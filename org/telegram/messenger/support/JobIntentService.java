package org.telegram.messenger.support;

import android.app.Service;
import android.app.job.JobInfo;
import android.app.job.JobInfo.Builder;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobServiceEngine;
import android.app.job.JobWorkItem;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import java.util.ArrayList;
import java.util.HashMap;

public abstract class JobIntentService
  extends Service
{
  static final boolean DEBUG = false;
  static final String TAG = "JobIntentService";
  static final HashMap<ComponentName, WorkEnqueuer> sClassWorkEnqueuer = new HashMap();
  static final Object sLock = new Object();
  final ArrayList<CompatWorkItem> mCompatQueue;
  WorkEnqueuer mCompatWorkEnqueuer;
  CommandProcessor mCurProcessor;
  boolean mDestroyed = false;
  boolean mInterruptIfStopped = false;
  CompatJobEngine mJobImpl;
  boolean mStopped = false;
  
  public JobIntentService()
  {
    if (Build.VERSION.SDK_INT >= 26) {}
    for (this.mCompatQueue = null;; this.mCompatQueue = new ArrayList()) {
      return;
    }
  }
  
  public static void enqueueWork(Context paramContext, ComponentName paramComponentName, int paramInt, Intent paramIntent)
  {
    if (paramIntent == null) {
      throw new IllegalArgumentException("work must not be null");
    }
    synchronized (sLock)
    {
      paramContext = getWorkEnqueuer(paramContext, paramComponentName, true, paramInt);
      paramContext.ensureJobId(paramInt);
      paramContext.enqueueWork(paramIntent);
      return;
    }
  }
  
  public static void enqueueWork(Context paramContext, Class paramClass, int paramInt, Intent paramIntent)
  {
    enqueueWork(paramContext, new ComponentName(paramContext, paramClass), paramInt, paramIntent);
  }
  
  static WorkEnqueuer getWorkEnqueuer(Context paramContext, ComponentName paramComponentName, boolean paramBoolean, int paramInt)
  {
    WorkEnqueuer localWorkEnqueuer = (WorkEnqueuer)sClassWorkEnqueuer.get(paramComponentName);
    Object localObject = localWorkEnqueuer;
    if (localWorkEnqueuer == null)
    {
      if (Build.VERSION.SDK_INT < 26) {
        break label69;
      }
      if (!paramBoolean) {
        throw new IllegalArgumentException("Can't be here without a job id");
      }
    }
    label69:
    for (paramContext = new JobWorkEnqueuer(paramContext, paramComponentName, paramInt);; paramContext = new CompatWorkEnqueuer(paramContext, paramComponentName))
    {
      sClassWorkEnqueuer.put(paramComponentName, paramContext);
      localObject = paramContext;
      return (WorkEnqueuer)localObject;
    }
  }
  
  GenericWorkItem dequeueWork()
  {
    GenericWorkItem localGenericWorkItem;
    if (this.mJobImpl != null) {
      localGenericWorkItem = this.mJobImpl.dequeueWork();
    }
    for (;;)
    {
      return localGenericWorkItem;
      synchronized (this.mCompatQueue)
      {
        if (this.mCompatQueue.size() > 0) {
          localGenericWorkItem = (GenericWorkItem)this.mCompatQueue.remove(0);
        }
      }
      Object localObject2 = null;
    }
  }
  
  boolean doStopCurrentWork()
  {
    if (this.mCurProcessor != null) {
      this.mCurProcessor.cancel(this.mInterruptIfStopped);
    }
    this.mStopped = true;
    return onStopCurrentWork();
  }
  
  void ensureProcessorRunningLocked(boolean paramBoolean)
  {
    if (this.mCurProcessor == null)
    {
      this.mCurProcessor = new CommandProcessor();
      if ((this.mCompatWorkEnqueuer != null) && (paramBoolean)) {
        this.mCompatWorkEnqueuer.serviceProcessingStarted();
      }
      this.mCurProcessor.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
    }
  }
  
  public boolean isStopped()
  {
    return this.mStopped;
  }
  
  public IBinder onBind(Intent paramIntent)
  {
    if (this.mJobImpl != null) {}
    for (paramIntent = this.mJobImpl.compatGetBinder();; paramIntent = null) {
      return paramIntent;
    }
  }
  
  public void onCreate()
  {
    super.onCreate();
    if (Build.VERSION.SDK_INT >= 26) {
      this.mJobImpl = new JobServiceEngineImpl(this);
    }
    for (this.mCompatWorkEnqueuer = null;; this.mCompatWorkEnqueuer = getWorkEnqueuer(this, new ComponentName(this, getClass()), false, 0))
    {
      return;
      this.mJobImpl = null;
    }
  }
  
  public void onDestroy()
  {
    super.onDestroy();
    if (this.mCompatQueue != null) {}
    synchronized (this.mCompatQueue)
    {
      this.mDestroyed = true;
      this.mCompatWorkEnqueuer.serviceProcessingFinished();
      return;
    }
  }
  
  protected abstract void onHandleWork(Intent paramIntent);
  
  public int onStartCommand(Intent paramIntent, int paramInt1, int paramInt2)
  {
    if (this.mCompatQueue != null) {
      this.mCompatWorkEnqueuer.serviceStartReceived();
    }
    for (;;)
    {
      synchronized (this.mCompatQueue)
      {
        ArrayList localArrayList2 = this.mCompatQueue;
        CompatWorkItem localCompatWorkItem = new org/telegram/messenger/support/JobIntentService$CompatWorkItem;
        if (paramIntent != null)
        {
          localCompatWorkItem.<init>(this, paramIntent, paramInt2);
          localArrayList2.add(localCompatWorkItem);
          ensureProcessorRunningLocked(true);
          paramInt1 = 3;
          return paramInt1;
        }
        paramIntent = new Intent();
      }
      paramInt1 = 2;
    }
  }
  
  public boolean onStopCurrentWork()
  {
    return true;
  }
  
  void processorFinished()
  {
    if (this.mCompatQueue != null) {}
    synchronized (this.mCompatQueue)
    {
      this.mCurProcessor = null;
      if ((this.mCompatQueue != null) && (this.mCompatQueue.size() > 0)) {
        ensureProcessorRunningLocked(false);
      }
      while (this.mDestroyed) {
        return;
      }
      this.mCompatWorkEnqueuer.serviceProcessingFinished();
    }
  }
  
  public void setInterruptIfStopped(boolean paramBoolean)
  {
    this.mInterruptIfStopped = paramBoolean;
  }
  
  final class CommandProcessor
    extends AsyncTask<Void, Void, Void>
  {
    CommandProcessor() {}
    
    protected Void doInBackground(Void... paramVarArgs)
    {
      for (;;)
      {
        paramVarArgs = JobIntentService.this.dequeueWork();
        if (paramVarArgs == null) {
          break;
        }
        JobIntentService.this.onHandleWork(paramVarArgs.getIntent());
        paramVarArgs.complete();
      }
      return null;
    }
    
    protected void onCancelled(Void paramVoid)
    {
      JobIntentService.this.processorFinished();
    }
    
    protected void onPostExecute(Void paramVoid)
    {
      JobIntentService.this.processorFinished();
    }
  }
  
  static abstract interface CompatJobEngine
  {
    public abstract IBinder compatGetBinder();
    
    public abstract JobIntentService.GenericWorkItem dequeueWork();
  }
  
  static final class CompatWorkEnqueuer
    extends JobIntentService.WorkEnqueuer
  {
    private final Context mContext;
    private final PowerManager.WakeLock mLaunchWakeLock;
    boolean mLaunchingService;
    private final PowerManager.WakeLock mRunWakeLock;
    boolean mServiceProcessing;
    
    CompatWorkEnqueuer(Context paramContext, ComponentName paramComponentName)
    {
      super(paramComponentName);
      this.mContext = paramContext.getApplicationContext();
      paramContext = (PowerManager)paramContext.getSystemService("power");
      this.mLaunchWakeLock = paramContext.newWakeLock(1, paramComponentName.getClassName() + ":launch");
      this.mLaunchWakeLock.setReferenceCounted(false);
      this.mRunWakeLock = paramContext.newWakeLock(1, paramComponentName.getClassName() + ":run");
      this.mRunWakeLock.setReferenceCounted(false);
    }
    
    void enqueueWork(Intent paramIntent)
    {
      paramIntent = new Intent(paramIntent);
      paramIntent.setComponent(this.mComponentName);
      if (this.mContext.startService(paramIntent) != null) {}
      try
      {
        if (!this.mLaunchingService)
        {
          this.mLaunchingService = true;
          if (!this.mServiceProcessing) {
            this.mLaunchWakeLock.acquire(60000L);
          }
        }
        return;
      }
      finally {}
    }
    
    public void serviceProcessingFinished()
    {
      try
      {
        if (this.mServiceProcessing)
        {
          if (this.mLaunchingService) {
            this.mLaunchWakeLock.acquire(60000L);
          }
          this.mServiceProcessing = false;
          this.mRunWakeLock.release();
        }
        return;
      }
      finally {}
    }
    
    public void serviceProcessingStarted()
    {
      try
      {
        if (!this.mServiceProcessing)
        {
          this.mServiceProcessing = true;
          this.mRunWakeLock.acquire(120000L);
          this.mLaunchWakeLock.release();
        }
        return;
      }
      finally {}
    }
    
    public void serviceStartReceived()
    {
      try
      {
        this.mLaunchingService = false;
        return;
      }
      finally {}
    }
  }
  
  final class CompatWorkItem
    implements JobIntentService.GenericWorkItem
  {
    final Intent mIntent;
    final int mStartId;
    
    CompatWorkItem(Intent paramIntent, int paramInt)
    {
      this.mIntent = paramIntent;
      this.mStartId = paramInt;
    }
    
    public void complete()
    {
      JobIntentService.this.stopSelf(this.mStartId);
    }
    
    public Intent getIntent()
    {
      return this.mIntent;
    }
  }
  
  static abstract interface GenericWorkItem
  {
    public abstract void complete();
    
    public abstract Intent getIntent();
  }
  
  static final class JobServiceEngineImpl
    extends JobServiceEngine
    implements JobIntentService.CompatJobEngine
  {
    static final boolean DEBUG = false;
    static final String TAG = "JobServiceEngineImpl";
    final Object mLock = new Object();
    JobParameters mParams;
    final JobIntentService mService;
    
    JobServiceEngineImpl(JobIntentService paramJobIntentService)
    {
      super();
      this.mService = paramJobIntentService;
    }
    
    public IBinder compatGetBinder()
    {
      return getBinder();
    }
    
    public JobIntentService.GenericWorkItem dequeueWork()
    {
      Object localObject1 = null;
      Object localObject2 = null;
      synchronized (this.mLock)
      {
        if (this.mParams == null) {}
        do
        {
          return (JobIntentService.GenericWorkItem)localObject1;
          try
          {
            JobWorkItem localJobWorkItem = this.mParams.dequeueWork();
            localObject2 = localJobWorkItem;
          }
          catch (Throwable localThrowable)
          {
            for (;;) {}
          }
        } while (localObject2 == null);
        ((JobWorkItem)localObject2).getIntent().setExtrasClassLoader(this.mService.getClassLoader());
        localObject1 = new WrapperWorkItem((JobWorkItem)localObject2);
      }
    }
    
    public boolean onStartJob(JobParameters paramJobParameters)
    {
      this.mParams = paramJobParameters;
      this.mService.ensureProcessorRunningLocked(false);
      return true;
    }
    
    public boolean onStopJob(JobParameters paramJobParameters)
    {
      boolean bool = this.mService.doStopCurrentWork();
      synchronized (this.mLock)
      {
        this.mParams = null;
        return bool;
      }
    }
    
    final class WrapperWorkItem
      implements JobIntentService.GenericWorkItem
    {
      final JobWorkItem mJobWork;
      
      WrapperWorkItem(JobWorkItem paramJobWorkItem)
      {
        this.mJobWork = paramJobWorkItem;
      }
      
      public void complete()
      {
        synchronized (JobIntentService.JobServiceEngineImpl.this.mLock)
        {
          if (JobIntentService.JobServiceEngineImpl.this.mParams != null) {
            JobIntentService.JobServiceEngineImpl.this.mParams.completeWork(this.mJobWork);
          }
          return;
        }
      }
      
      public Intent getIntent()
      {
        return this.mJobWork.getIntent();
      }
    }
  }
  
  static final class JobWorkEnqueuer
    extends JobIntentService.WorkEnqueuer
  {
    private final JobInfo mJobInfo;
    private final JobScheduler mJobScheduler;
    
    JobWorkEnqueuer(Context paramContext, ComponentName paramComponentName, int paramInt)
    {
      super(paramComponentName);
      ensureJobId(paramInt);
      this.mJobInfo = new JobInfo.Builder(paramInt, this.mComponentName).setOverrideDeadline(0L).setRequiredNetworkType(1).build();
      this.mJobScheduler = ((JobScheduler)paramContext.getApplicationContext().getSystemService("jobscheduler"));
    }
    
    void enqueueWork(Intent paramIntent)
    {
      this.mJobScheduler.enqueue(this.mJobInfo, new JobWorkItem(paramIntent));
    }
  }
  
  static abstract class WorkEnqueuer
  {
    final ComponentName mComponentName;
    boolean mHasJobId;
    int mJobId;
    
    WorkEnqueuer(Context paramContext, ComponentName paramComponentName)
    {
      this.mComponentName = paramComponentName;
    }
    
    abstract void enqueueWork(Intent paramIntent);
    
    void ensureJobId(int paramInt)
    {
      if (!this.mHasJobId)
      {
        this.mHasJobId = true;
        this.mJobId = paramInt;
      }
      while (this.mJobId == paramInt) {
        return;
      }
      throw new IllegalArgumentException("Given job ID " + paramInt + " is different than previous " + this.mJobId);
    }
    
    public void serviceProcessingFinished() {}
    
    public void serviceProcessingStarted() {}
    
    public void serviceStartReceived() {}
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/support/JobIntentService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */