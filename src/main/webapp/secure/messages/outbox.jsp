<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="model.Message" %>

<!DOCTYPE html>
<html>
<head><title>Outbox</title></head>
<body>
<jsp:include page="/secure/_menu.jsp" />

<h2>Outbox</h2>

<%
  List<Message> msgs = (List<Message>) request.getAttribute("messages");
  if (msgs == null || msgs.isEmpty()) {
%>
  <p>No sent messages.</p>
<% } else { %>
  <table border="1" cellpadding="8">
    <tr>
      <th>Date</th><th>To</th><th>Listing</th><th>Subject</th><th>Body</th>
    </tr>
    <% for (Message m : msgs) { %>
      <tr>
        <td><%= m.getCreatedAt() %></td>
        <td><%= m.getReceiverId() %></td>
        <td><%= (m.getListingId() == null ? "-" : m.getListingId()) %></td>
        <td><%= m.getSubject() %></td>
        <td><%= m.getBody() %></td>
      </tr>
    <% } %>
  </table>
<% } %>

</body>
</html>
