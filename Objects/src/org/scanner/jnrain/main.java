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
public static anywheresoftware.b4a.sql.SQL _sql1 = null;
public static anywheresoftware.b4a.sql.SQL.CursorWrapper _cursor1 = null;
public static String _result = "";
public ice.zxing.b4aZXingLib _zx = null;
public anywheresoftware.b4a.objects.ButtonWrapper _button1 = null;
public anywheresoftware.b4a.objects.LabelWrapper _label1 = null;
public anywheresoftware.b4a.objects.LabelWrapper _label2 = null;
public static String _bookname = "";
public static int _bookprice = 0;
public static long _bookcode = 0L;
public anywheresoftware.b4a.objects.ButtonWrapper _button2 = null;
public anywheresoftware.b4a.objects.ImageViewWrapper _imageview1 = null;
public anywheresoftware.b4a.objects.LabelWrapper _label3 = null;
public b4a.util.BClipboard _cc = null;
public org.scanner.jnrain.about _about = null;

public static boolean isAnyActivityVisible() {
    boolean vis = false;
vis = vis | (main.mostCurrent != null);
vis = vis | (about.mostCurrent != null);
return vis;}
public static String  _about_click() throws Exception{
 //BA.debugLineNum = 98;BA.debugLine="Sub about_Click";
 //BA.debugLineNum = 100;BA.debugLine="StartActivity(about)";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(mostCurrent._about.getObject()));
 //BA.debugLineNum = 101;BA.debugLine="End Sub";
return "";
}
public static String  _activity_create(boolean _firsttime) throws Exception{
 //BA.debugLineNum = 37;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 39;BA.debugLine="Activity.LoadLayout(\"scanner\")";
mostCurrent._activity.LoadLayout("scanner",mostCurrent.activityBA);
 //BA.debugLineNum = 40;BA.debugLine="File.Copy(File.DirAssets,\"book.db\",File.DirInternal,\"book.db\")";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"book.db",anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"book.db");
 //BA.debugLineNum = 42;BA.debugLine="If SQL1.IsInitialized = False Then";
if (_sql1.IsInitialized()==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 43;BA.debugLine="SQL1.Initialize(File.DirInternal, \"book.db\", False)";
_sql1.Initialize(anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"book.db",anywheresoftware.b4a.keywords.Common.False);
 };
 //BA.debugLineNum = 45;BA.debugLine="Activity.AddMenuItem(\"复制到剪贴板\",\"cp\")";
mostCurrent._activity.AddMenuItem("复制到剪贴板","cp");
 //BA.debugLineNum = 46;BA.debugLine="Activity.AddMenuItem(\"关于\",\"about\")";
mostCurrent._activity.AddMenuItem("关于","about");
 //BA.debugLineNum = 47;BA.debugLine="Activity.AddMenuItem(\"退出\",\"Quit\")";
mostCurrent._activity.AddMenuItem("退出","Quit");
 //BA.debugLineNum = 50;BA.debugLine="End Sub";
return "";
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 62;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
 //BA.debugLineNum = 64;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 58;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 60;BA.debugLine="End Sub";
return "";
}
public static String  _button1_click() throws Exception{
 //BA.debugLineNum = 70;BA.debugLine="Sub Button1_Click";
 //BA.debugLineNum = 71;BA.debugLine="zx.BeginScan(\"myzx\")";
mostCurrent._zx.BeginScan(mostCurrent.activityBA,"myzx");
 //BA.debugLineNum = 72;BA.debugLine="End Sub";
return "";
}
public static String  _button2_click() throws Exception{
int _i = 0;
 //BA.debugLineNum = 74;BA.debugLine="Sub Button2_Click";
 //BA.debugLineNum = 75;BA.debugLine="If result=1 Then";
if ((_result).equals(BA.NumberToString(1))) { 
 //BA.debugLineNum = 76;BA.debugLine="ToastMessageShow (\"请先扫描。\", False)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow("请先扫描。",anywheresoftware.b4a.keywords.Common.False);
 };
 //BA.debugLineNum = 78;BA.debugLine="Cursor1 = SQL1.ExecQuery(\"SELECT * FROM book\")";
_cursor1.setObject((android.database.Cursor)(_sql1.ExecQuery("SELECT * FROM book")));
 //BA.debugLineNum = 79;BA.debugLine="For i = 0 To Cursor1.rowcount - 1";
{
final int step44 = 1;
final int limit44 = (int) (_cursor1.getRowCount()-1);
for (_i = (int) (0); (step44 > 0 && _i <= limit44) || (step44 < 0 && _i >= limit44); _i = ((int)(0 + _i + step44))) {
 //BA.debugLineNum = 80;BA.debugLine="Cursor1.Position = i";
_cursor1.setPosition(_i);
 //BA.debugLineNum = 81;BA.debugLine="bookname=Cursor1.GetString(\"name\")";
mostCurrent._bookname = _cursor1.GetString("name");
 //BA.debugLineNum = 82;BA.debugLine="bookcode=Cursor1.GetLong(\"code\")";
_bookcode = _cursor1.GetLong("code");
 //BA.debugLineNum = 83;BA.debugLine="bookprice=Cursor1.GetInt(\"price\")";
_bookprice = _cursor1.GetInt("price");
 //BA.debugLineNum = 84;BA.debugLine="If bookcode = result Then";
if (_bookcode==(double)(Double.parseDouble(_result))) { 
 //BA.debugLineNum = 85;BA.debugLine="Label2.Text=\"条码是：\"&bookcode&CRLF&\"书名为：\"&bookname&CRLF&\"价格为：\"&bookprice";
mostCurrent._label2.setText((Object)("条码是："+BA.NumberToString(_bookcode)+anywheresoftware.b4a.keywords.Common.CRLF+"书名为："+mostCurrent._bookname+anywheresoftware.b4a.keywords.Common.CRLF+"价格为："+BA.NumberToString(_bookprice)));
 //BA.debugLineNum = 86;BA.debugLine="Exit";
if (true) break;
 };
 //BA.debugLineNum = 88;BA.debugLine="If i=Cursor1.rowcount - 1 AND bookcode <> result Then";
if (_i==_cursor1.getRowCount()-1 && _bookcode!=(double)(Double.parseDouble(_result))) { 
 //BA.debugLineNum = 89;BA.debugLine="Label2.Text=\"没有此书\"";
mostCurrent._label2.setText((Object)("没有此书"));
 };
 }
};
 //BA.debugLineNum = 92;BA.debugLine="End Sub";
return "";
}
public static String  _cp_click() throws Exception{
 //BA.debugLineNum = 103;BA.debugLine="Sub cp_Click";
 //BA.debugLineNum = 104;BA.debugLine="CC.setText(result)";
mostCurrent._cc.setText(mostCurrent.activityBA,_result);
 //BA.debugLineNum = 105;BA.debugLine="End Sub";
return "";
}

public static void initializeProcessGlobals() {
    
    if (main.processGlobalsRun == false) {
	    main.processGlobalsRun = true;
		try {
		        main._process_globals();
about._process_globals();
		
        } catch (Exception e) {
			throw new RuntimeException(e);
		}
    }
}public static String  _globals() throws Exception{
 //BA.debugLineNum = 20;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 23;BA.debugLine="Dim zx As Zxing_B4A";
mostCurrent._zx = new ice.zxing.b4aZXingLib();
 //BA.debugLineNum = 25;BA.debugLine="Dim Button1 As Button";
mostCurrent._button1 = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 26;BA.debugLine="Dim Label1 As Label";
mostCurrent._label1 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 27;BA.debugLine="Dim Label2 As Label";
mostCurrent._label2 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 28;BA.debugLine="Dim bookname As String";
mostCurrent._bookname = "";
 //BA.debugLineNum = 29;BA.debugLine="Dim bookprice As Int";
_bookprice = 0;
 //BA.debugLineNum = 30;BA.debugLine="Dim bookcode=0 As Long";
_bookcode = (long) (0);
 //BA.debugLineNum = 31;BA.debugLine="Dim Button2 As Button";
mostCurrent._button2 = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 32;BA.debugLine="Dim ImageView1 As ImageView";
mostCurrent._imageview1 = new anywheresoftware.b4a.objects.ImageViewWrapper();
 //BA.debugLineNum = 33;BA.debugLine="Dim Label3 As Label";
mostCurrent._label3 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 34;BA.debugLine="Dim CC As BClipboard";
mostCurrent._cc = new b4a.util.BClipboard();
 //BA.debugLineNum = 35;BA.debugLine="End Sub";
return "";
}
public static String  _myzx_result(String _atype,String _values) throws Exception{
 //BA.debugLineNum = 52;BA.debugLine="Sub myzx_result(atype As String,Values As String)";
 //BA.debugLineNum = 54;BA.debugLine="result=Values";
_result = _values;
 //BA.debugLineNum = 55;BA.debugLine="Label2.Text = \"结果为：\"&result";
mostCurrent._label2.setText((Object)("结果为："+_result));
 //BA.debugLineNum = 56;BA.debugLine="End Sub";
return "";
}
public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 12;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 15;BA.debugLine="Dim SQL1 As SQL";
_sql1 = new anywheresoftware.b4a.sql.SQL();
 //BA.debugLineNum = 16;BA.debugLine="Dim Cursor1 As Cursor";
_cursor1 = new anywheresoftware.b4a.sql.SQL.CursorWrapper();
 //BA.debugLineNum = 17;BA.debugLine="Dim result=1 As String";
_result = BA.NumberToString(1);
 //BA.debugLineNum = 18;BA.debugLine="End Sub";
return "";
}
public static String  _quit_click() throws Exception{
 //BA.debugLineNum = 94;BA.debugLine="Sub Quit_Click";
 //BA.debugLineNum = 95;BA.debugLine="ExitApplication";
anywheresoftware.b4a.keywords.Common.ExitApplication();
 //BA.debugLineNum = 96;BA.debugLine="End Sub";
return "";
}
}
