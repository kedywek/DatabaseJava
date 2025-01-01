package org.Database;

import java.util.List;
import java.util.Scanner;
import java.util.Vector;

public class InputController {

    enum Command {
        CREATE,
        DROP,
        ALTER,
        SELECT,
        INSERT,
        DELETE,
        UPDATE,
    }

    enum State {
        RUNNING,
        FINISHED
    }

    static Database database;

    static State state = State.RUNNING;

    static InputController.State getState() {
        return state;
    }

    static void setState(InputController.State state) {
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
        String[] commands = command.split(";");
        for (String commandPart : commands) {
            String[] commandParts = commandPart.split(" ");
            Command commandType = Command.valueOf(commandParts[0].toUpperCase());
            executeCommand(commandType, commandParts);
        }
    }

    static void executeCommand(Command commandType, String[] commandParts) {
        switch (commandType) {
            case CREATE:
                create(commandParts);
                break;
            case DROP:
                drop(commandParts);
                break;
            case ALTER:
                alter(commandParts);
                break;
            case SELECT:
                select(commandParts);
                break;
            case INSERT:
                insert(commandParts);
                break;
            case DELETE:
                delete(commandParts);
                break;
            case UPDATE:
                update(commandParts);
                break;
            default:
                throw new IllegalArgumentException("Invalid command");
        }
    }

    static void create(String[] commandParts) {
        switch (commandParts[1].toUpperCase()) {
            case "DATABASE":
                if (commandParts.length != 3) {
                    throw new IllegalArgumentException("Invalid command");
                }
                database = new Database(commandParts[2]);
                break;
            case "TABLE":
                List<String> columnNames = new Vector<>();
                List<String> columnTypes = new Vector<>();
                if (commandParts[3].equals("(*")) {
                    if (!commandParts[commandParts.length - 1].equals(")")) {
                        throw new IllegalArgumentException("Invalid command");
                    }
                    int i = 3;
                    while (!commandParts[i].equals(")")) {
                        while (!commandParts[i].equals(",")) {
                            i++;
                        }
                        columnNames.add(commandParts[i - 2]);
                        columnTypes.add(commandParts[i - 1]);
                        i++;
                    }
                }
                Table table = new Table(commandParts[2], columnNames.toArray(new String[0]), columnTypes.toArray(new String[0]));
                database.addTable(table);
                break;

            default:
                throw new IllegalArgumentException("Invalid command");
        }
    }

    static void drop(String[] commandParts) {
        if (commandParts.length != 3) {
            throw new IllegalArgumentException("Invalid command");
        }
        switch (commandParts[1]) {
            case "DATABASE":
                database = null;
                break;
            case "TABLE":
                database.removeTable(commandParts[2]);
                break;
            default:
                throw new IllegalArgumentException("Invalid command");
        }
    }

    static void alter(String[] commandParts) {
        if (commandParts.length != 6) {
            throw new IllegalArgumentException("Invalid command");
        }
        switch (commandParts[1]) {
            case "TABLE":
                Table table = database.getTable(commandParts[2]);
                switch (commandParts[3]) {
                    case "ADD":
                        Column column = new Column(commandParts[4], commandParts[5]);
                        table.addColumn(column);
                        break;
                    case "DROP":
                        table.removeColumn(commandParts[4]);
                        break;
                    case "MODIFY":
                        Column newColumn = new Column(commandParts[4], commandParts[5]);
                        table.modifyColumn(commandParts[4], newColumn);
                        break;
                    default:
                        throw new IllegalArgumentException("Invalid command");
                }
                break;
            default:
                throw new IllegalArgumentException("Invalid command");
        }
    }

    static String select(String[] commandParts) {
        if (commandParts.length < 4) {
            throw new IllegalArgumentException("Invalid command");
        }
        List<String> columns = new Vector<>();
        StringBuilder output = new StringBuilder();
        Table table;
        if (commandParts[1].equals("*")) {
            if (commandParts[2].equalsIgnoreCase("FROM")) {
                table = database.getTable(commandParts[3]);
                columns = table.getColumnNames();
            }
        }
        else{
            int i = 1;
            while (!commandParts[i].equals("FROM")) {
                columns.add(commandParts[i]);
                i++;
            }
            table = database.getTable(commandParts[i + 1]);
        }
        if(columns.isEmpty()){
            throw new IllegalArgumentException("Invalid command");
        }





        return output.toString();
    }

    
}

