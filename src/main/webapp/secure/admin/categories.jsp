<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="dao.AdminDAO.CategoryRow" %>

<!DOCTYPE html>
<html>
<head>
  <title>Admin - Categories</title>
</head>
<body>

<%@ include file="../_menu.jsp" %>

<h2>Admin - Categories</h2>

<h3>Add Category</h3>
<form method="post" action="<%= request.getContextPath() %>/secure/admin/categories">
  <input type="hidden" name="action" value="add">
  <input type="text" name="name" placeholder="Category name" required>
  <button type="submit">Add</button>
</form>

<hr/>

<h3>All Categories</h3>

<%
  List<CategoryRow> categories = (List<CategoryRow>) request.getAttribute("categories");
  if (categories == null || categories.isEmpty()) {
%>
  <p>No categories.</p>
<%
  } else {
%>
  <table border="1" cellpadding="8">
    <tr>
      <th>ID</th>
      <th>Name</th>
      <th>Action</th>
    </tr>

    <% for (CategoryRow c : categories) { %>
      <tr>
        <td><%= c.id %></td>
        <td><%= c.name %></td>
        <td>
          <form method="post" action="<%= request.getContextPath() %>/secure/admin/categories" style="display:inline;">
            <input type="hidden" name="action" value="delete">
            <input type="hidden" name="id" value="<%= c.id %>">
            <button type="submit" onclick="return confirm('Delete category?');">Delete</button>
          </form>
        </td>
      </tr>
    <% } %>
  </table>
<% } %>

</div>
</body>
</html>
