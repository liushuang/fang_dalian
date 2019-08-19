package ren.liushuang.fangdalian.dao;

import org.apache.ibatis.annotations.Mapper;

import ren.liushuang.fangdalian.model.MetaData;

@Mapper
public interface MetaDataMapper {
    int insert(MetaData metaData);
}
