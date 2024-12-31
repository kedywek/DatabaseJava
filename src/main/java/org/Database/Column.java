package org.Database;

import java.util.Set;

public class Column{
    private String name;
    private String type;

    private static final Set<String> VALID_TYPES = Set.of("String", "Integer", "Float", "Double", "Boolean");


    public Column(String name, String type) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Column name cannot be null or empty");
        }
        if (isValidType(type)) {
            throw new IllegalArgumentException("Column type cannot be null or empty");
        }
        this.name = name;
        this.type = type;
    }

    static boolean isValidType(String type){
        return VALID_TYPES.contains(type) || type == null || type.isEmpty();
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Column name cannot be null or empty");
        }
        this.name = name;
    }

    public String getType(){
        return type;
    }

    public void setType(String type){
        if (isValidType(type)) {
            throw new IllegalArgumentException("Column type cannot be null or empty");
        }
        this.type = type;
    }
}