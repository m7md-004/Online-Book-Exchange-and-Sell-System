<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="model.User" %>

<link rel="stylesheet" href="<%= request.getContextPath() %>/assets/style.css">

<%
    User menuUser = (User) session.getAttribute("user");
    String role = (menuUser == null) ? "" : String.valueOf(menuUser.getRole());

    String flash = (String) session.getAttribute("flash");
    String flashType = (String) session.getAttribute("flash_type"); 
    if (flashType == null || flashType.isBlank()) flashType = "success";

    String bg = "#e9ffe9";
    String border = "#99dd99";
    String text = "#155724";

    if ("error".equalsIgnoreCase(flashType)) {
        bg = "#ffe9e9";
        border = "#dd9999";
        text = "#721c24";
    } else if ("info".equalsIgnoreCase(flashType)) {
        bg = "#e9f2ff";
        border = "#99b7dd";
        text = "#0c5460";
    }
%>

<div class="topbar">
  <div class="container">

    <div class="nav">
      <div class="nav-left">
        <a class="brand" href="<%= request.getContextPath() %>/secure/dashboard.jsp">
          Online Book Exchange
        </a>

        <span class="sep">|</span>
        <a href="<%= request.getContextPath() %>/secure/dashboard.jsp">Dashboard</a>

        <span class="sep">|</span>
        <a href="<%= request.getContextPath() %>/secure/browse">Browse Listings</a>

        <span class="sep">|</span>
        <a href="<%= request.getContextPath() %>/secure/add-listing">Add Listing</a>

        <span class="sep">|</span>
        <a href="<%= request.getContextPath() %>/secure/my-listings">My Listings</a>

        <span class="sep">|</span>
        <a href="<%= request.getContextPath() %>/secure/exchange/incoming">Incoming Proposals</a>

        <span class="sep">|</span>
        <a href="<%= request.getContextPath() %>/secure/exchange/sent">My Sent Proposals</a>

        <span class="sep">|</span>
        <a href="<%= request.getContextPath() %>/secure/my-reservations">My Reservations</a>

        <span class="sep">|</span>
        <a href="<%= request.getContextPath() %>/secure/profile">Profile</a>

        <span class="sep">|</span>
        <a href="<%= request.getContextPath() %>/secure/messages/inbox">Inbox</a>

        <span class="sep">|</span>
        <a href="<%= request.getContextPath() %>/secure/messages/outbox">Outbox</a>
      </div>

      <div class="nav-right">
        <a class="logout" href="<%= request.getContextPath() %>/logout">Logout</a>
      </div>
    </div>

    <% if ("ADMIN".equals(role)) { %>
      <div class="admin-nav">
        <div class="nav-left">
          <strong>Admin</strong>

          <span class="sep">|</span>
          <a href="<%= request.getContextPath() %>/secure/admin/users">Users</a>

          <span class="sep">|</span>
          <a href="<%= request.getContextPath() %>/secure/admin/listings">Listings</a>

          <span class="sep">|</span>
          <a href="<%= request.getContextPath() %>/secure/admin/categories">Categories</a>

          <span class="sep">|</span>
          <a href="<%= request.getContextPath() %>/secure/admin/course-codes">Course Codes</a>
        </div>
      </div>
    <% } %>

    <% if (flash != null && !flash.isBlank()) { %>
      <p style="background:<%= bg %>;
                padding:10px;
                border:1px solid <%= border %>;
                color:<%= text %>;
                margin-top:12px;
                border-radius:6px;">
        <%= flash %>
      </p>
      <%
        session.removeAttribute("flash");
        session.removeAttribute("flash_type");
      %>
    <% } %>

  </div>
</div>

<div class="container">
