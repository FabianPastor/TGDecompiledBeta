package net.hockeyapp.android.metrics;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Point;
import android.os.Build;
import android.os.Build.VERSION;
import android.telephony.TelephonyManager;
import android.view.Display;
import android.view.WindowManager;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import net.hockeyapp.android.Constants;
import net.hockeyapp.android.metrics.model.Application;
import net.hockeyapp.android.metrics.model.Device;
import net.hockeyapp.android.metrics.model.Internal;
import net.hockeyapp.android.metrics.model.Session;
import net.hockeyapp.android.metrics.model.User;
import net.hockeyapp.android.utils.HockeyLog;
import net.hockeyapp.android.utils.Util;

class TelemetryContext
{
  private static final String SESSION_IS_FIRST_KEY = "SESSION_IS_FIRST";
  private static final String SHARED_PREFERENCES_KEY = "HOCKEY_APP_TELEMETRY_CONTEXT";
  private static final String TAG = "HockeyApp-Metrics";
  private final Object IKEY_LOCK = new Object();
  protected final Application mApplication = new Application();
  protected Context mContext;
  protected final Device mDevice = new Device();
  private String mInstrumentationKey;
  protected final Internal mInternal = new Internal();
  private String mPackageName;
  protected final Session mSession = new Session();
  private SharedPreferences mSettings;
  protected final User mUser = new User();
  
  private TelemetryContext() {}
  
  protected TelemetryContext(Context paramContext, String paramString)
  {
    this();
    this.mSettings = paramContext.getSharedPreferences("HOCKEY_APP_TELEMETRY_CONTEXT", 0);
    this.mContext = paramContext;
    this.mInstrumentationKey = Util.convertAppIdentifierToGuid(paramString);
    configDeviceContext();
    configUserId();
    configInternalContext();
    configApplicationContext();
  }
  
  protected void configApplicationContext()
  {
    HockeyLog.debug("HockeyApp-Metrics", "Configuring application context");
    this.mPackageName = "";
    try
    {
      Object localObject1 = this.mContext.getPackageManager().getPackageInfo(this.mContext.getPackageName(), 0);
      if (((PackageInfo)localObject1).packageName != null) {
        this.mPackageName = ((PackageInfo)localObject1).packageName;
      }
      String str = Integer.toString(((PackageInfo)localObject1).versionCode);
      localObject1 = String.format("%s (%S)", new Object[] { ((PackageInfo)localObject1).versionName, str });
      setAppVersion((String)localObject1);
    }
    catch (PackageManager.NameNotFoundException localNameNotFoundException)
    {
      for (;;)
      {
        HockeyLog.debug("HockeyApp-Metrics", "Could not get application context");
        setAppVersion("unknown");
      }
    }
    finally
    {
      setAppVersion("unknown");
    }
    setSdkVersion("android:" + "4.0.1");
  }
  
  protected void configDeviceContext()
  {
    HockeyLog.debug("HockeyApp-Metrics", "Configuring device context");
    setOsVersion(Build.VERSION.RELEASE);
    setOsName("Android");
    setDeviceModel(Build.MODEL);
    setDeviceOemName(Build.MANUFACTURER);
    setOsLocale(Locale.getDefault().toString());
    setOsLanguage(Locale.getDefault().getLanguage());
    updateScreenResolution();
    setDeviceId(Constants.DEVICE_IDENTIFIER);
    if (((TelephonyManager)this.mContext.getSystemService("phone")).getPhoneType() != 0) {
      setDeviceType("Phone");
    }
    for (;;)
    {
      if (Util.isEmulator()) {
        setDeviceModel("[Emulator]" + this.mDevice.getModel());
      }
      return;
      setDeviceType("Tablet");
    }
  }
  
  protected void configInternalContext()
  {
    setSdkVersion("android:" + "4.0.1");
  }
  
  protected void configSessionContext(String paramString)
  {
    HockeyLog.debug("HockeyApp-Metrics", "Configuring session context");
    setSessionId(paramString);
    HockeyLog.debug("HockeyApp-Metrics", "Setting the isNew-flag to true, as we only count new sessions");
    setIsNewSession("true");
    paramString = this.mSettings.edit();
    if (!this.mSettings.getBoolean("SESSION_IS_FIRST", false))
    {
      paramString.putBoolean("SESSION_IS_FIRST", true);
      paramString.apply();
      setIsFirstSession("true");
      HockeyLog.debug("HockeyApp-Metrics", "It's our first session, writing true to SharedPreferences.");
      return;
    }
    setIsFirstSession("false");
    HockeyLog.debug("HockeyApp-Metrics", "It's not their first session, writing false to SharedPreferences.");
  }
  
  protected void configUserId()
  {
    HockeyLog.debug("HockeyApp-Metrics", "Configuring user context");
    HockeyLog.debug("Using pre-supplied anonymous device identifier.");
    setAnonymousUserId(Constants.CRASH_IDENTIFIER);
  }
  
  public String getAnonymousUserId()
  {
    synchronized (this.mUser)
    {
      String str = this.mUser.getId();
      return str;
    }
  }
  
  public String getAppVersion()
  {
    synchronized (this.mApplication)
    {
      String str = this.mApplication.getVer();
      return str;
    }
  }
  
  protected Map<String, String> getContextTags()
  {
    LinkedHashMap localLinkedHashMap = new LinkedHashMap();
    synchronized (this.mApplication)
    {
      this.mApplication.addToHashMap(localLinkedHashMap);
      synchronized (this.mDevice)
      {
        this.mDevice.addToHashMap(localLinkedHashMap);
        synchronized (this.mSession)
        {
          this.mSession.addToHashMap(localLinkedHashMap);
          synchronized (this.mUser)
          {
            this.mUser.addToHashMap(localLinkedHashMap);
          }
        }
      }
    }
    synchronized (this.mInternal)
    {
      this.mInternal.addToHashMap(localLinkedHashMap);
      return localLinkedHashMap;
      localObject1 = finally;
      throw ((Throwable)localObject1);
      localObject2 = finally;
      throw ((Throwable)localObject2);
      localObject3 = finally;
      throw ((Throwable)localObject3);
      localObject4 = finally;
      throw ((Throwable)localObject4);
    }
  }
  
  public String getDeviceId()
  {
    return this.mDevice.getId();
  }
  
  public String getDeviceModel()
  {
    synchronized (this.mDevice)
    {
      String str = this.mDevice.getModel();
      return str;
    }
  }
  
  public String getDeviceOemName()
  {
    synchronized (this.mDevice)
    {
      String str = this.mDevice.getOemName();
      return str;
    }
  }
  
  public String getDeviceType()
  {
    return this.mDevice.getType();
  }
  
  public String getInstrumentationKey()
  {
    synchronized (this.IKEY_LOCK)
    {
      String str = this.mInstrumentationKey;
      return str;
    }
  }
  
  public String getIsFirstSession()
  {
    synchronized (this.mSession)
    {
      String str = this.mSession.getIsFirst();
      return str;
    }
  }
  
  public String getIsNewSession()
  {
    synchronized (this.mSession)
    {
      String str = this.mSession.getIsNew();
      return str;
    }
  }
  
  public String getOSLanguage()
  {
    synchronized (this.mDevice)
    {
      String str = this.mDevice.getLanguage();
      return str;
    }
  }
  
  public String getOsLocale()
  {
    synchronized (this.mDevice)
    {
      String str = this.mDevice.getLocale();
      return str;
    }
  }
  
  public String getOsName()
  {
    synchronized (this.mDevice)
    {
      String str = this.mDevice.getOs();
      return str;
    }
  }
  
  public String getOsVersion()
  {
    synchronized (this.mDevice)
    {
      String str = this.mDevice.getOsVersion();
      return str;
    }
  }
  
  protected String getPackageName()
  {
    return this.mPackageName;
  }
  
  public String getScreenResolution()
  {
    synchronized (this.mDevice)
    {
      String str = this.mDevice.getScreenResolution();
      return str;
    }
  }
  
  public String getSdkVersion()
  {
    synchronized (this.mInternal)
    {
      String str = this.mInternal.getSdkVersion();
      return str;
    }
  }
  
  public String getSessionId()
  {
    synchronized (this.mSession)
    {
      String str = this.mSession.getId();
      return str;
    }
  }
  
  protected void renewSessionContext(String paramString)
  {
    configSessionContext(paramString);
  }
  
  public void setAnonymousUserId(String paramString)
  {
    synchronized (this.mUser)
    {
      this.mUser.setId(paramString);
      return;
    }
  }
  
  public void setAppVersion(String paramString)
  {
    synchronized (this.mApplication)
    {
      this.mApplication.setVer(paramString);
      return;
    }
  }
  
  public void setDeviceId(String paramString)
  {
    synchronized (this.mDevice)
    {
      this.mDevice.setId(paramString);
      return;
    }
  }
  
  public void setDeviceModel(String paramString)
  {
    synchronized (this.mDevice)
    {
      this.mDevice.setModel(paramString);
      return;
    }
  }
  
  public void setDeviceOemName(String paramString)
  {
    synchronized (this.mDevice)
    {
      this.mDevice.setOemName(paramString);
      return;
    }
  }
  
  public void setDeviceType(String paramString)
  {
    synchronized (this.mDevice)
    {
      this.mDevice.setType(paramString);
      return;
    }
  }
  
  /* Error */
  public void setInstrumentationKey(String paramString)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 38	net/hockeyapp/android/metrics/TelemetryContext:IKEY_LOCK	Ljava/lang/Object;
    //   6: astore_2
    //   7: aload_2
    //   8: monitorenter
    //   9: aload_0
    //   10: aload_1
    //   11: putfield 84	net/hockeyapp/android/metrics/TelemetryContext:mInstrumentationKey	Ljava/lang/String;
    //   14: aload_2
    //   15: monitorexit
    //   16: aload_0
    //   17: monitorexit
    //   18: return
    //   19: astore_1
    //   20: aload_2
    //   21: monitorexit
    //   22: aload_1
    //   23: athrow
    //   24: astore_1
    //   25: aload_0
    //   26: monitorexit
    //   27: aload_1
    //   28: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	29	0	this	TelemetryContext
    //   0	29	1	paramString	String
    // Exception table:
    //   from	to	target	type
    //   9	16	19	finally
    //   20	22	19	finally
    //   2	9	24	finally
    //   22	24	24	finally
  }
  
  public void setIsFirstSession(String paramString)
  {
    synchronized (this.mSession)
    {
      this.mSession.setIsFirst(paramString);
      return;
    }
  }
  
  public void setIsNewSession(String paramString)
  {
    synchronized (this.mSession)
    {
      this.mSession.setIsNew(paramString);
      return;
    }
  }
  
  public void setOsLanguage(String paramString)
  {
    synchronized (this.mDevice)
    {
      this.mDevice.setLanguage(paramString);
      return;
    }
  }
  
  public void setOsLocale(String paramString)
  {
    synchronized (this.mDevice)
    {
      this.mDevice.setLocale(paramString);
      return;
    }
  }
  
  public void setOsName(String paramString)
  {
    synchronized (this.mDevice)
    {
      this.mDevice.setOs(paramString);
      return;
    }
  }
  
  public void setOsVersion(String paramString)
  {
    synchronized (this.mDevice)
    {
      this.mDevice.setOsVersion(paramString);
      return;
    }
  }
  
  public void setScreenResolution(String paramString)
  {
    synchronized (this.mDevice)
    {
      this.mDevice.setScreenResolution(paramString);
      return;
    }
  }
  
  public void setSdkVersion(String paramString)
  {
    synchronized (this.mInternal)
    {
      this.mInternal.setSdkVersion(paramString);
      return;
    }
  }
  
  public void setSessionId(String paramString)
  {
    synchronized (this.mSession)
    {
      this.mSession.setId(paramString);
      return;
    }
  }
  
  @SuppressLint({"NewApi", "Deprecation"})
  protected void updateScreenResolution()
  {
    Object localObject1;
    Object localObject2;
    int j;
    int i;
    if (this.mContext != null)
    {
      localObject1 = (WindowManager)this.mContext.getSystemService("window");
      if (Build.VERSION.SDK_INT < 17) {
        break label96;
      }
      localObject2 = new Point();
      ((WindowManager)localObject1).getDefaultDisplay().getRealSize((Point)localObject2);
      j = ((Point)localObject2).x;
      i = ((Point)localObject2).y;
    }
    for (;;)
    {
      setScreenResolution(String.valueOf(i) + "x" + String.valueOf(j));
      return;
      label96:
      if (Build.VERSION.SDK_INT >= 13)
      {
        try
        {
          localObject2 = Display.class.getMethod("getRawWidth", new Class[0]);
          localObject3 = Display.class.getMethod("getRawHeight", new Class[0]);
          Display localDisplay = ((WindowManager)localObject1).getDefaultDisplay();
          j = ((Integer)((Method)localObject2).invoke(localDisplay, new Object[0])).intValue();
          i = ((Integer)((Method)localObject3).invoke(localDisplay, new Object[0])).intValue();
        }
        catch (Exception localException)
        {
          Object localObject3 = new Point();
          ((WindowManager)localObject1).getDefaultDisplay().getSize((Point)localObject3);
          j = ((Point)localObject3).x;
          i = ((Point)localObject3).y;
          HockeyLog.debug("HockeyApp-Metrics", "Couldn't determine screen resolution: " + localException.toString());
        }
      }
      else
      {
        localObject1 = ((WindowManager)localObject1).getDefaultDisplay();
        j = ((Display)localObject1).getWidth();
        i = ((Display)localObject1).getHeight();
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/net/hockeyapp/android/metrics/TelemetryContext.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */