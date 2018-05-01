package com.google.android.gms.maps;

import android.app.Activity;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
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

public class SupportMapFragment
  extends Fragment
{
  private final zzb zzbmW = new zzb(this);
  
  public static SupportMapFragment newInstance()
  {
    return new SupportMapFragment();
  }
  
  public static SupportMapFragment newInstance(GoogleMapOptions paramGoogleMapOptions)
  {
    SupportMapFragment localSupportMapFragment = new SupportMapFragment();
    Bundle localBundle = new Bundle();
    localBundle.putParcelable("MapOptions", paramGoogleMapOptions);
    localSupportMapFragment.setArguments(localBundle);
    return localSupportMapFragment;
  }
  
  public void getMapAsync(OnMapReadyCallback paramOnMapReadyCallback)
  {
    zzbo.zzcz("getMapAsync must be called on the main thread.");
    this.zzbmW.getMapAsync(paramOnMapReadyCallback);
  }
  
  public void onActivityCreated(Bundle paramBundle)
  {
    if (paramBundle != null) {
      paramBundle.setClassLoader(SupportMapFragment.class.getClassLoader());
    }
    super.onActivityCreated(paramBundle);
  }
  
  public void onAttach(Activity paramActivity)
  {
    super.onAttach(paramActivity);
    zzb.zza(this.zzbmW, paramActivity);
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    this.zzbmW.onCreate(paramBundle);
  }
  
  public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
  {
    paramLayoutInflater = this.zzbmW.onCreateView(paramLayoutInflater, paramViewGroup, paramBundle);
    paramLayoutInflater.setClickable(true);
    return paramLayoutInflater;
  }
  
  public void onDestroy()
  {
    this.zzbmW.onDestroy();
    super.onDestroy();
  }
  
  public void onDestroyView()
  {
    this.zzbmW.onDestroyView();
    super.onDestroyView();
  }
  
  public final void onEnterAmbient(Bundle paramBundle)
  {
    zzbo.zzcz("onEnterAmbient must be called on the main thread.");
    zzb localzzb = this.zzbmW;
    if (localzzb.zztx() != null) {
      ((zza)localzzb.zztx()).onEnterAmbient(paramBundle);
    }
  }
  
  public final void onExitAmbient()
  {
    zzbo.zzcz("onExitAmbient must be called on the main thread.");
    zzb localzzb = this.zzbmW;
    if (localzzb.zztx() != null) {
      ((zza)localzzb.zztx()).onExitAmbient();
    }
  }
  
  public void onInflate(Activity paramActivity, AttributeSet paramAttributeSet, Bundle paramBundle)
  {
    super.onInflate(paramActivity, paramAttributeSet, paramBundle);
    zzb.zza(this.zzbmW, paramActivity);
    paramAttributeSet = GoogleMapOptions.createFromAttributes(paramActivity, paramAttributeSet);
    Bundle localBundle = new Bundle();
    localBundle.putParcelable("MapOptions", paramAttributeSet);
    this.zzbmW.onInflate(paramActivity, localBundle, paramBundle);
  }
  
  public void onLowMemory()
  {
    this.zzbmW.onLowMemory();
    super.onLowMemory();
  }
  
  public void onPause()
  {
    this.zzbmW.onPause();
    super.onPause();
  }
  
  public void onResume()
  {
    super.onResume();
    this.zzbmW.onResume();
  }
  
  public void onSaveInstanceState(Bundle paramBundle)
  {
    if (paramBundle != null) {
      paramBundle.setClassLoader(SupportMapFragment.class.getClassLoader());
    }
    super.onSaveInstanceState(paramBundle);
    this.zzbmW.onSaveInstanceState(paramBundle);
  }
  
  public void onStart()
  {
    super.onStart();
    this.zzbmW.onStart();
  }
  
  public void onStop()
  {
    this.zzbmW.onStop();
    super.onStop();
  }
  
  public void setArguments(Bundle paramBundle)
  {
    super.setArguments(paramBundle);
  }
  
  static final class zza
    implements MapLifecycleDelegate
  {
    private final Fragment zzaSE;
    private final IMapFragmentDelegate zzbmq;
    
    public zza(Fragment paramFragment, IMapFragmentDelegate paramIMapFragmentDelegate)
    {
      this.zzbmq = ((IMapFragmentDelegate)zzbo.zzu(paramIMapFragmentDelegate));
      this.zzaSE = ((Fragment)zzbo.zzu(paramFragment));
    }
    
    public final void getMapAsync(OnMapReadyCallback paramOnMapReadyCallback)
    {
      try
      {
        this.zzbmq.getMapAsync(new zzaj(this, paramOnMapReadyCallback));
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
        Bundle localBundle2 = this.zzaSE.getArguments();
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
    extends zza<SupportMapFragment.zza>
  {
    private Activity mActivity;
    private final Fragment zzaSE;
    private zzo<SupportMapFragment.zza> zzbms;
    private final List<OnMapReadyCallback> zzbmt = new ArrayList();
    
    zzb(Fragment paramFragment)
    {
      this.zzaSE = paramFragment;
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
          this.zzbms.zza(new SupportMapFragment.zza(this.zzaSE, (IMapFragmentDelegate)localObject));
          localObject = this.zzbmt.iterator();
          while (((Iterator)localObject).hasNext())
          {
            OnMapReadyCallback localOnMapReadyCallback = (OnMapReadyCallback)((Iterator)localObject).next();
            ((SupportMapFragment.zza)zztx()).getMapAsync(localOnMapReadyCallback);
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
        ((SupportMapFragment.zza)zztx()).getMapAsync(paramOnMapReadyCallback);
        return;
      }
      this.zzbmt.add(paramOnMapReadyCallback);
    }
    
    protected final void zza(zzo<SupportMapFragment.zza> paramzzo)
    {
      this.zzbms = paramzzo;
      zzwg();
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/maps/SupportMapFragment.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */