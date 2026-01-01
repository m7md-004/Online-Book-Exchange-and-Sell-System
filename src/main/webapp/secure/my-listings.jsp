<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="model.Listing" %>

<!DOCTYPE html>
<html>
<head>
  <title>My Listings</title>
</head>
<body>

<%@ include file="_menu.jsp" %>

<h2>My Listings</h2>

<%
  List<Listing> my = (List<Listing>) request.getAttribute("myListings");
  if (my == null || my.isEmpty()) {
%>
  <p>No listings yet.</p>
<%
  } else {
%>
  <table border="1" cellpadding="8">
    <tr>
      <th>ID</th>
      <th>Type</th>
      <th>Title</th>
      <th>Condition</th>
      <th>Price</th>
      <th>Status</th>
      <th>Actions</th>
      <th>Image</th>
    </tr>

    <% for (Listing l : my) { %>
      <tr>
        <td><%= l.getId() %></td>
        <td><%= l.getType() %></td>
        <td><%= l.getTitle() %></td>
        <td><%= l.getCondition() %></td>
        <td><%= (l.getPrice() == null ? "-" : l.getPrice()) %></td>
        <td><%= l.getStatus() %></td>

        <td>
          <% if ("SELL".equals(String.valueOf(l.getType())) && "RESERVED".equals(l.getStatus().name())) { %>

            <form method="post" action="<%= request.getContextPath() %>/secure/sell/complete" style="display:inline;">
              <input type="hidden" name="id" value="<%= l.getId() %>">
              <button type="submit">Complete (Sold)</button>
            </form>

            <form method="post" action="<%= request.getContextPath() %>/secure/sell/cancel" style="display:inline;">
              <input type="hidden" name="id" value="<%= l.getId() %>">
              <button type="submit">Cancel Reservation</button>
            </form>

          <% } else if ("AVAILABLE".equals(l.getStatus().name())) { %>

            <a href="<%= request.getContextPath() %>/secure/edit-listing?id=<%= l.getId() %>">Edit</a>
            &nbsp;|&nbsp;

            <form method="post"
                  action="<%= request.getContextPath() %>/secure/delete-listing"
                  style="display:inline;"
                  onsubmit="return confirm('Are you sure you want to delete this listing?');">
              <input type="hidden" name="id" value="<%= l.getId() %>">
              <button type="submit">Delete</button>
            </form>

          <% } else { %>
            -
          <% } %>
        </td>

        <td>
          <% if (l.getImagePath() != null && !l.getImagePath().isBlank()) { %>
            <img src="<%= request.getContextPath() %>/<%= l.getImagePath() %>" width="80">
          <% } else { %>
            -
          <% } %>
        </td>

      </tr>
    <% } %>
  </table>
<%
  }
%>

</div>
</body>
</html>
