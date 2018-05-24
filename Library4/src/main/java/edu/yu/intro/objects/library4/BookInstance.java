package edu.yu.intro.objects.library4;
import java.util.*;
public class BookInstance {

  final private String title;
  final private String author;
  final private long isbn;
  final private String bookFormat;
  private LoanState loanState;
  final private String uniqueID;
  final private Book thisBook;
  private Patron thisPatron;
  private Library library;

  public BookInstance(Book book)
  {
    if(book == null)
    {
      throw new IllegalArgumentException("Book can not be null");
    }
    this.title = book.getTitle();
    this.author = book.getAuthor();
    this.isbn = book.getISBN13();
    this.bookFormat = book.getBookType();
    this.uniqueID = UUID.randomUUID().toString();
    this.loanState = LoanState.AVAILABLE;
    this.thisBook = book;
    this.thisPatron = null;
    this.library = null;
  }

  public String getId()
  {
    return this.uniqueID;
  }

  public LoanState getLoanState()
  {
    return this.loanState;
  }

  public Book getBook()
  {
    return this.thisBook;
  }

  public String getBookType()
  {
    return this.bookFormat;
  }

  public long getISBN13()
  {
    return this.isbn;
  }

  public Patron getPatron()
  {
    return this.thisPatron;
  }

  public Library getLibrary()
  {
    return this.library;
  }

  public void setLibrary(Library library)
  {
    this.library = library;
  }

  public void setPatron(Patron patron)// this is to associate a patron with books not just by adding to personal collection
  {
    this.thisPatron = patron;
  }

  public void setLoanState(LoanState loanState)
  {
    this.loanState = loanState;
  }

  public void borrow(Patron patron) throws OnLoanException
  {
    if (this.getLoanState().equals(LoanState.ON_RESERVE))
    {
      throw new OnLoanException("Book is on hold already");
    }

    if(this.getLoanState().equals(LoanState.BORROWED))
    {
      throw new OnLoanException("Book is already borrowed");
    }

    if (patron == null)
    {
      throw new IllegalArgumentException("Patron can not be null");
    }

    this.setLoanState(LoanState.BORROWED);
    this.setPatron(patron);
  }

  public void reserve(Patron patron)
  {
    if(patron == null)
    {
      throw new IllegalArgumentException("Patron can not be null.");
    }

    if(this.loanState == LoanState.BORROWED)
    {
      throw new IllegalArgumentException("Book is borrowed.");
    }
    if(!this.library.getPatrons().contains(patron))
    {
      throw new IllegalArgumentException("Book and patron are not part of the same library.");
    }

    if(this.loanState == LoanState.ON_RESERVE && this.thisPatron != patron)
    {
      throw new IllegalArgumentException("This book is on hold by another patron.");
    }


    this.setLoanState(loanState.ON_RESERVE);
    this.setPatron(patron);
  }

  public void returnInstance(Patron patron)throws NotOnLoanException
  {
    if(patron == null)
    {
      throw new IllegalArgumentException("Can not be null");
    }
    if(this.getLoanState() != LoanState.BORROWED)
    {
      throw new NotOnLoanException("This book is was has not been borrowed from the library.");
    }

    if(this.getPatron() != patron)
    {
      throw new NotOnLoanException("This book is borrowed by another patron.");
    }
    this.setLoanState(LoanState.AVAILABLE);
    this.setPatron(null);
  }
  @Override
  public int hashCode()
  {
    return Objects.hash(uniqueID);
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

    BookInstance otherBook = (BookInstance) that;

    return this.getId()==otherBook.getId();
  }

  @Override
  public String toString()
  {
    return getClass() + " Title: " + this.title + ", Author: " + this.author + ", Book Format: " + this.bookFormat + " Isbn: " + isbn + ", Book ID; " + this.uniqueID + ", Loan State: " + this.loanState + " Patron: " + this.getPatron();
  }


}
