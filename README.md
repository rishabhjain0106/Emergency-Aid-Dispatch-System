# Emergency Aid Dispatch System

A Java-based desktop application designed to help coordinate emergency aid requests and volunteer assignments using graph algorithms and data structures.

## Overview

The Emergency Aid Dispatch System helps manage emergency requests, track volunteer availability, and assign volunteers to requests based on location, distance, and workload.

The project demonstrates the practical application of Design and Analysis of Algorithms (DAA) concepts in an emergency-response scenario.

## Features

- **Victim Portal:** Register emergency aid requests and check request status.
- **Volunteer Portal:** Register volunteers, view assignments, and manage assigned tasks.
- **Admin Portal:** Manage dispatch operations and monitor requests and volunteer workloads.
- **Graph-Based City Model:** Represent locations and roads as a weighted graph.
- **Shortest-Path Calculation:** Use Dijkstra's algorithm to calculate distances between locations.
- **Volunteer Dispatch:** Select suitable available volunteers based on distance, workload, and request priority factors.
- **Load Management:** Track volunteer capacity and assigned requests.
- **Road Management:** Support road-blocking and road-unblocking operations.
- **Local Data Persistence:** Store application data using JSON files.

## Technology Stack

- **Language:** Java
- **GUI:** Java Swing
- **Data Format:** JSON
- **JSON Library:** json-simple 1.1.1
- **Core Concepts:** Graphs, shortest-path algorithms, greedy selection, and load balancing
- **Development Environment:** Visual Studio Code or another Java IDE

## Project Structure

```text
DAA_Project/
├── src/
│   ├── Main.java
│   ├── GUI.java
│   ├── Graph.java
│   ├── Dispatcher.java
│   ├── DataStore.java
│   ├── AuthService.java
│   ├── Admin.java
│   ├── AidRequest.java
│   ├── User.java
│   ├── Volunteer.java
│   └── graph.txt
├── lib/
│   └── json-simple-1.1.1.jar
├── .gitignore
└── README.md
```

Runtime JSON files (`admin.json`, `victim.json`, and `volunteers.json`) are intentionally excluded from version control because they may contain sensitive information.

## Prerequisites

- Java Development Kit (JDK)
- Visual Studio Code or another Java IDE
- json-simple 1.1.1 library

## Setup and Installation

1. Clone the repository:

   ```bash
   git clone https://github.com/YOUR-USERNAME/DAA_Project.git
   ```

2. Open the project folder in Visual Studio Code.

3. Ensure that the `json-simple-1.1.1.jar` dependency is available in the `lib` directory.

4. Open a terminal at the project root and navigate to the source directory:

   ```bash
   cd src
   ```

5. Compile the Java source files on Windows:

   ```powershell
   javac -cp ".;../lib/json-simple-1.1.1.jar" *.java
   ```

6. Run the application:

   ```powershell
   java -cp ".;../lib/json-simple-1.1.1.jar" Main
   ```

**Note:** The classpath separator shown above (`;`) is for Windows. On macOS or Linux, use `:` instead.

## Data and Security

- Runtime JSON files may contain personal information and authentication data.
- These files are excluded from Git using `.gitignore`.
- Never commit real passwords, API keys, private credentials, or personal records.
- Authentication and password storage should be strengthened before production use.
- Use synthetic data for demonstrations and testing.

## Learning Outcomes

This project demonstrates how graph-based algorithms and data structures can support emergency-response planning, volunteer allocation, shortest-path calculations, and resource management.

## Future Enhancements

- Secure password hashing and authentication
- Database integration
- Interactive map integration
- Automated notifications for request updates
- Improved dispatch optimization and reporting
- Role-based access control

## Disclaimer

This is an academic project intended for learning and demonstration. It is not a certified emergency-response platform and should not be used as a substitute for official emergency services.
