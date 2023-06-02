package com.example.demo.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.search.FlagTerm;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.host}")
    private String host;

    @Value("${spring.mail.port}")
    private int port;
    
    
    @Value("${spring.mail.username}")
    private String username;

    @Value("${spring.mail.password}")
    private String password;

    @Scheduled(fixedRate = 10000)
    public void receiveEmails() {
    	System.out.println("controll in");
        try {
            Properties properties = getMailProperties();

            Session session = Session.getInstance(properties, null);
            Store store = session.getStore("imaps");
            store.connect(host, username, password);

            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_WRITE);

            Flags seen = new Flags(Flags.Flag.SEEN);
            FlagTerm unseenFlagTerm = new FlagTerm(seen, false);
            Message[] messages = inbox.search(unseenFlagTerm);

            for (Message message : messages) {
                // Process each email message
                String subject = message.getSubject();
                String from = message.getFrom()[0].toString();
                String body = "";

                if (message.getContent() instanceof String) {
                    body = (String) message.getContent();
                } else if (message.getContent() instanceof Multipart) {
                    Multipart multipart = (Multipart) message.getContent();
                    for (int i = 0; i < multipart.getCount(); i++) {
                        BodyPart bodyPart = multipart.getBodyPart(i);

                        // Check if the part is an attachment
                        if (Part.ATTACHMENT.equalsIgnoreCase(bodyPart.getDisposition())) {
                            // Save the attachment to a local file
                            String fileName = bodyPart.getFileName();
                            
                            String absolutePath = new FileSystemResource("").getFile().getAbsolutePath();
                            createDirectoryIfNotExists(absolutePath);
                            String filePathlocation = absolutePath + File.separator + "ATTACHMENTS" + File.separator + fileName;
                            
                            String storagePath = "attachments/" + fileName;
                            saveAttachment(bodyPart, filePathlocation);
                            System.out.println("Attachment saved: " + fileName);
                        } else if (bodyPart.isMimeType("text/plain")) {
                            body = bodyPart.getContent().toString();
                        }
                    }
                }

                // Perform actions with the email data
                System.out.println("Subject: " + subject);
                System.out.println("From: " + from);
                System.out.println("Body: " + body);

                // Mark the email as read
                message.setFlag(Flags.Flag.SEEN, true);
            }

            inbox.close(false);
            store.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveAttachment(BodyPart bodyPart, String storagePath) throws IOException, MessagingException {
        FileOutputStream fos = null;
        
        File downloadedAttachmentFile = new File(storagePath);
        try (
                OutputStream out = new FileOutputStream(downloadedAttachmentFile)
                // InputStream in = dataSource.getInputStream()
        ) {
            InputStream in = bodyPart.getInputStream();
            IOUtils.copy(in, out);
        } catch (IOException e) {
        	System.out.println("IOException");
        }
    }

    private Properties getMailProperties() {
        Properties properties = new Properties();
        properties.put("mail.store.protocol", "imaps");
        properties.put("mail.imaps.host", host);
        properties.put("mail.imaps.port", port);
        return properties;
    }
    
    
    private void createDirectoryIfNotExists(String directoryPath) {
        if (!Files.exists(Paths.get(directoryPath))) {
            try {
                Files.createDirectories(Paths.get(directoryPath));
            } catch (IOException e) {
            }
            System.out.println("File Creation Exception");
        }
    }
}



