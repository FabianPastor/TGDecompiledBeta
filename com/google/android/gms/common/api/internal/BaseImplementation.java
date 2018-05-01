package com.google.android.gms.common.api.internal;

import android.os.DeadObjectException;
import android.os.RemoteException;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.Api.AnyClient;
import com.google.android.gms.common.api.Api.AnyClientKey;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.common.internal.SimpleClientAdapter;

public class BaseImplementation
{
  public static abstract class ApiMethodImpl<R extends Result, A extends Api.AnyClient>
    extends BasePendingResult<R>
    implements BaseImplementation.ResultHolder<R>
  {
    private final Api<?> mApi;
    private final Api.AnyClientKey<A> mClientKey;
    
    protected ApiMethodImpl(Api<?> paramApi, GoogleApiClient paramGoogleApiClient)
    {
      super();
      Preconditions.checkNotNull(paramApi, "Api must not be null");
      this.mClientKey = paramApi.getClientKey();
      this.mApi = paramApi;
    }
    
    private void setFailedResult(RemoteException paramRemoteException)
    {
      setFailedResult(new Status(8, paramRemoteException.getLocalizedMessage(), null));
    }
    
    protected abstract void doExecute(A paramA)
      throws RemoteException;
    
    public final Api<?> getApi()
    {
      return this.mApi;
    }
    
    public final Api.AnyClientKey<A> getClientKey()
    {
      return this.mClientKey;
    }
    
    protected void onSetFailedResult(R paramR) {}
    
    public final void run(A paramA)
      throws DeadObjectException
    {
      Object localObject = paramA;
      if ((paramA instanceof SimpleClientAdapter)) {
        localObject = ((SimpleClientAdapter)paramA).getClient();
      }
      try
      {
        doExecute((Api.AnyClient)localObject);
        return;
      }
      catch (DeadObjectException paramA)
      {
        setFailedResult(paramA);
        throw paramA;
      }
      catch (RemoteException paramA)
      {
        for (;;)
        {
          setFailedResult(paramA);
        }
      }
    }
    
    public final void setFailedResult(Status paramStatus)
    {
      if (!paramStatus.isSuccess()) {}
      for (boolean bool = true;; bool = false)
      {
        Preconditions.checkArgument(bool, "Failed result must not be success");
        paramStatus = createFailedResult(paramStatus);
        setResult(paramStatus);
        onSetFailedResult(paramStatus);
        return;
      }
    }
  }
  
  public static abstract interface ResultHolder<R>
  {
    public abstract void setResult(R paramR);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/api/internal/BaseImplementation.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */