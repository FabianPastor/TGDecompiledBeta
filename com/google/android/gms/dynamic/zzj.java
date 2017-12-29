package com.google.android.gms.dynamic;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

@SuppressLint({"NewApi"})
public final class zzj extends zzl {
    private Fragment zzgwm;

    private zzj(Fragment fragment) {
        this.zzgwm = fragment;
    }

    public static zzj zza(Fragment fragment) {
        return fragment != null ? new zzj(fragment) : null;
    }

    public final Bundle getArguments() {
        return this.zzgwm.getArguments();
    }

    public final int getId() {
        return this.zzgwm.getId();
    }

    public final boolean getRetainInstance() {
        return this.zzgwm.getRetainInstance();
    }

    public final String getTag() {
        return this.zzgwm.getTag();
    }

    public final int getTargetRequestCode() {
        return this.zzgwm.getTargetRequestCode();
    }

    public final boolean getUserVisibleHint() {
        return this.zzgwm.getUserVisibleHint();
    }

    public final IObjectWrapper getView() {
        return zzn.zzz(this.zzgwm.getView());
    }

    public final boolean isAdded() {
        return this.zzgwm.isAdded();
    }

    public final boolean isDetached() {
        return this.zzgwm.isDetached();
    }

    public final boolean isHidden() {
        return this.zzgwm.isHidden();
    }

    public final boolean isInLayout() {
        return this.zzgwm.isInLayout();
    }

    public final boolean isRemoving() {
        return this.zzgwm.isRemoving();
    }

    public final boolean isResumed() {
        return this.zzgwm.isResumed();
    }

    public final boolean isVisible() {
        return this.zzgwm.isVisible();
    }

    public final void setHasOptionsMenu(boolean z) {
        this.zzgwm.setHasOptionsMenu(z);
    }

    public final void setMenuVisibility(boolean z) {
        this.zzgwm.setMenuVisibility(z);
    }

    public final void setRetainInstance(boolean z) {
        this.zzgwm.setRetainInstance(z);
    }

    public final void setUserVisibleHint(boolean z) {
        this.zzgwm.setUserVisibleHint(z);
    }

    public final void startActivity(Intent intent) {
        this.zzgwm.startActivity(intent);
    }

    public final void startActivityForResult(Intent intent, int i) {
        this.zzgwm.startActivityForResult(intent, i);
    }

    public final IObjectWrapper zzapx() {
        return zzn.zzz(this.zzgwm.getActivity());
    }

    public final zzk zzapy() {
        return zza(this.zzgwm.getParentFragment());
    }

    public final IObjectWrapper zzapz() {
        return zzn.zzz(this.zzgwm.getResources());
    }

    public final zzk zzaqa() {
        return zza(this.zzgwm.getTargetFragment());
    }

    public final void zzv(IObjectWrapper iObjectWrapper) {
        this.zzgwm.registerForContextMenu((View) zzn.zzx(iObjectWrapper));
    }

    public final void zzw(IObjectWrapper iObjectWrapper) {
        this.zzgwm.unregisterForContextMenu((View) zzn.zzx(iObjectWrapper));
    }
}
