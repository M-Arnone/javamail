package model;

import controller.Controler;
import gui.MiniOutlook;

import javax.swing.*;
import java.util.List;

public class MonThread extends Thread {
    private MiniOutlook miniOutlook;
    private int pollingInterval;
    private List<JMailRecv> currentEmails;

    public MonThread(MiniOutlook miniOutlook, int pollingInterval) {
        this.miniOutlook = miniOutlook;
        this.pollingInterval = pollingInterval;
        this.currentEmails = miniOutlook.getReceivedEmails();
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                List<JMailRecv> newEmails = miniOutlook.fetchEmails();

                if (newEmails.size() != currentEmails.size()) {
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(miniOutlook, "Nouveau message reçu");
                        miniOutlook.setJTableMail();
                    });
                    currentEmails = newEmails;
                }
                Thread.sleep(pollingInterval);
            }
        } catch (InterruptedException ex) {

        }
    }
}

