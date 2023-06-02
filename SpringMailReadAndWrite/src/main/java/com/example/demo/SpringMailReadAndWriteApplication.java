package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.example.demo.service.EmailService;

@SpringBootApplication
@EnableScheduling
public class SpringMailReadAndWriteApplication 
{
	public static void main(String[] args) 
	{
		ApplicationContext applicationContext = SpringApplication.run(SpringMailReadAndWriteApplication.class, args);
//		EmailService bean = applicationContext.getBean(EmailService.class);
//		bean.receiveEmails();
	}
}
