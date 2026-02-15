package com.polywave.userservice.application.address.query.result;

import java.util.List;

public record AddressSearchResult(
                int totalCount,
                int currentPage,
                int countPerPage,
                List<AddressInfoResult> addresses) {
}
