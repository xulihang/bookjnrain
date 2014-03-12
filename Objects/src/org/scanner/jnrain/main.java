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
public static anywheresoftware.b4a.sql.SQL _sql1 = null;
public ice.zxing.b4aZXingLib _zx = null;
public anywheresoftware.b4a.objects.ButtonWrapper _button1 = null;
public anywheresoftware.b4a.objects.LabelWrapper _label1 = null;
public anywheresoftware.b4a.objects.LabelWrapper _label2 = null;
public static String _bookname = "";
public static String _bookprice = "";
public static String _bookpublisher = "";
public static String _booksummary = "";
public anywheresoftware.b4a.objects.ButtonWrapper _button2 = null;
public anywheresoftware.b4a.objects.ImageViewWrapper _imageview1 = null;
public anywheresoftware.b4a.objects.LabelWrapper _label3 = null;
public static String _sjsondata = "";
public static String _bookpubdate = "";
public anywheresoftware.b4a.samples.httputils2.httpjob _job1 = null;
public org.scanner.jnrain.slidemenu _sm = null;
public anywheresoftware.b4a.objects.ButtonWrapper _button3 = null;
public static float _startx = 0f;
public static float _starty = 0f;
public anywheresoftware.b4a.samples.httputils2.httputils2service _httputils2service = null;
public org.scanner.jnrain.about _about = null;
public org.scanner.jnrain.web _web = null;
public org.scanner.jnrain.query _query = null;
  public Object[] GetGlobals() {
		return new Object[] {"about",Debug.moduleToString(org.scanner.jnrain.about.class),"Activity",mostCurrent._activity,"bookname",mostCurrent._bookname,"bookprice",mostCurrent._bookprice,"bookpubdate",mostCurrent._bookpubdate,"bookpublisher",mostCurrent._bookpublisher,"booksummary",mostCurrent._booksummary,"Button1",mostCurrent._button1,"Button2",mostCurrent._button2,"Button3",mostCurrent._button3,"HttpUtils2Service",mostCurrent._httputils2service,"ImageView1",mostCurrent._imageview1,"job1",mostCurrent._job1,"Label1",mostCurrent._label1,"Label2",mostCurrent._label2,"Label3",mostCurrent._label3,"query",Debug.moduleToString(org.scanner.jnrain.query.class),"result",_result,"sJSONData",mostCurrent._sjsondata,"sm",mostCurrent._sm,"SQL1",_sql1,"startX",_startx,"startY",_starty,"web",Debug.moduleToString(org.scanner.jnrain.web.class),"zx",mostCurrent._zx};
}

public static boolean isAnyActivityVisible() {
    boolean vis = false;
vis = vis | (main.mostCurrent != null);
vis = vis | (about.mostCurrent != null);
vis = vis | (web.mostCurrent != null);
vis = vis | (query.mostCurrent != null);
return vis;}

public static void killProgram() {
     {
            Activity __a = null;
            if (main.previousOne != null) {
				__a = main.previousOne.get();
			}
            else {
                BA ba = main.mostCurrent.processBA.sharedProcessBA.activityBA.get();
                if (ba != null) __a = ba.activity;
            }
            if (__a != null)
				__a.finish();}

 {
            Activity __a = null;
            if (about.previousOne != null) {
				__a = about.previousOne.get();
			}
            else {
                BA ba = about.mostCurrent.processBA.sharedProcessBA.activityBA.get();
                if (ba != null) __a = ba.activity;
            }
            if (__a != null)
				__a.finish();}

 {
            Activity __a = null;
            if (web.previousOne != null) {
				__a = web.previousOne.get();
			}
            else {
                BA ba = web.mostCurrent.processBA.sharedProcessBA.activityBA.get();
                if (ba != null) __a = ba.activity;
            }
            if (__a != null)
				__a.finish();}

 {
            Activity __a = null;
            if (query.previousOne != null) {
				__a = query.previousOne.get();
			}
            else {
                BA ba = query.mostCurrent.processBA.sharedProcessBA.activityBA.get();
                if (ba != null) __a = ba.activity;
            }
            if (__a != null)
				__a.finish();}

}
public static String  _about_click() throws Exception{
		Debug.PushSubsStack("about_Click (main) ","main",0,mostCurrent.activityBA,mostCurrent);
try {
 BA.debugLineNum = 153;BA.debugLine="Sub about_Click";
Debug.ShouldStop(16777216);
 BA.debugLineNum = 154;BA.debugLine="StartActivity(about)";
Debug.ShouldStop(33554432);
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(mostCurrent._about.getObject()));
 BA.debugLineNum = 155;BA.debugLine="End Sub";
Debug.ShouldStop(67108864);
return "";
}
catch (Exception e) {
			Debug.ErrorCaught(e);
			throw e;
		} 
finally {
			Debug.PopSubsStack();
		}}
public static String  _activity_create(boolean _firsttime) throws Exception{
		Debug.PushSubsStack("Activity_Create (main) ","main",0,mostCurrent.activityBA,mostCurrent);
try {
Debug.locals.put("FirstTime", _firsttime);
 BA.debugLineNum = 41;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
Debug.ShouldStop(256);
 BA.debugLineNum = 43;BA.debugLine="If File.Exists(File.DirInternal,\"mybook.db\") Then";
Debug.ShouldStop(1024);
if (anywheresoftware.b4a.keywords.Common.File.Exists(anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"mybook.db")) { 
 BA.debugLineNum = 44;BA.debugLine="ToastMessageShow(\"数据库已存在\",False)";
Debug.ShouldStop(2048);
anywheresoftware.b4a.keywords.Common.ToastMessageShow("数据库已存在",anywheresoftware.b4a.keywords.Common.False);
 }else {
 BA.debugLineNum = 46;BA.debugLine="SQL1.Initialize(File.DirInternal, \"mybook.db\", True)";
Debug.ShouldStop(8192);
Debug.DebugWarningEngine.CheckInitialize(_sql1);_sql1.Initialize(anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"mybook.db",anywheresoftware.b4a.keywords.Common.True);
 BA.debugLineNum = 47;BA.debugLine="SQL1.ExecNonQuery(\"CREATE TABLE book (title , price, publisher, isbn, pubdate, lasttime, introduction)\")";
Debug.ShouldStop(16384);
_sql1.ExecNonQuery("CREATE TABLE book (title , price, publisher, isbn, pubdate, lasttime, introduction)");
 };
 BA.debugLineNum = 50;BA.debugLine="If SQL1.IsInitialized = False Then";
Debug.ShouldStop(131072);
if (_sql1.IsInitialized()==anywheresoftware.b4a.keywords.Common.False) { 
 BA.debugLineNum = 51;BA.debugLine="SQL1.Initialize(File.DirInternal, \"mybook.db\", False)";
Debug.ShouldStop(262144);
Debug.DebugWarningEngine.CheckInitialize(_sql1);_sql1.Initialize(anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"mybook.db",anywheresoftware.b4a.keywords.Common.False);
 };
 BA.debugLineNum = 53;BA.debugLine="Activity.LoadLayout(\"scanner\")";
Debug.ShouldStop(1048576);
mostCurrent._activity.LoadLayout("scanner",mostCurrent.activityBA);
 BA.debugLineNum = 54;BA.debugLine="Activity.AddMenuItem(\"在线工具\",\"web\")";
Debug.ShouldStop(2097152);
mostCurrent._activity.AddMenuItem("在线工具","web");
 BA.debugLineNum = 55;BA.debugLine="Activity.AddMenuItem(\"复制到剪贴板\",\"cp\")";
Debug.ShouldStop(4194304);
mostCurrent._activity.AddMenuItem("复制到剪贴板","cp");
 BA.debugLineNum = 56;BA.debugLine="Activity.AddMenuItem(\"存储到本地\",\"storetodatabase\")";
Debug.ShouldStop(8388608);
mostCurrent._activity.AddMenuItem("存储到本地","storetodatabase");
 BA.debugLineNum = 57;BA.debugLine="Activity.AddMenuItem(\"查看本地数据\",\"query\")";
Debug.ShouldStop(16777216);
mostCurrent._activity.AddMenuItem("查看本地数据","query");
 BA.debugLineNum = 58;BA.debugLine="Activity.AddMenuItem(\"上传到服务器\",\"upload\")";
Debug.ShouldStop(33554432);
mostCurrent._activity.AddMenuItem("上传到服务器","upload");
 BA.debugLineNum = 59;BA.debugLine="Activity.AddMenuItem(\"关于\",\"about\")";
Debug.ShouldStop(67108864);
mostCurrent._activity.AddMenuItem("关于","about");
 BA.debugLineNum = 60;BA.debugLine="Activity.AddMenuItem(\"退出\",\"Quit\")";
Debug.ShouldStop(134217728);
mostCurrent._activity.AddMenuItem("退出","Quit");
 BA.debugLineNum = 62;BA.debugLine="Button2.Enabled=False";
Debug.ShouldStop(536870912);
mostCurrent._button2.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 BA.debugLineNum = 63;BA.debugLine="Initilize_sm";
Debug.ShouldStop(1073741824);
_initilize_sm();
 BA.debugLineNum = 64;BA.debugLine="End Sub";
Debug.ShouldStop(-2147483648);
return "";
}
catch (Exception e) {
			Debug.ErrorCaught(e);
			throw e;
		} 
finally {
			Debug.PopSubsStack();
		}}
public static String  _activity_pause(boolean _userclosed) throws Exception{
		Debug.PushSubsStack("Activity_Pause (main) ","main",0,mostCurrent.activityBA,mostCurrent);
try {
Debug.locals.put("UserClosed", _userclosed);
 BA.debugLineNum = 88;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
Debug.ShouldStop(8388608);
 BA.debugLineNum = 90;BA.debugLine="End Sub";
Debug.ShouldStop(33554432);
return "";
}
catch (Exception e) {
			Debug.ErrorCaught(e);
			throw e;
		} 
finally {
			Debug.PopSubsStack();
		}}
public static String  _activity_resume() throws Exception{
		Debug.PushSubsStack("Activity_Resume (main) ","main",0,mostCurrent.activityBA,mostCurrent);
try {
 BA.debugLineNum = 84;BA.debugLine="Sub Activity_Resume";
Debug.ShouldStop(524288);
 BA.debugLineNum = 85;BA.debugLine="Initilize_sm";
Debug.ShouldStop(1048576);
_initilize_sm();
 BA.debugLineNum = 86;BA.debugLine="End Sub";
Debug.ShouldStop(2097152);
return "";
}
catch (Exception e) {
			Debug.ErrorCaught(e);
			throw e;
		} 
finally {
			Debug.PopSubsStack();
		}}
public static String  _activity_touch(int _action,float _x,float _y) throws Exception{
		Debug.PushSubsStack("Activity_Touch (main) ","main",0,mostCurrent.activityBA,mostCurrent);
try {
int _leftorright = 0;
Debug.locals.put("Action", _action);
Debug.locals.put("X", _x);
Debug.locals.put("Y", _y);
 BA.debugLineNum = 231;BA.debugLine="Sub Activity_Touch (Action As Int, X As Float, Y As Float)";
Debug.ShouldStop(64);
 BA.debugLineNum = 232;BA.debugLine="Dim leftorright As Int";
Debug.ShouldStop(128);
_leftorright = 0;Debug.locals.put("leftorright", _leftorright);
 BA.debugLineNum = 233;BA.debugLine="leftorright=0";
Debug.ShouldStop(256);
_leftorright = (int) (0);Debug.locals.put("leftorright", _leftorright);
 BA.debugLineNum = 234;BA.debugLine="Select Action";
Debug.ShouldStop(512);
switch (BA.switchObjectToInt(_action,mostCurrent._activity.ACTION_DOWN,mostCurrent._activity.ACTION_UP)) {
case 0:
 BA.debugLineNum = 236;BA.debugLine="startX = X";
Debug.ShouldStop(2048);
_startx = _x;
 BA.debugLineNum = 237;BA.debugLine="startY = Y";
Debug.ShouldStop(4096);
_starty = _y;
 break;
case 1:
 BA.debugLineNum = 239;BA.debugLine="If Abs(Y - startY) > 20%y Then Return";
Debug.ShouldStop(16384);
if (anywheresoftware.b4a.keywords.Common.Abs(_y-_starty)>anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (20),mostCurrent.activityBA)) { 
if (true) return "";};
 BA.debugLineNum = 240;BA.debugLine="If X - startX > 30%x Then '向右划";
Debug.ShouldStop(32768);
if (_x-_startx>anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (30),mostCurrent.activityBA)) { 
 BA.debugLineNum = 241;BA.debugLine="If leftorright=0 Then";
Debug.ShouldStop(65536);
if (_leftorright==0) { 
 BA.debugLineNum = 242;BA.debugLine="sm.show";
Debug.ShouldStop(131072);
mostCurrent._sm._show();
 BA.debugLineNum = 243;BA.debugLine="leftorright=1";
Debug.ShouldStop(262144);
_leftorright = (int) (1);Debug.locals.put("leftorright", _leftorright);
 };
 }else 
{ BA.debugLineNum = 245;BA.debugLine="Else If startX - X > 30%x Then '向左划";
Debug.ShouldStop(1048576);
if (_startx-_x>anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (30),mostCurrent.activityBA)) { 
 BA.debugLineNum = 246;BA.debugLine="If leftorright=1 Then";
Debug.ShouldStop(2097152);
if (_leftorright==1) { 
 BA.debugLineNum = 247;BA.debugLine="sm.show";
Debug.ShouldStop(4194304);
mostCurrent._sm._show();
 BA.debugLineNum = 248;BA.debugLine="leftorright=0";
Debug.ShouldStop(8388608);
_leftorright = (int) (0);Debug.locals.put("leftorright", _leftorright);
 };
 }};
 break;
}
;
 BA.debugLineNum = 252;BA.debugLine="End Sub";
Debug.ShouldStop(134217728);
return "";
}
catch (Exception e) {
			Debug.ErrorCaught(e);
			throw e;
		} 
finally {
			Debug.PopSubsStack();
		}}
public static String  _button1_click() throws Exception{
		Debug.PushSubsStack("Button1_Click (main) ","main",0,mostCurrent.activityBA,mostCurrent);
try {
 BA.debugLineNum = 96;BA.debugLine="Sub Button1_Click";
Debug.ShouldStop(-2147483648);
 BA.debugLineNum = 97;BA.debugLine="zx.BeginScan(\"myzx\")";
Debug.ShouldStop(1);
mostCurrent._zx.BeginScan(mostCurrent.activityBA,"myzx");
 BA.debugLineNum = 98;BA.debugLine="End Sub";
Debug.ShouldStop(2);
return "";
}
catch (Exception e) {
			Debug.ErrorCaught(e);
			throw e;
		} 
finally {
			Debug.PopSubsStack();
		}}
public static String  _button2_click() throws Exception{
		Debug.PushSubsStack("Button2_Click (main) ","main",0,mostCurrent.activityBA,mostCurrent);
try {
anywheresoftware.b4a.samples.httputils2.httpjob _job2 = null;
 BA.debugLineNum = 100;BA.debugLine="Sub Button2_Click";
Debug.ShouldStop(8);
 BA.debugLineNum = 101;BA.debugLine="Dim job2 As HttpJob";
Debug.ShouldStop(16);
_job2 = new anywheresoftware.b4a.samples.httputils2.httpjob();Debug.locals.put("job2", _job2);
 BA.debugLineNum = 102;BA.debugLine="job2.Initialize(\"Job2\",Me)";
Debug.ShouldStop(32);
_job2._initialize(processBA,"Job2",main.getObject());
 BA.debugLineNum = 103;BA.debugLine="job2.Download(\"https://api.douban.com/v2/book/isbn/:\"&result)";
Debug.ShouldStop(64);
_job2._download("https://api.douban.com/v2/book/isbn/:"+_result);
 BA.debugLineNum = 104;BA.debugLine="ProgressDialogShow(\"获取数据中...\")";
Debug.ShouldStop(128);
anywheresoftware.b4a.keywords.Common.ProgressDialogShow(mostCurrent.activityBA,"获取数据中...");
 BA.debugLineNum = 105;BA.debugLine="End Sub";
Debug.ShouldStop(256);
return "";
}
catch (Exception e) {
			Debug.ErrorCaught(e);
			throw e;
		} 
finally {
			Debug.PopSubsStack();
		}}
public static String  _button3_click() throws Exception{
		Debug.PushSubsStack("Button3_Click (main) ","main",0,mostCurrent.activityBA,mostCurrent);
try {
 BA.debugLineNum = 205;BA.debugLine="Sub Button3_Click";
Debug.ShouldStop(4096);
 BA.debugLineNum = 206;BA.debugLine="sm.Show";
Debug.ShouldStop(8192);
mostCurrent._sm._show();
 BA.debugLineNum = 207;BA.debugLine="End Sub";
Debug.ShouldStop(16384);
return "";
}
catch (Exception e) {
			Debug.ErrorCaught(e);
			throw e;
		} 
finally {
			Debug.PopSubsStack();
		}}
public static String  _cp_click() throws Exception{
		Debug.PushSubsStack("cp_Click (main) ","main",0,mostCurrent.activityBA,mostCurrent);
try {
b4a.util.BClipboard _cc = null;
 BA.debugLineNum = 161;BA.debugLine="Sub cp_Click";
Debug.ShouldStop(1);
 BA.debugLineNum = 162;BA.debugLine="Dim CC As BClipboard";
Debug.ShouldStop(2);
_cc = new b4a.util.BClipboard();Debug.locals.put("CC", _cc);
 BA.debugLineNum = 163;BA.debugLine="CC.setText(Label2.Text)";
Debug.ShouldStop(4);
_cc.setText(mostCurrent.activityBA,mostCurrent._label2.getText());
 BA.debugLineNum = 164;BA.debugLine="ToastMessageShow(\"结果已复制到剪切板。\",False)";
Debug.ShouldStop(8);
anywheresoftware.b4a.keywords.Common.ToastMessageShow("结果已复制到剪切板。",anywheresoftware.b4a.keywords.Common.False);
 BA.debugLineNum = 165;BA.debugLine="End Sub";
Debug.ShouldStop(16);
return "";
}
catch (Exception e) {
			Debug.ErrorCaught(e);
			throw e;
		} 
finally {
			Debug.PopSubsStack();
		}}

public static void initializeProcessGlobals() {
    if (mostCurrent != null && mostCurrent.activityBA != null) {
Debug.StartDebugging(mostCurrent.activityBA, 45991, new int[] {8, 2, 3, 4, 2}, "572e108e-4a39-4231-aa32-ba636f2fc41f");}

    if (main.processGlobalsRun == false) {
	    main.processGlobalsRun = true;
		try {
		        anywheresoftware.b4a.samples.httputils2.httputils2service._process_globals();
main._process_globals();
about._process_globals();
web._process_globals();
query._process_globals();
		
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
 //BA.debugLineNum = 29;BA.debugLine="Dim booksummary As String";
mostCurrent._booksummary = "";
 //BA.debugLineNum = 30;BA.debugLine="Dim Button2 As Button";
mostCurrent._button2 = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 31;BA.debugLine="Dim ImageView1 As ImageView";
mostCurrent._imageview1 = new anywheresoftware.b4a.objects.ImageViewWrapper();
 //BA.debugLineNum = 32;BA.debugLine="Dim Label3 As Label";
mostCurrent._label3 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 33;BA.debugLine="Dim sJSONData As String";
mostCurrent._sjsondata = "";
 //BA.debugLineNum = 34;BA.debugLine="Dim bookpubdate As String";
mostCurrent._bookpubdate = "";
 //BA.debugLineNum = 35;BA.debugLine="Dim job1 As HttpJob";
mostCurrent._job1 = new anywheresoftware.b4a.samples.httputils2.httpjob();
 //BA.debugLineNum = 36;BA.debugLine="Dim sm As SlideMenu";
mostCurrent._sm = new org.scanner.jnrain.slidemenu();
 //BA.debugLineNum = 37;BA.debugLine="Dim Button3 As Button";
mostCurrent._button3 = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 38;BA.debugLine="Dim startX, startY As Float";
_startx = 0f;
_starty = 0f;
 //BA.debugLineNum = 39;BA.debugLine="End Sub";
return "";
}
public static String  _initilize_sm() throws Exception{
		Debug.PushSubsStack("Initilize_sm (main) ","main",0,mostCurrent.activityBA,mostCurrent);
try {
 BA.debugLineNum = 73;BA.debugLine="Sub Initilize_sm";
Debug.ShouldStop(256);
 BA.debugLineNum = 74;BA.debugLine="sm.Initialize(Activity, Me, \"SlideMenu\", 42dip, 180dip)";
Debug.ShouldStop(512);
mostCurrent._sm._initialize(mostCurrent.activityBA,mostCurrent._activity,main.getObject(),"SlideMenu",anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (42)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (180)));
 BA.debugLineNum = 76;BA.debugLine="sm.AddItem(\"在线工具\", Null, 1)";
Debug.ShouldStop(2048);
mostCurrent._sm._additem("在线工具",(anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper(), (android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.Null)),(Object)(1));
 BA.debugLineNum = 77;BA.debugLine="sm.AddItem(\"复制到剪贴板\", Null, 2)";
Debug.ShouldStop(4096);
mostCurrent._sm._additem("复制到剪贴板",(anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper(), (android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.Null)),(Object)(2));
 BA.debugLineNum = 78;BA.debugLine="sm.AddItem(\"存储到本地\", Null, 3)";
Debug.ShouldStop(8192);
mostCurrent._sm._additem("存储到本地",(anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper(), (android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.Null)),(Object)(3));
 BA.debugLineNum = 79;BA.debugLine="sm.AddItem(\"查看本地数据\", Null, 4)";
Debug.ShouldStop(16384);
mostCurrent._sm._additem("查看本地数据",(anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper(), (android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.Null)),(Object)(4));
 BA.debugLineNum = 80;BA.debugLine="sm.AddItem(\"上传到服务器\", Null, 5)";
Debug.ShouldStop(32768);
mostCurrent._sm._additem("上传到服务器",(anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper(), (android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.Null)),(Object)(5));
 BA.debugLineNum = 81;BA.debugLine="sm.AddItem(\"关于\", Null, 6)";
Debug.ShouldStop(65536);
mostCurrent._sm._additem("关于",(anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper(), (android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.Null)),(Object)(6));
 BA.debugLineNum = 82;BA.debugLine="sm.AddItem(\"退出\", Null, 7)";
Debug.ShouldStop(131072);
mostCurrent._sm._additem("退出",(anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper(), (android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.Null)),(Object)(7));
 BA.debugLineNum = 83;BA.debugLine="End Sub";
Debug.ShouldStop(262144);
return "";
}
catch (Exception e) {
			Debug.ErrorCaught(e);
			throw e;
		} 
finally {
			Debug.PopSubsStack();
		}}
public static String  _jobdone(anywheresoftware.b4a.samples.httputils2.httpjob _job) throws Exception{
		Debug.PushSubsStack("JobDone (main) ","main",0,mostCurrent.activityBA,mostCurrent);
try {
String _jresult = "";
Debug.locals.put("job", _job);
 BA.debugLineNum = 125;BA.debugLine="Sub JobDone (job As HttpJob)";
Debug.ShouldStop(268435456);
 BA.debugLineNum = 126;BA.debugLine="Log(\"JobName = \" & job.JobName & \", Success = \" & job.Success)";
Debug.ShouldStop(536870912);
anywheresoftware.b4a.keywords.Common.Log("JobName = "+_job._jobname+", Success = "+BA.ObjectToString(_job._success));
 BA.debugLineNum = 127;BA.debugLine="If job.Success = True Then";
Debug.ShouldStop(1073741824);
if (_job._success==anywheresoftware.b4a.keywords.Common.True) { 
 BA.debugLineNum = 128;BA.debugLine="Select job.JobName";
Debug.ShouldStop(-2147483648);
switch (BA.switchObjectToInt(_job._jobname,"Job1","Job2")) {
case 0:
 BA.debugLineNum = 131;BA.debugLine="ProgressDialogHide";
Debug.ShouldStop(4);
anywheresoftware.b4a.keywords.Common.ProgressDialogHide();
 BA.debugLineNum = 132;BA.debugLine="Log(job.GetString)";
Debug.ShouldStop(8);
anywheresoftware.b4a.keywords.Common.Log(_job._getstring());
 BA.debugLineNum = 133;BA.debugLine="ToastMessageShow(\"上传成功！\",False)";
Debug.ShouldStop(16);
anywheresoftware.b4a.keywords.Common.ToastMessageShow("上传成功！",anywheresoftware.b4a.keywords.Common.False);
 break;
case 1:
 BA.debugLineNum = 135;BA.debugLine="ProgressDialogHide";
Debug.ShouldStop(64);
anywheresoftware.b4a.keywords.Common.ProgressDialogHide();
 BA.debugLineNum = 136;BA.debugLine="Dim jresult As String";
Debug.ShouldStop(128);
_jresult = "";Debug.locals.put("jresult", _jresult);
 BA.debugLineNum = 137;BA.debugLine="jresult = job.GetString";
Debug.ShouldStop(256);
_jresult = _job._getstring();Debug.locals.put("jresult", _jresult);
 BA.debugLineNum = 138;BA.debugLine="sJSONData = jresult";
Debug.ShouldStop(512);
mostCurrent._sjsondata = _jresult;
 BA.debugLineNum = 139;BA.debugLine="LoadJSONDATA";
Debug.ShouldStop(1024);
_loadjsondata();
 break;
}
;
 }else {
 BA.debugLineNum = 142;BA.debugLine="ProgressDialogHide";
Debug.ShouldStop(8192);
anywheresoftware.b4a.keywords.Common.ProgressDialogHide();
 BA.debugLineNum = 143;BA.debugLine="Log(\"Error: \" & job.ErrorMessage)";
Debug.ShouldStop(16384);
anywheresoftware.b4a.keywords.Common.Log("Error: "+_job._errormessage);
 BA.debugLineNum = 144;BA.debugLine="ToastMessageShow(\"Error: \" & job.ErrorMessage, True)";
Debug.ShouldStop(32768);
anywheresoftware.b4a.keywords.Common.ToastMessageShow("Error: "+_job._errormessage,anywheresoftware.b4a.keywords.Common.True);
 };
 BA.debugLineNum = 146;BA.debugLine="job.Release";
Debug.ShouldStop(131072);
_job._release();
 BA.debugLineNum = 147;BA.debugLine="End Sub";
Debug.ShouldStop(262144);
return "";
}
catch (Exception e) {
			Debug.ErrorCaught(e);
			throw e;
		} 
finally {
			Debug.PopSubsStack();
		}}
public static String  _loadjsondata() throws Exception{
		Debug.PushSubsStack("LoadJSONDATA (main) ","main",0,mostCurrent.activityBA,mostCurrent);
try {
anywheresoftware.b4a.objects.collections.JSONParser _json = null;
anywheresoftware.b4a.objects.collections.Map _map1 = null;
 BA.debugLineNum = 107;BA.debugLine="Sub LoadJSONDATA()";
Debug.ShouldStop(1024);
 BA.debugLineNum = 109;BA.debugLine="Log(\"Data:\"&sJSONData)";
Debug.ShouldStop(4096);
anywheresoftware.b4a.keywords.Common.Log("Data:"+mostCurrent._sjsondata);
 BA.debugLineNum = 111;BA.debugLine="Dim JSON As JSONParser";
Debug.ShouldStop(16384);
_json = new anywheresoftware.b4a.objects.collections.JSONParser();Debug.locals.put("JSON", _json);
 BA.debugLineNum = 112;BA.debugLine="JSON.Initialize(sJSONData)";
Debug.ShouldStop(32768);
_json.Initialize(mostCurrent._sjsondata);
 BA.debugLineNum = 113;BA.debugLine="Dim map1 As Map";
Debug.ShouldStop(65536);
_map1 = new anywheresoftware.b4a.objects.collections.Map();Debug.locals.put("map1", _map1);
 BA.debugLineNum = 114;BA.debugLine="map1 = JSON.Nextobject";
Debug.ShouldStop(131072);
_map1 = _json.NextObject();Debug.locals.put("map1", _map1);
 BA.debugLineNum = 117;BA.debugLine="bookname = map1.GetValueAt(12)";
Debug.ShouldStop(1048576);
mostCurrent._bookname = BA.ObjectToString(_map1.GetValueAt((int) (12)));
 BA.debugLineNum = 118;BA.debugLine="bookprice= map1.GetValueAt(13)";
Debug.ShouldStop(2097152);
mostCurrent._bookprice = BA.ObjectToString(_map1.GetValueAt((int) (13)));
 BA.debugLineNum = 119;BA.debugLine="bookpublisher= map1.GetValueAt(9)";
Debug.ShouldStop(4194304);
mostCurrent._bookpublisher = BA.ObjectToString(_map1.GetValueAt((int) (9)));
 BA.debugLineNum = 120;BA.debugLine="bookpubdate= map1.GetValueAt(4)  '根据index";
Debug.ShouldStop(8388608);
mostCurrent._bookpubdate = BA.ObjectToString(_map1.GetValueAt((int) (4)));
 BA.debugLineNum = 121;BA.debugLine="booksummary= map1.Get(\"summary\") '根据Key的名称";
Debug.ShouldStop(16777216);
mostCurrent._booksummary = BA.ObjectToString(_map1.Get((Object)("summary")));
 BA.debugLineNum = 122;BA.debugLine="Label2.Text=\"条码：\"&result&CRLF&\"书名：\"&bookname&CRLF&\"价格：\"&bookprice&CRLF&\"出版社：\"&bookpublisher&CRLF&\"出版年份：\"&bookpubdate";
Debug.ShouldStop(33554432);
mostCurrent._label2.setText((Object)("条码："+_result+anywheresoftware.b4a.keywords.Common.CRLF+"书名："+mostCurrent._bookname+anywheresoftware.b4a.keywords.Common.CRLF+"价格："+mostCurrent._bookprice+anywheresoftware.b4a.keywords.Common.CRLF+"出版社："+mostCurrent._bookpublisher+anywheresoftware.b4a.keywords.Common.CRLF+"出版年份："+mostCurrent._bookpubdate));
 BA.debugLineNum = 123;BA.debugLine="End Sub";
Debug.ShouldStop(67108864);
return "";
}
catch (Exception e) {
			Debug.ErrorCaught(e);
			throw e;
		} 
finally {
			Debug.PopSubsStack();
		}}
public static String  _myzx_result(String _atype,String _values) throws Exception{
		Debug.PushSubsStack("myzx_result (main) ","main",0,mostCurrent.activityBA,mostCurrent);
try {
Debug.locals.put("atype", _atype);
Debug.locals.put("Values", _values);
 BA.debugLineNum = 66;BA.debugLine="Sub myzx_result(atype As String,Values As String)";
Debug.ShouldStop(2);
 BA.debugLineNum = 68;BA.debugLine="result=Values";
Debug.ShouldStop(8);
_result = _values;
 BA.debugLineNum = 69;BA.debugLine="Label2.Text = \"结果为：\"&result";
Debug.ShouldStop(16);
mostCurrent._label2.setText((Object)("结果为："+_result));
 BA.debugLineNum = 70;BA.debugLine="Button2.Enabled=True";
Debug.ShouldStop(32);
mostCurrent._button2.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 BA.debugLineNum = 71;BA.debugLine="End Sub";
Debug.ShouldStop(64);
return "";
}
catch (Exception e) {
			Debug.ErrorCaught(e);
			throw e;
		} 
finally {
			Debug.PopSubsStack();
		}}
public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 12;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 15;BA.debugLine="Dim result=1 As String";
_result = BA.NumberToString(1);
 //BA.debugLineNum = 16;BA.debugLine="Dim SQL1 As SQL";
_sql1 = new anywheresoftware.b4a.sql.SQL();
 //BA.debugLineNum = 17;BA.debugLine="End Sub";
return "";
}
public static String  _query_click() throws Exception{
		Debug.PushSubsStack("query_Click (main) ","main",0,mostCurrent.activityBA,mostCurrent);
try {
 BA.debugLineNum = 201;BA.debugLine="Sub query_Click";
Debug.ShouldStop(256);
 BA.debugLineNum = 202;BA.debugLine="StartActivity(query)";
Debug.ShouldStop(512);
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(mostCurrent._query.getObject()));
 BA.debugLineNum = 203;BA.debugLine="End Sub";
Debug.ShouldStop(1024);
return "";
}
catch (Exception e) {
			Debug.ErrorCaught(e);
			throw e;
		} 
finally {
			Debug.PopSubsStack();
		}}
public static String  _quit_click() throws Exception{
		Debug.PushSubsStack("Quit_Click (main) ","main",0,mostCurrent.activityBA,mostCurrent);
try {
 BA.debugLineNum = 149;BA.debugLine="Sub Quit_Click";
Debug.ShouldStop(1048576);
 BA.debugLineNum = 150;BA.debugLine="ExitApplication";
Debug.ShouldStop(2097152);
anywheresoftware.b4a.keywords.Common.ExitApplication();
 BA.debugLineNum = 151;BA.debugLine="End Sub";
Debug.ShouldStop(4194304);
return "";
}
catch (Exception e) {
			Debug.ErrorCaught(e);
			throw e;
		} 
finally {
			Debug.PopSubsStack();
		}}
public static String  _slidemenu_click(Object _item) throws Exception{
		Debug.PushSubsStack("SlideMenu_Click (main) ","main",0,mostCurrent.activityBA,mostCurrent);
try {
Debug.locals.put("Item", _item);
 BA.debugLineNum = 210;BA.debugLine="Sub SlideMenu_Click(Item As Object)";
Debug.ShouldStop(131072);
 BA.debugLineNum = 212;BA.debugLine="Select Item";
Debug.ShouldStop(524288);
switch (BA.switchObjectToInt(_item,(Object)(1),(Object)(2),(Object)(3),(Object)(4),(Object)(5),(Object)(6),(Object)(7))) {
case 0:
 BA.debugLineNum = 214;BA.debugLine="web_Click";
Debug.ShouldStop(2097152);
_web_click();
 break;
case 1:
 BA.debugLineNum = 216;BA.debugLine="cp_Click";
Debug.ShouldStop(8388608);
_cp_click();
 break;
case 2:
 BA.debugLineNum = 218;BA.debugLine="storetodatabase_Click";
Debug.ShouldStop(33554432);
_storetodatabase_click();
 break;
case 3:
 BA.debugLineNum = 220;BA.debugLine="query_Click";
Debug.ShouldStop(134217728);
_query_click();
 break;
case 4:
 BA.debugLineNum = 222;BA.debugLine="upload_Click";
Debug.ShouldStop(536870912);
_upload_click();
 break;
case 5:
 BA.debugLineNum = 224;BA.debugLine="about_Click";
Debug.ShouldStop(-2147483648);
_about_click();
 break;
case 6:
 BA.debugLineNum = 226;BA.debugLine="ExitApplication";
Debug.ShouldStop(2);
anywheresoftware.b4a.keywords.Common.ExitApplication();
 break;
}
;
 BA.debugLineNum = 228;BA.debugLine="End Sub";
Debug.ShouldStop(8);
return "";
}
catch (Exception e) {
			Debug.ErrorCaught(e);
			throw e;
		} 
finally {
			Debug.PopSubsStack();
		}}
public static String  _storetodatabase_click() throws Exception{
		Debug.PushSubsStack("storetodatabase_Click (main) ","main",0,mostCurrent.activityBA,mostCurrent);
try {
int _exist = 0;
long _now = 0L;
String _time = "";
anywheresoftware.b4a.sql.SQL.CursorWrapper _cursor1 = null;
int _i = 0;
 BA.debugLineNum = 179;BA.debugLine="Sub storetodatabase_Click";
Debug.ShouldStop(262144);
 BA.debugLineNum = 180;BA.debugLine="Dim exist=0 As Int '默认不存在";
Debug.ShouldStop(524288);
_exist = (int) (0);Debug.locals.put("exist", _exist);Debug.locals.put("exist", _exist);
 BA.debugLineNum = 181;BA.debugLine="Dim now As Long";
Debug.ShouldStop(1048576);
_now = 0L;Debug.locals.put("now", _now);
 BA.debugLineNum = 182;BA.debugLine="Dim time As String";
Debug.ShouldStop(2097152);
_time = "";Debug.locals.put("time", _time);
 BA.debugLineNum = 183;BA.debugLine="now = DateTime.now";
Debug.ShouldStop(4194304);
_now = anywheresoftware.b4a.keywords.Common.DateTime.getNow();Debug.locals.put("now", _now);
 BA.debugLineNum = 184;BA.debugLine="time=DateTime.GetYear(now)&\"/\"&DateTime.GetMonth(now)&\"/\"&DateTime.GetDayOfMonth(now)&\"/\"&DateTime.GetHour(now)&\"/\"&DateTime.GetMinute(now)&\"/\"";
Debug.ShouldStop(8388608);
_time = BA.NumberToString(anywheresoftware.b4a.keywords.Common.DateTime.GetYear(_now))+"/"+BA.NumberToString(anywheresoftware.b4a.keywords.Common.DateTime.GetMonth(_now))+"/"+BA.NumberToString(anywheresoftware.b4a.keywords.Common.DateTime.GetDayOfMonth(_now))+"/"+BA.NumberToString(anywheresoftware.b4a.keywords.Common.DateTime.GetHour(_now))+"/"+BA.NumberToString(anywheresoftware.b4a.keywords.Common.DateTime.GetMinute(_now))+"/";Debug.locals.put("time", _time);
 BA.debugLineNum = 185;BA.debugLine="Dim Cursor1 As Cursor";
Debug.ShouldStop(16777216);
_cursor1 = new anywheresoftware.b4a.sql.SQL.CursorWrapper();Debug.locals.put("Cursor1", _cursor1);
 BA.debugLineNum = 186;BA.debugLine="Cursor1 = SQL1.ExecQuery(\"SELECT title FROM book\")";
Debug.ShouldStop(33554432);
_cursor1.setObject((android.database.Cursor)(_sql1.ExecQuery("SELECT title FROM book")));
 BA.debugLineNum = 187;BA.debugLine="For i = 0 To Cursor1.RowCount - 1";
Debug.ShouldStop(67108864);
{
final int step140 = 1;
final int limit140 = (int) (_cursor1.getRowCount()-1);
for (_i = (int) (0); (step140 > 0 && _i <= limit140) || (step140 < 0 && _i >= limit140); _i = ((int)(0 + _i + step140))) {
Debug.locals.put("i", _i);
 BA.debugLineNum = 188;BA.debugLine="Cursor1.Position = i";
Debug.ShouldStop(134217728);
_cursor1.setPosition(_i);
 BA.debugLineNum = 189;BA.debugLine="If Cursor1.GetString(\"title\")=bookname Then";
Debug.ShouldStop(268435456);
if ((_cursor1.GetString("title")).equals(mostCurrent._bookname)) { 
 BA.debugLineNum = 190;BA.debugLine="exist=1";
Debug.ShouldStop(536870912);
_exist = (int) (1);Debug.locals.put("exist", _exist);
 BA.debugLineNum = 191;BA.debugLine="ToastMessageShow(\"该图书已扫描过。\",False)";
Debug.ShouldStop(1073741824);
anywheresoftware.b4a.keywords.Common.ToastMessageShow("该图书已扫描过。",anywheresoftware.b4a.keywords.Common.False);
 };
 }
}Debug.locals.put("i", _i);
;
 BA.debugLineNum = 194;BA.debugLine="Cursor1.Close";
Debug.ShouldStop(2);
_cursor1.Close();
 BA.debugLineNum = 195;BA.debugLine="If exist=0 Then";
Debug.ShouldStop(4);
if (_exist==0) { 
 BA.debugLineNum = 196;BA.debugLine="SQL1.ExecNonQuery2(\"INSERT INTO book VALUES(?, ?, ?, ?, ?, ?, ?)\", Array As Object(bookname, bookprice, bookpublisher, result, bookpubdate, time, booksummary  ))";
Debug.ShouldStop(8);
_sql1.ExecNonQuery2("INSERT INTO book VALUES(?, ?, ?, ?, ?, ?, ?)",anywheresoftware.b4a.keywords.Common.ArrayToList(new Object[]{(Object)(mostCurrent._bookname),(Object)(mostCurrent._bookprice),(Object)(mostCurrent._bookpublisher),(Object)(_result),(Object)(mostCurrent._bookpubdate),(Object)(_time),(Object)(mostCurrent._booksummary)}));
 BA.debugLineNum = 197;BA.debugLine="ToastMessageShow(\"已存储\",False)";
Debug.ShouldStop(16);
anywheresoftware.b4a.keywords.Common.ToastMessageShow("已存储",anywheresoftware.b4a.keywords.Common.False);
 };
 BA.debugLineNum = 199;BA.debugLine="End Sub";
Debug.ShouldStop(64);
return "";
}
catch (Exception e) {
			Debug.ErrorCaught(e);
			throw e;
		} 
finally {
			Debug.PopSubsStack();
		}}
public static String  _upload_click() throws Exception{
		Debug.PushSubsStack("upload_Click (main) ","main",0,mostCurrent.activityBA,mostCurrent);
try {
long _now = 0L;
String _time = "";
 BA.debugLineNum = 167;BA.debugLine="Sub upload_Click";
Debug.ShouldStop(64);
 BA.debugLineNum = 169;BA.debugLine="ProgressDialogShow(\"上传中...\")";
Debug.ShouldStop(256);
anywheresoftware.b4a.keywords.Common.ProgressDialogShow(mostCurrent.activityBA,"上传中...");
 BA.debugLineNum = 170;BA.debugLine="Dim now As Long";
Debug.ShouldStop(512);
_now = 0L;Debug.locals.put("now", _now);
 BA.debugLineNum = 171;BA.debugLine="Dim time As String";
Debug.ShouldStop(1024);
_time = "";Debug.locals.put("time", _time);
 BA.debugLineNum = 172;BA.debugLine="now = DateTime.now";
Debug.ShouldStop(2048);
_now = anywheresoftware.b4a.keywords.Common.DateTime.getNow();Debug.locals.put("now", _now);
 BA.debugLineNum = 173;BA.debugLine="time=DateTime.GetYear(now)&\"/\"&DateTime.GetMonth(now)&\"/\"&DateTime.GetDayOfMonth(now)&\"/\"&DateTime.GetHour(now)&\"/\"&DateTime.GetMinute(now)&\"/\"";
Debug.ShouldStop(4096);
_time = BA.NumberToString(anywheresoftware.b4a.keywords.Common.DateTime.GetYear(_now))+"/"+BA.NumberToString(anywheresoftware.b4a.keywords.Common.DateTime.GetMonth(_now))+"/"+BA.NumberToString(anywheresoftware.b4a.keywords.Common.DateTime.GetDayOfMonth(_now))+"/"+BA.NumberToString(anywheresoftware.b4a.keywords.Common.DateTime.GetHour(_now))+"/"+BA.NumberToString(anywheresoftware.b4a.keywords.Common.DateTime.GetMinute(_now))+"/";Debug.locals.put("time", _time);
 BA.debugLineNum = 174;BA.debugLine="Log(time)";
Debug.ShouldStop(8192);
anywheresoftware.b4a.keywords.Common.Log(_time);
 BA.debugLineNum = 175;BA.debugLine="job1.Initialize(\"Job1\",Me)";
Debug.ShouldStop(16384);
mostCurrent._job1._initialize(processBA,"Job1",main.getObject());
 BA.debugLineNum = 176;BA.debugLine="job1.PostString(\"https://bottle-bookjnrain.rhcloud.com/login\",\"username=admin&password=admin&isbn=\"&result&\"&time=\"&time)";
Debug.ShouldStop(32768);
mostCurrent._job1._poststring("https://bottle-bookjnrain.rhcloud.com/login","username=admin&password=admin&isbn="+_result+"&time="+_time);
 BA.debugLineNum = 177;BA.debugLine="End Sub";
Debug.ShouldStop(65536);
return "";
}
catch (Exception e) {
			Debug.ErrorCaught(e);
			throw e;
		} 
finally {
			Debug.PopSubsStack();
		}}
public static String  _web_click() throws Exception{
		Debug.PushSubsStack("web_Click (main) ","main",0,mostCurrent.activityBA,mostCurrent);
try {
 BA.debugLineNum = 157;BA.debugLine="Sub web_Click";
Debug.ShouldStop(268435456);
 BA.debugLineNum = 158;BA.debugLine="StartActivity(web)";
Debug.ShouldStop(536870912);
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(mostCurrent._web.getObject()));
 BA.debugLineNum = 159;BA.debugLine="End Sub";
Debug.ShouldStop(1073741824);
return "";
}
catch (Exception e) {
			Debug.ErrorCaught(e);
			throw e;
		} 
finally {
			Debug.PopSubsStack();
		}}
}
