package com.polywave.notificationservice.api.controller;

import com.polywave.notificationservice.api.dto.NoticeDetailResponse;
import com.polywave.notificationservice.api.dto.NoticeListResponse;
import com.polywave.notificationservice.api.spec.NoticeApi;
import com.polywave.notificationservice.application.notice.query.service.NoticeQueryService;
import com.polywave.security.annotation.LoginUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class NoticeController implements NoticeApi {

    private final NoticeQueryService noticeQueryService;

    @Override
    public ResponseEntity<NoticeListResponse> getNotices(@LoginUser Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        NoticeQueryService.NoticePage result = noticeQueryService.findNotices(pageable);
        return ResponseEntity.ok(NoticeListResponse.from(result));
    }

    @Override
    public ResponseEntity<NoticeDetailResponse> getNotice(Long noticeId, @LoginUser Long userId) {
        return ResponseEntity.ok(NoticeDetailResponse.from(noticeQueryService.getNotice(noticeId)));
    }
}
