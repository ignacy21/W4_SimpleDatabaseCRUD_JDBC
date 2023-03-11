package projektWgKarola;

import java.sql.*;
import java.util.*;
import java.util.function.Consumer;

public class DatabaseRunner {

    private static final String URL = "jdbc:postgresql://localhost:5432/projekt";
    private static final String USERNAME = "postgresql";
    private static final String PASSWORD = "postgresql";

    private static final String SQL_INSERT = "INSERT INTO TODOLIST (NAME, DESCRIPTION, DEADLINE, PRIORITY) VALUES (?, ?, ?, ?);";
    private static final String SQL_UPDATE = "UPDATE TODOLIST SET DESCRIPTION = ?, DEADLINE = ?, PRIORITY = ? WHERE NAME =  ?;";
    private static final String SQL_READ_WHERE = "SELECT * FROM TODOLIST WHERE NAME =  ?;";
    private static final String SQL_READ_ALL = "SELECT * FROM TODOLIST ORDER BY ?1 ?2;";
    private static final String SQL_READ_GROUPED = "SELECT DATE(deadline) AS DATE, " +
            "ARRAY_AGG(name) AS TASKS " +
            "FROM TODOLIST " +
            "GROUP BY DATE(deadline) " +
            "ORDER BY DATE DESC;";
    private static final String SQL_DELETE = "DELETE FROM TODOLIST WHERE NAME = ?;";
    private static final String SQL_DELETE_ALL = "DELETE FROM TODOLIST;";
    private static final String SQL_COMPLETED = "UPDATE TODOLIST SET STATUS = ? WHERE NAME = ?";


    private final Map<Command.Type, Consumer<Command>> EXECUTION_MAP;

    {
        EXECUTION_MAP = Map.of(
                Command.Type.CREATE, this::runAdd,
                Command.Type.DELETE_ALL, this::runDeleteAll,
                Command.Type.DELETE, this::runDelete,
                Command.Type.READ, this::runRead,
                Command.Type.READ_ALL, this::runReadAll,
                Command.Type.READ_GROUPED, this::runGrouped,
                Command.Type.UPDATE, this::runUpdate,
                Command.Type.COMPLETED, this::runCompleted
        );
    }

    private void runGrouped(Command command) {
        if (!Command.Type.READ_GROUPED.equals(command.getType())) {
            throw new IllegalArgumentException(command.getType().getName());
        }

        try (
                Connection connection = DriverManager.getConnection(URL);
                PreparedStatement statement = connection.prepareStatement(SQL_READ_GROUPED)
        ) {
            try (ResultSet resultSet = statement.executeQuery()) {
                var grouped = mapToGrouped(resultSet);
                print(grouped);
                System.out.printf("Run: [%s] successfully, read: [%s] rows %n", command.getType(), grouped.size());
            }
        } catch (SQLException e) {
            System.err.printf("%s error: %n-sqlState: %s%n-errorCode: %s%n-message: %s%n",
                    command.getType(), e.getSQLState(), e.getErrorCode(), e.getMessage());
        }

    }

    private Map<String, String> mapToGrouped(ResultSet resultSet) throws SQLException {
        Map<String, String> result = new LinkedHashMap<>();
        while (resultSet.next()) {
            result.put(resultSet.getString("DATE"), resultSet.getString("TASKS"));
        }
        return result;
    }

    void run(final Command command) {
        System.out.println("###### RUNNING COMMAND ######");
        Consumer<Command> commandConsumer = Optional.ofNullable(EXECUTION_MAP.get(command.getType()))
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("Command: [%s] not supported", command.getType())));

        commandConsumer.accept(command);
        System.out.println("###### FINISHED COMMAND ######\n");
    }

    private void runAdd(final Command command) {
        if (!Command.Type.CREATE.equals(command.getType())) {
            throw new IllegalArgumentException(command.getType().getName());
        }

        try (
                Connection connection = DriverManager.getConnection(URL);
                PreparedStatement statement = connection.prepareStatement(SQL_INSERT)
        ) {
            statement.setString(1, command.getToDoItem().getName());
            statement.setString(2, command.getToDoItem().getDescription());
            statement.setTimestamp(3, Timestamp.valueOf(command.getToDoItem().getDeadline()));
            statement.setInt(4, command.getToDoItem().getPriority());
            int count = statement.executeUpdate();
            System.out.printf("Run: [%s] successfully, created: [%s] rows %n", command.getType(), count);
        } catch (SQLException e) {
            System.err.printf("%s error: %n-sqlState: %s%n-errorCode: %s%n-message: %s%n",
                    command.getType(), e.getSQLState(), e.getErrorCode(), e.getMessage());
        }
    }
    private void runDeleteAll(final Command command) {
        if (!Command.Type.DELETE_ALL.equals(command.getType())) {
            throw new IllegalArgumentException(command.getType().getName());
        }

        try (
                Connection connection = DriverManager.getConnection(URL);
                PreparedStatement statement = connection.prepareStatement(SQL_DELETE_ALL)
        ) {
            int count = statement.executeUpdate();
            System.out.printf("Run: [%s] successfully, deleted: [%s] rows %n", command.getType(), count);
        } catch (SQLException e) {
            System.err.printf("%s error: %n-sqlState: %s%n-errorCode: %s%n-message: %s%n",
                    command.getType(), e.getSQLState(), e.getErrorCode(), e.getMessage());
        }
    }
    private void runUpdate(final Command command) {
        if (!Command.Type.UPDATE.equals(command.getType())) {
            throw new IllegalArgumentException(command.getType().getName());
        }

        try (
                Connection connection = DriverManager.getConnection(URL);
                PreparedStatement statement = connection.prepareStatement(SQL_UPDATE)
        ) {
            statement.setString(1, command.getToDoItem().getDescription());
            statement.setTimestamp(2, Timestamp.valueOf(command.getToDoItem().getDeadline()));
            statement.setInt(3, command.getToDoItem().getPriority());
            statement.setString(4, command.getToDoItem().getName());
            int count = statement.executeUpdate();
            System.out.printf("Run: [%s] successfully, updated: [%s] rows %n", command.getType(), count);
        } catch (SQLException e) {
            System.err.printf("%s error: %n-sqlState: %s%n-errorCode: %s%n-message: %s%n",
                    command.getType(), e.getSQLState(), e.getErrorCode(), e.getMessage());
        }
    }
    private void runRead(final Command command) {
        if (!Command.Type.READ.equals(command.getType())) {
            throw new IllegalArgumentException(command.getType().getName());
        }

        try (
                Connection connection = DriverManager.getConnection(URL);
                PreparedStatement statement = connection.prepareStatement(SQL_READ_WHERE)
        ) {
            statement.setString(1, command.getToDoItem().getName());
            try (ResultSet resultSet = statement.executeQuery()) {
                List<ToDoItem> readItems = mapToToDoItems(resultSet);
                print(readItems);
                System.out.printf("Run: [%s] successfully, read: [%s] rows %n", command.getType(), readItems.size());
            }
        } catch (SQLException e) {
            System.err.printf("%s error: %n-sqlState: %s%n-errorCode: %s%n-message: %s%n",
                    command.getType(), e.getSQLState(), e.getErrorCode(), e.getMessage());
        }
    }

    private void print(List<ToDoItem> readItems) {
        System.out.println("PRINTING TO DO LIST");
        String schema = "%-25s%-25s%-25s%-25s%-25s%n";
        System.out.printf(schema,
                ToDoItem.Field.NAME,
                ToDoItem.Field.DESCRIPTION.name(),
                ToDoItem.Field.DEADLINE.name(),
                ToDoItem.Field.PRIORITY.name(),
                ToDoItem.Field.STATUS.name());

        readItems.forEach(entry -> System.out.printf(schema,
                entry.getName(),
                entry.getDescription(),
                entry.getDeadline(),
                entry.getPriority(),
                entry.getStatus()));
    }

    private void print(Map<String, String> grouped) {
        System.out.println("READ GROUPED");
        String schema = "%-25s%-25s%n";
        System.out.printf(schema, "DATE", "TASKS");
        for (Map.Entry<String, String> entry : grouped.entrySet()) {
            System.out.printf(schema, entry.getKey(), entry.getValue());
        }
    }

    private List<ToDoItem> mapToToDoItems(ResultSet resultSet) throws SQLException {
        List<ToDoItem> result = new ArrayList<>();
        while (resultSet.next()) {
            ToDoItem toDoItem = new ToDoItem();
            toDoItem.setName(resultSet.getString(ToDoItem.Field.NAME.name()));
            toDoItem.setDescription(resultSet.getString(ToDoItem.Field.DESCRIPTION.name()));
            toDoItem.setDeadline(resultSet.getTimestamp(ToDoItem.Field.DEADLINE.name()).toLocalDateTime());
            toDoItem.setPriority(resultSet.getInt(ToDoItem.Field.PRIORITY.name()));
            toDoItem.setStatus(ToDoItem.Status.valueOf(resultSet.getString(ToDoItem.Field.STATUS.name())));
            result.add(toDoItem);
        }
        return result;
    }

    private void runReadAll(final Command command) {
        if (!Command.Type.READ_ALL.equals(command.getType())) {
            throw new IllegalArgumentException(command.getType().getName());
        }

        try (
                Connection connection = DriverManager.getConnection(URL);
                PreparedStatement statement = connection.prepareStatement(SQL_READ_ALL
                        .replace("?1", command.getSortBy().name())
                        .replace("?2", command.getSortDir().name()))
        ) {
            try (ResultSet resultSet = statement.executeQuery()) {
                List<ToDoItem> readItems = mapToToDoItems(resultSet);
                print(readItems);
                System.out.printf("Run: [%s] successfully, read: [%s] rows %n", command.getType(), readItems.size());
            }
        } catch (SQLException e) {
            System.err.printf("%s error: %n-sqlState: %s%n-errorCode: %s%n-message: %s%n",
                    command.getType(), e.getSQLState(), e.getErrorCode(), e.getMessage());
        }
    }

    private void runDelete(final Command command) {
        if (!Command.Type.DELETE.equals(command.getType())) {
            throw new IllegalArgumentException(command.getType().getName());
        }

        try (
                Connection connection = DriverManager.getConnection(URL);
                PreparedStatement statement = connection.prepareStatement(SQL_DELETE)
        ) {
            statement.setString(1, command.getToDoItem().getName());
            int count = statement.executeUpdate();
            System.out.printf("Run: [%s] successfully, deleted: [%s] rows %n", command.getType(), count);
        } catch (SQLException e) {
            System.err.printf("%s error: %n-sqlState: %s%n-errorCode: %s%n-message: %s%n",
                    command.getType(), e.getSQLState(), e.getErrorCode(), e.getMessage());
        }
    }
    private void runCompleted(final Command command) {
        if (!Command.Type.COMPLETED.equals(command.getType())) {
            throw new IllegalArgumentException(command.getType().getName());
        }

        try (
                Connection connection = DriverManager.getConnection(URL);
                PreparedStatement statement = connection.prepareStatement(SQL_COMPLETED)
        ) {
            statement.setString(1, command.getToDoItem().getStatus().name());
            statement.setString(2, command.getToDoItem().getName());
            int count = statement.executeUpdate();
            System.out.printf("Run: [%s] successfully, deleted: [%s] rows %n", command.getType(), count);
        } catch (SQLException e) {
            System.err.printf("%s error: %n-sqlState: %s%n-errorCode: %s%n-message: %s%n",
                    command.getType(), e.getSQLState(), e.getErrorCode(), e.getMessage());
        }
    }
}
