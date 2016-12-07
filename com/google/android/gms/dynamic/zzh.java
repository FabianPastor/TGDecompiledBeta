package com.google.android.gms.dynamic;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import com.google.android.gms.dynamic.zzc.zza;

public final class zzh extends zza {
    private Fragment Ov;

    private zzh(Fragment fragment) {
        this.Ov = fragment;
    }

    public static zzh zza(Fragment fragment) {
        return fragment != null ? new zzh(fragment) : null;
    }

    public Bundle getArguments() {
        return this.Ov.getArguments();
    }

    public int getId() {
        return this.Ov.getId();
    }

    public boolean getRetainInstance() {
        return this.Ov.getRetainInstance();
    }

    public String getTag() {
        return this.Ov.getTag();
    }

    public int getTargetRequestCode() {
        return this.Ov.getTargetRequestCode();
    }

    public boolean getUserVisibleHint() {
        return this.Ov.getUserVisibleHint();
    }

    public zzd getView() {
        return zze.zzac(this.Ov.getView());
    }

    public boolean isAdded() {
        return this.Ov.isAdded();
    }

    public boolean isDetached() {
        return this.Ov.isDetached();
    }

    public boolean isHidden() {
        return this.Ov.isHidden();
    }

    public boolean isInLayout() {
        return this.Ov.isInLayout();
    }

    public boolean isRemoving() {
        return this.Ov.isRemoving();
    }

    public boolean isResumed() {
        return this.Ov.isResumed();
    }

    public boolean isVisible() {
        return this.Ov.isVisible();
    }

    public void setHasOptionsMenu(boolean z) {
        this.Ov.setHasOptionsMenu(z);
    }

    public void setMenuVisibility(boolean z) {
        this.Ov.setMenuVisibility(z);
    }

    public void setRetainInstance(boolean z) {
        this.Ov.setRetainInstance(z);
    }

    public void setUserVisibleHint(boolean z) {
        this.Ov.setUserVisibleHint(z);
    }

    public void startActivity(Intent intent) {
        this.Ov.startActivity(intent);
    }

    public void startActivityForResult(Intent intent, int i) {
        this.Ov.startActivityForResult(intent, i);
    }

    public void zzac(zzd com_google_android_gms_dynamic_zzd) {
        this.Ov.registerForContextMenu((View) zze.zzae(com_google_android_gms_dynamic_zzd));
    }

    public void zzad(zzd com_google_android_gms_dynamic_zzd) {
        this.Ov.unregisterForContextMenu((View) zze.zzae(com_google_android_gms_dynamic_zzd));
    }

    public zzd zzbdu() {
        return zze.zzac(this.Ov.getActivity());
    }

    public zzc zzbdv() {
        return zza(this.Ov.getParentFragment());
    }

    public zzd zzbdw() {
        return zze.zzac(this.Ov.getResources());
    }

    public zzc zzbdx() {
        return zza(this.Ov.getTargetFragment());
    }
}
