package com.whf.webmagic.processor.videomake.bilibili;

import com.whf.webmagic.dao.DelightWebmagicDao;
import com.whf.webmagic.entity.DelightWebmagic;
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
public class BilibiliPageProcessor implements PageProcessor {
    private Site site = Site.me().setRetryTimes(3).setSleepTime(1000).setTimeOut(10000)
              .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36");
    private boolean extractLinks = true;
    private DelightWebmagicDao delightWebmagicDao;

    public BilibiliPageProcessor(){};

    public BilibiliPageProcessor(DelightWebmagicDao delightWebmagicDao){
        this.delightWebmagicDao = delightWebmagicDao;
    }

    private BilibiliPageProcessor(Site site) {
        this.site = site;
    }
    public static int pageNum = 1;
    @Override
    public void process(Page page) {
        if (page.getUrl().regex("www.budejie.com").match()) {
            try {
                //添加页数
                page.addTargetRequests(page.getHtml().xpath("//*[@class=\"j-page\"]/div/a/@href").all());
                pageNum++;
                //模拟get请求
                Request req = new Request();
                req.setMethod(HttpConstant.Method.GET);
                req.setUrl("http://www.budejie.com/pic/"+pageNum);
                page.addTargetRequest(req);
                //添加每页的请求url
                page.addTargetRequests(page.getHtml().xpath("//*[@class=\"j-r-list-c-desc\"]/a/@href").all());

                //模拟get请求
                req = new Request();
                req.setMethod(HttpConstant.Method.GET);
                req.setUrl("http://www.budejie.com/detail-29436936.html");
                page.addTargetRequest(req);
                page.putField("title", page.getHtml().xpath("//li/div[@class='j-r-list-c']/div[@class='j-r-list-c-desc']/h1/text()"));
                page.putField("context",page.getHtml().xpath("//li/div[@class='j-r-list-c']/div[@class='j-r-list-c-img']/img/@src"));
                if (page.getResultItems().get("title")==null || "null".equals(page.getResultItems().get("title").toString())){
                    //skip this page
                    page.setSkip(true);
                }
                else
                {
                    DelightWebmagic delightWebmagic = new DelightWebmagic();
                    delightWebmagic.setId(1);
                    delightWebmagic.setAddressUrl(page.getResultItems().getRequest().getUrl());
                    delightWebmagic.setJpgUrl(page.getResultItems().get("context").toString());
                    delightWebmagic.setName("百思不得姐");
                    delightWebmagic.setTitle(page.getResultItems().get("title").toString());
                    this.delightWebmagicDao.insertData(delightWebmagic);
//                    System.out.println(page.getResultItems().get("title").toString());
//                    System.out.println(page.getResultItems().get("context").toString());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
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
//        Spider.create(new TestPageProcessor()).addUrl("http://www.budejie.com").thread(5).run();
    }

    public static void task(DelightWebmagicDao delightWebmagicDao)
    {
        Spider.create(new BilibiliPageProcessor(delightWebmagicDao)).addUrl("http://www.budejie.com").thread(5).run();
    }
}
