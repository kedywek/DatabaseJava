package org.Database;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.LinkedHashMap;

public class Table {
    private String name;
    private LinkedHashMap<String, Column> columns;
    private List<Row> rows;

    public Table(String name, String[] columnNames, String[] columnTypes) {
        if (columnNames.length != columnTypes.length) {
            throw new IllegalArgumentException("Number of column names and types must be equal");
        }
        this.name = name;
        columns = new LinkedHashMap<>();
        for (int i = 0; i < columnNames.length; i++) {
            columns.put(columnNames[i], new Column(columnNames[i], columnTypes[i]));
        }
    }
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
        if (!columns.containsKey(name)) {
            throw new IllegalArgumentException("Column does not exist");
        }
        columns.remove(name);
    }
    public boolean containsColumn(String name) {
        return columns.containsKey(name);
    }
    public Column getColumn(String name) {
        if (!columns.containsKey(name)) {
            throw new IllegalArgumentException("Column does not exist");
        }
        return columns.get(name);
    }

    public List<String> getColumnNames() {
        return List.copyOf(columns.keySet());
    }

    public LinkedHashMap<String, Column> getColumns() {
        return columns;
    }

    public void modifyColumn(String name, Column column) {
        if (!columns.containsKey(name)) {
            throw new IllegalArgumentException("Column does not exist");
        }
        columns.put(name, column);
    }

    public void modifyColumnName(String oldName, String newName) {
        if (!columns.containsKey(oldName)) {
            throw new IllegalArgumentException("Column does not exist");
        }
        Column column = columns.get(oldName);
        columns.remove(oldName);
        columns.put(newName, column);
    }
    public Table join(Table table, String condition) {
        //TODO
        return null;
    }

    public String select(String[] columnNames) {

        StringBuilder result = new StringBuilder();
        for (String columnName : columnNames) {
            if (!columns.containsKey(columnName)) {
                throw new IllegalArgumentException("Column does not exist");
            }
            result.append(columnName).append(" ");
        }
        result.append("\n");

        for (Row row : rows) {
            for (String columnName : columnNames) {
                result.append(row.getValue(columns.get(columnName))).append(" ");
            }
            result.append("\n");
        }
        return result.toString();
    }

    public String where(String[] columnNames, String[] condition) {
        StringBuilder result = new StringBuilder();
        for (String columnName : columnNames) {
            if (!columns.containsKey(columnName)) {
                throw new IllegalArgumentException("Column does not exist");
            }
            result.append(columnName).append(" ");
        }
        result.append("\n");
        for (Row row : rows) {
            Object value1 = row.getValue(columns.get(condition[0]));
            Object value2 = parseValue(condition[2], columns.get(condition[0]).getType());

            boolean conditionMet = false;
            switch (condition[1]) {
                case "=":
                    conditionMet = value1.equals(value2);
                    break;
                case "!=":
                    conditionMet = !value1.equals(value2);
                    break;
                case ">":
                    conditionMet = compareValues(value1, value2) > 0;
                    break;
                case "<":
                    conditionMet = compareValues(value1, value2) < 0;
                    break;
                case ">=":
                    conditionMet = compareValues(value1, value2) >= 0;
                    break;
                case "<=":
                    conditionMet = compareValues(value1, value2) <= 0;
                    break;
                default:
                    throw new IllegalArgumentException("Invalid condition");
            }

            if (conditionMet) {
                for (String columnName : columnNames) {
                    result.append(row.getValue(columns.get(columnName))).append(" ");
                }
                result.append("\n");
            }
        }
        return result.toString();
    }

    private Object parseValue(String value, String type) {
        switch (type) {
            case "INT":
                return Integer.parseInt(value);
            case "DOUBLE":
                return Double.parseDouble(value);
            case "BOOLEAN":
                return Boolean.parseBoolean(value);
            case "DATE":
                try {
                    return new SimpleDateFormat("yyyy-MM-dd").parse(value);
                } catch (ParseException e) {
                    throw new IllegalArgumentException("Invalid date format");
                }
            default:
                return value;
        }
    }

    private int compareValues(Object value1, Object value2) {
        if (value1 instanceof Comparable && value2 instanceof Comparable) {
            return ((Comparable) value1).compareTo(value2);
        }
        throw new IllegalArgumentException("Values are not comparable");
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

