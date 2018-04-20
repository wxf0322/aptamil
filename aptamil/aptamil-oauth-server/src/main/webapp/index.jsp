<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>统一用户管理系统OAuth2.0认证流程说明文档</title>
</head>

<body style="display:none">
<xmp theme="cerulean">
# 统一用户管理系统OAuth2.0认证流程说明文档
--------

### <font color="red">《统一用户管理系统API文档》，见[此处](./views/api.jsp)</font>

## 一、网页端接入流程说明

###1. 先在统一用户管理系统中注册对接应用

统一用户管理系统会分配给对接系统 **client_id** 和 **client_secret**

|   参数名       |   值                                | 参数说明   |
|---------------|-------------------------------------|-----------|
| client_id     | 6433ada9-de68-40ba-89a0-7aa8ee9128df | 应用id     |
| client_secret | 3910c13e-33e0-437c-ac96-b487de9f1141 | 应用secret |

###2. 请求授权码

####2.1 请求地址

```
http://127.0.0.1:8080/usms/authorize?client_id=6433ada9-de68-40ba-89a0-7aa8ee9128df&response_type=code&redirect_uri=http://www.baidu.com
```

####2.2 参数说明

|   参数名       |   参数说明     |
|---------------|--------------|
| client_id     | 应用id        |
| response_type | 返回授权码的标识，固定值code |
| redirect_uri  | 回调地址       |

上面的网站会打开Oauth Server的用户登录页面，用户输入正确的用户名和密码，会重定向到用户所填的回调地址，并在地址后方携带授权码。请求成功后会返回如下的页面：

```
http://www.baidu.com/?code=63910432da9186b22b1ad888d55ae8ae
```

其中 code=63910432da9186b22b1ad888d55ae8ae 即 **授权码**

###3. 换取access_token (POST操作)

####3.1 请求地址

```
http://127.0.0.1:8080/usms/access
```

首先GET方式请求该页面，会出现一个表单，在该表单中填入必填项，具体详见参数说明。

表单将会以POST方式提交到 http://127.0.0.1:8080/usms/accessToken ，最终返回 access_token

####3.2 参数说明

|   参数名       |   值                                 | 参数说明    |
|---------------|--------------------------------------|-----------|
| client_id     | 6433ada9-de68-40ba-89a0-7aa8ee9128df | 应用id     |
| client_secret | 3910c13e-33e0-437c-ac96-b487de9f1141 | 应用secret |
| grant_type    | authorization_code                   | 用于传递授权码的参数，固定值 |
| code          | 63910432da9186b22b1ad888d55ae8ae     | 用户登录授权后的授权码 |
| redirect_uri  | http://www.baidu.com                 | 回调地址    |

####3.2 返回参数

```
{ "expires_in":3600, "access_token":"223ae05dfbb0794396fb60a0960c197e" }
```

###4. 测试 access_token

####4.1 请求地址

```
http://127.0.0.1:8080/usms/v1/openapi/user?access_token=223ae05dfbb0794396fb60a0960c197e
```

测试成功的话可以返回当前用户名的详细信息，access_token=223ae05dfbb0794396fb60a0960c197e 为上一步获取的 **access_token**

###5. 简化模式

####5.1 请求地址

```
http://127.0.0.1:8080/usms/authorize?client_id=6433ada9-de68-40ba-89a0-7aa8ee9128df&response_type=token&redirect_uri=http://www.baidu.com
```

####5.2 参数说明

|   参数名       |   值         |
|---------------|--------------|
| client_id     | 应用id        |
| response_type | 返回授权码的标识，固定值token |
| redirect_uri  | 回调地址       |

上面的网站会打开Oauth Server的用户登录页面，用户输入正确的用户名和密码，会重定向到用户所填的回调地址，并在地址后方携带授权令牌。

请求成功后会返回如下的页面：


```
HTTP/1.1 302 Found
Location: http://www.baidu.com/#access_token=27893fa79e38e449baf09fc2b066f1cc&expires_in=3600
```

重定向到 http://www.baidu.com

其中 access_token=27893fa79e38e449baf09fc2b066f1cc 即为访问令牌，expires_in=3600 为访问令牌有效时间。

###5.3 备注

该接口相应在前端完成，assess_token对用户可见，请在前端调用需要访问的api。

</xmp>

<script src="${pageContext.request.contextPath}/static/plugins/strapdown/strapdown.js"></script>

</body>
</html>