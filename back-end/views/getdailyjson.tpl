<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8" />
<title>获得json</title>
</head>
<body>

	<form action="/getdailyjson" method="post">
		<p>每页数目:<input type="text" name="itemnumber" /></p>
		<p>页数:<input type="text" name="page" /></p>
    <p>日期:<input type="text" name="date" /></p>
    <input type="submit" value="查询" method="post" name="Submit"><br />
	</form>
</body>
</html>