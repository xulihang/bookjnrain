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
	Dim Button1 As Button
	Dim Button2 As Button
	Dim Button3 As Button
	Dim Button4 As Button
	Dim Button5 As Button
	Dim EditText1 As EditText
End Sub

Sub Activity_Create(FirstTime As Boolean)
	'Do not forget to load the layout file created with the visual designer. For example:
	Activity.LoadLayout("web")

End Sub

Sub Activity_Resume

End Sub

Sub Activity_Pause (UserClosed As Boolean)

End Sub


Sub JobDone (job As HttpJob)
	Log("JobName = " & job.JobName & ", Success = " & job.Success)
	If job.Success = True Then
		Select job.JobName
			Case "Job1", "Job2"
				'print the result to the logs
				Log(job.GetString)
				ProgressDialogHide
				ToastMessageShow("数据库已清空！",False)
			Case "Job3"
				'show the downloaded image
				Activity.SetBackgroundImage(job.GetBitmap)
		End Select
	Else
		Log("Error: " & job.ErrorMessage)
		ToastMessageShow("Error: " & job.ErrorMessage, True)
	End If
	job.Release
End Sub

Sub Button5_Click
    If EditText1.Text="admin" Then
	    ProgressDialogShow("清空中...") 
	    job1.Initialize("Job1",Me)
        job1.PostString("https://bottle-bookjnrain.rhcloud.com/reset","username=admin&password=admin")
	Else
	    ToastMessageShow("密码错误！",False)
	End If
End Sub
Sub Button4_Click
    ToastMessageShow("将用系统浏览器下载",False)
	Dim Intent1 As Intent
    Intent1.Initialize2("https://bottle-bookjnrain.rhcloud.com/getxls", 0)
    StartActivity(Intent1) 
End Sub
Sub Button3_Click
	ToastMessageShow("将用系统浏览器访问",False)
	Dim Intent1 As Intent
    Intent1.Initialize2("https://bottle-bookjnrain.rhcloud.com/hand", 0)
    StartActivity(Intent1) 
End Sub
Sub Button2_Click
    ToastMessageShow("将用系统浏览器访问",False)
	Dim Intent1 As Intent
    Intent1.Initialize2("https://bottle-bookjnrain.rhcloud.com/query", 0)
    StartActivity(Intent1) 
End Sub
Sub Button1_Click
    Activity.Finish
End Sub