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

	Dim ImageView1 As ImageView
	Dim Label1 As Label
	Dim ListView1 As ListView
	Dim Button1 As Button
End Sub

Sub Activity_Create(FirstTime As Boolean)
	'Do not forget to load the layout file created with the visual designer. For example:
	Activity.LoadLayout("follows")
	Dim Label2 As Label
	Label2=ListView1.SingleLineLayout.Label
	Label2.TextColor=Colors.White
	Label2.TextSize=18
    'ListView1.Color=Colors.ARGB(245,34,139,34)
	Dim Reader As TextReader
	Dim username As String
	Reader.Initialize(File.OpenInput(File.DirInternal, "user"))
	username = Reader.ReadLine
	Reader.Close
    Reader.Initialize(File.OpenInput(File.DirInternal, username&"-follow"))
    Dim line As String
    line = Reader.ReadLine
    Do While line <> Null
        Log(line)
		If line<>"" Then
		    ListView1.AddSingleLine2(line,line)
		End If
        line = Reader.ReadLine
    Loop
    Reader.Close 
End Sub

Sub Activity_Resume

End Sub

Sub Activity_Pause (UserClosed As Boolean)

End Sub



Sub ListView1_ItemClick (Position As Int, Value As Object)
    comment.queryuser=Value
	StartActivity(userinfo)
End Sub
Sub ListView1_ItemLongClick (Position As Int, Value As Object)
    Dim new As String
	Dim resultofmsgbox As Int
    resultofmsgbox=Msgbox2("不再关注他？","关注","不关注了","","继续关注",Null)
	If resultofmsgbox=DialogResponse.POSITIVE Then
	    Dim Reader As TextReader
		Dim username As String
	    Reader.Initialize(File.OpenInput(File.DirInternal, "user"))
	    username = Reader.ReadLine
	    Reader.Close
	    Reader.Initialize(File.OpenInput(File.DirInternal, username&"-follow"))
        new=Reader.ReadAll.Replace(Value,"")
		Log(new)
        Reader.Close 
	    Dim Writer As TextWriter
        Writer.Initialize(File.OpenOutput(File.DirInternal, username&"-follow", False))
        Writer.Write(new)
        Writer.Close
		ListView1.RemoveAt(Position)
	End If
End Sub
Sub Button1_Click
	Activity.Finish
End Sub