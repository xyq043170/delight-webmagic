package com.whf.webmagic.processor.videomake;

import com.whf.webmagic.constant.ConstantKey;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class testVideo {
    private final static Logger LOG = LoggerFactory.getLogger(testVideo.class);
    String URl_Id = null;
    int count = 0;// 计数
    // 根据主页获取每个视频的id
    static {
        System.setProperty("webdriver.chrome.driver", ConstantKey.CHROM_DRIVER);
    }

    public void DownHtml(String url) throws InterruptedException {
        ArrayList<String> alURl = new ArrayList<>();//视频id集合
        ArrayList<String> alMP4 = new ArrayList<>();//视频下载URL集合
        // 实例化一个浏览器对象
        WebDriver driver = new ChromeDriver();
        driver.get(url);
        Thread.sleep(8000);// 休眠等待页面加载
        List<WebElement> elements = driver.findElements(By.cssSelector("li.item,goWork"));// 获取到每个视频的模块
        System.out.println(elements.size());
        //获取每个URl的ID
        for (WebElement we : elements) {
            String ids = we.getAttribute("data-id").toString();// 获取模块的data-id的属性值
            alURl.add("https://www.iesdouyin.com/share/video/" + ids);
        }
        driver.get("http://douyin.iiilab.com/");// 打开可以将每个视频链接转化成可以下载的链接的网页
        Thread.sleep(8000);// 休眠等待页面加载
        //获取可以下载的url
        for (int i = 0; i < alURl.size(); i++) {
            LOG.info("i="+alURl.get(i));
//            driver.findElement(By.cssSelector("input.form-control.link-input")).clear();// 清空这个输入框
//            driver.findElement(By.cssSelector("input.form-control.link-input")).sendKeys(alURl.get(i));// 将需要转换的链接放入该输入框中
//            driver.findElement(By.cssSelector("button.btn.btn-default")).click();// 点击解析
//            Thread.sleep(4000);// 休眠等待页面加载
//            alMP4.add( driver.findElement(By.cssSelector("a.btn.btn-success")).getAttribute("href").toString());// 获取解析后的链接
        }
        driver.close();
        //下载
        for (int i = 0; i < alMP4.size(); i++) {
            DownloadFile df = new DownloadFile();
            df.run(alMP4.get(i));
        }
    }


    /**
     * 入口
     *
     * @param id
     */
    public static void main(String[] args) {
        testVideo dl = new testVideo();
        String ID = "80602533314";// 人物ID
        try {
            dl.DownHtml("https://www.douyin.com/share/user/" + ID + "/?share_type=link");
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
