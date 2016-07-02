import java.util.Arrays;
import java.util.Random;

/**
 * Created by m on 6/26/16.
 */
public class Algorithm {
    public static final int NO_PREVIOUS = -1;
    public static final int NO_WAY = 0;

    private static final Random rnd = new Random(100);

    static float[] getNextVector(final Board board, final int stone) {

        final int[][] fields = board.getFields();
        final float[] currentPosition = board.getStonePosition(stone);
        final int clusterSize = getClusterSizeForStone(stone);

        // NB: nodeList[0] = new int[] {x,y} field coordinates
        final int[][] nodeList = createNodeList(currentPosition, clusterSize);
        final int[][] weightMatrix = createWeightMatrix(nodeList, fields, board.myPlayerNumber);
        final int[][] adjacencyMatrix = createAdjacencyMatrix(nodeList, weightMatrix);
        final int[][] distancesAndPrevious = dijkstra(adjacencyMatrix, currentPosition, nodeList);
        final int[][] paths = Algorithm.getAllPaths(distancesAndPrevious[1]);
        final int[] bestPath = Algorithm.calcBestPathFromPathsAndDistances(paths, distancesAndPrevious[0], stone);
        final int[][] pathFields = mapNodesToFields(nodeList, bestPath);
        final float[] vector = getVectorFromPath(bestPath, nodeList, currentPosition);

        if (stone == 1) {
            System.out.println(board.toString(pathFields));
            System.out.println(stone + ": " + Arrays.toString(vector));
        }
        return vector;
    }

    static int getClusterSizeForStone(final int stone) {
        switch (stone) {
            case 0:
                return 6;
            case 1:
                return 4;
            default:
            case 2:
                return 3;
        }
    }

    static int[][] createNodeList(final float[] currentPosition, final int clusterSize) {
        int maxPathLength = (clusterSize + clusterSize + 1) * (clusterSize + clusterSize + 1);

        final int[][] nodeList = new int[maxPathLength][];
        int currentNodeIndex = 0;

        final int playerPosX = (int) currentPosition[0];
        final int playerPosY = (int) currentPosition[1];

        for (int y = playerPosY - clusterSize; y <= playerPosY + clusterSize; y++) {
            if (y < 0 || y >= Board.MAX_Y) {
                continue;
            }

            for (int x = playerPosX - clusterSize; x <= playerPosX + clusterSize; x++) {
                if (x < 0 || x >= Board.MAX_X) {
                    continue;
                }

                final int[] fieldCoordinates = new int[]{x, y};
                nodeList[currentNodeIndex] = fieldCoordinates;
                currentNodeIndex++;
            }
        }

        if (currentNodeIndex < maxPathLength) {
            final int[][] nodeListResized = new int[currentNodeIndex][];
            System.arraycopy(nodeList, 0, nodeListResized, 0, currentNodeIndex);
            return nodeListResized;
        }

        return nodeList;
    }

    static int[][] createWeightMatrix(final int[][] nodeList, final int[][] fields, int myPlayerNumber) {
        final int distanceMatrix[][] = new int[Board.MAX_Y][Board.MAX_X];

        for (final int[] fieldCoordinates : nodeList) {
            final int x = fieldCoordinates[0];
            final int y = fieldCoordinates[1];

            final int field = fields[y][x];
            int weight;
            if (field == Board.WALL) {
                weight = NO_WAY;
            } else if (field == Board.EMPTY) {
                weight = 3;
            } else if (field == myPlayerNumber) {
                weight = 5; // our color
            } else {
                weight = 1; // opponent color field
            }
            //TODO: optimisation, avoid fields with ourself

            distanceMatrix[y][x] = weight;
        }

        return distanceMatrix;
    }

    static int[][] createAdjacencyMatrix(final int[][] nodeList, final int[][] weightMatrix) {
        final int adjacencyMatrix[][] = new int[nodeList.length][nodeList.length];

        for (int startNodeIndex = 0; startNodeIndex < nodeList.length - 1; startNodeIndex++) {
            final int[] startFieldCoordinates = nodeList[startNodeIndex];
            final int startX = startFieldCoordinates[0];
            final int startY = startFieldCoordinates[1];

            // TODO: maybe less iteration by better data structure
            for (int toNodeIndex = 1; toNodeIndex < nodeList.length; toNodeIndex++) {
                final int[] toFieldCoordinates = nodeList[toNodeIndex];
                final int toX = toFieldCoordinates[0];
                final int toY = toFieldCoordinates[1];

                final boolean isNotNeighbour = !withinLimit(toX, startX - 1, startX + 1) || !withinLimit(toY, startY - 1, startY + 1);
                if (isNotNeighbour) {
                    continue;
                }

                int weight = 0;
                final boolean isNotMyself = !(toX == startX && toY == startY);
                if (isNotMyself) {
                    weight = weightMatrix[toY][toX];
                }

                adjacencyMatrix[startNodeIndex][toNodeIndex] = weight;
            }

        }

        return adjacencyMatrix;
    }

    private static boolean withinLimit(final int toTest, final int start, final int stop) {
        return toTest >= start && toTest <= stop;
    }

    static int[][] dijkstra(final int[][] adjacencyMatrix, float[] currentPosition, int[][] nodeList) {
        final int listOfNodesWithPrevious[] = new int[adjacencyMatrix.length];
        final int listOfNodesWithTotalDistance[] = new int[adjacencyMatrix.length];
        final boolean isIncluded[] = new boolean[adjacencyMatrix.length];

        final int playerPosX = (int) currentPosition[0];
        final int playerPosY = (int) currentPosition[1];

        for (int i = 0; i < adjacencyMatrix.length; i++) {
            listOfNodesWithPrevious[i] = NO_PREVIOUS;
            final int[] currentNode = nodeList[i];
            if (currentNode[0] == playerPosX && currentNode[1] == playerPosY) {
                listOfNodesWithTotalDistance[i] = NO_WAY;
            } else {
                listOfNodesWithTotalDistance[i] = Integer.MAX_VALUE;

            }
        }

        for (int count = 0; count < adjacencyMatrix.length - 1; count++) {
            final int u = minDistance(listOfNodesWithTotalDistance, isIncluded);

            isIncluded[u] = true;

            for (int v = 0; v < adjacencyMatrix.length; v++) {
                final int distanceUV = adjacencyMatrix[u][v];
                final int currentDistance = listOfNodesWithTotalDistance[u];
                final int nextNodeDistance = listOfNodesWithTotalDistance[v];

                if (!isIncluded[v] && distanceUV != NO_WAY &&
                        currentDistance != Integer.MAX_VALUE &&
                        currentDistance + distanceUV < nextNodeDistance) {
                    listOfNodesWithTotalDistance[v] = currentDistance + distanceUV;
                    listOfNodesWithPrevious[v] = u;

                    //TODO: optimize and take first min value of 1 instead of keep iterating for no reason
//                    if(distanceUV == 1) {
//                        break;
//                    }
                }
            }
        }

        // unreachable previous ones have still -1, e.g. walls!
        return new int[][]{listOfNodesWithTotalDistance, listOfNodesWithPrevious};
    }

    private static int minDistance(final int[] distance, final boolean[] isIncluded) {
        int min = Integer.MAX_VALUE;
        int minIndex = -1;

        for (int i = 0; i < distance.length; i++) {
            if (distance[i] <= min && !isIncluded[i]) {
                min = distance[i];
                minIndex = i;
            }
        }

        return minIndex;
    }

    static int[][] getAllPaths(final int[] listOfNodesWithPrevious) {
        final int[][] paths = new int[listOfNodesWithPrevious.length][];
        for (int node = 0; node < listOfNodesWithPrevious.length; node++) {
            if (listOfNodesWithPrevious[node] == NO_PREVIOUS) {
                paths[node] = new int[0];
                continue;
            }

            paths[node] = concatPaths(listOfNodesWithPrevious, node);
        }

        return paths;
    }

    static int[] concatPaths(final int[] listOfNodesWithPrevious, final int currentNode) {
        final int previousNode = listOfNodesWithPrevious[currentNode];
        if (previousNode == NO_PREVIOUS) {
            return new int[]{currentNode};
        } else {
            final int[] previousNodes = concatPaths(listOfNodesWithPrevious, previousNode);
            final int[] concatArrays = concatArrays(previousNodes, new int[]{currentNode});
            return concatArrays;
        }
    }

    static int[] concatArrays(final int[] a, final int[] b) {
        final int aLen = a.length;
        final int bLen = b.length;
        final int[] c = new int[aLen + bLen];
        System.arraycopy(a, 0, c, 0, aLen);
        System.arraycopy(b, 0, c, aLen, bLen);
        return c;
    }

    static int[] calcBestPathFromPathsAndDistances(final int[][] paths, final int[] distances, int stone) {
        int lowestScore = Integer.MAX_VALUE;
        int longestPath = 0;

        int bestNode = -1;

        for (int node = 0; node < distances.length; node++) {
            final int pathLength = paths[node].length;
            if (pathLength == 0) {
                continue;
            }

            final int distance = distances[node];
            // NB: score  - the lower the better!
            final int score = (int) (distance * 100f / pathLength);

            if (lowestScore > score && longestPath < pathLength) {
                longestPath = pathLength;
                lowestScore = score;
                bestNode = node;
            }
        }

        return paths[bestNode];
    }

    static int[][] mapNodesToFields(int[][] nodeList, int[] nodes) {
        final int[][] fields = new int[nodes.length][];
        for (int i = 0; i < nodes.length; i++) {
            final int node = nodes[i];
            fields[i] = nodeList[node];
        }
        return fields;
    }

    static float[] getVectorFromPath(final int[] bestPath, final int[][] nodeList, final float[] currentPosition) {
        final float CENTER_FIX = rnd.nextFloat();

        final int nextNode = bestPath[1];
        final int[] destination = nodeList[nextNode];
        final float[] realDestination = new float[]{destination[0] + CENTER_FIX, destination[1] + CENTER_FIX};

        System.out.println("From " + Arrays.toString(currentPosition) + ", to: " + Arrays.toString(realDestination));

        final float[] vector = new float[]{realDestination[0] - currentPosition[0], realDestination[1] - currentPosition[1]};
        return vector;
    }
}
