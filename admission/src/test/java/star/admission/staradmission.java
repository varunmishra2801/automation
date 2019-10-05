package star.admission;


import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;

public class staradmission {
	WebDriver driver; 
	WebElement element;
	WebElement element1;
	WebElement element2;
	By ele;
	WebDriverWait wait;
    int numberOfFiles;
    JsonNode value;
    String patientid;
	public void starHealthLogin(String username, String password) {
		element = driver.findElement(By.xpath("//*[@id=\'txtLoginID\']"));
		element.sendKeys(username);

		element = driver.findElement(By.xpath("//*[@id=\'txtPassword\']"));
		element.sendKeys(password);

		element = driver.findElement(By.xpath("//*[@id=\'btnLogin\']"));
		element.click();
		
		
	}
@Test
	public void starNewAdmission() {
		
		
		patientid = JOptionPane.showInputDialog("Put patient id");
		
		String url = "https://vnusoftware.com/iclaimportal/api/preauth ";

		RequestSpecification requestspc = RestAssured.given();
		requestspc.header("Content-Type", "multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW");
		requestspc.multiPart("pid", patientid);
		io.restassured.response.Response response1 = requestspc.post(url);
		
		//sele.log("Response body: " + response1.getBody().asString());
		ObjectMapper mapper = new ObjectMapper();

		try {
			JsonNode actualObj = mapper.readTree(response1.asString());
			value = actualObj.get("0");
			normalStart("https://portal.starhealth.in/hospital/");
			JsonNode username = value.get("PortalUser");
			JsonNode password = value.get("PortalPass");
			starHealthLogin(username.asText(), password.asText());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		 requestspc = RestAssured.given();
		  requestspc.header("Content-Type", "multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW");
		  requestspc.multiPart("status", "Acknowledged");
		  requestspc.multiPart("refno", patientid); 
		  requestspc.multiPart("process", "Pa");
		  log(patientid+"- Acknowledged");
		  response1 = requestspc.post(url);	
		
		String line_treatment; line_treatment = "";
		String patientName = value.get("PatientName").asText();
		String patientContact = value.get("PatientContact").asText();
		String admissionDate = value.get("admission_date").asText();
		String admissionTime = value.get("admission_time").asText();
		String provisionalDiagnosis = value.get("provisional_diagnosis").asText();
		String line_treatment1 = value.get("line_treatment1").asText();
		String line_treatment2 = value.get("line_treatment2").asText();
		String address = value.get("Address").asText();
		String Total_expected_cost = value.get("Total_expected_cost").asText();
		String PatientID_TreatmentID = value.get("PatientID_TreatmentID").asText();
		String hospital_phone = value.get("hospital_phone").asText();
		String admission_type = value.get("admission_type").asText();
		if (line_treatment2.equals("Surgical Management"))
		{
			line_treatment = "Surgical";
		}
		if (line_treatment1.equals("Medical Management"))
		{
			if (line_treatment2.equals("Surgical Management")) 
			{
				line_treatment = "Both";
			}else
			{
				line_treatment = "Medical";
			}
		}
		
		String PatientPolicy = value.get("insid").asText();
		JsonNode docs = value.get("Docs");
		saveFiles(docs, patientid);
		try {
			element = driver.findElement(By.xpath("//*[@id=\'ctl00_ContentPlaceHolder1_btnClose\']"));
			element.click();
		}catch(Exception e) {}
			try {
		element = driver.findElement(By.xpath("//*[@id=\'ctl00_ContentPlaceHolder1_btnNewIntimation\']"));
		element.click();
		element = driver.findElement(By.xpath("//*[@id=\'ctl00_ContentPlaceHolder1_txtPolicyNumber\']"));
		element.sendKeys(PatientPolicy); // PolicyNo


		element = driver.findElement(By.xpath("//*[@id=\'ctl00_ContentPlaceHolder1_btnSearch\']"));
		element.click();
try {
		ele = By.xpath("//*[@id=\'ctl00_ContentPlaceHolder1_gvPolicyList\']/tbody/tr[2]/td[10]/a");
		wait.until(ExpectedConditions.presenceOfElementLocated(ele));
}catch(Exception e){
	element = driver.findElement(By.xpath("//*[@id=\'ctl00_ContentPlaceHolder1_txtPolicyNumber\']"));
	element.clear(); // PolicyNo
	element.sendKeys(Keys.CONTROL + "a");
	element.sendKeys(Keys.DELETE);
	element = driver.findElement(By.xpath("//*[@id=\'ctl00_ContentPlaceHolder1_txtHealthCardNo\']"));
	element.sendKeys(PatientPolicy); // HealthCardNo
	element = driver.findElement(By.xpath("//*[@id=\'ctl00_ContentPlaceHolder1_btnSearch\']"));
	element.click();
	
	ele = By.xpath("//*[@id=\'ctl00_ContentPlaceHolder1_gvPolicyList\']/tbody/tr[2]/td[10]/a");
	wait.until(ExpectedConditions.presenceOfElementLocated(ele));
}

		element = driver.findElement(By.xpath("//*[@id='ctl00_ContentPlaceHolder1_gvPolicyList']/tbody/tr[2]/td[10]/a"));
		 Actions actions = new Actions(driver);
		  actions.moveToElement(element).click().build().perform(); 
		
		element = driver.findElement(By.xpath("//*[@id=\'ctl00_ContentPlaceHolder1_cmbPatientName\']"));
		element.sendKeys(patientName);

		element = driver.findElement(By.xpath("//*[@id=\'ctl00_ContentPlaceHolder1_txt_ContactNo\']"));
		element.sendKeys(patientContact); // Patient Contact Number

		element = driver.findElement(By.xpath("//*[@id=\'ctl00_ContentPlaceHolder1_txt_AtdMobileNo\']"));
		element.sendKeys(hospital_phone); // AttendersMobileNo

		element = driver.findElement(By.xpath("//*[@id=\'ctl00_ContentPlaceHolder1_txtM_Address\']"));
		element.sendKeys(address); // Patient Address

		element = driver.findElement(By.xpath("//*[@id=\'ctl00_ContentPlaceHolder1_txtAdmissionNo\']"));
		element.sendKeys(PatientID_TreatmentID);

		element = driver.findElement(By.xpath("//*[@id=\'ctl00_ContentPlaceHolder1_cmbMH_TreatmentPlan\']"));
		element.sendKeys(line_treatment); //TreatmentPlan
try {   
	ele = By.xpath("//input[@name='ctl00$ContentPlaceHolder1$dtAFD_DateofAdmission']");
	wait.until(ExpectedConditions.presenceOfElementLocated(ele));
		element = driver.findElement(By.xpath("//input[@name='ctl00$ContentPlaceHolder1$dtAFD_DateofAdmission']"));
		String[] splited = admissionDate.split(" ");	  
		element.sendKeys(splited[0]);
}catch(Exception e) {}

	//	element = driver.findElement(By.xpath("//*[@id=\'ctl00_ContentPlaceHolder1_txtAFD_TimeofAdmission\']"));
	//	element.sendKeys(admissionTime); // Admission Time null
        if(admission_type.equals("Emergency")) {
		element = driver.findElement(By.xpath("//*[@id=\'ctl00_ContentPlaceHolder1_rbAFD_Emergency\']"));
		element.click();
         }
       if(admission_type.equals("Planned")) {
     	element = driver.findElement(By.xpath("//*[@id=\'ctl00_ContentPlaceHolder1_rbAFD_Planned\']"));
    	element.click();
         }
      if(admission_type.equals("Walk-In")) {
	    element = driver.findElement(By.xpath("//*[@id=\'ctl00_ContentPlaceHolder1_rbAFD_Normal\']"));
    	element.click();
         }
		element = driver.findElement(By.xpath("//*[@id=\'ctl00_ContentPlaceHolder1_txtImgdoctblAmount\']"));
		element.sendKeys(Total_expected_cost); //  Amount
		try {
		element = driver.findElement(By.xpath("//*[@id=\'ctl00_ContentPlaceHolder1_cmbMH_SurgCode\']"));
		element.sendKeys("Others"); //  Others
		Thread.sleep(10000);
		}catch(Exception e) {log("Others field not found on screen");}
		element = driver.findElement(By.xpath("//*[@id=\'ctl00_ContentPlaceHolder1_txtMH_ProvisionalDiag\']"));
		element.sendKeys(Keys.CONTROL + "a");
		element.sendKeys(Keys.DELETE);
		element.sendKeys(provisionalDiagnosis); // Provisional Diagnosis null
		try {
		for (int i = 0; i < numberOfFiles; i++) {
			element = driver.findElement(By.xpath("//*[@id=\'ctl00_ContentPlaceHolder1_UpdFilePreAuth\']"));
		    url = docs.get(i).asText();
			String filename = url.substring(url.lastIndexOf('/')+1,url.lastIndexOf('.'));
			element.sendKeys("C:\\Users\\" + System.getProperty("user.name") + "\\Documents\\" +patientid+"\\"+filename+i+".pdf");
			//element.sendKeys("C:\\Users\\" + System.getProperty("user.name") + "\\Documents\\Star\\star" + i + ".pdf");

		}}catch(Exception e) {log("issue in doc upload");}
		
	
//update api
		 String id = JOptionPane.showInputDialog("Put claim id");
			 requestspc = RestAssured.given();
			  requestspc.header("Content-Type", "multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW");
			  requestspc.multiPart("status", "In Progress");
			  requestspc.multiPart("refno", patientid); 
			  requestspc.multiPart("process", "Pa");
			  requestspc.multiPart("claimid", id);
			  log(patientid+"-In Progress");
			  response1 = requestspc.post(url);	
			}catch(Exception e) {}
}
	
	public void saveFiles(JsonNode docs, String port) {
		numberOfFiles = docs.size();
		File file;
		
			for (int i = 0; i < docs.size(); i++) {
				String url = docs.get(i).asText();
				String filename = url.substring(url.lastIndexOf('/')+1,url.lastIndexOf('.'));
				log(filename);
				file = new File("C:\\Users\\" + System.getProperty("user.name") + "\\Documents\\" + port);
				if (!file.exists()) {
					file.mkdir();
				}
				file = new File("C:\\Users\\" + System.getProperty("user.name") + "\\Documents\\" + port + "\\"
						+ filename + i + ".pdf");
				if (!file.exists()) {
					try {
						BufferedInputStream in = new BufferedInputStream(new URL(docs.get(i).asText()).openStream());
						FileOutputStream fileOutputStream = new FileOutputStream(file);
						byte dataBuffer[] = new byte[1024];
						int bytesRead;
						while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
							fileOutputStream.write(dataBuffer, 0, bytesRead);
						}
						fileOutputStream.close();
					} catch (Exception e) {
						System.out.println(docs.get(i));
						log("issue3");
					}
				}
			}}
	public void log(String log) {
		File file = new File("C:\\Users\\" + System.getProperty("user.name") + "\\Documents\\log.txt");
		FileWriter fw;

		try {
			if (file.exists()) {
				fw = new FileWriter(file, true);
				fw.write(log+System.getProperty("line.separator"));
				fw.close();
			} else {
				fw = new FileWriter(file);
				fw.write(log);
				fw.close();
			}
		} catch (Exception e) {

		}
	}
	public void normalStart(String url) {
		try {
		String os = System.getProperty("os.name").toLowerCase();
			if(os.contains("mac")) {
				//	System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir")+"/chromedriver");
				}else {
			System.setProperty("webdriver.chrome.driver", "C:\\Users\\" + System.getProperty("user.name") + "\\Documents\\chromedriver.exe");
				}driver = new ChromeDriver();
	     	log("Chrome Driver launched in Normal mode");
			driver.get(url);
			driver.manage().window().maximize();
			wait = new WebDriverWait(driver, 20);
		} catch (Exception e) {
			log("Chrome Driver failed to launch in Normal mode");
			driver.quit();
		}
	}
}

