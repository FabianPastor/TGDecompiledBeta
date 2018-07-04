package org.telegram.ui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.C0501R;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.ContactsController.Contact;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
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
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;

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

    /* renamed from: org.telegram.ui.PhonebookSelectActivity$1 */
    class C22291 extends ActionBarMenuOnItemClick {
        C22291() {
        }

        public void onItemClick(int id) {
            if (id == -1) {
                PhonebookSelectActivity.this.finishFragment();
            }
        }
    }

    /* renamed from: org.telegram.ui.PhonebookSelectActivity$2 */
    class C22302 extends ActionBarMenuItemSearchListener {
        C22302() {
        }

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
            PhonebookSelectActivity.this.emptyView.setText(LocaleController.getString("NoContacts", C0501R.string.NoContacts));
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
    }

    /* renamed from: org.telegram.ui.PhonebookSelectActivity$6 */
    class C22356 implements OnItemClickListener {

        /* renamed from: org.telegram.ui.PhonebookSelectActivity$6$1 */
        class C22341 implements PhonebookSelectActivityDelegate {
            C22341() {
            }

            public void didSelectContact(User user) {
                PhonebookSelectActivity.this.removeSelfFromStack();
                PhonebookSelectActivity.this.delegate.didSelectContact(user);
            }
        }

        C22356() {
        }

        public void onItemClick(View view, int position) {
            Contact object;
            if (PhonebookSelectActivity.this.searching && PhonebookSelectActivity.this.searchWas) {
                object = PhonebookSelectActivity.this.searchListViewAdapter.getItem(position);
            } else {
                int section = PhonebookSelectActivity.this.listViewAdapter.getSectionForPosition(position);
                int row = PhonebookSelectActivity.this.listViewAdapter.getPositionInSectionForPosition(position);
                if (row >= 0 && section >= 0) {
                    object = PhonebookSelectActivity.this.listViewAdapter.getItem(section, row);
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
                        name = TtmlNode.ANONYMOUS_REGION_ID;
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
                activity.setDelegate(new C22341());
                PhonebookSelectActivity.this.presentFragment(activity);
            }
        }
    }

    /* renamed from: org.telegram.ui.PhonebookSelectActivity$7 */
    class C22367 extends OnScrollListener {
        C22367() {
        }

        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            if (newState == 1 && PhonebookSelectActivity.this.searching && PhonebookSelectActivity.this.searchWas) {
                AndroidUtilities.hideKeyboard(PhonebookSelectActivity.this.getParentActivity().getCurrentFocus());
            }
        }

        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
        }
    }

    /* renamed from: org.telegram.ui.PhonebookSelectActivity$8 */
    class C22378 implements ThemeDescriptionDelegate {
        C22378() {
        }

        public void didSetColor() {
            if (PhonebookSelectActivity.this.listView != null) {
                int count = PhonebookSelectActivity.this.listView.getChildCount();
                for (int a = 0; a < count; a++) {
                    View child = PhonebookSelectActivity.this.listView.getChildAt(a);
                    if (child instanceof UserCell) {
                        ((UserCell) child).update(0);
                    }
                }
            }
        }
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.contactsDidLoaded);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.closeChats);
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.contactsDidLoaded);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.closeChats);
    }

    public View createView(Context context) {
        this.searching = false;
        this.searchWas = false;
        this.actionBar.setBackButtonImage(C0501R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("SelectContact", C0501R.string.SelectContact));
        this.actionBar.setActionBarMenuOnItemClick(new C22291());
        this.actionBar.createMenu().addItem(0, (int) C0501R.drawable.ic_ab_search).setIsSearchField(true).setActionBarMenuItemSearchListener(new C22302()).getSearchField().setHint(LocaleController.getString("Search", C0501R.string.Search));
        this.searchListViewAdapter = new PhonebookSearchAdapter(context) {
            protected void onUpdateSearchResults(String query) {
                if (!TextUtils.isEmpty(query) && PhonebookSelectActivity.this.listView != null && PhonebookSelectActivity.this.listView.getAdapter() != PhonebookSelectActivity.this.searchListViewAdapter) {
                    PhonebookSelectActivity.this.listView.setAdapter(PhonebookSelectActivity.this.searchListViewAdapter);
                    PhonebookSelectActivity.this.listView.setSectionsType(0);
                    PhonebookSelectActivity.this.searchListViewAdapter.notifyDataSetChanged();
                    PhonebookSelectActivity.this.listView.setFastScrollVisible(false);
                    PhonebookSelectActivity.this.listView.setVerticalScrollBarEnabled(true);
                    PhonebookSelectActivity.this.emptyView.setText(LocaleController.getString("NoResult", C0501R.string.NoResult));
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
        this.emptyView.setText(LocaleController.getString("NoContacts", C0501R.string.NoContacts));
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
        this.listView.setOnItemClickListener(new C22356());
        this.listView.setOnScrollListener(new C22367());
        return this.fragmentView;
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
        if (id == NotificationCenter.contactsDidLoaded) {
            if (this.listViewAdapter != null) {
                this.listViewAdapter.notifyDataSetChanged();
            }
        } else if (id == NotificationCenter.closeChats) {
            removeSelfFromStack();
        }
    }

    public void setDelegate(PhonebookSelectActivityDelegate delegate) {
        this.delegate = delegate;
    }

    public ThemeDescription[] getThemeDescriptions() {
        ThemeDescriptionDelegate cellDelegate = new C22378();
        ThemeDescription[] themeDescriptionArr = new ThemeDescription[26];
        themeDescriptionArr[0] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite);
        themeDescriptionArr[1] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault);
        themeDescriptionArr[2] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault);
        themeDescriptionArr[3] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon);
        themeDescriptionArr[4] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle);
        themeDescriptionArr[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector);
        themeDescriptionArr[6] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCH, null, null, null, null, Theme.key_actionBarDefaultSearch);
        themeDescriptionArr[7] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCHPLACEHOLDER, null, null, null, null, Theme.key_actionBarDefaultSearchPlaceholder);
        themeDescriptionArr[8] = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector);
        themeDescriptionArr[9] = new ThemeDescription(this.listView, ThemeDescription.FLAG_SECTIONS, new Class[]{LetterSectionCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText4);
        themeDescriptionArr[10] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider);
        themeDescriptionArr[11] = new ThemeDescription(this.emptyView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_emptyListPlaceholder);
        themeDescriptionArr[12] = new ThemeDescription(this.listView, ThemeDescription.FLAG_FASTSCROLL, null, null, null, null, Theme.key_fastScrollActive);
        themeDescriptionArr[13] = new ThemeDescription(this.listView, ThemeDescription.FLAG_FASTSCROLL, null, null, null, null, Theme.key_fastScrollInactive);
        themeDescriptionArr[14] = new ThemeDescription(this.listView, ThemeDescription.FLAG_FASTSCROLL, null, null, null, null, Theme.key_fastScrollText);
        themeDescriptionArr[15] = new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, new String[]{"nameTextView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        themeDescriptionArr[16] = new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, new String[]{"statusColor"}, null, null, cellDelegate, Theme.key_windowBackgroundWhiteGrayText);
        themeDescriptionArr[17] = new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, new String[]{"statusOnlineColor"}, null, null, cellDelegate, Theme.key_windowBackgroundWhiteBlueText);
        themeDescriptionArr[18] = new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, null, new Drawable[]{Theme.avatar_photoDrawable, Theme.avatar_broadcastDrawable, Theme.avatar_savedDrawable}, null, Theme.key_avatar_text);
        themeDescriptionArr[19] = new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundRed);
        themeDescriptionArr[20] = new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundOrange);
        themeDescriptionArr[21] = new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundViolet);
        themeDescriptionArr[22] = new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundGreen);
        themeDescriptionArr[23] = new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundCyan);
        themeDescriptionArr[24] = new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundBlue);
        themeDescriptionArr[25] = new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundPink);
        return themeDescriptionArr;
    }
}
