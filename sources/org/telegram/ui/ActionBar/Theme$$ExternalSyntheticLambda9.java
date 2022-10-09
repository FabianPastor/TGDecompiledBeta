package org.telegram.ui.ActionBar;

import java.util.Comparator;
import org.telegram.ui.ActionBar.Theme;
/* loaded from: classes3.dex */
public final /* synthetic */ class Theme$$ExternalSyntheticLambda9 implements Comparator {
    public static final /* synthetic */ Theme$$ExternalSyntheticLambda9 INSTANCE = new Theme$$ExternalSyntheticLambda9();

    private /* synthetic */ Theme$$ExternalSyntheticLambda9() {
    }

    @Override // java.util.Comparator
    public final int compare(Object obj, Object obj2) {
        int lambda$sortAccents$0;
        lambda$sortAccents$0 = Theme.lambda$sortAccents$0((Theme.ThemeAccent) obj, (Theme.ThemeAccent) obj2);
        return lambda$sortAccents$0;
    }
}
