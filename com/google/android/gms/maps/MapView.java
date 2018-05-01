package com.google.android.gms.maps;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.StrictMode;
import android.os.StrictMode.ThreadPolicy;
import android.os.StrictMode.ThreadPolicy.Builder;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.dynamic.DeferredLifecycleHelper;
import com.google.android.gms.dynamic.ObjectWrapper;
import com.google.android.gms.dynamic.OnDelegateCreatedListener;
import com.google.android.gms.maps.internal.IMapViewDelegate;
import com.google.android.gms.maps.internal.MapLifecycleDelegate;
import com.google.android.gms.maps.internal.zzby;
import com.google.android.gms.maps.internal.zzbz;
import com.google.android.gms.maps.internal.zze;
import com.google.android.gms.maps.model.RuntimeRemoteException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MapView
  extends FrameLayout
{
  private final zzb zzbf;
  
  public MapView(Context paramContext)
  {
    super(paramContext);
    this.zzbf = new zzb(this, paramContext, null);
    setClickable(true);
  }
  
  public MapView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    this.zzbf = new zzb(this, paramContext, GoogleMapOptions.createFromAttributes(paramContext, paramAttributeSet));
    setClickable(true);
  }
  
  public MapView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    this.zzbf = new zzb(this, paramContext, GoogleMapOptions.createFromAttributes(paramContext, paramAttributeSet));
    setClickable(true);
  }
  
  public MapView(Context paramContext, GoogleMapOptions paramGoogleMapOptions)
  {
    super(paramContext);
    this.zzbf = new zzb(this, paramContext, paramGoogleMapOptions);
    setClickable(true);
  }
  
  public void getMapAsync(OnMapReadyCallback paramOnMapReadyCallback)
  {
    Preconditions.checkMainThread("getMapAsync() must be called on the main thread");
    this.zzbf.getMapAsync(paramOnMapReadyCallback);
  }
  
  public final void onCreate(Bundle paramBundle)
  {
    StrictMode.ThreadPolicy localThreadPolicy = StrictMode.getThreadPolicy();
    StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder(localThreadPolicy).permitAll().build());
    try
    {
      this.zzbf.onCreate(paramBundle);
      if (this.zzbf.getDelegate() == null) {
        DeferredLifecycleHelper.showGooglePlayUnavailableMessage(this);
      }
      return;
    }
    finally
    {
      StrictMode.setThreadPolicy(localThreadPolicy);
    }
  }
  
  public final void onDestroy()
  {
    this.zzbf.onDestroy();
  }
  
  public final void onEnterAmbient(Bundle paramBundle)
  {
    Preconditions.checkMainThread("onEnterAmbient() must be called on the main thread");
    zzb localzzb = this.zzbf;
    if (localzzb.getDelegate() != null) {
      ((zza)localzzb.getDelegate()).onEnterAmbient(paramBundle);
    }
  }
  
  public final void onExitAmbient()
  {
    Preconditions.checkMainThread("onExitAmbient() must be called on the main thread");
    zzb localzzb = this.zzbf;
    if (localzzb.getDelegate() != null) {
      ((zza)localzzb.getDelegate()).onExitAmbient();
    }
  }
  
  public final void onLowMemory()
  {
    this.zzbf.onLowMemory();
  }
  
  public final void onPause()
  {
    this.zzbf.onPause();
  }
  
  public final void onResume()
  {
    this.zzbf.onResume();
  }
  
  public final void onSaveInstanceState(Bundle paramBundle)
  {
    this.zzbf.onSaveInstanceState(paramBundle);
  }
  
  public final void onStart()
  {
    this.zzbf.onStart();
  }
  
  public final void onStop()
  {
    this.zzbf.onStop();
  }
  
  static final class zza
    implements MapLifecycleDelegate
  {
    private final ViewGroup parent;
    private final IMapViewDelegate zzbg;
    private View zzbh;
    
    public zza(ViewGroup paramViewGroup, IMapViewDelegate paramIMapViewDelegate)
    {
      this.zzbg = ((IMapViewDelegate)Preconditions.checkNotNull(paramIMapViewDelegate));
      this.parent = ((ViewGroup)Preconditions.checkNotNull(paramViewGroup));
    }
    
    public final void getMapAsync(OnMapReadyCallback paramOnMapReadyCallback)
    {
      try
      {
        IMapViewDelegate localIMapViewDelegate = this.zzbg;
        zzac localzzac = new com/google/android/gms/maps/zzac;
        localzzac.<init>(this, paramOnMapReadyCallback);
        localIMapViewDelegate.getMapAsync(localzzac);
        return;
      }
      catch (RemoteException paramOnMapReadyCallback)
      {
        throw new RuntimeRemoteException(paramOnMapReadyCallback);
      }
    }
    
    public final void onCreate(Bundle paramBundle)
    {
      try
      {
        Bundle localBundle = new android/os/Bundle;
        localBundle.<init>();
        zzby.zza(paramBundle, localBundle);
        this.zzbg.onCreate(localBundle);
        zzby.zza(localBundle, paramBundle);
        this.zzbh = ((View)ObjectWrapper.unwrap(this.zzbg.getView()));
        this.parent.removeAllViews();
        this.parent.addView(this.zzbh);
        return;
      }
      catch (RemoteException paramBundle)
      {
        throw new RuntimeRemoteException(paramBundle);
      }
    }
    
    public final View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
    {
      throw new UnsupportedOperationException("onCreateView not allowed on MapViewDelegate");
    }
    
    public final void onDestroy()
    {
      try
      {
        this.zzbg.onDestroy();
        return;
      }
      catch (RemoteException localRemoteException)
      {
        throw new RuntimeRemoteException(localRemoteException);
      }
    }
    
    public final void onEnterAmbient(Bundle paramBundle)
    {
      try
      {
        Bundle localBundle = new android/os/Bundle;
        localBundle.<init>();
        zzby.zza(paramBundle, localBundle);
        this.zzbg.onEnterAmbient(localBundle);
        zzby.zza(localBundle, paramBundle);
        return;
      }
      catch (RemoteException paramBundle)
      {
        throw new RuntimeRemoteException(paramBundle);
      }
    }
    
    public final void onExitAmbient()
    {
      try
      {
        this.zzbg.onExitAmbient();
        return;
      }
      catch (RemoteException localRemoteException)
      {
        throw new RuntimeRemoteException(localRemoteException);
      }
    }
    
    public final void onInflate(Activity paramActivity, Bundle paramBundle1, Bundle paramBundle2)
    {
      throw new UnsupportedOperationException("onInflate not allowed on MapViewDelegate");
    }
    
    public final void onLowMemory()
    {
      try
      {
        this.zzbg.onLowMemory();
        return;
      }
      catch (RemoteException localRemoteException)
      {
        throw new RuntimeRemoteException(localRemoteException);
      }
    }
    
    public final void onPause()
    {
      try
      {
        this.zzbg.onPause();
        return;
      }
      catch (RemoteException localRemoteException)
      {
        throw new RuntimeRemoteException(localRemoteException);
      }
    }
    
    public final void onResume()
    {
      try
      {
        this.zzbg.onResume();
        return;
      }
      catch (RemoteException localRemoteException)
      {
        throw new RuntimeRemoteException(localRemoteException);
      }
    }
    
    public final void onSaveInstanceState(Bundle paramBundle)
    {
      try
      {
        Bundle localBundle = new android/os/Bundle;
        localBundle.<init>();
        zzby.zza(paramBundle, localBundle);
        this.zzbg.onSaveInstanceState(localBundle);
        zzby.zza(localBundle, paramBundle);
        return;
      }
      catch (RemoteException paramBundle)
      {
        throw new RuntimeRemoteException(paramBundle);
      }
    }
    
    public final void onStart()
    {
      try
      {
        this.zzbg.onStart();
        return;
      }
      catch (RemoteException localRemoteException)
      {
        throw new RuntimeRemoteException(localRemoteException);
      }
    }
    
    public final void onStop()
    {
      try
      {
        this.zzbg.onStop();
        return;
      }
      catch (RemoteException localRemoteException)
      {
        throw new RuntimeRemoteException(localRemoteException);
      }
    }
  }
  
  static final class zzb
    extends DeferredLifecycleHelper<MapView.zza>
  {
    private OnDelegateCreatedListener<MapView.zza> zzbc;
    private final List<OnMapReadyCallback> zzbe = new ArrayList();
    private final ViewGroup zzbi;
    private final Context zzbj;
    private final GoogleMapOptions zzbk;
    
    zzb(ViewGroup paramViewGroup, Context paramContext, GoogleMapOptions paramGoogleMapOptions)
    {
      this.zzbi = paramViewGroup;
      this.zzbj = paramContext;
      this.zzbk = paramGoogleMapOptions;
    }
    
    protected final void createDelegate(OnDelegateCreatedListener<MapView.zza> paramOnDelegateCreatedListener)
    {
      this.zzbc = paramOnDelegateCreatedListener;
      if ((this.zzbc != null) && (getDelegate() == null)) {}
      try
      {
        MapsInitializer.initialize(this.zzbj);
        localObject = zzbz.zza(this.zzbj).zza(ObjectWrapper.wrap(this.zzbj), this.zzbk);
        if (localObject == null) {
          return;
        }
      }
      catch (RemoteException paramOnDelegateCreatedListener)
      {
        for (;;)
        {
          Object localObject;
          OnDelegateCreatedListener localOnDelegateCreatedListener;
          throw new RuntimeRemoteException(paramOnDelegateCreatedListener);
          this.zzbe.clear();
        }
      }
      catch (GooglePlayServicesNotAvailableException paramOnDelegateCreatedListener)
      {
        for (;;) {}
      }
      localOnDelegateCreatedListener = this.zzbc;
      paramOnDelegateCreatedListener = new com/google/android/gms/maps/MapView$zza;
      paramOnDelegateCreatedListener.<init>(this.zzbi, (IMapViewDelegate)localObject);
      localOnDelegateCreatedListener.onDelegateCreated(paramOnDelegateCreatedListener);
      localObject = this.zzbe.iterator();
      while (((Iterator)localObject).hasNext())
      {
        paramOnDelegateCreatedListener = (OnMapReadyCallback)((Iterator)localObject).next();
        ((MapView.zza)getDelegate()).getMapAsync(paramOnDelegateCreatedListener);
      }
    }
    
    public final void getMapAsync(OnMapReadyCallback paramOnMapReadyCallback)
    {
      if (getDelegate() != null) {
        ((MapView.zza)getDelegate()).getMapAsync(paramOnMapReadyCallback);
      }
      for (;;)
      {
        return;
        this.zzbe.add(paramOnMapReadyCallback);
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/maps/MapView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */