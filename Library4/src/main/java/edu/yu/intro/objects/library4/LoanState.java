package edu.yu.intro.objects.library4;


/*
	- Book instance starts off AVAILABLE
	- If reserved, change to ON_RESERVE
	- If reserved, change to BORROWED
	- If returned, chane to AVAILABLE

*/

public enum LoanState
{
	AVAILABLE, ON_RESERVE , BORROWED
}
