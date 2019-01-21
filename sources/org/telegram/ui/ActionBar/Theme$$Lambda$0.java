package org.telegram.ui.ActionBar;

import java.util.Comparator;
import org.telegram.ui.ActionBar.Theme.ThemeInfo;

final /* synthetic */ class Theme$$Lambda$0 implements Comparator {
    static final Comparator $instance = new Theme$$Lambda$0();

    private Theme$$Lambda$0() {
    }

    public int compare(Object obj, Object obj2) {
        return Theme.lambda$sortThemes$0$Theme((ThemeInfo) obj, (ThemeInfo) obj2);
    }
}
