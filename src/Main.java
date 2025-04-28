import java.util.*;

public class Main {

    private static final ITaskHandler handler = new TaskJsonHandler();

    public static void main(String[] args) {
        start();
    }

    static void start() {

        Scanner scan = new Scanner(System.in);


        while (true) {

            System.out.print("Enter command: ");
            String command = scan.nextLine();

            if (command.contains("add")) {
                addTask(command);
            } else if (command.contains("get")) {
                getTask(command);
            } else if (command.contains("update")) {
                updateTask(command);
            } else if (command.contains("delete")) {
                deleteTask(command);
            } else if (command.contains("mark")) {
                markTask(command);
            } else if (command.contains("list")) {
                if (command.length() != 4) {
                    getTaskListByStatus(command);
                } else {
                    getTaskList();
                }
            } else {
                System.out.println("Введена неверная команда");
            }
        }
    }

    public static void addTask(String command) {
        command = command.substring(4);

        if (command.contains("\"")) {
            Task task = new Task(command.replace("\"", ""));
            System.out.printf("Task added successfully (ID: %d)\n", task.getId());
        } else {
            System.out.println("Неверный синтаксис команды");
        }
    }

    //get {ID}
    public static void getTask(String command) {
        command = command.substring(4);

        int id;
        if ((id = isCorrectID(command)) != -1) {
            printTask(handler.load(id));
        } else {
            System.out.println("ID таска должно быть числом!");
        }
    }

    //update {ID} "new description"
    public static void updateTask(String command) {

        command = command.substring(7);

        int id;
        if ((id = isCorrectID(command.split(" ")[0])) != -1) {

            char[] commandArr = command.substring(2).toCharArray();
            if (commandArr[0] == '"' && commandArr[commandArr.length-1] == '"') {

                String description = command.substring(2).replace("\"", "");

                if (!handler.update(id, description)) {
                    System.out.println("Таска с таким ID не существует!");
                } else {
                    System.out.printf("Task updated successfully (ID: %d)\n", id);
                }
            } else {
                System.out.println("Неверный формат аргументов команды!");
            }
        } else {
            System.out.println("ID таска должно быть числом!");
        }
    }

    //delete {ID}
    public static void deleteTask(String command) {
        command = command.substring(7);

        int id;
        if ((id = isCorrectID(command)) != -1) {
            if (!handler.delete(id)) {
                System.out.println("Таска с таким ID не существует");
            }
        } else {
            System.out.println("ID таска должно быть числом!");
        }
    }

    //mark {status} {ID}
    public static void markTask(String command) {
        command = command.substring(5);

        int id;
        if ((id = isCorrectID(command.split(" ")[command.split(" ").length-1])) != -1) {

            String strStatus = command.substring(0, command.length()-2);
            if (!handler.mark(id, TaskStatus.getStatusObj(strStatus))) {
                System.out.println("Таска с таким ID не существует");
            }
        } else {
            System.out.println("ID таска должно быть числом!");
        }
    }

    public static void getTaskList() {
        ArrayList<Task> allTasks = handler.loadAllTasks();
        if (allTasks.isEmpty()) {
            System.out.println("Список тасков пуст");
        }
        for (Task task : allTasks) {
            printTask(task);
            System.out.println();
        }
    }

    //list {status}
    public static void getTaskListByStatus(String command) {
        command = command.substring(5);
        TaskStatus status = TaskStatus.getStatusObj(command);

        if (status.equals(TaskStatus.TODO) && !command.equals("todo")) {
            System.out.println("Не существует такого статуса!");
            return;
        }

        ArrayList<Task> allTasks = handler.loadTasksByStatus(status);

        if (allTasks.isEmpty()) {
            System.out.println("Таксов с таким статусом нет");
            return;
        }

        for (Task task : allTasks) {
            printTask(task);
            System.out.println();
        }
    }

    public static void printTask(Task task) {

        if (task.getDescription().equals("null")) {
            System.out.println("Не существует задачи с таким номером");
            return;
        }

        System.out.printf("""
============================================
ID: %d
Description: "%s"
Status: %s
Updated: %s
Created: %s
============================================
                """, task.getId() , task.getDescription(), task.getStatus().getStatus() , task.getUpdatedAt(), task.getCreatedAt());
    }

    public static int isCorrectID(String commandWithID) {
        try {
            return Integer.parseInt(commandWithID);
        } catch (NumberFormatException _) {
            return -1;
        }
    }
}