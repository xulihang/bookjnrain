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
	Dim username As String
End Sub

Sub Activity_Create(FirstTime As Boolean)
	'Do not forget to load the layout file created with the visual designer. For example:
	Activity.LoadLayout("profile")
	Dim Reader As TextReader
	Reader.Initialize(File.OpenInput(File.DirInternal, "user"))
	username = Reader.ReadLine
	Reader.Close
    If File.Exists(File.DirInternal,username&"-profile") Then
	    readprofile
	End If	
End Sub

Sub Activity_Resume
    If File.Exists(File.DirInternal,"avatar.jpg") Then
	    ImageView2.Bitmap= LoadBitmap(File.DirInternal,"avatar.jpg")
	End If	
End Sub

Sub Activity_Pause (UserClosed As Boolean)

End Sub

Sub readprofile
    Dim Reader As TextReader
	Dim nickname,Phone,location,major,gender,age As String
    Reader.Initialize(File.OpenInput(File.DirInternal, username&"-profile"))
	nickname = Reader.ReadLine
    Phone = Reader.ReadLine
	location = Reader.ReadLine
	major = Reader.ReadLine
    gender = Reader.ReadLine
	age = Reader.ReadLine
    Reader.Close 
	EditText1.Text=nickname
	EditText2.Text=Phone
	EditText3.Text=location
	EditText4.Text=major
	EditText5.Text=gender
	EditText6.Text=age
	If File.Exists(File.DirInternal,"avatar.jpg") Then
	    ImageView2.Bitmap= LoadBitmap(File.DirInternal,"avatar.jpg")
	End If	
End Sub

Sub writeprofile
	Dim Writer As TextWriter
    Writer.Initialize(File.OpenOutput(File.DirInternal, username&"-profile", False))
    Writer.WriteLine(EditText1.Text)
    Writer.WriteLine(EditText2.Text)
	Writer.WriteLine(EditText3.Text)
    Writer.WriteLine(EditText4.Text)
	Writer.WriteLine(EditText5.Text)
    Writer.WriteLine(EditText6.Text)
    Writer.Close 
End Sub
Sub ImageView2_Click
	StartActivity(avatarcrop)
End Sub
Sub Button2_Click
	Activity.Finish
End Sub
Sub Button1_Click
    writeprofile
	ToastMessageShow("保存成功",False)
	Activity.Finish
End Sub