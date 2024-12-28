[SQLSchema.txt](https://github.com/user-attachments/files/18264805/SQLSchema.txt)# KickNStyle

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

- **User -Friendly Interface**: Intuitive UI for managing sneaker inventory.
- **Batch Management**: Add, edit, and delete sneakers and batch data.
- **Sales Tracking**: Track sales and customer details.
- **eCommerce Pane**: Prototype GUI for customers to choose sneakers with a recommendation system.
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
        <dependency>
            <groupId>com.jfoenix</groupId>
            <artifactId>jfoenix</artifactId>
            <version>9.0.10</version>
        </dependency>
        <dependency>
            <groupId>de.jensd</groupId>
            <artifactId>fontawesomefx</artifactId>
            <version>8.2</version>
        </dependency>
        <dependency>
            <groupId>com.mysql</groupId>
            <artifactId>mysql-connector-j</artifactId>
            <version>8.0.33</version>
        </dependency>
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
        <dependency>
            <groupId>org.kordamp.ikonli</groupId>
            <artifactId>ikonli-javafx</artifactId>
            <version>12.2.0</version>
        </dependency>
        <dependency>
            <groupId>org.kordamp.ikonli</groupId>
            <artifactId>ikonli-fontawesome5-pack</artifactId>
            <version>12.2.0</version>
        </dependency>
        <dependency>
            <groupId>org.kordamp.ikonli</groupId>
            <artifactId>ikonli-core</artifactId>
            <version>12.2.0</version>           
        </dependency>
        <dependency>
            <groupId>org.kordamp.ikonli</groupId>
            <artifactId>ikonli-javafx</artifactId>
            <version>12.2.0</version>
        </dependency>
        <dependency>
            <groupId>org.kordamp.ikonli</groupId>
            <artifactId>ikonli-fontawesome5-pack</artifactId>
            <version>12.2.0</version>
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
   - Schema used here: [Uploading SQLS    -- Create DPD_Supplier table
    CREATE TABLE DPD_Supplier (
        Supplier_ID INT AUTO_INCREMENT PRIMARY KEY,
        Supplier_Name VARCHAR(255) NOT NULL,
        Supplier_Contact VARCHAR(255),
        Supplier_Address TEXT
    );

    -- Create DPD_Shoe_Brand table
    CREATE TABLE DPD_Shoe_Brand (
        Brand_ID INT AUTO_INCREMENT PRIMARY KEY,
        Brand_Name VARCHAR(255) NOT NULL,
        Brand_Description TEXT
    );

    -- Create DPD_Sneaker_Category table
    CREATE TABLE DPD_Sneaker_Category (
        Category_ID INT AUTO_INCREMENT PRIMARY KEY,
        Category_Name VARCHAR(255) NOT NULL,
        Category_Description TEXT
    );

    -- Create DPD_Sneaker_Batch table
    CREATE TABLE DPD_Sneaker_Batch (
        Batch_ID INT AUTO_INCREMENT PRIMARY KEY,
        Batch_Number VARCHAR(100) NOT NULL,
        Batch_Date DATE NOT NULL,
        Supplier_ID INT,
        Batch_Status ENUM('Dispatched', 'Delivered') DEFAULT 'Dispatched',
        FOREIGN KEY (Supplier_ID) REFERENCES DPD_Supplier(Supplier_ID)
    );


    -- Create DPD_Sneaker table
    CREATE TABLE DPD_Sneaker (
        Sneaker_ID INT AUTO_INCREMENT PRIMARY KEY,
        Sneaker_Name VARCHAR(255) NOT NULL,
        Sneaker_Edition VARCHAR(255),
        Sneaker_Size VARCHAR(50),
        Sneaker_Category_ID INT,
        Sneaker_Selling_Price DECIMAL(10, 2) NOT NULL,
        Brand_ID INT,
        FOREIGN KEY (Sneaker_Category_ID) REFERENCES DPD_Sneaker_Category(Category_ID),
        FOREIGN KEY (Brand_ID) REFERENCES DPD_Shoe_Brand(Brand_ID)
    );


    -- Create DPD_Sneaker_Batch_Detail table
    CREATE TABLE DPD_Sneaker_Batch_Detail (
        Batch_Detail_ID INT AUTO_INCREMENT PRIMARY KEY,
        Batch_ID INT,
        Sneaker_ID INT,
        Quantity INT NOT NULL,
        Unit_Cost DECIMAL(10, 2) NOT NULL,
        Remaining_Quantity INT NOT NULL,
        FOREIGN KEY (Batch_ID) REFERENCES DPD_Sneaker_Batch(Batch_ID),
        FOREIGN KEY (Sneaker_ID) REFERENCES DPD_Sneaker(Sneaker_ID)
    );



    -- Create DPD_Customer table
    CREATE TABLE DPD_Customer (
        Customer_ID INT AUTO_INCREMENT PRIMARY KEY,
        Customer_Name VARCHAR(255) NOT NULL,
        Customer_Address TEXT,
        Contact_Information VARCHAR(255),
        Phone VARCHAR(50)
    );

    -- Create DPD_Sales table
    CREATE TABLE DPD_Sales (
        Sale_ID INT AUTO_INCREMENT PRIMARY KEY,
        Sale_Quantity INT NOT NULL,
        Date_of_Sale DATE NOT NULL,
        Total_Amount DECIMAL(10, 2) NOT NULL,
        Payment_Method ENUM('Cash', 'Card', 'Online Transfer', 'Other') DEFAULT 'Cash',
        Customer_ID INT,
        FOREIGN KEY (Customer_ID) REFERENCES DPD_Customer(Customer_ID)
    );

    -- Create DPD_Sales_Detail table
    CREATE TABLE DPD_Sales_Detail (
        Sales_Detail_ID INT AUTO_INCREMENT PRIMARY KEY,
        Sale_ID INT,
        Sneaker_ID INT,
        Quantity INT NOT NULL,
        Unit_Price DECIMAL(10, 2) NOT NULL,
        FOREIGN KEY (Sale_ID) REFERENCES DPD_Sales(Sale_ID),
        FOREIGN KEY (Sneaker_ID) REFERENCES DPD_Sneaker(Sneaker_ID)
    );

    -- table to track sneaker sales and quantities
    CREATE TABLE DPD_Sneaker_Sales (
        Sneaker_Sale_ID INT AUTO_INCREMENT PRIMARY KEY,
        Sneaker_ID INT NOT NULL,
        Sale_ID INT NOT NULL,
        Quantity_Sold INT NOT NULL,
        Sale_Price DECIMAL(10, 2) NOT NULL,
        FOREIGN KEY (Sneaker_ID) REFERENCES DPD_Sneaker(Sneaker_ID),
        FOREIGN KEY (Sale_ID) REFERENCES DPD_Sales(Sale_ID)
    );

chema.txt…]()


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
