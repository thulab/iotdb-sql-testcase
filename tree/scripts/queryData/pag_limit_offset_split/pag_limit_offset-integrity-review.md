# pag_limit_offset 拆分完整性复核

复核日期：2026-11-03。以原始 pag_limit_offset.run 为唯一对照，忽略空行和注释，逐条比较有效语句。

- 拆分文件：18 个，无额外 .run 文件。
- 原始 SELECT：32 条；拆分 SELECT：32 条；唯一查询：32 条。
- 查询遗漏：0；新增查询：0；重复查询：0。
- 查询后的 SQLSTATE 断言：9 条，逐条关联与顺序均与原文一致。
- 各原始分组内的查询顺序完全一致。
- 18 个文件的数据准备和收尾清理逐行等同于各自所属原始分组，包含 connect、delete、NULL 标记、建库、建序列、show、insert 和 flush。

| 文件 | SELECT 数 | 查询断言数 | 准备有效行数 | 清理有效行数 |
| --- | --- | --- | --- | --- |
| pag_limit_offset-row-basic.run | 4 | 2 | 41 | 3 |
| pag_limit_offset-row-offset.run | 4 | 1 | 41 | 3 |
| pag_limit_offset-row-where.run | 1 | 0 | 41 | 3 |
| pag_limit_offset-row-group-by.run | 1 | 0 | 41 | 3 |
| pag_limit_offset-row-fill.run | 3 | 0 | 41 | 3 |
| pag_limit_offset-row-fill-single-time.run | 1 | 0 | 41 | 3 |
| pag_limit_offset-row-fill-single-series.run | 1 | 0 | 41 | 3 |
| pag_limit_offset-row-fill-range.run | 1 | 0 | 41 | 3 |
| pag_limit_offset-series-basic.run | 3 | 2 | 39 | 3 |
| pag_limit_offset-series-offset.run | 3 | 1 | 39 | 3 |
| pag_limit_offset-series-group-by.run | 2 | 1 | 39 | 3 |
| pag_limit_offset-series-fill-range.run | 1 | 0 | 39 | 3 |
| pag_limit_offset-series-fill.run | 2 | 0 | 39 | 3 |
| pag_limit_offset-mixed-basic.run | 1 | 0 | 35 | 3 |
| pag_limit_offset-limit-exceeds-size.run | 1 | 0 | 35 | 3 |
| pag_limit_offset-limit-overflow.run | 1 | 1 | 35 | 3 |
| pag_limit_offset-limit-non-integer.run | 1 | 1 | 35 | 3 |
| pag_limit_offset-offset-exceeds-size.run | 1 | 0 | 35 | 3 |

查询场景无重复。各文件中的准备和清理语句存在有意重复，以保证独立执行；目前每个文件复制所属分组的完整准备数据，包含部分该场景不需要的序列或数据。未进行数据库运行验证，未生成 .result，以上结论为拆分完整性静态核对，不代表运行结果已验证。
