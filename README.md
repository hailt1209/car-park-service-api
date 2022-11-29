# Car Park Service Api

A Spring Boot application that returns the closest car parks to a user, together with each parking lot’s availability.

## Data sources
1. Car park information (CSV format): https://data.gov.sg/dataset/hdb-carpark-information
2. Car park availability (Rest endpoint): https://data.gov.sg/dataset/carpark-availability

Note: The coordinates provided are in a SVY21 format. We may have to do some conversion to a more widely used format.

## Approach
This application works with geographic objects and needs some functionalities like coordinate saving, coordinate format conversion, and distance calculation,... then I select PostgreSQL (plus PostGIS extension) as an application database.

With the help of the PostGIS extension we can easily do some kinds of stuff:
* Setup a geometry with spatial reference id set to srid: `geometry ST_SetSRID(geometry geom, integer srid);`. ([Reference](https://postgis.net/docs/ST_SetSRID.html))
* Transform coordinates to a different spatial reference system: `geometry ST_Transform(geometry g1, integer srid);` ([Reference](https://postgis.net/docs/ST_Transform.html))
* Get distance between two geometries: `float ST_Distance(geometry g1, geometry g2);` ([Reference](https://postgis.net/docs/ST_Distance.html))

First of all, we need to extract, transform, and load the car park information into our database. To do this, I defined 2 components
- CarParkLoader: This will create a batch job to read the CSV and insert data into our database in chunks.
- CarParkAvailabilityLoader: this will create another batch job to retrieve data from the REST endpoint and upsert (update an existing row if a specified value already exists, and insert a new row if the specified value doesn't already exist) into our database in chunks. 

To initiate the data, when the application is started it will trigger CarParkLoader to load car park data if the data doesn't already exist. Once CarParkLoader finishes, it will also trigger CarParkAvailabilityLoader to load the availability of car parks.

To get the closest car parks together with each parking lot’s availability, we will query the data in our database, calculate the distance between the user's location and the car park's location and then sort it according.

## Building from Source
Car Park Service Api uses a Maven-based build system

### Prerequisites
1. GIT
1. Java OpenJDK 11
1. Maven 3.8.6
1. PostgreSQL 13 with PostGIS extension. [How to install PostgreSQL/PostGIS](https://postgis.net/workshops/postgis-intro/installation.html)

### Check out sources
```
git clone git@github.com:hailt1209/car-park-service-api.git
cd car-park-service-api
```

### Create database
This application uses PostgreSQL to store data with a help of PostGis extension.
1. Database for main application
```
CREATE DATABASE car_park_information;
```
2. Database testing
```
CREATE DATABASE car_park_information_test;
```
3. Enabling PostGIS extension for these 2 databases above by accessing into them and run the query:
```
CREATE EXTENSION IF NOT EXISTS postgis;
```
### Config datasource for the application
1. File src/main/resources/application.properties
```
spring.datasource.url=jdbc:postgresql://{host}:{port}/car_park_information
spring.datasource.username={username}
spring.datasource.password={password}
```
2. File src/test/resources/application.properties
```
spring.datasource.url=jdbc:postgresql://{host}:{port}/car_park_information_test
spring.datasource.username={username}
spring.datasource.password={password}
```

### Compile and test
```
mvn clean install
```

### Run application
```
mvn spring-boot:run
```
or 
```
java -jar car-park-service-api/target/car-park-service-api-0.0.1-SNAPSHOT.jar
```
