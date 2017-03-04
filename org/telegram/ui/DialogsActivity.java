package org.telegram.ui;

import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Configuration;
import android.graphics.Outline;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewOutlineProvider;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.query.SearchQuery;
import org.telegram.messenger.query.StickersQuery;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.support.widget.RecyclerView.Adapter;
import org.telegram.messenger.support.widget.RecyclerView.OnScrollListener;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.EncryptedChat;
import org.telegram.tgnet.TLRPC.TL_dialog;
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
import org.telegram.ui.Cells.DialogCell;
import org.telegram.ui.Cells.DividerCell;
import org.telegram.ui.Cells.DrawerActionCell;
import org.telegram.ui.Cells.DrawerProfileCell;
import org.telegram.ui.Cells.GraySectionCell;
import org.telegram.ui.Cells.HashtagSearchCell;
import org.telegram.ui.Cells.HintDialogCell;
import org.telegram.ui.Cells.LoadingCell;
import org.telegram.ui.Cells.ProfileSearchCell;
import org.telegram.ui.Cells.UserCell;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.PlayerView;
import org.telegram.ui.Components.RadialProgressView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;
import org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener;

public class DialogsActivity extends BaseFragment implements NotificationCenterDelegate {
    public static boolean dialogsLoaded;
    private String addToGroupAlertString;
    private boolean cantSendToChannels;
    private boolean checkPermission = true;
    private DialogsActivityDelegate delegate;
    private DialogsAdapter dialogsAdapter;
    private DialogsSearchAdapter dialogsSearchAdapter;
    private int dialogsType;
    private TextView emptyTextView1;
    private TextView emptyTextView2;
    private LinearLayout emptyView;
    private ImageView floatingButton;
    private boolean floatingHidden;
    private final AccelerateDecelerateInterpolator floatingInterpolator = new AccelerateDecelerateInterpolator();
    private LinearLayoutManager layoutManager;
    private RecyclerListView listView;
    private boolean onlySelect;
    private long openedDialogId;
    private ActionBarMenuItem passcodeItem;
    private AlertDialog permissionDialog;
    private PlayerView playerView;
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

    public interface DialogsActivityDelegate {
        void didSelectDialog(DialogsActivity dialogsActivity, long j, boolean z);
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
        }
        if (this.searchString == null) {
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.dialogsNeedReload);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.emojiDidLoaded);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.updateInterfaces);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.encryptedChatUpdated);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.contactsDidLoaded);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.appDidLogout);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.openedChatChanged);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.notificationsSettingsUpdated);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.messageReceivedByAck);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.messageReceivedByServer);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.messageSendError);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.didSetPasscode);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.needReloadRecentDialogsSearch);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.didLoadedReplyMessages);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.reloadHints);
        }
        if (!dialogsLoaded) {
            MessagesController.getInstance().loadDialogs(0, 100, true);
            ContactsController.getInstance().checkInviteText();
            MessagesController.getInstance().loadPinnedDialogs(0, null);
            StickersQuery.checkFeaturedStickers();
            dialogsLoaded = true;
        }
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        if (this.searchString == null) {
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.dialogsNeedReload);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.emojiDidLoaded);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.updateInterfaces);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.encryptedChatUpdated);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.contactsDidLoaded);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.appDidLogout);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.openedChatChanged);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.notificationsSettingsUpdated);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.messageReceivedByAck);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.messageReceivedByServer);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.messageSendError);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didSetPasscode);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.needReloadRecentDialogsSearch);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didLoadedReplyMessages);
            NotificationCenter.getInstance().removeObserver(this, NotificationCenter.reloadHints);
        }
        this.delegate = null;
    }

    public View createView(Context context) {
        float f;
        int i;
        float f2;
        float f3;
        this.searching = false;
        this.searchWas = false;
        final Context context2 = context;
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                Theme.createChatResources(context2, false);
            }
        });
        ActionBarMenu menu = this.actionBar.createMenu();
        if (!this.onlySelect && this.searchString == null) {
            this.passcodeItem = menu.addItem(1, (int) R.drawable.lock_close);
            updatePasscodeButton();
        }
        menu.addItem(0, (int) R.drawable.ic_ab_search).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItemSearchListener() {
            public void onSearchExpand() {
                DialogsActivity.this.searching = true;
                if (DialogsActivity.this.listView != null) {
                    if (DialogsActivity.this.searchString != null) {
                        DialogsActivity.this.listView.setEmptyView(DialogsActivity.this.searchEmptyView);
                        DialogsActivity.this.progressView.setVisibility(8);
                        DialogsActivity.this.emptyView.setVisibility(8);
                    }
                    if (!DialogsActivity.this.onlySelect) {
                        DialogsActivity.this.floatingButton.setVisibility(8);
                    }
                }
                DialogsActivity.this.updatePasscodeButton();
            }

            public boolean canCollapseSearch() {
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
                    DialogsActivity.this.searchEmptyView.setVisibility(8);
                    if (MessagesController.getInstance().loadingDialogs && MessagesController.getInstance().dialogs.isEmpty()) {
                        DialogsActivity.this.emptyView.setVisibility(8);
                        DialogsActivity.this.listView.setEmptyView(DialogsActivity.this.progressView);
                    } else {
                        DialogsActivity.this.progressView.setVisibility(8);
                        DialogsActivity.this.listView.setEmptyView(DialogsActivity.this.emptyView);
                    }
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
                        DialogsActivity.this.emptyView.setVisibility(8);
                        DialogsActivity.this.progressView.setVisibility(8);
                        DialogsActivity.this.searchEmptyView.showTextView();
                        DialogsActivity.this.listView.setEmptyView(DialogsActivity.this.searchEmptyView);
                    }
                }
                if (DialogsActivity.this.dialogsSearchAdapter != null) {
                    DialogsActivity.this.dialogsSearchAdapter.searchDialogs(text);
                }
            }
        }).getSearchField().setHint(LocaleController.getString("Search", R.string.Search));
        if (this.onlySelect) {
            this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
            this.actionBar.setTitle(LocaleController.getString("SelectChat", R.string.SelectChat));
        } else {
            if (this.searchString != null) {
                this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
            } else {
                this.actionBar.setBackButtonDrawable(new MenuDrawable());
            }
            if (BuildVars.DEBUG_VERSION) {
                this.actionBar.setTitle(LocaleController.getString("AppNameBeta", R.string.AppNameBeta));
            } else {
                this.actionBar.setTitle(LocaleController.getString("AppName", R.string.AppName));
            }
        }
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                boolean z = true;
                if (id == -1) {
                    if (DialogsActivity.this.onlySelect) {
                        DialogsActivity.this.finishFragment();
                    } else if (DialogsActivity.this.parentLayout != null) {
                        DialogsActivity.this.parentLayout.getDrawerLayoutContainer().openDrawer(false);
                    }
                } else if (id == 1) {
                    if (UserConfig.appLocked) {
                        z = false;
                    }
                    UserConfig.appLocked = z;
                    UserConfig.saveConfig(false);
                    DialogsActivity.this.updatePasscodeButton();
                }
            }
        });
        if (this.sideMenu != null) {
            this.sideMenu.setBackgroundColor(Theme.getColor(Theme.key_chats_menuBackground));
            this.sideMenu.setGlowColor(Theme.getColor(Theme.key_chats_menuBackground));
            this.sideMenu.getAdapter().notifyDataSetChanged();
        }
        FrameLayout frameLayout = new FrameLayout(context);
        this.fragmentView = frameLayout;
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
        };
        this.layoutManager.setOrientation(1);
        this.listView.setLayoutManager(this.layoutManager);
        this.listView.setVerticalScrollbarPosition(LocaleController.isRTL ? 1 : 2);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(View view, int position) {
                if (DialogsActivity.this.listView != null && DialogsActivity.this.listView.getAdapter() != null) {
                    long dialog_id = 0;
                    int message_id = 0;
                    Adapter adapter = DialogsActivity.this.listView.getAdapter();
                    if (adapter == DialogsActivity.this.dialogsAdapter) {
                        TL_dialog dialog = DialogsActivity.this.dialogsAdapter.getItem(position);
                        if (dialog != null) {
                            dialog_id = dialog.id;
                        } else {
                            return;
                        }
                    } else if (adapter == DialogsActivity.this.dialogsSearchAdapter) {
                        MessageObject obj = DialogsActivity.this.dialogsSearchAdapter.getItem(position);
                        if (obj instanceof User) {
                            dialog_id = (long) ((User) obj).id;
                            if (DialogsActivity.this.dialogsSearchAdapter.isGlobalSearch(position)) {
                                ArrayList<User> users = new ArrayList();
                                users.add((User) obj);
                                MessagesController.getInstance().putUsers(users, false);
                                MessagesStorage.getInstance().putUsersAndChats(users, null, false, true);
                            }
                            if (!DialogsActivity.this.onlySelect) {
                                DialogsActivity.this.dialogsSearchAdapter.putRecentSearch(dialog_id, (User) obj);
                            }
                        } else if (obj instanceof Chat) {
                            if (DialogsActivity.this.dialogsSearchAdapter.isGlobalSearch(position)) {
                                ArrayList<Chat> chats = new ArrayList();
                                chats.add((Chat) obj);
                                MessagesController.getInstance().putChats(chats, false);
                                MessagesStorage.getInstance().putUsersAndChats(null, chats, false, true);
                            }
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
                    if (dialog_id == 0) {
                        return;
                    }
                    if (DialogsActivity.this.onlySelect) {
                        DialogsActivity.this.didSelectResult(dialog_id, true, false);
                        return;
                    }
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
                            Chat chat = MessagesController.getInstance().getChat(Integer.valueOf(-lower_part));
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
                        if (MessagesController.checkCanOpenChat(args, DialogsActivity.this)) {
                            NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[0]);
                            DialogsActivity.this.presentFragment(new ChatActivity(args));
                        }
                    } else if (MessagesController.checkCanOpenChat(args, DialogsActivity.this)) {
                        DialogsActivity.this.presentFragment(new ChatActivity(args));
                    }
                }
            }
        });
        this.listView.setOnItemLongClickListener(new OnItemLongClickListener() {
            public boolean onItemClick(View view, int position) {
                if (!DialogsActivity.this.onlySelect && ((!DialogsActivity.this.searching || !DialogsActivity.this.searchWas) && DialogsActivity.this.getParentActivity() != null)) {
                    ArrayList<TL_dialog> dialogs = DialogsActivity.this.getDialogsArray();
                    if (position < 0 || position >= dialogs.size()) {
                        return false;
                    }
                    TL_dialog dialog = (TL_dialog) dialogs.get(position);
                    DialogsActivity.this.selectedDialog = dialog.id;
                    boolean pinned = dialog.pinned;
                    Builder builder = new Builder(DialogsActivity.this.getParentActivity());
                    int lower_id = (int) DialogsActivity.this.selectedDialog;
                    int high_id = (int) (DialogsActivity.this.selectedDialog >> 32);
                    String string;
                    final boolean z;
                    if (DialogObject.isChannel(dialog)) {
                        CharSequence[] items;
                        final Chat chat = MessagesController.getInstance().getChat(Integer.valueOf(-lower_id));
                        int[] icons = new int[3];
                        icons[0] = dialog.pinned ? R.drawable.chats_unpin : R.drawable.chats_pin;
                        icons[1] = R.drawable.chats_clear;
                        icons[2] = R.drawable.chats_leave;
                        if (chat == null || !chat.megagroup) {
                            items = new CharSequence[3];
                            string = (dialog.pinned || MessagesController.getInstance().canPinDialog(false)) ? dialog.pinned ? LocaleController.getString("UnpinFromTop", R.string.UnpinFromTop) : LocaleController.getString("PinToTop", R.string.PinToTop) : null;
                            items[0] = string;
                            items[1] = LocaleController.getString("ClearHistoryCache", R.string.ClearHistoryCache);
                            if (chat == null || !chat.creator) {
                                string = LocaleController.getString("LeaveChannelMenu", R.string.LeaveChannelMenu);
                            } else {
                                string = LocaleController.getString("ChannelDeleteMenu", R.string.ChannelDeleteMenu);
                            }
                            items[2] = string;
                        } else {
                            items = new CharSequence[3];
                            string = (dialog.pinned || MessagesController.getInstance().canPinDialog(false)) ? dialog.pinned ? LocaleController.getString("UnpinFromTop", R.string.UnpinFromTop) : LocaleController.getString("PinToTop", R.string.PinToTop) : null;
                            items[0] = string;
                            items[1] = LocaleController.getString("ClearHistoryCache", R.string.ClearHistoryCache);
                            string = (chat == null || !chat.creator) ? LocaleController.getString("LeaveMegaMenu", R.string.LeaveMegaMenu) : LocaleController.getString("DeleteMegaMenu", R.string.DeleteMegaMenu);
                            items[2] = string;
                        }
                        z = pinned;
                        builder.setItems(items, icons, new OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                boolean z = true;
                                if (which == 0) {
                                    MessagesController instance = MessagesController.getInstance();
                                    long access$2300 = DialogsActivity.this.selectedDialog;
                                    if (z) {
                                        z = false;
                                    }
                                    if (instance.pinDialog(access$2300, z, null, 0) && !z) {
                                        DialogsActivity.this.listView.smoothScrollToPosition(0);
                                        return;
                                    }
                                    return;
                                }
                                AlertDialog.Builder builder = new AlertDialog.Builder(DialogsActivity.this.getParentActivity());
                                builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                                if (which == 1) {
                                    if (chat == null || !chat.megagroup) {
                                        builder.setMessage(LocaleController.getString("AreYouSureClearHistoryChannel", R.string.AreYouSureClearHistoryChannel));
                                    } else {
                                        builder.setMessage(LocaleController.getString("AreYouSureClearHistorySuper", R.string.AreYouSureClearHistorySuper));
                                    }
                                    builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new OnClickListener() {
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            MessagesController.getInstance().deleteDialog(DialogsActivity.this.selectedDialog, 2);
                                        }
                                    });
                                } else {
                                    if (chat == null || !chat.megagroup) {
                                        if (chat == null || !chat.creator) {
                                            builder.setMessage(LocaleController.getString("ChannelLeaveAlert", R.string.ChannelLeaveAlert));
                                        } else {
                                            builder.setMessage(LocaleController.getString("ChannelDeleteAlert", R.string.ChannelDeleteAlert));
                                        }
                                    } else if (chat.creator) {
                                        builder.setMessage(LocaleController.getString("MegaDeleteAlert", R.string.MegaDeleteAlert));
                                    } else {
                                        builder.setMessage(LocaleController.getString("MegaLeaveAlert", R.string.MegaLeaveAlert));
                                    }
                                    builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new OnClickListener() {
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            MessagesController.getInstance().deleteUserFromChat((int) (-DialogsActivity.this.selectedDialog), UserConfig.getCurrentUser(), null);
                                            if (AndroidUtilities.isTablet()) {
                                                NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, Long.valueOf(DialogsActivity.this.selectedDialog));
                                            }
                                        }
                                    });
                                }
                                builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                                DialogsActivity.this.showDialog(builder.create());
                            }
                        });
                        DialogsActivity.this.showDialog(builder.create());
                    } else {
                        int[] iArr;
                        final boolean isChat = lower_id < 0 && high_id != 1;
                        User user = null;
                        if (!(isChat || lower_id <= 0 || high_id == 1)) {
                            user = MessagesController.getInstance().getUser(Integer.valueOf(lower_id));
                        }
                        final boolean isBot = user != null && user.bot;
                        CharSequence[] charSequenceArr = new CharSequence[3];
                        if (!dialog.pinned) {
                            if (!MessagesController.getInstance().canPinDialog(lower_id == 0)) {
                                string = null;
                                charSequenceArr[0] = string;
                                charSequenceArr[1] = LocaleController.getString("ClearHistory", R.string.ClearHistory);
                                string = isChat ? LocaleController.getString("DeleteChat", R.string.DeleteChat) : isBot ? LocaleController.getString("DeleteAndStop", R.string.DeleteAndStop) : LocaleController.getString("Delete", R.string.Delete);
                                charSequenceArr[2] = string;
                                iArr = new int[3];
                                iArr[0] = dialog.pinned ? R.drawable.chats_unpin : R.drawable.chats_pin;
                                iArr[1] = R.drawable.chats_clear;
                                iArr[2] = isChat ? R.drawable.chats_leave : R.drawable.chats_delete;
                                z = pinned;
                                builder.setItems(charSequenceArr, iArr, new OnClickListener() {
                                    public void onClick(DialogInterface dialog, final int which) {
                                        boolean z = true;
                                        if (which == 0) {
                                            MessagesController instance = MessagesController.getInstance();
                                            long access$2300 = DialogsActivity.this.selectedDialog;
                                            if (z) {
                                                z = false;
                                            }
                                            if (instance.pinDialog(access$2300, z, null, 0) && !z) {
                                                DialogsActivity.this.listView.smoothScrollToPosition(0);
                                                return;
                                            }
                                            return;
                                        }
                                        AlertDialog.Builder builder = new AlertDialog.Builder(DialogsActivity.this.getParentActivity());
                                        builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                                        if (which == 1) {
                                            builder.setMessage(LocaleController.getString("AreYouSureClearHistory", R.string.AreYouSureClearHistory));
                                        } else if (isChat) {
                                            builder.setMessage(LocaleController.getString("AreYouSureDeleteAndExit", R.string.AreYouSureDeleteAndExit));
                                        } else {
                                            builder.setMessage(LocaleController.getString("AreYouSureDeleteThisChat", R.string.AreYouSureDeleteThisChat));
                                        }
                                        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new OnClickListener() {
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                if (which != 1) {
                                                    if (isChat) {
                                                        Chat currentChat = MessagesController.getInstance().getChat(Integer.valueOf((int) (-DialogsActivity.this.selectedDialog)));
                                                        if (currentChat == null || !ChatObject.isNotInChat(currentChat)) {
                                                            MessagesController.getInstance().deleteUserFromChat((int) (-DialogsActivity.this.selectedDialog), MessagesController.getInstance().getUser(Integer.valueOf(UserConfig.getClientUserId())), null);
                                                        } else {
                                                            MessagesController.getInstance().deleteDialog(DialogsActivity.this.selectedDialog, 0);
                                                        }
                                                    } else {
                                                        MessagesController.getInstance().deleteDialog(DialogsActivity.this.selectedDialog, 0);
                                                    }
                                                    if (isBot) {
                                                        MessagesController.getInstance().blockUser((int) DialogsActivity.this.selectedDialog);
                                                    }
                                                    if (AndroidUtilities.isTablet()) {
                                                        NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, Long.valueOf(DialogsActivity.this.selectedDialog));
                                                        return;
                                                    }
                                                    return;
                                                }
                                                MessagesController.getInstance().deleteDialog(DialogsActivity.this.selectedDialog, 1);
                                            }
                                        });
                                        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                                        DialogsActivity.this.showDialog(builder.create());
                                    }
                                });
                                DialogsActivity.this.showDialog(builder.create());
                            }
                        }
                        string = dialog.pinned ? LocaleController.getString("UnpinFromTop", R.string.UnpinFromTop) : LocaleController.getString("PinToTop", R.string.PinToTop);
                        charSequenceArr[0] = string;
                        charSequenceArr[1] = LocaleController.getString("ClearHistory", R.string.ClearHistory);
                        if (isChat) {
                        }
                        charSequenceArr[2] = string;
                        iArr = new int[3];
                        if (dialog.pinned) {
                        }
                        iArr[0] = dialog.pinned ? R.drawable.chats_unpin : R.drawable.chats_pin;
                        iArr[1] = R.drawable.chats_clear;
                        if (isChat) {
                        }
                        iArr[2] = isChat ? R.drawable.chats_leave : R.drawable.chats_delete;
                        z = pinned;
                        builder.setItems(charSequenceArr, iArr, /* anonymous class already generated */);
                        DialogsActivity.this.showDialog(builder.create());
                    }
                    return true;
                } else if (((!DialogsActivity.this.searchWas || !DialogsActivity.this.searching) && !DialogsActivity.this.dialogsSearchAdapter.isRecentSearchDisplayed()) || DialogsActivity.this.listView.getAdapter() != DialogsActivity.this.dialogsSearchAdapter || (!(DialogsActivity.this.dialogsSearchAdapter.getItem(position) instanceof String) && !DialogsActivity.this.dialogsSearchAdapter.isRecentSearchDisplayed())) {
                    return false;
                } else {
                    AlertDialog.Builder builder2 = new AlertDialog.Builder(DialogsActivity.this.getParentActivity());
                    builder2.setTitle(LocaleController.getString("AppName", R.string.AppName));
                    builder2.setMessage(LocaleController.getString("ClearSearch", R.string.ClearSearch));
                    builder2.setPositiveButton(LocaleController.getString("ClearButton", R.string.ClearButton).toUpperCase(), new OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (DialogsActivity.this.dialogsSearchAdapter.isRecentSearchDisplayed()) {
                                DialogsActivity.this.dialogsSearchAdapter.clearRecentSearch();
                            } else {
                                DialogsActivity.this.dialogsSearchAdapter.clearRecentHashtags();
                            }
                        }
                    });
                    builder2.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                    DialogsActivity.this.showDialog(builder2.create());
                    return true;
                }
            }
        });
        this.searchEmptyView = new EmptyTextProgressView(context);
        this.searchEmptyView.setVisibility(8);
        this.searchEmptyView.setShowAtCenter(true);
        this.searchEmptyView.setText(LocaleController.getString("NoResult", R.string.NoResult));
        frameLayout.addView(this.searchEmptyView, LayoutHelper.createFrame(-1, -1.0f));
        this.emptyView = new LinearLayout(context);
        this.emptyView.setOrientation(1);
        this.emptyView.setVisibility(8);
        this.emptyView.setGravity(17);
        frameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f));
        this.emptyView.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        this.emptyTextView1 = new TextView(context);
        this.emptyTextView1.setText(LocaleController.getString("NoChats", R.string.NoChats));
        this.emptyTextView1.setTextColor(Theme.getColor(Theme.key_emptyListPlaceholder));
        this.emptyTextView1.setGravity(17);
        this.emptyTextView1.setTextSize(1, 20.0f);
        this.emptyView.addView(this.emptyTextView1, LayoutHelper.createLinear(-2, -2));
        this.emptyTextView2 = new TextView(context);
        String help = LocaleController.getString("NoChatsHelp", R.string.NoChatsHelp);
        if (AndroidUtilities.isTablet() && !AndroidUtilities.isSmallTablet()) {
            help = help.replace('\n', ' ');
        }
        this.emptyTextView2.setText(help);
        this.emptyTextView2.setTextColor(Theme.getColor(Theme.key_emptyListPlaceholder));
        this.emptyTextView2.setTextSize(1, 15.0f);
        this.emptyTextView2.setGravity(17);
        this.emptyTextView2.setPadding(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(6.0f), AndroidUtilities.dp(8.0f), 0);
        this.emptyTextView2.setLineSpacing((float) AndroidUtilities.dp(2.0f), 1.0f);
        this.emptyView.addView(this.emptyTextView2, LayoutHelper.createLinear(-2, -2));
        this.progressView = new RadialProgressView(context);
        this.progressView.setVisibility(8);
        frameLayout.addView(this.progressView, LayoutHelper.createFrame(-2, -2, 17));
        this.floatingButton = new ImageView(context);
        this.floatingButton.setVisibility(this.onlySelect ? 8 : 0);
        this.floatingButton.setScaleType(ScaleType.CENTER);
        Drawable drawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56.0f), Theme.getColor(Theme.key_chats_actionBackground), Theme.getColor(Theme.key_chats_actionPressedBackground));
        if (VERSION.SDK_INT < 21) {
            Drawable shadowDrawable = context.getResources().getDrawable(R.drawable.floating_shadow).mutate();
            shadowDrawable.setColorFilter(new PorterDuffColorFilter(-16777216, Mode.MULTIPLY));
            Drawable combinedDrawable = new CombinedDrawable(shadowDrawable, drawable, 0, 0);
            combinedDrawable.setIconSize(AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
            drawable = combinedDrawable;
        }
        this.floatingButton.setBackgroundDrawable(drawable);
        this.floatingButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chats_actionIcon), Mode.MULTIPLY));
        this.floatingButton.setImageResource(R.drawable.floating_pencil);
        if (VERSION.SDK_INT >= 21) {
            StateListAnimator animator = new StateListAnimator();
            animator.addState(new int[]{16842919}, ObjectAnimator.ofFloat(this.floatingButton, "translationZ", new float[]{(float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(4.0f)}).setDuration(200));
            animator.addState(new int[0], ObjectAnimator.ofFloat(this.floatingButton, "translationZ", new float[]{(float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(2.0f)}).setDuration(200));
            this.floatingButton.setStateListAnimator(animator);
            this.floatingButton.setOutlineProvider(new ViewOutlineProvider() {
                @SuppressLint({"NewApi"})
                public void getOutline(View view, Outline outline) {
                    outline.setOval(0, 0, AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
                }
            });
        }
        View view = this.floatingButton;
        int i2 = VERSION.SDK_INT >= 21 ? 56 : 60;
        if (VERSION.SDK_INT >= 21) {
            f = 56.0f;
        } else {
            f = BitmapDescriptorFactory.HUE_YELLOW;
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
        frameLayout.addView(view, LayoutHelper.createFrame(i2, f, i, f2, 0.0f, f3, 14.0f));
        this.floatingButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Bundle args = new Bundle();
                args.putBoolean("destroyAfterSelect", true);
                DialogsActivity.this.presentFragment(new ContactsActivity(args));
            }
        });
        this.listView.setOnScrollListener(new OnScrollListener() {
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == 1 && DialogsActivity.this.searching && DialogsActivity.this.searchWas) {
                    AndroidUtilities.hideKeyboard(DialogsActivity.this.getParentActivity().getCurrentFocus());
                }
            }

            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int firstVisibleItem = DialogsActivity.this.layoutManager.findFirstVisibleItemPosition();
                int visibleItemCount = Math.abs(DialogsActivity.this.layoutManager.findLastVisibleItemPosition() - firstVisibleItem) + 1;
                int totalItemCount = recyclerView.getAdapter().getItemCount();
                if (!DialogsActivity.this.searching || !DialogsActivity.this.searchWas) {
                    if (visibleItemCount > 0 && DialogsActivity.this.layoutManager.findLastVisibleItemPosition() >= DialogsActivity.this.getDialogsArray().size() - 10) {
                        MessagesController.getInstance().loadDialogs(-1, 100, !MessagesController.getInstance().dialogsEndReached);
                    }
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
                        if (changed && DialogsActivity.this.scrollUpdated) {
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
            this.dialogsAdapter = new DialogsAdapter(context, this.dialogsType);
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

            public void didPressedOnSubDialog(int did) {
                if (DialogsActivity.this.onlySelect) {
                    DialogsActivity.this.didSelectResult((long) did, true, false);
                    return;
                }
                Bundle args = new Bundle();
                if (did > 0) {
                    args.putInt("user_id", did);
                } else {
                    args.putInt("chat_id", -did);
                }
                if (DialogsActivity.this.actionBar != null) {
                    DialogsActivity.this.actionBar.closeSearchField();
                }
                if (AndroidUtilities.isTablet() && DialogsActivity.this.dialogsAdapter != null) {
                    DialogsActivity.this.dialogsAdapter.setOpenedDialogId(DialogsActivity.this.openedDialogId = (long) did);
                    DialogsActivity.this.updateVisibleRows(512);
                }
                if (DialogsActivity.this.searchString != null) {
                    if (MessagesController.checkCanOpenChat(args, DialogsActivity.this)) {
                        NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[0]);
                        DialogsActivity.this.presentFragment(new ChatActivity(args));
                    }
                } else if (MessagesController.checkCanOpenChat(args, DialogsActivity.this)) {
                    DialogsActivity.this.presentFragment(new ChatActivity(args));
                }
            }

            public void needRemoveHint(final int did) {
                if (DialogsActivity.this.getParentActivity() != null && MessagesController.getInstance().getUser(Integer.valueOf(did)) != null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(DialogsActivity.this.getParentActivity());
                    builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                    builder.setMessage(LocaleController.formatString("ChatHintsDelete", R.string.ChatHintsDelete, ContactsController.formatName(user.first_name, user.last_name)));
                    builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            SearchQuery.removePeer(did);
                        }
                    });
                    builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                    DialogsActivity.this.showDialog(builder.create());
                }
            }
        });
        if (MessagesController.getInstance().loadingDialogs && MessagesController.getInstance().dialogs.isEmpty()) {
            this.searchEmptyView.setVisibility(8);
            this.emptyView.setVisibility(8);
            this.listView.setEmptyView(this.progressView);
        } else {
            this.searchEmptyView.setVisibility(8);
            this.progressView.setVisibility(8);
            this.listView.setEmptyView(this.emptyView);
        }
        if (this.searchString != null) {
            this.actionBar.openSearchField(this.searchString);
        }
        if (!this.onlySelect && this.dialogsType == 0) {
            View playerView = new PlayerView(context, this);
            this.playerView = playerView;
            frameLayout.addView(playerView, LayoutHelper.createFrame(-1, 39.0f, 51, 0.0f, -36.0f, 0.0f, 0.0f));
        }
        return this.fragmentView;
    }

    public void onResume() {
        super.onResume();
        if (this.dialogsAdapter != null) {
            this.dialogsAdapter.notifyDataSetChanged();
        }
        if (this.dialogsSearchAdapter != null) {
            this.dialogsSearchAdapter.notifyDataSetChanged();
        }
        if (this.checkPermission && !this.onlySelect && VERSION.SDK_INT >= 23) {
            Activity activity = getParentActivity();
            if (activity != null) {
                this.checkPermission = false;
                if (activity.checkSelfPermission("android.permission.READ_CONTACTS") != 0 || activity.checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") != 0) {
                    AlertDialog.Builder builder;
                    Dialog create;
                    if (activity.shouldShowRequestPermissionRationale("android.permission.READ_CONTACTS")) {
                        builder = new AlertDialog.Builder(activity);
                        builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                        builder.setMessage(LocaleController.getString("PermissionContacts", R.string.PermissionContacts));
                        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
                        create = builder.create();
                        this.permissionDialog = create;
                        showDialog(create);
                    } else if (activity.shouldShowRequestPermissionRationale("android.permission.WRITE_EXTERNAL_STORAGE")) {
                        builder = new AlertDialog.Builder(activity);
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
            activity.requestPermissions((String[]) permissons.toArray(new String[permissons.size()]), 1);
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
                    DialogsActivity.this.floatingButton.setClickable(!DialogsActivity.this.floatingHidden);
                    if (DialogsActivity.this.floatingButton == null) {
                        return;
                    }
                    if (VERSION.SDK_INT < 16) {
                        DialogsActivity.this.floatingButton.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    } else {
                        DialogsActivity.this.floatingButton.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                }
            });
        }
    }

    public void onRequestPermissionsResultFragment(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1) {
            int a = 0;
            while (a < permissions.length) {
                if (grantResults.length > a && grantResults[a] == 0) {
                    String str = permissions[a];
                    Object obj = -1;
                    switch (str.hashCode()) {
                        case 1365911975:
                            if (str.equals("android.permission.WRITE_EXTERNAL_STORAGE")) {
                                int i = 1;
                                break;
                            }
                            break;
                        case 1977429404:
                            if (str.equals("android.permission.READ_CONTACTS")) {
                                obj = null;
                                break;
                            }
                            break;
                    }
                    switch (obj) {
                        case null:
                            ContactsController.getInstance().readContacts();
                            break;
                        case 1:
                            ImageLoader.getInstance().checkMediaPaths();
                            break;
                        default:
                            break;
                    }
                }
                a++;
            }
        }
    }

    public void didReceivedNotification(int id, Object... args) {
        if (id == NotificationCenter.dialogsNeedReload) {
            if (this.dialogsAdapter != null) {
                if (this.dialogsAdapter.isDataSetChanged()) {
                    this.dialogsAdapter.notifyDataSetChanged();
                } else {
                    updateVisibleRows(2048);
                }
            }
            if (this.dialogsSearchAdapter != null) {
                this.dialogsSearchAdapter.notifyDataSetChanged();
            }
            if (this.listView == null) {
                return;
            }
            if (MessagesController.getInstance().loadingDialogs && MessagesController.getInstance().dialogs.isEmpty()) {
                this.searchEmptyView.setVisibility(8);
                this.emptyView.setVisibility(8);
                this.listView.setEmptyView(this.progressView);
                return;
            }
            try {
                this.progressView.setVisibility(8);
                if (this.searching && this.searchWas) {
                    this.emptyView.setVisibility(8);
                    this.listView.setEmptyView(this.searchEmptyView);
                    return;
                }
                this.searchEmptyView.setVisibility(8);
                this.listView.setEmptyView(this.emptyView);
            } catch (Throwable e) {
                FileLog.e(e);
            }
        } else if (id == NotificationCenter.emojiDidLoaded) {
            updateVisibleRows(0);
        } else if (id == NotificationCenter.updateInterfaces) {
            updateVisibleRows(((Integer) args[0]).intValue());
        } else if (id == NotificationCenter.appDidLogout) {
            dialogsLoaded = false;
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
        } else if (id == NotificationCenter.messageReceivedByAck || id == NotificationCenter.messageReceivedByServer || id == NotificationCenter.messageSendError) {
            updateVisibleRows(4096);
        } else if (id == NotificationCenter.didSetPasscode) {
            updatePasscodeButton();
        } else if (id == NotificationCenter.needReloadRecentDialogsSearch) {
            if (this.dialogsSearchAdapter != null) {
                this.dialogsSearchAdapter.loadRecentSearch();
            }
        } else if (id == NotificationCenter.didLoadedReplyMessages) {
            updateVisibleRows(0);
        } else if (id == NotificationCenter.reloadHints && this.dialogsSearchAdapter != null) {
            this.dialogsSearchAdapter.notifyDataSetChanged();
        }
    }

    private ArrayList<TL_dialog> getDialogsArray() {
        if (this.dialogsType == 0) {
            return MessagesController.getInstance().dialogs;
        }
        if (this.dialogsType == 1) {
            return MessagesController.getInstance().dialogsServerOnly;
        }
        if (this.dialogsType == 2) {
            return MessagesController.getInstance().dialogsGroupsOnly;
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
            if (UserConfig.passcodeHash.length() == 0 || this.searching) {
                this.passcodeItem.setVisibility(8);
                return;
            }
            this.passcodeItem.setVisibility(0);
            if (UserConfig.appLocked) {
                this.passcodeItem.setIcon(R.drawable.lock_close);
            } else {
                this.passcodeItem.setIcon(R.drawable.lock_open);
            }
        }
    }

    private void hideFloatingButton(boolean hide) {
        if (this.floatingHidden != hide) {
            boolean z;
            this.floatingHidden = hide;
            ImageView imageView = this.floatingButton;
            String str = "translationY";
            float[] fArr = new float[1];
            fArr[0] = this.floatingHidden ? (float) AndroidUtilities.dp(100.0f) : 0.0f;
            ObjectAnimator animator = ObjectAnimator.ofFloat(imageView, str, fArr).setDuration(300);
            animator.setInterpolator(this.floatingInterpolator);
            imageView = this.floatingButton;
            if (hide) {
                z = false;
            } else {
                z = true;
            }
            imageView.setClickable(z);
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

    private void didSelectResult(final long dialog_id, boolean useAlert, boolean param) {
        Chat chat;
        if (this.addToGroupAlertString == null && ((int) dialog_id) < 0) {
            chat = MessagesController.getInstance().getChat(Integer.valueOf(-((int) dialog_id)));
            if (ChatObject.isChannel(chat) && !chat.megagroup && (this.cantSendToChannels || !ChatObject.isCanWriteToChannel(-((int) dialog_id)))) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
                builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                builder.setMessage(LocaleController.getString("ChannelCantSendMessage", R.string.ChannelCantSendMessage));
                builder.setNegativeButton(LocaleController.getString("OK", R.string.OK), null);
                showDialog(builder.create());
                return;
            }
        }
        if (!useAlert || ((this.selectAlertString == null || this.selectAlertStringGroup == null) && this.addToGroupAlertString == null)) {
            if (this.delegate != null) {
                this.delegate.didSelectDialog(this, dialog_id, param);
                this.delegate = null;
                return;
            }
            finishFragment();
        } else if (getParentActivity() != null) {
            builder = new AlertDialog.Builder(getParentActivity());
            builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
            int lower_part = (int) dialog_id;
            int high_id = (int) (dialog_id >> 32);
            if (lower_part == 0) {
                if (MessagesController.getInstance().getUser(Integer.valueOf(MessagesController.getInstance().getEncryptedChat(Integer.valueOf(high_id)).user_id)) != null) {
                    builder.setMessage(LocaleController.formatStringSimple(this.selectAlertString, UserObject.getUserName(user)));
                } else {
                    return;
                }
            } else if (high_id == 1) {
                if (MessagesController.getInstance().getChat(Integer.valueOf(lower_part)) != null) {
                    builder.setMessage(LocaleController.formatStringSimple(this.selectAlertStringGroup, chat.title));
                } else {
                    return;
                }
            } else if (lower_part > 0) {
                if (MessagesController.getInstance().getUser(Integer.valueOf(lower_part)) != null) {
                    builder.setMessage(LocaleController.formatStringSimple(this.selectAlertString, UserObject.getUserName(user)));
                } else {
                    return;
                }
            } else if (lower_part < 0) {
                if (MessagesController.getInstance().getChat(Integer.valueOf(-lower_part)) == null) {
                    return;
                }
                if (this.addToGroupAlertString != null) {
                    builder.setMessage(LocaleController.formatStringSimple(this.addToGroupAlertString, chat.title));
                } else {
                    builder.setMessage(LocaleController.formatStringSimple(this.selectAlertStringGroup, chat.title));
                }
            }
            builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    DialogsActivity.this.didSelectResult(dialog_id, false, false);
                }
            });
            builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
            showDialog(builder.create());
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        ThemeDescriptionDelegate сellDelegate = new ThemeDescriptionDelegate() {
            public void didSetColor(int color) {
                int a;
                int count = DialogsActivity.this.listView.getChildCount();
                for (a = 0; a < count; a++) {
                    View child = DialogsActivity.this.listView.getChildAt(a);
                    if (child instanceof ProfileSearchCell) {
                        ((ProfileSearchCell) child).update(0);
                    } else if (child instanceof DialogCell) {
                        ((DialogCell) child).update(0);
                    }
                }
                RecyclerListView recyclerListView = DialogsActivity.this.dialogsSearchAdapter.getInnerListView();
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
        };
        ThemeDescription[] themeDescriptionArr = new ThemeDescription[110];
        themeDescriptionArr[0] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite);
        themeDescriptionArr[1] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault);
        themeDescriptionArr[2] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault);
        themeDescriptionArr[3] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon);
        themeDescriptionArr[4] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle);
        themeDescriptionArr[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector);
        themeDescriptionArr[6] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCH, null, null, null, null, Theme.key_actionBarDefaultSearch);
        themeDescriptionArr[7] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCHPLACEHOLDER, null, null, null, null, Theme.key_actionBarDefaultSearchPlaceholder);
        themeDescriptionArr[8] = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector);
        themeDescriptionArr[9] = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelectorSDK21);
        themeDescriptionArr[10] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider);
        themeDescriptionArr[11] = new ThemeDescription(this.searchEmptyView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_emptyListPlaceholder);
        themeDescriptionArr[12] = new ThemeDescription(this.searchEmptyView, ThemeDescription.FLAG_PROGRESSBAR, null, null, null, null, Theme.key_progressCircle);
        themeDescriptionArr[13] = new ThemeDescription(this.emptyTextView1, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_emptyListPlaceholder);
        themeDescriptionArr[14] = new ThemeDescription(this.emptyTextView2, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_emptyListPlaceholder);
        themeDescriptionArr[15] = new ThemeDescription(this.floatingButton, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, Theme.key_chats_actionIcon);
        themeDescriptionArr[16] = new ThemeDescription(this.floatingButton, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_chats_actionBackground);
        themeDescriptionArr[17] = new ThemeDescription(this.floatingButton, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, Theme.key_chats_actionPressedBackground);
        themeDescriptionArr[18] = new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class, ProfileSearchCell.class}, null, new Drawable[]{Theme.avatar_photoDrawable, Theme.avatar_broadcastDrawable}, null, Theme.key_avatar_text);
        themeDescriptionArr[19] = new ThemeDescription(null, 0, null, null, null, сellDelegate, Theme.key_avatar_backgroundRed);
        themeDescriptionArr[20] = new ThemeDescription(null, 0, null, null, null, сellDelegate, Theme.key_avatar_backgroundOrange);
        themeDescriptionArr[21] = new ThemeDescription(null, 0, null, null, null, сellDelegate, Theme.key_avatar_backgroundViolet);
        themeDescriptionArr[22] = new ThemeDescription(null, 0, null, null, null, сellDelegate, Theme.key_avatar_backgroundGreen);
        themeDescriptionArr[23] = new ThemeDescription(null, 0, null, null, null, сellDelegate, Theme.key_avatar_backgroundCyan);
        themeDescriptionArr[24] = new ThemeDescription(null, 0, null, null, null, сellDelegate, Theme.key_avatar_backgroundBlue);
        themeDescriptionArr[25] = new ThemeDescription(null, 0, null, null, null, сellDelegate, Theme.key_avatar_backgroundPink);
        themeDescriptionArr[26] = new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class}, Theme.dialogs_countPaint, null, null, Theme.key_chats_unreadCounter);
        themeDescriptionArr[27] = new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class}, Theme.dialogs_countGrayPaint, null, null, Theme.key_chats_unreadCounterMuted);
        themeDescriptionArr[28] = new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class}, Theme.dialogs_countTextPaint, null, null, Theme.key_chats_unreadCounterText);
        themeDescriptionArr[29] = new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class, ProfileSearchCell.class}, Theme.dialogs_namePaint, null, null, Theme.key_chats_name);
        themeDescriptionArr[30] = new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class, ProfileSearchCell.class}, Theme.dialogs_nameEncryptedPaint, null, null, Theme.key_chats_secretName);
        themeDescriptionArr[31] = new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class, ProfileSearchCell.class}, null, new Drawable[]{Theme.dialogs_lockDrawable}, null, Theme.key_chats_secretIcon);
        themeDescriptionArr[32] = new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class, ProfileSearchCell.class}, null, new Drawable[]{Theme.dialogs_groupDrawable, Theme.dialogs_broadcastDrawable, Theme.dialogs_botDrawable}, null, Theme.key_chats_nameIcon);
        themeDescriptionArr[33] = new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class}, null, new Drawable[]{Theme.dialogs_pinnedDrawable}, null, Theme.key_chats_pinnedIcon);
        themeDescriptionArr[34] = new ThemeDescription(this.listView, 0, new Class[]{DialogCell.class}, Theme.dialogs_messagePaint, null, null, Theme.key_chats_message);
        themeDescriptionArr[35] = new ThemeDescription(null, 0, null, null, null, сellDelegate, Theme.key_chats_nameMessage);
        themeDescriptionArr[36] = new ThemeDescription(null, 0, null, null, null, сellDelegate, Theme.key_chats_draft);
        themeDescriptionArr[37] = new ThemeDescription(null, 0, null, null, null, сellDelegate, Theme.key_chats_attachMessage);
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
        themeDescriptionArr[54] = new ThemeDescription(this.sideMenu, 0, new Class[]{DrawerProfileCell.class}, new String[]{"cloudDrawable"}, null, null, null, Theme.key_chats_menuCloud);
        themeDescriptionArr[55] = new ThemeDescription(this.sideMenu, 0, new Class[]{DrawerProfileCell.class}, null, null, null, Theme.key_chat_serviceBackground);
        themeDescriptionArr[56] = new ThemeDescription(this.sideMenu, 0, new Class[]{DrawerProfileCell.class}, null, null, null, Theme.key_chats_menuTopShadow);
        themeDescriptionArr[57] = new ThemeDescription(this.sideMenu, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{DrawerActionCell.class}, new String[]{"textView"}, null, null, null, Theme.key_chats_menuItemIcon);
        themeDescriptionArr[58] = new ThemeDescription(this.sideMenu, 0, new Class[]{DrawerActionCell.class}, new String[]{"textView"}, null, null, null, Theme.key_chats_menuItemText);
        themeDescriptionArr[59] = new ThemeDescription(this.sideMenu, 0, new Class[]{DividerCell.class}, Theme.dividerPaint, null, null, Theme.key_divider);
        themeDescriptionArr[60] = new ThemeDescription(this.listView, 0, new Class[]{LoadingCell.class}, new String[]{"progressBar"}, null, null, null, Theme.key_progressCircle);
        themeDescriptionArr[61] = new ThemeDescription(this.listView, 0, new Class[]{ProfileSearchCell.class}, Theme.dialogs_offlinePaint, null, null, Theme.key_windowBackgroundWhiteGrayText3);
        themeDescriptionArr[62] = new ThemeDescription(this.listView, 0, new Class[]{ProfileSearchCell.class}, Theme.dialogs_onlinePaint, null, null, Theme.key_windowBackgroundWhiteBlueText3);
        themeDescriptionArr[63] = new ThemeDescription(this.listView, 0, new Class[]{GraySectionCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText2);
        themeDescriptionArr[64] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{GraySectionCell.class}, null, null, null, Theme.key_graySection);
        themeDescriptionArr[65] = new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{HashtagSearchCell.class}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        themeDescriptionArr[66] = new ThemeDescription(this.progressView, ThemeDescription.FLAG_PROGRESSBAR, null, null, null, null, Theme.key_progressCircle);
        themeDescriptionArr[67] = new ThemeDescription(this.dialogsSearchAdapter.getInnerListView(), 0, new Class[]{HintDialogCell.class}, Theme.dialogs_countPaint, null, null, Theme.key_chats_unreadCounter);
        themeDescriptionArr[68] = new ThemeDescription(this.dialogsSearchAdapter.getInnerListView(), 0, new Class[]{HintDialogCell.class}, Theme.dialogs_countGrayPaint, null, null, Theme.key_chats_unreadCounterMuted);
        themeDescriptionArr[69] = new ThemeDescription(this.dialogsSearchAdapter.getInnerListView(), 0, new Class[]{HintDialogCell.class}, Theme.dialogs_countTextPaint, null, null, Theme.key_chats_unreadCounterText);
        themeDescriptionArr[70] = new ThemeDescription(this.dialogsSearchAdapter.getInnerListView(), 0, new Class[]{HintDialogCell.class}, new String[]{"nameTextView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        themeDescriptionArr[71] = new ThemeDescription(this.playerView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{PlayerView.class}, new String[]{"frameLayout"}, null, null, null, Theme.key_inappPlayerBackground);
        themeDescriptionArr[72] = new ThemeDescription(this.playerView, 0, new Class[]{PlayerView.class}, new String[]{"playButton"}, null, null, null, Theme.key_inappPlayerPlayPause);
        themeDescriptionArr[73] = new ThemeDescription(this.playerView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{PlayerView.class}, new String[]{"titleTextView"}, null, null, null, Theme.key_inappPlayerTitle);
        themeDescriptionArr[74] = new ThemeDescription(this.playerView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{PlayerView.class}, new String[]{"frameLayout"}, null, null, null, Theme.key_inappPlayerPerformer);
        themeDescriptionArr[75] = new ThemeDescription(this.playerView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{PlayerView.class}, new String[]{"closeButton"}, null, null, null, Theme.key_inappPlayerClose);
        themeDescriptionArr[76] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogBackground);
        themeDescriptionArr[77] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogTextBlack);
        themeDescriptionArr[78] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogTextLink);
        themeDescriptionArr[79] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogLinkSelection);
        themeDescriptionArr[80] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogTextBlue);
        themeDescriptionArr[81] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogTextBlue2);
        themeDescriptionArr[82] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogTextBlue3);
        themeDescriptionArr[83] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogTextBlue4);
        themeDescriptionArr[84] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogTextRed);
        themeDescriptionArr[85] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogTextGray);
        themeDescriptionArr[86] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogTextGray2);
        themeDescriptionArr[87] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogTextGray3);
        themeDescriptionArr[88] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogTextGray4);
        themeDescriptionArr[89] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogIcon);
        themeDescriptionArr[90] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogTextHint);
        themeDescriptionArr[91] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogInputField);
        themeDescriptionArr[92] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogInputFieldActivated);
        themeDescriptionArr[93] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogCheckboxSquareBackground);
        themeDescriptionArr[94] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogCheckboxSquareCheck);
        themeDescriptionArr[95] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogCheckboxSquareUnchecked);
        themeDescriptionArr[96] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogCheckboxSquareDisabled);
        themeDescriptionArr[97] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogRadioBackground);
        themeDescriptionArr[98] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogRadioBackgroundChecked);
        themeDescriptionArr[99] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogProgressCircle);
        themeDescriptionArr[100] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogButton);
        themeDescriptionArr[101] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogButtonSelector);
        themeDescriptionArr[102] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogScrollGlow);
        themeDescriptionArr[103] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogRoundCheckBox);
        themeDescriptionArr[104] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogRoundCheckBoxCheck);
        themeDescriptionArr[105] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogBadgeBackground);
        themeDescriptionArr[106] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogBadgeText);
        themeDescriptionArr[107] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogLineProgress);
        themeDescriptionArr[108] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogLineProgressBackground);
        themeDescriptionArr[109] = new ThemeDescription(null, 0, null, null, null, null, Theme.key_dialogGrayLine);
        return themeDescriptionArr;
    }
}
