package romm.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import romm.domain.Task;
import romm.service.TaskService;

import java.util.List;
import java.util.stream.IntStream;

import static java.util.Objects.isNull;

@Controller
@RequestMapping("/")
public class TaskController {
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/")
    public String tasks(Model model,
                            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
                            @RequestParam(value = "limit", required = false, defaultValue = "10") int limit
    ) {
        List<Task> tasks = taskService.getAll((page - 1) * limit, limit);
        model.addAttribute("tasks", tasks);
        model.addAttribute("currentPage", page);
        int totalPages = (int) Math.ceil((double) taskService.getAllCount() / limit);
        if (totalPages > 1) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages).boxed().toList();
            model.addAttribute("page_numbers", pageNumbers);
        }
        return "tasks";
    }

    @PostMapping("/{id}")
    public String edit(Model model,
                     @PathVariable Integer id,
                     @RequestBody TaskInfo taskInfo
    ) {
        if (isNull(id) || id <= 0) {
            throw new RuntimeException("Invalid id");
        }

        Task task = taskService.edit(id, taskInfo.getDescription(), taskInfo.getStatus());
        return tasks(model, 1, 10);
    }

    @PostMapping("/")
    public String add(Model model,
                     @RequestBody TaskInfo taskInfo
    ) {
        Task task = taskService.create(taskInfo.getDescription(), taskInfo.getStatus());
        return tasks(model, 1, 10);
    }

    @DeleteMapping("/{id}")
    public String delete(Model model,
                     @PathVariable Integer id
    ) {
        if (isNull(id) || id <= 0) {
            throw new RuntimeException("Invalid id");
        }
        taskService.delete(id);
        return tasks(model, 1, 10);
    }
}
