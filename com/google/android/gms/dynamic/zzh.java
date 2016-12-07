package com.google.android.gms.dynamic;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import com.google.android.gms.dynamic.zzc.zza;

public final class zzh extends zza {
    private Fragment Qg;

    private zzh(Fragment fragment) {
        this.Qg = fragment;
    }

    public static zzh zza(Fragment fragment) {
        return fragment != null ? new zzh(fragment) : null;
    }

    public Bundle getArguments() {
        return this.Qg.getArguments();
    }

    public int getId() {
        return this.Qg.getId();
    }

    public boolean getRetainInstance() {
        return this.Qg.getRetainInstance();
    }

    public String getTag() {
        return this.Qg.getTag();
    }

    public int getTargetRequestCode() {
        return this.Qg.getTargetRequestCode();
    }

    public boolean getUserVisibleHint() {
        return this.Qg.getUserVisibleHint();
    }

    public zzd getView() {
        return zze.zzac(this.Qg.getView());
    }

    public boolean isAdded() {
        return this.Qg.isAdded();
    }

    public boolean isDetached() {
        return this.Qg.isDetached();
    }

    public boolean isHidden() {
        return this.Qg.isHidden();
    }

    public boolean isInLayout() {
        return this.Qg.isInLayout();
    }

    public boolean isRemoving() {
        return this.Qg.isRemoving();
    }

    public boolean isResumed() {
        return this.Qg.isResumed();
    }

    public boolean isVisible() {
        return this.Qg.isVisible();
    }

    public void setHasOptionsMenu(boolean z) {
        this.Qg.setHasOptionsMenu(z);
    }

    public void setMenuVisibility(boolean z) {
        this.Qg.setMenuVisibility(z);
    }

    public void setRetainInstance(boolean z) {
        this.Qg.setRetainInstance(z);
    }

    public void setUserVisibleHint(boolean z) {
        this.Qg.setUserVisibleHint(z);
    }

    public void startActivity(Intent intent) {
        this.Qg.startActivity(intent);
    }

    public void startActivityForResult(Intent intent, int i) {
        this.Qg.startActivityForResult(intent, i);
    }

    public void zzac(zzd com_google_android_gms_dynamic_zzd) {
        this.Qg.registerForContextMenu((View) zze.zzae(com_google_android_gms_dynamic_zzd));
    }

    public void zzad(zzd com_google_android_gms_dynamic_zzd) {
        this.Qg.unregisterForContextMenu((View) zze.zzae(com_google_android_gms_dynamic_zzd));
    }

    public zzd zzbdp() {
        return zze.zzac(this.Qg.getActivity());
    }

    public zzc zzbdq() {
        return zza(this.Qg.getParentFragment());
    }

    public zzd zzbdr() {
        return zze.zzac(this.Qg.getResources());
    }

    public zzc zzbds() {
        return zza(this.Qg.getTargetFragment());
    }
}
