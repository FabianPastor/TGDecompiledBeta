package org.telegram.ui;

import java.util.Comparator;
import org.telegram.messenger.ChatObject$Call$$ExternalSyntheticBackport0;
import org.telegram.ui.ActionBar.Theme;

public final /* synthetic */ class ThemeActivity$$ExternalSyntheticLambda7 implements Comparator {
    public static final /* synthetic */ ThemeActivity$$ExternalSyntheticLambda7 INSTANCE = new ThemeActivity$$ExternalSyntheticLambda7();

    private /* synthetic */ ThemeActivity$$ExternalSyntheticLambda7() {
    }

    public final int compare(Object obj, Object obj2) {
        return ChatObject$Call$$ExternalSyntheticBackport0.m(((Theme.ThemeInfo) obj).sortIndex, ((Theme.ThemeInfo) obj2).sortIndex);
    }
}
