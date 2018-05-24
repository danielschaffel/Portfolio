package edu.yu.intro.objects.library4;

public class BookFilter
{
  private final String author;
  private final String title;
  private final long isbn13;
  private final String bookType;
  private BookFilter(Builder builder)
  {
   this.author = builder.author;
   this.title = builder.title;
   this.isbn13 = builder.isbn13;
   this.bookType = builder.bookType;
  }

  public boolean filter(Book book)//  PARTIALLY TESTED
  {
    boolean result = true;
    if(this.getAuthor() != null)
    {
      if(this.getAuthor().equals(book.getAuthor()) )
      {
        result = true;
      }
      else
      {
        result = false;
        return result;
      }
    }

    if(this.getTitle() != null)
    {
      if(this.getTitle().equals(book.getTitle()))
      {
        result = true;
      }
      else
      {
        result = false;
        return result;
      }
    }

    if(this.getISBN13() != 0)
    {
      if(this.getISBN13() == book.getISBN13())
      {
        result = true;
      }
      else
      {
        result = false;
        return result;
      }
    }

    if(this.getBookType() != null)
    {
      if(this.getBookType().equals(book.getBookType()))
      {
        result = true;
      }
      else
      {
        result = false;
        return result;
      }
    }
    return result;
  }

  public String getAuthor()
  {
    return author;
  }

  public String getTitle()
  {
    return title;
  }

  public long getISBN13()
  {
    return isbn13;
  }

  public String getBookType()
  {
    return bookType;
  }

  @Override
  public String toString()
  {
    return getClass() + " Title: " + this.title + ", Author: " + this.author + ", Book Format: " + this.bookType + " Isbn: " + isbn13;
  }

  public static class Builder
  {
    String author;
    String title;
    long isbn13;
    String bookType;

    public Builder()
    {

    }

    public Builder setAuthor(String author)
    {
      if(author == null)
      {
        throw new IllegalArgumentException("Can not search for null");
      }
      this.author = author;
      return this;
    }

    public Builder setTitle(String title)
    {
      if(title == null)
      {
        throw new IllegalArgumentException("Can not search for null");
      }
      this.title = title;
      return this;
    }

    public Builder setISBN13(long isbn13)
    {
      this.isbn13 = isbn13;
      return this;
    }

    public Builder setBookType(String bookType)
    {
      if(bookType != "paperback" && bookType != "hardcover" && bookType != "ebook")
      {
        throw new IllegalArgumentException("Invalid book format");
      }
      this.bookType = bookType;
      return this;
    }

    public String getAuthor()
    {
      return author;
    }

    public String getTitle()
    {
      return title;
    }

    public long getISBN13()
    {
      return isbn13;
    }

    public String getBookType()
    {
      return bookType;
    }

    public BookFilter build()
    {
      return new BookFilter(this);
    }

    @Override
    public String toString()
    {
      return getClass() + " Title: " + this.title + ", Author: " + this.author + ", Book Format: " + this.bookType + " Isbn: " + isbn13;
    }
  }
}
