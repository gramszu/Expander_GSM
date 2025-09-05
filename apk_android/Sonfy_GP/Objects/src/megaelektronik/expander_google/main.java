package megaelektronik.expander_google;


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
			processBA = new BA(this.getApplicationContext(), null, null, "megaelektronik.expander_google", "megaelektronik.expander_google.main");
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
		activityBA = new BA(this, layout, processBA, "megaelektronik.expander_google", "megaelektronik.expander_google.main");
        
        processBA.sharedProcessBA.activityBA = new java.lang.ref.WeakReference<BA>(activityBA);
        anywheresoftware.b4a.objects.ViewWrapper.lastId = 0;
        _activity = new ActivityWrapper(activityBA, "activity");
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (BA.isShellModeRuntimeCheck(processBA)) {
			if (isFirst)
				processBA.raiseEvent2(null, true, "SHELL", false);
			processBA.raiseEvent2(null, true, "CREATE", true, "megaelektronik.expander_google.main", processBA, activityBA, _activity, anywheresoftware.b4a.keywords.Common.Density, mostCurrent);
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
public anywheresoftware.b4a.objects.PanelWrapper _panel1 = null;
public anywheresoftware.b4a.objects.ButtonWrapper _save_tak = null;
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
public anywheresoftware.b4a.objects.ButtonWrapper _arm_on = null;
public anywheresoftware.b4a.objects.ButtonWrapper _arm_off = null;
public anywheresoftware.b4a.objects.PanelWrapper _panel_arm = null;
public anywheresoftware.b4a.objects.EditTextWrapper _timer = null;
public megaelektronik.expander_google.starter _starter = null;

public static boolean isAnyActivityVisible() {
    boolean vis = false;
vis = vis | (main.mostCurrent != null);
return vis;}
public static String  _activity_create(boolean _firsttime) throws Exception{
 //BA.debugLineNum = 91;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 93;BA.debugLine="Log(\"Activity_Create: Rozpoczęcie tworzenia aktyw";
anywheresoftware.b4a.keywords.Common.LogImpl("6131074","Activity_Create: Rozpoczęcie tworzenia aktywności.",0);
 //BA.debugLineNum = 94;BA.debugLine="sets.Initialize";
mostCurrent._sets.Initialize();
 //BA.debugLineNum = 95;BA.debugLine="PE.InitializeWithPhoneState(\"PE\", PhoneId)";
_pe.InitializeWithPhoneState(processBA,"PE",_phoneid);
 //BA.debugLineNum = 97;BA.debugLine="Activity.LoadLayout(\"Main\")";
mostCurrent._activity.LoadLayout("Main",mostCurrent.activityBA);
 //BA.debugLineNum = 99;BA.debugLine="TabStrip1.LoadLayout(\"sterowanie\", \"⚙️ STEROWANIE";
mostCurrent._tabstrip1.LoadLayout("sterowanie",BA.ObjectToCharSequence("⚙️ STEROWANIE"));
 //BA.debugLineNum = 100;BA.debugLine="TabStrip1.LoadLayout(\"timer_setings\", \"🛠️ USTAWI";
mostCurrent._tabstrip1.LoadLayout("timer_setings",BA.ObjectToCharSequence("🛠️ USTAWIENIA"));
 //BA.debugLineNum = 101;BA.debugLine="TabStrip1.LoadLayout(\"Wydawca\", \"ℹ️ O APLIKACJI\")";
mostCurrent._tabstrip1.LoadLayout("Wydawca",BA.ObjectToCharSequence("ℹ️ O APLIKACJI"));
 //BA.debugLineNum = 102;BA.debugLine="Log(\"Activity_Create: Layouts załadowane.\")";
anywheresoftware.b4a.keywords.Common.LogImpl("6131083","Activity_Create: Layouts załadowane.",0);
 //BA.debugLineNum = 104;BA.debugLine="End Sub";
return "";
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 137;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
 //BA.debugLineNum = 138;BA.debugLine="Log($\"Activity_Pause: Aktywność wstrzymana. Użytk";
anywheresoftware.b4a.keywords.Common.LogImpl("6458753",("Activity_Pause: Aktywność wstrzymana. Użytkownik zamknął: "+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(_userclosed))+""),0);
 //BA.debugLineNum = 139;BA.debugLine="If File.Exists(File.DirInternal, \"expander.set\")";
if (anywheresoftware.b4a.keywords.Common.File.Exists(anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"expander.set")) { 
 //BA.debugLineNum = 140;BA.debugLine="Log(\"Activity_Pause: Plik 'expander.set' istniej";
anywheresoftware.b4a.keywords.Common.LogImpl("6458755","Activity_Pause: Plik 'expander.set' istnieje. Odczytuję dane przed pauzą.",0);
 //BA.debugLineNum = 141;BA.debugLine="CallSub(\"Main\",read_text)";
anywheresoftware.b4a.keywords.Common.CallSubNew(processBA,(Object)("Main"),_read_text());
 }else {
 //BA.debugLineNum = 143;BA.debugLine="Log(\"Activity_Pause: Plik 'expander.set' NIE ist";
anywheresoftware.b4a.keywords.Common.LogImpl("6458758","Activity_Pause: Plik 'expander.set' NIE istnieje. Zapisuję dane przed pauzą.",0);
 //BA.debugLineNum = 144;BA.debugLine="CallSub (\"Main\", write_text)";
anywheresoftware.b4a.keywords.Common.CallSubNew(processBA,(Object)("Main"),_write_text());
 };
 //BA.debugLineNum = 147;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 126;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 127;BA.debugLine="Log(\"Activity_Resume: Aktywność wznowiona.\")";
anywheresoftware.b4a.keywords.Common.LogImpl("6393217","Activity_Resume: Aktywność wznowiona.",0);
 //BA.debugLineNum = 128;BA.debugLine="If File.Exists(File.DirInternal, \"expander.set\")";
if (anywheresoftware.b4a.keywords.Common.File.Exists(anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"expander.set")) { 
 //BA.debugLineNum = 129;BA.debugLine="Log(\"Activity_Resume: Plik 'expander.set' istnie";
anywheresoftware.b4a.keywords.Common.LogImpl("6393219","Activity_Resume: Plik 'expander.set' istnieje. Odczytuję dane.",0);
 //BA.debugLineNum = 130;BA.debugLine="CallSub(\"Main\",read_text)";
anywheresoftware.b4a.keywords.Common.CallSubNew(processBA,(Object)("Main"),_read_text());
 }else {
 //BA.debugLineNum = 132;BA.debugLine="Log(\"Activity_Resume: Plik 'expander.set' NIE is";
anywheresoftware.b4a.keywords.Common.LogImpl("6393222","Activity_Resume: Plik 'expander.set' NIE istnieje. Tworzę nowy i zapisuję dane.",0);
 //BA.debugLineNum = 133;BA.debugLine="CallSub (\"Main\", write_text)";
anywheresoftware.b4a.keywords.Common.CallSubNew(processBA,(Object)("Main"),_write_text());
 };
 //BA.debugLineNum = 135;BA.debugLine="End Sub";
return "";
}
public static String  _arm_off_click() throws Exception{
 //BA.debugLineNum = 493;BA.debugLine="Private Sub ARM_OFF_Click";
 //BA.debugLineNum = 494;BA.debugLine="Log(\"ARM_OFF_Click: Krótkie kliknięcie 'ARM OFF'.";
anywheresoftware.b4a.keywords.Common.LogImpl("62752513","ARM_OFF_Click: Krótkie kliknięcie 'ARM OFF'.",0);
 //BA.debugLineNum = 495;BA.debugLine="CallSub (\"Main\", za_krotko)";
anywheresoftware.b4a.keywords.Common.CallSubNew(processBA,(Object)("Main"),_za_krotko());
 //BA.debugLineNum = 496;BA.debugLine="End Sub";
return "";
}
public static String  _arm_off_longclick() throws Exception{
 //BA.debugLineNum = 282;BA.debugLine="Private Sub ARM_OFF_LongClick";
 //BA.debugLineNum = 283;BA.debugLine="Log($\"ARM_OFF_LongClick: Długie kliknięcie 'ARM O";
anywheresoftware.b4a.keywords.Common.LogImpl("61507329",("ARM_OFF_LongClick: Długie kliknięcie 'ARM OFF'. Wysyłam komendę ALARM#LOCK do "+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(mostCurrent._numer_urzadzenia.getText()))+""),0);
 //BA.debugLineNum = 284;BA.debugLine="WyslijKomende(numer_urzadzenia.Text, \"ALARM#LOCK\"";
_wyslijkomende(mostCurrent._numer_urzadzenia.getText(),"ALARM#LOCK");
 //BA.debugLineNum = 285;BA.debugLine="CallSub(\"Main\", \"wykonuje\")";
anywheresoftware.b4a.keywords.Common.CallSubNew(processBA,(Object)("Main"),"wykonuje");
 //BA.debugLineNum = 286;BA.debugLine="End Sub";
return "";
}
public static String  _arm_on_click() throws Exception{
 //BA.debugLineNum = 500;BA.debugLine="Private Sub ARM_ON_Click";
 //BA.debugLineNum = 501;BA.debugLine="Log(\"ARM_ON_Click: Krótkie kliknięcie 'ARM ON'.\")";
anywheresoftware.b4a.keywords.Common.LogImpl("62818049","ARM_ON_Click: Krótkie kliknięcie 'ARM ON'.",0);
 //BA.debugLineNum = 502;BA.debugLine="CallSub (\"Main\", za_krotko)";
anywheresoftware.b4a.keywords.Common.CallSubNew(processBA,(Object)("Main"),_za_krotko());
 //BA.debugLineNum = 503;BA.debugLine="End Sub";
return "";
}
public static String  _arm_on_longclick() throws Exception{
 //BA.debugLineNum = 276;BA.debugLine="Private Sub ARM_ON_LongClick";
 //BA.debugLineNum = 277;BA.debugLine="Log($\"ARM_ON_LongClick: Długie kliknięcie 'ARM ON";
anywheresoftware.b4a.keywords.Common.LogImpl("61441793",("ARM_ON_LongClick: Długie kliknięcie 'ARM ON'. Wysyłam komendę ALARM#ON do "+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(mostCurrent._numer_urzadzenia.getText()))+""),0);
 //BA.debugLineNum = 278;BA.debugLine="WyslijKomende(numer_urzadzenia.Text, \"ALARM#ON\")";
_wyslijkomende(mostCurrent._numer_urzadzenia.getText(),"ALARM#ON");
 //BA.debugLineNum = 279;BA.debugLine="CallSub(\"Main\", \"wykonuje\")";
anywheresoftware.b4a.keywords.Common.CallSubNew(processBA,(Object)("Main"),"wykonuje");
 //BA.debugLineNum = 280;BA.debugLine="End Sub";
return "";
}
public static String  _button_raport_click() throws Exception{
 //BA.debugLineNum = 419;BA.debugLine="Private Sub button_raport_Click";
 //BA.debugLineNum = 420;BA.debugLine="Log(\"button_raport_Click: Krótkie kliknięcie 'Rap";
anywheresoftware.b4a.keywords.Common.LogImpl("61966081","button_raport_Click: Krótkie kliknięcie 'Raport'.",0);
 //BA.debugLineNum = 421;BA.debugLine="CallSub (\"Main\", za_krotko)";
anywheresoftware.b4a.keywords.Common.CallSubNew(processBA,(Object)("Main"),_za_krotko());
 //BA.debugLineNum = 422;BA.debugLine="End Sub";
return "";
}
public static String  _button_raport_longclick() throws Exception{
 //BA.debugLineNum = 288;BA.debugLine="Private Sub button_raport_LongClick";
 //BA.debugLineNum = 289;BA.debugLine="Log($\"button_raport_LongClick: Długie kliknięcie";
anywheresoftware.b4a.keywords.Common.LogImpl("61572865",("button_raport_LongClick: Długie kliknięcie 'Raport'. Wysyłam komendę RAPORT do "+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(mostCurrent._numer_urzadzenia.getText()))+""),0);
 //BA.debugLineNum = 290;BA.debugLine="WyslijKomende(numer_urzadzenia.Text, \"RAPORT\")";
_wyslijkomende(mostCurrent._numer_urzadzenia.getText(),"RAPORT");
 //BA.debugLineNum = 291;BA.debugLine="CallSub(\"Main\", \"wykonuje\")";
anywheresoftware.b4a.keywords.Common.CallSubNew(processBA,(Object)("Main"),"wykonuje");
 //BA.debugLineNum = 292;BA.debugLine="End Sub";
return "";
}
public static String  _button_save_2_click() throws Exception{
 //BA.debugLineNum = 453;BA.debugLine="Private Sub button_save_2_Click";
 //BA.debugLineNum = 454;BA.debugLine="Log(\"button_save_2_Click: Krótkie kliknięcie 'Zap";
anywheresoftware.b4a.keywords.Common.LogImpl("62359297","button_save_2_Click: Krótkie kliknięcie 'Zapisz Timer'.",0);
 //BA.debugLineNum = 455;BA.debugLine="CallSub (\"Main\", za_krotko)";
anywheresoftware.b4a.keywords.Common.CallSubNew(processBA,(Object)("Main"),_za_krotko());
 //BA.debugLineNum = 456;BA.debugLine="End Sub";
return "";
}
public static void  _button_save_2_longclick() throws Exception{
ResumableSub_button_save_2_LongClick rsub = new ResumableSub_button_save_2_LongClick(null);
rsub.resume(processBA, null);
}
public static class ResumableSub_button_save_2_LongClick extends BA.ResumableSub {
public ResumableSub_button_save_2_LongClick(megaelektronik.expander_google.main parent) {
this.parent = parent;
}
megaelektronik.expander_google.main parent;

@Override
public void resume(BA ba, Object[] result) throws Exception{

    while (true) {
        switch (state) {
            case -1:
return;

case 0:
//C
this.state = 1;
 //BA.debugLineNum = 369;BA.debugLine="Log(\"button_save_2_LongClick: Długie kliknięcie '";
anywheresoftware.b4a.keywords.Common.LogImpl("61769473","button_save_2_LongClick: Długie kliknięcie 'Zapisz Timer'.",0);
 //BA.debugLineNum = 370;BA.debugLine="If timer.Text.Length > 5 Then";
if (true) break;

case 1:
//if
this.state = 4;
if (parent.mostCurrent._timer.getText().length()>5) { 
this.state = 3;
}if (true) break;

case 3:
//C
this.state = 4;
 //BA.debugLineNum = 371;BA.debugLine="MsgboxAsync(\"Wprowadź maksymalnie 5 cyfr\", \"Błąd";
anywheresoftware.b4a.keywords.Common.MsgboxAsync(BA.ObjectToCharSequence("Wprowadź maksymalnie 5 cyfr"),BA.ObjectToCharSequence("Błąd"),processBA);
 //BA.debugLineNum = 372;BA.debugLine="Log(\"button_save_2_LongClick: Błąd walidacji - d";
anywheresoftware.b4a.keywords.Common.LogImpl("61769476","button_save_2_LongClick: Błąd walidacji - długość timera > 5 cyfr.",0);
 //BA.debugLineNum = 373;BA.debugLine="Return";
if (true) return ;
 if (true) break;
;
 //BA.debugLineNum = 376;BA.debugLine="If IsNumber(timer.Text) = False Then";

case 4:
//if
this.state = 7;
if (anywheresoftware.b4a.keywords.Common.IsNumber(parent.mostCurrent._timer.getText())==anywheresoftware.b4a.keywords.Common.False) { 
this.state = 6;
}if (true) break;

case 6:
//C
this.state = 7;
 //BA.debugLineNum = 377;BA.debugLine="MsgboxAsync(\"Wprowadź tylko cyfry\", \"Błąd\")";
anywheresoftware.b4a.keywords.Common.MsgboxAsync(BA.ObjectToCharSequence("Wprowadź tylko cyfry"),BA.ObjectToCharSequence("Błąd"),processBA);
 //BA.debugLineNum = 378;BA.debugLine="Log(\"button_save_2_LongClick: Błąd walidacji - t";
anywheresoftware.b4a.keywords.Common.LogImpl("61769482","button_save_2_LongClick: Błąd walidacji - timer nie jest liczbą.",0);
 //BA.debugLineNum = 379;BA.debugLine="Return";
if (true) return ;
 if (true) break;
;
 //BA.debugLineNum = 382;BA.debugLine="If timer.Text > 99998 Then";

case 7:
//if
this.state = 10;
if ((double)(Double.parseDouble(parent.mostCurrent._timer.getText()))>99998) { 
this.state = 9;
}if (true) break;

case 9:
//C
this.state = 10;
 //BA.debugLineNum = 383;BA.debugLine="MsgboxAsync(\"Liczba nie może być większa niż 999";
anywheresoftware.b4a.keywords.Common.MsgboxAsync(BA.ObjectToCharSequence("Liczba nie może być większa niż 99998s"),BA.ObjectToCharSequence("Błąd"),processBA);
 //BA.debugLineNum = 384;BA.debugLine="Return";
if (true) return ;
 if (true) break;

case 10:
//C
this.state = -1;
;
 //BA.debugLineNum = 387;BA.debugLine="CallSub (\"Main\",wykonuje)";
anywheresoftware.b4a.keywords.Common.CallSubNew(processBA,(Object)("Main"),_wykonuje());
 //BA.debugLineNum = 388;BA.debugLine="Wait For (2000) Timeout";
anywheresoftware.b4a.keywords.Common.WaitFor("timeout", processBA, this, (Object)(2000));
this.state = 11;
return;
case 11:
//C
this.state = -1;
;
 //BA.debugLineNum = 389;BA.debugLine="CallSub (\"Main\", write_text) ' Zapisanie danych t";
anywheresoftware.b4a.keywords.Common.CallSubNew(processBA,(Object)("Main"),_write_text());
 //BA.debugLineNum = 390;BA.debugLine="Log(\"button_save_2_LongClick: Zakończono zapis da";
anywheresoftware.b4a.keywords.Common.LogImpl("61769494","button_save_2_LongClick: Zakończono zapis danych timera.",0);
 //BA.debugLineNum = 391;BA.debugLine="End Sub";
if (true) break;

            }
        }
    }
}
public static void  _timeout() throws Exception{
}
public static String  _dodaj_click() throws Exception{
 //BA.debugLineNum = 480;BA.debugLine="Private Sub dodaj_Click";
 //BA.debugLineNum = 481;BA.debugLine="Log(\"dodaj_Click: Krótkie kliknięcie 'Dodaj'.\")";
anywheresoftware.b4a.keywords.Common.LogImpl("62621441","dodaj_Click: Krótkie kliknięcie 'Dodaj'.",0);
 //BA.debugLineNum = 482;BA.debugLine="CallSub (\"Main\", za_krotko)";
anywheresoftware.b4a.keywords.Common.CallSubNew(processBA,(Object)("Main"),_za_krotko());
 //BA.debugLineNum = 483;BA.debugLine="End Sub";
return "";
}
public static String  _dodaj_longclick() throws Exception{
 //BA.debugLineNum = 213;BA.debugLine="Private Sub dodaj_LongClick";
 //BA.debugLineNum = 214;BA.debugLine="Log($\"dodaj_LongClick: Długie kliknięcie 'Dodaj'.";
anywheresoftware.b4a.keywords.Common.LogImpl("6851969",("dodaj_LongClick: Długie kliknięcie 'Dodaj'. Wysyłam komendę ADD z numerem: "+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(mostCurrent._numery.getText()))+""),0);
 //BA.debugLineNum = 215;BA.debugLine="WyslijKomende (numer_urzadzenia.Text,\"ADD^^\"&nume";
_wyslijkomende(mostCurrent._numer_urzadzenia.getText(),"ADD^^"+mostCurrent._numery.getText()+"^");
 //BA.debugLineNum = 216;BA.debugLine="CallSub (\"Main\", wykonuje)";
anywheresoftware.b4a.keywords.Common.CallSubNew(processBA,(Object)("Main"),_wykonuje());
 //BA.debugLineNum = 217;BA.debugLine="CallSub (\"Main\",za_krotko_2)";
anywheresoftware.b4a.keywords.Common.CallSubNew(processBA,(Object)("Main"),_za_krotko_2());
 //BA.debugLineNum = 218;BA.debugLine="End Sub";
return "";
}
public static String  _globals() throws Exception{
 //BA.debugLineNum = 30;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 34;BA.debugLine="Dim sets As Map";
mostCurrent._sets = new anywheresoftware.b4a.objects.collections.Map();
 //BA.debugLineNum = 35;BA.debugLine="Private TabStrip1 As TabStrip";
mostCurrent._tabstrip1 = new anywheresoftware.b4a.objects.TabStripViewPager();
 //BA.debugLineNum = 36;BA.debugLine="Private Panel1 As Panel";
mostCurrent._panel1 = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 37;BA.debugLine="Private Save_Tak As Button";
mostCurrent._save_tak = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 39;BA.debugLine="Private Label3 As Label";
mostCurrent._label3 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 40;BA.debugLine="Private Label2 As Label";
mostCurrent._label2 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 41;BA.debugLine="Private Label1 As Label";
mostCurrent._label1 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 42;BA.debugLine="Private numer_urzadzenia As EditText";
mostCurrent._numer_urzadzenia = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 43;BA.debugLine="Private dodaj As Button";
mostCurrent._dodaj = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 44;BA.debugLine="Private Usun As Button";
mostCurrent._usun = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 45;BA.debugLine="Private Label4 As Label";
mostCurrent._label4 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 46;BA.debugLine="Private Panel2 As Panel";
mostCurrent._panel2 = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 47;BA.debugLine="Private Panel3 As Panel";
mostCurrent._panel3 = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 48;BA.debugLine="Private Panel4 As Panel";
mostCurrent._panel4 = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 49;BA.debugLine="Private Button1 As Button";
mostCurrent._button1 = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 50;BA.debugLine="Private Button2 As Button";
mostCurrent._button2 = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 51;BA.debugLine="Private Raport As Label";
mostCurrent._raport = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 53;BA.debugLine="Private numery As EditText";
mostCurrent._numery = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 55;BA.debugLine="Private Button3 As Button";
mostCurrent._button3 = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 56;BA.debugLine="Private Button7 As Button";
mostCurrent._button7 = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 57;BA.debugLine="Private Button8 As Button";
mostCurrent._button8 = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 58;BA.debugLine="Private Button9 As Button";
mostCurrent._button9 = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 60;BA.debugLine="Private off_1 As Button";
mostCurrent._off_1 = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 61;BA.debugLine="Private off_2 As Button";
mostCurrent._off_2 = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 62;BA.debugLine="Private on_1 As Button";
mostCurrent._on_1 = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 63;BA.debugLine="Private on_2 As Button";
mostCurrent._on_2 = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 64;BA.debugLine="Private panel_out1 As Panel";
mostCurrent._panel_out1 = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 65;BA.debugLine="Private sterowanie_out1 As Label";
mostCurrent._sterowanie_out1 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 66;BA.debugLine="Private sterowanie_out2 As Label";
mostCurrent._sterowanie_out2 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 68;BA.debugLine="Private timer_2 As Button";
mostCurrent._timer_2 = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 69;BA.debugLine="Private www As Label";
mostCurrent._www = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 70;BA.debugLine="Private button_raport As Button";
mostCurrent._button_raport = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 72;BA.debugLine="Private button_save_2 As Button";
mostCurrent._button_save_2 = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 74;BA.debugLine="Private label_timer_out2 As Label";
mostCurrent._label_timer_out2 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 75;BA.debugLine="Private label_wlaczaj_2 As Label";
mostCurrent._label_wlaczaj_2 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 77;BA.debugLine="Private panel_timer_2 As Panel";
mostCurrent._panel_timer_2 = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 84;BA.debugLine="Private ARM_ON As Button";
mostCurrent._arm_on = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 85;BA.debugLine="Private ARM_OFF As Button";
mostCurrent._arm_off = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 86;BA.debugLine="Private Panel_ARM As Panel";
mostCurrent._panel_arm = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 87;BA.debugLine="Private timer As EditText";
mostCurrent._timer = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 89;BA.debugLine="End Sub";
return "";
}
public static String  _label2_click() throws Exception{
anywheresoftware.b4a.objects.IntentWrapper _i = null;
 //BA.debugLineNum = 508;BA.debugLine="Private Sub Label2_Click";
 //BA.debugLineNum = 509;BA.debugLine="Log(\"Label2_Click: Kliknięto Label2. Próba otwarc";
anywheresoftware.b4a.keywords.Common.LogImpl("62883585","Label2_Click: Kliknięto Label2. Próba otwarcia strony sonfy.pl.",0);
 //BA.debugLineNum = 510;BA.debugLine="Dim i As Intent ' Deklarujemy zmienną typu Intent";
_i = new anywheresoftware.b4a.objects.IntentWrapper();
 //BA.debugLineNum = 515;BA.debugLine="i.Initialize(i.ACTION_VIEW, \"https://www.sonfy.pl";
_i.Initialize(_i.ACTION_VIEW,"https://www.sonfy.pl");
 //BA.debugLineNum = 518;BA.debugLine="StartActivity(i)";
anywheresoftware.b4a.keywords.Common.StartActivity(processBA,(Object)(_i.getObject()));
 //BA.debugLineNum = 519;BA.debugLine="Log(\"Label2_Click: Intent do otwarcia strony uruc";
anywheresoftware.b4a.keywords.Common.LogImpl("62883595","Label2_Click: Intent do otwarcia strony uruchomiony.",0);
 //BA.debugLineNum = 520;BA.debugLine="End Sub";
return "";
}
public static String  _lista_numerow_click() throws Exception{
 //BA.debugLineNum = 244;BA.debugLine="Private Sub lista_numerow_Click";
 //BA.debugLineNum = 245;BA.debugLine="Log(\"lista_numerow_Click: Krótkie kliknięcie 'Lis";
anywheresoftware.b4a.keywords.Common.LogImpl("61114113","lista_numerow_Click: Krótkie kliknięcie 'Lista numerów'.",0);
 //BA.debugLineNum = 246;BA.debugLine="CallSub (\"Main\", za_krotko)";
anywheresoftware.b4a.keywords.Common.CallSubNew(processBA,(Object)("Main"),_za_krotko());
 //BA.debugLineNum = 247;BA.debugLine="End Sub";
return "";
}
public static String  _mnu1_click() throws Exception{
 //BA.debugLineNum = 107;BA.debugLine="Sub mnu1_Click";
 //BA.debugLineNum = 108;BA.debugLine="TabStrip1.ScrollTo(0, True)";
mostCurrent._tabstrip1.ScrollTo((int) (0),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 109;BA.debugLine="Log(\"Menu: Wybrano zakładkę 'Sterowanie'.\")";
anywheresoftware.b4a.keywords.Common.LogImpl("6196610","Menu: Wybrano zakładkę 'Sterowanie'.",0);
 //BA.debugLineNum = 110;BA.debugLine="End Sub";
return "";
}
public static String  _mnu2_click() throws Exception{
 //BA.debugLineNum = 112;BA.debugLine="Sub mnu2_Click";
 //BA.debugLineNum = 113;BA.debugLine="TabStrip1.ScrollTo(1, True)";
mostCurrent._tabstrip1.ScrollTo((int) (1),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 114;BA.debugLine="Log(\"Menu: Wybrano zakładkę 'Ustawienia'.\")";
anywheresoftware.b4a.keywords.Common.LogImpl("6262146","Menu: Wybrano zakładkę 'Ustawienia'.",0);
 //BA.debugLineNum = 115;BA.debugLine="End Sub";
return "";
}
public static String  _mnu3_click() throws Exception{
 //BA.debugLineNum = 117;BA.debugLine="Sub mnu3_Click";
 //BA.debugLineNum = 118;BA.debugLine="TabStrip1.ScrollTo(20, True)";
mostCurrent._tabstrip1.ScrollTo((int) (20),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 119;BA.debugLine="Log(\"Menu: Wybrano zakładkę 'O aplikacji'.\")";
anywheresoftware.b4a.keywords.Common.LogImpl("6327682","Menu: Wybrano zakładkę 'O aplikacji'.",0);
 //BA.debugLineNum = 120;BA.debugLine="End Sub";
return "";
}
public static String  _off_1_click() throws Exception{
 //BA.debugLineNum = 431;BA.debugLine="Private Sub off_1_Click";
 //BA.debugLineNum = 432;BA.debugLine="Log(\"off_1_Click: Krótkie kliknięcie 'OFF 1'.\")";
anywheresoftware.b4a.keywords.Common.LogImpl("62162689","off_1_Click: Krótkie kliknięcie 'OFF 1'.",0);
 //BA.debugLineNum = 433;BA.debugLine="CallSub (\"Main\", za_krotko)";
anywheresoftware.b4a.keywords.Common.CallSubNew(processBA,(Object)("Main"),_za_krotko());
 //BA.debugLineNum = 434;BA.debugLine="End Sub";
return "";
}
public static String  _off_1_longclick() throws Exception{
 //BA.debugLineNum = 258;BA.debugLine="Private Sub off_1_LongClick";
 //BA.debugLineNum = 259;BA.debugLine="Log($\"off_1_LongClick: Długie kliknięcie 'OFF 1'.";
anywheresoftware.b4a.keywords.Common.LogImpl("61245185",("off_1_LongClick: Długie kliknięcie 'OFF 1'. Wysyłam komendę OUT1#OFF do "+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(mostCurrent._numer_urzadzenia.getText()))+""),0);
 //BA.debugLineNum = 260;BA.debugLine="WyslijKomende(numer_urzadzenia.Text, \"OUT1#OFF\")";
_wyslijkomende(mostCurrent._numer_urzadzenia.getText(),"OUT1#OFF");
 //BA.debugLineNum = 261;BA.debugLine="CallSub(\"Main\", \"wykonuje\")";
anywheresoftware.b4a.keywords.Common.CallSubNew(processBA,(Object)("Main"),"wykonuje");
 //BA.debugLineNum = 262;BA.debugLine="End Sub";
return "";
}
public static String  _off_2_click() throws Exception{
 //BA.debugLineNum = 435;BA.debugLine="Private Sub off_2_Click";
 //BA.debugLineNum = 436;BA.debugLine="Log(\"off_2_Click: Krótkie kliknięcie 'OFF 2'.\")";
anywheresoftware.b4a.keywords.Common.LogImpl("62228225","off_2_Click: Krótkie kliknięcie 'OFF 2'.",0);
 //BA.debugLineNum = 437;BA.debugLine="CallSub (\"Main\", za_krotko)";
anywheresoftware.b4a.keywords.Common.CallSubNew(processBA,(Object)("Main"),_za_krotko());
 //BA.debugLineNum = 438;BA.debugLine="End Sub";
return "";
}
public static String  _off_2_longclick() throws Exception{
 //BA.debugLineNum = 270;BA.debugLine="Private Sub off_2_LongClick";
 //BA.debugLineNum = 271;BA.debugLine="Log($\"off_2_LongClick: Długie kliknięcie 'OFF 2'.";
anywheresoftware.b4a.keywords.Common.LogImpl("61376257",("off_2_LongClick: Długie kliknięcie 'OFF 2'. Wysyłam komendę OUT2#OFF do "+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(mostCurrent._numer_urzadzenia.getText()))+""),0);
 //BA.debugLineNum = 272;BA.debugLine="WyslijKomende(numer_urzadzenia.Text, \"OUT2#OFF\")";
_wyslijkomende(mostCurrent._numer_urzadzenia.getText(),"OUT2#OFF");
 //BA.debugLineNum = 273;BA.debugLine="CallSub(\"Main\", \"wykonuje\")";
anywheresoftware.b4a.keywords.Common.CallSubNew(processBA,(Object)("Main"),"wykonuje");
 //BA.debugLineNum = 274;BA.debugLine="End Sub";
return "";
}
public static String  _on_1_click() throws Exception{
 //BA.debugLineNum = 423;BA.debugLine="Private Sub on_1_Click";
 //BA.debugLineNum = 424;BA.debugLine="Log(\"on_1_Click: Krótkie kliknięcie 'ON 1'.\")";
anywheresoftware.b4a.keywords.Common.LogImpl("62031617","on_1_Click: Krótkie kliknięcie 'ON 1'.",0);
 //BA.debugLineNum = 425;BA.debugLine="CallSub (\"Main\", za_krotko)";
anywheresoftware.b4a.keywords.Common.CallSubNew(processBA,(Object)("Main"),_za_krotko());
 //BA.debugLineNum = 426;BA.debugLine="End Sub";
return "";
}
public static String  _on_1_longclick() throws Exception{
 //BA.debugLineNum = 252;BA.debugLine="Private Sub on_1_LongClick";
 //BA.debugLineNum = 253;BA.debugLine="Log($\"on_1_LongClick: Długie kliknięcie 'ON 1'. W";
anywheresoftware.b4a.keywords.Common.LogImpl("61179649",("on_1_LongClick: Długie kliknięcie 'ON 1'. Wysyłam komendę OUT1#ON do "+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(mostCurrent._numer_urzadzenia.getText()))+""),0);
 //BA.debugLineNum = 254;BA.debugLine="WyslijKomende(numer_urzadzenia.Text, \"OUT1#ON\")";
_wyslijkomende(mostCurrent._numer_urzadzenia.getText(),"OUT1#ON");
 //BA.debugLineNum = 255;BA.debugLine="CallSub(\"Main\", \"wykonuje\")";
anywheresoftware.b4a.keywords.Common.CallSubNew(processBA,(Object)("Main"),"wykonuje");
 //BA.debugLineNum = 256;BA.debugLine="End Sub";
return "";
}
public static String  _on_2_click() throws Exception{
 //BA.debugLineNum = 427;BA.debugLine="Private Sub on_2_Click";
 //BA.debugLineNum = 428;BA.debugLine="Log(\"on_2_Click: Krótkie kliknięcie 'ON 2'.\")";
anywheresoftware.b4a.keywords.Common.LogImpl("62097153","on_2_Click: Krótkie kliknięcie 'ON 2'.",0);
 //BA.debugLineNum = 429;BA.debugLine="CallSub (\"Main\", za_krotko)";
anywheresoftware.b4a.keywords.Common.CallSubNew(processBA,(Object)("Main"),_za_krotko());
 //BA.debugLineNum = 430;BA.debugLine="End Sub";
return "";
}
public static String  _on_2_longclick() throws Exception{
 //BA.debugLineNum = 264;BA.debugLine="Private Sub on_2_LongClick";
 //BA.debugLineNum = 265;BA.debugLine="Log($\"on_2_LongClick: Długie kliknięcie 'ON 2'. W";
anywheresoftware.b4a.keywords.Common.LogImpl("61310721",("on_2_LongClick: Długie kliknięcie 'ON 2'. Wysyłam komendę OUT2#ON do "+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(mostCurrent._numer_urzadzenia.getText()))+""),0);
 //BA.debugLineNum = 266;BA.debugLine="WyslijKomende(numer_urzadzenia.Text, \"OUT2#ON\")";
_wyslijkomende(mostCurrent._numer_urzadzenia.getText(),"OUT2#ON");
 //BA.debugLineNum = 267;BA.debugLine="CallSub(\"Main\", \"wykonuje\")";
anywheresoftware.b4a.keywords.Common.CallSubNew(processBA,(Object)("Main"),"wykonuje");
 //BA.debugLineNum = 268;BA.debugLine="End Sub";
return "";
}
public static String  _otworz_click() throws Exception{
 //BA.debugLineNum = 230;BA.debugLine="Private Sub Otworz_Click";
 //BA.debugLineNum = 231;BA.debugLine="Log(\"Otworz_Click: Krótkie kliknięcie 'Otwórz'.\")";
anywheresoftware.b4a.keywords.Common.LogImpl("6983041","Otworz_Click: Krótkie kliknięcie 'Otwórz'.",0);
 //BA.debugLineNum = 232;BA.debugLine="CallSub (\"Main\", za_krotko)";
anywheresoftware.b4a.keywords.Common.CallSubNew(processBA,(Object)("Main"),_za_krotko());
 //BA.debugLineNum = 233;BA.debugLine="End Sub";
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
 //BA.debugLineNum = 19;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 21;BA.debugLine="Dim Timer3 As Timer";
_timer3 = new anywheresoftware.b4a.objects.Timer();
 //BA.debugLineNum = 22;BA.debugLine="Public rp As RuntimePermissions";
_rp = new anywheresoftware.b4a.objects.RuntimePermissions();
 //BA.debugLineNum = 23;BA.debugLine="Dim PE As PhoneEvents";
_pe = new anywheresoftware.b4a.phone.PhoneEvents();
 //BA.debugLineNum = 24;BA.debugLine="Dim PhoneId As PhoneId";
_phoneid = new anywheresoftware.b4a.phone.Phone.PhoneId();
 //BA.debugLineNum = 28;BA.debugLine="End Sub";
return "";
}
public static String  _raport_click() throws Exception{
 //BA.debugLineNum = 458;BA.debugLine="Private Sub Raport_Click";
 //BA.debugLineNum = 459;BA.debugLine="Log(\"Raport_Click: Krótkie kliknięcie 'Raport' (k";
anywheresoftware.b4a.keywords.Common.LogImpl("62424833","Raport_Click: Krótkie kliknięcie 'Raport' (kontrolka Label).",0);
 //BA.debugLineNum = 460;BA.debugLine="CallSub (\"Main\", za_krotko)";
anywheresoftware.b4a.keywords.Common.CallSubNew(processBA,(Object)("Main"),_za_krotko());
 //BA.debugLineNum = 461;BA.debugLine="End Sub";
return "";
}
public static String  _read_text() throws Exception{
 //BA.debugLineNum = 165;BA.debugLine="Private Sub read_text";
 //BA.debugLineNum = 166;BA.debugLine="Log(\"read_text: Rozpoczęcie operacji odczytu dany";
anywheresoftware.b4a.keywords.Common.LogImpl("6589825","read_text: Rozpoczęcie operacji odczytu danych.",0);
 //BA.debugLineNum = 167;BA.debugLine="sets = File.ReadMap(File.DirInternal, \"expander.s";
mostCurrent._sets = anywheresoftware.b4a.keywords.Common.File.ReadMap(anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"expander.set");
 //BA.debugLineNum = 168;BA.debugLine="numer_urzadzenia.Text = sets.Get(\"numer_urzadzeni";
mostCurrent._numer_urzadzenia.setText(BA.ObjectToCharSequence(mostCurrent._sets.Get((Object)("numer_urzadzenia"))));
 //BA.debugLineNum = 169;BA.debugLine="timer.Text = sets.Get(\"timer\")";
mostCurrent._timer.setText(BA.ObjectToCharSequence(mostCurrent._sets.Get((Object)("timer"))));
 //BA.debugLineNum = 170;BA.debugLine="Log($\"read_text: Dane ODCZYTANO z pliku 'expander";
anywheresoftware.b4a.keywords.Common.LogImpl("6589829",("read_text: Dane ODCZYTANO z pliku 'expander.set' - numer_urzadzenia: "+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(mostCurrent._numer_urzadzenia.getText()))+", timer: "+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(mostCurrent._timer.getText()))+""),0);
 //BA.debugLineNum = 171;BA.debugLine="End Sub";
return "";
}
public static String  _save_tak_click() throws Exception{
 //BA.debugLineNum = 469;BA.debugLine="Private Sub Save_Tak_Click";
 //BA.debugLineNum = 470;BA.debugLine="Log(\"Save_Tak_Click: Krótkie kliknięcie 'Zapisz T";
anywheresoftware.b4a.keywords.Common.LogImpl("62555905","Save_Tak_Click: Krótkie kliknięcie 'Zapisz Tak'.",0);
 //BA.debugLineNum = 471;BA.debugLine="CallSub (\"Main\", za_krotko)";
anywheresoftware.b4a.keywords.Common.CallSubNew(processBA,(Object)("Main"),_za_krotko());
 //BA.debugLineNum = 472;BA.debugLine="End Sub";
return "";
}
public static void  _save_tak_longclick() throws Exception{
ResumableSub_Save_Tak_LongClick rsub = new ResumableSub_Save_Tak_LongClick(null);
rsub.resume(processBA, null);
}
public static class ResumableSub_Save_Tak_LongClick extends BA.ResumableSub {
public ResumableSub_Save_Tak_LongClick(megaelektronik.expander_google.main parent) {
this.parent = parent;
}
megaelektronik.expander_google.main parent;

@Override
public void resume(BA ba, Object[] result) throws Exception{

    while (true) {
        switch (state) {
            case -1:
return;

case 0:
//C
this.state = -1;
 //BA.debugLineNum = 196;BA.debugLine="Log(\"Save_Tak_LongClick: Długie kliknięcie 'Zapis";
anywheresoftware.b4a.keywords.Common.LogImpl("6786433","Save_Tak_LongClick: Długie kliknięcie 'Zapisz numer urządzenia'.",0);
 //BA.debugLineNum = 197;BA.debugLine="sets.Clear";
parent.mostCurrent._sets.Clear();
 //BA.debugLineNum = 198;BA.debugLine="sets.Put(\"numer_urzadzenia\", numer_urzadzenia.Tex";
parent.mostCurrent._sets.Put((Object)("numer_urzadzenia"),(Object)(parent.mostCurrent._numer_urzadzenia.getText()));
 //BA.debugLineNum = 199;BA.debugLine="sets.Put(\"timer\", timer.Text)";
parent.mostCurrent._sets.Put((Object)("timer"),(Object)(parent.mostCurrent._timer.getText()));
 //BA.debugLineNum = 200;BA.debugLine="Log($\"Save_Tak_LongClick: Dane do zapisu - numer_";
anywheresoftware.b4a.keywords.Common.LogImpl("6786437",("Save_Tak_LongClick: Dane do zapisu - numer_urzadzenia: "+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(parent.mostCurrent._numer_urzadzenia.getText()))+", timer: "+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(parent.mostCurrent._timer.getText()))+""),0);
 //BA.debugLineNum = 201;BA.debugLine="File.WriteMap(File.DirInternal, \"expander.set\", s";
anywheresoftware.b4a.keywords.Common.File.WriteMap(anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"expander.set",parent.mostCurrent._sets);
 //BA.debugLineNum = 202;BA.debugLine="Log(\"Save_Tak_LongClick: Dane ZAPISANO do pliku '";
anywheresoftware.b4a.keywords.Common.LogImpl("6786439","Save_Tak_LongClick: Dane ZAPISANO do pliku 'expander.set' z Save_Tak_LongClick.",0);
 //BA.debugLineNum = 203;BA.debugLine="CallSub (\"Main\",wykonuje)";
anywheresoftware.b4a.keywords.Common.CallSubNew(processBA,(Object)("Main"),_wykonuje());
 //BA.debugLineNum = 204;BA.debugLine="Wait For (2000) Timeout";
anywheresoftware.b4a.keywords.Common.WaitFor("timeout", processBA, this, (Object)(2000));
this.state = 1;
return;
case 1:
//C
this.state = -1;
;
 //BA.debugLineNum = 205;BA.debugLine="CallSub (\"Main\", write_text) ' Odczyt po zapisie,";
anywheresoftware.b4a.keywords.Common.CallSubNew(processBA,(Object)("Main"),_write_text());
 //BA.debugLineNum = 206;BA.debugLine="Log(\"Save_Tak_LongClick: Zakończono operację zapi";
anywheresoftware.b4a.keywords.Common.LogImpl("6786443","Save_Tak_LongClick: Zakończono operację zapisu i odczytu.",0);
 //BA.debugLineNum = 207;BA.debugLine="End Sub";
if (true) break;

            }
        }
    }
}
public static String  _timer_2_click() throws Exception{
 //BA.debugLineNum = 443;BA.debugLine="Private Sub timer_2_Click";
 //BA.debugLineNum = 444;BA.debugLine="Log(\"timer_2_Click: Krótkie kliknięcie 'Timer 2'.";
anywheresoftware.b4a.keywords.Common.LogImpl("62293761","timer_2_Click: Krótkie kliknięcie 'Timer 2'.",0);
 //BA.debugLineNum = 445;BA.debugLine="CallSub (\"Main\", za_krotko)";
anywheresoftware.b4a.keywords.Common.CallSubNew(processBA,(Object)("Main"),_za_krotko());
 //BA.debugLineNum = 446;BA.debugLine="End Sub";
return "";
}
public static void  _timer_2_longclick() throws Exception{
ResumableSub_timer_2_LongClick rsub = new ResumableSub_timer_2_LongClick(null);
rsub.resume(processBA, null);
}
public static class ResumableSub_timer_2_LongClick extends BA.ResumableSub {
public ResumableSub_timer_2_LongClick(megaelektronik.expander_google.main parent) {
this.parent = parent;
}
megaelektronik.expander_google.main parent;
int _seconds = 0;
int _hours = 0;
int _minutes = 0;
int _remainingseconds = 0;
String _timemessage = "";

@Override
public void resume(BA ba, Object[] result) throws Exception{

    while (true) {
        switch (state) {
            case -1:
return;

case 0:
//C
this.state = 1;
 //BA.debugLineNum = 298;BA.debugLine="Log(\"timer_LongClick: Długie kliknięcie 'Timer 2'";
anywheresoftware.b4a.keywords.Common.LogImpl("61638401","timer_LongClick: Długie kliknięcie 'Timer 2'.",0);
 //BA.debugLineNum = 299;BA.debugLine="If timer.Text.Length > 5 Then";
if (true) break;

case 1:
//if
this.state = 4;
if (parent.mostCurrent._timer.getText().length()>5) { 
this.state = 3;
}if (true) break;

case 3:
//C
this.state = 4;
 //BA.debugLineNum = 300;BA.debugLine="MsgboxAsync(\"Wprowadź maksymalnie 5 cyfr\", \"Błąd";
anywheresoftware.b4a.keywords.Common.MsgboxAsync(BA.ObjectToCharSequence("Wprowadź maksymalnie 5 cyfr"),BA.ObjectToCharSequence("Błąd"),processBA);
 //BA.debugLineNum = 301;BA.debugLine="Log(\"timer_LongClick: Błąd walidacji - długość t";
anywheresoftware.b4a.keywords.Common.LogImpl("61638404","timer_LongClick: Błąd walidacji - długość timera > 5 cyfr.",0);
 //BA.debugLineNum = 302;BA.debugLine="Return";
if (true) return ;
 if (true) break;
;
 //BA.debugLineNum = 305;BA.debugLine="If IsNumber(timer.Text) = False Then";

case 4:
//if
this.state = 7;
if (anywheresoftware.b4a.keywords.Common.IsNumber(parent.mostCurrent._timer.getText())==anywheresoftware.b4a.keywords.Common.False) { 
this.state = 6;
}if (true) break;

case 6:
//C
this.state = 7;
 //BA.debugLineNum = 306;BA.debugLine="MsgboxAsync(\"Wprowadź tylko cyfry\", \"Błąd\")";
anywheresoftware.b4a.keywords.Common.MsgboxAsync(BA.ObjectToCharSequence("Wprowadź tylko cyfry"),BA.ObjectToCharSequence("Błąd"),processBA);
 //BA.debugLineNum = 307;BA.debugLine="Log(\"timer_LongClick: Błąd walidacji - timer nie";
anywheresoftware.b4a.keywords.Common.LogImpl("61638410","timer_LongClick: Błąd walidacji - timer nie jest liczbą.",0);
 //BA.debugLineNum = 308;BA.debugLine="Return";
if (true) return ;
 if (true) break;
;
 //BA.debugLineNum = 311;BA.debugLine="If timer.Text > 99998 Then";

case 7:
//if
this.state = 10;
if ((double)(Double.parseDouble(parent.mostCurrent._timer.getText()))>99998) { 
this.state = 9;
}if (true) break;

case 9:
//C
this.state = 10;
 //BA.debugLineNum = 312;BA.debugLine="MsgboxAsync(\"Liczba nie może być większa niż 999";
anywheresoftware.b4a.keywords.Common.MsgboxAsync(BA.ObjectToCharSequence("Liczba nie może być większa niż 99998s"),BA.ObjectToCharSequence("Błąd"),processBA);
 //BA.debugLineNum = 313;BA.debugLine="Log(\"timer_2_LongClick: Błąd walidacji - timer >";
anywheresoftware.b4a.keywords.Common.LogImpl("61638416","timer_2_LongClick: Błąd walidacji - timer > 99998.",0);
 //BA.debugLineNum = 314;BA.debugLine="Return";
if (true) return ;
 if (true) break;

case 10:
//C
this.state = 11;
;
 //BA.debugLineNum = 318;BA.debugLine="Dim seconds As Int = timer.Text";
_seconds = (int)(Double.parseDouble(parent.mostCurrent._timer.getText()));
 //BA.debugLineNum = 319;BA.debugLine="Dim hours As Int = seconds / 3600";
_hours = (int) (_seconds/(double)3600);
 //BA.debugLineNum = 320;BA.debugLine="Dim minutes As Int = (seconds Mod 3600) / 60";
_minutes = (int) ((_seconds%3600)/(double)60);
 //BA.debugLineNum = 321;BA.debugLine="Dim remainingSeconds As Int = seconds Mod 60";
_remainingseconds = (int) (_seconds%60);
 //BA.debugLineNum = 324;BA.debugLine="Dim timeMessage As String";
_timemessage = "";
 //BA.debugLineNum = 325;BA.debugLine="If hours > 0 Then";
if (true) break;

case 11:
//if
this.state = 29;
if (_hours>0) { 
this.state = 13;
}else if(_minutes>0) { 
this.state = 22;
}else {
this.state = 28;
}if (true) break;

case 13:
//C
this.state = 14;
 //BA.debugLineNum = 326;BA.debugLine="timeMessage = $\"Timer włączony na ${hours} godz.";
_timemessage = ("Timer włączony na "+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(_hours))+" godz.");
 //BA.debugLineNum = 327;BA.debugLine="If minutes > 0 Or remainingSeconds > 0 Then";
if (true) break;

case 14:
//if
this.state = 17;
if (_minutes>0 || _remainingseconds>0) { 
this.state = 16;
}if (true) break;

case 16:
//C
this.state = 17;
 //BA.debugLineNum = 328;BA.debugLine="timeMessage = timeMessage & $\", ${minutes} min.";
_timemessage = _timemessage+(", "+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(_minutes))+" min.");
 if (true) break;
;
 //BA.debugLineNum = 330;BA.debugLine="If remainingSeconds > 0 Then";

case 17:
//if
this.state = 20;
if (_remainingseconds>0) { 
this.state = 19;
}if (true) break;

case 19:
//C
this.state = 20;
 //BA.debugLineNum = 331;BA.debugLine="timeMessage = timeMessage & $\", ${remainingSeco";
_timemessage = _timemessage+(", "+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(_remainingseconds))+" sek.");
 if (true) break;

case 20:
//C
this.state = 29;
;
 if (true) break;

case 22:
//C
this.state = 23;
 //BA.debugLineNum = 334;BA.debugLine="timeMessage = $\"Timer włączony na ${minutes} min";
_timemessage = ("Timer włączony na "+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(_minutes))+" min.");
 //BA.debugLineNum = 335;BA.debugLine="If remainingSeconds > 0 Then";
if (true) break;

case 23:
//if
this.state = 26;
if (_remainingseconds>0) { 
this.state = 25;
}if (true) break;

case 25:
//C
this.state = 26;
 //BA.debugLineNum = 336;BA.debugLine="timeMessage = timeMessage & $\", ${remainingSeco";
_timemessage = _timemessage+(", "+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(_remainingseconds))+" sek.");
 if (true) break;

case 26:
//C
this.state = 29;
;
 if (true) break;

case 28:
//C
this.state = 29;
 //BA.debugLineNum = 339;BA.debugLine="timeMessage = $\"Timer włączony na ${remainingSec";
_timemessage = ("Timer włączony na "+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(_remainingseconds))+" sek.");
 if (true) break;

case 29:
//C
this.state = -1;
;
 //BA.debugLineNum = 343;BA.debugLine="MsgboxAsync(timeMessage, \"⏱ Informacja \"&timer.Te";
anywheresoftware.b4a.keywords.Common.MsgboxAsync(BA.ObjectToCharSequence(_timemessage),BA.ObjectToCharSequence("⏱ Informacja "+parent.mostCurrent._timer.getText()),processBA);
 //BA.debugLineNum = 344;BA.debugLine="Log($\"timer_2_LongClick: Wyświetlono informację o";
anywheresoftware.b4a.keywords.Common.LogImpl("61638447",("timer_2_LongClick: Wyświetlono informację o timerze: "+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(_timemessage))+""),0);
 //BA.debugLineNum = 345;BA.debugLine="Sleep(1500)";
anywheresoftware.b4a.keywords.Common.Sleep(mostCurrent.activityBA,this,(int) (1500));
this.state = 30;
return;
case 30:
//C
this.state = -1;
;
 //BA.debugLineNum = 346;BA.debugLine="WyslijKomende(numer_urzadzenia.Text, \"OUT2#ON \" &";
_wyslijkomende(parent.mostCurrent._numer_urzadzenia.getText(),"OUT2#ON "+parent.mostCurrent._timer.getText());
 //BA.debugLineNum = 347;BA.debugLine="Log($\"timer_2_LongClick: Wysłano komendę OUT2#ON";
anywheresoftware.b4a.keywords.Common.LogImpl("61638450",("timer_2_LongClick: Wysłano komendę OUT2#ON z wartością timera: "+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(parent.mostCurrent._timer.getText()))+""),0);
 //BA.debugLineNum = 349;BA.debugLine="End Sub";
if (true) break;

            }
        }
    }
}
public static String  _timer_tick() throws Exception{
 //BA.debugLineNum = 186;BA.debugLine="Sub Timer_Tick";
 //BA.debugLineNum = 187;BA.debugLine="Log (\"Timer_Tick: Timer odliczył czas. Ukrywam Pr";
anywheresoftware.b4a.keywords.Common.LogImpl("6720897","Timer_Tick: Timer odliczył czas. Ukrywam ProgressDialog.",0);
 //BA.debugLineNum = 188;BA.debugLine="ProgressDialogHide";
anywheresoftware.b4a.keywords.Common.ProgressDialogHide();
 //BA.debugLineNum = 189;BA.debugLine="Timer3.Enabled =False";
_timer3.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 190;BA.debugLine="End Sub";
return "";
}
public static String  _usun_click() throws Exception{
 //BA.debugLineNum = 486;BA.debugLine="Private Sub usun_Click";
 //BA.debugLineNum = 487;BA.debugLine="Log(\"usun_Click: Krótkie kliknięcie 'Usuń'.\")";
anywheresoftware.b4a.keywords.Common.LogImpl("62686977","usun_Click: Krótkie kliknięcie 'Usuń'.",0);
 //BA.debugLineNum = 488;BA.debugLine="CallSub (\"Main\", za_krotko)";
anywheresoftware.b4a.keywords.Common.CallSubNew(processBA,(Object)("Main"),_za_krotko());
 //BA.debugLineNum = 489;BA.debugLine="End Sub";
return "";
}
public static String  _usun_longclick() throws Exception{
 //BA.debugLineNum = 220;BA.debugLine="Private Sub Usun_LongClick";
 //BA.debugLineNum = 221;BA.debugLine="Log(\"Usun_LongClick: Długie kliknięcie 'Usuń'. Wy";
anywheresoftware.b4a.keywords.Common.LogImpl("6917505","Usun_LongClick: Długie kliknięcie 'Usuń'. Wysyłam komendę DEL.",0);
 //BA.debugLineNum = 222;BA.debugLine="WyslijKomende (numer_urzadzenia.Text ,\"DEL^----^^";
_wyslijkomende(mostCurrent._numer_urzadzenia.getText(),"DEL^----^^");
 //BA.debugLineNum = 223;BA.debugLine="CallSub (\"Main\", wykonuje)";
anywheresoftware.b4a.keywords.Common.CallSubNew(processBA,(Object)("Main"),_wykonuje());
 //BA.debugLineNum = 224;BA.debugLine="CallSub (\"Main\",za_krotko_2)";
anywheresoftware.b4a.keywords.Common.CallSubNew(processBA,(Object)("Main"),_za_krotko_2());
 //BA.debugLineNum = 225;BA.debugLine="End Sub";
return "";
}
public static String  _write_text() throws Exception{
 //BA.debugLineNum = 150;BA.debugLine="Private Sub write_text";
 //BA.debugLineNum = 151;BA.debugLine="Log(\"write_text: Rozpoczęcie operacji zapisu dany";
anywheresoftware.b4a.keywords.Common.LogImpl("6524289","write_text: Rozpoczęcie operacji zapisu danych.",0);
 //BA.debugLineNum = 152;BA.debugLine="sets.Clear";
mostCurrent._sets.Clear();
 //BA.debugLineNum = 153;BA.debugLine="Log(\"write_text: Mapa 'sets' wyczyszczona.\")";
anywheresoftware.b4a.keywords.Common.LogImpl("6524291","write_text: Mapa 'sets' wyczyszczona.",0);
 //BA.debugLineNum = 154;BA.debugLine="sets.Put(\"numer_urzadzenia\", numer_urzadzenia.Tex";
mostCurrent._sets.Put((Object)("numer_urzadzenia"),(Object)(mostCurrent._numer_urzadzenia.getText()));
 //BA.debugLineNum = 155;BA.debugLine="sets.Put(\"timer\", timer.Text)";
mostCurrent._sets.Put((Object)("timer"),(Object)(mostCurrent._timer.getText()));
 //BA.debugLineNum = 156;BA.debugLine="Log($\"write_text: Przygotowane dane do zapisu - n";
anywheresoftware.b4a.keywords.Common.LogImpl("6524294",("write_text: Przygotowane dane do zapisu - numer_urzadzenia: "+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(mostCurrent._numer_urzadzenia.getText()))+", timer: "+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(mostCurrent._timer.getText()))+""),0);
 //BA.debugLineNum = 157;BA.debugLine="File.WriteMap(File.DirInternal, \"expander.set\", s";
anywheresoftware.b4a.keywords.Common.File.WriteMap(anywheresoftware.b4a.keywords.Common.File.getDirInternal(),"expander.set",mostCurrent._sets);
 //BA.debugLineNum = 158;BA.debugLine="Log(\"write_text: Dane ZAPISANO do pliku 'expander";
anywheresoftware.b4a.keywords.Common.LogImpl("6524296","write_text: Dane ZAPISANO do pliku 'expander.set'.",0);
 //BA.debugLineNum = 159;BA.debugLine="CallSub (\"Main\",za_krotko_2)";
anywheresoftware.b4a.keywords.Common.CallSubNew(processBA,(Object)("Main"),_za_krotko_2());
 //BA.debugLineNum = 160;BA.debugLine="StartActivity (\"main\")";
anywheresoftware.b4a.keywords.Common.StartActivity(processBA,(Object)("main"));
 //BA.debugLineNum = 161;BA.debugLine="Log(\"write_text: Zakończenie operacji zapisu dany";
anywheresoftware.b4a.keywords.Common.LogImpl("6524299","write_text: Zakończenie operacji zapisu danych.",0);
 //BA.debugLineNum = 162;BA.debugLine="End Sub";
return "";
}
public static String  _wykonuje() throws Exception{
 //BA.debugLineNum = 174;BA.debugLine="Sub wykonuje";
 //BA.debugLineNum = 175;BA.debugLine="Log(\"wykonuje: Wyświetlam ProgressDialog.\")";
anywheresoftware.b4a.keywords.Common.LogImpl("6655361","wykonuje: Wyświetlam ProgressDialog.",0);
 //BA.debugLineNum = 176;BA.debugLine="ProgressDialogShow ( \"Wykonuję chwileczkę...\")";
anywheresoftware.b4a.keywords.Common.ProgressDialogShow(mostCurrent.activityBA,BA.ObjectToCharSequence("Wykonuję chwileczkę..."));
 //BA.debugLineNum = 177;BA.debugLine="Timer3.Enabled=False";
_timer3.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 178;BA.debugLine="Timer3.Initialize(\"Timer\",5500)";
_timer3.Initialize(processBA,"Timer",(long) (5500));
 //BA.debugLineNum = 179;BA.debugLine="Timer3.Enabled =True";
_timer3.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 180;BA.debugLine="Log(\"wykonuje: Timer3 uruchomiony na 5.5 sekundy.";
anywheresoftware.b4a.keywords.Common.LogImpl("6655366","wykonuje: Timer3 uruchomiony na 5.5 sekundy.",0);
 //BA.debugLineNum = 181;BA.debugLine="End Sub";
return "";
}
public static String  _wyslijkomende(String _numer,String _tresc) throws Exception{
anywheresoftware.b4a.objects.IntentWrapper _sendintent = null;
 //BA.debugLineNum = 353;BA.debugLine="Private Sub WyslijKomende(numer As String, tresc A";
 //BA.debugLineNum = 354;BA.debugLine="Log($\"WyslijKomende: Próba wysłania komendy SMS.";
anywheresoftware.b4a.keywords.Common.LogImpl("61703937",("WyslijKomende: Próba wysłania komendy SMS. Numer: '"+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(_numer))+"', Treść: '"+anywheresoftware.b4a.keywords.Common.SmartStringFormatter("",(Object)(_tresc))+"'"),0);
 //BA.debugLineNum = 355;BA.debugLine="If numer.Trim = \"\" Then";
if ((_numer.trim()).equals("")) { 
 //BA.debugLineNum = 356;BA.debugLine="ToastMessageShow(\"Numer telefonu jest pusty!\", F";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Numer telefonu jest pusty!"),anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 357;BA.debugLine="Log(\"WyslijKomende: Błąd - numer telefonu jest p";
anywheresoftware.b4a.keywords.Common.LogImpl("61703940","WyslijKomende: Błąd - numer telefonu jest pusty, nie można wysłać komendy.",0);
 //BA.debugLineNum = 358;BA.debugLine="Return";
if (true) return "";
 };
 //BA.debugLineNum = 360;BA.debugLine="Dim SendIntent As Intent";
_sendintent = new anywheresoftware.b4a.objects.IntentWrapper();
 //BA.debugLineNum = 361;BA.debugLine="SendIntent.Initialize(SendIntent.ACTION_VIEW, \"sm";
_sendintent.Initialize(_sendintent.ACTION_VIEW,"sms:"+_numer);
 //BA.debugLineNum = 362;BA.debugLine="SendIntent.PutExtra(\"sms_body\", tresc)";
_sendintent.PutExtra("sms_body",(Object)(_tresc));
 //BA.debugLineNum = 363;BA.debugLine="StartActivity(SendIntent)";
anywheresoftware.b4a.keywords.Common.StartActivity(processBA,(Object)(_sendintent.getObject()));
 //BA.debugLineNum = 364;BA.debugLine="Log(\"WyslijKomende: Intent SMS zainicjowany i uru";
anywheresoftware.b4a.keywords.Common.LogImpl("61703947","WyslijKomende: Intent SMS zainicjowany i uruchomiony.",0);
 //BA.debugLineNum = 365;BA.debugLine="End Sub";
return "";
}
public static String  _za_krotko() throws Exception{
 //BA.debugLineNum = 397;BA.debugLine="Sub za_krotko";
 //BA.debugLineNum = 398;BA.debugLine="Log(\"za_krotko: Wykryto krótkie kliknięcie. Wyświ";
anywheresoftware.b4a.keywords.Common.LogImpl("61835009","za_krotko: Wykryto krótkie kliknięcie. Wyświetlam komunikat.",0);
 //BA.debugLineNum = 399;BA.debugLine="ToastMessageShow ( \"Przytrzymaj 3sek.\",True)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Przytrzymaj 3sek."),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 400;BA.debugLine="Timer3.Enabled=False";
_timer3.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 401;BA.debugLine="Timer3.Initialize(\"Timer\",1500)";
_timer3.Initialize(processBA,"Timer",(long) (1500));
 //BA.debugLineNum = 402;BA.debugLine="Timer3.Enabled =True";
_timer3.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 403;BA.debugLine="End Sub";
return "";
}
public static String  _za_krotko_2() throws Exception{
 //BA.debugLineNum = 407;BA.debugLine="Sub za_krotko_2";
 //BA.debugLineNum = 408;BA.debugLine="Log(\"za_krotko_2: Rozpoczęto zapisywanie. Wyświet";
anywheresoftware.b4a.keywords.Common.LogImpl("61900545","za_krotko_2: Rozpoczęto zapisywanie. Wyświetlam komunikat.",0);
 //BA.debugLineNum = 409;BA.debugLine="MsgboxAsync(\"zapisuje\", \"⏱ Informacja \")";
anywheresoftware.b4a.keywords.Common.MsgboxAsync(BA.ObjectToCharSequence("zapisuje"),BA.ObjectToCharSequence("⏱ Informacja "),processBA);
 //BA.debugLineNum = 410;BA.debugLine="ToastMessageShow ( \"Zapisuje\",True)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("Zapisuje"),anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 411;BA.debugLine="Timer3.Enabled=False";
_timer3.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 412;BA.debugLine="Timer3.Initialize(\"Timer\",2500)";
_timer3.Initialize(processBA,"Timer",(long) (2500));
 //BA.debugLineNum = 413;BA.debugLine="Timer3.Enabled =True";
_timer3.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 414;BA.debugLine="Log(\"za_krotko_2: Zakończono wyświetlanie komunik";
anywheresoftware.b4a.keywords.Common.LogImpl("61900551","za_krotko_2: Zakończono wyświetlanie komunikatu zapisu.",0);
 //BA.debugLineNum = 416;BA.debugLine="End Sub";
return "";
}
public static String  _zamknij_click() throws Exception{
 //BA.debugLineNum = 236;BA.debugLine="Private Sub Zamknij_Click";
 //BA.debugLineNum = 237;BA.debugLine="Log(\"Zamknij_Click: Krótkie kliknięcie 'Zamknij'.";
anywheresoftware.b4a.keywords.Common.LogImpl("61048577","Zamknij_Click: Krótkie kliknięcie 'Zamknij'.",0);
 //BA.debugLineNum = 238;BA.debugLine="CallSub (\"Main\", za_krotko)";
anywheresoftware.b4a.keywords.Common.CallSubNew(processBA,(Object)("Main"),_za_krotko());
 //BA.debugLineNum = 239;BA.debugLine="End Sub";
return "";
}
public static String  _zmianakodu_click() throws Exception{
 //BA.debugLineNum = 463;BA.debugLine="Private Sub zmianakodu_Click";
 //BA.debugLineNum = 464;BA.debugLine="Log(\"zmianakodu_Click: Krótkie kliknięcie 'Zmiana";
anywheresoftware.b4a.keywords.Common.LogImpl("62490369","zmianakodu_Click: Krótkie kliknięcie 'Zmiana kodu'.",0);
 //BA.debugLineNum = 465;BA.debugLine="CallSub (\"Main\", za_krotko)";
anywheresoftware.b4a.keywords.Common.CallSubNew(processBA,(Object)("Main"),_za_krotko());
 //BA.debugLineNum = 466;BA.debugLine="End Sub";
return "";
}
}
