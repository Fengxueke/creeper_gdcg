package com.isflee;

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
import java.util.*;
import java.util.stream.Collectors;

@Slf4j

public class DataFormatTest {

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

    public void test(){
        String msg = "{\"msg\":\"操作成功\",\"total\":\"474858\",\"code\":\"200\",\"data\":[{\"title\":\"潮州市湘桥区铁铺镇溪头小学电子卖场直接订购成交公告\",\"pageurl\":\"/freecms/site/gd/ggxx/info/2022/bfb83462-555c-40d7-90ab-446d487cc6f3.html\",\"content\":\"\\u003cstyle\\u003e\\n    .titleName {\\n        font-size: 20px;\\n        font-weight: bold;\\n        text-align: center;\\n        margin-bottom: 20px;\\n    }\\n\\n    .contractWrap {\\n        padding: 10px 20px\\n    }\\n\\n    .contractWrap p {\\n        text-indent: 2em;\\n        line-height: 2em;\\n        font-size: 14px;\\n        margin: 10px\\n    }\\n\\n    .contractWrap p input {\\n        width: 300px;\\n        height: 24px;\\n        border: none;\\n        border-bottom: solid 1px #999;\\n    }\\n\\n    .contractWrap .innerTitle {\\n        font-size: 16px;\\n        font-weight: bold;\\n        margin: 20px 0 10px 0;\\n        text-indent: 12px\\n    }\\n\\n    .contractTable,\\n    .contractTable th,\\n    .contractTable td {\\n        border: solid 1px #e5e5e5;\\n        border-collapse: collapse;\\n    }\\n\\n    .contractTable th {\\n        height: 40px;\\n        background-color: #f5f5f5;\\n        font-size: 14px;\\n        text-align: center\\n    }\\n\\n    .contractTable td {\\n        height: 30px;\\n        padding: 2px 4px;\\n    }\\n\\n    .contractWrap .indent2 {\\n        text-indent: 3em;\\n    }\\n\\n    .indent1 {\\n        text-indent: 1em;\\n    }\\n\\n    .signWrap {\\n        margin-top: 20px\\n    }\\n\\n    .text-right {\\n        text-align: right\\n    }\\n\\n    .con-btn-box {\\n        margin: 20px 10px;\\n        text-align: right;\\n    }\\n\\n    .con-btn-box .con-btn {\\n        padding: 0 20px;\\n        line-height: 30px;\\n        color: #2577e3;\\n        border: 1px solid #2577e3;\\n        display: inline-block;\\n        text-align: center;\\n        border-radius: 4px;\\n    }\\n\\u003c/style\\u003e\\n\\u003cdiv class\\u003d\\\"contractWrap\\\" style\\u003d\\\"page-break-after: always;\\\"\\u003e\\n\\n    \\u003cbr\\u003e\\n    \\u003cp\\u003e本项目于2022-09-13 13:14采购，现将本次直接采购结果公布如下：\\u003c/p\\u003e\\n\\n    \\u003ch2 class\\u003d\\\"innerTitle\\\"\\u003e一、项目信息\\u003c/h2\\u003e\\n    \\u003cp\\u003e采购单位：潮州市湘桥区铁铺镇溪头小学\\u003c/p\\u003e\\n    \\u003cp\\u003e预算金额：3,200.00\\u003c/p\\u003e\\n    \\u003cp\\u003e采购计划编号：445102-2022-00818\\u003c/p\\u003e\\n    \\u003ch2 class\\u003d\\\"innerTitle\\\"\\u003e二、成交信息\\u003c/h2\\u003e\\n    \\u003cp\\u003e成交供应商：潮州市湘桥区鼎顺贸易商行\\u003c/p\\u003e\\n    \\u003cp\\u003e成交金额：3200.00，大写(人民币)：叁仟贰佰元整）\\n    \\u003c/p\\u003e\\n    \\u003cp\\u003e\\u003ctable cellpadding\\u003d\\\"0\\\" cellspacing\\u003d\\\"0\\\" class\\u003d\\\"contractTable\\\" border\\u003d\\\"1\\\" style\\u003d\\\"width:100%\\\"\\u003e\\n        \\u003ctr style\\u003d\\\"height:40px\\\"\\u003e\\n            \\u003cth style\\u003d\\\"width:18%;text-align:center;\\\"\\u003e产品名称\\u003c/th\\u003e\\n            \\u003cth style\\u003d\\\"width:40%;text-align:center;\\\"\\u003e技术规格\\u003c/th\\u003e\\n            \\u003cth style\\u003d\\\"width:13%;text-align:center;\\\"\\u003e备注\\u003c/th\\u003e\\n            \\u003cth style\\u003d\\\"width:13%;text-align:center;\\\"\\u003e数量\\u003c/th\\u003e\\n            \\u003cth style\\u003d\\\"width:20%;text-align:center;\\\"\\u003e单价（元）\\u003c/th\\u003e\\n            \\u003cth style\\u003d\\\"width:20%;text-align:center;\\\"\\u003e金额（元）\\u003c/th\\u003e\\n        \\u003c/tr\\u003e\\n        \\u003c!--订单商品集合--\\u003e\\n        \\u003ctr style\\u003d\\\"height:36px;\\\"\\u003e\\n            \\u003ctd style\\u003d\\\"padding:3px 5px;\\\"\\u003e复印纸\\u003c/td\\u003e\\n            \\u003ctd style\\u003d\\\"padding:3px 5px;\\\"\\u003e\\n                富丽星/FLX,\\n                富丽星/FLX70克 500p A4 （5包/箱）,\\n                70克 500p A4 （5包/箱）,\\n                数量:20;\\n                \\u003cbr/\\u003e\\n                \\n            \\u003c/td\\u003e\\n            \\u003ctd\\u003e\\u003c/td\\u003e\\n            \\u003ctd style\\u003d\\\"padding:3px 5px;text-align:center\\\"\\u003e20\\u003c/td\\u003e\\n            \\u003ctd style\\u003d\\\"padding:3px 5px;text-align:right\\\"\\u003e\\n                    ￥160.00\\n            \\u003c/td\\u003e\\n            \\u003ctd style\\u003d\\\"padding:3px 5px;text-align:right\\\"\\u003e\\n                    ￥3200.00\\n            \\u003c/td\\u003e\\n        \\u003c/tr\\u003e\\n        \\u003ctr style\\u003d\\\"height:36px\\\"\\u003e\\n            \\u003ctd style\\u003d\\\"text-align: center;padding:3px 5px;\\\"\\u003e合计\\u003c/td\\u003e\\n            \\u003ctd colspan\\u003d\\\"5\\\" style\\u003d\\\"text-align: left;padding:3px 5px;\\\"\\u003e￥3200.00  大写（人民币）: 叁仟贰佰元整\\u003c/td\\u003e\\n        \\u003c/tr\\u003e\\n    \\u003c/table\\u003e\\u003c/p\\u003e\\n\\n    \\u003ch2 class\\u003d\\\"innerTitle\\\"\\u003e三、\\t项目联系方式\\u003c/h2\\u003e\\n\\n    \\u003cp\\u003e联系人：余树钗\\u003c/p\\u003e\\n\\n\\n    \\u003cp class\\u003d\\\"text-right\\\"\\u003e采购单位：潮州市湘桥区铁铺镇溪头小学\\u003c/p\\u003e\\n    \\u003cp class\\u003d\\\"text-right\\\"\\u003e2022年09月13日\\u003c/p\\u003e\\n\\u003c/div\\u003e\",\"regionCode\":\"445102\",\"regionName\":\"湘桥区\"},{\"title\":\"潮州市湘桥区铁铺镇溪头小学电子卖场直接订购成交公告\",\"pageurl\":\"/freecms/site/gd/ggxx/info/2022/86bd0d68-c5e7-4935-a0eb-5777a65e17d0.html\",\"content\":\"\\u003cstyle\\u003e\\n    .titleName {\\n        font-size: 20px;\\n        font-weight: bold;\\n        text-align: center;\\n        margin-bottom: 20px;\\n    }\\n\\n    .contractWrap {\\n        padding: 10px 20px\\n    }\\n\\n    .contractWrap p {\\n        text-indent: 2em;\\n        line-height: 2em;\\n        font-size: 14px;\\n        margin: 10px\\n    }\\n\\n    .contractWrap p input {\\n        width: 300px;\\n        height: 24px;\\n        border: none;\\n        border-bottom: solid 1px #999;\\n    }\\n\\n    .contractWrap .innerTitle {\\n        font-size: 16px;\\n        font-weight: bold;\\n        margin: 20px 0 10px 0;\\n        text-indent: 12px\\n    }\\n\\n    .contractTable,\\n    .contractTable th,\\n    .contractTable td {\\n        border: solid 1px #e5e5e5;\\n        border-collapse: collapse;\\n    }\\n\\n    .contractTable th {\\n        height: 40px;\\n        background-color: #f5f5f5;\\n        font-size: 14px;\\n        text-align: center\\n    }\\n\\n    .contractTable td {\\n        height: 30px;\\n        padding: 2px 4px;\\n    }\\n\\n    .contractWrap .indent2 {\\n        text-indent: 3em;\\n    }\\n\\n    .indent1 {\\n        text-indent: 1em;\\n    }\\n\\n    .signWrap {\\n        margin-top: 20px\\n    }\\n\\n    .text-right {\\n        text-align: right\\n    }\\n\\n    .con-btn-box {\\n        margin: 20px 10px;\\n        text-align: right;\\n    }\\n\\n    .con-btn-box .con-btn {\\n        padding: 0 20px;\\n        line-height: 30px;\\n        color: #2577e3;\\n        border: 1px solid #2577e3;\\n        display: inline-block;\\n        text-align: center;\\n        border-radius: 4px;\\n    }\\n\\u003c/style\\u003e\\n\\u003cdiv class\\u003d\\\"contractWrap\\\" style\\u003d\\\"page-break-after: always;\\\"\\u003e\\n\\n    \\u003cbr\\u003e\\n    \\u003cp\\u003e本项目于2022-09-13 13:15采购，现将本次直接采购结果公布如下：\\u003c/p\\u003e\\n\\n    \\u003ch2 class\\u003d\\\"innerTitle\\\"\\u003e一、项目信息\\u003c/h2\\u003e\\n    \\u003cp\\u003e采购单位：潮州市湘桥区铁铺镇溪头小学\\u003c/p\\u003e\\n    \\u003cp\\u003e预算金额：7,200.00\\u003c/p\\u003e\\n    \\u003cp\\u003e采购计划编号：445102-2022-00817\\u003c/p\\u003e\\n    \\u003ch2 class\\u003d\\\"innerTitle\\\"\\u003e二、成交信息\\u003c/h2\\u003e\\n    \\u003cp\\u003e成交供应商：潮州市湘桥区鼎顺贸易商行\\u003c/p\\u003e\\n    \\u003cp\\u003e成交金额：7198.00，大写(人民币)：柒仟壹佰玖拾捌元整）\\n    \\u003c/p\\u003e\\n    \\u003cp\\u003e\\u003ctable cellpadding\\u003d\\\"0\\\" cellspacing\\u003d\\\"0\\\" class\\u003d\\\"contractTable\\\" border\\u003d\\\"1\\\" style\\u003d\\\"width:100%\\\"\\u003e\\n        \\u003ctr style\\u003d\\\"height:40px\\\"\\u003e\\n            \\u003cth style\\u003d\\\"width:18%;text-align:center;\\\"\\u003e产品名称\\u003c/th\\u003e\\n            \\u003cth style\\u003d\\\"width:40%;text-align:center;\\\"\\u003e技术规格\\u003c/th\\u003e\\n            \\u003cth style\\u003d\\\"width:13%;text-align:center;\\\"\\u003e备注\\u003c/th\\u003e\\n            \\u003cth style\\u003d\\\"width:13%;text-align:center;\\\"\\u003e数量\\u003c/th\\u003e\\n            \\u003cth style\\u003d\\\"width:20%;text-align:center;\\\"\\u003e单价（元）\\u003c/th\\u003e\\n            \\u003cth style\\u003d\\\"width:20%;text-align:center;\\\"\\u003e金额（元）\\u003c/th\\u003e\\n        \\u003c/tr\\u003e\\n        \\u003c!--订单商品集合--\\u003e\\n        \\u003ctr style\\u003d\\\"height:36px;\\\"\\u003e\\n            \\u003ctd style\\u003d\\\"padding:3px 5px;\\\"\\u003e壁挂式空调机\\u003c/td\\u003e\\n            \\u003ctd style\\u003d\\\"padding:3px 5px;\\\"\\u003e\\n                美的/Midea,\\n                美的（Midea）新能效 KFR-35GW/G2-1-M 1.5匹 变频冷暖 空调挂机 一级能效,\\n                KFR-35GW/G2-1-M,\\n                数量:2;\\n                \\u003cbr/\\u003e\\n                \\n            \\u003c/td\\u003e\\n            \\u003ctd\\u003e\\u003c/td\\u003e\\n            \\u003ctd style\\u003d\\\"padding:3px 5px;text-align:center\\\"\\u003e2\\u003c/td\\u003e\\n            \\u003ctd style\\u003d\\\"padding:3px 5px;text-align:right\\\"\\u003e\\n                    ￥3599.00\\n            \\u003c/td\\u003e\\n            \\u003ctd style\\u003d\\\"padding:3px 5px;text-align:right\\\"\\u003e\\n                    ￥7198.00\\n            \\u003c/td\\u003e\\n        \\u003c/tr\\u003e\\n        \\u003ctr style\\u003d\\\"height:36px\\\"\\u003e\\n            \\u003ctd style\\u003d\\\"text-align: center;padding:3px 5px;\\\"\\u003e合计\\u003c/td\\u003e\\n            \\u003ctd colspan\\u003d\\\"5\\\" style\\u003d\\\"text-align: left;padding:3px 5px;\\\"\\u003e￥7198.00  大写（人民币）: 柒仟壹佰玖拾捌元整\\u003c/td\\u003e\\n        \\u003c/tr\\u003e\\n    \\u003c/table\\u003e\\u003c/p\\u003e\\n\\n    \\u003ch2 class\\u003d\\\"innerTitle\\\"\\u003e三、\\t项目联系方式\\u003c/h2\\u003e\\n\\n    \\u003cp\\u003e联系人：余树钗\\u003c/p\\u003e\\n\\n\\n    \\u003cp class\\u003d\\\"text-right\\\"\\u003e采购单位：潮州市湘桥区铁铺镇溪头小学\\u003c/p\\u003e\\n    \\u003cp class\\u003d\\\"text-right\\\"\\u003e2022年09月13日\\u003c/p\\u003e\\n\\u003c/div\\u003e\",\"regionCode\":\"445102\",\"regionName\":\"湘桥区\"}]}";
        Gson gson = new Gson();
        DzmzggBean dzmzggBean = gson.fromJson(msg, DzmzggBean.class);
        System.out.println(new Gson().toJson(dzmzggBean));
        List<DzmzggResultData> resultDataList = new ArrayList<>();

        Map<String, String> gdAndRegionMapping = createGdMapping();
        System.out.println(searchGdRegionByRegionCode(gdAndRegionMapping,"440801"));

        List<String> tableFeatures = new ArrayList<>();
        int dataCount = 0;

        for(DzmzggDataBean dzmzggDataBean : dzmzggBean.getData()){
            Document document = Jsoup.parse(dzmzggDataBean.getContent());

            Elements pElem = document.getElementsByTag("p");
            String docText = document.body().text().trim();




            Elements tables = document.select("table");
//            String tableFeatureStr = "https://gdgpo.czt.gd.gov.cn/" + dzmzggDataBean.getPageurl() + "," + tables.stream().map(s -> {
//                        return s.select("th").stream().map(t -> t.text().trim().replace(",", "")).collect(Collectors.joining("-"));
//                    }
//            ).collect(Collectors.joining(","));

            for (Element table : tables){

                String tableFeatureStr = table.select("th").stream().map(th -> th.text().trim().replace(",", "")).collect(Collectors.joining("-"));

                Elements tbodys = table.select("tbody");

                if(tbodys.size() == 1){
                    Element tbody = tbodys.get(0);
                    Elements trTmp = tbody.select("tr");
                    List<DzmzggResultData> dzmzggResultDatas = trTmp.stream().filter(tr -> !tr.text().trim().startsWith("合计")).filter(f -> f.select("td").size() > 0).map(m ->{
                        Elements tds = m.select("td");
                        DzmzggResultData dzmzggResultData = new DzmzggResultData();

                        if(TB11.equals(tableFeatureStr) && tds.size() == tableFeatureStr.split("-").length){
                            dzmzggResultData.setTb11ChanPinMingCheng(tds.get(0).text().trim().replace(",","，"));
                            dzmzggResultData.setTb11JiShuGuiGe(tds.get(1).text().trim().replace(",","，"));
                            dzmzggResultData.setTb11BeiZhu(tds.get(2).text().trim().replace(",","，"));
                            dzmzggResultData.setTb11ShuLiang(tds.get(3).text().trim().replace(",",""));
                            dzmzggResultData.setTb11DanJia(tds.get(4).text().trim().replace(",","").replace("￥",""));
                            dzmzggResultData.setTb11JinE(tds.get(5).text().trim().replace(",","").replace("￥",""));

                        }else if(TB12.equals(tableFeatureStr) && tds.size() == tableFeatureStr.split("-").length){
                            dzmzggResultData.setTb12FuWuMiaoShu(tds.get(0).text().trim().replace(",", "，"));
                            dzmzggResultData.setTb12ShuLiang(tds.get(1).text().trim().replace(",", ""));
                            dzmzggResultData.setTb12DanWei(tds.get(2).text().trim().replace(",", "，"));
                            dzmzggResultData.setTb12GongYingShangBaoJia(tds.get(3).text().trim().replace(",", "").replace("￥",""));
                            dzmzggResultData.setTb12ShiFouZhongBiao(tds.get(4).text().trim().replace(",", "，"));

                        }else if(TB13.equals(tableFeatureStr) && tds.size() == tableFeatureStr.split("-").length){
                            dzmzggResultData.setTb13GongYingShangMingCheng(tds.get(0).text().trim().replace(",", "，"));
                            dzmzggResultData.setTb13GongYingShangBaoJia(tds.get(1).text().trim().replace(",", "").replace("￥",""));
                            dzmzggResultData.setTb13BaoJiaShiJian(tds.get(2).text().trim().replace(",", "，"));

                        }else if(TB14.equals(tableFeatureStr) && tds.size() == tableFeatureStr.split("-").length){
                            dzmzggResultData.setTb14PaiMing(tds.get(0).text().trim().replace(",", ""));
                            dzmzggResultData.setTb14GongYingShangMingCheng(tds.get(1).text().trim().replace(",", "，"));
                            dzmzggResultData.setTb14ZongBaoJia(tds.get(2).text().trim().replace(",", "").replace("￥",""));
                            dzmzggResultData.setTb14BaoJiaShiJian(tds.get(3).text().trim().replace(",", "，"));

                        }else if(TB15.equals(tableFeatureStr) && tds.size() == tableFeatureStr.split("-").length){
                            dzmzggResultData.setTb15PaiMing(tds.get(0).text().trim().replace(",", ""));
                            dzmzggResultData.setTb15GongYingShang(tds.get(1).text().trim().replace(",", "，"));
                            dzmzggResultData.setTb15PinPai(tds.get(2).text().trim().replace(",", "，"));
                            dzmzggResultData.setTb15XingHao(tds.get(3).text().trim().replace(",", "，"));
                            dzmzggResultData.setTb15ShuLiang(tds.get(4).text().trim().replace(",", ""));
                            dzmzggResultData.setTb15DanJia(tds.get(5).text().trim().replace(",", "").replace("￥",""));
                            dzmzggResultData.setTb15BaoJiaJinE(tds.get(6).text().trim().replace(",", "").replace("￥",""));
                            dzmzggResultData.setTb15BaoJiaShiJian(tds.get(7).text().trim().replace(",", "，"));

                        }else if(TB21.equals(tableFeatureStr) && tds.size() == tableFeatureStr.split("-").length){

                            dzmzggResultData.setTb21BianHao(tds.get(0).text().trim().replace(",", "，"));
                            dzmzggResultData.setTb21CaiGouPinMu(tds.get(1).text().trim().replace(",", "，"));
                            dzmzggResultData.setTb21MingCheng(tds.get(2).text().trim().replace(",", "，"));
                            dzmzggResultData.setTb21PinPai(tds.get(3).text().trim().replace(",", "，"));
                            dzmzggResultData.setTb21XingHao(tds.get(4).text().trim().replace(",", "，"));
                            dzmzggResultData.setTb21ShuLiang(tds.get(5).text().trim().replace(",", ""));
                            dzmzggResultData.setTb21ZhuYaoJiShuCanShu(tds.get(6).text().trim().replace(",", "，"));
                            dzmzggResultData.setTb21JinE(tds.get(7).text().trim().replace(",", "").replace("￥",""));

                        }else if(TB22.equals(tableFeatureStr) && tds.size() == tableFeatureStr.split("-").length){

                            dzmzggResultData.setTb22GongYingShangMingCheng(tds.get(0).text().trim().replace(",", "，"));
                            dzmzggResultData.setTb22YuanYin(tds.get(1).text().trim().replace(",", "，"));


                        }else if(TB23.equals(tableFeatureStr) && tds.size() == tableFeatureStr.split("-").length){
                            dzmzggResultData.setTb23XuHao(tds.get(0).text().trim().replace(",", "，"));
                            dzmzggResultData.setTb23DanJia(tds.get(1).text().trim().replace(",", "").replace("￥",""));
                            dzmzggResultData.setTb23BaoJiaJinE(tds.get(2).text().trim().replace(",", "").replace("￥",""));
                            dzmzggResultData.setTb23ShangPinXingHao(tds.get(3).text().trim().replace(",", "，"));
                            dzmzggResultData.setTb23ShuLiang(tds.get(4).text().trim().replace(",", ""));
                            dzmzggResultData.setTb23CanShu(tds.get(5).text().trim().replace(",", "，"));


                        }else if(TB31.equals(tableFeatureStr) && tds.size() == tableFeatureStr.split("-").length){

                            dzmzggResultData.setTb31MingCheng(tds.get(0).text().trim().replace(",", "，"));
                            dzmzggResultData.setTb31ShuLiang(tds.get(1).text().trim().replace(",", ""));
                            dzmzggResultData.setTb31DanWei(tds.get(2).text().trim().replace(",", "，"));
                            dzmzggResultData.setTb31GongYingShangMingCheng(tds.get(3).text().trim().replace(",", "，"));
                            dzmzggResultData.setTb31GongYingShangBaoJia(tds.get(4).text().trim().replace(",", "").replace("￥",""));

                        }else if(TB32.equals(tableFeatureStr) && tds.size() == tableFeatureStr.split("-").length){

                            dzmzggResultData.setTb32XuHao(tds.get(0).text().trim().replace(",", "，"));
                            dzmzggResultData.setTb32XuQiuMingCheng(tds.get(1).text().trim().replace(",", "，"));
                            dzmzggResultData.setTb32XuQiuXuanXiang(tds.get(2).text().trim().replace(",", "，"));


                        }

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
                                        dzmzggResultData.setChengJiaoJinE(valueOfLineValue.substring(0, valueOfLineValue.indexOf("（")));
                                    }else if(lineValue.startsWith("项目编号：")){
                                        dzmzggResultData.setXiangMuBianHao(valueOfLineValue);
                                    }
                                }

                                if (lineValue.startsWith("采购单位：")) {
                                    dzmzggResultData.setCaiGouDanWei(valueOfLineValue);
                                }else if(lineValue.contains("成交供应商：")){
                                    dzmzggResultData.setChengJiaoGongYingShang(valueOfLineValue);
                                }else{
                                    if(dzmzggResultData.getChengJiaoJinE() == null)
                                        dzmzggResultData.setChengJiaoJinE("Null");
                                    if(dzmzggResultData.getYuSuanJinE() == null)
                                        dzmzggResultData.setYuSuanJinE("Null");
                                    if(dzmzggResultData.getCaiGouJiHuaBianHao() == null)
                                        dzmzggResultData.setCaiGouJiHuaBianHao("Null");
                                    if(dzmzggResultData.getXiangMuBianHao() == null)
                                        dzmzggResultData.setXiangMuBianHao("Null");
                                }

                                dzmzggResultData.setLuoKuanRiQi(pElem.get(pElem.size() - 1).text().trim());

                                dzmzggResultData.setTitle(dzmzggDataBean.getTitle());
                                dzmzggResultData.setPageurl("https://gdgpo.czt.gd.gov.cn" + dzmzggDataBean.getPageurl());
                                dzmzggResultData.setGdRegion(searchGdRegionByRegionCode(gdAndRegionMapping, dzmzggDataBean.getRegionCode()));
                                dzmzggResultData.setGdRegionChild(dzmzggDataBean.getRegionName());
                            }

                        }
                        return dzmzggResultData;
                    }).collect(Collectors.toList());
                    System.out.println(dzmzggResultDatas.size());
                    resultDataList.addAll(dzmzggResultDatas);
                }

            }

//            tableFeatures.add(tableFeatureStr);

        }

        if(resultDataList.size() > 0){
            String dzmzggResultDataObjectProperties=Arrays.stream(resultDataList.get(0).getClass().getDeclaredFields()).map(s -> s.getName()).collect(Collectors.joining(","));
            saveCsvContent(resultDataList, dzmzggResultDataObjectProperties);

        }

    }

    private void saveCsvHeader(String firstLineHeader) {
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(new File("src/main/resources/o.csv"), false), "GBK")) {
            CSVWriter csvWriter = new CSVWriter(writer, CSVWriter.DEFAULT_SEPARATOR, CSVWriter.NO_QUOTE_CHARACTER, '\\', "\n");
            csvWriter.writeNext(firstLineHeader.split(","));
            csvWriter.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveCsvContent(List<DzmzggResultData> resultDataList, String dzmzggResultDataObjectProperties) {
        String firstLineHeader = csvOutputHeader(dzmzggResultDataObjectProperties);
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(new File("src/main/resources/o.csv"), false), "GBK")) {
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

    public String searchGdRegionByRegionCode(Map<String, String> gdAndRegionMapping, String regionCode) {
        String searchGdResult  = gdAndRegionMapping
                .entrySet()
                .stream()
                .filter(s -> s.getValue().contains(regionCode)||s.getKey().contains(regionCode))
                .map(s -> s.getKey().substring(s.getKey().lastIndexOf("-")+1))
                .collect(Collectors.toList())
                .get(0);
        return searchGdResult;
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


    public String csvOutputHeader(String objectProperties){
        String csvOutputConfig = "";
        try(
                InputStream csvOutputConfigInput = new FileInputStream(new File("src/main/resources/csvOutputConfig"));
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
            log.error("src/main/resources/csvOutputConfig is empty!!");
        }
        return firstLineHeader;
    }

    public Map<String, String> createGdMapping() {
        Gson gsonForGdTree = new Gson();
        String gdTreeRaw = "";
        try(
            InputStream gdTreeInput = new FileInputStream(new File("src/main/resources/gdTree.json"));
        ){
            String gdTreeRawTmp = MyFileUtils.inputByteReader(gdTreeInput);
            gdTreeRaw = gdTreeRawTmp;
        }catch(IOException e) {
            e.printStackTrace();
        }
        //build gd region mapping
        List<GdTreeBean> gdTreeBeanList = gsonForGdTree.fromJson(gdTreeRaw, new TypeToken<List<GdTreeBean>>(){}.getType());
        Map<String, String> gdAndRegionMapping = new HashMap();
        List<GdTreeBean> newGdTree = gdTreeBeanList.stream().filter(s -> s.getChildren()!=null).collect(Collectors.toList());

        for(GdTreeBean gdTreeBean : newGdTree){
            String regions = gdTreeBean.getChildren().stream().map(s -> s.getRegionCode().trim()+"-"+s.getName().trim()).collect(Collectors.joining(","));
            System.out.println(gdTreeBean.getRegionCode().trim() + "-" + gdTreeBean.getName().trim() + ":" + regions);
            gdAndRegionMapping.put(gdTreeBean.getRegionCode().trim() + "-" + gdTreeBean.getName().trim(), regions);
        }
        return gdAndRegionMapping;
    }

    public static void main(String[] args) {
        DataFormatTest dataFormatTest = new DataFormatTest();
        dataFormatTest.test();
    }
}
