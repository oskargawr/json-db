# ogawryszewski_challenging_JSONDatabase



# JSON Database Client-Server Application

## Overview

This project is a **JSON database client-server application**. It operates as a client-server architecture, 
allowing multiple clients to interact with a central server. The database is stored in a JSON file located 
at `/resources/db.json`.


## Features

- **Data Operations**: Supports three primary operations:
    - **Set**: Add or update data in the database.
    - **Get**: Retrieve data from the database.
    - **Delete**: Remove data from the database.

- **Nested Entries**: Supports nested data structures, allowing for complex data organization and retrieval.

- **Command-Line Interface**: The client interacts with the server via command-line arguments for straightforward usage.

- **File Input Support**: Commands can be read from a file using the `-in` argument for batch processing.

- **Multithreading Support**: The server is capable of handling multiple client connections simultaneously.

- **Command Design Pattern**: Implements the command design pattern for executing commands, enhancing maintainability and scalability.


## Using the Client

To interact with the server, use the client CLI. The following command-line arguments are supported:

- `-t` (type): Specify the operation type (`set`, `get`, or `delete`).
- `-k` (index): Provide the database index/key for the operation.
- `-v` (value): Provide the value for `set` operations.
- `-in`: Specify a file containing commands to execute.

### Examples

#### Set a Value

To set a value in the database, use the following arguments:

```bash
-t set -k name -v "John Doe"
```

### Get a value
To retrieve the value of a specific key:
```bash
-t get -k name
```

### Delete a value
To delete a key-value pair from the database:
```bash
-t delete -k name
```

