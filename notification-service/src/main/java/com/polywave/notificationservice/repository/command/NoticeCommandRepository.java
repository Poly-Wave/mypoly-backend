package com.polywave.notificationservice.repository.command;

import com.polywave.notificationservice.domain.notice.Notice;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeCommandRepository extends JpaRepository<Notice, Long> {

    List<Notice> findByVisibleTrueAndBroadcastAtIsNull();
}
