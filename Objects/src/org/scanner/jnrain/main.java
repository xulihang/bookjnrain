package org.scanner.jnrain;

import anywheresoftware.b4a.B4AMenuItem;
import android.app.Activity;
import android.os.Bundle;
import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.BALayout;
import anywheresoftware.b4a.B4AActivity;
import anywheresoftware.b4a.ObjectWrapper;
import anywheresoftware.b4a.objects.ActivityWrapper;
import java.lang.reflect.InvocationTargetException;
import anywheresoftware.b4a.B4AUncaughtException;
import anywheresoftware.b4a.debug.*;
import java.lang.ref.WeakReference;

public class main extends Activity implements B4AActivity{
	public static main mostCurrent;
	static boolean afterFirstLayout;
	static boolean isFirst = true;
    private static boolean processGlobalsRun = false;
	BALayout layout;
	public static BA processBA;
	BA activityBA;
    ActivityWrapper _activity;
    java.util.ArrayList<B4AMenuItem> menuItems;
	public static final boolean fullScreen = false;
	public static final boolean includeTitle = false;
    public static WeakReference<Activity> previousOne;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (isFirst) {
			processBA = new BA(this.getApplicationContext(), null, null, "org.scanner.jnrain", "org.scanner.jnrain.main");
			processBA.loadHtSubs(this.getClass());
	        float deviceScale = getApplicationContext().getResources().getDisplayMetrics().density;
	        BALayout.setDeviceScale(deviceScale);
            
		}
		else if (previousOne != null) {
			Activity p = previousOne.get();
			if (p != null && p != this) {
                BA.LogInfo("Killing previous instance (main).");
				p.finish();
			}
		}
		if (!includeTitle) {
        	this.getWindow().requestFeature(android.view.Window.FEATURE_NO_TITLE);
        }
        if (fullScreen) {
        	getWindow().setFlags(android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN,   
        			android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
		mostCurrent = this;
        processBA.sharedProcessBA.activityBA = null;
		layout = new BALayout(this);
		setContentView(layout);
		afterFirstLayout = false;
		BA.handler.postDelayed(new WaitForLayout(), 5);

	}
	private static class WaitForLayout implements Runnable {
		public void run() {
			if (afterFirstLayout)
				return;
			if (mostCurrent == null)
				return;
            
			if (mostCurrent.layout.getWidth() == 0) {
				BA.handler.postDelayed(this, 5);
				return;
			}
			mostCurrent.layout.getLayoutParams().height = mostCurrent.layout.getHeight();
			mostCurrent.layout.getLayoutParams().width = mostCurrent.layout.getWidth();
			afterFirstLayout = true;
			mostCurrent.afterFirstLayout();
		}
	}
	private void afterFirstLayout() {
        if (this != mostCurrent)
			return;
		activityBA = new BA(this, layout, processBA, "org.scanner.jnrain", "org.scanner.jnrain.main");
        
        processBA.sharedProcessBA.activityBA = new java.lang.ref.WeakReference<BA>(activityBA);
        anywheresoftware.b4a.objects.ViewWrapper.lastId = 0;
        _activity = new ActivityWrapper(activityBA, "activity");
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (BA.shellMode) {
			if (isFirst)
				processBA.raiseEvent2(null, true, "SHELL", false);
			processBA.raiseEvent2(null, true, "CREATE", true, "org.scanner.jnrain.main", processBA, activityBA, _activity, anywheresoftware.b4a.keywords.Common.Density);
			_activity.reinitializeForShell(activityBA, "activity");
		}
        initializeProcessGlobals();		
        initializeGlobals();
        
        BA.LogInfo("** Activity (main) Create, isFirst = " + isFirst + " **");
        processBA.raiseEvent2(null, true, "activity_create", false, isFirst);
		isFirst = false;
		if (this != mostCurrent)
			return;
        processBA.setActivityPaused(false);
        BA.LogInfo("** Activity (main) Resume **");
        processBA.raiseEvent(null, "activity_resume");
        if (android.os.Build.VERSION.SDK_INT >= 11) {
			try {
				android.app.Activity.class.getMethod("invalidateOptionsMenu").invoke(this,(Object[]) null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
	public void addMenuItem(B4AMenuItem item) {
		if (menuItems == null)
			menuItems = new java.util.ArrayList<B4AMenuItem>();
		menuItems.add(item);
	}
	@Override
	public boolean onCreateOptionsMenu(android.view.Menu menu) {
		super.onCreateOptionsMenu(menu);
		if (menuItems == null)
			return false;
		for (B4AMenuItem bmi : menuItems) {
			android.view.MenuItem mi = menu.add(bmi.title);
			if (bmi.drawable != null)
				mi.setIcon(bmi.drawable);
            if (android.os.Build.VERSION.SDK_INT >= 11) {
				try {
                    if (bmi.addToBar) {
				        android.view.MenuItem.class.getMethod("setShowAsAction", int.class).invoke(mi, 1);
                    }
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			mi.setOnMenuItemClickListener(new B4AMenuItemsClickListener(bmi.eventName.toLowerCase(BA.cul)));
		}
		return true;
	}
    public void onWindowFocusChanged(boolean hasFocus) {
       super.onWindowFocusChanged(hasFocus);
       if (processBA.subExists("activity_windowfocuschanged"))
           processBA.raiseEvent2(null, true, "activity_windowfocuschanged", false, hasFocus);
    }
	private class B4AMenuItemsClickListener implements android.view.MenuItem.OnMenuItemClickListener {
		private final String eventName;
		public B4AMenuItemsClickListener(String eventName) {
			this.eventName = eventName;
		}
		public boolean onMenuItemClick(android.view.MenuItem item) {
			processBA.raiseEvent(item.getTitle(), eventName + "_click");
			return true;
		}
	}
    public static Class<?> getObject() {
		return main.class;
	}
    private Boolean onKeySubExist = null;
    private Boolean onKeyUpSubExist = null;
	@Override
	public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
		if (onKeySubExist == null)
			onKeySubExist = processBA.subExists("activity_keypress");
		if (onKeySubExist) {
			if (keyCode == anywheresoftware.b4a.keywords.constants.KeyCodes.KEYCODE_BACK &&
					android.os.Build.VERSION.SDK_INT >= 18) {
				HandleKeyDelayed hk = new HandleKeyDelayed();
				hk.kc = keyCode;
				BA.handler.post(hk);
				return true;
			}
			else {
				boolean res = new HandleKeyDelayed().runDirectly(keyCode);
				if (res)
					return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	private class HandleKeyDelayed implements Runnable {
		int kc;
		public void run() {
			runDirectly(kc);
		}
		public boolean runDirectly(int keyCode) {
			Boolean res =  (Boolean)processBA.raiseEvent2(_activity, false, "activity_keypress", false, keyCode);
			if (res == null || res == true) {
                return true;
            }
            else if (keyCode == anywheresoftware.b4a.keywords.constants.KeyCodes.KEYCODE_BACK) {
				finish();
				return true;
			}
            return false;
		}
		
	}
    @Override
	public boolean onKeyUp(int keyCode, android.view.KeyEvent event) {
		if (onKeyUpSubExist == null)
			onKeyUpSubExist = processBA.subExists("activity_keyup");
		if (onKeyUpSubExist) {
			Boolean res =  (Boolean)processBA.raiseEvent2(_activity, false, "activity_keyup", false, keyCode);
			if (res == null || res == true)
				return true;
		}
		return super.onKeyUp(keyCode, event);
	}
	@Override
	public void onNewIntent(android.content.Intent intent) {
		this.setIntent(intent);
	}
    @Override 
	public void onPause() {
		super.onPause();
        if (_activity == null) //workaround for emulator bug (Issue 2423)
            return;
		anywheresoftware.b4a.Msgbox.dismiss(true);
        BA.LogInfo("** Activity (main) Pause, UserClosed = " + activityBA.activity.isFinishing() + " **");
        processBA.raiseEvent2(_activity, true, "activity_pause", false, activityBA.activity.isFinishing());		
        processBA.setActivityPaused(true);
        mostCurrent = null;
        if (!activityBA.activity.isFinishing())
			previousOne = new WeakReference<Activity>(this);
        anywheresoftware.b4a.Msgbox.isDismissing = false;
	}

	@Override
	public void onDestroy() {
        super.onDestroy();
		previousOne = null;
	}
    @Override 
	public void onResume() {
		super.onResume();
        mostCurrent = this;
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (activityBA != null) { //will be null during activity create (which waits for AfterLayout).
        	ResumeMessage rm = new ResumeMessage(mostCurrent);
        	BA.handler.post(rm);
        }
	}
    private static class ResumeMessage implements Runnable {
    	private final WeakReference<Activity> activity;
    	public ResumeMessage(Activity activity) {
    		this.activity = new WeakReference<Activity>(activity);
    	}
		public void run() {
			if (mostCurrent == null || mostCurrent != activity.get())
				return;
			processBA.setActivityPaused(false);
            BA.LogInfo("** Activity (main) Resume **");
		    processBA.raiseEvent(mostCurrent._activity, "activity_resume", (Object[])null);
		}
    }
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
	      android.content.Intent data) {
		processBA.onActivityResult(requestCode, resultCode, data);
	}
	private static void initializeGlobals() {
		processBA.raiseEvent2(null, true, "globals", false, (Object[])null);
	}

public anywheresoftware.b4a.keywords.Common __c = null;
public static String _result = "";
public static anywheresoftware.b4a.http.HttpClientWrapper _httpclient1 = null;
public ice.zxing.b4aZXingLib _zx = null;
public anywheresoftware.b4a.objects.ButtonWrapper _button1 = null;
public anywheresoftware.b4a.objects.LabelWrapper _label1 = null;
public anywheresoftware.b4a.objects.LabelWrapper _label2 = null;
public static String _bookname = "";
public static String _bookprice = "";
public static String _bookpublisher = "";
public anywheresoftware.b4a.objects.ButtonWrapper _button2 = null;
public anywheresoftware.b4a.objects.ImageViewWrapper _imageview1 = null;
public anywheresoftware.b4a.objects.LabelWrapper _label3 = null;
public static String _sjsondata = "";
public static String _msg = "";
public static String _bookpubdate = "";
public anywheresoftware.b4a.samples.httputils2.httpjob _job1 = null;
public anywheresoftware.b4a.objects.ButtonWrapper _button3 = null;
public anywheresoftware.b4a.samples.httputils2.httputils2service _httputils2service = null;
public org.scanner.jnrain.about _about = null;
public org.scanner.jnrain.web _web = null;

public static boolean isAnyActivityVisible() {
    boolean vis = false;
vis = vis | (main.mostCurrent != null);
vis = vis | (about.mostCurrent != null);
vis = vis | (web.mostCurrent != null);
return vis;}
public static String  _about_click() throws Exception{
 //BA.debugLineNum = 154;BA.debugLine="Sub about_Click";
 //BA.debugLineNum = 155;BA.debugLine="StartActivity(about)";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(mostCurrent._about.getObject()));
 //BA.debugLineNum = 156;BA.debugLine="End Sub";
return "";
}
public static String  _activity_create(boolean _firsttime) throws Exception{
 //BA.debugLineNum = 39;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 41;BA.debugLine="HttpClient1.Initialize(\"HttpClient1\")";
_httpclient1.Initialize("HttpClient1");
 //BA.debugLineNum = 42;BA.debugLine="Activity.LoadLayout(\"scanner\")";
mostCurrent._activity.LoadLayout("scanner",mostCurrent.activityBA);
 //BA.debugLineNum = 43;BA.debugLine="Activity.AddMenuItem(\"在线工具\",\"web\")";
mostCurrent._activity.AddMenuItem("在线工具","web");
 //BA.debugLineNum = 44;BA.debugLine="Activity.AddMenuItem(\"复制到剪贴板\",\"cp\")";
mostCurrent._activity.AddMenuItem("复制到剪贴板","cp");
 //BA.debugLineNum = 45;BA.debugLine="Activity.AddMenuItem(\"上传到服务器\",\"upload\")";
mostCurrent._activity.AddMenuItem("上传到服务器","upload");
 //BA.debugLineNum = 46;BA.debugLine="Activity.AddMenuItem(\"关于\",\"about\")";
mostCurrent._activity.AddMenuItem("关于","about");
 //BA.debugLineNum = 47;BA.debugLine="Activity.AddMenuItem(\"退出\",\"Quit\")";
mostCurrent._activity.AddMenuItem("退出","Quit");
 //BA.debugLineNum = 49;BA.debugLine="Button2.Enabled=False";
mostCurrent._button2.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 50;BA.debugLine="Button3.Enabled=False";
mostCurrent._button3.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 51;BA.debugLine="End Sub";
return "";
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 64;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
 //BA.debugLineNum = 66;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 60;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 62;BA.debugLine="End Sub";
return "";
}
public static String  _button1_click() throws Exception{
 //BA.debugLineNum = 72;BA.debugLine="Sub Button1_Click";
 //BA.debugLineNum = 73;BA.debugLine="zx.BeginScan(\"myzx\")";
mostCurrent._zx.BeginScan(mostCurrent.activityBA,"myzx");
 //BA.debugLineNum = 74;BA.debugLine="End Sub";
return "";
}
public static String  _button2_click() throws Exception{
 //BA.debugLineNum = 76;BA.debugLine="Sub Button2_Click";
 //BA.debugLineNum = 77;BA.debugLine="GetJSONData";
_getjsondata();
 //BA.debugLineNum = 78;BA.debugLine="Button3.Enabled=True";
mostCurrent._button3.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 79;BA.debugLine="End Sub";
return "";
}
public static String  _button3_click() throws Exception{
 //BA.debugLineNum = 167;BA.debugLine="Sub Button3_Click";
 //BA.debugLineNum = 168;BA.debugLine="LoadJSONDATA";
_loadjsondata();
 //BA.debugLineNum = 169;BA.debugLine="End Sub";
return "";
}
public static String  _cp_click() throws Exception{
b4a.util.BClipboard _cc = null;
 //BA.debugLineNum = 162;BA.debugLine="Sub cp_Click";
 //BA.debugLineNum = 163;BA.debugLine="Dim CC As BClipboard";
_cc = new b4a.util.BClipboard();
 //BA.debugLineNum = 164;BA.debugLine="CC.setText(Label2.Text)";
_cc.setText(mostCurrent.activityBA,mostCurrent._label2.getText());
 //BA.debugLineNum = 165;BA.debugLine="End Sub";
return "";
}
public static String  _getjsondata() throws Exception{
String _url = "";
anywheresoftware.b4a.http.HttpClientWrapper.HttpUriRequestWrapper _request = null;
 //BA.debugLineNum = 100;BA.debugLine="Sub GetJSONData()";
 //BA.debugLineNum = 101;BA.debugLine="Dim URL As String";
_url = "";
 //BA.debugLineNum = 102;BA.debugLine="URL=\"https://api.douban.com/v2/book/isbn/:\"&result";
_url = "https://api.douban.com/v2/book/isbn/:"+_result;
 //BA.debugLineNum = 103;BA.debugLine="Dim request As HttpRequest";
_request = new anywheresoftware.b4a.http.HttpClientWrapper.HttpUriRequestWrapper();
 //BA.debugLineNum = 104;BA.debugLine="request.InitializeGet(URL)";
_request.InitializeGet(_url);
 //BA.debugLineNum = 105;BA.debugLine="request.Timeout = 10000 'set timeout to 10 seconds";
_request.setTimeout((int) (10000));
 //BA.debugLineNum = 106;BA.debugLine="If HttpClient1.Execute(request, 1) = False Then Return 'Will be false if their is already a running task (with the same id).";
if (_httpclient1.Execute(processBA,_request,(int) (1))==anywheresoftware.b4a.keywords.Common.False) { 
if (true) return "";};
 //BA.debugLineNum = 107;BA.debugLine="ProgressDialogShow(\"获取数据中...\")";
anywheresoftware.b4a.keywords.Common.ProgressDialogShow(mostCurrent.activityBA,"获取数据中...");
 //BA.debugLineNum = 109;BA.debugLine="End Sub";
return "";
}

public static void initializeProcessGlobals() {
    
    if (main.processGlobalsRun == false) {
	    main.processGlobalsRun = true;
		try {
		        anywheresoftware.b4a.samples.httputils2.httputils2service._process_globals();
main._process_globals();
about._process_globals();
web._process_globals();
		
        } catch (Exception e) {
			throw new RuntimeException(e);
		}
    }
}public static String  _globals() throws Exception{
 //BA.debugLineNum = 19;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 22;BA.debugLine="Dim zx As Zxing_B4A";
mostCurrent._zx = new ice.zxing.b4aZXingLib();
 //BA.debugLineNum = 23;BA.debugLine="Dim Button1 As Button";
mostCurrent._button1 = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 24;BA.debugLine="Dim Label1 As Label";
mostCurrent._label1 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 25;BA.debugLine="Dim Label2 As Label";
mostCurrent._label2 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 26;BA.debugLine="Dim bookname As String";
mostCurrent._bookname = "";
 //BA.debugLineNum = 27;BA.debugLine="Dim bookprice As String";
mostCurrent._bookprice = "";
 //BA.debugLineNum = 28;BA.debugLine="Dim bookpublisher As String";
mostCurrent._bookpublisher = "";
 //BA.debugLineNum = 29;BA.debugLine="Dim Button2 As Button";
mostCurrent._button2 = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 30;BA.debugLine="Dim ImageView1 As ImageView";
mostCurrent._imageview1 = new anywheresoftware.b4a.objects.ImageViewWrapper();
 //BA.debugLineNum = 31;BA.debugLine="Dim Label3 As Label";
mostCurrent._label3 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 32;BA.debugLine="Dim sJSONData As String";
mostCurrent._sjsondata = "";
 //BA.debugLineNum = 33;BA.debugLine="Dim msg As String";
mostCurrent._msg = "";
 //BA.debugLineNum = 34;BA.debugLine="Dim bookpubdate As String";
mostCurrent._bookpubdate = "";
 //BA.debugLineNum = 35;BA.debugLine="Dim job1 As HttpJob";
mostCurrent._job1 = new anywheresoftware.b4a.samples.httputils2.httpjob();
 //BA.debugLineNum = 36;BA.debugLine="Dim Button3 As Button";
mostCurrent._button3 = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 37;BA.debugLine="End Sub";
return "";
}
public static String  _httpclient1_responseerror(String _reason,int _statuscode,int _taskid) throws Exception{
 //BA.debugLineNum = 90;BA.debugLine="Sub HttpClient1_ResponseError (Reason As String, StatusCode As Int, TaskId As Int)";
 //BA.debugLineNum = 91;BA.debugLine="Log(Reason)";
anywheresoftware.b4a.keywords.Common.Log(_reason);
 //BA.debugLineNum = 92;BA.debugLine="Log(StatusCode)";
anywheresoftware.b4a.keywords.Common.Log(BA.NumberToString(_statuscode));
 //BA.debugLineNum = 93;BA.debugLine="ProgressDialogHide";
anywheresoftware.b4a.keywords.Common.ProgressDialogHide();
 //BA.debugLineNum = 94;BA.debugLine="msg = \"连接失败。\"";
mostCurrent._msg = "连接失败。";
 //BA.debugLineNum = 95;BA.debugLine="Button3.Enabled=False";
mostCurrent._button3.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 96;BA.debugLine="If Reason <> Null Then msg = msg & CRLF & Reason";
if (_reason!= null) { 
mostCurrent._msg = mostCurrent._msg+anywheresoftware.b4a.keywords.Common.CRLF+_reason;};
 //BA.debugLineNum = 97;BA.debugLine="ToastMessageShow (msg, True)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(mostCurrent._msg,anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 98;BA.debugLine="End Sub";
return "";
}
public static String  _httpclient1_responsesuccess(anywheresoftware.b4a.http.HttpClientWrapper.HttpResponeWrapper _response,int _taskid) throws Exception{
String _jresult = "";
 //BA.debugLineNum = 81;BA.debugLine="Sub HttpClient1_ResponseSuccess (Response As HttpResponse, TaskId As Int)";
 //BA.debugLineNum = 82;BA.debugLine="Log(\"ResponseSuccess\")";
anywheresoftware.b4a.keywords.Common.Log("ResponseSuccess");
 //BA.debugLineNum = 83;BA.debugLine="ProgressDialogHide";
anywheresoftware.b4a.keywords.Common.ProgressDialogHide();
 //BA.debugLineNum = 84;BA.debugLine="Dim jresult As String";
_jresult = "";
 //BA.debugLineNum = 85;BA.debugLine="jresult = Response.GetString(\"UTF8\") 'Convert the response to a string";
_jresult = _response.GetString("UTF8");
 //BA.debugLineNum = 86;BA.debugLine="sJSONData = jresult";
mostCurrent._sjsondata = _jresult;
 //BA.debugLineNum = 88;BA.debugLine="End Sub";
return "";
}
public static String  _jobdone(anywheresoftware.b4a.samples.httputils2.httpjob _job) throws Exception{
 //BA.debugLineNum = 131;BA.debugLine="Sub JobDone (job As HttpJob)";
 //BA.debugLineNum = 132;BA.debugLine="Log(\"JobName = \" & job.JobName & \", Success = \" & job.Success)";
anywheresoftware.b4a.keywords.Common.Log("JobName = "+_job._jobname+", Success = "+BA.ObjectToString(_job._success));
 //BA.debugLineNum = 133;BA.debugLine="If job.Success = True Then";
if (_job._success==anywheresoftware.b4a.keywords.Common.True) { 
 //BA.debugLineNum = 134;BA.debugLine="Select job.JobName";
switch (BA.switchObjectToInt(_job._jobname,"Job1","Job2","Job3")) {
case 0:
case 1:
 //BA.debugLineNum = 137;BA.debugLine="Log(job.GetString)";
anywheresoftware.b4a.keywords.Common.Log(_job._getstring());
 //BA.debugLineNum = 138;BA.debugLine="ToastMessageShow(\"上传成功！\",False)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow("上传成功！",anywheresoftware.b4a.keywords.Common.False);
 break;
case 2:
 //BA.debugLineNum = 141;BA.debugLine="Activity.SetBackgroundImage(job.GetBitmap)";
mostCurrent._activity.SetBackgroundImage((android.graphics.Bitmap)(_job._getbitmap().getObject()));
 break;
}
;
 }else {
 //BA.debugLineNum = 144;BA.debugLine="Log(\"Error: \" & job.ErrorMessage)";
anywheresoftware.b4a.keywords.Common.Log("Error: "+_job._errormessage);
 //BA.debugLineNum = 145;BA.debugLine="ToastMessageShow(\"Error: \" & job.ErrorMessage, True)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow("Error: "+_job._errormessage,anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 147;BA.debugLine="job.Release";
_job._release();
 //BA.debugLineNum = 148;BA.debugLine="End Sub";
return "";
}
public static String  _loadjsondata() throws Exception{
anywheresoftware.b4a.objects.collections.JSONParser _json = null;
anywheresoftware.b4a.objects.collections.Map _map1 = null;
 //BA.debugLineNum = 111;BA.debugLine="Sub LoadJSONDATA()";
 //BA.debugLineNum = 113;BA.debugLine="Log(\"Data:\"&sJSONData)";
anywheresoftware.b4a.keywords.Common.Log("Data:"+mostCurrent._sjsondata);
 //BA.debugLineNum = 115;BA.debugLine="Dim JSON As JSONParser";
_json = new anywheresoftware.b4a.objects.collections.JSONParser();
 //BA.debugLineNum = 116;BA.debugLine="JSON.Initialize(sJSONData)";
_json.Initialize(mostCurrent._sjsondata);
 //BA.debugLineNum = 117;BA.debugLine="Dim map1 As Map";
_map1 = new anywheresoftware.b4a.objects.collections.Map();
 //BA.debugLineNum = 118;BA.debugLine="map1 = JSON.Nextobject";
_map1 = _json.NextObject();
 //BA.debugLineNum = 121;BA.debugLine="bookname = map1.GetValueAt(12)";
mostCurrent._bookname = BA.ObjectToString(_map1.GetValueAt((int) (12)));
 //BA.debugLineNum = 122;BA.debugLine="bookprice= map1.GetValueAt(13)";
mostCurrent._bookprice = BA.ObjectToString(_map1.GetValueAt((int) (13)));
 //BA.debugLineNum = 123;BA.debugLine="bookpublisher= map1.GetValueAt(9)";
mostCurrent._bookpublisher = BA.ObjectToString(_map1.GetValueAt((int) (9)));
 //BA.debugLineNum = 124;BA.debugLine="bookpubdate= map1.GetValueAt(4)";
mostCurrent._bookpubdate = BA.ObjectToString(_map1.GetValueAt((int) (4)));
 //BA.debugLineNum = 128;BA.debugLine="Label2.Text=\"条码：\"&result&CRLF&\"书名：\"&bookname&CRLF&\"价格：\"&bookprice&CRLF&\"出版社：\"&bookpublisher&CRLF&\"出版年份：\"&bookpubdate";
mostCurrent._label2.setText((Object)("条码："+_result+anywheresoftware.b4a.keywords.Common.CRLF+"书名："+mostCurrent._bookname+anywheresoftware.b4a.keywords.Common.CRLF+"价格："+mostCurrent._bookprice+anywheresoftware.b4a.keywords.Common.CRLF+"出版社："+mostCurrent._bookpublisher+anywheresoftware.b4a.keywords.Common.CRLF+"出版年份："+mostCurrent._bookpubdate));
 //BA.debugLineNum = 129;BA.debugLine="End Sub";
return "";
}
public static String  _myzx_result(String _atype,String _values) throws Exception{
 //BA.debugLineNum = 53;BA.debugLine="Sub myzx_result(atype As String,Values As String)";
 //BA.debugLineNum = 55;BA.debugLine="result=Values";
_result = _values;
 //BA.debugLineNum = 56;BA.debugLine="Label2.Text = \"结果为：\"&result";
mostCurrent._label2.setText((Object)("结果为："+_result));
 //BA.debugLineNum = 57;BA.debugLine="Button2.Enabled=True";
mostCurrent._button2.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 58;BA.debugLine="End Sub";
return "";
}
public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 12;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 15;BA.debugLine="Dim result=1 As String";
_result = BA.NumberToString(1);
 //BA.debugLineNum = 16;BA.debugLine="Dim HttpClient1 As HttpClient";
_httpclient1 = new anywheresoftware.b4a.http.HttpClientWrapper();
 //BA.debugLineNum = 17;BA.debugLine="End Sub";
return "";
}
public static String  _quit_click() throws Exception{
 //BA.debugLineNum = 150;BA.debugLine="Sub Quit_Click";
 //BA.debugLineNum = 151;BA.debugLine="ExitApplication";
anywheresoftware.b4a.keywords.Common.ExitApplication();
 //BA.debugLineNum = 152;BA.debugLine="End Sub";
return "";
}
public static String  _upload_click() throws Exception{
long _now = 0L;
String _time = "";
 //BA.debugLineNum = 171;BA.debugLine="Sub upload_Click";
 //BA.debugLineNum = 172;BA.debugLine="ToastMessageShow(\"上传中，请等待提示。\",False)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow("上传中，请等待提示。",anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 173;BA.debugLine="Dim now As Long";
_now = 0L;
 //BA.debugLineNum = 174;BA.debugLine="Dim time As String";
_time = "";
 //BA.debugLineNum = 175;BA.debugLine="now = DateTime.now";
_now = anywheresoftware.b4a.keywords.Common.DateTime.getNow();
 //BA.debugLineNum = 176;BA.debugLine="time=DateTime.GetYear(now)&\"/\"&DateTime.GetMonth(now)&\"/\"&DateTime.GetDayOfMonth(now)&\"/\"&DateTime.GetHour(now)&\"/\"&DateTime.GetMinute(now)&\"/\"";
_time = BA.NumberToString(anywheresoftware.b4a.keywords.Common.DateTime.GetYear(_now))+"/"+BA.NumberToString(anywheresoftware.b4a.keywords.Common.DateTime.GetMonth(_now))+"/"+BA.NumberToString(anywheresoftware.b4a.keywords.Common.DateTime.GetDayOfMonth(_now))+"/"+BA.NumberToString(anywheresoftware.b4a.keywords.Common.DateTime.GetHour(_now))+"/"+BA.NumberToString(anywheresoftware.b4a.keywords.Common.DateTime.GetMinute(_now))+"/";
 //BA.debugLineNum = 177;BA.debugLine="Log(time)";
anywheresoftware.b4a.keywords.Common.Log(_time);
 //BA.debugLineNum = 178;BA.debugLine="job1.Initialize(\"Job1\",Me)";
mostCurrent._job1._initialize(processBA,"Job1",main.getObject());
 //BA.debugLineNum = 179;BA.debugLine="job1.PostString(\"https://bottle-bookjnrain.rhcloud.com/login\",\"username=admin&password=admin&isbn=\"&result&\"&time=\"&time)";
mostCurrent._job1._poststring("https://bottle-bookjnrain.rhcloud.com/login","username=admin&password=admin&isbn="+_result+"&time="+_time);
 //BA.debugLineNum = 180;BA.debugLine="End Sub";
return "";
}
public static String  _web_click() throws Exception{
 //BA.debugLineNum = 158;BA.debugLine="Sub web_Click";
 //BA.debugLineNum = 159;BA.debugLine="StartActivity(web)";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(mostCurrent._web.getObject()));
 //BA.debugLineNum = 160;BA.debugLine="End Sub";
return "";
}
}
