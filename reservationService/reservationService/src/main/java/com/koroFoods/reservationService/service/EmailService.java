package com.koroFoods.reservationService.service;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService implements NotificacionService {
    
    private final JavaMailSender mailSender;
    
    @Override
    public void enviarCodigoVerificacion(String destinatario, String codigo, String nombreUsuario) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setTo(destinatario);
            helper.setSubject("Código de Verificación - Reserva");
            helper.setText(construirContenidoEmail(codigo, nombreUsuario), true);
            
            mailSender.send(message);
            log.info("Email enviado exitosamente a: {}", destinatario);
            
        } catch (MessagingException e) {
            log.error("Error al enviar email a {}: {}", destinatario, e.getMessage());
            throw new RuntimeException("Error al enviar el email de verificación", e);
        }
    }
    
    private String construirContenidoEmail(String codigo, String nombreUsuario) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .code-box { 
                        background-color: #f4f4f4; 
                        padding: 20px; 
                        text-align: center; 
                        font-size: 32px; 
                        font-weight: bold; 
                        letter-spacing: 5px;
                        margin: 20px 0;
                        border-radius: 5px;
                    }
                    .warning { color: #666; font-size: 12px; margin-top: 20px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <h2>Hola %s,</h2>
                    <p>Tu código de verificación para confirmar tu asistencia es:</p>
                    <div class="code-box">%s</div>
                    <p>Este código expirará en 15 minutos.</p>
                    <p class="warning">
                        Si no solicitaste este código, por favor ignora este mensaje.
                    </p>
                </div>
            </body>
            </html>
            """.formatted(nombreUsuario, codigo);
    }
}