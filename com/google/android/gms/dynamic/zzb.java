package com.google.android.gms.dynamic;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.google.android.gms.dynamic.zzc.zza;

@SuppressLint({"NewApi"})
public final class zzb extends zza {
    private Fragment zzaRK;

    private zzb(Fragment fragment) {
        this.zzaRK = fragment;
    }

    public static zzb zza(Fragment fragment) {
        return fragment != null ? new zzb(fragment) : null;
    }

    public Bundle getArguments() {
        return this.zzaRK.getArguments();
    }

    public int getId() {
        return this.zzaRK.getId();
    }

    public boolean getRetainInstance() {
        return this.zzaRK.getRetainInstance();
    }

    public String getTag() {
        return this.zzaRK.getTag();
    }

    public int getTargetRequestCode() {
        return this.zzaRK.getTargetRequestCode();
    }

    public boolean getUserVisibleHint() {
        return this.zzaRK.getUserVisibleHint();
    }

    public IObjectWrapper getView() {
        return zzd.zzA(this.zzaRK.getView());
    }

    public boolean isAdded() {
        return this.zzaRK.isAdded();
    }

    public boolean isDetached() {
        return this.zzaRK.isDetached();
    }

    public boolean isHidden() {
        return this.zzaRK.isHidden();
    }

    public boolean isInLayout() {
        return this.zzaRK.isInLayout();
    }

    public boolean isRemoving() {
        return this.zzaRK.isRemoving();
    }

    public boolean isResumed() {
        return this.zzaRK.isResumed();
    }

    public boolean isVisible() {
        return this.zzaRK.isVisible();
    }

    public void setHasOptionsMenu(boolean z) {
        this.zzaRK.setHasOptionsMenu(z);
    }

    public void setMenuVisibility(boolean z) {
        this.zzaRK.setMenuVisibility(z);
    }

    public void setRetainInstance(boolean z) {
        this.zzaRK.setRetainInstance(z);
    }

    public void setUserVisibleHint(boolean z) {
        this.zzaRK.setUserVisibleHint(z);
    }

    public void startActivity(Intent intent) {
        this.zzaRK.startActivity(intent);
    }

    public void startActivityForResult(Intent intent, int i) {
        this.zzaRK.startActivityForResult(intent, i);
    }

    public IObjectWrapper zzBO() {
        return zzd.zzA(this.zzaRK.getActivity());
    }

    public zzc zzBP() {
        return zza(this.zzaRK.getParentFragment());
    }

    public IObjectWrapper zzBQ() {
        return zzd.zzA(this.zzaRK.getResources());
    }

    public zzc zzBR() {
        return zza(this.zzaRK.getTargetFragment());
    }

    public void zzD(IObjectWrapper iObjectWrapper) {
        this.zzaRK.registerForContextMenu((View) zzd.zzF(iObjectWrapper));
    }

    public void zzE(IObjectWrapper iObjectWrapper) {
        this.zzaRK.unregisterForContextMenu((View) zzd.zzF(iObjectWrapper));
    }
}
