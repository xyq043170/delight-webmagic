package com.whf.webmagic.task;

import com.whf.webmagic.dao.DelightArticleDao;
import com.whf.webmagic.dao.DelightVideoResultDao;
import com.whf.webmagic.dao.DelightWebmagicDao;
import com.whf.webmagic.selenium.aiqiyi.video.AiqiyiCookieLoginVideo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;


@Component
public class ScheduleAiqiyiVideoSendTask {
    private final static Logger LOG = LoggerFactory.getLogger(ScheduleAiqiyiVideoSendTask.class);
    private SimpleDateFormat f = new SimpleDateFormat("HH");
    @Autowired
    private DelightWebmagicDao delightWebmagicDao;

    @Autowired
    private DelightArticleDao delightArticleDao;

    @Autowired
    private DelightVideoResultDao delightVideoResultDao;

    @Scheduled(cron="0 20 8,12,18,20 * * ?")
    private void process(){
        Integer time =Integer.parseInt(f.format(new Date()));
        if(time >= 6)
        {
            LOG.info("ScheduleToutiaoVideoSendTask task start!");
//            AiqiyiCookieLoginVideo.task(delightWebmagicDao,delightArticleDao,delightVideoResultDao);
            LOG.info("ScheduleToutiaoVideoSendTask task end!");
        }

    }
}
