Type=Activity
Version=3
@EndOfDesignText@
#Region  Activity Attributes 
	#FullScreen: False
	#IncludeTitle: True
#End Region

Sub Process_Globals
	'These global variables will be declared once when the application starts.
	'These variables can be accessed from all modules.
    Dim SQL1 As SQL
End Sub

Sub Globals
	'These global variables will be redeclared each time the activity is created.
	'These variables can only be accessed from this module.

	Dim Button1 As Button
	Dim ListView1 As ListView
End Sub

Sub Activity_Create(FirstTime As Boolean)
	'Do not forget to load the layout file created with the visual designer. For example:
	Activity.LoadLayout("query")
	If SQL1.IsInitialized = False Then
	    SQL1.Initialize(File.DirInternal, "mybook.db", False)
	End If
	Dim Cursor1 As Cursor
	Cursor1 = SQL1.ExecQuery("SELECT title FROM book")
	For i = 0 To Cursor1.RowCount - 1
		Cursor1.Position = i
		ListView1.AddSingleLine2(Cursor1.GetString("title"),i)
		Log("************************")
		Log(Cursor1.GetString("title"))
	Next
	Cursor1.Close
End Sub

Sub Activity_Resume

End Sub

Sub Activity_Pause (UserClosed As Boolean)

End Sub

Sub Button1_Click
    Activity.Finish
End Sub

Sub ListView1_ItemClick (Position As Int, Value As Object)
    Dim info As String
	'Msgbox(Position,Value)
	Dim Cursor1 As Cursor
	Cursor1 = SQL1.ExecQuery("SELECT introduction FROM book")
	Cursor1.Position = Position
	info=Cursor1.GetString("introduction")
	Cursor1.Close
	Msgbox(info,"信息")
End Sub