# Java Smart Plug Energy Monitor

A Java smart plug energy monitoring system with client-server communication, Swing-based GUI, socket communication, threaded clients, and live power consumption updates.

The project was developed as part of an object-oriented programming and threads course. It demonstrates how multiple smart plug clients can send power consumption data to a server, where the total and individual consumption values are displayed in real time.

## Features

- Start a local energy monitoring server
- Create multiple smart plug clients
- Select appliance type and simulate power consumption with a slider
- Send power values from client to server over sockets
- Validate client connections using a security token
- Update live power consumption data in the server GUI
- Use threads and buffers for asynchronous data handling
- Display live updates for connected appliances

## Tech Stack

- Java
- Swing
- Sockets
- Threads
- Client-server architecture
- JFreeChart
- Course-provided support library

## Key Concepts

- Object-oriented design
- Client-server communication
- Threaded programming
- Data streams
- GUI programming with Swing
- Real-time data updates
- Encapsulation and separation of responsibilities

## Project Structure

```text
src/
  Main.java
  config/
    SmartPlugConfig.java
  Client/
    ClientGUI.java
    EnergyClient.java
  server/
    EnergyServer.java
    ServerConnectionWorker.java
    ServerGUIHandler.java

ClassDiagram/
```
## How It Works
The application starts with a small control window where the user can start the server and create smart plug clients. Each client represents an appliance, such as a laptop, TV, fridge, or lamp.

A client connects to the server using a socket connection and sends power consumption values. The values are placed in a buffer and sent asynchronously to the server. The server receives updates from connected clients and displays the current total and individual power consumption in its GUI.

Dependencies
This project depends on course-provided libraries from Malmö University and JFreeChart. These .jar files are not included in this repository because they are external dependencies.

To run the project locally, the required libraries must be added to the classpath.

Status
Educational Java project focused on object-oriented design, threads, sockets, GUI programming, and client-server communication.
