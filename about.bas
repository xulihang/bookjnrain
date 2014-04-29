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
	Dim Label2 As Label
	Dim ImageView2 As ImageView
	Dim Button2 As Button
	Dim caidan=0 As Int
	Dim caidan2=0 As Int
	Dim Label1 As Label
End Sub

Sub Activity_Create(FirstTime As Boolean)
	'Do not forget to load the layout file created with the visual designer. For example:
	Activity.LoadLayout("about")
	Activity.AddMenuItem("给管理员留言","messageboard")
End Sub

Sub Activity_Resume

End Sub

Sub Activity_Pause (UserClosed As Boolean)

End Sub

Sub JobDone (job As HttpJob)
	Log("JobName = " & job.JobName & ", Success = " & job.Success)
	If job.Success = True Then
		Select job.JobName
			Case "getver"
				'print the result to the logs
				ProgressDialogHide
				If job.GetString<90 Then
					ToastMessageShow("已是最新版",False)
				Else
				    Dim result As Int
				    result=Msgbox2("发现新版本","","更新","","以后再说",Null)
					If result=DialogResponse.POSITIVE Then
					    ToastMessageShow("将用系统浏览器下载",False)
	                    Dim Intent1 As Intent
                        Intent1.Initialize2("https://raw2.github.com/xulihang/xulihang.github.io/master/apk/bookjnrain.apk", 0)
                        StartActivity(Intent1) 
				    End If
				End If
		End Select
	Else
	    ProgressDialogHide
		Log("Error: " & job.ErrorMessage)
		ToastMessageShow("Error: " & job.ErrorMessage, True)
	End If
	job.Release
End Sub

Sub Button1_Click
	Activity.Finish
End Sub
Sub Button2_Click
    ProgressDialogShow("检查中")
    Dim getver As HttpJob
	getver.Initialize("getver",Me)
	getver.Download("https://bottle-bookjnrain.rhcloud.com/getver")
End Sub
Sub ImageView2_Click
	caidan=caidan+1
	If caidan=1 Then
	    ToastMessageShow("",False)
	End If
	If caidan=5 Then
	    Dim a1 As Animation
		a1.InitializeRotateCenter("", 0, 90, ImageView2)
        ImageView2.Tag = a1
        a1.Duration = 1000
        a1.RepeatCount = 3
        a1.RepeatMode = a1.REPEAT_REVERSE
		a1.Start(ImageView2)
		caidan=0
		ToastMessageShow("6L+Z5piv5Liq5b2p6JuL",False)
		Dim MP As MediaPlayer 'should be done in Sub Process_Globals
        MP.Initialize2("MP")
		MP.Stop 
        MP.Load(File.DirAssets, "wuqu.mid")
        MP.Play 
    End If
End Sub
Sub Label2_Click
	caidan2=caidan2+1
	If caidan2=2 Then
	    ToastMessageShow("",False)
	End If
	If caidan2=5 Then
		caidan2=0
		StartActivity(slotmachine)
    End If	
End Sub

Sub messageboard_click
    comment.queryuser="1120113325"
	StartActivity(usermessage)
End Sub