<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8"%>
<html>
<head>
    <title>drdh's search engine</title>
</head>
<body>
<br><br>
<form method = "POST" action = "result.jsp">
    <p align = "center"><font size = "12" face="Microsoft YaHei" color = "#f4a460">drdh's search engine</font></p><br><br>
    <p align = "center">
        <font size = "12">
            <input type = "text" name = "query" style = "width:400px;height:40px" id="kw"><input type = "submit" value = "search" style = "width:80px;height:40px" id="su">
        </font>
    </p>
</form>

<style>
    body
    {
        background:url(./green.png);
        background-size:100% 100%;
        background-repeat:no-repeat;
        padding-top:80px;
    }
</style>
</body>
</html>