package com.sistema.backend.repository;

import com.sistema.backend.models.Task;
import com.sistema.backend.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    // AÑADIDO: Filtrar las tareas pertenecientes al usuario específico
    List<Task> findByUser(User user);
}