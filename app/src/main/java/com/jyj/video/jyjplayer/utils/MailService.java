package com.jyj.video.jyjplayer.utils;

import com.zjyang.base.utils.LogUtil;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.event.TransportEvent;
import javax.mail.event.TransportListener;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;


/**
 * author: wr
 * date: on 2018/8/29.
 */
public class MailService {

    public static final String TAG = "MailService";

    private static final String ACCOUNT = "742155745@qq.com";
    private static final String AUTHEN_CODE = "ewghqpxrvjadbdia";

    public static void sendGmail(String email, String content) throws IOException, MessagingException {
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
        props.put("mail.smtp.password", AUTHEN_CODE);//password
        // 创建会话，getDefaultInstace得到的始终是该方法初次创建的缺省的对象，getInstace每次获取新对象
        Session session = Session.getInstance(props, new MyAuthenticator(ACCOUNT,AUTHEN_CODE));
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

    public static void send(String email, String content){
        Properties props = new Properties();
        //邮件的协议 pop3 smtp imap
        props.put("mail.transport.protocol", "smtp");
        //== The default host name of the mail server for both Stores and Transports. Used if the mail.xxx.host property isn't set.
        //如果 mail.xxx.host 未设置的时候取它的值
        //props.put("mail.host", MAIL_HOST);
        //== The default user name to use when connecting to the mail server. Used if the mail.protocol.user property isn't set.
        //如果 mail.xxx.user 未设置的时候取它的值
        //props.put("mail.user", FROM_USER);
        props.put("mail.smtp.host", "smtp.qq.com");
        props.put("mail.smtp.port", "25");
        props.put("mail.smtp.user", ACCOUNT);//登录邮件服务器的用户名
        //props.put("mail.smtp.class", "mail.smtp.class");
        //qq 邮箱必须要有这个 否则报 530 错误
        props.put("mail.smtp.starttls.enable", "true");
        // 开启debug调试
        props.put("mail.debug", true);
        // 发件人地址，针对服务器来说的  mail.from / mail.smtp.from 邮件被退回（bounced）等时，被退到的邮箱地址 可以设置成 fromAddress
        //服务使用的地址，用来设置邮件的 return 地址。缺省是Message.getFrom()或InternetAddress.getLocalAddress()。mail.user / mail.smtp.user 会优先使用
        props.put("mail.from", ACCOUNT);
        // props.put("mail.mime.address.strict", true);//严格
        // props.put("mail.store.protocol", "mail.store.protocol");
        // 发送服务器需要身份验证
        props.put("mail.smtp.auth", true);

        //使用SSL安全连接  java 1.8 有问题
       /* props.put("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.fallback", "false");
        props.put("mail.smtp.socketFactory.port", setupHostConfig().getEmailHostPortSsl());*/

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(ACCOUNT, AUTHEN_CODE);
            }
        });

        try {
            // MimeMessage 邮件类
            MimeMessage msg = new MimeMessage(session);
            // ！！！设置发件人地址，针对邮件来说的，是邮件类的属性，收件人邮箱里可以看到的，知道邮件是由谁发的
            //msg.setFrom(new InternetAddress("创业软件" + "<" + fromAddress + ">"));
            msg.setFrom(ACCOUNT);
            //多个收信人地址 逗号隔开 可以用 Address InternetAddress 封装
            // msg.addRecipients(Message.RecipientType.TO, TO_EMAIL);
            msg.setRecipients(Message.RecipientType.TO, ACCOUNT);
            //抄送 (抄送一份给发件人，降低 163之类的 报 554 错误（垃圾邮件，屏蔽问题） 的概率)
            //msg.addRecipient(Message.RecipientType.CC, addressAndName);
            msg.setSubject("反馈", "UTF-8");
            // msg.setSubject(title);//设置主题
            msg.setSentDate(new Date());//设置时间

            String contentGroup = "联系方式: " + email + "   内容: " + content;
            Multipart multipart = new MimeMultipart();
            MimeBodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setContent(contentGroup,  "text/html; charset=utf-8");
            multipart.addBodyPart(messageBodyPart);
            msg.setContent(multipart);
            // 设置回复人 ( 收件人回复此邮件时,不设置就是默认收件人 )
            // msg.setReplyTo();
            // 设置优先级(1:紧急   3:普通    5:低)
            // msg.setHeader("X-Priority", "1");
            // 要求阅读回执(收件人阅读邮件时会提示回复发件人,表明邮件已收到,并已阅读)
            //msg.setHeader("Disposition-Notification-To", fromAddress.toString());

            msg.saveChanges();

            // 发送
            Transport.send(msg);
            Transport transport = session.getTransport();
            transport.addTransportListener(new TransportListener() {
                @Override
                public void messageDelivered(TransportEvent e) {
                    LogUtil.d(TAG, "邮件送达");
                }

                @Override
                public void messageNotDelivered(TransportEvent e) {
                    LogUtil.d(TAG, "邮件未送达");
                }

                @Override
                public void messagePartiallyDelivered(TransportEvent e) {
                    LogUtil.d(TAG, "messagePartiallyDelivered");
                }
            });
            LogUtil.d(TAG, "SIZE: " + msg.getAllRecipients().length);
            transport.connect();
            transport.sendMessage(msg, msg.getAllRecipients());
            transport.close();

        } catch (MessagingException mex) {
            LogUtil.e(TAG, "getMimeMessage:" + mex.getMessage());
            LogUtil.e(TAG, "getMimeMessage:" + mex.getNextException());
        }
    }

    private static class MyAuthenticator extends Authenticator{

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

