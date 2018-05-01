package com.google.android.gms.dynamic;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

@SuppressLint({"NewApi"})
public final class FragmentWrapper
  extends IFragmentWrapper.Stub
{
  private Fragment zzabm;
  
  private FragmentWrapper(Fragment paramFragment)
  {
    this.zzabm = paramFragment;
  }
  
  public static FragmentWrapper wrap(Fragment paramFragment)
  {
    if (paramFragment != null) {}
    for (paramFragment = new FragmentWrapper(paramFragment);; paramFragment = null) {
      return paramFragment;
    }
  }
  
  public final IObjectWrapper getActivity()
  {
    return ObjectWrapper.wrap(this.zzabm.getActivity());
  }
  
  public final Bundle getArguments()
  {
    return this.zzabm.getArguments();
  }
  
  public final int getId()
  {
    return this.zzabm.getId();
  }
  
  public final IFragmentWrapper getParentFragment()
  {
    return wrap(this.zzabm.getParentFragment());
  }
  
  public final IObjectWrapper getResources()
  {
    return ObjectWrapper.wrap(this.zzabm.getResources());
  }
  
  public final boolean getRetainInstance()
  {
    return this.zzabm.getRetainInstance();
  }
  
  public final String getTag()
  {
    return this.zzabm.getTag();
  }
  
  public final IFragmentWrapper getTargetFragment()
  {
    return wrap(this.zzabm.getTargetFragment());
  }
  
  public final int getTargetRequestCode()
  {
    return this.zzabm.getTargetRequestCode();
  }
  
  public final boolean getUserVisibleHint()
  {
    return this.zzabm.getUserVisibleHint();
  }
  
  public final IObjectWrapper getView()
  {
    return ObjectWrapper.wrap(this.zzabm.getView());
  }
  
  public final boolean isAdded()
  {
    return this.zzabm.isAdded();
  }
  
  public final boolean isDetached()
  {
    return this.zzabm.isDetached();
  }
  
  public final boolean isHidden()
  {
    return this.zzabm.isHidden();
  }
  
  public final boolean isInLayout()
  {
    return this.zzabm.isInLayout();
  }
  
  public final boolean isRemoving()
  {
    return this.zzabm.isRemoving();
  }
  
  public final boolean isResumed()
  {
    return this.zzabm.isResumed();
  }
  
  public final boolean isVisible()
  {
    return this.zzabm.isVisible();
  }
  
  public final void registerForContextMenu(IObjectWrapper paramIObjectWrapper)
  {
    paramIObjectWrapper = (View)ObjectWrapper.unwrap(paramIObjectWrapper);
    this.zzabm.registerForContextMenu(paramIObjectWrapper);
  }
  
  public final void setHasOptionsMenu(boolean paramBoolean)
  {
    this.zzabm.setHasOptionsMenu(paramBoolean);
  }
  
  public final void setMenuVisibility(boolean paramBoolean)
  {
    this.zzabm.setMenuVisibility(paramBoolean);
  }
  
  public final void setRetainInstance(boolean paramBoolean)
  {
    this.zzabm.setRetainInstance(paramBoolean);
  }
  
  public final void setUserVisibleHint(boolean paramBoolean)
  {
    this.zzabm.setUserVisibleHint(paramBoolean);
  }
  
  public final void startActivity(Intent paramIntent)
  {
    this.zzabm.startActivity(paramIntent);
  }
  
  public final void startActivityForResult(Intent paramIntent, int paramInt)
  {
    this.zzabm.startActivityForResult(paramIntent, paramInt);
  }
  
  public final void unregisterForContextMenu(IObjectWrapper paramIObjectWrapper)
  {
    paramIObjectWrapper = (View)ObjectWrapper.unwrap(paramIObjectWrapper);
    this.zzabm.unregisterForContextMenu(paramIObjectWrapper);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/dynamic/FragmentWrapper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */