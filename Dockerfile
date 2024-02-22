FROM maven:3.9.6-eclipse-temurin-21-jammy AS mavenbuild
WORKDIR prebuild
COPY ./src ./src
COPY ./pom.xml ./
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre-jammy AS appbuilder
WORKDIR extracted
COPY --from=mavenbuild prebuild/target/order-service.jar ./
RUN java -Djarmode=layertools -jar order-service.jar extract

FROM eclipse-temurin:21-jre-jammy
RUN useradd orderservice
USER orderservice
WORKDIR app
COPY --from=appbuilder extracted/dependencies/ ./
COPY --from=appbuilder extracted/spring-boot-loader/ ./
COPY --from=appbuilder extracted/snapshot-dependencies/ ./
COPY --from=appbuilder extracted/application/ ./
EXPOSE 8080
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]