package org.scanner.jnrain.designerscripts;
import anywheresoftware.b4a.objects.TextViewWrapper;
import anywheresoftware.b4a.objects.ImageViewWrapper;
import anywheresoftware.b4a.BA;


public class LS_query{

public static void LS_general(java.util.HashMap<String, anywheresoftware.b4a.objects.ViewWrapper<?>> views, int width, int height, float scale) {
anywheresoftware.b4a.keywords.LayoutBuilder.setScaleRate(0.3);
//BA.debugLineNum = 3;BA.debugLine="ListView1.Width=100%x"[query/General script]
views.get("listview1").setWidth((int)((100d / 100 * width)));
//BA.debugLineNum = 4;BA.debugLine="ListView1.Height=85%y"[query/General script]
views.get("listview1").setHeight((int)((85d / 100 * height)));
//BA.debugLineNum = 5;BA.debugLine="Button1.Top=85%y"[query/General script]
views.get("button1").setTop((int)((85d / 100 * height)));

}
}