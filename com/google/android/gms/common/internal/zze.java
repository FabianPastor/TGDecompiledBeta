package com.google.android.gms.common.internal;

import android.accounts.Account;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.DeadObjectException;
import android.os.Handler;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.support.annotation.BinderThread;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.zzc;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class zze<T extends IInterface>
{
  public static final String[] Bs = { "service_esmobile", "service_googleme" };
  private int Ba;
  private long Bb;
  private long Bc;
  private int Bd;
  private long Be;
  private final zzn Bf;
  private final Object Bg = new Object();
  private zzv Bh;
  private zzf Bi;
  private T Bj;
  private final ArrayList<zze<?>> Bk = new ArrayList();
  private zzh Bl;
  private int Bm = 1;
  private final zzb Bn;
  private final zzc Bo;
  private final int Bp;
  private final String Bq;
  protected AtomicInteger Br = new AtomicInteger(0);
  private final Context mContext;
  final Handler mHandler;
  private final zzc xn;
  private final Looper zzajn;
  private final Object zzakd = new Object();
  
  protected zze(Context paramContext, Looper paramLooper, int paramInt, zzb paramzzb, zzc paramzzc, String paramString)
  {
    this(paramContext, paramLooper, zzn.zzcf(paramContext), zzc.zzapd(), paramInt, (zzb)zzac.zzy(paramzzb), (zzc)zzac.zzy(paramzzc), paramString);
  }
  
  protected zze(Context paramContext, Looper paramLooper, zzn paramzzn, zzc paramzzc, int paramInt, zzb paramzzb, zzc paramzzc1, String paramString)
  {
    this.mContext = ((Context)zzac.zzb(paramContext, "Context must not be null"));
    this.zzajn = ((Looper)zzac.zzb(paramLooper, "Looper must not be null"));
    this.Bf = ((zzn)zzac.zzb(paramzzn, "Supervisor must not be null"));
    this.xn = ((zzc)zzac.zzb(paramzzc, "API availability must not be null"));
    this.mHandler = new zzd(paramLooper);
    this.Bp = paramInt;
    this.Bn = paramzzb;
    this.Bo = paramzzc1;
    this.Bq = paramString;
  }
  
  private boolean zza(int paramInt1, int paramInt2, T paramT)
  {
    synchronized (this.zzakd)
    {
      if (this.Bm != paramInt1) {
        return false;
      }
      zzb(paramInt2, paramT);
      return true;
    }
  }
  
  private void zzats()
  {
    String str1;
    String str2;
    if (this.Bl != null)
    {
      str1 = String.valueOf(zzix());
      str2 = String.valueOf(zzatq());
      Log.e("GmsClient", String.valueOf(str1).length() + 70 + String.valueOf(str2).length() + "Calling connect() while still connected, missing disconnect() for " + str1 + " on " + str2);
      this.Bf.zzb(zzix(), zzatq(), this.Bl, zzatr());
      this.Br.incrementAndGet();
    }
    this.Bl = new zzh(this.Br.get());
    if (!this.Bf.zza(zzix(), zzatq(), this.Bl, zzatr()))
    {
      str1 = String.valueOf(zzix());
      str2 = String.valueOf(zzatq());
      Log.e("GmsClient", String.valueOf(str1).length() + 34 + String.valueOf(str2).length() + "unable to connect to service: " + str1 + " on " + str2);
      zza(16, null, this.Br.get());
    }
  }
  
  private void zzatt()
  {
    if (this.Bl != null)
    {
      this.Bf.zzb(zzix(), zzatq(), this.Bl, zzatr());
      this.Bl = null;
    }
  }
  
  private void zzb(int paramInt, T paramT)
  {
    boolean bool = true;
    int i;
    int j;
    if (paramInt == 3)
    {
      i = 1;
      if (paramT == null) {
        break label120;
      }
      j = 1;
      label17:
      if (i != j) {
        break label126;
      }
    }
    for (;;)
    {
      zzac.zzbs(bool);
      for (;;)
      {
        synchronized (this.zzakd)
        {
          this.Bm = paramInt;
          this.Bj = paramT;
          zzc(paramInt, paramT);
          switch (paramInt)
          {
          case 2: 
            return;
            zzats();
          }
        }
        zza(paramT);
        continue;
        zzatt();
      }
      i = 0;
      break;
      label120:
      j = 0;
      break label17;
      label126:
      bool = false;
    }
  }
  
  public void disconnect()
  {
    this.Br.incrementAndGet();
    synchronized (this.Bk)
    {
      int j = this.Bk.size();
      int i = 0;
      while (i < j)
      {
        ((zze)this.Bk.get(i)).zzaud();
        i += 1;
      }
      this.Bk.clear();
    }
    synchronized (this.Bg)
    {
      this.Bh = null;
      zzb(1, null);
      return;
      localObject2 = finally;
      throw ((Throwable)localObject2);
    }
  }
  
  public void dump(String paramString, FileDescriptor arg2, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    for (;;)
    {
      synchronized (this.zzakd)
      {
        int i = this.Bm;
        paramArrayOfString = this.Bj;
        paramPrintWriter.append(paramString).append("mConnectState=");
        switch (i)
        {
        default: 
          paramPrintWriter.print("UNKNOWN");
          paramPrintWriter.append(" mService=");
          if (paramArrayOfString != null) {
            break label482;
          }
          paramPrintWriter.println("null");
          ??? = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US);
          long l;
          String str;
          if (this.Bc > 0L)
          {
            paramArrayOfString = paramPrintWriter.append(paramString).append("lastConnectedTime=");
            l = this.Bc;
            str = String.valueOf(???.format(new Date(this.Bc)));
            paramArrayOfString.println(String.valueOf(str).length() + 21 + l + " " + str);
          }
          if (this.Bb > 0L) {
            paramPrintWriter.append(paramString).append("lastSuspendedCause=");
          }
          switch (this.Ba)
          {
          default: 
            paramPrintWriter.append(String.valueOf(this.Ba));
            paramArrayOfString = paramPrintWriter.append(" lastSuspendedTime=");
            l = this.Bb;
            str = String.valueOf(???.format(new Date(this.Bb)));
            paramArrayOfString.println(String.valueOf(str).length() + 21 + l + " " + str);
            if (this.Be > 0L)
            {
              paramPrintWriter.append(paramString).append("lastFailedStatus=").append(CommonStatusCodes.getStatusCodeString(this.Bd));
              paramString = paramPrintWriter.append(" lastFailedTime=");
              l = this.Be;
              ??? = String.valueOf(???.format(new Date(this.Be)));
              paramString.println(String.valueOf(???).length() + 21 + l + " " + ???);
            }
            return;
          }
          break;
        }
      }
      paramPrintWriter.print("CONNECTING");
      continue;
      paramPrintWriter.print("CONNECTED");
      continue;
      paramPrintWriter.print("DISCONNECTING");
      continue;
      paramPrintWriter.print("DISCONNECTED");
      continue;
      label482:
      paramPrintWriter.append(zziy()).append("@").println(Integer.toHexString(System.identityHashCode(paramArrayOfString.asBinder())));
      continue;
      paramPrintWriter.append("CAUSE_SERVICE_DISCONNECTED");
      continue;
      paramPrintWriter.append("CAUSE_NETWORK_LOST");
    }
  }
  
  public Account getAccount()
  {
    return null;
  }
  
  public final Context getContext()
  {
    return this.mContext;
  }
  
  public final Looper getLooper()
  {
    return this.zzajn;
  }
  
  public boolean isConnected()
  {
    for (;;)
    {
      synchronized (this.zzakd)
      {
        if (this.Bm == 3)
        {
          bool = true;
          return bool;
        }
      }
      boolean bool = false;
    }
  }
  
  public boolean isConnecting()
  {
    for (;;)
    {
      synchronized (this.zzakd)
      {
        if (this.Bm == 2)
        {
          bool = true;
          return bool;
        }
      }
      boolean bool = false;
    }
  }
  
  @CallSuper
  protected void onConnectionFailed(ConnectionResult paramConnectionResult)
  {
    this.Bd = paramConnectionResult.getErrorCode();
    this.Be = System.currentTimeMillis();
  }
  
  @CallSuper
  protected void onConnectionSuspended(int paramInt)
  {
    this.Ba = paramInt;
    this.Bb = System.currentTimeMillis();
  }
  
  protected void zza(int paramInt1, @Nullable Bundle paramBundle, int paramInt2)
  {
    this.mHandler.sendMessage(this.mHandler.obtainMessage(5, paramInt2, -1, new zzk(paramInt1, paramBundle)));
  }
  
  @BinderThread
  protected void zza(int paramInt1, IBinder paramIBinder, Bundle paramBundle, int paramInt2)
  {
    this.mHandler.sendMessage(this.mHandler.obtainMessage(1, paramInt2, -1, new zzj(paramInt1, paramIBinder, paramBundle)));
  }
  
  @CallSuper
  protected void zza(@NonNull T paramT)
  {
    this.Bc = System.currentTimeMillis();
  }
  
  public void zza(@NonNull zzf paramzzf)
  {
    this.Bi = ((zzf)zzac.zzb(paramzzf, "Connection progress callbacks cannot be null."));
    zzb(2, null);
  }
  
  public void zza(zzf paramzzf, ConnectionResult paramConnectionResult)
  {
    this.Bi = ((zzf)zzac.zzb(paramzzf, "Connection progress callbacks cannot be null."));
    this.mHandler.sendMessage(this.mHandler.obtainMessage(3, this.Br.get(), paramConnectionResult.getErrorCode(), paramConnectionResult.getResolution()));
  }
  
  @WorkerThread
  public void zza(zzr arg1, Set<Scope> paramSet)
  {
    try
    {
      Object localObject = zzagl();
      localObject = new GetServiceRequest(this.Bp).zzht(this.mContext.getPackageName()).zzo((Bundle)localObject);
      if (paramSet != null) {
        ((GetServiceRequest)localObject).zzf(paramSet);
      }
      if (zzahd()) {
        ((GetServiceRequest)localObject).zzd(zzatv()).zzb(???);
      }
      return;
    }
    catch (DeadObjectException ???)
    {
      synchronized (this.Bg)
      {
        while (this.Bh != null)
        {
          this.Bh.zza(new zzg(this, this.Br.get()), (GetServiceRequest)localObject);
          return;
          if (zzaty())
          {
            ((GetServiceRequest)localObject).zzd(getAccount());
            continue;
            ??? = ???;
            Log.w("GmsClient", "service died");
            zzgl(1);
            return;
          }
        }
        Log.w("GmsClient", "mServiceBroker is null, client disconnected");
      }
    }
    catch (RemoteException ???)
    {
      Log.w("GmsClient", "Remote exception occurred", ???);
    }
  }
  
  protected Bundle zzagl()
  {
    return new Bundle();
  }
  
  public boolean zzahd()
  {
    return false;
  }
  
  public boolean zzahs()
  {
    return false;
  }
  
  public Intent zzaht()
  {
    throw new UnsupportedOperationException("Not a sign in API");
  }
  
  public Bundle zzaoe()
  {
    return null;
  }
  
  public boolean zzapr()
  {
    return true;
  }
  
  @Nullable
  public IBinder zzaps()
  {
    synchronized (this.Bg)
    {
      if (this.Bh == null) {
        return null;
      }
      IBinder localIBinder = this.Bh.asBinder();
      return localIBinder;
    }
  }
  
  protected String zzatq()
  {
    return "com.google.android.gms";
  }
  
  @Nullable
  protected final String zzatr()
  {
    if (this.Bq == null) {
      return this.mContext.getClass().getName();
    }
    return this.Bq;
  }
  
  public void zzatu()
  {
    int i = this.xn.isGooglePlayServicesAvailable(this.mContext);
    if (i != 0)
    {
      zzb(1, null);
      this.Bi = new zzi();
      this.mHandler.sendMessage(this.mHandler.obtainMessage(3, this.Br.get(), i));
      return;
    }
    zza(new zzi());
  }
  
  public final Account zzatv()
  {
    if (getAccount() != null) {
      return getAccount();
    }
    return new Account("<<default account>>", "com.google");
  }
  
  protected final void zzatw()
  {
    if (!isConnected()) {
      throw new IllegalStateException("Not connected. Call connect() and wait for onConnected() to be called.");
    }
  }
  
  public final T zzatx()
    throws DeadObjectException
  {
    synchronized (this.zzakd)
    {
      if (this.Bm == 4) {
        throw new DeadObjectException();
      }
    }
    zzatw();
    if (this.Bj != null) {}
    for (boolean bool = true;; bool = false)
    {
      zzac.zza(bool, "Client is connected but service is null");
      IInterface localIInterface = this.Bj;
      return localIInterface;
    }
  }
  
  public boolean zzaty()
  {
    return false;
  }
  
  protected Set<Scope> zzatz()
  {
    return Collections.EMPTY_SET;
  }
  
  void zzc(int paramInt, T paramT) {}
  
  public void zzgl(int paramInt)
  {
    this.mHandler.sendMessage(this.mHandler.obtainMessage(4, this.Br.get(), paramInt));
  }
  
  @Nullable
  protected abstract T zzh(IBinder paramIBinder);
  
  @NonNull
  protected abstract String zzix();
  
  @NonNull
  protected abstract String zziy();
  
  private abstract class zza
    extends zze.zze<Boolean>
  {
    public final Bundle Bt;
    public final int statusCode;
    
    @BinderThread
    protected zza(int paramInt, Bundle paramBundle)
    {
      super(Boolean.valueOf(true));
      this.statusCode = paramInt;
      this.Bt = paramBundle;
    }
    
    protected abstract boolean zzaua();
    
    protected void zzaub() {}
    
    protected void zzc(Boolean paramBoolean)
    {
      Object localObject = null;
      if (paramBoolean == null) {
        zze.zza(zze.this, 1, null);
      }
      do
      {
        return;
        switch (this.statusCode)
        {
        default: 
          zze.zza(zze.this, 1, null);
          paramBoolean = (Boolean)localObject;
          if (this.Bt != null) {
            paramBoolean = (PendingIntent)this.Bt.getParcelable("pendingIntent");
          }
          zzm(new ConnectionResult(this.statusCode, paramBoolean));
          return;
        }
      } while (zzaua());
      zze.zza(zze.this, 1, null);
      zzm(new ConnectionResult(8, null));
      return;
      zze.zza(zze.this, 1, null);
      throw new IllegalStateException("A fatal developer error has occurred. Check the logs for further information.");
    }
    
    protected abstract void zzm(ConnectionResult paramConnectionResult);
  }
  
  public static abstract interface zzb
  {
    public abstract void onConnected(@Nullable Bundle paramBundle);
    
    public abstract void onConnectionSuspended(int paramInt);
  }
  
  public static abstract interface zzc
  {
    public abstract void onConnectionFailed(@NonNull ConnectionResult paramConnectionResult);
  }
  
  final class zzd
    extends Handler
  {
    public zzd(Looper paramLooper)
    {
      super();
    }
    
    private void zza(Message paramMessage)
    {
      paramMessage = (zze.zze)paramMessage.obj;
      paramMessage.zzaub();
      paramMessage.unregister();
    }
    
    private boolean zzb(Message paramMessage)
    {
      return (paramMessage.what == 2) || (paramMessage.what == 1) || (paramMessage.what == 5);
    }
    
    public void handleMessage(Message paramMessage)
    {
      PendingIntent localPendingIntent = null;
      if (zze.this.Br.get() != paramMessage.arg1)
      {
        if (zzb(paramMessage)) {
          zza(paramMessage);
        }
        return;
      }
      if (((paramMessage.what == 1) || (paramMessage.what == 5)) && (!zze.this.isConnecting()))
      {
        zza(paramMessage);
        return;
      }
      if (paramMessage.what == 3)
      {
        if ((paramMessage.obj instanceof PendingIntent)) {
          localPendingIntent = (PendingIntent)paramMessage.obj;
        }
        paramMessage = new ConnectionResult(paramMessage.arg2, localPendingIntent);
        zze.zzb(zze.this).zzh(paramMessage);
        zze.this.onConnectionFailed(paramMessage);
        return;
      }
      if (paramMessage.what == 4)
      {
        zze.zza(zze.this, 4, null);
        if (zze.zzc(zze.this) != null) {
          zze.zzc(zze.this).onConnectionSuspended(paramMessage.arg2);
        }
        zze.this.onConnectionSuspended(paramMessage.arg2);
        zze.zza(zze.this, 4, 1, null);
        return;
      }
      if ((paramMessage.what == 2) && (!zze.this.isConnected()))
      {
        zza(paramMessage);
        return;
      }
      if (zzb(paramMessage))
      {
        ((zze.zze)paramMessage.obj).zzauc();
        return;
      }
      int i = paramMessage.what;
      Log.wtf("GmsClient", 45 + "Don't know how to handle message: " + i, new Exception());
    }
  }
  
  protected abstract class zze<TListener>
  {
    private boolean Bv;
    private TListener mListener;
    
    public zze()
    {
      Object localObject;
      this.mListener = localObject;
      this.Bv = false;
    }
    
    public void unregister()
    {
      zzaud();
      synchronized (zze.zzd(zze.this))
      {
        zze.zzd(zze.this).remove(this);
        return;
      }
    }
    
    protected abstract void zzaub();
    
    public void zzauc()
    {
      for (;;)
      {
        try
        {
          Object localObject1 = this.mListener;
          if (this.Bv)
          {
            String str = String.valueOf(this);
            Log.w("GmsClient", String.valueOf(str).length() + 47 + "Callback proxy " + str + " being reused. This is not safe.");
          }
          if (localObject1 != null) {}
          zzaub();
        }
        finally
        {
          try
          {
            zzv(localObject1);
          }
          catch (RuntimeException localRuntimeException)
          {
            zzaub();
            throw localRuntimeException;
          }
          try
          {
            this.Bv = true;
            unregister();
            return;
          }
          finally {}
          localObject2 = finally;
        }
      }
    }
    
    public void zzaud()
    {
      try
      {
        this.mListener = null;
        return;
      }
      finally {}
    }
    
    protected abstract void zzv(TListener paramTListener);
  }
  
  public static abstract interface zzf
  {
    public abstract void zzh(@NonNull ConnectionResult paramConnectionResult);
  }
  
  public static final class zzg
    extends zzu.zza
  {
    private zze Bw;
    private final int Bx;
    
    public zzg(@NonNull zze paramzze, int paramInt)
    {
      this.Bw = paramzze;
      this.Bx = paramInt;
    }
    
    private void zzaue()
    {
      this.Bw = null;
    }
    
    @BinderThread
    public void zza(int paramInt, @NonNull IBinder paramIBinder, @Nullable Bundle paramBundle)
    {
      zzac.zzb(this.Bw, "onPostInitComplete can be called only once per call to getRemoteService");
      this.Bw.zza(paramInt, paramIBinder, paramBundle, this.Bx);
      zzaue();
    }
    
    @BinderThread
    public void zzb(int paramInt, @Nullable Bundle paramBundle)
    {
      Log.wtf("GmsClient", "received deprecated onAccountValidationComplete callback, ignoring", new Exception());
    }
  }
  
  public final class zzh
    implements ServiceConnection
  {
    private final int Bx;
    
    public zzh(int paramInt)
    {
      this.Bx = paramInt;
    }
    
    public void onServiceConnected(ComponentName arg1, IBinder paramIBinder)
    {
      zzac.zzb(paramIBinder, "Expecting a valid IBinder");
      synchronized (zze.zza(zze.this))
      {
        zze.zza(zze.this, zzv.zza.zzdv(paramIBinder));
        zze.this.zza(0, null, this.Bx);
        return;
      }
    }
    
    public void onServiceDisconnected(ComponentName arg1)
    {
      synchronized (zze.zza(zze.this))
      {
        zze.zza(zze.this, null);
        zze.this.mHandler.sendMessage(zze.this.mHandler.obtainMessage(4, this.Bx, 1));
        return;
      }
    }
  }
  
  protected class zzi
    implements zze.zzf
  {
    public zzi() {}
    
    public void zzh(@NonNull ConnectionResult paramConnectionResult)
    {
      if (paramConnectionResult.isSuccess()) {
        zze.this.zza(null, zze.this.zzatz());
      }
      while (zze.zze(zze.this) == null) {
        return;
      }
      zze.zze(zze.this).onConnectionFailed(paramConnectionResult);
    }
  }
  
  protected final class zzj
    extends zze.zza
  {
    public final IBinder By;
    
    @BinderThread
    public zzj(int paramInt, IBinder paramIBinder, Bundle paramBundle)
    {
      super(paramInt, paramBundle);
      this.By = paramIBinder;
    }
    
    protected boolean zzaua()
    {
      do
      {
        try
        {
          String str1 = this.By.getInterfaceDescriptor();
          if (!zze.this.zziy().equals(str1))
          {
            String str2 = String.valueOf(zze.this.zziy());
            Log.e("GmsClient", String.valueOf(str2).length() + 34 + String.valueOf(str1).length() + "service descriptor mismatch: " + str2 + " vs. " + str1);
            return false;
          }
        }
        catch (RemoteException localRemoteException)
        {
          Log.w("GmsClient", "service probably died");
          return false;
        }
        localObject = zze.this.zzh(this.By);
      } while ((localObject == null) || (!zze.zza(zze.this, 2, 3, (IInterface)localObject)));
      Object localObject = zze.this.zzaoe();
      if (zze.zzc(zze.this) != null) {
        zze.zzc(zze.this).onConnected((Bundle)localObject);
      }
      return true;
    }
    
    protected void zzm(ConnectionResult paramConnectionResult)
    {
      if (zze.zze(zze.this) != null) {
        zze.zze(zze.this).onConnectionFailed(paramConnectionResult);
      }
      zze.this.onConnectionFailed(paramConnectionResult);
    }
  }
  
  protected final class zzk
    extends zze.zza
  {
    @BinderThread
    public zzk(int paramInt, @Nullable Bundle paramBundle)
    {
      super(paramInt, paramBundle);
    }
    
    protected boolean zzaua()
    {
      zze.zzb(zze.this).zzh(ConnectionResult.uJ);
      return true;
    }
    
    protected void zzm(ConnectionResult paramConnectionResult)
    {
      zze.zzb(zze.this).zzh(paramConnectionResult);
      zze.this.onConnectionFailed(paramConnectionResult);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/internal/zze.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */