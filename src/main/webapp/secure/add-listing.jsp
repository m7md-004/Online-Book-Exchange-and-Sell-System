<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="dao.AdminDAO" %>

<!DOCTYPE html>
<html>
<head>
  <title>Add Listing</title>
</head>
<body>

<%@ include file="_menu.jsp" %>

<h2>Add Listing</h2>

<%
  List<AdminDAO.CategoryRow> cats =
      (List<AdminDAO.CategoryRow>) request.getAttribute("categories");
  List<AdminDAO.CourseCodeRow> codes =
      (List<AdminDAO.CourseCodeRow>) request.getAttribute("courseCodes");
%>

<form method="post"
      action="<%= request.getContextPath() %>/secure/add-listing"
      enctype="multipart/form-data">

  <label>Type:</label><br>
  <select name="type" id="type" required onchange="togglePrice()">
    <option value="SELL">Sell</option>
    <option value="EXCHANGE">Exchange</option>
  </select><br><br>

  <label>Title:</label><br>
  <input type="text" name="title" required><br><br>

  <label>Author:</label><br>
  <input type="text" name="author" required><br><br>

  <label>Condition:</label><br>
  <select name="condition" required>
    <option value="NEW">New</option>
    <option value="LIKE_NEW">Like New</option>
    <option value="USED">Used</option>
    <option value="DAMAGED">Damaged</option>
  </select><br><br>

  <div id="priceBlock">
    <label>Price (only for SELL):</label><br>
    <input type="number" name="price" id="price" step="0.01"><br><br>
  </div>

  <label>Edition:</label><br>
  <input type="text" name="edition" placeholder="e.g. 3rd" required><br><br>

  <label>Category:</label><br>
  <select name="categoryId" required>
    <option value="">-- Select Category --</option>
    <%
      if (cats != null) {
        for (AdminDAO.CategoryRow c : cats) {
    %>
          <option value="<%= c.id %>"><%= c.name %></option>
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
    %>
          <option value="<%= cc.id %>"><%= cc.department %> - <%= cc.code %></option>
    <%
        }
      }
    %>
  </select>
  <br><br>

  <label>Image:</label><br>
  <input type="file" name="image" accept=".jpg,.jpeg,.png" required><br>
  <small>Allowed: JPG/PNG, Max 5MB</small>
  <br><br>

  <button type="submit">Create Listing</button>

</form>

<script>
  function togglePrice() {
    const type = document.getElementById("type").value;
    const price = document.getElementById("price");
    const priceBlock = document.getElementById("priceBlock");

    if (type === "SELL") {
      priceBlock.style.display = "block";
      price.required = true;
    } else {
      priceBlock.style.display = "none";
      price.required = false;
      price.value = "";
    }
  }
  togglePrice();
</script>

</body>
</html>
