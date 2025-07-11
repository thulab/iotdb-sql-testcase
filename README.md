# iotdb-sql-testcase

## 一、项目介绍

```markdown
├── ainode_table                // AINode表模型测试目录
├── ainode_tree                 // AINode树模型测试目录
├── lib                         // 存放jar包的目录
├── table                       // 表模型测试目录
├── tree                        // 树模型测试目录
│   ├── CONFIG                  
│       └── otf_new.properties  // SQL覆盖工具配置文件
│   ├── scripts                 // 存放测试用例的目录
│   ├── README.md               // 测试目录说明文档
├── README.md                   // 项目说明文档
```

IoTDB SQL自动化脚本执行主要在Linux系统服务器上进行运行操作，目的是为了防止功能更新造成的功能BUG或兼容性BUG。

测试环境，会持续更新Apache-IoTDB仓库Master分支代码构建IoTDB安装包，并配置测试用例依赖的JAR包。
如：
- 安装配置trigger/UDF test cases运行环境（每个环境仅做一次,且使用root用户权限）

```shell
rsync -avz atmos@172.20.70.44:/data/support-soft .
cd support-soft
# target_path 默认/data/nginx
sudo ./setup-env.sh -u <user>

```

reference:
**UDF**：
- 测试文档: https://apache-iotdb.feishu.cn/sheets/shtcnBNOqZiICQvwyafSpKVy8Qe
- 测试java工程: https://github.com/changxue2022/iotdb-udf-test

**Trigger**：
- 测试文档: https://apache-iotdb.feishu.cn/sheets/shtcnMcsLTDUnYV95XBkjiddqAg
- 测试java工程: https://github.com/changxue2022/user-guide-trigger

----------

##  二、操作步骤

#### （一）部署IoTDB，添加依赖包到相关路径下（示例为：V0.14.0）
执行trigger/udf 测试用例：
放置jar包
- **UDF依赖**：
  * 将`lib/udf_jar/ext` 目录下的jar放置到目录 `$IOTDB_HOME/ext/udf` （也可以是`$IOTDB_HOME/ext/udf`的子目录）下.
  * 将`lib/udf_jar/local` 的两个jar 放到`/data/nginx` 目录下。

- **Trigger依赖**：
  * 将`lib/udf_jar/ext` 目录下的jar放置到目录 `$IOTDB_HOME/ext/udf` （也可以是`$IOTDB_HOME/ext/udf`的子目录）下.
  * 将`lib/udf_jar/local` 的两个jar 放到`/data/nginx` 目录下。


#### （二）SQL自动化工具中的驱动包需与IoTDB的lib包保持一致

需将`$IOTDB_HOME/lib`下所有的jar包全部拷贝到`iotdb-sql/user/driver/iotdb`目录下。

#### （三）SQL执行脚本`.run`统一在放置script文件夹中

```
//.run文件编写规则
connect root/root;   //连接用户
create/show/select/count/list...  //数据库相关的增删改成操作
<<NULL;             //不记录测试结果
<<SQLSTATE;        //校验异常

```

以`setup`模式生成的`.result`文件与`.run`文件最后统一存放在`iotdb-sql/user/scripts`目录下。

#### （四）需在`iotdb-sql/user/CONFIG/otf_new.properties`中修改配置：

```
DBtype             IOTDB
iotdbURL             jdbc:iotdb://172.20.31.7:6667/(修改为真实的ip和port)
iotdbDriver          org.apache.iotdb.jdbc.IoTDBDriver
iotdbUser            root（iotdb对应的用户名）
iotdbPasswd          root（iotdb对应的用户名密码）
interval           11000
maxCircle          1
user_ip            127.0.0.1
mode               setup(修改为：setup/test)
displayRow         10000
OVERTIME           1000
EndByMail          off
maxConnection      127
waitTime           20
```

##### 1. 首次启动SQL自动化工具，需将mode置为：`setup`模式，执行test.sh程序，生成result结果文件

##### 2. 接下来将mode更改为：`test`模式，后续每次执行test.sh均为该模式，生成.out文件

##### 3. `.out`文件会与`.result`文件进行对比差异

#### （五）当前环境下，测试用例不变的情况下，每次运行自动化工具，仅需参考（一）、（二）、（三）步骤即可。

----------

## 三、SQL-自动化工具目录结构描述

这是一个用于测试IoTDB的SQL语句的自动化工具，IoTDB SQL自动化脚本执行主要在Linux系统服务器上进行运行操作，目的是为了防止功能更新造成的功能BUG或兼容性BUG。

#### 1、总体目录结构

```
├── assert                      // 存放静态资源的目录
├── bin                         // class文件目录
├── lib						  
|   ├── jdom.jar                // jar包存放目录
├── src                         // 工具源码（执行测试的源码）
├── tools                       // 存放工具的目录
│   ├── benchmark               // benchmark工具
├── user                        // 数据库jdbc驱动/配置文件/用例
│   ├── CONFIG                  // 放配置文件的目录
│       ├── otf_new.properties  // iotdb-sql配置文件（连接数据库信息）
│   ├── driver                  // 放目标设备jar的目录
│       ├── iotdb               // 存放目标iotdb的lib目录下全部的jar包的目录（数据库jdbc驱动）
│       ├── POI                 // 资源依赖
│   ├── result                  // 结果索引（运行结果汇总）
│       └──ResultIndex.out	    // test模式下执行后的结果文件（用例级）
│   ├── scripts                 // 用例脚本目录(用于保存脚本文件夹)
├── compile.sh                  // 编译源码(Linux环境)
├── compile.bat                 // 编译源码(Windows环境)
├── README.md                   // 帮助文档
├── result.xml                  // 脚本级汇总结果（test模式下执行后的结果文件（脚本级））
├── test.sh                     // 启动工具进行用例测试的脚本(Linux环境)
├── test.bat                    // 启动工具进行用例测试的脚本(Windows环境)
```

#### 2、重要文件目录介绍

（1）otf_new.properties ：iotdb-sql-test 工具配置文件
·	位置：`iotdb-sql/user/CONFIG` 。
·	介绍：用于配置数据库信息和测试模式。

```
DBtype             IOTDB							    // 数据库类型
iotdbURL           jdbc:iotdb://127.0.0.1:6667/		      // 树模型数据库url地址（默认地址127.0.0.1，端口：6667）
//iotdbURL         jdbc:iotdb://127.0.0.1:6667?version=V_1_0&sql_dialect=table  // 表模型数据库url地址
iotdbDriver        org.apache.iotdb.jdbc.IoTDBDriver      // 驱动程序的全限定类名
iotdbUser          root								   // 数据库用户名
iotdbPasswd        root								   // 数据库密码
interval           11000							   // 时间间隔
maxCircle          1
user_ip            127.0.0.1                              // 测试用户标识（一般与ip地址一致）
mode               setup 							   // setup测试模式：根据.run文件生成.result参考文档
//mode             test                                   // test测试模式：用于根据.result参考文档生成.out测试结果文件）
displayRow         10000
OVERTIME           1000
EndByMail          off
maxConnection      127
waitTime           20
```



#### 3、使用步骤-Linux环境

~ 步骤一：部署IoTDB并启动iotdb

使用git拉取项目将iotdb-sql文件夹放一个合适的地方。【注意：使用Windows拉取再放到Linux中可能存在Windows符号在Linux中识别不了导致报错，故建议在什么环境使用就在该环境拉取】

~ 步骤二：导入目标数据库的jar包并编译源码

需将`目标数据库的lib目录`下所有的jar包全部拷贝到`项目/user/driver/iotdb`目录下，然后执行`complie.sh`文件编译源码。

~ 步骤三：修改配置文件

在`项目/user/CONFIG/otf_new.properties`中修改配置（已初配置文件信息为例）
·	修改iotdbURL参数：将ip和端口修改成自己数据库的ip和端口；
·	修改iotdbUser和iotdbPasswd参数：将用户名和密码修改成自己数据库的；
·	修改mode参数：首次启动SQL自动化工具，需将mode置为setup模式，接下来将mode更改为test模式，后续每次执行test.sh均为该模式；

#### 4、编写SQL执行脚本文件

在`项目/user/script`目录下先创建一个文件夹，在文件夹中编写 .run 的测试用例。

```
//.run文件编写规则
connect root/root;   //连接用户
create/show/select/count/list...  //数据库相关的增删改成操作
<<NULL;              // 不记录测试结果（一般用于无关紧要的的用例）
<<SQLSTATE;          // 校验异常（预期会报错的，一般用于错误情况的用例）
<<CHECKCODE;         // 不检查结果是否一致（一般用于每次会变化的查询）
sleep 1000;          // 休眠1000毫秒
分号的单独处理：单个分号为每句SQL的结束符，需要在字符串中使用分号需要对其添加转义字符反斜杠("\")，反斜杠只单独对分号进行转义，结合其他字符则无效果

```

#### 5、启动工具

在主目录下执行`test.sh`程序，生成基准文件 .result 。

#### 6、更改配置文件模式

在生成基准文件后修改配置文件模式为test

#### 7、再次启动工具

再在项目目录下执行`test.sh`程序，生成结果文件 .out 。


