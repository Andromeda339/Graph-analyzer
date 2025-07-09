import java.util.*;

public class GraphAnalyzer {
    private int[][] adjacencyMatrix;
    private int vertices;
    private int expectedEdgeCount;
    private boolean isDirected;
    private List<String> edgeList;
    private int[][] incidenceMatrix;
    private int actualEdgeCount;
    private List<Integer> loops; // Список вершин із петлями

    public GraphAnalyzer(int[][] adjacencyMatrix, int expectedEdgeCount, boolean isDirected) throws IllegalArgumentException {
        this.isDirected = isDirected;
        this.adjacencyMatrix = adjacencyMatrix;
        this.expectedEdgeCount = expectedEdgeCount;
        this.vertices = adjacencyMatrix.length;
        if (vertices == 0) {
            throw new IllegalArgumentException("Матриця суміжності не може бути порожньою!");
        }
        loops = new ArrayList<>(); // Ініціалізуємо список для петель
        validateMatrix();
        edgeList = new ArrayList<>();
        buildEdgesAndIncidenceMatrix();
    }

    private void validateMatrix() {
        for (int i = 0; i < vertices; i++) {
            if (adjacencyMatrix[i].length != vertices) {
                throw new IllegalArgumentException("Матриця суміжності має бути квадратною!");
            }
            for (int j = 0; j < vertices; j++) {
                if (adjacencyMatrix[i][j] != 0 && adjacencyMatrix[i][j] != 1) {
                    throw new IllegalArgumentException("Матриця суміжності має містити лише 0 або 1!");
                }
                if (!isDirected && adjacencyMatrix[i][j] != adjacencyMatrix[j][i]) {
                    throw new IllegalArgumentException("Для неорієнтованого графа матриця суміжності має бути симетричною!");
                }
            }
        }
    }

    private void buildEdgesAndIncidenceMatrix() {
        // Спочатку шукаємо петлі (на головній діагоналі)
        for (int i = 0; i < vertices; i++) {
            if (adjacencyMatrix[i][i] == 1) {
                loops.add(i + 1); // Додаємо вершину з петлею (індекс + 1)
                edgeList.add("(" + (i + 1) + (isDirected ? "→" : "-") + (i + 1) + ")"); // Додаємо петлю до списку ребер
            }
        }

        // Формуємо список ребер (без урахування петель, які вже оброблені)
        for (int i = 0; i < vertices; i++) {
            for (int j = isDirected ? 0 : i + 1; j < vertices; j++) {
                if (i != j && adjacencyMatrix[i][j] == 1) {
                    if (isDirected) {
                        edgeList.add("(" + (i + 1) + "→" + (j + 1) + ")");
                    } else {
                        edgeList.add("(" + (i + 1) + "-" + (j + 1) + ")");
                    }
                }
            }
        }

        // Визначаємо фактичну кількість ребер
        actualEdgeCount = edgeList.size();
        incidenceMatrix = new int[vertices][actualEdgeCount];

        // Будуємо матрицю інцидентності
        for (int e = 0; e < actualEdgeCount; e++) {
            String edge = edgeList.get(e);
            int u, v;
            if (isDirected) {
                u = Integer.parseInt(edge.substring(1, edge.indexOf("→"))) - 1;
                v = Integer.parseInt(edge.substring(edge.indexOf("→") + 1, edge.length() - 1)) - 1;
                if (u == v) { // Якщо це петля
                    incidenceMatrix[u][e] = 2; // Для петлі в орієнтованому графі ставимо 2
                } else {
                    incidenceMatrix[u][e] = 1;
                    incidenceMatrix[v][e] = -1;
                }
            } else {
                u = Integer.parseInt(edge.substring(1, edge.indexOf("-"))) - 1;
                v = Integer.parseInt(edge.substring(edge.indexOf("-") + 1, edge.length() - 1)) - 1;
                if (u == v) { // Якщо це петля
                    incidenceMatrix[u][e] = 2; // Для петлі в неорієнтованому графі також ставимо 2
                } else {
                    incidenceMatrix[u][e] = 1;
                    incidenceMatrix[v][e] = 1;
                }
            }
        }
    }

    public String getEdgesList() {
        return String.join(", ", edgeList);
    }

    public String getAdjacencyMatrix() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < vertices; i++) {
            for (int j = 0; j < vertices; j++) {
                sb.append(String.format("%3d", adjacencyMatrix[i][j]));
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public String getIncidenceMatrix() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < vertices; i++) {
            for (int j = 0; j < actualEdgeCount; j++) {
                sb.append(String.format("%3d", incidenceMatrix[i][j]));
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public int getActualEdgeCount() {
        return actualEdgeCount;
    }

    public String getEdgeCountComparison() {
        if (expectedEdgeCount == -1) {
            return "Фактична кількість ребер: " + actualEdgeCount + "\n";
        }
        if (expectedEdgeCount == actualEdgeCount) {
            return "Введена кількість ребер збігається з фактичною: " + actualEdgeCount + "\n";
        } else {
            return "Попередження: Введена кількість ребер (" + expectedEdgeCount + ") не збігається з фактичною (" + actualEdgeCount + ")!\n";
        }
    }

    public String getLoopsInfo() {
        if (loops.isEmpty()) {
            return "Петель у графі немає.\n";
        } else {
            return "Петлі виявлені на вершинах: " + loops.toString().replaceAll("[\\[\\]]", "") + "\n";
        }
    }
}