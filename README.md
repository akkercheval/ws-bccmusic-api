# ws-bccmusic-api
This webservice supports an MSSQL database: BCCMusic and allows for the creation, search, and update of music scores.  It can be run as a standard Java application.  

To run the application locally, you will need to set up a SQL database and update the application.yaml's datasource url to connect to it.  My production database uses MSSQL.  If you use a different kind of relational database, you will also need to update the driver-class name and pom dependency.

VM Arguments to run locally.  You will need to provide a username and password for your SQL server:  

-Dspring.datasource.username=  
-Dspring.datasource.password=  
-Dspring.profiles.active=local 
