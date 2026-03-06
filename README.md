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
├── tsfile						// TSFile文件测试目录
|   ├── table					// 表模型测试目录
|   ├── tree					// 树模型测试目录
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

#### （一）部署IoTDB，添加依赖包到相关路径下
执行trigger/udf 测试用例：
放置jar包
- **UDF依赖**：
  * 将`lib/udf_jar/ext` 目录下的jar放置到目录 `$IOTDB_HOME/ext/udf` （也可以是`$IOTDB_HOME/ext/udf`的子目录）下.
  * 将`lib/udf_jar/local` 的两个jar 放到`/data/nginx` 目录下。

- **Trigger依赖**：
  * 将`lib/udf_jar/ext` 目录下的jar放置到目录 `$IOTDB_HOME/ext/udf` （也可以是`$IOTDB_HOME/ext/udf`的子目录）下.
  * 将`lib/udf_jar/local` 的两个jar 放到`/data/nginx` 目录下。


#### （二）SQL自动化工具中的驱动包需与IoTDB的lib包保持一致

需将`$IOTDB_HOME/lib`下所有的jar包全部拷贝到`/iotdb-sql-test/user/driver/iotdb`目录下。

#### （三）SQL执行脚本`xxx.run`统一放在script文件夹中

```
//.run文件编写规则
connect root/root;   //连接用户
create/show/select/count/list...  //数据库相关的增删改成操作
<<NULL;             //不记录测试结果
<<SQLSTATE;        //校验异常

```

以`setup`模式生成的`xxx.result`文件与`xxx.run`文件最后统一存放在`iotdb-sql-test/user/scripts`目录下。

#### （四）需在`iotdb-sql-test/user/CONFIG/otf_new.properties`中修改配置：

```
DBtype             IOTDB
iotdbURL             jdbc:iotdb://127.0.0.1:6667/(修改为真实的ip和port)
iotdbDriver          org.apache.iotdb.jdbc.IoTDBDriver
iotdbUser            root（IoTDB对应的用户名）
iotdbPasswd          root（IoTDB对应的用户名密码）
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

- 位置：`iotdb-sql-test/user/CONFIG` 
- 介绍：用于配置数据库信息和测试模式
- 配置信息如下：

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

（2) special_query.csv : 输出结果集过滤列配置文件

- 位置：`iotdb-sql-test/user/CONFIG`
- 介绍：用于在执行SQL查询用例过程中，对输出的结果集进行列级别的过滤控制，需要从最终展示结果集中排除的列
- 配置示例如下：

```
show version;BuildInfo;
list user;UserId;
```

#### 3、使用步骤-Linux环境

- 步骤一：部署IoTDB并启动iotdb

使用git拉取项目将`iotdb-sql-test`文件夹放一个合适的地方。【注意：使用Windows拉取再放到Linux中可能存在Windows符号在Linux中识别不了导致报错，故建议在什么环境使用就在该环境拉取】

- 步骤二：导入目标数据库的jar包并编译源码

需将`目标数据库的lib目录`下所有的jar包全部拷贝到`项目/user/driver/iotdb`目录下，然后执行`complie.sh`文件编译源码。

- 步骤三：修改配置文件

在`项目~/iotdb-sql-test/user/CONFIG/otf_new.properties`中修改配置（已初配置文件信息为例）
（1）修改iotdbURL参数：将ip和端口修改成自己数据库的ip和端口；
（2）修改iotdbUser和iotdbPasswd参数：将用户名和密码修改成自己数据库的；
（3）修改mode参数：首次启动SQL自动化工具，需将mode置为setup模式，接下来将mode更改为test模式，后续每次执行test.sh均为该模式；

- 根据执行SQL用例情况，需在`~/iotdb-sql-test/user/CONFIG/special_query.csv`进行配置，对输出结果集进行列过滤

#### 4、编写SQL执行脚本文件

- 首先在`~/iotdb-sql-test/user/script`目录下创建一个文件夹或递归创建文件夹
- 然后在文件夹中创建以`xxx.run`结尾命名的SQL测试用例
- 关于`xxx.run`文件名命名规则如下：

`推荐统一命名规则：统一全小写，支持a-z、0-9、-（连字符）或_（下划线）`

- 关于`xxx.run`文件里面SQL编写规则如下：

```
-- 编写说明：
-- 1. 注释格式：-- 或 // ；
-- 2. 特殊关键字：<<NULL;  |  <<SQLSTATE;  |  <<CHECKCODE; |  <<ERRORCODE;
--  <<NULL;：表示期望能执行语句但不记录测试结果，无论是否成功还是失败都不会对其进行检测，可标记全部语句；
--  <<SQLSTATE;：表示期望语句报错，不报错会返回异常，可以标记全部语句；
--  <<CHECKCODE;：表示期望语句成功且不检查返回的结果，只能标记查询语句，标记非查询语句会报类型错误；
--  <<ERRORCODE;：表示期望语句报错，只返回错误码信息，可以标记全部语句；
--  上面关键字不写就正常判断是否成功；
-- 3. 分号的单独处理：单个分号为每句SQL的结束符，需要在字符串中使用分号需要对其添加转义字符反斜杠("\")，反斜杠只单独对分号进行转义，结合其他字符则无效果
-- 4. SQL用例开头与结尾都需要连接数据库并清理环境
-- connect root/root;   -- 连接用户
-- drop database root.**;    -- tree model
-- <<NULL;
-- drop database test;       -- table model
-- <<NULL;
-- 5. SQL相关的DDL和DML
create/show/select/count/list...  //数据库相关的增删改成操作
-- 6. 执行过程中，可增加sleep时间来延长返回结果节
sleep 1000;          // 休眠1000毫秒
```

#### 5、启动工具

在主目录下执行`test.sh`程序，生成基准文件 .result 。

#### 6、更改配置文件运行模式

在生成基准文件后修改配置文件模式为test

#### 7、再次启动工具

再在项目目录下执行`test.sh`程序，生成结果文件 .out 。
