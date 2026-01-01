<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="model.Listing" %>

<!DOCTYPE html>
<html>
<head>
  <title>Select Offer Listing</title>
</head>
<body>

<%@ include file="_menu.jsp" %>

<h2>Select one of your EXCHANGE listings to offer</h2>

<%
  Integer targetId = (Integer) request.getAttribute("targetId");
  List<Listing> myList = (List<Listing>) request.getAttribute("myExchangeListings");
%>

<% if (targetId == null) { %>
  <p>Error: Missing target listing.</p>

<% } else if (myList == null || myList.isEmpty()) { %>
  <p>You have no available EXCHANGE listings. Create one first.</p>
  <p><a href="<%= request.getContextPath() %>/secure/add-listing">Add Listing</a></p>

<% } else { %>

  <%
    boolean hasOptions = false;
    for (Listing l : myList) {
      if (l.getId() != targetId) { // ✅ مهم: لا نسمح بعرض نفس الإعلان
        hasOptions = true;
        break;
      }
    }
  %>

  <% if (!hasOptions) { %>
    <p>You cannot offer the same listing. Create another EXCHANGE listing first.</p>
    <p><a href="<%= request.getContextPath() %>/secure/add-listing.jsp">Add Listing</a></p>

  <% } else { %>
    <form method="post" action="<%= request.getContextPath() %>/secure/exchange/propose">
      <input type="hidden" name="targetId" value="<%= targetId %>" />

      <label>Choose your listing:</label><br/>
      <select name="offeredId" required>
        <% for (Listing l : myList) {
             if (l.getId() == targetId) continue;      
        %>
          <option value="<%= l.getId() %>">
            #<%= l.getId() %> - <%= l.getTitle() %> (<%= l.getCondition() %>)
          </option>
        <% } %>
      </select>

      <br/><br/>
      <button type="submit">Send Proposal</button>
    </form>
  <% } %>

<% } %>

</body>
</html>
