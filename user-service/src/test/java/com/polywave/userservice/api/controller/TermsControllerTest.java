package com.polywave.userservice.api.controller;

import com.polywave.security.JwtUtil;
import com.polywave.userservice.application.terms.query.result.TermsResult;
import com.polywave.userservice.application.terms.query.service.TermsQueryService;
import com.polywave.userservice.common.exception.TermsNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TermsController.class)
@Import(TestWebMvcConfig.class)
@AutoConfigureMockMvc(addFilters = false)
class TermsControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private TermsQueryService termsQueryService;

        @MockBean
        private JwtUtil jwtUtil;

        @Test
        @DisplayName("최신 약관 목록 조회 - 정상 응답")
        void getLatestTerms_Success() throws Exception {
                // given
                List<TermsResult> mockResults = List.of(
                                new TermsResult(1L, "SERVICE", "서비스 이용약관", 1, true, LocalDate.of(2026, 1, 1)),
                                new TermsResult(2L, "PRIVACY", "개인정보 처리방침", 1, true, LocalDate.of(2026, 1, 1)));
                given(termsQueryService.getLatestTerms()).willReturn(mockResults);

                // when & then
                mockMvc.perform(get("/terms"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.terms").isArray())
                                .andExpect(jsonPath("$.terms.length()").value(2))
                                .andExpect(jsonPath("$.terms[0].id").value(1))
                                .andExpect(jsonPath("$.terms[0].name").value("SERVICE"))
                                .andExpect(jsonPath("$.terms[0].title").value("서비스 이용약관"))
                                .andExpect(jsonPath("$.terms[1].id").value(2))
                                .andExpect(jsonPath("$.terms[1].name").value("PRIVACY"));
        }

        @Test
        @DisplayName("최신 약관 목록 조회 - 빈 목록 반환")
        void getLatestTerms_EmptyList() throws Exception {
                // given
                given(termsQueryService.getLatestTerms()).willReturn(List.of());

                // when & then
                mockMvc.perform(get("/terms"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.terms").isArray())
                                .andExpect(jsonPath("$.terms.length()").value(0));
        }

        @Test
        @DisplayName("약관 메타데이터 단건 조회 - 정상 응답")
        void getTermsMeta_Success() throws Exception {
                // given
                TermsResult mockResult = new TermsResult(1L, "SERVICE", "서비스 이용약관", 1, true, LocalDate.of(2026, 1, 1));
                given(termsQueryService.getTermsMeta(1L)).willReturn(mockResult);

                // when & then
                mockMvc.perform(get("/terms/{termsId}", 1L))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(1))
                                .andExpect(jsonPath("$.name").value("SERVICE"))
                                .andExpect(jsonPath("$.title").value("서비스 이용약관"))
                                .andExpect(jsonPath("$.version").value(1))
                                .andExpect(jsonPath("$.required").value(true));
        }

        @Test
        @DisplayName("약관 메타데이터 단건 조회 - 존재하지 않는 약관 ID 시 예외 발생")
        void getTermsMeta_NotFound() throws Exception {
                // given
                given(termsQueryService.getTermsMeta(999L)).willThrow(new TermsNotFoundException());

                // when & then
                mockMvc.perform(get("/terms/{termsId}", 999L))
                                .andExpect(result -> org.junit.jupiter.api.Assertions.assertTrue(
                                                result.getResolvedException() instanceof TermsNotFoundException));
        }

        @Test
        @DisplayName("약관 HTML 본문 조회 - 정상 응답(text/html)")
        void getTermsHtml_Success() throws Exception {
                // given
                String htmlContent = "<html><body><h1>Terms of Service</h1><p>content</p></body></html>";
                given(termsQueryService.getTermsHtml(1L)).willReturn(htmlContent);

                // when & then
                mockMvc.perform(get("/terms/{termsId}/html", 1L)
                                .accept(MediaType.TEXT_HTML))
                                .andExpect(status().isOk())
                                .andExpect(content().string(
                                                org.hamcrest.Matchers.containsString("<h1>Terms of Service</h1>")));
        }

        @Test
        @DisplayName("약관 HTML 본문 조회 - 존재하지 않는 약관 시 예외 발생")
        void getTermsHtml_NotFound() throws Exception {
                // given
                given(termsQueryService.getTermsHtml(999L)).willThrow(new TermsNotFoundException());

                // when & then - produces=TEXT_HTML 엔드포인트는 예외가 ServletException으로 래핑됨
                org.junit.jupiter.api.Assertions.assertThrows(
                                jakarta.servlet.ServletException.class,
                                () -> mockMvc.perform(get("/terms/{termsId}/html", 999L)
                                                .accept(MediaType.TEXT_HTML)));
        }
}
