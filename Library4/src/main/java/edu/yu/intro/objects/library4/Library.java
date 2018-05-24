package edu.yu.intro.objects.library4;
import java.util.*;
/**
 @Daniel Schaffel
 */

public class Library
{

  private Set<Book> holdings;
  private Set<Patron> patrons;
  private Map<Book,HashSet<BookInstance>> uniqueHoldings;
  private Map<Patron,Set<BookInstance>> patronHoldings;
  /**
   @param holdings This arrayList is holds all of the books in the libraries system
   **/
  //static Library library;
  final private String name;
  final private String address;
  final private String phoneNumber;

  public Library(String name,String address,String phoneNumber)
  {
    if(name == null || name == "")
    {
      throw new IllegalArgumentException("Invalid input: Library name can't be empty.");
    }
    if(address == null || address == "")
    {
      throw new IllegalArgumentException("Invalid input: Address cant't be empty.");
    }
    if(phoneNumber == null || phoneNumber == "")
    {
      throw new IllegalArgumentException("Invalid input: Phone Number can't be empty");
    }

    this.name = name;
    this.address = address;
    this.phoneNumber = phoneNumber;
    this.holdings = new HashSet<>();
    this.patrons = new HashSet<>();
    this.uniqueHoldings = new HashMap<>();
    this.patronHoldings = new HashMap<>();
  }
  public String getName()
  {
    return this.name;
  }

  public String getAddress()
  {
    return this.address;
  }

  public String getPhoneNumber()
  {
    return this.phoneNumber;
  }

  public Set<Patron> getPatrons()
  {
    return this.patrons;
  }

  public int nBooks()//TESTED
  {
    int numberOfBooks = 0;
    for (Book b:holdings)
    {
      if (b != null)
      {
        numberOfBooks++;
      }
    }
    return numberOfBooks;
  }

  public void getRidOfBooks()//my own method TESTED
  {
    this.holdings.clear();
  }

  public void borrow(Patron patron, Book book)//this adds the book to the patrons arraylist booksOutByPatron TESTED
  {
    if(!this.patrons.contains(patron))
    {
      throw new IllegalArgumentException("Patron is not a member of this library.");
    }
    if(!this.holdings.contains(book))
    {
      throw new RuntimeException("Book not found in the library holdings.");
    }
    patron.add(book);
    //i need to see if the library that borrow is being called on contains the patron but not sure how to do that.
  }

  public Collection<Book> search(BookFilter filter)//NEED TO TEST
  {
    List<Book> searchCollection = new ArrayList<>();
    for(Book b: holdings)
    {
      if(filter.filter(b) == true)
      {
        searchCollection.add(b);
      }
    }
    return searchCollection;
  }

  public void add(Patron patron) //TESTED
  {
    if (patron != null)
    {
      this.patrons.add(patron);
      this.patronHoldings.put(patron,new HashSet<>());
    }

    else throw new IllegalArgumentException("Can not add null patron.");
  }

  public void clearPatrons()//I THINK TESTED WILL HAVE TO LOOK OVER AGAIN.
  {
    for (Patron p : patrons)
    {
      p.clearBook();
    }
    patrons.clear();
  }

  public Patron get(String uuid)//TESTED
  {
    if (uuid == null || uuid == "")
    {
      throw new IllegalArgumentException("ID can't be null or empty");
    }

    for (Patron p: patrons)
    {
      if(p.getId() == uuid)
      {
        return p;
      }
    }
    return null;
  }

  public int nPatrons()//TESTED
  {
    int numberOfPatrons = 0;
    for (Patron b:patrons)
    {
      if (b != null)
      {
        numberOfPatrons++;
      }
    }
    return numberOfPatrons;
  }

  public Set<Patron> byLastNamePrefix(String prefix)//TESTED
  {
    if (prefix == null)
    {
      throw new IllegalArgumentException("Can not search for null prefix.");
    }
    if (prefix == "")
    {
      return patrons;
    }
    Set<Patron> lastNamePrefixSet = new HashSet<Patron>();
    boolean setter = false;// this boolean wil add the patron to the set if it returns true at the end of the for loop
    for (Patron p : patrons)
    {
      char[] checkingFirstLetter = p.getLastName().toCharArray();
      for (int i = 0;i<prefix.length();i++)
      {
        if (checkingFirstLetter[i] == prefix.charAt(i))
        {
          setter = true;
        }
        else if (checkingFirstLetter[i] != prefix.charAt(i))
        {
          setter = false;
          break;
        }
      }
      if(setter == true)
      {
        lastNamePrefixSet.add(p);
      }
    }
    return lastNamePrefixSet;
  }

  public BookInstance add(Book book)//look at testHappyPathBorrow when test this
  //remember to test for set library in bookinstance  or something
  {
    if(book == null)
    {
      throw new IllegalArgumentException("Can not add null book to library");
    }


    BookInstance bookInstance = new BookInstance(book);
    if(uniqueHoldings.get(book) == null)
    {
      uniqueHoldings.put(book,new HashSet<>());
    }
    bookInstance.setLibrary(this);
    holdings.add(book);
    uniqueHoldings.get(book).add(bookInstance);
    return bookInstance;
  }

  public void borrow(Patron patron,BookInstance book) throws OnLoanException
  {
    //look at BookInstance Borrow for tests to do here
    if(patron == null)
    {
      throw new IllegalArgumentException("Patron can not be null");//need to test for this this
    }

    if(book == null)
    {
      throw new IllegalArgumentException("Book can not be null");//need to test for this
    }

    if(!patrons.contains(patron))
    {
      throw new IllegalArgumentException("Patron not member of the this library");// need to test for this
    }

    if(!uniqueHoldings.get(book.getBook()).contains(book))
    {
      throw new IllegalArgumentException("Book is not in the library holdings");
    }

    if(book.getLoanState().equals(LoanState.BORROWED))
    {
      throw new OnLoanException("Book is already on loan ");
    }

    if(book.getLoanState().equals(LoanState.ON_RESERVE) && book.getPatron() != patron)
    {
      throw new OnLoanException("Book is reserved by another patron.");
    }

    if(!patronHoldings.containsKey(patron))
    {
      patronHoldings.put(patron,new HashSet<BookInstance>());
    }
    book.borrow(patron);
    patronHoldings.get(patron).add(book);
  }


  public boolean reserve(Patron patron, Book book)
  {
    if(patron == null)
    {
      throw new IllegalArgumentException("Patron can not be null");//need to test for this this
    }

    if(book == null)
    {
      throw new IllegalArgumentException("Book can not be null");//need to test for this
    }

    if(!holdings.contains(book))
    {
      throw new IllegalArgumentException("Book is not in library holdings");
    }

    if(!patrons.contains(patron))
    {
      throw new IllegalArgumentException("Patron not member of the this library");// need to test for this
    }

    if(!patronHoldings.containsKey(patron))
    {
      patronHoldings.put(patron,new HashSet<BookInstance>());
    }

    for (BookInstance b:uniqueHoldings.get(book))
    {
      if(b.getLoanState() == LoanState.AVAILABLE || b.getLoanState() == LoanState.ON_RESERVE && b.getPatron() == patron)
      {
        b.reserve(patron);
        patronHoldings.get(patron).add(b);
        return true;
      }
    }
    return false;
  }

  public void returnInstance(Patron patron,BookInstance book)throws NotOnLoanException
  {
    if(!patrons.contains(patron))
    {
      throw new IllegalArgumentException("Patron is not a member of the library");
    }
    if(!uniqueHoldings.get(book.getBook()).contains(book))
    {
      throw new IllegalArgumentException("Book is not in library holdings");
    }

    if(book.getPatron() != patron)
    {
      throw new NotOnLoanException("Book is not on loan to this patron");
    }

    if(book.getLoanState() != LoanState.BORROWED)
    {
      throw new NotOnLoanException("This book was never taken out.");
    }

    Book temp = book.getBook();
    for(BookInstance b:uniqueHoldings.get(temp))
    {
      if(b == book)
      {
        book.returnInstance(patron);
        this.patronHoldings.get(patron).remove(book);
      }
    }
  }

  public Set<BookInstance> onLoan(Patron patron)
  {
    if(!patrons.contains(patron))
    {
      throw new IllegalArgumentException("Patron is not a member of the library");
    }

    Set <BookInstance> temp = new HashSet<>();
    for(BookInstance b:patronHoldings.get(patron))
    {
      if(b.getLoanState() == LoanState.BORROWED)
      {
        temp.add(b);
      }
    }

    return temp;
  }

  public Set<BookInstance> onReserve(Patron patron)
  {
    if(!patrons.contains(patron))
    {
      throw new IllegalArgumentException("Patron is not a member of the library");
    }

    Set <BookInstance> temp = new HashSet<>();
    for(BookInstance b:patronHoldings.get(patron))
    {
      if(b.getLoanState() == LoanState.ON_RESERVE)
      {
        temp.add(b);
      }
    }
    return temp;
  }

  public Set<BookInstance> getInstances(Book book)
  {
    Set<BookInstance> temp = new HashSet<>();
    if(!holdings.contains(book))
    {
      return temp;
    }

    for(BookInstance b:uniqueHoldings.get(book))
    {
      if(b != null)
      {
        temp.add(b);
      }
    }
    return temp;
  }

  public boolean isInHoldings(BookInstance bookInstance)
  {
    return uniqueHoldings.containsValue(bookInstance);
  }

  public Collection<Book> allBooks()
  {
    return this.holdings;
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

    Library otherLibrary = (Library) that;

    return this.getName().equals(otherLibrary.getName());
  }

  @Override
  public int hashCode()
  {
    return Objects.hash(name);
  }

  @Override
  public String toString()
  {
    return getClass() + " Name: " + this.name + ", Address: " + this.address + ", Phone Number: " + this.phoneNumber;
  }

  public static void main(String[] args)
  {
    /*Library library1 = new Library("Skokie","3555 arcadi","668978987900");
    Driver.run();
    System.out.println(Patrons.Singleton.nPatrons());
    System.out.println(library1.nBooks());
    Library library = new Library("Skokie","3555 arcadi","668978987900");
    Book book = new Book("eragon","paolini",1234567890120L,"hardcover");
    Book book1 = new Book("eragon","paolin",1234567890120L,"hardcover");
    Patron patron1 = new Patron("Daniel","Schaffel","3555 arcadia");
    Patron patron2 = new Patron("Daniel","Schaffel","3555 arcadi");
    library.add(patron1);
    library.add(patron2);
    library.add(book);
    library.add(book1);
    library.borrow(patron1,book);
    library.borrow(patron1,book1);
    System.out.println(library.onLoan(patron1));
    String authorName ="daniel";
    String titleName = "eragon";
    long isbn = 1234567890123L;
    String bookType = "ebook";
    BookFilter bookFilter = new BookFilter.Builder().setAuthor(authorName).setTitle(titleName).setISBN13(isbn).build();
    System.out.println(bookFilter);
    System.out.println(bookFilter.filter(book));
    */
  }
}
