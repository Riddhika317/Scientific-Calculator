package calc;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class ScientificCalculator extends JFrame implements ActionListener {
    private JTextField display;
    private String currentInput = "";
    private double num1 = 0, num2 = 0;
    private String operator = "";
    private List<String> history = new ArrayList<>();

    public ScientificCalculator() {
        setTitle("Scientific Calculator");
        setSize(450, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel topPanel = new JPanel(new BorderLayout());
        display = new JTextField();
        display.setEditable(false);
        display.setFont(new Font("Arial", Font.BOLD, 28));
        display.setHorizontalAlignment(JTextField.RIGHT);

        JButton historyButton = new JButton("\u23F0");
        historyButton.setFont(new Font("Arial", Font.PLAIN, 22));
        historyButton.addActionListener(e -> showHistory());

        topPanel.add(display, BorderLayout.CENTER);
        topPanel.add(historyButton, BorderLayout.EAST);
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(topPanel, BorderLayout.NORTH);

        String[] buttons = {
            "7", "8", "9", "/", "sqrt",
            "4", "5", "6", "*", "log",
            "1", "2", "3", "-", "sin",
            "0", ".", "=", "+", "cos",
            "C", "tan", "^", "(", ")",
            "cbrt", "exp", "ln", "abs", "fact",
            "i", "asin", "acos", "atan", "mod"
        };

        JPanel panel = new JPanel(new GridLayout(8, 5, 6, 6));
        for (String text : buttons) {
            JButton btn = new JButton(text);
            btn.setFont(new Font("Arial", Font.BOLD, 20));
            btn.addActionListener(this);
            panel.add(btn);
        }

        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(panel, BorderLayout.CENTER);

        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        String input = e.getActionCommand();

        try {
            switch (input) {
                case "C" -> {
                    currentInput = "";
                    num1 = 0;
                    operator = "";
                    display.setText("");
                }
                case "=" -> {
                    if (operator.isEmpty()) return;
                    if (currentInput.isEmpty()) return;

                    String expression = display.getText();
                    if (expression.contains("i")) {
                        String left = expression.substring(0, expression.indexOf(operator));
                        String right = expression.substring(expression.indexOf(operator) + 1);

                        Complex c1 = Complex.parse(left);
                        Complex c2 = Complex.parse(right);
                        Complex result = switch (operator) {
                            case "+" -> c1.add(c2);
                            case "-" -> c1.subtract(c2);
                            case "*" -> c1.multiply(c2);
                            case "/" -> c1.divide(c2);
                            default -> throw new IllegalArgumentException("Unsupported operation");
                        };
                        history.add(c1 + " " + operator + " " + c2 + " = " + result);
                        display.setText(result.toString());
                        currentInput = result.toString();
                    } else {
                        num2 = Double.parseDouble(currentInput);
                        double result = calculate(num1, num2, operator);
                        String record = num1 + " " + operator + " " + num2 + " = " + result;
                        history.add(record);
                        display.setText(String.valueOf(result));
                        currentInput = String.valueOf(result);
                    }
                    operator = "";
                }
                case "sqrt" -> applyUnary(Math::sqrt);
                case "log" -> applyUnary(Math::log10);
                case "ln" -> applyUnary(Math::log);
                case "sin" -> applyUnary(val -> Math.sin(Math.toRadians(val)));
                case "cos" -> applyUnary(val -> Math.cos(Math.toRadians(val)));
                case "tan" -> applyUnary(val -> Math.tan(Math.toRadians(val)));
                case "cbrt" -> applyUnary(Math::cbrt);
                case "exp" -> applyUnary(Math::exp);
                case "abs" -> applyUnary(Math::abs);
                case "fact" -> {
                    int val = Integer.parseInt(currentInput);
                    if (val < 0) throw new IllegalArgumentException("Factorial of negative number");
                    long result = 1;
                    for (int i = 2; i <= val; i++) result *= i;
                    display.setText(String.valueOf(result));
                    currentInput = String.valueOf(result);
                }
                case "asin" -> applyUnary(val -> Math.toDegrees(Math.asin(val)));
                case "acos" -> applyUnary(val -> Math.toDegrees(Math.acos(val)));
                case "atan" -> applyUnary(val -> Math.toDegrees(Math.atan(val)));

                case "+", "-", "*", "/", "^", "mod" -> {
                    if (!currentInput.isEmpty()) {
                        if (currentInput.contains("i")) {
                            // defer evaluation until "="
                            operator = input;
                            display.setText(currentInput + operator);
                            currentInput = "";
                        } else {
                            num1 = Double.parseDouble(currentInput);
                            operator = input;
                            currentInput = "";
                        }
                    }
                }
                case "i" -> {
                    if (!currentInput.contains("i")) {
                        currentInput += "i";
                        display.setText(currentInput);
                    }
                }
                default -> {
                    currentInput += input;
                    display.setText(currentInput);
                }
            }
        } catch (Exception ex) {
            display.setText("Error");
            currentInput = "";
            operator = "";
        }
    }

    private void applyUnary(java.util.function.DoubleUnaryOperator op) {
        if (currentInput.isEmpty()) return;
        double val = Double.parseDouble(currentInput);
        double result = op.applyAsDouble(val);
        display.setText(String.valueOf(result));
        currentInput = String.valueOf(result);
    }

    private double calculate(double a, double b, String op) {
        return switch (op) {
            case "+" -> a + b;
            case "-" -> a - b;
            case "*" -> a * b;
            case "/" -> a / b;
            case "^" -> Math.pow(a, b);
            case "mod" -> a % b;
            default -> b;
        };
    }

    private void showHistory() {
        if (history.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No history available.", "History", JOptionPane.INFORMATION_MESSAGE);
        } else {
            StringBuilder sb = new StringBuilder();
            for (String entry : history) {
                sb.append(entry).append("\n");
            }
            JTextArea textArea = new JTextArea(sb.toString());
            textArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(350, 250));
            JOptionPane.showMessageDialog(this, scrollPane, "Calculation History", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ScientificCalculator::new);
    }
}
