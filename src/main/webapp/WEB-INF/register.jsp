<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Регистрация</title>
</head>
<body>
<h1>Регистрация</h1>

<%
    String error = (String) request.getAttribute("error");
    String login = (String) request.getAttribute("login");
    String email = (String) request.getAttribute("email");
    if (login == null) login = "";
    if (email == null) email = "";
    if (error != null && !error.isBlank()) {
%>
<p style="color: red;"><%= error %></p>
<%
    }
%>

<form method="post" action="<%= request.getContextPath() %>/register">
    <p>
        <label>Логин:
            <input type="text" name="login" value="<%= login %>" required>
        </label>
    </p>
    <p>
        <label>Email:
            <input type="email" name="email" value="<%= email %>" required>
        </label>
    </p>
    <p>
        <label>Пароль:
            <input type="password" name="password" required>
        </label>
    </p>
    <button type="submit">Зарегистрироваться</button>
</form>

<p><a href="<%= request.getContextPath() %>/login">Уже есть аккаунт</a></p>
</body>
</html>
