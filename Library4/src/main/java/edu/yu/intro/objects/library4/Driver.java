package edu.yu.intro.objects.library4;
import java.util.*;
/**
@Daniel Schaffel
*/

public class Driver
{
  public static Library run()
  {
    Library library = new Library("Skokie","3555 arcadia","1234567890");
    Patron patron2 = new Patron("second","two","6778998");
    Patron patron3 = new Patron("third","three","123132");
    Book book = new Book("eragon","paolini",1234567890120L,"hardcover");
    Book book1 = new Book("erago","paolin",1234567890120L,"hardcover");
    Book book2 = new Book("eragon","paolin",1234567890121L,"hardcover");
    library.add(patron2);
    library.add(patron3);
    library.add(book);
    library.add(book1);
    library.add(book2);
    return library;
  }

  public static void main(String[] args)
 {

   Driver.run();
  }
}
