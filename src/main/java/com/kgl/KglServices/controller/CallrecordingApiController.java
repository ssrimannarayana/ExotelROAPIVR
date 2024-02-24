package com.kgl.KglServices.controller;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kgl.KglServices.model.CampaignPojo;
import com.kgl.KglServices.services.PhonePeServcies;

@RestController
@RequestMapping({ "/call" })
public class CallrecordingApiController {

	private static final Logger logger = LoggerFactory.getLogger(CallrecordingApiController.class);

	@Autowired
	private PhonePeServcies phpeServices;

	@Value("${ROAP_CAMPAIGN_JSON_RESPONSE_URL}")
	private String ROAP_googlesheet_data_restapi;

	@Value("${END_TIME}")
	private String end_time;

	@GetMapping("/testApi")
	public String test() {
		return "hi this is roap sms";
	}

	@GetMapping("/RoApIvrCampaign")
	public Boolean ExotelCampaignROAP() throws Exception {
		boolean ivrStatus = false;
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
		LocalDateTime now = LocalDateTime.now();
		Date startTime = new SimpleDateFormat("HH:mm:ss").parse(dtf.format(now));
		Date endTime = new SimpleDateFormat("HH:mm:ss").parse(end_time);

		if (startTime.before(endTime)) {
			String ROAP_URL = ROAP_googlesheet_data_restapi;
			List<CampaignPojo> list = getCampData(ROAP_URL);
			int recordsSize = list.size();
			logger.info("ROAP IVR google sheet api records count:::" + recordsSize);
			if (recordsSize > 0) {
				return phpeServices.startIvrCampaignLists(list);
			}
		} else {
			logger.info("AP IVR service stopped due to time out");
			return false;
		}
		sendMail();
		return ivrStatus;
	}

	private void sendMail() {
		// TODO Auto-generated method stub
		String to = "operations.it@kanakadurgafinance.com";// change accordingly
		// Sender's email ID needs to be mentioned
		String from = "venkat@kanakadurgafinance.com";// change accordingly
		final String username = "venkat@kanakadurgafinance.com";// change accordingly
		final String password = "rfxhzjywnqgcggpm";// change accordingly
		// Assuming you are sending email through relay.jangosmtp.net
		String host = "smtp.gmail.com";
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.port", "587");

		// Get the Session object.
		Session session = Session.getInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});

		try {
			// Create a default MimeMessage object.
			Message message = new MimeMessage(session);

			// Set From: header field of the header.
			message.setFrom(new InternetAddress(from));

			// Set To: header field of the header.
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));

			// Set Subject: header field
			message.setSubject("ROAPIVR CAMPAINGS ENDED");

			// Now set the actual message
			message.setText("Hi, Bargavi ROAPIVR Call Campaigns Completed. Plz Tigger Callback API");

			// Send message
			Transport.send(message);

			System.out.println("Sent message successfully....");

		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
	}

	private List<CampaignPojo> getCampData(String campaign_ListUrls)
			throws JsonMappingException, JsonProcessingException {
		List<CampaignPojo> responseDataObj = null;
		try {
			ResponseEntity<String> response = new RestTemplate().getForEntity(campaign_ListUrls, String.class);
			ObjectMapper objectMapper;
			if (response != null) {
				String stringArry = response.getBody().toString();
				objectMapper = new ObjectMapper();
				responseDataObj = objectMapper.readValue(stringArry, new TypeReference<List<CampaignPojo>>() {
				});
			}
		} catch (Exception e) {
			logger.info("google sheet api response No records found:::" + e);
		}
		return responseDataObj;
	}

	@GetMapping("/RoApGoogleSheetUpdate")
	public Boolean updateCampaignAPI() throws Exception {
		return phpeServices.updateCampaignDataIntoGoogleSheet();
	}

}
