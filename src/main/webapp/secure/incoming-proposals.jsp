<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="model.ExchangeProposalRow" %>

<!DOCTYPE html>
<html>
<head>
  <title>Incoming Exchange Proposals</title>
</head>
<body>

<%@ include file="_menu.jsp" %>

<h2>Incoming Exchange Proposals</h2>

<%
  List<ExchangeProposalRow> incoming = (List<ExchangeProposalRow>) request.getAttribute("incoming");
  if (incoming == null || incoming.isEmpty()) {
%>
  <p>No incoming proposals.</p>
<%
  } else {
%>
  <table border="1" cellpadding="8">
    <tr>
      <th>ID</th>
      <th>Your Listing</th>
      <th>Offered Listing</th>
      <th>Status</th>
      <th>Confirmed</th>
      <th>Action</th>
    </tr>

    <% for (ExchangeProposalRow p : incoming) { %>
      <tr>
        <td><%= p.getId() %></td>
        <td>#<%= p.getTargetListingId() %> - <%= p.getTargetTitle() %></td>
        <td>#<%= p.getOfferedListingId() %> - <%= p.getOfferedTitle() %></td>

        <td><%= p.getStatus() %></td>

        <td>
          Target: <%= p.isTargetConfirmed() ? "✅" : "❌" %> |
          Proposer: <%= p.isProposerConfirmed() ? "✅" : "❌" %>
        </td>

        <td>
          <% if ("PENDING".equals(p.getStatus())) { %>

            <form method="post" action="<%= request.getContextPath() %>/secure/exchange/accept" style="display:inline;">
              <input type="hidden" name="proposalId" value="<%= p.getId() %>">
              <button type="submit">Accept</button>
            </form>

            <form method="post" action="<%= request.getContextPath() %>/secure/exchange/reject" style="display:inline;">
              <input type="hidden" name="proposalId" value="<%= p.getId() %>">
              <button type="submit">Reject</button>
            </form>

          <% } else if ("ACCEPTED".equals(p.getStatus())) { %>

            <% if (!p.isTargetConfirmed()) { %>
              <form method="post" action="<%= request.getContextPath() %>/secure/exchange/confirm-target" style="display:inline;">
                <input type="hidden" name="proposalId" value="<%= p.getId() %>">
                <button type="submit">Confirm (Target Owner)</button>
              </form>
            <% } %>

            <% if (p.isTargetConfirmed() && p.isProposerConfirmed()) { %>
              ✅ Exchange Completed
            <% } else if (p.isTargetConfirmed() && !p.isProposerConfirmed()) { %>
              ⏳ Waiting proposer confirm
            <% } else if (!p.isTargetConfirmed() && p.isProposerConfirmed()) { %>
              ⏳ Waiting your confirm
            <% } else { %>
              ⏳ Waiting confirmations
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
