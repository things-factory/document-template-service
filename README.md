# document-template-service

## Prerequisites
- OpenJDK 1.8
- gradle

## How to use
1. [Download](https://github.com/things-factory/document-template-service/releases) the jar file
1. open terminal and move to the downloaded jar
1. type this command
    ```shell
    java -jar document-template-service.jar
    ```

## Change server port
make the application.properties file. And open with editor.
```properties
server.port=PORT_NUMBER
```

## Write report
- design the template using [Jasper Studio](https://www.jaspersoft.com/products/jaspersoft-studio)
- create fields and datasource
- mapping with field
- get sub datasource from main datasource    
    ```java
    // e.g. get datasource named items from json data
    ((net.sf.jasperreports.engine.data.JsonDataSource)$P{REPORT_DATA_SOURCE}).subDataSource("items")
    ```
