package edu.yu.intro.objects.library4.mytest;

import org.junit.*;
import java.util.*;
import static org.junit.Assert.*;
import edu.yu.intro.objects.library4.*;


//stopped testing for null inputs near the middle, if you dont have them, sucks to suck

public class MyLibrary4Test
{

	private static Library library1;
	private static Library library2;
	private static Library library3;
	private static Patron patron1;
	private static Patron patron2;
	private static Patron patron3;
	private static Book book1;
	private static Book book2;
	private static Book book3;
	private static Book book99;
	private static BookInstance bookInstance1;

	@BeforeClass public static void onlyOnce()
	{
		library1 = new Library("Englewood Library", " 221 Allison Court", "201-290-2124");
		library2 = new Library("Teaneck Library", "1600 Queen Anne Road", "201-555-5555");
		library3 = new Library("New York Public Library", "Next To Stern", "999-999-9999");

		patron1 = new Patron("Avi", "Katz", "555 Broad Avenue");
		patron2 = new Patron("Myles", "Tyberg", "Somewhere In England");
		patron3 = new Patron("David", "Beardface", "777 Who Knows Road");

		book1 = new Book("Tanach", "God", 6574839201101L, "hardcover");
		book2 = new Book("Kuzari", "Big Al", 1234567891234L, "ebook");
		book3 = new Book("The Little Midrash Says", "Who Cares", 1234567894433L, "paperback");
		book99 = new Book("Hello Mom", "Who Cares", 1114567894433L, "paperback");

		library1.add(book1);
		library1.add(book99);
		library1.add(book2);
		library1.add(patron1);
		library1.add(patron3);
		library2.add(patron3);

		bookInstance1 = new BookInstance(book1);
	}


///////////////////////BOOK_INSTANCE/////////////////////////////////////
	@Test
	public void getBookHappyPath()
	{
		assertEquals(book1, bookInstance1.getBook());
	}

	@Test
	public void getNullPatronHappyPath()
	{
		assertEquals(null, bookInstance1.getPatron());
	}

	@Test
	public void getPatronHappyPath()
	{
		BookInstance bookInstance2 = new BookInstance(book1);
		bookInstance2.setPatron(patron1);

		assertEquals(patron1, bookInstance2.getPatron());
	}

	@Test
	public void getStartLoanStateHappyPath()
	{
		assertEquals(LoanState.AVAILABLE, bookInstance1.getLoanState());
	}

	@Test
	public void borrowHappyPath()
	{
		BookInstance bookInstance3 = library1.add(book1);

		try
		{
			bookInstance3.borrow(patron1);
		}

		catch (OnLoanException IOE)
		{

		}

		assertEquals(LoanState.BORROWED, bookInstance3.getLoanState());
		assertEquals(patron1, bookInstance3.getPatron());
		assertEquals(library1, bookInstance3.getLibrary());
	}


	@Test (expected = RuntimeException.class)
	public void borrowNullPatron()
	{
		BookInstance bookInstance3 = library1.add(book1);

		try
		{
			bookInstance3.borrow(null);
		}

		catch (OnLoanException IOE)
		{

		}

	}

	@Test (expected = RuntimeException.class)
	public void borrowPatronNotInLibrary()
	{
		BookInstance bookInstance3 = library1.add(book1);

		try
		{
			bookInstance3.borrow(patron2);
		}

		catch (OnLoanException IOE)
		{

		}
	}


	@Test (expected=OnLoanException.class)
	public void borrowBookAlreadyBorrowed()
	{
		BookInstance bookInstance4 = library1.add(book2);

		try
		{
			bookInstance4.borrow(patron1);
			bookInstance4.borrow(patron1);
			bookInstance4.borrow(patron2);
		}

		catch (OnLoanException IOE)
		{

		}
	}




	@Test
	public void reserveHappyPath()
	{
		BookInstance bookInstance3 = library1.add(book1);
		bookInstance3.reserve(patron3);


		assertEquals(LoanState.ON_RESERVE, bookInstance3.getLoanState());
		assertEquals(patron3, bookInstance3.getPatron());
		assertEquals(library1, bookInstance3.getLibrary());
	}


	@Test (expected = RuntimeException.class)
	public void reserveNullPatron()
	{
		BookInstance bookInstance5 = library1.add(book1);
		bookInstance5.reserve(null);
	}

	@Test (expected = RuntimeException.class)
	public void reservePatronNotInLibrary()
	{
		BookInstance bookInstance5 = library1.add(book1);
		bookInstance5.reserve(patron2);
	}

	@Test (expected = RuntimeException.class)
	public void reserveAlreadyBorrowedBook()
	{
		BookInstance bookInstance5 = library1.add(book1);

		try
		{
			bookInstance5.borrow(patron3);
		}

		catch(OnLoanException IOE)
		{

		}

		bookInstance5.reserve(patron1);
	}

	@Test (expected = RuntimeException.class)
	public void reserveAlreadyReservedByOtherPatronBook()
	{
		BookInstance bookInstance5 = library1.add(book1);

		bookInstance5.reserve(patron3);
		bookInstance5.reserve(patron1);
	}

	@Test
	public void reserveBookReservedToYouAlready()
	{
		BookInstance bookInstance5 = library1.add(book1);
		bookInstance5.reserve(patron3);
		bookInstance5.reserve(patron3);
	}


///////////////////////LIBRARY/////////////////////////////////////
/*
	@Test
	public void libraryBorrowHappyPath()
	{
		BookInstance bookInstance5 = library2.add(book1);

		try
		{
			library2.borrow(patron3, bookInstance5);
		}

		catch(OnLoanException IOE)
		{

		}

		assertEquals(LoanState.BORROWED, bookInstance5.getLoanState());
		assertEquals(patron3, bookInstance5.getPatron());
	}
	*/
/*
	@Test (expected = OnLoanException.class)
	public void instanceCurrentlyOnLoan()
	{
		BookInstance bookInstance5 = library2.add(book1);

		try
		{
			library2.borrow(patron3, bookInstance5);
			library2.borrow(patron3, bookInstance5);
		}

		catch(OnLoanException IOE)
		{

		}
	}
*/

	@Test (expected = RuntimeException.class)
	public void bookNotInHoldings()
	{
		BookInstance bookInstance5 = library2.add(book1);

		try
		{
			library3.borrow(patron3, bookInstance5);
		}

		catch(OnLoanException IOE)
		{

		}
	}

	@Test (expected = RuntimeException.class)
	public void patronNotPartOfLibrary()
	{
		BookInstance bookInstance5 = library2.add(book1);

		try
		{
			library2.borrow(patron1, bookInstance5);
		}

		catch(OnLoanException IOE)
		{

		}
	}

	@Test
	public void libraryReserveHappyPath()
	{
		BookInstance bookInstance5 = library2.add(book3);

		assertTrue(library2.reserve(patron3, book3));
		assertEquals(LoanState.ON_RESERVE, bookInstance5.getLoanState());
	}

	@Test
	public void noInstanceAvailable()
	{
		library1.reserve(patron1, book1);
		library1.reserve(patron1, book1);
		library1.reserve(patron1, book1);
		library1.reserve(patron1, book1);
		library1.reserve(patron1, book1);
		library1.reserve(patron1, book1);
		library1.reserve(patron1, book1);
		library1.reserve(patron1, book1);
		assertFalse(library1.reserve(patron1,book1));
	}

	@Test (expected = RuntimeException.class)
	public void bookNotInLibrary()
	{
		library1.reserve(patron1, book3);
	}

	@Test (expected = RuntimeException.class)
	public void patronNotInLibrary()
	{
		library1.reserve(patron2, book1);
	}
	/*
	@Test
	public void resturnInstanceHappyPath()
	{
		BookInstance bookInstance5 = library2.add(book3);
		library2.borrow(patron3, book3);

		try
		{
			library2.returnInstance(patron3, bookInstance5);
		}

		catch (NotOnLoanException IOE)
		{

		}
	}
	*/

	@Test (expected = RuntimeException.class)
	public void returnInstanceBookNotInlibrary()
	{
		Library library5 = new Library("Apple Library", "111 Apple Lane", "111-111-1111");
		library5.add(patron3);
		Book book4 = new Book("The Little Man", "Who Cares", 1234000894433L, "paperback");
		BookInstance bookInstance7 = library5.add(book4);
		library5.borrow(patron3, book4);

		try
		{
			library3.returnInstance(patron3, bookInstance7);
		}

		catch (NotOnLoanException IOE)
		{

		}
	}

	@Test (expected = RuntimeException.class)
	public void returnInstancePatronNotInlibrary()
	{
		Library library5 = new Library("Apple Library", "111 Apple Lane", "111-111-1111");
		library5.add(patron3);
		Book book4 = new Book("The Little Man", "Who Cares", 1234000894433L, "paperback");
		BookInstance bookInstance7 = library5.add(book4);
		library5.borrow(patron3, book4);

		try
		{
			library5.returnInstance(patron1, bookInstance7);
		}

		catch (NotOnLoanException IOE)
		{

		}

	}
/*
	@Test
	public void onLoanHappyPath()
	{
		BookInstance bookInstance8 = library3.add(book1);
		BookInstance bookInstance9 = library3.add(book2);

		library3.add(patron1);

		try
		{
			library3.borrow(patron1, bookInstance8);
			library3.borrow(patron1, bookInstance9);
		}

		catch(OnLoanException IOE)
		{

		}

		HashSet<BookInstance> testSet = new HashSet<>();
		testSet.add(bookInstance8);
		testSet.add(bookInstance9);

		assertEquals(testSet, library3.onLoan(patron1));
	}
	*/
	/*
	@Test
	public void onLoanHappyPath2()
	{
		BookInstance bookInstance8 = library3.add(book1);
		BookInstance bookInstance9 = library3.add(book2);

		library3.add(patron1);

		try
		{
			library3.borrow(patron1, bookInstance8);
			library3.borrow(patron1, bookInstance9);
		}

		catch(OnLoanException IOE)
		{

		}

		try
		{
			library3.returnInstance(patron1, bookInstance8);
		}

		catch(NotOnLoanException IOE)
		{

		}

		HashSet<BookInstance> testSet = new HashSet<>();
		testSet.add(bookInstance9);

		assertEquals(testSet, library3.onLoan(patron1));
	}


	@Test
	public void onLoanHappyPath3()
	{
		BookInstance bookInstance8 = library3.add(book1);
		BookInstance bookInstance9 = library3.add(book2);

		library3.add(patron1);

		try
		{
			library3.borrow(patron1, bookInstance8);
		}

		catch(OnLoanException IOE)
		{

		}

		library3.reserve(patron1, book2);

		HashSet<BookInstance> testSet = new HashSet<>();
		testSet.add(bookInstance8);

		assertEquals(testSet, library3.onLoan(patron1));
	}
	*/
	/*
	@Test
	public void OnLoanEmptySet()
	{
		library3.add(patron1);

		HashSet<BookInstance> testSet = new HashSet<>();

		assertEquals(testSet, library3.onLoan(patron1));

	}
	*/
	@Test (expected = RuntimeException.class)
	public void patronNotInLibrary2()
	{
		library3.add(patron1);

		library2.onLoan(patron1);
	}

	/*
	@Test
	public void onReserveHappyPath()
	{
		BookInstance bookInstance11 = library3.add(book1);
		BookInstance bookInstance12 = library3.add(book2);

		library3.add(patron1);


		library3.reserve(patron1, book1);
		library3.reserve(patron1, book2);


		HashSet<BookInstance> testSet = new HashSet<>();
		testSet.add(bookInstance11);
		testSet.add(bookInstance12);

		assertEquals(testSet, library3.onReserve(patron1));
	}
	*/
/* 																	//onlu use this test if there is in fact a way to un-reserve a book and re-did returnInstance method
	@Test
	public void onReserveHappyPath2()
	{
		BookInstance bookInstance11 = library3.add(book1);
		BookInstance bookInstance12 = library3.add(book2);

		library3.add(patron1);

		library3.reserve(patron1, book1);
		library3.reserve(patron1, book2);

		try
		{
			library3.returnInstance(patron1, bookInstance11);
		}

		catch(NotOnLoanException IOE)
		{

		}

		HashSet<BookInstance> testSet = new HashSet<>();
		testSet.add(bookInstance12);

		assertEquals(testSet, library3.onReserve(patron1));
	}
*/
	/*
	@Test
	public void onReserveHappyPath3()
	{
		BookInstance bookInstance11 = library3.add(book1);
		BookInstance bookInstance12 = library3.add(book2);

		library3.add(patron1);


		library3.reserve(patron1, book1);

		try
		{
			library3.borrow(patron1, bookInstance12);
		}

		catch(OnLoanException IOE)
		{

		}

		HashSet<BookInstance> testSet = new HashSet<>();
		testSet.add(bookInstance11);

		assertEquals(testSet, library3.onReserve(patron1));
	}
	*/
	/*
	@Test
	public void onReserveEmptySet()
	{
		BookInstance bookInstance11 = library3.add(book1);
		BookInstance bookInstance12 = library3.add(book2);

		library3.add(patron1);

		try
		{
			library3.borrow(patron1, bookInstance11);
		}
		catch (OnLoanException IOE)
		{

		}

		HashSet<BookInstance> testSet = new HashSet<>();

		assertEquals(testSet, library3.onReserve(patron1));

	}
	*/
	/*
	@Test (expected = RuntimeException.class)
	public void patronNotInLibrary3()
	{
		BookInstance bookInstance11 = library3.add(book1);
		library3.add(patron1);
		library3.reserve(patron1, book1);
		library2.onReserve(patron1);

	}
	*/

	@Test
	public void getInstancesHappyPath()
	{
		BookInstance bookInstance20 = library3.add(book99);
		BookInstance bookInstance21 = library3.add(book99);


		HashSet<BookInstance> testSet = new HashSet<>();
		testSet.add(bookInstance20);
		testSet.add(bookInstance21);

		assertEquals(testSet, library3.getInstances(book99));
	}

	@Test
	public void getInstancesEmptySetNotPartOfLibrary()
	{
		HashSet<BookInstance> testSet = new HashSet<>();
		assertEquals(testSet, library2.getInstances(book99));

	}
/*
	@Test
	public void isinHoldingsHappyPath()
	{
		Book book54 = new Book("Test", "Test", 1111111111111L, "ebook");
		BookInstance bookInstance88 = library3.add(book54);

		assertTrue(library3.isInHoldings(bookInstance88));
	}

	@Test
	public void isinHoldingsHappyPath2()
	{
		Book book54 = new Book("Test", "Test", 1111111111111L, "ebook");
		BookInstance bookInstance88 = library3.add(book54);

		assertFalse(library2.isInHoldings(bookInstance88));
	}
}
*/
/*
	@Test
	public void allBooksHappyPath()
	{
		Book book41 = new Book("Test", "Test", 1211111111111L, "ebook");
		Book book42 = new Book("Test", "Test", 1212111111111L, "ebook");
		Library library95 = new Library("Test", "Test", "Test");

		library95.add(book41);
		library95.add(book42);

		HashSet<Book> testSet = new HashSet<>();
		testSet.add(book41);
		testSet.add(book42);

		assertEquals(testSet, library95.allBooks());



	}


*/
}
