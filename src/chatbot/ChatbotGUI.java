package chatbot;

import chatbot.engine.Chatbot;
import chatbot.utils.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class ChatbotGUI implements WindowListener {
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
        ChatbotGUI chatbotGUI = new ChatbotGUI();
        frame.setContentPane(chatbotGUI.mainPanel);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.addWindowListener(chatbotGUI);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    @Override
    public void windowOpened(WindowEvent e) {

    }

    @Override
    public void windowClosing(WindowEvent e) {
        System.out.println("saving...");
        chatbot.save();
        System.out.println("done!");
    }

    @Override
    public void windowClosed(WindowEvent e) {

    }

    @Override
    public void windowIconified(WindowEvent e) {

    }

    @Override
    public void windowDeiconified(WindowEvent e) {

    }

    @Override
    public void windowActivated(WindowEvent e) {

    }

    @Override
    public void windowDeactivated(WindowEvent e) {

    }
}
