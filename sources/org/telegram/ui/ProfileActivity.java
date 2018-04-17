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
    private PhotoViewerProvider provider = new C23481();
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

    private class TopView extends View {
        private int currentColor;
        private Paint paint = new Paint();

        public TopView(Context context) {
            super(context);
        }

        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), (ActionBar.getCurrentActionBarHeight() + (ProfileActivity.this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0)) + AndroidUtilities.dp(91.0f));
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

    /* renamed from: org.telegram.ui.ProfileActivity$3 */
    class C22573 implements AvatarUpdaterDelegate {
        C22573() {
        }

        public void didUploadedPhoto(InputFile file, PhotoSize small, PhotoSize big) {
            if (ProfileActivity.this.chat_id != 0) {
                MessagesController.getInstance(ProfileActivity.this.currentAccount).changeChatAvatar(ProfileActivity.this.chat_id, file);
            }
        }
    }

    /* renamed from: org.telegram.ui.ProfileActivity$5 */
    class C22605 extends ActionBarMenuOnItemClick {

        /* renamed from: org.telegram.ui.ProfileActivity$5$1 */
        class C16441 implements OnClickListener {
            C16441() {
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                if (ProfileActivity.this.userBlocked) {
                    MessagesController.getInstance(ProfileActivity.this.currentAccount).unblockUser(ProfileActivity.this.user_id);
                } else {
                    MessagesController.getInstance(ProfileActivity.this.currentAccount).blockUser(ProfileActivity.this.user_id);
                }
            }
        }

        C22605() {
        }

        public void onItemClick(int id) {
            int i = id;
            if (ProfileActivity.this.getParentActivity() != null) {
                if (i == -1) {
                    ProfileActivity.this.finishFragment();
                } else if (i == 2) {
                    if (MessagesController.getInstance(ProfileActivity.this.currentAccount).getUser(Integer.valueOf(ProfileActivity.this.user_id)) != null) {
                        if (!ProfileActivity.this.isBot) {
                            builder = new Builder(ProfileActivity.this.getParentActivity());
                            if (ProfileActivity.this.userBlocked) {
                                builder.setMessage(LocaleController.getString("AreYouSureUnblockContact", R.string.AreYouSureUnblockContact));
                            } else {
                                builder.setMessage(LocaleController.getString("AreYouSureBlockContact", R.string.AreYouSureBlockContact));
                            }
                            builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                            builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new C16441());
                            builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                            ProfileActivity.this.showDialog(builder.create());
                        } else if (ProfileActivity.this.userBlocked) {
                            MessagesController.getInstance(ProfileActivity.this.currentAccount).unblockUser(ProfileActivity.this.user_id);
                            SendMessagesHelper.getInstance(ProfileActivity.this.currentAccount).sendMessage("/start", (long) ProfileActivity.this.user_id, null, null, false, null, null, null);
                            ProfileActivity.this.finishFragment();
                        } else {
                            MessagesController.getInstance(ProfileActivity.this.currentAccount).blockUser(ProfileActivity.this.user_id);
                        }
                    }
                } else if (i == 1) {
                    user = MessagesController.getInstance(ProfileActivity.this.currentAccount).getUser(Integer.valueOf(ProfileActivity.this.user_id));
                    Bundle args = new Bundle();
                    args.putInt("user_id", user.id);
                    args.putBoolean("addContact", true);
                    ProfileActivity.this.presentFragment(new ContactAddActivity(args));
                } else if (i == 3) {
                    args = new Bundle();
                    args.putBoolean("onlySelect", true);
                    args.putString("selectAlertString", LocaleController.getString("SendContactTo", R.string.SendContactTo));
                    args.putString("selectAlertStringGroup", LocaleController.getString("SendContactToGroup", R.string.SendContactToGroup));
                    fragment = new DialogsActivity(args);
                    fragment.setDelegate(ProfileActivity.this);
                    ProfileActivity.this.presentFragment(fragment);
                } else if (i == 4) {
                    args = new Bundle();
                    args.putInt("user_id", ProfileActivity.this.user_id);
                    ProfileActivity.this.presentFragment(new ContactAddActivity(args));
                } else if (i == 5) {
                    final User user = MessagesController.getInstance(ProfileActivity.this.currentAccount).getUser(Integer.valueOf(ProfileActivity.this.user_id));
                    if (user != null) {
                        if (ProfileActivity.this.getParentActivity() != null) {
                            builder = new Builder(ProfileActivity.this.getParentActivity());
                            builder.setMessage(LocaleController.getString("AreYouSureDeleteContact", R.string.AreYouSureDeleteContact));
                            builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                            builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new OnClickListener() {
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    ArrayList<User> arrayList = new ArrayList();
                                    arrayList.add(user);
                                    ContactsController.getInstance(ProfileActivity.this.currentAccount).deleteContact(arrayList);
                                }
                            });
                            builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                            ProfileActivity.this.showDialog(builder.create());
                        }
                    }
                } else if (i == 7) {
                    ProfileActivity.this.leaveChatPressed();
                } else if (i == 8) {
                    args = new Bundle();
                    args.putInt("chat_id", ProfileActivity.this.chat_id);
                    ProfileActivity.this.presentFragment(new ChangeChatNameActivity(args));
                } else if (i == 12) {
                    args = new Bundle();
                    args.putInt("chat_id", ProfileActivity.this.chat_id);
                    ChannelEditActivity fragment = new ChannelEditActivity(args);
                    fragment.setInfo(ProfileActivity.this.info);
                    ProfileActivity.this.presentFragment(fragment);
                } else if (i == 9) {
                    user = MessagesController.getInstance(ProfileActivity.this.currentAccount).getUser(Integer.valueOf(ProfileActivity.this.user_id));
                    if (user != null) {
                        Bundle args2 = new Bundle();
                        args2.putBoolean("onlySelect", true);
                        args2.putInt("dialogsType", 2);
                        args2.putString("addToGroupAlertString", LocaleController.formatString("AddToTheGroupTitle", R.string.AddToTheGroupTitle, UserObject.getUserName(user), "%1$s"));
                        fragment = new DialogsActivity(args2);
                        fragment.setDelegate(new DialogsActivityDelegate() {
                            public void didSelectDialogs(DialogsActivity fragment, ArrayList<Long> dids, CharSequence message, boolean param) {
                                long did = ((Long) dids.get(0)).longValue();
                                Bundle args = new Bundle();
                                args.putBoolean("scrollToTopOnResume", true);
                                args.putInt("chat_id", -((int) did));
                                if (MessagesController.getInstance(ProfileActivity.this.currentAccount).checkCanOpenChat(args, fragment)) {
                                    NotificationCenter.getInstance(ProfileActivity.this.currentAccount).removeObserver(ProfileActivity.this, NotificationCenter.closeChats);
                                    NotificationCenter.getInstance(ProfileActivity.this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                                    MessagesController.getInstance(ProfileActivity.this.currentAccount).addUserToChat(-((int) did), user, null, 0, null, ProfileActivity.this);
                                    ProfileActivity.this.presentFragment(new ChatActivity(args), true);
                                    ProfileActivity.this.removeSelfFromStack();
                                }
                            }
                        });
                        ProfileActivity.this.presentFragment(fragment);
                    }
                } else if (i == 10) {
                    try {
                        if (MessagesController.getInstance(ProfileActivity.this.currentAccount).getUser(Integer.valueOf(ProfileActivity.this.user_id)) != null) {
                            Intent intent = new Intent("android.intent.action.SEND");
                            intent.setType("text/plain");
                            TL_userFull userFull = MessagesController.getInstance(ProfileActivity.this.currentAccount).getUserFull(ProfileActivity.this.botInfo.user_id);
                            if (ProfileActivity.this.botInfo == null || userFull == null || TextUtils.isEmpty(userFull.about)) {
                                StringBuilder stringBuilder = new StringBuilder();
                                stringBuilder.append("https://");
                                stringBuilder.append(MessagesController.getInstance(ProfileActivity.this.currentAccount).linkPrefix);
                                stringBuilder.append("/%s");
                                intent.putExtra("android.intent.extra.TEXT", String.format(stringBuilder.toString(), new Object[]{user.username}));
                            } else {
                                StringBuilder stringBuilder2 = new StringBuilder();
                                stringBuilder2.append("%s https://");
                                stringBuilder2.append(MessagesController.getInstance(ProfileActivity.this.currentAccount).linkPrefix);
                                stringBuilder2.append("/%s");
                                intent.putExtra("android.intent.extra.TEXT", String.format(stringBuilder2.toString(), new Object[]{userFull.about, user.username}));
                            }
                            ProfileActivity.this.startActivityForResult(Intent.createChooser(intent, LocaleController.getString("BotShare", R.string.BotShare)), 500);
                        }
                    } catch (Throwable e) {
                        FileLog.m3e(e);
                    }
                } else if (i == 11) {
                    args = new Bundle();
                    args.putInt("chat_id", ProfileActivity.this.chat_id);
                    SetAdminsActivity fragment2 = new SetAdminsActivity(args);
                    fragment2.setChatInfo(ProfileActivity.this.info);
                    ProfileActivity.this.presentFragment(fragment2);
                } else if (i == 13) {
                    args = new Bundle();
                    args.putInt("chat_id", ProfileActivity.this.chat_id);
                    ProfileActivity.this.presentFragment(new ConvertGroupActivity(args));
                } else if (i == 14) {
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
                } else if (i == 15) {
                    user = MessagesController.getInstance(ProfileActivity.this.currentAccount).getUser(Integer.valueOf(ProfileActivity.this.user_id));
                    if (user != null) {
                        VoIPHelper.startCall(user, ProfileActivity.this.getParentActivity(), MessagesController.getInstance(ProfileActivity.this.currentAccount).getUserFull(user.id));
                    }
                } else if (i == 16) {
                    args = new Bundle();
                    args.putInt("chat_id", ProfileActivity.this.chat_id);
                    if (ChatObject.isChannel(ProfileActivity.this.currentChat)) {
                        args.putInt("type", 2);
                        args.putBoolean("open_search", true);
                        ProfileActivity.this.presentFragment(new ChannelUsersActivity(args));
                    } else {
                        ChatUsersActivity chatUsersActivity = new ChatUsersActivity(args);
                        chatUsersActivity.setInfo(ProfileActivity.this.info);
                        ProfileActivity.this.presentFragment(chatUsersActivity);
                    }
                }
            }
        }
    }

    /* renamed from: org.telegram.ui.ProfileActivity$9 */
    class C22619 implements OnItemClickListener {

        /* renamed from: org.telegram.ui.ProfileActivity$9$2 */
        class C16482 implements OnClickListener {
            C16482() {
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                ProfileActivity.this.creatingChat = true;
                SecretChatHelper.getInstance(ProfileActivity.this.currentAccount).startSecretChat(ProfileActivity.this.getParentActivity(), MessagesController.getInstance(ProfileActivity.this.currentAccount).getUser(Integer.valueOf(ProfileActivity.this.user_id)));
            }
        }

        /* renamed from: org.telegram.ui.ProfileActivity$9$3 */
        class C16493 implements OnClickListener {
            C16493() {
            }

            public void onClick(DialogInterface dialogInterface, int i) {
                MessagesController.getInstance(ProfileActivity.this.currentAccount).convertToMegaGroup(ProfileActivity.this.getParentActivity(), ProfileActivity.this.chat_id);
            }
        }

        C22619() {
        }

        public void onItemClick(View view, int position) {
            int i = position;
            if (ProfileActivity.this.getParentActivity() != null) {
                Bundle args;
                if (i == ProfileActivity.this.sharedMediaRow) {
                    args = new Bundle();
                    if (ProfileActivity.this.user_id != 0) {
                        args.putLong("dialog_id", ProfileActivity.this.dialog_id != 0 ? ProfileActivity.this.dialog_id : (long) ProfileActivity.this.user_id);
                    } else {
                        args.putLong("dialog_id", (long) (-ProfileActivity.this.chat_id));
                    }
                    MediaActivity fragment = new MediaActivity(args);
                    fragment.setChatInfo(ProfileActivity.this.info);
                    ProfileActivity.this.presentFragment(fragment);
                } else if (i == ProfileActivity.this.groupsInCommonRow) {
                    ProfileActivity.this.presentFragment(new CommonGroupsActivity(ProfileActivity.this.user_id));
                } else if (i == ProfileActivity.this.settingsKeyRow) {
                    args = new Bundle();
                    args.putInt("chat_id", (int) (ProfileActivity.this.dialog_id >> 32));
                    ProfileActivity.this.presentFragment(new IdenticonActivity(args));
                } else if (i == ProfileActivity.this.settingsTimerRow) {
                    ProfileActivity.this.showDialog(AlertsCreator.createTTLAlert(ProfileActivity.this.getParentActivity(), ProfileActivity.this.currentEncryptedChat).create());
                } else {
                    int i2 = 1;
                    if (i == ProfileActivity.this.settingsNotificationsRow) {
                        long did;
                        String[] strArr;
                        String[] descriptions;
                        int[] icons;
                        LinearLayout linearLayout;
                        int a;
                        TextView textView;
                        Drawable drawable;
                        Builder builder;
                        if (ProfileActivity.this.dialog_id != 0) {
                            did = ProfileActivity.this.dialog_id;
                        } else if (ProfileActivity.this.user_id != 0) {
                            did = (long) ProfileActivity.this.user_id;
                        } else {
                            did = (long) (-ProfileActivity.this.chat_id);
                            strArr = new String[5];
                            strArr[0] = LocaleController.getString("NotificationsTurnOn", R.string.NotificationsTurnOn);
                            strArr[1] = LocaleController.formatString("MuteFor", R.string.MuteFor, LocaleController.formatPluralString("Hours", 1));
                            strArr[2] = LocaleController.formatString("MuteFor", R.string.MuteFor, LocaleController.formatPluralString("Days", 2));
                            strArr[3] = LocaleController.getString("NotificationsCustomize", R.string.NotificationsCustomize);
                            strArr[4] = LocaleController.getString("NotificationsTurnOff", R.string.NotificationsTurnOff);
                            descriptions = strArr;
                            icons = new int[]{R.drawable.notifications_s_on, R.drawable.notifications_s_1h, R.drawable.notifications_s_2d, R.drawable.notifications_s_custom, R.drawable.notifications_s_off};
                            linearLayout = new LinearLayout(ProfileActivity.this.getParentActivity());
                            linearLayout.setOrientation(1);
                            a = 0;
                            while (a < descriptions.length) {
                                textView = new TextView(ProfileActivity.this.getParentActivity());
                                textView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
                                textView.setTextSize(i2, 16.0f);
                                textView.setLines(i2);
                                textView.setMaxLines(i2);
                                drawable = ProfileActivity.this.getParentActivity().getResources().getDrawable(icons[a]);
                                drawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_dialogIcon), Mode.MULTIPLY));
                                textView.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
                                textView.setTag(Integer.valueOf(a));
                                textView.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                                textView.setPadding(AndroidUtilities.dp(24.0f), 0, AndroidUtilities.dp(24.0f), 0);
                                textView.setSingleLine(true);
                                textView.setGravity(19);
                                textView.setCompoundDrawablePadding(AndroidUtilities.dp(26.0f));
                                textView.setText(descriptions[a]);
                                linearLayout.addView(textView, LayoutHelper.createLinear(-1, 48, 51));
                                textView.setOnClickListener(new View.OnClickListener() {
                                    public void onClick(View v) {
                                        int i = ((Integer) v.getTag()).intValue();
                                        if (i == 0) {
                                            Editor editor = MessagesController.getNotificationsSettings(ProfileActivity.this.currentAccount).edit();
                                            StringBuilder stringBuilder = new StringBuilder();
                                            stringBuilder.append("notify2_");
                                            stringBuilder.append(did);
                                            editor.putInt(stringBuilder.toString(), 0);
                                            MessagesStorage.getInstance(ProfileActivity.this.currentAccount).setDialogFlags(did, 0);
                                            editor.commit();
                                            TL_dialog dialog = (TL_dialog) MessagesController.getInstance(ProfileActivity.this.currentAccount).dialogs_dict.get(did);
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
                                            Editor editor2 = MessagesController.getNotificationsSettings(ProfileActivity.this.currentAccount).edit();
                                            StringBuilder stringBuilder2;
                                            if (i == 4) {
                                                stringBuilder2 = new StringBuilder();
                                                stringBuilder2.append("notify2_");
                                                stringBuilder2.append(did);
                                                editor2.putInt(stringBuilder2.toString(), 2);
                                                flags = 1;
                                            } else {
                                                StringBuilder stringBuilder3 = new StringBuilder();
                                                stringBuilder3.append("notify2_");
                                                stringBuilder3.append(did);
                                                editor2.putInt(stringBuilder3.toString(), 3);
                                                stringBuilder2 = new StringBuilder();
                                                stringBuilder2.append("notifyuntil_");
                                                stringBuilder2.append(did);
                                                editor2.putInt(stringBuilder2.toString(), untilTime);
                                                flags = (((long) untilTime) << 32) | 1;
                                            }
                                            NotificationsController.getInstance(ProfileActivity.this.currentAccount).removeNotificationsForDialog(did);
                                            MessagesStorage.getInstance(ProfileActivity.this.currentAccount).setDialogFlags(did, flags);
                                            editor2.commit();
                                            TL_dialog dialog2 = (TL_dialog) MessagesController.getInstance(ProfileActivity.this.currentAccount).dialogs_dict.get(did);
                                            if (dialog2 != null) {
                                                dialog2.notify_settings = new TL_peerNotifySettings();
                                                dialog2.notify_settings.mute_until = untilTime;
                                            }
                                            NotificationsController.getInstance(ProfileActivity.this.currentAccount).updateServerNotificationsSettings(did);
                                        }
                                        ProfileActivity.this.listAdapter.notifyItemChanged(ProfileActivity.this.settingsNotificationsRow);
                                        ProfileActivity.this.dismissCurrentDialig();
                                    }
                                });
                                a++;
                                i2 = 1;
                            }
                            builder = new Builder(ProfileActivity.this.getParentActivity());
                            builder.setTitle(LocaleController.getString("Notifications", R.string.Notifications));
                            builder.setView(linearLayout);
                            ProfileActivity.this.showDialog(builder.create());
                        }
                        strArr = new String[5];
                        strArr[0] = LocaleController.getString("NotificationsTurnOn", R.string.NotificationsTurnOn);
                        strArr[1] = LocaleController.formatString("MuteFor", R.string.MuteFor, LocaleController.formatPluralString("Hours", 1));
                        strArr[2] = LocaleController.formatString("MuteFor", R.string.MuteFor, LocaleController.formatPluralString("Days", 2));
                        strArr[3] = LocaleController.getString("NotificationsCustomize", R.string.NotificationsCustomize);
                        strArr[4] = LocaleController.getString("NotificationsTurnOff", R.string.NotificationsTurnOff);
                        descriptions = strArr;
                        icons = new int[]{R.drawable.notifications_s_on, R.drawable.notifications_s_1h, R.drawable.notifications_s_2d, R.drawable.notifications_s_custom, R.drawable.notifications_s_off};
                        linearLayout = new LinearLayout(ProfileActivity.this.getParentActivity());
                        linearLayout.setOrientation(1);
                        a = 0;
                        while (a < descriptions.length) {
                            textView = new TextView(ProfileActivity.this.getParentActivity());
                            textView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
                            textView.setTextSize(i2, 16.0f);
                            textView.setLines(i2);
                            textView.setMaxLines(i2);
                            drawable = ProfileActivity.this.getParentActivity().getResources().getDrawable(icons[a]);
                            drawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_dialogIcon), Mode.MULTIPLY));
                            textView.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
                            textView.setTag(Integer.valueOf(a));
                            textView.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                            textView.setPadding(AndroidUtilities.dp(24.0f), 0, AndroidUtilities.dp(24.0f), 0);
                            textView.setSingleLine(true);
                            textView.setGravity(19);
                            textView.setCompoundDrawablePadding(AndroidUtilities.dp(26.0f));
                            textView.setText(descriptions[a]);
                            linearLayout.addView(textView, LayoutHelper.createLinear(-1, 48, 51));
                            textView.setOnClickListener(/* anonymous class already generated */);
                            a++;
                            i2 = 1;
                        }
                        builder = new Builder(ProfileActivity.this.getParentActivity());
                        builder.setTitle(LocaleController.getString("Notifications", R.string.Notifications));
                        builder.setView(linearLayout);
                        ProfileActivity.this.showDialog(builder.create());
                    } else if (i == ProfileActivity.this.startSecretChatRow) {
                        builder = new Builder(ProfileActivity.this.getParentActivity());
                        builder.setMessage(LocaleController.getString("AreYouSureSecretChat", R.string.AreYouSureSecretChat));
                        builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new C16482());
                        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                        ProfileActivity.this.showDialog(builder.create());
                    } else if (i > ProfileActivity.this.emptyRowChat2 && i < ProfileActivity.this.membersEndRow) {
                        int user_id;
                        if (ProfileActivity.this.sortedUsers.isEmpty()) {
                            user_id = ((ChatParticipant) ProfileActivity.this.info.participants.participants.get((i - ProfileActivity.this.emptyRowChat2) - 1)).user_id;
                        } else {
                            user_id = ((ChatParticipant) ProfileActivity.this.info.participants.participants.get(((Integer) ProfileActivity.this.sortedUsers.get((i - ProfileActivity.this.emptyRowChat2) - 1)).intValue())).user_id;
                        }
                        if (user_id != UserConfig.getInstance(ProfileActivity.this.currentAccount).getClientUserId()) {
                            Bundle args2 = new Bundle();
                            args2.putInt("user_id", user_id);
                            ProfileActivity.this.presentFragment(new ProfileActivity(args2));
                        }
                    } else if (i == ProfileActivity.this.addMemberRow) {
                        ProfileActivity.this.openAddMember();
                    } else if (i == ProfileActivity.this.channelNameRow) {
                        try {
                            Intent intent = new Intent("android.intent.action.SEND");
                            intent.setType("text/plain");
                            StringBuilder stringBuilder;
                            if (ProfileActivity.this.info.about == null || ProfileActivity.this.info.about.length() <= 0) {
                                stringBuilder = new StringBuilder();
                                stringBuilder.append(ProfileActivity.this.currentChat.title);
                                stringBuilder.append("\nhttps://");
                                stringBuilder.append(MessagesController.getInstance(ProfileActivity.this.currentAccount).linkPrefix);
                                stringBuilder.append("/");
                                stringBuilder.append(ProfileActivity.this.currentChat.username);
                                intent.putExtra("android.intent.extra.TEXT", stringBuilder.toString());
                            } else {
                                stringBuilder = new StringBuilder();
                                stringBuilder.append(ProfileActivity.this.currentChat.title);
                                stringBuilder.append("\n");
                                stringBuilder.append(ProfileActivity.this.info.about);
                                stringBuilder.append("\nhttps://");
                                stringBuilder.append(MessagesController.getInstance(ProfileActivity.this.currentAccount).linkPrefix);
                                stringBuilder.append("/");
                                stringBuilder.append(ProfileActivity.this.currentChat.username);
                                intent.putExtra("android.intent.extra.TEXT", stringBuilder.toString());
                            }
                            ProfileActivity.this.getParentActivity().startActivityForResult(Intent.createChooser(intent, LocaleController.getString("BotShare", R.string.BotShare)), 500);
                        } catch (Throwable e) {
                            FileLog.m3e(e);
                        }
                    } else if (i == ProfileActivity.this.leaveChannelRow) {
                        ProfileActivity.this.leaveChatPressed();
                    } else if (i == ProfileActivity.this.membersRow) {
                        args = new Bundle();
                        args.putInt("chat_id", ProfileActivity.this.chat_id);
                        args.putInt("type", 2);
                        ProfileActivity.this.presentFragment(new ChannelUsersActivity(args));
                    } else if (i == ProfileActivity.this.convertRow) {
                        builder = new Builder(ProfileActivity.this.getParentActivity());
                        builder.setMessage(LocaleController.getString("ConvertGroupAlert", R.string.ConvertGroupAlert));
                        builder.setTitle(LocaleController.getString("ConvertGroupAlertWarning", R.string.ConvertGroupAlertWarning));
                        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new C16493());
                        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                        ProfileActivity.this.showDialog(builder.create());
                    } else {
                        ProfileActivity.this.processOnClickOrPress(i);
                    }
                }
            }
        }
    }

    /* renamed from: org.telegram.ui.ProfileActivity$1 */
    class C23481 extends EmptyPhotoViewerProvider {
        C23481() {
        }

        public PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, FileLocation fileLocation, int index) {
            if (fileLocation == null) {
                return null;
            }
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
            if (photoBig == null || photoBig.local_id != fileLocation.local_id || photoBig.volume_id != fileLocation.volume_id || photoBig.dc_id != fileLocation.dc_id) {
                return null;
            }
            int[] coords = new int[2];
            ProfileActivity.this.avatarImage.getLocationInWindow(coords);
            PlaceProviderObject object = new PlaceProviderObject();
            int i = 0;
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
            return object;
        }

        public void willHidePhotoViewer() {
            ProfileActivity.this.avatarImage.getImageReceiver().setVisible(true, true);
        }
    }

    private class ListAdapter extends SelectionAdapter {
        private Context mContext;

        /* renamed from: org.telegram.ui.ProfileActivity$ListAdapter$1 */
        class C22621 implements AboutLinkCellDelegate {
            C22621() {
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
                    CombinedDrawable combinedDrawable = new CombinedDrawable(new ColorDrawable(Theme.getColor(Theme.key_windowBackgroundGray)), Theme.getThemedDrawable(this.mContext, R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                    combinedDrawable.setFullsize(true);
                    view.setBackgroundDrawable(combinedDrawable);
                    break;
                case 6:
                    view = new TextInfoPrivacyCell(this.mContext);
                    TextInfoPrivacyCell cell = (TextInfoPrivacyCell) view;
                    CombinedDrawable combinedDrawable2 = new CombinedDrawable(new ColorDrawable(Theme.getColor(Theme.key_windowBackgroundGray)), Theme.getThemedDrawable(this.mContext, R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                    combinedDrawable2.setFullsize(true);
                    cell.setBackgroundDrawable(combinedDrawable2);
                    cell.setText(AndroidUtilities.replaceTags(LocaleController.formatString("ConvertGroupInfo", R.string.ConvertGroupInfo, LocaleController.formatPluralString("Members", MessagesController.getInstance(ProfileActivity.this.currentAccount).maxMegagroupCount))));
                    break;
                case 7:
                    view = new LoadingCell(this.mContext);
                    break;
                case 8:
                    view = new AboutLinkCell(this.mContext);
                    ((AboutLinkCell) view).setDelegate(new C22621());
                    break;
                default:
                    break;
            }
            view.setLayoutParams(new LayoutParams(-1, -2));
            return new Holder(view);
        }

        public void onBindViewHolder(ViewHolder holder, int i) {
            ListAdapter listAdapter = this;
            ViewHolder viewHolder = holder;
            int i2 = i;
            int itemViewType = holder.getItemViewType();
            boolean checkBackground;
            if (itemViewType != 0) {
                String str = null;
                String text;
                TL_userFull userFull;
                if (itemViewType != 8) {
                    int i3 = 0;
                    String text2;
                    switch (itemViewType) {
                        case 2:
                            checkBackground = true;
                            TextDetailCell checkBackground2 = (TextDetailCell) holder.itemView;
                            checkBackground2.setMultiline(false);
                            User user;
                            if (i2 == ProfileActivity.this.phoneRow) {
                                user = MessagesController.getInstance(ProfileActivity.this.currentAccount).getUser(Integer.valueOf(ProfileActivity.this.user_id));
                                if (user.phone == null || user.phone.length() == 0) {
                                    text2 = LocaleController.getString("NumberUnknown", R.string.NumberUnknown);
                                } else {
                                    text2 = PhoneFormat.getInstance();
                                    StringBuilder stringBuilder = new StringBuilder();
                                    stringBuilder.append("+");
                                    stringBuilder.append(user.phone);
                                    text2 = text2.format(stringBuilder.toString());
                                }
                                checkBackground2.setTextAndValueAndIcon(text2, LocaleController.getString("PhoneMobile", R.string.PhoneMobile), R.drawable.profile_phone, 0);
                                return;
                            } else if (i2 == ProfileActivity.this.usernameRow) {
                                user = MessagesController.getInstance(ProfileActivity.this.currentAccount).getUser(Integer.valueOf(ProfileActivity.this.user_id));
                                if (user == null || TextUtils.isEmpty(user.username)) {
                                    text2 = "-";
                                } else {
                                    r5 = new StringBuilder();
                                    r5.append("@");
                                    r5.append(user.username);
                                    text2 = r5.toString();
                                }
                                if (ProfileActivity.this.phoneRow == -1 && ProfileActivity.this.userInfoRow == -1 && ProfileActivity.this.userInfoDetailedRow == -1) {
                                    checkBackground2.setTextAndValueAndIcon(text2, LocaleController.getString("Username", R.string.Username), R.drawable.profile_info, 11);
                                } else {
                                    checkBackground2.setTextAndValue(text2, LocaleController.getString("Username", R.string.Username));
                                }
                                return;
                            } else if (i2 == ProfileActivity.this.channelNameRow) {
                                if (ProfileActivity.this.currentChat == null || TextUtils.isEmpty(ProfileActivity.this.currentChat.username)) {
                                    text = "-";
                                } else {
                                    text = new StringBuilder();
                                    text.append("@");
                                    text.append(ProfileActivity.this.currentChat.username);
                                    text = text.toString();
                                }
                                r5 = new StringBuilder();
                                r5.append(MessagesController.getInstance(ProfileActivity.this.currentAccount).linkPrefix);
                                r5.append("/");
                                r5.append(ProfileActivity.this.currentChat.username);
                                checkBackground2.setTextAndValue(text, r5.toString());
                                return;
                            } else if (i2 == ProfileActivity.this.userInfoDetailedRow) {
                                userFull = MessagesController.getInstance(ProfileActivity.this.currentAccount).getUserFull(ProfileActivity.this.user_id);
                                checkBackground2.setMultiline(true);
                                if (userFull != null) {
                                    str = userFull.about;
                                }
                                checkBackground2.setTextAndValueAndIcon(str, LocaleController.getString("UserBio", R.string.UserBio), R.drawable.profile_info, 11);
                                return;
                            } else {
                                return;
                            }
                        case 3:
                            TextCell textCell = viewHolder.itemView;
                            textCell.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
                            textCell.setTag(Theme.key_windowBackgroundWhiteBlackText);
                            if (i2 == ProfileActivity.this.sharedMediaRow) {
                                if (ProfileActivity.this.totalMediaCount == -1) {
                                    text2 = LocaleController.getString("Loading", R.string.Loading);
                                } else {
                                    text2 = "%d";
                                    Object[] objArr = new Object[1];
                                    objArr[0] = Integer.valueOf(ProfileActivity.this.totalMediaCount + (ProfileActivity.this.totalMediaCountMerge != -1 ? ProfileActivity.this.totalMediaCountMerge : 0));
                                    text2 = String.format(text2, objArr);
                                }
                                if (ProfileActivity.this.user_id == 0 || UserConfig.getInstance(ProfileActivity.this.currentAccount).getClientUserId() != ProfileActivity.this.user_id) {
                                    textCell.setTextAndValue(LocaleController.getString("SharedMedia", R.string.SharedMedia), text2);
                                } else {
                                    textCell.setTextAndValueAndIcon(LocaleController.getString("SharedMedia", R.string.SharedMedia), text2, R.drawable.profile_list);
                                }
                                break;
                            } else if (i2 == ProfileActivity.this.groupsInCommonRow) {
                                TL_userFull userFull2 = MessagesController.getInstance(ProfileActivity.this.currentAccount).getUserFull(ProfileActivity.this.user_id);
                                r6 = LocaleController.getString("GroupsInCommon", R.string.GroupsInCommon);
                                str = "%d";
                                r8 = new Object[1];
                                r8[0] = Integer.valueOf(userFull2 != null ? userFull2.common_chats_count : 0);
                                textCell.setTextAndValue(r6, String.format(str, r8));
                                break;
                            } else if (i2 == ProfileActivity.this.settingsTimerRow) {
                                EncryptedChat encryptedChat = MessagesController.getInstance(ProfileActivity.this.currentAccount).getEncryptedChat(Integer.valueOf((int) (ProfileActivity.this.dialog_id >> 32)));
                                if (encryptedChat.ttl == 0) {
                                    r6 = LocaleController.getString("ShortMessageLifetimeForever", R.string.ShortMessageLifetimeForever);
                                } else {
                                    r6 = LocaleController.formatTTLString(encryptedChat.ttl);
                                }
                                textCell.setTextAndValue(LocaleController.getString("MessageLifetime", R.string.MessageLifetime), r6);
                                break;
                            } else if (i2 == ProfileActivity.this.settingsNotificationsRow) {
                                long did;
                                StringBuilder stringBuilder2;
                                boolean custom;
                                StringBuilder stringBuilder3;
                                boolean hasOverride;
                                StringBuilder stringBuilder4;
                                int value;
                                StringBuilder stringBuilder5;
                                int delta;
                                boolean enabled;
                                String val;
                                SharedPreferences preferences = MessagesController.getNotificationsSettings(ProfileActivity.this.currentAccount);
                                if (ProfileActivity.this.dialog_id != 0) {
                                    did = ProfileActivity.this.dialog_id;
                                } else if (ProfileActivity.this.user_id != 0) {
                                    did = (long) ProfileActivity.this.user_id;
                                } else {
                                    did = (long) (-ProfileActivity.this.chat_id);
                                    stringBuilder2 = new StringBuilder();
                                    stringBuilder2.append("custom_");
                                    stringBuilder2.append(did);
                                    custom = preferences.getBoolean(stringBuilder2.toString(), false);
                                    stringBuilder3 = new StringBuilder();
                                    stringBuilder3.append("notify2_");
                                    stringBuilder3.append(did);
                                    hasOverride = preferences.contains(stringBuilder3.toString());
                                    stringBuilder4 = new StringBuilder();
                                    stringBuilder4.append("notify2_");
                                    stringBuilder4.append(did);
                                    value = preferences.getInt(stringBuilder4.toString(), 0);
                                    stringBuilder5 = new StringBuilder();
                                    stringBuilder5.append("notifyuntil_");
                                    stringBuilder5.append(did);
                                    delta = preferences.getInt(stringBuilder5.toString(), 0);
                                    if (value == 3 || delta == ConnectionsManager.DEFAULT_DATACENTER_ID) {
                                        checkBackground = true;
                                        if (value != 0) {
                                            if (hasOverride) {
                                                enabled = true;
                                            } else if (((int) did) >= 0) {
                                                enabled = preferences.getBoolean("EnableGroup", true);
                                            } else {
                                                enabled = preferences.getBoolean("EnableAll", true);
                                            }
                                        } else if (value == 1) {
                                            enabled = true;
                                        } else if (value != 2) {
                                            enabled = false;
                                        } else {
                                            enabled = false;
                                            if (enabled || !custom) {
                                                text2 = enabled ? LocaleController.getString("NotificationsOn", R.string.NotificationsOn) : LocaleController.getString("NotificationsOff", true);
                                                val = text2;
                                                if (val != null) {
                                                    textCell.setTextAndValueAndIcon(LocaleController.getString("Notifications", R.string.Notifications), val, true);
                                                } else {
                                                    textCell.setTextAndValueAndIcon(LocaleController.getString("Notifications", R.string.Notifications), LocaleController.getString("NotificationsOff", R.string.NotificationsOff), true);
                                                }
                                                viewHolder = holder;
                                                i2 = i;
                                                return;
                                            }
                                            text2 = LocaleController.getString("NotificationsCustom", R.string.NotificationsCustom);
                                        }
                                        if (enabled) {
                                        }
                                        if (enabled) {
                                        }
                                        text2 = enabled ? LocaleController.getString("NotificationsOn", R.string.NotificationsOn) : LocaleController.getString("NotificationsOff", true);
                                        val = text2;
                                        if (val != null) {
                                            textCell.setTextAndValueAndIcon(LocaleController.getString("Notifications", R.string.Notifications), LocaleController.getString("NotificationsOff", R.string.NotificationsOff), true);
                                        } else {
                                            textCell.setTextAndValueAndIcon(LocaleController.getString("Notifications", R.string.Notifications), val, true);
                                        }
                                        viewHolder = holder;
                                        i2 = i;
                                        return;
                                    }
                                    delta -= ConnectionsManager.getInstance(ProfileActivity.this.currentAccount).getCurrentTime();
                                    if (delta <= 0) {
                                        if (custom) {
                                            text2 = LocaleController.getString("NotificationsCustom", R.string.NotificationsCustom);
                                        } else {
                                            text2 = LocaleController.getString("NotificationsOn", R.string.NotificationsOn);
                                        }
                                    } else if (delta < 3600) {
                                        text2 = LocaleController.formatString("WillUnmuteIn", R.string.WillUnmuteIn, LocaleController.formatPluralString("Minutes", delta / 60));
                                    } else if (delta < 86400) {
                                        r8 = new Object[1];
                                        checkBackground = true;
                                        r8[0] = LocaleController.formatPluralString("Hours", (int) Math.ceil((double) ((((float) delta) / true) / true)));
                                        text2 = LocaleController.formatString("WillUnmuteIn", R.string.WillUnmuteIn, r8);
                                    } else {
                                        checkBackground = true;
                                        if (delta < 31536000) {
                                            Object[] objArr2 = new Object[1];
                                            int delta2 = delta;
                                            objArr2[0] = LocaleController.formatPluralString("Days", (int) Math.ceil((double) (((((float) delta) / 60.0f) / 60.0f) / 24.0f)));
                                            text2 = LocaleController.formatString("WillUnmuteIn", true, objArr2);
                                            delta = delta2;
                                            val = text2;
                                            if (val != null) {
                                                textCell.setTextAndValueAndIcon(LocaleController.getString("Notifications", R.string.Notifications), val, true);
                                            } else {
                                                textCell.setTextAndValueAndIcon(LocaleController.getString("Notifications", R.string.Notifications), LocaleController.getString("NotificationsOff", R.string.NotificationsOff), true);
                                            }
                                            viewHolder = holder;
                                            i2 = i;
                                            return;
                                        }
                                        text2 = null;
                                    }
                                    checkBackground = true;
                                    val = text2;
                                    if (val != null) {
                                        textCell.setTextAndValueAndIcon(LocaleController.getString("Notifications", R.string.Notifications), LocaleController.getString("NotificationsOff", R.string.NotificationsOff), true);
                                    } else {
                                        textCell.setTextAndValueAndIcon(LocaleController.getString("Notifications", R.string.Notifications), val, true);
                                    }
                                    viewHolder = holder;
                                    i2 = i;
                                    return;
                                    val = text2;
                                    if (val != null) {
                                        textCell.setTextAndValueAndIcon(LocaleController.getString("Notifications", R.string.Notifications), val, true);
                                    } else {
                                        textCell.setTextAndValueAndIcon(LocaleController.getString("Notifications", R.string.Notifications), LocaleController.getString("NotificationsOff", R.string.NotificationsOff), true);
                                    }
                                    viewHolder = holder;
                                    i2 = i;
                                    return;
                                }
                                stringBuilder2 = new StringBuilder();
                                stringBuilder2.append("custom_");
                                stringBuilder2.append(did);
                                custom = preferences.getBoolean(stringBuilder2.toString(), false);
                                stringBuilder3 = new StringBuilder();
                                stringBuilder3.append("notify2_");
                                stringBuilder3.append(did);
                                hasOverride = preferences.contains(stringBuilder3.toString());
                                stringBuilder4 = new StringBuilder();
                                stringBuilder4.append("notify2_");
                                stringBuilder4.append(did);
                                value = preferences.getInt(stringBuilder4.toString(), 0);
                                stringBuilder5 = new StringBuilder();
                                stringBuilder5.append("notifyuntil_");
                                stringBuilder5.append(did);
                                delta = preferences.getInt(stringBuilder5.toString(), 0);
                                if (value == 3) {
                                    break;
                                }
                                checkBackground = true;
                                if (value != 0) {
                                    if (value == 1) {
                                        enabled = true;
                                    } else if (value != 2) {
                                        enabled = false;
                                        if (enabled) {
                                        }
                                        if (enabled) {
                                        }
                                        text2 = enabled ? LocaleController.getString("NotificationsOn", R.string.NotificationsOn) : LocaleController.getString("NotificationsOff", true);
                                        val = text2;
                                        if (val != null) {
                                            textCell.setTextAndValueAndIcon(LocaleController.getString("Notifications", R.string.Notifications), LocaleController.getString("NotificationsOff", R.string.NotificationsOff), true);
                                        } else {
                                            textCell.setTextAndValueAndIcon(LocaleController.getString("Notifications", R.string.Notifications), val, true);
                                        }
                                        viewHolder = holder;
                                        i2 = i;
                                        return;
                                    } else {
                                        enabled = false;
                                    }
                                } else if (hasOverride) {
                                    enabled = true;
                                } else if (((int) did) >= 0) {
                                    enabled = preferences.getBoolean("EnableAll", true);
                                } else {
                                    enabled = preferences.getBoolean("EnableGroup", true);
                                }
                                if (enabled) {
                                }
                                if (enabled) {
                                }
                                text2 = enabled ? LocaleController.getString("NotificationsOn", R.string.NotificationsOn) : LocaleController.getString("NotificationsOff", true);
                                val = text2;
                                if (val != null) {
                                    textCell.setTextAndValueAndIcon(LocaleController.getString("Notifications", R.string.Notifications), val, true);
                                } else {
                                    textCell.setTextAndValueAndIcon(LocaleController.getString("Notifications", R.string.Notifications), LocaleController.getString("NotificationsOff", R.string.NotificationsOff), true);
                                }
                                viewHolder = holder;
                                i2 = i;
                                return;
                            } else {
                                checkBackground = true;
                                i2 = i;
                                if (i2 == ProfileActivity.this.startSecretChatRow) {
                                    textCell.setText(LocaleController.getString("StartEncryptedChat", true));
                                    textCell.setTag(Theme.key_windowBackgroundWhiteGreenText2);
                                    textCell.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGreenText2));
                                } else if (i2 == ProfileActivity.this.settingsKeyRow) {
                                    IdenticonDrawable identiconDrawable = new IdenticonDrawable();
                                    identiconDrawable.setEncryptedChat(MessagesController.getInstance(ProfileActivity.this.currentAccount).getEncryptedChat(Integer.valueOf((int) (ProfileActivity.this.dialog_id >> 32))));
                                    textCell.setTextAndValueDrawable(LocaleController.getString("EncryptionKey", R.string.EncryptionKey), identiconDrawable);
                                } else if (i2 == ProfileActivity.this.leaveChannelRow) {
                                    textCell.setTag(Theme.key_windowBackgroundWhiteRedText5);
                                    textCell.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText5));
                                    textCell.setText(LocaleController.getString("LeaveChannel", true));
                                } else if (i2 == ProfileActivity.this.convertRow) {
                                    textCell.setText(LocaleController.getString("UpgradeGroup", true));
                                    textCell.setTag(Theme.key_windowBackgroundWhiteGreenText2);
                                    textCell.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGreenText2));
                                } else if (i2 == ProfileActivity.this.addMemberRow) {
                                    if (ProfileActivity.this.chat_id > 0) {
                                        textCell.setText(LocaleController.getString("AddMember", true));
                                    } else {
                                        textCell.setText(LocaleController.getString("AddRecipient", true));
                                    }
                                } else if (i2 == ProfileActivity.this.membersRow) {
                                    if (ProfileActivity.this.info != null) {
                                        if (!ChatObject.isChannel(ProfileActivity.this.currentChat) || ProfileActivity.this.currentChat.megagroup) {
                                            textCell.setTextAndValue(LocaleController.getString("ChannelMembers", true), String.format("%d", new Object[]{Integer.valueOf(ProfileActivity.this.info.participants_count)}));
                                        } else {
                                            textCell.setTextAndValue(LocaleController.getString("ChannelSubscribers", true), String.format("%d", new Object[]{Integer.valueOf(ProfileActivity.this.info.participants_count)}));
                                        }
                                    } else if (!ChatObject.isChannel(ProfileActivity.this.currentChat) || ProfileActivity.this.currentChat.megagroup) {
                                        textCell.setText(LocaleController.getString("ChannelMembers", true));
                                    } else {
                                        textCell.setText(LocaleController.getString("ChannelSubscribers", true));
                                    }
                                }
                                viewHolder = holder;
                                return;
                            }
                            break;
                        case 4:
                            ChatParticipant part;
                            UserCell userCell = viewHolder.itemView;
                            if (ProfileActivity.this.sortedUsers.isEmpty()) {
                                part = (ChatParticipant) ProfileActivity.this.info.participants.participants.get((i2 - ProfileActivity.this.emptyRowChat2) - 1);
                            } else {
                                part = (ChatParticipant) ProfileActivity.this.info.participants.participants.get(((Integer) ProfileActivity.this.sortedUsers.get((i2 - ProfileActivity.this.emptyRowChat2) - 1)).intValue());
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
                                TLObject user2 = MessagesController.getInstance(ProfileActivity.this.currentAccount).getUser(Integer.valueOf(part.user_id));
                                if (i2 == ProfileActivity.this.emptyRowChat2 + 1) {
                                    i3 = R.drawable.menu_newgroup;
                                }
                                userCell.setData(user2, null, null, i3);
                                break;
                            }
                            break;
                    }
                    checkBackground = true;
                    return;
                }
                checkBackground = true;
                AboutLinkCell checkBackground3 = (AboutLinkCell) viewHolder.itemView;
                if (i2 == ProfileActivity.this.userInfoRow) {
                    userFull = MessagesController.getInstance(ProfileActivity.this.currentAccount).getUserFull(ProfileActivity.this.user_id);
                    if (userFull != null) {
                        str = userFull.about;
                    }
                    checkBackground3.setTextAndIcon(str, R.drawable.profile_info, ProfileActivity.this.isBot);
                    return;
                } else if (i2 == ProfileActivity.this.channelInfoRow) {
                    text = ProfileActivity.this.info.about;
                    while (text.contains("\n\n\n")) {
                        text = text.replace("\n\n\n", "\n\n");
                    }
                    checkBackground3.setTextAndIcon(text, R.drawable.profile_info, true);
                    return;
                } else {
                    return;
                }
            }
            checkBackground = true;
            if (i2 != ProfileActivity.this.emptyRowChat) {
                if (i2 != ProfileActivity.this.emptyRowChat2) {
                    ((EmptyCell) viewHolder.itemView).setHeight(AndroidUtilities.dp(36.0f));
                    return;
                }
            }
            ((EmptyCell) viewHolder.itemView).setHeight(AndroidUtilities.dp(8.0f));
        }

        public boolean isEnabled(ViewHolder holder) {
            int i = holder.getAdapterPosition();
            boolean z = true;
            if (ProfileActivity.this.user_id != 0) {
                if (!(i == ProfileActivity.this.phoneRow || i == ProfileActivity.this.settingsTimerRow || i == ProfileActivity.this.settingsKeyRow || i == ProfileActivity.this.settingsNotificationsRow || i == ProfileActivity.this.sharedMediaRow || i == ProfileActivity.this.startSecretChatRow || i == ProfileActivity.this.usernameRow || i == ProfileActivity.this.userInfoRow || i == ProfileActivity.this.groupsInCommonRow)) {
                    if (i != ProfileActivity.this.userInfoDetailedRow) {
                        z = false;
                        return z;
                    }
                }
                return z;
            } else if (ProfileActivity.this.chat_id == 0) {
                return false;
            } else {
                if (!(i == ProfileActivity.this.convertRow || i == ProfileActivity.this.settingsNotificationsRow || i == ProfileActivity.this.sharedMediaRow || ((i > ProfileActivity.this.emptyRowChat2 && i < ProfileActivity.this.membersEndRow) || i == ProfileActivity.this.addMemberRow || i == ProfileActivity.this.channelNameRow || i == ProfileActivity.this.leaveChannelRow || i == ProfileActivity.this.channelInfoRow))) {
                    if (i != ProfileActivity.this.membersRow) {
                        z = false;
                        return z;
                    }
                }
                return z;
            }
        }

        public int getItemCount() {
            return ProfileActivity.this.rowCount;
        }

        public int getItemViewType(int i) {
            if (!(i == ProfileActivity.this.emptyRow || i == ProfileActivity.this.emptyRowChat)) {
                if (i != ProfileActivity.this.emptyRowChat2) {
                    if (i != ProfileActivity.this.sectionRow) {
                        if (i != ProfileActivity.this.userSectionRow) {
                            if (!(i == ProfileActivity.this.phoneRow || i == ProfileActivity.this.usernameRow || i == ProfileActivity.this.channelNameRow)) {
                                if (i != ProfileActivity.this.userInfoDetailedRow) {
                                    if (!(i == ProfileActivity.this.leaveChannelRow || i == ProfileActivity.this.sharedMediaRow || i == ProfileActivity.this.settingsTimerRow || i == ProfileActivity.this.settingsNotificationsRow || i == ProfileActivity.this.startSecretChatRow || i == ProfileActivity.this.settingsKeyRow || i == ProfileActivity.this.convertRow || i == ProfileActivity.this.addMemberRow || i == ProfileActivity.this.groupsInCommonRow)) {
                                        if (i != ProfileActivity.this.membersRow) {
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
                                            if (i != ProfileActivity.this.userInfoRow) {
                                                if (i != ProfileActivity.this.channelInfoRow) {
                                                    return 0;
                                                }
                                            }
                                            return 8;
                                        }
                                    }
                                    return 3;
                                }
                            }
                            return 2;
                        }
                    }
                    return 1;
                }
            }
            return 0;
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
            this.avatarUpdater.delegate = new C22573();
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
        int i;
        boolean z;
        ActionBar actionBar = new ActionBar(context) {
            public boolean onTouchEvent(MotionEvent event) {
                return super.onTouchEvent(event);
            }
        };
        if (this.user_id == 0) {
            if (!ChatObject.isChannel(this.chat_id, this.currentAccount) || this.currentChat.megagroup) {
                i = this.chat_id;
                z = false;
                actionBar.setItemsBackgroundColor(AvatarDrawable.getButtonColorForId(i), false);
                actionBar.setItemsColor(Theme.getColor(Theme.key_actionBarDefaultIcon), false);
                actionBar.setItemsColor(Theme.getColor(Theme.key_actionBarActionModeDefaultIcon), true);
                actionBar.setBackButtonDrawable(new BackDrawable(false));
                actionBar.setCastShadows(false);
                actionBar.setAddToContainer(false);
                if (VERSION.SDK_INT >= 21 && !AndroidUtilities.isTablet()) {
                    z = true;
                }
                actionBar.setOccupyStatusBar(z);
                return actionBar;
            }
        }
        i = 5;
        z = false;
        actionBar.setItemsBackgroundColor(AvatarDrawable.getButtonColorForId(i), false);
        actionBar.setItemsColor(Theme.getColor(Theme.key_actionBarDefaultIcon), false);
        actionBar.setItemsColor(Theme.getColor(Theme.key_actionBarActionModeDefaultIcon), true);
        actionBar.setBackButtonDrawable(new BackDrawable(false));
        actionBar.setCastShadows(false);
        actionBar.setAddToContainer(false);
        z = true;
        actionBar.setOccupyStatusBar(z);
        return actionBar;
    }

    public View createView(Context context) {
        int i;
        TopView topView;
        int i2;
        int a;
        float f;
        SimpleTextView simpleTextView;
        int i3;
        float f2;
        Drawable drawable;
        Drawable shadowDrawable;
        Drawable combinedDrawable;
        boolean isChannel;
        View view;
        int i4;
        StateListAnimator animator;
        Context context2 = context;
        Theme.createProfileResources(context);
        this.hasOwnBackground = true;
        this.extraHeight = AndroidUtilities.dp(88.0f);
        this.actionBar.setActionBarMenuOnItemClick(new C22605());
        createActionBarMenu();
        this.listAdapter = new ListAdapter(context2);
        this.avatarDrawable = new AvatarDrawable();
        this.avatarDrawable.setProfile(true);
        this.fragmentView = new FrameLayout(context2) {
            public boolean hasOverlappingRendering() {
                return false;
            }

            protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
                super.onLayout(changed, left, top, right, bottom);
                ProfileActivity.this.checkListViewScroll();
            }
        };
        FrameLayout frameLayout = this.fragmentView;
        this.listView = new RecyclerListView(context2) {
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
        this.layoutManager = new LinearLayoutManager(context2) {
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        };
        this.layoutManager.setOrientation(1);
        this.listView.setLayoutManager(this.layoutManager);
        RecyclerListView recyclerListView = this.listView;
        if (this.user_id == 0) {
            if (!ChatObject.isChannel(r0.chat_id, r0.currentAccount) || r0.currentChat.megagroup) {
                i = r0.chat_id;
                recyclerListView.setGlowColor(AvatarDrawable.getProfileBackColorForId(i));
                frameLayout.addView(r0.listView, LayoutHelper.createFrame(-1, -1, 51));
                r0.listView.setAdapter(r0.listAdapter);
                r0.listView.setOnItemClickListener(new C22619());
                r0.listView.setOnItemLongClickListener(new OnItemLongClickListener() {
                    public boolean onItemClick(View view, int position) {
                        int i = position;
                        if (i <= ProfileActivity.this.emptyRowChat2 || i >= ProfileActivity.this.membersEndRow) {
                            return ProfileActivity.this.processOnClickOrPress(i);
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
                            user = (ChatParticipant) ProfileActivity.this.info.participants.participants.get((i - ProfileActivity.this.emptyRowChat2) - 1);
                        } else {
                            user = (ChatParticipant) ProfileActivity.this.info.participants.participants.get(((Integer) ProfileActivity.this.sortedUsers.get((i - ProfileActivity.this.emptyRowChat2) - 1)).intValue());
                        }
                        ProfileActivity.this.selectedUser = user.user_id;
                        if (ChatObject.isChannel(ProfileActivity.this.currentChat)) {
                            channelParticipant = ((TL_chatChannelParticipant) user).channelParticipant;
                            if (user.user_id == UserConfig.getInstance(ProfileActivity.this.currentAccount).getClientUserId()) {
                                return false;
                            }
                            boolean z;
                            User u = MessagesController.getInstance(ProfileActivity.this.currentAccount).getUser(Integer.valueOf(user.user_id));
                            if (!(channelParticipant instanceof TL_channelParticipant)) {
                                if (!(channelParticipant instanceof TL_channelParticipantBanned)) {
                                    z = false;
                                    allowSetAdmin = z;
                                    z = ((channelParticipant instanceof TL_channelParticipantAdmin) && !(channelParticipant instanceof TL_channelParticipantCreator)) || channelParticipant.can_edit;
                                    canEditAdmin = z;
                                }
                            }
                            z = true;
                            allowSetAdmin = z;
                            if (channelParticipant instanceof TL_channelParticipantAdmin) {
                            }
                            canEditAdmin = z;
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
                        boolean z2 = ProfileActivity.this.currentChat.megagroup;
                        int i2 = R.string.KickFromGroup;
                        if (z2) {
                            if (allowSetAdmin && ChatObject.canAddAdmins(ProfileActivity.this.currentChat)) {
                                items.add(LocaleController.getString("SetAsAdmin", R.string.SetAsAdmin));
                                actions.add(Integer.valueOf(0));
                            }
                            if (ChatObject.canBlockUsers(ProfileActivity.this.currentChat) && canEditAdmin) {
                                items.add(LocaleController.getString("KickFromSupergroup", R.string.KickFromSupergroup));
                                actions.add(Integer.valueOf(1));
                                items.add(LocaleController.getString("KickFromGroup", R.string.KickFromGroup));
                                actions.add(Integer.valueOf(2));
                            }
                        } else {
                            String str;
                            if (ProfileActivity.this.chat_id > 0) {
                                str = "KickFromGroup";
                            } else {
                                str = "KickFromBroadcast";
                                i2 = R.string.KickFromBroadcast;
                            }
                            items.add(LocaleController.getString(str, i2));
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
                                ChannelRightsEditActivity channelRightsEditActivity = new ChannelRightsEditActivity(user.user_id, ProfileActivity.this.chat_id, channelParticipant.admin_rights, channelParticipant.banned_rights, ((Integer) actions.get(i)).intValue(), true);
                                channelRightsEditActivity.setDelegate(new ChannelRightsEditActivityDelegate() {
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
                                            int a2 = 0;
                                            for (int a3 = 0; a3 < ProfileActivity.this.info.participants.participants.size(); a3++) {
                                                if (((TL_chatChannelParticipant) ProfileActivity.this.info.participants.participants.get(a3)).channelParticipant.user_id == user.user_id) {
                                                    if (ProfileActivity.this.info != null) {
                                                        ChatFull access$2400 = ProfileActivity.this.info;
                                                        access$2400.participants_count--;
                                                    }
                                                    ProfileActivity.this.info.participants.participants.remove(a3);
                                                    changed = true;
                                                    if (ProfileActivity.this.info != null && ProfileActivity.this.info.participants != null) {
                                                        while (true) {
                                                            a = a2;
                                                            if (a < ProfileActivity.this.info.participants.participants.size()) {
                                                                break;
                                                            } else if (((ChatParticipant) ProfileActivity.this.info.participants.participants.get(a)).user_id == user.user_id) {
                                                                break;
                                                            } else {
                                                                a2 = a + 1;
                                                            }
                                                        }
                                                        ProfileActivity.this.info.participants.participants.remove(a);
                                                        changed = true;
                                                    }
                                                    if (changed) {
                                                        ProfileActivity.this.updateOnlineCount();
                                                        ProfileActivity.this.updateRowsIds();
                                                        ProfileActivity.this.listAdapter.notifyDataSetChanged();
                                                    }
                                                }
                                            }
                                            while (true) {
                                                a = a2;
                                                if (a < ProfileActivity.this.info.participants.participants.size()) {
                                                    if (((ChatParticipant) ProfileActivity.this.info.participants.participants.get(a)).user_id == user.user_id) {
                                                        break;
                                                    }
                                                    a2 = a + 1;
                                                } else {
                                                    break;
                                                }
                                            }
                                            ProfileActivity.this.info.participants.participants.remove(a);
                                            changed = true;
                                            if (changed) {
                                                ProfileActivity.this.updateOnlineCount();
                                                ProfileActivity.this.updateRowsIds();
                                                ProfileActivity.this.listAdapter.notifyDataSetChanged();
                                            }
                                        }
                                    }
                                });
                                ProfileActivity.this.presentFragment(channelRightsEditActivity);
                            }
                        });
                        ProfileActivity.this.showDialog(builder.create());
                        return true;
                    }
                });
                if (r0.banFromGroup == 0) {
                    if (r0.currentChannelParticipant == null) {
                        TL_channels_getParticipant req = new TL_channels_getParticipant();
                        req.channel = MessagesController.getInstance(r0.currentAccount).getInputChannel(r0.banFromGroup);
                        req.user_id = MessagesController.getInstance(r0.currentAccount).getInputUser(r0.user_id);
                        ConnectionsManager.getInstance(r0.currentAccount).sendRequest(req, new RequestDelegate() {
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
                    FrameLayout frameLayout1 = new FrameLayout(context2) {
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
                        class C22561 implements ChannelRightsEditActivityDelegate {
                            C22561() {
                            }

                            public void didSetRights(int rights, TL_channelAdminRights rightsAdmin, TL_channelBannedRights rightsBanned) {
                                ProfileActivity.this.removeSelfFromStack();
                            }
                        }

                        public void onClick(View v) {
                            ChannelRightsEditActivity fragment = new ChannelRightsEditActivity(ProfileActivity.this.user_id, ProfileActivity.this.banFromGroup, null, ProfileActivity.this.currentChannelParticipant != null ? ProfileActivity.this.currentChannelParticipant.banned_rights : null, 1, true);
                            fragment.setDelegate(new C22561());
                            ProfileActivity.this.presentFragment(fragment);
                        }
                    });
                    TextView textView = new TextView(context2);
                    textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText));
                    textView.setTextSize(1, 15.0f);
                    textView.setGravity(17);
                    textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                    textView.setText(LocaleController.getString("BanFromTheGroup", R.string.BanFromTheGroup));
                    frameLayout1.addView(textView, LayoutHelper.createFrame(-2, -2.0f, 17, 0.0f, 1.0f, 0.0f, 0.0f));
                    r0.listView.setPadding(0, AndroidUtilities.dp(88.0f), 0, AndroidUtilities.dp(48.0f));
                    r0.listView.setBottomGlowOffset(AndroidUtilities.dp(48.0f));
                } else {
                    r0.listView.setPadding(0, AndroidUtilities.dp(88.0f), 0, 0);
                }
                r0.topView = new TopView(context2);
                topView = r0.topView;
                if (r0.user_id == 0) {
                    if (ChatObject.isChannel(r0.chat_id, r0.currentAccount) || r0.currentChat.megagroup) {
                        i2 = r0.chat_id;
                        topView.setBackgroundColor(AvatarDrawable.getProfileBackColorForId(i2));
                        frameLayout.addView(r0.topView);
                        frameLayout.addView(r0.actionBar);
                        r0.avatarImage = new BackupImageView(context2);
                        r0.avatarImage.setRoundRadius(AndroidUtilities.dp(21.0f));
                        r0.avatarImage.setPivotX(0.0f);
                        r0.avatarImage.setPivotY(0.0f);
                        frameLayout.addView(r0.avatarImage, LayoutHelper.createFrame(42, 42.0f, 51, 64.0f, 0.0f, 0.0f, 0.0f));
                        r0.avatarImage.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                if (ProfileActivity.this.user_id != 0) {
                                    User user = MessagesController.getInstance(ProfileActivity.this.currentAccount).getUser(Integer.valueOf(ProfileActivity.this.user_id));
                                    if (!(user.photo == null || user.photo.photo_big == null)) {
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
                        a = 0;
                        while (a < 2) {
                            if (r0.playProfileAnimation || a != 0) {
                                r0.nameTextView[a] = new SimpleTextView(context2);
                                if (a != 1) {
                                    r0.nameTextView[a].setTextColor(Theme.getColor(Theme.key_profile_title));
                                } else {
                                    r0.nameTextView[a].setTextColor(Theme.getColor(Theme.key_actionBarDefaultTitle));
                                }
                                r0.nameTextView[a].setTextSize(18);
                                r0.nameTextView[a].setGravity(3);
                                r0.nameTextView[a].setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                                r0.nameTextView[a].setLeftDrawableTopPadding(-AndroidUtilities.dp(1.3f));
                                r0.nameTextView[a].setPivotX(0.0f);
                                r0.nameTextView[a].setPivotY(0.0f);
                                f = 1.0f;
                                r0.nameTextView[a].setAlpha(a != 0 ? 0.0f : 1.0f);
                                frameLayout.addView(r0.nameTextView[a], LayoutHelper.createFrame(-2, -2.0f, 51, 118.0f, 0.0f, a != 0 ? 48.0f : 0.0f, 0.0f));
                                r0.onlineTextView[a] = new SimpleTextView(context2);
                                simpleTextView = r0.onlineTextView[a];
                                if (r0.user_id == 0) {
                                    if (ChatObject.isChannel(r0.chat_id, r0.currentAccount) || r0.currentChat.megagroup) {
                                        i3 = r0.chat_id;
                                        simpleTextView.setTextColor(AvatarDrawable.getProfileTextColorForId(i3));
                                        r0.onlineTextView[a].setTextSize(14);
                                        r0.onlineTextView[a].setGravity(3);
                                        simpleTextView = r0.onlineTextView[a];
                                        if (a == 0) {
                                            f = 0.0f;
                                        }
                                        simpleTextView.setAlpha(f);
                                        frameLayout.addView(r0.onlineTextView[a], LayoutHelper.createFrame(-2, -2.0f, 51, 118.0f, 0.0f, a != 0 ? 48.0f : 8.0f, 0.0f));
                                    }
                                }
                                i3 = 5;
                                simpleTextView.setTextColor(AvatarDrawable.getProfileTextColorForId(i3));
                                r0.onlineTextView[a].setTextSize(14);
                                r0.onlineTextView[a].setGravity(3);
                                simpleTextView = r0.onlineTextView[a];
                                if (a == 0) {
                                    f = 0.0f;
                                }
                                simpleTextView.setAlpha(f);
                                if (a != 0) {
                                }
                                frameLayout.addView(r0.onlineTextView[a], LayoutHelper.createFrame(-2, -2.0f, 51, 118.0f, 0.0f, a != 0 ? 48.0f : 8.0f, 0.0f));
                            }
                            a++;
                        }
                        if (r0.user_id != 0 || (r0.chat_id >= 0 && (!ChatObject.isLeftFromChat(r0.currentChat) || ChatObject.isChannel(r0.currentChat)))) {
                            r0.writeButton = new ImageView(context2);
                            f2 = 56.0f;
                            drawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56.0f), Theme.getColor(Theme.key_profile_actionBackground), Theme.getColor(Theme.key_profile_actionPressedBackground));
                            if (VERSION.SDK_INT < 21) {
                                shadowDrawable = context.getResources().getDrawable(R.drawable.floating_shadow_profile).mutate();
                                shadowDrawable.setColorFilter(new PorterDuffColorFilter(Theme.ACTION_BAR_VIDEO_EDIT_COLOR, Mode.MULTIPLY));
                                combinedDrawable = new CombinedDrawable(shadowDrawable, drawable, 0, 0);
                                combinedDrawable.setIconSize(AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
                                drawable = combinedDrawable;
                            }
                            r0.writeButton.setBackgroundDrawable(drawable);
                            r0.writeButton.setScaleType(ScaleType.CENTER);
                            r0.writeButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_profile_actionIcon), Mode.MULTIPLY));
                            if (r0.user_id == 0) {
                                r0.writeButton.setImageResource(R.drawable.floating_message);
                                r0.writeButton.setPadding(0, AndroidUtilities.dp(3.0f), 0, 0);
                            } else if (r0.chat_id != 0) {
                                isChannel = ChatObject.isChannel(r0.currentChat);
                                if ((isChannel || ChatObject.canEditInfo(r0.currentChat)) && (isChannel || r0.currentChat.admin || r0.currentChat.creator || !r0.currentChat.admins_enabled)) {
                                    r0.writeButton.setImageResource(R.drawable.floating_camera);
                                } else {
                                    r0.writeButton.setImageResource(R.drawable.floating_message);
                                    r0.writeButton.setPadding(0, AndroidUtilities.dp(3.0f), 0, 0);
                                }
                            }
                            view = r0.writeButton;
                            i4 = VERSION.SDK_INT < 21 ? 56 : 60;
                            if (VERSION.SDK_INT < 21) {
                                f2 = 60.0f;
                            }
                            frameLayout.addView(view, LayoutHelper.createFrame(i4, f2, 53, 0.0f, 0.0f, 16.0f, 0.0f));
                            if (VERSION.SDK_INT >= 21) {
                                animator = new StateListAnimator();
                                animator.addState(new int[]{16842919}, ObjectAnimator.ofFloat(r0.writeButton, "translationZ", new float[]{(float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(4.0f)}).setDuration(200));
                                animator.addState(new int[0], ObjectAnimator.ofFloat(r0.writeButton, "translationZ", new float[]{(float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(2.0f)}).setDuration(200));
                                r0.writeButton.setStateListAnimator(animator);
                                r0.writeButton.setOutlineProvider(new ViewOutlineProvider() {
                                    @SuppressLint({"NewApi"})
                                    public void getOutline(View view, Outline outline) {
                                        outline.setOval(0, 0, AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
                                    }
                                });
                            }
                            r0.writeButton.setOnClickListener(new View.OnClickListener() {

                                /* renamed from: org.telegram.ui.ProfileActivity$16$1 */
                                class C16411 implements OnClickListener {
                                    C16411() {
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
                                            } else {
                                                User user = MessagesController.getInstance(ProfileActivity.this.currentAccount).getUser(Integer.valueOf(ProfileActivity.this.user_id));
                                                if (user != null) {
                                                    if (!(user instanceof TL_userEmpty)) {
                                                        args = new Bundle();
                                                        args.putInt("user_id", ProfileActivity.this.user_id);
                                                        if (MessagesController.getInstance(ProfileActivity.this.currentAccount).checkCanOpenChat(args, ProfileActivity.this)) {
                                                            NotificationCenter.getInstance(ProfileActivity.this.currentAccount).removeObserver(ProfileActivity.this, NotificationCenter.closeChats);
                                                            NotificationCenter.getInstance(ProfileActivity.this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                                                            ProfileActivity.this.presentFragment(new ChatActivity(args), true);
                                                        }
                                                    }
                                                }
                                            }
                                        } else if (ProfileActivity.this.chat_id != 0) {
                                            boolean isChannel = ChatObject.isChannel(ProfileActivity.this.currentChat);
                                            if ((!isChannel || ChatObject.canEditInfo(ProfileActivity.this.currentChat)) && (isChannel || ProfileActivity.this.currentChat.admin || ProfileActivity.this.currentChat.creator || !ProfileActivity.this.currentChat.admins_enabled)) {
                                                CharSequence[] items;
                                                Builder builder = new Builder(ProfileActivity.this.getParentActivity());
                                                Chat chat = MessagesController.getInstance(ProfileActivity.this.currentAccount).getChat(Integer.valueOf(ProfileActivity.this.chat_id));
                                                if (!(chat.photo == null || chat.photo.photo_big == null)) {
                                                    if (!(chat.photo instanceof TL_chatPhotoEmpty)) {
                                                        items = new CharSequence[]{LocaleController.getString("FromCamera", R.string.FromCamera), LocaleController.getString("FromGalley", R.string.FromGalley), LocaleController.getString("DeletePhoto", R.string.DeletePhoto)};
                                                        builder.setItems(items, new C16411());
                                                        ProfileActivity.this.showDialog(builder.create());
                                                    }
                                                }
                                                items = new CharSequence[]{LocaleController.getString("FromCamera", R.string.FromCamera), LocaleController.getString("FromGalley", R.string.FromGalley)};
                                                builder.setItems(items, new C16411());
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
                        r0.listView.setOnScrollListener(new OnScrollListener() {
                            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                                ProfileActivity.this.checkListViewScroll();
                                if (ProfileActivity.this.participantsMap != null && ProfileActivity.this.loadMoreMembersRow != -1 && ProfileActivity.this.layoutManager.findLastVisibleItemPosition() > ProfileActivity.this.loadMoreMembersRow - 8) {
                                    ProfileActivity.this.getChannelParticipants(false);
                                }
                            }
                        });
                        return r0.fragmentView;
                    }
                }
                i2 = 5;
                topView.setBackgroundColor(AvatarDrawable.getProfileBackColorForId(i2));
                frameLayout.addView(r0.topView);
                frameLayout.addView(r0.actionBar);
                r0.avatarImage = new BackupImageView(context2);
                r0.avatarImage.setRoundRadius(AndroidUtilities.dp(21.0f));
                r0.avatarImage.setPivotX(0.0f);
                r0.avatarImage.setPivotY(0.0f);
                frameLayout.addView(r0.avatarImage, LayoutHelper.createFrame(42, 42.0f, 51, 64.0f, 0.0f, 0.0f, 0.0f));
                r0.avatarImage.setOnClickListener(/* anonymous class already generated */);
                a = 0;
                while (a < 2) {
                    if (!r0.playProfileAnimation) {
                    }
                    r0.nameTextView[a] = new SimpleTextView(context2);
                    if (a != 1) {
                        r0.nameTextView[a].setTextColor(Theme.getColor(Theme.key_actionBarDefaultTitle));
                    } else {
                        r0.nameTextView[a].setTextColor(Theme.getColor(Theme.key_profile_title));
                    }
                    r0.nameTextView[a].setTextSize(18);
                    r0.nameTextView[a].setGravity(3);
                    r0.nameTextView[a].setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                    r0.nameTextView[a].setLeftDrawableTopPadding(-AndroidUtilities.dp(1.3f));
                    r0.nameTextView[a].setPivotX(0.0f);
                    r0.nameTextView[a].setPivotY(0.0f);
                    f = 1.0f;
                    if (a != 0) {
                    }
                    r0.nameTextView[a].setAlpha(a != 0 ? 0.0f : 1.0f);
                    if (a != 0) {
                    }
                    frameLayout.addView(r0.nameTextView[a], LayoutHelper.createFrame(-2, -2.0f, 51, 118.0f, 0.0f, a != 0 ? 48.0f : 0.0f, 0.0f));
                    r0.onlineTextView[a] = new SimpleTextView(context2);
                    simpleTextView = r0.onlineTextView[a];
                    if (r0.user_id == 0) {
                        if (ChatObject.isChannel(r0.chat_id, r0.currentAccount)) {
                        }
                        i3 = r0.chat_id;
                        simpleTextView.setTextColor(AvatarDrawable.getProfileTextColorForId(i3));
                        r0.onlineTextView[a].setTextSize(14);
                        r0.onlineTextView[a].setGravity(3);
                        simpleTextView = r0.onlineTextView[a];
                        if (a == 0) {
                            f = 0.0f;
                        }
                        simpleTextView.setAlpha(f);
                        if (a != 0) {
                        }
                        frameLayout.addView(r0.onlineTextView[a], LayoutHelper.createFrame(-2, -2.0f, 51, 118.0f, 0.0f, a != 0 ? 48.0f : 8.0f, 0.0f));
                        a++;
                    }
                    i3 = 5;
                    simpleTextView.setTextColor(AvatarDrawable.getProfileTextColorForId(i3));
                    r0.onlineTextView[a].setTextSize(14);
                    r0.onlineTextView[a].setGravity(3);
                    simpleTextView = r0.onlineTextView[a];
                    if (a == 0) {
                        f = 0.0f;
                    }
                    simpleTextView.setAlpha(f);
                    if (a != 0) {
                    }
                    frameLayout.addView(r0.onlineTextView[a], LayoutHelper.createFrame(-2, -2.0f, 51, 118.0f, 0.0f, a != 0 ? 48.0f : 8.0f, 0.0f));
                    a++;
                }
                r0.writeButton = new ImageView(context2);
                f2 = 56.0f;
                drawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56.0f), Theme.getColor(Theme.key_profile_actionBackground), Theme.getColor(Theme.key_profile_actionPressedBackground));
                if (VERSION.SDK_INT < 21) {
                    shadowDrawable = context.getResources().getDrawable(R.drawable.floating_shadow_profile).mutate();
                    shadowDrawable.setColorFilter(new PorterDuffColorFilter(Theme.ACTION_BAR_VIDEO_EDIT_COLOR, Mode.MULTIPLY));
                    combinedDrawable = new CombinedDrawable(shadowDrawable, drawable, 0, 0);
                    combinedDrawable.setIconSize(AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
                    drawable = combinedDrawable;
                }
                r0.writeButton.setBackgroundDrawable(drawable);
                r0.writeButton.setScaleType(ScaleType.CENTER);
                r0.writeButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_profile_actionIcon), Mode.MULTIPLY));
                if (r0.user_id == 0) {
                    r0.writeButton.setImageResource(R.drawable.floating_message);
                    r0.writeButton.setPadding(0, AndroidUtilities.dp(3.0f), 0, 0);
                } else if (r0.chat_id != 0) {
                    isChannel = ChatObject.isChannel(r0.currentChat);
                    if (isChannel) {
                    }
                    r0.writeButton.setImageResource(R.drawable.floating_camera);
                }
                view = r0.writeButton;
                if (VERSION.SDK_INT < 21) {
                }
                i4 = VERSION.SDK_INT < 21 ? 56 : 60;
                if (VERSION.SDK_INT < 21) {
                    f2 = 60.0f;
                }
                frameLayout.addView(view, LayoutHelper.createFrame(i4, f2, 53, 0.0f, 0.0f, 16.0f, 0.0f));
                if (VERSION.SDK_INT >= 21) {
                    animator = new StateListAnimator();
                    animator.addState(new int[]{16842919}, ObjectAnimator.ofFloat(r0.writeButton, "translationZ", new float[]{(float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(4.0f)}).setDuration(200));
                    animator.addState(new int[0], ObjectAnimator.ofFloat(r0.writeButton, "translationZ", new float[]{(float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(2.0f)}).setDuration(200));
                    r0.writeButton.setStateListAnimator(animator);
                    r0.writeButton.setOutlineProvider(/* anonymous class already generated */);
                }
                r0.writeButton.setOnClickListener(/* anonymous class already generated */);
                needLayout();
                r0.listView.setOnScrollListener(/* anonymous class already generated */);
                return r0.fragmentView;
            }
        }
        i = 5;
        recyclerListView.setGlowColor(AvatarDrawable.getProfileBackColorForId(i));
        frameLayout.addView(r0.listView, LayoutHelper.createFrame(-1, -1, 51));
        r0.listView.setAdapter(r0.listAdapter);
        r0.listView.setOnItemClickListener(new C22619());
        r0.listView.setOnItemLongClickListener(/* anonymous class already generated */);
        if (r0.banFromGroup == 0) {
            r0.listView.setPadding(0, AndroidUtilities.dp(88.0f), 0, 0);
        } else {
            if (r0.currentChannelParticipant == null) {
                TL_channels_getParticipant req2 = new TL_channels_getParticipant();
                req2.channel = MessagesController.getInstance(r0.currentAccount).getInputChannel(r0.banFromGroup);
                req2.user_id = MessagesController.getInstance(r0.currentAccount).getInputUser(r0.user_id);
                ConnectionsManager.getInstance(r0.currentAccount).sendRequest(req2, /* anonymous class already generated */);
            }
            FrameLayout frameLayout12 = /* anonymous class already generated */;
            frameLayout12.setWillNotDraw(false);
            frameLayout.addView(frameLayout12, LayoutHelper.createFrame(-1, 51, 83));
            frameLayout12.setOnClickListener(/* anonymous class already generated */);
            TextView textView2 = new TextView(context2);
            textView2.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText));
            textView2.setTextSize(1, 15.0f);
            textView2.setGravity(17);
            textView2.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            textView2.setText(LocaleController.getString("BanFromTheGroup", R.string.BanFromTheGroup));
            frameLayout12.addView(textView2, LayoutHelper.createFrame(-2, -2.0f, 17, 0.0f, 1.0f, 0.0f, 0.0f));
            r0.listView.setPadding(0, AndroidUtilities.dp(88.0f), 0, AndroidUtilities.dp(48.0f));
            r0.listView.setBottomGlowOffset(AndroidUtilities.dp(48.0f));
        }
        r0.topView = new TopView(context2);
        topView = r0.topView;
        if (r0.user_id == 0) {
            if (ChatObject.isChannel(r0.chat_id, r0.currentAccount)) {
            }
            i2 = r0.chat_id;
            topView.setBackgroundColor(AvatarDrawable.getProfileBackColorForId(i2));
            frameLayout.addView(r0.topView);
            frameLayout.addView(r0.actionBar);
            r0.avatarImage = new BackupImageView(context2);
            r0.avatarImage.setRoundRadius(AndroidUtilities.dp(21.0f));
            r0.avatarImage.setPivotX(0.0f);
            r0.avatarImage.setPivotY(0.0f);
            frameLayout.addView(r0.avatarImage, LayoutHelper.createFrame(42, 42.0f, 51, 64.0f, 0.0f, 0.0f, 0.0f));
            r0.avatarImage.setOnClickListener(/* anonymous class already generated */);
            a = 0;
            while (a < 2) {
                if (r0.playProfileAnimation) {
                }
                r0.nameTextView[a] = new SimpleTextView(context2);
                if (a != 1) {
                    r0.nameTextView[a].setTextColor(Theme.getColor(Theme.key_profile_title));
                } else {
                    r0.nameTextView[a].setTextColor(Theme.getColor(Theme.key_actionBarDefaultTitle));
                }
                r0.nameTextView[a].setTextSize(18);
                r0.nameTextView[a].setGravity(3);
                r0.nameTextView[a].setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                r0.nameTextView[a].setLeftDrawableTopPadding(-AndroidUtilities.dp(1.3f));
                r0.nameTextView[a].setPivotX(0.0f);
                r0.nameTextView[a].setPivotY(0.0f);
                f = 1.0f;
                if (a != 0) {
                }
                r0.nameTextView[a].setAlpha(a != 0 ? 0.0f : 1.0f);
                if (a != 0) {
                }
                frameLayout.addView(r0.nameTextView[a], LayoutHelper.createFrame(-2, -2.0f, 51, 118.0f, 0.0f, a != 0 ? 48.0f : 0.0f, 0.0f));
                r0.onlineTextView[a] = new SimpleTextView(context2);
                simpleTextView = r0.onlineTextView[a];
                if (r0.user_id == 0) {
                    if (ChatObject.isChannel(r0.chat_id, r0.currentAccount)) {
                    }
                    i3 = r0.chat_id;
                    simpleTextView.setTextColor(AvatarDrawable.getProfileTextColorForId(i3));
                    r0.onlineTextView[a].setTextSize(14);
                    r0.onlineTextView[a].setGravity(3);
                    simpleTextView = r0.onlineTextView[a];
                    if (a == 0) {
                        f = 0.0f;
                    }
                    simpleTextView.setAlpha(f);
                    if (a != 0) {
                    }
                    frameLayout.addView(r0.onlineTextView[a], LayoutHelper.createFrame(-2, -2.0f, 51, 118.0f, 0.0f, a != 0 ? 48.0f : 8.0f, 0.0f));
                    a++;
                }
                i3 = 5;
                simpleTextView.setTextColor(AvatarDrawable.getProfileTextColorForId(i3));
                r0.onlineTextView[a].setTextSize(14);
                r0.onlineTextView[a].setGravity(3);
                simpleTextView = r0.onlineTextView[a];
                if (a == 0) {
                    f = 0.0f;
                }
                simpleTextView.setAlpha(f);
                if (a != 0) {
                }
                frameLayout.addView(r0.onlineTextView[a], LayoutHelper.createFrame(-2, -2.0f, 51, 118.0f, 0.0f, a != 0 ? 48.0f : 8.0f, 0.0f));
                a++;
            }
            r0.writeButton = new ImageView(context2);
            f2 = 56.0f;
            drawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56.0f), Theme.getColor(Theme.key_profile_actionBackground), Theme.getColor(Theme.key_profile_actionPressedBackground));
            if (VERSION.SDK_INT < 21) {
                shadowDrawable = context.getResources().getDrawable(R.drawable.floating_shadow_profile).mutate();
                shadowDrawable.setColorFilter(new PorterDuffColorFilter(Theme.ACTION_BAR_VIDEO_EDIT_COLOR, Mode.MULTIPLY));
                combinedDrawable = new CombinedDrawable(shadowDrawable, drawable, 0, 0);
                combinedDrawable.setIconSize(AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
                drawable = combinedDrawable;
            }
            r0.writeButton.setBackgroundDrawable(drawable);
            r0.writeButton.setScaleType(ScaleType.CENTER);
            r0.writeButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_profile_actionIcon), Mode.MULTIPLY));
            if (r0.user_id == 0) {
                r0.writeButton.setImageResource(R.drawable.floating_message);
                r0.writeButton.setPadding(0, AndroidUtilities.dp(3.0f), 0, 0);
            } else if (r0.chat_id != 0) {
                isChannel = ChatObject.isChannel(r0.currentChat);
                if (isChannel) {
                }
                r0.writeButton.setImageResource(R.drawable.floating_camera);
            }
            view = r0.writeButton;
            if (VERSION.SDK_INT < 21) {
            }
            i4 = VERSION.SDK_INT < 21 ? 56 : 60;
            if (VERSION.SDK_INT < 21) {
                f2 = 60.0f;
            }
            frameLayout.addView(view, LayoutHelper.createFrame(i4, f2, 53, 0.0f, 0.0f, 16.0f, 0.0f));
            if (VERSION.SDK_INT >= 21) {
                animator = new StateListAnimator();
                animator.addState(new int[]{16842919}, ObjectAnimator.ofFloat(r0.writeButton, "translationZ", new float[]{(float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(4.0f)}).setDuration(200));
                animator.addState(new int[0], ObjectAnimator.ofFloat(r0.writeButton, "translationZ", new float[]{(float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(2.0f)}).setDuration(200));
                r0.writeButton.setStateListAnimator(animator);
                r0.writeButton.setOutlineProvider(/* anonymous class already generated */);
            }
            r0.writeButton.setOnClickListener(/* anonymous class already generated */);
            needLayout();
            r0.listView.setOnScrollListener(/* anonymous class already generated */);
            return r0.fragmentView;
        }
        i2 = 5;
        topView.setBackgroundColor(AvatarDrawable.getProfileBackColorForId(i2));
        frameLayout.addView(r0.topView);
        frameLayout.addView(r0.actionBar);
        r0.avatarImage = new BackupImageView(context2);
        r0.avatarImage.setRoundRadius(AndroidUtilities.dp(21.0f));
        r0.avatarImage.setPivotX(0.0f);
        r0.avatarImage.setPivotY(0.0f);
        frameLayout.addView(r0.avatarImage, LayoutHelper.createFrame(42, 42.0f, 51, 64.0f, 0.0f, 0.0f, 0.0f));
        r0.avatarImage.setOnClickListener(/* anonymous class already generated */);
        a = 0;
        while (a < 2) {
            if (r0.playProfileAnimation) {
            }
            r0.nameTextView[a] = new SimpleTextView(context2);
            if (a != 1) {
                r0.nameTextView[a].setTextColor(Theme.getColor(Theme.key_actionBarDefaultTitle));
            } else {
                r0.nameTextView[a].setTextColor(Theme.getColor(Theme.key_profile_title));
            }
            r0.nameTextView[a].setTextSize(18);
            r0.nameTextView[a].setGravity(3);
            r0.nameTextView[a].setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            r0.nameTextView[a].setLeftDrawableTopPadding(-AndroidUtilities.dp(1.3f));
            r0.nameTextView[a].setPivotX(0.0f);
            r0.nameTextView[a].setPivotY(0.0f);
            f = 1.0f;
            if (a != 0) {
            }
            r0.nameTextView[a].setAlpha(a != 0 ? 0.0f : 1.0f);
            if (a != 0) {
            }
            frameLayout.addView(r0.nameTextView[a], LayoutHelper.createFrame(-2, -2.0f, 51, 118.0f, 0.0f, a != 0 ? 48.0f : 0.0f, 0.0f));
            r0.onlineTextView[a] = new SimpleTextView(context2);
            simpleTextView = r0.onlineTextView[a];
            if (r0.user_id == 0) {
                if (ChatObject.isChannel(r0.chat_id, r0.currentAccount)) {
                }
                i3 = r0.chat_id;
                simpleTextView.setTextColor(AvatarDrawable.getProfileTextColorForId(i3));
                r0.onlineTextView[a].setTextSize(14);
                r0.onlineTextView[a].setGravity(3);
                simpleTextView = r0.onlineTextView[a];
                if (a == 0) {
                    f = 0.0f;
                }
                simpleTextView.setAlpha(f);
                if (a != 0) {
                }
                frameLayout.addView(r0.onlineTextView[a], LayoutHelper.createFrame(-2, -2.0f, 51, 118.0f, 0.0f, a != 0 ? 48.0f : 8.0f, 0.0f));
                a++;
            }
            i3 = 5;
            simpleTextView.setTextColor(AvatarDrawable.getProfileTextColorForId(i3));
            r0.onlineTextView[a].setTextSize(14);
            r0.onlineTextView[a].setGravity(3);
            simpleTextView = r0.onlineTextView[a];
            if (a == 0) {
                f = 0.0f;
            }
            simpleTextView.setAlpha(f);
            if (a != 0) {
            }
            frameLayout.addView(r0.onlineTextView[a], LayoutHelper.createFrame(-2, -2.0f, 51, 118.0f, 0.0f, a != 0 ? 48.0f : 8.0f, 0.0f));
            a++;
        }
        r0.writeButton = new ImageView(context2);
        f2 = 56.0f;
        drawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56.0f), Theme.getColor(Theme.key_profile_actionBackground), Theme.getColor(Theme.key_profile_actionPressedBackground));
        if (VERSION.SDK_INT < 21) {
            shadowDrawable = context.getResources().getDrawable(R.drawable.floating_shadow_profile).mutate();
            shadowDrawable.setColorFilter(new PorterDuffColorFilter(Theme.ACTION_BAR_VIDEO_EDIT_COLOR, Mode.MULTIPLY));
            combinedDrawable = new CombinedDrawable(shadowDrawable, drawable, 0, 0);
            combinedDrawable.setIconSize(AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
            drawable = combinedDrawable;
        }
        r0.writeButton.setBackgroundDrawable(drawable);
        r0.writeButton.setScaleType(ScaleType.CENTER);
        r0.writeButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_profile_actionIcon), Mode.MULTIPLY));
        if (r0.user_id == 0) {
            r0.writeButton.setImageResource(R.drawable.floating_message);
            r0.writeButton.setPadding(0, AndroidUtilities.dp(3.0f), 0, 0);
        } else if (r0.chat_id != 0) {
            isChannel = ChatObject.isChannel(r0.currentChat);
            if (isChannel) {
            }
            r0.writeButton.setImageResource(R.drawable.floating_camera);
        }
        view = r0.writeButton;
        if (VERSION.SDK_INT < 21) {
        }
        i4 = VERSION.SDK_INT < 21 ? 56 : 60;
        if (VERSION.SDK_INT < 21) {
            f2 = 60.0f;
        }
        frameLayout.addView(view, LayoutHelper.createFrame(i4, f2, 53, 0.0f, 0.0f, 16.0f, 0.0f));
        if (VERSION.SDK_INT >= 21) {
            animator = new StateListAnimator();
            animator.addState(new int[]{16842919}, ObjectAnimator.ofFloat(r0.writeButton, "translationZ", new float[]{(float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(4.0f)}).setDuration(200));
            animator.addState(new int[0], ObjectAnimator.ofFloat(r0.writeButton, "translationZ", new float[]{(float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(2.0f)}).setDuration(200));
            r0.writeButton.setStateListAnimator(animator);
            r0.writeButton.setOutlineProvider(/* anonymous class already generated */);
        }
        r0.writeButton.setOnClickListener(/* anonymous class already generated */);
        needLayout();
        r0.listView.setOnScrollListener(/* anonymous class already generated */);
        return r0.fragmentView;
    }

    private boolean processOnClickOrPress(final int position) {
        final User user;
        Builder builder;
        Chat chat;
        if (position != this.usernameRow) {
            if (position != this.channelNameRow) {
                if (position == this.phoneRow) {
                    user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.user_id));
                    if (!(user == null || user.phone == null || user.phone.length() == 0)) {
                        if (getParentActivity() != null) {
                            builder = new Builder(getParentActivity());
                            ArrayList<CharSequence> items = new ArrayList();
                            final ArrayList<Integer> actions = new ArrayList();
                            TL_userFull userFull = MessagesController.getInstance(this.currentAccount).getUserFull(user.id);
                            if (userFull != null && userFull.phone_calls_available) {
                                items.add(LocaleController.getString("CallViaTelegram", R.string.CallViaTelegram));
                                actions.add(Integer.valueOf(2));
                            }
                            items.add(LocaleController.getString("Call", R.string.Call));
                            actions.add(Integer.valueOf(0));
                            items.add(LocaleController.getString("Copy", R.string.Copy));
                            actions.add(Integer.valueOf(1));
                            builder.setItems((CharSequence[]) items.toArray(new CharSequence[items.size()]), new OnClickListener() {
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    i = ((Integer) actions.get(i)).intValue();
                                    StringBuilder stringBuilder;
                                    if (i == 0) {
                                        try {
                                            stringBuilder = new StringBuilder();
                                            stringBuilder.append("tel:+");
                                            stringBuilder.append(user.phone);
                                            Intent intent = new Intent("android.intent.action.DIAL", Uri.parse(stringBuilder.toString()));
                                            intent.addFlags(268435456);
                                            ProfileActivity.this.getParentActivity().startActivityForResult(intent, 500);
                                        } catch (Throwable e) {
                                            FileLog.m3e(e);
                                        }
                                    } else if (i == 1) {
                                        try {
                                            ClipboardManager clipboard = (ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard");
                                            stringBuilder = new StringBuilder();
                                            stringBuilder.append("+");
                                            stringBuilder.append(user.phone);
                                            clipboard.setPrimaryClip(ClipData.newPlainText("label", stringBuilder.toString()));
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
                        }
                    }
                    return false;
                }
                if (!(position == this.channelInfoRow || position == this.userInfoRow)) {
                    if (position != this.userInfoDetailedRow) {
                        return false;
                    }
                }
                Builder builder2 = new Builder(getParentActivity());
                builder2.setItems(new CharSequence[]{LocaleController.getString("Copy", R.string.Copy)}, new OnClickListener() {
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
                showDialog(builder2.create());
                return true;
            }
        }
        if (position == this.usernameRow) {
            user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.user_id));
            if (user != null) {
                if (user.username != null) {
                    chat = user.username;
                }
            }
            return false;
        }
        String username = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.chat_id));
        if (username != null) {
            if (username.username != null) {
                chat = username.username;
            }
        }
        return false;
        builder = new Builder(getParentActivity());
        builder.setItems(new CharSequence[]{LocaleController.getString("Copy", R.string.Copy)}, new OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    try {
                        ClipboardManager clipboard = (ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard");
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("@");
                        stringBuilder.append(chat);
                        clipboard.setPrimaryClip(ClipData.newPlainText("label", stringBuilder.toString()));
                    } catch (Throwable e) {
                        FileLog.m3e(e);
                    }
                }
            }
        });
        showDialog(builder.create());
        return true;
    }

    private void leaveChatPressed() {
        Builder builder = new Builder(getParentActivity());
        boolean isChannel = ChatObject.isChannel(this.chat_id, this.currentAccount);
        int i = R.string.AreYouSureDeleteAndExit;
        if (!isChannel || this.currentChat.megagroup) {
            builder.setMessage(LocaleController.getString("AreYouSureDeleteAndExit", R.string.AreYouSureDeleteAndExit));
        } else {
            String str;
            if (ChatObject.isChannel(this.chat_id, this.currentAccount)) {
                str = "ChannelLeaveAlert";
                i = R.string.ChannelLeaveAlert;
            } else {
                str = "AreYouSureDeleteAndExit";
            }
            builder.setMessage(LocaleController.getString(str, i));
        }
        builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), new OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                ProfileActivity.this.kickUser(0);
            }
        });
        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
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
        if (!(this.loadingUsers || this.participantsMap == null)) {
            if (this.info != null) {
                this.loadingUsers = true;
                int i = 0;
                final int delay = (this.participantsMap.size() == 0 || !reload) ? 0 : 300;
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
    }

    private void openAddMember() {
        Bundle args = new Bundle();
        args.putBoolean("onlyUsers", true);
        args.putBoolean("destroyAfterSelect", true);
        args.putBoolean("returnAsResult", true);
        args.putBoolean("needForwardCount", true ^ ChatObject.isChannel(this.currentChat));
        if (this.chat_id > 0) {
            if (ChatObject.canAddViaLink(this.currentChat)) {
                args.putInt("chat_id", this.currentChat.id);
            }
            args.putString("selectAlertString", LocaleController.getString("AddToTheGroup", R.string.AddToTheGroup));
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
        if (this.listView.getChildCount() > 0) {
            if (!this.openAnimationInProgress) {
                boolean z = false;
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
    }

    private void needLayout() {
        int newTop = (this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0) + ActionBar.getCurrentActionBarHeight();
        if (!(this.listView == null || this.openAnimationInProgress)) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.listView.getLayoutParams();
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
                        AnimatorSet old;
                        if (setVisible) {
                            this.writeButton.setTag(null);
                        } else {
                            this.writeButton.setTag(Integer.valueOf(0));
                        }
                        if (this.writeButtonAnimation != null) {
                            old = this.writeButtonAnimation;
                            this.writeButtonAnimation = null;
                            old.cancel();
                        }
                        this.writeButtonAnimation = new AnimatorSet();
                        Animator[] animatorArr;
                        if (setVisible) {
                            this.writeButtonAnimation.setInterpolator(new DecelerateInterpolator());
                            AnimatorSet animatorSet = this.writeButtonAnimation;
                            animatorArr = new Animator[3];
                            animatorArr[0] = ObjectAnimator.ofFloat(this.writeButton, "scaleX", new float[]{1.0f});
                            animatorArr[1] = ObjectAnimator.ofFloat(this.writeButton, "scaleY", new float[]{1.0f});
                            animatorArr[2] = ObjectAnimator.ofFloat(this.writeButton, "alpha", new float[]{1.0f});
                            animatorSet.playTogether(animatorArr);
                        } else {
                            this.writeButtonAnimation.setInterpolator(new AccelerateInterpolator());
                            old = this.writeButtonAnimation;
                            animatorArr = new Animator[3];
                            animatorArr[0] = ObjectAnimator.ofFloat(this.writeButton, "scaleX", new float[]{0.2f});
                            animatorArr[1] = ObjectAnimator.ofFloat(this.writeButton, "scaleY", new float[]{0.2f});
                            animatorArr[2] = ObjectAnimator.ofFloat(this.writeButton, "alpha", new float[]{0.0f});
                            old.playTogether(animatorArr);
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
            this.avatarImage.setScaleX(((18.0f * diff) + 42.0f) / 42.0f);
            this.avatarImage.setScaleY(((18.0f * diff) + 42.0f) / 42.0f);
            this.avatarImage.setTranslationX(((float) (-AndroidUtilities.dp(47.0f))) * diff);
            this.avatarImage.setTranslationY((float) Math.ceil((double) avatarY));
            for (int a = 0; a < 2; a++) {
                if (this.nameTextView[a] != null) {
                    this.nameTextView[a].setTranslationX((AndroidUtilities.density * -21.0f) * diff);
                    this.nameTextView[a].setTranslationY((((float) Math.floor((double) avatarY)) + ((float) AndroidUtilities.dp(1.3f))) + (((float) AndroidUtilities.dp(7.0f)) * diff));
                    this.onlineTextView[a].setTranslationX((-21.0f * AndroidUtilities.density) * diff);
                    this.onlineTextView[a].setTranslationY((((float) Math.floor((double) avatarY)) + ((float) AndroidUtilities.dp(24.0f))) + (((float) Math.floor((double) (11.0f * AndroidUtilities.density))) * diff));
                    this.nameTextView[a].setScaleX((0.12f * diff) + 1.0f);
                    this.nameTextView[a].setScaleY((0.12f * diff) + 1.0f);
                    if (a == 1 && !this.openAnimationInProgress) {
                        int width;
                        int i;
                        FrameLayout.LayoutParams layoutParams2;
                        if (AndroidUtilities.isTablet()) {
                            width = AndroidUtilities.dp(NUM);
                        } else {
                            width = AndroidUtilities.displaySize.x;
                        }
                        if (this.callItem == null) {
                            if (this.editItem == null) {
                                i = 0;
                                width = (int) (((float) (width - AndroidUtilities.dp(126.0f + (((float) (40 + i)) * (1.0f - diff))))) - this.nameTextView[a].getTranslationX());
                                layoutParams2 = (FrameLayout.LayoutParams) this.nameTextView[a].getLayoutParams();
                                if (((float) width) >= (this.nameTextView[a].getPaint().measureText(this.nameTextView[a].getText().toString()) * this.nameTextView[a].getScaleX()) + ((float) this.nameTextView[a].getSideDrawablesSize())) {
                                    layoutParams2.width = (int) Math.ceil((double) (((float) width) / this.nameTextView[a].getScaleX()));
                                } else {
                                    layoutParams2.width = -2;
                                }
                                this.nameTextView[a].setLayoutParams(layoutParams2);
                                layoutParams2 = (FrameLayout.LayoutParams) this.onlineTextView[a].getLayoutParams();
                                layoutParams2.rightMargin = (int) Math.ceil((double) ((this.onlineTextView[a].getTranslationX() + ((float) AndroidUtilities.dp(8.0f))) + (((float) AndroidUtilities.dp(40.0f)) * (1.0f - diff))));
                                this.onlineTextView[a].setLayoutParams(layoutParams2);
                            }
                        }
                        i = 48;
                        width = (int) (((float) (width - AndroidUtilities.dp(126.0f + (((float) (40 + i)) * (1.0f - diff))))) - this.nameTextView[a].getTranslationX());
                        layoutParams2 = (FrameLayout.LayoutParams) this.nameTextView[a].getLayoutParams();
                        if (((float) width) >= (this.nameTextView[a].getPaint().measureText(this.nameTextView[a].getText().toString()) * this.nameTextView[a].getScaleX()) + ((float) this.nameTextView[a].getSideDrawablesSize())) {
                            layoutParams2.width = -2;
                        } else {
                            layoutParams2.width = (int) Math.ceil((double) (((float) width) / this.nameTextView[a].getScaleX()));
                        }
                        this.nameTextView[a].setLayoutParams(layoutParams2);
                        layoutParams2 = (FrameLayout.LayoutParams) this.onlineTextView[a].getLayoutParams();
                        layoutParams2.rightMargin = (int) Math.ceil((double) ((this.onlineTextView[a].getTranslationX() + ((float) AndroidUtilities.dp(8.0f))) + (((float) AndroidUtilities.dp(40.0f)) * (1.0f - diff))));
                        this.onlineTextView[a].setLayoutParams(layoutParams2);
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

    public void didReceivedNotification(int id, int account, final Object... args) {
        int a = 0;
        int mask;
        Chat newChat;
        if (id == NotificationCenter.updateInterfaces) {
            mask = ((Integer) args[0]).intValue();
            if (this.user_id != 0) {
                if (!((mask & 2) == 0 && (mask & 1) == 0 && (mask & 4) == 0)) {
                    updateProfileData();
                }
                if (!((mask & 1024) == 0 || this.listView == 0)) {
                    Holder a2 = (Holder) this.listView.findViewHolderForPosition(this.phoneRow);
                    if (a2 != null) {
                        this.listAdapter.onBindViewHolder(a2, this.phoneRow);
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
                if (!(((mask & 2) == 0 && (mask & 1) == 0 && (mask & 4) == 0) || this.listView == null)) {
                    int count = this.listView.getChildCount();
                    while (a < count) {
                        View child = this.listView.getChildAt(a);
                        if (child instanceof UserCell) {
                            ((UserCell) child).update(mask);
                        }
                        a++;
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
                    mask = this.listView.getChildCount();
                    while (a < mask) {
                        Holder holder = (Holder) this.listView.getChildViewHolder(this.listView.getChildAt(a));
                        if (holder.getAdapterPosition() == this.sharedMediaRow) {
                            this.listAdapter.onBindViewHolder(holder, this.sharedMediaRow);
                            break;
                        }
                        a++;
                    }
                }
            }
        } else if (id == NotificationCenter.encryptedChatCreated) {
            if (this.creatingChat) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        NotificationCenter.getInstance(ProfileActivity.this.currentAccount).removeObserver(ProfileActivity.this, NotificationCenter.closeChats);
                        NotificationCenter.getInstance(ProfileActivity.this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                        EncryptedChat encryptedChat = args[0];
                        Bundle args2 = new Bundle();
                        args2.putInt("enc_id", encryptedChat.id);
                        ProfileActivity.this.presentFragment(new ChatActivity(args2), true);
                    }
                });
            }
        } else if (id == NotificationCenter.encryptedChatUpdated) {
            EncryptedChat chat = args[0];
            if (this.currentEncryptedChat != 0 && chat.id == this.currentEncryptedChat.id) {
                this.currentEncryptedChat = chat;
                updateRowsIds();
                if (this.listAdapter != 0) {
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
                if (this.info == null && (chatFull instanceof TL_channelFull)) {
                    a = 1;
                }
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
                if (this.currentChat.megagroup && !(a == 0 && byChannelUsers)) {
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
                if (this.listAdapter != 0) {
                    this.listAdapter.notifyDataSetChanged();
                }
            }
        } else if (id == NotificationCenter.userInfoDidLoaded) {
            if (((Integer) args[0]).intValue() == this.user_id) {
                if (this.openAnimationInProgress == 0 && this.callItem == 0) {
                    createActionBarMenu();
                } else {
                    this.recreateMenuAfterAnimation = true;
                }
                updateRowsIds();
                if (this.listAdapter != 0) {
                    this.listAdapter.notifyDataSetChanged();
                }
            }
        } else if (id == NotificationCenter.didReceivedNewMessages && ((Long) args[0]).longValue() == this.dialog_id) {
            ArrayList<MessageObject> arr = args[1];
            while (a < arr.size()) {
                MessageObject obj = (MessageObject) arr.get(a);
                if (this.currentEncryptedChat != null && obj.messageOwner.action != null && (obj.messageOwner.action instanceof TL_messageEncryptedAction) && (obj.messageOwner.action.encryptedAction instanceof TL_decryptedMessageActionSetMessageTTL)) {
                    TL_decryptedMessageActionSetMessageTTL action = obj.messageOwner.action.encryptedAction;
                    if (this.listAdapter != null) {
                        this.listAdapter.notifyDataSetChanged();
                    }
                }
                a++;
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
        int color;
        int actionBarColor;
        int r;
        int g;
        int b;
        int i;
        int titleColor;
        int a;
        int rD;
        int gD;
        int bD;
        int aD;
        int i2;
        int i3;
        int i4;
        int color2;
        int actionBarColor2;
        int r2;
        int g2;
        int i5;
        int color3;
        int subtitleColor;
        int r3;
        int g3;
        float f = progress;
        this.animationProgress = f;
        this.listView.setAlpha(f);
        this.listView.setTranslationX(((float) AndroidUtilities.dp(48.0f)) - (((float) AndroidUtilities.dp(48.0f)) * f));
        if (this.user_id == 0) {
            if (!ChatObject.isChannel(r0.chat_id, r0.currentAccount) || r0.currentChat.megagroup) {
                color = r0.chat_id;
                color = AvatarDrawable.getProfileBackColorForId(color);
                actionBarColor = Theme.getColor(Theme.key_actionBarDefault);
                r = Color.red(actionBarColor);
                g = Color.green(actionBarColor);
                b = Color.blue(actionBarColor);
                r0.topView.setBackgroundColor(Color.rgb(r + ((int) (((float) (Color.red(color) - r)) * f)), g + ((int) (((float) (Color.green(color) - g)) * f)), b + ((int) (((float) (Color.blue(color) - b)) * f))));
                if (r0.user_id == 0) {
                    if (ChatObject.isChannel(r0.chat_id, r0.currentAccount) || r0.currentChat.megagroup) {
                        i = r0.chat_id;
                        color = AvatarDrawable.getIconColorForId(i);
                        i = Theme.getColor(Theme.key_actionBarDefaultIcon);
                        r = Color.red(i);
                        g = Color.green(i);
                        b = Color.blue(i);
                        r0.actionBar.setItemsColor(Color.rgb(r + ((int) (((float) (Color.red(color) - r)) * f)), g + ((int) (((float) (Color.green(color) - g)) * f)), b + ((int) (((float) (Color.blue(color) - b)) * f))), false);
                        color = Theme.getColor(Theme.key_profile_title);
                        titleColor = Theme.getColor(Theme.key_actionBarDefaultTitle);
                        r = Color.red(titleColor);
                        g = Color.green(titleColor);
                        b = Color.blue(titleColor);
                        a = Color.alpha(titleColor);
                        rD = (int) (((float) (Color.red(color) - r)) * f);
                        gD = (int) (((float) (Color.green(color) - g)) * f);
                        bD = (int) (((float) (Color.blue(color) - b)) * f);
                        aD = (int) (((float) (Color.alpha(color) - a)) * f);
                        i2 = 0;
                        while (true) {
                            i3 = 2;
                            i4 = i2;
                            if (i4 >= 2) {
                                break;
                            }
                            if (r0.nameTextView[i4] != null) {
                                color2 = color;
                                actionBarColor2 = actionBarColor;
                                r2 = r;
                                g2 = g;
                            } else {
                                color2 = color;
                                actionBarColor2 = actionBarColor;
                                r2 = r;
                                g2 = g;
                                r0.nameTextView[i4].setTextColor(Color.argb(a + aD, r + rD, g + gD, b + bD));
                            }
                            i2 = i4 + 1;
                            color = color2;
                            actionBarColor = actionBarColor2;
                            r = r2;
                            g = g2;
                        }
                        actionBarColor2 = actionBarColor;
                        r2 = r;
                        g2 = g;
                        if (r0.user_id == 0) {
                            if (ChatObject.isChannel(r0.chat_id, r0.currentAccount) || r0.currentChat.megagroup) {
                                color = r0.chat_id;
                                color = AvatarDrawable.getProfileTextColorForId(color);
                                actionBarColor = Theme.getColor(Theme.key_actionBarDefaultSubtitle);
                                r = Color.red(actionBarColor);
                                g = Color.green(actionBarColor);
                                b = Color.blue(actionBarColor);
                                a = Color.alpha(actionBarColor);
                                rD = (int) (((float) (Color.red(color) - r)) * f);
                                gD = (int) (((float) (Color.green(color) - g)) * f);
                                bD = (int) (((float) (Color.blue(color) - b)) * f);
                                i4 = (int) (((float) (Color.alpha(color) - a)) * f);
                                i5 = 0;
                                while (true) {
                                    aD = i5;
                                    if (aD >= i3) {
                                        break;
                                    }
                                    if (r0.onlineTextView[aD] != null) {
                                        color3 = color;
                                        subtitleColor = actionBarColor;
                                        r3 = r;
                                        g3 = g;
                                    } else {
                                        color3 = color;
                                        subtitleColor = actionBarColor;
                                        r3 = r;
                                        g3 = g;
                                        r0.onlineTextView[aD].setTextColor(Color.argb(a + i4, r + rD, g + gD, b + bD));
                                    }
                                    i5 = aD + 1;
                                    color = color3;
                                    actionBarColor = subtitleColor;
                                    r = r3;
                                    g = g3;
                                    i3 = 2;
                                }
                                subtitleColor = actionBarColor;
                                r3 = r;
                                g3 = g;
                                r0.extraHeight = (int) (((float) r0.initialAnimationExtraHeight) * f);
                                color = AvatarDrawable.getProfileColorForId(r0.user_id == 0 ? r0.user_id : r0.chat_id);
                                i3 = AvatarDrawable.getColorForId(r0.user_id == 0 ? r0.user_id : r0.chat_id);
                                if (color != i3) {
                                    r0.avatarDrawable.setColor(Color.rgb(Color.red(i3) + ((int) (((float) (Color.red(color) - Color.red(i3))) * f)), Color.green(i3) + ((int) (((float) (Color.green(color) - Color.green(i3))) * f)), Color.blue(i3) + ((int) (((float) (Color.blue(color) - Color.blue(i3))) * f))));
                                    r0.avatarImage.invalidate();
                                }
                                needLayout();
                            }
                        }
                        color = 5;
                        color = AvatarDrawable.getProfileTextColorForId(color);
                        actionBarColor = Theme.getColor(Theme.key_actionBarDefaultSubtitle);
                        r = Color.red(actionBarColor);
                        g = Color.green(actionBarColor);
                        b = Color.blue(actionBarColor);
                        a = Color.alpha(actionBarColor);
                        rD = (int) (((float) (Color.red(color) - r)) * f);
                        gD = (int) (((float) (Color.green(color) - g)) * f);
                        bD = (int) (((float) (Color.blue(color) - b)) * f);
                        i4 = (int) (((float) (Color.alpha(color) - a)) * f);
                        i5 = 0;
                        while (true) {
                            aD = i5;
                            if (aD >= i3) {
                                break;
                            }
                            if (r0.onlineTextView[aD] != null) {
                                color3 = color;
                                subtitleColor = actionBarColor;
                                r3 = r;
                                g3 = g;
                                r0.onlineTextView[aD].setTextColor(Color.argb(a + i4, r + rD, g + gD, b + bD));
                            } else {
                                color3 = color;
                                subtitleColor = actionBarColor;
                                r3 = r;
                                g3 = g;
                            }
                            i5 = aD + 1;
                            color = color3;
                            actionBarColor = subtitleColor;
                            r = r3;
                            g = g3;
                            i3 = 2;
                        }
                        subtitleColor = actionBarColor;
                        r3 = r;
                        g3 = g;
                        r0.extraHeight = (int) (((float) r0.initialAnimationExtraHeight) * f);
                        if (r0.user_id == 0) {
                        }
                        color = AvatarDrawable.getProfileColorForId(r0.user_id == 0 ? r0.user_id : r0.chat_id);
                        if (r0.user_id == 0) {
                        }
                        i3 = AvatarDrawable.getColorForId(r0.user_id == 0 ? r0.user_id : r0.chat_id);
                        if (color != i3) {
                            r0.avatarDrawable.setColor(Color.rgb(Color.red(i3) + ((int) (((float) (Color.red(color) - Color.red(i3))) * f)), Color.green(i3) + ((int) (((float) (Color.green(color) - Color.green(i3))) * f)), Color.blue(i3) + ((int) (((float) (Color.blue(color) - Color.blue(i3))) * f))));
                            r0.avatarImage.invalidate();
                        }
                        needLayout();
                    }
                }
                i = 5;
                color = AvatarDrawable.getIconColorForId(i);
                i = Theme.getColor(Theme.key_actionBarDefaultIcon);
                r = Color.red(i);
                g = Color.green(i);
                b = Color.blue(i);
                r0.actionBar.setItemsColor(Color.rgb(r + ((int) (((float) (Color.red(color) - r)) * f)), g + ((int) (((float) (Color.green(color) - g)) * f)), b + ((int) (((float) (Color.blue(color) - b)) * f))), false);
                color = Theme.getColor(Theme.key_profile_title);
                titleColor = Theme.getColor(Theme.key_actionBarDefaultTitle);
                r = Color.red(titleColor);
                g = Color.green(titleColor);
                b = Color.blue(titleColor);
                a = Color.alpha(titleColor);
                rD = (int) (((float) (Color.red(color) - r)) * f);
                gD = (int) (((float) (Color.green(color) - g)) * f);
                bD = (int) (((float) (Color.blue(color) - b)) * f);
                aD = (int) (((float) (Color.alpha(color) - a)) * f);
                i2 = 0;
                while (true) {
                    i3 = 2;
                    i4 = i2;
                    if (i4 >= 2) {
                        break;
                    }
                    if (r0.nameTextView[i4] != null) {
                        color2 = color;
                        actionBarColor2 = actionBarColor;
                        r2 = r;
                        g2 = g;
                        r0.nameTextView[i4].setTextColor(Color.argb(a + aD, r + rD, g + gD, b + bD));
                    } else {
                        color2 = color;
                        actionBarColor2 = actionBarColor;
                        r2 = r;
                        g2 = g;
                    }
                    i2 = i4 + 1;
                    color = color2;
                    actionBarColor = actionBarColor2;
                    r = r2;
                    g = g2;
                }
                actionBarColor2 = actionBarColor;
                r2 = r;
                g2 = g;
                if (r0.user_id == 0) {
                    if (ChatObject.isChannel(r0.chat_id, r0.currentAccount)) {
                    }
                    color = r0.chat_id;
                    color = AvatarDrawable.getProfileTextColorForId(color);
                    actionBarColor = Theme.getColor(Theme.key_actionBarDefaultSubtitle);
                    r = Color.red(actionBarColor);
                    g = Color.green(actionBarColor);
                    b = Color.blue(actionBarColor);
                    a = Color.alpha(actionBarColor);
                    rD = (int) (((float) (Color.red(color) - r)) * f);
                    gD = (int) (((float) (Color.green(color) - g)) * f);
                    bD = (int) (((float) (Color.blue(color) - b)) * f);
                    i4 = (int) (((float) (Color.alpha(color) - a)) * f);
                    i5 = 0;
                    while (true) {
                        aD = i5;
                        if (aD >= i3) {
                            break;
                        }
                        if (r0.onlineTextView[aD] != null) {
                            color3 = color;
                            subtitleColor = actionBarColor;
                            r3 = r;
                            g3 = g;
                        } else {
                            color3 = color;
                            subtitleColor = actionBarColor;
                            r3 = r;
                            g3 = g;
                            r0.onlineTextView[aD].setTextColor(Color.argb(a + i4, r + rD, g + gD, b + bD));
                        }
                        i5 = aD + 1;
                        color = color3;
                        actionBarColor = subtitleColor;
                        r = r3;
                        g = g3;
                        i3 = 2;
                    }
                    subtitleColor = actionBarColor;
                    r3 = r;
                    g3 = g;
                    r0.extraHeight = (int) (((float) r0.initialAnimationExtraHeight) * f);
                    if (r0.user_id == 0) {
                    }
                    color = AvatarDrawable.getProfileColorForId(r0.user_id == 0 ? r0.user_id : r0.chat_id);
                    if (r0.user_id == 0) {
                    }
                    i3 = AvatarDrawable.getColorForId(r0.user_id == 0 ? r0.user_id : r0.chat_id);
                    if (color != i3) {
                        r0.avatarDrawable.setColor(Color.rgb(Color.red(i3) + ((int) (((float) (Color.red(color) - Color.red(i3))) * f)), Color.green(i3) + ((int) (((float) (Color.green(color) - Color.green(i3))) * f)), Color.blue(i3) + ((int) (((float) (Color.blue(color) - Color.blue(i3))) * f))));
                        r0.avatarImage.invalidate();
                    }
                    needLayout();
                }
                color = 5;
                color = AvatarDrawable.getProfileTextColorForId(color);
                actionBarColor = Theme.getColor(Theme.key_actionBarDefaultSubtitle);
                r = Color.red(actionBarColor);
                g = Color.green(actionBarColor);
                b = Color.blue(actionBarColor);
                a = Color.alpha(actionBarColor);
                rD = (int) (((float) (Color.red(color) - r)) * f);
                gD = (int) (((float) (Color.green(color) - g)) * f);
                bD = (int) (((float) (Color.blue(color) - b)) * f);
                i4 = (int) (((float) (Color.alpha(color) - a)) * f);
                i5 = 0;
                while (true) {
                    aD = i5;
                    if (aD >= i3) {
                        break;
                    }
                    if (r0.onlineTextView[aD] != null) {
                        color3 = color;
                        subtitleColor = actionBarColor;
                        r3 = r;
                        g3 = g;
                        r0.onlineTextView[aD].setTextColor(Color.argb(a + i4, r + rD, g + gD, b + bD));
                    } else {
                        color3 = color;
                        subtitleColor = actionBarColor;
                        r3 = r;
                        g3 = g;
                    }
                    i5 = aD + 1;
                    color = color3;
                    actionBarColor = subtitleColor;
                    r = r3;
                    g = g3;
                    i3 = 2;
                }
                subtitleColor = actionBarColor;
                r3 = r;
                g3 = g;
                r0.extraHeight = (int) (((float) r0.initialAnimationExtraHeight) * f);
                if (r0.user_id == 0) {
                }
                color = AvatarDrawable.getProfileColorForId(r0.user_id == 0 ? r0.user_id : r0.chat_id);
                if (r0.user_id == 0) {
                }
                i3 = AvatarDrawable.getColorForId(r0.user_id == 0 ? r0.user_id : r0.chat_id);
                if (color != i3) {
                    r0.avatarDrawable.setColor(Color.rgb(Color.red(i3) + ((int) (((float) (Color.red(color) - Color.red(i3))) * f)), Color.green(i3) + ((int) (((float) (Color.green(color) - Color.green(i3))) * f)), Color.blue(i3) + ((int) (((float) (Color.blue(color) - Color.blue(i3))) * f))));
                    r0.avatarImage.invalidate();
                }
                needLayout();
            }
        }
        color = 5;
        color = AvatarDrawable.getProfileBackColorForId(color);
        actionBarColor = Theme.getColor(Theme.key_actionBarDefault);
        r = Color.red(actionBarColor);
        g = Color.green(actionBarColor);
        b = Color.blue(actionBarColor);
        r0.topView.setBackgroundColor(Color.rgb(r + ((int) (((float) (Color.red(color) - r)) * f)), g + ((int) (((float) (Color.green(color) - g)) * f)), b + ((int) (((float) (Color.blue(color) - b)) * f))));
        if (r0.user_id == 0) {
            if (ChatObject.isChannel(r0.chat_id, r0.currentAccount)) {
            }
            i = r0.chat_id;
            color = AvatarDrawable.getIconColorForId(i);
            i = Theme.getColor(Theme.key_actionBarDefaultIcon);
            r = Color.red(i);
            g = Color.green(i);
            b = Color.blue(i);
            r0.actionBar.setItemsColor(Color.rgb(r + ((int) (((float) (Color.red(color) - r)) * f)), g + ((int) (((float) (Color.green(color) - g)) * f)), b + ((int) (((float) (Color.blue(color) - b)) * f))), false);
            color = Theme.getColor(Theme.key_profile_title);
            titleColor = Theme.getColor(Theme.key_actionBarDefaultTitle);
            r = Color.red(titleColor);
            g = Color.green(titleColor);
            b = Color.blue(titleColor);
            a = Color.alpha(titleColor);
            rD = (int) (((float) (Color.red(color) - r)) * f);
            gD = (int) (((float) (Color.green(color) - g)) * f);
            bD = (int) (((float) (Color.blue(color) - b)) * f);
            aD = (int) (((float) (Color.alpha(color) - a)) * f);
            i2 = 0;
            while (true) {
                i3 = 2;
                i4 = i2;
                if (i4 >= 2) {
                    break;
                }
                if (r0.nameTextView[i4] != null) {
                    color2 = color;
                    actionBarColor2 = actionBarColor;
                    r2 = r;
                    g2 = g;
                } else {
                    color2 = color;
                    actionBarColor2 = actionBarColor;
                    r2 = r;
                    g2 = g;
                    r0.nameTextView[i4].setTextColor(Color.argb(a + aD, r + rD, g + gD, b + bD));
                }
                i2 = i4 + 1;
                color = color2;
                actionBarColor = actionBarColor2;
                r = r2;
                g = g2;
            }
            actionBarColor2 = actionBarColor;
            r2 = r;
            g2 = g;
            if (r0.user_id == 0) {
                if (ChatObject.isChannel(r0.chat_id, r0.currentAccount)) {
                }
                color = r0.chat_id;
                color = AvatarDrawable.getProfileTextColorForId(color);
                actionBarColor = Theme.getColor(Theme.key_actionBarDefaultSubtitle);
                r = Color.red(actionBarColor);
                g = Color.green(actionBarColor);
                b = Color.blue(actionBarColor);
                a = Color.alpha(actionBarColor);
                rD = (int) (((float) (Color.red(color) - r)) * f);
                gD = (int) (((float) (Color.green(color) - g)) * f);
                bD = (int) (((float) (Color.blue(color) - b)) * f);
                i4 = (int) (((float) (Color.alpha(color) - a)) * f);
                i5 = 0;
                while (true) {
                    aD = i5;
                    if (aD >= i3) {
                        break;
                    }
                    if (r0.onlineTextView[aD] != null) {
                        color3 = color;
                        subtitleColor = actionBarColor;
                        r3 = r;
                        g3 = g;
                    } else {
                        color3 = color;
                        subtitleColor = actionBarColor;
                        r3 = r;
                        g3 = g;
                        r0.onlineTextView[aD].setTextColor(Color.argb(a + i4, r + rD, g + gD, b + bD));
                    }
                    i5 = aD + 1;
                    color = color3;
                    actionBarColor = subtitleColor;
                    r = r3;
                    g = g3;
                    i3 = 2;
                }
                subtitleColor = actionBarColor;
                r3 = r;
                g3 = g;
                r0.extraHeight = (int) (((float) r0.initialAnimationExtraHeight) * f);
                if (r0.user_id == 0) {
                }
                color = AvatarDrawable.getProfileColorForId(r0.user_id == 0 ? r0.user_id : r0.chat_id);
                if (r0.user_id == 0) {
                }
                i3 = AvatarDrawable.getColorForId(r0.user_id == 0 ? r0.user_id : r0.chat_id);
                if (color != i3) {
                    r0.avatarDrawable.setColor(Color.rgb(Color.red(i3) + ((int) (((float) (Color.red(color) - Color.red(i3))) * f)), Color.green(i3) + ((int) (((float) (Color.green(color) - Color.green(i3))) * f)), Color.blue(i3) + ((int) (((float) (Color.blue(color) - Color.blue(i3))) * f))));
                    r0.avatarImage.invalidate();
                }
                needLayout();
            }
            color = 5;
            color = AvatarDrawable.getProfileTextColorForId(color);
            actionBarColor = Theme.getColor(Theme.key_actionBarDefaultSubtitle);
            r = Color.red(actionBarColor);
            g = Color.green(actionBarColor);
            b = Color.blue(actionBarColor);
            a = Color.alpha(actionBarColor);
            rD = (int) (((float) (Color.red(color) - r)) * f);
            gD = (int) (((float) (Color.green(color) - g)) * f);
            bD = (int) (((float) (Color.blue(color) - b)) * f);
            i4 = (int) (((float) (Color.alpha(color) - a)) * f);
            i5 = 0;
            while (true) {
                aD = i5;
                if (aD >= i3) {
                    break;
                }
                if (r0.onlineTextView[aD] != null) {
                    color3 = color;
                    subtitleColor = actionBarColor;
                    r3 = r;
                    g3 = g;
                    r0.onlineTextView[aD].setTextColor(Color.argb(a + i4, r + rD, g + gD, b + bD));
                } else {
                    color3 = color;
                    subtitleColor = actionBarColor;
                    r3 = r;
                    g3 = g;
                }
                i5 = aD + 1;
                color = color3;
                actionBarColor = subtitleColor;
                r = r3;
                g = g3;
                i3 = 2;
            }
            subtitleColor = actionBarColor;
            r3 = r;
            g3 = g;
            r0.extraHeight = (int) (((float) r0.initialAnimationExtraHeight) * f);
            if (r0.user_id == 0) {
            }
            color = AvatarDrawable.getProfileColorForId(r0.user_id == 0 ? r0.user_id : r0.chat_id);
            if (r0.user_id == 0) {
            }
            i3 = AvatarDrawable.getColorForId(r0.user_id == 0 ? r0.user_id : r0.chat_id);
            if (color != i3) {
                r0.avatarDrawable.setColor(Color.rgb(Color.red(i3) + ((int) (((float) (Color.red(color) - Color.red(i3))) * f)), Color.green(i3) + ((int) (((float) (Color.green(color) - Color.green(i3))) * f)), Color.blue(i3) + ((int) (((float) (Color.blue(color) - Color.blue(i3))) * f))));
                r0.avatarImage.invalidate();
            }
            needLayout();
        }
        i = 5;
        color = AvatarDrawable.getIconColorForId(i);
        i = Theme.getColor(Theme.key_actionBarDefaultIcon);
        r = Color.red(i);
        g = Color.green(i);
        b = Color.blue(i);
        r0.actionBar.setItemsColor(Color.rgb(r + ((int) (((float) (Color.red(color) - r)) * f)), g + ((int) (((float) (Color.green(color) - g)) * f)), b + ((int) (((float) (Color.blue(color) - b)) * f))), false);
        color = Theme.getColor(Theme.key_profile_title);
        titleColor = Theme.getColor(Theme.key_actionBarDefaultTitle);
        r = Color.red(titleColor);
        g = Color.green(titleColor);
        b = Color.blue(titleColor);
        a = Color.alpha(titleColor);
        rD = (int) (((float) (Color.red(color) - r)) * f);
        gD = (int) (((float) (Color.green(color) - g)) * f);
        bD = (int) (((float) (Color.blue(color) - b)) * f);
        aD = (int) (((float) (Color.alpha(color) - a)) * f);
        i2 = 0;
        while (true) {
            i3 = 2;
            i4 = i2;
            if (i4 >= 2) {
                break;
            }
            if (r0.nameTextView[i4] != null) {
                color2 = color;
                actionBarColor2 = actionBarColor;
                r2 = r;
                g2 = g;
                r0.nameTextView[i4].setTextColor(Color.argb(a + aD, r + rD, g + gD, b + bD));
            } else {
                color2 = color;
                actionBarColor2 = actionBarColor;
                r2 = r;
                g2 = g;
            }
            i2 = i4 + 1;
            color = color2;
            actionBarColor = actionBarColor2;
            r = r2;
            g = g2;
        }
        actionBarColor2 = actionBarColor;
        r2 = r;
        g2 = g;
        if (r0.user_id == 0) {
            if (ChatObject.isChannel(r0.chat_id, r0.currentAccount)) {
            }
            color = r0.chat_id;
            color = AvatarDrawable.getProfileTextColorForId(color);
            actionBarColor = Theme.getColor(Theme.key_actionBarDefaultSubtitle);
            r = Color.red(actionBarColor);
            g = Color.green(actionBarColor);
            b = Color.blue(actionBarColor);
            a = Color.alpha(actionBarColor);
            rD = (int) (((float) (Color.red(color) - r)) * f);
            gD = (int) (((float) (Color.green(color) - g)) * f);
            bD = (int) (((float) (Color.blue(color) - b)) * f);
            i4 = (int) (((float) (Color.alpha(color) - a)) * f);
            i5 = 0;
            while (true) {
                aD = i5;
                if (aD >= i3) {
                    break;
                }
                if (r0.onlineTextView[aD] != null) {
                    color3 = color;
                    subtitleColor = actionBarColor;
                    r3 = r;
                    g3 = g;
                } else {
                    color3 = color;
                    subtitleColor = actionBarColor;
                    r3 = r;
                    g3 = g;
                    r0.onlineTextView[aD].setTextColor(Color.argb(a + i4, r + rD, g + gD, b + bD));
                }
                i5 = aD + 1;
                color = color3;
                actionBarColor = subtitleColor;
                r = r3;
                g = g3;
                i3 = 2;
            }
            subtitleColor = actionBarColor;
            r3 = r;
            g3 = g;
            r0.extraHeight = (int) (((float) r0.initialAnimationExtraHeight) * f);
            if (r0.user_id == 0) {
            }
            color = AvatarDrawable.getProfileColorForId(r0.user_id == 0 ? r0.user_id : r0.chat_id);
            if (r0.user_id == 0) {
            }
            i3 = AvatarDrawable.getColorForId(r0.user_id == 0 ? r0.user_id : r0.chat_id);
            if (color != i3) {
                r0.avatarDrawable.setColor(Color.rgb(Color.red(i3) + ((int) (((float) (Color.red(color) - Color.red(i3))) * f)), Color.green(i3) + ((int) (((float) (Color.green(color) - Color.green(i3))) * f)), Color.blue(i3) + ((int) (((float) (Color.blue(color) - Color.blue(i3))) * f))));
                r0.avatarImage.invalidate();
            }
            needLayout();
        }
        color = 5;
        color = AvatarDrawable.getProfileTextColorForId(color);
        actionBarColor = Theme.getColor(Theme.key_actionBarDefaultSubtitle);
        r = Color.red(actionBarColor);
        g = Color.green(actionBarColor);
        b = Color.blue(actionBarColor);
        a = Color.alpha(actionBarColor);
        rD = (int) (((float) (Color.red(color) - r)) * f);
        gD = (int) (((float) (Color.green(color) - g)) * f);
        bD = (int) (((float) (Color.blue(color) - b)) * f);
        i4 = (int) (((float) (Color.alpha(color) - a)) * f);
        i5 = 0;
        while (true) {
            aD = i5;
            if (aD >= i3) {
                break;
            }
            if (r0.onlineTextView[aD] != null) {
                color3 = color;
                subtitleColor = actionBarColor;
                r3 = r;
                g3 = g;
                r0.onlineTextView[aD].setTextColor(Color.argb(a + i4, r + rD, g + gD, b + bD));
            } else {
                color3 = color;
                subtitleColor = actionBarColor;
                r3 = r;
                g3 = g;
            }
            i5 = aD + 1;
            color = color3;
            actionBarColor = subtitleColor;
            r = r3;
            g = g3;
            i3 = 2;
        }
        subtitleColor = actionBarColor;
        r3 = r;
        g3 = g;
        r0.extraHeight = (int) (((float) r0.initialAnimationExtraHeight) * f);
        if (r0.user_id == 0) {
        }
        color = AvatarDrawable.getProfileColorForId(r0.user_id == 0 ? r0.user_id : r0.chat_id);
        if (r0.user_id == 0) {
        }
        i3 = AvatarDrawable.getColorForId(r0.user_id == 0 ? r0.user_id : r0.chat_id);
        if (color != i3) {
            r0.avatarDrawable.setColor(Color.rgb(Color.red(i3) + ((int) (((float) (Color.red(color) - Color.red(i3))) * f)), Color.green(i3) + ((int) (((float) (Color.green(color) - Color.green(i3))) * f)), Color.blue(i3) + ((int) (((float) (Color.blue(color) - Color.blue(i3))) * f))));
            r0.avatarImage.invalidate();
        }
        needLayout();
    }

    protected AnimatorSet onCustomTransitionAnimation(boolean isOpen, Runnable callback) {
        if (this.playProfileAnimation && r0.allowProfileAnimation) {
            final AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.setDuration(180);
            int i = 2;
            r0.listView.setLayerType(2, null);
            ActionBarMenu menu = r0.actionBar.createMenu();
            if (menu.getItem(10) == null && r0.animatingItem == null) {
                r0.animatingItem = menu.addItem(10, (int) R.drawable.ic_ab_other);
            }
            int a;
            if (isOpen) {
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) r0.onlineTextView[1].getLayoutParams();
                layoutParams.rightMargin = (int) ((-21.0f * AndroidUtilities.density) + ((float) AndroidUtilities.dp(8.0f)));
                r0.onlineTextView[1].setLayoutParams(layoutParams);
                int width = (int) Math.ceil((double) (((float) (AndroidUtilities.displaySize.x - AndroidUtilities.dp(126.0f))) + (21.0f * AndroidUtilities.density)));
                layoutParams = (FrameLayout.LayoutParams) r0.nameTextView[1].getLayoutParams();
                if (((float) width) < (r0.nameTextView[1].getPaint().measureText(r0.nameTextView[1].getText().toString()) * 1.12f) + ((float) r0.nameTextView[1].getSideDrawablesSize())) {
                    layoutParams.width = (int) Math.ceil((double) (((float) width) / 1.12f));
                } else {
                    layoutParams.width = -2;
                }
                r0.nameTextView[1].setLayoutParams(layoutParams);
                r0.initialAnimationExtraHeight = AndroidUtilities.dp(88.0f);
                r0.fragmentView.setBackgroundColor(0);
                setAnimationProgress(0.0f);
                ArrayList<Animator> animators = new ArrayList();
                animators.add(ObjectAnimator.ofFloat(r0, "animationProgress", new float[]{0.0f, 1.0f}));
                if (r0.writeButton != null) {
                    r0.writeButton.setScaleX(0.2f);
                    r0.writeButton.setScaleY(0.2f);
                    r0.writeButton.setAlpha(0.0f);
                    animators.add(ObjectAnimator.ofFloat(r0.writeButton, "scaleX", new float[]{1.0f}));
                    animators.add(ObjectAnimator.ofFloat(r0.writeButton, "scaleY", new float[]{1.0f}));
                    animators.add(ObjectAnimator.ofFloat(r0.writeButton, "alpha", new float[]{1.0f}));
                }
                a = 0;
                while (a < i) {
                    r0.onlineTextView[a].setAlpha(a == 0 ? 1.0f : 0.0f);
                    r0.nameTextView[a].setAlpha(a == 0 ? 1.0f : 0.0f);
                    Object obj = r0.onlineTextView[a];
                    String str = "alpha";
                    float[] fArr = new float[1];
                    fArr[0] = a == 0 ? 0.0f : 1.0f;
                    animators.add(ObjectAnimator.ofFloat(obj, str, fArr));
                    Object obj2 = r0.nameTextView[a];
                    String str2 = "alpha";
                    float[] fArr2 = new float[1];
                    fArr2[0] = a == 0 ? 0.0f : 1.0f;
                    animators.add(ObjectAnimator.ofFloat(obj2, str2, fArr2));
                    a++;
                    i = 2;
                }
                if (r0.animatingItem != null) {
                    r0.animatingItem.setAlpha(1.0f);
                    animators.add(ObjectAnimator.ofFloat(r0.animatingItem, "alpha", new float[]{0.0f}));
                }
                if (r0.callItem != null) {
                    r0.callItem.setAlpha(0.0f);
                    animators.add(ObjectAnimator.ofFloat(r0.callItem, "alpha", new float[]{1.0f}));
                }
                if (r0.editItem != null) {
                    r0.editItem.setAlpha(0.0f);
                    animators.add(ObjectAnimator.ofFloat(r0.editItem, "alpha", new float[]{1.0f}));
                }
                animatorSet.playTogether(animators);
            } else {
                r0.initialAnimationExtraHeight = r0.extraHeight;
                ArrayList<Animator> animators2 = new ArrayList();
                animators2.add(ObjectAnimator.ofFloat(r0, "animationProgress", new float[]{1.0f, 0.0f}));
                if (r0.writeButton != null) {
                    animators2.add(ObjectAnimator.ofFloat(r0.writeButton, "scaleX", new float[]{0.2f}));
                    animators2.add(ObjectAnimator.ofFloat(r0.writeButton, "scaleY", new float[]{0.2f}));
                    animators2.add(ObjectAnimator.ofFloat(r0.writeButton, "alpha", new float[]{0.0f}));
                }
                a = 0;
                while (a < 2) {
                    Object obj3 = r0.onlineTextView[a];
                    String str3 = "alpha";
                    float[] fArr3 = new float[1];
                    fArr3[0] = a == 0 ? 1.0f : 0.0f;
                    animators2.add(ObjectAnimator.ofFloat(obj3, str3, fArr3));
                    obj3 = r0.nameTextView[a];
                    str3 = "alpha";
                    fArr3 = new float[1];
                    fArr3[0] = a == 0 ? 1.0f : 0.0f;
                    animators2.add(ObjectAnimator.ofFloat(obj3, str3, fArr3));
                    a++;
                }
                if (r0.animatingItem != null) {
                    r0.animatingItem.setAlpha(0.0f);
                    animators2.add(ObjectAnimator.ofFloat(r0.animatingItem, "alpha", new float[]{1.0f}));
                }
                if (r0.callItem != null) {
                    r0.callItem.setAlpha(1.0f);
                    animators2.add(ObjectAnimator.ofFloat(r0.callItem, "alpha", new float[]{0.0f}));
                }
                if (r0.editItem != null) {
                    r0.editItem.setAlpha(1.0f);
                    animators2.add(ObjectAnimator.ofFloat(r0.editItem, "alpha", new float[]{0.0f}));
                }
                animatorSet.playTogether(animators2);
            }
            final Runnable runnable = callback;
            animatorSet.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    ProfileActivity.this.listView.setLayerType(0, null);
                    if (ProfileActivity.this.animatingItem != null) {
                        ProfileActivity.this.actionBar.createMenu().clearItems();
                        ProfileActivity.this.animatingItem = null;
                    }
                    runnable.run();
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
        runnable = callback;
        return null;
    }

    private void updateOnlineCount() {
        int a = 0;
        this.onlineCount = 0;
        int currentTime = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
        this.sortedUsers.clear();
        if ((this.info instanceof TL_chatFull) || ((this.info instanceof TL_channelFull) && this.info.participants_count <= Callback.DEFAULT_DRAG_ANIMATION_DURATION && this.info.participants != null)) {
            while (a < this.info.participants.participants.size()) {
                User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(((ChatParticipant) this.info.participants.participants.get(a)).user_id));
                if (!(user == null || user.status == null || ((user.status.expires <= currentTime && user.id != UserConfig.getInstance(this.currentAccount).getClientUserId()) || user.status.expires <= 10000))) {
                    this.onlineCount++;
                }
                this.sortedUsers.add(Integer.valueOf(a));
                a++;
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
                                if ((status1 >= 0 || status2 <= 0) && (status1 != 0 || status2 == 0)) {
                                    return ((status2 >= 0 || status1 <= 0) && (status2 != 0 || status1 == 0)) ? 0 : 1;
                                } else {
                                    return -1;
                                }
                            } else if (status1 > status2) {
                                return 1;
                            } else {
                                return status1 < status2 ? -1 : 0;
                            }
                        } else if (status1 > status2) {
                            return 1;
                        } else {
                            return status1 < status2 ? -1 : 0;
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
        if (this.currentChat != null) {
            if (this.currentChat.megagroup) {
                if ((this.info instanceof TL_channelFull) && this.info.participants != null) {
                    for (int a = 0; a < this.info.participants.participants.size(); a++) {
                        ChatParticipant chatParticipant = (ChatParticipant) this.info.participants.participants.get(a);
                        this.participantsMap.put(chatParticipant.user_id, chatParticipant);
                    }
                }
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
        boolean hasUsername = false;
        this.rowCount = 0;
        int i;
        if (this.user_id != 0) {
            int i2;
            User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.user_id));
            int i3 = this.rowCount;
            this.rowCount = i3 + 1;
            this.emptyRow = i3;
            if (!(this.isBot || TextUtils.isEmpty(user.phone))) {
                i3 = this.rowCount;
                this.rowCount = i3 + 1;
                this.phoneRow = i3;
            }
            TL_userFull userFull = MessagesController.getInstance(this.currentAccount).getUserFull(this.user_id);
            if (!(user == null || TextUtils.isEmpty(user.username))) {
                hasUsername = true;
            }
            if (!(userFull == null || TextUtils.isEmpty(userFull.about))) {
                if (this.phoneRow != -1) {
                    i2 = this.rowCount;
                    this.rowCount = i2 + 1;
                    this.userSectionRow = i2;
                }
                if (!hasUsername) {
                    if (!this.isBot) {
                        i2 = this.rowCount;
                        this.rowCount = i2 + 1;
                        this.userInfoDetailedRow = i2;
                    }
                }
                i2 = this.rowCount;
                this.rowCount = i2 + 1;
                this.userInfoRow = i2;
            }
            if (hasUsername) {
                i2 = this.rowCount;
                this.rowCount = i2 + 1;
                this.usernameRow = i2;
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
            if (!(user == null || this.isBot || this.currentEncryptedChat != null || user.id == UserConfig.getInstance(this.currentAccount).getClientUserId())) {
                i = this.rowCount;
                this.rowCount = i + 1;
                this.startSecretChatRow = i;
            }
        } else if (this.chat_id == 0) {
        } else {
            if (this.chat_id > 0) {
                int i4 = this.rowCount;
                this.rowCount = i4 + 1;
                this.emptyRow = i4;
                if (ChatObject.isChannel(this.currentChat) && (!(this.info == null || this.info.about == null || this.info.about.length() <= 0) || (this.currentChat.username != null && this.currentChat.username.length() > 0))) {
                    if (!(this.info == null || this.info.about == null || this.info.about.length() <= 0)) {
                        i4 = this.rowCount;
                        this.rowCount = i4 + 1;
                        this.channelInfoRow = i4;
                    }
                    if (this.currentChat.username != null && this.currentChat.username.length() > 0) {
                        i4 = this.rowCount;
                        this.rowCount = i4 + 1;
                        this.channelNameRow = i4;
                    }
                    i4 = this.rowCount;
                    this.rowCount = i4 + 1;
                    this.sectionRow = i4;
                }
                i4 = this.rowCount;
                this.rowCount = i4 + 1;
                this.settingsNotificationsRow = i4;
                i4 = this.rowCount;
                this.rowCount = i4 + 1;
                this.sharedMediaRow = i4;
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
                        i4 = this.rowCount;
                        this.rowCount = i4 + 1;
                        this.addMemberRow = i4;
                    }
                    if (this.currentChat.creator && this.info.participants.participants.size() >= MessagesController.getInstance(this.currentAccount).minGroupConvertSize) {
                        i4 = this.rowCount;
                        this.rowCount = i4 + 1;
                        this.convertRow = i4;
                    }
                }
                i4 = this.rowCount;
                this.rowCount = i4 + 1;
                this.emptyRowChat = i4;
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
        if (this.avatarImage != null) {
            if (r0.nameTextView != null) {
                User user;
                TLObject photo;
                FileLocation photoBig;
                String newString;
                String newString2;
                int a;
                Drawable leftIcon;
                Drawable rightIcon;
                Chat chat;
                boolean isChannel;
                int i;
                String newString3;
                int[] result;
                String shortNumber;
                String newString4;
                int a2;
                TLObject photo2;
                FileLocation photoBig2;
                int currentConnectionState = ConnectionsManager.getInstance(r0.currentAccount).getConnectionState();
                int i2 = 2;
                String onlineTextOverride;
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
                    if (r0.user_id != 0) {
                        user = MessagesController.getInstance(r0.currentAccount).getUser(Integer.valueOf(r0.user_id));
                        photo = null;
                        photoBig = null;
                        if (user.photo != null) {
                            photo = user.photo.photo_small;
                            photoBig = user.photo.photo_big;
                        }
                        r0.avatarDrawable.setInfo(user);
                        r0.avatarImage.setImage(photo, "50_50", r0.avatarDrawable);
                        newString = UserObject.getUserName(user);
                        if (user.id != UserConfig.getInstance(r0.currentAccount).getClientUserId()) {
                            newString2 = LocaleController.getString("ChatYourSelf", R.string.ChatYourSelf);
                            newString = LocaleController.getString("ChatYourSelfName", R.string.ChatYourSelfName);
                        } else {
                            if (user.id != 333000) {
                                if (user.id == 777000) {
                                    if (r0.isBot) {
                                        newString2 = LocaleController.formatUserStatus(r0.currentAccount, user);
                                    } else {
                                        newString2 = LocaleController.getString("Bot", R.string.Bot);
                                    }
                                }
                            }
                            newString2 = LocaleController.getString("ServiceNotifications", R.string.ServiceNotifications);
                        }
                        a = 0;
                        while (a < i2) {
                            if (r0.nameTextView[a] == null) {
                                if (a != 0 && user.id != UserConfig.getInstance(r0.currentAccount).getClientUserId() && user.id / 1000 != 777 && user.id / 1000 != 333 && user.phone != null && user.phone.length() != 0 && ContactsController.getInstance(r0.currentAccount).contactsDict.get(Integer.valueOf(user.id)) == null && (ContactsController.getInstance(r0.currentAccount).contactsDict.size() != 0 || !ContactsController.getInstance(r0.currentAccount).isLoadingContacts())) {
                                    String phoneString = PhoneFormat.getInstance();
                                    StringBuilder stringBuilder = new StringBuilder();
                                    stringBuilder.append("+");
                                    stringBuilder.append(user.phone);
                                    phoneString = phoneString.format(stringBuilder.toString());
                                    if (!r0.nameTextView[a].getText().equals(phoneString)) {
                                        r0.nameTextView[a].setText(phoneString);
                                    }
                                } else if (!r0.nameTextView[a].getText().equals(newString)) {
                                    r0.nameTextView[a].setText(newString);
                                }
                                if (a != 0 && onlineTextOverride != null) {
                                    r0.onlineTextView[a].setText(onlineTextOverride);
                                } else if (!r0.onlineTextView[a].getText().equals(newString2)) {
                                    r0.onlineTextView[a].setText(newString2);
                                }
                                leftIcon = r0.currentEncryptedChat == null ? Theme.chat_lockIconDrawable : null;
                                rightIcon = null;
                                if (a == 0) {
                                    rightIcon = MessagesController.getInstance(r0.currentAccount).isDialogMuted(r0.dialog_id == 0 ? r0.dialog_id : (long) r0.user_id) ? Theme.chat_muteIconDrawable : null;
                                } else if (user.verified) {
                                    rightIcon = new CombinedDrawable(Theme.profile_verifiedDrawable, Theme.profile_verifiedCheckDrawable);
                                }
                                r0.nameTextView[a].setLeftDrawable(leftIcon);
                                r0.nameTextView[a].setRightDrawable(rightIcon);
                            }
                            a++;
                            i2 = 2;
                        }
                        r0.avatarImage.getImageReceiver().setVisible(PhotoViewer.isShowingImage(photoBig) ^ true, false);
                    } else if (r0.chat_id != 0) {
                        chat = MessagesController.getInstance(r0.currentAccount).getChat(Integer.valueOf(r0.chat_id));
                        if (chat == null) {
                            r0.currentChat = chat;
                        } else {
                            chat = r0.currentChat;
                        }
                        isChannel = ChatObject.isChannel(chat);
                        i = Callback.DEFAULT_DRAG_ANIMATION_DURATION;
                        if (isChannel) {
                            newString3 = chat.participants_count;
                            if (r0.info != null) {
                                newString3 = r0.info.participants.participants.size();
                            }
                            if (newString3 != null || r0.onlineCount <= 1) {
                                newString3 = LocaleController.formatPluralString("Members", newString3);
                            } else {
                                newString3 = String.format("%s, %s", new Object[]{LocaleController.formatPluralString("Members", newString3), LocaleController.formatPluralString("OnlineCount", r0.onlineCount)});
                            }
                        } else {
                            if (r0.info != null) {
                                if (!r0.currentChat.megagroup) {
                                    if (!(r0.info.participants_count == 0 || r0.currentChat.admin)) {
                                        if (r0.info.can_view_participants) {
                                        }
                                    }
                                }
                                if (r0.currentChat.megagroup || r0.info.participants_count > Callback.DEFAULT_DRAG_ANIMATION_DURATION) {
                                    result = new int[1];
                                    shortNumber = LocaleController.formatShortNumber(r0.info.participants_count, result);
                                    if (r0.currentChat.megagroup) {
                                        newString4 = LocaleController.formatPluralString("Subscribers", result[0]).replace(String.format("%d", new Object[]{Integer.valueOf(result[0])}), shortNumber);
                                    } else {
                                        newString4 = LocaleController.formatPluralString("Members", result[0]).replace(String.format("%d", new Object[]{Integer.valueOf(result[0])}), shortNumber);
                                    }
                                    newString3 = newString4;
                                } else if (r0.onlineCount <= 1 || r0.info.participants_count == 0) {
                                    newString3 = LocaleController.formatPluralString("Members", r0.info.participants_count);
                                } else {
                                    newString3 = String.format("%s, %s", new Object[]{LocaleController.formatPluralString("Members", r0.info.participants_count), LocaleController.formatPluralString("OnlineCount", r0.onlineCount)});
                                }
                            }
                            if (r0.currentChat.megagroup) {
                                newString3 = LocaleController.getString("Loading", R.string.Loading).toLowerCase();
                            } else if ((chat.flags & 64) == 0) {
                                newString3 = LocaleController.getString("ChannelPublic", R.string.ChannelPublic).toLowerCase();
                            } else {
                                newString3 = LocaleController.getString("ChannelPrivate", R.string.ChannelPrivate).toLowerCase();
                            }
                        }
                        a2 = 0;
                        while (a2 < 2) {
                            if (r0.nameTextView[a2] == null) {
                                if (!(chat.title == null || r0.nameTextView[a2].getText().equals(chat.title))) {
                                    r0.nameTextView[a2].setText(chat.title);
                                }
                                r0.nameTextView[a2].setLeftDrawable(null);
                                if (a2 != 0) {
                                    r0.nameTextView[a2].setRightDrawable(MessagesController.getInstance(r0.currentAccount).isDialogMuted((long) (-r0.chat_id)) ? Theme.chat_muteIconDrawable : null);
                                } else if (chat.verified) {
                                    r0.nameTextView[a2].setRightDrawable(null);
                                } else {
                                    r0.nameTextView[a2].setRightDrawable(new CombinedDrawable(Theme.profile_verifiedDrawable, Theme.profile_verifiedCheckDrawable));
                                }
                                if (a2 != 0 && onlineTextOverride != null) {
                                    r0.onlineTextView[a2].setText(onlineTextOverride);
                                } else if (r0.currentChat.megagroup || r0.info == null || r0.info.participants_count > r6 || r0.onlineCount <= 0) {
                                    if (a2 != 0 && ChatObject.isChannel(r0.currentChat) && r0.info != null && r0.info.participants_count != 0 && (r0.currentChat.megagroup || r0.currentChat.broadcast)) {
                                        int[] result2 = new int[1];
                                        String shortNumber2 = LocaleController.formatShortNumber(r0.info.participants_count, result2);
                                        if (r0.currentChat.megagroup) {
                                            r0.onlineTextView[a2].setText(LocaleController.formatPluralString("Members", result2[0]).replace(String.format("%d", new Object[]{Integer.valueOf(result2[0])}), shortNumber2));
                                        } else {
                                            r0.onlineTextView[a2].setText(LocaleController.formatPluralString("Subscribers", result2[0]).replace(String.format("%d", new Object[]{Integer.valueOf(result2[0])}), shortNumber2));
                                        }
                                    } else if (!r0.onlineTextView[a2].getText().equals(newString3)) {
                                        r0.onlineTextView[a2].setText(newString3);
                                    }
                                } else if (!r0.onlineTextView[a2].getText().equals(newString3)) {
                                    r0.onlineTextView[a2].setText(newString3);
                                }
                            }
                            a2++;
                            i = Callback.DEFAULT_DRAG_ANIMATION_DURATION;
                        }
                        photo2 = null;
                        photoBig2 = null;
                        if (chat.photo != null) {
                            photo2 = chat.photo.photo_small;
                            photoBig2 = chat.photo.photo_big;
                        }
                        r0.avatarDrawable.setInfo(chat);
                        r0.avatarImage.setImage(photo2, "50_50", r0.avatarDrawable);
                        r0.avatarImage.getImageReceiver().setVisible(true ^ PhotoViewer.isShowingImage(photoBig2), false);
                    }
                }
                if (r0.user_id != 0) {
                    user = MessagesController.getInstance(r0.currentAccount).getUser(Integer.valueOf(r0.user_id));
                    photo = null;
                    photoBig = null;
                    if (user.photo != null) {
                        photo = user.photo.photo_small;
                        photoBig = user.photo.photo_big;
                    }
                    r0.avatarDrawable.setInfo(user);
                    r0.avatarImage.setImage(photo, "50_50", r0.avatarDrawable);
                    newString = UserObject.getUserName(user);
                    if (user.id != UserConfig.getInstance(r0.currentAccount).getClientUserId()) {
                        if (user.id != 333000) {
                            if (user.id == 777000) {
                                if (r0.isBot) {
                                    newString2 = LocaleController.formatUserStatus(r0.currentAccount, user);
                                } else {
                                    newString2 = LocaleController.getString("Bot", R.string.Bot);
                                }
                            }
                        }
                        newString2 = LocaleController.getString("ServiceNotifications", R.string.ServiceNotifications);
                    } else {
                        newString2 = LocaleController.getString("ChatYourSelf", R.string.ChatYourSelf);
                        newString = LocaleController.getString("ChatYourSelfName", R.string.ChatYourSelfName);
                    }
                    a = 0;
                    while (a < i2) {
                        if (r0.nameTextView[a] == null) {
                            if (a != 0) {
                            }
                            if (r0.nameTextView[a].getText().equals(newString)) {
                                r0.nameTextView[a].setText(newString);
                            }
                            if (a != 0) {
                            }
                            if (r0.onlineTextView[a].getText().equals(newString2)) {
                                r0.onlineTextView[a].setText(newString2);
                            }
                            if (r0.currentEncryptedChat == null) {
                            }
                            rightIcon = null;
                            if (a == 0) {
                                if (r0.dialog_id == 0) {
                                }
                                if (MessagesController.getInstance(r0.currentAccount).isDialogMuted(r0.dialog_id == 0 ? r0.dialog_id : (long) r0.user_id)) {
                                }
                                rightIcon = MessagesController.getInstance(r0.currentAccount).isDialogMuted(r0.dialog_id == 0 ? r0.dialog_id : (long) r0.user_id) ? Theme.chat_muteIconDrawable : null;
                            } else if (user.verified) {
                                rightIcon = new CombinedDrawable(Theme.profile_verifiedDrawable, Theme.profile_verifiedCheckDrawable);
                            }
                            r0.nameTextView[a].setLeftDrawable(leftIcon);
                            r0.nameTextView[a].setRightDrawable(rightIcon);
                        }
                        a++;
                        i2 = 2;
                    }
                    r0.avatarImage.getImageReceiver().setVisible(PhotoViewer.isShowingImage(photoBig) ^ true, false);
                } else if (r0.chat_id != 0) {
                    chat = MessagesController.getInstance(r0.currentAccount).getChat(Integer.valueOf(r0.chat_id));
                    if (chat == null) {
                        chat = r0.currentChat;
                    } else {
                        r0.currentChat = chat;
                    }
                    isChannel = ChatObject.isChannel(chat);
                    i = Callback.DEFAULT_DRAG_ANIMATION_DURATION;
                    if (isChannel) {
                        newString3 = chat.participants_count;
                        if (r0.info != null) {
                            newString3 = r0.info.participants.participants.size();
                        }
                        if (newString3 != null) {
                        }
                        newString3 = LocaleController.formatPluralString("Members", newString3);
                    } else {
                        if (r0.info != null) {
                            if (r0.currentChat.megagroup) {
                                if (r0.info.can_view_participants) {
                                }
                            }
                            if (r0.currentChat.megagroup) {
                            }
                            result = new int[1];
                            shortNumber = LocaleController.formatShortNumber(r0.info.participants_count, result);
                            if (r0.currentChat.megagroup) {
                                newString4 = LocaleController.formatPluralString("Subscribers", result[0]).replace(String.format("%d", new Object[]{Integer.valueOf(result[0])}), shortNumber);
                            } else {
                                newString4 = LocaleController.formatPluralString("Members", result[0]).replace(String.format("%d", new Object[]{Integer.valueOf(result[0])}), shortNumber);
                            }
                            newString3 = newString4;
                        }
                        if (r0.currentChat.megagroup) {
                            newString3 = LocaleController.getString("Loading", R.string.Loading).toLowerCase();
                        } else if ((chat.flags & 64) == 0) {
                            newString3 = LocaleController.getString("ChannelPrivate", R.string.ChannelPrivate).toLowerCase();
                        } else {
                            newString3 = LocaleController.getString("ChannelPublic", R.string.ChannelPublic).toLowerCase();
                        }
                    }
                    a2 = 0;
                    while (a2 < 2) {
                        if (r0.nameTextView[a2] == null) {
                            r0.nameTextView[a2].setText(chat.title);
                            r0.nameTextView[a2].setLeftDrawable(null);
                            if (a2 != 0) {
                                if (MessagesController.getInstance(r0.currentAccount).isDialogMuted((long) (-r0.chat_id))) {
                                }
                                r0.nameTextView[a2].setRightDrawable(MessagesController.getInstance(r0.currentAccount).isDialogMuted((long) (-r0.chat_id)) ? Theme.chat_muteIconDrawable : null);
                            } else if (chat.verified) {
                                r0.nameTextView[a2].setRightDrawable(null);
                            } else {
                                r0.nameTextView[a2].setRightDrawable(new CombinedDrawable(Theme.profile_verifiedDrawable, Theme.profile_verifiedCheckDrawable));
                            }
                            if (a2 != 0) {
                            }
                            if (r0.currentChat.megagroup) {
                            }
                            if (a2 != 0) {
                            }
                            if (!r0.onlineTextView[a2].getText().equals(newString3)) {
                                r0.onlineTextView[a2].setText(newString3);
                            }
                        }
                        a2++;
                        i = Callback.DEFAULT_DRAG_ANIMATION_DURATION;
                    }
                    photo2 = null;
                    photoBig2 = null;
                    if (chat.photo != null) {
                        photo2 = chat.photo.photo_small;
                        photoBig2 = chat.photo.photo_big;
                    }
                    r0.avatarDrawable.setInfo(chat);
                    r0.avatarImage.setImage(photo2, "50_50", r0.avatarDrawable);
                    r0.avatarImage.getImageReceiver().setVisible(true ^ PhotoViewer.isShowingImage(photoBig2), false);
                }
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
                TL_userFull userFull = MessagesController.getInstance(this.currentAccount).getUserFull(this.user_id);
                if (userFull != null && userFull.phone_calls_available) {
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
                            item.addSubItem(1, LocaleController.getString("AddContact", R.string.AddContact));
                            item.addSubItem(3, LocaleController.getString("ShareContact", R.string.ShareContact));
                            item.addSubItem(2, !this.userBlocked ? LocaleController.getString("BlockContact", R.string.BlockContact) : LocaleController.getString("Unblock", R.string.Unblock));
                        } else if (this.isBot) {
                            String str;
                            int i;
                            if (this.userBlocked) {
                                str = "BotRestart";
                                i = R.string.BotRestart;
                            } else {
                                str = "BotStop";
                                i = R.string.BotStop;
                            }
                            item.addSubItem(2, LocaleController.getString(str, i));
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
                if (this.writeButton != null) {
                    boolean isChannel = ChatObject.isChannel(this.currentChat);
                    if ((!isChannel || ChatObject.canChangeChatInfo(this.currentChat)) && (isChannel || this.currentChat.admin || this.currentChat.creator || !this.currentChat.admins_enabled)) {
                        this.writeButton.setImageResource(R.drawable.floating_camera);
                        this.writeButton.setPadding(0, 0, 0, 0);
                    } else {
                        this.writeButton.setImageResource(R.drawable.floating_message);
                        this.writeButton.setPadding(0, AndroidUtilities.dp(3.0f), 0, 0);
                    }
                }
                if (ChatObject.isChannel(chat)) {
                    if (ChatObject.hasAdminRights(chat)) {
                        this.editItem = menu.addItem(12, (int) R.drawable.menu_settings);
                        if (null == null) {
                            item = menu.addItem(10, (int) R.drawable.ic_ab_other);
                        }
                        if (chat.megagroup) {
                            item.addSubItem(12, LocaleController.getString("ManageGroupMenu", R.string.ManageGroupMenu));
                        } else {
                            item.addSubItem(12, LocaleController.getString("ManageChannelMenu", R.string.ManageChannelMenu));
                        }
                    }
                    if (chat.megagroup) {
                        if (item == null) {
                            item = menu.addItem(10, (int) R.drawable.ic_ab_other);
                        }
                        item.addSubItem(16, LocaleController.getString("SearchMembers", R.string.SearchMembers));
                        if (!(chat.creator || chat.left || chat.kicked)) {
                            item.addSubItem(7, LocaleController.getString("LeaveMegaMenu", R.string.LeaveMegaMenu));
                        }
                    }
                } else {
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
                    item.addSubItem(16, LocaleController.getString("SearchMembers", R.string.SearchMembers));
                    if (chat.creator && (this.info == null || this.info.participants.participants.size() > 0)) {
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
        ProfileActivity profileActivity = this;
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
        if (MessagesController.getInstance(profileActivity.currentAccount).checkCanOpenChat(args, fragment)) {
            NotificationCenter.getInstance(profileActivity.currentAccount).removeObserver(profileActivity, NotificationCenter.closeChats);
            NotificationCenter.getInstance(profileActivity.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
            presentFragment(new ChatActivity(args), true);
            removeSelfFromStack();
            SendMessagesHelper.getInstance(profileActivity.currentAccount).sendMessage(MessagesController.getInstance(profileActivity.currentAccount).getUser(Integer.valueOf(profileActivity.user_id)), did, null, null, null);
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
        r10[4] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_avatar_backgroundActionBarBlue);
        r10[5] = new ThemeDescription(this.topView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_avatar_backgroundActionBarBlue);
        r10[6] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_avatar_actionBarSelectorBlue);
        r10[7] = new ThemeDescription(this.nameTextView[1], ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_profile_title);
        r10[8] = new ThemeDescription(this.onlineTextView[1], ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_avatar_subtitleInProfileBlue);
        r10[9] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_avatar_backgroundActionBarRed);
        r10[10] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_avatar_backgroundActionBarRed);
        r10[11] = new ThemeDescription(this.topView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_avatar_backgroundActionBarRed);
        r10[12] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_avatar_actionBarSelectorRed);
        r10[13] = new ThemeDescription(this.onlineTextView[1], ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_avatar_subtitleInProfileRed);
        r10[14] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_avatar_actionBarIconRed);
        r10[15] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_avatar_backgroundActionBarOrange);
        r10[16] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_avatar_backgroundActionBarOrange);
        r10[17] = new ThemeDescription(this.topView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_avatar_backgroundActionBarOrange);
        r10[18] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_avatar_actionBarSelectorOrange);
        r10[19] = new ThemeDescription(this.onlineTextView[1], ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_avatar_subtitleInProfileOrange);
        r10[20] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_avatar_actionBarIconOrange);
        r10[21] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_avatar_backgroundActionBarViolet);
        r10[22] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_avatar_backgroundActionBarViolet);
        r10[23] = new ThemeDescription(this.topView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_avatar_backgroundActionBarViolet);
        r10[24] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_avatar_actionBarSelectorViolet);
        r10[25] = new ThemeDescription(this.onlineTextView[1], ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_avatar_subtitleInProfileViolet);
        r10[26] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_avatar_actionBarIconViolet);
        r10[27] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_avatar_backgroundActionBarGreen);
        r10[28] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_avatar_backgroundActionBarGreen);
        r10[29] = new ThemeDescription(this.topView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_avatar_backgroundActionBarGreen);
        r10[30] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_avatar_actionBarSelectorGreen);
        r10[31] = new ThemeDescription(this.onlineTextView[1], ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_avatar_subtitleInProfileGreen);
        r10[32] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_avatar_actionBarIconGreen);
        r10[33] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_avatar_backgroundActionBarCyan);
        r10[34] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_avatar_backgroundActionBarCyan);
        r10[35] = new ThemeDescription(this.topView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_avatar_backgroundActionBarCyan);
        r10[36] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_avatar_actionBarSelectorCyan);
        r10[37] = new ThemeDescription(this.onlineTextView[1], ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_avatar_subtitleInProfileCyan);
        r10[38] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_avatar_actionBarIconCyan);
        r10[39] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_avatar_backgroundActionBarPink);
        r10[40] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_avatar_backgroundActionBarPink);
        r10[41] = new ThemeDescription(this.topView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_avatar_backgroundActionBarPink);
        r10[42] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_avatar_actionBarSelectorPink);
        r10[43] = new ThemeDescription(this.onlineTextView[1], ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_avatar_subtitleInProfilePink);
        r10[44] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_avatar_actionBarIconPink);
        r10[45] = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector);
        r10[46] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider);
        View view = this.listView;
        View view2 = view;
        r10[47] = new ThemeDescription(view2, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow);
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
        view = this.listView;
        view2 = view;
        r10[59] = new ThemeDescription(view2, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        view = this.listView;
        view2 = view;
        r10[60] = new ThemeDescription(view2, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteGreenText2);
        view = this.listView;
        view2 = view;
        r10[61] = new ThemeDescription(view2, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteRedText5);
        r10[62] = new ThemeDescription(this.listView, 0, new Class[]{TextCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteValueText);
        r10[63] = new ThemeDescription(this.listView, 0, new Class[]{TextCell.class}, new String[]{"imageView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayIcon);
        r10[64] = new ThemeDescription(this.listView, 0, new Class[]{TextDetailCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r10[65] = new ThemeDescription(this.listView, 0, new Class[]{TextDetailCell.class}, new String[]{"valueImageView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayIcon);
        r10[66] = new ThemeDescription(this.listView, 0, new Class[]{TextDetailCell.class}, new String[]{"imageView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayIcon);
        view = this.listView;
        view2 = view;
        r10[67] = new ThemeDescription(view2, ThemeDescription.FLAG_CHECKTAG, new Class[]{UserCell.class}, new String[]{"adminImage"}, null, null, null, Theme.key_profile_creatorIcon);
        view = this.listView;
        view2 = view;
        r10[68] = new ThemeDescription(view2, ThemeDescription.FLAG_CHECKTAG, new Class[]{UserCell.class}, new String[]{"adminImage"}, null, null, null, Theme.key_profile_adminIcon);
        r10[69] = new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, new String[]{"nameTextView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r10[70] = new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, new String[]{"statusColor"}, null, null, сellDelegate, Theme.key_windowBackgroundWhiteGrayText);
        r10[71] = new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, new String[]{"statusOnlineColor"}, null, null, сellDelegate, Theme.key_windowBackgroundWhiteBlueText);
        r10[72] = new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, null, new Drawable[]{Theme.avatar_photoDrawable, Theme.avatar_broadcastDrawable, Theme.avatar_savedDrawable}, null, Theme.key_avatar_text);
        ThemeDescriptionDelegate themeDescriptionDelegate = сellDelegate;
        r10[73] = new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundRed);
        r10[74] = new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundOrange);
        r10[75] = new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundViolet);
        r10[76] = new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundGreen);
        r10[77] = new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundCyan);
        r10[78] = new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundBlue);
        r10[79] = new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundPink);
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
