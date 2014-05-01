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
    
End Sub

Sub Globals
	'These global variables will be redeclared each time the activity is created.
	'These variables can only be accessed from this module.
    Dim SQL1 As SQL
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
    job1.Download("https://bottle-bookjnrain.rhcloud.com/getaskbook")
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
				out=File.OpenOutput(File.DirInternal,"askbook.db",False)
                File.Copy2(job.GetInputStream,out)
				out.Close
				loadlist
			Case "Job2"
			    ProgressDialogHide
				ToastMessageShow(job.GetString,False)
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
	If SQL1.IsInitialized = False Then
	    SQL1.Initialize(File.DirInternal, "askbook.db", False)
	End If
	Dim Cursor1 As Cursor
	Cursor1 = SQL1.ExecQuery("SELECT * FROM statics")
	For i = 0 To Cursor1.RowCount - 1
		Cursor1.Position = i
		ListView1.AddTwoLines2(Cursor1.GetString("username")&" : "&Cursor1.GetString("bookname"),Cursor1.GetString("detail"),Cursor1.GetString("username"))
	Next
	Cursor1.Close
End Sub

Sub ListView1_ItemLongClick (Position As Int, Value As Object)
    Dim r As List 
    r.Initialize
	r.Add("显示用户信息")
	Dim Reader As TextReader
    Dim username,password As String
	If File.Exists(File.DirInternal,"user") Then
        Reader.Initialize(File.OpenInput(File.DirInternal, "user"))
		username = Reader.ReadLine
        password=  Reader.ReadLine
        Reader.Close 
	End If
		
	If username=Value Then
		r.Add("删除该记录")
	End If
    Dim m As Int
    Dim x As id 
    m = x.InputList1(r,"我的图书")	
	Select m
	    Case 0
		    comment.queryuser=Value
			StartActivity(userinfo)
		Case 1
		    ProgressDialogShow("操作中")
		    ListView1.RemoveAt(Position)
	        Dim Cursor1 As Cursor
	        Cursor1 = SQL1.ExecQuery("SELECT * FROM statics")
		    Cursor1.Position = Position
            Dim bookname As String
			bookname=Cursor1.GetString("bookname")
			Dim time As String
			time=Cursor1.GetString("pubtime")
	        Cursor1.Close
		    Dim job2 As HttpJob
			job2.Initialize("Job2",Me)
			job2.PostString("https://bottle-bookjnrain.rhcloud.com/deleteaskbook","username="&Value&"&pubtime="&time)
	End Select	
End Sub

Sub Button2_Click
	Msgbox("暂不支持","")
End Sub

Sub ListView1_ItemClick (Position As Int, Value As Object)
    Dim detail As String
    Dim Cursor1 As Cursor
	Cursor1 = SQL1.ExecQuery("SELECT * FROM statics")
	Cursor1.Position = Position
	detail=Cursor1.GetString("detail")
	Cursor1.Close
	Msgbox(detail,"详细")
End Sub