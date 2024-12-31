package org.Database;

import java.util.List;

public class Row{

    private List<String> values;

    public Row(List<String> values){
        this.values = values;
    }

    public List<String> getValues(){
        return values;
    }

    public void setValues(List<String> values){
        this.values = values;
    }

    public String getValue(int index){
        return values.get(index);
    }

    public void addValue(String value){
        values.add(value);
    }

    public void modifyValue(int index, String value){
        values.set(index, value);
    }

}