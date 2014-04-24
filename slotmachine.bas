Type=Activity
Version=3
@EndOfDesignText@
#Region  Activity Attributes 
	#FullScreen: False
	#IncludeTitle: False
#End Region

'Activity module
Sub Process_Globals
	'These global variables will be declared once when the application starts.
	'These variables can be accessed from all modules.

End Sub

Sub Globals
	'These global variables will be redeclared each time the activity is created.
	'These variables can only be accessed from this module.
    Dim svstep As Int
    Dim wv1,wv2,wv3 As WheelView
    Dim overlay As Panel
	Dim Button1 As Button
	Dim j=1 As Int
	Dim EditText1 As EditText
	Dim Label1 As Label
	Dim Label2 As Label
	Dim Label3 As Label
	Dim Timer1 As Timer
	Dim Label4 As Label
	Dim Label5 As Label
	Dim Label6 As Label
End Sub

Sub Activity_Create(FirstTime As Boolean)
    Activity.LoadLayout("wv")
	Activity.AddMenuItem("返回","back")
    init_wheels
	Label2.Left=wv2.Left
	Label2.top=wv2.top
	Label6.Left=wv2.Left
	Label6.top=wv2.top
    wv1.Enabled=False
	wv2.Enabled=False
	wv3.Enabled=False
	Timer1.Initialize("Timer1", 20)
	Timer1.Enabled=True
End Sub

Sub Activity_Resume

End Sub

Sub Activity_Pause (UserClosed As Boolean)

End Sub

Sub Timer1_Tick
 'Handle tick events
   run
End Sub

Sub run
    If j=0 Then
        Dim num(3) As Int
	    num(0)=Rnd(0,9)
	    num(1)=Rnd(0,9)
	    num(2)=Rnd(0,9)
	    wv1.SetValue(num(0))
		wv2.SetValue(num(1))
		wv3.SetValue(num(2))
	End If
End Sub

Sub init_wheels
svstep = 36dip
wv1.Initialize(svstep,1,9,True,"wv1")
wv2.Initialize(svstep,1,9,True,"wv2") ' using lst as list
'wv2.Initialize2(svstep,val,True,"wv2") ' using val as array
wv3.Initialize(svstep,1,9,False,"wv3")

Activity.AddView(wv1,100dip, 170dip,40dip,svstep*3)
Activity.AddView(wv2,140dip, 170dip,50dip,svstep*3)
Activity.AddView(wv3,190dip, 170dip,70dip,svstep*3)
overlay.Initialize("")
overlay.SetBackgroundImage(LoadBitmap(File.DirAssets,"cover.png"))
Activity.AddView(overlay,100dip,170dip,160dip,svstep*3)
DoEvents
End Sub

Sub wv1_tick

End Sub

Sub wv2_tick

End Sub

Sub wv3_tick

End Sub

Sub Button1_Click
    If EditText1.Text="" OR EditText1.Text<=0 Then
	    Msgbox("必须下注！","")
	Else If Label3.Text-EditText1.Text<0 AND j=1 Then
	    Msgbox("余额不足","")
	Else
	    If j=0 Then
		    '点击stop
			j=1
			EditText1.Enabled=True
		    Button1.Text="Again!"
		    If wv1.ReadWheel=wv2.ReadWheel AND wv2.ReadWheel=wv3.ReadWheel Then
	            congra
	        Else If wv1.ReadWheel=wv2.ReadWheel OR wv2.ReadWheel=wv3.ReadWheel OR wv1.ReadWheel=wv3.ReadWheel Then
	            great
		    Else If Label3.Text<=0 Then
		        ToastMessageShow("Game Over",False)
			    Button1.Enabled=False
			End If
        Else
		    '点击again
			EditText1.Enabled=False
	        j=0
			Label3.Text=Label3.Text-EditText1.Text
		    Button1.Text="Stop!"
		End If
	End If
End Sub

Sub great
    ToastMessageShow("Great!",False)
    Dim a,a2 As Animation
	Log(Label3.Top)
	a.InitializeTranslate("a",0,0,Label3.left-Label2.Left,Label3.top-Label2.Top)
    Label2.Text=EditText1.Text*2
	Label2.Tag=a
	a.Duration = 1000
	a.Start(Label2)
	a2.InitializeTranslate("a2",0,0,Label5.left-Label6.Left,Label5.top-Label6.Top)
    Label6.Text=EditText1.Text*2
	Label6.Tag=a2
	a2.Duration = 1000
	a2.Start(Label6)
	Sleep(1000)
	Label3.Text=Label3.Text+EditText1.Text*2
	Label5.Text=Label5.Text+EditText1.Text*2
	Label2.Text=""
	Label6.Text=""
End Sub

Sub congra
    ToastMessageShow("Congratulations!",False)
    Dim a ,a2 As Animation
	Log(Label3.Top)
	a.InitializeTranslate("a",0,0,Label3.left-Label2.Left,Label3.top-Label2.Top)
    Label2.Text=EditText1.Text*10
	Label2.Tag=a
	a.Duration = 1000
	a.Start(Label2)
	a2.InitializeTranslate("a2",0,0,Label5.left-Label6.Left,Label5.top-Label6.Top)
    Label6.Text=EditText1.Text*10
	Label6.Tag=a2
	a2.Duration = 1000
	a2.Start(Label6)
	Sleep(1000)
	Label3.Text=Label3.Text+EditText1.Text*10
	Label5.Text=Label5.Text+EditText1.Text*10
	Label2.Text=""
	Label6.Text=""
End Sub

Sub Sleep(ms As Long)
   Dim now As Long
   If ms > 1000 Then ms =1000   'avoid application not responding error
   now=DateTime.now
   Do Until (DateTime.now>now+ms)
     DoEvents
   Loop
End Sub

Sub back_click
    Timer1.Enabled=False
    Activity.Finish
End Sub

Sub Activity_KeyPress (KeyCode As Int) As Boolean 'Return True to consume the event
	If KeyCode=KeyCodes.KEYCODE_BACK Then
	    Timer1.Enabled=False
		ToastMessageShow("再见",False)
		Activity.Finish
		Return True
	End If
End Sub