package com.google.android.gms.dynamic;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import com.google.android.gms.dynamic.zzc.zza;

public final class zzg extends zza {
    private Fragment zzaRN;

    private zzg(Fragment fragment) {
        this.zzaRN = fragment;
    }

    public static zzg zza(Fragment fragment) {
        return fragment != null ? new zzg(fragment) : null;
    }

    public Bundle getArguments() {
        return this.zzaRN.getArguments();
    }

    public int getId() {
        return this.zzaRN.getId();
    }

    public boolean getRetainInstance() {
        return this.zzaRN.getRetainInstance();
    }

    public String getTag() {
        return this.zzaRN.getTag();
    }

    public int getTargetRequestCode() {
        return this.zzaRN.getTargetRequestCode();
    }

    public boolean getUserVisibleHint() {
        return this.zzaRN.getUserVisibleHint();
    }

    public IObjectWrapper getView() {
        return zzd.zzA(this.zzaRN.getView());
    }

    public boolean isAdded() {
        return this.zzaRN.isAdded();
    }

    public boolean isDetached() {
        return this.zzaRN.isDetached();
    }

    public boolean isHidden() {
        return this.zzaRN.isHidden();
    }

    public boolean isInLayout() {
        return this.zzaRN.isInLayout();
    }

    public boolean isRemoving() {
        return this.zzaRN.isRemoving();
    }

    public boolean isResumed() {
        return this.zzaRN.isResumed();
    }

    public boolean isVisible() {
        return this.zzaRN.isVisible();
    }

    public void setHasOptionsMenu(boolean z) {
        this.zzaRN.setHasOptionsMenu(z);
    }

    public void setMenuVisibility(boolean z) {
        this.zzaRN.setMenuVisibility(z);
    }

    public void setRetainInstance(boolean z) {
        this.zzaRN.setRetainInstance(z);
    }

    public void setUserVisibleHint(boolean z) {
        this.zzaRN.setUserVisibleHint(z);
    }

    public void startActivity(Intent intent) {
        this.zzaRN.startActivity(intent);
    }

    public void startActivityForResult(Intent intent, int i) {
        this.zzaRN.startActivityForResult(intent, i);
    }

    public IObjectWrapper zzBO() {
        return zzd.zzA(this.zzaRN.getActivity());
    }

    public zzc zzBP() {
        return zza(this.zzaRN.getParentFragment());
    }

    public IObjectWrapper zzBQ() {
        return zzd.zzA(this.zzaRN.getResources());
    }

    public zzc zzBR() {
        return zza(this.zzaRN.getTargetFragment());
    }

    public void zzD(IObjectWrapper iObjectWrapper) {
        this.zzaRN.registerForContextMenu((View) zzd.zzF(iObjectWrapper));
    }

    public void zzE(IObjectWrapper iObjectWrapper) {
        this.zzaRN.unregisterForContextMenu((View) zzd.zzF(iObjectWrapper));
    }
}
