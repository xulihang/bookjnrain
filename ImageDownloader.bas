Type=Service
Version=3
@EndOfDesignText@
#Region  Service Attributes 
	#StartAtBoot: False
#End Region

Sub Process_Globals
	Private cache As Map
	Private tasks As Map
	Private ongoingTasks As Map
End Sub

Sub Service_Create
	tasks.Initialize
	cache.Initialize
	ongoingTasks.Initialize
End Sub

Sub Service_Start (StartingIntent As Intent)

End Sub

Sub Service_Destroy

End Sub

Sub DownloadAvatar (ImageViewsMap As Map)
	For i = 0 To ImageViewsMap.Size - 1
	    Dim headlink As String
		headlink="https://bottle-bookjnrain.rhcloud.com/getavatar/"
		Dim tail As String
		tail=ImageViewsMap.GetValueAt(i)
		tasks.Put(ImageViewsMap.GetKeyAt(i), tail)
		Dim link As String = headlink&ImageViewsMap.GetValueAt(i)
		
		If cache.ContainsKey(tail) Then
		    If File.Exists(File.DirDefaultExternal,tail&".jpg") AND File.Size(File.DirDefaultExternal,tail&".jpg")>0 Then
			    Dim bmp As Bitmap = LoadBitmap(File.DirDefaultExternal,tail&".jpg")
			    Dim iv As ImageView = ImageViewsMap.GetKeyAt(i)
			    'iv.SetBackgroundImage(cache.Get(link))
		        iv.SetBackgroundImage(bmp)
			End If
		Else If ongoingTasks.ContainsKey(tail) = False Then
			ongoingTasks.Put(tail, "")
			Dim j As HttpJob
			j.Initialize(tail, Me)
			j.Download(link)
		End If
    Next
End Sub

Sub DownloadCover (ImageViewsMap As Map)
	For i = 0 To ImageViewsMap.Size - 1
	    Dim headlink As String
		headlink="https://bottle-bookjnrain.rhcloud.com/getimage/"
		Dim tail As String
		tail=ImageViewsMap.GetValueAt(i)
		tasks.Put(ImageViewsMap.GetKeyAt(i), tail)
		Dim link As String = headlink&ImageViewsMap.GetValueAt(i)
		'If cache.ContainsKey(link) Then
		If File.Exists(File.DirDefaultExternal,tail&".jpg") AND File.Size(File.DirDefaultExternal,tail&".jpg")>0  Then
			Dim bmp As Bitmap = LoadBitmap(File.DirDefaultExternal,tail&".jpg")
			Dim iv As ImageView = ImageViewsMap.GetKeyAt(i)
			    'iv.SetBackgroundImage(cache.Get(link))
		    iv.SetBackgroundImage(bmp)
		Else If ongoingTasks.ContainsKey(tail) = False Then
			ongoingTasks.Put(tail, "")
			Dim j As HttpJob
			j.Initialize(tail, Me)
			j.Download(link)
		End If
	Next
End Sub

Sub JobDone(Job As HttpJob)
	ongoingTasks.Remove(Job.JobName)
	If Job.Success Then
		cache.Put(Job.JobName, Job.JobName) 'Pay attention
		Dim out As OutputStream
		out=File.OpenOutput(File.DirDefaultExternal,Job.JobName&".jpg",False)
        File.Copy2(Job.GetInputStream,out)
		out.Close
		Dim bmp As Bitmap = LoadBitmap(File.DirDefaultExternal,Job.JobName&".jpg")
		If tasks.IsInitialized Then
			For i = 0 To tasks.Size - 1
				Dim link As String = tasks.GetValueAt(i)
				If link = Job.JobName Then
					Dim iv As ImageView = tasks.GetKeyAt(i)
					iv.SetBackgroundImage(bmp)
				End If
			Next
		End If
	Else
		Log("Error downloading image: " & Job.JobName & CRLF & Job.ErrorMessage)
	End If
	Job.Release
End Sub
Sub ActivityIsPaused
	tasks.Clear
End Sub
