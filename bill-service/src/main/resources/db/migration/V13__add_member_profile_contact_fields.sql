SET search_path TO bill_service;

ALTER TABLE bill_members
ADD COLUMN phone_number VARCHAR(50),
ADD COLUMN office_room_number VARCHAR(100),
ADD COLUMN email VARCHAR(200),
ADD COLUMN aide_names VARCHAR(500),
ADD COLUMN chief_secretary_names VARCHAR(500),
ADD COLUMN secretary_names VARCHAR(1000);

UPDATE bill_members
SET
    phone_number = NULLIF(btrim(source_payload ->> 'NAAS_TEL_NO'), ''),
    office_room_number = NULLIF(btrim(source_payload ->> 'OFFM_RNUM_NO'), ''),
    email = NULLIF(btrim(source_payload ->> 'NAAS_EMAIL_ADDR'), ''),
    aide_names = NULLIF(btrim(source_payload ->> 'AIDE_NM'), ''),
    chief_secretary_names = NULLIF(btrim(source_payload ->> 'CHF_SCRT_NM'), ''),
    secretary_names = NULLIF(btrim(source_payload ->> 'SCRT_NM'), '')
WHERE source_payload IS NOT NULL;
