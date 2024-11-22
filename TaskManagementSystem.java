import java.time.LocalDateTime;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

public class TaskManagementSystem {
    public static void main(String[] args) {
        try {
            // Create task manager
            TaskManager manager = new TaskManager();

            // Create some tasks
            Task task1 = new Task.Builder("Implement Login System")
                .description("Create OAuth2 authentication")
                .priority(Priority.HIGH)
                .deadline(LocalDateTime.now().plusDays(3))
                .tags(Arrays.asList("backend", "security"))
                .build();

            Task task2 = new Task.Builder("Design Homepage")
                .description("Create responsive design for homepage")
                .priority(Priority.MEDIUM)
                .deadline(LocalDateTime.now().plusDays(5))
                .tags(Arrays.asList("frontend", "design"))
                .build();

            // Simulate work on task1
            task1.startTimeBlock();
            Thread.sleep(1500); // Simulate 1.5 seconds of work
            task1.endTimeBlock();

            // Add tasks to manager
            manager.addTask(task1);
            manager.addTask(task2);

            // Print task information
            System.out.println("\n=== Task Management System Demo ===");
            System.out.println("\nAll Tasks:");
            manager.getAllTasks().forEach(System.out::println);

            System.out.println("\nTasks by Priority (HIGH):");
            manager.getTasksByPriority(Priority.HIGH).forEach(System.out::println);

            System.out.println("\nTasks by Tag (backend):");
            manager.getTasksByTag("backend").forEach(System.out::println);

            System.out.println("\nProductivity Insights:");
            Map<String, Double> insights = manager.getProductivityInsights();
            insights.forEach((key, value) -> System.out.printf("%s: %.2f%n", key, value));

            System.out.println("\nOptimal Task Order:");
            manager.getOptimalTaskOrder().forEach(task -> 
                System.out.println(task.getTitle() + " (Priority: " + task.getPriority() + ")")
            );

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class TaskManager {
    private Map<String, Task> tasks;
    private Map<String, List<Task>> tagIndex;

    public TaskManager() {
        this.tasks = new HashMap<>();
        this.tagIndex = new HashMap<>();
    }

    public void addTask(Task task) {
        tasks.put(task.getId(), task);
        for (String tag : task.getTags()) {
            tagIndex.computeIfAbsent(tag, k -> new ArrayList<>()).add(task);
        }
    }

    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public List<Task> getTasksByTag(String tag) {
        return tagIndex.getOrDefault(tag, new ArrayList<>());
    }

    public List<Task> getTasksByPriority(Priority priority) {
        return tasks.values().stream()
            .filter(task -> task.getPriority() == priority)
            .collect(Collectors.toList());
    }

    public Map<String, Double> getProductivityInsights() {
        Map<String, Double> insights = new HashMap<>();
        
        double avgFocusScore = tasks.values().stream()
            .mapToDouble(Task::getFocusScore)
            .average()
            .orElse(0.0);
        insights.put("averageFocusScore", avgFocusScore);

        long completedTasks = tasks.values().stream()
            .filter(task -> task.getStatus() == TaskStatus.COMPLETED)
            .count();
        double completionRate = tasks.isEmpty() ? 0 : (double) completedTasks / tasks.size();
        insights.put("completionRate", completionRate);

        return insights;
    }

    public List<Task> getOptimalTaskOrder() {
        return tasks.values().stream()
            .filter(task -> task.getStatus() != TaskStatus.COMPLETED)
            .sorted((t1, t2) -> {
                int priorityCompare = t2.getPriority().compareTo(t1.getPriority());
                if (priorityCompare != 0) return priorityCompare;
                return Double.compare(t2.getFocusScore(), t1.getFocusScore());
            })
            .collect(Collectors.toList());
    }
}

class Task {
    private final String id;
    private String title;
    private String description;
    private Priority priority;
    private LocalDateTime deadline;
    private LocalDateTime created;
    private List<TimeBlock> timeBlocks;
    private TaskStatus status;
    private List<String> tags;
    private double focusScore;

    private Task(Builder builder) {
        this.id = UUID.randomUUID().toString();
        this.title = builder.title;
        this.description = builder.description;
        this.priority = builder.priority;
        this.deadline = builder.deadline;
        this.created = LocalDateTime.now();
        this.timeBlocks = new ArrayList<>();
        this.status = TaskStatus.TODO;
        this.tags = builder.tags;
        this.focusScore = 0.0;
    }

    public static class Builder {
        private String title;
        private String description;
        private Priority priority;
        private LocalDateTime deadline;
        private List<String> tags = new ArrayList<>();

        public Builder(String title) {
            this.title = title;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder priority(Priority priority) {
            this.priority = priority;
            return this;
        }

        public Builder deadline(LocalDateTime deadline) {
            this.deadline = deadline;
            return this;
        }

        public Builder tags(List<String> tags) {
            this.tags = new ArrayList<>(tags);
            return this;
        }

        public Task build() {
            return new Task(this);
        }
    }

    // Getters
    public String getId() { return id; }
    public String getTitle() { return title; }
    public Priority getPriority() { return priority; }
    public TaskStatus getStatus() { return status; }
    public List<String> getTags() { return new ArrayList<>(tags); }
    public double getFocusScore() { return focusScore; }

    public void startTimeBlock() {
        timeBlocks.add(new TimeBlock());
    }

    public void endTimeBlock() {
        if (!timeBlocks.isEmpty()) {
            timeBlocks.get(timeBlocks.size() - 1).end();
            calculateFocusScore();
        }
    }

    private void calculateFocusScore() {
        double consistencyScore = calculateConsistencyScore();
        double timeOfDayScore = calculateTimeOfDayScore();
        double durationScore = calculateDurationScore();
        focusScore = (consistencyScore + timeOfDayScore + durationScore) / 3.0;
    }

    private double calculateConsistencyScore() {
        if (timeBlocks.size() < 2) return 1.0;
        
        List<Duration> gaps = new ArrayList<>();
        for (int i = 1; i < timeBlocks.size(); i++) {
            Duration gap = Duration.between(
                timeBlocks.get(i-1).getEndTime(),
                timeBlocks.get(i).getStartTime()
            );
            gaps.add(gap);
        }

        double avgGap = gaps.stream()
            .mapToLong(Duration::toMinutes)
            .average()
            .orElse(0.0);

        double variance = gaps.stream()
            .mapToDouble(gap -> Math.pow(gap.toMinutes() - avgGap, 2))
            .average()
            .orElse(0.0);

        double stdDev = Math.sqrt(variance);
        return Math.max(0, 1 - (stdDev / (avgGap + 1)));
    }

    private double calculateTimeOfDayScore() {
        Map<Integer, Double> hourlyProductivity = timeBlocks.stream()
            .collect(Collectors.groupingBy(
                block -> block.getStartTime().getHour(),
                Collectors.averagingDouble(TimeBlock::getDurationMinutes)
            ));

        OptionalDouble maxProductivity = hourlyProductivity.values().stream()
            .mapToDouble(Double::doubleValue)
            .max();

        return maxProductivity.orElse(0.0) / 60.0;
    }

    private double calculateDurationScore() {
        double optimalMin = 25.0;
        double optimalMax = 45.0;

        return timeBlocks.stream()
            .mapToDouble(block -> {
                double duration = block.getDurationMinutes();
                if (duration >= optimalMin && duration <= optimalMax) {
                    return 1.0;
                } else {
                    return 1.0 - (Math.min(
                        Math.abs(duration - optimalMin),
                        Math.abs(duration - optimalMax)
                    ) / optimalMax);
                }
            })
            .average()
            .orElse(0.0);
    }

    @Override
    public String toString() {
        return String.format("Task{id='%s', title='%s', priority=%s, status=%s, focusScore=%.2f}",
            id, title, priority, status, focusScore);
    }
}

class TimeBlock {
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public TimeBlock() {
        this.startTime = LocalDateTime.now();
    }

    public void end() {
        this.endTime = LocalDateTime.now();
    }

    public LocalDateTime getStartTime() { return startTime; }
    public LocalDateTime getEndTime() { return endTime; }

    public double getDurationMinutes() {
        if (endTime == null) return 0;
        return Duration.between(startTime, endTime).toMinutes();
    }
}

enum Priority {
    LOW, MEDIUM, HIGH, URGENT
}

enum TaskStatus {
    TODO, IN_PROGRESS, COMPLETED, BLOCKED
}
