package com.sistema.backend.services;

import com.sistema.backend.models.Task;
import com.sistema.backend.models.User;
import com.sistema.backend.repository.TaskRepository;
import com.sistema.backend.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository; // AÑADIDO: Repositorio de usuario

    public TaskService(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    // AÑADIDO: Método auxiliar para obtener el usuario autenticado desde el JWT
    private User getAuthenticatedUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + username));
    }

    public List<Task> getAllTasks() {
        User user = getAuthenticatedUser();
        return taskRepository.findByUser(user); // ACTUALIZADO: Retorna solo las tareas del usuario
    }

    public Task createTask(Task task) {
        User user = getAuthenticatedUser();
        task.setUser(user); // ACTUALIZADO: Asigna el usuario propietario
        return taskRepository.save(task);
    }

    public Task updateTask(Long id, Task taskDetails) {
        User user = getAuthenticatedUser();
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tarea no encontrada con id: " + id));
        
        // Valida que la tarea pertenezca al usuario logueado
        if (task.getUser() != null && !task.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("No tienes permiso para modificar esta tarea");
        }

        task.setTitle(taskDetails.getTitle());
        task.setDescription(taskDetails.getDescription());
        task.setCompleted(taskDetails.isCompleted());
        
        return taskRepository.save(task);
    }

    public void deleteTask(Long id) {
        User user = getAuthenticatedUser();
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tarea no encontrada con id: " + id));

        // Valida que la tarea pertenezca al usuario logueado
        if (task.getUser() != null && !task.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("No tienes permiso para eliminar esta tarea");
        }

        taskRepository.deleteById(id);
    }
}