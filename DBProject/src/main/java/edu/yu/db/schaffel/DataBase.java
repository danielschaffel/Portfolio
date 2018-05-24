package edu.yu.db.schaffel;

import com.sun.org.apache.regexp.internal.RE;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.ColumnDescription;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.ColumnID;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.ColumnValuePair;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.Condition;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.CreateIndexQuery;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.CreateTableQuery;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.DeleteQuery;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.InsertQuery;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.SQLParser;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.SQLQuery;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.SelectQuery;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.UpdateQuery;
import net.sf.jsqlparser.JSQLParserException;

import java.util.ArrayList;
import java.util.HashMap;

public class DataBase
{
    private Table table;
    private HashMap<String,Table> schema = new HashMap<>();
    private HashMap<String,BTree> indexedColumns = new HashMap<>();
    private ArrayList<String> nameOfIndexedColumns = new ArrayList<>();

    public DataBase()
    {
    }

    /**
     * the method that parses the SQLQuery and preforms whatever is supposed to happen
     * @param sql
     * @return
     */
    public ResultSet execute(String sql)
    {
        SQLParser parser = new SQLParser();
        SQLQuery query;
        try
        {
            query = parser.parse(sql);
        }
        catch (JSQLParserException e)
        {
            return new ResultSet(new Table("False"),"SQL parsing error");//make return false ResultSet
        }

        if(query instanceof CreateTableQuery)
        {
            ResultSet checking = this.create(this.table, (CreateTableQuery) query);
            if(checking == null)
                return new ResultSet("True");//any problem with a create query would have been caught
            return checking;
        }
        getRightTable(query);
        if(this.table == null)
            return new ResultSet(new Table("False"),"non existant table");
        if(query instanceof DeleteQuery)
            return this.delete(table, (DeleteQuery) query);
        if(query instanceof UpdateQuery)
            return this.update(table, (UpdateQuery) query);
        if(!checkColumns(query))
            return new ResultSet(new Table("false"),"query contains non existing column(s)");
        if(query instanceof InsertQuery)
            return this.insert(this.table, (InsertQuery) query);
         if(query instanceof SelectQuery)
             return this.select(this.table, (SelectQuery) query);
        if(query instanceof CreateIndexQuery)
            return this.createIndex(table, (CreateIndexQuery) query);
        return new ResultSet(new Table("false"),"non existant query type");
    }

    /**
     * method preforms all of the functions of a SelectQuery
     * @param table
     * @param query
     * @return
     */
    ResultSet select(Table table,SelectQuery query)
    {
        Table selected = copyOriginalTable(table);

        if(query.getWhereCondition() != null)
        {
            ArrayList<String> columnNames = columnsFromWhereCondition(query.getWhereCondition());
            if(checkIfAllColumnsAreIndexed(columnNames))
            {
                ArrayList<String> values = valuesFromWhereCondition(query.getWhereCondition());
                selected = getWhereFromBTree(columnNames,values,query.getWhereCondition());
            }
            selected = where(selected,query);
        }

        if(query.isDistinct() == true)
            selected = getDistinct(selected,query);
        if(query.getFunctions() != null)
           selected = preformFunction(selected,query);
        if(query.getOrderBys().length != 0)
           selected = orderBy(selected,query);
        if(selected.size() == 1 && selected.get(0).get(0).getValue().equals("False"))
            return new ResultSet(selected,"can not preform function on column");
        selected = getColumnValuesFromRowsForSelectedColumns(selected,query);
        return new ResultSet(selected);
    }

    /**
     * method gets all of the rows that match any of the conditions in the where part of a given query
     * @param columnNames
     * @param values
     * @param query
     * @return
     */
    Table getWhereFromBTree(ArrayList<String> columnNames,ArrayList<String> values,Condition query)
    {
        Table table = new Table();
        ArrayList<Condition.Operator> conditions = conditionsFromWhereCondition(query);
        for(int i=0;i<columnNames.size();i++)
        {
            Cell cell = new Cell();
            cell.setColumnId(this.table.getColumnID(columnNames.get(i)));
            cell.setColumnDescription(this.table.getColumnDescription(columnNames.get(i)));
            cell.setValue(values.get(i));
            table.add(useRightFunctionWithBTree(conditions.get(i),cell));
        }
        return table;
    }

    /**
     * chooses the right function from the where condition for selecting from the Btree
     * @param condition
     * @param key
     * @return
     */
    Table useRightFunctionWithBTree(Condition.Operator condition,Cell key)
    {
        BTree thisColumnTree = indexedColumns.get(key.getColumnName());
        if(condition == Condition.Operator.EQUALS)
          return thisColumnTree.get(key);
        if(condition == Condition.Operator.NOT_EQUALS)
            return thisColumnTree.getAllNotEqual(key);
        if(condition == Condition.Operator.GREATER_THAN)
            return thisColumnTree.getAllBigger(key);
        if(condition == Condition.Operator.GREATER_THAN_OR_EQUALS)
            return thisColumnTree.getAllEqualAndGreater(key);
        if(condition == Condition.Operator.LESS_THAN)
            return thisColumnTree.getAllLess(key);
        if(condition == Condition.Operator.LESS_THAN_OR_EQUALS)
            return thisColumnTree.getAllEqualAndLess(key);
        return null;
    }

    /**
     * checks to ensure that all the columns in the where conditions are indexed to decide if worth using the btree or not
     * @param columnName
     * @return
     */
    boolean checkIfAllColumnsAreIndexed(ArrayList<String> columnName)
    {
        for(int i=0;i<columnName.size();i++)
        {
            if(!indexedColumns.containsKey(columnName.get(i)))
                return false;
        }
        return true;
    }

    /**
     * method assembles all of the operators from a given query
     * @param query
     * @return
     */
    ArrayList<Condition.Operator> conditionsFromWhereCondition(Condition query)
    {
        ArrayList<Condition.Operator> conditions = new ArrayList<>();
        if(query.getOperator() == Condition.Operator.AND || query.getOperator() == Condition.Operator.OR )
        {
            conditionsFromWhereCondition(conditions,(Condition) query.getLeftOperand());
            conditionsFromWhereCondition(conditions,(Condition) query.getRightOperand());
        }
        else
            conditions.add(query.getOperator());
        return conditions;
    }

    /**
     * does the actual assembly of operators by recursing through the conditions
     * @param conditions
     * @param query
     * @return
     */
    ArrayList<Condition.Operator> conditionsFromWhereCondition(ArrayList<Condition.Operator> conditions,Condition query)
    {
        if(query.getOperator() == Condition.Operator.AND || query.getOperator() == Condition.Operator.OR )
        {
            conditionsFromWhereCondition(conditions,(Condition) query.getLeftOperand());
            conditionsFromWhereCondition(conditions,(Condition) query.getRightOperand());
        }
        else
            conditions.add( query.getOperator());
        return conditions;
    }

    /**
     * gathers all of the values from condition
     * @param query
     * @return
     */
    ArrayList<String> valuesFromWhereCondition(Condition query)
    {
        ArrayList<String> columnNames = new ArrayList<>();
        if(query.getOperator() == Condition.Operator.AND || query.getOperator() == Condition.Operator.OR )
        {
            valuesFromWhereCondition(columnNames,(Condition) query.getLeftOperand());
            valuesFromWhereCondition(columnNames,(Condition) query.getRightOperand());
        }
        else
            columnNames.add(query.getRightOperand().toString());
        return columnNames;
    }

    /**
     * does the actual gathering of values through recursion
     * @param columnName
     * @param query
     * @return
     */
    ArrayList<String> valuesFromWhereCondition(ArrayList<String> columnName,Condition query)
    {
        if(query.getOperator() == Condition.Operator.AND || query.getOperator() == Condition.Operator.OR )
        {
            columnsFromWhereCondition(columnName,(Condition) query.getLeftOperand());
            columnsFromWhereCondition(columnName,(Condition) query.getRightOperand());
        }
        else
            columnName.add(query.getRightOperand().toString());
        return columnName;
    }

    /**
     * gathers the columns from the where conditions
     * @param query
     * @return
     */
    ArrayList<String> columnsFromWhereCondition(Condition query)
    {
        ArrayList<String> columnNames = new ArrayList<>();
        if(query.getOperator() == Condition.Operator.AND || query.getOperator() == Condition.Operator.OR )
        {
            columnsFromWhereCondition(columnNames,(Condition) query.getLeftOperand());
            columnsFromWhereCondition(columnNames,(Condition) query.getRightOperand());
        }
        else
            columnNames.add(query.getLeftOperand().toString());
        return columnNames;
    }

    /**
     * does the actual gathering of the columns from the conditions through recursion
     * @param columnName
     * @param query
     * @return
     */
    ArrayList<String> columnsFromWhereCondition(ArrayList<String> columnName,Condition query)
    {
        if(query.getOperator() == Condition.Operator.AND || query.getOperator() == Condition.Operator.OR )
        {
            columnsFromWhereCondition(columnName,(Condition) query.getLeftOperand());
            columnsFromWhereCondition(columnName,(Condition) query.getRightOperand());
        }
        else
            columnName.add(query.getLeftOperand().toString());
        return columnName;
    }

    /**
     * method takes care of UpdateQuery
     * @param table
     * @param query
     * @return
     */
    ResultSet update(Table table,UpdateQuery query)
    {
        Table selected = new Table();
        ResultSet result = new ResultSet();
        if(query.getWhereCondition() != null)
           result = updateWhere(this.table,query);
        else
            for(int i=0;i<table.size();i++)
            {
               result = this.table.get(i).updateRow(query.getColumnValuePairs());
            }
            if(result.isTrue())
                return result;
           return new ResultSet(new Table("False"),"for some reaosn reached this point but it never should");
    }

    /**
     * method takes care of DeleteQuery
     * @param table
     * @param query
     * @return
     */
    ResultSet delete(Table table,DeleteQuery query)
    {
        Table deleteFromBTree = whereDelete(table,query);
        ArrayList<String> columnNames = columnsFromWhereCondition(query.getWhereCondition());
        ArrayList<String> values = valuesFromWhereCondition(columnNames,query.getWhereCondition());
        if(checkIfAllColumnsAreIndexed(columnNames))
        {
            for(int i=0;i<columnNames.size();i++)
            {
                BTree thisColumn = indexedColumns.get(columnNames.get(i));

            }
        }
return new ResultSet("True");
    }

    /**
     * method takes care of CreateQuery
     * @param table
     * @param query
     * @return
     */
    ResultSet create(Table table, CreateTableQuery query)
    {
        //this.table = new Table(query);
        if(schema.containsKey(query.getTableName()))
            return new ResultSet(new Table("false"),"table name already used");
        schema.put(query.getTableName(),new Table(query));
        indexedColumns.put(query.getPrimaryKeyColumn().getColumnName(),new BTree());
        nameOfIndexedColumns.add(query.getPrimaryKeyColumn().getColumnName());
        return null;
    }

    /**
     * method ensures that all columns of rows being inserted into a table that are indexed are added to their resprective btrees
     * @param row
     */
    void indexDuringInsert(Row row)
    {
        for(int i=0;i<row.size();i++)
        {
            BTree columnIndex = indexedColumns.get(row.get(i).getColumnName());
            if(columnIndex != null)
            {
                columnIndex.put(row.get(i),row);
            }
        }
    }

    /**
     * method takes care of InsertQuery
     * @param table
     * @param query
     * @return
     */
    ResultSet insert(Table table,InsertQuery query)
    {
        Row row = createRow(query);
        ResultSet trueRow = fillRowWithData(row,query);
        if(trueRow.isTrue())//make else and return false result set
            this.table.add(row);
        else
            return trueRow;
        indexDuringInsert(row);
        return trueRow;

    }

    /**
     * method creates btree on specified column
     * @param table
     * @param query
     * @return
     */
    ResultSet createIndex(Table table,CreateIndexQuery query)
    {
        if(!indexedColumns.containsKey(query))
        {
            indexedColumns.put(query.getColumnName(),new BTree());
            nameOfIndexedColumns.add(query.getColumnName());
        }
        BTree currentTree = indexedColumns.get(query.getColumnName());

        for(int i=0;i<this.table.size();i++)
        {
            currentTree.put(this.table.get(i).get(query.getColumnName()),this.table.get(i));
        }
        return new ResultSet("true");
    }

    /**
     * this method preforms all of the functions from the SelectQuery
     * @param table
     * @param query
     * @return
     */
    Table preformFunction(Table table,SelectQuery query)
    {
        for(int i=0;i<query.getFunctions().size();i++)
        {
            if(!checkToMakeSureValidFunctionOnColumnType(table,query,i))
                return new Table("False");
            if(query.getFunctions().get(i).function == SelectQuery.FunctionName.AVG)
                getAvg(table,query,i);
            if(query.getFunctions().get(i).function == SelectQuery.FunctionName.COUNT)
                table = getCount(table,query,i);
            if(query.getFunctions().get(i).function == SelectQuery.FunctionName.MAX)
                table = getMax(table,query,i);
            if(query.getFunctions().get(i).function == SelectQuery.FunctionName.MIN)
                table = getMin(table,query,i);
            if(query.getFunctions().get(i).function == SelectQuery.FunctionName.SUM)
                getSum(table,query,i);
        }
        if(query.getSelectedColumnNames().length == query.getFunctions().size())
            removeExtraRows(table);
        return table;
    }

    /**
     * removes all of the extra rows if only calling for functions in a SelectQuery
     * @param table
     */
    void removeExtraRows(Table table)
    {

        for(int i=1;i<table.size();i++)
        {
            table.remove(i);
            i = 0;
        }
    }

    /**
     * ensures the validity of the function with the column type
     * @param table
     * @param query
     * @param numberInFunction
     * @return
     */
    boolean checkToMakeSureValidFunctionOnColumnType(Table table,SelectQuery query,int numberInFunction)
    {
        SelectQuery.FunctionInstance function = query.getFunctions().get(numberInFunction);
            if((function.function == SelectQuery.FunctionName.MIN || function.function == SelectQuery.FunctionName.MAX || function.function == SelectQuery.FunctionName.AVG || function.function == SelectQuery.FunctionName.SUM) &&
                    table.get(0).get(query.getFunctions().get(numberInFunction).column.getColumnName()).getColumnDescription().getColumnType() == ColumnDescription.DataType.BOOLEAN)
                return false;
            if((function.function == SelectQuery.FunctionName.AVG || function.function == SelectQuery.FunctionName.SUM)
                    &&table.get(0).get(query.getFunctions().get(numberInFunction).column.getColumnName()).getColumnDescription().getColumnType() == ColumnDescription.DataType.VARCHAR )
                return false;
            return true;
    }

    /**
     * gets the max value of the given column
     * @param table
     * @param query
     * @param numberInFunction
     * @return
     */
    Table getMax(Table table,SelectQuery query,int numberInFunction)
    {
        Table maxTable = getSpecificColumn(table,query.getFunctions().get(numberInFunction).column.getColumnName());
        Cell max = maxTable.get(0).get(query.getFunctions().get(numberInFunction).column.getColumnName());
        for(int i=1;i<maxTable.size();i++)
        {
            if(maxTable.get(i).get(query.getFunctions().get(numberInFunction).column.getColumnName()).compareTo(max) < 0)
                max = maxTable.get(i).get(query.getFunctions().get(numberInFunction).column.getColumnName());
        }
        table = setColumnToFunctionValue(maxTable,max,query.getFunctions().get(numberInFunction).column.getColumnName());
        return table;
    }

    /**
     * gets the minimum value of the given column
     * @param table
     * @param query
     * @param numberInFunction
     * @return
     */
    Table getMin(Table table,SelectQuery query,int numberInFunction)
    {
        Table minTable = table;
        Cell min = table.get(0).get(query.getFunctions().get(numberInFunction).column.getColumnName());
        for(int i=1;i<minTable.size();i++)
        {
            if(minTable.get(i).get(query.getFunctions().get(numberInFunction).column.getColumnName()).compareTo(min) > 0)
                min = minTable.get(i).get(query.getFunctions().get(numberInFunction).column.getColumnName());
        }
        minTable = setColumnToFunctionValue(minTable,min,query.getFunctions().get(numberInFunction).column.getColumnName());
        return minTable;
    }

    /**
     * gets the average of the given column
     * @param table
     * @param query
     * @param numberInFunction
     * @return
     */
    Table getAvg(Table table,SelectQuery query,int numberInFunction)
    {
        Table avgTable = new Table();
        String columnName = query.getFunctions().get(numberInFunction).column.getColumnName();
        if(table.get(0).get(columnName).getColumnDescription().getColumnType() == ColumnDescription.DataType.INT)
            return avgTable = getIntAvg(table,query,numberInFunction);
        if(table.get(0).get(columnName).getColumnDescription().getColumnType() == ColumnDescription.DataType.DECIMAL)
            return avgTable = getDecimalAvg(table,query,numberInFunction);
        return null;//have to make return result set false with error
    }

    /**
     * gets the sum of the given column
     * @param table
     * @param query
     * @param numberInFunction
     * @return
     */
    Table getSum(Table table,SelectQuery query,int numberInFunction)
    {
        String columnName = query.getFunctions().get(numberInFunction).column.getColumnName();
        Table sumTable = new Table();
        if(table.get(0).get(columnName).getColumnDescription().getColumnType() == ColumnDescription.DataType.INT)
            return sumTable = intSum(table,query,numberInFunction);
        if(table.get(0).get(columnName).getColumnDescription().getColumnType() == ColumnDescription.DataType.DECIMAL)
            return sumTable = decimalSum(table,query,numberInFunction);
        return null;//make return result false and shit
    }

    /**
     * gets the count of a given column
     * @param table
     * @param query
     * @param numberInFunction
     * @return
     */
    Table getCount(Table table,SelectQuery query,int numberInFunction)
    {
        int count =0;
        Table justThisColumn = getSpecificColumn(table,query.getFunctions().get(numberInFunction).column.getColumnName());
        if(query.getFunctions().get(numberInFunction).isDistinct)
            justThisColumn = getDistinctColumn(justThisColumn);
        for(int i=0;i<justThisColumn.size();i++)
        {
            if(justThisColumn.get(i).get(query.getFunctions().get(numberInFunction).column.getColumnName()) != null)
                count++;
        }
        Cell totalCount = new Cell((Integer) count);
        Table counted = setColumnToFunctionValue(table,totalCount,query.getFunctions().get(numberInFunction).column.getColumnName());
        return counted;
    }

    /**
     * gets all the distinct rows of given column
     * @param table
     * @return
     */
    Table getDistinctColumn(Table table)
    {
        Table distinct = table;
        for(int i=0;i<table.size();i++)
        {
            Row row = distinct.get(i);
            for(int x=i;x<distinct.size();x++)
            {
                if(i !=x && row.equal(distinct.get(x))) {
                    distinct.remove(x);
                    x--;
                }
            }
        }
    return distinct;
    }

    /**
     * makes table of a specific column
     * @param table
     * @param columnName
     * @return
     */
    Table getSpecificColumn(Table table,String columnName)
    {
        Table originalTable = table;
        Table justThisColumn = new Table();
        for(int i=0;i<table.size();i++)
        {
            Row newRow = new Row();
            for(int j =0;j<1;j++)
            {
                newRow.add(originalTable.get(i).get(columnName));
            }
            justThisColumn.add(newRow);
        }
        return justThisColumn;
    }

    /**
     * gets distinct rows from the table
     * @param table
     * @param query
     * @return
     */
    Table getDistinct(Table table,SelectQuery query)
    {
        Table correctRows = getColumnValuesFromRowsForSelectedColumns(table,query);
        Table distinct = correctRows;
        //distinct.add(correctRows.get(0));
        for(int i=0;i<correctRows.size();i++)
        {
            Row row = distinct.get(i);
            for(int j=i;j<distinct.size();j++)
            {
                if(i !=j && row.equal(distinct.get(j))) {
                    distinct.remove(j);
                    j--;
                }
            }
        }
        return distinct;
    }

    /**
     * sets all the value of a given column to the value of a function for every row
     * @param table
     * @param cell
     * @param columnName
     * @return
     */
    Table setColumnToFunctionValue(Table table,Cell cell,String columnName)
    {
        //have to check if the other things in query are null if they are have to empty the table and then add the cell and if there are multiple functions have to set those
        Table everyThingSet = table;
        for(int i=0;i<everyThingSet.size();i++)
        {
            everyThingSet.get(i).get(columnName).setValueForTemp(cell.getValue().toString());
        }
        return everyThingSet;
    }

    /**
     * gets the average of a column of type int
     * @param table
     * @param query
     * @param numberInFunction
     * @return
     */
    Table getIntAvg(Table table,SelectQuery query,int numberInFunction)
    {
        int total =0;
        int count =0;
        String columnName = query.getFunctions().get(numberInFunction).column.getColumnName();
        Table avgTable = getSpecificColumn(table,columnName);
        if(query.getFunctions().get(numberInFunction).isDistinct)
            avgTable = getDistinctColumn(avgTable);
        for(int i=0;i<avgTable.size();i++)
        {
            if(avgTable.get(i).get(columnName) != null)
            {
                total += (Integer)avgTable.get(i).get(columnName).getValue();
                count++;
            }

        }
        total = total/count;
        Cell avgCell = new Cell(total);

        table = setColumnToFunctionValue(avgTable,avgCell,columnName);
        return table;
    }

    /**
     * gets the average of a column of type decimal
     * @param table
     * @param query
     * @param numberInFunction
     * @return
     */
    Table getDecimalAvg(Table table,SelectQuery query,int numberInFunction)
    {
        double total =0;
        double count =0;
        String columnName = query.getFunctions().get(numberInFunction).column.getColumnName();
        Table avgTable = getSpecificColumn(table,columnName);
        if(query.getFunctions().get(numberInFunction).isDistinct)
            avgTable = getDistinctColumn(avgTable);
        for(int i=0;i<avgTable.size();i++)
        {
            if(avgTable.get(i).get(columnName) != null)
            {
                total += (Double)avgTable.get(i).get(columnName).getValue();
                count++;
            }
        }
        total = total/count;
        Cell avgCell = new Cell(total);
        table = setColumnToFunctionValue(table,avgCell,columnName);
        return table;
    }

    /**
     * gets the sum of a column of type int
     * @param table
     * @param query
     * @param numberInFunction
     * @return
     */
    Table intSum(Table table,SelectQuery query,int numberInFunction)
    {
        String columnName = query.getFunctions().get(numberInFunction).column.getColumnName();
        int totalSum =0;
        Table sumTable = getSpecificColumn(table,columnName);
        if(query.getFunctions().get(numberInFunction).isDistinct)
            sumTable = getDistinctColumn(sumTable);
        for (int i=0;i<sumTable.size();i++)
        {
            if(sumTable.get(i).get(columnName) != null)
                totalSum += (Integer)table.get(i).get(columnName).getValue();
        }
        Cell cell = new Cell(totalSum);
        table = setColumnToFunctionValue(table,cell,columnName);
        return sumTable;
    }

    /**
     * gets the sum of column of type decimal
     * @param table
     * @param query
     * @param numberInFunction
     * @return
     */
    Table decimalSum(Table table,SelectQuery query,int numberInFunction)
    {
        String columnName = query.getFunctions().get(numberInFunction).column.getColumnName();
        double totalSum =0;
        Table sumTable = getSpecificColumn(table,columnName);
        if(query.getFunctions().get(numberInFunction).isDistinct)
            sumTable = getDistinctColumn(sumTable);
        for (int i=0;i<sumTable.size();i++)
        {
            if(sumTable.get(i).get(columnName) != null)
                totalSum += (double)sumTable.get(i).get(columnName).getValue();
        }
        Cell cell = new Cell(totalSum);
        table = setColumnToFunctionValue(table,cell,columnName);
        return sumTable;
    }

    /**
     * orders the rows of the table by the specified column
     * @param table
     * @param query
     * @return
     */
    Table orderBy(Table table,SelectQuery query)
    {

        for(int i=0;i<query.getOrderBys().length;i++)
        {
            if(i == 0) {
                sort(table, query.getOrderBys()[i].getColumnID().getColumnName(), 0, table.size() - 1);
                if (query.getOrderBys()[0].isDescending())
                    table = reverse(table);
            }
            else
                continueOrdering(table,query,i);
        }
        return table;
    }

    /**
     * if more then one column is specified this method takes care of the rest the ordering
     * @param table
     * @param query
     * @param placeInOrderBy
     */
    void continueOrdering(Table table,SelectQuery query,int placeInOrderBy)
    {
        Table subTable = new Table();
        //maybe make i = 0 and check i+1 when i have time
        subTable.add(table.get(0));
        for(int i =1;i<table.size();i++)
        {
            if(subTable.size() == 0)
            {
                subTable.add(table.get(i));
                continue;
            }
            if(table.get(i).equal(table.get(i-1),query,placeInOrderBy))
            {
                subTable.add(table.get(i));
            }
            else
            {
                sortAndPutBack(subTable,table,query,i,placeInOrderBy);
                removeAll(subTable);
            }
        }
        if(subTable.size() > 1)
        {
            sortAndPutBack(subTable,table,query,table.size(),placeInOrderBy);
        }
    }

    /**
     * sorts the subTable of rows that match the previous order bys and then inserts back into the original table
     * @param subTable
     * @param table
     * @param query
     * @param whereInOriginaltable
     * @param placeInOrderBy
     */
    void sortAndPutBack(Table subTable,Table table,SelectQuery query,int whereInOriginaltable,int placeInOrderBy)
    {
        sort(subTable,query.getOrderBys()[placeInOrderBy].getColumnID().getColumnName(),0,subTable.size()-1);
        if(query.getOrderBys()[placeInOrderBy].isDescending())
            subTable = reverse(subTable);
        putBackIntoTable(subTable,table,whereInOriginaltable);
    }

    /**
     * if the order by is descending this method reverses the order of the table
     * @param row
     * @return
     */
    Table reverse(Table row)
    {
        Table reversed = new Table();
        for(int i = row.size()-1;i>=0;i--)
        {
            reversed.add(row.get(i));
        }
        return reversed;
    }

    /**
     * removes everything from a table used in sort
     * @param row
     */
    void removeAll(Table row)
    {
        int length = row.size();
        for(int i=0;i<length;i++)
            row.remove(0);
    }

    /**
     * puts the sorted and ordered subtable back into the table
     * @param subTable
     * @param table
     * @param whereInOriginalTable
     */
    void putBackIntoTable(Table subTable,Table table,int whereInOriginalTable)
    {
        int x =(whereInOriginalTable)-subTable.size();
        for(int i=0;i<subTable.size();i++)
        {
            table.set(x,subTable.get(i));
            x++;
        }
    }

    /**
     * slightly modified merge sort taken from the slides
     * @param table
     * @param columnName
     * @param lo
     * @param hi
     */
    void sort(Table table,String columnName,int lo,int hi)
    {
        Table a = table;
        Table aux = new Table(a.size());
        sort(a,aux,lo,hi,columnName);
    }

    void sort(Table a,Table aux,int lo,int hi,String columnName)
    {
        if(hi<=lo)
            return;
        int mid = (hi+lo)/2;
        sort(a,aux,lo,mid,columnName);
        sort(a,aux,mid+1,hi,columnName);
        merge(a,aux,lo,mid,hi,columnName);
    }

    void merge(Table a,Table aux,int lo,int mid,int hi,String columnName)
    {
        for(int i =lo;i<=hi;i++)
        {
            aux.add(i,a.get(i));
        }
        int left =lo,right = mid+1;
        for(int current = lo;current<=hi;current++)
        {
            if(left>mid)
                a.set(current,aux.get(right++));
            else if(right > hi)
                a.set(current,aux.get(left++));
            else if(aux.get(right).get(columnName).compareTo(aux.get(left).get(columnName))>0)
                a.set(current,aux.get(right++));
            else
                a.set(current,aux.get(left++));
        }
    }

    /**
     * adds all the rows that match where conditions to a table to return
     * @param table
     * @param query
     * @return
     */
    Table where(Table table,SelectQuery query)
    {
        Table selectedRows = new Table();
        for(int i =0;i<table.size();i++)
        {
            if(where(table.get(i), query.getWhereCondition()))
            {
                selectedRows.add(table.get(i));
            }
        }
        return selectedRows;
    }

    /**
     *recurses through the conditions to the bottom of the conditions and then checks to see if the given row matches the condition or not returning true if it does
     * false other wise
     * @param row
     * @param condition
     * @return
     */
    boolean where(Row row,Condition condition)
    {
        Boolean leftStatus;
        Boolean rightStatus;
        if(condition.getOperator() == Condition.Operator.AND)
        {
            leftStatus = where(row,(Condition)condition.getLeftOperand());
            rightStatus = where(row,(Condition)condition.getRightOperand());

            if(leftStatus == true && rightStatus == true)
            {
                return true;
            }
            return false;
        }
        if(condition.getOperator() == Condition.Operator.OR)
        {
            leftStatus = where(row,(Condition)condition.getLeftOperand());
            rightStatus = where(row,(Condition)condition.getRightOperand());
            if(leftStatus == true || rightStatus == true)
            {
                return true;
            }
            return false;
        }
        else {
            boolean checking = checkCondition(row,condition);
            return checking;
        }
    }

    /**
     * checkes to see if the given row actually matches the operators from the condition
     * @param row
     * @param condition
     * @return
     */
    boolean checkCondition(Row row,Condition condition)
    {
        Cell temp = row.get(condition.getLeftOperand().toString());
        Cell tempFromCondition = new Cell(temp.getColumnName(),"doesnt matter",temp.getColumnDescription());
        tempFromCondition.setValueForTemp(condition.getRightOperand().toString());
        Boolean result = false;
        if(condition.getOperator() == Condition.Operator.EQUALS)
            if (temp.compareTo(tempFromCondition) == 0)
                result = true;
        if(condition.getOperator() == Condition.Operator.NOT_EQUALS)
            if (temp.compareTo(tempFromCondition) != 0)
                result = true;
        if(condition.getOperator() == Condition.Operator.GREATER_THAN)
            if (tempFromCondition.compareTo(temp) > 0)
                result = true;
        if(condition.getOperator() == Condition.Operator.GREATER_THAN_OR_EQUALS)
            if (tempFromCondition.compareTo(temp) >= 0)
                result = true;
        if(condition.getOperator() == Condition.Operator.LESS_THAN)
            if (tempFromCondition.compareTo(temp) < 0)
                result = true;
        if(condition.getOperator() == Condition.Operator.LESS_THAN_OR_EQUALS)
            if (tempFromCondition.compareTo(temp) <= 0)
                result = true;
        return result;
    }

    /**
     * uses where methods to find all of the rows to delete from the table and from the btree
     * @param table
     * @param query
     * @return
     */
    Table whereDelete(Table table,DeleteQuery query)
    {
        Table allRowsToDeleteForBTree = new Table();

        for(int i =0;i<table.size();i++)
        {
            if(where(this.table.get(i), query.getWhereCondition()))
            {
                Row row = this.table.get(i);
                for(int j=0;j<nameOfIndexedColumns.size();j++)
                {
                    BTree tree = indexedColumns.get(nameOfIndexedColumns.get(j));
                    tree.delete(row.get(nameOfIndexedColumns.get(j)),row);
                }
                this.table.remove(i);
                i--;
            }
        }
        return allRowsToDeleteForBTree;
    }

    /**
     * uses the where methods to determine which rows to update and removes old values from btree and puts back new ones
     * @param table
     * @param query
     * @return
     */
    ResultSet updateWhere(Table table,UpdateQuery query)
    {
        ResultSet resultSet = new ResultSet();

        for(int i=0;i<table.size();i++)
        {
            if(where(table.get(i),query.getWhereCondition()))
            {
                Row row = table.get(i);
                for(int j=0;j<nameOfIndexedColumns.size();j++)
                {
                   BTree tree = indexedColumns.get(nameOfIndexedColumns.get(j));
                   tree.delete(row.get(nameOfIndexedColumns.get(j)),row);
                }
              resultSet = table.get(i).updateRow(query.getColumnValuePairs());
                for(int j=0;j<nameOfIndexedColumns.size();j++)
                {
                    BTree tree = indexedColumns.get(nameOfIndexedColumns.get(j));
                    tree.put(row.get(nameOfIndexedColumns.get(j)),row);
                }
            }
            if(resultSet.isFalse())
                return resultSet;
        }
        return new ResultSet("true");
    }

    /**
     * returns table with all of the columns requested in select query
     * @param table
     * @param query
     * @return
     */
    Table getColumnValuesFromRowsForSelectedColumns(Table table,SelectQuery query)
    {
        Table originalTable = table;
        Table selectedColumns = new Table();
        ArrayList<String> columnsWanted = new ArrayList<>();
        ColumnID[] fromQuery = query.getSelectedColumnNames();
        for(int i =0;i < query.getSelectedColumnNames().length;i++)
        {
            columnsWanted.add(fromQuery[i].getColumnName());
        }
        if(columnsWanted.size() == 1 && columnsWanted.get(0).equals("*"))
        {
            originalTable.setColumnDescription(this.table.getColumnDescription());
            return originalTable;
        }
        //this loop goes through each row of the table
        for(int i=0;i<table.size();i++)
        {
            Row ithRow = new Row();
            //this loop goes through the row selecting the cells that are requested
            for(int j =0;j<columnsWanted.size();j++)
            {
                    ithRow.add(table.get(i).get(columnsWanted.get(j)));
                    ithRow.get(columnsWanted.get(j)).setColumnDescription(table.get(i).get(columnsWanted.get(j)).getColumnDescription());

            }
            selectedColumns.add(ithRow);
        }
        return selectedColumns;
    }

    /**
     * creates a row with all of the cells set to the right columns but leaves the values null
     * @param query
     * @return
     */
    Row createRow(InsertQuery query)
    {
        Row thisRow = new Row();
        for(int i=0;i<this.table.getRowLength();i++)
        {
            thisRow.add(new Cell(this.table.getColumnNames().get(i),this.table.getTableName(),this.table.getColumnDescription()[i]));
        }
        return thisRow;
    }

    /**
     * goes through the row created in createRow method with the values from the insert query
     * @param thisRow
     * @param query
     * @return
     */
    ResultSet fillRowWithData(Row thisRow, InsertQuery query)
    {
        ResultSet resultSet = new ResultSet();
        ColumnValuePair[] temp = query.getColumnValuePairs();
        HashMap<ColumnID,String> values = new HashMap<>();
        for(int i = 0;i<temp.length;i++)
        {
            values.put(temp[i].getColumnID(),temp[i].getValue());
        }
        for(int i =0;i<this.table.getRowLength();i++)
        //This loop goes through Insert query and creates a Row of Cells holding the Data
        {
            if(values.containsKey(thisRow.get(i).getColumnId()))//have to check here when setting value if worked or not and if didnt then return null
                resultSet = thisRow.get(i).setValue(values.get(thisRow.get(i).getColumnId()));
            else if(thisRow.get(i).hasDefault())
                resultSet = thisRow.get(i).setValue(thisRow.get(i).getDefaultValue());
            if(thisRow.get(i).notNull() && thisRow.get(i).getValue() == null)
                return new ResultSet(new Table("False"),thisRow.get(i).getColumnName() + " can not be null");
            if(thisRow.get(i).getUnique() || thisRow.get(i).getColumnName().equals(this.table.getPrimaryKey().getColumnName()))//making sure that values are unique if necissary
                resultSet = checkIfUnique(i,thisRow);
            if(!resultSet.isTrue())
            {
                thisRow = null;
                return resultSet;
            }
        }
        return resultSet;
    }

    /**
     * checks unique columns to make sure the value being inserted is unique
     * @param uniqueCell
     * @param thisRow
     * @return
     */
    ResultSet checkIfUnique(int uniqueCell,Row thisRow)
    {
        for(int i=0;i<this.table.getTableSize();i++)
        {
            if(this.table.get(i).get(uniqueCell).equals(thisRow.get(uniqueCell)) && this.table.get(i).get(uniqueCell) != null)
            {
                return new ResultSet(new Table("False"),"not unique");
            }
        }
       return new ResultSet("true");
    }

    /**
     * deep clones the whole table
     * @param table
     * @return
     */
    Table copyOriginalTable(Table table)
    {
        Table newTable = new Table();
        newTable.setColumnDescription(this.table.getColumnDescription());
        for(int i=0;i<table.size();i++)
        {
            newTable.add(deepCloneOfRow(table.get(i)));
        }
        //newTable.setTableName("newTable");
        return newTable;
    }

    Row deepCloneOfRow(Row row)
    {
        Row clonedRow = new Row();
        for(int i=0;i<row.size();i++)
        {
            clonedRow.add(new Cell(row.get(i)));
            clonedRow.get(i).setColumnId(row.get(i).getColumnId());
        }
        return clonedRow;
    }

    /**
     * gets the table specified in the Query
     * @param query
     */
    void getRightTable(SQLQuery query)
    {
        if(query instanceof SelectQuery)
            getRightTableSelect((SelectQuery) query);
        if(query instanceof UpdateQuery)
            getRightTableUpdate((UpdateQuery) query);
        if(query instanceof DeleteQuery)
            getRightTableDelete((DeleteQuery) query);
        if(query instanceof InsertQuery)
            getRightTableInsert((InsertQuery) query);
        if(query instanceof CreateIndexQuery)
            getRightTableIndex((CreateIndexQuery) query);
    }

    void getRightTableSelect(SelectQuery query)
    {
        this.table = schema.get(query.getFromTableNames()[0]);// i used 0 bc it was stated that we only have to deal with one table at a time
    }

    void getRightTableDelete(DeleteQuery query)
    {
        this.table = schema.get(query.getTableName());
    }

    void getRightTableUpdate(UpdateQuery query)
    {
        this.table = schema.get(query.getTableName());
    }

    void getRightTableIndex(CreateIndexQuery query)
    {
        this.table = schema.get(query.getTableName());
    }

    void getRightTableInsert(InsertQuery query)
    {
        this.table = schema.get(query.getTableName());
    }

    /**
     * checks all of the columns from the query to makes sure they all exist
     * @param query
     * @return
     */
    boolean checkColumns(SQLQuery query)
    {
        if(query instanceof SelectQuery)
            return checkColumnsSelect((SelectQuery) query);
        if(query instanceof UpdateQuery)
            return checkColumnsUpdate((UpdateQuery) query);
        if(query instanceof DeleteQuery)
            return checkColumnsDelete((DeleteQuery) query);
        if(query instanceof InsertQuery)
            return checkColumnsInsert((InsertQuery) query);
        if(query instanceof CreateIndexQuery)
            return checkColumnsIndex((CreateIndexQuery) query);

        return false;
    }

    boolean checkColumnsSelect(SelectQuery query)
    {

        if(query.getSelectedColumnNames().length == 1 && query.getSelectedColumnNames()[0].getColumnName().equals("*"))
            return true;
        for(int i=0;i<query.getSelectedColumnNames().length;i++)
        {
            if(!this.table.containsColumn(query.getSelectedColumnNames()[i].getColumnName()))
                return false;
        }
        return true;
    }

    boolean checkColumnsUpdate(UpdateQuery query)
    {
        for(int i=0;i<query.getColumnValuePairs().length;i++)
        {
            if(!this.table.containsColumn(query.getColumnValuePairs()[i].getColumnID().getColumnName()))
                return false;
        }
        return true;
    }

    boolean checkColumnsDelete(DeleteQuery query)
    {
        for(int i=0;i<query.getColumnValuePairs().length;i++)
        {
            if(!this.table.containsColumn(query.getColumnValuePairs()[i].getColumnID().getColumnName()))
                return false;
        }
        return true;
    }

    boolean checkColumnsInsert(InsertQuery query)
    {
        for(int i=0;i<query.getColumnValuePairs().length;i++)
        {
            if(!this.table.containsColumn(query.getColumnValuePairs()[i].getColumnID().getColumnName()))
                return false;
        }
        return true;
    }

    boolean checkColumnsIndex(CreateIndexQuery query)
    {
            if(!this.table.containsColumn(query.getColumnName()))
                return false;

        return true;
    }

    void print()
    {
        this.table.print();
    }

    public Table getTable(String tableName)
    {
        return schema.get(tableName);
    }

    public int getTableSize(String tableName)
    {
        return schema.get(tableName).size();
    }

    public BTree getIndex(String indexedColumn)
    {
        return indexedColumns.get(indexedColumn);
    }
}
