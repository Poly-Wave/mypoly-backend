package com.polywave.billservice.repository.query.impl;

import com.polywave.billservice.domain.agenda.AgendaTab;
import com.polywave.billservice.repository.query.AgendaTabQueryRepository;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AgendaTabQueryRepositoryImpl implements AgendaTabQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<AgendaTab> findActiveOrderByDisplayOrder() {
        PathBuilder<AgendaTab> tab = new PathBuilder<>(AgendaTab.class, "agendaTab");
        return queryFactory
                .selectFrom(tab)
                .where(tab.getBoolean("active").isTrue())
                .orderBy(tab.getNumber("displayOrder", Integer.class).asc())
                .fetch();
    }
}

