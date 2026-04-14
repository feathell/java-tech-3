<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.io.File" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.Date" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>File Explorer</title>
</head>
<body>
<div style="text-align: right;">
    <form method="post" action="<%= request.getContextPath() %>/logout" style="margin: 0;">
        <button type="submit">Выход</button>
    </form>
</div>
<p><%= request.getAttribute("generatedAt") %></p>
<h1>Пользователь: <%= request.getAttribute("userLogin") %> | Путь: <%= request.getAttribute("currentPath") %></h1>
<hr/>

<%
    String parentPath = (String) request.getAttribute("parentPath");
    if (parentPath != null) {
%>
<p>
    <a href="<%= request.getContextPath() %>/files?path=<%= URLEncoder.encode(parentPath, "UTF-8") %>">Вверх</a>
</p>
<%
    }
%>

<table border="1" cellpadding="6" cellspacing="0">
    <tr>
        <th>Файл</th>
        <th>Размер</th>
        <th>Дата</th>
    </tr>
    <%
        File[] items = (File[]) request.getAttribute("items");
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        for (File f : items) {
            String encoded = URLEncoder.encode(f.getAbsolutePath(), "UTF-8");
            String sizeText;
            if (f.isDirectory()) {
                sizeText = "-";
            } else {
                sizeText = f.length() + " B";
            }
    %>
    <tr>
        <td>
            <% if (f.isDirectory()) { %>
                <a href="<%= request.getContextPath() %>/files?path=<%= encoded %>"><%= f.getName() %>/</a>
            <% } else { %>
                <a href="<%= request.getContextPath() %>/download?path=<%= encoded %>"><%= f.getName() %></a>
            <% } %>
        </td>
        <td><%= sizeText %></td>
        <td><%= sdf.format(new Date(f.lastModified())) %></td>
    </tr>
    <% } %>
</table>
</body>
</html>
