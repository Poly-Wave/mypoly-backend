const express = require('express');
const { Pool } = require('pg');
const cors = require('cors');
const path = require('path');

const app = express();
const basePath = (process.env.ADMIN_BASE_PATH || '').trim().replace(/\/$/, '');
const router = express.Router();

app.use(cors());
app.use(express.json());

router.use(express.static(path.join(__dirname, 'public')));

// ── 필수 환경변수 체크 ──
const requiredEnv = ['DB_HOST', 'DB_PORT', 'DB_NAME', 'DB_USERNAME', 'DB_PASSWORD'];

for (const key of requiredEnv) {
  if (!process.env[key]) {
    throw new Error(`Missing required environment variable: ${key}`);
  }
}

// ── PostgreSQL 접속 설정 ──
const pool = new Pool({
  host: process.env.DB_HOST,
  port: parseInt(process.env.DB_PORT, 10),
  database: process.env.DB_NAME,
  user: process.env.DB_USERNAME,
  password: process.env.DB_PASSWORD,
});

// ── 시스템 스키마 제외 목록 ──
const SYSTEM_SCHEMAS = [
  'pg_catalog', 'information_schema', 'pg_toast',
  'pg_temp_1', 'pg_toast_temp_1',
];

// ============================================================
//  메타데이터 API
// ============================================================

/** 스키마 목록 조회 */
router.get('/api/schemas', async (_req, res) => {
  try {
    const { rows } = await pool.query(`
      SELECT schema_name
      FROM information_schema.schemata
      WHERE schema_name NOT IN (${SYSTEM_SCHEMAS.map((_, i) => `$${i + 1}`).join(',')})
      ORDER BY schema_name
    `, SYSTEM_SCHEMAS);
    res.json(rows.map(r => r.schema_name));
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

/** 특정 스키마의 테이블 목록 */
router.get('/api/schemas/:schema/tables', async (req, res) => {
  try {
    const { rows } = await pool.query(`
      SELECT table_name
      FROM information_schema.tables
      WHERE table_schema = $1
        AND table_type = 'BASE TABLE'
      ORDER BY table_name
    `, [req.params.schema]);
    res.json(rows.map(r => r.table_name));
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

/** 테이블 컬럼 메타데이터 */
router.get('/api/schemas/:schema/tables/:table/columns', async (req, res) => {
  try {
    const { schema, table } = req.params;
    const { rows } = await pool.query(`
      SELECT
        c.column_name,
        c.data_type,
        c.udt_name,
        c.is_nullable,
        c.column_default,
        c.character_maximum_length,
        c.numeric_precision,
        CASE WHEN pk.column_name IS NOT NULL THEN true ELSE false END AS is_primary_key
      FROM information_schema.columns c
      LEFT JOIN (
        SELECT kcu.column_name
        FROM information_schema.table_constraints tc
        JOIN information_schema.key_column_usage kcu
          ON tc.constraint_name = kcu.constraint_name
          AND tc.table_schema = kcu.table_schema
        WHERE tc.constraint_type = 'PRIMARY KEY'
          AND tc.table_schema = $1
          AND tc.table_name = $2
      ) pk ON c.column_name = pk.column_name
      WHERE c.table_schema = $1
        AND c.table_name = $2
      ORDER BY c.ordinal_position
    `, [schema, table]);
    res.json(rows);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

/** FK 관계 조회 (이 테이블이 참조하는 + 이 테이블을 참조하는) */
router.get('/api/schemas/:schema/tables/:table/relationships', async (req, res) => {
  try {
    const { schema, table } = req.params;

    const outgoing = await pool.query(`
      SELECT
        kcu.column_name       AS from_column,
        ccu.table_schema      AS to_schema,
        ccu.table_name        AS to_table,
        ccu.column_name       AS to_column,
        tc.constraint_name
      FROM information_schema.table_constraints tc
      JOIN information_schema.key_column_usage kcu
        ON tc.constraint_name = kcu.constraint_name
        AND tc.table_schema = kcu.table_schema
      JOIN information_schema.constraint_column_usage ccu
        ON tc.constraint_name = ccu.constraint_name
        AND tc.table_schema = ccu.table_schema
      WHERE tc.constraint_type = 'FOREIGN KEY'
        AND tc.table_schema = $1
        AND tc.table_name = $2
    `, [schema, table]);

    const incoming = await pool.query(`
      SELECT
        kcu.table_schema      AS from_schema,
        kcu.table_name        AS from_table,
        kcu.column_name       AS from_column,
        ccu.column_name       AS to_column,
        tc.constraint_name
      FROM information_schema.table_constraints tc
      JOIN information_schema.key_column_usage kcu
        ON tc.constraint_name = kcu.constraint_name
        AND tc.table_schema = kcu.table_schema
      JOIN information_schema.constraint_column_usage ccu
        ON tc.constraint_name = ccu.constraint_name
        AND tc.table_schema = ccu.table_schema
      WHERE tc.constraint_type = 'FOREIGN KEY'
        AND ccu.table_schema = $1
        AND ccu.table_name = $2
    `, [schema, table]);

    res.json({
      outgoing: outgoing.rows,
      incoming: incoming.rows,
    });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

/** 테이블 행 수 조회 */
router.get('/api/schemas/:schema/tables/:table/count', async (req, res) => {
  try {
    const { schema, table } = req.params;
    const search = req.query.search || '';
    const searchColumn = req.query.searchColumn || '';

    let query = `SELECT COUNT(*) AS total FROM "${schema}"."${table}"`;
    const params = [];

    if (search && searchColumn) {
      query += ` WHERE "${searchColumn}"::text ILIKE $1`;
      params.push(`%${search}%`);
    }

    const { rows } = await pool.query(query, params);
    res.json({ total: parseInt(rows[0].total, 10) });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

/** 데이터 조회 (페이지네이션, 정렬, 검색) */
router.get('/api/schemas/:schema/tables/:table/data', async (req, res) => {
  try {
    const { schema, table } = req.params;
    const page = parseInt(req.query.page || '1', 10);
    const limit = Math.min(parseInt(req.query.limit || '50', 10), 200);
    const offset = (page - 1) * limit;
    const sortBy = req.query.sortBy || '';
    const sortDir = (req.query.sortDir || 'ASC').toUpperCase() === 'DESC' ? 'DESC' : 'ASC';
    const search = req.query.search || '';
    const searchColumn = req.query.searchColumn || '';

    const colCheck = await pool.query(`
      SELECT column_name FROM information_schema.columns
      WHERE table_schema = $1 AND table_name = $2
    `, [schema, table]);
    const validColumns = colCheck.rows.map(r => r.column_name);

    let query = `SELECT * FROM "${schema}"."${table}"`;
    const params = [];
    let paramIdx = 1;

    if (search && searchColumn && validColumns.includes(searchColumn)) {
      query += ` WHERE "${searchColumn}"::text ILIKE $${paramIdx}`;
      params.push(`%${search}%`);
      paramIdx++;
    }

    if (sortBy && validColumns.includes(sortBy)) {
      query += ` ORDER BY "${sortBy}" ${sortDir}`;
    }

    query += ` LIMIT $${paramIdx} OFFSET $${paramIdx + 1}`;
    params.push(limit, offset);

    const { rows } = await pool.query(query, params);
    res.json(rows);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

/** 단일 레코드 조회 (PK 기반) */
router.get('/api/schemas/:schema/tables/:table/data/:id', async (req, res) => {
  try {
    const { schema, table, id } = req.params;
    const pkCol = req.query.pkColumn || 'id';
    const { rows } = await pool.query(
      `SELECT * FROM "${schema}"."${table}" WHERE "${pkCol}" = $1 LIMIT 1`,
      [id]
    );
    if (rows.length === 0) return res.status(404).json({ error: 'Not found' });
    res.json(rows[0]);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

/** 새 레코드 삽입 */
router.post('/api/schemas/:schema/tables/:table/data', async (req, res) => {
  try {
    const { schema, table } = req.params;
    const data = req.body;
    const keys = Object.keys(data).filter(k => data[k] !== '' && data[k] !== null && data[k] !== undefined);
    const values = keys.map(k => data[k]);
    const cols = keys.map(k => `"${k}"`).join(', ');
    const placeholders = keys.map((_, i) => `$${i + 1}`).join(', ');

    const { rows } = await pool.query(
      `INSERT INTO "${schema}"."${table}" (${cols}) VALUES (${placeholders}) RETURNING *`,
      values
    );
    res.status(201).json(rows[0]);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

/** 레코드 수정 */
router.put('/api/schemas/:schema/tables/:table/data/:id', async (req, res) => {
  try {
    const { schema, table, id } = req.params;
    const pkCol = req.query.pkColumn || 'id';
    const data = req.body;
    const keys = Object.keys(data);
    const setClauses = keys.map((k, i) => `"${k}" = $${i + 1}`).join(', ');
    const values = keys.map(k => data[k]);
    values.push(id);

    const { rows } = await pool.query(
      `UPDATE "${schema}"."${table}" SET ${setClauses} WHERE "${pkCol}" = $${values.length} RETURNING *`,
      values
    );
    if (rows.length === 0) return res.status(404).json({ error: 'Not found' });
    res.json(rows[0]);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

/** 레코드 삭제 */
router.delete('/api/schemas/:schema/tables/:table/data/:id', async (req, res) => {
  try {
    const { schema, table, id } = req.params;
    const pkCol = req.query.pkColumn || 'id';
    const { rowCount } = await pool.query(
      `DELETE FROM "${schema}"."${table}" WHERE "${pkCol}" = $1`,
      [id]
    );
    if (rowCount === 0) return res.status(404).json({ error: 'Not found' });
    res.json({ deleted: true });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

/** FK로 참조되는 레코드 조회 */
router.get('/api/schemas/:schema/tables/:table/referenced-by', async (req, res) => {
  try {
    const { schema, table } = req.params;
    const columnValue = req.query.value;
    const refSchema = req.query.refSchema;
    const refTable = req.query.refTable;
    const refColumn = req.query.refColumn;

    if (!columnValue || !refTable || !refColumn) {
      return res.status(400).json({ error: 'Missing params' });
    }

    const targetSchema = refSchema || schema;
    const { rows } = await pool.query(
      `SELECT * FROM "${targetSchema}"."${refTable}" WHERE "${refColumn}" = $1 LIMIT 100`,
      [columnValue]
    );
    res.json(rows);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

// ── SPA fallback ──
router.get('*', (_req, res) => {
  res.sendFile(path.join(__dirname, 'public', 'index.html'));
});

if (basePath) {
  app.use(basePath, router);
  app.get('/', (_req, res) => res.redirect(basePath));
} else {
  app.use('/', router);
}

const PORT = process.env.ADMIN_PORT || 3000;
app.listen(PORT, () => {
  const servedPath = basePath || '/';
  console.log(`\n  MyPoly DB Admin running at http://localhost:${PORT}${servedPath}\n`);
});