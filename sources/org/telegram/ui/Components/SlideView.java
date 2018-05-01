package org.telegram.ui.Components;

import android.content.Context;
import android.os.Bundle;
import android.widget.LinearLayout;

public class SlideView extends LinearLayout {
    public String getHeaderName() {
        return TtmlNode.ANONYMOUS_REGION_ID;
    }

    public boolean needBackButton() {
        return false;
    }

    public void onBackPressed() {
    }

    public void onCancelPressed() {
    }

    public void onDestroyActivity() {
    }

    public void onNextPressed() {
    }

    public void onShow() {
    }

    public void restoreStateParams(Bundle bundle) {
    }

    public void saveStateParams(Bundle bundle) {
    }

    public void setParams(Bundle bundle, boolean z) {
    }

    public SlideView(Context context) {
        super(context);
    }
}
