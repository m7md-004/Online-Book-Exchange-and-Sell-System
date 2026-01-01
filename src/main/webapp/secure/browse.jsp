<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="model.Listing" %>
<%@ page import="model.User" %>

<!DOCTYPE html>
<html>
<head>
  <title>Browse Listings</title>
</head>
<body>

<%@ include file="_menu.jsp" %>

<h2>Available Listings</h2>

<form method="get" action="<%= request.getContextPath() %>/secure/browse">
  <input type="text" name="q" placeholder="Search..." />

  <select name="filter">
    <option value="TITLE">Title</option>
    <option value="TYPE">Type</option>
    <option value="COND">Condition</option>
    <option value="COURSE">Course Code</option>
<option value="DEPT">Department</option>
    
  </select>

  <button type="submit">Search</button>
</form>

<br/>

<%
  User me = (User) session.getAttribute("user");

  List<Listing> listings = (List<Listing>) request.getAttribute("listings");
  if (listings == null || listings.isEmpty()) {
%>
  <p>No listings available right now.</p>
<%
  } else {
%>
  <table border="1" cellpadding="8">
    <tr>
      <th>ID</th>
      <th>Type</th>
      <th>Title</th>
      <th>Author</th>
      <th>Condition</th>
      <th>Price</th>
      <th>Action</th>
      <th>Image</th>
    </tr>

<% for (Listing l : listings) { %>
  <tr>
    <td><%= l.getId() %></td>
    <td><%= l.getType() %></td>
    <td><%= l.getTitle() %></td>
    <td><%= l.getAuthor() %></td>
    <td><%= l.getCondition() %></td>
    <td><%= (l.getPrice() == null ? "-" : l.getPrice()) %></td>

    <td>
      <% if ("SELL".equals(String.valueOf(l.getType()))) { %>
        <form method="post"
              action="<%= request.getContextPath() %>/secure/reserve-sell"
              style="display:inline;">
          <input type="hidden" name="id" value="<%= l.getId() %>">
          <button type="submit">Reserve</button>
        </form>

      <% } else { %>
        <a href="<%= request.getContextPath() %>/secure/exchange/select-offer?targetId=<%= l.getId() %>">
          Propose Exchange
        </a>
      <% } %>

      <% if (me != null && l.getOwnerId() != me.getId()) { %>
        &nbsp; | &nbsp;
        <a href="<%= request.getContextPath() %>/secure/messages/compose?listingId=<%= l.getId() %>">
          Message Owner
        </a>
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

<% } %>

</div> 
</body>
</html>
