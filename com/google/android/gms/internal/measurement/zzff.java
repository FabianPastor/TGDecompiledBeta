package com.google.android.gms.internal.measurement;

import android.content.Context;
import android.os.Looper;
import com.google.android.gms.common.internal.BaseGmsClient;
import com.google.android.gms.common.internal.BaseGmsClient.BaseConnectionCallbacks;
import com.google.android.gms.common.internal.BaseGmsClient.BaseOnConnectionFailedListener;

public final class zzff
  extends BaseGmsClient<zzey>
{
  public zzff(Context paramContext, Looper paramLooper, BaseGmsClient.BaseConnectionCallbacks paramBaseConnectionCallbacks, BaseGmsClient.BaseOnConnectionFailedListener paramBaseOnConnectionFailedListener)
  {
    super(paramContext, paramLooper, 93, paramBaseConnectionCallbacks, paramBaseOnConnectionFailedListener, null);
  }
  
  protected final String getServiceDescriptor()
  {
    return "com.google.android.gms.measurement.internal.IMeasurementService";
  }
  
  protected final String getStartServiceAction()
  {
    return "com.google.android.gms.measurement.START";
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/measurement/zzff.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */