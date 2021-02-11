package com.dorm.booker.api.data.repositories;

import com.dorm.booker.api.data.models.Reminder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ReminderRepository extends JpaRepository<Reminder, Long>,
        JpaSpecificationExecutor<Reminder> {
}
