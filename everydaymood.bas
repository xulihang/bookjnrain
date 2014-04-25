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
	Dim Panel2 As Panel
	Dim clv1 As CustomListView
	Dim page2=1 As String
	Dim su As StringUtils
	Dim Button2 As Button
End Sub

Sub Activity_Create(FirstTime As Boolean)
	'Do not forget to load the layout file created with the visual designer. For example:
	Activity.LoadLayout("everydaymood")
	ProgressDialogShow("加载中")
	Dim get As HttpJob
	get.Initialize("get",Me)
	get.PostString("https://bottle-bookjnrain.rhcloud.com/getmoodjson","itemnumber="&Main.manager.GetString("list1")&"&page="&page2)
End Sub

Sub Activity_Resume

End Sub

Sub Activity_Pause (UserClosed As Boolean)

End Sub

Sub createlistview(jsonresult As String)
    If clv1.IsInitialized=True Then
	    clv1.AsView.RemoveView
	End If
	
	clv1.Initialize(Me, "clv1")

	Panel2.AddView(clv1.AsView, 0, 0, Panel2.Width, Panel2.Height-40dip)
    Dim JSON As JSONParser
    'JSON.Initialize(File.ReadString(File.DirAssets, "out.json")) 'Read the text from a file.
	JSON.Initialize(jsonresult)
    Dim list1 As List
    list1 = JSON.NextArray
	If list1.Size=0 Then
		ToastMessageShow("没有了",False)
	End If
	Log("size:"&list1.Size)
	For i =0 To list1.Size-1
	    Dim map1 As Map
        map1 = list1.get(i)
		
		Dim ProperHeight As Int
	    ProperHeight=autosize(map1.get("words"))+20dip
		Log(ProperHeight)

		clv1.Add(CreateListItem(map1.get("username"),map1.get("time"),map1.get("mood"),map1.get("words"), clv1.AsView.Width, ProperHeight), ProperHeight, map1.get("username"))

		If i=list1.Size-1 Then
		    clv1.Add(morelistitem(clv1.AsView.Width, 40dip),40dip,0)
		End If
	Next

	clv1.AsView.Visible=True

End Sub

Sub clv1_ItemClick (Index As Int, Value As Object)
	'Msgbox("Hello","Hello")
	If Value=0 Then
	    more_click	
	Else
	    Dim r As List 
        r.Initialize 
        r.AddAll(Array As String("查看用户"))
        Dim m As Int
        Dim x As id 
        m = x.InputList1(r,Index)
	
	    Select m
	        Case 0
                comment.queryuser=Value
				StartActivity(userinfo)
	    End Select		
	End If
End Sub

Sub CreateListItem(username As String, time As String, mood As String, words As String, Width As Int, Height As Int) As Panel
	Dim p As Panel
	p.Initialize("")
	p.Color = Colors.RGB(245,245,245)
	
	Dim lbl As Label
	lbl.Initialize("")
	lbl.Gravity = Gravity.LEFT
	lbl.Text = words
	lbl.TextSize = 18
	lbl.TextColor = Colors.Black
	
	Dim lbl2 As Label
	lbl2.Initialize("")
	lbl2.Gravity = Gravity.LEFT
	lbl2.Text = username
	lbl2.Tag = username
	lbl2.TextSize = 16
	lbl2.TextColor = Colors.Gray
	
	Dim date As String
	date=DateTime.GetYear(time)&"-"&DateTime.GetMonth(time)&"-"&DateTime.GetDayOfMonth(time)
	
	Dim lbl3 As Label
	lbl3.Initialize("")
	lbl3.Gravity = Gravity.RIGHT
	lbl3.Text = date
	lbl3.TextSize = 16
	lbl3.TextColor = Colors.Gray
		
	Dim gifpath As String
	Select mood
	    Case 0
		    gifpath="kx.gif"
		Case 1
		    gifpath="ng.gif"
		Case 2
		    gifpath="ym.gif"
		Case 3
		    gifpath="wl.gif"
		Case 4
		    gifpath="nu.gif"
		Case 5
		    gifpath="ch.gif"
		Case 6
		    gifpath="fd.gif"
		Case 7
		    gifpath="yl.gif"
		Case 8
		    gifpath="shuai.gif"
	End Select
	Dim wv As WebView
    wv.Initialize("")
    wv.LoadHtml("<html><body><img src='file:///android_asset/biaoqing/"&gifpath&"'/></body></html>")
	
	'获取用户昵称
	Dim links2 As Map
	links2.Initialize
	links2.Put(lbl2, "https://bottle-bookjnrain.rhcloud.com/getusernickname/"&username)
	CallSubDelayed2(nicknameloader, "Download", links2)
	
	p.AddView(lbl, 45dip, 2dip, Width-40dip, Height-20dip) 'view #0
    p.AddView(lbl2, 5dip, Height-20dip, Width-160dip, 20dip) 'view #1
	p.AddView(lbl3, Width-80dip,Height-20dip, 80dip, 20dip) 'view #2
	p.AddView(wv, 5dip, 2dip, 40dip, 40dip) 'view #3

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
	Activity.AddView(example,0,0,clv1.AsView.Width-40dip,20dip)
	example.Visible=False
	Dim minHeight,ProperHeight As Int
	minHeight = su.MeasureMultilineTextHeight(example, Text)
	ProperHeight = Max(50dip, minHeight)
	Return ProperHeight 
End Sub

Sub addlistview(jsonresult As String)
    Dim JSON As JSONParser
    'JSON.Initialize(File.ReadString(File.DirAssets, "out.json")) 'Read the text from a file.
	JSON.Initialize(jsonresult)
    Dim list1 As List
    list1 = JSON.NextArray
	If list1.Size=0 Then
		ToastMessageShow("没有了",False)
	End If
	Log("size:"&list1.Size)
	For i =0 To list1.Size-1
	    Dim map1 As Map
        map1 = list1.get(i)
		
		Dim ProperHeight As Int
	    ProperHeight=autosize(map1.get("words"))+20dip
		Log(ProperHeight)
        
		clv1.Add(CreateListItem(map1.get("username"),map1.get("time"),map1.get("mood"),map1.get("words"), clv1.AsView.Width, ProperHeight), ProperHeight, map1.get("username"))

		If i=list1.Size-1 Then
		    clv1.Add(morelistitem(clv1.AsView.Width, 40dip),40dip,0)
		End If
	Next

	clv1.AsView.Visible=True
End Sub

Sub Button1_Click
	StartActivity(pubmood)
End Sub

Sub more_click
    page2=page2+1
	'clv1.AsView.RemoveView
	clv1.RemoveAt(clv1.GetSize-1)
	ProgressDialogShow("加载中")

	Dim loadmore As HttpJob
	loadmore.Initialize("loadmore",Me)
	loadmore.PostString("https://bottle-bookjnrain.rhcloud.com/getmoodjson","itemnumber="&Main.manager.GetString("list1")&"&page="&page2)
    'addlistview(queryorder,page2-1)
End Sub

Sub morelistitem(Width As Int, Height As Int) As Panel
	Dim p As Panel
	p.Initialize("")
	p.Color = Colors.RGB(245,245,245)
	Dim more As Label
	more.Initialize("more")
	more.Text="点击加载更多"
	more.Gravity=Bit.OR(Gravity.CENTER_HORIZONTAL,Gravity.CENTER_VERTICAL)
	more.TextSize=18
	more.TextColor=Colors.Gray
	p.AddView(more, 0dip, 0dip, Width, 40dip) 'view #0
	Return p
End Sub

Sub JobDone (job As HttpJob)
	Log("JobName = " & job.JobName & ", Success = " & job.Success)
	If job.Success = True Then
		Select job.JobName
			Case "get"
				'print the result to the logs
				ProgressDialogHide
				createlistview(job.GetString)
			Case "loadmore"  
				ProgressDialogHide
				addlistview(job.GetString)
		End Select
	Else
	    ProgressDialogHide
		Log("Error: " & job.ErrorMessage)
		ToastMessageShow("Error: " & job.ErrorMessage, True)
	End If
	job.Release
End Sub
Sub Button2_Click
    ProgressDialogShow("更新中")
	Dim get As HttpJob
	get.Initialize("get",Me)
	get.PostString("https://bottle-bookjnrain.rhcloud.com/getmoodjson","itemnumber="&Main.manager.GetString("list1")&"&page="&page2)
End Sub