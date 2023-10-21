package model;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Properties;

public class JMailMultiPart {
    private final Session session;




    public JMailMultiPart(Session _session) {
        this.session = _session;
    }

    public void sendEmailWithAttachments(String from, String to, String subject, String body, ArrayList<File> listfile) throws MessagingException {
        try {

            Message message = new MimeMessage(session);

            message.setFrom(new InternetAddress(from));

            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));

            message.setSubject(subject);

            Multipart multipart = new MimeMultipart();
            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText(body);

            multipart.addBodyPart(messageBodyPart);

            for(File f : listfile){
                createAttachment(multipart,f);
            }

            message.setContent(multipart);

            Transport.send(message);

            System.out.println("E-mail avec pièces jointes envoyé avec succès.");

        } catch (MessagingException e) {
            e.printStackTrace();
            throw e;
        }
    }

    private void createAttachment(Multipart multipart, File f) throws MessagingException {
        BodyPart attachmentBodyPart = new MimeBodyPart();
        DataSource source = new FileDataSource(f.getPath());
        attachmentBodyPart.setDataHandler(new DataHandler(source));
        attachmentBodyPart.setFileName(f.getName());
        multipart.addBodyPart(attachmentBodyPart);
    }
}

