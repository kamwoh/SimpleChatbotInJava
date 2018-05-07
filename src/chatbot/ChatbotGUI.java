package chatbot;

import chatbot.engine.Chatbot;
import chatbot.utils.Logger;
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
    private JButton forgetButton;
    private JButton clearButton;
    private JLabel loadingLabel;
    private boolean loading;

    public ChatbotGUI() {
        chatbot = new Chatbot();
        loading = false;
        loadingLabel.setText("");

        mainPanel.setPreferredSize(new Dimension(width, height));
        mainPanel.setMinimumSize(new Dimension(width, height));

        ActionListener replyActionListener = e -> {
            final String message = messageField.getText().trim();
            messageField.setText("");
            if (message.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please type something!");
            } else {
                sendButton.setEnabled(false);
                messageField.setEnabled(false);
                loadingLabel.setText("Loading...");
                messageArea.append(String.format("[%s] You: ", Utils.getCurrentTimeInString()) + message + "\n\n");

                Runnable run = () -> {
                    Logger.println("---------------------------------------------------------------------------");
                    String replyMessage = chatbot.getReplyV2(message);
                    Logger.println("---------------------------------------------------------------------------");
                    String[] splitByNL = replyMessage.split("\n");

                    for (String m : splitByNL)
                        messageArea.append(String.format("[%s] Bot: ", Utils.getCurrentTimeInString()) + m + "\n\n");

                    sendButton.setEnabled(true);
                    messageField.setEnabled(true);
                    loadingLabel.setText("");

                    JScrollBar vertical = messageAreaScrollPane.getVerticalScrollBar();
                    vertical.setValue(vertical.getMaximum());
                };

                Thread thread = new Thread(run);
                thread.start();
            }
        };

        ActionListener forgetActionListener = e -> {
            chatbot.resetMemory();
            JOptionPane.showMessageDialog(null, "Memory has been reset!");
        };

        ActionListener clearActionListener = e -> {
            messageArea.setText("");
            JOptionPane.showMessageDialog(null, "The text area has been cleared");
        };


        sendButton.addActionListener(replyActionListener);
        forgetButton.addActionListener(forgetActionListener);
        clearButton.addActionListener(clearActionListener);
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
