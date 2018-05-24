package edu.yu.db.schaffel;

public class Node
{
    private Entry[] entries;
    static final int MAX_SIZE = 4;
    int entryCount=0;
    public Node()
    {
        entries = new Entry[MAX_SIZE];
    }

    public Node(int entryCount)
    {
        this.entryCount = entryCount;
        entries = new Entry[MAX_SIZE];
    }

    public Entry[] getEntries()
    {
        return entries;
    }

    public void setEntries(Entry[] entries)
    {
        this.entries = entries;
    }
}
