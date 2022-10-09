package org.telegram.ui;

import org.telegram.ui.Components.SimpleFloatPropertyCompat;
/* loaded from: classes3.dex */
public final /* synthetic */ class CodeNumberField$$ExternalSyntheticLambda3 implements SimpleFloatPropertyCompat.Getter {
    public static final /* synthetic */ CodeNumberField$$ExternalSyntheticLambda3 INSTANCE = new CodeNumberField$$ExternalSyntheticLambda3();

    private /* synthetic */ CodeNumberField$$ExternalSyntheticLambda3() {
    }

    @Override // org.telegram.ui.Components.SimpleFloatPropertyCompat.Getter
    public final float get(Object obj) {
        float f;
        f = ((CodeNumberField) obj).errorProgress;
        return f;
    }
}
