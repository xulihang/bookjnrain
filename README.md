听雨扫描器（自己先这样叫）
=======

#功能

  通过扫描条码，统计图书信息，数字化管理资源。

#语言选择

+ Java 

  Android的官方编程语言，功能强大，支持多，但需要重头学Java。表示对Java理解不能。

+ 网络技术（AppCan，phonegap 或 Intel xdk）  

  采用网页技术编写程序，提供全平台支持。现在我只需适配Android，暂不考虑。

+ Basic4android  

  语法类似VB，IDE简洁实用，调试功能很强，生成的是与Java一样的bytecode，效率一样。现在Basic4android  的社区支持也还是很给力的。我本身学过AU3，使用起来很顺滑，就选它了。


#现状

  框架大体写好了，UI简单也设计了一下。

#后端

  现采用openshift提供的免费空间做后台。由于gfw，只能用https连接，但不影响使用。

  https://bottle-bookjnrain.rhcloud.com/index （登入数据页面）

  https://bottle-bookjnrain.rhcloud.com/get （下载sqlite数据库文件）

  https://bottle-bookjnrain.rhcloud.com/getxls （生成并提供xls文件下载）

  https://bottle-bookjnrain.rhcloud.com/query （在线查询页面）

  https://bottle-bookjnrain.rhcloud.com/reset（危险操作，清空数据库）


  
