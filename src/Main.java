
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
    public Dict(String n)	{ word = n;}
    public String getName()			    { return word; }
    public String getMessage()
    {
        String s = String.format("%s", word);
        return s;
    }
    public void print()
    {
        System.out.println( getMessage() );
    }
    public boolean equals(Object o)
    {
        Dict other = (Dict) o;
        return this.word.equalsIgnoreCase(other.word);
    }
    public int hashCode()
    {
        return word.toLowerCase().hashCode();
    }
}

class Project{
    protected HashMap<String, Dict>  AllWord;
    protected ArrayList<String> Word,SearchResult,Keep;

    protected Graph<String, DefaultWeightedEdge>                 G;
    private   SimpleWeightedGraph<String, DefaultWeightedEdge>  SG;
    protected DijkstraShortestPath<String, DefaultWeightedEdge>         DSP;

    private String fileName,searchWord,Word1,Word2,Prev;
    private File wordFile;
    private boolean check = false,all = true;
    private int ncount,npos;

    public Project(){
        System.out.printf("Enter graph file : ");
        Scanner scan = new Scanner(System.in);
        String con;
        do {
            try {
                fileName = scan.next();
                wordFile = new File(fileName);
                Scanner readFile = new Scanner(wordFile);
                AllWord = new HashMap<String, Dict>();
                Word = new ArrayList<String>();
                Keep = new ArrayList<String>();
                String w1 = readFile.nextLine(), w2 = "";
                while (readFile.hasNextLine()) {
                    w2 = readFile.nextLine();
                    if (!AllWord.containsKey(w1)) {
                        AllWord.put(w1, new Dict(w1));
                    }
                    if (!Word.contains(w1)) {
                        Word.add(w1);
                    }
                    Keep.add(w1);
                    w1 = w2;
                }
                Keep.add(w1);
            } catch (Exception e) {
                System.out.printf("ERROR! : " + e);
                System.out.printf("\nEnter new file :");
            }
        } while (!wordFile.isFile());
        SG = new SimpleWeightedGraph<String, DefaultWeightedEdge>(DefaultWeightedEdge.class);
        G = (Graph<String, DefaultWeightedEdge>) SG;
        while (all) {
            for (int e = 0; e < Keep.size(); e++) {
                String w1 = Keep.get(e), w2 = " ";
                for (int f = 0; f < Keep.size(); f++) {
                    w2 = Keep.get(f);
                    ncount = 0;
                    for (int k = 0; k < 5; k++) {
                        if (w1.charAt(k) != w2.charAt(k)) {
                            ncount++;
                            npos = k;
                        }
                    }
                    if (ncount == 1) {
                        int asc1 = checkAlOrder(w1.charAt(npos)), asc2 = checkAlOrder(w2.charAt(npos));
                        int weight = Math.abs(asc1 - asc2);
                        Graphs.addEdgeWithVertices(G, w1, w2, weight);
                    }
                }
            }
            all = false;
        }
        do {
            System.out.printf("\nDo you want to search for the word or transform the word?\n =>");
            String choice = scan.next();
            int s = 0, t = 0;
            s = cmpString(choice, "search");
            t = cmpString(choice, "transform");
            boolean pass = false;
            do {
                s = cmpString(choice, "search");
                t = cmpString(choice, "transform");
                if (s == 0) {
                    System.out.print("\nSearch => ");
                    String inSearch = scan.next();
                    Search(inSearch);
                    report(SearchResult);
                    pass = true;
                } else if (t == 0) { Transform();pass = true; }
                else {System.out.printf("ERROR! Choice not found. Enter input again : ");choice = scan.next(); pass = false;}
            }while(!pass);
            System.out.printf("\nContinue (y/n)? >> ");
            con = scan.next();
        }while(cmpString(con,"y") == 0);
        System.out.println("\n--------END PROGRAM-------");
    }

    public void Search(String n)
    {
        searchWord = n;
        ArrayList<Character> arrayInput = new ArrayList<Character>();
        SearchResult = new ArrayList<String>();
        for(int i = 0; i < searchWord.length(); i++)
            {
                arrayInput.add(searchWord.charAt(i));
            }
        for (int j = 0; j < Keep.size(); j++)
        {
            String word = Keep.get(j);
            for(int i = 0; i <searchWord.length(); i++)
            {
                if(word.contains(searchWord))
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

    public void Transform()
    {
        System.out.print("Enter 5 - letters word 1 : ");
        Scanner scan = new Scanner(System.in);
        String k1 = scan.next();
        Word1 = k1;
        System.out.print("Enter 5 - letters word 2 : ");
        String k2 = scan.next();
        Word2 = k2;
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
        else {
            if(!G.containsVertex(k1))System.out.printf("%s is not in the file",k1);
            if(!G.containsVertex(k2)) System.out.printf("%s is not in the file",k2);
        }
    }

    public int checkAlOrder(char i)
    {
        int asc = (int) i - 96;
        return asc;
    }

    public int cmpString(String a , String b) {return a.compareToIgnoreCase(b);}

    public void printDefaultWeightedEdges(Collection<DefaultWeightedEdge> E, boolean f)
    {
        Prev = Word1;
        System.out.printf("\n%s",Word1);
        double total = 0;
        for (DefaultWeightedEdge e : E)
        {
            Dict source = searchPoint(G.getEdgeSource(e));
            Dict target = searchPoint(G.getEdgeTarget(e));
            String sourceWord = source.getMessage();
            String targetWord = target.getMessage();
            String printWord = targetWord;
            if(Prev.equals(sourceWord)){printWord = targetWord;}
            else if (Prev.equals(targetWord)){printWord = sourceWord;}
            double  weight = G.getEdgeWeight(e);
            total = total + weight;
            if (f)
                System.out.printf("\n%s (+%.0f)", printWord,weight);
            else
            {System.out.printf("\n%s - %s", target.getName());}
            Prev = printWord;
        }
        System.out.printf("\nTotal cost = %.0f",total);
    }

    public Dict searchPoint(String name)
    {
        return AllWord.get(name);
    }

}   // END CLASS PROJECT


public class Main {

    public static void main(String[] args)
    {
        Project mygraph = new Project();
    }
}
