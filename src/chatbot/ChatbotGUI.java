package chatbot;

import chatbot.engine.Chatbot;
import chatbot.utils.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class ChatbotGUI {
    private int width = 700;
    private int height = 700;

    private Chatbot chatbot;
    private JButton sendButton;
    private JTextField messageField;
    private JPanel mainPanel;
    private JScrollPane messageAreaScrollPane;
    private JTextArea messageArea;

    public ChatbotGUI() {
        chatbot = new Chatbot();

        mainPanel.setPreferredSize(new Dimension(width, height));
        mainPanel.setMinimumSize(new Dimension(width, height));

        ActionListener replyActionListener = e -> {
            String message = messageField.getText().trim();
            messageField.setText("");
            if (message.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please type something!");
            } else {
                messageArea.append(String.format("[%s] You: ", Utils.getCurrentTimeInString()) + message + "\n");
                System.out.println("---------------------------------------------------------------------------");
                message = chatbot.getReply(message);
                System.out.println("---------------------------------------------------------------------------");
                messageArea.append(String.format("[%s] Bot: ", Utils.getCurrentTimeInString()) + message + "\n");
            }
        };

        sendButton.addActionListener(replyActionListener);
        messageField.addActionListener(replyActionListener);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Chatbot");
        frame.setContentPane(new ChatbotGUI().mainPanel);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
