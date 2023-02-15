package com.isflee;

//https://gdgpo.czt.gd.gov.cn/cms-gd/site/guangdong/dzmcgg/index.html
//数据接口
//https://gdgpo.czt.gd.gov.cn/freecms/rest/v1/notice/selectInfoMoreChannel.do?&siteId=cd64e06a-21a7-4620-aebc-0576bab7e07a&channel=3b49b9ba-48b6-4220-9e8b-eb89f41e9d66&currPage=1&pageSize=10&noticeType=&regionCode=undefined&verifyCode=3018&title=&openTenderCode=&purchaser=&purchaseNature=&operationStartTime=&operationEndTime=&selectTimeName=noticeTime
//验证码接口
//https://gdgpo.czt.gd.gov.cn/cms-gd/verify/verifyCode.do?createTypeFlag=n&name=notice


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;



@Slf4j
//1. 用于处理raw data的类
public class HandleRawData {


    private final static String TB11 = "产品名称-技术规格-备注-数量-单价（元）-金额（元）";
    private final static String TB12 = "服务描述-数量-单位-供应商报价(元)-是否中标";
    private final static String TB13 = "供应商名称-供应商报价(元)-报价时间";
    private final static String TB14 = "排名-供应商名称-总报价(元)-报价时间";
    private final static String TB15 = "排名-供应商-品牌-型号-数量-单价-报价金额(元)-报价时间";
    private final static String TB21 = "编号-采购品目-名称-品牌-型号-数量-主要技术参数-金额(元)";
    private final static String TB22 = "供应商名称-原因";
    private final static String TB23 = "序号-单价(元)-报价金额(元)-商品型号-数量-参数";
    private final static String TB31 = "名称-数量-单位-供应商名称-供应商报价(元)";
    private final static String TB32 = "序号-需求名称-需求选项";

    public void handleRawData(Properties properties) throws ParseException, IOException, URISyntaxException {

        String allRawDataPath = Paths.get(properties.getProperty("all.raw.data.path")).toString();

        List<DzmzggDataBean> allData = new ArrayList<>();
        //resultDataList用于存放处理完的所有数据，
        //请注意设置配置文件的raw.data.url.operationStartTime和raw.data.url.operationEndTime，即发布开始日期和发布结束日期，
        //来防止内存溢出。
        //测试过爬取22年一年的数据，
        List<DzmzggResultData> resultDataList = new ArrayList<>();
        Map<String, String> gdAndRegionMapping = createGdMapping(properties);
        int emptyDataCount = 0;
        List<String> chengJiaoJinE_error_log = new ArrayList<>();

        File files [] = new File(allRawDataPath).listFiles();
        Gson convertRawJsonData = new Gson();
        //解析每一个*-raw.json文件
        for(File file : files){
            try(
                    InputStream rawJsonDataInput = new FileInputStream(file);
            ){
                String rawJsonData = MyFileUtils.inputByteReader(rawJsonDataInput);
                allData = convertRawJsonData.fromJson(rawJsonData, new TypeToken<List<DzmzggDataBean>>(){}.getType());
                //核心处理代码
                handelRawDataCore(allData, resultDataList, gdAndRegionMapping, emptyDataCount, properties);
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        //最后保存数据
        if(resultDataList.size() > 0){
            String dzmzggResultDataObjectProperties=Arrays.stream(resultDataList.get(0).getClass().getDeclaredFields()).map(s -> s.getName()).collect(Collectors.joining(","));
            saveCsvContent(resultDataList, dzmzggResultDataObjectProperties, properties);
            log.info("chengJiaoJinE_error_log -> " + chengJiaoJinE_error_log);
            log.info("emptyDataCount -> " + emptyDataCount);
        }
    }

    private int handelRawDataCore(List<DzmzggDataBean> allData, List<DzmzggResultData> resultDataList, Map<String, String> gdAndRegionMapping, int emptyDataCount, Properties properties) {
        for(DzmzggDataBean dzmzggDataBean : allData){
            if (dzmzggDataBean.getPageurl() == null){
                //记录空数据的条数
                emptyDataCount++;
            }else{
                Document document = Jsoup.parse(dzmzggDataBean.getContent());
                Elements pElem = document.getElementsByTag("p");
                String docText = document.body().text().trim();
                Elements tables = document.select("table");
                if(tables.size() > 0){
                    for (Element table : tables) {
                        String tableFeatureStr = table.select("th").stream().map(th -> th.text().trim().replace(",", "")).collect(Collectors.joining("-"));

                        Elements tbodys = table.select("tbody");
                        //对每个页面中的每个表格数据进行保存
                        if(tbodys.size() == 1){
                            Element tbody = tbodys.get(0);
                            Elements trTmp = tbody.select("tr");
                            List<DzmzggResultData> dzmzggResultDatas = trTmp.stream().filter(tr -> !tr.text().trim().startsWith("合计")).filter(f -> f.select("td").size() > 0).map(m ->{
                                Elements tds = m.select("td");
                                DzmzggResultData dzmzggResultData = new DzmzggResultData();

                                if(TB11.equals(tableFeatureStr) && tds.size() == tableFeatureStr.split("-").length){
                                    dzmzggResultData.setTb11ChanPinMingCheng(tds.get(0).text().trim().replace(",","，").replace("\"", ""));
                                    dzmzggResultData.setTb11JiShuGuiGe(tds.get(1).text().trim().replace(",","，").replace("\"", ""));
                                    dzmzggResultData.setTb11BeiZhu(tds.get(2).text().trim().replace(",","，").replace("\"", ""));
                                    dzmzggResultData.setTb11ShuLiang(tds.get(3).text().trim().replace(",","").replace("\"", ""));
                                    dzmzggResultData.setTb11DanJia(tds.get(4).text().trim().replace(",","").replace("￥","").replace("\"", ""));
                                    dzmzggResultData.setTb11JinE(tds.get(5).text().trim().replace(",","").replace("￥","").replace("\"", ""));

                                }else if(TB12.equals(tableFeatureStr) && tds.size() == tableFeatureStr.split("-").length){
                                    dzmzggResultData.setTb12FuWuMiaoShu(tds.get(0).text().trim().replace(",", "，").replace("\"", ""));
                                    dzmzggResultData.setTb12ShuLiang(tds.get(1).text().trim().replace(",", "").replace("\"", ""));
                                    dzmzggResultData.setTb12DanWei(tds.get(2).text().trim().replace(",", "，").replace("\"", ""));
                                    dzmzggResultData.setTb12GongYingShangBaoJia(tds.get(3).text().trim().replace(",", "").replace("￥","").replace("\"", ""));
                                    dzmzggResultData.setTb12ShiFouZhongBiao(tds.get(4).text().trim().replace(",", "，").replace("\"", ""));

                                }else if(TB13.equals(tableFeatureStr) && tds.size() == tableFeatureStr.split("-").length){
                                    dzmzggResultData.setTb13GongYingShangMingCheng(tds.get(0).text().trim().replace(",", "，").replace("\"", ""));
                                    dzmzggResultData.setTb13GongYingShangBaoJia(tds.get(1).text().trim().replace(",", "").replace("￥","").replace("\"", ""));
                                    dzmzggResultData.setTb13BaoJiaShiJian(tds.get(2).text().trim().replace(",", "，").replace("\"", ""));

                                }else if(TB14.equals(tableFeatureStr) && tds.size() == tableFeatureStr.split("-").length){
                                    dzmzggResultData.setTb14PaiMing(tds.get(0).text().trim().replace(",", "").replace("\"", ""));
                                    dzmzggResultData.setTb14GongYingShangMingCheng(tds.get(1).text().trim().replace(",", "，").replace("\"", ""));
                                    dzmzggResultData.setTb14ZongBaoJia(tds.get(2).text().trim().replace(",", "").replace("￥","").replace("\"", ""));
                                    dzmzggResultData.setTb14BaoJiaShiJian(tds.get(3).text().trim().replace(",", "，").replace("\"", ""));

                                }else if(TB15.equals(tableFeatureStr) && tds.size() == tableFeatureStr.split("-").length){
                                    dzmzggResultData.setTb15PaiMing(tds.get(0).text().trim().replace(",", "").replace("\"", ""));
                                    dzmzggResultData.setTb15GongYingShang(tds.get(1).text().trim().replace(",", "，").replace("\"", ""));
                                    dzmzggResultData.setTb15PinPai(tds.get(2).text().trim().replace(",", "，").replace("\"", ""));
                                    dzmzggResultData.setTb15XingHao(tds.get(3).text().trim().replace(",", "，").replace("\"", ""));
                                    dzmzggResultData.setTb15ShuLiang(tds.get(4).text().trim().replace(",", "").replace("\"", ""));
                                    dzmzggResultData.setTb15DanJia(tds.get(5).text().trim().replace(",", "").replace("￥","").replace("\"", ""));
                                    dzmzggResultData.setTb15BaoJiaJinE(tds.get(6).text().trim().replace(",", "").replace("￥","").replace("\"", ""));
                                    dzmzggResultData.setTb15BaoJiaShiJian(tds.get(7).text().trim().replace(",", "，").replace("\"", ""));

                                }else if(TB21.equals(tableFeatureStr) && tds.size() == tableFeatureStr.split("-").length){

                                    dzmzggResultData.setTb21BianHao(tds.get(0).text().trim().replace(",", "，").replace("\"", ""));
                                    dzmzggResultData.setTb21CaiGouPinMu(tds.get(1).text().trim().replace(",", "，").replace("\"", ""));
                                    dzmzggResultData.setTb21MingCheng(tds.get(2).text().trim().replace(",", "，").replace("\"", ""));
                                    dzmzggResultData.setTb21PinPai(tds.get(3).text().trim().replace(",", "，").replace("\"", ""));
                                    dzmzggResultData.setTb21XingHao(tds.get(4).text().trim().replace(",", "，").replace("\"", ""));
                                    dzmzggResultData.setTb21ShuLiang(tds.get(5).text().trim().replace(",", "").replace("\"", ""));
                                    dzmzggResultData.setTb21ZhuYaoJiShuCanShu(tds.get(6).text().trim().replace(",", "，").replace("\"", ""));
                                    dzmzggResultData.setTb21JinE(tds.get(7).text().trim().replace(",", "").replace("￥","").replace("\"", ""));

                                }else if(TB22.equals(tableFeatureStr) && tds.size() == tableFeatureStr.split("-").length){

                                    dzmzggResultData.setTb22GongYingShangMingCheng(tds.get(0).text().trim().replace(",", "，").replace("\"", ""));
                                    dzmzggResultData.setTb22YuanYin(tds.get(1).text().trim().replace(",", "，").replace("\"", ""));


                                }else if(TB23.equals(tableFeatureStr) && tds.size() == tableFeatureStr.split("-").length){
                                    dzmzggResultData.setTb23XuHao(tds.get(0).text().trim().replace(",", "，").replace("\"", ""));
                                    dzmzggResultData.setTb23DanJia(tds.get(1).text().trim().replace(",", "").replace("￥","").replace("\"", ""));
                                    dzmzggResultData.setTb23BaoJiaJinE(tds.get(2).text().trim().replace(",", "").replace("￥","").replace("\"", ""));
                                    dzmzggResultData.setTb23ShangPinXingHao(tds.get(3).text().trim().replace(",", "，").replace("\"", ""));
                                    dzmzggResultData.setTb23ShuLiang(tds.get(4).text().trim().replace(",", "").replace("\"", ""));
                                    dzmzggResultData.setTb23CanShu(tds.get(5).text().trim().replace(",", "，").replace("\"", ""));


                                }else if(TB31.equals(tableFeatureStr) && tds.size() == tableFeatureStr.split("-").length){

                                    dzmzggResultData.setTb31MingCheng(tds.get(0).text().trim().replace(",", "，").replace("\"", ""));
                                    dzmzggResultData.setTb31ShuLiang(tds.get(1).text().trim().replace(",", "").replace("\"", ""));
                                    dzmzggResultData.setTb31DanWei(tds.get(2).text().trim().replace(",", "，").replace("\"", ""));
                                    dzmzggResultData.setTb31GongYingShangMingCheng(tds.get(3).text().trim().replace(",", "，").replace("\"", ""));
                                    dzmzggResultData.setTb31GongYingShangBaoJia(tds.get(4).text().trim().replace(",", "").replace("￥","").replace("\"", ""));

                                }else if(TB32.equals(tableFeatureStr) && tds.size() == tableFeatureStr.split("-").length){

                                    dzmzggResultData.setTb32XuHao(tds.get(0).text().trim().replace(",", "，").replace("\"", ""));
                                    dzmzggResultData.setTb32XuQiuMingCheng(tds.get(1).text().trim().replace(",", "，").replace("\"", ""));
                                    dzmzggResultData.setTb32XuQiuXuanXiang(tds.get(2).text().trim().replace(",", "，").replace("\"", ""));


                                }
                                //保存每个页面的基本数据
                                getBaseData(gdAndRegionMapping, dzmzggDataBean, pElem, docText, dzmzggResultData, properties);
                                return dzmzggResultData;
                            }).collect(Collectors.toList());
                            resultDataList.addAll(dzmzggResultDatas);
                        }

                    }
                }else {
                    DzmzggResultData dzmzggResultData = new DzmzggResultData();
                    getBaseData(gdAndRegionMapping, dzmzggDataBean, pElem, docText, dzmzggResultData, properties);
                    resultDataList.add(dzmzggResultData);
                    log.info("tables size -> 0");
                }
            }


        }
        return emptyDataCount;
    }

    private void getBaseData(Map<String, String> gdAndRegionMapping, DzmzggDataBean dzmzggDataBean, Elements pElem, String docText, DzmzggResultData dzmzggResultData, Properties properties) {
        String detailsUrl = properties.getProperty("details.url");
        for (Element e : pElem){
            if (!"".equals(e.text().trim())){
                String lineValue = e.text().trim();
                String valueOfLineValue = lineValue.substring(lineValue.indexOf("：") + 1);
                if(docText.startsWith("本项目于")){
                    dzmzggResultData.setXiangMuMingCheng(dzmzggDataBean.getTitle());
                    if (lineValue.startsWith("预算金额：")){
                        dzmzggResultData.setYuSuanJinE(valueOfLineValue.replace(",", ""));
                    }else if(lineValue.startsWith("成交金额：") ){
                        dzmzggResultData.setChengJiaoJinE(valueOfLineValue.substring(0, valueOfLineValue.indexOf("，")));
                    }else if (lineValue.startsWith("采购计划编号：")){
                        dzmzggResultData.setCaiGouJiHuaBianHao(valueOfLineValue);
                    }
                }else if(docText.startsWith("项目名称：")){
                    if (lineValue.startsWith("项目名称：")){
                        dzmzggResultData.setXiangMuMingCheng(valueOfLineValue);
                    }else if(lineValue.contains("成交价：")){
                        String value = "";
                        if(valueOfLineValue.contains("(")){
                            value = valueOfLineValue.substring(0, valueOfLineValue.indexOf("("));
                        }else if (valueOfLineValue.contains("（")){
                            value = valueOfLineValue.substring(0, valueOfLineValue.indexOf("（"));
                        }
                        dzmzggResultData.setChengJiaoJinE(value.replace(",", ""));
                    }else if(lineValue.startsWith("成交金额：") ){
                        dzmzggResultData.setChengJiaoJinE(valueOfLineValue.substring(0, valueOfLineValue.indexOf("，")).replace(",", ""));
                    }else if(lineValue.startsWith("项目编号：")){
                        dzmzggResultData.setXiangMuBianHao(valueOfLineValue);
                    }
                }

                if (lineValue.startsWith("采购单位：")) {
                    dzmzggResultData.setCaiGouDanWei(valueOfLineValue);
                }else if(lineValue.contains("成交供应商：")){
                    dzmzggResultData.setChengJiaoGongYingShang("\""+ valueOfLineValue + "\"");
                }else{
                    if(dzmzggResultData.getChengJiaoJinE() == null ){
                        dzmzggResultData.setChengJiaoJinE("Null");
                    }

                    if(dzmzggResultData.getYuSuanJinE() == null)
                        dzmzggResultData.setYuSuanJinE("Null");
                    if(dzmzggResultData.getCaiGouJiHuaBianHao() == null)
                        dzmzggResultData.setCaiGouJiHuaBianHao("Null");
                    if(dzmzggResultData.getXiangMuBianHao() == null)
                        dzmzggResultData.setXiangMuBianHao("Null");
                }

                dzmzggResultData.setLuoKuanRiQi(pElem.get(pElem.size() - 1).text().trim());

                dzmzggResultData.setTitle(dzmzggDataBean.getTitle());
                dzmzggResultData.setPageurl(detailsUrl + dzmzggDataBean.getPageurl());
                dzmzggResultData.setGdRegion(searchGdRegionByRegionCode(gdAndRegionMapping, dzmzggDataBean.getRegionCode()));
                dzmzggResultData.setGdRegionChild(dzmzggDataBean.getRegionName());
            }
        }
    }

    private void saveCsvContent(List<DzmzggResultData> resultDataList, String dzmzggResultDataObjectProperties, Properties properties) {
        String resultCsv = properties.getProperty("result.csv.path");
        String firstLineHeader = csvOutputHeader(dzmzggResultDataObjectProperties, properties);
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(new File(resultCsv), false), "GBK")) {
            ColumnPositionMappingStrategy<DzmzggResultData> mapper = new ColumnPositionMappingStrategy<>();
            mapper.setType(DzmzggResultData.class);
            mapper.setColumnMapping(dzmzggResultDataObjectProperties.split(","));
            CSVWriter csvWriter = new CSVWriter(writer, CSVWriter.DEFAULT_SEPARATOR, CSVWriter.NO_QUOTE_CHARACTER, '\\', "\n");
            csvWriter.writeNext(firstLineHeader.split(","));

            StatefulBeanToCsv beanToCsv = new StatefulBeanToCsvBuilder(writer)
                    .withMappingStrategy(mapper)
                    .withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
                    .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
                    .withEscapechar('\\')
                    .build();
            beanToCsv.write(resultDataList);
            csvWriter.close();

        } catch (CsvRequiredFieldEmptyException e) {
            e.printStackTrace();
        } catch (CsvDataTypeMismatchException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String csvOutputHeader(String objectProperties, Properties properties){
        String csvOutputConfigFile = properties.getProperty("result.csv.config.file");
        String csvOutputConfig = "";
        try(
                InputStream csvOutputConfigInput = new FileInputStream(new File(csvOutputConfigFile));
        ){
            String csvOutputConfigRaw = MyFileUtils.inputByteReader(csvOutputConfigInput);
            csvOutputConfig = csvOutputConfigRaw;
        }catch (IOException e){
            e.printStackTrace();
        }

        String firstLineHeader = "";
        if (!"".equals(csvOutputConfig.trim())){
            Map<String, String> headerMapping = new HashMap<>();
            Arrays.stream(csvOutputConfig.trim().split(",")).forEach(s ->headerMapping.put(s.substring(0, s.indexOf("-")), s.substring(s.indexOf("-") + 1)));
            System.out.println(new Gson().toJson(headerMapping));
            firstLineHeader = Arrays.stream(objectProperties.split(",")).map(s -> searchHeader(headerMapping, s)).collect(Collectors.joining(","));
        }else {
            log.error(csvOutputConfigFile + "is empty!!");
        }
        return firstLineHeader;
    }

    public String searchHeader(Map<String, String> headerMapping, String item) {
        String searchGdResult  = headerMapping
                .entrySet()
                .stream()
                .filter(s -> s.getKey().equals(item))
                .map(s -> s.getValue())
                .collect(Collectors.toList())
                .get(0);
        return searchGdResult;
    }

    public Map<String, String> createGdMapping(Properties properties) {
        String gdTree = properties.getProperty("gd.mapping.file");
        Gson gsonForGdTree = new Gson();
        String gdTreeRaw = "";
        try(
                InputStream gdTreeInput = new FileInputStream(new File(gdTree));
        ){
            String gdTreeRawTmp = MyFileUtils.inputByteReader(gdTreeInput);
            gdTreeRaw = gdTreeRawTmp;
        }catch(IOException e) {
            e.printStackTrace();
        }
        //build gd region mapping
        List<GdTreeBean> gdTreeBeanList = gsonForGdTree.fromJson(gdTreeRaw, new TypeToken<List<GdTreeBean>>(){}.getType());
        Map<String, String> gdAndRegionMapping = new HashMap();

//        List<GdTreeBean> newGdTreeWithoutRoot = gdTreeBeanList.stream().filter(s -> s.getChildren()!=null).collect(Collectors.toList());
//        List<GdTreeBean> newGdTreeWithoutChildren = gdTreeBeanList.stream().filter(s -> s.getChildren()==null).collect(Collectors.toList());

        for(GdTreeBean gdTreeBean : gdTreeBeanList){
            List<GdTreeChildrenBean> childrenBeanList = gdTreeBean.getChildren();
            String regions = "";
            if (childrenBeanList != null){
                regions = childrenBeanList.stream().map(s -> s.getRegionCode().trim()+"-"+s.getName().trim()).collect(Collectors.joining(","));
            }else {
                regions = gdTreeBean.getName();
            }
            System.out.println(gdTreeBean.getRegionCode().trim() + "-" + gdTreeBean.getName().trim() + ":" + regions);
            gdAndRegionMapping.put(gdTreeBean.getRegionCode().trim() + "-" + gdTreeBean.getName().trim(), regions);

        }
        return gdAndRegionMapping;
    }

    public String searchGdRegionByRegionCode(Map<String, String> gdAndRegionMapping, String regionCode) {
        List<String> searchGdResultList  = gdAndRegionMapping
                .entrySet()
                .stream()
                .filter(s -> s.getValue().contains(regionCode)||s.getKey().contains(regionCode))
                .map(s -> s.getKey().substring(s.getKey().lastIndexOf("-")+1))
                .collect(Collectors.toList());
        String searchGdResult = "";
        if(searchGdResultList.size() == 0){
            searchGdResult = regionCode;
        }else {
            searchGdResult = searchGdResultList.get(0);
        }
        return searchGdResult;
    }


    public static void main(String[] args) throws IOException, ParseException, URISyntaxException {
        Properties properties = new Properties();
        properties.load(new FileInputStream(new File("src/main/resources/app.properties")));
        HandleRawData demo = new HandleRawData();
        demo.handleRawData(properties);
    }




}
