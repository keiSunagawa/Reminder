```
$ sbt "project reminder-server" docker
$ docker run -d -p 8080:8080 com.kerfume/reminder-server
$ curl -d '{"title": "task A", "trigger": "2007-12-03"}' "localhost:30080/regist/date"
$ curl -v -X POST -- "localhost:8080/resolve/1"
```
