import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class ShortestPath {
    // A utility function to find the vertex with minimum distance value,
    // from the set of vertices not yet included in shortest path tree
    static final int V=8;
    static Map<String,Integer> zip;
    static HashMap<String,Integer> nearestNeighbour;
    int minDistance(int dist[], Boolean sptSet[])
    {
        // Initialize min value
        int min = Integer.MAX_VALUE, min_index=-1;

        for (int v = 0; v < V; v++)
            if (sptSet[v] == false && dist[v] <= min)
            {
                min = dist[v];
                min_index = v;
            }

        return min_index;
    }

    // A utility function to print the constructed distance array
    HashMap<String,Integer> printSolution(int[] dist, int v, int src) throws IOException {
        FileWriter fw;
        BufferedWriter bw;
        fw = new FileWriter("data/Output.txt",true);
        bw = new BufferedWriter(fw);
        nearestNeighbour = new HashMap<String, Integer>();
        String nearestZipCode = null;
        int distance = 0;
        bw.write("Source->Destination   Distance from Source\n");
        for (int i = 0; i < V; i++)
            for(Map.Entry<String, Integer> entry : zip.entrySet()){
                if(entry.getValue().equals(i)){
                    if(!getLabel(src).equals(entry.getKey())){
                        //System.out.println(getLabel(src)+"->"+entry.getKey()+"\t\t\t "+dist[i]);
                        nearestNeighbour.put(entry.getKey(),dist[i]);
                        //distances[i] = dist[i];
                        bw.write(getLabel(src)+"->"+entry.getKey()+"\t\t\t "+dist[i] );
                        bw.newLine();
                    }
                    else{
                        //System.out.println(getLabel(src)+"->"+entry.getKey()+"\t\t\t "+Double.POSITIVE_INFINITY);
                        nearestNeighbour.put(entry.getKey(),Integer.MAX_VALUE);
                        bw.write(getLabel(src)+"->"+entry.getKey()+"\t\t\t "+Double.POSITIVE_INFINITY);
                        bw.newLine();
                    }

                }
            }
            bw.write(("-------------------------------------------------------------------------------------"));
            bw.newLine();

        //int min = Collections.min(nearestNeighbour.values());
        //int n = distances.length;


        bw.flush();
        bw.close();
        return nearestNeighbour;
    }

    // Funtion that implements Dijkstra's single source shortest path
    // algorithm for a graph represented using adjacency matrix
    // representation
    HashMap<String, Integer> dijkstra(int graph[][], int src) throws IOException {
        int dist[] = new int[V]; // The output array. dist[i] will hold
        // the shortest distance from src to i

        // sptSet[i] will true if vertex i is included in shortest
        // path tree or shortest distance from src to i is finalized
        Boolean sptSet[] = new Boolean[V];

        // Initialize all distances as INFINITE and stpSet[] as false
        for (int i = 0; i < V; i++)
        {
            dist[i] = Integer.MAX_VALUE;
            sptSet[i] = false;
        }

        // Distance of source vertex from itself is always 0
        dist[src] = 0;

        // Find shortest path for all vertices
        for (int count = 0; count < V-1; count++)
        {
            // Pick the minimum distance vertex from the set of vertices
            // not yet processed. u is always equal to src in first
            // iteration.
            int u = minDistance(dist, sptSet);

            // Mark the picked vertex as processed
            sptSet[u] = true;

            // Update dist value of the adjacent vertices of the
            // picked vertex.
            for (int v = 0; v < V; v++)

                // Update dist[v] only if is not in sptSet, there is an
                // edge from u to v, and total weight of path from src to
                // v through u is smaller than current value of dist[v]
                if (!sptSet[v] && graph[u][v]!=0 &&
                        dist[u] != Integer.MAX_VALUE &&
                        dist[u]+graph[u][v] < dist[v])
                    dist[v] = dist[u] + graph[u][v];
        }

        // print the constructed distance array
        HashMap<String,Integer> result = printSolution(dist, V,src);
        return result;
    }

    // Driver method
    public HashMap<String,Integer> algorithmImplementation (String requestZipCode) throws IOException {
        /* Let us create the example graph discussed above */

        zip= new HashMap<String,Integer>();
        BufferedReader in = new BufferedReader(new FileReader("data/Label.txt"));
        String line = "";
        while ((line = in.readLine()) != null) {
            String parts[] = line.split(",");
            zip.put(parts[0], Integer.parseInt(parts[1]));
        }
        in.close();

        int sourceZip=0;
        for (Map.Entry<String, Integer> entry : zip.entrySet()) {
            if(entry.getKey().equals(requestZipCode)){
                sourceZip=entry.getValue();
            }
        }
        int graph[][] = new int[8][8];

        File reqFile = new File("data/DistanceTable.txt");
        BufferedReader breader = new BufferedReader(new FileReader(reqFile));
        String line1 = "";
        while ((line1 = breader.readLine()) != null) {
            String parts[] = line1.split(",");
            //edges[i] = new Edge(new Vertex(parts[0].substring(parts[0].length()-1)),new Vertex(parts[1].substring(parts[1].length()-1)), new Integer(parts[2]));
            int i =  Integer.parseInt(parts[0].substring(parts[0].length()-1));
            int j = Integer.parseInt(parts[1].substring(parts[1].length()-1));
            int distance = Integer.parseInt(parts[2]);
            graph[i][j] = distance;
            graph[j][i] = distance;
        }

        breader.close();

        //System.out.println(Arrays.deepToString(graph));

        ShortestPath t = new ShortestPath();
        HashMap<String,Integer> result = t.dijkstra(graph, Integer.parseInt(requestZipCode.substring(requestZipCode.length()-1)));
        return result;

    }

    public String getLabel(int x)
    {
        return "6411"+Integer.toString(x);
    }
}
