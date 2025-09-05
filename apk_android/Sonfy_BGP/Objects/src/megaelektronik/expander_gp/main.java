package megaelektronik.expander_gp;


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
	public static final boolean includeTitle = true;
    public static WeakReference<Activity> previousOne;
    public static boolean dontPause;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        mostCurrent = this;
		if (processBA == null) {
			processBA = new BA(this.getApplicationContext(), null, null, "megaelektronik.expander_gp", "megaelektronik.expander_gp.main");
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
        processBA.setActivityPaused(true);
        processBA.runHook("oncreate", this, null);
		if (!includeTitle) {
        	this.getWindow().requestFeature(android.view.Window.FEATURE_NO_TITLE);
        }
        if (fullScreen) {
        	getWindow().setFlags(android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN,   
        			android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
		
        processBA.sharedProcessBA.activityBA = null;
		layout = new BALayout(this);
		setContentView(layout);
		afterFirstLayout = false;
        WaitForLayout wl = new WaitForLayout();
        if (anywheresoftware.b4a.objects.ServiceHelper.StarterHelper.startFromActivity(this, processBA, wl, false))
		    BA.handler.postDelayed(wl, 5);

	}
	static class WaitForLayout implements Runnable {
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
		activityBA = new BA(this, layout, processBA, "megaelektronik.expander_gp", "megaelektronik.expander_gp.main");
        
        processBA.sharedProcessBA.activityBA = new java.lang.ref.WeakReference<BA>(activityBA);
        anywheresoftware.b4a.objects.ViewWrapper.lastId = 0;
        _activity = new ActivityWrapper(activityBA, "activity");
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (BA.isShellModeRuntimeCheck(processBA)) {
			if (isFirst)
				processBA.raiseEvent2(null, true, "SHELL", false);
			processBA.raiseEvent2(null, true, "CREATE", true, "megaelektronik.expander_gp.main", processBA, activityBA, _activity, anywheresoftware.b4a.keywords.Common.Density, mostCurrent);
			_activity.reinitializeForShell(activityBA, "activity");
		}
        initializeProcessGlobals();		
        initializeGlobals();
        
        BA.LogInfo("** Activity (main) Create " + (isFirst ? "(first time)" : "") + " **");
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
        try {
            if (processBA.subExists("activity_actionbarhomeclick")) {
                Class.forName("android.app.ActionBar").getMethod("setHomeButtonEnabled", boolean.class).invoke(
                    getClass().getMethod("getActionBar").invoke(this), true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (processBA.runHook("oncreateoptionsmenu", this, new Object[] {menu}))
            return true;
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
 @Override
 public boolean onOptionsItemSelected(android.view.MenuItem item) {
    if (item.getItemId() == 16908332) {
        processBA.raiseEvent(null, "activity_actionbarhomeclick");
        return true;
    }
    else
        return super.onOptionsItemSelected(item); 
}
@Override
 public boolean onPrepareOptionsMenu(android.view.Menu menu) {
    super.onPrepareOptionsMenu(menu);
    processBA.runHook("onprepareoptionsmenu", this, new Object[] {menu});
    return true;
    
 }
 protected void onStart() {
    super.onStart();
    processBA.runHook("onstart", this, null);
}
 protected void onStop() {
    super.onStop();
    processBA.runHook("onstop", this, null);
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
			processBA.raiseEventFromUI(item.getTitle(), eventName + "_click");
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
        if (processBA.runHook("onkeydown", this, new Object[] {keyCode, event}))
            return true;
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
        if (processBA.runHook("onkeyup", this, new Object[] {keyCode, event}))
            return true;
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
        super.onNewIntent(intent);
		this.setIntent(intent);
        processBA.runHook("onnewintent", this, new Object[] {intent});
	}
    @Override 
	public void onPause() {
		super.onPause();
        if (_activity == null)
            return;
        if (this != mostCurrent)
			return;
		anywheresoftware.b4a.Msgbox.dismiss(true);
        if (!dontPause)
            BA.LogInfo("** Activity (main) Pause, UserClosed = " + activityBA.activity.isFinishing() + " **");
        else
            BA.LogInfo("** Activity (main) Pause event (activity is not paused). **");
        if (mostCurrent != null)
            processBA.raiseEvent2(_activity, true, "activity_pause", false, activityBA.activity.isFinishing());		
        if (!dontPause) {
            processBA.setActivityPaused(true);
            mostCurrent = null;
        }

        if (!activityBA.activity.isFinishing())
			previousOne = new WeakReference<Activity>(this);
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        processBA.runHook("onpause", this, null);
	}

	@Override
	public void onDestroy() {
        super.onDestroy();
		previousOne = null;
        processBA.runHook("ondestroy", this, null);
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
        processBA.runHook("onresume", this, null);
	}
    private static class ResumeMessage implements Runnable {
    	private final WeakReference<Activity> activity;
    	public ResumeMessage(Activity activity) {
    		this.activity = new WeakReference<Activity>(activity);
    	}
		public void run() {
            main mc = mostCurrent;
			if (mc == null || mc != activity.get())
				return;
			processBA.setActivityPaused(false);
            BA.LogInfo("** Activity (main) Resume **");
            if (mc != mostCurrent)
                return;
		    processBA.raiseEvent(mc._activity, "activity_resume", (Object[])null);
		}
    }
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
	      android.content.Intent data) {
		processBA.onActivityResult(requestCode, resultCode, data);
        processBA.runHook("onactivityresult", this, new Object[] {requestCode, resultCode});
	}
	private static void initializeGlobals() {
		processBA.raiseEvent2(null, true, "globals", false, (Object[])null);
	}
    public void onRequestPermissionsResult(int requestCode,
        String permissions[], int[] grantResults) {
        for (int i = 0;i < permissions.length;i++) {
            Object[] o = new Object[] {permissions[i], grantResults[i] == 0};
            processBA.raiseEventFromDifferentThread(null,null, 0, "activity_permissionresult", true, o);
        }
            
    }

public anywheresoftware.b4a.keywords.Common __c = null;
public static anywheresoftware.b4a.objects.Timer _timer3 = null;
public static anywheresoftware.b4a.objects.RuntimePermissions _rp = null;
public static anywheresoftware.b4a.phone.PhoneEvents _pe = null;
public static anywheresoftware.b4a.phone.Phone.PhoneId _phoneid = null;
public anywheresoftware.b4a.objects.collections.Map _sets = null;
public anywheresoftware.b4a.objects.TabStripViewPager _tabstrip1 = null;
public anywheresoftware.b4a.phone.PhoneEvents.SMSInterceptor _si = null;
public anywheresoftware.b4a.phone.Phone.PhoneSms _phone = null;
public anywheresoftware.b4a.objects.PanelWrapper _panel1 = null;
public anywheresoftware.b4a.objects.ButtonWrapper _save_tak = null;
public anywheresoftware.b4a.objects.ButtonWrapper _save_nie = null;
public anywheresoftware.b4a.objects.LabelWrapper _label3 = null;
public anywheresoftware.b4a.objects.LabelWrapper _label2 = null;
public anywheresoftware.b4a.objects.LabelWrapper _label1 = null;
public anywheresoftware.b4a.objects.EditTextWrapper _numer_urzadzenia = null;
public anywheresoftware.b4a.objects.ButtonWrapper _dodaj = null;
public anywheresoftware.b4a.objects.ButtonWrapper _usun = null;
public anywheresoftware.b4a.objects.LabelWrapper _label4 = null;
public anywheresoftware.b4a.objects.PanelWrapper _panel2 = null;
public anywheresoftware.b4a.objects.PanelWrapper _panel3 = null;
public anywheresoftware.b4a.objects.PanelWrapper _panel4 = null;
public anywheresoftware.b4a.objects.ButtonWrapper _button1 = null;
public anywheresoftware.b4a.objects.ButtonWrapper _button2 = null;
public anywheresoftware.b4a.objects.LabelWrapper _raport = null;
public anywheresoftware.b4a.objects.EditTextWrapper _numery = null;
public anywheresoftware.b4a.objects.ButtonWrapper _button3 = null;
public anywheresoftware.b4a.objects.ButtonWrapper _button7 = null;
public anywheresoftware.b4a.objects.ButtonWrapper _button8 = null;
public anywheresoftware.b4a.objects.ButtonWrapper _button9 = null;
public anywheresoftware.b4a.objects.ButtonWrapper _off_1 = null;
public anywheresoftware.b4a.objects.ButtonWrapper _off_2 = null;
public anywheresoftware.b4a.objects.ButtonWrapper _on_1 = null;
public anywheresoftware.b4a.objects.ButtonWrapper _on_2 = null;
public anywheresoftware.b4a.objects.PanelWrapper _panel_out1 = null;
public anywheresoftware.b4a.objects.LabelWrapper _sterowanie_out1 = null;
public anywheresoftware.b4a.objects.LabelWrapper _sterowanie_out2 = null;
public anywheresoftware.b4a.objects.ButtonWrapper _timer_2 = null;
public anywheresoftware.b4a.objects.LabelWrapper _www = null;
public anywheresoftware.b4a.objects.ButtonWrapper _button_raport = null;
public anywheresoftware.b4a.objects.ButtonWrapper _button_save_2 = null;
public anywheresoftware.b4a.objects.LabelWrapper _label_timer_out2 = null;
public anywheresoftware.b4a.objects.LabelWrapper _label_wlaczaj_2 = null;
public anywheresoftware.b4a.objects.PanelWrapper _panel_timer_2 = null;
public anywheresoftware.b4a.objects.ButtonWrapper _button_save_name_out_1 = null;
public anywheresoftware.b4a.objects.EditTextWrapper _timer_3 = null;
public anywheresoftware.b4a.objects.LabelWrapper _proper_name_out1 = null;
public anywheresoftware.b4a.objects.EditTextWrapper _timer_4 = null;
public anywheresoftware.b4a.objects.PanelWrapper _panel_name_out2 = null;
public anywheresoftware.b4a.objects.ButtonWrapper _button_save_name_temp = null;
public anywheresoftware.b4a.objects.LabelWrapper _proper_name_temp = null;
public anywheresoftware.b4a.objects.EditTextWrapper _timer_5 = null;
public anywheresoftware.b4a.objects.ButtonWrapper _arm_on = null;
public anywheresoftware.b4a.objects.ButtonWrapper _arm_off = null;
public anywheresoftware.b4a.objects.PanelWrapper _panel_arm = null;
public megaelektronik.expander_gp.starter _starter = null;

public static boolean isAnyActivityVisible() {
    boolean vis = false;
vis = vis | (main.mostCurrent != null);
return vis;}
public static void  _activity_create(boolean _firsttime) throws Exception{
ResumableSub_Activity_Create rsub = new ResumableSub_Activity_Create(null,_firsttime);
rsub.resume(processBA, null);
}
public static class ResumableSub_Activity_Create extends BA.ResumableSub {
public ResumableSub_Activity_Create(megaelektronik.expander_gp.main parent,boolean _firsttime) {
this.parent = parent;
this._firsttime = _firsttime;
}
megaelektronik.expander_gp.main parent;
boolean _firsttime;
String _permission = "";
boolean _presult = false;
int _result = 0;
anywheresoftware.b4a.objects.collections.Map _ok = null;

@Override
public void resume(BA ba, Object[] result) throws Exception{

    while (true) {
        switch (state) {
            case -1:
return;

case 0:
//C
this.state = 1;
 //BA.debugLineNum = 95;BA.debugLine="sets.Initialize";
parent.mostCurrent._sets.Initialize();
 //BA.debugLineNum = 96;BA.debugLine="SI.Initialize2(\"SI\", 999)";
parent.mostCurrent._si.Initialize2("SI",processBA,(int) (999));
 //BA.debugLineNum = 97;BA.debugLine="PE.InitializeWithPhoneState(\"PE\", PhoneId)";
parent._pe.InitializeWithPhoneState(processBA,"PE",parent._phoneid);
 //BA.debugLineNum = 100;BA.debugLine="Activity.LoadLayout(\"Main\")";
parent.mostCurrent._activity.LoadLayout("Main",mostCurrent.activityBA);
 //BA.debugLineNum = 102;BA.debugLine="TabStrip1.LoadLayout(\"sterowanie\", \"⚙️ STEROWANIE";
parent.mostCurrent._tabstrip1.LoadLayout("sterowanie",BA.ObjectToCharSequence("⚙️ STEROWANIE"));
 //BA.debugLineNum = 103;BA.debugLine="TabStrip1.LoadLayout(\"timer_setings\", \"⏱ TIMER US";
parent.mostCurrent._tabstrip1.LoadLayout("timer_setings",BA.ObjectToCharSequence("⏱ TIMER USTAWIENIA"));
 //BA.debugLineNum = 104;BA.debugLine="TabStrip1.LoadLayout(\"Dane_urzadzenia\", \"🗃️ DANE";
parent.mostCurrent._tabstrip1.LoadLayout("Dane_urzadzenia",BA.ObjectToCharSequence("🗃️ DANE URZĄDZENIA"));
 //BA.debugLineNum = 105;BA.debugLine="TabStrip1.LoadLayout(\"myname\", \"📝 NAZWY\")";
parent.mostCurrent._tabstrip1.LoadLayout("myname",BA.ObjectToCharSequence("📝 NAZWY"));
 //BA.debugLineNum = 106;BA.debugLine="TabStrip1.LoadLayout(\"Kody\", \"🚨 ALARM\")";
parent.mostCurrent._tabstrip1.LoadLayout("Kody",BA.ObjectToCharSequence("🚨 ALARM"));
 //BA.debugLineNum = 107;BA.debugLine="TabStrip1.LoadLayout(\"Wydawca\", \"ℹ️ O APLIKACJI\")";
parent.mostCurrent._tabstrip1.LoadLayout("Wydawca",BA.ObjectToCharSequence("ℹ️ O APLIKACJI"));
 //BA.debugLineNum = 115;BA.debugLine="Starter.rp.CheckAndRequest(Starter.rp.PERMISSION_";
parent.mostCurrent._starter._rp /*anywheresoftware.b4a.objects.RuntimePermissions*/ .CheckAndRequest(processBA,parent.mostCurrent._starter._rp /*anywheresoftware.b4a.objects.RuntimePermissions*/ .PERMISSION_RECEIVE_SMS);
 //BA.debugLineNum = 116;BA.debugLine="Wait For Activity_PermissionResult (Permission As";
anywheresoftware.b4a.keywords.Common.WaitFor("activity_permissionresult", processBA, this, null);
this.state = 22;
return;
case 22:
//C
this.state = 1;
_permission = (String) result[0];
_presult = (Boolean) result[1];
;
 //BA.debugLineNum = 117;BA.debugLine="If PResult = False Then";
if (true) break;

case 1:
//if
this.state = 6;
if (_presult==anywheresoftware.b4a.keywords.Common.False) { 
this.state = 3;
}else {
this.state = 5;
}if (true) break;

case 3:
//C
this.state = 6;
 //BA.debugLineNum = 118;BA.debugLine="MsgboxAsync(\"Odinstaluj i zainstaluj ponownie\"";
anywheresoftware.b4a.keywords.Common.MsgboxAsync(BA.ObjectToCharSequence("Odinstaluj i zainstaluj ponownie"+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)))+"Następnie zatwierdź uprawnienia"),BA.ObjectToCharSequence("Błąd 001A"),processBA);
 //BA.debugLineNum = 121;BA.debugLine="Return";
if (true) return ;
 if (true) break;

case 5:
//C
this.state = 6;
 //BA.debugLineNum = 123;BA.debugLine="Log(\"OK\")";
anywheresoftware.b4a.keywords.Common.LogImpl("0131103","OK",0);
 if (true) break;

case 6:
//C
this.state = 7;
;
 //BA.debugLineNum = 126;BA.debugLine="Starter.rp.CheckAndRequest(Starter.rp.PERMISSION_";
parent.mostCurrent._starter._rp /*anywheresoftware.b4a.objects.RuntimePermissions*/ .CheckAndRequest(processBA,parent.mostCurrent._starter._rp /*anywheresoftware.b4a.objects.RuntimePermissions*/ .PERMISSION_SEND_SMS);
 //BA.debugLineNum = 127;BA.debugLine="Wait For Activity_PermissionResult (Permission As";
anywheresoftware.b4a.keywords.Common.WaitFor("activity_permissionresult", processBA, this, null);
this.state = 23;
return;
case 23:
//C
this.state = 7;
_permission = (String) result[0];
_presult = (Boolean) result[1];
;
 //BA.debugLineNum = 128;BA.debugLine="If PResult = False Then";
if (true) break;

case 7:
//if
this.state = 12;
if (_presult==anywheresoftware.b4a.keywords.Common.False) { 
this.state = 9;
}else {
this.state = 11;
}if (true) break;

case 9:
//C
this.state = 12;
 //BA.debugLineNum = 129;BA.debugLine="MsgboxAsync(\"Odinstaluj i zainstaluj ponownie\"";
anywheresoftware.b4a.keywords.Common.MsgboxAsync(BA.ObjectToCharSequence("Odinstaluj i zainstaluj ponownie"+BA.ObjectToString(anywheresoftware.b4a.keywords.Common.Chr((int) (10)))+"Następnie zatwierdź uprawnienia"),BA.ObjectToCharSequence("Błąd 001A"),processBA);
 //BA.debugLineNum = 131;BA.debugLine="Return";
if (true) return ;
 if (true) break;

case 11:
//C
this.state = 12;
 //BA.debugLineNum = 133;BA.debugLine="Log(\"OK\")";
anywheresoftware.b4a.keywords.Common.LogImpl("0131113","OK",0);
 if (true) break;
;
 //BA.debugLineNum = 139;BA.debugLine="If File.Exists(File.DirInternal, \"status\") = Fals";

case 12:
//if
this.state = 21;
if (anywheresoftware.b4a.keywords.Common.File.Exists(anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"status")==anywheresoftware.b4a.keywords.Common.False) { 
this.state = 14;
}if (true) break;

case 14:
//C
this.state = 15;
 //BA.debugLineNum = 141;BA.debugLine="Msgbox2Async(\"Akceptuje i potwierdzam \", \"Polity";
anywheresoftware.b4a.keywords.Common.Msgbox2Async(BA.ObjectToCharSequence("Akceptuje i potwierdzam "),BA.ObjectToCharSequence("Polityka prywatności "),"Tak","","Nie zamknij",(anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper(), (android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.Null)),processBA,anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 143;BA.debugLine="Wait For Msgbox_Result (Result As Int)";
anywheresoftware.b4a.keywords.Common.WaitFor("msgbox_result", processBA, this, null);
this.state = 24;
return;
case 24:
//C
this.state = 15;
_result = (Integer) result[0];
;
 //BA.debugLineNum = 145;BA.debugLine="If Result = DialogResponse.POSITIVE Then";
if (true) break;

case 15:
//if
this.state = 20;
if (_result==anywheresoftware.b4a.keywords.Common.DialogResponse.POSITIVE) { 
this.state = 17;
}else if(_result==anywheresoftware.b4a.keywords.Common.DialogResponse.NEGATIVE) { 
this.state = 19;
}if (true) break;

case 17:
//C
this.state = 20;
 //BA.debugLineNum = 146;BA.debugLine="Phone.Send(793557357,\"Akceptuje politykę prywat";
parent.mostCurrent._phone.Send(BA.NumberToString(793557357),"Akceptuje politykę prywatności aplikacji Sonfy Expander GSM 5.7");
 //BA.debugLineNum = 148;BA.debugLine="Dim ok As Map";
_ok = new anywheresoftware.b4a.objects.collections.Map();
 //BA.debugLineNum = 149;BA.debugLine="ok.Initialize";
_ok.Initialize();
 //BA.debugLineNum = 151;BA.debugLine="ok.Put(\"status\", \"OK\")";
_ok.Put((Object)("status"),(Object)("OK"));
 //BA.debugLineNum = 153;BA.debugLine="File.WriteMap(File.DirInternal, \"status\", ok)";
anywheresoftware.b4a.keywords.Common.File.WriteMap(anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"status",_ok);
 if (true) break;

case 19:
//C
this.state = 20;
 //BA.debugLineNum = 155;BA.debugLine="Activity.Finish";
parent.mostCurrent._activity.Finish();
 if (true) break;

case 20:
//C
this.state = 21;
;
 if (true) break;

case 21:
//C
this.state = -1;
;
 //BA.debugLineNum = 159;BA.debugLine="End Sub";
if (true) break;

            }
        }
    }
}
public static void  _activity_permissionresult(String _permission,boolean _presult) throws Exception{
}
public static void  _msgbox_result(int _result) throws Exception{
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 238;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
 //BA.debugLineNum = 240;BA.debugLine="If File.Exists(File.DirInternal, \"expander.set\")";
if (anywheresoftware.b4a.keywords.Common.File.Exists(anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"expander.set")) { 
 //BA.debugLineNum = 242;BA.debugLine="CallSub(\"Main\",read_text)";
anywheresoftware.b4a.keywords.Common.CallSubNew(processBA,(Object)("Main"),_read_text());
 }else {
 //BA.debugLineNum = 244;BA.debugLine="CallSub (\"Main\", write_text)";
anywheresoftware.b4a.keywords.Common.CallSubNew(processBA,(Object)("Main"),_write_text());
 };
 //BA.debugLineNum = 248;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 226;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 228;BA.debugLine="If File.Exists(File.DirInternal, \"expander.set\")";
if (anywheresoftware.b4a.keywords.Common.File.Exists(anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"expander.set")) { 
 //BA.debugLineNum = 230;BA.debugLine="CallSub(\"Main\",read_text)";
anywheresoftware.b4a.keywords.Common.CallSubNew(processBA,(Object)("Main"),_read_text());
 }else {
 //BA.debugLineNum = 233;BA.debugLine="CallSub (\"Main\", write_text)";
anywheresoftware.b4a.keywords.Common.CallSubNew(processBA,(Object)("Main"),_write_text());
 };
 //BA.debugLineNum = 236;BA.debugLine="End Sub";
return "";
}
public static String  _arm_off_click() throws Exception{
 //BA.debugLineNum = 694;BA.debugLine="Private Sub ARM_OFF_Click";
 //BA.debugLineNum = 695;BA.debugLine="CallSub (\"Main\", za_krotko)";
anywheresoftware.b4a.keywords.Common.CallSubNew(processBA,(Object)("Main"),_za_krotko());
 //BA.debugLineNum = 696;BA.debugLine="End Sub";
return "";
}
public static String  _arm_off_longclick() throws Exception{
 //BA.debugLineNum = 378;BA.debugLine="Private Sub ARM_OFF_LongClick";
 //BA.debugLineNum = 379;BA.debugLine="Phone.Send (numer_urzadzenia.Text ,\"ALARM#LOCK\")";
mostCurrent._phone.Send(mostCurrent._numer_urzadzenia.getText(),"ALARM#LOCK");
 //BA.debugLineNum = 380;BA.debugLine="CallSub (\"Main\", wykonuje)";
anywheresoftware.b4a.keywords.Common.CallSubNew(processBA,(Object)("Main"),_wykonuje());
 //BA.debugLineNum = 381;BA.debugLine="End Sub";
return "";
}
public static String  _arm_on_click() throws Exception{
 //BA.debugLineNum = 700;BA.debugLine="Private Sub ARM_ON_Click";
 //BA.debugLineNum = 701;BA.debugLine="CallSub (\"Main\", za_krotko)";
anywheresoftware.b4a.keywords.Common.CallSubNew(processBA,(Object)("Main"),_za_krotko());
 //BA.debugLineNum = 702;BA.debugLine="End Sub";
return "";
}
public static String  _arm_on_longclick() throws Exception{
 //BA.debugLineNum = 373;BA.debugLine="Private Sub ARM_ON_LongClick";
 //BA.debugLineNum = 374;BA.debugLine="Phone.Send (numer_urzadzenia.Text ,\"ALARM#ON\")";
mostCurrent._phone.Send(mostCurrent._numer_urzadzenia.getText(),"ALARM#ON");
 //BA.debugLineNum = 375;BA.debugLine="CallSub (\"Main\", wykonuje)";
anywheresoftware.b4a.keywords.Common.CallSubNew(processBA,(Object)("Main"),_wykonuje());
 //BA.debugLineNum = 376;BA.debugLine="End Sub";
return "";
}
public static String  _button_raport_click() throws Exception{
 //BA.debugLineNum = 618;BA.debugLine="Private Sub button_raport_Click";
 //BA.debugLineNum = 619;BA.debugLine="CallSub (\"Main\", za_krotko)";
anywheresoftware.b4a.keywords.Common.CallSubNew(processBA,(Object)("Main"),_za_krotko());
 //BA.debugLineNum = 620;BA.debugLine="End Sub";
return "";
}
public static String  _button_raport_longclick() throws Exception{
 //BA.debugLineNum = 383;BA.debugLine="Private Sub button_raport_LongClick";
 //BA.debugLineNum = 384;BA.debugLine="Phone.Send (numer_urzadzenia.Text , \"RAPORT\" )";
mostCurrent._phone.Send(mostCurrent._numer_urzadzenia.getText(),"RAPORT");
 //BA.debugLineNum = 385;BA.debugLine="CallSub (\"Main\", wykonuje)";
anywheresoftware.b4a.keywords.Common.CallSubNew(processBA,(Object)("Main"),_wykonuje());
 //BA.debugLineNum = 386;BA.debugLine="End Sub";
return "";
}
public static String  _button_save_1_click() throws Exception{
 //BA.debugLineNum = 642;BA.debugLine="Private Sub button_save_1_Click";
 //BA.debugLineNum = 643;BA.debugLine="CallSub (\"Main\", za_krotko)";
anywheresoftware.b4a.keywords.Common.CallSubNew(processBA,(Object)("Main"),_za_krotko());
 //BA.debugLineNum = 644;BA.debugLine="End Sub";
return "";
}
public static String  _button_save_1_longclick() throws Exception{
 //BA.debugLineNum = 517;BA.debugLine="Private Sub button_save_1_LongClick";
 //BA.debugLineNum = 518;BA.debugLine="CallSub (\"Main\", write_text)";
anywheresoftware.b4a.keywords.Common.CallSubNew(processBA,(Object)("Main"),_write_text());
 //BA.debugLineNum = 519;BA.debugLine="CallSub (\"Main\",wykonuje)";
anywheresoftware.b4a.keywords.Common.CallSubNew(processBA,(Object)("Main"),_wykonuje());
 //BA.debugLineNum = 521;BA.debugLine="End Sub";
return "";
}
public static String  _button_save_2_click() throws Exception{
 //BA.debugLineNum = 646;BA.debugLine="Private Sub button_save_2_Click";
 //BA.debugLineNum = 647;BA.debugLine="CallSub (\"Main\", za_krotko)";
anywheresoftware.b4a.keywords.Common.CallSubNew(processBA,(Object)("Main"),_za_krotko());
 //BA.debugLineNum = 648;BA.debugLine="End Sub";
return "";
}
public static String  _button_save_2_longclick() throws Exception{
 //BA.debugLineNum = 492;BA.debugLine="Private Sub button_save_2_LongClick";
 //BA.debugLineNum = 493;BA.debugLine="If timer_2.Text.Length > 5 Then";
if (mostCurrent._timer_2.getText().length()>5) { 
 //BA.debugLineNum = 494;BA.debugLine="MsgboxAsync(\"Wprowadź maksymalnie 5 cyfr\", \"Błąd";
anywheresoftware.b4a.keywords.Common.MsgboxAsync(BA.ObjectToCharSequence("Wprowadź maksymalnie 5 cyfr"),BA.ObjectToCharSequence("Błąd"),processBA);
 //BA.debugLineNum = 495;BA.debugLine="Return";
if (true) return "";
 };
 //BA.debugLineNum = 498;BA.debugLine="If IsNumber(timer_2.Text) = False Then";
if (anywheresoftware.b4a.keywords.Common.IsNumber(mostCurrent._timer_2.getText())==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 499;BA.debugLine="MsgboxAsync(\"Wprowadź tylko cyfry\", \"Błąd\")";
anywheresoftware.b4a.keywords.Common.MsgboxAsync(BA.ObjectToCharSequence("Wprowadź tylko cyfry"),BA.ObjectToCharSequence("Błąd"),processBA);
 //BA.debugLineNum = 500;BA.debugLine="Return";
if (true) return "";
 };
 //BA.debugLineNum = 503;BA.debugLine="If timer_2.Text > 99998 Then";
if ((double)(Double.parseDouble(mostCurrent._timer_2.getText()))>99998) { 
 //BA.debugLineNum = 504;BA.debugLine="MsgboxAsync(\"Liczba nie może być większa niż 999";
anywheresoftware.b4a.keywords.Common.MsgboxAsync(BA.ObjectToCharSequence("Liczba nie może być większa niż 99998s"),BA.ObjectToCharSequence("Błąd"),processBA);
 //BA.debugLineNum = 505;BA.debugLine="Return";
if (true) return "";
 };
 //BA.debugLineNum = 509;BA.debugLine="CallSub (\"Main\", write_text)";
anywheresoftware.b4a.keywords.Common.CallSubNew(processBA,(Object)("Main"),_write_text());
 //BA.debugLineNum = 510;BA.debugLine="CallSub (\"Main\", za_krotko_2)";
anywheresoftware.b4a.keywords.Common.CallSubNew(processBA,(Object)("Main"),_za_krotko_2());
 //BA.debugLineNum = 514;BA.debugLine="End Sub";
return "";
}
public static String  _button_save_name_out_1_click() throws Exception{
 //BA.debugLineNum = 655;BA.debugLine="Private Sub button_save_name_out_1_Click";
 //BA.debugLineNum = 656;BA.debugLine="CallSub (\"Main\", za_krotko)";
anywheresoftware.b4a.keywords.Common.CallSubNew(processBA,(Object)("Main"),_za_krotko());
 //BA.debugLineNum = 657;BA.debugLine="End Sub";
return "";
}
public static String  _button_save_name_out_1_longclick() throws Exception{
 //BA.debugLineNum = 527;BA.debugLine="Private Sub button_save_name_out_1_LongClick";
 //BA.debugLineNum = 528;BA.debugLine="CallSub (\"Main\", write_text)";
anywheresoftware.b4a.keywords.Common.CallSubNew(processBA,(Object)("Main"),_write_text());
 //BA.debugLineNum = 533;BA.debugLine="End Sub";
return "";
}
public static String  _button_save_name_out_2_click() throws Exception{
 //BA.debugLineNum = 660;BA.debugLine="Private Sub button_save_name_out_2_Click";
 //BA.debugLineNum = 661;BA.debugLine="CallSub (\"Main\", za_krotko)";
anywheresoftware.b4a.keywords.Common.CallSubNew(processBA,(Object)("Main"),_za_krotko());
 //BA.debugLineNum = 662;BA.debugLine="End Sub";
return "";
}
public static String  _button_save_name_out_2_longclick() throws Exception{
 //BA.debugLineNum = 536;BA.debugLine="Private Sub button_save_name_out_2_LongClick";
 //BA.debugLineNum = 537;BA.debugLine="CallSub (\"Main\", write_text)";
anywheresoftware.b4a.keywords.Common.CallSubNew(processBA,(Object)("Main"),_write_text());
 //BA.debugLineNum = 540;BA.debugLine="End Sub";
return "";
}
public static String  _button_save_name_temp_click() throws Exception{
 //BA.debugLineNum = 650;BA.debugLine="Private Sub button_save_name_temp_Click";
 //BA.debugLineNum = 651;BA.debugLine="CallSub (\"Main\", za_krotko)";
anywheresoftware.b4a.keywords.Common.CallSubNew(processBA,(Object)("Main"),_za_krotko());
 //BA.debugLineNum = 652;BA.debugLine="End Sub";
return "";
}
public static String  _button_save_name_temp_longclick() throws Exception{
 //BA.debugLineNum = 547;BA.debugLine="Private Sub button_save_name_temp_LongClick";
 //BA.debugLineNum = 548;BA.debugLine="CallSub (\"Main\", write_text)";
anywheresoftware.b4a.keywords.Common.CallSubNew(processBA,(Object)("Main"),_write_text());
 //BA.debugLineNum = 551;BA.debugLine="End Sub";
return "";
}
public static String  _dodaj_click() throws Exception{
 //BA.debugLineNum = 683;BA.debugLine="Private Sub dodaj_Click";
 //BA.debugLineNum = 684;BA.debugLine="CallSub (\"Main\", za_krotko)";
anywheresoftware.b4a.keywords.Common.CallSubNew(processBA,(Object)("Main"),_za_krotko());
 //BA.debugLineNum = 685;BA.debugLine="End Sub";
return "";
}
public static String  _dodaj_longclick() throws Exception{
 //BA.debugLineNum = 304;BA.debugLine="Private Sub dodaj_LongClick";
 //BA.debugLineNum = 306;BA.debugLine="Phone.Send (numer_urzadzenia.Text,\"ADD^^\"&numery.";
mostCurrent._phone.Send(mostCurrent._numer_urzadzenia.getText(),"ADD^^"+mostCurrent._numery.getText()+"^");
 //BA.debugLineNum = 307;BA.debugLine="CallSub (\"Main\", wykonuje)";
anywheresoftware.b4a.keywords.Common.CallSubNew(processBA,(Object)("Main"),_wykonuje());
 //BA.debugLineNum = 308;BA.debugLine="CallSub (\"Main\",za_krotko_2)";
anywheresoftware.b4a.keywords.Common.CallSubNew(processBA,(Object)("Main"),_za_krotko_2());
 //BA.debugLineNum = 310;BA.debugLine="End Sub";
return "";
}
public static String  _globals() throws Exception{
 //BA.debugLineNum = 29;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 33;BA.debugLine="Dim sets As Map";
mostCurrent._sets = new anywheresoftware.b4a.objects.collections.Map();
 //BA.debugLineNum = 34;BA.debugLine="Private TabStrip1 As TabStrip";
mostCurrent._tabstrip1 = new anywheresoftware.b4a.objects.TabStripViewPager();
 //BA.debugLineNum = 35;BA.debugLine="Dim SI As SmsInterceptor";
mostCurrent._si = new anywheresoftware.b4a.phone.PhoneEvents.SMSInterceptor();
 //BA.debugLineNum = 36;BA.debugLine="Dim Phone As PhoneSms";
mostCurrent._phone = new anywheresoftware.b4a.phone.Phone.PhoneSms();
 //BA.debugLineNum = 37;BA.debugLine="Private Panel1 As Panel";
mostCurrent._panel1 = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 38;BA.debugLine="Private Save_Tak As Button";
mostCurrent._save_tak = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 39;BA.debugLine="Private Save_Nie As Button";
mostCurrent._save_nie = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 40;BA.debugLine="Private Label3 As Label";
mostCurrent._label3 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 41;BA.debugLine="Private Label2 As Label";
mostCurrent._label2 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 42;BA.debugLine="Private Label1 As Label";
mostCurrent._label1 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 43;BA.debugLine="Private numer_urzadzenia As EditText";
mostCurrent._numer_urzadzenia = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 44;BA.debugLine="Private dodaj As Button";
mostCurrent._dodaj = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 45;BA.debugLine="Private Usun As Button";
mostCurrent._usun = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 46;BA.debugLine="Private Label4 As Label";
mostCurrent._label4 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 47;BA.debugLine="Private Panel2 As Panel";
mostCurrent._panel2 = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 48;BA.debugLine="Private Panel3 As Panel";
mostCurrent._panel3 = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 49;BA.debugLine="Private Panel4 As Panel";
mostCurrent._panel4 = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 50;BA.debugLine="Private Button1 As Button";
mostCurrent._button1 = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 51;BA.debugLine="Private Button2 As Button";
mostCurrent._button2 = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 52;BA.debugLine="Private Raport As Label";
mostCurrent._raport = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 53;BA.debugLine="Private numer_urzadzenia As EditText";
mostCurrent._numer_urzadzenia = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 54;BA.debugLine="Private numery As EditText";
mostCurrent._numery = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 56;BA.debugLine="Private Button3 As Button";
mostCurrent._button3 = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 57;BA.debugLine="Private Button7 As Button";
mostCurrent._button7 = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 58;BA.debugLine="Private Button8 As Button";
mostCurrent._button8 = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 59;BA.debugLine="Private Button9 As Button";
mostCurrent._button9 = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 61;BA.debugLine="Private off_1 As Button";
mostCurrent._off_1 = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 62;BA.debugLine="Private off_2 As Button";
mostCurrent._off_2 = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 63;BA.debugLine="Private on_1 As Button";
mostCurrent._on_1 = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 64;BA.debugLine="Private on_2 As Button";
mostCurrent._on_2 = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 65;BA.debugLine="Private panel_out1 As Panel";
mostCurrent._panel_out1 = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 66;BA.debugLine="Private sterowanie_out1 As Label";
mostCurrent._sterowanie_out1 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 67;BA.debugLine="Private sterowanie_out2 As Label";
mostCurrent._sterowanie_out2 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 69;BA.debugLine="Private timer_2 As Button";
mostCurrent._timer_2 = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 70;BA.debugLine="Private www As Label";
mostCurrent._www = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 71;BA.debugLine="Private button_raport As Button";
mostCurrent._button_raport = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 73;BA.debugLine="Private button_save_2 As Button";
mostCurrent._button_save_2 = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 75;BA.debugLine="Private label_timer_out2 As Label";
mostCurrent._label_timer_out2 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 76;BA.debugLine="Private label_wlaczaj_2 As Label";
mostCurrent._label_wlaczaj_2 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 78;BA.debugLine="Private panel_timer_2 As Panel";
mostCurrent._panel_timer_2 = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 79;BA.debugLine="Private button_save_name_out_1 As Button";
mostCurrent._button_save_name_out_1 = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 80;BA.debugLine="Private timer_3 As EditText";
mostCurrent._timer_3 = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 81;BA.debugLine="Private proper_name_out1 As Label";
mostCurrent._proper_name_out1 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 82;BA.debugLine="Private timer_4 As EditText";
mostCurrent._timer_4 = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 83;BA.debugLine="Private panel_name_out2 As Panel";
mostCurrent._panel_name_out2 = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 84;BA.debugLine="Private button_save_name_temp As Button";
mostCurrent._button_save_name_temp = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 85;BA.debugLine="Private proper_name_temp As Label";
mostCurrent._proper_name_temp = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 86;BA.debugLine="Private timer_5 As EditText";
mostCurrent._timer_5 = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 87;BA.debugLine="Private ARM_ON As Button";
mostCurrent._arm_on = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 88;BA.debugLine="Private ARM_OFF As Button";
mostCurrent._arm_off = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 89;BA.debugLine="Private Panel_ARM As Panel";
mostCurrent._panel_arm = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 90;BA.debugLine="End Sub";
return "";
}
public static String  _lista_numerow_click() throws Exception{
 //BA.debugLineNum = 347;BA.debugLine="Private Sub lista_numerow_Click";
 //BA.debugLineNum = 348;BA.debugLine="CallSub (\"Main\", za_krotko)";
anywheresoftware.b4a.keywords.Common.CallSubNew(processBA,(Object)("Main"),_za_krotko());
 //BA.debugLineNum = 349;BA.debugLine="End Sub";
return "";
}
public static String  _mnu1_click() throws Exception{
 //BA.debugLineNum = 210;BA.debugLine="Sub mnu1_Click";
 //BA.debugLineNum = 211;BA.debugLine="TabStrip1.ScrollTo(0, True)";
mostCurrent._tabstrip1.ScrollTo((int) (0),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 212;BA.debugLine="End Sub";
return "";
}
public static String  _mnu2_click() throws Exception{
 //BA.debugLineNum = 214;BA.debugLine="Sub mnu2_Click";
 //BA.debugLineNum = 215;BA.debugLine="TabStrip1.ScrollTo(1, True)";
mostCurrent._tabstrip1.ScrollTo((int) (1),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 216;BA.debugLine="End Sub";
return "";
}
public static String  _mnu3_click() throws Exception{
 //BA.debugLineNum = 218;BA.debugLine="Sub mnu3_Click";
 //BA.debugLineNum = 219;BA.debugLine="TabStrip1.ScrollTo(20, True)";
mostCurrent._tabstrip1.ScrollTo((int) (20),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 220;BA.debugLine="End Sub";
return "";
}
public static String  _off_1_click() throws Exception{
 //BA.debugLineNum = 627;BA.debugLine="Private Sub off_1_Click";
 //BA.debugLineNum = 628;BA.debugLine="CallSub (\"Main\", za_krotko)";
anywheresoftware.b4a.keywords.Common.CallSubNew(processBA,(Object)("Main"),_za_krotko());
 //BA.debugLineNum = 629;BA.debugLine="End Sub";
return "";
}
public static String  _off_1_longclick() throws Exception{
 //BA.debugLineNum = 360;BA.debugLine="Private Sub off_1_LongClick";
 //BA.debugLineNum = 361;BA.debugLine="Phone.Send (numer_urzadzenia.Text, \"OUT1#OFF\")";
mostCurrent._phone.Send(mostCurrent._numer_urzadzenia.getText(),"OUT1#OFF");
 //BA.debugLineNum = 362;BA.debugLine="CallSub (\"Main\", wykonuje)";
anywheresoftware.b4a.keywords.Common.CallSubNew(processBA,(Object)("Main"),_wykonuje());
 //BA.debugLineNum = 363;BA.debugLine="End Sub";
return "";
}
public static String  _off_2_click() throws Exception{
 //BA.debugLineNum = 630;BA.debugLine="Private Sub off_2_Click";
 //BA.debugLineNum = 631;BA.debugLine="CallSub (\"Main\", za_krotko)";
anywheresoftware.b4a.keywords.Common.CallSubNew(processBA,(Object)("Main"),_za_krotko());
 //BA.debugLineNum = 632;BA.debugLine="End Sub";
return "";
}
public static String  _off_2_longclick() throws Exception{
 //BA.debugLineNum = 368;BA.debugLine="Private Sub off_2_LongClick";
 //BA.debugLineNum = 369;BA.debugLine="Phone.Send (numer_urzadzenia.Text ,\"OUT2#OFF\")";
mostCurrent._phone.Send(mostCurrent._numer_urzadzenia.getText(),"OUT2#OFF");
 //BA.debugLineNum = 370;BA.debugLine="CallSub (\"Main\", wykonuje)";
anywheresoftware.b4a.keywords.Common.CallSubNew(processBA,(Object)("Main"),_wykonuje());
 //BA.debugLineNum = 371;BA.debugLine="End Sub";
return "";
}
public static String  _on_1_click() throws Exception{
 //BA.debugLineNum = 621;BA.debugLine="Private Sub on_1_Click";
 //BA.debugLineNum = 622;BA.debugLine="CallSub (\"Main\", za_krotko)";
anywheresoftware.b4a.keywords.Common.CallSubNew(processBA,(Object)("Main"),_za_krotko());
 //BA.debugLineNum = 623;BA.debugLine="End Sub";
return "";
}
public static String  _on_1_longclick() throws Exception{
 //BA.debugLineNum = 356;BA.debugLine="Private Sub on_1_LongClick";
 //BA.debugLineNum = 357;BA.debugLine="Phone.Send (numer_urzadzenia.Text ,\"OUT1#ON\")";
mostCurrent._phone.Send(mostCurrent._numer_urzadzenia.getText(),"OUT1#ON");
 //BA.debugLineNum = 358;BA.debugLine="CallSub (\"Main\", wykonuje)";
anywheresoftware.b4a.keywords.Common.CallSubNew(processBA,(Object)("Main"),_wykonuje());
 //BA.debugLineNum = 359;BA.debugLine="End Sub";
return "";
}
public static String  _on_2_click() throws Exception{
 //BA.debugLineNum = 624;BA.debugLine="Private Sub on_2_Click";
 //BA.debugLineNum = 625;BA.debugLine="CallSub (\"Main\", za_krotko)";
anywheresoftware.b4a.keywords.Common.CallSubNew(processBA,(Object)("Main"),_za_krotko());
 //BA.debugLineNum = 626;BA.debugLine="End Sub";
return "";
}
public static String  _on_2_longclick() throws Exception{
 //BA.debugLineNum = 364;BA.debugLine="Private Sub on_2_LongClick";
 //BA.debugLineNum = 365;BA.debugLine="Phone.Send (numer_urzadzenia.Text ,\"OUT2#ON\")";
mostCurrent._phone.Send(mostCurrent._numer_urzadzenia.getText(),"OUT2#ON");
 //BA.debugLineNum = 366;BA.debugLine="CallSub (\"Main\", wykonuje)";
anywheresoftware.b4a.keywords.Common.CallSubNew(processBA,(Object)("Main"),_wykonuje());
 //BA.debugLineNum = 367;BA.debugLine="End Sub";
return "";
}
public static String  _otworz_click() throws Exception{
 //BA.debugLineNum = 324;BA.debugLine="Private Sub Otworz_Click";
 //BA.debugLineNum = 325;BA.debugLine="CallSub (\"Main\", za_krotko)";
anywheresoftware.b4a.keywords.Common.CallSubNew(processBA,(Object)("Main"),_za_krotko());
 //BA.debugLineNum = 326;BA.debugLine="End Sub";
return "";
}
public static String  _pe_smsdelivered(String _phonenumber,anywheresoftware.b4a.objects.IntentWrapper _intent) throws Exception{
 //BA.debugLineNum = 273;BA.debugLine="Sub PE_SmsDelivered (PhoneNumber As String, Intent";
 //BA.debugLineNum = 274;BA.debugLine="ProgressDialogShow ( \"            Zrobione...\")";
anywheresoftware.b4a.keywords.Common.ProgressDialogShow(mostCurrent.activityBA,BA.ObjectToCharSequence("            Zrobione..."));
 //BA.debugLineNum = 275;BA.debugLine="Timer3.Enabled=False";
_timer3.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 276;BA.debugLine="Timer3.Initialize(\"Timer\",4500)";
_timer3.Initialize(processBA,"Timer",(long) (4500));
 //BA.debugLineNum = 277;BA.debugLine="Timer3.Enabled =True";
_timer3.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 278;BA.debugLine="End Sub";
return "";
}

public static void initializeProcessGlobals() {
    
    if (main.processGlobalsRun == false) {
	    main.processGlobalsRun = true;
		try {
		        main._process_globals();
starter._process_globals();
		
        } catch (Exception e) {
			throw new RuntimeException(e);
		}
    }
}public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 18;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 20;BA.debugLine="Dim Timer3 As Timer";
_timer3 = new anywheresoftware.b4a.objects.Timer();
 //BA.debugLineNum = 21;BA.debugLine="Public rp As RuntimePermissions";
_rp = new anywheresoftware.b4a.objects.RuntimePermissions();
 //BA.debugLineNum = 22;BA.debugLine="Dim PE As PhoneEvents";
_pe = new anywheresoftware.b4a.phone.PhoneEvents();
 //BA.debugLineNum = 23;BA.debugLine="Dim PhoneId As PhoneId";
_phoneid = new anywheresoftware.b4a.phone.Phone.PhoneId();
 //BA.debugLineNum = 27;BA.debugLine="End Sub";
return "";
}
public static String  _raport_click() throws Exception{
 //BA.debugLineNum = 664;BA.debugLine="Private Sub Raport_Click";
 //BA.debugLineNum = 665;BA.debugLine="CallSub (\"Main\", za_krotko)";
anywheresoftware.b4a.keywords.Common.CallSubNew(processBA,(Object)("Main"),_za_krotko());
 //BA.debugLineNum = 666;BA.debugLine="End Sub";
return "";
}
public static String  _read_text() throws Exception{
 //BA.debugLineNum = 573;BA.debugLine="Private Sub read_text";
 //BA.debugLineNum = 574;BA.debugLine="sets = File.ReadMap(File.DirInternal, \"expander.s";
mostCurrent._sets = anywheresoftware.b4a.keywords.Common.File.ReadMap(anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"expander.set");
 //BA.debugLineNum = 575;BA.debugLine="numer_urzadzenia.Text = sets.Get(\"numer_urzadzeni";
mostCurrent._numer_urzadzenia.setText(BA.ObjectToCharSequence(mostCurrent._sets.Get((Object)("numer_urzadzenia"))));
 //BA.debugLineNum = 578;BA.debugLine="timer_2.Text =sets.Get (\"timer_2\")";
mostCurrent._timer_2.setText(BA.ObjectToCharSequence(mostCurrent._sets.Get((Object)("timer_2"))));
 //BA.debugLineNum = 579;BA.debugLine="timer_3.Text =sets.Get (\"timer_3\")";
mostCurrent._timer_3.setText(BA.ObjectToCharSequence(mostCurrent._sets.Get((Object)("timer_3"))));
 //BA.debugLineNum = 580;BA.debugLine="timer_4.Text =sets.Get (\"timer_4\")";
mostCurrent._timer_4.setText(BA.ObjectToCharSequence(mostCurrent._sets.Get((Object)("timer_4"))));
 //BA.debugLineNum = 581;BA.debugLine="timer_5.Text =sets.Get (\"timer_5\")";
mostCurrent._timer_5.setText(BA.ObjectToCharSequence(mostCurrent._sets.Get((Object)("timer_5"))));
 //BA.debugLineNum = 582;BA.debugLine="sterowanie_out1.Text =sets.Get(\"timer_3\")";
mostCurrent._sterowanie_out1.setText(BA.ObjectToCharSequence(mostCurrent._sets.Get((Object)("timer_3"))));
 //BA.debugLineNum = 584;BA.debugLine="sterowanie_out2.Text =sets.Get(\"timer_4\")";
mostCurrent._sterowanie_out2.setText(BA.ObjectToCharSequence(mostCurrent._sets.Get((Object)("timer_4"))));
 //BA.debugLineNum = 585;BA.debugLine="label_timer_out2.Text =sets.Get(\"timer_4\")";
mostCurrent._label_timer_out2.setText(BA.ObjectToCharSequence(mostCurrent._sets.Get((Object)("timer_4"))));
 //BA.debugLineNum = 587;BA.debugLine="End Sub";
return "";
}
public static String  _save_nie_click() throws Exception{
 //BA.debugLineNum = 678;BA.debugLine="Private Sub Save_Nie_Click";
 //BA.debugLineNum = 679;BA.debugLine="CallSub (\"Main\", za_krotko)";
anywheresoftware.b4a.keywords.Common.CallSubNew(processBA,(Object)("Main"),_za_krotko());
 //BA.debugLineNum = 680;BA.debugLine="End Sub";
return "";
}
public static String  _save_nie_longclick() throws Exception{
 //BA.debugLineNum = 294;BA.debugLine="Private Sub Save_Nie_LongClick";
 //BA.debugLineNum = 295;BA.debugLine="ExitApplication";
anywheresoftware.b4a.keywords.Common.ExitApplication();
 //BA.debugLineNum = 296;BA.debugLine="End Sub";
return "";
}
public static String  _save_tak_click() throws Exception{
 //BA.debugLineNum = 673;BA.debugLine="Private Sub Save_Tak_Click";
 //BA.debugLineNum = 674;BA.debugLine="CallSub (\"Main\", za_krotko)";
anywheresoftware.b4a.keywords.Common.CallSubNew(processBA,(Object)("Main"),_za_krotko());
 //BA.debugLineNum = 675;BA.debugLine="End Sub";
return "";
}
public static String  _save_tak_longclick() throws Exception{
 //BA.debugLineNum = 282;BA.debugLine="Private Sub Save_Tak_LongClick";
 //BA.debugLineNum = 283;BA.debugLine="CallSub (\"Main\", write_text)";
anywheresoftware.b4a.keywords.Common.CallSubNew(processBA,(Object)("Main"),_write_text());
 //BA.debugLineNum = 285;BA.debugLine="End Sub";
return "";
}
public static boolean  _si_messagereceived(String _from,String _body) throws Exception{
String _msg = "";
anywheresoftware.b4a.keywords.Regex.MatcherWrapper _matcher = null;
int _datastart = 0;
String[] _s = null;
anywheresoftware.b4a.keywords.StringBuilderWrapper _modifiedmsg = null;
int _i = 0;
String _part = "";
 //BA.debugLineNum = 163;BA.debugLine="Sub SI_MessageReceived (From As String, Body As St";
 //BA.debugLineNum = 164;BA.debugLine="Dim msg As String = Body";
_msg = _body;
 //BA.debugLineNum = 167;BA.debugLine="Dim matcher As Matcher = Regex.Matcher(\"Signal GS";
_matcher = new anywheresoftware.b4a.keywords.Regex.MatcherWrapper();
_matcher = anywheresoftware.b4a.keywords.Common.Regex.Matcher("Signal GSM:",_msg);
 //BA.debugLineNum = 168;BA.debugLine="If matcher.Find Then";
if (_matcher.Find()) { 
 //BA.debugLineNum = 169;BA.debugLine="Dim dataStart As Int = matcher.GetStart(0)";
_datastart = _matcher.GetStart((int) (0));
 //BA.debugLineNum = 170;BA.debugLine="msg = \"*\" & msg.SubString(dataStart) ' dodaj gwi";
_msg = "*"+_msg.substring(_datastart);
 }else {
 //BA.debugLineNum = 173;BA.debugLine="Return False ' jeśli nie znaleziono tokenu, igno";
if (true) return anywheresoftware.b4a.keywords.Common.False;
 };
 //BA.debugLineNum = 177;BA.debugLine="Dim s() As String = Regex.Split(\"\\*\", msg)";
_s = anywheresoftware.b4a.keywords.Common.Regex.Split("\\*",_msg);
 //BA.debugLineNum = 179;BA.debugLine="Dim modifiedMsg As StringBuilder";
_modifiedmsg = new anywheresoftware.b4a.keywords.StringBuilderWrapper();
 //BA.debugLineNum = 180;BA.debugLine="modifiedMsg.Initialize";
_modifiedmsg.Initialize();
 //BA.debugLineNum = 182;BA.debugLine="For i = 0 To s.Length - 1";
{
final int step12 = 1;
final int limit12 = (int) (_s.length-1);
_i = (int) (0) ;
for (;_i <= limit12 ;_i = _i + step12 ) {
 //BA.debugLineNum = 183;BA.debugLine="Dim part As String = s(i).Trim";
_part = _s[_i].trim();
 //BA.debugLineNum = 184;BA.debugLine="If part.Length > 0 Then";
if (_part.length()>0) { 
 //BA.debugLineNum = 185;BA.debugLine="s(i) = s(i).Replace(\"OUT1: OFF\", timer_3.text &";
_s[_i] = _s[_i].replace("OUT1: OFF",mostCurrent._timer_3.getText()+": OFF");
 //BA.debugLineNum = 186;BA.debugLine="s(i) = s(i).Replace(\"OUT2: OFF\", timer_4.Text &";
_s[_i] = _s[_i].replace("OUT2: OFF",mostCurrent._timer_4.getText()+": OFF");
 //BA.debugLineNum = 187;BA.debugLine="s(i) = s(i).Replace(\"OUT1: ON\", timer_3.text &";
_s[_i] = _s[_i].replace("OUT1: ON",mostCurrent._timer_3.getText()+": ON");
 //BA.debugLineNum = 188;BA.debugLine="s(i) = s(i).Replace(\"OUT2: ON\", timer_4.Text &";
_s[_i] = _s[_i].replace("OUT2: ON",mostCurrent._timer_4.getText()+": ON");
 //BA.debugLineNum = 189;BA.debugLine="s(i) = s(i).Replace(\"Temp:\", timer_5.Text)";
_s[_i] = _s[_i].replace("Temp:",mostCurrent._timer_5.getText());
 //BA.debugLineNum = 191;BA.debugLine="modifiedMsg.Append(s(i)).Append(CRLF) ' ✅ używa";
_modifiedmsg.Append(_s[_i]).Append(anywheresoftware.b4a.keywords.Common.CRLF);
 };
 }
};
 //BA.debugLineNum = 196;BA.debugLine="If modifiedMsg.Length > 0 Then";
if (_modifiedmsg.getLength()>0) { 
 //BA.debugLineNum = 197;BA.debugLine="MsgboxAsync(\"\" & CRLF & modifiedMsg.ToString.Tri";
anywheresoftware.b4a.keywords.Common.MsgboxAsync(BA.ObjectToCharSequence(""+anywheresoftware.b4a.keywords.Common.CRLF+_modifiedmsg.ToString().trim()),BA.ObjectToCharSequence("Aktualne parametry sterownika"),processBA);
 //BA.debugLineNum = 198;BA.debugLine="Return True";
if (true) return anywheresoftware.b4a.keywords.Common.True;
 };
 //BA.debugLineNum = 201;BA.debugLine="Return False";
if (true) return anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 202;BA.debugLine="End Sub";
return false;
}
public static String  _timer_1_click() throws Exception{
 //BA.debugLineNum = 633;BA.debugLine="Private Sub timer_1_Click";
 //BA.debugLineNum = 634;BA.debugLine="CallSub (\"Main\", za_krotko)";
anywheresoftware.b4a.keywords.Common.CallSubNew(processBA,(Object)("Main"),_za_krotko());
 //BA.debugLineNum = 635;BA.debugLine="End Sub";
return "";
}
public static String  _timer_2_click() throws Exception{
 //BA.debugLineNum = 637;BA.debugLine="Private Sub timer_2_Click";
 //BA.debugLineNum = 638;BA.debugLine="CallSub (\"Main\", za_krotko)";
anywheresoftware.b4a.keywords.Common.CallSubNew(processBA,(Object)("Main"),_za_krotko());
 //BA.debugLineNum = 639;BA.debugLine="End Sub";
return "";
}
public static String  _timer_2_longclick() throws Exception{
int _seconds = 0;
int _hours = 0;
int _minutes = 0;
int _remainingseconds = 0;
String _timemessage = "";
 //BA.debugLineNum = 441;BA.debugLine="Private Sub timer_2_LongClick";
 //BA.debugLineNum = 442;BA.debugLine="If timer_2.Text.Length > 5 Then";
if (mostCurrent._timer_2.getText().length()>5) { 
 //BA.debugLineNum = 443;BA.debugLine="MsgboxAsync(\"Wprowadź maksymalnie 5 cyfr\", \"Błąd";
anywheresoftware.b4a.keywords.Common.MsgboxAsync(BA.ObjectToCharSequence("Wprowadź maksymalnie 5 cyfr"),BA.ObjectToCharSequence("Błąd"),processBA);
 //BA.debugLineNum = 444;BA.debugLine="Return";
if (true) return "";
 };
 //BA.debugLineNum = 447;BA.debugLine="If IsNumber(timer_2.Text) = False Then";
if (anywheresoftware.b4a.keywords.Common.IsNumber(mostCurrent._timer_2.getText())==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 448;BA.debugLine="MsgboxAsync(\"Wprowadź tylko cyfry\", \"Błąd\")";
anywheresoftware.b4a.keywords.Common.MsgboxAsync(BA.ObjectToCharSequence("Wprowadź tylko cyfry"),BA.ObjectToCharSequence("Błąd"),processBA);
 //BA.debugLineNum = 449;BA.debugLine="Return";
if (true) return "";
 };
 //BA.debugLineNum = 452;BA.debugLine="If timer_2.Text > 99998 Then";
if ((double)(Double.parseDouble(mostCurrent._timer_2.getText()))>99998) { 
 //BA.debugLineNum = 453;BA.debugLine="MsgboxAsync(\"Liczba nie może być większa niż 999";
anywheresoftware.b4a.keywords.Common.MsgboxAsync(BA.ObjectToCharSequence("Liczba nie może być większa niż 99998s"),BA.ObjectToCharSequence("Błąd"),processBA);
 //BA.debugLineNum = 454;BA.debugLine="Return";
if (true) return "";
 };
 //BA.debugLineNum = 458;BA.debugLine="Dim seconds As Int = timer_2.Text";
_seconds = (int)(Double.parseDouble(mostCurrent._timer_2.getText()));
 //BA.debugLineNum = 459;BA.debugLine="Dim hours As Int = seconds / 3600";
_hours = (int) (_seconds/(double)3600);
 //BA.debugLineNum = 460;BA.debugLine="Dim minutes As Int = (seconds Mod 3600) / 60";
_minutes = (int) ((_seconds%3600)/(double)60);
 //BA.debugLineNum = 461;BA.debugLine="Dim remainingSeconds As Int = seconds Mod 60";
_remainingseconds = (int) (_seconds%60);
 //BA.debugLineNum = 464;BA.debugLine="Dim timeMessage As String";
_timemessage = "";
 //BA.debugLineNum = 465;BA.debugLine="If hours > 0 Then";
if (_hours>0) { 
 //BA.debugLineNum = 466;BA.debugLine="timeMessage = $\"Timer włączony na ${hours} godz.";
_timemessage = ("Timer włączony na "+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(_hours))+" godz.");
 //BA.debugLineNum = 467;BA.debugLine="If minutes > 0 Or remainingSeconds > 0 Then";
if (_minutes>0 || _remainingseconds>0) { 
 //BA.debugLineNum = 468;BA.debugLine="timeMessage = timeMessage & $\", ${minutes} min.";
_timemessage = _timemessage+(", "+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(_minutes))+" min.");
 };
 //BA.debugLineNum = 470;BA.debugLine="If remainingSeconds > 0 Then";
if (_remainingseconds>0) { 
 //BA.debugLineNum = 471;BA.debugLine="timeMessage = timeMessage & $\", ${remainingSeco";
_timemessage = _timemessage+(", "+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(_remainingseconds))+" sek.");
 };
 }else if(_minutes>0) { 
 //BA.debugLineNum = 474;BA.debugLine="timeMessage = $\"Timer włączony na ${minutes} min";
_timemessage = ("Timer włączony na "+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(_minutes))+" min.");
 //BA.debugLineNum = 475;BA.debugLine="If remainingSeconds > 0 Then";
if (_remainingseconds>0) { 
 //BA.debugLineNum = 476;BA.debugLine="timeMessage = timeMessage & $\", ${remainingSeco";
_timemessage = _timemessage+(", "+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(_remainingseconds))+" sek.");
 };
 }else {
 //BA.debugLineNum = 479;BA.debugLine="timeMessage = $\"Timer włączony na ${remainingSec";
_timemessage = ("Timer włączony na "+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(_remainingseconds))+" sek.");
 };
 //BA.debugLineNum = 483;BA.debugLine="MsgboxAsync(timeMessage, \"⏱ Informacja \"&timer_4.";
anywheresoftware.b4a.keywords.Common.MsgboxAsync(BA.ObjectToCharSequence(_timemessage),BA.ObjectToCharSequence("⏱ Informacja "+mostCurrent._timer_4.getText()),processBA);
 //BA.debugLineNum = 485;BA.debugLine="Phone.Send(numer_urzadzenia.Text, \"OUT2#ON \" & ti";
mostCurrent._phone.Send(mostCurrent._numer_urzadzenia.getText(),"OUT2#ON "+mostCurrent._timer_2.getText());
 //BA.debugLineNum = 487;BA.debugLine="End Sub";
return "";
}
public static String  _timer_tick() throws Exception{
 //BA.debugLineNum = 262;BA.debugLine="Sub Timer_Tick";
 //BA.debugLineNum = 263;BA.debugLine="Log (\"timer odliczyl\")";
anywheresoftware.b4a.keywords.Common.LogImpl("0655361","timer odliczyl",0);
 //BA.debugLineNum = 264;BA.debugLine="ProgressDialogHide";
anywheresoftware.b4a.keywords.Common.ProgressDialogHide();
 //BA.debugLineNum = 265;BA.debugLine="Timer3.Enabled =False";
_timer3.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 266;BA.debugLine="End Sub";
return "";
}
public static String  _usun_click() throws Exception{
 //BA.debugLineNum = 688;BA.debugLine="Private Sub usun_Click";
 //BA.debugLineNum = 689;BA.debugLine="CallSub (\"Main\", za_krotko)";
anywheresoftware.b4a.keywords.Common.CallSubNew(processBA,(Object)("Main"),_za_krotko());
 //BA.debugLineNum = 690;BA.debugLine="End Sub";
return "";
}
public static String  _usun_longclick() throws Exception{
 //BA.debugLineNum = 312;BA.debugLine="Private Sub Usun_LongClick";
 //BA.debugLineNum = 313;BA.debugLine="Phone.Send (numer_urzadzenia.Text ,\"DEL^----^^\" )";
mostCurrent._phone.Send(mostCurrent._numer_urzadzenia.getText(),"DEL^----^^");
 //BA.debugLineNum = 314;BA.debugLine="CallSub (\"Main\", wykonuje)";
anywheresoftware.b4a.keywords.Common.CallSubNew(processBA,(Object)("Main"),_wykonuje());
 //BA.debugLineNum = 315;BA.debugLine="CallSub (\"Main\",za_krotko_2)";
anywheresoftware.b4a.keywords.Common.CallSubNew(processBA,(Object)("Main"),_za_krotko_2());
 //BA.debugLineNum = 316;BA.debugLine="End Sub";
return "";
}
public static String  _write_text() throws Exception{
 //BA.debugLineNum = 555;BA.debugLine="Private Sub  write_text";
 //BA.debugLineNum = 556;BA.debugLine="sets.Clear";
mostCurrent._sets.Clear();
 //BA.debugLineNum = 557;BA.debugLine="sets.Put(\"numer_urzadzenia\", numer_urzadzenia.Tex";
mostCurrent._sets.Put((Object)("numer_urzadzenia"),(Object)(mostCurrent._numer_urzadzenia.getText()));
 //BA.debugLineNum = 559;BA.debugLine="sets.Put(\"timer_2\", timer_2.Text)";
mostCurrent._sets.Put((Object)("timer_2"),(Object)(mostCurrent._timer_2.getText()));
 //BA.debugLineNum = 560;BA.debugLine="sets.Put(\"timer_3\", timer_3.Text)";
mostCurrent._sets.Put((Object)("timer_3"),(Object)(mostCurrent._timer_3.getText()));
 //BA.debugLineNum = 561;BA.debugLine="sets.Put(\"timer_4\", timer_4.Text)";
mostCurrent._sets.Put((Object)("timer_4"),(Object)(mostCurrent._timer_4.getText()));
 //BA.debugLineNum = 562;BA.debugLine="sets.Put(\"timer_5\", timer_5.Text)";
mostCurrent._sets.Put((Object)("timer_5"),(Object)(mostCurrent._timer_5.getText()));
 //BA.debugLineNum = 565;BA.debugLine="File.WriteMap(File.DirInternal, \"expander.set\", s";
anywheresoftware.b4a.keywords.Common.File.WriteMap(anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"expander.set",mostCurrent._sets);
 //BA.debugLineNum = 566;BA.debugLine="CallSub (\"Main\",za_krotko_2)";
anywheresoftware.b4a.keywords.Common.CallSubNew(processBA,(Object)("Main"),_za_krotko_2());
 //BA.debugLineNum = 567;BA.debugLine="StartActivity (\"main\")";
anywheresoftware.b4a.keywords.Common.StartActivity(processBA,(Object)("main"));
 //BA.debugLineNum = 570;BA.debugLine="End Sub";
return "";
}
public static String  _wykonuje() throws Exception{
 //BA.debugLineNum = 252;BA.debugLine="Sub wykonuje";
 //BA.debugLineNum = 254;BA.debugLine="ProgressDialogShow ( \"Wykonuję chwileczkę...\")";
anywheresoftware.b4a.keywords.Common.ProgressDialogShow(mostCurrent.activityBA,BA.ObjectToCharSequence("Wykonuję chwileczkę..."));
 //BA.debugLineNum = 255;BA.debugLine="Timer3.Enabled=False";
_timer3.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 256;BA.debugLine="Timer3.Initialize(\"Timer\",5500)";
_timer3.Initialize(processBA,"Timer",(long) (5500));
 //BA.debugLineNum = 257;BA.debugLine="Timer3.Enabled =True";
_timer3.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 259;BA.debugLine="End Sub";
return "";
}
public static String  _za_krotko() throws Exception{
 //BA.debugLineNum = 596;BA.debugLine="Sub za_krotko";
 //BA.debugLineNum = 598;BA.debugLine="ToastMessageShow ( \"Przytrzymaj 3sek.\",True)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Przytrzymaj 3sek."),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 599;BA.debugLine="Timer3.Enabled=False";
_timer3.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 600;BA.debugLine="Timer3.Initialize(\"Timer\",1500)";
_timer3.Initialize(processBA,"Timer",(long) (1500));
 //BA.debugLineNum = 601;BA.debugLine="Timer3.Enabled =True";
_timer3.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 602;BA.debugLine="End Sub";
return "";
}
public static String  _za_krotko_2() throws Exception{
 //BA.debugLineNum = 606;BA.debugLine="Sub za_krotko_2";
 //BA.debugLineNum = 608;BA.debugLine="ToastMessageShow ( \"Zapisuje\",True)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Zapisuje"),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 609;BA.debugLine="Timer3.Enabled=False";
_timer3.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 610;BA.debugLine="Timer3.Initialize(\"Timer\",2500)";
_timer3.Initialize(processBA,"Timer",(long) (2500));
 //BA.debugLineNum = 611;BA.debugLine="Timer3.Enabled =True";
_timer3.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 612;BA.debugLine="End Sub";
return "";
}
public static String  _zamknij_click() throws Exception{
 //BA.debugLineNum = 329;BA.debugLine="Private Sub Zamknij_Click";
 //BA.debugLineNum = 330;BA.debugLine="CallSub (\"Main\", za_krotko)";
anywheresoftware.b4a.keywords.Common.CallSubNew(processBA,(Object)("Main"),_za_krotko());
 //BA.debugLineNum = 331;BA.debugLine="End Sub";
return "";
}
public static String  _zmianakodu_click() throws Exception{
 //BA.debugLineNum = 668;BA.debugLine="Private Sub zmianakodu_Click";
 //BA.debugLineNum = 669;BA.debugLine="CallSub (\"Main\", za_krotko)";
anywheresoftware.b4a.keywords.Common.CallSubNew(processBA,(Object)("Main"),_za_krotko());
 //BA.debugLineNum = 670;BA.debugLine="End Sub";
return "";
}
}
