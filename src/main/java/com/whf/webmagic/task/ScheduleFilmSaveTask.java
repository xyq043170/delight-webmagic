package com.whf.webmagic.task;

import com.whf.webmagic.dao.DelightArticleDao;
import com.whf.webmagic.dao.DelightVideoDao;
import com.whf.webmagic.dao.DelightVideoResultDao;
import com.whf.webmagic.processor.videomake.VideoCutMergeProcessor;
import com.whf.webmagic.processor.videomake.VideoSaveProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;


@Component
public class ScheduleFilmSaveTask {
    private final static Logger LOG = LoggerFactory.getLogger(ScheduleFilmSaveTask.class);
    private SimpleDateFormat f = new SimpleDateFormat("HH");
    @Autowired
    private DelightVideoDao delightVideoDao;

    @Autowired
    private DelightArticleDao delightArticleDao;

    @Autowired
    private DelightVideoResultDao delightVideoResultDao;

    @Scheduled(cron="0 0 0 * * ?")
    private void process(){
        Integer time =Integer.parseInt(f.format(new Date()));
        if(time >= 6)
        {
            LOG.info("ScheduleFilmSaveTask task start!");
//            VideoSaveProcessor.task(delightVideoDao);
            LOG.info("ScheduleFilmSaveTask task end!");
        }

    }
}
