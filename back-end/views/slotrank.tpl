<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8" />
<title>上传分数</title>
</head>
<body>

	<form action="/user/slotrank" method="post">
    <p>用户名:<input type="text" name="username" /></p>
		<p>上传时间:<input type="text" name="time" /></p>
		<p>分数:<input type="text" name="score" /></p>
    <input type="submit" value="查询" method="post" name="Submit"><br />
	</form>
</body>
</html>