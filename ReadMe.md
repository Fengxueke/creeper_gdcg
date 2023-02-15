##使用jsoup爬取广东省政府采购网的数据
###使用方法
#####1. 安装python和ddddocr， 用于验证码的识别
#####2. 设置raw.data.url.operationStartTime，raw.data.url.operationEndTime和all.raw.data.path， 具体的设置请参考后面的说明
#####3. 运行DownloadRowData.java开始爬取数据，并等待完成
#####4. 运行HandleRawData.java开始解析爬取到的数据， 结果会保存到src/main/resources/result.csv

###说明
####DownloadRowData.java
用于爬取数据

####HandleRawData.java
用于处理爬取的数据，并输出为csv表格

####app.properties
配置文件，具体如下

* index.url，采购页面主页， 主要用来获取cookie
* verify.code.url，验证码链接
* raw.data.url，获取目标数据的链接
* raw.data.url.operationStartTime，配置查看目标数据的（公告）开始时间
* raw.data.url.operationEndTime，配置查看目标数据的（公告）结束时间，这里的时间间隔不宜太大， 否则可能导致内存溢出。经测试，1年的数据，idea设置-Xmx2031m， 内存没有溢出
* verify.code.orc.2.text.script，用于将验证码图片转换成文字的python脚本
* verify.code.image.path，验证码图片存放位置
* all.raw.data.path，目标数据（未处理）的存放位置
* result.csv.path，处理好的结果文件存放位置
* result.csv.config.file，结果文件的文件头配置
* gd.mapping.file，广东省的行政区划关系
* details.url，公告结果链接的开头部分

####result.csv
关于输出的结果文件， 文件内容分为基本数据（一条公告）和基本数据中包含的表格数据（多个）\
即一条公告链接，包含多个表格，这是一对多的关系\
为了方便，我把公告的基本信息的字段，和其中包含的多个表格的字段放到了同一行里面，\
公告基本信息的字段：\
标题	公告链接	广东地级市	广东地级市城区	项目名称	项目编号	采购单位	预算金额	采购编号	成交供应商	成交金额	公告日期\
公告中多个表格的字段：\
tb11产品名称	tb11技术规格	tb11备注	tb11数量	tb11单价（元）	tb11金额（元）	tb12服务描述	tb12数量	tb12单位	tb12供应商报价(元)	tb12是否中标	tb13供应商名称	tb13供应商报价(元)	tb13报价时间	tb14排名	tb14供应商名称	tb14总报价(元)	tb14报价时间	tb15排名	tb15供应商	tb15品牌	tb15型号	tb15数量	tb15单价	tb15报价金额(元)	tb15报价时间	tb21编号	tb21采购品目	tb21名称	tb21品牌	tb21型号	tb21数量	tb21主要技术参数	tb21金额(元)	tb22供应商名称	tb22原因	tb23序号	tb23单价(元)	tb23报价金额(元)	tb23商品型号	tb23数量	tb23参数	tb31名称	tb31数量	tb31单位	tb31供应商名称	tb31供应商报价(元)	tb32序号	tb32需求名称	tb32需求选项\

在对22年公告进行扫描后， 表格大致有10种：\
tb11 = "产品名称-技术规格-备注-数量-单价（元）-金额（元）";\
tb12 = "服务描述-数量-单位-供应商报价(元)-是否中标";\
tb13 = "供应商名称-供应商报价(元)-报价时间";\
tb14 = "排名-供应商名称-总报价(元)-报价时间";\
tb15 = "排名-供应商-品牌-型号-数量-单价-报价金额(元)-报价时间";\
tb21 = "编号-采购品目-名称-品牌-型号-数量-主要技术参数-金额(元)";\
tb22 = "供应商名称-原因";\
tb23 = "序号-单价(元)-报价金额(元)-商品型号-数量-参数";\
tb31 = "名称-数量-单位-供应商名称-供应商报价(元)";\
tb32 = "序号-需求名称-需求选项";\

其中tb1， tb2， tb3是对表格的分类， 应该对应不同的公告类型\
