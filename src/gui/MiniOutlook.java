package gui;

import controller.Controler;
import model.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;


public class MiniOutlook extends JFrame {
    private Controler controler;
    private JTabbedPane tabbedPane1;
    private JPanel panel1;
    private JFormattedTextField formattedTextField1;
    private JFormattedTextField formattedTextField2;
    private JPanel envoi;
    private JTree treeModel;
    private JTextField textFieldDestinataire;
    private JTextField JTextFieldExpediteur;
    private JTextArea textAreaContenu;
    private JTextField textFieldObjet;
    private JTable table1;
    private JButton button1;
    private JButton actualiser;
    private JLabel JLabelPieceJointe;
    private JButton buttonPieceJointe;
    private JTextField textFieldPieceJointe;
    private JComboBox choixMail;
    private JLabel JLabelName;
    private JButton buttonJTree;

    private String password;
    private ArrayList<File> attachmentsList = new ArrayList<>();

    private void setPassword(String s) {
        this.password = s;
    }

    public String getPassword() {
        return password;
    }

    public JButton getEnvoyer() {
        return button1;
    }

    public JButton getActualiser() {
        return actualiser;
    }


    public JButton getButtonPieceJointe() {
        return buttonPieceJointe;
    }

    public JTextField getTextFieldObjet() {
        return textFieldObjet;
    }

    public JTextField getJTextFieldExpediteur() {
        return JTextFieldExpediteur;
    }

    public JTextField getJTextFieldDestinataire() {
        return textFieldDestinataire;
    }

    public JLabel getJLabelPieceJointe() {
        return JLabelPieceJointe;
    }

    public JTextArea getTextAreaContenu() {
        return textAreaContenu;
    }

    public JTextField getTextFieldPieceJointe() {
        return textFieldPieceJointe;
    }

    public JComboBox getChoixMail() {
        return choixMail;
    }

    public JButton getButtonJTree() {
        return buttonJTree;
    }

    public List<JMailRecv> getReceivedEmails() {
        return receivedEmails;
    }

    private List<JMailRecv> receivedEmails;


    public MiniOutlook() {
        setContentPane(panel1);
        buttonPieceJointe.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleAttachButtonClick();
            }
        });
    }

    private void handleAttachButtonClick() {
        JFileChooser fileChooser = new JFileChooser();
        int returnValue = fileChooser.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            attachmentsList.add(selectedFile);
        }

        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < attachmentsList.size(); i++) {
            File file = attachmentsList.get(i);
            String fileName = file.getName();
            stringBuilder.append(fileName);

            if (i < attachmentsList.size() - 1) {
                stringBuilder.append(", ");
            }
        }
        String result = stringBuilder.toString();
        getTextFieldPieceJointe().setText(result);
    }




    public void setControler(Controler c, String loginTextFieldText, String pwd) {
        JTextFieldExpediteur.setText(loginTextFieldText);
        setPassword(pwd);
        getEnvoyer().addActionListener(c);
        getActualiser().addActionListener(c);
        getButtonPieceJointe().addActionListener(c);
        getButtonJTree().addActionListener(c);
        c.setAttachmentsList(attachmentsList);
        this.controler = c;
        addWindowListener(c);

    }
    public List<JMailRecv> fetchEmails() {
        MailSessionManager msm = MailSessionManager.getInstance();
        String username = msm.getUsername();
        String password = msm.getPassword();
        List<JMailRecv> fetchedEmails = new ArrayList<>();

        try {
            JMailRecv receiver = new JMailRecv(msm.getSession());
            receiver.enableDebugging();
            fetchedEmails = receiver.receiveEmails(username, password);
        } catch (MessagingException ex) {
            ex.printStackTrace();
        }
        return fetchedEmails;
    }
    public void updateEmailTable(List<JMailRecv> emails) {
        DefaultTableModel tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableModel.addColumn("Expéditeur");
        tableModel.addColumn("Objet");
        tableModel.addColumn("Contenu");
        tableModel.addColumn("Pièces jointes");

        for (JMailRecv email : emails) {
            Vector<Object> row = new Vector<>();

            row.add(email.getExpediteur());
            row.add(email.getSujet());
            row.add(email.getContenu());
            String attachments = String.join(", ", email.getAttachments());
            row.add(attachments);

            tableModel.addRow(row);
        }

        table1.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) { // Détecter un double-clic
                    JTable target = (JTable) e.getSource();
                    int row = target.getSelectedRow(); // Obtenir la ligne sélectionnée


                    showEmailDetailsDialog(row);
                }
            }
        });

        table1.setModel(tableModel);
    }

    public void setJTableMail() {
        receivedEmails = fetchEmails();

        updateEmailTable(receivedEmails);
    }

    public void setComboBox() {
        getChoixMail().removeAllItems();
        for (JMailRecv email : receivedEmails) {
            getChoixMail().addItem(email.getSujet());
        }

    }
    public void setJTree() {
        String selectedSubject = (String) getChoixMail().getSelectedItem();

        JMailRecv selectedMail = null;
        for (JMailRecv mail : receivedEmails) {
            if (mail.getSujet().equals(selectedSubject)) {
                selectedMail = mail;
                break;
            }
        }
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("mail " + selectedMail.getExpediteur());

        if (selectedMail != null) {
            List<String> receivedHeaders = selectedMail.getReceivedHeaders();


            String messageId = selectedMail.getMessageId();
            String contentType = selectedMail.getContentType();
            root.add(new DefaultMutableTreeNode("message-id : " + messageId));
            root.add(new DefaultMutableTreeNode("content-type : " + contentType));

            DefaultMutableTreeNode receivedHeaderNode = new DefaultMutableTreeNode("Received header:");


            if (receivedHeaders != null) {
                for (String header : receivedHeaders) {
                    String[] parts = header.split("\\s+");
                    StringBuilder buffer = new StringBuilder();
                    String key = null;
                    for (String part : parts) {
                        if (Arrays.asList("from", "by", "with", "id", "for").contains(part)) {

                            if (key != null && buffer.length() > 0) {
                                receivedHeaderNode.add(new DefaultMutableTreeNode(key + ": " + buffer.toString().trim()));
                            }
                            key = part;
                            buffer.setLength(0);
                        } else {
                            buffer.append(part).append(" ");
                        }
                    }
                    if (key != null && buffer.length() > 0) {
                        receivedHeaderNode.add(new DefaultMutableTreeNode(key + ": " + buffer.toString().trim()));
                    }
                }

            }

            root.add(receivedHeaderNode);
        }
        DefaultTreeModel treeModel1 = new DefaultTreeModel(root);
        treeModel1.reload();
        treeModel1.setAsksAllowsChildren(true);

        if (treeModel != null) {
            treeModel.setModel(treeModel1);
        }
    }
    private void showEmailDetailsDialog(int row) {
        DefaultTableModel model = (DefaultTableModel) table1.getModel();
        String subject = (String) model.getValueAt(row, 0);
        String sender = (String) model.getValueAt(row, 1);
        String content = (String) model.getValueAt(row, 2);

        List<String> attachments = receivedEmails.get(row).getAttachments();

        JDialog emailDialog = new JDialog(this, "Détails de l'email", true);
        emailDialog.setLayout(new BorderLayout());

        JTextArea emailContentArea = new JTextArea();
        emailContentArea.setText("Sujet: " + subject + "\n\n" +
                "Expéditeur: " + sender + "\n\n" +
                "Contenu:\n" + content);
        emailContentArea.setEditable(false);

        JPanel attachmentsPanel = new JPanel();
        attachmentsPanel.setLayout(new BoxLayout(attachmentsPanel, BoxLayout.Y_AXIS));

        for (String attachment : attachments) {
            JLabel attachmentLabel = new JLabel(attachment);
            attachmentLabel.setForeground(Color.BLUE.darker());
            attachmentLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            attachmentLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    try {
                        Desktop.getDesktop().browse(new File(attachment).toURI());
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }
            });
            attachmentsPanel.add(attachmentLabel);
        }

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.add(new JScrollPane(emailContentArea), BorderLayout.CENTER);

        if (!attachments.isEmpty()) {
            JScrollPane attachmentScrollPane = new JScrollPane(attachmentsPanel);
            contentPanel.add(attachmentScrollPane, BorderLayout.SOUTH);
        }

        emailDialog.add(contentPanel, BorderLayout.CENTER);


        emailDialog.setSize(400, 400);
        emailDialog.setLocationRelativeTo(this);
        emailDialog.setVisible(true);
    }




    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MiniOutlook dialog = new MiniOutlook();
            dialog.pack();
            dialog.setSize(new Dimension(450, 250));
            dialog.setVisible(true);
        });
    }

}
