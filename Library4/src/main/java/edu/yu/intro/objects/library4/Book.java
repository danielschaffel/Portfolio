package edu.yu.intro.objects.library4;
import java.util.*;
/**
@Daniel Schaffel
*/

public class Book
{

  final private String title;
  final private String author;
  final private long isbn;
  final private String bookFormat;

  public Book(String title,String author, long isbn,String bookFormat)
  {
    String isbnString = String.valueOf(isbn);
    if(isbnString.length() != 13)
    {
      throw new IllegalArgumentException("Invalid ISBN number, must be 13 digits");
    }

    if(bookFormat != "paperback" && bookFormat != "hardcover" && bookFormat != "ebook")
    {
      throw new IllegalArgumentException("Invalid book format");
    }

    if (title == null || title == "")
    {
      throw new IllegalArgumentException("Title can not be null or empty");
    }

    if (author == null || author == "")
    {
      throw new IllegalArgumentException("Author can not be null or empty");
    }
    this.title = title;
    this.author = author;
    this.isbn = isbn;
    this.bookFormat = bookFormat;
  }

  public String getTitle()
  {
    return this.title;
  }
  public String getAuthor()
  {
    return this.author;
  }
  public String getBookType()
  {
    return this.bookFormat;
  }
  public long getISBN13()
  {
    return this.isbn;
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

   Book otherBook = (Book) that;

   return this.getISBN13()==otherBook.getISBN13();
  }

  @Override
  public int hashCode()
  {
    return Objects.hash(isbn);
  }

  @Override
  public String toString()
  {
    return getClass() + " Title: " + this.title + ", Author: " + this.author + ", Book Format: " + this.bookFormat + " Isbn: " + isbn;
  }
}
