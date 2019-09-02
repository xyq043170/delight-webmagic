package com.whf.webmagic.processor.webmagic.pic;

import com.whf.webmagic.dao.DelightWebmagicDao;
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
public class HaHaPageProcessor implements PageProcessor {
    private Site site = Site.me().setRetryTimes(3).setSleepTime(1000).setTimeOut(10000)
              .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36");
    private boolean extractLinks = true;
    private DelightWebmagicDao delightWebmagicDao;

    public HaHaPageProcessor(){};

    public HaHaPageProcessor(DelightWebmagicDao delightWebmagicDao){
        this.delightWebmagicDao = delightWebmagicDao;
    }

    private HaHaPageProcessor(Site site) {
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
        if (page.getUrl().regex("https://www.hahamx.cn/pic").match()) {
            try {
                //添加页数
//                page.addTargetRequests(page.getHtml().xpath("//*[@class=\"fl main\"]/div/a/@href").all());
//                pageNum++;
//                //模拟get请求
//                Request req = new Request();
//                req.setMethod(HttpConstant.Method.GET);
//                req.setUrl("https://www.hahamx.cn/pic/new/"+pageNum);
//                page.addTargetRequest(req);
//                //添加每页的请求url
//                page.addTargetRequests(page.getHtml().xpath("//*[@class=\"joke-list-item-main\"]/div/a/@href").all());

                //模拟get请求
                Request req = new Request();
                req.setMethod(HttpConstant.Method.GET);
                req.setUrl("https://www.hahamx.cn/joke/2868253");
//                req.setRequestBody(HttpRequestBody.json("{r: 'login'}", "utf-8"));
                page.addTargetRequest(req);
                page.putField("title", page.getHtml().xpath("//div[@class='f1 main']/div[@class='joke-main']/div/p/text()"));
                page.putField("context",page.getHtml().xpath("//div[@class='f1 main']/div[@class='joke-main']/div/img/@src"));
                System.out.println(page.getResultItems().getRequest().getUrl().toString());
                System.out.println(page.getResultItems().get("title").toString());
                System.out.println(page.getResultItems().get("context").toString());
                if (page.getResultItems().get("title")==null || "null".equals(page.getResultItems().get("title").toString())){
                    //skip this page
                    page.setSkip(true);
                }
                else
                {
//                    DelightWebmagic delightWebmagic = new DelightWebmagic();
//                    delightWebmagic.setId(2);
//                    delightWebmagic.setAddressUrl(page.getResultItems().getRequest().getUrl());
//                    delightWebmagic.setJpgUrl(page.getResultItems().get("context").toString());
//                    delightWebmagic.setName("哈哈网");
//                    delightWebmagic.setTitle(page.getResultItems().get("title").toString());
//                    this.delightWebmagicDao.insertData(delightWebmagic);
                    System.out.println(page.getResultItems().get("title").toString());
                    System.out.println(page.getResultItems().get("context").toString());
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
        Spider.create(new HaHaPageProcessor()).addUrl("https://www.hahamx.cn/pic").thread(1).run();
    }

    public static void task(DelightWebmagicDao delightWebmagicDao)
    {
        Spider.create(new HaHaPageProcessor(delightWebmagicDao)).addUrl("https://www.hahamx.cn/pic").thread(5).run();
    }
}
