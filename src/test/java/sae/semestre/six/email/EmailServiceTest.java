/*
 * EmailServiceTest.java                                 21 avr. 2025
 * IUT de Rodez, no author rights
 */

package sae.semestre.six.email;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mockMailSender;

    private EmailService emailService;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setUp() {
        // Redirect standard output to capture messages
        System.setOut(new PrintStream(outContent));

        // Get the singleton instance
        emailService = EmailService.getInstance();

        // Inject mock in the singleton instance
        ReflectionTestUtils.setField(emailService, "mailSender", mockMailSender);
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    void testSendEmailSuccess() {
        // Arrange
        String to = "destinataire@exemple.com";
        String subject = "Sujet de test";
        String body = "Corps du message de test";

        // Act
        emailService.sendEmail(to, subject, body);

        // Assert
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mockMailSender).send(messageCaptor.capture());

        SimpleMailMessage sentMessage = messageCaptor.getValue();
        assertEquals("hospital.system@gmail.com", sentMessage.getFrom());
        assertEquals(to, sentMessage.getTo()[0]);
        assertEquals(subject, sentMessage.getSubject());
        assertEquals(body, sentMessage.getText());

        assertTrue(outContent.toString().contains("Email sent successfully"));
    }

    @Test
    void testSendEmailFailure() {
        // Arrange
        String to = "destinataire@exemple.com";
        String subject = "Sujet de test";
        String body = "Corps du message de test";

        doThrow(new RuntimeException("Erreur test")).when(mockMailSender).send(any(SimpleMailMessage.class));

        // Act
        emailService.sendEmail(to, subject, body);

        // Assert
        assertTrue(outContent.toString().contains("Failed to send email: Erreur test"));
    }

    @Test
    void testGetInstance() {
        // Act
        EmailService instance1 = EmailService.getInstance();
        EmailService instance2 = EmailService.getInstance();

        // Assert
        assertNotNull(instance1);
        assertSame(instance1, instance2, "getInstance devrait retourner la même instance (pattern Singleton)");
    }
}