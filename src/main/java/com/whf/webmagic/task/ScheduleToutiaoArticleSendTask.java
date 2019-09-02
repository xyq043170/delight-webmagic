package com.whf.webmagic.task;

import com.whf.webmagic.dao.DelightArticleDao;
import com.whf.webmagic.dao.DelightWebmagicDao;
import com.whf.webmagic.selenium.toutiao.article.CookieLoginArticle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;


@Component
public class ScheduleToutiaoArticleSendTask {
    private final static Logger LOG = LoggerFactory.getLogger(ScheduleToutiaoArticleSendTask.class);
    private SimpleDateFormat f = new SimpleDateFormat("HH");
    @Autowired
    private DelightWebmagicDao delightWebmagicDao;

    @Autowired
    private DelightArticleDao delightArticleDao;

    @Scheduled(cron="0 0 8,12,18,19,20,21 * * ?")
    private void process(){
        Integer time =Integer.parseInt(f.format(new Date()));
        if(time >= 6)
        {
            LOG.info("ScheduleToutiaoArticleSendTask task start!");
//            CookieLoginArticle.task(delightWebmagicDao,delightArticleDao);
            LOG.info("ScheduleToutiaoArticleSendTask task end!");
        }

    }
}
