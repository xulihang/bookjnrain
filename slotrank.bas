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
	Dim ListView1 As ListView
	Dim ImageView1 As ImageView
	Dim Label1 As Label
	Dim Button2 As Button
End Sub

Sub Activity_Create(FirstTime As Boolean)
	'Do not forget to load the layout file created with the visual designer. For example:
	Activity.LoadLayout("query")
	Label1.Text="前20名"
	Dim job1 As HttpJob
	job1.Initialize("Job1",Me)
    job1.Download("https://bottle-bookjnrain.rhcloud.com/getslotrank")
	ProgressDialogShow("获取数据中...")
End Sub

Sub Activity_Resume

End Sub

Sub Activity_Pause (UserClosed As Boolean)

End Sub

Sub Button1_Click
    Activity.Finish
End Sub


Sub JobDone (job As HttpJob)
	Log("JobName = " & job.JobName & ", Success = " & job.Success)
	If job.Success = True Then
		Select job.JobName
			Case "Job1"
				'print the result to the logs
				ProgressDialogHide
				loadlist(job.GetString)
		End Select
	Else
	    ProgressDialogHide
		Log("Error: " & job.ErrorMessage)
		ToastMessageShow("Error: " & job.ErrorMessage, True)
	End If
	job.Release
End Sub

Sub loadlist(jsonresult As String)
    Dim Label2 As Label
	Dim Label3 As Label
    Label2 = ListView1.TwoLinesLayout.Label
	Label3 = ListView1.TwoLinesLayout.SecondLabel
    Label2.TextSize = 14
	Label3.TextColor= Colors.Black

	Dim JSON As JSONParser
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
		Dim time As String
		time=map1.Get("time")
		Dim date As String
		date=DateTime.GetYear(time)&"-"&DateTime.GetMonth(time)&"-"&DateTime.GetDayOfMonth(time)&" "&DateTime.GetHour(time)&":"&DateTime.GetMinute(time)&":"&DateTime.GetSecond(time)
		
		ListView1.AddTwoLines2("第"&(i+1)&"名:"&map1.get("username")&" 得分："&map1.get("score"),date,map1.get("username"))
	Next
End Sub

Sub ListView1_ItemClick (Position As Int, Value As Object)
    Dim r As List 
    r.Initialize
	r.Add("显示用户信息")
    Dim m As Int
    Dim x As id 
    m = x.InputList1(r,"排行榜")	
	Select m
	    Case 0
		    comment.queryuser=Value
			StartActivity(userinfo)
	End Select	
End Sub

Sub Button2_Click
	Msgbox("暂不支持","")
End Sub


