<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="model.User" %>

<%
    User u = (User) session.getAttribute("user");
    if (u == null) {
        response.sendRedirect(request.getContextPath() + "/index.jsp");
        return;
    }
%>

<!DOCTYPE html>
<html>
<head>
  <title>Dashboard</title>
</head>
<body>

<%@ include file="_menu.jsp" %>

<h2>Welcome, <%= u.getName() %> ✅</h2>
<p>Email: <%= u.getEmail() %></p>
<p>Role: <%= u.getRole() %></p>

</body>
</html>
