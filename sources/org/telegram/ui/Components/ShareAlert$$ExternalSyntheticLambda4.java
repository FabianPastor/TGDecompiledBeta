package org.telegram.ui.Components;

import androidx.arch.core.util.Function;

public final /* synthetic */ class ShareAlert$$ExternalSyntheticLambda4 implements Function {
    public final /* synthetic */ boolean f$0;

    public /* synthetic */ ShareAlert$$ExternalSyntheticLambda4(boolean z) {
        this.f$0 = z;
    }

    public final Object apply(Object obj) {
        return ((BulletinFactory) obj).createCopyLinkBulletin(this.f$0);
    }
}
