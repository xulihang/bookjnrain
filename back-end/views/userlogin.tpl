<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8" />
<title>用户登录</title>
</head>
<body>

	<form action="/user/login" method="post">
		<p>用户帐号:<input type="text" name="username" /></p>
		<p>用户密码:<input type="password" name="password" /></p>
    <input type="submit" value="登录" method="post" name="Submit"><br />
	</form>
</body>
</html>