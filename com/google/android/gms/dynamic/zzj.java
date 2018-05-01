package com.google.android.gms.dynamic;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

@SuppressLint({"NewApi"})
public final class zzj
  extends zzl
{
  private Fragment zzgwm;
  
  private zzj(Fragment paramFragment)
  {
    this.zzgwm = paramFragment;
  }
  
  public static zzj zza(Fragment paramFragment)
  {
    if (paramFragment != null) {
      return new zzj(paramFragment);
    }
    return null;
  }
  
  public final Bundle getArguments()
  {
    return this.zzgwm.getArguments();
  }
  
  public final int getId()
  {
    return this.zzgwm.getId();
  }
  
  public final boolean getRetainInstance()
  {
    return this.zzgwm.getRetainInstance();
  }
  
  public final String getTag()
  {
    return this.zzgwm.getTag();
  }
  
  public final int getTargetRequestCode()
  {
    return this.zzgwm.getTargetRequestCode();
  }
  
  public final boolean getUserVisibleHint()
  {
    return this.zzgwm.getUserVisibleHint();
  }
  
  public final IObjectWrapper getView()
  {
    return zzn.zzz(this.zzgwm.getView());
  }
  
  public final boolean isAdded()
  {
    return this.zzgwm.isAdded();
  }
  
  public final boolean isDetached()
  {
    return this.zzgwm.isDetached();
  }
  
  public final boolean isHidden()
  {
    return this.zzgwm.isHidden();
  }
  
  public final boolean isInLayout()
  {
    return this.zzgwm.isInLayout();
  }
  
  public final boolean isRemoving()
  {
    return this.zzgwm.isRemoving();
  }
  
  public final boolean isResumed()
  {
    return this.zzgwm.isResumed();
  }
  
  public final boolean isVisible()
  {
    return this.zzgwm.isVisible();
  }
  
  public final void setHasOptionsMenu(boolean paramBoolean)
  {
    this.zzgwm.setHasOptionsMenu(paramBoolean);
  }
  
  public final void setMenuVisibility(boolean paramBoolean)
  {
    this.zzgwm.setMenuVisibility(paramBoolean);
  }
  
  public final void setRetainInstance(boolean paramBoolean)
  {
    this.zzgwm.setRetainInstance(paramBoolean);
  }
  
  public final void setUserVisibleHint(boolean paramBoolean)
  {
    this.zzgwm.setUserVisibleHint(paramBoolean);
  }
  
  public final void startActivity(Intent paramIntent)
  {
    this.zzgwm.startActivity(paramIntent);
  }
  
  public final void startActivityForResult(Intent paramIntent, int paramInt)
  {
    this.zzgwm.startActivityForResult(paramIntent, paramInt);
  }
  
  public final IObjectWrapper zzapx()
  {
    return zzn.zzz(this.zzgwm.getActivity());
  }
  
  public final zzk zzapy()
  {
    return zza(this.zzgwm.getParentFragment());
  }
  
  public final IObjectWrapper zzapz()
  {
    return zzn.zzz(this.zzgwm.getResources());
  }
  
  public final zzk zzaqa()
  {
    return zza(this.zzgwm.getTargetFragment());
  }
  
  public final void zzv(IObjectWrapper paramIObjectWrapper)
  {
    paramIObjectWrapper = (View)zzn.zzx(paramIObjectWrapper);
    this.zzgwm.registerForContextMenu(paramIObjectWrapper);
  }
  
  public final void zzw(IObjectWrapper paramIObjectWrapper)
  {
    paramIObjectWrapper = (View)zzn.zzx(paramIObjectWrapper);
    this.zzgwm.unregisterForContextMenu(paramIObjectWrapper);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/dynamic/zzj.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */