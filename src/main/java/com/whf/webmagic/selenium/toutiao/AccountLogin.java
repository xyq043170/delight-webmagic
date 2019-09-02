package com.whf.webmagic.selenium.toutiao;

import org.apache.commons.io.FileUtils;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.ssl.SSLContexts;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import javax.imageio.ImageIO;
import javax.net.ssl.SSLContext;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by wycm on 2018/9/20.
 * selenium破解腾讯滑动验证码
 */
public class AccountLogin {
    private static String BASE_PATH = "E:\\testpic\\";
    //小方块距离左边界距离
    private static int START_DISTANCE = 0;//22 + 16;
    private static ChromeDriver driver = null;
    static {
        System.setProperty("webdriver.chrome.driver", "E:\\private\\chromedriver_win32\\chromedriver.exe");
    }
    public static void main(String[] args) {
        crawl();
//        try
//        {
////            for (int i = 0; i < 20; i++) {
//                calcMoveDistance(101L);
////            }
//        }catch (Exception e)
//        {
//            e.printStackTrace();
//        }

    }


    public static void crawl(){
        driver = new ChromeDriver();
//        for(int i = 0; i < 10; i++) {
            try {
                driver.manage().window().setSize(new Dimension(1024, 768));
                driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
                driver.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);
                driver.get("https://sso.toutiao.com/login/?service=https://mp.toutiao.com/sso_confirm/?redirect_url=/");
//                WebElement element = driver.findElement(By.cssSelector("a[data-type='1']"));
//                element.click();
                Thread.sleep(5 * 1000);
                driver.findElement(By.id("login-type-account")).click();
                Thread.sleep(2 * 1000);
                WebDriverWait webDriverWait=new WebDriverWait(driver,5);
                driver.findElement(By.id("user-name")).clear();
                //输入登录账号
                webDriverWait.until(ExpectedConditions.elementToBeClickable(By.id("user-name"))).sendKeys("xxxx");
                WebElement e= driver.findElement(By.id("password"));
                e.clear();
                System.out.println("清空密码框内容----------------------------------------------------------------------------------------------------------");
                e.sendKeys("xxxxxxx");

                driver.findElement(By.id("bytedance-login-submit")).click();
                Thread.sleep(2 * 1000);
                WebElement element =null;
                Actions actions = new Actions(driver);
//                driver.switchTo().frame("tcaptcha_iframe");
//                String originalUrl = Jsoup.parse(driver.getPageSource()).select("[id=validate-big]").first().attr("src");
                moveDistance(element,actions);
                sendArticle(element,actions);
                Thread.sleep(2 * 1000);
                actions.release(element).perform();
            } catch (Exception e) {
                e.printStackTrace();
            }
//        }
            driver.quit();
    }

    public static void moveDistance(WebElement element,Actions actions) throws Exception {
        String originalUrl =driver.findElementByXPath("//*[@id='validate-big']").getAttribute("src");
//                String originalUrl =driver.select("[id=validate-big]").first().attr("src");
//                Thread.sleep(2 * 1000);
        System.out.println(originalUrl);
        long currentTime = System.currentTimeMillis();
        downloadOriginalImg(currentTime, originalUrl, driver.manage().getCookies());
        int distance = calcMoveDistance(currentTime);
        List<MoveEntity> list = getMoveEntity(distance);
        element = driver.findElement(By.className("drag-button"));
        actions.clickAndHold(element).perform();
        int d = 0;
        for (MoveEntity moveEntity : list) {
            actions.moveByOffset(moveEntity.getX(), moveEntity.getY()).perform();
            System.out.println("向右总共移动了:" + (d = d + moveEntity.getX()));
            Thread.sleep(moveEntity.getSleepTime());
        }
        Thread.sleep(3 * 1000);
    }

    public static void sendArticle(WebElement element,Actions actions) throws Exception {

        while (true)
        {
            if(doesButtonExist())
            {
                break;
            }
            else
            {
                driver.findElement(By.className("refresh")).click();
                Thread.sleep(5 * 1000);
                moveDistance(element,actions);
            }
        }
        driver.findElement(By.xpath("//a[text()='发头条']")).click();
        Thread.sleep(2 * 1000);
    }

    public static boolean doesButtonExist()
    {
        try
        {
            driver.findElement(By.xpath("//a[text()='发头条']"));
            return true;
        }
        catch (Exception e)
        {
            return false;
        }
   }
    private static void downloadOriginalImg(long i, String originalUrl, Set<Cookie> cookieSet) throws IOException {
        CookieStore cookieStore = new BasicCookieStore();
        cookieSet.forEach( c -> {
            BasicClientCookie cookie = new BasicClientCookie(c.getName(), c.getValue());
            cookie.setPath(c.getPath());
            cookie.setDomain(c.getDomain());
            cookie.setExpiryDate(c.getExpiry());
            cookie.setSecure(true);
            cookieStore.addCookie(cookie);
        });
        InputStream is = null;
        try {
            SSLContext sslContext = SSLContexts.custom()
                    .loadTrustMaterial(KeyStore.getInstance(KeyStore.getDefaultType())
                            , (chain, authType) -> true).build();
            Registry<ConnectionSocketFactory> socketFactoryRegistry =
                    RegistryBuilder.<ConnectionSocketFactory>create()
                            .register("http", PlainConnectionSocketFactory.INSTANCE)
                            .register("https", new SSLConnectionSocketFactory(sslContext))
                            .build();
            is = HttpClients.custom()
//                    .setProxy(new HttpHost("127.0.0.1", 8888))
                    .setUserAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36")
                    .setDefaultCookieStore(cookieStore)
                    .setConnectionManager(new PoolingHttpClientConnectionManager(socketFactoryRegistry))
                    .build()
                    .execute(new HttpGet(originalUrl))
                    .getEntity().getContent();
            FileUtils.copyInputStreamToFile(is, new File(BASE_PATH + "tencent-original" + i + ".png"));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * 如何判定找到目标滑块位置
     * y轴上至少找到一条长度为30px的白线
     * @throws IOException
     */
    public static int calcMoveDistance(long i) throws IOException {
        BufferedImage fullBI = ImageIO.read(new File(BASE_PATH + "tencent-original" + i + ".png"));
        int sameCnts = 0;
        for(int w = 53 ; w < fullBI.getWidth(); w++){
            int grayLineLen = 0;
            sameCnts = 0;
            for (int h = 30; h < fullBI.getHeight(); h++){
                int[] fullRgb = new int[3];
                fullRgb[0] = (fullBI.getRGB(w, h)  & 0xff0000) >> 16;
                fullRgb[1] = (fullBI.getRGB(w, h)  & 0xff00) >> 8;
                fullRgb[2] = (fullBI.getRGB(w, h)  & 0xff);
//                if((fullRgb[0] >= 0x80 && fullRgb[0] <= 0x99)
//                    && (fullRgb[1] >= 0x80 && fullRgb[1] <= 0x99)
//                        && (fullRgb[2] >= 0x80 && fullRgb[2] <= 0x99))
                int len = fullRgb[0]+fullRgb[1]+fullRgb[2];
                if(len >= 399 && len <= 477)
                {
                    grayLineLen++;
                }
                else {
                    grayLineLen = 0;
                    continue;
                }
//                if ((Math.abs(fullRgb[0] - 0xff) + Math.abs(fullRgb[1] -0xff) + Math.abs(fullRgb[2] - 0xff)) < 40){
//                    whiteLineLen++;
//                } else {
//                    whiteLineLen = 0;
//                    continue;
//                }
                if (grayLineLen >= 12){
                    sameCnts++;
                }
                if (sameCnts >= 2){
                    System.out.println("w="+w+",h="+h);
                    System.out.println("fullRgb[0]="+fullRgb[0]+",fullRgb[1]="+fullRgb[1]+",fullRgb[2]="+fullRgb[2]);
                    System.out.println("Math.abs(fullRgb[0] - 0xff)="+Math.abs(fullRgb[0] - 0xff));
                    System.out.println("Math.abs(fullRgb[1] - 0xff)="+Math.abs(fullRgb[1] - 0xff));
                    System.out.println("Math.abs(fullRgb[2] - 0xff)="+Math.abs(fullRgb[2] - 0xff));
                    System.out.println("找到缺口成功，实际缺口位置x：" + w);
                    System.out.println("应该移动距离：" + (w - START_DISTANCE));
                    //网页显示大小为实际图片大小的一半
                    return w - START_DISTANCE;
                }
            }

        }
        throw new RuntimeException("计算缺口位置失败");
    }
    public static List<MoveEntity> getMoveEntity(int distance){
        List<MoveEntity> list = new ArrayList<>();
        for (int i = 0 ;i < distance; i++){

            MoveEntity moveEntity = new MoveEntity();
            moveEntity.setX(1);
            moveEntity.setY(0);
            moveEntity.setSleepTime(0);
            list.add(moveEntity);
        }
        return list;
    }
    static class MoveEntity{
        private int x;
        private int y;
        private int sleepTime;//毫秒

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }

        public int getSleepTime() {
            return sleepTime;
        }

        public void setSleepTime(int sleepTime) {
            this.sleepTime = sleepTime;
        }
    }
}
