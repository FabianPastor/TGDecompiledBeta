package org.telegram.p005ui;

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
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Outline;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.text.TextUtils;
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
import android.widget.TextView;
import com.google.android.exoplayer2.extractor.p003ts.TsExtractor;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.C0541R;
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
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.LinearSmoothScrollerMiddle;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.support.widget.RecyclerView.Adapter;
import org.telegram.messenger.support.widget.RecyclerView.OnScrollListener;
import org.telegram.messenger.support.widget.RecyclerView.State;
import org.telegram.p005ui.ActionBar.ActionBarMenu;
import org.telegram.p005ui.ActionBar.ActionBarMenuItem;
import org.telegram.p005ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener;
import org.telegram.p005ui.ActionBar.AlertDialog;
import org.telegram.p005ui.ActionBar.AlertDialog.Builder;
import org.telegram.p005ui.ActionBar.BaseFragment;
import org.telegram.p005ui.ActionBar.BottomSheet;
import org.telegram.p005ui.ActionBar.C0704ActionBar.ActionBarMenuOnItemClick;
import org.telegram.p005ui.ActionBar.MenuDrawable;
import org.telegram.p005ui.ActionBar.Theme;
import org.telegram.p005ui.ActionBar.ThemeDescription;
import org.telegram.p005ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate;
import org.telegram.p005ui.Adapters.DialogsAdapter;
import org.telegram.p005ui.Adapters.DialogsSearchAdapter;
import org.telegram.p005ui.Adapters.DialogsSearchAdapter.DialogsSearchAdapterDelegate;
import org.telegram.p005ui.Cells.AccountSelectCell;
import org.telegram.p005ui.Cells.DialogCell;
import org.telegram.p005ui.Cells.DialogsEmptyCell;
import org.telegram.p005ui.Cells.DividerCell;
import org.telegram.p005ui.Cells.DrawerActionCell;
import org.telegram.p005ui.Cells.DrawerAddCell;
import org.telegram.p005ui.Cells.DrawerProfileCell;
import org.telegram.p005ui.Cells.DrawerUserCell;
import org.telegram.p005ui.Cells.GraySectionCell;
import org.telegram.p005ui.Cells.HashtagSearchCell;
import org.telegram.p005ui.Cells.HintDialogCell;
import org.telegram.p005ui.Cells.LoadingCell;
import org.telegram.p005ui.Cells.ProfileSearchCell;
import org.telegram.p005ui.Cells.UserCell;
import org.telegram.p005ui.Components.AlertsCreator;
import org.telegram.p005ui.Components.AnimatedArrowDrawable;
import org.telegram.p005ui.Components.AvatarDrawable;
import org.telegram.p005ui.Components.BackupImageView;
import org.telegram.p005ui.Components.ChatActivityEnterView;
import org.telegram.p005ui.Components.ChatActivityEnterView.ChatActivityEnterViewDelegate;
import org.telegram.p005ui.Components.CombinedDrawable;
import org.telegram.p005ui.Components.EmptyTextProgressView;
import org.telegram.p005ui.Components.FragmentContextView;
import org.telegram.p005ui.Components.JoinGroupAlert;
import org.telegram.p005ui.Components.LayoutHelper;
import org.telegram.p005ui.Components.ProxyDrawable;
import org.telegram.p005ui.Components.RadialProgressView;
import org.telegram.p005ui.Components.RecyclerListView;
import org.telegram.p005ui.Components.RecyclerListView.Holder;
import org.telegram.p005ui.Components.RecyclerListView.OnItemLongClickListenerExtended;
import org.telegram.p005ui.Components.SizeNotifierFrameLayout;
import org.telegram.p005ui.Components.StickersAlert;
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
import org.telegram.tgnet.TLRPC.User;

/* renamed from: org.telegram.ui.DialogsActivity */
public class DialogsActivity extends BaseFragment implements NotificationCenterDelegate {
    public static boolean[] dialogsLoaded = new boolean[3];
    private String addToGroupAlertString;
    private boolean allowSwitchAccount;
    private AnimatedArrowDrawable arrowDrawable;
    private boolean askAboutContacts = true;
    private boolean cantSendToChannels;
    private boolean checkPermission = true;
    private ChatActivityEnterView commentView;
    private int currentConnectionState;
    private int currentUnreadCount;
    private DialogsActivityDelegate delegate;
    private DialogsAdapter dialogsAdapter;
    private DialogsSearchAdapter dialogsSearchAdapter;
    private int dialogsType;
    private ImageView floatingButton;
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
    private EmptyTextProgressView searchEmptyView;
    private String searchString;
    private boolean searchWas;
    private boolean searching;
    private String selectAlertString;
    private String selectAlertStringGroup;
    private long selectedDialog;
    private RecyclerView sideMenu;
    private ActionBarMenuItem switchItem;
    private ImageView unreadFloatingButton;
    private FrameLayout unreadFloatingButtonContainer;
    private TextView unreadFloatingButtonCounter;

    /* renamed from: org.telegram.ui.DialogsActivity$DialogsActivityDelegate */
    public interface DialogsActivityDelegate {
        void didSelectDialogs(DialogsActivity dialogsActivity, ArrayList<Long> arrayList, CharSequence charSequence, boolean z);
    }

    /* renamed from: org.telegram.ui.DialogsActivity$10 */
    class C167510 implements DialogsSearchAdapterDelegate {
        C167510() {
        }

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
                if (DialogsActivity.this.actionBar != null) {
                    DialogsActivity.this.actionBar.closeSearchField();
                }
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
                DialogsActivity.this.actionBar.closeSearchField();
            } else {
                DialogsActivity.this.didSelectResult(did, true, false);
            }
        }

        public void needRemoveHint(int did) {
            if (DialogsActivity.this.getParentActivity() != null && MessagesController.getInstance(DialogsActivity.this.currentAccount).getUser(Integer.valueOf(did)) != null) {
                Builder builder = new Builder(DialogsActivity.this.getParentActivity());
                builder.setTitle(LocaleController.getString("AppName", C0541R.string.AppName));
                builder.setMessage(LocaleController.formatString("ChatHintsDelete", C0541R.string.ChatHintsDelete, ContactsController.formatName(user.first_name, user.last_name)));
                builder.setPositiveButton(LocaleController.getString("OK", C0541R.string.OK), new DialogsActivity$10$$Lambda$0(this, did));
                builder.setNegativeButton(LocaleController.getString("Cancel", C0541R.string.Cancel), null);
                DialogsActivity.this.showDialog(builder.create());
            }
        }

        final /* synthetic */ void lambda$needRemoveHint$0$DialogsActivity$10(int did, DialogInterface dialogInterface, int i) {
            DataQuery.getInstance(DialogsActivity.this.currentAccount).removePeer(did);
        }
    }

    /* renamed from: org.telegram.ui.DialogsActivity$11 */
    class C167611 implements ChatActivityEnterViewDelegate {
        C167611() {
        }

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
    }

    /* renamed from: org.telegram.ui.DialogsActivity$12 */
    class C167712 extends AnimatorListenerAdapter {
        C167712() {
        }

        public void onAnimationEnd(Animator animation) {
            DialogsActivity.this.unreadFloatingButtonContainer.setVisibility(4);
        }
    }

    /* renamed from: org.telegram.ui.DialogsActivity$13 */
    class C167813 extends AnimatorListenerAdapter {
        C167813() {
        }

        public void onAnimationEnd(Animator animation) {
            DialogsActivity.this.commentView.setVisibility(8);
        }
    }

    /* renamed from: org.telegram.ui.DialogsActivity$14 */
    class C167914 extends AnimatorListenerAdapter {
        C167914() {
        }

        public void onAnimationEnd(Animator animation) {
            DialogsActivity.this.commentView.setTag(Integer.valueOf(2));
        }
    }

    /* renamed from: org.telegram.ui.DialogsActivity$15 */
    class C168015 implements OnGlobalLayoutListener {
        C168015() {
        }

        public void onGlobalLayout() {
            float f = 0.0f;
            DialogsActivity.this.floatingButton.setTranslationY(DialogsActivity.this.floatingHidden ? (float) AndroidUtilities.m10dp(100.0f) : 0.0f);
            FrameLayout access$1000 = DialogsActivity.this.unreadFloatingButtonContainer;
            if (DialogsActivity.this.floatingHidden) {
                f = (float) AndroidUtilities.m10dp(74.0f);
            }
            access$1000.setTranslationY(f);
            DialogsActivity.this.floatingButton.setClickable(!DialogsActivity.this.floatingHidden);
            if (DialogsActivity.this.floatingButton != null) {
                DialogsActivity.this.floatingButton.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        }
    }

    /* renamed from: org.telegram.ui.DialogsActivity$1 */
    class C16811 extends ActionBarMenuItemSearchListener {
        C16811() {
        }

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
                    DialogsActivity.this.floatingButton.setVisibility(8);
                    DialogsActivity.this.unreadFloatingButtonContainer.setVisibility(8);
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
            DialogsActivity.this.finishFragment();
            return false;
        }

        public void onSearchCollapse() {
            DialogsActivity.this.searching = false;
            DialogsActivity.this.searchWas = false;
            if (DialogsActivity.this.listView != null) {
                DialogsActivity.this.listView.setEmptyView(DialogsActivity.this.progressView);
                DialogsActivity.this.searchEmptyView.setVisibility(8);
                if (!DialogsActivity.this.onlySelect) {
                    DialogsActivity.this.floatingButton.setVisibility(0);
                    if (DialogsActivity.this.currentUnreadCount != 0) {
                        DialogsActivity.this.unreadFloatingButtonContainer.setVisibility(0);
                        DialogsActivity.this.unreadFloatingButtonContainer.setTranslationY((float) AndroidUtilities.m10dp(74.0f));
                    }
                    DialogsActivity.this.floatingHidden = true;
                    DialogsActivity.this.floatingButton.setTranslationY((float) AndroidUtilities.m10dp(100.0f));
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
    }

    /* renamed from: org.telegram.ui.DialogsActivity$2 */
    class C16822 extends ActionBarMenuOnItemClick {
        C16822() {
        }

        public void onItemClick(int id) {
            boolean z = true;
            if (id == -1) {
                if (DialogsActivity.this.onlySelect) {
                    DialogsActivity.this.finishFragment();
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
    }

    /* renamed from: org.telegram.ui.DialogsActivity$5 */
    class C16855 implements OnItemLongClickListenerExtended {
        C16855() {
        }

        /* JADX WARNING: Removed duplicated region for block: B:164:0x04eb  */
        /* JADX WARNING: Removed duplicated region for block: B:145:0x0474  */
        /* JADX WARNING: Removed duplicated region for block: B:165:0x04f6  */
        /* JADX WARNING: Removed duplicated region for block: B:148:0x0483  */
        /* JADX WARNING: Removed duplicated region for block: B:168:0x050e  */
        /* JADX WARNING: Removed duplicated region for block: B:151:0x0499  */
        /* JADX WARNING: Removed duplicated region for block: B:169:0x0512  */
        /* JADX WARNING: Removed duplicated region for block: B:154:0x04a7  */
        /* JADX WARNING: Removed duplicated region for block: B:170:0x0516  */
        /* JADX WARNING: Removed duplicated region for block: B:157:0x04af  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
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
                    DialogsActivity.this.selectedDialog = dialog.f165id;
                    boolean pinned = dialog.pinned;
                    BottomSheet.Builder builder = new BottomSheet.Builder(DialogsActivity.this.getParentActivity());
                    int lower_id = (int) DialogsActivity.this.selectedDialog;
                    high_id = (int) (DialogsActivity.this.selectedDialog >> 32);
                    boolean hasUnread = dialog.unread_count != 0 || dialog.unread_mark;
                    String string;
                    if (DialogObject.isChannel(dialog)) {
                        CharSequence[] items;
                        chat = MessagesController.getInstance(DialogsActivity.this.currentAccount).getChat(Integer.valueOf(-lower_id));
                        int[] icons = new int[4];
                        icons[0] = dialog.pinned ? C0541R.drawable.chats_unpin : C0541R.drawable.chats_pin;
                        icons[1] = C0541R.drawable.chats_clear;
                        icons[2] = hasUnread ? C0541R.drawable.menu_read : C0541R.drawable.menu_unread;
                        icons[3] = C0541R.drawable.chats_leave;
                        if (MessagesController.getInstance(DialogsActivity.this.currentAccount).isProxyDialog(dialog.f165id)) {
                            items = new CharSequence[4];
                            items[0] = null;
                            items[1] = LocaleController.getString("ClearHistoryCache", C0541R.string.ClearHistoryCache);
                            items[2] = hasUnread ? LocaleController.getString("MarkAsRead", C0541R.string.MarkAsRead) : LocaleController.getString("MarkAsUnread", C0541R.string.MarkAsUnread);
                            items[3] = null;
                        } else if (chat == null || !chat.megagroup) {
                            items = new CharSequence[4];
                            string = (dialog.pinned || MessagesController.getInstance(DialogsActivity.this.currentAccount).canPinDialog(false)) ? dialog.pinned ? LocaleController.getString("UnpinFromTop", C0541R.string.UnpinFromTop) : LocaleController.getString("PinToTop", C0541R.string.PinToTop) : null;
                            items[0] = string;
                            items[1] = LocaleController.getString("ClearHistoryCache", C0541R.string.ClearHistoryCache);
                            if (hasUnread) {
                                string = LocaleController.getString("MarkAsRead", C0541R.string.MarkAsRead);
                            } else {
                                string = LocaleController.getString("MarkAsUnread", C0541R.string.MarkAsUnread);
                            }
                            items[2] = string;
                            items[3] = LocaleController.getString("LeaveChannelMenu", C0541R.string.LeaveChannelMenu);
                        } else {
                            items = new CharSequence[4];
                            string = (dialog.pinned || MessagesController.getInstance(DialogsActivity.this.currentAccount).canPinDialog(false)) ? dialog.pinned ? LocaleController.getString("UnpinFromTop", C0541R.string.UnpinFromTop) : LocaleController.getString("PinToTop", C0541R.string.PinToTop) : null;
                            items[0] = string;
                            items[1] = TextUtils.isEmpty(chat.username) ? LocaleController.getString("ClearHistory", C0541R.string.ClearHistory) : LocaleController.getString("ClearHistoryCache", C0541R.string.ClearHistoryCache);
                            items[2] = hasUnread ? LocaleController.getString("MarkAsRead", C0541R.string.MarkAsRead) : LocaleController.getString("MarkAsUnread", C0541R.string.MarkAsUnread);
                            items[3] = LocaleController.getString("LeaveMegaMenu", C0541R.string.LeaveMegaMenu);
                        }
                        builder.setItems(items, icons, new DialogsActivity$5$$Lambda$1(this, pinned, hasUnread, dialog, chat));
                        DialogsActivity.this.showDialog(builder.create());
                    } else {
                        int[] iArr;
                        boolean isChat = lower_id < 0 && high_id != 1;
                        User user = null;
                        if (!(isChat || lower_id <= 0 || high_id == 1)) {
                            user = MessagesController.getInstance(DialogsActivity.this.currentAccount).getUser(Integer.valueOf(lower_id));
                        }
                        boolean isBot = user != null && user.bot;
                        CharSequence[] charSequenceArr = new CharSequence[4];
                        if (!dialog.pinned) {
                            if (!MessagesController.getInstance(DialogsActivity.this.currentAccount).canPinDialog(lower_id == 0)) {
                                string = null;
                                charSequenceArr[0] = string;
                                charSequenceArr[1] = LocaleController.getString("ClearHistory", C0541R.string.ClearHistory);
                                charSequenceArr[2] = hasUnread ? LocaleController.getString("MarkAsRead", C0541R.string.MarkAsRead) : LocaleController.getString("MarkAsUnread", C0541R.string.MarkAsUnread);
                                string = isChat ? LocaleController.getString("DeleteChat", C0541R.string.DeleteChat) : isBot ? LocaleController.getString("DeleteAndStop", C0541R.string.DeleteAndStop) : LocaleController.getString("Delete", C0541R.string.Delete);
                                charSequenceArr[3] = string;
                                iArr = new int[4];
                                iArr[0] = dialog.pinned ? C0541R.drawable.chats_unpin : C0541R.drawable.chats_pin;
                                iArr[1] = C0541R.drawable.chats_clear;
                                iArr[2] = hasUnread ? C0541R.drawable.menu_read : C0541R.drawable.menu_unread;
                                iArr[3] = isChat ? C0541R.drawable.chats_leave : C0541R.drawable.chats_delete;
                                builder.setItems(charSequenceArr, iArr, new DialogsActivity$5$$Lambda$2(this, pinned, hasUnread, dialog, isChat, isBot));
                                DialogsActivity.this.showDialog(builder.create());
                            }
                        }
                        string = dialog.pinned ? LocaleController.getString("UnpinFromTop", C0541R.string.UnpinFromTop) : LocaleController.getString("PinToTop", C0541R.string.PinToTop);
                        charSequenceArr[0] = string;
                        charSequenceArr[1] = LocaleController.getString("ClearHistory", C0541R.string.ClearHistory);
                        if (hasUnread) {
                        }
                        charSequenceArr[2] = hasUnread ? LocaleController.getString("MarkAsRead", C0541R.string.MarkAsRead) : LocaleController.getString("MarkAsUnread", C0541R.string.MarkAsUnread);
                        if (isChat) {
                        }
                        charSequenceArr[3] = string;
                        iArr = new int[4];
                        if (dialog.pinned) {
                        }
                        iArr[0] = dialog.pinned ? C0541R.drawable.chats_unpin : C0541R.drawable.chats_pin;
                        iArr[1] = C0541R.drawable.chats_clear;
                        if (hasUnread) {
                        }
                        iArr[2] = hasUnread ? C0541R.drawable.menu_read : C0541R.drawable.menu_unread;
                        if (isChat) {
                        }
                        iArr[3] = isChat ? C0541R.drawable.chats_leave : C0541R.drawable.chats_delete;
                        builder.setItems(charSequenceArr, iArr, new DialogsActivity$5$$Lambda$2(this, pinned, hasUnread, dialog, isChat, isBot));
                        DialogsActivity.this.showDialog(builder.create());
                    }
                } else if (DialogsActivity.this.dialogsType != 3 || DialogsActivity.this.selectAlertString != null) {
                    return false;
                } else {
                    DialogsActivity.this.dialogsAdapter.addOrRemoveSelectedDialog(dialog.f165id, view);
                    DialogsActivity.this.updateSelectedCount();
                }
                return true;
            } else if (!(DialogsActivity.this.dialogsSearchAdapter.getItem(position) instanceof String) && !DialogsActivity.this.dialogsSearchAdapter.isRecentSearchDisplayed()) {
                return false;
            } else {
                Builder builder2 = new Builder(DialogsActivity.this.getParentActivity());
                builder2.setTitle(LocaleController.getString("AppName", C0541R.string.AppName));
                builder2.setMessage(LocaleController.getString("ClearSearch", C0541R.string.ClearSearch));
                builder2.setPositiveButton(LocaleController.getString("ClearButton", C0541R.string.ClearButton).toUpperCase(), new DialogsActivity$5$$Lambda$0(this));
                builder2.setNegativeButton(LocaleController.getString("Cancel", C0541R.string.Cancel), null);
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

        final /* synthetic */ void lambda$onItemClick$3$DialogsActivity$5(boolean pinned, boolean hasUnread, TL_dialog dialog, Chat chat, DialogInterface d, int which) {
            if (which == 0) {
                if (MessagesController.getInstance(DialogsActivity.this.currentAccount).pinDialog(DialogsActivity.this.selectedDialog, !pinned, null, 0) && !pinned) {
                    DialogsActivity.this.hideFloatingButton(false);
                    DialogsActivity.this.listView.smoothScrollToPosition(0);
                }
            } else if (which != 2) {
                Builder builder1 = new Builder(DialogsActivity.this.getParentActivity());
                builder1.setTitle(LocaleController.getString("AppName", C0541R.string.AppName));
                if (which == 1) {
                    if (chat == null || !chat.megagroup) {
                        builder1.setMessage(LocaleController.getString("AreYouSureClearHistoryChannel", C0541R.string.AreYouSureClearHistoryChannel));
                    } else if (TextUtils.isEmpty(chat.username)) {
                        builder1.setMessage(LocaleController.getString("AreYouSureClearHistory", C0541R.string.AreYouSureClearHistory));
                    } else {
                        builder1.setMessage(LocaleController.getString("AreYouSureClearHistoryGroup", C0541R.string.AreYouSureClearHistoryGroup));
                    }
                    builder1.setPositiveButton(LocaleController.getString("OK", C0541R.string.OK), new DialogsActivity$5$$Lambda$4(this, chat));
                } else {
                    if (chat == null || !chat.megagroup) {
                        builder1.setMessage(LocaleController.getString("ChannelLeaveAlert", C0541R.string.ChannelLeaveAlert));
                    } else {
                        builder1.setMessage(LocaleController.getString("MegaLeaveAlert", C0541R.string.MegaLeaveAlert));
                    }
                    builder1.setPositiveButton(LocaleController.getString("OK", C0541R.string.OK), new DialogsActivity$5$$Lambda$5(this));
                }
                builder1.setNegativeButton(LocaleController.getString("Cancel", C0541R.string.Cancel), null);
                DialogsActivity.this.showDialog(builder1.create());
            } else if (hasUnread) {
                MessagesController.getInstance(DialogsActivity.this.currentAccount).markMentionsAsRead(DialogsActivity.this.selectedDialog);
                MessagesController.getInstance(DialogsActivity.this.currentAccount).markDialogAsRead(DialogsActivity.this.selectedDialog, dialog.top_message, dialog.top_message, dialog.last_message_date, false, 0, true);
            } else {
                MessagesController.getInstance(DialogsActivity.this.currentAccount).markDialogAsUnread(DialogsActivity.this.selectedDialog, null, 0);
            }
        }

        final /* synthetic */ void lambda$null$1$DialogsActivity$5(Chat chat, DialogInterface dialogInterface, int i) {
            if (chat != null && chat.megagroup && TextUtils.isEmpty(chat.username)) {
                MessagesController.getInstance(DialogsActivity.this.currentAccount).deleteDialog(DialogsActivity.this.selectedDialog, 1);
            } else {
                MessagesController.getInstance(DialogsActivity.this.currentAccount).deleteDialog(DialogsActivity.this.selectedDialog, 2);
            }
        }

        final /* synthetic */ void lambda$null$2$DialogsActivity$5(DialogInterface dialogInterface, int i) {
            MessagesController.getInstance(DialogsActivity.this.currentAccount).deleteUserFromChat((int) (-DialogsActivity.this.selectedDialog), UserConfig.getInstance(DialogsActivity.this.currentAccount).getCurrentUser(), null);
            if (AndroidUtilities.isTablet()) {
                NotificationCenter.getInstance(DialogsActivity.this.currentAccount).postNotificationName(NotificationCenter.closeChats, Long.valueOf(DialogsActivity.this.selectedDialog));
            }
        }

        final /* synthetic */ void lambda$onItemClick$5$DialogsActivity$5(boolean pinned, boolean hasUnread, TL_dialog dialog, boolean isChat, boolean isBot, DialogInterface d, int which) {
            if (which == 0) {
                if (MessagesController.getInstance(DialogsActivity.this.currentAccount).pinDialog(DialogsActivity.this.selectedDialog, !pinned, null, 0) && !pinned) {
                    DialogsActivity.this.hideFloatingButton(false);
                    DialogsActivity.this.listView.smoothScrollToPosition(0);
                }
            } else if (which != 2) {
                Builder builder12 = new Builder(DialogsActivity.this.getParentActivity());
                builder12.setTitle(LocaleController.getString("AppName", C0541R.string.AppName));
                if (which == 1) {
                    builder12.setMessage(LocaleController.getString("AreYouSureClearHistory", C0541R.string.AreYouSureClearHistory));
                } else if (isChat) {
                    builder12.setMessage(LocaleController.getString("AreYouSureDeleteAndExit", C0541R.string.AreYouSureDeleteAndExit));
                } else {
                    builder12.setMessage(LocaleController.getString("AreYouSureDeleteThisChat", C0541R.string.AreYouSureDeleteThisChat));
                }
                builder12.setPositiveButton(LocaleController.getString("OK", C0541R.string.OK), new DialogsActivity$5$$Lambda$3(this, which, isChat, isBot));
                builder12.setNegativeButton(LocaleController.getString("Cancel", C0541R.string.Cancel), null);
                DialogsActivity.this.showDialog(builder12.create());
            } else if (hasUnread) {
                MessagesController.getInstance(DialogsActivity.this.currentAccount).markMentionsAsRead(DialogsActivity.this.selectedDialog);
                MessagesController.getInstance(DialogsActivity.this.currentAccount).markDialogAsRead(DialogsActivity.this.selectedDialog, dialog.top_message, dialog.top_message, dialog.last_message_date, false, 0, true);
            } else {
                MessagesController.getInstance(DialogsActivity.this.currentAccount).markDialogAsUnread(DialogsActivity.this.selectedDialog, null, 0);
            }
        }

        final /* synthetic */ void lambda$null$4$DialogsActivity$5(int which, boolean isChat, boolean isBot, DialogInterface dialogInterface, int i) {
            if (which != 1) {
                if (isChat) {
                    Chat currentChat = MessagesController.getInstance(DialogsActivity.this.currentAccount).getChat(Integer.valueOf((int) (-DialogsActivity.this.selectedDialog)));
                    if (currentChat == null || !ChatObject.isNotInChat(currentChat)) {
                        MessagesController.getInstance(DialogsActivity.this.currentAccount).deleteUserFromChat((int) (-DialogsActivity.this.selectedDialog), MessagesController.getInstance(DialogsActivity.this.currentAccount).getUser(Integer.valueOf(UserConfig.getInstance(DialogsActivity.this.currentAccount).getClientUserId())), null);
                    } else {
                        MessagesController.getInstance(DialogsActivity.this.currentAccount).deleteDialog(DialogsActivity.this.selectedDialog, 0);
                    }
                } else {
                    MessagesController.getInstance(DialogsActivity.this.currentAccount).deleteDialog(DialogsActivity.this.selectedDialog, 0);
                }
                if (isBot) {
                    MessagesController.getInstance(DialogsActivity.this.currentAccount).blockUser((int) DialogsActivity.this.selectedDialog);
                }
                if (AndroidUtilities.isTablet()) {
                    NotificationCenter.getInstance(DialogsActivity.this.currentAccount).postNotificationName(NotificationCenter.closeChats, Long.valueOf(DialogsActivity.this.selectedDialog));
                    return;
                }
                return;
            }
            MessagesController.getInstance(DialogsActivity.this.currentAccount).deleteDialog(DialogsActivity.this.selectedDialog, 1);
        }

        public void onLongClickRelease() {
            DialogsActivity.this.finishPreviewFragment();
        }

        public void onMove(float dx, float dy) {
            DialogsActivity.this.movePreviewFragment(dy);
        }
    }

    /* renamed from: org.telegram.ui.DialogsActivity$6 */
    class C16866 extends ViewOutlineProvider {
        C16866() {
        }

        @SuppressLint({"NewApi"})
        public void getOutline(View view, Outline outline) {
            outline.setOval(0, 0, AndroidUtilities.m10dp(56.0f), AndroidUtilities.m10dp(56.0f));
        }
    }

    /* renamed from: org.telegram.ui.DialogsActivity$7 */
    class C16877 extends ViewOutlineProvider {
        C16877() {
        }

        @SuppressLint({"NewApi"})
        public void getOutline(View view, Outline outline) {
            outline.setOval(0, 0, AndroidUtilities.m10dp(56.0f), AndroidUtilities.m10dp(56.0f));
        }
    }

    /* renamed from: org.telegram.ui.DialogsActivity$8 */
    class C16888 extends ViewOutlineProvider {
        C16888() {
        }

        @SuppressLint({"NewApi"})
        public void getOutline(View view, Outline outline) {
            outline.setEmpty();
        }
    }

    /* renamed from: org.telegram.ui.DialogsActivity$9 */
    class C16899 extends OnScrollListener {
        private boolean scrollingManually;

        C16899() {
        }

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
                DialogsActivity.this.checkUnreadButton(true);
                if (DialogsActivity.this.floatingButton.getVisibility() != 8) {
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
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiDidLoaded);
            if (!this.onlySelect) {
                NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.closeSearchByActiveAction);
                NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.proxySettingsChanged);
            }
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.updateInterfaces);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.encryptedChatUpdated);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.contactsDidLoaded);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.appDidLogout);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.openedChatChanged);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.notificationsSettingsUpdated);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messageReceivedByAck);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messageReceivedByServer);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messageSendError);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.needReloadRecentDialogsSearch);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.didLoadedReplyMessages);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.reloadHints);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.didUpdatedConnectionState);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.dialogsUnreadCounterChanged);
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
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiDidLoaded);
            if (!this.onlySelect) {
                NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.closeSearchByActiveAction);
                NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.proxySettingsChanged);
            }
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.updateInterfaces);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.encryptedChatUpdated);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.contactsDidLoaded);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.appDidLogout);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.openedChatChanged);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.notificationsSettingsUpdated);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messageReceivedByAck);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messageReceivedByServer);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messageSendError);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.needReloadRecentDialogsSearch);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.didLoadedReplyMessages);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.reloadHints);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.didUpdatedConnectionState);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.dialogsUnreadCounterChanged);
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didSetPasscode);
        }
        if (this.commentView != null) {
            this.commentView.onDestroy();
        }
        this.delegate = null;
    }

    public View createView(Context context) {
        View backupImageView;
        Drawable shadowDrawable;
        Drawable combinedDrawable;
        StateListAnimator animator;
        float f;
        int i;
        float f2;
        float f3;
        int i2;
        this.searching = false;
        this.searchWas = false;
        AndroidUtilities.runOnUIThread(new DialogsActivity$$Lambda$0(context));
        ActionBarMenu menu = this.actionBar.createMenu();
        if (!this.onlySelect && this.searchString == null) {
            this.proxyDrawable = new ProxyDrawable(context);
            this.proxyItem = menu.addItem(2, this.proxyDrawable);
            this.passcodeItem = menu.addItem(1, (int) C0541R.drawable.lock_close);
            updatePasscodeButton();
            updateProxyButton(false);
        }
        menu.addItem(0, (int) C0541R.drawable.ic_ab_search).setIsSearchField(true).setActionBarMenuItemSearchListener(new C16811()).getSearchField().setHint(LocaleController.getString("Search", C0541R.string.Search));
        if (this.onlySelect) {
            this.actionBar.setBackButtonImage(C0541R.drawable.ic_ab_back);
            if (this.dialogsType == 3 && this.selectAlertString == null) {
                this.actionBar.setTitle(LocaleController.getString("ForwardTo", C0541R.string.ForwardTo));
            } else {
                this.actionBar.setTitle(LocaleController.getString("SelectChat", C0541R.string.SelectChat));
            }
        } else {
            if (this.searchString != null) {
                this.actionBar.setBackButtonImage(C0541R.drawable.ic_ab_back);
            } else {
                this.actionBar.setBackButtonDrawable(new MenuDrawable());
            }
            if (BuildVars.DEBUG_VERSION) {
                this.actionBar.setTitle("Telegram Beta");
            } else {
                this.actionBar.setTitle(LocaleController.getString("AppName", C0541R.string.AppName));
            }
            this.actionBar.setSupportsHolidayImage(true);
        }
        this.actionBar.setTitleActionRunnable(new DialogsActivity$$Lambda$1(this));
        if (this.allowSwitchAccount && UserConfig.getActivatedAccountsCount() > 1) {
            TLObject avatar;
            this.switchItem = menu.addItemWithWidth(1, 0, AndroidUtilities.m10dp(56.0f));
            Drawable avatarDrawable = new AvatarDrawable();
            avatarDrawable.setTextSize(AndroidUtilities.m10dp(12.0f));
            backupImageView = new BackupImageView(context);
            backupImageView.setRoundRadius(AndroidUtilities.m10dp(18.0f));
            this.switchItem.addView(backupImageView, LayoutHelper.createFrame(36, 36, 17));
            User user = UserConfig.getInstance(this.currentAccount).getCurrentUser();
            avatarDrawable.setInfo(user);
            if (user.photo == null || user.photo.photo_small == null || user.photo.photo_small.volume_id == 0 || user.photo.photo_small.local_id == 0) {
                avatar = null;
            } else {
                avatar = user.photo.photo_small;
            }
            backupImageView.getImageReceiver().setCurrentAccount(this.currentAccount);
            backupImageView.setImage(avatar, "50_50", avatarDrawable);
            for (int a = 0; a < 3; a++) {
                if (UserConfig.getInstance(a).getCurrentUser() != null) {
                    AccountSelectCell cell = new AccountSelectCell(context);
                    cell.setAccount(a, true);
                    this.switchItem.addSubItem(a + 10, cell, AndroidUtilities.m10dp(230.0f), AndroidUtilities.m10dp(48.0f));
                }
            }
        }
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setActionBarMenuOnItemClick(new C16822());
        if (this.sideMenu != null) {
            this.sideMenu.setBackgroundColor(Theme.getColor(Theme.key_chats_menuBackground));
            this.sideMenu.setGlowColor(Theme.getColor(Theme.key_chats_menuBackground));
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
                        if (keyboardSize <= AndroidUtilities.m10dp(20.0f) && !AndroidUtilities.isInMultiwindow) {
                            heightSize -= DialogsActivity.this.commentView.getEmojiPadding();
                        }
                        this.inputFieldHeight = DialogsActivity.this.commentView.getMeasuredHeight();
                    }
                }
                for (int i = 0; i < childCount; i++) {
                    View child = getChildAt(i);
                    if (!(child == null || child.getVisibility() == 8 || child == DialogsActivity.this.commentView || child == DialogsActivity.this.actionBar)) {
                        if (child == DialogsActivity.this.listView || child == DialogsActivity.this.progressView || child == DialogsActivity.this.searchEmptyView) {
                            child.measure(MeasureSpec.makeMeasureSpec(widthSize, NUM), MeasureSpec.makeMeasureSpec(Math.max(AndroidUtilities.m10dp(10.0f), (heightSize - this.inputFieldHeight) + AndroidUtilities.m10dp(2.0f)), NUM));
                        } else if (DialogsActivity.this.commentView == null || !DialogsActivity.this.commentView.isPopupView(child)) {
                            measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);
                        } else if (!AndroidUtilities.isInMultiwindow) {
                            child.measure(MeasureSpec.makeMeasureSpec(widthSize, NUM), MeasureSpec.makeMeasureSpec(child.getLayoutParams().height, NUM));
                        } else if (AndroidUtilities.isTablet()) {
                            child.measure(MeasureSpec.makeMeasureSpec(widthSize, NUM), MeasureSpec.makeMeasureSpec(Math.min(AndroidUtilities.m10dp(320.0f), ((heightSize - this.inputFieldHeight) - AndroidUtilities.statusBarHeight) + getPaddingTop()), NUM));
                        } else {
                            child.measure(MeasureSpec.makeMeasureSpec(widthSize, NUM), MeasureSpec.makeMeasureSpec(((heightSize - this.inputFieldHeight) - AndroidUtilities.statusBarHeight) + getPaddingTop(), NUM));
                        }
                    }
                }
            }

            protected void onLayout(boolean changed, int l, int t, int r, int b) {
                int count = getChildCount();
                Object tag = DialogsActivity.this.commentView != null ? DialogsActivity.this.commentView.getTag() : null;
                int paddingBottom = (tag == null || !tag.equals(Integer.valueOf(2))) ? 0 : (getKeyboardHeight() > AndroidUtilities.m10dp(20.0f) || AndroidUtilities.isInMultiwindow) ? 0 : DialogsActivity.this.commentView.getEmojiPadding();
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
                                childTop = (DialogsActivity.this.commentView.getTop() - child.getMeasuredHeight()) + AndroidUtilities.m10dp(1.0f);
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
        this.listView.setOnItemLongClickListener(new C16855());
        this.searchEmptyView = new EmptyTextProgressView(context);
        this.searchEmptyView.setVisibility(8);
        this.searchEmptyView.setShowAtCenter(true);
        this.searchEmptyView.setText(LocaleController.getString("NoResult", C0541R.string.NoResult));
        backupImageView.addView(this.searchEmptyView, LayoutHelper.createFrame(-1, -1.0f));
        this.progressView = new RadialProgressView(context);
        this.progressView.setVisibility(8);
        backupImageView.addView(this.progressView, LayoutHelper.createFrame(-2, -2, 17));
        this.floatingButton = new ImageView(context);
        this.floatingButton.setVisibility(this.onlySelect ? 8 : 0);
        this.floatingButton.setScaleType(ScaleType.CENTER);
        Drawable drawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.m10dp(56.0f), Theme.getColor(Theme.key_chats_actionBackground), Theme.getColor(Theme.key_chats_actionPressedBackground));
        if (VERSION.SDK_INT < 21) {
            shadowDrawable = context.getResources().getDrawable(C0541R.drawable.floating_shadow).mutate();
            shadowDrawable.setColorFilter(new PorterDuffColorFilter(Theme.ACTION_BAR_VIDEO_EDIT_COLOR, Mode.MULTIPLY));
            combinedDrawable = new CombinedDrawable(shadowDrawable, drawable, 0, 0);
            combinedDrawable.setIconSize(AndroidUtilities.m10dp(56.0f), AndroidUtilities.m10dp(56.0f));
            drawable = combinedDrawable;
        }
        this.floatingButton.setBackgroundDrawable(drawable);
        this.floatingButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chats_actionIcon), Mode.MULTIPLY));
        this.floatingButton.setImageResource(C0541R.drawable.floating_pencil);
        if (VERSION.SDK_INT >= 21) {
            animator = new StateListAnimator();
            animator.addState(new int[]{16842919}, ObjectAnimator.ofFloat(this.floatingButton, "translationZ", new float[]{(float) AndroidUtilities.m10dp(2.0f), (float) AndroidUtilities.m10dp(4.0f)}).setDuration(200));
            animator.addState(new int[0], ObjectAnimator.ofFloat(this.floatingButton, "translationZ", new float[]{(float) AndroidUtilities.m10dp(4.0f), (float) AndroidUtilities.m10dp(2.0f)}).setDuration(200));
            this.floatingButton.setStateListAnimator(animator);
            this.floatingButton.setOutlineProvider(new C16866());
        }
        View view = this.floatingButton;
        int i3 = VERSION.SDK_INT >= 21 ? 56 : 60;
        if (VERSION.SDK_INT >= 21) {
            f = 56.0f;
        } else {
            f = 60.0f;
        }
        if (LocaleController.isRTL) {
            i = 3;
        } else {
            i = 5;
        }
        i |= 80;
        if (LocaleController.isRTL) {
            f2 = 14.0f;
        } else {
            f2 = 0.0f;
        }
        if (LocaleController.isRTL) {
            f3 = 0.0f;
        } else {
            f3 = 14.0f;
        }
        backupImageView.addView(view, LayoutHelper.createFrame(i3, f, i, f2, 0.0f, f3, 14.0f));
        this.floatingButton.setOnClickListener(new DialogsActivity$$Lambda$3(this));
        this.unreadFloatingButtonContainer = new FrameLayout(context);
        if (this.onlySelect) {
            this.unreadFloatingButtonContainer.setVisibility(8);
        } else {
            this.unreadFloatingButtonContainer.setVisibility(this.currentUnreadCount != 0 ? 0 : 4);
            this.unreadFloatingButtonContainer.setTag(this.currentUnreadCount != 0 ? Integer.valueOf(1) : null);
        }
        view = this.unreadFloatingButtonContainer;
        i3 = (VERSION.SDK_INT >= 21 ? 56 : 60) + 20;
        if (VERSION.SDK_INT >= 21) {
            i2 = 56;
        } else {
            i2 = 60;
        }
        f = (float) (i2 + 20);
        if (LocaleController.isRTL) {
            i = 3;
        } else {
            i = 5;
        }
        i |= 80;
        if (LocaleController.isRTL) {
            f2 = 4.0f;
        } else {
            f2 = 0.0f;
        }
        if (LocaleController.isRTL) {
            f3 = 0.0f;
        } else {
            f3 = 4.0f;
        }
        backupImageView.addView(view, LayoutHelper.createFrame(i3, f, i, f2, 0.0f, f3, 81.0f));
        this.unreadFloatingButtonContainer.setOnClickListener(new DialogsActivity$$Lambda$4(this));
        this.unreadFloatingButton = new ImageView(context);
        this.unreadFloatingButton.setScaleType(ScaleType.CENTER);
        drawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.m10dp(56.0f), Theme.getColor(Theme.key_chats_actionUnreadBackground), Theme.getColor(Theme.key_chats_actionUnreadPressedBackground));
        if (VERSION.SDK_INT < 21) {
            shadowDrawable = context.getResources().getDrawable(C0541R.drawable.floating_shadow_profile).mutate();
            shadowDrawable.setColorFilter(new PorterDuffColorFilter(Theme.ACTION_BAR_VIDEO_EDIT_COLOR, Mode.MULTIPLY));
            combinedDrawable = new CombinedDrawable(shadowDrawable, drawable, 0, 0);
            combinedDrawable.setIconSize(AndroidUtilities.m10dp(56.0f), AndroidUtilities.m10dp(56.0f));
            drawable = combinedDrawable;
        }
        this.unreadFloatingButton.setBackgroundDrawable(drawable);
        ImageView imageView = this.unreadFloatingButton;
        Drawable animatedArrowDrawable = new AnimatedArrowDrawable(-1);
        this.arrowDrawable = animatedArrowDrawable;
        imageView.setImageDrawable(animatedArrowDrawable);
        this.unreadFloatingButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chats_actionUnreadIcon), Mode.MULTIPLY));
        this.unreadFloatingButton.setPadding(0, AndroidUtilities.m10dp(4.0f), 0, 0);
        this.arrowDrawable.setAnimationProgress(1.0f);
        if (VERSION.SDK_INT >= 21) {
            animator = new StateListAnimator();
            animator.addState(new int[]{16842919}, ObjectAnimator.ofFloat(this.unreadFloatingButton, "translationZ", new float[]{(float) AndroidUtilities.m10dp(2.0f), (float) AndroidUtilities.m10dp(4.0f)}).setDuration(200));
            animator.addState(new int[0], ObjectAnimator.ofFloat(this.unreadFloatingButton, "translationZ", new float[]{(float) AndroidUtilities.m10dp(4.0f), (float) AndroidUtilities.m10dp(2.0f)}).setDuration(200));
            this.unreadFloatingButton.setStateListAnimator(animator);
            this.unreadFloatingButton.setOutlineProvider(new C16877());
        }
        FrameLayout frameLayout = this.unreadFloatingButtonContainer;
        View view2 = this.unreadFloatingButton;
        i3 = VERSION.SDK_INT >= 21 ? 56 : 60;
        if (VERSION.SDK_INT >= 21) {
            f = 56.0f;
        } else {
            f = 60.0f;
        }
        if (LocaleController.isRTL) {
            i = 3;
        } else {
            i = 5;
        }
        frameLayout.addView(view2, LayoutHelper.createFrame(i3, f, i | 48, 10.0f, 13.0f, 10.0f, 0.0f));
        this.unreadFloatingButtonCounter = new TextView(context);
        this.unreadFloatingButtonCounter.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.unreadFloatingButtonCounter.setTextSize(1, 13.0f);
        if (this.currentUnreadCount > 0) {
            this.unreadFloatingButtonCounter.setText(String.format("%d", new Object[]{Integer.valueOf(this.currentUnreadCount)}));
        }
        if (VERSION.SDK_INT >= 21) {
            this.unreadFloatingButtonCounter.setElevation((float) AndroidUtilities.m10dp(5.0f));
            this.unreadFloatingButtonCounter.setOutlineProvider(new C16888());
        }
        this.unreadFloatingButtonCounter.setTextColor(Theme.getColor(Theme.key_chat_goDownButtonCounter));
        this.unreadFloatingButtonCounter.setGravity(17);
        this.unreadFloatingButtonCounter.setBackgroundDrawable(Theme.createRoundRectDrawable(AndroidUtilities.m10dp(11.5f), Theme.getColor(Theme.key_chat_goDownButtonCounterBackground)));
        this.unreadFloatingButtonCounter.setMinWidth(AndroidUtilities.m10dp(23.0f));
        this.unreadFloatingButtonCounter.setPadding(AndroidUtilities.m10dp(8.0f), 0, AndroidUtilities.m10dp(8.0f), AndroidUtilities.m10dp(1.0f));
        this.unreadFloatingButtonContainer.addView(this.unreadFloatingButtonCounter, LayoutHelper.createFrame(-2, 23, 49));
        this.listView.setOnScrollListener(new C16899());
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
        this.dialogsSearchAdapter.setDelegate(new C167510());
        this.listView.setEmptyView(this.progressView);
        if (this.searchString != null) {
            this.actionBar.openSearchField(this.searchString);
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
            this.commentView.setDelegate(new C167611());
        }
        if (!this.onlySelect) {
            checkUnreadCount(false);
        }
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
                    dialog_id = (long) ((User) object).f228id;
                } else if (object instanceof TL_dialog) {
                    dialog_id = ((TL_dialog) object).f165id;
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
                        dialog_id = (long) (-invite.chat.f113id);
                    } else {
                        return;
                    }
                } else if (object instanceof TL_recentMeUrlStickerSet) {
                    StickerSet stickerSet = ((TL_recentMeUrlStickerSet) object).set.set;
                    TL_inputStickerSetID set = new TL_inputStickerSetID();
                    set.f138id = stickerSet.f146id;
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
                    dialog_id = (long) ((User) obj).f228id;
                    if (!this.onlySelect) {
                        this.dialogsSearchAdapter.putRecentSearch(dialog_id, (User) obj);
                    }
                } else if (obj instanceof Chat) {
                    if (((Chat) obj).f113id > 0) {
                        dialog_id = (long) (-((Chat) obj).f113id);
                    } else {
                        dialog_id = AndroidUtilities.makeBroadcastId(((Chat) obj).f113id);
                    }
                    if (!this.onlySelect) {
                        this.dialogsSearchAdapter.putRecentSearch(dialog_id, (Chat) obj);
                    }
                } else if (obj instanceof EncryptedChat) {
                    dialog_id = ((long) ((EncryptedChat) obj).f123id) << 32;
                    if (!this.onlySelect) {
                        this.dialogsSearchAdapter.putRecentSearch(dialog_id, (EncryptedChat) obj);
                    }
                } else if (obj instanceof MessageObject) {
                    MessageObject messageObject = obj;
                    dialog_id = messageObject.getDialogId();
                    message_id = messageObject.getId();
                    this.dialogsSearchAdapter.addHashtagsFromMessage(this.dialogsSearchAdapter.getLastSearchString());
                } else if (obj instanceof String) {
                    this.actionBar.openSearchField((String) obj);
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
                } else if (!(isGlobalSearch || this.actionBar == null)) {
                    this.actionBar.closeSearchField();
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

    /* JADX WARNING: Removed duplicated region for block: B:52:? A:{SYNTHETIC, RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:34:0x00ad  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    final /* synthetic */ void lambda$createView$4$DialogsActivity(View view) {
        if (this.listView.getAdapter() != this.dialogsAdapter) {
            return;
        }
        ArrayList<TL_dialog> array;
        int a;
        TL_dialog dialog;
        if (this.layoutManager.findFirstVisibleItemPosition() == 0) {
            array = getDialogsArray();
            for (a = array.size() - 1; a >= 0; a--) {
                dialog = (TL_dialog) array.get(a);
                if ((dialog.unread_count != 0 || dialog.unread_mark) && !MessagesController.getInstance(this.currentAccount).isDialogMuted(dialog.f165id)) {
                    this.listView.smoothScrollToPosition(a);
                    return;
                }
            }
            return;
        }
        int middle = this.listView.getMeasuredHeight() / 2;
        boolean found = false;
        int b = 0;
        int count = this.listView.getChildCount();
        while (b < count) {
            View child = this.listView.getChildAt(b);
            if (!(child instanceof DialogCell) || child.getTop() > middle || child.getBottom() < middle) {
                b++;
            } else {
                Holder holder = (Holder) this.listView.findContainingViewHolder(child);
                if (holder != null) {
                    array = getDialogsArray();
                    for (a = Math.min(holder.getAdapterPosition(), array.size()) - 1; a >= 0; a--) {
                        dialog = (TL_dialog) array.get(a);
                        if ((dialog.unread_count != 0 || dialog.unread_mark) && !MessagesController.getInstance(this.currentAccount).isDialogMuted(dialog.f165id)) {
                            found = true;
                            this.listView.smoothScrollToPosition(a);
                            break;
                        }
                    }
                }
                if (found) {
                    hideFloatingButton(false);
                    this.listView.smoothScrollToPosition(0);
                    return;
                }
                return;
            }
        }
        if (found) {
        }
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
                    if (UserConfig.getInstance(this.currentAccount).syncContacts && activity.shouldShowRequestPermissionRationale("android.permission.READ_CONTACTS")) {
                        create = AlertsCreator.createContactsPermissionDialog(activity, new DialogsActivity$$Lambda$5(this)).create();
                        this.permissionDialog = create;
                        showDialog(create);
                    } else if (activity.shouldShowRequestPermissionRationale("android.permission.WRITE_EXTERNAL_STORAGE")) {
                        Builder builder = new Builder(activity);
                        builder.setTitle(LocaleController.getString("AppName", C0541R.string.AppName));
                        builder.setMessage(LocaleController.getString("PermissionStorage", C0541R.string.PermissionStorage));
                        builder.setPositiveButton(LocaleController.getString("OK", C0541R.string.OK), null);
                        create = builder.create();
                        this.permissionDialog = create;
                        showDialog(create);
                    } else {
                        askForPermissons(true);
                    }
                }
            }
        }
    }

    final /* synthetic */ void lambda$onResume$5$DialogsActivity(int param) {
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

    public void onPause() {
        super.onPause();
        if (this.commentView != null) {
            this.commentView.onResume();
        }
    }

    private void checkUnreadCount(boolean animated) {
        if (BuildVars.DEBUG_PRIVATE_VERSION) {
            int newCount = MessagesController.getInstance(this.currentAccount).unreadUnmutedDialogs;
            if (newCount != this.currentUnreadCount) {
                this.currentUnreadCount = newCount;
                if (this.unreadFloatingButtonContainer != null) {
                    if (this.currentUnreadCount > 0) {
                        this.unreadFloatingButtonCounter.setText(String.format("%d", new Object[]{Integer.valueOf(this.currentUnreadCount)}));
                    }
                    checkUnreadButton(animated);
                }
            }
        }
    }

    private void checkUnreadButton(boolean animated) {
        if (!this.onlySelect && this.listView.getAdapter() == this.dialogsAdapter) {
            boolean found = false;
            if (this.currentUnreadCount > 0) {
                int b;
                View child;
                int middle = this.listView.getMeasuredHeight() / 2;
                int firstVisibleItem = this.layoutManager.findFirstVisibleItemPosition();
                int count = this.listView.getChildCount();
                int unreadOnScreen = 0;
                for (b = 0; b < count; b++) {
                    child = this.listView.getChildAt(b);
                    if ((child instanceof DialogCell) && ((DialogCell) child).isUnread()) {
                        unreadOnScreen++;
                    }
                }
                b = 0;
                while (b < count) {
                    child = this.listView.getChildAt(b);
                    if (!(child instanceof DialogCell) || child.getTop() > middle || child.getBottom() < middle) {
                        b++;
                    } else {
                        Holder holder = (Holder) this.listView.findContainingViewHolder(child);
                        if (holder != null) {
                            ArrayList<TL_dialog> array = getDialogsArray();
                            if (firstVisibleItem != 0) {
                                found = true;
                                this.arrowDrawable.setAnimationProgressAnimated(0.0f);
                            } else if (unreadOnScreen != this.currentUnreadCount) {
                                int size = array.size();
                                for (int a = holder.getAdapterPosition() + 1; a < size; a++) {
                                    TL_dialog dialog = (TL_dialog) array.get(a);
                                    if ((dialog.unread_count != 0 || dialog.unread_mark) && !MessagesController.getInstance(this.currentAccount).isDialogMuted(dialog.f165id)) {
                                        this.arrowDrawable.setAnimationProgressAnimated(1.0f);
                                        found = true;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (found) {
                if (this.unreadFloatingButtonContainer.getTag() == null) {
                    this.unreadFloatingButtonContainer.setTag(Integer.valueOf(1));
                    this.unreadFloatingButtonContainer.setVisibility(0);
                    if (animated) {
                        this.unreadFloatingButtonContainer.animate().alpha(1.0f).setDuration(200).setInterpolator(new DecelerateInterpolator()).setListener(null).start();
                    } else {
                        this.unreadFloatingButtonContainer.setAlpha(1.0f);
                    }
                }
            } else if (this.unreadFloatingButtonContainer.getTag() != null) {
                this.unreadFloatingButtonContainer.setTag(null);
                if (animated) {
                    this.unreadFloatingButtonContainer.animate().alpha(0.0f).setDuration(200).setInterpolator(new DecelerateInterpolator()).setListener(new C167712()).start();
                    return;
                }
                this.unreadFloatingButtonContainer.setAlpha(0.0f);
                this.unreadFloatingButtonContainer.setVisibility(4);
            }
        }
    }

    private void updateProxyButton(boolean animated) {
        boolean z = false;
        if (this.proxyDrawable != null) {
            boolean proxyEnabled;
            SharedPreferences preferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
            String proxyAddress = preferences.getString("proxy_ip", TtmlNode.ANONYMOUS_REGION_ID);
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
                    this.commentView.setFieldText(TtmlNode.ANONYMOUS_REGION_ID);
                    this.commentView.setVisibility(0);
                    animatorSet = new AnimatorSet();
                    animatorArr = new Animator[1];
                    animatorArr[0] = ObjectAnimator.ofFloat(this.commentView, "translationY", new float[]{(float) this.commentView.getMeasuredHeight(), 0.0f});
                    animatorSet.playTogether(animatorArr);
                    animatorSet.setDuration(180);
                    animatorSet.setInterpolator(new DecelerateInterpolator());
                    animatorSet.addListener(new C167914());
                    animatorSet.start();
                    this.commentView.setTag(Integer.valueOf(1));
                }
                this.actionBar.setTitle(LocaleController.formatPluralString("Recipient", this.dialogsAdapter.getSelectedDialogs().size()));
                return;
            }
            if (this.dialogsType == 3 && this.selectAlertString == null) {
                this.actionBar.setTitle(LocaleController.getString("ForwardTo", C0541R.string.ForwardTo));
            } else {
                this.actionBar.setTitle(LocaleController.getString("SelectChat", C0541R.string.SelectChat));
            }
            if (this.commentView.getTag() != null) {
                this.commentView.hidePopup(false);
                this.commentView.closeKeyboard();
                animatorSet = new AnimatorSet();
                animatorArr = new Animator[1];
                animatorArr[0] = ObjectAnimator.ofFloat(this.commentView, "translationY", new float[]{0.0f, (float) this.commentView.getMeasuredHeight()});
                animatorSet.playTogether(animatorArr);
                animatorSet.setDuration(180);
                animatorSet.setInterpolator(new DecelerateInterpolator());
                animatorSet.addListener(new C167813());
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
                    Dialog create = AlertsCreator.createContactsPermissionDialog(activity, new DialogsActivity$$Lambda$6(this)).create();
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

    final /* synthetic */ void lambda$askForPermissons$6$DialogsActivity(int param) {
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
        if (this.permissionDialog != null && dialog == this.permissionDialog && getParentActivity() != null) {
            askForPermissons(false);
        }
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (!this.onlySelect && this.floatingButton != null) {
            this.floatingButton.getViewTreeObserver().addOnGlobalLayoutListener(new C168015());
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
            checkUnreadCount(true);
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
                    FileLog.m14e(e);
                }
            }
        } else if (id == NotificationCenter.emojiDidLoaded) {
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
            if ((mask.intValue() & 2048) != 0 || (mask.intValue() & 256) != 0) {
                checkUnreadCount(true);
            }
        } else if (id == NotificationCenter.appDidLogout) {
            dialogsLoaded[this.currentAccount] = false;
        } else if (id == NotificationCenter.encryptedChatUpdated) {
            updateVisibleRows(0);
        } else if (id == NotificationCenter.contactsDidLoaded) {
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
        } else if (id == NotificationCenter.didLoadedReplyMessages) {
            updateVisibleRows(32768);
        } else if (id == NotificationCenter.reloadHints) {
            if (this.dialogsSearchAdapter != null) {
                this.dialogsSearchAdapter.notifyDataSetChanged();
            }
        } else if (id == NotificationCenter.didUpdatedConnectionState) {
            int state = ConnectionsManager.getInstance(account).getConnectionState();
            if (this.currentConnectionState != state) {
                this.currentConnectionState = state;
                updateProxyButton(true);
            }
        } else {
            if (id == NotificationCenter.dialogsUnreadCounterChanged) {
            }
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
        return null;
    }

    public void setSideMenu(RecyclerView recyclerView) {
        this.sideMenu = recyclerView;
        this.sideMenu.setBackgroundColor(Theme.getColor(Theme.key_chats_menuBackground));
        this.sideMenu.setGlowColor(Theme.getColor(Theme.key_chats_menuBackground));
    }

    private void updatePasscodeButton() {
        if (this.passcodeItem != null) {
            if (SharedConfig.passcodeHash.length() == 0 || this.searching) {
                this.passcodeItem.setVisibility(8);
                return;
            }
            this.passcodeItem.setVisibility(0);
            if (SharedConfig.appLocked) {
                this.passcodeItem.setIcon((int) C0541R.drawable.lock_close);
            } else {
                this.passcodeItem.setIcon((int) C0541R.drawable.lock_open);
            }
        }
    }

    private void hideFloatingButton(boolean hide) {
        float f = 0.0f;
        if (this.floatingHidden != hide) {
            boolean z;
            this.floatingHidden = hide;
            AnimatorSet animatorSet = new AnimatorSet();
            Animator[] animatorArr = new Animator[2];
            ImageView imageView = this.floatingButton;
            String str = "translationY";
            float[] fArr = new float[1];
            fArr[0] = this.floatingHidden ? (float) AndroidUtilities.m10dp(100.0f) : 0.0f;
            animatorArr[0] = ObjectAnimator.ofFloat(imageView, str, fArr);
            FrameLayout frameLayout = this.unreadFloatingButtonContainer;
            String str2 = "translationY";
            float[] fArr2 = new float[1];
            if (this.floatingHidden) {
                f = (float) AndroidUtilities.m10dp(74.0f);
            }
            fArr2[0] = f;
            animatorArr[1] = ObjectAnimator.ofFloat(frameLayout, str2, fArr2);
            animatorSet.playTogether(animatorArr);
            animatorSet.setDuration(300);
            animatorSet.setInterpolator(this.floatingInterpolator);
            ImageView imageView2 = this.floatingButton;
            if (hide) {
                z = false;
            } else {
                z = true;
            }
            imageView2.setClickable(z);
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
                            ((HintDialogCell) child2).checkUnreadCounter(mask);
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
        Builder builder;
        if (this.addToGroupAlertString == null && ((int) dialog_id) < 0) {
            chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-((int) dialog_id)));
            if (ChatObject.isChannel(chat) && !chat.megagroup && (this.cantSendToChannels || !ChatObject.isCanWriteToChannel(-((int) dialog_id), this.currentAccount))) {
                builder = new Builder(getParentActivity());
                builder.setTitle(LocaleController.getString("AppName", C0541R.string.AppName));
                builder.setMessage(LocaleController.getString("ChannelCantSendMessage", C0541R.string.ChannelCantSendMessage));
                builder.setNegativeButton(LocaleController.getString("OK", C0541R.string.OK), null);
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
            finishFragment();
        } else if (getParentActivity() != null) {
            builder = new Builder(getParentActivity());
            builder.setTitle(LocaleController.getString("AppName", C0541R.string.AppName));
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
                builder.setMessage(LocaleController.formatStringSimple(this.selectAlertStringGroup, LocaleController.getString("SavedMessages", C0541R.string.SavedMessages)));
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
            builder.setPositiveButton(LocaleController.getString("OK", C0541R.string.OK), new DialogsActivity$$Lambda$7(this, dialog_id));
            builder.setNegativeButton(LocaleController.getString("Cancel", C0541R.string.Cancel), null);
            showDialog(builder.create());
        }
    }

    final /* synthetic */ void lambda$didSelectResult$7$DialogsActivity(long dialog_id, DialogInterface dialogInterface, int i) {
        didSelectResult(dialog_id, false, false);
    }

    public ThemeDescription[] getThemeDescriptions() {
        ThemeDescriptionDelegate cellDelegate = new DialogsActivity$$Lambda$8(this);
        ThemeDescription[] themeDescriptionArr = new ThemeDescription[140];
        themeDescriptionArr[0] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite);
        themeDescriptionArr[1] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault);
        themeDescriptionArr[2] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault);
        themeDescriptionArr[3] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon);
        themeDescriptionArr[4] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle);
        themeDescriptionArr[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector);
        themeDescriptionArr[6] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCH, null, null, null, null, Theme.key_actionBarDefaultSearch);
        themeDescriptionArr[7] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCHPLACEHOLDER, null, null, null, null, Theme.key_actionBarDefaultSearchPlaceholder);
        themeDescriptionArr[8] = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector);
        themeDescriptionArr[9] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider);
        themeDescriptionArr[10] = new ThemeDescription(this.searchEmptyView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_emptyListPlaceholder);
        themeDescriptionArr[11] = new ThemeDescription(this.searchEmptyView, ThemeDescription.FLAG_PROGRESSBAR, null, null, null, null, Theme.key_progressCircle);
        themeDescriptionArr[12] = new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{DialogsEmptyCell.class}, new String[]{"emptyTextView1"}, null, null, null, Theme.key_emptyListPlaceholder);
        themeDescriptionArr[13] = new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{DialogsEmptyCell.class}, new String[]{"emptyTextView2"}, null, null, null, Theme.key_emptyListPlaceholder);
        themeDescriptionArr[14] = new ThemeDescription(this.floatingButton, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, Theme.key_chats_actionIcon);
        themeDescriptionArr[15] = new ThemeDescription(this.floatingButton, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_chats_actionBackground);
        themeDescriptionArr[16] = new ThemeDescription(this.floatingButton, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, Theme.key_chats_actionPressedBackground);
        themeDescriptionArr[17] = new ThemeDescription(this.unreadFloatingButtonCounter, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_chat_goDownButtonCounterBackground);
        themeDescriptionArr[18] = new ThemeDescription(this.unreadFloatingButtonCounter, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_chat_goDownButtonCounter);
        themeDescriptionArr[19] = new ThemeDescription(this.unreadFloatingButton, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, Theme.key_chats_actionUnreadIcon);
        themeDescriptionArr[20] = new ThemeDescription(this.unreadFloatingButton, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_chats_actionUnreadBackground);
        themeDescriptionArr[21] = new ThemeDescription(this.unreadFloatingButton, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, Theme.key_chats_actionUnreadPressedBackground);
        themeDescriptionArr[22] = new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class, ProfileSearchCell.class}, null, new Drawable[]{Theme.avatar_photoDrawable, Theme.avatar_broadcastDrawable, Theme.avatar_savedDrawable}, null, Theme.key_avatar_text);
        themeDescriptionArr[23] = new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundRed);
        themeDescriptionArr[24] = new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundOrange);
        themeDescriptionArr[25] = new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundViolet);
        themeDescriptionArr[26] = new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundGreen);
        themeDescriptionArr[27] = new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundCyan);
        themeDescriptionArr[28] = new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundBlue);
        themeDescriptionArr[29] = new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundPink);
        themeDescriptionArr[30] = new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundSaved);
        themeDescriptionArr[31] = new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class}, Theme.dialogs_countPaint, null, null, Theme.key_chats_unreadCounter);
        themeDescriptionArr[32] = new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class}, Theme.dialogs_countGrayPaint, null, null, Theme.key_chats_unreadCounterMuted);
        themeDescriptionArr[33] = new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class}, Theme.dialogs_countTextPaint, null, null, Theme.key_chats_unreadCounterText);
        themeDescriptionArr[34] = new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class, ProfileSearchCell.class}, Theme.dialogs_namePaint, null, null, Theme.key_chats_name);
        themeDescriptionArr[35] = new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class, ProfileSearchCell.class}, Theme.dialogs_nameEncryptedPaint, null, null, Theme.key_chats_secretName);
        themeDescriptionArr[36] = new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class, ProfileSearchCell.class}, null, new Drawable[]{Theme.dialogs_lockDrawable}, null, Theme.key_chats_secretIcon);
        themeDescriptionArr[37] = new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class, ProfileSearchCell.class}, null, new Drawable[]{Theme.dialogs_groupDrawable, Theme.dialogs_broadcastDrawable, Theme.dialogs_botDrawable}, null, Theme.key_chats_nameIcon);
        themeDescriptionArr[38] = new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class}, null, new Drawable[]{Theme.dialogs_pinnedDrawable}, null, Theme.key_chats_pinnedIcon);
        themeDescriptionArr[39] = new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class}, Theme.dialogs_messagePaint, null, null, Theme.key_chats_message);
        themeDescriptionArr[40] = new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_chats_nameMessage);
        themeDescriptionArr[41] = new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_chats_draft);
        themeDescriptionArr[42] = new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_chats_attachMessage);
        themeDescriptionArr[43] = new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class}, Theme.dialogs_messagePrintingPaint, null, null, Theme.key_chats_actionMessage);
        themeDescriptionArr[44] = new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class}, Theme.dialogs_timePaint, null, null, Theme.key_chats_date);
        themeDescriptionArr[45] = new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class}, Theme.dialogs_pinnedPaint, null, null, Theme.key_chats_pinnedOverlay);
        themeDescriptionArr[46] = new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class}, Theme.dialogs_tabletSeletedPaint, null, null, Theme.key_chats_tabletSelectedOverlay);
        themeDescriptionArr[47] = new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class}, null, new Drawable[]{Theme.dialogs_checkDrawable, Theme.dialogs_halfCheckDrawable}, null, Theme.key_chats_sentCheck);
        themeDescriptionArr[48] = new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class}, null, new Drawable[]{Theme.dialogs_clockDrawable}, null, Theme.key_chats_sentClock);
        themeDescriptionArr[49] = new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class}, Theme.dialogs_errorPaint, null, null, Theme.key_chats_sentError);
        themeDescriptionArr[50] = new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class}, null, new Drawable[]{Theme.dialogs_errorDrawable}, null, Theme.key_chats_sentErrorIcon);
        themeDescriptionArr[51] = new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class, ProfileSearchCell.class}, null, new Drawable[]{Theme.dialogs_verifiedCheckDrawable}, null, Theme.key_chats_verifiedCheck);
        themeDescriptionArr[52] = new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class, ProfileSearchCell.class}, null, new Drawable[]{Theme.dialogs_verifiedDrawable}, null, Theme.key_chats_verifiedBackground);
        themeDescriptionArr[53] = new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class}, null, new Drawable[]{Theme.dialogs_muteDrawable}, null, Theme.key_chats_muteIcon);
        themeDescriptionArr[54] = new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class}, null, new Drawable[]{Theme.dialogs_mentionDrawable}, null, Theme.key_chats_mentionIcon);
        themeDescriptionArr[55] = new ThemeDescription(this.sideMenu, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_chats_menuBackground);
        themeDescriptionArr[56] = new ThemeDescription(this.sideMenu, 0, new Class[]{DrawerProfileCell.class}, null, null, null, Theme.key_chats_menuName);
        themeDescriptionArr[57] = new ThemeDescription(this.sideMenu, 0, new Class[]{DrawerProfileCell.class}, null, null, null, Theme.key_chats_menuPhone);
        themeDescriptionArr[58] = new ThemeDescription(this.sideMenu, 0, new Class[]{DrawerProfileCell.class}, null, null, null, Theme.key_chats_menuPhoneCats);
        themeDescriptionArr[59] = new ThemeDescription(this.sideMenu, 0, new Class[]{DrawerProfileCell.class}, null, null, null, Theme.key_chats_menuCloudBackgroundCats);
        themeDescriptionArr[60] = new ThemeDescription(this.sideMenu, 0, new Class[]{DrawerProfileCell.class}, null, null, null, Theme.key_chat_serviceBackground);
        themeDescriptionArr[61] = new ThemeDescription(this.sideMenu, 0, new Class[]{DrawerProfileCell.class}, null, null, null, Theme.key_chats_menuTopShadow);
        themeDescriptionArr[62] = new ThemeDescription(this.sideMenu, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{DrawerProfileCell.class}, null, null, null, Theme.key_avatar_backgroundActionBarBlue);
        themeDescriptionArr[63] = new ThemeDescription(this.sideMenu, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{DrawerActionCell.class}, new String[]{"textView"}, null, null, null, Theme.key_chats_menuItemIcon);
        themeDescriptionArr[64] = new ThemeDescription(this.sideMenu, 0, new Class[]{DrawerActionCell.class}, new String[]{"textView"}, null, null, null, Theme.key_chats_menuItemText);
        themeDescriptionArr[65] = new ThemeDescription(this.sideMenu, 0, new Class[]{DrawerUserCell.class}, new String[]{"textView"}, null, null, null, Theme.key_chats_menuItemText);
        themeDescriptionArr[66] = new ThemeDescription(this.sideMenu, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{DrawerUserCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_chats_unreadCounterText);
        themeDescriptionArr[67] = new ThemeDescription(this.sideMenu, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{DrawerUserCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_chats_unreadCounter);
        themeDescriptionArr[68] = new ThemeDescription(this.sideMenu, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{DrawerUserCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_chats_menuBackground);
        themeDescriptionArr[69] = new ThemeDescription(this.sideMenu, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{DrawerAddCell.class}, new String[]{"textView"}, null, null, null, Theme.key_chats_menuItemIcon);
        themeDescriptionArr[70] = new ThemeDescription(this.sideMenu, 0, new Class[]{DrawerAddCell.class}, new String[]{"textView"}, null, null, null, Theme.key_chats_menuItemText);
        themeDescriptionArr[71] = new ThemeDescription(this.sideMenu, 0, new Class[]{DividerCell.class}, Theme.dividerPaint, null, null, Theme.key_divider);
        themeDescriptionArr[72] = new ThemeDescription(this.listView, 0, new Class[]{LoadingCell.class}, new String[]{"progressBar"}, null, null, null, Theme.key_progressCircle);
        themeDescriptionArr[73] = new ThemeDescription(this.listView, 0, new Class[]{ProfileSearchCell.class}, Theme.dialogs_offlinePaint, null, null, Theme.key_windowBackgroundWhiteGrayText3);
        themeDescriptionArr[74] = new ThemeDescription(this.listView, 0, new Class[]{ProfileSearchCell.class}, Theme.dialogs_onlinePaint, null, null, Theme.key_windowBackgroundWhiteBlueText3);
        themeDescriptionArr[75] = new ThemeDescription(this.listView, 0, new Class[]{GraySectionCell.class}, new String[]{"textView"}, null, null, null, Theme.key_graySectionText);
        themeDescriptionArr[76] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{GraySectionCell.class}, null, null, null, Theme.key_graySection);
        themeDescriptionArr[77] = new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{HashtagSearchCell.class}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        themeDescriptionArr[78] = new ThemeDescription(this.progressView, ThemeDescription.FLAG_PROGRESSBAR, null, null, null, null, Theme.key_progressCircle);
        themeDescriptionArr[79] = new ThemeDescription(this.dialogsSearchAdapter != null ? this.dialogsSearchAdapter.getInnerListView() : null, 0, new Class[]{HintDialogCell.class}, Theme.dialogs_countPaint, null, null, Theme.key_chats_unreadCounter);
        themeDescriptionArr[80] = new ThemeDescription(this.dialogsSearchAdapter != null ? this.dialogsSearchAdapter.getInnerListView() : null, 0, new Class[]{HintDialogCell.class}, Theme.dialogs_countGrayPaint, null, null, Theme.key_chats_unreadCounterMuted);
        themeDescriptionArr[81] = new ThemeDescription(this.dialogsSearchAdapter != null ? this.dialogsSearchAdapter.getInnerListView() : null, 0, new Class[]{HintDialogCell.class}, Theme.dialogs_countTextPaint, null, null, Theme.key_chats_unreadCounterText);
        themeDescriptionArr[82] = new ThemeDescription(this.dialogsSearchAdapter != null ? this.dialogsSearchAdapter.getInnerListView() : null, 0, new Class[]{HintDialogCell.class}, new String[]{"nameTextView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        themeDescriptionArr[83] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND | ThemeDescription.FLAG_CHECKTAG, new Class[]{FragmentContextView.class}, new String[]{"frameLayout"}, null, null, null, Theme.key_inappPlayerBackground);
        themeDescriptionArr[84] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{FragmentContextView.class}, new String[]{"playButton"}, null, null, null, Theme.key_inappPlayerPlayPause);
        themeDescriptionArr[85] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{FragmentContextView.class}, new String[]{"titleTextView"}, null, null, null, Theme.key_inappPlayerTitle);
        themeDescriptionArr[86] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_FASTSCROLL, new Class[]{FragmentContextView.class}, new String[]{"titleTextView"}, null, null, null, Theme.key_inappPlayerPerformer);
        themeDescriptionArr[87] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{FragmentContextView.class}, new String[]{"closeButton"}, null, null, null, Theme.key_inappPlayerClose);
        themeDescriptionArr[88] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND | ThemeDescription.FLAG_CHECKTAG, new Class[]{FragmentContextView.class}, new String[]{"frameLayout"}, null, null, null, Theme.key_returnToCallBackground);
        themeDescriptionArr[89] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{FragmentContextView.class}, new String[]{"titleTextView"}, null, null, null, Theme.key_returnToCallText);
        themeDescriptionArr[90] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogBackground);
        themeDescriptionArr[91] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogBackgroundGray);
        themeDescriptionArr[92] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogTextBlack);
        themeDescriptionArr[93] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogTextLink);
        themeDescriptionArr[94] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogLinkSelection);
        themeDescriptionArr[95] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogTextBlue);
        themeDescriptionArr[96] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogTextBlue2);
        themeDescriptionArr[97] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogTextBlue3);
        themeDescriptionArr[98] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogTextBlue4);
        themeDescriptionArr[99] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogTextRed);
        themeDescriptionArr[100] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogTextGray);
        themeDescriptionArr[101] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogTextGray2);
        themeDescriptionArr[102] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogTextGray3);
        themeDescriptionArr[103] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogTextGray4);
        themeDescriptionArr[104] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogIcon);
        themeDescriptionArr[105] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogTextHint);
        themeDescriptionArr[106] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogInputField);
        themeDescriptionArr[107] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogInputFieldActivated);
        themeDescriptionArr[108] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogCheckboxSquareBackground);
        themeDescriptionArr[109] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogCheckboxSquareCheck);
        themeDescriptionArr[110] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogCheckboxSquareUnchecked);
        themeDescriptionArr[111] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogCheckboxSquareDisabled);
        themeDescriptionArr[112] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogRadioBackground);
        themeDescriptionArr[113] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogRadioBackgroundChecked);
        themeDescriptionArr[114] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogProgressCircle);
        themeDescriptionArr[115] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogButton);
        themeDescriptionArr[116] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogButtonSelector);
        themeDescriptionArr[117] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogScrollGlow);
        themeDescriptionArr[118] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogRoundCheckBox);
        themeDescriptionArr[119] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogRoundCheckBoxCheck);
        themeDescriptionArr[120] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogBadgeBackground);
        themeDescriptionArr[121] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogBadgeText);
        themeDescriptionArr[122] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogLineProgress);
        themeDescriptionArr[123] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogLineProgressBackground);
        themeDescriptionArr[124] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogGrayLine);
        themeDescriptionArr[125] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_player_actionBar);
        themeDescriptionArr[126] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_player_actionBarSelector);
        themeDescriptionArr[127] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_player_actionBarTitle);
        themeDescriptionArr[128] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_player_actionBarTop);
        themeDescriptionArr[TsExtractor.TS_STREAM_TYPE_AC3] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_player_actionBarSubtitle);
        themeDescriptionArr[TsExtractor.TS_STREAM_TYPE_HDMV_DTS] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_player_actionBarItems);
        themeDescriptionArr[131] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_player_background);
        themeDescriptionArr[132] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_player_time);
        themeDescriptionArr[133] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_player_progressBackground);
        themeDescriptionArr[TsExtractor.TS_STREAM_TYPE_SPLICE_INFO] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_player_progressCachedBackground);
        themeDescriptionArr[TsExtractor.TS_STREAM_TYPE_E_AC3] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_player_progress);
        themeDescriptionArr[136] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_player_placeholder);
        themeDescriptionArr[137] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_player_placeholderBackground);
        themeDescriptionArr[TsExtractor.TS_STREAM_TYPE_DTS] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_player_button);
        themeDescriptionArr[139] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_player_buttonActive);
        return themeDescriptionArr;
    }

    final /* synthetic */ void lambda$getThemeDescriptions$8$DialogsActivity() {
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
