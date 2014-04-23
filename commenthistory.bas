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
	Dim ImageView1 As ImageView
	Dim Label1 As Label
	Dim ListView1 As ListView
End Sub

Sub Activity_Create(FirstTime As Boolean)
	'Do not forget to load the layout file created with the visual designer. For example:
	Activity.LoadLayout("uploaderlist")
	Label1.Text="我的评论"
    creatlistview
End Sub

Sub Activity_Resume

End Sub

Sub Activity_Pause (UserClosed As Boolean)

End Sub


Sub creatlistview
	If comment.SQL1.IsInitialized = False Then
	    comment.SQL1.Initialize(File.DirInternal, "comment.db", False)
	End If
	
	Dim Label2 As Label
	Label2=ListView1.TwoLinesLayout.Label
	Label2.TextColor=Colors.White
	Label2.TextSize=18
	Dim Label3 As Label
	Label3=ListView1.TwoLinesLayout.SecondLabel
	Label3.TextColor=Colors.Black
	Label3.TextSize=16
	
	Dim Cursor1 As Cursor
	Cursor1 = comment.SQL1.ExecQuery("SELECT * FROM book")
	For i = 0 To Cursor1.RowCount - 1
		Cursor1.Position = i
		Dim time As String
	    time = Cursor1.GetString("time")
		Dim content As String
		content=Cursor1.GetString("comment")
		Dim isbn As String
		isbn=Cursor1.GetString("isbn")
        ListView1.AddTwoLines2(Cursor1.GetString("title")&" 我说:"&content,isbn&" "&time,isbn)
	Next
	Cursor1.Close
	comment.SQL1.Close

End Sub

Sub ListView1_ItemClick (Position As Int, Value As Object)
    Main.book=Value
    StartActivity(comment)
End Sub
Sub Button1_Click
	Activity.Finish
End Sub
Sub ListView1_ItemLongClick (Position As Int, Value As Object)
    Dim CC As BClipboard
    CC.setText(Value)
	ToastMessageShow("ISBN已复制到剪切板。",False)
	If comment.SQL1.IsInitialized = False Then
	    comment.SQL1.Initialize(File.DirInternal, "comment.db", False)
	End If
	Dim Cursor1 As Cursor
	Cursor1 = comment.SQL1.ExecQuery("SELECT * FROM book")
	Cursor1.Position = Position
	Msgbox(Cursor1.GetString("comment"),"我说")
	Cursor1.Close
	comment.SQL1.Close
End Sub