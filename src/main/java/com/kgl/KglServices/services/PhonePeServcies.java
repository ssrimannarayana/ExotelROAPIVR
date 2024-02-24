package com.kgl.KglServices.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.kgl.KglServices.exceptions.CustomFileNotFoundException;
import com.kgl.KglServices.model.CampaignPojo;
import com.kgl.KglServices.utility.Utility;

@Service
public class PhonePeServcies {

	@Value("${ROAP_CAMPAIGN_UPDATE_DATA_INTO_GOOGLESHEET_URL}")
	private String ROAP_CAMPAIGN_UPDATE_GOOGLESHEET_URL;

	@Value("${EXOTEL_USER_NAME}")
	private String EXOTEL_USER_NAME;

	@Value("${EXOTEL_PASSWORD}")
	private String EXOTEL_PASSWORD;

	@Value("${EXOTEL_CALL_URL}")
	private String EXOTEL_CALL_URL;

	@Value("${EXOTEL_APP_FLOW_URL}")
	private String EXOTEL_APP_FLOW_URL;

	@Value("${EXCEL_FILE_PATH}")
	private String EXCEL_FILE_PATH;

	@Value("${EXOTEL_CALL_STATUS_URL}")
	private String EXOTEL_CALL_STATUS_URL;

	@Value("${TS_Exotel_No}")
	private String ts_exotel_no;

	@Value("${TN_Exotel_No}")
	private String tn_exotel_no;

	@Value("${GJ_Exotel_No}")
	private String gj_exotel_no;

	@Value("${KA_Exotel_No}")
	private String ka_exotel_no;

	@Autowired
	private Utility utility;

	private static final Logger logger = LoggerFactory.getLogger(PhonePeServcies.class);

	public Boolean startIvrCampaignLists(List<CampaignPojo> campList) throws InterruptedException, IOException {
		// TODO Auto-generated method stub
		String appFlowId = null;
		CampaignPojo campojo = null;
		String exotelNo = null;
		int count = 0;
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("IVR_CAMP_DATA");
		Row row = sheet.createRow(0);
		CellStyle style = workbook.createCellStyle();
		XSSFFont font = workbook.createFont();
		font.setBold(true);
		font.setFontHeight(20);
		style.setFont(font);
		style.setAlignment(HorizontalAlignment.CENTER);
		createCell(row, 0, "Campaign Information", style);
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 4));
		font.setFontHeightInPoints((short) (10));
		row = sheet.createRow(1);
		font.setBold(true);
		font.setFontHeight(16);
		style.setFont(font);
		createCell(row, 0, "CamId", style);
		createCell(row, 1, "Sid", style);
		createCell(row, 2, "Date", style);
		createCell(row, 3, "Status", style);
		int rowCount = 2;
		CellStyle style2 = workbook.createCellStyle();
		XSSFFont font2 = workbook.createFont();
		font.setFontHeight(14);
		style.setFont(font2);
		logger.info("...............................IVR Campaign started............................");
		FileOutputStream out = new FileOutputStream(new File(EXCEL_FILE_PATH));
		for (CampaignPojo campaignPojo : campList) {
			long campaign = utility.getCampaign(campaignPojo.getDUE_DATE());
			if (campaignPojo.getMOBILE_NUMBER().length() == 10) {
				if (campaignPojo.getSTATE().equalsIgnoreCase("TN")) {
					exotelNo = tn_exotel_no;
					if (campaign == 5) {
						appFlowId = "629872";
					} else if (campaign == 10) {
						appFlowId = "629870";
					} else if (campaign == 15) {
						appFlowId = "629867";
					} else if (campaign == 20) {
						appFlowId = "629865";
					} else if (campaign == 25) {
						appFlowId = "629864";
					}
				}
				if (campaignPojo.getSTATE().equalsIgnoreCase("TS")) {
					exotelNo = ts_exotel_no;
					if (campaign == 5) {
						appFlowId = "629887";
					} else if (campaign == 10) {
						appFlowId = "629885";
					} else if (campaign == 15) {
						appFlowId = "629884";
					} else if (campaign == 20) {
						appFlowId = "629883";
					} else if (campaign == 25) {
						appFlowId = "629882";
					}

				}
				if (campaignPojo.getSTATE().equalsIgnoreCase("GJ")) {
					exotelNo = gj_exotel_no;
					if (campaign == 5) {
						appFlowId = "629844";
					} else if (campaign == 10) {
						appFlowId = "629843";
					} else if (campaign == 15) {
						appFlowId = "629842";
					} else if (campaign == 20) {
						appFlowId = "629841";
					} else if (campaign == 25) {
						appFlowId = "629836";
					}
				}
				if (campaignPojo.getSTATE().equalsIgnoreCase("KA")) {
					exotelNo = ka_exotel_no;
					if (campaign == 5) {
						appFlowId = "629850";
					} else if (campaign == 10) {
						appFlowId = "629849";
					} else if (campaign == 15) {
						appFlowId = "629848";
					} else if (campaign == 20) {
						appFlowId = "629847";
					} else if (campaign == 25) {
						appFlowId = "629845";
					}
				}
				campojo = IvrCallRoAp(campaignPojo, exotelNo, appFlowId);
				if (campojo != null) {
					Row row2 = sheet.createRow(rowCount++);
					int columnCount = 0;
					createCell(row2, columnCount++, campojo.getID(), style2);
					createCell(row2, columnCount++, campojo.getCAMPAIGN_SID(), style2);
					createCell(row2, columnCount++, campojo.getDUE_DATE(), style2);
					createCell(row2, columnCount++, campojo.getSTATUS(), style2);
				}
				count++;
				Thread.sleep(700);
				logger.info("count:: " + count + " :: " + campojo.getID() + " :: " + campojo.getSTATUS());
			}
		}
		workbook.write(out);
		workbook.close();
		out.close();
		logger.info("...............................IVR Campaign Ended............................");
		return true;
	}

	private CampaignPojo IvrCallRoAp(CampaignPojo campaignPojo, String exoteNo, String appFlowId) {
		// TODO Auto-generated method stub
		org.json.simple.JSONObject respobj;
		org.json.simple.JSONObject respobj2;
		HttpHeaders headers = new HttpHeaders();
		headers.setBasicAuth(EXOTEL_USER_NAME, EXOTEL_PASSWORD);
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);
		String appFlowUrl = EXOTEL_APP_FLOW_URL + appFlowId;
		LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
		map.add("From", campaignPojo.getMOBILE_NUMBER());
		map.add("CallerId", exoteNo);
		map.add("Url", appFlowUrl);
		HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = new HttpEntity<>(map, headers);
		try {
			String results = new RestTemplate().postForObject(EXOTEL_CALL_URL, requestEntity, String.class);
			if (!results.isEmpty()) {
				respobj = utility.jsonParsing(results);
				respobj2 = (org.json.simple.JSONObject) respobj.get("Call");
				campaignPojo.setCAMPAIGN_SID((String) respobj2.get("Sid"));
				campaignPojo.setDUE_DATE((String) respobj2.get("DateCreated"));
				campaignPojo.setSTATUS((String) respobj2.get("Status"));
			} else {
				campaignPojo.setCAMPAIGN_SID("NA");
				campaignPojo.setDUE_DATE("NA");
				campaignPojo.setSTATUS("ERROR");
			}
		} catch (Exception e) {
			logger.info("AP Connect Call API Error:::" + campaignPojo.getID() + " :: " + e.getMessage());
		}
		return campaignPojo;
	}

	private void createCell(Row row, int columnCount, Object value, CellStyle style) {
		Cell cell = row.createCell(columnCount);
		if (value instanceof Long) {
			cell.setCellValue((Long) value);
		} else if (value instanceof Integer) {
			cell.setCellValue((Integer) value);
		} else if (value instanceof Boolean) {
			cell.setCellValue((Boolean) value);
		} else {
			cell.setCellValue((String) value);
		}
		cell.setCellStyle(style);
	}

	// ::update data into googlesheet api code starts here:://
	public Boolean updateCampaignDataIntoGoogleSheet()
			throws IOException, InterruptedException, CustomFileNotFoundException {
		CampaignPojo campojo;
		File renamedfilePath = new File("D:/ROAP_IVR/ROAP_IVR" + utility.getDate() + ".xlsx");
		boolean status = false;
		int count = 1;
		try {
			File file = new File(EXCEL_FILE_PATH);
			if (!file.exists()) {
				throw new CustomFileNotFoundException("File Not Found");
			} else {
				FileInputStream fis = new FileInputStream(file);
				XSSFWorkbook book = new XSSFWorkbook(fis);
				XSSFSheet sheet = book.getSheetAt(0);
				int rowCount = (sheet.getLastRowNum()) - (sheet.getFirstRowNum());
				logger.info("........................ROAP Exotel Call Status API Started........................");
				for (int i = 2; i <= rowCount; i++) {
					int cellcount = (sheet.getRow(i).getLastCellNum()) - 2;
					for (int j = 1; j < cellcount; j++) {
						String id = sheet.getRow(i).getCell(0).getStringCellValue().toString();
						String sid = sheet.getRow(i).getCell(1).getStringCellValue().toString();
						campojo = new CampaignPojo();
						campojo.setID(id);
						campojo.setCAMPAIGN_SID(sid);
						status = exotelcallstatusApis(campojo, count);
						count++;
					}
				}
				logger.info("........................ROAP Exotel Call Status API Ended........................");
				book.close();
				boolean reNamedFile = file.renameTo(renamedfilePath);
				fis.close();
				logger.info("renamedFile status::  " + reNamedFile);
			}
		} catch (Exception ex) {
			logger.info("Exception:: " + ex.getMessage());
		}
		return status;
	}

	private Boolean exotelcallstatusApis(CampaignPojo campojo, int j) throws InterruptedException {
		org.json.simple.JSONObject respobj;
		org.json.simple.JSONObject respobj2;
		HttpHeaders headers = new HttpHeaders();
		headers.setBasicAuth(EXOTEL_USER_NAME, EXOTEL_PASSWORD);
		HttpEntity<String> entity = new HttpEntity<String>(headers);
		Map<String, String> map = null;
		boolean callStatus = false;
		try {
			ResponseEntity<String> result = new RestTemplate().exchange(
					EXOTEL_CALL_STATUS_URL + campojo.getCAMPAIGN_SID() + ".json", HttpMethod.GET, entity, String.class);
			String results = result.getBody();
			if (!results.isEmpty()) {
				respobj = utility.jsonParsing(results);
				respobj2 = (org.json.simple.JSONObject) respobj.get("Call");
				String status = ((String) respobj2.get("Status"));
				String sid = ((String) respobj2.get("Sid"));
				String dateStarted = ((String) respobj2.get("DateCreated"));
				String startTime = ((String) respobj2.get("StartTime"));
				String endTime = ((String) respobj2.get("EndTime"));
				String from = ((String) respobj2.get("From"));
				String to = ((String) respobj2.get("To"));
				String phSid = ((String) respobj2.get("PhoneNumberSid"));
				Object duration = ((Object) respobj2.get("Duration"));
				Object price = (Object) respobj2.get("Price");
				if (price == null) {
					price = 0.0;
				}
				map = new HashMap<String, String>();
				map.put("id", campojo.getID());
				map.put("sid", sid);
				map.put("status", status);
				map.put("dateStarted", dateStarted);
				map.put("startTime", startTime);
				map.put("endTime", endTime);
				map.put("from", from);
				map.put("to", to);
				map.put("phSid", phSid);
				map.put("duration", duration.toString());
				map.put("price", price.toString());
				logger.info("ROAP Connect Call API Success:::" + status + "::" + campojo.getID() + "  :: " + j);
				callStatus = updateExotelCallApiIntoAppSheet(map);
			}

		} catch (Exception e) {
			logger.info("ROAP Connect Call API Error:::" + e.getMessage() + "::" + campojo.getID() + " :: " + j);
		}
		return callStatus;
	}

	private Boolean updateExotelCallApiIntoAppSheet(Map<String, String> callData) {
		String url = ROAP_CAMPAIGN_UPDATE_GOOGLESHEET_URL;
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		boolean dataUpdateIntoGoogleSheetStatus = false;
		MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
		map.add("id", callData.get("id"));
		map.add("campaign_sid", callData.get("sid"));
		map.add("status", callData.get("status"));
		map.add("date_created", callData.get("dateStarted"));
		map.add("date_started", callData.get("startTime"));
		map.add("date_updated", callData.get("endTime"));
		map.add("from", callData.get("from"));
		map.add("to", callData.get("to"));
		map.add("phSid", callData.get("phSid"));
		map.add("duration", callData.get("duration"));
		map.add("price", callData.get("price"));
		map.add("method", "IvrCallStatusApi");
		try {
			HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<MultiValueMap<String, String>>(map,
					headers);
			ResponseEntity<String> restTemplate = new RestTemplate().exchange(url, HttpMethod.POST, entity,
					String.class);
			logger.info(callData.get("id") + "  ::  Success  ::  " + "  ::  " + restTemplate.getStatusCode());
			if (restTemplate.getStatusCode().toString().equalsIgnoreCase("302 FOUND")) {
				dataUpdateIntoGoogleSheetStatus = true;
			}

		} catch (Exception e) {
			logger.info("Update into googlesheet status id::  " + callData.get("id") + " ::: " + e.getMessage());
		}
		return dataUpdateIntoGoogleSheetStatus;
	}
}
