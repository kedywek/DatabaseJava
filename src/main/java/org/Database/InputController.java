package org.Database;

import java.util.LinkedHashMap;
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
            String[] commandParts = commandPart.split(",\\s|\\s+");
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

                try {
                    if (commandParts[3].equals("(")) {
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
                } catch (ArrayIndexOutOfBoundsException e) {

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
        switch (commandParts[1].toUpperCase()) {
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
        switch (commandParts[1].toUpperCase()) {
            case "TABLE":
                Table table = database.getTable(commandParts[2]);
                switch (commandParts[3].toUpperCase()) {
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
            }else {
                throw new IllegalArgumentException("Invalid command");
            }
        }
        else{
            int i = 1;
            try{
                while (!commandParts[i].equalsIgnoreCase("FROM")){
                    columns.add(commandParts[i]);
                    i++;
                }
            }catch (ArrayIndexOutOfBoundsException e){
                throw new IllegalArgumentException("Invalid command");
            }
            try{
                table = database.getTable(commandParts[i + 1]);
            }catch (ArrayIndexOutOfBoundsException e){
                throw new IllegalArgumentException("Invalid command");
            }
        }
        if(columns.isEmpty()){
            throw new IllegalArgumentException("Invalid command");
        }


        for (int i = 4; i < commandParts.length; i++) {
            if (commandParts[i].equalsIgnoreCase("WHERE")) {
               Vector<String> condition = new Vector<>();
                for (int j = i + 1; j < commandParts.length; j++) {
                    condition.add(commandParts[j]);
                }
                return table.where(columns.toArray(new String[0]), condition.toArray(new String[0]));
            }
            if (commandParts[i].equalsIgnoreCase("JOIN")) {
                Vector<String> tableNames = new Vector<>();
                Vector<String> condition = new Vector<>();
                Table toJoin = null;
                i++;
                try{
                    toJoin = database.getTable(commandParts[i]);
                }
                catch (ArrayIndexOutOfBoundsException e){
                    throw new IllegalArgumentException("Invalid command");
                }
                i++;
                if (commandParts[i].equalsIgnoreCase("ON")) {
                    i++;
                    condition.add(commandParts[i]);
                    i++;
                    condition.add(commandParts[i]);
                    i++;
                    condition.add(commandParts[i]);
                    i++;
                }
                table = database.join(new String[]{table.getName(), toJoin.getName()}, condition.toArray(new String[0]));
            }
        }
        return output.toString();
    }
    static void insert(String[] commandParts) {
        if (commandParts.length < 5) {
            throw new IllegalArgumentException("Invalid command");
        }
        if (!commandParts[1].equalsIgnoreCase("INTO")) {
            throw new IllegalArgumentException("Invalid command");
        }
        Table table = database.getTable(commandParts[2]);
        LinkedHashMap<String, Column> columns;
        int i = 3;
        switch (commandParts[i].toUpperCase()){
            case "VALUES":
                columns = table.getColumns();
                i++;
                break;
            case "(":
                columns = new LinkedHashMap<>();
                for(i = 4; i < table.getColumnNames().size() + 4; i++){
                    if(commandParts[i].equals(")")){
                        break;
                    }
                   try{
                       columns.put(commandParts[i], table.getColumn(commandParts[i]));
                     }catch (IllegalArgumentException e){
                       throw new IllegalArgumentException("Invalid command");
                   }
                }
                i++;
                if (!commandParts[i].equalsIgnoreCase("VALUES")) {
                    throw new IllegalArgumentException("Invalid command");
                }
                i++;
                break;
            default:
                throw new IllegalArgumentException("Invalid command");
        }
        for (i = i; i< commandParts.length; i++){
            if (commandParts[i].equalsIgnoreCase("(")){
                try{
                    Row row = new Row();
                    while (!commandParts[i].equalsIgnoreCase(")")){
                        i++;
                        if (commandParts[i].equals(",")){
                            continue;
                        }
                        row.addValue(table.getColumn(table.getColumnNames().get(i)), commandParts[i]);
                    }
                    try{
                        table.addRow(row);
                    }
                    catch (IllegalArgumentException e){
                        throw new IllegalArgumentException("Invalid command");
                    }
                }
                catch (ArrayIndexOutOfBoundsException e){
                    throw new IllegalArgumentException("Invalid command");
                }
            }
        }
    }
    static void delete(String[] commandParts) {
        if (commandParts.length < 4) {
            throw new IllegalArgumentException("Invalid command");
        }
        if (!commandParts[1].equalsIgnoreCase("FROM")) {
            throw new IllegalArgumentException("Invalid command");
        }
        Table table = database.getTable(commandParts[2]);
        if (!commandParts[3].equalsIgnoreCase("WHERE")) {
            throw new IllegalArgumentException("Invalid command");
        }
        Vector<String> condition = new Vector<>();
        for (int i = 4; i < commandParts.length; i++) {
            condition.add(commandParts[i]);
        }
        table.delete(condition.toArray(new String[0]));
    }

    static void update(String[] commandParts) {
        if (commandParts.length < 6) {
            throw new IllegalArgumentException("Invalid command");
        }
        Table table;
        try{
            table = database.getTable(commandParts[1]);
        }
        catch (ArrayIndexOutOfBoundsException e){
            throw new IllegalArgumentException("Invalid command");
        }
        if (!commandParts[2].equalsIgnoreCase("SET")) {
            throw new IllegalArgumentException("Invalid command");
        }
        Vector<String> columns = new Vector<>();
        Vector<String> values = new Vector<>();
        int i = 3;
        while (!commandParts[i].equalsIgnoreCase("WHERE")) {
            while(!commandParts[i].equals(",")){
                i++;
            }
            try{
                columns.add(commandParts[i - 3]);
                values.add(commandParts[i-1]);
            }
            catch (IllegalArgumentException e){
                throw new IllegalArgumentException("Invalid command");
            }
        }
        Vector<String> condition = new Vector<>();
        for (i = i + 1; i < commandParts.length; i++) {
            condition.add(commandParts[i]);
        }
        try{
            table.update(columns.toArray(new String[0]), values.toArray(new String[0]), condition.toArray(new String[0]));
        }
        catch (IllegalArgumentException e){
            throw new IllegalArgumentException("Invalid command");
        }
    }
}

