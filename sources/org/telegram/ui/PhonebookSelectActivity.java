package org.telegram.ui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.ContactsController.Contact;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.R;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.support.widget.RecyclerView.OnScrollListener;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate;
import org.telegram.ui.Adapters.PhonebookAdapter;
import org.telegram.ui.Adapters.PhonebookSearchAdapter;
import org.telegram.ui.Cells.LetterSectionCell;
import org.telegram.ui.Cells.UserCell;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;

public class PhonebookSelectActivity extends BaseFragment implements NotificationCenterDelegate {
    private static final int search_button = 0;
    private PhonebookSelectActivityDelegate delegate;
    private EmptyTextProgressView emptyView;
    private RecyclerListView listView;
    private PhonebookAdapter listViewAdapter;
    private PhonebookSearchAdapter searchListViewAdapter;
    private boolean searchWas;
    private boolean searching;

    public interface PhonebookSelectActivityDelegate {
        void didSelectContact(User user);
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.contactsDidLoad);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.closeChats);
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.contactsDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.closeChats);
    }

    public View createView(Context context) {
        this.searching = false;
        this.searchWas = false;
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("SelectContact", R.string.SelectContact));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    PhonebookSelectActivity.this.lambda$checkDiscard$70$PassportActivity();
                }
            }
        });
        this.actionBar.createMenu().addItem(0, (int) R.drawable.ic_ab_search).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItemSearchListener() {
            public void onSearchExpand() {
                PhonebookSelectActivity.this.searching = true;
            }

            public void onSearchCollapse() {
                PhonebookSelectActivity.this.searchListViewAdapter.search(null);
                PhonebookSelectActivity.this.searching = false;
                PhonebookSelectActivity.this.searchWas = false;
                PhonebookSelectActivity.this.listView.setAdapter(PhonebookSelectActivity.this.listViewAdapter);
                PhonebookSelectActivity.this.listView.setSectionsType(1);
                PhonebookSelectActivity.this.listViewAdapter.notifyDataSetChanged();
                PhonebookSelectActivity.this.listView.setFastScrollVisible(true);
                PhonebookSelectActivity.this.listView.setVerticalScrollBarEnabled(false);
                PhonebookSelectActivity.this.emptyView.setText(LocaleController.getString("NoContacts", R.string.NoContacts));
            }

            public void onTextChanged(EditText editText) {
                if (PhonebookSelectActivity.this.searchListViewAdapter != null) {
                    String text = editText.getText().toString();
                    if (text.length() != 0) {
                        PhonebookSelectActivity.this.searchWas = true;
                    }
                    PhonebookSelectActivity.this.searchListViewAdapter.search(text);
                }
            }
        }).setSearchFieldHint(LocaleController.getString("Search", R.string.Search));
        this.searchListViewAdapter = new PhonebookSearchAdapter(context) {
            protected void onUpdateSearchResults(String query) {
                if (!TextUtils.isEmpty(query) && PhonebookSelectActivity.this.listView != null && PhonebookSelectActivity.this.listView.getAdapter() != PhonebookSelectActivity.this.searchListViewAdapter) {
                    PhonebookSelectActivity.this.listView.setAdapter(PhonebookSelectActivity.this.searchListViewAdapter);
                    PhonebookSelectActivity.this.listView.setSectionsType(0);
                    PhonebookSelectActivity.this.searchListViewAdapter.notifyDataSetChanged();
                    PhonebookSelectActivity.this.listView.setFastScrollVisible(false);
                    PhonebookSelectActivity.this.listView.setVerticalScrollBarEnabled(true);
                    PhonebookSelectActivity.this.emptyView.setText(LocaleController.getString("NoResult", R.string.NoResult));
                }
            }
        };
        this.listViewAdapter = new PhonebookAdapter(context) {
            public void notifyDataSetChanged() {
                super.notifyDataSetChanged();
                if (PhonebookSelectActivity.this.listView.getAdapter() == this) {
                    PhonebookSelectActivity.this.listView.setFastScrollVisible(super.getItemCount() != 0);
                }
            }
        };
        this.fragmentView = new FrameLayout(context) {
            protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
                super.onLayout(changed, left, top, right, bottom);
                if (PhonebookSelectActivity.this.listView.getAdapter() != PhonebookSelectActivity.this.listViewAdapter) {
                    PhonebookSelectActivity.this.emptyView.setTranslationY((float) AndroidUtilities.dp(0.0f));
                } else if (PhonebookSelectActivity.this.emptyView.getVisibility() == 0) {
                    PhonebookSelectActivity.this.emptyView.setTranslationY((float) AndroidUtilities.dp(74.0f));
                }
            }
        };
        FrameLayout frameLayout = this.fragmentView;
        this.emptyView = new EmptyTextProgressView(context);
        this.emptyView.setShowAtCenter(true);
        this.emptyView.setText(LocaleController.getString("NoContacts", R.string.NoContacts));
        this.emptyView.showTextView();
        frameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView = new RecyclerListView(context);
        this.listView.setSectionsType(1);
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setFastScrollEnabled();
        this.listView.setEmptyView(this.emptyView);
        this.listView.setLayoutManager(new LinearLayoutManager(context, 1, false));
        this.listView.setAdapter(this.listViewAdapter);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setOnItemClickListener(new PhonebookSelectActivity$$Lambda$0(this));
        this.listView.setOnScrollListener(new OnScrollListener() {
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == 1 && PhonebookSelectActivity.this.searching && PhonebookSelectActivity.this.searchWas) {
                    AndroidUtilities.hideKeyboard(PhonebookSelectActivity.this.getParentActivity().getCurrentFocus());
                }
            }

            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        return this.fragmentView;
    }

    final /* synthetic */ void lambda$createView$1$PhonebookSelectActivity(View view, int position) {
        Contact object;
        if (this.searching && this.searchWas) {
            object = this.searchListViewAdapter.getItem(position);
        } else {
            int section = this.listViewAdapter.getSectionForPosition(position);
            int row = this.listViewAdapter.getPositionInSectionForPosition(position);
            if (row >= 0 && section >= 0) {
                object = this.listViewAdapter.getItem(section, row);
            } else {
                return;
            }
        }
        if (object != null) {
            Contact contact;
            String name;
            if (object instanceof Contact) {
                contact = object;
                if (contact.user != null) {
                    name = ContactsController.formatName(contact.user.first_name, contact.user.last_name);
                } else {
                    name = "";
                }
            } else {
                User user = (User) object;
                contact = new Contact();
                contact.first_name = user.first_name;
                contact.last_name = user.last_name;
                contact.phones.add(user.phone);
                contact.user = user;
                name = ContactsController.formatName(contact.first_name, contact.last_name);
            }
            PhonebookShareActivity activity = new PhonebookShareActivity(contact, null, null, name);
            activity.setDelegate(new PhonebookSelectActivity$$Lambda$2(this));
            presentFragment(activity);
        }
    }

    final /* synthetic */ void lambda$null$0$PhonebookSelectActivity(User user) {
        lambda$null$9$ProfileActivity();
        this.delegate.didSelectContact(user);
    }

    public void onResume() {
        super.onResume();
        if (this.listViewAdapter != null) {
            this.listViewAdapter.notifyDataSetChanged();
        }
    }

    public void onPause() {
        super.onPause();
        if (this.actionBar != null) {
            this.actionBar.closeSearchField();
        }
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.contactsDidLoad) {
            if (this.listViewAdapter != null) {
                this.listViewAdapter.notifyDataSetChanged();
            }
        } else if (id == NotificationCenter.closeChats) {
            lambda$null$9$ProfileActivity();
        }
    }

    public void setDelegate(PhonebookSelectActivityDelegate delegate) {
        this.delegate = delegate;
    }

    public ThemeDescription[] getThemeDescriptions() {
        ThemeDescriptionDelegate cellDelegate = new PhonebookSelectActivity$$Lambda$1(this);
        ThemeDescription[] themeDescriptionArr = new ThemeDescription[26];
        themeDescriptionArr[0] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite");
        themeDescriptionArr[1] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault");
        themeDescriptionArr[2] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault");
        themeDescriptionArr[3] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon");
        themeDescriptionArr[4] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle");
        themeDescriptionArr[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector");
        themeDescriptionArr[6] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCH, null, null, null, null, "actionBarDefaultSearch");
        themeDescriptionArr[7] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCHPLACEHOLDER, null, null, null, null, "actionBarDefaultSearchPlaceholder");
        themeDescriptionArr[8] = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21");
        themeDescriptionArr[9] = new ThemeDescription(this.listView, ThemeDescription.FLAG_SECTIONS, new Class[]{LetterSectionCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteGrayText4");
        themeDescriptionArr[10] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, "divider");
        themeDescriptionArr[11] = new ThemeDescription(this.emptyView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "emptyListPlaceholder");
        themeDescriptionArr[12] = new ThemeDescription(this.listView, ThemeDescription.FLAG_FASTSCROLL, null, null, null, null, "fastScrollActive");
        themeDescriptionArr[13] = new ThemeDescription(this.listView, ThemeDescription.FLAG_FASTSCROLL, null, null, null, null, "fastScrollInactive");
        themeDescriptionArr[14] = new ThemeDescription(this.listView, ThemeDescription.FLAG_FASTSCROLL, null, null, null, null, "fastScrollText");
        themeDescriptionArr[15] = new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, new String[]{"nameTextView"}, null, null, null, "windowBackgroundWhiteBlackText");
        themeDescriptionArr[16] = new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, new String[]{"statusColor"}, null, null, cellDelegate, "windowBackgroundWhiteGrayText");
        themeDescriptionArr[17] = new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, new String[]{"statusOnlineColor"}, null, null, cellDelegate, "windowBackgroundWhiteBlueText");
        themeDescriptionArr[18] = new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, null, new Drawable[]{Theme.avatar_broadcastDrawable, Theme.avatar_savedDrawable}, null, "avatar_text");
        themeDescriptionArr[19] = new ThemeDescription(null, 0, null, null, null, cellDelegate, "avatar_backgroundRed");
        themeDescriptionArr[20] = new ThemeDescription(null, 0, null, null, null, cellDelegate, "avatar_backgroundOrange");
        themeDescriptionArr[21] = new ThemeDescription(null, 0, null, null, null, cellDelegate, "avatar_backgroundViolet");
        themeDescriptionArr[22] = new ThemeDescription(null, 0, null, null, null, cellDelegate, "avatar_backgroundGreen");
        themeDescriptionArr[23] = new ThemeDescription(null, 0, null, null, null, cellDelegate, "avatar_backgroundCyan");
        themeDescriptionArr[24] = new ThemeDescription(null, 0, null, null, null, cellDelegate, "avatar_backgroundBlue");
        themeDescriptionArr[25] = new ThemeDescription(null, 0, null, null, null, cellDelegate, "avatar_backgroundPink");
        return themeDescriptionArr;
    }

    final /* synthetic */ void lambda$getThemeDescriptions$2$PhonebookSelectActivity() {
        if (this.listView != null) {
            int count = this.listView.getChildCount();
            for (int a = 0; a < count; a++) {
                View child = this.listView.getChildAt(a);
                if (child instanceof UserCell) {
                    ((UserCell) child).update(0);
                }
            }
        }
    }
}
