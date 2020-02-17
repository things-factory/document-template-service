# document-template-service

## Prerequisites
- OpenJDK 1.8
- gradle

## How to use
```shell
java -jar document-template-service.jar
```

## Change server port
make the application.properties file. And open with editor.
```properties
server.port=PORT_NUMBER
```

## Write report
- design 
- create fields and datasource
- mapping with field
- get sub datasource from main datasource    
    ```java
    // e.g. get datasource named items from json data
    ((net.sf.jasperreports.engine.data.JsonDataSource)$P{REPORT_DATA_SOURCE}).subDataSource("items")
    ```
