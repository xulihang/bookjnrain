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
	Dim EditText1 As EditText
	Dim EditText2 As EditText
	Dim ImageView1 As ImageView
	Dim Label1 As Label
	Dim Label2 As Label
	Dim Label3 As Label
	Dim b As Base64
End Sub

Sub Activity_Create(FirstTime As Boolean)
	'Do not forget to load the layout file created with the visual designer. For example:
	Activity.LoadLayout("register")
    Dim Reader As TextReader   
	Reader.Initialize(File.OpenInput(File.DirInternal, "user"))
	EditText1.Text = Reader.ReadLine
	EditText1.Enabled=False
	Reader.Close
	If File.Exists(File.DirInternal,EditText1.Text&"-account")=True Then
	    Label2.Text="修改本地密码"
		Button1.Text="修改"
		Label3.Text="因为注销会清除密码，所以再次登录若与e江南密码不同，需要更改。"
	End If
End Sub

Sub Activity_Resume

End Sub

Sub Activity_Pause (UserClosed As Boolean)

End Sub


Sub JobDone (job As HttpJob)
	Log("JobName = " & job.JobName & ", Success = " & job.Success)
	If job.Success = True Then
		Select job.JobName
			Case "Job1"
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

Sub Button1_Click
    'ToastMessageShow("注册成功！",False)
	'Dim password As String
	'password=b.EncodeStoS(EditText2.Text,"UTF8")
	'Label3.Text=password
	'Dim out As String
	'out=b.DecodeStoS(password,"UTF8")
	'ToastMessageShow(out,False)

	If File.Exists(File.DirInternal,EditText1.Text&"-account")=False Then
	    ProgressDialogShow("注册中")
	    Dim job1 As HttpJob
	    job1.Initialize("Job1",Me)
        job1.PostString("https://bottle-bookjnrain.rhcloud.com/user/new","username="&EditText1.Text&"&password="&EditText2.Text)
	Else
	    ToastMessageShow("修改成功",False)
	End If
	Dim write As TextWriter
	write.Initialize(File.OpenOutput(File.DirInternal,EditText1.Text&"-account",False))
	write.WriteLine(0)
	write.Close
	write.Initialize(File.OpenOutput(File.DirInternal,"user",False))
	write.WriteLine(EditText1.Text)
	write.WriteLine(EditText2.Text)
	write.Close
End Sub
Sub Label1_Click
	'StartActivity(login)
	Msgbox("已有账户仍点注册。","")
End Sub