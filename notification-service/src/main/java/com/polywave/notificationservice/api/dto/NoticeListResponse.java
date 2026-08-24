package com.polywave.notificationservice.api.dto;

import com.polywave.notificationservice.application.notice.query.service.NoticeQueryService.NoticePage;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "공지사항 목록 응답")
public record NoticeListResponse(
        @Schema(description = "공지사항 목록(없으면 빈 배열)", requiredMode = Schema.RequiredMode.REQUIRED)
        List<NoticeSummaryResponse> notices,

        @Schema(description = "현재 페이지 번호", example = "0", requiredMode = Schema.RequiredMode.REQUIRED)
        int page,

        @Schema(description = "페이지 크기", example = "20", requiredMode = Schema.RequiredMode.REQUIRED)
        int size,

        @Schema(description = "다음 페이지 존재 여부", example = "false", requiredMode = Schema.RequiredMode.REQUIRED)
        boolean hasNext
) {
    public static NoticeListResponse from(NoticePage page) {
        return new NoticeListResponse(
                page.content().stream().map(NoticeSummaryResponse::from).toList(),
                page.page(),
                page.size(),
                page.hasNext()
        );
    }
}
