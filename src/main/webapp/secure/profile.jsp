<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="model.User" %>

<!DOCTYPE html>
<html>
<head>
  <title>My Profile</title>
</head>
<body>

<%@ include file="_menu.jsp" %>

<h2>My Profile</h2>

<%
  User u = (User) session.getAttribute("user");
  if (u == null) {
%>
  <p>Please login.</p>
<%
  } else {
%>

  <% String msg = (String) request.getAttribute("msg"); %>
  <% if (msg != null) { %>
    <p style="color:green;"><%= msg %></p>
  <% } %>

  <form method="post" action="<%= request.getContextPath() %>/secure/profile">
   <label>Name:</label><br>
<input type="text" name="name" required value="<%= (u.getName()==null?"":u.getName()) %>"><br><br>

<label>Study Year:</label><br>
<input type="number" name="studyYear" min="1" max="6" required value="<%= u.getStudyYear() %>"><br><br>

<label>Email:</label><br>
<input type="text" value="<%= u.getEmail() %>" readonly><br><br>

    <button type="submit">Save</button>
  </form>

<% } %>

</div>
</body>
</html>
