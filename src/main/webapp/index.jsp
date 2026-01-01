<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
  <title>Login</title>
</head>
<body>

<h2>Login</h2>

<%
  String error = request.getParameter("error");
  if ("invalid".equals(error)) {
%>
  <p style="color:red;">Invalid email or password</p>
<%
  } else if ("blocked".equals(error)) {
%>
  <p style="color:red;">Your account is blocked</p>
<%
  } else if ("missing".equals(error)) {
%>
  <p style="color:red;">Please fill all fields</p>
<%
  }
%>

<form method="post" action="<%= request.getContextPath() %>/login">
  <label>Email:</label><br>
  <input type="email" name="email" required><br><br>

  <label>Password:</label><br>
  <input type="password" name="password" required><br><br>

  <button type="submit">Login</button>
</form>

<p>
  <a href="<%= request.getContextPath() %>/register.jsp">
    Create new account
  </a>
</p>

</body>
</html>
