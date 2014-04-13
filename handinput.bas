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

	Dim Button1 As Button
	Dim Button2 As Button
	Dim EditText1 As EditText
	Dim EditText2 As EditText
	Dim EditText3 As EditText
	Dim EditText4 As EditText
	Dim EditText5 As EditText
	Dim EditText6 As EditText
	Dim EditText7 As EditText
	Dim ImageView1 As ImageView
	Dim Label1 As Label
	Dim Label2 As Label
	Dim Label3 As Label
	Dim Label4 As Label
	Dim Label5 As Label
	Dim Label6 As Label
	Dim Label7 As Label
	Dim Label8 As Label
	Dim Button3 As Button
End Sub

Sub Activity_Create(FirstTime As Boolean)
	'Do not forget to load the layout file created with the visual designer. For example:
	Activity.LoadLayout("handinput")

End Sub

Sub Activity_Resume

End Sub

Sub Activity_Pause (UserClosed As Boolean)

End Sub



Sub Button2_Click
	Activity.Finish
End Sub
Sub Button1_Click
	Dim exist=0 As Int '默认不存在
    'Dim now As Long
    'Dim time As String
	'now = DateTime.now
    'time=DateTime.GetYear(now)&"/"&DateTime.GetMonth(now)&"/"&DateTime.GetDayOfMonth(now)&"/"&DateTime.GetHour(now)&"/"&DateTime.GetMinute(now)&"/"
	Dim Cursor1 As Cursor
	Cursor1 = Main.SQL1.ExecQuery("SELECT title FROM book")
    For I = 0 To Cursor1.RowCount - 1
	    Cursor1.Position = I
		If Cursor1.GetString("title")=EditText1.Text Then
		    exist=1
			ToastMessageShow("该图书已扫描过。",False)
		End If    
	Next
	Cursor1.Close
	If exist=0 Then
        Main.SQL1.ExecNonQuery2("INSERT INTO book VALUES(?, ?, ?, ?, ?, ?, ?)", Array As Object(EditText1.Text, EditText2.Text, EditText3.Text, EditText4.Text, EditText5.Text, EditText6.Text, EditText7.Text  ))
		ToastMessageShow("已存储",False)
    End If
End Sub

Sub LoadJSONDATA(sJSONDATA As String)

    Log("Data:"&sJSONDATA)
 
    Dim JSON As JSONParser
    JSON.Initialize(sJSONDATA)
    Dim map1 As Map
    map1 = JSON.Nextobject
    'Log(map1.Size)
    'm = map1.GetKeyAt(12)
    EditText1.Text= map1.GetValueAt(12)
    EditText2.Text= map1.GetValueAt(13)
    EditText3.Text= map1.GetValueAt(9)
    EditText5.Text= map1.GetValueAt(4)  '根据index
	EditText7.Text= map1.get("summary") '根据Key的名称
End Sub

Sub JobDone (job As HttpJob)
	Log("JobName = " & job.JobName & ", Success = " & job.Success)
	If job.Success = True Then
		Select job.JobName
			Case "Job2"
			    ProgressDialogHide
			    Dim jresult As String
			    jresult = job.GetString
				'sJSONData = jresult
				LoadJSONDATA(jresult)
		End Select
	Else
	    ProgressDialogHide
		Log("Error: " & job.ErrorMessage)
		ToastMessageShow("Error: " & job.ErrorMessage, True)
	End If
	job.Release
End Sub
Sub Button3_Click
	Dim job2 As HttpJob
	job2.Initialize("Job2",Me)
    job2.Download("https://api.douban.com/v2/book/isbn/:"&EditText4.Text)
	ProgressDialogShow("获取数据中...")
End Sub