<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8" />
<title>书籍登录</title>
</head>
<body>

	<form action="/login" method="post">
		<p>用户帐号:<input type="text" name="username" /></p>
		<p>用户密码:<input type="password" name="password" /></p>
		<p>图书条码:<input type="text" name="isbn" /></p>
		<p>图书名称:<input type="text" name="bookname" /></p>
		<p>扫描时间:<input type="text" name="time" /></p>
    <input type="submit" value="Login" method="post" name="Submit"><br />
	</form>
</body>
</html>