package org.Database;

import java.io.File;
import java.util.Map;
import java.util.Set;

public class Database {


    private String name;
    private Map<String, Table> tables;


    public Database(String name){
        this.name = name;
    }
    public String getName(){
        return name;
    }

    public void setName(){
        this.name = name;
    }

    public void addTable(Table table){
        tables.put(table.getName(), table);
    }

    public void removeTable(String name){
        tables.remove(name);
    }

    public Table getTable(String name){
        return tables.get(name);
    }

    public Set<String> getTableNames(){
        return tables.keySet();
    }

    public void modifyTable(String name, Table table){
        tables.put(name, table);
    }

    public void modifyTableName(String oldName, String newName){
        Table table = tables.get(oldName);
        tables.remove(oldName);
        tables.put(newName, table);
    }

    public void saveDatabase(File file){
        //TODO
    }

    public void setName(String name){
        this.name = name;
    }






}