<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>手工登录</title>
</head>
<body>

	<form action="/handlogin" method="post">
		<a>用户帐号:</a><input type="text" name="username" />
		</br>
		<a>用户密码:</a><input type="password" name="password" />
		</br>
		<a>图书名称:</a><input type="text" name="title" />
		</br>
		<a>图书价格:</a><input type="text" name="price" />
		</br>
		<a>图书条码:</a><input type="text" name="isbn" />
		</br>
		<a>出版时间:</a><input type="text" name="pubdate" />
		</br>
		<a>出版社:</a><input type="text" name="publisher" />
		</br>
		<a>扫描时间:</a><input type="text" name="time" />
		</br>
    <INPUT TYPE="submit" VALUE="Login" METHOD="post" NAME="Submit"><br />
	</form>
</body>
</html>