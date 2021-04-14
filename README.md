# Todoly
 `Todoly` is a task organizer

## Usage
### Build

```
./gradlew clean build

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
####And the result
<img src="https://user-images.githubusercontent.com/10801236/114638976-703dd680-9cd5-11eb-9cdd-98c3f7798136.jpg">
