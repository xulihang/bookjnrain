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
    Dim job1 As HttpJob
	Dim job2 As HttpJob
	Dim Button1 As Button
	Dim EditText1 As EditText
	Dim EditText2 As EditText
	Dim ImageView1 As ImageView
	Dim Label1 As Label
	Dim Label2 As Label
	Dim EditText3 As EditText
	Dim Label3 As Label
End Sub

Sub Activity_Create(FirstTime As Boolean)
	'Do not forget to load the layout file created with the visual designer. For example:
	Activity.LoadLayout("login")
    EditText3.Enabled=False

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
				Log(job.GetString)
				If job.GetString.Contains("<title>e江南</title>")=True OR job.GetString.Contains("<title>江南大学</title>")=True Then
				    ToastMessageShow("登录失败,请加输验证码。",False)
					EditText3.Enabled=True
					job2.Initialize("Job2", Me)
                    job2.Download("http://e.jiangnan.edu.cn/valcode.jpg")
				Else 
				    ToastMessageShow("登录成功!",False)
					Dim Writer As TextWriter
                    Writer.Initialize(File.OpenOutput(File.DirInternal, "user", False))
                    Writer.WriteLine(EditText1.Text)
                    Writer.WriteLine(EditText2.Text)
                    Writer.Close 
					Activity.Finish
				End If
			Case "Job2"
			    Dim ValCode As ImageView
				ValCode.Initialize("")
				ValCode.Bitmap=job2.GetBitmap
				Activity.AddView(ValCode,EditText2.Left,EditText2.Top+100dip,60dip,20dip)
		End Select
	Else
	    ProgressDialogHide
		Log("Error: " & job.ErrorMessage)
		ToastMessageShow("Error: " & job.ErrorMessage, True)
	End If
	job.Release
End Sub

Sub Label1_Click
	Msgbox("请联系管理员。","忘记密码")
End Sub

Sub Button1_Click
    userlogin
	'ToastMessageShow("登录成功",False)
	'Activity.Finish
End Sub

Sub userlogin
    Dim URL As String
	If EditText3.Enabled=False Then
	    URL="http://e.jiangnan.edu.cn/main.login.do?email="&EditText1.Text&"&password="&EditText2.Text
	Else
	    URL="http://e.jiangnan.edu.cn/main.login.do?email="&EditText1.Text&"&password="&EditText2.Text&"&validationCode="&EditText3.Text
	End If
    job1.Initialize("Job1",Me)
	Log(URL)
    job1.Download(URL)
	ProgressDialogShow("登录中")
End Sub