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
    Dim searchtime As String
End Sub

Sub Globals
	'These global variables will be redeclared each time the activity is created.
	'These variables can only be accessed from this module.

	Dim Button1 As Button
	Dim ImageView1 As ImageView
	Dim Label1 As Label
	Dim val(12) As String
    Dim svstep As Int
    Dim wv1,wv2,wv3 As WheelView
    Dim overlay As Panel
    Dim result As Label
End Sub

Sub Activity_Create(FirstTime As Boolean)
	'Do not forget to load the layout file created with the visual designer. For example:
	Activity.LoadLayout("wheelview")
    init_label
    init_wheels
    show_today
End Sub

Sub Activity_Resume

End Sub

Sub Activity_Pause (UserClosed As Boolean)

End Sub


Sub Button1_Click
	searchtime=wv3.ReadWheel & wv2.ReadWheel & wv1.ReadWheel
	Activity.Finish
End Sub

Sub init_label
result.Initialize("")
result.TextSize = 24
result.Gravity = Gravity.CENTER_HORIZONTAL
Activity.AddView(result,100dip,100dip,150dip,40dip)
End Sub

Sub init_wheels
Dim lst As List
lst.Initialize
val(0) = "01"
val(1) = "02"
val(2) = "03"
val(3) = "04"
val(4) = "05"
val(5) = "06"
val(6) = "07"
val(7) = "08"
val(8) = "09"
val(9) = "10"
val(10) = "11"
val(11) = "12"

For i = 0 To 11
	lst.Add(val(i))
Next
svstep = 36dip
wv1.Initialize(svstep,1,31,True,"wv1")
wv2.Initialize1(svstep,lst,True,"wv2") ' using lst as list
'wv2.Initialize2(svstep,val,True,"wv2") ' using val as array
wv3.Initialize(svstep,2000,2030,False,"wv3")

Activity.AddView(wv1,100dip, 170dip,40dip,svstep*3)
Activity.AddView(wv2,140dip, 170dip,50dip,svstep*3)
Activity.AddView(wv3,190dip, 170dip,70dip,svstep*3)
overlay.Initialize("")
overlay.SetBackgroundImage(LoadBitmap(File.DirAssets,"cover.png"))
Activity.AddView(overlay,100dip,170dip,160dip,svstep*3)
DoEvents
End Sub

Sub show_today
wv1.SetValue( DateTime.GetDayOfMonth(DateTime.Now))
wv2.SetValue( DateTime.GetMonth(DateTime.Now))
wv3.SetValue(DateTime.Getyear(DateTime.Now)-2000)
End Sub

Sub wv1_tick
show_result
End Sub

Sub wv2_tick
show_result
End Sub

Sub wv3_tick
show_result
End Sub

Sub show_result
result.Text =   wv1.ReadWheel & " " & wv2.ReadWheel & "  " & wv3.ReadWheel
End Sub