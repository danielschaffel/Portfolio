package edu.yu.db.schaffel;

public class ResultSet
{
    private Table resultSet;
    private String errorMessage;

    public ResultSet(Table table)
    {
        resultSet = table;
    }

    public ResultSet(Table table,String errorMessage)
    {
        resultSet = table;
        this.errorMessage = errorMessage;
    }

    public ResultSet(String result)
    {
        resultSet = new Table();
        resultSet.add(new Row());
        resultSet.get(0).add(new Cell(result));
    }

    public ResultSet()
    {

    }

    /**
     * checks to see if result set is true
     * @return
     */
    public boolean isTrue()
    {
        if(resultSet.get(0).get(0).getValue() == null)
            return false;
        if(resultSet.get(0).get(0).getValue().toString().equalsIgnoreCase("true") && resultSet.size()==1)
            return true;
        return false;
    }

    /**
     * checks to see if the result set is false
     * @return
     */
    public boolean isFalse()
    {
        if(resultSet.get(0).get(0).getValue() == null)
            return false;
        if(resultSet.get(0).get(0).getValue().toString().equalsIgnoreCase("false") && resultSet.size()==1)
            return true;
        return false;
    }
    public void print()
    {
        if(resultSet.size() == 0)
            return;
        if(this.isTrue() == false&& this.isFalse() == false)
            for(int i=0;i<resultSet.get(0).size();i++)
                System.out.print(resultSet.get(0).get(i).getColumnId().getColumnName() + "  ");
        System.out.println();
        resultSet.print();
        if(errorMessage != null)
            System.out.println(errorMessage);
    }

    public int getSize()
    {
        return this.resultSet.size();
    }

    public Table getTable()
    {
        return resultSet;
    }
}
