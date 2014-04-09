Type=Activity
Version=3
@EndOfDesignText@
#Region  Activity Attributes 
	#FullScreen: False
	#IncludeTitle: False
#End Region

Sub Process_Globals
	'These global variables will be declared once when the application starts.
	'These variables can be accessed from all modules.
    Dim hc As HttpClient
End Sub

Sub Globals
	'These global variables will be redeclared each time the activity is created.
	'These variables can only be accessed from this module.
    

	Dim Button1 As Button
	Dim Button2 As Button
	Dim EditText1 As EditText
	Dim EditText2 As EditText
	Dim EditText3 As EditText
	Dim EditText4 As EditText
	Dim EditText5 As EditText
	Dim EditText6 As EditText
	Dim ImageView1 As ImageView
	Dim ImageView2 As ImageView
	Dim Label1 As Label
	Dim Label2 As Label
	Dim Label3 As Label
	Dim Label4 As Label
	Dim Label5 As Label
	Dim Label6 As Label
	Dim Label7 As Label
	Dim username As String
End Sub

Sub Activity_Create(FirstTime As Boolean)
	'Do not forget to load the layout file created with the visual designer. For example:
    If FirstTime Then
        hc.Initialize("hc")
    End If
	Activity.LoadLayout("profile")
	Activity.AddMenuItem("上传到服务器","upload")
	Activity.AddMenuItem("上传头像","uploadavatar")
	Dim Reader As TextReader
	Reader.Initialize(File.OpenInput(File.DirInternal, "user"))
	username = Reader.ReadLine
	Reader.Close
    If File.Exists(File.DirInternal,username&"-profile") Then
	    readprofile
	End If	
End Sub

Sub Activity_Resume
    If File.Exists(File.DirInternal,"avatar.jpg") Then
	    ImageView2.Bitmap= LoadBitmap(File.DirInternal,"avatar.jpg")
	End If	
End Sub

Sub Activity_Pause (UserClosed As Boolean)

End Sub

Sub readprofile
    Dim Reader As TextReader
	Dim nickname,PhoneNumber,location,major,gender,age As String
    Reader.Initialize(File.OpenInput(File.DirInternal, username&"-profile"))
	nickname = Reader.ReadLine
    PhoneNumber = Reader.ReadLine
	location = Reader.ReadLine
	major = Reader.ReadLine
    gender = Reader.ReadLine
	age = Reader.ReadLine
    Reader.Close 
	EditText1.Text=nickname
	EditText2.Text=PhoneNumber
	EditText3.Text=location
	EditText4.Text=major
	EditText5.Text=gender
	EditText6.Text=age
	If File.Exists(File.DirInternal,"avatar.jpg") Then
	    ImageView2.Bitmap= LoadBitmap(File.DirInternal,"avatar.jpg")
	End If	
End Sub

Sub writeprofile
	Dim Writer As TextWriter
    Writer.Initialize(File.OpenOutput(File.DirInternal, username&"-profile", False))
    Writer.WriteLine(EditText1.Text)
    Writer.WriteLine(EditText2.Text)
	Writer.WriteLine(EditText3.Text)
    Writer.WriteLine(EditText4.Text)
	Writer.WriteLine(EditText5.Text)
    Writer.WriteLine(EditText6.Text)
    Writer.Close 
End Sub
Sub ImageView2_Click
	StartActivity(avatarcrop)
End Sub
Sub Button2_Click
	Activity.Finish
End Sub
Sub Button1_Click
    writeprofile
	ToastMessageShow("保存成功",False)
	Activity.Finish
End Sub

Sub upload_click
	Dim b As Base64Image
	b.Initialize
	Dim code As String
	code=b.EncodeFromImage(File.DirInternal,"avatar.jpg")
	Dim Writer As TextWriter
    Writer.Initialize(File.OpenOutput(File.DirInternal, "base64", False))
    Writer.Write(code)
    Writer.Close 
	'Log(code)
	Dim URL As String
	URL="username="&username&"&nickname="&EditText1.Text&"&phone="&EditText2.Text&"&address="&EditText3.Text&"&major="&EditText4.Text&"&sex="&EditText5.Text&"&age="&EditText6.Text&"&avatar="&code
	'Log(URL)
	ProgressDialogShow("注册中")
	Dim job1 As HttpJob
	job1.Initialize("Job1",Me)
    job1.PostString("https://bottle-bookjnrain.rhcloud.com/user/editprofile",URL)
End Sub


Sub JobDone (job As HttpJob)
	Log("JobName = " & job.JobName & ", Success = " & job.Success)
	If job.Success = True Then
		Select job.JobName
			Case "Job1"
				'print the result to the logs
				ProgressDialogHide
                ToastMessageShow(job.GetString,False)
			Case "Job2"
			    ProgressDialogHide
				ToastMessageShow("上传成功！",False)
		End Select
	Else
	    ProgressDialogHide
		Log("Error: " & job.ErrorMessage)
		ToastMessageShow("Error: " & job.ErrorMessage, True)
	End If
	job.Release
End Sub

Sub uploadavatar_click
    ToastMessageShow("有错误提示点Yes，实际已上传成功。",False)
    If File.Exists(File.DirInternal,"avatar.jpg") Then
	    File.Copy(File.DirInternal,"avatar.jpg",File.DirInternal,username&".jpg")
	End If
	ProgressDialogShow("上传中……")
    'Dim Job2 As HttpJob
	'Job2.Initialize("Job2",Me)
	'Job2.PostFile("https://bottle-bookjnrain.rhcloud.com/user/avatarupload",File.DirInternal,username&".jpg")
    
	'Add files
    Dim files As List
    files.Initialize
    Dim FD As FileData
    FD.Initialize
    FD.Dir = File.DirInternal
    FD.FileName = username&".jpg"
    FD.KeyName = "upload"
    FD.ContentType = "application/octet-stream"
    files.Add(FD)
    'Add second file
    'Dim fd As FileData
    'FD.Initialize
    'FD.Dir = File.DirAssets
    'FD.FileName = "1.png"
    'FD.KeyName = "upfile"
    'FD.ContentType = "application/octet-stream"
    'files.Add(FD)
    'Add name / values pairs (parameters)
    'Dim NV As Map
    'NV.Initialize
    'NV.Put("note1", "abc")
    'NV.Put("note2", "def")
    Dim req As HttpRequest
    req = MultipartPost.CreatePostRequest("https://bottle-bookjnrain.rhcloud.com/user/avatarupload", Null, files)
    hc.Execute(req, 1)
End Sub

Sub hc_ResponseError (Response As HttpResponse, Reason As String, StatusCode As Int, TaskId As Int)
    Log("error: " & Response & " " & StatusCode)
    If Response <> Null Then
        Log(Response.GetString("UTF8"))
		ProgressDialogHide
        Response.Release
    End If
End Sub
Sub hc_ResponseSuccess (Response As HttpResponse, TaskId As Int)
    ProgressDialogHide
    'Msgbox(Response.GetString("UTF8"), "")
	ToastMessageShow("上传成功！",False)
    Response.Release
End Sub

