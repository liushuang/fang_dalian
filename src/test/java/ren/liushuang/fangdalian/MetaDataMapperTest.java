package ren.liushuang.fangdalian;

import java.util.Calendar;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import ren.liushuang.fangdalian.dao.MetaDataMapper;
import ren.liushuang.fangdalian.model.MetaData;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MetaDataMapperTest {

    @Autowired
    private MetaDataMapper metaDataMapper;

    @Test
    public void testInsert(){
        MetaData metaData = new MetaData();
        metaData.setUrl("test url");
        metaData.setCreatedTime(Calendar.getInstance().getTime());
        metaData.setBlockNo("test blockNo");
        metaData.setYongdiMianji(1);
        metaData.setJianzhuMianji(2);
        metaData.setStartPrice(100);
        metaData.setFinishPrice(120);
        metaDataMapper.insert(metaData);
    }
}
