package org.telegram.ui;

import android.graphics.Bitmap;
import org.telegram.messenger.GenericProvider;
import org.telegram.ui.IntroActivity;
/* loaded from: classes3.dex */
public final /* synthetic */ class IntroActivity$EGLThread$$ExternalSyntheticLambda1 implements GenericProvider {
    public static final /* synthetic */ IntroActivity$EGLThread$$ExternalSyntheticLambda1 INSTANCE = new IntroActivity$EGLThread$$ExternalSyntheticLambda1();

    private /* synthetic */ IntroActivity$EGLThread$$ExternalSyntheticLambda1() {
    }

    @Override // org.telegram.messenger.GenericProvider
    public final Object provide(Object obj) {
        Bitmap lambda$initGL$1;
        lambda$initGL$1 = IntroActivity.EGLThread.lambda$initGL$1((Void) obj);
        return lambda$initGL$1;
    }
}
