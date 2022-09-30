package com.example.tutorboot.repo;

import com.example.tutorboot.models.Tasks;
import com.example.tutorboot.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Tasks, Long> {
    List<Tasks> findByUser (User user); //Найти задачи для конкретного пользователя по user_id

}
