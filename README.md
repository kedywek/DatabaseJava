# DatabaseJava

A lightweight, console-based relational database management system implemented in Java. This project provides a simplified SQL-like interface for managing databases, tables, columns, and rows, with support for JSON-based persistence.

## Features

* **SQL-like Command Interface**: Supports standard database operations through a console scanner.
* **Data Management**:
    * **CREATE**: Initialize new databases and tables with defined schemas.
    * **DROP**: Remove existing databases or tables.
    * **ALTER**: Modify table structures by adding, dropping, or modifying columns.
    * **SELECT**: Query data with support for specific columns and `WHERE` clause filtering.
    * **JOIN**: Perform inner joins between two tables based on a condition.
    * **INSERT/UPDATE/DELETE**: Full CRUD operations for managing table records.
* **Persistence**: Save and load database states to/from `.json` files using the `org.json` library.
* **Strong Typing**: Supports multiple data types including `STRING`, `INT`, `FLOAT`, `DOUBLE`, `BOOLEAN`, and `DATE`.

## Project Structure

The project is organized into several key classes within the `org.Database` package:

* **`Database`**: Manages a collection of tables and handles high-level operations like table joining.
* **`Table`**: Represents a data structure containing columns (linked for order) and a list of rows.
* **`Column`**: Defines the metadata for a table field, including its name and data type.
* **`Row`**: Holds the actual data mapping columns to their respective values.
* **`InputController`**: The main entry point that parses user input and routes commands to the appropriate logic.
* **`FileController`**: Handles the serialization and deserialization of the database to JSON format.

## Prerequisites

* **Java**: JDK 21
* **Maven**: For dependency management and building
* **Dependencies**: 
    * `org.json:json:20240303`

## Getting Started

### Installation

1.  Clone the repository.
2.  Ensure you have Maven installed and configured.
3.  Build the project using:
    ```bash
    mvn clean install
    ```

### Usage

Run the `InputController` class to start the interactive console. You can then enter commands such as:

* **Create a Database**: `CREATE DATABASE MyLibrary`
* **Create a Table**: `CREATE TABLE Books ( title STRING , author STRING , year INT )`
* **Insert Data**: `INSERT INTO Books ( title author year ) VALUES ( "The Hobbit" "Tolkien" 1937 )`
* **Query Data**: `SELECT * FROM Books WHERE year > 1900`
* **Save Progress**: `SAVE` (This creates `MyLibrary.json`)
* **Exit**: `EXIT`

## Configuration

* **Encoding**: The project uses UTF-8 encoding for all source files.
* **Build Output**: Compiled classes are directed to the `target/` directory, which is ignored by version control.
