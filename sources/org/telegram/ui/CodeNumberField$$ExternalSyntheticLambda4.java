package org.telegram.ui;

import org.telegram.ui.Components.SimpleFloatPropertyCompat;
/* loaded from: classes3.dex */
public final /* synthetic */ class CodeNumberField$$ExternalSyntheticLambda4 implements SimpleFloatPropertyCompat.Getter {
    public static final /* synthetic */ CodeNumberField$$ExternalSyntheticLambda4 INSTANCE = new CodeNumberField$$ExternalSyntheticLambda4();

    private /* synthetic */ CodeNumberField$$ExternalSyntheticLambda4() {
    }

    @Override // org.telegram.ui.Components.SimpleFloatPropertyCompat.Getter
    public final float get(Object obj) {
        float f;
        f = ((CodeNumberField) obj).focusedProgress;
        return f;
    }
}
