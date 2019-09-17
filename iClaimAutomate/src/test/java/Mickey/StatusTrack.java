package Mickey;

import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;

import org.testng.annotations.BeforeTest;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.swing.JOptionPane;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterTest;

public class StatusTrack {

	String status = "Failed", strDate = "";
	String os;
	private int numberOfFiles;
	String url = "https://vnusoftware.com/iclaimportal/api/preauth";
	
	
		
	@Test 
  public void starHealthStatusTrack() {
		WebDriverWait wait = null;
		WebDriver driver = null;
		WebElement element = null;
		By ele = null;
	    String id = "", m ="", hname ="", hid = "LCBP-2009-00337", fileAddress = "", response ="";
	    String PAuth ="",PApprovedAmount="", PAStatus="";
	    int i, i1, i2; String j, preauthid; List<WebElement>  rows;
	    String hospitalList[] = {"U74900PN2012PTC144088","LCBP-2009-00337","U74999PN2015PTC156635"};
    	int hListLength = hospitalList.length;
    	int b,q = 0;
    	if(System.getProperty("user.name").contains("LAPTOP-MI1R02SA")) {
    		System.setProperty("user.name", "91871");
    	};
	    JsonNode value = null, actualObj = null, preauth = null, claim = null; int data = 0, a = 0;
	    do {	    	
	    	a= a+1;
	    	b=(a-1)%hListLength;
	    	hid = hospitalList[b];
	    	if(b==0 && a>1) { //1 full round of all portals have happened..so, rest time
	    		log("Going to sleep for 10 minutes");
	    		q++;
	    		System.out.println("Going to sleep for 10 minutes at"+q+"time");
	    		try {
	    			Thread.sleep(600000);
	    		} catch (InterruptedException e) {
	    			// TODO Auto-generated catch block
	    			e.printStackTrace();
	    		}
	    	}
	    RequestSpecification requestspc;
		requestspc = RestAssured.given();
		requestspc.header("Content-Type", "multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW");
	//	requestspc.multiPart("sdate", "13/09/2019");
		requestspc.multiPart("status", "In Progress");
		requestspc.multiPart("insid", "I29"); //I29 for Star
		requestspc.multiPart("hid", hid); //need to change after every run
	//	requestspc.multiPart("process", "Cl");
		//U74900PN2012PTC144088 - CCH LCBP-2009-00337 - IH   U74999PN2015PTC156635-RM
		io.restassured.response.Response response1 = requestspc.post(url);
		
		ObjectMapper mapper = new ObjectMapper();

		try {
			actualObj = mapper.readTree(response1.asString());
			if(actualObj != null) {
			preauth = actualObj.get("preauth");
			claim = actualObj.get("Claim");
			if(preauth != null) {
				
			}else {
				log("No preauth exist for STAR for hospital-"+hid+" at time" +java.time.LocalTime.now().toString());
			}
			if(claim != null){
				
			} else {
				log("No claim exist for STAR for hospital-"+hid+" at time" +java.time.LocalTime.now().toString());
				}
			}
			else {
				log("Nothing to track for STAR thread for hospital "+hid+" at time "+java.time.LocalTime.now().toString());
			}
		}catch(Exception e) {
			log("Exception occured in reading results of API for STAR thread for hospital"+hid);
		}
// check if preauth or claim exist for this hospital
		if(preauth != null || claim != null) {
//login	because something from above exist	
//before that, get values of hospital and insurer
			if(preauth != null) {
				value = preauth.get("0");
			}else {
				value = claim.get("0");
			}
			id = value.get("RefNo").asText();
			hname = value.get("Hospital_Name").asText();
			log("Processing of Hospital -"+hname+ " is started for "+a+"time.");
				
//launch in headless mode				
	     	try {		
			if(os.contains("mac")) {
				//	System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir")+"/chromedriver");
				}else {
				System.setProperty("webdriver.chrome.driver", "C:\\Users\\" + System.getProperty("user.name") + "\\Documents\\chromedriver.exe");
				}
			    	ChromeOptions options = new ChromeOptions();
			        options.addArguments("headless");
		        System.out.println(System.getProperty("user.name"));
			    System.out.println(os);
				driver = new ChromeDriver(options);

				driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
				log("Chrome Driver launched in Headless mode");
				driver.get("https://portal.starhealth.in/hospital/");
				driver.manage().window().maximize();
				wait = new WebDriverWait(driver, 20);			
	     	} catch (Exception e) {
	     		e.printStackTrace();
				driver.navigate().refresh();
				driver.manage().window().maximize();
				JOptionPane.showMessageDialog(null, e.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
				log("Chrome Driver failed to launch in Headless mode");
				driver.quit();
			}
	     	
// login with hospital credentials
	     	String username = value.get("PortalUser").asText();
			String password = value.get("PortalPass").asText();
	     	element = driver.findElement(By.xpath("//*[@id=\'txtLoginID\']"));
			element.sendKeys(username);

			element = driver.findElement(By.xpath("//*[@id=\'txtPassword\']"));
			element.sendKeys(password);

			element = driver.findElement(By.xpath("//*[@id=\'btnLogin\']"));
			element.click();	
			 try {
				  element = driver.findElement(By.xpath("//*[@id=\'ctl00_ContentPlaceHolder1_btnClose\']"));
				  element.click();
				  } catch (Exception e) {

				  }
//now, check status of all pre-auth; if found, then call update api with proper logging
			if(preauth != null) {
				data = preauth.get("pa_count").asInt();
				for(i = 0; i < data; i++)
				{ 
					j = Integer.toString(i);
					value = preauth.get(j);
					id = value.get("RefNo").asText();
					preauthid = value.get("Preauth").asText();
					if(preauthid.equals("0")) {
						preauthid = value.get("claimid").asText();
					}
					
					element = driver.findElement(By.xpath("//input[@id='ctl00_ContentPlaceHolder1_txtIntimationNo']"));
					if(i>0) {
					element.sendKeys(Keys.CONTROL + "a");
					element.sendKeys(Keys.DELETE);
					}
					element.sendKeys(preauthid);

					element = driver.findElement(By.xpath("//input[@id='ctl00_ContentPlaceHolder1_btnSearch']"));
					element.click();
					ele = By.xpath("//*[@id='ctl00_ContentPlaceHolder1_dg_DashBoard']/tbody/tr");
					  wait.until(ExpectedConditions.presenceOfElementLocated(ele));
					  
//search result on star will come, compare the status (print the status)
					   rows =driver.findElements(By.xpath("//*[@id='ctl00_ContentPlaceHolder1_dg_DashBoard']/tbody/tr"));
				       System.out.println("No of rows are : " + rows.size());
				       i1 = rows.size();
					  String status = null;String url1 = null;
					  if(i1>0) {
					  status = driver.findElement(By.xpath("//*[@id='ctl00_ContentPlaceHolder1_dg_DashBoard']/tbody/tr["+i1+"]/td[4]")).getText();		  
					  response = driver.findElement(By.xpath("//*[@id='ctl00_ContentPlaceHolder1_dg_DashBoard']/tbody/tr["+i1+"]/td[6]")).getText();		  
					if(!response.equals("")) {	 
					  String[] splited2 = null;
						  String[] splited = response.split(" ");
						  response = splited[0];
						  String year = response.substring(response.lastIndexOf("/") + 1);
						  String month = response.substring(response.indexOf("/") + 1, response.lastIndexOf("/"));
						  SimpleDateFormat inputFormat = new SimpleDateFormat("MMM");
							Calendar cal = Calendar.getInstance();
							try {
								cal.setTime(inputFormat.parse(month));
							} catch (ParseException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							SimpleDateFormat outputFormat = new SimpleDateFormat("MM"); // 01-12
							m = outputFormat.format(cal.getTime());
					//		System.out.println(m);
						  String day = response.substring(0, response.indexOf("/"));
					    		if(splited[2].equals("PM")) {
					    		 splited2 = response.split(" ");
					    			splited2 = splited[1].split(":");
					    			if(!splited2[0].equals("12")) {
					    				int p = Integer.parseInt(splited2[0]);
					    			splited2[0] = Integer.toString(p+12);
					    			}
					    			
					    			
					    		}else {
					    			splited2 = splited[1].split(":");
					    		}
					    		m = day+"/"+m+"/"+year+" "+splited2[0]+":"+splited2[1]+":00";
					  }
					    		log("Response time for "+id+" is "+m);
					  log("Status of" +id+ "is-" +status+" at time- "+java.time.LocalTime.now().toString());
					  requestspc = null; response1 = null;
					  }
					  
			  if(status.equals("APPROVED") || status.equals("QUERY")) {
				  if(status.equals("APPROVED")){
					//traverse to Reports->preauth approved/rejected -> pass preauthid to Intimation no and search->and read approved amount->download letter
					  driver.get("https://portal.starhealth.in/hospital/Transaction/wfrm_HPPreAuthApprRejRpt.aspx");
					  driver.findElement(By.xpath("//input[@name='ctl00$ContentPlaceHolder1$txtIntimationNo']")).sendKeys(preauthid);

					  driver.findElement(By.xpath("//input[@name='ctl00$ContentPlaceHolder1$btnSearch']")).click();
					  String claimno = " ";
					  ele = By.xpath("//table[@id='ctl00_ContentPlaceHolder1_dgPreAuthApprRejList']/tbody/tr[2]/td[2]");
					  wait.until(ExpectedConditions.presenceOfElementLocated(ele));
					  do {
						 try {
					  claimno = driver.findElement(By.xpath("//table[@id='ctl00_ContentPlaceHolder1_dgPreAuthApprRejList']/tbody/tr[2]/td[2]")).getText();  
						 }catch(Exception e) { System.out.println("no such element found");}
						 }while(!claimno.equals(preauthid)); 
					  i1 = 0;
					  rows = driver.findElements(By.xpath("//*[@id='ctl00_ContentPlaceHolder1_dgPreAuthApprRejList']/tbody/tr"));
				      System.out.println("No of Approved rows are : " + rows.size());
				      i1 = rows.size();
				     for(i2 =2; i2<=i1; i2++) {
					  PAuth = driver.findElement(By.xpath("//table[@id='ctl00_ContentPlaceHolder1_dgPreAuthApprRejList']/tbody/tr["+i2+"]/td[7]")).getText();					  
					  PAStatus = driver.findElement(By.xpath("//table[@id='ctl00_ContentPlaceHolder1_dgPreAuthApprRejList']/tbody/tr["+i2+"]/td[9]")).getText();
					  if(PAuth.equals("PREAUTH") && PAStatus.equals("APPROVED")) break;
				      }//while(!PAuth.equals("PREAUTH") && !PAStatus.equals("APPROVED"));
					  PApprovedAmount = driver.findElement(By.xpath("//*[@id='ctl00_ContentPlaceHolder1_dgPreAuthApprRejList']/tbody/tr["+i2+"]/td[8]")).getText();
					  log("Type:-"+PAuth+" Status:-"+PAStatus+" Amount:-"+PApprovedAmount);	  
						
					  element = driver.findElement(By.xpath("//*[@id='ctl00_ContentPlaceHolder1_dgPreAuthApprRejList']/tbody/tr["+i2+"]/td[10]"));
					  Actions actions = new Actions(driver);
					  actions.moveToElement(element).click().build().perform();
				      try {
						  for (String handle : driver.getWindowHandles()) { 
							   // driver.switchTo().window(handle);

							    ArrayList<String> windowTab = new ArrayList<String>(driver.getWindowHandles());

							    driver.switchTo().window(windowTab.get(1));
                              
							    url1 = driver.getCurrentUrl();
							    
                                if(url1.contains("http://claims.starhealth.in:8080/ims/documentToken")) {
                                	driver.switchTo().window(windowTab.get(1)).close();
                                	driver.switchTo().window(windowTab.get(0));
                                	driver.get(url1);
                                	element = driver.findElement(By.xpath("//iframe[@src]"));
                                    fileAddress = element.getAttribute("src");
                                	log(fileAddress);
                                	driver.get("https://portal.starhealth.in/hospital/Transaction/wfrm_HPDashBoard_Latests.aspx");
                                	break;
                                }
							}
							BufferedInputStream in = new BufferedInputStream(new URL(fileAddress).openStream());
                            FileOutputStream fileOutputStream = new FileOutputStream(new File("C:\\Users\\" + System.getProperty("user.name") + "\\Documents\\PA_Approved\\"+id+".pdf" ));
							
							byte dataBuffer[] = new byte[1024];
							int bytesRead;
							while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
								fileOutputStream.write(dataBuffer, 0, bytesRead);

							}
							fileOutputStream.close();
				  } catch (Exception e) {
						log("DOCS NOT FOUND");
				  }
				  
				  requestspc = RestAssured.given();
				  requestspc.header("Content-Type", "multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW");
				  requestspc.multiPart("status", "Approved");
				  if(status.equals("APPROVED")) {
				  requestspc.multiPart("doc", new File("C:\\Users\\" + System.getProperty("user.name") + "\\Documents\\PA_Approved\\"+id+".pdf"));
				  }
				  requestspc.multiPart("refno", id); 
				  requestspc.multiPart("process", "Pa");
				  requestspc.multiPart("lettertime", m);
				  int pa = Integer.parseInt(PApprovedAmount);
				  if(pa>0 && status.equals("APPROVED")) {
				  requestspc.multiPart("amount", PApprovedAmount);
				  }
				  log(id+" "+m+" "+PApprovedAmount+" Approved");
				  response1 = requestspc.post(url);
				  log("Status updated for" +id+ "as-" +status+ "at time- "+java.time.LocalTime.now().toString());
				   
			  }	
				  if(status.equals("QUERY")){
					  int z = 0;
					//traverse to QUERY for searching the preauthid in table, if found, then->download letter and update, else ignore and dont call update status api
					  driver.findElement(By.xpath("//*[@id='ctl00_ContentPlaceHolder1_lnkQuery']")).click();
					  rows =driver.findElements(By.xpath("//*[@id='ctl00_ContentPlaceHolder1_dg_DashBoard']/tbody/tr"));
				       System.out.println("No of rows are : " + rows.size()); i1=0;
				       i1 = rows.size(); 
				       if(i1>0) {
				    	   for ( i1 = 2; i1 <= rows.size(); i1++) {
				    	        System.out.println(i1);
				    	       
				    	        element = driver.findElement(By.xpath("//*[@id='ctl00_ContentPlaceHolder1_dg_DashBoard']/tbody/tr["+i1+"]/td[2]"));
				    	        log(element.getText());
				    	       
				    	        if (element.getText().equals(preauthid)) {
				    	        log("Query is raised for -" +id);
				    	        z = 1;
				    	        break;
				    	        }
				    	   }
				       }else {
				    	   log("No Query for patient- "+id);
				       }
				       if(z==1) {
				    	   z=0;
				    	   element = null;
				    	   element = driver.findElement(By.xpath("//*[@id='ctl00_ContentPlaceHolder1_dg_DashBoard_ctl0"+i1+"_lnkEdit']"));
							  Actions actions = new Actions(driver);
							  actions.moveToElement(element).click().build().perform(); 
							
							  
						      try {
								  for (String handle : driver.getWindowHandles()) { 
									   // driver.switchTo().window(handle);

									    ArrayList<String> windowTab = new ArrayList<String>(driver.getWindowHandles());

									    driver.switchTo().window(windowTab.get(1));
		                              
									    url1 = driver.getCurrentUrl();
									    
		                                if(url1.contains("http://claims.starhealth.in:8080/ims/documentToken")) {
		                                	driver.switchTo().window(windowTab.get(1)).close();
		                                	driver.switchTo().window(windowTab.get(0));
		                                	driver.get(url1);
		                                	element = driver.findElement(By.xpath("//iframe[@src]"));
		                                    fileAddress = element.getAttribute("src");
		                                	log(fileAddress);
		                                	driver.get("https://portal.starhealth.in/hospital/Transaction/wfrm_HPDashBoard_Latests.aspx");
		                                	break;
		                                }
									}
									BufferedInputStream in = new BufferedInputStream(new URL(fileAddress).openStream());
		                            FileOutputStream fileOutputStream = new FileOutputStream(new File("C:\\Users\\" + System.getProperty("user.name") + "\\Documents\\PA_Approved\\"+id+".pdf" ));
									
									byte dataBuffer[] = new byte[1024];
									int bytesRead;
									while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
										fileOutputStream.write(dataBuffer, 0, bytesRead);

									}
									fileOutputStream.close();
						  } catch (Exception e) {
								log("DOCS NOT FOUND");
						  }
						  
						  requestspc = RestAssured.given();
						  requestspc.header("Content-Type", "multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW");
						  requestspc.multiPart("status", "Information Awaiting");
						  if(status.equals("QUERY")) {
						  requestspc.multiPart("doc", new File("C:\\Users\\" + System.getProperty("user.name") + "\\Documents\\PA_Approved\\"+id+".pdf"));
						  }
						  requestspc.multiPart("lettertime", m);
						  requestspc.multiPart("refno", id); 
						  requestspc.multiPart("process", "Pa"); 
						  response1 = requestspc.post(url);
						  log(id+" "+m+"  Query");
						  log("Status updated for" +id+ "as-" +status+ "at time- "+java.time.LocalTime.now().toString());
						   
				       }
			  }  
				}
			  else if(!status.equals("Claim In Process")) {
				  String message = status +"-"+ id+"-"+preauthid;
				  smsstar(message, "9967044874");
			  }
			  else { log("status is still in process for -"+id+"-"+preauthid);}
			}
			}
//all preauth checked, now check status of claims, call update api
			if(claim != null) {
				data = claim.get("claim_count").asInt();
				preauthid = null; id =null; j=null; i =0;
				for(i = 0; i < data; i++)
				{ 
					j = Integer.toString(i);
					value = claim.get(j);
					id = value.get("RefNo").asText();
					preauthid = value.get("claimid").asText();
					if(preauthid.equals("0")) {
						preauthid = value.get("Preauth").asText();
					}
					
					element = driver.findElement(By.xpath("//input[@id='ctl00_ContentPlaceHolder1_txtIntimationNo']"));
					if(preauth != null || i>0) {
					element.sendKeys(Keys.CONTROL + "a");
					element.sendKeys(Keys.DELETE);}
					element.sendKeys(preauthid);

					element = driver.findElement(By.xpath("//input[@id='ctl00_ContentPlaceHolder1_btnSearch']"));
					element.click();
					ele = By.xpath("//*[@id='ctl00_ContentPlaceHolder1_dg_DashBoard']/tbody/tr");
					  wait.until(ExpectedConditions.presenceOfElementLocated(ele));
//search result on star will come, compare the status (print the status)
				      rows =driver.findElements(By.xpath("//*[@id='ctl00_ContentPlaceHolder1_dg_DashBoard']/tbody/tr"));
				       System.out.println("No of rows are : " + rows.size());
				       System.out.println(preauthid + "-" +id);
				       i1 = 0;
				       i1 = rows.size(); 
// make it ascending by clicking once on response and then pick last...
				       response = driver.findElement(By.xpath("//*[@id='ctl00_ContentPlaceHolder1_dg_DashBoard']/tbody/tr["+i1+"]/td[6]")).getText();		  
				       status = driver.findElement(By.xpath("//*[@id='ctl00_ContentPlaceHolder1_dg_DashBoard']/tbody/tr["+i1+"]/td[4]")).getText();		  				
				       if(response.equals("")&& status.equals("Claim In Process")) {
				    	   //don't sort it then
				       }
				       else {
				       element = driver.findElement(By.xpath("//*[@id=\"ctl00_ContentPlaceHolder1_dg_DashBoard\"]/tbody/tr[1]/th[6]/a[1]"));
						element.click();
				       }
						try {
							Thread.sleep(9000);
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					  String status = null;String url1 = null; response="";
					  status = driver.findElement(By.xpath("//*[@id='ctl00_ContentPlaceHolder1_dg_DashBoard']/tbody/tr["+i1+"]/td[4]")).getText();		  
					  response = driver.findElement(By.xpath("//*[@id='ctl00_ContentPlaceHolder1_dg_DashBoard']/tbody/tr["+i1+"]/td[6]")).getText();		  
					if(!response.equals("")) {	 
					  String[] splited2 = null;
						  String[] splited = response.split(" ");
						  response = splited[0];
						  String year = response.substring(response.lastIndexOf("/") + 1);
						  String month = response.substring(response.indexOf("/") + 1, response.lastIndexOf("/"));
						  SimpleDateFormat inputFormat = new SimpleDateFormat("MMM");
							Calendar cal = Calendar.getInstance();
							try {
								cal.setTime(inputFormat.parse(month));
							} catch (ParseException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							SimpleDateFormat outputFormat = new SimpleDateFormat("MM"); // 01-12
							m = outputFormat.format(cal.getTime());
					//		System.out.println(m);
						  String day = response.substring(0, response.indexOf("/"));
					    		if(splited[2].equals("PM")) {
					    		 splited2 = response.split(" ");
					    			splited2 = splited[1].split(":");
					    			if(!splited2[0].equals("12")) {
					    				int p = Integer.parseInt(splited2[0]);
					    			splited2[0] = Integer.toString(p+12);
					    			}					    			
					    		}
					    		else {
					    			splited2 = splited[1].split(":");
					    		}
					    		m = day+"/"+m+"/"+year+" "+splited2[0]+":"+splited2[1]+":00";
					  }					  
					  log("Status of "+id+" is-" +status+" at time- "+java.time.LocalTime.now().toString());
					  requestspc = null; response1 = null;
			  if(status.equals("APPROVED") || status.equals("QUERY")) {
				  if(status.equals("APPROVED")){
					//traverse to Reports->preauth approved/rejected -> pass preauthid to Intimation no and search->and read approved amount->download letter
					  driver.get("https://portal.starhealth.in/hospital/Transaction/wfrm_HPPreAuthApprRejRpt.aspx");
					  
					  driver.findElement(By.xpath("//input[@name='ctl00$ContentPlaceHolder1$txtIntimationNo']")).sendKeys(preauthid);

					  driver.findElement(By.xpath("//input[@name='ctl00$ContentPlaceHolder1$btnSearch']")).click();
					  String claimno = " ";
					  ele = By.xpath("//table[@id='ctl00_ContentPlaceHolder1_dgPreAuthApprRejList']/tbody/tr[2]/td[2]");
					  wait.until(ExpectedConditions.presenceOfElementLocated(ele));
					  do {
						  try {
							  claimno = driver.findElement(By.xpath("//table[@id='ctl00_ContentPlaceHolder1_dgPreAuthApprRejList']/tbody/tr[2]/td[2]")).getText();  
								 }catch(Exception e) { System.out.println("no such element found");}
								  }while(!claimno.equals(preauthid)); 
					  
					  i1 = 0;
					  rows = driver.findElements(By.xpath("//*[@id='ctl00_ContentPlaceHolder1_dgPreAuthApprRejList']/tbody/tr"));
				      System.out.println("No of Approved rows are : " + rows.size());
				      i1 = rows.size();
				     for(i2 =2; i2<=i1; i2++) {
					  PAuth = driver.findElement(By.xpath("//table[@id='ctl00_ContentPlaceHolder1_dgPreAuthApprRejList']/tbody/tr["+i2+"]/td[7]")).getText();					  
					  PAStatus = driver.findElement(By.xpath("//table[@id='ctl00_ContentPlaceHolder1_dgPreAuthApprRejList']/tbody/tr["+i2+"]/td[9]")).getText();
					  if(PAuth.equals("ENHANCE") && PAStatus.equals("APPROVED")) break;
				      }//while(!PAuth.equals("PREAUTH") && !PAStatus.equals("APPROVED"));
					  PApprovedAmount = driver.findElement(By.xpath("//*[@id='ctl00_ContentPlaceHolder1_dgPreAuthApprRejList']/tbody/tr["+i2+"]/td[8]")).getText();
					  log("Type:-"+PAuth+" Status:-"+PAStatus+" Amount:-"+PApprovedAmount);	  
						
					  element = driver.findElement(By.xpath("//*[@id='ctl00_ContentPlaceHolder1_dgPreAuthApprRejList']/tbody/tr["+i2+"]/td[10]"));
					   Actions actions = new Actions(driver);
					  actions.moveToElement(element).click().build().perform();
				      try {
						  for (String handle : driver.getWindowHandles()) { 
							   // driver.switchTo().window(handle);

							    ArrayList<String> windowTab = new ArrayList<String>(driver.getWindowHandles());

							    driver.switchTo().window(windowTab.get(1));
                              
							    url1 = driver.getCurrentUrl();
							    
                                if(url1.contains("http://claims.starhealth.in:8080/ims/documentToken")) {
                                	driver.switchTo().window(windowTab.get(1)).close();
                                	driver.switchTo().window(windowTab.get(0));
                                	driver.get(url1);
                                	element = driver.findElement(By.xpath("//iframe[@src]"));
                                    fileAddress = element.getAttribute("src");
                                	log(fileAddress);
                                	break;
                                }
							}
							BufferedInputStream in = new BufferedInputStream(new URL(fileAddress).openStream());
                            FileOutputStream fileOutputStream = new FileOutputStream(new File("C:\\Users\\" + System.getProperty("user.name") + "\\Documents\\Claim_Approved\\"+id+".pdf" ));
							
							byte dataBuffer[] = new byte[1024];
							int bytesRead;
							while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
								fileOutputStream.write(dataBuffer, 0, bytesRead);

							}
							fileOutputStream.close();
				  } catch (Exception e) {
						log("DOCS NOT FOUND");
				  }
				  
				  requestspc = RestAssured.given();
				  requestspc.header("Content-Type", "multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW");
				  requestspc.multiPart("status", "Approved");
				  if(status.equals("APPROVED")) {
				  requestspc.multiPart("doc", new File("C:\\Users\\" + System.getProperty("user.name") + "\\Documents\\Claim_Approved\\"+id+".pdf"));
				  }
				  requestspc.multiPart("lettertime", m);
				  requestspc.multiPart("refno", id); //I29 for Star
				  requestspc.multiPart("process", "Cl"); 
				  
				  int pa = Integer.parseInt(PApprovedAmount);
				  if(pa>0 && status.equals("APPROVED")) {
				  requestspc.multiPart("amount", PApprovedAmount);
				  }
				  log(id+" "+m+" "+PApprovedAmount+" Approved Claim");
				  response1 = requestspc.post(url);
				  log("Status updated for" +id+ "as-" +status+ "at time- "+java.time.LocalTime.now().toString());
				   
			  }	
				  if(status.equals("QUERY")){
					//traverse to QUERY for searching the preauthid in table, if found, then->download letter and update, else ignore and dont call update status api
					  int z = 0;
						//traverse to QUERY for searching the preauthid in table, if found, then->download letter and update, else ignore and dont call update status api
						  driver.findElement(By.xpath("//*[@id='ctl00_ContentPlaceHolder1_lnkQuery']")).click();
						  rows =driver.findElements(By.xpath("//*[@id='ctl00_ContentPlaceHolder1_dg_DashBoard']/tbody/tr"));
					       System.out.println("No of rows are : " + rows.size()); i1=0;
					       i1 = rows.size(); 
					       if(i1>0) {
					    	   for ( i1 = 2; i1 <= rows.size(); i1++) {
					    	        System.out.println(i1);
					    	       
					    	        element = driver.findElement(By.xpath("//*[@id='ctl00_ContentPlaceHolder1_dg_DashBoard']/tbody/tr["+i1+"]/td[2]"));
					    	        log(element.getText());
					    	       
					    	        if (element.getText().equals(preauthid)) {
					    	        log("Query is raised for -" +id);
					    	        z = 1;
					    	        break;
					    	        }
					    	   }
					       }else {
					    	   log("No Query for patient- "+id);
					       }
					       if(z==1) {
					    	   z=0;
					    	   element = driver.findElement(By.xpath("//*[@id='ctl00_ContentPlaceHolder1_dg_DashBoard_ctl0"+i1+"_lnkEdit']"));
								  Actions actions = new Actions(driver);
								  actions.moveToElement(element).click().build().perform(); 
								  
							      try {
									  for (String handle : driver.getWindowHandles()) { 
										   // driver.switchTo().window(handle);

										    ArrayList<String> windowTab = new ArrayList<String>(driver.getWindowHandles());

										    driver.switchTo().window(windowTab.get(1));
			                              
										    url1 = driver.getCurrentUrl();
										    
			                                if(url1.contains("http://claims.starhealth.in:8080/ims/documentToken")) {
			                                	driver.switchTo().window(windowTab.get(1)).close();
			                                	driver.switchTo().window(windowTab.get(0));
			                                	driver.get(url1);
			                                	element = driver.findElement(By.xpath("//iframe[@src]"));
			                                    fileAddress = element.getAttribute("src");
			                                	log(fileAddress);
			                                	driver.get("https://portal.starhealth.in/hospital/Transaction/wfrm_HPDashBoard_Latests.aspx");
			                                	break;
			                                }
										}
										BufferedInputStream in = new BufferedInputStream(new URL(fileAddress).openStream());
			                            FileOutputStream fileOutputStream = new FileOutputStream(new File("C:\\Users\\" + System.getProperty("user.name") + "\\Documents\\Claim_Approved\\"+id+".pdf" ));
										
										byte dataBuffer[] = new byte[1024];
										int bytesRead;
										while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
											fileOutputStream.write(dataBuffer, 0, bytesRead);

										}
										fileOutputStream.close();
							  } catch (Exception e) {
							        e.printStackTrace();
									log("DOCS NOT FOUND");
							  }
							  
							  requestspc = RestAssured.given();
							  requestspc.header("Content-Type", "multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW");
							  requestspc.multiPart("status", "Information Awaiting");
							  if(status.equals("QUERY")) {
							  requestspc.multiPart("doc", new File("C:\\Users\\" + System.getProperty("user.name") + "\\Documents\\Claim_Approved\\"+id+".pdf"));
							  }
							  requestspc.multiPart("lettertime", m);
							  requestspc.multiPart("refno", id); 
							  requestspc.multiPart("process", "Cl");
							  log(id+" "+m+"  Claim Query");
						 	  response1 = requestspc.post(url);
							  log("Status updated for" +id+ "as-" +status+ "at time- "+java.time.LocalTime.now().toString());
							   
					       }
				  
			  }  
				}
			  else if(!status.equals("Claim In Process")) {
				  String message = status +"-"+ id+"-"+preauthid;
				  smsstar(message, "9967044874");
			  }
			  else { log("status is still in process for -"+id+"-"+preauthid);}
			}
			}
			try {
			 element = driver.findElement(By.xpath("//*[@id=\"ctl00_ImageButton3\"]"));
			 Actions actions = new Actions(driver);
		    actions.moveToElement(element).click().build().perform();
		    driver.close();
		    System.out.println("Driver closed");
			}catch(Exception e) {
				System.out.println("Driver closed");
				driver.close();
			}
	 }
  }while(a != 100);
}
	  
/*
	  try {

	  for ( i = 1; i <= 5; i++) {
	  element = driver.findElement(
	  By.xpath("//*[@id='ctl00_ContentPlaceHolder1_dg_DashBoard']/tbody/tr[2]/td[" + i + "]"));
	  log(element.getText());
	  if (element.getText().contains(patientid)) {
	  j = i+2;
	  element = driver.findElement(By.xpath(                                                  //8
	  "//*[@id='ctl00_ContentPlaceHolder1_dg_DashBoard']/tbody/tr[2]/td[" + j + "]"));

	  String currentStatus = element.getText();
	  log(element.getText());
	  if(currentStatus.contains(status)) {
	  //update our API
	  if (update == null)
	  update = new Update();
	  update.doUpdate(RefNo, process, status,"Running", strDate, stime);

	  log("Status is same for -" + RefNo);
	     lastStatus = " ";
	  break;
	  }
	  else {
	  lastStatus = new String(currentStatus);
	  log("status has been changed to " + lastStatus + " for " + RefNo);
	  // driver.close();
	  if (update == null)
	  update = new Update();
	  update.doUpdateStatus(RefNo, process, lastStatus, "Completed",  strDate, stime);
	  break;
	  }
	  }
	  }
	  element = driver.findElement(By.xpath("//input[@id='ctl00_ImageButton3']"));
	  element.click();
	  }catch(Exception e)
	  {
	  log("exception raised at star status track");
	  driver.close();
	  if (update == null)
	  update = new Update();
	  update.doUpdate(RefNo, process, status,"Failed", strDate, stime);
	  //return lastStatus;
	  }
	        driver.close();
	  //return lastStatus;
	}
	*/

  
  
	public void log(String log) {
		File file = new File("C:\\Users\\" + System.getProperty("user.name") + "\\Documents\\starlog.txt");
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
  
	public  void smsstar(String message, String mobile){
		HttpResponse<String> response = Unirest.post("https://control.msg91.com/api/postsms.php")
				  .header("content-type", "application/xml")
				  .body("<MESSAGE>"
				  		+ "<AUTHKEY>167826ARvnR1lKl5cee8065</AUTHKEY>"
				  		+ "<ROUTE>4</ROUTE>"
				  		+ "<COUNTRY>91</COUNTRY>"
				  		+ "<SENDER>iClaim</SENDER>"
				  		+ "<SMS TEXT="+message+">"
				  		+ " <ADDRESS TO="+mobile+"></ADDRESS>"
				  		+ "</SMS> "
				  		+ "</MESSAGE>")
				  .asString();
		log("SMS for Star sent to-"+mobile+"-"+message);
	}   

public void logic(String log) {
	File file = new File("C:\\Users\\" + System.getProperty("user.name") + "\\Documents\\icicilog.txt");
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

public  void smsicici(String message, String mobile){
	HttpResponse<String> response = Unirest.post("https://control.msg91.com/api/postsms.php")
			  .header("content-type", "application/xml")
			  .body("<MESSAGE>"
			  		+ "<AUTHKEY>167826ARvnR1lKl5cee8065</AUTHKEY>"
			  		+ "<ROUTE>4</ROUTE>"
			  		+ "<COUNTRY>91</COUNTRY>"
			  		+ "<SENDER>iClaim</SENDER>"
			  		+ "<SMS TEXT="+message+">"
			  		+ " <ADDRESS TO="+mobile+"></ADDRESS>"
			  		+ "</SMS> "
			  		+ "</MESSAGE>")
			  .asString();
	log("SMS for ICICI sent to-"+mobile+"-"+message);
}
	
	
/*	
		String lastStatus = null;
		String RefNo = value.get("RefNo").asText();
		try {
		ele = By.xpath("//*[@id=\'content\']/ul/li[1]/div[1]/div[1]/span[1]/a");
	//	wait.until(ExpectedConditions.presenceOfElementLocated(ele));

		element = driver.findElement(By.xpath("//*[@id=\'content\']/ul/li[1]/div[1]/div[1]/span[1]/a"));
		element.click();

		element = driver.findElement(By.xpath("//*[@id=\'inner-contents\']/div[1]/ul/li[3]/a/span"));
		element.click();

		element = driver.findElement(By.xpath("//*[@id=\'sAlNo\']"));
		element.sendKeys(patientid);

		element = driver.findElement(By.xpath("//*[@id=\'btnSubmit\']"));
		element.click();


		List<WebElement> rows = driver.findElements(By.xpath("//*[@id=\'mt\']/tbody/tr/td[1]"));
		System.out.println(rows.size());

		for (int i = 2; i <= rows.size(); i++) {
		System.out.println(i);
		element = driver
		.findElement(By.xpath("//*[@id=\'mt\']/tbody/tr[" + i + "]/td/div/table/tbody/tr[1]/td[2]"));
		log(element.getText());
		if (element.getText().equals(patientid)) {
		element = driver.findElement(
		By.xpath("//*[@id=\'mt\']/tbody/tr[" + i + "]/td/div/table/tbody/tr[1]/td[5]"));
		                 
		String currentStatus = element.getText();
		log(element.getText());

		if(currentStatus.equals(status)) {
		//update our API
		if (update == null)
		update = new Update();
		update.doUpdate(RefNo, process, status,"Running", strDate, stime);

		log("Status is same for -" + RefNo);
		   lastStatus = " ";
		break;
		}
		else {
		lastStatus = new String(currentStatus);
		log("status has been changed to " + lastStatus + " for " + RefNo);
		if (update == null)
		update = new Update();
		update.doUpdateStatus(RefNo, process, lastStatus, "Completed",  strDate, stime);
		break;
		}

		}
		}}catch(Exception e)
		{
		log("issue in ICICI status track-" + RefNo);
		if (update == null)
		update = new Update();
		update.doUpdate(RefNo, process, status ,"Failed", strDate, stime);
		    driver.close();
		// return lastStatus;
		}
		driver.close();
		// return lastStatus;
		}
*/	
	
	
	
	
	
  
  
  @BeforeTest
  public void beforeTest() {
	  Date date = new Date();  
	    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");  
	    strDate= formatter.format(date); 
	    os = System.getProperty("os.name").toLowerCase();
  }

  @AfterTest
  public void afterTest() {
  }

}
