# order-service

## Description
This is a simple order service that allows you to place, take and fetch orders.

## Getting Started
To get started, you need to have the following installed on your machine:
- Docker
- Docker Compose

---
1. Clone the repository

2. Create `.env` file
    ```shell
    cp .env.example .env
    ```

3. Set the `GOOGLE_MAPS_API_KEY` variable in the `.env` file
    ```env
    GOOGLE_MAPS_API_KEY=<your_key>
    ```

4. Execute `start.sh`
    ```shell
    chmod +x ./start.sh
    ./start.sh
    ```

5. Start using the API with port `8080`

## Tech Stack

- Java
- Spring Boot
- Mybatis
- MySQL
- Docker
- JUnit
- Testcontainers
