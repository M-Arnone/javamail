package gui;


import controller.Controler;

import javax.swing.*;
import java.awt.*;

public class Login extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JLabel adresse;
    private JLabel password;
    private JPanel JPanelImage;
    private JLabel JLabelImage;
    private JTextField AdresseMail;
    private JPasswordField passwordField1;

    public JButton getButtonOK(){return buttonOK;}
    public JButton getButtonCancel(){return buttonCancel;}

    public JTextField getAdresseMail(){return AdresseMail;}

    public JPasswordField getPasswordField(){return passwordField1;}

    public Login() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
    }
    public void setControler(Controler c){
        getButtonOK().addActionListener(c);
        getButtonCancel().addActionListener(c);
        getAdresseMail().addActionListener(c);
        getPasswordField().addActionListener(c);
        addWindowListener(c);
    }

    public static void main(String[] args) {
        Login dialog = new Login();
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setSize(new Dimension(700, 500));
        dialog.setVisible(true);
    }

}
