<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8" />
<title>书籍登录</title>
</head>
<body>

	<form action="/addpraise" method="post">
		<p>图书条码:<input type="text" name="isbn" /></p>
		<p>用户帐号:<input type="text" name="username" /></p>
		<p>扫描时间:<input type="text" name="time" /></p>
		<p>赞次数:<input type="text" name="praise" /></p>
    <input type="submit" value="Login" method="post" name="Submit"><br />
	</form>
</body>
</html>