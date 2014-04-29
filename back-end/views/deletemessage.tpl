<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8" />
<title>删除留言</title>
</head>
<body>

	<form action="/deletemessage" method="post">
		<p>用户帐号:<input type="text" name="username" /></p>
		<p>用户密码:<input type="password" name="password" /></p>
		<p>删除的帐号:<input type="text" name="tusername" /></p>
		<p>发布时间:<input type="text" name="time" /></p>
    <input type="submit" value="操作" method="post" name="Submit"><br />
	</form>
</body>
</html>