package com.polywave.notificationservice.domain.notification;

import java.util.Arrays;
import java.util.Optional;

/**
 * 앱 알림함 카테고리(피그마 "공지사항 / 안건 / 보조금" 분류 기준).
 *
 * - displayName 은 앱 알림함 UI 에서 카테고리 라벨로 사용된다.
 * - 추후 카테고리가 늘어나도 enum 추가만으로 확장할 수 있도록 한다.
 */
public enum NotificationCategory {
    NOTICE("공지사항"),
    BILL("안건"),
    SUBSIDY("보조금"),
    ETC("기타");

    private final String displayName;

    NotificationCategory(String displayName) {
        this.displayName = displayName;
    }

    public String displayName() {
        return displayName;
    }

    public static Optional<NotificationCategory> findByName(String name) {
        if (name == null || name.isBlank()) {
            return Optional.empty();
        }
        return Arrays.stream(values())
                .filter(c -> c.name().equalsIgnoreCase(name.trim()))
                .findFirst();
    }
}
