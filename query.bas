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
	Dim EditText1 As EditText
	Dim Panel1 As Panel
	Dim Spinner1 As Spinner
	Dim according="title" As String
End Sub

Sub Activity_Create(FirstTime As Boolean)
	'Do not forget to load the layout file created with the visual designer. For example:
	Activity.LoadLayout("query")
	Dim Label2 As Label
	Dim Label3 As Label
    Label2 = ListView1.TwoLinesLayout.Label
	Label3 = ListView1.TwoLinesLayout.SecondLabel
    Label2.TextSize = 14
	Label3.TextColor= Colors.Black
	If SQL1.IsInitialized = False Then
	    SQL1.Initialize(File.DirInternal, "mybook.db", False)
	End If
	
	Dim Cursor1 As Cursor
	Cursor1 = SQL1.ExecQuery("SELECT * FROM book")
	For i = 0 To Cursor1.RowCount - 1
		Cursor1.Position = i
		ListView1.AddTwoLines2(Cursor1.GetString("title"),Cursor1.GetString("lasttime")&Cursor1.GetString("publisher")&Cursor1.GetString("price"),Cursor1.GetString("isbn"))
		Log("************************")
		Log(Cursor1.GetString("title"))
	Next
	Cursor1.Close
	Spinner1.AddAll(Array As String("书名","作者","ISBN","出版社"))
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
				Log(job.GetString)
				ToastMessageShow(job.GetString,False)
		End Select
	Else
	    ProgressDialogHide
		Log("Error: " & job.ErrorMessage)
		ToastMessageShow("Error: " & job.ErrorMessage, True)
	End If
	job.Release
End Sub

Sub ListView1_ItemClick (Position As Int, Value As Object)
    Dim info As String
	'Msgbox(Position,Value)
	Dim Cursor1 As Cursor
	Cursor1 = SQL1.ExecQuery("SELECT introduction FROM book")
	Cursor1.Position = Position
	info=Cursor1.GetString("introduction")
	Cursor1.Close
	Msgbox(info,"信息")
End Sub
Sub ListView1_ItemLongClick (Position As Int, Value As Object)
    'Log(Value)
	'Dim result As Int
	'result=Msgbox2("删除该记录？","删除确认","是","","否",Null)
	'If result=DialogResponse.Positive Then
	'    SQL1.ExecNonQuery("DELETE FROM book WHERE isbn Like '"&Value&"'")
	'	ListView1.RemoveAt(Position)
	'End If
	
    Dim r As List 
    r.Initialize 
    r.AddAll(Array As String("删除该记录","上传"))
    Dim m As Int
    Dim x As id 
    m = x.InputList1(r,"我的图书")
	
	Select m
	    Case 0
		    SQL1.ExecNonQuery("DELETE FROM book WHERE isbn Like '"&Value&"'")
	        ListView1.RemoveAt(Position)
	    Case 1
		    Dim bookname As String
	        Dim Cursor1 As Cursor
	        Cursor1 = SQL1.ExecQuery("SELECT title FROM book")
        	Cursor1.Position = Position
	        bookname=Cursor1.GetString("title")
	        Cursor1.Close
			'ToastMessageShow(bookname,False)
            upload(Value,bookname)
	End Select			
    'Msgbox(m,"Result")
End Sub

Sub upload(Value As String,bookname As String)
    ProgressDialogShow("上传中……")
	Dim Reader As TextReader
	Dim username,password As String
	Reader.Initialize(File.OpenInput(File.DirInternal, "user"))
    username = Reader.ReadLine
    password = Reader.ReadLine
    Dim now As Long
    'Dim time As String
	now = DateTime.now
    'time=DateTime.GetYear(now)&"/"&DateTime.GetMonth(now)&"/"&DateTime.GetDayOfMonth(now)&"/"&DateTime.GetHour(now)&"/"&DateTime.GetMinute(now)&"/"
	'Log(time)
	Dim job1 As HttpJob
    job1.Initialize("Job1",Me)
    job1.PostString("https://bottle-bookjnrain.rhcloud.com/login","username="&username&"&password="&password&"&isbn="&Value&"&time="&now&"&bookname="&bookname)
End Sub

Sub Button2_Click
	If EditText1.Visible=False Then
	    EditText1.Visible=True
		Spinner1.Visible=True
		Button1.Visible=False
		Label1.Visible=False
		ListView1.Enabled=False
        Panel1.Visible=True
	Else
	    If EditText1.Text="" Then
	        Msgbox("请输入关键词","")
	    Else
	        EditText1.Visible=False
		    Spinner1.Visible=False
	        Button1.Visible=True
		    Label1.Visible=True
		    ListView1.Enabled=True
		    Panel1.Visible=False
		    ListView1.Clear
		    Dim Cursor1 As Cursor
	        Cursor1 = SQL1.ExecQuery("SELECT * FROM book WHERE "&according&" like '%"&EditText1.Text&"%'")
	        For i = 0 To Cursor1.RowCount - 1
		        Cursor1.Position = i
		        ListView1.AddTwoLines2(Cursor1.GetString("title"),Cursor1.GetString("lasttime")&Cursor1.GetString("publisher")&Cursor1.GetString("price"),Cursor1.GetString("isbn"))
	        Next
	        Cursor1.Close
		    If ListView1.Size=0 Then
			    ToastMessageShow("没有你要的书。",False)
		    End If
	    End If
	End If
End Sub
Sub Spinner1_ItemClick (Position As Int, Value As Object)
	Select Value
	    Case "书名"
		    according="title"
		Case "作者"
		    according="author"
		Case "ISBN"
		    according="isbn"
		Case "出版社"
		    according="publisher"
	End Select
End Sub