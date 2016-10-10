package com.google.android.gms.dynamic;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

public final class zzh
  extends zzc.zza
{
  private Fragment Ov;
  
  private zzh(Fragment paramFragment)
  {
    this.Ov = paramFragment;
  }
  
  public static zzh zza(Fragment paramFragment)
  {
    if (paramFragment != null) {
      return new zzh(paramFragment);
    }
    return null;
  }
  
  public Bundle getArguments()
  {
    return this.Ov.getArguments();
  }
  
  public int getId()
  {
    return this.Ov.getId();
  }
  
  public boolean getRetainInstance()
  {
    return this.Ov.getRetainInstance();
  }
  
  public String getTag()
  {
    return this.Ov.getTag();
  }
  
  public int getTargetRequestCode()
  {
    return this.Ov.getTargetRequestCode();
  }
  
  public boolean getUserVisibleHint()
  {
    return this.Ov.getUserVisibleHint();
  }
  
  public zzd getView()
  {
    return zze.zzac(this.Ov.getView());
  }
  
  public boolean isAdded()
  {
    return this.Ov.isAdded();
  }
  
  public boolean isDetached()
  {
    return this.Ov.isDetached();
  }
  
  public boolean isHidden()
  {
    return this.Ov.isHidden();
  }
  
  public boolean isInLayout()
  {
    return this.Ov.isInLayout();
  }
  
  public boolean isRemoving()
  {
    return this.Ov.isRemoving();
  }
  
  public boolean isResumed()
  {
    return this.Ov.isResumed();
  }
  
  public boolean isVisible()
  {
    return this.Ov.isVisible();
  }
  
  public void setHasOptionsMenu(boolean paramBoolean)
  {
    this.Ov.setHasOptionsMenu(paramBoolean);
  }
  
  public void setMenuVisibility(boolean paramBoolean)
  {
    this.Ov.setMenuVisibility(paramBoolean);
  }
  
  public void setRetainInstance(boolean paramBoolean)
  {
    this.Ov.setRetainInstance(paramBoolean);
  }
  
  public void setUserVisibleHint(boolean paramBoolean)
  {
    this.Ov.setUserVisibleHint(paramBoolean);
  }
  
  public void startActivity(Intent paramIntent)
  {
    this.Ov.startActivity(paramIntent);
  }
  
  public void startActivityForResult(Intent paramIntent, int paramInt)
  {
    this.Ov.startActivityForResult(paramIntent, paramInt);
  }
  
  public void zzac(zzd paramzzd)
  {
    paramzzd = (View)zze.zzae(paramzzd);
    this.Ov.registerForContextMenu(paramzzd);
  }
  
  public void zzad(zzd paramzzd)
  {
    paramzzd = (View)zze.zzae(paramzzd);
    this.Ov.unregisterForContextMenu(paramzzd);
  }
  
  public zzd zzbdu()
  {
    return zze.zzac(this.Ov.getActivity());
  }
  
  public zzc zzbdv()
  {
    return zza(this.Ov.getParentFragment());
  }
  
  public zzd zzbdw()
  {
    return zze.zzac(this.Ov.getResources());
  }
  
  public zzc zzbdx()
  {
    return zza(this.Ov.getTargetFragment());
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/dynamic/zzh.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */