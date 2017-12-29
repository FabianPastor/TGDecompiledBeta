package com.google.android.gms.maps;

import android.os.RemoteException;
import com.google.android.gms.maps.internal.IUiSettingsDelegate;
import com.google.android.gms.maps.model.RuntimeRemoteException;

public final class UiSettings {
    private final IUiSettingsDelegate zziua;

    UiSettings(IUiSettingsDelegate iUiSettingsDelegate) {
        this.zziua = iUiSettingsDelegate;
    }

    public final void setCompassEnabled(boolean z) {
        try {
            this.zziua.setCompassEnabled(z);
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public final void setMyLocationButtonEnabled(boolean z) {
        try {
            this.zziua.setMyLocationButtonEnabled(z);
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }

    public final void setZoomControlsEnabled(boolean z) {
        try {
            this.zziua.setZoomControlsEnabled(z);
        } catch (RemoteException e) {
            throw new RuntimeRemoteException(e);
        }
    }
}
