# KickNStyle

KickNStyle is a **JavaFX** project designed to manage sneaker inventory and sales. This application allows users to track sneakers, handle batch operations, and monitor sales efficiently. It utilizes **Maven** for dependency management and follows a modular architecture.

<div align="center">
  <img src="path/to/banner_image.png" alt="KickNStyle Banner" width="600" />
</div>

## Table of Contents

- [Features](#features)
- [Technologies Used](#technologies-used)
- [Dependencies](#dependencies)
- [Installation](#installation)
- [Usage](#usage)
- [Showcase](#showcase)
- [Screenshots](#screenshots)

---

## Features

- **User-Friendly Interface**: Intuitive UI for managing sneaker inventory.
- **Batch Management**: Add, edit, and delete sneakers and batch data.
- **Sales Tracking**: Track sales and customer details.
- **Reporting**: Generate reports and view dashboard summaries.
- **Modern UI**: Responsive design with rounded corners and updated JavaFX elements.

---

## Technologies Used

- **Java** – Core programming language for the application.
- **JavaFX** – Framework for building modern desktop UIs.
- **Maven** – Dependency management and build automation.
- **MySQL** – Database for storing sneaker and sales data.

---

## Dependencies

The following are the main dependencies included in the `pom.xml`:

```xml
<dependencies>
    <!-- JavaFX Controls and FXML -->
    <dependency>
        <groupId>org.openjfx</groupId>
        <artifactId>javafx-controls</artifactId>
        <version>22</version>
    </dependency>
    <dependency>
        <groupId>org.openjfx</groupId>
        <artifactId>javafx-fxml</artifactId>
        <version>22</version>
    </dependency>
    
    <!-- JFoenix for advanced UI components -->
    <dependency>
        <groupId>com.jfoenix</groupId>
        <artifactId>jfoenix</artifactId>
        <version>9.0.10</version>
    </dependency>
    
    <!-- FontAwesomeFX for icon support -->
    <dependency>
        <groupId>de.jensd</groupId>
        <artifactId>fontawesomefx</artifactId>
        <version>8.2</version>
    </dependency>
    
    <!-- MySQL Connector -->
    <dependency>
        <groupId>com.mysql</groupId>
        <artifactId>mysql-connector-j</artifactId>
        <version>8.0.33</version>
    </dependency>
    
    <!-- Logging -->
    <dependency>
        <groupId>org.slf4j</groupId>
        <artifactId>slf4j-api</artifactId>
        <version>1.7.32</version>
    </dependency>
    <dependency>
        <groupId>ch.qos.logback</groupId>
        <artifactId>logback-classic</artifactId>
        <version>1.2.6</version>
    </dependency>
</dependencies>
```

---

## Installation

1. **Clone the repository**:
   ```bash
   git clone https://github.com/yourusername/KickNStyle.git
   cd KickNStyle
   ```

2. **Edit Database Configuration**:
   - Open the file located at:  
     `Kick-N-Style/src/main/java/com/ddp/kicknstyle/util/DatabaseConnection.java`
   - Update the `localhost`, `username`, and `password` to match your MySQL database credentials:
     ```java
     // Example:
     private static final String URL = "jdbc:mysql://localhost:3306/your_database_name";
     private static final String USERNAME = "your_username";
     private static final String PASSWORD = "your_password";
     ```

3. **Prepare the Main GUI Controller**:
   - Open the file located at:  
     `Kick-N-Style/src/main/java/com/ddp/kicknstyle/controller/mainGUI_Controller.java`
   - Confirm or make any necessary updates for the application to run.

4. **Install Maven**:  
   Ensure Maven is installed on your machine.  
   [Download Maven](https://maven.apache.org/download.cgi)

5. **Build the project**:
   ```bash
   mvn clean install
   ```

6. **Run the application**:
   ```bash
   mvn javafx:run
   ```

---

## Usage

1. **Launch the application** and explore the user interface.
2. **Add Sneakers**: Use the "Add Sneaker" button to add new items to the inventory.
3. **Manage Batches and Sales**: Navigate to the respective sections for batch operations and sales tracking.
4. **View Dashboard**: Access sales summaries and analytics in the dashboard.

---

## Showcase

### Sneaker Management
![image](https://github.com/user-attachments/assets/c74480c2-9ca2-4100-8fd7-0b54ef1ff5b2)

### Sales Management
![image](https://github.com/user-attachments/assets/22753913-9b17-4c3e-bd34-af7180b8c0f1)

### Batch Management
![Screenshot 2024-12-23 092120](https://github.com/user-attachments/assets/a1782de6-0b58-4240-855d-b8e25da6432d)

---

<div align="center">
  <strong>🎉 Happy Sneaker Managing! 👟</strong>
  <br><br>
  ✨ BIG CONTRIBUTION WITH MY GROUPMATES: Kent Paulo Delgado, Theo Benedict Pasia, and Galileon Destura 💪🤝
</div>
