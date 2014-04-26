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
	Dim ImageView1 As ImageView
	Dim ImageView2 As ImageView
	Dim Label1 As Label
	Dim Label2 As Label
	Dim Label3 As Label
	Dim Label4 As Label
	Dim Label5 As Label
	Dim Label6 As Label
	Dim Label7 As Label
	Dim Button3 As Button
End Sub

Sub Activity_Create(FirstTime As Boolean)
	'Do not forget to load the layout file created with the visual designer. For example:
	Activity.LoadLayout("userinfo")
    EditText1.Enabled=False
	EditText2.Enabled=False
	EditText3.Enabled=False
	EditText4.Enabled=False
	EditText5.Enabled=False
	EditText6.Enabled=False
	ProgressDialogShow("加载中")
	Dim URL As String
	Dim job1 As HttpJob
	URL="https://bottle-bookjnrain.rhcloud.com/getuserinfo/"&comment.queryuser
    job1.Initialize("Job1",Me)
    job1.Download(URL)
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
			    ProgressDialogHide
				Dim out As OutputStream
				out=File.OpenOutput(File.DirInternal,comment.queryuser&"-info",False)
                File.Copy2(job.GetInputStream,out)
				out.Close
				
                Dim Reader As TextReader
	            Dim nickname,PhoneNumber,location,major,gender,age As String
                Reader.Initialize(File.OpenInput(File.DirInternal,comment.queryuser&"-info"))
	            nickname = Reader.ReadLine
                PhoneNumber = Reader.ReadLine
	            location = Reader.ReadLine
	            major = Reader.ReadLine
                gender = Reader.ReadLine
	            age = Reader.ReadLine
                Reader.Close 
	            EditText1.Text=nickname
	            EditText2.Text=PhoneNumber
	            EditText3.Text=location
	            EditText4.Text=major
	            EditText5.Text=gender
	            EditText6.Text=age
	            'If File.Exists(File.DirInternal,"avatar.jpg") Then
	            '    ImageView2.Bitmap= LoadBitmap(File.DirInternal,"avatar.jpg")
	            'End If	
			Case "Job2"
			    ProgressDialogHide
				Dim out As OutputStream
				out=File.OpenOutput(File.DirInternal,comment.queryuser&".jpg",False)
                File.Copy2(job.GetInputStream,out)
				out.Close
				ImageView2.Bitmap = LoadBitmap(File.DirInternal,comment.queryuser&".jpg")
		End Select
	Else
	    ProgressDialogHide
		Log("Error: " & job.ErrorMessage)
		ToastMessageShow("Error: " & job.ErrorMessage, True)
	End If
	job.Release
End Sub

Sub ImageView2_Click
    ProgressDialogShow("加载中")
    Dim URL As String
	Dim job2 As HttpJob
	URL="https://bottle-bookjnrain.rhcloud.com/getavatar/"&comment.queryuser
    job2.Initialize("Job2",Me)
    job2.Download(URL)
End Sub
Sub Button2_Click
	Activity.Finish
End Sub
Sub Button1_Click
	Dim Reader As TextReader
	Dim username As String
	Reader.Initialize(File.OpenInput(File.DirInternal, "user"))
	username = Reader.ReadLine
	Reader.Close
	Dim exist=0 As Int '默认没关注
    If File.Exists(File.DirInternal,username&"-follow") Then
	    Reader.Initialize(File.OpenInput(File.DirInternal, username&"-follow"))
	    Dim line As String
        line = Reader.ReadLine
        Do While line <> Null
            If line=comment.queryuser Then
			    ToastMessageShow("已关注！",False)
				exist=1
				Exit
			End If
            line = Reader.ReadLine
        Loop
        Reader.Close 
	End If
	If exist=0 Then
	    Dim Writer As TextWriter
        Writer.Initialize(File.OpenOutput(File.DirInternal, username&"-follow", False))
        Writer.WriteLine(comment.queryuser)
        Writer.Close
		ToastMessageShow("关注成功！",False)
	End If
End Sub
Sub Button3_Click
	StartActivity(userhistory)
End Sub