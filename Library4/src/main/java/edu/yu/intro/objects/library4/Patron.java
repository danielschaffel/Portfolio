package edu.yu.intro.objects.library4;
import java.util.*;
/**
@Daniel Schaffel


*/

public class Patron
{
  final private String firstName;
  final private String lastName;
  final private  String address;
  //Set<Patron> patrons = new HashSet<>();
  final private String uniqueID;
  final private ArrayList<Book> booksOutByPatron;
  
 public Patron(String firstName,String lastName,String address)
 {
   if(firstName == null || firstName == "")
   {
     throw new IllegalArgumentException("Invalid input: First name can not be empty");
   }

   if(lastName == null || lastName == "")
   {
     throw new IllegalArgumentException("Invalid input: Last name can't be empty");
   }

   if(address == null || address == "")
   {
     throw new IllegalArgumentException("Invalid input: Address can't be empty.");
   }

  this.firstName = firstName;
  this.lastName = lastName;
  this.address = address;
  UUID id = UUID.randomUUID();
  uniqueID = id.toString();
  booksOutByPatron = new ArrayList<>();
  //"" + this.makeIdNumber(firstName,lastName,address);
  //
 }

 public String getFirstName()
 {
   return this.firstName;
 }

 public String getLastName()
 {
   return this.lastName;
 }

 public String getAddress()
 {
   return this.address;
 }

 public String getId()
 {
   return uniqueID;
 }

 private long makeIdNumber(String firstNam, String lastNam, String addres)
 {
   String string = firstNam + lastNam + addres;
   return Objects.hash(string);
 }

 public void add(Book b)//used in library for borrow and onLoan
 {
   booksOutByPatron.add(b);
 }

 public List getBooksOutByPatron()//used in library for borrow and onLoan
 {
   return booksOutByPatron;
 }

 public int nBooksInPatron()
 {
   int numberOfBooks = 0;
  for (Book b:booksOutByPatron)
  {
    if (b != null)
    {
      numberOfBooks++;
    }
  }
  return numberOfBooks;
 }

 public void clearBook()
 {
   booksOutByPatron.clear();
 }

 @Override
 public int hashCode()
 {
   return Objects.hash(uniqueID);
 }

 @Override
 public String toString()
 {
   return getClass().getName() + ", First Name: " + this.firstName + ", Last name: " + this.lastName + ", Address: " + this.address + ", Patron Id: " + this.uniqueID;
 }

 @Override
 public boolean equals(Object that)
 {
  if (this == that)
  {
    return true;
  }

  if (that == null)
  {
    return false;
  }

  if (getClass() != that.getClass())
  {
    return false;
  }

  Patron otherPatron = (Patron) that;

  return this.getId() == otherPatron.getId();
 }
}
