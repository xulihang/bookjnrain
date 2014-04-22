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

Sub Download (nickname As Map)
	For i = 0 To nickname.Size - 1
	    'Log(nickname.GetKeyAt(i))
		tasks.Put(nickname.GetKeyAt(i), nickname.GetValueAt(i))
		Dim link As String = nickname.GetValueAt(i)
		If cache.ContainsKey(link) Then
			Dim iv As Label = nickname.GetKeyAt(i)
			iv.Text="上传者:"&cache.Get(link)
		Else If ongoingTasks.ContainsKey(link) = False Then
			ongoingTasks.Put(link, "")
			Dim j As HttpJob
			j.Initialize(link, Me)
			j.Download(link)
		End If
	Next
End Sub

Sub JobDone(Job As HttpJob)
	ongoingTasks.Remove(Job.JobName)
	If Job.Success Then
	    Log(Job.GetString)
		Dim name As String = Job.GetString
		cache.Put(Job.JobName,name)
		If tasks.IsInitialized Then
			For i = 0 To tasks.Size - 1
				Dim link As String = tasks.GetValueAt(i)
				If link = Job.JobName Then
					Dim lbl As Label = tasks.GetKeyAt(i)
					lbl.Text="上传者:"&name
				End If
			Next
		End If
	Else
		Log("Error downloading: " & Job.JobName & CRLF & Job.ErrorMessage)
	End If
	Job.Release
End Sub
Sub ActivityIsPaused
	tasks.Clear
End Sub
