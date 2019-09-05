package apps;
import structures.Graph;
import structures.Vertex;
import java.io.IOException;


/**
 * Created by sid on 4/26/17.
 */
public class driver {
    public static void main(String[] args) {
        Graph g = null;
        try {
            g = new Graph("graph1.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }

        PartialTreeList ptl = MST.initialize(g);

        System.out.print(MST.execute(ptl));

    }
}
