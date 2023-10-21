package model;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.*;

public class JMailSimplePart {
    private final Session session;



    public JMailSimplePart(Session _session) {
        this.session = _session;
    }

    public void sendSimpleEmail(String from, String to, String subject, String body) throws MessagingException {
        try {
            Message message = new MimeMessage(session);

            message.setFrom(new InternetAddress(from));

            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));

            message.setSubject(subject);

            message.setText(body);

            Transport.send(message);

            System.out.println("E-mail simple envoyé avec succès.");

        } catch (MessagingException e) {
            e.printStackTrace();
            throw e;
        }
    }
}
