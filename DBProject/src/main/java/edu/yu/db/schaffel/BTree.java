package edu.yu.db.schaffel;

import java.util.ArrayList;

public class BTree
{
    private int height;
    private Node root;
    private int n;
    public BTree()
    {
        root = new Node();
        Entry sentinal = new Entry(new Cell(),null);
        this.root.getEntries()[0] = sentinal;
        root.entryCount++;
        height = 0;
    }

    /**
     * puts key value pair into the btree
     * @param key
     * @param value
     */
    public void put(Cell key,Row value)
    {
        Node newNode = this.put(this.root,key,value,this.height);
        this.n++;
        if(newNode == null)
            return;
        Node newRoot = new Node(2);
        newRoot.getEntries()[0] = new Entry(this.root.getEntries()[0].key,null,this.root);
        newRoot.getEntries()[1] = new Entry(newNode.getEntries()[0].key,null,newNode);
        this.root = newRoot;

        this.height++;
    }


    Node put(Node currentNode,Cell key,Row value,int height) {
        int j;
        Entry newEntry = new Entry(key,value);
        if(height == 0) {
            for(j=0;j<currentNode.entryCount;j++)
                if (key.compareTo(currentNode.getEntries()[j].getKey()) > 0 || key.compareTo(currentNode.getEntries()[j].getKey()) == 0)
                    break;
        }
        else {
            for (j = 0; j < currentNode.entryCount; j++) {
                if(j+1 != currentNode.entryCount && key.compareTo(currentNode.getEntries()[j+1].getKey()) == 0) {
                    Node newNode = this.put(currentNode.getEntries()[j++].child,key,value,height-1);
                        return null;
                }
                if (j + 1 == currentNode.entryCount || key.compareTo(currentNode.getEntries()[j + 1].getKey()) > 0) {
                    Node newNode = this.put(currentNode.getEntries()[j++].child, key, value, height - 1);
                    if (newNode == null)
                        return null;
                    newEntry.key = newNode.getEntries()[0].key;
                    newEntry.child = newNode;
                    break;
                }
            }
        }
        if (currentNode.getEntries()[j] != null && key.compareTo(currentNode.getEntries()[j].getKey()) == 0)
            currentNode.getEntries()[j].setValue(value);
        else {
            for (int i = currentNode.entryCount; i > j; i--)
                currentNode.getEntries()[i] = currentNode.getEntries()[i - 1];
            currentNode.getEntries()[j] = newEntry;
            currentNode.entryCount++;
            }
            if(currentNode.entryCount<Node.MAX_SIZE)
                return null;
            else
                return this.split(currentNode);
        }

    /**
     * splits a node if entryCount is equal to max size
     * @param currentNode
     * @return
     */
    Node split(Node currentNode)
    {
        Node newNode = new Node(Node.MAX_SIZE/2);
        currentNode.entryCount = Node.MAX_SIZE/2;
        for(int j=0;j<Node.MAX_SIZE/2;j++)
        {
            newNode.getEntries()[j] = currentNode.getEntries()[Node.MAX_SIZE/2 + j];
        }
        return newNode;
    }

    /**
     * gets values that match the key
     * @param cell
     * @return
     */
    Table get(Cell cell)
    {
        return this.get(this.root,cell,this.height);
    }


    Table get(Node currentNode,Cell cell,int height)
    {
        Entry[] entries = currentNode.getEntries();
        Table valuesForKey = new Table();
        if(height == 0)
        {
            for(int i=0;i<currentNode.entryCount;i++)
            {
                if(cell.compareTo(entries[i].getKey())==0)
                    return entries[i].getValue();
            }
        }
        else
        {
            for(int j=0;j<currentNode.entryCount;j++)
            {
                if(j+1 == currentNode.entryCount || cell.compareTo(entries[j+1].getKey()) > 0)
                    return this.get(entries[j].child,cell,height-1);
            }
        }
        return null;
    }

    /**
     * gets all values with keys bigger then the given key
     * @param key
     * @return
     */
    Table getAllBigger(Cell key)
    {
        return getAllBigger(this.root,key,this.height);
    }

    Table getAllBigger(Node currentNode,Cell key,int height)
    {
        Entry[] entries = currentNode.getEntries();
        Table valuesGreaterThenKey = new Table();
        if(height == 0)
        {
            for(int i=0;i<currentNode.entryCount;i++)
            {
                if(key.compareTo(entries[i].getKey()) > 0)
                    valuesGreaterThenKey.add(entries[i].getValue());
            }
            return valuesGreaterThenKey;
        }
        else
        {
            for(int j=0;j<currentNode.entryCount;j++)
            {
                if(key.compareTo(entries[j].getKey()) >=0 || (j==0 && key.compareTo(entries[j+1].getKey())>0))
                    valuesGreaterThenKey.add(getAllBigger(entries[j].child,key,height-1));
            }
            return valuesGreaterThenKey;
        }
    }

    /**
     * gets all values with keys being less then the given key
     * @param key
     * @return
     */
    Table getAllLess(Cell key)
    {
        return getAllLess(this.root,key,this.height);
    }

    Table getAllLess(Node currentNode,Cell key,int height)
    {
        Entry[] entries = currentNode.getEntries();
        Table valuesLessThenKey = new Table();
        if(height == 0)
        {
            for(int i=0;i<currentNode.entryCount;i++)
            {
                if(key.compareTo(entries[i].getKey()) < 0 && entries[i].getValue() !=null)
                    valuesLessThenKey.add(entries[i].getValue());
            }
            return valuesLessThenKey;
        }
        else
        {
            for(int j=0;j<currentNode.entryCount;j++)
            {
                if(key.compareTo(entries[j].getKey()) < 0)
                    valuesLessThenKey.add(getAllLess(entries[j].child,key,height-1));
            }
            return valuesLessThenKey;
        }
    }

    /**
     * gets all values with keys being equal to or greater then the given key
     * @param key
     * @return
     */
    Table getAllEqualAndGreater(Cell key)
    {
        Table all = get(key);
        all.add(getAllBigger(key));
        return all;
    }

    /**
     * gets all values with keys equal or less then given key
     * @param key
     * @return
     */
    Table getAllEqualAndLess(Cell key)
    {
        Table all = get(key);
        all.add(getAllLess(key));
        return all;
    }

    /**
     * gets all values with keys not equal to the given key
     * @param key
     * @return
     */
    Table getAllNotEqual(Cell key)
    {
        Table allNot = this.getAllBigger(key);
        allNot.add(getAllLess(key));
        return allNot;
    }

    /**
     * deletes the given row from set of values associated with that key
     * @param key
     * @param value
     */
    void delete(Cell key,Row value)
    {
        Table thisKeysValues = get(key);
        if(thisKeysValues == null)
            return;
        for(int i=0;i<thisKeysValues.size();i++)
        {
            if(value.equal(thisKeysValues.get(i)))
            {
                get(key).remove(i);
                n--;
            }
        }

    }

    int getAmountOfKeyValuePairs()
    {
        return this.n;
    }
}
