<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="renderer" content="webkit">
    <title>平潭综合实验区网格化服务管理信息平台</title>
    <style>
        .login_check input[type="checkbox"] {
            margin-top: -2px;
            margin-right: 8px;
            width: 18px;
            height: 18px;
            vertical-align: middle;
        }
    </style>
    <link href="${pageContext.request.contextPath}/static/css/style.css" rel="stylesheet" type="text/css"/>
    <link href="${pageContext.request.contextPath}/static/css/animate.css" rel="stylesheet" type="text/css"/>

    <script type="text/javascript"
            src="${pageContext.request.contextPath}/static/plugins/jquery-3.2.0/jquery-3.2.0.min.js"></script>
    <script type="text/javascript">
        $(function () {
            delCookie("model");
            var errorMsg = "${error}";
            if (errorMsg != null && errorMsg != '') {
                $("#loginNameDiv").show();
                $("#loginNameSpan").html(errorMsg);
            }
        });
        //设置cookie
        function setCookie(name, value) {
            var Days = 30;
            var exp = new Date();
            exp.setTime(exp.getTime() + Days * 24 * 60 * 60 * 1000);
            document.cookie = name + "=" + escape(value) + ";expires=" + exp.toGMTString() + ";path=/";
        }
        //删除cookie
        function delCookie(name) {
            var exp = new Date();
            exp.setTime(exp.getTime() - 1);
            var cval = getCookie(name);
            if (cval != null)
                document.cookie = name + "=" + cval + ";expires=" + exp.toGMTString() + ";path=/";
        }
        //获取cookie
        function getCookie(name) {
            var arr, reg = new RegExp("(^| )" + name + "=([^;]*)(;|$)");
            if (arr = document.cookie.match(reg))
                return unescape(arr[2]);
            else
                return null;
        }
        function ssoLogin() {
            var loginName = $("input[name='loginName']").val();
            var password = $("input[name='password']").val();
            if (loginName == null || loginName == '') {
                $("#loginNameDiv").show();
                $("#loginNameSpan").html("请填写用户名！");
                return;
            }
            if (password == null || password == '') {
                $("#passwordDiv").show();
                $("#passwordSpan").html("请填写密码！");
                return;
            }
            $('#ssoLoginForm').submit();
        }
        function validate(name, htmlStr) {
            var value = $("input[name='" + name + "']").val();
            if (value == null || value == '') {
                $("#" + name + "Div").show();
                $("#" + name + "Span").html(htmlStr);
                return;
            } else {
                $("#" + name + "Div").hide();
                return;
            }
        }

        function keyDown() {
            if (event.keyCode == 13)
            {
                event.returnValue=false;
                event.cancel = true;
                ssoLogin();
            }
        }

    </script>
</head>

<body class="eui-login-bg animated" onkeydown="keyDown();">>
<form action="" method="post" id="ssoLoginForm">
    <div class="eui-login-box">
        <h3 class="animated-long"></h3>
        <div class="eui-login-input animated-long">
            <div class="eui-login-user">
                <input type="text" placeholder="用户名……" name="loginName" autocomplete="off"
                       onblur="validate('loginName','请填写用户名！')" />
                <div class="eui-login-bubble" id="loginNameDiv" style="display:none;"><i></i><span id="loginNameSpan">请填写用户名！</span>
                </div>
            </div>
            <div class="eui-login-password">
                <input type="password" placeholder="密码……" name="password" autocomplete="off"
                       onblur="validate('password','请填写密码！')"  />
                <div class="eui-login-bubble" id="passwordDiv" style="display:none;"><i></i><span id="passwordSpan">请填写密码！</span>
                </div>
            </div>
        </div>
        <div class="eui-login-sub animated-long">
            <a href="javascript:ssoLogin();">授权并登录</a>
        </div>
    </div>
</form>
</body>
</html>