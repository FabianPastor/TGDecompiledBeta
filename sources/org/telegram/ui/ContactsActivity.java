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
import org.telegram.messenger.beta.R;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.support.widget.RecyclerView.LayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.OnScrollListener;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.EncryptedChat;
import org.telegram.tgnet.TLRPC.User;
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

    public ContactsActivity(Bundle args) {
        super(args);
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.contactsDidLoad);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.updateInterfaces);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.encryptedChatCreated);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.closeChats);
        MessagesController.getGlobalNotificationsSettings().getBoolean("askAboutContacts", true);
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
        boolean inviteViaLink;
        int i;
        this.searching = false;
        this.searchWas = false;
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        if (!this.destroyAfterSelect) {
            this.actionBar.setTitle(LocaleController.getString("Contacts", R.string.Contacts));
        } else if (this.returnAsResult) {
            this.actionBar.setTitle(LocaleController.getString("SelectContact", R.string.SelectContact));
        } else if (this.createSecretChat) {
            this.actionBar.setTitle(LocaleController.getString("NewSecretChat", R.string.NewSecretChat));
        } else {
            this.actionBar.setTitle(LocaleController.getString("NewMessageTitle", R.string.NewMessageTitle));
        }
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                int i = 1;
                if (id == -1) {
                    ContactsActivity.this.lambda$createView$1$PhotoAlbumPickerActivity();
                } else if (id == 1) {
                    SharedConfig.toggleSortContactsByName();
                    ContactsActivity.this.sortByName = SharedConfig.sortContactsByName;
                    ContactsAdapter access$100 = ContactsActivity.this.listViewAdapter;
                    if (!ContactsActivity.this.sortByName) {
                        i = 2;
                    }
                    access$100.setSortType(i);
                    ContactsActivity.this.sortItem.setIcon(ContactsActivity.this.sortByName ? R.drawable.contacts_sort_time : R.drawable.contacts_sort_name);
                }
            }
        });
        ActionBarMenu menu = this.actionBar.createMenu();
        menu.addItem(0, (int) R.drawable.ic_ab_search).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItemSearchListener() {
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
                ContactsActivity.this.emptyView.setText(LocaleController.getString("NoContacts", R.string.NoContacts));
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
                    String text = editText.getText().toString();
                    if (text.length() != 0) {
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
                            ContactsActivity.this.emptyView.setText(LocaleController.getString("NoResult", R.string.NoResult));
                        }
                    }
                    ContactsActivity.this.searchListViewAdapter.searchDialogs(text);
                }
            }
        }).setSearchFieldHint(LocaleController.getString("Search", R.string.Search));
        if (!(this.createSecretChat || this.returnAsResult)) {
            this.sortItem = menu.addItem(1, this.sortByName ? R.drawable.contacts_sort_time : R.drawable.contacts_sort_name);
        }
        this.searchListViewAdapter = new SearchAdapter(context, this.ignoreUsers, this.allowUsernameSearch, false, false, this.allowBots, 0);
        if (this.chat_id != 0) {
            inviteViaLink = ChatObject.canUserDoAdminAction(MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.chat_id)), 3);
        } else {
            inviteViaLink = false;
        }
        if (this.onlyUsers) {
            i = 1;
        } else {
            i = 0;
        }
        this.listViewAdapter = new ContactsAdapter(context, i, this.needPhonebook, this.ignoreUsers, inviteViaLink) {
            public void notifyDataSetChanged() {
                int i = 8;
                boolean z = true;
                boolean z2 = false;
                super.notifyDataSetChanged();
                if (ContactsActivity.this.listView != null && ContactsActivity.this.listView.getAdapter() == this) {
                    int count = super.getItemCount();
                    EmptyTextProgressView access$800;
                    RecyclerListView access$700;
                    if (ContactsActivity.this.needPhonebook) {
                        access$800 = ContactsActivity.this.emptyView;
                        if (count == 2) {
                            i = 0;
                        }
                        access$800.setVisibility(i);
                        access$700 = ContactsActivity.this.listView;
                        if (count != 2) {
                            z2 = true;
                        }
                        access$700.setFastScrollVisible(z2);
                        return;
                    }
                    access$800 = ContactsActivity.this.emptyView;
                    if (count == 0) {
                        i = 0;
                    }
                    access$800.setVisibility(i);
                    access$700 = ContactsActivity.this.listView;
                    if (count == 0) {
                        z = false;
                    }
                    access$700.setFastScrollVisible(z);
                }
            }
        };
        ContactsAdapter contactsAdapter = this.listViewAdapter;
        int i2 = this.sortItem != null ? this.sortByName ? 1 : 2 : 0;
        contactsAdapter.setSortType(i2);
        this.fragmentView = new FrameLayout(context) {
            protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
                super.onLayout(changed, left, top, right, bottom);
                if (ContactsActivity.this.listView.getAdapter() != ContactsActivity.this.listViewAdapter) {
                    ContactsActivity.this.emptyView.setTranslationY((float) AndroidUtilities.dp(0.0f));
                } else if (ContactsActivity.this.emptyView.getVisibility() == 0) {
                    ContactsActivity.this.emptyView.setTranslationY((float) AndroidUtilities.dp(74.0f));
                }
            }
        };
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        this.emptyView = new EmptyTextProgressView(context);
        this.emptyView.setShowAtCenter(true);
        this.emptyView.setText(LocaleController.getString("NoContacts", R.string.NoContacts));
        this.emptyView.showTextView();
        frameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView = new RecyclerListView(context);
        this.listView.setSectionsType(1);
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setFastScrollEnabled();
        RecyclerListView recyclerListView = this.listView;
        LayoutManager linearLayoutManager = new LinearLayoutManager(context, 1, false);
        this.layoutManager = linearLayoutManager;
        recyclerListView.setLayoutManager(linearLayoutManager);
        this.listView.setAdapter(this.listViewAdapter);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setOnItemClickListener(new ContactsActivity$$Lambda$0(this));
        this.listView.setOnScrollListener(new OnScrollListener() {
            private boolean scrollingManually;

            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == 1) {
                    if (ContactsActivity.this.searching && ContactsActivity.this.searchWas) {
                        AndroidUtilities.hideKeyboard(ContactsActivity.this.getParentActivity().getCurrentFocus());
                    }
                    this.scrollingManually = true;
                    return;
                }
                this.scrollingManually = false;
            }

            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (ContactsActivity.this.floatingButtonContainer != null && ContactsActivity.this.floatingButtonContainer.getVisibility() != 8) {
                    boolean goingDown;
                    int firstVisibleItem = ContactsActivity.this.layoutManager.findFirstVisibleItemPosition();
                    View topChild = recyclerView.getChildAt(0);
                    int firstViewTop = 0;
                    if (topChild != null) {
                        firstViewTop = topChild.getTop();
                    }
                    boolean changed = true;
                    if (ContactsActivity.this.prevPosition == firstVisibleItem) {
                        int topDelta = ContactsActivity.this.prevTop - firstViewTop;
                        if (firstViewTop < ContactsActivity.this.prevTop) {
                            goingDown = true;
                        } else {
                            goingDown = false;
                        }
                        changed = Math.abs(topDelta) > 1;
                    } else {
                        goingDown = firstVisibleItem > ContactsActivity.this.prevPosition;
                    }
                    if (changed && ContactsActivity.this.scrollUpdated && (goingDown || (!goingDown && this.scrollingManually))) {
                        ContactsActivity.this.hideFloatingButton(goingDown);
                    }
                    ContactsActivity.this.prevPosition = firstVisibleItem;
                    ContactsActivity.this.prevTop = firstViewTop;
                    ContactsActivity.this.scrollUpdated = true;
                }
            }
        });
        if (!(this.createSecretChat || this.returnAsResult)) {
            float f;
            float f2;
            this.floatingButtonContainer = new FrameLayout(context);
            View view = this.floatingButtonContainer;
            int i3 = (VERSION.SDK_INT >= 21 ? 56 : 60) + 20;
            if (VERSION.SDK_INT >= 21) {
                i2 = 56;
            } else {
                i2 = 60;
            }
            float f3 = (float) (i2 + 14);
            if (LocaleController.isRTL) {
                i2 = 3;
            } else {
                i2 = 5;
            }
            int i4 = i2 | 80;
            if (LocaleController.isRTL) {
                f = 4.0f;
            } else {
                f = 0.0f;
            }
            if (LocaleController.isRTL) {
                f2 = 0.0f;
            } else {
                f2 = 4.0f;
            }
            frameLayout.addView(view, LayoutHelper.createFrame(i3, f3, i4, f, 0.0f, f2, 0.0f));
            this.floatingButtonContainer.setOnClickListener(new ContactsActivity$$Lambda$1(this));
            this.floatingButton = new ImageView(context);
            this.floatingButton.setScaleType(ScaleType.CENTER);
            Drawable drawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56.0f), Theme.getColor("chats_actionBackground"), Theme.getColor("chats_actionPressedBackground"));
            if (VERSION.SDK_INT < 21) {
                Drawable shadowDrawable = context.getResources().getDrawable(R.drawable.floating_shadow).mutate();
                shadowDrawable.setColorFilter(new PorterDuffColorFilter(-16777216, Mode.MULTIPLY));
                Drawable combinedDrawable = new CombinedDrawable(shadowDrawable, drawable, 0, 0);
                combinedDrawable.setIconSize(AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
                drawable = combinedDrawable;
            }
            this.floatingButton.setBackgroundDrawable(drawable);
            this.floatingButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chats_actionIcon"), Mode.MULTIPLY));
            this.floatingButton.setImageResource(R.drawable.add_contact_new);
            if (VERSION.SDK_INT >= 21) {
                StateListAnimator animator = new StateListAnimator();
                animator.addState(new int[]{16842919}, ObjectAnimator.ofFloat(this.floatingButton, View.TRANSLATION_Z, new float[]{(float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(4.0f)}).setDuration(200));
                animator.addState(new int[0], ObjectAnimator.ofFloat(this.floatingButton, View.TRANSLATION_Z, new float[]{(float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(2.0f)}).setDuration(200));
                this.floatingButton.setStateListAnimator(animator);
                this.floatingButton.setOutlineProvider(new ViewOutlineProvider() {
                    @SuppressLint({"NewApi"})
                    public void getOutline(View view, Outline outline) {
                        outline.setOval(0, 0, AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
                    }
                });
            }
            this.floatingButtonContainer.addView(this.floatingButton, LayoutHelper.createFrame(VERSION.SDK_INT >= 21 ? 56 : 60, (float) (VERSION.SDK_INT >= 21 ? 56 : 60), 51, 10.0f, 0.0f, 10.0f, 0.0f));
        }
        return this.fragmentView;
    }

    final /* synthetic */ void lambda$createView$1$ContactsActivity(View view, int position) {
        User user;
        Bundle args;
        if (this.searching && this.searchWas) {
            user = (User) this.searchListViewAdapter.getItem(position);
            if (user != null) {
                if (this.searchListViewAdapter.isGlobalSearch(position)) {
                    ArrayList<User> users = new ArrayList();
                    users.add(user);
                    MessagesController.getInstance(this.currentAccount).putUsers(users, false);
                    MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(users, null, false, true);
                }
                if (this.returnAsResult) {
                    if (this.ignoreUsers == null || this.ignoreUsers.indexOfKey(user.id) < 0) {
                        didSelectResult(user, true, null);
                        return;
                    }
                    return;
                } else if (!this.createSecretChat) {
                    args = new Bundle();
                    args.putInt("user_id", user.id);
                    if (MessagesController.getInstance(this.currentAccount).checkCanOpenChat(args, this)) {
                        presentFragment(new ChatActivity(args), true);
                        return;
                    }
                    return;
                } else if (user.id != UserConfig.getInstance(this.currentAccount).getClientUserId()) {
                    this.creatingChat = true;
                    SecretChatHelper.getInstance(this.currentAccount).startSecretChat(getParentActivity(), user);
                    return;
                } else {
                    return;
                }
            }
            return;
        }
        int section = this.listViewAdapter.getSectionForPosition(position);
        int row = this.listViewAdapter.getPositionInSectionForPosition(position);
        if (row >= 0 && section >= 0) {
            if ((this.onlyUsers && this.chat_id == 0) || section != 0) {
                Contact item1 = this.listViewAdapter.getItem(section, row);
                if (item1 instanceof User) {
                    user = (User) item1;
                    if (this.returnAsResult) {
                        if (this.ignoreUsers == null || this.ignoreUsers.indexOfKey(user.id) < 0) {
                            didSelectResult(user, true, null);
                        }
                    } else if (this.createSecretChat) {
                        this.creatingChat = true;
                        SecretChatHelper.getInstance(this.currentAccount).startSecretChat(getParentActivity(), user);
                    } else {
                        args = new Bundle();
                        args.putInt("user_id", user.id);
                        if (MessagesController.getInstance(this.currentAccount).checkCanOpenChat(args, this)) {
                            presentFragment(new ChatActivity(args), true);
                        }
                    }
                } else if (item1 instanceof Contact) {
                    Contact contact = item1;
                    String usePhone = null;
                    if (!contact.phones.isEmpty()) {
                        usePhone = (String) contact.phones.get(0);
                    }
                    if (usePhone != null && getParentActivity() != null) {
                        Builder builder = new Builder(getParentActivity());
                        builder.setMessage(LocaleController.getString("InviteUser", R.string.InviteUser));
                        builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new ContactsActivity$$Lambda$7(this, usePhone));
                        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                        showDialog(builder.create());
                    }
                }
            } else if (this.needPhonebook) {
                if (row == 0) {
                    presentFragment(new InviteContactsActivity());
                }
            } else if (this.chat_id != 0) {
                if (row == 0) {
                    presentFragment(new GroupInviteActivity(this.chat_id));
                }
            } else if (row == 0) {
                args = new Bundle();
                args.putBoolean("showFabButton", true);
                presentFragment(new GroupCreateActivity(args), false);
            } else if (row == 1) {
                args = new Bundle();
                args.putBoolean("onlyUsers", true);
                args.putBoolean("destroyAfterSelect", true);
                args.putBoolean("createSecretChat", true);
                args.putBoolean("allowBots", false);
                presentFragment(new ContactsActivity(args), false);
            } else if (row == 2) {
                SharedPreferences preferences = MessagesController.getGlobalMainSettings();
                if (BuildVars.DEBUG_VERSION || !preferences.getBoolean("channel_intro", false)) {
                    presentFragment(new ChannelIntroActivity());
                    preferences.edit().putBoolean("channel_intro", true).commit();
                    return;
                }
                args = new Bundle();
                args.putInt("step", 0);
                presentFragment(new ChannelCreateActivity(args));
            }
        }
    }

    final /* synthetic */ void lambda$null$0$ContactsActivity(String arg1, DialogInterface dialogInterface, int i) {
        try {
            Intent intent = new Intent("android.intent.action.VIEW", Uri.fromParts("sms", arg1, null));
            intent.putExtra("sms_body", ContactsController.getInstance(this.currentAccount).getInviteText(1));
            getParentActivity().startActivityForResult(intent, 500);
        } catch (Throwable e) {
            FileLog.e(e);
        }
    }

    final /* synthetic */ void lambda$createView$2$ContactsActivity(View v) {
        presentFragment(new NewContactActivity());
    }

    private void didSelectResult(User user, boolean useAlert, String param) {
        if (!useAlert || this.selectAlertString == null) {
            if (this.delegate != null) {
                this.delegate.didSelectContact(user, param, this);
                this.delegate = null;
            }
            if (this.needFinishFragment) {
                lambda$createView$1$PhotoAlbumPickerActivity();
            }
        } else if (getParentActivity() != null) {
            Builder builder;
            if (user.bot) {
                if (user.bot_nochats) {
                    try {
                        Toast.makeText(getParentActivity(), LocaleController.getString("BotCantJoinGroups", R.string.BotCantJoinGroups), 0).show();
                        return;
                    } catch (Throwable e) {
                        FileLog.e(e);
                        return;
                    }
                } else if (this.channelId != 0) {
                    Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.channelId));
                    builder = new Builder(getParentActivity());
                    if (ChatObject.canAddAdmins(chat)) {
                        builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                        builder.setMessage(LocaleController.getString("AddBotAsAdmin", R.string.AddBotAsAdmin));
                        builder.setPositiveButton(LocaleController.getString("MakeAdmin", R.string.MakeAdmin), new ContactsActivity$$Lambda$2(this, user, param));
                        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                    } else {
                        builder.setMessage(LocaleController.getString("CantAddBotAsAdmin", R.string.CantAddBotAsAdmin));
                        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
                    }
                    showDialog(builder.create());
                    return;
                }
            }
            builder = new Builder(getParentActivity());
            builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
            String message = LocaleController.formatStringSimple(this.selectAlertString, UserObject.getUserName(user));
            EditText editText = null;
            if (!user.bot && this.needForwardCount) {
                message = String.format("%s\n\n%s", new Object[]{message, LocaleController.getString("AddToTheGroupForwardCount", R.string.AddToTheGroupForwardCount)});
                editText = new EditText(getParentActivity());
                editText.setTextSize(1, 18.0f);
                editText.setText("50");
                editText.setTextColor(Theme.getColor("dialogTextBlack"));
                editText.setGravity(17);
                editText.setInputType(2);
                editText.setImeOptions(6);
                editText.setBackgroundDrawable(Theme.createEditTextDrawable(getParentActivity(), true));
                final EditText editTextFinal = editText;
                editText.addTextChangedListener(new TextWatcher() {
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    public void afterTextChanged(Editable s) {
                        try {
                            String str = s.toString();
                            if (str.length() != 0) {
                                int value = Utilities.parseInt(str).intValue();
                                if (value < 0) {
                                    editTextFinal.setText("0");
                                    editTextFinal.setSelection(editTextFinal.length());
                                } else if (value > 300) {
                                    editTextFinal.setText("300");
                                    editTextFinal.setSelection(editTextFinal.length());
                                } else if (!str.equals("" + value)) {
                                    editTextFinal.setText("" + value);
                                    editTextFinal.setSelection(editTextFinal.length());
                                }
                            }
                        } catch (Throwable e) {
                            FileLog.e(e);
                        }
                    }
                });
                builder.setView(editText);
            }
            builder.setMessage(message);
            builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new ContactsActivity$$Lambda$3(this, user, editText));
            builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
            showDialog(builder.create());
            if (editText != null) {
                MarginLayoutParams layoutParams = (MarginLayoutParams) editText.getLayoutParams();
                if (layoutParams != null) {
                    if (layoutParams instanceof LayoutParams) {
                        ((LayoutParams) layoutParams).gravity = 1;
                    }
                    int dp = AndroidUtilities.dp(24.0f);
                    layoutParams.leftMargin = dp;
                    layoutParams.rightMargin = dp;
                    layoutParams.height = AndroidUtilities.dp(36.0f);
                    editText.setLayoutParams(layoutParams);
                }
                editText.setSelection(editText.getText().length());
            }
        }
    }

    final /* synthetic */ void lambda$didSelectResult$3$ContactsActivity(User user, String param, DialogInterface dialogInterface, int i) {
        if (this.delegate != null) {
            this.delegate.didSelectContact(user, param, this);
            this.delegate = null;
        }
    }

    final /* synthetic */ void lambda$didSelectResult$4$ContactsActivity(User user, EditText finalEditText, DialogInterface dialogInterface, int i) {
        didSelectResult(user, false, finalEditText != null ? finalEditText.getText().toString() : "0");
    }

    public void onResume() {
        super.onResume();
        if (this.listViewAdapter != null) {
            this.listViewAdapter.notifyDataSetChanged();
        }
        if (this.checkPermission && VERSION.SDK_INT >= 23) {
            Activity activity = getParentActivity();
            if (activity != null) {
                this.checkPermission = false;
                if (activity.checkSelfPermission("android.permission.READ_CONTACTS") == 0) {
                    return;
                }
                if (activity.shouldShowRequestPermissionRationale("android.permission.READ_CONTACTS")) {
                    Dialog create = AlertsCreator.createContactsPermissionDialog(activity, new ContactsActivity$$Lambda$4(this)).create();
                    this.permissionDialog = create;
                    showDialog(create);
                    return;
                }
                askForPermissons(true);
            }
        }
    }

    final /* synthetic */ void lambda$onResume$5$ContactsActivity(int param) {
        boolean z;
        if (param != 0) {
            z = true;
        } else {
            z = false;
        }
        this.askAboutContacts = z;
        MessagesController.getGlobalNotificationsSettings().edit().putBoolean("askAboutContacts", this.askAboutContacts).commit();
        askForPermissons(false);
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (this.floatingButtonContainer != null) {
            this.floatingButtonContainer.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
                public void onGlobalLayout() {
                    boolean z = false;
                    ContactsActivity.this.floatingButtonContainer.setTranslationY((float) (ContactsActivity.this.floatingHidden ? AndroidUtilities.dp(100.0f) : 0));
                    FrameLayout access$400 = ContactsActivity.this.floatingButtonContainer;
                    if (!ContactsActivity.this.floatingHidden) {
                        z = true;
                    }
                    access$400.setClickable(z);
                    if (ContactsActivity.this.floatingButtonContainer != null) {
                        ContactsActivity.this.floatingButtonContainer.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                }
            });
        }
    }

    protected void onDialogDismiss(Dialog dialog) {
        super.onDialogDismiss(dialog);
        if (this.permissionDialog != null && dialog == this.permissionDialog && getParentActivity() != null) {
            askForPermissons(false);
        }
    }

    @TargetApi(23)
    private void askForPermissons(boolean alert) {
        Activity activity = getParentActivity();
        if (activity != null && UserConfig.getInstance(this.currentAccount).syncContacts && activity.checkSelfPermission("android.permission.READ_CONTACTS") != 0) {
            if (alert && this.askAboutContacts) {
                showDialog(AlertsCreator.createContactsPermissionDialog(activity, new ContactsActivity$$Lambda$5(this)).create());
                return;
            }
            ArrayList<String> permissons = new ArrayList();
            permissons.add("android.permission.READ_CONTACTS");
            permissons.add("android.permission.WRITE_CONTACTS");
            permissons.add("android.permission.GET_ACCOUNTS");
            activity.requestPermissions((String[]) permissons.toArray(new String[0]), 1);
        }
    }

    final /* synthetic */ void lambda$askForPermissons$6$ContactsActivity(int param) {
        boolean z;
        if (param != 0) {
            z = true;
        } else {
            z = false;
        }
        this.askAboutContacts = z;
        MessagesController.getGlobalNotificationsSettings().edit().putBoolean("askAboutContacts", this.askAboutContacts).commit();
        askForPermissons(false);
    }

    public void onRequestPermissionsResultFragment(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1) {
            for (int a = 0; a < permissions.length; a++) {
                if (grantResults.length > a) {
                    String str = permissions[a];
                    boolean z = true;
                    switch (str.hashCode()) {
                        case 1977429404:
                            if (str.equals("android.permission.READ_CONTACTS")) {
                                z = false;
                                break;
                            }
                            break;
                    }
                    switch (z) {
                        case false:
                            if (grantResults[a] != 0) {
                                this.askAboutContacts = false;
                                MessagesController.getGlobalNotificationsSettings().edit().putBoolean("askAboutContacts", false).commit();
                                break;
                            }
                            ContactsController.getInstance(this.currentAccount).forceImportContacts();
                            break;
                        default:
                            break;
                    }
                }
            }
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
        } else if (id == NotificationCenter.updateInterfaces) {
            int mask = ((Integer) args[0]).intValue();
            if (!((mask & 2) == 0 && (mask & 1) == 0 && (mask & 4) == 0)) {
                updateVisibleRows(mask);
            }
            if ((mask & 4) != 0 && !this.sortByName && this.listViewAdapter != null) {
                this.listViewAdapter.sortOnlineContacts();
            }
        } else if (id == NotificationCenter.encryptedChatCreated) {
            if (this.createSecretChat && this.creatingChat) {
                EncryptedChat encryptedChat = args[0];
                Bundle args2 = new Bundle();
                args2.putInt("enc_id", encryptedChat.id);
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                presentFragment(new ChatActivity(args2), true);
            }
        } else if (id == NotificationCenter.closeChats && !this.creatingChat) {
            lambda$null$9$ProfileActivity();
        }
    }

    private void updateVisibleRows(int mask) {
        if (this.listView != null) {
            int count = this.listView.getChildCount();
            for (int a = 0; a < count; a++) {
                View child = this.listView.getChildAt(a);
                if (child instanceof UserCell) {
                    ((UserCell) child).update(mask);
                }
            }
        }
    }

    private void hideFloatingButton(boolean hide) {
        boolean z = false;
        if (this.floatingHidden != hide) {
            int dp;
            this.floatingHidden = hide;
            AnimatorSet animatorSet = new AnimatorSet();
            Animator[] animatorArr = new Animator[1];
            FrameLayout frameLayout = this.floatingButtonContainer;
            Property property = View.TRANSLATION_Y;
            float[] fArr = new float[1];
            if (this.floatingHidden) {
                dp = AndroidUtilities.dp(100.0f);
            } else {
                dp = 0;
            }
            fArr[0] = (float) dp;
            animatorArr[0] = ObjectAnimator.ofFloat(frameLayout, property, fArr);
            animatorSet.playTogether(animatorArr);
            animatorSet.setDuration(300);
            animatorSet.setInterpolator(this.floatingInterpolator);
            FrameLayout frameLayout2 = this.floatingButtonContainer;
            if (!hide) {
                z = true;
            }
            frameLayout2.setClickable(z);
            animatorSet.start();
        }
    }

    public void setDelegate(ContactsActivityDelegate delegate) {
        this.delegate = delegate;
    }

    public void setIgnoreUsers(SparseArray<User> users) {
        this.ignoreUsers = users;
    }

    public ThemeDescription[] getThemeDescriptions() {
        ThemeDescriptionDelegate cellDelegate = new ContactsActivity$$Lambda$6(this);
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

    final /* synthetic */ void lambda$getThemeDescriptions$7$ContactsActivity() {
        if (this.listView != null) {
            int count = this.listView.getChildCount();
            for (int a = 0; a < count; a++) {
                View child = this.listView.getChildAt(a);
                if (child instanceof UserCell) {
                    ((UserCell) child).update(0);
                } else if (child instanceof ProfileSearchCell) {
                    ((ProfileSearchCell) child).update(0);
                }
            }
        }
    }
}
