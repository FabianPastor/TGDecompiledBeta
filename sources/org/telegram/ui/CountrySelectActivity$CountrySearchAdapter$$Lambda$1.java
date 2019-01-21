package org.telegram.ui;

import java.util.ArrayList;
import org.telegram.ui.CountrySelectActivity.CountrySearchAdapter;

final /* synthetic */ class CountrySelectActivity$CountrySearchAdapter$$Lambda$1 implements Runnable {
    private final CountrySearchAdapter arg$1;
    private final ArrayList arg$2;

    CountrySelectActivity$CountrySearchAdapter$$Lambda$1(CountrySearchAdapter countrySearchAdapter, ArrayList arrayList) {
        this.arg$1 = countrySearchAdapter;
        this.arg$2 = arrayList;
    }

    public void run() {
        this.arg$1.lambda$updateSearchResults$1$CountrySelectActivity$CountrySearchAdapter(this.arg$2);
    }
}
