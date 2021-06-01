# Todoly

`Todoly` is a task organizer

### Build

```
docker build --tag todoly:latest .
```

### Run

```
docker run --rm -it -p 8080:8080 --name todoly-app todoly:latest
```

### Check

```
http://localhost:8080/actuator/health
```

#### And the result

[![healt-cheack image](https://user-images.githubusercontent.com/10801236/114638976-703dd680-9cd5-11eb-9cdd-98c3f7798136.jpg)](https://user-images.githubusercontent.com/10801236/114638976-703dd680-9cd5-11eb-9cdd-98c3f7798136.jpg)

## Usage

*Can be used with any rest client application*

#### To create task

*Note: The title or due date should not be empty and title should be min five characters, also due date must be a future
date.*

```
POST
localhost:8080/todoly/task

{
    "title": "Title of task",
    "description": "Description of task",
    "dueDate": "20-03-2080 16:00:55"
}
```

#### To list all tasks

```
GET
localhost:8080/todoly/tasks
```

#### To get a task with id

```
GET
localhost:8080/todoly/task/<id>
```

#### To delete a task with id

```
DELETE
localhost:8080/todoly/task/<id>
```

#### To filter tasks by due date

```
GET
http://localhost:8080/todoly/tasks/duedate?duedate=yyyy-MM-ddTHH:mm:ss
```

#### To filter tasks by title or description

```
GET
http://localhost:8080/todoly/tasks/titleordesc?keyword=<word to search>
```