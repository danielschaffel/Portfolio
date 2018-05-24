package edu.yu.intro.objects.library4;
/**
This class throws Exception when bookInstance is either not onloan or not on loan
to a given patron and trying to do illegal operation that requires these things
*/

public class NotOnLoanException extends Exception
{
  public NotOnLoanException(String message)
  {
    super(message);
  }
}
