package org.telegram.ui;

import android.content.Intent;
import android.os.Bundle;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;

public class ChatsWidgetConfigActivity extends ExternalActionActivity {
    private int creatingAppWidgetId = 0;

    /* access modifiers changed from: protected */
    public boolean handleIntent(Intent intent, boolean isNew, boolean restore, boolean fromPassword, int intentAccount, int state) {
        if (!checkPasscode(intent, isNew, restore, fromPassword, intentAccount, state)) {
            return false;
        }
        Bundle extras = intent.getExtras();
        if (extras != null) {
            this.creatingAppWidgetId = extras.getInt("appWidgetId", 0);
        }
        if (this.creatingAppWidgetId != 0) {
            Bundle args = new Bundle();
            args.putBoolean("onlySelect", true);
            args.putInt("dialogsType", 10);
            args.putBoolean("allowSwitchAccount", true);
            EditWidgetActivity fragment = new EditWidgetActivity(0, this.creatingAppWidgetId);
            fragment.setDelegate(new ChatsWidgetConfigActivity$$ExternalSyntheticLambda0(this));
            if (AndroidUtilities.isTablet()) {
                if (this.layersActionBarLayout.fragmentsStack.isEmpty()) {
                    this.layersActionBarLayout.addFragmentToStack(fragment);
                }
            } else if (this.actionBarLayout.fragmentsStack.isEmpty()) {
                this.actionBarLayout.addFragmentToStack(fragment);
            }
            if (!AndroidUtilities.isTablet()) {
                this.backgroundTablet.setVisibility(8);
            }
            this.actionBarLayout.showLastFragment();
            if (AndroidUtilities.isTablet()) {
                this.layersActionBarLayout.showLastFragment();
            }
            intent.setAction((String) null);
        } else {
            finish();
        }
        return true;
    }

    /* renamed from: lambda$handleIntent$0$org-telegram-ui-ChatsWidgetConfigActivity  reason: not valid java name */
    public /* synthetic */ void m1985lambda$handleIntent$0$orgtelegramuiChatsWidgetConfigActivity(ArrayList dialogs) {
        Intent resultValue = new Intent();
        resultValue.putExtra("appWidgetId", this.creatingAppWidgetId);
        setResult(-1, resultValue);
        finish();
    }
}
