package org.Database;

import java.util.Scanner;

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

    static State state = State.RUNNING;

    static InputController.State getState(){
        return state;
    }

    static void setState(InputController.State state){
        InputController.state = state;
    }

    static Database database;

    public static void main(String[] args) {
       Scanner scaner = new Scanner(System.in);

       while(state == State.RUNNING){
           String command = scaner.nextLine();
           processCommand(command, database);
       }
    }

    static void processCommand(String command, Database database){
        String[] commandParts = command.split(" ");
        Command commandType = Command.valueOf(commandParts[0].toUpperCase());
        switch (commandType){
            case CREATE:
                createCommand(commandParts, database);
                break;
            case DROP:
                dropCommand(commandParts, database);
                break;
            case ALTER:
                alterCommand(commandParts, database);
                break;
            case SELECT:
                selectCommand(commandParts, database);
                break;
            case INSERT:
                insertCommand(commandParts, database);
                break;
            case DELETE:
                deleteCommand(commandParts, database);
                break;
            case UPDATE:
                updateCommand(commandParts, database);
                break;
        }
    }

    static void createCommand(String[] commandParts, Database database){
        if(commandParts.length < 3){
            System.out.println("Invalid command");
            return;
        }
        String type = commandParts[1];
        switch (type){
            case "DATABASE":
                database = new Database(commandParts[2]);
                if (commandParts.length > 3){
                    throw new IllegalArgumentException("Invalid command");
                }
                break;
            case "TABLE":
                if (database == null){
                    throw new IllegalArgumentException("No database selected");
                }
                Table table = new Table(commandParts[2]);
                database.addTable(table);
                break;
        }
    }
    static void dropCommand(String[] commandParts, Database database){
        if(commandParts.length < 3){
            System.out.println("Invalid command");
            return;
        }
        String type = commandParts[1];
        switch (type){
            case "DATABASE":
                database = null;
                break;
            case "TABLE":
                if (database == null){
                    throw new IllegalArgumentException("No database selected");
                }
                database.removeTable(commandParts[2]);
                break;
        }
    }

}
