# Parallel communication with db using kotlin coroutine

* Update db config from application.yml
* Change db class in code
* Run the app
* Test with following curl command

#### Sequential

```
curl -o /dev/null -s -w 'Total: %{time_total}s\n' http://localhost:8080/consumer/APP                                                                                                                   [15:30:45]
Total: 6.058122s
```
#### Parallel

```
curl -o /dev/null -s -w 'Total: %{time_total}s\n' http://localhost:8080/consumer/p/APP                                                                                                                 [15:31:32]
Total: 2.088072s
```