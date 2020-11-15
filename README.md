# Todoly
 `Todoly` is a task organizer

## Usage
### Build

```
docker build --tag todoly:latest .
```
### Run

```
docker run --rm -it --name todoly-app -v $PWD/.output/:/root/.output todoly:latest
```

