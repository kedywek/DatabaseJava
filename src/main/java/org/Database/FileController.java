package org.Database;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.io.FileWriter;

import java.io.File;
import java.util.List;
import java.util.Set;


public class FileController {

    static void saveFile(Database database) {
        JSONObject jsonDatabase = new JSONObject();
        jsonDatabase.put("name", database.getName());

        JSONArray jsonTables = new JSONArray();
        for (Table table : database.getTables()) {
            JSONObject jsonTable = new JSONObject();
            jsonTable.put("name", table.getName());

            JSONArray jsonColumns = new JSONArray();
            for (Column column : table.getColumns()) {
                JSONObject jsonColumn = new JSONObject();
                jsonColumn.put("name", column.getName());
                jsonColumn.put("type", column.getType());
                jsonColumns.put(jsonColumn);
            }
            jsonTable.put("columns", jsonColumns);

            JSONArray jsonRows = new JSONArray();
            for (Row row : table.getRows()) {
                JSONObject jsonRow = new JSONObject();
                jsonRow.put("columnNames", row.getColumnNames());

                JSONObject jsonValues = new JSONObject();
                for (Column column : table.getColumns()) {
                    jsonValues.put(column.getName(), row.getValue(column.getName()));
                }
                jsonRow.put("values", jsonValues);
                jsonRows.put(jsonRow);
            }
            jsonTable.put("rows", jsonRows);

            jsonTables.put(jsonTable);
        }
        jsonDatabase.put("tables", jsonTables);

        try (FileWriter file = new FileWriter(database.getName() + ".json")) {
            file.write(jsonDatabase.toString());
            System.out.println("Successfully saved to " + database.getName() + ".json");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static Database loadFile(String name) {
        File file = new File(name + ".json");
        if (!file.exists()) {
            throw new IllegalArgumentException("File does not exist");
        }
        try {
            String content = new String(java.nio.file.Files.readAllBytes(file.toPath()));
            JSONObject jsonObject = new JSONObject(content);
            Database database = new Database(jsonObject.getString("name"));
            for (Object tableJSON : jsonObject.getJSONArray("tables")) {
                Table table = new Table(((JSONObject) tableJSON).getString("name"));
                for (Object columnJSON : ((JSONObject) tableJSON).getJSONArray("columns")) {
                    JSONObject jsonColumn = (JSONObject) columnJSON;
                    Column column = new Column(jsonColumn.getString("name"), jsonColumn.getString("type"));
                    table.addColumn(column);
                }
                for (Object rowJSON : ((JSONObject) tableJSON).getJSONArray("rows")) {
                    JSONObject jsonRow = (JSONObject) rowJSON;
                    Row row = new Row();
                    for (Object columnName : jsonRow.getJSONArray("columnNames").toList()) {
                        row.addValue(table.getColumn((String) columnName), jsonRow.getJSONObject("values").get((String) columnName));
                    }
                    table.addRow(row);
                }
                database.addTable(table);
            }
            System.out.println("Successfully loaded from " + name + ".json");
            return database;
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid file format");
        }
    }

}
