package ren.liushuang.fangdalian.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kong.unirest.Unirest;
import ren.liushuang.fangdalian.dao.MetaDataMapper;
import ren.liushuang.fangdalian.model.MetaData;

@Service
public class FetchDataService {

    @Autowired
    private MetaDataMapper metaDataMapper;

    private static final String DOMAIN = "http://ggzy.dl.gov.cn";

    public void fetchData() throws Exception {
        for (int i = 1; i < 26; i++) {
            String body = Unirest.get("http://ggzy.dl.gov.cn/TPFront/jyxx/071004/071004003/?pageing=" + i)
                                 .asString().getBody();
            body = body.replaceAll("&", "&amp;");
            body = body.substring(0, body.indexOf("</table>") + "</table>".length());
            Document document = DocumentHelper.parseText(body);
            List<Node> list = document.selectNodes("//a");
            for (Node node : list) {
                MetaData metaData = new MetaData();

                String href = node.valueOf("@href");
                String title = node.getText();
                String noticeHtml = Unirest.get(DOMAIN + href).asString().getBody();
                Date publishTime = getPublishTime(noticeHtml);
                metaData.setPublishTime(publishTime);
                metaData.setUrl(href);
                metaData.setTitle(title);
                metaData.setCreatedTime(Calendar.getInstance().getTime());
                int start = noticeHtml.indexOf("id=\"mainContent\">") + "id=\"mainContent\">".length();
                int end = noticeHtml.indexOf("</div>", start);
                String mainContent = noticeHtml.substring(start, end);
                int index = -1;
                int nextIndex = 0;
                do {
                    index = mainContent.indexOf("大连市地块编号为", index) +1;
                    nextIndex = mainContent.indexOf("大连市地块编号为", index);

                    if (nextIndex == -1) {
                        nextIndex = mainContent.length();
                    }
                    try {
                        handleMainContent(metaData, mainContent.substring(index, nextIndex));
                        metaDataMapper.insert(metaData);
                    }catch (Exception e){
                        System.out.println("error, mainContent=" + mainContent);
                        e.printStackTrace();
                    }
                } while (nextIndex != mainContent.length());
            }
            System.out.println("finish pageing" + i);
        }
    }

    private Date getPublishTime(String noticeHtml) {
        if (noticeHtml.contains("信息时间：")) {
            int start = noticeHtml.indexOf("信息时间：") + "信息时间：".length();
            int end = noticeHtml.indexOf(" ", start);
            String timeStr = noticeHtml.substring(start, end);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            try {
                return simpleDateFormat.parse(timeStr);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private void handleMainContent(MetaData metaData, String mainContent) {

        if(mainContent.contains("的出让地块")){
            String blockNo = mainContent.substring(mainContent.indexOf("地块编号为") + "地块编号为".length(), mainContent.indexOf("的出让地块"));
            metaData.setBlockNo(blockNo);
        }else{
            metaData.setBlockNo("UNKNOWN");
            System.out.println("未找到地块名：" + mainContent);
        }

        if (mainContent.contains("用地面积")) {
            int start = mainContent.indexOf("用地面积：");
            int yongdiMianji = findNumber(mainContent, start);
            metaData.setYongdiMianji(yongdiMianji);
        } else {
            System.out.println("不包含用地面积" + mainContent);
        }

        if (mainContent.contains("建筑规模：")) {
            int start = mainContent.indexOf("建筑规模：");
            int jianzhuMianji = findNumber(mainContent, start);
            metaData.setJianzhuMianji(jianzhuMianji);
        } else {
            System.out.println("不包含建筑规模" + mainContent);
        }

        if (mainContent.contains("起始价")) {
            int start = mainContent.indexOf("起始价：");
            int qishijia = findNumber(mainContent, start);
            metaData.setStartPrice(qishijia);
        } else {
            System.out.println("不包含起始价" + mainContent);
        }

        if (mainContent.contains("成交价格")) {
            int start = mainContent.indexOf("成交价格：");
            int chengjiaojia = findNumber(mainContent, start);
            metaData.setFinishPrice(chengjiaojia);
        } else if (mainContent.contains("未成交")) {
            metaData.setFinishPrice(-1);
        } else {
            System.out.println("不包含成交价格" + mainContent);
        }

        if (mainContent.contains("土地使用性质：") && !mainContent.contains("<iframe")){
            metaData.setTudiShiyongXingzhi(mainContent.substring(
                    mainContent.indexOf("土地使用性质：") + "土地使用性质：".length(),
                    mainContent.indexOf("</p>", mainContent.indexOf("土地使用性质："))));
        }

        metaData.setMainContent(mainContent);
    }

    private int findNumber(String mainContent, int start) {
        int index = start;
        int length = mainContent.length();
        while (index < length) {
            while (!CharUtils.isAsciiNumeric(mainContent.charAt(index))) {
                index++;
            }
            StringBuilder number = new StringBuilder();
            do {
                number.append(mainContent.charAt(index));
                index++;
            }
            while (CharUtils.isAsciiNumeric(mainContent.charAt(index)));
            return Integer.parseInt(number.toString());
        }
        return 0;
    }
}
