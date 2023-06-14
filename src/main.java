import javax.swing.*;
import java.awt.FlowLayout;

public class main extends JFrame {
    public main() {
        // Set up the frame
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Project 2");
        setSize(800, 500);

        // Create components
        JLabel label = new JLabel("Hello, GUI!");
        JButton button = new JButton("Click Me!");

        // Add components to the frame
        add(label);
        add(button);

        // Set layout manager (optional)
        setLayout(new FlowLayout());

        // Make the frame visible
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new main());
    }
}
