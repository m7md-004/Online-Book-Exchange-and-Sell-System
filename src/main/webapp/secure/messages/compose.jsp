<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head><title>Send Message</title></head>
<body>
<jsp:include page="/secure/_menu.jsp" />

<h2>Send Message</h2>

<form method="post" action="<%= request.getContextPath() %>/secure/message/send">
  <input type="hidden" name="listingId" value="<%= request.getAttribute("listingId") %>">

  <label>Subject:</label><br>
  <input type="text" name="subject" required><br><br>

  <label>Message:</label><br>
  <textarea name="body" rows="6" cols="50" required></textarea><br><br>

  <button type="submit">Send</button>
</form>

</body>
</html>
