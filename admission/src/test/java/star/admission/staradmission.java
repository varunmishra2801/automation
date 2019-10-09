package star.admission;


import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

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
		 if(!id.equals(null) && !id.equals("")) {
			 requestspc = RestAssured.given();
			  requestspc.header("Content-Type", "multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW");
			  requestspc.multiPart("status", "In Progress");
			  requestspc.multiPart("refno", patientid); 
			  requestspc.multiPart("process", "Pa");
			  requestspc.multiPart("claimid", id);
			  log(patientid+"-In Progress");
			  response1 = requestspc.post(url);	}
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
	public void newAdmission(String patientId, String process,String strDate, String stime) {
		try {
			String memberId = value.get("insid").asText();
			String patientName = value.get("PatientName").asText();
			String patientmobile = value.get("PatientContact").asText();
			JsonNode docs = value.get("Docs");
			saveFiles(docs, patientId);
			String monthsArray[] = { null, "January", "February", "March", "April", "May", "June", "July", "August",
					"September", "October", "November", "December" };
			String admissionDate = value.get("admission_date").asText(); //admission date
			String dischargedate = value.get("dischargedate").asText(); //discharge date
			String year = admissionDate.substring(admissionDate.lastIndexOf("/") + 1);
			String month = admissionDate.substring(admissionDate.indexOf("/") + 1, admissionDate.lastIndexOf("/"));
			String day = admissionDate.substring(0, admissionDate.indexOf("/"));
			String treating_doctor = value.get("treating_doctor").asText();
			String diagnosis_type = value.get("diagnosis_type").asText();
			String cataract_type = value.get("cataract_type").asText();
			String delivery_type = value.get("delivery_type").asText();
			String other_diagnosis = value.get("other_diagnosis").asText();
			String maternity_G = value.get("maternity_G").asText();
			String maternity_L = value.get("maternity_L").asText();
			String maternity_P = value.get("maternity_P").asText();
			String maternity_A = value.get("maternity_A").asText();
			String cataract_procedure = value.get("cataract_procedure").asText();
			String lens_type = value.get("lens_type").asText();
			String room_type = value.get("room_type").asText();
			String package_details = value.get("package_details").asText();
            String Total_Expected_cost = value.get("Total_expected_cost").asText();
			
			month = monthsArray[Integer.parseInt(month)];
			
			ele = By.xpath("/html/body/div[1]/section/header/div[4]/a[1]");
			wait.until(ExpectedConditions.presenceOfElementLocated(ele));
			// Click on New Admission Button
			element = driver.findElement(By.xpath("/html/body/div[1]/section/header/div[4]/a[1]"));
			element.click();

			// Wait for the Member ID RadioButton
			ele = By.xpath("//span[text()=' Member Id ']");
			wait.until(ExpectedConditions.presenceOfElementLocated(ele));

			// Click on member ID radio Button
			element = driver.findElement(By.xpath("//span[text()=' Member Id ']"));
			element.click();

			// Find element of member id field
			ele = By.xpath("//input[@placeholder ='Enter MemberId']");
			wait.until(ExpectedConditions.presenceOfElementLocated(ele));

			// Fill the field with the member id
			element = driver.findElement(By.xpath("//input[@placeholder ='Enter MemberId']"));
			element.sendKeys(memberId);

			// Fill it with the patient id
			element = driver.findElement(By.xpath("//input[@placeholder ='Enter patient name']"));
			element.sendKeys(patientName);

			// Click the button to enter the patient details
			ele = By.xpath("//*[@id=\'tabclaim-new\']/div[5]/form/div/div[3]/button");
			wait.until(ExpectedConditions.presenceOfElementLocated(ele));

			element = driver.findElement(By.xpath("//*[@id=\'tabclaim-new\']/div[5]/form/div/div[3]/button"));
			element.click();

			// Click the patient table to enter data
			ele = By.xpath("//*[@id=\'tabclaim-new\']/div[8]/div/div/div/div");
			wait.until(ExpectedConditions.presenceOfElementLocated(ele));

			element = driver.findElement(By.xpath("//*[@id=\'tabclaim-new\']/div[8]/div/div/div/div"));
			element.click();

			ele = By.xpath("//*[@id=\'myForm\']/div[2]/div[1]/div/div/span/button");
			wait.until(ExpectedConditions.presenceOfElementLocated(ele));

			element = driver.findElement(By.xpath("//*[@id=\'myForm\']/div[2]/div[1]/div/div/span/button"));
			element.click();
            //mobile
			ele = By.xpath("//input[@name='contactNum']");
			wait.until(ExpectedConditions.presenceOfElementLocated(ele));
			
        	element = driver.findElement(By.xpath("//input[@name='contactNum']"));
        	element.sendKeys(patientmobile);
						
			
			// This is datepicker area //
			element = driver.findElement(
					By.xpath("//*[@id=\'myForm\']/div[2]/div[1]/div/div/div/ul/li/div/table/thead/tr[1]/th[2]"));
			element.click();

			element = driver.findElement(
					By.xpath("//*[@id=\'myForm\']/div[2]/div[1]/div/div/div/ul/li/div/table/thead/tr/th[2]"));
			element.click();

			WebElement YearPicker = driver
					.findElement(By.xpath("//*[@id=\'myForm\']/div[2]/div[1]/div/div/div/ul/li/div/table/tbody"));

			List<WebElement> yearcoloumns = YearPicker.findElements(By.tagName("td"));

			for (WebElement cell : yearcoloumns) {
				if (cell.getText().equals(year)) {
					cell.click();
					break;
				}
			}

			WebElement MonthPicker = driver
					.findElement(By.xpath("//*[@id=\'myForm\']/div[2]/div[1]/div/div/div/ul/li/div/table/tbody"));

			List<WebElement> monthcoloumns = MonthPicker.findElements(By.tagName("td"));

			for (WebElement cell : monthcoloumns) {

				if (cell.getText().contains(month)) {
					cell.click();
					break;
				}
			}

			// This is from date picker table

			WebElement dateWidgetFrom = driver
					.findElement(By.xpath("//*[@id=\'myForm\']/div[2]/div[1]/div/div/div/ul/li/div/table/tbody"));

			// This are the rows of the from date picker table List<WebElement> rows =
			dateWidgetFrom.findElements(By.tagName("tr"));

			List<WebElement> columns = dateWidgetFrom.findElements(By.tagName("td"));

			for (WebElement cell : columns) {
				if (cell.getText().equals(day)) {
					cell.click();
					break;
				}
			}
//discharge date			
			year = dischargedate.substring(dischargedate.lastIndexOf("/") + 1);
			month = dischargedate.substring(dischargedate.indexOf("/") + 1, dischargedate.lastIndexOf("/"));
		    day = dischargedate.substring(0, dischargedate.indexOf("/"));
			
			element = driver.findElement(By.xpath("//*[@id=\'myForm\']/div[2]/div[2]/div/div/span/button"));
			element.click();
			
			element = driver.findElement(By.xpath("//*[@id=\'myForm\']/div[2]/div[2]/div/div/div/ul/li/div/table/thead/tr[1]/th[2]"));
			element.click();
			
			element = driver.findElement(By.xpath("//*[@id=\'myForm\']/div[2]/div[2]/div/div/div/ul/li/div/table/thead/tr/th[2]"));
			element.click();
			
			WebElement DYearPicker = driver
					.findElement(By.xpath("//*[@id=\'myForm\']/div[2]/div[2]/div/div/div/ul/li/div/table/tbody"));

			
			List<WebElement> dyearcoloumns = DYearPicker.findElements(By.tagName("td"));

			for (WebElement cell : dyearcoloumns) {
				if (cell.getText().equals(year)) {
					cell.click();
					break;
				}
			}

			WebElement DMonthPicker = driver
					.findElement(By.xpath("//*[@id=\'myForm\']/div[2]/div[2]/div/div/div/ul/li/div/table/tbody"));

			List<WebElement> dmonthcoloumns = DMonthPicker.findElements(By.tagName("td"));
			month = monthsArray[Integer.parseInt(month)];
			for (WebElement cell : dmonthcoloumns) {

				if (cell.getText().contains(month)) {
					cell.click();
					break;
				}
			}

			// This is from date picker table

			WebElement DdateWidgetFrom = driver
					.findElement(By.xpath("//*[@id=\'myForm\']/div[2]/div[2]/div/div/div/ul/li/div/table/tbody"));

			// This are the rows of the from date picker table List<WebElement> rows =
			DdateWidgetFrom.findElements(By.tagName("tr"));

			List<WebElement> dcolumns = DdateWidgetFrom.findElements(By.tagName("td"));

			for (WebElement cell : dcolumns) {
				if (cell.getText().equals(day)) {
					cell.click();
					break;
				}
			}
						
// doctor name			
            element = driver.findElement(By.xpath("//input[@name='docName']"));
            element.sendKeys(treating_doctor);
            if(diagnosis_type.equals("OTHER")) {	    
            	ele = By.xpath("//input[@value='OTHER']");
    			wait.until(ExpectedConditions.presenceOfElementLocated(ele));

    			// Click on OTHER radio Button
    			element = driver.findElement(By.xpath("//input[@value='OTHER']"));
    			element.click();
            	element = driver.findElement(By.xpath("//textarea[@name='diagnosis']"));
            	element.sendKeys(other_diagnosis);
            }
            try {
            if(diagnosis_type.equals("CATARACT")) {	   
            	ele = By.xpath("//input[@value='CATARACT']");
    			wait.until(ExpectedConditions.presenceOfElementLocated(ele));
    			
            	element = driver.findElement(By.xpath("//input[@value='CATARACT']"));
            	element.click();
            	if(cataract_type.equals("Right Eye")) { //right eye
            		ele = By.xpath("//input[@value='8933']");
        			wait.until(ExpectedConditions.presenceOfElementLocated(ele));

                	element = driver.findElement(By.xpath("//input[@value='8933']"));
                	element.click();
            	}
            	if(cataract_type.equals("Left Eye")) { //left eye
            		ele = By.xpath("//input[@value='8934']");
        			wait.until(ExpectedConditions.presenceOfElementLocated(ele));

                	element = driver.findElement(By.xpath("//input[@value='8934']"));
                	element.click();
            	}
            	ele = By.xpath("//select[@class='form-control ng-touched ng-dirty ng-valid-parse ng-valid ng-valid-required user-success']");
    			wait.until(ExpectedConditions.presenceOfElementLocated(ele));

            	element = driver.findElement(By.xpath("//select[@class='form-control ng-touched ng-dirty ng-valid-parse ng-valid ng-valid-required user-success']"));
            	element.sendKeys(cataract_procedure);
            	
            	element = driver.findElement(By.xpath("//select[@class='form-control ng-pristine ng-invalid ng-invalid-required ng-touched user-error']"));
            	element.sendKeys(lens_type);
            	
            	
            }
            if(diagnosis_type.equals("MATERNITY")) {	
            	ele = By.xpath("//input[@value='MATERNITY']");
    			wait.until(ExpectedConditions.presenceOfElementLocated(ele));

            	element = driver.findElement(By.xpath("//input[@value='MATERNITY']"));
            	element.click();
            	
            	if(delivery_type.equals("Normal")) { //normal
            		ele = By.xpath("//input[@value='4455']");
        			wait.until(ExpectedConditions.presenceOfElementLocated(ele));

                	element = driver.findElement(By.xpath("//input[@value='4455']"));
                	element.click();
            	}
            	if(delivery_type.equals("LSCS")) { //LSCS
            		ele = By.xpath("//input[@value='3240']");
        			wait.until(ExpectedConditions.presenceOfElementLocated(ele));

                	element = driver.findElement(By.xpath("//input[@value='3240']"));
                	element.click();
            	}
            	
            	//if(delivery_type == "") { //G
            		ele = By.xpath("//input[@ng-model='form.obstetricHistoryG']");
        			wait.until(ExpectedConditions.presenceOfElementLocated(ele));

                	element = driver.findElement(By.xpath("//input[@ng-model='form.obstetricHistoryG']"));
                	element.sendKeys(maternity_G);
            	//}
            	//if(delivery_type == "") { //P
            		ele = By.xpath("//input[@ng-model='form.obstetricHistoryP']");
        			wait.until(ExpectedConditions.presenceOfElementLocated(ele));

                	element = driver.findElement(By.xpath("//input[@ng-model='form.obstetricHistoryP']"));
                	element.sendKeys(maternity_P);
            	//}
            	//if(delivery_type == "") { //L
            		ele = By.xpath("//input[@ng-model='form.obstetricHistoryL']");
        			wait.until(ExpectedConditions.presenceOfElementLocated(ele));

                	element = driver.findElement(By.xpath("//input[@ng-model='form.obstetricHistoryL']"));
                	element.sendKeys(maternity_L);
            	//}
            	//if(delivery_type == "") { //A
            		ele = By.xpath("//input[@ng-model='form.obstetricHistoryA']");
        			wait.until(ExpectedConditions.presenceOfElementLocated(ele));

                	element = driver.findElement(By.xpath("//input[@ng-model='form.obstetricHistoryA']"));
                	element.sendKeys(maternity_A);
            	//}
            }
            }catch(Exception e) {
            	log("issue in filling diagnosis type");
            }
            if(room_type.equals("DELUXE ROOM")) {
            	room_type = "DELUXE AC ROOM";
            }
            element = driver.findElement(By.xpath("//input[@placeholder='Type provided room type']"));
        	element.sendKeys(room_type);
            if(package_details.equals("Open") || package_details.equals("0")) {            	
            	element = driver.findElement(By.xpath("//input[@placeholder='Start typing procedure name...']"));
            	element.sendKeys("Open Billing");
            	element = driver.findElement(By.xpath("//input[@placeholder='Enter amount']"));
            	element.sendKeys(Total_Expected_cost);
            	
            	element = driver.findElement(By.xpath("//button[@ng-click='addManualPackage(form.name, form.amount)']"));
            	element.click();
            }
                   	
			element = driver.findElement(By.id("uploadFileInput"));

			for (int i = 0; i < numberOfFiles; i++) {
				String url = docs.get(i).asText();
				String filename = url.substring(url.lastIndexOf('/')+1,url.lastIndexOf('.'));
				element.sendKeys("C:\\Users\\" + System.getProperty("user.name") + "\\Documents\\" +patientId+"\\"+filename+i+".pdf");
			}
			
//update our API
			
			
		} catch (Exception e) {
			log("MB New Admission script failed- "+patientId);
		}
	}

	public void login(String username, String password) {
		try {
			// Enter email or Username
			element = driver.findElement(By.xpath("//input[@placeholder='Username or Email']"));

			element.sendKeys(username);

			// Enter password
			element = driver.findElement(By.xpath("//input[@placeholder='Password']"));
			element.sendKeys(password);

			// Click signin
			element = driver.findElement(By.xpath("//button[text()='Sign In']"));
			element.click();

			// Wait to login and wait unitl newadmission buttton is found
			try {
				element = driver.findElement(By.xpath("//*[@id=\'ngdialog1\']/div[2]/button"));
				element.click();

			} catch (Exception e) {
				log("MB first screen issue");
			}
			ele = By.xpath("/html/body/div[1]/aside/ul/li[8]/a");
			wait.until(ExpectedConditions.presenceOfElementLocated(ele));
		} catch (Exception e) {
			log("MB login script failed");
		}
	}
	public void fhplNewAdmissison(String patientid, String process, String strDate, String stime) {
		try {
			String admissionDate = value.get("admission_date").asText();
			String PatientContact = value.get("PatientContact").asText();
			String line_treatment; line_treatment = "";
			String patientName = value.get("PatientName").asText();
			String patientContact = value.get("PatientContact").asText();
			String admissionTime = value.get("admission_time").asText();
			String provisional_diagnosis = value.get("provisional_diagnosis").asText();
			String line_treatment1 = value.get("line_treatment1").asText();
			String line_treatment2 = value.get("line_treatment2").asText();
			String line_treatment3 = value.get("line_treatment3").asText();
			String line_treatment4 = value.get("line_treatment4").asText();
			String line_treatment5 = value.get("line_treatment5").asText();
			String route_drug = value.get("route_drug").asText();
			String Total_expected_cost = value.get("Total_expected_cost").asText();
			String PatientID_TreatmentID = value.get("PatientID_TreatmentID").asText();
			String hospital_phone = value.get("hospital_phone").asText();
			String admission_type = value.get("admission_type").asText();
			String insname = value.get("insname").asText();
			String insid = value.get("insid").asText();
			String insurer_tpa = value.get("insurer_tpa").asText();
			String treating_doctor = value.get("treating_doctor").asText();		
			String stay_hospital = value.get("stay_hospital").asText();
			String room_type = value.get("room_type").asText();
			String present_ailment_day = value.get("present_ailment_day").asText();
			String RelativeRelation = value.get("RelativeRelation").asText();
			String type_anesthesia = value.get("type_anesthesia").asText();
			String if_investigation = value.get("if_investigation").asText();
			String illness_name = value.get("illness_name").asText();
			String Diabetes = value.get("Diabetes").asText();
			String Hypertension = value.get("Hypertension").asText();
			String room_rent = value.get("room_rent").asText();
			String professional_fees = value.get("professional_fees").asText();
			
			JsonNode docs = value.get("Docs");
			saveFiles(docs,patientid);
			element = driver.findElement(By.xpath("//*[@id=\'mnuMain\']/ul/li[1]/a"));
			element.click();

			ele = By.name("ctl00$ContentPlaceHolder1$TabContainer1$tbPatientDetails$ddlInsuranceCompany");
			wait.until(ExpectedConditions.presenceOfElementLocated(ele));
          
			element = driver.findElement(
					By.name("ctl00$ContentPlaceHolder1$TabContainer1$tbPatientDetails$ddlInsuranceCompany"));
			element.sendKeys(insurer_tpa); // Insurance Company "Iffco Tokio General Insurance Co. Ltd" replaced with insname

			element = driver.findElement(By.name("ctl00$ContentPlaceHolder1$TabContainer1$tbPatientDetails$txtUHIDNo"));
			element.sendKeys(insid); // UHID NO replaced with insid

			element = driver
					.findElement(By.xpath("//*[@id=\'ContentPlaceHolder1_TabContainer1_tbPatientDetails_btnGo\']"));
			element.click();

			element = driver.findElement(
					By.xpath("//*[@id=\'ContentPlaceHolder1_TabContainer1_tbPatientDetails_txtPatientMobileNo\']"));
			element.sendKeys(PatientContact); // patient contact number "9959721018" replaced with PatientContact

			element = driver.findElement(
					By.xpath("//*[@id=\'ContentPlaceHolder1_TabContainer1_tbPatientDetails_btnPatientDetailsNext\']"));
			element.click();

			element = driver.findElement(By.xpath(
					"//*[@id=\'ContentPlaceHolder1_TabContainer1_tbHospitalizationDetails_txtHospitalDoctorName\']"));
			element.sendKeys(treating_doctor); // Treating Doctor Name "ABC" replaced with "treating_doctor"

			element = driver.findElement(By.xpath(
					"//*[@id=\'ContentPlaceHolder1_TabContainer1_tbHospitalizationDetails_txtDateOfAdmission\']"));
			element.sendKeys(admissionDate);

			element = driver.findElement(By.xpath(
					"//*[@id=\'ContentPlaceHolder1_TabContainer1_tbHospitalizationDetails_ddlClassOfAccomodation\']"));
			element.sendKeys(room_type); // Class of Accomodation "DELUXE" replaced with room_type

			element = driver.findElement(By
					.xpath("//*[@id=\'ContentPlaceHolder1_TabContainer1_tbHospitalizationDetails_txtEstimatedDays\']"));
			element.sendKeys(stay_hospital); // Hardcode Estimated Days  "missing in APIs"

			element = driver.findElement(By.xpath(
					"//*[@id=\'ContentPlaceHolder1_TabContainer1_tbHospitalizationDetails_txtDurationOfAilment\']"));
			element.sendKeys(present_ailment_day); //  Duration Of Ailment "3" replaced with present_ailment_day

			element = driver.findElement(By
					.xpath("//*[@id=\'ContentPlaceHolder1_TabContainer1_tbHospitalizationDetails_ddlAdmissionType\']"));
			element.sendKeys(admission_type); //  Admission Type "Emergency" replaced with admission_type
         if(!line_treatment1.equals(""))
        	 line_treatment = line_treatment1;
         if(!line_treatment2.equals(""))
        	 line_treatment = line_treatment2;
         if(!line_treatment3.equals(""))
        	 line_treatment = line_treatment3;
         if(!line_treatment4.equals(""))
        	 line_treatment = line_treatment4;
         if(!line_treatment5.equals(""))
        	 line_treatment = line_treatment5;
			element = driver.findElement(By.xpath(
					"//*[@id=\'ContentPlaceHolder1_TabContainer1_tbHospitalizationDetails_ddlProposedLineOfTreatment\']"));
			element.sendKeys(line_treatment); // Hardcode ProposedLineOfTreatment

			element = driver.findElement(By.xpath(
					"//*[@id=\'ContentPlaceHolder1_TabContainer1_tbHospitalizationDetails_ddlTypeOfAnesthesia\']"));
			element.sendKeys(type_anesthesia); // Hardcode TypeOfAnesthesia

			element = driver.findElement(By.xpath(
					"//*[@id=\'ContentPlaceHolder1_TabContainer1_tbHospitalizationDetails_txtPlainOfTreatment\']"));
			element.sendKeys(if_investigation); // Hardcode PlainOfTreatment

			element = driver.findElement(By.xpath(
					"//*[@id=\'ContentPlaceHolder1_TabContainer1_tbHospitalizationDetails_txtProbableDiagnostics\']"));
			element.sendKeys(provisional_diagnosis); // Hardcode textProbableDiagnostics

			element = driver.findElement(
					By.xpath("//*[@id=\'ContentPlaceHolder1_TabContainer1_tbHospitalizationDetails_txtRouteOfDrug\']"));
			element.sendKeys(route_drug); // Hardcode txtRouteOfDrug

			element = driver.findElement(By.xpath(
					"//*[@id=\'ContentPlaceHolder1_TabContainer1_tbHospitalizationDetails_txtPresentComplaint\']"));
			element.sendKeys(illness_name); // Hardcode txtPresentComplaint

			element = driver.findElement(By.xpath(
					"//*[@id=\'ContentPlaceHolder1_TabContainer1_tbHospitalizationDetails_btnHospitalizationNext\']"));
			element.click();

			element = driver.findElement(
					By.xpath("//*[@id=\'ContentPlaceHolder1_TabContainer1_tbBillingDetails_txtHypertension\']"));
			element.sendKeys(Hypertension); // Hypertension

			element = driver.findElement(
					By.xpath("//*[@id=\'ContentPlaceHolder1_TabContainer1_tbBillingDetails_txtDiabetes\']"));
			element.sendKeys(Diabetes); // Diabetes

			element = driver.findElement(
					By.xpath("//*[@id=\'ContentPlaceHolder1_TabContainer1_tbBillingDetails_txtEstimatedCost\']"));
			element.sendKeys(Total_expected_cost); // Hardcode EstimatedCost "22500" replaced with Total_expected_cost

			element = driver.findElement(
					By.xpath("//*[@id=\'ContentPlaceHolder1_TabContainer1_tbBillingDetails_txtRoomNursingDiat\']"));
			element.sendKeys(room_rent); // Hardcode RoomNursingDiat

			element = driver.findElement(By.xpath(
					"//*[@id=\'ContentPlaceHolder1_TabContainer1_tbBillingDetails_txtProfessionalConsultation\']"));
			element.sendKeys(professional_fees); // Hardcode ProfessionalConsultation

			element = driver.findElement(
					By.xpath("//*[@id=\'ContentPlaceHolder1_TabContainer1_tbBillingDetails_btnBillingDetails\']"));
			element.click();

			element = driver.findElement(By.xpath(
					"//*[@id=\'ContentPlaceHolder1_TabContainer1_tbAddDocuments_MultipleFileUpload1_fuUpload\']"));
			for (int i = 0; i < numberOfFiles; i++) {
				String url = docs.get(i).asText();
				String filename = url.substring(url.lastIndexOf('/')+1,url.lastIndexOf('.'));
				element.sendKeys("C:\\Users\\" + System.getProperty("user.name") + "\\Documents\\" +patientid+"\\"+filename+i+".pdf");
			//	element.sendKeys( "C:\\Users\\" + System.getProperty("user.name") + "\\Documents\\FHPL\\fhpl" + i + ".pdf");

				element = driver.findElement(By.xpath(
						"//*[@id=\'ContentPlaceHolder1_TabContainer1_tbAddDocuments_MultipleFileUpload1_btnAdd\']"));
			}

			element.click();
//update our API
			
			
	} catch (Exception e) {
		log("FHPL New Admission script failed-" + patientid);
		}
	}
	public void fhplLogin(String username, String password) {

		element = driver.findElement(By.xpath("//*[@id=\'txtUserName\']"));
		element.sendKeys(username);

		element = driver.findElement(By.xpath("//*[@id=\'txtPassword\']"));
		element.sendKeys(password);

		element = driver.findElement(By.xpath("//*[@id=\'btnLogIn\']"));
		element.click();

	}
	
	public void preauthicicNewAdmission(String patientid, String process, String strDate, String stime) {
		try {
	    String MemberId = value.get("MemberId").asText();
	    String PatientContact = value.get("PatientContact").asText();
	    String treating_doctor = value.get("treating_doctor").asText();
	    String age = value.get("PatientAge").asText();
	    String quali_ation = value.get("quali_ation").asText();
	    String hospital_phone = value.get("hospital_phone").asText();
	    String room_type = value.get("room_type").asText();
	    String Medicines_Consumables = value.get("Medicines_Consumables").asText();
	    String Total_Consumables = value.get("Total_Consumables").asText();
	    String provisional_diagnosis = value.get("provisional_diagnosis").asText();
	    String stay_hospital = value.get("stay_hospital").asText();
	    String investigation_cost = value.get("investigation_cost").asText();
	    String professional_fees = value.get("professional_fees").asText();
	    String room_rent = value.get("room_rent").asText();
	    String treatment_type = value.get("treatment_type").asText();
	    String procedures = value.get("procedures").asText();
	    String line_treatment1 = value.get("line_treatment1").asText();
	    String ped = "";//value.get("ped").asText();
	    
	    
		JsonNode docs = value.get("Docs");
		saveFiles(docs, patientid);
		ele = By.xpath("//*[@id=\'content\']/ul/li[1]/div[1]/div[1]/span[1]/a");
		wait.until(ExpectedConditions.presenceOfElementLocated(ele));

		element = driver.findElement(By.xpath("//*[@id=\'content\']/ul/li[1]/div[1]/div[1]/span[1]/a"));
		element.click();

		element = driver.findElement(By.xpath("//*[@id=\'hcnUhid\']"));
		element.sendKeys(MemberId); // UHID s

		element = driver.findElement(By.xpath("//*[@id=\'btnSearch\']"));
		element.click();

		ele = By.xpath("//*[@id=\'link-table\']/div[2]/div/table/tbody/tr");
		wait.until(ExpectedConditions.presenceOfElementLocated(ele));

		element = driver.findElement(By.xpath("//*[@id=\'link-table\']/div[2]/div/table/tbody/tr"));
		element.click();

		element = driver.findElement(By.xpath("//*[@id=\'txtInsuredMobileNo\']"));
		element.sendKeys(PatientContact); // Hardccde Mobile Number

		element = driver.findElement(By.xpath("//*[@id=\"landlineno\"]"));
		element.sendKeys("");

		element = driver.findElement(By.xpath("//*[@id=\'txtagepatient\']"));
		element.sendKeys(age); // Hardcode Patient age

		element = driver.findElement(By.xpath("//*[@id=\'txtTreatingDrName\']"));
		element.sendKeys(treating_doctor); // Hardcode Treating Doctor Name

		element = driver.findElement(By.xpath("//*[@id=\'txtQualifications\']"));
		element.sendKeys(quali_ation);

		element = driver.findElement(By.xpath("//*[@id=\'txtMobileNo\']"));
		element.sendKeys(hospital_phone); // HardcodeMobileNo
if(treatment_type.equals("Surgical")) {
	element = driver.findElement(By.xpath("//*[@id=\'Radio_Treatment_Surgical\']"));
	element.click();
	if(procedures.equals("Single")) {
		element = driver.findElement(By.xpath("//*[@id=\'Radio_Procedure_Single\']"));
		element.click();
	}else if(procedures.equals("Multiple")) {
		element = driver.findElement(By.xpath("//*[@id=\'Radio_Procedure_Multiple\']"));
		element.click();
	} 
	element = driver.findElement(By.xpath("//*[@id=\'ddlDisease_Type\']"));
	element.sendKeys(line_treatment1); 
}else if(treatment_type.equals("Medical")) {
		element = driver.findElement(By.xpath("//*[@id=\'Radio_Treatment_Medical\']"));
		element.click();
}
		element = driver.findElement(By.xpath("//*[@id=\'ddlDiagnosis_Type\']"));
		element.sendKeys(provisional_diagnosis); // Provisional Diagnosisi
	    
		element = driver.findElement(By.xpath("//*[@id=\'Radio_PED_NO\']"));
		element.click();
		if(ped.equals("Yes")) {
		element = driver.findElement(By.xpath("//*[@id=\'Radio_PED_YES\']"));
		element.click();
		}	
		

		element = driver.findElement(By.xpath("//*[@id=\'divExpectedDOA\']/p[1]/img"));
		element.click();

		WebElement dateWidgetFrom = driver.findElement(By.xpath("//*[@id=\'ui-datepicker-div\']/table/tbody"));

		List<WebElement> columns = dateWidgetFrom.findElements(By.tagName("td"));
		String admissionDate = value.get("admission_date").asText(); //admission date
		String year = admissionDate.substring(admissionDate.lastIndexOf("/") + 1);
		String month = admissionDate.substring(admissionDate.indexOf("/") + 1, admissionDate.lastIndexOf("/"));
		String day = admissionDate.substring(0, admissionDate.indexOf("/"));//08/09/2019
		if(Integer.parseInt(day)<10) {
			day = admissionDate.substring(1, admissionDate.indexOf("/"));//08/09/2019				
		}
		for (WebElement cell : columns) {

			if (cell.getText().equals(day)) {//8 sept 2019
				cell.click();
				break;
			}
		}

		element = driver.findElement(By.xpath("//*[@id=\'ddlAccommodation\']"));
		element.sendKeys(room_type);

		element = driver.findElement(By.xpath("//*[@id=\'bill_Room_rent_perday\']"));
		element.sendKeys(room_rent);

		element = driver.findElement(By.xpath("//*[@id=\'los\']"));
		element.sendKeys(stay_hospital);

		element = driver.findElement(By.xpath("//*[@id=\'txtTotal_consultation\']"));
		element.sendKeys(professional_fees);

		element = driver.findElement(By.xpath("//*[@id=\'txt_consumables\']"));
		element.sendKeys(Total_Consumables);

		element = driver.findElement(By.xpath("//*[@id=\'txt_Pharmacy\']"));
		element.sendKeys(Medicines_Consumables);

		element = driver.findElement(By.xpath("//*[@id=\'txt_Investigations\']"));
		element.sendKeys(investigation_cost);

		element = driver.findElement(By.xpath("//*[@id=\'btnAdd\']"));
		element.click();

		element = driver.findElement(By.xpath("//*[@id=\'Radio_PREAUTH_YES\']"));
		element.click();

		element = driver.findElement(By.xpath("//*[@id=\'Radio_IDPROOF_YES\']"));
		element.click();

		element = driver.findElement(By.xpath("//*[@id=\'Radio_REPORTS_YES\']"));
		element.click();

		element = driver.findElement(By.xpath("//*[@id=\'ddlDocumentType2\']"));
		element.sendKeys("Pre Authorization Request Note");

		for (int i = 0; i < numberOfFiles; i++) {
			element = driver.findElement(By.xpath("//*[@id=\'btnBrowse2\']"));
			String url = docs.get(i).asText();
			String filename = url.substring(url.lastIndexOf('/')+1,url.lastIndexOf('.'));
			element.sendKeys("C:\\Users\\" + System.getProperty("user.name") + "\\Documents\\" +patientid+"\\"+filename+i+".pdf");
		//	element.sendKeys("C:\\Users\\" + System.getProperty("user.name") + "\\Documents\\PreauthICIC\\preauthicic"+ i + ".pdf");
		}
		element = driver.findElement(By.xpath("//*[@id=\'Comments\']"));
		element.sendKeys("Pre-auth Processed Letter");

		
		}catch(Exception e) {
		}
	}
	public void preauthicicLogin(String username, String password) {
		element = driver.findElement(By.xpath("//*[@id=\'username\']"));
		element.sendKeys(username);

		element = driver.findElement(By.xpath("//*[@id=\'password\']"));
		element.sendKeys(password);

		element = driver.findElement(By.xpath("//*[@id=\'btnLogin\']"));
		element.click();

	}
	
	
}

