package org.telegram.ui;

import org.telegram.ui.CountrySelectActivity.CountrySearchAdapter;

final /* synthetic */ class CountrySelectActivity$CountrySearchAdapter$$Lambda$0 implements Runnable {
    private final CountrySearchAdapter arg$1;
    private final String arg$2;

    CountrySelectActivity$CountrySearchAdapter$$Lambda$0(CountrySearchAdapter countrySearchAdapter, String str) {
        this.arg$1 = countrySearchAdapter;
        this.arg$2 = str;
    }

    public void run() {
        this.arg$1.lambda$processSearch$0$CountrySelectActivity$CountrySearchAdapter(this.arg$2);
    }
}
