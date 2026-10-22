package com.ruoyi.zw.mapper;

import com.ruoyi.zw.domain.ZwKnowledgeBase;
import java.util.List;

public interface ZwKnowledgeBaseMapper {
    List<ZwKnowledgeBase> selectZwKnowledgeBaseList(ZwKnowledgeBase knowledgeBase);
    ZwKnowledgeBase selectZwKnowledgeBaseById(Long kbId);
    int insertZwKnowledgeBase(ZwKnowledgeBase knowledgeBase);
    int updateZwKnowledgeBase(ZwKnowledgeBase knowledgeBase);
    int deleteZwKnowledgeBaseByIds(Long[] kbIds);
    List<ZwKnowledgeBase> selectByKeyword(String keyword);
}
