package com.google.android.gms.maps;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.dynamic.zza;
import com.google.android.gms.dynamic.zze;
import com.google.android.gms.dynamic.zzf;
import com.google.android.gms.maps.internal.IGoogleMapDelegate;
import com.google.android.gms.maps.internal.IMapViewDelegate;
import com.google.android.gms.maps.internal.MapLifecycleDelegate;
import com.google.android.gms.maps.internal.zzai;
import com.google.android.gms.maps.internal.zzc;
import com.google.android.gms.maps.internal.zzt.zza;
import com.google.android.gms.maps.model.RuntimeRemoteException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MapView
  extends FrameLayout
{
  private final zzb alI;
  
  public MapView(Context paramContext)
  {
    super(paramContext);
    this.alI = new zzb(this, paramContext, null);
    zzbrv();
  }
  
  public MapView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    this.alI = new zzb(this, paramContext, GoogleMapOptions.createFromAttributes(paramContext, paramAttributeSet));
    zzbrv();
  }
  
  public MapView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    this.alI = new zzb(this, paramContext, GoogleMapOptions.createFromAttributes(paramContext, paramAttributeSet));
    zzbrv();
  }
  
  public MapView(Context paramContext, GoogleMapOptions paramGoogleMapOptions)
  {
    super(paramContext);
    this.alI = new zzb(this, paramContext, paramGoogleMapOptions);
    zzbrv();
  }
  
  private void zzbrv()
  {
    setClickable(true);
  }
  
  public void getMapAsync(OnMapReadyCallback paramOnMapReadyCallback)
  {
    zzac.zzhq("getMapAsync() must be called on the main thread");
    this.alI.getMapAsync(paramOnMapReadyCallback);
  }
  
  public final void onCreate(Bundle paramBundle)
  {
    this.alI.onCreate(paramBundle);
    if (this.alI.zzbdt() == null) {
      zza.zzb(this);
    }
  }
  
  public final void onDestroy()
  {
    this.alI.onDestroy();
  }
  
  public final void onEnterAmbient(Bundle paramBundle)
  {
    zzac.zzhq("onEnterAmbient() must be called on the main thread");
    this.alI.onEnterAmbient(paramBundle);
  }
  
  public final void onExitAmbient()
  {
    zzac.zzhq("onExitAmbient() must be called on the main thread");
    this.alI.onExitAmbient();
  }
  
  public final void onLowMemory()
  {
    this.alI.onLowMemory();
  }
  
  public final void onPause()
  {
    this.alI.onPause();
  }
  
  public final void onResume()
  {
    this.alI.onResume();
  }
  
  public final void onSaveInstanceState(Bundle paramBundle)
  {
    this.alI.onSaveInstanceState(paramBundle);
  }
  
  public final void onStart()
  {
    this.alI.onStart();
  }
  
  public final void onStop()
  {
    this.alI.onStop();
  }
  
  static class zza
    implements MapLifecycleDelegate
  {
    private final ViewGroup alJ;
    private final IMapViewDelegate alK;
    private View alL;
    
    public zza(ViewGroup paramViewGroup, IMapViewDelegate paramIMapViewDelegate)
    {
      this.alK = ((IMapViewDelegate)zzac.zzy(paramIMapViewDelegate));
      this.alJ = ((ViewGroup)zzac.zzy(paramViewGroup));
    }
    
    public void getMapAsync(final OnMapReadyCallback paramOnMapReadyCallback)
    {
      try
      {
        this.alK.getMapAsync(new zzt.zza()
        {
          public void zza(IGoogleMapDelegate paramAnonymousIGoogleMapDelegate)
            throws RemoteException
          {
            paramOnMapReadyCallback.onMapReady(new GoogleMap(paramAnonymousIGoogleMapDelegate));
          }
        });
        return;
      }
      catch (RemoteException paramOnMapReadyCallback)
      {
        throw new RuntimeRemoteException(paramOnMapReadyCallback);
      }
    }
    
    public void onCreate(Bundle paramBundle)
    {
      try
      {
        this.alK.onCreate(paramBundle);
        this.alL = ((View)zze.zzae(this.alK.getView()));
        this.alJ.removeAllViews();
        this.alJ.addView(this.alL);
        return;
      }
      catch (RemoteException paramBundle)
      {
        throw new RuntimeRemoteException(paramBundle);
      }
    }
    
    public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
    {
      throw new UnsupportedOperationException("onCreateView not allowed on MapViewDelegate");
    }
    
    public void onDestroy()
    {
      try
      {
        this.alK.onDestroy();
        return;
      }
      catch (RemoteException localRemoteException)
      {
        throw new RuntimeRemoteException(localRemoteException);
      }
    }
    
    public void onDestroyView()
    {
      throw new UnsupportedOperationException("onDestroyView not allowed on MapViewDelegate");
    }
    
    public void onEnterAmbient(Bundle paramBundle)
    {
      try
      {
        this.alK.onEnterAmbient(paramBundle);
        return;
      }
      catch (RemoteException paramBundle)
      {
        throw new RuntimeRemoteException(paramBundle);
      }
    }
    
    public void onExitAmbient()
    {
      try
      {
        this.alK.onExitAmbient();
        return;
      }
      catch (RemoteException localRemoteException)
      {
        throw new RuntimeRemoteException(localRemoteException);
      }
    }
    
    public void onInflate(Activity paramActivity, Bundle paramBundle1, Bundle paramBundle2)
    {
      throw new UnsupportedOperationException("onInflate not allowed on MapViewDelegate");
    }
    
    public void onLowMemory()
    {
      try
      {
        this.alK.onLowMemory();
        return;
      }
      catch (RemoteException localRemoteException)
      {
        throw new RuntimeRemoteException(localRemoteException);
      }
    }
    
    public void onPause()
    {
      try
      {
        this.alK.onPause();
        return;
      }
      catch (RemoteException localRemoteException)
      {
        throw new RuntimeRemoteException(localRemoteException);
      }
    }
    
    public void onResume()
    {
      try
      {
        this.alK.onResume();
        return;
      }
      catch (RemoteException localRemoteException)
      {
        throw new RuntimeRemoteException(localRemoteException);
      }
    }
    
    public void onSaveInstanceState(Bundle paramBundle)
    {
      try
      {
        this.alK.onSaveInstanceState(paramBundle);
        return;
      }
      catch (RemoteException paramBundle)
      {
        throw new RuntimeRemoteException(paramBundle);
      }
    }
    
    public void onStart()
    {
      try
      {
        this.alK.onStart();
        return;
      }
      catch (RemoteException localRemoteException)
      {
        throw new RuntimeRemoteException(localRemoteException);
      }
    }
    
    public void onStop()
    {
      try
      {
        this.alK.onStop();
        return;
      }
      catch (RemoteException localRemoteException)
      {
        throw new RuntimeRemoteException(localRemoteException);
      }
    }
  }
  
  static class zzb
    extends zza<MapView.zza>
  {
    protected zzf<MapView.zza> alG;
    private final List<OnMapReadyCallback> alH = new ArrayList();
    private final ViewGroup alN;
    private final GoogleMapOptions alO;
    private final Context mContext;
    
    zzb(ViewGroup paramViewGroup, Context paramContext, GoogleMapOptions paramGoogleMapOptions)
    {
      this.alN = paramViewGroup;
      this.mContext = paramContext;
      this.alO = paramGoogleMapOptions;
    }
    
    public void getMapAsync(OnMapReadyCallback paramOnMapReadyCallback)
    {
      if (zzbdt() != null)
      {
        ((MapView.zza)zzbdt()).getMapAsync(paramOnMapReadyCallback);
        return;
      }
      this.alH.add(paramOnMapReadyCallback);
    }
    
    public void onEnterAmbient(Bundle paramBundle)
    {
      if (zzbdt() != null) {
        ((MapView.zza)zzbdt()).onEnterAmbient(paramBundle);
      }
    }
    
    public void onExitAmbient()
    {
      if (zzbdt() != null) {
        ((MapView.zza)zzbdt()).onExitAmbient();
      }
    }
    
    protected void zza(zzf<MapView.zza> paramzzf)
    {
      this.alG = paramzzf;
      zzbru();
    }
    
    public void zzbru()
    {
      if ((this.alG != null) && (zzbdt() == null)) {
        try
        {
          MapsInitializer.initialize(this.mContext);
          Object localObject = zzai.zzdp(this.mContext).zza(zze.zzac(this.mContext), this.alO);
          if (localObject == null) {
            return;
          }
          this.alG.zza(new MapView.zza(this.alN, (IMapViewDelegate)localObject));
          localObject = this.alH.iterator();
          while (((Iterator)localObject).hasNext())
          {
            OnMapReadyCallback localOnMapReadyCallback = (OnMapReadyCallback)((Iterator)localObject).next();
            ((MapView.zza)zzbdt()).getMapAsync(localOnMapReadyCallback);
          }
          return;
        }
        catch (RemoteException localRemoteException)
        {
          throw new RuntimeRemoteException(localRemoteException);
          this.alH.clear();
          return;
        }
        catch (GooglePlayServicesNotAvailableException localGooglePlayServicesNotAvailableException) {}
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/maps/MapView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */