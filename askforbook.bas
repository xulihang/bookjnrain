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
	Dim ImageView1 As ImageView
	Dim Label1 As Label
	Dim Label2 As Label
	Dim Label3 As Label
End Sub

Sub Activity_Create(FirstTime As Boolean)
	'Do not forget to load the layout file created with the visual designer. For example:
	Activity.LoadLayout("askforbook")

End Sub

Sub Activity_Resume

End Sub

Sub Activity_Pause (UserClosed As Boolean)

End Sub



Sub Button2_Click
	Activity.Finish
End Sub

Sub Button1_Click
	Dim Reader As TextReader
    Dim username,password As String
	If File.Exists(File.DirInternal,"user") Then
        Reader.Initialize(File.OpenInput(File.DirInternal, "user"))
		username = Reader.ReadLine
		password = Reader.ReadLine
        Reader.Close 
		ProgressDialogShow("上传中...")
        Dim now As Long
	    now = DateTime.now
	    Dim job4 As HttpJob
        job4.Initialize("Job4",Me)
        job4.PostString("https://bottle-bookjnrain.rhcloud.com/askforbook","username="&username&"&password="&password&"&bookname="&EditText1.Text&"&detail="&EditText2.Text&"&pubtime="&now)
	Else
		Msgbox("请先登录！","")
		Activity.Finish
	End If
End Sub


Sub JobDone (job As HttpJob)
	Log("JobName = " & job.JobName & ", Success = " & job.Success)
	If job.Success = True Then
		Select job.JobName
			Case "Job4"
				'print the result to the logs
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