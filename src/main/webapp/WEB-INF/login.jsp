<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Вход</title>
</head>
<body>
<h1>Вход</h1>

<%
    String error = (String) request.getAttribute("error");
    String login = (String) request.getAttribute("login");
    if (login == null) login = "";
    if (error != null && !error.isBlank()) {
%>
<p style="color: red;"><%= error %></p>
<%
    }
%>

<form method="post" action="<%= request.getContextPath() %>/login">
    <p>
        <label>Логин:
            <input type="text" name="login" value="<%= login %>" required>
        </label>
    </p>
    <p>
        <label>Пароль:
            <input type="password" name="password" required>
        </label>
    </p>
    <button type="submit">Войти</button>
</form>

<p><a href="<%= request.getContextPath() %>/register">Регистрация</a></p>
</body>
</html>
