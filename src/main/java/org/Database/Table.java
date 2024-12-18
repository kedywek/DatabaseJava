package org.Database;

import java.util.List;

public class Table {
    private String name;
    private List<Column> columns;
    private List<Row> rows;

    public Table(String name, List<Column> columns, List<Row> rows) {
        this.name = name;
        this.columns = columns;
        this.rows = rows;
    }

    public Table(String name, List<Column> columns) {
        this(name, columns, null);
    }

    public String getName() {
        return name;
    }

    public List<Column> getColumns() {
        return columns;
    }

    public List<Row> getRows() {
        return rows;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public void setColumns(List<Column> columns) {
        this.columns = columns;
    }

    public void setRows(List<Row> rows) {
        this.rows = rows;
    }
    public void addRow(Row row){
        rows.add(row);
    }
    public void removeRow(int index){
        rows.remove(index);
    }
    public void addColumn(Column column){
        columns.add(column);
    }
    public void removeColumn(int index){}
}

