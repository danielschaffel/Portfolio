package edu.yu.db.schaffel;

import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.ColumnDescription;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.ColumnID;
import java.util.Objects;

import static edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.ColumnDescription.DataType.*;


public class Cell<T> implements Comparable<Cell>
{
    private ColumnID columnId;
    private T value;
    private ColumnDescription columnDescription;
    private ColumnDescription.DataType dataType;



    public Cell(String columnName,String tableName,ColumnDescription thisColumn)
    {
        this.columnId = new ColumnID(columnName,tableName);
        this.value = null;
        this.columnDescription = thisColumn;
    }

    public Cell()
    {
        this.value = null;
    }

    public Cell(Double value)
    {
        this.value = (T)value;
    }

    public Cell(String value)
    {
        this.value = (T)value;
    }

    public Cell(Integer value)
    {
        this.value = (T)value;
    }

    public Cell(Boolean value){this.value = (T)value;}

    public Cell(Cell other)
    {
        this.columnId = other.columnId;
        this.value = (T)other.value;
        this.columnDescription = other.columnDescription;
    }

    /**
     * sets the value of a cell and checks to make sure it meets all the right requirements
     * @param thisValue
     * @return
     */
    ResultSet setValue(String thisValue)
    {
        if(this.columnDescription.getColumnType() == VARCHAR)
        {
            if(thisValue.length()>columnDescription.getVarCharLength())
                return new ResultSet(new Table("False")," Too many characters in " + this.columnDescription.getColumnName());
            if(!thisValue.startsWith("'") && !thisValue.endsWith("'"))
                return new ResultSet(new Table("False"),"varchar has to be surrounded by '' in column " + this.columnDescription.getColumnName());
            thisValue = thisValue.substring(1,thisValue.length()-1);
            this.value = (T) thisValue;
        }
        if (this.columnDescription.getColumnType() == INT)
            try {
                this.value = (T) (Integer) Integer.valueOf(thisValue);
            } catch (Exception e) {
                return new ResultSet(new Table("False"),"invalid input in column " + this.columnDescription.getColumnName());
            }
        if (this.columnDescription.getColumnType() == DECIMAL)
        {
            if(!(this.columnDescription.getFractionLength() == 0&&this.columnDescription.getFractionLength()==0))
            {
                if(!checkDecimalStuff(thisValue))
                    return new ResultSet(new Table("False"),"Error with number length.");
                thisValue = fixDecimalLength(thisValue);
            }
            try{
                this.value = (T) (Double) Double.valueOf(thisValue);
            }catch (Exception e){
                return new ResultSet(new Table("False"),"invalid input in column " + this.columnDescription.getColumnName());
            }
        }
        if(this.columnDescription.getColumnType() == BOOLEAN)
            try {
                this.value = (T) Boolean.valueOf(thisValue);
            } catch (Exception e) {
                return new ResultSet(new Table("False"),"invalid input in column " + this.columnDescription.getColumnName());
            }
            return new ResultSet("true");
    }

    /**
     * cuts of any extra digits after the decimal point
     * @param thisValue
     * @return
     */
    String fixDecimalLength(String thisValue)
    {
        if(thisValue.contains("."))
            if(thisValue.substring(thisValue.indexOf(".")).length() > this.columnDescription.getFractionLength())
            {
                thisValue =thisValue.substring(0,thisValue.indexOf(".")+this.columnDescription.getFractionLength()+1);
            }
        return thisValue;
    }

    /**
     * sets values for a temp cell where dont need to to check for certain things
     * @param thisValue
     */
    void setValueForTemp(String thisValue)
    {
        if(this.columnDescription.getColumnType() == VARCHAR)
        {
            if(thisValue.length()>columnDescription.getVarCharLength())
                throw new IllegalArgumentException("To many characters in " + this.columnDescription.getColumnName());
            this.value = (T) thisValue;
        }
        if (this.columnDescription.getColumnType() == INT)
            try {
                this.value = (T) (Integer) Integer.valueOf(thisValue);
            } catch (Exception e) {
                throw new IllegalArgumentException(e);
            }
        if (this.columnDescription.getColumnType() == DECIMAL)
        {
            try{
                this.value = (T) (Double) Double.valueOf(thisValue);
            }catch (Exception e){
                throw new IllegalArgumentException(e);
            }
        }
        if(this.columnDescription.getColumnType() == BOOLEAN)
            try {
                this.value = (T) Boolean.valueOf(thisValue);
            } catch (Exception e) {
                throw new IllegalArgumentException(e);
            }
    }


    public void setColumnDescription(ColumnDescription columnDescription)
    {
        this.columnDescription = columnDescription;
    }

    public void setColumnId(ColumnID columnId)
    {

        this.columnId = columnId;
    }

    public String getColumnName()
    {
        return this.columnId.getColumnName();
    }

    public ColumnDescription getColumnDescription()
    {
        return columnDescription;
    }

    public ColumnID getColumnId()
    {
        return columnId;
    }

    public Boolean getUnique()
    {
        return this.columnDescription.isUnique();
    }

    public Boolean notNull()
    {
        return this.columnDescription.isNotNull();
    }

    public void setUnique(boolean uniqueness)
    {
        this.columnDescription.setUnique(uniqueness);
    }

    public T getValue()
    {
        return this.value;
    }

    public Boolean hasDefault()
    {
        return this.columnDescription.getHasDefault();
    }

    public String getDefaultValue()
    {
        return this.columnDescription.getDefaultValue();
    }

    /**
     * checks to see if anything is wrong with a decimal that would require throwing a false result set
     * @param thisValue
     * @return
     */
    boolean checkDecimalStuff(String thisValue)
    {
        if(!thisValue.contains(".")) {
            if(thisValue.length()>this.columnDescription.getWholeNumberLength())
                return false;
         else {
                for(int i = 0; i<=this.columnDescription.getFractionLength(); i++)
                {
                    if(i==0)
                        thisValue.concat(".");
                    else
                        thisValue.concat("0");
                }
                return true;
            }
        }
        if(thisValue.substring(thisValue.indexOf(".")).length()-1>this.columnDescription.getFractionLength())
        {
            thisValue = thisValue.substring(0,thisValue.indexOf(".")+this.columnDescription.getFractionLength());
            return true;
        }
        if(thisValue.indexOf(".")>this.columnDescription.getWholeNumberLength())
            return false;
        return true;
    }

    private int stringComparison(Cell thisCell,Cell fromTable)
    {
       String fromCellValue = (String)thisCell.value;
       String fromTableValue = (String)fromTable.value;
       return fromTableValue.compareTo(fromCellValue);
    }

    private int doubleComparison(Cell thisCell,Cell fromTable)
    {
        Double fromCellValue = (Double) thisCell.value;
        Double fromTableValue = (Double) fromTable.value;
        return fromTableValue.compareTo(fromCellValue);
    }

    private int intComparison(Cell thisCell,Cell fromTable)
    {
        Integer fromCellValue = (Integer)thisCell.value;
        Integer fromTableValue = (Integer)fromTable.value;
        return fromTableValue.compareTo(fromCellValue);
    }
    private int booleanComparison(Cell thisCell,Cell fromTable)
    {
        Boolean fromCellValue = (Boolean)thisCell.value;
        Boolean fromTableValue = (Boolean)fromTable.value;
        return fromTableValue.compareTo(fromCellValue);
    }

    @Override
    public int compareTo(Cell o)
    {
        int value =0;
        if(this.value == o.value)
            return 0;

        if(this.value == null || o.value == null)
            return -1;

        if(this.value.getClass() == String.class)
        {
           return value = stringComparison(this,o);
        }

        if(this.value.getClass() == Double.class)
        {
            return value = doubleComparison(this,o);
        }

        if(this.value.getClass() == Integer.class)
        {
            return value = intComparison(this,o);
        }

        if(this.value.getClass() == Boolean.class)
        {
            return value = booleanComparison(this,o);
        }
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cell<?> cell = (Cell<?>) o;
        return Objects.equals(value, cell.value);
    }

    @Override
    public int hashCode() {

        return Objects.hash(value);
    }

    @Override
    public String toString() {

        if(value == null)
            return "  ";
        return  value.toString();

    }
}
