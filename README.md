# iotdb-sql-testcase

### 一、环境依赖

IoTDB SQL自动化脚本执行主要在Linux系统服务器上进行运行操作，目的是为了防止功能更新造成的功能BUG或兼容性BUG。

测试环境，会持续更新Apache-IoTDB仓库Master分支代码构建IoTDB安装包，并配置测试用例依赖的JAR包。
如：
**UDF依赖**：当前测试的UDF类，需将JAR 包放置到目录 `$IOTDB_HOME/ext/udf` （也可以是`$IOTDB_HOME/ext/udf`的子目录）下。 
**Trigger依赖**：需将lib/trigger_jar内JAR包(不包括stateful的2个)放置到目录 `$IOTDB_HOME/ext/trigger` （也可以是`$IOTDB_HOME/ext/trigger`的子目录）下，将stateful开头的2个jar包放到目录/data/trigger下。

###  二、操作步骤

#### （一）部署IoTDB，添加依赖包到相关路径下（示例为：V0.13.0）
执行trigger/udf 测试用例：
1. 需要替换IP地址：
```shell
sed -i 's/172.20.31.30/新的ip/g' user/scripts/processData/trigger/*.run
```
2. 放置jar包
```markdown
//进入到IoTDB主文件夹路径下，cd 到trigger相关目录下，添加依赖jar包
$IOTDB_HOME/ext/trigger
//进入到IoTDB主文件夹路径下，cd 到udf相关目录下，添加依赖jar包
$IOTDB_HOME/ext/udf
```
3. 安装docker-ce,docker-compose
```shell
rsync -avz atmos@172.20.70.44:/data/support-soft .
cd support-soft/docker && yum install -y centos/*
cd ../
sudo cp docker-compose /usr/bin/
```
4. 加载nginx docker镜像
```shell
scp atmos@172.20.70.44:/data/support-soft/nginx_v1.23.2-alpine.tar .
docker load -i nginx_v1.23.2-alpine.tar
```
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

### 三、SQL-自动化工具目录结构描述

```markdown
├── Readme.md                    // help
├── bin                         // class文件目录
├── lib
|   ├── jdom.jar                //jar包存放目录
├── result.xml                  //脚本级汇总结果
├── src                         //工具源码
├── test.sh                     //运行测试
├── tools                       //当前benchmark工具存放路径
├── user                        // 数据库jdbc驱动/配置文件/用例
│   ├── CONFIG                  //配置
│   ├── driver                  // iotdb的lib目录下全部的jar包
│   ├── result                  // 结果索引
│       └──ResultIndex.out
│   ├── scripts                 // 用例执行脚本
```

### 四、当前测试用例涵盖模块

| 序号 |      覆盖功能模块       |                             备注                             |
| :--: | :---------------------: | :----------------------------------------------------------: |
|  1   |         元数据          | 如存储组，节点，时间序列，元数据模板，TTL，自动创建元数据等操作- |
|  2   |        数据查询         | 如选择表达式查询，过滤条件查询，结果分页查询，结果对齐格式查询，聚合查询，最新点查询，空值查询，空值过滤，性能追踪，*和**通用符查询 |
|  3   |        运维命令         |                如FLUSH，MERGE，CLEAR CACHE等                 |
|  4   |         元数据          | 如存储组，节点，时间序列，元数据模板，TTL，自动创建元数据等操作 |
|  5   | 查询写回（select into） |                              -                               |
|  6   |    触发器（Trigger）    |                              -                               |
|  7   |  用户自定义函数（UDF）  |                              -                               |
|  8   |        权限管理         |   角色，用户，权限，用户与角色，角色与权限，用户与权限等-    |

### 五、后续补充

可参照run文件规则编写测试用例，本地运行生成result文件，然后将`.run`文件和`.result`文件统一放到测试工具scripts文件目录下。

#### 已知问题处理

trigger/privilege_trig.run
问题说明：
给普通用户在root.**路径的CREATE_TRIGGER权限，脚本中的第128，129行：
128 list privileges user lily_create_trig on root.sg1;
129 list privileges user lily_create_trig on root.sg2;
root.sg1,root.sg2路径看不到CREATE_TRIGGER权限，此bug已记录到IOTDB-2797。
现在的处理方式是，按照现有iotdb的表现（错误表现），来标记SQL语句结果类型，比如
128，129行的list privileges应该不为空（现在.result中为空）。
133，145，155行的create trigger应该成功（现在标记<<SQLSTATE）。
143，165的show triggers应该有对应结果（现在.result中为空）。
168，169，170的drop trigger应该成功（现在标记<<SQLSTATE）

权限模块：
1. 赋予用户角色操作权限（GRANT_USER_ROLE）和撤销用户角色操作权限（REVOKE_USER_ROLE）功能异常，关于这块的测试用例采取注释处理，后续bug修复再启动测试。
2. 删除存储组权限（DELETE_STORAGE_GROUP）功能丢失，相关用例采取注释处理，后续bug修复再启动测试。

数据处理模块：
Select into 创建出非法时间序列：select temperature into h1 from root.sg1.**;
创建出时间序列 root.sg1.**.h1

对应bug修复后，test会失败，到时分析结果，修改.run脚本，重新生成.result即可。
