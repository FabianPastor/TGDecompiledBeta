package com.google.android.gms.dynamic;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

@SuppressLint({"NewApi"})
public final class zzj extends zzl {
    private Fragment zzaSB;

    private zzj(Fragment fragment) {
        this.zzaSB = fragment;
    }

    public static zzj zza(Fragment fragment) {
        return fragment != null ? new zzj(fragment) : null;
    }

    public final Bundle getArguments() {
        return this.zzaSB.getArguments();
    }

    public final int getId() {
        return this.zzaSB.getId();
    }

    public final boolean getRetainInstance() {
        return this.zzaSB.getRetainInstance();
    }

    public final String getTag() {
        return this.zzaSB.getTag();
    }

    public final int getTargetRequestCode() {
        return this.zzaSB.getTargetRequestCode();
    }

    public final boolean getUserVisibleHint() {
        return this.zzaSB.getUserVisibleHint();
    }

    public final IObjectWrapper getView() {
        return zzn.zzw(this.zzaSB.getView());
    }

    public final boolean isAdded() {
        return this.zzaSB.isAdded();
    }

    public final boolean isDetached() {
        return this.zzaSB.isDetached();
    }

    public final boolean isHidden() {
        return this.zzaSB.isHidden();
    }

    public final boolean isInLayout() {
        return this.zzaSB.isInLayout();
    }

    public final boolean isRemoving() {
        return this.zzaSB.isRemoving();
    }

    public final boolean isResumed() {
        return this.zzaSB.isResumed();
    }

    public final boolean isVisible() {
        return this.zzaSB.isVisible();
    }

    public final void setHasOptionsMenu(boolean z) {
        this.zzaSB.setHasOptionsMenu(z);
    }

    public final void setMenuVisibility(boolean z) {
        this.zzaSB.setMenuVisibility(z);
    }

    public final void setRetainInstance(boolean z) {
        this.zzaSB.setRetainInstance(z);
    }

    public final void setUserVisibleHint(boolean z) {
        this.zzaSB.setUserVisibleHint(z);
    }

    public final void startActivity(Intent intent) {
        this.zzaSB.startActivity(intent);
    }

    public final void startActivityForResult(Intent intent, int i) {
        this.zzaSB.startActivityForResult(intent, i);
    }

    public final void zzC(IObjectWrapper iObjectWrapper) {
        this.zzaSB.registerForContextMenu((View) zzn.zzE(iObjectWrapper));
    }

    public final void zzD(IObjectWrapper iObjectWrapper) {
        this.zzaSB.unregisterForContextMenu((View) zzn.zzE(iObjectWrapper));
    }

    public final IObjectWrapper zztA() {
        return zzn.zzw(this.zzaSB.getResources());
    }

    public final zzk zztB() {
        return zza(this.zzaSB.getTargetFragment());
    }

    public final IObjectWrapper zzty() {
        return zzn.zzw(this.zzaSB.getActivity());
    }

    public final zzk zztz() {
        return zza(this.zzaSB.getParentFragment());
    }
}
