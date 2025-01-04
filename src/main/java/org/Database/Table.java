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
                String value = row.getValue(columns.get(columnName)).toString();
                if (value != null) {
                    result.append(value).append(" ");
                }
                else {
                    result.append(" ").append(" ");
                }
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
            boolean conditionMet = multipleConditionsMet(row, condition);
            if (conditionMet) {
                for (String columnName : columnNames) {
                    result.append(row.getValue(columns.get(columnName))).append(" ");
                }
                result.append("\n");
            }
        }
        return result.toString();
    }
    public void delete(String[] condition) {
        for (int i = 0; i < rows.size(); i++) {
            Row row = rows.get(i);
            boolean conditionMet = multipleConditionsMet(row, condition);
            if (conditionMet) {
                rows.remove(i);
                i--;
            }
        }
    }
    public void update(String[] columnNames,String[] values, String[] condition) {
        for (Row row : rows) {
            boolean conditionMet = multipleConditionsMet(row, condition);
            if (conditionMet) {
                for (int i = 0; i < columnNames.length; i++) {
                    if (!columns.containsKey(columnNames[i])) {
                        throw new IllegalArgumentException("Column does not exist");
                    }
                    row.addValue(columns.get(columnNames[i]), parseValue(values[i], columns.get(columnNames[i]).getType()));
                }
            }
        }
    }
    private boolean multipleConditionsMet(Row row, String[] conditions) {
        boolean result = true;
        for (int i = 3; i < conditions.length; i += 4) {
            try{
                if (conditions[i].equals("AND")) {
                    result = result && isConditionMet(row, new String[]{conditions[i - 3], conditions[i - 2], conditions[i - 1]});
                }
                else if (conditions[i].equals("OR")) {
                    result = result || isConditionMet(row, new String[]{conditions[i - 3], conditions[i - 2], conditions[i - 1]});
                }
                else {
                    throw new IllegalArgumentException("Invalid condition");
                }
            }
            catch (ArrayIndexOutOfBoundsException e){
                return result;
            }
        }
        return result;
    }
    private boolean isConditionMet(Row row, String[] condition) {
        Object value1 = row.getValue(columns.get(condition[0]));
        Object value2 = parseValue(condition[2], columns.get(condition[0]).getType());

        switch (condition[1]) {
            case "=":
                return value1.equals(value2);
            case "!=":
                return !value1.equals(value2);
            case ">":
                return compareValues(value1, value2) > 0;
            case "<":
                return compareValues(value1, value2) < 0;
            case ">=":
                return compareValues(value1, value2) >= 0;
            case "<=":
                return compareValues(value1, value2) <= 0;
            default:
                throw new IllegalArgumentException("Invalid condition");
        }
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

