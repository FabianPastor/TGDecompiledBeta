package org.telegram.p005ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.annotation.Keep;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.exoplayer2.CLASSNAMEC;
import com.google.android.exoplayer2.DefaultLoadControl;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.CountDownLatch;
import org.telegram.PhoneFormat.CLASSNAMEPhoneFormat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DataQuery;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.NotificationsController;
import org.telegram.messenger.SecretChatHelper;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.support.widget.RecyclerView.LayoutParams;
import org.telegram.messenger.support.widget.RecyclerView.OnScrollListener;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.messenger.support.widget.helper.ItemTouchHelper.Callback;
import org.telegram.p005ui.ActionBar.ActionBarMenu;
import org.telegram.p005ui.ActionBar.ActionBarMenuItem;
import org.telegram.p005ui.ActionBar.AlertDialog.Builder;
import org.telegram.p005ui.ActionBar.BackDrawable;
import org.telegram.p005ui.ActionBar.BaseFragment;
import org.telegram.p005ui.ActionBar.CLASSNAMEActionBar;
import org.telegram.p005ui.ActionBar.CLASSNAMEActionBar.ActionBarMenuOnItemClick;
import org.telegram.p005ui.ActionBar.SimpleTextView;
import org.telegram.p005ui.ActionBar.Theme;
import org.telegram.p005ui.ActionBar.ThemeDescription;
import org.telegram.p005ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate;
import org.telegram.p005ui.Cells.AboutLinkCell;
import org.telegram.p005ui.Cells.DividerCell;
import org.telegram.p005ui.Cells.EmptyCell;
import org.telegram.p005ui.Cells.HeaderCell;
import org.telegram.p005ui.Cells.NotificationsCheckCell;
import org.telegram.p005ui.Cells.ShadowSectionCell;
import org.telegram.p005ui.Cells.TextCell;
import org.telegram.p005ui.Cells.TextDetailCell;
import org.telegram.p005ui.Cells.TextInfoPrivacyCell;
import org.telegram.p005ui.Cells.UserCell;
import org.telegram.p005ui.Components.AlertsCreator;
import org.telegram.p005ui.Components.AvatarDrawable;
import org.telegram.p005ui.Components.BackupImageView;
import org.telegram.p005ui.Components.CombinedDrawable;
import org.telegram.p005ui.Components.IdenticonDrawable;
import org.telegram.p005ui.Components.LayoutHelper;
import org.telegram.p005ui.Components.RecyclerListView;
import org.telegram.p005ui.Components.RecyclerListView.Holder;
import org.telegram.p005ui.Components.RecyclerListView.SelectionAdapter;
import org.telegram.p005ui.Components.voip.VoIPHelper;
import org.telegram.p005ui.DialogsActivity.DialogsActivityDelegate;
import org.telegram.p005ui.MediaActivity.SharedMediaData;
import org.telegram.p005ui.PhotoViewer.EmptyPhotoViewerProvider;
import org.telegram.p005ui.PhotoViewer.PhotoViewerProvider;
import org.telegram.p005ui.PhotoViewer.PlaceProviderObject;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.BotInfo;
import org.telegram.tgnet.TLRPC.ChannelParticipant;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.ChatFull;
import org.telegram.tgnet.TLRPC.ChatParticipant;
import org.telegram.tgnet.TLRPC.EncryptedChat;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.TL_channelAdminRights;
import org.telegram.tgnet.TLRPC.TL_channelBannedRights;
import org.telegram.tgnet.TLRPC.TL_channelFull;
import org.telegram.tgnet.TLRPC.TL_channelParticipant;
import org.telegram.tgnet.TLRPC.TL_channelParticipantAdmin;
import org.telegram.tgnet.TLRPC.TL_channelParticipantBanned;
import org.telegram.tgnet.TLRPC.TL_channelParticipantCreator;
import org.telegram.tgnet.TLRPC.TL_channelParticipantsRecent;
import org.telegram.tgnet.TLRPC.TL_channels_channelParticipant;
import org.telegram.tgnet.TLRPC.TL_channels_channelParticipants;
import org.telegram.tgnet.TLRPC.TL_channels_getParticipant;
import org.telegram.tgnet.TLRPC.TL_channels_getParticipants;
import org.telegram.tgnet.TLRPC.TL_chatChannelParticipant;
import org.telegram.tgnet.TLRPC.TL_chatFull;
import org.telegram.tgnet.TLRPC.TL_chatParticipant;
import org.telegram.tgnet.TLRPC.TL_chatParticipantAdmin;
import org.telegram.tgnet.TLRPC.TL_chatParticipantCreator;
import org.telegram.tgnet.TLRPC.TL_chatParticipants;
import org.telegram.tgnet.TLRPC.TL_chatParticipantsForbidden;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageActionSetMessageTTL;
import org.telegram.tgnet.TLRPC.TL_dialog;
import org.telegram.tgnet.TLRPC.TL_encryptedChat;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_messageEncryptedAction;
import org.telegram.tgnet.TLRPC.TL_peerNotifySettings;
import org.telegram.tgnet.TLRPC.TL_userEmpty;
import org.telegram.tgnet.TLRPC.TL_userFull;
import org.telegram.tgnet.TLRPC.User;

/* renamed from: org.telegram.ui.ProfileActivity */
public class ProfileActivity extends BaseFragment implements NotificationCenterDelegate, DialogsActivityDelegate {
    private static final int add_contact = 1;
    private static final int add_member = 18;
    private static final int add_shortcut = 14;
    private static final int block_contact = 2;
    private static final int call_item = 15;
    private static final int convert_to_supergroup = 13;
    private static final int delete_contact = 5;
    private static final int edit_channel = 12;
    private static final int edit_contact = 4;
    private static final int edit_name = 8;
    private static final int invite_to_group = 9;
    private static final int leave_group = 7;
    private static final int search_members = 17;
    private static final int set_admins = 11;
    private static final int share = 10;
    private static final int share_contact = 3;
    private ActionBarMenuItem addItem;
    private int addMemberRow;
    private int administratorsRow;
    private boolean allowProfileAnimation = true;
    private ActionBarMenuItem animatingItem;
    private float animationProgress;
    private int audioRow;
    private AvatarDrawable avatarDrawable;
    private BackupImageView avatarImage;
    private int banFromGroup;
    private int blockedUsersRow;
    private BotInfo botInfo;
    private ActionBarMenuItem callItem;
    private int channelInfoRow;
    private ChatFull chatInfo;
    private int chat_id;
    private int convertHelpRow;
    private int convertRow;
    private boolean creatingChat;
    private ChannelParticipant currentChannelParticipant;
    private Chat currentChat;
    private EncryptedChat currentEncryptedChat;
    private long dialog_id;
    private ActionBarMenuItem editItem;
    private int emptyRow;
    private int extraHeight;
    private int filesRow;
    private int groupsInCommonRow;
    private int infoHeaderRow;
    private int infoSectionRow;
    private int initialAnimationExtraHeight;
    private boolean isBot;
    private int joinRow;
    private int[] lastMediaCount = new int[]{-1, -1, -1, -1, -1};
    private int lastSectionRow;
    private LinearLayoutManager layoutManager;
    private int leaveChannelRow;
    private int linksRow;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    private boolean loadingUsers;
    private int[] mediaCount = new int[]{-1, -1, -1, -1, -1};
    private int[] mediaMergeCount = new int[]{-1, -1, -1, -1, -1};
    private int membersEndRow;
    private int membersHeaderRow;
    private int membersSectionRow;
    private int membersStartRow;
    private long mergeDialogId;
    private SimpleTextView[] nameTextView = new SimpleTextView[2];
    private int notificationsDividerRow;
    private int notificationsRow;
    private int onlineCount = -1;
    private SimpleTextView[] onlineTextView = new SimpleTextView[2];
    private boolean openAnimationInProgress;
    private SparseArray<ChatParticipant> participantsMap = new SparseArray();
    private int phoneRow;
    private int photosRow;
    private boolean playProfileAnimation;
    private int[] prevMediaCount = new int[]{-1, -1, -1, -1, -1};
    private PhotoViewerProvider provider = new CLASSNAME();
    private boolean recreateMenuAfterAnimation;
    private int rowCount;
    private int selectedUser;
    private int settingsKeyRow;
    private int settingsSectionRow;
    private int settingsTimerRow;
    private int sharedHeaderRow;
    private SharedMediaData[] sharedMediaData;
    private int sharedSectionRow;
    private ArrayList<Integer> sortedUsers;
    private int startSecretChatRow;
    private int subscribersRow;
    private TopView topView;
    private boolean userBlocked;
    private TL_userFull userInfo;
    private int userInfoRow;
    private int user_id;
    private int usernameRow;
    private boolean usersEndReached;
    private int voiceRow;
    private ImageView writeButton;
    private AnimatorSet writeButtonAnimation;

    /* renamed from: org.telegram.ui.ProfileActivity$10 */
    class CLASSNAME extends AnimatorListenerAdapter {
        CLASSNAME() {
        }

        public void onAnimationEnd(Animator animation) {
            if (ProfileActivity.this.writeButtonAnimation != null && ProfileActivity.this.writeButtonAnimation.equals(animation)) {
                ProfileActivity.this.writeButtonAnimation = null;
            }
        }
    }

    /* renamed from: org.telegram.ui.ProfileActivity$11 */
    class CLASSNAME implements OnPreDrawListener {
        CLASSNAME() {
        }

        public boolean onPreDraw() {
            if (ProfileActivity.this.fragmentView != null) {
                ProfileActivity.this.checkListViewScroll();
                ProfileActivity.this.needLayout();
                ProfileActivity.this.fragmentView.getViewTreeObserver().removeOnPreDrawListener(this);
            }
            return true;
        }
    }

    /* renamed from: org.telegram.ui.ProfileActivity$1 */
    class CLASSNAME extends EmptyPhotoViewerProvider {
        CLASSNAME() {
        }

        public PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, FileLocation fileLocation, int index) {
            PlaceProviderObject object = null;
            int i = 0;
            if (fileLocation != null) {
                FileLocation photoBig = null;
                if (ProfileActivity.this.user_id != 0) {
                    User user = MessagesController.getInstance(ProfileActivity.this.currentAccount).getUser(Integer.valueOf(ProfileActivity.this.user_id));
                    if (!(user == null || user.photo == null || user.photo.photo_big == null)) {
                        photoBig = user.photo.photo_big;
                    }
                } else if (ProfileActivity.this.chat_id != 0) {
                    Chat chat = MessagesController.getInstance(ProfileActivity.this.currentAccount).getChat(Integer.valueOf(ProfileActivity.this.chat_id));
                    if (!(chat == null || chat.photo == null || chat.photo.photo_big == null)) {
                        photoBig = chat.photo.photo_big;
                    }
                }
                if (photoBig != null && photoBig.local_id == fileLocation.local_id && photoBig.volume_id == fileLocation.volume_id && photoBig.dc_id == fileLocation.dc_id) {
                    int[] coords = new int[2];
                    ProfileActivity.this.avatarImage.getLocationInWindow(coords);
                    object = new PlaceProviderObject();
                    object.viewX = coords[0];
                    int i2 = coords[1];
                    if (VERSION.SDK_INT < 21) {
                        i = AndroidUtilities.statusBarHeight;
                    }
                    object.viewY = i2 - i;
                    object.parentView = ProfileActivity.this.avatarImage;
                    object.imageReceiver = ProfileActivity.this.avatarImage.getImageReceiver();
                    if (ProfileActivity.this.user_id != 0) {
                        object.dialogId = ProfileActivity.this.user_id;
                    } else if (ProfileActivity.this.chat_id != 0) {
                        object.dialogId = -ProfileActivity.this.chat_id;
                    }
                    object.thumb = object.imageReceiver.getBitmapSafe();
                    object.size = -1;
                    object.radius = ProfileActivity.this.avatarImage.getImageReceiver().getRoundRadius();
                    object.scale = ProfileActivity.this.avatarImage.getScaleX();
                }
            }
            return object;
        }

        public void willHidePhotoViewer() {
            ProfileActivity.this.avatarImage.getImageReceiver().setVisible(true, true);
        }
    }

    /* renamed from: org.telegram.ui.ProfileActivity$3 */
    class CLASSNAME extends ActionBarMenuOnItemClick {
        CLASSNAME() {
        }

        public void onItemClick(int id) {
            if (ProfileActivity.this.getParentActivity() != null) {
                Builder builder;
                User user;
                Bundle args;
                BaseFragment dialogsActivity;
                if (id == -1) {
                    ProfileActivity.this.lambda$checkDiscard$70$PassportActivity();
                } else if (id == 2) {
                    if (MessagesController.getInstance(ProfileActivity.this.currentAccount).getUser(Integer.valueOf(ProfileActivity.this.user_id)) == null) {
                        return;
                    }
                    if (!ProfileActivity.this.isBot) {
                        builder = new Builder(ProfileActivity.this.getParentActivity());
                        if (ProfileActivity.this.userBlocked) {
                            builder.setMessage(LocaleController.getString("AreYouSureUnblockContact", R.string.AreYouSureUnblockContact));
                        } else {
                            builder.setMessage(LocaleController.getString("AreYouSureBlockContact", R.string.AreYouSureBlockContact));
                        }
                        builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new ProfileActivity$3$$Lambda$0(this));
                        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                        ProfileActivity.this.showDialog(builder.create());
                    } else if (ProfileActivity.this.userBlocked) {
                        MessagesController.getInstance(ProfileActivity.this.currentAccount).unblockUser(ProfileActivity.this.user_id);
                        SendMessagesHelper.getInstance(ProfileActivity.this.currentAccount).sendMessage("/start", (long) ProfileActivity.this.user_id, null, null, false, null, null, null);
                        ProfileActivity.this.lambda$checkDiscard$70$PassportActivity();
                    } else {
                        MessagesController.getInstance(ProfileActivity.this.currentAccount).blockUser(ProfileActivity.this.user_id);
                    }
                } else if (id == 1) {
                    user = MessagesController.getInstance(ProfileActivity.this.currentAccount).getUser(Integer.valueOf(ProfileActivity.this.user_id));
                    args = new Bundle();
                    args.putInt("user_id", user.var_id);
                    args.putBoolean("addContact", true);
                    ProfileActivity.this.presentFragment(new ContactAddActivity(args));
                } else if (id == 3) {
                    args = new Bundle();
                    args.putBoolean("onlySelect", true);
                    args.putString("selectAlertString", LocaleController.getString("SendContactTo", R.string.SendContactTo));
                    args.putString("selectAlertStringGroup", LocaleController.getString("SendContactToGroup", R.string.SendContactToGroup));
                    dialogsActivity = new DialogsActivity(args);
                    dialogsActivity.setDelegate(ProfileActivity.this);
                    ProfileActivity.this.presentFragment(dialogsActivity);
                } else if (id == 4) {
                    args = new Bundle();
                    args.putInt("user_id", ProfileActivity.this.user_id);
                    ProfileActivity.this.presentFragment(new ContactAddActivity(args));
                } else if (id == 5) {
                    user = MessagesController.getInstance(ProfileActivity.this.currentAccount).getUser(Integer.valueOf(ProfileActivity.this.user_id));
                    if (user != null && ProfileActivity.this.getParentActivity() != null) {
                        builder = new Builder(ProfileActivity.this.getParentActivity());
                        builder.setMessage(LocaleController.getString("AreYouSureDeleteContact", R.string.AreYouSureDeleteContact));
                        builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new ProfileActivity$3$$Lambda$1(this, user));
                        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                        ProfileActivity.this.showDialog(builder.create());
                    }
                } else if (id == 7) {
                    ProfileActivity.this.leaveChatPressed();
                } else if (id == 8) {
                    args = new Bundle();
                    args.putInt("chat_id", ProfileActivity.this.chat_id);
                    ProfileActivity.this.presentFragment(new ChangeChatNameActivity(args));
                } else if (id == 12) {
                    args = new Bundle();
                    args.putInt("chat_id", ProfileActivity.this.chat_id);
                    dialogsActivity = new ChannelEditActivity(args);
                    dialogsActivity.setInfo(ProfileActivity.this.chatInfo);
                    ProfileActivity.this.presentFragment(dialogsActivity);
                } else if (id == 9) {
                    user = MessagesController.getInstance(ProfileActivity.this.currentAccount).getUser(Integer.valueOf(ProfileActivity.this.user_id));
                    if (user != null) {
                        args = new Bundle();
                        args.putBoolean("onlySelect", true);
                        args.putInt("dialogsType", 2);
                        args.putString("addToGroupAlertString", LocaleController.formatString("AddToTheGroupTitle", R.string.AddToTheGroupTitle, UserObject.getUserName(user), "%1$s"));
                        dialogsActivity = new DialogsActivity(args);
                        dialogsActivity.setDelegate(new ProfileActivity$3$$Lambda$2(this, user));
                        ProfileActivity.this.presentFragment(dialogsActivity);
                    }
                } else if (id == 10) {
                    try {
                        if (MessagesController.getInstance(ProfileActivity.this.currentAccount).getUser(Integer.valueOf(ProfileActivity.this.user_id)) != null) {
                            Intent intent = new Intent("android.intent.action.SEND");
                            intent.setType("text/plain");
                            if (ProfileActivity.this.botInfo == null || ProfileActivity.this.userInfo == null || TextUtils.isEmpty(ProfileActivity.this.userInfo.about)) {
                                intent = intent;
                                intent.putExtra("android.intent.extra.TEXT", String.format("https://" + MessagesController.getInstance(ProfileActivity.this.currentAccount).linkPrefix + "/%s", new Object[]{user.username}));
                            } else {
                                intent = intent;
                                intent.putExtra("android.intent.extra.TEXT", String.format("%s https://" + MessagesController.getInstance(ProfileActivity.this.currentAccount).linkPrefix + "/%s", new Object[]{ProfileActivity.this.userInfo.about, user.username}));
                            }
                            ProfileActivity.this.startActivityForResult(Intent.createChooser(intent, LocaleController.getString("BotShare", R.string.BotShare)), 500);
                        }
                    } catch (Throwable e) {
                        FileLog.m13e(e);
                    }
                } else if (id == 11) {
                    args = new Bundle();
                    args.putInt("chat_id", ProfileActivity.this.chat_id);
                    dialogsActivity = new SetAdminsActivity(args);
                    dialogsActivity.setChatInfo(ProfileActivity.this.chatInfo);
                    ProfileActivity.this.presentFragment(dialogsActivity);
                } else if (id == 13) {
                    args = new Bundle();
                    args.putInt("chat_id", ProfileActivity.this.chat_id);
                    ProfileActivity.this.presentFragment(new ConvertGroupActivity(args));
                } else if (id == 14) {
                    try {
                        long did;
                        if (ProfileActivity.this.currentEncryptedChat != null) {
                            did = ((long) ProfileActivity.this.currentEncryptedChat.var_id) << 32;
                        } else if (ProfileActivity.this.user_id != 0) {
                            did = (long) ProfileActivity.this.user_id;
                        } else if (ProfileActivity.this.chat_id != 0) {
                            did = (long) (-ProfileActivity.this.chat_id);
                        } else {
                            return;
                        }
                        DataQuery.getInstance(ProfileActivity.this.currentAccount).installShortcut(did);
                    } catch (Throwable e2) {
                        FileLog.m13e(e2);
                    }
                } else if (id == 15) {
                    user = MessagesController.getInstance(ProfileActivity.this.currentAccount).getUser(Integer.valueOf(ProfileActivity.this.user_id));
                    if (user != null) {
                        VoIPHelper.startCall(user, ProfileActivity.this.getParentActivity(), ProfileActivity.this.userInfo);
                    }
                } else if (id == 17) {
                    args = new Bundle();
                    args.putInt("chat_id", ProfileActivity.this.chat_id);
                    if (ChatObject.isChannel(ProfileActivity.this.currentChat)) {
                        args.putInt("type", 2);
                        args.putBoolean("open_search", true);
                        ProfileActivity.this.presentFragment(new ChannelUsersActivity(args));
                        return;
                    }
                    ChatUsersActivity chatUsersActivity = new ChatUsersActivity(args);
                    chatUsersActivity.setInfo(ProfileActivity.this.chatInfo);
                    ProfileActivity.this.presentFragment(chatUsersActivity);
                } else if (id == 18) {
                    ProfileActivity.this.openAddMember();
                }
            }
        }

        final /* synthetic */ void lambda$onItemClick$0$ProfileActivity$3(DialogInterface dialogInterface, int i) {
            if (ProfileActivity.this.userBlocked) {
                MessagesController.getInstance(ProfileActivity.this.currentAccount).unblockUser(ProfileActivity.this.user_id);
            } else {
                MessagesController.getInstance(ProfileActivity.this.currentAccount).blockUser(ProfileActivity.this.user_id);
            }
        }

        final /* synthetic */ void lambda$onItemClick$1$ProfileActivity$3(User user, DialogInterface dialogInterface, int i) {
            ArrayList<User> arrayList = new ArrayList();
            arrayList.add(user);
            ContactsController.getInstance(ProfileActivity.this.currentAccount).deleteContact(arrayList);
        }

        final /* synthetic */ void lambda$onItemClick$2$ProfileActivity$3(User user, DialogsActivity fragment1, ArrayList dids, CharSequence message, boolean param) {
            long did = ((Long) dids.get(0)).longValue();
            Bundle args1 = new Bundle();
            args1.putBoolean("scrollToTopOnResume", true);
            args1.putInt("chat_id", -((int) did));
            if (MessagesController.getInstance(ProfileActivity.this.currentAccount).checkCanOpenChat(args1, fragment1)) {
                NotificationCenter.getInstance(ProfileActivity.this.currentAccount).removeObserver(ProfileActivity.this, NotificationCenter.closeChats);
                NotificationCenter.getInstance(ProfileActivity.this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                MessagesController.getInstance(ProfileActivity.this.currentAccount).addUserToChat(-((int) did), user, null, 0, null, ProfileActivity.this);
                ProfileActivity.this.presentFragment(new ChatActivity(args1), true);
                ProfileActivity.this.lambda$null$10$ProfileActivity();
            }
        }
    }

    /* renamed from: org.telegram.ui.ProfileActivity$8 */
    class CLASSNAME extends ViewOutlineProvider {
        CLASSNAME() {
        }

        @SuppressLint({"NewApi"})
        public void getOutline(View view, Outline outline) {
            outline.setOval(0, 0, AndroidUtilities.m9dp(56.0f), AndroidUtilities.m9dp(56.0f));
        }
    }

    /* renamed from: org.telegram.ui.ProfileActivity$9 */
    class CLASSNAME extends OnScrollListener {
        CLASSNAME() {
        }

        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            ProfileActivity.this.checkListViewScroll();
            if (ProfileActivity.this.participantsMap != null && !ProfileActivity.this.usersEndReached && ProfileActivity.this.layoutManager.findLastVisibleItemPosition() > ProfileActivity.this.membersEndRow - 8) {
                ProfileActivity.this.getChannelParticipants(false);
            }
        }
    }

    /* renamed from: org.telegram.ui.ProfileActivity$ListAdapter */
    private class ListAdapter extends SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = null;
            switch (viewType) {
                case 1:
                    view = new HeaderCell(this.mContext, 23);
                    break;
                case 2:
                    view = new TextDetailCell(this.mContext);
                    break;
                case 3:
                    view = new AboutLinkCell(this.mContext) {
                        protected void didPressUrl(String url) {
                            if (url.startsWith("@")) {
                                MessagesController.getInstance(ProfileActivity.this.currentAccount).openByUserName(url.substring(1), ProfileActivity.this, 0);
                            } else if (url.startsWith("#")) {
                                DialogsActivity fragment = new DialogsActivity(null);
                                fragment.setSearchString(url);
                                ProfileActivity.this.presentFragment(fragment);
                            } else if (url.startsWith("/") && ProfileActivity.this.parentLayout.fragmentsStack.size() > 1) {
                                BaseFragment previousFragment = (BaseFragment) ProfileActivity.this.parentLayout.fragmentsStack.get(ProfileActivity.this.parentLayout.fragmentsStack.size() - 2);
                                if (previousFragment instanceof ChatActivity) {
                                    ProfileActivity.this.lambda$checkDiscard$70$PassportActivity();
                                    ((ChatActivity) previousFragment).chatActivityEnterView.setCommand(null, url, false, false);
                                }
                            }
                        }
                    };
                    break;
                case 4:
                    view = new TextCell(this.mContext);
                    break;
                case 5:
                    view = new DividerCell(this.mContext);
                    view.setPadding(AndroidUtilities.m9dp(20.0f), AndroidUtilities.m9dp(4.0f), 0, 0);
                    break;
                case 6:
                    view = new NotificationsCheckCell(this.mContext, 23);
                    break;
                case 7:
                    view = new ShadowSectionCell(this.mContext);
                    break;
                case 8:
                    view = new UserCell(this.mContext, 12, 0, true);
                    break;
                case 10:
                    view = new TextInfoPrivacyCell(this.mContext);
                    TextInfoPrivacyCell cell = (TextInfoPrivacyCell) view;
                    CombinedDrawable combinedDrawable = new CombinedDrawable(new ColorDrawable(Theme.getColor(Theme.key_windowBackgroundGray)), Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                    combinedDrawable.setFullsize(true);
                    cell.setBackgroundDrawable(combinedDrawable);
                    cell.setText(AndroidUtilities.replaceTags(LocaleController.formatString("ConvertGroupInfo", R.string.ConvertGroupInfo, LocaleController.formatPluralString("Members", MessagesController.getInstance(ProfileActivity.this.currentAccount).maxMegagroupCount))));
                    break;
                case 11:
                    view = new EmptyCell(this.mContext, 36);
                    break;
            }
            view.setLayoutParams(new LayoutParams(-1, -2));
            return new Holder(view);
        }

        public void onBindViewHolder(ViewHolder holder, int position) {
            String text;
            switch (holder.getItemViewType()) {
                case 1:
                    HeaderCell headerCell = (HeaderCell) holder.itemView;
                    if (position == ProfileActivity.this.infoHeaderRow) {
                        if (!ChatObject.isChannel(ProfileActivity.this.currentChat) || ProfileActivity.this.currentChat.megagroup || ProfileActivity.this.channelInfoRow == -1) {
                            headerCell.setText(LocaleController.getString("Info", R.string.Info));
                            return;
                        } else {
                            headerCell.setText(LocaleController.getString("ReportChatDescription", R.string.ReportChatDescription));
                            return;
                        }
                    } else if (position == ProfileActivity.this.sharedHeaderRow) {
                        headerCell.setText(LocaleController.getString("SharedContent", R.string.SharedContent));
                        return;
                    } else if (position == ProfileActivity.this.membersHeaderRow) {
                        headerCell.setText(LocaleController.getString("ChannelMembers", R.string.ChannelMembers));
                        return;
                    } else {
                        return;
                    }
                case 2:
                    TextDetailCell detailCell = holder.itemView;
                    User user;
                    if (position == ProfileActivity.this.phoneRow) {
                        user = MessagesController.getInstance(ProfileActivity.this.currentAccount).getUser(Integer.valueOf(ProfileActivity.this.user_id));
                        if (TextUtils.isEmpty(user.phone)) {
                            text = LocaleController.getString("PhoneHidden", R.string.PhoneHidden);
                        } else {
                            text = CLASSNAMEPhoneFormat.getInstance().format("+" + user.phone);
                        }
                        detailCell.setTextAndValue(text, LocaleController.getString("PhoneMobile", R.string.PhoneMobile), false);
                        return;
                    } else if (position != ProfileActivity.this.usernameRow) {
                        return;
                    } else {
                        if (ProfileActivity.this.user_id != 0) {
                            user = MessagesController.getInstance(ProfileActivity.this.currentAccount).getUser(Integer.valueOf(ProfileActivity.this.user_id));
                            if (user == null || TextUtils.isEmpty(user.username)) {
                                text = "-";
                            } else {
                                text = "@" + user.username;
                            }
                            detailCell.setTextAndValue(text, LocaleController.getString("Username", R.string.Username), false);
                            return;
                        } else if (ProfileActivity.this.currentChat != null) {
                            detailCell.setTextAndValue(MessagesController.getInstance(ProfileActivity.this.currentAccount).linkPrefix + "/" + MessagesController.getInstance(ProfileActivity.this.currentAccount).getChat(Integer.valueOf(ProfileActivity.this.chat_id)).username, LocaleController.getString("InviteLink", R.string.InviteLink), false);
                            return;
                        } else {
                            return;
                        }
                    }
                case 3:
                    AboutLinkCell aboutLinkCell = holder.itemView;
                    if (position == ProfileActivity.this.userInfoRow) {
                        aboutLinkCell.setTextAndValue(ProfileActivity.this.userInfo.about, LocaleController.getString("UserBio", R.string.UserBio), ProfileActivity.this.isBot);
                        return;
                    } else if (position == ProfileActivity.this.channelInfoRow) {
                        text = ProfileActivity.this.chatInfo.about;
                        while (text.contains("\n\n\n")) {
                            text = text.replace("\n\n\n", "\n\n");
                        }
                        aboutLinkCell.setText(text, true);
                        return;
                    } else {
                        return;
                    }
                case 4:
                    TextCell textCell = (TextCell) holder.itemView;
                    textCell.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
                    textCell.setTag(Theme.key_windowBackgroundWhiteBlackText);
                    if (position == ProfileActivity.this.photosRow) {
                        textCell.setTextAndValueAndIcon(LocaleController.getString("SharedPhotosAndVideos", R.string.SharedPhotosAndVideos), String.format("%d", new Object[]{Integer.valueOf(ProfileActivity.this.lastMediaCount[0])}), R.drawable.profile_photos, position != ProfileActivity.this.sharedSectionRow + -1);
                        return;
                    } else if (position == ProfileActivity.this.filesRow) {
                        textCell.setTextAndValueAndIcon(LocaleController.getString("FilesDataUsage", R.string.FilesDataUsage), String.format("%d", new Object[]{Integer.valueOf(ProfileActivity.this.lastMediaCount[1])}), R.drawable.profile_file, position != ProfileActivity.this.sharedSectionRow + -1);
                        return;
                    } else if (position == ProfileActivity.this.linksRow) {
                        textCell.setTextAndValueAndIcon(LocaleController.getString("SharedLinks", R.string.SharedLinks), String.format("%d", new Object[]{Integer.valueOf(ProfileActivity.this.lastMediaCount[3])}), R.drawable.profile_link, position != ProfileActivity.this.sharedSectionRow + -1);
                        return;
                    } else if (position == ProfileActivity.this.audioRow) {
                        textCell.setTextAndValueAndIcon(LocaleController.getString("SharedAudioFiles", R.string.SharedAudioFiles), String.format("%d", new Object[]{Integer.valueOf(ProfileActivity.this.lastMediaCount[4])}), R.drawable.profile_audio, position != ProfileActivity.this.sharedSectionRow + -1);
                        return;
                    } else if (position == ProfileActivity.this.voiceRow) {
                        textCell.setTextAndValueAndIcon(LocaleController.getString("AudioAutodownload", R.string.AudioAutodownload), String.format("%d", new Object[]{Integer.valueOf(ProfileActivity.this.lastMediaCount[2])}), R.drawable.profile_voice, position != ProfileActivity.this.sharedSectionRow + -1);
                        return;
                    } else if (position == ProfileActivity.this.groupsInCommonRow) {
                        textCell.setTextAndValueAndIcon(LocaleController.getString("GroupsInCommonTitle", R.string.GroupsInCommonTitle), String.format("%d", new Object[]{Integer.valueOf(ProfileActivity.this.userInfo.common_chats_count)}), R.drawable.profile_groups, position != ProfileActivity.this.sharedSectionRow + -1);
                        return;
                    } else if (position == ProfileActivity.this.settingsTimerRow) {
                        String value;
                        EncryptedChat encryptedChat = MessagesController.getInstance(ProfileActivity.this.currentAccount).getEncryptedChat(Integer.valueOf((int) (ProfileActivity.this.dialog_id >> 32)));
                        if (encryptedChat.ttl == 0) {
                            value = LocaleController.getString("ShortMessageLifetimeForever", R.string.ShortMessageLifetimeForever);
                        } else {
                            value = LocaleController.formatTTLString(encryptedChat.ttl);
                        }
                        textCell.setTextAndValue(LocaleController.getString("MessageLifetime", R.string.MessageLifetime), value, false);
                        return;
                    } else if (position == ProfileActivity.this.startSecretChatRow) {
                        textCell.setText(LocaleController.getString("StartEncryptedChat", R.string.StartEncryptedChat), false);
                        textCell.setTag(Theme.key_windowBackgroundWhiteGreenText2);
                        textCell.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGreenText2));
                        return;
                    } else if (position == ProfileActivity.this.settingsKeyRow) {
                        Drawable identiconDrawable = new IdenticonDrawable();
                        identiconDrawable.setEncryptedChat(MessagesController.getInstance(ProfileActivity.this.currentAccount).getEncryptedChat(Integer.valueOf((int) (ProfileActivity.this.dialog_id >> 32))));
                        textCell.setTextAndValueDrawable(LocaleController.getString("EncryptionKey", R.string.EncryptionKey), identiconDrawable, false);
                        return;
                    } else if (position == ProfileActivity.this.leaveChannelRow) {
                        textCell.setTag(Theme.key_windowBackgroundWhiteRedText5);
                        textCell.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText5));
                        textCell.setText(LocaleController.getString("LeaveChannel", R.string.LeaveChannel), false);
                        return;
                    } else if (position == ProfileActivity.this.joinRow) {
                        textCell.setTag(Theme.key_windowBackgroundWhiteBlueText2);
                        textCell.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText2));
                        if (ProfileActivity.this.currentChat.megagroup) {
                            textCell.setText(LocaleController.getString("ProfileJoinGroup", R.string.ProfileJoinGroup), false);
                            return;
                        } else {
                            textCell.setText(LocaleController.getString("ProfileJoinChannel", R.string.ProfileJoinChannel), false);
                            return;
                        }
                    } else if (position == ProfileActivity.this.subscribersRow) {
                        if (ProfileActivity.this.chatInfo != null) {
                            if (!ChatObject.isChannel(ProfileActivity.this.currentChat) || ProfileActivity.this.currentChat.megagroup) {
                                textCell.setTextAndValueAndIcon(LocaleController.getString("ChannelMembers", R.string.ChannelMembers), String.format("%d", new Object[]{Integer.valueOf(ProfileActivity.this.chatInfo.participants_count)}), R.drawable.menu_newgroup, position != ProfileActivity.this.membersSectionRow + -1);
                                return;
                            } else {
                                textCell.setTextAndValueAndIcon(LocaleController.getString("ChannelSubscribers", R.string.ChannelSubscribers), String.format("%d", new Object[]{Integer.valueOf(ProfileActivity.this.chatInfo.participants_count)}), R.drawable.menu_newgroup, position != ProfileActivity.this.membersSectionRow + -1);
                                return;
                            }
                        } else if (!ChatObject.isChannel(ProfileActivity.this.currentChat) || ProfileActivity.this.currentChat.megagroup) {
                            textCell.setTextAndIcon(LocaleController.getString("ChannelMembers", R.string.ChannelMembers), R.drawable.menu_newgroup, position != ProfileActivity.this.membersSectionRow + -1);
                            return;
                        } else {
                            textCell.setTextAndIcon(LocaleController.getString("ChannelSubscribers", R.string.ChannelSubscribers), R.drawable.menu_newgroup, position != ProfileActivity.this.membersSectionRow + -1);
                            return;
                        }
                    } else if (position == ProfileActivity.this.administratorsRow) {
                        if (ProfileActivity.this.chatInfo != null) {
                            textCell.setTextAndValueAndIcon(LocaleController.getString("ChannelAdministrators", R.string.ChannelAdministrators), String.format("%d", new Object[]{Integer.valueOf(ProfileActivity.this.chatInfo.admins_count)}), R.drawable.profile_admin, position != ProfileActivity.this.membersSectionRow + -1);
                            return;
                        } else {
                            textCell.setTextAndIcon(LocaleController.getString("ChannelAdministrators", R.string.ChannelAdministrators), R.drawable.profile_admin, position != ProfileActivity.this.membersSectionRow + -1);
                            return;
                        }
                    } else if (position == ProfileActivity.this.blockedUsersRow) {
                        if (ProfileActivity.this.chatInfo != null) {
                            textCell.setTextAndValueAndIcon(LocaleController.getString("ChannelBlacklist", R.string.ChannelBlacklist), String.format("%d", new Object[]{Integer.valueOf(Math.max(ProfileActivity.this.chatInfo.banned_count, ProfileActivity.this.chatInfo.kicked_count))}), R.drawable.profile_ban, position != ProfileActivity.this.membersSectionRow + -1);
                            return;
                        } else {
                            textCell.setTextAndIcon(LocaleController.getString("ChannelBlacklist", R.string.ChannelBlacklist), R.drawable.profile_ban, position != ProfileActivity.this.membersSectionRow + -1);
                            return;
                        }
                    } else if (position == ProfileActivity.this.convertRow) {
                        textCell.setText(LocaleController.getString("UpgradeGroup", R.string.UpgradeGroup), false);
                        textCell.setTag(Theme.key_windowBackgroundWhiteGreenText2);
                        textCell.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGreenText2));
                        return;
                    } else if (position != ProfileActivity.this.addMemberRow) {
                        return;
                    } else {
                        if (ProfileActivity.this.chat_id > 0) {
                            textCell.setText(LocaleController.getString("AddMember", R.string.AddMember), false);
                            return;
                        } else {
                            textCell.setText(LocaleController.getString("AddRecipient", R.string.AddRecipient), false);
                            return;
                        }
                    }
                case 6:
                    NotificationsCheckCell checkCell = holder.itemView;
                    if (position == ProfileActivity.this.notificationsRow) {
                        long did;
                        String val;
                        SharedPreferences preferences = MessagesController.getNotificationsSettings(ProfileActivity.this.currentAccount);
                        if (ProfileActivity.this.dialog_id != 0) {
                            did = ProfileActivity.this.dialog_id;
                        } else if (ProfileActivity.this.user_id != 0) {
                            did = (long) ProfileActivity.this.user_id;
                        } else {
                            did = (long) (-ProfileActivity.this.chat_id);
                        }
                        boolean enabled = false;
                        boolean custom = preferences.getBoolean("custom_" + did, false);
                        boolean hasOverride = preferences.contains("notify2_" + did);
                        int value2 = preferences.getInt("notify2_" + did, 0);
                        int delta = preferences.getInt("notifyuntil_" + did, 0);
                        if (value2 != 3 || delta == Integer.MAX_VALUE) {
                            if (value2 == 0) {
                                if (hasOverride) {
                                    enabled = true;
                                } else {
                                    enabled = NotificationsController.getInstance(ProfileActivity.this.currentAccount).isGlobalNotificationsEnabled(did);
                                }
                            } else if (value2 == 1) {
                                enabled = true;
                            } else if (value2 == 2) {
                                enabled = false;
                            } else {
                                enabled = false;
                            }
                            val = (enabled && custom) ? LocaleController.getString("NotificationsCustom", R.string.NotificationsCustom) : enabled ? LocaleController.getString("NotificationsOn", R.string.NotificationsOn) : LocaleController.getString("NotificationsOff", R.string.NotificationsOff);
                        } else {
                            delta -= ConnectionsManager.getInstance(ProfileActivity.this.currentAccount).getCurrentTime();
                            if (delta <= 0) {
                                if (custom) {
                                    val = LocaleController.getString("NotificationsCustom", R.string.NotificationsCustom);
                                } else {
                                    val = LocaleController.getString("NotificationsOn", R.string.NotificationsOn);
                                }
                                enabled = true;
                            } else {
                                val = delta < 3600 ? LocaleController.formatString("WillUnmuteIn", R.string.WillUnmuteIn, LocaleController.formatPluralString("Minutes", delta / 60)) : delta < 86400 ? LocaleController.formatString("WillUnmuteIn", R.string.WillUnmuteIn, LocaleController.formatPluralString("Hours", (int) Math.ceil((double) ((((float) delta) / 60.0f) / 60.0f)))) : delta < 31536000 ? LocaleController.formatString("WillUnmuteIn", R.string.WillUnmuteIn, LocaleController.formatPluralString("Days", (int) Math.ceil((double) (((((float) delta) / 60.0f) / 60.0f) / 24.0f)))) : null;
                            }
                        }
                        if (val == null) {
                            val = LocaleController.getString("NotificationsOff", R.string.NotificationsOff);
                        }
                        checkCell.setTextAndValueAndCheck(LocaleController.getString("Notifications", R.string.Notifications), val, enabled, false);
                        return;
                    }
                    return;
                case 7:
                    Drawable drawable;
                    View sectionCell = holder.itemView;
                    sectionCell.setTag(Integer.valueOf(position));
                    if ((position == ProfileActivity.this.infoSectionRow && ProfileActivity.this.sharedSectionRow == -1 && ProfileActivity.this.lastSectionRow == -1 && ProfileActivity.this.settingsSectionRow == -1) || ((position == ProfileActivity.this.sharedSectionRow && ProfileActivity.this.lastSectionRow == -1) || position == ProfileActivity.this.lastSectionRow || (position == ProfileActivity.this.membersSectionRow && ProfileActivity.this.lastSectionRow == -1 && ProfileActivity.this.sharedSectionRow == -1))) {
                        drawable = Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow);
                    } else {
                        drawable = Theme.getThemedDrawable(this.mContext, (int) R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow);
                    }
                    CombinedDrawable combinedDrawable = new CombinedDrawable(new ColorDrawable(Theme.getColor(Theme.key_windowBackgroundGray)), drawable);
                    combinedDrawable.setFullsize(true);
                    sectionCell.setBackgroundDrawable(combinedDrawable);
                    return;
                case 8:
                    ChatParticipant part;
                    UserCell userCell = (UserCell) holder.itemView;
                    if (ProfileActivity.this.sortedUsers.isEmpty()) {
                        part = (ChatParticipant) ProfileActivity.this.chatInfo.participants.participants.get(position - ProfileActivity.this.membersStartRow);
                    } else {
                        part = (ChatParticipant) ProfileActivity.this.chatInfo.participants.participants.get(((Integer) ProfileActivity.this.sortedUsers.get(position - ProfileActivity.this.membersStartRow)).intValue());
                    }
                    if (part != null) {
                        if (part instanceof TL_chatChannelParticipant) {
                            ChannelParticipant channelParticipant = ((TL_chatChannelParticipant) part).channelParticipant;
                            if (channelParticipant instanceof TL_channelParticipantCreator) {
                                userCell.setIsAdmin(1);
                            } else if (channelParticipant instanceof TL_channelParticipantAdmin) {
                                userCell.setIsAdmin(2);
                            } else {
                                userCell.setIsAdmin(0);
                            }
                        } else if (part instanceof TL_chatParticipantCreator) {
                            userCell.setIsAdmin(1);
                        } else if (ProfileActivity.this.currentChat.admins_enabled && (part instanceof TL_chatParticipantAdmin)) {
                            userCell.setIsAdmin(2);
                        } else {
                            userCell.setIsAdmin(0);
                        }
                        userCell.setData(MessagesController.getInstance(ProfileActivity.this.currentAccount).getUser(Integer.valueOf(part.user_id)), null, null, 0);
                        return;
                    }
                    return;
                default:
                    return;
            }
        }

        public boolean isEnabled(ViewHolder holder) {
            int type = holder.getItemViewType();
            if (type == 1 || type == 5 || type == 7 || type == 9 || type == 10 || type == 11) {
                return false;
            }
            return true;
        }

        public int getItemCount() {
            return ProfileActivity.this.rowCount;
        }

        public int getItemViewType(int i) {
            if (i == ProfileActivity.this.infoHeaderRow || i == ProfileActivity.this.sharedHeaderRow || i == ProfileActivity.this.membersHeaderRow) {
                return 1;
            }
            if (i == ProfileActivity.this.phoneRow || i == ProfileActivity.this.usernameRow) {
                return 2;
            }
            if (i == ProfileActivity.this.userInfoRow || i == ProfileActivity.this.channelInfoRow) {
                return 3;
            }
            if (i == ProfileActivity.this.settingsTimerRow || i == ProfileActivity.this.settingsKeyRow || i == ProfileActivity.this.photosRow || i == ProfileActivity.this.filesRow || i == ProfileActivity.this.linksRow || i == ProfileActivity.this.audioRow || i == ProfileActivity.this.voiceRow || i == ProfileActivity.this.groupsInCommonRow || i == ProfileActivity.this.startSecretChatRow || i == ProfileActivity.this.subscribersRow || i == ProfileActivity.this.administratorsRow || i == ProfileActivity.this.blockedUsersRow || i == ProfileActivity.this.leaveChannelRow || i == ProfileActivity.this.addMemberRow || i == ProfileActivity.this.convertRow || i == ProfileActivity.this.joinRow) {
                return 4;
            }
            if (i == ProfileActivity.this.notificationsDividerRow) {
                return 5;
            }
            if (i == ProfileActivity.this.notificationsRow) {
                return 6;
            }
            if (i == ProfileActivity.this.infoSectionRow || i == ProfileActivity.this.sharedSectionRow || i == ProfileActivity.this.lastSectionRow || i == ProfileActivity.this.membersSectionRow || i == ProfileActivity.this.settingsSectionRow) {
                return 7;
            }
            if (i >= ProfileActivity.this.membersStartRow && i < ProfileActivity.this.membersEndRow) {
                return 8;
            }
            if (i == ProfileActivity.this.convertHelpRow) {
                return 10;
            }
            if (i == ProfileActivity.this.emptyRow) {
                return 11;
            }
            return 0;
        }
    }

    /* renamed from: org.telegram.ui.ProfileActivity$TopView */
    private class TopView extends View {
        private int currentColor;
        private Paint paint = new Paint();

        public TopView(Context context) {
            super(context);
        }

        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), ((ProfileActivity.this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0) + CLASSNAMEActionBar.getCurrentActionBarHeight()) + AndroidUtilities.m9dp(91.0f));
        }

        public void setBackgroundColor(int color) {
            if (color != this.currentColor) {
                this.paint.setColor(color);
                invalidate();
            }
        }

        protected void onDraw(Canvas canvas) {
            int height = getMeasuredHeight() - AndroidUtilities.m9dp(91.0f);
            canvas.drawRect(0.0f, 0.0f, (float) getMeasuredWidth(), (float) (ProfileActivity.this.extraHeight + height), this.paint);
            if (ProfileActivity.this.parentLayout != null) {
                ProfileActivity.this.parentLayout.drawHeaderShadow(canvas, ProfileActivity.this.extraHeight + height);
            }
        }
    }

    public ProfileActivity(Bundle args) {
        super(args);
    }

    public boolean onFragmentCreate() {
        this.user_id = this.arguments.getInt("user_id", 0);
        this.chat_id = this.arguments.getInt("chat_id", 0);
        this.banFromGroup = this.arguments.getInt("ban_chat_id", 0);
        if (this.user_id != 0) {
            this.dialog_id = this.arguments.getLong("dialog_id", 0);
            if (this.dialog_id != 0) {
                this.currentEncryptedChat = MessagesController.getInstance(this.currentAccount).getEncryptedChat(Integer.valueOf((int) (this.dialog_id >> 32)));
            }
            User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.user_id));
            if (user == null) {
                return false;
            }
            boolean z;
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.updateInterfaces);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.contactsDidLoad);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.encryptedChatCreated);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.encryptedChatUpdated);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.blockedUsersDidLoad);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.botInfoDidLoad);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.userInfoDidLoad);
            if (MessagesController.getInstance(this.currentAccount).blockedUsers.indexOfKey(this.user_id) >= 0) {
                z = true;
            } else {
                z = false;
            }
            this.userBlocked = z;
            if (user.bot) {
                this.isBot = true;
                DataQuery.getInstance(this.currentAccount).loadBotInfo(user.var_id, true, this.classGuid);
            }
            MessagesController.getInstance(this.currentAccount).loadFullUser(MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.user_id)), this.classGuid, true);
            this.participantsMap = null;
        } else if (this.chat_id == 0) {
            return false;
        } else {
            this.currentChat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.chat_id));
            if (this.currentChat == null) {
                CountDownLatch countDownLatch = new CountDownLatch(1);
                MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new ProfileActivity$$Lambda$0(this, countDownLatch));
                try {
                    countDownLatch.await();
                } catch (Throwable e) {
                    FileLog.m13e(e);
                }
                if (this.currentChat == null) {
                    return false;
                }
                MessagesController.getInstance(this.currentAccount).putChat(this.currentChat, true);
            }
            if (this.currentChat.megagroup) {
                getChannelParticipants(true);
            } else {
                this.participantsMap = null;
            }
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.chatInfoDidLoad);
            this.sortedUsers = new ArrayList();
            updateOnlineCount();
            if (ChatObject.isChannel(this.currentChat)) {
                MessagesController.getInstance(this.currentAccount).loadFullChat(this.chat_id, this.classGuid, true);
            }
        }
        this.sharedMediaData = new SharedMediaData[5];
        for (int a = 0; a < this.sharedMediaData.length; a++) {
            this.sharedMediaData[a] = new SharedMediaData();
            this.sharedMediaData[a].setMaxId(0, this.dialog_id != 0 ? Integer.MIN_VALUE : ConnectionsManager.DEFAULT_DATACENTER_ID);
        }
        loadMediaCounts();
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.mediaCountDidLoad);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.mediaCountsDidLoad);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.mediaDidLoad);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.updateInterfaces);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.didReceiveNewMessages);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagesDeleted);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.closeChats);
        updateRowsIds();
        return true;
    }

    final /* synthetic */ void lambda$onFragmentCreate$0$ProfileActivity(CountDownLatch countDownLatch) {
        this.currentChat = MessagesStorage.getInstance(this.currentAccount).getChat(this.chat_id);
        countDownLatch.countDown();
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.mediaCountDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.mediaCountsDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.mediaDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.updateInterfaces);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.closeChats);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.didReceiveNewMessages);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagesDeleted);
        if (this.user_id != 0) {
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.contactsDidLoad);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.encryptedChatCreated);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.encryptedChatUpdated);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.blockedUsersDidLoad);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.botInfoDidLoad);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.userInfoDidLoad);
            MessagesController.getInstance(this.currentAccount).cancelLoadFullUser(this.user_id);
        } else if (this.chat_id != 0) {
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.chatInfoDidLoad);
        }
    }

    protected CLASSNAMEActionBar createActionBar(Context context) {
        boolean z;
        CLASSNAMEActionBar actionBar = new CLASSNAMEActionBar(context) {
            public boolean onTouchEvent(MotionEvent event) {
                return super.onTouchEvent(event);
            }
        };
        int i = (this.user_id != 0 || (ChatObject.isChannel(this.chat_id, this.currentAccount) && !this.currentChat.megagroup)) ? 5 : this.chat_id;
        actionBar.setItemsBackgroundColor(AvatarDrawable.getButtonColorForId(i), false);
        actionBar.setItemsColor(Theme.getColor(Theme.key_actionBarDefaultIcon), false);
        actionBar.setItemsColor(Theme.getColor(Theme.key_actionBarActionModeDefaultIcon), true);
        actionBar.setBackButtonDrawable(new BackDrawable(false));
        actionBar.setCastShadows(false);
        actionBar.setAddToContainer(false);
        if (VERSION.SDK_INT < 21 || AndroidUtilities.isTablet()) {
            z = false;
        } else {
            z = true;
        }
        actionBar.setOccupyStatusBar(z);
        return actionBar;
    }

    public View createView(Context context) {
        int i;
        Theme.createProfileResources(context);
        this.hasOwnBackground = true;
        this.extraHeight = AndroidUtilities.m9dp(88.0f);
        this.actionBar.setActionBarMenuOnItemClick(new CLASSNAME());
        createActionBarMenu();
        this.listAdapter = new ListAdapter(context);
        this.avatarDrawable = new AvatarDrawable();
        this.avatarDrawable.setProfile(true);
        this.fragmentView = new FrameLayout(context) {
            public boolean hasOverlappingRendering() {
                return false;
            }

            protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
                super.onLayout(changed, left, top, right, bottom);
                ProfileActivity.this.checkListViewScroll();
            }
        };
        FrameLayout frameLayout = this.fragmentView;
        this.listView = new RecyclerListView(context) {
            private Paint paint = new Paint();

            public boolean hasOverlappingRendering() {
                return false;
            }

            public void onDraw(Canvas c) {
                ViewHolder holder;
                int bottom;
                if (ProfileActivity.this.lastSectionRow != -1) {
                    holder = findViewHolderForAdapterPosition(ProfileActivity.this.lastSectionRow);
                } else if (ProfileActivity.this.sharedSectionRow != -1 && (ProfileActivity.this.membersSectionRow == -1 || ProfileActivity.this.membersSectionRow < ProfileActivity.this.sharedSectionRow)) {
                    holder = findViewHolderForAdapterPosition(ProfileActivity.this.sharedSectionRow);
                } else if (ProfileActivity.this.membersSectionRow != -1 && (ProfileActivity.this.sharedSectionRow == -1 || ProfileActivity.this.membersSectionRow > ProfileActivity.this.sharedSectionRow)) {
                    holder = findViewHolderForAdapterPosition(ProfileActivity.this.membersSectionRow);
                } else if (ProfileActivity.this.infoSectionRow != -1) {
                    holder = findViewHolderForAdapterPosition(ProfileActivity.this.infoSectionRow);
                } else {
                    holder = null;
                }
                int height = getMeasuredHeight();
                if (holder != null) {
                    bottom = holder.itemView.getBottom();
                    if (holder.itemView.getBottom() >= height) {
                        bottom = height;
                    }
                } else {
                    bottom = height;
                }
                this.paint.setColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                c.drawRect(0.0f, 0.0f, (float) getMeasuredWidth(), (float) bottom, this.paint);
                if (bottom != height) {
                    this.paint.setColor(Theme.getColor(Theme.key_windowBackgroundGray));
                    c.drawRect(0.0f, (float) bottom, (float) getMeasuredWidth(), (float) height, this.paint);
                }
            }
        };
        this.listView.setTag(Integer.valueOf(6));
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setItemAnimator(null);
        this.listView.setLayoutAnimation(null);
        this.listView.setClipToPadding(false);
        this.layoutManager = new LinearLayoutManager(context) {
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        };
        this.layoutManager.setOrientation(1);
        this.listView.setLayoutManager(this.layoutManager);
        RecyclerListView recyclerListView = this.listView;
        if (this.user_id != 0 || (ChatObject.isChannel(this.chat_id, this.currentAccount) && !this.currentChat.megagroup)) {
            i = 5;
        } else {
            i = this.chat_id;
        }
        recyclerListView.setGlowColor(AvatarDrawable.getProfileBackColorForId(i));
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
        this.listView.setAdapter(this.listAdapter);
        this.listView.setOnItemClickListener(new ProfileActivity$$Lambda$1(this));
        this.listView.setOnItemLongClickListener(new ProfileActivity$$Lambda$2(this));
        if (this.banFromGroup != 0) {
            if (this.currentChannelParticipant == null) {
                TL_channels_getParticipant req = new TL_channels_getParticipant();
                req.channel = MessagesController.getInstance(this.currentAccount).getInputChannel(this.banFromGroup);
                req.user_id = MessagesController.getInstance(this.currentAccount).getInputUser(this.user_id);
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new ProfileActivity$$Lambda$3(this));
            }
            FrameLayout frameLayout1 = new FrameLayout(context) {
                protected void onDraw(Canvas canvas) {
                    int bottom = Theme.chat_composeShadowDrawable.getIntrinsicHeight();
                    Theme.chat_composeShadowDrawable.setBounds(0, 0, getMeasuredWidth(), bottom);
                    Theme.chat_composeShadowDrawable.draw(canvas);
                    canvas.drawRect(0.0f, (float) bottom, (float) getMeasuredWidth(), (float) getMeasuredHeight(), Theme.chat_composeBackgroundPaint);
                }
            };
            frameLayout1.setWillNotDraw(false);
            frameLayout.addView(frameLayout1, LayoutHelper.createFrame(-1, 51, 83));
            frameLayout1.setOnClickListener(new ProfileActivity$$Lambda$4(this));
            View textView = new TextView(context);
            textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText));
            textView.setTextSize(1, 15.0f);
            textView.setGravity(17);
            textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            textView.setText(LocaleController.getString("BanFromTheGroup", R.string.BanFromTheGroup));
            frameLayout1.addView(textView, LayoutHelper.createFrame(-2, -2.0f, 17, 0.0f, 1.0f, 0.0f, 0.0f));
            this.listView.setPadding(0, AndroidUtilities.m9dp(88.0f), 0, AndroidUtilities.m9dp(48.0f));
            this.listView.setBottomGlowOffset(AndroidUtilities.m9dp(48.0f));
        } else {
            this.listView.setPadding(0, AndroidUtilities.m9dp(88.0f), 0, 0);
        }
        this.topView = new TopView(context);
        TopView topView = this.topView;
        if (this.user_id != 0 || (ChatObject.isChannel(this.chat_id, this.currentAccount) && !this.currentChat.megagroup)) {
            i = 5;
        } else {
            i = this.chat_id;
        }
        topView.setBackgroundColor(AvatarDrawable.getProfileBackColorForId(i));
        frameLayout.addView(this.topView);
        frameLayout.addView(this.actionBar);
        this.avatarImage = new BackupImageView(context);
        this.avatarImage.setRoundRadius(AndroidUtilities.m9dp(21.0f));
        this.avatarImage.setPivotX(0.0f);
        this.avatarImage.setPivotY(0.0f);
        frameLayout.addView(this.avatarImage, LayoutHelper.createFrame(42, 42.0f, 51, 64.0f, 0.0f, 0.0f, 0.0f));
        this.avatarImage.setOnClickListener(new ProfileActivity$$Lambda$5(this));
        int a = 0;
        while (a < 2) {
            if (this.playProfileAnimation || a != 0) {
                float f;
                this.nameTextView[a] = new SimpleTextView(context);
                if (a == 1) {
                    this.nameTextView[a].setTextColor(Theme.getColor(Theme.key_profile_title));
                } else {
                    this.nameTextView[a].setTextColor(Theme.getColor(Theme.key_actionBarDefaultTitle));
                }
                this.nameTextView[a].setTextSize(18);
                this.nameTextView[a].setGravity(3);
                this.nameTextView[a].setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                this.nameTextView[a].setLeftDrawableTopPadding(-AndroidUtilities.m9dp(1.3f));
                this.nameTextView[a].setPivotX(0.0f);
                this.nameTextView[a].setPivotY(0.0f);
                this.nameTextView[a].setAlpha(a == 0 ? 0.0f : 1.0f);
                frameLayout.addView(this.nameTextView[a], LayoutHelper.createFrame(-2, -2.0f, 51, 118.0f, 0.0f, a == 0 ? 48.0f : 0.0f, 0.0f));
                this.onlineTextView[a] = new SimpleTextView(context);
                SimpleTextView simpleTextView = this.onlineTextView[a];
                i = (this.user_id != 0 || (ChatObject.isChannel(this.chat_id, this.currentAccount) && !this.currentChat.megagroup)) ? 5 : this.chat_id;
                simpleTextView.setTextColor(AvatarDrawable.getProfileTextColorForId(i));
                this.onlineTextView[a].setTextSize(14);
                this.onlineTextView[a].setGravity(3);
                this.onlineTextView[a].setAlpha(a == 0 ? 0.0f : 1.0f);
                View view = this.onlineTextView[a];
                if (a == 0) {
                    f = 48.0f;
                } else {
                    f = 8.0f;
                }
                frameLayout.addView(view, LayoutHelper.createFrame(-2, -2.0f, 51, 118.0f, 0.0f, f, 0.0f));
            }
            a++;
        }
        if (this.user_id != 0) {
            this.writeButton = new ImageView(context);
            Drawable drawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.m9dp(56.0f), Theme.getColor(Theme.key_profile_actionBackground), Theme.getColor(Theme.key_profile_actionPressedBackground));
            if (VERSION.SDK_INT < 21) {
                Drawable shadowDrawable = context.getResources().getDrawable(R.drawable.floating_shadow_profile).mutate();
                shadowDrawable.setColorFilter(new PorterDuffColorFilter(Theme.ACTION_BAR_VIDEO_EDIT_COLOR, Mode.MULTIPLY));
                Drawable combinedDrawable = new CombinedDrawable(shadowDrawable, drawable, 0, 0);
                combinedDrawable.setIconSize(AndroidUtilities.m9dp(56.0f), AndroidUtilities.m9dp(56.0f));
                drawable = combinedDrawable;
            }
            this.writeButton.setBackgroundDrawable(drawable);
            this.writeButton.setImageResource(R.drawable.profile_newmsg);
            this.writeButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_profile_actionIcon), Mode.MULTIPLY));
            this.writeButton.setScaleType(ScaleType.CENTER);
            if (VERSION.SDK_INT >= 21) {
                StateListAnimator animator = new StateListAnimator();
                animator.addState(new int[]{16842919}, ObjectAnimator.ofFloat(this.writeButton, "translationZ", new float[]{(float) AndroidUtilities.m9dp(2.0f), (float) AndroidUtilities.m9dp(4.0f)}).setDuration(200));
                animator.addState(new int[0], ObjectAnimator.ofFloat(this.writeButton, "translationZ", new float[]{(float) AndroidUtilities.m9dp(4.0f), (float) AndroidUtilities.m9dp(2.0f)}).setDuration(200));
                this.writeButton.setStateListAnimator(animator);
                this.writeButton.setOutlineProvider(new CLASSNAME());
            }
            frameLayout.addView(this.writeButton, LayoutHelper.createFrame(VERSION.SDK_INT >= 21 ? 56 : 60, VERSION.SDK_INT >= 21 ? 56.0f : 60.0f, 53, 0.0f, 0.0f, 16.0f, 0.0f));
            this.writeButton.setOnClickListener(new ProfileActivity$$Lambda$6(this));
        }
        needLayout();
        this.listView.setOnScrollListener(new CLASSNAME());
        return this.fragmentView;
    }

    final /* synthetic */ void lambda$createView$4$ProfileActivity(View view, int position, float x, float y) {
        if (getParentActivity() != null) {
            Bundle args;
            Builder builder;
            if (position == this.photosRow || position == this.filesRow || position == this.linksRow || position == this.audioRow || position == this.voiceRow) {
                int tab;
                if (position == this.photosRow) {
                    tab = 0;
                } else if (position == this.filesRow) {
                    tab = 1;
                } else if (position == this.linksRow) {
                    tab = 3;
                } else if (position == this.audioRow) {
                    tab = 4;
                } else {
                    tab = 2;
                }
                args = new Bundle();
                if (this.user_id != 0) {
                    args.putLong("dialog_id", this.dialog_id != 0 ? this.dialog_id : (long) this.user_id);
                } else {
                    args.putLong("dialog_id", (long) (-this.chat_id));
                }
                Object media = new int[5];
                System.arraycopy(this.lastMediaCount, 0, media, 0, media.length);
                BaseFragment mediaActivity = new MediaActivity(args, media, this.sharedMediaData, tab);
                mediaActivity.setChatInfo(this.chatInfo);
                presentFragment(mediaActivity);
            } else if (position == this.groupsInCommonRow) {
                presentFragment(new CommonGroupsActivity(this.user_id));
            } else if (position == this.settingsKeyRow) {
                args = new Bundle();
                args.putInt("chat_id", (int) (this.dialog_id >> 32));
                presentFragment(new IdenticonActivity(args));
            } else if (position == this.settingsTimerRow) {
                showDialog(AlertsCreator.createTTLAlert(getParentActivity(), this.currentEncryptedChat).create());
            } else if (position == this.notificationsRow) {
                long did;
                if (this.dialog_id != 0) {
                    did = this.dialog_id;
                } else if (this.user_id != 0) {
                    did = (long) this.user_id;
                } else {
                    did = (long) (-this.chat_id);
                }
                if ((!LocaleController.isRTL || x > ((float) AndroidUtilities.m9dp(76.0f))) && (LocaleController.isRTL || x < ((float) (view.getMeasuredWidth() - AndroidUtilities.m9dp(76.0f))))) {
                    AlertsCreator.showCustomNotificationsDialog(this, did, -1, null, this.currentAccount, new ProfileActivity$$Lambda$22(this));
                    return;
                }
                NotificationsCheckCell checkCell = (NotificationsCheckCell) view;
                boolean checked = !checkCell.isChecked();
                boolean defaultEnabled = NotificationsController.getInstance(this.currentAccount).isGlobalNotificationsEnabled(did);
                Editor editor;
                TL_dialog dialog;
                if (checked) {
                    editor = MessagesController.getNotificationsSettings(this.currentAccount).edit();
                    if (defaultEnabled) {
                        editor.remove("notify2_" + did);
                    } else {
                        editor.putInt("notify2_" + did, 0);
                    }
                    MessagesStorage.getInstance(this.currentAccount).setDialogFlags(did, 0);
                    editor.commit();
                    dialog = (TL_dialog) MessagesController.getInstance(this.currentAccount).dialogs_dict.get(did);
                    if (dialog != null) {
                        dialog.notify_settings = new TL_peerNotifySettings();
                    }
                    NotificationsController.getInstance(this.currentAccount).updateServerNotificationsSettings(did);
                } else {
                    long flags;
                    editor = MessagesController.getNotificationsSettings(this.currentAccount).edit();
                    if (defaultEnabled) {
                        editor.putInt("notify2_" + did, 2);
                        flags = 1;
                    } else {
                        editor.remove("notify2_" + did);
                        flags = 0;
                    }
                    NotificationsController.getInstance(this.currentAccount).removeNotificationsForDialog(did);
                    MessagesStorage.getInstance(this.currentAccount).setDialogFlags(did, flags);
                    editor.commit();
                    dialog = (TL_dialog) MessagesController.getInstance(this.currentAccount).dialogs_dict.get(did);
                    if (dialog != null) {
                        dialog.notify_settings = new TL_peerNotifySettings();
                        if (defaultEnabled) {
                            dialog.notify_settings.mute_until = ConnectionsManager.DEFAULT_DATACENTER_ID;
                        }
                    }
                    NotificationsController.getInstance(this.currentAccount).updateServerNotificationsSettings(did);
                }
                checkCell.setChecked(checked);
                ViewHolder holder = (Holder) this.listView.findViewHolderForPosition(this.notificationsRow);
                if (holder != null) {
                    this.listAdapter.onBindViewHolder(holder, this.notificationsRow);
                }
            } else if (position == this.startSecretChatRow) {
                builder = new Builder(getParentActivity());
                builder.setMessage(LocaleController.getString("AreYouSureSecretChat", R.string.AreYouSureSecretChat));
                builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new ProfileActivity$$Lambda$23(this));
                builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                showDialog(builder.create());
            } else if (position >= this.membersStartRow && position < this.membersEndRow) {
                int user_id;
                if (this.sortedUsers.isEmpty()) {
                    user_id = ((ChatParticipant) this.chatInfo.participants.participants.get(position - this.membersStartRow)).user_id;
                } else {
                    user_id = ((ChatParticipant) this.chatInfo.participants.participants.get(((Integer) this.sortedUsers.get(position - this.membersStartRow)).intValue())).user_id;
                }
                if (user_id != UserConfig.getInstance(this.currentAccount).getClientUserId()) {
                    args = new Bundle();
                    args.putInt("user_id", user_id);
                    presentFragment(new ProfileActivity(args));
                }
            } else if (position == this.addMemberRow) {
                openAddMember();
            } else if (position == this.usernameRow) {
                if (this.currentChat != null) {
                    try {
                        Intent intent = new Intent("android.intent.action.SEND");
                        intent.setType("text/plain");
                        if (TextUtils.isEmpty(this.chatInfo.about)) {
                            intent.putExtra("android.intent.extra.TEXT", this.currentChat.title + "\nhttps://" + MessagesController.getInstance(this.currentAccount).linkPrefix + "/" + this.currentChat.username);
                        } else {
                            intent.putExtra("android.intent.extra.TEXT", this.currentChat.title + "\n" + this.chatInfo.about + "\nhttps://" + MessagesController.getInstance(this.currentAccount).linkPrefix + "/" + this.currentChat.username);
                        }
                        getParentActivity().startActivityForResult(Intent.createChooser(intent, LocaleController.getString("BotShare", R.string.BotShare)), 500);
                    } catch (Throwable e) {
                        FileLog.m13e(e);
                    }
                }
            } else if (position == this.leaveChannelRow) {
                leaveChatPressed();
            } else if (position == this.joinRow) {
                MessagesController.getInstance(this.currentAccount).addUserToChat(this.currentChat.var_id, UserConfig.getInstance(this.currentAccount).getCurrentUser(), null, 0, null, this);
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.closeSearchByActiveAction, new Object[0]);
            } else if (position == this.subscribersRow) {
                args = new Bundle();
                args.putInt("chat_id", this.chat_id);
                args.putInt("type", 2);
                presentFragment(new ChannelUsersActivity(args));
            } else if (position == this.administratorsRow) {
                args = new Bundle();
                args.putInt("chat_id", this.chat_id);
                args.putInt("type", 1);
                presentFragment(new ChannelUsersActivity(args));
            } else if (position == this.blockedUsersRow) {
                args = new Bundle();
                args.putInt("chat_id", this.chat_id);
                args.putInt("type", 0);
                presentFragment(new ChannelUsersActivity(args));
            } else if (position == this.convertRow) {
                builder = new Builder(getParentActivity());
                builder.setMessage(LocaleController.getString("ConvertGroupAlert", R.string.ConvertGroupAlert));
                builder.setTitle(LocaleController.getString("ConvertGroupAlertWarning", R.string.ConvertGroupAlertWarning));
                builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new ProfileActivity$$Lambda$24(this));
                builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                showDialog(builder.create());
            } else {
                processOnClickOrPress(position);
            }
        }
    }

    final /* synthetic */ void lambda$null$1$ProfileActivity(int param) {
        this.listAdapter.notifyItemChanged(this.notificationsRow);
    }

    final /* synthetic */ void lambda$null$2$ProfileActivity(DialogInterface dialogInterface, int i) {
        this.creatingChat = true;
        SecretChatHelper.getInstance(this.currentAccount).startSecretChat(getParentActivity(), MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.user_id)));
    }

    final /* synthetic */ void lambda$null$3$ProfileActivity(DialogInterface dialogInterface, int i) {
        MessagesController.getInstance(this.currentAccount).convertToMegaGroup(getParentActivity(), this.chat_id);
    }

    final /* synthetic */ boolean lambda$createView$7$ProfileActivity(View view, int position) {
        if (position < this.membersStartRow || position >= this.membersEndRow) {
            return processOnClickOrPress(position);
        }
        if (getParentActivity() == null) {
            return false;
        }
        ChatParticipant user;
        ChannelParticipant channelParticipant;
        boolean allowKick = false;
        boolean allowSetAdmin = false;
        boolean canEditAdmin = false;
        if (this.sortedUsers.isEmpty()) {
            user = (ChatParticipant) this.chatInfo.participants.participants.get(position - this.membersStartRow);
        } else {
            user = (ChatParticipant) this.chatInfo.participants.participants.get(((Integer) this.sortedUsers.get(position - this.membersStartRow)).intValue());
        }
        this.selectedUser = user.user_id;
        if (ChatObject.isChannel(this.currentChat)) {
            channelParticipant = ((TL_chatChannelParticipant) user).channelParticipant;
            if (user.user_id == UserConfig.getInstance(this.currentAccount).getClientUserId()) {
                return false;
            }
            User u = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(user.user_id));
            allowSetAdmin = (channelParticipant instanceof TL_channelParticipant) || (channelParticipant instanceof TL_channelParticipantBanned);
            if (((channelParticipant instanceof TL_channelParticipantAdmin) || (channelParticipant instanceof TL_channelParticipantCreator)) && !channelParticipant.can_edit) {
                canEditAdmin = false;
            } else {
                canEditAdmin = true;
            }
        } else {
            channelParticipant = null;
            if (user.user_id != UserConfig.getInstance(this.currentAccount).getClientUserId()) {
                if (this.currentChat.creator) {
                    allowKick = true;
                } else if ((user instanceof TL_chatParticipant) && ((this.currentChat.admin && this.currentChat.admins_enabled) || user.inviter_id == UserConfig.getInstance(this.currentAccount).getClientUserId())) {
                    allowKick = true;
                }
            }
            if (!allowKick) {
                return false;
            }
        }
        Builder builder = new Builder(getParentActivity());
        ArrayList<String> items = new ArrayList();
        ArrayList<Integer> actions = new ArrayList();
        if (this.currentChat.megagroup) {
            if (allowSetAdmin && ChatObject.canAddAdmins(this.currentChat)) {
                items.add(LocaleController.getString("SetAsAdmin", R.string.SetAsAdmin));
                actions.add(Integer.valueOf(0));
            }
            if (ChatObject.canBlockUsers(this.currentChat) && canEditAdmin) {
                items.add(LocaleController.getString("KickFromSupergroup", R.string.KickFromSupergroup));
                actions.add(Integer.valueOf(1));
                items.add(LocaleController.getString("KickFromGroup", R.string.KickFromGroup));
                actions.add(Integer.valueOf(2));
            }
        } else {
            items.add(this.chat_id > 0 ? LocaleController.getString("KickFromGroup", R.string.KickFromGroup) : LocaleController.getString("KickFromBroadcast", R.string.KickFromBroadcast));
            actions.add(Integer.valueOf(2));
        }
        if (items.isEmpty()) {
            return false;
        }
        builder.setItems((CharSequence[]) items.toArray(new CharSequence[items.size()]), new ProfileActivity$$Lambda$20(this, actions, user, channelParticipant));
        showDialog(builder.create());
        return true;
    }

    final /* synthetic */ void lambda$null$6$ProfileActivity(ArrayList actions, ChatParticipant user, ChannelParticipant channelParticipant, DialogInterface dialogInterface, int i) {
        if (((Integer) actions.get(i)).intValue() == 2) {
            kickUser(this.selectedUser);
            return;
        }
        ChannelRightsEditActivity fragment = new ChannelRightsEditActivity(user.user_id, this.chat_id, channelParticipant.admin_rights, channelParticipant.banned_rights, ((Integer) actions.get(i)).intValue(), true);
        fragment.setDelegate(new ProfileActivity$$Lambda$21(this, actions, i, user));
        presentFragment(fragment);
    }

    /* JADX WARNING: Removed duplicated region for block: B:32:0x00b1  */
    /* JADX WARNING: Removed duplicated region for block: B:49:? A:{SYNTHETIC, RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:36:0x00cf  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    final /* synthetic */ void lambda$null$5$ProfileActivity(ArrayList actions, int i, ChatParticipant user, int rights, TL_channelAdminRights rightsAdmin, TL_channelBannedRights rightsBanned) {
        if (((Integer) actions.get(i)).intValue() == 0) {
            TL_chatChannelParticipant channelParticipant1 = (TL_chatChannelParticipant) user;
            if (rights == 1) {
                channelParticipant1.channelParticipant = new TL_channelParticipantAdmin();
            } else {
                channelParticipant1.channelParticipant = new TL_channelParticipant();
            }
            channelParticipant1.channelParticipant.inviter_id = UserConfig.getInstance(this.currentAccount).getClientUserId();
            channelParticipant1.channelParticipant.user_id = user.user_id;
            channelParticipant1.channelParticipant.date = user.date;
            channelParticipant1.channelParticipant.banned_rights = rightsBanned;
            channelParticipant1.channelParticipant.admin_rights = rightsAdmin;
        } else if (((Integer) actions.get(i)).intValue() == 1 && rights == 0 && this.currentChat.megagroup && this.chatInfo != null && this.chatInfo.participants != null) {
            int a;
            boolean changed = false;
            for (a = 0; a < this.chatInfo.participants.participants.size(); a++) {
                if (((TL_chatChannelParticipant) this.chatInfo.participants.participants.get(a)).channelParticipant.user_id == user.user_id) {
                    if (this.chatInfo != null) {
                        ChatFull chatFull = this.chatInfo;
                        chatFull.participants_count--;
                    }
                    this.chatInfo.participants.participants.remove(a);
                    changed = true;
                    if (this.chatInfo != null && this.chatInfo.participants != null) {
                        for (a = 0; a < this.chatInfo.participants.participants.size(); a++) {
                            if (((ChatParticipant) this.chatInfo.participants.participants.get(a)).user_id == user.user_id) {
                                this.chatInfo.participants.participants.remove(a);
                                changed = true;
                                break;
                            }
                        }
                    }
                    if (!changed) {
                        updateOnlineCount();
                        updateRowsIds();
                        this.listAdapter.notifyDataSetChanged();
                        return;
                    }
                    return;
                }
            }
            while (a < this.chatInfo.participants.participants.size()) {
            }
            if (!changed) {
            }
        }
    }

    final /* synthetic */ void lambda$createView$9$ProfileActivity(TLObject response, TL_error error) {
        if (response != null) {
            AndroidUtilities.runOnUIThread(new ProfileActivity$$Lambda$19(this, response));
        }
    }

    final /* synthetic */ void lambda$null$8$ProfileActivity(TLObject response) {
        this.currentChannelParticipant = ((TL_channels_channelParticipant) response).participant;
    }

    final /* synthetic */ void lambda$createView$11$ProfileActivity(View v) {
        TL_channelBannedRights tL_channelBannedRights;
        int i = this.user_id;
        int i2 = this.banFromGroup;
        if (this.currentChannelParticipant != null) {
            tL_channelBannedRights = this.currentChannelParticipant.banned_rights;
        } else {
            tL_channelBannedRights = null;
        }
        ChannelRightsEditActivity fragment = new ChannelRightsEditActivity(i, i2, null, tL_channelBannedRights, 1, true);
        fragment.setDelegate(new ProfileActivity$$Lambda$18(this));
        presentFragment(fragment);
    }

    final /* synthetic */ void lambda$createView$12$ProfileActivity(View v) {
        if (this.user_id != 0) {
            User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.user_id));
            if (user.photo != null && user.photo.photo_big != null) {
                PhotoViewer.getInstance().setParentActivity(getParentActivity());
                PhotoViewer.getInstance().openPhoto(user.photo.photo_big, this.provider);
            }
        } else if (this.chat_id != 0) {
            Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.chat_id));
            if (chat.photo != null && chat.photo.photo_big != null) {
                PhotoViewer.getInstance().setParentActivity(getParentActivity());
                PhotoViewer.getInstance().openPhoto(chat.photo.photo_big, this.provider);
            }
        }
    }

    final /* synthetic */ void lambda$createView$13$ProfileActivity(View v) {
        if (this.playProfileAnimation && (this.parentLayout.fragmentsStack.get(this.parentLayout.fragmentsStack.size() - 2) instanceof ChatActivity)) {
            lambda$checkDiscard$70$PassportActivity();
            return;
        }
        User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.user_id));
        if (user != null && !(user instanceof TL_userEmpty)) {
            Bundle args = new Bundle();
            args.putInt("user_id", this.user_id);
            if (MessagesController.getInstance(this.currentAccount).checkCanOpenChat(args, this)) {
                NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.closeChats);
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                presentFragment(new ChatActivity(args), true);
            }
        }
    }

    private boolean processOnClickOrPress(int position) {
        User user;
        Builder builder;
        if (position == this.usernameRow) {
            String username;
            if (this.user_id != 0) {
                user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.user_id));
                if (user == null || user.username == null) {
                    return false;
                }
                username = user.username;
            } else if (this.chat_id == 0) {
                return false;
            } else {
                Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.chat_id));
                if (chat == null || chat.username == null) {
                    return false;
                }
                username = chat.username;
            }
            builder = new Builder(getParentActivity());
            builder.setItems(new CharSequence[]{LocaleController.getString("Copy", R.string.Copy)}, new ProfileActivity$$Lambda$7(this, username));
            showDialog(builder.create());
            return true;
        } else if (position == this.phoneRow) {
            user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.user_id));
            if (user == null || user.phone == null || user.phone.length() == 0 || getParentActivity() == null) {
                return false;
            }
            builder = new Builder(getParentActivity());
            ArrayList<CharSequence> items = new ArrayList();
            ArrayList<Integer> actions = new ArrayList();
            if (this.userInfo != null && this.userInfo.phone_calls_available) {
                items.add(LocaleController.getString("CallViaTelegram", R.string.CallViaTelegram));
                actions.add(Integer.valueOf(2));
            }
            items.add(LocaleController.getString("Call", R.string.Call));
            actions.add(Integer.valueOf(0));
            items.add(LocaleController.getString("Copy", R.string.Copy));
            actions.add(Integer.valueOf(1));
            builder.setItems((CharSequence[]) items.toArray(new CharSequence[items.size()]), new ProfileActivity$$Lambda$8(this, actions, user));
            showDialog(builder.create());
            return true;
        } else if (position != this.channelInfoRow && position != this.userInfoRow) {
            return false;
        } else {
            builder = new Builder(getParentActivity());
            builder.setItems(new CharSequence[]{LocaleController.getString("Copy", R.string.Copy)}, new ProfileActivity$$Lambda$9(this, position));
            showDialog(builder.create());
            return true;
        }
    }

    final /* synthetic */ void lambda$processOnClickOrPress$14$ProfileActivity(String username, DialogInterface dialogInterface, int i) {
        if (i == 0) {
            try {
                ((ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", "@" + username));
                Toast.makeText(getParentActivity(), LocaleController.getString("TextCopied", R.string.TextCopied), 0).show();
            } catch (Throwable e) {
                FileLog.m13e(e);
            }
        }
    }

    final /* synthetic */ void lambda$processOnClickOrPress$15$ProfileActivity(ArrayList actions, User user, DialogInterface dialogInterface, int i) {
        i = ((Integer) actions.get(i)).intValue();
        if (i == 0) {
            try {
                Intent intent = new Intent("android.intent.action.DIAL", Uri.parse("tel:+" + user.phone));
                intent.addFlags(CLASSNAMEC.ENCODING_PCM_MU_LAW);
                getParentActivity().startActivityForResult(intent, 500);
            } catch (Throwable e) {
                FileLog.m13e(e);
            }
        } else if (i == 1) {
            try {
                ((ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", "+" + user.phone));
                Toast.makeText(getParentActivity(), LocaleController.getString("PhoneCopied", R.string.PhoneCopied), 0).show();
            } catch (Throwable e2) {
                FileLog.m13e(e2);
            }
        } else if (i == 2) {
            VoIPHelper.startCall(user, getParentActivity(), this.userInfo);
        }
    }

    final /* synthetic */ void lambda$processOnClickOrPress$16$ProfileActivity(int position, DialogInterface dialogInterface, int i) {
        String about = null;
        try {
            if (position == this.channelInfoRow) {
                if (this.chatInfo != null) {
                    about = this.chatInfo.about;
                }
            } else if (this.userInfo != null) {
                about = this.userInfo.about;
            }
            if (!TextUtils.isEmpty(about)) {
                AndroidUtilities.addToClipboard(about);
                Toast.makeText(getParentActivity(), LocaleController.getString("TextCopied", R.string.TextCopied), 0).show();
            }
        } catch (Throwable e) {
            FileLog.m13e(e);
        }
    }

    private void leaveChatPressed() {
        Builder builder = new Builder(getParentActivity());
        if (!ChatObject.isChannel(this.chat_id, this.currentAccount) || this.currentChat.megagroup) {
            builder.setMessage(LocaleController.getString("AreYouSureDeleteAndExit", R.string.AreYouSureDeleteAndExit));
        } else {
            builder.setMessage(ChatObject.isChannel(this.chat_id, this.currentAccount) ? LocaleController.getString("ChannelLeaveAlert", R.string.ChannelLeaveAlert) : LocaleController.getString("AreYouSureDeleteAndExit", R.string.AreYouSureDeleteAndExit));
        }
        builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new ProfileActivity$$Lambda$10(this));
        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
        showDialog(builder.create());
    }

    final /* synthetic */ void lambda$leaveChatPressed$17$ProfileActivity(DialogInterface dialogInterface, int i) {
        kickUser(0);
    }

    private void getChannelParticipants(boolean reload) {
        int i = 0;
        if (!this.loadingUsers && this.participantsMap != null && this.chatInfo != null) {
            int delay;
            this.loadingUsers = true;
            if (this.participantsMap.size() == 0 || !reload) {
                delay = 0;
            } else {
                delay = 300;
            }
            TL_channels_getParticipants req = new TL_channels_getParticipants();
            req.channel = MessagesController.getInstance(this.currentAccount).getInputChannel(this.chat_id);
            req.filter = new TL_channelParticipantsRecent();
            if (!reload) {
                i = this.participantsMap.size();
            }
            req.offset = i;
            req.limit = Callback.DEFAULT_DRAG_ANIMATION_DURATION;
            ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new ProfileActivity$$Lambda$11(this, req, delay)), this.classGuid);
        }
    }

    final /* synthetic */ void lambda$getChannelParticipants$19$ProfileActivity(TL_channels_getParticipants req, int delay, TLObject response, TL_error error) {
        AndroidUtilities.runOnUIThread(new ProfileActivity$$Lambda$17(this, error, response, req), (long) delay);
    }

    final /* synthetic */ void lambda$null$18$ProfileActivity(TL_error error, TLObject response, TL_channels_getParticipants req) {
        if (error == null) {
            TL_channels_channelParticipants res = (TL_channels_channelParticipants) response;
            MessagesController.getInstance(this.currentAccount).putUsers(res.users, false);
            if (res.users.size() < Callback.DEFAULT_DRAG_ANIMATION_DURATION) {
                this.usersEndReached = true;
            }
            if (req.offset == 0) {
                this.participantsMap.clear();
                this.chatInfo.participants = new TL_chatParticipants();
                MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(res.users, null, true, true);
                MessagesStorage.getInstance(this.currentAccount).updateChannelUsers(this.chat_id, res.participants);
            }
            for (int a = 0; a < res.participants.size(); a++) {
                TL_chatChannelParticipant participant = new TL_chatChannelParticipant();
                participant.channelParticipant = (ChannelParticipant) res.participants.get(a);
                participant.inviter_id = participant.channelParticipant.inviter_id;
                participant.user_id = participant.channelParticipant.user_id;
                participant.date = participant.channelParticipant.date;
                if (this.participantsMap.indexOfKey(participant.user_id) < 0) {
                    this.chatInfo.participants.participants.add(participant);
                    this.participantsMap.put(participant.user_id, participant);
                }
            }
        }
        updateOnlineCount();
        this.loadingUsers = false;
        updateRowsIds();
        if (this.listAdapter != null) {
            this.listAdapter.notifyDataSetChanged();
        }
    }

    private void openAddMember() {
        boolean z = true;
        Bundle args = new Bundle();
        args.putBoolean("onlyUsers", true);
        args.putBoolean("destroyAfterSelect", true);
        args.putBoolean("returnAsResult", true);
        String str = "needForwardCount";
        if (ChatObject.isChannel(this.currentChat)) {
            z = false;
        }
        args.putBoolean(str, z);
        if (this.chat_id > 0) {
            if (ChatObject.canAddViaLink(this.currentChat)) {
                args.putInt("chat_id", this.currentChat.var_id);
            }
            args.putString("selectAlertString", LocaleController.getString("AddToTheGroup", R.string.AddToTheGroup));
        }
        ContactsActivity fragment = new ContactsActivity(args);
        fragment.setDelegate(new ProfileActivity$$Lambda$12(this));
        if (!(this.chatInfo == null || this.chatInfo.participants == null)) {
            SparseArray<User> users = new SparseArray();
            for (int a = 0; a < this.chatInfo.participants.participants.size(); a++) {
                users.put(((ChatParticipant) this.chatInfo.participants.participants.get(a)).user_id, null);
            }
            fragment.setIgnoreUsers(users);
        }
        presentFragment(fragment);
    }

    final /* synthetic */ void lambda$openAddMember$20$ProfileActivity(User user, String param, ContactsActivity activity) {
        MessagesController.getInstance(this.currentAccount).addUserToChat(this.chat_id, user, this.chatInfo, param != null ? Utilities.parseInt(param).intValue() : 0, null, this);
    }

    private void checkListViewScroll() {
        boolean z = false;
        if (this.listView.getChildCount() > 0 && !this.openAnimationInProgress) {
            View child = this.listView.getChildAt(0);
            Holder holder = (Holder) this.listView.findContainingViewHolder(child);
            int top = child.getTop();
            int newOffset = 0;
            if (top >= 0 && holder != null && holder.getAdapterPosition() == 0) {
                newOffset = top;
            }
            if (this.extraHeight != newOffset) {
                this.extraHeight = newOffset;
                this.topView.invalidate();
                if (this.playProfileAnimation) {
                    if (this.extraHeight != 0) {
                        z = true;
                    }
                    this.allowProfileAnimation = z;
                }
                needLayout();
            }
        }
    }

    private void needLayout() {
        FrameLayout.LayoutParams layoutParams;
        int newTop = (this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0) + CLASSNAMEActionBar.getCurrentActionBarHeight();
        if (!(this.listView == null || this.openAnimationInProgress)) {
            layoutParams = (FrameLayout.LayoutParams) this.listView.getLayoutParams();
            if (layoutParams.topMargin != newTop) {
                layoutParams.topMargin = newTop;
                this.listView.setLayoutParams(layoutParams);
            }
        }
        if (this.avatarImage != null) {
            float diff = ((float) this.extraHeight) / ((float) AndroidUtilities.m9dp(88.0f));
            this.listView.setTopGlowOffset(this.extraHeight);
            if (this.writeButton != null) {
                this.writeButton.setTranslationY((float) ((((this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0) + CLASSNAMEActionBar.getCurrentActionBarHeight()) + this.extraHeight) - AndroidUtilities.m9dp(29.5f)));
                if (!this.openAnimationInProgress) {
                    boolean setVisible = diff > 0.2f;
                    if (setVisible != (this.writeButton.getTag() == null)) {
                        if (setVisible) {
                            this.writeButton.setTag(null);
                        } else {
                            this.writeButton.setTag(Integer.valueOf(0));
                        }
                        if (this.writeButtonAnimation != null) {
                            AnimatorSet old = this.writeButtonAnimation;
                            this.writeButtonAnimation = null;
                            old.cancel();
                        }
                        this.writeButtonAnimation = new AnimatorSet();
                        AnimatorSet animatorSet;
                        Animator[] animatorArr;
                        if (setVisible) {
                            this.writeButtonAnimation.setInterpolator(new DecelerateInterpolator());
                            animatorSet = this.writeButtonAnimation;
                            animatorArr = new Animator[3];
                            animatorArr[0] = ObjectAnimator.ofFloat(this.writeButton, "scaleX", new float[]{1.0f});
                            animatorArr[1] = ObjectAnimator.ofFloat(this.writeButton, "scaleY", new float[]{1.0f});
                            animatorArr[2] = ObjectAnimator.ofFloat(this.writeButton, "alpha", new float[]{1.0f});
                            animatorSet.playTogether(animatorArr);
                        } else {
                            this.writeButtonAnimation.setInterpolator(new AccelerateInterpolator());
                            animatorSet = this.writeButtonAnimation;
                            animatorArr = new Animator[3];
                            animatorArr[0] = ObjectAnimator.ofFloat(this.writeButton, "scaleX", new float[]{0.2f});
                            animatorArr[1] = ObjectAnimator.ofFloat(this.writeButton, "scaleY", new float[]{0.2f});
                            animatorArr[2] = ObjectAnimator.ofFloat(this.writeButton, "alpha", new float[]{0.0f});
                            animatorSet.playTogether(animatorArr);
                        }
                        this.writeButtonAnimation.setDuration(150);
                        this.writeButtonAnimation.addListener(new CLASSNAME());
                        this.writeButtonAnimation.start();
                    }
                }
            }
            float avatarY = ((((float) (this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0)) + ((((float) CLASSNAMEActionBar.getCurrentActionBarHeight()) / 2.0f) * (1.0f + diff))) - (21.0f * AndroidUtilities.density)) + ((27.0f * AndroidUtilities.density) * diff);
            this.avatarImage.setScaleX((42.0f + (18.0f * diff)) / 42.0f);
            this.avatarImage.setScaleY((42.0f + (18.0f * diff)) / 42.0f);
            this.avatarImage.setTranslationX(((float) (-AndroidUtilities.m9dp(47.0f))) * diff);
            this.avatarImage.setTranslationY((float) Math.ceil((double) avatarY));
            for (int a = 0; a < 2; a++) {
                if (this.nameTextView[a] != null) {
                    this.nameTextView[a].setTranslationX((-21.0f * AndroidUtilities.density) * diff);
                    this.nameTextView[a].setTranslationY((((float) Math.floor((double) avatarY)) + ((float) AndroidUtilities.m9dp(1.3f))) + (((float) AndroidUtilities.m9dp(7.0f)) * diff));
                    this.onlineTextView[a].setTranslationX((-21.0f * AndroidUtilities.density) * diff);
                    this.onlineTextView[a].setTranslationY((((float) Math.floor((double) avatarY)) + ((float) AndroidUtilities.m9dp(24.0f))) + (((float) Math.floor((double) (11.0f * AndroidUtilities.density))) * diff));
                    float scale = 1.0f + (0.12f * diff);
                    this.nameTextView[a].setScaleX(scale);
                    this.nameTextView[a].setScaleY(scale);
                    if (a == 1 && !this.openAnimationInProgress) {
                        int viewWidth;
                        if (AndroidUtilities.isTablet()) {
                            viewWidth = AndroidUtilities.m9dp(490.0f);
                        } else {
                            viewWidth = AndroidUtilities.displaySize.x;
                        }
                        int i = (this.addItem != null ? 48 : 0) + 40;
                        int i2 = (this.callItem == null && this.editItem == null) ? 0 : 48;
                        int buttonsWidth = AndroidUtilities.m9dp((float) ((i2 + i) + 126));
                        int minWidth = viewWidth - buttonsWidth;
                        int width = (int) ((((float) viewWidth) - (Math.max(0.0f, 1.0f - (diff != 1.0f ? (0.15f * diff) / (1.0f - diff) : 1.0f)) * ((float) buttonsWidth))) - this.nameTextView[a].getTranslationX());
                        float width2 = (this.nameTextView[a].getPaint().measureText(this.nameTextView[a].getText().toString()) * scale) + ((float) this.nameTextView[a].getSideDrawablesSize());
                        layoutParams = (FrameLayout.LayoutParams) this.nameTextView[a].getLayoutParams();
                        if (((float) width) < width2) {
                            layoutParams.width = Math.max(minWidth, (int) Math.ceil((double) (((float) (width - AndroidUtilities.m9dp(24.0f))) / (((1.12f - scale) * 7.0f) + scale))));
                        } else {
                            layoutParams.width = (int) Math.ceil((double) width2);
                        }
                        layoutParams.width = (int) Math.min(((((float) viewWidth) - this.nameTextView[a].getX()) / scale) - ((float) AndroidUtilities.m9dp(8.0f)), (float) layoutParams.width);
                        this.nameTextView[a].setLayoutParams(layoutParams);
                        width2 = this.onlineTextView[a].getPaint().measureText(this.onlineTextView[a].getText().toString());
                        layoutParams = (FrameLayout.LayoutParams) this.onlineTextView[a].getLayoutParams();
                        layoutParams.rightMargin = (int) Math.ceil((double) ((this.onlineTextView[a].getTranslationX() + ((float) AndroidUtilities.m9dp(8.0f))) + (((float) AndroidUtilities.m9dp(40.0f)) * (1.0f - diff))));
                        if (((float) width) < width2) {
                            layoutParams.width = (int) Math.ceil((double) width);
                        } else {
                            layoutParams.width = -2;
                        }
                        this.onlineTextView[a].setLayoutParams(layoutParams);
                    }
                }
            }
        }
    }

    private void loadMediaCounts() {
        if (this.dialog_id != 0) {
            DataQuery.getInstance(this.currentAccount).getMediaCounts(this.dialog_id, this.classGuid);
        } else if (this.user_id != 0) {
            DataQuery.getInstance(this.currentAccount).getMediaCounts((long) this.user_id, this.classGuid);
        } else if (this.chat_id > 0) {
            DataQuery.getInstance(this.currentAccount).getMediaCounts((long) (-this.chat_id), this.classGuid);
            if (this.mergeDialogId != 0) {
                DataQuery.getInstance(this.currentAccount).getMediaCounts(this.mergeDialogId, this.classGuid);
            }
        }
    }

    private void fixLayout() {
        if (this.fragmentView != null) {
            this.fragmentView.getViewTreeObserver().addOnPreDrawListener(new CLASSNAME());
        }
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        fixLayout();
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        Chat newChat;
        int a;
        long uid;
        long did;
        int type;
        ArrayList<MessageObject> arr;
        boolean enc;
        if (id == NotificationCenter.updateInterfaces) {
            int mask = ((Integer) args[0]).intValue();
            if (this.user_id != 0) {
                if (!((mask & 2) == 0 && (mask & 1) == 0 && (mask & 4) == 0)) {
                    updateProfileData();
                }
                if ((mask & 1024) != 0 && this.listView != null) {
                    ViewHolder holder = (Holder) this.listView.findViewHolderForPosition(this.phoneRow);
                    if (holder != null) {
                        this.listAdapter.onBindViewHolder(holder, this.phoneRow);
                    }
                }
            } else if (this.chat_id != 0) {
                if ((mask & MessagesController.UPDATE_MASK_CHAT_ADMINS) != 0) {
                    newChat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.chat_id));
                    if (newChat != null) {
                        this.currentChat = newChat;
                        createActionBarMenu();
                        updateRowsIds();
                        if (this.listAdapter != null) {
                            this.listAdapter.notifyDataSetChanged();
                        }
                    }
                }
                if (!((mask & MessagesController.UPDATE_MASK_CHANNEL) == 0 && (mask & 8) == 0 && (mask & 16) == 0 && (mask & 32) == 0 && (mask & 4) == 0)) {
                    updateOnlineCount();
                    updateProfileData();
                }
                if ((mask & MessagesController.UPDATE_MASK_CHANNEL) != 0) {
                    updateRowsIds();
                    if (this.listAdapter != null) {
                        this.listAdapter.notifyDataSetChanged();
                    }
                }
                if (((mask & 2) != 0 || (mask & 1) != 0 || (mask & 4) != 0) && this.listView != null) {
                    int count = this.listView.getChildCount();
                    for (a = 0; a < count; a++) {
                        View child = this.listView.getChildAt(a);
                        if (child instanceof UserCell) {
                            ((UserCell) child).update(mask);
                        }
                    }
                }
            }
        } else if (id == NotificationCenter.contactsDidLoad) {
            createActionBarMenu();
        } else if (id == NotificationCenter.mediaDidLoad) {
            uid = ((Long) args[0]).longValue();
            if (((Integer) args[3]).intValue() == this.classGuid) {
                did = this.dialog_id;
                if (did == 0) {
                    if (this.user_id != 0) {
                        did = (long) this.user_id;
                    } else if (this.chat_id != 0) {
                        did = (long) (-this.chat_id);
                    }
                }
                type = ((Integer) args[4]).intValue();
                this.sharedMediaData[type].setTotalCount(((Integer) args[1]).intValue());
                arr = args[2];
                enc = ((int) did) == 0;
                int loadIndex = uid == did ? 0 : 1;
                this.sharedMediaData[type].setEndReached(loadIndex, ((Boolean) args[5]).booleanValue());
                for (a = 0; a < arr.size(); a++) {
                    this.sharedMediaData[type].addMessage((MessageObject) arr.get(a), loadIndex, false, enc);
                }
            }
        } else if (id == NotificationCenter.mediaCountsDidLoad) {
            uid = ((Long) args[0]).longValue();
            did = this.dialog_id;
            if (did == 0) {
                if (this.user_id != 0) {
                    did = (long) this.user_id;
                } else if (this.chat_id != 0) {
                    did = (long) (-this.chat_id);
                }
            }
            if (uid == did || uid == this.mergeDialogId) {
                int[] counts = (int[]) args[1];
                if (uid == did) {
                    this.mediaCount = counts;
                } else {
                    this.mediaMergeCount = counts;
                }
                System.arraycopy(this.lastMediaCount, 0, this.prevMediaCount, 0, this.prevMediaCount.length);
                a = 0;
                while (a < this.lastMediaCount.length) {
                    if (this.mediaCount[a] >= 0 && this.mediaMergeCount[a] >= 0) {
                        this.lastMediaCount[a] = this.mediaCount[a] + this.mediaMergeCount[a];
                    } else if (this.mediaCount[a] >= 0) {
                        this.lastMediaCount[a] = this.mediaCount[a];
                    } else if (this.mediaMergeCount[a] >= 0) {
                        this.lastMediaCount[a] = this.mediaMergeCount[a];
                    } else {
                        this.lastMediaCount[a] = 0;
                    }
                    if (uid == did && this.lastMediaCount[a] != 0) {
                        DataQuery.getInstance(this.currentAccount).loadMedia(did, 50, 0, a, 2, this.classGuid);
                    }
                    a++;
                }
                updateSharedMediaRows();
            }
        } else if (id == NotificationCenter.mediaCountDidLoad) {
            uid = ((Long) args[0]).longValue();
            did = this.dialog_id;
            if (did == 0) {
                if (this.user_id != 0) {
                    did = (long) this.user_id;
                } else if (this.chat_id != 0) {
                    did = (long) (-this.chat_id);
                }
            }
            if (uid == did || uid == this.mergeDialogId) {
                type = ((Integer) args[3]).intValue();
                int mCount = ((Integer) args[1]).intValue();
                if (uid == did) {
                    this.mediaCount[type] = mCount;
                } else {
                    this.mediaMergeCount[type] = mCount;
                }
                this.prevMediaCount[type] = this.lastMediaCount[type];
                if (this.mediaCount[type] >= 0 && this.mediaMergeCount[type] >= 0) {
                    this.lastMediaCount[type] = this.mediaCount[type] + this.mediaMergeCount[type];
                } else if (this.mediaCount[type] >= 0) {
                    this.lastMediaCount[type] = this.mediaCount[type];
                } else if (this.mediaMergeCount[type] >= 0) {
                    this.lastMediaCount[type] = this.mediaMergeCount[type];
                } else {
                    this.lastMediaCount[type] = 0;
                }
                updateSharedMediaRows();
            }
        } else if (id == NotificationCenter.encryptedChatCreated) {
            if (this.creatingChat) {
                AndroidUtilities.runOnUIThread(new ProfileActivity$$Lambda$13(this, args));
            }
        } else if (id == NotificationCenter.encryptedChatUpdated) {
            EncryptedChat chat = args[0];
            if (this.currentEncryptedChat != null && chat.var_id == this.currentEncryptedChat.var_id) {
                this.currentEncryptedChat = chat;
                updateRowsIds();
                if (this.listAdapter != null) {
                    this.listAdapter.notifyDataSetChanged();
                }
            }
        } else if (id == NotificationCenter.blockedUsersDidLoad) {
            boolean oldValue = this.userBlocked;
            this.userBlocked = MessagesController.getInstance(this.currentAccount).blockedUsers.indexOfKey(this.user_id) >= 0;
            if (oldValue != this.userBlocked) {
                createActionBarMenu();
            }
        } else if (id == NotificationCenter.chatInfoDidLoad) {
            ChatFull chatFull = args[0];
            if (chatFull.var_id == this.chat_id) {
                boolean byChannelUsers = ((Boolean) args[2]).booleanValue();
                if ((this.chatInfo instanceof TL_channelFull) && chatFull.participants == null && this.chatInfo != null) {
                    chatFull.participants = this.chatInfo.participants;
                }
                boolean loadChannelParticipants = this.chatInfo == null && (chatFull instanceof TL_channelFull);
                this.chatInfo = chatFull;
                if (this.mergeDialogId == 0 && this.chatInfo.migrated_from_chat_id != 0) {
                    this.mergeDialogId = (long) (-this.chatInfo.migrated_from_chat_id);
                    DataQuery.getInstance(this.currentAccount).getMediaCount(this.mergeDialogId, 0, this.classGuid, true);
                }
                fetchUsersFromChannelInfo();
                updateOnlineCount();
                updateRowsIds();
                if (this.listAdapter != null) {
                    this.listAdapter.notifyDataSetChanged();
                }
                newChat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.chat_id));
                if (newChat != null) {
                    this.currentChat = newChat;
                    createActionBarMenu();
                }
                if (!this.currentChat.megagroup) {
                    return;
                }
                if (loadChannelParticipants || !byChannelUsers) {
                    getChannelParticipants(true);
                }
            }
        } else if (id == NotificationCenter.closeChats) {
            lambda$null$10$ProfileActivity();
        } else if (id == NotificationCenter.botInfoDidLoad) {
            BotInfo info = args[0];
            if (info.user_id == this.user_id) {
                this.botInfo = info;
                updateRowsIds();
                if (this.listAdapter != null) {
                    this.listAdapter.notifyDataSetChanged();
                }
            }
        } else if (id == NotificationCenter.userInfoDidLoad) {
            if (((Integer) args[0]).intValue() == this.user_id) {
                this.userInfo = (TL_userFull) args[1];
                if (this.openAnimationInProgress || this.callItem != null) {
                    this.recreateMenuAfterAnimation = true;
                } else {
                    createActionBarMenu();
                }
                updateRowsIds();
                if (this.listAdapter != null) {
                    this.listAdapter.notifyDataSetChanged();
                }
            }
        } else if (id == NotificationCenter.didReceiveNewMessages) {
            if (this.dialog_id != 0) {
                did = this.dialog_id;
            } else if (this.user_id != 0) {
                did = (long) this.user_id;
            } else {
                did = (long) (-this.chat_id);
            }
            if (did == ((Long) args[0]).longValue()) {
                enc = ((int) did) == 0;
                arr = (ArrayList) args[1];
                a = 0;
                while (a < arr.size()) {
                    MessageObject obj = (MessageObject) arr.get(a);
                    if (this.currentEncryptedChat != null && obj.messageOwner.action != null && (obj.messageOwner.action instanceof TL_messageEncryptedAction) && (obj.messageOwner.action.encryptedAction instanceof TL_decryptedMessageActionSetMessageTTL)) {
                        TL_decryptedMessageActionSetMessageTTL action = (TL_decryptedMessageActionSetMessageTTL) obj.messageOwner.action.encryptedAction;
                        if (this.listAdapter != null) {
                            this.listAdapter.notifyDataSetChanged();
                        }
                    }
                    type = DataQuery.getMediaType(obj.messageOwner);
                    if (type != -1) {
                        this.sharedMediaData[type].addMessage(obj, 0, true, enc);
                        a++;
                    } else {
                        return;
                    }
                }
                loadMediaCounts();
            }
        } else if (id == NotificationCenter.messagesDeleted) {
            int channelId = ((Integer) args[1]).intValue();
            if (ChatObject.isChannel(this.currentChat)) {
                if ((channelId != 0 || this.mergeDialogId == 0) && channelId != this.currentChat.var_id) {
                    return;
                }
            } else if (channelId != 0) {
                return;
            }
            ArrayList<Integer> markAsDeletedMessages = args[0];
            int N = markAsDeletedMessages.size();
            for (a = 0; a < N; a++) {
                for (SharedMediaData deleteMessage : this.sharedMediaData) {
                    deleteMessage.deleteMessage(((Integer) markAsDeletedMessages.get(a)).intValue(), 0);
                }
            }
            loadMediaCounts();
        }
    }

    final /* synthetic */ void lambda$didReceivedNotification$21$ProfileActivity(Object[] args) {
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.closeChats);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
        EncryptedChat encryptedChat = args[0];
        Bundle args2 = new Bundle();
        args2.putInt("enc_id", encryptedChat.var_id);
        presentFragment(new ChatActivity(args2), true);
    }

    public void onResume() {
        super.onResume();
        if (this.listAdapter != null) {
            this.listAdapter.notifyDataSetChanged();
        }
        updateProfileData();
        fixLayout();
    }

    public void setPlayProfileAnimation(boolean value) {
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        if (!AndroidUtilities.isTablet() && preferences.getBoolean("view_animations", true)) {
            this.playProfileAnimation = value;
        }
    }

    private void updateSharedMediaRows() {
        if (this.listAdapter != null) {
            int sharedHeaderRowPrev = this.sharedHeaderRow;
            int photosRowPrev = this.photosRow;
            int filesRowPrev = this.filesRow;
            int linksRowPrev = this.linksRow;
            int audioRowPrev = this.audioRow;
            int voiceRowPrev = this.voiceRow;
            int groupsInCommonRowPrev = this.groupsInCommonRow;
            int sharedSectionRowPrev = this.sharedSectionRow;
            updateRowsIds();
            if (sharedHeaderRowPrev == -1 && this.sharedHeaderRow != -1) {
                int newRowsCount = 2;
                if (this.photosRow != -1) {
                    newRowsCount = 2 + 1;
                }
                if (this.filesRow != -1) {
                    newRowsCount++;
                }
                if (this.linksRow != -1) {
                    newRowsCount++;
                }
                if (this.audioRow != -1) {
                    newRowsCount++;
                }
                if (this.voiceRow != -1) {
                    newRowsCount++;
                }
                this.listAdapter.notifyItemRangeChanged(this.sharedHeaderRow, newRowsCount);
            } else if (sharedHeaderRowPrev != -1 && this.sharedHeaderRow != -1) {
                if (!(photosRowPrev == -1 || this.photosRow == -1 || this.prevMediaCount[0] == this.lastMediaCount[0])) {
                    this.listAdapter.notifyItemChanged(this.photosRow);
                }
                if (!(filesRowPrev == -1 || this.filesRow == -1 || this.prevMediaCount[1] == this.lastMediaCount[1])) {
                    this.listAdapter.notifyItemChanged(this.filesRow);
                }
                if (!(linksRowPrev == -1 || this.linksRow == -1 || this.prevMediaCount[3] == this.lastMediaCount[3])) {
                    this.listAdapter.notifyItemChanged(this.linksRow);
                }
                if (!(audioRowPrev == -1 || this.audioRow == -1 || this.prevMediaCount[4] == this.lastMediaCount[4])) {
                    this.listAdapter.notifyItemChanged(this.audioRow);
                }
                if (!(voiceRowPrev == -1 || this.voiceRow == -1 || this.prevMediaCount[2] == this.lastMediaCount[2])) {
                    this.listAdapter.notifyItemChanged(this.voiceRow);
                }
                if (photosRowPrev == -1 && this.photosRow != -1) {
                    this.listAdapter.notifyItemInserted(this.photosRow);
                } else if (photosRowPrev != -1 && this.photosRow == -1) {
                    this.listAdapter.notifyItemRemoved(photosRowPrev);
                }
                if (filesRowPrev == -1 && this.filesRow != -1) {
                    this.listAdapter.notifyItemInserted(this.filesRow);
                } else if (filesRowPrev != -1 && this.filesRow == -1) {
                    this.listAdapter.notifyItemRemoved(filesRowPrev);
                }
                if (linksRowPrev == -1 && this.linksRow != -1) {
                    this.listAdapter.notifyItemInserted(this.linksRow);
                } else if (linksRowPrev != -1 && this.linksRow == -1) {
                    this.listAdapter.notifyItemRemoved(linksRowPrev);
                }
                if (audioRowPrev == -1 && this.audioRow != -1) {
                    this.listAdapter.notifyItemInserted(this.audioRow);
                } else if (audioRowPrev != -1 && this.audioRow == -1) {
                    this.listAdapter.notifyItemRemoved(audioRowPrev);
                }
                if (voiceRowPrev == -1 && this.voiceRow != -1) {
                    this.listAdapter.notifyItemInserted(this.voiceRow);
                } else if (voiceRowPrev != -1 && this.voiceRow == -1) {
                    this.listAdapter.notifyItemRemoved(voiceRowPrev);
                }
            }
        }
    }

    protected void onTransitionAnimationStart(boolean isOpen, boolean backward) {
        if (!backward && this.playProfileAnimation && this.allowProfileAnimation) {
            this.openAnimationInProgress = true;
        }
        NotificationCenter.getInstance(this.currentAccount).setAllowedNotificationsDutingAnimation(new int[]{NotificationCenter.dialogsNeedReload, NotificationCenter.closeChats, NotificationCenter.mediaCountDidLoad, NotificationCenter.mediaCountsDidLoad});
        NotificationCenter.getInstance(this.currentAccount).setAnimationInProgress(true);
    }

    protected void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        if (isOpen && !backward && this.playProfileAnimation && this.allowProfileAnimation) {
            this.openAnimationInProgress = false;
            if (this.recreateMenuAfterAnimation) {
                createActionBarMenu();
            }
        }
        NotificationCenter.getInstance(this.currentAccount).setAnimationInProgress(false);
    }

    public float getAnimationProgress() {
        return this.animationProgress;
    }

    @Keep
    public void setAnimationProgress(float progress) {
        int i;
        int i2;
        this.animationProgress = progress;
        this.listView.setAlpha(progress);
        this.listView.setTranslationX(((float) AndroidUtilities.m9dp(48.0f)) - (((float) AndroidUtilities.m9dp(48.0f)) * progress));
        if (this.user_id != 0 || (ChatObject.isChannel(this.chat_id, this.currentAccount) && !this.currentChat.megagroup)) {
            i = 5;
        } else {
            i = this.chat_id;
        }
        int color = AvatarDrawable.getProfileBackColorForId(i);
        int actionBarColor = Theme.getColor(Theme.key_actionBarDefault);
        int r = Color.red(actionBarColor);
        int g = Color.green(actionBarColor);
        int b = Color.blue(actionBarColor);
        this.topView.setBackgroundColor(Color.rgb(r + ((int) (((float) (Color.red(color) - r)) * progress)), g + ((int) (((float) (Color.green(color) - g)) * progress)), b + ((int) (((float) (Color.blue(color) - b)) * progress))));
        if (this.user_id != 0 || (ChatObject.isChannel(this.chat_id, this.currentAccount) && !this.currentChat.megagroup)) {
            i = 5;
        } else {
            i = this.chat_id;
        }
        color = AvatarDrawable.getIconColorForId(i);
        int iconColor = Theme.getColor(Theme.key_actionBarDefaultIcon);
        r = Color.red(iconColor);
        g = Color.green(iconColor);
        b = Color.blue(iconColor);
        this.actionBar.setItemsColor(Color.rgb(r + ((int) (((float) (Color.red(color) - r)) * progress)), g + ((int) (((float) (Color.green(color) - g)) * progress)), b + ((int) (((float) (Color.blue(color) - b)) * progress))), false);
        color = Theme.getColor(Theme.key_profile_title);
        int titleColor = Theme.getColor(Theme.key_actionBarDefaultTitle);
        r = Color.red(titleColor);
        g = Color.green(titleColor);
        b = Color.blue(titleColor);
        int a = Color.alpha(titleColor);
        int rD = (int) (((float) (Color.red(color) - r)) * progress);
        int gD = (int) (((float) (Color.green(color) - g)) * progress);
        int bD = (int) (((float) (Color.blue(color) - b)) * progress);
        int aD = (int) (((float) (Color.alpha(color) - a)) * progress);
        for (i2 = 0; i2 < 2; i2++) {
            if (this.nameTextView[i2] != null) {
                this.nameTextView[i2].setTextColor(Color.argb(a + aD, r + rD, g + gD, b + bD));
            }
        }
        if (this.user_id != 0 || (ChatObject.isChannel(this.chat_id, this.currentAccount) && !this.currentChat.megagroup)) {
            i = 5;
        } else {
            i = this.chat_id;
        }
        color = AvatarDrawable.getProfileTextColorForId(i);
        int subtitleColor = Theme.getColor(Theme.key_actionBarDefaultSubtitle);
        r = Color.red(subtitleColor);
        g = Color.green(subtitleColor);
        b = Color.blue(subtitleColor);
        a = Color.alpha(subtitleColor);
        rD = (int) (((float) (Color.red(color) - r)) * progress);
        gD = (int) (((float) (Color.green(color) - g)) * progress);
        bD = (int) (((float) (Color.blue(color) - b)) * progress);
        aD = (int) (((float) (Color.alpha(color) - a)) * progress);
        for (i2 = 0; i2 < 2; i2++) {
            if (this.onlineTextView[i2] != null) {
                this.onlineTextView[i2].setTextColor(Color.argb(a + aD, r + rD, g + gD, b + bD));
            }
        }
        this.extraHeight = (int) (((float) this.initialAnimationExtraHeight) * progress);
        color = AvatarDrawable.getProfileColorForId(this.user_id != 0 ? this.user_id : this.chat_id);
        int color2 = AvatarDrawable.getColorForId(this.user_id != 0 ? this.user_id : this.chat_id);
        if (color != color2) {
            this.avatarDrawable.setColor(Color.rgb(Color.red(color2) + ((int) (((float) (Color.red(color) - Color.red(color2))) * progress)), Color.green(color2) + ((int) (((float) (Color.green(color) - Color.green(color2))) * progress)), Color.blue(color2) + ((int) (((float) (Color.blue(color) - Color.blue(color2))) * progress))));
            this.avatarImage.invalidate();
        }
        needLayout();
    }

    protected AnimatorSet onCustomTransitionAnimation(boolean isOpen, final Runnable callback) {
        if (!this.playProfileAnimation || !this.allowProfileAnimation) {
            return null;
        }
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(180);
        this.listView.setLayerType(2, null);
        ActionBarMenu menu = this.actionBar.createMenu();
        if (menu.getItem(10) == null && this.animatingItem == null) {
            this.animatingItem = menu.addItem(10, (int) R.drawable.ic_ab_other);
        }
        ArrayList<Animator> animators;
        int a;
        Object obj;
        String str;
        float[] fArr;
        if (isOpen) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.onlineTextView[1].getLayoutParams();
            layoutParams.rightMargin = (int) ((-21.0f * AndroidUtilities.density) + ((float) AndroidUtilities.m9dp(8.0f)));
            this.onlineTextView[1].setLayoutParams(layoutParams);
            int width = (int) Math.ceil((double) (((float) (AndroidUtilities.displaySize.x - AndroidUtilities.m9dp(126.0f))) + (21.0f * AndroidUtilities.density)));
            layoutParams = (FrameLayout.LayoutParams) this.nameTextView[1].getLayoutParams();
            if (((float) width) < (this.nameTextView[1].getPaint().measureText(this.nameTextView[1].getText().toString()) * 1.12f) + ((float) this.nameTextView[1].getSideDrawablesSize())) {
                layoutParams.width = (int) Math.ceil((double) (((float) width) / 1.12f));
            } else {
                layoutParams.width = -2;
            }
            this.nameTextView[1].setLayoutParams(layoutParams);
            this.initialAnimationExtraHeight = AndroidUtilities.m9dp(88.0f);
            this.fragmentView.setBackgroundColor(0);
            setAnimationProgress(0.0f);
            animators = new ArrayList();
            animators.add(ObjectAnimator.ofFloat(this, "animationProgress", new float[]{0.0f, 1.0f}));
            if (this.writeButton != null) {
                this.writeButton.setScaleX(0.2f);
                this.writeButton.setScaleY(0.2f);
                this.writeButton.setAlpha(0.0f);
                animators.add(ObjectAnimator.ofFloat(this.writeButton, "scaleX", new float[]{1.0f}));
                animators.add(ObjectAnimator.ofFloat(this.writeButton, "scaleY", new float[]{1.0f}));
                animators.add(ObjectAnimator.ofFloat(this.writeButton, "alpha", new float[]{1.0f}));
            }
            a = 0;
            while (a < 2) {
                this.onlineTextView[a].setAlpha(a == 0 ? 1.0f : 0.0f);
                this.nameTextView[a].setAlpha(a == 0 ? 1.0f : 0.0f);
                obj = this.onlineTextView[a];
                str = "alpha";
                fArr = new float[1];
                fArr[0] = a == 0 ? 0.0f : 1.0f;
                animators.add(ObjectAnimator.ofFloat(obj, str, fArr));
                obj = this.nameTextView[a];
                str = "alpha";
                fArr = new float[1];
                fArr[0] = a == 0 ? 0.0f : 1.0f;
                animators.add(ObjectAnimator.ofFloat(obj, str, fArr));
                a++;
            }
            if (this.animatingItem != null) {
                this.animatingItem.setAlpha(1.0f);
                animators.add(ObjectAnimator.ofFloat(this.animatingItem, "alpha", new float[]{0.0f}));
            }
            if (this.callItem != null) {
                this.callItem.setAlpha(0.0f);
                animators.add(ObjectAnimator.ofFloat(this.callItem, "alpha", new float[]{1.0f}));
            }
            if (this.editItem != null) {
                this.editItem.setAlpha(0.0f);
                animators.add(ObjectAnimator.ofFloat(this.editItem, "alpha", new float[]{1.0f}));
            }
            if (this.addItem != null) {
                this.addItem.setAlpha(0.0f);
                animators.add(ObjectAnimator.ofFloat(this.addItem, "alpha", new float[]{1.0f}));
            }
            animatorSet.playTogether(animators);
        } else {
            this.initialAnimationExtraHeight = this.extraHeight;
            animators = new ArrayList();
            animators.add(ObjectAnimator.ofFloat(this, "animationProgress", new float[]{1.0f, 0.0f}));
            if (this.writeButton != null) {
                animators.add(ObjectAnimator.ofFloat(this.writeButton, "scaleX", new float[]{0.2f}));
                animators.add(ObjectAnimator.ofFloat(this.writeButton, "scaleY", new float[]{0.2f}));
                animators.add(ObjectAnimator.ofFloat(this.writeButton, "alpha", new float[]{0.0f}));
            }
            a = 0;
            while (a < 2) {
                obj = this.onlineTextView[a];
                str = "alpha";
                fArr = new float[1];
                fArr[0] = a == 0 ? 1.0f : 0.0f;
                animators.add(ObjectAnimator.ofFloat(obj, str, fArr));
                obj = this.nameTextView[a];
                str = "alpha";
                fArr = new float[1];
                fArr[0] = a == 0 ? 1.0f : 0.0f;
                animators.add(ObjectAnimator.ofFloat(obj, str, fArr));
                a++;
            }
            if (this.animatingItem != null) {
                this.animatingItem.setAlpha(0.0f);
                animators.add(ObjectAnimator.ofFloat(this.animatingItem, "alpha", new float[]{1.0f}));
            }
            if (this.callItem != null) {
                this.callItem.setAlpha(1.0f);
                animators.add(ObjectAnimator.ofFloat(this.callItem, "alpha", new float[]{0.0f}));
            }
            if (this.editItem != null) {
                this.editItem.setAlpha(1.0f);
                animators.add(ObjectAnimator.ofFloat(this.editItem, "alpha", new float[]{0.0f}));
            }
            if (this.addItem != null) {
                this.addItem.setAlpha(1.0f);
                animators.add(ObjectAnimator.ofFloat(this.addItem, "alpha", new float[]{0.0f}));
            }
            animatorSet.playTogether(animators);
        }
        animatorSet.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                ProfileActivity.this.listView.setLayerType(0, null);
                if (ProfileActivity.this.animatingItem != null) {
                    ProfileActivity.this.actionBar.createMenu().clearItems();
                    ProfileActivity.this.animatingItem = null;
                }
                callback.run();
            }
        });
        animatorSet.setInterpolator(new DecelerateInterpolator());
        animatorSet.getClass();
        AndroidUtilities.runOnUIThread(ProfileActivity$$Lambda$14.get$Lambda(animatorSet), 50);
        return animatorSet;
    }

    private void updateOnlineCount() {
        this.onlineCount = 0;
        int currentTime = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
        this.sortedUsers.clear();
        if ((this.chatInfo instanceof TL_chatFull) || ((this.chatInfo instanceof TL_channelFull) && this.chatInfo.participants_count <= Callback.DEFAULT_DRAG_ANIMATION_DURATION && this.chatInfo.participants != null)) {
            for (int a = 0; a < this.chatInfo.participants.participants.size(); a++) {
                User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(((ChatParticipant) this.chatInfo.participants.participants.get(a)).user_id));
                if (!(user == null || user.status == null || ((user.status.expires <= currentTime && user.var_id != UserConfig.getInstance(this.currentAccount).getClientUserId()) || user.status.expires <= 10000))) {
                    this.onlineCount++;
                }
                this.sortedUsers.add(Integer.valueOf(a));
            }
            if (this.chatInfo instanceof TL_chatFull) {
                try {
                    Collections.sort(this.sortedUsers, new ProfileActivity$$Lambda$15(this));
                } catch (Throwable e) {
                    FileLog.m13e(e);
                }
                if (this.listAdapter != null) {
                    this.listAdapter.notifyItemRangeChanged(this.membersStartRow, this.sortedUsers.size());
                }
            }
        }
    }

    final /* synthetic */ int lambda$updateOnlineCount$22$ProfileActivity(Integer lhs, Integer rhs) {
        User user1 = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(((ChatParticipant) this.chatInfo.participants.participants.get(rhs.intValue())).user_id));
        User user2 = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(((ChatParticipant) this.chatInfo.participants.participants.get(lhs.intValue())).user_id));
        int status1 = 0;
        int status2 = 0;
        if (!(user1 == null || user1.status == null)) {
            status1 = user1.var_id == UserConfig.getInstance(this.currentAccount).getClientUserId() ? ConnectionsManager.getInstance(this.currentAccount).getCurrentTime() + DefaultLoadControl.DEFAULT_MAX_BUFFER_MS : user1.status.expires;
        }
        if (!(user2 == null || user2.status == null)) {
            status2 = user2.var_id == UserConfig.getInstance(this.currentAccount).getClientUserId() ? ConnectionsManager.getInstance(this.currentAccount).getCurrentTime() + DefaultLoadControl.DEFAULT_MAX_BUFFER_MS : user2.status.expires;
        }
        if (status1 <= 0 || status2 <= 0) {
            if (status1 >= 0 || status2 >= 0) {
                if ((status1 < 0 && status2 > 0) || (status1 == 0 && status2 != 0)) {
                    return -1;
                }
                if ((status2 >= 0 || status1 <= 0) && (status2 != 0 || status1 == 0)) {
                    return 0;
                }
                return 1;
            } else if (status1 > status2) {
                return 1;
            } else {
                if (status1 < status2) {
                    return -1;
                }
                return 0;
            }
        } else if (status1 > status2) {
            return 1;
        } else {
            if (status1 < status2) {
                return -1;
            }
            return 0;
        }
    }

    public void setChatInfo(ChatFull value) {
        this.chatInfo = value;
        if (!(this.chatInfo == null || this.chatInfo.migrated_from_chat_id == 0)) {
            this.mergeDialogId = (long) (-this.chatInfo.migrated_from_chat_id);
        }
        fetchUsersFromChannelInfo();
    }

    public void setUserInfo(TL_userFull value) {
        this.userInfo = value;
    }

    private void fetchUsersFromChannelInfo() {
        if (this.currentChat != null && this.currentChat.megagroup && (this.chatInfo instanceof TL_channelFull) && this.chatInfo.participants != null) {
            for (int a = 0; a < this.chatInfo.participants.participants.size(); a++) {
                ChatParticipant chatParticipant = (ChatParticipant) this.chatInfo.participants.participants.get(a);
                this.participantsMap.put(chatParticipant.user_id, chatParticipant);
            }
        }
    }

    private void kickUser(int uid) {
        if (uid != 0) {
            MessagesController.getInstance(this.currentAccount).deleteUserFromChat(this.chat_id, MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(uid)), this.chatInfo);
            return;
        }
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.closeChats);
        if (AndroidUtilities.isTablet()) {
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.closeChats, Long.valueOf(-((long) this.chat_id)));
        } else {
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
        }
        MessagesController.getInstance(this.currentAccount).deleteUserFromChat(this.chat_id, MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(UserConfig.getInstance(this.currentAccount).getClientUserId())), this.chatInfo);
        this.playProfileAnimation = false;
        lambda$checkDiscard$70$PassportActivity();
    }

    public boolean isChat() {
        return this.chat_id != 0;
    }

    private void updateRowsIds() {
        int i;
        this.rowCount = 0;
        this.emptyRow = -1;
        this.infoHeaderRow = -1;
        this.phoneRow = -1;
        this.userInfoRow = -1;
        this.channelInfoRow = -1;
        this.usernameRow = -1;
        this.settingsTimerRow = -1;
        this.settingsKeyRow = -1;
        this.notificationsDividerRow = -1;
        this.notificationsRow = -1;
        this.infoSectionRow = -1;
        this.settingsSectionRow = -1;
        this.membersHeaderRow = -1;
        this.membersStartRow = -1;
        this.membersEndRow = -1;
        this.addMemberRow = -1;
        this.subscribersRow = -1;
        this.administratorsRow = -1;
        this.blockedUsersRow = -1;
        this.membersSectionRow = -1;
        this.sharedHeaderRow = -1;
        this.photosRow = -1;
        this.filesRow = -1;
        this.linksRow = -1;
        this.audioRow = -1;
        this.voiceRow = -1;
        this.groupsInCommonRow = -1;
        this.sharedSectionRow = -1;
        this.startSecretChatRow = -1;
        this.leaveChannelRow = -1;
        this.joinRow = -1;
        this.lastSectionRow = -1;
        this.convertRow = -1;
        this.convertHelpRow = -1;
        boolean hasMedia = false;
        for (int i2 : this.lastMediaCount) {
            if (i2 > 0) {
                hasMedia = true;
                break;
            }
        }
        if (this.user_id != 0 && LocaleController.isRTL) {
            i2 = this.rowCount;
            this.rowCount = i2 + 1;
            this.emptyRow = i2;
        }
        int i3;
        int i4;
        if (this.user_id != 0) {
            User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.user_id));
            boolean hasInfo;
            if ((this.userInfo == null || TextUtils.isEmpty(this.userInfo.about)) && (user == null || TextUtils.isEmpty(user.username))) {
                hasInfo = false;
            } else {
                hasInfo = true;
            }
            boolean hasPhone;
            if (user == null || TextUtils.isEmpty(user.phone)) {
                hasPhone = false;
            } else {
                hasPhone = true;
            }
            i2 = this.rowCount;
            this.rowCount = i2 + 1;
            this.infoHeaderRow = i2;
            if (!this.isBot && (hasPhone || !(hasPhone || hasInfo))) {
                i2 = this.rowCount;
                this.rowCount = i2 + 1;
                this.phoneRow = i2;
            }
            if (!(this.userInfo == null || TextUtils.isEmpty(this.userInfo.about))) {
                i2 = this.rowCount;
                this.rowCount = i2 + 1;
                this.userInfoRow = i2;
            }
            if (!(user == null || TextUtils.isEmpty(user.username))) {
                i2 = this.rowCount;
                this.rowCount = i2 + 1;
                this.usernameRow = i2;
            }
            if (!(this.phoneRow == -1 && this.userInfoRow == -1 && this.usernameRow == -1)) {
                i2 = this.rowCount;
                this.rowCount = i2 + 1;
                this.notificationsDividerRow = i2;
            }
            if (this.user_id != UserConfig.getInstance(this.currentAccount).getClientUserId()) {
                i2 = this.rowCount;
                this.rowCount = i2 + 1;
                this.notificationsRow = i2;
            }
            i2 = this.rowCount;
            this.rowCount = i2 + 1;
            this.infoSectionRow = i2;
            if (this.currentEncryptedChat instanceof TL_encryptedChat) {
                i2 = this.rowCount;
                this.rowCount = i2 + 1;
                this.settingsTimerRow = i2;
                i2 = this.rowCount;
                this.rowCount = i2 + 1;
                this.settingsKeyRow = i2;
                i2 = this.rowCount;
                this.rowCount = i2 + 1;
                this.settingsSectionRow = i2;
            }
            if (hasMedia || !(this.userInfo == null || this.userInfo.common_chats_count == 0)) {
                i2 = this.rowCount;
                this.rowCount = i2 + 1;
                this.sharedHeaderRow = i2;
                if (this.lastMediaCount[0] > 0) {
                    i3 = this.rowCount;
                    this.rowCount = i3 + 1;
                    this.photosRow = i3;
                } else {
                    this.photosRow = -1;
                }
                if (this.lastMediaCount[1] > 0) {
                    i4 = this.rowCount;
                    this.rowCount = i4 + 1;
                    this.filesRow = i4;
                } else {
                    this.filesRow = -1;
                }
                if (this.lastMediaCount[3] > 0) {
                    i4 = this.rowCount;
                    this.rowCount = i4 + 1;
                    this.linksRow = i4;
                } else {
                    this.linksRow = -1;
                }
                if (this.lastMediaCount[4] > 0) {
                    i4 = this.rowCount;
                    this.rowCount = i4 + 1;
                    this.audioRow = i4;
                } else {
                    this.audioRow = -1;
                }
                if (this.lastMediaCount[2] > 0) {
                    i4 = this.rowCount;
                    this.rowCount = i4 + 1;
                    this.voiceRow = i4;
                } else {
                    this.voiceRow = -1;
                }
                if (!(this.userInfo == null || this.userInfo.common_chats_count == 0)) {
                    i4 = this.rowCount;
                    this.rowCount = i4 + 1;
                    this.groupsInCommonRow = i4;
                }
                i4 = this.rowCount;
                this.rowCount = i4 + 1;
                this.sharedSectionRow = i4;
            }
            if (user != null && !this.isBot && this.currentEncryptedChat == null && user.var_id != UserConfig.getInstance(this.currentAccount).getClientUserId()) {
                i4 = this.rowCount;
                this.rowCount = i4 + 1;
                this.startSecretChatRow = i4;
                i4 = this.rowCount;
                this.rowCount = i4 + 1;
                this.lastSectionRow = i4;
            }
        } else if (this.chat_id == 0) {
        } else {
            if (this.chat_id > 0) {
                if (ChatObject.isChannel(this.currentChat) && !((this.chatInfo == null || TextUtils.isEmpty(this.chatInfo.about)) && TextUtils.isEmpty(this.currentChat.username))) {
                    i2 = this.rowCount;
                    this.rowCount = i2 + 1;
                    this.infoHeaderRow = i2;
                    if (!(this.chatInfo == null || TextUtils.isEmpty(this.chatInfo.about))) {
                        i2 = this.rowCount;
                        this.rowCount = i2 + 1;
                        this.channelInfoRow = i2;
                    }
                    if (!TextUtils.isEmpty(this.currentChat.username)) {
                        i2 = this.rowCount;
                        this.rowCount = i2 + 1;
                        this.usernameRow = i2;
                    }
                }
                if (!(this.channelInfoRow == -1 && this.usernameRow == -1)) {
                    i2 = this.rowCount;
                    this.rowCount = i2 + 1;
                    this.notificationsDividerRow = i2;
                }
                i2 = this.rowCount;
                this.rowCount = i2 + 1;
                this.notificationsRow = i2;
                i2 = this.rowCount;
                this.rowCount = i2 + 1;
                this.infoSectionRow = i2;
                if (ChatObject.isChannel(this.currentChat) && !this.currentChat.megagroup && this.chatInfo != null && (this.currentChat.creator || this.chatInfo.can_view_participants)) {
                    i2 = this.rowCount;
                    this.rowCount = i2 + 1;
                    this.membersHeaderRow = i2;
                    i2 = this.rowCount;
                    this.rowCount = i2 + 1;
                    this.subscribersRow = i2;
                    i2 = this.rowCount;
                    this.rowCount = i2 + 1;
                    this.administratorsRow = i2;
                    i2 = this.rowCount;
                    this.rowCount = i2 + 1;
                    this.blockedUsersRow = i2;
                    i2 = this.rowCount;
                    this.rowCount = i2 + 1;
                    this.membersSectionRow = i2;
                }
                if (hasMedia) {
                    i2 = this.rowCount;
                    this.rowCount = i2 + 1;
                    this.sharedHeaderRow = i2;
                    if (this.lastMediaCount[0] > 0) {
                        i3 = this.rowCount;
                        this.rowCount = i3 + 1;
                        this.photosRow = i3;
                    } else {
                        this.photosRow = -1;
                    }
                    if (this.lastMediaCount[1] > 0) {
                        i4 = this.rowCount;
                        this.rowCount = i4 + 1;
                        this.filesRow = i4;
                    } else {
                        this.filesRow = -1;
                    }
                    if (this.lastMediaCount[3] > 0) {
                        i4 = this.rowCount;
                        this.rowCount = i4 + 1;
                        this.linksRow = i4;
                    } else {
                        this.linksRow = -1;
                    }
                    if (this.lastMediaCount[4] > 0) {
                        i4 = this.rowCount;
                        this.rowCount = i4 + 1;
                        this.audioRow = i4;
                    } else {
                        this.audioRow = -1;
                    }
                    if (this.lastMediaCount[2] > 0) {
                        i4 = this.rowCount;
                        this.rowCount = i4 + 1;
                        this.voiceRow = i4;
                    } else {
                        this.voiceRow = -1;
                    }
                    i4 = this.rowCount;
                    this.rowCount = i4 + 1;
                    this.sharedSectionRow = i4;
                }
                if (ChatObject.isChannel(this.currentChat)) {
                    if (!(this.currentChat.creator || this.currentChat.left || this.currentChat.kicked || this.currentChat.megagroup)) {
                        i4 = this.rowCount;
                        this.rowCount = i4 + 1;
                        this.leaveChannelRow = i4;
                        i4 = this.rowCount;
                        this.rowCount = i4 + 1;
                        this.lastSectionRow = i4;
                    }
                    if (!(this.chatInfo == null || !this.currentChat.megagroup || this.chatInfo.participants == null || this.chatInfo.participants.participants.isEmpty())) {
                        i4 = this.rowCount;
                        this.rowCount = i4 + 1;
                        this.membersHeaderRow = i4;
                        this.membersStartRow = this.rowCount;
                        this.rowCount += this.chatInfo.participants.participants.size();
                        this.membersEndRow = this.rowCount;
                        i4 = this.rowCount;
                        this.rowCount = i4 + 1;
                        this.membersSectionRow = i4;
                    }
                    if (this.currentChat.megagroup && (((this.currentChat.admin_rights != null && this.currentChat.admin_rights.invite_users) || this.currentChat.creator || this.currentChat.democracy) && (this.chatInfo == null || this.chatInfo.participants_count < MessagesController.getInstance(this.currentAccount).maxMegagroupCount))) {
                        i4 = this.rowCount;
                        this.rowCount = i4 + 1;
                        this.addMemberRow = i4;
                        i4 = this.rowCount;
                        this.rowCount = i4 + 1;
                        this.lastSectionRow = i4;
                    }
                    if (this.lastSectionRow == -1 && this.currentChat.left && !this.currentChat.kicked) {
                        i4 = this.rowCount;
                        this.rowCount = i4 + 1;
                        this.joinRow = i4;
                        i4 = this.rowCount;
                        this.rowCount = i4 + 1;
                        this.lastSectionRow = i4;
                        return;
                    }
                    return;
                }
                if (this.chatInfo != null && this.currentChat.creator && this.chatInfo.participants.participants.size() >= MessagesController.getInstance(this.currentAccount).minGroupConvertSize) {
                    i4 = this.rowCount;
                    this.rowCount = i4 + 1;
                    this.convertRow = i4;
                    i4 = this.rowCount;
                    this.rowCount = i4 + 1;
                    this.convertHelpRow = i4;
                }
                if (!(this.chatInfo == null || (this.chatInfo.participants instanceof TL_chatParticipantsForbidden))) {
                    i4 = this.rowCount;
                    this.rowCount = i4 + 1;
                    this.membersHeaderRow = i4;
                    this.membersStartRow = this.rowCount;
                    this.rowCount += this.chatInfo.participants.participants.size();
                    this.membersEndRow = this.rowCount;
                    i4 = this.rowCount;
                    this.rowCount = i4 + 1;
                    this.membersSectionRow = i4;
                }
                if (this.chatInfo != null && !(this.chatInfo.participants instanceof TL_chatParticipantsForbidden) && this.chatInfo.participants.participants.size() < MessagesController.getInstance(this.currentAccount).maxGroupCount) {
                    if (this.currentChat.admin || this.currentChat.creator || !this.currentChat.admins_enabled) {
                        i4 = this.rowCount;
                        this.rowCount = i4 + 1;
                        this.addMemberRow = i4;
                        i4 = this.rowCount;
                        this.rowCount = i4 + 1;
                        this.lastSectionRow = i4;
                    }
                }
            } else if (!ChatObject.isChannel(this.currentChat) && this.chatInfo != null && !(this.chatInfo.participants instanceof TL_chatParticipantsForbidden)) {
                i4 = this.rowCount;
                this.rowCount = i4 + 1;
                this.membersHeaderRow = i4;
                this.membersStartRow = this.rowCount;
                this.rowCount += this.chatInfo.participants.participants.size();
                this.membersEndRow = this.rowCount;
                i4 = this.rowCount;
                this.rowCount = i4 + 1;
                this.membersSectionRow = i4;
                i4 = this.rowCount;
                this.rowCount = i4 + 1;
                this.addMemberRow = i4;
            }
        }
    }

    private void updateProfileData() {
        if (this.avatarImage != null && this.nameTextView != null) {
            String onlineTextOverride;
            int currentConnectionState = ConnectionsManager.getInstance(this.currentAccount).getConnectionState();
            if (currentConnectionState == 2) {
                onlineTextOverride = LocaleController.getString("WaitingForNetwork", R.string.WaitingForNetwork);
            } else if (currentConnectionState == 1) {
                onlineTextOverride = LocaleController.getString("Connecting", R.string.Connecting);
            } else if (currentConnectionState == 5) {
                onlineTextOverride = LocaleController.getString("Updating", R.string.Updating);
            } else if (currentConnectionState == 4) {
                onlineTextOverride = LocaleController.getString("ConnectingToProxy", R.string.ConnectingToProxy);
            } else {
                onlineTextOverride = null;
            }
            TLObject photo;
            FileLocation photoBig;
            String newString;
            int a;
            if (this.user_id != 0) {
                String newString2;
                User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.user_id));
                photo = null;
                photoBig = null;
                if (user.photo != null) {
                    photo = user.photo.photo_small;
                    photoBig = user.photo.photo_big;
                }
                this.avatarDrawable.setInfo(user);
                this.avatarImage.setImage(photo, "50_50", this.avatarDrawable, (Object) user);
                newString = UserObject.getUserName(user);
                if (user.var_id == UserConfig.getInstance(this.currentAccount).getClientUserId()) {
                    newString2 = LocaleController.getString("ChatYourSelf", R.string.ChatYourSelf);
                    newString = LocaleController.getString("ChatYourSelfName", R.string.ChatYourSelfName);
                } else if (user.var_id == 333000 || user.var_id == 777000) {
                    newString2 = LocaleController.getString("ServiceNotifications", R.string.ServiceNotifications);
                } else if (this.isBot) {
                    newString2 = LocaleController.getString("Bot", R.string.Bot);
                } else {
                    newString2 = LocaleController.formatUserStatus(this.currentAccount, user);
                }
                for (a = 0; a < 2; a++) {
                    if (this.nameTextView[a] != null) {
                        if (a == 0 && user.var_id != UserConfig.getInstance(this.currentAccount).getClientUserId() && user.var_id / 1000 != 777 && user.var_id / 1000 != 333 && user.phone != null && user.phone.length() != 0 && ContactsController.getInstance(this.currentAccount).contactsDict.get(Integer.valueOf(user.var_id)) == null && (ContactsController.getInstance(this.currentAccount).contactsDict.size() != 0 || !ContactsController.getInstance(this.currentAccount).isLoadingContacts())) {
                            String phoneString = CLASSNAMEPhoneFormat.getInstance().format("+" + user.phone);
                            if (!this.nameTextView[a].getText().equals(phoneString)) {
                                this.nameTextView[a].setText(phoneString);
                            }
                        } else if (!this.nameTextView[a].getText().equals(newString)) {
                            this.nameTextView[a].setText(newString);
                        }
                        if (a == 0 && onlineTextOverride != null) {
                            this.onlineTextView[a].setText(onlineTextOverride);
                        } else if (!this.onlineTextView[a].getText().equals(newString2)) {
                            this.onlineTextView[a].setText(newString2);
                        }
                        Drawable leftIcon = this.currentEncryptedChat != null ? Theme.chat_lockIconDrawable : null;
                        Drawable rightIcon = null;
                        if (a == 0) {
                            rightIcon = MessagesController.getInstance(this.currentAccount).isDialogMuted((this.dialog_id > 0 ? 1 : (this.dialog_id == 0 ? 0 : -1)) != 0 ? this.dialog_id : (long) this.user_id) ? Theme.chat_muteIconDrawable : null;
                        } else if (user.verified) {
                            Drawable combinedDrawable = new CombinedDrawable(Theme.profile_verifiedDrawable, Theme.profile_verifiedCheckDrawable);
                        }
                        this.nameTextView[a].setLeftDrawable(leftIcon);
                        this.nameTextView[a].setRightDrawable(rightIcon);
                    }
                }
                this.avatarImage.getImageReceiver().setVisible(!PhotoViewer.isShowingImage(photoBig), false);
            } else if (this.chat_id != 0) {
                String shortNumber;
                Object chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.chat_id));
                if (chat != null) {
                    this.currentChat = chat;
                } else {
                    chat = this.currentChat;
                }
                if (!ChatObject.isChannel(chat)) {
                    int count = chat.participants_count;
                    if (this.chatInfo != null) {
                        count = this.chatInfo.participants.participants.size();
                    }
                    if (count == 0 || this.onlineCount <= 1) {
                        newString = LocaleController.formatPluralString("Members", count);
                    } else {
                        newString = String.format("%s, %s", new Object[]{LocaleController.formatPluralString("Members", count), LocaleController.formatPluralString("OnlineCount", this.onlineCount)});
                    }
                } else if (this.chatInfo == null || (!this.currentChat.megagroup && (this.chatInfo.participants_count == 0 || this.currentChat.admin || this.chatInfo.can_view_participants))) {
                    if (this.currentChat.megagroup) {
                        newString = LocaleController.getString("Loading", R.string.Loading).toLowerCase();
                    } else if ((chat.flags & 64) != 0) {
                        newString = LocaleController.getString("ChannelPublic", R.string.ChannelPublic).toLowerCase();
                    } else {
                        newString = LocaleController.getString("ChannelPrivate", R.string.ChannelPrivate).toLowerCase();
                    }
                } else if (!this.currentChat.megagroup || this.chatInfo.participants_count > 200) {
                    shortNumber = LocaleController.formatShortNumber(this.chatInfo.participants_count, new int[1]);
                    if (this.currentChat.megagroup) {
                        newString = LocaleController.formatPluralString("Members", this.chatInfo.participants_count);
                    } else {
                        newString = LocaleController.formatPluralString("Subscribers", this.chatInfo.participants_count);
                    }
                } else if (this.onlineCount <= 1 || this.chatInfo.participants_count == 0) {
                    newString = LocaleController.formatPluralString("Members", this.chatInfo.participants_count);
                } else {
                    newString = String.format("%s, %s", new Object[]{LocaleController.formatPluralString("Members", this.chatInfo.participants_count), LocaleController.formatPluralString("OnlineCount", this.onlineCount)});
                }
                a = 0;
                while (a < 2) {
                    if (this.nameTextView[a] != null) {
                        if (!(chat.title == null || this.nameTextView[a].getText().equals(chat.title))) {
                            this.nameTextView[a].setText(chat.title);
                        }
                        this.nameTextView[a].setLeftDrawable(null);
                        if (a == 0) {
                            this.nameTextView[a].setRightDrawable(MessagesController.getInstance(this.currentAccount).isDialogMuted((long) (-this.chat_id)) ? Theme.chat_muteIconDrawable : null);
                        } else if (chat.verified) {
                            this.nameTextView[a].setRightDrawable(new CombinedDrawable(Theme.profile_verifiedDrawable, Theme.profile_verifiedCheckDrawable));
                        } else {
                            this.nameTextView[a].setRightDrawable(null);
                        }
                        if (a == 0 && onlineTextOverride != null) {
                            this.onlineTextView[a].setText(onlineTextOverride);
                        } else if (!this.currentChat.megagroup || this.chatInfo == null || this.chatInfo.participants_count > 200 || this.onlineCount <= 0) {
                            if (a == 0 && ChatObject.isChannel(this.currentChat) && this.chatInfo != null && this.chatInfo.participants_count != 0 && (this.currentChat.megagroup || this.currentChat.broadcast)) {
                                int[] result = new int[1];
                                shortNumber = LocaleController.formatShortNumber(this.chatInfo.participants_count, result);
                                if (this.currentChat.megagroup) {
                                    this.onlineTextView[a].setText(LocaleController.formatPluralString("Members", result[0]).replace(String.format("%d", new Object[]{Integer.valueOf(result[0])}), shortNumber));
                                } else {
                                    this.onlineTextView[a].setText(LocaleController.formatPluralString("Subscribers", result[0]).replace(String.format("%d", new Object[]{Integer.valueOf(result[0])}), shortNumber));
                                }
                            } else if (!this.onlineTextView[a].getText().equals(newString)) {
                                this.onlineTextView[a].setText(newString);
                            }
                        } else if (!this.onlineTextView[a].getText().equals(newString)) {
                            this.onlineTextView[a].setText(newString);
                        }
                    }
                    a++;
                }
                photo = null;
                photoBig = null;
                if (chat.photo != null) {
                    photo = chat.photo.photo_small;
                    photoBig = chat.photo.photo_big;
                }
                this.avatarDrawable.setInfo((Chat) chat);
                this.avatarImage.setImage(photo, "50_50", this.avatarDrawable, chat);
                this.avatarImage.getImageReceiver().setVisible(!PhotoViewer.isShowingImage(photoBig), false);
            }
        }
    }

    private void createActionBarMenu() {
        ActionBarMenu menu = this.actionBar.createMenu();
        menu.clearItems();
        this.animatingItem = null;
        ActionBarMenuItem item = null;
        if (this.user_id != 0) {
            if (UserConfig.getInstance(this.currentAccount).getClientUserId() != this.user_id) {
                if (this.userInfo != null && this.userInfo.phone_calls_available) {
                    this.callItem = menu.addItem(15, (int) R.drawable.ic_call_white_24dp);
                }
                if (ContactsController.getInstance(this.currentAccount).contactsDict.get(Integer.valueOf(this.user_id)) == null) {
                    User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.user_id));
                    if (user != null) {
                        item = menu.addItem(10, (int) R.drawable.ic_ab_other);
                        if (this.isBot) {
                            if (!user.bot_nochats) {
                                item.addSubItem(9, LocaleController.getString("BotInvite", R.string.BotInvite));
                            }
                            item.addSubItem(10, LocaleController.getString("BotShare", R.string.BotShare));
                        }
                        if (user.phone != null && user.phone.length() != 0) {
                            CharSequence string;
                            item.addSubItem(1, LocaleController.getString("AddContact", R.string.AddContact));
                            item.addSubItem(3, LocaleController.getString("ShareContact", R.string.ShareContact));
                            if (this.userBlocked) {
                                string = LocaleController.getString("Unblock", R.string.Unblock);
                            } else {
                                string = LocaleController.getString("BlockContact", R.string.BlockContact);
                            }
                            item.addSubItem(2, string);
                        } else if (this.isBot) {
                            item.addSubItem(2, !this.userBlocked ? LocaleController.getString("BotStop", R.string.BotStop) : LocaleController.getString("BotRestart", R.string.BotRestart));
                        } else {
                            item.addSubItem(2, !this.userBlocked ? LocaleController.getString("BlockContact", R.string.BlockContact) : LocaleController.getString("Unblock", R.string.Unblock));
                        }
                    } else {
                        return;
                    }
                }
                item = menu.addItem(10, (int) R.drawable.ic_ab_other);
                item.addSubItem(3, LocaleController.getString("ShareContact", R.string.ShareContact));
                item.addSubItem(2, !this.userBlocked ? LocaleController.getString("BlockContact", R.string.BlockContact) : LocaleController.getString("Unblock", R.string.Unblock));
                item.addSubItem(4, LocaleController.getString("EditContact", R.string.EditContact));
                item.addSubItem(5, LocaleController.getString("DeleteContact", R.string.DeleteContact));
            } else {
                item = menu.addItem(10, (int) R.drawable.ic_ab_other);
                item.addSubItem(3, LocaleController.getString("ShareContact", R.string.ShareContact));
            }
        } else if (this.chat_id != 0) {
            if (this.chat_id > 0) {
                Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.chat_id));
                if (ChatObject.isChannel(chat)) {
                    if (this.currentChat.megagroup && (((this.currentChat.admin_rights != null && this.currentChat.admin_rights.invite_users) || this.currentChat.creator || this.currentChat.democracy) && (this.chatInfo == null || this.chatInfo.participants_count < MessagesController.getInstance(this.currentAccount).maxMegagroupCount))) {
                        this.addItem = menu.addItem(18, (int) R.drawable.group_addmember);
                    }
                    if ((chat.megagroup && ChatObject.hasAdminRights(chat)) || (!chat.megagroup && ChatObject.canChangeChatInfo(chat))) {
                        this.editItem = menu.addItem(12, (int) R.drawable.menu_settings_filled);
                        if (null == null) {
                            item = menu.addItem(10, (int) R.drawable.ic_ab_other);
                        }
                    }
                    if (chat.megagroup) {
                        if (item == null) {
                            item = menu.addItem(10, (int) R.drawable.ic_ab_other);
                        }
                        item.addSubItem(17, LocaleController.getString("SearchMembers", R.string.SearchMembers));
                        if (!(chat.creator || chat.left || chat.kicked)) {
                            item.addSubItem(7, LocaleController.getString("LeaveMegaMenu", R.string.LeaveMegaMenu));
                        }
                    }
                } else {
                    if (this.chatInfo != null && !(this.chatInfo.participants instanceof TL_chatParticipantsForbidden) && this.chatInfo.participants.participants.size() < MessagesController.getInstance(this.currentAccount).maxGroupCount && (this.currentChat.admin || this.currentChat.creator || !this.currentChat.admins_enabled)) {
                        this.addItem = menu.addItem(18, (int) R.drawable.group_addmember);
                    }
                    if (!chat.admins_enabled || chat.creator || chat.admin) {
                        this.editItem = menu.addItem(8, (int) R.drawable.group_edit_profile);
                    }
                    item = menu.addItem(10, (int) R.drawable.ic_ab_other);
                    if (chat.creator && this.chat_id > 0) {
                        item.addSubItem(11, LocaleController.getString("SetAdmins", R.string.SetAdmins));
                    }
                    if (!chat.admins_enabled || chat.creator || chat.admin) {
                        item.addSubItem(8, LocaleController.getString("ChannelEdit", R.string.ChannelEdit));
                    }
                    item.addSubItem(17, LocaleController.getString("SearchMembers", R.string.SearchMembers));
                    if (chat.creator && (this.chatInfo == null || this.chatInfo.participants.participants.size() > 0)) {
                        item.addSubItem(13, LocaleController.getString("ConvertGroupMenu", R.string.ConvertGroupMenu));
                    }
                    item.addSubItem(7, LocaleController.getString("DeleteAndExit", R.string.DeleteAndExit));
                }
            } else {
                item = menu.addItem(10, (int) R.drawable.ic_ab_other);
                item.addSubItem(8, LocaleController.getString("EditName", R.string.EditName));
            }
        }
        if (item == null) {
            item = menu.addItem(10, (int) R.drawable.ic_ab_other);
        }
        item.addSubItem(14, LocaleController.getString("AddShortcut", R.string.AddShortcut));
    }

    protected void onDialogDismiss(Dialog dialog) {
        if (this.listView != null) {
            this.listView.invalidateViews();
        }
    }

    public void didSelectDialogs(DialogsActivity fragment, ArrayList<Long> dids, CharSequence message, boolean param) {
        long did = ((Long) dids.get(0)).longValue();
        Bundle args = new Bundle();
        args.putBoolean("scrollToTopOnResume", true);
        int lower_part = (int) did;
        if (lower_part == 0) {
            args.putInt("enc_id", (int) (did >> 32));
        } else if (lower_part > 0) {
            args.putInt("user_id", lower_part);
        } else if (lower_part < 0) {
            args.putInt("chat_id", -lower_part);
        }
        if (MessagesController.getInstance(this.currentAccount).checkCanOpenChat(args, fragment)) {
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.closeChats);
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
            presentFragment(new ChatActivity(args), true);
            lambda$null$10$ProfileActivity();
            SendMessagesHelper.getInstance(this.currentAccount).sendMessage(MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.user_id)), did, null, null, null);
        }
    }

    public void onRequestPermissionsResultFragment(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 101) {
            User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.user_id));
            if (user != null) {
                if (grantResults.length <= 0 || grantResults[0] != 0) {
                    VoIPHelper.permissionDenied(getParentActivity(), null);
                } else {
                    VoIPHelper.startCall(user, getParentActivity(), this.userInfo);
                }
            }
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        ThemeDescriptionDelegate cellDelegate = new ProfileActivity$$Lambda$16(this);
        r10 = new ThemeDescription[54];
        r10[11] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider);
        r10[12] = new ThemeDescription(this.avatarImage, 0, null, null, new Drawable[]{Theme.avatar_broadcastDrawable, Theme.avatar_savedDrawable}, null, Theme.key_avatar_text);
        r10[13] = new ThemeDescription(this.avatarImage, 0, null, null, new Drawable[]{this.avatarDrawable}, null, Theme.key_avatar_backgroundInProfileBlue);
        r10[14] = new ThemeDescription(this.writeButton, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, Theme.key_profile_actionIcon);
        r10[15] = new ThemeDescription(this.writeButton, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_profile_actionBackground);
        r10[16] = new ThemeDescription(this.writeButton, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, Theme.key_profile_actionPressedBackground);
        r10[17] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r10[18] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteGreenText2);
        r10[19] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteRedText5);
        r10[20] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlueText2);
        r10[21] = new ThemeDescription(this.listView, 0, new Class[]{TextCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteValueText);
        r10[22] = new ThemeDescription(this.listView, 0, new Class[]{TextCell.class}, new String[]{"imageView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayIcon);
        r10[23] = new ThemeDescription(this.listView, 0, new Class[]{TextDetailCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r10[24] = new ThemeDescription(this.listView, 0, new Class[]{TextDetailCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText2);
        r10[25] = new ThemeDescription(this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlueHeader);
        r10[26] = new ThemeDescription(this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r10[27] = new ThemeDescription(this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText2);
        r10[28] = new ThemeDescription(this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchTrack);
        r10[29] = new ThemeDescription(this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_switchTrackChecked);
        r10[30] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG | ThemeDescription.FLAG_TEXTCOLOR, new Class[]{UserCell.class}, new String[]{"adminTextView"}, null, null, null, Theme.key_profile_creatorIcon);
        r10[31] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG | ThemeDescription.FLAG_TEXTCOLOR, new Class[]{UserCell.class}, new String[]{"adminTextView"}, null, null, null, Theme.key_profile_adminIcon);
        r10[32] = new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, new String[]{"imageView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayIcon);
        r10[33] = new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, new String[]{"nameTextView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r10[34] = new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, new String[]{"statusColor"}, null, null, cellDelegate, Theme.key_windowBackgroundWhiteGrayText);
        r10[35] = new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, new String[]{"statusOnlineColor"}, null, null, cellDelegate, Theme.key_windowBackgroundWhiteBlueText);
        r10[36] = new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, null, new Drawable[]{Theme.avatar_broadcastDrawable, Theme.avatar_savedDrawable}, null, Theme.key_avatar_text);
        r10[37] = new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundRed);
        r10[38] = new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundOrange);
        r10[39] = new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundViolet);
        r10[40] = new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundGreen);
        r10[41] = new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundCyan);
        r10[42] = new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundBlue);
        r10[43] = new ThemeDescription(null, 0, null, null, null, cellDelegate, Theme.key_avatar_backgroundPink);
        r10[44] = new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{AboutLinkCell.class}, Theme.profile_aboutTextPaint, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r10[45] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LINKCOLOR, new Class[]{AboutLinkCell.class}, Theme.profile_aboutTextPaint, null, null, Theme.key_windowBackgroundWhiteLinkText);
        r10[46] = new ThemeDescription(this.listView, 0, new Class[]{AboutLinkCell.class}, Theme.linkSelectionPaint, null, null, Theme.key_windowBackgroundWhiteLinkSelection);
        r10[47] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow);
        r10[48] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{ShadowSectionCell.class}, null, null, null, Theme.key_windowBackgroundGray);
        r10[49] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow);
        r10[50] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextInfoPrivacyCell.class}, null, null, null, Theme.key_windowBackgroundGray);
        r10[51] = new ThemeDescription(this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText4);
        r10[52] = new ThemeDescription(this.nameTextView[1], 0, null, null, new Drawable[]{Theme.profile_verifiedCheckDrawable}, null, Theme.key_profile_verifiedCheck);
        r10[53] = new ThemeDescription(this.nameTextView[1], 0, null, null, new Drawable[]{Theme.profile_verifiedDrawable}, null, Theme.key_profile_verifiedBackground);
        return r10;
    }

    final /* synthetic */ void lambda$getThemeDescriptions$23$ProfileActivity() {
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
