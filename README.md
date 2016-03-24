
To synchronize the Garoon schedule to Google Calendar.

## Requirements

- Java 1.8

## Usage

### 1.Build
```sh
$ ./gradlew clean build copy
```

### 2.Setting
```sh
$ cp ./src/main/resources/GGsync.properties .
```

and rewrite GGsync.properties.

See also https://cybozudev.zendesk.com/hc/ja/articles/204426680

### 3.Synchronize
```sh
$ java -jar GGsync.jar .
```
