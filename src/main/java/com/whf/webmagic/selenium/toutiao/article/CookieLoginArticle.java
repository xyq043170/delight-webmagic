package com.whf.webmagic.selenium.toutiao.article;

import com.alibaba.fastjson.JSON;
import com.whf.webmagic.constant.ConstantKey;
import com.whf.webmagic.dao.DelightArticleDao;
import com.whf.webmagic.dao.DelightWebmagicDao;
import com.whf.webmagic.entity.DelightArticle;
import com.whf.webmagic.entity.DelightWebmagic;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by wycm on 2018/9/20.
 * selenium破解腾讯滑动验证码
 */
@Component
public class CookieLoginArticle {
    private final static Logger LOG = LoggerFactory.getLogger(CookieLoginArticle.class);
    public static ChromeDriver driver = null;
    static {
        System.setProperty("webdriver.chrome.driver", ConstantKey.CHROM_DRIVER);
    }

    private DelightWebmagicDao delightWebmagicDao;
    private DelightArticleDao delightArticleDao;
    public CookieLoginArticle(DelightWebmagicDao delightWebmagicDao, DelightArticleDao delightArticleDao){
        this.delightWebmagicDao = delightWebmagicDao;
        this.delightArticleDao = delightArticleDao;
    }


    public static void main(String[] args) {
//        crawl();
    }


    public void crawl(){
        driver = new ChromeDriver();
            try {
                //cook登录
                driver.manage().window().setSize(new Dimension(1024, 768));
                driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
                driver.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);
                driver.get(ConstantKey.TOUTIAO_LOGIN_URL);
                Thread.sleep(3 * 1000);
                File cookieFile = new File(ConstantKey.TOUTIAO_COOKIE_PATH);
                FileReader fileReader = new FileReader(cookieFile);
                BufferedReader bufferedReader = new BufferedReader(fileReader);

                String line;

                while ((line = bufferedReader.readLine()) != null){
                    StringTokenizer stringTokenizer = new StringTokenizer(line, ";");
                    while (stringTokenizer.hasMoreTokens()){

                        String name = stringTokenizer.nextToken();
                        String value = stringTokenizer.nextToken();
                        String domain = stringTokenizer.nextToken();
                        String path = stringTokenizer.nextToken();
                        Date expiry = null;
                        String dt;

                        if (!(dt = stringTokenizer.nextToken()).equals("null")){
                            expiry = new Date(dt);
                        }

                        boolean isSecure = new Boolean(stringTokenizer.nextToken()).booleanValue();
                        Cookie cookie = new Cookie(name, value,domain,path,expiry,isSecure);
                        driver.manage().addCookie(cookie);
                    }
                }

                //打开主页
                driver.get(ConstantKey.TOUTIAO_MAIN_URL);
                Thread.sleep(10 * 1000);
                int sendCnts =0;
                while (!sendArticle())
                {
                    sendCnts++;
                    if(sendCnts >= 3) {break;}
                }
                Thread.sleep(3 * 1000);
            } catch (Exception e) {
                e.printStackTrace();
            }

            driver.quit();
    }

    public boolean sendArticle() throws InterruptedException {
        boolean result = false;
        List<DelightWebmagic> lists =delightWebmagicDao.queryRandPic();
        LOG.info("lists size ="+lists.size());
        driver.findElement(By.xpath("//a[text()='发头条']")).click();
        Thread.sleep(5 * 1000);
        WebElement e= driver.findElement(By.id("title"));
        e.clear();
        //输入文章标题
        DelightArticle param = new DelightArticle();
        param.setChannel(ConstantKey.TOUTIAO_CHANNEL);
        DelightArticle delightArticle=delightArticleDao.queryArticleNum(param);
        LOG.info("delightArticle ="+ JSON.toJSONString(delightArticle));
        long articleNum =delightArticle.getArticleNum()+1;
        String title = ConstantKey.ARTICLE_TITLE+articleNum+"期";
        LOG.info("title ="+title);
        e.sendKeys(title);
        Thread.sleep(2 * 1000);
        e= driver.findElement(By.className("ql-editor"));
        e.clear();
        for (int i = 0; i < lists.size(); i++) {
            e.sendKeys(lists.get(i).getTitle());
            driver.findElement(By.className("ql-image")).click();
            Thread.sleep(3 * 1000);
            JavascriptExecutor j=(JavascriptExecutor)driver;
            j.executeScript("document.querySelector('.syl-img-upload>input').style.display='block';");
            WebElement file = driver.findElement(By.xpath("//span[@class='syl-img-upload']/input"));
            file.sendKeys(lists.get(i).getLocalUrl());
            Thread.sleep(10 * 1000);
            driver.findElement(By.xpath("//div[@class='confirm']/button[@class='tui2-btn tui2-btn-size-default tui2-btn-primary']")).click();
            Thread.sleep(1 * 1000);
            e.sendKeys(Keys.ENTER);
            LOG.info("send pic i="+i);
        }

        e.sendKeys(Keys.ENTER);
        e.sendKeys("喜欢的请点击我加关注哦！谢谢！");
        Thread.sleep(2 * 1000);
        driver.findElement(By.xpath("//span[text()='自动']/preceding-sibling::div[1]/input")).click();
        Thread.sleep(1 * 1000);
        driver.findElement(By.id("publish")).click();
        Thread.sleep(70 * 1000);
        LOG.info("publish");
        boolean result1=isSendStatus1();
        boolean result2=isSendStatus2();
        LOG.info("publish status1="+result1);
        LOG.info("publish status2="+result2);
        if(result1 || result2){
            param = new DelightArticle();
            param.setChannel(ConstantKey.TOUTIAO_CHANNEL);
            param.setArticleNum((int)articleNum);
            delightArticleDao.updateArticleNum(param);
            result = true;
        }
        return result;
    }

    private boolean isSendStatus1()
    {
        boolean result = false;

        try {
            driver.findElement(By.xpath("//div[@class='article-card-bone']/div[@class='abstruct']/span[text()='审核中']"));
            result = true;
        }catch (Exception e)
        {
            result = false;
        }
        return result;
    }

    private boolean isSendStatus2()
    {
        boolean result = false;

        try {
            driver.findElement(By.xpath("//div[@class='article-card-bone']/div[@class='abstruct']/span[text()='已发表']"));
            result = true;
        }catch (Exception e)
        {
            result = false;
        }
        return result;
    }

    public static void task(DelightWebmagicDao delightWebmagicDao, DelightArticleDao delightArticleDao)
    {
        LOG.info("Start toutiao cookieLogin task");
        new CookieLoginArticle(delightWebmagicDao,delightArticleDao).crawl();
    }
}
