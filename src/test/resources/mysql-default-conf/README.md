# Troubleshooting

## 1. Testcontainers
For Testcontainers, the following error occurs when running the test:

```shell
> docker logs <mysql_container_id>

Error: -19T04:32:46.946550Z 0 [ERROR] [MY-000068] [Server] unknown option '--skip-host-cache'.
```

Solve: https://github.com/testcontainers/testcontainers-java/issues/8130#issuecomment-1900154122