package com.dorm.booker.api.data.repositories;

import com.dorm.booker.api.data.models.Reminder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReminderRepository extends JpaRepository<Reminder, Long> {
}
