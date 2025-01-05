package org.Database;

import java.util.*;

public class InputController {

    enum Command {
        CREATE,
        DROP,
        ALTER,
        SELECT,
        INSERT,
        DELETE,
        UPDATE,
        SAVE,
        LOAD
    }

    enum State {
        RUNNING,
        FINISHED
    }

    static Database database;

    static State state = State.RUNNING;



    static State getState() {
        return state;
    }

    static void setState(State state) {
        InputController.state = state;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (state == State.RUNNING) {
            String command = scanner.nextLine();
            processCommand(command);
        }
    }

    static void processCommand(String command) {
        if (command.equalsIgnoreCase("EXIT")) {
            state = State.FINISHED;
            return;
        }
        List<String> commands = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder currentCommand = new StringBuilder();
        for (char c : command.toCharArray()) {
            if (c == '\"' || c == '\'') {
                inQuotes = !inQuotes;
            }
            if (c == ';' && !inQuotes) {
                commands.add(currentCommand.toString());
                currentCommand.setLength(0);
            } else {
                currentCommand.append(c);
            }
        }
        if (currentCommand.length() > 0) {
            commands.add(currentCommand.toString());
        }
        for (String commandPart : commands) {
            List<String> commandParts = new ArrayList<>();
            inQuotes = false;
            currentCommand.setLength(0);
            for (char c : commandPart.toCharArray()) {
                if (c == '\"' || c == '\'') {
                    inQuotes = !inQuotes;
                }
                if (c == ' ' && !inQuotes) {
                    if (currentCommand.length() > 0) {
                        commandParts.add(currentCommand.toString());
                        currentCommand.setLength(0);
                    }
                } else {
                    currentCommand.append(c);
                }
            }
            if (currentCommand.length() > 0) {
                commandParts.add(currentCommand.toString());
            }
            Command commandType = Command.valueOf(commandParts.get(0).toUpperCase());
            executeCommand(commandType, commandParts.toArray(new String[0]));
        }
    }

    static void executeCommand(Command commandType, String[] commandParts) {
        switch (commandType) {
            case CREATE:
                try {
                    create(commandParts);
                } catch (IllegalArgumentException e) {
                    System.out.println("Invalid CREATE command: " + e.getMessage());
                }
                break;
            case DROP:
                try {
                    drop(commandParts);
                } catch (IllegalArgumentException e) {
                    System.out.println("Invalid DROP command: " + e.getMessage());
                }
                break;
            case ALTER:
                try {
                    alter(commandParts);
                } catch (IllegalArgumentException e) {
                    System.out.println("Invalid ALTER command: " + e.getMessage());
                }
                break;
            case SELECT:
                try {
                    System.out.println(select(commandParts));
                } catch (IllegalArgumentException e) {
                    System.out.println("Invalid SELECT command: " + e.getMessage());
                }
                break;
            case INSERT:
                try {
                    insert(commandParts);
                } catch (IllegalArgumentException e) {
                    System.out.println("Invalid INSERT command: " + e.getMessage());
                }
                break;
            case DELETE:
                try {
                    delete(commandParts);
                } catch (IllegalArgumentException e) {
                    System.out.println("Invalid DELETE command: " + e.getMessage());
                }
                break;
            case UPDATE:
                try {
                    update(commandParts);
                } catch (IllegalArgumentException e) {
                    System.out.println("Invalid UPDATE command: " + e.getMessage());
                }
                break;
            case SAVE:
                try {
                    FileController.saveFile(database);
                } catch (IllegalArgumentException e) {
                    System.out.println("Invalid SAVE command: " + e.getMessage());
                }
                break;
            case LOAD:
                try {
                    database = FileController.loadFile(commandParts[1]);
                    if (commandParts.length > 2) {
                        throw new IllegalArgumentException("Invalid LOAD command: " + commandParts[2]);
                    }
                } catch (IllegalArgumentException e) {
                    System.out.println("Invalid LOAD command: " + e.getMessage());
                }
                break;
            default:
                throw new IllegalArgumentException("Invalid command type: " + commandType);
        }
    }

    static void create(String[] commandParts) {
        switch (commandParts[1].toUpperCase()) {
            case "DATABASE":
                if (commandParts.length != 3) {
                    throw new IllegalArgumentException("Expected format: CREATE DATABASE <name>");
                }
                database = new Database(commandParts[2]);
                System.out.println("Database " + commandParts[2] + " created");
                break;
            case "TABLE":
                List<String> columnNames = new Vector<>();
                List<String> columnTypes = new Vector<>();

                try {
                    if (commandParts[3].equals("(")) {
                        if (!commandParts[commandParts.length - 1].equals(")")) {
                            throw new IllegalArgumentException("Expected closing parenthesis for column definitions");
                        }
                        int i = 3;
                        while (!commandParts[i].equals(")")) {
                            while (!commandParts[i].equals(",") && !commandParts[i].equals(")")) {
                                i++;
                            }
                            columnNames.add(commandParts[i - 2]);
                            columnTypes.add(commandParts[i - 1]);
                            i++;
                            if (commandParts[i - 1].equals(")")) {
                                break;
                            }
                        }
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    throw new IllegalArgumentException("Invalid column definition format");
                }
                Table table = new Table(commandParts[2], columnNames.toArray(new String[0]), columnTypes.toArray(new String[0]));
                try {
                    if (database == null) {
                        throw new IllegalArgumentException("No database selected");
                    }
                    else {
                        database.addTable(table);
                    }
                }
                catch (IllegalArgumentException e){
                    throw new IllegalArgumentException("Invalid CREATE command: " + e.getMessage());
                }
                System.out.println("Table " + commandParts[2] + " created");
                break;

            default:
                throw new IllegalArgumentException("Invalid CREATE command: " + commandParts[1]);
        }
    }

    static void drop(String[] commandParts) {
        if (commandParts.length != 3) {
            throw new IllegalArgumentException("Expected format: DROP <DATABASE|TABLE> <name>");
        }
        switch (commandParts[1].toUpperCase()) {
            case "DATABASE":
                database = null;
                System.out.println("Database " + commandParts[2] + " dropped");
                break;
            case "TABLE":
                database.removeTable(commandParts[2]);
                System.out.println("Table " + commandParts[2] + " dropped");
                break;
            default:
                throw new IllegalArgumentException("Invalid DROP command: " + commandParts[1]);
        }
    }

    static void alter(String[] commandParts) {
        if (commandParts.length != 6) {
            throw new IllegalArgumentException("Expected format: ALTER TABLE <name> <ADD|DROP|MODIFY> <column> <type>");
        }
        if (commandParts[1].equalsIgnoreCase("TABLE")) {
            Table table = database.getTable(commandParts[2]);
            switch (commandParts[3].toUpperCase()) {
                case "ADD":
                    Column column = new Column(commandParts[4], commandParts[5]);
                    table.addColumn(column);
                    System.out.println("Column " + commandParts[4] + " added to table " + commandParts[2]);
                    break;
                case "DROP":
                    table.removeColumn(commandParts[4]);
                    System.out.println("Column " + commandParts[4] + " dropped from table " + commandParts[2]);
                    break;
                case "MODIFY":
                    Column newColumn = new Column(commandParts[4], commandParts[5]);
                    table.modifyColumn(commandParts[4], newColumn);
                    System.out.println("Column " + commandParts[4] + " modified in table " + commandParts[2]);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid ALTER command: " + commandParts[3]);
            }
        } else {
            throw new IllegalArgumentException("Invalid ALTER command: " + commandParts[1]);
        }
    }

    static String select(String[] commandParts) {
        if (commandParts.length < 4) {
            throw new IllegalArgumentException("Expected format: SELECT <columns> FROM <table> [WHERE <condition>]");
        }
        List<String> columns = new Vector<>();
        Table table;
        if (commandParts[1].equals("*")) {
            if (commandParts[2].equalsIgnoreCase("FROM")) {
                table = database.getTable(commandParts[3]);
                columns = table.getColumnNames();
            } else {
                throw new IllegalArgumentException("Expected keyword FROM after columns");
            }
        } else {
            int i = 1;
            try {
                while (!commandParts[i].equalsIgnoreCase("FROM")) {
                    if (commandParts[i].equals(",")) {
                        i++;
                    }
                    columns.add(commandParts[i]);
                    i++;
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                throw new IllegalArgumentException("Invalid column list format");
            }
            try {
                table = database.getTable(commandParts[i + 1]);
            } catch (ArrayIndexOutOfBoundsException e) {
                throw new IllegalArgumentException("Expected table name after FROM");
            }
        }
        if (columns.isEmpty()) {
            throw new IllegalArgumentException("No columns specified for SELECT");
        }
        Vector<String> condition;
        for (int i = 4; i < commandParts.length; i++) {
            if (commandParts[i].equalsIgnoreCase("WHERE")) {
                condition = new Vector<>(Arrays.asList(commandParts).subList(i + 1, commandParts.length));
                try {
                    return table.where(columns.toArray(new String[0]), condition.toArray(new String[0]));
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("Invalid WHERE clause: " + e.getMessage());
                }
            }
            if (commandParts[i].equalsIgnoreCase("JOIN")) {
                condition = new Vector<>();
                Table toJoin;
                i++;
                try {
                    toJoin = database.getTable(commandParts[i]);
                } catch (ArrayIndexOutOfBoundsException e) {
                    throw new IllegalArgumentException("Expected table name after JOIN");
                }
                i++;
                if (commandParts[i].equalsIgnoreCase("ON")) {
                    i++;
                    condition.add(commandParts[i]);
                    i+=2;
                    condition.add(commandParts[i]);
                    i++;
                }
                table = database.join(new String[]{table.getName(), toJoin.getName()}, condition.toArray(new String[0]));
                if (commandParts[1].equals("*")) {
                    columns = table.getColumnNames();
                }
            }
        }
        return table.select(columns.toArray(new String[0]));
    }

    static void insert(String[] commandParts) {
        if (commandParts.length < 5) {
            throw new IllegalArgumentException("Expected format: INSERT INTO <table> (columns) VALUES (values)");
        }
        if (!commandParts[1].equalsIgnoreCase("INTO")) {
            throw new IllegalArgumentException("Expected keyword INTO after INSERT");
        }
        Table table = database.getTable(commandParts[2]);

        List<Column> columns;
        int i = 3;
        switch (commandParts[i].toUpperCase()) {
            case "VALUES":
                columns = table.getColumns();
                i++;
                break;
            case "(":
                columns = new Vector<>();
                for (i = 4; i < table.getColumnNames().size() + 4; i++) {
                    if (commandParts[i].equals(")")) {
                        break;
                    }
                    try {
                        columns.add(table.getColumn(commandParts[i]));
                    } catch (IllegalArgumentException e) {
                        throw new IllegalArgumentException("Invalid column name: " + commandParts[i]);
                    }
                }
                i++;
                if (!commandParts[i].equalsIgnoreCase("VALUES")) {
                    throw new IllegalArgumentException("Expected keyword VALUES after column list");
                }
                i++;
                break;
            default:
                throw new IllegalArgumentException("Invalid INSERT command format");
        }
        for (; i < commandParts.length; i++) {
            if (commandParts[i].equalsIgnoreCase("(")) {
                int j = 0;
                try {
                    List<String> values = new Vector<>();
                    i++;
                    while (!commandParts[i].equalsIgnoreCase(")")) {
                        if (commandParts[i].equals(",")) {
                            i++;
                        }
                        values.add(commandParts[i]);
                        j++;
                        i++;
                    }
                    try {
                        table.insert(columns.stream().map(Column::getName).toArray(String[]::new), values.toArray(new String[0]));
                    } catch (IllegalArgumentException e) {
                        throw new IllegalArgumentException("Invalid row data: " + e.getMessage());
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    throw new IllegalArgumentException("Invalid row data format");
                }
            }
        }
        System.out.println("Row inserted into table " + table.getName());
    }

    static void delete(String[] commandParts) {
        if (commandParts.length < 4) {
            throw new IllegalArgumentException("Expected format: DELETE FROM <table> WHERE <condition>");
        }
        if (!commandParts[1].equalsIgnoreCase("FROM")) {
            throw new IllegalArgumentException("Expected keyword FROM after DELETE");
        }
        Table table = database.getTable(commandParts[2]);
        if (!commandParts[3].equalsIgnoreCase("WHERE")) {
            throw new IllegalArgumentException("Expected keyword WHERE after table name");
        }
        Vector<String> condition = new Vector<>(Arrays.asList(commandParts).subList(4, commandParts.length));
        try {
            table.delete(condition.toArray(new String[0]));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid DELETE condition: " + e.getMessage());
        }
        System.out.println("Row deleted from table " + table.getName());
    }

    static void update(String[] commandParts) {
        if (commandParts.length < 6) {
            throw new IllegalArgumentException("Expected format: UPDATE <table> SET <column=value> WHERE <condition>");
        }
        Table table;
        try {
            table = database.getTable(commandParts[1]);
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new IllegalArgumentException("Expected table name after UPDATE");
        }
        if (!commandParts[2].equalsIgnoreCase("SET")) {
            throw new IllegalArgumentException("Expected keyword SET after table name");
        }
        Vector<String> columns = new Vector<>();
        Vector<String> values = new Vector<>();
        int i = 3;
        try {
            while (!commandParts[i].equalsIgnoreCase("WHERE")) {
                while (!commandParts[i].equals(",") && !commandParts[i].equalsIgnoreCase("WHERE")) {
                    i++;
                }
                columns.add(commandParts[i - 3]);
                values.add(commandParts[i - 1]);
                if (commandParts[i].equalsIgnoreCase("WHERE")) {
                    break;
                }
            }
        }
        catch (ArrayIndexOutOfBoundsException e) {
            throw new IllegalArgumentException("Invalid column=value list format");
        }

        Vector<String> condition = new Vector<>();
        for (i = i + 1; i < commandParts.length; i++) {
            condition.add(commandParts[i]);
        }
        try {
            table.update(columns.toArray(new String[0]), values.toArray(new String[0]), condition.toArray(new String[0]));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid UPDATE condition: " + e.getMessage());
        }
        System.out.println("Row updated in table " + table.getName());
    }
}