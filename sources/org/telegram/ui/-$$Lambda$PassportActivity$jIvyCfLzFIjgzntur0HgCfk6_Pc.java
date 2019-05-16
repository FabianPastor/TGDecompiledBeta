package org.telegram.ui;

import org.telegram.messenger.MrzRecognizer.Result;
import org.telegram.ui.MrzCameraActivity.MrzCameraActivityDelegate;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$PassportActivity$jIvyCfLzFIjgzntur0HgCfk6_Pc implements MrzCameraActivityDelegate {
    private final /* synthetic */ PassportActivity f$0;

    public /* synthetic */ -$$Lambda$PassportActivity$jIvyCfLzFIjgzntur0HgCfk6_Pc(PassportActivity passportActivity) {
        this.f$0 = passportActivity;
    }

    public final void didFindMrzInfo(Result result) {
        this.f$0.lambda$null$44$PassportActivity(result);
    }
}
