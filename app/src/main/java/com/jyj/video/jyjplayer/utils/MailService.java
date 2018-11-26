package com.jyj.video.jyjplayer.utils;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;


/**
 * author: wr
 * date: on 2018/8/29.
 */
public class MailService {

    private static final String ACCOUNT = "";
    private static final String PASSWORD = "";

    public static void send_email(String email, String content) throws IOException, MessagingException {
        File file = new File(content);


        //创建配置文件
        Properties props = new Properties();
        // 开启认证
        props.put("mail.smtp.auth", true);
        // 设置协议方式
        props.put("mail.transport.protocol", "gsmtp");
        // 设置主/机名
        props.put("mail.smtp.host", "smtp.gmail.com");//host
        // 设置SSL加密(未采用SSL时，端口一般为25，可以不用设置；采用SSL时，端口为465，需要显示设置)
        props.setProperty("mail.smtp.port", "465");
        props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.setProperty("mail.smtp.socketFactory.fallback", "false");
        props.setProperty("mail.smtp.socketFactory.port", "465");
        // 设置账户和密码
        props.put("mail.smtp.username", ACCOUNT);//username
        props.put("mail.smtp.password", PASSWORD);//password
        // 创建会话，getDefaultInstace得到的始终是该方法初次创建的缺省的对象，getInstace每次获取新对象
        Session session = Session.getInstance(props, new MyAuthenticator(ACCOUNT,PASSWORD));
        // 显示错误信息
        session.setDebug(true);
        // 创建发送时的消息对象
        MimeMessage message = new MimeMessage(session);
        // 设置发送发的账户和名称
        message.setFrom(new InternetAddress(email, "user", "UTF-8"));
        // 获取收件方的账户和名称
        message.setRecipients(MimeMessage.RecipientType.TO, ACCOUNT);
        // 设置主题
        message.setSubject("反馈");

        // 设置带附件的内容
        Multipart multipart = new MimeMultipart();
        MimeBodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setContent(content,  "text/html; charset=utf-8");
        multipart.addBodyPart(messageBodyPart);
        // 添加邮件附件
//        MimeBodyPart attachPart = new MimeBodyPart();
//        DataSource source = new FileDataSource(content);
//        attachPart.setDataHandler(new DataHandler(source));
//        attachPart.setFileName("作业统计");
//        multipart.addBodyPart(attachPart);
        // 保存邮件内容
        message.setContent(multipart);

        // 设置文本内容内容
        //message.setContent(content, "text/html; charset=utf-8");

        // 发送
        Transport.send(message);

    }

    static class MyAuthenticator extends Authenticator{

        String userName = null;
        String password = null;

        public MyAuthenticator(String username, String password) {
            this.userName = username;
            this.password = password;
        }
        protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(userName, password);
        }
    }
}

