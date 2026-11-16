# last_query.run 拆分说明

源文件保持不变。按场景拆为 8 个独立脚本；前 7 个仅保留各自查询所需的数据准备与清理，第 8 个保留第二节完整流程。SQL、sleep 和结果标记保持原样。

| 文件 | 场景 | 结果标记数 |
| --- | --- | --- |
| last_query-basic.run | 最新点查询：通配符与指定序列 | 2 |
| last_query-time-filter.run | 最新点查询：时间下界过滤 | 2 |
| last_query-timeseries-order.run | 最新点查询：按序列名降序排列 | 2 |
| last_query-pagination.run | 最新点查询：LIMIT OFFSET 与排序分页 | 2 |
| last_query-unsupported-series-pagination.run | 最新点查询：拒绝 SLIMIT SOFFSET | 4 |
| last_query-non-aligned-user-data.run | 最新点查询：非对齐用户数据与排序 | 2 |
| last_query-aligned-alias.run | 最新点查询：对齐序列、别名与特殊字符 | 3 |
| last_query-last-value-comparison.run | 最新点查询：空序列及 BLOB 的 LAST 与 LAST_VALUE 对比 | 2 |

静态核对通过：原文件查询、sleep 与结果标记全部保留；无关准备语句已按 insert-data-review.md 清理；新增文件 UTF-8 无 BOM，首条有效语句均为 connect。未连接数据库执行，未导入 PLM。各拆分文件须重新生成 .result 基准。
