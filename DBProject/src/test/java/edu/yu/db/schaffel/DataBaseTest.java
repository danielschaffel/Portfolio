package edu.yu.db.schaffel;

import org.junit.Test;

import static org.junit.Assert.*;

public class DataBaseTest {

  @Test
    public void create()
  {
      String family = "create table family(firstname varchar(255),middlename varchar(255),lastname varchar(255),age decimal,home boolean,numberinfam int, primary key (numberinfam))";
      DataBase db = new DataBase();
      db.execute(family);
      Table familyTable = db.getTable("family");
      assertTrue(familyTable.containsColumn("firstname"));
      assertTrue(familyTable.containsColumn("middlename"));
      assertTrue(familyTable.containsColumn("lastname"));
      assertTrue(familyTable.containsColumn("age"));
      assertTrue(familyTable.containsColumn("home"));
      assertTrue(familyTable.containsColumn("numberinfam"));
  }

  @Test
    public void insert()
  {
      DataBase db = new DataBase();
      String family = "create table family(firstname varchar(255),middlename varchar(255),lastname varchar(255),age decimal,home boolean,numberinfam int, primary key (numberinfam));";
      String daniel = "insert into family(firstname,middlename,lastname,age,home,numberinfam) values ('daniel','ephraim','schaffel',20.5,false,3);";
      db.execute(family);
      db.execute(daniel);
      Table familyTable = db.getTable("family");
      assertEquals(1,db.getTableSize("family"));
      assertEquals(new Cell("daniel"),db.getTable("family").get(0).get("firstname"));
      assertEquals(new Cell("ephraim"),db.getTable("family").get(0).get("middlename"));
      assertEquals(new Cell("schaffel"),db.getTable("family").get(0).get("lastname"));
      assertEquals(new Cell(20.5),db.getTable("family").get(0).get("age"));
      assertEquals(new Cell(false),db.getTable("family").get(0).get("home"));
      assertEquals(new Cell(3),db.getTable("family").get(0).get("numberinfam"));
      assertTrue(db.execute("insert into fam(firstname,middlename,lastname,age,home,numberinfam) values ('daniel','ephraim','schaffel',20.5,false,3);").isFalse());//returns false if table doesnt exist
     ResultSet te = db.execute(daniel);
      assertTrue(db.execute(daniel).isFalse());
  }

  @Test
    public void update()
  {
      DataBase db = new DataBase();
      String family = "create table family(firstname varchar(255),middlename varchar(255),lastname varchar(255),age decimal,home boolean,numberinfam int, primary key (numberinfam));";
      String daniel = "insert into family(firstname,middlename,lastname,age,home,numberinfam) values ('daniel','ephraim','schaffel',20.5,false,3);";
      String sruli = "INSERT INTO family(home,numberinfam,firstname,middlename,lastname,age) VALUES(true,1,'sruli','a','schaffel',23);";
      String deeni = "INSERT INTO family(home,numberinfam,firstname,middlename,lastname,age) VALUES(false,12,'deeni','a','rubin',23);";
      String yechiel = "INSERT INTO family(home,numberinfam,firstname,middlename,lastname,age) VALUES(false,2,'yechiel','a','schaffel',5);";
      String chana = "INSERT INTO family(home,numberinfam,firstname,middlename,lastname,age) VALUES(false,4,'chana','a','glassberg',18);";
      String chaya = "INSERT INTO family(home,numberinfam,firstname,middlename,lastname,age) VALUES(true,5,'chaya','a','schaffel',16);";
      String yitzi = "INSERT INTO family(home,numberinfam,firstname,middlename,lastname,age) VALUES(false,6,'yitzi','a','glassberg',14);";
      String avrumi = "INSERT INTO family(home,numberinfam,firstname,middlename,lastname,age) VALUES(true,7,'avrumi','a','schaffel',12);";
      String fredi = "INSERT INTO family(home,numberinfam,firstname,middlename,lastname,age) VALUES(true,8,'freidi','a','schaffel',10);";
      String yaakov = "INSERT INTO family(home,numberinfam,firstname,middlename,lastname,age) VALUES(true,9,'yaakov','a','glassberg',8);";
      String nossi = "INSERT INTO family(home,numberinfam,firstname,middlename,lastname,age) VALUES(true,10,'nossi','a','schaffel',6);";
      String shlomie = "INSERT INTO family(home,numberinfam,firstname,middlename,lastname,age) VALUES(true,11,'shlomie','a','schaffel',2);";
      String naomi = "insert into family(home,numberinfam,firstname,middlename,lastname,age) values(false,13,'naomi','rochel','montrose',18);";

      String update1 = "update family set age=100 where age>20;";



      db.execute(family);
      db.execute(daniel);
      db.execute(yitzi);
      db.execute(chana);
      db.execute(fredi);
      db.execute(nossi);
      db.execute(deeni);
      db.execute(avrumi);
      db.execute(yechiel);
      db.execute(shlomie);
      db.execute(chaya);
      db.execute(yaakov);
      db.execute(sruli);
      db.execute(naomi);

      assertEquals(13,db.getTableSize("family"));
       db.execute(update1);
      Table afterUpdateTable = db.getTable("family");
      boolean checkUpdate = true;
      for(int i=0;i<afterUpdateTable.size();i++)
      {
          if((double)afterUpdateTable.get(i).get("age").getValue()>20 && !afterUpdateTable.get(i).get("age").getValue().equals(100.0))
              checkUpdate = false;
      }
      assertTrue(checkUpdate);

  }

  @Test
    public void delete()
  {
      DataBase db = new DataBase();
      String family = "create table family(firstname varchar(255),middlename varchar(255),lastname varchar(255),age decimal,home boolean,numberinfam int, primary key (numberinfam));";
      String daniel = "insert into family(firstname,middlename,lastname,age,home,numberinfam) values ('daniel','ephraim','schaffel',20.5,false,3);";
      String sruli = "INSERT INTO family(home,numberinfam,firstname,middlename,lastname,age) VALUES(true,1,'sruli','a','schaffel',23);";
      String deeni = "INSERT INTO family(home,numberinfam,firstname,middlename,lastname,age) VALUES(false,12,'deeni','a','rubin',23);";
      String yechiel = "INSERT INTO family(home,numberinfam,firstname,middlename,lastname,age) VALUES(false,2,'yechiel','a','schaffel',22);";
      String chana = "INSERT INTO family(home,numberinfam,firstname,middlename,lastname,age) VALUES(false,4,'chana','a','glassberg',18);";
      String chaya = "INSERT INTO family(home,numberinfam,firstname,middlename,lastname,age) VALUES(true,5,'chaya','a','schaffel',16);";
      String yitzi = "INSERT INTO family(home,numberinfam,firstname,middlename,lastname,age) VALUES(false,6,'yitzi','a','glassberg',14);";
      String avrumi = "INSERT INTO family(home,numberinfam,firstname,middlename,lastname,age) VALUES(true,7,'avrumi','a','schaffel',12);";
      String fredi = "INSERT INTO family(home,numberinfam,firstname,middlename,lastname,age) VALUES(true,8,'freidi','a','schaffel',10);";
      String yaakov = "INSERT INTO family(home,numberinfam,firstname,middlename,lastname,age) VALUES(true,9,'yaakov','a','glassberg',8);";
      String nossi = "INSERT INTO family(home,numberinfam,firstname,middlename,lastname,age) VALUES(true,10,'nossi','a','schaffel',6);";
      String shlomie = "INSERT INTO family(home,numberinfam,firstname,middlename,lastname,age) VALUES(true,11,'shlomie','a','schaffel',2);";
      String naomi = "insert into family(home,numberinfam,firstname,middlename,lastname,age) values(false,13,'naomi','rochel','montrose',18);";

      db.execute(family);
      db.execute(daniel);
      db.execute(yitzi);
      db.execute(chana);
      db.execute(fredi);
      db.execute(nossi);
      db.execute(deeni);
      db.execute(avrumi);
      db.execute(yechiel);
      db.execute(shlomie);
      db.execute(chaya);
      db.execute(yaakov);
      db.execute(sruli);
      db.execute(naomi);

      String delete = "delete family where age>20 or lastname=glassberg;";
      String selectToCheckUpdate = "select age from family where age=100;";

      db.execute(delete);
      boolean lastNameCantBeGlassberg = true;
      Table deletedTable = db.getTable("family");
      for(int i=0;i<db.getTableSize("family");i++)
      {
          if(deletedTable.get(i).get("lastname").getValue().equals("glassberg"))
              lastNameCantBeGlassberg = false;

      }
      assertTrue(lastNameCantBeGlassberg);
  }

  //most of my heavy testing will be done with select
    //this is because all of the other functions use the same where methods
    //so would be pointless to be doing heavy testing on all of them if
    //i would know that there is a problem from testing just one of the methods
    // for the where condition
  @Test
    public void select()
  {
      DataBase db = new DataBase();
      String family = "create table family(firstname varchar(255),middlename varchar(255),lastname varchar(255),age decimal,home boolean,numberinfam int, primary key (numberinfam));";
      String daniel = "insert into family(firstname,middlename,lastname,age,home,numberinfam) values ('daniel','ephraim','schaffel',20,false,3);";
      String sruli = "INSERT INTO family(home,numberinfam,firstname,middlename,lastname,age) VALUES(true,1,'sruli','a','schaffel',23);";
      String deeni = "INSERT INTO family(home,numberinfam,firstname,middlename,lastname,age) VALUES(false,12,'deeni','a','rubin',23);";
      String yechiel = "INSERT INTO family(home,numberinfam,firstname,middlename,lastname,age) VALUES(false,2,'yechiel','a','schaffel',22);";
      String chana = "INSERT INTO family(home,numberinfam,firstname,middlename,lastname,age) VALUES(false,4,'chana','a','glassberg',18);";
      String chaya = "INSERT INTO family(home,numberinfam,firstname,middlename,lastname,age) VALUES(true,5,'chaya','a','schaffel',16);";
      String yitzi = "INSERT INTO family(home,numberinfam,firstname,middlename,lastname,age) VALUES(false,6,'yitzi','a','glassberg',14);";
      String avrumi = "INSERT INTO family(home,numberinfam,firstname,middlename,lastname,age) VALUES(true,7,'avrumi','a','schaffel',12);";
      String fredi = "INSERT INTO family(home,numberinfam,firstname,middlename,lastname,age) VALUES(true,8,'freidi','a','schaffel',10);";
      String yaakov = "INSERT INTO family(home,numberinfam,firstname,middlename,lastname,age) VALUES(true,9,'yaakov','a','glassberg',8);";
      String nossi = "INSERT INTO family(home,numberinfam,firstname,middlename,lastname,age) VALUES(true,10,'nossi','a','schaffel',6);";
      String shlomie = "INSERT INTO family(home,numberinfam,firstname,middlename,lastname,age) VALUES(true,11,'shlomie','a','schaffel',2);";
      String naomi = "insert into family(home,numberinfam,firstname,middlename,lastname,age) values(false,13,'naomi','rochel','montrose',18);";

      db.execute(family);
      db.execute(daniel);
      db.execute(yitzi);
      db.execute(chana);
      db.execute(fredi);
      db.execute(nossi);
      db.execute(deeni);
      db.execute(avrumi);
      db.execute(yechiel);
      db.execute(shlomie);
      db.execute(chaya);
      db.execute(yaakov);
      db.execute(sruli);
      db.execute(naomi);





      String selectAndOrAnd = "Select * from family where age=18 or numberinfam<7 and lastname=schaffel;";
      ResultSet select1 = db.execute(selectAndOrAnd);
      boolean select1Checker = false;
      Table select1table = select1.getTable();
      for(int i=1;i<select1table.size();i++)
      {
          if(select1table.get(i).get("age").getValue().equals(18.0) || (int)select1table.get(i).get("numberinfam").getValue()<7 && select1table.get(i).get("lastname").getValue().equals("schaffel"))
              select1Checker = true;
          else
          {
              select1Checker = false;
              break;
          }
      }
      assertTrue(select1Checker);

      String selectOROrOR = "Select * from family where age>20 or numberinfam>10 or lastname=glassberg;";
      ResultSet select2 = db.execute(selectOROrOR);
      Table select2Table = select2.getTable();
      boolean select2checker = true;
      for(int i=0;i<select2Table.size();i++)
      {
          if(!((double)select2Table.get(i).get("age").getValue()>20 ||(int)select2Table.get(i).get("numberinfam").getValue()>10 || select2Table.get(i).get("lastname").getValue().equals("glassberg")))
              select2checker = false;
      }
     assertTrue(select2checker);

      String selectAndAndAnd = "select * from family where age>20 and lastname=rubin and home=false;";
      ResultSet select3 = db.execute(selectAndAndAnd);
      Table select3table = select3.getTable();
      boolean select3checker = true;
      for(int i=0;i<select3table.size();i++)
      {
          if(!((double)select3table.get(i).get("age").getValue()>20.0 && select3table.get(i).get("lastname").getValue().equals("rubin") && select3table.get(i).get("home").getValue().equals(false)))
              select3checker = false;
      }
      assertTrue(select3checker);


      String distinct = "select distinct age from family;";
      ResultSet distinct1 = db.execute(distinct);
      assertEquals(11,distinct1.getSize());
  }

  @Test
    public void functions()
  {
      DataBase db = new DataBase();
      String family = "create table family(firstname varchar(255),middlename varchar(255),lastname varchar(255),age decimal,home boolean,numberinfam int, primary key (numberinfam));";
      String daniel = "insert into family(firstname,middlename,lastname,age,home,numberinfam) values ('daniel','ephraim','schaffel',20.0,false,3);";
      String sruli = "INSERT INTO family(home,numberinfam,firstname,middlename,lastname,age) VALUES(true,1,'sruli','a','schaffel',23);";
      String deeni = "INSERT INTO family(home,numberinfam,firstname,middlename,lastname,age) VALUES(false,12,'deeni','a','rubin',23);";
      String yechiel = "INSERT INTO family(home,numberinfam,firstname,middlename,lastname,age) VALUES(false,2,'yechiel','a','schaffel',22);";
      String chana = "INSERT INTO family(home,numberinfam,firstname,middlename,lastname,age) VALUES(false,4,'chana','a','glassberg',18);";
      String chaya = "INSERT INTO family(home,numberinfam,firstname,middlename,lastname,age) VALUES(true,5,'chaya','a','schaffel',16);";
      String yitzi = "INSERT INTO family(home,numberinfam,firstname,middlename,lastname,age) VALUES(false,6,'yitzi','a','glassberg',14);";
      String avrumi = "INSERT INTO family(home,numberinfam,firstname,middlename,lastname,age) VALUES(true,7,'avrumi','a','schaffel',12);";
      String fredi = "INSERT INTO family(home,numberinfam,firstname,middlename,lastname,age) VALUES(true,8,'freidi','a','schaffel',10);";
      String yaakov = "INSERT INTO family(home,numberinfam,firstname,middlename,lastname,age) VALUES(true,9,'yaakov','a','glassberg',8);";
      String nossi = "INSERT INTO family(home,numberinfam,firstname,middlename,lastname,age) VALUES(true,10,'nossi','a','schaffel',6);";
      String shlomie = "INSERT INTO family(home,numberinfam,firstname,middlename,lastname,age) VALUES(true,11,'shlomie','a','schaffel',2);";
      String naomi = "insert into family(home,numberinfam,firstname,middlename,lastname,age) values(false,13,'naomi','rochel','montrose',18);";

      db.execute(family);
      db.execute(daniel);
      db.execute(yitzi);
      db.execute(chana);
      db.execute(fredi);
      db.execute(nossi);
      db.execute(deeni);
      db.execute(avrumi);
      db.execute(yechiel);
      db.execute(shlomie);
      db.execute(chaya);
      db.execute(yaakov);
      db.execute(sruli);
      db.execute(naomi);

      String sum = "select SUM(age) from family;";
      ResultSet sumResult = db.execute(sum);
      assertEquals(192.0,sumResult.getTable().get(0).get(0).getValue());

      String avg = "select AVG(age) from family;";
      ResultSet avgResult = db.execute(avg);
      assertEquals(14.76923076923077,avgResult.getTable().get(0).get(0).getValue());

      String maxDouble = "select MAX(age) from family;";
      ResultSet maxResult = db.execute(maxDouble);
      assertEquals(23.0,maxResult.getTable().get(0).get(0).getValue());

      String min ="select MIN(age) from family;";
      ResultSet minResult = db.execute(min);
      assertEquals(2.0,minResult.getTable().get(0).get(0).getValue());

      String count ="select COUNT(lastname) from family;";
      ResultSet countResult = db.execute(count);
      assertEquals("13",countResult.getTable().get(0).get(0).getValue());// the value is returned as a string if it is in a varchar column

      String maxName = "select MAX(firstname) from family;";
      ResultSet maxNameResult = db.execute(maxName);
      assertEquals("yitzi",maxNameResult.getTable().get(0).get(0).getValue());

      String minName = "select MIN(firstname) from family";
      ResultSet minNameResult = db.execute(minName);
      assertEquals("avrumi",minNameResult.getTable().get(0).get(0).getValue());
  }

  @Test
    public void functionsWithDistinct()
  {
      DataBase db = new DataBase();
      String family = "create table family(firstname varchar(255),middlename varchar(255),lastname varchar(255),age decimal,home boolean,numberinfam int, primary key (numberinfam));";
      String chana = "INSERT INTO family(home,numberinfam,firstname,middlename,lastname,age) VALUES(false,4,'chana','a','glassberg',18);";
      String naomi = "insert into family(home,numberinfam,firstname,middlename,lastname,age) values(false,13,'naomi','rochel','montrose',18);";
      String fredi = "INSERT INTO family(home,numberinfam,firstname,middlename,lastname,age) VALUES(true,8,'freidi','a','schaffel',10);";
      String daniel = "insert into family(firstname,middlename,lastname,age,home,numberinfam) values ('daniel','ephraim','schaffel',20,false,3);";

      db.execute(family);
      db.execute(fredi);
      db.execute(daniel);

      String distinctSum ="select SUM(distinct age) from family;";
      ResultSet distinctSumResult = db.execute(distinctSum);
      Table distinctSumTable = distinctSumResult.getTable();
      assertEquals(30.0,distinctSumTable.get(0).get("age").getValue());

      String distinctAvg = "select AVG(distinct age) from family;";
      ResultSet distinctAVGResult = db.execute(distinctAvg);
      Table distinctAVgTable =  distinctAVGResult.getTable();
      assertEquals(15.0,distinctAVgTable.get(0).get(0).getValue());

      db.execute(naomi);
      db.execute(chana);

      String distintCount = "select COUNT(distinct middlename) from family;";
      ResultSet distinctNameResult = db.execute(distintCount);
      assertEquals("3",distinctNameResult.getTable().get(0).get(0).getValue());
  }

  @Test
    public void orderBy()
  {
      DataBase db = new DataBase();
      String family = "create table family(firstname varchar(255),middlename varchar(255),lastname varchar(255),age decimal,home boolean,numberinfam int, primary key (numberinfam));";
      String chana = "INSERT INTO family(home,numberinfam,firstname,middlename,lastname,age) VALUES(false,4,'chana','a','glassberg',18.0);";
      String naomi = "insert into family(home,numberinfam,firstname,middlename,lastname,age) values(false,13,'naomi','rochel','montrose',18.0);";

      db.execute(family);
      db.execute(chana);
      db.execute(naomi);

      String orderByFirstName = "select * from family order by firstname desc;";
      ResultSet orderby = db.execute(orderByFirstName);
      Table orderByTable = orderby.getTable();
      boolean checker = true;
      for(int i=0;i<orderByTable.size()-1;i++)
      {
          if(orderByTable.get(i).get("firstname").compareTo(orderByTable.get(i+1).get("firstname"))>=0)
              checker=false;
      }
      assertTrue(checker);

      String fredi = "INSERT INTO family(home,numberinfam,firstname,middlename,lastname,age) VALUES(true,8,'freidi','a','schaffel',10);";
      db.execute(fredi);

      String multipleOrderBy = "select * from family order by age,lastname;";
      ResultSet multipleOrderBySet = db.execute(multipleOrderBy);
      Table multiOrderByTable = multipleOrderBySet.getTable();
      assertEquals(10.0,multiOrderByTable.get(0).get("age").getValue());
      assertEquals("glassberg",multiOrderByTable.get(1).get("lastname").getValue());
      assertEquals("montrose",multiOrderByTable.get(2).get("lastname").getValue());
      assertEquals(new ResultSet("false").isFalse(),db.execute("select * from fam;").isFalse());
  }

  @Test
    public void testingFailedQuery()
  {
      DataBase db = new DataBase();
      String family = "create table family(firstname varchar(255) not null,middlename varchar(255),lastname varchar(10),age decimal(2,1),home boolean,numberinfam int, primary key (numberinfam));";
      String naomi = "insert into family(home,numberinfam,firstname,middlename,lastname,age) values(false,13,'naomi','rochel','montrose',18.0);";
      String wrongTable = "insert into fam(home,numberinfam,firstname,middlename,lastname,age) values(false,13,'naomi','rochel','montrose',18.0);";
      String notUnique = "insert into family(home,numberinfam,firstname,middlename,lastname,age) values(false,13,'naomi','rochel','montrose',18.0);";
      String notNull = "insert into family(home,numberinfam,middlename,lastname,age) values(false,13,'rochel','montrose',18.0);";
      String toLongString = "insert into family(home,numberinfam,firstname,middlename,lastname,age) values(false,13,'naomi','rochel','montrose98m',18.0);";
      String toLongDecimalBefore = "insert into family(home,numberinfam,firstname,middlename,lastname,age) values(false,13,'naomi','rochel','montrose',518.0);";
      String selectFromNonExistantColumn = "select secondname from family;";
      db.execute(family);
      db.execute(naomi);

      assertTrue(db.execute(wrongTable).isFalse());
      assertTrue(db.execute(notUnique).isFalse());
      assertTrue(db.execute(notNull).isFalse());
      assertTrue(db.execute(toLongDecimalBefore).isFalse());
      assertTrue(db.execute(toLongString).isFalse());
      assertTrue(db.execute(selectFromNonExistantColumn).isFalse());// this tests for all queries having non existant columns
  }
}