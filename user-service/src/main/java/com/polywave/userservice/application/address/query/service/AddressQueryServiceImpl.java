package com.polywave.userservice.application.address.query.service;

import com.polywave.userservice.application.address.query.result.AddressInfoResult;
import com.polywave.userservice.application.address.query.result.AddressSearchResult;
import com.polywave.userservice.domain.AdministrativeArea;
import com.polywave.userservice.repository.query.AdministrativeAreaQueryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AddressQueryServiceImpl implements AddressQueryService {

    private final AdministrativeAreaQueryRepository administrativeAreaQueryRepository;

    @Override
    @Transactional(readOnly = true)
    public AddressSearchResult searchAddress(String keyword, int currentPage, int countPerPage) {
        Pageable pageable = PageRequest.of(currentPage - 1, countPerPage); // Page is 0-indexed in JPA
        Page<AdministrativeArea> pageResult = administrativeAreaQueryRepository.searchByKeyword(keyword, pageable);

        List<AddressInfoResult> addresses = pageResult.getContent().stream()
                .map(a -> new AddressInfoResult(a.getSido(), a.getSigungu(), a.getEmdName()))
                .collect(Collectors.toList());

        return new AddressSearchResult(
                (int) pageResult.getTotalElements(),
                currentPage,
                countPerPage,
                addresses);
    }
}
