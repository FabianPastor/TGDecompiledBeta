package org.telegram.ui;

import java.util.Comparator;
import org.telegram.ui.ActionBar.Theme;

/* renamed from: org.telegram.ui.-$$Lambda$ThemeActivity$cs0N3OVBAa2T6bewE_YVZM-eTCA  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$ThemeActivity$cs0N3OVBAa2T6bewE_YVZMeTCA implements Comparator {
    public static final /* synthetic */ $$Lambda$ThemeActivity$cs0N3OVBAa2T6bewE_YVZMeTCA INSTANCE = new $$Lambda$ThemeActivity$cs0N3OVBAa2T6bewE_YVZMeTCA();

    private /* synthetic */ $$Lambda$ThemeActivity$cs0N3OVBAa2T6bewE_YVZMeTCA() {
    }

    public final int compare(Object obj, Object obj2) {
        return Integer.compare(((Theme.ThemeInfo) obj).sortIndex, ((Theme.ThemeInfo) obj2).sortIndex);
    }
}
