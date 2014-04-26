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
	Dim ImageView1 As ImageView
	Dim Label1 As Label
	Dim Spinner1 As Spinner
	Dim gif As GifDecoder
	Dim ImageView2 As ImageView
	Dim Timer1 As Timer
	Dim Frame As Int
	Dim mood As Int
	
End Sub

Sub Activity_Create(FirstTime As Boolean)
	'Do not forget to load the layout file created with the visual designer. For example:
	Activity.LoadLayout("pubmood")
    Spinner1.AddAll(Array As String("开心","难过","郁闷","无聊","愤怒","擦汗","奋斗","慵懒","衰"))
	Timer1.Initialize("Timer1",200)
End Sub

Sub Activity_Resume

End Sub

Sub Activity_Pause (UserClosed As Boolean)
    Timer1.Enabled = False
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

Sub Spinner1_ItemClick (Position As Int, Value As Object)
    mood=Position
	Log(mood)
	Select Value
	    Case "开心"
		    loadgif("kx.gif")
		Case "难过"
		    loadgif("ng.gif")
		Case "郁闷"
		    loadgif("ym.gif")
		Case "无聊"
		    loadgif("wl.gif")
		Case "愤怒"
		    loadgif("nu.gif")
		Case "擦汗"
		    loadgif("ch.gif")
		Case "奋斗"
		    loadgif("fd.gif")
		Case "慵懒"
		    loadgif("yl.gif")
		Case "衰"
		    loadgif("shuai.gif")
	End Select
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
	    Dim date As String
	    date=DateTime.GetYear(now)&DateTime.GetMonth(now)&DateTime.GetDayOfMonth(now)
	    Log(date)
	    Dim job1 As HttpJob
	    job1.Initialize("Job1",Me)
	    job1.PostString("https://bottle-bookjnrain.rhcloud.com/everydaymood","username="&username&"&mood="&mood&"&words="&EditText1.Text&"&pubtime="&now&"&date="&date)
	Else
		Msgbox("请先登录！","")
		Activity.Finish
	End If
End Sub


Sub Timer1_Tick
	Timer1.Enabled = False
	Frame = Frame + 1
	If Frame >= gif.FrameCount Then
		Frame = 0
	End If
	'Timer1.Interval = gif.Delay(Frame)	
	ImageView2.Bitmap = gif.Frame(Frame)
	Timer1.Enabled = True
End Sub

Sub loadgif(filename As String)
	gif.DisposeFrames
	gif.Load(File.DirAssets, "biaoqing/"&filename)
	ToastMessageShow(gif.FrameCount & " frames", True)
	Frame = 0
	'Timer1.Interval = gif.Delay(Frame)	
	ImageView2.Bitmap = gif.Frame(Frame)
	'ImageView1.Gravity = Gravity.FILL
	Timer1.Enabled = True
	gif.SaveFrame(0, File.DirRootExternal, "frame0.png", "P", 85)	
End Sub