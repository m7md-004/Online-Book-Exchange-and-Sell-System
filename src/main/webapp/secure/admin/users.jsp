<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="dao.AdminDAO.UserRow" %>

<!DOCTYPE html>
<html>
<head>
  <title>Admin - Users</title>
</head>
<body>

<%@ include file="../_menu.jsp" %>

<h2>Admin - Users</h2>

<%
  List<UserRow> users = (List<UserRow>) request.getAttribute("users");
  if (users == null || users.isEmpty()) {
%>
  <p>No users.</p>
<%
  } else {
%>
  <table border="1" cellpadding="8">
    <tr>
      <th>ID</th>
      <th>Name</th>
      <th>Email</th>
      <th>Year</th>
      <th>Role</th>
      <th>Blocked</th>
      <th>Action</th>
    </tr>

    <% for (UserRow r : users) { %>
      <tr>
        <td><%= r.id %></td>
        <td><%= r.name %></td>
        <td><%= r.email %></td>
        <td><%= r.studyYear %></td>
        <td><%= r.role %></td>
        <td><%= r.isBlocked ? "YES" : "NO" %></td>
        <td>
          <% if (!"ADMIN".equals(r.role)) { %>
            <% if (!r.isBlocked) { %>
              <form method="post" action="<%= request.getContextPath() %>/secure/admin/toggle-block" style="display:inline;">
                <input type="hidden" name="id" value="<%= r.id %>">
                <input type="hidden" name="action" value="block">
                <button type="submit">Block</button>
              </form>
            <% } else { %>
              <form method="post" action="<%= request.getContextPath() %>/secure/admin/toggle-block" style="display:inline;">
                <input type="hidden" name="id" value="<%= r.id %>">
                <input type="hidden" name="action" value="unblock">
                <button type="submit">Unblock</button>
              </form>
            <% } %>
          <% } else { %>
            -
          <% } %>
        </td>
      </tr>
    <% } %>
  </table>
<% } %>

</div>
</body>
</html>
