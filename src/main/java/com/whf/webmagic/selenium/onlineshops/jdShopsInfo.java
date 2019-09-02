package com.whf.webmagic.selenium.onlineshops;

import com.whf.webmagic.constant.ConstantKey;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class jdShopsInfo {
    private static ChromeDriver driver = null;
    static {
        System.setProperty("webdriver.chrome.driver", ConstantKey.CHROM_DRIVER);
    }
    public static void main(String[] args) throws InterruptedException {
//        ChromeOptions chromeOptions= new ChromeOptions();
//        chromeOptions.addArguments("--headless");
//        chromeOptions.addArguments("--disable-gpu");
//        chromeOptions.addArguments("--window-size=1920,1080");
//        driver = new ChromeDriver(chromeOptions);
        driver = new ChromeDriver();
        driver.get("https://www.1688.com/");
        Thread.sleep(5000);
        driver.findElement(By.xpath("//div[@id='check-dialog']/span/i")).click();
        WebElement e= driver.findElement(By.id("alisearch-keywords"));
        e.clear();
        e.sendKeys("童装");
        Thread.sleep(1000);
        driver.findElement(By.id("alisearch-submit")).click();
        Thread.sleep(2000);
        driver.findElement(By.xpath("//div[@id='s-module-overlay']/div/div/div[@class='s-overlay-close']")).click();
        Thread.sleep(1000);
        driver.findElement(By.xpath("//span[text()='成交额']")).click();
        Thread.sleep(500);
        System.out.println(driver.getCurrentUrl());

//        String title =driver.findElement(By.xpath("//ul[@id='sm-offer-list']/li/div[@class='imgofferresult-mainBlock']/div/a/img/@src"));
//
////        String title = driver.getTitle();
//        System.out.println(title);
        driver.quit();
    }
}
