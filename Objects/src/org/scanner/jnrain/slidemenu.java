package org.scanner.jnrain;

import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.B4AClass;
import anywheresoftware.b4a.BALayout;
import anywheresoftware.b4a.debug.*;

public class slidemenu extends B4AClass.ImplB4AClass implements BA.SubDelegator{
    private static java.util.HashMap<String, java.lang.reflect.Method> htSubs;
    private void innerInitialize(BA _ba) throws Exception {
        if (ba == null) {
            ba = new BA(_ba, this, htSubs, "org.scanner.jnrain.slidemenu");
            if (htSubs == null) {
                ba.loadHtSubs(this.getClass());
                htSubs = ba.htSubs;
            }
            if (ba.getClass().getName().endsWith("ShellBA")) {
			    ba.raiseEvent2(null, true, "CREATE", true, "org.scanner.jnrain.slidemenu",
                    ba);
		    }
        }
        ba.raiseEvent2(null, true, "class_globals", false);
    }

 public anywheresoftware.b4a.keywords.Common __c = null;
public anywheresoftware.b4a.objects.PanelWrapper _mslidepanel = null;
public anywheresoftware.b4a.objects.PanelWrapper _mbackpanel = null;
public Object _mmodule = null;
public String _meventname = "";
public anywheresoftware.b4a.objects.ListViewWrapper _mlistview = null;
public anywheresoftware.b4a.objects.AnimationWrapper _minanimation = null;
public anywheresoftware.b4a.objects.AnimationWrapper _moutanimation = null;
public anywheresoftware.b4a.samples.httputils2.httputils2service _httputils2service = null;
public org.scanner.jnrain.main _main = null;
public org.scanner.jnrain.about _about = null;
public org.scanner.jnrain.web _web = null;
  public Object[] GetGlobals() {
		return new Object[] {"about",Debug.moduleToString(org.scanner.jnrain.about.class),"HttpUtils2Service",_httputils2service,"Main",Debug.moduleToString(org.scanner.jnrain.main.class),"mBackPanel",_mbackpanel,"mEventName",_meventname,"mInAnimation",_minanimation,"mListView",_mlistview,"mModule",_mmodule,"mOutAnimation",_moutanimation,"mSlidePanel",_mslidepanel,"web",Debug.moduleToString(org.scanner.jnrain.web.class)};
}
public static class _actionitem{
public boolean IsInitialized;
public String Text;
public anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper Image;
public Object Value;
public void Initialize() {
IsInitialized = true;
Text = "";
Image = new anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper();
Value = new Object();
}
@Override
		public String toString() {
			return BA.TypeToString(this, false);
		}}
public String  _additem(String _text,anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper _image,Object _returnvalue) throws Exception{
		Debug.PushSubsStack("AddItem (slidemenu) ","slidemenu",3,ba,this);
try {
org.scanner.jnrain.slidemenu._actionitem _item = null;
Debug.locals.put("Text", _text);
Debug.locals.put("Image", _image);
Debug.locals.put("ReturnValue", _returnvalue);
 BA.debugLineNum = 62;BA.debugLine="Public Sub AddItem(Text As String, Image As Bitmap, ReturnValue As Object)";
Debug.ShouldStop(536870912);
 BA.debugLineNum = 63;BA.debugLine="Dim item As ActionItem";
Debug.ShouldStop(1073741824);
_item = new org.scanner.jnrain.slidemenu._actionitem();Debug.locals.put("item", _item);
 BA.debugLineNum = 64;BA.debugLine="item.Initialize";
Debug.ShouldStop(-2147483648);
_item.Initialize();
 BA.debugLineNum = 65;BA.debugLine="item.Text = Text";
Debug.ShouldStop(1);
_item.Text = _text;Debug.locals.put("item", _item);
 BA.debugLineNum = 66;BA.debugLine="item.Image = Image";
Debug.ShouldStop(2);
_item.Image = _image;Debug.locals.put("item", _item);
 BA.debugLineNum = 67;BA.debugLine="item.Value = ReturnValue";
Debug.ShouldStop(4);
_item.Value = _returnvalue;Debug.locals.put("item", _item);
 BA.debugLineNum = 69;BA.debugLine="If Not(Image.IsInitialized) Then";
Debug.ShouldStop(16);
if (__c.Not(_image.IsInitialized())) { 
 BA.debugLineNum = 70;BA.debugLine="mListView.AddTwoLinesAndBitmap2(Text, \"\", Null, ReturnValue)";
Debug.ShouldStop(32);
_mlistview.AddTwoLinesAndBitmap2(_text,"",(android.graphics.Bitmap)(__c.Null),_returnvalue);
 }else {
 BA.debugLineNum = 72;BA.debugLine="mListView.AddTwoLinesAndBitmap2(Text, \"\", Image, ReturnValue)";
Debug.ShouldStop(128);
_mlistview.AddTwoLinesAndBitmap2(_text,"",(android.graphics.Bitmap)(_image.getObject()),_returnvalue);
 };
 BA.debugLineNum = 74;BA.debugLine="End Sub";
Debug.ShouldStop(512);
return "";
}
catch (Exception e) {
			Debug.ErrorCaught(e);
			throw e;
		} 
finally {
			Debug.PopSubsStack();
		}}
public String  _class_globals() throws Exception{
 //BA.debugLineNum = 7;BA.debugLine="Private Sub Class_Globals";
 //BA.debugLineNum = 8;BA.debugLine="Type ActionItem (Text As String, Image As Bitmap, Value As Object)";
;
 //BA.debugLineNum = 10;BA.debugLine="Private mSlidePanel As Panel";
_mslidepanel = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 11;BA.debugLine="Private mBackPanel As Panel";
_mbackpanel = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 13;BA.debugLine="Private mModule As Object";
_mmodule = new Object();
 //BA.debugLineNum = 14;BA.debugLine="Private mEventName As String";
_meventname = "";
 //BA.debugLineNum = 16;BA.debugLine="Private mListView As ListView";
_mlistview = new anywheresoftware.b4a.objects.ListViewWrapper();
 //BA.debugLineNum = 18;BA.debugLine="Private mInAnimation As Animation";
_minanimation = new anywheresoftware.b4a.objects.AnimationWrapper();
 //BA.debugLineNum = 19;BA.debugLine="Private mOutAnimation As Animation";
_moutanimation = new anywheresoftware.b4a.objects.AnimationWrapper();
 //BA.debugLineNum = 20;BA.debugLine="End Sub";
return "";
}
public String  _hide() throws Exception{
		Debug.PushSubsStack("Hide (slidemenu) ","slidemenu",3,ba,this);
try {
 BA.debugLineNum = 90;BA.debugLine="Public Sub Hide";
Debug.ShouldStop(33554432);
 BA.debugLineNum = 91;BA.debugLine="If isVisible = False Then Return";
Debug.ShouldStop(67108864);
if (_isvisible()==__c.False) { 
if (true) return "";};
 BA.debugLineNum = 93;BA.debugLine="mBackPanel.Left = -mBackPanel.Width";
Debug.ShouldStop(268435456);
_mbackpanel.setLeft((int) (-_mbackpanel.getWidth()));
 BA.debugLineNum = 94;BA.debugLine="mSlidePanel.Left = -mSlidePanel.Width";
Debug.ShouldStop(536870912);
_mslidepanel.setLeft((int) (-_mslidepanel.getWidth()));
 BA.debugLineNum = 95;BA.debugLine="mOutAnimation.Start(mSlidePanel)";
Debug.ShouldStop(1073741824);
_moutanimation.Start((android.view.View)(_mslidepanel.getObject()));
 BA.debugLineNum = 96;BA.debugLine="End Sub";
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
public String  _initialize(anywheresoftware.b4a.BA _ba,anywheresoftware.b4a.objects.ActivityWrapper _activity,Object _module,String _eventname,int _top,int _width) throws Exception{
innerInitialize(_ba);
		Debug.PushSubsStack("Initialize (slidemenu) ","slidemenu",3,ba,this);
try {
Debug.locals.put("ba", _ba);
Debug.locals.put("Activity", _activity);
Debug.locals.put("Module", _module);
Debug.locals.put("EventName", _eventname);
Debug.locals.put("Top", _top);
Debug.locals.put("Width", _width);
 BA.debugLineNum = 28;BA.debugLine="Sub Initialize(Activity As Activity, Module As Object, EventName As String, Top As Int, Width As Int)";
Debug.ShouldStop(134217728);
 BA.debugLineNum = 29;BA.debugLine="mModule = Module";
Debug.ShouldStop(268435456);
_mmodule = _module;
 BA.debugLineNum = 30;BA.debugLine="mEventName = EventName";
Debug.ShouldStop(536870912);
_meventname = _eventname;
 BA.debugLineNum = 32;BA.debugLine="mSlidePanel.Initialize(\"mSlidePanel\")";
Debug.ShouldStop(-2147483648);
_mslidepanel.Initialize(ba,"mSlidePanel");
 BA.debugLineNum = 34;BA.debugLine="mListView.Initialize(\"mListView\")";
Debug.ShouldStop(2);
_mlistview.Initialize(ba,"mListView");
 BA.debugLineNum = 35;BA.debugLine="mListView.TwoLinesAndBitmap.SecondLabel.Visible = False";
Debug.ShouldStop(4);
_mlistview.getTwoLinesAndBitmap().SecondLabel.setVisible(__c.False);
 BA.debugLineNum = 36;BA.debugLine="mListView.TwoLinesAndBitmap.ItemHeight = 50dip";
Debug.ShouldStop(8);
_mlistview.getTwoLinesAndBitmap().setItemHeight(__c.DipToCurrent((int) (50)));
 BA.debugLineNum = 37;BA.debugLine="mListView.TwoLinesAndBitmap.Label.Gravity = Gravity.CENTER_VERTICAL";
Debug.ShouldStop(16);
_mlistview.getTwoLinesAndBitmap().Label.setGravity(__c.Gravity.CENTER_VERTICAL);
 BA.debugLineNum = 38;BA.debugLine="mListView.TwoLinesAndBitmap.Label.Height = mListView.TwoLinesAndBitmap.ItemHeight";
Debug.ShouldStop(32);
_mlistview.getTwoLinesAndBitmap().Label.setHeight(_mlistview.getTwoLinesAndBitmap().getItemHeight());
 BA.debugLineNum = 39;BA.debugLine="mListView.TwoLinesAndBitmap.Label.Top = 0";
Debug.ShouldStop(64);
_mlistview.getTwoLinesAndBitmap().Label.setTop((int) (0));
 BA.debugLineNum = 40;BA.debugLine="mListView.TwoLinesAndBitmap.ImageView.SetLayout(13dip, 13dip, 24dip, 24dip)";
Debug.ShouldStop(128);
_mlistview.getTwoLinesAndBitmap().ImageView.SetLayout(__c.DipToCurrent((int) (13)),__c.DipToCurrent((int) (13)),__c.DipToCurrent((int) (24)),__c.DipToCurrent((int) (24)));
 BA.debugLineNum = 41;BA.debugLine="mListView.Color = Colors.Black";
Debug.ShouldStop(256);
_mlistview.setColor(__c.Colors.Black);
 BA.debugLineNum = 43;BA.debugLine="mInAnimation.InitializeTranslate(\"\", -Width, 0, 0, 0)";
Debug.ShouldStop(1024);
_minanimation.InitializeTranslate(ba,"",(float) (-_width),(float) (0),(float) (0),(float) (0));
 BA.debugLineNum = 44;BA.debugLine="mInAnimation.Duration = 200";
Debug.ShouldStop(2048);
_minanimation.setDuration((long) (200));
 BA.debugLineNum = 45;BA.debugLine="mOutAnimation.InitializeTranslate(\"Out\", Width, 0, 0, 0)";
Debug.ShouldStop(4096);
_moutanimation.InitializeTranslate(ba,"Out",(float) (_width),(float) (0),(float) (0),(float) (0));
 BA.debugLineNum = 46;BA.debugLine="mOutAnimation.Duration = 200";
Debug.ShouldStop(8192);
_moutanimation.setDuration((long) (200));
 BA.debugLineNum = 48;BA.debugLine="Activity.AddView(mSlidePanel, 0, Top, Width, 100%y - Top)";
Debug.ShouldStop(32768);
_activity.AddView((android.view.View)(_mslidepanel.getObject()),(int) (0),_top,_width,(int) (__c.PerYToCurrent((float) (100),ba)-_top));
 BA.debugLineNum = 50;BA.debugLine="mBackPanel.Initialize(\"mBackPanel\")";
Debug.ShouldStop(131072);
_mbackpanel.Initialize(ba,"mBackPanel");
 BA.debugLineNum = 51;BA.debugLine="mBackPanel.Color = Colors.Transparent";
Debug.ShouldStop(262144);
_mbackpanel.setColor(__c.Colors.Transparent);
 BA.debugLineNum = 52;BA.debugLine="Activity.AddView(mBackPanel, -100%x, 0, 100%x, 100%y)";
Debug.ShouldStop(524288);
_activity.AddView((android.view.View)(_mbackpanel.getObject()),(int) (-__c.PerXToCurrent((float) (100),ba)),(int) (0),__c.PerXToCurrent((float) (100),ba),__c.PerYToCurrent((float) (100),ba));
 BA.debugLineNum = 54;BA.debugLine="mSlidePanel.AddView(mListView, 0, 0, mSlidePanel.Width, mSlidePanel.Height)";
Debug.ShouldStop(2097152);
_mslidepanel.AddView((android.view.View)(_mlistview.getObject()),(int) (0),(int) (0),_mslidepanel.getWidth(),_mslidepanel.getHeight());
 BA.debugLineNum = 55;BA.debugLine="mSlidePanel.Visible = False";
Debug.ShouldStop(4194304);
_mslidepanel.setVisible(__c.False);
 BA.debugLineNum = 56;BA.debugLine="End Sub";
Debug.ShouldStop(8388608);
return "";
}
catch (Exception e) {
			Debug.ErrorCaught(e);
			throw e;
		} 
finally {
			Debug.PopSubsStack();
		}}
public boolean  _isvisible() throws Exception{
		Debug.PushSubsStack("isVisible (slidemenu) ","slidemenu",3,ba,this);
try {
 BA.debugLineNum = 118;BA.debugLine="Public Sub isVisible As Boolean";
Debug.ShouldStop(2097152);
 BA.debugLineNum = 119;BA.debugLine="Return mSlidePanel.Visible";
Debug.ShouldStop(4194304);
if (true) return _mslidepanel.getVisible();
 BA.debugLineNum = 120;BA.debugLine="End Sub";
Debug.ShouldStop(8388608);
return false;
}
catch (Exception e) {
			Debug.ErrorCaught(e);
			throw e;
		} 
finally {
			Debug.PopSubsStack();
		}}
public String  _mbackpanel_touch(int _action,float _x,float _y) throws Exception{
		Debug.PushSubsStack("mBackPanel_Touch (slidemenu) ","slidemenu",3,ba,this);
try {
Debug.locals.put("Action", _action);
Debug.locals.put("X", _x);
Debug.locals.put("Y", _y);
 BA.debugLineNum = 102;BA.debugLine="Private Sub mBackPanel_Touch (Action As Int, X As Float, Y As Float)";
Debug.ShouldStop(32);
 BA.debugLineNum = 103;BA.debugLine="If Action = 1 Then";
Debug.ShouldStop(64);
if (_action==1) { 
 BA.debugLineNum = 104;BA.debugLine="Hide";
Debug.ShouldStop(128);
_hide();
 };
 BA.debugLineNum = 106;BA.debugLine="End Sub";
Debug.ShouldStop(512);
return "";
}
catch (Exception e) {
			Debug.ErrorCaught(e);
			throw e;
		} 
finally {
			Debug.PopSubsStack();
		}}
public String  _mlistview_itemclick(int _position,Object _value) throws Exception{
		Debug.PushSubsStack("mListView_ItemClick (slidemenu) ","slidemenu",3,ba,this);
try {
String _subname = "";
Debug.locals.put("Position", _position);
Debug.locals.put("Value", _value);
 BA.debugLineNum = 108;BA.debugLine="Private Sub mListView_ItemClick (Position As Int, Value As Object)";
Debug.ShouldStop(2048);
 BA.debugLineNum = 109;BA.debugLine="Dim subname As String";
Debug.ShouldStop(4096);
_subname = "";Debug.locals.put("subname", _subname);
 BA.debugLineNum = 110;BA.debugLine="Hide";
Debug.ShouldStop(8192);
_hide();
 BA.debugLineNum = 111;BA.debugLine="subname = mEventName & \"_Click\"";
Debug.ShouldStop(16384);
_subname = _meventname+"_Click";Debug.locals.put("subname", _subname);
 BA.debugLineNum = 112;BA.debugLine="If SubExists(mModule, subname) Then";
Debug.ShouldStop(32768);
if (__c.SubExists(ba,_mmodule,_subname)) { 
 BA.debugLineNum = 113;BA.debugLine="CallSub2(mModule, subname, Value)";
Debug.ShouldStop(65536);
__c.CallSubNew2(ba,_mmodule,_subname,_value);
 };
 BA.debugLineNum = 115;BA.debugLine="End Sub";
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
public String  _out_animationend() throws Exception{
		Debug.PushSubsStack("Out_AnimationEnd (slidemenu) ","slidemenu",3,ba,this);
try {
 BA.debugLineNum = 98;BA.debugLine="Private Sub Out_AnimationEnd";
Debug.ShouldStop(2);
 BA.debugLineNum = 99;BA.debugLine="mSlidePanel.Visible = False";
Debug.ShouldStop(4);
_mslidepanel.setVisible(__c.False);
 BA.debugLineNum = 100;BA.debugLine="End Sub";
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
public String  _show() throws Exception{
		Debug.PushSubsStack("Show (slidemenu) ","slidemenu",3,ba,this);
try {
 BA.debugLineNum = 77;BA.debugLine="Public Sub Show";
Debug.ShouldStop(4096);
 BA.debugLineNum = 78;BA.debugLine="If isVisible = True Then Return";
Debug.ShouldStop(8192);
if (_isvisible()==__c.True) { 
if (true) return "";};
 BA.debugLineNum = 80;BA.debugLine="mBackPanel.BringToFront";
Debug.ShouldStop(32768);
_mbackpanel.BringToFront();
 BA.debugLineNum = 81;BA.debugLine="mSlidePanel.BringToFront";
Debug.ShouldStop(65536);
_mslidepanel.BringToFront();
 BA.debugLineNum = 82;BA.debugLine="mBackPanel.Left = 0";
Debug.ShouldStop(131072);
_mbackpanel.setLeft((int) (0));
 BA.debugLineNum = 83;BA.debugLine="mSlidePanel.Left = 0";
Debug.ShouldStop(262144);
_mslidepanel.setLeft((int) (0));
 BA.debugLineNum = 85;BA.debugLine="mSlidePanel.Visible = True";
Debug.ShouldStop(1048576);
_mslidepanel.setVisible(__c.True);
 BA.debugLineNum = 86;BA.debugLine="mInAnimation.Start(mSlidePanel)";
Debug.ShouldStop(2097152);
_minanimation.Start((android.view.View)(_mslidepanel.getObject()));
 BA.debugLineNum = 87;BA.debugLine="End Sub";
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
public Object callSub(String sub, Object sender, Object[] args) throws Exception {
ba.sharedProcessBA.sender = sender;
return BA.SubDelegator.SubNotFound;
}
}
