# File generator

csv files are kept in input folder & output jsons will be kept after processing into output folder in src/main/resources path.
spring boot scheduling is used to polling input folder for new files every 5 minutes.

Run command to build jar

mvn clean install

Run foolwing command to run spring  boot application

mvn spring-boot:run

Run following command to create docker image

mvn spring-boot:build-image
