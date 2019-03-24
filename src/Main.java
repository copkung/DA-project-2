
import java.util.*;

import org.jgrapht.*;
import org.jgrapht.graph.*;
import org.jgrapht.alg.*;
import sun.tools.tree.CharExpression;

import java.io.*;
import java.lang.*;
import java.math.*;


class Dict
{
    private String word;
    public Dict(String n)	{ word = n; }
    public String getName()			    { return word; }
    public String getMessage()
    {
        String s = String.format("%s ", word);
        return s;
    }
    public void print()
    {
        System.out.println( getMessage() );
    }
    // equality based on name
    public boolean equals(Object o)
    {
        Dict other = (Dict) o;
        return this.word.equalsIgnoreCase(other.word);
    }
    // hashcode based on the hashcode of name
    public int hashCode()
    {
        return word.toLowerCase().hashCode();
    }
}   //END CLASS DICT


class Project{
    protected HashMap<String, Dict>  AllWord;// real objects
    protected ArrayList<String> Word,SearchResult;		 // graph nodes
    protected ArrayDeque<String> closedPath,TransTemp /*= new ArrayDeque<>()*/;

    protected Graph<String, DefaultEdge>                       G;
    private   SimpleGraph<String, DefaultEdge>                 SG;
   /* protected ConnectivityInspector<String, DefaultEdge>       conn;
    protected KruskalMinimumSpanningTree<String, DefaultEdge>  MST;*/
    private String fileName,searchWord,Word1,Word2;
    private File wordFile;
    private boolean check = false;
    private int counter = 0,pos;

    public Project(){
        System.out.printf("Enter graph file : ");
        Scanner scan = new Scanner(System.in);
        fileName = scan.next();
        closedPath = new ArrayDeque<String>();
        try{
            wordFile = new File(fileName);
            Scanner readFile = new Scanner (wordFile);
            AllWord = new HashMap<String, Dict>();
            Word = new ArrayList<String>();
            SG = new SimpleGraph<String, DefaultEdge>(DefaultEdge.class);
            G  = (Graph<String, DefaultEdge>)SG;
            while(readFile.hasNextLine())
            {
                String w1 = readFile.nextLine();
                if (!AllWord.containsKey(w1))
                {
                    AllWord.put(w1,new Dict(w1));
                }
                if(!Word.contains(w1))
                {
                    Word.add(w1);
                }
            }
        }catch(Exception e){ System.out.printf("ERROR! : " + e ); System.exit(0);}
        Graphs.addAllVertices(G,Word);
        ArrayList<String> list = new ArrayList<String>(Word);
        Collections.sort(list);     // Sort all words in order
        ArrayDeque<String> temp = new ArrayDeque<>(list);   // put the ordered words into the arraydeque
        for (int i = 1 ; i - 1 < temp.size();i++)
        {
            String w1 = temp.pop();
            String w2 = temp.peek();
             G.addEdge(w1,w2);      // add and connect together to the graph
        }
        //printGraph(); print the graph out
        System.out.printf("Do you want to search for the word or transform the word?\n =>");
        String choice = scan.next();
        //System.out.printf(choice);
        switch(choice)
        {
            case "search" :
                System.out.print("\nSearch => ");
                String inSearch = scan.next();Search(inSearch);report(SearchResult);
                break;
            case "transform":Transform();break;
        }
        System.out.println("--------END PROGRAM-------");
    }

    public void Search(String n)
    {
        Set<DefaultEdge> allEdges = G.edgeSet();
        searchWord = n;
        ArrayList<Character> arrayInput = new ArrayList<Character>();
        TransTemp = new ArrayDeque<String>();
        SearchResult = new ArrayList<String>();
        for(int i = 0; i < searchWord.length(); i++)
            {
                arrayInput.add(searchWord.charAt(i));
            }

        for (DefaultEdge e : allEdges)
        {
            String word = searchPoint(G.getEdgeSource(e)).getMessage();
            for(int i = 0; i <searchWord.length(); i++)
            {
                if(searchWord.charAt(i) == word.charAt(i))
                {
                    check = true;
                }
                else {check = false;break;}
            }
            if(check){TransTemp.add(word);SearchResult.add(word);}
            else continue;
        }
    }

    public void report(ArrayList<String> a){
        for(int i = 0; i< a.size(); i++)
        {System.out.println(a.get(i));}
    }

    public void Transform()
    {
        System.out.print("Enter 5 - letters word 1 : ");
        Scanner scan = new Scanner(System.in);
        Word1 = scan.next();
        System.out.println("Enter 5 - letters word 2 : ");
        Word2 = scan.next();
        int check = cmpStr(Word1,Word2),i;
        String next = "",Cur = Word1;
        boolean same = true;
        String subCur;
        char cur,nchar;
        while(check != 0) {
            for (i = 3; i >= 0; i--)
            {
                subCur = Cur.substring(0, i);
                Search(subCur);
                if (!TransTemp.isEmpty()) break;
            }
            //need to fix this for j loop -> changing algorithm
            for (int j = 0; j < TransTemp.size(); j++)
            {
                next = TransTemp.pop();
                check(Cur,next);
                if(counter == 1) break;
            }
            cur = Cur.charAt(pos);nchar = next.charAt(pos);

            System.out.printf("This is cur :" + Cur);
            System.out.printf("\n This is next : " + next);
            Cur = next;
            int asc1 = (int)cur,asc2 = (int) nchar,diff;
            diff = Math.abs(asc1-asc2);
            System.out.printf("\n" + next + "(+%d)",diff);
            check = cmpStr(Cur,Word2);
            check = 0;
        }// end while
    }

    public int checkAlOrder(char i)
    {
        int ascii = (int) i - 96;
        return ascii;
    }

    public int cmpStr(String a, String b)
    {
        return a.compareTo(b);
    }
    // It returns 0 when the strings are equal otherwise it returns positive or negative value.

    public void check(String a, String b)
    {
        counter = 0;
        char cA,cB;
        for (int i = 0 ; i < 5 ; i++)
        {
            cA = a.charAt(i);cB = b.charAt(i);
            if(cA != cB){counter++; pos = i;}
        }
    }

    public void printDefaultEdges(Collection<DefaultEdge> E, boolean f)
    {
        for (DefaultEdge e : E)
        {
            //System.out.println(e.toString());
            Dict source = searchPoint(G.getEdgeSource(e));
            Dict target = searchPoint(G.getEdgeTarget(e));
            if (f)  // print Country details
                System.out.printf("%6s - %6s\n",
                        source.getMessage(), target.getMessage());
            else    // print only Country name
            {System.out.printf("%s - %s  ", source.getName(), target.getName());}
        }
        //add = false;
    }

    public Dict searchPoint(String name)
    {
        return AllWord.get(name);
    }

   /* public void printGraph()
    {
        Set<DefaultEdge> allEdges = G.edgeSet();
        printDefaultEdges(allEdges, true);
    }*/

    /*public void testMST()
    {
        conn = new ConnectivityInspector<String, DefaultEdge>(SG);
        if (conn.isGraphConnected())
        {
            System.out.println("\nGraph is connected");
            MST = new KruskalMinimumSpanningTree<String, DefaultEdge>(SG);
            Set<DefaultEdge> treeEdges = MST.getEdgeSet();
            System.out.printf("MST edge length = %.0f \n", MST.getSpanningTreeCost());
            printDefaultEdges(treeEdges, true);
        }
        else
            System.out.println("\nGraph is not connected");
    }*/

}   // END CLASS PROJECT


public class Main {

    public static void main(String[] args)
    {
        Project mygraph = new Project();
    }
}
