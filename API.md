## 习习向上 API 文档

### 请求规范

以下的API使用的是Https协议，需要使用HttpsURLConnection类来请求。

此类默认使用Get方法请求

### 登录API

以下列出习习向上有效的登录API。含有API的请求地址、请求方式、请求参数和请求结果。部分包括示例和备注。

#### 获取登录Token

###### 请求

| 项目 | 内容                                                         |
| ---- | ------------------------------------------------------------ |
| 地址 | https://eapi.ciwong.com/gateway/oauth/v2/token               |
| 方式 | GET                                                          |
| 示例 | https://eapi.ciwong.com/gateway/oauth/v2/token?&clientId=20008&brandId=570678343&userName=10086&passWord=12345 |

###### 参数

| 元素名   | 值或值说明             |
| -------- | ---------------------- |
| clientId | 20008                  |
| brandId  | 570678343              |
| userName | 账号（一般是电话号码） |
| passWord | 密码                   |

###### 返回示例

```
{"ret":0,"errcode":0,"msg":"success","data":{"accessToken":"5d2f","expiresIn":99999999,"refreshToken":"5d2f","refreshTokenExpiresIn":99999999,"userId":123456,"mobile":"10086","realName":"小明","nickName":"小明","avatar":""}}
```

###### 返回

| 值                    | 类型       | 说明                 | 枚举                                          |
| --------------------- | ---------- | -------------------- | --------------------------------------------- |
| ret                   | int        | 返回值               | **0**：正常 **3**：账号或密码错误             |
| errcode               | int        | 错误代码             | **0**：正常 **28**：账号错误 **29**：密码错误 |
| msg                   | String     | 错误描述             | 无                                            |
| **accessToken**       | **String** | **登录Token**        | **无**                                        |
| expiresIn             | int        | Token到期时间戳      | 无                                            |
| refreshToken          | String     | 刷新Token            | 无                                            |
| refreshTokenExpiresIn | int        | 刷新Token到期时间戳  | 无                                            |
| userId                | int        | 用户ID               | 无                                            |
| mobile                | String     | 电话号               | 无                                            |
| realName              | String     | 实名                 | 无                                            |
| nickName              | String     | 昵称                 | 无                                            |
| avatar                | String     | 头像地址（可以为空） | 无                                            |

###### 备注

1. refreshToken是用来刷新  登录Token的Token



#### 获取班级信息

###### 请求

| 项目 | 内容                                                         |
| ---- | ------------------------------------------------------------ |
| 地址 | https://eapi.ciwong.com/gateway/v4/relation/class/get_my_classes |
| 方式 | GET                                                          |
| 示例 | https://eapi.ciwong.com/gateway/v4/relation/class/get_my_classes?clientId=20008&accessToken=xxx |

###### 参数

| 元素名     | 值或值说明 |
| ---------- | ---------- |
| clientId   | 20008      |
| accessToke | 登录Token  |

###### 返回示例

```
{"ret":0,"errcode":0,"msg":"success","data":[{"classId":100001,"className":"三年6班","studentSize":50,"classAvatar":"http://rimg3.ciwong.net/6v68/tools/images/100001_class_logo.png","classType":6,"userRole":1,"gradeId":1109,"schoolArea":"1234","SchoolID":9999,"SchoolName":"第一中学"}]}
```

###### 返回

| 值                      | 类型        | 说明         | 枚举                                                         |
| ----------------------- | ----------- | ------------ | ------------------------------------------------------------ |
| ret                     | int         | 返回值       | **0**：正常 **3**：请求失败                                  |
| errcode                 | int         | 错误代码     | **0**：正常 **17**：无法授权Token **107**：clientId和Token不对应 **109**：clientId不存在 |
| msg                     | String      | 错误描述     | 无                                                           |
| cause（只有出错时可用） | String      | 错误原因     | 无                                                           |
| **classId**             | **Stringb** | **班级ID**   | **无**                                                       |
| **className**           | **String**  | **班级名**   | **无**                                                       |
| classAvatar             | String      | 班级logo地址 | 无                                                           |
| classType               | int         | 班级类型     | 未知                                                         |
| userRole                | int         | 用户身份     | **1**：学生                                                  |
| gradeId                 | int         | 年级id       | 无                                                           |
| schoolArea              | String      | 学校区域     | 无                                                           |
| SchoolID                | int         | 学校id       | 无                                                           |
| **SchoolName**          | **String**  | **校名**     | **无**                                                       |



#### 获取用户作业列表

| 项目 | 内容                                                         |
| ---- | ------------------------------------------------------------ |
| 地址 | https://eapi.ciwong.com/gateway/v2/studentWork/getUserWorks  |
| 方式 | GET                                                          |
| 示例 | https://eapi.ciwong.com/gateway/v2/studentWork/getUserWorks?pageSize=20&page=1&workMode=3&brandId=570678343&clientId=20008&accessToken=xxx |

###### 参数

| 元素名            | 值或值说明                                               |
| ----------------- | -------------------------------------------------------- |
| pageSize          | 每一页的作业数量                                         |
| page              | 页码                                                     |
| workMode          | 作业模式：1：机房考试 0：全部作业 3：全部作业  选择**3** |
| brandId, clientId | 570678343, 20008                                         |
| accessToken       | 登录Token                                                |