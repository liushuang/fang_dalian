package ren.liushuang.fangdalian;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import ren.liushuang.fangdalian.service.FetchDataService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FetchDataServiceTest {
    @Autowired
    private FetchDataService fetchDataService;

    @Test
    public void testFetchData() throws Exception {
        fetchDataService.fetchData();
    }
}
