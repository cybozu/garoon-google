
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

with secure access
```sh
$ java -Djavax.net.ssl.keyStore=xxxx.pfx -Djavax.net.ssl.keyStorePassword=xxxx -Djavax.net.ssl.keyStoreType=PKCS12 -jar GGsync.jar .
```

with proxy
```sh
$ java -Dhttp.proxyHost=ホスト名 -Dhttp.proxyPort=ポート番号 -Dhttps.proxyHost=ホスト名 -Dhttps.proxyPort=ポート番号 -jar GGsync.jar .
```
