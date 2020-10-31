package dal.fatih.todoly;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;


public class Main {

   /* public static String dateFormatter(Date date) {
        DateFormat dueDateFormat = new SimpleDateFormat("dd.MM.yyyy");
        String resultDate = dueDateFormat.format(date);
        return resultDate;
    }*/

    public static Task handleCreateTask() {

        System.out.println("(*)  Can't be empty");
        System.out.print("Title of the task (*) : ");
        String title = scn.nextLine();
        System.out.print("Description(Optional): ");
        String description = scn.nextLine();
        System.out.print("Due Date dd/MM/yyyy (*): ");
        String dueDateInput = scn.nextLine();

        try {
            DateFormat dueDateParser = new SimpleDateFormat("dd/MM/yyyy");
            Date dueDate = dueDateParser.parse(dueDateInput);
            if (title.isEmpty()) {
                System.out.println("fill required fields");

            } else if (dueDate.before(new Date())) {
                System.out.println("The given date can not be older than now");

            } else {
                UUID uniqId = UUID.randomUUID();
                Task task = new Task(uniqId, title, description, dueDate);
                tasks.put(uniqId.toString(), task);
                System.out.println(task.getTitle() + " titled task added");
                return task;
            }
        } catch (Exception e) {
            System.out.println("Incorrect date format");
        }
        return handleCreateTask();
    }

    public static void listAllTasks() {
        if (tasks.isEmpty()) {
            System.out.println("Task list is empty");
        } else {
            for (Map.Entry task : tasks.entrySet()) {
                System.out.println(task.getValue());
                System.out.println("----------------------------------");
            }
        }
    }

    public static void showTaskDetails() {
        System.out.print("Task Id :");
        String taskId = scn.nextLine();
        Task task = tasks.get(taskId);
        if (task != null) {
            System.out.println(tasks.get(taskId));
        } else {
            System.out.println("Task not found");
        }
    }

    public static void deleteTask() {
        System.out.print("Task Id :");
        String taskId = scn.nextLine();
        Task task = tasks.get(taskId);
        if (task != null) {
            tasks.remove(taskId);
            System.out.println(task.getTitle() + "titled task deleted");
        } else {
            System.out.println("Task not found");
        }
    }

    public static void filterTask() {

        try {
            DateFormat dueDateParser = new SimpleDateFormat("dd/MM/yyyy");
            ArrayList<Task> taskList = new ArrayList<>(tasks.values());
            System.out.println("Last Date");
            String lastDateInput = scn.nextLine();
            Date lastDate = dueDateParser.parse(lastDateInput);

            int none = 0;
            for (Task task : taskList) {
                if (task.getDate().before(lastDate)) {
                    System.out.println(task);
                    none++;
                }
            }
            if (none == 0) {
                System.out.println("No tasks found between these dates");
            }
        } catch (Exception e) {
            System.out.println("Incorrect date format");
        }
    }

    static Scanner scn = new Scanner(System.in);
    static Map<String, Task> tasks = new HashMap<String, Task>();

    public static void main(String[] args) {

        System.out.println("Welcome to todoly");
        System.out.println("------------------------------------");
        String transactions = ("1- Create new task\n" +
                "2- All tasks list\n" +
                "3- Task details\n" +
                "4- Delete Task\n" +
                "5- List between two dates\n" +
                "q- Qit from Todoly");
        System.out.println("Transactions : \n" + transactions);
        System.out.println("Please select the action you want to do");
        int loopCounter = 0;
        while (true) {

            if (loopCounter >= 1) {
                System.out.println("To see the actions menu (t) ");
            }
            loopCounter++;
            System.out.print("Choice: ");
            String transaction = scn.nextLine();

            if (transaction.equals("q")) {
                System.out.println("Exiting todoly");
                break;

            } else if (transaction.equals("t")) {
                System.out.println(transactions);
                loopCounter = 0;
            } else if (transaction.equals("1")) {
                handleCreateTask();
            } else if (transaction.equals("2")) {
                listAllTasks();
            } else if (transaction.equals("3")) {
                showTaskDetails();
            } else if (transaction.equals("4")) {
                deleteTask();
            } else if (transaction.equals("5")) {
                filterTask();
            } else {
                System.out.println("invalid input");
            }
        }
    }
}