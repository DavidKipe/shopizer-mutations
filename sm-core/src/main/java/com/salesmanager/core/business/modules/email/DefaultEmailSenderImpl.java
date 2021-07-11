package com.salesmanager.core.business.modules.email;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.Map;
import java.util.Properties;
import javax.inject.Inject;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import org.springframework.mail.MailPreparationException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Component;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

@Component("defaultEmailSender")
public class DefaultEmailSenderImpl implements EmailModule {

  @Inject
  private Configuration freemarkerMailConfiguration;
  
  @Inject
  private JavaMailSender mailSender;

  private static final String CHARSET = "UTF-8";
  private EmailConfig emailConfig;

  private final static String TEMPLATE_PATH = "templates/email";

  @Override
  public void send(Email email) throws Exception {

    final String eml = email.getFrom();
    final String from = email.getFromEmail();
    final String to = email.getTo();
    final String subject = email.getSubject();
    final String tmpl = email.getTemplateName();
    final Map<String, String> templateTokens = email.getTemplateTokens();

    MimeMessagePreparator preparator = new MimeMessagePreparator() {
      public void prepare(MimeMessage mimeMessage) throws MessagingException, IOException {

        JavaMailSenderImpl impl = (JavaMailSenderImpl) mailSender;
        // if email configuration is present in Database, use the same
								System.out.println("$#371#"); if (emailConfig != null) {
										System.out.println("$#372#"); impl.setProtocol(emailConfig.getProtocol());
										System.out.println("$#373#"); impl.setHost(emailConfig.getHost());
										System.out.println("$#374#"); impl.setPort(Integer.parseInt(emailConfig.getPort()));
										System.out.println("$#375#"); impl.setUsername(emailConfig.getUsername());
										System.out.println("$#376#"); impl.setPassword(emailConfig.getPassword());

          Properties prop = new Properties();
          prop.put("mail.smtp.auth", emailConfig.isSmtpAuth());
          prop.put("mail.smtp.starttls.enable", emailConfig.isStarttls());
										System.out.println("$#377#"); impl.setJavaMailProperties(prop);
        }

								System.out.println("$#378#"); mimeMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(to));

        InternetAddress inetAddress = new InternetAddress();

								System.out.println("$#379#"); inetAddress.setPersonal(eml);
								System.out.println("$#380#"); inetAddress.setAddress(from);

								System.out.println("$#381#"); mimeMessage.setFrom(inetAddress);
								System.out.println("$#382#"); mimeMessage.setSubject(subject);

        Multipart mp = new MimeMultipart("alternative");

        // Create a "text" Multipart message
        BodyPart textPart = new MimeBodyPart();
								System.out.println("$#383#"); freemarkerMailConfiguration.setClassForTemplateLoading(DefaultEmailSenderImpl.class, "/");
        Template textTemplate = freemarkerMailConfiguration.getTemplate(
            new StringBuilder(TEMPLATE_PATH).append("").append("/").append(tmpl).toString());
        final StringWriter textWriter = new StringWriter();
        try {
										System.out.println("$#384#"); textTemplate.process(templateTokens, textWriter);
        } catch (TemplateException e) {
          throw new MailPreparationException("Can't generate text mail", e);
        }
								System.out.println("$#385#"); textPart.setDataHandler(new javax.activation.DataHandler(new javax.activation.DataSource() {
          public InputStream getInputStream() throws IOException {
            // return new StringBufferInputStream(textWriter
            // .toString());
												System.out.println("$#394#"); return new ByteArrayInputStream(textWriter.toString().getBytes(CHARSET));
          }

          public OutputStream getOutputStream() throws IOException {
            throw new IOException("Read-only data");
          }

          public String getContentType() {
												System.out.println("$#395#"); return "text/plain";
          }

          public String getName() {
												System.out.println("$#396#"); return "main";
          }
        }));
								System.out.println("$#386#"); mp.addBodyPart(textPart);

        // Create a "HTML" Multipart message
        Multipart htmlContent = new MimeMultipart("related");
        BodyPart htmlPage = new MimeBodyPart();
								System.out.println("$#387#"); freemarkerMailConfiguration.setClassForTemplateLoading(DefaultEmailSenderImpl.class, "/");
        Template htmlTemplate = freemarkerMailConfiguration.getTemplate(
            new StringBuilder(TEMPLATE_PATH).append("").append("/").append(tmpl).toString());
        final StringWriter htmlWriter = new StringWriter();
        try {
										System.out.println("$#388#"); htmlTemplate.process(templateTokens, htmlWriter);
        } catch (TemplateException e) {
          throw new MailPreparationException("Can't generate HTML mail", e);
        }
								System.out.println("$#389#"); htmlPage.setDataHandler(new javax.activation.DataHandler(new javax.activation.DataSource() {
          public InputStream getInputStream() throws IOException {
            // return new StringBufferInputStream(htmlWriter
            // .toString());
												System.out.println("$#397#"); return new ByteArrayInputStream(textWriter.toString().getBytes(CHARSET));
          }

          public OutputStream getOutputStream() throws IOException {
            throw new IOException("Read-only data");
          }

          public String getContentType() {
												System.out.println("$#398#"); return "text/html";
          }

          public String getName() {
												System.out.println("$#399#"); return "main";
          }
        }));
								System.out.println("$#390#"); htmlContent.addBodyPart(htmlPage);
        BodyPart htmlPart = new MimeBodyPart();
								System.out.println("$#391#"); htmlPart.setContent(htmlContent);
								System.out.println("$#392#"); mp.addBodyPart(htmlPart);

								System.out.println("$#393#"); mimeMessage.setContent(mp);

        // if(attachment!=null) {
        // MimeMessageHelper messageHelper = new
        // MimeMessageHelper(mimeMessage, true);
        // messageHelper.addAttachment(attachmentFileName, attachment);
        // }

      }
    };

				System.out.println("$#367#"); mailSender.send(preparator);
  }

  public Configuration getFreemarkerMailConfiguration() {
				System.out.println("$#368#"); return freemarkerMailConfiguration;
  }

  public void setFreemarkerMailConfiguration(Configuration freemarkerMailConfiguration) {
    this.freemarkerMailConfiguration = freemarkerMailConfiguration;
  }

  public JavaMailSender getMailSender() {
				System.out.println("$#369#"); return mailSender;
  }

  public void setMailSender(JavaMailSender mailSender) {
    this.mailSender = mailSender;
  }

  public EmailConfig getEmailConfig() {
				System.out.println("$#370#"); return emailConfig;
  }

  public void setEmailConfig(EmailConfig emailConfig) {
    this.emailConfig = emailConfig;
  }

}
