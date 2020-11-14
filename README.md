# Todoly
 `Todoly` is a task organizer

## Usage
### Build

```
docker build --tag todoly:latest .
```
### Run

```
docker volume create --name root
```



```
docker run --rm -it --name todoly-app -v root:/root todoly:latest
```

