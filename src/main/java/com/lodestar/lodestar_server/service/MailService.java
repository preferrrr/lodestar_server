package com.lodestar.lodestar_server.service;

import com.lodestar.lodestar_server.entity.Mail;
import com.lodestar.lodestar_server.exception.DuplicateMailException;
import com.lodestar.lodestar_server.exception.SendMailFailException;
import com.lodestar.lodestar_server.repository.MailRepository;
import com.lodestar.lodestar_server.repository.UserRepository;
import jakarta.mail.Message;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
@AllArgsConstructor
@Transactional
public class MailService {

    private final JavaMailSender mailSender;

    private final UserRepository userRepository;

    private final MailRepository mailRepository;

    private MimeMessage createMessage(String to, String authCode) throws Exception {
        System.out.println("보내는 대상 : " + to);
        System.out.println("인증 코드 : " + authCode);
        MimeMessage message = mailSender.createMimeMessage();

        message.addRecipients(Message.RecipientType.TO, to);//보내는 대상
        message.setSubject("LODESTAR 인증 코드");//제목

        String msgg = "";
        msgg += "<div style='margin:20px;'>";
        msgg += "<h1> 안녕하세요 LODESTAR 입니다. </h1>";
        msgg += "<br>";
        msgg += "<p>아래 코드를 복사해 입력해주세요<p>";
        msgg += "<br>";
        msgg += "<p>감사합니다.<p>";
        msgg += "<br>";
        msgg += "<div align='center' style='border:1px solid black; font-family:verdana';>";
        msgg += "<h3 style='color:blue;'>회원가입 인증 코드입니다.</h3>";
        msgg += "<div style='font-size:130%'>";
        msgg += "CODE : <strong>";
        msgg += authCode + "</strong><div><br/> ";
        msgg += "</div>";
        message.setText(msgg, "utf-8", "html");//내용
        message.setFrom(new InternetAddress("tjsgh2946@gmail.com", "LODESTAR"));//보내는 사람

        return message;
    }

    public static String createKey() {
        StringBuffer key = new StringBuffer();
        Random rnd = new Random();

        for (int i = 0; i < 8; i++) { // 인증코드 8자리
            int index = rnd.nextInt(3); // 0~2 까지 랜덤

            switch (index) {
                case 0:
                    key.append((char) ((int) (rnd.nextInt(26)) + 97));
                    //  a~z  (ex. 1+97=98 => (char)98 = 'b')
                    break;
                case 1:
                    key.append((char) ((int) (rnd.nextInt(26)) + 65));
                    //  A~Z
                    break;
                case 2:
                    key.append((rnd.nextInt(10)));
                    // 0~9
                    break;
            }
        }
        return key.toString();
    }

    public void sendMail(String to) throws Exception {

        if (duplicateEmail(to)) {
            throw new DuplicateMailException(to);
        }

        String key = createKey();
        MimeMessage message = createMessage(to, key);
        Mail mail = new Mail();
        mail.setEmail(to);
        mail.setAuthKey(key);

        try {
            mailSender.send(message);
            mailRepository.save(mail);
        } catch (MailException e) {
            throw new SendMailFailException(to);
        }
    }

    private boolean duplicateEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    /** 이메일로 테이블 조회해서 그 이메일에 대응되는 Key값과 비교*/
    public boolean checkKey(String email, String key) {

        List<Mail> emails = mailRepository.findByEmail(email);
        Mail testEmail = emails.get(emails.size() - 1);
        String testKey = testEmail.getAuthKey();

        if(key.equals(testKey) && validKeyTime(testEmail.getCreatedAt())) {
            return true;
        }
        else return false;
    }

    //생성된 시간 + 3분보다 현재가 더 작아야함
    private boolean validKeyTime(LocalDateTime createdTime) {
        LocalDateTime now = LocalDateTime.now();

        return now.isBefore(createdTime.plusMinutes(3)) && now.isAfter(createdTime);
    }


}
