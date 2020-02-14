# document-template-service

## Prerequisites
- OpenJDK 1.8
- gradle

## Write report
- design 
- create fields and datasource
- mapping with field
- get sub datasource from main datasource    
    ```java
        ((net.sf.jasperreports.engine.data.JsonDataSource)$P{REPORT_DATA_SOURCE}).subDataSource("items")
    ```
