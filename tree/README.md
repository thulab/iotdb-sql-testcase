# 树模型SQL覆盖
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
