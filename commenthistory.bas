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
	Dim ImageView1 As ImageView
	Dim Label1 As Label
	Dim ListView1 As ListView
	Dim username As String
End Sub

Sub Activity_Create(FirstTime As Boolean)
	'Do not forget to load the layout file created with the visual designer. For example:
	If FirstTime Then
        hc.Initialize("hc")
    End If
	Activity.LoadLayout("uploaderlist")
	Activity.AddMenuItem("从本地同步到服务器","sync")
	Activity.AddMenuItem("从服务器同步到本地","download")
	Label1.Text="我的评论"
	Dim Reader As TextReader
	Reader.Initialize(File.OpenInput(File.DirInternal, "user"))
	username = Reader.ReadLine
	Reader.Close
	If File.Exists(File.DirInternal,username&"-comment.db")=False Then
	    ToastMessageShow("评论数为零。",False)
	Else
        creatlistview
	End If
End Sub

Sub Activity_Resume

End Sub

Sub Activity_Pause (UserClosed As Boolean)

End Sub


Sub creatlistview
	If comment.SQL1.IsInitialized = False Then
	    comment.SQL1.Initialize(File.DirInternal, username&"-comment.db", False)
	End If
	
	Dim Label2 As Label
	Label2=ListView1.TwoLinesLayout.Label
	Label2.TextColor=Colors.White
	Label2.TextSize=18
	Dim Label3 As Label
	Label3=ListView1.TwoLinesLayout.SecondLabel
	Label3.TextColor=Colors.Black
	Label3.TextSize=16
	
	Dim Cursor1 As Cursor
	Cursor1 = comment.SQL1.ExecQuery("SELECT * FROM book")
	For i = 0 To Cursor1.RowCount - 1
		Cursor1.Position = i
		Dim time As String
	    time = Cursor1.GetString("time")
		Dim content As String
		content=Cursor1.GetString("comment")
		Dim isbn As String
		isbn=Cursor1.GetString("isbn")
        ListView1.AddTwoLines2(Cursor1.GetString("title")&" 我说:"&content,isbn&" "&time,isbn)
	Next
	Cursor1.Close
	comment.SQL1.Close

End Sub

Sub ListView1_ItemClick (Position As Int, Value As Object)
    Main.book=Value
    StartActivity(comment)
End Sub
Sub Button1_Click
	Activity.Finish
End Sub
Sub ListView1_ItemLongClick (Position As Int, Value As Object)
    Dim CC As BClipboard
    CC.setText(Value)
	ToastMessageShow("ISBN已复制到剪切板。",False)
	If comment.SQL1.IsInitialized = False Then
	    comment.SQL1.Initialize(File.DirInternal, username&"-comment.db", False)
	End If
	Dim Cursor1 As Cursor
	Cursor1 = comment.SQL1.ExecQuery("SELECT * FROM book")
	Cursor1.Position = Position
	Msgbox(Cursor1.GetString("comment"),"我说")
	Cursor1.Close
	comment.SQL1.Close
End Sub

Sub Sync_click
	ProgressDialogShow("上传中……")
	'Add files
    Dim files As List
    files.Initialize
    Dim FD As FileData
    FD.Initialize
    FD.Dir = File.DirInternal
    FD.FileName = username&"-comment.db"
    FD.KeyName = "upload"
    FD.ContentType = "application/octet-stream"
    files.Add(FD)
    Dim req As HttpRequest
    req = MultipartPost.CreatePostRequest("https://bottle-bookjnrain.rhcloud.com/user/commentupload", Null, files)
    hc.Execute(req, 1)
End Sub


Sub hc_ResponseError (Response As HttpResponse, Reason As String, StatusCode As Int, TaskId As Int)
    ToastMessageShow("error: " & Response & " " & StatusCode,False)
    If Response <> Null Then
        Log(Response.GetString("UTF8"))
		ProgressDialogHide
        'Response.Release
    End If
End Sub
Sub hc_ResponseSuccess (Response As HttpResponse, TaskId As Int)
    ProgressDialogHide
    'Msgbox(Response.GetString("UTF8"), "")
	ToastMessageShow("上传成功！",False)
    'Response.Release
End Sub

Sub download_click
    ProgressDialogShow("同步中……")
    Dim URL As String
	Dim job4 As HttpJob
	URL="https://bottle-bookjnrain.rhcloud.com/getusercomment/"&username
    job4.Initialize("Job4",Me)
    job4.Download(URL)
End Sub

Sub JobDone (job As HttpJob)
	Log("JobName = " & job.JobName & ", Success = " & job.Success)
	If job.Success = True Then
		Select job.JobName
			Case "Job4"
			    ProgressDialogHide
				Dim out As OutputStream
				out=File.OpenOutput(File.DirInternal,username&"-comment.db",False)
                File.Copy2(job.GetInputStream,out)
				out.Close
				ListView1.Clear
				creatlistview
		End Select
	Else
	    ProgressDialogHide
		Log("Error: " & job.ErrorMessage)
		ToastMessageShow("Error: " & job.ErrorMessage, True)
	End If
	job.Release
End Sub