package com.google.android.gms.dynamic;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

public final class zzr
  extends zzl
{
  private Fragment zzaSE;
  
  private zzr(Fragment paramFragment)
  {
    this.zzaSE = paramFragment;
  }
  
  public static zzr zza(Fragment paramFragment)
  {
    if (paramFragment != null) {
      return new zzr(paramFragment);
    }
    return null;
  }
  
  public final Bundle getArguments()
  {
    return this.zzaSE.getArguments();
  }
  
  public final int getId()
  {
    return this.zzaSE.getId();
  }
  
  public final boolean getRetainInstance()
  {
    return this.zzaSE.getRetainInstance();
  }
  
  public final String getTag()
  {
    return this.zzaSE.getTag();
  }
  
  public final int getTargetRequestCode()
  {
    return this.zzaSE.getTargetRequestCode();
  }
  
  public final boolean getUserVisibleHint()
  {
    return this.zzaSE.getUserVisibleHint();
  }
  
  public final IObjectWrapper getView()
  {
    return zzn.zzw(this.zzaSE.getView());
  }
  
  public final boolean isAdded()
  {
    return this.zzaSE.isAdded();
  }
  
  public final boolean isDetached()
  {
    return this.zzaSE.isDetached();
  }
  
  public final boolean isHidden()
  {
    return this.zzaSE.isHidden();
  }
  
  public final boolean isInLayout()
  {
    return this.zzaSE.isInLayout();
  }
  
  public final boolean isRemoving()
  {
    return this.zzaSE.isRemoving();
  }
  
  public final boolean isResumed()
  {
    return this.zzaSE.isResumed();
  }
  
  public final boolean isVisible()
  {
    return this.zzaSE.isVisible();
  }
  
  public final void setHasOptionsMenu(boolean paramBoolean)
  {
    this.zzaSE.setHasOptionsMenu(paramBoolean);
  }
  
  public final void setMenuVisibility(boolean paramBoolean)
  {
    this.zzaSE.setMenuVisibility(paramBoolean);
  }
  
  public final void setRetainInstance(boolean paramBoolean)
  {
    this.zzaSE.setRetainInstance(paramBoolean);
  }
  
  public final void setUserVisibleHint(boolean paramBoolean)
  {
    this.zzaSE.setUserVisibleHint(paramBoolean);
  }
  
  public final void startActivity(Intent paramIntent)
  {
    this.zzaSE.startActivity(paramIntent);
  }
  
  public final void startActivityForResult(Intent paramIntent, int paramInt)
  {
    this.zzaSE.startActivityForResult(paramIntent, paramInt);
  }
  
  public final void zzC(IObjectWrapper paramIObjectWrapper)
  {
    paramIObjectWrapper = (View)zzn.zzE(paramIObjectWrapper);
    this.zzaSE.registerForContextMenu(paramIObjectWrapper);
  }
  
  public final void zzD(IObjectWrapper paramIObjectWrapper)
  {
    paramIObjectWrapper = (View)zzn.zzE(paramIObjectWrapper);
    this.zzaSE.unregisterForContextMenu(paramIObjectWrapper);
  }
  
  public final IObjectWrapper zztA()
  {
    return zzn.zzw(this.zzaSE.getResources());
  }
  
  public final zzk zztB()
  {
    return zza(this.zzaSE.getTargetFragment());
  }
  
  public final IObjectWrapper zzty()
  {
    return zzn.zzw(this.zzaSE.getActivity());
  }
  
  public final zzk zztz()
  {
    return zza(this.zzaSE.getParentFragment());
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/dynamic/zzr.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */