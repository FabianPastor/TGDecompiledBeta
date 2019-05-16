package org.telegram.ui;

import java.util.Comparator;
import org.telegram.ui.CountrySelectActivity.Country;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$CountrySelectActivity$CountryAdapter$GbbT4_eUqPD5K07oYJV3z7sG8q4 implements Comparator {
    public static final /* synthetic */ -$$Lambda$CountrySelectActivity$CountryAdapter$GbbT4_eUqPD5K07oYJV3z7sG8q4 INSTANCE = new -$$Lambda$CountrySelectActivity$CountryAdapter$GbbT4_eUqPD5K07oYJV3z7sG8q4();

    private /* synthetic */ -$$Lambda$CountrySelectActivity$CountryAdapter$GbbT4_eUqPD5K07oYJV3z7sG8q4() {
    }

    public final int compare(Object obj, Object obj2) {
        return ((Country) obj).name.compareTo(((Country) obj2).name);
    }
}
