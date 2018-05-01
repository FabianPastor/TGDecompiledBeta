package com.google.android.gms.maps;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.dynamic.zza;
import com.google.android.gms.dynamic.zzn;
import com.google.android.gms.dynamic.zzo;
import com.google.android.gms.maps.internal.IMapFragmentDelegate;
import com.google.android.gms.maps.internal.MapLifecycleDelegate;
import com.google.android.gms.maps.internal.zzbw;
import com.google.android.gms.maps.internal.zzbx;
import com.google.android.gms.maps.internal.zze;
import com.google.android.gms.maps.model.RuntimeRemoteException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@TargetApi(11)
public class MapFragment
  extends Fragment
{
  private final zzb zzbmp = new zzb(this);
  
  public static MapFragment newInstance()
  {
    return new MapFragment();
  }
  
  public static MapFragment newInstance(GoogleMapOptions paramGoogleMapOptions)
  {
    MapFragment localMapFragment = new MapFragment();
    Bundle localBundle = new Bundle();
    localBundle.putParcelable("MapOptions", paramGoogleMapOptions);
    localMapFragment.setArguments(localBundle);
    return localMapFragment;
  }
  
  public void getMapAsync(OnMapReadyCallback paramOnMapReadyCallback)
  {
    zzbo.zzcz("getMapAsync must be called on the main thread.");
    this.zzbmp.getMapAsync(paramOnMapReadyCallback);
  }
  
  public void onActivityCreated(Bundle paramBundle)
  {
    if (paramBundle != null) {
      paramBundle.setClassLoader(MapFragment.class.getClassLoader());
    }
    super.onActivityCreated(paramBundle);
  }
  
  public void onAttach(Activity paramActivity)
  {
    super.onAttach(paramActivity);
    zzb.zza(this.zzbmp, paramActivity);
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    this.zzbmp.onCreate(paramBundle);
  }
  
  public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
  {
    paramLayoutInflater = this.zzbmp.onCreateView(paramLayoutInflater, paramViewGroup, paramBundle);
    paramLayoutInflater.setClickable(true);
    return paramLayoutInflater;
  }
  
  public void onDestroy()
  {
    this.zzbmp.onDestroy();
    super.onDestroy();
  }
  
  public void onDestroyView()
  {
    this.zzbmp.onDestroyView();
    super.onDestroyView();
  }
  
  public final void onEnterAmbient(Bundle paramBundle)
  {
    zzbo.zzcz("onEnterAmbient must be called on the main thread.");
    zzb localzzb = this.zzbmp;
    if (localzzb.zztx() != null) {
      ((zza)localzzb.zztx()).onEnterAmbient(paramBundle);
    }
  }
  
  public final void onExitAmbient()
  {
    zzbo.zzcz("onExitAmbient must be called on the main thread.");
    zzb localzzb = this.zzbmp;
    if (localzzb.zztx() != null) {
      ((zza)localzzb.zztx()).onExitAmbient();
    }
  }
  
  @SuppressLint({"NewApi"})
  public void onInflate(Activity paramActivity, AttributeSet paramAttributeSet, Bundle paramBundle)
  {
    super.onInflate(paramActivity, paramAttributeSet, paramBundle);
    zzb.zza(this.zzbmp, paramActivity);
    paramAttributeSet = GoogleMapOptions.createFromAttributes(paramActivity, paramAttributeSet);
    Bundle localBundle = new Bundle();
    localBundle.putParcelable("MapOptions", paramAttributeSet);
    this.zzbmp.onInflate(paramActivity, localBundle, paramBundle);
  }
  
  public void onLowMemory()
  {
    this.zzbmp.onLowMemory();
    super.onLowMemory();
  }
  
  public void onPause()
  {
    this.zzbmp.onPause();
    super.onPause();
  }
  
  public void onResume()
  {
    super.onResume();
    this.zzbmp.onResume();
  }
  
  public void onSaveInstanceState(Bundle paramBundle)
  {
    if (paramBundle != null) {
      paramBundle.setClassLoader(MapFragment.class.getClassLoader());
    }
    super.onSaveInstanceState(paramBundle);
    this.zzbmp.onSaveInstanceState(paramBundle);
  }
  
  public void onStart()
  {
    super.onStart();
    this.zzbmp.onStart();
  }
  
  public void onStop()
  {
    this.zzbmp.onStop();
    super.onStop();
  }
  
  public void setArguments(Bundle paramBundle)
  {
    super.setArguments(paramBundle);
  }
  
  static final class zza
    implements MapLifecycleDelegate
  {
    private final Fragment zzaSB;
    private final IMapFragmentDelegate zzbmq;
    
    public zza(Fragment paramFragment, IMapFragmentDelegate paramIMapFragmentDelegate)
    {
      this.zzbmq = ((IMapFragmentDelegate)zzbo.zzu(paramIMapFragmentDelegate));
      this.zzaSB = ((Fragment)zzbo.zzu(paramFragment));
    }
    
    public final void getMapAsync(OnMapReadyCallback paramOnMapReadyCallback)
    {
      try
      {
        this.zzbmq.getMapAsync(new zzaa(this, paramOnMapReadyCallback));
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
        Bundle localBundle1 = new Bundle();
        zzbw.zzd(paramBundle, localBundle1);
        Bundle localBundle2 = this.zzaSB.getArguments();
        if ((localBundle2 != null) && (localBundle2.containsKey("MapOptions"))) {
          zzbw.zza(localBundle1, "MapOptions", localBundle2.getParcelable("MapOptions"));
        }
        this.zzbmq.onCreate(localBundle1);
        zzbw.zzd(localBundle1, paramBundle);
        return;
      }
      catch (RemoteException paramBundle)
      {
        throw new RuntimeRemoteException(paramBundle);
      }
    }
    
    public final View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
    {
      try
      {
        Bundle localBundle = new Bundle();
        zzbw.zzd(paramBundle, localBundle);
        paramLayoutInflater = this.zzbmq.onCreateView(zzn.zzw(paramLayoutInflater), zzn.zzw(paramViewGroup), localBundle);
        zzbw.zzd(localBundle, paramBundle);
        return (View)zzn.zzE(paramLayoutInflater);
      }
      catch (RemoteException paramLayoutInflater)
      {
        throw new RuntimeRemoteException(paramLayoutInflater);
      }
    }
    
    public final void onDestroy()
    {
      try
      {
        this.zzbmq.onDestroy();
        return;
      }
      catch (RemoteException localRemoteException)
      {
        throw new RuntimeRemoteException(localRemoteException);
      }
    }
    
    public final void onDestroyView()
    {
      try
      {
        this.zzbmq.onDestroyView();
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
        Bundle localBundle = new Bundle();
        zzbw.zzd(paramBundle, localBundle);
        this.zzbmq.onEnterAmbient(localBundle);
        zzbw.zzd(localBundle, paramBundle);
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
        this.zzbmq.onExitAmbient();
        return;
      }
      catch (RemoteException localRemoteException)
      {
        throw new RuntimeRemoteException(localRemoteException);
      }
    }
    
    public final void onInflate(Activity paramActivity, Bundle paramBundle1, Bundle paramBundle2)
    {
      paramBundle1 = (GoogleMapOptions)paramBundle1.getParcelable("MapOptions");
      try
      {
        Bundle localBundle = new Bundle();
        zzbw.zzd(paramBundle2, localBundle);
        this.zzbmq.onInflate(zzn.zzw(paramActivity), paramBundle1, localBundle);
        zzbw.zzd(localBundle, paramBundle2);
        return;
      }
      catch (RemoteException paramActivity)
      {
        throw new RuntimeRemoteException(paramActivity);
      }
    }
    
    public final void onLowMemory()
    {
      try
      {
        this.zzbmq.onLowMemory();
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
        this.zzbmq.onPause();
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
        this.zzbmq.onResume();
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
        Bundle localBundle = new Bundle();
        zzbw.zzd(paramBundle, localBundle);
        this.zzbmq.onSaveInstanceState(localBundle);
        zzbw.zzd(localBundle, paramBundle);
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
        this.zzbmq.onStart();
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
        this.zzbmq.onStop();
        return;
      }
      catch (RemoteException localRemoteException)
      {
        throw new RuntimeRemoteException(localRemoteException);
      }
    }
  }
  
  static final class zzb
    extends zza<MapFragment.zza>
  {
    private Activity mActivity;
    private final Fragment zzaSB;
    private zzo<MapFragment.zza> zzbms;
    private final List<OnMapReadyCallback> zzbmt = new ArrayList();
    
    zzb(Fragment paramFragment)
    {
      this.zzaSB = paramFragment;
    }
    
    private final void setActivity(Activity paramActivity)
    {
      this.mActivity = paramActivity;
      zzwg();
    }
    
    private final void zzwg()
    {
      if ((this.mActivity != null) && (this.zzbms != null) && (zztx() == null)) {
        try
        {
          MapsInitializer.initialize(this.mActivity);
          Object localObject = zzbx.zzbh(this.mActivity).zzH(zzn.zzw(this.mActivity));
          if (localObject == null) {
            return;
          }
          this.zzbms.zza(new MapFragment.zza(this.zzaSB, (IMapFragmentDelegate)localObject));
          localObject = this.zzbmt.iterator();
          while (((Iterator)localObject).hasNext())
          {
            OnMapReadyCallback localOnMapReadyCallback = (OnMapReadyCallback)((Iterator)localObject).next();
            ((MapFragment.zza)zztx()).getMapAsync(localOnMapReadyCallback);
          }
          return;
        }
        catch (RemoteException localRemoteException)
        {
          throw new RuntimeRemoteException(localRemoteException);
          this.zzbmt.clear();
          return;
        }
        catch (GooglePlayServicesNotAvailableException localGooglePlayServicesNotAvailableException) {}
      }
    }
    
    public final void getMapAsync(OnMapReadyCallback paramOnMapReadyCallback)
    {
      if (zztx() != null)
      {
        ((MapFragment.zza)zztx()).getMapAsync(paramOnMapReadyCallback);
        return;
      }
      this.zzbmt.add(paramOnMapReadyCallback);
    }
    
    protected final void zza(zzo<MapFragment.zza> paramzzo)
    {
      this.zzbms = paramzzo;
      zzwg();
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/maps/MapFragment.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */