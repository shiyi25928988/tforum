package cc.shiyi.mcp.utils;



import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;

import java.util.Properties;

public class EmailUtil {

    public static String sendEmail(String from, String password, String to, String subject, String content) throws EmailException {
        SimpleEmail email = new SimpleEmail();
        // 设置 SMTP 服务器地址
        email.setHostName("mail.shiyi.intra");
        // 设置 SMTP 端口（587 通常用于 STARTTLS，465 用于 SSL）
        email.setSmtpPort(465);
        // 启用 STARTTLS 加密
        email.setStartTLSEnabled(true);
        // 设置认证信息
        email.setAuthentication(from, password);
        email.setFrom(from);
        // 设置邮件主题和内容
        email.setSubject(subject);
        email.setMsg(content);
        // 设置收件人
        email.addTo(to);
        // 发送邮件
        return email.send();
    }

}
