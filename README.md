[![Build Status](https://travis-ci.org/vladsmirn289/ItemServiceRest.svg?branch=master)](https://travis-ci.org/github/vladsmirn289/ItemServiceRest)
[![BCH compliance](https://bettercodehub.com/edge/badge/vladsmirn289/ItemServiceRest?branch=master)](https://bettercodehub.com/)
# Item service

## About
This is the client service for [goods-shop-rest] project. This service allows retrieve, save or delete items.

## If you find a bug, or you have any suggestions
You can follow the next link and describe your problem/suggestion: https://github.com/vladsmirn289/ItemServiceRest/issues

## Running
If you want to run this service separately, you can run it from your IDE, or use the next command:
```shell script
docker-compose up
```
from a root folder of the project. However, docker-compose.yml file start the postgreSQL database on the port
5432, you can add the next option to postgreSQL service in the docker-compose.yml:
```shell script
network_mode: "host"
```
You also need to have the shop_db database.

## Using
After start, you can use a **[postman]** application or follow the next link http://localhost:8083/items-rest-swagger.
The second variant is the swagger documentation, where you also can perform any http requests (GET, POST, PUT, etc.).
The first variant is more universal method with using third-party application.

## Package structure
The diagram of the package structure:
*   GoodsShop
    *   data (database schema and data for separately running)
    *   logs
    *   src
        *   [main]
            *   [java]
                *   [com.shop.ItemServiceRest]
                    *   [Config] (Swagger config)
                    *   [Controller] (Item and Root controllers)
                    *   [Jackson] (Serializers and deserializers for item class)
                    *   [Model] (JPA entities)
                    *   [Repository] (Spring Data repos)
                    *   [Service] (Service logic, that delegates work to the repos)
                    *   [ItemServiceRestApp.java] (Main class for, Spring Boot)
            *   [resources]
                *   [application.properties] (Stores various properties of the database, JWT and swagger)
                *   [log4j2.xml] (Stores log4j2 properties)
        *   [test]
            *   [java][java2]
                *   [com.shop.ItemServiceRest][comInTest]
                    *   [Controller][ControllerTest]
                    *   [DTO][DTOTest]
                    *   [Model][ModelTest]
                    *   [Repository][RepoTest]
                    *   [Service][ServiceTest]
                    *   [docker-compose-test.yml]
            *   [resources][testRes]
                *   [db][testDb]
                    *   [PostgreSQL] (H2 scripts for tests)
                *   [application.properties][application-test.properties] (Various properties for test environment)

## License
Item service is the service released under version 2.0 of the [Apache License](https://www.apache.org/licenses/LICENSE-2.0).

[goods-shop-rest]: https://github.com/vladsmirn289/GoodsShopRest
[postman]: https://www.postman.com/

[main]: ./src/main
[java]: ./src/main/java
[com.shop.ItemServiceRest]: ./src/main/java/com/shop/ItemServiceRest
[Config]: ./src/main/java/com/shop/ItemServiceRest/Config
[Controller]: ./src/main/java/com/shop/ItemServiceRest/Controller
[Jackson]: ./src/main/java/com/shop/ItemServiceRest/Jackson
[Model]: ./src/main/java/com/shop/ItemServiceRest/Model
[Repository]: ./src/main/java/com/shop/ItemServiceRest/Repository
[Service]: ./src/main/java/com/shop/ItemServiceRest/Service
[ItemServiceRestApp.java]: ./src/main/java/com/shop/ItemServiceRest/ItemServiceRestApp.java

[resources]: ./src/main/resources
[application.properties]: ./src/main/resources/application.properties
[log4j2.xml]: ./src/main/resources/log4j2.xml

[test]: ./src/test
[testRes]: ./src/test/resources
[testDb]: ./src/test/resources/db
[PostgreSQL]: ./src/test/resources/db/PostgreSQL
[application-test.properties]: ./src/test/resources/application.properties
[java2]: ./src/test/java
[comInTest]: ./src/test/java/com/shop/ItemServiceRest
[ControllerTest]: ./src/test/java/com/shop/ItemServiceRest/Controller
[DTOTest]: ./src/test/java/com/shop/ItemServiceRest/DTO
[ModelTest]: ./src/test/java/com/shop/ItemServiceRest/Model
[RepoTest]: ./src/test/java/com/shop/ItemServiceRest/Repository
[ServiceTest]: ./src/test/java/com/shop/ItemServiceRest/Service
[docker-compose-test.yml]: ./src/test/java/com/shop/ItemServiceRest/docker-compose-test.yml