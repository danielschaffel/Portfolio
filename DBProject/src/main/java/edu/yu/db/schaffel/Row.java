package edu.yu.db.schaffel;

import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.*;
import net.sf.jsqlparser.JSQLParserException;

import java.util.ArrayList;
import java.util.HashSet;

public class Row implements Comparable<Cell>
{
    private ArrayList<Cell> row;

 public Row()
 {
     row = new ArrayList<Cell>();
 }

 void add(Cell c)
 {
     this.row.add(c);
 }

 Cell get(int index)
 {
     return this.row.get(index);
 }

 int size()
 {
     return row.size();
 }

 Cell get(String columnname)
 {
     for(int i =0;i<this.size();i++)
     {
         if(this.get(i).getColumnName().equals(columnname))
         {
             return this.get(i);
         }
     }
     return null;
 }

 boolean contains(String columnName)
 {
     boolean checker = false;
     for(int i=0;i<this.size();i++)
     {
         if(this.get(i).getColumnName().equals(columnName))
             return true;
     }
     return checker;
 }

    /**
     * updates the cells in the row that need to be updated
     * @param values
     * @return
     */
 public ResultSet updateRow(ColumnValuePair[] values)
 {
     ResultSet result = new ResultSet();
     for(int i=0;i<values.length;i++)
     {
         if(this.contains(values[i].getColumnID().getColumnName()))
          result  = this.get(values[i].getColumnID().getColumnName()).setValue(values[i].getValue());
         if(result.isFalse())
             return result;
     }
     return new ResultSet("true");
 }

 public boolean equal(Row row)
 {
     boolean checker = false;
     for(int i=0;i<row.size();i++)
     {
         if(this.get(i).equals(row.get(i)))
             checker = true;
         else
         {
             checker = false;
             return checker;
         }

     }
     return checker;
 }

 public boolean equal(Row row,SelectQuery query,int whereInOrderBy)
 {
    Row thisRow = new Row();
    Row otherRow = new Row();
    for(int i=0;i<whereInOrderBy;i++)
    {
        thisRow.add(this.get(query.getOrderBys()[i].getColumnID().getColumnName()));
        otherRow.add(row.get(query.getOrderBys()[i].getColumnID().getColumnName()));
    }
    return thisRow.equal(otherRow);
 }

    @Override
    public int compareTo(Cell o) {
     return this.get(o.getColumnName()).compareTo(o);
    }

    @Override
    public String toString() {
        String string = "";
        for(int i=0;i<this.size();i++) {
            if(this.get(i) == null)
                continue;
            string += this.get(i).toString() + " ";
        }
        return string;
    }
}
