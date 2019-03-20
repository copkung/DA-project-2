
import java.util.*;

import org.jgrapht.*;
import org.jgrapht.graph.*;
import org.jgrapht.alg.*;
import sun.tools.tree.CharExpression;

import java.io.*;
import java.lang.*;


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
}


class Project{
    protected HashMap<String, Dict>  AllWord;// real objects
    protected ArrayList<String> Word;		 // graph nodes
    protected ArrayDeque<String> closedPath /*= new ArrayDeque<>()*/;

    protected Graph<String, DefaultEdge>                       G;
    private   SimpleGraph<String, DefaultEdge>                 SG;
    protected ConnectivityInspector<String, DefaultEdge>       conn;
    protected KruskalMinimumSpanningTree<String, DefaultEdge>  MST;
    protected String fileName,p1,p2,searchWord;
    protected File wordFile;
    protected boolean check = false;

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
        ArrayDeque<String> temp = new ArrayDeque<>(Word);
        for (int i = 1 ; i - 1 < temp.size();i++)
        {
            //System.out.print(i);
            String w1 = temp.pop();
            String w2 = temp.peek();
             G.addEdge(w1,w2);
        }
        //printGraph(); print the graph out
        System.out.printf("Do you want to search for the word or transform the word?");
        scan.next();
        if(scan.next() == "search" || scan.next() == "Search"){ Search();}
    }

    public void Search()
    {
        Set<DefaultEdge> allEdges = G.edgeSet();
        Scanner scan = new Scanner(System.in);
        System.out.print("\nSearch = ");
        searchWord = scan.next();
        ArrayList<Character> arrayInput = new ArrayList<Character>();
        for(int i = 0; i < searchWord.length(); i++)
            {
                arrayInput.add(searchWord.charAt(i));
            }
            System.out.print(searchWord.length() + "\n");

        for (DefaultEdge e : allEdges)
        {
            String word = searchPoint(G.getEdgeSource(e)).getMessage();
            for(int i = 0; i <searchWord.length(); i++)
            {
                if(searchWord.charAt(i) == word.charAt(i))
                {
                    check = true;
                    /*System.out.printf("\n TRUE!!");*/
                }
                else {check = false;/*System.out.printf("\n False!");*/ break;}
            }
            if(check){System.out.println(word);}
            else continue;
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

    public void printGraph()
    {
        Set<DefaultEdge> allEdges = G.edgeSet();
        printDefaultEdges(allEdges, true);
    }

    public void testMST()
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
    }

}


public class Main {

    public static void main(String[] args)
    {
        Project mygraph = new Project();
    }
}
