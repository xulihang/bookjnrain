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
	Dim ImageView1 As ImageView
	Dim Label1 As Label
	Dim Label2 As Label
	Dim Label3 As Label
	Dim Label4 As Label
	Dim Label5 As Label
	Dim Label6 As Label
	Dim Spinner1 As Spinner
	Dim purpose As Int
End Sub

Sub Activity_Create(FirstTime As Boolean)
	'Do not forget to load the layout file created with the visual designer. For example:
	Activity.LoadLayout("idlebook")
    Spinner1.AddAll(Array As String("出售","出借"))
End Sub

Sub Activity_Resume

End Sub

Sub Activity_Pause (UserClosed As Boolean)

End Sub

Sub Spinner1_ItemClick (Position As Int, Value As Object)
	Select Position
	    Case 0
		    purpose=0
		Case 1
		    purpose=1
	End Select
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
	Else
		Msgbox("请先登录！","")
		Activity.Finish
	End If
	ProgressDialogShow("上传中...")
    Dim now As Long
	now = DateTime.now
	Dim job4 As HttpJob
    job4.Initialize("Job4",Me)
    job4.PostString("https://bottle-bookjnrain.rhcloud.com/idlepublish","username="&username&"&password="&password&"&bookname="&EditText1.Text&"&isbn="&EditText2.Text&"&price="&EditText3.Text&"&detail="&EditText4.Text&"&purpose="&purpose&"&pubtime="&now)
	Log("username="&username&"&password="&password&"&bookname="&EditText1.Text&"&isbn="&EditText2.Text&"&price="&EditText3.Text&"&detail="&EditText4.Text&"&purpose="&purpose&"&pubtime="&now)
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