package org.Database;

import java.util.Map;
import java.util.HashMap;

public class Row {

    private Map<Column, Object> values = new HashMap<>();

    public Row(Map<Column, Object> values) {
        this.values = values;
    }

    public Row() {
    }

    public Map<Column, Object> getValues() {
        return values;
    }

    public void setValues(Map<Column, Object> values) {
        this.values = values;
    }

    public Object getValue(Column column) {
        if (!values.containsKey(column)) {
            throw new IllegalArgumentException("Column does not exist");
        }
        return values.get(column);
    }

    public Object getValue(String columnName) {
        for (Column column : values.keySet()) {
            if (column.getName().equals(columnName)) {
                return values.get(column);
            }
        }
        throw new IllegalArgumentException("Column does not exist");
    }

    public void addValue(Column column, Object value) {
        values.put(column, value);
    }

    public void modifyValue(Column column, Object value) {
        values.put(column, value);
    }
    public String[] getColumnNames() {
        return values.keySet().stream().map(Column::getName).toArray(String[]::new);
    }
}