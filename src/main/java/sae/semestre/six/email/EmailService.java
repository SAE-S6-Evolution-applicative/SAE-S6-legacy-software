/*
 * EmailService.java                                  19 mai. 2025
 * IUT de Rodez, no author rights
 */

package sae.semestre.six.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;


@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Autowired
    public EmailService(final JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Send a simple email
     *
     * @param receiver The receiver of the email
     * @param subject  The subject of the email
     * @param text     Content of the email
     */
    public void sendEmail(String receiver, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(receiver);
        message.setSubject(subject);
        message.setText(text);
        message.setFrom("hospital.system@gmail.com");

        mailSender.send(message);
    }
}