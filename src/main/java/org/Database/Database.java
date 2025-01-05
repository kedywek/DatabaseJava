package org.Database;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Database {


    private String name;
    private Map<String, Table> tables = new HashMap<>();

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
        if (tables.containsKey(table.getName())) {
            throw new IllegalArgumentException("Table already exists");
        }
        tables.put(table.getName(), table);
    }

    public void removeTable(String name){
        tables.remove(name);
    }
    public Table[] getTables(){
        return tables.values().toArray(new Table[0]);
    }
    public Table getTable(String name){
        if (!tables.containsKey(name)) {
            throw new IllegalArgumentException("Table does not exist");
        }
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

    public Table join(String[] tableNames, String[] condition){
        Table result = new Table(tableNames[0] + "_" + tableNames[1]);
        String[] leftPartOfCondition = condition[0].split("\\.");
        String[] rightPartOfCondition = condition[1].split("\\.");
        Table[] tables = new Table[2];
        tables[0] = getTable(tableNames[0]);
        tables[1] = getTable(tableNames[1]);
        Column[] columnsToMerge = new Column[2];
        if (leftPartOfCondition[0].equals(tableNames[0])) {
            columnsToMerge[0] = tables[0].getColumn(leftPartOfCondition[1]);
            if (rightPartOfCondition[0].equals(tableNames[1])) {
                columnsToMerge[1] = tables[1].getColumn(rightPartOfCondition[1]);
            }
            else {
                throw new IllegalArgumentException("Invalid condition");
            }
        }
        else if(rightPartOfCondition[0].equals(tableNames[0])){
            columnsToMerge[0] = tables[0].getColumn(rightPartOfCondition[1]);
            if (leftPartOfCondition[0].equals(tableNames[1])) {
                columnsToMerge[1] = tables[1].getColumn(leftPartOfCondition[1]);
            }
            else {
                throw new IllegalArgumentException("Invalid condition");
            }
        }
        else{
            throw new IllegalArgumentException("Invalid condition");
        }
        for (Column column : tables[0].getColumns()) {
            result.addColumn(column);
        }
        for (Column column : tables[1].getColumns()) {
            result.addColumn(column);
        }
        for (Row row1 : tables[0].getRows()) {
            for (Row row2 : tables[1].getRows()) {
                if (row1.getValue(columnsToMerge[0]).equals(row2.getValue(columnsToMerge[1]))) {
                    Row newRow = new Row();
                    for (Column column : tables[0].getColumns()) {
                        newRow.addValue(column, row1.getValue(column));
                    }
                    for (Column column : tables[1].getColumns()) {
                        newRow.addValue(column, row2.getValue(column));
                    }
                    result.addRow(newRow);
                }
            }
        }
        return result;
    }

    public void loadDatabase(File file){
        //TODO
    }

    public void dropDatabase(){
        //TODO
    }

    public void setName(String name){
        this.name = name;
    }






}