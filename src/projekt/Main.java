package projekt;

import zajavka.Counter;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class Main {

    public static void main(String[] args) {

        String address = "jdbc:postgresql://127.0.0.1:5432/todo_list";
        List<Row> rowList = new ArrayList<>();
        List<String> commandListString = List.of(
                "DELETE ALL;",
                "CREATE;NAME=TASK1;DESCRIPTION=SOME DESCRIPTION1;DEADLINE=11.02.2021 20:10;PRIORITY=0",
                "CREATE;NAME=TASK2;DESCRIPTION=SOME DESCRIPTION2;DEADLINE=12.02.2021 20:10;PRIORITY=1",
                "CREATE;NAME=TASK3;DESCRIPTION=SOME DESCRIPTION3;DEADLINE=13.02.2021 20:10;PRIORITY=2",
                "CREATE;NAME=TASK4;DESCRIPTION=SOME DESCRIPTION4;DEADLINE=14.02.2021 20:10;PRIORITY=3",
                "CREATE;NAME=TASK5;DESCRIPTION=SOME DESCRIPTION5;DEADLINE=15.02.2021 20:10;PRIORITY=4",
                "UPDATE;NAME=TASK3;DESCRIPTION=SOME NEW DESCRIPTION;DEADLINE=14.02.2021 20:10;PRIORITY=10",
                "READ;NAME=TASK1",
                "READ ALL;",
                "DELETE;NAME=TASK4",
                "READ ALL;SORT=PRIORITY,ASC",
                "READ ALL;SORT=PRIORITY,DESC",
                "DELETE ALL;"
        );

        Counter counter = new Counter(0);
        for (String command : commandListString) {
            try (
                    Connection connection = DriverManager.getConnection(address);
                    PreparedStatement statement = connection.prepareStatement(
                            String.valueOf(whatTypeOfCommandItIS(command)).
                                    substring(String.valueOf(whatTypeOfCommandItIS(command)).indexOf("[") + 1,
                                            String.valueOf(whatTypeOfCommandItIS(command)).length() - 1))
            ) {
                whatDoWeDoWithOurCommand(command, statement, counter, rowList);

            } catch (SQLException e) {
                System.err.printf("Error occurred during connection %nsqlState: %s%nerrorCode: %s%nmessage: %s%n",
                        e.getSQLState(), e.getErrorCode(), e.getMessage());
            }
        }
    }

    private static Optional<String> whatTypeOfCommandItIS(String s) {
        String[] split = s.split(";");
        String commandType = split[0];
        switch (commandType) {
            case "CREATE":
                return Optional.of("INSERT INTO TODO_TABLE (ID, NAME, DESCRIPTION, DEADLINE, PRIORITY) VALUES (?, ?, ?, ?, ?)");
            case "UPDATE":
                return Optional.of("UPDATE TODO_TABLE SET DESCRIPTION = ?, DEADLINE = ?, PRIORITY = ? WHERE NAME =  ?");
            case "READ":
                return Optional.of("SELECT * FROM TODO_TABLE WHERE NAME = ?");
            case "READ ALL":
                return Optional.of("SELECT * FROM TODO_TABLE");
            case "DELETE":
                return Optional.of("DELETE FROM TODO_TABLE WHERE NAME = ?");
            case "DELETE ALL":
                return Optional.of("DELETE FROM TODO_TABLE");
            default:
                System.err.println("Wrong command u idiot, try again please");
                break;
        }
        return Optional.empty();
    }

    private static void whatDoWeDoWithOurCommand(String s, PreparedStatement statement, Counter counter, List<Row> rowList) throws SQLException {
        String[] split = s.split(";");
        String commandType = split[0];
        switch (commandType) {
            case "CREATE" -> {
                counter.setCounter(counter.getCounter() + 1);
                createCommand(split, statement, counter.getCounter(), rowList);
            }
            case "UPDATE" -> updateCommand(split, statement, rowList);
            case "READ" -> readSpecificTask(split, rowList);
            case "READ ALL" -> readAllTasks(split, rowList);
            case "DELETE" -> deleteCommand(split, statement, rowList);
            case "DELETE ALL" -> deleteAllCommand(statement, rowList);
        }
    }

    private static void readSpecificTask(String[] split, List<Row> rowList) {
        System.out.print(split[1].substring(split[1].indexOf("=") + 1) + ": -- ");
        for (Row row : rowList) {
            if (row.getName().equals(split[1].substring(split[1].indexOf("=") + 1))) {
                System.out.println(row);
            }
        }
    }

    private static void deleteAllCommand(PreparedStatement statement, List<Row> rowList) throws SQLException {
        statement.executeUpdate();
        System.out.println("Delete: " + rowList.size() + " rows");
        rowList.clear();
    }

    private static void readAllTasks(String[] split, List<Row> rowList) {
        if (split.length == 1) {
            System.out.println("ID  |  NAME  |  DESCRIPTION  |  DEADLINE  |  PRIORITY");
            for (Row row : rowList) {
                System.out.println(row);
            }
        } else if (split[1].startsWith("SORT")) {
            String sort = split[1].substring(split[1].indexOf("=") + 1);
            String sortBy = sort.split(",")[0];
            String ascOrDesc = sort.split(",")[1];

            Comparator<Row> comparatorId = Comparator.comparing(Row::getId);
            Comparator<Row> comparatorName = Comparator.comparing(Row::getName);
            Comparator<Row> comparatorDescription = Comparator.comparing(Row::getDescription);
            Comparator<Row> comparatorDeadline = Comparator.comparing(Row::getDeadline);
            Comparator<Row> comparatorPriority = Comparator.comparing(Row::getPriority);

            Comparator<Row> comparator;
            if (sortBy.equals(Row.rowName.ID.name())) {
                comparator = comparatorId;
            } else if (sortBy.equals(Row.rowName.NAME.name())) {
                comparator = comparatorName;
            } else if (sortBy.equals(Row.rowName.DESCRIPTION.name())) {
                comparator = comparatorDescription;
            } else if (sortBy.equals(Row.rowName.DEADLINE.name())) {
                comparator = comparatorDeadline;
            } else if (sortBy.equals(Row.rowName.PRIORITY.name())) {
                comparator = comparatorPriority;
            } else {
                throw new IllegalArgumentException("wrong type of SORT: " + split[1].substring(4));
            }
            if (ascOrDesc.equals("ASC")) {
                rowList.sort(comparator);
                System.out.println("ID  |  NAME  |  DESCRIPTION  |  DEADLINE  |  PRIORITY   |   " + split[1]);
                for (Row row : rowList) {
                    System.out.println(row);
                }
            } else if (ascOrDesc.equals("DESC")) {
                rowList.sort(comparator.reversed());
                System.out.println("ID  |  NAME  |  DESCRIPTION  |  DEADLINE  |  PRIORITY   |   " + split[1]);
                for (Row row : rowList) {
                    System.out.println(row);
                }
            } else {
                throw new IllegalArgumentException("wrong type of SORT: " + split[1].substring(4));
            }

        } else {
            throw new IllegalArgumentException("wrong type of SORT, sort pattern: \"SORT=sortBy,ACS/DESC\" you type: " +
                    split[1]);
        }
    }

    private static void deleteCommand(String[] split, PreparedStatement statement, List<Row> rowList) throws SQLException {
        String nameOfTaskWeWantToDelete = split[1].substring(split[1].indexOf("=") + 1);
        statement.setString(1, nameOfTaskWeWantToDelete);
        int i = statement.executeUpdate();
        rowList.removeIf(row -> row.getName().equals(nameOfTaskWeWantToDelete));
        System.out.println("Delete: " + i + " rows");
    }

    private static void updateCommand(String[] split, PreparedStatement statement, List<Row> rowList) throws SQLException {
        String nameOfTaskWeWantToChange = split[1].substring(split[1].indexOf("=") + 1);
        String newDescription = split[2].substring(split[2].indexOf("=") + 1);
        String newDeadlineString = split[3].substring(split[3].indexOf("=") + 1);
        int newPriority = Integer.parseInt(split[4].substring(split[4].indexOf("=") + 1));
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        LocalDateTime newDeadlineDateTime = LocalDateTime.parse(newDeadlineString, dateTimeFormatter);

        statement.setString(1, newDescription);
        statement.setTimestamp(2, Timestamp.valueOf(newDeadlineDateTime));
        statement.setInt(3, newPriority);
        statement.setString(4, nameOfTaskWeWantToChange);
        int i = statement.executeUpdate();
        for (Row row : rowList) {
            if (row.getName().equals(nameOfTaskWeWantToChange)) {
                row.setName(nameOfTaskWeWantToChange);
                row.setDescription(newDescription);
                row.setDeadline(newDeadlineDateTime);
                row.setPriority(newPriority);
            }
        }
        System.out.println("Update: " + i + " rows");
    }

    private static void createCommand(String[] split, PreparedStatement statement, int counterKeyID, List<Row> rowList) throws SQLException {
        String name = split[1].substring(split[1].indexOf("=") + 1);
        String description = split[2].substring(split[2].indexOf("=") + 1);
        String deadlineString = split[3].substring(split[3].indexOf("=") + 1);
        int priority = Integer.parseInt(split[4].substring(split[4].indexOf("=") + 1));
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        LocalDateTime deadlineDateTime = LocalDateTime.parse(deadlineString, dateTimeFormatter);

        statement.setInt(1, counterKeyID);
        statement.setString(2, name);
        statement.setString(3, description);
        statement.setTimestamp(4, Timestamp.valueOf(deadlineDateTime));
        statement.setInt(5, priority);
        int i = statement.executeUpdate();
        rowList.add(new Row(counterKeyID, name, description, deadlineDateTime, priority));
        System.out.println("Create: " + i + " rows");
    }
}

//-- DROP TABLE TODO_TABLE
//
//        -- CREATE TABLE TODO_TABLE(
//        -- 	ID INT NOT NULL,
//        -- 	NAME VARCHAR(100) NOT NULL,
//        -- 	DESCRIPTION TEXT NOT NULL,
//        -- 	DEADLINE TIMESTAMP NOT NULL,
//        -- 	PRIORITY INT CHECK(PRIORITY IN (0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10)) NOT NULL,
//        -- 	UNIQUE(NAME),
//        -- 	PRIMARY KEY(ID)
//        -- 	);
//
//        -- select * from todo_table
//        -- DELETE FROM TODO_TABLE WHERE ID IN (1, 2, 3, 4, 5)
//        -- DELETE FROM TODO_TABLE