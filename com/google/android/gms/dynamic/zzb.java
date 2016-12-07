package com.google.android.gms.dynamic;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.google.android.gms.dynamic.zzc.zza;

@SuppressLint({"NewApi"})
public final class zzb extends zza {
    private Fragment Os;

    private zzb(Fragment fragment) {
        this.Os = fragment;
    }

    public static zzb zza(Fragment fragment) {
        return fragment != null ? new zzb(fragment) : null;
    }

    public Bundle getArguments() {
        return this.Os.getArguments();
    }

    public int getId() {
        return this.Os.getId();
    }

    public boolean getRetainInstance() {
        return this.Os.getRetainInstance();
    }

    public String getTag() {
        return this.Os.getTag();
    }

    public int getTargetRequestCode() {
        return this.Os.getTargetRequestCode();
    }

    public boolean getUserVisibleHint() {
        return this.Os.getUserVisibleHint();
    }

    public zzd getView() {
        return zze.zzac(this.Os.getView());
    }

    public boolean isAdded() {
        return this.Os.isAdded();
    }

    public boolean isDetached() {
        return this.Os.isDetached();
    }

    public boolean isHidden() {
        return this.Os.isHidden();
    }

    public boolean isInLayout() {
        return this.Os.isInLayout();
    }

    public boolean isRemoving() {
        return this.Os.isRemoving();
    }

    public boolean isResumed() {
        return this.Os.isResumed();
    }

    public boolean isVisible() {
        return this.Os.isVisible();
    }

    public void setHasOptionsMenu(boolean z) {
        this.Os.setHasOptionsMenu(z);
    }

    public void setMenuVisibility(boolean z) {
        this.Os.setMenuVisibility(z);
    }

    public void setRetainInstance(boolean z) {
        this.Os.setRetainInstance(z);
    }

    public void setUserVisibleHint(boolean z) {
        this.Os.setUserVisibleHint(z);
    }

    public void startActivity(Intent intent) {
        this.Os.startActivity(intent);
    }

    public void startActivityForResult(Intent intent, int i) {
        this.Os.startActivityForResult(intent, i);
    }

    public void zzac(zzd com_google_android_gms_dynamic_zzd) {
        this.Os.registerForContextMenu((View) zze.zzae(com_google_android_gms_dynamic_zzd));
    }

    public void zzad(zzd com_google_android_gms_dynamic_zzd) {
        this.Os.unregisterForContextMenu((View) zze.zzae(com_google_android_gms_dynamic_zzd));
    }

    public zzd zzbdu() {
        return zze.zzac(this.Os.getActivity());
    }

    public zzc zzbdv() {
        return zza(this.Os.getParentFragment());
    }

    public zzd zzbdw() {
        return zze.zzac(this.Os.getResources());
    }

    public zzc zzbdx() {
        return zza(this.Os.getTargetFragment());
    }
}
