<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8" />
<title>上传用户信息</title>
</head>
<body>

	<form action="/user/editprofile" method="post">
		<p>用户帐号:<input type="text" name="username" /></p>
		<p>头像:<input type="text" name="avatar" /></p>
		<p>昵称:<input type="text" name="nickname" /></p>
		<p>联系电话:<input type="text" name="phone" /></p>
		<p>住址:<input type="text" name="address" /></p>
		<p>学院与专业:<input type="text" name="major" /></p>
		<p>性别:<input type="text" name="sex" /></p>
		<p>年龄:<input type="text" name="age" /></p>
    <input type="submit" value="登录" method="post" name="Submit"><br />
	</form>
</body>
</html>