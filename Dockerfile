FROM alpine/git as clone
WORKDIR /home/itemService
RUN git clone https://github.com/vladsmirn289/ItemServiceRest.git

FROM maven:3.5-jdk-8-alpine as build
WORKDIR /home/itemService
COPY --from=clone /home/itemService/ItemServiceRest .
RUN mvn -DskipTests=true package

FROM openjdk:8-jre-alpine
WORKDIR /home/itemService
COPY --from=build /home/itemService/target/*.jar .
ENV db_host db
CMD java -jar *.jar --db_url=jdbc:postgresql://${db_host}:5432/shop_db