package com.whf.webmagic;

import com.whf.webmagic.dao.DelightArticleDao;
import com.whf.webmagic.dao.DelightVideoDao;
import com.whf.webmagic.dao.DelightVideoResultDao;
import com.whf.webmagic.dao.DelightWebmagicDao;
import com.whf.webmagic.processor.videomake.VideoCutMergeProcessor;
import com.whf.webmagic.processor.videomake.VideoSaveProcessor;
import com.whf.webmagic.processor.videomake.VideoShortMergeProcessor;
import com.whf.webmagic.selenium.aiqiyi.video.AiqiyiCookieLoginVideo;
import com.whf.webmagic.selenium.toutiao.article.CookieLoginArticle;
import com.whf.webmagic.selenium.toutiao.video.CookieLoginVideo;
import com.whf.webmagic.selenium.wangyi.article.WangyiCookieLoginArticle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class WebMagicInit implements CommandLineRunner {
    @Autowired
    private DelightWebmagicDao delightWebmagicDao;

    @Autowired
    private DelightArticleDao delightArticleDao;

    @Autowired
    private DelightVideoDao delightVideoDao;

    @Autowired
    private DelightVideoResultDao delightVideoResultDao;

    public WebMagicInit() {
    }

    @Override
    public void run(String... args) throws Exception {
//        BsbdjPageProcessor.task(delightWebmagicDao);
//        CookieLoginArticle.task(delightWebmagicDao,delightArticleDao);
//        CookieLoginVideo.task(delightWebmagicDao,delightArticleDao,delightVideoResultDao);
//        AiqiyiCookieLoginVideo.task(delightWebmagicDao,delightArticleDao,delightVideoResultDao);
//        VideoRenameProcessor.task();
//        VideoSaveProcessor.task(delightVideoDao);
        VideoShortMergeProcessor.task(delightVideoDao,delightArticleDao,delightVideoResultDao);
//        WangyiCookieLoginArticle.task(delightWebmagicDao,delightArticleDao);

    }
}
