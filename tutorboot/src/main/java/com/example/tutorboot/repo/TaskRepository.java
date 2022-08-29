package com.example.tutorboot.repo;

import com.example.tutorboot.models.Tasks;
import org.springframework.data.repository.CrudRepository;

public interface TaskRepository extends CrudRepository<Tasks, Long> {

}
