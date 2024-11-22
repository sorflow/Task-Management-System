# Task Management System

A sophisticated Java-based task management system with time tracking, productivity analytics, and smart task prioritization features.

## Features

- **Task Management**
  - Create tasks with title, description, priority, deadline, and tags
  - Builder pattern for flexible task creation
  - Status tracking (TODO, IN_PROGRESS, COMPLETED, BLOCKED)
  - Tag-based organization and filtering

- **Time Tracking**
  - Track work sessions with start/end times
  - Automatic duration calculation
  - Historical work pattern analysis

- **Smart Analytics**
  - Focus Score calculation based on:
    - Work session consistency
    - Time-of-day productivity patterns
    - Optimal session duration analysis
  - Productivity insights and metrics
  - Completion rate tracking

- **Task Prioritization**
  - Priority levels (LOW, MEDIUM, HIGH, URGENT)
  - Smart task ordering based on priority and focus scores
  - Tag-based task filtering

## Getting Started

### Prerequisites
- Java Development Kit (JDK) 8 or higher
- Command line terminal or Java IDE

### Installation

1. Clone the repository or download `TaskManagementSystem.java`
```bash
git clone [repository-url]
```

2. Compile the Java file
```bash
javac TaskManagementSystem.java
```

3. Run the program
```bash
java TaskManagementSystem
```

## Usage

### Creating Tasks
```java
Task task = new Task.Builder("Task Title")
    .description("Task description")
    .priority(Priority.HIGH)
    .deadline(LocalDateTime.now().plusDays(3))
    .tags(Arrays.asList("important", "project"))
    .build();
```

### Managing Time Blocks
```java
// Start working on a task
task.startTimeBlock();

// ... do work ...

// End the time block
task.endTimeBlock();
```

### Using TaskManager
```java
TaskManager manager = new TaskManager();

// Add tasks
manager.addTask(task);

// Get tasks by tag
List<Task> backendTasks = manager.getTasksByTag("backend");

// Get tasks by priority
List<Task> highPriorityTasks = manager.getTasksByPriority(Priority.HIGH);

// Get productivity insights
Map<String, Double> insights = manager.getProductivityInsights();

// Get optimal task order
List<Task> optimalOrder = manager.getOptimalTaskOrder();
```

## Project Structure

- **TaskManagementSystem**: Main class with demo implementation
- **TaskManager**: Manages task collection and provides task operations
- **Task**: Core task entity with builder pattern implementation
- **TimeBlock**: Represents work sessions with timing functionality
- **Priority**: Enum for task priority levels
- **TaskStatus**: Enum for task status states

## Focus Score Algorithm

The system calculates a focus score (0-1) based on three factors:

1. **Consistency Score**: Measures the regularity of work sessions
2. **Time of Day Score**: Identifies optimal working hours
3. **Duration Score**: Evaluates work session lengths against ideal durations (25-45 minutes)

## Future Enhancements

1. **Data Persistence**
   - Database integration
   - File-based storage
   - Export/Import functionality

2. **User Interface**
   - GUI implementation
   - Web interface
   - Mobile app integration

3. **Advanced Features**
   - Team collaboration
   - Project management integration
   - Calendar synchronization
   - Automated task scheduling
   - Machine learning for better task optimization

4. **Reporting**
   - Detailed productivity reports
   - Time tracking analytics
   - Performance metrics
   - Custom dashboards

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Author

[Derek Grant]
[derek.grant@bulldogs.aamu.edu]

## Acknowledgments

- Inspired by productivity techniques like Pomodoro
- Built with modern Java design patterns and best practices
- Focus on clean code and maintainability
