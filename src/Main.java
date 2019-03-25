
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
    private int spot;
    public Dict(String n)	{ word = n;}
    public String getName()			    { return word; }
    public int getSpot(){return spot;}
    public String getMessage()
    {
        String s = String.format("%s", word);
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
    protected ArrayList<String> Word,SearchResult,Keep;		 // graph nodes

    protected Graph<String, DefaultWeightedEdge>                 G,GSearch;
    private   SimpleWeightedGraph<String, DefaultWeightedEdge>  SG;
    protected DijkstraShortestPath<String, DefaultWeightedEdge>         DSP;
    private String fileName,searchWord,Word1,Word2;
    private File wordFile;
    private boolean check = false,all = true;
    private int counter = 0,pos,ncount,npos,spot;

    public Project(){
        System.out.printf("Enter graph file : ");
        Scanner scan = new Scanner(System.in);
        fileName = scan.next();
        SG = new SimpleWeightedGraph<String, DefaultWeightedEdge>(DefaultWeightedEdge.class);
        GSearch = (Graph<String, DefaultWeightedEdge>)SG;
        spot = 1;
        try{
            wordFile = new File(fileName);
            Scanner readFile = new Scanner (wordFile);
            AllWord = new HashMap<String, Dict>();
            Word = new ArrayList<String>();
            Keep = new ArrayList<String>();
            String w1 = readFile.nextLine(),w2 = "";
            while(readFile.hasNextLine())
            {
                w2 = readFile.nextLine();
                if (!AllWord.containsKey(w1))
                {
                    AllWord.put(w1,new Dict(w1));
                }
                if(!Word.contains(w1))
                {
                    Word.add(w1);
                }
                Keep.add(w1);
                Graphs.addEdgeWithVertices(GSearch,w1,w2,spot);
                spot++;
                w1 = w2;
            }
        }catch(Exception e){ System.out.printf("ERROR! : " + e ); System.exit(0);}
        System.out.printf("HELLO,WORLD!!");
//        report(Keep);
        SG = new SimpleWeightedGraph<String, DefaultWeightedEdge>(DefaultWeightedEdge.class);
        G  = (Graph<String, DefaultWeightedEdge>)SG;
        while(all)
        {
            for (int e = 0 ; e < Keep.size(); e++)  // change w1 to all word in file
            {
                String w1 = Keep.get(e),w2 = " ";
                for(int f = 0; f < Keep.size(); f++)  // change w2 for checking char with w1
                {
                    w2 = Keep.get(f);
                    ncount = 0;
                    for( int  k = 0 ; k < 5 ; k++)// check every char if it's the same  if it's not the same put counter to count
                    // if counter == 1 , then these 2 word is different by 1 char and can put into new graph
                    {
                        if(w1.charAt(k) != w2.charAt(k)){ncount++; npos = k;} // if char w1 != char w2 counter++
                    }
                    if (ncount == 1) {
                            int asc1 = checkAlOrder(w1.charAt(npos)),asc2 = checkAlOrder(w2.charAt(npos));
                            int weight = Math.abs(asc1-asc2);
                            Graphs.addEdgeWithVertices(G,w1,w2,weight);
                            //Keep.remove(e);
//                            break;
                        }
                }
                //System.out.println("change w1");
            }
            System.out.print("\nEnd while");
            all = false;
        }
//        printGraph();
        System.out.printf("\nDo you want to search for the word or transform the word?\n =>");
        String choice = scan.next();
        switch(choice)
        {
            case "search" :
                System.out.print("\nSearch => ");
                String inSearch = scan.next();Search(inSearch);report(SearchResult);
                break;
            case "transform":Transform();break;
        }
        System.out.println("\n--------END PROGRAM-------");
    }

    public void Search(String n)
    {
        Set<DefaultWeightedEdge> allEdges = GSearch.edgeSet();
        searchWord = n;
        ArrayList<Character> arrayInput = new ArrayList<Character>();
        SearchResult = new ArrayList<String>();
        for(int i = 0; i < searchWord.length(); i++)
            {
                arrayInput.add(searchWord.charAt(i));
            }
        for (DefaultWeightedEdge e : allEdges)
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
            if(check){SearchResult.add(word);}
            else continue;
        }
        Collections.sort(SearchResult);
    }

    public void report(ArrayList<String> a){
        for(int i = 0; i< a.size(); i++)
        {System.out.println(a.get(i));}
    }

    public void Transform() // this part need works
    {
        System.out.print("Enter 5 - letters word 1 : ");
        Scanner scan = new Scanner(System.in);
        String k1 = scan.next();
        Word1 = k1;
        System.out.println("Enter 5 - letters word 2 : ");
        String k2 = scan.next();
        Word2 = k2;
        System.out.printf("\n%s",Word1);
        if (G.containsVertex(k1) && G.containsVertex(k2))
        {
            DSP = new DijkstraShortestPath<String, DefaultWeightedEdge>(SG, k1, k2);
            List<DefaultWeightedEdge> path = DSP.getPathEdgeList();
            if (path != null)
            {
                printDefaultWeightedEdges(path, true);
            }
            else
                System.out.printf("\nCannot transform %s into %s\n", k1, k2);
        }
    }

    public int checkAlOrder(char i)
    {
        int asc = (int) i - 96;
        return asc;
    }

    public void printDefaultWeightedEdges(Collection<DefaultWeightedEdge> E, boolean f)
    {
        for (DefaultWeightedEdge e : E)
        {
            //System.out.println(e.toString());
            //Dict source = searchPoint(G.getEdgeSource(e));

            Dict target = searchPoint(G.getEdgeTarget(e));
            double  weight = G.getEdgeWeight(e);
            if (f)  // print Country details
                System.out.printf("\n%s (+%.0f)", target.getMessage(),weight);
            else    // print only Country name
            {System.out.printf("\n%s - %s", target.getName());}
            G.removeVertex(G.getEdgeTarget(e));
        }
        //add = false;
    }

    public Dict searchPoint(String name)
    {
        return AllWord.get(name);
    }

    public void printGraph()
    {
        Set<DefaultWeightedEdge> allEdges = G.edgeSet();
        printDefaultWeightedEdges(allEdges, true);
    }


}   // END CLASS PROJECT


public class Main {

    public static void main(String[] args)
    {
        Project mygraph = new Project();
    }
}
