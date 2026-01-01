<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="model.ExchangeProposalRow" %>

<!DOCTYPE html>
<html>
<head>
  <title>My Sent Proposals</title>
</head>
<body>

<%@ include file="../_menu.jsp" %>

<h2>My Sent Exchange Proposals</h2>

<%
  List<ExchangeProposalRow> sent = (List<ExchangeProposalRow>) request.getAttribute("sent");
  if (sent == null || sent.isEmpty()) {
%>
  <p>No sent proposals.</p>
<%
  } else {
%>
  <table border="1" cellpadding="8">
    <tr>
      <th>ID</th>
      <th>Target Listing</th>
      <th>Your Offered Listing</th>
      <th>Status</th>
      <th>Confirmed</th>
      <th>Action</th>
    </tr>

    <% for (ExchangeProposalRow p : sent) { %>
      <tr>
        <td><%= p.getId() %></td>
        <td>#<%= p.getTargetListingId() %> - <%= p.getTargetTitle() %></td>
        <td>#<%= p.getOfferedListingId() %> - <%= p.getOfferedTitle() %></td>
        <td><%= p.getStatus() %></td>

        <td>
          Target: <%= p.isTargetConfirmed() ? "✅" : "❌" %> |
          You: <%= p.isProposerConfirmed() ? "✅" : "❌" %>
        </td>

        <td>
          <% if ("PENDING".equals(p.getStatus())) { %>
            <form method="post" action="<%= request.getContextPath() %>/secure/exchange/cancel" style="display:inline;">
              <input type="hidden" name="proposalId" value="<%= p.getId() %>">
              <button type="submit">Cancel</button>
            </form>

          <% } else if ("ACCEPTED".equals(p.getStatus())) { %>

            <% if (!p.isProposerConfirmed()) { %>
              <form method="post" action="<%= request.getContextPath() %>/secure/exchange/confirm-proposer" style="display:inline;">
                <input type="hidden" name="proposalId" value="<%= p.getId() %>">
                <button type="submit">Confirm Completion</button>
              </form>
            <% } else { %>
              ✅ Completed
            <% } %>

            <form method="post" action="<%= request.getContextPath() %>/secure/exchange/cancel" style="display:inline;">
              <input type="hidden" name="proposalId" value="<%= p.getId() %>">
              <button type="submit">Cancel</button>
            </form>

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
