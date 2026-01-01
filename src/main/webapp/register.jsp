<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
  <title>Register</title>
</head>
<body>
  <h2>Register</h2>

  <form method="post" action="${pageContext.request.contextPath}/register">
    <label>Name:</label><br>
    <input type="text" name="name" required><br><br>

    <label>Email:</label><br>
    <input type="email" name="email" required><br><br>

    <label>Password:</label><br>
    <input type="password" name="password" required><br><br>

    <label>Study Year:</label><br>
    <input type="number" name="studyYear" min="1" max="6" required><br><br>

    <button type="submit">Create Account</button>
  </form>

  <p><a href="${pageContext.request.contextPath}/index.jsp">Back to Login</a></p>
</body>
</html>
