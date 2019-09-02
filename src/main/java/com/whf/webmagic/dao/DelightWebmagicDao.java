package com.whf.webmagic.dao;

import com.whf.webmagic.entity.DelightWebmagic;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DelightWebmagicDao {
    int insertData(DelightWebmagic delightWebmagic);      //新增爬取图片记录
    List<DelightWebmagic> queryUnSavePic();
    int updateSaveStatus(DelightWebmagic delightWebmagic);
    List<DelightWebmagic> queryRandPic();
}
