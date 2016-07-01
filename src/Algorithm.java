import lenz.htw.yakip.net.NetworkClient;

/**
 * Created by m on 6/26/16.
 */
public class Algorithm {
    public static final int NO_PREVIOUS = -1;
    public static final int NO_WAY = 0;
    public static final int CLUSTER_SIZE = 4;
    public static final int MAX_PATH_LENGTH = (CLUSTER_SIZE + CLUSTER_SIZE + 1) * (CLUSTER_SIZE + CLUSTER_SIZE + 1);
    public static final int STONE_TOTAL_POSITION_IN_CLUSTER = MAX_PATH_LENGTH / 2;

    private final Board board;
    private final int myPlayerNumber;

    public Algorithm(Board board, NetworkClient network) {
        this.board = board;
//        this.network = network;

        this.myPlayerNumber = network.getMyPlayerNumber();
    }

    public int[] getNextPath(int stone) {
        final int[][] fields = board.getFields();

        final float[][] stonePosition = board.getStonePosition();

        float[] currentPosition = stonePosition[stone];

        // NB: nodeList[0] = new int[] {x,y} field coordinates
        final int[][] nodeList = createNodeList(currentPosition);
        final int[][] weightMatrix = createWeightMatrix(nodeList, fields);
        final int[][] adjacencyMatrix = createAdjacencyMatrix(nodeList, weightMatrix);
        final int[][] distancesAndPrevious = dijkstra(adjacencyMatrix);

        System.out.println(board.toString(distancesAndPrevious[0], nodeList));

//        final int[] coordinates = getCoordinatesFromPath(distances, currentPosition, nodeList);
        return null;
    }

    static int[][] createNodeList(float[] currentPosition) {
        final int[][] nodeList = new int[MAX_PATH_LENGTH][];
        int currentNodeIndex = 0;

        final int playerPosX = (int) currentPosition[0];
        final int playerPosY = (int) currentPosition[1];

        for (int y = playerPosY - CLUSTER_SIZE; y <= playerPosY + CLUSTER_SIZE; y++) {
            if (y < 0 || y > Board.MAX_Y) {
                continue;
            }

            for (int x = playerPosX - CLUSTER_SIZE; x <= playerPosX + CLUSTER_SIZE; x++) {
                if (x < 0 || x > Board.MAX_X) {
                    continue;
                }

                final int[] fieldCoordinates = new int[]{x, y};
                nodeList[currentNodeIndex] = fieldCoordinates;
                currentNodeIndex++;
            }
        }
        return nodeList;
    }

    int[][] createWeightMatrix(int[][] nodeList, int[][] fields) {
        final int distanceMatrix[][] = new int[Board.MAX_Y][Board.MAX_X];

        for (final int[] fieldCoordinates : nodeList) {
            final int x = fieldCoordinates[0];
            final int y = fieldCoordinates[1];

            final int field = fields[y][x];
            int weight;
            if (field == Board.WALL) {
                weight = NO_WAY;
            } else if (field == Board.EMPTY) {
                weight = 2;
            } else if (field == myPlayerNumber) {
                weight = 3; // our color
            } else {
                weight = 1; // opponent color field
            }
            //TODO: optimisation, avoid fields with ourself

            distanceMatrix[y][x] = weight;
        }

        return distanceMatrix;
    }

    static int[][] createAdjacencyMatrix(int[][] nodeList, int[][] weightMatrix) {
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

                boolean isNotNeighbour = !withinLimit(toX, startX - 1, startX + 1) || !withinLimit(toY, startY - 1, startY + 1);
                if (isNotNeighbour) {
                    continue;
                }

                int weight = 0;
                boolean isNotMyself = !(toX == startX && toY == startY);
                if (isNotMyself) {
                    weight = weightMatrix[toY][toX];
                }

                adjacencyMatrix[startNodeIndex][toNodeIndex] = weight;
            }

        }

        return adjacencyMatrix;
    }

    private static boolean withinLimit(int toTest, int start, int stop) {
        return toTest >= start && toTest <= stop;
    }

    static int[][] dijkstra(int[][] adjacencyMatrix) {
        final int listOfNodesWithPrevious[] = new int[MAX_PATH_LENGTH];
        final int listOfNodesWithTotalDistance[] = new int[MAX_PATH_LENGTH];
        final boolean isIncluded[] = new boolean[MAX_PATH_LENGTH];

        for (int i = 0; i < MAX_PATH_LENGTH; i++) {
            listOfNodesWithTotalDistance[i] = Integer.MAX_VALUE;
            listOfNodesWithPrevious[i] = NO_PREVIOUS;
        }

        listOfNodesWithTotalDistance[STONE_TOTAL_POSITION_IN_CLUSTER] = NO_WAY;

        for (int count = 0; count < MAX_PATH_LENGTH - 1; count++) {
            final int u = minDistance(listOfNodesWithTotalDistance, isIncluded);

            isIncluded[u] = true;

            for (int v = 0; v < MAX_PATH_LENGTH; v++) {
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

    private static int minDistance(int[] distance, boolean[] isIncluded) {
        int min = Integer.MAX_VALUE;
        int minIndex = -1;

        for (int i = 0; i < MAX_PATH_LENGTH; i++) {
            if (distance[i] <= min && !isIncluded[i]) {
                min = distance[i];
                minIndex = i;
            }
        }

        return minIndex;
    }

    static int[][] getAllPaths(final int[] listOfNodesWithPrevious) {
        int[][] paths = new int[listOfNodesWithPrevious.length][];
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

    static int[] concatArrays(int[] a, int[] b) {
        int aLen = a.length;
        int bLen = b.length;
        int[] c = new int[aLen + bLen];
        System.arraycopy(a, 0, c, 0, aLen);
        System.arraycopy(b, 0, c, aLen, bLen);
        return c;
    }

    static int[] getScoresFromPathsAndDistances(final int[][] paths, final int[] listOfNodesWithPrevious, int[] distances) {
        final int[] liftOfNodesWithScore = new int[listOfNodesWithPrevious.length];
        for (int node = 0; node < listOfNodesWithPrevious.length; node++) {

            final int pathLength = paths[node].length;
            if (pathLength != 0) {
                final int distance = distances[node];
                final int score = (int) (distance * 100f / pathLength);
                liftOfNodesWithScore[node] = score;
            } else {
                liftOfNodesWithScore[node] = Integer.MAX_VALUE;

            }
        }

        // NB: score  - the lower the better!
        return liftOfNodesWithScore;
    }

    private int[] getCoordinatesFromPath(int[] shortestPath, float[] startFieldCoordinates, int[][] nodeList) {
        //TODO: translate to vector
        return null;
    }
}
