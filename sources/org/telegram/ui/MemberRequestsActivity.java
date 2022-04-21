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
    public static final int searchMenuItem = 0;
    /* access modifiers changed from: private */
    public final MemberRequestsDelegate delegate;

    public MemberRequestsActivity(long chatId) {
        this.delegate = new MemberRequestsDelegate(this, getLayoutContainer(), chatId, true) {
            /* access modifiers changed from: protected */
            public void onImportersChanged(String query, boolean fromCache, boolean fromHide) {
                if (fromHide) {
                    MemberRequestsActivity.this.actionBar.setSearchFieldText("");
                } else {
                    super.onImportersChanged(query, fromCache, fromHide);
                }
            }
        };
    }

    public View createView(Context context) {
        String str;
        int i;
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
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
        ActionBarMenuItem searchItem = this.actionBar.createMenu().addItem(0, NUM).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener() {
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
        searchItem.setSearchFieldHint(LocaleController.getString("Search", NUM));
        searchItem.setVisibility(8);
        FrameLayout rootLayout = this.delegate.getRootLayout();
        this.delegate.loadMembers();
        this.fragmentView = rootLayout;
        return rootLayout;
    }

    public boolean onBackPressed() {
        return this.delegate.onBackPressed();
    }
}
