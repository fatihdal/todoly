package dal.fatih.todoly;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;


public class Main {

    public static String dateFormatter(Date date) {
        DateFormat dueDateFormat = new SimpleDateFormat("dd.MM.yyyy");
        String resultDate = dueDateFormat.format(date);
        return resultDate;
    }

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
                Task task = new Task(title, description, dueDate);
                tasks.add(task);
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
            for (Task task : tasks) {
                System.out.println("ID: " + task.getId() + " TITLE: " + task.getTitle() +
                        " DUE DATE: " + dateFormatter(task.getDate()));
            }
        }
    }

    public static void showTaskDetails() {
        if (tasks.isEmpty()) {
            System.out.println("Task list is empty");
        } else {
            System.out.println("Task Id :");
            String taskId = scn.nextLine();
            Task task = null;
            for (Task task_ : tasks) {
                if (task_.getId().equals(taskId)) {
                    task = task_;
                }
            }
            if (task != null) {
                System.out.println("ID: " + task.getId() + "\nTITLE: " + task.getTitle() +
                        "\nDESCRIPTION: " + task.getDescription() +
                        "\nDUE DATE: " + dateFormatter(task.getDate()));
            } else {
                System.out.println("Task not found");
            }
        }
    }

    static Scanner scn = new Scanner(System.in);
    static ArrayList<Task> tasks = new ArrayList<>();

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
                System.out.println("This option is not supported yet");
            } else if (transaction.equals("5")) {
                System.out.println("This option is not supported yet");
            } else {
                System.out.println("invalid input");
            }
        }
    }
}