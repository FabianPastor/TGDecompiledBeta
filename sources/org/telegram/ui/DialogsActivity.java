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
import android.view.View.OnClickListener;
import android.view.ViewOutlineProvider;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
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
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.exoplayer2.extractor.ts.TsExtractor;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.support.widget.RecyclerView.Adapter;
import org.telegram.messenger.support.widget.RecyclerView.OnScrollListener;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.ChatInvite;
import org.telegram.tgnet.TLRPC.EncryptedChat;
import org.telegram.tgnet.TLRPC.InputStickerSet;
import org.telegram.tgnet.TLRPC.StickerSet;
import org.telegram.tgnet.TLRPC.TL_dialog;
import org.telegram.tgnet.TLRPC.TL_inputStickerSetID;
import org.telegram.tgnet.TLRPC.TL_recentMeUrlChat;
import org.telegram.tgnet.TLRPC.TL_recentMeUrlChatInvite;
import org.telegram.tgnet.TLRPC.TL_recentMeUrlStickerSet;
import org.telegram.tgnet.TLRPC.TL_recentMeUrlUnknown;
import org.telegram.tgnet.TLRPC.TL_recentMeUrlUser;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
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
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.ChatActivityEnterView;
import org.telegram.ui.Components.ChatActivityEnterView.ChatActivityEnterViewDelegate;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.FragmentContextView;
import org.telegram.ui.Components.JoinGroupAlert;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RadialProgressView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;
import org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener;
import org.telegram.ui.Components.SizeNotifierFrameLayout;
import org.telegram.ui.Components.StickersAlert;

public class DialogsActivity extends BaseFragment implements NotificationCenterDelegate {
    public static boolean[] dialogsLoaded = new boolean[3];
    private String addToGroupAlertString;
    private boolean allowSwitchAccount;
    private boolean cantSendToChannels;
    private boolean checkPermission = true;
    private ChatActivityEnterView commentView;
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

    /* renamed from: org.telegram.ui.DialogsActivity$8 */
    class C13848 extends ViewOutlineProvider {
        C13848() {
        }

        @SuppressLint({"NewApi"})
        public void getOutline(View view, Outline outline) {
            outline.setOval(0, 0, AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
        }
    }

    /* renamed from: org.telegram.ui.DialogsActivity$9 */
    class C13859 implements OnClickListener {
        C13859() {
        }

        public void onClick(View v) {
            Bundle args = new Bundle();
            args.putBoolean("destroyAfterSelect", true);
            DialogsActivity.this.presentFragment(new ContactsActivity(args));
        }
    }

    public interface DialogsActivityDelegate {
        void didSelectDialogs(DialogsActivity dialogsActivity, ArrayList<Long> arrayList, CharSequence charSequence, boolean z);
    }

    /* renamed from: org.telegram.ui.DialogsActivity$2 */
    class C21272 extends ActionBarMenuItemSearchListener {
        C21272() {
        }

        public void onSearchExpand() {
            DialogsActivity.this.searching = true;
            if (DialogsActivity.this.switchItem != null) {
                DialogsActivity.this.switchItem.setVisibility(8);
            }
            if (DialogsActivity.this.listView != null) {
                if (DialogsActivity.this.searchString != null) {
                    DialogsActivity.this.listView.setEmptyView(DialogsActivity.this.searchEmptyView);
                    DialogsActivity.this.progressView.setVisibility(8);
                }
                if (!DialogsActivity.this.onlySelect) {
                    DialogsActivity.this.floatingButton.setVisibility(8);
                }
            }
            DialogsActivity.this.updatePasscodeButton();
        }

        public boolean canCollapseSearch() {
            if (DialogsActivity.this.switchItem != null) {
                DialogsActivity.this.switchItem.setVisibility(0);
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
                if (MessagesController.getInstance(DialogsActivity.this.currentAccount).loadingDialogs && MessagesController.getInstance(DialogsActivity.this.currentAccount).dialogs.isEmpty()) {
                    DialogsActivity.this.listView.setEmptyView(DialogsActivity.this.progressView);
                } else {
                    DialogsActivity.this.progressView.setVisibility(8);
                    DialogsActivity.this.listView.setEmptyView(null);
                }
                DialogsActivity.this.searchEmptyView.setVisibility(8);
                if (!DialogsActivity.this.onlySelect) {
                    DialogsActivity.this.floatingButton.setVisibility(0);
                    DialogsActivity.this.floatingHidden = true;
                    DialogsActivity.this.floatingButton.setTranslationY((float) AndroidUtilities.dp(100.0f));
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

    /* renamed from: org.telegram.ui.DialogsActivity$3 */
    class C21283 extends ActionBarMenuOnItemClick {
        C21283() {
        }

        public void onItemClick(int id) {
            if (id == -1) {
                if (DialogsActivity.this.onlySelect) {
                    DialogsActivity.this.finishFragment();
                } else if (DialogsActivity.this.parentLayout != null) {
                    DialogsActivity.this.parentLayout.getDrawerLayoutContainer().openDrawer(false);
                }
            } else if (id == 1) {
                SharedConfig.appLocked ^= true;
                SharedConfig.saveConfig();
                DialogsActivity.this.updatePasscodeButton();
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

    /* renamed from: org.telegram.ui.DialogsActivity$6 */
    class C21306 implements OnItemClickListener {
        C21306() {
        }

        public void onItemClick(View view, int position) {
            View view2;
            int i = position;
            if (!(DialogsActivity.this.listView == null || DialogsActivity.this.listView.getAdapter() == null)) {
                if (DialogsActivity.this.getParentActivity() != null) {
                    long dialog_id;
                    int message_id = 0;
                    Adapter adapter = DialogsActivity.this.listView.getAdapter();
                    long dialog_id2;
                    if (adapter == DialogsActivity.this.dialogsAdapter) {
                        long dialog_id3;
                        TLObject object = DialogsActivity.this.dialogsAdapter.getItem(i);
                        if (object instanceof TL_dialog) {
                            dialog_id3 = ((TL_dialog) object).id;
                        } else if (object instanceof TL_recentMeUrlChat) {
                            dialog_id3 = (long) (-((TL_recentMeUrlChat) object).chat_id);
                        } else if (object instanceof TL_recentMeUrlUser) {
                            dialog_id3 = (long) ((TL_recentMeUrlUser) object).user_id;
                        } else if (object instanceof TL_recentMeUrlChatInvite) {
                            TL_recentMeUrlChatInvite chatInvite = (TL_recentMeUrlChatInvite) object;
                            ChatInvite invite = chatInvite.chat_invite;
                            if ((invite.chat == null && (!invite.channel || invite.megagroup)) || (invite.chat != null && (!ChatObject.isChannel(invite.chat) || invite.chat.megagroup))) {
                                String hash = chatInvite.url;
                                int index = hash.indexOf(47);
                                if (index > 0) {
                                    hash = hash.substring(index + 1);
                                }
                                DialogsActivity.this.showDialog(new JoinGroupAlert(DialogsActivity.this.getParentActivity(), invite, hash, DialogsActivity.this));
                                return;
                            } else if (invite.chat != null) {
                                dialog_id3 = (long) (-invite.chat.id);
                            } else {
                                return;
                            }
                        } else if (object instanceof TL_recentMeUrlStickerSet) {
                            StickerSet stickerSet = ((TL_recentMeUrlStickerSet) object).set.set;
                            InputStickerSet set = new TL_inputStickerSetID();
                            set.id = stickerSet.id;
                            set.access_hash = stickerSet.access_hash;
                            DialogsActivity dialogsActivity = DialogsActivity.this;
                            dialog_id2 = 0;
                            Dialog dialog = r9;
                            Dialog stickersAlert = new StickersAlert(DialogsActivity.this.getParentActivity(), DialogsActivity.this, set, null, null);
                            dialogsActivity.showDialog(dialog);
                            return;
                        } else {
                            dialog_id2 = 0;
                            if (!(object instanceof TL_recentMeUrlUnknown)) {
                                return;
                            }
                            return;
                        }
                        dialog_id = dialog_id3;
                    } else {
                        dialog_id2 = 0;
                        if (adapter == DialogsActivity.this.dialogsSearchAdapter) {
                            MessageObject obj = DialogsActivity.this.dialogsSearchAdapter.getItem(i);
                            if (obj instanceof User) {
                                dialog_id = (long) ((User) obj).id;
                                if (!DialogsActivity.this.onlySelect) {
                                    DialogsActivity.this.dialogsSearchAdapter.putRecentSearch(dialog_id, (User) obj);
                                }
                            } else if (obj instanceof Chat) {
                                if (((Chat) obj).id > 0) {
                                    dialog_id = (long) (-((Chat) obj).id);
                                } else {
                                    dialog_id = AndroidUtilities.makeBroadcastId(((Chat) obj).id);
                                }
                                if (!DialogsActivity.this.onlySelect) {
                                    DialogsActivity.this.dialogsSearchAdapter.putRecentSearch(dialog_id, (Chat) obj);
                                }
                            } else if (obj instanceof EncryptedChat) {
                                dialog_id = ((long) ((EncryptedChat) obj).id) << 32;
                                if (!DialogsActivity.this.onlySelect) {
                                    DialogsActivity.this.dialogsSearchAdapter.putRecentSearch(dialog_id, (EncryptedChat) obj);
                                }
                            } else if (obj instanceof MessageObject) {
                                MessageObject messageObject = obj;
                                dialog_id = messageObject.getDialogId();
                                message_id = messageObject.getId();
                                DialogsActivity.this.dialogsSearchAdapter.addHashtagsFromMessage(DialogsActivity.this.dialogsSearchAdapter.getLastSearchString());
                            } else if (obj instanceof String) {
                                DialogsActivity.this.actionBar.openSearchField((String) obj);
                            }
                        }
                        dialog_id = dialog_id2;
                    }
                    if (dialog_id != 0) {
                        if (!DialogsActivity.this.onlySelect) {
                            view2 = view;
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
                                    Chat chat = MessagesController.getInstance(DialogsActivity.this.currentAccount).getChat(Integer.valueOf(-lower_part));
                                    if (!(chat == null || chat.migrated_to == null)) {
                                        args.putInt("migrated_to", lower_part);
                                        lower_part = -chat.migrated_to.channel_id;
                                    }
                                }
                                args.putInt("chat_id", -lower_part);
                            }
                            if (message_id != 0) {
                                args.putInt("message_id", message_id);
                            } else if (DialogsActivity.this.actionBar != null) {
                                DialogsActivity.this.actionBar.closeSearchField();
                            }
                            if (AndroidUtilities.isTablet()) {
                                if (DialogsActivity.this.openedDialogId == dialog_id && adapter != DialogsActivity.this.dialogsSearchAdapter) {
                                    return;
                                }
                                if (DialogsActivity.this.dialogsAdapter != null) {
                                    DialogsActivity.this.dialogsAdapter.setOpenedDialogId(DialogsActivity.this.openedDialogId = dialog_id);
                                    DialogsActivity.this.updateVisibleRows(512);
                                }
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
                            DialogsActivity.this.dialogsAdapter.addOrRemoveSelectedDialog(dialog_id, view);
                            DialogsActivity.this.updateSelectedCount();
                        } else {
                            view2 = view;
                            DialogsActivity.this.didSelectResult(dialog_id, true, false);
                        }
                        return;
                    }
                    return;
                }
            }
            view2 = view;
        }
    }

    /* renamed from: org.telegram.ui.DialogsActivity$7 */
    class C21317 implements OnItemLongClickListener {

        /* renamed from: org.telegram.ui.DialogsActivity$7$1 */
        class C13781 implements DialogInterface.OnClickListener {
            C13781() {
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                if (DialogsActivity.this.dialogsSearchAdapter.isRecentSearchDisplayed()) {
                    DialogsActivity.this.dialogsSearchAdapter.clearRecentSearch();
                } else {
                    DialogsActivity.this.dialogsSearchAdapter.clearRecentHashtags();
                }
            }
        }

        C21317() {
        }

        public boolean onItemClick(View view, int position) {
            int i = position;
            boolean z = false;
            if (DialogsActivity.this.getParentActivity() == null) {
                return false;
            }
            if (DialogsActivity.this.listView.getAdapter() == DialogsActivity.this.dialogsSearchAdapter) {
                if (!(DialogsActivity.this.dialogsSearchAdapter.getItem(i) instanceof String)) {
                    if (!DialogsActivity.this.dialogsSearchAdapter.isRecentSearchDisplayed()) {
                        return false;
                    }
                }
                Builder builder = new Builder(DialogsActivity.this.getParentActivity());
                builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                builder.setMessage(LocaleController.getString("ClearSearch", R.string.ClearSearch));
                builder.setPositiveButton(LocaleController.getString("ClearButton", R.string.ClearButton).toUpperCase(), new C13781());
                builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                DialogsActivity.this.showDialog(builder.create());
                return true;
            }
            View view2;
            ArrayList<TL_dialog> dialogs = DialogsActivity.this.getDialogsArray();
            if (i >= 0) {
                if (i < dialogs.size()) {
                    TL_dialog dialog = (TL_dialog) dialogs.get(i);
                    if (DialogsActivity.this.onlySelect) {
                        if (DialogsActivity.this.dialogsType == 3) {
                            if (DialogsActivity.this.selectAlertString == null) {
                                DialogsActivity.this.dialogsAdapter.addOrRemoveSelectedDialog(dialog.id, view);
                                DialogsActivity.this.updateSelectedCount();
                            }
                        }
                        view2 = view;
                        return false;
                    }
                    view2 = view;
                    DialogsActivity.this.selectedDialog = dialog.id;
                    final boolean pinned = dialog.pinned;
                    BottomSheet.Builder builder2 = new BottomSheet.Builder(DialogsActivity.this.getParentActivity());
                    int lower_id = (int) DialogsActivity.this.selectedDialog;
                    int high_id = (int) (DialogsActivity.this.selectedDialog >> 32);
                    boolean isChannel = DialogObject.isChannel(dialog);
                    int i2 = R.drawable.chats_leave;
                    int i3 = R.drawable.chats_pin;
                    String str;
                    int i4;
                    if (isChannel) {
                        CharSequence[] items;
                        final Chat chat = MessagesController.getInstance(DialogsActivity.this.currentAccount).getChat(Integer.valueOf(-lower_id));
                        int[] icons = new int[3];
                        if (dialog.pinned) {
                            i3 = R.drawable.chats_unpin;
                        }
                        icons[0] = i3;
                        icons[1] = R.drawable.chats_clear;
                        icons[2] = R.drawable.chats_leave;
                        CharSequence[] charSequenceArr;
                        String str2;
                        int i5;
                        if (chat == null || !chat.megagroup) {
                            charSequenceArr = new CharSequence[3];
                            if (!dialog.pinned) {
                                if (!MessagesController.getInstance(DialogsActivity.this.currentAccount).canPinDialog(false)) {
                                    str2 = null;
                                    charSequenceArr[0] = str2;
                                    charSequenceArr[1] = LocaleController.getString("ClearHistoryCache", R.string.ClearHistoryCache);
                                    charSequenceArr[2] = LocaleController.getString("LeaveChannelMenu", R.string.LeaveChannelMenu);
                                    items = charSequenceArr;
                                }
                            }
                            if (dialog.pinned) {
                                str2 = "UnpinFromTop";
                                i5 = R.string.UnpinFromTop;
                            } else {
                                str2 = "PinToTop";
                                i5 = R.string.PinToTop;
                            }
                            str2 = LocaleController.getString(str2, i5);
                            charSequenceArr[0] = str2;
                            charSequenceArr[1] = LocaleController.getString("ClearHistoryCache", R.string.ClearHistoryCache);
                            charSequenceArr[2] = LocaleController.getString("LeaveChannelMenu", R.string.LeaveChannelMenu);
                            items = charSequenceArr;
                        } else {
                            charSequenceArr = new CharSequence[3];
                            if (!dialog.pinned) {
                                if (!MessagesController.getInstance(DialogsActivity.this.currentAccount).canPinDialog(false)) {
                                    str2 = null;
                                    charSequenceArr[0] = str2;
                                    if (TextUtils.isEmpty(chat.username)) {
                                        str = "ClearHistoryCache";
                                        i4 = R.string.ClearHistoryCache;
                                    } else {
                                        str = "ClearHistory";
                                        i4 = R.string.ClearHistory;
                                    }
                                    charSequenceArr[1] = LocaleController.getString(str, i4);
                                    charSequenceArr[2] = LocaleController.getString("LeaveMegaMenu", R.string.LeaveMegaMenu);
                                    items = charSequenceArr;
                                }
                            }
                            if (dialog.pinned) {
                                str2 = "UnpinFromTop";
                                i5 = R.string.UnpinFromTop;
                            } else {
                                str2 = "PinToTop";
                                i5 = R.string.PinToTop;
                            }
                            str2 = LocaleController.getString(str2, i5);
                            charSequenceArr[0] = str2;
                            if (TextUtils.isEmpty(chat.username)) {
                                str = "ClearHistoryCache";
                                i4 = R.string.ClearHistoryCache;
                            } else {
                                str = "ClearHistory";
                                i4 = R.string.ClearHistory;
                            }
                            charSequenceArr[1] = LocaleController.getString(str, i4);
                            charSequenceArr[2] = LocaleController.getString("LeaveMegaMenu", R.string.LeaveMegaMenu);
                            items = charSequenceArr;
                        }
                        builder2.setItems(items, icons, new DialogInterface.OnClickListener() {

                            /* renamed from: org.telegram.ui.DialogsActivity$7$2$1 */
                            class C13791 implements DialogInterface.OnClickListener {
                                C13791() {
                                }

                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if (chat != null && chat.megagroup && TextUtils.isEmpty(chat.username)) {
                                        MessagesController.getInstance(DialogsActivity.this.currentAccount).deleteDialog(DialogsActivity.this.selectedDialog, 1);
                                    } else {
                                        MessagesController.getInstance(DialogsActivity.this.currentAccount).deleteDialog(DialogsActivity.this.selectedDialog, 2);
                                    }
                                }
                            }

                            /* renamed from: org.telegram.ui.DialogsActivity$7$2$2 */
                            class C13802 implements DialogInterface.OnClickListener {
                                C13802() {
                                }

                                public void onClick(DialogInterface dialogInterface, int i) {
                                    MessagesController.getInstance(DialogsActivity.this.currentAccount).deleteUserFromChat((int) (-DialogsActivity.this.selectedDialog), UserConfig.getInstance(DialogsActivity.this.currentAccount).getCurrentUser(), null);
                                    if (AndroidUtilities.isTablet()) {
                                        NotificationCenter.getInstance(DialogsActivity.this.currentAccount).postNotificationName(NotificationCenter.closeChats, Long.valueOf(DialogsActivity.this.selectedDialog));
                                    }
                                }
                            }

                            public void onClick(DialogInterface dialog, int which) {
                                if (which != 0) {
                                    Builder builder = new Builder(DialogsActivity.this.getParentActivity());
                                    builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                                    if (which == 1) {
                                        if (chat == null || !chat.megagroup) {
                                            builder.setMessage(LocaleController.getString("AreYouSureClearHistoryChannel", R.string.AreYouSureClearHistoryChannel));
                                        } else if (TextUtils.isEmpty(chat.username)) {
                                            builder.setMessage(LocaleController.getString("AreYouSureClearHistory", R.string.AreYouSureClearHistory));
                                        } else {
                                            builder.setMessage(LocaleController.getString("AreYouSureClearHistoryGroup", R.string.AreYouSureClearHistoryGroup));
                                        }
                                        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new C13791());
                                    } else {
                                        if (chat == null || !chat.megagroup) {
                                            builder.setMessage(LocaleController.getString("ChannelLeaveAlert", R.string.ChannelLeaveAlert));
                                        } else {
                                            builder.setMessage(LocaleController.getString("MegaLeaveAlert", R.string.MegaLeaveAlert));
                                        }
                                        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new C13802());
                                    }
                                    builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                                    DialogsActivity.this.showDialog(builder.create());
                                } else if (MessagesController.getInstance(DialogsActivity.this.currentAccount).pinDialog(DialogsActivity.this.selectedDialog, pinned ^ 1, null, 0) && !pinned) {
                                    DialogsActivity.this.listView.smoothScrollToPosition(0);
                                }
                            }
                        });
                        DialogsActivity.this.showDialog(builder2.create());
                    } else {
                        int[] iArr;
                        final boolean isChat = lower_id < 0 && high_id != 1;
                        User user = null;
                        if (!(isChat || lower_id <= 0 || high_id == 1)) {
                            user = MessagesController.getInstance(DialogsActivity.this.currentAccount).getUser(Integer.valueOf(lower_id));
                        }
                        isChannel = user != null && user.bot;
                        CharSequence[] charSequenceArr2 = new CharSequence[3];
                        if (!dialog.pinned) {
                            MessagesController instance = MessagesController.getInstance(DialogsActivity.this.currentAccount);
                            if (lower_id == 0) {
                                z = true;
                            }
                            if (!instance.canPinDialog(z)) {
                                str = null;
                                charSequenceArr2[0] = str;
                                charSequenceArr2[1] = LocaleController.getString("ClearHistory", R.string.ClearHistory);
                                if (isChat) {
                                    str = "DeleteChat";
                                    i4 = R.string.DeleteChat;
                                } else if (isChannel) {
                                    str = "Delete";
                                    i4 = R.string.Delete;
                                } else {
                                    str = "DeleteAndStop";
                                    i4 = R.string.DeleteAndStop;
                                }
                                charSequenceArr2[2] = LocaleController.getString(str, i4);
                                iArr = new int[3];
                                if (dialog.pinned) {
                                    i3 = R.drawable.chats_unpin;
                                }
                                iArr[0] = i3;
                                iArr[1] = R.drawable.chats_clear;
                                if (isChat) {
                                    i2 = R.drawable.chats_delete;
                                }
                                iArr[2] = i2;
                                builder2.setItems(charSequenceArr2, iArr, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, final int which) {
                                        if (which != 0) {
                                            Builder builder = new Builder(DialogsActivity.this.getParentActivity());
                                            builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                                            if (which == 1) {
                                                builder.setMessage(LocaleController.getString("AreYouSureClearHistory", R.string.AreYouSureClearHistory));
                                            } else if (isChat) {
                                                builder.setMessage(LocaleController.getString("AreYouSureDeleteAndExit", R.string.AreYouSureDeleteAndExit));
                                            } else {
                                                builder.setMessage(LocaleController.getString("AreYouSureDeleteThisChat", R.string.AreYouSureDeleteThisChat));
                                            }
                                            builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialogInterface, int i) {
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
                                                        if (isChannel) {
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
                                            });
                                            builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                                            DialogsActivity.this.showDialog(builder.create());
                                        } else if (MessagesController.getInstance(DialogsActivity.this.currentAccount).pinDialog(DialogsActivity.this.selectedDialog, pinned ^ 1, null, 0) && !pinned) {
                                            DialogsActivity.this.listView.smoothScrollToPosition(0);
                                        }
                                    }
                                });
                                DialogsActivity.this.showDialog(builder2.create());
                            }
                        }
                        if (dialog.pinned) {
                            str = "UnpinFromTop";
                            i4 = R.string.UnpinFromTop;
                        } else {
                            str = "PinToTop";
                            i4 = R.string.PinToTop;
                        }
                        str = LocaleController.getString(str, i4);
                        charSequenceArr2[0] = str;
                        charSequenceArr2[1] = LocaleController.getString("ClearHistory", R.string.ClearHistory);
                        if (isChat) {
                            str = "DeleteChat";
                            i4 = R.string.DeleteChat;
                        } else if (isChannel) {
                            str = "Delete";
                            i4 = R.string.Delete;
                        } else {
                            str = "DeleteAndStop";
                            i4 = R.string.DeleteAndStop;
                        }
                        charSequenceArr2[2] = LocaleController.getString(str, i4);
                        iArr = new int[3];
                        if (dialog.pinned) {
                            i3 = R.drawable.chats_unpin;
                        }
                        iArr[0] = i3;
                        iArr[1] = R.drawable.chats_clear;
                        if (isChat) {
                            i2 = R.drawable.chats_delete;
                        }
                        iArr[2] = i2;
                        builder2.setItems(charSequenceArr2, iArr, /* anonymous class already generated */);
                        DialogsActivity.this.showDialog(builder2.create());
                    }
                    return true;
                }
            }
            view2 = view;
            return false;
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
        if (this.searchString == null) {
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.dialogsNeedReload);
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiDidLoaded);
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
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didSetPasscode);
        }
        if (!dialogsLoaded[this.currentAccount]) {
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
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didSetPasscode);
        }
        if (this.commentView != null) {
            this.commentView.onDestroy();
        }
        this.delegate = null;
    }

    public View createView(Context context) {
        final Context context2 = context;
        this.searching = false;
        this.searchWas = false;
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                Theme.createChatResources(context2, false);
            }
        });
        ActionBarMenu menu = this.actionBar.createMenu();
        if (!this.onlySelect && r0.searchString == null) {
            r0.passcodeItem = menu.addItem(1, (int) R.drawable.lock_close);
            updatePasscodeButton();
        }
        menu.addItem(0, (int) R.drawable.ic_ab_search).setIsSearchField(true).setActionBarMenuItemSearchListener(new C21272()).getSearchField().setHint(LocaleController.getString("Search", R.string.Search));
        ActionBarMenu actionBarMenu = 3;
        if (r0.onlySelect) {
            r0.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
            if (r0.dialogsType == 3 && r0.selectAlertString == null) {
                r0.actionBar.setTitle(LocaleController.getString("ForwardTo", R.string.ForwardTo));
            } else {
                r0.actionBar.setTitle(LocaleController.getString("SelectChat", R.string.SelectChat));
            }
        } else {
            if (r0.searchString != null) {
                r0.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
            } else {
                r0.actionBar.setBackButtonDrawable(new MenuDrawable());
            }
            if (BuildVars.DEBUG_VERSION) {
                r0.actionBar.setTitle("Telegram Beta");
            } else {
                r0.actionBar.setTitle(LocaleController.getString("AppName", R.string.AppName));
            }
            r0.actionBar.setSupportsHolidayImage(true);
        }
        if (!r0.allowSwitchAccount || UserConfig.getActivatedAccountsCount() <= 1) {
        } else {
            TLObject avatar;
            AccountSelectCell cell;
            int i;
            r0.switchItem = menu.addItemWithWidth(1, 0, AndroidUtilities.dp(56.0f));
            Drawable avatarDrawable = new AvatarDrawable();
            avatarDrawable.setTextSize(AndroidUtilities.dp(12.0f));
            BackupImageView imageView = new BackupImageView(context2);
            imageView.setRoundRadius(AndroidUtilities.dp(18.0f));
            r0.switchItem.addView(imageView, LayoutHelper.createFrame(36, 36, 17));
            User user = UserConfig.getInstance(r0.currentAccount).getCurrentUser();
            avatarDrawable.setInfo(user);
            if (user.photo == null || user.photo.photo_small == null) {
            } else {
                if (!(user.photo.photo_small.volume_id == 0 || user.photo.photo_small.local_id == 0)) {
                    avatar = user.photo.photo_small;
                    imageView.getImageReceiver().setCurrentAccount(r0.currentAccount);
                    imageView.setImage(avatar, "50_50", avatarDrawable);
                    menu = null;
                    while (menu < actionBarMenu) {
                        if (UserConfig.getInstance(menu).getCurrentUser() != null) {
                            cell = new AccountSelectCell(context2);
                            cell.setAccount(menu);
                            r0.switchItem.addSubItem(10 + menu, cell, AndroidUtilities.dp(230.0f), AndroidUtilities.dp(48.0f));
                        }
                        menu++;
                        i = 3;
                    }
                }
            }
            avatar = null;
            imageView.getImageReceiver().setCurrentAccount(r0.currentAccount);
            imageView.setImage(avatar, "50_50", avatarDrawable);
            menu = null;
            while (menu < actionBarMenu) {
                if (UserConfig.getInstance(menu).getCurrentUser() != null) {
                    cell = new AccountSelectCell(context2);
                    cell.setAccount(menu);
                    r0.switchItem.addSubItem(10 + menu, cell, AndroidUtilities.dp(230.0f), AndroidUtilities.dp(48.0f));
                }
                menu++;
                i = 3;
            }
        }
        r0.actionBar.setAllowOverlayTitle(true);
        r0.actionBar.setActionBarMenuOnItemClick(new C21283());
        if (r0.sideMenu != null) {
            r0.sideMenu.setBackgroundColor(Theme.getColor(Theme.key_chats_menuBackground));
            r0.sideMenu.setGlowColor(Theme.getColor(Theme.key_chats_menuBackground));
            r0.sideMenu.getAdapter().notifyDataSetChanged();
        }
        SizeNotifierFrameLayout contentView = new SizeNotifierFrameLayout(context2) {
            int inputFieldHeight = 0;

            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                int widthSize = MeasureSpec.getSize(widthMeasureSpec);
                int heightSize = MeasureSpec.getSize(heightMeasureSpec);
                setMeasuredDimension(widthSize, heightSize);
                heightSize -= getPaddingTop();
                measureChildWithMargins(DialogsActivity.this.actionBar, widthMeasureSpec, 0, heightMeasureSpec, 0);
                int keyboardSize = getKeyboardHeight();
                int childCount = getChildCount();
                int i = 0;
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
                while (true) {
                    int i2 = i;
                    if (i2 < childCount) {
                        View child = getChildAt(i2);
                        if (!(child == null || child.getVisibility() == 8 || child == DialogsActivity.this.commentView)) {
                            if (child != DialogsActivity.this.actionBar) {
                                if (!(child == DialogsActivity.this.listView || child == DialogsActivity.this.progressView)) {
                                    if (child != DialogsActivity.this.searchEmptyView) {
                                        if (DialogsActivity.this.commentView == null || !DialogsActivity.this.commentView.isPopupView(child)) {
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
                                child.measure(MeasureSpec.makeMeasureSpec(widthSize, NUM), MeasureSpec.makeMeasureSpec(Math.max(AndroidUtilities.dp(10.0f), (heightSize - this.inputFieldHeight) + AndroidUtilities.dp(2.0f)), NUM));
                            }
                        }
                        i = i2 + 1;
                    } else {
                        return;
                    }
                }
            }

            protected void onLayout(boolean changed, int l, int t, int r, int b) {
                int paddingBottom;
                int count = getChildCount();
                Object tag = DialogsActivity.this.commentView != null ? DialogsActivity.this.commentView.getTag() : null;
                int i = 0;
                if (tag == null || !tag.equals(Integer.valueOf(2))) {
                    paddingBottom = 0;
                } else {
                    paddingBottom = (getKeyboardHeight() > AndroidUtilities.dp(20.0f) || AndroidUtilities.isInMultiwindow) ? 0 : DialogsActivity.this.commentView.getEmojiPadding();
                }
                setBottomClip(paddingBottom);
                while (i < count) {
                    int count2;
                    View child = getChildAt(i);
                    if (child.getVisibility() == 8) {
                        count2 = count;
                    } else {
                        int childLeft;
                        LayoutParams lp = (LayoutParams) child.getLayoutParams();
                        int width = child.getMeasuredWidth();
                        int height = child.getMeasuredHeight();
                        int gravity = lp.gravity;
                        if (gravity == -1) {
                            gravity = 51;
                        }
                        int verticalGravity = gravity & 112;
                        int i2 = (gravity & 7) & 7;
                        count2 = count;
                        if (i2 != 1) {
                            if (i2 != 5) {
                                count = lp.leftMargin;
                            } else {
                                count = (r - width) - lp.rightMargin;
                            }
                            childLeft = count;
                        } else {
                            childLeft = ((((r - l) - width) / 2) + lp.leftMargin) - lp.rightMargin;
                        }
                        if (verticalGravity == 16) {
                            i2 = (((((b - paddingBottom) - t) - height) / 2) + lp.topMargin) - lp.bottomMargin;
                        } else if (verticalGravity == 48) {
                            i2 = getPaddingTop() + lp.topMargin;
                        } else if (verticalGravity != 80) {
                            i2 = lp.topMargin;
                        } else {
                            i2 = (((b - paddingBottom) - t) - height) - lp.bottomMargin;
                        }
                        count = i2;
                        if (DialogsActivity.this.commentView != null && DialogsActivity.this.commentView.isPopupView(child)) {
                            count = AndroidUtilities.isInMultiwindow ? (DialogsActivity.this.commentView.getTop() - child.getMeasuredHeight()) + AndroidUtilities.dp(1.0f) : DialogsActivity.this.commentView.getBottom();
                        }
                        child.layout(childLeft, count, childLeft + width, count + height);
                    }
                    i++;
                    count = count2;
                }
                notifyHeightChanged();
            }
        };
        r0.fragmentView = contentView;
        r0.listView = new RecyclerListView(context2);
        r0.listView.setVerticalScrollBarEnabled(true);
        r0.listView.setItemAnimator(null);
        r0.listView.setInstantClick(true);
        r0.listView.setLayoutAnimation(null);
        r0.listView.setTag(Integer.valueOf(4));
        r0.layoutManager = new LinearLayoutManager(context2) {
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        };
        r0.layoutManager.setOrientation(1);
        r0.listView.setLayoutManager(r0.layoutManager);
        r0.listView.setVerticalScrollbarPosition(LocaleController.isRTL ? 1 : 2);
        contentView.addView(r0.listView, LayoutHelper.createFrame(-1, -1.0f));
        r0.listView.setOnItemClickListener(new C21306());
        r0.listView.setOnItemLongClickListener(new C21317());
        r0.searchEmptyView = new EmptyTextProgressView(context2);
        r0.searchEmptyView.setVisibility(8);
        r0.searchEmptyView.setShowAtCenter(true);
        r0.searchEmptyView.setText(LocaleController.getString("NoResult", R.string.NoResult));
        contentView.addView(r0.searchEmptyView, LayoutHelper.createFrame(-1, -1.0f));
        r0.progressView = new RadialProgressView(context2);
        r0.progressView.setVisibility(8);
        contentView.addView(r0.progressView, LayoutHelper.createFrame(-2, -2, 17));
        r0.floatingButton = new ImageView(context2);
        r0.floatingButton.setVisibility(r0.onlySelect ? 8 : 0);
        r0.floatingButton.setScaleType(ScaleType.CENTER);
        Drawable drawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56.0f), Theme.getColor(Theme.key_chats_actionBackground), Theme.getColor(Theme.key_chats_actionPressedBackground));
        if (VERSION.SDK_INT < 21) {
            Drawable shadowDrawable = context.getResources().getDrawable(R.drawable.floating_shadow).mutate();
            shadowDrawable.setColorFilter(new PorterDuffColorFilter(Theme.ACTION_BAR_VIDEO_EDIT_COLOR, Mode.MULTIPLY));
            Drawable combinedDrawable = new CombinedDrawable(shadowDrawable, drawable, 0, 0);
            combinedDrawable.setIconSize(AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
            drawable = combinedDrawable;
        }
        r0.floatingButton.setBackgroundDrawable(drawable);
        r0.floatingButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chats_actionIcon), Mode.MULTIPLY));
        r0.floatingButton.setImageResource(R.drawable.floating_pencil);
        if (VERSION.SDK_INT >= 21) {
            StateListAnimator animator = new StateListAnimator();
            animator.addState(new int[]{16842919}, ObjectAnimator.ofFloat(r0.floatingButton, "translationZ", new float[]{(float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(4.0f)}).setDuration(200));
            animator.addState(new int[0], ObjectAnimator.ofFloat(r0.floatingButton, "translationZ", new float[]{(float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(2.0f)}).setDuration(200));
            r0.floatingButton.setStateListAnimator(animator);
            r0.floatingButton.setOutlineProvider(new C13848());
        }
        contentView.addView(r0.floatingButton, LayoutHelper.createFrame(VERSION.SDK_INT >= 21 ? 56 : 60, VERSION.SDK_INT >= 21 ? 56.0f : 60.0f, (LocaleController.isRTL ? 3 : 5) | 80, LocaleController.isRTL ? 14.0f : 0.0f, 0.0f, LocaleController.isRTL ? 0.0f : 14.0f, 14.0f));
        r0.floatingButton.setOnClickListener(new C13859());
        r0.listView.setOnScrollListener(new OnScrollListener() {
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == 1 && DialogsActivity.this.searching && DialogsActivity.this.searchWas) {
                    AndroidUtilities.hideKeyboard(DialogsActivity.this.getParentActivity().getCurrentFocus());
                }
            }

            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int firstVisibleItem = DialogsActivity.this.layoutManager.findFirstVisibleItemPosition();
                int visibleItemCount = Math.abs(DialogsActivity.this.layoutManager.findLastVisibleItemPosition() - firstVisibleItem) + 1;
                int totalItemCount = recyclerView.getAdapter().getItemCount();
                if (DialogsActivity.this.searching && DialogsActivity.this.searchWas) {
                    if (visibleItemCount > 0 && DialogsActivity.this.layoutManager.findLastVisibleItemPosition() == totalItemCount - 1 && !DialogsActivity.this.dialogsSearchAdapter.isMessagesSearchEndReached()) {
                        DialogsActivity.this.dialogsSearchAdapter.loadMoreSearchMessages();
                    }
                    return;
                }
                boolean fromCache;
                if (visibleItemCount > 0 && DialogsActivity.this.layoutManager.findLastVisibleItemPosition() >= DialogsActivity.this.getDialogsArray().size() - 10) {
                    fromCache = MessagesController.getInstance(DialogsActivity.this.currentAccount).dialogsEndReached ^ true;
                    if (fromCache || !MessagesController.getInstance(DialogsActivity.this.currentAccount).serverDialogsEndReached) {
                        MessagesController.getInstance(DialogsActivity.this.currentAccount).loadDialogs(-1, 100, fromCache);
                    }
                }
                if (DialogsActivity.this.floatingButton.getVisibility() != 8) {
                    boolean goingDown;
                    fromCache = false;
                    View topChild = recyclerView.getChildAt(0);
                    int firstViewTop = 0;
                    if (topChild != null) {
                        firstViewTop = topChild.getTop();
                    }
                    boolean changed = true;
                    if (DialogsActivity.this.prevPosition == firstVisibleItem) {
                        int topDelta = DialogsActivity.this.prevTop - firstViewTop;
                        goingDown = firstViewTop < DialogsActivity.this.prevTop;
                        if (Math.abs(topDelta) > 1) {
                            fromCache = true;
                        }
                        changed = fromCache;
                    } else {
                        goingDown = firstVisibleItem > DialogsActivity.this.prevPosition;
                    }
                    fromCache = goingDown;
                    if (changed && DialogsActivity.this.scrollUpdated) {
                        DialogsActivity.this.hideFloatingButton(fromCache);
                    }
                    DialogsActivity.this.prevPosition = firstVisibleItem;
                    DialogsActivity.this.prevTop = firstViewTop;
                    DialogsActivity.this.scrollUpdated = true;
                }
            }
        });
        if (r0.searchString == null) {
            r0.dialogsAdapter = new DialogsAdapter(context2, r0.dialogsType, r0.onlySelect);
            if (AndroidUtilities.isTablet() && r0.openedDialogId != 0) {
                r0.dialogsAdapter.setOpenedDialogId(r0.openedDialogId);
            }
            r0.listView.setAdapter(r0.dialogsAdapter);
        }
        int type = 0;
        if (r0.searchString != null) {
            type = 2;
        } else if (!r0.onlySelect) {
            type = 1;
        }
        r0.dialogsSearchAdapter = new DialogsSearchAdapter(context2, type, r0.dialogsType);
        r0.dialogsSearchAdapter.setDelegate(new DialogsSearchAdapterDelegate() {
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

            public void needRemoveHint(final int did) {
                if (DialogsActivity.this.getParentActivity() != null && MessagesController.getInstance(DialogsActivity.this.currentAccount).getUser(Integer.valueOf(did)) != null) {
                    Builder builder = new Builder(DialogsActivity.this.getParentActivity());
                    builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                    builder.setMessage(LocaleController.formatString("ChatHintsDelete", R.string.ChatHintsDelete, ContactsController.formatName(user.first_name, user.last_name)));
                    builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            DataQuery.getInstance(DialogsActivity.this.currentAccount).removePeer(did);
                        }
                    });
                    builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                    DialogsActivity.this.showDialog(builder.create());
                }
            }
        });
        if (MessagesController.getInstance(r0.currentAccount).loadingDialogs && MessagesController.getInstance(r0.currentAccount).dialogs.isEmpty()) {
            r0.searchEmptyView.setVisibility(8);
            r0.listView.setEmptyView(r0.progressView);
        } else {
            r0.searchEmptyView.setVisibility(8);
            r0.progressView.setVisibility(8);
            r0.listView.setEmptyView(null);
        }
        if (r0.searchString != null) {
            r0.actionBar.openSearchField(r0.searchString);
        }
        if (!r0.onlySelect && r0.dialogsType == 0) {
            FragmentContextView fragmentLocationContextView = new FragmentContextView(context2, r0, true);
            contentView.addView(fragmentLocationContextView, LayoutHelper.createFrame(-1, 39.0f, 51, 0.0f, -36.0f, 0.0f, 0.0f));
            FragmentContextView fragmentContextView = new FragmentContextView(context2, r0, false);
            contentView.addView(fragmentContextView, LayoutHelper.createFrame(-1, 39.0f, 51, 0.0f, -36.0f, 0.0f, 0.0f));
            fragmentContextView.setAdditionalContextView(fragmentLocationContextView);
            fragmentLocationContextView.setAdditionalContextView(fragmentContextView);
        } else if (r0.dialogsType == 3 && r0.selectAlertString == null) {
            if (r0.commentView != null) {
                r0.commentView.onDestroy();
            }
            r0.commentView = new ChatActivityEnterView(getParentActivity(), contentView, null, false);
            r0.commentView.setAllowStickersAndGifs(false, false);
            r0.commentView.setForceShowSendButton(true, false);
            r0.commentView.setVisibility(8);
            contentView.addView(r0.commentView, LayoutHelper.createFrame(-1, -2, 83));
            r0.commentView.setDelegate(new ChatActivityEnterViewDelegate() {
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

                public void onStickersExpandedChange() {
                }

                public void onPreAudioVideoRecord() {
                }

                public void onTextChanged(CharSequence text, boolean bigChange) {
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
        return r0.fragmentView;
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
                    Builder builder;
                    Dialog create;
                    if (activity.shouldShowRequestPermissionRationale("android.permission.READ_CONTACTS")) {
                        builder = new Builder(activity);
                        builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                        builder.setMessage(LocaleController.getString("PermissionContacts", R.string.PermissionContacts));
                        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
                        create = builder.create();
                        this.permissionDialog = create;
                        showDialog(create);
                    } else if (activity.shouldShowRequestPermissionRationale("android.permission.WRITE_EXTERNAL_STORAGE")) {
                        builder = new Builder(activity);
                        builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                        builder.setMessage(LocaleController.getString("PermissionStorage", R.string.PermissionStorage));
                        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
                        create = builder.create();
                        this.permissionDialog = create;
                        showDialog(create);
                    } else {
                        askForPermissons();
                    }
                }
            }
        }
    }

    public void onPause() {
        super.onPause();
        if (this.commentView != null) {
            this.commentView.onResume();
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
                    animatorSet.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animation) {
                            DialogsActivity.this.commentView.setTag(Integer.valueOf(2));
                        }
                    });
                    animatorSet.start();
                    this.commentView.setTag(Integer.valueOf(1));
                }
                this.actionBar.setTitle(LocaleController.formatPluralString("Recipient", this.dialogsAdapter.getSelectedDialogs().size()));
            } else {
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
                    animatorArr[0] = ObjectAnimator.ofFloat(this.commentView, "translationY", new float[]{0.0f, (float) this.commentView.getMeasuredHeight()});
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
    }

    @TargetApi(23)
    private void askForPermissons() {
        Activity activity = getParentActivity();
        if (activity != null) {
            ArrayList<String> permissons = new ArrayList();
            if (activity.checkSelfPermission("android.permission.READ_CONTACTS") != 0) {
                permissons.add("android.permission.READ_CONTACTS");
                permissons.add("android.permission.WRITE_CONTACTS");
                permissons.add("android.permission.GET_ACCOUNTS");
            }
            if (activity.checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") != 0) {
                permissons.add("android.permission.READ_EXTERNAL_STORAGE");
                permissons.add("android.permission.WRITE_EXTERNAL_STORAGE");
            }
            try {
                activity.requestPermissions((String[]) permissons.toArray(new String[permissons.size()]), 1);
            } catch (Exception e) {
            }
        }
    }

    protected void onDialogDismiss(Dialog dialog) {
        super.onDialogDismiss(dialog);
        if (this.permissionDialog != null && dialog == this.permissionDialog && getParentActivity() != null) {
            askForPermissons();
        }
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (!this.onlySelect && this.floatingButton != null) {
            this.floatingButton.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
                public void onGlobalLayout() {
                    DialogsActivity.this.floatingButton.setTranslationY(DialogsActivity.this.floatingHidden ? (float) AndroidUtilities.dp(100.0f) : 0.0f);
                    DialogsActivity.this.floatingButton.setClickable(DialogsActivity.this.floatingHidden ^ 1);
                    if (DialogsActivity.this.floatingButton != null) {
                        DialogsActivity.this.floatingButton.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                }
            });
        }
    }

    public void onRequestPermissionsResultFragment(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1) {
            for (int a = 0; a < permissions.length; a++) {
                if (grantResults.length > a) {
                    if (grantResults[a] == 0) {
                        String str = permissions[a];
                        Object obj = -1;
                        int hashCode = str.hashCode();
                        if (hashCode != NUM) {
                            if (hashCode == NUM) {
                                if (str.equals("android.permission.READ_CONTACTS")) {
                                    obj = null;
                                }
                            }
                        } else if (str.equals("android.permission.WRITE_EXTERNAL_STORAGE")) {
                            obj = 1;
                        }
                        switch (obj) {
                            case null:
                                ContactsController.getInstance(this.currentAccount).forceImportContacts();
                                break;
                            case 1:
                                ImageLoader.getInstance().checkMediaPaths();
                                break;
                            default:
                                break;
                        }
                    }
                }
            }
        }
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.dialogsNeedReload) {
            if (this.dialogsAdapter != null) {
                if (this.dialogsAdapter.isDataSetChanged()) {
                    this.dialogsAdapter.notifyDataSetChanged();
                } else {
                    updateVisibleRows(2048);
                }
            }
            if (this.listView != null) {
                try {
                    if (MessagesController.getInstance(this.currentAccount).loadingDialogs && MessagesController.getInstance(this.currentAccount).dialogs.isEmpty()) {
                        this.searchEmptyView.setVisibility(8);
                        this.listView.setEmptyView(this.progressView);
                    }
                    this.progressView.setVisibility(8);
                    if (this.searching && this.searchWas) {
                        this.listView.setEmptyView(this.searchEmptyView);
                    }
                    this.searchEmptyView.setVisibility(8);
                    this.listView.setEmptyView(null);
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
            }
        } else if (id == NotificationCenter.emojiDidLoaded) {
            updateVisibleRows(0);
        } else if (id == NotificationCenter.updateInterfaces) {
            updateVisibleRows(((Integer) args[0]).intValue());
        } else if (id == NotificationCenter.appDidLogout) {
            dialogsLoaded[this.currentAccount] = false;
        } else if (id == NotificationCenter.encryptedChatUpdated) {
            updateVisibleRows(0);
        } else if (id == NotificationCenter.contactsDidLoaded) {
            updateVisibleRows(0);
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
        } else {
            if (!(id == NotificationCenter.messageReceivedByAck || id == NotificationCenter.messageReceivedByServer)) {
                if (id != NotificationCenter.messageSendError) {
                    if (id == NotificationCenter.didSetPasscode) {
                        updatePasscodeButton();
                        return;
                    } else if (id == NotificationCenter.needReloadRecentDialogsSearch) {
                        if (this.dialogsSearchAdapter != null) {
                            this.dialogsSearchAdapter.loadRecentSearch();
                            return;
                        }
                        return;
                    } else if (id == NotificationCenter.didLoadedReplyMessages) {
                        updateVisibleRows(32768);
                        return;
                    } else if (id == NotificationCenter.reloadHints && this.dialogsSearchAdapter != null) {
                        this.dialogsSearchAdapter.notifyDataSetChanged();
                        return;
                    } else {
                        return;
                    }
                }
            }
            updateVisibleRows(4096);
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
            } else {
                this.passcodeItem.setVisibility(0);
                if (SharedConfig.appLocked) {
                    this.passcodeItem.setIcon((int) R.drawable.lock_close);
                } else {
                    this.passcodeItem.setIcon((int) R.drawable.lock_open);
                }
            }
        }
    }

    private void hideFloatingButton(boolean hide) {
        if (this.floatingHidden != hide) {
            this.floatingHidden = hide;
            ImageView imageView = this.floatingButton;
            String str = "translationY";
            float[] fArr = new float[1];
            fArr[0] = this.floatingHidden ? (float) AndroidUtilities.dp(100.0f) : 0.0f;
            ObjectAnimator animator = ObjectAnimator.ofFloat(imageView, str, fArr).setDuration(300);
            animator.setInterpolator(this.floatingInterpolator);
            this.floatingButton.setClickable(hide ^ 1);
            animator.start();
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
                        boolean z = true;
                        if ((mask & 2048) != 0) {
                            cell.checkCurrentDialogIndex();
                            if (this.dialogsType == 0 && AndroidUtilities.isTablet()) {
                                if (cell.getDialogId() != this.openedDialogId) {
                                    z = false;
                                }
                                cell.setDialogSelected(z);
                            }
                        } else if ((mask & 512) == 0) {
                            cell.update(mask);
                        } else if (this.dialogsType == 0 && AndroidUtilities.isTablet()) {
                            if (cell.getDialogId() != this.openedDialogId) {
                                z = false;
                            }
                            cell.setDialogSelected(z);
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

    private void didSelectResult(final long dialog_id, boolean useAlert, boolean param) {
        if (this.addToGroupAlertString == null && ((int) dialog_id) < 0) {
            Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-((int) dialog_id)));
            if (ChatObject.isChannel(chat) && !chat.megagroup && (this.cantSendToChannels || !ChatObject.isCanWriteToChannel(-((int) dialog_id), this.currentAccount))) {
                Builder builder = new Builder(getParentActivity());
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
            } else {
                finishFragment();
            }
        } else if (getParentActivity() != null) {
            Builder builder2 = new Builder(getParentActivity());
            builder2.setTitle(LocaleController.getString("AppName", R.string.AppName));
            int lower_part = (int) dialog_id;
            int high_id = (int) (dialog_id >> 32);
            if (lower_part == 0) {
                if (MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(MessagesController.getInstance(this.currentAccount).getEncryptedChat(Integer.valueOf(high_id)).user_id)) != null) {
                    builder2.setMessage(LocaleController.formatStringSimple(this.selectAlertString, UserObject.getUserName(user)));
                } else {
                    return;
                }
            } else if (high_id == 1) {
                if (MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(lower_part)) != null) {
                    builder2.setMessage(LocaleController.formatStringSimple(this.selectAlertStringGroup, chat.title));
                } else {
                    return;
                }
            } else if (lower_part == UserConfig.getInstance(this.currentAccount).getClientUserId()) {
                builder2.setMessage(LocaleController.formatStringSimple(this.selectAlertStringGroup, LocaleController.getString("SavedMessages", R.string.SavedMessages)));
            } else if (lower_part > 0) {
                if (MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(lower_part)) != null) {
                    builder2.setMessage(LocaleController.formatStringSimple(this.selectAlertString, UserObject.getUserName(user)));
                } else {
                    return;
                }
            } else if (lower_part < 0) {
                if (MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-lower_part)) != null) {
                    if (this.addToGroupAlertString != null) {
                        builder2.setMessage(LocaleController.formatStringSimple(this.addToGroupAlertString, chat.title));
                    } else {
                        builder2.setMessage(LocaleController.formatStringSimple(this.selectAlertStringGroup, chat.title));
                    }
                } else {
                    return;
                }
            }
            builder2.setPositiveButton(LocaleController.getString("OK", R.string.OK), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    DialogsActivity.this.didSelectResult(dialog_id, false, false);
                }
            });
            builder2.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
            showDialog(builder2.create());
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        ThemeDescriptionDelegate сellDelegate = new ThemeDescriptionDelegate() {
            public void didSetColor() {
                int a;
                View child;
                int a2 = 0;
                if (DialogsActivity.this.listView != null) {
                    int count = DialogsActivity.this.listView.getChildCount();
                    for (a = 0; a < count; a++) {
                        child = DialogsActivity.this.listView.getChildAt(a);
                        if (child instanceof ProfileSearchCell) {
                            ((ProfileSearchCell) child).update(0);
                        } else if (child instanceof DialogCell) {
                            ((DialogCell) child).update(0);
                        }
                    }
                }
                if (DialogsActivity.this.dialogsSearchAdapter != null) {
                    RecyclerListView recyclerListView = DialogsActivity.this.dialogsSearchAdapter.getInnerListView();
                    if (recyclerListView != null) {
                        a = recyclerListView.getChildCount();
                        while (a2 < a) {
                            child = recyclerListView.getChildAt(a2);
                            if (child instanceof HintDialogCell) {
                                ((HintDialogCell) child).update();
                            }
                            a2++;
                        }
                    }
                }
            }
        };
        ThemeDescription[] themeDescriptionArr = new ThemeDescription[TsExtractor.TS_STREAM_TYPE_SPLICE_INFO];
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
        themeDescriptionArr[17] = new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class, ProfileSearchCell.class}, null, new Drawable[]{Theme.avatar_photoDrawable, Theme.avatar_broadcastDrawable, Theme.avatar_savedDrawable}, null, Theme.key_avatar_text);
        themeDescriptionArr[18] = new ThemeDescription(null, 0, null, null, null, сellDelegate, Theme.key_avatar_backgroundRed);
        themeDescriptionArr[19] = new ThemeDescription(null, 0, null, null, null, сellDelegate, Theme.key_avatar_backgroundOrange);
        ThemeDescriptionDelegate themeDescriptionDelegate = сellDelegate;
        themeDescriptionArr[20] = new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundViolet);
        themeDescriptionArr[21] = new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundGreen);
        themeDescriptionArr[22] = new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundCyan);
        themeDescriptionArr[23] = new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundBlue);
        themeDescriptionArr[24] = new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundPink);
        themeDescriptionArr[25] = new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundSaved);
        themeDescriptionArr[26] = new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class}, Theme.dialogs_countPaint, null, null, Theme.key_chats_unreadCounter);
        themeDescriptionArr[27] = new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class}, Theme.dialogs_countGrayPaint, null, null, Theme.key_chats_unreadCounterMuted);
        themeDescriptionArr[28] = new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class}, Theme.dialogs_countTextPaint, null, null, Theme.key_chats_unreadCounterText);
        themeDescriptionArr[29] = new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class, ProfileSearchCell.class}, Theme.dialogs_namePaint, null, null, Theme.key_chats_name);
        themeDescriptionArr[30] = new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class, ProfileSearchCell.class}, Theme.dialogs_nameEncryptedPaint, null, null, Theme.key_chats_secretName);
        themeDescriptionArr[31] = new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class, ProfileSearchCell.class}, null, new Drawable[]{Theme.dialogs_lockDrawable}, null, Theme.key_chats_secretIcon);
        themeDescriptionArr[32] = new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class, ProfileSearchCell.class}, null, new Drawable[]{Theme.dialogs_groupDrawable, Theme.dialogs_broadcastDrawable, Theme.dialogs_botDrawable}, null, Theme.key_chats_nameIcon);
        themeDescriptionArr[33] = new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class}, null, new Drawable[]{Theme.dialogs_pinnedDrawable}, null, Theme.key_chats_pinnedIcon);
        themeDescriptionArr[34] = new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class}, Theme.dialogs_messagePaint, null, null, Theme.key_chats_message);
        ThemeDescriptionDelegate themeDescriptionDelegate2 = сellDelegate;
        themeDescriptionArr[35] = new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate2, Theme.key_chats_nameMessage);
        themeDescriptionArr[36] = new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate2, Theme.key_chats_draft);
        themeDescriptionArr[37] = new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate2, Theme.key_chats_attachMessage);
        themeDescriptionArr[38] = new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class}, Theme.dialogs_messagePrintingPaint, null, null, Theme.key_chats_actionMessage);
        themeDescriptionArr[39] = new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class}, Theme.dialogs_timePaint, null, null, Theme.key_chats_date);
        themeDescriptionArr[40] = new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class}, Theme.dialogs_pinnedPaint, null, null, Theme.key_chats_pinnedOverlay);
        themeDescriptionArr[41] = new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class}, Theme.dialogs_tabletSeletedPaint, null, null, Theme.key_chats_tabletSelectedOverlay);
        themeDescriptionArr[42] = new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class}, null, new Drawable[]{Theme.dialogs_checkDrawable, Theme.dialogs_halfCheckDrawable}, null, Theme.key_chats_sentCheck);
        themeDescriptionArr[43] = new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class}, null, new Drawable[]{Theme.dialogs_clockDrawable}, null, Theme.key_chats_sentClock);
        themeDescriptionArr[44] = new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class}, Theme.dialogs_errorPaint, null, null, Theme.key_chats_sentError);
        themeDescriptionArr[45] = new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class}, null, new Drawable[]{Theme.dialogs_errorDrawable}, null, Theme.key_chats_sentErrorIcon);
        themeDescriptionArr[46] = new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class, ProfileSearchCell.class}, null, new Drawable[]{Theme.dialogs_verifiedCheckDrawable}, null, Theme.key_chats_verifiedCheck);
        themeDescriptionArr[47] = new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class, ProfileSearchCell.class}, null, new Drawable[]{Theme.dialogs_verifiedDrawable}, null, Theme.key_chats_verifiedBackground);
        themeDescriptionArr[48] = new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class}, null, new Drawable[]{Theme.dialogs_muteDrawable}, null, Theme.key_chats_muteIcon);
        themeDescriptionArr[49] = new ThemeDescription(this.sideMenu, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_chats_menuBackground);
        themeDescriptionArr[50] = new ThemeDescription(this.sideMenu, 0, new Class[]{DrawerProfileCell.class}, null, null, null, Theme.key_chats_menuName);
        themeDescriptionArr[51] = new ThemeDescription(this.sideMenu, 0, new Class[]{DrawerProfileCell.class}, null, null, null, Theme.key_chats_menuPhone);
        themeDescriptionArr[52] = new ThemeDescription(this.sideMenu, 0, new Class[]{DrawerProfileCell.class}, null, null, null, Theme.key_chats_menuPhoneCats);
        themeDescriptionArr[53] = new ThemeDescription(this.sideMenu, 0, new Class[]{DrawerProfileCell.class}, null, null, null, Theme.key_chats_menuCloudBackgroundCats);
        themeDescriptionArr[54] = new ThemeDescription(this.sideMenu, 0, new Class[]{DrawerProfileCell.class}, null, null, null, Theme.key_chat_serviceBackground);
        themeDescriptionArr[55] = new ThemeDescription(this.sideMenu, 0, new Class[]{DrawerProfileCell.class}, null, null, null, Theme.key_chats_menuTopShadow);
        themeDescriptionArr[56] = new ThemeDescription(this.sideMenu, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{DrawerProfileCell.class}, null, null, null, Theme.key_avatar_backgroundActionBarBlue);
        themeDescriptionArr[57] = new ThemeDescription(this.sideMenu, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{DrawerActionCell.class}, new String[]{"textView"}, null, null, null, Theme.key_chats_menuItemIcon);
        themeDescriptionArr[58] = new ThemeDescription(this.sideMenu, 0, new Class[]{DrawerActionCell.class}, new String[]{"textView"}, null, null, null, Theme.key_chats_menuItemText);
        themeDescriptionArr[59] = new ThemeDescription(this.sideMenu, 0, new Class[]{DrawerUserCell.class}, new String[]{"textView"}, null, null, null, Theme.key_chats_menuItemText);
        themeDescriptionArr[60] = new ThemeDescription(this.sideMenu, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{DrawerUserCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_chats_unreadCounterText);
        themeDescriptionArr[61] = new ThemeDescription(this.sideMenu, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{DrawerUserCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_chats_unreadCounter);
        themeDescriptionArr[62] = new ThemeDescription(this.sideMenu, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{DrawerUserCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_chats_menuBackground);
        themeDescriptionArr[63] = new ThemeDescription(this.sideMenu, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{DrawerAddCell.class}, new String[]{"textView"}, null, null, null, Theme.key_chats_menuItemIcon);
        themeDescriptionArr[64] = new ThemeDescription(this.sideMenu, 0, new Class[]{DrawerAddCell.class}, new String[]{"textView"}, null, null, null, Theme.key_chats_menuItemText);
        themeDescriptionArr[65] = new ThemeDescription(this.sideMenu, 0, new Class[]{DividerCell.class}, Theme.dividerPaint, null, null, Theme.key_divider);
        themeDescriptionArr[66] = new ThemeDescription(this.listView, 0, new Class[]{LoadingCell.class}, new String[]{"progressBar"}, null, null, null, Theme.key_progressCircle);
        themeDescriptionArr[67] = new ThemeDescription(this.listView, 0, new Class[]{ProfileSearchCell.class}, Theme.dialogs_offlinePaint, null, null, Theme.key_windowBackgroundWhiteGrayText3);
        themeDescriptionArr[68] = new ThemeDescription(this.listView, 0, new Class[]{ProfileSearchCell.class}, Theme.dialogs_onlinePaint, null, null, Theme.key_windowBackgroundWhiteBlueText3);
        themeDescriptionArr[69] = new ThemeDescription(this.listView, 0, new Class[]{GraySectionCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText2);
        themeDescriptionArr[70] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{GraySectionCell.class}, null, null, null, Theme.key_graySection);
        themeDescriptionArr[71] = new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{HashtagSearchCell.class}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        themeDescriptionArr[72] = new ThemeDescription(this.progressView, ThemeDescription.FLAG_PROGRESSBAR, null, null, null, null, Theme.key_progressCircle);
        View view = null;
        themeDescriptionArr[73] = new ThemeDescription(this.dialogsSearchAdapter != null ? r0.dialogsSearchAdapter.getInnerListView() : null, 0, new Class[]{HintDialogCell.class}, Theme.dialogs_countPaint, null, null, Theme.key_chats_unreadCounter);
        themeDescriptionArr[74] = new ThemeDescription(r0.dialogsSearchAdapter != null ? r0.dialogsSearchAdapter.getInnerListView() : null, 0, new Class[]{HintDialogCell.class}, Theme.dialogs_countGrayPaint, null, null, Theme.key_chats_unreadCounterMuted);
        themeDescriptionArr[75] = new ThemeDescription(r0.dialogsSearchAdapter != null ? r0.dialogsSearchAdapter.getInnerListView() : null, 0, new Class[]{HintDialogCell.class}, Theme.dialogs_countTextPaint, null, null, Theme.key_chats_unreadCounterText);
        if (r0.dialogsSearchAdapter != null) {
            view = r0.dialogsSearchAdapter.getInnerListView();
        }
        themeDescriptionArr[76] = new ThemeDescription(view, 0, new Class[]{HintDialogCell.class}, new String[]{"nameTextView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        themeDescriptionArr[77] = new ThemeDescription(r0.fragmentView, ThemeDescription.FLAG_BACKGROUND | ThemeDescription.FLAG_CHECKTAG, new Class[]{FragmentContextView.class}, new String[]{"frameLayout"}, null, null, null, Theme.key_inappPlayerBackground);
        themeDescriptionArr[78] = new ThemeDescription(r0.fragmentView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{FragmentContextView.class}, new String[]{"playButton"}, null, null, null, Theme.key_inappPlayerPlayPause);
        themeDescriptionArr[79] = new ThemeDescription(r0.fragmentView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{FragmentContextView.class}, new String[]{"titleTextView"}, null, null, null, Theme.key_inappPlayerTitle);
        themeDescriptionArr[80] = new ThemeDescription(r0.fragmentView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_FASTSCROLL, new Class[]{FragmentContextView.class}, new String[]{"titleTextView"}, null, null, null, Theme.key_inappPlayerPerformer);
        themeDescriptionArr[81] = new ThemeDescription(r0.fragmentView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{FragmentContextView.class}, new String[]{"closeButton"}, null, null, null, Theme.key_inappPlayerClose);
        themeDescriptionArr[82] = new ThemeDescription(r0.fragmentView, ThemeDescription.FLAG_BACKGROUND | ThemeDescription.FLAG_CHECKTAG, new Class[]{FragmentContextView.class}, new String[]{"frameLayout"}, null, null, null, Theme.key_returnToCallBackground);
        themeDescriptionArr[83] = new ThemeDescription(r0.fragmentView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{FragmentContextView.class}, new String[]{"titleTextView"}, null, null, null, Theme.key_returnToCallText);
        themeDescriptionArr[84] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogBackground);
        themeDescriptionArr[85] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogBackgroundGray);
        themeDescriptionArr[86] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogTextBlack);
        themeDescriptionArr[87] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogTextLink);
        themeDescriptionArr[88] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogLinkSelection);
        themeDescriptionArr[89] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogTextBlue);
        themeDescriptionArr[90] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogTextBlue2);
        themeDescriptionArr[91] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogTextBlue3);
        themeDescriptionArr[92] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogTextBlue4);
        themeDescriptionArr[93] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogTextRed);
        themeDescriptionArr[94] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogTextGray);
        themeDescriptionArr[95] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogTextGray2);
        themeDescriptionArr[96] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogTextGray3);
        themeDescriptionArr[97] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogTextGray4);
        themeDescriptionArr[98] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogIcon);
        themeDescriptionArr[99] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogTextHint);
        themeDescriptionArr[100] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogInputField);
        themeDescriptionArr[101] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogInputFieldActivated);
        themeDescriptionArr[102] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogCheckboxSquareBackground);
        themeDescriptionArr[103] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogCheckboxSquareCheck);
        themeDescriptionArr[104] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogCheckboxSquareUnchecked);
        themeDescriptionArr[105] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogCheckboxSquareDisabled);
        themeDescriptionArr[106] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogRadioBackground);
        themeDescriptionArr[107] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogRadioBackgroundChecked);
        themeDescriptionArr[108] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogProgressCircle);
        themeDescriptionArr[109] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogButton);
        themeDescriptionArr[110] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogButtonSelector);
        themeDescriptionArr[111] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogScrollGlow);
        themeDescriptionArr[112] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogRoundCheckBox);
        themeDescriptionArr[113] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogRoundCheckBoxCheck);
        themeDescriptionArr[114] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogBadgeBackground);
        themeDescriptionArr[115] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogBadgeText);
        themeDescriptionArr[116] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogLineProgress);
        themeDescriptionArr[117] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogLineProgressBackground);
        themeDescriptionArr[118] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogGrayLine);
        themeDescriptionArr[119] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_player_actionBar);
        themeDescriptionArr[120] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_player_actionBarSelector);
        themeDescriptionArr[121] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_player_actionBarTitle);
        themeDescriptionArr[122] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_player_actionBarTop);
        themeDescriptionArr[123] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_player_actionBarSubtitle);
        themeDescriptionArr[124] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_player_actionBarItems);
        themeDescriptionArr[125] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_player_background);
        themeDescriptionArr[126] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_player_time);
        themeDescriptionArr[127] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_player_progressBackground);
        themeDescriptionArr[128] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_player_progressCachedBackground);
        themeDescriptionArr[TsExtractor.TS_STREAM_TYPE_AC3] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_player_progress);
        themeDescriptionArr[TsExtractor.TS_STREAM_TYPE_HDMV_DTS] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_player_placeholder);
        themeDescriptionArr[131] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_player_placeholderBackground);
        themeDescriptionArr[132] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_player_button);
        themeDescriptionArr[133] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_player_buttonActive);
        return themeDescriptionArr;
    }
}
