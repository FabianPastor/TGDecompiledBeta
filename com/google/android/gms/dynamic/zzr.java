package com.google.android.gms.dynamic;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

public final class zzr extends zzl {
    private Fragment zzaSE;

    private zzr(Fragment fragment) {
        this.zzaSE = fragment;
    }

    public static zzr zza(Fragment fragment) {
        return fragment != null ? new zzr(fragment) : null;
    }

    public final Bundle getArguments() {
        return this.zzaSE.getArguments();
    }

    public final int getId() {
        return this.zzaSE.getId();
    }

    public final boolean getRetainInstance() {
        return this.zzaSE.getRetainInstance();
    }

    public final String getTag() {
        return this.zzaSE.getTag();
    }

    public final int getTargetRequestCode() {
        return this.zzaSE.getTargetRequestCode();
    }

    public final boolean getUserVisibleHint() {
        return this.zzaSE.getUserVisibleHint();
    }

    public final IObjectWrapper getView() {
        return zzn.zzw(this.zzaSE.getView());
    }

    public final boolean isAdded() {
        return this.zzaSE.isAdded();
    }

    public final boolean isDetached() {
        return this.zzaSE.isDetached();
    }

    public final boolean isHidden() {
        return this.zzaSE.isHidden();
    }

    public final boolean isInLayout() {
        return this.zzaSE.isInLayout();
    }

    public final boolean isRemoving() {
        return this.zzaSE.isRemoving();
    }

    public final boolean isResumed() {
        return this.zzaSE.isResumed();
    }

    public final boolean isVisible() {
        return this.zzaSE.isVisible();
    }

    public final void setHasOptionsMenu(boolean z) {
        this.zzaSE.setHasOptionsMenu(z);
    }

    public final void setMenuVisibility(boolean z) {
        this.zzaSE.setMenuVisibility(z);
    }

    public final void setRetainInstance(boolean z) {
        this.zzaSE.setRetainInstance(z);
    }

    public final void setUserVisibleHint(boolean z) {
        this.zzaSE.setUserVisibleHint(z);
    }

    public final void startActivity(Intent intent) {
        this.zzaSE.startActivity(intent);
    }

    public final void startActivityForResult(Intent intent, int i) {
        this.zzaSE.startActivityForResult(intent, i);
    }

    public final void zzC(IObjectWrapper iObjectWrapper) {
        this.zzaSE.registerForContextMenu((View) zzn.zzE(iObjectWrapper));
    }

    public final void zzD(IObjectWrapper iObjectWrapper) {
        this.zzaSE.unregisterForContextMenu((View) zzn.zzE(iObjectWrapper));
    }

    public final IObjectWrapper zztA() {
        return zzn.zzw(this.zzaSE.getResources());
    }

    public final zzk zztB() {
        return zza(this.zzaSE.getTargetFragment());
    }

    public final IObjectWrapper zzty() {
        return zzn.zzw(this.zzaSE.getActivity());
    }

    public final zzk zztz() {
        return zza(this.zzaSE.getParentFragment());
    }
}
