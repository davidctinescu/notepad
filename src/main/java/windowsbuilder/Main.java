package windowsbuilder;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;

public class Main {
    private JFrame frame;
    private JTextArea textArea;
    private File currentFile;

    public Main() {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }

        frame = new JFrame("Notepad");
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        textArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(textArea);
        frame.add(scrollPane, BorderLayout.CENTER);

        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenu helpMenu = new JMenu("Help");

        JMenuItem newMenuItem = new JMenuItem("New");
        newMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                newFile();
            }
        });
        JMenuItem openMenuItem = new JMenuItem("Open");
        openMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openFile();
            }
        });
        JMenuItem saveMenuItem = new JMenuItem("Save As...");
        saveMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveFile();
            }
        });
        JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int option = promptSave();
                if (option == JOptionPane.YES_OPTION) {
                    saveFile();
                } else if (option == JOptionPane.NO_OPTION || option == JOptionPane.CLOSED_OPTION) {
                    System.exit(0);
                }
            }
        });
        fileMenu.add(newMenuItem);
        fileMenu.add(openMenuItem);
        fileMenu.add(saveMenuItem);
        fileMenu.addSeparator();
        fileMenu.add(exitMenuItem);

        JMenuItem aboutMenuItem = new JMenuItem("About");
        aboutMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAboutDialog();
            }
        });
        helpMenu.add(aboutMenuItem);

        menuBar.add(fileMenu);
        menuBar.add(helpMenu);

        frame.setJMenuBar(menuBar);

        frame.setVisible(true);
    }

    private int promptSave() {
        if (currentFile != null) {
            int option = JOptionPane.showConfirmDialog(frame, 
            "Do you want to save changes to " + currentFile.getName() + "?",
            "Save Changes",
            JOptionPane.YES_NO_CANCEL_OPTION);
            
            if (option == JOptionPane.YES_OPTION) {
                saveFile(currentFile);
            }
            
            return option;
        }
        return JOptionPane.NO_OPTION;
    }    

    private void newFile() {
        int option = promptSave();
        if (option != JOptionPane.CANCEL_OPTION) {
            textArea.setText("");
            frame.getContentPane().removeAll();
            frame.add(textArea, BorderLayout.CENTER);
            frame.revalidate();
            frame.repaint();
            currentFile = null;
        }
    }    

    private void openFile() {
        int option = promptSave();
        if (option != JOptionPane.CANCEL_OPTION) {
                JFileChooser fileChooser = new JFileChooser();
                int result = fileChooser.showOpenDialog(frame);
                if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                currentFile = selectedFile;
                try {
                    BufferedReader reader = new BufferedReader(new FileReader(selectedFile));
                    RSyntaxTextArea syntaxTextArea = new RSyntaxTextArea(20, 60);
                    syntaxTextArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
                    syntaxTextArea.setCodeFoldingEnabled(true);
                    syntaxTextArea.setText("");
                    String line;
                    while ((line = reader.readLine()) != null) {
                        syntaxTextArea.append(line + "\n");
                    }
                    reader.close();
                    frame.getContentPane().removeAll();
                    JScrollPane scrollPane = new JScrollPane(syntaxTextArea);
                    frame.add(scrollPane, BorderLayout.CENTER);
                    frame.revalidate();
                    frame.repaint();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }    

    private void saveFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save As");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Text Files", "txt"));
        int result = fileChooser.showSaveDialog(frame);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(selectedFile));
                writer.write(textArea.getText());
                writer.close();
                currentFile = selectedFile;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveFile(File file) {
        if (file!= null) {
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(file));
                writer.write(textArea.getText());
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }    

    private void showAboutDialog() {
        JOptionPane.showMessageDialog(frame,
                "Notepad\nVersion 1.0\nPackage: windowsbuilder",
                "About",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::new);
    }
}