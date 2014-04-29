<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8" />
<title>留言</title>
</head>
<body>

	<form action="/user/message" method="post">
		<p>对象用户名:<input type="text" name="tusername" /></p>
		<p>留言者用户名:<input type="text" name="username" /></p>
		<p>留言时间:<input type="text" name="time" /></p>
		<p>留言:<input type="text" name="words" /></p>
    <input type="submit" value="查询" method="post" name="Submit"><br />
	</form>
</body>
</html>