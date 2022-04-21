package org.telegram.ui.Components;

import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.View;
import java.util.ArrayList;
import org.telegram.ui.ActionBar.ThemeDescription;

public class SimpleThemeDescription {
    private SimpleThemeDescription() {
    }

    public static ArrayList<ThemeDescription> createThemeDescriptions(ThemeDescription.ThemeDescriptionDelegate del, String... keys) {
        ArrayList<ThemeDescription> l = new ArrayList<>(keys.length);
        for (String k : keys) {
            l.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, del, k));
        }
        return l;
    }
}
