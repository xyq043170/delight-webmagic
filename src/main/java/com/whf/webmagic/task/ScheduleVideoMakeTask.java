package com.whf.webmagic.task;

import com.whf.webmagic.dao.DelightArticleDao;
import com.whf.webmagic.dao.DelightVideoDao;
import com.whf.webmagic.dao.DelightVideoResultDao;
import com.whf.webmagic.dao.DelightWebmagicDao;
import com.whf.webmagic.processor.videomake.VideoCutMergeProcessor;
import com.whf.webmagic.processor.videomake.VideoShortMergeProcessor;
import com.whf.webmagic.selenium.toutiao.article.CookieLoginArticle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;


@Component
public class ScheduleVideoMakeTask {
    private final static Logger LOG = LoggerFactory.getLogger(ScheduleVideoMakeTask.class);
    private SimpleDateFormat f = new SimpleDateFormat("HH");
    @Autowired
    private DelightVideoDao delightVideoDao;

    @Autowired
    private DelightArticleDao delightArticleDao;

    @Autowired
    private DelightVideoResultDao delightVideoResultDao;

    @Scheduled(cron="0 0/30 * * * ?")
    private void process(){
        LOG.info("ScheduleToutiaoSendTask task start!");
        VideoShortMergeProcessor.task(delightVideoDao,delightArticleDao,delightVideoResultDao);
        LOG.info("ScheduleToutiaoSendTask task end!");
    }
}
