package org.telegram.ui;

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
import android.content.DialogInterface.OnClickListener;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.CountDownLatch;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.C0493R;
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
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.support.widget.RecyclerView.LayoutParams;
import org.telegram.messenger.support.widget.RecyclerView.OnScrollListener;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.messenger.support.widget.helper.ItemTouchHelper.Callback;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.BotInfo;
import org.telegram.tgnet.TLRPC.ChannelParticipant;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.ChatFull;
import org.telegram.tgnet.TLRPC.ChatParticipant;
import org.telegram.tgnet.TLRPC.EncryptedChat;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.InputFile;
import org.telegram.tgnet.TLRPC.PhotoSize;
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
import org.telegram.tgnet.TLRPC.TL_chatPhotoEmpty;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageActionSetMessageTTL;
import org.telegram.tgnet.TLRPC.TL_dialog;
import org.telegram.tgnet.TLRPC.TL_encryptedChat;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_messageEncryptedAction;
import org.telegram.tgnet.TLRPC.TL_peerNotifySettings;
import org.telegram.tgnet.TLRPC.TL_userEmpty;
import org.telegram.tgnet.TLRPC.TL_userFull;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BackDrawable;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate;
import org.telegram.ui.Cells.AboutLinkCell;
import org.telegram.ui.Cells.AboutLinkCell.AboutLinkCellDelegate;
import org.telegram.ui.Cells.DividerCell;
import org.telegram.ui.Cells.EmptyCell;
import org.telegram.ui.Cells.LoadingCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Cells.TextDetailCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.UserCell;
import org.telegram.ui.ChannelRightsEditActivity.ChannelRightsEditActivityDelegate;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.AvatarUpdater;
import org.telegram.ui.Components.AvatarUpdater.AvatarUpdaterDelegate;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.IdenticonDrawable;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;
import org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;
import org.telegram.ui.Components.voip.VoIPHelper;
import org.telegram.ui.ContactsActivity.ContactsActivityDelegate;
import org.telegram.ui.DialogsActivity.DialogsActivityDelegate;
import org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider;
import org.telegram.ui.PhotoViewer.PhotoViewerProvider;
import org.telegram.ui.PhotoViewer.PlaceProviderObject;

public class ProfileActivity extends BaseFragment implements NotificationCenterDelegate, DialogsActivityDelegate {
    private static final int add_contact = 1;
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
    private static final int search_members = 16;
    private static final int set_admins = 11;
    private static final int share = 10;
    private static final int share_contact = 3;
    private int addMemberRow;
    private boolean allowProfileAnimation = true;
    private ActionBarMenuItem animatingItem;
    private float animationProgress;
    private AvatarDrawable avatarDrawable;
    private BackupImageView avatarImage;
    private AvatarUpdater avatarUpdater;
    private int banFromGroup;
    private BotInfo botInfo;
    private ActionBarMenuItem callItem;
    private int channelInfoRow;
    private int channelNameRow;
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
    private int emptyRowChat;
    private int emptyRowChat2;
    private int extraHeight;
    private int groupsInCommonRow;
    private ChatFull info;
    private int initialAnimationExtraHeight;
    private boolean isBot;
    private LinearLayoutManager layoutManager;
    private int leaveChannelRow;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    private int loadMoreMembersRow;
    private boolean loadingUsers;
    private int membersEndRow;
    private int membersRow;
    private int membersSectionRow;
    private long mergeDialogId;
    private SimpleTextView[] nameTextView = new SimpleTextView[2];
    private int onlineCount = -1;
    private SimpleTextView[] onlineTextView = new SimpleTextView[2];
    private boolean openAnimationInProgress;
    private SparseArray<ChatParticipant> participantsMap = new SparseArray();
    private int phoneRow;
    private boolean playProfileAnimation;
    private PhotoViewerProvider provider = new C21741();
    private boolean recreateMenuAfterAnimation;
    private int rowCount = 0;
    private int sectionRow;
    private int selectedUser;
    private int settingsKeyRow;
    private int settingsNotificationsRow;
    private int settingsTimerRow;
    private int sharedMediaRow;
    private ArrayList<Integer> sortedUsers;
    private int startSecretChatRow;
    private TopView topView;
    private int totalMediaCount = -1;
    private int totalMediaCountMerge = -1;
    private boolean userBlocked;
    private int userInfoDetailedRow;
    private int userInfoRow;
    private int userSectionRow;
    private int user_id;
    private int usernameRow;
    private boolean usersEndReached;
    private ImageView writeButton;
    private AnimatorSet writeButtonAnimation;

    /* renamed from: org.telegram.ui.ProfileActivity$1 */
    class C21741 extends EmptyPhotoViewerProvider {
        C21741() {
        }

        public PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, FileLocation fileLocation, int index) {
            PlaceProviderObject placeProviderObject = null;
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
                    placeProviderObject = new PlaceProviderObject();
                    placeProviderObject.viewX = coords[0];
                    int i2 = coords[1];
                    if (VERSION.SDK_INT < 21) {
                        i = AndroidUtilities.statusBarHeight;
                    }
                    placeProviderObject.viewY = i2 - i;
                    placeProviderObject.parentView = ProfileActivity.this.avatarImage;
                    placeProviderObject.imageReceiver = ProfileActivity.this.avatarImage.getImageReceiver();
                    if (ProfileActivity.this.user_id != 0) {
                        placeProviderObject.dialogId = ProfileActivity.this.user_id;
                    } else if (ProfileActivity.this.chat_id != 0) {
                        placeProviderObject.dialogId = -ProfileActivity.this.chat_id;
                    }
                    placeProviderObject.thumb = placeProviderObject.imageReceiver.getBitmapSafe();
                    placeProviderObject.size = -1;
                    placeProviderObject.radius = ProfileActivity.this.avatarImage.getImageReceiver().getRoundRadius();
                    placeProviderObject.scale = ProfileActivity.this.avatarImage.getScaleX();
                }
            }
            return placeProviderObject;
        }

        public void willHidePhotoViewer() {
            ProfileActivity.this.avatarImage.getImageReceiver().setVisible(true, true);
        }
    }

    /* renamed from: org.telegram.ui.ProfileActivity$3 */
    class C21773 implements AvatarUpdaterDelegate {
        C21773() {
        }

        public void didUploadedPhoto(InputFile file, PhotoSize small, PhotoSize big) {
            if (ProfileActivity.this.chat_id != 0) {
                MessagesController.getInstance(ProfileActivity.this.currentAccount).changeChatAvatar(ProfileActivity.this.chat_id, file);
            }
        }
    }

    /* renamed from: org.telegram.ui.ProfileActivity$5 */
    class C21825 extends ActionBarMenuOnItemClick {

        /* renamed from: org.telegram.ui.ProfileActivity$5$1 */
        class C21791 implements OnClickListener {
            C21791() {
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                if (ProfileActivity.this.userBlocked) {
                    MessagesController.getInstance(ProfileActivity.this.currentAccount).unblockUser(ProfileActivity.this.user_id);
                } else {
                    MessagesController.getInstance(ProfileActivity.this.currentAccount).blockUser(ProfileActivity.this.user_id);
                }
            }
        }

        C21825() {
        }

        public void onItemClick(int id) {
            if (ProfileActivity.this.getParentActivity() != null) {
                if (id == -1) {
                    ProfileActivity.this.finishFragment();
                } else if (id == 2) {
                    if (MessagesController.getInstance(ProfileActivity.this.currentAccount).getUser(Integer.valueOf(ProfileActivity.this.user_id)) == null) {
                        return;
                    }
                    if (!ProfileActivity.this.isBot) {
                        builder = new Builder(ProfileActivity.this.getParentActivity());
                        if (ProfileActivity.this.userBlocked) {
                            builder.setMessage(LocaleController.getString("AreYouSureUnblockContact", C0493R.string.AreYouSureUnblockContact));
                        } else {
                            builder.setMessage(LocaleController.getString("AreYouSureBlockContact", C0493R.string.AreYouSureBlockContact));
                        }
                        builder.setTitle(LocaleController.getString("AppName", C0493R.string.AppName));
                        builder.setPositiveButton(LocaleController.getString("OK", C0493R.string.OK), new C21791());
                        builder.setNegativeButton(LocaleController.getString("Cancel", C0493R.string.Cancel), null);
                        ProfileActivity.this.showDialog(builder.create());
                    } else if (ProfileActivity.this.userBlocked) {
                        MessagesController.getInstance(ProfileActivity.this.currentAccount).unblockUser(ProfileActivity.this.user_id);
                        SendMessagesHelper.getInstance(ProfileActivity.this.currentAccount).sendMessage("/start", (long) ProfileActivity.this.user_id, null, null, false, null, null, null);
                        ProfileActivity.this.finishFragment();
                    } else {
                        MessagesController.getInstance(ProfileActivity.this.currentAccount).blockUser(ProfileActivity.this.user_id);
                    }
                } else if (id == 1) {
                    user = MessagesController.getInstance(ProfileActivity.this.currentAccount).getUser(Integer.valueOf(ProfileActivity.this.user_id));
                    args = new Bundle();
                    args.putInt("user_id", user.id);
                    args.putBoolean("addContact", true);
                    ProfileActivity.this.presentFragment(new ContactAddActivity(args));
                } else if (id == 3) {
                    args = new Bundle();
                    args.putBoolean("onlySelect", true);
                    args.putString("selectAlertString", LocaleController.getString("SendContactTo", C0493R.string.SendContactTo));
                    args.putString("selectAlertStringGroup", LocaleController.getString("SendContactToGroup", C0493R.string.SendContactToGroup));
                    r0 = new DialogsActivity(args);
                    r0.setDelegate(ProfileActivity.this);
                    ProfileActivity.this.presentFragment(r0);
                } else if (id == 4) {
                    args = new Bundle();
                    args.putInt("user_id", ProfileActivity.this.user_id);
                    ProfileActivity.this.presentFragment(new ContactAddActivity(args));
                } else if (id == 5) {
                    user = MessagesController.getInstance(ProfileActivity.this.currentAccount).getUser(Integer.valueOf(ProfileActivity.this.user_id));
                    if (user != null && ProfileActivity.this.getParentActivity() != null) {
                        builder = new Builder(ProfileActivity.this.getParentActivity());
                        builder.setMessage(LocaleController.getString("AreYouSureDeleteContact", C0493R.string.AreYouSureDeleteContact));
                        builder.setTitle(LocaleController.getString("AppName", C0493R.string.AppName));
                        r1 = user;
                        builder.setPositiveButton(LocaleController.getString("OK", C0493R.string.OK), new OnClickListener() {
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ArrayList<User> arrayList = new ArrayList();
                                arrayList.add(r1);
                                ContactsController.getInstance(ProfileActivity.this.currentAccount).deleteContact(arrayList);
                            }
                        });
                        builder.setNegativeButton(LocaleController.getString("Cancel", C0493R.string.Cancel), null);
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
                    r0 = new ChannelEditActivity(args);
                    r0.setInfo(ProfileActivity.this.info);
                    ProfileActivity.this.presentFragment(r0);
                } else if (id == 9) {
                    user = MessagesController.getInstance(ProfileActivity.this.currentAccount).getUser(Integer.valueOf(ProfileActivity.this.user_id));
                    if (user != null) {
                        args = new Bundle();
                        args.putBoolean("onlySelect", true);
                        args.putInt("dialogsType", 2);
                        args.putString("addToGroupAlertString", LocaleController.formatString("AddToTheGroupTitle", C0493R.string.AddToTheGroupTitle, UserObject.getUserName(user), "%1$s"));
                        r0 = new DialogsActivity(args);
                        r1 = user;
                        r0.setDelegate(new DialogsActivityDelegate() {
                            public void didSelectDialogs(DialogsActivity fragment, ArrayList<Long> dids, CharSequence message, boolean param) {
                                long did = ((Long) dids.get(0)).longValue();
                                Bundle args = new Bundle();
                                args.putBoolean("scrollToTopOnResume", true);
                                args.putInt("chat_id", -((int) did));
                                if (MessagesController.getInstance(ProfileActivity.this.currentAccount).checkCanOpenChat(args, fragment)) {
                                    NotificationCenter.getInstance(ProfileActivity.this.currentAccount).removeObserver(ProfileActivity.this, NotificationCenter.closeChats);
                                    NotificationCenter.getInstance(ProfileActivity.this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                                    MessagesController.getInstance(ProfileActivity.this.currentAccount).addUserToChat(-((int) did), r1, null, 0, null, ProfileActivity.this);
                                    ProfileActivity.this.presentFragment(new ChatActivity(args), true);
                                    ProfileActivity.this.removeSelfFromStack();
                                }
                            }
                        });
                        ProfileActivity.this.presentFragment(r0);
                    }
                } else if (id == 10) {
                    try {
                        if (MessagesController.getInstance(ProfileActivity.this.currentAccount).getUser(Integer.valueOf(ProfileActivity.this.user_id)) != null) {
                            Intent intent = new Intent("android.intent.action.SEND");
                            intent.setType("text/plain");
                            TL_userFull userFull = MessagesController.getInstance(ProfileActivity.this.currentAccount).getUserFull(ProfileActivity.this.botInfo.user_id);
                            if (ProfileActivity.this.botInfo == null || userFull == null || TextUtils.isEmpty(userFull.about)) {
                                intent = intent;
                                intent.putExtra("android.intent.extra.TEXT", String.format("https://" + MessagesController.getInstance(ProfileActivity.this.currentAccount).linkPrefix + "/%s", new Object[]{user.username}));
                            } else {
                                intent = intent;
                                intent.putExtra("android.intent.extra.TEXT", String.format("%s https://" + MessagesController.getInstance(ProfileActivity.this.currentAccount).linkPrefix + "/%s", new Object[]{userFull.about, user.username}));
                            }
                            ProfileActivity.this.startActivityForResult(Intent.createChooser(intent, LocaleController.getString("BotShare", C0493R.string.BotShare)), 500);
                        }
                    } catch (Throwable e) {
                        FileLog.m3e(e);
                    }
                } else if (id == 11) {
                    args = new Bundle();
                    args.putInt("chat_id", ProfileActivity.this.chat_id);
                    r0 = new SetAdminsActivity(args);
                    r0.setChatInfo(ProfileActivity.this.info);
                    ProfileActivity.this.presentFragment(r0);
                } else if (id == 13) {
                    args = new Bundle();
                    args.putInt("chat_id", ProfileActivity.this.chat_id);
                    ProfileActivity.this.presentFragment(new ConvertGroupActivity(args));
                } else if (id == 14) {
                    try {
                        long did;
                        if (ProfileActivity.this.currentEncryptedChat != null) {
                            did = ((long) ProfileActivity.this.currentEncryptedChat.id) << 32;
                        } else if (ProfileActivity.this.user_id != 0) {
                            did = (long) ProfileActivity.this.user_id;
                        } else if (ProfileActivity.this.chat_id != 0) {
                            did = (long) (-ProfileActivity.this.chat_id);
                        } else {
                            return;
                        }
                        DataQuery.getInstance(ProfileActivity.this.currentAccount).installShortcut(did);
                    } catch (Throwable e2) {
                        FileLog.m3e(e2);
                    }
                } else if (id == 15) {
                    user = MessagesController.getInstance(ProfileActivity.this.currentAccount).getUser(Integer.valueOf(ProfileActivity.this.user_id));
                    if (user != null) {
                        VoIPHelper.startCall(user, ProfileActivity.this.getParentActivity(), MessagesController.getInstance(ProfileActivity.this.currentAccount).getUserFull(user.id));
                    }
                } else if (id == 16) {
                    args = new Bundle();
                    args.putInt("chat_id", ProfileActivity.this.chat_id);
                    if (ChatObject.isChannel(ProfileActivity.this.currentChat)) {
                        args.putInt("type", 2);
                        args.putBoolean("open_search", true);
                        ProfileActivity.this.presentFragment(new ChannelUsersActivity(args));
                        return;
                    }
                    ChatUsersActivity chatUsersActivity = new ChatUsersActivity(args);
                    chatUsersActivity.setInfo(ProfileActivity.this.info);
                    ProfileActivity.this.presentFragment(chatUsersActivity);
                }
            }
        }
    }

    /* renamed from: org.telegram.ui.ProfileActivity$9 */
    class C21899 implements OnItemClickListener {

        /* renamed from: org.telegram.ui.ProfileActivity$9$2 */
        class C21872 implements OnClickListener {
            C21872() {
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                ProfileActivity.this.creatingChat = true;
                SecretChatHelper.getInstance(ProfileActivity.this.currentAccount).startSecretChat(ProfileActivity.this.getParentActivity(), MessagesController.getInstance(ProfileActivity.this.currentAccount).getUser(Integer.valueOf(ProfileActivity.this.user_id)));
            }
        }

        /* renamed from: org.telegram.ui.ProfileActivity$9$3 */
        class C21883 implements OnClickListener {
            C21883() {
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                MessagesController.getInstance(ProfileActivity.this.currentAccount).convertToMegaGroup(ProfileActivity.this.getParentActivity(), ProfileActivity.this.chat_id);
            }
        }

        C21899() {
        }

        public void onItemClick(View view, int position) {
            if (ProfileActivity.this.getParentActivity() != null) {
                Bundle args;
                if (position == ProfileActivity.this.sharedMediaRow) {
                    args = new Bundle();
                    if (ProfileActivity.this.user_id != 0) {
                        args.putLong("dialog_id", ProfileActivity.this.dialog_id != 0 ? ProfileActivity.this.dialog_id : (long) ProfileActivity.this.user_id);
                    } else {
                        args.putLong("dialog_id", (long) (-ProfileActivity.this.chat_id));
                    }
                    MediaActivity fragment = new MediaActivity(args);
                    fragment.setChatInfo(ProfileActivity.this.info);
                    ProfileActivity.this.presentFragment(fragment);
                } else if (position == ProfileActivity.this.groupsInCommonRow) {
                    ProfileActivity.this.presentFragment(new CommonGroupsActivity(ProfileActivity.this.user_id));
                } else if (position == ProfileActivity.this.settingsKeyRow) {
                    args = new Bundle();
                    args.putInt("chat_id", (int) (ProfileActivity.this.dialog_id >> 32));
                    ProfileActivity.this.presentFragment(new IdenticonActivity(args));
                } else if (position == ProfileActivity.this.settingsTimerRow) {
                    ProfileActivity.this.showDialog(AlertsCreator.createTTLAlert(ProfileActivity.this.getParentActivity(), ProfileActivity.this.currentEncryptedChat).create());
                } else if (position == ProfileActivity.this.settingsNotificationsRow) {
                    long did;
                    if (ProfileActivity.this.dialog_id != 0) {
                        did = ProfileActivity.this.dialog_id;
                    } else if (ProfileActivity.this.user_id != 0) {
                        did = (long) ProfileActivity.this.user_id;
                    } else {
                        did = (long) (-ProfileActivity.this.chat_id);
                    }
                    String[] descriptions = new String[5];
                    descriptions[0] = LocaleController.getString("NotificationsTurnOn", C0493R.string.NotificationsTurnOn);
                    descriptions[1] = LocaleController.formatString("MuteFor", C0493R.string.MuteFor, LocaleController.formatPluralString("Hours", 1));
                    descriptions[2] = LocaleController.formatString("MuteFor", C0493R.string.MuteFor, LocaleController.formatPluralString("Days", 2));
                    descriptions[3] = LocaleController.getString("NotificationsCustomize", C0493R.string.NotificationsCustomize);
                    descriptions[4] = LocaleController.getString("NotificationsTurnOff", C0493R.string.NotificationsTurnOff);
                    int i = 5;
                    int[] icons = new int[]{C0493R.drawable.notifications_s_on, C0493R.drawable.notifications_s_1h, C0493R.drawable.notifications_s_2d, C0493R.drawable.notifications_s_custom, C0493R.drawable.notifications_s_off};
                    View linearLayout = new LinearLayout(ProfileActivity.this.getParentActivity());
                    linearLayout.setOrientation(1);
                    for (int a = 0; a < descriptions.length; a++) {
                        linearLayout = new TextView(ProfileActivity.this.getParentActivity());
                        linearLayout.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
                        linearLayout.setTextSize(1, 16.0f);
                        linearLayout.setLines(1);
                        linearLayout.setMaxLines(1);
                        Drawable drawable = ProfileActivity.this.getParentActivity().getResources().getDrawable(icons[a]);
                        drawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_dialogIcon), Mode.MULTIPLY));
                        linearLayout.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
                        linearLayout.setTag(Integer.valueOf(a));
                        linearLayout.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                        linearLayout.setPadding(AndroidUtilities.dp(24.0f), 0, AndroidUtilities.dp(24.0f), 0);
                        linearLayout.setSingleLine(true);
                        linearLayout.setGravity(19);
                        linearLayout.setCompoundDrawablePadding(AndroidUtilities.dp(26.0f));
                        linearLayout.setText(descriptions[a]);
                        linearLayout.addView(linearLayout, LayoutHelper.createLinear(-1, 48, 51));
                        linearLayout.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                int i = ((Integer) v.getTag()).intValue();
                                Editor editor;
                                TL_dialog dialog;
                                if (i == 0) {
                                    editor = MessagesController.getNotificationsSettings(ProfileActivity.this.currentAccount).edit();
                                    editor.putInt("notify2_" + did, 0);
                                    MessagesStorage.getInstance(ProfileActivity.this.currentAccount).setDialogFlags(did, 0);
                                    editor.commit();
                                    dialog = (TL_dialog) MessagesController.getInstance(ProfileActivity.this.currentAccount).dialogs_dict.get(did);
                                    if (dialog != null) {
                                        dialog.notify_settings = new TL_peerNotifySettings();
                                    }
                                    NotificationsController.getInstance(ProfileActivity.this.currentAccount).updateServerNotificationsSettings(did);
                                } else if (i == 3) {
                                    Bundle args = new Bundle();
                                    args.putLong("dialog_id", did);
                                    ProfileActivity.this.presentFragment(new ProfileNotificationsActivity(args));
                                } else {
                                    long flags;
                                    int untilTime = ConnectionsManager.getInstance(ProfileActivity.this.currentAccount).getCurrentTime();
                                    if (i == 1) {
                                        untilTime += 3600;
                                    } else if (i == 2) {
                                        untilTime += 172800;
                                    } else if (i == 4) {
                                        untilTime = ConnectionsManager.DEFAULT_DATACENTER_ID;
                                    }
                                    editor = MessagesController.getNotificationsSettings(ProfileActivity.this.currentAccount).edit();
                                    if (i == 4) {
                                        editor.putInt("notify2_" + did, 2);
                                        flags = 1;
                                    } else {
                                        editor.putInt("notify2_" + did, 3);
                                        editor.putInt("notifyuntil_" + did, untilTime);
                                        flags = (((long) untilTime) << 32) | 1;
                                    }
                                    NotificationsController.getInstance(ProfileActivity.this.currentAccount).removeNotificationsForDialog(did);
                                    MessagesStorage.getInstance(ProfileActivity.this.currentAccount).setDialogFlags(did, flags);
                                    editor.commit();
                                    dialog = (TL_dialog) MessagesController.getInstance(ProfileActivity.this.currentAccount).dialogs_dict.get(did);
                                    if (dialog != null) {
                                        dialog.notify_settings = new TL_peerNotifySettings();
                                        dialog.notify_settings.mute_until = untilTime;
                                    }
                                    NotificationsController.getInstance(ProfileActivity.this.currentAccount).updateServerNotificationsSettings(did);
                                }
                                ProfileActivity.this.listAdapter.notifyItemChanged(ProfileActivity.this.settingsNotificationsRow);
                                ProfileActivity.this.dismissCurrentDialig();
                            }
                        });
                    }
                    builder = new Builder(ProfileActivity.this.getParentActivity());
                    builder.setTitle(LocaleController.getString("Notifications", C0493R.string.Notifications));
                    builder.setView(linearLayout);
                    ProfileActivity.this.showDialog(builder.create());
                } else if (position == ProfileActivity.this.startSecretChatRow) {
                    builder = new Builder(ProfileActivity.this.getParentActivity());
                    builder.setMessage(LocaleController.getString("AreYouSureSecretChat", C0493R.string.AreYouSureSecretChat));
                    builder.setTitle(LocaleController.getString("AppName", C0493R.string.AppName));
                    builder.setPositiveButton(LocaleController.getString("OK", C0493R.string.OK), new C21872());
                    builder.setNegativeButton(LocaleController.getString("Cancel", C0493R.string.Cancel), null);
                    ProfileActivity.this.showDialog(builder.create());
                } else if (position > ProfileActivity.this.emptyRowChat2 && position < ProfileActivity.this.membersEndRow) {
                    int user_id;
                    if (ProfileActivity.this.sortedUsers.isEmpty()) {
                        user_id = ((ChatParticipant) ProfileActivity.this.info.participants.participants.get((position - ProfileActivity.this.emptyRowChat2) - 1)).user_id;
                    } else {
                        user_id = ((ChatParticipant) ProfileActivity.this.info.participants.participants.get(((Integer) ProfileActivity.this.sortedUsers.get((position - ProfileActivity.this.emptyRowChat2) - 1)).intValue())).user_id;
                    }
                    if (user_id != UserConfig.getInstance(ProfileActivity.this.currentAccount).getClientUserId()) {
                        args = new Bundle();
                        args.putInt("user_id", user_id);
                        ProfileActivity.this.presentFragment(new ProfileActivity(args));
                    }
                } else if (position == ProfileActivity.this.addMemberRow) {
                    ProfileActivity.this.openAddMember();
                } else if (position == ProfileActivity.this.channelNameRow) {
                    try {
                        Intent intent = new Intent("android.intent.action.SEND");
                        intent.setType("text/plain");
                        if (ProfileActivity.this.info.about == null || ProfileActivity.this.info.about.length() <= 0) {
                            intent.putExtra("android.intent.extra.TEXT", ProfileActivity.this.currentChat.title + "\nhttps://" + MessagesController.getInstance(ProfileActivity.this.currentAccount).linkPrefix + "/" + ProfileActivity.this.currentChat.username);
                        } else {
                            intent.putExtra("android.intent.extra.TEXT", ProfileActivity.this.currentChat.title + "\n" + ProfileActivity.this.info.about + "\nhttps://" + MessagesController.getInstance(ProfileActivity.this.currentAccount).linkPrefix + "/" + ProfileActivity.this.currentChat.username);
                        }
                        ProfileActivity.this.getParentActivity().startActivityForResult(Intent.createChooser(intent, LocaleController.getString("BotShare", C0493R.string.BotShare)), 500);
                    } catch (Throwable e) {
                        FileLog.m3e(e);
                    }
                } else if (position == ProfileActivity.this.leaveChannelRow) {
                    ProfileActivity.this.leaveChatPressed();
                } else if (position == ProfileActivity.this.membersRow) {
                    args = new Bundle();
                    args.putInt("chat_id", ProfileActivity.this.chat_id);
                    args.putInt("type", 2);
                    ProfileActivity.this.presentFragment(new ChannelUsersActivity(args));
                } else if (position == ProfileActivity.this.convertRow) {
                    builder = new Builder(ProfileActivity.this.getParentActivity());
                    builder.setMessage(LocaleController.getString("ConvertGroupAlert", C0493R.string.ConvertGroupAlert));
                    builder.setTitle(LocaleController.getString("ConvertGroupAlertWarning", C0493R.string.ConvertGroupAlertWarning));
                    builder.setPositiveButton(LocaleController.getString("OK", C0493R.string.OK), new C21883());
                    builder.setNegativeButton(LocaleController.getString("Cancel", C0493R.string.Cancel), null);
                    ProfileActivity.this.showDialog(builder.create());
                } else {
                    ProfileActivity.this.processOnClickOrPress(position);
                }
            }
        }
    }

    private class ListAdapter extends SelectionAdapter {
        private Context mContext;

        /* renamed from: org.telegram.ui.ProfileActivity$ListAdapter$1 */
        class C21901 implements AboutLinkCellDelegate {
            C21901() {
            }

            public void didPressUrl(String url) {
                if (url.startsWith("@")) {
                    MessagesController.getInstance(ProfileActivity.this.currentAccount).openByUserName(url.substring(1), ProfileActivity.this, 0);
                } else if (url.startsWith("#")) {
                    DialogsActivity fragment = new DialogsActivity(null);
                    fragment.setSearchString(url);
                    ProfileActivity.this.presentFragment(fragment);
                } else if (url.startsWith("/") && ProfileActivity.this.parentLayout.fragmentsStack.size() > 1) {
                    BaseFragment previousFragment = (BaseFragment) ProfileActivity.this.parentLayout.fragmentsStack.get(ProfileActivity.this.parentLayout.fragmentsStack.size() - 2);
                    if (previousFragment instanceof ChatActivity) {
                        ProfileActivity.this.finishFragment();
                        ((ChatActivity) previousFragment).chatActivityEnterView.setCommand(null, url, false, false);
                    }
                }
            }
        }

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = null;
            CombinedDrawable combinedDrawable;
            switch (viewType) {
                case 0:
                    view = new EmptyCell(this.mContext);
                    break;
                case 1:
                    view = new DividerCell(this.mContext);
                    view.setPadding(AndroidUtilities.dp(72.0f), 0, 0, 0);
                    break;
                case 2:
                    view = new TextDetailCell(this.mContext);
                    break;
                case 3:
                    view = new TextCell(this.mContext);
                    break;
                case 4:
                    view = new UserCell(this.mContext, 61, 0, true);
                    break;
                case 5:
                    view = new ShadowSectionCell(this.mContext);
                    combinedDrawable = new CombinedDrawable(new ColorDrawable(Theme.getColor(Theme.key_windowBackgroundGray)), Theme.getThemedDrawable(this.mContext, C0493R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                    combinedDrawable.setFullsize(true);
                    view.setBackgroundDrawable(combinedDrawable);
                    break;
                case 6:
                    view = new TextInfoPrivacyCell(this.mContext);
                    TextInfoPrivacyCell cell = (TextInfoPrivacyCell) view;
                    combinedDrawable = new CombinedDrawable(new ColorDrawable(Theme.getColor(Theme.key_windowBackgroundGray)), Theme.getThemedDrawable(this.mContext, C0493R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                    combinedDrawable.setFullsize(true);
                    cell.setBackgroundDrawable(combinedDrawable);
                    cell.setText(AndroidUtilities.replaceTags(LocaleController.formatString("ConvertGroupInfo", C0493R.string.ConvertGroupInfo, LocaleController.formatPluralString("Members", MessagesController.getInstance(ProfileActivity.this.currentAccount).maxMegagroupCount))));
                    break;
                case 7:
                    view = new LoadingCell(this.mContext);
                    break;
                case 8:
                    view = new AboutLinkCell(this.mContext);
                    ((AboutLinkCell) view).setDelegate(new C21901());
                    break;
            }
            view.setLayoutParams(new LayoutParams(-1, -2));
            return new Holder(view);
        }

        public void onBindViewHolder(ViewHolder holder, int i) {
            String text;
            TL_userFull userFull;
            switch (holder.getItemViewType()) {
                case 0:
                    if (i == ProfileActivity.this.emptyRowChat || i == ProfileActivity.this.emptyRowChat2) {
                        ((EmptyCell) holder.itemView).setHeight(AndroidUtilities.dp(8.0f));
                        return;
                    } else {
                        ((EmptyCell) holder.itemView).setHeight(AndroidUtilities.dp(36.0f));
                        return;
                    }
                case 2:
                    TextDetailCell textDetailCell = (TextDetailCell) holder.itemView;
                    textDetailCell.setMultiline(false);
                    User user;
                    if (i == ProfileActivity.this.phoneRow) {
                        user = MessagesController.getInstance(ProfileActivity.this.currentAccount).getUser(Integer.valueOf(ProfileActivity.this.user_id));
                        if (user.phone == null || user.phone.length() == 0) {
                            text = LocaleController.getString("NumberUnknown", C0493R.string.NumberUnknown);
                        } else {
                            text = PhoneFormat.getInstance().format("+" + user.phone);
                        }
                        textDetailCell.setTextAndValueAndIcon(text, LocaleController.getString("PhoneMobile", C0493R.string.PhoneMobile), C0493R.drawable.profile_phone, 0);
                        return;
                    } else if (i == ProfileActivity.this.usernameRow) {
                        user = MessagesController.getInstance(ProfileActivity.this.currentAccount).getUser(Integer.valueOf(ProfileActivity.this.user_id));
                        if (user == null || TextUtils.isEmpty(user.username)) {
                            text = "-";
                        } else {
                            text = "@" + user.username;
                        }
                        if (ProfileActivity.this.phoneRow == -1 && ProfileActivity.this.userInfoRow == -1 && ProfileActivity.this.userInfoDetailedRow == -1) {
                            textDetailCell.setTextAndValueAndIcon(text, LocaleController.getString("Username", C0493R.string.Username), C0493R.drawable.profile_info, 11);
                            return;
                        } else {
                            textDetailCell.setTextAndValue(text, LocaleController.getString("Username", C0493R.string.Username));
                            return;
                        }
                    } else if (i == ProfileActivity.this.channelNameRow) {
                        if (ProfileActivity.this.currentChat == null || TextUtils.isEmpty(ProfileActivity.this.currentChat.username)) {
                            text = "-";
                        } else {
                            text = "@" + ProfileActivity.this.currentChat.username;
                        }
                        textDetailCell.setTextAndValue(text, MessagesController.getInstance(ProfileActivity.this.currentAccount).linkPrefix + "/" + ProfileActivity.this.currentChat.username);
                        return;
                    } else if (i == ProfileActivity.this.userInfoDetailedRow) {
                        userFull = MessagesController.getInstance(ProfileActivity.this.currentAccount).getUserFull(ProfileActivity.this.user_id);
                        textDetailCell.setMultiline(true);
                        textDetailCell.setTextAndValueAndIcon(userFull != null ? userFull.about : null, LocaleController.getString("UserBio", C0493R.string.UserBio), C0493R.drawable.profile_info, 11);
                        return;
                    } else {
                        return;
                    }
                case 3:
                    TextCell textCell = (TextCell) holder.itemView;
                    textCell.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
                    textCell.setTag(Theme.key_windowBackgroundWhiteBlackText);
                    String value;
                    String str;
                    if (i == ProfileActivity.this.sharedMediaRow) {
                        if (ProfileActivity.this.totalMediaCount == -1) {
                            value = LocaleController.getString("Loading", C0493R.string.Loading);
                        } else {
                            str = "%d";
                            Object[] objArr = new Object[1];
                            objArr[0] = Integer.valueOf((ProfileActivity.this.totalMediaCountMerge != -1 ? ProfileActivity.this.totalMediaCountMerge : 0) + ProfileActivity.this.totalMediaCount);
                            value = String.format(str, objArr);
                        }
                        if (ProfileActivity.this.user_id == 0 || UserConfig.getInstance(ProfileActivity.this.currentAccount).getClientUserId() != ProfileActivity.this.user_id) {
                            textCell.setTextAndValue(LocaleController.getString("SharedMedia", C0493R.string.SharedMedia), value);
                            return;
                        } else {
                            textCell.setTextAndValueAndIcon(LocaleController.getString("SharedMedia", C0493R.string.SharedMedia), value, C0493R.drawable.profile_list);
                            return;
                        }
                    } else if (i == ProfileActivity.this.groupsInCommonRow) {
                        userFull = MessagesController.getInstance(ProfileActivity.this.currentAccount).getUserFull(ProfileActivity.this.user_id);
                        str = LocaleController.getString("GroupsInCommon", C0493R.string.GroupsInCommon);
                        String str2 = "%d";
                        Object[] objArr2 = new Object[1];
                        objArr2[0] = Integer.valueOf(userFull != null ? userFull.common_chats_count : 0);
                        textCell.setTextAndValue(str, String.format(str2, objArr2));
                        return;
                    } else if (i == ProfileActivity.this.settingsTimerRow) {
                        EncryptedChat encryptedChat = MessagesController.getInstance(ProfileActivity.this.currentAccount).getEncryptedChat(Integer.valueOf((int) (ProfileActivity.this.dialog_id >> 32)));
                        if (encryptedChat.ttl == 0) {
                            value = LocaleController.getString("ShortMessageLifetimeForever", C0493R.string.ShortMessageLifetimeForever);
                        } else {
                            value = LocaleController.formatTTLString(encryptedChat.ttl);
                        }
                        textCell.setTextAndValue(LocaleController.getString("MessageLifetime", C0493R.string.MessageLifetime), value);
                        return;
                    } else if (i == ProfileActivity.this.settingsNotificationsRow) {
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
                        boolean custom = preferences.getBoolean("custom_" + did, false);
                        boolean hasOverride = preferences.contains("notify2_" + did);
                        int value2 = preferences.getInt("notify2_" + did, 0);
                        int delta = preferences.getInt("notifyuntil_" + did, 0);
                        if (value2 != 3 || delta == Integer.MAX_VALUE) {
                            boolean enabled;
                            if (value2 == 0) {
                                if (hasOverride) {
                                    enabled = true;
                                } else if (((int) did) < 0) {
                                    enabled = preferences.getBoolean("EnableGroup", true);
                                } else {
                                    enabled = preferences.getBoolean("EnableAll", true);
                                }
                            } else if (value2 == 1) {
                                enabled = true;
                            } else if (value2 == 2) {
                                enabled = false;
                            } else {
                                enabled = false;
                            }
                            if (enabled && custom) {
                                val = LocaleController.getString("NotificationsCustom", C0493R.string.NotificationsCustom);
                            } else {
                                val = enabled ? LocaleController.getString("NotificationsOn", C0493R.string.NotificationsOn) : LocaleController.getString("NotificationsOff", C0493R.string.NotificationsOff);
                            }
                        } else {
                            delta -= ConnectionsManager.getInstance(ProfileActivity.this.currentAccount).getCurrentTime();
                            if (delta <= 0) {
                                if (custom) {
                                    val = LocaleController.getString("NotificationsCustom", C0493R.string.NotificationsCustom);
                                } else {
                                    val = LocaleController.getString("NotificationsOn", C0493R.string.NotificationsOn);
                                }
                            } else if (delta < 3600) {
                                val = LocaleController.formatString("WillUnmuteIn", C0493R.string.WillUnmuteIn, LocaleController.formatPluralString("Minutes", delta / 60));
                            } else if (delta < 86400) {
                                val = LocaleController.formatString("WillUnmuteIn", C0493R.string.WillUnmuteIn, LocaleController.formatPluralString("Hours", (int) Math.ceil((double) ((((float) delta) / 60.0f) / 60.0f))));
                            } else if (delta < 31536000) {
                                val = LocaleController.formatString("WillUnmuteIn", C0493R.string.WillUnmuteIn, LocaleController.formatPluralString("Days", (int) Math.ceil((double) (((((float) delta) / 60.0f) / 60.0f) / 24.0f))));
                            } else {
                                val = null;
                            }
                        }
                        if (val != null) {
                            textCell.setTextAndValueAndIcon(LocaleController.getString("Notifications", C0493R.string.Notifications), val, C0493R.drawable.profile_list);
                            return;
                        } else {
                            textCell.setTextAndValueAndIcon(LocaleController.getString("Notifications", C0493R.string.Notifications), LocaleController.getString("NotificationsOff", C0493R.string.NotificationsOff), C0493R.drawable.profile_list);
                            return;
                        }
                    } else if (i == ProfileActivity.this.startSecretChatRow) {
                        textCell.setText(LocaleController.getString("StartEncryptedChat", C0493R.string.StartEncryptedChat));
                        textCell.setTag(Theme.key_windowBackgroundWhiteGreenText2);
                        textCell.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGreenText2));
                        return;
                    } else if (i == ProfileActivity.this.settingsKeyRow) {
                        Drawable identiconDrawable = new IdenticonDrawable();
                        identiconDrawable.setEncryptedChat(MessagesController.getInstance(ProfileActivity.this.currentAccount).getEncryptedChat(Integer.valueOf((int) (ProfileActivity.this.dialog_id >> 32))));
                        textCell.setTextAndValueDrawable(LocaleController.getString("EncryptionKey", C0493R.string.EncryptionKey), identiconDrawable);
                        return;
                    } else if (i == ProfileActivity.this.leaveChannelRow) {
                        textCell.setTag(Theme.key_windowBackgroundWhiteRedText5);
                        textCell.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText5));
                        textCell.setText(LocaleController.getString("LeaveChannel", C0493R.string.LeaveChannel));
                        return;
                    } else if (i == ProfileActivity.this.convertRow) {
                        textCell.setText(LocaleController.getString("UpgradeGroup", C0493R.string.UpgradeGroup));
                        textCell.setTag(Theme.key_windowBackgroundWhiteGreenText2);
                        textCell.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGreenText2));
                        return;
                    } else if (i == ProfileActivity.this.addMemberRow) {
                        if (ProfileActivity.this.chat_id > 0) {
                            textCell.setText(LocaleController.getString("AddMember", C0493R.string.AddMember));
                            return;
                        } else {
                            textCell.setText(LocaleController.getString("AddRecipient", C0493R.string.AddRecipient));
                            return;
                        }
                    } else if (i != ProfileActivity.this.membersRow) {
                        return;
                    } else {
                        if (ProfileActivity.this.info != null) {
                            if (!ChatObject.isChannel(ProfileActivity.this.currentChat) || ProfileActivity.this.currentChat.megagroup) {
                                textCell.setTextAndValue(LocaleController.getString("ChannelMembers", C0493R.string.ChannelMembers), String.format("%d", new Object[]{Integer.valueOf(ProfileActivity.this.info.participants_count)}));
                                return;
                            } else {
                                textCell.setTextAndValue(LocaleController.getString("ChannelSubscribers", C0493R.string.ChannelSubscribers), String.format("%d", new Object[]{Integer.valueOf(ProfileActivity.this.info.participants_count)}));
                                return;
                            }
                        } else if (!ChatObject.isChannel(ProfileActivity.this.currentChat) || ProfileActivity.this.currentChat.megagroup) {
                            textCell.setText(LocaleController.getString("ChannelMembers", C0493R.string.ChannelMembers));
                            return;
                        } else {
                            textCell.setText(LocaleController.getString("ChannelSubscribers", C0493R.string.ChannelSubscribers));
                            return;
                        }
                    }
                case 4:
                    ChatParticipant part;
                    UserCell userCell = (UserCell) holder.itemView;
                    if (ProfileActivity.this.sortedUsers.isEmpty()) {
                        part = (ChatParticipant) ProfileActivity.this.info.participants.participants.get((i - ProfileActivity.this.emptyRowChat2) - 1);
                    } else {
                        part = (ChatParticipant) ProfileActivity.this.info.participants.participants.get(((Integer) ProfileActivity.this.sortedUsers.get((i - ProfileActivity.this.emptyRowChat2) - 1)).intValue());
                    }
                    if (part != null) {
                        int i2;
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
                        TLObject user2 = MessagesController.getInstance(ProfileActivity.this.currentAccount).getUser(Integer.valueOf(part.user_id));
                        if (i == ProfileActivity.this.emptyRowChat2 + 1) {
                            i2 = C0493R.drawable.menu_newgroup;
                        } else {
                            i2 = 0;
                        }
                        userCell.setData(user2, null, null, i2);
                        return;
                    }
                    return;
                case 8:
                    AboutLinkCell aboutLinkCell = holder.itemView;
                    if (i == ProfileActivity.this.userInfoRow) {
                        userFull = MessagesController.getInstance(ProfileActivity.this.currentAccount).getUserFull(ProfileActivity.this.user_id);
                        aboutLinkCell.setTextAndIcon(userFull != null ? userFull.about : null, C0493R.drawable.profile_info, ProfileActivity.this.isBot);
                        return;
                    } else if (i == ProfileActivity.this.channelInfoRow) {
                        text = ProfileActivity.this.info.about;
                        while (text.contains("\n\n\n")) {
                            text = text.replace("\n\n\n", "\n\n");
                        }
                        aboutLinkCell.setTextAndIcon(text, C0493R.drawable.profile_info, true);
                        return;
                    } else {
                        return;
                    }
                default:
                    return;
            }
        }

        public boolean isEnabled(ViewHolder holder) {
            int i = holder.getAdapterPosition();
            if (ProfileActivity.this.user_id != 0) {
                if (i == ProfileActivity.this.phoneRow || i == ProfileActivity.this.settingsTimerRow || i == ProfileActivity.this.settingsKeyRow || i == ProfileActivity.this.settingsNotificationsRow || i == ProfileActivity.this.sharedMediaRow || i == ProfileActivity.this.startSecretChatRow || i == ProfileActivity.this.usernameRow || i == ProfileActivity.this.userInfoRow || i == ProfileActivity.this.groupsInCommonRow || i == ProfileActivity.this.userInfoDetailedRow) {
                    return true;
                }
                return false;
            } else if (ProfileActivity.this.chat_id == 0) {
                return false;
            } else {
                if (i == ProfileActivity.this.convertRow || i == ProfileActivity.this.settingsNotificationsRow || i == ProfileActivity.this.sharedMediaRow || ((i > ProfileActivity.this.emptyRowChat2 && i < ProfileActivity.this.membersEndRow) || i == ProfileActivity.this.addMemberRow || i == ProfileActivity.this.channelNameRow || i == ProfileActivity.this.leaveChannelRow || i == ProfileActivity.this.channelInfoRow || i == ProfileActivity.this.membersRow)) {
                    return true;
                }
                return false;
            }
        }

        public int getItemCount() {
            return ProfileActivity.this.rowCount;
        }

        public int getItemViewType(int i) {
            if (i == ProfileActivity.this.emptyRow || i == ProfileActivity.this.emptyRowChat || i == ProfileActivity.this.emptyRowChat2) {
                return 0;
            }
            if (i == ProfileActivity.this.sectionRow || i == ProfileActivity.this.userSectionRow) {
                return 1;
            }
            if (i == ProfileActivity.this.phoneRow || i == ProfileActivity.this.usernameRow || i == ProfileActivity.this.channelNameRow || i == ProfileActivity.this.userInfoDetailedRow) {
                return 2;
            }
            if (i == ProfileActivity.this.leaveChannelRow || i == ProfileActivity.this.sharedMediaRow || i == ProfileActivity.this.settingsTimerRow || i == ProfileActivity.this.settingsNotificationsRow || i == ProfileActivity.this.startSecretChatRow || i == ProfileActivity.this.settingsKeyRow || i == ProfileActivity.this.convertRow || i == ProfileActivity.this.addMemberRow || i == ProfileActivity.this.groupsInCommonRow || i == ProfileActivity.this.membersRow) {
                return 3;
            }
            if (i > ProfileActivity.this.emptyRowChat2 && i < ProfileActivity.this.membersEndRow) {
                return 4;
            }
            if (i == ProfileActivity.this.membersSectionRow) {
                return 5;
            }
            if (i == ProfileActivity.this.convertHelpRow) {
                return 6;
            }
            if (i == ProfileActivity.this.loadMoreMembersRow) {
                return 7;
            }
            if (i == ProfileActivity.this.userInfoRow || i == ProfileActivity.this.channelInfoRow) {
                return 8;
            }
            return 0;
        }
    }

    private class TopView extends View {
        private int currentColor;
        private Paint paint = new Paint();

        public TopView(Context context) {
            super(context);
        }

        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), ((ProfileActivity.this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0) + ActionBar.getCurrentActionBarHeight()) + AndroidUtilities.dp(91.0f));
        }

        public void setBackgroundColor(int color) {
            if (color != this.currentColor) {
                this.paint.setColor(color);
                invalidate();
            }
        }

        protected void onDraw(Canvas canvas) {
            int height = getMeasuredHeight() - AndroidUtilities.dp(91.0f);
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
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.updateInterfaces);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.contactsDidLoaded);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.encryptedChatCreated);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.encryptedChatUpdated);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.blockedUsersDidLoaded);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.botInfoDidLoaded);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.userInfoDidLoaded);
            if (this.currentEncryptedChat != null) {
                NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.didReceivedNewMessages);
            }
            this.userBlocked = MessagesController.getInstance(this.currentAccount).blockedUsers.contains(Integer.valueOf(this.user_id));
            if (user.bot) {
                this.isBot = true;
                DataQuery.getInstance(this.currentAccount).loadBotInfo(user.id, true, this.classGuid);
            }
            MessagesController.getInstance(this.currentAccount).loadFullUser(MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.user_id)), this.classGuid, true);
            this.participantsMap = null;
        } else if (this.chat_id == 0) {
            return false;
        } else {
            this.currentChat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.chat_id));
            if (this.currentChat == null) {
                final CountDownLatch countDownLatch = new CountDownLatch(1);
                MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable() {
                    public void run() {
                        ProfileActivity.this.currentChat = MessagesStorage.getInstance(ProfileActivity.this.currentAccount).getChat(ProfileActivity.this.chat_id);
                        countDownLatch.countDown();
                    }
                });
                try {
                    countDownLatch.await();
                } catch (Throwable e) {
                    FileLog.m3e(e);
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
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.chatInfoDidLoaded);
            this.sortedUsers = new ArrayList();
            updateOnlineCount();
            this.avatarUpdater = new AvatarUpdater();
            this.avatarUpdater.delegate = new C21773();
            this.avatarUpdater.parentFragment = this;
            if (ChatObject.isChannel(this.currentChat)) {
                MessagesController.getInstance(this.currentAccount).loadFullChat(this.chat_id, this.classGuid, true);
            }
        }
        if (this.dialog_id != 0) {
            DataQuery.getInstance(this.currentAccount).getMediaCount(this.dialog_id, 0, this.classGuid, true);
        } else if (this.user_id != 0) {
            DataQuery.getInstance(this.currentAccount).getMediaCount((long) this.user_id, 0, this.classGuid, true);
        } else if (this.chat_id > 0) {
            DataQuery.getInstance(this.currentAccount).getMediaCount((long) (-this.chat_id), 0, this.classGuid, true);
            if (this.mergeDialogId != 0) {
                DataQuery.getInstance(this.currentAccount).getMediaCount(this.mergeDialogId, 0, this.classGuid, true);
            }
        }
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.mediaCountDidLoaded);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.updateInterfaces);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.closeChats);
        updateRowsIds();
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.mediaCountDidLoaded);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.updateInterfaces);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.closeChats);
        if (this.user_id != 0) {
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.contactsDidLoaded);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.encryptedChatCreated);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.encryptedChatUpdated);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.blockedUsersDidLoaded);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.botInfoDidLoaded);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.userInfoDidLoaded);
            MessagesController.getInstance(this.currentAccount).cancelLoadFullUser(this.user_id);
            if (this.currentEncryptedChat != null) {
                NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.didReceivedNewMessages);
            }
        } else if (this.chat_id != 0) {
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.chatInfoDidLoaded);
            this.avatarUpdater.clear();
        }
    }

    protected ActionBar createActionBar(Context context) {
        boolean z;
        ActionBar actionBar = new ActionBar(context) {
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
        this.extraHeight = AndroidUtilities.dp(88.0f);
        this.actionBar.setActionBarMenuOnItemClick(new C21825());
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
            public boolean hasOverlappingRendering() {
                return false;
            }
        };
        this.listView.setTag(Integer.valueOf(6));
        this.listView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
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
        this.listView.setOnItemClickListener(new C21899());
        this.listView.setOnItemLongClickListener(new OnItemLongClickListener() {
            public boolean onItemClick(View view, int position) {
                if (position <= ProfileActivity.this.emptyRowChat2 || position >= ProfileActivity.this.membersEndRow) {
                    return ProfileActivity.this.processOnClickOrPress(position);
                }
                if (ProfileActivity.this.getParentActivity() == null) {
                    return false;
                }
                ChatParticipant user;
                ChannelParticipant channelParticipant;
                boolean allowKick = false;
                boolean allowSetAdmin = false;
                boolean canEditAdmin = false;
                if (ProfileActivity.this.sortedUsers.isEmpty()) {
                    user = (ChatParticipant) ProfileActivity.this.info.participants.participants.get((position - ProfileActivity.this.emptyRowChat2) - 1);
                } else {
                    user = (ChatParticipant) ProfileActivity.this.info.participants.participants.get(((Integer) ProfileActivity.this.sortedUsers.get((position - ProfileActivity.this.emptyRowChat2) - 1)).intValue());
                }
                ProfileActivity.this.selectedUser = user.user_id;
                if (ChatObject.isChannel(ProfileActivity.this.currentChat)) {
                    channelParticipant = ((TL_chatChannelParticipant) user).channelParticipant;
                    if (user.user_id == UserConfig.getInstance(ProfileActivity.this.currentAccount).getClientUserId()) {
                        return false;
                    }
                    User u = MessagesController.getInstance(ProfileActivity.this.currentAccount).getUser(Integer.valueOf(user.user_id));
                    allowSetAdmin = (channelParticipant instanceof TL_channelParticipant) || (channelParticipant instanceof TL_channelParticipantBanned);
                    if (((channelParticipant instanceof TL_channelParticipantAdmin) || (channelParticipant instanceof TL_channelParticipantCreator)) && !channelParticipant.can_edit) {
                        canEditAdmin = false;
                    } else {
                        canEditAdmin = true;
                    }
                } else {
                    channelParticipant = null;
                    if (user.user_id != UserConfig.getInstance(ProfileActivity.this.currentAccount).getClientUserId()) {
                        if (ProfileActivity.this.currentChat.creator) {
                            allowKick = true;
                        } else if ((user instanceof TL_chatParticipant) && ((ProfileActivity.this.currentChat.admin && ProfileActivity.this.currentChat.admins_enabled) || user.inviter_id == UserConfig.getInstance(ProfileActivity.this.currentAccount).getClientUserId())) {
                            allowKick = true;
                        }
                    }
                    if (!allowKick) {
                        return false;
                    }
                }
                Builder builder = new Builder(ProfileActivity.this.getParentActivity());
                ArrayList<String> items = new ArrayList();
                final ArrayList<Integer> actions = new ArrayList();
                if (ProfileActivity.this.currentChat.megagroup) {
                    if (allowSetAdmin && ChatObject.canAddAdmins(ProfileActivity.this.currentChat)) {
                        items.add(LocaleController.getString("SetAsAdmin", C0493R.string.SetAsAdmin));
                        actions.add(Integer.valueOf(0));
                    }
                    if (ChatObject.canBlockUsers(ProfileActivity.this.currentChat) && canEditAdmin) {
                        items.add(LocaleController.getString("KickFromSupergroup", C0493R.string.KickFromSupergroup));
                        actions.add(Integer.valueOf(1));
                        items.add(LocaleController.getString("KickFromGroup", C0493R.string.KickFromGroup));
                        actions.add(Integer.valueOf(2));
                    }
                } else {
                    items.add(ProfileActivity.this.chat_id > 0 ? LocaleController.getString("KickFromGroup", C0493R.string.KickFromGroup) : LocaleController.getString("KickFromBroadcast", C0493R.string.KickFromBroadcast));
                    actions.add(Integer.valueOf(2));
                }
                if (items.isEmpty()) {
                    return false;
                }
                builder.setItems((CharSequence[]) items.toArray(new CharSequence[items.size()]), new OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, final int i) {
                        if (((Integer) actions.get(i)).intValue() == 2) {
                            ProfileActivity.this.kickUser(ProfileActivity.this.selectedUser);
                            return;
                        }
                        ChannelRightsEditActivity fragment = new ChannelRightsEditActivity(user.user_id, ProfileActivity.this.chat_id, channelParticipant.admin_rights, channelParticipant.banned_rights, ((Integer) actions.get(i)).intValue(), true);
                        fragment.setDelegate(new ChannelRightsEditActivityDelegate() {
                            public void didSetRights(int rights, TL_channelAdminRights rightsAdmin, TL_channelBannedRights rightsBanned) {
                                if (((Integer) actions.get(i)).intValue() == 0) {
                                    TL_chatChannelParticipant channelParticipant = user;
                                    if (rights == 1) {
                                        channelParticipant.channelParticipant = new TL_channelParticipantAdmin();
                                    } else {
                                        channelParticipant.channelParticipant = new TL_channelParticipant();
                                    }
                                    channelParticipant.channelParticipant.inviter_id = UserConfig.getInstance(ProfileActivity.this.currentAccount).getClientUserId();
                                    channelParticipant.channelParticipant.user_id = user.user_id;
                                    channelParticipant.channelParticipant.date = user.date;
                                    channelParticipant.channelParticipant.banned_rights = rightsBanned;
                                    channelParticipant.channelParticipant.admin_rights = rightsAdmin;
                                } else if (((Integer) actions.get(i)).intValue() == 1 && rights == 0 && ProfileActivity.this.currentChat.megagroup && ProfileActivity.this.info != null && ProfileActivity.this.info.participants != null) {
                                    int a;
                                    boolean changed = false;
                                    for (a = 0; a < ProfileActivity.this.info.participants.participants.size(); a++) {
                                        if (((TL_chatChannelParticipant) ProfileActivity.this.info.participants.participants.get(a)).channelParticipant.user_id == user.user_id) {
                                            if (ProfileActivity.this.info != null) {
                                                ChatFull access$2400 = ProfileActivity.this.info;
                                                access$2400.participants_count--;
                                            }
                                            ProfileActivity.this.info.participants.participants.remove(a);
                                            changed = true;
                                            if (ProfileActivity.this.info != null && ProfileActivity.this.info.participants != null) {
                                                for (a = 0; a < ProfileActivity.this.info.participants.participants.size(); a++) {
                                                    if (((ChatParticipant) ProfileActivity.this.info.participants.participants.get(a)).user_id == user.user_id) {
                                                        ProfileActivity.this.info.participants.participants.remove(a);
                                                        changed = true;
                                                        break;
                                                    }
                                                }
                                            }
                                            if (changed) {
                                                ProfileActivity.this.updateOnlineCount();
                                                ProfileActivity.this.updateRowsIds();
                                                ProfileActivity.this.listAdapter.notifyDataSetChanged();
                                            }
                                        }
                                    }
                                    for (a = 0; a < ProfileActivity.this.info.participants.participants.size(); a++) {
                                        if (((ChatParticipant) ProfileActivity.this.info.participants.participants.get(a)).user_id == user.user_id) {
                                            ProfileActivity.this.info.participants.participants.remove(a);
                                            changed = true;
                                            break;
                                        }
                                    }
                                    if (changed) {
                                        ProfileActivity.this.updateOnlineCount();
                                        ProfileActivity.this.updateRowsIds();
                                        ProfileActivity.this.listAdapter.notifyDataSetChanged();
                                    }
                                }
                            }
                        });
                        ProfileActivity.this.presentFragment(fragment);
                    }
                });
                ProfileActivity.this.showDialog(builder.create());
                return true;
            }
        });
        if (this.banFromGroup != 0) {
            if (this.currentChannelParticipant == null) {
                TLObject req = new TL_channels_getParticipant();
                req.channel = MessagesController.getInstance(this.currentAccount).getInputChannel(this.banFromGroup);
                req.user_id = MessagesController.getInstance(this.currentAccount).getInputUser(this.user_id);
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
                    public void run(final TLObject response, TL_error error) {
                        if (response != null) {
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public void run() {
                                    ProfileActivity.this.currentChannelParticipant = ((TL_channels_channelParticipant) response).participant;
                                }
                            });
                        }
                    }
                });
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
            frameLayout1.setOnClickListener(new View.OnClickListener() {

                /* renamed from: org.telegram.ui.ProfileActivity$13$1 */
                class C21721 implements ChannelRightsEditActivityDelegate {
                    C21721() {
                    }

                    public void didSetRights(int rights, TL_channelAdminRights rightsAdmin, TL_channelBannedRights rightsBanned) {
                        ProfileActivity.this.removeSelfFromStack();
                    }
                }

                public void onClick(View v) {
                    TL_channelBannedRights tL_channelBannedRights;
                    int access$000 = ProfileActivity.this.user_id;
                    int access$8500 = ProfileActivity.this.banFromGroup;
                    if (ProfileActivity.this.currentChannelParticipant != null) {
                        tL_channelBannedRights = ProfileActivity.this.currentChannelParticipant.banned_rights;
                    } else {
                        tL_channelBannedRights = null;
                    }
                    ChannelRightsEditActivity fragment = new ChannelRightsEditActivity(access$000, access$8500, null, tL_channelBannedRights, 1, true);
                    fragment.setDelegate(new C21721());
                    ProfileActivity.this.presentFragment(fragment);
                }
            });
            View textView = new TextView(context);
            textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText));
            textView.setTextSize(1, 15.0f);
            textView.setGravity(17);
            textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            textView.setText(LocaleController.getString("BanFromTheGroup", C0493R.string.BanFromTheGroup));
            frameLayout1.addView(textView, LayoutHelper.createFrame(-2, -2.0f, 17, 0.0f, 1.0f, 0.0f, 0.0f));
            this.listView.setPadding(0, AndroidUtilities.dp(88.0f), 0, AndroidUtilities.dp(48.0f));
            this.listView.setBottomGlowOffset(AndroidUtilities.dp(48.0f));
        } else {
            this.listView.setPadding(0, AndroidUtilities.dp(88.0f), 0, 0);
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
        this.avatarImage.setRoundRadius(AndroidUtilities.dp(21.0f));
        this.avatarImage.setPivotX(0.0f);
        this.avatarImage.setPivotY(0.0f);
        frameLayout.addView(this.avatarImage, LayoutHelper.createFrame(42, 42.0f, 51, 64.0f, 0.0f, 0.0f, 0.0f));
        this.avatarImage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (ProfileActivity.this.user_id != 0) {
                    User user = MessagesController.getInstance(ProfileActivity.this.currentAccount).getUser(Integer.valueOf(ProfileActivity.this.user_id));
                    if (user.photo != null && user.photo.photo_big != null) {
                        PhotoViewer.getInstance().setParentActivity(ProfileActivity.this.getParentActivity());
                        PhotoViewer.getInstance().openPhoto(user.photo.photo_big, ProfileActivity.this.provider);
                    }
                } else if (ProfileActivity.this.chat_id != 0) {
                    Chat chat = MessagesController.getInstance(ProfileActivity.this.currentAccount).getChat(Integer.valueOf(ProfileActivity.this.chat_id));
                    if (chat.photo != null && chat.photo.photo_big != null) {
                        PhotoViewer.getInstance().setParentActivity(ProfileActivity.this.getParentActivity());
                        PhotoViewer.getInstance().openPhoto(chat.photo.photo_big, ProfileActivity.this.provider);
                    }
                }
            }
        });
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
                this.nameTextView[a].setLeftDrawableTopPadding(-AndroidUtilities.dp(1.3f));
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
        if (this.user_id != 0 || (this.chat_id >= 0 && (!ChatObject.isLeftFromChat(this.currentChat) || ChatObject.isChannel(this.currentChat)))) {
            this.writeButton = new ImageView(context);
            Drawable drawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56.0f), Theme.getColor(Theme.key_profile_actionBackground), Theme.getColor(Theme.key_profile_actionPressedBackground));
            if (VERSION.SDK_INT < 21) {
                Drawable shadowDrawable = context.getResources().getDrawable(C0493R.drawable.floating_shadow_profile).mutate();
                shadowDrawable.setColorFilter(new PorterDuffColorFilter(Theme.ACTION_BAR_VIDEO_EDIT_COLOR, Mode.MULTIPLY));
                Drawable combinedDrawable = new CombinedDrawable(shadowDrawable, drawable, 0, 0);
                combinedDrawable.setIconSize(AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
                drawable = combinedDrawable;
            }
            this.writeButton.setBackgroundDrawable(drawable);
            this.writeButton.setScaleType(ScaleType.CENTER);
            this.writeButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_profile_actionIcon), Mode.MULTIPLY));
            if (this.user_id != 0) {
                this.writeButton.setImageResource(C0493R.drawable.floating_message);
                this.writeButton.setPadding(0, AndroidUtilities.dp(3.0f), 0, 0);
            } else if (this.chat_id != 0) {
                boolean isChannel = ChatObject.isChannel(this.currentChat);
                if ((!isChannel || ChatObject.canEditInfo(this.currentChat)) && (isChannel || this.currentChat.admin || this.currentChat.creator || !this.currentChat.admins_enabled)) {
                    this.writeButton.setImageResource(C0493R.drawable.floating_camera);
                } else {
                    this.writeButton.setImageResource(C0493R.drawable.floating_message);
                    this.writeButton.setPadding(0, AndroidUtilities.dp(3.0f), 0, 0);
                }
            }
            frameLayout.addView(this.writeButton, LayoutHelper.createFrame(VERSION.SDK_INT >= 21 ? 56 : 60, VERSION.SDK_INT >= 21 ? 56.0f : 60.0f, 53, 0.0f, 0.0f, 16.0f, 0.0f));
            if (VERSION.SDK_INT >= 21) {
                StateListAnimator animator = new StateListAnimator();
                animator.addState(new int[]{16842919}, ObjectAnimator.ofFloat(this.writeButton, "translationZ", new float[]{(float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(4.0f)}).setDuration(200));
                animator.addState(new int[0], ObjectAnimator.ofFloat(this.writeButton, "translationZ", new float[]{(float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(2.0f)}).setDuration(200));
                this.writeButton.setStateListAnimator(animator);
                this.writeButton.setOutlineProvider(new ViewOutlineProvider() {
                    @SuppressLint({"NewApi"})
                    public void getOutline(View view, Outline outline) {
                        outline.setOval(0, 0, AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
                    }
                });
            }
            this.writeButton.setOnClickListener(new View.OnClickListener() {

                /* renamed from: org.telegram.ui.ProfileActivity$16$1 */
                class C21731 implements OnClickListener {
                    C21731() {
                    }

                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i == 0) {
                            ProfileActivity.this.avatarUpdater.openCamera();
                        } else if (i == 1) {
                            ProfileActivity.this.avatarUpdater.openGallery();
                        } else if (i == 2) {
                            MessagesController.getInstance(ProfileActivity.this.currentAccount).changeChatAvatar(ProfileActivity.this.chat_id, null);
                        }
                    }
                }

                public void onClick(View v) {
                    if (ProfileActivity.this.getParentActivity() != null) {
                        Bundle args;
                        if (ProfileActivity.this.user_id != 0) {
                            if (ProfileActivity.this.playProfileAnimation && (ProfileActivity.this.parentLayout.fragmentsStack.get(ProfileActivity.this.parentLayout.fragmentsStack.size() - 2) instanceof ChatActivity)) {
                                ProfileActivity.this.finishFragment();
                                return;
                            }
                            User user = MessagesController.getInstance(ProfileActivity.this.currentAccount).getUser(Integer.valueOf(ProfileActivity.this.user_id));
                            if (user != null && !(user instanceof TL_userEmpty)) {
                                args = new Bundle();
                                args.putInt("user_id", ProfileActivity.this.user_id);
                                if (MessagesController.getInstance(ProfileActivity.this.currentAccount).checkCanOpenChat(args, ProfileActivity.this)) {
                                    NotificationCenter.getInstance(ProfileActivity.this.currentAccount).removeObserver(ProfileActivity.this, NotificationCenter.closeChats);
                                    NotificationCenter.getInstance(ProfileActivity.this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                                    ProfileActivity.this.presentFragment(new ChatActivity(args), true);
                                }
                            }
                        } else if (ProfileActivity.this.chat_id != 0) {
                            boolean isChannel = ChatObject.isChannel(ProfileActivity.this.currentChat);
                            if ((!isChannel || ChatObject.canEditInfo(ProfileActivity.this.currentChat)) && (isChannel || ProfileActivity.this.currentChat.admin || ProfileActivity.this.currentChat.creator || !ProfileActivity.this.currentChat.admins_enabled)) {
                                Builder builder = new Builder(ProfileActivity.this.getParentActivity());
                                Chat chat = MessagesController.getInstance(ProfileActivity.this.currentAccount).getChat(Integer.valueOf(ProfileActivity.this.chat_id));
                                CharSequence[] items = (chat.photo == null || chat.photo.photo_big == null || (chat.photo instanceof TL_chatPhotoEmpty)) ? new CharSequence[]{LocaleController.getString("FromCamera", C0493R.string.FromCamera), LocaleController.getString("FromGalley", C0493R.string.FromGalley)} : new CharSequence[]{LocaleController.getString("FromCamera", C0493R.string.FromCamera), LocaleController.getString("FromGalley", C0493R.string.FromGalley), LocaleController.getString("DeletePhoto", C0493R.string.DeletePhoto)};
                                builder.setItems(items, new C21731());
                                ProfileActivity.this.showDialog(builder.create());
                            } else if (ProfileActivity.this.playProfileAnimation && (ProfileActivity.this.parentLayout.fragmentsStack.get(ProfileActivity.this.parentLayout.fragmentsStack.size() - 2) instanceof ChatActivity)) {
                                ProfileActivity.this.finishFragment();
                            } else {
                                args = new Bundle();
                                args.putInt("chat_id", ProfileActivity.this.currentChat.id);
                                if (MessagesController.getInstance(ProfileActivity.this.currentAccount).checkCanOpenChat(args, ProfileActivity.this)) {
                                    NotificationCenter.getInstance(ProfileActivity.this.currentAccount).removeObserver(ProfileActivity.this, NotificationCenter.closeChats);
                                    NotificationCenter.getInstance(ProfileActivity.this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                                    ProfileActivity.this.presentFragment(new ChatActivity(args), true);
                                }
                            }
                        }
                    }
                }
            });
        }
        needLayout();
        this.listView.setOnScrollListener(new OnScrollListener() {
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                ProfileActivity.this.checkListViewScroll();
                if (ProfileActivity.this.participantsMap != null && ProfileActivity.this.loadMoreMembersRow != -1 && ProfileActivity.this.layoutManager.findLastVisibleItemPosition() > ProfileActivity.this.loadMoreMembersRow - 8) {
                    ProfileActivity.this.getChannelParticipants(false);
                }
            }
        });
        return this.fragmentView;
    }

    private boolean processOnClickOrPress(final int position) {
        User user;
        Builder builder;
        if (position == this.usernameRow || position == this.channelNameRow) {
            String username;
            if (position == this.usernameRow) {
                user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.user_id));
                if (user == null || user.username == null) {
                    return false;
                }
                username = user.username;
            } else {
                Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.chat_id));
                if (chat == null || chat.username == null) {
                    return false;
                }
                username = chat.username;
            }
            builder = new Builder(getParentActivity());
            builder.setItems(new CharSequence[]{LocaleController.getString("Copy", C0493R.string.Copy)}, new OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (i == 0) {
                        try {
                            ((ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", "@" + username));
                        } catch (Throwable e) {
                            FileLog.m3e(e);
                        }
                    }
                }
            });
            showDialog(builder.create());
            return true;
        } else if (position == this.phoneRow) {
            user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.user_id));
            if (user == null || user.phone == null || user.phone.length() == 0 || getParentActivity() == null) {
                return false;
            }
            builder = new Builder(getParentActivity());
            ArrayList<CharSequence> items = new ArrayList();
            final ArrayList<Integer> actions = new ArrayList();
            TL_userFull userFull = MessagesController.getInstance(this.currentAccount).getUserFull(user.id);
            if (userFull != null && userFull.phone_calls_available) {
                items.add(LocaleController.getString("CallViaTelegram", C0493R.string.CallViaTelegram));
                actions.add(Integer.valueOf(2));
            }
            items.add(LocaleController.getString("Call", C0493R.string.Call));
            actions.add(Integer.valueOf(0));
            items.add(LocaleController.getString("Copy", C0493R.string.Copy));
            actions.add(Integer.valueOf(1));
            builder.setItems((CharSequence[]) items.toArray(new CharSequence[items.size()]), new OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    i = ((Integer) actions.get(i)).intValue();
                    if (i == 0) {
                        try {
                            Intent intent = new Intent("android.intent.action.DIAL", Uri.parse("tel:+" + user.phone));
                            intent.addFlags(268435456);
                            ProfileActivity.this.getParentActivity().startActivityForResult(intent, 500);
                        } catch (Throwable e) {
                            FileLog.m3e(e);
                        }
                    } else if (i == 1) {
                        try {
                            ((ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", "+" + user.phone));
                        } catch (Throwable e2) {
                            FileLog.m3e(e2);
                        }
                    } else if (i == 2) {
                        VoIPHelper.startCall(user, ProfileActivity.this.getParentActivity(), MessagesController.getInstance(ProfileActivity.this.currentAccount).getUserFull(user.id));
                    }
                }
            });
            showDialog(builder.create());
            return true;
        } else if (position != this.channelInfoRow && position != this.userInfoRow && position != this.userInfoDetailedRow) {
            return false;
        } else {
            builder = new Builder(getParentActivity());
            builder.setItems(new CharSequence[]{LocaleController.getString("Copy", C0493R.string.Copy)}, new OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    try {
                        String about;
                        if (position == ProfileActivity.this.channelInfoRow) {
                            about = ProfileActivity.this.info.about;
                        } else {
                            TL_userFull userFull = MessagesController.getInstance(ProfileActivity.this.currentAccount).getUserFull(ProfileActivity.this.user_id);
                            about = userFull != null ? userFull.about : null;
                        }
                        if (!TextUtils.isEmpty(about)) {
                            AndroidUtilities.addToClipboard(about);
                        }
                    } catch (Throwable e) {
                        FileLog.m3e(e);
                    }
                }
            });
            showDialog(builder.create());
            return true;
        }
    }

    private void leaveChatPressed() {
        Builder builder = new Builder(getParentActivity());
        if (!ChatObject.isChannel(this.chat_id, this.currentAccount) || this.currentChat.megagroup) {
            builder.setMessage(LocaleController.getString("AreYouSureDeleteAndExit", C0493R.string.AreYouSureDeleteAndExit));
        } else {
            builder.setMessage(ChatObject.isChannel(this.chat_id, this.currentAccount) ? LocaleController.getString("ChannelLeaveAlert", C0493R.string.ChannelLeaveAlert) : LocaleController.getString("AreYouSureDeleteAndExit", C0493R.string.AreYouSureDeleteAndExit));
        }
        builder.setTitle(LocaleController.getString("AppName", C0493R.string.AppName));
        builder.setPositiveButton(LocaleController.getString("OK", C0493R.string.OK), new OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                ProfileActivity.this.kickUser(0);
            }
        });
        builder.setNegativeButton(LocaleController.getString("Cancel", C0493R.string.Cancel), null);
        showDialog(builder.create());
    }

    public void saveSelfArgs(Bundle args) {
        if (this.chat_id != 0 && this.avatarUpdater != null && this.avatarUpdater.currentPicturePath != null) {
            args.putString("path", this.avatarUpdater.currentPicturePath);
        }
    }

    public void restoreSelfArgs(Bundle args) {
        if (this.chat_id != 0) {
            MessagesController.getInstance(this.currentAccount).loadChatInfo(this.chat_id, null, false);
            if (this.avatarUpdater != null) {
                this.avatarUpdater.currentPicturePath = args.getString("path");
            }
        }
    }

    public void onActivityResultFragment(int requestCode, int resultCode, Intent data) {
        if (this.chat_id != 0) {
            this.avatarUpdater.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void getChannelParticipants(boolean reload) {
        int i = 0;
        if (!this.loadingUsers && this.participantsMap != null && this.info != null) {
            int delay;
            this.loadingUsers = true;
            if (this.participantsMap.size() == 0 || !reload) {
                delay = 0;
            } else {
                delay = 300;
            }
            final TL_channels_getParticipants req = new TL_channels_getParticipants();
            req.channel = MessagesController.getInstance(this.currentAccount).getInputChannel(this.chat_id);
            req.filter = new TL_channelParticipantsRecent();
            if (!reload) {
                i = this.participantsMap.size();
            }
            req.offset = i;
            req.limit = Callback.DEFAULT_DRAG_ANIMATION_DURATION;
            ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
                public void run(final TLObject response, final TL_error error) {
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            if (error == null) {
                                TL_channels_channelParticipants res = response;
                                MessagesController.getInstance(ProfileActivity.this.currentAccount).putUsers(res.users, false);
                                if (res.users.size() < Callback.DEFAULT_DRAG_ANIMATION_DURATION) {
                                    ProfileActivity.this.usersEndReached = true;
                                }
                                if (req.offset == 0) {
                                    ProfileActivity.this.participantsMap.clear();
                                    ProfileActivity.this.info.participants = new TL_chatParticipants();
                                    MessagesStorage.getInstance(ProfileActivity.this.currentAccount).putUsersAndChats(res.users, null, true, true);
                                    MessagesStorage.getInstance(ProfileActivity.this.currentAccount).updateChannelUsers(ProfileActivity.this.chat_id, res.participants);
                                }
                                for (int a = 0; a < res.participants.size(); a++) {
                                    TL_chatChannelParticipant participant = new TL_chatChannelParticipant();
                                    participant.channelParticipant = (ChannelParticipant) res.participants.get(a);
                                    participant.inviter_id = participant.channelParticipant.inviter_id;
                                    participant.user_id = participant.channelParticipant.user_id;
                                    participant.date = participant.channelParticipant.date;
                                    if (ProfileActivity.this.participantsMap.indexOfKey(participant.user_id) < 0) {
                                        ProfileActivity.this.info.participants.participants.add(participant);
                                        ProfileActivity.this.participantsMap.put(participant.user_id, participant);
                                    }
                                }
                            }
                            ProfileActivity.this.updateOnlineCount();
                            ProfileActivity.this.loadingUsers = false;
                            ProfileActivity.this.updateRowsIds();
                            if (ProfileActivity.this.listAdapter != null) {
                                ProfileActivity.this.listAdapter.notifyDataSetChanged();
                            }
                        }
                    }, (long) delay);
                }
            }), this.classGuid);
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
                args.putInt("chat_id", this.currentChat.id);
            }
            args.putString("selectAlertString", LocaleController.getString("AddToTheGroup", C0493R.string.AddToTheGroup));
        }
        ContactsActivity fragment = new ContactsActivity(args);
        fragment.setDelegate(new ContactsActivityDelegate() {
            public void didSelectContact(User user, String param, ContactsActivity activity) {
                MessagesController.getInstance(ProfileActivity.this.currentAccount).addUserToChat(ProfileActivity.this.chat_id, user, ProfileActivity.this.info, param != null ? Utilities.parseInt(param).intValue() : 0, null, ProfileActivity.this);
            }
        });
        if (!(this.info == null || this.info.participants == null)) {
            SparseArray<User> users = new SparseArray();
            for (int a = 0; a < this.info.participants.participants.size(); a++) {
                users.put(((ChatParticipant) this.info.participants.participants.get(a)).user_id, null);
            }
            fragment.setIgnoreUsers(users);
        }
        presentFragment(fragment);
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
        int newTop = (this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0) + ActionBar.getCurrentActionBarHeight();
        if (!(this.listView == null || this.openAnimationInProgress)) {
            layoutParams = (FrameLayout.LayoutParams) this.listView.getLayoutParams();
            if (layoutParams.topMargin != newTop) {
                layoutParams.topMargin = newTop;
                this.listView.setLayoutParams(layoutParams);
            }
        }
        if (this.avatarImage != null) {
            float diff = ((float) this.extraHeight) / ((float) AndroidUtilities.dp(88.0f));
            this.listView.setTopGlowOffset(this.extraHeight);
            if (this.writeButton != null) {
                this.writeButton.setTranslationY((float) ((((this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0) + ActionBar.getCurrentActionBarHeight()) + this.extraHeight) - AndroidUtilities.dp(29.5f)));
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
                        this.writeButtonAnimation.addListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animation) {
                                if (ProfileActivity.this.writeButtonAnimation != null && ProfileActivity.this.writeButtonAnimation.equals(animation)) {
                                    ProfileActivity.this.writeButtonAnimation = null;
                                }
                            }
                        });
                        this.writeButtonAnimation.start();
                    }
                }
            }
            float avatarY = ((((float) (this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0)) + ((((float) ActionBar.getCurrentActionBarHeight()) / 2.0f) * (1.0f + diff))) - (21.0f * AndroidUtilities.density)) + ((27.0f * AndroidUtilities.density) * diff);
            this.avatarImage.setScaleX((42.0f + (18.0f * diff)) / 42.0f);
            this.avatarImage.setScaleY((42.0f + (18.0f * diff)) / 42.0f);
            this.avatarImage.setTranslationX(((float) (-AndroidUtilities.dp(47.0f))) * diff);
            this.avatarImage.setTranslationY((float) Math.ceil((double) avatarY));
            for (int a = 0; a < 2; a++) {
                if (this.nameTextView[a] != null) {
                    this.nameTextView[a].setTranslationX((-21.0f * AndroidUtilities.density) * diff);
                    this.nameTextView[a].setTranslationY((((float) Math.floor((double) avatarY)) + ((float) AndroidUtilities.dp(1.3f))) + (((float) AndroidUtilities.dp(7.0f)) * diff));
                    this.onlineTextView[a].setTranslationX((-21.0f * AndroidUtilities.density) * diff);
                    this.onlineTextView[a].setTranslationY((((float) Math.floor((double) avatarY)) + ((float) AndroidUtilities.dp(24.0f))) + (((float) Math.floor((double) (11.0f * AndroidUtilities.density))) * diff));
                    this.nameTextView[a].setScaleX(1.0f + (0.12f * diff));
                    this.nameTextView[a].setScaleY(1.0f + (0.12f * diff));
                    if (a == 1 && !this.openAnimationInProgress) {
                        int width;
                        if (AndroidUtilities.isTablet()) {
                            width = AndroidUtilities.dp(490.0f);
                        } else {
                            width = AndroidUtilities.displaySize.x;
                        }
                        int i = (this.callItem == null && this.editItem == null) ? 0 : 48;
                        width = (int) (((float) (width - AndroidUtilities.dp((((float) (i + 40)) * (1.0f - diff)) + 126.0f))) - this.nameTextView[a].getTranslationX());
                        layoutParams = (FrameLayout.LayoutParams) this.nameTextView[a].getLayoutParams();
                        if (((float) width) < (this.nameTextView[a].getPaint().measureText(this.nameTextView[a].getText().toString()) * this.nameTextView[a].getScaleX()) + ((float) this.nameTextView[a].getSideDrawablesSize())) {
                            layoutParams.width = (int) Math.ceil((double) (((float) width) / this.nameTextView[a].getScaleX()));
                        } else {
                            layoutParams.width = -2;
                        }
                        this.nameTextView[a].setLayoutParams(layoutParams);
                        layoutParams = (FrameLayout.LayoutParams) this.onlineTextView[a].getLayoutParams();
                        layoutParams.rightMargin = (int) Math.ceil((double) ((this.onlineTextView[a].getTranslationX() + ((float) AndroidUtilities.dp(8.0f))) + (((float) AndroidUtilities.dp(40.0f)) * (1.0f - diff))));
                        this.onlineTextView[a].setLayoutParams(layoutParams);
                    }
                }
            }
        }
    }

    private void fixLayout() {
        if (this.fragmentView != null) {
            this.fragmentView.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {
                public boolean onPreDraw() {
                    if (ProfileActivity.this.fragmentView != null) {
                        ProfileActivity.this.checkListViewScroll();
                        ProfileActivity.this.needLayout();
                        ProfileActivity.this.fragmentView.getViewTreeObserver().removeOnPreDrawListener(this);
                    }
                    return true;
                }
            });
        }
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        fixLayout();
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        ViewHolder holder;
        Chat newChat;
        int count;
        int a;
        if (id == NotificationCenter.updateInterfaces) {
            int mask = ((Integer) args[0]).intValue();
            if (this.user_id != 0) {
                if (!((mask & 2) == 0 && (mask & 1) == 0 && (mask & 4) == 0)) {
                    updateProfileData();
                }
                if ((mask & 1024) != 0 && this.listView != null) {
                    holder = (Holder) this.listView.findViewHolderForPosition(this.phoneRow);
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
                    count = this.listView.getChildCount();
                    for (a = 0; a < count; a++) {
                        View child = this.listView.getChildAt(a);
                        if (child instanceof UserCell) {
                            ((UserCell) child).update(mask);
                        }
                    }
                }
            }
        } else if (id == NotificationCenter.contactsDidLoaded) {
            createActionBarMenu();
        } else if (id == NotificationCenter.mediaCountDidLoaded) {
            long uid = ((Long) args[0]).longValue();
            long did = this.dialog_id;
            if (did == 0) {
                if (this.user_id != 0) {
                    did = (long) this.user_id;
                } else if (this.chat_id != 0) {
                    did = (long) (-this.chat_id);
                }
            }
            if (uid == did || uid == this.mergeDialogId) {
                if (uid == did) {
                    this.totalMediaCount = ((Integer) args[1]).intValue();
                } else {
                    this.totalMediaCountMerge = ((Integer) args[1]).intValue();
                }
                if (this.listView != null) {
                    count = this.listView.getChildCount();
                    for (a = 0; a < count; a++) {
                        holder = (Holder) this.listView.getChildViewHolder(this.listView.getChildAt(a));
                        if (holder.getAdapterPosition() == this.sharedMediaRow) {
                            this.listAdapter.onBindViewHolder(holder, this.sharedMediaRow);
                            return;
                        }
                    }
                }
            }
        } else if (id == NotificationCenter.encryptedChatCreated) {
            if (this.creatingChat) {
                final Object[] objArr = args;
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        NotificationCenter.getInstance(ProfileActivity.this.currentAccount).removeObserver(ProfileActivity.this, NotificationCenter.closeChats);
                        NotificationCenter.getInstance(ProfileActivity.this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                        EncryptedChat encryptedChat = objArr[0];
                        Bundle args2 = new Bundle();
                        args2.putInt("enc_id", encryptedChat.id);
                        ProfileActivity.this.presentFragment(new ChatActivity(args2), true);
                    }
                });
            }
        } else if (id == NotificationCenter.encryptedChatUpdated) {
            EncryptedChat chat = args[0];
            if (this.currentEncryptedChat != null && chat.id == this.currentEncryptedChat.id) {
                this.currentEncryptedChat = chat;
                updateRowsIds();
                if (this.listAdapter != null) {
                    this.listAdapter.notifyDataSetChanged();
                }
            }
        } else if (id == NotificationCenter.blockedUsersDidLoaded) {
            boolean oldValue = this.userBlocked;
            this.userBlocked = MessagesController.getInstance(this.currentAccount).blockedUsers.contains(Integer.valueOf(this.user_id));
            if (oldValue != this.userBlocked) {
                createActionBarMenu();
            }
        } else if (id == NotificationCenter.chatInfoDidLoaded) {
            ChatFull chatFull = args[0];
            if (chatFull.id == this.chat_id) {
                boolean byChannelUsers = ((Boolean) args[2]).booleanValue();
                if ((this.info instanceof TL_channelFull) && chatFull.participants == null && this.info != null) {
                    chatFull.participants = this.info.participants;
                }
                boolean loadChannelParticipants = this.info == null && (chatFull instanceof TL_channelFull);
                this.info = chatFull;
                if (this.mergeDialogId == 0 && this.info.migrated_from_chat_id != 0) {
                    this.mergeDialogId = (long) (-this.info.migrated_from_chat_id);
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
            removeSelfFromStack();
        } else if (id == NotificationCenter.botInfoDidLoaded) {
            BotInfo info = args[0];
            if (info.user_id == this.user_id) {
                this.botInfo = info;
                updateRowsIds();
                if (this.listAdapter != null) {
                    this.listAdapter.notifyDataSetChanged();
                }
            }
        } else if (id == NotificationCenter.userInfoDidLoaded) {
            if (((Integer) args[0]).intValue() == this.user_id) {
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
        } else if (id == NotificationCenter.didReceivedNewMessages && ((Long) args[0]).longValue() == this.dialog_id) {
            ArrayList<MessageObject> arr = args[1];
            for (a = 0; a < arr.size(); a++) {
                MessageObject obj = (MessageObject) arr.get(a);
                if (this.currentEncryptedChat != null && obj.messageOwner.action != null && (obj.messageOwner.action instanceof TL_messageEncryptedAction) && (obj.messageOwner.action.encryptedAction instanceof TL_decryptedMessageActionSetMessageTTL)) {
                    TL_decryptedMessageActionSetMessageTTL action = obj.messageOwner.action.encryptedAction;
                    if (this.listAdapter != null) {
                        this.listAdapter.notifyDataSetChanged();
                    }
                }
            }
        }
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

    protected void onTransitionAnimationStart(boolean isOpen, boolean backward) {
        if (!backward && this.playProfileAnimation && this.allowProfileAnimation) {
            this.openAnimationInProgress = true;
        }
        NotificationCenter.getInstance(this.currentAccount).setAllowedNotificationsDutingAnimation(new int[]{NotificationCenter.dialogsNeedReload, NotificationCenter.closeChats, NotificationCenter.mediaCountDidLoaded});
        NotificationCenter.getInstance(this.currentAccount).setAnimationInProgress(true);
    }

    protected void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        if (!backward && this.playProfileAnimation && this.allowProfileAnimation) {
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
        this.listView.setTranslationX(((float) AndroidUtilities.dp(48.0f)) - (((float) AndroidUtilities.dp(48.0f)) * progress));
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
        final AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(180);
        this.listView.setLayerType(2, null);
        ActionBarMenu menu = this.actionBar.createMenu();
        if (menu.getItem(10) == null && this.animatingItem == null) {
            this.animatingItem = menu.addItem(10, (int) C0493R.drawable.ic_ab_other);
        }
        ArrayList<Animator> animators;
        int a;
        Object obj;
        String str;
        float[] fArr;
        if (isOpen) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.onlineTextView[1].getLayoutParams();
            layoutParams.rightMargin = (int) ((-21.0f * AndroidUtilities.density) + ((float) AndroidUtilities.dp(8.0f)));
            this.onlineTextView[1].setLayoutParams(layoutParams);
            int width = (int) Math.ceil((double) (((float) (AndroidUtilities.displaySize.x - AndroidUtilities.dp(126.0f))) + (21.0f * AndroidUtilities.density)));
            layoutParams = (FrameLayout.LayoutParams) this.nameTextView[1].getLayoutParams();
            if (((float) width) < (this.nameTextView[1].getPaint().measureText(this.nameTextView[1].getText().toString()) * 1.12f) + ((float) this.nameTextView[1].getSideDrawablesSize())) {
                layoutParams.width = (int) Math.ceil((double) (((float) width) / 1.12f));
            } else {
                layoutParams.width = -2;
            }
            this.nameTextView[1].setLayoutParams(layoutParams);
            this.initialAnimationExtraHeight = AndroidUtilities.dp(88.0f);
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
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                animatorSet.start();
            }
        }, 50);
        return animatorSet;
    }

    private void updateOnlineCount() {
        this.onlineCount = 0;
        int currentTime = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
        this.sortedUsers.clear();
        if ((this.info instanceof TL_chatFull) || ((this.info instanceof TL_channelFull) && this.info.participants_count <= Callback.DEFAULT_DRAG_ANIMATION_DURATION && this.info.participants != null)) {
            for (int a = 0; a < this.info.participants.participants.size(); a++) {
                User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(((ChatParticipant) this.info.participants.participants.get(a)).user_id));
                if (!(user == null || user.status == null || ((user.status.expires <= currentTime && user.id != UserConfig.getInstance(this.currentAccount).getClientUserId()) || user.status.expires <= 10000))) {
                    this.onlineCount++;
                }
                this.sortedUsers.add(Integer.valueOf(a));
            }
            try {
                Collections.sort(this.sortedUsers, new Comparator<Integer>() {
                    public int compare(Integer lhs, Integer rhs) {
                        User user1 = MessagesController.getInstance(ProfileActivity.this.currentAccount).getUser(Integer.valueOf(((ChatParticipant) ProfileActivity.this.info.participants.participants.get(rhs.intValue())).user_id));
                        User user2 = MessagesController.getInstance(ProfileActivity.this.currentAccount).getUser(Integer.valueOf(((ChatParticipant) ProfileActivity.this.info.participants.participants.get(lhs.intValue())).user_id));
                        int status1 = 0;
                        int status2 = 0;
                        if (!(user1 == null || user1.status == null)) {
                            status1 = user1.id == UserConfig.getInstance(ProfileActivity.this.currentAccount).getClientUserId() ? ConnectionsManager.getInstance(ProfileActivity.this.currentAccount).getCurrentTime() + 50000 : user1.status.expires;
                        }
                        if (!(user2 == null || user2.status == null)) {
                            status2 = user2.id == UserConfig.getInstance(ProfileActivity.this.currentAccount).getClientUserId() ? ConnectionsManager.getInstance(ProfileActivity.this.currentAccount).getCurrentTime() + 50000 : user2.status.expires;
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
                });
            } catch (Throwable e) {
                FileLog.m3e(e);
            }
            if (this.listAdapter != null) {
                this.listAdapter.notifyItemRangeChanged(this.emptyRowChat2 + 1, this.sortedUsers.size());
            }
        }
    }

    public void setChatInfo(ChatFull chatInfo) {
        this.info = chatInfo;
        if (!(this.info == null || this.info.migrated_from_chat_id == 0)) {
            this.mergeDialogId = (long) (-this.info.migrated_from_chat_id);
        }
        fetchUsersFromChannelInfo();
    }

    private void fetchUsersFromChannelInfo() {
        if (this.currentChat != null && this.currentChat.megagroup && (this.info instanceof TL_channelFull) && this.info.participants != null) {
            for (int a = 0; a < this.info.participants.participants.size(); a++) {
                ChatParticipant chatParticipant = (ChatParticipant) this.info.participants.participants.get(a);
                this.participantsMap.put(chatParticipant.user_id, chatParticipant);
            }
        }
    }

    private void kickUser(int uid) {
        if (uid != 0) {
            MessagesController.getInstance(this.currentAccount).deleteUserFromChat(this.chat_id, MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(uid)), this.info);
            return;
        }
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.closeChats);
        if (AndroidUtilities.isTablet()) {
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.closeChats, Long.valueOf(-((long) this.chat_id)));
        } else {
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
        }
        MessagesController.getInstance(this.currentAccount).deleteUserFromChat(this.chat_id, MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(UserConfig.getInstance(this.currentAccount).getClientUserId())), this.info);
        this.playProfileAnimation = false;
        finishFragment();
    }

    public boolean isChat() {
        return this.chat_id != 0;
    }

    private void updateRowsIds() {
        boolean hasUsername = false;
        this.emptyRow = -1;
        this.phoneRow = -1;
        this.userInfoRow = -1;
        this.userInfoDetailedRow = -1;
        this.userSectionRow = -1;
        this.sectionRow = -1;
        this.sharedMediaRow = -1;
        this.settingsNotificationsRow = -1;
        this.usernameRow = -1;
        this.settingsTimerRow = -1;
        this.settingsKeyRow = -1;
        this.startSecretChatRow = -1;
        this.membersEndRow = -1;
        this.emptyRowChat2 = -1;
        this.addMemberRow = -1;
        this.channelInfoRow = -1;
        this.channelNameRow = -1;
        this.convertRow = -1;
        this.convertHelpRow = -1;
        this.emptyRowChat = -1;
        this.membersSectionRow = -1;
        this.membersRow = -1;
        this.leaveChannelRow = -1;
        this.loadMoreMembersRow = -1;
        this.groupsInCommonRow = -1;
        this.rowCount = 0;
        int i;
        if (this.user_id != 0) {
            User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.user_id));
            i = this.rowCount;
            this.rowCount = i + 1;
            this.emptyRow = i;
            if (!(this.isBot || TextUtils.isEmpty(user.phone))) {
                i = this.rowCount;
                this.rowCount = i + 1;
                this.phoneRow = i;
            }
            TL_userFull userFull = MessagesController.getInstance(this.currentAccount).getUserFull(this.user_id);
            if (!(user == null || TextUtils.isEmpty(user.username))) {
                hasUsername = true;
            }
            if (!(userFull == null || TextUtils.isEmpty(userFull.about))) {
                if (this.phoneRow != -1) {
                    i = this.rowCount;
                    this.rowCount = i + 1;
                    this.userSectionRow = i;
                }
                if (hasUsername || this.isBot) {
                    i = this.rowCount;
                    this.rowCount = i + 1;
                    this.userInfoRow = i;
                } else {
                    i = this.rowCount;
                    this.rowCount = i + 1;
                    this.userInfoDetailedRow = i;
                }
            }
            if (hasUsername) {
                i = this.rowCount;
                this.rowCount = i + 1;
                this.usernameRow = i;
            }
            if (!(this.phoneRow == -1 && this.userInfoRow == -1 && this.userInfoDetailedRow == -1 && this.usernameRow == -1)) {
                i = this.rowCount;
                this.rowCount = i + 1;
                this.sectionRow = i;
            }
            if (this.user_id != UserConfig.getInstance(this.currentAccount).getClientUserId()) {
                i = this.rowCount;
                this.rowCount = i + 1;
                this.settingsNotificationsRow = i;
            }
            i = this.rowCount;
            this.rowCount = i + 1;
            this.sharedMediaRow = i;
            if (this.currentEncryptedChat instanceof TL_encryptedChat) {
                i = this.rowCount;
                this.rowCount = i + 1;
                this.settingsTimerRow = i;
                i = this.rowCount;
                this.rowCount = i + 1;
                this.settingsKeyRow = i;
            }
            if (!(userFull == null || userFull.common_chats_count == 0)) {
                i = this.rowCount;
                this.rowCount = i + 1;
                this.groupsInCommonRow = i;
            }
            if (user != null && !this.isBot && this.currentEncryptedChat == null && user.id != UserConfig.getInstance(this.currentAccount).getClientUserId()) {
                i = this.rowCount;
                this.rowCount = i + 1;
                this.startSecretChatRow = i;
            }
        } else if (this.chat_id == 0) {
        } else {
            if (this.chat_id > 0) {
                i = this.rowCount;
                this.rowCount = i + 1;
                this.emptyRow = i;
                if (ChatObject.isChannel(this.currentChat) && (!(this.info == null || this.info.about == null || this.info.about.length() <= 0) || (this.currentChat.username != null && this.currentChat.username.length() > 0))) {
                    if (!(this.info == null || this.info.about == null || this.info.about.length() <= 0)) {
                        i = this.rowCount;
                        this.rowCount = i + 1;
                        this.channelInfoRow = i;
                    }
                    if (this.currentChat.username != null && this.currentChat.username.length() > 0) {
                        i = this.rowCount;
                        this.rowCount = i + 1;
                        this.channelNameRow = i;
                    }
                    i = this.rowCount;
                    this.rowCount = i + 1;
                    this.sectionRow = i;
                }
                i = this.rowCount;
                this.rowCount = i + 1;
                this.settingsNotificationsRow = i;
                i = this.rowCount;
                this.rowCount = i + 1;
                this.sharedMediaRow = i;
                if (ChatObject.isChannel(this.currentChat)) {
                    if (!(this.currentChat.megagroup || this.info == null || (!this.currentChat.creator && !this.info.can_view_participants))) {
                        i = this.rowCount;
                        this.rowCount = i + 1;
                        this.membersRow = i;
                    }
                    if (!(this.currentChat.creator || this.currentChat.left || this.currentChat.kicked || this.currentChat.megagroup)) {
                        i = this.rowCount;
                        this.rowCount = i + 1;
                        this.leaveChannelRow = i;
                    }
                    if (this.currentChat.megagroup && (((this.currentChat.admin_rights != null && this.currentChat.admin_rights.invite_users) || this.currentChat.creator || this.currentChat.democracy) && (this.info == null || this.info.participants_count < MessagesController.getInstance(this.currentAccount).maxMegagroupCount))) {
                        i = this.rowCount;
                        this.rowCount = i + 1;
                        this.addMemberRow = i;
                    }
                    if (this.info != null && this.currentChat.megagroup && this.info.participants != null && !this.info.participants.participants.isEmpty()) {
                        i = this.rowCount;
                        this.rowCount = i + 1;
                        this.emptyRowChat = i;
                        i = this.rowCount;
                        this.rowCount = i + 1;
                        this.membersSectionRow = i;
                        i = this.rowCount;
                        this.rowCount = i + 1;
                        this.emptyRowChat2 = i;
                        this.rowCount += this.info.participants.participants.size();
                        this.membersEndRow = this.rowCount;
                        if (!this.usersEndReached) {
                            i = this.rowCount;
                            this.rowCount = i + 1;
                            this.loadMoreMembersRow = i;
                            return;
                        }
                        return;
                    }
                    return;
                }
                if (this.info != null) {
                    if (!(this.info.participants instanceof TL_chatParticipantsForbidden) && this.info.participants.participants.size() < MessagesController.getInstance(this.currentAccount).maxGroupCount && (this.currentChat.admin || this.currentChat.creator || !this.currentChat.admins_enabled)) {
                        i = this.rowCount;
                        this.rowCount = i + 1;
                        this.addMemberRow = i;
                    }
                    if (this.currentChat.creator && this.info.participants.participants.size() >= MessagesController.getInstance(this.currentAccount).minGroupConvertSize) {
                        i = this.rowCount;
                        this.rowCount = i + 1;
                        this.convertRow = i;
                    }
                }
                i = this.rowCount;
                this.rowCount = i + 1;
                this.emptyRowChat = i;
                if (this.convertRow != -1) {
                    i = this.rowCount;
                    this.rowCount = i + 1;
                    this.convertHelpRow = i;
                } else {
                    i = this.rowCount;
                    this.rowCount = i + 1;
                    this.membersSectionRow = i;
                }
                if (this.info != null && !(this.info.participants instanceof TL_chatParticipantsForbidden)) {
                    i = this.rowCount;
                    this.rowCount = i + 1;
                    this.emptyRowChat2 = i;
                    this.rowCount += this.info.participants.participants.size();
                    this.membersEndRow = this.rowCount;
                }
            } else if (!ChatObject.isChannel(this.currentChat) && this.info != null && !(this.info.participants instanceof TL_chatParticipantsForbidden)) {
                i = this.rowCount;
                this.rowCount = i + 1;
                this.addMemberRow = i;
                i = this.rowCount;
                this.rowCount = i + 1;
                this.emptyRowChat2 = i;
                this.rowCount += this.info.participants.participants.size();
                this.membersEndRow = this.rowCount;
            }
        }
    }

    private void updateProfileData() {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Unknown predecessor block by arg (r16_4 'rightIcon' android.graphics.drawable.Drawable) in PHI: PHI: (r16_3 'rightIcon' android.graphics.drawable.Drawable) = (r16_1 'rightIcon' android.graphics.drawable.Drawable), (r16_2 'rightIcon' android.graphics.drawable.Drawable), (r16_0 'rightIcon' android.graphics.drawable.Drawable), (r16_4 'rightIcon' android.graphics.drawable.Drawable) binds: {(r16_1 'rightIcon' android.graphics.drawable.Drawable)=B:73:0x0280, (r16_2 'rightIcon' android.graphics.drawable.Drawable)=B:83:0x02f5, (r16_0 'rightIcon' android.graphics.drawable.Drawable)=B:85:0x02fe, (r16_4 'rightIcon' android.graphics.drawable.Drawable)=B:86:0x0300}
	at jadx.core.dex.instructions.PhiInsn.replaceArg(PhiInsn.java:79)
	at jadx.core.dex.visitors.ModVisitor.processInvoke(ModVisitor.java:222)
	at jadx.core.dex.visitors.ModVisitor.replaceStep(ModVisitor.java:83)
	at jadx.core.dex.visitors.ModVisitor.visit(ModVisitor.java:68)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:60)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
        /*
        r25 = this;
        r0 = r25;
        r0 = r0.avatarImage;
        r19 = r0;
        if (r19 == 0) goto L_0x0010;
    L_0x0008:
        r0 = r25;
        r0 = r0.nameTextView;
        r19 = r0;
        if (r19 != 0) goto L_0x0011;
    L_0x0010:
        return;
    L_0x0011:
        r0 = r25;
        r0 = r0.currentAccount;
        r19 = r0;
        r19 = org.telegram.tgnet.ConnectionsManager.getInstance(r19);
        r7 = r19.getConnectionState();
        r19 = 2;
        r0 = r19;
        if (r7 != r0) goto L_0x00d8;
    L_0x0025:
        r19 = "WaitingForNetwork";
        r20 = NUM; // 0x7f0c06ea float:1.8612782E38 double:1.053098273E-314;
        r11 = org.telegram.messenger.LocaleController.getString(r19, r20);
    L_0x002f:
        r0 = r25;
        r0 = r0.user_id;
        r19 = r0;
        if (r19 == 0) goto L_0x0333;
    L_0x0037:
        r0 = r25;
        r0 = r0.currentAccount;
        r19 = r0;
        r19 = org.telegram.messenger.MessagesController.getInstance(r19);
        r0 = r25;
        r0 = r0.user_id;
        r20 = r0;
        r20 = java.lang.Integer.valueOf(r20);
        r18 = r19.getUser(r20);
        r13 = 0;
        r14 = 0;
        r0 = r18;
        r0 = r0.photo;
        r19 = r0;
        if (r19 == 0) goto L_0x006d;
    L_0x0059:
        r0 = r18;
        r0 = r0.photo;
        r19 = r0;
        r0 = r19;
        r13 = r0.photo_small;
        r0 = r18;
        r0 = r0.photo;
        r19 = r0;
        r0 = r19;
        r14 = r0.photo_big;
    L_0x006d:
        r0 = r25;
        r0 = r0.avatarDrawable;
        r19 = r0;
        r0 = r19;
        r1 = r18;
        r0.setInfo(r1);
        r0 = r25;
        r0 = r0.avatarImage;
        r19 = r0;
        r20 = "50_50";
        r0 = r25;
        r0 = r0.avatarDrawable;
        r21 = r0;
        r0 = r19;
        r1 = r20;
        r2 = r21;
        r0.setImage(r13, r1, r2);
        r9 = org.telegram.messenger.UserObject.getUserName(r18);
        r0 = r18;
        r0 = r0.id;
        r19 = r0;
        r0 = r25;
        r0 = r0.currentAccount;
        r20 = r0;
        r20 = org.telegram.messenger.UserConfig.getInstance(r20);
        r20 = r20.getClientUserId();
        r0 = r19;
        r1 = r20;
        if (r0 != r1) goto L_0x0111;
    L_0x00b0:
        r19 = "ChatYourSelf";
        r20 = NUM; // 0x7f0c0173 float:1.8609944E38 double:1.053097582E-314;
        r10 = org.telegram.messenger.LocaleController.getString(r19, r20);
        r19 = "ChatYourSelfName";
        r20 = NUM; // 0x7f0c0178 float:1.8609955E38 double:1.053097584E-314;
        r9 = org.telegram.messenger.LocaleController.getString(r19, r20);
    L_0x00c4:
        r4 = 0;
    L_0x00c5:
        r19 = 2;
        r0 = r19;
        if (r4 >= r0) goto L_0x0311;
    L_0x00cb:
        r0 = r25;
        r0 = r0.nameTextView;
        r19 = r0;
        r19 = r19[r4];
        if (r19 != 0) goto L_0x015e;
    L_0x00d5:
        r4 = r4 + 1;
        goto L_0x00c5;
    L_0x00d8:
        r19 = 1;
        r0 = r19;
        if (r7 != r0) goto L_0x00ea;
    L_0x00de:
        r19 = "Connecting";
        r20 = NUM; // 0x7f0c01a6 float:1.8610048E38 double:1.053097607E-314;
        r11 = org.telegram.messenger.LocaleController.getString(r19, r20);
        goto L_0x002f;
    L_0x00ea:
        r19 = 5;
        r0 = r19;
        if (r7 != r0) goto L_0x00fc;
    L_0x00f0:
        r19 = "Updating";
        r20 = NUM; // 0x7f0c0684 float:1.8612575E38 double:1.0530982226E-314;
        r11 = org.telegram.messenger.LocaleController.getString(r19, r20);
        goto L_0x002f;
    L_0x00fc:
        r19 = 4;
        r0 = r19;
        if (r7 != r0) goto L_0x010e;
    L_0x0102:
        r19 = "ConnectingToProxy";
        r20 = NUM; // 0x7f0c01a8 float:1.8610052E38 double:1.053097608E-314;
        r11 = org.telegram.messenger.LocaleController.getString(r19, r20);
        goto L_0x002f;
    L_0x010e:
        r11 = 0;
        goto L_0x002f;
    L_0x0111:
        r0 = r18;
        r0 = r0.id;
        r19 = r0;
        r20 = 333000; // 0x514c8 float:4.66632E-40 double:1.64524E-318;
        r0 = r19;
        r1 = r20;
        if (r0 == r1) goto L_0x012f;
    L_0x0120:
        r0 = r18;
        r0 = r0.id;
        r19 = r0;
        r20 = 777000; // 0xbdb28 float:1.088809E-39 double:3.83889E-318;
        r0 = r19;
        r1 = r20;
        if (r0 != r1) goto L_0x013a;
    L_0x012f:
        r19 = "ServiceNotifications";
        r20 = NUM; // 0x7f0c05e1 float:1.8612244E38 double:1.053098142E-314;
        r10 = org.telegram.messenger.LocaleController.getString(r19, r20);
        goto L_0x00c4;
    L_0x013a:
        r0 = r25;
        r0 = r0.isBot;
        r19 = r0;
        if (r19 == 0) goto L_0x014e;
    L_0x0142:
        r19 = "Bot";
        r20 = NUM; // 0x7f0c00e0 float:1.8609646E38 double:1.053097509E-314;
        r10 = org.telegram.messenger.LocaleController.getString(r19, r20);
        goto L_0x00c4;
    L_0x014e:
        r0 = r25;
        r0 = r0.currentAccount;
        r19 = r0;
        r0 = r19;
        r1 = r18;
        r10 = org.telegram.messenger.LocaleController.formatUserStatus(r0, r1);
        goto L_0x00c4;
    L_0x015e:
        if (r4 != 0) goto L_0x02a0;
    L_0x0160:
        r0 = r18;
        r0 = r0.id;
        r19 = r0;
        r0 = r25;
        r0 = r0.currentAccount;
        r20 = r0;
        r20 = org.telegram.messenger.UserConfig.getInstance(r20);
        r20 = r20.getClientUserId();
        r0 = r19;
        r1 = r20;
        if (r0 == r1) goto L_0x02a0;
    L_0x017a:
        r0 = r18;
        r0 = r0.id;
        r19 = r0;
        r0 = r19;
        r0 = r0 / 1000;
        r19 = r0;
        r20 = 777; // 0x309 float:1.089E-42 double:3.84E-321;
        r0 = r19;
        r1 = r20;
        if (r0 == r1) goto L_0x02a0;
    L_0x018e:
        r0 = r18;
        r0 = r0.id;
        r19 = r0;
        r0 = r19;
        r0 = r0 / 1000;
        r19 = r0;
        r20 = 333; // 0x14d float:4.67E-43 double:1.645E-321;
        r0 = r19;
        r1 = r20;
        if (r0 == r1) goto L_0x02a0;
    L_0x01a2:
        r0 = r18;
        r0 = r0.phone;
        r19 = r0;
        if (r19 == 0) goto L_0x02a0;
    L_0x01aa:
        r0 = r18;
        r0 = r0.phone;
        r19 = r0;
        r19 = r19.length();
        if (r19 == 0) goto L_0x02a0;
    L_0x01b6:
        r0 = r25;
        r0 = r0.currentAccount;
        r19 = r0;
        r19 = org.telegram.messenger.ContactsController.getInstance(r19);
        r0 = r19;
        r0 = r0.contactsDict;
        r19 = r0;
        r0 = r18;
        r0 = r0.id;
        r20 = r0;
        r20 = java.lang.Integer.valueOf(r20);
        r19 = r19.get(r20);
        if (r19 != 0) goto L_0x02a0;
    L_0x01d6:
        r0 = r25;
        r0 = r0.currentAccount;
        r19 = r0;
        r19 = org.telegram.messenger.ContactsController.getInstance(r19);
        r0 = r19;
        r0 = r0.contactsDict;
        r19 = r0;
        r19 = r19.size();
        if (r19 != 0) goto L_0x01fc;
    L_0x01ec:
        r0 = r25;
        r0 = r0.currentAccount;
        r19 = r0;
        r19 = org.telegram.messenger.ContactsController.getInstance(r19);
        r19 = r19.isLoadingContacts();
        if (r19 != 0) goto L_0x02a0;
    L_0x01fc:
        r19 = org.telegram.PhoneFormat.PhoneFormat.getInstance();
        r20 = new java.lang.StringBuilder;
        r20.<init>();
        r21 = "+";
        r20 = r20.append(r21);
        r0 = r18;
        r0 = r0.phone;
        r21 = r0;
        r20 = r20.append(r21);
        r20 = r20.toString();
        r12 = r19.format(r20);
        r0 = r25;
        r0 = r0.nameTextView;
        r19 = r0;
        r19 = r19[r4];
        r19 = r19.getText();
        r0 = r19;
        r19 = r0.equals(r12);
        if (r19 != 0) goto L_0x023f;
    L_0x0232:
        r0 = r25;
        r0 = r0.nameTextView;
        r19 = r0;
        r19 = r19[r4];
        r0 = r19;
        r0.setText(r12);
    L_0x023f:
        if (r4 != 0) goto L_0x02c3;
    L_0x0241:
        if (r11 == 0) goto L_0x02c3;
    L_0x0243:
        r0 = r25;
        r0 = r0.onlineTextView;
        r19 = r0;
        r19 = r19[r4];
        r0 = r19;
        r0.setText(r11);
    L_0x0250:
        r0 = r25;
        r0 = r0.currentEncryptedChat;
        r19 = r0;
        if (r19 == 0) goto L_0x02e6;
    L_0x0258:
        r8 = org.telegram.ui.ActionBar.Theme.chat_lockIconDrawable;
    L_0x025a:
        r16 = 0;
        if (r4 != 0) goto L_0x02f8;
    L_0x025e:
        r0 = r25;
        r0 = r0.currentAccount;
        r19 = r0;
        r19 = org.telegram.messenger.MessagesController.getInstance(r19);
        r0 = r25;
        r0 = r0.dialog_id;
        r20 = r0;
        r22 = 0;
        r20 = (r20 > r22 ? 1 : (r20 == r22 ? 0 : -1));
        if (r20 == 0) goto L_0x02e9;
    L_0x0274:
        r0 = r25;
        r0 = r0.dialog_id;
        r20 = r0;
    L_0x027a:
        r19 = r19.isDialogMuted(r20);
        if (r19 == 0) goto L_0x02f5;
    L_0x0280:
        r16 = org.telegram.ui.ActionBar.Theme.chat_muteIconDrawable;
    L_0x0282:
        r0 = r25;
        r0 = r0.nameTextView;
        r19 = r0;
        r19 = r19[r4];
        r0 = r19;
        r0.setLeftDrawable(r8);
        r0 = r25;
        r0 = r0.nameTextView;
        r19 = r0;
        r19 = r19[r4];
        r0 = r19;
        r1 = r16;
        r0.setRightDrawable(r1);
        goto L_0x00d5;
    L_0x02a0:
        r0 = r25;
        r0 = r0.nameTextView;
        r19 = r0;
        r19 = r19[r4];
        r19 = r19.getText();
        r0 = r19;
        r19 = r0.equals(r9);
        if (r19 != 0) goto L_0x023f;
    L_0x02b4:
        r0 = r25;
        r0 = r0.nameTextView;
        r19 = r0;
        r19 = r19[r4];
        r0 = r19;
        r0.setText(r9);
        goto L_0x023f;
    L_0x02c3:
        r0 = r25;
        r0 = r0.onlineTextView;
        r19 = r0;
        r19 = r19[r4];
        r19 = r19.getText();
        r0 = r19;
        r19 = r0.equals(r10);
        if (r19 != 0) goto L_0x0250;
    L_0x02d7:
        r0 = r25;
        r0 = r0.onlineTextView;
        r19 = r0;
        r19 = r19[r4];
        r0 = r19;
        r0.setText(r10);
        goto L_0x0250;
    L_0x02e6:
        r8 = 0;
        goto L_0x025a;
    L_0x02e9:
        r0 = r25;
        r0 = r0.user_id;
        r20 = r0;
        r0 = r20;
        r0 = (long) r0;
        r20 = r0;
        goto L_0x027a;
    L_0x02f5:
        r16 = 0;
        goto L_0x0282;
    L_0x02f8:
        r0 = r18;
        r0 = r0.verified;
        r19 = r0;
        if (r19 == 0) goto L_0x0282;
    L_0x0300:
        r16 = new org.telegram.ui.Components.CombinedDrawable;
        r19 = org.telegram.ui.ActionBar.Theme.profile_verifiedDrawable;
        r20 = org.telegram.ui.ActionBar.Theme.profile_verifiedCheckDrawable;
        r0 = r16;
        r1 = r19;
        r2 = r20;
        r0.<init>(r1, r2);
        goto L_0x0282;
    L_0x0311:
        r0 = r25;
        r0 = r0.avatarImage;
        r19 = r0;
        r20 = r19.getImageReceiver();
        r19 = org.telegram.ui.PhotoViewer.isShowingImage(r14);
        if (r19 != 0) goto L_0x0330;
    L_0x0321:
        r19 = 1;
    L_0x0323:
        r21 = 0;
        r0 = r20;
        r1 = r19;
        r2 = r21;
        r0.setVisible(r1, r2);
        goto L_0x0010;
    L_0x0330:
        r19 = 0;
        goto L_0x0323;
    L_0x0333:
        r0 = r25;
        r0 = r0.chat_id;
        r19 = r0;
        if (r19 == 0) goto L_0x0010;
    L_0x033b:
        r0 = r25;
        r0 = r0.currentAccount;
        r19 = r0;
        r19 = org.telegram.messenger.MessagesController.getInstance(r19);
        r0 = r25;
        r0 = r0.chat_id;
        r20 = r0;
        r20 = java.lang.Integer.valueOf(r20);
        r5 = r19.getChat(r20);
        if (r5 == 0) goto L_0x03cf;
    L_0x0355:
        r0 = r25;
        r0.currentChat = r5;
    L_0x0359:
        r19 = org.telegram.messenger.ChatObject.isChannel(r5);
        if (r19 == 0) goto L_0x0510;
    L_0x035f:
        r0 = r25;
        r0 = r0.info;
        r19 = r0;
        if (r19 == 0) goto L_0x039f;
    L_0x0367:
        r0 = r25;
        r0 = r0.currentChat;
        r19 = r0;
        r0 = r19;
        r0 = r0.megagroup;
        r19 = r0;
        if (r19 != 0) goto L_0x03fa;
    L_0x0375:
        r0 = r25;
        r0 = r0.info;
        r19 = r0;
        r0 = r19;
        r0 = r0.participants_count;
        r19 = r0;
        if (r19 == 0) goto L_0x039f;
    L_0x0383:
        r0 = r25;
        r0 = r0.currentChat;
        r19 = r0;
        r0 = r19;
        r0 = r0.admin;
        r19 = r0;
        if (r19 != 0) goto L_0x039f;
    L_0x0391:
        r0 = r25;
        r0 = r0.info;
        r19 = r0;
        r0 = r19;
        r0 = r0.can_view_participants;
        r19 = r0;
        if (r19 == 0) goto L_0x03fa;
    L_0x039f:
        r0 = r25;
        r0 = r0.currentChat;
        r19 = r0;
        r0 = r19;
        r0 = r0.megagroup;
        r19 = r0;
        if (r19 == 0) goto L_0x03d4;
    L_0x03ad:
        r19 = "Loading";
        r20 = NUM; // 0x7f0c0383 float:1.8611015E38 double:1.0530978426E-314;
        r19 = org.telegram.messenger.LocaleController.getString(r19, r20);
        r9 = r19.toLowerCase();
    L_0x03bb:
        r4 = 0;
    L_0x03bc:
        r19 = 2;
        r0 = r19;
        if (r4 >= r0) goto L_0x0780;
    L_0x03c2:
        r0 = r25;
        r0 = r0.nameTextView;
        r19 = r0;
        r19 = r19[r4];
        if (r19 != 0) goto L_0x057a;
    L_0x03cc:
        r4 = r4 + 1;
        goto L_0x03bc;
    L_0x03cf:
        r0 = r25;
        r5 = r0.currentChat;
        goto L_0x0359;
    L_0x03d4:
        r0 = r5.flags;
        r19 = r0;
        r19 = r19 & 64;
        if (r19 == 0) goto L_0x03eb;
    L_0x03dc:
        r19 = "ChannelPublic";
        r20 = NUM; // 0x7f0c0152 float:1.8609877E38 double:1.0530975655E-314;
        r19 = org.telegram.messenger.LocaleController.getString(r19, r20);
        r9 = r19.toLowerCase();
        goto L_0x03bb;
    L_0x03eb:
        r19 = "ChannelPrivate";
        r20 = NUM; // 0x7f0c014f float:1.8609871E38 double:1.053097564E-314;
        r19 = org.telegram.messenger.LocaleController.getString(r19, r20);
        r9 = r19.toLowerCase();
        goto L_0x03bb;
    L_0x03fa:
        r0 = r25;
        r0 = r0.currentChat;
        r19 = r0;
        r0 = r19;
        r0 = r0.megagroup;
        r19 = r0;
        if (r19 == 0) goto L_0x0486;
    L_0x0408:
        r0 = r25;
        r0 = r0.info;
        r19 = r0;
        r0 = r19;
        r0 = r0.participants_count;
        r19 = r0;
        r20 = 200; // 0xc8 float:2.8E-43 double:9.9E-322;
        r0 = r19;
        r1 = r20;
        if (r0 > r1) goto L_0x0486;
    L_0x041c:
        r0 = r25;
        r0 = r0.onlineCount;
        r19 = r0;
        r20 = 1;
        r0 = r19;
        r1 = r20;
        if (r0 <= r1) goto L_0x0471;
    L_0x042a:
        r0 = r25;
        r0 = r0.info;
        r19 = r0;
        r0 = r19;
        r0 = r0.participants_count;
        r19 = r0;
        if (r19 == 0) goto L_0x0471;
    L_0x0438:
        r19 = "%s, %s";
        r20 = 2;
        r0 = r20;
        r0 = new java.lang.Object[r0];
        r20 = r0;
        r21 = 0;
        r22 = "Members";
        r0 = r25;
        r0 = r0.info;
        r23 = r0;
        r0 = r23;
        r0 = r0.participants_count;
        r23 = r0;
        r22 = org.telegram.messenger.LocaleController.formatPluralString(r22, r23);
        r20[r21] = r22;
        r21 = 1;
        r22 = "OnlineCount";
        r0 = r25;
        r0 = r0.onlineCount;
        r23 = r0;
        r22 = org.telegram.messenger.LocaleController.formatPluralString(r22, r23);
        r20[r21] = r22;
        r9 = java.lang.String.format(r19, r20);
        goto L_0x03bb;
    L_0x0471:
        r19 = "Members";
        r0 = r25;
        r0 = r0.info;
        r20 = r0;
        r0 = r20;
        r0 = r0.participants_count;
        r20 = r0;
        r9 = org.telegram.messenger.LocaleController.formatPluralString(r19, r20);
        goto L_0x03bb;
    L_0x0486:
        r19 = 1;
        r0 = r19;
        r15 = new int[r0];
        r0 = r25;
        r0 = r0.info;
        r19 = r0;
        r0 = r19;
        r0 = r0.participants_count;
        r19 = r0;
        r0 = r19;
        r17 = org.telegram.messenger.LocaleController.formatShortNumber(r0, r15);
        r0 = r25;
        r0 = r0.currentChat;
        r19 = r0;
        r0 = r19;
        r0 = r0.megagroup;
        r19 = r0;
        if (r19 == 0) goto L_0x04de;
    L_0x04ac:
        r19 = "Members";
        r20 = 0;
        r20 = r15[r20];
        r19 = org.telegram.messenger.LocaleController.formatPluralString(r19, r20);
        r20 = "%d";
        r21 = 1;
        r0 = r21;
        r0 = new java.lang.Object[r0];
        r21 = r0;
        r22 = 0;
        r23 = 0;
        r23 = r15[r23];
        r23 = java.lang.Integer.valueOf(r23);
        r21[r22] = r23;
        r20 = java.lang.String.format(r20, r21);
        r0 = r19;
        r1 = r20;
        r2 = r17;
        r9 = r0.replace(r1, r2);
        goto L_0x03bb;
    L_0x04de:
        r19 = "Subscribers";
        r20 = 0;
        r20 = r15[r20];
        r19 = org.telegram.messenger.LocaleController.formatPluralString(r19, r20);
        r20 = "%d";
        r21 = 1;
        r0 = r21;
        r0 = new java.lang.Object[r0];
        r21 = r0;
        r22 = 0;
        r23 = 0;
        r23 = r15[r23];
        r23 = java.lang.Integer.valueOf(r23);
        r21[r22] = r23;
        r20 = java.lang.String.format(r20, r21);
        r0 = r19;
        r1 = r20;
        r2 = r17;
        r9 = r0.replace(r1, r2);
        goto L_0x03bb;
    L_0x0510:
        r6 = r5.participants_count;
        r0 = r25;
        r0 = r0.info;
        r19 = r0;
        if (r19 == 0) goto L_0x0530;
    L_0x051a:
        r0 = r25;
        r0 = r0.info;
        r19 = r0;
        r0 = r19;
        r0 = r0.participants;
        r19 = r0;
        r0 = r19;
        r0 = r0.participants;
        r19 = r0;
        r6 = r19.size();
    L_0x0530:
        if (r6 == 0) goto L_0x056f;
    L_0x0532:
        r0 = r25;
        r0 = r0.onlineCount;
        r19 = r0;
        r20 = 1;
        r0 = r19;
        r1 = r20;
        if (r0 <= r1) goto L_0x056f;
    L_0x0540:
        r19 = "%s, %s";
        r20 = 2;
        r0 = r20;
        r0 = new java.lang.Object[r0];
        r20 = r0;
        r21 = 0;
        r22 = "Members";
        r0 = r22;
        r22 = org.telegram.messenger.LocaleController.formatPluralString(r0, r6);
        r20[r21] = r22;
        r21 = 1;
        r22 = "OnlineCount";
        r0 = r25;
        r0 = r0.onlineCount;
        r23 = r0;
        r22 = org.telegram.messenger.LocaleController.formatPluralString(r22, r23);
        r20[r21] = r22;
        r9 = java.lang.String.format(r19, r20);
        goto L_0x03bb;
    L_0x056f:
        r19 = "Members";
        r0 = r19;
        r9 = org.telegram.messenger.LocaleController.formatPluralString(r0, r6);
        goto L_0x03bb;
    L_0x057a:
        r0 = r5.title;
        r19 = r0;
        if (r19 == 0) goto L_0x05a5;
    L_0x0580:
        r0 = r25;
        r0 = r0.nameTextView;
        r19 = r0;
        r19 = r19[r4];
        r19 = r19.getText();
        r0 = r5.title;
        r20 = r0;
        r19 = r19.equals(r20);
        if (r19 != 0) goto L_0x05a5;
    L_0x0596:
        r0 = r25;
        r0 = r0.nameTextView;
        r19 = r0;
        r19 = r19[r4];
        r0 = r5.title;
        r20 = r0;
        r19.setText(r20);
    L_0x05a5:
        r0 = r25;
        r0 = r0.nameTextView;
        r19 = r0;
        r19 = r19[r4];
        r20 = 0;
        r19.setLeftDrawable(r20);
        if (r4 == 0) goto L_0x05ef;
    L_0x05b4:
        r0 = r5.verified;
        r19 = r0;
        if (r19 == 0) goto L_0x05e1;
    L_0x05ba:
        r0 = r25;
        r0 = r0.nameTextView;
        r19 = r0;
        r19 = r19[r4];
        r20 = new org.telegram.ui.Components.CombinedDrawable;
        r21 = org.telegram.ui.ActionBar.Theme.profile_verifiedDrawable;
        r22 = org.telegram.ui.ActionBar.Theme.profile_verifiedCheckDrawable;
        r20.<init>(r21, r22);
        r19.setRightDrawable(r20);
    L_0x05ce:
        if (r4 != 0) goto L_0x0628;
    L_0x05d0:
        if (r11 == 0) goto L_0x0628;
    L_0x05d2:
        r0 = r25;
        r0 = r0.onlineTextView;
        r19 = r0;
        r19 = r19[r4];
        r0 = r19;
        r0.setText(r11);
        goto L_0x03cc;
    L_0x05e1:
        r0 = r25;
        r0 = r0.nameTextView;
        r19 = r0;
        r19 = r19[r4];
        r20 = 0;
        r19.setRightDrawable(r20);
        goto L_0x05ce;
    L_0x05ef:
        r0 = r25;
        r0 = r0.nameTextView;
        r19 = r0;
        r20 = r19[r4];
        r0 = r25;
        r0 = r0.currentAccount;
        r19 = r0;
        r19 = org.telegram.messenger.MessagesController.getInstance(r19);
        r0 = r25;
        r0 = r0.chat_id;
        r21 = r0;
        r0 = r21;
        r0 = -r0;
        r21 = r0;
        r0 = r21;
        r0 = (long) r0;
        r22 = r0;
        r0 = r19;
        r1 = r22;
        r19 = r0.isDialogMuted(r1);
        if (r19 == 0) goto L_0x0625;
    L_0x061b:
        r19 = org.telegram.ui.ActionBar.Theme.chat_muteIconDrawable;
    L_0x061d:
        r0 = r20;
        r1 = r19;
        r0.setRightDrawable(r1);
        goto L_0x05ce;
    L_0x0625:
        r19 = 0;
        goto L_0x061d;
    L_0x0628:
        r0 = r25;
        r0 = r0.currentChat;
        r19 = r0;
        r0 = r19;
        r0 = r0.megagroup;
        r19 = r0;
        if (r19 == 0) goto L_0x067d;
    L_0x0636:
        r0 = r25;
        r0 = r0.info;
        r19 = r0;
        if (r19 == 0) goto L_0x067d;
    L_0x063e:
        r0 = r25;
        r0 = r0.info;
        r19 = r0;
        r0 = r19;
        r0 = r0.participants_count;
        r19 = r0;
        r20 = 200; // 0xc8 float:2.8E-43 double:9.9E-322;
        r0 = r19;
        r1 = r20;
        if (r0 > r1) goto L_0x067d;
    L_0x0652:
        r0 = r25;
        r0 = r0.onlineCount;
        r19 = r0;
        if (r19 <= 0) goto L_0x067d;
    L_0x065a:
        r0 = r25;
        r0 = r0.onlineTextView;
        r19 = r0;
        r19 = r19[r4];
        r19 = r19.getText();
        r0 = r19;
        r19 = r0.equals(r9);
        if (r19 != 0) goto L_0x03cc;
    L_0x066e:
        r0 = r25;
        r0 = r0.onlineTextView;
        r19 = r0;
        r19 = r19[r4];
        r0 = r19;
        r0.setText(r9);
        goto L_0x03cc;
    L_0x067d:
        if (r4 != 0) goto L_0x075d;
    L_0x067f:
        r0 = r25;
        r0 = r0.currentChat;
        r19 = r0;
        r19 = org.telegram.messenger.ChatObject.isChannel(r19);
        if (r19 == 0) goto L_0x075d;
    L_0x068b:
        r0 = r25;
        r0 = r0.info;
        r19 = r0;
        if (r19 == 0) goto L_0x075d;
    L_0x0693:
        r0 = r25;
        r0 = r0.info;
        r19 = r0;
        r0 = r19;
        r0 = r0.participants_count;
        r19 = r0;
        if (r19 == 0) goto L_0x075d;
    L_0x06a1:
        r0 = r25;
        r0 = r0.currentChat;
        r19 = r0;
        r0 = r19;
        r0 = r0.megagroup;
        r19 = r0;
        if (r19 != 0) goto L_0x06bd;
    L_0x06af:
        r0 = r25;
        r0 = r0.currentChat;
        r19 = r0;
        r0 = r19;
        r0 = r0.broadcast;
        r19 = r0;
        if (r19 == 0) goto L_0x075d;
    L_0x06bd:
        r19 = 1;
        r0 = r19;
        r15 = new int[r0];
        r0 = r25;
        r0 = r0.info;
        r19 = r0;
        r0 = r19;
        r0 = r0.participants_count;
        r19 = r0;
        r0 = r19;
        r17 = org.telegram.messenger.LocaleController.formatShortNumber(r0, r15);
        r0 = r25;
        r0 = r0.currentChat;
        r19 = r0;
        r0 = r19;
        r0 = r0.megagroup;
        r19 = r0;
        if (r19 == 0) goto L_0x0720;
    L_0x06e3:
        r0 = r25;
        r0 = r0.onlineTextView;
        r19 = r0;
        r19 = r19[r4];
        r20 = "Members";
        r21 = 0;
        r21 = r15[r21];
        r20 = org.telegram.messenger.LocaleController.formatPluralString(r20, r21);
        r21 = "%d";
        r22 = 1;
        r0 = r22;
        r0 = new java.lang.Object[r0];
        r22 = r0;
        r23 = 0;
        r24 = 0;
        r24 = r15[r24];
        r24 = java.lang.Integer.valueOf(r24);
        r22[r23] = r24;
        r21 = java.lang.String.format(r21, r22);
        r0 = r20;
        r1 = r21;
        r2 = r17;
        r20 = r0.replace(r1, r2);
        r19.setText(r20);
        goto L_0x03cc;
    L_0x0720:
        r0 = r25;
        r0 = r0.onlineTextView;
        r19 = r0;
        r19 = r19[r4];
        r20 = "Subscribers";
        r21 = 0;
        r21 = r15[r21];
        r20 = org.telegram.messenger.LocaleController.formatPluralString(r20, r21);
        r21 = "%d";
        r22 = 1;
        r0 = r22;
        r0 = new java.lang.Object[r0];
        r22 = r0;
        r23 = 0;
        r24 = 0;
        r24 = r15[r24];
        r24 = java.lang.Integer.valueOf(r24);
        r22[r23] = r24;
        r21 = java.lang.String.format(r21, r22);
        r0 = r20;
        r1 = r21;
        r2 = r17;
        r20 = r0.replace(r1, r2);
        r19.setText(r20);
        goto L_0x03cc;
    L_0x075d:
        r0 = r25;
        r0 = r0.onlineTextView;
        r19 = r0;
        r19 = r19[r4];
        r19 = r19.getText();
        r0 = r19;
        r19 = r0.equals(r9);
        if (r19 != 0) goto L_0x03cc;
    L_0x0771:
        r0 = r25;
        r0 = r0.onlineTextView;
        r19 = r0;
        r19 = r19[r4];
        r0 = r19;
        r0.setText(r9);
        goto L_0x03cc;
    L_0x0780:
        r13 = 0;
        r14 = 0;
        r0 = r5.photo;
        r19 = r0;
        if (r19 == 0) goto L_0x0798;
    L_0x0788:
        r0 = r5.photo;
        r19 = r0;
        r0 = r19;
        r13 = r0.photo_small;
        r0 = r5.photo;
        r19 = r0;
        r0 = r19;
        r14 = r0.photo_big;
    L_0x0798:
        r0 = r25;
        r0 = r0.avatarDrawable;
        r19 = r0;
        r0 = r19;
        r0.setInfo(r5);
        r0 = r25;
        r0 = r0.avatarImage;
        r19 = r0;
        r20 = "50_50";
        r0 = r25;
        r0 = r0.avatarDrawable;
        r21 = r0;
        r0 = r19;
        r1 = r20;
        r2 = r21;
        r0.setImage(r13, r1, r2);
        r0 = r25;
        r0 = r0.avatarImage;
        r19 = r0;
        r20 = r19.getImageReceiver();
        r19 = org.telegram.ui.PhotoViewer.isShowingImage(r14);
        if (r19 != 0) goto L_0x07da;
    L_0x07cb:
        r19 = 1;
    L_0x07cd:
        r21 = 0;
        r0 = r20;
        r1 = r19;
        r2 = r21;
        r0.setVisible(r1, r2);
        goto L_0x0010;
    L_0x07da:
        r19 = 0;
        goto L_0x07cd;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ProfileActivity.updateProfileData():void");
    }

    private void createActionBarMenu() {
        ActionBarMenu menu = this.actionBar.createMenu();
        menu.clearItems();
        this.animatingItem = null;
        ActionBarMenuItem item = null;
        if (this.user_id != 0) {
            if (UserConfig.getInstance(this.currentAccount).getClientUserId() != this.user_id) {
                TL_userFull userFull = MessagesController.getInstance(this.currentAccount).getUserFull(this.user_id);
                if (userFull != null && userFull.phone_calls_available) {
                    this.callItem = menu.addItem(15, (int) C0493R.drawable.ic_call_white_24dp);
                }
                if (ContactsController.getInstance(this.currentAccount).contactsDict.get(Integer.valueOf(this.user_id)) == null) {
                    User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.user_id));
                    if (user != null) {
                        item = menu.addItem(10, (int) C0493R.drawable.ic_ab_other);
                        if (this.isBot) {
                            if (!user.bot_nochats) {
                                item.addSubItem(9, LocaleController.getString("BotInvite", C0493R.string.BotInvite));
                            }
                            item.addSubItem(10, LocaleController.getString("BotShare", C0493R.string.BotShare));
                        }
                        if (user.phone != null && user.phone.length() != 0) {
                            String string;
                            item.addSubItem(1, LocaleController.getString("AddContact", C0493R.string.AddContact));
                            item.addSubItem(3, LocaleController.getString("ShareContact", C0493R.string.ShareContact));
                            if (this.userBlocked) {
                                string = LocaleController.getString("Unblock", C0493R.string.Unblock);
                            } else {
                                string = LocaleController.getString("BlockContact", C0493R.string.BlockContact);
                            }
                            item.addSubItem(2, string);
                        } else if (this.isBot) {
                            item.addSubItem(2, !this.userBlocked ? LocaleController.getString("BotStop", C0493R.string.BotStop) : LocaleController.getString("BotRestart", C0493R.string.BotRestart));
                        } else {
                            item.addSubItem(2, !this.userBlocked ? LocaleController.getString("BlockContact", C0493R.string.BlockContact) : LocaleController.getString("Unblock", C0493R.string.Unblock));
                        }
                    } else {
                        return;
                    }
                }
                item = menu.addItem(10, (int) C0493R.drawable.ic_ab_other);
                item.addSubItem(3, LocaleController.getString("ShareContact", C0493R.string.ShareContact));
                item.addSubItem(2, !this.userBlocked ? LocaleController.getString("BlockContact", C0493R.string.BlockContact) : LocaleController.getString("Unblock", C0493R.string.Unblock));
                item.addSubItem(4, LocaleController.getString("EditContact", C0493R.string.EditContact));
                item.addSubItem(5, LocaleController.getString("DeleteContact", C0493R.string.DeleteContact));
            } else {
                item = menu.addItem(10, (int) C0493R.drawable.ic_ab_other);
                item.addSubItem(3, LocaleController.getString("ShareContact", C0493R.string.ShareContact));
            }
        } else if (this.chat_id != 0) {
            if (this.chat_id > 0) {
                Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.chat_id));
                if (this.writeButton != null) {
                    boolean isChannel = ChatObject.isChannel(this.currentChat);
                    if ((!isChannel || ChatObject.canChangeChatInfo(this.currentChat)) && (isChannel || this.currentChat.admin || this.currentChat.creator || !this.currentChat.admins_enabled)) {
                        this.writeButton.setImageResource(C0493R.drawable.floating_camera);
                        this.writeButton.setPadding(0, 0, 0, 0);
                    } else {
                        this.writeButton.setImageResource(C0493R.drawable.floating_message);
                        this.writeButton.setPadding(0, AndroidUtilities.dp(3.0f), 0, 0);
                    }
                }
                if (ChatObject.isChannel(chat)) {
                    if (ChatObject.hasAdminRights(chat)) {
                        this.editItem = menu.addItem(12, (int) C0493R.drawable.menu_settings);
                        if (null == null) {
                            item = menu.addItem(10, (int) C0493R.drawable.ic_ab_other);
                        }
                        if (chat.megagroup) {
                            item.addSubItem(12, LocaleController.getString("ManageGroupMenu", C0493R.string.ManageGroupMenu));
                        } else {
                            item.addSubItem(12, LocaleController.getString("ManageChannelMenu", C0493R.string.ManageChannelMenu));
                        }
                    }
                    if (chat.megagroup) {
                        if (item == null) {
                            item = menu.addItem(10, (int) C0493R.drawable.ic_ab_other);
                        }
                        item.addSubItem(16, LocaleController.getString("SearchMembers", C0493R.string.SearchMembers));
                        if (!(chat.creator || chat.left || chat.kicked)) {
                            item.addSubItem(7, LocaleController.getString("LeaveMegaMenu", C0493R.string.LeaveMegaMenu));
                        }
                    }
                } else {
                    if (!chat.admins_enabled || chat.creator || chat.admin) {
                        this.editItem = menu.addItem(8, (int) C0493R.drawable.group_edit_profile);
                    }
                    item = menu.addItem(10, (int) C0493R.drawable.ic_ab_other);
                    if (chat.creator && this.chat_id > 0) {
                        item.addSubItem(11, LocaleController.getString("SetAdmins", C0493R.string.SetAdmins));
                    }
                    if (!chat.admins_enabled || chat.creator || chat.admin) {
                        item.addSubItem(8, LocaleController.getString("ChannelEdit", C0493R.string.ChannelEdit));
                    }
                    item.addSubItem(16, LocaleController.getString("SearchMembers", C0493R.string.SearchMembers));
                    if (chat.creator && (this.info == null || this.info.participants.participants.size() > 0)) {
                        item.addSubItem(13, LocaleController.getString("ConvertGroupMenu", C0493R.string.ConvertGroupMenu));
                    }
                    item.addSubItem(7, LocaleController.getString("DeleteAndExit", C0493R.string.DeleteAndExit));
                }
            } else {
                item = menu.addItem(10, (int) C0493R.drawable.ic_ab_other);
                item.addSubItem(8, LocaleController.getString("EditName", C0493R.string.EditName));
            }
        }
        if (item == null) {
            item = menu.addItem(10, (int) C0493R.drawable.ic_ab_other);
        }
        item.addSubItem(14, LocaleController.getString("AddShortcut", C0493R.string.AddShortcut));
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
            removeSelfFromStack();
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
                    VoIPHelper.startCall(user, getParentActivity(), MessagesController.getInstance(this.currentAccount).getUserFull(user.id));
                }
            }
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        ThemeDescriptionDelegate сellDelegate = new ThemeDescriptionDelegate() {
            public void didSetColor() {
                if (ProfileActivity.this.listView != null) {
                    int count = ProfileActivity.this.listView.getChildCount();
                    for (int a = 0; a < count; a++) {
                        View child = ProfileActivity.this.listView.getChildAt(a);
                        if (child instanceof UserCell) {
                            ((UserCell) child).update(0);
                        }
                    }
                }
            }
        };
        r10 = new ThemeDescription[92];
        r10[46] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider);
        r10[47] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow);
        r10[48] = new ThemeDescription(this.avatarImage, 0, null, null, new Drawable[]{Theme.avatar_photoDrawable, Theme.avatar_broadcastDrawable, Theme.avatar_savedDrawable}, null, Theme.key_avatar_text);
        r10[49] = new ThemeDescription(this.avatarImage, 0, null, null, new Drawable[]{this.avatarDrawable}, null, Theme.key_avatar_backgroundInProfileRed);
        r10[50] = new ThemeDescription(this.avatarImage, 0, null, null, new Drawable[]{this.avatarDrawable}, null, Theme.key_avatar_backgroundInProfileOrange);
        r10[51] = new ThemeDescription(this.avatarImage, 0, null, null, new Drawable[]{this.avatarDrawable}, null, Theme.key_avatar_backgroundInProfileViolet);
        r10[52] = new ThemeDescription(this.avatarImage, 0, null, null, new Drawable[]{this.avatarDrawable}, null, Theme.key_avatar_backgroundInProfileGreen);
        r10[53] = new ThemeDescription(this.avatarImage, 0, null, null, new Drawable[]{this.avatarDrawable}, null, Theme.key_avatar_backgroundInProfileCyan);
        r10[54] = new ThemeDescription(this.avatarImage, 0, null, null, new Drawable[]{this.avatarDrawable}, null, Theme.key_avatar_backgroundInProfileBlue);
        r10[55] = new ThemeDescription(this.avatarImage, 0, null, null, new Drawable[]{this.avatarDrawable}, null, Theme.key_avatar_backgroundInProfilePink);
        r10[56] = new ThemeDescription(this.writeButton, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, Theme.key_profile_actionIcon);
        r10[57] = new ThemeDescription(this.writeButton, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_profile_actionBackground);
        r10[58] = new ThemeDescription(this.writeButton, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, Theme.key_profile_actionPressedBackground);
        r10[59] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r10[60] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteGreenText2);
        r10[61] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteRedText5);
        r10[62] = new ThemeDescription(this.listView, 0, new Class[]{TextCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteValueText);
        r10[63] = new ThemeDescription(this.listView, 0, new Class[]{TextCell.class}, new String[]{"imageView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayIcon);
        r10[64] = new ThemeDescription(this.listView, 0, new Class[]{TextDetailCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r10[65] = new ThemeDescription(this.listView, 0, new Class[]{TextDetailCell.class}, new String[]{"valueImageView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayIcon);
        r10[66] = new ThemeDescription(this.listView, 0, new Class[]{TextDetailCell.class}, new String[]{"imageView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayIcon);
        r10[67] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{UserCell.class}, new String[]{"adminImage"}, null, null, null, Theme.key_profile_creatorIcon);
        r10[68] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{UserCell.class}, new String[]{"adminImage"}, null, null, null, Theme.key_profile_adminIcon);
        r10[69] = new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, new String[]{"nameTextView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r10[70] = new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, new String[]{"statusColor"}, null, null, сellDelegate, Theme.key_windowBackgroundWhiteGrayText);
        r10[71] = new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, new String[]{"statusOnlineColor"}, null, null, сellDelegate, Theme.key_windowBackgroundWhiteBlueText);
        r10[72] = new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, null, new Drawable[]{Theme.avatar_photoDrawable, Theme.avatar_broadcastDrawable, Theme.avatar_savedDrawable}, null, Theme.key_avatar_text);
        r10[73] = new ThemeDescription(null, 0, null, null, null, сellDelegate, Theme.key_avatar_backgroundRed);
        r10[74] = new ThemeDescription(null, 0, null, null, null, сellDelegate, Theme.key_avatar_backgroundOrange);
        r10[75] = new ThemeDescription(null, 0, null, null, null, сellDelegate, Theme.key_avatar_backgroundViolet);
        r10[76] = new ThemeDescription(null, 0, null, null, null, сellDelegate, Theme.key_avatar_backgroundGreen);
        r10[77] = new ThemeDescription(null, 0, null, null, null, сellDelegate, Theme.key_avatar_backgroundCyan);
        r10[78] = new ThemeDescription(null, 0, null, null, null, сellDelegate, Theme.key_avatar_backgroundBlue);
        r10[79] = new ThemeDescription(null, 0, null, null, null, сellDelegate, Theme.key_avatar_backgroundPink);
        r10[80] = new ThemeDescription(this.listView, 0, new Class[]{LoadingCell.class}, new String[]{"progressBar"}, null, null, null, Theme.key_progressCircle);
        r10[81] = new ThemeDescription(this.listView, 0, new Class[]{AboutLinkCell.class}, new String[]{"imageView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayIcon);
        r10[82] = new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{AboutLinkCell.class}, Theme.profile_aboutTextPaint, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r10[83] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LINKCOLOR, new Class[]{AboutLinkCell.class}, Theme.profile_aboutTextPaint, null, null, Theme.key_windowBackgroundWhiteLinkText);
        r10[84] = new ThemeDescription(this.listView, 0, new Class[]{AboutLinkCell.class}, Theme.linkSelectionPaint, null, null, Theme.key_windowBackgroundWhiteLinkSelection);
        r10[85] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow);
        r10[86] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{ShadowSectionCell.class}, null, null, null, Theme.key_windowBackgroundGray);
        r10[87] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow);
        r10[88] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextInfoPrivacyCell.class}, null, null, null, Theme.key_windowBackgroundGray);
        r10[89] = new ThemeDescription(this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText4);
        r10[90] = new ThemeDescription(this.nameTextView[1], 0, null, null, new Drawable[]{Theme.profile_verifiedCheckDrawable}, null, Theme.key_profile_verifiedCheck);
        r10[91] = new ThemeDescription(this.nameTextView[1], 0, null, null, new Drawable[]{Theme.profile_verifiedDrawable}, null, Theme.key_profile_verifiedBackground);
        return r10;
    }
}
