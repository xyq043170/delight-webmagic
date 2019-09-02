package com.whf.webmagic.processor.webmagic.video;

import com.whf.webmagic.constant.ConstantKey;
import com.whf.webmagic.dao.DelightWebmagicDao;
import com.whf.webmagic.entity.DelightWebmagic;
import com.whf.webmagic.selenium.aiqiyi.video.AiqiyiCookieLoginVideo;
import org.openqa.selenium.chrome.ChromeDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Selector;
import us.codecraft.webmagic.utils.HttpConstant;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The extension to PageProcessor for page model extractor.
 *
 * @author code4crafter@gmail.com <br>
 * @since 0.2.0
 */
@Component
public class Video6VPageProcessor implements PageProcessor {
    private final static Logger LOG = LoggerFactory.getLogger(Video6VPageProcessor.class);
    private Site site = Site.me().setRetryTimes(3).setSleepTime(1000).setTimeOut(10000)
            .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36");
    private boolean extractLinks = true;
    private DelightWebmagicDao delightWebmagicDao;

    public static ChromeDriver driver = null;
    static {
        System.setProperty("webdriver.chrome.driver", ConstantKey.CHROM_DRIVER);
    }

    public Video6VPageProcessor(){};

    public Video6VPageProcessor(DelightWebmagicDao delightWebmagicDao){
        this.delightWebmagicDao = delightWebmagicDao;
    }

    private Video6VPageProcessor(Site site) {
        this.site = site;
    }
    public static int pageNum = 1;
    @Override
    public void process(Page page) {
//        for (PageModelExtractor pageModelExtractor : pageModelExtractorList) {
//            if (extractLinks) {
//                extractLinks(page, pageModelExtractor.getHelpUrlRegionSelector(), pageModelExtractor.getHelpUrlPatterns());
//                extractLinks(page, pageModelExtractor.getTargetUrlRegionSelector(), pageModelExtractor.getTargetUrlPatterns());
//            }
//            Object process = pageModelExtractor.process(page);
//            if (process == null || (process instanceof List && ((List) process).size() == 0)) {
//                continue;
//            }
//            postProcessPageModel(pageModelExtractor.getClazz(), process);
//            page.putField(pageModelExtractor.getClazz().getCanonicalName(), process);
//        }
//        if (page.getResultItems().getAll().size() == 0) {
//            page.getResultItems().setSkip(true);
//        }

        http://www.budejie.com/pic/
//        if (page.getUrl().regex("^http://www\\.budejie\\.com/pic/").match()) {
        if (page.getUrl().regex("http://www.6vhao.tv/dy1/index.html").match()) {
            try {
                //添加页数
                page.addTargetRequests(page.getHtml().xpath("//*[@class='pagebox']/a/@href").all());
                pageNum++;
                if(pageNum > 1)
                {
                    //模拟get请求
                    Request req = new Request();
                    req.setMethod(HttpConstant.Method.GET);
                    req.setUrl("http://www.6vhao.tv/dy1/index_"+pageNum+".html");
                    page.addTargetRequest(req);
                    //添加每页的请求url
                    page.addTargetRequests(page.getHtml().xpath("//*[@class='listInfo']/h3/a/@href").all());

                    //模拟get请求
                    req = new Request();
                    req.setMethod(HttpConstant.Method.GET);
                    req.setUrl("http://www.6vhao.tv/dy1/2011-08-21/15839.html");
                    page.addTargetRequest(req);
                    boolean status1 = judgeTag(page,"//span[@id='showpf']/following-sibling::span[1]/li[@class='current-rating3']");
                    boolean status2 = judgeTag(page,"//span[@id='showpf']/following-sibling::span[1]/li[@class='current-rating4']");
                    LOG.info("status1="+status1);
                    LOG.info("status2="+status2);
                    if(status1 || status2)
                    {
                        LOG.info("start get downAddress");
                        page.putField("downAddress",page.getHtml().xpath("//div[@id='text']/table/tbody/tr[1]/td/a/@href"));
                        String downAddress = page.getResultItems().get("downAddress").toString();
                        System.out.println("downAddress="+downAddress);
                    }
                    else
                    {
                        page.setSkip(true);
                    }

//                    page.putField("context",page.getHtml().xpath("//li/div[@class='j-r-list-c']/div[@class='j-r-list-c-img']/img/@src"));
//                    if (page.getResultItems().get("title")==null || "null".equals(page.getResultItems().get("title").toString())){
//                        //skip this page
//                        page.setSkip(true);
//                    }
//                    else
//                    {
//                        DelightWebmagic delightWebmagic = new DelightWebmagic();
//                        delightWebmagic.setId(1);
//                        delightWebmagic.setAddressUrl(page.getResultItems().getRequest().getUrl());
//                        delightWebmagic.setJpgUrl(page.getResultItems().get("context").toString());
//                        delightWebmagic.setName("百思不得姐");
//                        delightWebmagic.setTitle(page.getResultItems().get("title").toString());
//                        this.delightWebmagicDao.insertData(delightWebmagic);
////                    LOG.info(page.getResultItems().get("title").toString());
////                    LOG.info(page.getResultItems().get("context").toString());
//                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private boolean judgeTag(Page page,String tag)
    {
        try
        {
            page.getHtml().xpath(tag);
            return true;
        }catch (Exception e)
        {
            return false;
        }
    }

    private void extractLinks(Page page, Selector urlRegionSelector, List<Pattern> urlPatterns) {
        List<String> links;
        if (urlRegionSelector == null) {
            links = page.getHtml().links().all();
        } else {
            links = page.getHtml().selectList(urlRegionSelector).links().all();
        }
        for (String link : links) {
            for (Pattern targetUrlPattern : urlPatterns) {
                Matcher matcher = targetUrlPattern.matcher(link);
                if (matcher.find()) {
                    page.addTargetRequest(new Request(matcher.group(0)));
                }
            }
        }
    }

    @Override
    public Site getSite() {
        return site;
    }

    public boolean isExtractLinks() {
        return extractLinks;
    }

    public void setExtractLinks(boolean extractLinks) {
        this.extractLinks = extractLinks;
    }

    public static void main(String[] args) {
        Spider.create(new Video6VPageProcessor()).addUrl("http://www.6vhao.tv/dy1/index.html").thread(5).run();
    }

    public static void task(DelightWebmagicDao delightWebmagicDao)
    {
        Spider.create(new Video6VPageProcessor(delightWebmagicDao)).addUrl("http://www.6vhao.tv/dy1/index.html").thread(5).run();
    }
}
