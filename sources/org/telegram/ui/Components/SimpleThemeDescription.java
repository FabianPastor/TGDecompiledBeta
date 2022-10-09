package org.telegram.ui.Components;

import java.util.ArrayList;
import org.telegram.ui.ActionBar.ThemeDescription;
/* loaded from: classes3.dex */
public class SimpleThemeDescription {
    public static ThemeDescription createThemeDescription(ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate, String str) {
        return new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, str);
    }

    public static ArrayList<ThemeDescription> createThemeDescriptions(ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate, String... strArr) {
        ArrayList<ThemeDescription> arrayList = new ArrayList<>(strArr.length);
        for (String str : strArr) {
            arrayList.add(createThemeDescription(themeDescriptionDelegate, str));
        }
        return arrayList;
    }
}
