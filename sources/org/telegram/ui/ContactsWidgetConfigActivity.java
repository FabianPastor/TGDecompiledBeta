package org.telegram.ui;

import android.content.Intent;
import android.os.Bundle;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.EditWidgetActivity;
/* loaded from: classes3.dex */
public class ContactsWidgetConfigActivity extends ExternalActionActivity {
    private int creatingAppWidgetId = 0;

    @Override // org.telegram.ui.ExternalActionActivity
    protected boolean handleIntent(Intent intent, boolean z, boolean z2, boolean z3, int i, int i2) {
        if (!checkPasscode(intent, z, z2, z3, i, i2)) {
            return false;
        }
        Bundle extras = intent.getExtras();
        if (extras != null) {
            this.creatingAppWidgetId = extras.getInt("appWidgetId", 0);
        }
        if (this.creatingAppWidgetId != 0) {
            Bundle bundle = new Bundle();
            bundle.putBoolean("onlySelect", true);
            bundle.putInt("dialogsType", 10);
            bundle.putBoolean("allowSwitchAccount", true);
            EditWidgetActivity editWidgetActivity = new EditWidgetActivity(1, this.creatingAppWidgetId);
            editWidgetActivity.setDelegate(new EditWidgetActivity.EditWidgetActivityDelegate() { // from class: org.telegram.ui.ContactsWidgetConfigActivity$$ExternalSyntheticLambda0
                @Override // org.telegram.ui.EditWidgetActivity.EditWidgetActivityDelegate
                public final void didSelectDialogs(ArrayList arrayList) {
                    ContactsWidgetConfigActivity.this.lambda$handleIntent$0(arrayList);
                }
            });
            if (AndroidUtilities.isTablet()) {
                if (this.layersActionBarLayout.getFragmentStack().isEmpty()) {
                    this.layersActionBarLayout.addFragmentToStack(editWidgetActivity);
                }
            } else if (this.actionBarLayout.getFragmentStack().isEmpty()) {
                this.actionBarLayout.addFragmentToStack(editWidgetActivity);
            }
            if (!AndroidUtilities.isTablet()) {
                this.backgroundTablet.setVisibility(8);
            }
            this.actionBarLayout.showLastFragment();
            if (AndroidUtilities.isTablet()) {
                this.layersActionBarLayout.showLastFragment();
            }
            intent.setAction(null);
        } else {
            finish();
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$handleIntent$0(ArrayList arrayList) {
        Intent intent = new Intent();
        intent.putExtra("appWidgetId", this.creatingAppWidgetId);
        setResult(-1, intent);
        finish();
    }
}
