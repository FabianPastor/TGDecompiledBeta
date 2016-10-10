package net.hockeyapp.android.metrics;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.app.Application.ActivityLifecycleCallbacks;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import java.lang.ref.WeakReference;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import net.hockeyapp.android.Constants;
import net.hockeyapp.android.metrics.model.Data;
import net.hockeyapp.android.metrics.model.Domain;
import net.hockeyapp.android.metrics.model.SessionState;
import net.hockeyapp.android.metrics.model.SessionStateData;
import net.hockeyapp.android.metrics.model.TelemetryData;
import net.hockeyapp.android.utils.AsyncTaskUtils;
import net.hockeyapp.android.utils.HockeyLog;
import net.hockeyapp.android.utils.Util;

public class MetricsManager
{
  protected static final AtomicInteger ACTIVITY_COUNT = new AtomicInteger(0);
  protected static final AtomicLong LAST_BACKGROUND = new AtomicLong(getTime());
  private static final Object LOCK = new Object();
  private static final Integer SESSION_RENEWAL_INTERVAL = Integer.valueOf(20000);
  private static final String TAG = "HA-MetricsManager";
  private static volatile MetricsManager instance;
  private static Channel sChannel;
  private static Sender sSender;
  private static TelemetryContext sTelemetryContext;
  private static WeakReference<Application> sWeakApplication;
  private volatile boolean mSessionTrackingDisabled;
  private TelemetryLifecycleCallbacks mTelemetryLifecycleCallbacks;
  
  protected MetricsManager(Context paramContext, TelemetryContext paramTelemetryContext, Sender paramSender, Persistence paramPersistence, Channel paramChannel)
  {
    sTelemetryContext = paramTelemetryContext;
    paramTelemetryContext = paramSender;
    if (paramSender == null) {
      paramTelemetryContext = new Sender();
    }
    sSender = paramTelemetryContext;
    if (paramPersistence == null) {
      paramPersistence = new Persistence(paramContext, paramTelemetryContext);
    }
    for (;;)
    {
      sSender.setPersistence(paramPersistence);
      if (paramChannel != null) {
        break;
      }
      sChannel = new Channel(sTelemetryContext, paramPersistence);
      return;
      paramPersistence.setSender(paramTelemetryContext);
    }
    sChannel = paramChannel;
  }
  
  private static Application getApplication()
  {
    Application localApplication = null;
    if (sWeakApplication != null) {
      localApplication = (Application)sWeakApplication.get();
    }
    return localApplication;
  }
  
  protected static Channel getChannel()
  {
    return sChannel;
  }
  
  protected static MetricsManager getInstance()
  {
    return instance;
  }
  
  protected static Sender getSender()
  {
    return sSender;
  }
  
  private static long getTime()
  {
    return new Date().getTime();
  }
  
  public static void register(Context paramContext, Application paramApplication)
  {
    String str = Util.getAppIdentifier(paramContext);
    if ((str == null) || (str.length() == 0)) {
      throw new IllegalArgumentException("HockeyApp app identifier was not configured correctly in manifest or build configuration.");
    }
    register(paramContext, paramApplication, str);
  }
  
  public static void register(Context paramContext, Application paramApplication, String paramString)
  {
    register(paramContext, paramApplication, paramString, null, null, null);
  }
  
  protected static void register(Context paramContext, Application paramApplication, String paramString, Sender paramSender, Persistence paramPersistence, Channel paramChannel)
  {
    if (instance == null) {}
    synchronized (LOCK)
    {
      MetricsManager localMetricsManager = instance;
      if (localMetricsManager == null) {}
      for (;;)
      {
        try
        {
          Constants.loadFromContext(paramContext);
          paramContext = new MetricsManager(paramContext, new TelemetryContext(paramContext, paramString), paramSender, paramPersistence, paramChannel);
          sWeakApplication = new WeakReference(paramApplication);
          if (Util.sessionTrackingSupported()) {
            break;
          }
          bool = true;
          paramContext.mSessionTrackingDisabled = bool;
          instance = paramContext;
          if (!paramContext.mSessionTrackingDisabled) {
            setSessionTrackingDisabled(Boolean.valueOf(false));
          }
          return;
        }
        finally {}
        throw paramContext;
        paramContext = localMetricsManager;
      }
      return;
      boolean bool = false;
    }
  }
  
  @TargetApi(14)
  private void registerTelemetryLifecycleCallbacks()
  {
    if (this.mTelemetryLifecycleCallbacks == null) {
      this.mTelemetryLifecycleCallbacks = new TelemetryLifecycleCallbacks(null);
    }
    getApplication().registerActivityLifecycleCallbacks(this.mTelemetryLifecycleCallbacks);
  }
  
  public static boolean sessionTrackingEnabled()
  {
    return !instance.mSessionTrackingDisabled;
  }
  
  public static void setCustomServerURL(String paramString)
  {
    if (sSender != null)
    {
      sSender.setCustomServerURL(paramString);
      return;
    }
    HockeyLog.warn("HA-MetricsManager", "HockeyApp couldn't set the custom server url. Please register(...) the MetricsManager before setting the server URL.");
  }
  
  protected static void setSender(Sender paramSender)
  {
    sSender = paramSender;
  }
  
  public static void setSessionTrackingDisabled(Boolean paramBoolean)
  {
    if (instance == null)
    {
      HockeyLog.warn("HA-MetricsManager", "MetricsManager hasn't been registered. No Metrics will be collected!");
      return;
    }
    for (;;)
    {
      synchronized (LOCK)
      {
        if (Util.sessionTrackingSupported())
        {
          instance.mSessionTrackingDisabled = paramBoolean.booleanValue();
          if (!paramBoolean.booleanValue()) {
            instance.registerTelemetryLifecycleCallbacks();
          }
          return;
        }
      }
      instance.mSessionTrackingDisabled = true;
      instance.unregisterTelemetryLifecycleCallbacks();
    }
  }
  
  private void trackSessionState(final SessionState paramSessionState)
  {
    AsyncTaskUtils.execute(new AsyncTask()
    {
      protected Void doInBackground(Void... paramAnonymousVarArgs)
      {
        paramAnonymousVarArgs = new SessionStateData();
        paramAnonymousVarArgs.setState(paramSessionState);
        paramAnonymousVarArgs = MetricsManager.this.createData(paramAnonymousVarArgs);
        MetricsManager.sChannel.enqueueData(paramAnonymousVarArgs);
        return null;
      }
    });
  }
  
  @TargetApi(14)
  private void unregisterTelemetryLifecycleCallbacks()
  {
    getApplication().unregisterActivityLifecycleCallbacks(this.mTelemetryLifecycleCallbacks);
    this.mTelemetryLifecycleCallbacks = null;
  }
  
  private void updateSession()
  {
    if (ACTIVITY_COUNT.getAndIncrement() == 0)
    {
      if (sessionTrackingEnabled())
      {
        HockeyLog.debug("HA-MetricsManager", "Starting & tracking session");
        renewSession();
        return;
      }
      HockeyLog.debug("HA-MetricsManager", "Session management disabled by the developer");
      return;
    }
    long l1 = getTime();
    long l2 = LAST_BACKGROUND.getAndSet(getTime());
    if (l1 - l2 >= SESSION_RENEWAL_INTERVAL.intValue()) {}
    for (int i = 1;; i = 0)
    {
      HockeyLog.debug("HA-MetricsManager", "Checking if we have to renew a session, time difference is: " + (l1 - l2));
      if ((i == 0) || (!sessionTrackingEnabled())) {
        break;
      }
      HockeyLog.debug("HA-MetricsManager", "Renewing session");
      renewSession();
      return;
    }
  }
  
  protected Data<Domain> createData(TelemetryData paramTelemetryData)
  {
    Data localData = new Data();
    localData.setBaseData(paramTelemetryData);
    localData.setBaseType(paramTelemetryData.getBaseType());
    localData.QualifiedName = paramTelemetryData.getEnvelopeName();
    return localData;
  }
  
  protected void renewSession()
  {
    String str = UUID.randomUUID().toString();
    sTelemetryContext.renewSessionContext(str);
    trackSessionState(SessionState.START);
  }
  
  protected void setChannel(Channel paramChannel)
  {
    sChannel = paramChannel;
  }
  
  @TargetApi(14)
  private class TelemetryLifecycleCallbacks
    implements Application.ActivityLifecycleCallbacks
  {
    private TelemetryLifecycleCallbacks() {}
    
    public void onActivityCreated(Activity paramActivity, Bundle paramBundle) {}
    
    public void onActivityDestroyed(Activity paramActivity) {}
    
    public void onActivityPaused(Activity paramActivity)
    {
      MetricsManager.LAST_BACKGROUND.set(MetricsManager.access$300());
    }
    
    public void onActivityResumed(Activity paramActivity)
    {
      MetricsManager.this.updateSession();
    }
    
    public void onActivitySaveInstanceState(Activity paramActivity, Bundle paramBundle) {}
    
    public void onActivityStarted(Activity paramActivity) {}
    
    public void onActivityStopped(Activity paramActivity) {}
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/net/hockeyapp/android/metrics/MetricsManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */