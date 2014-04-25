<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8" />
<title>每日心情</title>
</head>
<body>

	<form action="/everydaymood" method="post">
		<p>用户帐号:<input type="text" name="username" /></p>
		<p>你的心情:<input type="text" name="mood" /></p>
		<p>想说的话:<input type="text" name="words" /></p>
		<p>发布时间:<input type="text" name="pubtime" /></p>
		<p>发布日期:<input type="text" name="date" /></p>
    <input type="submit" value="Login" method="post" name="Submit"><br />
	</form>
</body>
</html>