## Project Overview

This is a small Java console project demonstrating a simple DAO pattern backed by a MySQL connection.

- Source layout: `src/` (sources), `lib/` (external jars), compiled output in `bin/`.
- Entrypoint: `application.App` — creates a `User` and calls `UserDAO.createUser()`.

## Big Picture / Architecture

- Layers: `entities` (POJOs) → `DAO` (data access) → `connection` (JDBC connection) → `application` (runner).
- Data flow example: `application.App` constructs `entities.User` → `DAO.UserDAO.createUser()` → uses `connection.MySQLConnection.getConnection()` to obtain a JDBC `Connection` and runs a `PreparedStatement` inserting into `user_table`.

Key example files:

- [src/application/App.java](src/application/App.java)
- [src/DAO/UserDAO.java](src/DAO/UserDAO.java)
- [src/connection/MySQLConnection.java](src/connection/MySQLConnection.java)
- [src/entities/User.java](src/entities/User.java)

## Project-specific Patterns & Conventions

- Single shared JDBC connection: `MySQLConnection` holds a static `Connection` and returns it via `getConnection()`.
- DAOs use `PreparedStatement` with parameter binding; SQL strings are inlined in DAO methods (e.g., `INSERT INTO user_table ...`).
- Error handling: exceptions are caught and printed with `e.printStackTrace()` — no custom logging framework is used.
- POJOs follow simple getters/setters and a constructor for required fields (`User` has `username`, `password`, `email`).

## Build & Run (developer workflows)

Preferred local workflows:

- Use VS Code Java extension (project compiles to `bin/` automatically).
- Manual quick compile & run (project root):

```bash
mkdir -p bin
javac -d bin src/application/App.java src/connection/MySQLConnection.java src/DAO/UserDAO.java src/entities/User.java
java -cp bin application.App
```

Notes:

- Database is configured in `MySQLConnection` (jdbc url `jdbc:mysql://localhost:3306/exemplodb`, user `root`, password `1234567`). Ensure a MySQL instance and the `exemplodb` schema exist and `user_table` has columns `(username, password, email)` before running.

## Integration Points & External Dependencies

- External dependency: MySQL JDBC driver is expected under `lib/` or available via IDE classpath. If running manually, add the driver jar to the classpath.
- The code expects a running MySQL server and a `user_table` schema — this is the main external integration.

## What to look for when editing

- When adding DAO methods, follow existing pattern: get connection from `MySQLConnection`, create `PreparedStatement`, set parameters, call `executeUpdate()` or `executeQuery()` and close resources.
- Be aware `MySQLConnection.getConnection()` may return `null` on error — callers don't currently check for `null`.
- The project uses try-with-resources in DAOs but sometimes still calls `pstmt.close()` explicitly; be consistent when changing resources handling.

## Useful examples (copy-paste)

Insert pattern from `UserDAO`:

```java
String sql = "INSERT INTO user_table (username, password, email) VALUES (?, ?, ?)";
try (PreparedStatement pstmt = MySQLConnection.getConnection().prepareStatement(sql)) {
    pstmt.setString(1, user.getUsername());
    pstmt.setString(2, user.getPassword());
    pstmt.setString(3, user.getEmail());
    pstmt.executeUpdate();
}
```

## Merge/Editing Guidelines for AI agents

- Preserve current file-level structure and package names.
- When modifying DB connection details, update only `MySQLConnection` and note credentials are hard-coded.
- Do not introduce build system changes (no Maven/Gradle) unless explicitly requested — this project is plain Java sources compiled to `bin/`.

---

If anything above is unclear or you want me to include additional examples (tests, schema DDL, or a Gradle build), tell me what to add and I'll iterate.
