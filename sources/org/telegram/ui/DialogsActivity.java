package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.annotation.SuppressLint;
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
import org.telegram.messenger.C0446R;
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

        public void onClick(View view) {
            view = new Bundle();
            view.putBoolean("destroyAfterSelect", true);
            DialogsActivity.this.presentFragment(new ContactsActivity(view));
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
            editText = editText.getText().toString();
            if (editText.length() != 0 || (DialogsActivity.this.dialogsSearchAdapter != null && DialogsActivity.this.dialogsSearchAdapter.hasRecentRearch())) {
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
                DialogsActivity.this.dialogsSearchAdapter.searchDialogs(editText);
            }
        }
    }

    /* renamed from: org.telegram.ui.DialogsActivity$3 */
    class C21283 extends ActionBarMenuOnItemClick {
        C21283() {
        }

        public void onItemClick(int i) {
            if (i == -1) {
                if (DialogsActivity.this.onlySelect != 0) {
                    DialogsActivity.this.finishFragment();
                } else if (DialogsActivity.this.parentLayout != 0) {
                    DialogsActivity.this.parentLayout.getDrawerLayoutContainer().openDrawer(false);
                }
            } else if (i == 1) {
                SharedConfig.appLocked ^= 1;
                SharedConfig.saveConfig();
                DialogsActivity.this.updatePasscodeButton();
            } else if (i >= 10 && i < 13 && DialogsActivity.this.getParentActivity() != null) {
                DialogsActivityDelegate access$1800 = DialogsActivity.this.delegate;
                LaunchActivity launchActivity = (LaunchActivity) DialogsActivity.this.getParentActivity();
                launchActivity.switchToAccount(i - 10, true);
                i = new DialogsActivity(DialogsActivity.this.arguments);
                i.setDelegate(access$1800);
                launchActivity.presentFragment(i, false, true);
            }
        }
    }

    /* renamed from: org.telegram.ui.DialogsActivity$6 */
    class C21306 implements OnItemClickListener {
        C21306() {
        }

        public void onItemClick(View view, int i) {
            if (!(DialogsActivity.this.listView == null || DialogsActivity.this.listView.getAdapter() == null)) {
                if (DialogsActivity.this.getParentActivity() != null) {
                    long j;
                    int i2;
                    int i3;
                    Chat chat;
                    Adapter adapter = DialogsActivity.this.listView.getAdapter();
                    if (adapter == DialogsActivity.this.dialogsAdapter) {
                        i = DialogsActivity.this.dialogsAdapter.getItem(i);
                        if (i instanceof TL_dialog) {
                            j = ((TL_dialog) i).id;
                        } else if (i instanceof TL_recentMeUrlChat) {
                            j = (long) (-((TL_recentMeUrlChat) i).chat_id);
                        } else if (i instanceof TL_recentMeUrlUser) {
                            j = (long) ((TL_recentMeUrlUser) i).user_id;
                        } else if (i instanceof TL_recentMeUrlChatInvite) {
                            TL_recentMeUrlChatInvite tL_recentMeUrlChatInvite = (TL_recentMeUrlChatInvite) i;
                            ChatInvite chatInvite = tL_recentMeUrlChatInvite.chat_invite;
                            if ((chatInvite.chat == null && (!chatInvite.channel || chatInvite.megagroup)) || (chatInvite.chat != null && (!ChatObject.isChannel(chatInvite.chat) || chatInvite.chat.megagroup))) {
                                view = tL_recentMeUrlChatInvite.url;
                                i = view.indexOf(47);
                                if (i > 0) {
                                    view = view.substring(i + 1);
                                }
                                DialogsActivity.this.showDialog(new JoinGroupAlert(DialogsActivity.this.getParentActivity(), chatInvite, view, DialogsActivity.this));
                                return;
                            } else if (chatInvite.chat != 0) {
                                j = (long) (-chatInvite.chat.id);
                            } else {
                                return;
                            }
                        } else if ((i instanceof TL_recentMeUrlStickerSet) != null) {
                            view = ((TL_recentMeUrlStickerSet) i).set.set;
                            InputStickerSet tL_inputStickerSetID = new TL_inputStickerSetID();
                            tL_inputStickerSetID.id = view.id;
                            tL_inputStickerSetID.access_hash = view.access_hash;
                            DialogsActivity.this.showDialog(new StickersAlert(DialogsActivity.this.getParentActivity(), DialogsActivity.this, tL_inputStickerSetID, null, null));
                            return;
                        } else if ((i instanceof TL_recentMeUrlUnknown) == null) {
                            return;
                        } else {
                            return;
                        }
                    }
                    if (adapter == DialogsActivity.this.dialogsSearchAdapter) {
                        i = DialogsActivity.this.dialogsSearchAdapter.getItem(i);
                        if (i instanceof User) {
                            User user = (User) i;
                            j = (long) user.id;
                            if (!DialogsActivity.this.onlySelect) {
                                DialogsActivity.this.dialogsSearchAdapter.putRecentSearch(j, user);
                            }
                        } else if (i instanceof Chat) {
                            Chat chat2 = (Chat) i;
                            if (chat2.id > 0) {
                                j = (long) (-chat2.id);
                            } else {
                                j = AndroidUtilities.makeBroadcastId(chat2.id);
                            }
                            if (!DialogsActivity.this.onlySelect) {
                                DialogsActivity.this.dialogsSearchAdapter.putRecentSearch(j, chat2);
                            }
                        } else if (i instanceof EncryptedChat) {
                            EncryptedChat encryptedChat = (EncryptedChat) i;
                            j = ((long) encryptedChat.id) << 32;
                            if (!DialogsActivity.this.onlySelect) {
                                DialogsActivity.this.dialogsSearchAdapter.putRecentSearch(j, encryptedChat);
                            }
                        } else if (i instanceof MessageObject) {
                            MessageObject messageObject = (MessageObject) i;
                            j = messageObject.getDialogId();
                            i = messageObject.getId();
                            DialogsActivity.this.dialogsSearchAdapter.addHashtagsFromMessage(DialogsActivity.this.dialogsSearchAdapter.getLastSearchString());
                            if (j == 0) {
                                if (DialogsActivity.this.onlySelect) {
                                    view = new Bundle();
                                    i2 = (int) j;
                                    i3 = (int) (j >> 32);
                                    if (i2 != 0) {
                                        view.putInt("enc_id", i3);
                                    } else if (i3 == 1) {
                                        view.putInt("chat_id", i2);
                                    } else if (i2 > 0) {
                                        view.putInt("user_id", i2);
                                    } else if (i2 < 0) {
                                        if (i != 0) {
                                            chat = MessagesController.getInstance(DialogsActivity.this.currentAccount).getChat(Integer.valueOf(-i2));
                                            if (!(chat == null || chat.migrated_to == null)) {
                                                view.putInt("migrated_to", i2);
                                                i2 = -chat.migrated_to.channel_id;
                                            }
                                        }
                                        view.putInt("chat_id", -i2);
                                    }
                                    if (i != 0) {
                                        view.putInt("message_id", i);
                                    } else if (DialogsActivity.this.actionBar != 0) {
                                        DialogsActivity.this.actionBar.closeSearchField();
                                    }
                                    if (AndroidUtilities.isTablet() != 0) {
                                        if (DialogsActivity.this.openedDialogId != j && adapter != DialogsActivity.this.dialogsSearchAdapter) {
                                            return;
                                        }
                                        if (DialogsActivity.this.dialogsAdapter != 0) {
                                            DialogsActivity.this.dialogsAdapter.setOpenedDialogId(DialogsActivity.this.openedDialogId = j);
                                            DialogsActivity.this.updateVisibleRows(512);
                                        }
                                    }
                                    if (DialogsActivity.this.searchString == 0) {
                                        if (MessagesController.getInstance(DialogsActivity.this.currentAccount).checkCanOpenChat(view, DialogsActivity.this) != 0) {
                                            NotificationCenter.getInstance(DialogsActivity.this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                                            DialogsActivity.this.presentFragment(new ChatActivity(view));
                                        }
                                    } else if (MessagesController.getInstance(DialogsActivity.this.currentAccount).checkCanOpenChat(view, DialogsActivity.this) != 0) {
                                        DialogsActivity.this.presentFragment(new ChatActivity(view));
                                    }
                                } else if (DialogsActivity.this.dialogsAdapter.hasSelectedDialogs() == 0) {
                                    DialogsActivity.this.dialogsAdapter.addOrRemoveSelectedDialog(j, view);
                                    DialogsActivity.this.updateSelectedCount();
                                } else {
                                    DialogsActivity.this.didSelectResult(j, true, false);
                                }
                            }
                        } else if (i instanceof String) {
                            DialogsActivity.this.actionBar.openSearchField((String) i);
                        }
                    }
                    j = 0;
                    i = 0;
                    if (j == 0) {
                        if (DialogsActivity.this.onlySelect) {
                            view = new Bundle();
                            i2 = (int) j;
                            i3 = (int) (j >> 32);
                            if (i2 != 0) {
                                view.putInt("enc_id", i3);
                            } else if (i3 == 1) {
                                view.putInt("chat_id", i2);
                            } else if (i2 > 0) {
                                view.putInt("user_id", i2);
                            } else if (i2 < 0) {
                                if (i != 0) {
                                    chat = MessagesController.getInstance(DialogsActivity.this.currentAccount).getChat(Integer.valueOf(-i2));
                                    view.putInt("migrated_to", i2);
                                    i2 = -chat.migrated_to.channel_id;
                                }
                                view.putInt("chat_id", -i2);
                            }
                            if (i != 0) {
                                view.putInt("message_id", i);
                            } else if (DialogsActivity.this.actionBar != 0) {
                                DialogsActivity.this.actionBar.closeSearchField();
                            }
                            if (AndroidUtilities.isTablet() != 0) {
                                if (DialogsActivity.this.openedDialogId != j) {
                                }
                                if (DialogsActivity.this.dialogsAdapter != 0) {
                                    DialogsActivity.this.dialogsAdapter.setOpenedDialogId(DialogsActivity.this.openedDialogId = j);
                                    DialogsActivity.this.updateVisibleRows(512);
                                }
                            }
                            if (DialogsActivity.this.searchString == 0) {
                                if (MessagesController.getInstance(DialogsActivity.this.currentAccount).checkCanOpenChat(view, DialogsActivity.this) != 0) {
                                    DialogsActivity.this.presentFragment(new ChatActivity(view));
                                }
                            } else if (MessagesController.getInstance(DialogsActivity.this.currentAccount).checkCanOpenChat(view, DialogsActivity.this) != 0) {
                                NotificationCenter.getInstance(DialogsActivity.this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                                DialogsActivity.this.presentFragment(new ChatActivity(view));
                            }
                        } else if (DialogsActivity.this.dialogsAdapter.hasSelectedDialogs() == 0) {
                            DialogsActivity.this.didSelectResult(j, true, false);
                        } else {
                            DialogsActivity.this.dialogsAdapter.addOrRemoveSelectedDialog(j, view);
                            DialogsActivity.this.updateSelectedCount();
                        }
                    }
                }
            }
        }
    }

    /* renamed from: org.telegram.ui.DialogsActivity$7 */
    class C21317 implements OnItemLongClickListener {

        /* renamed from: org.telegram.ui.DialogsActivity$7$1 */
        class C13781 implements DialogInterface.OnClickListener {
            C13781() {
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                if (DialogsActivity.this.dialogsSearchAdapter.isRecentSearchDisplayed() != null) {
                    DialogsActivity.this.dialogsSearchAdapter.clearRecentSearch();
                } else {
                    DialogsActivity.this.dialogsSearchAdapter.clearRecentHashtags();
                }
            }
        }

        C21317() {
        }

        public boolean onItemClick(View view, int i) {
            int i2 = i;
            if (DialogsActivity.this.getParentActivity() == null) {
                return false;
            }
            if (DialogsActivity.this.listView.getAdapter() == DialogsActivity.this.dialogsSearchAdapter) {
                if (!(DialogsActivity.this.dialogsSearchAdapter.getItem(i2) instanceof String)) {
                    if (!DialogsActivity.this.dialogsSearchAdapter.isRecentSearchDisplayed()) {
                        return false;
                    }
                }
                Builder builder = new Builder(DialogsActivity.this.getParentActivity());
                builder.setTitle(LocaleController.getString("AppName", C0446R.string.AppName));
                builder.setMessage(LocaleController.getString("ClearSearch", C0446R.string.ClearSearch));
                builder.setPositiveButton(LocaleController.getString("ClearButton", C0446R.string.ClearButton).toUpperCase(), new C13781());
                builder.setNegativeButton(LocaleController.getString("Cancel", C0446R.string.Cancel), null);
                DialogsActivity.this.showDialog(builder.create());
                return true;
            }
            ArrayList access$3400 = DialogsActivity.this.getDialogsArray();
            if (i2 >= 0) {
                if (i2 < access$3400.size()) {
                    TL_dialog tL_dialog = (TL_dialog) access$3400.get(i2);
                    if (DialogsActivity.this.onlySelect) {
                        if (DialogsActivity.this.dialogsType == 3) {
                            if (DialogsActivity.this.selectAlertString == null) {
                                DialogsActivity.this.dialogsAdapter.addOrRemoveSelectedDialog(tL_dialog.id, view);
                                DialogsActivity.this.updateSelectedCount();
                            }
                        }
                        return false;
                    }
                    DialogsActivity.this.selectedDialog = tL_dialog.id;
                    final boolean z = tL_dialog.pinned;
                    BottomSheet.Builder builder2 = new BottomSheet.Builder(DialogsActivity.this.getParentActivity());
                    int access$3700 = (int) DialogsActivity.this.selectedDialog;
                    int access$37002 = (int) (DialogsActivity.this.selectedDialog >> 32);
                    String str;
                    if (DialogObject.isChannel(tL_dialog)) {
                        CharSequence[] charSequenceArr;
                        final Chat chat = MessagesController.getInstance(DialogsActivity.this.currentAccount).getChat(Integer.valueOf(-access$3700));
                        int[] iArr = new int[3];
                        iArr[0] = tL_dialog.pinned ? C0446R.drawable.chats_unpin : C0446R.drawable.chats_pin;
                        iArr[1] = C0446R.drawable.chats_clear;
                        iArr[2] = C0446R.drawable.chats_leave;
                        if (chat == null || !chat.megagroup) {
                            charSequenceArr = new CharSequence[3];
                            if (!tL_dialog.pinned) {
                                if (!MessagesController.getInstance(DialogsActivity.this.currentAccount).canPinDialog(false)) {
                                    str = null;
                                    charSequenceArr[0] = str;
                                    charSequenceArr[1] = LocaleController.getString("ClearHistoryCache", C0446R.string.ClearHistoryCache);
                                    charSequenceArr[2] = LocaleController.getString("LeaveChannelMenu", C0446R.string.LeaveChannelMenu);
                                }
                            }
                            str = tL_dialog.pinned ? LocaleController.getString("UnpinFromTop", C0446R.string.UnpinFromTop) : LocaleController.getString("PinToTop", C0446R.string.PinToTop);
                            charSequenceArr[0] = str;
                            charSequenceArr[1] = LocaleController.getString("ClearHistoryCache", C0446R.string.ClearHistoryCache);
                            charSequenceArr[2] = LocaleController.getString("LeaveChannelMenu", C0446R.string.LeaveChannelMenu);
                        } else {
                            charSequenceArr = new CharSequence[3];
                            if (!tL_dialog.pinned) {
                                if (!MessagesController.getInstance(DialogsActivity.this.currentAccount).canPinDialog(false)) {
                                    str = null;
                                    charSequenceArr[0] = str;
                                    charSequenceArr[1] = TextUtils.isEmpty(chat.username) ? LocaleController.getString("ClearHistory", C0446R.string.ClearHistory) : LocaleController.getString("ClearHistoryCache", C0446R.string.ClearHistoryCache);
                                    charSequenceArr[2] = LocaleController.getString("LeaveMegaMenu", C0446R.string.LeaveMegaMenu);
                                }
                            }
                            str = tL_dialog.pinned ? LocaleController.getString("UnpinFromTop", C0446R.string.UnpinFromTop) : LocaleController.getString("PinToTop", C0446R.string.PinToTop);
                            charSequenceArr[0] = str;
                            if (TextUtils.isEmpty(chat.username)) {
                            }
                            charSequenceArr[1] = TextUtils.isEmpty(chat.username) ? LocaleController.getString("ClearHistory", C0446R.string.ClearHistory) : LocaleController.getString("ClearHistoryCache", C0446R.string.ClearHistoryCache);
                            charSequenceArr[2] = LocaleController.getString("LeaveMegaMenu", C0446R.string.LeaveMegaMenu);
                        }
                        builder2.setItems(charSequenceArr, iArr, new DialogInterface.OnClickListener() {

                            /* renamed from: org.telegram.ui.DialogsActivity$7$2$1 */
                            class C13791 implements DialogInterface.OnClickListener {
                                C13791() {
                                }

                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if (chat == null || chat.megagroup == null || TextUtils.isEmpty(chat.username) == null) {
                                        MessagesController.getInstance(DialogsActivity.this.currentAccount).deleteDialog(DialogsActivity.this.selectedDialog, 2);
                                    } else {
                                        MessagesController.getInstance(DialogsActivity.this.currentAccount).deleteDialog(DialogsActivity.this.selectedDialog, 1);
                                    }
                                }
                            }

                            /* renamed from: org.telegram.ui.DialogsActivity$7$2$2 */
                            class C13802 implements DialogInterface.OnClickListener {
                                C13802() {
                                }

                                public void onClick(DialogInterface dialogInterface, int i) {
                                    MessagesController.getInstance(DialogsActivity.this.currentAccount).deleteUserFromChat((int) (-DialogsActivity.this.selectedDialog), UserConfig.getInstance(DialogsActivity.this.currentAccount).getCurrentUser(), null);
                                    if (AndroidUtilities.isTablet() != null) {
                                        NotificationCenter.getInstance(DialogsActivity.this.currentAccount).postNotificationName(NotificationCenter.closeChats, Long.valueOf(DialogsActivity.this.selectedDialog));
                                    }
                                }
                            }

                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (i != 0) {
                                    Builder builder = new Builder(DialogsActivity.this.getParentActivity());
                                    builder.setTitle(LocaleController.getString("AppName", C0446R.string.AppName));
                                    if (i == 1) {
                                        if (chat == null || chat.megagroup == null) {
                                            builder.setMessage(LocaleController.getString("AreYouSureClearHistoryChannel", C0446R.string.AreYouSureClearHistoryChannel));
                                        } else if (TextUtils.isEmpty(chat.username) != null) {
                                            builder.setMessage(LocaleController.getString("AreYouSureClearHistory", C0446R.string.AreYouSureClearHistory));
                                        } else {
                                            builder.setMessage(LocaleController.getString("AreYouSureClearHistoryGroup", C0446R.string.AreYouSureClearHistoryGroup));
                                        }
                                        builder.setPositiveButton(LocaleController.getString("OK", C0446R.string.OK), new C13791());
                                    } else {
                                        if (chat == null || chat.megagroup == null) {
                                            builder.setMessage(LocaleController.getString("ChannelLeaveAlert", C0446R.string.ChannelLeaveAlert));
                                        } else {
                                            builder.setMessage(LocaleController.getString("MegaLeaveAlert", C0446R.string.MegaLeaveAlert));
                                        }
                                        builder.setPositiveButton(LocaleController.getString("OK", C0446R.string.OK), new C13802());
                                    }
                                    builder.setNegativeButton(LocaleController.getString("Cancel", C0446R.string.Cancel), 0);
                                    DialogsActivity.this.showDialog(builder.create());
                                } else if (MessagesController.getInstance(DialogsActivity.this.currentAccount).pinDialog(DialogsActivity.this.selectedDialog, z ^ 1, null, 0) != null && z == null) {
                                    DialogsActivity.this.listView.smoothScrollToPosition(0);
                                }
                            }
                        });
                        DialogsActivity.this.showDialog(builder2.create());
                    } else {
                        int[] iArr2;
                        final boolean z2 = access$3700 < 0 && access$37002 != 1;
                        User user = (z2 || access$3700 <= 0 || access$37002 == 1) ? null : MessagesController.getInstance(DialogsActivity.this.currentAccount).getUser(Integer.valueOf(access$3700));
                        final boolean z3 = user != null && user.bot;
                        CharSequence[] charSequenceArr2 = new CharSequence[3];
                        if (!tL_dialog.pinned) {
                            if (!MessagesController.getInstance(DialogsActivity.this.currentAccount).canPinDialog(access$3700 == 0)) {
                                str = null;
                                charSequenceArr2[0] = str;
                                charSequenceArr2[1] = LocaleController.getString("ClearHistory", C0446R.string.ClearHistory);
                                if (z2) {
                                    str = "DeleteChat";
                                    access$3700 = C0446R.string.DeleteChat;
                                } else if (z3) {
                                    str = "Delete";
                                    access$3700 = C0446R.string.Delete;
                                } else {
                                    str = "DeleteAndStop";
                                    access$3700 = C0446R.string.DeleteAndStop;
                                }
                                charSequenceArr2[2] = LocaleController.getString(str, access$3700);
                                iArr2 = new int[3];
                                iArr2[0] = tL_dialog.pinned ? C0446R.drawable.chats_unpin : C0446R.drawable.chats_pin;
                                iArr2[1] = C0446R.drawable.chats_clear;
                                iArr2[2] = z2 ? C0446R.drawable.chats_leave : C0446R.drawable.chats_delete;
                                builder2.setItems(charSequenceArr2, iArr2, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialogInterface, final int i) {
                                        if (i != 0) {
                                            Builder builder = new Builder(DialogsActivity.this.getParentActivity());
                                            builder.setTitle(LocaleController.getString("AppName", C0446R.string.AppName));
                                            if (i == 1) {
                                                builder.setMessage(LocaleController.getString("AreYouSureClearHistory", C0446R.string.AreYouSureClearHistory));
                                            } else if (z2 != null) {
                                                builder.setMessage(LocaleController.getString("AreYouSureDeleteAndExit", C0446R.string.AreYouSureDeleteAndExit));
                                            } else {
                                                builder.setMessage(LocaleController.getString("AreYouSureDeleteThisChat", C0446R.string.AreYouSureDeleteThisChat));
                                            }
                                            builder.setPositiveButton(LocaleController.getString("OK", C0446R.string.OK), new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    if (i != 1) {
                                                        if (z2 != null) {
                                                            dialogInterface = MessagesController.getInstance(DialogsActivity.this.currentAccount).getChat(Integer.valueOf((int) (-DialogsActivity.this.selectedDialog)));
                                                            if (dialogInterface == null || ChatObject.isNotInChat(dialogInterface) == null) {
                                                                MessagesController.getInstance(DialogsActivity.this.currentAccount).deleteUserFromChat((int) (-DialogsActivity.this.selectedDialog), MessagesController.getInstance(DialogsActivity.this.currentAccount).getUser(Integer.valueOf(UserConfig.getInstance(DialogsActivity.this.currentAccount).getClientUserId())), null);
                                                            } else {
                                                                MessagesController.getInstance(DialogsActivity.this.currentAccount).deleteDialog(DialogsActivity.this.selectedDialog, 0);
                                                            }
                                                        } else {
                                                            MessagesController.getInstance(DialogsActivity.this.currentAccount).deleteDialog(DialogsActivity.this.selectedDialog, 0);
                                                        }
                                                        if (z3 != null) {
                                                            MessagesController.getInstance(DialogsActivity.this.currentAccount).blockUser((int) DialogsActivity.this.selectedDialog);
                                                        }
                                                        if (AndroidUtilities.isTablet() != null) {
                                                            NotificationCenter.getInstance(DialogsActivity.this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[]{Long.valueOf(DialogsActivity.this.selectedDialog)});
                                                            return;
                                                        }
                                                        return;
                                                    }
                                                    MessagesController.getInstance(DialogsActivity.this.currentAccount).deleteDialog(DialogsActivity.this.selectedDialog, 1);
                                                }
                                            });
                                            builder.setNegativeButton(LocaleController.getString("Cancel", C0446R.string.Cancel), 0);
                                            DialogsActivity.this.showDialog(builder.create());
                                        } else if (MessagesController.getInstance(DialogsActivity.this.currentAccount).pinDialog(DialogsActivity.this.selectedDialog, z ^ 1, null, 0) != null && z == null) {
                                            DialogsActivity.this.listView.smoothScrollToPosition(0);
                                        }
                                    }
                                });
                                DialogsActivity.this.showDialog(builder2.create());
                            }
                        }
                        str = tL_dialog.pinned ? LocaleController.getString("UnpinFromTop", C0446R.string.UnpinFromTop) : LocaleController.getString("PinToTop", C0446R.string.PinToTop);
                        charSequenceArr2[0] = str;
                        charSequenceArr2[1] = LocaleController.getString("ClearHistory", C0446R.string.ClearHistory);
                        if (z2) {
                            str = "DeleteChat";
                            access$3700 = C0446R.string.DeleteChat;
                        } else if (z3) {
                            str = "Delete";
                            access$3700 = C0446R.string.Delete;
                        } else {
                            str = "DeleteAndStop";
                            access$3700 = C0446R.string.DeleteAndStop;
                        }
                        charSequenceArr2[2] = LocaleController.getString(str, access$3700);
                        iArr2 = new int[3];
                        if (tL_dialog.pinned) {
                        }
                        iArr2[0] = tL_dialog.pinned ? C0446R.drawable.chats_unpin : C0446R.drawable.chats_pin;
                        iArr2[1] = C0446R.drawable.chats_clear;
                        if (z2) {
                        }
                        iArr2[2] = z2 ? C0446R.drawable.chats_leave : C0446R.drawable.chats_delete;
                        builder2.setItems(charSequenceArr2, iArr2, /* anonymous class already generated */);
                        DialogsActivity.this.showDialog(builder2.create());
                    }
                    return true;
                }
            }
            return false;
        }
    }

    public DialogsActivity(Bundle bundle) {
        super(bundle);
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
        View backupImageView;
        final Context context2 = context;
        this.searching = false;
        this.searchWas = false;
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                Theme.createChatResources(context2, false);
            }
        });
        ActionBarMenu createMenu = this.actionBar.createMenu();
        if (!this.onlySelect && r0.searchString == null) {
            r0.passcodeItem = createMenu.addItem(1, (int) C0446R.drawable.lock_close);
            updatePasscodeButton();
        }
        createMenu.addItem(0, (int) C0446R.drawable.ic_ab_search).setIsSearchField(true).setActionBarMenuItemSearchListener(new C21272()).getSearchField().setHint(LocaleController.getString("Search", C0446R.string.Search));
        if (r0.onlySelect) {
            r0.actionBar.setBackButtonImage(C0446R.drawable.ic_ab_back);
            if (r0.dialogsType == 3 && r0.selectAlertString == null) {
                r0.actionBar.setTitle(LocaleController.getString("ForwardTo", C0446R.string.ForwardTo));
            } else {
                r0.actionBar.setTitle(LocaleController.getString("SelectChat", C0446R.string.SelectChat));
            }
        } else {
            if (r0.searchString != null) {
                r0.actionBar.setBackButtonImage(C0446R.drawable.ic_ab_back);
            } else {
                r0.actionBar.setBackButtonDrawable(new MenuDrawable());
            }
            if (BuildVars.DEBUG_VERSION) {
                r0.actionBar.setTitle("Telegram Beta");
            } else {
                r0.actionBar.setTitle(LocaleController.getString("AppName", C0446R.string.AppName));
            }
            r0.actionBar.setSupportsHolidayImage(true);
        }
        if (r0.allowSwitchAccount && UserConfig.getActivatedAccountsCount() > 1) {
            r0.switchItem = createMenu.addItemWithWidth(1, 0, AndroidUtilities.dp(56.0f));
            Drawable avatarDrawable = new AvatarDrawable();
            avatarDrawable.setTextSize(AndroidUtilities.dp(12.0f));
            backupImageView = new BackupImageView(context2);
            backupImageView.setRoundRadius(AndroidUtilities.dp(18.0f));
            r0.switchItem.addView(backupImageView, LayoutHelper.createFrame(36, 36, 17));
            User currentUser = UserConfig.getInstance(r0.currentAccount).getCurrentUser();
            avatarDrawable.setInfo(currentUser);
            TLObject tLObject = (currentUser.photo == null || currentUser.photo.photo_small == null || currentUser.photo.photo_small.volume_id == 0 || currentUser.photo.photo_small.local_id == 0) ? null : currentUser.photo.photo_small;
            backupImageView.getImageReceiver().setCurrentAccount(r0.currentAccount);
            backupImageView.setImage(tLObject, "50_50", avatarDrawable);
            for (int i = 0; i < 3; i++) {
                if (UserConfig.getInstance(i).getCurrentUser() != null) {
                    backupImageView = new AccountSelectCell(context2);
                    backupImageView.setAccount(i);
                    r0.switchItem.addSubItem(10 + i, backupImageView, AndroidUtilities.dp(230.0f), AndroidUtilities.dp(48.0f));
                }
            }
        }
        r0.actionBar.setAllowOverlayTitle(true);
        r0.actionBar.setActionBarMenuOnItemClick(new C21283());
        if (r0.sideMenu != null) {
            r0.sideMenu.setBackgroundColor(Theme.getColor(Theme.key_chats_menuBackground));
            r0.sideMenu.setGlowColor(Theme.getColor(Theme.key_chats_menuBackground));
            r0.sideMenu.getAdapter().notifyDataSetChanged();
        }
        View c21294 = new SizeNotifierFrameLayout(context2) {
            int inputFieldHeight = null;

            protected void onMeasure(int i, int i2) {
                int size = MeasureSpec.getSize(i);
                int size2 = MeasureSpec.getSize(i2);
                setMeasuredDimension(size, size2);
                size2 -= getPaddingTop();
                measureChildWithMargins(DialogsActivity.this.actionBar, i, 0, i2, 0);
                int keyboardHeight = getKeyboardHeight();
                int childCount = getChildCount();
                int i3 = 0;
                if (DialogsActivity.this.commentView != null) {
                    measureChildWithMargins(DialogsActivity.this.commentView, i, 0, i2, 0);
                    Object tag = DialogsActivity.this.commentView.getTag();
                    if (tag == null || !tag.equals(Integer.valueOf(2))) {
                        this.inputFieldHeight = 0;
                    } else {
                        if (keyboardHeight <= AndroidUtilities.dp(20.0f) && !AndroidUtilities.isInMultiwindow) {
                            size2 -= DialogsActivity.this.commentView.getEmojiPadding();
                        }
                        this.inputFieldHeight = DialogsActivity.this.commentView.getMeasuredHeight();
                    }
                }
                while (i3 < childCount) {
                    View childAt = getChildAt(i3);
                    if (!(childAt == null || childAt.getVisibility() == 8 || childAt == DialogsActivity.this.commentView)) {
                        if (childAt != DialogsActivity.this.actionBar) {
                            if (!(childAt == DialogsActivity.this.listView || childAt == DialogsActivity.this.progressView)) {
                                if (childAt != DialogsActivity.this.searchEmptyView) {
                                    if (DialogsActivity.this.commentView == null || !DialogsActivity.this.commentView.isPopupView(childAt)) {
                                        measureChildWithMargins(childAt, i, 0, i2, 0);
                                    } else if (!AndroidUtilities.isInMultiwindow) {
                                        childAt.measure(MeasureSpec.makeMeasureSpec(size, NUM), MeasureSpec.makeMeasureSpec(childAt.getLayoutParams().height, NUM));
                                    } else if (AndroidUtilities.isTablet()) {
                                        childAt.measure(MeasureSpec.makeMeasureSpec(size, NUM), MeasureSpec.makeMeasureSpec(Math.min(AndroidUtilities.dp(320.0f), ((size2 - this.inputFieldHeight) - AndroidUtilities.statusBarHeight) + getPaddingTop()), NUM));
                                    } else {
                                        childAt.measure(MeasureSpec.makeMeasureSpec(size, NUM), MeasureSpec.makeMeasureSpec(((size2 - this.inputFieldHeight) - AndroidUtilities.statusBarHeight) + getPaddingTop(), NUM));
                                    }
                                }
                            }
                            childAt.measure(MeasureSpec.makeMeasureSpec(size, NUM), MeasureSpec.makeMeasureSpec(Math.max(AndroidUtilities.dp(10.0f), (size2 - this.inputFieldHeight) + AndroidUtilities.dp(2.0f)), NUM));
                        }
                    }
                    i3++;
                }
            }

            protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
                z = getChildCount();
                Object tag = DialogsActivity.this.commentView != null ? DialogsActivity.this.commentView.getTag() : null;
                boolean z2 = false;
                int emojiPadding = (tag == null || !tag.equals(Integer.valueOf(2)) || getKeyboardHeight() > AndroidUtilities.dp(20.0f) || AndroidUtilities.isInMultiwindow) ? 0 : DialogsActivity.this.commentView.getEmojiPadding();
                setBottomClip(emojiPadding);
                while (z2 < z) {
                    View childAt = getChildAt(z2);
                    if (childAt.getVisibility() != 8) {
                        LayoutParams layoutParams = (LayoutParams) childAt.getLayoutParams();
                        int measuredWidth = childAt.getMeasuredWidth();
                        int measuredHeight = childAt.getMeasuredHeight();
                        int i5 = layoutParams.gravity;
                        if (i5 == -1) {
                            i5 = 51;
                        }
                        int i6 = i5 & 7;
                        i5 &= 112;
                        i6 &= 7;
                        if (i6 == 1) {
                            i6 = ((((i3 - i) - measuredWidth) / 2) + layoutParams.leftMargin) - layoutParams.rightMargin;
                        } else if (i6 != 5) {
                            i6 = layoutParams.leftMargin;
                        } else {
                            i6 = (i3 - measuredWidth) - layoutParams.rightMargin;
                        }
                        int paddingTop = i5 != 16 ? i5 != 48 ? i5 != 80 ? layoutParams.topMargin : (((i4 - emojiPadding) - i2) - measuredHeight) - layoutParams.bottomMargin : layoutParams.topMargin + getPaddingTop() : (((((i4 - emojiPadding) - i2) - measuredHeight) / 2) + layoutParams.topMargin) - layoutParams.bottomMargin;
                        if (DialogsActivity.this.commentView != null && DialogsActivity.this.commentView.isPopupView(childAt)) {
                            if (AndroidUtilities.isInMultiwindow) {
                                paddingTop = (DialogsActivity.this.commentView.getTop() - childAt.getMeasuredHeight()) + AndroidUtilities.dp(1.0f);
                            } else {
                                paddingTop = DialogsActivity.this.commentView.getBottom();
                            }
                        }
                        childAt.layout(i6, paddingTop, measuredWidth + i6, measuredHeight + paddingTop);
                    }
                    z2++;
                }
                notifyHeightChanged();
            }
        };
        r0.fragmentView = c21294;
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
        int i2 = 2;
        r0.listView.setVerticalScrollbarPosition(LocaleController.isRTL ? 1 : 2);
        c21294.addView(r0.listView, LayoutHelper.createFrame(-1, -1.0f));
        r0.listView.setOnItemClickListener(new C21306());
        r0.listView.setOnItemLongClickListener(new C21317());
        r0.searchEmptyView = new EmptyTextProgressView(context2);
        r0.searchEmptyView.setVisibility(8);
        r0.searchEmptyView.setShowAtCenter(true);
        r0.searchEmptyView.setText(LocaleController.getString("NoResult", C0446R.string.NoResult));
        c21294.addView(r0.searchEmptyView, LayoutHelper.createFrame(-1, -1.0f));
        r0.progressView = new RadialProgressView(context2);
        r0.progressView.setVisibility(8);
        c21294.addView(r0.progressView, LayoutHelper.createFrame(-2, -2, 17));
        r0.floatingButton = new ImageView(context2);
        r0.floatingButton.setVisibility(r0.onlySelect ? 8 : 0);
        r0.floatingButton.setScaleType(ScaleType.CENTER);
        Drawable createSimpleSelectorCircleDrawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56.0f), Theme.getColor(Theme.key_chats_actionBackground), Theme.getColor(Theme.key_chats_actionPressedBackground));
        if (VERSION.SDK_INT < 21) {
            Drawable mutate = context.getResources().getDrawable(C0446R.drawable.floating_shadow).mutate();
            mutate.setColorFilter(new PorterDuffColorFilter(Theme.ACTION_BAR_VIDEO_EDIT_COLOR, Mode.MULTIPLY));
            Drawable combinedDrawable = new CombinedDrawable(mutate, createSimpleSelectorCircleDrawable, 0, 0);
            combinedDrawable.setIconSize(AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
            createSimpleSelectorCircleDrawable = combinedDrawable;
        }
        r0.floatingButton.setBackgroundDrawable(createSimpleSelectorCircleDrawable);
        r0.floatingButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chats_actionIcon), Mode.MULTIPLY));
        r0.floatingButton.setImageResource(C0446R.drawable.floating_pencil);
        if (VERSION.SDK_INT >= 21) {
            StateListAnimator stateListAnimator = new StateListAnimator();
            stateListAnimator.addState(new int[]{16842919}, ObjectAnimator.ofFloat(r0.floatingButton, "translationZ", new float[]{(float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(4.0f)}).setDuration(200));
            stateListAnimator.addState(new int[0], ObjectAnimator.ofFloat(r0.floatingButton, "translationZ", new float[]{(float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(2.0f)}).setDuration(200));
            r0.floatingButton.setStateListAnimator(stateListAnimator);
            r0.floatingButton.setOutlineProvider(new C13848());
        }
        c21294.addView(r0.floatingButton, LayoutHelper.createFrame(VERSION.SDK_INT >= 21 ? 56 : 60, VERSION.SDK_INT >= 21 ? 56.0f : 60.0f, (LocaleController.isRTL ? 3 : 5) | 80, LocaleController.isRTL ? 14.0f : 0.0f, 0.0f, LocaleController.isRTL ? 0.0f : 14.0f, 14.0f));
        r0.floatingButton.setOnClickListener(new C13859());
        r0.listView.setOnScrollListener(new OnScrollListener() {
            public void onScrollStateChanged(RecyclerView recyclerView, int i) {
                if (i == 1 && DialogsActivity.this.searching != null && DialogsActivity.this.searchWas != null) {
                    AndroidUtilities.hideKeyboard(DialogsActivity.this.getParentActivity().getCurrentFocus());
                }
            }

            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                i = DialogsActivity.this.layoutManager.findFirstVisibleItemPosition();
                i2 = Math.abs(DialogsActivity.this.layoutManager.findLastVisibleItemPosition() - i) + 1;
                int itemCount = recyclerView.getAdapter().getItemCount();
                if (DialogsActivity.this.searching && DialogsActivity.this.searchWas) {
                    if (i2 > 0 && DialogsActivity.this.layoutManager.findLastVisibleItemPosition() == itemCount - 1 && DialogsActivity.this.dialogsSearchAdapter.isMessagesSearchEndReached() == null) {
                        DialogsActivity.this.dialogsSearchAdapter.loadMoreSearchMessages();
                    }
                    return;
                }
                if (i2 > 0 && DialogsActivity.this.layoutManager.findLastVisibleItemPosition() >= DialogsActivity.this.getDialogsArray().size() - 10) {
                    i2 = MessagesController.getInstance(DialogsActivity.this.currentAccount).dialogsEndReached ^ 1;
                    if (!(i2 == 0 && MessagesController.getInstance(DialogsActivity.this.currentAccount).serverDialogsEndReached)) {
                        MessagesController.getInstance(DialogsActivity.this.currentAccount).loadDialogs(-1, 100, i2);
                    }
                }
                if (DialogsActivity.this.floatingButton.getVisibility() != 8) {
                    boolean z;
                    i2 = 0;
                    recyclerView = recyclerView.getChildAt(0);
                    recyclerView = recyclerView != null ? recyclerView.getTop() : null;
                    if (DialogsActivity.this.prevPosition == i) {
                        itemCount = DialogsActivity.this.prevTop - recyclerView;
                        z = recyclerView < DialogsActivity.this.prevTop;
                        if (Math.abs(itemCount) > 1) {
                        }
                        if (!(i2 == 0 || DialogsActivity.this.scrollUpdated == 0)) {
                            DialogsActivity.this.hideFloatingButton(z);
                        }
                        DialogsActivity.this.prevPosition = i;
                        DialogsActivity.this.prevTop = recyclerView;
                        DialogsActivity.this.scrollUpdated = true;
                    } else {
                        z = i > DialogsActivity.this.prevPosition;
                    }
                    i2 = 1;
                    DialogsActivity.this.hideFloatingButton(z);
                    DialogsActivity.this.prevPosition = i;
                    DialogsActivity.this.prevTop = recyclerView;
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
        if (r0.searchString == null) {
            i2 = !r0.onlySelect ? 1 : 0;
        }
        r0.dialogsSearchAdapter = new DialogsSearchAdapter(context2, i2, r0.dialogsType);
        r0.dialogsSearchAdapter.setDelegate(new DialogsSearchAdapterDelegate() {
            public void searchStateChanged(boolean z) {
                if (!DialogsActivity.this.searching || !DialogsActivity.this.searchWas || DialogsActivity.this.searchEmptyView == null) {
                    return;
                }
                if (z) {
                    DialogsActivity.this.searchEmptyView.showProgress();
                } else {
                    DialogsActivity.this.searchEmptyView.showTextView();
                }
            }

            public void didPressedOnSubDialog(long j) {
                if (!DialogsActivity.this.onlySelect) {
                    int i = (int) j;
                    Bundle bundle = new Bundle();
                    if (i > 0) {
                        bundle.putInt("user_id", i);
                    } else {
                        bundle.putInt("chat_id", -i);
                    }
                    if (DialogsActivity.this.actionBar != null) {
                        DialogsActivity.this.actionBar.closeSearchField();
                    }
                    if (AndroidUtilities.isTablet() && DialogsActivity.this.dialogsAdapter != null) {
                        DialogsActivity.this.dialogsAdapter.setOpenedDialogId(DialogsActivity.this.openedDialogId = j);
                        DialogsActivity.this.updateVisibleRows(512);
                    }
                    if (DialogsActivity.this.searchString != null) {
                        if (MessagesController.getInstance(DialogsActivity.this.currentAccount).checkCanOpenChat(bundle, DialogsActivity.this) != null) {
                            NotificationCenter.getInstance(DialogsActivity.this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                            DialogsActivity.this.presentFragment(new ChatActivity(bundle));
                        }
                    } else if (MessagesController.getInstance(DialogsActivity.this.currentAccount).checkCanOpenChat(bundle, DialogsActivity.this) != null) {
                        DialogsActivity.this.presentFragment(new ChatActivity(bundle));
                    }
                } else if (DialogsActivity.this.dialogsAdapter.hasSelectedDialogs()) {
                    DialogsActivity.this.dialogsAdapter.addOrRemoveSelectedDialog(j, null);
                    DialogsActivity.this.updateSelectedCount();
                    DialogsActivity.this.actionBar.closeSearchField();
                } else {
                    DialogsActivity.this.didSelectResult(j, true, false);
                }
            }

            public void needRemoveHint(final int i) {
                if (DialogsActivity.this.getParentActivity() != null && MessagesController.getInstance(DialogsActivity.this.currentAccount).getUser(Integer.valueOf(i)) != null) {
                    Builder builder = new Builder(DialogsActivity.this.getParentActivity());
                    builder.setTitle(LocaleController.getString("AppName", C0446R.string.AppName));
                    builder.setMessage(LocaleController.formatString("ChatHintsDelete", C0446R.string.ChatHintsDelete, ContactsController.formatName(r0.first_name, r0.last_name)));
                    builder.setPositiveButton(LocaleController.getString("OK", C0446R.string.OK), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            DataQuery.getInstance(DialogsActivity.this.currentAccount).removePeer(i);
                        }
                    });
                    builder.setNegativeButton(LocaleController.getString("Cancel", C0446R.string.Cancel), null);
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
            backupImageView = new FragmentContextView(context2, r0, true);
            c21294.addView(backupImageView, LayoutHelper.createFrame(-1, 39.0f, 51, 0.0f, -36.0f, 0.0f, 0.0f));
            View fragmentContextView = new FragmentContextView(context2, r0, false);
            c21294.addView(fragmentContextView, LayoutHelper.createFrame(-1, 39.0f, 51, 0.0f, -36.0f, 0.0f, 0.0f));
            fragmentContextView.setAdditionalContextView(backupImageView);
            backupImageView.setAdditionalContextView(fragmentContextView);
        } else if (r0.dialogsType == 3 && r0.selectAlertString == null) {
            if (r0.commentView != null) {
                r0.commentView.onDestroy();
            }
            r0.commentView = new ChatActivityEnterView(getParentActivity(), c21294, null, false);
            r0.commentView.setAllowStickersAndGifs(false, false);
            r0.commentView.setForceShowSendButton(true, false);
            r0.commentView.setVisibility(8);
            c21294.addView(r0.commentView, LayoutHelper.createFrame(-1, -2, 83));
            r0.commentView.setDelegate(new ChatActivityEnterViewDelegate() {
                public void didPressedAttachButton() {
                }

                public void needChangeVideoPreviewState(int i, float f) {
                }

                public void needSendTyping() {
                }

                public void needShowMediaBanHint() {
                }

                public void needStartRecordAudio(int i) {
                }

                public void needStartRecordVideo(int i) {
                }

                public void onAttachButtonHidden() {
                }

                public void onAttachButtonShow() {
                }

                public void onMessageEditEnd(boolean z) {
                }

                public void onPreAudioVideoRecord() {
                }

                public void onStickersExpandedChange() {
                }

                public void onStickersTab(boolean z) {
                }

                public void onSwitchRecordMode(boolean z) {
                }

                public void onTextChanged(CharSequence charSequence, boolean z) {
                }

                public void onWindowSizeChanged(int i) {
                }

                public void onMessageSend(CharSequence charSequence) {
                    if (DialogsActivity.this.delegate != null) {
                        ArrayList selectedDialogs = DialogsActivity.this.dialogsAdapter.getSelectedDialogs();
                        if (!selectedDialogs.isEmpty()) {
                            DialogsActivity.this.delegate.didSelectDialogs(DialogsActivity.this, selectedDialogs, charSequence, false);
                        }
                    }
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
            Context parentActivity = getParentActivity();
            if (parentActivity != null) {
                this.checkPermission = false;
                if (parentActivity.checkSelfPermission("android.permission.READ_CONTACTS") != 0 || parentActivity.checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") != 0) {
                    Builder builder;
                    Dialog create;
                    if (parentActivity.shouldShowRequestPermissionRationale("android.permission.READ_CONTACTS")) {
                        builder = new Builder(parentActivity);
                        builder.setTitle(LocaleController.getString("AppName", C0446R.string.AppName));
                        builder.setMessage(LocaleController.getString("PermissionContacts", C0446R.string.PermissionContacts));
                        builder.setPositiveButton(LocaleController.getString("OK", C0446R.string.OK), null);
                        create = builder.create();
                        this.permissionDialog = create;
                        showDialog(create);
                    } else if (parentActivity.shouldShowRequestPermissionRationale("android.permission.WRITE_EXTERNAL_STORAGE")) {
                        builder = new Builder(parentActivity);
                        builder.setTitle(LocaleController.getString("AppName", C0446R.string.AppName));
                        builder.setMessage(LocaleController.getString("PermissionStorage", C0446R.string.PermissionStorage));
                        builder.setPositiveButton(LocaleController.getString("OK", C0446R.string.OK), null);
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
                        public void onAnimationEnd(Animator animator) {
                            DialogsActivity.this.commentView.setTag(Integer.valueOf(2));
                        }
                    });
                    animatorSet.start();
                    this.commentView.setTag(Integer.valueOf(1));
                }
                this.actionBar.setTitle(LocaleController.formatPluralString("Recipient", this.dialogsAdapter.getSelectedDialogs().size()));
            } else {
                if (this.dialogsType == 3 && this.selectAlertString == null) {
                    this.actionBar.setTitle(LocaleController.getString("ForwardTo", C0446R.string.ForwardTo));
                } else {
                    this.actionBar.setTitle(LocaleController.getString("SelectChat", C0446R.string.SelectChat));
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
                        public void onAnimationEnd(Animator animator) {
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

    @android.annotation.TargetApi(23)
    private void askForPermissons() {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
*/
        /*
        r3 = this;
        r0 = r3.getParentActivity();
        if (r0 != 0) goto L_0x0007;
    L_0x0006:
        return;
    L_0x0007:
        r1 = new java.util.ArrayList;
        r1.<init>();
        r2 = "android.permission.READ_CONTACTS";
        r2 = r0.checkSelfPermission(r2);
        if (r2 == 0) goto L_0x0023;
    L_0x0014:
        r2 = "android.permission.READ_CONTACTS";
        r1.add(r2);
        r2 = "android.permission.WRITE_CONTACTS";
        r1.add(r2);
        r2 = "android.permission.GET_ACCOUNTS";
        r1.add(r2);
    L_0x0023:
        r2 = "android.permission.WRITE_EXTERNAL_STORAGE";
        r2 = r0.checkSelfPermission(r2);
        if (r2 == 0) goto L_0x0035;
    L_0x002b:
        r2 = "android.permission.READ_EXTERNAL_STORAGE";
        r1.add(r2);
        r2 = "android.permission.WRITE_EXTERNAL_STORAGE";
        r1.add(r2);
    L_0x0035:
        r2 = r1.size();
        r2 = new java.lang.String[r2];
        r1 = r1.toArray(r2);
        r1 = (java.lang.String[]) r1;
        r2 = 1;
        r0.requestPermissions(r1, r2);	 Catch:{ Exception -> 0x0045 }
    L_0x0045:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.DialogsActivity.askForPermissons():void");
    }

    protected void onDialogDismiss(Dialog dialog) {
        super.onDialogDismiss(dialog);
        if (this.permissionDialog != null && dialog == this.permissionDialog && getParentActivity() != null) {
            askForPermissons();
        }
    }

    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        if (this.onlySelect == null && this.floatingButton != null) {
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

    public void onRequestPermissionsResultFragment(int i, String[] strArr, int[] iArr) {
        if (i == 1) {
            for (int i2 = 0; i2 < strArr.length; i2++) {
                if (iArr.length > i2) {
                    if (iArr[i2] == 0) {
                        String str = strArr[i2];
                        int i3 = -1;
                        int hashCode = str.hashCode();
                        if (hashCode != NUM) {
                            if (hashCode == NUM) {
                                if (str.equals("android.permission.READ_CONTACTS")) {
                                    i3 = 0;
                                }
                            }
                        } else if (str.equals("android.permission.WRITE_EXTERNAL_STORAGE")) {
                            i3 = 1;
                        }
                        switch (i3) {
                            case 0:
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

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.dialogsNeedReload) {
            if (this.dialogsAdapter != 0) {
                if (this.dialogsAdapter.isDataSetChanged() != 0) {
                    this.dialogsAdapter.notifyDataSetChanged();
                } else {
                    updateVisibleRows(2048);
                }
            }
            if (this.listView != 0) {
                try {
                    if (MessagesController.getInstance(this.currentAccount).loadingDialogs == 0 || MessagesController.getInstance(this.currentAccount).dialogs.isEmpty() == 0) {
                        this.progressView.setVisibility(8);
                        if (this.searching == 0 || this.searchWas == 0) {
                            this.searchEmptyView.setVisibility(8);
                            this.listView.setEmptyView(0);
                            return;
                        }
                        this.listView.setEmptyView(this.searchEmptyView);
                        return;
                    }
                    this.searchEmptyView.setVisibility(8);
                    this.listView.setEmptyView(this.progressView);
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
            }
        } else if (i == NotificationCenter.emojiDidLoaded) {
            updateVisibleRows(0);
        } else if (i == NotificationCenter.updateInterfaces) {
            updateVisibleRows(((Integer) objArr[0]).intValue());
        } else if (i == NotificationCenter.appDidLogout) {
            dialogsLoaded[this.currentAccount] = null;
        } else if (i == NotificationCenter.encryptedChatUpdated) {
            updateVisibleRows(0);
        } else if (i == NotificationCenter.contactsDidLoaded) {
            updateVisibleRows(0);
        } else if (i == NotificationCenter.openedChatChanged) {
            if (this.dialogsType == 0 && AndroidUtilities.isTablet() != 0) {
                i = ((Boolean) objArr[1]).booleanValue();
                i2 = ((Long) objArr[0]).longValue();
                if (i == 0) {
                    this.openedDialogId = i2;
                } else if (i2 == this.openedDialogId) {
                    this.openedDialogId = 0;
                }
                if (this.dialogsAdapter != 0) {
                    this.dialogsAdapter.setOpenedDialogId(this.openedDialogId);
                }
                updateVisibleRows(512);
            }
        } else if (i == NotificationCenter.notificationsSettingsUpdated) {
            updateVisibleRows(0);
        } else {
            if (!(i == NotificationCenter.messageReceivedByAck || i == NotificationCenter.messageReceivedByServer)) {
                if (i != NotificationCenter.messageSendError) {
                    if (i == NotificationCenter.didSetPasscode) {
                        updatePasscodeButton();
                        return;
                    } else if (i == NotificationCenter.needReloadRecentDialogsSearch) {
                        if (this.dialogsSearchAdapter != 0) {
                            this.dialogsSearchAdapter.loadRecentSearch();
                            return;
                        }
                        return;
                    } else if (i == NotificationCenter.didLoadedReplyMessages) {
                        updateVisibleRows(32768);
                        return;
                    } else if (i == NotificationCenter.reloadHints && this.dialogsSearchAdapter != 0) {
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
        return this.dialogsType == 3 ? MessagesController.getInstance(this.currentAccount).dialogsForward : null;
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
                    this.passcodeItem.setIcon((int) C0446R.drawable.lock_close);
                } else {
                    this.passcodeItem.setIcon((int) C0446R.drawable.lock_open);
                }
            }
        }
    }

    private void hideFloatingButton(boolean z) {
        if (this.floatingHidden != z) {
            this.floatingHidden = z;
            ImageView imageView = this.floatingButton;
            String str = "translationY";
            float[] fArr = new float[1];
            fArr[0] = this.floatingHidden ? (float) AndroidUtilities.dp(100.0f) : 0.0f;
            ObjectAnimator duration = ObjectAnimator.ofFloat(imageView, str, fArr).setDuration(300);
            duration.setInterpolator(this.floatingInterpolator);
            this.floatingButton.setClickable(z ^ true);
            duration.start();
        }
    }

    private void updateVisibleRows(int i) {
        if (this.listView != null) {
            int childCount = this.listView.getChildCount();
            for (int i2 = 0; i2 < childCount; i2++) {
                View childAt = this.listView.getChildAt(i2);
                if (childAt instanceof DialogCell) {
                    if (this.listView.getAdapter() != this.dialogsSearchAdapter) {
                        DialogCell dialogCell = (DialogCell) childAt;
                        boolean z = true;
                        if ((i & 2048) != 0) {
                            dialogCell.checkCurrentDialogIndex();
                            if (this.dialogsType == 0 && AndroidUtilities.isTablet()) {
                                if (dialogCell.getDialogId() != this.openedDialogId) {
                                    z = false;
                                }
                                dialogCell.setDialogSelected(z);
                            }
                        } else if ((i & 512) == 0) {
                            dialogCell.update(i);
                        } else if (this.dialogsType == 0 && AndroidUtilities.isTablet()) {
                            if (dialogCell.getDialogId() != this.openedDialogId) {
                                z = false;
                            }
                            dialogCell.setDialogSelected(z);
                        }
                    }
                } else if (childAt instanceof UserCell) {
                    ((UserCell) childAt).update(i);
                } else if (childAt instanceof ProfileSearchCell) {
                    ((ProfileSearchCell) childAt).update(i);
                } else if (childAt instanceof RecyclerListView) {
                    RecyclerListView recyclerListView = (RecyclerListView) childAt;
                    int childCount2 = recyclerListView.getChildCount();
                    for (int i3 = 0; i3 < childCount2; i3++) {
                        View childAt2 = recyclerListView.getChildAt(i3);
                        if (childAt2 instanceof HintDialogCell) {
                            ((HintDialogCell) childAt2).checkUnreadCounter(i);
                        }
                    }
                }
            }
        }
    }

    public void setDelegate(DialogsActivityDelegate dialogsActivityDelegate) {
        this.delegate = dialogsActivityDelegate;
    }

    public void setSearchString(String str) {
        this.searchString = str;
    }

    public boolean isMainDialogList() {
        return this.delegate == null && this.searchString == null;
    }

    private void didSelectResult(final long j, boolean z, boolean z2) {
        int i;
        if (this.addToGroupAlertString == null) {
            i = (int) j;
            if (i < 0) {
                i = -i;
                Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(i));
                if (ChatObject.isChannel(chat) && !chat.megagroup && (this.cantSendToChannels || !ChatObject.isCanWriteToChannel(i, this.currentAccount))) {
                    j = new Builder(getParentActivity());
                    j.setTitle(LocaleController.getString("AppName", C0446R.string.AppName));
                    j.setMessage(LocaleController.getString("ChannelCantSendMessage", true));
                    j.setNegativeButton(LocaleController.getString("OK", C0446R.string.OK), null);
                    showDialog(j.create());
                    return;
                }
            }
        }
        if (z && ((this.selectAlertString && this.selectAlertStringGroup) || this.addToGroupAlertString)) {
            if (getParentActivity()) {
                z = new Builder(getParentActivity());
                z.setTitle(LocaleController.getString("AppName", C0446R.string.AppName));
                z2 = (int) j;
                i = (int) (j >> 32);
                if (!z2) {
                    if (MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(MessagesController.getInstance(this.currentAccount).getEncryptedChat(Integer.valueOf(i)).user_id))) {
                        z.setMessage(LocaleController.formatStringSimple(this.selectAlertString, UserObject.getUserName(z2)));
                    } else {
                        return;
                    }
                } else if (i == 1) {
                    if (MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(z2))) {
                        z.setMessage(LocaleController.formatStringSimple(this.selectAlertStringGroup, z2.title));
                    } else {
                        return;
                    }
                } else if (z2 == UserConfig.getInstance(this.currentAccount).getClientUserId()) {
                    z.setMessage(LocaleController.formatStringSimple(this.selectAlertStringGroup, LocaleController.getString("SavedMessages", C0446R.string.SavedMessages)));
                } else if (z2 <= false) {
                    if (MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(z2))) {
                        z.setMessage(LocaleController.formatStringSimple(this.selectAlertString, UserObject.getUserName(z2)));
                    } else {
                        return;
                    }
                } else if (z2 >= false) {
                    if (!MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-z2))) {
                        return;
                    }
                    if (this.addToGroupAlertString != null) {
                        z.setMessage(LocaleController.formatStringSimple(this.addToGroupAlertString, z2.title));
                    } else {
                        z.setMessage(LocaleController.formatStringSimple(this.selectAlertStringGroup, z2.title));
                    }
                }
                z.setPositiveButton(LocaleController.getString("OK", C0446R.string.OK), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        DialogsActivity.this.didSelectResult(j, false, false);
                    }
                });
                z.setNegativeButton(LocaleController.getString("Cancel", C0446R.string.Cancel), null);
                showDialog(z.create());
            }
        } else if (this.delegate) {
            z = new ArrayList();
            z.add(Long.valueOf(j));
            this.delegate.didSelectDialogs(this, z, null, z2);
            this.delegate = null;
        } else {
            finishFragment();
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        AnonymousClass17 anonymousClass17 = new ThemeDescriptionDelegate() {
            public void didSetColor() {
                int i;
                View childAt;
                int i2 = 0;
                if (DialogsActivity.this.listView != null) {
                    int childCount = DialogsActivity.this.listView.getChildCount();
                    for (i = 0; i < childCount; i++) {
                        childAt = DialogsActivity.this.listView.getChildAt(i);
                        if (childAt instanceof ProfileSearchCell) {
                            ((ProfileSearchCell) childAt).update(0);
                        } else if (childAt instanceof DialogCell) {
                            ((DialogCell) childAt).update(0);
                        }
                    }
                }
                if (DialogsActivity.this.dialogsSearchAdapter != null) {
                    RecyclerListView innerListView = DialogsActivity.this.dialogsSearchAdapter.getInnerListView();
                    if (innerListView != null) {
                        i = innerListView.getChildCount();
                        while (i2 < i) {
                            childAt = innerListView.getChildAt(i2);
                            if (childAt instanceof HintDialogCell) {
                                ((HintDialogCell) childAt).update();
                            }
                            i2++;
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
        AnonymousClass17 anonymousClass172 = anonymousClass17;
        themeDescriptionArr[18] = new ThemeDescription(null, 0, null, null, null, anonymousClass172, Theme.key_avatar_backgroundRed);
        themeDescriptionArr[19] = new ThemeDescription(null, 0, null, null, null, anonymousClass172, Theme.key_avatar_backgroundOrange);
        themeDescriptionArr[20] = new ThemeDescription(null, 0, null, null, null, anonymousClass172, Theme.key_avatar_backgroundViolet);
        themeDescriptionArr[21] = new ThemeDescription(null, 0, null, null, null, anonymousClass172, Theme.key_avatar_backgroundGreen);
        themeDescriptionArr[22] = new ThemeDescription(null, 0, null, null, null, anonymousClass172, Theme.key_avatar_backgroundCyan);
        themeDescriptionArr[23] = new ThemeDescription(null, 0, null, null, null, anonymousClass172, Theme.key_avatar_backgroundBlue);
        themeDescriptionArr[24] = new ThemeDescription(null, 0, null, null, null, anonymousClass172, Theme.key_avatar_backgroundPink);
        themeDescriptionArr[25] = new ThemeDescription(null, 0, null, null, null, anonymousClass172, Theme.key_avatar_backgroundSaved);
        themeDescriptionArr[26] = new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class}, Theme.dialogs_countPaint, null, null, Theme.key_chats_unreadCounter);
        themeDescriptionArr[27] = new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class}, Theme.dialogs_countGrayPaint, null, null, Theme.key_chats_unreadCounterMuted);
        themeDescriptionArr[28] = new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class}, Theme.dialogs_countTextPaint, null, null, Theme.key_chats_unreadCounterText);
        themeDescriptionArr[29] = new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class, ProfileSearchCell.class}, Theme.dialogs_namePaint, null, null, Theme.key_chats_name);
        themeDescriptionArr[30] = new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class, ProfileSearchCell.class}, Theme.dialogs_nameEncryptedPaint, null, null, Theme.key_chats_secretName);
        themeDescriptionArr[31] = new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class, ProfileSearchCell.class}, null, new Drawable[]{Theme.dialogs_lockDrawable}, null, Theme.key_chats_secretIcon);
        themeDescriptionArr[32] = new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class, ProfileSearchCell.class}, null, new Drawable[]{Theme.dialogs_groupDrawable, Theme.dialogs_broadcastDrawable, Theme.dialogs_botDrawable}, null, Theme.key_chats_nameIcon);
        themeDescriptionArr[33] = new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class}, null, new Drawable[]{Theme.dialogs_pinnedDrawable}, null, Theme.key_chats_pinnedIcon);
        themeDescriptionArr[34] = new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class}, Theme.dialogs_messagePaint, null, null, Theme.key_chats_message);
        themeDescriptionArr[35] = new ThemeDescription(null, 0, null, null, null, anonymousClass172, Theme.key_chats_nameMessage);
        themeDescriptionArr[36] = new ThemeDescription(null, 0, null, null, null, anonymousClass172, Theme.key_chats_draft);
        themeDescriptionArr[37] = new ThemeDescription(null, 0, null, null, null, anonymousClass172, Theme.key_chats_attachMessage);
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
