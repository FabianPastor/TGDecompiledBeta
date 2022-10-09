package org.telegram.ui.Components;

import java.util.Comparator;
/* loaded from: classes3.dex */
public final /* synthetic */ class InstantCameraView$$ExternalSyntheticLambda6 implements Comparator {
    public static final /* synthetic */ InstantCameraView$$ExternalSyntheticLambda6 INSTANCE = new InstantCameraView$$ExternalSyntheticLambda6();

    private /* synthetic */ InstantCameraView$$ExternalSyntheticLambda6() {
    }

    @Override // java.util.Comparator
    public final int compare(Object obj, Object obj2) {
        int lambda$chooseOptimalSize$2;
        lambda$chooseOptimalSize$2 = InstantCameraView.lambda$chooseOptimalSize$2((org.telegram.messenger.camera.Size) obj, (org.telegram.messenger.camera.Size) obj2);
        return lambda$chooseOptimalSize$2;
    }
}
