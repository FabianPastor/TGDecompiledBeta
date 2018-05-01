package android.support.v4.app;

import android.app.AppOpsManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.DeadObjectException;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.provider.Settings.Secure;
import android.util.Log;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public final class NotificationManagerCompat
{
  private static Set<String> sEnabledNotificationListenerPackages = new HashSet();
  private static String sEnabledNotificationListeners;
  private static final Object sEnabledNotificationListenersLock = new Object();
  private static final Object sLock = new Object();
  private static SideChannelManager sSideChannelManager;
  private final Context mContext;
  private final NotificationManager mNotificationManager;
  
  private NotificationManagerCompat(Context paramContext)
  {
    this.mContext = paramContext;
    this.mNotificationManager = ((NotificationManager)this.mContext.getSystemService("notification"));
  }
  
  public static NotificationManagerCompat from(Context paramContext)
  {
    return new NotificationManagerCompat(paramContext);
  }
  
  public static Set<String> getEnabledListenerPackages(Context paramContext)
  {
    String str = Settings.Secure.getString(paramContext.getContentResolver(), "enabled_notification_listeners");
    paramContext = sEnabledNotificationListenersLock;
    if (str != null) {}
    try
    {
      if (!str.equals(sEnabledNotificationListeners))
      {
        String[] arrayOfString = str.split(":");
        localObject1 = new java/util/HashSet;
        ((HashSet)localObject1).<init>(arrayOfString.length);
        int i = arrayOfString.length;
        for (int j = 0; j < i; j++)
        {
          ComponentName localComponentName = ComponentName.unflattenFromString(arrayOfString[j]);
          if (localComponentName != null) {
            ((Set)localObject1).add(localComponentName.getPackageName());
          }
        }
        sEnabledNotificationListenerPackages = (Set)localObject1;
        sEnabledNotificationListeners = str;
      }
      Object localObject1 = sEnabledNotificationListenerPackages;
      return (Set<String>)localObject1;
    }
    finally {}
  }
  
  private void pushSideChannelQueue(Task paramTask)
  {
    synchronized (sLock)
    {
      if (sSideChannelManager == null)
      {
        SideChannelManager localSideChannelManager = new android/support/v4/app/NotificationManagerCompat$SideChannelManager;
        localSideChannelManager.<init>(this.mContext.getApplicationContext());
        sSideChannelManager = localSideChannelManager;
      }
      sSideChannelManager.queueTask(paramTask);
      return;
    }
  }
  
  private static boolean useSideChannelForNotification(Notification paramNotification)
  {
    paramNotification = NotificationCompat.getExtras(paramNotification);
    if ((paramNotification != null) && (paramNotification.getBoolean("android.support.useSideChannel"))) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public boolean areNotificationsEnabled()
  {
    boolean bool1 = true;
    boolean bool2;
    if (Build.VERSION.SDK_INT >= 24) {
      bool2 = this.mNotificationManager.areNotificationsEnabled();
    }
    for (;;)
    {
      return bool2;
      bool2 = bool1;
      if (Build.VERSION.SDK_INT < 19) {
        continue;
      }
      AppOpsManager localAppOpsManager = (AppOpsManager)this.mContext.getSystemService("appops");
      Object localObject = this.mContext.getApplicationInfo();
      String str = this.mContext.getApplicationContext().getPackageName();
      int i = ((ApplicationInfo)localObject).uid;
      try
      {
        localObject = Class.forName(AppOpsManager.class.getName());
        i = ((Integer)((Class)localObject).getMethod("checkOpNoThrow", new Class[] { Integer.TYPE, Integer.TYPE, String.class }).invoke(localAppOpsManager, new Object[] { Integer.valueOf(((Integer)((Class)localObject).getDeclaredField("OP_POST_NOTIFICATION").get(Integer.class)).intValue()), Integer.valueOf(i), str })).intValue();
        if (i == 0) {}
        for (bool2 = true;; bool2 = false) {
          break;
        }
      }
      catch (ClassNotFoundException localClassNotFoundException)
      {
        bool2 = bool1;
      }
      catch (RuntimeException localRuntimeException)
      {
        for (;;) {}
      }
      catch (NoSuchFieldException localNoSuchFieldException)
      {
        for (;;) {}
      }
      catch (IllegalAccessException localIllegalAccessException)
      {
        for (;;) {}
      }
      catch (NoSuchMethodException localNoSuchMethodException)
      {
        for (;;) {}
      }
      catch (InvocationTargetException localInvocationTargetException)
      {
        for (;;) {}
      }
    }
  }
  
  public void cancel(int paramInt)
  {
    cancel(null, paramInt);
  }
  
  public void cancel(String paramString, int paramInt)
  {
    this.mNotificationManager.cancel(paramString, paramInt);
    if (Build.VERSION.SDK_INT <= 19) {
      pushSideChannelQueue(new CancelTask(this.mContext.getPackageName(), paramInt, paramString));
    }
  }
  
  public void notify(int paramInt, Notification paramNotification)
  {
    notify(null, paramInt, paramNotification);
  }
  
  public void notify(String paramString, int paramInt, Notification paramNotification)
  {
    if (useSideChannelForNotification(paramNotification))
    {
      pushSideChannelQueue(new NotifyTask(this.mContext.getPackageName(), paramInt, paramString, paramNotification));
      this.mNotificationManager.cancel(paramString, paramInt);
    }
    for (;;)
    {
      return;
      this.mNotificationManager.notify(paramString, paramInt, paramNotification);
    }
  }
  
  private static class CancelTask
    implements NotificationManagerCompat.Task
  {
    final boolean all;
    final int id;
    final String packageName;
    final String tag;
    
    CancelTask(String paramString1, int paramInt, String paramString2)
    {
      this.packageName = paramString1;
      this.id = paramInt;
      this.tag = paramString2;
      this.all = false;
    }
    
    public void send(INotificationSideChannel paramINotificationSideChannel)
      throws RemoteException
    {
      if (this.all) {
        paramINotificationSideChannel.cancelAll(this.packageName);
      }
      for (;;)
      {
        return;
        paramINotificationSideChannel.cancel(this.packageName, this.id, this.tag);
      }
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder("CancelTask[");
      localStringBuilder.append("packageName:").append(this.packageName);
      localStringBuilder.append(", id:").append(this.id);
      localStringBuilder.append(", tag:").append(this.tag);
      localStringBuilder.append(", all:").append(this.all);
      localStringBuilder.append("]");
      return localStringBuilder.toString();
    }
  }
  
  private static class NotifyTask
    implements NotificationManagerCompat.Task
  {
    final int id;
    final Notification notif;
    final String packageName;
    final String tag;
    
    NotifyTask(String paramString1, int paramInt, String paramString2, Notification paramNotification)
    {
      this.packageName = paramString1;
      this.id = paramInt;
      this.tag = paramString2;
      this.notif = paramNotification;
    }
    
    public void send(INotificationSideChannel paramINotificationSideChannel)
      throws RemoteException
    {
      paramINotificationSideChannel.notify(this.packageName, this.id, this.tag, this.notif);
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder("NotifyTask[");
      localStringBuilder.append("packageName:").append(this.packageName);
      localStringBuilder.append(", id:").append(this.id);
      localStringBuilder.append(", tag:").append(this.tag);
      localStringBuilder.append("]");
      return localStringBuilder.toString();
    }
  }
  
  private static class ServiceConnectedEvent
  {
    final ComponentName componentName;
    final IBinder iBinder;
    
    ServiceConnectedEvent(ComponentName paramComponentName, IBinder paramIBinder)
    {
      this.componentName = paramComponentName;
      this.iBinder = paramIBinder;
    }
  }
  
  private static class SideChannelManager
    implements ServiceConnection, Handler.Callback
  {
    private Set<String> mCachedEnabledPackages = new HashSet();
    private final Context mContext;
    private final Handler mHandler;
    private final HandlerThread mHandlerThread;
    private final Map<ComponentName, ListenerRecord> mRecordMap = new HashMap();
    
    SideChannelManager(Context paramContext)
    {
      this.mContext = paramContext;
      this.mHandlerThread = new HandlerThread("NotificationManagerCompat");
      this.mHandlerThread.start();
      this.mHandler = new Handler(this.mHandlerThread.getLooper(), this);
    }
    
    private boolean ensureServiceBound(ListenerRecord paramListenerRecord)
    {
      boolean bool;
      if (paramListenerRecord.bound)
      {
        bool = true;
        return bool;
      }
      Intent localIntent = new Intent("android.support.BIND_NOTIFICATION_SIDE_CHANNEL").setComponent(paramListenerRecord.componentName);
      paramListenerRecord.bound = this.mContext.bindService(localIntent, this, 33);
      if (paramListenerRecord.bound) {
        paramListenerRecord.retryCount = 0;
      }
      for (;;)
      {
        bool = paramListenerRecord.bound;
        break;
        Log.w("NotifManCompat", "Unable to bind to listener " + paramListenerRecord.componentName);
        this.mContext.unbindService(this);
      }
    }
    
    private void ensureServiceUnbound(ListenerRecord paramListenerRecord)
    {
      if (paramListenerRecord.bound)
      {
        this.mContext.unbindService(this);
        paramListenerRecord.bound = false;
      }
      paramListenerRecord.service = null;
    }
    
    private void handleQueueTask(NotificationManagerCompat.Task paramTask)
    {
      updateListenerMap();
      Iterator localIterator = this.mRecordMap.values().iterator();
      while (localIterator.hasNext())
      {
        ListenerRecord localListenerRecord = (ListenerRecord)localIterator.next();
        localListenerRecord.taskQueue.add(paramTask);
        processListenerQueue(localListenerRecord);
      }
    }
    
    private void handleRetryListenerQueue(ComponentName paramComponentName)
    {
      paramComponentName = (ListenerRecord)this.mRecordMap.get(paramComponentName);
      if (paramComponentName != null) {
        processListenerQueue(paramComponentName);
      }
    }
    
    private void handleServiceConnected(ComponentName paramComponentName, IBinder paramIBinder)
    {
      paramComponentName = (ListenerRecord)this.mRecordMap.get(paramComponentName);
      if (paramComponentName != null)
      {
        paramComponentName.service = INotificationSideChannel.Stub.asInterface(paramIBinder);
        paramComponentName.retryCount = 0;
        processListenerQueue(paramComponentName);
      }
    }
    
    private void handleServiceDisconnected(ComponentName paramComponentName)
    {
      paramComponentName = (ListenerRecord)this.mRecordMap.get(paramComponentName);
      if (paramComponentName != null) {
        ensureServiceUnbound(paramComponentName);
      }
    }
    
    private void processListenerQueue(ListenerRecord paramListenerRecord)
    {
      if (Log.isLoggable("NotifManCompat", 3)) {
        Log.d("NotifManCompat", "Processing component " + paramListenerRecord.componentName + ", " + paramListenerRecord.taskQueue.size() + " queued tasks");
      }
      if (paramListenerRecord.taskQueue.isEmpty()) {}
      for (;;)
      {
        return;
        if ((!ensureServiceBound(paramListenerRecord)) || (paramListenerRecord.service == null)) {
          scheduleListenerRetry(paramListenerRecord);
        }
        try
        {
          Object localObject;
          do
          {
            if (Log.isLoggable("NotifManCompat", 3))
            {
              StringBuilder localStringBuilder = new java/lang/StringBuilder;
              localStringBuilder.<init>();
              Log.d("NotifManCompat", "Sending task " + localObject);
            }
            ((NotificationManagerCompat.Task)localObject).send(paramListenerRecord.service);
            paramListenerRecord.taskQueue.remove();
            localObject = (NotificationManagerCompat.Task)paramListenerRecord.taskQueue.peek();
          } while (localObject != null);
        }
        catch (DeadObjectException localDeadObjectException)
        {
          for (;;)
          {
            if (Log.isLoggable("NotifManCompat", 3)) {
              Log.d("NotifManCompat", "Remote service has died: " + paramListenerRecord.componentName);
            }
          }
        }
        catch (RemoteException localRemoteException)
        {
          for (;;)
          {
            Log.w("NotifManCompat", "RemoteException communicating with " + paramListenerRecord.componentName, localRemoteException);
          }
        }
        if (!paramListenerRecord.taskQueue.isEmpty()) {
          scheduleListenerRetry(paramListenerRecord);
        }
      }
    }
    
    private void scheduleListenerRetry(ListenerRecord paramListenerRecord)
    {
      if (this.mHandler.hasMessages(3, paramListenerRecord.componentName)) {}
      for (;;)
      {
        return;
        paramListenerRecord.retryCount += 1;
        if (paramListenerRecord.retryCount > 6)
        {
          Log.w("NotifManCompat", "Giving up on delivering " + paramListenerRecord.taskQueue.size() + " tasks to " + paramListenerRecord.componentName + " after " + paramListenerRecord.retryCount + " retries");
          paramListenerRecord.taskQueue.clear();
        }
        else
        {
          int i = (1 << paramListenerRecord.retryCount - 1) * 1000;
          if (Log.isLoggable("NotifManCompat", 3)) {
            Log.d("NotifManCompat", "Scheduling retry for " + i + " ms");
          }
          paramListenerRecord = this.mHandler.obtainMessage(3, paramListenerRecord.componentName);
          this.mHandler.sendMessageDelayed(paramListenerRecord, i);
        }
      }
    }
    
    private void updateListenerMap()
    {
      Object localObject1 = NotificationManagerCompat.getEnabledListenerPackages(this.mContext);
      if (((Set)localObject1).equals(this.mCachedEnabledPackages)) {}
      for (;;)
      {
        return;
        this.mCachedEnabledPackages = ((Set)localObject1);
        Object localObject2 = this.mContext.getPackageManager().queryIntentServices(new Intent().setAction("android.support.BIND_NOTIFICATION_SIDE_CHANNEL"), 0);
        HashSet localHashSet = new HashSet();
        Iterator localIterator = ((List)localObject2).iterator();
        while (localIterator.hasNext())
        {
          localObject2 = (ResolveInfo)localIterator.next();
          if (((Set)localObject1).contains(((ResolveInfo)localObject2).serviceInfo.packageName))
          {
            ComponentName localComponentName = new ComponentName(((ResolveInfo)localObject2).serviceInfo.packageName, ((ResolveInfo)localObject2).serviceInfo.name);
            if (((ResolveInfo)localObject2).serviceInfo.permission != null) {
              Log.w("NotifManCompat", "Permission present on component " + localComponentName + ", not adding listener record.");
            } else {
              localHashSet.add(localComponentName);
            }
          }
        }
        localObject1 = localHashSet.iterator();
        while (((Iterator)localObject1).hasNext())
        {
          localObject2 = (ComponentName)((Iterator)localObject1).next();
          if (!this.mRecordMap.containsKey(localObject2))
          {
            if (Log.isLoggable("NotifManCompat", 3)) {
              Log.d("NotifManCompat", "Adding listener record for " + localObject2);
            }
            this.mRecordMap.put(localObject2, new ListenerRecord((ComponentName)localObject2));
          }
        }
        localObject2 = this.mRecordMap.entrySet().iterator();
        while (((Iterator)localObject2).hasNext())
        {
          localObject1 = (Map.Entry)((Iterator)localObject2).next();
          if (!localHashSet.contains(((Map.Entry)localObject1).getKey()))
          {
            if (Log.isLoggable("NotifManCompat", 3)) {
              Log.d("NotifManCompat", "Removing listener record for " + ((Map.Entry)localObject1).getKey());
            }
            ensureServiceUnbound((ListenerRecord)((Map.Entry)localObject1).getValue());
            ((Iterator)localObject2).remove();
          }
        }
      }
    }
    
    public boolean handleMessage(Message paramMessage)
    {
      boolean bool;
      switch (paramMessage.what)
      {
      default: 
        bool = false;
      }
      for (;;)
      {
        return bool;
        handleQueueTask((NotificationManagerCompat.Task)paramMessage.obj);
        bool = true;
        continue;
        paramMessage = (NotificationManagerCompat.ServiceConnectedEvent)paramMessage.obj;
        handleServiceConnected(paramMessage.componentName, paramMessage.iBinder);
        bool = true;
        continue;
        handleServiceDisconnected((ComponentName)paramMessage.obj);
        bool = true;
        continue;
        handleRetryListenerQueue((ComponentName)paramMessage.obj);
        bool = true;
      }
    }
    
    public void onServiceConnected(ComponentName paramComponentName, IBinder paramIBinder)
    {
      if (Log.isLoggable("NotifManCompat", 3)) {
        Log.d("NotifManCompat", "Connected to service " + paramComponentName);
      }
      this.mHandler.obtainMessage(1, new NotificationManagerCompat.ServiceConnectedEvent(paramComponentName, paramIBinder)).sendToTarget();
    }
    
    public void onServiceDisconnected(ComponentName paramComponentName)
    {
      if (Log.isLoggable("NotifManCompat", 3)) {
        Log.d("NotifManCompat", "Disconnected from service " + paramComponentName);
      }
      this.mHandler.obtainMessage(2, paramComponentName).sendToTarget();
    }
    
    public void queueTask(NotificationManagerCompat.Task paramTask)
    {
      this.mHandler.obtainMessage(0, paramTask).sendToTarget();
    }
    
    private static class ListenerRecord
    {
      boolean bound = false;
      final ComponentName componentName;
      int retryCount = 0;
      INotificationSideChannel service;
      ArrayDeque<NotificationManagerCompat.Task> taskQueue = new ArrayDeque();
      
      ListenerRecord(ComponentName paramComponentName)
      {
        this.componentName = paramComponentName;
      }
    }
  }
  
  private static abstract interface Task
  {
    public abstract void send(INotificationSideChannel paramINotificationSideChannel)
      throws RemoteException;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/android/support/v4/app/NotificationManagerCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */