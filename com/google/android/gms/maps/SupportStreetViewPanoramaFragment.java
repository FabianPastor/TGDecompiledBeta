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
import com.google.android.gms.maps.internal.IStreetViewPanoramaFragmentDelegate;
import com.google.android.gms.maps.internal.StreetViewLifecycleDelegate;
import com.google.android.gms.maps.internal.zzbw;
import com.google.android.gms.maps.internal.zzbx;
import com.google.android.gms.maps.internal.zze;
import com.google.android.gms.maps.model.RuntimeRemoteException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SupportStreetViewPanoramaFragment
  extends Fragment
{
  private final zzb zzbmX = new zzb(this);
  
  public static SupportStreetViewPanoramaFragment newInstance()
  {
    return new SupportStreetViewPanoramaFragment();
  }
  
  public static SupportStreetViewPanoramaFragment newInstance(StreetViewPanoramaOptions paramStreetViewPanoramaOptions)
  {
    SupportStreetViewPanoramaFragment localSupportStreetViewPanoramaFragment = new SupportStreetViewPanoramaFragment();
    Bundle localBundle = new Bundle();
    localBundle.putParcelable("StreetViewPanoramaOptions", paramStreetViewPanoramaOptions);
    localSupportStreetViewPanoramaFragment.setArguments(localBundle);
    return localSupportStreetViewPanoramaFragment;
  }
  
  public void getStreetViewPanoramaAsync(OnStreetViewPanoramaReadyCallback paramOnStreetViewPanoramaReadyCallback)
  {
    zzbo.zzcz("getStreetViewPanoramaAsync() must be called on the main thread");
    this.zzbmX.getStreetViewPanoramaAsync(paramOnStreetViewPanoramaReadyCallback);
  }
  
  public void onActivityCreated(Bundle paramBundle)
  {
    if (paramBundle != null) {
      paramBundle.setClassLoader(SupportStreetViewPanoramaFragment.class.getClassLoader());
    }
    super.onActivityCreated(paramBundle);
  }
  
  public void onAttach(Activity paramActivity)
  {
    super.onAttach(paramActivity);
    zzb.zza(this.zzbmX, paramActivity);
  }
  
  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    this.zzbmX.onCreate(paramBundle);
  }
  
  public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
  {
    return this.zzbmX.onCreateView(paramLayoutInflater, paramViewGroup, paramBundle);
  }
  
  public void onDestroy()
  {
    this.zzbmX.onDestroy();
    super.onDestroy();
  }
  
  public void onDestroyView()
  {
    this.zzbmX.onDestroyView();
    super.onDestroyView();
  }
  
  public void onInflate(Activity paramActivity, AttributeSet paramAttributeSet, Bundle paramBundle)
  {
    super.onInflate(paramActivity, paramAttributeSet, paramBundle);
    zzb.zza(this.zzbmX, paramActivity);
    paramAttributeSet = new Bundle();
    this.zzbmX.onInflate(paramActivity, paramAttributeSet, paramBundle);
  }
  
  public void onLowMemory()
  {
    this.zzbmX.onLowMemory();
    super.onLowMemory();
  }
  
  public void onPause()
  {
    this.zzbmX.onPause();
    super.onPause();
  }
  
  public void onResume()
  {
    super.onResume();
    this.zzbmX.onResume();
  }
  
  public void onSaveInstanceState(Bundle paramBundle)
  {
    if (paramBundle != null) {
      paramBundle.setClassLoader(SupportStreetViewPanoramaFragment.class.getClassLoader());
    }
    super.onSaveInstanceState(paramBundle);
    this.zzbmX.onSaveInstanceState(paramBundle);
  }
  
  public void setArguments(Bundle paramBundle)
  {
    super.setArguments(paramBundle);
  }
  
  static final class zza
    implements StreetViewLifecycleDelegate
  {
    private final Fragment zzaSE;
    private final IStreetViewPanoramaFragmentDelegate zzbmI;
    
    public zza(Fragment paramFragment, IStreetViewPanoramaFragmentDelegate paramIStreetViewPanoramaFragmentDelegate)
    {
      this.zzbmI = ((IStreetViewPanoramaFragmentDelegate)zzbo.zzu(paramIStreetViewPanoramaFragmentDelegate));
      this.zzaSE = ((Fragment)zzbo.zzu(paramFragment));
    }
    
    public final void getStreetViewPanoramaAsync(OnStreetViewPanoramaReadyCallback paramOnStreetViewPanoramaReadyCallback)
    {
      try
      {
        this.zzbmI.getStreetViewPanoramaAsync(new zzak(this, paramOnStreetViewPanoramaReadyCallback));
        return;
      }
      catch (RemoteException paramOnStreetViewPanoramaReadyCallback)
      {
        throw new RuntimeRemoteException(paramOnStreetViewPanoramaReadyCallback);
      }
    }
    
    public final void onCreate(Bundle paramBundle)
    {
      try
      {
        Bundle localBundle1 = new Bundle();
        zzbw.zzd(paramBundle, localBundle1);
        Bundle localBundle2 = this.zzaSE.getArguments();
        if ((localBundle2 != null) && (localBundle2.containsKey("StreetViewPanoramaOptions"))) {
          zzbw.zza(localBundle1, "StreetViewPanoramaOptions", localBundle2.getParcelable("StreetViewPanoramaOptions"));
        }
        this.zzbmI.onCreate(localBundle1);
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
        paramLayoutInflater = this.zzbmI.onCreateView(zzn.zzw(paramLayoutInflater), zzn.zzw(paramViewGroup), localBundle);
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
        this.zzbmI.onDestroy();
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
        this.zzbmI.onDestroyView();
        return;
      }
      catch (RemoteException localRemoteException)
      {
        throw new RuntimeRemoteException(localRemoteException);
      }
    }
    
    public final void onInflate(Activity paramActivity, Bundle paramBundle1, Bundle paramBundle2)
    {
      try
      {
        paramBundle1 = new Bundle();
        zzbw.zzd(paramBundle2, paramBundle1);
        this.zzbmI.onInflate(zzn.zzw(paramActivity), null, paramBundle1);
        zzbw.zzd(paramBundle1, paramBundle2);
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
        this.zzbmI.onLowMemory();
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
        this.zzbmI.onPause();
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
        this.zzbmI.onResume();
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
        this.zzbmI.onSaveInstanceState(localBundle);
        zzbw.zzd(localBundle, paramBundle);
        return;
      }
      catch (RemoteException paramBundle)
      {
        throw new RuntimeRemoteException(paramBundle);
      }
    }
    
    public final void onStart() {}
    
    public final void onStop() {}
  }
  
  static final class zzb
    extends zza<SupportStreetViewPanoramaFragment.zza>
  {
    private Activity mActivity;
    private final Fragment zzaSE;
    private final List<OnStreetViewPanoramaReadyCallback> zzbmK = new ArrayList();
    private zzo<SupportStreetViewPanoramaFragment.zza> zzbms;
    
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
      if ((this.mActivity != null) && (this.zzbms != null) && (zztx() == null)) {}
      try
      {
        MapsInitializer.initialize(this.mActivity);
        Object localObject = zzbx.zzbh(this.mActivity).zzI(zzn.zzw(this.mActivity));
        this.zzbms.zza(new SupportStreetViewPanoramaFragment.zza(this.zzaSE, (IStreetViewPanoramaFragmentDelegate)localObject));
        localObject = this.zzbmK.iterator();
        while (((Iterator)localObject).hasNext())
        {
          OnStreetViewPanoramaReadyCallback localOnStreetViewPanoramaReadyCallback = (OnStreetViewPanoramaReadyCallback)((Iterator)localObject).next();
          ((SupportStreetViewPanoramaFragment.zza)zztx()).getStreetViewPanoramaAsync(localOnStreetViewPanoramaReadyCallback);
        }
        return;
      }
      catch (RemoteException localRemoteException)
      {
        throw new RuntimeRemoteException(localRemoteException);
        this.zzbmK.clear();
        return;
      }
      catch (GooglePlayServicesNotAvailableException localGooglePlayServicesNotAvailableException) {}
    }
    
    public final void getStreetViewPanoramaAsync(OnStreetViewPanoramaReadyCallback paramOnStreetViewPanoramaReadyCallback)
    {
      if (zztx() != null)
      {
        ((SupportStreetViewPanoramaFragment.zza)zztx()).getStreetViewPanoramaAsync(paramOnStreetViewPanoramaReadyCallback);
        return;
      }
      this.zzbmK.add(paramOnStreetViewPanoramaReadyCallback);
    }
    
    protected final void zza(zzo<SupportStreetViewPanoramaFragment.zza> paramzzo)
    {
      this.zzbms = paramzzo;
      zzwg();
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/maps/SupportStreetViewPanoramaFragment.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */