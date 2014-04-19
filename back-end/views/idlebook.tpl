<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8" />
<title>空闲书籍登录</title>
</head>
<body>

	<form action="/idlepublish" method="post">
		<p>用户帐号:<input type="text" name="username" /></p>
		<p>用户密码:<input type="password" name="password" /></p>
		<p>图书条码:<input type="text" name="isbn" /></p>
		<p>图书名称:<input type="text" name="bookname" /></p>
		<p>图书价格:<input type="text" name="price" /></p>
		<p>发布目的:<input type="text" name="purpose" /></p>
		<p>发布详情:<input type="text" name="detail" /></p>
		<p>发布时间:<input type="text" name="pubtime" /></p>
    <input type="submit" value="Login" method="post" name="Submit"><br />
	</form>
</body>
</html>