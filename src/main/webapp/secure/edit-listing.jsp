<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="dao.AdminDAO" %>
<%@ page import="model.Listing" %>

<!DOCTYPE html>
<html>
<head>
  <title>Edit Listing</title>
</head>
<body>

<%@ include file="_menu.jsp" %>

<h2>Edit Listing</h2>

<%
  Listing listing = (Listing) request.getAttribute("listing");
  if (listing == null) {
%>
    <p>Listing not found.</p>
    <a href="<%= request.getContextPath() %>/secure/my-listings">Back</a>

<%
  } else {

    List<AdminDAO.CategoryRow> cats =
        (List<AdminDAO.CategoryRow>) request.getAttribute("categories");
    List<AdminDAO.CourseCodeRow> codes =
        (List<AdminDAO.CourseCodeRow>) request.getAttribute("courseCodes");
%>

<form method="post" action="<%= request.getContextPath() %>/secure/edit-listing">
  <input type="hidden" name="id" value="<%= listing.getId() %>">

  <label>Type:</label><br>
  <input type="text" value="<%= listing.getType() %>" disabled>
  <br><br>

  <label>Title:</label><br>
  <input type="text" name="title" required value="<%= listing.getTitle() %>">
  <br><br>

  <label>Author:</label><br>
  <input type="text" name="author" required value="<%= listing.getAuthor() %>">
  <br><br>

  <label>Condition:</label><br>
  <select name="condition" required>
    <option value="NEW" <%= "NEW".equals(listing.getCondition().name()) ? "selected" : "" %>>New</option>
    <option value="LIKE_NEW" <%= "LIKE_NEW".equals(listing.getCondition().name()) ? "selected" : "" %>>Like New</option>
    <option value="USED" <%= "USED".equals(listing.getCondition().name()) ? "selected" : "" %>>Used</option>
    <option value="DAMAGED" <%= "DAMAGED".equals(listing.getCondition().name()) ? "selected" : "" %>>Damaged</option>
  </select>
  <br><br>

  <div id="priceBlock">
    <label>Price (only for SELL):</label><br>
    <input type="number" name="price" id="price" step="0.01"
           value="<%= (listing.getPrice() == null ? "" : listing.getPrice()) %>">
    <br><br>
  </div>

  <label>Edition:</label><br>
  <input type="text" name="edition" required value="<%= listing.getEdition() %>">
  <br><br>

  <label>Category:</label><br>
  <select name="categoryId" required>
    <option value="">-- Select Category --</option>
    <%
      if (cats != null) {
        for (AdminDAO.CategoryRow c : cats) {
          boolean sel = (c.id == listing.getCategoryId());
    %>
      <option value="<%= c.id %>" <%= sel ? "selected" : "" %>><%= c.name %></option>
    <%
        }
      }
    %>
  </select>
  <br><br>

  <label>Course Code:</label><br>
  <select name="courseCodeId" required>
    <option value="">-- Select Course Code --</option>
    <%
      if (codes != null) {
        for (AdminDAO.CourseCodeRow cc : codes) {
          boolean sel = (cc.id == listing.getCourseCodeId());
    %>
      <option value="<%= cc.id %>" <%= sel ? "selected" : "" %>>
        <%= cc.department %> - <%= cc.code %>
      </option>
    <%
        }
      }
    %>
  </select>
  <br><br>

  <button type="submit">Save Changes</button>
  &nbsp;
  <a href="<%= request.getContextPath() %>/secure/my-listings">Cancel</a>
</form>

<script>
  (function() {
    const type = "<%= listing.getType().name() %>";
    const priceBlock = document.getElementById("priceBlock");
    const price = document.getElementById("price");

    if (type === "SELL") {
      priceBlock.style.display = "block";
      price.required = true;
    } else {
      priceBlock.style.display = "none";
      price.required = false;
      price.value = "";
    }
  })();
</script>

<%
  } 
%>

</body>
</html>
