# Smart Irrigation System
This project was realized as part of the Cloud of Things module at the Higher School of Communication of Tunis, dedicated to create an intelligent watering system.

Created by:
- [Molka Dammak]
- [Amina Abid]
- [Ghazza Ghorbel]

# Description

This project consists on developing and deploying a PWA application to monitor plants irrigation through IoT integration. The application allows real-time visualization of the humidity level, the Soil moisture and the temperature in order to keep the plants alive as much as possible with less water waste. 
The irrigation system automatically controls the irrigation cycles by predicting the pump status depending on the plants need thanks to a Machine Learning model.

# Technologies

- Wildfly 40.0.0 Beta1
- JDK 21.0.11
- Mosquitto broker
- MongoDB
- Docker

# IoT components:

- ESP32
- Moisture sensor
- LED

# Installation guide
- Clone the repository.
- Build and Run Dockerfile using the commands:
  `docker build -t cot:latest .`
  `docker run -p 8765:8765 -d cot`
- Upload iot.ino to ESP32 board.
- Build the code into a single.war file using the command `mvn clean install`
- Place smartirrigation-1.0-SNAPSHOT.war file in wildfly/standalone/deployments folder.
- Run wildfly using `standalone.bat` in the bin folder.
- Test the dashboard in localhost:8080

# Deployment Machine
The Application is hosted locally on a PC with the following characteristics:
- Ram: 7.43GB
- vCPUS: 20
- MongoDB: localhost:27017

