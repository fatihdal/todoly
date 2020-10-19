package dal.fatih.todoly;

public class Main {

    public static void main(String[] args) {
    }
}


//Todoly:
//* Should have a command line interface (CLI)
//* Should offer three operations;
//    * (1) To create new task in the todo list
//        * Should have a unique ID that is automatically generated
//        * Should have a title that is passed by the user (Not empty)
//        * Should have a description that is passed by the user (Can be empty)
//        * Should have a due date that is passed by the user (dd.MM.yyyy ex: 21.12.2020)
//            * The given date format must be validated
//            * The given date can not be older than now
//    * (2) To list all tasks in the todo list (ID, Title, DueDate)
//    * (3) To print detail of a task with a give task ID by the user (ID, Title, Description,  DueDate)
//    * (4) To delete a task with a given task ID (Print title, and ask if the user is sure and let user answer 'y' or 'n')
//    * (5) To filter and list tasks whose due date is earlier than a given datetime (dd.MM.yyyy ex: 21.12.2020)

//Note: In the first version of the project, it is not needed to remember the tasks
// that are created on the first run when the application runs for the second time.
