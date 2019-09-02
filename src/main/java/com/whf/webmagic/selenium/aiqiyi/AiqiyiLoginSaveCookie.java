package com.whf.webmagic.selenium.aiqiyi;

import com.whf.webmagic.constant.ConstantKey;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.concurrent.TimeUnit;

/**
 * Created by wycm on 2018/9/20.
 * selenium破解腾讯滑动验证码
 */
public class AiqiyiLoginSaveCookie {
    private static ChromeDriver driver = null;
    static {
        System.setProperty("webdriver.chrome.driver", ConstantKey.CHROM_DRIVER);
    }
    public static void main(String[] args) {
        crawl();
    }


    public static void crawl(){
        driver = new ChromeDriver();
            try {
                driver.manage().window().setSize(new Dimension(1024, 768));
                driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
                driver.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);
                driver.get(ConstantKey.AIQIYI_LOGIN_URL);
                Thread.sleep(5 * 1000);

//                WebDriverWait webDriverWait=new WebDriverWait(driver,5);
//                driver.findElement(By.id("user-mobile")).clear();
//                webDriverWait.until(ExpectedConditions.elementToBeClickable(By.id("user-mobile"))).sendKeys("xxxxx");
//                driver.findElement(By.className("get-code")).click();

                Thread.sleep(60 * 1000);
                File cookieFile = new File(ConstantKey.AIQIYI_COOKIE_PATH);
                cookieFile.delete();
                cookieFile.createNewFile();
                FileWriter fileWriter = new FileWriter(cookieFile);
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

                for (Cookie cookie:driver.manage().getCookies()){
                    bufferedWriter.write((cookie.getName()+";"+
                            cookie.getValue()+";"+
                            cookie.getDomain()+";"+
                            cookie.getPath()+";"+
                            cookie.getExpiry()+";"+
                            cookie.isSecure()));
                    bufferedWriter.newLine();
                }
                bufferedWriter.flush();
                bufferedWriter.close();
                fileWriter.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            driver.quit();
    }
}
