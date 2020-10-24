package dal.fatih.todoly;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;


public class Main {

    public static Task handleCreateTask() {

        System.out.println("(*)  Can't be empty");
        System.out.print("Title of the task (*) : ");
        String title = scn.nextLine();
        System.out.print("Description(Optional): ");
        String description = scn.nextLine();
        System.out.print("Due Date dd/MM/yyyy (*): ");
        String date = scn.nextLine();

        try {
            Date date1 = new SimpleDateFormat("dd/MM/yyyy").parse(date);
            if (title.isEmpty()) {
                System.out.println("fill required fields");

            } else if (convertDate(date).before(new Date())) {
                System.out.println("The given date can not be older than now");

            } else {
                Task task = new Task(title, description, date1);
                tasks.add(task);
                System.out.println(task.getTitle() + " titled task added");
                return task;
            }
        } catch (Exception e) {
            System.out.println("Incorrect date format");
        }
        return handleCreateTask();
    }
    public static Date convertDate(String date) throws ParseException {
        DateFormat parser = new SimpleDateFormat("dd/MM/yyyy");
        Date date1 = parser.parse(date);
        return date1;
    }

    static Scanner scn = new Scanner(System.in);
    static ArrayList<Task> tasks = new ArrayList<>();

    public static void main(String[] args) throws ParseException {

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
                //System.out.println("This option is not supported yet");
                System.out.println(tasks);
            } else if (transaction.equals("3")) {
                System.out.println("This option is not supported yet");
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