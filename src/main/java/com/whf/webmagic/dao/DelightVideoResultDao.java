package com.whf.webmagic.dao;

import com.whf.webmagic.entity.DelightVideo;
import com.whf.webmagic.entity.DelightVideoResult;
import org.springframework.stereotype.Repository;

@Repository
public interface DelightVideoResultDao {
    int insertVideoResultInfo(DelightVideoResult delightVideoResult);
    DelightVideoResult queryDelightVideoResultInfo(DelightVideoResult delightVideoResult);
}
