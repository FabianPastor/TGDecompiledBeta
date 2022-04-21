package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Property;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import androidx.collection.LongSparseArray;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SecretChatHelper;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Adapters.ContactsAdapter;
import org.telegram.ui.Adapters.SearchAdapter;
import org.telegram.ui.Cells.GraySectionCell;
import org.telegram.ui.Cells.LetterSectionCell;
import org.telegram.ui.Cells.ProfileSearchCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Cells.UserCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.FlickerLoadingView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RLottieImageView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.StickerEmptyView;

public class ContactsActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private static final int search_button = 0;
    private static final int sort_button = 1;
    private boolean allowBots = true;
    private boolean allowSelf = true;
    private boolean allowUsernameSearch = true;
    /* access modifiers changed from: private */
    public int animationIndex = -1;
    private boolean askAboutContacts = true;
    /* access modifiers changed from: private */
    public AnimatorSet bounceIconAnimator;
    private long channelId;
    private long chatId;
    private boolean checkPermission = true;
    private boolean createSecretChat;
    private boolean creatingChat;
    private ContactsActivityDelegate delegate;
    private boolean destroyAfterSelect;
    private boolean disableSections;
    /* access modifiers changed from: private */
    public StickerEmptyView emptyView;
    /* access modifiers changed from: private */
    public RLottieImageView floatingButton;
    /* access modifiers changed from: private */
    public FrameLayout floatingButtonContainer;
    /* access modifiers changed from: private */
    public boolean floatingHidden;
    private AccelerateDecelerateInterpolator floatingInterpolator = new AccelerateDecelerateInterpolator();
    private boolean hasGps;
    private LongSparseArray<TLRPC.User> ignoreUsers;
    private String initialSearchString;
    /* access modifiers changed from: private */
    public LinearLayoutManager layoutManager;
    /* access modifiers changed from: private */
    public RecyclerListView listView;
    /* access modifiers changed from: private */
    public ContactsAdapter listViewAdapter;
    private boolean needFinishFragment = true;
    private boolean needForwardCount = true;
    /* access modifiers changed from: private */
    public boolean needPhonebook;
    private boolean onlyUsers;
    private AlertDialog permissionDialog;
    private long permissionRequestTime;
    /* access modifiers changed from: private */
    public int prevPosition;
    /* access modifiers changed from: private */
    public int prevTop;
    private boolean resetDelegate = true;
    private boolean returnAsResult;
    /* access modifiers changed from: private */
    public boolean scrollUpdated;
    /* access modifiers changed from: private */
    public SearchAdapter searchListViewAdapter;
    /* access modifiers changed from: private */
    public boolean searchWas;
    /* access modifiers changed from: private */
    public boolean searching;
    private String selectAlertString = null;
    /* access modifiers changed from: private */
    public boolean sortByName;
    /* access modifiers changed from: private */
    public ActionBarMenuItem sortItem;

    public interface ContactsActivityDelegate {
        void didSelectContact(TLRPC.User user, String str, ContactsActivity contactsActivity);
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
        this.checkPermission = UserConfig.getInstance(this.currentAccount).syncContacts;
        if (this.arguments != null) {
            this.onlyUsers = this.arguments.getBoolean("onlyUsers", false);
            this.destroyAfterSelect = this.arguments.getBoolean("destroyAfterSelect", false);
            this.returnAsResult = this.arguments.getBoolean("returnAsResult", false);
            this.createSecretChat = this.arguments.getBoolean("createSecretChat", false);
            this.selectAlertString = this.arguments.getString("selectAlertString");
            this.allowUsernameSearch = this.arguments.getBoolean("allowUsernameSearch", true);
            this.needForwardCount = this.arguments.getBoolean("needForwardCount", true);
            this.allowBots = this.arguments.getBoolean("allowBots", true);
            this.allowSelf = this.arguments.getBoolean("allowSelf", true);
            this.channelId = this.arguments.getLong("channelId", 0);
            this.needFinishFragment = this.arguments.getBoolean("needFinishFragment", true);
            this.chatId = this.arguments.getLong("chat_id", 0);
            this.disableSections = this.arguments.getBoolean("disableSections", false);
            this.resetDelegate = this.arguments.getBoolean("resetDelegate", false);
        } else {
            this.needPhonebook = true;
        }
        if (!this.createSecretChat && !this.returnAsResult) {
            this.sortByName = SharedConfig.sortContactsByName;
        }
        getContactsController().checkInviteText();
        getContactsController().reloadContactsStatusesMaybe();
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.contactsDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.updateInterfaces);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.encryptedChatCreated);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.closeChats);
        this.delegate = null;
        AndroidUtilities.removeAdjustResize(getParentActivity(), this.classGuid);
        getNotificationCenter().onAnimationFinish(this.animationIndex);
    }

    /* access modifiers changed from: protected */
    public void onTransitionAnimationProgress(boolean isOpen, float progress) {
        super.onTransitionAnimationProgress(isOpen, progress);
        if (this.fragmentView != null) {
            this.fragmentView.invalidate();
        }
    }

    public View createView(Context context) {
        int inviteViaLink;
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
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    ContactsActivity.this.finishFragment();
                    return;
                }
                int i = 1;
                if (id == 1) {
                    SharedConfig.toggleSortContactsByName();
                    boolean unused = ContactsActivity.this.sortByName = SharedConfig.sortContactsByName;
                    ContactsAdapter access$100 = ContactsActivity.this.listViewAdapter;
                    if (!ContactsActivity.this.sortByName) {
                        i = 2;
                    }
                    access$100.setSortType(i, false);
                    ContactsActivity.this.sortItem.setIcon(ContactsActivity.this.sortByName ? NUM : NUM);
                }
            }
        });
        ActionBarMenu menu = this.actionBar.createMenu();
        ActionBarMenuItem item = menu.addItem(0, NUM).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener() {
            public void onSearchExpand() {
                boolean unused = ContactsActivity.this.searching = true;
                if (ContactsActivity.this.floatingButtonContainer != null) {
                    ContactsActivity.this.floatingButtonContainer.setVisibility(8);
                }
                if (ContactsActivity.this.sortItem != null) {
                    ContactsActivity.this.sortItem.setVisibility(8);
                }
            }

            public void onSearchCollapse() {
                ContactsActivity.this.searchListViewAdapter.searchDialogs((String) null);
                boolean unused = ContactsActivity.this.searching = false;
                boolean unused2 = ContactsActivity.this.searchWas = false;
                ContactsActivity.this.listView.setAdapter(ContactsActivity.this.listViewAdapter);
                ContactsActivity.this.listView.setSectionsType(1);
                ContactsActivity.this.listViewAdapter.notifyDataSetChanged();
                ContactsActivity.this.listView.setFastScrollVisible(true);
                ContactsActivity.this.listView.setVerticalScrollBarEnabled(false);
                if (ContactsActivity.this.floatingButtonContainer != null) {
                    ContactsActivity.this.floatingButtonContainer.setVisibility(0);
                    boolean unused3 = ContactsActivity.this.floatingHidden = true;
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
                        boolean unused = ContactsActivity.this.searchWas = true;
                        if (ContactsActivity.this.listView != null) {
                            ContactsActivity.this.listView.setAdapter(ContactsActivity.this.searchListViewAdapter);
                            ContactsActivity.this.listView.setSectionsType(0);
                            ContactsActivity.this.searchListViewAdapter.notifyDataSetChanged();
                            ContactsActivity.this.listView.setFastScrollVisible(false);
                            ContactsActivity.this.listView.setVerticalScrollBarEnabled(true);
                        }
                        ContactsActivity.this.emptyView.showProgress(true, true);
                        ContactsActivity.this.searchListViewAdapter.searchDialogs(text);
                    } else if (ContactsActivity.this.listView != null) {
                        ContactsActivity.this.listView.setAdapter(ContactsActivity.this.listViewAdapter);
                        ContactsActivity.this.listView.setSectionsType(1);
                    }
                }
            }
        });
        item.setSearchFieldHint(LocaleController.getString("Search", NUM));
        item.setContentDescription(LocaleController.getString("Search", NUM));
        if (!this.createSecretChat && !this.returnAsResult) {
            ActionBarMenuItem addItem = menu.addItem(1, this.sortByName ? NUM : NUM);
            this.sortItem = addItem;
            addItem.setContentDescription(LocaleController.getString("AccDescrContactSorting", NUM));
        }
        ActionBarMenuItem actionBarMenuItem = item;
        ActionBarMenu actionBarMenu = menu;
        this.searchListViewAdapter = new SearchAdapter(context, this.ignoreUsers, this.allowUsernameSearch, false, false, this.allowBots, this.allowSelf, true, 0) {
            /* access modifiers changed from: protected */
            public void onSearchProgressChanged() {
                if (!searchInProgress() && getItemCount() == 0) {
                    ContactsActivity.this.emptyView.showProgress(false, true);
                }
                ContactsActivity.this.showItemsAnimated();
            }
        };
        int i = 3;
        if (this.chatId != 0) {
            inviteViaLink = ChatObject.canUserDoAdminAction(getMessagesController().getChat(Long.valueOf(this.chatId)), 3);
        } else if (this.channelId != 0) {
            TLRPC.Chat chat = getMessagesController().getChat(Long.valueOf(this.channelId));
            inviteViaLink = (!ChatObject.canUserDoAdminAction(chat, 3) || !TextUtils.isEmpty(chat.username)) ? 0 : 2;
        } else {
            inviteViaLink = 0;
        }
        try {
            this.hasGps = ApplicationLoader.applicationContext.getPackageManager().hasSystemFeature("android.hardware.location.gps");
        } catch (Throwable th) {
            this.hasGps = false;
        }
        AnonymousClass4 r1 = new ContactsAdapter(context, this.onlyUsers ? 1 : 0, this.needPhonebook, this.ignoreUsers, (int) inviteViaLink, this.hasGps) {
            public void notifyDataSetChanged() {
                super.notifyDataSetChanged();
                if (ContactsActivity.this.listView != null && ContactsActivity.this.listView.getAdapter() == this) {
                    int count = super.getItemCount();
                    boolean z = true;
                    if (ContactsActivity.this.needPhonebook) {
                        RecyclerListView access$700 = ContactsActivity.this.listView;
                        if (count == 2) {
                            z = false;
                        }
                        access$700.setFastScrollVisible(z);
                        return;
                    }
                    RecyclerListView access$7002 = ContactsActivity.this.listView;
                    if (count == 0) {
                        z = false;
                    }
                    access$7002.setFastScrollVisible(z);
                }
            }
        };
        this.listViewAdapter = r1;
        r1.setSortType(this.sortItem != null ? this.sortByName ? 1 : 2 : 0, false);
        this.listViewAdapter.setDisableSections(this.disableSections);
        this.fragmentView = new FrameLayout(context2) {
            /* access modifiers changed from: protected */
            public void onLayout(boolean changed, int left, int top, int right, int bottom) {
                super.onLayout(changed, left, top, right, bottom);
                if (ContactsActivity.this.listView.getAdapter() != ContactsActivity.this.listViewAdapter) {
                    ContactsActivity.this.emptyView.setTranslationY((float) AndroidUtilities.dp(0.0f));
                } else if (ContactsActivity.this.emptyView.getVisibility() == 0) {
                    ContactsActivity.this.emptyView.setTranslationY((float) AndroidUtilities.dp(74.0f));
                }
            }
        };
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        FlickerLoadingView flickerLoadingView = new FlickerLoadingView(context2);
        flickerLoadingView.setViewType(6);
        flickerLoadingView.showDate(false);
        StickerEmptyView stickerEmptyView = new StickerEmptyView(context2, flickerLoadingView, 1);
        this.emptyView = stickerEmptyView;
        stickerEmptyView.addView(flickerLoadingView, 0);
        this.emptyView.setAnimateLayoutChange(true);
        this.emptyView.showProgress(true, false);
        this.emptyView.title.setText(LocaleController.getString("NoResult", NUM));
        this.emptyView.subtitle.setText(LocaleController.getString("SearchEmptyViewFilteredSubtitle2", NUM));
        frameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f));
        AnonymousClass6 r2 = new RecyclerListView(context2) {
            public void setPadding(int left, int top, int right, int bottom) {
                super.setPadding(left, top, right, bottom);
                if (ContactsActivity.this.emptyView != null) {
                    ContactsActivity.this.emptyView.setPadding(left, top, right, bottom);
                }
            }
        };
        this.listView = r2;
        r2.setSectionsType(1);
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setFastScrollEnabled(0);
        RecyclerListView recyclerListView = this.listView;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context2, 1, false);
        this.layoutManager = linearLayoutManager;
        recyclerListView.setLayoutManager(linearLayoutManager);
        this.listView.setAdapter(this.listViewAdapter);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setEmptyView(this.emptyView);
        this.listView.setAnimateEmptyView(true, 0);
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new ContactsActivity$$ExternalSyntheticLambda9(this, (int) inviteViaLink));
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
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
                boolean goingDown;
                super.onScrolled(recyclerView, dx, dy);
                if (ContactsActivity.this.floatingButtonContainer != null && ContactsActivity.this.floatingButtonContainer.getVisibility() != 8) {
                    int firstVisibleItem = ContactsActivity.this.layoutManager.findFirstVisibleItemPosition();
                    boolean z = false;
                    View topChild = recyclerView.getChildAt(0);
                    int firstViewTop = 0;
                    if (topChild != null) {
                        firstViewTop = topChild.getTop();
                    }
                    boolean changed = true;
                    if (ContactsActivity.this.prevPosition == firstVisibleItem) {
                        int topDelta = ContactsActivity.this.prevTop - firstViewTop;
                        goingDown = firstViewTop < ContactsActivity.this.prevTop;
                        if (Math.abs(topDelta) > 1) {
                            z = true;
                        }
                        changed = z;
                    } else {
                        if (firstVisibleItem > ContactsActivity.this.prevPosition) {
                            z = true;
                        }
                        goingDown = z;
                    }
                    if (changed && ContactsActivity.this.scrollUpdated && (goingDown || this.scrollingManually)) {
                        ContactsActivity.this.hideFloatingButton(goingDown);
                    }
                    int unused = ContactsActivity.this.prevPosition = firstVisibleItem;
                    int unused2 = ContactsActivity.this.prevTop = firstViewTop;
                    boolean unused3 = ContactsActivity.this.scrollUpdated = true;
                }
            }
        });
        if (!this.createSecretChat && !this.returnAsResult) {
            FrameLayout frameLayout2 = new FrameLayout(context2);
            this.floatingButtonContainer = frameLayout2;
            int i2 = (Build.VERSION.SDK_INT >= 21 ? 56 : 60) + 20;
            float f = (float) ((Build.VERSION.SDK_INT >= 21 ? 56 : 60) + 20);
            if (!LocaleController.isRTL) {
                i = 5;
            }
            frameLayout.addView(frameLayout2, LayoutHelper.createFrame(i2, f, i | 80, LocaleController.isRTL ? 4.0f : 0.0f, 0.0f, LocaleController.isRTL ? 0.0f : 4.0f, 0.0f));
            this.floatingButtonContainer.setOnClickListener(new ContactsActivity$$ExternalSyntheticLambda4(this));
            RLottieImageView rLottieImageView = new RLottieImageView(context2);
            this.floatingButton = rLottieImageView;
            rLottieImageView.setScaleType(ImageView.ScaleType.CENTER);
            Drawable drawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56.0f), Theme.getColor("chats_actionBackground"), Theme.getColor("chats_actionPressedBackground"));
            if (Build.VERSION.SDK_INT < 21) {
                Drawable shadowDrawable = context.getResources().getDrawable(NUM).mutate();
                shadowDrawable.setColorFilter(new PorterDuffColorFilter(-16777216, PorterDuff.Mode.MULTIPLY));
                CombinedDrawable combinedDrawable = new CombinedDrawable(shadowDrawable, drawable, 0, 0);
                combinedDrawable.setIconSize(AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
                drawable = combinedDrawable;
            }
            this.floatingButton.setBackgroundDrawable(drawable);
            this.floatingButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chats_actionIcon"), PorterDuff.Mode.MULTIPLY));
            this.floatingButton.setAnimation(MessagesController.getGlobalMainSettings().getBoolean("view_animations", true) ? NUM : NUM, 52, 52);
            this.floatingButtonContainer.setContentDescription(LocaleController.getString("CreateNewContact", NUM));
            if (Build.VERSION.SDK_INT >= 21) {
                StateListAnimator animator = new StateListAnimator();
                animator.addState(new int[]{16842919}, ObjectAnimator.ofFloat(this.floatingButton, View.TRANSLATION_Z, new float[]{(float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(4.0f)}).setDuration(200));
                animator.addState(new int[0], ObjectAnimator.ofFloat(this.floatingButton, View.TRANSLATION_Z, new float[]{(float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(2.0f)}).setDuration(200));
                this.floatingButton.setStateListAnimator(animator);
                this.floatingButton.setOutlineProvider(new ViewOutlineProvider() {
                    public void getOutline(View view, Outline outline) {
                        outline.setOval(0, 0, AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
                    }
                });
            }
            this.floatingButtonContainer.addView(this.floatingButton, LayoutHelper.createFrame(Build.VERSION.SDK_INT >= 21 ? 56 : 60, (float) (Build.VERSION.SDK_INT >= 21 ? 56 : 60), 51, 10.0f, 6.0f, 10.0f, 0.0f));
        }
        if (this.initialSearchString != null) {
            this.actionBar.openSearchField(this.initialSearchString, false);
            this.initialSearchString = null;
        }
        return this.fragmentView;
    }

    /* renamed from: lambda$createView$1$org-telegram-ui-ContactsActivity  reason: not valid java name */
    public /* synthetic */ void m2033lambda$createView$1$orgtelegramuiContactsActivity(int inviteViaLink, View view, int position) {
        Activity activity;
        RecyclerView.Adapter adapter = this.listView.getAdapter();
        SearchAdapter searchAdapter = this.searchListViewAdapter;
        boolean z = false;
        if (adapter == searchAdapter) {
            Object object = searchAdapter.getItem(position);
            if (object instanceof TLRPC.User) {
                TLRPC.User user = (TLRPC.User) object;
                if (this.searchListViewAdapter.isGlobalSearch(position)) {
                    ArrayList<TLRPC.User> users = new ArrayList<>();
                    users.add(user);
                    getMessagesController().putUsers(users, false);
                    MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(users, (ArrayList<TLRPC.Chat>) null, false, true);
                }
                if (this.returnAsResult) {
                    LongSparseArray<TLRPC.User> longSparseArray = this.ignoreUsers;
                    if (longSparseArray == null || longSparseArray.indexOfKey(user.id) < 0) {
                        didSelectResult(user, true, (String) null);
                    }
                } else if (!this.createSecretChat) {
                    Bundle args = new Bundle();
                    args.putLong("user_id", user.id);
                    if (getMessagesController().checkCanOpenChat(args, this)) {
                        presentFragment(new ChatActivity(args), true);
                    }
                } else if (user.id != UserConfig.getInstance(this.currentAccount).getClientUserId()) {
                    this.creatingChat = true;
                    SecretChatHelper.getInstance(this.currentAccount).startSecretChat(getParentActivity(), user);
                }
            } else if (object instanceof String) {
                String str = (String) object;
                if (!str.equals("section")) {
                    NewContactActivity activity2 = new NewContactActivity();
                    activity2.setInitialPhoneNumber(str, true);
                    presentFragment(activity2);
                }
            }
        } else {
            int section = this.listViewAdapter.getSectionForPosition(position);
            int row = this.listViewAdapter.getPositionInSectionForPosition(position);
            if (row >= 0 && section >= 0) {
                if ((this.onlyUsers && inviteViaLink == 0) || section != 0) {
                    Object item1 = this.listViewAdapter.getItem(section, row);
                    if (item1 instanceof TLRPC.User) {
                        TLRPC.User user2 = (TLRPC.User) item1;
                        if (this.returnAsResult) {
                            LongSparseArray<TLRPC.User> longSparseArray2 = this.ignoreUsers;
                            if (longSparseArray2 == null || longSparseArray2.indexOfKey(user2.id) < 0) {
                                didSelectResult(user2, true, (String) null);
                            }
                        } else if (this.createSecretChat) {
                            this.creatingChat = true;
                            SecretChatHelper.getInstance(this.currentAccount).startSecretChat(getParentActivity(), user2);
                        } else {
                            Bundle args2 = new Bundle();
                            args2.putLong("user_id", user2.id);
                            if (getMessagesController().checkCanOpenChat(args2, this)) {
                                presentFragment(new ChatActivity(args2), true);
                            }
                        }
                    } else if (item1 instanceof ContactsController.Contact) {
                        ContactsController.Contact contact = (ContactsController.Contact) item1;
                        String usePhone = null;
                        if (!contact.phones.isEmpty()) {
                            usePhone = contact.phones.get(0);
                        }
                        if (usePhone != null && getParentActivity() != null) {
                            AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
                            builder.setMessage(LocaleController.getString("InviteUser", NUM));
                            builder.setTitle(LocaleController.getString("AppName", NUM));
                            builder.setPositiveButton(LocaleController.getString("OK", NUM), new ContactsActivity$$ExternalSyntheticLambda1(this, usePhone));
                            builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                            showDialog(builder.create());
                        }
                    }
                } else if (this.needPhonebook) {
                    if (row == 0) {
                        presentFragment(new InviteContactsActivity());
                    } else if (row == 1 && this.hasGps) {
                        if (Build.VERSION.SDK_INT < 23 || (activity = getParentActivity()) == null || activity.checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION") == 0) {
                            boolean enabled = true;
                            if (Build.VERSION.SDK_INT >= 28) {
                                enabled = ((LocationManager) ApplicationLoader.applicationContext.getSystemService("location")).isLocationEnabled();
                            } else if (Build.VERSION.SDK_INT >= 19) {
                                try {
                                    if (Settings.Secure.getInt(ApplicationLoader.applicationContext.getContentResolver(), "location_mode", 0) != 0) {
                                        z = true;
                                    }
                                    enabled = z;
                                } catch (Throwable e) {
                                    FileLog.e(e);
                                }
                            }
                            if (!enabled) {
                                presentFragment(new ActionIntroActivity(4));
                            } else {
                                presentFragment(new PeopleNearbyActivity());
                            }
                        } else {
                            presentFragment(new ActionIntroActivity(1));
                        }
                    }
                } else if (inviteViaLink != 0) {
                    if (row == 0) {
                        long j = this.chatId;
                        if (j == 0) {
                            j = this.channelId;
                        }
                        presentFragment(new GroupInviteActivity(j));
                    }
                } else if (row == 0) {
                    presentFragment(new GroupCreateActivity(new Bundle()), false);
                } else if (row == 1) {
                    Bundle args3 = new Bundle();
                    args3.putBoolean("onlyUsers", true);
                    args3.putBoolean("destroyAfterSelect", true);
                    args3.putBoolean("createSecretChat", true);
                    args3.putBoolean("allowBots", false);
                    args3.putBoolean("allowSelf", false);
                    presentFragment(new ContactsActivity(args3), false);
                } else if (row == 2) {
                    SharedPreferences preferences = MessagesController.getGlobalMainSettings();
                    if (BuildVars.DEBUG_VERSION || !preferences.getBoolean("channel_intro", false)) {
                        presentFragment(new ActionIntroActivity(0));
                        preferences.edit().putBoolean("channel_intro", true).commit();
                        return;
                    }
                    Bundle args4 = new Bundle();
                    args4.putInt("step", 0);
                    presentFragment(new ChannelCreateActivity(args4));
                }
            }
        }
    }

    /* renamed from: lambda$createView$0$org-telegram-ui-ContactsActivity  reason: not valid java name */
    public /* synthetic */ void m2032lambda$createView$0$orgtelegramuiContactsActivity(String arg1, DialogInterface dialogInterface, int i) {
        try {
            Intent intent = new Intent("android.intent.action.VIEW", Uri.fromParts("sms", arg1, (String) null));
            intent.putExtra("sms_body", ContactsController.getInstance(this.currentAccount).getInviteText(1));
            getParentActivity().startActivityForResult(intent, 500);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    /* renamed from: lambda$createView$2$org-telegram-ui-ContactsActivity  reason: not valid java name */
    public /* synthetic */ void m2034lambda$createView$2$orgtelegramuiContactsActivity(View v) {
        presentFragment(new NewContactActivity());
    }

    private void didSelectResult(TLRPC.User user, boolean useAlert, String param) {
        TLRPC.User user2 = user;
        String str = param;
        if (!useAlert || this.selectAlertString == null) {
            ContactsActivityDelegate contactsActivityDelegate = this.delegate;
            if (contactsActivityDelegate != null) {
                contactsActivityDelegate.didSelectContact(user2, str, this);
                if (this.resetDelegate) {
                    this.delegate = null;
                }
            }
            if (this.needFinishFragment) {
                finishFragment();
            }
        } else if (getParentActivity() != null) {
            if (user2.bot) {
                if (user2.bot_nochats) {
                    try {
                        BulletinFactory.of(this).createErrorBulletin(LocaleController.getString("BotCantJoinGroups", NUM)).show();
                        return;
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                        return;
                    }
                } else if (this.channelId != 0) {
                    TLRPC.Chat chat = getMessagesController().getChat(Long.valueOf(this.channelId));
                    AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
                    if (ChatObject.canAddAdmins(chat)) {
                        builder.setTitle(LocaleController.getString("AppName", NUM));
                        builder.setMessage(LocaleController.getString("AddBotAsAdmin", NUM));
                        builder.setPositiveButton(LocaleController.getString("MakeAdmin", NUM), new ContactsActivity$$ExternalSyntheticLambda3(this, user2, str));
                        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                    } else {
                        builder.setMessage(LocaleController.getString("CantAddBotAsAdmin", NUM));
                        builder.setPositiveButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null);
                    }
                    showDialog(builder.create());
                    return;
                }
            }
            AlertDialog.Builder builder2 = new AlertDialog.Builder((Context) getParentActivity());
            builder2.setTitle(LocaleController.getString("AppName", NUM));
            String message = LocaleController.formatStringSimple(this.selectAlertString, UserObject.getUserName(user));
            EditTextBoldCursor editText = null;
            if (!user2.bot && this.needForwardCount) {
                message = String.format("%s\n\n%s", new Object[]{message, LocaleController.getString("AddToTheGroupForwardCount", NUM)});
                editText = new EditTextBoldCursor(getParentActivity());
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
                                    EditText editText = editTextFinal;
                                    editText.setSelection(editText.length());
                                } else if (value > 300) {
                                    editTextFinal.setText("300");
                                    EditText editText2 = editTextFinal;
                                    editText2.setSelection(editText2.length());
                                } else {
                                    if (!str.equals("" + value)) {
                                        EditText editText3 = editTextFinal;
                                        editText3.setText("" + value);
                                        EditText editText4 = editTextFinal;
                                        editText4.setSelection(editText4.length());
                                    }
                                }
                            }
                        } catch (Exception e) {
                            FileLog.e((Throwable) e);
                        }
                    }
                });
                builder2.setView(editText);
            }
            builder2.setMessage(message);
            builder2.setPositiveButton(LocaleController.getString("OK", NUM), new ContactsActivity$$ExternalSyntheticLambda2(this, user2, editText));
            builder2.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
            showDialog(builder2.create());
            if (editText != null) {
                ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) editText.getLayoutParams();
                if (layoutParams != null) {
                    if (layoutParams instanceof FrameLayout.LayoutParams) {
                        ((FrameLayout.LayoutParams) layoutParams).gravity = 1;
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

    /* renamed from: lambda$didSelectResult$3$org-telegram-ui-ContactsActivity  reason: not valid java name */
    public /* synthetic */ void m2035lambda$didSelectResult$3$orgtelegramuiContactsActivity(TLRPC.User user, String param, DialogInterface dialogInterface, int i) {
        ContactsActivityDelegate contactsActivityDelegate = this.delegate;
        if (contactsActivityDelegate != null) {
            contactsActivityDelegate.didSelectContact(user, param, this);
            this.delegate = null;
        }
    }

    /* renamed from: lambda$didSelectResult$4$org-telegram-ui-ContactsActivity  reason: not valid java name */
    public /* synthetic */ void m2036lambda$didSelectResult$4$orgtelegramuiContactsActivity(TLRPC.User user, EditText finalEditText, DialogInterface dialogInterface, int i) {
        didSelectResult(user, false, finalEditText != null ? finalEditText.getText().toString() : "0");
    }

    public void onResume() {
        Activity activity;
        super.onResume();
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
        ContactsAdapter contactsAdapter = this.listViewAdapter;
        if (contactsAdapter != null) {
            contactsAdapter.notifyDataSetChanged();
        }
        if (this.checkPermission && Build.VERSION.SDK_INT >= 23 && (activity = getParentActivity()) != null) {
            this.checkPermission = false;
            if (activity.checkSelfPermission("android.permission.READ_CONTACTS") == 0) {
                return;
            }
            if (activity.shouldShowRequestPermissionRationale("android.permission.READ_CONTACTS")) {
                AlertDialog create = AlertsCreator.createContactsPermissionDialog(activity, new ContactsActivity$$ExternalSyntheticLambda7(this)).create();
                this.permissionDialog = create;
                showDialog(create);
                return;
            }
            askForPermissons(true);
        }
    }

    /* renamed from: lambda$onResume$5$org-telegram-ui-ContactsActivity  reason: not valid java name */
    public /* synthetic */ void m2039lambda$onResume$5$orgtelegramuiContactsActivity(int param) {
        this.askAboutContacts = param != 0;
        if (param != 0) {
            askForPermissons(false);
        }
    }

    /* access modifiers changed from: protected */
    public RecyclerListView getListView() {
        return this.listView;
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        FrameLayout frameLayout = this.floatingButtonContainer;
        if (frameLayout != null) {
            frameLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                public void onGlobalLayout() {
                    ContactsActivity.this.floatingButtonContainer.setTranslationY((float) (ContactsActivity.this.floatingHidden ? AndroidUtilities.dp(100.0f) : 0));
                    ContactsActivity.this.floatingButtonContainer.setClickable(!ContactsActivity.this.floatingHidden);
                    if (ContactsActivity.this.floatingButtonContainer != null) {
                        ContactsActivity.this.floatingButtonContainer.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                }
            });
        }
    }

    /* access modifiers changed from: protected */
    public void onDialogDismiss(Dialog dialog) {
        super.onDialogDismiss(dialog);
        AlertDialog alertDialog = this.permissionDialog;
        if (alertDialog != null && dialog == alertDialog && getParentActivity() != null && this.askAboutContacts) {
            askForPermissons(false);
        }
    }

    private void askForPermissons(boolean alert) {
        Activity activity = getParentActivity();
        if (activity != null && UserConfig.getInstance(this.currentAccount).syncContacts && activity.checkSelfPermission("android.permission.READ_CONTACTS") != 0) {
            if (!alert || !this.askAboutContacts) {
                this.permissionRequestTime = SystemClock.elapsedRealtime();
                ArrayList<String> permissons = new ArrayList<>();
                permissons.add("android.permission.READ_CONTACTS");
                permissons.add("android.permission.WRITE_CONTACTS");
                permissons.add("android.permission.GET_ACCOUNTS");
                try {
                    activity.requestPermissions((String[]) permissons.toArray(new String[0]), 1);
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
            } else {
                showDialog(AlertsCreator.createContactsPermissionDialog(activity, new ContactsActivity$$ExternalSyntheticLambda6(this)).create());
            }
        }
    }

    /* renamed from: lambda$askForPermissons$6$org-telegram-ui-ContactsActivity  reason: not valid java name */
    public /* synthetic */ void m2031lambda$askForPermissons$6$orgtelegramuiContactsActivity(int param) {
        this.askAboutContacts = param != 0;
        if (param != 0) {
            askForPermissons(false);
        }
    }

    public void onRequestPermissionsResultFragment(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1) {
            int a = 0;
            while (a < permissions.length) {
                if (grantResults.length <= a || !"android.permission.READ_CONTACTS".equals(permissions[a])) {
                    a++;
                } else if (grantResults[a] == 0) {
                    ContactsController.getInstance(this.currentAccount).forceImportContacts();
                    return;
                } else {
                    SharedPreferences.Editor edit = MessagesController.getGlobalNotificationsSettings().edit();
                    this.askAboutContacts = false;
                    edit.putBoolean("askAboutContacts", false).commit();
                    if (SystemClock.elapsedRealtime() - this.permissionRequestTime < 200) {
                        try {
                            Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
                            intent.setData(Uri.fromParts("package", ApplicationLoader.applicationContext.getPackageName(), (String) null));
                            getParentActivity().startActivity(intent);
                            return;
                        } catch (Exception e) {
                            FileLog.e((Throwable) e);
                            return;
                        }
                    } else {
                        return;
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
        ContactsAdapter contactsAdapter;
        if (id == NotificationCenter.contactsDidLoad) {
            ContactsAdapter contactsAdapter2 = this.listViewAdapter;
            if (contactsAdapter2 != null) {
                if (!this.sortByName) {
                    contactsAdapter2.setSortType(2, true);
                }
                this.listViewAdapter.notifyDataSetChanged();
            }
        } else if (id == NotificationCenter.updateInterfaces) {
            int mask = args[0].intValue();
            if (!((MessagesController.UPDATE_MASK_AVATAR & mask) == 0 && (MessagesController.UPDATE_MASK_NAME & mask) == 0 && (MessagesController.UPDATE_MASK_STATUS & mask) == 0)) {
                updateVisibleRows(mask);
            }
            if ((MessagesController.UPDATE_MASK_STATUS & mask) != 0 && !this.sortByName && (contactsAdapter = this.listViewAdapter) != null) {
                contactsAdapter.sortOnlineContacts();
            }
        } else if (id == NotificationCenter.encryptedChatCreated) {
            if (this.createSecretChat && this.creatingChat) {
                Bundle args2 = new Bundle();
                args2.putInt("enc_id", args[0].id);
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                presentFragment(new ChatActivity(args2), true);
            }
        } else if (id == NotificationCenter.closeChats && !this.creatingChat) {
            removeSelfFromStack();
        }
    }

    private void updateVisibleRows(int mask) {
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            int count = recyclerListView.getChildCount();
            for (int a = 0; a < count; a++) {
                View child = this.listView.getChildAt(a);
                if (child instanceof UserCell) {
                    ((UserCell) child).update(mask);
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void hideFloatingButton(boolean hide) {
        if (this.floatingHidden != hide) {
            this.floatingHidden = hide;
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
            this.floatingButtonContainer.setClickable(!hide);
            animatorSet.start();
        }
    }

    public void setDelegate(ContactsActivityDelegate delegate2) {
        this.delegate = delegate2;
    }

    public void setIgnoreUsers(LongSparseArray<TLRPC.User> users) {
        this.ignoreUsers = users;
    }

    public void setInitialSearchString(String initialSearchString2) {
        this.initialSearchString = initialSearchString2;
    }

    /* access modifiers changed from: private */
    public void showItemsAnimated() {
        LinearLayoutManager linearLayoutManager = this.layoutManager;
        final int from = linearLayoutManager == null ? 0 : linearLayoutManager.findLastVisibleItemPosition();
        this.listView.invalidate();
        this.listView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                ContactsActivity.this.listView.getViewTreeObserver().removeOnPreDrawListener(this);
                int n = ContactsActivity.this.listView.getChildCount();
                AnimatorSet animatorSet = new AnimatorSet();
                for (int i = 0; i < n; i++) {
                    View child = ContactsActivity.this.listView.getChildAt(i);
                    if (ContactsActivity.this.listView.getChildAdapterPosition(child) > from) {
                        child.setAlpha(0.0f);
                        ObjectAnimator a = ObjectAnimator.ofFloat(child, View.ALPHA, new float[]{0.0f, 1.0f});
                        a.setStartDelay((long) ((int) ((((float) Math.min(ContactsActivity.this.listView.getMeasuredHeight(), Math.max(0, child.getTop()))) / ((float) ContactsActivity.this.listView.getMeasuredHeight())) * 100.0f)));
                        a.setDuration(200);
                        animatorSet.playTogether(new Animator[]{a});
                    }
                }
                animatorSet.start();
                return true;
            }
        });
    }

    /* access modifiers changed from: protected */
    public AnimatorSet onCustomTransitionAnimation(boolean isOpen, Runnable callback) {
        ValueAnimator valueAnimator;
        DialogsActivity dialogsActivity;
        boolean z = isOpen;
        float[] fArr = {0.0f, 1.0f};
        if (z) {
            // fill-array-data instruction
            fArr[0] = NUM;
            fArr[1] = 0;
            valueAnimator = ValueAnimator.ofFloat(fArr);
        } else {
            valueAnimator = ValueAnimator.ofFloat(fArr);
        }
        ValueAnimator valueAnimator2 = valueAnimator;
        ViewGroup parent = (ViewGroup) this.fragmentView.getParent();
        BaseFragment previousFragment = this.parentLayout.fragmentsStack.size() > 1 ? this.parentLayout.fragmentsStack.get(this.parentLayout.fragmentsStack.size() - 2) : null;
        if (previousFragment instanceof DialogsActivity) {
            dialogsActivity = (DialogsActivity) previousFragment;
        } else {
            dialogsActivity = null;
        }
        if (dialogsActivity == null) {
            return null;
        }
        RLottieImageView previousFab = dialogsActivity.getFloatingButton();
        View previousFabContainer = previousFab.getParent() != null ? (View) previousFab.getParent() : null;
        if (this.floatingButtonContainer == null || previousFabContainer == null || previousFab.getVisibility() != 0 || Math.abs(previousFabContainer.getTranslationY()) > ((float) AndroidUtilities.dp(4.0f)) || Math.abs(this.floatingButtonContainer.getTranslationY()) > ((float) AndroidUtilities.dp(4.0f))) {
            return null;
        }
        previousFabContainer.setVisibility(8);
        if (z) {
            parent.setAlpha(0.0f);
        }
        valueAnimator2.addUpdateListener(new ContactsActivity$$ExternalSyntheticLambda0(valueAnimator2, parent));
        if (this.floatingButtonContainer != null) {
            ((ViewGroup) this.fragmentView).removeView(this.floatingButtonContainer);
            ((FrameLayout) parent.getParent()).addView(this.floatingButtonContainer);
        }
        valueAnimator2.setDuration(150);
        valueAnimator2.setInterpolator(new DecelerateInterpolator(1.5f));
        AnimatorSet animatorSet = new AnimatorSet();
        final View view = previousFabContainer;
        final boolean z2 = isOpen;
        final RLottieImageView rLottieImageView = previousFab;
        AnonymousClass12 r10 = r0;
        final Runnable runnable = callback;
        AnonymousClass12 r0 = new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                if (ContactsActivity.this.floatingButtonContainer != null) {
                    if (ContactsActivity.this.floatingButtonContainer.getParent() instanceof ViewGroup) {
                        ((ViewGroup) ContactsActivity.this.floatingButtonContainer.getParent()).removeView(ContactsActivity.this.floatingButtonContainer);
                    }
                    ((ViewGroup) ContactsActivity.this.fragmentView).addView(ContactsActivity.this.floatingButtonContainer);
                    view.setVisibility(0);
                    if (!z2) {
                        rLottieImageView.setAnimation(NUM, 52, 52);
                        rLottieImageView.getAnimatedDrawable().setCurrentFrame(ContactsActivity.this.floatingButton.getAnimatedDrawable().getCurrentFrame());
                        rLottieImageView.playAnimation();
                    }
                }
                runnable.run();
            }
        };
        animatorSet.addListener(r10);
        animatorSet.playTogether(new Animator[]{valueAnimator2});
        AndroidUtilities.runOnUIThread(new ContactsActivity$$ExternalSyntheticLambda5(this, animatorSet, z, previousFabContainer), 50);
        return animatorSet;
    }

    static /* synthetic */ void lambda$onCustomTransitionAnimation$7(ValueAnimator valueAnimator, ViewGroup parent, ValueAnimator valueAnimator1) {
        float v = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        parent.setTranslationX(((float) AndroidUtilities.dp(48.0f)) * v);
        parent.setAlpha(1.0f - v);
    }

    /* renamed from: lambda$onCustomTransitionAnimation$8$org-telegram-ui-ContactsActivity  reason: not valid java name */
    public /* synthetic */ void m2038xa0ce3b06(AnimatorSet animatorSet, boolean isOpen, View previousFabContainer) {
        final View view = previousFabContainer;
        this.animationIndex = getNotificationCenter().setAnimationInProgress(this.animationIndex, new int[]{NotificationCenter.diceStickersDidLoad}, false);
        animatorSet.start();
        if (isOpen) {
            this.floatingButton.setAnimation(NUM, 52, 52);
            this.floatingButton.playAnimation();
        } else {
            this.floatingButton.setAnimation(NUM, 52, 52);
            this.floatingButton.playAnimation();
        }
        AnimatorSet animatorSet2 = this.bounceIconAnimator;
        if (animatorSet2 != null) {
            animatorSet2.cancel();
        }
        this.bounceIconAnimator = new AnimatorSet();
        float totalDuration = (float) this.floatingButton.getAnimatedDrawable().getDuration();
        long delay = 0;
        int i = 4;
        if (isOpen) {
            for (int i2 = 0; i2 < 6; i2++) {
                AnimatorSet set = new AnimatorSet();
                if (i2 == 0) {
                    set.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.floatingButton, View.SCALE_X, new float[]{1.0f, 0.9f}), ObjectAnimator.ofFloat(this.floatingButton, View.SCALE_Y, new float[]{1.0f, 0.9f}), ObjectAnimator.ofFloat(view, View.SCALE_X, new float[]{1.0f, 0.9f}), ObjectAnimator.ofFloat(view, View.SCALE_Y, new float[]{1.0f, 0.9f})});
                    set.setDuration((long) (0.12765957f * totalDuration));
                    set.setInterpolator(CubicBezierInterpolator.EASE_OUT);
                } else if (i2 == 1) {
                    set.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.floatingButton, View.SCALE_X, new float[]{0.9f, 1.06f}), ObjectAnimator.ofFloat(this.floatingButton, View.SCALE_Y, new float[]{0.9f, 1.06f}), ObjectAnimator.ofFloat(view, View.SCALE_X, new float[]{0.9f, 1.06f}), ObjectAnimator.ofFloat(view, View.SCALE_Y, new float[]{0.9f, 1.06f})});
                    set.setDuration((long) (0.3617021f * totalDuration));
                    set.setInterpolator(CubicBezierInterpolator.EASE_BOTH);
                } else if (i2 == 2) {
                    set.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.floatingButton, View.SCALE_X, new float[]{1.06f, 0.9f}), ObjectAnimator.ofFloat(this.floatingButton, View.SCALE_Y, new float[]{1.06f, 0.9f}), ObjectAnimator.ofFloat(view, View.SCALE_X, new float[]{1.06f, 0.9f}), ObjectAnimator.ofFloat(view, View.SCALE_Y, new float[]{1.06f, 0.9f})});
                    set.setDuration((long) (0.21276596f * totalDuration));
                    set.setInterpolator(CubicBezierInterpolator.EASE_BOTH);
                } else if (i2 == 3) {
                    set.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.floatingButton, View.SCALE_X, new float[]{0.9f, 1.03f}), ObjectAnimator.ofFloat(this.floatingButton, View.SCALE_Y, new float[]{0.9f, 1.03f}), ObjectAnimator.ofFloat(view, View.SCALE_X, new float[]{0.9f, 1.03f}), ObjectAnimator.ofFloat(view, View.SCALE_Y, new float[]{0.9f, 1.03f})});
                    set.setDuration((long) (totalDuration * 0.10638298f));
                    set.setInterpolator(CubicBezierInterpolator.EASE_BOTH);
                } else if (i2 == 4) {
                    set.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.floatingButton, View.SCALE_X, new float[]{1.03f, 0.98f}), ObjectAnimator.ofFloat(this.floatingButton, View.SCALE_Y, new float[]{1.03f, 0.98f}), ObjectAnimator.ofFloat(view, View.SCALE_X, new float[]{1.03f, 0.98f}), ObjectAnimator.ofFloat(view, View.SCALE_Y, new float[]{1.03f, 0.98f})});
                    set.setDuration((long) (totalDuration * 0.10638298f));
                    set.setInterpolator(CubicBezierInterpolator.EASE_BOTH);
                } else {
                    set.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.floatingButton, View.SCALE_X, new float[]{0.98f, 1.0f}), ObjectAnimator.ofFloat(this.floatingButton, View.SCALE_Y, new float[]{0.98f, 1.0f}), ObjectAnimator.ofFloat(view, View.SCALE_X, new float[]{0.98f, 1.0f}), ObjectAnimator.ofFloat(view, View.SCALE_Y, new float[]{0.98f, 1.0f})});
                    set.setDuration((long) (0.08510638f * totalDuration));
                    set.setInterpolator(CubicBezierInterpolator.EASE_IN);
                }
                set.setStartDelay(delay);
                delay += set.getDuration();
                this.bounceIconAnimator.playTogether(new Animator[]{set});
            }
        } else {
            for (int i3 = 0; i3 < 5; i3++) {
                AnimatorSet set2 = new AnimatorSet();
                if (i3 == 0) {
                    Animator[] animatorArr = new Animator[i];
                    animatorArr[0] = ObjectAnimator.ofFloat(this.floatingButton, View.SCALE_X, new float[]{1.0f, 0.9f});
                    animatorArr[1] = ObjectAnimator.ofFloat(this.floatingButton, View.SCALE_Y, new float[]{1.0f, 0.9f});
                    animatorArr[2] = ObjectAnimator.ofFloat(view, View.SCALE_X, new float[]{1.0f, 0.9f});
                    animatorArr[3] = ObjectAnimator.ofFloat(view, View.SCALE_Y, new float[]{1.0f, 0.9f});
                    set2.playTogether(animatorArr);
                    set2.setDuration((long) (0.19444445f * totalDuration));
                    set2.setInterpolator(CubicBezierInterpolator.EASE_OUT);
                    i = 4;
                } else if (i3 == 1) {
                    set2.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.floatingButton, View.SCALE_X, new float[]{0.9f, 1.06f}), ObjectAnimator.ofFloat(this.floatingButton, View.SCALE_Y, new float[]{0.9f, 1.06f}), ObjectAnimator.ofFloat(view, View.SCALE_X, new float[]{0.9f, 1.06f}), ObjectAnimator.ofFloat(view, View.SCALE_Y, new float[]{0.9f, 1.06f})});
                    set2.setDuration((long) (0.22222222f * totalDuration));
                    set2.setInterpolator(CubicBezierInterpolator.EASE_BOTH);
                    i = 4;
                } else if (i3 == 2) {
                    set2.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.floatingButton, View.SCALE_X, new float[]{1.06f, 0.92f}), ObjectAnimator.ofFloat(this.floatingButton, View.SCALE_Y, new float[]{1.06f, 0.92f}), ObjectAnimator.ofFloat(view, View.SCALE_X, new float[]{1.06f, 0.92f}), ObjectAnimator.ofFloat(view, View.SCALE_Y, new float[]{1.06f, 0.92f})});
                    set2.setDuration((long) (0.19444445f * totalDuration));
                    set2.setInterpolator(CubicBezierInterpolator.EASE_BOTH);
                    i = 4;
                } else if (i3 == 3) {
                    set2.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.floatingButton, View.SCALE_X, new float[]{0.92f, 1.02f}), ObjectAnimator.ofFloat(this.floatingButton, View.SCALE_Y, new float[]{0.92f, 1.02f}), ObjectAnimator.ofFloat(view, View.SCALE_X, new float[]{0.92f, 1.02f}), ObjectAnimator.ofFloat(view, View.SCALE_Y, new float[]{0.92f, 1.02f})});
                    set2.setDuration((long) (0.25f * totalDuration));
                    set2.setInterpolator(CubicBezierInterpolator.EASE_BOTH);
                    i = 4;
                } else {
                    i = 4;
                    set2.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.floatingButton, View.SCALE_X, new float[]{1.02f, 1.0f}), ObjectAnimator.ofFloat(this.floatingButton, View.SCALE_Y, new float[]{1.02f, 1.0f}), ObjectAnimator.ofFloat(view, View.SCALE_X, new float[]{1.02f, 1.0f}), ObjectAnimator.ofFloat(view, View.SCALE_Y, new float[]{1.02f, 1.0f})});
                    set2.setDuration((long) (totalDuration * 0.10638298f));
                    set2.setInterpolator(CubicBezierInterpolator.EASE_IN);
                }
                set2.setStartDelay(delay);
                delay += set2.getDuration();
                this.bounceIconAnimator.playTogether(new Animator[]{set2});
            }
        }
        this.bounceIconAnimator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                ContactsActivity.this.floatingButton.setScaleX(1.0f);
                ContactsActivity.this.floatingButton.setScaleY(1.0f);
                view.setScaleX(1.0f);
                view.setScaleY(1.0f);
                AnimatorSet unused = ContactsActivity.this.bounceIconAnimator = null;
                ContactsActivity.this.getNotificationCenter().onAnimationFinish(ContactsActivity.this.animationIndex);
            }
        });
        this.bounceIconAnimator.start();
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();
        ThemeDescription.ThemeDescriptionDelegate cellDelegate = new ContactsActivity$$ExternalSyntheticLambda8(this);
        themeDescriptions.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultIcon"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultTitle"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCH, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSearch"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCHPLACEHOLDER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSearchPlaceholder"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_SECTIONS, new Class[]{LetterSectionCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText4"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "divider"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_FASTSCROLL, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "fastScrollActive"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_FASTSCROLL, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "fastScrollInactive"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_FASTSCROLL, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "fastScrollText"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{UserCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{UserCell.class}, new String[]{"statusColor"}, (Paint[]) null, (Drawable[]) null, cellDelegate, "windowBackgroundWhiteGrayText"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{UserCell.class}, new String[]{"statusOnlineColor"}, (Paint[]) null, (Drawable[]) null, cellDelegate, "windowBackgroundWhiteBlueText"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, (Paint) null, Theme.avatarDrawables, (ThemeDescription.ThemeDescriptionDelegate) null, "avatar_text"));
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate = cellDelegate;
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, "avatar_backgroundRed"));
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate2 = cellDelegate;
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, "avatar_backgroundOrange"));
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, "avatar_backgroundViolet"));
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, "avatar_backgroundGreen"));
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, "avatar_backgroundCyan"));
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, "avatar_backgroundBlue"));
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, "avatar_backgroundPink"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueText2"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayIcon"));
        themeDescriptions.add(new ThemeDescription(this.floatingButton, ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_actionIcon"));
        themeDescriptions.add(new ThemeDescription(this.floatingButton, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_actionBackground"));
        themeDescriptions.add(new ThemeDescription(this.floatingButton, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_actionPressedBackground"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{GraySectionCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "key_graySectionText"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{GraySectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "graySection"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{ProfileSearchCell.class}, (Paint) null, new Drawable[]{Theme.dialogs_groupDrawable, Theme.dialogs_broadcastDrawable, Theme.dialogs_botDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_nameIcon"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{ProfileSearchCell.class}, (Paint) null, new Drawable[]{Theme.dialogs_verifiedCheckDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_verifiedCheck"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{ProfileSearchCell.class}, (Paint) null, new Drawable[]{Theme.dialogs_verifiedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_verifiedBackground"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{ProfileSearchCell.class}, Theme.dialogs_offlinePaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText3"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{ProfileSearchCell.class}, Theme.dialogs_onlinePaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueText3"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{ProfileSearchCell.class}, (String[]) null, new Paint[]{Theme.dialogs_namePaint[0], Theme.dialogs_namePaint[1], Theme.dialogs_searchNamePaint}, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_name"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{ProfileSearchCell.class}, (String[]) null, new Paint[]{Theme.dialogs_nameEncryptedPaint[0], Theme.dialogs_nameEncryptedPaint[1], Theme.dialogs_searchNameEncryptedPaint}, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_secretName"));
        return themeDescriptions;
    }

    /* renamed from: lambda$getThemeDescriptions$9$org-telegram-ui-ContactsActivity  reason: not valid java name */
    public /* synthetic */ void m2037lambda$getThemeDescriptions$9$orgtelegramuiContactsActivity() {
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            int count = recyclerListView.getChildCount();
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
