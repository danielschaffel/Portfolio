package edu.yu.db.schaffel;

import java.util.ArrayList;

public class Entry
{
    Cell key;
    Table value;
    Node child;
    public Entry(Cell cell,Row row)
    {
        key = cell;
        value = new Table();
        value.add(row);
    }

    public Entry(Cell key,Row value,Node child)
    {
        this.key = key;
        this.value = new Table();
        this.value.add(value);
        this.child = child;
    }
    public Cell getKey()
    {
        return key;
    }

    public void setKey(Cell key)
    {
        this.key = key;
    }

    public Table getValue()
    {
        return value;
    }

    public void setValue(Row value)
    {
        this.value.add(value);
    }
}
