package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.annotation.SuppressLint;
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
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Property;
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
import androidx.annotation.Keep;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.LayoutParams;
import androidx.recyclerview.widget.RecyclerView.OnScrollListener;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.CountDownLatch;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
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
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.BotInfo;
import org.telegram.tgnet.TLRPC.ChannelParticipant;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.ChatFull;
import org.telegram.tgnet.TLRPC.ChatParticipant;
import org.telegram.tgnet.TLRPC.ChatPhoto;
import org.telegram.tgnet.TLRPC.DecryptedMessageAction;
import org.telegram.tgnet.TLRPC.Dialog;
import org.telegram.tgnet.TLRPC.EncryptedChat;
import org.telegram.tgnet.TLRPC.MessageAction;
import org.telegram.tgnet.TLRPC.TL_channelFull;
import org.telegram.tgnet.TLRPC.TL_channelLocation;
import org.telegram.tgnet.TLRPC.TL_channelParticipantAdmin;
import org.telegram.tgnet.TLRPC.TL_channelParticipantCreator;
import org.telegram.tgnet.TLRPC.TL_channelParticipantsRecent;
import org.telegram.tgnet.TLRPC.TL_channels_channelParticipant;
import org.telegram.tgnet.TLRPC.TL_channels_channelParticipants;
import org.telegram.tgnet.TLRPC.TL_channels_getParticipant;
import org.telegram.tgnet.TLRPC.TL_channels_getParticipants;
import org.telegram.tgnet.TLRPC.TL_chatAdminRights;
import org.telegram.tgnet.TLRPC.TL_chatBannedRights;
import org.telegram.tgnet.TLRPC.TL_chatChannelParticipant;
import org.telegram.tgnet.TLRPC.TL_chatFull;
import org.telegram.tgnet.TLRPC.TL_chatParticipant;
import org.telegram.tgnet.TLRPC.TL_chatParticipantAdmin;
import org.telegram.tgnet.TLRPC.TL_chatParticipants;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageActionSetMessageTTL;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_messageEncryptedAction;
import org.telegram.tgnet.TLRPC.TL_messages_getStatsURL;
import org.telegram.tgnet.TLRPC.TL_peerNotifySettings;
import org.telegram.tgnet.TLRPC.TL_statsURL;
import org.telegram.tgnet.TLRPC.TL_userEmpty;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.UserFull;
import org.telegram.tgnet.TLRPC.UserProfilePhoto;
import org.telegram.tgnet.TLRPC.UserStatus;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BackDrawable;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate;
import org.telegram.ui.Cells.AboutLinkCell;
import org.telegram.ui.Cells.DividerCell;
import org.telegram.ui.Cells.EmptyCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.NotificationsCheckCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Cells.TextDetailCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.UserCell;
import org.telegram.ui.ChatRightsEditActivity.ChatRightsEditActivityDelegate;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;
import org.telegram.ui.Components.ScamDrawable;
import org.telegram.ui.Components.UndoView;
import org.telegram.ui.Components.voip.VoIPHelper;
import org.telegram.ui.DialogsActivity.DialogsActivityDelegate;
import org.telegram.ui.MediaActivity.SharedMediaData;
import org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider;
import org.telegram.ui.PhotoViewer.PhotoViewerProvider;

public class ProfileActivity extends BaseFragment implements NotificationCenterDelegate, DialogsActivityDelegate {
    private static final int add_contact = 1;
    private static final int add_member = 18;
    private static final int add_shortcut = 14;
    private static final int block_contact = 2;
    private static final int call_item = 15;
    private static final int delete_contact = 5;
    private static final int edit_channel = 12;
    private static final int edit_contact = 4;
    private static final int invite_to_group = 9;
    private static final int leave_group = 7;
    private static final int search_members = 17;
    private static final int share = 10;
    private static final int share_contact = 3;
    private static final int statistics = 19;
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
    private boolean[] isOnline = new boolean[1];
    private int joinRow;
    private int[] lastMediaCount = new int[]{-1, -1, -1, -1, -1};
    private int lastSectionRow;
    private LinearLayoutManager layoutManager;
    private int leaveChannelRow;
    private int linksRow;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    private boolean loadingUsers;
    private int locationRow;
    private MediaActivity mediaActivity;
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
    private PhotoViewerProvider provider = new EmptyPhotoViewerProvider() {
        /* JADX WARNING: Missing block: B:10:0x002c, code skipped:
            if (r7 != null) goto L_0x005b;
     */
        /* JADX WARNING: Missing block: B:18:0x0057, code skipped:
            if (r7 != null) goto L_0x005b;
     */
        public org.telegram.ui.PhotoViewer.PlaceProviderObject getPlaceForPhoto(org.telegram.messenger.MessageObject r5, org.telegram.tgnet.TLRPC.FileLocation r6, int r7, boolean r8) {
            /*
            r4 = this;
            r5 = 0;
            if (r6 != 0) goto L_0x0004;
        L_0x0003:
            return r5;
        L_0x0004:
            r7 = org.telegram.ui.ProfileActivity.this;
            r7 = r7.user_id;
            if (r7 == 0) goto L_0x002f;
        L_0x000c:
            r7 = org.telegram.ui.ProfileActivity.this;
            r7 = r7.currentAccount;
            r7 = org.telegram.messenger.MessagesController.getInstance(r7);
            r8 = org.telegram.ui.ProfileActivity.this;
            r8 = r8.user_id;
            r8 = java.lang.Integer.valueOf(r8);
            r7 = r7.getUser(r8);
            if (r7 == 0) goto L_0x005a;
        L_0x0026:
            r7 = r7.photo;
            if (r7 == 0) goto L_0x005a;
        L_0x002a:
            r7 = r7.photo_big;
            if (r7 == 0) goto L_0x005a;
        L_0x002e:
            goto L_0x005b;
        L_0x002f:
            r7 = org.telegram.ui.ProfileActivity.this;
            r7 = r7.chat_id;
            if (r7 == 0) goto L_0x005a;
        L_0x0037:
            r7 = org.telegram.ui.ProfileActivity.this;
            r7 = r7.currentAccount;
            r7 = org.telegram.messenger.MessagesController.getInstance(r7);
            r8 = org.telegram.ui.ProfileActivity.this;
            r8 = r8.chat_id;
            r8 = java.lang.Integer.valueOf(r8);
            r7 = r7.getChat(r8);
            if (r7 == 0) goto L_0x005a;
        L_0x0051:
            r7 = r7.photo;
            if (r7 == 0) goto L_0x005a;
        L_0x0055:
            r7 = r7.photo_big;
            if (r7 == 0) goto L_0x005a;
        L_0x0059:
            goto L_0x005b;
        L_0x005a:
            r7 = r5;
        L_0x005b:
            if (r7 == 0) goto L_0x00f4;
        L_0x005d:
            r8 = r7.local_id;
            r0 = r6.local_id;
            if (r8 != r0) goto L_0x00f4;
        L_0x0063:
            r0 = r7.volume_id;
            r2 = r6.volume_id;
            r8 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1));
            if (r8 != 0) goto L_0x00f4;
        L_0x006b:
            r7 = r7.dc_id;
            r6 = r6.dc_id;
            if (r7 != r6) goto L_0x00f4;
        L_0x0071:
            r5 = 2;
            r5 = new int[r5];
            r6 = org.telegram.ui.ProfileActivity.this;
            r6 = r6.avatarImage;
            r6.getLocationInWindow(r5);
            r6 = new org.telegram.ui.PhotoViewer$PlaceProviderObject;
            r6.<init>();
            r7 = 0;
            r8 = r5[r7];
            r6.viewX = r8;
            r8 = 1;
            r5 = r5[r8];
            r8 = android.os.Build.VERSION.SDK_INT;
            r0 = 21;
            if (r8 < r0) goto L_0x0091;
        L_0x0090:
            goto L_0x0093;
        L_0x0091:
            r7 = org.telegram.messenger.AndroidUtilities.statusBarHeight;
        L_0x0093:
            r5 = r5 - r7;
            r6.viewY = r5;
            r5 = org.telegram.ui.ProfileActivity.this;
            r5 = r5.avatarImage;
            r6.parentView = r5;
            r5 = org.telegram.ui.ProfileActivity.this;
            r5 = r5.avatarImage;
            r5 = r5.getImageReceiver();
            r6.imageReceiver = r5;
            r5 = org.telegram.ui.ProfileActivity.this;
            r5 = r5.user_id;
            if (r5 == 0) goto L_0x00bb;
        L_0x00b2:
            r5 = org.telegram.ui.ProfileActivity.this;
            r5 = r5.user_id;
            r6.dialogId = r5;
            goto L_0x00cc;
        L_0x00bb:
            r5 = org.telegram.ui.ProfileActivity.this;
            r5 = r5.chat_id;
            if (r5 == 0) goto L_0x00cc;
        L_0x00c3:
            r5 = org.telegram.ui.ProfileActivity.this;
            r5 = r5.chat_id;
            r5 = -r5;
            r6.dialogId = r5;
        L_0x00cc:
            r5 = r6.imageReceiver;
            r5 = r5.getBitmapSafe();
            r6.thumb = r5;
            r5 = -1;
            r6.size = r5;
            r5 = org.telegram.ui.ProfileActivity.this;
            r5 = r5.avatarImage;
            r5 = r5.getImageReceiver();
            r5 = r5.getRoundRadius();
            r6.radius = r5;
            r5 = org.telegram.ui.ProfileActivity.this;
            r5 = r5.avatarImage;
            r5 = r5.getScaleX();
            r6.scale = r5;
            return r6;
        L_0x00f4:
            return r5;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ProfileActivity$AnonymousClass1.getPlaceForPhoto(org.telegram.messenger.MessageObject, org.telegram.tgnet.TLRPC$FileLocation, int, boolean):org.telegram.ui.PhotoViewer$PlaceProviderObject");
        }

        public void willHidePhotoViewer() {
            ProfileActivity.this.avatarImage.getImageReceiver().setVisible(true, true);
        }
    };
    private boolean recreateMenuAfterAnimation;
    private boolean reportSpam;
    private int rowCount;
    private ScamDrawable scamDrawable;
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
    private int unblockRow;
    private UndoView undoView;
    private boolean userBlocked;
    private UserFull userInfo;
    private int userInfoRow;
    private int user_id;
    private int usernameRow;
    private boolean usersEndReached;
    private int voiceRow;
    private ImageView writeButton;
    private AnimatorSet writeButtonAnimation;

    private class TopView extends View {
        private int currentColor;
        private Paint paint = new Paint();

        public TopView(Context context) {
            super(context);
        }

        /* Access modifiers changed, original: protected */
        public void onMeasure(int i, int i2) {
            setMeasuredDimension(MeasureSpec.getSize(i), (ActionBar.getCurrentActionBarHeight() + (ProfileActivity.this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0)) + AndroidUtilities.dp(91.0f));
        }

        public void setBackgroundColor(int i) {
            if (i != this.currentColor) {
                this.paint.setColor(i);
                invalidate();
            }
        }

        /* Access modifiers changed, original: protected */
        public void onDraw(Canvas canvas) {
            int measuredHeight = getMeasuredHeight() - AndroidUtilities.dp(91.0f);
            canvas.drawRect(0.0f, 0.0f, (float) getMeasuredWidth(), (float) (ProfileActivity.this.extraHeight + measuredHeight), this.paint);
            if (ProfileActivity.this.parentLayout != null) {
                ProfileActivity.this.parentLayout.drawHeaderShadow(canvas, measuredHeight + ProfileActivity.this.extraHeight);
            }
        }
    }

    private class ListAdapter extends SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View textDetailCell;
            if (i != 11) {
                View headerCell;
                switch (i) {
                    case 1:
                        headerCell = new HeaderCell(this.mContext, 23);
                        break;
                    case 2:
                        textDetailCell = new TextDetailCell(this.mContext);
                        break;
                    case 3:
                        textDetailCell = new AboutLinkCell(this.mContext) {
                            /* Access modifiers changed, original: protected */
                            public void didPressUrl(String str) {
                                if (str.startsWith("@")) {
                                    MessagesController.getInstance(ProfileActivity.this.currentAccount).openByUserName(str.substring(1), ProfileActivity.this, 0);
                                } else if (str.startsWith("#")) {
                                    DialogsActivity dialogsActivity = new DialogsActivity(null);
                                    dialogsActivity.setSearchString(str);
                                    ProfileActivity.this.presentFragment(dialogsActivity);
                                } else if (str.startsWith("/") && ProfileActivity.this.parentLayout.fragmentsStack.size() > 1) {
                                    BaseFragment baseFragment = (BaseFragment) ProfileActivity.this.parentLayout.fragmentsStack.get(ProfileActivity.this.parentLayout.fragmentsStack.size() - 2);
                                    if (baseFragment instanceof ChatActivity) {
                                        ProfileActivity.this.finishFragment();
                                        ((ChatActivity) baseFragment).chatActivityEnterView.setCommand(null, str, false, false);
                                    }
                                }
                            }
                        };
                        break;
                    case 4:
                        textDetailCell = new TextCell(this.mContext);
                        break;
                    case 5:
                        textDetailCell = new DividerCell(this.mContext);
                        textDetailCell.setPadding(AndroidUtilities.dp(20.0f), AndroidUtilities.dp(4.0f), 0, 0);
                        break;
                    case 6:
                        headerCell = new NotificationsCheckCell(this.mContext, 23, 70);
                        break;
                    case 7:
                        textDetailCell = new ShadowSectionCell(this.mContext);
                        break;
                    case 8:
                        textDetailCell = new UserCell(this.mContext, ProfileActivity.this.addMemberRow == -1 ? 9 : 6, 0, true);
                        break;
                    default:
                        textDetailCell = null;
                        break;
                }
                textDetailCell = headerCell;
            } else {
                textDetailCell = new EmptyCell(this.mContext, 36);
            }
            textDetailCell.setLayoutParams(new LayoutParams(-1, -2));
            return new Holder(textDetailCell);
        }

        /* JADX WARNING: Removed duplicated region for block: B:100:0x02ce  */
        /* JADX WARNING: Removed duplicated region for block: B:100:0x02ce  */
        public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r19, int r20) {
            /*
            r18 = this;
            r0 = r18;
            r1 = r19;
            r2 = r20;
            r3 = r19.getItemViewType();
            r4 = 3;
            r5 = NUM; // 0x7f0d023f float:1.874328E38 double:1.0531300616E-314;
            r6 = "ChannelMembers";
            r7 = 2;
            r8 = 0;
            r9 = -1;
            r10 = 0;
            r11 = 1;
            switch(r3) {
                case 1: goto L_0x088d;
                case 2: goto L_0x0753;
                case 3: goto L_0x0707;
                case 4: goto L_0x02e5;
                case 5: goto L_0x0018;
                case 6: goto L_0x0173;
                case 7: goto L_0x00c9;
                case 8: goto L_0x001a;
                default: goto L_0x0018;
            };
        L_0x0018:
            goto L_0x08f5;
        L_0x001a:
            r1 = r1.itemView;
            r12 = r1;
            r12 = (org.telegram.ui.Cells.UserCell) r12;
            r1 = org.telegram.ui.ProfileActivity.this;
            r1 = r1.sortedUsers;
            r1 = r1.isEmpty();
            if (r1 != 0) goto L_0x0054;
        L_0x002b:
            r1 = org.telegram.ui.ProfileActivity.this;
            r1 = r1.chatInfo;
            r1 = r1.participants;
            r1 = r1.participants;
            r3 = org.telegram.ui.ProfileActivity.this;
            r3 = r3.sortedUsers;
            r4 = org.telegram.ui.ProfileActivity.this;
            r4 = r4.membersStartRow;
            r4 = r2 - r4;
            r3 = r3.get(r4);
            r3 = (java.lang.Integer) r3;
            r3 = r3.intValue();
            r1 = r1.get(r3);
            r1 = (org.telegram.tgnet.TLRPC.ChatParticipant) r1;
            goto L_0x006c;
        L_0x0054:
            r1 = org.telegram.ui.ProfileActivity.this;
            r1 = r1.chatInfo;
            r1 = r1.participants;
            r1 = r1.participants;
            r3 = org.telegram.ui.ProfileActivity.this;
            r3 = r3.membersStartRow;
            r3 = r2 - r3;
            r1 = r1.get(r3);
            r1 = (org.telegram.tgnet.TLRPC.ChatParticipant) r1;
        L_0x006c:
            if (r1 == 0) goto L_0x08f5;
        L_0x006e:
            r3 = r1 instanceof org.telegram.tgnet.TLRPC.TL_chatChannelParticipant;
            if (r3 == 0) goto L_0x008b;
        L_0x0072:
            r3 = r1;
            r3 = (org.telegram.tgnet.TLRPC.TL_chatChannelParticipant) r3;
            r3 = r3.channelParticipant;
            r4 = r3 instanceof org.telegram.tgnet.TLRPC.TL_channelParticipantCreator;
            if (r4 == 0) goto L_0x007f;
        L_0x007b:
            r12.setIsAdmin(r11);
            goto L_0x009e;
        L_0x007f:
            r3 = r3 instanceof org.telegram.tgnet.TLRPC.TL_channelParticipantAdmin;
            if (r3 == 0) goto L_0x0087;
        L_0x0083:
            r12.setIsAdmin(r7);
            goto L_0x009e;
        L_0x0087:
            r12.setIsAdmin(r10);
            goto L_0x009e;
        L_0x008b:
            r3 = r1 instanceof org.telegram.tgnet.TLRPC.TL_chatParticipantCreator;
            if (r3 == 0) goto L_0x0093;
        L_0x008f:
            r12.setIsAdmin(r11);
            goto L_0x009e;
        L_0x0093:
            r3 = r1 instanceof org.telegram.tgnet.TLRPC.TL_chatParticipantAdmin;
            if (r3 == 0) goto L_0x009b;
        L_0x0097:
            r12.setIsAdmin(r7);
            goto L_0x009e;
        L_0x009b:
            r12.setIsAdmin(r10);
        L_0x009e:
            r3 = org.telegram.ui.ProfileActivity.this;
            r3 = r3.currentAccount;
            r3 = org.telegram.messenger.MessagesController.getInstance(r3);
            r1 = r1.user_id;
            r1 = java.lang.Integer.valueOf(r1);
            r13 = r3.getUser(r1);
            r14 = 0;
            r15 = 0;
            r16 = 0;
            r1 = org.telegram.ui.ProfileActivity.this;
            r1 = r1.membersEndRow;
            r1 = r1 - r11;
            if (r2 == r1) goto L_0x00c2;
        L_0x00bf:
            r17 = 1;
            goto L_0x00c4;
        L_0x00c2:
            r17 = 0;
        L_0x00c4:
            r12.setData(r13, r14, r15, r16, r17);
            goto L_0x08f5;
        L_0x00c9:
            r1 = r1.itemView;
            r3 = java.lang.Integer.valueOf(r20);
            r1.setTag(r3);
            r3 = org.telegram.ui.ProfileActivity.this;
            r3 = r3.infoSectionRow;
            if (r2 != r3) goto L_0x00f2;
        L_0x00da:
            r3 = org.telegram.ui.ProfileActivity.this;
            r3 = r3.sharedSectionRow;
            if (r3 != r9) goto L_0x00f2;
        L_0x00e2:
            r3 = org.telegram.ui.ProfileActivity.this;
            r3 = r3.lastSectionRow;
            if (r3 != r9) goto L_0x00f2;
        L_0x00ea:
            r3 = org.telegram.ui.ProfileActivity.this;
            r3 = r3.settingsSectionRow;
            if (r3 == r9) goto L_0x014e;
        L_0x00f2:
            r3 = org.telegram.ui.ProfileActivity.this;
            r3 = r3.settingsSectionRow;
            if (r2 != r3) goto L_0x0102;
        L_0x00fa:
            r3 = org.telegram.ui.ProfileActivity.this;
            r3 = r3.sharedSectionRow;
            if (r3 == r9) goto L_0x014e;
        L_0x0102:
            r3 = org.telegram.ui.ProfileActivity.this;
            r3 = r3.sharedSectionRow;
            if (r2 != r3) goto L_0x0112;
        L_0x010a:
            r3 = org.telegram.ui.ProfileActivity.this;
            r3 = r3.lastSectionRow;
            if (r3 == r9) goto L_0x014e;
        L_0x0112:
            r3 = org.telegram.ui.ProfileActivity.this;
            r3 = r3.lastSectionRow;
            if (r2 == r3) goto L_0x014e;
        L_0x011a:
            r3 = org.telegram.ui.ProfileActivity.this;
            r3 = r3.membersSectionRow;
            if (r2 != r3) goto L_0x0141;
        L_0x0122:
            r2 = org.telegram.ui.ProfileActivity.this;
            r2 = r2.lastSectionRow;
            if (r2 != r9) goto L_0x0141;
        L_0x012a:
            r2 = org.telegram.ui.ProfileActivity.this;
            r2 = r2.sharedSectionRow;
            if (r2 == r9) goto L_0x014e;
        L_0x0132:
            r2 = org.telegram.ui.ProfileActivity.this;
            r2 = r2.membersSectionRow;
            r3 = org.telegram.ui.ProfileActivity.this;
            r3 = r3.sharedSectionRow;
            if (r2 <= r3) goto L_0x0141;
        L_0x0140:
            goto L_0x014e;
        L_0x0141:
            r2 = r0.mContext;
            r3 = NUM; // 0x7var_e5 float:1.7945042E38 double:1.052935616E-314;
            r4 = "windowBackgroundGrayShadow";
            r2 = org.telegram.ui.ActionBar.Theme.getThemedDrawable(r2, r3, r4);
            goto L_0x015a;
        L_0x014e:
            r2 = r0.mContext;
            r3 = NUM; // 0x7var_e6 float:1.7945044E38 double:1.0529356167E-314;
            r4 = "windowBackgroundGrayShadow";
            r2 = org.telegram.ui.ActionBar.Theme.getThemedDrawable(r2, r3, r4);
        L_0x015a:
            r3 = new org.telegram.ui.Components.CombinedDrawable;
            r4 = new android.graphics.drawable.ColorDrawable;
            r5 = "windowBackgroundGray";
            r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
            r4.<init>(r5);
            r3.<init>(r4, r2);
            r3.setFullsize(r11);
            r1.setBackgroundDrawable(r3);
            goto L_0x08f5;
        L_0x0173:
            r1 = r1.itemView;
            r1 = (org.telegram.ui.Cells.NotificationsCheckCell) r1;
            r3 = org.telegram.ui.ProfileActivity.this;
            r3 = r3.notificationsRow;
            if (r2 != r3) goto L_0x08f5;
        L_0x017f:
            r2 = org.telegram.ui.ProfileActivity.this;
            r2 = r2.currentAccount;
            r2 = org.telegram.messenger.MessagesController.getNotificationsSettings(r2);
            r3 = org.telegram.ui.ProfileActivity.this;
            r5 = r3.dialog_id;
            r12 = 0;
            r3 = (r5 > r12 ? 1 : (r5 == r12 ? 0 : -1));
            if (r3 == 0) goto L_0x019c;
        L_0x0195:
            r3 = org.telegram.ui.ProfileActivity.this;
            r5 = r3.dialog_id;
            goto L_0x01b3;
        L_0x019c:
            r3 = org.telegram.ui.ProfileActivity.this;
            r3 = r3.user_id;
            if (r3 == 0) goto L_0x01ab;
        L_0x01a4:
            r3 = org.telegram.ui.ProfileActivity.this;
            r3 = r3.user_id;
            goto L_0x01b2;
        L_0x01ab:
            r3 = org.telegram.ui.ProfileActivity.this;
            r3 = r3.chat_id;
            r3 = -r3;
        L_0x01b2:
            r5 = (long) r3;
        L_0x01b3:
            r3 = new java.lang.StringBuilder;
            r3.<init>();
            r9 = "custom_";
            r3.append(r9);
            r3.append(r5);
            r3 = r3.toString();
            r3 = r2.getBoolean(r3, r10);
            r9 = new java.lang.StringBuilder;
            r9.<init>();
            r12 = "notify2_";
            r9.append(r12);
            r9.append(r5);
            r9 = r9.toString();
            r9 = r2.contains(r9);
            r12 = new java.lang.StringBuilder;
            r12.<init>();
            r13 = "notify2_";
            r12.append(r13);
            r12.append(r5);
            r12 = r12.toString();
            r12 = r2.getInt(r12, r10);
            r13 = new java.lang.StringBuilder;
            r13.<init>();
            r14 = "notifyuntil_";
            r13.append(r14);
            r13.append(r5);
            r13 = r13.toString();
            r2 = r2.getInt(r13, r10);
            if (r12 != r4) goto L_0x0292;
        L_0x0209:
            r4 = NUM; // 0x7fffffff float:NaN double:1.060997895E-314;
            if (r2 == r4) goto L_0x0292;
        L_0x020e:
            r4 = org.telegram.ui.ProfileActivity.this;
            r4 = r4.currentAccount;
            r4 = org.telegram.tgnet.ConnectionsManager.getInstance(r4);
            r4 = r4.getCurrentTime();
            r2 = r2 - r4;
            if (r2 > 0) goto L_0x0237;
        L_0x021f:
            if (r3 == 0) goto L_0x022b;
        L_0x0221:
            r2 = NUM; // 0x7f0d06a8 float:1.874557E38 double:1.0531306194E-314;
            r3 = "NotificationsCustom";
            r2 = org.telegram.messenger.LocaleController.getString(r3, r2);
            goto L_0x0234;
        L_0x022b:
            r2 = NUM; // 0x7f0d06c0 float:1.874562E38 double:1.0531306313E-314;
            r3 = "NotificationsOn";
            r2 = org.telegram.messenger.LocaleController.getString(r3, r2);
        L_0x0234:
            r8 = r2;
            goto L_0x02cc;
        L_0x0237:
            r3 = 3600; // 0xe10 float:5.045E-42 double:1.7786E-320;
            r4 = NUM; // 0x7f0d0aeb float:1.8747784E38 double:1.0531311585E-314;
            r5 = "WillUnmuteIn";
            if (r2 >= r3) goto L_0x0253;
        L_0x0240:
            r3 = new java.lang.Object[r11];
            r2 = r2 / 60;
            r6 = "Minutes";
            r2 = org.telegram.messenger.LocaleController.formatPluralString(r6, r2);
            r3[r10] = r2;
            r8 = org.telegram.messenger.LocaleController.formatString(r5, r4, r3);
        L_0x0250:
            r11 = 0;
            goto L_0x02cc;
        L_0x0253:
            r3 = 86400; // 0x15180 float:1.21072E-40 double:4.26873E-319;
            r6 = NUM; // 0x42700000 float:60.0 double:5.507034975E-315;
            if (r2 >= r3) goto L_0x0272;
        L_0x025a:
            r3 = new java.lang.Object[r11];
            r2 = (float) r2;
            r2 = r2 / r6;
            r2 = r2 / r6;
            r6 = (double) r2;
            r6 = java.lang.Math.ceil(r6);
            r2 = (int) r6;
            r6 = "Hours";
            r2 = org.telegram.messenger.LocaleController.formatPluralString(r6, r2);
            r3[r10] = r2;
            r8 = org.telegram.messenger.LocaleController.formatString(r5, r4, r3);
            goto L_0x0250;
        L_0x0272:
            r3 = 31536000; // 0x1e13380 float:8.2725845E-38 double:1.5580854E-316;
            if (r2 >= r3) goto L_0x0250;
        L_0x0277:
            r3 = new java.lang.Object[r11];
            r2 = (float) r2;
            r2 = r2 / r6;
            r2 = r2 / r6;
            r6 = NUM; // 0x41CLASSNAME float:24.0 double:5.450047783E-315;
            r2 = r2 / r6;
            r6 = (double) r2;
            r6 = java.lang.Math.ceil(r6);
            r2 = (int) r6;
            r6 = "Days";
            r2 = org.telegram.messenger.LocaleController.formatPluralString(r6, r2);
            r3[r10] = r2;
            r8 = org.telegram.messenger.LocaleController.formatString(r5, r4, r3);
            goto L_0x0250;
        L_0x0292:
            if (r12 != 0) goto L_0x02a7;
        L_0x0294:
            if (r9 == 0) goto L_0x0297;
        L_0x0296:
            goto L_0x02ab;
        L_0x0297:
            r2 = org.telegram.ui.ProfileActivity.this;
            r2 = r2.currentAccount;
            r2 = org.telegram.messenger.NotificationsController.getInstance(r2);
            r2 = r2.isGlobalNotificationsEnabled(r5);
            r11 = r2;
            goto L_0x02ab;
        L_0x02a7:
            if (r12 != r11) goto L_0x02aa;
        L_0x02a9:
            goto L_0x02ab;
        L_0x02aa:
            r11 = 0;
        L_0x02ab:
            if (r11 == 0) goto L_0x02b9;
        L_0x02ad:
            if (r3 == 0) goto L_0x02b9;
        L_0x02af:
            r2 = NUM; // 0x7f0d06a8 float:1.874557E38 double:1.0531306194E-314;
            r3 = "NotificationsCustom";
            r8 = org.telegram.messenger.LocaleController.getString(r3, r2);
            goto L_0x02cc;
        L_0x02b9:
            if (r11 == 0) goto L_0x02c1;
        L_0x02bb:
            r2 = NUM; // 0x7f0d06c0 float:1.874562E38 double:1.0531306313E-314;
            r3 = "NotificationsOn";
            goto L_0x02c6;
        L_0x02c1:
            r2 = NUM; // 0x7f0d06be float:1.8745615E38 double:1.0531306303E-314;
            r3 = "NotificationsOff";
        L_0x02c6:
            r2 = org.telegram.messenger.LocaleController.getString(r3, r2);
            goto L_0x0234;
        L_0x02cc:
            if (r8 != 0) goto L_0x02d7;
        L_0x02ce:
            r2 = NUM; // 0x7f0d06be float:1.8745615E38 double:1.0531306303E-314;
            r3 = "NotificationsOff";
            r8 = org.telegram.messenger.LocaleController.getString(r3, r2);
        L_0x02d7:
            r2 = NUM; // 0x7f0d06a4 float:1.8745563E38 double:1.0531306175E-314;
            r3 = "Notifications";
            r2 = org.telegram.messenger.LocaleController.getString(r3, r2);
            r1.setTextAndValueAndCheck(r2, r8, r11, r10);
            goto L_0x08f5;
        L_0x02e5:
            r1 = r1.itemView;
            r1 = (org.telegram.ui.Cells.TextCell) r1;
            r3 = "windowBackgroundWhiteBlackText";
            r9 = "windowBackgroundWhiteGrayIcon";
            r1.setColors(r9, r3);
            r1.setTag(r3);
            r3 = org.telegram.ui.ProfileActivity.this;
            r3 = r3.photosRow;
            r9 = "%d";
            if (r2 != r3) goto L_0x032e;
        L_0x02ff:
            r3 = NUM; // 0x7f0d0986 float:1.874706E38 double:1.053130982E-314;
            r4 = "SharedPhotosAndVideos";
            r3 = org.telegram.messenger.LocaleController.getString(r4, r3);
            r4 = new java.lang.Object[r11];
            r5 = org.telegram.ui.ProfileActivity.this;
            r5 = r5.lastMediaCount;
            r5 = r5[r10];
            r5 = java.lang.Integer.valueOf(r5);
            r4[r10] = r5;
            r4 = java.lang.String.format(r9, r4);
            r5 = NUM; // 0x7var_a float:1.7945831E38 double:1.0529358084E-314;
            r6 = org.telegram.ui.ProfileActivity.this;
            r6 = r6.sharedSectionRow;
            r6 = r6 - r11;
            if (r2 == r6) goto L_0x0329;
        L_0x0328:
            r10 = 1;
        L_0x0329:
            r1.setTextAndValueAndIcon(r3, r4, r5, r10);
            goto L_0x08f5;
        L_0x032e:
            r3 = org.telegram.ui.ProfileActivity.this;
            r3 = r3.filesRow;
            if (r2 != r3) goto L_0x0365;
        L_0x0336:
            r3 = NUM; // 0x7f0d045d float:1.874438E38 double:1.0531303294E-314;
            r4 = "FilesDataUsage";
            r3 = org.telegram.messenger.LocaleController.getString(r4, r3);
            r4 = new java.lang.Object[r11];
            r5 = org.telegram.ui.ProfileActivity.this;
            r5 = r5.lastMediaCount;
            r5 = r5[r11];
            r5 = java.lang.Integer.valueOf(r5);
            r4[r10] = r5;
            r4 = java.lang.String.format(r9, r4);
            r5 = NUM; // 0x7var_ float:1.7945817E38 double:1.052935805E-314;
            r6 = org.telegram.ui.ProfileActivity.this;
            r6 = r6.sharedSectionRow;
            r6 = r6 - r11;
            if (r2 == r6) goto L_0x0360;
        L_0x035f:
            r10 = 1;
        L_0x0360:
            r1.setTextAndValueAndIcon(r3, r4, r5, r10);
            goto L_0x08f5;
        L_0x0365:
            r3 = org.telegram.ui.ProfileActivity.this;
            r3 = r3.linksRow;
            if (r2 != r3) goto L_0x039c;
        L_0x036d:
            r3 = NUM; // 0x7f0d0981 float:1.874705E38 double:1.0531309796E-314;
            r5 = "SharedLinks";
            r3 = org.telegram.messenger.LocaleController.getString(r5, r3);
            r5 = new java.lang.Object[r11];
            r6 = org.telegram.ui.ProfileActivity.this;
            r6 = r6.lastMediaCount;
            r4 = r6[r4];
            r4 = java.lang.Integer.valueOf(r4);
            r5[r10] = r4;
            r4 = java.lang.String.format(r9, r5);
            r5 = NUM; // 0x7var_ float:1.7945821E38 double:1.052935806E-314;
            r6 = org.telegram.ui.ProfileActivity.this;
            r6 = r6.sharedSectionRow;
            r6 = r6 - r11;
            if (r2 == r6) goto L_0x0397;
        L_0x0396:
            r10 = 1;
        L_0x0397:
            r1.setTextAndValueAndIcon(r3, r4, r5, r10);
            goto L_0x08f5;
        L_0x039c:
            r3 = org.telegram.ui.ProfileActivity.this;
            r3 = r3.audioRow;
            if (r2 != r3) goto L_0x03d4;
        L_0x03a4:
            r3 = NUM; // 0x7f0d097d float:1.8747041E38 double:1.0531309776E-314;
            r4 = "SharedAudioFiles";
            r3 = org.telegram.messenger.LocaleController.getString(r4, r3);
            r4 = new java.lang.Object[r11];
            r5 = org.telegram.ui.ProfileActivity.this;
            r5 = r5.lastMediaCount;
            r6 = 4;
            r5 = r5[r6];
            r5 = java.lang.Integer.valueOf(r5);
            r4[r10] = r5;
            r4 = java.lang.String.format(r9, r4);
            r5 = NUM; // 0x7var_ float:1.7945813E38 double:1.052935804E-314;
            r6 = org.telegram.ui.ProfileActivity.this;
            r6 = r6.sharedSectionRow;
            r6 = r6 - r11;
            if (r2 == r6) goto L_0x03cf;
        L_0x03ce:
            r10 = 1;
        L_0x03cf:
            r1.setTextAndValueAndIcon(r3, r4, r5, r10);
            goto L_0x08f5;
        L_0x03d4:
            r3 = org.telegram.ui.ProfileActivity.this;
            r3 = r3.voiceRow;
            if (r2 != r3) goto L_0x040b;
        L_0x03dc:
            r3 = NUM; // 0x7f0d0153 float:1.8742802E38 double:1.053129945E-314;
            r4 = "AudioAutodownload";
            r3 = org.telegram.messenger.LocaleController.getString(r4, r3);
            r4 = new java.lang.Object[r11];
            r5 = org.telegram.ui.ProfileActivity.this;
            r5 = r5.lastMediaCount;
            r5 = r5[r7];
            r5 = java.lang.Integer.valueOf(r5);
            r4[r10] = r5;
            r4 = java.lang.String.format(r9, r4);
            r5 = NUM; // 0x7var_b float:1.7945833E38 double:1.052935809E-314;
            r6 = org.telegram.ui.ProfileActivity.this;
            r6 = r6.sharedSectionRow;
            r6 = r6 - r11;
            if (r2 == r6) goto L_0x0406;
        L_0x0405:
            r10 = 1;
        L_0x0406:
            r1.setTextAndValueAndIcon(r3, r4, r5, r10);
            goto L_0x08f5;
        L_0x040b:
            r3 = org.telegram.ui.ProfileActivity.this;
            r3 = r3.groupsInCommonRow;
            r4 = NUM; // 0x7var_d float:1.7944767E38 double:1.052935549E-314;
            if (r2 != r3) goto L_0x0442;
        L_0x0416:
            r3 = NUM; // 0x7f0d04ee float:1.8744674E38 double:1.053130401E-314;
            r5 = "GroupsInCommonTitle";
            r3 = org.telegram.messenger.LocaleController.getString(r5, r3);
            r5 = new java.lang.Object[r11];
            r6 = org.telegram.ui.ProfileActivity.this;
            r6 = r6.userInfo;
            r6 = r6.common_chats_count;
            r6 = java.lang.Integer.valueOf(r6);
            r5[r10] = r6;
            r5 = java.lang.String.format(r9, r5);
            r6 = org.telegram.ui.ProfileActivity.this;
            r6 = r6.sharedSectionRow;
            r6 = r6 - r11;
            if (r2 == r6) goto L_0x043d;
        L_0x043c:
            r10 = 1;
        L_0x043d:
            r1.setTextAndValueAndIcon(r3, r5, r4, r10);
            goto L_0x08f5;
        L_0x0442:
            r3 = org.telegram.ui.ProfileActivity.this;
            r3 = r3.settingsTimerRow;
            if (r2 != r3) goto L_0x0486;
        L_0x044a:
            r2 = org.telegram.ui.ProfileActivity.this;
            r2 = r2.currentAccount;
            r2 = org.telegram.messenger.MessagesController.getInstance(r2);
            r3 = org.telegram.ui.ProfileActivity.this;
            r3 = r3.dialog_id;
            r5 = 32;
            r3 = r3 >> r5;
            r4 = (int) r3;
            r3 = java.lang.Integer.valueOf(r4);
            r2 = r2.getEncryptedChat(r3);
            r2 = r2.ttl;
            if (r2 != 0) goto L_0x0474;
        L_0x046a:
            r2 = NUM; // 0x7f0d098d float:1.8747074E38 double:1.0531309855E-314;
            r3 = "ShortMessageLifetimeForever";
            r2 = org.telegram.messenger.LocaleController.getString(r3, r2);
            goto L_0x0478;
        L_0x0474:
            r2 = org.telegram.messenger.LocaleController.formatTTLString(r2);
        L_0x0478:
            r3 = NUM; // 0x7f0d05cb float:1.8745123E38 double:1.05313051E-314;
            r4 = "MessageLifetime";
            r3 = org.telegram.messenger.LocaleController.getString(r4, r3);
            r1.setTextAndValue(r3, r2, r10);
            goto L_0x08f5;
        L_0x0486:
            r3 = org.telegram.ui.ProfileActivity.this;
            r3 = r3.unblockRow;
            if (r2 != r3) goto L_0x04a2;
        L_0x048e:
            r2 = NUM; // 0x7f0d0a2c float:1.8747396E38 double:1.053131064E-314;
            r3 = "Unblock";
            r2 = org.telegram.messenger.LocaleController.getString(r3, r2);
            r1.setText(r2, r10);
            r2 = "windowBackgroundWhiteRedText5";
            r1.setColors(r8, r2);
            goto L_0x08f5;
        L_0x04a2:
            r3 = org.telegram.ui.ProfileActivity.this;
            r3 = r3.startSecretChatRow;
            if (r2 != r3) goto L_0x04be;
        L_0x04aa:
            r2 = NUM; // 0x7f0d09a8 float:1.8747129E38 double:1.053130999E-314;
            r3 = "StartEncryptedChat";
            r2 = org.telegram.messenger.LocaleController.getString(r3, r2);
            r1.setText(r2, r10);
            r2 = "windowBackgroundWhiteGreenText2";
            r1.setColors(r8, r2);
            goto L_0x08f5;
        L_0x04be:
            r3 = org.telegram.ui.ProfileActivity.this;
            r3 = r3.settingsKeyRow;
            if (r2 != r3) goto L_0x04f8;
        L_0x04c6:
            r2 = new org.telegram.ui.Components.IdenticonDrawable;
            r2.<init>();
            r3 = org.telegram.ui.ProfileActivity.this;
            r3 = r3.currentAccount;
            r3 = org.telegram.messenger.MessagesController.getInstance(r3);
            r4 = org.telegram.ui.ProfileActivity.this;
            r4 = r4.dialog_id;
            r6 = 32;
            r4 = r4 >> r6;
            r5 = (int) r4;
            r4 = java.lang.Integer.valueOf(r5);
            r3 = r3.getEncryptedChat(r4);
            r2.setEncryptedChat(r3);
            r3 = NUM; // 0x7f0d03df float:1.8744125E38 double:1.053130267E-314;
            r4 = "EncryptionKey";
            r3 = org.telegram.messenger.LocaleController.getString(r4, r3);
            r1.setTextAndValueDrawable(r3, r2, r10);
            goto L_0x08f5;
        L_0x04f8:
            r3 = org.telegram.ui.ProfileActivity.this;
            r3 = r3.leaveChannelRow;
            if (r2 != r3) goto L_0x0514;
        L_0x0500:
            r2 = "windowBackgroundWhiteRedText5";
            r1.setColors(r8, r2);
            r2 = NUM; // 0x7f0d0563 float:1.8744912E38 double:1.053130459E-314;
            r3 = "LeaveChannel";
            r2 = org.telegram.messenger.LocaleController.getString(r3, r2);
            r1.setText(r2, r10);
            goto L_0x08f5;
        L_0x0514:
            r3 = org.telegram.ui.ProfileActivity.this;
            r3 = r3.joinRow;
            if (r2 != r3) goto L_0x0552;
        L_0x051c:
            r2 = "windowBackgroundWhiteBlueText2";
            r1.setColors(r8, r2);
            r2 = "windowBackgroundWhiteBlueText2";
            r2 = org.telegram.ui.ActionBar.Theme.getColor(r2);
            r1.setTextColor(r2);
            r2 = org.telegram.ui.ProfileActivity.this;
            r2 = r2.currentChat;
            r2 = r2.megagroup;
            if (r2 == 0) goto L_0x0544;
        L_0x0536:
            r2 = NUM; // 0x7f0d087d float:1.8746522E38 double:1.053130851E-314;
            r3 = "ProfileJoinGroup";
            r2 = org.telegram.messenger.LocaleController.getString(r3, r2);
            r1.setText(r2, r10);
            goto L_0x08f5;
        L_0x0544:
            r2 = NUM; // 0x7f0d087c float:1.874652E38 double:1.0531308507E-314;
            r3 = "ProfileJoinChannel";
            r2 = org.telegram.messenger.LocaleController.getString(r3, r2);
            r1.setText(r2, r10);
            goto L_0x08f5;
        L_0x0552:
            r3 = org.telegram.ui.ProfileActivity.this;
            r3 = r3.subscribersRow;
            if (r2 != r3) goto L_0x060c;
        L_0x055a:
            r3 = org.telegram.ui.ProfileActivity.this;
            r3 = r3.chatInfo;
            if (r3 == 0) goto L_0x05cb;
        L_0x0562:
            r3 = org.telegram.ui.ProfileActivity.this;
            r3 = r3.currentChat;
            r3 = org.telegram.messenger.ChatObject.isChannel(r3);
            if (r3 == 0) goto L_0x05a4;
        L_0x056e:
            r3 = org.telegram.ui.ProfileActivity.this;
            r3 = r3.currentChat;
            r3 = r3.megagroup;
            if (r3 != 0) goto L_0x05a4;
        L_0x0578:
            r3 = NUM; // 0x7f0d0269 float:1.8743366E38 double:1.0531300824E-314;
            r5 = "ChannelSubscribers";
            r3 = org.telegram.messenger.LocaleController.getString(r5, r3);
            r5 = new java.lang.Object[r11];
            r6 = org.telegram.ui.ProfileActivity.this;
            r6 = r6.chatInfo;
            r6 = r6.participants_count;
            r6 = java.lang.Integer.valueOf(r6);
            r5[r10] = r6;
            r5 = java.lang.String.format(r9, r5);
            r6 = org.telegram.ui.ProfileActivity.this;
            r6 = r6.membersSectionRow;
            r6 = r6 - r11;
            if (r2 == r6) goto L_0x059f;
        L_0x059e:
            r10 = 1;
        L_0x059f:
            r1.setTextAndValueAndIcon(r3, r5, r4, r10);
            goto L_0x08f5;
        L_0x05a4:
            r3 = org.telegram.messenger.LocaleController.getString(r6, r5);
            r5 = new java.lang.Object[r11];
            r6 = org.telegram.ui.ProfileActivity.this;
            r6 = r6.chatInfo;
            r6 = r6.participants_count;
            r6 = java.lang.Integer.valueOf(r6);
            r5[r10] = r6;
            r5 = java.lang.String.format(r9, r5);
            r6 = org.telegram.ui.ProfileActivity.this;
            r6 = r6.membersSectionRow;
            r6 = r6 - r11;
            if (r2 == r6) goto L_0x05c6;
        L_0x05c5:
            r10 = 1;
        L_0x05c6:
            r1.setTextAndValueAndIcon(r3, r5, r4, r10);
            goto L_0x08f5;
        L_0x05cb:
            r3 = org.telegram.ui.ProfileActivity.this;
            r3 = r3.currentChat;
            r3 = org.telegram.messenger.ChatObject.isChannel(r3);
            if (r3 == 0) goto L_0x05f9;
        L_0x05d7:
            r3 = org.telegram.ui.ProfileActivity.this;
            r3 = r3.currentChat;
            r3 = r3.megagroup;
            if (r3 != 0) goto L_0x05f9;
        L_0x05e1:
            r3 = NUM; // 0x7f0d0269 float:1.8743366E38 double:1.0531300824E-314;
            r5 = "ChannelSubscribers";
            r3 = org.telegram.messenger.LocaleController.getString(r5, r3);
            r5 = org.telegram.ui.ProfileActivity.this;
            r5 = r5.membersSectionRow;
            r5 = r5 - r11;
            if (r2 == r5) goto L_0x05f4;
        L_0x05f3:
            r10 = 1;
        L_0x05f4:
            r1.setTextAndIcon(r3, r4, r10);
            goto L_0x08f5;
        L_0x05f9:
            r3 = org.telegram.messenger.LocaleController.getString(r6, r5);
            r5 = org.telegram.ui.ProfileActivity.this;
            r5 = r5.membersSectionRow;
            r5 = r5 - r11;
            if (r2 == r5) goto L_0x0607;
        L_0x0606:
            r10 = 1;
        L_0x0607:
            r1.setTextAndIcon(r3, r4, r10);
            goto L_0x08f5;
        L_0x060c:
            r3 = org.telegram.ui.ProfileActivity.this;
            r3 = r3.administratorsRow;
            if (r2 != r3) goto L_0x0666;
        L_0x0614:
            r3 = org.telegram.ui.ProfileActivity.this;
            r3 = r3.chatInfo;
            if (r3 == 0) goto L_0x064b;
        L_0x061c:
            r3 = NUM; // 0x7f0d021c float:1.874321E38 double:1.0531300443E-314;
            r4 = "ChannelAdministrators";
            r3 = org.telegram.messenger.LocaleController.getString(r4, r3);
            r4 = new java.lang.Object[r11];
            r5 = org.telegram.ui.ProfileActivity.this;
            r5 = r5.chatInfo;
            r5 = r5.admins_count;
            r5 = java.lang.Integer.valueOf(r5);
            r4[r10] = r5;
            r4 = java.lang.String.format(r9, r4);
            r5 = NUM; // 0x7var_ float:1.7944754E38 double:1.052935546E-314;
            r6 = org.telegram.ui.ProfileActivity.this;
            r6 = r6.membersSectionRow;
            r6 = r6 - r11;
            if (r2 == r6) goto L_0x0646;
        L_0x0645:
            r10 = 1;
        L_0x0646:
            r1.setTextAndValueAndIcon(r3, r4, r5, r10);
            goto L_0x08f5;
        L_0x064b:
            r3 = NUM; // 0x7f0d021c float:1.874321E38 double:1.0531300443E-314;
            r4 = "ChannelAdministrators";
            r3 = org.telegram.messenger.LocaleController.getString(r4, r3);
            r4 = NUM; // 0x7var_ float:1.7944754E38 double:1.052935546E-314;
            r5 = org.telegram.ui.ProfileActivity.this;
            r5 = r5.membersSectionRow;
            r5 = r5 - r11;
            if (r2 == r5) goto L_0x0661;
        L_0x0660:
            r10 = 1;
        L_0x0661:
            r1.setTextAndIcon(r3, r4, r10);
            goto L_0x08f5;
        L_0x0666:
            r3 = org.telegram.ui.ProfileActivity.this;
            r3 = r3.blockedUsersRow;
            if (r2 != r3) goto L_0x06cc;
        L_0x066e:
            r3 = org.telegram.ui.ProfileActivity.this;
            r3 = r3.chatInfo;
            if (r3 == 0) goto L_0x06b1;
        L_0x0676:
            r3 = NUM; // 0x7f0d0221 float:1.874322E38 double:1.053130047E-314;
            r4 = "ChannelBlacklist";
            r3 = org.telegram.messenger.LocaleController.getString(r4, r3);
            r4 = new java.lang.Object[r11];
            r5 = org.telegram.ui.ProfileActivity.this;
            r5 = r5.chatInfo;
            r5 = r5.banned_count;
            r6 = org.telegram.ui.ProfileActivity.this;
            r6 = r6.chatInfo;
            r6 = r6.kicked_count;
            r5 = java.lang.Math.max(r5, r6);
            r5 = java.lang.Integer.valueOf(r5);
            r4[r10] = r5;
            r4 = java.lang.String.format(r9, r4);
            r5 = NUM; // 0x7var_b float:1.7944763E38 double:1.052935548E-314;
            r6 = org.telegram.ui.ProfileActivity.this;
            r6 = r6.membersSectionRow;
            r6 = r6 - r11;
            if (r2 == r6) goto L_0x06ac;
        L_0x06ab:
            r10 = 1;
        L_0x06ac:
            r1.setTextAndValueAndIcon(r3, r4, r5, r10);
            goto L_0x08f5;
        L_0x06b1:
            r3 = NUM; // 0x7f0d0221 float:1.874322E38 double:1.053130047E-314;
            r4 = "ChannelBlacklist";
            r3 = org.telegram.messenger.LocaleController.getString(r4, r3);
            r4 = NUM; // 0x7var_b float:1.7944763E38 double:1.052935548E-314;
            r5 = org.telegram.ui.ProfileActivity.this;
            r5 = r5.membersSectionRow;
            r5 = r5 - r11;
            if (r2 == r5) goto L_0x06c7;
        L_0x06c6:
            r10 = 1;
        L_0x06c7:
            r1.setTextAndIcon(r3, r4, r10);
            goto L_0x08f5;
        L_0x06cc:
            r3 = org.telegram.ui.ProfileActivity.this;
            r3 = r3.addMemberRow;
            if (r2 != r3) goto L_0x08f5;
        L_0x06d4:
            r2 = "windowBackgroundWhiteBlueIcon";
            r3 = "windowBackgroundWhiteBlueButton";
            r1.setColors(r2, r3);
            r2 = org.telegram.ui.ProfileActivity.this;
            r2 = r2.chat_id;
            if (r2 <= 0) goto L_0x06f6;
        L_0x06e5:
            r2 = NUM; // 0x7f0d00af float:1.874247E38 double:1.053129864E-314;
            r3 = "AddMember";
            r2 = org.telegram.messenger.LocaleController.getString(r3, r2);
            r3 = NUM; // 0x7var_ float:1.7944756E38 double:1.0529355465E-314;
            r1.setTextAndIcon(r2, r3, r11);
            goto L_0x08f5;
        L_0x06f6:
            r2 = NUM; // 0x7f0d00b9 float:1.874249E38 double:1.053129869E-314;
            r3 = "AddRecipient";
            r2 = org.telegram.messenger.LocaleController.getString(r3, r2);
            r3 = NUM; // 0x7var_ float:1.7944756E38 double:1.0529355465E-314;
            r1.setTextAndIcon(r2, r3, r11);
            goto L_0x08f5;
        L_0x0707:
            r1 = r1.itemView;
            r1 = (org.telegram.ui.Cells.AboutLinkCell) r1;
            r3 = org.telegram.ui.ProfileActivity.this;
            r3 = r3.userInfoRow;
            if (r2 != r3) goto L_0x072f;
        L_0x0713:
            r2 = org.telegram.ui.ProfileActivity.this;
            r2 = r2.userInfo;
            r2 = r2.about;
            r3 = NUM; // 0x7f0d0a64 float:1.874751E38 double:1.053131092E-314;
            r4 = "UserBio";
            r3 = org.telegram.messenger.LocaleController.getString(r4, r3);
            r4 = org.telegram.ui.ProfileActivity.this;
            r4 = r4.isBot;
            r1.setTextAndValue(r2, r3, r4);
            goto L_0x08f5;
        L_0x072f:
            r3 = org.telegram.ui.ProfileActivity.this;
            r3 = r3.channelInfoRow;
            if (r2 != r3) goto L_0x08f5;
        L_0x0737:
            r2 = org.telegram.ui.ProfileActivity.this;
            r2 = r2.chatInfo;
            r2 = r2.about;
        L_0x073f:
            r3 = "\n\n\n";
            r4 = r2.contains(r3);
            if (r4 == 0) goto L_0x074e;
        L_0x0747:
            r4 = "\n\n";
            r2 = r2.replace(r3, r4);
            goto L_0x073f;
        L_0x074e:
            r1.setText(r2, r11);
            goto L_0x08f5;
        L_0x0753:
            r1 = r1.itemView;
            r1 = (org.telegram.ui.Cells.TextDetailCell) r1;
            r3 = org.telegram.ui.ProfileActivity.this;
            r3 = r3.phoneRow;
            if (r2 != r3) goto L_0x07b2;
        L_0x075f:
            r2 = org.telegram.ui.ProfileActivity.this;
            r2 = r2.currentAccount;
            r2 = org.telegram.messenger.MessagesController.getInstance(r2);
            r3 = org.telegram.ui.ProfileActivity.this;
            r3 = r3.user_id;
            r3 = java.lang.Integer.valueOf(r3);
            r2 = r2.getUser(r3);
            r3 = r2.phone;
            r3 = android.text.TextUtils.isEmpty(r3);
            if (r3 != 0) goto L_0x079b;
        L_0x077f:
            r3 = org.telegram.PhoneFormat.PhoneFormat.getInstance();
            r4 = new java.lang.StringBuilder;
            r4.<init>();
            r5 = "+";
            r4.append(r5);
            r2 = r2.phone;
            r4.append(r2);
            r2 = r4.toString();
            r2 = r3.format(r2);
            goto L_0x07a4;
        L_0x079b:
            r2 = NUM; // 0x7f0d0821 float:1.8746336E38 double:1.0531308057E-314;
            r3 = "PhoneHidden";
            r2 = org.telegram.messenger.LocaleController.getString(r3, r2);
        L_0x07a4:
            r3 = NUM; // 0x7f0d0824 float:1.8746342E38 double:1.053130807E-314;
            r4 = "PhoneMobile";
            r3 = org.telegram.messenger.LocaleController.getString(r4, r3);
            r1.setTextAndValue(r2, r3, r10);
            goto L_0x08f5;
        L_0x07b2:
            r3 = org.telegram.ui.ProfileActivity.this;
            r3 = r3.usernameRow;
            if (r2 != r3) goto L_0x0858;
        L_0x07ba:
            r2 = org.telegram.ui.ProfileActivity.this;
            r2 = r2.user_id;
            if (r2 == 0) goto L_0x0808;
        L_0x07c2:
            r2 = org.telegram.ui.ProfileActivity.this;
            r2 = r2.currentAccount;
            r2 = org.telegram.messenger.MessagesController.getInstance(r2);
            r3 = org.telegram.ui.ProfileActivity.this;
            r3 = r3.user_id;
            r3 = java.lang.Integer.valueOf(r3);
            r2 = r2.getUser(r3);
            if (r2 == 0) goto L_0x07f8;
        L_0x07dc:
            r3 = r2.username;
            r3 = android.text.TextUtils.isEmpty(r3);
            if (r3 != 0) goto L_0x07f8;
        L_0x07e4:
            r3 = new java.lang.StringBuilder;
            r3.<init>();
            r4 = "@";
            r3.append(r4);
            r2 = r2.username;
            r3.append(r2);
            r2 = r3.toString();
            goto L_0x07fa;
        L_0x07f8:
            r2 = "-";
        L_0x07fa:
            r3 = NUM; // 0x7f0d0a86 float:1.8747579E38 double:1.0531311086E-314;
            r4 = "Username";
            r3 = org.telegram.messenger.LocaleController.getString(r4, r3);
            r1.setTextAndValue(r2, r3, r10);
            goto L_0x08f5;
        L_0x0808:
            r2 = org.telegram.ui.ProfileActivity.this;
            r2 = r2.currentChat;
            if (r2 == 0) goto L_0x08f5;
        L_0x0810:
            r2 = org.telegram.ui.ProfileActivity.this;
            r2 = r2.currentAccount;
            r2 = org.telegram.messenger.MessagesController.getInstance(r2);
            r3 = org.telegram.ui.ProfileActivity.this;
            r3 = r3.chat_id;
            r3 = java.lang.Integer.valueOf(r3);
            r2 = r2.getChat(r3);
            r3 = new java.lang.StringBuilder;
            r3.<init>();
            r4 = org.telegram.ui.ProfileActivity.this;
            r4 = r4.currentAccount;
            r4 = org.telegram.messenger.MessagesController.getInstance(r4);
            r4 = r4.linkPrefix;
            r3.append(r4);
            r4 = "/";
            r3.append(r4);
            r2 = r2.username;
            r3.append(r2);
            r2 = r3.toString();
            r3 = NUM; // 0x7f0d0522 float:1.874478E38 double:1.0531304267E-314;
            r4 = "InviteLink";
            r3 = org.telegram.messenger.LocaleController.getString(r4, r3);
            r1.setTextAndValue(r2, r3, r10);
            goto L_0x08f5;
        L_0x0858:
            r3 = org.telegram.ui.ProfileActivity.this;
            r3 = r3.locationRow;
            if (r2 != r3) goto L_0x08f5;
        L_0x0860:
            r2 = org.telegram.ui.ProfileActivity.this;
            r2 = r2.chatInfo;
            if (r2 == 0) goto L_0x08f5;
        L_0x0868:
            r2 = org.telegram.ui.ProfileActivity.this;
            r2 = r2.chatInfo;
            r2 = r2.location;
            r2 = r2 instanceof org.telegram.tgnet.TLRPC.TL_channelLocation;
            if (r2 == 0) goto L_0x08f5;
        L_0x0874:
            r2 = org.telegram.ui.ProfileActivity.this;
            r2 = r2.chatInfo;
            r2 = r2.location;
            r2 = (org.telegram.tgnet.TLRPC.TL_channelLocation) r2;
            r2 = r2.address;
            r3 = NUM; // 0x7f0d0147 float:1.8742778E38 double:1.053129939E-314;
            r4 = "AttachLocation";
            r3 = org.telegram.messenger.LocaleController.getString(r4, r3);
            r1.setTextAndValue(r2, r3, r10);
            goto L_0x08f5;
        L_0x088d:
            r1 = r1.itemView;
            r1 = (org.telegram.ui.Cells.HeaderCell) r1;
            r3 = org.telegram.ui.ProfileActivity.this;
            r3 = r3.infoHeaderRow;
            if (r2 != r3) goto L_0x08d1;
        L_0x0899:
            r2 = org.telegram.ui.ProfileActivity.this;
            r2 = r2.currentChat;
            r2 = org.telegram.messenger.ChatObject.isChannel(r2);
            if (r2 == 0) goto L_0x08c4;
        L_0x08a5:
            r2 = org.telegram.ui.ProfileActivity.this;
            r2 = r2.currentChat;
            r2 = r2.megagroup;
            if (r2 != 0) goto L_0x08c4;
        L_0x08af:
            r2 = org.telegram.ui.ProfileActivity.this;
            r2 = r2.channelInfoRow;
            if (r2 == r9) goto L_0x08c4;
        L_0x08b7:
            r2 = NUM; // 0x7f0d08b7 float:1.874664E38 double:1.05313088E-314;
            r3 = "ReportChatDescription";
            r2 = org.telegram.messenger.LocaleController.getString(r3, r2);
            r1.setText(r2);
            goto L_0x08f5;
        L_0x08c4:
            r2 = NUM; // 0x7f0d0514 float:1.8744751E38 double:1.05313042E-314;
            r3 = "Info";
            r2 = org.telegram.messenger.LocaleController.getString(r3, r2);
            r1.setText(r2);
            goto L_0x08f5;
        L_0x08d1:
            r3 = org.telegram.ui.ProfileActivity.this;
            r3 = r3.sharedHeaderRow;
            if (r2 != r3) goto L_0x08e6;
        L_0x08d9:
            r2 = NUM; // 0x7f0d097e float:1.8747043E38 double:1.053130978E-314;
            r3 = "SharedContent";
            r2 = org.telegram.messenger.LocaleController.getString(r3, r2);
            r1.setText(r2);
            goto L_0x08f5;
        L_0x08e6:
            r3 = org.telegram.ui.ProfileActivity.this;
            r3 = r3.membersHeaderRow;
            if (r2 != r3) goto L_0x08f5;
        L_0x08ee:
            r2 = org.telegram.messenger.LocaleController.getString(r6, r5);
            r1.setText(r2);
        L_0x08f5:
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ProfileActivity$ListAdapter.onBindViewHolder(androidx.recyclerview.widget.RecyclerView$ViewHolder, int):void");
        }

        public boolean isEnabled(ViewHolder viewHolder) {
            int itemViewType = viewHolder.getItemViewType();
            return (itemViewType == 1 || itemViewType == 5 || itemViewType == 7 || itemViewType == 9 || itemViewType == 10 || itemViewType == 11) ? false : true;
        }

        public int getItemCount() {
            return ProfileActivity.this.rowCount;
        }

        public int getItemViewType(int i) {
            if (i == ProfileActivity.this.infoHeaderRow || i == ProfileActivity.this.sharedHeaderRow || i == ProfileActivity.this.membersHeaderRow) {
                return 1;
            }
            if (i == ProfileActivity.this.phoneRow || i == ProfileActivity.this.usernameRow || i == ProfileActivity.this.locationRow) {
                return 2;
            }
            if (i == ProfileActivity.this.userInfoRow || i == ProfileActivity.this.channelInfoRow) {
                return 3;
            }
            if (i == ProfileActivity.this.settingsTimerRow || i == ProfileActivity.this.settingsKeyRow || i == ProfileActivity.this.photosRow || i == ProfileActivity.this.filesRow || i == ProfileActivity.this.linksRow || i == ProfileActivity.this.audioRow || i == ProfileActivity.this.voiceRow || i == ProfileActivity.this.groupsInCommonRow || i == ProfileActivity.this.startSecretChatRow || i == ProfileActivity.this.subscribersRow || i == ProfileActivity.this.administratorsRow || i == ProfileActivity.this.blockedUsersRow || i == ProfileActivity.this.leaveChannelRow || i == ProfileActivity.this.addMemberRow || i == ProfileActivity.this.joinRow || i == ProfileActivity.this.unblockRow) {
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
            if (i < ProfileActivity.this.membersStartRow || i >= ProfileActivity.this.membersEndRow) {
                return i == ProfileActivity.this.emptyRow ? 11 : 0;
            } else {
                return 8;
            }
        }
    }

    public ProfileActivity(Bundle bundle) {
        super(bundle);
    }

    public boolean onFragmentCreate() {
        this.user_id = this.arguments.getInt("user_id", 0);
        this.chat_id = this.arguments.getInt("chat_id", 0);
        this.banFromGroup = this.arguments.getInt("ban_chat_id", 0);
        this.reportSpam = this.arguments.getBoolean("reportSpam", false);
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
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.contactsDidLoad);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.encryptedChatCreated);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.encryptedChatUpdated);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.blockedUsersDidLoad);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.botInfoDidLoad);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.userInfoDidLoad);
            this.userBlocked = MessagesController.getInstance(this.currentAccount).blockedUsers.indexOfKey(this.user_id) >= 0;
            if (user.bot) {
                this.isBot = true;
                MediaDataController.getInstance(this.currentAccount).loadBotInfo(user.id, true, this.classGuid);
            }
            this.userInfo = MessagesController.getInstance(this.currentAccount).getUserFull(this.user_id);
            MessagesController.getInstance(this.currentAccount).loadFullUser(MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.user_id)), this.classGuid, true);
            this.participantsMap = null;
        } else if (this.chat_id == 0) {
            return false;
        } else {
            this.currentChat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.chat_id));
            if (this.currentChat == null) {
                CountDownLatch countDownLatch = new CountDownLatch(1);
                MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new -$$Lambda$ProfileActivity$ELtZVLkMORk4xUSfHIm0LgPewE8(this, countDownLatch));
                try {
                    countDownLatch.await();
                } catch (Exception e) {
                    FileLog.e(e);
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
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.chatOnlineCountDidLoad);
            this.sortedUsers = new ArrayList();
            updateOnlineCount();
            if (ChatObject.isChannel(this.currentChat)) {
                MessagesController.getInstance(this.currentAccount).loadFullChat(this.chat_id, this.classGuid, true);
            } else if (this.chatInfo == null) {
                MessagesController.getInstance(this.currentAccount).loadChatInfo(this.chat_id, null, false);
            }
            if (this.chatInfo == null) {
                this.chatInfo = getMessagesController().getChatFull(this.chat_id);
            }
        }
        this.sharedMediaData = new SharedMediaData[5];
        int i = 0;
        while (true) {
            SharedMediaData[] sharedMediaDataArr = this.sharedMediaData;
            if (i < sharedMediaDataArr.length) {
                sharedMediaDataArr[i] = new SharedMediaData();
                this.sharedMediaData[i].setMaxId(0, this.dialog_id != 0 ? Integer.MIN_VALUE : Integer.MAX_VALUE);
                i++;
            } else {
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
        }
    }

    public /* synthetic */ void lambda$onFragmentCreate$0$ProfileActivity(CountDownLatch countDownLatch) {
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
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.chatOnlineCountDidLoad);
        }
    }

    /* Access modifiers changed, original: protected */
    public ActionBar createActionBar(Context context) {
        AnonymousClass2 anonymousClass2 = new ActionBar(context) {
            public boolean onTouchEvent(MotionEvent motionEvent) {
                return super.onTouchEvent(motionEvent);
            }
        };
        int i = (this.user_id != 0 || (ChatObject.isChannel(this.chat_id, this.currentAccount) && !this.currentChat.megagroup)) ? 5 : this.chat_id;
        boolean z = false;
        anonymousClass2.setItemsBackgroundColor(AvatarDrawable.getButtonColorForId(i), false);
        anonymousClass2.setItemsColor(Theme.getColor("actionBarDefaultIcon"), false);
        anonymousClass2.setItemsColor(Theme.getColor("actionBarActionModeDefaultIcon"), true);
        anonymousClass2.setBackButtonDrawable(new BackDrawable(false));
        anonymousClass2.setCastShadows(false);
        anonymousClass2.setAddToContainer(false);
        if (VERSION.SDK_INT >= 21 && !AndroidUtilities.isTablet()) {
            z = true;
        }
        anonymousClass2.setOccupyStatusBar(z);
        return anonymousClass2;
    }

    public View createView(Context context) {
        Context context2 = context;
        Theme.createProfileResources(context);
        this.hasOwnBackground = true;
        this.extraHeight = AndroidUtilities.dp(88.0f);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                int i2 = i;
                if (ProfileActivity.this.getParentActivity() != null) {
                    if (i2 == -1) {
                        ProfileActivity.this.finishFragment();
                    } else {
                        String str = "dialogTextRed2";
                        String str2 = "Cancel";
                        AlertDialog create;
                        TextView textView;
                        if (i2 == 2) {
                            User user = MessagesController.getInstance(ProfileActivity.this.currentAccount).getUser(Integer.valueOf(ProfileActivity.this.user_id));
                            if (user != null) {
                                if (!ProfileActivity.this.isBot || MessagesController.isSupportUser(user)) {
                                    if (ProfileActivity.this.userBlocked) {
                                        MessagesController.getInstance(ProfileActivity.this.currentAccount).unblockUser(ProfileActivity.this.user_id);
                                        AlertsCreator.showSimpleToast(ProfileActivity.this, LocaleController.getString("UserUnblocked", NUM));
                                    } else if (ProfileActivity.this.reportSpam) {
                                        ProfileActivity profileActivity = ProfileActivity.this;
                                        AlertsCreator.showBlockReportSpamAlert(profileActivity, (long) profileActivity.user_id, user, null, ProfileActivity.this.currentEncryptedChat, false, null, new -$$Lambda$ProfileActivity$3$RRzyGWhwzPxDafj4prZmgViMsEY(this));
                                    } else {
                                        Builder builder = new Builder(ProfileActivity.this.getParentActivity());
                                        builder.setTitle(LocaleController.getString("BlockUser", NUM));
                                        builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("AreYouSureBlockContact2", NUM, ContactsController.formatName(user.first_name, user.last_name))));
                                        builder.setPositiveButton(LocaleController.getString("BlockContact", NUM), new -$$Lambda$ProfileActivity$3$Nq0KnTbSQV8YnWB2bdQtiwIOT-Q(this));
                                        builder.setNegativeButton(LocaleController.getString(str2, NUM), null);
                                        create = builder.create();
                                        ProfileActivity.this.showDialog(create);
                                        textView = (TextView) create.getButton(-1);
                                        if (textView != null) {
                                            textView.setTextColor(Theme.getColor(str));
                                        }
                                    }
                                } else if (ProfileActivity.this.userBlocked) {
                                    MessagesController.getInstance(ProfileActivity.this.currentAccount).unblockUser(ProfileActivity.this.user_id);
                                    SendMessagesHelper.getInstance(ProfileActivity.this.currentAccount).sendMessage("/start", (long) ProfileActivity.this.user_id, null, null, false, null, null, null);
                                    ProfileActivity.this.finishFragment();
                                } else {
                                    MessagesController.getInstance(ProfileActivity.this.currentAccount).blockUser(ProfileActivity.this.user_id);
                                }
                            } else {
                                return;
                            }
                        }
                        String str3 = "user_id";
                        User user2;
                        Bundle bundle;
                        if (i2 == 1) {
                            user2 = MessagesController.getInstance(ProfileActivity.this.currentAccount).getUser(Integer.valueOf(ProfileActivity.this.user_id));
                            bundle = new Bundle();
                            bundle.putInt(str3, user2.id);
                            bundle.putBoolean("addContact", true);
                            ProfileActivity.this.presentFragment(new ContactAddActivity(bundle));
                        } else {
                            String str4 = "onlySelect";
                            Bundle bundle2;
                            if (i2 == 3) {
                                bundle2 = new Bundle();
                                bundle2.putBoolean(str4, true);
                                bundle2.putString("selectAlertString", LocaleController.getString("SendContactTo", NUM));
                                bundle2.putString("selectAlertStringGroup", LocaleController.getString("SendContactToGroup", NUM));
                                DialogsActivity dialogsActivity = new DialogsActivity(bundle2);
                                dialogsActivity.setDelegate(ProfileActivity.this);
                                ProfileActivity.this.presentFragment(dialogsActivity);
                            } else if (i2 == 4) {
                                bundle2 = new Bundle();
                                bundle2.putInt(str3, ProfileActivity.this.user_id);
                                ProfileActivity.this.presentFragment(new ContactAddActivity(bundle2));
                            } else if (i2 == 5) {
                                user2 = MessagesController.getInstance(ProfileActivity.this.currentAccount).getUser(Integer.valueOf(ProfileActivity.this.user_id));
                                if (user2 != null && ProfileActivity.this.getParentActivity() != null) {
                                    Builder builder2 = new Builder(ProfileActivity.this.getParentActivity());
                                    builder2.setTitle(LocaleController.getString("DeleteContact", NUM));
                                    builder2.setMessage(LocaleController.getString("AreYouSureDeleteContact", NUM));
                                    builder2.setPositiveButton(LocaleController.getString("Delete", NUM), new -$$Lambda$ProfileActivity$3$CKTfTi-3YZbYfut7QOeW7eLPwNY(this, user2));
                                    builder2.setNegativeButton(LocaleController.getString(str2, NUM), null);
                                    create = builder2.create();
                                    ProfileActivity.this.showDialog(create);
                                    textView = (TextView) create.getButton(-1);
                                    if (textView != null) {
                                        textView.setTextColor(Theme.getColor(str));
                                    }
                                }
                            } else if (i2 == 7) {
                                ProfileActivity.this.leaveChatPressed();
                            } else {
                                str = "chat_id";
                                if (i2 == 12) {
                                    bundle2 = new Bundle();
                                    bundle2.putInt(str, ProfileActivity.this.chat_id);
                                    ChatEditActivity chatEditActivity = new ChatEditActivity(bundle2);
                                    chatEditActivity.setInfo(ProfileActivity.this.chatInfo);
                                    ProfileActivity.this.presentFragment(chatEditActivity);
                                } else if (i2 == 9) {
                                    user2 = MessagesController.getInstance(ProfileActivity.this.currentAccount).getUser(Integer.valueOf(ProfileActivity.this.user_id));
                                    if (user2 != null) {
                                        bundle = new Bundle();
                                        bundle.putBoolean(str4, true);
                                        bundle.putInt("dialogsType", 2);
                                        bundle.putString("addToGroupAlertString", LocaleController.formatString("AddToTheGroupTitle", NUM, UserObject.getUserName(user2), "%1$s"));
                                        DialogsActivity dialogsActivity2 = new DialogsActivity(bundle);
                                        dialogsActivity2.setDelegate(new -$$Lambda$ProfileActivity$3$fjMhx-P1BMqbwH45pN5jgfN8nNI(this, user2));
                                        ProfileActivity.this.presentFragment(dialogsActivity2);
                                    }
                                } else if (i2 == 10) {
                                    try {
                                        if (MessagesController.getInstance(ProfileActivity.this.currentAccount).getUser(Integer.valueOf(ProfileActivity.this.user_id)) != null) {
                                            StringBuilder stringBuilder;
                                            Intent intent = new Intent("android.intent.action.SEND");
                                            intent.setType("text/plain");
                                            String str5 = "/%s";
                                            String str6 = "android.intent.extra.TEXT";
                                            if (ProfileActivity.this.botInfo != null) {
                                                if (!(ProfileActivity.this.userInfo == null || TextUtils.isEmpty(ProfileActivity.this.userInfo.about))) {
                                                    stringBuilder = new StringBuilder();
                                                    stringBuilder.append("%s https://");
                                                    stringBuilder.append(MessagesController.getInstance(ProfileActivity.this.currentAccount).linkPrefix);
                                                    stringBuilder.append(str5);
                                                    intent.putExtra(str6, String.format(stringBuilder.toString(), new Object[]{ProfileActivity.this.userInfo.about, user2.username}));
                                                    ProfileActivity.this.startActivityForResult(Intent.createChooser(intent, LocaleController.getString("BotShare", NUM)), 500);
                                                }
                                            }
                                            stringBuilder = new StringBuilder();
                                            stringBuilder.append("https://");
                                            stringBuilder.append(MessagesController.getInstance(ProfileActivity.this.currentAccount).linkPrefix);
                                            stringBuilder.append(str5);
                                            intent.putExtra(str6, String.format(stringBuilder.toString(), new Object[]{user2.username}));
                                            ProfileActivity.this.startActivityForResult(Intent.createChooser(intent, LocaleController.getString("BotShare", NUM)), 500);
                                        }
                                    } catch (Exception e) {
                                        FileLog.e(e);
                                    }
                                } else if (i2 == 14) {
                                    try {
                                        long j;
                                        if (ProfileActivity.this.currentEncryptedChat != null) {
                                            j = ((long) ProfileActivity.this.currentEncryptedChat.id) << 32;
                                        } else {
                                            if (ProfileActivity.this.user_id != 0) {
                                                i2 = ProfileActivity.this.user_id;
                                            } else if (ProfileActivity.this.chat_id != 0) {
                                                i2 = -ProfileActivity.this.chat_id;
                                            } else {
                                                return;
                                            }
                                            j = (long) i2;
                                        }
                                        MediaDataController.getInstance(ProfileActivity.this.currentAccount).installShortcut(j);
                                    } catch (Exception e2) {
                                        FileLog.e(e2);
                                    }
                                } else if (i2 == 15) {
                                    user2 = MessagesController.getInstance(ProfileActivity.this.currentAccount).getUser(Integer.valueOf(ProfileActivity.this.user_id));
                                    if (user2 != null) {
                                        VoIPHelper.startCall(user2, ProfileActivity.this.getParentActivity(), ProfileActivity.this.userInfo);
                                    }
                                } else if (i2 == 17) {
                                    bundle2 = new Bundle();
                                    bundle2.putInt(str, ProfileActivity.this.chat_id);
                                    bundle2.putInt("type", 2);
                                    bundle2.putBoolean("open_search", true);
                                    ChatUsersActivity chatUsersActivity = new ChatUsersActivity(bundle2);
                                    chatUsersActivity.setInfo(ProfileActivity.this.chatInfo);
                                    ProfileActivity.this.presentFragment(chatUsersActivity);
                                } else if (i2 == 18) {
                                    ProfileActivity.this.openAddMember();
                                } else if (i2 == 19) {
                                    if (ProfileActivity.this.user_id != 0) {
                                        i2 = ProfileActivity.this.user_id;
                                    } else {
                                        i2 = -ProfileActivity.this.chat_id;
                                    }
                                    AlertDialog[] alertDialogArr = new AlertDialog[]{new AlertDialog(ProfileActivity.this.getParentActivity(), 3)};
                                    TL_messages_getStatsURL tL_messages_getStatsURL = new TL_messages_getStatsURL();
                                    tL_messages_getStatsURL.peer = MessagesController.getInstance(ProfileActivity.this.currentAccount).getInputPeer(i2);
                                    tL_messages_getStatsURL.dark = Theme.getCurrentTheme().isDark();
                                    tL_messages_getStatsURL.params = "";
                                    i2 = ConnectionsManager.getInstance(ProfileActivity.this.currentAccount).sendRequest(tL_messages_getStatsURL, new -$$Lambda$ProfileActivity$3$10b11ADJG6tYq3lPsRhNkeDms-U(this, alertDialogArr));
                                    if (alertDialogArr[0] != null) {
                                        alertDialogArr[0].setOnCancelListener(new -$$Lambda$ProfileActivity$3$deF4lbNc0vIGfr_kxrQx_2fEO-A(this, i2));
                                        ProfileActivity.this.showDialog(alertDialogArr[0]);
                                    }
                                }
                            }
                        }
                    }
                }
            }

            public /* synthetic */ void lambda$onItemClick$0$ProfileActivity$3(int i) {
                if (i == 1) {
                    NotificationCenter.getInstance(ProfileActivity.this.currentAccount).removeObserver(ProfileActivity.this, NotificationCenter.closeChats);
                    NotificationCenter.getInstance(ProfileActivity.this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                    ProfileActivity.this.playProfileAnimation = false;
                    ProfileActivity.this.finishFragment();
                    return;
                }
                ProfileActivity.this.getNotificationCenter().postNotificationName(NotificationCenter.peerSettingsDidLoad, Long.valueOf((long) ProfileActivity.this.user_id));
            }

            public /* synthetic */ void lambda$onItemClick$1$ProfileActivity$3(DialogInterface dialogInterface, int i) {
                MessagesController.getInstance(ProfileActivity.this.currentAccount).blockUser(ProfileActivity.this.user_id);
                AlertsCreator.showSimpleToast(ProfileActivity.this, LocaleController.getString("UserBlocked", NUM));
            }

            public /* synthetic */ void lambda$onItemClick$2$ProfileActivity$3(User user, DialogInterface dialogInterface, int i) {
                ArrayList arrayList = new ArrayList();
                arrayList.add(user);
                ContactsController.getInstance(ProfileActivity.this.currentAccount).deleteContact(arrayList);
            }

            public /* synthetic */ void lambda$onItemClick$3$ProfileActivity$3(User user, DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z) {
                long longValue = ((Long) arrayList.get(0)).longValue();
                Bundle bundle = new Bundle();
                bundle.putBoolean("scrollToTopOnResume", true);
                int i = -((int) longValue);
                bundle.putInt("chat_id", i);
                if (MessagesController.getInstance(ProfileActivity.this.currentAccount).checkCanOpenChat(bundle, dialogsActivity)) {
                    NotificationCenter.getInstance(ProfileActivity.this.currentAccount).removeObserver(ProfileActivity.this, NotificationCenter.closeChats);
                    NotificationCenter.getInstance(ProfileActivity.this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                    MessagesController.getInstance(ProfileActivity.this.currentAccount).addUserToChat(i, user, null, 0, null, ProfileActivity.this, null);
                    ProfileActivity.this.presentFragment(new ChatActivity(bundle), true);
                    ProfileActivity.this.removeSelfFromStack();
                }
            }

            public /* synthetic */ void lambda$onItemClick$5$ProfileActivity$3(AlertDialog[] alertDialogArr, TLObject tLObject, TL_error tL_error) {
                AndroidUtilities.runOnUIThread(new -$$Lambda$ProfileActivity$3$U_3oCTriqFRosMt86ezSEX5gH_c(this, alertDialogArr, tLObject));
            }

            public /* synthetic */ void lambda$null$4$ProfileActivity$3(AlertDialog[] alertDialogArr, TLObject tLObject) {
                try {
                    alertDialogArr[0].dismiss();
                } catch (Throwable unused) {
                }
                alertDialogArr[0] = null;
                if (tLObject != null) {
                    TL_statsURL tL_statsURL = (TL_statsURL) tLObject;
                    ProfileActivity profileActivity = ProfileActivity.this;
                    profileActivity.presentFragment(new WebviewActivity(tL_statsURL.url, (long) (-profileActivity.chat_id)));
                }
            }

            public /* synthetic */ void lambda$onItemClick$6$ProfileActivity$3(int i, DialogInterface dialogInterface) {
                ConnectionsManager.getInstance(ProfileActivity.this.currentAccount).cancelRequest(i, true);
            }
        });
        createActionBarMenu();
        this.listAdapter = new ListAdapter(context2);
        this.avatarDrawable = new AvatarDrawable();
        this.avatarDrawable.setProfile(true);
        this.fragmentView = new FrameLayout(context2) {
            public boolean hasOverlappingRendering() {
                return false;
            }

            /* Access modifiers changed, original: protected */
            public void onLayout(boolean z, int i, int i2, int i3, int i4) {
                super.onLayout(z, i, i2, i3, i4);
                ProfileActivity.this.checkListViewScroll();
            }
        };
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        this.listView = new RecyclerListView(context2) {
            private Paint paint = new Paint();

            public boolean hasOverlappingRendering() {
                return false;
            }

            /* JADX WARNING: Missing block: B:27:0x00a0, code skipped:
            if (r0.itemView.getBottom() >= r1) goto L_0x00a2;
     */
            public void onDraw(android.graphics.Canvas r15) {
                /*
                r14 = this;
                r0 = org.telegram.ui.ProfileActivity.this;
                r0 = r0.lastSectionRow;
                r1 = -1;
                if (r0 == r1) goto L_0x0015;
            L_0x0009:
                r0 = org.telegram.ui.ProfileActivity.this;
                r0 = r0.lastSectionRow;
                r0 = r14.findViewHolderForAdapterPosition(r0);
                goto L_0x008e;
            L_0x0015:
                r0 = org.telegram.ui.ProfileActivity.this;
                r0 = r0.sharedSectionRow;
                if (r0 == r1) goto L_0x003e;
            L_0x001d:
                r0 = org.telegram.ui.ProfileActivity.this;
                r0 = r0.membersSectionRow;
                if (r0 == r1) goto L_0x0033;
            L_0x0025:
                r0 = org.telegram.ui.ProfileActivity.this;
                r0 = r0.membersSectionRow;
                r2 = org.telegram.ui.ProfileActivity.this;
                r2 = r2.sharedSectionRow;
                if (r0 >= r2) goto L_0x003e;
            L_0x0033:
                r0 = org.telegram.ui.ProfileActivity.this;
                r0 = r0.sharedSectionRow;
                r0 = r14.findViewHolderForAdapterPosition(r0);
                goto L_0x008e;
            L_0x003e:
                r0 = org.telegram.ui.ProfileActivity.this;
                r0 = r0.membersSectionRow;
                if (r0 == r1) goto L_0x0067;
            L_0x0046:
                r0 = org.telegram.ui.ProfileActivity.this;
                r0 = r0.sharedSectionRow;
                if (r0 == r1) goto L_0x005c;
            L_0x004e:
                r0 = org.telegram.ui.ProfileActivity.this;
                r0 = r0.membersSectionRow;
                r2 = org.telegram.ui.ProfileActivity.this;
                r2 = r2.sharedSectionRow;
                if (r0 <= r2) goto L_0x0067;
            L_0x005c:
                r0 = org.telegram.ui.ProfileActivity.this;
                r0 = r0.membersSectionRow;
                r0 = r14.findViewHolderForAdapterPosition(r0);
                goto L_0x008e;
            L_0x0067:
                r0 = org.telegram.ui.ProfileActivity.this;
                r0 = r0.settingsSectionRow;
                if (r0 == r1) goto L_0x007a;
            L_0x006f:
                r0 = org.telegram.ui.ProfileActivity.this;
                r0 = r0.settingsSectionRow;
                r0 = r14.findViewHolderForAdapterPosition(r0);
                goto L_0x008e;
            L_0x007a:
                r0 = org.telegram.ui.ProfileActivity.this;
                r0 = r0.infoSectionRow;
                if (r0 == r1) goto L_0x008d;
            L_0x0082:
                r0 = org.telegram.ui.ProfileActivity.this;
                r0 = r0.infoSectionRow;
                r0 = r14.findViewHolderForAdapterPosition(r0);
                goto L_0x008e;
            L_0x008d:
                r0 = 0;
            L_0x008e:
                r1 = r14.getMeasuredHeight();
                if (r0 == 0) goto L_0x00a2;
            L_0x0094:
                r2 = r0.itemView;
                r2 = r2.getBottom();
                r0 = r0.itemView;
                r0 = r0.getBottom();
                if (r0 < r1) goto L_0x00a3;
            L_0x00a2:
                r2 = r1;
            L_0x00a3:
                r0 = r14.paint;
                r3 = "windowBackgroundWhite";
                r3 = org.telegram.ui.ActionBar.Theme.getColor(r3);
                r0.setColor(r3);
                r5 = 0;
                r6 = 0;
                r0 = r14.getMeasuredWidth();
                r7 = (float) r0;
                r10 = (float) r2;
                r9 = r14.paint;
                r4 = r15;
                r8 = r10;
                r4.drawRect(r5, r6, r7, r8, r9);
                if (r2 == r1) goto L_0x00d9;
            L_0x00c0:
                r0 = r14.paint;
                r2 = "windowBackgroundGray";
                r2 = org.telegram.ui.ActionBar.Theme.getColor(r2);
                r0.setColor(r2);
                r9 = 0;
                r0 = r14.getMeasuredWidth();
                r11 = (float) r0;
                r12 = (float) r1;
                r13 = r14.paint;
                r8 = r15;
                r8.drawRect(r9, r10, r11, r12, r13);
            L_0x00d9:
                return;
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ProfileActivity$AnonymousClass5.onDraw(android.graphics.Canvas):void");
            }
        };
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
        int i = (this.user_id != 0 || (ChatObject.isChannel(this.chat_id, this.currentAccount) && !this.currentChat.megagroup)) ? 5 : this.chat_id;
        recyclerListView.setGlowColor(AvatarDrawable.getProfileBackColorForId(i));
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
        this.listView.setAdapter(this.listAdapter);
        this.listView.setOnItemClickListener(new -$$Lambda$ProfileActivity$H08izesUB4mAkQt5BMshtXqt2Qs(this));
        this.listView.setOnItemLongClickListener(new -$$Lambda$ProfileActivity$Y_qIPa4kzyhg9puFuR2Vq4GocZU(this));
        String str = "fonts/rmedium.ttf";
        if (this.banFromGroup != 0) {
            Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.banFromGroup));
            if (this.currentChannelParticipant == null) {
                TL_channels_getParticipant tL_channels_getParticipant = new TL_channels_getParticipant();
                tL_channels_getParticipant.channel = MessagesController.getInputChannel(chat);
                tL_channels_getParticipant.user_id = MessagesController.getInstance(this.currentAccount).getInputUser(this.user_id);
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_channels_getParticipant, new -$$Lambda$ProfileActivity$UPmZCQ76huGf0mwqhk7A6RKfTkw(this));
            }
            AnonymousClass7 anonymousClass7 = new FrameLayout(context2) {
                /* Access modifiers changed, original: protected */
                public void onDraw(Canvas canvas) {
                    int intrinsicHeight = Theme.chat_composeShadowDrawable.getIntrinsicHeight();
                    Theme.chat_composeShadowDrawable.setBounds(0, 0, getMeasuredWidth(), intrinsicHeight);
                    Theme.chat_composeShadowDrawable.draw(canvas);
                    canvas.drawRect(0.0f, (float) intrinsicHeight, (float) getMeasuredWidth(), (float) getMeasuredHeight(), Theme.chat_composeBackgroundPaint);
                }
            };
            anonymousClass7.setWillNotDraw(false);
            frameLayout.addView(anonymousClass7, LayoutHelper.createFrame(-1, 51, 83));
            anonymousClass7.setOnClickListener(new -$$Lambda$ProfileActivity$J6S3hQy_nW1WeGvcfet5MPw3HFQ(this, chat));
            TextView textView = new TextView(context2);
            textView.setTextColor(Theme.getColor("windowBackgroundWhiteRedText"));
            textView.setTextSize(1, 15.0f);
            textView.setGravity(17);
            textView.setTypeface(AndroidUtilities.getTypeface(str));
            textView.setText(LocaleController.getString("BanFromTheGroup", NUM));
            anonymousClass7.addView(textView, LayoutHelper.createFrame(-2, -2.0f, 17, 0.0f, 1.0f, 0.0f, 0.0f));
            this.listView.setPadding(0, AndroidUtilities.dp(88.0f), 0, AndroidUtilities.dp(48.0f));
            this.listView.setBottomGlowOffset(AndroidUtilities.dp(48.0f));
        } else {
            this.listView.setPadding(0, AndroidUtilities.dp(88.0f), 0, 0);
        }
        this.topView = new TopView(context2);
        TopView topView = this.topView;
        int i2 = (this.user_id != 0 || (ChatObject.isChannel(this.chat_id, this.currentAccount) && !this.currentChat.megagroup)) ? 5 : this.chat_id;
        topView.setBackgroundColor(AvatarDrawable.getProfileBackColorForId(i2));
        frameLayout.addView(this.topView);
        frameLayout.addView(this.actionBar);
        this.avatarImage = new BackupImageView(context2);
        this.avatarImage.setRoundRadius(AndroidUtilities.dp(21.0f));
        this.avatarImage.setPivotX(0.0f);
        this.avatarImage.setPivotY(0.0f);
        frameLayout.addView(this.avatarImage, LayoutHelper.createFrame(42, 42.0f, 51, 64.0f, 0.0f, 0.0f, 0.0f));
        this.avatarImage.setOnClickListener(new -$$Lambda$ProfileActivity$x_zxLVJHDncW-LPz2s41OV-XOro(this));
        this.avatarImage.setContentDescription(LocaleController.getString("AccDescrProfilePicture", NUM));
        int i3 = 0;
        while (i3 < 2) {
            if (this.playProfileAnimation || i3 != 0) {
                SimpleTextView simpleTextView;
                int i4;
                this.nameTextView[i3] = new SimpleTextView(context2);
                if (i3 == 1) {
                    this.nameTextView[i3].setTextColor(Theme.getColor("profile_title"));
                } else {
                    this.nameTextView[i3].setTextColor(Theme.getColor("actionBarDefaultTitle"));
                }
                this.nameTextView[i3].setTextSize(18);
                this.nameTextView[i3].setGravity(3);
                this.nameTextView[i3].setTypeface(AndroidUtilities.getTypeface(str));
                this.nameTextView[i3].setLeftDrawableTopPadding(-AndroidUtilities.dp(1.3f));
                this.nameTextView[i3].setPivotX(0.0f);
                this.nameTextView[i3].setPivotY(0.0f);
                this.nameTextView[i3].setAlpha(i3 == 0 ? 0.0f : 1.0f);
                if (i3 == 1) {
                    this.nameTextView[i3].setScrollNonFitText(true);
                    simpleTextView = this.nameTextView[i3];
                    i4 = (this.user_id != 0 || (ChatObject.isChannel(this.chat_id, this.currentAccount) && !this.currentChat.megagroup)) ? 5 : this.chat_id;
                    simpleTextView.setBackgroundColor(AvatarDrawable.getProfileBackColorForId(i4));
                }
                frameLayout.addView(this.nameTextView[i3], LayoutHelper.createFrame(-2, -2.0f, 51, 118.0f, 0.0f, i3 == 0 ? 48.0f : 0.0f, 0.0f));
                this.onlineTextView[i3] = new SimpleTextView(context2);
                simpleTextView = this.onlineTextView[i3];
                i4 = (this.user_id != 0 || (ChatObject.isChannel(this.chat_id, this.currentAccount) && !this.currentChat.megagroup)) ? 5 : this.chat_id;
                simpleTextView.setTextColor(AvatarDrawable.getProfileTextColorForId(i4));
                this.onlineTextView[i3].setTextSize(14);
                this.onlineTextView[i3].setGravity(3);
                this.onlineTextView[i3].setAlpha(i3 == 0 ? 0.0f : 1.0f);
                frameLayout.addView(this.onlineTextView[i3], LayoutHelper.createFrame(-2, -2.0f, 51, 118.0f, 0.0f, i3 == 0 ? 48.0f : 8.0f, 0.0f));
            }
            i3++;
        }
        if (this.user_id != 0) {
            this.writeButton = new ImageView(context2);
            Drawable createSimpleSelectorCircleDrawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56.0f), Theme.getColor("profile_actionBackground"), Theme.getColor("profile_actionPressedBackground"));
            if (VERSION.SDK_INT < 21) {
                Drawable mutate = context.getResources().getDrawable(NUM).mutate();
                mutate.setColorFilter(new PorterDuffColorFilter(-16777216, Mode.MULTIPLY));
                Drawable combinedDrawable = new CombinedDrawable(mutate, createSimpleSelectorCircleDrawable, 0, 0);
                combinedDrawable.setIconSize(AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
                createSimpleSelectorCircleDrawable = combinedDrawable;
            }
            this.writeButton.setBackgroundDrawable(createSimpleSelectorCircleDrawable);
            this.writeButton.setImageResource(NUM);
            this.writeButton.setContentDescription(LocaleController.getString("AccDescrOpenChat", NUM));
            this.writeButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("profile_actionIcon"), Mode.MULTIPLY));
            this.writeButton.setScaleType(ScaleType.CENTER);
            if (VERSION.SDK_INT >= 21) {
                StateListAnimator stateListAnimator = new StateListAnimator();
                stateListAnimator.addState(new int[]{16842919}, ObjectAnimator.ofFloat(this.writeButton, View.TRANSLATION_Z, new float[]{(float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(4.0f)}).setDuration(200));
                stateListAnimator.addState(new int[0], ObjectAnimator.ofFloat(this.writeButton, View.TRANSLATION_Z, new float[]{(float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(2.0f)}).setDuration(200));
                this.writeButton.setStateListAnimator(stateListAnimator);
                this.writeButton.setOutlineProvider(new ViewOutlineProvider() {
                    @SuppressLint({"NewApi"})
                    public void getOutline(View view, Outline outline) {
                        outline.setOval(0, 0, AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
                    }
                });
            }
            frameLayout.addView(this.writeButton, LayoutHelper.createFrame(VERSION.SDK_INT >= 21 ? 56 : 60, VERSION.SDK_INT >= 21 ? 56.0f : 60.0f, 53, 0.0f, 0.0f, 16.0f, 0.0f));
            this.writeButton.setOnClickListener(new -$$Lambda$ProfileActivity$MnD_12oukmBdwxWh9wauwLpeZDg(this));
        }
        needLayout();
        this.listView.setOnScrollListener(new OnScrollListener() {
            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                ProfileActivity.this.checkListViewScroll();
                if (ProfileActivity.this.participantsMap != null && !ProfileActivity.this.usersEndReached && ProfileActivity.this.layoutManager.findLastVisibleItemPosition() > ProfileActivity.this.membersEndRow - 8) {
                    ProfileActivity.this.getChannelParticipants(false);
                }
            }
        });
        this.undoView = new UndoView(context2);
        frameLayout.addView(this.undoView, LayoutHelper.createFrame(-1, -2.0f, 83, 8.0f, 0.0f, 8.0f, 8.0f));
        return this.fragmentView;
    }

    public /* synthetic */ void lambda$createView$3$ProfileActivity(View view, int i, float f, float f2) {
        int i2 = i;
        if (getParentActivity() != null) {
            int i3 = 2;
            long j = 0;
            Bundle bundle;
            int i4;
            String str;
            long j2;
            if (i2 == this.photosRow || i2 == this.filesRow || i2 == this.linksRow || i2 == this.audioRow || i2 == this.voiceRow) {
                if (i2 == this.photosRow) {
                    i3 = 0;
                } else if (i2 == this.filesRow) {
                    i3 = 1;
                } else if (i2 == this.linksRow) {
                    i3 = 3;
                } else if (i2 == this.audioRow) {
                    i3 = 4;
                }
                bundle = new Bundle();
                i4 = this.user_id;
                str = "dialog_id";
                if (i4 != 0) {
                    j2 = this.dialog_id;
                    if (j2 == 0) {
                        j2 = (long) i4;
                    }
                    bundle.putLong(str, j2);
                } else {
                    bundle.putLong(str, (long) (-this.chat_id));
                }
                int[] iArr = new int[5];
                System.arraycopy(this.lastMediaCount, 0, iArr, 0, iArr.length);
                this.mediaActivity = new MediaActivity(bundle, iArr, this.sharedMediaData, i3);
                this.mediaActivity.setChatInfo(this.chatInfo);
                presentFragment(this.mediaActivity);
            } else if (i2 == this.groupsInCommonRow) {
                presentFragment(new CommonGroupsActivity(this.user_id));
            } else {
                String str2 = "chat_id";
                String str3;
                if (i2 == this.settingsKeyRow) {
                    bundle = new Bundle();
                    bundle.putInt(str2, (int) (this.dialog_id >> 32));
                    presentFragment(new IdenticonActivity(bundle));
                } else if (i2 == this.settingsTimerRow) {
                    showDialog(AlertsCreator.createTTLAlert(getParentActivity(), this.currentEncryptedChat).create());
                } else if (i2 == this.notificationsRow) {
                    long j3 = this.dialog_id;
                    if (j3 == 0) {
                        i2 = this.user_id;
                        if (i2 == 0) {
                            i2 = -this.chat_id;
                        }
                        j3 = (long) i2;
                    }
                    j2 = j3;
                    if ((!LocaleController.isRTL || f > ((float) AndroidUtilities.dp(76.0f))) && (LocaleController.isRTL || f < ((float) (view.getMeasuredWidth() - AndroidUtilities.dp(76.0f))))) {
                        AlertsCreator.showCustomNotificationsDialog(this, j2, -1, null, this.currentAccount, new -$$Lambda$ProfileActivity$KjKoxTLNU0USdgocPZA7qT-wkmY(this));
                    } else {
                        NotificationsCheckCell notificationsCheckCell = (NotificationsCheckCell) view;
                        i4 = notificationsCheckCell.isChecked() ^ 1;
                        boolean isGlobalNotificationsEnabled = NotificationsController.getInstance(this.currentAccount).isGlobalNotificationsEnabled(j2);
                        str = "notify2_";
                        if (i4 != 0) {
                            Editor edit = MessagesController.getNotificationsSettings(this.currentAccount).edit();
                            StringBuilder stringBuilder;
                            if (isGlobalNotificationsEnabled) {
                                stringBuilder = new StringBuilder();
                                stringBuilder.append(str);
                                stringBuilder.append(j2);
                                edit.remove(stringBuilder.toString());
                            } else {
                                stringBuilder = new StringBuilder();
                                stringBuilder.append(str);
                                stringBuilder.append(j2);
                                edit.putInt(stringBuilder.toString(), 0);
                            }
                            MessagesStorage.getInstance(this.currentAccount).setDialogFlags(j2, 0);
                            edit.commit();
                            Dialog dialog = (Dialog) MessagesController.getInstance(this.currentAccount).dialogs_dict.get(j2);
                            if (dialog != null) {
                                dialog.notify_settings = new TL_peerNotifySettings();
                            }
                            NotificationsController.getInstance(this.currentAccount).updateServerNotificationsSettings(j2);
                        } else {
                            Editor edit2 = MessagesController.getNotificationsSettings(this.currentAccount).edit();
                            if (isGlobalNotificationsEnabled) {
                                StringBuilder stringBuilder2 = new StringBuilder();
                                stringBuilder2.append(str);
                                stringBuilder2.append(j2);
                                edit2.putInt(stringBuilder2.toString(), 2);
                                j = 1;
                            } else {
                                StringBuilder stringBuilder3 = new StringBuilder();
                                stringBuilder3.append(str);
                                stringBuilder3.append(j2);
                                edit2.remove(stringBuilder3.toString());
                            }
                            NotificationsController.getInstance(this.currentAccount).removeNotificationsForDialog(j2);
                            MessagesStorage.getInstance(this.currentAccount).setDialogFlags(j2, j);
                            edit2.commit();
                            Dialog dialog2 = (Dialog) MessagesController.getInstance(this.currentAccount).dialogs_dict.get(j2);
                            if (dialog2 != null) {
                                dialog2.notify_settings = new TL_peerNotifySettings();
                                if (isGlobalNotificationsEnabled) {
                                    dialog2.notify_settings.mute_until = Integer.MAX_VALUE;
                                }
                            }
                            NotificationsController.getInstance(this.currentAccount).updateServerNotificationsSettings(j2);
                        }
                        notificationsCheckCell.setChecked(i4);
                        Holder holder = (Holder) this.listView.findViewHolderForPosition(this.notificationsRow);
                        if (holder != null) {
                            this.listAdapter.onBindViewHolder(holder, this.notificationsRow);
                        }
                    }
                } else if (i2 == this.startSecretChatRow) {
                    Builder builder = new Builder(getParentActivity());
                    builder.setTitle(LocaleController.getString("AreYouSureSecretChatTitle", NUM));
                    builder.setMessage(LocaleController.getString("AreYouSureSecretChat", NUM));
                    builder.setPositiveButton(LocaleController.getString("Start", NUM), new -$$Lambda$ProfileActivity$kx-V4hZeGCLASSNAMEZkCyzsaLus8IpoA(this));
                    builder.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
                    showDialog(builder.create());
                } else if (i2 == this.unblockRow) {
                    MessagesController.getInstance(this.currentAccount).unblockUser(this.user_id);
                    AlertsCreator.showSimpleToast(this, LocaleController.getString("UserUnblocked", NUM));
                } else if (i2 >= this.membersStartRow && i2 < this.membersEndRow) {
                    if (this.sortedUsers.isEmpty()) {
                        i2 = ((ChatParticipant) this.chatInfo.participants.participants.get(i2 - this.membersStartRow)).user_id;
                    } else {
                        i2 = ((ChatParticipant) this.chatInfo.participants.participants.get(((Integer) this.sortedUsers.get(i2 - this.membersStartRow)).intValue())).user_id;
                    }
                    if (i2 != UserConfig.getInstance(this.currentAccount).getClientUserId()) {
                        Bundle bundle2 = new Bundle();
                        bundle2.putInt("user_id", i2);
                        presentFragment(new ProfileActivity(bundle2));
                    }
                } else if (i2 == this.addMemberRow) {
                    openAddMember();
                } else if (i2 == this.usernameRow) {
                    if (this.currentChat != null) {
                        try {
                            Intent intent = new Intent("android.intent.action.SEND");
                            intent.setType("text/plain");
                            str3 = "/";
                            String str4 = "\nhttps://";
                            str = "android.intent.extra.TEXT";
                            StringBuilder stringBuilder4;
                            if (TextUtils.isEmpty(this.chatInfo.about)) {
                                stringBuilder4 = new StringBuilder();
                                stringBuilder4.append(this.currentChat.title);
                                stringBuilder4.append(str4);
                                stringBuilder4.append(MessagesController.getInstance(this.currentAccount).linkPrefix);
                                stringBuilder4.append(str3);
                                stringBuilder4.append(this.currentChat.username);
                                intent.putExtra(str, stringBuilder4.toString());
                            } else {
                                stringBuilder4 = new StringBuilder();
                                stringBuilder4.append(this.currentChat.title);
                                stringBuilder4.append("\n");
                                stringBuilder4.append(this.chatInfo.about);
                                stringBuilder4.append(str4);
                                stringBuilder4.append(MessagesController.getInstance(this.currentAccount).linkPrefix);
                                stringBuilder4.append(str3);
                                stringBuilder4.append(this.currentChat.username);
                                intent.putExtra(str, stringBuilder4.toString());
                            }
                            getParentActivity().startActivityForResult(Intent.createChooser(intent, LocaleController.getString("BotShare", NUM)), 500);
                        } catch (Exception e) {
                            FileLog.e(e);
                        }
                    }
                } else if (i2 == this.locationRow) {
                    if (this.chatInfo.location instanceof TL_channelLocation) {
                        LocationActivity locationActivity = new LocationActivity(5);
                        locationActivity.setChatLocation(this.chat_id, (TL_channelLocation) this.chatInfo.location);
                        presentFragment(locationActivity);
                    }
                } else if (i2 == this.leaveChannelRow) {
                    leaveChatPressed();
                } else if (i2 == this.joinRow) {
                    MessagesController.getInstance(this.currentAccount).addUserToChat(this.currentChat.id, UserConfig.getInstance(this.currentAccount).getCurrentUser(), null, 0, null, this, null);
                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.closeSearchByActiveAction, new Object[0]);
                } else {
                    str3 = "type";
                    ChatUsersActivity chatUsersActivity;
                    if (i2 == this.subscribersRow) {
                        bundle = new Bundle();
                        bundle.putInt(str2, this.chat_id);
                        bundle.putInt(str3, 2);
                        chatUsersActivity = new ChatUsersActivity(bundle);
                        chatUsersActivity.setInfo(this.chatInfo);
                        presentFragment(chatUsersActivity);
                    } else if (i2 == this.administratorsRow) {
                        bundle = new Bundle();
                        bundle.putInt(str2, this.chat_id);
                        bundle.putInt(str3, 1);
                        chatUsersActivity = new ChatUsersActivity(bundle);
                        chatUsersActivity.setInfo(this.chatInfo);
                        presentFragment(chatUsersActivity);
                    } else if (i2 == this.blockedUsersRow) {
                        bundle = new Bundle();
                        bundle.putInt(str2, this.chat_id);
                        bundle.putInt(str3, 0);
                        chatUsersActivity = new ChatUsersActivity(bundle);
                        chatUsersActivity.setInfo(this.chatInfo);
                        presentFragment(chatUsersActivity);
                    } else {
                        processOnClickOrPress(i);
                    }
                }
            }
        }
    }

    public /* synthetic */ void lambda$null$1$ProfileActivity(int i) {
        this.listAdapter.notifyItemChanged(this.notificationsRow);
    }

    public /* synthetic */ void lambda$null$2$ProfileActivity(DialogInterface dialogInterface, int i) {
        this.creatingChat = true;
        SecretChatHelper.getInstance(this.currentAccount).startSecretChat(getParentActivity(), MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.user_id)));
    }

    public /* synthetic */ boolean lambda$createView$6$ProfileActivity(View view, int i) {
        if (i < this.membersStartRow || i >= this.membersEndRow) {
            return processOnClickOrPress(i);
        }
        if (getParentActivity() == null) {
            return false;
        }
        ChatParticipant chatParticipant;
        if (this.sortedUsers.isEmpty()) {
            chatParticipant = (ChatParticipant) this.chatInfo.participants.participants.get(i - this.membersStartRow);
        } else {
            chatParticipant = (ChatParticipant) this.chatInfo.participants.participants.get(((Integer) this.sortedUsers.get(i - this.membersStartRow)).intValue());
        }
        ChatParticipant chatParticipant2 = chatParticipant;
        User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(chatParticipant2.user_id));
        if (user == null || chatParticipant2.user_id == UserConfig.getInstance(this.currentAccount).getClientUserId()) {
            return false;
        }
        boolean canAddAdmins;
        boolean z;
        boolean z2;
        ChannelParticipant channelParticipant;
        boolean z3;
        Object obj;
        this.selectedUser = chatParticipant2.user_id;
        if (ChatObject.isChannel(this.currentChat)) {
            ChannelParticipant channelParticipant2 = ((TL_chatChannelParticipant) chatParticipant2).channelParticipant;
            MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(chatParticipant2.user_id));
            canAddAdmins = ChatObject.canAddAdmins(this.currentChat);
            if (canAddAdmins && ((channelParticipant2 instanceof TL_channelParticipantCreator) || ((channelParticipant2 instanceof TL_channelParticipantAdmin) && !channelParticipant2.can_edit))) {
                canAddAdmins = false;
            }
            z = ChatObject.canBlockUsers(this.currentChat) && (!((channelParticipant2 instanceof TL_channelParticipantAdmin) || (channelParticipant2 instanceof TL_channelParticipantCreator)) || channelParticipant2.can_edit);
            z2 = channelParticipant2 instanceof TL_channelParticipantAdmin;
            channelParticipant = channelParticipant2;
            z3 = z;
        } else {
            Chat chat = this.currentChat;
            z = chat.creator || ((chatParticipant2 instanceof TL_chatParticipant) && (ChatObject.canBlockUsers(chat) || chatParticipant2.inviter_id == UserConfig.getInstance(this.currentAccount).getClientUserId()));
            canAddAdmins = this.currentChat.creator;
            z2 = chatParticipant2 instanceof TL_chatParticipantAdmin;
            channelParticipant = null;
            z3 = canAddAdmins;
        }
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        ArrayList arrayList3 = new ArrayList();
        if (canAddAdmins) {
            int i2;
            String str;
            if (z2) {
                i2 = NUM;
                str = "EditAdminRights";
            } else {
                i2 = NUM;
                str = "SetAsAdmin";
            }
            arrayList.add(LocaleController.getString(str, i2));
            arrayList2.add(Integer.valueOf(NUM));
            arrayList3.add(Integer.valueOf(0));
        }
        if (z3) {
            arrayList.add(LocaleController.getString("ChangePermissions", NUM));
            arrayList2.add(Integer.valueOf(NUM));
            arrayList3.add(Integer.valueOf(1));
        }
        if (z) {
            arrayList.add(LocaleController.getString("KickFromGroup", NUM));
            arrayList2.add(Integer.valueOf(NUM));
            arrayList3.add(Integer.valueOf(2));
            obj = 1;
        } else {
            obj = null;
        }
        if (arrayList.isEmpty()) {
            return false;
        }
        Builder builder = new Builder(getParentActivity());
        builder.setItems((CharSequence[]) arrayList.toArray(new CharSequence[0]), AndroidUtilities.toIntArray(arrayList2), new -$$Lambda$ProfileActivity$iWlc9ep_wbwBIz7hlp_XPY9A0O0(this, arrayList3, channelParticipant, chatParticipant2, user));
        AlertDialog create = builder.create();
        showDialog(create);
        if (obj != null) {
            create.setItemColor(arrayList.size() - 1, Theme.getColor("dialogTextRed2"), Theme.getColor("dialogRedIcon"));
        }
        return true;
    }

    public /* synthetic */ void lambda$null$5$ProfileActivity(ArrayList arrayList, ChannelParticipant channelParticipant, ChatParticipant chatParticipant, User user, DialogInterface dialogInterface, int i) {
        if (((Integer) arrayList.get(i)).intValue() == 2) {
            kickUser(this.selectedUser);
            return;
        }
        int intValue = ((Integer) arrayList.get(i)).intValue();
        if (intValue == 1 && ((channelParticipant instanceof TL_channelParticipantAdmin) || (chatParticipant instanceof TL_chatParticipantAdmin))) {
            Builder builder = new Builder(getParentActivity());
            builder.setTitle(LocaleController.getString("AppName", NUM));
            builder.setMessage(LocaleController.formatString("AdminWillBeRemoved", NUM, ContactsController.formatName(user.first_name, user.last_name)));
            builder.setPositiveButton(LocaleController.getString("OK", NUM), new -$$Lambda$ProfileActivity$M0pE8LsEZwD1lKkfGrG8ZoBFh8o(this, intValue, user, chatParticipant, channelParticipant));
            builder.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
            showDialog(builder.create());
            return;
        }
        openRightsEdit(intValue, user.id, chatParticipant, channelParticipant != null ? channelParticipant.admin_rights : null, channelParticipant != null ? channelParticipant.banned_rights : null);
    }

    public /* synthetic */ void lambda$null$4$ProfileActivity(int i, User user, ChatParticipant chatParticipant, ChannelParticipant channelParticipant, DialogInterface dialogInterface, int i2) {
        int i3 = user.id;
        TL_chatBannedRights tL_chatBannedRights = null;
        TL_chatAdminRights tL_chatAdminRights = channelParticipant != null ? channelParticipant.admin_rights : null;
        if (channelParticipant != null) {
            tL_chatBannedRights = channelParticipant.banned_rights;
        }
        openRightsEdit(i, i3, chatParticipant, tL_chatAdminRights, tL_chatBannedRights);
    }

    public /* synthetic */ void lambda$createView$8$ProfileActivity(TLObject tLObject, TL_error tL_error) {
        if (tLObject != null) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$ProfileActivity$l9cktgJWqXuDhbbfkCq09NYKvPo(this, tLObject));
        }
    }

    public /* synthetic */ void lambda$null$7$ProfileActivity(TLObject tLObject) {
        this.currentChannelParticipant = ((TL_channels_channelParticipant) tLObject).participant;
    }

    public /* synthetic */ void lambda$createView$9$ProfileActivity(Chat chat, View view) {
        int i = this.user_id;
        int i2 = this.banFromGroup;
        TL_chatBannedRights tL_chatBannedRights = chat.default_banned_rights;
        ChannelParticipant channelParticipant = this.currentChannelParticipant;
        ChatRightsEditActivity chatRightsEditActivity = new ChatRightsEditActivity(i, i2, null, tL_chatBannedRights, channelParticipant != null ? channelParticipant.banned_rights : null, 1, true, false);
        chatRightsEditActivity.setDelegate(new ChatRightsEditActivityDelegate() {
            public void didSetRights(int i, TL_chatAdminRights tL_chatAdminRights, TL_chatBannedRights tL_chatBannedRights) {
                ProfileActivity.this.removeSelfFromStack();
            }

            public void didChangeOwner(User user) {
                ProfileActivity.this.undoView.showWithAction((long) (-ProfileActivity.this.chat_id), ProfileActivity.this.currentChat.megagroup ? 10 : 9, (Object) user);
            }
        });
        presentFragment(chatRightsEditActivity);
    }

    public /* synthetic */ void lambda$createView$10$ProfileActivity(View view) {
        int i;
        if (this.user_id != 0) {
            User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.user_id));
            UserProfilePhoto userProfilePhoto = user.photo;
            if (userProfilePhoto != null && userProfilePhoto.photo_big != null) {
                PhotoViewer.getInstance().setParentActivity(getParentActivity());
                userProfilePhoto = user.photo;
                i = userProfilePhoto.dc_id;
                if (i != 0) {
                    userProfilePhoto.photo_big.dc_id = i;
                }
                PhotoViewer.getInstance().openPhoto(user.photo.photo_big, this.provider);
            }
        } else if (this.chat_id != 0) {
            Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.chat_id));
            ChatPhoto chatPhoto = chat.photo;
            if (chatPhoto != null && chatPhoto.photo_big != null) {
                PhotoViewer.getInstance().setParentActivity(getParentActivity());
                chatPhoto = chat.photo;
                i = chatPhoto.dc_id;
                if (i != 0) {
                    chatPhoto.photo_big.dc_id = i;
                }
                PhotoViewer.getInstance().openPhoto(chat.photo.photo_big, this.provider);
            }
        }
    }

    public /* synthetic */ void lambda$createView$11$ProfileActivity(View view) {
        if (this.playProfileAnimation) {
            ArrayList arrayList = this.parentLayout.fragmentsStack;
            if (arrayList.get(arrayList.size() - 2) instanceof ChatActivity) {
                finishFragment();
            }
        }
        User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.user_id));
        if (!(user == null || (user instanceof TL_userEmpty))) {
            Bundle bundle = new Bundle();
            bundle.putInt("user_id", this.user_id);
            if (MessagesController.getInstance(this.currentAccount).checkCanOpenChat(bundle, this)) {
                NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.closeChats);
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
                presentFragment(new ChatActivity(bundle), true);
            }
        }
    }

    private void openRightsEdit(final int i, int i2, final ChatParticipant chatParticipant, TL_chatAdminRights tL_chatAdminRights, TL_chatBannedRights tL_chatBannedRights) {
        ChatRightsEditActivity chatRightsEditActivity = new ChatRightsEditActivity(i2, this.chat_id, tL_chatAdminRights, this.currentChat.default_banned_rights, tL_chatBannedRights, i, true, false);
        chatRightsEditActivity.setDelegate(new ChatRightsEditActivityDelegate() {
            /* JADX WARNING: Removed duplicated region for block: B:42:0x0116  */
            /* JADX WARNING: Removed duplicated region for block: B:62:? A:{SYNTHETIC, RETURN} */
            /* JADX WARNING: Removed duplicated region for block: B:47:0x0142  */
            public void didSetRights(int r4, org.telegram.tgnet.TLRPC.TL_chatAdminRights r5, org.telegram.tgnet.TLRPC.TL_chatBannedRights r6) {
                /*
                r3 = this;
                r0 = r11;
                r1 = 1;
                if (r0 != 0) goto L_0x0082;
            L_0x0005:
                r0 = r13;
                r2 = r0 instanceof org.telegram.tgnet.TLRPC.TL_chatChannelParticipant;
                if (r2 == 0) goto L_0x0042;
            L_0x000b:
                r0 = (org.telegram.tgnet.TLRPC.TL_chatChannelParticipant) r0;
                if (r4 != r1) goto L_0x0017;
            L_0x000f:
                r4 = new org.telegram.tgnet.TLRPC$TL_channelParticipantAdmin;
                r4.<init>();
                r0.channelParticipant = r4;
                goto L_0x001e;
            L_0x0017:
                r4 = new org.telegram.tgnet.TLRPC$TL_channelParticipant;
                r4.<init>();
                r0.channelParticipant = r4;
            L_0x001e:
                r4 = r0.channelParticipant;
                r1 = org.telegram.ui.ProfileActivity.this;
                r1 = r1.currentAccount;
                r1 = org.telegram.messenger.UserConfig.getInstance(r1);
                r1 = r1.getClientUserId();
                r4.inviter_id = r1;
                r4 = r0.channelParticipant;
                r0 = r13;
                r1 = r0.user_id;
                r4.user_id = r1;
                r0 = r0.date;
                r4.date = r0;
                r4.banned_rights = r6;
                r4.admin_rights = r5;
                goto L_0x0155;
            L_0x0042:
                r5 = r0 instanceof org.telegram.tgnet.TLRPC.ChatParticipant;
                if (r5 == 0) goto L_0x0155;
            L_0x0046:
                if (r4 != r1) goto L_0x004e;
            L_0x0048:
                r4 = new org.telegram.tgnet.TLRPC$TL_chatParticipantAdmin;
                r4.<init>();
                goto L_0x0053;
            L_0x004e:
                r4 = new org.telegram.tgnet.TLRPC$TL_chatParticipant;
                r4.<init>();
            L_0x0053:
                r5 = r13;
                r6 = r5.user_id;
                r4.user_id = r6;
                r6 = r5.date;
                r4.date = r6;
                r5 = r5.inviter_id;
                r4.inviter_id = r5;
                r5 = org.telegram.ui.ProfileActivity.this;
                r5 = r5.chatInfo;
                r5 = r5.participants;
                r5 = r5.participants;
                r6 = r13;
                r5 = r5.indexOf(r6);
                if (r5 < 0) goto L_0x0155;
            L_0x0073:
                r6 = org.telegram.ui.ProfileActivity.this;
                r6 = r6.chatInfo;
                r6 = r6.participants;
                r6 = r6.participants;
                r6.set(r5, r4);
                goto L_0x0155;
            L_0x0082:
                if (r0 != r1) goto L_0x0155;
            L_0x0084:
                if (r4 != 0) goto L_0x0155;
            L_0x0086:
                r4 = org.telegram.ui.ProfileActivity.this;
                r4 = r4.currentChat;
                r4 = r4.megagroup;
                if (r4 == 0) goto L_0x0155;
            L_0x0090:
                r4 = org.telegram.ui.ProfileActivity.this;
                r4 = r4.chatInfo;
                if (r4 == 0) goto L_0x0155;
            L_0x0098:
                r4 = org.telegram.ui.ProfileActivity.this;
                r4 = r4.chatInfo;
                r4 = r4.participants;
                if (r4 == 0) goto L_0x0155;
            L_0x00a2:
                r4 = 0;
                r5 = 0;
            L_0x00a4:
                r6 = org.telegram.ui.ProfileActivity.this;
                r6 = r6.chatInfo;
                r6 = r6.participants;
                r6 = r6.participants;
                r6 = r6.size();
                if (r5 >= r6) goto L_0x00f3;
            L_0x00b4:
                r6 = org.telegram.ui.ProfileActivity.this;
                r6 = r6.chatInfo;
                r6 = r6.participants;
                r6 = r6.participants;
                r6 = r6.get(r5);
                r6 = (org.telegram.tgnet.TLRPC.TL_chatChannelParticipant) r6;
                r6 = r6.channelParticipant;
                r6 = r6.user_id;
                r0 = r13;
                r0 = r0.user_id;
                if (r6 != r0) goto L_0x00f0;
            L_0x00ce:
                r6 = org.telegram.ui.ProfileActivity.this;
                r6 = r6.chatInfo;
                if (r6 == 0) goto L_0x00e1;
            L_0x00d6:
                r6 = org.telegram.ui.ProfileActivity.this;
                r6 = r6.chatInfo;
                r0 = r6.participants_count;
                r0 = r0 - r1;
                r6.participants_count = r0;
            L_0x00e1:
                r6 = org.telegram.ui.ProfileActivity.this;
                r6 = r6.chatInfo;
                r6 = r6.participants;
                r6 = r6.participants;
                r6.remove(r5);
                r5 = 1;
                goto L_0x00f4;
            L_0x00f0:
                r5 = r5 + 1;
                goto L_0x00a4;
            L_0x00f3:
                r5 = 0;
            L_0x00f4:
                r6 = org.telegram.ui.ProfileActivity.this;
                r6 = r6.chatInfo;
                if (r6 == 0) goto L_0x0140;
            L_0x00fc:
                r6 = org.telegram.ui.ProfileActivity.this;
                r6 = r6.chatInfo;
                r6 = r6.participants;
                if (r6 == 0) goto L_0x0140;
            L_0x0106:
                r6 = org.telegram.ui.ProfileActivity.this;
                r6 = r6.chatInfo;
                r6 = r6.participants;
                r6 = r6.participants;
                r6 = r6.size();
                if (r4 >= r6) goto L_0x0140;
            L_0x0116:
                r6 = org.telegram.ui.ProfileActivity.this;
                r6 = r6.chatInfo;
                r6 = r6.participants;
                r6 = r6.participants;
                r6 = r6.get(r4);
                r6 = (org.telegram.tgnet.TLRPC.ChatParticipant) r6;
                r6 = r6.user_id;
                r0 = r13;
                r0 = r0.user_id;
                if (r6 != r0) goto L_0x013d;
            L_0x012e:
                r5 = org.telegram.ui.ProfileActivity.this;
                r5 = r5.chatInfo;
                r5 = r5.participants;
                r5 = r5.participants;
                r5.remove(r4);
                r5 = 1;
                goto L_0x0140;
            L_0x013d:
                r4 = r4 + 1;
                goto L_0x0106;
            L_0x0140:
                if (r5 == 0) goto L_0x0155;
            L_0x0142:
                r4 = org.telegram.ui.ProfileActivity.this;
                r4.updateOnlineCount();
                r4 = org.telegram.ui.ProfileActivity.this;
                r4.updateRowsIds();
                r4 = org.telegram.ui.ProfileActivity.this;
                r4 = r4.listAdapter;
                r4.notifyDataSetChanged();
            L_0x0155:
                return;
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ProfileActivity$AnonymousClass11.didSetRights(int, org.telegram.tgnet.TLRPC$TL_chatAdminRights, org.telegram.tgnet.TLRPC$TL_chatBannedRights):void");
            }

            public void didChangeOwner(User user) {
                ProfileActivity.this.undoView.showWithAction((long) (-ProfileActivity.this.chat_id), ProfileActivity.this.currentChat.megagroup ? 10 : 9, (Object) user);
            }
        });
        presentFragment(chatRightsEditActivity);
    }

    /* JADX WARNING: Missing block: B:7:0x0023, code skipped:
            if (r10 == null) goto L_0x0025;
     */
    /* JADX WARNING: Missing block: B:14:0x003e, code skipped:
            if (r10 == null) goto L_0x0062;
     */
    private boolean processOnClickOrPress(int r10) {
        /*
        r9 = this;
        r0 = r9.usernameRow;
        r1 = NUM; // 0x7f0d030d float:1.8743699E38 double:1.0531301634E-314;
        r2 = "Copy";
        r3 = 1;
        r4 = 0;
        if (r10 != r0) goto L_0x0063;
    L_0x000b:
        r10 = r9.user_id;
        if (r10 == 0) goto L_0x0026;
    L_0x000f:
        r10 = r9.currentAccount;
        r10 = org.telegram.messenger.MessagesController.getInstance(r10);
        r0 = r9.user_id;
        r0 = java.lang.Integer.valueOf(r0);
        r10 = r10.getUser(r0);
        if (r10 == 0) goto L_0x0025;
    L_0x0021:
        r10 = r10.username;
        if (r10 != 0) goto L_0x0041;
    L_0x0025:
        return r4;
    L_0x0026:
        r10 = r9.chat_id;
        if (r10 == 0) goto L_0x0062;
    L_0x002a:
        r10 = r9.currentAccount;
        r10 = org.telegram.messenger.MessagesController.getInstance(r10);
        r0 = r9.chat_id;
        r0 = java.lang.Integer.valueOf(r0);
        r10 = r10.getChat(r0);
        if (r10 == 0) goto L_0x0062;
    L_0x003c:
        r10 = r10.username;
        if (r10 != 0) goto L_0x0041;
    L_0x0040:
        goto L_0x0062;
    L_0x0041:
        r0 = new org.telegram.ui.ActionBar.AlertDialog$Builder;
        r5 = r9.getParentActivity();
        r0.<init>(r5);
        r5 = new java.lang.CharSequence[r3];
        r1 = org.telegram.messenger.LocaleController.getString(r2, r1);
        r5[r4] = r1;
        r1 = new org.telegram.ui.-$$Lambda$ProfileActivity$gKpOlBaS6Xkdlroi3J21GA6_fho;
        r1.<init>(r9, r10);
        r0.setItems(r5, r1);
        r10 = r0.create();
        r9.showDialog(r10);
        return r3;
    L_0x0062:
        return r4;
    L_0x0063:
        r0 = r9.phoneRow;
        if (r10 != r0) goto L_0x00f3;
    L_0x0067:
        r10 = r9.currentAccount;
        r10 = org.telegram.messenger.MessagesController.getInstance(r10);
        r0 = r9.user_id;
        r0 = java.lang.Integer.valueOf(r0);
        r10 = r10.getUser(r0);
        if (r10 == 0) goto L_0x00f2;
    L_0x0079:
        r0 = r10.phone;
        if (r0 == 0) goto L_0x00f2;
    L_0x007d:
        r0 = r0.length();
        if (r0 == 0) goto L_0x00f2;
    L_0x0083:
        r0 = r9.getParentActivity();
        if (r0 != 0) goto L_0x008a;
    L_0x0089:
        goto L_0x00f2;
    L_0x008a:
        r0 = new org.telegram.ui.ActionBar.AlertDialog$Builder;
        r5 = r9.getParentActivity();
        r0.<init>(r5);
        r5 = new java.util.ArrayList;
        r5.<init>();
        r6 = new java.util.ArrayList;
        r6.<init>();
        r7 = r9.userInfo;
        if (r7 == 0) goto L_0x00b9;
    L_0x00a1:
        r7 = r7.phone_calls_available;
        if (r7 == 0) goto L_0x00b9;
    L_0x00a5:
        r7 = NUM; // 0x7f0d01f1 float:1.8743123E38 double:1.053130023E-314;
        r8 = "CallViaTelegram";
        r7 = org.telegram.messenger.LocaleController.getString(r8, r7);
        r5.add(r7);
        r7 = 2;
        r7 = java.lang.Integer.valueOf(r7);
        r6.add(r7);
    L_0x00b9:
        r7 = NUM; // 0x7f0d01e0 float:1.8743088E38 double:1.0531300147E-314;
        r8 = "Call";
        r7 = org.telegram.messenger.LocaleController.getString(r8, r7);
        r5.add(r7);
        r7 = java.lang.Integer.valueOf(r4);
        r6.add(r7);
        r1 = org.telegram.messenger.LocaleController.getString(r2, r1);
        r5.add(r1);
        r1 = java.lang.Integer.valueOf(r3);
        r6.add(r1);
        r1 = new java.lang.CharSequence[r4];
        r1 = r5.toArray(r1);
        r1 = (java.lang.CharSequence[]) r1;
        r2 = new org.telegram.ui.-$$Lambda$ProfileActivity$LoFmbe3tPZ0XaDk0LvzW8Vph-Cw;
        r2.<init>(r9, r6, r10);
        r0.setItems(r1, r2);
        r10 = r0.create();
        r9.showDialog(r10);
        return r3;
    L_0x00f2:
        return r4;
    L_0x00f3:
        r0 = r9.channelInfoRow;
        if (r10 == r0) goto L_0x0101;
    L_0x00f7:
        r0 = r9.userInfoRow;
        if (r10 == r0) goto L_0x0101;
    L_0x00fb:
        r0 = r9.locationRow;
        if (r10 != r0) goto L_0x0100;
    L_0x00ff:
        goto L_0x0101;
    L_0x0100:
        return r4;
    L_0x0101:
        r0 = new org.telegram.ui.ActionBar.AlertDialog$Builder;
        r5 = r9.getParentActivity();
        r0.<init>(r5);
        r5 = new java.lang.CharSequence[r3];
        r1 = org.telegram.messenger.LocaleController.getString(r2, r1);
        r5[r4] = r1;
        r1 = new org.telegram.ui.-$$Lambda$ProfileActivity$1K2B1MD99m2QRNDFsE1oeM3NoTk;
        r1.<init>(r9, r10);
        r0.setItems(r5, r1);
        r10 = r0.create();
        r9.showDialog(r10);
        return r3;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ProfileActivity.processOnClickOrPress(int):boolean");
    }

    public /* synthetic */ void lambda$processOnClickOrPress$12$ProfileActivity(String str, DialogInterface dialogInterface, int i) {
        if (i == 0) {
            try {
                ClipboardManager clipboardManager = (ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard");
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("@");
                stringBuilder.append(str);
                clipboardManager.setPrimaryClip(ClipData.newPlainText("label", stringBuilder.toString()));
                Toast.makeText(getParentActivity(), LocaleController.getString("TextCopied", NUM), 0).show();
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    public /* synthetic */ void lambda$processOnClickOrPress$13$ProfileActivity(ArrayList arrayList, User user, DialogInterface dialogInterface, int i) {
        int intValue = ((Integer) arrayList.get(i)).intValue();
        StringBuilder stringBuilder;
        if (intValue == 0) {
            try {
                stringBuilder = new StringBuilder();
                stringBuilder.append("tel:+");
                stringBuilder.append(user.phone);
                Intent intent = new Intent("android.intent.action.DIAL", Uri.parse(stringBuilder.toString()));
                intent.addFlags(NUM);
                getParentActivity().startActivityForResult(intent, 500);
            } catch (Exception e) {
                FileLog.e(e);
            }
        } else if (intValue == 1) {
            try {
                ClipboardManager clipboardManager = (ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard");
                stringBuilder = new StringBuilder();
                stringBuilder.append("+");
                stringBuilder.append(user.phone);
                clipboardManager.setPrimaryClip(ClipData.newPlainText("label", stringBuilder.toString()));
                Toast.makeText(getParentActivity(), LocaleController.getString("PhoneCopied", NUM), 0).show();
            } catch (Exception e2) {
                FileLog.e(e2);
            }
        } else if (intValue == 2) {
            VoIPHelper.startCall(user, getParentActivity(), this.userInfo);
        }
    }

    public /* synthetic */ void lambda$processOnClickOrPress$14$ProfileActivity(int i, DialogInterface dialogInterface, int i2) {
        try {
            CharSequence charSequence = null;
            if (i == this.locationRow) {
                if (this.chatInfo != null && (this.chatInfo.location instanceof TL_channelLocation)) {
                    charSequence = ((TL_channelLocation) this.chatInfo.location).address;
                }
            } else if (i == this.channelInfoRow) {
                if (this.chatInfo != null) {
                    charSequence = this.chatInfo.about;
                }
            } else if (this.userInfo != null) {
                charSequence = this.userInfo.about;
            }
            if (!TextUtils.isEmpty(charSequence)) {
                AndroidUtilities.addToClipboard(charSequence);
                Toast.makeText(getParentActivity(), LocaleController.getString("TextCopied", NUM), 0).show();
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    private void leaveChatPressed() {
        AlertsCreator.createClearOrDeleteDialogAlert(this, false, this.currentChat, null, false, new -$$Lambda$ProfileActivity$8wL604xu3h506rbS1IIgKCgRL9o(this));
    }

    public /* synthetic */ void lambda$leaveChatPressed$15$ProfileActivity(boolean z) {
        this.playProfileAnimation = false;
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.closeChats);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
        finishFragment();
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.needDeleteDialog, Long.valueOf((long) (-this.currentChat.id)), null, this.currentChat, Boolean.valueOf(z));
    }

    private void getChannelParticipants(boolean z) {
        if (!this.loadingUsers) {
            SparseArray sparseArray = this.participantsMap;
            if (sparseArray != null && this.chatInfo != null) {
                this.loadingUsers = true;
                int i = 0;
                int i2 = (sparseArray.size() == 0 || !z) ? 0 : 300;
                TL_channels_getParticipants tL_channels_getParticipants = new TL_channels_getParticipants();
                tL_channels_getParticipants.channel = MessagesController.getInstance(this.currentAccount).getInputChannel(this.chat_id);
                tL_channels_getParticipants.filter = new TL_channelParticipantsRecent();
                if (!z) {
                    i = this.participantsMap.size();
                }
                tL_channels_getParticipants.offset = i;
                tL_channels_getParticipants.limit = 200;
                ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_channels_getParticipants, new -$$Lambda$ProfileActivity$hHrZMvrpfZD1yW63VBgt5bkL98w(this, tL_channels_getParticipants, i2)), this.classGuid);
            }
        }
    }

    public /* synthetic */ void lambda$getChannelParticipants$17$ProfileActivity(TL_channels_getParticipants tL_channels_getParticipants, int i, TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$ProfileActivity$D2jIXF1e9oJMwXWFymibGxQ8fxw(this, tL_error, tLObject, tL_channels_getParticipants), (long) i);
    }

    public /* synthetic */ void lambda$null$16$ProfileActivity(TL_error tL_error, TLObject tLObject, TL_channels_getParticipants tL_channels_getParticipants) {
        if (tL_error == null) {
            TL_channels_channelParticipants tL_channels_channelParticipants = (TL_channels_channelParticipants) tLObject;
            MessagesController.getInstance(this.currentAccount).putUsers(tL_channels_channelParticipants.users, false);
            if (tL_channels_channelParticipants.users.size() < 200) {
                this.usersEndReached = true;
            }
            if (tL_channels_getParticipants.offset == 0) {
                this.participantsMap.clear();
                this.chatInfo.participants = new TL_chatParticipants();
                MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(tL_channels_channelParticipants.users, null, true, true);
                MessagesStorage.getInstance(this.currentAccount).updateChannelUsers(this.chat_id, tL_channels_channelParticipants.participants);
            }
            for (int i = 0; i < tL_channels_channelParticipants.participants.size(); i++) {
                TL_chatChannelParticipant tL_chatChannelParticipant = new TL_chatChannelParticipant();
                tL_chatChannelParticipant.channelParticipant = (ChannelParticipant) tL_channels_channelParticipants.participants.get(i);
                ChannelParticipant channelParticipant = tL_chatChannelParticipant.channelParticipant;
                tL_chatChannelParticipant.inviter_id = channelParticipant.inviter_id;
                tL_chatChannelParticipant.user_id = channelParticipant.user_id;
                tL_chatChannelParticipant.date = channelParticipant.date;
                if (this.participantsMap.indexOfKey(tL_chatChannelParticipant.user_id) < 0) {
                    this.chatInfo.participants.participants.add(tL_chatChannelParticipant);
                    this.participantsMap.put(tL_chatChannelParticipant.user_id, tL_chatChannelParticipant);
                }
            }
        }
        updateOnlineCount();
        this.loadingUsers = false;
        updateRowsIds();
        ListAdapter listAdapter = this.listAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    private void openAddMember() {
        Bundle bundle = new Bundle();
        bundle.putBoolean("addToGroup", true);
        bundle.putInt("chatId", this.currentChat.id);
        GroupCreateActivity groupCreateActivity = new GroupCreateActivity(bundle);
        groupCreateActivity.setInfo(this.chatInfo);
        ChatFull chatFull = this.chatInfo;
        if (!(chatFull == null || chatFull.participants == null)) {
            SparseArray sparseArray = new SparseArray();
            for (int i = 0; i < this.chatInfo.participants.participants.size(); i++) {
                sparseArray.put(((ChatParticipant) this.chatInfo.participants.participants.get(i)).user_id, null);
            }
            groupCreateActivity.setIgnoreUsers(sparseArray);
        }
        groupCreateActivity.setDelegate(new -$$Lambda$ProfileActivity$kd4YT1P4AH1AD-EpsY7SnVeJBKE(this));
        presentFragment(groupCreateActivity);
    }

    public /* synthetic */ void lambda$openAddMember$18$ProfileActivity(ArrayList arrayList, int i) {
        int size = arrayList.size();
        for (int i2 = 0; i2 < size; i2++) {
            MessagesController.getInstance(this.currentAccount).addUserToChat(this.chat_id, (User) arrayList.get(i2), this.chatInfo, i, null, this, null);
        }
    }

    private void checkListViewScroll() {
        if (this.listView.getChildCount() > 0 && !this.openAnimationInProgress) {
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

    private void needLayout() {
        int currentActionBarHeight = (this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0) + ActionBar.getCurrentActionBarHeight();
        RecyclerListView recyclerListView = this.listView;
        if (!(recyclerListView == null || this.openAnimationInProgress)) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) recyclerListView.getLayoutParams();
            if (layoutParams.topMargin != currentActionBarHeight) {
                layoutParams.topMargin = currentActionBarHeight;
                this.listView.setLayoutParams(layoutParams);
            }
        }
        if (this.avatarImage != null) {
            float dp = ((float) this.extraHeight) / ((float) AndroidUtilities.dp(88.0f));
            this.listView.setTopGlowOffset(this.extraHeight);
            ImageView imageView = this.writeButton;
            float f = 0.0f;
            int i = 2;
            if (imageView != null) {
                imageView.setTranslationY((float) ((((this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0) + ActionBar.getCurrentActionBarHeight()) + this.extraHeight) - AndroidUtilities.dp(29.5f)));
                if (!this.openAnimationInProgress) {
                    Object obj = dp > 0.2f ? 1 : null;
                    if (obj != (this.writeButton.getTag() == null ? 1 : null)) {
                        if (obj != null) {
                            this.writeButton.setTag(null);
                        } else {
                            this.writeButton.setTag(Integer.valueOf(0));
                        }
                        AnimatorSet animatorSet = this.writeButtonAnimation;
                        if (animatorSet != null) {
                            this.writeButtonAnimation = null;
                            animatorSet.cancel();
                        }
                        this.writeButtonAnimation = new AnimatorSet();
                        if (obj != null) {
                            this.writeButtonAnimation.setInterpolator(new DecelerateInterpolator());
                            AnimatorSet animatorSet2 = this.writeButtonAnimation;
                            Animator[] animatorArr = new Animator[3];
                            animatorArr[0] = ObjectAnimator.ofFloat(this.writeButton, View.SCALE_X, new float[]{1.0f});
                            animatorArr[1] = ObjectAnimator.ofFloat(this.writeButton, View.SCALE_Y, new float[]{1.0f});
                            animatorArr[2] = ObjectAnimator.ofFloat(this.writeButton, View.ALPHA, new float[]{1.0f});
                            animatorSet2.playTogether(animatorArr);
                        } else {
                            this.writeButtonAnimation.setInterpolator(new AccelerateInterpolator());
                            AnimatorSet animatorSet3 = this.writeButtonAnimation;
                            Animator[] animatorArr2 = new Animator[3];
                            animatorArr2[0] = ObjectAnimator.ofFloat(this.writeButton, View.SCALE_X, new float[]{0.2f});
                            animatorArr2[1] = ObjectAnimator.ofFloat(this.writeButton, View.SCALE_Y, new float[]{0.2f});
                            animatorArr2[2] = ObjectAnimator.ofFloat(this.writeButton, View.ALPHA, new float[]{0.0f});
                            animatorSet3.playTogether(animatorArr2);
                        }
                        this.writeButtonAnimation.setDuration(150);
                        this.writeButtonAnimation.addListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animator) {
                                if (ProfileActivity.this.writeButtonAnimation != null && ProfileActivity.this.writeButtonAnimation.equals(animator)) {
                                    ProfileActivity.this.writeButtonAnimation = null;
                                }
                            }
                        });
                        this.writeButtonAnimation.start();
                    }
                }
            }
            float currentActionBarHeight2 = ((float) (this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0)) + ((((float) ActionBar.getCurrentActionBarHeight()) / 2.0f) * (dp + 1.0f));
            float f2 = AndroidUtilities.density;
            currentActionBarHeight2 = (currentActionBarHeight2 - (21.0f * f2)) + ((f2 * 27.0f) * dp);
            f2 = ((18.0f * dp) + 42.0f) / 42.0f;
            this.avatarImage.setScaleX(f2);
            this.avatarImage.setScaleY(f2);
            this.avatarImage.setTranslationX(((float) (-AndroidUtilities.dp(47.0f))) * dp);
            double d = (double) currentActionBarHeight2;
            this.avatarImage.setTranslationY((float) Math.ceil(d));
            int i2 = 0;
            while (i2 < i) {
                SimpleTextView[] simpleTextViewArr = this.nameTextView;
                if (simpleTextViewArr[i2] != null) {
                    simpleTextViewArr[i2].setTranslationX((AndroidUtilities.density * -21.0f) * dp);
                    this.nameTextView[i2].setTranslationY((((float) Math.floor(d)) + ((float) AndroidUtilities.dp(1.3f))) + (((float) AndroidUtilities.dp(7.0f)) * dp));
                    this.onlineTextView[i2].setTranslationX((AndroidUtilities.density * -21.0f) * dp);
                    this.onlineTextView[i2].setTranslationY((((float) Math.floor(d)) + ((float) AndroidUtilities.dp(24.0f))) + (((float) Math.floor((double) (AndroidUtilities.density * 11.0f))) * dp));
                    float f3 = (0.12f * dp) + 1.0f;
                    this.nameTextView[i2].setScaleX(f3);
                    this.nameTextView[i2].setScaleY(f3);
                    if (i2 == 1 && !this.openAnimationInProgress) {
                        int dp2;
                        if (AndroidUtilities.isTablet()) {
                            dp2 = AndroidUtilities.dp(490.0f);
                        } else {
                            dp2 = AndroidUtilities.displaySize.x;
                        }
                        int i3 = (this.callItem == null && this.editItem == null) ? 0 : 48;
                        i3 = AndroidUtilities.dp((float) (126 + (40 + i3)));
                        int i4 = dp2 - i3;
                        float f4 = (float) dp2;
                        i3 = (int) ((f4 - (((float) i3) * Math.max(f, 1.0f - (dp != 1.0f ? (0.15f * dp) / (1.0f - dp) : 1.0f)))) - this.nameTextView[i2].getTranslationX());
                        f = (this.nameTextView[i2].getPaint().measureText(this.nameTextView[i2].getText().toString()) * f3) + ((float) this.nameTextView[i2].getSideDrawablesSize());
                        FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) this.nameTextView[i2].getLayoutParams();
                        float f5 = (float) i3;
                        if (f5 < f) {
                            layoutParams2.width = Math.max(i4, (int) Math.ceil((double) (((float) (i3 - AndroidUtilities.dp(24.0f))) / (((1.12f - f3) * 7.0f) + f3))));
                        } else {
                            layoutParams2.width = (int) Math.ceil((double) f);
                        }
                        layoutParams2.width = (int) Math.min(((f4 - this.nameTextView[i2].getX()) / f3) - ((float) AndroidUtilities.dp(8.0f)), (float) layoutParams2.width);
                        this.nameTextView[i2].setLayoutParams(layoutParams2);
                        f3 = this.onlineTextView[i2].getPaint().measureText(this.onlineTextView[i2].getText().toString());
                        FrameLayout.LayoutParams layoutParams3 = (FrameLayout.LayoutParams) this.onlineTextView[i2].getLayoutParams();
                        layoutParams3.rightMargin = (int) Math.ceil((double) ((this.onlineTextView[i2].getTranslationX() + ((float) AndroidUtilities.dp(8.0f))) + (((float) AndroidUtilities.dp(40.0f)) * (1.0f - dp))));
                        if (f5 < f3) {
                            layoutParams3.width = (int) Math.ceil((double) i3);
                        } else {
                            layoutParams3.width = -2;
                        }
                        this.onlineTextView[i2].setLayoutParams(layoutParams3);
                    }
                }
                i2++;
                f = 0.0f;
                i = 2;
            }
        }
    }

    private void loadMediaCounts() {
        if (this.dialog_id != 0) {
            MediaDataController.getInstance(this.currentAccount).getMediaCounts(this.dialog_id, this.classGuid);
        } else if (this.user_id != 0) {
            MediaDataController.getInstance(this.currentAccount).getMediaCounts((long) this.user_id, this.classGuid);
        } else if (this.chat_id > 0) {
            MediaDataController.getInstance(this.currentAccount).getMediaCounts((long) (-this.chat_id), this.classGuid);
            if (this.mergeDialogId != 0) {
                MediaDataController.getInstance(this.currentAccount).getMediaCounts(this.mergeDialogId, this.classGuid);
            }
        }
    }

    private void fixLayout() {
        View view = this.fragmentView;
        if (view != null) {
            view.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {
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

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        int i3 = 0;
        if (i == NotificationCenter.updateInterfaces) {
            i = ((Integer) objArr[0]).intValue();
            if (this.user_id != 0) {
                if (!((i & 2) == 0 && (i & 1) == 0 && (i & 4) == 0)) {
                    updateProfileData();
                }
                if ((i & 1024) != 0) {
                    RecyclerListView recyclerListView = this.listView;
                    if (recyclerListView != null) {
                        Holder holder = (Holder) recyclerListView.findViewHolderForPosition(this.phoneRow);
                        if (holder != null) {
                            this.listAdapter.onBindViewHolder(holder, this.phoneRow);
                        }
                    }
                }
            } else if (this.chat_id != 0) {
                i2 = i & 8192;
                if (!(i2 == 0 && (i & 8) == 0 && (i & 16) == 0 && (i & 32) == 0 && (i & 4) == 0)) {
                    updateOnlineCount();
                    updateProfileData();
                }
                if (i2 != 0) {
                    updateRowsIds();
                    ListAdapter listAdapter = this.listAdapter;
                    if (listAdapter != null) {
                        listAdapter.notifyDataSetChanged();
                    }
                }
                if (!((i & 2) == 0 && (i & 1) == 0 && (i & 4) == 0)) {
                    RecyclerListView recyclerListView2 = this.listView;
                    if (recyclerListView2 != null) {
                        i2 = recyclerListView2.getChildCount();
                        while (i3 < i2) {
                            View childAt = this.listView.getChildAt(i3);
                            if (childAt instanceof UserCell) {
                                ((UserCell) childAt).update(i);
                            }
                            i3++;
                        }
                    }
                }
            }
        } else {
            int i4 = 1;
            long longValue;
            long j;
            int i5;
            int[] iArr;
            int i6;
            int i7;
            ListAdapter listAdapter2;
            boolean z;
            if (i == NotificationCenter.chatOnlineCountDidLoad) {
                Integer num = (Integer) objArr[0];
                if (this.chatInfo != null) {
                    Chat chat = this.currentChat;
                    if (chat != null && chat.id == num.intValue()) {
                        this.chatInfo.online_count = ((Integer) objArr[1]).intValue();
                        updateOnlineCount();
                        updateProfileData();
                    }
                }
            } else if (i == NotificationCenter.contactsDidLoad) {
                createActionBarMenu();
            } else if (i == NotificationCenter.mediaDidLoad) {
                longValue = ((Long) objArr[0]).longValue();
                if (((Integer) objArr[3]).intValue() == this.classGuid) {
                    j = this.dialog_id;
                    if (j == 0) {
                        i5 = this.user_id;
                        if (i5 == 0) {
                            i5 = this.chat_id;
                            if (i5 != 0) {
                                i5 = -i5;
                            }
                        }
                        j = (long) i5;
                    }
                    i5 = ((Integer) objArr[4]).intValue();
                    this.sharedMediaData[i5].setTotalCount(((Integer) objArr[1]).intValue());
                    ArrayList arrayList = (ArrayList) objArr[2];
                    boolean z2 = ((int) j) == 0;
                    if (longValue == j) {
                        i4 = 0;
                    }
                    if (!arrayList.isEmpty()) {
                        this.sharedMediaData[i5].setEndReached(i4, ((Boolean) objArr[5]).booleanValue());
                    }
                    for (i = 0; i < arrayList.size(); i++) {
                        this.sharedMediaData[i5].addMessage((MessageObject) arrayList.get(i), i4, false, z2);
                    }
                }
            } else if (i == NotificationCenter.mediaCountsDidLoad) {
                longValue = ((Long) objArr[0]).longValue();
                long j2 = this.dialog_id;
                if (j2 == 0) {
                    int i8 = this.user_id;
                    if (i8 != 0) {
                        j2 = (long) i8;
                    } else {
                        i8 = this.chat_id;
                        if (i8 != 0) {
                            j2 = (long) (-i8);
                        }
                    }
                }
                if (longValue == j2 || longValue == this.mergeDialogId) {
                    iArr = (int[]) objArr[1];
                    if (longValue == j2) {
                        this.mediaCount = iArr;
                    } else {
                        this.mediaMergeCount = iArr;
                    }
                    iArr = this.lastMediaCount;
                    int[] iArr2 = this.prevMediaCount;
                    System.arraycopy(iArr, 0, iArr2, 0, iArr2.length);
                    i6 = 0;
                    while (true) {
                        iArr2 = this.lastMediaCount;
                        if (i6 >= iArr2.length) {
                            break;
                        }
                        int[] iArr3 = this.mediaCount;
                        if (iArr3[i6] >= 0) {
                            int[] iArr4 = this.mediaMergeCount;
                            if (iArr4[i6] >= 0) {
                                iArr2[i6] = iArr3[i6] + iArr4[i6];
                                if (longValue == j2 && this.lastMediaCount[i6] != 0) {
                                    MediaDataController.getInstance(this.currentAccount).loadMedia(j2, 50, 0, i6, 2, this.classGuid);
                                }
                                i6++;
                            }
                        }
                        iArr2 = this.mediaCount;
                        if (iArr2[i6] >= 0) {
                            this.lastMediaCount[i6] = iArr2[i6];
                        } else {
                            iArr2 = this.mediaMergeCount;
                            if (iArr2[i6] >= 0) {
                                this.lastMediaCount[i6] = iArr2[i6];
                            } else {
                                this.lastMediaCount[i6] = 0;
                            }
                        }
                        MediaDataController.getInstance(this.currentAccount).loadMedia(j2, 50, 0, i6, 2, this.classGuid);
                        i6++;
                    }
                    updateSharedMediaRows();
                }
            } else if (i == NotificationCenter.mediaCountDidLoad) {
                longValue = ((Long) objArr[0]).longValue();
                j = this.dialog_id;
                if (j == 0) {
                    i7 = this.user_id;
                    if (i7 == 0) {
                        i7 = this.chat_id;
                        if (i7 != 0) {
                            i7 = -i7;
                        }
                    }
                    j = (long) i7;
                }
                if (longValue == j || longValue == this.mergeDialogId) {
                    i5 = ((Integer) objArr[3]).intValue();
                    i6 = ((Integer) objArr[1]).intValue();
                    if (longValue == j) {
                        this.mediaCount[i5] = i6;
                    } else {
                        this.mediaMergeCount[i5] = i6;
                    }
                    int[] iArr5 = this.prevMediaCount;
                    int[] iArr6 = this.lastMediaCount;
                    iArr5[i5] = iArr6[i5];
                    iArr5 = this.mediaCount;
                    if (iArr5[i5] >= 0) {
                        iArr = this.mediaMergeCount;
                        if (iArr[i5] >= 0) {
                            iArr6[i5] = iArr5[i5] + iArr[i5];
                            updateSharedMediaRows();
                        }
                    }
                    iArr5 = this.mediaCount;
                    if (iArr5[i5] >= 0) {
                        this.lastMediaCount[i5] = iArr5[i5];
                    } else {
                        iArr5 = this.mediaMergeCount;
                        if (iArr5[i5] >= 0) {
                            this.lastMediaCount[i5] = iArr5[i5];
                        } else {
                            this.lastMediaCount[i5] = 0;
                        }
                    }
                    updateSharedMediaRows();
                }
            } else if (i == NotificationCenter.encryptedChatCreated) {
                if (this.creatingChat) {
                    AndroidUtilities.runOnUIThread(new -$$Lambda$ProfileActivity$0-D0ofJ9OrdEukkEwUiB-hIwlCk(this, objArr));
                }
            } else if (i == NotificationCenter.encryptedChatUpdated) {
                EncryptedChat encryptedChat = (EncryptedChat) objArr[0];
                EncryptedChat encryptedChat2 = this.currentEncryptedChat;
                if (encryptedChat2 != null && encryptedChat.id == encryptedChat2.id) {
                    this.currentEncryptedChat = encryptedChat;
                    updateRowsIds();
                    listAdapter2 = this.listAdapter;
                    if (listAdapter2 != null) {
                        listAdapter2.notifyDataSetChanged();
                    }
                }
            } else if (i == NotificationCenter.blockedUsersDidLoad) {
                boolean z3;
                z = this.userBlocked;
                if (MessagesController.getInstance(this.currentAccount).blockedUsers.indexOfKey(this.user_id) >= 0) {
                    z3 = true;
                }
                this.userBlocked = z3;
                if (z != this.userBlocked) {
                    createActionBarMenu();
                    updateRowsIds();
                    this.listAdapter.notifyDataSetChanged();
                }
            } else if (i == NotificationCenter.chatInfoDidLoad) {
                ChatFull chatFull = (ChatFull) objArr[0];
                if (chatFull.id == this.chat_id) {
                    boolean booleanValue = ((Boolean) objArr[2]).booleanValue();
                    ChatFull chatFull2 = this.chatInfo;
                    if ((chatFull2 instanceof TL_channelFull) && chatFull.participants == null && chatFull2 != null) {
                        chatFull.participants = chatFull2.participants;
                    }
                    if (this.chatInfo == null && (chatFull instanceof TL_channelFull)) {
                        i3 = 1;
                    }
                    this.chatInfo = chatFull;
                    if (this.mergeDialogId == 0) {
                        i = this.chatInfo.migrated_from_chat_id;
                        if (i != 0) {
                            this.mergeDialogId = (long) (-i);
                            MediaDataController.getInstance(this.currentAccount).getMediaCount(this.mergeDialogId, 0, this.classGuid, true);
                        }
                    }
                    fetchUsersFromChannelInfo();
                    updateOnlineCount();
                    updateRowsIds();
                    listAdapter2 = this.listAdapter;
                    if (listAdapter2 != null) {
                        listAdapter2.notifyDataSetChanged();
                    }
                    Chat chat2 = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.chat_id));
                    if (chat2 != null) {
                        this.currentChat = chat2;
                        createActionBarMenu();
                    }
                    if (this.currentChat.megagroup && !(i3 == 0 && booleanValue)) {
                        getChannelParticipants(true);
                    }
                }
            } else if (i == NotificationCenter.closeChats) {
                removeSelfFromStack();
            } else if (i == NotificationCenter.botInfoDidLoad) {
                BotInfo botInfo = (BotInfo) objArr[0];
                if (botInfo.user_id == this.user_id) {
                    this.botInfo = botInfo;
                    updateRowsIds();
                    listAdapter2 = this.listAdapter;
                    if (listAdapter2 != null) {
                        listAdapter2.notifyDataSetChanged();
                    }
                }
            } else if (i == NotificationCenter.userInfoDidLoad) {
                if (((Integer) objArr[0]).intValue() == this.user_id) {
                    this.userInfo = (UserFull) objArr[1];
                    if (this.openAnimationInProgress || this.callItem != null) {
                        this.recreateMenuAfterAnimation = true;
                    } else {
                        createActionBarMenu();
                    }
                    updateRowsIds();
                    listAdapter2 = this.listAdapter;
                    if (listAdapter2 != null) {
                        listAdapter2.notifyDataSetChanged();
                    }
                }
            } else if (i == NotificationCenter.didReceiveNewMessages) {
                longValue = this.dialog_id;
                if (longValue == 0) {
                    i = this.user_id;
                    if (i == 0) {
                        i = -this.chat_id;
                    }
                    longValue = (long) i;
                }
                if (longValue == ((Long) objArr[0]).longValue()) {
                    z = ((int) longValue) == 0;
                    ArrayList arrayList2 = (ArrayList) objArr[1];
                    i6 = 0;
                    while (i6 < arrayList2.size()) {
                        MessageObject messageObject = (MessageObject) arrayList2.get(i6);
                        if (this.currentEncryptedChat != null) {
                            MessageAction messageAction = messageObject.messageOwner.action;
                            if (messageAction instanceof TL_messageEncryptedAction) {
                                DecryptedMessageAction decryptedMessageAction = messageAction.encryptedAction;
                                if (decryptedMessageAction instanceof TL_decryptedMessageActionSetMessageTTL) {
                                    TL_decryptedMessageActionSetMessageTTL tL_decryptedMessageActionSetMessageTTL = (TL_decryptedMessageActionSetMessageTTL) decryptedMessageAction;
                                    ListAdapter listAdapter3 = this.listAdapter;
                                    if (listAdapter3 != null) {
                                        listAdapter3.notifyDataSetChanged();
                                    }
                                }
                            }
                        }
                        i7 = MediaDataController.getMediaType(messageObject.messageOwner);
                        if (i7 != -1) {
                            this.sharedMediaData[i7].addMessage(messageObject, 0, true, z);
                            i6++;
                        } else {
                            return;
                        }
                    }
                    loadMediaCounts();
                }
            } else if (i == NotificationCenter.messagesDeleted) {
                i = ((Integer) objArr[1]).intValue();
                if (ChatObject.isChannel(this.currentChat)) {
                    if ((i != 0 || this.mergeDialogId == 0) && i != this.currentChat.id) {
                        return;
                    }
                } else if (i != 0) {
                    return;
                }
                ArrayList arrayList3 = (ArrayList) objArr[0];
                i2 = arrayList3.size();
                i6 = 0;
                Object obj = null;
                while (i6 < i2) {
                    Object obj2 = obj;
                    i5 = 0;
                    while (true) {
                        SharedMediaData[] sharedMediaDataArr = this.sharedMediaData;
                        if (i5 >= sharedMediaDataArr.length) {
                            break;
                        }
                        if (sharedMediaDataArr[i5].deleteMessage(((Integer) arrayList3.get(i6)).intValue(), 0)) {
                            obj2 = 1;
                        }
                        i5++;
                    }
                    i6++;
                    obj = obj2;
                }
                if (obj != null) {
                    MediaActivity mediaActivity = this.mediaActivity;
                    if (mediaActivity != null) {
                        mediaActivity.updateAdapters();
                    }
                }
                loadMediaCounts();
            }
        }
    }

    public /* synthetic */ void lambda$didReceivedNotification$19$ProfileActivity(Object[] objArr) {
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.closeChats);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
        EncryptedChat encryptedChat = (EncryptedChat) objArr[0];
        Bundle bundle = new Bundle();
        bundle.putInt("enc_id", encryptedChat.id);
        presentFragment(new ChatActivity(bundle), true);
    }

    public void onResume() {
        super.onResume();
        ListAdapter listAdapter = this.listAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
        updateProfileData();
        fixLayout();
        SimpleTextView[] simpleTextViewArr = this.nameTextView;
        if (simpleTextViewArr[1] != null) {
            setParentActivityTitle(simpleTextViewArr[1].getText());
        }
    }

    public void onPause() {
        super.onPause();
        UndoView undoView = this.undoView;
        if (undoView != null) {
            undoView.hide(true, 0);
        }
    }

    /* Access modifiers changed, original: protected */
    public void onBecomeFullyHidden() {
        UndoView undoView = this.undoView;
        if (undoView != null) {
            undoView.hide(true, 0);
        }
    }

    public void setPlayProfileAnimation(boolean z) {
        SharedPreferences globalMainSettings = MessagesController.getGlobalMainSettings();
        if (!AndroidUtilities.isTablet() && globalMainSettings.getBoolean("view_animations", true)) {
            this.playProfileAnimation = z;
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:100:0x0135  */
    /* JADX WARNING: Removed duplicated region for block: B:92:0x011e  */
    /* JADX WARNING: Removed duplicated region for block: B:100:0x0135  */
    /* JADX WARNING: Removed duplicated region for block: B:84:0x0107  */
    /* JADX WARNING: Removed duplicated region for block: B:92:0x011e  */
    /* JADX WARNING: Removed duplicated region for block: B:100:0x0135  */
    /* JADX WARNING: Removed duplicated region for block: B:76:0x00f0  */
    /* JADX WARNING: Removed duplicated region for block: B:84:0x0107  */
    /* JADX WARNING: Removed duplicated region for block: B:92:0x011e  */
    /* JADX WARNING: Removed duplicated region for block: B:100:0x0135  */
    /* JADX WARNING: Removed duplicated region for block: B:68:0x00d9  */
    /* JADX WARNING: Removed duplicated region for block: B:76:0x00f0  */
    /* JADX WARNING: Removed duplicated region for block: B:84:0x0107  */
    /* JADX WARNING: Removed duplicated region for block: B:92:0x011e  */
    /* JADX WARNING: Removed duplicated region for block: B:100:0x0135  */
    private void updateSharedMediaRows() {
        /*
        r13 = this;
        r0 = r13.listAdapter;
        if (r0 != 0) goto L_0x0005;
    L_0x0004:
        return;
    L_0x0005:
        r0 = r13.sharedHeaderRow;
        r1 = r13.photosRow;
        r2 = r13.filesRow;
        r3 = r13.linksRow;
        r4 = r13.audioRow;
        r5 = r13.voiceRow;
        r6 = r13.groupsInCommonRow;
        r7 = r13.sharedSectionRow;
        r13.updateRowsIds();
        r7 = 3;
        r8 = 2;
        r9 = -1;
        if (r0 != r9) goto L_0x004e;
    L_0x001d:
        r10 = r13.sharedHeaderRow;
        if (r10 == r9) goto L_0x004e;
    L_0x0021:
        r0 = r13.photosRow;
        if (r0 == r9) goto L_0x0026;
    L_0x0025:
        goto L_0x0027;
    L_0x0026:
        r7 = 2;
    L_0x0027:
        r0 = r13.filesRow;
        if (r0 == r9) goto L_0x002d;
    L_0x002b:
        r7 = r7 + 1;
    L_0x002d:
        r0 = r13.linksRow;
        if (r0 == r9) goto L_0x0033;
    L_0x0031:
        r7 = r7 + 1;
    L_0x0033:
        r0 = r13.audioRow;
        if (r0 == r9) goto L_0x0039;
    L_0x0037:
        r7 = r7 + 1;
    L_0x0039:
        r0 = r13.voiceRow;
        if (r0 == r9) goto L_0x003f;
    L_0x003d:
        r7 = r7 + 1;
    L_0x003f:
        r0 = r13.groupsInCommonRow;
        if (r0 == r9) goto L_0x0045;
    L_0x0043:
        r7 = r7 + 1;
    L_0x0045:
        r0 = r13.listAdapter;
        r1 = r13.sharedHeaderRow;
        r0.notifyItemRangeInserted(r1, r7);
        goto L_0x014a;
    L_0x004e:
        if (r0 == r9) goto L_0x014a;
    L_0x0050:
        r0 = r13.sharedHeaderRow;
        if (r0 == r9) goto L_0x014a;
    L_0x0054:
        if (r1 == r9) goto L_0x006a;
    L_0x0056:
        r0 = r13.photosRow;
        if (r0 == r9) goto L_0x006a;
    L_0x005a:
        r10 = r13.prevMediaCount;
        r11 = 0;
        r10 = r10[r11];
        r12 = r13.lastMediaCount;
        r11 = r12[r11];
        if (r10 == r11) goto L_0x006a;
    L_0x0065:
        r10 = r13.listAdapter;
        r10.notifyItemChanged(r0);
    L_0x006a:
        if (r2 == r9) goto L_0x0080;
    L_0x006c:
        r0 = r13.filesRow;
        if (r0 == r9) goto L_0x0080;
    L_0x0070:
        r10 = r13.prevMediaCount;
        r11 = 1;
        r10 = r10[r11];
        r12 = r13.lastMediaCount;
        r11 = r12[r11];
        if (r10 == r11) goto L_0x0080;
    L_0x007b:
        r10 = r13.listAdapter;
        r10.notifyItemChanged(r0);
    L_0x0080:
        if (r3 == r9) goto L_0x0095;
    L_0x0082:
        r0 = r13.linksRow;
        if (r0 == r9) goto L_0x0095;
    L_0x0086:
        r10 = r13.prevMediaCount;
        r10 = r10[r7];
        r11 = r13.lastMediaCount;
        r7 = r11[r7];
        if (r10 == r7) goto L_0x0095;
    L_0x0090:
        r7 = r13.listAdapter;
        r7.notifyItemChanged(r0);
    L_0x0095:
        if (r4 == r9) goto L_0x00ab;
    L_0x0097:
        r0 = r13.audioRow;
        if (r0 == r9) goto L_0x00ab;
    L_0x009b:
        r7 = r13.prevMediaCount;
        r10 = 4;
        r7 = r7[r10];
        r11 = r13.lastMediaCount;
        r10 = r11[r10];
        if (r7 == r10) goto L_0x00ab;
    L_0x00a6:
        r7 = r13.listAdapter;
        r7.notifyItemChanged(r0);
    L_0x00ab:
        if (r5 == r9) goto L_0x00c0;
    L_0x00ad:
        r0 = r13.voiceRow;
        if (r0 == r9) goto L_0x00c0;
    L_0x00b1:
        r7 = r13.prevMediaCount;
        r7 = r7[r8];
        r10 = r13.lastMediaCount;
        r8 = r10[r8];
        if (r7 == r8) goto L_0x00c0;
    L_0x00bb:
        r7 = r13.listAdapter;
        r7.notifyItemChanged(r0);
    L_0x00c0:
        if (r1 != r9) goto L_0x00cc;
    L_0x00c2:
        r0 = r13.photosRow;
        if (r0 == r9) goto L_0x00cc;
    L_0x00c6:
        r1 = r13.listAdapter;
        r1.notifyItemInserted(r0);
        goto L_0x00d7;
    L_0x00cc:
        if (r1 == r9) goto L_0x00d7;
    L_0x00ce:
        r0 = r13.photosRow;
        if (r0 != r9) goto L_0x00d7;
    L_0x00d2:
        r0 = r13.listAdapter;
        r0.notifyItemRemoved(r1);
    L_0x00d7:
        if (r2 != r9) goto L_0x00e3;
    L_0x00d9:
        r0 = r13.filesRow;
        if (r0 == r9) goto L_0x00e3;
    L_0x00dd:
        r1 = r13.listAdapter;
        r1.notifyItemInserted(r0);
        goto L_0x00ee;
    L_0x00e3:
        if (r2 == r9) goto L_0x00ee;
    L_0x00e5:
        r0 = r13.filesRow;
        if (r0 != r9) goto L_0x00ee;
    L_0x00e9:
        r0 = r13.listAdapter;
        r0.notifyItemRemoved(r2);
    L_0x00ee:
        if (r3 != r9) goto L_0x00fa;
    L_0x00f0:
        r0 = r13.linksRow;
        if (r0 == r9) goto L_0x00fa;
    L_0x00f4:
        r1 = r13.listAdapter;
        r1.notifyItemInserted(r0);
        goto L_0x0105;
    L_0x00fa:
        if (r3 == r9) goto L_0x0105;
    L_0x00fc:
        r0 = r13.linksRow;
        if (r0 != r9) goto L_0x0105;
    L_0x0100:
        r0 = r13.listAdapter;
        r0.notifyItemRemoved(r3);
    L_0x0105:
        if (r4 != r9) goto L_0x0111;
    L_0x0107:
        r0 = r13.audioRow;
        if (r0 == r9) goto L_0x0111;
    L_0x010b:
        r1 = r13.listAdapter;
        r1.notifyItemInserted(r0);
        goto L_0x011c;
    L_0x0111:
        if (r4 == r9) goto L_0x011c;
    L_0x0113:
        r0 = r13.audioRow;
        if (r0 != r9) goto L_0x011c;
    L_0x0117:
        r0 = r13.listAdapter;
        r0.notifyItemRemoved(r4);
    L_0x011c:
        if (r5 != r9) goto L_0x0128;
    L_0x011e:
        r0 = r13.voiceRow;
        if (r0 == r9) goto L_0x0128;
    L_0x0122:
        r1 = r13.listAdapter;
        r1.notifyItemInserted(r0);
        goto L_0x0133;
    L_0x0128:
        if (r5 == r9) goto L_0x0133;
    L_0x012a:
        r0 = r13.voiceRow;
        if (r0 != r9) goto L_0x0133;
    L_0x012e:
        r0 = r13.listAdapter;
        r0.notifyItemRemoved(r5);
    L_0x0133:
        if (r6 != r9) goto L_0x013f;
    L_0x0135:
        r0 = r13.groupsInCommonRow;
        if (r0 == r9) goto L_0x013f;
    L_0x0139:
        r1 = r13.listAdapter;
        r1.notifyItemInserted(r0);
        goto L_0x014a;
    L_0x013f:
        if (r6 == r9) goto L_0x014a;
    L_0x0141:
        r0 = r13.groupsInCommonRow;
        if (r0 != r9) goto L_0x014a;
    L_0x0145:
        r0 = r13.listAdapter;
        r0.notifyItemRemoved(r6);
    L_0x014a:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ProfileActivity.updateSharedMediaRows():void");
    }

    /* Access modifiers changed, original: protected */
    public void onTransitionAnimationStart(boolean z, boolean z2) {
        if (!(z && z2) && !z2 && this.playProfileAnimation && this.allowProfileAnimation) {
            this.openAnimationInProgress = true;
        }
        if (z) {
            NotificationCenter.getInstance(this.currentAccount).setAllowedNotificationsDutingAnimation(new int[]{NotificationCenter.dialogsNeedReload, NotificationCenter.closeChats, NotificationCenter.mediaCountDidLoad, NotificationCenter.mediaCountsDidLoad});
            NotificationCenter.getInstance(this.currentAccount).setAnimationInProgress(true);
        }
    }

    /* Access modifiers changed, original: protected */
    public void onTransitionAnimationEnd(boolean z, boolean z2) {
        if (z) {
            if (!z2 && this.playProfileAnimation && this.allowProfileAnimation) {
                this.openAnimationInProgress = false;
                if (this.recreateMenuAfterAnimation) {
                    createActionBarMenu();
                }
            }
            NotificationCenter.getInstance(this.currentAccount).setAnimationInProgress(false);
        }
    }

    public float getAnimationProgress() {
        return this.animationProgress;
    }

    @Keep
    public void setAnimationProgress(float f) {
        int i;
        float f2 = f;
        this.animationProgress = f2;
        this.listView.setAlpha(f2);
        this.listView.setTranslationX(((float) AndroidUtilities.dp(48.0f)) - (((float) AndroidUtilities.dp(48.0f)) * f2));
        int i2 = (this.user_id != 0 || (ChatObject.isChannel(this.chat_id, this.currentAccount) && !this.currentChat.megagroup)) ? 5 : this.chat_id;
        i2 = AvatarDrawable.getProfileBackColorForId(i2);
        int color = Theme.getColor("actionBarDefault");
        int red = Color.red(color);
        int green = Color.green(color);
        color = Color.blue(color);
        this.topView.setBackgroundColor(Color.rgb(red + ((int) (((float) (Color.red(i2) - red)) * f2)), green + ((int) (((float) (Color.green(i2) - green)) * f2)), color + ((int) (((float) (Color.blue(i2) - color)) * f2))));
        i2 = (this.user_id != 0 || (ChatObject.isChannel(this.chat_id, this.currentAccount) && !this.currentChat.megagroup)) ? 5 : this.chat_id;
        i2 = AvatarDrawable.getIconColorForId(i2);
        color = Theme.getColor("actionBarDefaultIcon");
        red = Color.red(color);
        green = Color.green(color);
        color = Color.blue(color);
        this.actionBar.setItemsColor(Color.rgb(red + ((int) (((float) (Color.red(i2) - red)) * f2)), green + ((int) (((float) (Color.green(i2) - green)) * f2)), color + ((int) (((float) (Color.blue(i2) - color)) * f2))), false);
        i2 = Theme.getColor("profile_title");
        red = Theme.getColor("actionBarDefaultTitle");
        green = Color.red(red);
        int green2 = Color.green(red);
        int blue = Color.blue(red);
        red = Color.alpha(red);
        int red2 = (int) (((float) (Color.red(i2) - green)) * f2);
        int green3 = (int) (((float) (Color.green(i2) - green2)) * f2);
        int blue2 = (int) (((float) (Color.blue(i2) - blue)) * f2);
        i2 = (int) (((float) (Color.alpha(i2) - red)) * f2);
        int i3 = 0;
        while (true) {
            i = 2;
            if (i3 >= 2) {
                break;
            }
            SimpleTextView[] simpleTextViewArr = this.nameTextView;
            if (simpleTextViewArr[i3] != null) {
                simpleTextViewArr[i3].setTextColor(Color.argb(red + i2, green + red2, green2 + green3, blue + blue2));
            }
            i3++;
        }
        if (this.isOnline[0]) {
            i2 = Theme.getColor("profile_status");
        } else {
            int i4 = (this.user_id != 0 || (ChatObject.isChannel(this.chat_id, this.currentAccount) && !this.currentChat.megagroup)) ? 5 : this.chat_id;
            i2 = AvatarDrawable.getProfileTextColorForId(i4);
        }
        color = 0;
        int color2 = Theme.getColor(this.isOnline[0] ? "chat_status" : "actionBarDefaultSubtitle");
        red = Color.red(color2);
        green = Color.green(color2);
        green2 = Color.blue(color2);
        color2 = Color.alpha(color2);
        blue = (int) (((float) (Color.red(i2) - red)) * f2);
        red2 = (int) (((float) (Color.green(i2) - green)) * f2);
        green3 = (int) (((float) (Color.blue(i2) - green2)) * f2);
        i2 = (int) (((float) (Color.alpha(i2) - color2)) * f2);
        while (color < i) {
            SimpleTextView[] simpleTextViewArr2 = this.onlineTextView;
            if (simpleTextViewArr2[color] != null) {
                simpleTextViewArr2[color].setTextColor(Color.argb(color2 + i2, red + blue, green + red2, green2 + green3));
            }
            color++;
            i = 2;
        }
        this.extraHeight = (int) (((float) this.initialAnimationExtraHeight) * f2);
        i2 = this.user_id;
        if (i2 == 0) {
            i2 = this.chat_id;
        }
        i2 = AvatarDrawable.getProfileColorForId(i2);
        color2 = this.user_id;
        if (color2 == 0) {
            color2 = this.chat_id;
        }
        color2 = AvatarDrawable.getColorForId(color2);
        if (i2 != color2) {
            this.avatarDrawable.setColor(Color.rgb(Color.red(color2) + ((int) (((float) (Color.red(i2) - Color.red(color2))) * f2)), Color.green(color2) + ((int) (((float) (Color.green(i2) - Color.green(color2))) * f2)), Color.blue(color2) + ((int) (((float) (Color.blue(i2) - Color.blue(color2))) * f2))));
            this.avatarImage.invalidate();
        }
        needLayout();
    }

    /* Access modifiers changed, original: protected */
    public AnimatorSet onCustomTransitionAnimation(boolean z, final Runnable runnable) {
        if (!this.playProfileAnimation || !this.allowProfileAnimation) {
            return null;
        }
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(180);
        this.listView.setLayerType(2, null);
        ActionBarMenu createMenu = this.actionBar.createMenu();
        if (createMenu.getItem(10) == null && this.animatingItem == null) {
            this.animatingItem = createMenu.addItem(10, NUM);
        }
        String str = "animationProgress";
        ArrayList arrayList;
        ImageView imageView;
        int i;
        Object obj;
        Property property;
        float[] fArr;
        ActionBarMenuItem actionBarMenuItem;
        if (z) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.onlineTextView[1].getLayoutParams();
            layoutParams.rightMargin = (int) ((AndroidUtilities.density * -21.0f) + ((float) AndroidUtilities.dp(8.0f)));
            this.onlineTextView[1].setLayoutParams(layoutParams);
            FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) this.nameTextView[1].getLayoutParams();
            float ceil = (float) ((int) Math.ceil((double) (((float) (AndroidUtilities.displaySize.x - AndroidUtilities.dp(126.0f))) + (AndroidUtilities.density * 21.0f))));
            if (ceil < (this.nameTextView[1].getPaint().measureText(this.nameTextView[1].getText().toString()) * 1.12f) + ((float) this.nameTextView[1].getSideDrawablesSize())) {
                layoutParams2.width = (int) Math.ceil((double) (ceil / 1.12f));
            } else {
                layoutParams2.width = -2;
            }
            this.nameTextView[1].setLayoutParams(layoutParams2);
            this.initialAnimationExtraHeight = AndroidUtilities.dp(88.0f);
            this.fragmentView.setBackgroundColor(0);
            setAnimationProgress(0.0f);
            arrayList = new ArrayList();
            arrayList.add(ObjectAnimator.ofFloat(this, str, new float[]{0.0f, 1.0f}));
            imageView = this.writeButton;
            if (imageView != null) {
                imageView.setScaleX(0.2f);
                this.writeButton.setScaleY(0.2f);
                this.writeButton.setAlpha(0.0f);
                arrayList.add(ObjectAnimator.ofFloat(this.writeButton, View.SCALE_X, new float[]{1.0f}));
                arrayList.add(ObjectAnimator.ofFloat(this.writeButton, View.SCALE_Y, new float[]{1.0f}));
                arrayList.add(ObjectAnimator.ofFloat(this.writeButton, View.ALPHA, new float[]{1.0f}));
            }
            i = 0;
            while (i < 2) {
                this.onlineTextView[i].setAlpha(i == 0 ? 1.0f : 0.0f);
                this.nameTextView[i].setAlpha(i == 0 ? 1.0f : 0.0f);
                obj = this.onlineTextView[i];
                property = View.ALPHA;
                fArr = new float[1];
                fArr[0] = i == 0 ? 0.0f : 1.0f;
                arrayList.add(ObjectAnimator.ofFloat(obj, property, fArr));
                obj = this.nameTextView[i];
                property = View.ALPHA;
                fArr = new float[1];
                fArr[0] = i == 0 ? 0.0f : 1.0f;
                arrayList.add(ObjectAnimator.ofFloat(obj, property, fArr));
                i++;
            }
            actionBarMenuItem = this.animatingItem;
            if (actionBarMenuItem != null) {
                actionBarMenuItem.setAlpha(1.0f);
                arrayList.add(ObjectAnimator.ofFloat(this.animatingItem, View.ALPHA, new float[]{0.0f}));
            }
            actionBarMenuItem = this.callItem;
            if (actionBarMenuItem != null) {
                actionBarMenuItem.setAlpha(0.0f);
                arrayList.add(ObjectAnimator.ofFloat(this.callItem, View.ALPHA, new float[]{1.0f}));
            }
            actionBarMenuItem = this.editItem;
            if (actionBarMenuItem != null) {
                actionBarMenuItem.setAlpha(0.0f);
                arrayList.add(ObjectAnimator.ofFloat(this.editItem, View.ALPHA, new float[]{1.0f}));
            }
            animatorSet.playTogether(arrayList);
        } else {
            this.initialAnimationExtraHeight = this.extraHeight;
            arrayList = new ArrayList();
            arrayList.add(ObjectAnimator.ofFloat(this, str, new float[]{1.0f, 0.0f}));
            imageView = this.writeButton;
            if (imageView != null) {
                arrayList.add(ObjectAnimator.ofFloat(imageView, View.SCALE_X, new float[]{0.2f}));
                arrayList.add(ObjectAnimator.ofFloat(this.writeButton, View.SCALE_Y, new float[]{0.2f}));
                arrayList.add(ObjectAnimator.ofFloat(this.writeButton, View.ALPHA, new float[]{0.0f}));
            }
            i = 0;
            while (i < 2) {
                obj = this.onlineTextView[i];
                property = View.ALPHA;
                fArr = new float[1];
                fArr[0] = i == 0 ? 1.0f : 0.0f;
                arrayList.add(ObjectAnimator.ofFloat(obj, property, fArr));
                obj = this.nameTextView[i];
                property = View.ALPHA;
                fArr = new float[1];
                fArr[0] = i == 0 ? 1.0f : 0.0f;
                arrayList.add(ObjectAnimator.ofFloat(obj, property, fArr));
                i++;
            }
            actionBarMenuItem = this.animatingItem;
            if (actionBarMenuItem != null) {
                actionBarMenuItem.setAlpha(0.0f);
                arrayList.add(ObjectAnimator.ofFloat(this.animatingItem, View.ALPHA, new float[]{1.0f}));
            }
            actionBarMenuItem = this.callItem;
            if (actionBarMenuItem != null) {
                actionBarMenuItem.setAlpha(1.0f);
                arrayList.add(ObjectAnimator.ofFloat(this.callItem, View.ALPHA, new float[]{0.0f}));
            }
            actionBarMenuItem = this.editItem;
            if (actionBarMenuItem != null) {
                actionBarMenuItem.setAlpha(1.0f);
                arrayList.add(ObjectAnimator.ofFloat(this.editItem, View.ALPHA, new float[]{0.0f}));
            }
            animatorSet.playTogether(arrayList);
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
        animatorSet.getClass();
        AndroidUtilities.runOnUIThread(new -$$Lambda$V1ckApGFHcFeYW_JU7RAm0ElIv8(animatorSet), 50);
        return animatorSet;
    }

    private void updateOnlineCount() {
        int i = 0;
        this.onlineCount = 0;
        int currentTime = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
        this.sortedUsers.clear();
        ChatFull chatFull = this.chatInfo;
        if ((chatFull instanceof TL_chatFull) || ((chatFull instanceof TL_channelFull) && chatFull.participants_count <= 200 && chatFull.participants != null)) {
            while (i < this.chatInfo.participants.participants.size()) {
                User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(((ChatParticipant) this.chatInfo.participants.participants.get(i)).user_id));
                if (user != null) {
                    UserStatus userStatus = user.status;
                    if (userStatus != null && ((userStatus.expires > currentTime || user.id == UserConfig.getInstance(this.currentAccount).getClientUserId()) && user.status.expires > 10000)) {
                        this.onlineCount++;
                    }
                }
                this.sortedUsers.add(Integer.valueOf(i));
                i++;
            }
            try {
                Collections.sort(this.sortedUsers, new -$$Lambda$ProfileActivity$qRSHt0LtTrY0lhK6FdXXdigGth4(this, currentTime));
            } catch (Exception e) {
                FileLog.e(e);
            }
            ListAdapter listAdapter = this.listAdapter;
            if (listAdapter != null) {
                currentTime = this.membersStartRow;
                if (currentTime > 0) {
                    listAdapter.notifyItemRangeChanged(currentTime, this.sortedUsers.size());
                    return;
                }
                return;
            }
            return;
        }
        ChatFull chatFull2 = this.chatInfo;
        if ((chatFull2 instanceof TL_channelFull) && chatFull2.participants_count > 200) {
            this.onlineCount = chatFull2.online_count;
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:13:0x0060  */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x0078 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:31:0x0083 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x008e A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:43:0x0097 A:{SKIP} */
    public /* synthetic */ int lambda$updateOnlineCount$20$ProfileActivity(int r5, java.lang.Integer r6, java.lang.Integer r7) {
        /*
        r4 = this;
        r0 = r4.currentAccount;
        r0 = org.telegram.messenger.MessagesController.getInstance(r0);
        r1 = r4.chatInfo;
        r1 = r1.participants;
        r1 = r1.participants;
        r7 = r7.intValue();
        r7 = r1.get(r7);
        r7 = (org.telegram.tgnet.TLRPC.ChatParticipant) r7;
        r7 = r7.user_id;
        r7 = java.lang.Integer.valueOf(r7);
        r7 = r0.getUser(r7);
        r0 = r4.currentAccount;
        r0 = org.telegram.messenger.MessagesController.getInstance(r0);
        r1 = r4.chatInfo;
        r1 = r1.participants;
        r1 = r1.participants;
        r6 = r6.intValue();
        r6 = r1.get(r6);
        r6 = (org.telegram.tgnet.TLRPC.ChatParticipant) r6;
        r6 = r6.user_id;
        r6 = java.lang.Integer.valueOf(r6);
        r6 = r0.getUser(r6);
        r0 = 50000; // 0xCLASSNAME float:7.0065E-41 double:2.47033E-319;
        r1 = -110; // 0xfffffffffffffvar_ float:NaN double:NaN;
        r2 = 0;
        if (r7 == 0) goto L_0x005d;
    L_0x0048:
        r3 = r7.bot;
        if (r3 == 0) goto L_0x004f;
    L_0x004c:
        r7 = -110; // 0xfffffffffffffvar_ float:NaN double:NaN;
        goto L_0x005e;
    L_0x004f:
        r3 = r7.self;
        if (r3 == 0) goto L_0x0056;
    L_0x0053:
        r7 = r5 + r0;
        goto L_0x005e;
    L_0x0056:
        r7 = r7.status;
        if (r7 == 0) goto L_0x005d;
    L_0x005a:
        r7 = r7.expires;
        goto L_0x005e;
    L_0x005d:
        r7 = 0;
    L_0x005e:
        if (r6 == 0) goto L_0x0073;
    L_0x0060:
        r3 = r6.bot;
        if (r3 == 0) goto L_0x0065;
    L_0x0064:
        goto L_0x0074;
    L_0x0065:
        r1 = r6.self;
        if (r1 == 0) goto L_0x006c;
    L_0x0069:
        r1 = r5 + r0;
        goto L_0x0074;
    L_0x006c:
        r5 = r6.status;
        if (r5 == 0) goto L_0x0073;
    L_0x0070:
        r1 = r5.expires;
        goto L_0x0074;
    L_0x0073:
        r1 = 0;
    L_0x0074:
        r5 = -1;
        r6 = 1;
        if (r7 <= 0) goto L_0x0081;
    L_0x0078:
        if (r1 <= 0) goto L_0x0081;
    L_0x007a:
        if (r7 <= r1) goto L_0x007d;
    L_0x007c:
        return r6;
    L_0x007d:
        if (r7 >= r1) goto L_0x0080;
    L_0x007f:
        return r5;
    L_0x0080:
        return r2;
    L_0x0081:
        if (r7 >= 0) goto L_0x008c;
    L_0x0083:
        if (r1 >= 0) goto L_0x008c;
    L_0x0085:
        if (r7 <= r1) goto L_0x0088;
    L_0x0087:
        return r6;
    L_0x0088:
        if (r7 >= r1) goto L_0x008b;
    L_0x008a:
        return r5;
    L_0x008b:
        return r2;
    L_0x008c:
        if (r7 >= 0) goto L_0x0090;
    L_0x008e:
        if (r1 > 0) goto L_0x0094;
    L_0x0090:
        if (r7 != 0) goto L_0x0095;
    L_0x0092:
        if (r1 == 0) goto L_0x0095;
    L_0x0094:
        return r5;
    L_0x0095:
        if (r1 >= 0) goto L_0x0099;
    L_0x0097:
        if (r7 > 0) goto L_0x009d;
    L_0x0099:
        if (r1 != 0) goto L_0x009e;
    L_0x009b:
        if (r7 == 0) goto L_0x009e;
    L_0x009d:
        return r6;
    L_0x009e:
        return r2;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ProfileActivity.lambda$updateOnlineCount$20$ProfileActivity(int, java.lang.Integer, java.lang.Integer):int");
    }

    public void setChatInfo(ChatFull chatFull) {
        this.chatInfo = chatFull;
        chatFull = this.chatInfo;
        if (chatFull != null) {
            int i = chatFull.migrated_from_chat_id;
            if (i != 0 && this.mergeDialogId == 0) {
                this.mergeDialogId = (long) (-i);
                MediaDataController.getInstance(this.currentAccount).getMediaCounts(this.mergeDialogId, this.classGuid);
            }
        }
        fetchUsersFromChannelInfo();
    }

    public void setUserInfo(UserFull userFull) {
        this.userInfo = userFull;
    }

    private void fetchUsersFromChannelInfo() {
        Chat chat = this.currentChat;
        if (chat != null && chat.megagroup) {
            ChatFull chatFull = this.chatInfo;
            if ((chatFull instanceof TL_channelFull) && chatFull.participants != null) {
                for (int i = 0; i < this.chatInfo.participants.participants.size(); i++) {
                    ChatParticipant chatParticipant = (ChatParticipant) this.chatInfo.participants.participants.get(i);
                    this.participantsMap.put(chatParticipant.user_id, chatParticipant);
                }
            }
        }
    }

    private void kickUser(int i) {
        if (i != 0) {
            MessagesController.getInstance(this.currentAccount).deleteUserFromChat(this.chat_id, MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(i)), this.chatInfo);
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
        finishFragment();
    }

    public boolean isChat() {
        return this.chat_id != 0;
    }

    /* JADX WARNING: Missing block: B:61:0x013e, code skipped:
            if (r2.common_chats_count != 0) goto L_0x0140;
     */
    private void updateRowsIds() {
        /*
        r12 = this;
        r0 = 0;
        r12.rowCount = r0;
        r1 = -1;
        r12.emptyRow = r1;
        r12.infoHeaderRow = r1;
        r12.phoneRow = r1;
        r12.userInfoRow = r1;
        r12.locationRow = r1;
        r12.channelInfoRow = r1;
        r12.usernameRow = r1;
        r12.settingsTimerRow = r1;
        r12.settingsKeyRow = r1;
        r12.notificationsDividerRow = r1;
        r12.notificationsRow = r1;
        r12.infoSectionRow = r1;
        r12.settingsSectionRow = r1;
        r12.membersHeaderRow = r1;
        r12.membersStartRow = r1;
        r12.membersEndRow = r1;
        r12.addMemberRow = r1;
        r12.subscribersRow = r1;
        r12.administratorsRow = r1;
        r12.blockedUsersRow = r1;
        r12.membersSectionRow = r1;
        r12.sharedHeaderRow = r1;
        r12.photosRow = r1;
        r12.filesRow = r1;
        r12.linksRow = r1;
        r12.audioRow = r1;
        r12.voiceRow = r1;
        r12.groupsInCommonRow = r1;
        r12.sharedSectionRow = r1;
        r12.unblockRow = r1;
        r12.startSecretChatRow = r1;
        r12.leaveChannelRow = r1;
        r12.joinRow = r1;
        r12.lastSectionRow = r1;
        r2 = 0;
    L_0x0049:
        r3 = r12.lastMediaCount;
        r4 = r3.length;
        r5 = 1;
        if (r2 >= r4) goto L_0x0058;
    L_0x004f:
        r3 = r3[r2];
        if (r3 <= 0) goto L_0x0055;
    L_0x0053:
        r2 = 1;
        goto L_0x0059;
    L_0x0055:
        r2 = r2 + 1;
        goto L_0x0049;
    L_0x0058:
        r2 = 0;
    L_0x0059:
        r3 = r12.user_id;
        if (r3 == 0) goto L_0x0069;
    L_0x005d:
        r3 = org.telegram.messenger.LocaleController.isRTL;
        if (r3 == 0) goto L_0x0069;
    L_0x0061:
        r3 = r12.rowCount;
        r4 = r3 + 1;
        r12.rowCount = r4;
        r12.emptyRow = r3;
    L_0x0069:
        r3 = r12.user_id;
        r4 = 2;
        r6 = 4;
        r7 = 3;
        if (r3 == 0) goto L_0x01ec;
    L_0x0070:
        r3 = r12.currentAccount;
        r3 = org.telegram.messenger.MessagesController.getInstance(r3);
        r8 = r12.user_id;
        r8 = java.lang.Integer.valueOf(r8);
        r3 = r3.getUser(r8);
        r8 = r12.userInfo;
        if (r8 == 0) goto L_0x008c;
    L_0x0084:
        r8 = r8.about;
        r8 = android.text.TextUtils.isEmpty(r8);
        if (r8 == 0) goto L_0x0096;
    L_0x008c:
        if (r3 == 0) goto L_0x0098;
    L_0x008e:
        r8 = r3.username;
        r8 = android.text.TextUtils.isEmpty(r8);
        if (r8 != 0) goto L_0x0098;
    L_0x0096:
        r8 = 1;
        goto L_0x0099;
    L_0x0098:
        r8 = 0;
    L_0x0099:
        if (r3 == 0) goto L_0x00a5;
    L_0x009b:
        r9 = r3.phone;
        r9 = android.text.TextUtils.isEmpty(r9);
        if (r9 != 0) goto L_0x00a5;
    L_0x00a3:
        r9 = 1;
        goto L_0x00a6;
    L_0x00a5:
        r9 = 0;
    L_0x00a6:
        r10 = r12.rowCount;
        r11 = r10 + 1;
        r12.rowCount = r11;
        r12.infoHeaderRow = r10;
        r10 = r12.isBot;
        if (r10 != 0) goto L_0x00c0;
    L_0x00b2:
        if (r9 != 0) goto L_0x00b8;
    L_0x00b4:
        if (r9 != 0) goto L_0x00c0;
    L_0x00b6:
        if (r8 != 0) goto L_0x00c0;
    L_0x00b8:
        r8 = r12.rowCount;
        r9 = r8 + 1;
        r12.rowCount = r9;
        r12.phoneRow = r8;
    L_0x00c0:
        r8 = r12.userInfo;
        if (r8 == 0) goto L_0x00d4;
    L_0x00c4:
        r8 = r8.about;
        r8 = android.text.TextUtils.isEmpty(r8);
        if (r8 != 0) goto L_0x00d4;
    L_0x00cc:
        r8 = r12.rowCount;
        r9 = r8 + 1;
        r12.rowCount = r9;
        r12.userInfoRow = r8;
    L_0x00d4:
        if (r3 == 0) goto L_0x00e6;
    L_0x00d6:
        r8 = r3.username;
        r8 = android.text.TextUtils.isEmpty(r8);
        if (r8 != 0) goto L_0x00e6;
    L_0x00de:
        r8 = r12.rowCount;
        r9 = r8 + 1;
        r12.rowCount = r9;
        r12.usernameRow = r8;
    L_0x00e6:
        r8 = r12.phoneRow;
        if (r8 != r1) goto L_0x00f2;
    L_0x00ea:
        r8 = r12.userInfoRow;
        if (r8 != r1) goto L_0x00f2;
    L_0x00ee:
        r8 = r12.usernameRow;
        if (r8 == r1) goto L_0x00fa;
    L_0x00f2:
        r8 = r12.rowCount;
        r9 = r8 + 1;
        r12.rowCount = r9;
        r12.notificationsDividerRow = r8;
    L_0x00fa:
        r8 = r12.user_id;
        r9 = r12.currentAccount;
        r9 = org.telegram.messenger.UserConfig.getInstance(r9);
        r9 = r9.getClientUserId();
        if (r8 == r9) goto L_0x0110;
    L_0x0108:
        r8 = r12.rowCount;
        r9 = r8 + 1;
        r12.rowCount = r9;
        r12.notificationsRow = r8;
    L_0x0110:
        r8 = r12.rowCount;
        r9 = r8 + 1;
        r12.rowCount = r9;
        r12.infoSectionRow = r8;
        r8 = r12.currentEncryptedChat;
        r8 = r8 instanceof org.telegram.tgnet.TLRPC.TL_encryptedChat;
        if (r8 == 0) goto L_0x0136;
    L_0x011e:
        r8 = r12.rowCount;
        r9 = r8 + 1;
        r12.rowCount = r9;
        r12.settingsTimerRow = r8;
        r8 = r12.rowCount;
        r9 = r8 + 1;
        r12.rowCount = r9;
        r12.settingsKeyRow = r8;
        r8 = r12.rowCount;
        r9 = r8 + 1;
        r12.rowCount = r9;
        r12.settingsSectionRow = r8;
    L_0x0136:
        if (r2 != 0) goto L_0x0140;
    L_0x0138:
        r2 = r12.userInfo;
        if (r2 == 0) goto L_0x01b5;
    L_0x013c:
        r2 = r2.common_chats_count;
        if (r2 == 0) goto L_0x01b5;
    L_0x0140:
        r2 = r12.rowCount;
        r8 = r2 + 1;
        r12.rowCount = r8;
        r12.sharedHeaderRow = r2;
        r2 = r12.lastMediaCount;
        r0 = r2[r0];
        if (r0 <= 0) goto L_0x0157;
    L_0x014e:
        r0 = r12.rowCount;
        r2 = r0 + 1;
        r12.rowCount = r2;
        r12.photosRow = r0;
        goto L_0x0159;
    L_0x0157:
        r12.photosRow = r1;
    L_0x0159:
        r0 = r12.lastMediaCount;
        r0 = r0[r5];
        if (r0 <= 0) goto L_0x0168;
    L_0x015f:
        r0 = r12.rowCount;
        r2 = r0 + 1;
        r12.rowCount = r2;
        r12.filesRow = r0;
        goto L_0x016a;
    L_0x0168:
        r12.filesRow = r1;
    L_0x016a:
        r0 = r12.lastMediaCount;
        r0 = r0[r7];
        if (r0 <= 0) goto L_0x0179;
    L_0x0170:
        r0 = r12.rowCount;
        r2 = r0 + 1;
        r12.rowCount = r2;
        r12.linksRow = r0;
        goto L_0x017b;
    L_0x0179:
        r12.linksRow = r1;
    L_0x017b:
        r0 = r12.lastMediaCount;
        r0 = r0[r6];
        if (r0 <= 0) goto L_0x018a;
    L_0x0181:
        r0 = r12.rowCount;
        r2 = r0 + 1;
        r12.rowCount = r2;
        r12.audioRow = r0;
        goto L_0x018c;
    L_0x018a:
        r12.audioRow = r1;
    L_0x018c:
        r0 = r12.lastMediaCount;
        r0 = r0[r4];
        if (r0 <= 0) goto L_0x019b;
    L_0x0192:
        r0 = r12.rowCount;
        r1 = r0 + 1;
        r12.rowCount = r1;
        r12.voiceRow = r0;
        goto L_0x019d;
    L_0x019b:
        r12.voiceRow = r1;
    L_0x019d:
        r0 = r12.userInfo;
        if (r0 == 0) goto L_0x01ad;
    L_0x01a1:
        r0 = r0.common_chats_count;
        if (r0 == 0) goto L_0x01ad;
    L_0x01a5:
        r0 = r12.rowCount;
        r1 = r0 + 1;
        r12.rowCount = r1;
        r12.groupsInCommonRow = r0;
    L_0x01ad:
        r0 = r12.rowCount;
        r1 = r0 + 1;
        r12.rowCount = r1;
        r12.sharedSectionRow = r0;
    L_0x01b5:
        if (r3 == 0) goto L_0x0450;
    L_0x01b7:
        r0 = r12.isBot;
        if (r0 != 0) goto L_0x0450;
    L_0x01bb:
        r0 = r12.currentEncryptedChat;
        if (r0 != 0) goto L_0x0450;
    L_0x01bf:
        r0 = r3.id;
        r1 = r12.currentAccount;
        r1 = org.telegram.messenger.UserConfig.getInstance(r1);
        r1 = r1.getClientUserId();
        if (r0 == r1) goto L_0x0450;
    L_0x01cd:
        r0 = r12.userBlocked;
        if (r0 == 0) goto L_0x01da;
    L_0x01d1:
        r0 = r12.rowCount;
        r1 = r0 + 1;
        r12.rowCount = r1;
        r12.unblockRow = r0;
        goto L_0x01e2;
    L_0x01da:
        r0 = r12.rowCount;
        r1 = r0 + 1;
        r12.rowCount = r1;
        r12.startSecretChatRow = r0;
    L_0x01e2:
        r0 = r12.rowCount;
        r1 = r0 + 1;
        r12.rowCount = r1;
        r12.lastSectionRow = r0;
        goto L_0x0450;
    L_0x01ec:
        r3 = r12.chat_id;
        if (r3 == 0) goto L_0x0450;
    L_0x01f0:
        if (r3 <= 0) goto L_0x0417;
    L_0x01f2:
        r3 = r12.chatInfo;
        if (r3 == 0) goto L_0x0206;
    L_0x01f6:
        r3 = r3.about;
        r3 = android.text.TextUtils.isEmpty(r3);
        if (r3 == 0) goto L_0x0210;
    L_0x01fe:
        r3 = r12.chatInfo;
        r3 = r3.location;
        r3 = r3 instanceof org.telegram.tgnet.TLRPC.TL_channelLocation;
        if (r3 != 0) goto L_0x0210;
    L_0x0206:
        r3 = r12.currentChat;
        r3 = r3.username;
        r3 = android.text.TextUtils.isEmpty(r3);
        if (r3 != 0) goto L_0x024e;
    L_0x0210:
        r3 = r12.rowCount;
        r8 = r3 + 1;
        r12.rowCount = r8;
        r12.infoHeaderRow = r3;
        r3 = r12.chatInfo;
        if (r3 == 0) goto L_0x023c;
    L_0x021c:
        r3 = r3.about;
        r3 = android.text.TextUtils.isEmpty(r3);
        if (r3 != 0) goto L_0x022c;
    L_0x0224:
        r3 = r12.rowCount;
        r8 = r3 + 1;
        r12.rowCount = r8;
        r12.channelInfoRow = r3;
    L_0x022c:
        r3 = r12.chatInfo;
        r3 = r3.location;
        r3 = r3 instanceof org.telegram.tgnet.TLRPC.TL_channelLocation;
        if (r3 == 0) goto L_0x023c;
    L_0x0234:
        r3 = r12.rowCount;
        r8 = r3 + 1;
        r12.rowCount = r8;
        r12.locationRow = r3;
    L_0x023c:
        r3 = r12.currentChat;
        r3 = r3.username;
        r3 = android.text.TextUtils.isEmpty(r3);
        if (r3 != 0) goto L_0x024e;
    L_0x0246:
        r3 = r12.rowCount;
        r8 = r3 + 1;
        r12.rowCount = r8;
        r12.usernameRow = r3;
    L_0x024e:
        r3 = r12.infoHeaderRow;
        if (r3 == r1) goto L_0x025a;
    L_0x0252:
        r3 = r12.rowCount;
        r8 = r3 + 1;
        r12.rowCount = r8;
        r12.notificationsDividerRow = r3;
    L_0x025a:
        r3 = r12.rowCount;
        r8 = r3 + 1;
        r12.rowCount = r8;
        r12.notificationsRow = r3;
        r3 = r12.rowCount;
        r8 = r3 + 1;
        r12.rowCount = r8;
        r12.infoSectionRow = r3;
        r3 = r12.currentChat;
        r3 = org.telegram.messenger.ChatObject.isChannel(r3);
        if (r3 == 0) goto L_0x02b6;
    L_0x0272:
        r3 = r12.currentChat;
        r8 = r3.megagroup;
        if (r8 != 0) goto L_0x02b6;
    L_0x0278:
        r8 = r12.chatInfo;
        if (r8 == 0) goto L_0x02b6;
    L_0x027c:
        r3 = r3.creator;
        if (r3 != 0) goto L_0x0284;
    L_0x0280:
        r3 = r8.can_view_participants;
        if (r3 == 0) goto L_0x02b6;
    L_0x0284:
        r3 = r12.rowCount;
        r8 = r3 + 1;
        r12.rowCount = r8;
        r12.membersHeaderRow = r3;
        r3 = r12.rowCount;
        r8 = r3 + 1;
        r12.rowCount = r8;
        r12.subscribersRow = r3;
        r3 = r12.rowCount;
        r8 = r3 + 1;
        r12.rowCount = r8;
        r12.administratorsRow = r3;
        r3 = r12.chatInfo;
        r8 = r3.banned_count;
        if (r8 != 0) goto L_0x02a6;
    L_0x02a2:
        r3 = r3.kicked_count;
        if (r3 == 0) goto L_0x02ae;
    L_0x02a6:
        r3 = r12.rowCount;
        r8 = r3 + 1;
        r12.rowCount = r8;
        r12.blockedUsersRow = r3;
    L_0x02ae:
        r3 = r12.rowCount;
        r8 = r3 + 1;
        r12.rowCount = r8;
        r12.membersSectionRow = r3;
    L_0x02b6:
        if (r2 == 0) goto L_0x031d;
    L_0x02b8:
        r2 = r12.rowCount;
        r3 = r2 + 1;
        r12.rowCount = r3;
        r12.sharedHeaderRow = r2;
        r2 = r12.lastMediaCount;
        r0 = r2[r0];
        if (r0 <= 0) goto L_0x02cf;
    L_0x02c6:
        r0 = r12.rowCount;
        r2 = r0 + 1;
        r12.rowCount = r2;
        r12.photosRow = r0;
        goto L_0x02d1;
    L_0x02cf:
        r12.photosRow = r1;
    L_0x02d1:
        r0 = r12.lastMediaCount;
        r0 = r0[r5];
        if (r0 <= 0) goto L_0x02e0;
    L_0x02d7:
        r0 = r12.rowCount;
        r2 = r0 + 1;
        r12.rowCount = r2;
        r12.filesRow = r0;
        goto L_0x02e2;
    L_0x02e0:
        r12.filesRow = r1;
    L_0x02e2:
        r0 = r12.lastMediaCount;
        r0 = r0[r7];
        if (r0 <= 0) goto L_0x02f1;
    L_0x02e8:
        r0 = r12.rowCount;
        r2 = r0 + 1;
        r12.rowCount = r2;
        r12.linksRow = r0;
        goto L_0x02f3;
    L_0x02f1:
        r12.linksRow = r1;
    L_0x02f3:
        r0 = r12.lastMediaCount;
        r0 = r0[r6];
        if (r0 <= 0) goto L_0x0302;
    L_0x02f9:
        r0 = r12.rowCount;
        r2 = r0 + 1;
        r12.rowCount = r2;
        r12.audioRow = r0;
        goto L_0x0304;
    L_0x0302:
        r12.audioRow = r1;
    L_0x0304:
        r0 = r12.lastMediaCount;
        r0 = r0[r4];
        if (r0 <= 0) goto L_0x0313;
    L_0x030a:
        r0 = r12.rowCount;
        r2 = r0 + 1;
        r12.rowCount = r2;
        r12.voiceRow = r0;
        goto L_0x0315;
    L_0x0313:
        r12.voiceRow = r1;
    L_0x0315:
        r0 = r12.rowCount;
        r2 = r0 + 1;
        r12.rowCount = r2;
        r12.sharedSectionRow = r0;
    L_0x031d:
        r0 = r12.currentChat;
        r0 = org.telegram.messenger.ChatObject.isChannel(r0);
        if (r0 == 0) goto L_0x03cd;
    L_0x0325:
        r0 = r12.currentChat;
        r2 = r0.creator;
        if (r2 != 0) goto L_0x0347;
    L_0x032b:
        r2 = r0.left;
        if (r2 != 0) goto L_0x0347;
    L_0x032f:
        r2 = r0.kicked;
        if (r2 != 0) goto L_0x0347;
    L_0x0333:
        r0 = r0.megagroup;
        if (r0 != 0) goto L_0x0347;
    L_0x0337:
        r0 = r12.rowCount;
        r2 = r0 + 1;
        r12.rowCount = r2;
        r12.leaveChannelRow = r0;
        r0 = r12.rowCount;
        r2 = r0 + 1;
        r12.rowCount = r2;
        r12.lastSectionRow = r0;
    L_0x0347:
        r0 = r12.chatInfo;
        if (r0 == 0) goto L_0x03ad;
    L_0x034b:
        r2 = r12.currentChat;
        r2 = r2.megagroup;
        if (r2 == 0) goto L_0x03ad;
    L_0x0351:
        r0 = r0.participants;
        if (r0 == 0) goto L_0x03ad;
    L_0x0355:
        r0 = r0.participants;
        r0 = r0.isEmpty();
        if (r0 != 0) goto L_0x03ad;
    L_0x035d:
        r0 = r12.currentChat;
        r0 = org.telegram.messenger.ChatObject.isNotInChat(r0);
        if (r0 != 0) goto L_0x038a;
    L_0x0365:
        r0 = r12.currentChat;
        r2 = r0.megagroup;
        if (r2 == 0) goto L_0x038a;
    L_0x036b:
        r0 = org.telegram.messenger.ChatObject.canAddUsers(r0);
        if (r0 == 0) goto L_0x038a;
    L_0x0371:
        r0 = r12.chatInfo;
        if (r0 == 0) goto L_0x0381;
    L_0x0375:
        r0 = r0.participants_count;
        r2 = r12.currentAccount;
        r2 = org.telegram.messenger.MessagesController.getInstance(r2);
        r2 = r2.maxMegagroupCount;
        if (r0 >= r2) goto L_0x038a;
    L_0x0381:
        r0 = r12.rowCount;
        r2 = r0 + 1;
        r12.rowCount = r2;
        r12.addMemberRow = r0;
        goto L_0x0392;
    L_0x038a:
        r0 = r12.rowCount;
        r2 = r0 + 1;
        r12.rowCount = r2;
        r12.membersHeaderRow = r0;
    L_0x0392:
        r0 = r12.rowCount;
        r12.membersStartRow = r0;
        r2 = r12.chatInfo;
        r2 = r2.participants;
        r2 = r2.participants;
        r2 = r2.size();
        r0 = r0 + r2;
        r12.rowCount = r0;
        r0 = r12.rowCount;
        r12.membersEndRow = r0;
        r2 = r0 + 1;
        r12.rowCount = r2;
        r12.membersSectionRow = r0;
    L_0x03ad:
        r0 = r12.lastSectionRow;
        if (r0 != r1) goto L_0x0450;
    L_0x03b1:
        r0 = r12.currentChat;
        r1 = r0.left;
        if (r1 == 0) goto L_0x0450;
    L_0x03b7:
        r0 = r0.kicked;
        if (r0 != 0) goto L_0x0450;
    L_0x03bb:
        r0 = r12.rowCount;
        r1 = r0 + 1;
        r12.rowCount = r1;
        r12.joinRow = r0;
        r0 = r12.rowCount;
        r1 = r0 + 1;
        r12.rowCount = r1;
        r12.lastSectionRow = r0;
        goto L_0x0450;
    L_0x03cd:
        r0 = r12.chatInfo;
        if (r0 == 0) goto L_0x0450;
    L_0x03d1:
        r0 = r0.participants;
        r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_chatParticipantsForbidden;
        if (r0 != 0) goto L_0x0450;
    L_0x03d7:
        r0 = r12.currentChat;
        r0 = org.telegram.messenger.ChatObject.canAddUsers(r0);
        if (r0 != 0) goto L_0x03f3;
    L_0x03df:
        r0 = r12.currentChat;
        r0 = r0.default_banned_rights;
        if (r0 == 0) goto L_0x03f3;
    L_0x03e5:
        r0 = r0.invite_users;
        if (r0 != 0) goto L_0x03ea;
    L_0x03e9:
        goto L_0x03f3;
    L_0x03ea:
        r0 = r12.rowCount;
        r1 = r0 + 1;
        r12.rowCount = r1;
        r12.membersHeaderRow = r0;
        goto L_0x03fb;
    L_0x03f3:
        r0 = r12.rowCount;
        r1 = r0 + 1;
        r12.rowCount = r1;
        r12.addMemberRow = r0;
    L_0x03fb:
        r0 = r12.rowCount;
        r12.membersStartRow = r0;
        r1 = r12.chatInfo;
        r1 = r1.participants;
        r1 = r1.participants;
        r1 = r1.size();
        r0 = r0 + r1;
        r12.rowCount = r0;
        r0 = r12.rowCount;
        r12.membersEndRow = r0;
        r1 = r0 + 1;
        r12.rowCount = r1;
        r12.membersSectionRow = r0;
        goto L_0x0450;
    L_0x0417:
        r0 = r12.currentChat;
        r0 = org.telegram.messenger.ChatObject.isChannel(r0);
        if (r0 != 0) goto L_0x0450;
    L_0x041f:
        r0 = r12.chatInfo;
        if (r0 == 0) goto L_0x0450;
    L_0x0423:
        r0 = r0.participants;
        r1 = r0 instanceof org.telegram.tgnet.TLRPC.TL_chatParticipantsForbidden;
        if (r1 != 0) goto L_0x0450;
    L_0x0429:
        r1 = r12.rowCount;
        r2 = r1 + 1;
        r12.rowCount = r2;
        r12.membersHeaderRow = r1;
        r1 = r12.rowCount;
        r12.membersStartRow = r1;
        r0 = r0.participants;
        r0 = r0.size();
        r1 = r1 + r0;
        r12.rowCount = r1;
        r0 = r12.rowCount;
        r12.membersEndRow = r0;
        r1 = r0 + 1;
        r12.rowCount = r1;
        r12.membersSectionRow = r0;
        r0 = r12.rowCount;
        r1 = r0 + 1;
        r12.rowCount = r1;
        r12.addMemberRow = r0;
    L_0x0450:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ProfileActivity.updateRowsIds():void");
    }

    private Drawable getScamDrawable() {
        if (this.scamDrawable == null) {
            this.scamDrawable = new ScamDrawable(11);
            ScamDrawable scamDrawable = this.scamDrawable;
            int i = (this.user_id != 0 || (ChatObject.isChannel(this.chat_id, this.currentAccount) && !this.currentChat.megagroup)) ? 5 : this.chat_id;
            scamDrawable.setColor(AvatarDrawable.getProfileTextColorForId(i));
        }
        return this.scamDrawable;
    }

    /* JADX WARNING: Removed duplicated region for block: B:77:0x01dd  */
    /* JADX WARNING: Removed duplicated region for block: B:81:0x01eb  */
    /* JADX WARNING: Removed duplicated region for block: B:80:0x01e8  */
    /* JADX WARNING: Removed duplicated region for block: B:92:0x0212  */
    /* JADX WARNING: Removed duplicated region for block: B:83:0x01ee  */
    /* JADX WARNING: Removed duplicated region for block: B:222:0x04ee  */
    /* JADX WARNING: Removed duplicated region for block: B:212:0x0489  */
    /* JADX WARNING: Removed duplicated region for block: B:188:0x042c A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:192:0x043d  */
    /* JADX WARNING: Removed duplicated region for block: B:212:0x0489  */
    /* JADX WARNING: Removed duplicated region for block: B:222:0x04ee  */
    private void updateProfileData() {
        /*
        r20 = this;
        r0 = r20;
        r1 = r0.avatarImage;
        if (r1 == 0) goto L_0x0575;
    L_0x0006:
        r1 = r0.nameTextView;
        if (r1 != 0) goto L_0x000c;
    L_0x000a:
        goto L_0x0575;
    L_0x000c:
        r1 = r0.currentAccount;
        r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r1);
        r1 = r1.getConnectionState();
        r2 = 2;
        r4 = 1;
        if (r1 != r2) goto L_0x0024;
    L_0x001a:
        r1 = NUM; // 0x7f0d0ad7 float:1.8747743E38 double:1.0531311486E-314;
        r5 = "WaitingForNetwork";
        r1 = org.telegram.messenger.LocaleController.getString(r5, r1);
        goto L_0x004b;
    L_0x0024:
        if (r1 != r4) goto L_0x0030;
    L_0x0026:
        r1 = NUM; // 0x7f0d02f3 float:1.8743646E38 double:1.0531301506E-314;
        r5 = "Connecting";
        r1 = org.telegram.messenger.LocaleController.getString(r5, r1);
        goto L_0x004b;
    L_0x0030:
        r5 = 5;
        if (r1 != r5) goto L_0x003d;
    L_0x0033:
        r1 = NUM; // 0x7f0d0a4b float:1.874746E38 double:1.0531310794E-314;
        r5 = "Updating";
        r1 = org.telegram.messenger.LocaleController.getString(r5, r1);
        goto L_0x004b;
    L_0x003d:
        r5 = 4;
        if (r1 != r5) goto L_0x004a;
    L_0x0040:
        r1 = NUM; // 0x7f0d02f5 float:1.874365E38 double:1.0531301516E-314;
        r5 = "ConnectingToProxy";
        r1 = org.telegram.messenger.LocaleController.getString(r5, r1);
        goto L_0x004b;
    L_0x004a:
        r1 = 0;
    L_0x004b:
        r5 = r0.user_id;
        r6 = "50_50";
        r7 = 0;
        if (r5 == 0) goto L_0x024c;
    L_0x0052:
        r5 = r0.currentAccount;
        r5 = org.telegram.messenger.MessagesController.getInstance(r5);
        r8 = r0.user_id;
        r8 = java.lang.Integer.valueOf(r8);
        r5 = r5.getUser(r8);
        r8 = r5.photo;
        if (r8 == 0) goto L_0x0069;
    L_0x0066:
        r8 = r8.photo_big;
        goto L_0x006a;
    L_0x0069:
        r8 = 0;
    L_0x006a:
        r9 = r0.avatarDrawable;
        r9.setInfo(r5);
        r9 = r0.avatarImage;
        r10 = org.telegram.messenger.ImageLocation.getForUser(r5, r7);
        r11 = r0.avatarDrawable;
        r9.setImage(r10, r6, r11, r5);
        r6 = r0.currentAccount;
        r9 = org.telegram.messenger.FileLoader.getInstance(r6);
        r10 = org.telegram.messenger.ImageLocation.getForUser(r5, r4);
        r12 = 0;
        r13 = 0;
        r14 = 1;
        r11 = r5;
        r9.loadFile(r10, r11, r12, r13, r14);
        r6 = org.telegram.messenger.UserObject.getUserName(r5);
        r9 = r5.id;
        r10 = r0.currentAccount;
        r10 = org.telegram.messenger.UserConfig.getInstance(r10);
        r10 = r10.getClientUserId();
        if (r9 != r10) goto L_0x00b5;
    L_0x009d:
        r6 = NUM; // 0x7f0d0296 float:1.8743457E38 double:1.0531301046E-314;
        r9 = "ChatYourSelf";
        r6 = org.telegram.messenger.LocaleController.getString(r9, r6);
        r9 = NUM; // 0x7f0d029b float:1.8743468E38 double:1.053130107E-314;
        r10 = "ChatYourSelfName";
        r9 = org.telegram.messenger.LocaleController.getString(r10, r9);
        r19 = r9;
        r9 = r6;
        r6 = r19;
        goto L_0x011c;
    L_0x00b5:
        r9 = r5.id;
        r10 = 333000; // 0x514c8 float:4.66632E-40 double:1.64524E-318;
        if (r9 == r10) goto L_0x0113;
    L_0x00bc:
        r10 = 777000; // 0xbdb28 float:1.088809E-39 double:3.83889E-318;
        if (r9 == r10) goto L_0x0113;
    L_0x00c1:
        r10 = 42777; // 0xa719 float:5.9943E-41 double:2.11346E-319;
        if (r9 != r10) goto L_0x00c7;
    L_0x00c6:
        goto L_0x0113;
    L_0x00c7:
        r9 = org.telegram.messenger.MessagesController.isSupportUser(r5);
        if (r9 == 0) goto L_0x00d7;
    L_0x00cd:
        r9 = NUM; // 0x7f0d09d6 float:1.8747222E38 double:1.0531310216E-314;
        r10 = "SupportStatus";
        r9 = org.telegram.messenger.LocaleController.getString(r10, r9);
        goto L_0x011c;
    L_0x00d7:
        r9 = r0.isBot;
        if (r9 == 0) goto L_0x00e5;
    L_0x00db:
        r9 = NUM; // 0x7f0d01cb float:1.8743046E38 double:1.0531300043E-314;
        r10 = "Bot";
        r9 = org.telegram.messenger.LocaleController.getString(r10, r9);
        goto L_0x011c;
    L_0x00e5:
        r9 = r0.isOnline;
        r9[r7] = r7;
        r10 = r0.currentAccount;
        r9 = org.telegram.messenger.LocaleController.formatUserStatus(r10, r5, r9);
        r10 = r0.onlineTextView;
        r10 = r10[r4];
        if (r10 == 0) goto L_0x011c;
    L_0x00f5:
        r10 = r0.isOnline;
        r10 = r10[r7];
        if (r10 == 0) goto L_0x00fe;
    L_0x00fb:
        r10 = "profile_status";
        goto L_0x0100;
    L_0x00fe:
        r10 = "avatar_subtitleInProfileBlue";
    L_0x0100:
        r11 = r0.onlineTextView;
        r11 = r11[r4];
        r11.setTag(r10);
        r11 = r0.onlineTextView;
        r11 = r11[r4];
        r10 = org.telegram.ui.ActionBar.Theme.getColor(r10);
        r11.setTextColor(r10);
        goto L_0x011c;
    L_0x0113:
        r9 = NUM; // 0x7f0d0957 float:1.8746964E38 double:1.053130959E-314;
        r10 = "ServiceNotifications";
        r9 = org.telegram.messenger.LocaleController.getString(r10, r9);
    L_0x011c:
        r10 = 0;
    L_0x011d:
        if (r10 >= r2) goto L_0x023c;
    L_0x011f:
        r11 = r0.nameTextView;
        r11 = r11[r10];
        if (r11 != 0) goto L_0x0127;
    L_0x0125:
        goto L_0x0238;
    L_0x0127:
        if (r10 != 0) goto L_0x01ae;
    L_0x0129:
        r11 = r5.id;
        r12 = r0.currentAccount;
        r12 = org.telegram.messenger.UserConfig.getInstance(r12);
        r12 = r12.getClientUserId();
        if (r11 == r12) goto L_0x01ae;
    L_0x0137:
        r11 = r5.id;
        r12 = r11 / 1000;
        r13 = 777; // 0x309 float:1.089E-42 double:3.84E-321;
        if (r12 == r13) goto L_0x01ae;
    L_0x013f:
        r11 = r11 / 1000;
        r12 = 333; // 0x14d float:4.67E-43 double:1.645E-321;
        if (r11 == r12) goto L_0x01ae;
    L_0x0145:
        r11 = r5.phone;
        if (r11 == 0) goto L_0x01ae;
    L_0x0149:
        r11 = r11.length();
        if (r11 == 0) goto L_0x01ae;
    L_0x014f:
        r11 = r0.currentAccount;
        r11 = org.telegram.messenger.ContactsController.getInstance(r11);
        r11 = r11.contactsDict;
        r12 = r5.id;
        r12 = java.lang.Integer.valueOf(r12);
        r11 = r11.get(r12);
        if (r11 != 0) goto L_0x01ae;
    L_0x0163:
        r11 = r0.currentAccount;
        r11 = org.telegram.messenger.ContactsController.getInstance(r11);
        r11 = r11.contactsDict;
        r11 = r11.size();
        if (r11 != 0) goto L_0x017d;
    L_0x0171:
        r11 = r0.currentAccount;
        r11 = org.telegram.messenger.ContactsController.getInstance(r11);
        r11 = r11.isLoadingContacts();
        if (r11 != 0) goto L_0x01ae;
    L_0x017d:
        r11 = org.telegram.PhoneFormat.PhoneFormat.getInstance();
        r12 = new java.lang.StringBuilder;
        r12.<init>();
        r13 = "+";
        r12.append(r13);
        r13 = r5.phone;
        r12.append(r13);
        r12 = r12.toString();
        r11 = r11.format(r12);
        r12 = r0.nameTextView;
        r12 = r12[r10];
        r12 = r12.getText();
        r12 = r12.equals(r11);
        if (r12 != 0) goto L_0x01c3;
    L_0x01a6:
        r12 = r0.nameTextView;
        r12 = r12[r10];
        r12.setText(r11);
        goto L_0x01c3;
    L_0x01ae:
        r11 = r0.nameTextView;
        r11 = r11[r10];
        r11 = r11.getText();
        r11 = r11.equals(r6);
        if (r11 != 0) goto L_0x01c3;
    L_0x01bc:
        r11 = r0.nameTextView;
        r11 = r11[r10];
        r11.setText(r6);
    L_0x01c3:
        if (r10 != 0) goto L_0x01cf;
    L_0x01c5:
        if (r1 == 0) goto L_0x01cf;
    L_0x01c7:
        r11 = r0.onlineTextView;
        r11 = r11[r10];
        r11.setText(r1);
        goto L_0x01e4;
    L_0x01cf:
        r11 = r0.onlineTextView;
        r11 = r11[r10];
        r11 = r11.getText();
        r11 = r11.equals(r9);
        if (r11 != 0) goto L_0x01e4;
    L_0x01dd:
        r11 = r0.onlineTextView;
        r11 = r11[r10];
        r11.setText(r9);
    L_0x01e4:
        r11 = r0.currentEncryptedChat;
        if (r11 == 0) goto L_0x01eb;
    L_0x01e8:
        r11 = org.telegram.ui.ActionBar.Theme.chat_lockIconDrawable;
        goto L_0x01ec;
    L_0x01eb:
        r11 = 0;
    L_0x01ec:
        if (r10 != 0) goto L_0x0212;
    L_0x01ee:
        r12 = r5.scam;
        if (r12 == 0) goto L_0x01f7;
    L_0x01f2:
        r12 = r20.getScamDrawable();
        goto L_0x022a;
    L_0x01f7:
        r12 = r0.currentAccount;
        r12 = org.telegram.messenger.MessagesController.getInstance(r12);
        r13 = r0.dialog_id;
        r15 = 0;
        r17 = (r13 > r15 ? 1 : (r13 == r15 ? 0 : -1));
        if (r17 == 0) goto L_0x0206;
    L_0x0205:
        goto L_0x0209;
    L_0x0206:
        r13 = r0.user_id;
        r13 = (long) r13;
    L_0x0209:
        r12 = r12.isDialogMuted(r13);
        if (r12 == 0) goto L_0x0229;
    L_0x020f:
        r12 = org.telegram.ui.ActionBar.Theme.chat_muteIconDrawable;
        goto L_0x022a;
    L_0x0212:
        r12 = r5.scam;
        if (r12 == 0) goto L_0x021b;
    L_0x0216:
        r12 = r20.getScamDrawable();
        goto L_0x022a;
    L_0x021b:
        r12 = r5.verified;
        if (r12 == 0) goto L_0x0229;
    L_0x021f:
        r12 = new org.telegram.ui.Components.CombinedDrawable;
        r13 = org.telegram.ui.ActionBar.Theme.profile_verifiedDrawable;
        r14 = org.telegram.ui.ActionBar.Theme.profile_verifiedCheckDrawable;
        r12.<init>(r13, r14);
        goto L_0x022a;
    L_0x0229:
        r12 = 0;
    L_0x022a:
        r13 = r0.nameTextView;
        r13 = r13[r10];
        r13.setLeftDrawable(r11);
        r11 = r0.nameTextView;
        r11 = r11[r10];
        r11.setRightDrawable(r12);
    L_0x0238:
        r10 = r10 + 1;
        goto L_0x011d;
    L_0x023c:
        r1 = r0.avatarImage;
        r1 = r1.getImageReceiver();
        r2 = org.telegram.ui.PhotoViewer.isShowingImage(r8);
        r2 = r2 ^ r4;
        r1.setVisible(r2, r7);
        goto L_0x0575;
    L_0x024c:
        r5 = r0.chat_id;
        if (r5 == 0) goto L_0x0575;
    L_0x0250:
        r5 = r0.currentAccount;
        r5 = org.telegram.messenger.MessagesController.getInstance(r5);
        r8 = r0.chat_id;
        r8 = java.lang.Integer.valueOf(r8);
        r5 = r5.getChat(r8);
        if (r5 == 0) goto L_0x0265;
    L_0x0262:
        r0.currentChat = r5;
        goto L_0x0267;
    L_0x0265:
        r5 = r0.currentChat;
    L_0x0267:
        r10 = r5;
        r5 = org.telegram.messenger.ChatObject.isChannel(r10);
        r8 = "MegaPublic";
        r11 = "MegaPrivate";
        r13 = "MegaLocation";
        r14 = "Subscribers";
        r15 = "OnlineCount";
        r3 = "%s, %s";
        r9 = "Members";
        if (r5 == 0) goto L_0x035d;
    L_0x027c:
        r5 = r0.chatInfo;
        if (r5 == 0) goto L_0x0327;
    L_0x0280:
        r12 = r0.currentChat;
        r7 = r12.megagroup;
        if (r7 != 0) goto L_0x0298;
    L_0x0286:
        r5 = r5.participants_count;
        if (r5 == 0) goto L_0x0327;
    L_0x028a:
        r5 = org.telegram.messenger.ChatObject.hasAdminRights(r12);
        if (r5 != 0) goto L_0x0327;
    L_0x0290:
        r5 = r0.chatInfo;
        r5 = r5.can_view_participants;
        if (r5 == 0) goto L_0x0298;
    L_0x0296:
        goto L_0x0327;
    L_0x0298:
        r5 = r0.currentChat;
        r5 = r5.megagroup;
        if (r5 == 0) goto L_0x0306;
    L_0x029e:
        r5 = r0.onlineCount;
        if (r5 <= r4) goto L_0x02c7;
    L_0x02a2:
        r5 = r0.chatInfo;
        r5 = r5.participants_count;
        if (r5 == 0) goto L_0x02c7;
    L_0x02a8:
        r7 = new java.lang.Object[r2];
        r5 = org.telegram.messenger.LocaleController.formatPluralString(r9, r5);
        r12 = 0;
        r7[r12] = r5;
        r5 = r0.onlineCount;
        r12 = r0.chatInfo;
        r12 = r12.participants_count;
        r5 = java.lang.Math.min(r5, r12);
        r5 = org.telegram.messenger.LocaleController.formatPluralString(r15, r5);
        r7[r4] = r5;
        r3 = java.lang.String.format(r3, r7);
        goto L_0x038b;
    L_0x02c7:
        r3 = r0.chatInfo;
        r3 = r3.participants_count;
        if (r3 != 0) goto L_0x0300;
    L_0x02cd:
        r3 = r10.has_geo;
        if (r3 == 0) goto L_0x02de;
    L_0x02d1:
        r3 = NUM; // 0x7f0d05bb float:1.874509E38 double:1.0531305023E-314;
        r5 = org.telegram.messenger.LocaleController.getString(r13, r3);
        r3 = r5.toLowerCase();
        goto L_0x038b;
    L_0x02de:
        r3 = r10.username;
        r3 = android.text.TextUtils.isEmpty(r3);
        if (r3 != 0) goto L_0x02f3;
    L_0x02e6:
        r3 = NUM; // 0x7f0d05bf float:1.8745098E38 double:1.0531305043E-314;
        r3 = org.telegram.messenger.LocaleController.getString(r8, r3);
        r3 = r3.toLowerCase();
        goto L_0x038b;
    L_0x02f3:
        r3 = NUM; // 0x7f0d05bc float:1.8745092E38 double:1.053130503E-314;
        r5 = org.telegram.messenger.LocaleController.getString(r11, r3);
        r3 = r5.toLowerCase();
        goto L_0x038b;
    L_0x0300:
        r3 = org.telegram.messenger.LocaleController.formatPluralString(r9, r3);
        goto L_0x038b;
    L_0x0306:
        r3 = new int[r4];
        r5 = r0.chatInfo;
        r5 = r5.participants_count;
        org.telegram.messenger.LocaleController.formatShortNumber(r5, r3);
        r3 = r0.currentChat;
        r3 = r3.megagroup;
        if (r3 == 0) goto L_0x031e;
    L_0x0315:
        r3 = r0.chatInfo;
        r3 = r3.participants_count;
        r3 = org.telegram.messenger.LocaleController.formatPluralString(r9, r3);
        goto L_0x038b;
    L_0x031e:
        r3 = r0.chatInfo;
        r3 = r3.participants_count;
        r3 = org.telegram.messenger.LocaleController.formatPluralString(r14, r3);
        goto L_0x038b;
    L_0x0327:
        r3 = r0.currentChat;
        r3 = r3.megagroup;
        if (r3 == 0) goto L_0x033b;
    L_0x032d:
        r3 = NUM; // 0x7f0d057b float:1.874496E38 double:1.0531304707E-314;
        r5 = "Loading";
        r3 = org.telegram.messenger.LocaleController.getString(r5, r3);
        r3 = r3.toLowerCase();
        goto L_0x038b;
    L_0x033b:
        r3 = r10.flags;
        r3 = r3 & 64;
        if (r3 == 0) goto L_0x034f;
    L_0x0341:
        r3 = NUM; // 0x7f0d025c float:1.874334E38 double:1.053130076E-314;
        r5 = "ChannelPublic";
        r3 = org.telegram.messenger.LocaleController.getString(r5, r3);
        r3 = r3.toLowerCase();
        goto L_0x038b;
    L_0x034f:
        r3 = NUM; // 0x7f0d0259 float:1.8743334E38 double:1.0531300745E-314;
        r5 = "ChannelPrivate";
        r3 = org.telegram.messenger.LocaleController.getString(r5, r3);
        r3 = r3.toLowerCase();
        goto L_0x038b;
    L_0x035d:
        r5 = r10.participants_count;
        r7 = r0.chatInfo;
        if (r7 == 0) goto L_0x036b;
    L_0x0363:
        r5 = r7.participants;
        r5 = r5.participants;
        r5 = r5.size();
    L_0x036b:
        if (r5 == 0) goto L_0x0387;
    L_0x036d:
        r7 = r0.onlineCount;
        if (r7 <= r4) goto L_0x0387;
    L_0x0371:
        r7 = new java.lang.Object[r2];
        r5 = org.telegram.messenger.LocaleController.formatPluralString(r9, r5);
        r12 = 0;
        r7[r12] = r5;
        r5 = r0.onlineCount;
        r5 = org.telegram.messenger.LocaleController.formatPluralString(r15, r5);
        r7[r4] = r5;
        r3 = java.lang.String.format(r3, r7);
        goto L_0x038b;
    L_0x0387:
        r3 = org.telegram.messenger.LocaleController.formatPluralString(r9, r5);
    L_0x038b:
        r7 = r3;
        r3 = 0;
        r5 = 0;
    L_0x038e:
        if (r3 >= r2) goto L_0x0532;
    L_0x0390:
        r12 = r0.nameTextView;
        r15 = r12[r3];
        if (r15 != 0) goto L_0x039a;
    L_0x0396:
        r16 = r6;
        goto L_0x052a;
    L_0x039a:
        r15 = r10.title;
        if (r15 == 0) goto L_0x03b9;
    L_0x039e:
        r12 = r12[r3];
        r12 = r12.getText();
        r15 = r10.title;
        r12 = r12.equals(r15);
        if (r12 != 0) goto L_0x03b9;
    L_0x03ac:
        r12 = r0.nameTextView;
        r12 = r12[r3];
        r15 = r10.title;
        r12 = r12.setText(r15);
        if (r12 == 0) goto L_0x03b9;
    L_0x03b8:
        r5 = 1;
    L_0x03b9:
        r12 = r0.nameTextView;
        r12 = r12[r3];
        r15 = 0;
        r12.setLeftDrawable(r15);
        if (r3 == 0) goto L_0x03f6;
    L_0x03c3:
        r12 = r10.scam;
        if (r12 == 0) goto L_0x03d8;
    L_0x03c7:
        r12 = r0.nameTextView;
        r12 = r12[r3];
        r15 = r20.getScamDrawable();
        r12.setRightDrawable(r15);
    L_0x03d2:
        r18 = r5;
        r16 = r6;
        r15 = 0;
        goto L_0x042a;
    L_0x03d8:
        r12 = r10.verified;
        if (r12 == 0) goto L_0x03ed;
    L_0x03dc:
        r12 = r0.nameTextView;
        r12 = r12[r3];
        r15 = new org.telegram.ui.Components.CombinedDrawable;
        r2 = org.telegram.ui.ActionBar.Theme.profile_verifiedDrawable;
        r4 = org.telegram.ui.ActionBar.Theme.profile_verifiedCheckDrawable;
        r15.<init>(r2, r4);
        r12.setRightDrawable(r15);
        goto L_0x03d2;
    L_0x03ed:
        r2 = r0.nameTextView;
        r2 = r2[r3];
        r15 = 0;
        r2.setRightDrawable(r15);
        goto L_0x0406;
    L_0x03f6:
        r15 = 0;
        r2 = r10.scam;
        if (r2 == 0) goto L_0x040b;
    L_0x03fb:
        r2 = r0.nameTextView;
        r2 = r2[r3];
        r4 = r20.getScamDrawable();
        r2.setRightDrawable(r4);
    L_0x0406:
        r18 = r5;
        r16 = r6;
        goto L_0x042a;
    L_0x040b:
        r2 = r0.nameTextView;
        r2 = r2[r3];
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.MessagesController.getInstance(r4);
        r12 = r0.chat_id;
        r12 = -r12;
        r18 = r5;
        r16 = r6;
        r5 = (long) r12;
        r4 = r4.isDialogMuted(r5);
        if (r4 == 0) goto L_0x0426;
    L_0x0423:
        r4 = org.telegram.ui.ActionBar.Theme.chat_muteIconDrawable;
        goto L_0x0427;
    L_0x0426:
        r4 = r15;
    L_0x0427:
        r2.setRightDrawable(r4);
    L_0x042a:
        if (r3 != 0) goto L_0x0437;
    L_0x042c:
        if (r1 == 0) goto L_0x0437;
    L_0x042e:
        r2 = r0.onlineTextView;
        r2 = r2[r3];
        r2.setText(r1);
        goto L_0x0528;
    L_0x0437:
        r2 = r0.currentChat;
        r2 = r2.megagroup;
        if (r2 == 0) goto L_0x045c;
    L_0x043d:
        r2 = r0.chatInfo;
        if (r2 == 0) goto L_0x045c;
    L_0x0441:
        r2 = r0.onlineCount;
        if (r2 <= 0) goto L_0x045c;
    L_0x0445:
        r2 = r0.onlineTextView;
        r2 = r2[r3];
        r2 = r2.getText();
        r2 = r2.equals(r7);
        if (r2 != 0) goto L_0x0528;
    L_0x0453:
        r2 = r0.onlineTextView;
        r2 = r2[r3];
        r2.setText(r7);
        goto L_0x0528;
    L_0x045c:
        if (r3 != 0) goto L_0x0513;
    L_0x045e:
        r2 = r0.currentChat;
        r2 = org.telegram.messenger.ChatObject.isChannel(r2);
        if (r2 == 0) goto L_0x0513;
    L_0x0466:
        r2 = r0.chatInfo;
        if (r2 == 0) goto L_0x0513;
    L_0x046a:
        r2 = r2.participants_count;
        if (r2 == 0) goto L_0x0513;
    L_0x046e:
        r2 = r0.currentChat;
        r4 = r2.megagroup;
        if (r4 != 0) goto L_0x0478;
    L_0x0474:
        r2 = r2.broadcast;
        if (r2 == 0) goto L_0x0513;
    L_0x0478:
        r2 = 1;
        r4 = new int[r2];
        r2 = r0.chatInfo;
        r2 = r2.participants_count;
        r2 = org.telegram.messenger.LocaleController.formatShortNumber(r2, r4);
        r5 = r0.currentChat;
        r5 = r5.megagroup;
        if (r5 == 0) goto L_0x04ee;
    L_0x0489:
        r5 = r0.chatInfo;
        r5 = r5.participants_count;
        if (r5 != 0) goto L_0x04c3;
    L_0x048f:
        r2 = r10.has_geo;
        if (r2 == 0) goto L_0x04a0;
    L_0x0493:
        r6 = NUM; // 0x7f0d05bb float:1.874509E38 double:1.0531305023E-314;
        r2 = org.telegram.messenger.LocaleController.getString(r13, r6);
        r7 = r2.toLowerCase();
        goto L_0x0528;
    L_0x04a0:
        r6 = NUM; // 0x7f0d05bb float:1.874509E38 double:1.0531305023E-314;
        r2 = r10.username;
        r2 = android.text.TextUtils.isEmpty(r2);
        if (r2 != 0) goto L_0x04b7;
    L_0x04ab:
        r2 = NUM; // 0x7f0d05bf float:1.8745098E38 double:1.0531305043E-314;
        r2 = org.telegram.messenger.LocaleController.getString(r8, r2);
        r7 = r2.toLowerCase();
        goto L_0x0528;
    L_0x04b7:
        r12 = NUM; // 0x7f0d05bc float:1.8745092E38 double:1.053130503E-314;
        r2 = org.telegram.messenger.LocaleController.getString(r11, r12);
        r7 = r2.toLowerCase();
        goto L_0x0528;
    L_0x04c3:
        r6 = NUM; // 0x7f0d05bb float:1.874509E38 double:1.0531305023E-314;
        r12 = NUM; // 0x7f0d05bc float:1.8745092E38 double:1.053130503E-314;
        r5 = r0.onlineTextView;
        r5 = r5[r3];
        r17 = 0;
        r6 = r4[r17];
        r6 = org.telegram.messenger.LocaleController.formatPluralString(r9, r6);
        r12 = 1;
        r15 = new java.lang.Object[r12];
        r4 = r4[r17];
        r4 = java.lang.Integer.valueOf(r4);
        r15[r17] = r4;
        r4 = "%d";
        r4 = java.lang.String.format(r4, r15);
        r2 = r6.replace(r4, r2);
        r5.setText(r2);
        goto L_0x0528;
    L_0x04ee:
        r17 = 0;
        r5 = r0.onlineTextView;
        r5 = r5[r3];
        r6 = r4[r17];
        r6 = org.telegram.messenger.LocaleController.formatPluralString(r14, r6);
        r12 = 1;
        r15 = new java.lang.Object[r12];
        r4 = r4[r17];
        r4 = java.lang.Integer.valueOf(r4);
        r15[r17] = r4;
        r4 = "%d";
        r4 = java.lang.String.format(r4, r15);
        r2 = r6.replace(r4, r2);
        r5.setText(r2);
        goto L_0x0528;
    L_0x0513:
        r2 = r0.onlineTextView;
        r2 = r2[r3];
        r2 = r2.getText();
        r2 = r2.equals(r7);
        if (r2 != 0) goto L_0x0528;
    L_0x0521:
        r2 = r0.onlineTextView;
        r2 = r2[r3];
        r2.setText(r7);
    L_0x0528:
        r5 = r18;
    L_0x052a:
        r3 = r3 + 1;
        r6 = r16;
        r2 = 2;
        r4 = 1;
        goto L_0x038e;
    L_0x0532:
        r16 = r6;
        if (r5 == 0) goto L_0x0539;
    L_0x0536:
        r20.needLayout();
    L_0x0539:
        r1 = r10.photo;
        if (r1 == 0) goto L_0x0540;
    L_0x053d:
        r3 = r1.photo_big;
        goto L_0x0541;
    L_0x0540:
        r3 = 0;
    L_0x0541:
        r1 = r0.avatarDrawable;
        r1.setInfo(r10);
        r1 = r0.avatarImage;
        r2 = 0;
        r4 = org.telegram.messenger.ImageLocation.getForChat(r10, r2);
        r2 = r0.avatarDrawable;
        r5 = r16;
        r1.setImage(r4, r5, r2, r10);
        r1 = r0.currentAccount;
        r8 = org.telegram.messenger.FileLoader.getInstance(r1);
        r1 = 1;
        r9 = org.telegram.messenger.ImageLocation.getForChat(r10, r1);
        r11 = 0;
        r12 = 0;
        r13 = 1;
        r8.loadFile(r9, r10, r11, r12, r13);
        r1 = r0.avatarImage;
        r1 = r1.getImageReceiver();
        r2 = org.telegram.ui.PhotoViewer.isShowingImage(r3);
        r3 = 1;
        r2 = r2 ^ r3;
        r3 = 0;
        r1.setVisible(r2, r3);
    L_0x0575:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ProfileActivity.updateProfileData():void");
    }

    /* JADX WARNING: Removed duplicated region for block: B:91:0x0239  */
    /* JADX WARNING: Removed duplicated region for block: B:94:0x0261  */
    /* JADX WARNING: Removed duplicated region for block: B:97:0x0271  */
    private void createActionBarMenu() {
        /*
        r16 = this;
        r0 = r16;
        r1 = r0.actionBar;
        r1 = r1.createMenu();
        r1.clearItems();
        r2 = 0;
        r0.animatingItem = r2;
        r3 = r0.user_id;
        r4 = NUM; // 0x7var_ff float:1.7945095E38 double:1.052935629E-314;
        r5 = 10;
        if (r3 == 0) goto L_0x016f;
    L_0x0017:
        r2 = r0.currentAccount;
        r2 = org.telegram.messenger.UserConfig.getInstance(r2);
        r2 = r2.getClientUserId();
        r3 = r0.user_id;
        r6 = NUM; // 0x7f0d096e float:1.874701E38 double:1.05313097E-314;
        r7 = "ShareContact";
        r8 = 3;
        r9 = NUM; // 0x7var_f7 float:1.7945598E38 double:1.0529357515E-314;
        if (r2 == r3) goto L_0x015e;
    L_0x002e:
        r2 = r0.currentAccount;
        r2 = org.telegram.messenger.MessagesController.getInstance(r2);
        r3 = r0.user_id;
        r3 = java.lang.Integer.valueOf(r3);
        r2 = r2.getUser(r3);
        if (r2 != 0) goto L_0x0041;
    L_0x0040:
        return;
    L_0x0041:
        r3 = r0.userInfo;
        if (r3 == 0) goto L_0x0054;
    L_0x0045:
        r3 = r3.phone_calls_available;
        if (r3 == 0) goto L_0x0054;
    L_0x0049:
        r3 = 15;
        r10 = NUM; // 0x7var_c float:1.7945122E38 double:1.0529356354E-314;
        r3 = r1.addItem(r3, r10);
        r0.callItem = r3;
    L_0x0054:
        r3 = r0.isBot;
        r10 = NUM; // 0x7f0d0a2c float:1.8747396E38 double:1.053131064E-314;
        r11 = "Unblock";
        r12 = NUM; // 0x7var_bf float:1.7945485E38 double:1.052935724E-314;
        r13 = 2;
        if (r3 != 0) goto L_0x00c2;
    L_0x0061:
        r3 = r0.currentAccount;
        r3 = org.telegram.messenger.ContactsController.getInstance(r3);
        r3 = r3.contactsDict;
        r14 = r0.user_id;
        r14 = java.lang.Integer.valueOf(r14);
        r3 = r3.get(r14);
        if (r3 != 0) goto L_0x0076;
    L_0x0075:
        goto L_0x00c2;
    L_0x0076:
        r3 = r1.addItem(r5, r4);
        r2 = r2.phone;
        r2 = android.text.TextUtils.isEmpty(r2);
        if (r2 != 0) goto L_0x0089;
    L_0x0082:
        r2 = org.telegram.messenger.LocaleController.getString(r7, r6);
        r3.addSubItem(r8, r9, r2);
    L_0x0089:
        r2 = r0.userBlocked;
        r2 = r0.userBlocked;
        if (r2 != 0) goto L_0x0099;
    L_0x008f:
        r2 = NUM; // 0x7f0d01b6 float:1.8743003E38 double:1.053129994E-314;
        r6 = "BlockContact";
        r2 = org.telegram.messenger.LocaleController.getString(r6, r2);
        goto L_0x009d;
    L_0x0099:
        r2 = org.telegram.messenger.LocaleController.getString(r11, r10);
    L_0x009d:
        r3.addSubItem(r13, r12, r2);
        r2 = 4;
        r6 = NUM; // 0x7var_ca float:1.7945507E38 double:1.0529357293E-314;
        r7 = NUM; // 0x7f0d03b9 float:1.8744048E38 double:1.0531302484E-314;
        r8 = "EditContact";
        r7 = org.telegram.messenger.LocaleController.getString(r8, r7);
        r3.addSubItem(r2, r6, r7);
        r2 = 5;
        r6 = NUM; // 0x7var_c8 float:1.7945503E38 double:1.0529357283E-314;
        r7 = NUM; // 0x7f0d035b float:1.8743857E38 double:1.053130202E-314;
        r8 = "DeleteContact";
        r7 = org.telegram.messenger.LocaleController.getString(r8, r7);
        r3.addSubItem(r2, r6, r7);
        goto L_0x016c;
    L_0x00c2:
        r3 = r1.addItem(r5, r4);
        r14 = org.telegram.messenger.MessagesController.isSupportUser(r2);
        if (r14 == 0) goto L_0x00d9;
    L_0x00cc:
        r2 = r0.userBlocked;
        if (r2 == 0) goto L_0x016c;
    L_0x00d0:
        r2 = org.telegram.messenger.LocaleController.getString(r11, r10);
        r3.addSubItem(r13, r12, r2);
        goto L_0x016c;
    L_0x00d9:
        r14 = r0.isBot;
        if (r14 == 0) goto L_0x00fe;
    L_0x00dd:
        r14 = r2.bot_nochats;
        if (r14 != 0) goto L_0x00f2;
    L_0x00e1:
        r14 = 9;
        r15 = NUM; // 0x7var_bc float:1.7945478E38 double:1.0529357224E-314;
        r4 = NUM; // 0x7f0d01cf float:1.8743054E38 double:1.0531300063E-314;
        r12 = "BotInvite";
        r4 = org.telegram.messenger.LocaleController.getString(r12, r4);
        r3.addSubItem(r14, r15, r4);
    L_0x00f2:
        r4 = NUM; // 0x7f0d01d3 float:1.8743062E38 double:1.0531300083E-314;
        r12 = "BotShare";
        r4 = org.telegram.messenger.LocaleController.getString(r12, r4);
        r3.addSubItem(r5, r9, r4);
    L_0x00fe:
        r4 = 1;
        r12 = NUM; // 0x7var_bd float:1.794548E38 double:1.052935723E-314;
        r14 = NUM; // 0x7f0d00a7 float:1.8742453E38 double:1.05312986E-314;
        r15 = "AddContact";
        r14 = org.telegram.messenger.LocaleController.getString(r15, r14);
        r3.addSubItem(r4, r12, r14);
        r2 = r2.phone;
        r2 = android.text.TextUtils.isEmpty(r2);
        if (r2 != 0) goto L_0x011d;
    L_0x0116:
        r2 = org.telegram.messenger.LocaleController.getString(r7, r6);
        r3.addSubItem(r8, r9, r2);
    L_0x011d:
        r2 = r0.isBot;
        if (r2 == 0) goto L_0x0143;
    L_0x0121:
        r2 = r0.userBlocked;
        if (r2 != 0) goto L_0x0129;
    L_0x0125:
        r12 = NUM; // 0x7var_bf float:1.7945485E38 double:1.052935724E-314;
        goto L_0x012c;
    L_0x0129:
        r12 = NUM; // 0x7var_ee float:1.794558E38 double:1.052935747E-314;
    L_0x012c:
        r2 = r0.userBlocked;
        if (r2 != 0) goto L_0x0136;
    L_0x0130:
        r2 = NUM; // 0x7f0d01d7 float:1.874307E38 double:1.0531300102E-314;
        r4 = "BotStop";
        goto L_0x013b;
    L_0x0136:
        r2 = NUM; // 0x7f0d01d1 float:1.8743058E38 double:1.0531300073E-314;
        r4 = "BotRestart";
    L_0x013b:
        r2 = org.telegram.messenger.LocaleController.getString(r4, r2);
        r3.addSubItem(r13, r12, r2);
        goto L_0x016c;
    L_0x0143:
        r2 = r0.userBlocked;
        r2 = r0.userBlocked;
        if (r2 != 0) goto L_0x0153;
    L_0x0149:
        r2 = NUM; // 0x7f0d01b6 float:1.8743003E38 double:1.053129994E-314;
        r4 = "BlockContact";
        r2 = org.telegram.messenger.LocaleController.getString(r4, r2);
        goto L_0x0157;
    L_0x0153:
        r2 = org.telegram.messenger.LocaleController.getString(r11, r10);
    L_0x0157:
        r4 = NUM; // 0x7var_bf float:1.7945485E38 double:1.052935724E-314;
        r3.addSubItem(r13, r4, r2);
        goto L_0x016c;
    L_0x015e:
        r2 = NUM; // 0x7var_ff float:1.7945095E38 double:1.052935629E-314;
        r3 = r1.addItem(r5, r2);
        r2 = org.telegram.messenger.LocaleController.getString(r7, r6);
        r3.addSubItem(r8, r9, r2);
    L_0x016c:
        r2 = r3;
        goto L_0x0237;
    L_0x016f:
        r3 = r0.chat_id;
        if (r3 == 0) goto L_0x0237;
    L_0x0173:
        if (r3 <= 0) goto L_0x0237;
    L_0x0175:
        r3 = r0.currentAccount;
        r3 = org.telegram.messenger.MessagesController.getInstance(r3);
        r4 = r0.chat_id;
        r4 = java.lang.Integer.valueOf(r4);
        r3 = r3.getChat(r4);
        r4 = org.telegram.messenger.ChatObject.isChannel(r3);
        r6 = NUM; // 0x7f0d0911 float:1.8746822E38 double:1.0531309243E-314;
        r7 = "SearchMembers";
        r8 = NUM; // 0x7var_f6 float:1.7945596E38 double:1.052935751E-314;
        r9 = 17;
        if (r4 == 0) goto L_0x0206;
    L_0x0195:
        r4 = org.telegram.messenger.ChatObject.hasAdminRights(r3);
        if (r4 != 0) goto L_0x01a5;
    L_0x019b:
        r4 = r3.megagroup;
        if (r4 == 0) goto L_0x01b0;
    L_0x019f:
        r4 = org.telegram.messenger.ChatObject.canChangeChatInfo(r3);
        if (r4 == 0) goto L_0x01b0;
    L_0x01a5:
        r4 = 12;
        r10 = NUM; // 0x7var_ef float:1.7945063E38 double:1.052935621E-314;
        r4 = r1.addItem(r4, r10);
        r0.editItem = r4;
    L_0x01b0:
        r4 = r3.megagroup;
        if (r4 != 0) goto L_0x01d5;
    L_0x01b4:
        r4 = r0.chatInfo;
        if (r4 == 0) goto L_0x01d5;
    L_0x01b8:
        r4 = r4.can_view_stats;
        if (r4 == 0) goto L_0x01d5;
    L_0x01bc:
        r2 = NUM; // 0x7var_ff float:1.7945095E38 double:1.052935629E-314;
        r4 = r1.addItem(r5, r2);
        r2 = 19;
        r10 = NUM; // 0x7var_f9 float:1.7945602E38 double:1.0529357525E-314;
        r11 = NUM; // 0x7f0d09ab float:1.8747135E38 double:1.0531310004E-314;
        r12 = "Statistics";
        r11 = org.telegram.messenger.LocaleController.getString(r12, r11);
        r4.addSubItem(r2, r10, r11);
        r2 = r4;
    L_0x01d5:
        r4 = r3.megagroup;
        if (r4 == 0) goto L_0x0237;
    L_0x01d9:
        if (r2 != 0) goto L_0x01e2;
    L_0x01db:
        r4 = NUM; // 0x7var_ff float:1.7945095E38 double:1.052935629E-314;
        r2 = r1.addItem(r5, r4);
    L_0x01e2:
        r4 = org.telegram.messenger.LocaleController.getString(r7, r6);
        r2.addSubItem(r9, r8, r4);
        r4 = r3.creator;
        if (r4 != 0) goto L_0x0237;
    L_0x01ed:
        r4 = r3.left;
        if (r4 != 0) goto L_0x0237;
    L_0x01f1:
        r3 = r3.kicked;
        if (r3 != 0) goto L_0x0237;
    L_0x01f5:
        r3 = 7;
        r4 = NUM; // 0x7var_d8 float:1.7945535E38 double:1.052935736E-314;
        r6 = NUM; // 0x7f0d0565 float:1.8744916E38 double:1.05313046E-314;
        r7 = "LeaveMegaMenu";
        r6 = org.telegram.messenger.LocaleController.getString(r7, r6);
        r2.addSubItem(r3, r4, r6);
        goto L_0x0237;
    L_0x0206:
        r2 = org.telegram.messenger.ChatObject.canChangeChatInfo(r3);
        if (r2 == 0) goto L_0x0217;
    L_0x020c:
        r2 = 12;
        r3 = NUM; // 0x7var_ef float:1.7945063E38 double:1.052935621E-314;
        r2 = r1.addItem(r2, r3);
        r0.editItem = r2;
    L_0x0217:
        r2 = NUM; // 0x7var_ff float:1.7945095E38 double:1.052935629E-314;
        r3 = r1.addItem(r5, r2);
        r2 = org.telegram.messenger.LocaleController.getString(r7, r6);
        r3.addSubItem(r9, r8, r2);
        r2 = 7;
        r4 = NUM; // 0x7var_d8 float:1.7945535E38 double:1.052935736E-314;
        r6 = NUM; // 0x7f0d0354 float:1.8743843E38 double:1.0531301985E-314;
        r7 = "DeleteAndExit";
        r6 = org.telegram.messenger.LocaleController.getString(r7, r6);
        r3.addSubItem(r2, r4, r6);
        goto L_0x016c;
    L_0x0237:
        if (r2 != 0) goto L_0x0240;
    L_0x0239:
        r3 = NUM; // 0x7var_ff float:1.7945095E38 double:1.052935629E-314;
        r2 = r1.addItem(r5, r3);
    L_0x0240:
        r1 = 14;
        r3 = NUM; // 0x7var_d2 float:1.7945523E38 double:1.0529357333E-314;
        r4 = NUM; // 0x7f0d00ba float:1.8742492E38 double:1.0531298694E-314;
        r5 = "AddShortcut";
        r4 = org.telegram.messenger.LocaleController.getString(r5, r4);
        r2.addSubItem(r1, r3, r4);
        r1 = NUM; // 0x7f0d002b float:1.8742202E38 double:1.053129799E-314;
        r3 = "AccDescrMoreOptions";
        r1 = org.telegram.messenger.LocaleController.getString(r3, r1);
        r2.setContentDescription(r1);
        r1 = r0.editItem;
        if (r1 == 0) goto L_0x026d;
    L_0x0261:
        r2 = NUM; // 0x7f0d0397 float:1.8743979E38 double:1.0531302316E-314;
        r3 = "Edit";
        r2 = org.telegram.messenger.LocaleController.getString(r3, r2);
        r1.setContentDescription(r2);
    L_0x026d:
        r1 = r0.callItem;
        if (r1 == 0) goto L_0x027d;
    L_0x0271:
        r2 = NUM; // 0x7f0d01e0 float:1.8743088E38 double:1.0531300147E-314;
        r3 = "Call";
        r2 = org.telegram.messenger.LocaleController.getString(r3, r2);
        r1.setContentDescription(r2);
    L_0x027d:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ProfileActivity.createActionBarMenu():void");
    }

    /* Access modifiers changed, original: protected */
    public void onDialogDismiss(android.app.Dialog dialog) {
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            recyclerListView.invalidateViews();
        }
    }

    public void didSelectDialogs(DialogsActivity dialogsActivity, ArrayList<Long> arrayList, CharSequence charSequence, boolean z) {
        long longValue = ((Long) arrayList.get(0)).longValue();
        Bundle bundle = new Bundle();
        bundle.putBoolean("scrollToTopOnResume", true);
        int i = (int) longValue;
        if (i == 0) {
            bundle.putInt("enc_id", (int) (longValue >> 32));
        } else if (i > 0) {
            bundle.putInt("user_id", i);
        } else if (i < 0) {
            bundle.putInt("chat_id", -i);
        }
        if (MessagesController.getInstance(this.currentAccount).checkCanOpenChat(bundle, dialogsActivity)) {
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.closeChats);
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
            presentFragment(new ChatActivity(bundle), true);
            removeSelfFromStack();
            SendMessagesHelper.getInstance(this.currentAccount).sendMessage(MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.user_id)), longValue, null, null, null);
        }
    }

    public void onRequestPermissionsResultFragment(int i, String[] strArr, int[] iArr) {
        if (i == 101) {
            User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.user_id));
            if (user != null) {
                if (iArr.length <= 0 || iArr[0] != 0) {
                    VoIPHelper.permissionDenied(getParentActivity(), null);
                } else {
                    VoIPHelper.startCall(user, getParentActivity(), this.userInfo);
                }
            }
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        -$$Lambda$ProfileActivity$UnQ2igF3-HzP0AhTDZ-WibwhM_I -__lambda_profileactivity_unq2igf3-hzp0ahtdz-wibwhm_i = new -$$Lambda$ProfileActivity$UnQ2igF3-HzP0AhTDZ-WibwhM_I(this);
        r11 = new ThemeDescription[65];
        r11[4] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM | ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, "actionBarDefaultSubmenuItemIcon");
        r11[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "avatar_backgroundActionBarBlue");
        r11[6] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "avatar_backgroundActionBarBlue");
        r11[7] = new ThemeDescription(this.topView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "avatar_backgroundActionBarBlue");
        r11[8] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "avatar_actionBarSelectorBlue");
        r11[9] = new ThemeDescription(this.nameTextView[1], ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "profile_title");
        r11[10] = new ThemeDescription(this.nameTextView[1], ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "avatar_backgroundActionBarBlue");
        r11[11] = new ThemeDescription(this.onlineTextView[1], ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, null, null, null, null, "profile_status");
        r11[12] = new ThemeDescription(this.onlineTextView[1], ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, null, null, null, null, "avatar_subtitleInProfileBlue");
        r11[13] = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21");
        r11[14] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, "divider");
        r11[15] = new ThemeDescription(this.avatarImage, 0, null, null, new Drawable[]{Theme.avatar_broadcastDrawable, Theme.avatar_savedDrawable}, null, "avatar_text");
        r11[16] = new ThemeDescription(this.avatarImage, 0, null, null, new Drawable[]{this.avatarDrawable}, null, "avatar_backgroundInProfileBlue");
        r11[17] = new ThemeDescription(this.writeButton, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, "profile_actionIcon");
        r11[18] = new ThemeDescription(this.writeButton, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "profile_actionBackground");
        r11[19] = new ThemeDescription(this.writeButton, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, "profile_actionPressedBackground");
        View view = this.listView;
        int i = ThemeDescription.FLAG_CHECKTAG;
        Class[] clsArr = new Class[]{TextCell.class};
        String[] strArr = new String[1];
        strArr[0] = "textView";
        r11[20] = new ThemeDescription(view, i, clsArr, strArr, null, null, null, "windowBackgroundWhiteBlackText");
        view = this.listView;
        View view2 = view;
        r11[21] = new ThemeDescription(view2, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteGreenText2");
        view = this.listView;
        view2 = view;
        r11[22] = new ThemeDescription(view2, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteRedText5");
        view = this.listView;
        view2 = view;
        r11[23] = new ThemeDescription(view2, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlueText2");
        view = this.listView;
        view2 = view;
        r11[24] = new ThemeDescription(view2, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlueButton");
        view = this.listView;
        clsArr = new Class[]{TextCell.class};
        strArr = new String[1];
        strArr[0] = "valueTextView";
        r11[25] = new ThemeDescription(view, 0, clsArr, strArr, null, null, null, "windowBackgroundWhiteValueText");
        view = this.listView;
        i = ThemeDescription.FLAG_CHECKTAG;
        clsArr = new Class[]{TextCell.class};
        strArr = new String[1];
        strArr[0] = "imageView";
        r11[26] = new ThemeDescription(view, i, clsArr, strArr, null, null, null, "windowBackgroundWhiteGrayIcon");
        view = this.listView;
        view2 = view;
        r11[27] = new ThemeDescription(view2, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"imageView"}, null, null, null, "windowBackgroundWhiteBlueIcon");
        r11[28] = new ThemeDescription(this.listView, 0, new Class[]{TextDetailCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText");
        r11[29] = new ThemeDescription(this.listView, 0, new Class[]{TextDetailCell.class}, new String[]{"valueTextView"}, null, null, null, "windowBackgroundWhiteGrayText2");
        r11[30] = new ThemeDescription(this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlueHeader");
        r11[31] = new ThemeDescription(this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText");
        r11[32] = new ThemeDescription(this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"valueTextView"}, null, null, null, "windowBackgroundWhiteGrayText2");
        view = this.listView;
        clsArr = new Class[]{NotificationsCheckCell.class};
        strArr = new String[1];
        strArr[0] = "checkBox";
        r11[33] = new ThemeDescription(view, 0, clsArr, strArr, null, null, null, "switchTrack");
        r11[34] = new ThemeDescription(this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"checkBox"}, null, null, null, "switchTrackChecked");
        view = this.listView;
        view2 = view;
        r11[35] = new ThemeDescription(view2, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{UserCell.class}, new String[]{"adminTextView"}, null, null, null, "profile_creatorIcon");
        r11[36] = new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, new String[]{"imageView"}, null, null, null, "windowBackgroundWhiteGrayIcon");
        r11[37] = new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, new String[]{"nameTextView"}, null, null, null, "windowBackgroundWhiteBlackText");
        ThemeDescriptionDelegate themeDescriptionDelegate = -__lambda_profileactivity_unq2igf3-hzp0ahtdz-wibwhm_i;
        r11[38] = new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, new String[]{"statusColor"}, null, null, themeDescriptionDelegate, "windowBackgroundWhiteGrayText");
        r11[39] = new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, new String[]{"statusOnlineColor"}, null, null, themeDescriptionDelegate, "windowBackgroundWhiteBlueText");
        r11[40] = new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, null, new Drawable[]{Theme.avatar_broadcastDrawable, Theme.avatar_savedDrawable}, null, "avatar_text");
        -$$Lambda$ProfileActivity$UnQ2igF3-HzP0AhTDZ-WibwhM_I -__lambda_profileactivity_unq2igf3-hzp0ahtdz-wibwhm_i2 = -__lambda_profileactivity_unq2igf3-hzp0ahtdz-wibwhm_i;
        r11[41] = new ThemeDescription(null, 0, null, null, null, -__lambda_profileactivity_unq2igf3-hzp0ahtdz-wibwhm_i2, "avatar_backgroundRed");
        r11[42] = new ThemeDescription(null, 0, null, null, null, -__lambda_profileactivity_unq2igf3-hzp0ahtdz-wibwhm_i2, "avatar_backgroundOrange");
        r11[43] = new ThemeDescription(null, 0, null, null, null, -__lambda_profileactivity_unq2igf3-hzp0ahtdz-wibwhm_i2, "avatar_backgroundViolet");
        r11[44] = new ThemeDescription(null, 0, null, null, null, -__lambda_profileactivity_unq2igf3-hzp0ahtdz-wibwhm_i2, "avatar_backgroundGreen");
        r11[45] = new ThemeDescription(null, 0, null, null, null, -__lambda_profileactivity_unq2igf3-hzp0ahtdz-wibwhm_i2, "avatar_backgroundCyan");
        r11[46] = new ThemeDescription(null, 0, null, null, null, -__lambda_profileactivity_unq2igf3-hzp0ahtdz-wibwhm_i2, "avatar_backgroundBlue");
        r11[47] = new ThemeDescription(null, 0, null, null, null, -__lambda_profileactivity_unq2igf3-hzp0ahtdz-wibwhm_i2, "avatar_backgroundPink");
        r11[48] = new ThemeDescription(this.undoView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "undo_background");
        r11[49] = new ThemeDescription(this.undoView, 0, new Class[]{UndoView.class}, new String[]{"undoImageView"}, null, null, null, "undo_cancelColor");
        r11[50] = new ThemeDescription(this.undoView, 0, new Class[]{UndoView.class}, new String[]{"undoTextView"}, null, null, null, "undo_cancelColor");
        r11[51] = new ThemeDescription(this.undoView, 0, new Class[]{UndoView.class}, new String[]{"infoTextView"}, null, null, null, "undo_infoColor");
        r11[52] = new ThemeDescription(this.undoView, 0, new Class[]{UndoView.class}, new String[]{"textPaint"}, null, null, null, "undo_infoColor");
        r11[53] = new ThemeDescription(this.undoView, 0, new Class[]{UndoView.class}, new String[]{"progressPaint"}, null, null, null, "undo_infoColor");
        r11[54] = new ThemeDescription(this.undoView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{UndoView.class}, new String[]{"leftImageView"}, null, null, null, "undo_infoColor");
        r11[55] = new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{AboutLinkCell.class}, Theme.profile_aboutTextPaint, null, null, "windowBackgroundWhiteBlackText");
        r11[56] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LINKCOLOR, new Class[]{AboutLinkCell.class}, Theme.profile_aboutTextPaint, null, null, "windowBackgroundWhiteLinkText");
        r11[57] = new ThemeDescription(this.listView, 0, new Class[]{AboutLinkCell.class}, Theme.linkSelectionPaint, null, null, "windowBackgroundWhiteLinkSelection");
        r11[58] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, "windowBackgroundGrayShadow");
        r11[59] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{ShadowSectionCell.class}, null, null, null, "windowBackgroundGray");
        r11[60] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, "windowBackgroundGrayShadow");
        r11[61] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextInfoPrivacyCell.class}, null, null, null, "windowBackgroundGray");
        r11[62] = new ThemeDescription(this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteGrayText4");
        r11[63] = new ThemeDescription(this.nameTextView[1], 0, null, null, new Drawable[]{Theme.profile_verifiedCheckDrawable}, null, "profile_verifiedCheck");
        r11[64] = new ThemeDescription(this.nameTextView[1], 0, null, null, new Drawable[]{Theme.profile_verifiedDrawable}, null, "profile_verifiedBackground");
        return r11;
    }

    public /* synthetic */ void lambda$getThemeDescriptions$21$ProfileActivity() {
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            int childCount = recyclerListView.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = this.listView.getChildAt(i);
                if (childAt instanceof UserCell) {
                    ((UserCell) childAt).update(0);
                }
            }
        }
    }
}
