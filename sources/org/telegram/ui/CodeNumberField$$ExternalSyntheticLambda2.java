package org.telegram.ui;

import org.telegram.ui.Components.SimpleFloatPropertyCompat;
/* loaded from: classes3.dex */
public final /* synthetic */ class CodeNumberField$$ExternalSyntheticLambda2 implements SimpleFloatPropertyCompat.Getter {
    public static final /* synthetic */ CodeNumberField$$ExternalSyntheticLambda2 INSTANCE = new CodeNumberField$$ExternalSyntheticLambda2();

    private /* synthetic */ CodeNumberField$$ExternalSyntheticLambda2() {
    }

    @Override // org.telegram.ui.Components.SimpleFloatPropertyCompat.Getter
    public final float get(Object obj) {
        float f;
        f = ((CodeNumberField) obj).successProgress;
        return f;
    }
}
