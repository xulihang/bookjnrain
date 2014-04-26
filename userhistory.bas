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
    Dim SQL1 As SQL
End Sub

Sub Globals
	'These global variables will be redeclared each time the activity is created.
	'These variables can only be accessed from this module.

	Dim Button1 As Button
	Dim ListView1 As ListView
	Dim ImageView1 As ImageView
	Dim Label1 As Label
	Dim Button2 As Button
End Sub

Sub Activity_Create(FirstTime As Boolean)
	'Do not forget to load the layout file created with the visual designer. For example:
	Activity.LoadLayout("query")
	Dim job1 As HttpJob
	job1.Initialize("Job1",Me)
    job1.Download("https://bottle-bookjnrain.rhcloud.com/getuserhistory/"&comment.queryuser)
	ProgressDialogShow("获取数据中...")
End Sub

Sub Activity_Resume

End Sub

Sub Activity_Pause (UserClosed As Boolean)

End Sub

Sub Button1_Click
    Activity.Finish
End Sub


Sub JobDone (job As HttpJob)
	Log("JobName = " & job.JobName & ", Success = " & job.Success)
	If job.Success = True Then
		Select job.JobName
			Case "Job1"
				'print the result to the logs
				ProgressDialogHide
				Dim out As OutputStream
				out=File.OpenOutput(File.DirInternal,comment.queryuser&".db",False)
                File.Copy2(job.GetInputStream,out)
				out.Close
				loadlist
		End Select
	Else
	    ProgressDialogHide
		Log("Error: " & job.ErrorMessage)
		ToastMessageShow("Error: " & job.ErrorMessage, True)
	End If
	job.Release
End Sub

Sub loadlist
    Dim Label2 As Label
	Dim Label3 As Label
    Label2 = ListView1.TwoLinesLayout.Label
	Label3 = ListView1.TwoLinesLayout.SecondLabel
    Label2.TextSize = 14
	Label3.TextColor= Colors.Black
	Label3.TextSize=13
	If SQL1.IsInitialized = False Then
	    SQL1.Initialize(File.DirInternal, comment.queryuser&".db", False)
	End If
	Dim Cursor1 As Cursor
	Cursor1 = SQL1.ExecQuery("SELECT * FROM book")
	For i = 0 To Cursor1.RowCount - 1
		Cursor1.Position = i
		Dim now As String
		now=Cursor1.GetString("lasttime")
		Dim time As String
		time=DateTime.GetYear(now)&"/"&DateTime.GetMonth(now)&"/"&DateTime.GetDayOfMonth(now)&" "&DateTime.GetHour(now)&":"&DateTime.GetMinute(now)&":"&DateTime.GetSecond(now)
		ListView1.AddTwoLines2(Cursor1.GetString("title"),time&" "&Cursor1.GetString("publisher")&Cursor1.GetString("price"),Cursor1.GetString("isbn"))
		Log("************************")
		Log(Cursor1.GetString("title"))
	Next
	Cursor1.Close
End Sub

Sub ListView1_ItemClick (Position As Int, Value As Object)
    Dim CC As BClipboard
    CC.setText(Value)
	ToastMessageShow("ISBN已复制到剪切板。",False)
End Sub

Sub Button2_Click
	Msgbox("暂不支持","")
End Sub