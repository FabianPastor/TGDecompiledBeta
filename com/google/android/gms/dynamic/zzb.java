package com.google.android.gms.dynamic;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

@SuppressLint({"NewApi"})
public final class zzb
  extends zzc.zza
{
  private Fragment Os;
  
  private zzb(Fragment paramFragment)
  {
    this.Os = paramFragment;
  }
  
  public static zzb zza(Fragment paramFragment)
  {
    if (paramFragment != null) {
      return new zzb(paramFragment);
    }
    return null;
  }
  
  public Bundle getArguments()
  {
    return this.Os.getArguments();
  }
  
  public int getId()
  {
    return this.Os.getId();
  }
  
  public boolean getRetainInstance()
  {
    return this.Os.getRetainInstance();
  }
  
  public String getTag()
  {
    return this.Os.getTag();
  }
  
  public int getTargetRequestCode()
  {
    return this.Os.getTargetRequestCode();
  }
  
  public boolean getUserVisibleHint()
  {
    return this.Os.getUserVisibleHint();
  }
  
  public zzd getView()
  {
    return zze.zzac(this.Os.getView());
  }
  
  public boolean isAdded()
  {
    return this.Os.isAdded();
  }
  
  public boolean isDetached()
  {
    return this.Os.isDetached();
  }
  
  public boolean isHidden()
  {
    return this.Os.isHidden();
  }
  
  public boolean isInLayout()
  {
    return this.Os.isInLayout();
  }
  
  public boolean isRemoving()
  {
    return this.Os.isRemoving();
  }
  
  public boolean isResumed()
  {
    return this.Os.isResumed();
  }
  
  public boolean isVisible()
  {
    return this.Os.isVisible();
  }
  
  public void setHasOptionsMenu(boolean paramBoolean)
  {
    this.Os.setHasOptionsMenu(paramBoolean);
  }
  
  public void setMenuVisibility(boolean paramBoolean)
  {
    this.Os.setMenuVisibility(paramBoolean);
  }
  
  public void setRetainInstance(boolean paramBoolean)
  {
    this.Os.setRetainInstance(paramBoolean);
  }
  
  public void setUserVisibleHint(boolean paramBoolean)
  {
    this.Os.setUserVisibleHint(paramBoolean);
  }
  
  public void startActivity(Intent paramIntent)
  {
    this.Os.startActivity(paramIntent);
  }
  
  public void startActivityForResult(Intent paramIntent, int paramInt)
  {
    this.Os.startActivityForResult(paramIntent, paramInt);
  }
  
  public void zzac(zzd paramzzd)
  {
    paramzzd = (View)zze.zzae(paramzzd);
    this.Os.registerForContextMenu(paramzzd);
  }
  
  public void zzad(zzd paramzzd)
  {
    paramzzd = (View)zze.zzae(paramzzd);
    this.Os.unregisterForContextMenu(paramzzd);
  }
  
  public zzd zzbdu()
  {
    return zze.zzac(this.Os.getActivity());
  }
  
  public zzc zzbdv()
  {
    return zza(this.Os.getParentFragment());
  }
  
  public zzd zzbdw()
  {
    return zze.zzac(this.Os.getResources());
  }
  
  public zzc zzbdx()
  {
    return zza(this.Os.getTargetFragment());
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/dynamic/zzb.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */