package com.whf.webmagic.processor.webmagic.video;

import com.alibaba.fastjson.JSON;
import com.whf.webmagic.dao.DelightWebmagicDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.model.HttpRequestBody;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Selector;
import us.codecraft.webmagic.utils.HttpConstant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The extension to PageProcessor for page model extractor.
 *
 * @author code4crafter@gmail.com <br>
 * @since 0.2.0
 */
@Component
public class VideoV1PageProcessor implements PageProcessor {
    private final static Logger LOG = LoggerFactory.getLogger(VideoV1PageProcessor.class);
    private Site site = Site.me().setRetryTimes(3).setSleepTime(1000).setTimeOut(10000)
            .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36");
    private boolean extractLinks = true;
    private DelightWebmagicDao delightWebmagicDao;

    public VideoV1PageProcessor(){};

    public VideoV1PageProcessor(DelightWebmagicDao delightWebmagicDao){
        this.delightWebmagicDao = delightWebmagicDao;
    }

    private VideoV1PageProcessor(Site site) {
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

        if (page.getUrl().regex("http://www.v1.cn/gaoxiao").match()) {
            try {
                //添加页数
//                page.addTargetRequests(page.getHtml().xpath("//*[@class='pagination']/ul/li/a/@href").all());
                for (int i = 0; i < 100; i++) {
                    pageNum++;
                    if(pageNum > 1)
                    {
                        //模拟get请求
                        Request req = new Request();
                        req.setMethod(HttpConstant.Method.POST);
                        req.setUrl("http://www.v1.cn/index/getList4Ajax");
                        Map<String,Object> map = new HashMap<>();
                        map.put("cid",6);
                        map.put("page",pageNum);
                        req.setRequestBody(HttpRequestBody.form(map,"UTF-8"));
                        page.addTargetRequest(req);
                        //添加每页的请求url
                        page.addTargetRequests(page.getHtml().xpath("//div[@class='pic']/a/@href").all());

                        //模拟get请求
                        req = new Request();
                        req.setMethod(HttpConstant.Method.GET);
                        req.setUrl("http://www.v1.cn/video/15659123.shtml");
                        page.addTargetRequest(req);
                        page.putField("title",page.getHtml().xpath("//*[@class='mainBox shadowBox']/div[@class='videoTop']/h2/text()"));
//                        page.putField("videoUrl",page.getHtml().xpath("//param[@name='FlashVars']/@value"));

                        if (page.getResultItems().get("title")==null || "null".equals(page.getResultItems().get("title").toString())){
                            //skip this page
                            page.setSkip(true);
                        }
                        else
                        {
//                        DelightWebmagic delightWebmagic = new DelightWebmagic();
//                        delightWebmagic.setId(1);
//                        delightWebmagic.setAddressUrl(page.getResultItems().getRequest().getUrl());
//                        delightWebmagic.setJpgUrl(page.getResultItems().get("context").toString());
//                        delightWebmagic.setName("百思不得姐");
//                        delightWebmagic.setTitle(page.getResultItems().get("title").toString());
//                        this.delightWebmagicDao.insertData(delightWebmagic);
                            LOG.info(page.getResultItems().get("title").toString());
//                            LOG.info(page.getResultItems().get("videoUrl").toString());
                            String html = page.getHtml().toString();
                    LOG.info("html="+html);
////                    // String source = "<a title=中国体育报 href=''>aaa</a><a title='北京日报' href=''>bbb</a>";
//                            List<String> list = match(html, "embed", "flashvars");
//                            LOG.info("list size="+list.size()+"");
//                            LOG.info("list="+ JSON.toJSONString(list));
                            String value = html.substring(html.indexOf("<param name=\"FlashVars\" value=\""),30);
                            LOG.info("value="+ value);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static List<String> match(String source, String element, String attr) {
        List<String> result = new ArrayList<String>();
        String reg = "<" + element + "[^<>]*?\\s" + attr + "=['\"]?(.*?)['\"]?\\s.*?>";
        Matcher m = Pattern.compile(reg).matcher(source);
        while (m.find()) {
            String r = m.group(1);
            result.add(r);
        }
        return result;
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

    public static void main(String[] args) throws InterruptedException {
        Spider.create(new VideoV1PageProcessor()).addUrl("http://www.v1.cn/gaoxiao").thread(5).run();
    }

    public static void task(DelightWebmagicDao delightWebmagicDao)
    {
        Spider.create(new VideoV1PageProcessor(delightWebmagicDao)).addUrl("http://www.6vhao.tv/dy1/index.html").thread(5).run();
    }
}
