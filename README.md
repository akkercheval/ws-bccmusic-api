# ws-bccmusic-api
This webservice supports an MSSQL database: BCCMusic and allows for the creation, search, and update of music scores.  It can be run as a standard Java application.  

You will need to add an application.yaml in order to run this application.  Text in **bold** indicates information you will need to supply based on your database.  The below template can be used for your application.yaml

'''  
spring:  
  application:  
    name: ws-bccmusic-api  
  datasource:  
    url: jdbc:sqlserver:**serverurl**;database=**database name**;encrypt=true;trustServerCertificate=true;  
    username: **user name to connect to the database**  
    password: **password to connect to the database**  
    driver-class-name: com.microsoft.sqlserver.jdbc.SQLServerDriver  
    hikari:  
      maximum-pool-size=10  
      minimum-idle=5  
      idle-timeout=300000  
      connection-timeout=20000  
  jpa:  
    hibernate:  
      ddl-auto: validate  
      naming:  
        physical-strategy: org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl  
    show-sql: false  
    properties:  
      hibernate:  
        dialect: org.hibernate.dialect.SQLServerDialect  
        format_sql: true  
  security:  
    password:  
      bcrypt-strength=12  
logging:  
  level:  
    org:  
      springframework:  
        security: DEBUG  
        web: DEBUG  
'''  