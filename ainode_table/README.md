# iotdb-sql-testcase-ainode

给予 sql-test 的 ainode 测试

## 关于 ainode

### 打包

```shell
cd ${IOTDB_HOME}
mvn clean package -DskipTests -am -pl distribution -P with-ainode
```

打包成功后，文件存储在：`${IOTDB_HOME}/distribution/target/apache-iotdb-${version}-ainode-bin.zip`

### 配置参数

ainode 配置文件在：`conf/iotdb-ainode.properties`

```properties
# 集群名称
cluster_name=defaultCluster
ain_seed_config_node=127.0.0.1:10710
ain_inference_rpc_address=127.0.0.1
ain_inference_rpc_port=10810
```

## 部署环境

操作系统 == ubuntu (latest)  
python == 3.11 (建议)

无论什么系统 or 环境，推荐使用 miniconda。

## 资源文件

以下文件均可在此下载：https://nas.timecho.com:5001/sharing/yOMR2iow9

1. Miniconda3
   安装步骤：

   ```shell
   chmod +x  Miniconda3-py311_25.1.1-2-Linux-x86_64.sh
   ./ Miniconda3-py311_25.1.1-2-Linux-x86_64.sh
   ```

   根据提示键入“回车”、“长按空格”、“回车”、“yes”、“yes”，即可完成安装，重新进入 shell 即可

2. 测试用 `tsfile`

3. 模型包 models.zip，包含 `timer` 和 `dlinear`

4. 内置模型`timer_xl`的模型文件 timerxl.zip

## 测试流程：

1. 安装 & 配置 & 启动 IoTDB

2. 安装 & 配置 & 启动 ainode（初次启动会下载依赖）

3. 将 tsfile 放到 `/data/root/ainode`下

```shell
# tree /data/root/ainode

/data/root/ainode/
├── ai-data.tsfile
└── ai-data.tsfile.resource

```

4. 将 模型包 解压后放到 `/root/data/ainode/models` 下

```shell
# tree /data/root/ainode/models

/data/root/ainode/models/
├── timer
│   ├── config.yaml
│   └── model.pt
└── dlinear
    ├── config.yaml
    └── model.pt

```

5. 将 \timer_xl 模型包解压后放入 `data/ainode/models/weights/` 目录

```shell
cd <ainode_home>
mkdir -p data/ainode/models/weights/timerxl
mv timerxl/model.safetensors data/ainode/models/weights/timerxl

# tree data
data
└── ainode
    └── models
       └── weights
           └── timerxl
               └── model.safetensors
```

6. 拉取 & 编译 & 启动 sql-test 测试工具（`sqldialect=tree` & `mode=test`）
