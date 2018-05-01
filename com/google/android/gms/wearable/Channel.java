package com.google.android.gms.wearable;

import android.os.Parcelable;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Releasable;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import java.io.InputStream;
import java.io.OutputStream;
import javax.annotation.Nullable;

@Deprecated
public abstract interface Channel
  extends Parcelable
{
  public abstract PendingResult<Status> close(GoogleApiClient paramGoogleApiClient);
  
  public abstract PendingResult<GetInputStreamResult> getInputStream(GoogleApiClient paramGoogleApiClient);
  
  public abstract PendingResult<GetOutputStreamResult> getOutputStream(GoogleApiClient paramGoogleApiClient);
  
  public abstract String getPath();
  
  @Deprecated
  public static abstract interface GetInputStreamResult
    extends Releasable, Result
  {
    @Nullable
    public abstract InputStream getInputStream();
  }
  
  @Deprecated
  public static abstract interface GetOutputStreamResult
    extends Releasable, Result
  {
    @Nullable
    public abstract OutputStream getOutputStream();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/wearable/Channel.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */