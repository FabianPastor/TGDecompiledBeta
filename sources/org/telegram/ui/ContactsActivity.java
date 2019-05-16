package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.graphics.Outline;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Property;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewOutlineProvider;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.OnScrollListener;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.ContactsController.Contact;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.SecretChatHelper;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.EncryptedChat;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate;
import org.telegram.ui.Adapters.ContactsAdapter;
import org.telegram.ui.Adapters.SearchAdapter;
import org.telegram.ui.Cells.GraySectionCell;
import org.telegram.ui.Cells.LetterSectionCell;
import org.telegram.ui.Cells.ProfileSearchCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Cells.UserCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;

public class ContactsActivity extends BaseFragment implements NotificationCenterDelegate {
    private static final int search_button = 0;
    private static final int sort_button = 1;
    private boolean allowBots = true;
    private boolean allowUsernameSearch = true;
    private boolean askAboutContacts = true;
    private int channelId;
    private int chat_id;
    private boolean checkPermission = true;
    private boolean createSecretChat;
    private boolean creatingChat;
    private ContactsActivityDelegate delegate;
    private boolean destroyAfterSelect;
    private EmptyTextProgressView emptyView;
    private ImageView floatingButton;
    private FrameLayout floatingButtonContainer;
    private boolean floatingHidden;
    private AccelerateDecelerateInterpolator floatingInterpolator = new AccelerateDecelerateInterpolator();
    private SparseArray<User> ignoreUsers;
    private LinearLayoutManager layoutManager;
    private RecyclerListView listView;
    private ContactsAdapter listViewAdapter;
    private boolean needFinishFragment = true;
    private boolean needForwardCount = true;
    private boolean needPhonebook;
    private boolean onlyUsers;
    private AlertDialog permissionDialog;
    private int prevPosition;
    private int prevTop;
    private boolean returnAsResult;
    private boolean scrollUpdated;
    private SearchAdapter searchListViewAdapter;
    private boolean searchWas;
    private boolean searching;
    private String selectAlertString = null;
    private boolean sortByName;
    private ActionBarMenuItem sortItem;

    public interface ContactsActivityDelegate {
        void didSelectContact(User user, String str, ContactsActivity contactsActivity);
    }

    public ContactsActivity(Bundle bundle) {
        super(bundle);
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.contactsDidLoad);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.updateInterfaces);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.encryptedChatCreated);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.closeChats);
        this.checkPermission = UserConfig.getInstance(this.currentAccount).syncContacts;
        if (this.arguments != null) {
            this.onlyUsers = getArguments().getBoolean("onlyUsers", false);
            this.destroyAfterSelect = this.arguments.getBoolean("destroyAfterSelect", false);
            this.returnAsResult = this.arguments.getBoolean("returnAsResult", false);
            this.createSecretChat = this.arguments.getBoolean("createSecretChat", false);
            this.selectAlertString = this.arguments.getString("selectAlertString");
            this.allowUsernameSearch = this.arguments.getBoolean("allowUsernameSearch", true);
            this.needForwardCount = this.arguments.getBoolean("needForwardCount", true);
            this.allowBots = this.arguments.getBoolean("allowBots", true);
            this.channelId = this.arguments.getInt("channelId", 0);
            this.needFinishFragment = this.arguments.getBoolean("needFinishFragment", true);
            this.chat_id = this.arguments.getInt("chat_id", 0);
        } else {
            this.needPhonebook = true;
        }
        if (!(this.createSecretChat || this.returnAsResult)) {
            this.sortByName = SharedConfig.sortContactsByName;
        }
        ContactsController.getInstance(this.currentAccount).checkInviteText();
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.contactsDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.updateInterfaces);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.encryptedChatCreated);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.closeChats);
        this.delegate = null;
    }

    public View createView(Context context) {
        Context context2 = context;
        this.searching = false;
        this.searchWas = false;
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setAllowOverlayTitle(true);
        if (!this.destroyAfterSelect) {
            this.actionBar.setTitle(LocaleController.getString("Contacts", NUM));
        } else if (this.returnAsResult) {
            this.actionBar.setTitle(LocaleController.getString("SelectContact", NUM));
        } else if (this.createSecretChat) {
            this.actionBar.setTitle(LocaleController.getString("NewSecretChat", NUM));
        } else {
            this.actionBar.setTitle(LocaleController.getString("NewMessageTitle", NUM));
        }
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    ContactsActivity.this.finishFragment();
                    return;
                }
                int i2 = 1;
                if (i == 1) {
                    SharedConfig.toggleSortContactsByName();
                    ContactsActivity.this.sortByName = SharedConfig.sortContactsByName;
                    ContactsAdapter access$100 = ContactsActivity.this.listViewAdapter;
                    if (!ContactsActivity.this.sortByName) {
                        i2 = 2;
                    }
                    access$100.setSortType(i2);
                    ContactsActivity.this.sortItem.setIcon(ContactsActivity.this.sortByName ? NUM : NUM);
                }
            }
        });
        ActionBarMenu createMenu = this.actionBar.createMenu();
        ActionBarMenuItem actionBarMenuItemSearchListener = createMenu.addItem(0, NUM).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItemSearchListener() {
            public void onSearchExpand() {
                ContactsActivity.this.searching = true;
                if (ContactsActivity.this.floatingButtonContainer != null) {
                    ContactsActivity.this.floatingButtonContainer.setVisibility(8);
                }
                if (ContactsActivity.this.sortItem != null) {
                    ContactsActivity.this.sortItem.setVisibility(8);
                }
            }

            public void onSearchCollapse() {
                ContactsActivity.this.searchListViewAdapter.searchDialogs(null);
                ContactsActivity.this.searching = false;
                ContactsActivity.this.searchWas = false;
                ContactsActivity.this.listView.setAdapter(ContactsActivity.this.listViewAdapter);
                ContactsActivity.this.listView.setSectionsType(1);
                ContactsActivity.this.listViewAdapter.notifyDataSetChanged();
                ContactsActivity.this.listView.setFastScrollVisible(true);
                ContactsActivity.this.listView.setVerticalScrollBarEnabled(false);
                ContactsActivity.this.listView.setEmptyView(null);
                ContactsActivity.this.emptyView.setText(LocaleController.getString("NoContacts", NUM));
                if (ContactsActivity.this.floatingButtonContainer != null) {
                    ContactsActivity.this.floatingButtonContainer.setVisibility(0);
                    ContactsActivity.this.floatingHidden = true;
                    ContactsActivity.this.floatingButtonContainer.setTranslationY((float) AndroidUtilities.dp(100.0f));
                    ContactsActivity.this.hideFloatingButton(false);
                }
                if (ContactsActivity.this.sortItem != null) {
                    ContactsActivity.this.sortItem.setVisibility(0);
                }
            }

            public void onTextChanged(EditText editText) {
                if (ContactsActivity.this.searchListViewAdapter != null) {
                    String obj = editText.getText().toString();
                    if (obj.length() != 0) {
                        ContactsActivity.this.searchWas = true;
                        if (ContactsActivity.this.listView != null) {
                            ContactsActivity.this.listView.setAdapter(ContactsActivity.this.searchListViewAdapter);
                            ContactsActivity.this.listView.setSectionsType(0);
                            ContactsActivity.this.searchListViewAdapter.notifyDataSetChanged();
                            ContactsActivity.this.listView.setFastScrollVisible(false);
                            ContactsActivity.this.listView.setVerticalScrollBarEnabled(true);
                        }
                        if (ContactsActivity.this.emptyView != null) {
                            ContactsActivity.this.listView.setEmptyView(ContactsActivity.this.emptyView);
                            ContactsActivity.this.emptyView.setText(LocaleController.getString("NoResult", NUM));
                        }
                    }
                    ContactsActivity.this.searchListViewAdapter.searchDialogs(obj);
                }
            }
        });
        String str = "Search";
        actionBarMenuItemSearchListener.setSearchFieldHint(LocaleController.getString(str, NUM));
        actionBarMenuItemSearchListener.setContentDescription(LocaleController.getString(str, NUM));
        if (!(this.createSecretChat || this.returnAsResult)) {
            this.sortItem = createMenu.addItem(1, this.sortByName ? NUM : NUM);
            this.sortItem.setContentDescription(LocaleController.getString("AccDescrContactSorting", NUM));
        }
        this.searchListViewAdapter = new SearchAdapter(context, this.ignoreUsers, this.allowUsernameSearch, false, false, this.allowBots, 0);
        int i = 3;
        boolean canUserDoAdminAction = this.chat_id != 0 ? ChatObject.canUserDoAdminAction(MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.chat_id)), 3) : false;
        Context context3 = context2;
        this.listViewAdapter = new ContactsAdapter(context, this.onlyUsers, this.needPhonebook, this.ignoreUsers, canUserDoAdminAction) {
            public void notifyDataSetChanged() {
                super.notifyDataSetChanged();
                if (ContactsActivity.this.listView != null && ContactsActivity.this.listView.getAdapter() == this) {
                    int itemCount = super.getItemCount();
                    boolean z = true;
                    int i = 8;
                    EmptyTextProgressView access$800;
                    RecyclerListView access$700;
                    if (ContactsActivity.this.needPhonebook) {
                        access$800 = ContactsActivity.this.emptyView;
                        if (itemCount == 2) {
                            i = 0;
                        }
                        access$800.setVisibility(i);
                        access$700 = ContactsActivity.this.listView;
                        if (itemCount == 2) {
                            z = false;
                        }
                        access$700.setFastScrollVisible(z);
                        return;
                    }
                    access$800 = ContactsActivity.this.emptyView;
                    if (itemCount == 0) {
                        i = 0;
                    }
                    access$800.setVisibility(i);
                    access$700 = ContactsActivity.this.listView;
                    if (itemCount == 0) {
                        z = false;
                    }
                    access$700.setFastScrollVisible(z);
                }
            }
        };
        ContactsAdapter contactsAdapter = this.listViewAdapter;
        int i2 = this.sortItem != null ? this.sortByName ? 1 : 2 : 0;
        contactsAdapter.setSortType(i2);
        this.fragmentView = new FrameLayout(context3) {
            /* Access modifiers changed, original: protected */
            public void onLayout(boolean z, int i, int i2, int i3, int i4) {
                super.onLayout(z, i, i2, i3, i4);
                if (ContactsActivity.this.listView.getAdapter() != ContactsActivity.this.listViewAdapter) {
                    ContactsActivity.this.emptyView.setTranslationY((float) AndroidUtilities.dp(0.0f));
                } else if (ContactsActivity.this.emptyView.getVisibility() == 0) {
                    ContactsActivity.this.emptyView.setTranslationY((float) AndroidUtilities.dp(74.0f));
                }
            }
        };
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        this.emptyView = new EmptyTextProgressView(context3);
        this.emptyView.setShowAtCenter(true);
        this.emptyView.setText(LocaleController.getString("NoContacts", NUM));
        this.emptyView.showTextView();
        frameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView = new RecyclerListView(context3);
        this.listView.setSectionsType(1);
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setFastScrollEnabled();
        RecyclerListView recyclerListView = this.listView;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context3, 1, false);
        this.layoutManager = linearLayoutManager;
        recyclerListView.setLayoutManager(linearLayoutManager);
        this.listView.setAdapter(this.listViewAdapter);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setOnItemClickListener(new -$$Lambda$ContactsActivity$ASDbl9eX6i4wml3tNp0AYXJhYt8(this, canUserDoAdminAction));
        this.listView.setOnScrollListener(new OnScrollListener() {
            private boolean scrollingManually;

            public void onScrollStateChanged(RecyclerView recyclerView, int i) {
                if (i == 1) {
                    if (ContactsActivity.this.searching && ContactsActivity.this.searchWas) {
                        AndroidUtilities.hideKeyboard(ContactsActivity.this.getParentActivity().getCurrentFocus());
                    }
                    this.scrollingManually = true;
                    return;
                }
                this.scrollingManually = false;
            }

            /* JADX WARNING: Missing block: B:15:0x004f, code skipped:
            if (java.lang.Math.abs(r0) > 1) goto L_0x005d;
     */
            public void onScrolled(androidx.recyclerview.widget.RecyclerView r4, int r5, int r6) {
                /*
                r3 = this;
                super.onScrolled(r4, r5, r6);
                r5 = org.telegram.ui.ContactsActivity.this;
                r5 = r5.floatingButtonContainer;
                if (r5 == 0) goto L_0x0084;
            L_0x000b:
                r5 = org.telegram.ui.ContactsActivity.this;
                r5 = r5.floatingButtonContainer;
                r5 = r5.getVisibility();
                r6 = 8;
                if (r5 == r6) goto L_0x0084;
            L_0x0019:
                r5 = org.telegram.ui.ContactsActivity.this;
                r5 = r5.layoutManager;
                r5 = r5.findFirstVisibleItemPosition();
                r6 = 0;
                r4 = r4.getChildAt(r6);
                if (r4 == 0) goto L_0x002f;
            L_0x002a:
                r4 = r4.getTop();
                goto L_0x0030;
            L_0x002f:
                r4 = 0;
            L_0x0030:
                r0 = org.telegram.ui.ContactsActivity.this;
                r0 = r0.prevPosition;
                r1 = 1;
                if (r0 != r5) goto L_0x0052;
            L_0x0039:
                r0 = org.telegram.ui.ContactsActivity.this;
                r0 = r0.prevTop;
                r0 = r0 - r4;
                r2 = org.telegram.ui.ContactsActivity.this;
                r2 = r2.prevTop;
                if (r4 >= r2) goto L_0x004a;
            L_0x0048:
                r2 = 1;
                goto L_0x004b;
            L_0x004a:
                r2 = 0;
            L_0x004b:
                r0 = java.lang.Math.abs(r0);
                if (r0 <= r1) goto L_0x005e;
            L_0x0051:
                goto L_0x005d;
            L_0x0052:
                r0 = org.telegram.ui.ContactsActivity.this;
                r0 = r0.prevPosition;
                if (r5 <= r0) goto L_0x005c;
            L_0x005a:
                r2 = 1;
                goto L_0x005d;
            L_0x005c:
                r2 = 0;
            L_0x005d:
                r6 = 1;
            L_0x005e:
                if (r6 == 0) goto L_0x0075;
            L_0x0060:
                r6 = org.telegram.ui.ContactsActivity.this;
                r6 = r6.scrollUpdated;
                if (r6 == 0) goto L_0x0075;
            L_0x0068:
                if (r2 != 0) goto L_0x0070;
            L_0x006a:
                if (r2 != 0) goto L_0x0075;
            L_0x006c:
                r6 = r3.scrollingManually;
                if (r6 == 0) goto L_0x0075;
            L_0x0070:
                r6 = org.telegram.ui.ContactsActivity.this;
                r6.hideFloatingButton(r2);
            L_0x0075:
                r6 = org.telegram.ui.ContactsActivity.this;
                r6.prevPosition = r5;
                r5 = org.telegram.ui.ContactsActivity.this;
                r5.prevTop = r4;
                r4 = org.telegram.ui.ContactsActivity.this;
                r4.scrollUpdated = r1;
            L_0x0084:
                return;
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ContactsActivity$AnonymousClass5.onScrolled(androidx.recyclerview.widget.RecyclerView, int, int):void");
            }
        });
        if (!(this.createSecretChat || this.returnAsResult)) {
            this.floatingButtonContainer = new FrameLayout(context3);
            FrameLayout frameLayout2 = this.floatingButtonContainer;
            int i3 = 56;
            int i4 = (VERSION.SDK_INT >= 21 ? 56 : 60) + 20;
            float f = (float) ((VERSION.SDK_INT >= 21 ? 56 : 60) + 14);
            if (!LocaleController.isRTL) {
                i = 5;
            }
            frameLayout.addView(frameLayout2, LayoutHelper.createFrame(i4, f, i | 80, LocaleController.isRTL ? 4.0f : 0.0f, 0.0f, LocaleController.isRTL ? 0.0f : 4.0f, 0.0f));
            this.floatingButtonContainer.setOnClickListener(new -$$Lambda$ContactsActivity$XQCfC9ukjnkgkb2J-v7QLg6mf6E(this));
            this.floatingButton = new ImageView(context3);
            this.floatingButton.setScaleType(ScaleType.CENTER);
            Drawable createSimpleSelectorCircleDrawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56.0f), Theme.getColor("chats_actionBackground"), Theme.getColor("chats_actionPressedBackground"));
            if (VERSION.SDK_INT < 21) {
                Drawable mutate = context.getResources().getDrawable(NUM).mutate();
                mutate.setColorFilter(new PorterDuffColorFilter(-16777216, Mode.MULTIPLY));
                Drawable combinedDrawable = new CombinedDrawable(mutate, createSimpleSelectorCircleDrawable, 0, 0);
                combinedDrawable.setIconSize(AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
                createSimpleSelectorCircleDrawable = combinedDrawable;
            }
            this.floatingButton.setBackgroundDrawable(createSimpleSelectorCircleDrawable);
            this.floatingButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chats_actionIcon"), Mode.MULTIPLY));
            this.floatingButton.setImageResource(NUM);
            this.floatingButtonContainer.setContentDescription(LocaleController.getString("CreateNewContact", NUM));
            if (VERSION.SDK_INT >= 21) {
                StateListAnimator stateListAnimator = new StateListAnimator();
                stateListAnimator.addState(new int[]{16842919}, ObjectAnimator.ofFloat(this.floatingButton, View.TRANSLATION_Z, new float[]{(float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(4.0f)}).setDuration(200));
                stateListAnimator.addState(new int[0], ObjectAnimator.ofFloat(this.floatingButton, View.TRANSLATION_Z, new float[]{(float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(2.0f)}).setDuration(200));
                this.floatingButton.setStateListAnimator(stateListAnimator);
                this.floatingButton.setOutlineProvider(new ViewOutlineProvider() {
                    @SuppressLint({"NewApi"})
                    public void getOutline(View view, Outline outline) {
                        outline.setOval(0, 0, AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
                    }
                });
            }
            frameLayout = this.floatingButtonContainer;
            ImageView imageView = this.floatingButton;
            i = VERSION.SDK_INT >= 21 ? 56 : 60;
            if (VERSION.SDK_INT < 21) {
                i3 = 60;
            }
            frameLayout.addView(imageView, LayoutHelper.createFrame(i, (float) i3, 51, 10.0f, 0.0f, 10.0f, 0.0f));
        }
        return this.fragmentView;
    }

    public /* synthetic */ void lambda$createView$1$ContactsActivity(boolean z, View view, int i) {
        String str = "user_id";
        User user;
        SparseArray sparseArray;
        Bundle bundle;
        if (this.searching && this.searchWas) {
            user = (User) this.searchListViewAdapter.getItem(i);
            if (user != null) {
                if (this.searchListViewAdapter.isGlobalSearch(i)) {
                    ArrayList arrayList = new ArrayList();
                    arrayList.add(user);
                    MessagesController.getInstance(this.currentAccount).putUsers(arrayList, false);
                    MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(arrayList, null, false, true);
                }
                if (this.returnAsResult) {
                    sparseArray = this.ignoreUsers;
                    if (sparseArray == null || sparseArray.indexOfKey(user.id) < 0) {
                        didSelectResult(user, true, null);
                    } else {
                        return;
                    }
                } else if (!this.createSecretChat) {
                    bundle = new Bundle();
                    bundle.putInt(str, user.id);
                    if (MessagesController.getInstance(this.currentAccount).checkCanOpenChat(bundle, this)) {
                        presentFragment(new ChatActivity(bundle), true);
                    }
                } else if (user.id != UserConfig.getInstance(this.currentAccount).getClientUserId()) {
                    this.creatingChat = true;
                    SecretChatHelper.getInstance(this.currentAccount).startSecretChat(getParentActivity(), user);
                } else {
                    return;
                }
            }
            return;
        }
        int sectionForPosition = this.listViewAdapter.getSectionForPosition(i);
        i = this.listViewAdapter.getPositionInSectionForPosition(i);
        if (i >= 0 && sectionForPosition >= 0) {
            if ((this.onlyUsers && (this.chat_id == 0 || !z)) || sectionForPosition != 0) {
                Object item = this.listViewAdapter.getItem(sectionForPosition, i);
                if (item instanceof User) {
                    user = (User) item;
                    if (this.returnAsResult) {
                        sparseArray = this.ignoreUsers;
                        if (sparseArray == null || sparseArray.indexOfKey(user.id) < 0) {
                            didSelectResult(user, true, null);
                        }
                    } else if (this.createSecretChat) {
                        this.creatingChat = true;
                        SecretChatHelper.getInstance(this.currentAccount).startSecretChat(getParentActivity(), user);
                    } else {
                        bundle = new Bundle();
                        bundle.putInt(str, user.id);
                        if (MessagesController.getInstance(this.currentAccount).checkCanOpenChat(bundle, this)) {
                            presentFragment(new ChatActivity(bundle), true);
                        }
                    }
                } else if (item instanceof Contact) {
                    Contact contact = (Contact) item;
                    String str2 = !contact.phones.isEmpty() ? (String) contact.phones.get(0) : null;
                    if (!(str2 == null || getParentActivity() == null)) {
                        Builder builder = new Builder(getParentActivity());
                        builder.setMessage(LocaleController.getString("InviteUser", NUM));
                        builder.setTitle(LocaleController.getString("AppName", NUM));
                        builder.setPositiveButton(LocaleController.getString("OK", NUM), new -$$Lambda$ContactsActivity$UtCNWtIF8nw25DCjjOFgiXVoPRQ(this, str2));
                        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
                        showDialog(builder.create());
                    }
                }
            } else if (!this.needPhonebook) {
                int i2 = this.chat_id;
                Bundle bundle2;
                if (i2 != 0) {
                    if (i == 0) {
                        presentFragment(new GroupInviteActivity(i2));
                    }
                } else if (i == 0) {
                    bundle2 = new Bundle();
                    bundle2.putBoolean("showFabButton", true);
                    presentFragment(new GroupCreateActivity(bundle2), false);
                } else if (i == 1) {
                    bundle2 = new Bundle();
                    bundle2.putBoolean("onlyUsers", true);
                    bundle2.putBoolean("destroyAfterSelect", true);
                    bundle2.putBoolean("createSecretChat", true);
                    bundle2.putBoolean("allowBots", false);
                    presentFragment(new ContactsActivity(bundle2), false);
                } else if (i == 2) {
                    SharedPreferences globalMainSettings = MessagesController.getGlobalMainSettings();
                    String str3 = "channel_intro";
                    if (BuildVars.DEBUG_VERSION || !globalMainSettings.getBoolean(str3, false)) {
                        presentFragment(new ChannelIntroActivity());
                        globalMainSettings.edit().putBoolean(str3, true).commit();
                    } else {
                        bundle2 = new Bundle();
                        bundle2.putInt("step", 0);
                        presentFragment(new ChannelCreateActivity(bundle2));
                    }
                }
            } else if (i == 0) {
                presentFragment(new InviteContactsActivity());
            }
        }
    }

    public /* synthetic */ void lambda$null$0$ContactsActivity(String str, DialogInterface dialogInterface, int i) {
        try {
            Intent intent = new Intent("android.intent.action.VIEW", Uri.fromParts("sms", str, null));
            intent.putExtra("sms_body", ContactsController.getInstance(this.currentAccount).getInviteText(1));
            getParentActivity().startActivityForResult(intent, 500);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public /* synthetic */ void lambda$createView$2$ContactsActivity(View view) {
        presentFragment(new NewContactActivity());
    }

    private void didSelectResult(User user, boolean z, String str) {
        if (!z || this.selectAlertString == null) {
            ContactsActivityDelegate contactsActivityDelegate = this.delegate;
            if (contactsActivityDelegate != null) {
                contactsActivityDelegate.didSelectContact(user, str, this);
                this.delegate = null;
            }
            if (this.needFinishFragment) {
                finishFragment();
            }
        } else if (getParentActivity() != null) {
            EditText editText;
            String str2 = "Cancel";
            String str3 = "OK";
            String str4 = "AppName";
            if (user.bot) {
                if (user.bot_nochats) {
                    try {
                        Toast.makeText(getParentActivity(), LocaleController.getString("BotCantJoinGroups", NUM), 0).show();
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                    return;
                } else if (this.channelId != 0) {
                    Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.channelId));
                    Builder builder = new Builder(getParentActivity());
                    if (ChatObject.canAddAdmins(chat)) {
                        builder.setTitle(LocaleController.getString(str4, NUM));
                        builder.setMessage(LocaleController.getString("AddBotAsAdmin", NUM));
                        builder.setPositiveButton(LocaleController.getString("MakeAdmin", NUM), new -$$Lambda$ContactsActivity$mmdvMVULktgHXYl_8FoV1fI5DjI(this, user, str));
                        builder.setNegativeButton(LocaleController.getString(str2, NUM), null);
                    } else {
                        builder.setMessage(LocaleController.getString("CantAddBotAsAdmin", NUM));
                        builder.setPositiveButton(LocaleController.getString(str3, NUM), null);
                    }
                    showDialog(builder.create());
                    return;
                }
            }
            Builder builder2 = new Builder(getParentActivity());
            builder2.setTitle(LocaleController.getString(str4, NUM));
            CharSequence formatStringSimple = LocaleController.formatStringSimple(this.selectAlertString, UserObject.getUserName(user));
            if (user.bot || !this.needForwardCount) {
                editText = null;
            } else {
                formatStringSimple = String.format("%s\n\n%s", new Object[]{formatStringSimple, LocaleController.getString("AddToTheGroupForwardCount", NUM)});
                editText = new EditText(getParentActivity());
                editText.setTextSize(1, 18.0f);
                editText.setText("50");
                editText.setTextColor(Theme.getColor("dialogTextBlack"));
                editText.setGravity(17);
                editText.setInputType(2);
                editText.setImeOptions(6);
                editText.setBackgroundDrawable(Theme.createEditTextDrawable(getParentActivity(), true));
                editText.addTextChangedListener(new TextWatcher() {
                    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                    }

                    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                    }

                    public void afterTextChanged(Editable editable) {
                        String str = "";
                        try {
                            String obj = editable.toString();
                            if (obj.length() != 0) {
                                int intValue = Utilities.parseInt(obj).intValue();
                                if (intValue < 0) {
                                    editText.setText("0");
                                    editText.setSelection(editText.length());
                                } else if (intValue > 300) {
                                    editText.setText("300");
                                    editText.setSelection(editText.length());
                                } else {
                                    StringBuilder stringBuilder = new StringBuilder();
                                    stringBuilder.append(str);
                                    stringBuilder.append(intValue);
                                    if (!obj.equals(stringBuilder.toString())) {
                                        EditText editText = editText;
                                        stringBuilder = new StringBuilder();
                                        stringBuilder.append(str);
                                        stringBuilder.append(intValue);
                                        editText.setText(stringBuilder.toString());
                                        editText.setSelection(editText.length());
                                    }
                                }
                            }
                        } catch (Exception e) {
                            FileLog.e(e);
                        }
                    }
                });
                builder2.setView(editText);
            }
            builder2.setMessage(formatStringSimple);
            builder2.setPositiveButton(LocaleController.getString(str3, NUM), new -$$Lambda$ContactsActivity$YV_dxfbBQkZGaBy_yZbP6YhG1n0(this, user, editText));
            builder2.setNegativeButton(LocaleController.getString(str2, NUM), null);
            showDialog(builder2.create());
            if (editText != null) {
                MarginLayoutParams marginLayoutParams = (MarginLayoutParams) editText.getLayoutParams();
                if (marginLayoutParams != null) {
                    if (marginLayoutParams instanceof LayoutParams) {
                        ((LayoutParams) marginLayoutParams).gravity = 1;
                    }
                    int dp = AndroidUtilities.dp(24.0f);
                    marginLayoutParams.leftMargin = dp;
                    marginLayoutParams.rightMargin = dp;
                    marginLayoutParams.height = AndroidUtilities.dp(36.0f);
                    editText.setLayoutParams(marginLayoutParams);
                }
                editText.setSelection(editText.getText().length());
            }
        }
    }

    public /* synthetic */ void lambda$didSelectResult$3$ContactsActivity(User user, String str, DialogInterface dialogInterface, int i) {
        ContactsActivityDelegate contactsActivityDelegate = this.delegate;
        if (contactsActivityDelegate != null) {
            contactsActivityDelegate.didSelectContact(user, str, this);
            this.delegate = null;
        }
    }

    public /* synthetic */ void lambda$didSelectResult$4$ContactsActivity(User user, EditText editText, DialogInterface dialogInterface, int i) {
        didSelectResult(user, false, editText != null ? editText.getText().toString() : "0");
    }

    public void onResume() {
        super.onResume();
        ContactsAdapter contactsAdapter = this.listViewAdapter;
        if (contactsAdapter != null) {
            contactsAdapter.notifyDataSetChanged();
        }
        if (this.checkPermission && VERSION.SDK_INT >= 23) {
            Activity parentActivity = getParentActivity();
            if (parentActivity != null) {
                this.checkPermission = false;
                String str = "android.permission.READ_CONTACTS";
                if (parentActivity.checkSelfPermission(str) == 0) {
                    return;
                }
                if (parentActivity.shouldShowRequestPermissionRationale(str)) {
                    AlertDialog create = AlertsCreator.createContactsPermissionDialog(parentActivity, new -$$Lambda$ContactsActivity$xFVgeouE3JWG-nldltsKWVnF4Hg(this)).create();
                    this.permissionDialog = create;
                    showDialog(create);
                    return;
                }
                askForPermissons(true);
            }
        }
    }

    public /* synthetic */ void lambda$onResume$5$ContactsActivity(int i) {
        this.askAboutContacts = i != 0;
        if (i != 0) {
            askForPermissons(false);
        }
    }

    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        FrameLayout frameLayout = this.floatingButtonContainer;
        if (frameLayout != null) {
            frameLayout.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
                public void onGlobalLayout() {
                    ContactsActivity.this.floatingButtonContainer.setTranslationY((float) (ContactsActivity.this.floatingHidden ? AndroidUtilities.dp(100.0f) : 0));
                    ContactsActivity.this.floatingButtonContainer.setClickable(ContactsActivity.this.floatingHidden ^ 1);
                    if (ContactsActivity.this.floatingButtonContainer != null) {
                        ContactsActivity.this.floatingButtonContainer.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                }
            });
        }
    }

    /* Access modifiers changed, original: protected */
    public void onDialogDismiss(Dialog dialog) {
        super.onDialogDismiss(dialog);
        Dialog dialog2 = this.permissionDialog;
        if (dialog2 != null && dialog == dialog2 && getParentActivity() != null && this.askAboutContacts) {
            askForPermissons(false);
        }
    }

    @TargetApi(23)
    private void askForPermissons(boolean z) {
        Activity parentActivity = getParentActivity();
        if (parentActivity != null && UserConfig.getInstance(this.currentAccount).syncContacts) {
            String str = "android.permission.READ_CONTACTS";
            if (parentActivity.checkSelfPermission(str) != 0) {
                if (z && this.askAboutContacts) {
                    showDialog(AlertsCreator.createContactsPermissionDialog(parentActivity, new -$$Lambda$ContactsActivity$mwsX5pHa6b8khBUhCBIBphXHFEY(this)).create());
                    return;
                }
                ArrayList arrayList = new ArrayList();
                arrayList.add(str);
                arrayList.add("android.permission.WRITE_CONTACTS");
                arrayList.add("android.permission.GET_ACCOUNTS");
                parentActivity.requestPermissions((String[]) arrayList.toArray(new String[0]), 1);
            }
        }
    }

    public /* synthetic */ void lambda$askForPermissons$6$ContactsActivity(int i) {
        this.askAboutContacts = i != 0;
        if (i != 0) {
            askForPermissons(false);
        }
    }

    public void onRequestPermissionsResultFragment(int i, String[] strArr, int[] iArr) {
        if (i == 1) {
            for (int i2 = 0; i2 < strArr.length; i2++) {
                if (iArr.length > i2) {
                    String str = strArr[i2];
                    Object obj = -1;
                    if (str.hashCode() == NUM && str.equals("android.permission.READ_CONTACTS")) {
                        obj = null;
                    }
                    if (obj == null) {
                        if (iArr[i2] == 0) {
                            ContactsController.getInstance(this.currentAccount).forceImportContacts();
                        } else {
                            Editor edit = MessagesController.getGlobalNotificationsSettings().edit();
                            this.askAboutContacts = false;
                            edit.putBoolean("askAboutContacts", false).commit();
                        }
                    }
                }
            }
        }
    }

    public void onPause() {
        super.onPause();
        ActionBar actionBar = this.actionBar;
        if (actionBar != null) {
            actionBar.closeSearchField();
        }
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        ContactsAdapter contactsAdapter;
        if (i == NotificationCenter.contactsDidLoad) {
            contactsAdapter = this.listViewAdapter;
            if (contactsAdapter != null) {
                contactsAdapter.notifyDataSetChanged();
            }
        } else if (i == NotificationCenter.updateInterfaces) {
            i = ((Integer) objArr[0]).intValue();
            if (!((i & 2) == 0 && (i & 1) == 0 && (i & 4) == 0)) {
                updateVisibleRows(i);
            }
            if ((i & 4) != 0 && !this.sortByName) {
                contactsAdapter = this.listViewAdapter;
                if (contactsAdapter != null) {
                    contactsAdapter.sortOnlineContacts();
                }
            }
        } else if (i == NotificationCenter.encryptedChatCreated) {
            if (this.createSecretChat && this.creatingChat) {
                EncryptedChat encryptedChat = (EncryptedChat) objArr[0];
                Bundle bundle = new Bundle();
                bundle.putInt("enc_id", encryptedChat.id);
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                presentFragment(new ChatActivity(bundle), true);
            }
        } else if (i == NotificationCenter.closeChats && !this.creatingChat) {
            removeSelfFromStack();
        }
    }

    private void updateVisibleRows(int i) {
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            int childCount = recyclerListView.getChildCount();
            for (int i2 = 0; i2 < childCount; i2++) {
                View childAt = this.listView.getChildAt(i2);
                if (childAt instanceof UserCell) {
                    ((UserCell) childAt).update(i);
                }
            }
        }
    }

    private void hideFloatingButton(boolean z) {
        if (this.floatingHidden != z) {
            this.floatingHidden = z;
            AnimatorSet animatorSet = new AnimatorSet();
            Animator[] animatorArr = new Animator[1];
            FrameLayout frameLayout = this.floatingButtonContainer;
            Property property = View.TRANSLATION_Y;
            float[] fArr = new float[1];
            fArr[0] = (float) (this.floatingHidden ? AndroidUtilities.dp(100.0f) : 0);
            animatorArr[0] = ObjectAnimator.ofFloat(frameLayout, property, fArr);
            animatorSet.playTogether(animatorArr);
            animatorSet.setDuration(300);
            animatorSet.setInterpolator(this.floatingInterpolator);
            this.floatingButtonContainer.setClickable(z ^ 1);
            animatorSet.start();
        }
    }

    public void setDelegate(ContactsActivityDelegate contactsActivityDelegate) {
        this.delegate = contactsActivityDelegate;
    }

    public void setIgnoreUsers(SparseArray<User> sparseArray) {
        this.ignoreUsers = sparseArray;
    }

    public ThemeDescription[] getThemeDescriptions() {
        -$$Lambda$ContactsActivity$mX2AyzEZkfKNXN64OUntWgWNyBY -__lambda_contactsactivity_mx2ayzezkfknxn64ountwgwnyby = new -$$Lambda$ContactsActivity$mX2AyzEZkfKNXN64OUntWgWNyBY(this);
        ThemeDescription[] themeDescriptionArr = new ThemeDescription[39];
        themeDescriptionArr[0] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite");
        themeDescriptionArr[1] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault");
        themeDescriptionArr[2] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault");
        themeDescriptionArr[3] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon");
        themeDescriptionArr[4] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle");
        themeDescriptionArr[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector");
        themeDescriptionArr[6] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCH, null, null, null, null, "actionBarDefaultSearch");
        themeDescriptionArr[7] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCHPLACEHOLDER, null, null, null, null, "actionBarDefaultSearchPlaceholder");
        themeDescriptionArr[8] = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21");
        View view = this.listView;
        int i = ThemeDescription.FLAG_SECTIONS;
        Class[] clsArr = new Class[]{LetterSectionCell.class};
        String[] strArr = new String[1];
        strArr[0] = "textView";
        themeDescriptionArr[9] = new ThemeDescription(view, i, clsArr, strArr, null, null, null, "windowBackgroundWhiteGrayText4");
        themeDescriptionArr[10] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, "divider");
        themeDescriptionArr[11] = new ThemeDescription(this.emptyView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "emptyListPlaceholder");
        themeDescriptionArr[12] = new ThemeDescription(this.listView, ThemeDescription.FLAG_FASTSCROLL, null, null, null, null, "fastScrollActive");
        themeDescriptionArr[13] = new ThemeDescription(this.listView, ThemeDescription.FLAG_FASTSCROLL, null, null, null, null, "fastScrollInactive");
        themeDescriptionArr[14] = new ThemeDescription(this.listView, ThemeDescription.FLAG_FASTSCROLL, null, null, null, null, "fastScrollText");
        themeDescriptionArr[15] = new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, new String[]{"nameTextView"}, null, null, null, "windowBackgroundWhiteBlackText");
        ThemeDescriptionDelegate themeDescriptionDelegate = -__lambda_contactsactivity_mx2ayzezkfknxn64ountwgwnyby;
        themeDescriptionArr[16] = new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, new String[]{"statusColor"}, null, null, themeDescriptionDelegate, "windowBackgroundWhiteGrayText");
        themeDescriptionArr[17] = new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, new String[]{"statusOnlineColor"}, null, null, themeDescriptionDelegate, "windowBackgroundWhiteBlueText");
        themeDescriptionArr[18] = new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, null, new Drawable[]{Theme.avatar_broadcastDrawable, Theme.avatar_savedDrawable}, null, "avatar_text");
        -$$Lambda$ContactsActivity$mX2AyzEZkfKNXN64OUntWgWNyBY -__lambda_contactsactivity_mx2ayzezkfknxn64ountwgwnyby2 = -__lambda_contactsactivity_mx2ayzezkfknxn64ountwgwnyby;
        themeDescriptionArr[19] = new ThemeDescription(null, 0, null, null, null, -__lambda_contactsactivity_mx2ayzezkfknxn64ountwgwnyby2, "avatar_backgroundRed");
        themeDescriptionArr[20] = new ThemeDescription(null, 0, null, null, null, -__lambda_contactsactivity_mx2ayzezkfknxn64ountwgwnyby2, "avatar_backgroundOrange");
        themeDescriptionArr[21] = new ThemeDescription(null, 0, null, null, null, -__lambda_contactsactivity_mx2ayzezkfknxn64ountwgwnyby2, "avatar_backgroundViolet");
        themeDescriptionArr[22] = new ThemeDescription(null, 0, null, null, null, -__lambda_contactsactivity_mx2ayzezkfknxn64ountwgwnyby2, "avatar_backgroundGreen");
        themeDescriptionArr[23] = new ThemeDescription(null, 0, null, null, null, -__lambda_contactsactivity_mx2ayzezkfknxn64ountwgwnyby2, "avatar_backgroundCyan");
        themeDescriptionArr[24] = new ThemeDescription(null, 0, null, null, null, -__lambda_contactsactivity_mx2ayzezkfknxn64ountwgwnyby2, "avatar_backgroundBlue");
        themeDescriptionArr[25] = new ThemeDescription(null, 0, null, null, null, -__lambda_contactsactivity_mx2ayzezkfknxn64ountwgwnyby2, "avatar_backgroundPink");
        themeDescriptionArr[26] = new ThemeDescription(this.listView, 0, new Class[]{TextCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText");
        themeDescriptionArr[27] = new ThemeDescription(this.listView, 0, new Class[]{TextCell.class}, new String[]{"imageView"}, null, null, null, "windowBackgroundWhiteGrayIcon");
        themeDescriptionArr[28] = new ThemeDescription(this.floatingButton, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, "chats_actionIcon");
        themeDescriptionArr[29] = new ThemeDescription(this.floatingButton, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "chats_actionBackground");
        themeDescriptionArr[30] = new ThemeDescription(this.floatingButton, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, "chats_actionPressedBackground");
        themeDescriptionArr[31] = new ThemeDescription(this.listView, 0, new Class[]{GraySectionCell.class}, new String[]{"textView"}, null, null, null, "key_graySectionText");
        themeDescriptionArr[32] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{GraySectionCell.class}, null, null, null, "graySection");
        themeDescriptionArr[33] = new ThemeDescription(this.listView, 0, new Class[]{ProfileSearchCell.class}, null, new Drawable[]{Theme.dialogs_groupDrawable, Theme.dialogs_broadcastDrawable, Theme.dialogs_botDrawable}, null, "chats_nameIcon");
        themeDescriptionArr[34] = new ThemeDescription(this.listView, 0, new Class[]{ProfileSearchCell.class}, null, new Drawable[]{Theme.dialogs_verifiedCheckDrawable}, null, "chats_verifiedCheck");
        themeDescriptionArr[35] = new ThemeDescription(this.listView, 0, new Class[]{ProfileSearchCell.class}, null, new Drawable[]{Theme.dialogs_verifiedDrawable}, null, "chats_verifiedBackground");
        themeDescriptionArr[36] = new ThemeDescription(this.listView, 0, new Class[]{ProfileSearchCell.class}, Theme.dialogs_offlinePaint, null, null, "windowBackgroundWhiteGrayText3");
        themeDescriptionArr[37] = new ThemeDescription(this.listView, 0, new Class[]{ProfileSearchCell.class}, Theme.dialogs_onlinePaint, null, null, "windowBackgroundWhiteBlueText3");
        themeDescriptionArr[38] = new ThemeDescription(this.listView, 0, new Class[]{ProfileSearchCell.class}, Theme.dialogs_namePaint, null, null, "chats_name");
        return themeDescriptionArr;
    }

    public /* synthetic */ void lambda$getThemeDescriptions$7$ContactsActivity() {
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            int childCount = recyclerListView.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = this.listView.getChildAt(i);
                if (childAt instanceof UserCell) {
                    ((UserCell) childAt).update(0);
                } else if (childAt instanceof ProfileSearchCell) {
                    ((ProfileSearchCell) childAt).update(0);
                }
            }
        }
    }
}
