<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="dao.ListingDAO" %>

<!DOCTYPE html>
<html>
<head>
  <title>My Reservations</title>
</head>
<body>
<%@ include file="_menu.jsp" %>

<h2>My Reservations</h2>

<%
  List<ListingDAO.ReservationRow> rows =
      (List<ListingDAO.ReservationRow>) request.getAttribute("reservations");
  if (rows == null || rows.isEmpty()) {
%>
  <p>No reservations.</p>
<%
  } else {
%>
  <table border="1" cellpadding="8">
    <tr>
      <th>Reservation #</th>
      <th>Listing ID</th>
      <th>Title</th>
      <th>Author</th>
      <th>Condition</th>
      <th>Price</th>
      <th>Status</th>
      <th>Reserved At</th>
      <th>Image</th>
    </tr>

    <% for (ListingDAO.ReservationRow r : rows) { %>
      <tr>
        <td><%= r.reservationId %></td>
        <td><%= r.listingId %></td>
        <td><%= r.title %></td>
        <td><%= r.author %></td>
        <td><%= r.condition %></td>
        <td><%= (r.price == null ? "-" : r.price) %></td>
        <td><%= r.status %></td>
        <td><%= r.reservedAt %></td>
        <td>
          <% if (r.imagePath != null && !r.imagePath.isBlank()) { %>
            <img src="<%= request.getContextPath() %>/<%= r.imagePath %>" width="80">
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
