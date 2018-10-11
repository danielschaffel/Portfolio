package edu.yu.intro.objects.library4.mytest;

import org.junit.*;

import java.util.*;

import static org.junit.Assert.*;


public class MyActualTestsForLibrary4
{
  /////////////////////////////////////////////////////////////////////////////
  //BOOKINSTANCE TESTS
  @Test
  public void testHappyPathBookInstanceConstructor()
  {
    Book book = new Book("eragon","paolini",1234567890123L,"ebook");
    BookInstance bookInstance = new BookInstance(book);
    assertEquals(book,bookInstance.getBook());
    assertEquals(LoanState.AVAILABLE,bookInstance.getLoanState());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testBookInstanceThrowIAEBcNull()
  {
    BookInstance bookInstance = new BookInstance(null);
  }

  @Test//ALL OF THESE METHODS ARE MY OWN AND ARE NOT IN REQUIREMENTS
  public void testAllSetMethods()
  {
    Book book = new Book("eragon","paolini",1234567890123L,"ebook");
    BookInstance bookInstance = new BookInstance(book);
    Library library = new Library("Skokie","3555","123445678");
    Patron patron = new Patron("daniel","Schaffel","3555");
    bookInstance.setLibrary(library);
    bookInstance.setPatron(patron);
    assertEquals(library,bookInstance.getLibrary());
    assertEquals(patron,bookInstance.getPatron());
    //next test is just to make sure that id was actually assigned something
    assertNotEquals(null,bookInstance.getId());
  }

  @Test
  public void testBorrowMethodHappyPath()throws OnLoanException
  {
    Book book = new Book("eragon","paolini",1234567890123L,"ebook");
    BookInstance bookInstance = new BookInstance(book);
    Library library = new Library("Skokie","3555","123445678");
    Patron patron = new Patron("daniel","Schaffel","3555");
    try
    {
      bookInstance.borrow(patron);
    }
    catch(OnLoanException e)
    {
      throw new OnLoanException("");
    }
    assertEquals(patron,bookInstance.getPatron());
    assertEquals(LoanState.BORROWED,bookInstance.getLoanState());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testBorrowMethodNullPatron()
  {
    Book book = new Book("eragon","paolini",1234567890123L,"ebook");
    BookInstance bookInstance = new BookInstance(book);
    try
    {
      bookInstance.borrow(null);
    }
    catch(OnLoanException e)
    {

    }
  }

  @Test(expected = OnLoanException.class)
  public void testBorrowMethodThrowOLEForOnReserveToDiffPatron() throws OnLoanException
  {
    Book book = new Book("eragon","paolini",1234567890123L,"ebook");
    BookInstance bookInstance = new BookInstance(book);
    Patron patron = new Patron("daniel","Schaffel","3555");
    Patron patron1 = new Patron("danie","Schaffel","3555");
    bookInstance.setLoanState(LoanState.ON_RESERVE);
    bookInstance.setPatron(patron1);
    assertEquals(patron1,bookInstance.getPatron());
    try {
      bookInstance.borrow(patron);
    } catch (OnLoanException e) {
      throw new OnLoanException("please throw");
    }
  }

  @Test
  public void testReserveHappyPath()
  {
    Book book = new Book("eragon","paolini",1234567890123L,"ebook");
    BookInstance bookInstance = new BookInstance(book);
    Patron patron = new Patron("daniel","Schaffel","3555");
    Library library = new Library("Skokie","3555","123445678");
    library.add(patron);
    bookInstance.setLibrary(library);
    bookInstance.reserve(patron);
    assertEquals(patron,bookInstance.getPatron());
    assertEquals(LoanState.ON_RESERVE,bookInstance.getLoanState());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testPatronNullAndPatronNotPartOfLibrary()
  {
    Book book = new Book("eragon","paolini",1234567890123L,"ebook");
    BookInstance bookInstance = new BookInstance(book);
    Patron patron = new Patron("daniel","Schaffel","3555");
    Library library = new Library("Skokie","3555","123445678");
    bookInstance.setLibrary(library);
    bookInstance.reserve(null);
    bookInstance.reserve(patron);
    //i tried both by themselves as well
  }

  @Test(expected = IllegalArgumentException.class)
  public void testBorrowedAlready()
  {
    Book book = new Book("eragon","paolini",1234567890123L,"ebook");
    BookInstance bookInstance = new BookInstance(book);
    Patron patron = new Patron("daniel","Schaffel","3555");
    Patron patron1 = new Patron("danie","Schaffel","3555");
    Library library = new Library("Skokie","3555","123445678");
    bookInstance.setLibrary(library);
    bookInstance.setLoanState(LoanState.BORROWED);
    bookInstance.setPatron(patron1);
    library.add(patron);
    bookInstance.reserve(patron);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testOnReserveByAnotherPatron()
  {
    Book book = new Book("eragon","paolini",1234567890123L,"ebook");
    BookInstance bookInstance = new BookInstance(book);
    Patron patron = new Patron("daniel","Schaffel","3555");
    Patron patron1 = new Patron("danie","Schaffel","3555");
    Library library = new Library("Skokie","3555","123445678");
    bookInstance.setLibrary(library);
    bookInstance.setLoanState(LoanState.ON_RESERVE);
    bookInstance.setPatron(patron1);
    library.add(patron);
    bookInstance.reserve(patron);
  }

  @Test
  public void testReturnInstanceBookInstanceMethodHappyPath()throws NotOnLoanException,OnLoanException
  {
    Library library = new Library("Skokie","3555","123445678");
    Book book = new Book("eragon","paolini",1234567890123L,"ebook");
    BookInstance bookInstance = library.add(book);
    Patron patron = new Patron("daniel","Schaffel","3555");
    Patron patron1 = new Patron("danie","Schaffel","3555");
    library.add(patron);
    try {
      library.borrow(patron,bookInstance);
    } catch (OnLoanException e) {
      throw new OnLoanException("");
    }
    bookInstance.returnInstance(patron);
    assertEquals(null,bookInstance.getPatron());
    assertEquals(LoanState.AVAILABLE,bookInstance.getLoanState());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testReturnInstanceIAENullPatron()throws NotOnLoanException
  {
    Library library = new Library("Skokie","3555","123445678");
    Book book = new Book("eragon","paolini",1234567890123L,"ebook");
    BookInstance bookInstance = library.add(book);
    bookInstance.returnInstance(null);
  }


  @Test(expected = NotOnLoanException.class)
  public void testReturnInstanceBookInstanceMethodNOLEForBookIsAvailable()throws NotOnLoanException,OnLoanException
  {
    Library library = new Library("Skokie","3555","123445678");
    Book book = new Book("eragon","paolini",1234567890123L,"ebook");
    BookInstance bookInstance = library.add(book);
    Patron patron = new Patron("daniel","Schaffel","3555");
    Patron patron1 = new Patron("danie","Schaffel","3555");
    library.add(patron);
    bookInstance.setLoanState(LoanState.AVAILABLE);
    bookInstance.returnInstance(patron);
  }

  @Test(expected = NotOnLoanException.class)
  public void testReturnInstanceBookInstanceMethodNOLEForBorrowedByDiffPatron()throws NotOnLoanException,OnLoanException
  {
    Library library = new Library("Skokie","3555","123445678");
    Book book = new Book("eragon","paolini",1234567890123L,"ebook");
    BookInstance bookInstance = library.add(book);
    Patron patron = new Patron("daniel","Schaffel","3555");
    Patron patron1 = new Patron("danie","Schaffel","3555");
    library.add(patron);
    bookInstance.setLoanState(LoanState.BORROWED);
    bookInstance.setPatron(patron1);
    bookInstance.returnInstance(patron);
  }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///NEW LIBRARY METHOD TESTS


  @Test
  public void testLibraryBookAddMethodHappyPathAsWellAsAllBookMethod()
  {
    Library library = new Library("Skokie","3555","123445678");
    Book book = new Book("eragon","paolini",1234567890123L,"ebook");
    BookInstance bookInstance = library.add(book);
    Set<Book> temp = new HashSet<>();
    temp.add(book);
    assertEquals(bookInstance.getBook(),book);
    assertEquals(temp,library.allBooks());
  }

  @Test
  public void testLibraryReserveMethodHappyPath()
  {
    Library library = new Library("Skokie","3555","123445678");
    Book book = new Book("eragon","paolini",1234567890123L,"ebook");
    Patron patron = new Patron("daniel","Schaffel","3555");
    library.add(patron);
    library.add(book);
    assertTrue(library.reserve(patron,book));
    Set<Book> temp = new HashSet<>();
    temp.add(book);
    assertEquals(library.getInstances(book),library.onReserve(patron));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testIAELibraryReserveMethodPatronNotMemberOfLibrary()
  {
    Library library = new Library("Skokie","3555","123445678");
    Book book = new Book("eragon","paolini",1234567890123L,"ebook");
    Patron patron = new Patron("daniel","Schaffel","3555");
    library.add(book);
    library.reserve(patron,book);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testIAELibraryReserveMethodBookNotPartOfLibrary()
  {
    Library library = new Library("Skokie","3555","123445678");
    Book book = new Book("eragon","paolini",1234567890123L,"ebook");
    Patron patron = new Patron("daniel","Schaffel","3555");
    library.add(patron);
    library.reserve(patron,book);
  }

  @Test
  public void testLibraryOnReserve()
  {
    Library library = new Library("Skokie","3555","123445678");
    Book book = new Book("eragon","paolini",1234567890123L,"ebook");
    library.add(book);
    assertEquals(1,library.getInstances(book).size());
  }

  @Test
  public void testLibraryBorrowMethodHappyPath()throws OnLoanException
  {
    Library library = new Library("Skokie","3555","123445678");
    Book book = new Book("eragon","paolini",1234567890123L,"ebook");
    Patron patron = new Patron("daniel","Schaffel","3555");
    BookInstance bookInstance = library.add(book);
    library.add(patron);
    try {
      library.borrow(patron,bookInstance);
    } catch (OnLoanException e) {
      throw new OnLoanException("");
    }
    Set<BookInstance> temp = new HashSet<>();
    temp.add(bookInstance);
    assertEquals(temp,library.onLoan(patron));//this also tests onLoan library method

  }

  @Test(expected = OnLoanException.class)
  public void testLibraryBorrowOLEAlreadyBorrowed()throws OnLoanException
  {
    Library library = new Library("Skokie","3555","123445678");
    Book book = new Book("eragon","paolini",1234567890123L,"ebook");
    Patron patron = new Patron("daniel","Schaffel","3555");
    BookInstance bookInstance = library.add(book);
    library.add(patron);
    bookInstance.setLoanState(LoanState.BORROWED);
    library.borrow(patron,bookInstance);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testLibraryBorrowBookNotInHoldings()throws OnLoanException
  {
    Library library = new Library("Skokie","3555","123445678");
    Book book = new Book("eragon","paolini",1234567890123L,"ebook");
    Patron patron = new Patron("daniel","Schaffel","3555");
    BookInstance bookInstance = new BookInstance(book);
    library.add(book);
    library.add(patron);
    try
    {
      library.borrow(patron,bookInstance);
    }
    catch(OnLoanException e)
    {
      throw new OnLoanException("");
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void testLibraryBorrowMethodPatronNotInLibrary()throws OnLoanException
  {
    Library library = new Library("Skokie","3555","123445678");
    Book book = new Book("eragon","paolini",1234567890123L,"ebook");
    Patron patron = new Patron("daniel","Schaffel","3555");
    BookInstance bookInstance = library.add(book);

    try
    {
      library.borrow(patron,bookInstance);
    }
    catch(OnLoanException e)
    {
      throw new OnLoanException("");
    }
  }

  @Test
  public void testReturnInstanceHappyPath()throws NotOnLoanException,OnLoanException
  {
    Library library = new Library("Skokie","3555","123445678");
    Book book = new Book("eragon","paolini",1234567890123L,"ebook");
    Patron patron = new Patron("daniel","Schaffel","3555");
    BookInstance bookInstance = library.add(book);
    library.add(patron);
    try {
      library.borrow(patron,bookInstance);
    } catch (OnLoanException e) {
      throw new OnLoanException("");
    }
    Set<BookInstance> temp1 = new HashSet<>();
    temp1.add(bookInstance);
    assertEquals(patron,bookInstance.getPatron());
    assertEquals(temp1,library.onLoan(patron));
    try{
      library.returnInstance(patron,bookInstance);
    }
    catch(NotOnLoanException e)
    {
      throw new NotOnLoanException("");
    }

    temp1.clear();
    Set<BookInstance> temp2 = new HashSet<>();
    assertEquals(library.onLoan(patron), temp2);
    assertEquals(LoanState.AVAILABLE,bookInstance.getLoanState());
    assertEquals(null,bookInstance.getPatron());
  }

  @Test(expected = NotOnLoanException.class)
  public void testNOLEForBookNotBorrowed()throws NotOnLoanException
  {
    Library library = new Library("Skokie","3555","123445678");
    Book book = new Book("eragon","paolini",1234567890123L,"ebook");
    Patron patron = new Patron("daniel","Schaffel","3555");
    BookInstance bookInstance = library.add(book);
    library.add(patron);
    try
    {
      library.returnInstance(patron,bookInstance);
    }
    catch(NotOnLoanException e)
    {
      throw new NotOnLoanException("");
    }
  }

  @Test(expected = NotOnLoanException.class)
  public void testReturnInstanceLibraryMethodNOLEForBookAssociatedWithDiffPatron()throws NotOnLoanException
  {
    Library library = new Library("Skokie","3555","123445678");
    Book book = new Book("eragon","paolini",1234567890123L,"ebook");
    Patron patron = new Patron("daniel","Schaffel","3555");
    Patron patron1 = new Patron("chana","Schaffel","3555 arcadia");
    BookInstance bookInstance = library.add(book);
    library.add(patron);
    bookInstance.setLoanState(LoanState.BORROWED);
    bookInstance.setPatron(patron1);
    try
    {
      library.returnInstance(patron,bookInstance);
    }
    catch(NotOnLoanException e)
    {
      throw new NotOnLoanException("");
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void testLibraryReturnInstanceMethodThrowIAEBookNotPartOfLibrary()throws NotOnLoanException
  {
    Library library = new Library("Skokie","3555","123445678");
    Book book = new Book("eragon","paolini",1234567890123L,"ebook");
    Patron patron = new Patron("daniel","Schaffel","3555");
    Patron patron1 = new Patron("chana","Schaffel","3555 arcadia");
    library.add(patron);
    BookInstance bookInstance = new BookInstance(book);
    library.add(book);
    try
    {
      library.returnInstance(patron,bookInstance);
    }
    catch(NotOnLoanException e)
    {
      throw new NotOnLoanException("");
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void testLibraryReturnInstanceThrowIAEForPatronNotPartOfLibrary()throws NotOnLoanException
  {
    Library library = new Library("Skokie","3555","123445678");
    Book book = new Book("eragon","paolini",1234567890123L,"ebook");
    Patron patron = new Patron("daniel","Schaffel","3555");
    Patron patron1 = new Patron("chana","Schaffel","3555 arcadia");
    BookInstance bookInstance = library.add(book);
    try
    {
      library.returnInstance(patron,bookInstance);
    }
    catch(NotOnLoanException e)
    {
      throw new NotOnLoanException("");
    }
  }

  @Test
  public void testOnLoanHappyPath()throws OnLoanException,NotOnLoanException
  {
    Library library = new Library("Skokie","3555","123445678");
    Book book = new Book("eragon","paolini",1234567890123L,"ebook");
    Patron patron = new Patron("daniel","Schaffel","3555");
    BookInstance bookInstance = library.add(book);
    library.add(patron);
    Set<BookInstance> temp = new HashSet<>();
    assertEquals(temp,library.onLoan(patron));
    library.borrow(patron,bookInstance);
    temp.add(bookInstance);
    assertEquals(temp,library.onLoan(patron));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testOnLoanPatronNotPartOfLibrary()
  {
    Library library = new Library("Skokie","3555","123445678");
    Book book = new Book("eragon","paolini",1234567890123L,"ebook");
    Patron patron = new Patron("daniel","Schaffel","3555");
    BookInstance bookInstance = library.add(book);
    library.onLoan(patron);
  }

  @Test
  public void testOnReserveHappyPath()
  {
    Library library = new Library("Skokie","3555","123445678");
    Book book = new Book("eragon","paolini",1234567890123L,"ebook");
    Patron patron = new Patron("daniel","Schaffel","3555");
    BookInstance bookInstance = library.add(book);
    library.add(patron);
    Set<BookInstance> temp = new HashSet<>();
    assertEquals(temp,library.onReserve(patron));
    library.reserve(patron,book);
    temp.add(bookInstance);
    assertEquals(temp,library.onReserve(patron));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testOnReserveIAEPatronNotInLibrary()
  {
    Library library = new Library("Skokie","3555","123445678");
    Book book = new Book("eragon","paolini",1234567890123L,"ebook");
    Patron patron = new Patron("daniel","Schaffel","3555");
    BookInstance bookInstance = library.add(book);
    library.onReserve(patron);
  }

  @Test
  public void testGetInstancesHappyPath()
  {
    Library library = new Library("Skokie","3555","123445678");
    Book book = new Book("eragon","paolini",1234567890123L,"ebook");
    Set<BookInstance> temp = new HashSet<>();
    assertEquals(temp,library.getInstances(book));
    BookInstance bookInstance = library.add(book);
    BookInstance bookInstance1 = library.add(book);
    temp.add(bookInstance);
    temp.add(bookInstance1);
    assertEquals(temp,library.getInstances(book));
  }
}
