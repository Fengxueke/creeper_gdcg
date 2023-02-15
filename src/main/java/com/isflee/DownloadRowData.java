package com.isflee;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.utils.URIBuilder;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

//GET /freecms/rest/v1/notice/selectInfoMoreChannel.do?&siteId=cd64e06a-21a7-4620-aebc-0576bab7e07a&channel=3b49b9ba-48b6-4220-9e8b-eb89f41e9d66&currPage=1&pageSize=10&noticeType=&regionCode=undefined&verifyCode=3018&title=&openTenderCode=&purchaser=&purchaseNature=&operationStartTime=&operationEndTime=&selectTimeName=noticeTime HTTP/1.1
//Accept: */*
//Accept-Encoding: gzip, deflate, br
//Accept-Language: zh-CN,zh;q=0.9,en-US;q=0.8,en;q=0.7
//Connection: keep-alive
//Content-Type: application/json;charset=utf-8
//Cookie: JSESSIONID=A8193976A94CD5C39D0219C796004A1F; the_codes=440001; the_codesIndex=%E5%B9%BF%E4%B8%9C%E7%9C%81%E6%9C%AC%E7%BA%A7; is_read=1
//Host: gdgpo.czt.gd.gov.cn
//Referer: https://gdgpo.czt.gd.gov.cn/cms-gd/site/guangdong/dzmcgg/index.html
//Sec-Fetch-Dest: empty
//Sec-Fetch-Mode: cors
//Sec-Fetch-Site: same-origin
//User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36
//X-Requested-With: XMLHttpRequest
//sec-ch-ua: "Not?A_Brand";v="8", "Chromium";v="108", "Google Chrome";v="108"
//sec-ch-ua-mobile: ?0
//sec-ch-ua-platform: "Windows"

@Slf4j
//1. 用于爬取和保存raw data的类
public class DownloadRowData {

    public static final int pageSize = 10;
    private Map<String, String> cookies = null;


    public void downloadRawData(Properties properties) throws ParseException, IOException, URISyntaxException {

        //设置文件保存路径
        String allRawDataPath = Paths.get(properties.getProperty("all.raw.data.path")).toString();
        //获取验证码
        VerifyCodeData verifyCodeData = getVerifyCodeValue(properties);
        //首次尝试
        DzmzggBean dzmzggFirstBean = getDzmzggRawData(verifyCodeData, "1", properties);

        if ("200".equals(dzmzggFirstBean.getCode().trim())) {
            //根据首次尝试的结果，获取总的结果条数，并逐页进行获取
            int maxPageIndex = Integer.parseInt(dzmzggFirstBean.getTotal()) / pageSize + 1;
            log.info("maxPageIndex:" + maxPageIndex);
            for (int i = 1; i <= maxPageIndex; i++) {
                //对于每个页面，访问失败会有三次重试的机会
                for (int retryTime = 0; retryTime <= 3; retryTime++) {
                    //访问页面并获取数据
                    DzmzggBean dzmzggBean = getDzmzggRawData(verifyCodeData, i + "", properties);
                    if ("200".equals(dzmzggBean.getCode().trim())) {
                        //save data
                        //保存数据
                        List<DzmzggDataBean> dzmzggDataBeans = dzmzggBean.getData();
                        byte[] rawJsonLineData = new Gson().toJson(dzmzggDataBeans).getBytes(StandardCharsets.UTF_8);
                        MyFileUtils.saveFile(rawJsonLineData, allRawDataPath, i + "-raw.json");
                        break;
                    } else {
                        //update code
                        verifyCodeData = getVerifyCodeValue(properties);
                        log.warn("url of maxPageIndex have been retried, maxPageIndex is:" + maxPageIndex);
                        if (retryTime == 3) {
                            log.error("url retry times more than 3, maxPageIndex is:" + maxPageIndex);
                        }
                    }
                }

                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } else {
            log.error("first try error！");
        }

    }


    //获取验证码图片，将验证码图片转换成文字
    public VerifyCodeData getVerifyCodeValue(Properties properties) throws IOException {
        //获取配置
        String verifyCodeImagePath = Paths.get(properties.getProperty("verify.code.image.path")).toString();
        String firstPageUrl = properties.getProperty("index.url");
        String verifyCodeUrl = properties.getProperty("verify.code.url");

        Connection conn = createConnectionFakeHeader(firstPageUrl);
        Connection.Response res = conn.ignoreContentType(true).method(Connection.Method.GET).execute();
        cookies = res.cookies();
        for(Map.Entry<String, String> entry : cookies.entrySet()){
            log.info(entry.getKey() + "-" + entry.getValue());
        }

        //save image
        Connection imageUrlConn = Jsoup.connect(verifyCodeUrl);
        imageUrlConn.cookies(cookies);
        imageUrlConn.timeout(5 * 1000);
        Connection.Response response = imageUrlConn.ignoreContentType(true).execute();
        byte [] image = response.bodyAsBytes();
        Map<String, String> imageCookie = response.cookies();
        for(Map.Entry<String, String> entry : imageCookie.entrySet()){
            log.info(entry.getKey() + "-" + entry.getValue());
        }
        MyFileUtils.saveFile(image, verifyCodeImagePath, "1.jfif");
        log.info("verifyImageOcr2Text: " + verifyImageOcr2Text(properties));
        VerifyCodeData verifyCodeData = new VerifyCodeData();
        //使用python和ddddocr对验证码图片进行文字转换
        verifyCodeData.setVerifyCode(verifyImageOcr2Text(properties));
        verifyCodeData.setCookies(cookies);
        return verifyCodeData;
    }

    private Connection createConnectionFakeHeader(String url) {
        Connection conn = Jsoup.connect(url);
        conn.header("Accept", "*/*").header("Accept-Encoding", "gzip, deflate, br");
        conn.header("Accept-Language", "zh-CN,zh;q=0.9,en-US;q=0.8,en;q=0.7").header("Connection", "keep-alive");
        conn.header("Content-Type", "application/json;charset=utf-8");
        conn.header("Host", "gdgpo.czt.gd.gov.cn").header("Referer", url);
        conn.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36")
                .header("X-Requested-With", "XMLHttpRequest");
        return conn;
    }

    private DzmzggBean getDzmzggRawData(VerifyCodeData verifyCodeData, String currPage, Properties properties) throws URISyntaxException, IOException {
        String baseUrl = properties.getProperty("raw.data.url");
        String operationStartTime = properties.getProperty("raw.data.url.operationStartTime");
        String operationEndTime = properties.getProperty("raw.data.url.operationEndTime");
        //构造获取数据的url
        String url = new URIBuilder(baseUrl)
                .addParameter("siteId","cd64e06a-21a7-4620-aebc-0576bab7e07a")
                .addParameter("channel","3b49b9ba-48b6-4220-9e8b-eb89f41e9d66")
                .addParameter("currPage",currPage)
                .addParameter("pageSize","10")
                .addParameter("noticeType","202022,202023,202111,00107E,001076")
                .addParameter("regionCode","")
                .addParameter("verifyCode", verifyCodeData.getVerifyCode().trim())
                .addParameter("title","")
                .addParameter("purchaseManner","")
                .addParameter("openTenderCode","")
                .addParameter("purchaser","")
                .addParameter("purchaseNature","")
                .addParameter("operationStartTime",operationStartTime)
                .addParameter("operationEndTime",operationEndTime)
                .addParameter("selectTimeName","noticeTime")
                .build().toString();
        log.info(url);
        //添加相应头
        Connection conn = createConnectionFakeHeader(url);
        conn.cookies(verifyCodeData.getCookies());
        conn.timeout(60 * 1000);
        Connection.Response res = conn.ignoreContentType(true).method(Connection.Method.GET).execute();
        Gson gson = new Gson();
        System.out.println(res.body());
        DzmzggBean dzmzggBean = gson.fromJson(res.body(), DzmzggBean.class);
        return dzmzggBean;
    }

    public String verifyImageOcr2Text(Properties properties){
        String script = properties.getProperty("verify.code.orc.2.text.script");
        String verifyCode = "";
        Runtime runtime = Runtime.getRuntime();
        try {
            Process process = runtime.exec("python " + script);
            InputStream in = process.getInputStream();
            verifyCode = MyFileUtils.inputByteReader(in);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return verifyCode;
    }


    public static void main(String[] args) throws IOException, ParseException, URISyntaxException {
        Properties properties = new Properties();
        properties.load(new FileInputStream(new File("src/main/resources/app.properties")));
        DownloadRowData downloadRowData = new DownloadRowData();
        downloadRowData.downloadRawData(properties);
    }
}
