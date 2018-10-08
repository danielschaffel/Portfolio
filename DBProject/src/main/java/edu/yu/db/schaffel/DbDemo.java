package edu.yu.db.schaffel;

import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.CreateTableQuery;
import edu.yu.cs.dataStructures.fall2016.SimpleSQLParser.SQLParser;
import net.sf.jsqlparser.JSQLParserException;

public class DbDemo
{
    public static void main(String[] args) throws JSQLParserException {
        DataBase dataBase = new DataBase();
        String myTable ="CREATE TABLE MYFAMILY"
                + "("
                + "FirstName varchar(255),"
                + "MiddleName varchar(255),"
                + "LastName varchar(255),"
                + "Id int NOT NULL,"
                + "Age int,"
                + "LAH boolean default(false),"
                + "PRIMARY KEY (Id)"
                +");"
                ;

        String sruli = "INSERT INTO MYFAMILY(LAH,Id,FirstName,MiddleName,Age) VALUES(true,1,'sruli','a',23)";
        String deeni = "INSERT INTO MYFAMILY(LAH,Id,FirstName,MiddleName,LastName,Age) VALUES(false,12,'deeni','a','rubin',23)";
        String yechiel = "INSERT INTO MYFAMILY(LAH,Id,FirstName,MiddleName,LastName,Age) VALUES(false,2,'yechiel','a','schaffel',22)";
        String daniel = "INSERT INTO MYFAMILY(LAH,Id,FirstName,MiddleName,LastName,Age) VALUES(false,3,'daniel','a','schaffel',20)";
        String chana = "INSERT INTO MYFAMILY(LAH,Id,FirstName,MiddleName,LastName,Age) VALUES(false,4,'chana','a','glassberg',18)";
        String chaya = "INSERT INTO MYFAMILY(LAH,Id,FirstName,MiddleName,LastName,Age) VALUES(true,5,'chaya','a','schaffel',16)";
        String yitzi = "INSERT INTO MYFAMILY(LAH,Id,FirstName,MiddleName,LastName,Age) VALUES(false,6,'yitzi','a','glassberg',14)";
        String avrumi = "INSERT INTO MYFAMILY(LAH,Id,FirstName,MiddleName,LastName,Age) VALUES(true,7,'avrumi','a','schaffel',12)";
        String fredi = "INSERT INTO MYFAMILY(LAH,Id,FirstName,MiddleName,LastName,Age) VALUES(true,8,'freidi','a','schaffel',10)";
        String yaakov = "INSERT INTO MYFAMILY(LAH,Id,FirstName,MiddleName,LastName,Age) VALUES(true,9,'yaakov','a','glassberg',8)";
        String nossi = "INSERT INTO MYFAMILY(LAH,Id,FirstName,MiddleName,LastName,Age) VALUES(true,10,'nossi','a','schaffel',6)";
        String shlomie = "INSERT INTO MYFAMILY(LAH,Id,FirstName,MiddleName,LastName,Age) VALUES(true,11,'shlomie','a','schaffel',2)";
        String naomi = "insert into MYFAMILY(LAH,Id,FirstName,MiddleName,LastName,Age)values(false,13,'naomi','rochel','montrose',18);";


        String multipleWhere = "SELECT Id,LastName from MYFAMILY where Id>1 and LastName=schaffel;";
        String delete = "Delete from MYFAMILY where Age > 15;";
        String update = "update MYFAMILY set Age=1,LAH=false where Age>20;";
        String select = "select * from MYFAMILY;";
        String average = "select AVG(distinct Age) from MYFAMILY;";
        String orderBy = "select FirstName,MiddleName,LastName from MYFAMILY order by LastName;";
        String maxFunction = "select MAX(FirstName) from MYFAMILY;";
        String minFunction = "select FirstName,MIN(Age) from MYFAMILY;";
        String countFunction = "select COUNT(distinct Age) from MYFAMILY;";
        String sumFunction = "select SUM(distinct Age) from MYFAMILY;";
        String selectDistinct = "select distinct LastName from MYFAMILY;";
        String createLastNameIndex = "create index Last_Name on MYFAMILY (LastName);";

        dataBase.execute(myTable);
        dataBase.execute(sruli);
        dataBase.execute(deeni);
        dataBase.execute(yechiel);
        dataBase.execute(daniel);
        dataBase.execute(chana);
        dataBase.execute(chaya);
        dataBase.execute(yitzi);
        dataBase.execute(avrumi);
        dataBase.execute(fredi);
        dataBase.execute(yaakov);
        dataBase.execute(nossi);
        dataBase.execute(shlomie);
        dataBase.execute(naomi);

        dataBase.execute(createLastNameIndex);


        System.out.println(select);
        dataBase.execute(select).print();
        System.out.println();
        System.out.println(selectDistinct);
        dataBase.execute(selectDistinct).print();
        System.out.println();
        System.out.println(multipleWhere);
        dataBase.execute(multipleWhere).print();
        System.out.println();
        ResultSet max = dataBase.execute(maxFunction);
        System.out.println(maxFunction);
        max.print();
        System.out.println();
        ResultSet min = dataBase.execute(minFunction);
        System.out.println(minFunction);
        min.print();
        System.out.println();
        System.out.println(orderBy);
        dataBase.execute(orderBy).print();
        System.out.println();
        System.out.println(countFunction);
        dataBase.execute(countFunction).print();
        System.out.println();
        System.out.println(average);
        dataBase.execute(average).print();
        System.out.println();
        System.out.println(sumFunction);
        dataBase.execute(sumFunction).print();
        System.out.println();
        System.out.println(update);
        dataBase.execute(update).print();
        dataBase.print();
        System.out.println();
        System.out.println(delete);
        dataBase.execute(delete).print();
        dataBase.print();


    }
}
