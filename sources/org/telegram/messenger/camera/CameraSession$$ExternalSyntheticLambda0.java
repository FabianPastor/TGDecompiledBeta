package org.telegram.messenger.camera;

import android.hardware.Camera;
/* loaded from: classes.dex */
public final /* synthetic */ class CameraSession$$ExternalSyntheticLambda0 implements Camera.AutoFocusCallback {
    public static final /* synthetic */ CameraSession$$ExternalSyntheticLambda0 INSTANCE = new CameraSession$$ExternalSyntheticLambda0();

    private /* synthetic */ CameraSession$$ExternalSyntheticLambda0() {
    }

    @Override // android.hardware.Camera.AutoFocusCallback
    public final void onAutoFocus(boolean z, Camera camera) {
        CameraSession.lambda$new$0(z, camera);
    }
}
