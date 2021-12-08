package org.telegram.ui;

import com.google.android.gms.internal.icing.zzby$$ExternalSyntheticBackport0;
import java.util.Comparator;
import org.telegram.ui.ActionBar.Theme;

public final /* synthetic */ class ThemeActivity$$ExternalSyntheticLambda0 implements Comparator {
    public static final /* synthetic */ ThemeActivity$$ExternalSyntheticLambda0 INSTANCE = new ThemeActivity$$ExternalSyntheticLambda0();

    private /* synthetic */ ThemeActivity$$ExternalSyntheticLambda0() {
    }

    public final int compare(Object obj, Object obj2) {
        return zzby$$ExternalSyntheticBackport0.m(((Theme.ThemeInfo) obj).sortIndex, ((Theme.ThemeInfo) obj2).sortIndex);
    }
}
