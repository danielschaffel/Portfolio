package edu.yu.db.schaffel;

import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.*;
import net.sf.jsqlparser.JSQLParserException;

import java.util.ArrayList;

public class Table
{

    private String tableName;
    private ColumnDescription primaryKey;
    private ColumnDescription[] columnDescription;
    private ArrayList<String> columnNames;
    private ArrayList<ColumnDescription.DataType> columnType;
    private int rowLength;
    private ArrayList<Row> table;

    public Table(CreateTableQuery query)
    {
        this.tableName = query.getTableName();
        this.primaryKey = query.getPrimaryKeyColumn();
        this.columnDescription = query.getColumnDescriptions();

        this.columnNames = new ArrayList<>();
        this.columnType = new ArrayList<>();



        this.rowLength = columnDescription.length;

        this.table = new ArrayList<Row>();

        setTableColumns();
    }

    public Table()
    {
        this.table = new ArrayList<Row>();
    }

    public Table(int size)
    {
        this.table = new ArrayList<>(size);
    }

    public Table(String string)
    {
        this.table = new ArrayList<>();
        table.add(new Row());
        table.get(0).add(new Cell(string));
    }

    public ArrayList<Row> getTable()
    {
        return this.table;
    }

    /**
     * adds row to a table
     * @param row
     */
    public void add(Row row)
    {
        this.table.add(row);
    }

    /**
     * adds row at specific index in the tree
     * @param index
     * @param row
     */
    public void add(int index,Row row)
    {
        this.table.add(index,row);
    }

    /**
     * adds a whole table to the table
     * @param newTable
     */
    public void add(Table newTable)
    {
        if(newTable == null)
            return;
        for(int i=0;i<newTable.size();i++)
        {
            this.table.add(newTable.get(i));
        }
    }


    void set(int index,Row element)
    {
        this.table.set(index,element);
    }

    public int getTableSize()
    {
        return this.table.size();
    }

    public Row get(int index)
    {
        return this.table.get(index);
    }

    public void remove(int i)
    {
        table.remove(i);
    }

    public String getTableName()
    {
        return this.tableName;
    }

    public ColumnDescription getPrimaryKey()
    {
        return this.primaryKey;
    }

    ColumnDescription getColumnDescription(String columnName)
    {
        for(int i=0;i<this.columnDescription.length;i++)
        {
            if(this.columnDescription[i].getColumnName().equalsIgnoreCase(columnName))
                return columnDescription[i];
        }
        return null;
    }

    ColumnID getColumnID(String columnName)
    {
        for(int i=0;i<this.table.size();i++)
        {
            if(this.table.get(i).get(columnName) != null)
                return this.table.get(i).get(columnName).getColumnId();
        }
        return null;
    }

    public int getRowLength()
    {
        return this.rowLength;
    }

    public ArrayList<String> getColumnNames()
    {
        return this.columnNames;
    }

    public ColumnDescription[] getColumnDescription()
    {
        return this.columnDescription;
    }

    void setColumnDescription(ColumnDescription[] columnDescription)
    {
        this.columnDescription = columnDescription;
    }

    public void setTableColumns()
    {
        for(int i = 0;i<columnDescription.length;i++)
        {
            columnNames.add(columnDescription[i].getColumnName());
            columnType.add(columnDescription[i].getColumnType());
        }
    }

    public int size()
    {
        return table.size();
    }

    /**
     * checks to see if the given column is contained in the table
     * @param columnName
     * @return
     */
    public boolean containsColumn(String columnName)
    {
        for(int i=0;i<columnNames.size();i++)
        {
            if(columnName.equals(columnNames.get(i)))
                return true;
        }
        return false;
    }

    public void print()
    {
        for(int i=0;i<this.table.size();i++)
            System.out.println(this.table.get(i).toString());
    }
}
