# [Two-Tier Client-Server Application with MySQL and JDBC]()

This project is a Java-based two-tier client-server application that interacts with a MySQL database using JDBC (Java Database Connectivity). The application provides a graphical user interface (GUI) front-end for executing MySQL commands and retrieving query results.

## Features

- Connect to a remote MySQL database using JDBC.
- Execute any SQL Data Definition Language (DDL) or Data Manipulation Language (DML) command supported by MySQL.
- Display the command execution results in the GUI.
- User authentication through properties files.
- Separate logging of queries and updates in an operationslog database.
- Support for multiple simultaneous client connections.

## Prerequisites

Before running the application, make sure you have the following:

- MySQL Workbench installed and configured.
- Java Development Kit (JDK) installed.
- MySQL database created and populated with the provided script (project2dbscript.sql).
- Create a client-level user with limited permissions on the project2 and bikedb schemas.
- Create the operationslog database using the project2operationslog.sql script.
- Configure the necessary properties files for database driver, URL, and user credentials.

## Usage

1. Clone the project repository to your local machine.
2. Open the project in your preferred Java development environment.
3. Update the necessary properties files with the appropriate database information and user credentials.
4. Build and run the application.

<pre dir="ltr" class="w-full"><div class="bg-black mb-4 rounded-md"><div id="code-header" class="flex items-center relative text-gray-200 bg-gray-800 px-4 py-2 text-xs font-sans rounded-t-md"><span class="">bash</span><button id="copy-code" data-initialized="true" class="flex ml-auto gap-2"><svg stroke="currentColor" fill="none" stroke-width="2" viewBox="0 0 24 24" stroke-linecap="round" stroke-linejoin="round" class="h-4 w-4" height="1em" width="1em" xmlns="http://www.w3.org/2000/svg"><path d="M16 4h2a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2H6a2 2 0 0 1-2-2V6a2 2 0 0 1 2-2h2"></path><rect x="8" y="2" width="8" height="4" rx="1" ry="1"></rect></svg>Copy code</button></div><div class="p-4 overflow-y-auto"><code class="!whitespace-pre hljs language-bash"># Run the project
npm start
</code></div></div></pre>

5. In the GUI, select the properties files for the database connection.
6. Enter your username and password in the designated fields.
7. Enter a valid SQL command in the command window.
8. Click the "Execute" button to execute the command.
9. View the results in the SQL Execution Result window.
10. Use the provided buttons to clear the command window, results window, or establish a new connection.

## Screenshots

Refer to the provided screenshots for visual reference on the application's GUI and different command executions.

## Notes

- Only single SQL commands can be executed; script execution is not supported.
- MySQL-specific commands do not display results in the SQL Execution Result window.
- Non-query DML and DDL commands should have before and after screenshots to demonstrate their effects.

Please follow the instructions in the documentation carefully to set up and run the application successfully. For any further questions or clarifications, refer to the provided lecture notes or contact the project instructor.
