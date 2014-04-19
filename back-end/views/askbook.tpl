<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8" />
<title>求书</title>
</head>
<body>

	<form action="/askforbook" method="post">
		<p>用户帐号:<input type="text" name="username" /></p>
		<p>用户密码:<input type="password" name="password" /></p>
		<p>图书名称:<input type="text" name="bookname" /></p>
		<p>图书备注:<input type="text" name="detail" /></p>
    <input type="submit" value="Login" method="post" name="Submit"><br />
	</form>
</body>
</html>