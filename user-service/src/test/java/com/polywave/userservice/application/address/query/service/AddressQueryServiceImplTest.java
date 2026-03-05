package com.polywave.userservice.application.address.query.service;

import com.polywave.userservice.application.address.query.result.AddressInfoResult;
import com.polywave.userservice.application.address.query.result.AddressSearchResult;
import com.polywave.userservice.domain.AdministrativeArea;
import com.polywave.userservice.repository.query.AdministrativeAreaQueryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class AddressQueryServiceImplTest {

    @Mock
    private AdministrativeAreaQueryRepository administrativeAreaQueryRepository;

    @InjectMocks
    private AddressQueryServiceImpl addressQueryService;

    @Test
    @DisplayName("주소지 검색 기능 - 정상 페이징 반환 및 도메인 분리 맵핑 검증")
    void searchAddress_Success() {
        // given
        String keyword = "구로구";
        int currentPage = 1;
        int countPerPage = 10;
        Pageable expectedPageable = PageRequest.of(0, countPerPage);

        AdministrativeArea area1 = mock(AdministrativeArea.class);
        given(area1.getSido()).willReturn("서울특별시");
        given(area1.getSigungu()).willReturn("구로구");
        given(area1.getEmdName()).willReturn("오류동");

        AdministrativeArea area2 = mock(AdministrativeArea.class);
        given(area2.getSido()).willReturn("서울특별시");
        given(area2.getSigungu()).willReturn("구로구");
        given(area2.getEmdName()).willReturn("항동");

        List<AdministrativeArea> mockList = List.of(area1, area2);
        Page<AdministrativeArea> mockPage = new PageImpl<>(mockList, expectedPageable, 100);

        given(administrativeAreaQueryRepository.searchByKeyword(keyword, expectedPageable)).willReturn(mockPage);

        // when
        AddressSearchResult result = addressQueryService.searchAddress(keyword, currentPage, countPerPage);

        // then
        assertThat(result.totalCount()).isEqualTo(100);
        assertThat(result.currentPage()).isEqualTo(1);
        assertThat(result.countPerPage()).isEqualTo(10);
        assertThat(result.addresses()).hasSize(2);

        AddressInfoResult firstAddress = result.addresses().get(0);
        assertThat(firstAddress.sido()).isEqualTo("서울특별시");
        assertThat(firstAddress.sigungu()).isEqualTo("구로구");
        assertThat(firstAddress.emdName()).isEqualTo("오류동");
    }

    @Test
    @DisplayName("주소지 검색 기능 - 검색 결과 빈 경우 빈 리스트 반환")
    void searchAddress_EmptyResult() {
        // given
        String keyword = "없는주소";
        int currentPage = 2;
        int countPerPage = 5;
        Pageable expectedPageable = PageRequest.of(1, countPerPage);

        Page<AdministrativeArea> emptyPage = new PageImpl<>(List.of(), expectedPageable, 0);

        given(administrativeAreaQueryRepository.searchByKeyword(keyword, expectedPageable)).willReturn(emptyPage);

        // when
        AddressSearchResult result = addressQueryService.searchAddress(keyword, currentPage, countPerPage);

        // then
        assertThat(result.totalCount()).isEqualTo(0);
        assertThat(result.currentPage()).isEqualTo(2);
        assertThat(result.addresses()).isEmpty();
    }
}
