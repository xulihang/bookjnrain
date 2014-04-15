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
    Dim SQL4 As SQL
End Sub

Sub Globals
	'These global variables will be redeclared each time the activity is created.
	'These variables can only be accessed from this module.

	Dim Button1 As Button
	Dim ImageView1 As ImageView
	Dim Label1 As Label
	Dim ListView1 As ListView
End Sub

Sub Activity_Create(FirstTime As Boolean)
	'Do not forget to load the layout file created with the visual designer. For example:
	Activity.LoadLayout("uploaderlist")
	Dim get As HttpJob
	get.Initialize("get",Me)
    get.Download("https://bottle-bookjnrain.rhcloud.com/get4/"&Main.book)
	ProgressDialogShow("获取数据中...")
    Log(Main.book)
End Sub

Sub Activity_Resume

End Sub

Sub Activity_Pause (UserClosed As Boolean)

End Sub

Sub JobDone (job As HttpJob)
	Log("JobName = " & job.JobName & ", Success = " & job.Success)
	If job.Success = True Then
		Select job.JobName
			Case "get"
				'print the result to the logs
				ProgressDialogHide
				Dim out As OutputStream
				out=File.OpenOutput(File.DirInternal,Main.book&"-who.db",False)
                File.Copy2(job.GetInputStream,out)
				out.Close
				creatlistview
		End Select
	Else
	    ProgressDialogHide
		Log("Error: " & job.ErrorMessage)
		ToastMessageShow("Error: " & job.ErrorMessage, True)
	End If
	job.Release
End Sub

Sub creatlistview
	If SQL4.IsInitialized = False Then
	    SQL4.Initialize(File.DirInternal, Main.book&"-who.db", False)
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
	Cursor1 = SQL4.ExecQuery("SELECT * FROM statics")
	For i = 0 To Cursor1.RowCount - 1
		Cursor1.Position = i
		Dim now As Long
		Dim time As String
	    now = Cursor1.GetString("time")
		Dim username As String
		username=Cursor1.GetString("who")
        time=DateTime.GetYear(now)&"/"&DateTime.GetMonth(now)&"/"&DateTime.GetDayOfMonth(now)&" "&DateTime.GetHour(now)&":"&DateTime.GetMinute(now)&":"&DateTime.GetSecond(now)
        ListView1.AddTwoLines2(username,time,username)
	Next
	Cursor1.Close
	SQL4.Close

End Sub

Sub ListView1_ItemClick (Position As Int, Value As Object)
	comment.queryuser=Value
	StartActivity(userinfo)
End Sub
Sub Button1_Click
	Activity.Finish
End Sub