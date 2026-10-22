package com.ruoyi.zw.service.impl;

import com.ruoyi.zw.domain.ZwKnowledgeBase;
import com.ruoyi.zw.mapper.ZwKnowledgeBaseMapper;
import com.ruoyi.zw.service.IZwKnowledgeBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ZwKnowledgeBaseServiceImpl implements IZwKnowledgeBaseService {

    @Autowired
    private ZwKnowledgeBaseMapper knowledgeBaseMapper;

    @Override
    public List<ZwKnowledgeBase> selectZwKnowledgeBaseList(ZwKnowledgeBase knowledgeBase) {
        return knowledgeBaseMapper.selectZwKnowledgeBaseList(knowledgeBase);
    }

    @Override
    public ZwKnowledgeBase selectZwKnowledgeBaseById(Long kbId) {
        return knowledgeBaseMapper.selectZwKnowledgeBaseById(kbId);
    }

    @Override
    public int insertZwKnowledgeBase(ZwKnowledgeBase knowledgeBase) {
        knowledgeBase.setDelFlag("0");
        return knowledgeBaseMapper.insertZwKnowledgeBase(knowledgeBase);
    }

    @Override
    public int updateZwKnowledgeBase(ZwKnowledgeBase knowledgeBase) {
        return knowledgeBaseMapper.updateZwKnowledgeBase(knowledgeBase);
    }

    @Override
    public int deleteZwKnowledgeBaseByIds(Long[] kbIds) {
        return knowledgeBaseMapper.deleteZwKnowledgeBaseByIds(kbIds);
    }

    @Override
    public List<ZwKnowledgeBase> suggest(String keyword) {
        return knowledgeBaseMapper.selectByKeyword(keyword);
    }
}
