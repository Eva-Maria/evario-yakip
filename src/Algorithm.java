import lenz.htw.yakip.net.NetworkClient;

/**
 * Created by m on 6/26/16.
 */
public class Algorithm {
    public static final int NO_WAY = 0;
    public static final int CLUSTER_SIZE = 4;
    public static final int MAX_PATH_LENGTH = (CLUSTER_SIZE + CLUSTER_SIZE + 1) * (CLUSTER_SIZE + CLUSTER_SIZE + 1);

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
        final int[][] nodeList = createNodeList(currentPosition);

//        System.out.println("Start: " + Arrays.toString(stonePosition[0]));
//        System.out.println("NodeList: ");
//        Arrays.stream(nodeList).forEach(ints -> System.out.print(Arrays.toString(ints) + "|"));
//        System.out.println();

        final int[][] weightMatrix = createWeightMatrix(nodeList, fields);

      /*  for (int[] m : weightMatrix) {
            for (int n : m) {
                System.out.print(n + " ");
            }
            System.out.println();
        }*/

        final int[][] adjacencyMatrix = createAdjacencyMatrix(nodeList, weightMatrix);

        int i = 0;
//        for (int[] row : adjacencyMatrix) {
//            System.out.println(i + " > " + Arrays.toString(row));
//            i++;
//        }

        final int[] shortestPath = dijkstra(adjacencyMatrix);
        final int[] coordinates = getCoordinatesFromPath(shortestPath, currentPosition, nodeList);

        return coordinates;
    }

    private static int[][] createNodeList(float[] currentPosition) {
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

    private int[][] createWeightMatrix(int[][] nodeList, int[][] fields) {
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

    private static int[][] createAdjacencyMatrix(int[][] nodeList, int[][] weightMatrix) {
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

    private int[] dijkstra(int[][] distanceMatrix) {
        final int distance[] = new int[MAX_PATH_LENGTH];
        final boolean isIncluded[] = new boolean[MAX_PATH_LENGTH];

        for (int i = 0; i < MAX_PATH_LENGTH; i++) {
            distance[i] = Integer.MAX_VALUE;
        }

        distance[0] = 0;

        for (int count = 0; count < MAX_PATH_LENGTH - 1; count++) {
            final int u = minDistance(distance, isIncluded);

            isIncluded[u] = true;

            for (int v = 0; v < MAX_PATH_LENGTH; v++) {
                final int distanceUV = distanceMatrix[u][v];
                final int currentDistance = distance[u];
                final int nextNodeDistance = distance[v];

                if (!isIncluded[v] && distanceUV != NO_WAY &&
                        currentDistance != Integer.MAX_VALUE &&
                        currentDistance + distanceUV < nextNodeDistance) {
                    distance[v] = currentDistance + distanceUV;
                }
            }
        }
        return distance;
    }

    private int minDistance(int[] distance, boolean[] isIncluded) {
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

    private int[] getCoordinatesFromPath(int[] shortestPath, float[] startFieldCoordinates, int[][] nodeList) {
        final int firstNode = shortestPath[0];
        final int[] toFieldCoordinates = nodeList[firstNode];




        //TODO: translate to vector
        return null;
    }
}
