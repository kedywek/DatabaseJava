package org.Database;

import java.util.Map;

public class Row {

    private Map<Column, Object> values;

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
        return values.get(column);
    }

    public void addValue(Column column, Object value) {
        values.put(column, value);
    }

    public void modifyValue(Column column, Object value) {
        values.put(column, value);
    }
}