package com.google.android.gms.dynamic;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.google.android.gms.dynamic.zzc.zza;

@SuppressLint({"NewApi"})
public final class zzb extends zza {
    private Fragment Qd;

    private zzb(Fragment fragment) {
        this.Qd = fragment;
    }

    public static zzb zza(Fragment fragment) {
        return fragment != null ? new zzb(fragment) : null;
    }

    public Bundle getArguments() {
        return this.Qd.getArguments();
    }

    public int getId() {
        return this.Qd.getId();
    }

    public boolean getRetainInstance() {
        return this.Qd.getRetainInstance();
    }

    public String getTag() {
        return this.Qd.getTag();
    }

    public int getTargetRequestCode() {
        return this.Qd.getTargetRequestCode();
    }

    public boolean getUserVisibleHint() {
        return this.Qd.getUserVisibleHint();
    }

    public zzd getView() {
        return zze.zzac(this.Qd.getView());
    }

    public boolean isAdded() {
        return this.Qd.isAdded();
    }

    public boolean isDetached() {
        return this.Qd.isDetached();
    }

    public boolean isHidden() {
        return this.Qd.isHidden();
    }

    public boolean isInLayout() {
        return this.Qd.isInLayout();
    }

    public boolean isRemoving() {
        return this.Qd.isRemoving();
    }

    public boolean isResumed() {
        return this.Qd.isResumed();
    }

    public boolean isVisible() {
        return this.Qd.isVisible();
    }

    public void setHasOptionsMenu(boolean z) {
        this.Qd.setHasOptionsMenu(z);
    }

    public void setMenuVisibility(boolean z) {
        this.Qd.setMenuVisibility(z);
    }

    public void setRetainInstance(boolean z) {
        this.Qd.setRetainInstance(z);
    }

    public void setUserVisibleHint(boolean z) {
        this.Qd.setUserVisibleHint(z);
    }

    public void startActivity(Intent intent) {
        this.Qd.startActivity(intent);
    }

    public void startActivityForResult(Intent intent, int i) {
        this.Qd.startActivityForResult(intent, i);
    }

    public void zzac(zzd com_google_android_gms_dynamic_zzd) {
        this.Qd.registerForContextMenu((View) zze.zzae(com_google_android_gms_dynamic_zzd));
    }

    public void zzad(zzd com_google_android_gms_dynamic_zzd) {
        this.Qd.unregisterForContextMenu((View) zze.zzae(com_google_android_gms_dynamic_zzd));
    }

    public zzd zzbdp() {
        return zze.zzac(this.Qd.getActivity());
    }

    public zzc zzbdq() {
        return zza(this.Qd.getParentFragment());
    }

    public zzd zzbdr() {
        return zze.zzac(this.Qd.getResources());
    }

    public zzc zzbds() {
        return zza(this.Qd.getTargetFragment());
    }
}
