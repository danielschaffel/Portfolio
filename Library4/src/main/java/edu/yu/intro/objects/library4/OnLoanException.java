package edu.yu.intro.objects.library4;
import java.util.*;
/**
This class throws Exception when book trying to borrow and the BookInstance is onLoan or onReserved
*/

public class OnLoanException extends Exception
{
  public OnLoanException(String message)
  {
    super(message);
  }
}
