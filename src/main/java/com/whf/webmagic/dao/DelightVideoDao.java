package com.whf.webmagic.dao;

import com.whf.webmagic.entity.DelightArticle;
import com.whf.webmagic.entity.DelightVideo;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DelightVideoDao {
    int insertVideoInfo(DelightVideo delightVideo);
    DelightVideo queryRandVideo();
    List<DelightVideo> queryRandShortVideo();
}
