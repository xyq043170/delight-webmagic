package com.whf.webmagic.selenium.aiqiyi.video;

import com.alibaba.fastjson.JSON;
import com.whf.webmagic.constant.ConstantKey;
import com.whf.webmagic.dao.DelightArticleDao;
import com.whf.webmagic.dao.DelightVideoResultDao;
import com.whf.webmagic.dao.DelightWebmagicDao;
import com.whf.webmagic.entity.DelightArticle;
import com.whf.webmagic.entity.DelightVideoResult;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;

/**
 * Created by wycm on 2018/9/20.
 * selenium破解腾讯滑动验证码
 */
@Component
public class AiqiyiCookieLoginVideo {
    private final static Logger LOG = LoggerFactory.getLogger(AiqiyiCookieLoginVideo.class);
    public static ChromeDriver driver = null;
    static {
        System.setProperty("webdriver.chrome.driver", ConstantKey.CHROM_DRIVER);
    }

    private DelightWebmagicDao delightWebmagicDao;
    private DelightArticleDao delightArticleDao;
    private DelightVideoResultDao delightVideoResultDao;
    public AiqiyiCookieLoginVideo(DelightWebmagicDao delightWebmagicDao, DelightArticleDao delightArticleDao, DelightVideoResultDao delightVideoResultDao){
        this.delightWebmagicDao = delightWebmagicDao;
        this.delightArticleDao = delightArticleDao;
        this.delightVideoResultDao = delightVideoResultDao;
    }


    public static void main(String[] args) {
//        crawl();
    }


    public void crawl(){
        driver = new ChromeDriver();
            try {
                driver.manage().window().setSize(new Dimension(1024, 768));
                driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
                driver.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);
                driver.get(ConstantKey.AIQIYI_LOGIN_URL);
                Thread.sleep(3 * 1000);
                File cookieFile = new File(ConstantKey.AIQIYI_COOKIE_PATH);
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

                driver.get(ConstantKey.AIQIYI_MAIN_URL);
                Thread.sleep(10 * 1000);
                int sendCnts =0;
                while (!sendVideo())
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

    public boolean sendVideo() throws InterruptedException {
        boolean result = false;
        DelightArticle param = new DelightArticle();
        param.setChannel(ConstantKey.AIQIYI_VIDEO_CHANNEL);
        DelightArticle delightArticle=delightArticleDao.queryArticleNum(param);
        LOG.info("sendVideo delightArticle ="+ JSON.toJSONString(delightArticle));
        long articleNum =delightArticle.getArticleNum()+1;
        DelightVideoResult delightVideoParam = new DelightVideoResult();
        delightVideoParam.setVideoNum((int)articleNum);
        DelightVideoResult delightVideoResult=delightVideoResultDao.queryDelightVideoResultInfo(delightVideoParam);
        LOG.info("sendVideo delightVideoResult ="+ JSON.toJSONString(delightVideoResult));

        driver.findElement(By.xpath("//span[text()='发布作品']")).click();
        Thread.sleep(5000);


        WebElement file = driver.findElement(By.xpath("//div[@class='hotVideo-active']/following-sibling::div[1]/input"));
        file.sendKeys(delightVideoResult.getLocalPath());
        Thread.sleep(120000);
//        Thread.sleep(10000);

        driver.findElement(By.xpath("//div[text()='设置封面']")).click();

        file = driver.findElement(By.xpath("//div[@class='setCover-popup']/following-sibling::div[1]/div/input"));
        file.sendKeys(delightVideoResult.getPicPath());
        Thread.sleep(10000);

        driver.findElement(By.xpath("//div[@class='setCover-btn']/button")).click();
        Thread.sleep(5000);

        //输入视频标签
//        driver.findElement(By.xpath("//div[text()='请输入标签']")).click();
//        driver.executeScript("document.querySelector('.Select-input>input').value='搞笑'");
//        Thread.sleep(2000);
        WebElement searchBox = driver.findElement(By.xpath("//div[@class='mp-input mp-input--height--normal mp-input--suffix']/div/input"));
        searchBox.sendKeys("搞笑");
        Thread.sleep(2000);
        searchBox.sendKeys(Keys.ENTER);
        Thread.sleep(2000);
        searchBox = driver.findElement(By.xpath("//div[@class='mp-input mp-input--height--normal mp-input--suffix']/div/input"));
        searchBox.sendKeys("影视");
        Thread.sleep(2000);
        searchBox.sendKeys(Keys.ENTER);
        Thread.sleep(2000);
        searchBox = driver.findElement(By.xpath("//div[@class='mp-input mp-input--height--normal mp-input--suffix']/div/input"));
        searchBox.sendKeys("幽默");
        Thread.sleep(2000);
        searchBox.sendKeys(Keys.ENTER);
        Thread.sleep(2000);
        //点击视频分类
        driver.findElement(By.xpath("//div[@class='mp-select']/div[@class='mp-input mp-input--height--normal mp-input--suffix']")).click();
        Thread.sleep(2000);
        driver.findElement(By.xpath("//div[@class='mp-scrollbar']/div/ul/li/span[text()='搞笑']/parent::li")).click();
        Thread.sleep(2000);

        driver.findElement(By.xpath("//span[text()='原创']/parent::label")).click();
        Thread.sleep(2000);

        driver.findElement(By.xpath("//div[@class='mp-videoUpload-btn']/button/span[text()='发布']/parent::button")).click();
        Thread.sleep(5000);
        LOG.info("publish");
        boolean result1=isSendStatus1();
        boolean result2=isSendStatus2();
        LOG.info("publish status1="+result1);
        LOG.info("publish status2="+result2);
        if(result1 || result2){
            param = new DelightArticle();
            param.setChannel(ConstantKey.AIQIYI_VIDEO_CHANNEL);
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
            driver.findElement(By.xpath("//span[text()='转码中']"));
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
            driver.findElement(By.xpath("//div[@class='public-topCon']/div/span[text()='作品管理']"));
            result = true;
        }catch (Exception e)
        {
            result = false;
        }
        return result;
    }

    public static void task(DelightWebmagicDao delightWebmagicDao, DelightArticleDao delightArticleDao,DelightVideoResultDao delightVideoResultDao)
    {
        LOG.info("Start toutiao cookieLoginVideo task");
        new AiqiyiCookieLoginVideo(delightWebmagicDao,delightArticleDao,delightVideoResultDao).crawl();
    }
}
