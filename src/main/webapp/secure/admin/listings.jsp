<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="dao.AdminDAO.ListingRow" %>

<!DOCTYPE html>
<html>
<head>
  <title>Admin - Listings</title>
</head>
<body>

<%@ include file="../_menu.jsp" %>

<h2>Admin - Listings</h2>

<%
  List<ListingRow> listings = (List<ListingRow>) request.getAttribute("listings");
  if (listings == null || listings.isEmpty()) {
%>
  <p>No listings.</p>
<%
  } else {
%>
  <table border="1" cellpadding="8">
    <tr>
      <th>ID</th>
      <th>Owner</th>
      <th>Type</th>
      <th>Title</th>
      <th>Status</th>
      <th>Created</th>
      <th>Expires</th>
      <th>Action</th>
    </tr>

    <% for (ListingRow r : listings) { %>
      <tr>
        <td><%= r.id %></td>
        <td>#<%= r.ownerId %> (<%= r.ownerEmail %>)</td>
        <td><%= r.type %></td>
        <td><%= r.title %></td>
        <td><%= r.status %></td>
        <td><%= r.createdAt %></td>
        <td><%= r.expiresAt %></td>
        <td>
          <form method="post" action="<%= request.getContextPath() %>/secure/admin/delete-listing" style="display:inline;">
            <input type="hidden" name="id" value="<%= r.id %>">
            <button type="submit" onclick="return confirm('Delete listing #<%= r.id %>?');">Delete</button>
          </form>
        </td>
      </tr>
    <% } %>
  </table>
<% } %>

</div>
</body>
</html>
