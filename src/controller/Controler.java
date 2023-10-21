package controller;

import gui.*;
import model.*;
import javax.mail.*;
import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class Controler  implements ActionListener, WindowListener {
    private Login _jdl = new Login();
    private MiniOutlook _jmi = new MiniOutlook();
    private ArrayList<File> attachmentsList;
    MailSessionManager msm;

    public Controler(){};
    public Controler(Login jdl){_jdl = jdl;}
    public Controler(MiniOutlook jmi,ArrayList<File> attachmentsList){
        _jmi = jmi;
        this.attachmentsList = attachmentsList;
    }

    public void setAttachmentsList(ArrayList<File> attachmentsList) {
        this.attachmentsList = attachmentsList;
    }





    @Override
    public void actionPerformed(ActionEvent e) {

        if(e.getSource() == _jdl.getButtonOK())
        {
            String loginTextFieldText = _jdl.getAdresseMail().getText();
            char[] passwordChars =_jdl.getPasswordField().getPassword();
            String password = new String(passwordChars);

            msm = MailSessionManager.getInstance();
            msm.setUsername(loginTextFieldText);
            msm.setPassword(password);
            _jmi.setJTableMail();

            _jdl.setVisible(false);
            _jmi.setSize(700,500);
            _jmi.setLocationRelativeTo(null);
            _jmi.setControler(this,loginTextFieldText,password);
            _jmi.setComboBox();
            _jmi.setVisible(true);

            MonThread th1 = new MonThread(_jmi,5000);
            th1.start();
        }

        if(e.getSource() == _jmi.getEnvoyer())
        {
            String expediteur = _jmi.getJTextFieldExpediteur().getText();
            String destinataire =  _jmi.getJTextFieldDestinataire().getText();
            String obj =  _jmi.getTextFieldObjet().getText();
            String contenu =  _jmi.getTextAreaContenu().getText();

            Session session = msm.getSession();

            try {
                if (!attachmentsList.isEmpty()) {
                    _jmi.getTextFieldPieceJointe().setText("");
                    JMailMultiPart mailer = new JMailMultiPart(session);
                    mailer.sendEmailWithAttachments(expediteur, destinataire, obj, contenu, attachmentsList);
                    attachmentsList.clear();
                } else {
                    JMailSimplePart simpleMailer = new JMailSimplePart(session);

                    simpleMailer.sendSimpleEmail(expediteur, destinataire, obj, contenu);
                }
            } catch (MessagingException ec) {
                ec.printStackTrace();
            }



        }
        if(e.getSource() == _jmi.getActualiser())
        {
            _jmi.setJTableMail();
            _jmi.setComboBox();
        }
        if(e.getSource() == _jmi.getButtonJTree()){
            _jmi.setJTree();
        }



    }


    @Override
    public void windowOpened(WindowEvent e) {

    }

    @Override
    public void windowClosing(WindowEvent e) {

    }

    @Override
    public void windowClosed(WindowEvent e) {
        e.getWindow().dispose();
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
