package ru.inbox.vinnikov.tsys_sbb_railway_tickets.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

@Service
public class EMailService {
    @Value("${email.username}")
    private String emailUsername;
    @Value("${email.password}")
    private String emailPassword;

    @Autowired
    public EMailService() {}

    /*public EMailService(String emailUsername, String emailPassword) {
        this.emailUsername = emailUsername;
        this.emailPassword = emailPassword;
    }*/

    // отправка мэйла с аттаченным файлом
    public void sendFileMail(String textSubject, String textMail, String fileNameAndPath)
            throws MessagingException, UnsupportedEncodingException {
        //https://yandex.ru/support/mail/mail-clients.html (раздел "Исходящая почта")
        Properties properties = new Properties();
        //Хост или IP-адрес почтового сервера
        properties.put("mail.smtp.host", "smtp.yandex.ru");
        //Требуется ли аутентификация для отправки сообщения
        properties.put("mail.smtp.auth", "true");
        //Порт для установки соединения
        properties.put("mail.smtp.socketFactory.port", "465");
        //Фабрика сокетов, так как при отправке сообщения Yandex требует SSL-соединения
        properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

        //Создаем соединение для отправки почтового сообщения
        Session session = Session.getDefaultInstance(properties,
                //Аутентификатор - объект, который передает логин и пароль
                new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(emailUsername, emailPassword);
                    }
                });

        //Создаем новое почтовое сообщение
        Message message = new MimeMessage(session);
        //От кого
        message.setFrom(new InternetAddress("ptpsk@yandex.ru","SBB RAILWAYS (no reply)"));
        //Кому
        message.setRecipient(Message.RecipientType.TO, new InternetAddress("ptpsk@yandex.ru"));
        //Тема письма
        message.setSubject(textSubject);

        /*//Текст письма
        message.setText(textMail); // "Hello, Email!"*/

        //Файл вложения
        File file = new File(fileNameAndPath);
//Собираем содержимое письма из кусочков
        MimeMultipart multipart = new MimeMultipart();
//Первый кусочек - текст письма
        MimeBodyPart part1 = new MimeBodyPart();
        part1.addHeader(textSubject, "text/plain; charset=UTF-8"); // "Content-Type"
        part1.setDataHandler(new DataHandler
                (textMail, "text/plain; charset=\"utf-8\"")); // "Письмо с файлом!!"
        multipart.addBodyPart(part1);

//Второй кусочек - файл
        MimeBodyPart part2 = new MimeBodyPart();
        part2.setFileName(MimeUtility.encodeWord(file.getName()));
        part2.setDataHandler(new DataHandler(new FileDataSource(file)));
        multipart.addBodyPart(part2);
//Добавляем оба кусочка в сообщение
        message.setContent(multipart);

        //Поехали!!!
        Transport.send(message);
    }

    // отправка простого мэйла с текстом (без файла)
    public void sendSimpleMail(String textSubject, String textMail) throws MessagingException, UnsupportedEncodingException {
        //https://yandex.ru/support/mail/mail-clients.html (раздел "Исходящая почта")
        Properties properties = new Properties();
        //Хост или IP-адрес почтового сервера
        properties.put("mail.smtp.host", "smtp.yandex.ru");
        //Требуется ли аутентификация для отправки сообщения
        properties.put("mail.smtp.auth", "true");
        //Порт для установки соединения
        properties.put("mail.smtp.socketFactory.port", "465");
        //Фабрика сокетов, так как при отправке сообщения Yandex требует SSL-соединения
        properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

        //Создаем соединение для отправки почтового сообщения
        Session session = Session.getDefaultInstance(properties,
                //Аутентификатор - объект, который передает логин и пароль
                new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(emailUsername, emailPassword);
                    }
                });

        //Создаем новое почтовое сообщение
        Message message = new MimeMessage(session);
        //От кого
        message.setFrom(new InternetAddress("ptpsk@yandex.ru","SBB RAILWAYS (no reply)"));
        //Кому
        message.setRecipient(Message.RecipientType.TO, new InternetAddress("ptpsk@yandex.ru"));
        //Тема письма
        message.setSubject(textSubject); //
        //Текст письма
        message.setText(textMail); // "Hello, Email!"
        //Поехали!!!
        Transport.send(message);
    }
}
