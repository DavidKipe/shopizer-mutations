package com.salesmanager.core.business.modules.email;

import javax.inject.Inject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component("htmlEmailSender")
public class EmailComponent implements HtmlEmailSender {
  
  @Value("${config.emailSender}")
  private String emailSender;

  @Inject
  private EmailModule defaultEmailSender;

  @Inject
  private EmailModule sesEmailSender;

  @Override
  public void send(Email email) throws Exception {
    switch(emailSender) 
    { 
        case "default": 
										System.out.println("$#406#"); defaultEmailSender.send(email);
            break; 
        case "ses": 
										System.out.println("$#407#"); sesEmailSender.send(email);
            break; 
        default: 
            throw new Exception("No email implementation for " + emailSender); 
    }
    
  }

  @Override
  public void setEmailConfig(EmailConfig emailConfig) {
    switch(emailSender) 
    { 
        case "default": 
										System.out.println("$#408#"); defaultEmailSender.setEmailConfig(emailConfig);
            break; 
        case "ses": 
										System.out.println("$#409#"); sesEmailSender.setEmailConfig(emailConfig);
            break; 
        default: 
 
    }
    
  }



}
