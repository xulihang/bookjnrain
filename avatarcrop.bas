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
    Dim CC As ContentChooser
End Sub

Sub Globals
	'These global variables will be redeclared each time the activity is created.
	'These variables can only be accessed from this module.
	Dim ImageView1 As ImageView
	Dim IO As RSImageProcessing
	Dim btmpFlower, cut, resize As Bitmap
	'Dim jpg As Jpeg
	Dim SeekBar1 As SeekBar
	Dim SeekBar2 As SeekBar
	Dim SeekBar3 As SeekBar
	Dim SeekBar4 As SeekBar
	Dim Panel1 As Panel
	Dim scale As Float
	Dim Hscale As Float
	Dim Wscale As Float
	Dim Button1 As Button
	Dim Button2 As Button
	Dim SeekBar5 As SeekBar
	Dim SeekBar6 As SeekBar
End Sub

Sub Activity_Create(FirstTime As Boolean)
	'Do not forget to load the layout file created with the visual designer. For example:
	Activity.LoadLayout("crop.bal")
	IO.Initialize
	scale = GetDeviceLayoutValues.scale
	Activity.AddMenuItem("选图片","choose")
	Activity.AddMenuItem("剪裁","crop")

	btmpFlower.Initialize(File.DirAssets,"image4.jpg")
	loadimage(File.DirAssets,"image4.jpg")
	Log(scale)
	Log(ImageView1.Width)
	Log(ImageView1.Height)
	SeekBar5.Max=Activity.Height*scale*2
	SeekBar6.Max=Activity.Width*scale*2
	SeekBar5.Value=Activity.Height*scale
	SeekBar6.Value=Activity.Width*scale
	ImageView1.Left=0
	ImageView1.Top=0
	SeekBar4.Enabled=False
	ToastMessageShow("使用滚动条选择裁剪范围。按MENU键选择操作。",False)
End Sub

Sub Activity_Resume

End Sub

Sub Activity_Pause (UserClosed As Boolean)

End Sub

Sub loadimage(Dir As String,FileName As String)
	ImageView1.Bitmap = btmpFlower
    'jpg.Initialize("")
	'jpg.LoadJpegSizeOnly(File.OpenInput(Dir,FileName))
	'Log(jpg.JpegHeight)
	'Log(jpg.JpegWidth)
	ImageView1.Height=btmpFlower.Height/btmpFlower.Width*ImageView1.Width '约束比例
	Hscale=btmpFlower.Height/ImageView1.Height/scale
	Wscale=btmpFlower.Width/ImageView1.Width/scale
	SeekBar1.Max=ImageView1.Height*scale '上 乘Scale以转换到真实像素
	SeekBar2.Max=ImageView1.Width*scale  '左
	SeekBar3.Max=ImageView1.Height*scale '高
	SeekBar4.Max=ImageView1.Width*scale  '宽
	SeekBar1.Value=0
	SeekBar2.Value=0
	SeekBar3.Value=30
	SeekBar4.Value=30
End Sub
Sub choose_Click
    ToastMessageShow("请选择一张图片.",False)
    CC.Initialize("CC") 
    CC.Show("image/*", "Choose image")
	
End Sub

Sub crop_Click
    Log(btmpFlower.Width)
	Log(btmpFlower.Height)
	Log(SeekBar4.Value*Wscale)
	Log(SeekBar3.Value*Hscale)
    If SeekBar2.Value*Wscale+SeekBar4.Value*Wscale>btmpFlower.Width OR SeekBar1.Value*Hscale+SeekBar3.Value*Hscale>btmpFlower.Height Then
	    Msgbox("超出边界。","警告")
	Else	
        cut=IO.extractBitmap(btmpFlower,SeekBar2.Value*Wscale,SeekBar1.Value*Hscale,SeekBar4.Value*Wscale,SeekBar3.Value*Hscale)
	    Log(cut.Width)
	    Log(cut.Height)
	    ImageView1.Bitmap = cut
	    ImageView1.Height=cut.Height/cut.Width*ImageView1.Width '约束比例
	    rsimage
	End If
End Sub

Sub rsimage
    resize=IO.createScaledBitmap(cut,128,128,False)
	IO.writeBitmapToFile(resize,File.DirInternal,"avatar.jpg",60)
	ToastMessageShow("修改成功！。",False)
	Activity.Finish
End Sub

Sub cc_Result (Success As Boolean, Dir As String, FileName As String)
    If Success Then
	    'Log(Dir)
		'Log(FileName)
	    'ToastMessageShow(Dir&FileName,False)
		'File.Copy(Dir,FileName,"/sdcard","out11.jpg")
		btmpFlower.Initialize(Dir,FileName)
		loadimage(Dir,FileName)
    End If
End Sub

Sub SeekBar4_ValueChanged (Value As Int, UserChanged As Boolean)
    If Panel1.Left+Panel1.Width>ImageView1.Left+ImageView1.Width Then
	    SeekBar4.Value=0
		ToastMessageShow("超出边界",False)
	End If
	Panel1.Width=SeekBar4.Value/scale
End Sub
Sub SeekBar3_ValueChanged (Value As Int, UserChanged As Boolean)
    If Panel1.Top+Panel1.Height>ImageView1.Top+ImageView1.Height Then
	    SeekBar3.Value=0
		ToastMessageShow("超出边界",False)
	End If
	If Panel1.Left+Panel1.Width>ImageView1.Left+ImageView1.Width Then
	    SeekBar3.Value=0
		ToastMessageShow("超出边界",False)
	End If
	SeekBar4.Value=SeekBar3.Value
	Panel1.Height=SeekBar3.Value/scale
End Sub
Sub SeekBar2_ValueChanged (Value As Int, UserChanged As Boolean)
    If Panel1.Left+Panel1.Width>ImageView1.Left+ImageView1.Width Then
	    SeekBar2.Value=0
		ToastMessageShow("超出边界",False)
	End If
	Panel1.Left=ImageView1.Left+SeekBar2.Value/scale
End Sub
Sub SeekBar1_ValueChanged (Value As Int, UserChanged As Boolean)
    If Panel1.Top+Panel1.Height>ImageView1.Top+ImageView1.Height Then
	    SeekBar1.Value=0
		ToastMessageShow("超出边界",False)
	End If
	Panel1.Top=ImageView1.Top+SeekBar1.Value/scale
End Sub
Sub SeekBar6_ValueChanged (Value As Int, UserChanged As Boolean)
	ImageView1.Left=(Value-Activity.Width*scale)/scale
	Panel1.Left=ImageView1.Left
End Sub
Sub SeekBar5_ValueChanged (Value As Int, UserChanged As Boolean)
	ImageView1.Top=(Value-Activity.Height*scale)/scale
	Panel1.Top=ImageView1.Top
End Sub
Sub Button2_Click
	ImageView1.Width=ImageView1.Width/1.2
	ImageView1.Height=ImageView1.Height/1.2
	SeekBar1.Max=SeekBar1.Max/1.2
	SeekBar2.Max=SeekBar2.Max/1.2
	SeekBar3.Max=SeekBar3.Max/1.2
	SeekBar4.Max=SeekBar4.Max/1.2
	Hscale=btmpFlower.Height/ImageView1.Height/scale
	Wscale=btmpFlower.Width/ImageView1.Width/scale
End Sub
Sub Button1_Click
	ImageView1.Width=ImageView1.Width*1.2
	ImageView1.Height=ImageView1.Height*1.2
	SeekBar1.Max=SeekBar1.Max*1.2
	SeekBar2.Max=SeekBar2.Max*1.2
	SeekBar3.Max=SeekBar3.Max*1.2
	SeekBar4.Max=SeekBar4.Max*1.2
	Hscale=btmpFlower.Height/ImageView1.Height/scale
	Wscale=btmpFlower.Width/ImageView1.Width/scale
End Sub
