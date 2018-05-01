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
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.dynamic.zza;
import com.google.android.gms.dynamic.zzn;
import com.google.android.gms.dynamic.zzo;
import com.google.android.gms.maps.internal.IStreetViewPanoramaViewDelegate;
import com.google.android.gms.maps.internal.StreetViewLifecycleDelegate;
import com.google.android.gms.maps.internal.zzbw;
import com.google.android.gms.maps.internal.zzbx;
import com.google.android.gms.maps.internal.zze;
import com.google.android.gms.maps.model.RuntimeRemoteException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class StreetViewPanoramaView
  extends FrameLayout
{
  private final zzb zzbmS;
  
  public StreetViewPanoramaView(Context paramContext)
  {
    super(paramContext);
    this.zzbmS = new zzb(this, paramContext, null);
  }
  
  public StreetViewPanoramaView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    this.zzbmS = new zzb(this, paramContext, null);
  }
  
  public StreetViewPanoramaView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    this.zzbmS = new zzb(this, paramContext, null);
  }
  
  public StreetViewPanoramaView(Context paramContext, StreetViewPanoramaOptions paramStreetViewPanoramaOptions)
  {
    super(paramContext);
    this.zzbmS = new zzb(this, paramContext, paramStreetViewPanoramaOptions);
  }
  
  public void getStreetViewPanoramaAsync(OnStreetViewPanoramaReadyCallback paramOnStreetViewPanoramaReadyCallback)
  {
    zzbo.zzcz("getStreetViewPanoramaAsync() must be called on the main thread");
    this.zzbmS.getStreetViewPanoramaAsync(paramOnStreetViewPanoramaReadyCallback);
  }
  
  public final void onCreate(Bundle paramBundle)
  {
    this.zzbmS.onCreate(paramBundle);
    if (this.zzbmS.zztx() == null) {
      zza.zzb(this);
    }
  }
  
  public final void onDestroy()
  {
    this.zzbmS.onDestroy();
  }
  
  public final void onLowMemory()
  {
    this.zzbmS.onLowMemory();
  }
  
  public final void onPause()
  {
    this.zzbmS.onPause();
  }
  
  public final void onResume()
  {
    this.zzbmS.onResume();
  }
  
  public final void onSaveInstanceState(Bundle paramBundle)
  {
    this.zzbmS.onSaveInstanceState(paramBundle);
  }
  
  static final class zza
    implements StreetViewLifecycleDelegate
  {
    private final IStreetViewPanoramaViewDelegate zzbmT;
    private View zzbmU;
    private final ViewGroup zzbmv;
    
    public zza(ViewGroup paramViewGroup, IStreetViewPanoramaViewDelegate paramIStreetViewPanoramaViewDelegate)
    {
      this.zzbmT = ((IStreetViewPanoramaViewDelegate)zzbo.zzu(paramIStreetViewPanoramaViewDelegate));
      this.zzbmv = ((ViewGroup)zzbo.zzu(paramViewGroup));
    }
    
    public final void getStreetViewPanoramaAsync(OnStreetViewPanoramaReadyCallback paramOnStreetViewPanoramaReadyCallback)
    {
      try
      {
        this.zzbmT.getStreetViewPanoramaAsync(new zzai(this, paramOnStreetViewPanoramaReadyCallback));
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
        Bundle localBundle = new Bundle();
        zzbw.zzd(paramBundle, localBundle);
        this.zzbmT.onCreate(localBundle);
        zzbw.zzd(localBundle, paramBundle);
        this.zzbmU = ((View)zzn.zzE(this.zzbmT.getView()));
        this.zzbmv.removeAllViews();
        this.zzbmv.addView(this.zzbmU);
        return;
      }
      catch (RemoteException paramBundle)
      {
        throw new RuntimeRemoteException(paramBundle);
      }
    }
    
    public final View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
    {
      throw new UnsupportedOperationException("onCreateView not allowed on StreetViewPanoramaViewDelegate");
    }
    
    public final void onDestroy()
    {
      try
      {
        this.zzbmT.onDestroy();
        return;
      }
      catch (RemoteException localRemoteException)
      {
        throw new RuntimeRemoteException(localRemoteException);
      }
    }
    
    public final void onDestroyView()
    {
      throw new UnsupportedOperationException("onDestroyView not allowed on StreetViewPanoramaViewDelegate");
    }
    
    public final void onInflate(Activity paramActivity, Bundle paramBundle1, Bundle paramBundle2)
    {
      throw new UnsupportedOperationException("onInflate not allowed on StreetViewPanoramaViewDelegate");
    }
    
    public final void onLowMemory()
    {
      try
      {
        this.zzbmT.onLowMemory();
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
        this.zzbmT.onPause();
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
        this.zzbmT.onResume();
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
        this.zzbmT.onSaveInstanceState(localBundle);
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
    extends zza<StreetViewPanoramaView.zza>
  {
    private final List<OnStreetViewPanoramaReadyCallback> zzbmK = new ArrayList();
    private final StreetViewPanoramaOptions zzbmV;
    private zzo<StreetViewPanoramaView.zza> zzbms;
    private final ViewGroup zzbmy;
    private final Context zzbmz;
    
    zzb(ViewGroup paramViewGroup, Context paramContext, StreetViewPanoramaOptions paramStreetViewPanoramaOptions)
    {
      this.zzbmy = paramViewGroup;
      this.zzbmz = paramContext;
      this.zzbmV = paramStreetViewPanoramaOptions;
    }
    
    public final void getStreetViewPanoramaAsync(OnStreetViewPanoramaReadyCallback paramOnStreetViewPanoramaReadyCallback)
    {
      if (zztx() != null)
      {
        ((StreetViewPanoramaView.zza)zztx()).getStreetViewPanoramaAsync(paramOnStreetViewPanoramaReadyCallback);
        return;
      }
      this.zzbmK.add(paramOnStreetViewPanoramaReadyCallback);
    }
    
    protected final void zza(zzo<StreetViewPanoramaView.zza> paramzzo)
    {
      this.zzbms = paramzzo;
      if ((this.zzbms != null) && (zztx() == null)) {}
      try
      {
        paramzzo = zzbx.zzbh(this.zzbmz).zza(zzn.zzw(this.zzbmz), this.zzbmV);
        this.zzbms.zza(new StreetViewPanoramaView.zza(this.zzbmy, paramzzo));
        paramzzo = this.zzbmK.iterator();
        while (paramzzo.hasNext())
        {
          OnStreetViewPanoramaReadyCallback localOnStreetViewPanoramaReadyCallback = (OnStreetViewPanoramaReadyCallback)paramzzo.next();
          ((StreetViewPanoramaView.zza)zztx()).getStreetViewPanoramaAsync(localOnStreetViewPanoramaReadyCallback);
        }
        return;
      }
      catch (RemoteException paramzzo)
      {
        throw new RuntimeRemoteException(paramzzo);
        this.zzbmK.clear();
        return;
      }
      catch (GooglePlayServicesNotAvailableException paramzzo) {}
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/maps/StreetViewPanoramaView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */