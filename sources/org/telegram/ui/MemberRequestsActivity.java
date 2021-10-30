package org.telegram.ui;

import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Delegates.MemberRequestsDelegate;

public class MemberRequestsActivity extends BaseFragment {
    /* access modifiers changed from: private */
    public final MemberRequestsDelegate delegate;

    public MemberRequestsActivity(long j) {
        this.delegate = new MemberRequestsDelegate(this, j, true) {
            /* access modifiers changed from: protected */
            public void onImportersChanged(String str, boolean z, boolean z2) {
                if (z2) {
                    MemberRequestsActivity.this.actionBar.setSearchFieldText("");
                } else {
                    super.onImportersChanged(str, z, z2);
                }
            }
        };
    }

    public View createView(Context context) {
        String str;
        int i;
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    MemberRequestsActivity.this.finishFragment();
                }
            }
        });
        this.actionBar.setBackButtonImage(NUM);
        ActionBar actionBar = this.actionBar;
        if (this.delegate.isChannel) {
            i = NUM;
            str = "SubscribeRequests";
        } else {
            i = NUM;
            str = "MemberRequests";
        }
        actionBar.setTitle(LocaleController.getString(str, i));
        ActionBarMenuItem actionBarMenuItemSearchListener = this.actionBar.createMenu().addItem(0, NUM).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener() {
            public void onSearchExpand() {
                super.onSearchExpand();
                MemberRequestsActivity.this.delegate.setSearchExpanded(true);
            }

            public void onSearchCollapse() {
                super.onSearchCollapse();
                MemberRequestsActivity.this.delegate.setSearchExpanded(false);
                MemberRequestsActivity.this.delegate.setQuery((String) null);
            }

            public void onTextChanged(EditText editText) {
                super.onTextChanged(editText);
                MemberRequestsActivity.this.delegate.setQuery(editText.getText().toString());
            }
        });
        actionBarMenuItemSearchListener.setSearchFieldHint(LocaleController.getString("Search", NUM));
        actionBarMenuItemSearchListener.setVisibility(8);
        FrameLayout rootLayout = this.delegate.getRootLayout();
        this.delegate.loadMembers();
        this.fragmentView = rootLayout;
        return rootLayout;
    }

    public boolean onBackPressed() {
        return this.delegate.onBackPressed();
    }
}
