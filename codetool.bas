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
	Dim myInterface As JSInterface
	Dim zx As Zxing_B4A
	Dim WebView1 As WebView
	Dim EditText1 As EditText
	Dim Button1 As Button
	Dim ImageView1 As ImageView
	Dim Label1 As Label
	Dim Label2 As Label
End Sub

Sub Activity_Create(FirstTime As Boolean)
	'Do not forget to load the layout file created with the visual designer. For example:
	Activity.LoadLayout("qrcode")
	Activity.AddMenuItem("保存图片","save")
	Activity.AddMenuItem("生成图片","generate")
    WebView1.LoadUrl("file:///android_asset/qrcode.htm")
	myInterface.addJSInterface(WebView1, "B4A")
	Dim Reader As TextReader
	Dim username As String
	Reader.Initialize(File.OpenInput(File.DirInternal, "user"))
	username = Reader.ReadLine
	Reader.Close
	EditText1.Text="关注"&username
	generate_click
End Sub

Sub Activity_Resume

End Sub

Sub Activity_Pause (UserClosed As Boolean)

End Sub


Sub save_click
    Dim now As Long
	now=DateTime.now
    Dim img As Bitmap
    img=WebView1.CaptureBitmap
	Dim out As OutputStream
	out=File.OpenOutput(File.DirRootExternal, now&".jpg", False)
	img.WriteToStream(out, 100, "JPEG")
    out.Flush
    out.Close
	ToastMessageShow("已保存到"&File.DirRootExternal&"/"&now&".jpg",False)
End Sub

Sub generate_click
	Dim myJS As String
	myJS="getedittext();"
	myInterface.execJS(WebView1, myJS)
End Sub

Sub ShowToast(Message As String)
	'	A Sub that receives a string from javascript and displays it as a toast message
	ToastMessageShow(Message, True)
End Sub

Sub WebView1_PageFinished (Url As String)
	
End Sub

Sub GetNewDataItem As String
	'	A Sub that returns a string from B4A to javascript
	Return EditText1.Text
End Sub

Sub LogIt(Message As String)
	'	A simple Sub to log a message from javascript to logcat
	Log("LogIt says: '"&Message&"'")
End Sub


Sub Button1_Click
	zx.BeginScan("myzx")
End Sub

Sub myzx_result(atype As String,Values As String)
	'Msgbox("type:"&atype&"values:"&Values,"result")
	Log(Values.SubString2(0,2))
    If Values.SubString2(0,2)="关注" Then
	    Log(Values.SubString(2))
		comment.queryuser=Values.SubString(2)
		StartActivity(userinfo)
	End If
End Sub