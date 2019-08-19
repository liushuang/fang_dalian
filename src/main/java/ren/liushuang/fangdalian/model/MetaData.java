package ren.liushuang.fangdalian.model;

import java.util.Date;

import lombok.Data;

@Data
public class MetaData {
    private long id;
    private String title;
    private String url;
    private Date publishTime;
    private Date createdTime;
    private String blockNo;
    private int yongdiMianji;
    private int jianzhuMianji;
    private int startPrice;
    private int finishPrice;
    private String mainContent;
    private String tudiShiyongXingzhi;
}
