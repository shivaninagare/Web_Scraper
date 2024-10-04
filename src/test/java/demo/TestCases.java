package demo;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
//import org.util.ArrayList;
import com.google.common.base.Equivalence.Wrapper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
//import com.fasterxml.jackson.databind.ObjectMapper;

import demo.wrappers.Utilities;
// import io.github.bonigarcia.wdm.WebDriverManager;
import demo.wrappers.Wrappers;

public class TestCases {
    ChromeDriver driver;

    @Test
    public void testCase01() throws InterruptedException{
        driver.get("https://www.scrapethissite.com/pages/");

        Assert.assertTrue(driver.getCurrentUrl().equals("https://www.scrapethissite.com/pages/"),"Unverified URL");
        System.out.println("Verified URL: https://www.scrapethissite.com/pages/");
        Thread.sleep(3000);
        WebElement hockeyTeamsElement = driver.findElement(By.xpath("//a[contains(text(),'Hockey Teams')]"));
        Wrappers.clickOnElement(hockeyTeamsElement,driver);

        ArrayList<HashMap<String, Object>> dataList = new ArrayList<>();

        WebElement clickOnPage = driver.findElement(By.xpath("(//ul[@class='pagination']/li/a)[1]"));

        Wrappers.clickOnElement(clickOnPage,driver);

        for(int page  = 1; page <= 4; page++){
            List<WebElement> rows = driver.findElements(By.xpath("//tr[@class='team']"));
            for(WebElement row:rows){
                String teamsName= row.findElement(By.xpath(".//td[@class='name']")).getText();

                int year= Integer.parseInt(row.findElement(By.xpath(".//td[@class='year']")).getText()) ;

                double winPercentage = Double.parseDouble(row.findElement(By.xpath(".//td[contains(@class,'pct')]")).getText());

                //declare epochTime
                long epoch = System.currentTimeMillis()/1000;

                String epochTime = String.valueOf(epoch);
                if(winPercentage <0.4){
                    HashMap<String, Object> dataMap = new HashMap<>();
                    dataMap.put("epochTime",epochTime);
                    dataMap.put("teamName",teamsName);
                    dataMap.put("year",year);
                    dataMap.put("winPercentage",winPercentage);

                    dataList.add(dataMap);

                }

            }
            if(page<4){
                WebElement nextPagElement = driver.findElement(By.xpath("//a[@aria-label = 'Next']"));
                nextPagElement.click();
                Thread.sleep(5000);
            }
        }
        for(HashMap<String,Object> data : dataList){
            System.out.println("Epoch time of scrape "+ data.get("epochTime")+ ", Team Name: "+ data.get("teamName")+ ", Years: "+ data.get("year")+", win% " +data.get("winPercentage"));
        }

        ObjectMapper mapper = new ObjectMapper();
        try{
            String uderDir = System.getProperty("user.dir");
            File jsonFile = new File(uderDir+ "/src/test/resources/hokey-team-data.json");
            mapper.writeValue(jsonFile,dataList);
            System.out.println("JSON data written to: "+jsonFile.getAbsolutePath());
            Assert.assertTrue(jsonFile.length() !=0);
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    @Test
    public void testCase02(){
        driver.get("https://www.scrapethissite.com/pages/");
        WebElement oscarWiningFilms = driver.findElement(By.xpath("//a[contains(text(),'Oscar Winning Films')]"));
        Wrappers.clickOnElement(oscarWiningFilms, driver);
        Utilities.scrape("2015",driver);
        Utilities.scrape("2014",driver);
        Utilities.scrape("2013",driver);
        Utilities.scrape("2012",driver);
        Utilities.scrape("2011",driver);
        Utilities.scrape("2010",driver);



    }
    @BeforeTest
    public void startBrowser()
    {
        System.setProperty("java.util.logging.config.file", "logging.properties");

        // NOT NEEDED FOR SELENIUM MANAGER
        // WebDriverManager.chromedriver().timeout(30).setup();

        ChromeOptions options = new ChromeOptions();
        LoggingPreferences logs = new LoggingPreferences();

        logs.enable(LogType.BROWSER, Level.ALL);
        logs.enable(LogType.DRIVER, Level.ALL);
        options.setCapability("goog:loggingPrefs", logs);
        options.addArguments("--remote-allow-origins=*");

        System.setProperty(ChromeDriverService.CHROME_DRIVER_LOG_PROPERTY, "build/chromedriver.log"); 

        driver = new ChromeDriver(options);

        driver.manage().window().maximize();
    }
    

    // @AfterTest
    // public void endTest()
    // {
    //     driver.close();
    //     driver.quit();

    // }
}