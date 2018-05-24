package encrypter.justforkicks;

import java.util.ArrayDeque;
import java.util.Queue;

import static java.util.Arrays.binarySearch;
//(char+amount to shift%26)+96

public class StringEncrypter
{
    private ArrayDeque<Integer> keyHolder;
    int amntOfKeys;
    private char[] letters = {'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z'};

    public StringEncrypter(int[] originalCode)
    {
        amntOfKeys = originalCode.length;
        keyHolder = new ArrayDeque<>(originalCode.length);
        for(int i=0;i<originalCode.length;i++) keyHolder.add(originalCode[i]);
    }

    public String encrypt(String message)
    {
        ArrayDeque<Integer> encrypter = keyHolder.clone();
        char[] toEncrypt = message.toCharArray();
        for(int i=0;i<toEncrypt.length;i++)
        {
           if(toEncrypt[i] != ' ')
           {
               int shift = encrypter.remove();
               int index = binarySearch(letters,toEncrypt[i]);
               char letter = letters[(shift+index)%25];
               toEncrypt[i] = letter;
               if(encrypter.isEmpty())
               {
                   encrypter = keyHolder.clone();
               }
           }
        }
        String encrypted = "";
        for(int i=0;i<toEncrypt.length;i++)
            encrypted += toEncrypt[i];
        return encrypted;
    }

    public String decrypt(String message)
    {
        ArrayDeque<Integer> decrypter = keyHolder.clone();
        char[] toDecrypt = message.toCharArray();
        for(int i=0;i<toDecrypt.length;i++)
        {
            if(toDecrypt[i] != ' ')
            {
                int shift = decrypter.remove()%25;
                int index = binarySearch(letters,toDecrypt[i]);
                char letter;
                if(index-shift<0)
                    letter = letters[25-Math.abs(index-shift)];
                else
                    letter = letters[index-shift];
                toDecrypt[i] = letter;
                if(decrypter.isEmpty())
                {
                    decrypter = keyHolder.clone();
                }
            }
        }
        String encrypted = "";
        for(int i=0;i<toDecrypt.length;i++)
            encrypted += toDecrypt[i];
        return encrypted;
    }

    public void changeKey(int week,int month)
    {
        if(week%2 == 0 && month%2==0)
            changeKey(5);
        if(week%2 ==1 & month%2 ==1)
            changeKey(6);
        else
            changeKey(7);
    }

    private void changeKey(int change)
    {
        for(int i=0;i<amntOfKeys;i++)
        {
            int changing = keyHolder.remove();
            changing = changing*change;
            keyHolder.add(changing);
        }
    }
    public static void main(String[] args)
    {
        int[] keys = {1,2,3,4,5};
        StringEncrypter encrypter = new StringEncrypter(keys);
        String checking = encrypter.encrypt("hello my name is daniel schaffel");
        System.out.println(checking);
        System.out.println(encrypter.decrypt(checking));
        encrypter.changeKey(2,4);
        String checker2 = encrypter.encrypt("hello");
        System.out.println(checker2);
        System.out.println(encrypter.decrypt(checker2));
    }
}
