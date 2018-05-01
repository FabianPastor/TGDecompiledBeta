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
import org.telegram.messenger.C0446R;
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
    private PhotoViewerProvider provider = new C23481();
    private boolean recreateMenuAfterAnimation;
    private int rowCount = null;
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

        protected void onMeasure(int i, int i2) {
            setMeasuredDimension(MeasureSpec.getSize(i), (ActionBar.getCurrentActionBarHeight() + (ProfileActivity.this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0)) + AndroidUtilities.dp(91.0f));
        }

        public void setBackgroundColor(int i) {
            if (i != this.currentColor) {
                this.paint.setColor(i);
                invalidate();
            }
        }

        protected void onDraw(Canvas canvas) {
            int measuredHeight = getMeasuredHeight() - AndroidUtilities.dp(91.0f);
            canvas.drawRect(0.0f, 0.0f, (float) getMeasuredWidth(), (float) (ProfileActivity.this.extraHeight + measuredHeight), this.paint);
            if (ProfileActivity.this.parentLayout != null) {
                ProfileActivity.this.parentLayout.drawHeaderShadow(canvas, measuredHeight + ProfileActivity.this.extraHeight);
            }
        }
    }

    /* renamed from: org.telegram.ui.ProfileActivity$3 */
    class C22573 implements AvatarUpdaterDelegate {
        C22573() {
        }

        public void didUploadedPhoto(InputFile inputFile, PhotoSize photoSize, PhotoSize photoSize2) {
            if (ProfileActivity.this.chat_id != null) {
                MessagesController.getInstance(ProfileActivity.this.currentAccount).changeChatAvatar(ProfileActivity.this.chat_id, inputFile);
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
                if (ProfileActivity.this.userBlocked == null) {
                    MessagesController.getInstance(ProfileActivity.this.currentAccount).blockUser(ProfileActivity.this.user_id);
                } else {
                    MessagesController.getInstance(ProfileActivity.this.currentAccount).unblockUser(ProfileActivity.this.user_id);
                }
            }
        }

        C22605() {
        }

        public void onItemClick(int i) {
            if (ProfileActivity.this.getParentActivity() != null) {
                if (i == -1) {
                    ProfileActivity.this.finishFragment();
                } else if (i == 2) {
                    if (MessagesController.getInstance(ProfileActivity.this.currentAccount).getUser(Integer.valueOf(ProfileActivity.this.user_id)) != 0) {
                        if (ProfileActivity.this.isBot == 0) {
                            i = new Builder(ProfileActivity.this.getParentActivity());
                            if (ProfileActivity.this.userBlocked) {
                                i.setMessage(LocaleController.getString("AreYouSureUnblockContact", C0446R.string.AreYouSureUnblockContact));
                            } else {
                                i.setMessage(LocaleController.getString("AreYouSureBlockContact", C0446R.string.AreYouSureBlockContact));
                            }
                            i.setTitle(LocaleController.getString("AppName", C0446R.string.AppName));
                            i.setPositiveButton(LocaleController.getString("OK", C0446R.string.OK), new C16441());
                            i.setNegativeButton(LocaleController.getString("Cancel", C0446R.string.Cancel), null);
                            ProfileActivity.this.showDialog(i.create());
                        } else if (ProfileActivity.this.userBlocked == 0) {
                            MessagesController.getInstance(ProfileActivity.this.currentAccount).blockUser(ProfileActivity.this.user_id);
                        } else {
                            MessagesController.getInstance(ProfileActivity.this.currentAccount).unblockUser(ProfileActivity.this.user_id);
                            SendMessagesHelper.getInstance(ProfileActivity.this.currentAccount).sendMessage("/start", (long) ProfileActivity.this.user_id, null, null, false, null, null, null);
                            ProfileActivity.this.finishFragment();
                        }
                    }
                } else if (i == 1) {
                    i = MessagesController.getInstance(ProfileActivity.this.currentAccount).getUser(Integer.valueOf(ProfileActivity.this.user_id));
                    r0 = new Bundle();
                    r0.putInt("user_id", i.id);
                    r0.putBoolean("addContact", true);
                    ProfileActivity.this.presentFragment(new ContactAddActivity(r0));
                } else if (i == 3) {
                    i = new Bundle();
                    i.putBoolean("onlySelect", true);
                    i.putString("selectAlertString", LocaleController.getString("SendContactTo", C0446R.string.SendContactTo));
                    i.putString("selectAlertStringGroup", LocaleController.getString("SendContactToGroup", C0446R.string.SendContactToGroup));
                    r0 = new DialogsActivity(i);
                    r0.setDelegate(ProfileActivity.this);
                    ProfileActivity.this.presentFragment(r0);
                } else if (i == 4) {
                    i = new Bundle();
                    i.putInt("user_id", ProfileActivity.this.user_id);
                    ProfileActivity.this.presentFragment(new ContactAddActivity(i));
                } else if (i == 5) {
                    i = MessagesController.getInstance(ProfileActivity.this.currentAccount).getUser(Integer.valueOf(ProfileActivity.this.user_id));
                    if (i != 0) {
                        if (ProfileActivity.this.getParentActivity() != null) {
                            Builder builder = new Builder(ProfileActivity.this.getParentActivity());
                            builder.setMessage(LocaleController.getString("AreYouSureDeleteContact", C0446R.string.AreYouSureDeleteContact));
                            builder.setTitle(LocaleController.getString("AppName", C0446R.string.AppName));
                            builder.setPositiveButton(LocaleController.getString("OK", C0446R.string.OK), new OnClickListener() {
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface = new ArrayList();
                                    dialogInterface.add(i);
                                    ContactsController.getInstance(ProfileActivity.this.currentAccount).deleteContact(dialogInterface);
                                }
                            });
                            builder.setNegativeButton(LocaleController.getString("Cancel", C0446R.string.Cancel), null);
                            ProfileActivity.this.showDialog(builder.create());
                        }
                    }
                } else if (i == 7) {
                    ProfileActivity.this.leaveChatPressed();
                } else if (i == 8) {
                    i = new Bundle();
                    i.putInt("chat_id", ProfileActivity.this.chat_id);
                    ProfileActivity.this.presentFragment(new ChangeChatNameActivity(i));
                } else if (i == 12) {
                    i = new Bundle();
                    i.putInt("chat_id", ProfileActivity.this.chat_id);
                    r0 = new ChannelEditActivity(i);
                    r0.setInfo(ProfileActivity.this.info);
                    ProfileActivity.this.presentFragment(r0);
                } else if (i == 9) {
                    i = MessagesController.getInstance(ProfileActivity.this.currentAccount).getUser(Integer.valueOf(ProfileActivity.this.user_id));
                    if (i != 0) {
                        r0 = new Bundle();
                        r0.putBoolean("onlySelect", true);
                        r0.putInt("dialogsType", 2);
                        r0.putString("addToGroupAlertString", LocaleController.formatString("AddToTheGroupTitle", C0446R.string.AddToTheGroupTitle, UserObject.getUserName(i), "%1$s"));
                        BaseFragment dialogsActivity = new DialogsActivity(r0);
                        dialogsActivity.setDelegate(new DialogsActivityDelegate() {
                            public void didSelectDialogs(DialogsActivity dialogsActivity, ArrayList<Long> arrayList, CharSequence charSequence, boolean z) {
                                long longValue = ((Long) arrayList.get(0)).longValue();
                                arrayList = new Bundle();
                                arrayList.putBoolean("scrollToTopOnResume", true);
                                int i = -((int) longValue);
                                arrayList.putInt("chat_id", i);
                                if (MessagesController.getInstance(ProfileActivity.this.currentAccount).checkCanOpenChat(arrayList, dialogsActivity) != null) {
                                    NotificationCenter.getInstance(ProfileActivity.this.currentAccount).removeObserver(ProfileActivity.this, NotificationCenter.closeChats);
                                    NotificationCenter.getInstance(ProfileActivity.this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                                    MessagesController.getInstance(ProfileActivity.this.currentAccount).addUserToChat(i, i, null, 0, null, ProfileActivity.this);
                                    ProfileActivity.this.presentFragment(new ChatActivity(arrayList), true);
                                    ProfileActivity.this.removeSelfFromStack();
                                }
                            }
                        });
                        ProfileActivity.this.presentFragment(dialogsActivity);
                    }
                } else if (i == 10) {
                    try {
                        if (MessagesController.getInstance(ProfileActivity.this.currentAccount).getUser(Integer.valueOf(ProfileActivity.this.user_id)) != 0) {
                            Intent intent = new Intent("android.intent.action.SEND");
                            intent.setType("text/plain");
                            TL_userFull userFull = MessagesController.getInstance(ProfileActivity.this.currentAccount).getUserFull(ProfileActivity.this.botInfo.user_id);
                            if (ProfileActivity.this.botInfo == null || userFull == null || TextUtils.isEmpty(userFull.about)) {
                                StringBuilder stringBuilder = new StringBuilder();
                                stringBuilder.append("https://");
                                stringBuilder.append(MessagesController.getInstance(ProfileActivity.this.currentAccount).linkPrefix);
                                stringBuilder.append("/%s");
                                intent.putExtra("android.intent.extra.TEXT", String.format(stringBuilder.toString(), new Object[]{i.username}));
                            } else {
                                StringBuilder stringBuilder2 = new StringBuilder();
                                stringBuilder2.append("%s https://");
                                stringBuilder2.append(MessagesController.getInstance(ProfileActivity.this.currentAccount).linkPrefix);
                                stringBuilder2.append("/%s");
                                intent.putExtra("android.intent.extra.TEXT", String.format(stringBuilder2.toString(), new Object[]{userFull.about, i.username}));
                            }
                            ProfileActivity.this.startActivityForResult(Intent.createChooser(intent, LocaleController.getString("BotShare", C0446R.string.BotShare)), 500);
                        }
                    } catch (Throwable e) {
                        FileLog.m3e(e);
                    }
                } else if (i == 11) {
                    i = new Bundle();
                    i.putInt("chat_id", ProfileActivity.this.chat_id);
                    r0 = new SetAdminsActivity(i);
                    r0.setChatInfo(ProfileActivity.this.info);
                    ProfileActivity.this.presentFragment(r0);
                } else if (i == 13) {
                    i = new Bundle();
                    i.putInt("chat_id", ProfileActivity.this.chat_id);
                    ProfileActivity.this.presentFragment(new ConvertGroupActivity(i));
                } else if (i == 14) {
                    try {
                        long j;
                        if (ProfileActivity.this.currentEncryptedChat != 0) {
                            j = ((long) ProfileActivity.this.currentEncryptedChat.id) << 32;
                        } else if (ProfileActivity.this.user_id != 0) {
                            j = (long) ProfileActivity.this.user_id;
                        } else if (ProfileActivity.this.chat_id != 0) {
                            j = (long) (-ProfileActivity.this.chat_id);
                        } else {
                            return;
                        }
                        DataQuery.getInstance(ProfileActivity.this.currentAccount).installShortcut(j);
                    } catch (Throwable e2) {
                        FileLog.m3e(e2);
                    }
                } else if (i == 15) {
                    i = MessagesController.getInstance(ProfileActivity.this.currentAccount).getUser(Integer.valueOf(ProfileActivity.this.user_id));
                    if (i != 0) {
                        VoIPHelper.startCall(i, ProfileActivity.this.getParentActivity(), MessagesController.getInstance(ProfileActivity.this.currentAccount).getUserFull(i.id));
                    }
                } else if (i == 16) {
                    i = new Bundle();
                    i.putInt("chat_id", ProfileActivity.this.chat_id);
                    if (ChatObject.isChannel(ProfileActivity.this.currentChat)) {
                        i.putInt("type", 2);
                        i.putBoolean("open_search", true);
                        ProfileActivity.this.presentFragment(new ChannelUsersActivity(i));
                    } else {
                        r0 = new ChatUsersActivity(i);
                        r0.setInfo(ProfileActivity.this.info);
                        ProfileActivity.this.presentFragment(r0);
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
                ProfileActivity.this.creatingChat = 1;
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

        public void onItemClick(View view, int i) {
            if (ProfileActivity.this.getParentActivity() != null) {
                if (i == ProfileActivity.this.sharedMediaRow) {
                    view = new Bundle();
                    if (ProfileActivity.this.user_id != 0) {
                        view.putLong("dialog_id", ProfileActivity.this.dialog_id != 0 ? ProfileActivity.this.dialog_id : (long) ProfileActivity.this.user_id);
                    } else {
                        view.putLong("dialog_id", (long) (-ProfileActivity.this.chat_id));
                    }
                    i = new MediaActivity(view);
                    i.setChatInfo(ProfileActivity.this.info);
                    ProfileActivity.this.presentFragment(i);
                } else if (i == ProfileActivity.this.groupsInCommonRow) {
                    ProfileActivity.this.presentFragment(new CommonGroupsActivity(ProfileActivity.this.user_id));
                } else if (i == ProfileActivity.this.settingsKeyRow) {
                    view = new Bundle();
                    view.putInt("chat_id", (int) (ProfileActivity.this.dialog_id >> 32));
                    ProfileActivity.this.presentFragment(new IdenticonActivity(view));
                } else if (i == ProfileActivity.this.settingsTimerRow) {
                    ProfileActivity.this.showDialog(AlertsCreator.createTTLAlert(ProfileActivity.this.getParentActivity(), ProfileActivity.this.currentEncryptedChat).create());
                } else if (i == ProfileActivity.this.settingsNotificationsRow) {
                    if (ProfileActivity.this.dialog_id != null) {
                        view = ProfileActivity.this.dialog_id;
                    } else if (ProfileActivity.this.user_id != null) {
                        view = (long) ProfileActivity.this.user_id;
                    } else {
                        view = (long) (-ProfileActivity.this.chat_id);
                    }
                    String[] strArr = new String[5];
                    strArr[0] = LocaleController.getString("NotificationsTurnOn", C0446R.string.NotificationsTurnOn);
                    strArr[1] = LocaleController.formatString("MuteFor", C0446R.string.MuteFor, LocaleController.formatPluralString("Hours", 1));
                    strArr[2] = LocaleController.formatString("MuteFor", C0446R.string.MuteFor, LocaleController.formatPluralString("Days", 2));
                    strArr[3] = LocaleController.getString("NotificationsCustomize", C0446R.string.NotificationsCustomize);
                    strArr[4] = LocaleController.getString("NotificationsTurnOff", C0446R.string.NotificationsTurnOff);
                    int[] iArr = new int[]{C0446R.drawable.notifications_s_on, C0446R.drawable.notifications_s_1h, C0446R.drawable.notifications_s_2d, C0446R.drawable.notifications_s_custom, C0446R.drawable.notifications_s_off};
                    View linearLayout = new LinearLayout(ProfileActivity.this.getParentActivity());
                    linearLayout.setOrientation(1);
                    for (int i2 = 0; i2 < strArr.length; i2++) {
                        View textView = new TextView(ProfileActivity.this.getParentActivity());
                        textView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
                        textView.setTextSize(1, 16.0f);
                        textView.setLines(1);
                        textView.setMaxLines(1);
                        Drawable drawable = ProfileActivity.this.getParentActivity().getResources().getDrawable(iArr[i2]);
                        drawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_dialogIcon), Mode.MULTIPLY));
                        textView.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
                        textView.setTag(Integer.valueOf(i2));
                        textView.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                        textView.setPadding(AndroidUtilities.dp(24.0f), 0, AndroidUtilities.dp(24.0f), 0);
                        textView.setSingleLine(true);
                        textView.setGravity(19);
                        textView.setCompoundDrawablePadding(AndroidUtilities.dp(26.0f));
                        textView.setText(strArr[i2]);
                        linearLayout.addView(textView, LayoutHelper.createLinear(-1, 48, 51));
                        textView.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View view) {
                                view = ((Integer) view.getTag()).intValue();
                                TL_dialog tL_dialog;
                                if (view == null) {
                                    view = MessagesController.getNotificationsSettings(ProfileActivity.this.currentAccount).edit();
                                    StringBuilder stringBuilder = new StringBuilder();
                                    stringBuilder.append("notify2_");
                                    stringBuilder.append(view);
                                    view.putInt(stringBuilder.toString(), 0);
                                    MessagesStorage.getInstance(ProfileActivity.this.currentAccount).setDialogFlags(view, 0);
                                    view.commit();
                                    tL_dialog = (TL_dialog) MessagesController.getInstance(ProfileActivity.this.currentAccount).dialogs_dict.get(view);
                                    if (tL_dialog != null) {
                                        tL_dialog.notify_settings = new TL_peerNotifySettings();
                                    }
                                    NotificationsController.getInstance(ProfileActivity.this.currentAccount).updateServerNotificationsSettings(view);
                                } else if (view == 3) {
                                    view = new Bundle();
                                    view.putLong("dialog_id", view);
                                    ProfileActivity.this.presentFragment(new ProfileNotificationsActivity(view));
                                } else {
                                    int currentTime = ConnectionsManager.getInstance(ProfileActivity.this.currentAccount).getCurrentTime();
                                    if (view == 1) {
                                        currentTime += 3600;
                                    } else if (view == 2) {
                                        currentTime += 172800;
                                    } else if (view == 4) {
                                        currentTime = ConnectionsManager.DEFAULT_DATACENTER_ID;
                                    }
                                    Editor edit = MessagesController.getNotificationsSettings(ProfileActivity.this.currentAccount).edit();
                                    long j = 1;
                                    if (view == 4) {
                                        view = new StringBuilder();
                                        view.append("notify2_");
                                        view.append(view);
                                        edit.putInt(view.toString(), 2);
                                    } else {
                                        view = new StringBuilder();
                                        view.append("notify2_");
                                        view.append(view);
                                        edit.putInt(view.toString(), 3);
                                        view = new StringBuilder();
                                        view.append("notifyuntil_");
                                        view.append(view);
                                        edit.putInt(view.toString(), currentTime);
                                        j = (((long) currentTime) << 32) | 1;
                                    }
                                    NotificationsController.getInstance(ProfileActivity.this.currentAccount).removeNotificationsForDialog(view);
                                    MessagesStorage.getInstance(ProfileActivity.this.currentAccount).setDialogFlags(view, j);
                                    edit.commit();
                                    tL_dialog = (TL_dialog) MessagesController.getInstance(ProfileActivity.this.currentAccount).dialogs_dict.get(view);
                                    if (tL_dialog != null) {
                                        tL_dialog.notify_settings = new TL_peerNotifySettings();
                                        tL_dialog.notify_settings.mute_until = currentTime;
                                    }
                                    NotificationsController.getInstance(ProfileActivity.this.currentAccount).updateServerNotificationsSettings(view);
                                }
                                ProfileActivity.this.listAdapter.notifyItemChanged(ProfileActivity.this.settingsNotificationsRow);
                                ProfileActivity.this.dismissCurrentDialig();
                            }
                        });
                    }
                    view = new Builder(ProfileActivity.this.getParentActivity());
                    view.setTitle(LocaleController.getString("Notifications", C0446R.string.Notifications));
                    view.setView(linearLayout);
                    ProfileActivity.this.showDialog(view.create());
                } else if (i == ProfileActivity.this.startSecretChatRow) {
                    view = new Builder(ProfileActivity.this.getParentActivity());
                    view.setMessage(LocaleController.getString("AreYouSureSecretChat", C0446R.string.AreYouSureSecretChat));
                    view.setTitle(LocaleController.getString("AppName", C0446R.string.AppName));
                    view.setPositiveButton(LocaleController.getString("OK", C0446R.string.OK), new C16482());
                    view.setNegativeButton(LocaleController.getString("Cancel", C0446R.string.Cancel), null);
                    ProfileActivity.this.showDialog(view.create());
                } else if (i > ProfileActivity.this.emptyRowChat2 && i < ProfileActivity.this.membersEndRow) {
                    if (ProfileActivity.this.sortedUsers.isEmpty() == null) {
                        view = ((ChatParticipant) ProfileActivity.this.info.participants.participants.get(((Integer) ProfileActivity.this.sortedUsers.get((i - ProfileActivity.this.emptyRowChat2) - 1)).intValue())).user_id;
                    } else {
                        view = ((ChatParticipant) ProfileActivity.this.info.participants.participants.get((i - ProfileActivity.this.emptyRowChat2) - 1)).user_id;
                    }
                    if (view != UserConfig.getInstance(ProfileActivity.this.currentAccount).getClientUserId()) {
                        i = new Bundle();
                        i.putInt("user_id", view);
                        ProfileActivity.this.presentFragment(new ProfileActivity(i));
                    }
                } else if (i == ProfileActivity.this.addMemberRow) {
                    ProfileActivity.this.openAddMember();
                } else if (i == ProfileActivity.this.channelNameRow) {
                    try {
                        view = new Intent("android.intent.action.SEND");
                        view.setType("text/plain");
                        StringBuilder stringBuilder;
                        if (ProfileActivity.this.info.about == 0 || ProfileActivity.this.info.about.length() <= 0) {
                            stringBuilder = new StringBuilder();
                            stringBuilder.append(ProfileActivity.this.currentChat.title);
                            stringBuilder.append("\nhttps://");
                            stringBuilder.append(MessagesController.getInstance(ProfileActivity.this.currentAccount).linkPrefix);
                            stringBuilder.append("/");
                            stringBuilder.append(ProfileActivity.this.currentChat.username);
                            view.putExtra("android.intent.extra.TEXT", stringBuilder.toString());
                        } else {
                            stringBuilder = new StringBuilder();
                            stringBuilder.append(ProfileActivity.this.currentChat.title);
                            stringBuilder.append("\n");
                            stringBuilder.append(ProfileActivity.this.info.about);
                            stringBuilder.append("\nhttps://");
                            stringBuilder.append(MessagesController.getInstance(ProfileActivity.this.currentAccount).linkPrefix);
                            stringBuilder.append("/");
                            stringBuilder.append(ProfileActivity.this.currentChat.username);
                            view.putExtra("android.intent.extra.TEXT", stringBuilder.toString());
                        }
                        ProfileActivity.this.getParentActivity().startActivityForResult(Intent.createChooser(view, LocaleController.getString("BotShare", C0446R.string.BotShare)), 500);
                    } catch (Throwable e) {
                        FileLog.m3e(e);
                    }
                } else if (i == ProfileActivity.this.leaveChannelRow) {
                    ProfileActivity.this.leaveChatPressed();
                } else if (i == ProfileActivity.this.membersRow) {
                    view = new Bundle();
                    view.putInt("chat_id", ProfileActivity.this.chat_id);
                    view.putInt("type", 2);
                    ProfileActivity.this.presentFragment(new ChannelUsersActivity(view));
                } else if (i == ProfileActivity.this.convertRow) {
                    view = new Builder(ProfileActivity.this.getParentActivity());
                    view.setMessage(LocaleController.getString("ConvertGroupAlert", C0446R.string.ConvertGroupAlert));
                    view.setTitle(LocaleController.getString("ConvertGroupAlertWarning", C0446R.string.ConvertGroupAlertWarning));
                    view.setPositiveButton(LocaleController.getString("OK", C0446R.string.OK), new C16493());
                    view.setNegativeButton(LocaleController.getString("Cancel", C0446R.string.Cancel), null);
                    ProfileActivity.this.showDialog(view.create());
                } else {
                    ProfileActivity.this.processOnClickOrPress(i);
                }
            }
        }
    }

    /* renamed from: org.telegram.ui.ProfileActivity$1 */
    class C23481 extends EmptyPhotoViewerProvider {
        C23481() {
        }

        public PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, FileLocation fileLocation, int i) {
            if (fileLocation == null) {
                return null;
            }
            if (ProfileActivity.this.user_id != 0) {
                i = MessagesController.getInstance(ProfileActivity.this.currentAccount).getUser(Integer.valueOf(ProfileActivity.this.user_id));
                if (!(i == 0 || i.photo == null || i.photo.photo_big == null)) {
                    i = i.photo.photo_big;
                    if (i != 0 || i.local_id != fileLocation.local_id || i.volume_id != fileLocation.volume_id || i.dc_id != fileLocation.dc_id) {
                        return null;
                    }
                    messageObject = new int[2];
                    ProfileActivity.this.avatarImage.getLocationInWindow(messageObject);
                    fileLocation = new PlaceProviderObject();
                    i = 0;
                    fileLocation.viewX = messageObject[0];
                    messageObject = messageObject[1];
                    if (VERSION.SDK_INT < 21) {
                        i = AndroidUtilities.statusBarHeight;
                    }
                    fileLocation.viewY = messageObject - i;
                    fileLocation.parentView = ProfileActivity.this.avatarImage;
                    fileLocation.imageReceiver = ProfileActivity.this.avatarImage.getImageReceiver();
                    if (ProfileActivity.this.user_id != null) {
                        fileLocation.dialogId = ProfileActivity.this.user_id;
                    } else if (ProfileActivity.this.chat_id != null) {
                        fileLocation.dialogId = -ProfileActivity.this.chat_id;
                    }
                    fileLocation.thumb = fileLocation.imageReceiver.getBitmapSafe();
                    fileLocation.size = -1;
                    fileLocation.radius = ProfileActivity.this.avatarImage.getImageReceiver().getRoundRadius();
                    fileLocation.scale = ProfileActivity.this.avatarImage.getScaleX();
                    return fileLocation;
                }
            } else if (ProfileActivity.this.chat_id != 0) {
                i = MessagesController.getInstance(ProfileActivity.this.currentAccount).getChat(Integer.valueOf(ProfileActivity.this.chat_id));
                if (!(i == 0 || i.photo == null || i.photo.photo_big == null)) {
                    i = i.photo.photo_big;
                    if (i != 0) {
                    }
                    return null;
                }
            }
            i = 0;
            if (i != 0) {
            }
            return null;
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

            public void didPressUrl(String str) {
                if (str.startsWith("@")) {
                    MessagesController.getInstance(ProfileActivity.this.currentAccount).openByUserName(str.substring(1), ProfileActivity.this, 0);
                } else if (str.startsWith("#")) {
                    r0 = new DialogsActivity(null);
                    r0.setSearchString(str);
                    ProfileActivity.this.presentFragment(r0);
                } else if (str.startsWith("/") && ProfileActivity.this.parentLayout.fragmentsStack.size() > 1) {
                    r0 = (BaseFragment) ProfileActivity.this.parentLayout.fragmentsStack.get(ProfileActivity.this.parentLayout.fragmentsStack.size() - 2);
                    if (r0 instanceof ChatActivity) {
                        ProfileActivity.this.finishFragment();
                        ((ChatActivity) r0).chatActivityEnterView.setCommand(null, str, false, false);
                    }
                }
            }
        }

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            switch (i) {
                case 0:
                    viewGroup = new EmptyCell(this.mContext);
                    break;
                case 1:
                    viewGroup = new DividerCell(this.mContext);
                    viewGroup.setPadding(AndroidUtilities.dp(NUM), 0, 0, 0);
                    break;
                case 2:
                    viewGroup = new TextDetailCell(this.mContext);
                    break;
                case 3:
                    viewGroup = new TextCell(this.mContext);
                    break;
                case 4:
                    viewGroup = new UserCell(this.mContext, 61, 0, true);
                    break;
                case 5:
                    i = new ShadowSectionCell(this.mContext);
                    Drawable combinedDrawable = new CombinedDrawable(new ColorDrawable(Theme.getColor(Theme.key_windowBackgroundGray)), Theme.getThemedDrawable(this.mContext, C0446R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                    combinedDrawable.setFullsize(true);
                    i.setBackgroundDrawable(combinedDrawable);
                    break;
                case 6:
                    i = new TextInfoPrivacyCell(this.mContext);
                    TextInfoPrivacyCell textInfoPrivacyCell = (TextInfoPrivacyCell) i;
                    Drawable combinedDrawable2 = new CombinedDrawable(new ColorDrawable(Theme.getColor(Theme.key_windowBackgroundGray)), Theme.getThemedDrawable(this.mContext, C0446R.drawable.greydivider, Theme.key_windowBackgroundGrayShadow));
                    combinedDrawable2.setFullsize(true);
                    textInfoPrivacyCell.setBackgroundDrawable(combinedDrawable2);
                    textInfoPrivacyCell.setText(AndroidUtilities.replaceTags(LocaleController.formatString("ConvertGroupInfo", C0446R.string.ConvertGroupInfo, LocaleController.formatPluralString("Members", MessagesController.getInstance(ProfileActivity.this.currentAccount).maxMegagroupCount))));
                    break;
                case 7:
                    viewGroup = new LoadingCell(this.mContext);
                    break;
                case 8:
                    viewGroup = new AboutLinkCell(this.mContext);
                    ((AboutLinkCell) viewGroup).setDelegate(new C22621());
                    break;
                default:
                    viewGroup = null;
                    break;
            }
            viewGroup = i;
            viewGroup.setLayoutParams(new LayoutParams(-1, -2));
            return new Holder(viewGroup);
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            ListAdapter listAdapter = this;
            ViewHolder viewHolder2 = viewHolder;
            int i2 = i;
            int itemViewType = viewHolder.getItemViewType();
            if (itemViewType != 0) {
                boolean z = true;
                String string;
                TL_userFull userFull;
                if (itemViewType != 8) {
                    int i3 = 0;
                    StringBuilder stringBuilder;
                    String str;
                    switch (itemViewType) {
                        case 2:
                            TextDetailCell textDetailCell = (TextDetailCell) viewHolder2.itemView;
                            textDetailCell.setMultiline(false);
                            User user;
                            if (i2 == ProfileActivity.this.phoneRow) {
                                user = MessagesController.getInstance(ProfileActivity.this.currentAccount).getUser(Integer.valueOf(ProfileActivity.this.user_id));
                                if (user.phone == null || user.phone.length() == 0) {
                                    string = LocaleController.getString("NumberUnknown", C0446R.string.NumberUnknown);
                                } else {
                                    PhoneFormat instance = PhoneFormat.getInstance();
                                    StringBuilder stringBuilder2 = new StringBuilder();
                                    stringBuilder2.append("+");
                                    stringBuilder2.append(user.phone);
                                    string = instance.format(stringBuilder2.toString());
                                }
                                textDetailCell.setTextAndValueAndIcon(string, LocaleController.getString("PhoneMobile", C0446R.string.PhoneMobile), C0446R.drawable.profile_phone, 0);
                                return;
                            } else if (i2 == ProfileActivity.this.usernameRow) {
                                user = MessagesController.getInstance(ProfileActivity.this.currentAccount).getUser(Integer.valueOf(ProfileActivity.this.user_id));
                                if (user == null || TextUtils.isEmpty(user.username)) {
                                    string = "-";
                                } else {
                                    stringBuilder = new StringBuilder();
                                    stringBuilder.append("@");
                                    stringBuilder.append(user.username);
                                    string = stringBuilder.toString();
                                }
                                if (ProfileActivity.this.phoneRow == -1 && ProfileActivity.this.userInfoRow == -1 && ProfileActivity.this.userInfoDetailedRow == -1) {
                                    textDetailCell.setTextAndValueAndIcon(string, LocaleController.getString("Username", C0446R.string.Username), C0446R.drawable.profile_info, 11);
                                    return;
                                } else {
                                    textDetailCell.setTextAndValue(string, LocaleController.getString("Username", C0446R.string.Username));
                                    return;
                                }
                            } else if (i2 == ProfileActivity.this.channelNameRow) {
                                if (ProfileActivity.this.currentChat == null || TextUtils.isEmpty(ProfileActivity.this.currentChat.username)) {
                                    string = "-";
                                } else {
                                    StringBuilder stringBuilder3 = new StringBuilder();
                                    stringBuilder3.append("@");
                                    stringBuilder3.append(ProfileActivity.this.currentChat.username);
                                    string = stringBuilder3.toString();
                                }
                                stringBuilder = new StringBuilder();
                                stringBuilder.append(MessagesController.getInstance(ProfileActivity.this.currentAccount).linkPrefix);
                                stringBuilder.append("/");
                                stringBuilder.append(ProfileActivity.this.currentChat.username);
                                textDetailCell.setTextAndValue(string, stringBuilder.toString());
                                return;
                            } else if (i2 == ProfileActivity.this.userInfoDetailedRow) {
                                userFull = MessagesController.getInstance(ProfileActivity.this.currentAccount).getUserFull(ProfileActivity.this.user_id);
                                textDetailCell.setMultiline(true);
                                if (userFull != null) {
                                    str = userFull.about;
                                } else {
                                    str = null;
                                }
                                textDetailCell.setTextAndValueAndIcon(str, LocaleController.getString("UserBio", C0446R.string.UserBio), C0446R.drawable.profile_info, 11);
                                return;
                            } else {
                                return;
                            }
                        case 3:
                            TextCell textCell = (TextCell) viewHolder2.itemView;
                            textCell.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
                            textCell.setTag(Theme.key_windowBackgroundWhiteBlackText);
                            if (i2 == ProfileActivity.this.sharedMediaRow) {
                                if (ProfileActivity.this.totalMediaCount == -1) {
                                    string = LocaleController.getString("Loading", C0446R.string.Loading);
                                } else {
                                    string = "%d";
                                    Object[] objArr = new Object[1];
                                    objArr[0] = Integer.valueOf(ProfileActivity.this.totalMediaCount + (ProfileActivity.this.totalMediaCountMerge != -1 ? ProfileActivity.this.totalMediaCountMerge : 0));
                                    string = String.format(string, objArr);
                                }
                                if (ProfileActivity.this.user_id == 0 || UserConfig.getInstance(ProfileActivity.this.currentAccount).getClientUserId() != ProfileActivity.this.user_id) {
                                    textCell.setTextAndValue(LocaleController.getString("SharedMedia", C0446R.string.SharedMedia), string);
                                    return;
                                } else {
                                    textCell.setTextAndValueAndIcon(LocaleController.getString("SharedMedia", C0446R.string.SharedMedia), string, C0446R.drawable.profile_list);
                                    return;
                                }
                            } else if (i2 == ProfileActivity.this.groupsInCommonRow) {
                                userFull = MessagesController.getInstance(ProfileActivity.this.currentAccount).getUserFull(ProfileActivity.this.user_id);
                                String string2 = LocaleController.getString("GroupsInCommon", C0446R.string.GroupsInCommon);
                                String str2 = "%d";
                                Object[] objArr2 = new Object[1];
                                objArr2[0] = Integer.valueOf(userFull != null ? userFull.common_chats_count : 0);
                                textCell.setTextAndValue(string2, String.format(str2, objArr2));
                                return;
                            } else if (i2 == ProfileActivity.this.settingsTimerRow) {
                                EncryptedChat encryptedChat = MessagesController.getInstance(ProfileActivity.this.currentAccount).getEncryptedChat(Integer.valueOf((int) (ProfileActivity.this.dialog_id >> 32)));
                                if (encryptedChat.ttl == 0) {
                                    string = LocaleController.getString("ShortMessageLifetimeForever", C0446R.string.ShortMessageLifetimeForever);
                                } else {
                                    string = LocaleController.formatTTLString(encryptedChat.ttl);
                                }
                                textCell.setTextAndValue(LocaleController.getString("MessageLifetime", C0446R.string.MessageLifetime), string);
                                return;
                            } else if (i2 == ProfileActivity.this.settingsNotificationsRow) {
                                long access$4100;
                                SharedPreferences notificationsSettings = MessagesController.getNotificationsSettings(ProfileActivity.this.currentAccount);
                                if (ProfileActivity.this.dialog_id != 0) {
                                    access$4100 = ProfileActivity.this.dialog_id;
                                } else if (ProfileActivity.this.user_id != 0) {
                                    access$4100 = (long) ProfileActivity.this.user_id;
                                } else {
                                    access$4100 = (long) (-ProfileActivity.this.chat_id);
                                }
                                stringBuilder = new StringBuilder();
                                stringBuilder.append("custom_");
                                stringBuilder.append(access$4100);
                                boolean z2 = notificationsSettings.getBoolean(stringBuilder.toString(), false);
                                StringBuilder stringBuilder4 = new StringBuilder();
                                stringBuilder4.append("notify2_");
                                stringBuilder4.append(access$4100);
                                boolean contains = notificationsSettings.contains(stringBuilder4.toString());
                                StringBuilder stringBuilder5 = new StringBuilder();
                                stringBuilder5.append("notify2_");
                                stringBuilder5.append(access$4100);
                                int i4 = notificationsSettings.getInt(stringBuilder5.toString(), 0);
                                StringBuilder stringBuilder6 = new StringBuilder();
                                stringBuilder6.append("notifyuntil_");
                                stringBuilder6.append(access$4100);
                                int i5 = notificationsSettings.getInt(stringBuilder6.toString(), 0);
                                if (i4 != 3 || i5 == ConnectionsManager.DEFAULT_DATACENTER_ID) {
                                    if (i4 == 0) {
                                        if (!contains) {
                                            z = ((int) access$4100) < 0 ? notificationsSettings.getBoolean("EnableGroup", true) : notificationsSettings.getBoolean("EnableAll", true);
                                        }
                                    } else if (i4 != 1) {
                                        z = false;
                                    }
                                    if (z && z2) {
                                        str = LocaleController.getString("NotificationsCustom", C0446R.string.NotificationsCustom);
                                    } else {
                                        str = z ? LocaleController.getString("NotificationsOn", C0446R.string.NotificationsOn) : LocaleController.getString("NotificationsOff", C0446R.string.NotificationsOff);
                                    }
                                } else {
                                    i5 -= ConnectionsManager.getInstance(ProfileActivity.this.currentAccount).getCurrentTime();
                                    str = i5 <= 0 ? z2 ? LocaleController.getString("NotificationsCustom", C0446R.string.NotificationsCustom) : LocaleController.getString("NotificationsOn", C0446R.string.NotificationsOn) : i5 < 3600 ? LocaleController.formatString("WillUnmuteIn", C0446R.string.WillUnmuteIn, LocaleController.formatPluralString("Minutes", i5 / 60)) : i5 < 86400 ? LocaleController.formatString("WillUnmuteIn", C0446R.string.WillUnmuteIn, LocaleController.formatPluralString("Hours", (int) Math.ceil((double) ((((float) i5) / 60.0f) / 60.0f)))) : i5 < 31536000 ? LocaleController.formatString("WillUnmuteIn", C0446R.string.WillUnmuteIn, LocaleController.formatPluralString("Days", (int) Math.ceil((double) (((((float) i5) / 60.0f) / 60.0f) / 24.0f)))) : null;
                                }
                                if (str != null) {
                                    textCell.setTextAndValueAndIcon(LocaleController.getString("Notifications", C0446R.string.Notifications), str, C0446R.drawable.profile_list);
                                    return;
                                } else {
                                    textCell.setTextAndValueAndIcon(LocaleController.getString("Notifications", C0446R.string.Notifications), LocaleController.getString("NotificationsOff", C0446R.string.NotificationsOff), C0446R.drawable.profile_list);
                                    return;
                                }
                            } else if (i2 == ProfileActivity.this.startSecretChatRow) {
                                textCell.setText(LocaleController.getString("StartEncryptedChat", C0446R.string.StartEncryptedChat));
                                textCell.setTag(Theme.key_windowBackgroundWhiteGreenText2);
                                textCell.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGreenText2));
                                return;
                            } else if (i2 == ProfileActivity.this.settingsKeyRow) {
                                Drawable identiconDrawable = new IdenticonDrawable();
                                identiconDrawable.setEncryptedChat(MessagesController.getInstance(ProfileActivity.this.currentAccount).getEncryptedChat(Integer.valueOf((int) (ProfileActivity.this.dialog_id >> 32))));
                                textCell.setTextAndValueDrawable(LocaleController.getString("EncryptionKey", C0446R.string.EncryptionKey), identiconDrawable);
                                return;
                            } else if (i2 == ProfileActivity.this.leaveChannelRow) {
                                textCell.setTag(Theme.key_windowBackgroundWhiteRedText5);
                                textCell.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText5));
                                textCell.setText(LocaleController.getString("LeaveChannel", C0446R.string.LeaveChannel));
                                return;
                            } else if (i2 == ProfileActivity.this.convertRow) {
                                textCell.setText(LocaleController.getString("UpgradeGroup", C0446R.string.UpgradeGroup));
                                textCell.setTag(Theme.key_windowBackgroundWhiteGreenText2);
                                textCell.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGreenText2));
                                return;
                            } else if (i2 == ProfileActivity.this.addMemberRow) {
                                if (ProfileActivity.this.chat_id > 0) {
                                    textCell.setText(LocaleController.getString("AddMember", C0446R.string.AddMember));
                                    return;
                                } else {
                                    textCell.setText(LocaleController.getString("AddRecipient", C0446R.string.AddRecipient));
                                    return;
                                }
                            } else if (i2 != ProfileActivity.this.membersRow) {
                                return;
                            } else {
                                if (ProfileActivity.this.info != null) {
                                    if (!ChatObject.isChannel(ProfileActivity.this.currentChat) || ProfileActivity.this.currentChat.megagroup) {
                                        textCell.setTextAndValue(LocaleController.getString("ChannelMembers", C0446R.string.ChannelMembers), String.format("%d", new Object[]{Integer.valueOf(ProfileActivity.this.info.participants_count)}));
                                        return;
                                    } else {
                                        textCell.setTextAndValue(LocaleController.getString("ChannelSubscribers", C0446R.string.ChannelSubscribers), String.format("%d", new Object[]{Integer.valueOf(ProfileActivity.this.info.participants_count)}));
                                        return;
                                    }
                                } else if (!ChatObject.isChannel(ProfileActivity.this.currentChat) || ProfileActivity.this.currentChat.megagroup) {
                                    textCell.setText(LocaleController.getString("ChannelMembers", C0446R.string.ChannelMembers));
                                    return;
                                } else {
                                    textCell.setText(LocaleController.getString("ChannelSubscribers", C0446R.string.ChannelSubscribers));
                                    return;
                                }
                            }
                        case 4:
                            ChatParticipant chatParticipant;
                            UserCell userCell = (UserCell) viewHolder2.itemView;
                            if (ProfileActivity.this.sortedUsers.isEmpty()) {
                                chatParticipant = (ChatParticipant) ProfileActivity.this.info.participants.participants.get((i2 - ProfileActivity.this.emptyRowChat2) - 1);
                            } else {
                                chatParticipant = (ChatParticipant) ProfileActivity.this.info.participants.participants.get(((Integer) ProfileActivity.this.sortedUsers.get((i2 - ProfileActivity.this.emptyRowChat2) - 1)).intValue());
                            }
                            if (chatParticipant != null) {
                                if (chatParticipant instanceof TL_chatChannelParticipant) {
                                    ChannelParticipant channelParticipant = ((TL_chatChannelParticipant) chatParticipant).channelParticipant;
                                    if (channelParticipant instanceof TL_channelParticipantCreator) {
                                        userCell.setIsAdmin(1);
                                    } else if (channelParticipant instanceof TL_channelParticipantAdmin) {
                                        userCell.setIsAdmin(2);
                                    } else {
                                        userCell.setIsAdmin(0);
                                    }
                                } else if (chatParticipant instanceof TL_chatParticipantCreator) {
                                    userCell.setIsAdmin(1);
                                } else if (ProfileActivity.this.currentChat.admins_enabled && (chatParticipant instanceof TL_chatParticipantAdmin)) {
                                    userCell.setIsAdmin(2);
                                } else {
                                    userCell.setIsAdmin(0);
                                }
                                TLObject user2 = MessagesController.getInstance(ProfileActivity.this.currentAccount).getUser(Integer.valueOf(chatParticipant.user_id));
                                if (i2 == ProfileActivity.this.emptyRowChat2 + 1) {
                                    i3 = C0446R.drawable.menu_newgroup;
                                }
                                userCell.setData(user2, null, null, i3);
                                return;
                            }
                            return;
                        default:
                            return;
                    }
                }
                AboutLinkCell aboutLinkCell = (AboutLinkCell) viewHolder2.itemView;
                if (i2 == ProfileActivity.this.userInfoRow) {
                    userFull = MessagesController.getInstance(ProfileActivity.this.currentAccount).getUserFull(ProfileActivity.this.user_id);
                    aboutLinkCell.setTextAndIcon(userFull != null ? userFull.about : null, C0446R.drawable.profile_info, ProfileActivity.this.isBot);
                    return;
                } else if (i2 == ProfileActivity.this.channelInfoRow) {
                    string = ProfileActivity.this.info.about;
                    while (string.contains("\n\n\n")) {
                        string = string.replace("\n\n\n", "\n\n");
                    }
                    aboutLinkCell.setTextAndIcon(string, C0446R.drawable.profile_info, true);
                    return;
                } else {
                    return;
                }
            }
            if (i2 != ProfileActivity.this.emptyRowChat) {
                if (i2 != ProfileActivity.this.emptyRowChat2) {
                    ((EmptyCell) viewHolder2.itemView).setHeight(AndroidUtilities.dp(36.0f));
                    return;
                }
            }
            ((EmptyCell) viewHolder2.itemView).setHeight(AndroidUtilities.dp(8.0f));
        }

        public boolean isEnabled(ViewHolder viewHolder) {
            viewHolder = viewHolder.getAdapterPosition();
            boolean z = true;
            if (ProfileActivity.this.user_id != 0) {
                if (!(viewHolder == ProfileActivity.this.phoneRow || viewHolder == ProfileActivity.this.settingsTimerRow || viewHolder == ProfileActivity.this.settingsKeyRow || viewHolder == ProfileActivity.this.settingsNotificationsRow || viewHolder == ProfileActivity.this.sharedMediaRow || viewHolder == ProfileActivity.this.startSecretChatRow || viewHolder == ProfileActivity.this.usernameRow || viewHolder == ProfileActivity.this.userInfoRow || viewHolder == ProfileActivity.this.groupsInCommonRow)) {
                    if (viewHolder != ProfileActivity.this.userInfoDetailedRow) {
                        z = false;
                    }
                }
                return z;
            } else if (ProfileActivity.this.chat_id == 0) {
                return false;
            } else {
                if (!(viewHolder == ProfileActivity.this.convertRow || viewHolder == ProfileActivity.this.settingsNotificationsRow || viewHolder == ProfileActivity.this.sharedMediaRow || ((viewHolder > ProfileActivity.this.emptyRowChat2 && viewHolder < ProfileActivity.this.membersEndRow) || viewHolder == ProfileActivity.this.addMemberRow || viewHolder == ProfileActivity.this.channelNameRow || viewHolder == ProfileActivity.this.leaveChannelRow || viewHolder == ProfileActivity.this.channelInfoRow))) {
                    if (viewHolder != ProfileActivity.this.membersRow) {
                        z = false;
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

    public ProfileActivity(Bundle bundle) {
        super(bundle);
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
        boolean z;
        ActionBar c22584 = new ActionBar(context) {
            public boolean onTouchEvent(MotionEvent motionEvent) {
                return super.onTouchEvent(motionEvent);
            }
        };
        if (this.user_id == null) {
            if (ChatObject.isChannel(this.chat_id, this.currentAccount) == null || this.currentChat.megagroup != null) {
                context = this.chat_id;
                z = false;
                c22584.setItemsBackgroundColor(AvatarDrawable.getButtonColorForId(context), false);
                c22584.setItemsColor(Theme.getColor(Theme.key_actionBarDefaultIcon), false);
                c22584.setItemsColor(Theme.getColor(Theme.key_actionBarActionModeDefaultIcon), true);
                c22584.setBackButtonDrawable(new BackDrawable(false));
                c22584.setCastShadows(false);
                c22584.setAddToContainer(false);
                if (VERSION.SDK_INT >= 21 && AndroidUtilities.isTablet() == null) {
                    z = true;
                }
                c22584.setOccupyStatusBar(z);
                return c22584;
            }
        }
        context = 5;
        z = false;
        c22584.setItemsBackgroundColor(AvatarDrawable.getButtonColorForId(context), false);
        c22584.setItemsColor(Theme.getColor(Theme.key_actionBarDefaultIcon), false);
        c22584.setItemsColor(Theme.getColor(Theme.key_actionBarActionModeDefaultIcon), true);
        c22584.setBackButtonDrawable(new BackDrawable(false));
        c22584.setCastShadows(false);
        c22584.setAddToContainer(false);
        z = true;
        c22584.setOccupyStatusBar(z);
        return c22584;
    }

    public View createView(Context context) {
        int i;
        TopView topView;
        int i2;
        int i3;
        float f;
        SimpleTextView simpleTextView;
        int i4;
        float f2;
        Drawable createSimpleSelectorCircleDrawable;
        Drawable mutate;
        Drawable combinedDrawable;
        boolean isChannel;
        View view;
        int i5;
        StateListAnimator stateListAnimator;
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

            protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
                super.onLayout(z, i, i2, i3, i4);
                ProfileActivity.this.checkListViewScroll();
            }
        };
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
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
                    public boolean onItemClick(View view, int i) {
                        if (i <= ProfileActivity.this.emptyRowChat2 || i >= ProfileActivity.this.membersEndRow) {
                            return ProfileActivity.this.processOnClickOrPress(i);
                        }
                        if (ProfileActivity.this.getParentActivity() == null) {
                            return false;
                        }
                        boolean z;
                        if (ProfileActivity.this.sortedUsers.isEmpty() == null) {
                            view = (ChatParticipant) ProfileActivity.this.info.participants.participants.get(((Integer) ProfileActivity.this.sortedUsers.get((i - ProfileActivity.this.emptyRowChat2) - 1)).intValue());
                        } else {
                            view = (ChatParticipant) ProfileActivity.this.info.participants.participants.get((i - ProfileActivity.this.emptyRowChat2) - 1);
                        }
                        ProfileActivity.this.selectedUser = view.user_id;
                        boolean z2;
                        if (ChatObject.isChannel(ProfileActivity.this.currentChat) != 0) {
                            i = ((TL_chatChannelParticipant) view).channelParticipant;
                            if (view.user_id == UserConfig.getInstance(ProfileActivity.this.currentAccount).getClientUserId()) {
                                return false;
                            }
                            MessagesController.getInstance(ProfileActivity.this.currentAccount).getUser(Integer.valueOf(view.user_id));
                            if (!(i instanceof TL_channelParticipant)) {
                                if (!(i instanceof TL_channelParticipantBanned)) {
                                    z = false;
                                    z2 = ((i instanceof TL_channelParticipantAdmin) && !(i instanceof TL_channelParticipantCreator)) || i.can_edit;
                                }
                            }
                            z = true;
                            if (i instanceof TL_channelParticipantAdmin) {
                            }
                        } else {
                            i = 0;
                            if (view.user_id != UserConfig.getInstance(ProfileActivity.this.currentAccount).getClientUserId()) {
                                if (!ProfileActivity.this.currentChat.creator) {
                                    if (view instanceof TL_chatParticipant) {
                                        if (!(ProfileActivity.this.currentChat.admin && ProfileActivity.this.currentChat.admins_enabled)) {
                                            if (view.inviter_id == UserConfig.getInstance(ProfileActivity.this.currentAccount).getClientUserId()) {
                                            }
                                        }
                                    }
                                }
                                z = true;
                                if (!z) {
                                    return false;
                                }
                                z = false;
                                z2 = z;
                            }
                            z = false;
                            if (!z) {
                                return false;
                            }
                            z = false;
                            z2 = z;
                        }
                        Builder builder = new Builder(ProfileActivity.this.getParentActivity());
                        ArrayList arrayList = new ArrayList();
                        final ArrayList arrayList2 = new ArrayList();
                        if (ProfileActivity.this.currentChat.megagroup) {
                            if (z && ChatObject.canAddAdmins(ProfileActivity.this.currentChat)) {
                                arrayList.add(LocaleController.getString("SetAsAdmin", C0446R.string.SetAsAdmin));
                                arrayList2.add(Integer.valueOf(0));
                            }
                            if (ChatObject.canBlockUsers(ProfileActivity.this.currentChat) && r3) {
                                arrayList.add(LocaleController.getString("KickFromSupergroup", C0446R.string.KickFromSupergroup));
                                arrayList2.add(Integer.valueOf(1));
                                arrayList.add(LocaleController.getString("KickFromGroup", C0446R.string.KickFromGroup));
                                arrayList2.add(Integer.valueOf(2));
                            }
                        } else {
                            arrayList.add(ProfileActivity.this.chat_id > 0 ? LocaleController.getString("KickFromGroup", C0446R.string.KickFromGroup) : LocaleController.getString("KickFromBroadcast", C0446R.string.KickFromBroadcast));
                            arrayList2.add(Integer.valueOf(2));
                        }
                        if (arrayList.isEmpty()) {
                            return false;
                        }
                        builder.setItems((CharSequence[]) arrayList.toArray(new CharSequence[arrayList.size()]), new OnClickListener() {
                            public void onClick(DialogInterface dialogInterface, final int i) {
                                if (((Integer) arrayList2.get(i)).intValue() == 2) {
                                    ProfileActivity.this.kickUser(ProfileActivity.this.selectedUser);
                                    return;
                                }
                                DialogInterface channelRightsEditActivity = new ChannelRightsEditActivity(view.user_id, ProfileActivity.this.chat_id, i.admin_rights, i.banned_rights, ((Integer) arrayList2.get(i)).intValue(), true);
                                channelRightsEditActivity.setDelegate(new ChannelRightsEditActivityDelegate() {
                                    public void didSetRights(int i, TL_channelAdminRights tL_channelAdminRights, TL_channelBannedRights tL_channelBannedRights) {
                                        if (((Integer) arrayList2.get(i)).intValue() == 0) {
                                            TL_chatChannelParticipant tL_chatChannelParticipant = (TL_chatChannelParticipant) view;
                                            if (i == 1) {
                                                tL_chatChannelParticipant.channelParticipant = new TL_channelParticipantAdmin();
                                            } else {
                                                tL_chatChannelParticipant.channelParticipant = new TL_channelParticipant();
                                            }
                                            tL_chatChannelParticipant.channelParticipant.inviter_id = UserConfig.getInstance(ProfileActivity.this.currentAccount).getClientUserId();
                                            tL_chatChannelParticipant.channelParticipant.user_id = view.user_id;
                                            tL_chatChannelParticipant.channelParticipant.date = view.date;
                                            tL_chatChannelParticipant.channelParticipant.banned_rights = tL_channelBannedRights;
                                            tL_chatChannelParticipant.channelParticipant.admin_rights = tL_channelAdminRights;
                                        } else if (((Integer) arrayList2.get(i)).intValue() == 1 && i == 0 && ProfileActivity.this.currentChat.megagroup != 0 && ProfileActivity.this.info != 0 && ProfileActivity.this.info.participants != 0) {
                                            i = 0;
                                            for (TLObject tLObject = null; tLObject < ProfileActivity.this.info.participants.participants.size(); tLObject++) {
                                                if (((TL_chatChannelParticipant) ProfileActivity.this.info.participants.participants.get(tLObject)).channelParticipant.user_id == view.user_id) {
                                                    if (ProfileActivity.this.info != null) {
                                                        tL_channelBannedRights = ProfileActivity.this.info;
                                                        tL_channelBannedRights.participants_count--;
                                                    }
                                                    ProfileActivity.this.info.participants.participants.remove(tLObject);
                                                    tL_channelAdminRights = 1;
                                                    if (ProfileActivity.this.info != null && ProfileActivity.this.info.participants != null) {
                                                        while (i < ProfileActivity.this.info.participants.participants.size()) {
                                                            if (((ChatParticipant) ProfileActivity.this.info.participants.participants.get(i)).user_id == view.user_id) {
                                                                ProfileActivity.this.info.participants.participants.remove(i);
                                                                tL_channelAdminRights = 1;
                                                                break;
                                                            }
                                                            i++;
                                                        }
                                                    }
                                                    if (tL_channelAdminRights != null) {
                                                        ProfileActivity.this.updateOnlineCount();
                                                        ProfileActivity.this.updateRowsIds();
                                                        ProfileActivity.this.listAdapter.notifyDataSetChanged();
                                                    }
                                                }
                                            }
                                            tL_channelAdminRights = null;
                                            while (i < ProfileActivity.this.info.participants.participants.size()) {
                                                if (((ChatParticipant) ProfileActivity.this.info.participants.participants.get(i)).user_id == view.user_id) {
                                                    ProfileActivity.this.info.participants.participants.remove(i);
                                                    tL_channelAdminRights = 1;
                                                    break;
                                                }
                                                i++;
                                            }
                                            if (tL_channelAdminRights != null) {
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
                        TLObject tL_channels_getParticipant = new TL_channels_getParticipant();
                        tL_channels_getParticipant.channel = MessagesController.getInstance(r0.currentAccount).getInputChannel(r0.banFromGroup);
                        tL_channels_getParticipant.user_id = MessagesController.getInstance(r0.currentAccount).getInputUser(r0.user_id);
                        ConnectionsManager.getInstance(r0.currentAccount).sendRequest(tL_channels_getParticipant, new RequestDelegate() {
                            public void run(final TLObject tLObject, TL_error tL_error) {
                                if (tLObject != null) {
                                    AndroidUtilities.runOnUIThread(new Runnable() {
                                        public void run() {
                                            ProfileActivity.this.currentChannelParticipant = ((TL_channels_channelParticipant) tLObject).participant;
                                        }
                                    });
                                }
                            }
                        });
                    }
                    View anonymousClass12 = new FrameLayout(context2) {
                        protected void onDraw(Canvas canvas) {
                            int intrinsicHeight = Theme.chat_composeShadowDrawable.getIntrinsicHeight();
                            Theme.chat_composeShadowDrawable.setBounds(0, 0, getMeasuredWidth(), intrinsicHeight);
                            Theme.chat_composeShadowDrawable.draw(canvas);
                            canvas.drawRect(0.0f, (float) intrinsicHeight, (float) getMeasuredWidth(), (float) getMeasuredHeight(), Theme.chat_composeBackgroundPaint);
                        }
                    };
                    anonymousClass12.setWillNotDraw(false);
                    frameLayout.addView(anonymousClass12, LayoutHelper.createFrame(-1, 51, 83));
                    anonymousClass12.setOnClickListener(new View.OnClickListener() {

                        /* renamed from: org.telegram.ui.ProfileActivity$13$1 */
                        class C22561 implements ChannelRightsEditActivityDelegate {
                            C22561() {
                            }

                            public void didSetRights(int i, TL_channelAdminRights tL_channelAdminRights, TL_channelBannedRights tL_channelBannedRights) {
                                ProfileActivity.this.removeSelfFromStack();
                            }
                        }

                        public void onClick(View view) {
                            View channelRightsEditActivity = new ChannelRightsEditActivity(ProfileActivity.this.user_id, ProfileActivity.this.banFromGroup, null, ProfileActivity.this.currentChannelParticipant != null ? ProfileActivity.this.currentChannelParticipant.banned_rights : null, 1, true);
                            channelRightsEditActivity.setDelegate(new C22561());
                            ProfileActivity.this.presentFragment(channelRightsEditActivity);
                        }
                    });
                    View textView = new TextView(context2);
                    textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText));
                    textView.setTextSize(1, 15.0f);
                    textView.setGravity(17);
                    textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                    textView.setText(LocaleController.getString("BanFromTheGroup", C0446R.string.BanFromTheGroup));
                    anonymousClass12.addView(textView, LayoutHelper.createFrame(-2, -2.0f, 17, 0.0f, 1.0f, 0.0f, 0.0f));
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
                            public void onClick(View view) {
                                if (ProfileActivity.this.user_id != null) {
                                    view = MessagesController.getInstance(ProfileActivity.this.currentAccount).getUser(Integer.valueOf(ProfileActivity.this.user_id));
                                    if (view.photo != null && view.photo.photo_big != null) {
                                        PhotoViewer.getInstance().setParentActivity(ProfileActivity.this.getParentActivity());
                                        PhotoViewer.getInstance().openPhoto(view.photo.photo_big, ProfileActivity.this.provider);
                                    }
                                } else if (ProfileActivity.this.chat_id != null) {
                                    view = MessagesController.getInstance(ProfileActivity.this.currentAccount).getChat(Integer.valueOf(ProfileActivity.this.chat_id));
                                    if (view.photo != null && view.photo.photo_big != null) {
                                        PhotoViewer.getInstance().setParentActivity(ProfileActivity.this.getParentActivity());
                                        PhotoViewer.getInstance().openPhoto(view.photo.photo_big, ProfileActivity.this.provider);
                                    }
                                }
                            }
                        });
                        i3 = 0;
                        while (i3 < 2) {
                            if (r0.playProfileAnimation || i3 != 0) {
                                r0.nameTextView[i3] = new SimpleTextView(context2);
                                if (i3 != 1) {
                                    r0.nameTextView[i3].setTextColor(Theme.getColor(Theme.key_profile_title));
                                } else {
                                    r0.nameTextView[i3].setTextColor(Theme.getColor(Theme.key_actionBarDefaultTitle));
                                }
                                r0.nameTextView[i3].setTextSize(18);
                                r0.nameTextView[i3].setGravity(3);
                                r0.nameTextView[i3].setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                                r0.nameTextView[i3].setLeftDrawableTopPadding(-AndroidUtilities.dp(1.3f));
                                r0.nameTextView[i3].setPivotX(0.0f);
                                r0.nameTextView[i3].setPivotY(0.0f);
                                f = 1.0f;
                                r0.nameTextView[i3].setAlpha(i3 != 0 ? 0.0f : 1.0f);
                                frameLayout.addView(r0.nameTextView[i3], LayoutHelper.createFrame(-2, -2.0f, 51, 118.0f, 0.0f, i3 != 0 ? 48.0f : 0.0f, 0.0f));
                                r0.onlineTextView[i3] = new SimpleTextView(context2);
                                simpleTextView = r0.onlineTextView[i3];
                                if (r0.user_id == 0) {
                                    if (ChatObject.isChannel(r0.chat_id, r0.currentAccount) || r0.currentChat.megagroup) {
                                        i4 = r0.chat_id;
                                        simpleTextView.setTextColor(AvatarDrawable.getProfileTextColorForId(i4));
                                        r0.onlineTextView[i3].setTextSize(14);
                                        r0.onlineTextView[i3].setGravity(3);
                                        simpleTextView = r0.onlineTextView[i3];
                                        if (i3 == 0) {
                                            f = 0.0f;
                                        }
                                        simpleTextView.setAlpha(f);
                                        frameLayout.addView(r0.onlineTextView[i3], LayoutHelper.createFrame(-2, -2.0f, 51, 118.0f, 0.0f, i3 != 0 ? 48.0f : 8.0f, 0.0f));
                                    }
                                }
                                i4 = 5;
                                simpleTextView.setTextColor(AvatarDrawable.getProfileTextColorForId(i4));
                                r0.onlineTextView[i3].setTextSize(14);
                                r0.onlineTextView[i3].setGravity(3);
                                simpleTextView = r0.onlineTextView[i3];
                                if (i3 == 0) {
                                    f = 0.0f;
                                }
                                simpleTextView.setAlpha(f);
                                if (i3 != 0) {
                                }
                                frameLayout.addView(r0.onlineTextView[i3], LayoutHelper.createFrame(-2, -2.0f, 51, 118.0f, 0.0f, i3 != 0 ? 48.0f : 8.0f, 0.0f));
                            }
                            i3++;
                        }
                        if (r0.user_id != 0 || (r0.chat_id >= 0 && (!ChatObject.isLeftFromChat(r0.currentChat) || ChatObject.isChannel(r0.currentChat)))) {
                            r0.writeButton = new ImageView(context2);
                            f2 = 56.0f;
                            createSimpleSelectorCircleDrawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56.0f), Theme.getColor(Theme.key_profile_actionBackground), Theme.getColor(Theme.key_profile_actionPressedBackground));
                            if (VERSION.SDK_INT < 21) {
                                mutate = context.getResources().getDrawable(C0446R.drawable.floating_shadow_profile).mutate();
                                mutate.setColorFilter(new PorterDuffColorFilter(Theme.ACTION_BAR_VIDEO_EDIT_COLOR, Mode.MULTIPLY));
                                combinedDrawable = new CombinedDrawable(mutate, createSimpleSelectorCircleDrawable, 0, 0);
                                combinedDrawable.setIconSize(AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
                                createSimpleSelectorCircleDrawable = combinedDrawable;
                            }
                            r0.writeButton.setBackgroundDrawable(createSimpleSelectorCircleDrawable);
                            r0.writeButton.setScaleType(ScaleType.CENTER);
                            r0.writeButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_profile_actionIcon), Mode.MULTIPLY));
                            if (r0.user_id == 0) {
                                r0.writeButton.setImageResource(C0446R.drawable.floating_message);
                                r0.writeButton.setPadding(0, AndroidUtilities.dp(3.0f), 0, 0);
                            } else if (r0.chat_id != 0) {
                                isChannel = ChatObject.isChannel(r0.currentChat);
                                if ((isChannel || ChatObject.canEditInfo(r0.currentChat)) && (isChannel || r0.currentChat.admin || r0.currentChat.creator || !r0.currentChat.admins_enabled)) {
                                    r0.writeButton.setImageResource(C0446R.drawable.floating_camera);
                                } else {
                                    r0.writeButton.setImageResource(C0446R.drawable.floating_message);
                                    r0.writeButton.setPadding(0, AndroidUtilities.dp(3.0f), 0, 0);
                                }
                            }
                            view = r0.writeButton;
                            i5 = VERSION.SDK_INT < 21 ? 56 : 60;
                            if (VERSION.SDK_INT < 21) {
                                f2 = 60.0f;
                            }
                            frameLayout.addView(view, LayoutHelper.createFrame(i5, f2, 53, 0.0f, 0.0f, 16.0f, 0.0f));
                            if (VERSION.SDK_INT >= 21) {
                                stateListAnimator = new StateListAnimator();
                                stateListAnimator.addState(new int[]{16842919}, ObjectAnimator.ofFloat(r0.writeButton, "translationZ", new float[]{(float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(4.0f)}).setDuration(200));
                                stateListAnimator.addState(new int[0], ObjectAnimator.ofFloat(r0.writeButton, "translationZ", new float[]{(float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(2.0f)}).setDuration(200));
                                r0.writeButton.setStateListAnimator(stateListAnimator);
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

                                public void onClick(View view) {
                                    if (ProfileActivity.this.getParentActivity() != null) {
                                        if (ProfileActivity.this.user_id != null) {
                                            if (ProfileActivity.this.playProfileAnimation == null || (ProfileActivity.this.parentLayout.fragmentsStack.get(ProfileActivity.this.parentLayout.fragmentsStack.size() - 2) instanceof ChatActivity) == null) {
                                                view = MessagesController.getInstance(ProfileActivity.this.currentAccount).getUser(Integer.valueOf(ProfileActivity.this.user_id));
                                                if (view != null) {
                                                    if ((view instanceof TL_userEmpty) == null) {
                                                        view = new Bundle();
                                                        view.putInt("user_id", ProfileActivity.this.user_id);
                                                        if (MessagesController.getInstance(ProfileActivity.this.currentAccount).checkCanOpenChat(view, ProfileActivity.this)) {
                                                            NotificationCenter.getInstance(ProfileActivity.this.currentAccount).removeObserver(ProfileActivity.this, NotificationCenter.closeChats);
                                                            NotificationCenter.getInstance(ProfileActivity.this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                                                            ProfileActivity.this.presentFragment(new ChatActivity(view), true);
                                                        } else {
                                                            return;
                                                        }
                                                    }
                                                }
                                                return;
                                            }
                                            ProfileActivity.this.finishFragment();
                                        } else if (ProfileActivity.this.chat_id != null) {
                                            view = ChatObject.isChannel(ProfileActivity.this.currentChat);
                                            if ((view == null || ChatObject.canEditInfo(ProfileActivity.this.currentChat)) && !(view == null && ProfileActivity.this.currentChat.admin == null && ProfileActivity.this.currentChat.creator == null && ProfileActivity.this.currentChat.admins_enabled != null)) {
                                                CharSequence[] charSequenceArr;
                                                view = new Builder(ProfileActivity.this.getParentActivity());
                                                Chat chat = MessagesController.getInstance(ProfileActivity.this.currentAccount).getChat(Integer.valueOf(ProfileActivity.this.chat_id));
                                                if (!(chat.photo == null || chat.photo.photo_big == null)) {
                                                    if (!(chat.photo instanceof TL_chatPhotoEmpty)) {
                                                        charSequenceArr = new CharSequence[]{LocaleController.getString("FromCamera", C0446R.string.FromCamera), LocaleController.getString("FromGalley", C0446R.string.FromGalley), LocaleController.getString("DeletePhoto", C0446R.string.DeletePhoto)};
                                                        view.setItems(charSequenceArr, new C16411());
                                                        ProfileActivity.this.showDialog(view.create());
                                                    }
                                                }
                                                charSequenceArr = new CharSequence[]{LocaleController.getString("FromCamera", C0446R.string.FromCamera), LocaleController.getString("FromGalley", C0446R.string.FromGalley)};
                                                view.setItems(charSequenceArr, new C16411());
                                                ProfileActivity.this.showDialog(view.create());
                                            } else if (ProfileActivity.this.playProfileAnimation == null || (ProfileActivity.this.parentLayout.fragmentsStack.get(ProfileActivity.this.parentLayout.fragmentsStack.size() - 2) instanceof ChatActivity) == null) {
                                                view = new Bundle();
                                                view.putInt("chat_id", ProfileActivity.this.currentChat.id);
                                                if (MessagesController.getInstance(ProfileActivity.this.currentAccount).checkCanOpenChat(view, ProfileActivity.this)) {
                                                    NotificationCenter.getInstance(ProfileActivity.this.currentAccount).removeObserver(ProfileActivity.this, NotificationCenter.closeChats);
                                                    NotificationCenter.getInstance(ProfileActivity.this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                                                    ProfileActivity.this.presentFragment(new ChatActivity(view), true);
                                                }
                                            } else {
                                                ProfileActivity.this.finishFragment();
                                            }
                                        }
                                    }
                                }
                            });
                        }
                        needLayout();
                        r0.listView.setOnScrollListener(new OnScrollListener() {
                            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                                ProfileActivity.this.checkListViewScroll();
                                if (ProfileActivity.this.participantsMap != null && ProfileActivity.this.loadMoreMembersRow != -1 && ProfileActivity.this.layoutManager.findLastVisibleItemPosition() > ProfileActivity.this.loadMoreMembersRow - 8) {
                                    ProfileActivity.this.getChannelParticipants(0);
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
                i3 = 0;
                while (i3 < 2) {
                    if (!r0.playProfileAnimation) {
                    }
                    r0.nameTextView[i3] = new SimpleTextView(context2);
                    if (i3 != 1) {
                        r0.nameTextView[i3].setTextColor(Theme.getColor(Theme.key_actionBarDefaultTitle));
                    } else {
                        r0.nameTextView[i3].setTextColor(Theme.getColor(Theme.key_profile_title));
                    }
                    r0.nameTextView[i3].setTextSize(18);
                    r0.nameTextView[i3].setGravity(3);
                    r0.nameTextView[i3].setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                    r0.nameTextView[i3].setLeftDrawableTopPadding(-AndroidUtilities.dp(1.3f));
                    r0.nameTextView[i3].setPivotX(0.0f);
                    r0.nameTextView[i3].setPivotY(0.0f);
                    f = 1.0f;
                    if (i3 != 0) {
                    }
                    r0.nameTextView[i3].setAlpha(i3 != 0 ? 0.0f : 1.0f);
                    if (i3 != 0) {
                    }
                    frameLayout.addView(r0.nameTextView[i3], LayoutHelper.createFrame(-2, -2.0f, 51, 118.0f, 0.0f, i3 != 0 ? 48.0f : 0.0f, 0.0f));
                    r0.onlineTextView[i3] = new SimpleTextView(context2);
                    simpleTextView = r0.onlineTextView[i3];
                    if (r0.user_id == 0) {
                        if (ChatObject.isChannel(r0.chat_id, r0.currentAccount)) {
                        }
                        i4 = r0.chat_id;
                        simpleTextView.setTextColor(AvatarDrawable.getProfileTextColorForId(i4));
                        r0.onlineTextView[i3].setTextSize(14);
                        r0.onlineTextView[i3].setGravity(3);
                        simpleTextView = r0.onlineTextView[i3];
                        if (i3 == 0) {
                            f = 0.0f;
                        }
                        simpleTextView.setAlpha(f);
                        if (i3 != 0) {
                        }
                        frameLayout.addView(r0.onlineTextView[i3], LayoutHelper.createFrame(-2, -2.0f, 51, 118.0f, 0.0f, i3 != 0 ? 48.0f : 8.0f, 0.0f));
                        i3++;
                    }
                    i4 = 5;
                    simpleTextView.setTextColor(AvatarDrawable.getProfileTextColorForId(i4));
                    r0.onlineTextView[i3].setTextSize(14);
                    r0.onlineTextView[i3].setGravity(3);
                    simpleTextView = r0.onlineTextView[i3];
                    if (i3 == 0) {
                        f = 0.0f;
                    }
                    simpleTextView.setAlpha(f);
                    if (i3 != 0) {
                    }
                    frameLayout.addView(r0.onlineTextView[i3], LayoutHelper.createFrame(-2, -2.0f, 51, 118.0f, 0.0f, i3 != 0 ? 48.0f : 8.0f, 0.0f));
                    i3++;
                }
                r0.writeButton = new ImageView(context2);
                f2 = 56.0f;
                createSimpleSelectorCircleDrawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56.0f), Theme.getColor(Theme.key_profile_actionBackground), Theme.getColor(Theme.key_profile_actionPressedBackground));
                if (VERSION.SDK_INT < 21) {
                    mutate = context.getResources().getDrawable(C0446R.drawable.floating_shadow_profile).mutate();
                    mutate.setColorFilter(new PorterDuffColorFilter(Theme.ACTION_BAR_VIDEO_EDIT_COLOR, Mode.MULTIPLY));
                    combinedDrawable = new CombinedDrawable(mutate, createSimpleSelectorCircleDrawable, 0, 0);
                    combinedDrawable.setIconSize(AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
                    createSimpleSelectorCircleDrawable = combinedDrawable;
                }
                r0.writeButton.setBackgroundDrawable(createSimpleSelectorCircleDrawable);
                r0.writeButton.setScaleType(ScaleType.CENTER);
                r0.writeButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_profile_actionIcon), Mode.MULTIPLY));
                if (r0.user_id == 0) {
                    r0.writeButton.setImageResource(C0446R.drawable.floating_message);
                    r0.writeButton.setPadding(0, AndroidUtilities.dp(3.0f), 0, 0);
                } else if (r0.chat_id != 0) {
                    isChannel = ChatObject.isChannel(r0.currentChat);
                    if (isChannel) {
                    }
                    r0.writeButton.setImageResource(C0446R.drawable.floating_camera);
                }
                view = r0.writeButton;
                if (VERSION.SDK_INT < 21) {
                }
                i5 = VERSION.SDK_INT < 21 ? 56 : 60;
                if (VERSION.SDK_INT < 21) {
                    f2 = 60.0f;
                }
                frameLayout.addView(view, LayoutHelper.createFrame(i5, f2, 53, 0.0f, 0.0f, 16.0f, 0.0f));
                if (VERSION.SDK_INT >= 21) {
                    stateListAnimator = new StateListAnimator();
                    stateListAnimator.addState(new int[]{16842919}, ObjectAnimator.ofFloat(r0.writeButton, "translationZ", new float[]{(float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(4.0f)}).setDuration(200));
                    stateListAnimator.addState(new int[0], ObjectAnimator.ofFloat(r0.writeButton, "translationZ", new float[]{(float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(2.0f)}).setDuration(200));
                    r0.writeButton.setStateListAnimator(stateListAnimator);
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
                TLObject tL_channels_getParticipant2 = new TL_channels_getParticipant();
                tL_channels_getParticipant2.channel = MessagesController.getInstance(r0.currentAccount).getInputChannel(r0.banFromGroup);
                tL_channels_getParticipant2.user_id = MessagesController.getInstance(r0.currentAccount).getInputUser(r0.user_id);
                ConnectionsManager.getInstance(r0.currentAccount).sendRequest(tL_channels_getParticipant2, /* anonymous class already generated */);
            }
            View anonymousClass122 = /* anonymous class already generated */;
            anonymousClass122.setWillNotDraw(false);
            frameLayout.addView(anonymousClass122, LayoutHelper.createFrame(-1, 51, 83));
            anonymousClass122.setOnClickListener(/* anonymous class already generated */);
            View textView2 = new TextView(context2);
            textView2.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteRedText));
            textView2.setTextSize(1, 15.0f);
            textView2.setGravity(17);
            textView2.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            textView2.setText(LocaleController.getString("BanFromTheGroup", C0446R.string.BanFromTheGroup));
            anonymousClass122.addView(textView2, LayoutHelper.createFrame(-2, -2.0f, 17, 0.0f, 1.0f, 0.0f, 0.0f));
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
            i3 = 0;
            while (i3 < 2) {
                if (r0.playProfileAnimation) {
                }
                r0.nameTextView[i3] = new SimpleTextView(context2);
                if (i3 != 1) {
                    r0.nameTextView[i3].setTextColor(Theme.getColor(Theme.key_profile_title));
                } else {
                    r0.nameTextView[i3].setTextColor(Theme.getColor(Theme.key_actionBarDefaultTitle));
                }
                r0.nameTextView[i3].setTextSize(18);
                r0.nameTextView[i3].setGravity(3);
                r0.nameTextView[i3].setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                r0.nameTextView[i3].setLeftDrawableTopPadding(-AndroidUtilities.dp(1.3f));
                r0.nameTextView[i3].setPivotX(0.0f);
                r0.nameTextView[i3].setPivotY(0.0f);
                f = 1.0f;
                if (i3 != 0) {
                }
                r0.nameTextView[i3].setAlpha(i3 != 0 ? 0.0f : 1.0f);
                if (i3 != 0) {
                }
                frameLayout.addView(r0.nameTextView[i3], LayoutHelper.createFrame(-2, -2.0f, 51, 118.0f, 0.0f, i3 != 0 ? 48.0f : 0.0f, 0.0f));
                r0.onlineTextView[i3] = new SimpleTextView(context2);
                simpleTextView = r0.onlineTextView[i3];
                if (r0.user_id == 0) {
                    if (ChatObject.isChannel(r0.chat_id, r0.currentAccount)) {
                    }
                    i4 = r0.chat_id;
                    simpleTextView.setTextColor(AvatarDrawable.getProfileTextColorForId(i4));
                    r0.onlineTextView[i3].setTextSize(14);
                    r0.onlineTextView[i3].setGravity(3);
                    simpleTextView = r0.onlineTextView[i3];
                    if (i3 == 0) {
                        f = 0.0f;
                    }
                    simpleTextView.setAlpha(f);
                    if (i3 != 0) {
                    }
                    frameLayout.addView(r0.onlineTextView[i3], LayoutHelper.createFrame(-2, -2.0f, 51, 118.0f, 0.0f, i3 != 0 ? 48.0f : 8.0f, 0.0f));
                    i3++;
                }
                i4 = 5;
                simpleTextView.setTextColor(AvatarDrawable.getProfileTextColorForId(i4));
                r0.onlineTextView[i3].setTextSize(14);
                r0.onlineTextView[i3].setGravity(3);
                simpleTextView = r0.onlineTextView[i3];
                if (i3 == 0) {
                    f = 0.0f;
                }
                simpleTextView.setAlpha(f);
                if (i3 != 0) {
                }
                frameLayout.addView(r0.onlineTextView[i3], LayoutHelper.createFrame(-2, -2.0f, 51, 118.0f, 0.0f, i3 != 0 ? 48.0f : 8.0f, 0.0f));
                i3++;
            }
            r0.writeButton = new ImageView(context2);
            f2 = 56.0f;
            createSimpleSelectorCircleDrawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56.0f), Theme.getColor(Theme.key_profile_actionBackground), Theme.getColor(Theme.key_profile_actionPressedBackground));
            if (VERSION.SDK_INT < 21) {
                mutate = context.getResources().getDrawable(C0446R.drawable.floating_shadow_profile).mutate();
                mutate.setColorFilter(new PorterDuffColorFilter(Theme.ACTION_BAR_VIDEO_EDIT_COLOR, Mode.MULTIPLY));
                combinedDrawable = new CombinedDrawable(mutate, createSimpleSelectorCircleDrawable, 0, 0);
                combinedDrawable.setIconSize(AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
                createSimpleSelectorCircleDrawable = combinedDrawable;
            }
            r0.writeButton.setBackgroundDrawable(createSimpleSelectorCircleDrawable);
            r0.writeButton.setScaleType(ScaleType.CENTER);
            r0.writeButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_profile_actionIcon), Mode.MULTIPLY));
            if (r0.user_id == 0) {
                r0.writeButton.setImageResource(C0446R.drawable.floating_message);
                r0.writeButton.setPadding(0, AndroidUtilities.dp(3.0f), 0, 0);
            } else if (r0.chat_id != 0) {
                isChannel = ChatObject.isChannel(r0.currentChat);
                if (isChannel) {
                }
                r0.writeButton.setImageResource(C0446R.drawable.floating_camera);
            }
            view = r0.writeButton;
            if (VERSION.SDK_INT < 21) {
            }
            i5 = VERSION.SDK_INT < 21 ? 56 : 60;
            if (VERSION.SDK_INT < 21) {
                f2 = 60.0f;
            }
            frameLayout.addView(view, LayoutHelper.createFrame(i5, f2, 53, 0.0f, 0.0f, 16.0f, 0.0f));
            if (VERSION.SDK_INT >= 21) {
                stateListAnimator = new StateListAnimator();
                stateListAnimator.addState(new int[]{16842919}, ObjectAnimator.ofFloat(r0.writeButton, "translationZ", new float[]{(float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(4.0f)}).setDuration(200));
                stateListAnimator.addState(new int[0], ObjectAnimator.ofFloat(r0.writeButton, "translationZ", new float[]{(float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(2.0f)}).setDuration(200));
                r0.writeButton.setStateListAnimator(stateListAnimator);
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
        i3 = 0;
        while (i3 < 2) {
            if (r0.playProfileAnimation) {
            }
            r0.nameTextView[i3] = new SimpleTextView(context2);
            if (i3 != 1) {
                r0.nameTextView[i3].setTextColor(Theme.getColor(Theme.key_actionBarDefaultTitle));
            } else {
                r0.nameTextView[i3].setTextColor(Theme.getColor(Theme.key_profile_title));
            }
            r0.nameTextView[i3].setTextSize(18);
            r0.nameTextView[i3].setGravity(3);
            r0.nameTextView[i3].setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            r0.nameTextView[i3].setLeftDrawableTopPadding(-AndroidUtilities.dp(1.3f));
            r0.nameTextView[i3].setPivotX(0.0f);
            r0.nameTextView[i3].setPivotY(0.0f);
            f = 1.0f;
            if (i3 != 0) {
            }
            r0.nameTextView[i3].setAlpha(i3 != 0 ? 0.0f : 1.0f);
            if (i3 != 0) {
            }
            frameLayout.addView(r0.nameTextView[i3], LayoutHelper.createFrame(-2, -2.0f, 51, 118.0f, 0.0f, i3 != 0 ? 48.0f : 0.0f, 0.0f));
            r0.onlineTextView[i3] = new SimpleTextView(context2);
            simpleTextView = r0.onlineTextView[i3];
            if (r0.user_id == 0) {
                if (ChatObject.isChannel(r0.chat_id, r0.currentAccount)) {
                }
                i4 = r0.chat_id;
                simpleTextView.setTextColor(AvatarDrawable.getProfileTextColorForId(i4));
                r0.onlineTextView[i3].setTextSize(14);
                r0.onlineTextView[i3].setGravity(3);
                simpleTextView = r0.onlineTextView[i3];
                if (i3 == 0) {
                    f = 0.0f;
                }
                simpleTextView.setAlpha(f);
                if (i3 != 0) {
                }
                frameLayout.addView(r0.onlineTextView[i3], LayoutHelper.createFrame(-2, -2.0f, 51, 118.0f, 0.0f, i3 != 0 ? 48.0f : 8.0f, 0.0f));
                i3++;
            }
            i4 = 5;
            simpleTextView.setTextColor(AvatarDrawable.getProfileTextColorForId(i4));
            r0.onlineTextView[i3].setTextSize(14);
            r0.onlineTextView[i3].setGravity(3);
            simpleTextView = r0.onlineTextView[i3];
            if (i3 == 0) {
                f = 0.0f;
            }
            simpleTextView.setAlpha(f);
            if (i3 != 0) {
            }
            frameLayout.addView(r0.onlineTextView[i3], LayoutHelper.createFrame(-2, -2.0f, 51, 118.0f, 0.0f, i3 != 0 ? 48.0f : 8.0f, 0.0f));
            i3++;
        }
        r0.writeButton = new ImageView(context2);
        f2 = 56.0f;
        createSimpleSelectorCircleDrawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56.0f), Theme.getColor(Theme.key_profile_actionBackground), Theme.getColor(Theme.key_profile_actionPressedBackground));
        if (VERSION.SDK_INT < 21) {
            mutate = context.getResources().getDrawable(C0446R.drawable.floating_shadow_profile).mutate();
            mutate.setColorFilter(new PorterDuffColorFilter(Theme.ACTION_BAR_VIDEO_EDIT_COLOR, Mode.MULTIPLY));
            combinedDrawable = new CombinedDrawable(mutate, createSimpleSelectorCircleDrawable, 0, 0);
            combinedDrawable.setIconSize(AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
            createSimpleSelectorCircleDrawable = combinedDrawable;
        }
        r0.writeButton.setBackgroundDrawable(createSimpleSelectorCircleDrawable);
        r0.writeButton.setScaleType(ScaleType.CENTER);
        r0.writeButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_profile_actionIcon), Mode.MULTIPLY));
        if (r0.user_id == 0) {
            r0.writeButton.setImageResource(C0446R.drawable.floating_message);
            r0.writeButton.setPadding(0, AndroidUtilities.dp(3.0f), 0, 0);
        } else if (r0.chat_id != 0) {
            isChannel = ChatObject.isChannel(r0.currentChat);
            if (isChannel) {
            }
            r0.writeButton.setImageResource(C0446R.drawable.floating_camera);
        }
        view = r0.writeButton;
        if (VERSION.SDK_INT < 21) {
        }
        i5 = VERSION.SDK_INT < 21 ? 56 : 60;
        if (VERSION.SDK_INT < 21) {
            f2 = 60.0f;
        }
        frameLayout.addView(view, LayoutHelper.createFrame(i5, f2, 53, 0.0f, 0.0f, 16.0f, 0.0f));
        if (VERSION.SDK_INT >= 21) {
            stateListAnimator = new StateListAnimator();
            stateListAnimator.addState(new int[]{16842919}, ObjectAnimator.ofFloat(r0.writeButton, "translationZ", new float[]{(float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(4.0f)}).setDuration(200));
            stateListAnimator.addState(new int[0], ObjectAnimator.ofFloat(r0.writeButton, "translationZ", new float[]{(float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(2.0f)}).setDuration(200));
            r0.writeButton.setStateListAnimator(stateListAnimator);
            r0.writeButton.setOutlineProvider(/* anonymous class already generated */);
        }
        r0.writeButton.setOnClickListener(/* anonymous class already generated */);
        needLayout();
        r0.listView.setOnScrollListener(/* anonymous class already generated */);
        return r0.fragmentView;
    }

    private boolean processOnClickOrPress(final int i) {
        Builder builder;
        if (i != this.usernameRow) {
            if (i != this.channelNameRow) {
                if (i == this.phoneRow) {
                    i = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.user_id));
                    if (!(i == 0 || i.phone == null || i.phone.length() == 0)) {
                        if (getParentActivity() != null) {
                            builder = new Builder(getParentActivity());
                            ArrayList arrayList = new ArrayList();
                            final ArrayList arrayList2 = new ArrayList();
                            TL_userFull userFull = MessagesController.getInstance(this.currentAccount).getUserFull(i.id);
                            if (userFull != null && userFull.phone_calls_available) {
                                arrayList.add(LocaleController.getString("CallViaTelegram", C0446R.string.CallViaTelegram));
                                arrayList2.add(Integer.valueOf(2));
                            }
                            arrayList.add(LocaleController.getString("Call", C0446R.string.Call));
                            arrayList2.add(Integer.valueOf(0));
                            arrayList.add(LocaleController.getString("Copy", C0446R.string.Copy));
                            arrayList2.add(Integer.valueOf(1));
                            builder.setItems((CharSequence[]) arrayList.toArray(new CharSequence[arrayList.size()]), new OnClickListener() {
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface = ((Integer) arrayList2.get(i)).intValue();
                                    StringBuilder stringBuilder;
                                    if (dialogInterface == null) {
                                        try {
                                            stringBuilder = new StringBuilder();
                                            stringBuilder.append("tel:+");
                                            stringBuilder.append(i.phone);
                                            dialogInterface = new Intent("android.intent.action.DIAL", Uri.parse(stringBuilder.toString()));
                                            dialogInterface.addFlags(268435456);
                                            ProfileActivity.this.getParentActivity().startActivityForResult(dialogInterface, 500);
                                        } catch (Throwable e) {
                                            FileLog.m3e(e);
                                        }
                                    } else if (dialogInterface == 1) {
                                        try {
                                            ClipboardManager clipboardManager = (ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard");
                                            stringBuilder = new StringBuilder();
                                            stringBuilder.append("+");
                                            stringBuilder.append(i.phone);
                                            clipboardManager.setPrimaryClip(ClipData.newPlainText("label", stringBuilder.toString()));
                                        } catch (Throwable e2) {
                                            FileLog.m3e(e2);
                                        }
                                    } else if (dialogInterface == 2) {
                                        VoIPHelper.startCall(i, ProfileActivity.this.getParentActivity(), MessagesController.getInstance(ProfileActivity.this.currentAccount).getUserFull(i.id));
                                    }
                                }
                            });
                            showDialog(builder.create());
                            return true;
                        }
                    }
                    return false;
                }
                if (!(i == this.channelInfoRow || i == this.userInfoRow)) {
                    if (i != this.userInfoDetailedRow) {
                        return false;
                    }
                }
                builder = new Builder(getParentActivity());
                builder.setItems(new CharSequence[]{LocaleController.getString("Copy", C0446R.string.Copy)}, new OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        try {
                            if (i == ProfileActivity.this.channelInfoRow) {
                                dialogInterface = ProfileActivity.this.info.about;
                            } else {
                                dialogInterface = MessagesController.getInstance(ProfileActivity.this.currentAccount).getUserFull(ProfileActivity.this.user_id);
                                dialogInterface = dialogInterface != null ? dialogInterface.about : null;
                            }
                            if (TextUtils.isEmpty(dialogInterface) == 0) {
                                AndroidUtilities.addToClipboard(dialogInterface);
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
        if (i == this.usernameRow) {
            i = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.user_id));
            if (i != 0) {
                if (i.username != null) {
                    i = i.username;
                }
            }
            return false;
        }
        i = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.chat_id));
        if (i != 0) {
            if (i.username != null) {
                i = i.username;
            }
        }
        return false;
        builder = new Builder(getParentActivity());
        builder.setItems(new CharSequence[]{LocaleController.getString("Copy", C0446R.string.Copy)}, new OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    try {
                        ClipboardManager clipboardManager = (ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard");
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("@");
                        stringBuilder.append(i);
                        clipboardManager.setPrimaryClip(ClipData.newPlainText("label", stringBuilder.toString()));
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
        int i = C0446R.string.AreYouSureDeleteAndExit;
        if (!isChannel || this.currentChat.megagroup) {
            builder.setMessage(LocaleController.getString("AreYouSureDeleteAndExit", C0446R.string.AreYouSureDeleteAndExit));
        } else {
            String str;
            if (ChatObject.isChannel(this.chat_id, this.currentAccount)) {
                str = "ChannelLeaveAlert";
                i = C0446R.string.ChannelLeaveAlert;
            } else {
                str = "AreYouSureDeleteAndExit";
            }
            builder.setMessage(LocaleController.getString(str, i));
        }
        builder.setTitle(LocaleController.getString("AppName", C0446R.string.AppName));
        builder.setPositiveButton(LocaleController.getString("OK", C0446R.string.OK), new OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                ProfileActivity.this.kickUser(0);
            }
        });
        builder.setNegativeButton(LocaleController.getString("Cancel", C0446R.string.Cancel), null);
        showDialog(builder.create());
    }

    public void saveSelfArgs(Bundle bundle) {
        if (this.chat_id != 0 && this.avatarUpdater != null && this.avatarUpdater.currentPicturePath != null) {
            bundle.putString("path", this.avatarUpdater.currentPicturePath);
        }
    }

    public void restoreSelfArgs(Bundle bundle) {
        if (this.chat_id != 0) {
            MessagesController.getInstance(this.currentAccount).loadChatInfo(this.chat_id, null, false);
            if (this.avatarUpdater != null) {
                this.avatarUpdater.currentPicturePath = bundle.getString("path");
            }
        }
    }

    public void onActivityResultFragment(int i, int i2, Intent intent) {
        if (this.chat_id != 0) {
            this.avatarUpdater.onActivityResult(i, i2, intent);
        }
    }

    private void getChannelParticipants(boolean z) {
        if (!(this.loadingUsers || this.participantsMap == null)) {
            if (this.info != null) {
                this.loadingUsers = true;
                int i = 0;
                final int i2 = (this.participantsMap.size() == 0 || !z) ? 0 : 300;
                final TLObject tL_channels_getParticipants = new TL_channels_getParticipants();
                tL_channels_getParticipants.channel = MessagesController.getInstance(this.currentAccount).getInputChannel(this.chat_id);
                tL_channels_getParticipants.filter = new TL_channelParticipantsRecent();
                if (!z) {
                    i = this.participantsMap.size();
                }
                tL_channels_getParticipants.offset = i;
                tL_channels_getParticipants.limit = true;
                ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_channels_getParticipants, new RequestDelegate() {
                    public void run(final TLObject tLObject, final TL_error tL_error) {
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                if (tL_error == null) {
                                    TL_channels_channelParticipants tL_channels_channelParticipants = (TL_channels_channelParticipants) tLObject;
                                    MessagesController.getInstance(ProfileActivity.this.currentAccount).putUsers(tL_channels_channelParticipants.users, false);
                                    if (tL_channels_channelParticipants.users.size() < Callback.DEFAULT_DRAG_ANIMATION_DURATION) {
                                        ProfileActivity.this.usersEndReached = true;
                                    }
                                    if (tL_channels_getParticipants.offset == 0) {
                                        ProfileActivity.this.participantsMap.clear();
                                        ProfileActivity.this.info.participants = new TL_chatParticipants();
                                        MessagesStorage.getInstance(ProfileActivity.this.currentAccount).putUsersAndChats(tL_channels_channelParticipants.users, null, true, true);
                                        MessagesStorage.getInstance(ProfileActivity.this.currentAccount).updateChannelUsers(ProfileActivity.this.chat_id, tL_channels_channelParticipants.participants);
                                    }
                                    for (int i = 0; i < tL_channels_channelParticipants.participants.size(); i++) {
                                        TL_chatChannelParticipant tL_chatChannelParticipant = new TL_chatChannelParticipant();
                                        tL_chatChannelParticipant.channelParticipant = (ChannelParticipant) tL_channels_channelParticipants.participants.get(i);
                                        tL_chatChannelParticipant.inviter_id = tL_chatChannelParticipant.channelParticipant.inviter_id;
                                        tL_chatChannelParticipant.user_id = tL_chatChannelParticipant.channelParticipant.user_id;
                                        tL_chatChannelParticipant.date = tL_chatChannelParticipant.channelParticipant.date;
                                        if (ProfileActivity.this.participantsMap.indexOfKey(tL_chatChannelParticipant.user_id) < 0) {
                                            ProfileActivity.this.info.participants.participants.add(tL_chatChannelParticipant);
                                            ProfileActivity.this.participantsMap.put(tL_chatChannelParticipant.user_id, tL_chatChannelParticipant);
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
                        }, (long) i2);
                    }
                }), this.classGuid);
            }
        }
    }

    private void openAddMember() {
        Bundle bundle = new Bundle();
        bundle.putBoolean("onlyUsers", true);
        bundle.putBoolean("destroyAfterSelect", true);
        bundle.putBoolean("returnAsResult", true);
        bundle.putBoolean("needForwardCount", true ^ ChatObject.isChannel(this.currentChat));
        if (this.chat_id > 0) {
            if (ChatObject.canAddViaLink(this.currentChat)) {
                bundle.putInt("chat_id", this.currentChat.id);
            }
            bundle.putString("selectAlertString", LocaleController.getString("AddToTheGroup", C0446R.string.AddToTheGroup));
        }
        BaseFragment contactsActivity = new ContactsActivity(bundle);
        contactsActivity.setDelegate(new ContactsActivityDelegate() {
            public void didSelectContact(User user, String str, ContactsActivity contactsActivity) {
                MessagesController.getInstance(ProfileActivity.this.currentAccount).addUserToChat(ProfileActivity.this.chat_id, user, ProfileActivity.this.info, str != null ? Utilities.parseInt(str).intValue() : null, null, ProfileActivity.this);
            }
        });
        if (!(this.info == null || this.info.participants == null)) {
            SparseArray sparseArray = new SparseArray();
            for (int i = 0; i < this.info.participants.participants.size(); i++) {
                sparseArray.put(((ChatParticipant) this.info.participants.participants.get(i)).user_id, null);
            }
            contactsActivity.setIgnoreUsers(sparseArray);
        }
        presentFragment(contactsActivity);
    }

    private void checkListViewScroll() {
        if (this.listView.getChildCount() > 0) {
            if (!this.openAnimationInProgress) {
                boolean z = false;
                View childAt = this.listView.getChildAt(0);
                Holder holder = (Holder) this.listView.findContainingViewHolder(childAt);
                int top = childAt.getTop();
                if (top < 0 || holder == null || holder.getAdapterPosition() != 0) {
                    top = 0;
                }
                if (this.extraHeight != top) {
                    this.extraHeight = top;
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
        int currentActionBarHeight = (this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0) + ActionBar.getCurrentActionBarHeight();
        if (!(this.listView == null || this.openAnimationInProgress)) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.listView.getLayoutParams();
            if (layoutParams.topMargin != currentActionBarHeight) {
                layoutParams.topMargin = currentActionBarHeight;
                this.listView.setLayoutParams(layoutParams);
            }
        }
        if (this.avatarImage != null) {
            int i;
            float dp = ((float) this.extraHeight) / ((float) AndroidUtilities.dp(88.0f));
            this.listView.setTopGlowOffset(this.extraHeight);
            if (this.writeButton != null) {
                this.writeButton.setTranslationY((float) ((((this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0) + ActionBar.getCurrentActionBarHeight()) + this.extraHeight) - AndroidUtilities.dp(29.5f)));
                if (!this.openAnimationInProgress) {
                    i = dp > 0.2f ? 1 : 0;
                    if (i != (this.writeButton.getTag() == null ? 1 : 0)) {
                        if (i != 0) {
                            this.writeButton.setTag(null);
                        } else {
                            this.writeButton.setTag(Integer.valueOf(0));
                        }
                        if (this.writeButtonAnimation != null) {
                            AnimatorSet animatorSet = this.writeButtonAnimation;
                            this.writeButtonAnimation = null;
                            animatorSet.cancel();
                        }
                        this.writeButtonAnimation = new AnimatorSet();
                        if (i != 0) {
                            this.writeButtonAnimation.setInterpolator(new DecelerateInterpolator());
                            AnimatorSet animatorSet2 = this.writeButtonAnimation;
                            r6 = new Animator[3];
                            r6[0] = ObjectAnimator.ofFloat(this.writeButton, "scaleX", new float[]{1.0f});
                            r6[1] = ObjectAnimator.ofFloat(this.writeButton, "scaleY", new float[]{1.0f});
                            r6[2] = ObjectAnimator.ofFloat(this.writeButton, "alpha", new float[]{1.0f});
                            animatorSet2.playTogether(r6);
                        } else {
                            this.writeButtonAnimation.setInterpolator(new AccelerateInterpolator());
                            AnimatorSet animatorSet3 = this.writeButtonAnimation;
                            r7 = new Animator[3];
                            r7[0] = ObjectAnimator.ofFloat(this.writeButton, "scaleX", new float[]{0.2f});
                            r7[1] = ObjectAnimator.ofFloat(this.writeButton, "scaleY", new float[]{0.2f});
                            r7[2] = ObjectAnimator.ofFloat(this.writeButton, "alpha", new float[]{0.0f});
                            animatorSet3.playTogether(r7);
                        }
                        this.writeButtonAnimation.setDuration(150);
                        this.writeButtonAnimation.addListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animator) {
                                if (ProfileActivity.this.writeButtonAnimation != null && ProfileActivity.this.writeButtonAnimation.equals(animator) != null) {
                                    ProfileActivity.this.writeButtonAnimation = null;
                                }
                            }
                        });
                        this.writeButtonAnimation.start();
                    }
                }
            }
            float currentActionBarHeight2 = ((((float) (this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0)) + ((((float) ActionBar.getCurrentActionBarHeight()) / 2.0f) * (1.0f + dp))) - (21.0f * AndroidUtilities.density)) + ((27.0f * AndroidUtilities.density) * dp);
            float f = ((18.0f * dp) + 42.0f) / 42.0f;
            this.avatarImage.setScaleX(f);
            this.avatarImage.setScaleY(f);
            this.avatarImage.setTranslationX(((float) (-AndroidUtilities.dp(47.0f))) * dp);
            double d = (double) currentActionBarHeight2;
            this.avatarImage.setTranslationY((float) Math.ceil(d));
            for (int i2 = 0; i2 < 2; i2++) {
                if (this.nameTextView[i2] != null) {
                    this.nameTextView[i2].setTranslationX((AndroidUtilities.density * -21.0f) * dp);
                    this.nameTextView[i2].setTranslationY((((float) Math.floor(d)) + ((float) AndroidUtilities.dp(1.3f))) + (((float) AndroidUtilities.dp(7.0f)) * dp));
                    this.onlineTextView[i2].setTranslationX((-21.0f * AndroidUtilities.density) * dp);
                    this.onlineTextView[i2].setTranslationY((((float) Math.floor(d)) + ((float) AndroidUtilities.dp(24.0f))) + (((float) Math.floor((double) (11.0f * AndroidUtilities.density))) * dp));
                    float f2 = (0.12f * dp) + 1.0f;
                    this.nameTextView[i2].setScaleX(f2);
                    this.nameTextView[i2].setScaleY(f2);
                    if (i2 == 1 && !this.openAnimationInProgress) {
                        int i3;
                        float f3;
                        FrameLayout.LayoutParams layoutParams2;
                        float dp2;
                        FrameLayout.LayoutParams layoutParams3;
                        if (AndroidUtilities.isTablet()) {
                            i = AndroidUtilities.dp(490.0f);
                        } else {
                            i = AndroidUtilities.displaySize.x;
                        }
                        if (this.callItem == null) {
                            if (this.editItem == null) {
                                i3 = 0;
                                f3 = 1.0f - dp;
                                layoutParams2 = (FrameLayout.LayoutParams) this.nameTextView[i2].getLayoutParams();
                                dp2 = (float) ((int) (((float) (i - AndroidUtilities.dp(126.0f + (((float) (40 + i3)) * f3)))) - this.nameTextView[i2].getTranslationX()));
                                if (dp2 >= (this.nameTextView[i2].getPaint().measureText(this.nameTextView[i2].getText().toString()) * this.nameTextView[i2].getScaleX()) + ((float) this.nameTextView[i2].getSideDrawablesSize())) {
                                    layoutParams2.width = (int) Math.ceil((double) (dp2 / this.nameTextView[i2].getScaleX()));
                                } else {
                                    layoutParams2.width = -2;
                                }
                                this.nameTextView[i2].setLayoutParams(layoutParams2);
                                layoutParams3 = (FrameLayout.LayoutParams) this.onlineTextView[i2].getLayoutParams();
                                layoutParams3.rightMargin = (int) Math.ceil((double) ((this.onlineTextView[i2].getTranslationX() + ((float) AndroidUtilities.dp(8.0f))) + (((float) AndroidUtilities.dp(40.0f)) * f3)));
                                this.onlineTextView[i2].setLayoutParams(layoutParams3);
                            }
                        }
                        i3 = 48;
                        f3 = 1.0f - dp;
                        layoutParams2 = (FrameLayout.LayoutParams) this.nameTextView[i2].getLayoutParams();
                        dp2 = (float) ((int) (((float) (i - AndroidUtilities.dp(126.0f + (((float) (40 + i3)) * f3)))) - this.nameTextView[i2].getTranslationX()));
                        if (dp2 >= (this.nameTextView[i2].getPaint().measureText(this.nameTextView[i2].getText().toString()) * this.nameTextView[i2].getScaleX()) + ((float) this.nameTextView[i2].getSideDrawablesSize())) {
                            layoutParams2.width = -2;
                        } else {
                            layoutParams2.width = (int) Math.ceil((double) (dp2 / this.nameTextView[i2].getScaleX()));
                        }
                        this.nameTextView[i2].setLayoutParams(layoutParams2);
                        layoutParams3 = (FrameLayout.LayoutParams) this.onlineTextView[i2].getLayoutParams();
                        layoutParams3.rightMargin = (int) Math.ceil((double) ((this.onlineTextView[i2].getTranslationX() + ((float) AndroidUtilities.dp(8.0f))) + (((float) AndroidUtilities.dp(40.0f)) * f3)));
                        this.onlineTextView[i2].setLayoutParams(layoutParams3);
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

    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        fixLayout();
    }

    public void didReceivedNotification(int i, int i2, final Object... objArr) {
        int i3 = 0;
        if (i == NotificationCenter.updateInterfaces) {
            i = ((Integer) objArr[0]).intValue();
            if (this.user_id != 0) {
                if (!((i & 2) == 0 && (i & 1) == 0 && (i & 4) == 0)) {
                    updateProfileData();
                }
                if ((i & 1024) != 0 && this.listView != 0) {
                    Holder holder = (Holder) this.listView.findViewHolderForPosition(this.phoneRow);
                    if (holder != null) {
                        this.listAdapter.onBindViewHolder(holder, this.phoneRow);
                    }
                }
            } else if (this.chat_id != 0) {
                if ((i & MessagesController.UPDATE_MASK_CHAT_ADMINS) != 0) {
                    i2 = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.chat_id));
                    if (i2 != 0) {
                        this.currentChat = i2;
                        createActionBarMenu();
                        updateRowsIds();
                        if (this.listAdapter != 0) {
                            this.listAdapter.notifyDataSetChanged();
                        }
                    }
                }
                i2 = i & MessagesController.UPDATE_MASK_CHANNEL;
                if (!(i2 == 0 && (i & 8) == null && (i & 16) == null && (i & 32) == null && (i & 4) == null)) {
                    updateOnlineCount();
                    updateProfileData();
                }
                if (i2 != 0) {
                    updateRowsIds();
                    if (this.listAdapter != 0) {
                        this.listAdapter.notifyDataSetChanged();
                    }
                }
                if (((i & 2) != 0 || (i & 1) != 0 || (i & 4) != 0) && this.listView != 0) {
                    i2 = this.listView.getChildCount();
                    while (i3 < i2) {
                        objArr = this.listView.getChildAt(i3);
                        if (objArr instanceof UserCell) {
                            ((UserCell) objArr).update(i);
                        }
                        i3++;
                    }
                }
            }
        } else if (i == NotificationCenter.contactsDidLoaded) {
            createActionBarMenu();
        } else if (i == NotificationCenter.mediaCountDidLoaded) {
            i = ((Long) objArr[0]).longValue();
            long j = this.dialog_id;
            if (j == 0) {
                if (this.user_id != 0) {
                    j = (long) this.user_id;
                } else if (this.chat_id != 0) {
                    j = (long) (-this.chat_id);
                }
            }
            if (i == j || i == this.mergeDialogId) {
                if (i == j) {
                    this.totalMediaCount = ((Integer) objArr[1]).intValue();
                } else {
                    this.totalMediaCountMerge = ((Integer) objArr[1]).intValue();
                }
                if (this.listView != 0) {
                    i = this.listView.getChildCount();
                    while (i3 < i) {
                        Holder holder2 = (Holder) this.listView.getChildViewHolder(this.listView.getChildAt(i3));
                        if (holder2.getAdapterPosition() == this.sharedMediaRow) {
                            this.listAdapter.onBindViewHolder(holder2, this.sharedMediaRow);
                            return;
                        }
                        i3++;
                    }
                }
            }
        } else if (i == NotificationCenter.encryptedChatCreated) {
            if (this.creatingChat != 0) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        NotificationCenter.getInstance(ProfileActivity.this.currentAccount).removeObserver(ProfileActivity.this, NotificationCenter.closeChats);
                        NotificationCenter.getInstance(ProfileActivity.this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                        EncryptedChat encryptedChat = (EncryptedChat) objArr[0];
                        Bundle bundle = new Bundle();
                        bundle.putInt("enc_id", encryptedChat.id);
                        ProfileActivity.this.presentFragment(new ChatActivity(bundle), true);
                    }
                });
            }
        } else if (i == NotificationCenter.encryptedChatUpdated) {
            EncryptedChat encryptedChat = (EncryptedChat) objArr[0];
            if (this.currentEncryptedChat != 0 && encryptedChat.id == this.currentEncryptedChat.id) {
                this.currentEncryptedChat = encryptedChat;
                updateRowsIds();
                if (this.listAdapter != 0) {
                    this.listAdapter.notifyDataSetChanged();
                }
            }
        } else if (i == NotificationCenter.blockedUsersDidLoaded) {
            i = this.userBlocked;
            this.userBlocked = MessagesController.getInstance(this.currentAccount).blockedUsers.contains(Integer.valueOf(this.user_id));
            if (i != this.userBlocked) {
                createActionBarMenu();
            }
        } else if (i == NotificationCenter.chatInfoDidLoaded) {
            ChatFull chatFull = (ChatFull) objArr[0];
            if (chatFull.id == this.chat_id) {
                i2 = ((Boolean) objArr[2]).booleanValue();
                if (!((this.info instanceof TL_channelFull) == null || chatFull.participants != null || this.info == null)) {
                    chatFull.participants = this.info.participants;
                }
                if (this.info == null && (chatFull instanceof TL_channelFull) != null) {
                    i3 = 1;
                }
                this.info = chatFull;
                if (this.mergeDialogId == 0 && this.info.migrated_from_chat_id != 0) {
                    this.mergeDialogId = (long) (-this.info.migrated_from_chat_id);
                    DataQuery.getInstance(this.currentAccount).getMediaCount(this.mergeDialogId, 0, this.classGuid, true);
                }
                fetchUsersFromChannelInfo();
                updateOnlineCount();
                updateRowsIds();
                if (this.listAdapter != 0) {
                    this.listAdapter.notifyDataSetChanged();
                }
                i = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.chat_id));
                if (i != 0) {
                    this.currentChat = i;
                    createActionBarMenu();
                }
                if (this.currentChat.megagroup == 0) {
                    return;
                }
                if (i3 != 0 || i2 == 0) {
                    getChannelParticipants(true);
                }
            }
        } else if (i == NotificationCenter.closeChats) {
            removeSelfFromStack();
        } else if (i == NotificationCenter.botInfoDidLoaded) {
            BotInfo botInfo = (BotInfo) objArr[0];
            if (botInfo.user_id == this.user_id) {
                this.botInfo = botInfo;
                updateRowsIds();
                if (this.listAdapter != 0) {
                    this.listAdapter.notifyDataSetChanged();
                }
            }
        } else if (i == NotificationCenter.userInfoDidLoaded) {
            if (((Integer) objArr[0]).intValue() == this.user_id) {
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
        } else if (i == NotificationCenter.didReceivedNewMessages && ((Long) objArr[0]).longValue() == this.dialog_id) {
            ArrayList arrayList = (ArrayList) objArr[1];
            while (i3 < arrayList.size()) {
                MessageObject messageObject = (MessageObject) arrayList.get(i3);
                if (!(this.currentEncryptedChat == null || messageObject.messageOwner.action == null || (messageObject.messageOwner.action instanceof TL_messageEncryptedAction) == null || (messageObject.messageOwner.action.encryptedAction instanceof TL_decryptedMessageActionSetMessageTTL) == null)) {
                    TL_decryptedMessageActionSetMessageTTL tL_decryptedMessageActionSetMessageTTL = (TL_decryptedMessageActionSetMessageTTL) messageObject.messageOwner.action.encryptedAction;
                    if (this.listAdapter != 0) {
                        this.listAdapter.notifyDataSetChanged();
                    }
                }
                i3++;
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

    public void setPlayProfileAnimation(boolean z) {
        SharedPreferences globalMainSettings = MessagesController.getGlobalMainSettings();
        if (!AndroidUtilities.isTablet() && globalMainSettings.getBoolean("view_animations", true)) {
            this.playProfileAnimation = z;
        }
    }

    protected void onTransitionAnimationStart(boolean z, boolean z2) {
        if (!z2 && this.playProfileAnimation && this.allowProfileAnimation) {
            this.openAnimationInProgress = true;
        }
        NotificationCenter.getInstance(this.currentAccount).setAllowedNotificationsDutingAnimation(new int[]{NotificationCenter.dialogsNeedReload, NotificationCenter.closeChats, NotificationCenter.mediaCountDidLoaded});
        NotificationCenter.getInstance(this.currentAccount).setAnimationInProgress(true);
    }

    protected void onTransitionAnimationEnd(boolean z, boolean z2) {
        if (!z2 && this.playProfileAnimation && this.allowProfileAnimation) {
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
    public void setAnimationProgress(float f) {
        int i;
        int color;
        int red;
        int green;
        int green2;
        int blue;
        int red2;
        int green3;
        int blue2;
        int i2;
        int i3;
        int i4;
        float f2 = f;
        this.animationProgress = f2;
        this.listView.setAlpha(f2);
        this.listView.setTranslationX(((float) AndroidUtilities.dp(48.0f)) - (((float) AndroidUtilities.dp(48.0f)) * f2));
        if (this.user_id == 0) {
            if (!ChatObject.isChannel(r0.chat_id, r0.currentAccount) || r0.currentChat.megagroup) {
                i = r0.chat_id;
                i = AvatarDrawable.getProfileBackColorForId(i);
                color = Theme.getColor(Theme.key_actionBarDefault);
                red = Color.red(color);
                green = Color.green(color);
                color = Color.blue(color);
                r0.topView.setBackgroundColor(Color.rgb(red + ((int) (((float) (Color.red(i) - red)) * f2)), green + ((int) (((float) (Color.green(i) - green)) * f2)), color + ((int) (((float) (Color.blue(i) - color)) * f2))));
                if (r0.user_id == 0) {
                    if (ChatObject.isChannel(r0.chat_id, r0.currentAccount) || r0.currentChat.megagroup) {
                        i = r0.chat_id;
                        i = AvatarDrawable.getIconColorForId(i);
                        color = Theme.getColor(Theme.key_actionBarDefaultIcon);
                        red = Color.red(color);
                        green = Color.green(color);
                        color = Color.blue(color);
                        r0.actionBar.setItemsColor(Color.rgb(red + ((int) (((float) (Color.red(i) - red)) * f2)), green + ((int) (((float) (Color.green(i) - green)) * f2)), color + ((int) (((float) (Color.blue(i) - color)) * f2))), false);
                        i = Theme.getColor(Theme.key_profile_title);
                        red = Theme.getColor(Theme.key_actionBarDefaultTitle);
                        green = Color.red(red);
                        green2 = Color.green(red);
                        blue = Color.blue(red);
                        red = Color.alpha(red);
                        red2 = (int) (((float) (Color.red(i) - green)) * f2);
                        green3 = (int) (((float) (Color.green(i) - green2)) * f2);
                        blue2 = (int) (((float) (Color.blue(i) - blue)) * f2);
                        i = (int) (((float) (Color.alpha(i) - red)) * f2);
                        i2 = 0;
                        while (true) {
                            i3 = 2;
                            if (i2 >= 2) {
                                break;
                            }
                            if (r0.nameTextView[i2] != null) {
                                r0.nameTextView[i2].setTextColor(Color.argb(red + i, green + red2, green2 + green3, blue + blue2));
                            }
                            i2++;
                        }
                        if (r0.user_id == 0) {
                            if (ChatObject.isChannel(r0.chat_id, r0.currentAccount) || r0.currentChat.megagroup) {
                                i4 = r0.chat_id;
                                i = AvatarDrawable.getProfileTextColorForId(i4);
                                i4 = Theme.getColor(Theme.key_actionBarDefaultSubtitle);
                                color = Color.red(i4);
                                red = Color.green(i4);
                                green = Color.blue(i4);
                                i4 = Color.alpha(i4);
                                green2 = (int) (((float) (Color.red(i) - color)) * f2);
                                blue = (int) (((float) (Color.green(i) - red)) * f2);
                                red2 = (int) (((float) (Color.blue(i) - green)) * f2);
                                i = (int) (((float) (Color.alpha(i) - i4)) * f2);
                                green3 = 0;
                                while (green3 < i3) {
                                    if (r0.onlineTextView[green3] != null) {
                                        r0.onlineTextView[green3].setTextColor(Color.argb(i4 + i, color + green2, red + blue, green + red2));
                                    }
                                    green3++;
                                    i3 = 2;
                                }
                                r0.extraHeight = (int) (((float) r0.initialAnimationExtraHeight) * f2);
                                i = AvatarDrawable.getProfileColorForId(r0.user_id == 0 ? r0.user_id : r0.chat_id);
                                i4 = AvatarDrawable.getColorForId(r0.user_id == 0 ? r0.user_id : r0.chat_id);
                                if (i != i4) {
                                    r0.avatarDrawable.setColor(Color.rgb(Color.red(i4) + ((int) (((float) (Color.red(i) - Color.red(i4))) * f2)), Color.green(i4) + ((int) (((float) (Color.green(i) - Color.green(i4))) * f2)), Color.blue(i4) + ((int) (((float) (Color.blue(i) - Color.blue(i4))) * f2))));
                                    r0.avatarImage.invalidate();
                                }
                                needLayout();
                            }
                        }
                        i4 = 5;
                        i = AvatarDrawable.getProfileTextColorForId(i4);
                        i4 = Theme.getColor(Theme.key_actionBarDefaultSubtitle);
                        color = Color.red(i4);
                        red = Color.green(i4);
                        green = Color.blue(i4);
                        i4 = Color.alpha(i4);
                        green2 = (int) (((float) (Color.red(i) - color)) * f2);
                        blue = (int) (((float) (Color.green(i) - red)) * f2);
                        red2 = (int) (((float) (Color.blue(i) - green)) * f2);
                        i = (int) (((float) (Color.alpha(i) - i4)) * f2);
                        green3 = 0;
                        while (green3 < i3) {
                            if (r0.onlineTextView[green3] != null) {
                                r0.onlineTextView[green3].setTextColor(Color.argb(i4 + i, color + green2, red + blue, green + red2));
                            }
                            green3++;
                            i3 = 2;
                        }
                        r0.extraHeight = (int) (((float) r0.initialAnimationExtraHeight) * f2);
                        if (r0.user_id == 0) {
                        }
                        i = AvatarDrawable.getProfileColorForId(r0.user_id == 0 ? r0.user_id : r0.chat_id);
                        if (r0.user_id == 0) {
                        }
                        i4 = AvatarDrawable.getColorForId(r0.user_id == 0 ? r0.user_id : r0.chat_id);
                        if (i != i4) {
                            r0.avatarDrawable.setColor(Color.rgb(Color.red(i4) + ((int) (((float) (Color.red(i) - Color.red(i4))) * f2)), Color.green(i4) + ((int) (((float) (Color.green(i) - Color.green(i4))) * f2)), Color.blue(i4) + ((int) (((float) (Color.blue(i) - Color.blue(i4))) * f2))));
                            r0.avatarImage.invalidate();
                        }
                        needLayout();
                    }
                }
                i = 5;
                i = AvatarDrawable.getIconColorForId(i);
                color = Theme.getColor(Theme.key_actionBarDefaultIcon);
                red = Color.red(color);
                green = Color.green(color);
                color = Color.blue(color);
                r0.actionBar.setItemsColor(Color.rgb(red + ((int) (((float) (Color.red(i) - red)) * f2)), green + ((int) (((float) (Color.green(i) - green)) * f2)), color + ((int) (((float) (Color.blue(i) - color)) * f2))), false);
                i = Theme.getColor(Theme.key_profile_title);
                red = Theme.getColor(Theme.key_actionBarDefaultTitle);
                green = Color.red(red);
                green2 = Color.green(red);
                blue = Color.blue(red);
                red = Color.alpha(red);
                red2 = (int) (((float) (Color.red(i) - green)) * f2);
                green3 = (int) (((float) (Color.green(i) - green2)) * f2);
                blue2 = (int) (((float) (Color.blue(i) - blue)) * f2);
                i = (int) (((float) (Color.alpha(i) - red)) * f2);
                i2 = 0;
                while (true) {
                    i3 = 2;
                    if (i2 >= 2) {
                        break;
                    }
                    if (r0.nameTextView[i2] != null) {
                        r0.nameTextView[i2].setTextColor(Color.argb(red + i, green + red2, green2 + green3, blue + blue2));
                    }
                    i2++;
                }
                if (r0.user_id == 0) {
                    if (ChatObject.isChannel(r0.chat_id, r0.currentAccount)) {
                    }
                    i4 = r0.chat_id;
                    i = AvatarDrawable.getProfileTextColorForId(i4);
                    i4 = Theme.getColor(Theme.key_actionBarDefaultSubtitle);
                    color = Color.red(i4);
                    red = Color.green(i4);
                    green = Color.blue(i4);
                    i4 = Color.alpha(i4);
                    green2 = (int) (((float) (Color.red(i) - color)) * f2);
                    blue = (int) (((float) (Color.green(i) - red)) * f2);
                    red2 = (int) (((float) (Color.blue(i) - green)) * f2);
                    i = (int) (((float) (Color.alpha(i) - i4)) * f2);
                    green3 = 0;
                    while (green3 < i3) {
                        if (r0.onlineTextView[green3] != null) {
                            r0.onlineTextView[green3].setTextColor(Color.argb(i4 + i, color + green2, red + blue, green + red2));
                        }
                        green3++;
                        i3 = 2;
                    }
                    r0.extraHeight = (int) (((float) r0.initialAnimationExtraHeight) * f2);
                    if (r0.user_id == 0) {
                    }
                    i = AvatarDrawable.getProfileColorForId(r0.user_id == 0 ? r0.user_id : r0.chat_id);
                    if (r0.user_id == 0) {
                    }
                    i4 = AvatarDrawable.getColorForId(r0.user_id == 0 ? r0.user_id : r0.chat_id);
                    if (i != i4) {
                        r0.avatarDrawable.setColor(Color.rgb(Color.red(i4) + ((int) (((float) (Color.red(i) - Color.red(i4))) * f2)), Color.green(i4) + ((int) (((float) (Color.green(i) - Color.green(i4))) * f2)), Color.blue(i4) + ((int) (((float) (Color.blue(i) - Color.blue(i4))) * f2))));
                        r0.avatarImage.invalidate();
                    }
                    needLayout();
                }
                i4 = 5;
                i = AvatarDrawable.getProfileTextColorForId(i4);
                i4 = Theme.getColor(Theme.key_actionBarDefaultSubtitle);
                color = Color.red(i4);
                red = Color.green(i4);
                green = Color.blue(i4);
                i4 = Color.alpha(i4);
                green2 = (int) (((float) (Color.red(i) - color)) * f2);
                blue = (int) (((float) (Color.green(i) - red)) * f2);
                red2 = (int) (((float) (Color.blue(i) - green)) * f2);
                i = (int) (((float) (Color.alpha(i) - i4)) * f2);
                green3 = 0;
                while (green3 < i3) {
                    if (r0.onlineTextView[green3] != null) {
                        r0.onlineTextView[green3].setTextColor(Color.argb(i4 + i, color + green2, red + blue, green + red2));
                    }
                    green3++;
                    i3 = 2;
                }
                r0.extraHeight = (int) (((float) r0.initialAnimationExtraHeight) * f2);
                if (r0.user_id == 0) {
                }
                i = AvatarDrawable.getProfileColorForId(r0.user_id == 0 ? r0.user_id : r0.chat_id);
                if (r0.user_id == 0) {
                }
                i4 = AvatarDrawable.getColorForId(r0.user_id == 0 ? r0.user_id : r0.chat_id);
                if (i != i4) {
                    r0.avatarDrawable.setColor(Color.rgb(Color.red(i4) + ((int) (((float) (Color.red(i) - Color.red(i4))) * f2)), Color.green(i4) + ((int) (((float) (Color.green(i) - Color.green(i4))) * f2)), Color.blue(i4) + ((int) (((float) (Color.blue(i) - Color.blue(i4))) * f2))));
                    r0.avatarImage.invalidate();
                }
                needLayout();
            }
        }
        i = 5;
        i = AvatarDrawable.getProfileBackColorForId(i);
        color = Theme.getColor(Theme.key_actionBarDefault);
        red = Color.red(color);
        green = Color.green(color);
        color = Color.blue(color);
        r0.topView.setBackgroundColor(Color.rgb(red + ((int) (((float) (Color.red(i) - red)) * f2)), green + ((int) (((float) (Color.green(i) - green)) * f2)), color + ((int) (((float) (Color.blue(i) - color)) * f2))));
        if (r0.user_id == 0) {
            if (ChatObject.isChannel(r0.chat_id, r0.currentAccount)) {
            }
            i = r0.chat_id;
            i = AvatarDrawable.getIconColorForId(i);
            color = Theme.getColor(Theme.key_actionBarDefaultIcon);
            red = Color.red(color);
            green = Color.green(color);
            color = Color.blue(color);
            r0.actionBar.setItemsColor(Color.rgb(red + ((int) (((float) (Color.red(i) - red)) * f2)), green + ((int) (((float) (Color.green(i) - green)) * f2)), color + ((int) (((float) (Color.blue(i) - color)) * f2))), false);
            i = Theme.getColor(Theme.key_profile_title);
            red = Theme.getColor(Theme.key_actionBarDefaultTitle);
            green = Color.red(red);
            green2 = Color.green(red);
            blue = Color.blue(red);
            red = Color.alpha(red);
            red2 = (int) (((float) (Color.red(i) - green)) * f2);
            green3 = (int) (((float) (Color.green(i) - green2)) * f2);
            blue2 = (int) (((float) (Color.blue(i) - blue)) * f2);
            i = (int) (((float) (Color.alpha(i) - red)) * f2);
            i2 = 0;
            while (true) {
                i3 = 2;
                if (i2 >= 2) {
                    break;
                }
                if (r0.nameTextView[i2] != null) {
                    r0.nameTextView[i2].setTextColor(Color.argb(red + i, green + red2, green2 + green3, blue + blue2));
                }
                i2++;
            }
            if (r0.user_id == 0) {
                if (ChatObject.isChannel(r0.chat_id, r0.currentAccount)) {
                }
                i4 = r0.chat_id;
                i = AvatarDrawable.getProfileTextColorForId(i4);
                i4 = Theme.getColor(Theme.key_actionBarDefaultSubtitle);
                color = Color.red(i4);
                red = Color.green(i4);
                green = Color.blue(i4);
                i4 = Color.alpha(i4);
                green2 = (int) (((float) (Color.red(i) - color)) * f2);
                blue = (int) (((float) (Color.green(i) - red)) * f2);
                red2 = (int) (((float) (Color.blue(i) - green)) * f2);
                i = (int) (((float) (Color.alpha(i) - i4)) * f2);
                green3 = 0;
                while (green3 < i3) {
                    if (r0.onlineTextView[green3] != null) {
                        r0.onlineTextView[green3].setTextColor(Color.argb(i4 + i, color + green2, red + blue, green + red2));
                    }
                    green3++;
                    i3 = 2;
                }
                r0.extraHeight = (int) (((float) r0.initialAnimationExtraHeight) * f2);
                if (r0.user_id == 0) {
                }
                i = AvatarDrawable.getProfileColorForId(r0.user_id == 0 ? r0.user_id : r0.chat_id);
                if (r0.user_id == 0) {
                }
                i4 = AvatarDrawable.getColorForId(r0.user_id == 0 ? r0.user_id : r0.chat_id);
                if (i != i4) {
                    r0.avatarDrawable.setColor(Color.rgb(Color.red(i4) + ((int) (((float) (Color.red(i) - Color.red(i4))) * f2)), Color.green(i4) + ((int) (((float) (Color.green(i) - Color.green(i4))) * f2)), Color.blue(i4) + ((int) (((float) (Color.blue(i) - Color.blue(i4))) * f2))));
                    r0.avatarImage.invalidate();
                }
                needLayout();
            }
            i4 = 5;
            i = AvatarDrawable.getProfileTextColorForId(i4);
            i4 = Theme.getColor(Theme.key_actionBarDefaultSubtitle);
            color = Color.red(i4);
            red = Color.green(i4);
            green = Color.blue(i4);
            i4 = Color.alpha(i4);
            green2 = (int) (((float) (Color.red(i) - color)) * f2);
            blue = (int) (((float) (Color.green(i) - red)) * f2);
            red2 = (int) (((float) (Color.blue(i) - green)) * f2);
            i = (int) (((float) (Color.alpha(i) - i4)) * f2);
            green3 = 0;
            while (green3 < i3) {
                if (r0.onlineTextView[green3] != null) {
                    r0.onlineTextView[green3].setTextColor(Color.argb(i4 + i, color + green2, red + blue, green + red2));
                }
                green3++;
                i3 = 2;
            }
            r0.extraHeight = (int) (((float) r0.initialAnimationExtraHeight) * f2);
            if (r0.user_id == 0) {
            }
            i = AvatarDrawable.getProfileColorForId(r0.user_id == 0 ? r0.user_id : r0.chat_id);
            if (r0.user_id == 0) {
            }
            i4 = AvatarDrawable.getColorForId(r0.user_id == 0 ? r0.user_id : r0.chat_id);
            if (i != i4) {
                r0.avatarDrawable.setColor(Color.rgb(Color.red(i4) + ((int) (((float) (Color.red(i) - Color.red(i4))) * f2)), Color.green(i4) + ((int) (((float) (Color.green(i) - Color.green(i4))) * f2)), Color.blue(i4) + ((int) (((float) (Color.blue(i) - Color.blue(i4))) * f2))));
                r0.avatarImage.invalidate();
            }
            needLayout();
        }
        i = 5;
        i = AvatarDrawable.getIconColorForId(i);
        color = Theme.getColor(Theme.key_actionBarDefaultIcon);
        red = Color.red(color);
        green = Color.green(color);
        color = Color.blue(color);
        r0.actionBar.setItemsColor(Color.rgb(red + ((int) (((float) (Color.red(i) - red)) * f2)), green + ((int) (((float) (Color.green(i) - green)) * f2)), color + ((int) (((float) (Color.blue(i) - color)) * f2))), false);
        i = Theme.getColor(Theme.key_profile_title);
        red = Theme.getColor(Theme.key_actionBarDefaultTitle);
        green = Color.red(red);
        green2 = Color.green(red);
        blue = Color.blue(red);
        red = Color.alpha(red);
        red2 = (int) (((float) (Color.red(i) - green)) * f2);
        green3 = (int) (((float) (Color.green(i) - green2)) * f2);
        blue2 = (int) (((float) (Color.blue(i) - blue)) * f2);
        i = (int) (((float) (Color.alpha(i) - red)) * f2);
        i2 = 0;
        while (true) {
            i3 = 2;
            if (i2 >= 2) {
                break;
            }
            if (r0.nameTextView[i2] != null) {
                r0.nameTextView[i2].setTextColor(Color.argb(red + i, green + red2, green2 + green3, blue + blue2));
            }
            i2++;
        }
        if (r0.user_id == 0) {
            if (ChatObject.isChannel(r0.chat_id, r0.currentAccount)) {
            }
            i4 = r0.chat_id;
            i = AvatarDrawable.getProfileTextColorForId(i4);
            i4 = Theme.getColor(Theme.key_actionBarDefaultSubtitle);
            color = Color.red(i4);
            red = Color.green(i4);
            green = Color.blue(i4);
            i4 = Color.alpha(i4);
            green2 = (int) (((float) (Color.red(i) - color)) * f2);
            blue = (int) (((float) (Color.green(i) - red)) * f2);
            red2 = (int) (((float) (Color.blue(i) - green)) * f2);
            i = (int) (((float) (Color.alpha(i) - i4)) * f2);
            green3 = 0;
            while (green3 < i3) {
                if (r0.onlineTextView[green3] != null) {
                    r0.onlineTextView[green3].setTextColor(Color.argb(i4 + i, color + green2, red + blue, green + red2));
                }
                green3++;
                i3 = 2;
            }
            r0.extraHeight = (int) (((float) r0.initialAnimationExtraHeight) * f2);
            if (r0.user_id == 0) {
            }
            i = AvatarDrawable.getProfileColorForId(r0.user_id == 0 ? r0.user_id : r0.chat_id);
            if (r0.user_id == 0) {
            }
            i4 = AvatarDrawable.getColorForId(r0.user_id == 0 ? r0.user_id : r0.chat_id);
            if (i != i4) {
                r0.avatarDrawable.setColor(Color.rgb(Color.red(i4) + ((int) (((float) (Color.red(i) - Color.red(i4))) * f2)), Color.green(i4) + ((int) (((float) (Color.green(i) - Color.green(i4))) * f2)), Color.blue(i4) + ((int) (((float) (Color.blue(i) - Color.blue(i4))) * f2))));
                r0.avatarImage.invalidate();
            }
            needLayout();
        }
        i4 = 5;
        i = AvatarDrawable.getProfileTextColorForId(i4);
        i4 = Theme.getColor(Theme.key_actionBarDefaultSubtitle);
        color = Color.red(i4);
        red = Color.green(i4);
        green = Color.blue(i4);
        i4 = Color.alpha(i4);
        green2 = (int) (((float) (Color.red(i) - color)) * f2);
        blue = (int) (((float) (Color.green(i) - red)) * f2);
        red2 = (int) (((float) (Color.blue(i) - green)) * f2);
        i = (int) (((float) (Color.alpha(i) - i4)) * f2);
        green3 = 0;
        while (green3 < i3) {
            if (r0.onlineTextView[green3] != null) {
                r0.onlineTextView[green3].setTextColor(Color.argb(i4 + i, color + green2, red + blue, green + red2));
            }
            green3++;
            i3 = 2;
        }
        r0.extraHeight = (int) (((float) r0.initialAnimationExtraHeight) * f2);
        if (r0.user_id == 0) {
        }
        i = AvatarDrawable.getProfileColorForId(r0.user_id == 0 ? r0.user_id : r0.chat_id);
        if (r0.user_id == 0) {
        }
        i4 = AvatarDrawable.getColorForId(r0.user_id == 0 ? r0.user_id : r0.chat_id);
        if (i != i4) {
            r0.avatarDrawable.setColor(Color.rgb(Color.red(i4) + ((int) (((float) (Color.red(i) - Color.red(i4))) * f2)), Color.green(i4) + ((int) (((float) (Color.green(i) - Color.green(i4))) * f2)), Color.blue(i4) + ((int) (((float) (Color.blue(i) - Color.blue(i4))) * f2))));
            r0.avatarImage.invalidate();
        }
        needLayout();
    }

    protected AnimatorSet onCustomTransitionAnimation(boolean z, final Runnable runnable) {
        if (!this.playProfileAnimation || !this.allowProfileAnimation) {
            return null;
        }
        final AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(180);
        this.listView.setLayerType(2, null);
        ActionBarMenu createMenu = this.actionBar.createMenu();
        if (createMenu.getItem(10) == null && this.animatingItem == null) {
            this.animatingItem = createMenu.addItem(10, (int) C0446R.drawable.ic_ab_other);
        }
        int i;
        Object obj;
        String str;
        float[] fArr;
        if (z) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.onlineTextView[1].getLayoutParams();
            layoutParams.rightMargin = (int) ((-21.0f * AndroidUtilities.density) + ((float) AndroidUtilities.dp(8.0f)));
            this.onlineTextView[1].setLayoutParams(layoutParams);
            FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) this.nameTextView[1].getLayoutParams();
            z = (float) ((int) Math.ceil((double) (((float) (AndroidUtilities.displaySize.x - AndroidUtilities.dp(126.0f))) + (21.0f * AndroidUtilities.density))));
            if (z < (this.nameTextView[1].getPaint().measureText(this.nameTextView[1].getText().toString()) * 1.12f) + ((float) this.nameTextView[1].getSideDrawablesSize())) {
                layoutParams2.width = (int) Math.ceil((double) (z / true));
            } else {
                layoutParams2.width = true;
            }
            this.nameTextView[1].setLayoutParams(layoutParams2);
            this.initialAnimationExtraHeight = AndroidUtilities.dp(true);
            this.fragmentView.setBackgroundColor(0);
            setAnimationProgress(0.0f);
            z = new ArrayList();
            z.add(ObjectAnimator.ofFloat(this, "animationProgress", new float[]{0.0f, 1.0f}));
            if (this.writeButton != null) {
                this.writeButton.setScaleX(0.2f);
                this.writeButton.setScaleY(0.2f);
                this.writeButton.setAlpha(0.0f);
                z.add(ObjectAnimator.ofFloat(this.writeButton, "scaleX", new float[]{1.0f}));
                z.add(ObjectAnimator.ofFloat(this.writeButton, "scaleY", new float[]{1.0f}));
                z.add(ObjectAnimator.ofFloat(this.writeButton, "alpha", new float[]{1.0f}));
            }
            i = 0;
            while (i < 2) {
                this.onlineTextView[i].setAlpha(i == 0 ? 1.0f : 0.0f);
                this.nameTextView[i].setAlpha(i == 0 ? 1.0f : 0.0f);
                obj = this.onlineTextView[i];
                str = "alpha";
                fArr = new float[1];
                fArr[0] = i == 0 ? 0.0f : 1.0f;
                z.add(ObjectAnimator.ofFloat(obj, str, fArr));
                obj = this.nameTextView[i];
                str = "alpha";
                fArr = new float[1];
                fArr[0] = i == 0 ? 0.0f : 1.0f;
                z.add(ObjectAnimator.ofFloat(obj, str, fArr));
                i++;
            }
            if (this.animatingItem != null) {
                this.animatingItem.setAlpha(1.0f);
                z.add(ObjectAnimator.ofFloat(this.animatingItem, "alpha", new float[]{0.0f}));
            }
            if (this.callItem != null) {
                this.callItem.setAlpha(0.0f);
                z.add(ObjectAnimator.ofFloat(this.callItem, "alpha", new float[]{1.0f}));
            }
            if (this.editItem != null) {
                this.editItem.setAlpha(0.0f);
                z.add(ObjectAnimator.ofFloat(this.editItem, "alpha", new float[]{1.0f}));
            }
            animatorSet.playTogether(z);
        } else {
            this.initialAnimationExtraHeight = this.extraHeight;
            z = new ArrayList();
            z.add(ObjectAnimator.ofFloat(this, "animationProgress", new float[]{1.0f, 0.0f}));
            if (this.writeButton != null) {
                z.add(ObjectAnimator.ofFloat(this.writeButton, "scaleX", new float[]{0.2f}));
                z.add(ObjectAnimator.ofFloat(this.writeButton, "scaleY", new float[]{0.2f}));
                z.add(ObjectAnimator.ofFloat(this.writeButton, "alpha", new float[]{0.0f}));
            }
            i = 0;
            while (i < 2) {
                obj = this.onlineTextView[i];
                str = "alpha";
                fArr = new float[1];
                fArr[0] = i == 0 ? 1.0f : 0.0f;
                z.add(ObjectAnimator.ofFloat(obj, str, fArr));
                obj = this.nameTextView[i];
                str = "alpha";
                fArr = new float[1];
                fArr[0] = i == 0 ? 1.0f : 0.0f;
                z.add(ObjectAnimator.ofFloat(obj, str, fArr));
                i++;
            }
            if (this.animatingItem != null) {
                this.animatingItem.setAlpha(0.0f);
                z.add(ObjectAnimator.ofFloat(this.animatingItem, "alpha", new float[]{1.0f}));
            }
            if (this.callItem != null) {
                this.callItem.setAlpha(1.0f);
                z.add(ObjectAnimator.ofFloat(this.callItem, "alpha", new float[]{0.0f}));
            }
            if (this.editItem != null) {
                this.editItem.setAlpha(1.0f);
                z.add(ObjectAnimator.ofFloat(this.editItem, "alpha", new float[]{0.0f}));
            }
            animatorSet.playTogether(z);
        }
        animatorSet.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
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

    private void updateOnlineCount() {
        int i = 0;
        this.onlineCount = 0;
        int currentTime = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
        this.sortedUsers.clear();
        if ((this.info instanceof TL_chatFull) || ((this.info instanceof TL_channelFull) && this.info.participants_count <= Callback.DEFAULT_DRAG_ANIMATION_DURATION && this.info.participants != null)) {
            while (i < this.info.participants.participants.size()) {
                User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(((ChatParticipant) this.info.participants.participants.get(i)).user_id));
                if (!(user == null || user.status == null || ((user.status.expires <= currentTime && user.id != UserConfig.getInstance(this.currentAccount).getClientUserId()) || user.status.expires <= 10000))) {
                    this.onlineCount++;
                }
                this.sortedUsers.add(Integer.valueOf(i));
                i++;
            }
            try {
                Collections.sort(this.sortedUsers, new Comparator<Integer>() {
                    public int compare(Integer num, Integer num2) {
                        num2 = MessagesController.getInstance(ProfileActivity.this.currentAccount).getUser(Integer.valueOf(((ChatParticipant) ProfileActivity.this.info.participants.participants.get(num2.intValue())).user_id));
                        num = MessagesController.getInstance(ProfileActivity.this.currentAccount).getUser(Integer.valueOf(((ChatParticipant) ProfileActivity.this.info.participants.participants.get(num.intValue())).user_id));
                        num2 = (num2 == null || num2.status == null) ? null : num2.id == UserConfig.getInstance(ProfileActivity.this.currentAccount).getClientUserId() ? ConnectionsManager.getInstance(ProfileActivity.this.currentAccount).getCurrentTime() + 50000 : num2.status.expires;
                        num = (num == null || num.status == null) ? null : num.id == UserConfig.getInstance(ProfileActivity.this.currentAccount).getClientUserId() ? ConnectionsManager.getInstance(ProfileActivity.this.currentAccount).getCurrentTime() + 50000 : num.status.expires;
                        if (num2 <= null || num <= null) {
                            if (num2 >= null || num >= null) {
                                if ((num2 < null && num > null) || (num2 == null && num != null)) {
                                    return -1;
                                }
                                if ((num >= null || num2 <= null) && (num != null || num2 == null)) {
                                    return 0;
                                }
                                return 1;
                            } else if (num2 > num) {
                                return 1;
                            } else {
                                if (num2 < num) {
                                    return -1;
                                }
                                return 0;
                            }
                        } else if (num2 > num) {
                            return 1;
                        } else {
                            if (num2 < num) {
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

    public void setChatInfo(ChatFull chatFull) {
        this.info = chatFull;
        if (!(this.info == null || this.info.migrated_from_chat_id == null)) {
            this.mergeDialogId = (long) (-this.info.migrated_from_chat_id);
        }
        fetchUsersFromChannelInfo();
    }

    private void fetchUsersFromChannelInfo() {
        if (this.currentChat != null) {
            if (this.currentChat.megagroup) {
                if ((this.info instanceof TL_channelFull) && this.info.participants != null) {
                    for (int i = 0; i < this.info.participants.participants.size(); i++) {
                        ChatParticipant chatParticipant = (ChatParticipant) this.info.participants.participants.get(i);
                        this.participantsMap.put(chatParticipant.user_id, chatParticipant);
                    }
                }
            }
        }
    }

    private void kickUser(int i) {
        if (i != 0) {
            MessagesController.getInstance(this.currentAccount).deleteUserFromChat(this.chat_id, MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(i)), this.info);
            return;
        }
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.closeChats);
        if (AndroidUtilities.isTablet() != 0) {
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
        int i = 0;
        this.rowCount = 0;
        int i2;
        if (this.user_id != 0) {
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
                i = 1;
            }
            if (!(userFull == null || TextUtils.isEmpty(userFull.about))) {
                int i4;
                if (this.phoneRow != -1) {
                    i4 = this.rowCount;
                    this.rowCount = i4 + 1;
                    this.userSectionRow = i4;
                }
                if (i == 0) {
                    if (!this.isBot) {
                        i4 = this.rowCount;
                        this.rowCount = i4 + 1;
                        this.userInfoDetailedRow = i4;
                    }
                }
                i4 = this.rowCount;
                this.rowCount = i4 + 1;
                this.userInfoRow = i4;
            }
            if (i != 0) {
                i = this.rowCount;
                this.rowCount = i + 1;
                this.usernameRow = i;
            }
            if (!(this.phoneRow == -1 && this.userInfoRow == -1 && this.userInfoDetailedRow == -1 && this.usernameRow == -1)) {
                i2 = this.rowCount;
                this.rowCount = i2 + 1;
                this.sectionRow = i2;
            }
            if (this.user_id != UserConfig.getInstance(this.currentAccount).getClientUserId()) {
                i2 = this.rowCount;
                this.rowCount = i2 + 1;
                this.settingsNotificationsRow = i2;
            }
            i2 = this.rowCount;
            this.rowCount = i2 + 1;
            this.sharedMediaRow = i2;
            if (this.currentEncryptedChat instanceof TL_encryptedChat) {
                i2 = this.rowCount;
                this.rowCount = i2 + 1;
                this.settingsTimerRow = i2;
                i2 = this.rowCount;
                this.rowCount = i2 + 1;
                this.settingsKeyRow = i2;
            }
            if (!(userFull == null || userFull.common_chats_count == 0)) {
                i2 = this.rowCount;
                this.rowCount = i2 + 1;
                this.groupsInCommonRow = i2;
            }
            if (user != null && !this.isBot && this.currentEncryptedChat == null && user.id != UserConfig.getInstance(this.currentAccount).getClientUserId()) {
                i2 = this.rowCount;
                this.rowCount = i2 + 1;
                this.startSecretChatRow = i2;
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
                        i2 = this.rowCount;
                        this.rowCount = i2 + 1;
                        this.membersRow = i2;
                    }
                    if (!(this.currentChat.creator || this.currentChat.left || this.currentChat.kicked || this.currentChat.megagroup)) {
                        i2 = this.rowCount;
                        this.rowCount = i2 + 1;
                        this.leaveChannelRow = i2;
                    }
                    if (this.currentChat.megagroup && (((this.currentChat.admin_rights != null && this.currentChat.admin_rights.invite_users) || this.currentChat.creator || this.currentChat.democracy) && (this.info == null || this.info.participants_count < MessagesController.getInstance(this.currentAccount).maxMegagroupCount))) {
                        i2 = this.rowCount;
                        this.rowCount = i2 + 1;
                        this.addMemberRow = i2;
                    }
                    if (this.info != null && this.currentChat.megagroup && this.info.participants != null && !this.info.participants.participants.isEmpty()) {
                        i2 = this.rowCount;
                        this.rowCount = i2 + 1;
                        this.emptyRowChat = i2;
                        i2 = this.rowCount;
                        this.rowCount = i2 + 1;
                        this.membersSectionRow = i2;
                        i2 = this.rowCount;
                        this.rowCount = i2 + 1;
                        this.emptyRowChat2 = i2;
                        this.rowCount += this.info.participants.participants.size();
                        this.membersEndRow = this.rowCount;
                        if (!this.usersEndReached) {
                            i2 = this.rowCount;
                            this.rowCount = i2 + 1;
                            this.loadMoreMembersRow = i2;
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
                    i2 = this.rowCount;
                    this.rowCount = i2 + 1;
                    this.convertHelpRow = i2;
                } else {
                    i2 = this.rowCount;
                    this.rowCount = i2 + 1;
                    this.membersSectionRow = i2;
                }
                if (this.info != null && !(this.info.participants instanceof TL_chatParticipantsForbidden)) {
                    i2 = this.rowCount;
                    this.rowCount = i2 + 1;
                    this.emptyRowChat2 = i2;
                    this.rowCount += this.info.participants.participants.size();
                    this.membersEndRow = this.rowCount;
                }
            } else if (!ChatObject.isChannel(this.currentChat) && this.info != null && !(this.info.participants instanceof TL_chatParticipantsForbidden)) {
                i2 = this.rowCount;
                this.rowCount = i2 + 1;
                this.addMemberRow = i2;
                i2 = this.rowCount;
                this.rowCount = i2 + 1;
                this.emptyRowChat2 = i2;
                this.rowCount += this.info.participants.participants.size();
                this.membersEndRow = this.rowCount;
            }
        }
    }

    private void updateProfileData() {
        if (this.avatarImage != null) {
            if (r0.nameTextView != null) {
                int connectionState = ConnectionsManager.getInstance(r0.currentAccount).getConnectionState();
                TLObject tLObject = null;
                CharSequence string = connectionState == 2 ? LocaleController.getString("WaitingForNetwork", C0446R.string.WaitingForNetwork) : connectionState == 1 ? LocaleController.getString("Connecting", C0446R.string.Connecting) : connectionState == 5 ? LocaleController.getString("Updating", C0446R.string.Updating) : connectionState == 4 ? LocaleController.getString("ConnectingToProxy", C0446R.string.ConnectingToProxy) : null;
                CharSequence userName;
                CharSequence string2;
                CharSequence format;
                if (r0.user_id != 0) {
                    TLObject tLObject2;
                    FileLocation fileLocation;
                    User user = MessagesController.getInstance(r0.currentAccount).getUser(Integer.valueOf(r0.user_id));
                    if (user.photo != null) {
                        tLObject2 = user.photo.photo_small;
                        fileLocation = user.photo.photo_big;
                    } else {
                        tLObject2 = null;
                        fileLocation = tLObject2;
                    }
                    r0.avatarDrawable.setInfo(user);
                    r0.avatarImage.setImage(tLObject2, "50_50", r0.avatarDrawable);
                    userName = UserObject.getUserName(user);
                    if (user.id == UserConfig.getInstance(r0.currentAccount).getClientUserId()) {
                        string2 = LocaleController.getString("ChatYourSelf", C0446R.string.ChatYourSelf);
                        userName = LocaleController.getString("ChatYourSelfName", C0446R.string.ChatYourSelfName);
                    } else {
                        if (user.id != 333000) {
                            if (user.id != 777000) {
                                if (r0.isBot) {
                                    string2 = LocaleController.getString("Bot", C0446R.string.Bot);
                                } else {
                                    string2 = LocaleController.formatUserStatus(r0.currentAccount, user);
                                }
                            }
                        }
                        string2 = LocaleController.getString("ServiceNotifications", C0446R.string.ServiceNotifications);
                    }
                    for (int i = 0; i < 2; i++) {
                        if (r0.nameTextView[i] != null) {
                            Drawable drawable;
                            if (i == 0 && user.id != UserConfig.getInstance(r0.currentAccount).getClientUserId() && user.id / 1000 != 777 && user.id / 1000 != 333 && user.phone != null && user.phone.length() != 0 && ContactsController.getInstance(r0.currentAccount).contactsDict.get(Integer.valueOf(user.id)) == null && (ContactsController.getInstance(r0.currentAccount).contactsDict.size() != 0 || !ContactsController.getInstance(r0.currentAccount).isLoadingContacts())) {
                                PhoneFormat instance = PhoneFormat.getInstance();
                                StringBuilder stringBuilder = new StringBuilder();
                                stringBuilder.append("+");
                                stringBuilder.append(user.phone);
                                format = instance.format(stringBuilder.toString());
                                if (!r0.nameTextView[i].getText().equals(format)) {
                                    r0.nameTextView[i].setText(format);
                                }
                            } else if (!r0.nameTextView[i].getText().equals(userName)) {
                                r0.nameTextView[i].setText(userName);
                            }
                            if (i == 0 && string != null) {
                                r0.onlineTextView[i].setText(string);
                            } else if (!r0.onlineTextView[i].getText().equals(string2)) {
                                r0.onlineTextView[i].setText(string2);
                            }
                            Drawable drawable2 = r0.currentEncryptedChat != null ? Theme.chat_lockIconDrawable : null;
                            if (i == 0) {
                                if (MessagesController.getInstance(r0.currentAccount).isDialogMuted(r0.dialog_id != 0 ? r0.dialog_id : (long) r0.user_id)) {
                                    drawable = Theme.chat_muteIconDrawable;
                                    r0.nameTextView[i].setLeftDrawable(drawable2);
                                    r0.nameTextView[i].setRightDrawable(drawable);
                                }
                            } else if (user.verified) {
                                drawable = new CombinedDrawable(Theme.profile_verifiedDrawable, Theme.profile_verifiedCheckDrawable);
                                r0.nameTextView[i].setLeftDrawable(drawable2);
                                r0.nameTextView[i].setRightDrawable(drawable);
                            }
                            drawable = null;
                            r0.nameTextView[i].setLeftDrawable(drawable2);
                            r0.nameTextView[i].setRightDrawable(drawable);
                        }
                    }
                    r0.avatarImage.getImageReceiver().setVisible(PhotoViewer.isShowingImage(fileLocation) ^ true, false);
                } else if (r0.chat_id != 0) {
                    FileLocation fileLocation2;
                    Chat chat = MessagesController.getInstance(r0.currentAccount).getChat(Integer.valueOf(r0.chat_id));
                    if (chat != null) {
                        r0.currentChat = chat;
                    } else {
                        chat = r0.currentChat;
                    }
                    if (ChatObject.isChannel(chat)) {
                        if (r0.info != null) {
                            if (!r0.currentChat.megagroup) {
                                if (!(r0.info.participants_count == 0 || r0.currentChat.admin)) {
                                    if (r0.info.can_view_participants) {
                                    }
                                }
                            }
                            if (!r0.currentChat.megagroup || r0.info.participants_count > Callback.DEFAULT_DRAG_ANIMATION_DURATION) {
                                int[] iArr = new int[1];
                                string2 = LocaleController.formatShortNumber(r0.info.participants_count, iArr);
                                if (r0.currentChat.megagroup) {
                                    userName = LocaleController.formatPluralString("Members", iArr[0]).replace(String.format("%d", new Object[]{Integer.valueOf(iArr[0])}), string2);
                                } else {
                                    userName = LocaleController.formatPluralString("Subscribers", iArr[0]).replace(String.format("%d", new Object[]{Integer.valueOf(iArr[0])}), string2);
                                }
                            } else if (r0.onlineCount <= 1 || r0.info.participants_count == 0) {
                                userName = LocaleController.formatPluralString("Members", r0.info.participants_count);
                            } else {
                                userName = String.format("%s, %s", new Object[]{LocaleController.formatPluralString("Members", r0.info.participants_count), LocaleController.formatPluralString("OnlineCount", r0.onlineCount)});
                            }
                        }
                        if (r0.currentChat.megagroup) {
                            userName = LocaleController.getString("Loading", C0446R.string.Loading).toLowerCase();
                        } else if ((chat.flags & 64) != 0) {
                            userName = LocaleController.getString("ChannelPublic", C0446R.string.ChannelPublic).toLowerCase();
                        } else {
                            userName = LocaleController.getString("ChannelPrivate", C0446R.string.ChannelPrivate).toLowerCase();
                        }
                    } else {
                        int i2 = chat.participants_count;
                        if (r0.info != null) {
                            i2 = r0.info.participants.participants.size();
                        }
                        if (i2 == 0 || r0.onlineCount <= 1) {
                            userName = LocaleController.formatPluralString("Members", i2);
                        } else {
                            userName = String.format("%s, %s", new Object[]{LocaleController.formatPluralString("Members", i2), LocaleController.formatPluralString("OnlineCount", r0.onlineCount)});
                        }
                    }
                    int i3 = 0;
                    while (i3 < 2) {
                        if (r0.nameTextView[i3] != null) {
                            if (!(chat.title == null || r0.nameTextView[i3].getText().equals(chat.title))) {
                                r0.nameTextView[i3].setText(chat.title);
                            }
                            r0.nameTextView[i3].setLeftDrawable(null);
                            if (i3 == 0) {
                                r0.nameTextView[i3].setRightDrawable(MessagesController.getInstance(r0.currentAccount).isDialogMuted((long) (-r0.chat_id)) ? Theme.chat_muteIconDrawable : null);
                            } else if (chat.verified) {
                                r0.nameTextView[i3].setRightDrawable(new CombinedDrawable(Theme.profile_verifiedDrawable, Theme.profile_verifiedCheckDrawable));
                            } else {
                                r0.nameTextView[i3].setRightDrawable(null);
                            }
                            if (i3 == 0 && string != null) {
                                r0.onlineTextView[i3].setText(string);
                            } else if (!r0.currentChat.megagroup || r0.info == null || r0.info.participants_count > Callback.DEFAULT_DRAG_ANIMATION_DURATION || r0.onlineCount <= 0) {
                                if (i3 == 0 && ChatObject.isChannel(r0.currentChat) && r0.info != null && r0.info.participants_count != 0 && (r0.currentChat.megagroup || r0.currentChat.broadcast)) {
                                    int[] iArr2 = new int[1];
                                    format = LocaleController.formatShortNumber(r0.info.participants_count, iArr2);
                                    if (r0.currentChat.megagroup) {
                                        r0.onlineTextView[i3].setText(LocaleController.formatPluralString("Members", iArr2[0]).replace(String.format("%d", new Object[]{Integer.valueOf(iArr2[0])}), format));
                                    } else {
                                        r0.onlineTextView[i3].setText(LocaleController.formatPluralString("Subscribers", iArr2[0]).replace(String.format("%d", new Object[]{Integer.valueOf(iArr2[0])}), format));
                                    }
                                } else if (!r0.onlineTextView[i3].getText().equals(userName)) {
                                    r0.onlineTextView[i3].setText(userName);
                                }
                            } else if (!r0.onlineTextView[i3].getText().equals(userName)) {
                                r0.onlineTextView[i3].setText(userName);
                            }
                        }
                        i3++;
                    }
                    if (chat.photo != null) {
                        tLObject = chat.photo.photo_small;
                        fileLocation2 = chat.photo.photo_big;
                    } else {
                        fileLocation2 = null;
                    }
                    r0.avatarDrawable.setInfo(chat);
                    r0.avatarImage.setImage(tLObject, "50_50", r0.avatarDrawable);
                    r0.avatarImage.getImageReceiver().setVisible(PhotoViewer.isShowingImage(fileLocation2) ^ true, false);
                }
            }
        }
    }

    private void createActionBarMenu() {
        ActionBarMenu createMenu = this.actionBar.createMenu();
        createMenu.clearItems();
        ActionBarMenuItem actionBarMenuItem = null;
        this.animatingItem = null;
        if (this.user_id != 0) {
            if (UserConfig.getInstance(this.currentAccount).getClientUserId() != this.user_id) {
                TL_userFull userFull = MessagesController.getInstance(this.currentAccount).getUserFull(this.user_id);
                if (userFull != null && userFull.phone_calls_available) {
                    this.callItem = createMenu.addItem(15, (int) C0446R.drawable.ic_call_white_24dp);
                }
                if (ContactsController.getInstance(this.currentAccount).contactsDict.get(Integer.valueOf(this.user_id)) == null) {
                    User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.user_id));
                    if (user != null) {
                        ActionBarMenuItem addItem = createMenu.addItem(10, (int) C0446R.drawable.ic_ab_other);
                        if (this.isBot) {
                            if (!user.bot_nochats) {
                                addItem.addSubItem(9, LocaleController.getString("BotInvite", C0446R.string.BotInvite));
                            }
                            addItem.addSubItem(10, LocaleController.getString("BotShare", C0446R.string.BotShare));
                        }
                        if (user.phone != null && user.phone.length() != 0) {
                            addItem.addSubItem(1, LocaleController.getString("AddContact", C0446R.string.AddContact));
                            addItem.addSubItem(3, LocaleController.getString("ShareContact", C0446R.string.ShareContact));
                            addItem.addSubItem(2, !this.userBlocked ? LocaleController.getString("BlockContact", C0446R.string.BlockContact) : LocaleController.getString("Unblock", C0446R.string.Unblock));
                        } else if (this.isBot) {
                            String str;
                            int i;
                            if (this.userBlocked) {
                                str = "BotRestart";
                                i = C0446R.string.BotRestart;
                            } else {
                                str = "BotStop";
                                i = C0446R.string.BotStop;
                            }
                            addItem.addSubItem(2, LocaleController.getString(str, i));
                        } else {
                            addItem.addSubItem(2, !this.userBlocked ? LocaleController.getString("BlockContact", C0446R.string.BlockContact) : LocaleController.getString("Unblock", C0446R.string.Unblock));
                        }
                        actionBarMenuItem = addItem;
                    } else {
                        return;
                    }
                }
                actionBarMenuItem = createMenu.addItem(10, (int) C0446R.drawable.ic_ab_other);
                actionBarMenuItem.addSubItem(3, LocaleController.getString("ShareContact", C0446R.string.ShareContact));
                actionBarMenuItem.addSubItem(2, !this.userBlocked ? LocaleController.getString("BlockContact", C0446R.string.BlockContact) : LocaleController.getString("Unblock", C0446R.string.Unblock));
                actionBarMenuItem.addSubItem(4, LocaleController.getString("EditContact", C0446R.string.EditContact));
                actionBarMenuItem.addSubItem(5, LocaleController.getString("DeleteContact", C0446R.string.DeleteContact));
            } else {
                actionBarMenuItem = createMenu.addItem(10, (int) C0446R.drawable.ic_ab_other);
                actionBarMenuItem.addSubItem(3, LocaleController.getString("ShareContact", C0446R.string.ShareContact));
            }
        } else if (this.chat_id != 0) {
            if (this.chat_id > 0) {
                Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.chat_id));
                if (this.writeButton != null) {
                    boolean isChannel = ChatObject.isChannel(this.currentChat);
                    if ((!isChannel || ChatObject.canChangeChatInfo(this.currentChat)) && (isChannel || this.currentChat.admin || this.currentChat.creator || !this.currentChat.admins_enabled)) {
                        this.writeButton.setImageResource(C0446R.drawable.floating_camera);
                        this.writeButton.setPadding(0, 0, 0, 0);
                    } else {
                        this.writeButton.setImageResource(C0446R.drawable.floating_message);
                        this.writeButton.setPadding(0, AndroidUtilities.dp(3.0f), 0, 0);
                    }
                }
                if (ChatObject.isChannel(chat)) {
                    if (ChatObject.hasAdminRights(chat)) {
                        this.editItem = createMenu.addItem(12, (int) C0446R.drawable.menu_settings);
                        actionBarMenuItem = createMenu.addItem(10, (int) C0446R.drawable.ic_ab_other);
                        if (chat.megagroup) {
                            actionBarMenuItem.addSubItem(12, LocaleController.getString("ManageGroupMenu", C0446R.string.ManageGroupMenu));
                        } else {
                            actionBarMenuItem.addSubItem(12, LocaleController.getString("ManageChannelMenu", C0446R.string.ManageChannelMenu));
                        }
                    }
                    if (chat.megagroup) {
                        if (actionBarMenuItem == null) {
                            actionBarMenuItem = createMenu.addItem(10, (int) C0446R.drawable.ic_ab_other);
                        }
                        actionBarMenuItem.addSubItem(16, LocaleController.getString("SearchMembers", C0446R.string.SearchMembers));
                        if (!(chat.creator || chat.left || chat.kicked)) {
                            actionBarMenuItem.addSubItem(7, LocaleController.getString("LeaveMegaMenu", C0446R.string.LeaveMegaMenu));
                        }
                    }
                } else {
                    if (!chat.admins_enabled || chat.creator || chat.admin) {
                        this.editItem = createMenu.addItem(8, (int) C0446R.drawable.group_edit_profile);
                    }
                    actionBarMenuItem = createMenu.addItem(10, (int) C0446R.drawable.ic_ab_other);
                    if (chat.creator && this.chat_id > 0) {
                        actionBarMenuItem.addSubItem(11, LocaleController.getString("SetAdmins", C0446R.string.SetAdmins));
                    }
                    if (!chat.admins_enabled || chat.creator || chat.admin) {
                        actionBarMenuItem.addSubItem(8, LocaleController.getString("ChannelEdit", C0446R.string.ChannelEdit));
                    }
                    actionBarMenuItem.addSubItem(16, LocaleController.getString("SearchMembers", C0446R.string.SearchMembers));
                    if (chat.creator && (this.info == null || this.info.participants.participants.size() > 0)) {
                        actionBarMenuItem.addSubItem(13, LocaleController.getString("ConvertGroupMenu", C0446R.string.ConvertGroupMenu));
                    }
                    actionBarMenuItem.addSubItem(7, LocaleController.getString("DeleteAndExit", C0446R.string.DeleteAndExit));
                }
            } else {
                actionBarMenuItem = createMenu.addItem(10, (int) C0446R.drawable.ic_ab_other);
                actionBarMenuItem.addSubItem(8, LocaleController.getString("EditName", C0446R.string.EditName));
            }
        }
        if (actionBarMenuItem == null) {
            actionBarMenuItem = createMenu.addItem(10, (int) C0446R.drawable.ic_ab_other);
        }
        actionBarMenuItem.addSubItem(14, LocaleController.getString("AddShortcut", C0446R.string.AddShortcut));
    }

    protected void onDialogDismiss(Dialog dialog) {
        if (this.listView != null) {
            this.listView.invalidateViews();
        }
    }

    public void didSelectDialogs(DialogsActivity dialogsActivity, ArrayList<Long> arrayList, CharSequence charSequence, boolean z) {
        long longValue = ((Long) arrayList.get(0)).longValue();
        arrayList = new Bundle();
        arrayList.putBoolean("scrollToTopOnResume", true);
        z = (int) longValue;
        if (!z) {
            arrayList.putInt("enc_id", (int) (longValue >> 32));
        } else if (z <= false) {
            arrayList.putInt("user_id", z);
        } else if (z >= false) {
            arrayList.putInt("chat_id", -z);
        }
        if (MessagesController.getInstance(this.currentAccount).checkCanOpenChat(arrayList, dialogsActivity) != null) {
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.closeChats);
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
            presentFragment(new ChatActivity(arrayList), true);
            removeSelfFromStack();
            SendMessagesHelper.getInstance(this.currentAccount).sendMessage(MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.user_id)), longValue, null, null, null);
        }
    }

    public void onRequestPermissionsResultFragment(int i, String[] strArr, int[] iArr) {
        if (i == 101) {
            i = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.user_id));
            if (i != 0) {
                if (iArr.length <= null || iArr[null] != null) {
                    VoIPHelper.permissionDenied(getParentActivity(), null);
                } else {
                    VoIPHelper.startCall(i, getParentActivity(), MessagesController.getInstance(this.currentAccount).getUserFull(i.id));
                }
            }
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        AnonymousClass30 anonymousClass30 = new ThemeDescriptionDelegate() {
            public void didSetColor() {
                if (ProfileActivity.this.listView != null) {
                    int childCount = ProfileActivity.this.listView.getChildCount();
                    for (int i = 0; i < childCount; i++) {
                        View childAt = ProfileActivity.this.listView.getChildAt(i);
                        if (childAt instanceof UserCell) {
                            ((UserCell) childAt).update(0);
                        }
                    }
                }
            }
        };
        ThemeDescription[] themeDescriptionArr = new ThemeDescription[92];
        themeDescriptionArr[0] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite);
        themeDescriptionArr[1] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUBACKGROUND, null, null, null, null, Theme.key_actionBarDefaultSubmenuBackground);
        themeDescriptionArr[2] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM, null, null, null, null, Theme.key_actionBarDefaultSubmenuItem);
        themeDescriptionArr[3] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_avatar_backgroundActionBarBlue);
        themeDescriptionArr[4] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_avatar_backgroundActionBarBlue);
        themeDescriptionArr[5] = new ThemeDescription(this.topView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_avatar_backgroundActionBarBlue);
        themeDescriptionArr[6] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_avatar_actionBarSelectorBlue);
        themeDescriptionArr[7] = new ThemeDescription(this.nameTextView[1], ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_profile_title);
        themeDescriptionArr[8] = new ThemeDescription(this.onlineTextView[1], ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_avatar_subtitleInProfileBlue);
        themeDescriptionArr[9] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_avatar_backgroundActionBarRed);
        themeDescriptionArr[10] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_avatar_backgroundActionBarRed);
        themeDescriptionArr[11] = new ThemeDescription(this.topView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_avatar_backgroundActionBarRed);
        themeDescriptionArr[12] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_avatar_actionBarSelectorRed);
        themeDescriptionArr[13] = new ThemeDescription(this.onlineTextView[1], ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_avatar_subtitleInProfileRed);
        themeDescriptionArr[14] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_avatar_actionBarIconRed);
        themeDescriptionArr[15] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_avatar_backgroundActionBarOrange);
        themeDescriptionArr[16] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_avatar_backgroundActionBarOrange);
        themeDescriptionArr[17] = new ThemeDescription(this.topView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_avatar_backgroundActionBarOrange);
        themeDescriptionArr[18] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_avatar_actionBarSelectorOrange);
        themeDescriptionArr[19] = new ThemeDescription(this.onlineTextView[1], ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_avatar_subtitleInProfileOrange);
        themeDescriptionArr[20] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_avatar_actionBarIconOrange);
        themeDescriptionArr[21] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_avatar_backgroundActionBarViolet);
        themeDescriptionArr[22] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_avatar_backgroundActionBarViolet);
        themeDescriptionArr[23] = new ThemeDescription(this.topView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_avatar_backgroundActionBarViolet);
        themeDescriptionArr[24] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_avatar_actionBarSelectorViolet);
        themeDescriptionArr[25] = new ThemeDescription(this.onlineTextView[1], ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_avatar_subtitleInProfileViolet);
        themeDescriptionArr[26] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_avatar_actionBarIconViolet);
        themeDescriptionArr[27] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_avatar_backgroundActionBarGreen);
        themeDescriptionArr[28] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_avatar_backgroundActionBarGreen);
        themeDescriptionArr[29] = new ThemeDescription(this.topView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_avatar_backgroundActionBarGreen);
        themeDescriptionArr[30] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_avatar_actionBarSelectorGreen);
        themeDescriptionArr[31] = new ThemeDescription(this.onlineTextView[1], ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_avatar_subtitleInProfileGreen);
        themeDescriptionArr[32] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_avatar_actionBarIconGreen);
        themeDescriptionArr[33] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_avatar_backgroundActionBarCyan);
        themeDescriptionArr[34] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_avatar_backgroundActionBarCyan);
        themeDescriptionArr[35] = new ThemeDescription(this.topView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_avatar_backgroundActionBarCyan);
        themeDescriptionArr[36] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_avatar_actionBarSelectorCyan);
        themeDescriptionArr[37] = new ThemeDescription(this.onlineTextView[1], ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_avatar_subtitleInProfileCyan);
        themeDescriptionArr[38] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_avatar_actionBarIconCyan);
        themeDescriptionArr[39] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_avatar_backgroundActionBarPink);
        themeDescriptionArr[40] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_avatar_backgroundActionBarPink);
        themeDescriptionArr[41] = new ThemeDescription(this.topView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_avatar_backgroundActionBarPink);
        themeDescriptionArr[42] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_avatar_actionBarSelectorPink);
        themeDescriptionArr[43] = new ThemeDescription(this.onlineTextView[1], ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_avatar_subtitleInProfilePink);
        themeDescriptionArr[44] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_avatar_actionBarIconPink);
        themeDescriptionArr[45] = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector);
        themeDescriptionArr[46] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider);
        View view = this.listView;
        View view2 = view;
        themeDescriptionArr[47] = new ThemeDescription(view2, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow);
        themeDescriptionArr[48] = new ThemeDescription(this.avatarImage, 0, null, null, new Drawable[]{Theme.avatar_photoDrawable, Theme.avatar_broadcastDrawable, Theme.avatar_savedDrawable}, null, Theme.key_avatar_text);
        themeDescriptionArr[49] = new ThemeDescription(this.avatarImage, 0, null, null, new Drawable[]{this.avatarDrawable}, null, Theme.key_avatar_backgroundInProfileRed);
        themeDescriptionArr[50] = new ThemeDescription(this.avatarImage, 0, null, null, new Drawable[]{this.avatarDrawable}, null, Theme.key_avatar_backgroundInProfileOrange);
        themeDescriptionArr[51] = new ThemeDescription(this.avatarImage, 0, null, null, new Drawable[]{this.avatarDrawable}, null, Theme.key_avatar_backgroundInProfileViolet);
        themeDescriptionArr[52] = new ThemeDescription(this.avatarImage, 0, null, null, new Drawable[]{this.avatarDrawable}, null, Theme.key_avatar_backgroundInProfileGreen);
        themeDescriptionArr[53] = new ThemeDescription(this.avatarImage, 0, null, null, new Drawable[]{this.avatarDrawable}, null, Theme.key_avatar_backgroundInProfileCyan);
        themeDescriptionArr[54] = new ThemeDescription(this.avatarImage, 0, null, null, new Drawable[]{this.avatarDrawable}, null, Theme.key_avatar_backgroundInProfileBlue);
        themeDescriptionArr[55] = new ThemeDescription(this.avatarImage, 0, null, null, new Drawable[]{this.avatarDrawable}, null, Theme.key_avatar_backgroundInProfilePink);
        themeDescriptionArr[56] = new ThemeDescription(this.writeButton, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, Theme.key_profile_actionIcon);
        themeDescriptionArr[57] = new ThemeDescription(this.writeButton, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_profile_actionBackground);
        themeDescriptionArr[58] = new ThemeDescription(this.writeButton, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, Theme.key_profile_actionPressedBackground);
        view = this.listView;
        view2 = view;
        themeDescriptionArr[59] = new ThemeDescription(view2, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        view = this.listView;
        view2 = view;
        themeDescriptionArr[60] = new ThemeDescription(view2, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteGreenText2);
        view = this.listView;
        view2 = view;
        themeDescriptionArr[61] = new ThemeDescription(view2, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteRedText5);
        themeDescriptionArr[62] = new ThemeDescription(this.listView, 0, new Class[]{TextCell.class}, new String[]{"valueTextView"}, null, null, null, Theme.key_windowBackgroundWhiteValueText);
        themeDescriptionArr[63] = new ThemeDescription(this.listView, 0, new Class[]{TextCell.class}, new String[]{"imageView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayIcon);
        themeDescriptionArr[64] = new ThemeDescription(this.listView, 0, new Class[]{TextDetailCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        themeDescriptionArr[65] = new ThemeDescription(this.listView, 0, new Class[]{TextDetailCell.class}, new String[]{"valueImageView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayIcon);
        themeDescriptionArr[66] = new ThemeDescription(this.listView, 0, new Class[]{TextDetailCell.class}, new String[]{"imageView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayIcon);
        view = this.listView;
        view2 = view;
        themeDescriptionArr[67] = new ThemeDescription(view2, ThemeDescription.FLAG_CHECKTAG, new Class[]{UserCell.class}, new String[]{"adminImage"}, null, null, null, Theme.key_profile_creatorIcon);
        view = this.listView;
        view2 = view;
        themeDescriptionArr[68] = new ThemeDescription(view2, ThemeDescription.FLAG_CHECKTAG, new Class[]{UserCell.class}, new String[]{"adminImage"}, null, null, null, Theme.key_profile_adminIcon);
        themeDescriptionArr[69] = new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, new String[]{"nameTextView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        ThemeDescriptionDelegate themeDescriptionDelegate = anonymousClass30;
        themeDescriptionArr[70] = new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, new String[]{"statusColor"}, null, null, themeDescriptionDelegate, Theme.key_windowBackgroundWhiteGrayText);
        themeDescriptionArr[71] = new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, new String[]{"statusOnlineColor"}, null, null, themeDescriptionDelegate, Theme.key_windowBackgroundWhiteBlueText);
        themeDescriptionArr[72] = new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, null, new Drawable[]{Theme.avatar_photoDrawable, Theme.avatar_broadcastDrawable, Theme.avatar_savedDrawable}, null, Theme.key_avatar_text);
        AnonymousClass30 anonymousClass302 = anonymousClass30;
        themeDescriptionArr[73] = new ThemeDescription(null, 0, null, null, null, anonymousClass302, Theme.key_avatar_backgroundRed);
        themeDescriptionArr[74] = new ThemeDescription(null, 0, null, null, null, anonymousClass302, Theme.key_avatar_backgroundOrange);
        themeDescriptionArr[75] = new ThemeDescription(null, 0, null, null, null, anonymousClass302, Theme.key_avatar_backgroundViolet);
        themeDescriptionArr[76] = new ThemeDescription(null, 0, null, null, null, anonymousClass302, Theme.key_avatar_backgroundGreen);
        themeDescriptionArr[77] = new ThemeDescription(null, 0, null, null, null, anonymousClass302, Theme.key_avatar_backgroundCyan);
        themeDescriptionArr[78] = new ThemeDescription(null, 0, null, null, null, anonymousClass302, Theme.key_avatar_backgroundBlue);
        themeDescriptionArr[79] = new ThemeDescription(null, 0, null, null, null, anonymousClass302, Theme.key_avatar_backgroundPink);
        themeDescriptionArr[80] = new ThemeDescription(this.listView, 0, new Class[]{LoadingCell.class}, new String[]{"progressBar"}, null, null, null, Theme.key_progressCircle);
        themeDescriptionArr[81] = new ThemeDescription(this.listView, 0, new Class[]{AboutLinkCell.class}, new String[]{"imageView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayIcon);
        themeDescriptionArr[82] = new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{AboutLinkCell.class}, Theme.profile_aboutTextPaint, null, null, Theme.key_windowBackgroundWhiteBlackText);
        themeDescriptionArr[83] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LINKCOLOR, new Class[]{AboutLinkCell.class}, Theme.profile_aboutTextPaint, null, null, Theme.key_windowBackgroundWhiteLinkText);
        themeDescriptionArr[84] = new ThemeDescription(this.listView, 0, new Class[]{AboutLinkCell.class}, Theme.linkSelectionPaint, null, null, Theme.key_windowBackgroundWhiteLinkSelection);
        themeDescriptionArr[85] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow);
        themeDescriptionArr[86] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{ShadowSectionCell.class}, null, null, null, Theme.key_windowBackgroundGray);
        themeDescriptionArr[87] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow);
        themeDescriptionArr[88] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextInfoPrivacyCell.class}, null, null, null, Theme.key_windowBackgroundGray);
        themeDescriptionArr[89] = new ThemeDescription(this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText4);
        themeDescriptionArr[90] = new ThemeDescription(this.nameTextView[1], 0, null, null, new Drawable[]{Theme.profile_verifiedCheckDrawable}, null, Theme.key_profile_verifiedCheck);
        themeDescriptionArr[91] = new ThemeDescription(this.nameTextView[1], 0, null, null, new Drawable[]{Theme.profile_verifiedDrawable}, null, Theme.key_profile_verifiedBackground);
        return themeDescriptionArr;
    }
}
