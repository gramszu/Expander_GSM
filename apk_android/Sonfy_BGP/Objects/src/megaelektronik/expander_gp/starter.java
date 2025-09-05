package megaelektronik.expander_gp;


import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.objects.ServiceHelper;
import anywheresoftware.b4a.debug.*;

public class starter extends android.app.Service{
	public static class starter_BR extends android.content.BroadcastReceiver {

		@Override
		public void onReceive(android.content.Context context, android.content.Intent intent) {
            BA.LogInfo("** Receiver (starter) OnReceive **");
			android.content.Intent in = new android.content.Intent(context, starter.class);
			if (intent != null)
				in.putExtra("b4a_internal_intent", intent);
            ServiceHelper.StarterHelper.startServiceFromReceiver (context, in, true, BA.class);
		}

	}
    static starter mostCurrent;
	public static BA processBA;
    private ServiceHelper _service;
    public static Class<?> getObject() {
		return starter.class;
	}
	@Override
	public void onCreate() {
        super.onCreate();
        mostCurrent = this;
        if (processBA == null) {
		    processBA = new BA(this, null, null, "megaelektronik.expander_gp", "megaelektronik.expander_gp.starter");
            if (BA.isShellModeRuntimeCheck(processBA)) {
                processBA.raiseEvent2(null, true, "SHELL", false);
		    }
            try {
                Class.forName(BA.applicationContext.getPackageName() + ".main").getMethod("initializeProcessGlobals").invoke(null, null);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            processBA.loadHtSubs(this.getClass());
            ServiceHelper.init();
        }
        _service = new ServiceHelper(this);
        processBA.service = this;
        
        if (BA.isShellModeRuntimeCheck(processBA)) {
			processBA.raiseEvent2(null, true, "CREATE", true, "megaelektronik.expander_gp.starter", processBA, _service, anywheresoftware.b4a.keywords.Common.Density);
		}
        if (!true && ServiceHelper.StarterHelper.startFromServiceCreate(processBA, false) == false) {
				
		}
		else {
            processBA.setActivityPaused(false);
            BA.LogInfo("*** Service (starter) Create ***");
            processBA.raiseEvent(null, "service_create");
        }
        processBA.runHook("oncreate", this, null);
        if (true) {
			if (ServiceHelper.StarterHelper.runWaitForLayouts() == false) {
                BA.LogInfo("stopping spontaneous created service");
                stopSelf();
            }
		}
    }
		@Override
	public void onStart(android.content.Intent intent, int startId) {
		onStartCommand(intent, 0, 0);
    }
    @Override
    public int onStartCommand(final android.content.Intent intent, int flags, int startId) {
    	if (ServiceHelper.StarterHelper.onStartCommand(processBA, new Runnable() {
            public void run() {
                handleStart(intent);
            }}))
			;
		else {
			ServiceHelper.StarterHelper.addWaitForLayout (new Runnable() {
				public void run() {
                    processBA.setActivityPaused(false);
                    BA.LogInfo("** Service (starter) Create **");
                    processBA.raiseEvent(null, "service_create");
					handleStart(intent);
                    ServiceHelper.StarterHelper.removeWaitForLayout();
				}
			});
		}
        processBA.runHook("onstartcommand", this, new Object[] {intent, flags, startId});
		return android.app.Service.START_NOT_STICKY;
    }
    public void onTaskRemoved(android.content.Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        if (true)
            processBA.raiseEvent(null, "service_taskremoved");
            
    }
    private void handleStart(android.content.Intent intent) {
    	BA.LogInfo("** Service (starter) Start **");
    	java.lang.reflect.Method startEvent = processBA.htSubs.get("service_start");
    	if (startEvent != null) {
    		if (startEvent.getParameterTypes().length > 0) {
    			anywheresoftware.b4a.objects.IntentWrapper iw = ServiceHelper.StarterHelper.handleStartIntent(intent, _service, processBA);
    			processBA.raiseEvent(null, "service_start", iw);
    		}
    		else {
    			processBA.raiseEvent(null, "service_start");
    		}
    	}
    }

	public void onTimeout(int startId) {
        BA.LogInfo("** Service (starter) Timeout **");
        anywheresoftware.b4a.objects.collections.Map params = new anywheresoftware.b4a.objects.collections.Map();
        params.Initialize();
        params.Put("StartId", startId);
        processBA.raiseEvent(null, "service_timeout", params);
            
    }
	@Override
	public void onDestroy() {
        super.onDestroy();
        if (true) {
            BA.LogInfo("** Service (starter) Destroy (ignored)**");
        }
        else {
            BA.LogInfo("** Service (starter) Destroy **");
		    processBA.raiseEvent(null, "service_destroy");
            processBA.service = null;
		    mostCurrent = null;
		    processBA.setActivityPaused(true);
            processBA.runHook("ondestroy", this, null);
        }
	}

@Override
	public android.os.IBinder onBind(android.content.Intent intent) {
		return null;
	}public anywheresoftware.b4a.keywords.Common __c = null;
public static anywheresoftware.b4a.objects.RuntimePermissions _rp = null;
public static anywheresoftware.b4a.gps.GPS _gps1 = null;
public static boolean _gpsstarted = false;
public megaelektronik.expander_gp.main _main = null;
public static boolean  _application_error(anywheresoftware.b4a.objects.B4AException _error,String _stacktrace) throws Exception{
 //BA.debugLineNum = 43;BA.debugLine="Sub Application_Error (Error As Exception, StackTr";
 //BA.debugLineNum = 44;BA.debugLine="Return True";
if (true) return anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 45;BA.debugLine="End Sub";
return false;
}
public static String  _gps_gpsstatus(anywheresoftware.b4a.objects.collections.List _satellites) throws Exception{
 //BA.debugLineNum = 39;BA.debugLine="Sub GPS_GpsStatus (Satellites As List)";
 //BA.debugLineNum = 41;BA.debugLine="End Sub";
return "";
}
public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 6;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 7;BA.debugLine="Public rp As RuntimePermissions";
_rp = new anywheresoftware.b4a.objects.RuntimePermissions();
 //BA.debugLineNum = 8;BA.debugLine="Public GPS1 As GPS";
_gps1 = new anywheresoftware.b4a.gps.GPS();
 //BA.debugLineNum = 9;BA.debugLine="Private gpsStarted As Boolean";
_gpsstarted = false;
 //BA.debugLineNum = 10;BA.debugLine="End Sub";
return "";
}
public static String  _service_create() throws Exception{
 //BA.debugLineNum = 12;BA.debugLine="Sub Service_Create";
 //BA.debugLineNum = 13;BA.debugLine="GPS1.Initialize(\"GPS\")";
_gps1.Initialize("GPS");
 //BA.debugLineNum = 14;BA.debugLine="End Sub";
return "";
}
public static String  _service_destroy() throws Exception{
 //BA.debugLineNum = 47;BA.debugLine="Sub Service_Destroy";
 //BA.debugLineNum = 48;BA.debugLine="StopGps";
_stopgps();
 //BA.debugLineNum = 49;BA.debugLine="End Sub";
return "";
}
public static String  _service_start(anywheresoftware.b4a.objects.IntentWrapper _startingintent) throws Exception{
 //BA.debugLineNum = 16;BA.debugLine="Sub Service_Start (StartingIntent As Intent)";
 //BA.debugLineNum = 18;BA.debugLine="End Sub";
return "";
}
public static String  _startgps() throws Exception{
 //BA.debugLineNum = 20;BA.debugLine="Public Sub StartGps";
 //BA.debugLineNum = 21;BA.debugLine="If gpsStarted = False Then";
if (_gpsstarted==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 22;BA.debugLine="GPS1.Start(0, 0)";
_gps1.Start(processBA,(long) (0),(float) (0));
 //BA.debugLineNum = 23;BA.debugLine="gpsStarted = True";
_gpsstarted = anywheresoftware.b4a.keywords.Common.True;
 };
 //BA.debugLineNum = 25;BA.debugLine="End Sub";
return "";
}
public static String  _stopgps() throws Exception{
 //BA.debugLineNum = 27;BA.debugLine="Public Sub StopGps";
 //BA.debugLineNum = 28;BA.debugLine="If gpsStarted Then";
if (_gpsstarted) { 
 //BA.debugLineNum = 29;BA.debugLine="GPS1.Stop";
_gps1.Stop();
 //BA.debugLineNum = 30;BA.debugLine="gpsStarted = False";
_gpsstarted = anywheresoftware.b4a.keywords.Common.False;
 };
 //BA.debugLineNum = 32;BA.debugLine="End Sub";
return "";
}
}
