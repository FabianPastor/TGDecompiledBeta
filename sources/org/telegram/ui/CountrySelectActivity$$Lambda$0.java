package org.telegram.ui;

import android.view.View;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;

final /* synthetic */ class CountrySelectActivity$$Lambda$0 implements OnItemClickListener {
    private final CountrySelectActivity arg$1;

    CountrySelectActivity$$Lambda$0(CountrySelectActivity countrySelectActivity) {
        this.arg$1 = countrySelectActivity;
    }

    public void onItemClick(View view, int i) {
        this.arg$1.lambda$createView$0$CountrySelectActivity(view, i);
    }
}
