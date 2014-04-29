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
	Dim SQL4 As SQL
End Sub

Sub Globals
	'These global variables will be redeclared each time the activity is created.
	'These variables can only be accessed from this module.

	Dim Button1 As Button
	Dim EditText1 As EditText
	Dim ImageView1 As ImageView
	Dim Label1 As Label
	Dim clv1 As CustomListView
	Dim Panel2 As Panel
	Dim su As StringUtils
	Dim ime1 As IME
	Dim tusername As String
End Sub

Sub Activity_Create(FirstTime As Boolean)
	'Do not forget to load the layout file created with the visual designer. For example:
	Activity.LoadLayout("comment")
	tusername=comment.queryuser
	Dim getmessage As HttpJob
	getmessage.Initialize("getmessage",Me)
    getmessage.Download("https://bottle-bookjnrain.rhcloud.com/getusermessage/"&tusername)
	ProgressDialogShow("获取数据中...")
	ime1.Initialize("IME")
    ime1.AddHeightChangedEvent
	'IME_HeightChanged(100%y, 0) 
	ime1.AddHandleActionEvent(EditText1)
End Sub

Sub Activity_Resume

End Sub

Sub Activity_Pause (UserClosed As Boolean)

End Sub

Sub JobDone (job As HttpJob)
	Log("JobName = " & job.JobName & ", Success = " & job.Success)
	If job.Success = True Then
		Select job.JobName
			Case "getmessage"
				'print the result to the logs
				ProgressDialogHide
				Dim out As OutputStream
				out=File.OpenOutput(File.DirInternal,tusername&"-message.db",False)
                File.Copy2(job.GetInputStream,out)
				out.Close
				createlistview
			Case "Job4"
			    ProgressDialogHide
				ToastMessageShow("留言成功!",False)
			Case "Job1"
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

'以下是Panel2上的ListView
Sub createlistview
	clv1.Initialize(Me, "clv1")
	Panel2.AddView(clv1.AsView, 0, 0, Panel2.Width, Panel2.Height-40dip)
	
	If SQL4.IsInitialized = False Then
	    SQL4.Initialize(File.DirInternal, tusername&"-message.db", False)
	End If
	
	Dim Cursor1 As Cursor
	Cursor1 = SQL4.ExecQuery("SELECT * FROM statics")
	If Cursor1.RowCount=0 Then
	    clv1.Add(nonelistitem(clv1.AsView.Width, 40dip),40dip,0)
	End If
	For i = 0 To Cursor1.RowCount - 1
		Cursor1.Position = i
		
		Dim now As Long
		Dim time As String
	    now = Cursor1.GetString("time")
        time=DateTime.GetYear(now)&"/"&DateTime.GetMonth(now)&"/"&DateTime.GetDayOfMonth(now)&" "&DateTime.GetHour(now)&":"&DateTime.GetMinute(now)
	    Dim ProperHeight As Int
	    ProperHeight=autosize(Cursor1.GetString("words"))+20dip
		Log(ProperHeight)

		clv1.Add(CreateListItem(Cursor1.GetString("words"),Cursor1.GetString("username"),time, clv1.AsView.Width, ProperHeight), ProperHeight, Cursor1.GetString("time"))
	Next
	Cursor1.Close
	SQL4.Close
	Panel2.Visible=True
	clv1.AsView.Visible=True
End Sub

Sub clv1_ItemClick (Index As Int, Value As Object)
	'Msgbox("Hello","Hello")
	If Value<>0 Then
	    Dim r As List 
        r.Initialize 
        r.AddAll(Array As String("查看用户"))
		Dim Reader As TextReader
        Dim username,password As String
	    If File.Exists(File.DirInternal,"user") Then
            Reader.Initialize(File.OpenInput(File.DirInternal, "user"))
		    username = Reader.ReadLine
			password = Reader.ReadLine
            Reader.Close 
		    If username=tusername Then
			    r.Add("删除该留言")
			End If
		End If
		Dim pnl As Panel
	    pnl = clv1.GetPanel(Index)
		Dim lbl2 As Label
	    lbl2 = pnl.GetView(1)
        Dim m As Int
        Dim x As id 
        m = x.InputList1(r,Index)
	
	    Select m
	        Case 0
                comment.queryuser=lbl2.Tag
				StartActivity(userinfo)
			Case 1
			    clv1.RemoveAt(Index)
			    ProgressDialogShow("操作中")
				Dim job1 As HttpJob
                job1.Initialize("Job1",Me)
                job1.PostString("https://bottle-bookjnrain.rhcloud.com/deletemessage/","username="&username&"&password="&password&"&tusername="&tusername&"&time="&Value)
	    End Select
	End If
End Sub


Sub CreateListItem(FirstLineText As String,username As String,time As String, Width As Int, Height As Int) As Panel
	Dim p As Panel
	p.Initialize("")
	p.Color = Colors.RGB(245,245,245)
	
	'Dim b As Button
	'b.Initialize("button") 'all buttons click events will be handled with Sub Button_Click
	'b.Text = "Click"
	'Dim chk As CheckBox
	'chk.Initialize("")
	'p.AddView(b, Width-60dip, 2dip, 20dip, Height-4dip) 'view #1
	'p.AddView(chk, Width-120dip, 2dip, 50dip, Height-4dip) 'view #2
	Dim lbl As Label
	lbl.Initialize("")
	lbl.Gravity = Gravity.LEFT
	lbl.Text = FirstLineText
	lbl.TextSize = 18
	lbl.TextColor = Colors.Black
	
	Dim lbl2 As Label
	lbl2.Initialize("userinfo")
	lbl2.Gravity = Gravity.LEFT
	lbl2.Text = username
	lbl2.Tag = username
	lbl2.TextSize = 16
	lbl2.TextColor = Colors.Gray
	
	Dim lbl3 As Label
	lbl3.Initialize("")
	lbl3.Gravity = Gravity.LEFT
	lbl3.Text = time
	lbl3.TextSize = 16
	lbl3.TextColor = Colors.Gray
	
	Dim cover As ImageView
    cover.Initialize("")
	cover.Gravity=Gravity.FILL
	cover.Bitmap=LoadBitmap(File.DirAssets,"default_avatar.png")
	'获取用户昵称
	Dim links2 As Map
	links2.Initialize
	links2.Put(lbl2, "https://bottle-bookjnrain.rhcloud.com/getusernickname/"&username)
	CallSubDelayed2(nicknameloader, "Download", links2)
	'获取用户头像
	Dim links As Map
	links.Initialize
	links.Put(cover, "https://bottle-bookjnrain.rhcloud.com/getavatar/"&username)
	
	CallSubDelayed2(ImageDownloader, "Download", links)
	p.AddView(lbl, 64dip+10dip, 2dip, Width-74dip, Height-4dip) 'view #0
	p.AddView(lbl2, 64dip+10dip, Height-20dip, Width-144dip, 20dip) 'view #1
	p.AddView(lbl3, Width-144dip, Height-20dip, 144dip, 20dip) 'view #2
	p.AddView(cover, 2dip, 2dip, 64dip, 64dip) 'view #3
	Return p
End Sub

'Let custom list have auto height.
Sub autosize(Text As String) As Int
	Dim example As Label
	example.Initialize("")
	example.Gravity = Bit.OR(Gravity.CENTER_VERTICAL, Gravity.LEFT)
	example.Text = Text
	example.TextSize = 18
	example.TextColor = Colors.White
	Activity.AddView(example,0,0,clv1.AsView.Width-74dip,20dip)
	example.Visible=False
	Dim minHeight,ProperHeight As Int
	minHeight = su.MeasureMultilineTextHeight(example, Text)
	ProperHeight = Max(50dip, minHeight)
	Return ProperHeight 
End Sub


Sub Button1_Click
	Dim Reader As TextReader
    Dim username As String
	If File.Exists(File.DirInternal,"user") Then
        Reader.Initialize(File.OpenInput(File.DirInternal, "user"))
		username = Reader.ReadLine
        Reader.Close 
		ProgressDialogShow("上传中...")
        Dim now As Long
	    now = DateTime.now
	    Dim job4 As HttpJob
        job4.Initialize("Job4",Me)
        job4.PostString("https://bottle-bookjnrain.rhcloud.com/user/message","username="&username&"&tusername="&tusername&"&words="&EditText1.Text&"&time="&now)
	Else
	    ToastMessageShow("请登录！",False)
	End If
End Sub

Sub userinfo_click
	Dim index As Int
	index = clv1.GetItemFromView(Sender)
	Dim pnl As Panel
	pnl = clv1.GetPanel(index)
	Dim lbl2 As Label
	lbl2 = pnl.GetView(1)
	comment.queryuser=lbl2.Tag
    StartActivity(userinfo)
End Sub

Sub IME_HeightChanged(NewHeight As Int, OldHeight As Int)
    EditText1.Top = NewHeight - EditText1.Height
	Button1.Top = NewHeight - Button1.Height
    Panel2.Height = EditText1.Top - Panel2.Top
End Sub

Sub nonelistitem(Width As Int, Height As Int) As Panel
	Dim p As Panel
	p.Initialize("")
	p.Color = Colors.RGB(245,245,245)
	Dim nothing As Label
	nothing.Initialize("nothing")
	nothing.Text="还没有人说话。"
	nothing.Gravity=Bit.OR(Gravity.CENTER_HORIZONTAL,Gravity.CENTER_VERTICAL)
	nothing.TextSize=18
	nothing.TextColor=Colors.Gray
	p.AddView(nothing, 0dip, 0dip, Width, 40dip) 'view #0
	Return p
End Sub