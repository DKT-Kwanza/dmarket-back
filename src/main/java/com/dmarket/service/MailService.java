package com.dmarket.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MailService {

    private final JavaMailSender mailSender;

    @Transactional
    public void sendEmail(String toEmail, String title, String text) {

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name());

            String htmlMsg =
                    "<div style='width:100%;height:100%;color:#333'>"
                    + "<div style='border:1px solid #ccc;width:730px;height:800px;margin:50px auto;border-radius:10px'>"
                    + "<div style='color:#000000;margin:0 50px;font-size:20px;text-align:left;margin-top:60px'>"
                    + "<h3>안녕하세요, 고객님.</h3>"
                    + "</div>"
                    + "<div style='color:#000000;margin:0 50px;font-size:19px;text-align:left;margin-top:30px'>DmarKeT을 찾아주셔서 감사합니다! 인증 코드는</div>"
                    + "<div style='color:#0052a9;font-size:30px;margin-top:30px;text-align:center'>"
                    + "<h3>" + text + "</h3>"
                    + "</div>"
                    + "<div style='color:#000000;margin:0 50px;font-size:19px;text-align:left;margin-top:50px'>이며, 10분 내에 입력해 주시면 감사하겠습니다.</div>"
                    + "<div style='margin:0 50px;font-size:20px;text-align:left;margin-top:80px'><img src=\"http://dmarketmall.com/static/media/logo.5b04dd7ad81677c60299.png\" style=\"height:76px\">"
                    + "<div style='border-bottom:2px dashed #000;margin-top:15px'></div>"
                    + "</div>"
                    + "<div style='color:#808080;margin:0 50px;font-size:19px;text-align:left;margin-top:17px'>타인과 인증 코드를 공유하지 말아 주세요."
                    + "<br>시스템 메세지입니다, 해당 메일로 답장을 보내지 말아 주세요."
                    + "</div>"
                    + "</div>";

            helper.setTo(toEmail);
            helper.setText(htmlMsg, true);
            helper.setSubject(title);

            mailSender.send(message);
        } catch (MessagingException e) {
            log.warn("MailService.sendEmail exception occur toEmail: {}, title: {}, text: {}", toEmail, title, text);
            throw new RuntimeException(e.getMessage(), e.getCause());
        }
    }

    private SimpleMailMessage createEmailForm(String toEmail, String title, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject(title);
        message.setText(text);

        return message;
    }
}
