<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="dao.AdminDAO.CourseCodeRow" %>

<!DOCTYPE html>
<html>
<head>
  <title>Admin - Course Codes</title>
</head>
<body>

<%@ include file="../_menu.jsp" %>

<h2>Admin - Course Codes</h2>

<%
  String flashError = (String) session.getAttribute("flash_error");
  String flashSuccess = (String) session.getAttribute("flash_success");

  if (flashError != null) {
%>
    <div style="padding:10px; border:1px solid #d33; background:#ffecec; color:#a00; margin:12px 0;">
      <%= flashError %>
    </div>
<%
    session.removeAttribute("flash_error");
  }

  if (flashSuccess != null) {
%>
    <div style="padding:10px; border:1px solid #2c7; background:#eaffea; color:#060; margin:12px 0;">
      <%= flashSuccess %>
    </div>
<%
    session.removeAttribute("flash_success");
  }
%>


<h3>Add Course Code</h3>
<form method="post" action="<%= request.getContextPath() %>/secure/admin/course-codes">
  <input type="hidden" name="action" value="add">
  <input type="text" name="code" placeholder="CODE (e.g. CS101)" required>
  <input type="text" name="department" placeholder="Department (e.g. CS)" required>
  <button type="submit">Add</button>
</form>

<hr/>

<h3>All Course Codes</h3>

<%
  List<CourseCodeRow> courseCodes = (List<CourseCodeRow>) request.getAttribute("courseCodes");
  if (courseCodes == null || courseCodes.isEmpty()) {
%>
  <p>No course codes.</p>
<%
  } else {
%>
  <table border="1" cellpadding="8">
    <tr>
      <th>ID</th>
      <th>Code</th>
      <th>Department</th>
      <th>Action</th>
    </tr>

    <% for (CourseCodeRow c : courseCodes) { %>
      <tr>
        <td><%= c.id %></td>
        <td><%= c.code %></td>
        <td><%= c.department %></td>
        <td>
          <form method="post" action="<%= request.getContextPath() %>/secure/admin/course-codes" style="display:inline;">
            <input type="hidden" name="action" value="delete">
            <input type="hidden" name="id" value="<%= c.id %>">
            <button type="submit" onclick="return confirm('Delete course code?');">Delete</button>
          </form>
        </td>
      </tr>
    <% } %>
  </table>
<% } %>

</div>
</body>
</html>
