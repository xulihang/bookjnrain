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

End Sub

Sub Activity_Resume

End Sub

Sub Activity_Pause (UserClosed As Boolean)

End Sub



Sub Button1_Click
    'ToastMessageShow("注册成功！",False)
	Dim password As String
	password=b.EncodeStoS(EditText2.Text,"UTF8")
	Label3.Text=password
	Dim out As String
	out=b.DecodeStoS(password,"UTF8")
	ToastMessageShow(out,False)
End Sub
Sub Label1_Click
	StartActivity(login)
	Activity.Finish
End Sub