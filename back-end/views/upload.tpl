<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8" />
<title>头像上传</title>
</head>
<body>

<form action="/user/avatarupload" method="post" enctype="multipart/form-data">
  选头像: <input type="file" name="upload" />
  <input type="submit" value="Start upload" />
</form>

</body>
</html>