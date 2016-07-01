import junit.framework.Assert;
import junit.framework.TestCase;

import java.util.Arrays;

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
//         printMatrix(nodeList);
        final int[][] adjacencyMatrix = Algorithm.createAdjacencyMatrix(nodeList, WEIGHT_MATRIX);
//        printMatrix(adjacencyMatrix);

        final int[][] distancesAndPrevious = Algorithm.dijkstra(adjacencyMatrix);

        final int[][] paths = Algorithm.getAllPaths(distancesAndPrevious[1]);

        final int[] liftOfNodesWithScore = Algorithm.getScoresFromPathsAndDistances(paths, distancesAndPrevious[1], distancesAndPrevious[0]);

        int lowestScore = Integer.MAX_VALUE;
        int longestPath = 0;

        int bestNode = -1;
        for (int node = 0; node < liftOfNodesWithScore.length; node++) {
            final int score = liftOfNodesWithScore[node];
            final int pathLength = paths[node].length;

            if (lowestScore > score && pathLength > longestPath) {
                longestPath = pathLength;
                lowestScore = score;
                bestNode = node;
            }
        }

        System.out.println("Best node is number " + bestNode + " with score " + lowestScore);
        System.out.println("Paths is: " + Arrays.toString(paths[bestNode]));

        Assert.assertEquals(26, bestNode);
        Assert.assertEquals(150, lowestScore);
    }

    static void printMatrix(int[] array) {
        for (int i = 0; i < array.length; i++) {
            System.out.printf("%2s| %s\n", i, array[i]);
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
