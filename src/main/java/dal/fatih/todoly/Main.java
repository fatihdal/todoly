package dal.fatih.todoly;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;


public class Main {
    private static final Scanner scn = new Scanner(System.in);
    private static Map<String, Task> tasks = new HashMap<String, Task>();
    private static final DateFormat dueDateParser = new SimpleDateFormat("dd/MM/yyyy");
    private static final String file = "Tasks.bin";
    public static ObjectInputStream inputTask;
    public static ObjectOutputStream outputTask;

    public static Task handleCreateTask() {

        System.out.println("(*)  Can't be empty");
        System.out.print("Title of the task (*) : ");
        String title = scn.nextLine();
        System.out.print("Description(Optional): ");
        String description = scn.nextLine();
        System.out.print("Due Date dd/MM/yyyy (*): ");
        String dueDateInput = scn.nextLine();

        try {
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
                outputTask = new ObjectOutputStream(new FileOutputStream(file));
                outputTask.writeObject(tasks);
                outputTask.close();
                return task;
            }
        } catch (Exception e) {
            System.out.println("Incorrect date format");
        }
        return handleCreateTask();
    }

    public static void listAllTasks() {
        try {
            inputTask = new ObjectInputStream(new FileInputStream(file));
            tasks = (HashMap) inputTask.readObject();
            Set set = tasks.entrySet();
            Iterator iterator = set.iterator();
            while (iterator.hasNext()) {
                Map.Entry task = (Map.Entry) iterator.next();

                if (tasks.isEmpty()) {
                    System.out.println("Task list is empty");
                } else {
                    System.out.println(task.getValue());
                    System.out.println("----------------------------------");
                }
            }
        } catch (FileNotFoundException ex) {
            System.out.println("Tasks file not found");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void showTaskDetails() {
        try {
            inputTask = new ObjectInputStream(new FileInputStream(file));
            tasks = (HashMap) inputTask.readObject();
            System.out.print("Task Id :");
            String taskId = scn.nextLine();
            Task task = tasks.get(taskId);
            if (task != null) {
                System.out.println(tasks.get(taskId));
            } else {
                System.out.println("Task not found");
            }
        } catch (FileNotFoundException ex) {
            System.out.println("Tasks file not found");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void deleteTask() {
        try {
            inputTask = new ObjectInputStream(new FileInputStream(file));
            tasks = (HashMap) inputTask.readObject();
            System.out.print("Task Id :");
            String taskId = scn.nextLine();
            Task task = tasks.get(taskId);
            if (task != null) {
                tasks.remove(taskId);
                System.out.println(task.getTitle() + " titled task deleted");
                new FileOutputStream(file).close();
                outputTask = new ObjectOutputStream(new FileOutputStream(file));
                outputTask.writeObject(tasks);
                outputTask.close();
            } else {
                System.out.println("Task not found");
            }
        } catch (FileNotFoundException ex) {
            System.out.println("Tasks file not found");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void filterTask() {

        try {
            inputTask = new ObjectInputStream(new FileInputStream(file));
            tasks = (HashMap) inputTask.readObject();
            Map<String, Task> taskDate = tasks;
            List<Task> foundTask = new ArrayList<>();
            System.out.println("Last Date");
            String lastDateInput = scn.nextLine();
            Date lastDate = dueDateParser.parse(lastDateInput);

            for (Task task : taskDate.values()) {
                if (task.getDate().before(lastDate)) {
                    foundTask.add(task);
                }
            }
            if (foundTask.isEmpty()) {
                System.out.println("No tasks found between these dates");
            } else {
                System.out.println(foundTask);
            }
        } catch (FileNotFoundException ex) {
            System.out.println("Tasks file not found");
        } catch (Exception e) {
            System.out.println("Incorrect date format");
        }
    }

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