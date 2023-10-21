package model;

import javax.mail.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

public class JMailRecv {
    private Session session;
    private String expediteur;
    private String sujet;
    private String contenu;
    private List<String> attachments;
    private List<String> receivedHeaders;
    private String messageId;
    private String contentType;

    public List<JMailRecv> getReceivedEmails() {
        return receivedEmails;
    }

    public void enableDebugging() {
        if (session != null) {
            session.setDebug(true);
        }
    }



    List<JMailRecv> receivedEmails = new ArrayList<>();
    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public List<String> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<String> attachments) {
        this.attachments = attachments;
    }

    public String getExpediteur() {
        return expediteur;
    }

    public void setExpediteur(String expediteur) {
        this.expediteur = expediteur;
    }

    public String getSujet() {
        return sujet;
    }

    public void setSujet(String sujet) {
        this.sujet = sujet;
    }

    public String getContenu() {
        return contenu;
    }
    public List<String> getReceivedHeaders() {
        return receivedHeaders;
    }

    public void extractAndStoreHeaders(Message message) {
        try {
            Enumeration<Header> headers = message.getAllHeaders();
            receivedHeaders = new ArrayList<>();
            while (headers.hasMoreElements()) {
                Header header = headers.nextElement();
                if (header.getName().equalsIgnoreCase("Received") || header.getName().equalsIgnoreCase("Return-Path")) {
                    receivedHeaders.add(header.getName() + " --> " + header.getValue());
                }
            }
        } catch (MessagingException me) {
            me.printStackTrace();
        }
    }


    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public JMailRecv(Session _session) {
        this.session = _session;
        this.attachments = new ArrayList<>();
    }

    public List<JMailRecv> receiveEmails(String username, String password) throws MessagingException {

        String host = "imap.gmail.com";
        Properties properties = new Properties();
        properties.put("mail.store.protocol", "imaps");

        try {
            session = Session.getInstance(properties);

            Store store = session.getStore();
            store.connect(host, username, password);

            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);

            Message[] messages = inbox.getMessages();

            for (Message message : messages) {
                JMailRecv receivedEmail = new JMailRecv(session);
                receivedEmail.setExpediteur(message.getFrom()[0].toString());
                receivedEmail.setSujet(message.getSubject());
                receivedEmail.extractAndStoreHeaders(message);

                try {
                    String[] messageIdHeaders = message.getHeader("Message-ID");
                    if (messageIdHeaders != null && messageIdHeaders.length > 0) {
                        receivedEmail.setMessageId(messageIdHeaders[0]);
                    } else {
                        receivedEmail.setMessageId("N/A");
                    }


                    String[] contentTypeHeaders = message.getHeader("Content-Type");
                    if (contentTypeHeaders != null && contentTypeHeaders.length > 0) {
                        String baseContentType = contentTypeHeaders[0].split(";")[0].trim();
                        receivedEmail.setContentType(baseContentType);
                    } else {
                        receivedEmail.setContentType("N/A");
                    }
                } catch (MessagingException e) {
                    e.printStackTrace();
                }

                if (message.isMimeType("text/plain")) {

                    receivedEmail.setContenu((String) message.getContent());
                } else if (message.isMimeType("multipart/*")) {
                    Multipart multipart = (Multipart) message.getContent();

                    StringBuilder textContent = new StringBuilder();
                    List<String> attachments = new ArrayList<>();

                    for (int i = 0; i < multipart.getCount(); i++) {
                        BodyPart bodyPart = multipart.getBodyPart(i);

                        if (bodyPart.getDisposition() != null &&
                                bodyPart.getDisposition().equalsIgnoreCase(Part.ATTACHMENT)) {

                            String attachmentName = bodyPart.getFileName();

                            String savePath =  attachmentName;

                            InputStream attachmentStream = bodyPart.getInputStream();

                            try (FileOutputStream fos = new FileOutputStream(savePath)) {
                                byte[] buffer = new byte[1024];
                                int bytesRead;
                                while ((bytesRead = attachmentStream.read(buffer)) != -1) {
                                    fos.write(buffer, 0, bytesRead);
                                }
                            }

                            attachments.add(attachmentName);
                            System.out.println("Pièce jointe " + attachmentName + " enregistrée à " + savePath);
                        } else if (bodyPart.isMimeType("text/plain")) {

                            textContent.append((String) bodyPart.getContent());
                        }
                    }

                    receivedEmail.setContenu(textContent.toString());
                    receivedEmail.setAttachments(attachments);
                }

                receivedEmails.add(receivedEmail);


                System.out.println("Headers for message:");
                Enumeration<Header> headers = message.getAllHeaders();
                while (headers.hasMoreElements()) {
                    Header header = headers.nextElement();
                    System.out.println(header.getName() + ": " + header.getValue());
                }
                System.out.println("End of headers for message.");
            }

            inbox.close(false);
            store.close();

        } catch (NoSuchProviderException e) {
            System.out.println("Erreur sur provider : " + e.getMessage());
        } catch (MessagingException e) {
            System.out.println("Erreur sur message : " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Erreur sur I/O : " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Erreur indéterminée : " + e.getMessage());
        }

        return receivedEmails;
    }


}
