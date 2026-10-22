package com.ruoyi.zw.service;

import com.ruoyi.zw.domain.ZwKnowledgeBase;
import java.util.List;

public interface IZwKnowledgeBaseService {
    List<ZwKnowledgeBase> selectZwKnowledgeBaseList(ZwKnowledgeBase knowledgeBase);
    ZwKnowledgeBase selectZwKnowledgeBaseById(Long kbId);
    int insertZwKnowledgeBase(ZwKnowledgeBase knowledgeBase);
    int updateZwKnowledgeBase(ZwKnowledgeBase knowledgeBase);
    int deleteZwKnowledgeBaseByIds(Long[] kbIds);
    List<ZwKnowledgeBase> suggest(String keyword);
}
