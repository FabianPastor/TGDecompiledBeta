package net.hockeyapp.android.metrics;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import net.hockeyapp.android.metrics.model.Base;
import net.hockeyapp.android.metrics.model.Data;
import net.hockeyapp.android.metrics.model.Domain;
import net.hockeyapp.android.metrics.model.Envelope;
import net.hockeyapp.android.metrics.model.TelemetryData;
import net.hockeyapp.android.utils.HockeyLog;
import net.hockeyapp.android.utils.Util;

class Channel
{
  private static final Object LOCK = new Object();
  private static final String TAG = "HockeyApp-Metrics";
  protected static int mMaxBatchCount = 50;
  protected static int mMaxBatchInterval = 15000;
  private final Persistence mPersistence;
  protected final List<String> mQueue;
  private SynchronizeChannelTask mSynchronizeTask;
  protected final TelemetryContext mTelemetryContext;
  private final Timer mTimer;
  
  public Channel(TelemetryContext paramTelemetryContext, Persistence paramPersistence)
  {
    this.mTelemetryContext = paramTelemetryContext;
    this.mQueue = new LinkedList();
    this.mPersistence = paramPersistence;
    this.mTimer = new Timer("HockeyApp User Metrics Sender Queue", true);
  }
  
  protected Envelope createEnvelope(Data<Domain> paramData)
  {
    Envelope localEnvelope = new Envelope();
    localEnvelope.setData(paramData);
    paramData = paramData.getBaseData();
    if ((paramData instanceof TelemetryData)) {
      localEnvelope.setName(((TelemetryData)paramData).getEnvelopeName());
    }
    this.mTelemetryContext.updateScreenResolution();
    localEnvelope.setTime(Util.dateToISO8601(new Date()));
    localEnvelope.setIKey(this.mTelemetryContext.getInstrumentationKey());
    paramData = this.mTelemetryContext.getContextTags();
    if (paramData != null) {
      localEnvelope.setTags(paramData);
    }
    return localEnvelope;
  }
  
  protected void enqueue(String paramString)
  {
    if (paramString == null) {
      return;
    }
    for (;;)
    {
      synchronized (LOCK)
      {
        if (!this.mQueue.add(paramString)) {
          break label71;
        }
        if (this.mQueue.size() >= mMaxBatchCount)
        {
          synchronize();
          return;
        }
      }
      if (this.mQueue.size() == 1)
      {
        scheduleSynchronizeTask();
        continue;
        label71:
        HockeyLog.verbose("HockeyApp-Metrics", "Unable to add item to queue");
      }
    }
  }
  
  public void enqueueData(Base paramBase)
  {
    if ((paramBase instanceof Data))
    {
      Object localObject = null;
      try
      {
        paramBase = createEnvelope((Data)paramBase);
        if (paramBase != null)
        {
          enqueue(serializeEnvelope(paramBase));
          HockeyLog.debug("HockeyApp-Metrics", "enqueued telemetry: " + paramBase.getName());
        }
        return;
      }
      catch (ClassCastException paramBase)
      {
        for (;;)
        {
          HockeyLog.debug("HockeyApp-Metrics", "Telemetry not enqueued, could not create envelope, must be of type ITelemetry");
          paramBase = (Base)localObject;
        }
      }
    }
    HockeyLog.debug("HockeyApp-Metrics", "Telemetry not enqueued, must be of type ITelemetry");
  }
  
  protected void scheduleSynchronizeTask()
  {
    this.mSynchronizeTask = new SynchronizeChannelTask();
    this.mTimer.schedule(this.mSynchronizeTask, mMaxBatchInterval);
  }
  
  protected String serializeEnvelope(Envelope paramEnvelope)
  {
    if (paramEnvelope != null) {}
    try
    {
      StringWriter localStringWriter = new StringWriter();
      paramEnvelope.serialize(localStringWriter);
      return localStringWriter.toString();
    }
    catch (IOException paramEnvelope)
    {
      HockeyLog.debug("HockeyApp-Metrics", "Failed to save data with exception: " + paramEnvelope.toString());
    }
    HockeyLog.debug("HockeyApp-Metrics", "Envelope wasn't empty but failed to serialize anything, returning null");
    return null;
    return null;
  }
  
  protected void synchronize()
  {
    if (this.mSynchronizeTask != null) {
      this.mSynchronizeTask.cancel();
    }
    if (!this.mQueue.isEmpty())
    {
      String[] arrayOfString = new String[this.mQueue.size()];
      this.mQueue.toArray(arrayOfString);
      this.mQueue.clear();
      if (this.mPersistence != null) {
        this.mPersistence.persist(arrayOfString);
      }
    }
  }
  
  private class SynchronizeChannelTask
    extends TimerTask
  {
    public SynchronizeChannelTask() {}
    
    public void run()
    {
      Channel.this.synchronize();
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/net/hockeyapp/android/metrics/Channel.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */