package com.polywave.userservice.application.address.query.service;

import com.polywave.userservice.application.address.query.result.AddressSearchResult;

public interface AddressQueryService {
    AddressSearchResult searchAddress(String keyword, int currentPage, int countPerPage);
}
