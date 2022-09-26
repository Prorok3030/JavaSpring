package com.example.tutorboot.controllers;

import com.example.tutorboot.models.Difficulty;
import com.example.tutorboot.models.Tasks;
import com.example.tutorboot.models.User;
import com.example.tutorboot.repo.DifficultyRepository;
import com.example.tutorboot.repo.TaskRepository;
import com.example.tutorboot.repo.UserRepository;
import com.example.tutorboot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class TaskController {

    private final TaskRepository taskRepository;

    public TaskController(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DifficultyRepository difficultyRepository;

    @Autowired
    private UserService userService;

//    private static List<String> difficulties;
//    static {
//        difficulties = new ArrayList<>();
//        difficulties.add("low");
//        difficulties.add("medium");
//        difficulties.add("high");
//    }

    private static List<String> skills;
    static {
        skills = new ArrayList<>();
        skills.add("strength");
        skills.add("intelligence");
        skills.add("health");
        skills.add("creativity");
        skills.add("communication");
    }

    @GetMapping("/tasks")
    public String home(@AuthenticationPrincipal User user, Model model, Principal principal) {
//        Long id = userRepository.findByUsername(principal.getName()).getId();
//        model.addAttribute("id", "Твой id: " + id); //TODO Возможно оставить только переменную, а строки в кавычках перенести в html
        Iterable<Tasks> tasks = taskRepository.findByUser(user);
        model.addAttribute("tasks", tasks);
        model.addAttribute("username", "Привет, " + principal.getName() + "!");
        model.addAttribute("user", user);
        return "tasks";
    }

    @GetMapping("/taskAdd")
    public String taskAdd(Model model){
        Iterable<Difficulty> difficulty = difficultyRepository.findAll();
        model.addAttribute("difficulties", difficulty);
        model.addAttribute("skills", skills);
        return "taskAdd";
    }

    @PostMapping("/taskAdd")
    public String taskPostAdd(@AuthenticationPrincipal User user,
                              @RequestParam String name, @RequestParam String skill, @RequestParam String difficulty, Model model){
        Difficulty diff = difficultyRepository.findByName(difficulty);
        Tasks tasks = new Tasks(name, skill, diff, user);
        taskRepository.save(tasks);
        return "redirect:/taskAdd";
    }

    @GetMapping("/taskEdit/{id}")
    public String taskEdit(@PathVariable("id") long id, Model model){
        Optional<Tasks> tasks = taskRepository.findById(id); //TODO Optional (в методе taskSkillUp есть альтернативное решение)
        Iterable<Difficulty> difficulty = difficultyRepository.findAll();
        model.addAttribute("tasks", tasks);
        model.addAttribute("difficulties", difficulty);
        //model.addAttribute("difficulties", difficulties);
        model.addAttribute("skills", skills);
        return "taskEdit";
    }

    @PostMapping("/taskEdit/{id}")
    public String taskPostEdit(@AuthenticationPrincipal User user, Tasks tasks){
        tasks.setUser(user); //TODO Исправить, чтобы пользователь не терялся после передачи формы Edit
        taskRepository.save(tasks);
        return "redirect:/tasks";
    }

    @GetMapping("/taskDelete/{id}")
    public String taskDelete(@PathVariable("id") long id){
        taskRepository.deleteById(id);
        return "redirect:/tasks";
    }

    @GetMapping("/taskSkillUp/{id}")
    public String taskSkillUp(@AuthenticationPrincipal User user ,@PathVariable("id") long id, Model model){
        Tasks tasks = taskRepository.findById(id).orElse(null);
        Difficulty difficulty = tasks.getDifficulty();
        Integer difPoint = difficulty.getPoints();

        switch (tasks.getSkill_name()) {
            case "strength": user.setStrength(user.getStrength() + difPoint);
                break;
            case "intelligence": user.setIntelligence(user.getIntelligence() + difPoint);
                break;
            case "health": user.setHealth(user.getHealth() + difPoint);
                break;
            case "creativity": user.setCreativity(user.getCreativity() + difPoint);
                break;
            case "communication": user.setCommunication(user.getCommunication() + difPoint);
                break;
        }
        userService.UserExpUp(user,difPoint);
        userRepository.save(user);
        taskRepository.deleteById(id);
        return "redirect:/tasks";
    }
}
