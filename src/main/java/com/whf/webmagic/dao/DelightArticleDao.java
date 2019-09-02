package com.whf.webmagic.dao;

import com.whf.webmagic.entity.DelightArticle;
import com.whf.webmagic.entity.DelightWebmagic;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DelightArticleDao {
    int updateArticleNum(DelightArticle delightArticle);
    DelightArticle queryArticleNum(DelightArticle delightArticle);
}
