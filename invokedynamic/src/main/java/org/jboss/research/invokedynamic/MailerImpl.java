package org.jboss.research.invokedynamic;

public class MailerImpl implements Mailer {
    @Override
    public void sendAMail(String mailAddress, String body) {
        System.out.println("send a mail ...");
    }
}
