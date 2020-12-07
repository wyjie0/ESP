<%@ page import="java.util.Locale" %>
<%@ page contentType="text/html;charset=utf-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:if test="${sessionScope.locale!=null}">
    <fmt:setLocale value="${sessionScope.locale}"/>
</c:if>
<!-- 资源文件必须放置在src下 -->
<fmt:setBundle basename="message"/>

<!DOCTYPE html>
<html>
<head>
    <title><fmt:message key="loginPage"/></title>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <!-- 上述3个meta标签*必须*放在最前面，任何其他内容都必须跟随其后！ -->
    <!-- 注意路径的写法，路径并不存在，经过mvc来解析后可以找到 -->
    <link href="../ESP4/resources/bootstrap-3.3.7-dist/css/bootstrap.min.css" rel="stylesheet">
    <script src="../ESP4/resources/js/jquery-3.1.0.min.js"></script>
    <script src="../ESP4/resources/bootstrap-3.3.7-dist/js/bootstrap.min.js"></script>
    <link href="../ESP4/resources/css/login.css" rel="stylesheet" type="text/css"/>
</head>
<body>
<header class="container-fluid">
    <!-- 顶部图片部分 -->
    <div class='box row'>
        <div class='col-md-1'></div>
        <div class='col-md-10'>
            <p><fmt:message key="title"/></p>
        </div>
        <div class='col-md-1'></div>
    </div>
</header>
<div class='box1 row'>
    <div class='col-md-4'></div>
    <div class='box2 col-md-4 '>
        <p><fmt:message key="userLogin"/></p>
        <br>
        <form id="login">
            <fmt:message key="username"/>
            <input id="user_name" type="text" class='username1' name='username'
                   placeholder="<fmt:message key="usernamePlaceHolder"/>" style='color:black'>
            <br>
            <br>
            <fmt:message key="password"/>
            <input id="password" type="password" class='password1' name='password'
                   placeholder="<fmt:message key="passwordPlaceHolder"/>" style='color:black'>
            <br>
            <br>
            <input class='btn' id="loginBtn" type="button" value="<fmt:message key="login"/>"
                   style="background-color: white;color:black">
            <br>
            <br>
            <input class='btn' type="radio" name="userIdentity" value="creator" checked="checked">
            <fmt:message key="owner"/>
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            <input class='btn' type="radio" name="userIdentity" value="auditor">
            <fmt:message key="auditor"/>
        </form>
    </div>
    <div class='col-md-4'></div>
</div>
<script type="text/javascript">
    $(document).ready(function () {
        //登陆按钮按下
        $("#loginBtn").click(function () {
            //填写处不为空
            if ($("#user_name").val() != "" && $("#password").val() != "") {
                $.ajax({
                    url: "as",
                    type: "post",
                    data: $("form#login").serialize(),
                    success: function (code) {
                        //不要写成code === 1
                        if (code == 1) {
                            alert("<fmt:message key="loginSucceeded"/>");
                            location.href = "index";
                        } else if (code == 2) {
                            alert("<fmt:message key="loginSucceeded"/>");
                            //防止与requestmapping重名
                            location.href = "auditPage";
                        } else {
                            alert("<fmt:message key="loginFailed"/>");
                        }
                    },
                    error: function (XMLResponse) {
                        alert("<fmt:message key="error"/>" + XMLResponse.responseText);
                        console.log($("#login").serialize());
                    }
                })
            } else {
                alert("<fmt:message key="username&PasswordEmpty"/>");
            }
        });

        <%
            System.out.println(session.getAttribute("locale"));
            if (session.getAttribute("locale") == null) {
                    session.setAttribute("locale", new Locale("en", "US"));
            }
        %>
        //页面刷新：读取语言资源文件
        if (location.href.indexOf('#reloaded') == -1) {
            location.href = location.href + "#reloaded";
            location.reload();
        }
    });
</script>
</body>
</html>