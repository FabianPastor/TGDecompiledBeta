package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
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
import android.text.TextUtils;
import android.util.Property;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewOutlineProvider;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DataQuery;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.R;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.XiaomiUtilities;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.LinearSmoothScrollerMiddle;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.support.widget.RecyclerView.Adapter;
import org.telegram.messenger.support.widget.RecyclerView.OnScrollListener;
import org.telegram.messenger.support.widget.RecyclerView.State;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.ChatInvite;
import org.telegram.tgnet.TLRPC.EncryptedChat;
import org.telegram.tgnet.TLRPC.StickerSet;
import org.telegram.tgnet.TLRPC.TL_dialog;
import org.telegram.tgnet.TLRPC.TL_inputStickerSetID;
import org.telegram.tgnet.TLRPC.TL_recentMeUrlChat;
import org.telegram.tgnet.TLRPC.TL_recentMeUrlChatInvite;
import org.telegram.tgnet.TLRPC.TL_recentMeUrlStickerSet;
import org.telegram.tgnet.TLRPC.TL_recentMeUrlUnknown;
import org.telegram.tgnet.TLRPC.TL_recentMeUrlUser;
import org.telegram.tgnet.TLRPC.TL_userEmpty;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet.Builder;
import org.telegram.ui.ActionBar.MenuDrawable;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate;
import org.telegram.ui.Adapters.DialogsAdapter;
import org.telegram.ui.Adapters.DialogsSearchAdapter;
import org.telegram.ui.Adapters.DialogsSearchAdapter.DialogsSearchAdapterDelegate;
import org.telegram.ui.Cells.AccountSelectCell;
import org.telegram.ui.Cells.DialogCell;
import org.telegram.ui.Cells.DialogsEmptyCell;
import org.telegram.ui.Cells.DividerCell;
import org.telegram.ui.Cells.DrawerActionCell;
import org.telegram.ui.Cells.DrawerAddCell;
import org.telegram.ui.Cells.DrawerProfileCell;
import org.telegram.ui.Cells.DrawerUserCell;
import org.telegram.ui.Cells.GraySectionCell;
import org.telegram.ui.Cells.HashtagSearchCell;
import org.telegram.ui.Cells.HintDialogCell;
import org.telegram.ui.Cells.LoadingCell;
import org.telegram.ui.Cells.ProfileSearchCell;
import org.telegram.ui.Cells.UserCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.AnimatedArrowDrawable;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.ChatActivityEnterView;
import org.telegram.ui.Components.ChatActivityEnterView.ChatActivityEnterViewDelegate;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.FragmentContextView;
import org.telegram.ui.Components.JoinGroupAlert;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.ProxyDrawable;
import org.telegram.ui.Components.RadialProgressView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.OnItemLongClickListenerExtended;
import org.telegram.ui.Components.SizeNotifierFrameLayout;
import org.telegram.ui.Components.StickersAlert;
import org.telegram.ui.Components.UndoView;

public class DialogsActivity extends BaseFragment implements NotificationCenterDelegate {
    public static boolean[] dialogsLoaded = new boolean[3];
    private String addToGroupAlertString;
    private float additionalFloatingTranslation;
    private boolean allowSwitchAccount;
    private AnimatedArrowDrawable arrowDrawable;
    private boolean askAboutContacts = true;
    private boolean cantSendToChannels;
    private boolean checkPermission = true;
    private boolean closeSearchFieldOnHide;
    private ChatActivityEnterView commentView;
    private int currentConnectionState;
    private DialogsActivityDelegate delegate;
    private DialogsAdapter dialogsAdapter;
    private DialogsSearchAdapter dialogsSearchAdapter;
    private int dialogsType;
    private ImageView floatingButton;
    private FrameLayout floatingButtonContainer;
    private boolean floatingHidden;
    private final AccelerateDecelerateInterpolator floatingInterpolator = new AccelerateDecelerateInterpolator();
    private LinearLayoutManager layoutManager;
    private RecyclerListView listView;
    private boolean onlySelect;
    private long openedDialogId;
    private ActionBarMenuItem passcodeItem;
    private AlertDialog permissionDialog;
    private int prevPosition;
    private int prevTop;
    private RadialProgressView progressView;
    private ProxyDrawable proxyDrawable;
    private ActionBarMenuItem proxyItem;
    private boolean proxyItemVisisble;
    private boolean scrollUpdated;
    private long searchDialogId;
    private EmptyTextProgressView searchEmptyView;
    private TLObject searchObject;
    private String searchString;
    private boolean searchWas;
    private boolean searching;
    private String selectAlertString;
    private String selectAlertStringGroup;
    private RecyclerView sideMenu;
    private ActionBarMenuItem switchItem;
    private UndoView undoView;

    public interface DialogsActivityDelegate {
        void didSelectDialogs(DialogsActivity dialogsActivity, ArrayList<Long> arrayList, CharSequence charSequence, boolean z);
    }

    public DialogsActivity(Bundle args) {
        super(args);
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        if (getArguments() != null) {
            this.onlySelect = this.arguments.getBoolean("onlySelect", false);
            this.cantSendToChannels = this.arguments.getBoolean("cantSendToChannels", false);
            this.dialogsType = this.arguments.getInt("dialogsType", 0);
            this.selectAlertString = this.arguments.getString("selectAlertString");
            this.selectAlertStringGroup = this.arguments.getString("selectAlertStringGroup");
            this.addToGroupAlertString = this.arguments.getString("addToGroupAlertString");
            this.allowSwitchAccount = this.arguments.getBoolean("allowSwitchAccount");
        }
        if (this.dialogsType == 0) {
            this.askAboutContacts = MessagesController.getGlobalNotificationsSettings().getBoolean("askAboutContacts", true);
            SharedConfig.loadProxyList();
        }
        if (this.searchString == null) {
            this.currentConnectionState = ConnectionsManager.getInstance(this.currentAccount).getConnectionState();
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.dialogsNeedReload);
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiDidLoad);
            if (!this.onlySelect) {
                NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.closeSearchByActiveAction);
                NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.proxySettingsChanged);
            }
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.updateInterfaces);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.encryptedChatUpdated);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.contactsDidLoad);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.appDidLogout);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.openedChatChanged);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.notificationsSettingsUpdated);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messageReceivedByAck);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messageReceivedByServer);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messageSendError);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.needReloadRecentDialogsSearch);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.replyMessagesDidLoad);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.reloadHints);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.didUpdateConnectionState);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.dialogsUnreadCounterChanged);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.needDeleteDialog);
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didSetPasscode);
        }
        if (!dialogsLoaded[this.currentAccount]) {
            MessagesController.getInstance(this.currentAccount).loadGlobalNotificationsSettings();
            MessagesController.getInstance(this.currentAccount).loadDialogs(0, 100, true);
            MessagesController.getInstance(this.currentAccount).loadHintDialogs();
            ContactsController.getInstance(this.currentAccount).checkInviteText();
            MessagesController.getInstance(this.currentAccount).loadPinnedDialogs(0, null);
            DataQuery.getInstance(this.currentAccount).loadRecents(2, false, true, false);
            DataQuery.getInstance(this.currentAccount).checkFeaturedStickers();
            dialogsLoaded[this.currentAccount] = true;
        }
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        if (this.searchString == null) {
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.dialogsNeedReload);
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiDidLoad);
            if (!this.onlySelect) {
                NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.closeSearchByActiveAction);
                NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.proxySettingsChanged);
            }
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.updateInterfaces);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.encryptedChatUpdated);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.contactsDidLoad);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.appDidLogout);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.openedChatChanged);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.notificationsSettingsUpdated);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messageReceivedByAck);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messageReceivedByServer);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messageSendError);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.needReloadRecentDialogsSearch);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.replyMessagesDidLoad);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.reloadHints);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.didUpdateConnectionState);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.dialogsUnreadCounterChanged);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.needDeleteDialog);
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didSetPasscode);
        }
        if (this.commentView != null) {
            this.commentView.onDestroy();
        }
        if (this.undoView != null) {
            this.undoView.hide(true, false);
        }
        this.delegate = null;
    }

    public View createView(Context context) {
        View backupImageView;
        int i;
        int i2;
        float f;
        float f2;
        this.searching = false;
        this.searchWas = false;
        AndroidUtilities.runOnUIThread(new DialogsActivity$$Lambda$0(context));
        ActionBarMenu menu = this.actionBar.createMenu();
        if (!this.onlySelect && this.searchString == null) {
            this.proxyDrawable = new ProxyDrawable(context);
            this.proxyItem = menu.addItem(2, this.proxyDrawable);
            this.passcodeItem = menu.addItem(1, (int) R.drawable.lock_close);
            updatePasscodeButton();
            updateProxyButton(false);
        }
        menu.addItem(0, (int) R.drawable.ic_ab_search).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItemSearchListener() {
            public void onSearchExpand() {
                DialogsActivity.this.searching = true;
                if (DialogsActivity.this.switchItem != null) {
                    DialogsActivity.this.switchItem.setVisibility(8);
                }
                if (DialogsActivity.this.proxyItem != null && DialogsActivity.this.proxyItemVisisble) {
                    DialogsActivity.this.proxyItem.setVisibility(8);
                }
                if (DialogsActivity.this.listView != null) {
                    if (DialogsActivity.this.searchString != null) {
                        DialogsActivity.this.listView.setEmptyView(DialogsActivity.this.searchEmptyView);
                        DialogsActivity.this.progressView.setVisibility(8);
                    }
                    if (!DialogsActivity.this.onlySelect) {
                        DialogsActivity.this.floatingButtonContainer.setVisibility(8);
                    }
                }
                DialogsActivity.this.updatePasscodeButton();
            }

            public boolean canCollapseSearch() {
                if (DialogsActivity.this.switchItem != null) {
                    DialogsActivity.this.switchItem.setVisibility(0);
                }
                if (DialogsActivity.this.proxyItem != null && DialogsActivity.this.proxyItemVisisble) {
                    DialogsActivity.this.proxyItem.setVisibility(0);
                }
                if (DialogsActivity.this.searchString == null) {
                    return true;
                }
                DialogsActivity.this.lambda$createView$1$PhotoAlbumPickerActivity();
                return false;
            }

            public void onSearchCollapse() {
                DialogsActivity.this.searching = false;
                DialogsActivity.this.searchWas = false;
                if (DialogsActivity.this.listView != null) {
                    DialogsActivity.this.listView.setEmptyView(DialogsActivity.this.progressView);
                    DialogsActivity.this.searchEmptyView.setVisibility(8);
                    if (!DialogsActivity.this.onlySelect) {
                        DialogsActivity.this.floatingButtonContainer.setVisibility(0);
                        DialogsActivity.this.floatingHidden = true;
                        DialogsActivity.this.floatingButtonContainer.setTranslationY((float) AndroidUtilities.dp(100.0f));
                        DialogsActivity.this.hideFloatingButton(false);
                    }
                    if (DialogsActivity.this.listView.getAdapter() != DialogsActivity.this.dialogsAdapter) {
                        DialogsActivity.this.listView.setAdapter(DialogsActivity.this.dialogsAdapter);
                        DialogsActivity.this.dialogsAdapter.notifyDataSetChanged();
                    }
                }
                if (DialogsActivity.this.dialogsSearchAdapter != null) {
                    DialogsActivity.this.dialogsSearchAdapter.searchDialogs(null);
                }
                DialogsActivity.this.updatePasscodeButton();
            }

            public void onTextChanged(EditText editText) {
                String text = editText.getText().toString();
                if (text.length() != 0 || (DialogsActivity.this.dialogsSearchAdapter != null && DialogsActivity.this.dialogsSearchAdapter.hasRecentRearch())) {
                    DialogsActivity.this.searchWas = true;
                    if (!(DialogsActivity.this.dialogsSearchAdapter == null || DialogsActivity.this.listView.getAdapter() == DialogsActivity.this.dialogsSearchAdapter)) {
                        DialogsActivity.this.listView.setAdapter(DialogsActivity.this.dialogsSearchAdapter);
                        DialogsActivity.this.dialogsSearchAdapter.notifyDataSetChanged();
                    }
                    if (!(DialogsActivity.this.searchEmptyView == null || DialogsActivity.this.listView.getEmptyView() == DialogsActivity.this.searchEmptyView)) {
                        DialogsActivity.this.progressView.setVisibility(8);
                        DialogsActivity.this.searchEmptyView.showTextView();
                        DialogsActivity.this.listView.setEmptyView(DialogsActivity.this.searchEmptyView);
                    }
                }
                if (DialogsActivity.this.dialogsSearchAdapter != null) {
                    DialogsActivity.this.dialogsSearchAdapter.searchDialogs(text);
                }
            }
        }).setSearchFieldHint(LocaleController.getString("Search", R.string.Search));
        if (this.onlySelect) {
            this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
            if (this.dialogsType == 3 && this.selectAlertString == null) {
                this.actionBar.setTitle(LocaleController.getString("ForwardTo", R.string.ForwardTo));
            } else {
                this.actionBar.setTitle(LocaleController.getString("SelectChat", R.string.SelectChat));
            }
        } else {
            if (this.searchString != null) {
                this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
            } else {
                this.actionBar.setBackButtonDrawable(new MenuDrawable());
            }
            if (BuildVars.DEBUG_VERSION) {
                this.actionBar.setTitle("Telegram Beta");
            } else {
                this.actionBar.setTitle(LocaleController.getString("AppName", R.string.AppName));
            }
            this.actionBar.setSupportsHolidayImage(true);
        }
        this.actionBar.setTitleActionRunnable(new DialogsActivity$$Lambda$1(this));
        if (this.allowSwitchAccount && UserConfig.getActivatedAccountsCount() > 1) {
            TLObject avatar;
            this.switchItem = menu.addItemWithWidth(1, 0, AndroidUtilities.dp(56.0f));
            Drawable avatarDrawable = new AvatarDrawable();
            avatarDrawable.setTextSize(AndroidUtilities.dp(12.0f));
            backupImageView = new BackupImageView(context);
            backupImageView.setRoundRadius(AndroidUtilities.dp(18.0f));
            this.switchItem.addView(backupImageView, LayoutHelper.createFrame(36, 36, 17));
            User user = UserConfig.getInstance(this.currentAccount).getCurrentUser();
            avatarDrawable.setInfo(user);
            if (user.photo == null || user.photo.photo_small == null || user.photo.photo_small.volume_id == 0 || user.photo.photo_small.local_id == 0) {
                avatar = null;
            } else {
                avatar = user.photo.photo_small;
            }
            backupImageView.getImageReceiver().setCurrentAccount(this.currentAccount);
            backupImageView.setImage(avatar, "50_50", avatarDrawable, (Object) user);
            for (int a = 0; a < 3; a++) {
                if (UserConfig.getInstance(a).getCurrentUser() != null) {
                    AccountSelectCell cell = new AccountSelectCell(context);
                    cell.setAccount(a, true);
                    this.switchItem.addSubItem(a + 10, cell, AndroidUtilities.dp(230.0f), AndroidUtilities.dp(48.0f));
                }
            }
        }
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                boolean z = true;
                if (id == -1) {
                    if (DialogsActivity.this.onlySelect) {
                        DialogsActivity.this.lambda$createView$1$PhotoAlbumPickerActivity();
                    } else if (DialogsActivity.this.parentLayout != null) {
                        DialogsActivity.this.parentLayout.getDrawerLayoutContainer().openDrawer(false);
                    }
                } else if (id == 1) {
                    if (SharedConfig.appLocked) {
                        z = false;
                    }
                    SharedConfig.appLocked = z;
                    SharedConfig.saveConfig();
                    DialogsActivity.this.updatePasscodeButton();
                } else if (id == 2) {
                    DialogsActivity.this.presentFragment(new ProxyListActivity());
                } else if (id >= 10 && id < 13 && DialogsActivity.this.getParentActivity() != null) {
                    DialogsActivityDelegate oldDelegate = DialogsActivity.this.delegate;
                    LaunchActivity launchActivity = (LaunchActivity) DialogsActivity.this.getParentActivity();
                    launchActivity.switchToAccount(id - 10, true);
                    DialogsActivity dialogsActivity = new DialogsActivity(DialogsActivity.this.arguments);
                    dialogsActivity.setDelegate(oldDelegate);
                    launchActivity.presentFragment(dialogsActivity, false, true);
                }
            }
        });
        if (this.sideMenu != null) {
            this.sideMenu.setBackgroundColor(Theme.getColor("chats_menuBackground"));
            this.sideMenu.setGlowColor(Theme.getColor("chats_menuBackground"));
            this.sideMenu.getAdapter().notifyDataSetChanged();
        }
        backupImageView = new SizeNotifierFrameLayout(context) {
            int inputFieldHeight = 0;

            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int widthSize = MeasureSpec.getSize(widthMeasureSpec);
                int heightSize = MeasureSpec.getSize(heightMeasureSpec);
                setMeasuredDimension(widthSize, heightSize);
                heightSize -= getPaddingTop();
                measureChildWithMargins(DialogsActivity.this.actionBar, widthMeasureSpec, 0, heightMeasureSpec, 0);
                int keyboardSize = getKeyboardHeight();
                int childCount = getChildCount();
                if (DialogsActivity.this.commentView != null) {
                    measureChildWithMargins(DialogsActivity.this.commentView, widthMeasureSpec, 0, heightMeasureSpec, 0);
                    Object tag = DialogsActivity.this.commentView.getTag();
                    if (tag == null || !tag.equals(Integer.valueOf(2))) {
                        this.inputFieldHeight = 0;
                    } else {
                        if (keyboardSize <= AndroidUtilities.dp(20.0f) && !AndroidUtilities.isInMultiwindow) {
                            heightSize -= DialogsActivity.this.commentView.getEmojiPadding();
                        }
                        this.inputFieldHeight = DialogsActivity.this.commentView.getMeasuredHeight();
                    }
                }
                for (int i = 0; i < childCount; i++) {
                    View child = getChildAt(i);
                    if (!(child == null || child.getVisibility() == 8 || child == DialogsActivity.this.commentView || child == DialogsActivity.this.actionBar)) {
                        if (child == DialogsActivity.this.listView || child == DialogsActivity.this.progressView || child == DialogsActivity.this.searchEmptyView) {
                            child.measure(MeasureSpec.makeMeasureSpec(widthSize, NUM), MeasureSpec.makeMeasureSpec(Math.max(AndroidUtilities.dp(10.0f), (heightSize - this.inputFieldHeight) + AndroidUtilities.dp(2.0f)), NUM));
                        } else if (DialogsActivity.this.commentView == null || !DialogsActivity.this.commentView.isPopupView(child)) {
                            measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);
                        } else if (!AndroidUtilities.isInMultiwindow) {
                            child.measure(MeasureSpec.makeMeasureSpec(widthSize, NUM), MeasureSpec.makeMeasureSpec(child.getLayoutParams().height, NUM));
                        } else if (AndroidUtilities.isTablet()) {
                            child.measure(MeasureSpec.makeMeasureSpec(widthSize, NUM), MeasureSpec.makeMeasureSpec(Math.min(AndroidUtilities.dp(320.0f), ((heightSize - this.inputFieldHeight) - AndroidUtilities.statusBarHeight) + getPaddingTop()), NUM));
                        } else {
                            child.measure(MeasureSpec.makeMeasureSpec(widthSize, NUM), MeasureSpec.makeMeasureSpec(((heightSize - this.inputFieldHeight) - AndroidUtilities.statusBarHeight) + getPaddingTop(), NUM));
                        }
                    }
                }
            }

            protected void onLayout(boolean changed, int l, int t, int r, int b) {
                int count = getChildCount();
                Object tag = DialogsActivity.this.commentView != null ? DialogsActivity.this.commentView.getTag() : null;
                int paddingBottom = (tag == null || !tag.equals(Integer.valueOf(2))) ? 0 : (getKeyboardHeight() > AndroidUtilities.dp(20.0f) || AndroidUtilities.isInMultiwindow) ? 0 : DialogsActivity.this.commentView.getEmojiPadding();
                setBottomClip(paddingBottom);
                for (int i = 0; i < count; i++) {
                    View child = getChildAt(i);
                    if (child.getVisibility() != 8) {
                        int childLeft;
                        int childTop;
                        LayoutParams lp = (LayoutParams) child.getLayoutParams();
                        int width = child.getMeasuredWidth();
                        int height = child.getMeasuredHeight();
                        int gravity = lp.gravity;
                        if (gravity == -1) {
                            gravity = 51;
                        }
                        int verticalGravity = gravity & 112;
                        switch ((gravity & 7) & 7) {
                            case 1:
                                childLeft = ((((r - l) - width) / 2) + lp.leftMargin) - lp.rightMargin;
                                break;
                            case 5:
                                childLeft = (r - width) - lp.rightMargin;
                                break;
                            default:
                                childLeft = lp.leftMargin;
                                break;
                        }
                        switch (verticalGravity) {
                            case 16:
                                childTop = (((((b - paddingBottom) - t) - height) / 2) + lp.topMargin) - lp.bottomMargin;
                                break;
                            case 48:
                                childTop = lp.topMargin + getPaddingTop();
                                break;
                            case 80:
                                childTop = (((b - paddingBottom) - t) - height) - lp.bottomMargin;
                                break;
                            default:
                                childTop = lp.topMargin;
                                break;
                        }
                        if (DialogsActivity.this.commentView != null && DialogsActivity.this.commentView.isPopupView(child)) {
                            if (AndroidUtilities.isInMultiwindow) {
                                childTop = (DialogsActivity.this.commentView.getTop() - child.getMeasuredHeight()) + AndroidUtilities.dp(1.0f);
                            } else {
                                childTop = DialogsActivity.this.commentView.getBottom();
                            }
                        }
                        child.layout(childLeft, childTop, childLeft + width, childTop + height);
                    }
                }
                notifyHeightChanged();
            }
        };
        this.fragmentView = backupImageView;
        this.listView = new RecyclerListView(context);
        this.listView.setVerticalScrollBarEnabled(true);
        this.listView.setItemAnimator(null);
        this.listView.setInstantClick(true);
        this.listView.setLayoutAnimation(null);
        this.listView.setTag(Integer.valueOf(4));
        this.layoutManager = new LinearLayoutManager(context) {
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }

            public void smoothScrollToPosition(RecyclerView recyclerView, State state, int position) {
                LinearSmoothScrollerMiddle linearSmoothScroller = new LinearSmoothScrollerMiddle(recyclerView.getContext());
                linearSmoothScroller.setTargetPosition(position);
                startSmoothScroll(linearSmoothScroller);
            }
        };
        this.layoutManager.setOrientation(1);
        this.listView.setLayoutManager(this.layoutManager);
        this.listView.setVerticalScrollbarPosition(LocaleController.isRTL ? 1 : 2);
        backupImageView.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setOnItemClickListener(new DialogsActivity$$Lambda$2(this));
        this.listView.setOnItemLongClickListener(new OnItemLongClickListenerExtended() {
            public boolean onItemClick(View view, int position, float x, float y) {
                if (DialogsActivity.this.getParentActivity() == null) {
                    return false;
                }
                int high_id;
                Chat chat;
                if (!(AndroidUtilities.isTablet() || DialogsActivity.this.onlySelect || !(view instanceof DialogCell))) {
                    DialogCell cell = (DialogCell) view;
                    if (cell.isPointInsideAvatar(x, y)) {
                        long dialog_id = cell.getDialogId();
                        Bundle args = new Bundle();
                        int lower_part = (int) dialog_id;
                        high_id = (int) (dialog_id >> 32);
                        int message_id = cell.getMessageId();
                        if (lower_part == 0) {
                            return false;
                        }
                        if (high_id == 1) {
                            args.putInt("chat_id", lower_part);
                        } else if (lower_part > 0) {
                            args.putInt("user_id", lower_part);
                        } else if (lower_part < 0) {
                            if (message_id != 0) {
                                chat = MessagesController.getInstance(DialogsActivity.this.currentAccount).getChat(Integer.valueOf(-lower_part));
                                if (!(chat == null || chat.migrated_to == null)) {
                                    args.putInt("migrated_to", lower_part);
                                    lower_part = -chat.migrated_to.channel_id;
                                }
                            }
                            args.putInt("chat_id", -lower_part);
                        }
                        if (message_id != 0) {
                            args.putInt("message_id", message_id);
                        }
                        if (DialogsActivity.this.searchString != null) {
                            if (MessagesController.getInstance(DialogsActivity.this.currentAccount).checkCanOpenChat(args, DialogsActivity.this)) {
                                NotificationCenter.getInstance(DialogsActivity.this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                                DialogsActivity.this.presentFragmentAsPreview(new ChatActivity(args));
                            }
                        } else {
                            if (MessagesController.getInstance(DialogsActivity.this.currentAccount).checkCanOpenChat(args, DialogsActivity.this)) {
                                DialogsActivity.this.presentFragmentAsPreview(new ChatActivity(args));
                            }
                        }
                        return true;
                    }
                }
                if (DialogsActivity.this.listView.getAdapter() != DialogsActivity.this.dialogsSearchAdapter) {
                    ArrayList<TL_dialog> dialogs = DialogsActivity.this.getDialogsArray();
                    if (position < 0 || position >= dialogs.size()) {
                        return false;
                    }
                    TL_dialog dialog = (TL_dialog) dialogs.get(position);
                    if (!DialogsActivity.this.onlySelect) {
                        long selectedDialog = dialog.id;
                        boolean pinned = dialog.pinned;
                        Builder builder = new Builder(DialogsActivity.this.getParentActivity());
                        int lower_id = (int) selectedDialog;
                        high_id = (int) (selectedDialog >> 32);
                        boolean hasUnread = dialog.unread_count != 0 || dialog.unread_mark;
                        String string;
                        Dialog sheet;
                        if (DialogObject.isChannel(dialog)) {
                            CharSequence[] items;
                            chat = MessagesController.getInstance(DialogsActivity.this.currentAccount).getChat(Integer.valueOf(-lower_id));
                            int[] icons = new int[4];
                            icons[0] = dialog.pinned ? R.drawable.chats_unpin : R.drawable.chats_pin;
                            icons[1] = hasUnread ? R.drawable.menu_read : R.drawable.menu_unread;
                            icons[2] = R.drawable.chats_clear;
                            icons[3] = R.drawable.chats_leave;
                            if (MessagesController.getInstance(DialogsActivity.this.currentAccount).isProxyDialog(dialog.id)) {
                                items = new CharSequence[4];
                                items[0] = null;
                                items[1] = hasUnread ? LocaleController.getString("MarkAsRead", R.string.MarkAsRead) : LocaleController.getString("MarkAsUnread", R.string.MarkAsUnread);
                                items[2] = LocaleController.getString("ClearHistoryCache", R.string.ClearHistoryCache);
                                items[3] = null;
                            } else if (chat == null || !chat.megagroup) {
                                items = new CharSequence[4];
                                items[0] = dialog.pinned ? LocaleController.getString("UnpinFromTop", R.string.UnpinFromTop) : LocaleController.getString("PinToTop", R.string.PinToTop);
                                if (hasUnread) {
                                    string = LocaleController.getString("MarkAsRead", R.string.MarkAsRead);
                                } else {
                                    string = LocaleController.getString("MarkAsUnread", R.string.MarkAsUnread);
                                }
                                items[1] = string;
                                items[2] = LocaleController.getString("ClearHistoryCache", R.string.ClearHistoryCache);
                                items[3] = LocaleController.getString("LeaveChannelMenu", R.string.LeaveChannelMenu);
                            } else {
                                items = new CharSequence[4];
                                items[0] = dialog.pinned ? LocaleController.getString("UnpinFromTop", R.string.UnpinFromTop) : LocaleController.getString("PinToTop", R.string.PinToTop);
                                items[1] = hasUnread ? LocaleController.getString("MarkAsRead", R.string.MarkAsRead) : LocaleController.getString("MarkAsUnread", R.string.MarkAsUnread);
                                items[2] = TextUtils.isEmpty(chat.username) ? LocaleController.getString("ClearHistory", R.string.ClearHistory) : LocaleController.getString("ClearHistoryCache", R.string.ClearHistoryCache);
                                items[3] = LocaleController.getString("LeaveMegaMenu", R.string.LeaveMegaMenu);
                            }
                            builder.setItems(items, icons, new DialogsActivity$5$$Lambda$1(this, dialog, selectedDialog, pinned, hasUnread, chat, lower_id));
                            sheet = builder.create();
                            DialogsActivity.this.showDialog(sheet);
                            sheet.setItemColor(3, Theme.getColor("dialogTextRed2"), Theme.getColor("dialogRedIcon"));
                        } else {
                            User user;
                            boolean isChat = lower_id < 0 && high_id != 1;
                            chat = isChat ? MessagesController.getInstance(DialogsActivity.this.currentAccount).getChat(Integer.valueOf(-lower_id)) : null;
                            if (lower_id == 0) {
                                EncryptedChat encryptedChat = MessagesController.getInstance(DialogsActivity.this.currentAccount).getEncryptedChat(Integer.valueOf(high_id));
                                user = encryptedChat != null ? MessagesController.getInstance(DialogsActivity.this.currentAccount).getUser(Integer.valueOf(encryptedChat.user_id)) : new TL_userEmpty();
                            } else {
                                user = (isChat || lower_id <= 0 || high_id == 1) ? null : MessagesController.getInstance(DialogsActivity.this.currentAccount).getUser(Integer.valueOf(lower_id));
                            }
                            boolean isBot = user != null && user.bot;
                            CharSequence[] charSequenceArr = new CharSequence[4];
                            charSequenceArr[0] = dialog.pinned ? LocaleController.getString("UnpinFromTop", R.string.UnpinFromTop) : LocaleController.getString("PinToTop", R.string.PinToTop);
                            charSequenceArr[1] = hasUnread ? LocaleController.getString("MarkAsRead", R.string.MarkAsRead) : LocaleController.getString("MarkAsUnread", R.string.MarkAsUnread);
                            charSequenceArr[2] = LocaleController.getString("ClearHistory", R.string.ClearHistory);
                            string = isChat ? LocaleController.getString("DeleteChat", R.string.DeleteChat) : isBot ? LocaleController.getString("DeleteAndStop", R.string.DeleteAndStop) : LocaleController.getString("Delete", R.string.Delete);
                            charSequenceArr[3] = string;
                            int[] iArr = new int[4];
                            iArr[0] = dialog.pinned ? R.drawable.chats_unpin : R.drawable.chats_pin;
                            iArr[1] = hasUnread ? R.drawable.menu_read : R.drawable.menu_unread;
                            iArr[2] = R.drawable.chats_clear;
                            iArr[3] = isChat ? R.drawable.chats_leave : R.drawable.chats_delete;
                            builder.setItems(charSequenceArr, iArr, new DialogsActivity$5$$Lambda$2(this, dialog, lower_id, selectedDialog, pinned, hasUnread, chat, user, isChat, isBot));
                            sheet = builder.create();
                            DialogsActivity.this.showDialog(sheet);
                            sheet.setItemColor(3, Theme.getColor("dialogTextRed2"), Theme.getColor("dialogRedIcon"));
                        }
                    } else if (DialogsActivity.this.dialogsType != 3 || DialogsActivity.this.selectAlertString != null) {
                        return false;
                    } else {
                        DialogsActivity.this.dialogsAdapter.addOrRemoveSelectedDialog(dialog.id, view);
                        DialogsActivity.this.updateSelectedCount();
                    }
                    return true;
                } else if (!(DialogsActivity.this.dialogsSearchAdapter.getItem(position) instanceof String) && !DialogsActivity.this.dialogsSearchAdapter.isRecentSearchDisplayed()) {
                    return false;
                } else {
                    AlertDialog.Builder builder2 = new AlertDialog.Builder(DialogsActivity.this.getParentActivity());
                    builder2.setTitle(LocaleController.getString("AppName", R.string.AppName));
                    builder2.setMessage(LocaleController.getString("ClearSearch", R.string.ClearSearch));
                    builder2.setPositiveButton(LocaleController.getString("ClearButton", R.string.ClearButton).toUpperCase(), new DialogsActivity$5$$Lambda$0(this));
                    builder2.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                    DialogsActivity.this.showDialog(builder2.create());
                    return true;
                }
            }

            final /* synthetic */ void lambda$onItemClick$0$DialogsActivity$5(DialogInterface dialogInterface, int i) {
                if (DialogsActivity.this.dialogsSearchAdapter.isRecentSearchDisplayed()) {
                    DialogsActivity.this.dialogsSearchAdapter.clearRecentSearch();
                } else {
                    DialogsActivity.this.dialogsSearchAdapter.clearRecentHashtags();
                }
            }

            final /* synthetic */ void lambda$onItemClick$3$DialogsActivity$5(TL_dialog dialog, long selectedDialog, boolean pinned, boolean hasUnread, Chat chat, int lower_id, DialogInterface d, int which) {
                if (which == 0) {
                    if (dialog.pinned || MessagesController.getInstance(DialogsActivity.this.currentAccount).canPinDialog(false)) {
                        if (MessagesController.getInstance(DialogsActivity.this.currentAccount).pinDialog(selectedDialog, !pinned, null, 0) && !pinned) {
                            DialogsActivity.this.hideFloatingButton(false);
                            DialogsActivity.this.listView.smoothScrollToPosition(0);
                            return;
                        }
                        return;
                    }
                    AlertsCreator.showSimpleAlert(DialogsActivity.this, LocaleController.formatString("PinToTopLimitReached", R.string.PinToTopLimitReached, LocaleController.formatPluralString("Chats", MessagesController.getInstance(DialogsActivity.this.currentAccount).maxPinnedDialogsCount)));
                } else if (which != 1) {
                    AlertsCreator.createClearOrDeleteDialogAlert(DialogsActivity.this, which == 2, chat, null, lower_id == 0, new DialogsActivity$5$$Lambda$5(this, which, chat, selectedDialog));
                } else if (hasUnread) {
                    MessagesController.getInstance(DialogsActivity.this.currentAccount).markMentionsAsRead(selectedDialog);
                    MessagesController.getInstance(DialogsActivity.this.currentAccount).markDialogAsRead(selectedDialog, dialog.top_message, dialog.top_message, dialog.last_message_date, false, 0, true);
                } else {
                    MessagesController.getInstance(DialogsActivity.this.currentAccount).markDialogAsUnread(selectedDialog, null, 0);
                }
            }

            final /* synthetic */ void lambda$null$2$DialogsActivity$5(int which, Chat chat, long selectedDialog) {
                if (which != 2 || (chat.megagroup && TextUtils.isEmpty(chat.username))) {
                    DialogsActivity.this.undoView.showWithAction(selectedDialog, which == 2, new DialogsActivity$5$$Lambda$6(this, which, selectedDialog));
                } else {
                    MessagesController.getInstance(DialogsActivity.this.currentAccount).deleteDialog(selectedDialog, 2);
                }
            }

            final /* synthetic */ void lambda$null$1$DialogsActivity$5(int which, long selectedDialog) {
                if (which == 2) {
                    MessagesController.getInstance(DialogsActivity.this.currentAccount).deleteDialog(selectedDialog, 1);
                    return;
                }
                MessagesController.getInstance(DialogsActivity.this.currentAccount).deleteUserFromChat((int) (-selectedDialog), UserConfig.getInstance(DialogsActivity.this.currentAccount).getCurrentUser(), null);
                if (AndroidUtilities.isTablet()) {
                    NotificationCenter.getInstance(DialogsActivity.this.currentAccount).postNotificationName(NotificationCenter.closeChats, Long.valueOf(selectedDialog));
                }
            }

            final /* synthetic */ void lambda$onItemClick$6$DialogsActivity$5(TL_dialog dialog, int lower_id, long selectedDialog, boolean pinned, boolean hasUnread, Chat chat, User user, boolean isChat, boolean isBot, DialogInterface d, int which) {
                if (which == 0) {
                    if (!dialog.pinned) {
                        if (!MessagesController.getInstance(DialogsActivity.this.currentAccount).canPinDialog(lower_id == 0)) {
                            AlertsCreator.showSimpleAlert(DialogsActivity.this, LocaleController.formatString("PinToTopLimitReached", R.string.PinToTopLimitReached, LocaleController.formatPluralString("Chats", MessagesController.getInstance(DialogsActivity.this.currentAccount).maxPinnedDialogsCount)));
                            return;
                        }
                    }
                    if (MessagesController.getInstance(DialogsActivity.this.currentAccount).pinDialog(selectedDialog, !pinned, null, 0) && !pinned) {
                        DialogsActivity.this.hideFloatingButton(false);
                        DialogsActivity.this.listView.smoothScrollToPosition(0);
                    }
                } else if (which != 1) {
                    AlertsCreator.createClearOrDeleteDialogAlert(DialogsActivity.this, which == 2, chat, user, lower_id == 0, new DialogsActivity$5$$Lambda$3(this, selectedDialog, which, isChat, isBot));
                } else if (hasUnread) {
                    MessagesController.getInstance(DialogsActivity.this.currentAccount).markMentionsAsRead(selectedDialog);
                    MessagesController.getInstance(DialogsActivity.this.currentAccount).markDialogAsRead(selectedDialog, dialog.top_message, dialog.top_message, dialog.last_message_date, false, 0, true);
                } else {
                    MessagesController.getInstance(DialogsActivity.this.currentAccount).markDialogAsUnread(selectedDialog, null, 0);
                }
            }

            final /* synthetic */ void lambda$null$5$DialogsActivity$5(long selectedDialog, int which, boolean isChat, boolean isBot) {
                DialogsActivity.this.undoView.showWithAction(selectedDialog, which == 2, new DialogsActivity$5$$Lambda$4(this, which, isChat, selectedDialog, isBot));
            }

            final /* synthetic */ void lambda$null$4$DialogsActivity$5(int which, boolean isChat, long selectedDialog, boolean isBot) {
                if (which != 2) {
                    if (isChat) {
                        Chat currentChat = MessagesController.getInstance(DialogsActivity.this.currentAccount).getChat(Integer.valueOf((int) (-selectedDialog)));
                        if (currentChat == null || !ChatObject.isNotInChat(currentChat)) {
                            MessagesController.getInstance(DialogsActivity.this.currentAccount).deleteUserFromChat((int) (-selectedDialog), MessagesController.getInstance(DialogsActivity.this.currentAccount).getUser(Integer.valueOf(UserConfig.getInstance(DialogsActivity.this.currentAccount).getClientUserId())), null);
                        } else {
                            MessagesController.getInstance(DialogsActivity.this.currentAccount).deleteDialog(selectedDialog, 0);
                        }
                    } else {
                        MessagesController.getInstance(DialogsActivity.this.currentAccount).deleteDialog(selectedDialog, 0);
                    }
                    if (isBot) {
                        MessagesController.getInstance(DialogsActivity.this.currentAccount).blockUser((int) selectedDialog);
                    }
                    if (AndroidUtilities.isTablet()) {
                        NotificationCenter.getInstance(DialogsActivity.this.currentAccount).postNotificationName(NotificationCenter.closeChats, Long.valueOf(selectedDialog));
                        return;
                    }
                    return;
                }
                MessagesController.getInstance(DialogsActivity.this.currentAccount).deleteDialog(selectedDialog, 1);
            }

            public void onLongClickRelease() {
                DialogsActivity.this.finishPreviewFragment();
            }

            public void onMove(float dx, float dy) {
                DialogsActivity.this.movePreviewFragment(dy);
            }
        });
        this.searchEmptyView = new EmptyTextProgressView(context);
        this.searchEmptyView.setVisibility(8);
        this.searchEmptyView.setShowAtCenter(true);
        this.searchEmptyView.setText(LocaleController.getString("NoResult", R.string.NoResult));
        backupImageView.addView(this.searchEmptyView, LayoutHelper.createFrame(-1, -1.0f));
        this.progressView = new RadialProgressView(context);
        this.progressView.setVisibility(8);
        backupImageView.addView(this.progressView, LayoutHelper.createFrame(-2, -2, 17));
        this.floatingButtonContainer = new FrameLayout(context);
        this.floatingButtonContainer.setVisibility(this.onlySelect ? 8 : 0);
        View view = this.floatingButtonContainer;
        int i3 = (VERSION.SDK_INT >= 21 ? 56 : 60) + 20;
        if (VERSION.SDK_INT >= 21) {
            i = 56;
        } else {
            i = 60;
        }
        float f3 = (float) (i + 14);
        if (LocaleController.isRTL) {
            i2 = 3;
        } else {
            i2 = 5;
        }
        i2 |= 80;
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
        backupImageView.addView(view, LayoutHelper.createFrame(i3, f3, i2, f, 0.0f, f2, 0.0f));
        this.floatingButtonContainer.setOnClickListener(new DialogsActivity$$Lambda$3(this));
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
        this.floatingButton.setImageResource(R.drawable.floating_pencil);
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
        FrameLayout frameLayout = this.floatingButtonContainer;
        View view2 = this.floatingButton;
        if (VERSION.SDK_INT >= 21) {
            i3 = 56;
        } else {
            i3 = 60;
        }
        if (VERSION.SDK_INT >= 21) {
            i = 56;
        } else {
            i = 60;
        }
        frameLayout.addView(view2, LayoutHelper.createFrame(i3, (float) i, 51, 10.0f, 0.0f, 10.0f, 0.0f));
        this.listView.setOnScrollListener(new OnScrollListener() {
            private boolean scrollingManually;

            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == 1) {
                    if (DialogsActivity.this.searching && DialogsActivity.this.searchWas) {
                        AndroidUtilities.hideKeyboard(DialogsActivity.this.getParentActivity().getCurrentFocus());
                    }
                    this.scrollingManually = true;
                    return;
                }
                this.scrollingManually = false;
            }

            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int firstVisibleItem = DialogsActivity.this.layoutManager.findFirstVisibleItemPosition();
                int visibleItemCount = Math.abs(DialogsActivity.this.layoutManager.findLastVisibleItemPosition() - firstVisibleItem) + 1;
                int totalItemCount = recyclerView.getAdapter().getItemCount();
                if (!DialogsActivity.this.searching || !DialogsActivity.this.searchWas) {
                    if (visibleItemCount > 0 && DialogsActivity.this.layoutManager.findLastVisibleItemPosition() >= DialogsActivity.this.getDialogsArray().size() - 10) {
                        boolean fromCache = !MessagesController.getInstance(DialogsActivity.this.currentAccount).dialogsEndReached;
                        if (fromCache || !MessagesController.getInstance(DialogsActivity.this.currentAccount).serverDialogsEndReached) {
                            MessagesController.getInstance(DialogsActivity.this.currentAccount).loadDialogs(-1, 100, fromCache);
                        }
                    }
                    if (DialogsActivity.this.floatingButtonContainer.getVisibility() != 8) {
                        boolean goingDown;
                        View topChild = recyclerView.getChildAt(0);
                        int firstViewTop = 0;
                        if (topChild != null) {
                            firstViewTop = topChild.getTop();
                        }
                        boolean changed = true;
                        if (DialogsActivity.this.prevPosition == firstVisibleItem) {
                            int topDelta = DialogsActivity.this.prevTop - firstViewTop;
                            goingDown = firstViewTop < DialogsActivity.this.prevTop;
                            changed = Math.abs(topDelta) > 1;
                        } else {
                            goingDown = firstVisibleItem > DialogsActivity.this.prevPosition;
                        }
                        if (changed && DialogsActivity.this.scrollUpdated && (goingDown || (!goingDown && this.scrollingManually))) {
                            DialogsActivity.this.hideFloatingButton(goingDown);
                        }
                        DialogsActivity.this.prevPosition = firstVisibleItem;
                        DialogsActivity.this.prevTop = firstViewTop;
                        DialogsActivity.this.scrollUpdated = true;
                    }
                } else if (visibleItemCount > 0 && DialogsActivity.this.layoutManager.findLastVisibleItemPosition() == totalItemCount - 1 && !DialogsActivity.this.dialogsSearchAdapter.isMessagesSearchEndReached()) {
                    DialogsActivity.this.dialogsSearchAdapter.loadMoreSearchMessages();
                }
            }
        });
        if (this.searchString == null) {
            this.dialogsAdapter = new DialogsAdapter(context, this.dialogsType, this.onlySelect);
            if (AndroidUtilities.isTablet() && this.openedDialogId != 0) {
                this.dialogsAdapter.setOpenedDialogId(this.openedDialogId);
            }
            this.listView.setAdapter(this.dialogsAdapter);
        }
        int type = 0;
        if (this.searchString != null) {
            type = 2;
        } else if (!this.onlySelect) {
            type = 1;
        }
        this.dialogsSearchAdapter = new DialogsSearchAdapter(context, type, this.dialogsType);
        this.dialogsSearchAdapter.setDelegate(new DialogsSearchAdapterDelegate() {
            public void searchStateChanged(boolean search) {
                if (!DialogsActivity.this.searching || !DialogsActivity.this.searchWas || DialogsActivity.this.searchEmptyView == null) {
                    return;
                }
                if (search) {
                    DialogsActivity.this.searchEmptyView.showProgress();
                } else {
                    DialogsActivity.this.searchEmptyView.showTextView();
                }
            }

            public void didPressedOnSubDialog(long did) {
                if (!DialogsActivity.this.onlySelect) {
                    int lower_id = (int) did;
                    Bundle args = new Bundle();
                    if (lower_id > 0) {
                        args.putInt("user_id", lower_id);
                    } else {
                        args.putInt("chat_id", -lower_id);
                    }
                    DialogsActivity.this.closeSearch();
                    if (AndroidUtilities.isTablet() && DialogsActivity.this.dialogsAdapter != null) {
                        DialogsActivity.this.dialogsAdapter.setOpenedDialogId(DialogsActivity.this.openedDialogId = did);
                        DialogsActivity.this.updateVisibleRows(512);
                    }
                    if (DialogsActivity.this.searchString != null) {
                        if (MessagesController.getInstance(DialogsActivity.this.currentAccount).checkCanOpenChat(args, DialogsActivity.this)) {
                            NotificationCenter.getInstance(DialogsActivity.this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                            DialogsActivity.this.presentFragment(new ChatActivity(args));
                        }
                    } else if (MessagesController.getInstance(DialogsActivity.this.currentAccount).checkCanOpenChat(args, DialogsActivity.this)) {
                        DialogsActivity.this.presentFragment(new ChatActivity(args));
                    }
                } else if (DialogsActivity.this.dialogsAdapter.hasSelectedDialogs()) {
                    DialogsActivity.this.dialogsAdapter.addOrRemoveSelectedDialog(did, null);
                    DialogsActivity.this.updateSelectedCount();
                    DialogsActivity.this.closeSearch();
                } else {
                    DialogsActivity.this.didSelectResult(did, true, false);
                }
            }

            public void needRemoveHint(int did) {
                if (DialogsActivity.this.getParentActivity() != null && MessagesController.getInstance(DialogsActivity.this.currentAccount).getUser(Integer.valueOf(did)) != null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(DialogsActivity.this.getParentActivity());
                    builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                    builder.setMessage(LocaleController.formatString("ChatHintsDelete", R.string.ChatHintsDelete, ContactsController.formatName(user.first_name, user.last_name)));
                    builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogsActivity$8$$Lambda$0(this, did));
                    builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                    DialogsActivity.this.showDialog(builder.create());
                }
            }

            final /* synthetic */ void lambda$needRemoveHint$0$DialogsActivity$8(int did, DialogInterface dialogInterface, int i) {
                DataQuery.getInstance(DialogsActivity.this.currentAccount).removePeer(did);
            }
        });
        this.listView.setEmptyView(this.progressView);
        if (this.searchString != null) {
            this.actionBar.openSearchField(this.searchString, false);
        }
        if (!this.onlySelect && this.dialogsType == 0) {
            backupImageView = new FragmentContextView(context, this, true);
            backupImageView.addView(backupImageView, LayoutHelper.createFrame(-1, 39.0f, 51, 0.0f, -36.0f, 0.0f, 0.0f));
            backupImageView = new FragmentContextView(context, this, false);
            backupImageView.addView(backupImageView, LayoutHelper.createFrame(-1, 39.0f, 51, 0.0f, -36.0f, 0.0f, 0.0f));
            backupImageView.setAdditionalContextView(backupImageView);
            backupImageView.setAdditionalContextView(backupImageView);
        } else if (this.dialogsType == 3 && this.selectAlertString == null) {
            if (this.commentView != null) {
                this.commentView.onDestroy();
            }
            this.commentView = new ChatActivityEnterView(getParentActivity(), backupImageView, null, false);
            this.commentView.setAllowStickersAndGifs(false, false);
            this.commentView.setForceShowSendButton(true, false);
            this.commentView.setVisibility(8);
            backupImageView.addView(this.commentView, LayoutHelper.createFrame(-1, -2, 83));
            this.commentView.setDelegate(new ChatActivityEnterViewDelegate() {
                public void onMessageSend(CharSequence message) {
                    if (DialogsActivity.this.delegate != null) {
                        ArrayList<Long> selectedDialogs = DialogsActivity.this.dialogsAdapter.getSelectedDialogs();
                        if (!selectedDialogs.isEmpty()) {
                            DialogsActivity.this.delegate.didSelectDialogs(DialogsActivity.this, selectedDialogs, message, false);
                        }
                    }
                }

                public void onSwitchRecordMode(boolean video) {
                }

                public void onTextSelectionChanged(int start, int end) {
                }

                public void onStickersExpandedChange() {
                }

                public void onPreAudioVideoRecord() {
                }

                public void onTextChanged(CharSequence text, boolean bigChange) {
                }

                public void onTextSpansChanged(CharSequence text) {
                }

                public void needSendTyping() {
                }

                public void onAttachButtonHidden() {
                }

                public void onAttachButtonShow() {
                }

                public void onMessageEditEnd(boolean loading) {
                }

                public void onWindowSizeChanged(int size) {
                }

                public void onStickersTab(boolean opened) {
                }

                public void didPressedAttachButton() {
                }

                public void needStartRecordVideo(int state) {
                }

                public void needChangeVideoPreviewState(int state, float seekProgress) {
                }

                public void needStartRecordAudio(int state) {
                }

                public void needShowMediaBanHint() {
                }
            });
        }
        this.undoView = new UndoView(context) {
            public void setTranslationY(float translationY) {
                super.setTranslationY(translationY);
                float diff = ((float) getMeasuredHeight()) - translationY;
                if (!DialogsActivity.this.floatingHidden) {
                    DialogsActivity.this.floatingButtonContainer.setTranslationY((DialogsActivity.this.floatingButtonContainer.getTranslationY() + DialogsActivity.this.additionalFloatingTranslation) - diff);
                }
                DialogsActivity.this.additionalFloatingTranslation = diff;
            }
        };
        backupImageView.addView(this.undoView, LayoutHelper.createFrame(-1, -2, 83));
        return this.fragmentView;
    }

    final /* synthetic */ void lambda$createView$1$DialogsActivity() {
        hideFloatingButton(false);
        this.listView.smoothScrollToPosition(0);
    }

    final /* synthetic */ void lambda$createView$2$DialogsActivity(View view, int position) {
        if (this.listView != null && this.listView.getAdapter() != null && getParentActivity() != null) {
            long dialog_id = 0;
            int message_id = 0;
            boolean isGlobalSearch = false;
            Adapter adapter = this.listView.getAdapter();
            if (adapter == this.dialogsAdapter) {
                TLObject object = this.dialogsAdapter.getItem(position);
                if (object instanceof User) {
                    dialog_id = (long) ((User) object).id;
                } else if (object instanceof TL_dialog) {
                    dialog_id = ((TL_dialog) object).id;
                } else if (object instanceof TL_recentMeUrlChat) {
                    dialog_id = (long) (-((TL_recentMeUrlChat) object).chat_id);
                } else if (object instanceof TL_recentMeUrlUser) {
                    dialog_id = (long) ((TL_recentMeUrlUser) object).user_id;
                } else if (object instanceof TL_recentMeUrlChatInvite) {
                    TL_recentMeUrlChatInvite chatInvite = (TL_recentMeUrlChatInvite) object;
                    ChatInvite invite = chatInvite.chat_invite;
                    if ((invite.chat == null && (!invite.channel || invite.megagroup)) || (invite.chat != null && (!ChatObject.isChannel(invite.chat) || invite.chat.megagroup))) {
                        String hash = chatInvite.url;
                        int index = hash.indexOf(47);
                        if (index > 0) {
                            hash = hash.substring(index + 1);
                        }
                        showDialog(new JoinGroupAlert(getParentActivity(), invite, hash, this));
                        return;
                    } else if (invite.chat != null) {
                        dialog_id = (long) (-invite.chat.id);
                    } else {
                        return;
                    }
                } else if (object instanceof TL_recentMeUrlStickerSet) {
                    StickerSet stickerSet = ((TL_recentMeUrlStickerSet) object).set.set;
                    TL_inputStickerSetID set = new TL_inputStickerSetID();
                    set.id = stickerSet.id;
                    set.access_hash = stickerSet.access_hash;
                    showDialog(new StickersAlert(getParentActivity(), this, set, null, null));
                    return;
                } else {
                    if (object instanceof TL_recentMeUrlUnknown) {
                    }
                    return;
                }
            } else if (adapter == this.dialogsSearchAdapter) {
                MessageObject obj = this.dialogsSearchAdapter.getItem(position);
                isGlobalSearch = this.dialogsSearchAdapter.isGlobalSearch(position);
                if (obj instanceof User) {
                    dialog_id = (long) ((User) obj).id;
                    if (!this.onlySelect) {
                        this.searchDialogId = dialog_id;
                        this.searchObject = (User) obj;
                    }
                } else if (obj instanceof Chat) {
                    if (((Chat) obj).id > 0) {
                        dialog_id = (long) (-((Chat) obj).id);
                    } else {
                        dialog_id = AndroidUtilities.makeBroadcastId(((Chat) obj).id);
                    }
                    if (!this.onlySelect) {
                        this.searchDialogId = dialog_id;
                        this.searchObject = (Chat) obj;
                    }
                } else if (obj instanceof EncryptedChat) {
                    dialog_id = ((long) ((EncryptedChat) obj).id) << 32;
                    if (!this.onlySelect) {
                        this.searchDialogId = dialog_id;
                        this.searchObject = (EncryptedChat) obj;
                    }
                } else if (obj instanceof MessageObject) {
                    MessageObject messageObject = obj;
                    dialog_id = messageObject.getDialogId();
                    message_id = messageObject.getId();
                    this.dialogsSearchAdapter.addHashtagsFromMessage(this.dialogsSearchAdapter.getLastSearchString());
                } else if (obj instanceof String) {
                    this.actionBar.openSearchField((String) obj, false);
                }
            }
            if (dialog_id == 0) {
                return;
            }
            if (!this.onlySelect) {
                Bundle args = new Bundle();
                int lower_part = (int) dialog_id;
                int high_id = (int) (dialog_id >> 32);
                if (lower_part == 0) {
                    args.putInt("enc_id", high_id);
                } else if (high_id == 1) {
                    args.putInt("chat_id", lower_part);
                } else if (lower_part > 0) {
                    args.putInt("user_id", lower_part);
                } else if (lower_part < 0) {
                    if (message_id != 0) {
                        Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-lower_part));
                        if (!(chat == null || chat.migrated_to == null)) {
                            args.putInt("migrated_to", lower_part);
                            lower_part = -chat.migrated_to.channel_id;
                        }
                    }
                    args.putInt("chat_id", -lower_part);
                }
                if (message_id != 0) {
                    args.putInt("message_id", message_id);
                } else if (!isGlobalSearch) {
                    closeSearch();
                } else if (this.searchObject != null) {
                    this.dialogsSearchAdapter.putRecentSearch(this.searchDialogId, this.searchObject);
                    this.searchObject = null;
                }
                if (AndroidUtilities.isTablet()) {
                    if (this.openedDialogId == dialog_id && adapter != this.dialogsSearchAdapter) {
                        return;
                    }
                    if (this.dialogsAdapter != null) {
                        DialogsAdapter dialogsAdapter = this.dialogsAdapter;
                        this.openedDialogId = dialog_id;
                        dialogsAdapter.setOpenedDialogId(dialog_id);
                        updateVisibleRows(512);
                    }
                }
                if (this.searchString != null) {
                    if (MessagesController.getInstance(this.currentAccount).checkCanOpenChat(args, this)) {
                        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                        presentFragment(new ChatActivity(args));
                    }
                } else if (MessagesController.getInstance(this.currentAccount).checkCanOpenChat(args, this)) {
                    presentFragment(new ChatActivity(args));
                }
            } else if (this.dialogsAdapter.hasSelectedDialogs()) {
                this.dialogsAdapter.addOrRemoveSelectedDialog(dialog_id, view);
                updateSelectedCount();
            } else {
                didSelectResult(dialog_id, true, false);
            }
        }
    }

    final /* synthetic */ void lambda$createView$3$DialogsActivity(View v) {
        Bundle args = new Bundle();
        args.putBoolean("destroyAfterSelect", true);
        presentFragment(new ContactsActivity(args));
    }

    public void onResume() {
        super.onResume();
        if (this.dialogsAdapter != null) {
            this.dialogsAdapter.notifyDataSetChanged();
        }
        if (this.commentView != null) {
            this.commentView.onResume();
        }
        if (this.dialogsSearchAdapter != null) {
            this.dialogsSearchAdapter.notifyDataSetChanged();
        }
        if (this.checkPermission && !this.onlySelect && VERSION.SDK_INT >= 23) {
            Context activity = getParentActivity();
            if (activity != null) {
                this.checkPermission = false;
                if (activity.checkSelfPermission("android.permission.READ_CONTACTS") != 0 || activity.checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") != 0) {
                    Dialog create;
                    if (this.askAboutContacts && UserConfig.getInstance(this.currentAccount).syncContacts && activity.shouldShowRequestPermissionRationale("android.permission.READ_CONTACTS")) {
                        create = AlertsCreator.createContactsPermissionDialog(activity, new DialogsActivity$$Lambda$4(this)).create();
                        this.permissionDialog = create;
                        showDialog(create);
                    } else if (activity.shouldShowRequestPermissionRationale("android.permission.WRITE_EXTERNAL_STORAGE")) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                        builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                        builder.setMessage(LocaleController.getString("PermissionStorage", R.string.PermissionStorage));
                        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
                        create = builder.create();
                        this.permissionDialog = create;
                        showDialog(create);
                    } else {
                        askForPermissons(true);
                    }
                }
            }
        } else if (!this.onlySelect && XiaomiUtilities.isMIUI() && VERSION.SDK_INT >= 19 && !XiaomiUtilities.isCustomPermissionGranted(10020) && getParentActivity() != null && !MessagesController.getGlobalNotificationsSettings().getBoolean("askedAboutMiuiLockscreen", false)) {
            showDialog(new AlertDialog.Builder(getParentActivity()).setTitle(LocaleController.getString("AppName", R.string.AppName)).setMessage(LocaleController.getString("PermissionXiaomiLockscreen", R.string.PermissionXiaomiLockscreen)).setPositiveButton(LocaleController.getString("PermissionOpenSettings", R.string.PermissionOpenSettings), new DialogsActivity$$Lambda$5(this)).setNegativeButton(LocaleController.getString("ContactsPermissionAlertNotNow", R.string.ContactsPermissionAlertNotNow), DialogsActivity$$Lambda$6.$instance).create());
        }
    }

    final /* synthetic */ void lambda$onResume$4$DialogsActivity(int param) {
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

    final /* synthetic */ void lambda$onResume$5$DialogsActivity(DialogInterface dialog, int which) {
        Throwable xx;
        Intent intent = XiaomiUtilities.getPermissionManagerIntent();
        if (intent != null) {
            try {
                getParentActivity().startActivity(intent);
            } catch (Exception e) {
                try {
                    Intent intent2 = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
                    try {
                        intent2.setData(Uri.parse("package:" + ApplicationLoader.applicationContext.getPackageName()));
                        getParentActivity().startActivity(intent2);
                        intent = intent2;
                    } catch (Exception e2) {
                        xx = e2;
                        intent = intent2;
                        FileLog.e(xx);
                    }
                } catch (Exception e3) {
                    xx = e3;
                    FileLog.e(xx);
                }
            }
        }
    }

    public void onPause() {
        super.onPause();
        if (this.commentView != null) {
            this.commentView.onResume();
        }
        if (this.undoView != null) {
            this.undoView.hide(true, false);
        }
    }

    protected void onBecomeFullyHidden() {
        if (this.closeSearchFieldOnHide) {
            if (this.actionBar != null) {
                this.actionBar.closeSearchField();
            }
            if (this.searchObject != null) {
                this.dialogsSearchAdapter.putRecentSearch(this.searchDialogId, this.searchObject);
                this.searchObject = null;
            }
            this.closeSearchFieldOnHide = false;
        }
        if (this.undoView != null) {
            this.undoView.hide(true, false);
        }
    }

    private void closeSearch() {
        if (AndroidUtilities.isTablet()) {
            if (this.actionBar != null) {
                this.actionBar.closeSearchField();
            }
            if (this.searchObject != null) {
                this.dialogsSearchAdapter.putRecentSearch(this.searchDialogId, this.searchObject);
                this.searchObject = null;
                return;
            }
            return;
        }
        this.closeSearchFieldOnHide = true;
    }

    private void updateProxyButton(boolean animated) {
        boolean z = false;
        if (this.proxyDrawable != null) {
            boolean proxyEnabled;
            SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
            String proxyAddress = preferences.getString("proxy_ip", "");
            if (!preferences.getBoolean("proxy_enabled", false) || TextUtils.isEmpty(proxyAddress)) {
                proxyEnabled = false;
            } else {
                proxyEnabled = true;
            }
            if (proxyEnabled || (MessagesController.getInstance(this.currentAccount).blockedCountry && !SharedConfig.proxyList.isEmpty())) {
                if (!this.actionBar.isSearchFieldVisible()) {
                    this.proxyItem.setVisibility(0);
                }
                ProxyDrawable proxyDrawable = this.proxyDrawable;
                if (this.currentConnectionState == 3 || this.currentConnectionState == 5) {
                    z = true;
                }
                proxyDrawable.setConnected(proxyEnabled, z, animated);
                this.proxyItemVisisble = true;
                return;
            }
            this.proxyItem.setVisibility(8);
            this.proxyItemVisisble = false;
        }
    }

    private void updateSelectedCount() {
        if (this.commentView != null) {
            AnimatorSet animatorSet;
            Animator[] animatorArr;
            if (this.dialogsAdapter.hasSelectedDialogs()) {
                if (this.commentView.getTag() == null) {
                    this.commentView.setFieldText("");
                    this.commentView.setVisibility(0);
                    animatorSet = new AnimatorSet();
                    animatorArr = new Animator[1];
                    animatorArr[0] = ObjectAnimator.ofFloat(this.commentView, View.TRANSLATION_Y, new float[]{(float) this.commentView.getMeasuredHeight(), 0.0f});
                    animatorSet.playTogether(animatorArr);
                    animatorSet.setDuration(180);
                    animatorSet.setInterpolator(new DecelerateInterpolator());
                    animatorSet.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animation) {
                            DialogsActivity.this.commentView.setTag(Integer.valueOf(2));
                        }
                    });
                    animatorSet.start();
                    this.commentView.setTag(Integer.valueOf(1));
                }
                this.actionBar.setTitle(LocaleController.formatPluralString("Recipient", this.dialogsAdapter.getSelectedDialogs().size()));
                return;
            }
            if (this.dialogsType == 3 && this.selectAlertString == null) {
                this.actionBar.setTitle(LocaleController.getString("ForwardTo", R.string.ForwardTo));
            } else {
                this.actionBar.setTitle(LocaleController.getString("SelectChat", R.string.SelectChat));
            }
            if (this.commentView.getTag() != null) {
                this.commentView.hidePopup(false);
                this.commentView.closeKeyboard();
                animatorSet = new AnimatorSet();
                animatorArr = new Animator[1];
                animatorArr[0] = ObjectAnimator.ofFloat(this.commentView, View.TRANSLATION_Y, new float[]{0.0f, (float) this.commentView.getMeasuredHeight()});
                animatorSet.playTogether(animatorArr);
                animatorSet.setDuration(180);
                animatorSet.setInterpolator(new DecelerateInterpolator());
                animatorSet.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        DialogsActivity.this.commentView.setVisibility(8);
                    }
                });
                animatorSet.start();
                this.commentView.setTag(null);
                this.listView.requestLayout();
            }
        }
    }

    @TargetApi(23)
    private void askForPermissons(boolean alert) {
        Activity activity = getParentActivity();
        if (activity != null) {
            ArrayList<String> permissons = new ArrayList();
            if (UserConfig.getInstance(this.currentAccount).syncContacts && this.askAboutContacts && activity.checkSelfPermission("android.permission.READ_CONTACTS") != 0) {
                if (alert) {
                    Dialog create = AlertsCreator.createContactsPermissionDialog(activity, new DialogsActivity$$Lambda$7(this)).create();
                    this.permissionDialog = create;
                    showDialog(create);
                    return;
                }
                permissons.add("android.permission.READ_CONTACTS");
                permissons.add("android.permission.WRITE_CONTACTS");
                permissons.add("android.permission.GET_ACCOUNTS");
            }
            if (activity.checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") != 0) {
                permissons.add("android.permission.READ_EXTERNAL_STORAGE");
                permissons.add("android.permission.WRITE_EXTERNAL_STORAGE");
            }
            if (!permissons.isEmpty()) {
                try {
                    activity.requestPermissions((String[]) permissons.toArray(new String[permissons.size()]), 1);
                } catch (Exception e) {
                }
            }
        }
    }

    final /* synthetic */ void lambda$askForPermissons$7$DialogsActivity(int param) {
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

    protected void onDialogDismiss(Dialog dialog) {
        super.onDialogDismiss(dialog);
        if (this.permissionDialog != null && dialog == this.permissionDialog && getParentActivity() != null && this.askAboutContacts) {
            askForPermissons(false);
        }
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (!this.onlySelect && this.floatingButtonContainer != null) {
            this.floatingButtonContainer.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
                public void onGlobalLayout() {
                    DialogsActivity.this.floatingButtonContainer.setTranslationY(DialogsActivity.this.floatingHidden ? (float) AndroidUtilities.dp(100.0f) : -DialogsActivity.this.additionalFloatingTranslation);
                    DialogsActivity.this.floatingButtonContainer.setClickable(!DialogsActivity.this.floatingHidden);
                    if (DialogsActivity.this.floatingButtonContainer != null) {
                        DialogsActivity.this.floatingButtonContainer.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                }
            });
        }
    }

    public void onRequestPermissionsResultFragment(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1) {
            for (int a = 0; a < permissions.length; a++) {
                if (grantResults.length > a) {
                    String str = permissions[a];
                    boolean z = true;
                    switch (str.hashCode()) {
                        case 1365911975:
                            if (str.equals("android.permission.WRITE_EXTERNAL_STORAGE")) {
                                z = true;
                                break;
                            }
                            break;
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
                        case true:
                            if (grantResults[a] != 0) {
                                break;
                            }
                            ImageLoader.getInstance().checkMediaPaths();
                            break;
                        default:
                            break;
                    }
                }
            }
        }
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.dialogsNeedReload) {
            if (this.dialogsAdapter != null) {
                if (this.dialogsAdapter.isDataSetChanged() || args.length > 0) {
                    this.dialogsAdapter.notifyDataSetChanged();
                } else {
                    updateVisibleRows(2048);
                }
            }
            if (this.listView != null) {
                try {
                    if (this.listView.getAdapter() == this.dialogsAdapter) {
                        this.searchEmptyView.setVisibility(8);
                        this.listView.setEmptyView(this.progressView);
                        return;
                    }
                    if (this.searching && this.searchWas) {
                        this.listView.setEmptyView(this.searchEmptyView);
                    } else {
                        this.searchEmptyView.setVisibility(8);
                        this.listView.setEmptyView(null);
                    }
                    this.progressView.setVisibility(8);
                } catch (Throwable e) {
                    FileLog.e(e);
                }
            }
        } else if (id == NotificationCenter.emojiDidLoad) {
            updateVisibleRows(0);
        } else if (id == NotificationCenter.closeSearchByActiveAction) {
            if (this.actionBar != null) {
                this.actionBar.closeSearchField();
            }
        } else if (id == NotificationCenter.proxySettingsChanged) {
            updateProxyButton(false);
        } else if (id == NotificationCenter.updateInterfaces) {
            Integer mask = args[0];
            updateVisibleRows(mask.intValue());
            if (!((mask.intValue() & 2048) == 0 && (mask.intValue() & 256) == 0)) {
            }
        } else if (id == NotificationCenter.appDidLogout) {
            dialogsLoaded[this.currentAccount] = false;
        } else if (id == NotificationCenter.encryptedChatUpdated) {
            updateVisibleRows(0);
        } else if (id == NotificationCenter.contactsDidLoad) {
            if (this.dialogsType != 0 || !MessagesController.getInstance(this.currentAccount).dialogs.isEmpty()) {
                updateVisibleRows(0);
            } else if (this.dialogsAdapter != null) {
                this.dialogsAdapter.notifyDataSetChanged();
            }
        } else if (id == NotificationCenter.openedChatChanged) {
            if (this.dialogsType == 0 && AndroidUtilities.isTablet()) {
                boolean close = ((Boolean) args[1]).booleanValue();
                long dialog_id = ((Long) args[0]).longValue();
                if (!close) {
                    this.openedDialogId = dialog_id;
                } else if (dialog_id == this.openedDialogId) {
                    this.openedDialogId = 0;
                }
                if (this.dialogsAdapter != null) {
                    this.dialogsAdapter.setOpenedDialogId(this.openedDialogId);
                }
                updateVisibleRows(512);
            }
        } else if (id == NotificationCenter.notificationsSettingsUpdated) {
            updateVisibleRows(0);
        } else if (id == NotificationCenter.messageReceivedByAck || id == NotificationCenter.messageReceivedByServer || id == NotificationCenter.messageSendError) {
            updateVisibleRows(4096);
        } else if (id == NotificationCenter.didSetPasscode) {
            updatePasscodeButton();
        } else if (id == NotificationCenter.needReloadRecentDialogsSearch) {
            if (this.dialogsSearchAdapter != null) {
                this.dialogsSearchAdapter.loadRecentSearch();
            }
        } else if (id == NotificationCenter.replyMessagesDidLoad) {
            updateVisibleRows(32768);
        } else if (id == NotificationCenter.reloadHints) {
            if (this.dialogsSearchAdapter != null) {
                this.dialogsSearchAdapter.notifyDataSetChanged();
            }
        } else if (id == NotificationCenter.didUpdateConnectionState) {
            int state = ConnectionsManager.getInstance(account).getConnectionState();
            if (this.currentConnectionState != state) {
                this.currentConnectionState = state;
                updateProxyButton(true);
            }
        } else if (id != NotificationCenter.dialogsUnreadCounterChanged && id == NotificationCenter.needDeleteDialog && this.undoView != null) {
            long dialogId = ((Long) args[0]).longValue();
            User user = args[1];
            this.undoView.showWithAction(dialogId, false, new DialogsActivity$$Lambda$8(this, args[2], dialogId));
        }
    }

    final /* synthetic */ void lambda$didReceivedNotification$8$DialogsActivity(Chat chat, long dialogId) {
        if (chat == null) {
            MessagesController.getInstance(this.currentAccount).deleteDialog(dialogId, 0);
        } else if (ChatObject.isNotInChat(chat)) {
            MessagesController.getInstance(this.currentAccount).deleteDialog(dialogId, 0);
        } else {
            MessagesController.getInstance(this.currentAccount).deleteUserFromChat((int) (-dialogId), MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(UserConfig.getInstance(this.currentAccount).getClientUserId())), null);
        }
    }

    private ArrayList<TL_dialog> getDialogsArray() {
        if (this.dialogsType == 0) {
            return MessagesController.getInstance(this.currentAccount).dialogs;
        }
        if (this.dialogsType == 1) {
            return MessagesController.getInstance(this.currentAccount).dialogsServerOnly;
        }
        if (this.dialogsType == 2) {
            return MessagesController.getInstance(this.currentAccount).dialogsGroupsOnly;
        }
        if (this.dialogsType == 3) {
            return MessagesController.getInstance(this.currentAccount).dialogsForward;
        }
        if (this.dialogsType == 4) {
            return MessagesController.getInstance(this.currentAccount).dialogsUsersOnly;
        }
        if (this.dialogsType == 5) {
            return MessagesController.getInstance(this.currentAccount).dialogsChannelsOnly;
        }
        return null;
    }

    public void setSideMenu(RecyclerView recyclerView) {
        this.sideMenu = recyclerView;
        this.sideMenu.setBackgroundColor(Theme.getColor("chats_menuBackground"));
        this.sideMenu.setGlowColor(Theme.getColor("chats_menuBackground"));
    }

    private void updatePasscodeButton() {
        if (this.passcodeItem != null) {
            if (SharedConfig.passcodeHash.length() == 0 || this.searching) {
                this.passcodeItem.setVisibility(8);
                return;
            }
            this.passcodeItem.setVisibility(0);
            if (SharedConfig.appLocked) {
                this.passcodeItem.setIcon((int) R.drawable.lock_close);
            } else {
                this.passcodeItem.setIcon((int) R.drawable.lock_open);
            }
        }
    }

    private void hideFloatingButton(boolean hide) {
        if (this.floatingHidden != hide) {
            boolean z;
            this.floatingHidden = hide;
            AnimatorSet animatorSet = new AnimatorSet();
            Animator[] animatorArr = new Animator[1];
            FrameLayout frameLayout = this.floatingButtonContainer;
            Property property = View.TRANSLATION_Y;
            float[] fArr = new float[1];
            fArr[0] = this.floatingHidden ? (float) AndroidUtilities.dp(100.0f) : -this.additionalFloatingTranslation;
            animatorArr[0] = ObjectAnimator.ofFloat(frameLayout, property, fArr);
            animatorSet.playTogether(animatorArr);
            animatorSet.setDuration(300);
            animatorSet.setInterpolator(this.floatingInterpolator);
            FrameLayout frameLayout2 = this.floatingButtonContainer;
            if (hide) {
                z = false;
            } else {
                z = true;
            }
            frameLayout2.setClickable(z);
            animatorSet.start();
        }
    }

    private void updateVisibleRows(int mask) {
        if (this.listView != null) {
            int count = this.listView.getChildCount();
            for (int a = 0; a < count; a++) {
                View child = this.listView.getChildAt(a);
                if (child instanceof DialogCell) {
                    if (this.listView.getAdapter() != this.dialogsSearchAdapter) {
                        DialogCell cell = (DialogCell) child;
                        if ((mask & 2048) != 0) {
                            cell.checkCurrentDialogIndex();
                            if (this.dialogsType == 0 && AndroidUtilities.isTablet()) {
                                boolean z;
                                if (cell.getDialogId() == this.openedDialogId) {
                                    z = true;
                                } else {
                                    z = false;
                                }
                                cell.setDialogSelected(z);
                            }
                        } else if ((mask & 512) == 0) {
                            cell.update(mask);
                        } else if (this.dialogsType == 0 && AndroidUtilities.isTablet()) {
                            cell.setDialogSelected(cell.getDialogId() == this.openedDialogId);
                        }
                    }
                } else if (child instanceof UserCell) {
                    ((UserCell) child).update(mask);
                } else if (child instanceof ProfileSearchCell) {
                    ((ProfileSearchCell) child).update(mask);
                } else if (child instanceof RecyclerListView) {
                    RecyclerListView innerListView = (RecyclerListView) child;
                    int count2 = innerListView.getChildCount();
                    for (int b = 0; b < count2; b++) {
                        View child2 = innerListView.getChildAt(b);
                        if (child2 instanceof HintDialogCell) {
                            ((HintDialogCell) child2).update(mask);
                        }
                    }
                }
            }
        }
    }

    public void setDelegate(DialogsActivityDelegate dialogsActivityDelegate) {
        this.delegate = dialogsActivityDelegate;
    }

    public void setSearchString(String string) {
        this.searchString = string;
    }

    public boolean isMainDialogList() {
        return this.delegate == null && this.searchString == null;
    }

    private void didSelectResult(long dialog_id, boolean useAlert, boolean param) {
        Chat chat;
        AlertDialog.Builder builder;
        if (this.addToGroupAlertString == null && ((int) dialog_id) < 0) {
            chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-((int) dialog_id)));
            if (ChatObject.isChannel(chat) && !chat.megagroup && (this.cantSendToChannels || !ChatObject.isCanWriteToChannel(-((int) dialog_id), this.currentAccount))) {
                builder = new AlertDialog.Builder(getParentActivity());
                builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                builder.setMessage(LocaleController.getString("ChannelCantSendMessage", R.string.ChannelCantSendMessage));
                builder.setNegativeButton(LocaleController.getString("OK", R.string.OK), null);
                showDialog(builder.create());
                return;
            }
        }
        if (!useAlert || ((this.selectAlertString == null || this.selectAlertStringGroup == null) && this.addToGroupAlertString == null)) {
            if (this.delegate != null) {
                ArrayList<Long> dids = new ArrayList();
                dids.add(Long.valueOf(dialog_id));
                this.delegate.didSelectDialogs(this, dids, null, param);
                this.delegate = null;
                return;
            }
            lambda$createView$1$PhotoAlbumPickerActivity();
        } else if (getParentActivity() != null) {
            builder = new AlertDialog.Builder(getParentActivity());
            builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
            int lower_part = (int) dialog_id;
            int high_id = (int) (dialog_id >> 32);
            if (lower_part == 0) {
                if (MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(MessagesController.getInstance(this.currentAccount).getEncryptedChat(Integer.valueOf(high_id)).user_id)) != null) {
                    builder.setMessage(LocaleController.formatStringSimple(this.selectAlertString, UserObject.getUserName(user)));
                } else {
                    return;
                }
            } else if (high_id == 1) {
                if (MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(lower_part)) != null) {
                    builder.setMessage(LocaleController.formatStringSimple(this.selectAlertStringGroup, chat.title));
                } else {
                    return;
                }
            } else if (lower_part == UserConfig.getInstance(this.currentAccount).getClientUserId()) {
                builder.setMessage(LocaleController.formatStringSimple(this.selectAlertStringGroup, LocaleController.getString("SavedMessages", R.string.SavedMessages)));
            } else if (lower_part > 0) {
                if (MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(lower_part)) != null) {
                    builder.setMessage(LocaleController.formatStringSimple(this.selectAlertString, UserObject.getUserName(user)));
                } else {
                    return;
                }
            } else if (lower_part < 0) {
                if (MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-lower_part)) == null) {
                    return;
                }
                if (this.addToGroupAlertString != null) {
                    builder.setMessage(LocaleController.formatStringSimple(this.addToGroupAlertString, chat.title));
                } else {
                    builder.setMessage(LocaleController.formatStringSimple(this.selectAlertStringGroup, chat.title));
                }
            }
            builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogsActivity$$Lambda$9(this, dialog_id));
            builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
            showDialog(builder.create());
        }
    }

    final /* synthetic */ void lambda$didSelectResult$9$DialogsActivity(long dialog_id, DialogInterface dialogInterface, int i) {
        didSelectResult(dialog_id, false, false);
    }

    public ThemeDescription[] getThemeDescriptions() {
        ThemeDescriptionDelegate cellDelegate = new DialogsActivity$$Lambda$10(this);
        r10 = new ThemeDescription[146];
        r10[4] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, new Drawable[]{Theme.dialogs_holidayDrawable}, null, "actionBarDefaultTitle");
        r10[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector");
        r10[6] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCH, null, null, null, null, "actionBarDefaultSearch");
        r10[7] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCHPLACEHOLDER, null, null, null, null, "actionBarDefaultSearchPlaceholder");
        r10[8] = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21");
        r10[9] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, "divider");
        r10[10] = new ThemeDescription(this.searchEmptyView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "emptyListPlaceholder");
        r10[11] = new ThemeDescription(this.searchEmptyView, ThemeDescription.FLAG_PROGRESSBAR, null, null, null, null, "progressCircle");
        r10[12] = new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{DialogsEmptyCell.class}, new String[]{"emptyTextView1"}, null, null, null, "emptyListPlaceholder");
        r10[13] = new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{DialogsEmptyCell.class}, new String[]{"emptyTextView2"}, null, null, null, "emptyListPlaceholder");
        r10[14] = new ThemeDescription(this.floatingButton, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, "chats_actionIcon");
        r10[15] = new ThemeDescription(this.floatingButton, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "chats_actionBackground");
        r10[16] = new ThemeDescription(this.floatingButton, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, "chats_actionPressedBackground");
        r10[17] = new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class, ProfileSearchCell.class}, null, new Drawable[]{Theme.avatar_broadcastDrawable, Theme.avatar_savedDrawable}, null, "avatar_text");
        r10[18] = new ThemeDescription(null, 0, null, null, null, cellDelegate, "avatar_backgroundRed");
        r10[19] = new ThemeDescription(null, 0, null, null, null, cellDelegate, "avatar_backgroundOrange");
        r10[20] = new ThemeDescription(null, 0, null, null, null, cellDelegate, "avatar_backgroundViolet");
        r10[21] = new ThemeDescription(null, 0, null, null, null, cellDelegate, "avatar_backgroundGreen");
        r10[22] = new ThemeDescription(null, 0, null, null, null, cellDelegate, "avatar_backgroundCyan");
        r10[23] = new ThemeDescription(null, 0, null, null, null, cellDelegate, "avatar_backgroundBlue");
        r10[24] = new ThemeDescription(null, 0, null, null, null, cellDelegate, "avatar_backgroundPink");
        r10[25] = new ThemeDescription(null, 0, null, null, null, cellDelegate, "avatar_backgroundSaved");
        r10[26] = new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class}, Theme.dialogs_countPaint, null, null, "chats_unreadCounter");
        r10[27] = new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class}, Theme.dialogs_countGrayPaint, null, null, "chats_unreadCounterMuted");
        r10[28] = new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class}, Theme.dialogs_countTextPaint, null, null, "chats_unreadCounterText");
        r10[29] = new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class, ProfileSearchCell.class}, Theme.dialogs_namePaint, null, null, "chats_name");
        r10[30] = new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class, ProfileSearchCell.class}, Theme.dialogs_nameEncryptedPaint, null, null, "chats_secretName");
        r10[31] = new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class, ProfileSearchCell.class}, null, new Drawable[]{Theme.dialogs_lockDrawable}, null, "chats_secretIcon");
        r10[32] = new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class, ProfileSearchCell.class}, null, new Drawable[]{Theme.dialogs_groupDrawable, Theme.dialogs_broadcastDrawable, Theme.dialogs_botDrawable}, null, "chats_nameIcon");
        r10[33] = new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class}, null, new Drawable[]{Theme.dialogs_pinnedDrawable}, null, "chats_pinnedIcon");
        r10[34] = new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class}, Theme.dialogs_messagePaint, null, null, "chats_message");
        r10[35] = new ThemeDescription(null, 0, null, null, null, cellDelegate, "chats_nameMessage");
        r10[36] = new ThemeDescription(null, 0, null, null, null, cellDelegate, "chats_draft");
        r10[37] = new ThemeDescription(null, 0, null, null, null, cellDelegate, "chats_attachMessage");
        r10[38] = new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class}, Theme.dialogs_messagePrintingPaint, null, null, "chats_actionMessage");
        r10[39] = new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class}, Theme.dialogs_timePaint, null, null, "chats_date");
        r10[40] = new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class}, Theme.dialogs_pinnedPaint, null, null, "chats_pinnedOverlay");
        r10[41] = new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class}, Theme.dialogs_tabletSeletedPaint, null, null, "chats_tabletSelectedOverlay");
        r10[42] = new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class}, null, new Drawable[]{Theme.dialogs_checkDrawable, Theme.dialogs_halfCheckDrawable}, null, "chats_sentCheck");
        r10[43] = new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class}, null, new Drawable[]{Theme.dialogs_clockDrawable}, null, "chats_sentClock");
        r10[44] = new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class}, Theme.dialogs_errorPaint, null, null, "chats_sentError");
        r10[45] = new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class}, null, new Drawable[]{Theme.dialogs_errorDrawable}, null, "chats_sentErrorIcon");
        r10[46] = new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class, ProfileSearchCell.class}, null, new Drawable[]{Theme.dialogs_verifiedCheckDrawable}, null, "chats_verifiedCheck");
        r10[47] = new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class, ProfileSearchCell.class}, null, new Drawable[]{Theme.dialogs_verifiedDrawable}, null, "chats_verifiedBackground");
        r10[48] = new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class}, null, new Drawable[]{Theme.dialogs_muteDrawable}, null, "chats_muteIcon");
        r10[49] = new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class}, null, new Drawable[]{Theme.dialogs_mentionDrawable}, null, "chats_mentionIcon");
        r10[50] = new ThemeDescription(this.sideMenu, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "chats_menuBackground");
        r10[51] = new ThemeDescription(this.sideMenu, 0, new Class[]{DrawerProfileCell.class}, null, null, null, "chats_menuName");
        r10[52] = new ThemeDescription(this.sideMenu, 0, new Class[]{DrawerProfileCell.class}, null, null, null, "chats_menuPhone");
        r10[53] = new ThemeDescription(this.sideMenu, 0, new Class[]{DrawerProfileCell.class}, null, null, null, "chats_menuPhoneCats");
        r10[54] = new ThemeDescription(this.sideMenu, 0, new Class[]{DrawerProfileCell.class}, null, null, null, "chats_menuCloudBackgroundCats");
        r10[55] = new ThemeDescription(this.sideMenu, 0, new Class[]{DrawerProfileCell.class}, null, null, null, "chat_serviceBackground");
        r10[56] = new ThemeDescription(this.sideMenu, 0, new Class[]{DrawerProfileCell.class}, null, null, null, "chats_menuTopShadow");
        r10[57] = new ThemeDescription(this.sideMenu, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{DrawerProfileCell.class}, null, null, null, "avatar_backgroundActionBarBlue");
        r10[58] = new ThemeDescription(this.sideMenu, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{DrawerActionCell.class}, new String[]{"textView"}, null, null, null, "chats_menuItemIcon");
        r10[59] = new ThemeDescription(this.sideMenu, 0, new Class[]{DrawerActionCell.class}, new String[]{"textView"}, null, null, null, "chats_menuItemText");
        r10[60] = new ThemeDescription(this.sideMenu, 0, new Class[]{DrawerUserCell.class}, new String[]{"textView"}, null, null, null, "chats_menuItemText");
        r10[61] = new ThemeDescription(this.sideMenu, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{DrawerUserCell.class}, new String[]{"checkBox"}, null, null, null, "chats_unreadCounterText");
        r10[62] = new ThemeDescription(this.sideMenu, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{DrawerUserCell.class}, new String[]{"checkBox"}, null, null, null, "chats_unreadCounter");
        r10[63] = new ThemeDescription(this.sideMenu, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{DrawerUserCell.class}, new String[]{"checkBox"}, null, null, null, "chats_menuBackground");
        r10[64] = new ThemeDescription(this.sideMenu, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{DrawerAddCell.class}, new String[]{"textView"}, null, null, null, "chats_menuItemIcon");
        r10[65] = new ThemeDescription(this.sideMenu, 0, new Class[]{DrawerAddCell.class}, new String[]{"textView"}, null, null, null, "chats_menuItemText");
        r10[66] = new ThemeDescription(this.sideMenu, 0, new Class[]{DividerCell.class}, Theme.dividerPaint, null, null, "divider");
        r10[67] = new ThemeDescription(this.listView, 0, new Class[]{LoadingCell.class}, new String[]{"progressBar"}, null, null, null, "progressCircle");
        r10[68] = new ThemeDescription(this.listView, 0, new Class[]{ProfileSearchCell.class}, Theme.dialogs_offlinePaint, null, null, "windowBackgroundWhiteGrayText3");
        r10[69] = new ThemeDescription(this.listView, 0, new Class[]{ProfileSearchCell.class}, Theme.dialogs_onlinePaint, null, null, "windowBackgroundWhiteBlueText3");
        r10[70] = new ThemeDescription(this.listView, 0, new Class[]{GraySectionCell.class}, new String[]{"textView"}, null, null, null, "key_graySectionText");
        r10[71] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{GraySectionCell.class}, null, null, null, "graySection");
        r10[72] = new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{HashtagSearchCell.class}, null, null, null, "windowBackgroundWhiteBlackText");
        r10[73] = new ThemeDescription(this.progressView, ThemeDescription.FLAG_PROGRESSBAR, null, null, null, null, "progressCircle");
        r10[74] = new ThemeDescription(this.dialogsSearchAdapter != null ? this.dialogsSearchAdapter.getInnerListView() : null, 0, new Class[]{HintDialogCell.class}, Theme.dialogs_countPaint, null, null, "chats_unreadCounter");
        r10[75] = new ThemeDescription(this.dialogsSearchAdapter != null ? this.dialogsSearchAdapter.getInnerListView() : null, 0, new Class[]{HintDialogCell.class}, Theme.dialogs_countGrayPaint, null, null, "chats_unreadCounterMuted");
        r10[76] = new ThemeDescription(this.dialogsSearchAdapter != null ? this.dialogsSearchAdapter.getInnerListView() : null, 0, new Class[]{HintDialogCell.class}, Theme.dialogs_countTextPaint, null, null, "chats_unreadCounterText");
        r10[77] = new ThemeDescription(this.dialogsSearchAdapter != null ? this.dialogsSearchAdapter.getInnerListView() : null, 0, new Class[]{HintDialogCell.class}, new String[]{"nameTextView"}, null, null, null, "windowBackgroundWhiteBlackText");
        r10[78] = new ThemeDescription(this.dialogsSearchAdapter != null ? this.dialogsSearchAdapter.getInnerListView() : null, 0, new Class[]{HintDialogCell.class}, null, null, null, "chats_onlineCircle");
        r10[79] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND | ThemeDescription.FLAG_CHECKTAG, new Class[]{FragmentContextView.class}, new String[]{"frameLayout"}, null, null, null, "inappPlayerBackground");
        r10[80] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{FragmentContextView.class}, new String[]{"playButton"}, null, null, null, "inappPlayerPlayPause");
        r10[81] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{FragmentContextView.class}, new String[]{"titleTextView"}, null, null, null, "inappPlayerTitle");
        r10[82] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_FASTSCROLL, new Class[]{FragmentContextView.class}, new String[]{"titleTextView"}, null, null, null, "inappPlayerPerformer");
        r10[83] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{FragmentContextView.class}, new String[]{"closeButton"}, null, null, null, "inappPlayerClose");
        r10[84] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND | ThemeDescription.FLAG_CHECKTAG, new Class[]{FragmentContextView.class}, new String[]{"frameLayout"}, null, null, null, "returnToCallBackground");
        r10[85] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{FragmentContextView.class}, new String[]{"titleTextView"}, null, null, null, "returnToCallText");
        r10[86] = new ThemeDescription(this.undoView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "undo_background");
        r10[87] = new ThemeDescription(this.undoView, 0, new Class[]{UndoView.class}, new String[]{"undoImageView"}, null, null, null, "undo_cancelColor");
        r10[88] = new ThemeDescription(this.undoView, 0, new Class[]{UndoView.class}, new String[]{"undoTextView"}, null, null, null, "undo_cancelColor");
        r10[89] = new ThemeDescription(this.undoView, 0, new Class[]{UndoView.class}, new String[]{"infoTextView"}, null, null, null, "undo_infoColor");
        r10[90] = new ThemeDescription(this.undoView, 0, new Class[]{UndoView.class}, new String[]{"textPaint"}, null, null, null, "undo_infoColor");
        r10[91] = new ThemeDescription(this.undoView, 0, new Class[]{UndoView.class}, new String[]{"progressPaint"}, null, null, null, "undo_infoColor");
        r10[92] = new ThemeDescription(null, 0, null, null, null, null, "dialogBackground");
        r10[93] = new ThemeDescription(null, 0, null, null, null, null, "dialogBackgroundGray");
        r10[94] = new ThemeDescription(null, 0, null, null, null, null, "dialogTextBlack");
        r10[95] = new ThemeDescription(null, 0, null, null, null, null, "dialogTextLink");
        r10[96] = new ThemeDescription(null, 0, null, null, null, null, "dialogLinkSelection");
        r10[97] = new ThemeDescription(null, 0, null, null, null, null, "dialogTextBlue");
        r10[98] = new ThemeDescription(null, 0, null, null, null, null, "dialogTextBlue2");
        r10[99] = new ThemeDescription(null, 0, null, null, null, null, "dialogTextBlue3");
        r10[100] = new ThemeDescription(null, 0, null, null, null, null, "dialogTextBlue4");
        r10[101] = new ThemeDescription(null, 0, null, null, null, null, "dialogTextRed");
        r10[102] = new ThemeDescription(null, 0, null, null, null, null, "dialogTextRed2");
        r10[103] = new ThemeDescription(null, 0, null, null, null, null, "dialogTextGray");
        r10[104] = new ThemeDescription(null, 0, null, null, null, null, "dialogTextGray2");
        r10[105] = new ThemeDescription(null, 0, null, null, null, null, "dialogTextGray3");
        r10[106] = new ThemeDescription(null, 0, null, null, null, null, "dialogTextGray4");
        r10[107] = new ThemeDescription(null, 0, null, null, null, null, "dialogIcon");
        r10[108] = new ThemeDescription(null, 0, null, null, null, null, "dialogRedIcon");
        r10[109] = new ThemeDescription(null, 0, null, null, null, null, "dialogTextHint");
        r10[110] = new ThemeDescription(null, 0, null, null, null, null, "dialogInputField");
        r10[111] = new ThemeDescription(null, 0, null, null, null, null, "dialogInputFieldActivated");
        r10[112] = new ThemeDescription(null, 0, null, null, null, null, "dialogCheckboxSquareBackground");
        r10[113] = new ThemeDescription(null, 0, null, null, null, null, "dialogCheckboxSquareCheck");
        r10[114] = new ThemeDescription(null, 0, null, null, null, null, "dialogCheckboxSquareUnchecked");
        r10[115] = new ThemeDescription(null, 0, null, null, null, null, "dialogCheckboxSquareDisabled");
        r10[116] = new ThemeDescription(null, 0, null, null, null, null, "dialogRadioBackground");
        r10[117] = new ThemeDescription(null, 0, null, null, null, null, "dialogRadioBackgroundChecked");
        r10[118] = new ThemeDescription(null, 0, null, null, null, null, "dialogProgressCircle");
        r10[119] = new ThemeDescription(null, 0, null, null, null, null, "dialogButton");
        r10[120] = new ThemeDescription(null, 0, null, null, null, null, "dialogButtonSelector");
        r10[121] = new ThemeDescription(null, 0, null, null, null, null, "dialogScrollGlow");
        r10[122] = new ThemeDescription(null, 0, null, null, null, null, "dialogRoundCheckBox");
        r10[123] = new ThemeDescription(null, 0, null, null, null, null, "dialogRoundCheckBoxCheck");
        r10[124] = new ThemeDescription(null, 0, null, null, null, null, "dialogBadgeBackground");
        r10[125] = new ThemeDescription(null, 0, null, null, null, null, "dialogBadgeText");
        r10[126] = new ThemeDescription(null, 0, null, null, null, null, "dialogLineProgress");
        r10[127] = new ThemeDescription(null, 0, null, null, null, null, "dialogLineProgressBackground");
        r10[128] = new ThemeDescription(null, 0, null, null, null, null, "dialogGrayLine");
        r10[129] = new ThemeDescription(null, 0, null, null, null, null, "dialog_inlineProgressBackground");
        r10[130] = new ThemeDescription(null, 0, null, null, null, null, "dialog_inlineProgress");
        r10[131] = new ThemeDescription(null, 0, null, null, null, null, "player_actionBar");
        r10[132] = new ThemeDescription(null, 0, null, null, null, null, "player_actionBarSelector");
        r10[133] = new ThemeDescription(null, 0, null, null, null, null, "player_actionBarTitle");
        r10[134] = new ThemeDescription(null, 0, null, null, null, null, "player_actionBarTop");
        r10[135] = new ThemeDescription(null, 0, null, null, null, null, "player_actionBarSubtitle");
        r10[136] = new ThemeDescription(null, 0, null, null, null, null, "player_actionBarItems");
        r10[137] = new ThemeDescription(null, 0, null, null, null, null, "player_background");
        r10[138] = new ThemeDescription(null, 0, null, null, null, null, "player_time");
        r10[139] = new ThemeDescription(null, 0, null, null, null, null, "player_progressBackground");
        r10[140] = new ThemeDescription(null, 0, null, null, null, null, "key_player_progressCachedBackground");
        r10[141] = new ThemeDescription(null, 0, null, null, null, null, "player_progress");
        r10[142] = new ThemeDescription(null, 0, null, null, null, null, "player_placeholder");
        r10[143] = new ThemeDescription(null, 0, null, null, null, null, "player_placeholderBackground");
        r10[144] = new ThemeDescription(null, 0, null, null, null, null, "player_button");
        r10[145] = new ThemeDescription(null, 0, null, null, null, null, "player_buttonActive");
        return r10;
    }

    final /* synthetic */ void lambda$getThemeDescriptions$10$DialogsActivity() {
        int count;
        int a;
        View child;
        if (this.listView != null) {
            count = this.listView.getChildCount();
            for (a = 0; a < count; a++) {
                child = this.listView.getChildAt(a);
                if (child instanceof ProfileSearchCell) {
                    ((ProfileSearchCell) child).update(0);
                } else if (child instanceof DialogCell) {
                    ((DialogCell) child).update(0);
                }
            }
        }
        if (this.dialogsSearchAdapter != null) {
            RecyclerListView recyclerListView = this.dialogsSearchAdapter.getInnerListView();
            if (recyclerListView != null) {
                count = recyclerListView.getChildCount();
                for (a = 0; a < count; a++) {
                    child = recyclerListView.getChildAt(a);
                    if (child instanceof HintDialogCell) {
                        ((HintDialogCell) child).update();
                    }
                }
            }
        }
    }
}
