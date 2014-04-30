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
    Dim SQL1 As SQL
	Dim Button1 As Button
	Dim EditText1 As EditText
	Dim EditText2 As EditText
	Dim EditText3 As EditText
	Dim ImageView1 As ImageView
	Dim Label1 As Label
End Sub

Sub Activity_Create(FirstTime As Boolean)
	'Do not forget to load the layout file created with the visual designer. For example:
	Activity.LoadLayout("note")
	If File.Exists(File.DirInternal,"mynote.db")=False Then
	    SQL1.Initialize(File.DirInternal, "mynote.db", True)
		SQL1.ExecNonQuery("CREATE TABLE note (title , page, note,time)")
	End If
	
	If SQL1.IsInitialized = False Then
	    SQL1.Initialize(File.DirInternal, "mynote.db", False)
	End If
End Sub

Sub Activity_Resume

End Sub

Sub Activity_Pause (UserClosed As Boolean)

End Sub



Sub Button1_Click
	storetodatabase
End Sub

Sub storetodatabase
    Dim now As Long
	now = DateTime.now
    SQL1.ExecNonQuery2("INSERT INTO note VALUES(?, ?, ?, ?)", Array As Object(EditText1.Text, EditText2.Text, EditText3.Text, now))
    ToastMessageShow("已存储",False)
End Sub