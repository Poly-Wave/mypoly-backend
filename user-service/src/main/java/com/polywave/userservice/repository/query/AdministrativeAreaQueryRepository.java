package com.polywave.userservice.repository.query;

import com.polywave.userservice.domain.AdministrativeArea;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdministrativeAreaQueryRepository {
    Page<AdministrativeArea> searchByKeyword(String keyword, Pageable pageable);
}
