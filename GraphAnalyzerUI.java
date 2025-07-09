import javax.swing.*;
import java.awt.*;

public class GraphAnalyzerUI extends JFrame {
    private JTextField verticesInput;
    private JTextField edgesInput;
    private JTextArea matrixInput;
    private JCheckBox directedCheckBox;
    private JTextArea resultArea;
    private JButton analyzeButton;
    private JPanel matrixPanel;

    public GraphAnalyzerUI() {
        initUI();
    }

    private void initUI() {
        setTitle("Аналіз графів");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Кількість вершин:"), gbc);

        verticesInput = new JTextField(5);
        gbc.gridx = 1;
        add(verticesInput, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Кількість ребер (опціонально):"), gbc);

        edgesInput = new JTextField(5);
        gbc.gridx = 1;
        add(edgesInput, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        add(new JLabel("Введіть матрицю суміжності (рядки через ; або \\n, елементи через пробіл):"), gbc);

        matrixInput = new JTextArea(5, 30);
        matrixInput.setLineWrap(true);
        matrixInput.setWrapStyleWord(true);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        add(new JScrollPane(matrixInput), gbc);

        directedCheckBox = new JCheckBox("Орієнтований граф");
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weighty = 0.0;
        add(directedCheckBox, gbc);

        analyzeButton = new JButton("Аналізувати");
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        add(analyzeButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        add(new JLabel("Результати:"), gbc);

        resultArea = new JTextArea(15, 40);
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.fill = GridBagConstraints.BOTH;
        add(new JScrollPane(resultArea), gbc);

        analyzeButton.addActionListener(e -> analyzeGraph());

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void analyzeGraph() {
        try {
            if (matrixInput.getText().trim().isEmpty()) {
                resultArea.setText("Спочатку введіть матрицю суміжності!");
                return;
            }

            int vertices = Integer.parseInt(verticesInput.getText().trim());
            if (vertices <= 0) {
                throw new IllegalArgumentException("Кількість вершин має бути позитивною!");
            }

            int expectedEdges;
            String edgesText = edgesInput.getText().trim();
            if (edgesText.isEmpty()) {
                expectedEdges = -1;
            } else {
                expectedEdges = Integer.parseInt(edgesText);
                if (expectedEdges < 0) {
                    throw new IllegalArgumentException("Кількість ребер не може бути від'ємною!");
                }
            }

            String[] rows = matrixInput.getText().trim().replaceAll("\n", ";").split(";");
            if (rows.length != vertices) {
                throw new IllegalArgumentException("Кількість рядків у матриці має відповідати кількості вершин!");
            }

            int[][] adjacencyMatrix = new int[vertices][vertices];
            for (int i = 0; i < vertices; i++) {
                String[] values = rows[i].trim().split("\\s+");
                if (values.length != vertices) {
                    throw new IllegalArgumentException("Кожен рядок матриці має містити " + vertices + " елементів!");
                }
                for (int j = 0; j < vertices; j++) {
                    String value = values[j].trim();
                    if (value.isEmpty()) {
                        adjacencyMatrix[i][j] = 0;
                    } else {
                        adjacencyMatrix[i][j] = Integer.parseInt(value);
                    }
                }
            }

            boolean isDirected = directedCheckBox.isSelected();
            GraphAnalyzer analyzer = new GraphAnalyzer(adjacencyMatrix, expectedEdges, isDirected);

            StringBuilder result = new StringBuilder();
            result.append("Введена матриця суміжності:\n");
            result.append(analyzer.getAdjacencyMatrix()).append("\n");
            result.append(analyzer.getEdgeCountComparison());
            result.append(analyzer.getLoopsInfo()); // Додаємо інформацію про петлі
            result.append("Список ребер:\n");
            result.append(analyzer.getEdgesList()).append("\n\n");
            result.append("Матриця інцидентності:\n");
            result.append(analyzer.getIncidenceMatrix());

            resultArea.setText(result.toString());
        } catch (Exception ex) {
            resultArea.setText("Помилка: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(GraphAnalyzerUI::new);
    }
}