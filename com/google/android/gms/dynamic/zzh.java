package com.google.android.gms.dynamic;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import com.google.android.gms.dynamic.zzc.zza;

public final class zzh extends zza {
    private Fragment zzaQq;

    private zzh(Fragment fragment) {
        this.zzaQq = fragment;
    }

    public static zzh zza(Fragment fragment) {
        return fragment != null ? new zzh(fragment) : null;
    }

    public Bundle getArguments() {
        return this.zzaQq.getArguments();
    }

    public int getId() {
        return this.zzaQq.getId();
    }

    public boolean getRetainInstance() {
        return this.zzaQq.getRetainInstance();
    }

    public String getTag() {
        return this.zzaQq.getTag();
    }

    public int getTargetRequestCode() {
        return this.zzaQq.getTargetRequestCode();
    }

    public boolean getUserVisibleHint() {
        return this.zzaQq.getUserVisibleHint();
    }

    public zzd getView() {
        return zze.zzA(this.zzaQq.getView());
    }

    public boolean isAdded() {
        return this.zzaQq.isAdded();
    }

    public boolean isDetached() {
        return this.zzaQq.isDetached();
    }

    public boolean isHidden() {
        return this.zzaQq.isHidden();
    }

    public boolean isInLayout() {
        return this.zzaQq.isInLayout();
    }

    public boolean isRemoving() {
        return this.zzaQq.isRemoving();
    }

    public boolean isResumed() {
        return this.zzaQq.isResumed();
    }

    public boolean isVisible() {
        return this.zzaQq.isVisible();
    }

    public void setHasOptionsMenu(boolean z) {
        this.zzaQq.setHasOptionsMenu(z);
    }

    public void setMenuVisibility(boolean z) {
        this.zzaQq.setMenuVisibility(z);
    }

    public void setRetainInstance(boolean z) {
        this.zzaQq.setRetainInstance(z);
    }

    public void setUserVisibleHint(boolean z) {
        this.zzaQq.setUserVisibleHint(z);
    }

    public void startActivity(Intent intent) {
        this.zzaQq.startActivity(intent);
    }

    public void startActivityForResult(Intent intent, int i) {
        this.zzaQq.startActivityForResult(intent, i);
    }

    public zzd zzAZ() {
        return zze.zzA(this.zzaQq.getActivity());
    }

    public zzc zzBa() {
        return zza(this.zzaQq.getParentFragment());
    }

    public zzd zzBb() {
        return zze.zzA(this.zzaQq.getResources());
    }

    public zzc zzBc() {
        return zza(this.zzaQq.getTargetFragment());
    }

    public void zzC(zzd com_google_android_gms_dynamic_zzd) {
        this.zzaQq.registerForContextMenu((View) zze.zzE(com_google_android_gms_dynamic_zzd));
    }

    public void zzD(zzd com_google_android_gms_dynamic_zzd) {
        this.zzaQq.unregisterForContextMenu((View) zze.zzE(com_google_android_gms_dynamic_zzd));
    }
}
