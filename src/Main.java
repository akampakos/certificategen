import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;

public class Main {

    public static void main(String[] args) {
        // Creating instance of JFrame
        JFrame frame = new JFrame("Certificate Generator");
        // Setting the width and height of frame
        frame.setSize(1000, 625);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        // adding panel to frame
        frame.add(panel);
        placeComponents(panel);

        frame.setVisible(true);
    }

    private static void placeComponents(JPanel panel) {

        panel.setLayout(null);

        // Creating JLabel
        final JLabel locationLabel = new JLabel("Location :");
        locationLabel.setBounds(10, 20, 80, 25);
        panel.add(locationLabel);

        /* Creating text field where user is supposed to
         * enter location.
         */
        final JTextField locationText = new JTextField(20);
        locationText.setBounds(110, 20, 165, 25);
        panel.add(locationText);

        JLabel providerLabel = new JLabel("Provider name:");
        providerLabel.setBounds(10, 50, 100, 25);
        panel.add(providerLabel);

        final JTextField providertext = new JTextField(20);
        providertext.setBounds(110, 50, 165, 25);
        panel.add(providertext);

        // Creating Genarate button
        JButton genarateButton = new JButton("Generate");
        genarateButton.setBounds(30, 330, 200, 30);
        panel.add(genarateButton);

        // Creating Clear button
        final JButton clearButton = new JButton("Clear Text");
        clearButton.setBounds(30, 390, 200, 30);
        clearButton.setEnabled(false);
        panel.add(clearButton);

        // Creating Copy button
        final JButton copyButton = new JButton("Copy Keys");
        copyButton.setBounds(30, 450, 200, 30);
        copyButton.setEnabled(false);
        panel.add(copyButton);

        // Creating Create button
        final JButton createButton = new JButton("Save Certificate");
        createButton.setBounds(30, 510, 200, 30);
        createButton.setEnabled(false);
        panel.add(createButton);

        // Creating Keys area
        final JTextArea keys = new JTextArea();
        keys.setBounds(280, 4, 700, 550);
        panel.add(keys);

        JScrollPane scrollPane = new JScrollPane(keys);
        scrollPane.setBounds(280, 4, 700, 550);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        panel.add(scrollPane);


        genarateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (locationText.getText().isEmpty() || providertext.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "You must complete all fields !!!");
                } else {
                    createButton.setEnabled(true);
                    copyButton.setEnabled(true);
                    clearButton.setEnabled(true);
                    Certificate cert = new Certificate();
                    String arguments = "C=GR, ST=AT, O=Your_Company"; //"C=GR, ST=AT, O=Certs_R_Us, CN=info@example.com";
                    arguments = arguments + ", " + "L=" + locationText.getText().toString() + ", " + "CN=" + providertext.getText().toString();
                    try {
                        keys.setText(cert.generateCertificate(arguments, 10950, "SHA1withRSA", 1024));
                    } catch (GeneralSecurityException e1) {
                        e1.printStackTrace();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }

            }

        });

        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                JFileChooser chooser = new JFileChooser();
                JFrame frame = new JFrame("Certificate Generator");
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int option = chooser.showSaveDialog(null);

                String[] lines = keys.getText().split("\\n");

                if (option == JFileChooser.APPROVE_OPTION) {
                    String path = chooser.getSelectedFile().getAbsolutePath() + "\\certificate.crt";
                    System.out.println(path);
                    PrintWriter writer = null;
                    try {
                        writer = new PrintWriter(path, "UTF-8");
                    } catch (FileNotFoundException e1) {
                        e1.printStackTrace();
                    } catch (UnsupportedEncodingException e1) {
                        e1.printStackTrace();
                    }
                    for (int i = 0; i <= 16; i++)
                        writer.println(lines[i]);
                    writer.close();
                }
            }

        });

        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearButton.setEnabled(false);
                keys.setText("");
                createButton.setEnabled(false);
                copyButton.setEnabled(false);
            }
        });

        copyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String myString = keys.getText().toString();
                StringSelection stringSelection = new StringSelection(myString);
                Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
                clpbrd.setContents(stringSelection, null);
                JOptionPane.showMessageDialog(null, "Successfully copied Private and Public key");
            }
        });

    }


}


