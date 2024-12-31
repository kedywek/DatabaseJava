package org.Database;

import java.util.ArrayList;
import java.util.List;
import java.util.LinkedHashMap;

public class Table {
    private String name;
    private LinkedHashMap<String, Column> columns;
    private List<Row> rows;

    public Table(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addColumn(Column column) {
        columns.put(column.getName(), column);
    }

    public void removeColumn(String name) {
        columns.remove(name);
    }

    public Column getColumn(String name) {
        return columns.get(name);
    }

    public List<String> getColumnNames() {
        return List.copyOf(columns.keySet());
    }

    public LinkedHashMap<String, Column> getColumns() {
        return columns;
    }

    public void modifyColumn(String name, Column column) {
        columns.put(name, column);
    }

    public void modifyColumnName(String oldName, String newName) {
        Column column = columns.get(oldName);
        columns.remove(oldName);
        columns.put(newName, column);
    }

    public Row getRow(int index) {
        return rows.get(index);
    }

    public List<Row> getRows() {
        return rows;
    }

    public void addRow(Row row) {
        rows.add(row);
    }

    public void removeRow(int index) {
        rows.remove(index);
    }

    public void modifyRow(int index, Row row) {
        rows.set(index, row);
    }

    public void saveTable() {
        //TODO
    }
}

