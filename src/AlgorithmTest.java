import junit.framework.TestCase;

/**
 * Created by m on 7/1/16.
 */
public class AlgorithmTest extends TestCase {

    static final float[] CURRENT_POSITION = new float[]{4, 4};

    static int[][] WEIGHT_MATRIX = new int[][]
            {
                    {0, 1, 3, 3, 2, 2, 2, 2, 2},
                    {0, 3, 3, 2, 2, 2, 2, 2, 2},
                    {3, 3, 2, 2, 2, 2, 2, 2, 3},
                    {2, 2, 1, 2, 2, 2, 2, 3, 1},
                    {2, 1, 2, 2, 3, 2, 2, 1, 1},
                    {1, 2, 2, 2, 2, 4, 2, 1, 0},
                    {2, 1, 2, 2, 2, 2, 2, 2, 0},
                    {2, 1, 2, 2, 2, 2, 2, 2, 0},
                    {1, 2, 2, 2, 2, 0, 0, 0, 2}
            };

    public void testName() throws Exception {
        final int[][] nodeList = Algorithm.createNodeList(CURRENT_POSITION);
         printMatrix(nodeList);
        final int[][] adjacencyMatrix = Algorithm.createAdjacencyMatrix(nodeList, WEIGHT_MATRIX);
        printMatrix(adjacencyMatrix);

        final int[] dijkstra = Algorithm.dijkstra(adjacencyMatrix);
        printMatrix(dijkstra);
    }

    static void printMatrix(int[] array) {
        for (int m : array) {
            System.out.print(m + " ");
            System.out.println();
        }
    }

    static void printMatrix(int[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            final int[] m = matrix[i];
            System.out.printf("%2s| ", i);
            for (int j = 0; j < m.length; j++) {
                final int n = m[j];
                System.out.print(n + " ");
            }
            System.out.println();
        }
    }
}
