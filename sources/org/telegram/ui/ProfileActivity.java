package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Property;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.VelocityTracker;
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
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.LayoutParams;
import androidx.recyclerview.widget.RecyclerView.OnScrollListener;
import androidx.recyclerview.widget.RecyclerView.Recycler;
import androidx.recyclerview.widget.RecyclerView.State;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager.OnPageChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.CountDownLatch;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
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
import org.telegram.ui.Components.CrossfadeDrawable;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.ProfileGalleryView;
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
    private boolean allowPullingDown;
    private ActionBarMenuItem animatingItem;
    private float animationProgress;
    private int audioRow;
    private AvatarDrawable avatarDrawable;
    private AvatarImageView avatarImage;
    private float avatarScale;
    private float avatarX;
    private float avatarY;
    private ProfileGalleryView avatarsViewPager;
    private PagerIndicatorView avatarsViewPagerIndicatorView;
    private int banFromGroup;
    private int blockedUsersRow;
    private BotInfo botInfo;
    private int bottomPaddingRow;
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
    private ValueAnimator expandAnimator;
    private float[] expandAnimatorValues = new float[]{0.0f, 1.0f};
    private float expandProgress;
    private float extraHeight;
    private int filesRow;
    private int groupsInCommonRow;
    private int infoHeaderRow;
    private int infoSectionRow;
    private float initialAnimationExtraHeight;
    private boolean isBot;
    private boolean isInLandscapeMode;
    private boolean[] isOnline = new boolean[1];
    private boolean isPulledDown;
    private int joinRow;
    private int lastMeasuredContentWidth;
    private int[] lastMediaCount = new int[]{-1, -1, -1, -1, -1};
    private int lastSectionRow;
    private LinearLayoutManager layoutManager;
    private int leaveChannelRow;
    private int linksRow;
    private ListAdapter listAdapter;
    private int listContentHeight;
    private RecyclerListView listView;
    private float listViewVelocityY;
    private boolean loadingUsers;
    private int locationRow;
    private Drawable lockIconDrawable;
    private MediaActivity mediaActivity;
    private int[] mediaCount = new int[]{-1, -1, -1, -1, -1};
    private int[] mediaMergeCount = new int[]{-1, -1, -1, -1, -1};
    private int membersEndRow;
    private int membersHeaderRow;
    private int membersSectionRow;
    private int membersStartRow;
    private long mergeDialogId;
    private SimpleTextView[] nameTextView = new SimpleTextView[2];
    private float nameX;
    private float nameY;
    private int notificationsDividerRow;
    private int notificationsRow;
    private int onlineCount = -1;
    private SimpleTextView[] onlineTextView = new SimpleTextView[2];
    private float onlineX;
    private float onlineY;
    private boolean openAnimationInProgress;
    private OverlaysView overlaysView;
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
    private Drawable verifiedCheckDrawable;
    private CrossfadeDrawable verifiedCrossfadeDrawable;
    private Drawable verifiedDrawable;
    private int voiceRow;
    private ImageView writeButton;
    private AnimatorSet writeButtonAnimation;

    private class OverlaysView extends View {
        private final ValueAnimator animator;
        private final float[] animatorValues;
        private final Paint backgroundPaint;
        private final GradientDrawable bottomOverlayGradient;
        private final Rect bottomOverlayRect;
        private boolean isOverlaysVisible;
        private final int statusBarHeight;
        private final GradientDrawable topOverlayGradient;
        private final Rect topOverlayRect;

        public OverlaysView(Context context) {
            super(context);
            this.statusBarHeight = ProfileActivity.this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0;
            this.topOverlayRect = new Rect();
            this.bottomOverlayRect = new Rect();
            this.animatorValues = new float[]{0.0f, 1.0f};
            setVisibility(8);
            this.topOverlayGradient = new GradientDrawable(Orientation.TOP_BOTTOM, new int[]{NUM, 0});
            this.topOverlayGradient.setShape(0);
            this.bottomOverlayGradient = new GradientDrawable(Orientation.BOTTOM_TOP, new int[]{NUM, 0});
            this.bottomOverlayGradient.setShape(0);
            this.backgroundPaint = new Paint(1);
            this.backgroundPaint.setColor(-16777216);
            this.animator = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
            this.animator.setDuration(250);
            this.animator.setInterpolator(CubicBezierInterpolator.EASE_BOTH);
            this.animator.addUpdateListener(new -$$Lambda$ProfileActivity$OverlaysView$yN0iGjZwjWa6hWzP6Bj7fHDyIxM(this));
            this.animator.addListener(new AnimatorListenerAdapter(ProfileActivity.this) {
                public void onAnimationEnd(Animator animator) {
                    if (!OverlaysView.this.isOverlaysVisible) {
                        OverlaysView.this.setVisibility(8);
                    }
                }

                public void onAnimationStart(Animator animator) {
                    OverlaysView.this.setVisibility(0);
                }
            });
        }

        public /* synthetic */ void lambda$new$0$ProfileActivity$OverlaysView(ValueAnimator valueAnimator) {
            float lerp = AndroidUtilities.lerp(this.animatorValues, valueAnimator.getAnimatedFraction());
            int i = (int) (255.0f * lerp);
            this.topOverlayGradient.setAlpha(i);
            this.bottomOverlayGradient.setAlpha(i);
            this.backgroundPaint.setAlpha((int) (lerp * 66.0f));
            invalidate();
        }

        public boolean isOverlaysVisible() {
            return this.isOverlaysVisible;
        }

        public void setOverlaysVisible(boolean z, float f) {
            if (z != this.isOverlaysVisible) {
                this.isOverlaysVisible = z;
                this.animator.cancel();
                float lerp = AndroidUtilities.lerp(this.animatorValues, this.animator.getAnimatedFraction());
                float f2 = 1.0f;
                if (z) {
                    this.animator.setDuration((long) (((1.0f - lerp) * 250.0f) / f));
                } else {
                    this.animator.setDuration((long) ((250.0f * lerp) / f));
                }
                float[] fArr = this.animatorValues;
                fArr[0] = lerp;
                if (!z) {
                    f2 = 0.0f;
                }
                fArr[1] = f2;
                this.animator.start();
            }
        }

        /* Access modifiers changed, original: protected */
        public void onSizeChanged(int i, int i2, int i3, int i4) {
            i3 = this.statusBarHeight + ActionBar.getCurrentActionBarHeight();
            this.topOverlayRect.set(0, 0, i, (int) (((float) i3) * 0.5f));
            this.bottomOverlayRect.set(0, (int) (((float) i2) - (((float) AndroidUtilities.dp(72.0f)) * 0.5f)), i, i2);
            this.topOverlayGradient.setBounds(0, this.topOverlayRect.bottom, i, i3 + AndroidUtilities.dp(16.0f));
            this.bottomOverlayGradient.setBounds(0, (i2 - AndroidUtilities.dp(72.0f)) - AndroidUtilities.dp(24.0f), i, this.bottomOverlayRect.top);
        }

        /* Access modifiers changed, original: protected */
        public void onDraw(Canvas canvas) {
            this.topOverlayGradient.draw(canvas);
            this.bottomOverlayGradient.draw(canvas);
            canvas.drawRect(this.topOverlayRect, this.backgroundPaint);
            canvas.drawRect(this.bottomOverlayRect, this.backgroundPaint);
        }
    }

    private class PagerIndicatorView extends View {
        private final PagerAdapter adapter = ProfileActivity.this.avatarsViewPager.getAdapter();
        private final ValueAnimator animator;
        private final float[] animatorValues = new float[]{0.0f, 1.0f};
        private final Paint backgroundPaint;
        private final RectF indicatorRect = new RectF();
        private boolean isIndicatorVisible;
        private final TextPaint textPaint;

        public PagerIndicatorView(Context context) {
            super(context);
            setVisibility(8);
            this.textPaint = new TextPaint(1);
            this.textPaint.setColor(-1);
            this.textPaint.setTypeface(Typeface.SANS_SERIF);
            this.textPaint.setTextAlign(Align.CENTER);
            this.textPaint.setTextSize(AndroidUtilities.dpf2(15.0f));
            this.backgroundPaint = new Paint(1);
            this.backgroundPaint.setColor(Color.parseColor("#26000000"));
            this.animator = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
            this.animator.setInterpolator(CubicBezierInterpolator.EASE_BOTH);
            this.animator.addUpdateListener(new -$$Lambda$ProfileActivity$PagerIndicatorView$cOgGdM_nCMAg8zT1arvWLr1lm0g(this));
            this.animator.addListener(new AnimatorListenerAdapter(ProfileActivity.this) {
                public void onAnimationEnd(Animator animator) {
                    if (PagerIndicatorView.this.isIndicatorVisible) {
                        ActionBarMenuItem access$1300 = PagerIndicatorView.this.getSecondaryMenuItem();
                        if (access$1300 != null) {
                            access$1300.setVisibility(8);
                            return;
                        }
                        return;
                    }
                    PagerIndicatorView.this.setVisibility(8);
                }

                public void onAnimationStart(Animator animator) {
                    ActionBarMenuItem access$1300 = PagerIndicatorView.this.getSecondaryMenuItem();
                    if (access$1300 != null) {
                        access$1300.setVisibility(0);
                    }
                    PagerIndicatorView.this.setVisibility(0);
                }
            });
            ProfileActivity.this.avatarsViewPager.addOnPageChangeListener(new OnPageChangeListener(ProfileActivity.this) {
                public void onPageScrollStateChanged(int i) {
                }

                public void onPageScrolled(int i, float f, int i2) {
                }

                public void onPageSelected(int i) {
                    PagerIndicatorView.this.invalidateIndicatorRect();
                }
            });
            this.adapter.registerDataSetObserver(new DataSetObserver(ProfileActivity.this) {
                public void onChanged() {
                    PagerIndicatorView.this.invalidateIndicatorRect();
                    PagerIndicatorView.this.refreshVisibility(1.0f);
                }
            });
        }

        public /* synthetic */ void lambda$new$0$ProfileActivity$PagerIndicatorView(ValueAnimator valueAnimator) {
            float lerp = AndroidUtilities.lerp(this.animatorValues, valueAnimator.getAnimatedFraction());
            ActionBarMenuItem secondaryMenuItem = getSecondaryMenuItem();
            if (secondaryMenuItem != null) {
                float f = 1.0f - lerp;
                secondaryMenuItem.setScaleX(f);
                secondaryMenuItem.setScaleY(f);
                secondaryMenuItem.setAlpha(f);
            }
            setScaleX(lerp);
            setScaleY(lerp);
            setAlpha(lerp);
        }

        public boolean isIndicatorVisible() {
            return this.isIndicatorVisible;
        }

        public boolean isIndicatorFullyVisible() {
            return this.isIndicatorVisible && !this.animator.isRunning();
        }

        public void setIndicatorVisible(boolean z, float f) {
            if (z != this.isIndicatorVisible) {
                this.isIndicatorVisible = z;
                this.animator.cancel();
                float lerp = AndroidUtilities.lerp(this.animatorValues, this.animator.getAnimatedFraction());
                float f2 = 1.0f;
                if (f <= 0.0f) {
                    this.animator.setDuration(0);
                } else if (z) {
                    this.animator.setDuration((long) (((1.0f - lerp) * 250.0f) / f));
                } else {
                    this.animator.setDuration((long) ((250.0f * lerp) / f));
                }
                float[] fArr = this.animatorValues;
                fArr[0] = lerp;
                if (!z) {
                    f2 = 0.0f;
                }
                fArr[1] = f2;
                this.animator.start();
            }
        }

        public void refreshVisibility(float f) {
            boolean z = true;
            if (!ProfileActivity.this.isPulledDown || this.adapter.getCount() <= 1) {
                z = false;
            }
            setIndicatorVisible(z, f);
        }

        /* Access modifiers changed, original: protected */
        public void onSizeChanged(int i, int i2, int i3, int i4) {
            invalidateIndicatorRect();
        }

        private void invalidateIndicatorRect() {
            float measureText = this.textPaint.measureText(getCurrentTitle());
            this.indicatorRect.right = (float) (getMeasuredWidth() - AndroidUtilities.dp(54.0f));
            RectF rectF = this.indicatorRect;
            rectF.left = rectF.right - (measureText + AndroidUtilities.dpf2(16.0f));
            this.indicatorRect.top = (float) ((ProfileActivity.this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0) + AndroidUtilities.dp(15.0f));
            RectF rectF2 = this.indicatorRect;
            rectF2.bottom = rectF2.top + ((float) AndroidUtilities.dp(26.0f));
            setPivotX(this.indicatorRect.centerX());
            setPivotY(this.indicatorRect.centerY());
            invalidate();
        }

        /* Access modifiers changed, original: protected */
        public void onDraw(Canvas canvas) {
            float dpf2 = AndroidUtilities.dpf2(12.0f);
            canvas.drawRoundRect(this.indicatorRect, dpf2, dpf2, this.backgroundPaint);
            canvas.drawText(getCurrentTitle(), this.indicatorRect.centerX(), this.indicatorRect.top + AndroidUtilities.dpf2(18.5f), this.textPaint);
        }

        private String getCurrentTitle() {
            return this.adapter.getPageTitle(ProfileActivity.this.avatarsViewPager.getCurrentItem()).toString();
        }

        private ActionBarMenuItem getSecondaryMenuItem() {
            if (ProfileActivity.this.callItem != null) {
                return ProfileActivity.this.callItem;
            }
            return ProfileActivity.this.editItem != null ? ProfileActivity.this.editItem : null;
        }
    }

    private class TopView extends View {
        private int currentColor;
        private Paint paint = new Paint();

        public TopView(Context context) {
            super(context);
        }

        /* Access modifiers changed, original: protected */
        public void onMeasure(int i, int i2) {
            setMeasuredDimension(MeasureSpec.getSize(i), MeasureSpec.getSize(i) + AndroidUtilities.dp(3.0f));
        }

        public void setBackgroundColor(int i) {
            if (i != this.currentColor) {
                this.currentColor = i;
                this.paint.setColor(i);
                invalidate();
            }
        }

        /* Access modifiers changed, original: protected */
        public void onDraw(Canvas canvas) {
            float currentActionBarHeight = ((float) (ActionBar.getCurrentActionBarHeight() + (ProfileActivity.this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0))) + ProfileActivity.this.extraHeight;
            canvas.drawRect(0.0f, 0.0f, (float) getMeasuredWidth(), currentActionBarHeight, this.paint);
            if (ProfileActivity.this.parentLayout != null) {
                ProfileActivity.this.parentLayout.drawHeaderShadow(canvas, (int) currentActionBarHeight);
            }
        }
    }

    public class AvatarImageView extends BackupImageView {
        private float foregroundAlpha;
        private ImageReceiver foregroundImageReceiver = new ImageReceiver(this);
        private final Paint placeholderPaint = new Paint(1);
        private final RectF rect = new RectF();

        public AvatarImageView(Context context) {
            super(context);
            this.placeholderPaint.setColor(-16777216);
        }

        public void setForegroundImage(ImageLocation imageLocation, String str, Drawable drawable) {
            this.foregroundImageReceiver.setImage(imageLocation, str, drawable, 0, null, null, 0);
        }

        public void setForegroundImageDrawable(Drawable drawable) {
            this.foregroundImageReceiver.setImageBitmap(drawable);
        }

        public float getForegroundAlpha() {
            return this.foregroundAlpha;
        }

        public void setForegroundAlpha(float f) {
            this.foregroundAlpha = f;
            invalidate();
        }

        public void clearForeground() {
            this.foregroundImageReceiver.clearImage();
            this.foregroundAlpha = 0.0f;
            invalidate();
        }

        /* Access modifiers changed, original: protected */
        public void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            this.foregroundImageReceiver.onDetachedFromWindow();
        }

        /* Access modifiers changed, original: protected */
        public void onAttachedToWindow() {
            super.onAttachedToWindow();
            this.foregroundImageReceiver.onAttachedToWindow();
        }

        public void setRoundRadius(int i) {
            super.setRoundRadius(i);
            this.foregroundImageReceiver.setRoundRadius(i);
        }

        /* Access modifiers changed, original: protected */
        public void onDraw(Canvas canvas) {
            if (this.foregroundAlpha < 1.0f) {
                this.imageReceiver.setImageCoords(0, 0, getMeasuredWidth(), getMeasuredHeight());
                this.imageReceiver.draw(canvas);
            }
            if (this.foregroundAlpha <= 0.0f) {
                return;
            }
            if (this.foregroundImageReceiver.getDrawable() != null) {
                this.foregroundImageReceiver.setImageCoords(0, 0, getMeasuredWidth(), getMeasuredHeight());
                this.foregroundImageReceiver.setAlpha(this.foregroundAlpha);
                this.foregroundImageReceiver.draw(canvas);
                return;
            }
            this.rect.set(0.0f, 0.0f, (float) getMeasuredWidth(), (float) getMeasuredHeight());
            this.placeholderPaint.setAlpha((int) (this.foregroundAlpha * 255.0f));
            float f = (float) this.foregroundImageReceiver.getRoundRadius()[0];
            canvas.drawRoundRect(this.rect, f, f, this.placeholderPaint);
        }
    }

    private class ListAdapter extends SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View headerCell;
            View textDetailCell;
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
                case 11:
                    textDetailCell = new EmptyCell(this.mContext, 36);
                    break;
                case 12:
                    textDetailCell = new View(this.mContext) {
                        private int lastListViewHeight = 0;
                        private int lastPaddingHeight = 0;

                        /* Access modifiers changed, original: protected */
                        public void onMeasure(int i, int i2) {
                            if (this.lastListViewHeight != ProfileActivity.this.listView.getMeasuredHeight()) {
                                this.lastPaddingHeight = 0;
                            }
                            this.lastListViewHeight = ProfileActivity.this.listView.getMeasuredHeight();
                            i = ProfileActivity.this.listView.getChildCount();
                            if (i == ProfileActivity.this.listAdapter.getItemCount()) {
                                int i3 = 0;
                                for (i2 = 0; i2 < i; i2++) {
                                    if (ProfileActivity.this.listView.getChildAdapterPosition(ProfileActivity.this.listView.getChildAt(i2)) != ProfileActivity.this.bottomPaddingRow) {
                                        i3 += ProfileActivity.this.listView.getChildAt(i2).getMeasuredHeight();
                                    }
                                }
                                i = ((ProfileActivity.this.fragmentView.getMeasuredHeight() - ActionBar.getCurrentActionBarHeight()) - AndroidUtilities.statusBarHeight) - i3;
                                if (i > AndroidUtilities.dp(88.0f)) {
                                    i = 0;
                                }
                                if (i <= 0) {
                                    i = 0;
                                }
                                i2 = ProfileActivity.this.listView.getMeasuredWidth();
                                this.lastPaddingHeight = i;
                                setMeasuredDimension(i2, i);
                                return;
                            }
                            setMeasuredDimension(ProfileActivity.this.listView.getMeasuredWidth(), this.lastPaddingHeight);
                        }
                    };
                    break;
                default:
                    textDetailCell = null;
                    break;
            }
            textDetailCell = headerCell;
            textDetailCell.setLayoutParams(new LayoutParams(-1, -2));
            return new Holder(textDetailCell);
        }

        /* JADX WARNING: Removed duplicated region for block: B:102:0x02ea  */
        /* JADX WARNING: Removed duplicated region for block: B:102:0x02ea  */
        public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r18, int r19) {
            /*
            r17 = this;
            r0 = r17;
            r1 = r18;
            r2 = r19;
            r3 = r18.getItemViewType();
            r4 = 3;
            r5 = NUM; // 0x7f0e0267 float:1.8876285E38 double:1.0531624605E-314;
            r6 = "ChannelMembers";
            r7 = 0;
            r8 = -1;
            r9 = 0;
            r10 = 1;
            switch(r3) {
                case 1: goto L_0x08aa;
                case 2: goto L_0x0770;
                case 3: goto L_0x0724;
                case 4: goto L_0x0301;
                case 5: goto L_0x0017;
                case 6: goto L_0x0190;
                case 7: goto L_0x00e6;
                case 8: goto L_0x0019;
                default: goto L_0x0017;
            };
        L_0x0017:
            goto L_0x0912;
        L_0x0019:
            r1 = r1.itemView;
            r11 = r1;
            r11 = (org.telegram.ui.Cells.UserCell) r11;
            r1 = org.telegram.ui.ProfileActivity.this;
            r1 = r1.sortedUsers;
            r1 = r1.isEmpty();
            if (r1 != 0) goto L_0x0053;
        L_0x002a:
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
            goto L_0x006b;
        L_0x0053:
            r1 = org.telegram.ui.ProfileActivity.this;
            r1 = r1.chatInfo;
            r1 = r1.participants;
            r1 = r1.participants;
            r3 = org.telegram.ui.ProfileActivity.this;
            r3 = r3.membersStartRow;
            r3 = r2 - r3;
            r1 = r1.get(r3);
            r1 = (org.telegram.tgnet.TLRPC.ChatParticipant) r1;
        L_0x006b:
            if (r1 == 0) goto L_0x0912;
        L_0x006d:
            r3 = r1 instanceof org.telegram.tgnet.TLRPC.TL_chatChannelParticipant;
            if (r3 == 0) goto L_0x009e;
        L_0x0071:
            r3 = r1;
            r3 = (org.telegram.tgnet.TLRPC.TL_chatChannelParticipant) r3;
            r3 = r3.channelParticipant;
            r4 = r3.rank;
            r4 = android.text.TextUtils.isEmpty(r4);
            if (r4 != 0) goto L_0x0082;
        L_0x007e:
            r3 = r3.rank;
        L_0x0080:
            r7 = r3;
            goto L_0x00b9;
        L_0x0082:
            r4 = r3 instanceof org.telegram.tgnet.TLRPC.TL_channelParticipantCreator;
            if (r4 == 0) goto L_0x0090;
        L_0x0086:
            r3 = NUM; // 0x7f0e0253 float:1.8876244E38 double:1.0531624506E-314;
            r4 = "ChannelCreator";
            r3 = org.telegram.messenger.LocaleController.getString(r4, r3);
            goto L_0x0080;
        L_0x0090:
            r3 = r3 instanceof org.telegram.tgnet.TLRPC.TL_channelParticipantAdmin;
            if (r3 == 0) goto L_0x00b9;
        L_0x0094:
            r3 = NUM; // 0x7f0e0242 float:1.887621E38 double:1.053162442E-314;
            r4 = "ChannelAdmin";
            r3 = org.telegram.messenger.LocaleController.getString(r4, r3);
            goto L_0x0080;
        L_0x009e:
            r3 = r1 instanceof org.telegram.tgnet.TLRPC.TL_chatParticipantCreator;
            if (r3 == 0) goto L_0x00ac;
        L_0x00a2:
            r3 = NUM; // 0x7f0e0253 float:1.8876244E38 double:1.0531624506E-314;
            r4 = "ChannelCreator";
            r7 = org.telegram.messenger.LocaleController.getString(r4, r3);
            goto L_0x00b9;
        L_0x00ac:
            r3 = r1 instanceof org.telegram.tgnet.TLRPC.TL_chatParticipantAdmin;
            if (r3 == 0) goto L_0x00b9;
        L_0x00b0:
            r3 = NUM; // 0x7f0e0242 float:1.887621E38 double:1.053162442E-314;
            r4 = "ChannelAdmin";
            r7 = org.telegram.messenger.LocaleController.getString(r4, r3);
        L_0x00b9:
            r11.setAdminRole(r7);
            r3 = org.telegram.ui.ProfileActivity.this;
            r3 = r3.currentAccount;
            r3 = org.telegram.messenger.MessagesController.getInstance(r3);
            r1 = r1.user_id;
            r1 = java.lang.Integer.valueOf(r1);
            r12 = r3.getUser(r1);
            r13 = 0;
            r14 = 0;
            r15 = 0;
            r1 = org.telegram.ui.ProfileActivity.this;
            r1 = r1.membersEndRow;
            r1 = r1 - r10;
            if (r2 == r1) goto L_0x00df;
        L_0x00dc:
            r16 = 1;
            goto L_0x00e1;
        L_0x00df:
            r16 = 0;
        L_0x00e1:
            r11.setData(r12, r13, r14, r15, r16);
            goto L_0x0912;
        L_0x00e6:
            r1 = r1.itemView;
            r3 = java.lang.Integer.valueOf(r19);
            r1.setTag(r3);
            r3 = org.telegram.ui.ProfileActivity.this;
            r3 = r3.infoSectionRow;
            if (r2 != r3) goto L_0x010f;
        L_0x00f7:
            r3 = org.telegram.ui.ProfileActivity.this;
            r3 = r3.sharedSectionRow;
            if (r3 != r8) goto L_0x010f;
        L_0x00ff:
            r3 = org.telegram.ui.ProfileActivity.this;
            r3 = r3.lastSectionRow;
            if (r3 != r8) goto L_0x010f;
        L_0x0107:
            r3 = org.telegram.ui.ProfileActivity.this;
            r3 = r3.settingsSectionRow;
            if (r3 == r8) goto L_0x016b;
        L_0x010f:
            r3 = org.telegram.ui.ProfileActivity.this;
            r3 = r3.settingsSectionRow;
            if (r2 != r3) goto L_0x011f;
        L_0x0117:
            r3 = org.telegram.ui.ProfileActivity.this;
            r3 = r3.sharedSectionRow;
            if (r3 == r8) goto L_0x016b;
        L_0x011f:
            r3 = org.telegram.ui.ProfileActivity.this;
            r3 = r3.sharedSectionRow;
            if (r2 != r3) goto L_0x012f;
        L_0x0127:
            r3 = org.telegram.ui.ProfileActivity.this;
            r3 = r3.lastSectionRow;
            if (r3 == r8) goto L_0x016b;
        L_0x012f:
            r3 = org.telegram.ui.ProfileActivity.this;
            r3 = r3.lastSectionRow;
            if (r2 == r3) goto L_0x016b;
        L_0x0137:
            r3 = org.telegram.ui.ProfileActivity.this;
            r3 = r3.membersSectionRow;
            if (r2 != r3) goto L_0x015e;
        L_0x013f:
            r2 = org.telegram.ui.ProfileActivity.this;
            r2 = r2.lastSectionRow;
            if (r2 != r8) goto L_0x015e;
        L_0x0147:
            r2 = org.telegram.ui.ProfileActivity.this;
            r2 = r2.sharedSectionRow;
            if (r2 == r8) goto L_0x016b;
        L_0x014f:
            r2 = org.telegram.ui.ProfileActivity.this;
            r2 = r2.membersSectionRow;
            r3 = org.telegram.ui.ProfileActivity.this;
            r3 = r3.sharedSectionRow;
            if (r2 <= r3) goto L_0x015e;
        L_0x015d:
            goto L_0x016b;
        L_0x015e:
            r2 = r0.mContext;
            r3 = NUM; // 0x7var_df float:1.794503E38 double:1.052935613E-314;
            r4 = "windowBackgroundGrayShadow";
            r2 = org.telegram.ui.ActionBar.Theme.getThemedDrawable(r2, r3, r4);
            goto L_0x0177;
        L_0x016b:
            r2 = r0.mContext;
            r3 = NUM; // 0x7var_e0 float:1.7945032E38 double:1.0529356137E-314;
            r4 = "windowBackgroundGrayShadow";
            r2 = org.telegram.ui.ActionBar.Theme.getThemedDrawable(r2, r3, r4);
        L_0x0177:
            r3 = new org.telegram.ui.Components.CombinedDrawable;
            r4 = new android.graphics.drawable.ColorDrawable;
            r5 = "windowBackgroundGray";
            r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
            r4.<init>(r5);
            r3.<init>(r4, r2);
            r3.setFullsize(r10);
            r1.setBackgroundDrawable(r3);
            goto L_0x0912;
        L_0x0190:
            r1 = r1.itemView;
            r1 = (org.telegram.ui.Cells.NotificationsCheckCell) r1;
            r3 = org.telegram.ui.ProfileActivity.this;
            r3 = r3.notificationsRow;
            if (r2 != r3) goto L_0x0912;
        L_0x019c:
            r2 = org.telegram.ui.ProfileActivity.this;
            r2 = r2.currentAccount;
            r2 = org.telegram.messenger.MessagesController.getNotificationsSettings(r2);
            r3 = org.telegram.ui.ProfileActivity.this;
            r5 = r3.dialog_id;
            r11 = 0;
            r3 = (r5 > r11 ? 1 : (r5 == r11 ? 0 : -1));
            if (r3 == 0) goto L_0x01b9;
        L_0x01b2:
            r3 = org.telegram.ui.ProfileActivity.this;
            r5 = r3.dialog_id;
            goto L_0x01d0;
        L_0x01b9:
            r3 = org.telegram.ui.ProfileActivity.this;
            r3 = r3.user_id;
            if (r3 == 0) goto L_0x01c8;
        L_0x01c1:
            r3 = org.telegram.ui.ProfileActivity.this;
            r3 = r3.user_id;
            goto L_0x01cf;
        L_0x01c8:
            r3 = org.telegram.ui.ProfileActivity.this;
            r3 = r3.chat_id;
            r3 = -r3;
        L_0x01cf:
            r5 = (long) r3;
        L_0x01d0:
            r3 = new java.lang.StringBuilder;
            r3.<init>();
            r8 = "custom_";
            r3.append(r8);
            r3.append(r5);
            r3 = r3.toString();
            r3 = r2.getBoolean(r3, r9);
            r8 = new java.lang.StringBuilder;
            r8.<init>();
            r11 = "notify2_";
            r8.append(r11);
            r8.append(r5);
            r8 = r8.toString();
            r8 = r2.contains(r8);
            r12 = new java.lang.StringBuilder;
            r12.<init>();
            r12.append(r11);
            r12.append(r5);
            r11 = r12.toString();
            r11 = r2.getInt(r11, r9);
            r12 = new java.lang.StringBuilder;
            r12.<init>();
            r13 = "notifyuntil_";
            r12.append(r13);
            r12.append(r5);
            r12 = r12.toString();
            r2 = r2.getInt(r12, r9);
            if (r11 != r4) goto L_0x02ad;
        L_0x0224:
            r4 = NUM; // 0x7fffffff float:NaN double:1.060997895E-314;
            if (r2 == r4) goto L_0x02ad;
        L_0x0229:
            r4 = org.telegram.ui.ProfileActivity.this;
            r4 = r4.currentAccount;
            r4 = org.telegram.tgnet.ConnectionsManager.getInstance(r4);
            r4 = r4.getCurrentTime();
            r2 = r2 - r4;
            if (r2 > 0) goto L_0x0252;
        L_0x023a:
            if (r3 == 0) goto L_0x0246;
        L_0x023c:
            r2 = NUM; // 0x7f0e074b float:1.8878824E38 double:1.053163079E-314;
            r3 = "NotificationsCustom";
            r2 = org.telegram.messenger.LocaleController.getString(r3, r2);
            goto L_0x024f;
        L_0x0246:
            r2 = NUM; // 0x7f0e0763 float:1.8878873E38 double:1.053163091E-314;
            r3 = "NotificationsOn";
            r2 = org.telegram.messenger.LocaleController.getString(r3, r2);
        L_0x024f:
            r7 = r2;
            goto L_0x02e8;
        L_0x0252:
            r3 = 3600; // 0xe10 float:5.045E-42 double:1.7786E-320;
            r4 = NUM; // 0x7f0e0cc9 float:1.8881676E38 double:1.0531637737E-314;
            r5 = "WillUnmuteIn";
            if (r2 >= r3) goto L_0x026e;
        L_0x025b:
            r3 = new java.lang.Object[r10];
            r2 = r2 / 60;
            r6 = "Minutes";
            r2 = org.telegram.messenger.LocaleController.formatPluralString(r6, r2);
            r3[r9] = r2;
            r7 = org.telegram.messenger.LocaleController.formatString(r5, r4, r3);
        L_0x026b:
            r10 = 0;
            goto L_0x02e8;
        L_0x026e:
            r3 = 86400; // 0x15180 float:1.21072E-40 double:4.26873E-319;
            r6 = NUM; // 0x42700000 float:60.0 double:5.507034975E-315;
            if (r2 >= r3) goto L_0x028d;
        L_0x0275:
            r3 = new java.lang.Object[r10];
            r2 = (float) r2;
            r2 = r2 / r6;
            r2 = r2 / r6;
            r6 = (double) r2;
            r6 = java.lang.Math.ceil(r6);
            r2 = (int) r6;
            r6 = "Hours";
            r2 = org.telegram.messenger.LocaleController.formatPluralString(r6, r2);
            r3[r9] = r2;
            r7 = org.telegram.messenger.LocaleController.formatString(r5, r4, r3);
            goto L_0x026b;
        L_0x028d:
            r3 = 31536000; // 0x1e13380 float:8.2725845E-38 double:1.5580854E-316;
            if (r2 >= r3) goto L_0x026b;
        L_0x0292:
            r3 = new java.lang.Object[r10];
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
            r3[r9] = r2;
            r7 = org.telegram.messenger.LocaleController.formatString(r5, r4, r3);
            goto L_0x026b;
        L_0x02ad:
            if (r11 != 0) goto L_0x02c2;
        L_0x02af:
            if (r8 == 0) goto L_0x02b2;
        L_0x02b1:
            goto L_0x02c7;
        L_0x02b2:
            r2 = org.telegram.ui.ProfileActivity.this;
            r2 = r2.currentAccount;
            r2 = org.telegram.messenger.NotificationsController.getInstance(r2);
            r2 = r2.isGlobalNotificationsEnabled(r5);
            r10 = r2;
            goto L_0x02c7;
        L_0x02c2:
            if (r11 != r10) goto L_0x02c5;
        L_0x02c4:
            goto L_0x02c7;
        L_0x02c5:
            r2 = 2;
            r10 = 0;
        L_0x02c7:
            if (r10 == 0) goto L_0x02d5;
        L_0x02c9:
            if (r3 == 0) goto L_0x02d5;
        L_0x02cb:
            r2 = NUM; // 0x7f0e074b float:1.8878824E38 double:1.053163079E-314;
            r3 = "NotificationsCustom";
            r7 = org.telegram.messenger.LocaleController.getString(r3, r2);
            goto L_0x02e8;
        L_0x02d5:
            if (r10 == 0) goto L_0x02dd;
        L_0x02d7:
            r2 = NUM; // 0x7f0e0763 float:1.8878873E38 double:1.053163091E-314;
            r3 = "NotificationsOn";
            goto L_0x02e2;
        L_0x02dd:
            r2 = NUM; // 0x7f0e0761 float:1.8878869E38 double:1.05316309E-314;
            r3 = "NotificationsOff";
        L_0x02e2:
            r2 = org.telegram.messenger.LocaleController.getString(r3, r2);
            goto L_0x024f;
        L_0x02e8:
            if (r7 != 0) goto L_0x02f3;
        L_0x02ea:
            r2 = NUM; // 0x7f0e0761 float:1.8878869E38 double:1.05316309E-314;
            r3 = "NotificationsOff";
            r7 = org.telegram.messenger.LocaleController.getString(r3, r2);
        L_0x02f3:
            r2 = NUM; // 0x7f0e0747 float:1.8878816E38 double:1.053163077E-314;
            r3 = "Notifications";
            r2 = org.telegram.messenger.LocaleController.getString(r3, r2);
            r1.setTextAndValueAndCheck(r2, r7, r10, r9);
            goto L_0x0912;
        L_0x0301:
            r1 = r1.itemView;
            r1 = (org.telegram.ui.Cells.TextCell) r1;
            r3 = "windowBackgroundWhiteBlackText";
            r8 = "windowBackgroundWhiteGrayIcon";
            r1.setColors(r8, r3);
            r1.setTag(r3);
            r3 = org.telegram.ui.ProfileActivity.this;
            r3 = r3.photosRow;
            r8 = "%d";
            if (r2 != r3) goto L_0x034a;
        L_0x031b:
            r3 = NUM; // 0x7f0e0a6e float:1.8880453E38 double:1.053163476E-314;
            r4 = "SharedPhotosAndVideos";
            r3 = org.telegram.messenger.LocaleController.getString(r4, r3);
            r4 = new java.lang.Object[r10];
            r5 = org.telegram.ui.ProfileActivity.this;
            r5 = r5.lastMediaCount;
            r5 = r5[r9];
            r5 = java.lang.Integer.valueOf(r5);
            r4[r9] = r5;
            r4 = java.lang.String.format(r8, r4);
            r5 = NUM; // 0x7var_ float:1.7945876E38 double:1.052935819E-314;
            r6 = org.telegram.ui.ProfileActivity.this;
            r6 = r6.sharedSectionRow;
            r6 = r6 - r10;
            if (r2 == r6) goto L_0x0345;
        L_0x0344:
            r9 = 1;
        L_0x0345:
            r1.setTextAndValueAndIcon(r3, r4, r5, r9);
            goto L_0x0912;
        L_0x034a:
            r3 = org.telegram.ui.ProfileActivity.this;
            r3 = r3.filesRow;
            if (r2 != r3) goto L_0x0381;
        L_0x0352:
            r3 = NUM; // 0x7f0e04be float:1.88775E38 double:1.0531627564E-314;
            r4 = "FilesDataUsage";
            r3 = org.telegram.messenger.LocaleController.getString(r4, r3);
            r4 = new java.lang.Object[r10];
            r5 = org.telegram.ui.ProfileActivity.this;
            r5 = r5.lastMediaCount;
            r5 = r5[r10];
            r5 = java.lang.Integer.valueOf(r5);
            r4[r9] = r5;
            r4 = java.lang.String.format(r8, r4);
            r5 = NUM; // 0x7var_b float:1.7945866E38 double:1.052935817E-314;
            r6 = org.telegram.ui.ProfileActivity.this;
            r6 = r6.sharedSectionRow;
            r6 = r6 - r10;
            if (r2 == r6) goto L_0x037c;
        L_0x037b:
            r9 = 1;
        L_0x037c:
            r1.setTextAndValueAndIcon(r3, r4, r5, r9);
            goto L_0x0912;
        L_0x0381:
            r3 = org.telegram.ui.ProfileActivity.this;
            r3 = r3.linksRow;
            if (r2 != r3) goto L_0x03b8;
        L_0x0389:
            r3 = NUM; // 0x7f0e0a69 float:1.8880443E38 double:1.0531634733E-314;
            r5 = "SharedLinks";
            r3 = org.telegram.messenger.LocaleController.getString(r5, r3);
            r5 = new java.lang.Object[r10];
            r6 = org.telegram.ui.ProfileActivity.this;
            r6 = r6.lastMediaCount;
            r4 = r6[r4];
            r4 = java.lang.Integer.valueOf(r4);
            r5[r9] = r4;
            r4 = java.lang.String.format(r8, r5);
            r5 = NUM; // 0x7var_d float:1.794587E38 double:1.0529358177E-314;
            r6 = org.telegram.ui.ProfileActivity.this;
            r6 = r6.sharedSectionRow;
            r6 = r6 - r10;
            if (r2 == r6) goto L_0x03b3;
        L_0x03b2:
            r9 = 1;
        L_0x03b3:
            r1.setTextAndValueAndIcon(r3, r4, r5, r9);
            goto L_0x0912;
        L_0x03b8:
            r3 = org.telegram.ui.ProfileActivity.this;
            r3 = r3.audioRow;
            if (r2 != r3) goto L_0x03f0;
        L_0x03c0:
            r3 = NUM; // 0x7f0e0a65 float:1.8880435E38 double:1.0531634713E-314;
            r4 = "SharedAudioFiles";
            r3 = org.telegram.messenger.LocaleController.getString(r4, r3);
            r4 = new java.lang.Object[r10];
            r5 = org.telegram.ui.ProfileActivity.this;
            r5 = r5.lastMediaCount;
            r6 = 4;
            r5 = r5[r6];
            r5 = java.lang.Integer.valueOf(r5);
            r4[r9] = r5;
            r4 = java.lang.String.format(r8, r4);
            r5 = NUM; // 0x7var_ float:1.7945862E38 double:1.052935816E-314;
            r6 = org.telegram.ui.ProfileActivity.this;
            r6 = r6.sharedSectionRow;
            r6 = r6 - r10;
            if (r2 == r6) goto L_0x03eb;
        L_0x03ea:
            r9 = 1;
        L_0x03eb:
            r1.setTextAndValueAndIcon(r3, r4, r5, r9);
            goto L_0x0912;
        L_0x03f0:
            r3 = org.telegram.ui.ProfileActivity.this;
            r3 = r3.voiceRow;
            if (r2 != r3) goto L_0x0428;
        L_0x03f8:
            r3 = NUM; // 0x7f0e016a float:1.8875772E38 double:1.0531623355E-314;
            r4 = "AudioAutodownload";
            r3 = org.telegram.messenger.LocaleController.getString(r4, r3);
            r4 = new java.lang.Object[r10];
            r5 = org.telegram.ui.ProfileActivity.this;
            r5 = r5.lastMediaCount;
            r6 = 2;
            r5 = r5[r6];
            r5 = java.lang.Integer.valueOf(r5);
            r4[r9] = r5;
            r4 = java.lang.String.format(r8, r4);
            r5 = NUM; // 0x7var_ float:1.7945878E38 double:1.0529358197E-314;
            r6 = org.telegram.ui.ProfileActivity.this;
            r6 = r6.sharedSectionRow;
            r6 = r6 - r10;
            if (r2 == r6) goto L_0x0423;
        L_0x0422:
            r9 = 1;
        L_0x0423:
            r1.setTextAndValueAndIcon(r3, r4, r5, r9);
            goto L_0x0912;
        L_0x0428:
            r3 = org.telegram.ui.ProfileActivity.this;
            r3 = r3.groupsInCommonRow;
            r4 = NUM; // 0x7var_ float:1.7944718E38 double:1.052935537E-314;
            if (r2 != r3) goto L_0x045f;
        L_0x0433:
            r3 = NUM; // 0x7f0e0556 float:1.8877808E38 double:1.0531628315E-314;
            r5 = "GroupsInCommonTitle";
            r3 = org.telegram.messenger.LocaleController.getString(r5, r3);
            r5 = new java.lang.Object[r10];
            r6 = org.telegram.ui.ProfileActivity.this;
            r6 = r6.userInfo;
            r6 = r6.common_chats_count;
            r6 = java.lang.Integer.valueOf(r6);
            r5[r9] = r6;
            r5 = java.lang.String.format(r8, r5);
            r6 = org.telegram.ui.ProfileActivity.this;
            r6 = r6.sharedSectionRow;
            r6 = r6 - r10;
            if (r2 == r6) goto L_0x045a;
        L_0x0459:
            r9 = 1;
        L_0x045a:
            r1.setTextAndValueAndIcon(r3, r5, r4, r9);
            goto L_0x0912;
        L_0x045f:
            r3 = org.telegram.ui.ProfileActivity.this;
            r3 = r3.settingsTimerRow;
            if (r2 != r3) goto L_0x04a3;
        L_0x0467:
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
            if (r2 != 0) goto L_0x0491;
        L_0x0487:
            r2 = NUM; // 0x7f0e0a75 float:1.8880467E38 double:1.053163479E-314;
            r3 = "ShortMessageLifetimeForever";
            r2 = org.telegram.messenger.LocaleController.getString(r3, r2);
            goto L_0x0495;
        L_0x0491:
            r2 = org.telegram.messenger.LocaleController.formatTTLString(r2);
        L_0x0495:
            r3 = NUM; // 0x7f0e0648 float:1.8878299E38 double:1.053162951E-314;
            r4 = "MessageLifetime";
            r3 = org.telegram.messenger.LocaleController.getString(r4, r3);
            r1.setTextAndValue(r3, r2, r9);
            goto L_0x0912;
        L_0x04a3:
            r3 = org.telegram.ui.ProfileActivity.this;
            r3 = r3.unblockRow;
            if (r2 != r3) goto L_0x04bf;
        L_0x04ab:
            r2 = NUM; // 0x7f0e0b67 float:1.8880958E38 double:1.053163599E-314;
            r3 = "Unblock";
            r2 = org.telegram.messenger.LocaleController.getString(r3, r2);
            r1.setText(r2, r9);
            r2 = "windowBackgroundWhiteRedText5";
            r1.setColors(r7, r2);
            goto L_0x0912;
        L_0x04bf:
            r3 = org.telegram.ui.ProfileActivity.this;
            r3 = r3.startSecretChatRow;
            if (r2 != r3) goto L_0x04db;
        L_0x04c7:
            r2 = NUM; // 0x7f0e0aa2 float:1.8880558E38 double:1.0531635015E-314;
            r3 = "StartEncryptedChat";
            r2 = org.telegram.messenger.LocaleController.getString(r3, r2);
            r1.setText(r2, r9);
            r2 = "windowBackgroundWhiteGreenText2";
            r1.setColors(r7, r2);
            goto L_0x0912;
        L_0x04db:
            r3 = org.telegram.ui.ProfileActivity.this;
            r3 = r3.settingsKeyRow;
            if (r2 != r3) goto L_0x0515;
        L_0x04e3:
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
            r3 = NUM; // 0x7f0e0438 float:1.8877228E38 double:1.05316269E-314;
            r4 = "EncryptionKey";
            r3 = org.telegram.messenger.LocaleController.getString(r4, r3);
            r1.setTextAndValueDrawable(r3, r2, r9);
            goto L_0x0912;
        L_0x0515:
            r3 = org.telegram.ui.ProfileActivity.this;
            r3 = r3.leaveChannelRow;
            if (r2 != r3) goto L_0x0531;
        L_0x051d:
            r2 = "windowBackgroundWhiteRedText5";
            r1.setColors(r7, r2);
            r2 = NUM; // 0x7f0e05d3 float:1.8878062E38 double:1.0531628933E-314;
            r3 = "LeaveChannel";
            r2 = org.telegram.messenger.LocaleController.getString(r3, r2);
            r1.setText(r2, r9);
            goto L_0x0912;
        L_0x0531:
            r3 = org.telegram.ui.ProfileActivity.this;
            r3 = r3.joinRow;
            if (r2 != r3) goto L_0x056f;
        L_0x0539:
            r2 = "windowBackgroundWhiteBlueText2";
            r1.setColors(r7, r2);
            r2 = "windowBackgroundWhiteBlueText2";
            r2 = org.telegram.ui.ActionBar.Theme.getColor(r2);
            r1.setTextColor(r2);
            r2 = org.telegram.ui.ProfileActivity.this;
            r2 = r2.currentChat;
            r2 = r2.megagroup;
            if (r2 == 0) goto L_0x0561;
        L_0x0553:
            r2 = NUM; // 0x7f0e093b float:1.887983E38 double:1.053163324E-314;
            r3 = "ProfileJoinGroup";
            r2 = org.telegram.messenger.LocaleController.getString(r3, r2);
            r1.setText(r2, r9);
            goto L_0x0912;
        L_0x0561:
            r2 = NUM; // 0x7f0e093a float:1.8879828E38 double:1.0531633236E-314;
            r3 = "ProfileJoinChannel";
            r2 = org.telegram.messenger.LocaleController.getString(r3, r2);
            r1.setText(r2, r9);
            goto L_0x0912;
        L_0x056f:
            r3 = org.telegram.ui.ProfileActivity.this;
            r3 = r3.subscribersRow;
            if (r2 != r3) goto L_0x0629;
        L_0x0577:
            r3 = org.telegram.ui.ProfileActivity.this;
            r3 = r3.chatInfo;
            if (r3 == 0) goto L_0x05e8;
        L_0x057f:
            r3 = org.telegram.ui.ProfileActivity.this;
            r3 = r3.currentChat;
            r3 = org.telegram.messenger.ChatObject.isChannel(r3);
            if (r3 == 0) goto L_0x05c1;
        L_0x058b:
            r3 = org.telegram.ui.ProfileActivity.this;
            r3 = r3.currentChat;
            r3 = r3.megagroup;
            if (r3 != 0) goto L_0x05c1;
        L_0x0595:
            r3 = NUM; // 0x7f0e0293 float:1.8876374E38 double:1.053162482E-314;
            r5 = "ChannelSubscribers";
            r3 = org.telegram.messenger.LocaleController.getString(r5, r3);
            r5 = new java.lang.Object[r10];
            r6 = org.telegram.ui.ProfileActivity.this;
            r6 = r6.chatInfo;
            r6 = r6.participants_count;
            r6 = java.lang.Integer.valueOf(r6);
            r5[r9] = r6;
            r5 = java.lang.String.format(r8, r5);
            r6 = org.telegram.ui.ProfileActivity.this;
            r6 = r6.membersSectionRow;
            r6 = r6 - r10;
            if (r2 == r6) goto L_0x05bc;
        L_0x05bb:
            r9 = 1;
        L_0x05bc:
            r1.setTextAndValueAndIcon(r3, r5, r4, r9);
            goto L_0x0912;
        L_0x05c1:
            r3 = org.telegram.messenger.LocaleController.getString(r6, r5);
            r5 = new java.lang.Object[r10];
            r6 = org.telegram.ui.ProfileActivity.this;
            r6 = r6.chatInfo;
            r6 = r6.participants_count;
            r6 = java.lang.Integer.valueOf(r6);
            r5[r9] = r6;
            r5 = java.lang.String.format(r8, r5);
            r6 = org.telegram.ui.ProfileActivity.this;
            r6 = r6.membersSectionRow;
            r6 = r6 - r10;
            if (r2 == r6) goto L_0x05e3;
        L_0x05e2:
            r9 = 1;
        L_0x05e3:
            r1.setTextAndValueAndIcon(r3, r5, r4, r9);
            goto L_0x0912;
        L_0x05e8:
            r3 = org.telegram.ui.ProfileActivity.this;
            r3 = r3.currentChat;
            r3 = org.telegram.messenger.ChatObject.isChannel(r3);
            if (r3 == 0) goto L_0x0616;
        L_0x05f4:
            r3 = org.telegram.ui.ProfileActivity.this;
            r3 = r3.currentChat;
            r3 = r3.megagroup;
            if (r3 != 0) goto L_0x0616;
        L_0x05fe:
            r3 = NUM; // 0x7f0e0293 float:1.8876374E38 double:1.053162482E-314;
            r5 = "ChannelSubscribers";
            r3 = org.telegram.messenger.LocaleController.getString(r5, r3);
            r5 = org.telegram.ui.ProfileActivity.this;
            r5 = r5.membersSectionRow;
            r5 = r5 - r10;
            if (r2 == r5) goto L_0x0611;
        L_0x0610:
            r9 = 1;
        L_0x0611:
            r1.setTextAndIcon(r3, r4, r9);
            goto L_0x0912;
        L_0x0616:
            r3 = org.telegram.messenger.LocaleController.getString(r6, r5);
            r5 = org.telegram.ui.ProfileActivity.this;
            r5 = r5.membersSectionRow;
            r5 = r5 - r10;
            if (r2 == r5) goto L_0x0624;
        L_0x0623:
            r9 = 1;
        L_0x0624:
            r1.setTextAndIcon(r3, r4, r9);
            goto L_0x0912;
        L_0x0629:
            r3 = org.telegram.ui.ProfileActivity.this;
            r3 = r3.administratorsRow;
            if (r2 != r3) goto L_0x0683;
        L_0x0631:
            r3 = org.telegram.ui.ProfileActivity.this;
            r3 = r3.chatInfo;
            if (r3 == 0) goto L_0x0668;
        L_0x0639:
            r3 = NUM; // 0x7f0e0244 float:1.8876214E38 double:1.053162443E-314;
            r4 = "ChannelAdministrators";
            r3 = org.telegram.messenger.LocaleController.getString(r4, r3);
            r4 = new java.lang.Object[r10];
            r5 = org.telegram.ui.ProfileActivity.this;
            r5 = r5.chatInfo;
            r5 = r5.admins_count;
            r5 = java.lang.Integer.valueOf(r5);
            r4[r9] = r5;
            r4 = java.lang.String.format(r8, r4);
            r5 = NUM; // 0x7var_f float:1.7944706E38 double:1.052935534E-314;
            r6 = org.telegram.ui.ProfileActivity.this;
            r6 = r6.membersSectionRow;
            r6 = r6 - r10;
            if (r2 == r6) goto L_0x0663;
        L_0x0662:
            r9 = 1;
        L_0x0663:
            r1.setTextAndValueAndIcon(r3, r4, r5, r9);
            goto L_0x0912;
        L_0x0668:
            r3 = NUM; // 0x7f0e0244 float:1.8876214E38 double:1.053162443E-314;
            r4 = "ChannelAdministrators";
            r3 = org.telegram.messenger.LocaleController.getString(r4, r3);
            r4 = NUM; // 0x7var_f float:1.7944706E38 double:1.052935534E-314;
            r5 = org.telegram.ui.ProfileActivity.this;
            r5 = r5.membersSectionRow;
            r5 = r5 - r10;
            if (r2 == r5) goto L_0x067e;
        L_0x067d:
            r9 = 1;
        L_0x067e:
            r1.setTextAndIcon(r3, r4, r9);
            goto L_0x0912;
        L_0x0683:
            r3 = org.telegram.ui.ProfileActivity.this;
            r3 = r3.blockedUsersRow;
            if (r2 != r3) goto L_0x06e9;
        L_0x068b:
            r3 = org.telegram.ui.ProfileActivity.this;
            r3 = r3.chatInfo;
            if (r3 == 0) goto L_0x06ce;
        L_0x0693:
            r3 = NUM; // 0x7f0e0249 float:1.8876224E38 double:1.0531624457E-314;
            r4 = "ChannelBlacklist";
            r3 = org.telegram.messenger.LocaleController.getString(r4, r3);
            r4 = new java.lang.Object[r10];
            r5 = org.telegram.ui.ProfileActivity.this;
            r5 = r5.chatInfo;
            r5 = r5.banned_count;
            r6 = org.telegram.ui.ProfileActivity.this;
            r6 = r6.chatInfo;
            r6 = r6.kicked_count;
            r5 = java.lang.Math.max(r5, r6);
            r5 = java.lang.Integer.valueOf(r5);
            r4[r9] = r5;
            r4 = java.lang.String.format(r8, r4);
            r5 = NUM; // 0x7var_ float:1.7944714E38 double:1.052935536E-314;
            r6 = org.telegram.ui.ProfileActivity.this;
            r6 = r6.membersSectionRow;
            r6 = r6 - r10;
            if (r2 == r6) goto L_0x06c9;
        L_0x06c8:
            r9 = 1;
        L_0x06c9:
            r1.setTextAndValueAndIcon(r3, r4, r5, r9);
            goto L_0x0912;
        L_0x06ce:
            r3 = NUM; // 0x7f0e0249 float:1.8876224E38 double:1.0531624457E-314;
            r4 = "ChannelBlacklist";
            r3 = org.telegram.messenger.LocaleController.getString(r4, r3);
            r4 = NUM; // 0x7var_ float:1.7944714E38 double:1.052935536E-314;
            r5 = org.telegram.ui.ProfileActivity.this;
            r5 = r5.membersSectionRow;
            r5 = r5 - r10;
            if (r2 == r5) goto L_0x06e4;
        L_0x06e3:
            r9 = 1;
        L_0x06e4:
            r1.setTextAndIcon(r3, r4, r9);
            goto L_0x0912;
        L_0x06e9:
            r3 = org.telegram.ui.ProfileActivity.this;
            r3 = r3.addMemberRow;
            if (r2 != r3) goto L_0x0912;
        L_0x06f1:
            r2 = "windowBackgroundWhiteBlueIcon";
            r3 = "windowBackgroundWhiteBlueButton";
            r1.setColors(r2, r3);
            r2 = org.telegram.ui.ProfileActivity.this;
            r2 = r2.chat_id;
            if (r2 <= 0) goto L_0x0713;
        L_0x0702:
            r2 = NUM; // 0x7f0e00b6 float:1.8875407E38 double:1.0531622466E-314;
            r3 = "AddMember";
            r2 = org.telegram.messenger.LocaleController.getString(r3, r2);
            r3 = NUM; // 0x7var_ float:1.7944708E38 double:1.0529355346E-314;
            r1.setTextAndIcon(r2, r3, r10);
            goto L_0x0912;
        L_0x0713:
            r2 = NUM; // 0x7f0e00c0 float:1.8875427E38 double:1.0531622515E-314;
            r3 = "AddRecipient";
            r2 = org.telegram.messenger.LocaleController.getString(r3, r2);
            r3 = NUM; // 0x7var_ float:1.7944708E38 double:1.0529355346E-314;
            r1.setTextAndIcon(r2, r3, r10);
            goto L_0x0912;
        L_0x0724:
            r1 = r1.itemView;
            r1 = (org.telegram.ui.Cells.AboutLinkCell) r1;
            r3 = org.telegram.ui.ProfileActivity.this;
            r3 = r3.userInfoRow;
            if (r2 != r3) goto L_0x074c;
        L_0x0730:
            r2 = org.telegram.ui.ProfileActivity.this;
            r2 = r2.userInfo;
            r2 = r2.about;
            r3 = NUM; // 0x7f0e0ba1 float:1.8881076E38 double:1.0531636275E-314;
            r4 = "UserBio";
            r3 = org.telegram.messenger.LocaleController.getString(r4, r3);
            r4 = org.telegram.ui.ProfileActivity.this;
            r4 = r4.isBot;
            r1.setTextAndValue(r2, r3, r4);
            goto L_0x0912;
        L_0x074c:
            r3 = org.telegram.ui.ProfileActivity.this;
            r3 = r3.channelInfoRow;
            if (r2 != r3) goto L_0x0912;
        L_0x0754:
            r2 = org.telegram.ui.ProfileActivity.this;
            r2 = r2.chatInfo;
            r2 = r2.about;
        L_0x075c:
            r3 = "\n\n\n";
            r4 = r2.contains(r3);
            if (r4 == 0) goto L_0x076b;
        L_0x0764:
            r4 = "\n\n";
            r2 = r2.replace(r3, r4);
            goto L_0x075c;
        L_0x076b:
            r1.setText(r2, r10);
            goto L_0x0912;
        L_0x0770:
            r1 = r1.itemView;
            r1 = (org.telegram.ui.Cells.TextDetailCell) r1;
            r3 = org.telegram.ui.ProfileActivity.this;
            r3 = r3.phoneRow;
            if (r2 != r3) goto L_0x07cf;
        L_0x077c:
            r2 = org.telegram.ui.ProfileActivity.this;
            r2 = r2.currentAccount;
            r2 = org.telegram.messenger.MessagesController.getInstance(r2);
            r3 = org.telegram.ui.ProfileActivity.this;
            r3 = r3.user_id;
            r3 = java.lang.Integer.valueOf(r3);
            r2 = r2.getUser(r3);
            r3 = r2.phone;
            r3 = android.text.TextUtils.isEmpty(r3);
            if (r3 != 0) goto L_0x07b8;
        L_0x079c:
            r3 = org.telegram.PhoneFormat.PhoneFormat.getInstance();
            r4 = new java.lang.StringBuilder;
            r4.<init>();
            r5 = "+";
            r4.append(r5);
            r2 = r2.phone;
            r4.append(r2);
            r2 = r4.toString();
            r2 = r3.format(r2);
            goto L_0x07c1;
        L_0x07b8:
            r2 = NUM; // 0x7f0e08c8 float:1.8879597E38 double:1.0531632673E-314;
            r3 = "PhoneHidden";
            r2 = org.telegram.messenger.LocaleController.getString(r3, r2);
        L_0x07c1:
            r3 = NUM; // 0x7f0e08cb float:1.8879603E38 double:1.053163269E-314;
            r4 = "PhoneMobile";
            r3 = org.telegram.messenger.LocaleController.getString(r4, r3);
            r1.setTextAndValue(r2, r3, r9);
            goto L_0x0912;
        L_0x07cf:
            r3 = org.telegram.ui.ProfileActivity.this;
            r3 = r3.usernameRow;
            if (r2 != r3) goto L_0x0875;
        L_0x07d7:
            r2 = org.telegram.ui.ProfileActivity.this;
            r2 = r2.user_id;
            if (r2 == 0) goto L_0x0825;
        L_0x07df:
            r2 = org.telegram.ui.ProfileActivity.this;
            r2 = r2.currentAccount;
            r2 = org.telegram.messenger.MessagesController.getInstance(r2);
            r3 = org.telegram.ui.ProfileActivity.this;
            r3 = r3.user_id;
            r3 = java.lang.Integer.valueOf(r3);
            r2 = r2.getUser(r3);
            if (r2 == 0) goto L_0x0815;
        L_0x07f9:
            r3 = r2.username;
            r3 = android.text.TextUtils.isEmpty(r3);
            if (r3 != 0) goto L_0x0815;
        L_0x0801:
            r3 = new java.lang.StringBuilder;
            r3.<init>();
            r4 = "@";
            r3.append(r4);
            r2 = r2.username;
            r3.append(r2);
            r2 = r3.toString();
            goto L_0x0817;
        L_0x0815:
            r2 = "-";
        L_0x0817:
            r3 = NUM; // 0x7f0e0bc5 float:1.8881149E38 double:1.0531636453E-314;
            r4 = "Username";
            r3 = org.telegram.messenger.LocaleController.getString(r4, r3);
            r1.setTextAndValue(r2, r3, r9);
            goto L_0x0912;
        L_0x0825:
            r2 = org.telegram.ui.ProfileActivity.this;
            r2 = r2.currentChat;
            if (r2 == 0) goto L_0x0912;
        L_0x082d:
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
            r3 = NUM; // 0x7f0e0591 float:1.8877928E38 double:1.0531628607E-314;
            r4 = "InviteLink";
            r3 = org.telegram.messenger.LocaleController.getString(r4, r3);
            r1.setTextAndValue(r2, r3, r9);
            goto L_0x0912;
        L_0x0875:
            r3 = org.telegram.ui.ProfileActivity.this;
            r3 = r3.locationRow;
            if (r2 != r3) goto L_0x0912;
        L_0x087d:
            r2 = org.telegram.ui.ProfileActivity.this;
            r2 = r2.chatInfo;
            if (r2 == 0) goto L_0x0912;
        L_0x0885:
            r2 = org.telegram.ui.ProfileActivity.this;
            r2 = r2.chatInfo;
            r2 = r2.location;
            r2 = r2 instanceof org.telegram.tgnet.TLRPC.TL_channelLocation;
            if (r2 == 0) goto L_0x0912;
        L_0x0891:
            r2 = org.telegram.ui.ProfileActivity.this;
            r2 = r2.chatInfo;
            r2 = r2.location;
            r2 = (org.telegram.tgnet.TLRPC.TL_channelLocation) r2;
            r2 = r2.address;
            r3 = NUM; // 0x7f0e015e float:1.8875747E38 double:1.0531623296E-314;
            r4 = "AttachLocation";
            r3 = org.telegram.messenger.LocaleController.getString(r4, r3);
            r1.setTextAndValue(r2, r3, r9);
            goto L_0x0912;
        L_0x08aa:
            r1 = r1.itemView;
            r1 = (org.telegram.ui.Cells.HeaderCell) r1;
            r3 = org.telegram.ui.ProfileActivity.this;
            r3 = r3.infoHeaderRow;
            if (r2 != r3) goto L_0x08ee;
        L_0x08b6:
            r2 = org.telegram.ui.ProfileActivity.this;
            r2 = r2.currentChat;
            r2 = org.telegram.messenger.ChatObject.isChannel(r2);
            if (r2 == 0) goto L_0x08e1;
        L_0x08c2:
            r2 = org.telegram.ui.ProfileActivity.this;
            r2 = r2.currentChat;
            r2 = r2.megagroup;
            if (r2 != 0) goto L_0x08e1;
        L_0x08cc:
            r2 = org.telegram.ui.ProfileActivity.this;
            r2 = r2.channelInfoRow;
            if (r2 == r8) goto L_0x08e1;
        L_0x08d4:
            r2 = NUM; // 0x7f0e0982 float:1.8879974E38 double:1.053163359E-314;
            r3 = "ReportChatDescription";
            r2 = org.telegram.messenger.LocaleController.getString(r3, r2);
            r1.setText(r2);
            goto L_0x0912;
        L_0x08e1:
            r2 = NUM; // 0x7f0e0582 float:1.8877897E38 double:1.0531628533E-314;
            r3 = "Info";
            r2 = org.telegram.messenger.LocaleController.getString(r3, r2);
            r1.setText(r2);
            goto L_0x0912;
        L_0x08ee:
            r3 = org.telegram.ui.ProfileActivity.this;
            r3 = r3.sharedHeaderRow;
            if (r2 != r3) goto L_0x0903;
        L_0x08f6:
            r2 = NUM; // 0x7f0e0a66 float:1.8880437E38 double:1.053163472E-314;
            r3 = "SharedContent";
            r2 = org.telegram.messenger.LocaleController.getString(r3, r2);
            r1.setText(r2);
            goto L_0x0912;
        L_0x0903:
            r3 = org.telegram.ui.ProfileActivity.this;
            r3 = r3.membersHeaderRow;
            if (r2 != r3) goto L_0x0912;
        L_0x090b:
            r2 = org.telegram.messenger.LocaleController.getString(r6, r5);
            r1.setText(r2);
        L_0x0912:
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ProfileActivity$ListAdapter.onBindViewHolder(androidx.recyclerview.widget.RecyclerView$ViewHolder, int):void");
        }

        public boolean isEnabled(ViewHolder viewHolder) {
            int itemViewType = viewHolder.getItemViewType();
            return (itemViewType == 1 || itemViewType == 5 || itemViewType == 7 || itemViewType == 9 || itemViewType == 10 || itemViewType == 11 || itemViewType == 12) ? false : true;
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
            if (i >= ProfileActivity.this.membersStartRow && i < ProfileActivity.this.membersEndRow) {
                return 8;
            }
            if (i == ProfileActivity.this.emptyRow) {
                return 11;
            }
            return i == ProfileActivity.this.bottomPaddingRow ? 12 : 0;
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
            if (this.chatInfo == null) {
                this.chatInfo = getMessagesController().getChatFull(this.chat_id);
            }
            if (ChatObject.isChannel(this.currentChat)) {
                MessagesController.getInstance(this.currentAccount).loadFullChat(this.chat_id, this.classGuid, true);
            } else if (this.chatInfo == null) {
                this.chatInfo = getMessagesStorage().loadChatInfo(this.chat_id, null, false, false);
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
                NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiDidLoad);
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
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiDidLoad);
        ProfileGalleryView profileGalleryView = this.avatarsViewPager;
        if (profileGalleryView != null) {
            profileGalleryView.onDestroy();
        }
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
        boolean z = false;
        anonymousClass2.setBackgroundColor(0);
        int i = (this.user_id != 0 || (ChatObject.isChannel(this.chat_id, this.currentAccount) && !this.currentChat.megagroup)) ? 5 : this.chat_id;
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
        FrameLayout frameLayout;
        Context context2 = context;
        Theme.createProfileResources(context);
        this.hasOwnBackground = true;
        this.extraHeight = AndroidUtilities.dpf2(88.0f);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                int i2 = i;
                if (ProfileActivity.this.getParentActivity() != null) {
                    if (i2 == -1) {
                        ProfileActivity.this.finishFragment();
                    } else {
                        String str = "dialogTextRed2";
                        String str2 = "Cancel";
                        CharSequence charSequence = null;
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
                                    SendMessagesHelper.getInstance(ProfileActivity.this.currentAccount).sendMessage("/start", (long) ProfileActivity.this.user_id, null, null, false, null, null, null, true, 0);
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
                                bundle2.putString("selectAlertString", LocaleController.getString("SendContactToText", NUM));
                                bundle2.putString("selectAlertStringGroup", LocaleController.getString("SendContactToGroupText", NUM));
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
                                        bundle.putString("addToGroupAlertString", LocaleController.formatString("AddToTheGroupAlertText", NUM, UserObject.getUserName(user2), "%1$s"));
                                        DialogsActivity dialogsActivity2 = new DialogsActivity(bundle);
                                        dialogsActivity2.setDelegate(new -$$Lambda$ProfileActivity$3$fjMhx-P1BMqbwH45pN5jgfN8nNI(this, user2));
                                        ProfileActivity.this.presentFragment(dialogsActivity2);
                                    }
                                } else if (i2 == 10) {
                                    try {
                                        String str5 = "https://";
                                        str = "/%s";
                                        StringBuilder stringBuilder;
                                        StringBuilder stringBuilder2;
                                        if (ProfileActivity.this.user_id != 0) {
                                            if (MessagesController.getInstance(ProfileActivity.this.currentAccount).getUser(Integer.valueOf(ProfileActivity.this.user_id)) != null) {
                                                String format;
                                                if (ProfileActivity.this.botInfo == null || ProfileActivity.this.userInfo == null || TextUtils.isEmpty(ProfileActivity.this.userInfo.about)) {
                                                    stringBuilder = new StringBuilder();
                                                    stringBuilder.append(str5);
                                                    stringBuilder.append(MessagesController.getInstance(ProfileActivity.this.currentAccount).linkPrefix);
                                                    stringBuilder.append(str);
                                                    format = String.format(stringBuilder.toString(), new Object[]{user2.username});
                                                } else {
                                                    stringBuilder2 = new StringBuilder();
                                                    stringBuilder2.append("%s https://");
                                                    stringBuilder2.append(MessagesController.getInstance(ProfileActivity.this.currentAccount).linkPrefix);
                                                    stringBuilder2.append(str);
                                                    format = String.format(stringBuilder2.toString(), new Object[]{ProfileActivity.this.userInfo.about, user2.username});
                                                }
                                                charSequence = format;
                                            } else {
                                                return;
                                            }
                                        } else if (ProfileActivity.this.chat_id != 0) {
                                            if (MessagesController.getInstance(ProfileActivity.this.currentAccount).getChat(Integer.valueOf(ProfileActivity.this.chat_id)) != null) {
                                                if (ProfileActivity.this.chatInfo == null || TextUtils.isEmpty(ProfileActivity.this.chatInfo.about)) {
                                                    stringBuilder = new StringBuilder();
                                                    stringBuilder.append(str5);
                                                    stringBuilder.append(MessagesController.getInstance(ProfileActivity.this.currentAccount).linkPrefix);
                                                    stringBuilder.append(str);
                                                    charSequence = String.format(stringBuilder.toString(), new Object[]{r0.username});
                                                } else {
                                                    stringBuilder2 = new StringBuilder();
                                                    stringBuilder2.append("%s\nhttps://");
                                                    stringBuilder2.append(MessagesController.getInstance(ProfileActivity.this.currentAccount).linkPrefix);
                                                    stringBuilder2.append(str);
                                                    charSequence = String.format(stringBuilder2.toString(), new Object[]{ProfileActivity.this.chatInfo.about, r0.username});
                                                }
                                            } else {
                                                return;
                                            }
                                        }
                                        if (!TextUtils.isEmpty(charSequence)) {
                                            Intent intent = new Intent("android.intent.action.SEND");
                                            intent.setType("text/plain");
                                            intent.putExtra("android.intent.extra.TEXT", charSequence);
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
            private boolean firstLayout = true;
            private boolean ignoreLayout;

            public boolean hasOverlappingRendering() {
                return false;
            }

            /* Access modifiers changed, original: protected */
            public void onMeasure(int i, int i2) {
                Object obj;
                int itemCount;
                int makeMeasureSpec;
                int i3;
                int currentActionBarHeight = (ProfileActivity.this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0) + ActionBar.getCurrentActionBarHeight();
                if (ProfileActivity.this.listView != null) {
                    FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) ProfileActivity.this.listView.getLayoutParams();
                    if (layoutParams.topMargin != currentActionBarHeight) {
                        layoutParams.topMargin = currentActionBarHeight;
                    }
                }
                super.onMeasure(i, i2);
                int currentActionBarHeight2 = ActionBar.getCurrentActionBarHeight() + (ProfileActivity.this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0);
                if (ProfileActivity.this.lastMeasuredContentWidth != getMeasuredWidth()) {
                    obj = ProfileActivity.this.lastMeasuredContentWidth != 0 ? 1 : null;
                    ProfileActivity.this.listContentHeight = 0;
                    itemCount = ProfileActivity.this.listAdapter.getItemCount();
                    ProfileActivity.this.lastMeasuredContentWidth = getMeasuredWidth();
                    makeMeasureSpec = MeasureSpec.makeMeasureSpec(getMeasuredWidth(), NUM);
                    int makeMeasureSpec2 = MeasureSpec.makeMeasureSpec(0, 0);
                    for (i3 = 0; i3 < itemCount; i3++) {
                        ViewHolder createViewHolder = ProfileActivity.this.listAdapter.createViewHolder(null, ProfileActivity.this.listAdapter.getItemViewType(i3));
                        ProfileActivity.this.listAdapter.onBindViewHolder(createViewHolder, i3);
                        createViewHolder.itemView.measure(makeMeasureSpec, makeMeasureSpec2);
                        ProfileActivity profileActivity = ProfileActivity.this;
                        profileActivity.listContentHeight = profileActivity.listContentHeight + createViewHolder.itemView.getMeasuredHeight();
                        if ((ProfileActivity.this.listContentHeight + currentActionBarHeight2) + AndroidUtilities.dp(88.0f) >= ProfileActivity.this.fragmentView.getMeasuredHeight()) {
                            break;
                        }
                    }
                } else {
                    obj = null;
                }
                if (!ProfileActivity.this.openAnimationInProgress && !this.firstLayout) {
                    this.ignoreLayout = true;
                    if (ProfileActivity.this.isInLandscapeMode) {
                        itemCount = AndroidUtilities.dp(88.0f);
                        currentActionBarHeight2 = 0;
                    } else {
                        itemCount = ProfileActivity.this.listView.getMeasuredWidth() - currentActionBarHeight2;
                        currentActionBarHeight2 = Math.max(0, ProfileActivity.this.fragmentView.getMeasuredHeight() - ((ProfileActivity.this.listContentHeight + currentActionBarHeight2) + AndroidUtilities.dp(88.0f)));
                    }
                    if (ProfileActivity.this.banFromGroup != 0) {
                        currentActionBarHeight2 += AndroidUtilities.dp(48.0f);
                        ProfileActivity.this.listView.setBottomGlowOffset(AndroidUtilities.dp(48.0f));
                    } else {
                        ProfileActivity.this.listView.setBottomGlowOffset(0);
                    }
                    int paddingTop = ProfileActivity.this.listView.getPaddingTop();
                    View childAt = ProfileActivity.this.listView.getChildAt(0);
                    if (childAt != null) {
                        ViewHolder findContainingViewHolder = ProfileActivity.this.listView.findContainingViewHolder(childAt);
                        int adapterPosition = findContainingViewHolder.getAdapterPosition();
                        i3 = adapterPosition == -1 ? findContainingViewHolder.getPosition() : adapterPosition;
                        makeMeasureSpec = childAt.getTop();
                    } else {
                        makeMeasureSpec = 0;
                        i3 = -1;
                    }
                    if (obj != null || i3 == -1) {
                        obj = null;
                    } else {
                        ProfileActivity.this.layoutManager.scrollToPositionWithOffset(i3, makeMeasureSpec - itemCount);
                        obj = 1;
                    }
                    if (!(paddingTop == itemCount && ProfileActivity.this.listView.getPaddingBottom() == currentActionBarHeight2)) {
                        ProfileActivity.this.listView.setPadding(0, itemCount, 0, currentActionBarHeight2);
                        obj = 1;
                    }
                    if (obj != null) {
                        measureChildWithMargins(ProfileActivity.this.listView, i, 0, i2, 0);
                        ProfileActivity.this.listView.layout(0, currentActionBarHeight, ProfileActivity.this.listView.getMeasuredWidth(), ProfileActivity.this.listView.getMeasuredHeight() + currentActionBarHeight);
                    }
                    this.ignoreLayout = false;
                }
            }

            /* Access modifiers changed, original: protected */
            public void onLayout(boolean z, int i, int i2, int i3, int i4) {
                super.onLayout(z, i, i2, i3, i4);
                this.firstLayout = false;
                ProfileActivity.this.checkListViewScroll();
            }

            public void requestLayout() {
                if (!this.ignoreLayout) {
                    super.requestLayout();
                }
            }
        };
        FrameLayout frameLayout2 = (FrameLayout) this.fragmentView;
        this.listView = new RecyclerListView(context2) {
            private final Paint paint = new Paint();
            private VelocityTracker velocityTracker;

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

            public boolean onTouchEvent(MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                VelocityTracker velocityTracker;
                if (action == 0) {
                    velocityTracker = this.velocityTracker;
                    if (velocityTracker == null) {
                        this.velocityTracker = VelocityTracker.obtain();
                    } else {
                        velocityTracker.clear();
                    }
                    this.velocityTracker.addMovement(motionEvent);
                } else if (action == 2) {
                    velocityTracker = this.velocityTracker;
                    if (velocityTracker != null) {
                        velocityTracker.addMovement(motionEvent);
                        this.velocityTracker.computeCurrentVelocity(1000);
                        ProfileActivity.this.listViewVelocityY = this.velocityTracker.getYVelocity(motionEvent.getPointerId(motionEvent.getActionIndex()));
                    }
                } else if (action == 1 || action == 3) {
                    velocityTracker = this.velocityTracker;
                    if (velocityTracker != null) {
                        velocityTracker.recycle();
                        this.velocityTracker = null;
                    }
                }
                boolean onTouchEvent = super.onTouchEvent(motionEvent);
                if ((action == 1 || action == 3) && ProfileActivity.this.allowPullingDown) {
                    View findViewByPosition = ProfileActivity.this.layoutManager.findViewByPosition(0);
                    if (findViewByPosition != null) {
                        if (ProfileActivity.this.isPulledDown) {
                            ProfileActivity.this.listView.smoothScrollBy(0, (findViewByPosition.getTop() - ProfileActivity.this.listView.getMeasuredWidth()) + (ActionBar.getCurrentActionBarHeight() + (ProfileActivity.this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0)), CubicBezierInterpolator.EASE_OUT_QUINT);
                        } else {
                            ProfileActivity.this.listView.smoothScrollBy(0, findViewByPosition.getTop() - AndroidUtilities.dp(88.0f), CubicBezierInterpolator.EASE_OUT_QUINT);
                        }
                    }
                }
                return onTouchEvent;
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

            public int scrollVerticallyBy(int i, Recycler recycler, State state) {
                View findViewByPosition = ProfileActivity.this.layoutManager.findViewByPosition(0);
                if (findViewByPosition != null) {
                    int top = findViewByPosition.getTop() - AndroidUtilities.dp(88.0f);
                    if (ProfileActivity.this.allowPullingDown || top <= i) {
                        if (ProfileActivity.this.allowPullingDown) {
                            if (i >= top) {
                                ProfileActivity.this.allowPullingDown = false;
                            } else if (ProfileActivity.this.listView.getScrollState() == 1 && !ProfileActivity.this.isPulledDown) {
                                i /= 2;
                            }
                        }
                    } else if (ProfileActivity.this.avatarsViewPager.hasImages() && ProfileActivity.this.avatarImage.getImageReceiver().hasNotThumb() && !ProfileActivity.this.isInLandscapeMode && !AndroidUtilities.isTablet()) {
                        ProfileActivity.this.allowPullingDown = true;
                    }
                    i = top;
                }
                return super.scrollVerticallyBy(i, recycler, state);
            }
        };
        this.layoutManager.setOrientation(1);
        this.listView.setLayoutManager(this.layoutManager);
        RecyclerListView recyclerListView = this.listView;
        int i = (this.user_id != 0 || (ChatObject.isChannel(this.chat_id, this.currentAccount) && !this.currentChat.megagroup)) ? 5 : this.chat_id;
        recyclerListView.setGlowColor(AvatarDrawable.getProfileBackColorForId(i));
        frameLayout2.addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
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
            frameLayout2.addView(anonymousClass7, LayoutHelper.createFrame(-1, 51, 83));
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
        String str2 = "avatar_backgroundActionBarBlue";
        this.topView.setBackgroundColor(Theme.getColor(str2));
        frameLayout2.addView(this.topView);
        this.avatarImage = new AvatarImageView(context2);
        this.avatarImage.setRoundRadius(AndroidUtilities.dp(21.0f));
        this.avatarImage.setPivotX(0.0f);
        this.avatarImage.setPivotY(0.0f);
        frameLayout2.addView(this.avatarImage, LayoutHelper.createFrame(42, 42.0f, 51, 64.0f, 0.0f, 0.0f, 0.0f));
        this.avatarImage.setOnClickListener(new -$$Lambda$ProfileActivity$x_zxLVJHDncW-LPz2s41OV-XOro(this));
        this.avatarImage.setContentDescription(LocaleController.getString("AccDescrProfilePicture", NUM));
        ProfileGalleryView profileGalleryView = this.avatarsViewPager;
        if (profileGalleryView != null) {
            profileGalleryView.onDestroy();
        }
        int i2 = this.user_id;
        if (i2 == 0) {
            i2 = -this.chat_id;
        }
        ProfileGalleryView profileGalleryView2 = profileGalleryView;
        profileGalleryView = new ProfileGalleryView(context, (long) i2, this.actionBar, this.listView, this.avatarImage, getClassGuid());
        this.avatarsViewPager = profileGalleryView2;
        frameLayout2.addView(this.avatarsViewPager);
        this.overlaysView = new OverlaysView(context2);
        frameLayout2.addView(this.overlaysView);
        this.avatarsViewPagerIndicatorView = new PagerIndicatorView(context2);
        frameLayout2.addView(this.avatarsViewPagerIndicatorView, LayoutHelper.createFrame(-1, -1.0f));
        frameLayout2.addView(this.actionBar);
        i2 = 0;
        while (i2 < 2) {
            if (this.playProfileAnimation || i2 != 0) {
                this.nameTextView[i2] = new SimpleTextView(context2);
                if (i2 == 1) {
                    this.nameTextView[i2].setTextColor(Theme.getColor("profile_title"));
                } else {
                    this.nameTextView[i2].setTextColor(Theme.getColor("actionBarDefaultTitle"));
                }
                this.nameTextView[i2].setTextSize(18);
                this.nameTextView[i2].setGravity(3);
                this.nameTextView[i2].setTypeface(AndroidUtilities.getTypeface(str));
                this.nameTextView[i2].setLeftDrawableTopPadding(-AndroidUtilities.dp(1.3f));
                this.nameTextView[i2].setPivotX(0.0f);
                this.nameTextView[i2].setPivotY(0.0f);
                this.nameTextView[i2].setAlpha(i2 == 0 ? 0.0f : 1.0f);
                if (i2 == 1) {
                    this.nameTextView[i2].setScrollNonFitText(true);
                    this.nameTextView[i2].setBackgroundColor(Theme.getColor(str2));
                }
                frameLayout2.addView(this.nameTextView[i2], LayoutHelper.createFrame(-2, -2.0f, 51, 118.0f, 0.0f, i2 == 0 ? 48.0f : 0.0f, 0.0f));
                this.onlineTextView[i2] = new SimpleTextView(context2);
                this.onlineTextView[i2].setTextColor(Theme.getColor("avatar_subtitleInProfileBlue"));
                this.onlineTextView[i2].setTextSize(14);
                this.onlineTextView[i2].setGravity(3);
                this.onlineTextView[i2].setAlpha(i2 == 0 ? 0.0f : 1.0f);
                frameLayout2.addView(this.onlineTextView[i2], LayoutHelper.createFrame(-2, -2.0f, 51, 118.0f, 0.0f, i2 == 0 ? 48.0f : 8.0f, 0.0f));
            }
            i2++;
        }
        updateProfileData();
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
                frameLayout = frameLayout2;
                stateListAnimator.addState(new int[]{16842919}, ObjectAnimator.ofFloat(this.writeButton, View.TRANSLATION_Z, new float[]{(float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(4.0f)}).setDuration(200));
                stateListAnimator.addState(new int[0], ObjectAnimator.ofFloat(this.writeButton, View.TRANSLATION_Z, new float[]{(float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(2.0f)}).setDuration(200));
                this.writeButton.setStateListAnimator(stateListAnimator);
                this.writeButton.setOutlineProvider(new ViewOutlineProvider() {
                    @SuppressLint({"NewApi"})
                    public void getOutline(View view, Outline outline) {
                        outline.setOval(0, 0, AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
                    }
                });
            } else {
                frameLayout = frameLayout2;
            }
            frameLayout.addView(this.writeButton, LayoutHelper.createFrame(VERSION.SDK_INT >= 21 ? 56 : 60, VERSION.SDK_INT >= 21 ? 56.0f : 60.0f, 53, 0.0f, 0.0f, 16.0f, 0.0f));
            this.writeButton.setOnClickListener(new -$$Lambda$ProfileActivity$MnD_12oukmBdwxWh9wauwLpeZDg(this));
        } else {
            frameLayout = frameLayout2;
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
            builder.setPositiveButton(LocaleController.getString("OK", NUM), new -$$Lambda$ProfileActivity$LG5g4oO2GJkr4RZqZ2poVAp7tys(this, channelParticipant, intValue, user, chatParticipant));
            builder.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
            showDialog(builder.create());
        } else if (channelParticipant != null) {
            openRightsEdit(intValue, user.id, chatParticipant, channelParticipant.admin_rights, channelParticipant.banned_rights, channelParticipant.rank);
        } else {
            openRightsEdit(intValue, user.id, chatParticipant, null, null, "");
        }
    }

    public /* synthetic */ void lambda$null$4$ProfileActivity(ChannelParticipant channelParticipant, int i, User user, ChatParticipant chatParticipant, DialogInterface dialogInterface, int i2) {
        ChannelParticipant channelParticipant2 = channelParticipant;
        User user2 = user;
        if (channelParticipant2 != null) {
            openRightsEdit(i, user2.id, chatParticipant, channelParticipant2.admin_rights, channelParticipant2.banned_rights, channelParticipant2.rank);
            return;
        }
        openRightsEdit(i, user2.id, chatParticipant, null, null, "");
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
        ChatRightsEditActivity chatRightsEditActivity = new ChatRightsEditActivity(i, i2, null, tL_chatBannedRights, channelParticipant != null ? channelParticipant.banned_rights : null, "", 1, true, false);
        chatRightsEditActivity.setDelegate(new ChatRightsEditActivityDelegate() {
            public void didSetRights(int i, TL_chatAdminRights tL_chatAdminRights, TL_chatBannedRights tL_chatBannedRights, String str) {
                ProfileActivity.this.removeSelfFromStack();
            }

            public void didChangeOwner(User user) {
                ProfileActivity.this.undoView.showWithAction((long) (-ProfileActivity.this.chat_id), ProfileActivity.this.currentChat.megagroup ? 10 : 9, (Object) user);
            }
        });
        presentFragment(chatRightsEditActivity);
    }

    public /* synthetic */ void lambda$createView$10$ProfileActivity(View view) {
        if (this.listView.getScrollState() == 1) {
            return;
        }
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

    private void openRightsEdit(final int i, int i2, final ChatParticipant chatParticipant, TL_chatAdminRights tL_chatAdminRights, TL_chatBannedRights tL_chatBannedRights, String str) {
        ChatRightsEditActivity chatRightsEditActivity = new ChatRightsEditActivity(i2, this.chat_id, tL_chatAdminRights, this.currentChat.default_banned_rights, tL_chatBannedRights, str, i, true, false);
        int i3 = i;
        ChatParticipant chatParticipant2 = chatParticipant;
        chatRightsEditActivity.setDelegate(new ChatRightsEditActivityDelegate() {
            /* JADX WARNING: Removed duplicated region for block: B:42:0x0120  */
            /* JADX WARNING: Removed duplicated region for block: B:62:? A:{SYNTHETIC, RETURN} */
            /* JADX WARNING: Removed duplicated region for block: B:47:0x014c  */
            public void didSetRights(int r4, org.telegram.tgnet.TLRPC.TL_chatAdminRights r5, org.telegram.tgnet.TLRPC.TL_chatBannedRights r6, java.lang.String r7) {
                /*
                r3 = this;
                r0 = r13;
                r1 = 1;
                if (r0 != 0) goto L_0x008c;
            L_0x0005:
                r0 = r15;
                r2 = r0 instanceof org.telegram.tgnet.TLRPC.TL_chatChannelParticipant;
                if (r2 == 0) goto L_0x004c;
            L_0x000b:
                r0 = (org.telegram.tgnet.TLRPC.TL_chatChannelParticipant) r0;
                if (r4 != r1) goto L_0x001f;
            L_0x000f:
                r4 = new org.telegram.tgnet.TLRPC$TL_channelParticipantAdmin;
                r4.<init>();
                r0.channelParticipant = r4;
                r4 = r0.channelParticipant;
                r1 = r4.flags;
                r1 = r1 | 4;
                r4.flags = r1;
                goto L_0x0026;
            L_0x001f:
                r4 = new org.telegram.tgnet.TLRPC$TL_channelParticipant;
                r4.<init>();
                r0.channelParticipant = r4;
            L_0x0026:
                r4 = r0.channelParticipant;
                r1 = org.telegram.ui.ProfileActivity.this;
                r1 = r1.currentAccount;
                r1 = org.telegram.messenger.UserConfig.getInstance(r1);
                r1 = r1.getClientUserId();
                r4.inviter_id = r1;
                r4 = r0.channelParticipant;
                r0 = r15;
                r1 = r0.user_id;
                r4.user_id = r1;
                r0 = r0.date;
                r4.date = r0;
                r4.banned_rights = r6;
                r4.admin_rights = r5;
                r4.rank = r7;
                goto L_0x015f;
            L_0x004c:
                r5 = r0 instanceof org.telegram.tgnet.TLRPC.ChatParticipant;
                if (r5 == 0) goto L_0x015f;
            L_0x0050:
                if (r4 != r1) goto L_0x0058;
            L_0x0052:
                r4 = new org.telegram.tgnet.TLRPC$TL_chatParticipantAdmin;
                r4.<init>();
                goto L_0x005d;
            L_0x0058:
                r4 = new org.telegram.tgnet.TLRPC$TL_chatParticipant;
                r4.<init>();
            L_0x005d:
                r5 = r15;
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
                r6 = r15;
                r5 = r5.indexOf(r6);
                if (r5 < 0) goto L_0x015f;
            L_0x007d:
                r6 = org.telegram.ui.ProfileActivity.this;
                r6 = r6.chatInfo;
                r6 = r6.participants;
                r6 = r6.participants;
                r6.set(r5, r4);
                goto L_0x015f;
            L_0x008c:
                if (r0 != r1) goto L_0x015f;
            L_0x008e:
                if (r4 != 0) goto L_0x015f;
            L_0x0090:
                r4 = org.telegram.ui.ProfileActivity.this;
                r4 = r4.currentChat;
                r4 = r4.megagroup;
                if (r4 == 0) goto L_0x015f;
            L_0x009a:
                r4 = org.telegram.ui.ProfileActivity.this;
                r4 = r4.chatInfo;
                if (r4 == 0) goto L_0x015f;
            L_0x00a2:
                r4 = org.telegram.ui.ProfileActivity.this;
                r4 = r4.chatInfo;
                r4 = r4.participants;
                if (r4 == 0) goto L_0x015f;
            L_0x00ac:
                r4 = 0;
                r5 = 0;
            L_0x00ae:
                r6 = org.telegram.ui.ProfileActivity.this;
                r6 = r6.chatInfo;
                r6 = r6.participants;
                r6 = r6.participants;
                r6 = r6.size();
                if (r5 >= r6) goto L_0x00fd;
            L_0x00be:
                r6 = org.telegram.ui.ProfileActivity.this;
                r6 = r6.chatInfo;
                r6 = r6.participants;
                r6 = r6.participants;
                r6 = r6.get(r5);
                r6 = (org.telegram.tgnet.TLRPC.TL_chatChannelParticipant) r6;
                r6 = r6.channelParticipant;
                r6 = r6.user_id;
                r7 = r15;
                r7 = r7.user_id;
                if (r6 != r7) goto L_0x00fa;
            L_0x00d8:
                r6 = org.telegram.ui.ProfileActivity.this;
                r6 = r6.chatInfo;
                if (r6 == 0) goto L_0x00eb;
            L_0x00e0:
                r6 = org.telegram.ui.ProfileActivity.this;
                r6 = r6.chatInfo;
                r7 = r6.participants_count;
                r7 = r7 - r1;
                r6.participants_count = r7;
            L_0x00eb:
                r6 = org.telegram.ui.ProfileActivity.this;
                r6 = r6.chatInfo;
                r6 = r6.participants;
                r6 = r6.participants;
                r6.remove(r5);
                r5 = 1;
                goto L_0x00fe;
            L_0x00fa:
                r5 = r5 + 1;
                goto L_0x00ae;
            L_0x00fd:
                r5 = 0;
            L_0x00fe:
                r6 = org.telegram.ui.ProfileActivity.this;
                r6 = r6.chatInfo;
                if (r6 == 0) goto L_0x014a;
            L_0x0106:
                r6 = org.telegram.ui.ProfileActivity.this;
                r6 = r6.chatInfo;
                r6 = r6.participants;
                if (r6 == 0) goto L_0x014a;
            L_0x0110:
                r6 = org.telegram.ui.ProfileActivity.this;
                r6 = r6.chatInfo;
                r6 = r6.participants;
                r6 = r6.participants;
                r6 = r6.size();
                if (r4 >= r6) goto L_0x014a;
            L_0x0120:
                r6 = org.telegram.ui.ProfileActivity.this;
                r6 = r6.chatInfo;
                r6 = r6.participants;
                r6 = r6.participants;
                r6 = r6.get(r4);
                r6 = (org.telegram.tgnet.TLRPC.ChatParticipant) r6;
                r6 = r6.user_id;
                r7 = r15;
                r7 = r7.user_id;
                if (r6 != r7) goto L_0x0147;
            L_0x0138:
                r5 = org.telegram.ui.ProfileActivity.this;
                r5 = r5.chatInfo;
                r5 = r5.participants;
                r5 = r5.participants;
                r5.remove(r4);
                r5 = 1;
                goto L_0x014a;
            L_0x0147:
                r4 = r4 + 1;
                goto L_0x0110;
            L_0x014a:
                if (r5 == 0) goto L_0x015f;
            L_0x014c:
                r4 = org.telegram.ui.ProfileActivity.this;
                r4.updateOnlineCount();
                r4 = org.telegram.ui.ProfileActivity.this;
                r4.updateRowsIds();
                r4 = org.telegram.ui.ProfileActivity.this;
                r4 = r4.listAdapter;
                r4.notifyDataSetChanged();
            L_0x015f:
                return;
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ProfileActivity$AnonymousClass11.didSetRights(int, org.telegram.tgnet.TLRPC$TL_chatAdminRights, org.telegram.tgnet.TLRPC$TL_chatBannedRights, java.lang.String):void");
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
        r1 = NUM; // 0x7f0e0342 float:1.887673E38 double:1.0531625687E-314;
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
        r7 = NUM; // 0x7f0e0216 float:1.887612E38 double:1.0531624205E-314;
        r8 = "CallViaTelegram";
        r7 = org.telegram.messenger.LocaleController.getString(r8, r7);
        r5.add(r7);
        r7 = 2;
        r7 = java.lang.Integer.valueOf(r7);
        r6.add(r7);
    L_0x00b9:
        r7 = NUM; // 0x7f0e0205 float:1.8876086E38 double:1.053162412E-314;
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
            float f = (float) top;
            if (this.extraHeight != f) {
                this.extraHeight = f;
                this.topView.invalidate();
                if (this.playProfileAnimation) {
                    if (this.extraHeight != 0.0f) {
                        z = true;
                    }
                    this.allowProfileAnimation = z;
                }
                needLayout();
            }
        }
    }

    private void needLayout() {
        int i = 0;
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
            float min = Math.min(1.0f, this.extraHeight / AndroidUtilities.dpf2(88.0f));
            this.listView.setTopGlowOffset((int) this.extraHeight);
            RecyclerListView recyclerListView2 = this.listView;
            int i2 = (this.extraHeight <= ((float) AndroidUtilities.dp(88.0f)) || this.extraHeight >= ((float) (this.listView.getMeasuredWidth() - currentActionBarHeight))) ? 0 : 2;
            recyclerListView2.setOverScrollMode(i2);
            ImageView imageView = this.writeButton;
            if (imageView != null) {
                imageView.setTranslationY((((float) ((this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0) + ActionBar.getCurrentActionBarHeight())) + this.extraHeight) - ((float) AndroidUtilities.dp(29.5f)));
                if (!this.openAnimationInProgress) {
                    Object obj = min > 0.2f ? 1 : null;
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
            this.avatarX = (-AndroidUtilities.dpf2(47.0f)) * min;
            float currentActionBarHeight2 = ((float) (this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0)) + ((((float) ActionBar.getCurrentActionBarHeight()) / 2.0f) * (min + 1.0f));
            float f = AndroidUtilities.density;
            this.avatarY = (currentActionBarHeight2 - (21.0f * f)) + ((f * 27.0f) * min);
            if (this.extraHeight > AndroidUtilities.dpf2(88.0f) || this.isPulledDown) {
                this.expandProgress = Math.max(0.0f, Math.min(1.0f, (this.extraHeight - AndroidUtilities.dpf2(88.0f)) / (((float) (this.listView.getMeasuredWidth() - currentActionBarHeight)) - AndroidUtilities.dpf2(88.0f))));
                this.avatarScale = AndroidUtilities.lerp(1.4285715f, 2.4285715f, Math.min(1.0f, this.expandProgress * 3.0f));
                currentActionBarHeight2 = Math.min(AndroidUtilities.dpf2(2000.0f), Math.max(AndroidUtilities.dpf2(1100.0f), Math.abs(this.listViewVelocityY))) / AndroidUtilities.dpf2(1100.0f);
                float f2;
                if (this.expandProgress >= 0.33f) {
                    if (!this.isPulledDown) {
                        this.isPulledDown = true;
                        this.overlaysView.setOverlaysVisible(true, currentActionBarHeight2);
                        this.avatarsViewPagerIndicatorView.refreshVisibility(currentActionBarHeight2);
                        ValueAnimator valueAnimator = this.expandAnimator;
                        if (valueAnimator == null) {
                            this.expandAnimator = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
                            this.expandAnimator.addUpdateListener(new -$$Lambda$ProfileActivity$BWWVoX_sVHctWKiV_og89RyDqdY(this, currentActionBarHeight));
                            this.expandAnimator.setDuration((long) (250.0f / currentActionBarHeight2));
                            this.expandAnimator.setInterpolator(CubicBezierInterpolator.EASE_BOTH);
                            this.expandAnimator.addListener(new AnimatorListenerAdapter() {
                                public void onAnimationStart(Animator animator) {
                                    ProfileActivity.this.nameTextView[1].setBackgroundColor(0);
                                }

                                public void onAnimationEnd(Animator animator) {
                                    if (!ProfileActivity.this.isPulledDown) {
                                        ProfileActivity.this.nameTextView[1].setBackgroundColor(Theme.getColor("avatar_backgroundActionBarBlue"));
                                    }
                                    ProfileActivity.this.actionBar.setItemsBackgroundColor(ProfileActivity.this.isPulledDown ? NUM : Theme.getColor("avatar_actionBarSelectorBlue"), false);
                                }
                            });
                        } else {
                            valueAnimator.cancel();
                            float lerp = AndroidUtilities.lerp(this.expandAnimatorValues, this.expandAnimator.getAnimatedFraction());
                            float[] fArr = this.expandAnimatorValues;
                            fArr[0] = lerp;
                            fArr[1] = 1.0f;
                            this.expandAnimator.setDuration((long) (((1.0f - lerp) * 250.0f) / currentActionBarHeight2));
                        }
                        this.expandAnimator.addListener(new AnimatorListenerAdapter() {
                            public void onAnimationStart(Animator animator) {
                                ProfileActivity.this.avatarImage.setForegroundImage(ProfileActivity.this.avatarsViewPager.getImageLocation(0), null, ProfileActivity.this.avatarImage.getImageReceiver().getDrawable());
                                ProfileActivity.this.avatarsViewPager.resetCurrentItem();
                            }

                            public void onAnimationEnd(Animator animator) {
                                ProfileActivity.this.expandAnimator.removeListener(this);
                                ProfileActivity.this.avatarImage.clearForeground();
                                ProfileActivity.this.topView.setBackgroundColor(-16777216);
                                ProfileActivity.this.avatarImage.setVisibility(8);
                                ProfileActivity.this.avatarsViewPager.setVisibility(0);
                            }
                        });
                        this.expandAnimator.start();
                    }
                    ViewGroup.LayoutParams layoutParams2 = this.avatarsViewPager.getLayoutParams();
                    layoutParams2.width = this.listView.getMeasuredWidth();
                    f2 = (float) currentActionBarHeight;
                    layoutParams2.height = (int) (this.extraHeight + f2);
                    this.avatarsViewPager.requestLayout();
                    if (!this.expandAnimator.isRunning()) {
                        this.nameTextView[1].setTranslationX(AndroidUtilities.dpf2(16.0f) - ((float) this.nameTextView[1].getLeft()));
                        this.nameTextView[1].setTranslationY(((this.extraHeight + f2) - AndroidUtilities.dpf2(38.0f)) - ((float) this.nameTextView[1].getBottom()));
                        this.onlineTextView[1].setTranslationX(AndroidUtilities.dpf2(16.0f) - ((float) this.onlineTextView[1].getLeft()));
                        this.onlineTextView[1].setTranslationY(((f2 + this.extraHeight) - AndroidUtilities.dpf2(18.0f)) - ((float) this.onlineTextView[1].getBottom()));
                    }
                } else {
                    if (this.isPulledDown) {
                        this.isPulledDown = false;
                        this.overlaysView.setOverlaysVisible(false, currentActionBarHeight2);
                        this.avatarsViewPagerIndicatorView.refreshVisibility(currentActionBarHeight2);
                        this.expandAnimator.cancel();
                        f2 = AndroidUtilities.lerp(this.expandAnimatorValues, this.expandAnimator.getAnimatedFraction());
                        float[] fArr2 = this.expandAnimatorValues;
                        fArr2[0] = f2;
                        fArr2[1] = 0.0f;
                        if (this.isInLandscapeMode) {
                            this.expandAnimator.setDuration(0);
                        } else {
                            this.expandAnimator.setDuration((long) ((f2 * 250.0f) / currentActionBarHeight2));
                        }
                        this.topView.setBackgroundColor(Theme.getColor("avatar_backgroundActionBarBlue"));
                        BackupImageView currentItemView = this.avatarsViewPager.getCurrentItemView();
                        if (currentItemView != null) {
                            this.avatarImage.setForegroundImageDrawable(currentItemView.getImageReceiver().getDrawable());
                        }
                        this.avatarImage.setForegroundAlpha(1.0f);
                        this.avatarImage.setVisibility(0);
                        this.avatarsViewPager.setVisibility(8);
                        this.expandAnimator.start();
                    }
                    this.avatarImage.setScaleX(this.avatarScale);
                    this.avatarImage.setScaleY(this.avatarScale);
                    ValueAnimator valueAnimator2 = this.expandAnimator;
                    if (valueAnimator2 == null || !valueAnimator2.isRunning()) {
                        refreshNameAndOnlineXY();
                        this.nameTextView[1].setTranslationX(this.nameX);
                        this.nameTextView[1].setTranslationY(this.nameY);
                        this.onlineTextView[1].setTranslationX(this.onlineX);
                        this.onlineTextView[1].setTranslationY(this.onlineY);
                    }
                }
            }
            if (this.extraHeight <= AndroidUtilities.dpf2(88.0f)) {
                this.avatarScale = ((18.0f * min) + 42.0f) / 42.0f;
                float f3 = (0.12f * min) + 1.0f;
                ValueAnimator valueAnimator3 = this.expandAnimator;
                if (valueAnimator3 == null || !valueAnimator3.isRunning()) {
                    this.avatarImage.setScaleX(this.avatarScale);
                    this.avatarImage.setScaleY(this.avatarScale);
                    this.avatarImage.setTranslationX(this.avatarX);
                    this.avatarImage.setTranslationY((float) Math.ceil((double) this.avatarY));
                }
                this.nameX = (AndroidUtilities.density * -21.0f) * min;
                this.nameY = (((float) Math.floor((double) this.avatarY)) + ((float) AndroidUtilities.dp(1.3f))) + (((float) AndroidUtilities.dp(7.0f)) * min);
                this.onlineX = (AndroidUtilities.density * -21.0f) * min;
                this.onlineY = (((float) Math.floor((double) this.avatarY)) + ((float) AndroidUtilities.dp(24.0f))) + (((float) Math.floor((double) (AndroidUtilities.density * 11.0f))) * min);
                while (i < 2) {
                    if (this.nameTextView[i] != null) {
                        valueAnimator3 = this.expandAnimator;
                        if (valueAnimator3 == null || !valueAnimator3.isRunning()) {
                            this.nameTextView[i].setTranslationX(this.nameX);
                            this.nameTextView[i].setTranslationY(this.nameY);
                            this.onlineTextView[i].setTranslationX(this.onlineX);
                            this.onlineTextView[i].setTranslationY(this.onlineY);
                        }
                        this.nameTextView[i].setScaleX(f3);
                        this.nameTextView[i].setScaleY(f3);
                    }
                    i++;
                }
            }
            if (!this.openAnimationInProgress) {
                ValueAnimator valueAnimator4 = this.expandAnimator;
                if (valueAnimator4 == null || !valueAnimator4.isRunning()) {
                    needLayoutText(min);
                }
            }
        }
        if (this.isPulledDown || (this.overlaysView.animator != null && this.overlaysView.animator.isRunning())) {
            ViewGroup.LayoutParams layoutParams3 = this.overlaysView.getLayoutParams();
            layoutParams3.width = this.listView.getMeasuredWidth();
            layoutParams3.height = (int) (this.extraHeight + ((float) currentActionBarHeight));
            this.overlaysView.requestLayout();
        }
    }

    public /* synthetic */ void lambda$needLayout$19$ProfileActivity(int i, ValueAnimator valueAnimator) {
        int color;
        float lerp = AndroidUtilities.lerp(this.expandAnimatorValues, valueAnimator.getAnimatedFraction());
        this.avatarImage.setScaleX(this.avatarScale);
        this.avatarImage.setScaleY(this.avatarScale);
        this.avatarImage.setTranslationX(AndroidUtilities.lerp(this.avatarX, 0.0f, lerp));
        this.avatarImage.setTranslationY(AndroidUtilities.lerp((float) Math.ceil((double) this.avatarY), 0.0f, lerp));
        this.avatarImage.setRoundRadius((int) AndroidUtilities.lerp(AndroidUtilities.dpf2(21.0f), 0.0f, lerp));
        if (this.extraHeight > AndroidUtilities.dpf2(88.0f) && this.expandProgress < 0.33f) {
            refreshNameAndOnlineXY();
        }
        ScamDrawable scamDrawable = this.scamDrawable;
        String str = "avatar_subtitleInProfileBlue";
        if (scamDrawable != null) {
            scamDrawable.setColor(ColorUtils.blendARGB(Theme.getColor(str), Color.argb(179, 255, 255, 255), lerp));
        }
        Drawable drawable = this.lockIconDrawable;
        if (drawable != null) {
            drawable.setColorFilter(ColorUtils.blendARGB(Theme.getColor("chat_lockIcon"), -1, lerp), Mode.MULTIPLY);
        }
        CrossfadeDrawable crossfadeDrawable = this.verifiedCrossfadeDrawable;
        if (crossfadeDrawable != null) {
            crossfadeDrawable.setProgress(lerp);
        }
        float dpf2 = AndroidUtilities.dpf2(8.0f);
        float dpvar_ = AndroidUtilities.dpf2(16.0f) - ((float) this.nameTextView[1].getLeft());
        float f = (float) i;
        float dpvar_ = ((this.extraHeight + f) - AndroidUtilities.dpf2(38.0f)) - ((float) this.nameTextView[1].getBottom());
        float f2 = this.nameX;
        float f3 = (dpf2 + f2) + ((dpvar_ - f2) / 2.0f);
        float f4 = this.nameY;
        float f5 = 1.0f - lerp;
        float f6 = f5 * f5;
        f5 = (f5 * 2.0f) * lerp;
        f2 = (f2 * f6) + (f3 * f5);
        f3 = lerp * lerp;
        f2 += dpvar_ * f3;
        f4 = ((f4 * f6) + (((dpf2 + f4) + ((dpvar_ - f4) / 2.0f)) * f5)) + (dpvar_ * f3);
        float dpvar_ = AndroidUtilities.dpf2(16.0f) - ((float) this.onlineTextView[1].getLeft());
        dpvar_ = ((this.extraHeight + f) - AndroidUtilities.dpf2(18.0f)) - ((float) this.onlineTextView[1].getBottom());
        dpvar_ = this.onlineX;
        float f7 = (dpf2 + dpvar_) + ((dpvar_ - dpvar_) / 2.0f);
        float f8 = this.onlineY;
        dpvar_ = ((dpvar_ * f6) + (f7 * f5)) + (dpvar_ * f3);
        dpf2 = ((f6 * f8) + (f5 * ((dpf2 + f8) + ((dpvar_ - f8) / 2.0f)))) + (f3 * dpvar_);
        this.nameTextView[1].setTranslationX(f2);
        this.nameTextView[1].setTranslationY(f4);
        this.onlineTextView[1].setTranslationX(dpvar_);
        this.onlineTextView[1].setTranslationY(dpf2);
        Object tag = this.onlineTextView[1].getTag();
        if (tag instanceof String) {
            color = Theme.getColor((String) tag);
        } else {
            color = Theme.getColor(str);
        }
        this.onlineTextView[1].setTextColor(ColorUtils.blendARGB(color, Color.argb(179, 255, 255, 255), lerp));
        if (this.extraHeight > AndroidUtilities.dpf2(88.0f)) {
            SimpleTextView[] simpleTextViewArr = this.nameTextView;
            simpleTextViewArr[1].setPivotY(AndroidUtilities.lerp(0.0f, (float) simpleTextViewArr[1].getMeasuredHeight(), lerp));
            this.nameTextView[1].setScaleX(AndroidUtilities.lerp(1.12f, 1.67f, lerp));
            this.nameTextView[1].setScaleY(AndroidUtilities.lerp(1.12f, 1.67f, lerp));
        }
        needLayoutText(Math.min(1.0f, this.extraHeight / AndroidUtilities.dpf2(88.0f)));
        this.nameTextView[1].setTextColor(ColorUtils.blendARGB(Theme.getColor("profile_title"), -1, lerp));
        this.actionBar.setItemsColor(ColorUtils.blendARGB(Theme.getColor("actionBarDefaultIcon"), -1, lerp), false);
        this.avatarImage.setForegroundAlpha(lerp);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.avatarImage.getLayoutParams();
        layoutParams.width = (int) AndroidUtilities.lerp(AndroidUtilities.dpf2(42.0f), ((float) this.listView.getMeasuredWidth()) / this.avatarScale, lerp);
        layoutParams.height = (int) AndroidUtilities.lerp(AndroidUtilities.dpf2(42.0f), (this.extraHeight + f) / this.avatarScale, lerp);
        layoutParams.leftMargin = (int) AndroidUtilities.lerp(AndroidUtilities.dpf2(64.0f), 0.0f, lerp);
        this.avatarImage.requestLayout();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("w = ");
        stringBuilder.append(layoutParams.width);
        stringBuilder.append(" h = ");
        stringBuilder.append(layoutParams.height);
        stringBuilder.append(" scale ");
        stringBuilder.append(this.avatarScale);
        FileLog.d(stringBuilder.toString());
    }

    private void refreshNameAndOnlineXY() {
        this.nameX = ((float) AndroidUtilities.dp(-21.0f)) + (((float) this.avatarImage.getMeasuredWidth()) * (this.avatarScale - 1.4285715f));
        this.nameY = ((((float) Math.floor((double) this.avatarY)) + ((float) AndroidUtilities.dp(1.3f))) + ((float) AndroidUtilities.dp(7.0f))) + ((((float) this.avatarImage.getMeasuredHeight()) * (this.avatarScale - 1.4285715f)) / 2.0f);
        this.onlineX = ((float) AndroidUtilities.dp(-21.0f)) + (((float) this.avatarImage.getMeasuredWidth()) * (this.avatarScale - 1.4285715f));
        this.onlineY = ((((float) Math.floor((double) this.avatarY)) + ((float) AndroidUtilities.dp(24.0f))) + ((float) Math.floor((double) (AndroidUtilities.density * 11.0f)))) + ((((float) this.avatarImage.getMeasuredHeight()) * (this.avatarScale - 1.4285715f)) / 2.0f);
    }

    private void needLayoutText(float f) {
        float scaleX = this.nameTextView[1].getScaleX();
        float f2 = this.extraHeight > AndroidUtilities.dpf2(88.0f) ? 1.67f : 1.12f;
        if (this.extraHeight <= ((float) AndroidUtilities.dp(88.0f)) || scaleX == f2) {
            int dp = AndroidUtilities.isTablet() ? AndroidUtilities.dp(490.0f) : AndroidUtilities.displaySize.x;
            int i = (this.callItem == null && this.editItem == null) ? 0 : 48;
            int dp2 = AndroidUtilities.dp((float) (126 + (40 + i)));
            int i2 = dp - dp2;
            float f3 = (float) dp;
            dp2 = (int) ((f3 - (((float) dp2) * Math.max(0.0f, 1.0f - (f != 1.0f ? (0.15f * f) / (1.0f - f) : 1.0f)))) - this.nameTextView[1].getTranslationX());
            float measureText = (this.nameTextView[1].getPaint().measureText(this.nameTextView[1].getText().toString()) * scaleX) + ((float) this.nameTextView[1].getSideDrawablesSize());
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.nameTextView[1].getLayoutParams();
            int i3 = layoutParams.width;
            float f4 = (float) dp2;
            if (f4 < measureText) {
                layoutParams.width = Math.max(i2, (int) Math.ceil((double) (((float) (dp2 - AndroidUtilities.dp(24.0f))) / (((f2 - scaleX) * 7.0f) + scaleX))));
            } else {
                layoutParams.width = (int) Math.ceil((double) measureText);
            }
            layoutParams.width = (int) Math.min(((f3 - this.nameTextView[1].getX()) / scaleX) - ((float) AndroidUtilities.dp(8.0f)), (float) layoutParams.width);
            if (layoutParams.width != i3) {
                this.nameTextView[1].requestLayout();
            }
            f2 = this.onlineTextView[1].getPaint().measureText(this.onlineTextView[1].getText().toString());
            FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) this.onlineTextView[1].getLayoutParams();
            i2 = layoutParams2.width;
            layoutParams2.rightMargin = (int) Math.ceil((double) ((this.onlineTextView[1].getTranslationX() + ((float) AndroidUtilities.dp(8.0f))) + (((float) AndroidUtilities.dp(40.0f)) * (1.0f - f))));
            if (f4 < f2) {
                layoutParams2.width = (int) Math.ceil((double) dp2);
            } else {
                layoutParams2.width = -2;
            }
            if (i2 != layoutParams2.width) {
                this.onlineTextView[1].requestLayout();
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
        invalidateIsInLandscapeMode();
        if (this.isInLandscapeMode && this.isPulledDown) {
            View findViewByPosition = this.layoutManager.findViewByPosition(0);
            if (findViewByPosition != null) {
                this.listView.scrollBy(0, findViewByPosition.getTop() - AndroidUtilities.dp(88.0f));
            }
        }
        fixLayout();
    }

    private void invalidateIsInLandscapeMode() {
        Point point = new Point();
        getParentActivity().getWindowManager().getDefaultDisplay().getSize(point);
        this.isInLandscapeMode = point.x > point.y;
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        int i3 = i;
        Object[] objArr2 = objArr;
        int i4 = 0;
        RecyclerListView recyclerListView;
        int i5;
        if (i3 == NotificationCenter.updateInterfaces) {
            i3 = ((Integer) objArr2[0]).intValue();
            if (this.user_id != 0) {
                if (!((i3 & 2) == 0 && (i3 & 1) == 0 && (i3 & 4) == 0)) {
                    updateProfileData();
                }
                if ((i3 & 1024) != 0) {
                    recyclerListView = this.listView;
                    if (recyclerListView != null) {
                        Holder holder = (Holder) recyclerListView.findViewHolderForPosition(this.phoneRow);
                        if (holder != null) {
                            this.listAdapter.onBindViewHolder(holder, this.phoneRow);
                        }
                    }
                }
            } else if (this.chat_id != 0) {
                i5 = i3 & 8192;
                if (!(i5 == 0 && (i3 & 8) == 0 && (i3 & 16) == 0 && (i3 & 32) == 0 && (i3 & 4) == 0)) {
                    updateOnlineCount();
                    updateProfileData();
                }
                if (i5 != 0) {
                    updateRowsIds();
                    ListAdapter listAdapter = this.listAdapter;
                    if (listAdapter != null) {
                        listAdapter.notifyDataSetChanged();
                    }
                }
                if (!((i3 & 2) == 0 && (i3 & 1) == 0 && (i3 & 4) == 0)) {
                    RecyclerListView recyclerListView2 = this.listView;
                    if (recyclerListView2 != null) {
                        i5 = recyclerListView2.getChildCount();
                        while (i4 < i5) {
                            View childAt = this.listView.getChildAt(i4);
                            if (childAt instanceof UserCell) {
                                ((UserCell) childAt).update(i3);
                            }
                            i4++;
                        }
                    }
                }
            }
        } else {
            int i6 = 1;
            long longValue;
            long j;
            int[] iArr;
            int[] iArr2;
            int[] iArr3;
            ListAdapter listAdapter2;
            boolean z;
            int i7;
            if (i3 == NotificationCenter.chatOnlineCountDidLoad) {
                Integer num = (Integer) objArr2[0];
                if (this.chatInfo != null) {
                    Chat chat = this.currentChat;
                    if (chat != null && chat.id == num.intValue()) {
                        this.chatInfo.online_count = ((Integer) objArr2[1]).intValue();
                        updateOnlineCount();
                        updateProfileData();
                    }
                }
            } else if (i3 == NotificationCenter.contactsDidLoad) {
                createActionBarMenu();
            } else if (i3 == NotificationCenter.mediaDidLoad) {
                long longValue2 = ((Long) objArr2[0]).longValue();
                if (((Integer) objArr2[3]).intValue() == this.classGuid) {
                    long j2 = this.dialog_id;
                    if (j2 == 0) {
                        i3 = this.user_id;
                        if (i3 == 0) {
                            i3 = this.chat_id;
                            if (i3 != 0) {
                                i3 = -i3;
                            }
                        }
                        j2 = (long) i3;
                    }
                    i3 = ((Integer) objArr2[4]).intValue();
                    this.sharedMediaData[i3].setTotalCount(((Integer) objArr2[1]).intValue());
                    ArrayList arrayList = (ArrayList) objArr2[2];
                    boolean z2 = ((int) j2) == 0;
                    if (longValue2 == j2) {
                        i6 = 0;
                    }
                    if (!arrayList.isEmpty()) {
                        this.sharedMediaData[i3].setEndReached(i6, ((Boolean) objArr2[5]).booleanValue());
                    }
                    for (i5 = 0; i5 < arrayList.size(); i5++) {
                        this.sharedMediaData[i3].addMessage((MessageObject) arrayList.get(i5), i6, false, z2);
                    }
                }
            } else if (i3 == NotificationCenter.mediaCountsDidLoad) {
                longValue = ((Long) objArr2[0]).longValue();
                j = this.dialog_id;
                if (j == 0) {
                    i3 = this.user_id;
                    if (i3 == 0) {
                        i3 = this.chat_id;
                        if (i3 != 0) {
                            i3 = -i3;
                        }
                    }
                    j = (long) i3;
                }
                if (longValue == j || longValue == this.mergeDialogId) {
                    int[] iArr4 = (int[]) objArr2[1];
                    if (longValue == j) {
                        this.mediaCount = iArr4;
                    } else {
                        this.mediaMergeCount = iArr4;
                    }
                    iArr4 = this.lastMediaCount;
                    iArr = this.prevMediaCount;
                    System.arraycopy(iArr4, 0, iArr, 0, iArr.length);
                    i3 = 0;
                    while (true) {
                        iArr = this.lastMediaCount;
                        if (i3 >= iArr.length) {
                            break;
                        }
                        iArr2 = this.mediaCount;
                        if (iArr2[i3] >= 0) {
                            iArr3 = this.mediaMergeCount;
                            if (iArr3[i3] >= 0) {
                                iArr[i3] = iArr2[i3] + iArr3[i3];
                                if (longValue == j && this.lastMediaCount[i3] != 0) {
                                    MediaDataController.getInstance(this.currentAccount).loadMedia(j, 50, 0, i3, 2, this.classGuid);
                                }
                                i3++;
                            }
                        }
                        iArr = this.mediaCount;
                        if (iArr[i3] >= 0) {
                            this.lastMediaCount[i3] = iArr[i3];
                        } else {
                            iArr = this.mediaMergeCount;
                            if (iArr[i3] >= 0) {
                                this.lastMediaCount[i3] = iArr[i3];
                            } else {
                                this.lastMediaCount[i3] = 0;
                            }
                        }
                        MediaDataController.getInstance(this.currentAccount).loadMedia(j, 50, 0, i3, 2, this.classGuid);
                        i3++;
                    }
                    updateSharedMediaRows();
                }
            } else if (i3 == NotificationCenter.mediaCountDidLoad) {
                longValue = ((Long) objArr2[0]).longValue();
                j = this.dialog_id;
                if (j == 0) {
                    i3 = this.user_id;
                    if (i3 == 0) {
                        i3 = this.chat_id;
                        if (i3 != 0) {
                            i3 = -i3;
                        }
                    }
                    j = (long) i3;
                }
                if (longValue == j || longValue == this.mergeDialogId) {
                    i3 = ((Integer) objArr2[3]).intValue();
                    i5 = ((Integer) objArr2[1]).intValue();
                    if (longValue == j) {
                        this.mediaCount[i3] = i5;
                    } else {
                        this.mediaMergeCount[i3] = i5;
                    }
                    iArr = this.prevMediaCount;
                    iArr2 = this.lastMediaCount;
                    iArr[i3] = iArr2[i3];
                    iArr = this.mediaCount;
                    if (iArr[i3] >= 0) {
                        iArr3 = this.mediaMergeCount;
                        if (iArr3[i3] >= 0) {
                            iArr2[i3] = iArr[i3] + iArr3[i3];
                            updateSharedMediaRows();
                        }
                    }
                    iArr = this.mediaCount;
                    if (iArr[i3] >= 0) {
                        this.lastMediaCount[i3] = iArr[i3];
                    } else {
                        iArr = this.mediaMergeCount;
                        if (iArr[i3] >= 0) {
                            this.lastMediaCount[i3] = iArr[i3];
                        } else {
                            this.lastMediaCount[i3] = 0;
                        }
                    }
                    updateSharedMediaRows();
                }
            } else if (i3 == NotificationCenter.encryptedChatCreated) {
                if (this.creatingChat) {
                    AndroidUtilities.runOnUIThread(new -$$Lambda$ProfileActivity$1kAHYRs1Xitld36bWHa7gE_IAF4(this, objArr2));
                }
            } else if (i3 == NotificationCenter.encryptedChatUpdated) {
                EncryptedChat encryptedChat = (EncryptedChat) objArr2[0];
                EncryptedChat encryptedChat2 = this.currentEncryptedChat;
                if (encryptedChat2 != null && encryptedChat.id == encryptedChat2.id) {
                    this.currentEncryptedChat = encryptedChat;
                    updateRowsIds();
                    listAdapter2 = this.listAdapter;
                    if (listAdapter2 != null) {
                        listAdapter2.notifyDataSetChanged();
                    }
                }
            } else if (i3 == NotificationCenter.blockedUsersDidLoad) {
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
            } else if (i3 == NotificationCenter.chatInfoDidLoad) {
                ChatFull chatFull = (ChatFull) objArr2[0];
                if (chatFull.id == this.chat_id) {
                    boolean booleanValue = ((Boolean) objArr2[2]).booleanValue();
                    ChatFull chatFull2 = this.chatInfo;
                    if ((chatFull2 instanceof TL_channelFull) && chatFull.participants == null && chatFull2 != null) {
                        chatFull.participants = chatFull2.participants;
                    }
                    if (this.chatInfo == null && (chatFull instanceof TL_channelFull)) {
                        i4 = 1;
                    }
                    this.chatInfo = chatFull;
                    if (this.mergeDialogId == 0) {
                        i3 = this.chatInfo.migrated_from_chat_id;
                        if (i3 != 0) {
                            this.mergeDialogId = (long) (-i3);
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
                    if (this.currentChat.megagroup && !(i4 == 0 && booleanValue)) {
                        getChannelParticipants(true);
                    }
                }
            } else if (i3 == NotificationCenter.closeChats) {
                removeSelfFromStack();
            } else if (i3 == NotificationCenter.botInfoDidLoad) {
                BotInfo botInfo = (BotInfo) objArr2[0];
                if (botInfo.user_id == this.user_id) {
                    this.botInfo = botInfo;
                    updateRowsIds();
                    listAdapter2 = this.listAdapter;
                    if (listAdapter2 != null) {
                        listAdapter2.notifyDataSetChanged();
                    }
                }
            } else if (i3 == NotificationCenter.userInfoDidLoad) {
                if (((Integer) objArr2[0]).intValue() == this.user_id) {
                    this.userInfo = (UserFull) objArr2[1];
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
            } else if (i3 == NotificationCenter.didReceiveNewMessages) {
                if (!((Boolean) objArr2[2]).booleanValue()) {
                    longValue = this.dialog_id;
                    if (longValue == 0) {
                        i3 = this.user_id;
                        if (i3 == 0) {
                            i3 = -this.chat_id;
                        }
                        longValue = (long) i3;
                    }
                    if (longValue == ((Long) objArr2[0]).longValue()) {
                        z = ((int) longValue) == 0;
                        ArrayList arrayList2 = (ArrayList) objArr2[1];
                        i7 = 0;
                        while (i7 < arrayList2.size()) {
                            MessageObject messageObject = (MessageObject) arrayList2.get(i7);
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
                            int mediaType = MediaDataController.getMediaType(messageObject.messageOwner);
                            if (mediaType != -1) {
                                this.sharedMediaData[mediaType].addMessage(messageObject, 0, true, z);
                                i7++;
                            } else {
                                return;
                            }
                        }
                        loadMediaCounts();
                    }
                }
            } else if (i3 == NotificationCenter.messagesDeleted) {
                if (!((Boolean) objArr2[2]).booleanValue()) {
                    i3 = ((Integer) objArr2[1]).intValue();
                    if (ChatObject.isChannel(this.currentChat)) {
                        if ((i3 != 0 || this.mergeDialogId == 0) && i3 != this.currentChat.id) {
                            return;
                        }
                    } else if (i3 != 0) {
                        return;
                    }
                    ArrayList arrayList3 = (ArrayList) objArr2[0];
                    i5 = arrayList3.size();
                    i7 = 0;
                    Object obj = null;
                    while (i7 < i5) {
                        Object obj2 = obj;
                        int i8 = 0;
                        while (true) {
                            SharedMediaData[] sharedMediaDataArr = this.sharedMediaData;
                            if (i8 >= sharedMediaDataArr.length) {
                                break;
                            }
                            if (sharedMediaDataArr[i8].deleteMessage(((Integer) arrayList3.get(i7)).intValue(), 0)) {
                                obj2 = 1;
                            }
                            i8++;
                        }
                        i7++;
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
            } else if (i3 == NotificationCenter.emojiDidLoad) {
                recyclerListView = this.listView;
                if (recyclerListView != null) {
                    recyclerListView.invalidateViews();
                }
            }
        }
    }

    public /* synthetic */ void lambda$didReceivedNotification$20$ProfileActivity(Object[] objArr) {
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.closeChats);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.closeChats, new Object[0]);
        EncryptedChat encryptedChat = (EncryptedChat) objArr[0];
        Bundle bundle = new Bundle();
        bundle.putInt("enc_id", encryptedChat.id);
        presentFragment(new ChatActivity(bundle), true);
    }

    public void onResume() {
        super.onResume();
        invalidateIsInLandscapeMode();
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

    /* JADX WARNING: Removed duplicated region for block: B:100:0x0133  */
    /* JADX WARNING: Removed duplicated region for block: B:92:0x011c  */
    /* JADX WARNING: Removed duplicated region for block: B:100:0x0133  */
    /* JADX WARNING: Removed duplicated region for block: B:84:0x0105  */
    /* JADX WARNING: Removed duplicated region for block: B:92:0x011c  */
    /* JADX WARNING: Removed duplicated region for block: B:100:0x0133  */
    /* JADX WARNING: Removed duplicated region for block: B:76:0x00ee  */
    /* JADX WARNING: Removed duplicated region for block: B:84:0x0105  */
    /* JADX WARNING: Removed duplicated region for block: B:92:0x011c  */
    /* JADX WARNING: Removed duplicated region for block: B:100:0x0133  */
    /* JADX WARNING: Removed duplicated region for block: B:68:0x00d7  */
    /* JADX WARNING: Removed duplicated region for block: B:76:0x00ee  */
    /* JADX WARNING: Removed duplicated region for block: B:84:0x0105  */
    /* JADX WARNING: Removed duplicated region for block: B:92:0x011c  */
    /* JADX WARNING: Removed duplicated region for block: B:100:0x0133  */
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
        r13.updateRowsIds();
        r7 = 3;
        r8 = 2;
        r9 = -1;
        if (r0 != r9) goto L_0x004c;
    L_0x001b:
        r10 = r13.sharedHeaderRow;
        if (r10 == r9) goto L_0x004c;
    L_0x001f:
        r0 = r13.photosRow;
        if (r0 == r9) goto L_0x0024;
    L_0x0023:
        goto L_0x0025;
    L_0x0024:
        r7 = 2;
    L_0x0025:
        r0 = r13.filesRow;
        if (r0 == r9) goto L_0x002b;
    L_0x0029:
        r7 = r7 + 1;
    L_0x002b:
        r0 = r13.linksRow;
        if (r0 == r9) goto L_0x0031;
    L_0x002f:
        r7 = r7 + 1;
    L_0x0031:
        r0 = r13.audioRow;
        if (r0 == r9) goto L_0x0037;
    L_0x0035:
        r7 = r7 + 1;
    L_0x0037:
        r0 = r13.voiceRow;
        if (r0 == r9) goto L_0x003d;
    L_0x003b:
        r7 = r7 + 1;
    L_0x003d:
        r0 = r13.groupsInCommonRow;
        if (r0 == r9) goto L_0x0043;
    L_0x0041:
        r7 = r7 + 1;
    L_0x0043:
        r0 = r13.listAdapter;
        r1 = r13.sharedHeaderRow;
        r0.notifyItemRangeInserted(r1, r7);
        goto L_0x016c;
    L_0x004c:
        if (r0 == r9) goto L_0x0149;
    L_0x004e:
        r10 = r13.sharedHeaderRow;
        if (r10 == r9) goto L_0x0149;
    L_0x0052:
        if (r1 == r9) goto L_0x0068;
    L_0x0054:
        r0 = r13.photosRow;
        if (r0 == r9) goto L_0x0068;
    L_0x0058:
        r10 = r13.prevMediaCount;
        r11 = 0;
        r10 = r10[r11];
        r12 = r13.lastMediaCount;
        r11 = r12[r11];
        if (r10 == r11) goto L_0x0068;
    L_0x0063:
        r10 = r13.listAdapter;
        r10.notifyItemChanged(r0);
    L_0x0068:
        if (r2 == r9) goto L_0x007e;
    L_0x006a:
        r0 = r13.filesRow;
        if (r0 == r9) goto L_0x007e;
    L_0x006e:
        r10 = r13.prevMediaCount;
        r11 = 1;
        r10 = r10[r11];
        r12 = r13.lastMediaCount;
        r11 = r12[r11];
        if (r10 == r11) goto L_0x007e;
    L_0x0079:
        r10 = r13.listAdapter;
        r10.notifyItemChanged(r0);
    L_0x007e:
        if (r3 == r9) goto L_0x0093;
    L_0x0080:
        r0 = r13.linksRow;
        if (r0 == r9) goto L_0x0093;
    L_0x0084:
        r10 = r13.prevMediaCount;
        r10 = r10[r7];
        r11 = r13.lastMediaCount;
        r7 = r11[r7];
        if (r10 == r7) goto L_0x0093;
    L_0x008e:
        r7 = r13.listAdapter;
        r7.notifyItemChanged(r0);
    L_0x0093:
        if (r4 == r9) goto L_0x00a9;
    L_0x0095:
        r0 = r13.audioRow;
        if (r0 == r9) goto L_0x00a9;
    L_0x0099:
        r7 = r13.prevMediaCount;
        r10 = 4;
        r7 = r7[r10];
        r11 = r13.lastMediaCount;
        r10 = r11[r10];
        if (r7 == r10) goto L_0x00a9;
    L_0x00a4:
        r7 = r13.listAdapter;
        r7.notifyItemChanged(r0);
    L_0x00a9:
        if (r5 == r9) goto L_0x00be;
    L_0x00ab:
        r0 = r13.voiceRow;
        if (r0 == r9) goto L_0x00be;
    L_0x00af:
        r7 = r13.prevMediaCount;
        r7 = r7[r8];
        r10 = r13.lastMediaCount;
        r8 = r10[r8];
        if (r7 == r8) goto L_0x00be;
    L_0x00b9:
        r7 = r13.listAdapter;
        r7.notifyItemChanged(r0);
    L_0x00be:
        if (r1 != r9) goto L_0x00ca;
    L_0x00c0:
        r0 = r13.photosRow;
        if (r0 == r9) goto L_0x00ca;
    L_0x00c4:
        r1 = r13.listAdapter;
        r1.notifyItemInserted(r0);
        goto L_0x00d5;
    L_0x00ca:
        if (r1 == r9) goto L_0x00d5;
    L_0x00cc:
        r0 = r13.photosRow;
        if (r0 != r9) goto L_0x00d5;
    L_0x00d0:
        r0 = r13.listAdapter;
        r0.notifyItemRemoved(r1);
    L_0x00d5:
        if (r2 != r9) goto L_0x00e1;
    L_0x00d7:
        r0 = r13.filesRow;
        if (r0 == r9) goto L_0x00e1;
    L_0x00db:
        r1 = r13.listAdapter;
        r1.notifyItemInserted(r0);
        goto L_0x00ec;
    L_0x00e1:
        if (r2 == r9) goto L_0x00ec;
    L_0x00e3:
        r0 = r13.filesRow;
        if (r0 != r9) goto L_0x00ec;
    L_0x00e7:
        r0 = r13.listAdapter;
        r0.notifyItemRemoved(r2);
    L_0x00ec:
        if (r3 != r9) goto L_0x00f8;
    L_0x00ee:
        r0 = r13.linksRow;
        if (r0 == r9) goto L_0x00f8;
    L_0x00f2:
        r1 = r13.listAdapter;
        r1.notifyItemInserted(r0);
        goto L_0x0103;
    L_0x00f8:
        if (r3 == r9) goto L_0x0103;
    L_0x00fa:
        r0 = r13.linksRow;
        if (r0 != r9) goto L_0x0103;
    L_0x00fe:
        r0 = r13.listAdapter;
        r0.notifyItemRemoved(r3);
    L_0x0103:
        if (r4 != r9) goto L_0x010f;
    L_0x0105:
        r0 = r13.audioRow;
        if (r0 == r9) goto L_0x010f;
    L_0x0109:
        r1 = r13.listAdapter;
        r1.notifyItemInserted(r0);
        goto L_0x011a;
    L_0x010f:
        if (r4 == r9) goto L_0x011a;
    L_0x0111:
        r0 = r13.audioRow;
        if (r0 != r9) goto L_0x011a;
    L_0x0115:
        r0 = r13.listAdapter;
        r0.notifyItemRemoved(r4);
    L_0x011a:
        if (r5 != r9) goto L_0x0126;
    L_0x011c:
        r0 = r13.voiceRow;
        if (r0 == r9) goto L_0x0126;
    L_0x0120:
        r1 = r13.listAdapter;
        r1.notifyItemInserted(r0);
        goto L_0x0131;
    L_0x0126:
        if (r5 == r9) goto L_0x0131;
    L_0x0128:
        r0 = r13.voiceRow;
        if (r0 != r9) goto L_0x0131;
    L_0x012c:
        r0 = r13.listAdapter;
        r0.notifyItemRemoved(r5);
    L_0x0131:
        if (r6 != r9) goto L_0x013d;
    L_0x0133:
        r0 = r13.groupsInCommonRow;
        if (r0 == r9) goto L_0x013d;
    L_0x0137:
        r1 = r13.listAdapter;
        r1.notifyItemInserted(r0);
        goto L_0x016c;
    L_0x013d:
        if (r6 == r9) goto L_0x016c;
    L_0x013f:
        r0 = r13.groupsInCommonRow;
        if (r0 != r9) goto L_0x016c;
    L_0x0143:
        r0 = r13.listAdapter;
        r0.notifyItemRemoved(r6);
        goto L_0x016c;
    L_0x0149:
        if (r0 == r9) goto L_0x016c;
    L_0x014b:
        r10 = r13.sharedHeaderRow;
        if (r10 != r9) goto L_0x016c;
    L_0x014f:
        if (r1 == r9) goto L_0x0152;
    L_0x0151:
        goto L_0x0153;
    L_0x0152:
        r7 = 2;
    L_0x0153:
        if (r2 == r9) goto L_0x0157;
    L_0x0155:
        r7 = r7 + 1;
    L_0x0157:
        if (r3 == r9) goto L_0x015b;
    L_0x0159:
        r7 = r7 + 1;
    L_0x015b:
        if (r4 == r9) goto L_0x015f;
    L_0x015d:
        r7 = r7 + 1;
    L_0x015f:
        if (r5 == r9) goto L_0x0163;
    L_0x0161:
        r7 = r7 + 1;
    L_0x0163:
        if (r6 == r9) goto L_0x0167;
    L_0x0165:
        r7 = r7 + 1;
    L_0x0167:
        r1 = r13.listAdapter;
        r1.notifyItemRangeChanged(r0, r7);
    L_0x016c:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ProfileActivity.updateSharedMediaRows():void");
    }

    /* Access modifiers changed, original: protected */
    public void onTransitionAnimationStart(boolean z, boolean z2) {
        if (((!z && z2) || (z && !z2)) && this.playProfileAnimation && this.allowProfileAnimation && !this.isPulledDown) {
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
        this.extraHeight = this.initialAnimationExtraHeight * f2;
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
        this.topView.invalidate();
        needLayout();
    }

    /* Access modifiers changed, original: protected */
    public AnimatorSet onCustomTransitionAnimation(boolean z, final Runnable runnable) {
        if (!this.playProfileAnimation || !this.allowProfileAnimation || this.isPulledDown) {
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
            this.initialAnimationExtraHeight = AndroidUtilities.dpf2(88.0f);
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
                Collections.sort(this.sortedUsers, new -$$Lambda$ProfileActivity$q7OGkMLrfQsy8mGoEuiTu63G1i8(this, currentTime));
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
    public /* synthetic */ int lambda$updateOnlineCount$21$ProfileActivity(int r5, java.lang.Integer r6, java.lang.Integer r7) {
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
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ProfileActivity.lambda$updateOnlineCount$21$ProfileActivity(int, java.lang.Integer, java.lang.Integer):int");
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

    /* JADX WARNING: Removed duplicated region for block: B:98:0x01e5  */
    /* JADX WARNING: Removed duplicated region for block: B:97:0x01d3  */
    /* JADX WARNING: Missing block: B:61:0x0140, code skipped:
            if (r3.common_chats_count != 0) goto L_0x0142;
     */
    private void updateRowsIds() {
        /*
        r13 = this;
        r0 = r13.rowCount;
        r1 = 0;
        r13.rowCount = r1;
        r2 = -1;
        r13.emptyRow = r2;
        r13.infoHeaderRow = r2;
        r13.phoneRow = r2;
        r13.userInfoRow = r2;
        r13.locationRow = r2;
        r13.channelInfoRow = r2;
        r13.usernameRow = r2;
        r13.settingsTimerRow = r2;
        r13.settingsKeyRow = r2;
        r13.notificationsDividerRow = r2;
        r13.notificationsRow = r2;
        r13.infoSectionRow = r2;
        r13.settingsSectionRow = r2;
        r13.membersHeaderRow = r2;
        r13.membersStartRow = r2;
        r13.membersEndRow = r2;
        r13.addMemberRow = r2;
        r13.subscribersRow = r2;
        r13.administratorsRow = r2;
        r13.blockedUsersRow = r2;
        r13.membersSectionRow = r2;
        r13.sharedHeaderRow = r2;
        r13.photosRow = r2;
        r13.filesRow = r2;
        r13.linksRow = r2;
        r13.audioRow = r2;
        r13.voiceRow = r2;
        r13.groupsInCommonRow = r2;
        r13.sharedSectionRow = r2;
        r13.unblockRow = r2;
        r13.startSecretChatRow = r2;
        r13.leaveChannelRow = r2;
        r13.joinRow = r2;
        r13.lastSectionRow = r2;
        r3 = 0;
    L_0x004b:
        r4 = r13.lastMediaCount;
        r5 = r4.length;
        r6 = 1;
        if (r3 >= r5) goto L_0x005a;
    L_0x0051:
        r4 = r4[r3];
        if (r4 <= 0) goto L_0x0057;
    L_0x0055:
        r3 = 1;
        goto L_0x005b;
    L_0x0057:
        r3 = r3 + 1;
        goto L_0x004b;
    L_0x005a:
        r3 = 0;
    L_0x005b:
        r4 = r13.user_id;
        if (r4 == 0) goto L_0x006b;
    L_0x005f:
        r4 = org.telegram.messenger.LocaleController.isRTL;
        if (r4 == 0) goto L_0x006b;
    L_0x0063:
        r4 = r13.rowCount;
        r5 = r4 + 1;
        r13.rowCount = r5;
        r13.emptyRow = r4;
    L_0x006b:
        r4 = r13.user_id;
        r5 = 2;
        r7 = 4;
        r8 = 3;
        if (r4 == 0) goto L_0x0208;
    L_0x0072:
        r4 = r13.currentAccount;
        r4 = org.telegram.messenger.MessagesController.getInstance(r4);
        r9 = r13.user_id;
        r9 = java.lang.Integer.valueOf(r9);
        r4 = r4.getUser(r9);
        r9 = r13.userInfo;
        if (r9 == 0) goto L_0x008e;
    L_0x0086:
        r9 = r9.about;
        r9 = android.text.TextUtils.isEmpty(r9);
        if (r9 == 0) goto L_0x0098;
    L_0x008e:
        if (r4 == 0) goto L_0x009a;
    L_0x0090:
        r9 = r4.username;
        r9 = android.text.TextUtils.isEmpty(r9);
        if (r9 != 0) goto L_0x009a;
    L_0x0098:
        r9 = 1;
        goto L_0x009b;
    L_0x009a:
        r9 = 0;
    L_0x009b:
        if (r4 == 0) goto L_0x00a7;
    L_0x009d:
        r10 = r4.phone;
        r10 = android.text.TextUtils.isEmpty(r10);
        if (r10 != 0) goto L_0x00a7;
    L_0x00a5:
        r10 = 1;
        goto L_0x00a8;
    L_0x00a7:
        r10 = 0;
    L_0x00a8:
        r11 = r13.rowCount;
        r12 = r11 + 1;
        r13.rowCount = r12;
        r13.infoHeaderRow = r11;
        r11 = r13.isBot;
        if (r11 != 0) goto L_0x00c2;
    L_0x00b4:
        if (r10 != 0) goto L_0x00ba;
    L_0x00b6:
        if (r10 != 0) goto L_0x00c2;
    L_0x00b8:
        if (r9 != 0) goto L_0x00c2;
    L_0x00ba:
        r9 = r13.rowCount;
        r10 = r9 + 1;
        r13.rowCount = r10;
        r13.phoneRow = r9;
    L_0x00c2:
        r9 = r13.userInfo;
        if (r9 == 0) goto L_0x00d6;
    L_0x00c6:
        r9 = r9.about;
        r9 = android.text.TextUtils.isEmpty(r9);
        if (r9 != 0) goto L_0x00d6;
    L_0x00ce:
        r9 = r13.rowCount;
        r10 = r9 + 1;
        r13.rowCount = r10;
        r13.userInfoRow = r9;
    L_0x00d6:
        if (r4 == 0) goto L_0x00e8;
    L_0x00d8:
        r9 = r4.username;
        r9 = android.text.TextUtils.isEmpty(r9);
        if (r9 != 0) goto L_0x00e8;
    L_0x00e0:
        r9 = r13.rowCount;
        r10 = r9 + 1;
        r13.rowCount = r10;
        r13.usernameRow = r9;
    L_0x00e8:
        r9 = r13.phoneRow;
        if (r9 != r2) goto L_0x00f4;
    L_0x00ec:
        r9 = r13.userInfoRow;
        if (r9 != r2) goto L_0x00f4;
    L_0x00f0:
        r9 = r13.usernameRow;
        if (r9 == r2) goto L_0x00fc;
    L_0x00f4:
        r9 = r13.rowCount;
        r10 = r9 + 1;
        r13.rowCount = r10;
        r13.notificationsDividerRow = r9;
    L_0x00fc:
        r9 = r13.user_id;
        r10 = r13.currentAccount;
        r10 = org.telegram.messenger.UserConfig.getInstance(r10);
        r10 = r10.getClientUserId();
        if (r9 == r10) goto L_0x0112;
    L_0x010a:
        r9 = r13.rowCount;
        r10 = r9 + 1;
        r13.rowCount = r10;
        r13.notificationsRow = r9;
    L_0x0112:
        r9 = r13.rowCount;
        r10 = r9 + 1;
        r13.rowCount = r10;
        r13.infoSectionRow = r9;
        r9 = r13.currentEncryptedChat;
        r9 = r9 instanceof org.telegram.tgnet.TLRPC.TL_encryptedChat;
        if (r9 == 0) goto L_0x0138;
    L_0x0120:
        r9 = r13.rowCount;
        r10 = r9 + 1;
        r13.rowCount = r10;
        r13.settingsTimerRow = r9;
        r9 = r13.rowCount;
        r10 = r9 + 1;
        r13.rowCount = r10;
        r13.settingsKeyRow = r9;
        r9 = r13.rowCount;
        r10 = r9 + 1;
        r13.rowCount = r10;
        r13.settingsSectionRow = r9;
    L_0x0138:
        if (r3 != 0) goto L_0x0142;
    L_0x013a:
        r3 = r13.userInfo;
        if (r3 == 0) goto L_0x01b7;
    L_0x013e:
        r3 = r3.common_chats_count;
        if (r3 == 0) goto L_0x01b7;
    L_0x0142:
        r3 = r13.rowCount;
        r9 = r3 + 1;
        r13.rowCount = r9;
        r13.sharedHeaderRow = r3;
        r3 = r13.lastMediaCount;
        r3 = r3[r1];
        if (r3 <= 0) goto L_0x0159;
    L_0x0150:
        r3 = r13.rowCount;
        r9 = r3 + 1;
        r13.rowCount = r9;
        r13.photosRow = r3;
        goto L_0x015b;
    L_0x0159:
        r13.photosRow = r2;
    L_0x015b:
        r3 = r13.lastMediaCount;
        r3 = r3[r6];
        if (r3 <= 0) goto L_0x016a;
    L_0x0161:
        r3 = r13.rowCount;
        r6 = r3 + 1;
        r13.rowCount = r6;
        r13.filesRow = r3;
        goto L_0x016c;
    L_0x016a:
        r13.filesRow = r2;
    L_0x016c:
        r3 = r13.lastMediaCount;
        r3 = r3[r8];
        if (r3 <= 0) goto L_0x017b;
    L_0x0172:
        r3 = r13.rowCount;
        r6 = r3 + 1;
        r13.rowCount = r6;
        r13.linksRow = r3;
        goto L_0x017d;
    L_0x017b:
        r13.linksRow = r2;
    L_0x017d:
        r3 = r13.lastMediaCount;
        r3 = r3[r7];
        if (r3 <= 0) goto L_0x018c;
    L_0x0183:
        r3 = r13.rowCount;
        r6 = r3 + 1;
        r13.rowCount = r6;
        r13.audioRow = r3;
        goto L_0x018e;
    L_0x018c:
        r13.audioRow = r2;
    L_0x018e:
        r3 = r13.lastMediaCount;
        r3 = r3[r5];
        if (r3 <= 0) goto L_0x019d;
    L_0x0194:
        r2 = r13.rowCount;
        r3 = r2 + 1;
        r13.rowCount = r3;
        r13.voiceRow = r2;
        goto L_0x019f;
    L_0x019d:
        r13.voiceRow = r2;
    L_0x019f:
        r2 = r13.userInfo;
        if (r2 == 0) goto L_0x01af;
    L_0x01a3:
        r2 = r2.common_chats_count;
        if (r2 == 0) goto L_0x01af;
    L_0x01a7:
        r2 = r13.rowCount;
        r3 = r2 + 1;
        r13.rowCount = r3;
        r13.groupsInCommonRow = r2;
    L_0x01af:
        r2 = r13.rowCount;
        r3 = r2 + 1;
        r13.rowCount = r3;
        r13.sharedSectionRow = r2;
    L_0x01b7:
        if (r4 == 0) goto L_0x046c;
    L_0x01b9:
        r2 = r13.isBot;
        if (r2 != 0) goto L_0x046c;
    L_0x01bd:
        r2 = r13.currentEncryptedChat;
        if (r2 != 0) goto L_0x046c;
    L_0x01c1:
        r2 = r4.id;
        r3 = r13.currentAccount;
        r3 = org.telegram.messenger.UserConfig.getInstance(r3);
        r3 = r3.getClientUserId();
        if (r2 == r3) goto L_0x046c;
    L_0x01cf:
        r2 = r13.userBlocked;
        if (r2 == 0) goto L_0x01e5;
    L_0x01d3:
        r2 = r13.rowCount;
        r3 = r2 + 1;
        r13.rowCount = r3;
        r13.unblockRow = r2;
        r2 = r13.rowCount;
        r3 = r2 + 1;
        r13.rowCount = r3;
        r13.lastSectionRow = r2;
        goto L_0x046c;
    L_0x01e5:
        r2 = r4.id;
        r3 = 333000; // 0x514c8 float:4.66632E-40 double:1.64524E-318;
        if (r2 == r3) goto L_0x046c;
    L_0x01ec:
        r3 = 777000; // 0xbdb28 float:1.088809E-39 double:3.83889E-318;
        if (r2 == r3) goto L_0x046c;
    L_0x01f1:
        r3 = 42777; // 0xa719 float:5.9943E-41 double:2.11346E-319;
        if (r2 == r3) goto L_0x046c;
    L_0x01f6:
        r2 = r13.rowCount;
        r3 = r2 + 1;
        r13.rowCount = r3;
        r13.startSecretChatRow = r2;
        r2 = r13.rowCount;
        r3 = r2 + 1;
        r13.rowCount = r3;
        r13.lastSectionRow = r2;
        goto L_0x046c;
    L_0x0208:
        r4 = r13.chat_id;
        if (r4 == 0) goto L_0x046c;
    L_0x020c:
        if (r4 <= 0) goto L_0x0433;
    L_0x020e:
        r4 = r13.chatInfo;
        if (r4 == 0) goto L_0x0222;
    L_0x0212:
        r4 = r4.about;
        r4 = android.text.TextUtils.isEmpty(r4);
        if (r4 == 0) goto L_0x022c;
    L_0x021a:
        r4 = r13.chatInfo;
        r4 = r4.location;
        r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_channelLocation;
        if (r4 != 0) goto L_0x022c;
    L_0x0222:
        r4 = r13.currentChat;
        r4 = r4.username;
        r4 = android.text.TextUtils.isEmpty(r4);
        if (r4 != 0) goto L_0x026a;
    L_0x022c:
        r4 = r13.rowCount;
        r9 = r4 + 1;
        r13.rowCount = r9;
        r13.infoHeaderRow = r4;
        r4 = r13.chatInfo;
        if (r4 == 0) goto L_0x0258;
    L_0x0238:
        r4 = r4.about;
        r4 = android.text.TextUtils.isEmpty(r4);
        if (r4 != 0) goto L_0x0248;
    L_0x0240:
        r4 = r13.rowCount;
        r9 = r4 + 1;
        r13.rowCount = r9;
        r13.channelInfoRow = r4;
    L_0x0248:
        r4 = r13.chatInfo;
        r4 = r4.location;
        r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_channelLocation;
        if (r4 == 0) goto L_0x0258;
    L_0x0250:
        r4 = r13.rowCount;
        r9 = r4 + 1;
        r13.rowCount = r9;
        r13.locationRow = r4;
    L_0x0258:
        r4 = r13.currentChat;
        r4 = r4.username;
        r4 = android.text.TextUtils.isEmpty(r4);
        if (r4 != 0) goto L_0x026a;
    L_0x0262:
        r4 = r13.rowCount;
        r9 = r4 + 1;
        r13.rowCount = r9;
        r13.usernameRow = r4;
    L_0x026a:
        r4 = r13.infoHeaderRow;
        if (r4 == r2) goto L_0x0276;
    L_0x026e:
        r4 = r13.rowCount;
        r9 = r4 + 1;
        r13.rowCount = r9;
        r13.notificationsDividerRow = r4;
    L_0x0276:
        r4 = r13.rowCount;
        r9 = r4 + 1;
        r13.rowCount = r9;
        r13.notificationsRow = r4;
        r4 = r13.rowCount;
        r9 = r4 + 1;
        r13.rowCount = r9;
        r13.infoSectionRow = r4;
        r4 = r13.currentChat;
        r4 = org.telegram.messenger.ChatObject.isChannel(r4);
        if (r4 == 0) goto L_0x02d2;
    L_0x028e:
        r4 = r13.currentChat;
        r9 = r4.megagroup;
        if (r9 != 0) goto L_0x02d2;
    L_0x0294:
        r9 = r13.chatInfo;
        if (r9 == 0) goto L_0x02d2;
    L_0x0298:
        r4 = r4.creator;
        if (r4 != 0) goto L_0x02a0;
    L_0x029c:
        r4 = r9.can_view_participants;
        if (r4 == 0) goto L_0x02d2;
    L_0x02a0:
        r4 = r13.rowCount;
        r9 = r4 + 1;
        r13.rowCount = r9;
        r13.membersHeaderRow = r4;
        r4 = r13.rowCount;
        r9 = r4 + 1;
        r13.rowCount = r9;
        r13.subscribersRow = r4;
        r4 = r13.rowCount;
        r9 = r4 + 1;
        r13.rowCount = r9;
        r13.administratorsRow = r4;
        r4 = r13.chatInfo;
        r9 = r4.banned_count;
        if (r9 != 0) goto L_0x02c2;
    L_0x02be:
        r4 = r4.kicked_count;
        if (r4 == 0) goto L_0x02ca;
    L_0x02c2:
        r4 = r13.rowCount;
        r9 = r4 + 1;
        r13.rowCount = r9;
        r13.blockedUsersRow = r4;
    L_0x02ca:
        r4 = r13.rowCount;
        r9 = r4 + 1;
        r13.rowCount = r9;
        r13.membersSectionRow = r4;
    L_0x02d2:
        if (r3 == 0) goto L_0x0339;
    L_0x02d4:
        r3 = r13.rowCount;
        r4 = r3 + 1;
        r13.rowCount = r4;
        r13.sharedHeaderRow = r3;
        r3 = r13.lastMediaCount;
        r3 = r3[r1];
        if (r3 <= 0) goto L_0x02eb;
    L_0x02e2:
        r3 = r13.rowCount;
        r4 = r3 + 1;
        r13.rowCount = r4;
        r13.photosRow = r3;
        goto L_0x02ed;
    L_0x02eb:
        r13.photosRow = r2;
    L_0x02ed:
        r3 = r13.lastMediaCount;
        r3 = r3[r6];
        if (r3 <= 0) goto L_0x02fc;
    L_0x02f3:
        r3 = r13.rowCount;
        r4 = r3 + 1;
        r13.rowCount = r4;
        r13.filesRow = r3;
        goto L_0x02fe;
    L_0x02fc:
        r13.filesRow = r2;
    L_0x02fe:
        r3 = r13.lastMediaCount;
        r3 = r3[r8];
        if (r3 <= 0) goto L_0x030d;
    L_0x0304:
        r3 = r13.rowCount;
        r4 = r3 + 1;
        r13.rowCount = r4;
        r13.linksRow = r3;
        goto L_0x030f;
    L_0x030d:
        r13.linksRow = r2;
    L_0x030f:
        r3 = r13.lastMediaCount;
        r3 = r3[r7];
        if (r3 <= 0) goto L_0x031e;
    L_0x0315:
        r3 = r13.rowCount;
        r4 = r3 + 1;
        r13.rowCount = r4;
        r13.audioRow = r3;
        goto L_0x0320;
    L_0x031e:
        r13.audioRow = r2;
    L_0x0320:
        r3 = r13.lastMediaCount;
        r3 = r3[r5];
        if (r3 <= 0) goto L_0x032f;
    L_0x0326:
        r3 = r13.rowCount;
        r4 = r3 + 1;
        r13.rowCount = r4;
        r13.voiceRow = r3;
        goto L_0x0331;
    L_0x032f:
        r13.voiceRow = r2;
    L_0x0331:
        r3 = r13.rowCount;
        r4 = r3 + 1;
        r13.rowCount = r4;
        r13.sharedSectionRow = r3;
    L_0x0339:
        r3 = r13.currentChat;
        r3 = org.telegram.messenger.ChatObject.isChannel(r3);
        if (r3 == 0) goto L_0x03e9;
    L_0x0341:
        r3 = r13.currentChat;
        r4 = r3.creator;
        if (r4 != 0) goto L_0x0363;
    L_0x0347:
        r4 = r3.left;
        if (r4 != 0) goto L_0x0363;
    L_0x034b:
        r4 = r3.kicked;
        if (r4 != 0) goto L_0x0363;
    L_0x034f:
        r3 = r3.megagroup;
        if (r3 != 0) goto L_0x0363;
    L_0x0353:
        r3 = r13.rowCount;
        r4 = r3 + 1;
        r13.rowCount = r4;
        r13.leaveChannelRow = r3;
        r3 = r13.rowCount;
        r4 = r3 + 1;
        r13.rowCount = r4;
        r13.lastSectionRow = r3;
    L_0x0363:
        r3 = r13.chatInfo;
        if (r3 == 0) goto L_0x03c9;
    L_0x0367:
        r4 = r13.currentChat;
        r4 = r4.megagroup;
        if (r4 == 0) goto L_0x03c9;
    L_0x036d:
        r3 = r3.participants;
        if (r3 == 0) goto L_0x03c9;
    L_0x0371:
        r3 = r3.participants;
        r3 = r3.isEmpty();
        if (r3 != 0) goto L_0x03c9;
    L_0x0379:
        r3 = r13.currentChat;
        r3 = org.telegram.messenger.ChatObject.isNotInChat(r3);
        if (r3 != 0) goto L_0x03a6;
    L_0x0381:
        r3 = r13.currentChat;
        r4 = r3.megagroup;
        if (r4 == 0) goto L_0x03a6;
    L_0x0387:
        r3 = org.telegram.messenger.ChatObject.canAddUsers(r3);
        if (r3 == 0) goto L_0x03a6;
    L_0x038d:
        r3 = r13.chatInfo;
        if (r3 == 0) goto L_0x039d;
    L_0x0391:
        r3 = r3.participants_count;
        r4 = r13.currentAccount;
        r4 = org.telegram.messenger.MessagesController.getInstance(r4);
        r4 = r4.maxMegagroupCount;
        if (r3 >= r4) goto L_0x03a6;
    L_0x039d:
        r3 = r13.rowCount;
        r4 = r3 + 1;
        r13.rowCount = r4;
        r13.addMemberRow = r3;
        goto L_0x03ae;
    L_0x03a6:
        r3 = r13.rowCount;
        r4 = r3 + 1;
        r13.rowCount = r4;
        r13.membersHeaderRow = r3;
    L_0x03ae:
        r3 = r13.rowCount;
        r13.membersStartRow = r3;
        r4 = r13.chatInfo;
        r4 = r4.participants;
        r4 = r4.participants;
        r4 = r4.size();
        r3 = r3 + r4;
        r13.rowCount = r3;
        r3 = r13.rowCount;
        r13.membersEndRow = r3;
        r4 = r3 + 1;
        r13.rowCount = r4;
        r13.membersSectionRow = r3;
    L_0x03c9:
        r3 = r13.lastSectionRow;
        if (r3 != r2) goto L_0x046c;
    L_0x03cd:
        r2 = r13.currentChat;
        r3 = r2.left;
        if (r3 == 0) goto L_0x046c;
    L_0x03d3:
        r2 = r2.kicked;
        if (r2 != 0) goto L_0x046c;
    L_0x03d7:
        r2 = r13.rowCount;
        r3 = r2 + 1;
        r13.rowCount = r3;
        r13.joinRow = r2;
        r2 = r13.rowCount;
        r3 = r2 + 1;
        r13.rowCount = r3;
        r13.lastSectionRow = r2;
        goto L_0x046c;
    L_0x03e9:
        r2 = r13.chatInfo;
        if (r2 == 0) goto L_0x046c;
    L_0x03ed:
        r2 = r2.participants;
        r2 = r2 instanceof org.telegram.tgnet.TLRPC.TL_chatParticipantsForbidden;
        if (r2 != 0) goto L_0x046c;
    L_0x03f3:
        r2 = r13.currentChat;
        r2 = org.telegram.messenger.ChatObject.canAddUsers(r2);
        if (r2 != 0) goto L_0x040f;
    L_0x03fb:
        r2 = r13.currentChat;
        r2 = r2.default_banned_rights;
        if (r2 == 0) goto L_0x040f;
    L_0x0401:
        r2 = r2.invite_users;
        if (r2 != 0) goto L_0x0406;
    L_0x0405:
        goto L_0x040f;
    L_0x0406:
        r2 = r13.rowCount;
        r3 = r2 + 1;
        r13.rowCount = r3;
        r13.membersHeaderRow = r2;
        goto L_0x0417;
    L_0x040f:
        r2 = r13.rowCount;
        r3 = r2 + 1;
        r13.rowCount = r3;
        r13.addMemberRow = r2;
    L_0x0417:
        r2 = r13.rowCount;
        r13.membersStartRow = r2;
        r3 = r13.chatInfo;
        r3 = r3.participants;
        r3 = r3.participants;
        r3 = r3.size();
        r2 = r2 + r3;
        r13.rowCount = r2;
        r2 = r13.rowCount;
        r13.membersEndRow = r2;
        r3 = r2 + 1;
        r13.rowCount = r3;
        r13.membersSectionRow = r2;
        goto L_0x046c;
    L_0x0433:
        r2 = r13.currentChat;
        r2 = org.telegram.messenger.ChatObject.isChannel(r2);
        if (r2 != 0) goto L_0x046c;
    L_0x043b:
        r2 = r13.chatInfo;
        if (r2 == 0) goto L_0x046c;
    L_0x043f:
        r2 = r2.participants;
        r3 = r2 instanceof org.telegram.tgnet.TLRPC.TL_chatParticipantsForbidden;
        if (r3 != 0) goto L_0x046c;
    L_0x0445:
        r3 = r13.rowCount;
        r4 = r3 + 1;
        r13.rowCount = r4;
        r13.membersHeaderRow = r3;
        r3 = r13.rowCount;
        r13.membersStartRow = r3;
        r2 = r2.participants;
        r2 = r2.size();
        r3 = r3 + r2;
        r13.rowCount = r3;
        r2 = r13.rowCount;
        r13.membersEndRow = r2;
        r3 = r2 + 1;
        r13.rowCount = r3;
        r13.membersSectionRow = r2;
        r2 = r13.rowCount;
        r3 = r2 + 1;
        r13.rowCount = r3;
        r13.addMemberRow = r2;
    L_0x046c:
        r2 = r13.rowCount;
        r3 = r2 + 1;
        r13.rowCount = r3;
        r13.bottomPaddingRow = r2;
        r2 = r13.actionBar;
        if (r2 == 0) goto L_0x048a;
    L_0x0478:
        r2 = org.telegram.ui.ActionBar.ActionBar.getCurrentActionBarHeight();
        r3 = r13.actionBar;
        r3 = r3.getOccupyStatusBar();
        if (r3 == 0) goto L_0x0487;
    L_0x0484:
        r3 = org.telegram.messenger.AndroidUtilities.statusBarHeight;
        goto L_0x0488;
    L_0x0487:
        r3 = 0;
    L_0x0488:
        r2 = r2 + r3;
        goto L_0x048b;
    L_0x048a:
        r2 = 0;
    L_0x048b:
        r3 = r13.listView;
        if (r3 == 0) goto L_0x04a7;
    L_0x048f:
        r3 = r13.rowCount;
        if (r0 > r3) goto L_0x04a7;
    L_0x0493:
        r0 = r13.listContentHeight;
        if (r0 == 0) goto L_0x04a9;
    L_0x0497:
        r0 = r0 + r2;
        r2 = NUM; // 0x42b00000 float:88.0 double:5.52775759E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r0 = r0 + r2;
        r2 = r13.listView;
        r2 = r2.getMeasuredHeight();
        if (r0 >= r2) goto L_0x04a9;
    L_0x04a7:
        r13.lastMeasuredContentWidth = r1;
    L_0x04a9:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ProfileActivity.updateRowsIds():void");
    }

    private Drawable getScamDrawable() {
        if (this.scamDrawable == null) {
            this.scamDrawable = new ScamDrawable(11);
            this.scamDrawable.setColor(Theme.getColor("avatar_subtitleInProfileBlue"));
        }
        return this.scamDrawable;
    }

    private Drawable getLockIconDrawable() {
        if (this.lockIconDrawable == null) {
            this.lockIconDrawable = Theme.chat_lockIconDrawable.getConstantState().newDrawable().mutate();
        }
        return this.lockIconDrawable;
    }

    private Drawable getVerifiedCrossfadeDrawable() {
        if (this.verifiedCrossfadeDrawable == null) {
            this.verifiedDrawable = Theme.profile_verifiedDrawable.getConstantState().newDrawable().mutate();
            this.verifiedCheckDrawable = Theme.profile_verifiedCheckDrawable.getConstantState().newDrawable().mutate();
            this.verifiedCrossfadeDrawable = new CrossfadeDrawable(new CombinedDrawable(this.verifiedDrawable, this.verifiedCheckDrawable), ContextCompat.getDrawable(getParentActivity(), NUM));
        }
        return this.verifiedCrossfadeDrawable;
    }

    /* JADX WARNING: Removed duplicated region for block: B:77:0x01cd  */
    /* JADX WARNING: Removed duplicated region for block: B:76:0x01c8  */
    /* JADX WARNING: Removed duplicated region for block: B:88:0x01f4  */
    /* JADX WARNING: Removed duplicated region for block: B:79:0x01d0  */
    /* JADX WARNING: Removed duplicated region for block: B:228:0x0526  */
    /* JADX WARNING: Removed duplicated region for block: B:217:0x04a7  */
    /* JADX WARNING: Removed duplicated region for block: B:164:0x03d4  */
    /* JADX WARNING: Removed duplicated region for block: B:236:0x056e  */
    /* JADX WARNING: Removed duplicated region for block: B:240:0x057a  */
    /* JADX WARNING: Removed duplicated region for block: B:239:0x0575  */
    private void updateProfileData() {
        /*
        r24 = this;
        r0 = r24;
        r1 = r0.avatarImage;
        if (r1 == 0) goto L_0x05b3;
    L_0x0006:
        r1 = r0.nameTextView;
        if (r1 != 0) goto L_0x000c;
    L_0x000a:
        goto L_0x05b3;
    L_0x000c:
        r1 = r0.currentAccount;
        r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r1);
        r1 = r1.getConnectionState();
        r2 = 2;
        r4 = 1;
        if (r1 != r2) goto L_0x0024;
    L_0x001a:
        r1 = NUM; // 0x7f0e0c1d float:1.8881327E38 double:1.0531636887E-314;
        r5 = "WaitingForNetwork";
        r1 = org.telegram.messenger.LocaleController.getString(r5, r1);
        goto L_0x004b;
    L_0x0024:
        if (r1 != r4) goto L_0x0030;
    L_0x0026:
        r1 = NUM; // 0x7f0e0327 float:1.8876674E38 double:1.0531625553E-314;
        r5 = "Connecting";
        r1 = org.telegram.messenger.LocaleController.getString(r5, r1);
        goto L_0x004b;
    L_0x0030:
        r5 = 5;
        if (r1 != r5) goto L_0x003d;
    L_0x0033:
        r1 = NUM; // 0x7f0e0b86 float:1.888102E38 double:1.053163614E-314;
        r5 = "Updating";
        r1 = org.telegram.messenger.LocaleController.getString(r5, r1);
        goto L_0x004b;
    L_0x003d:
        r5 = 4;
        if (r1 != r5) goto L_0x004a;
    L_0x0040:
        r1 = NUM; // 0x7f0e0329 float:1.8876678E38 double:1.0531625563E-314;
        r5 = "ConnectingToProxy";
        r1 = org.telegram.messenger.LocaleController.getString(r5, r1);
        goto L_0x004b;
    L_0x004a:
        r1 = 0;
    L_0x004b:
        r5 = r0.user_id;
        r6 = "50_50";
        r7 = 0;
        if (r5 == 0) goto L_0x0229;
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
        r10 = org.telegram.messenger.ImageLocation.getForUser(r5, r4);
        r9 = org.telegram.messenger.ImageLocation.getForUser(r5, r7);
        r11 = r0.avatarsViewPager;
        r11.initIfEmpty(r10, r9);
        r11 = r0.avatarImage;
        r12 = r0.avatarDrawable;
        r11.setImage(r9, r6, r12, r5);
        r6 = r0.currentAccount;
        r9 = org.telegram.messenger.FileLoader.getInstance(r6);
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
        if (r9 != r10) goto L_0x00bb;
    L_0x00a2:
        r6 = NUM; // 0x7f0e02c2 float:1.887647E38 double:1.0531625054E-314;
        r9 = "ChatYourSelf";
        r6 = org.telegram.messenger.LocaleController.getString(r9, r6);
        r9 = NUM; // 0x7f0e02c7 float:1.887648E38 double:1.053162508E-314;
        r10 = "ChatYourSelfName";
        r9 = org.telegram.messenger.LocaleController.getString(r10, r9);
        r23 = r9;
        r9 = r6;
        r6 = r23;
        goto L_0x0126;
    L_0x00bb:
        r9 = r5.id;
        r10 = 333000; // 0x514c8 float:4.66632E-40 double:1.64524E-318;
        if (r9 == r10) goto L_0x011d;
    L_0x00c2:
        r10 = 777000; // 0xbdb28 float:1.088809E-39 double:3.83889E-318;
        if (r9 == r10) goto L_0x011d;
    L_0x00c7:
        r10 = 42777; // 0xa719 float:5.9943E-41 double:2.11346E-319;
        if (r9 != r10) goto L_0x00cd;
    L_0x00cc:
        goto L_0x011d;
    L_0x00cd:
        r9 = org.telegram.messenger.MessagesController.isSupportUser(r5);
        if (r9 == 0) goto L_0x00dd;
    L_0x00d3:
        r9 = NUM; // 0x7f0e0ad5 float:1.8880662E38 double:1.0531635267E-314;
        r10 = "SupportStatus";
        r9 = org.telegram.messenger.LocaleController.getString(r10, r9);
        goto L_0x0126;
    L_0x00dd:
        r9 = r0.isBot;
        if (r9 == 0) goto L_0x00eb;
    L_0x00e1:
        r9 = NUM; // 0x7f0e01ef float:1.8876042E38 double:1.053162401E-314;
        r10 = "Bot";
        r9 = org.telegram.messenger.LocaleController.getString(r10, r9);
        goto L_0x0126;
    L_0x00eb:
        r9 = r0.isOnline;
        r9[r7] = r7;
        r10 = r0.currentAccount;
        r9 = org.telegram.messenger.LocaleController.formatUserStatus(r10, r5, r9);
        r10 = r0.onlineTextView;
        r10 = r10[r4];
        if (r10 == 0) goto L_0x0126;
    L_0x00fb:
        r10 = r0.isOnline;
        r10 = r10[r7];
        if (r10 == 0) goto L_0x0104;
    L_0x0101:
        r10 = "profile_status";
        goto L_0x0106;
    L_0x0104:
        r10 = "avatar_subtitleInProfileBlue";
    L_0x0106:
        r11 = r0.onlineTextView;
        r11 = r11[r4];
        r11.setTag(r10);
        r11 = r0.isPulledDown;
        if (r11 != 0) goto L_0x0126;
    L_0x0111:
        r11 = r0.onlineTextView;
        r11 = r11[r4];
        r10 = org.telegram.ui.ActionBar.Theme.getColor(r10);
        r11.setTextColor(r10);
        goto L_0x0126;
    L_0x011d:
        r9 = NUM; // 0x7f0e0a34 float:1.8880335E38 double:1.053163447E-314;
        r10 = "ServiceNotifications";
        r9 = org.telegram.messenger.LocaleController.getString(r10, r9);
    L_0x0126:
        r10 = 0;
    L_0x0127:
        if (r10 >= r2) goto L_0x0219;
    L_0x0129:
        r11 = r0.nameTextView;
        r11 = r11[r10];
        if (r11 != 0) goto L_0x0131;
    L_0x012f:
        goto L_0x0215;
    L_0x0131:
        if (r10 != 0) goto L_0x01aa;
    L_0x0133:
        r11 = r5.id;
        r12 = r0.currentAccount;
        r12 = org.telegram.messenger.UserConfig.getInstance(r12);
        r12 = r12.getClientUserId();
        if (r11 == r12) goto L_0x01aa;
    L_0x0141:
        r11 = r5.id;
        r12 = r11 / 1000;
        r13 = 777; // 0x309 float:1.089E-42 double:3.84E-321;
        if (r12 == r13) goto L_0x01aa;
    L_0x0149:
        r11 = r11 / 1000;
        r12 = 333; // 0x14d float:4.67E-43 double:1.645E-321;
        if (r11 == r12) goto L_0x01aa;
    L_0x014f:
        r11 = r5.phone;
        if (r11 == 0) goto L_0x01aa;
    L_0x0153:
        r11 = r11.length();
        if (r11 == 0) goto L_0x01aa;
    L_0x0159:
        r11 = r0.currentAccount;
        r11 = org.telegram.messenger.ContactsController.getInstance(r11);
        r11 = r11.contactsDict;
        r12 = r5.id;
        r12 = java.lang.Integer.valueOf(r12);
        r11 = r11.get(r12);
        if (r11 != 0) goto L_0x01aa;
    L_0x016d:
        r11 = r0.currentAccount;
        r11 = org.telegram.messenger.ContactsController.getInstance(r11);
        r11 = r11.contactsDict;
        r11 = r11.size();
        if (r11 != 0) goto L_0x0187;
    L_0x017b:
        r11 = r0.currentAccount;
        r11 = org.telegram.messenger.ContactsController.getInstance(r11);
        r11 = r11.isLoadingContacts();
        if (r11 != 0) goto L_0x01aa;
    L_0x0187:
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
        r12.setText(r11);
        goto L_0x01b1;
    L_0x01aa:
        r11 = r0.nameTextView;
        r11 = r11[r10];
        r11.setText(r6);
    L_0x01b1:
        if (r10 != 0) goto L_0x01bd;
    L_0x01b3:
        if (r1 == 0) goto L_0x01bd;
    L_0x01b5:
        r11 = r0.onlineTextView;
        r11 = r11[r10];
        r11.setText(r1);
        goto L_0x01c4;
    L_0x01bd:
        r11 = r0.onlineTextView;
        r11 = r11[r10];
        r11.setText(r9);
    L_0x01c4:
        r11 = r0.currentEncryptedChat;
        if (r11 == 0) goto L_0x01cd;
    L_0x01c8:
        r11 = r24.getLockIconDrawable();
        goto L_0x01ce;
    L_0x01cd:
        r11 = 0;
    L_0x01ce:
        if (r10 != 0) goto L_0x01f4;
    L_0x01d0:
        r12 = r5.scam;
        if (r12 == 0) goto L_0x01d9;
    L_0x01d4:
        r12 = r24.getScamDrawable();
        goto L_0x0207;
    L_0x01d9:
        r12 = r0.currentAccount;
        r12 = org.telegram.messenger.MessagesController.getInstance(r12);
        r13 = r0.dialog_id;
        r15 = 0;
        r17 = (r13 > r15 ? 1 : (r13 == r15 ? 0 : -1));
        if (r17 == 0) goto L_0x01e8;
    L_0x01e7:
        goto L_0x01eb;
    L_0x01e8:
        r13 = r0.user_id;
        r13 = (long) r13;
    L_0x01eb:
        r12 = r12.isDialogMuted(r13);
        if (r12 == 0) goto L_0x0206;
    L_0x01f1:
        r12 = org.telegram.ui.ActionBar.Theme.chat_muteIconDrawable;
        goto L_0x0207;
    L_0x01f4:
        r12 = r5.scam;
        if (r12 == 0) goto L_0x01fd;
    L_0x01f8:
        r12 = r24.getScamDrawable();
        goto L_0x0207;
    L_0x01fd:
        r12 = r5.verified;
        if (r12 == 0) goto L_0x0206;
    L_0x0201:
        r12 = r24.getVerifiedCrossfadeDrawable();
        goto L_0x0207;
    L_0x0206:
        r12 = 0;
    L_0x0207:
        r13 = r0.nameTextView;
        r13 = r13[r10];
        r13.setLeftDrawable(r11);
        r11 = r0.nameTextView;
        r11 = r11[r10];
        r11.setRightDrawable(r12);
    L_0x0215:
        r10 = r10 + 1;
        goto L_0x0127;
    L_0x0219:
        r1 = r0.avatarImage;
        r1 = r1.getImageReceiver();
        r2 = org.telegram.ui.PhotoViewer.isShowingImage(r8);
        r2 = r2 ^ r4;
        r1.setVisible(r2, r7);
        goto L_0x05b3;
    L_0x0229:
        r5 = r0.chat_id;
        if (r5 == 0) goto L_0x05b3;
    L_0x022d:
        r5 = r0.currentAccount;
        r5 = org.telegram.messenger.MessagesController.getInstance(r5);
        r8 = r0.chat_id;
        r8 = java.lang.Integer.valueOf(r8);
        r5 = r5.getChat(r8);
        if (r5 == 0) goto L_0x0242;
    L_0x023f:
        r0.currentChat = r5;
        goto L_0x0244;
    L_0x0242:
        r5 = r0.currentChat;
    L_0x0244:
        r10 = r5;
        r5 = org.telegram.messenger.ChatObject.isChannel(r10);
        r8 = "MegaPublic";
        r11 = "MegaPrivate";
        r13 = "MegaLocation";
        r14 = "OnlineCount";
        r15 = "%s, %s";
        r3 = "Subscribers";
        r9 = "Members";
        if (r5 == 0) goto L_0x037e;
    L_0x0259:
        r5 = r0.chatInfo;
        if (r5 == 0) goto L_0x0348;
    L_0x025d:
        r12 = r0.currentChat;
        r7 = r12.megagroup;
        if (r7 != 0) goto L_0x0275;
    L_0x0263:
        r5 = r5.participants_count;
        if (r5 == 0) goto L_0x0348;
    L_0x0267:
        r5 = org.telegram.messenger.ChatObject.hasAdminRights(r12);
        if (r5 != 0) goto L_0x0348;
    L_0x026d:
        r5 = r0.chatInfo;
        r5 = r5.can_view_participants;
        if (r5 == 0) goto L_0x0275;
    L_0x0273:
        goto L_0x0348;
    L_0x0275:
        r5 = r0.currentChat;
        r5 = r5.megagroup;
        if (r5 == 0) goto L_0x030c;
    L_0x027b:
        r5 = r0.onlineCount;
        if (r5 <= r4) goto L_0x02c6;
    L_0x027f:
        r5 = r0.chatInfo;
        r5 = r5.participants_count;
        if (r5 == 0) goto L_0x02c6;
    L_0x0285:
        r7 = new java.lang.Object[r2];
        r5 = org.telegram.messenger.LocaleController.formatPluralString(r9, r5);
        r12 = 0;
        r7[r12] = r5;
        r5 = r0.onlineCount;
        r12 = r0.chatInfo;
        r12 = r12.participants_count;
        r5 = java.lang.Math.min(r5, r12);
        r5 = org.telegram.messenger.LocaleController.formatPluralString(r14, r5);
        r7[r4] = r5;
        r5 = java.lang.String.format(r15, r7);
        r7 = new java.lang.Object[r2];
        r12 = r0.chatInfo;
        r12 = r12.participants_count;
        r12 = org.telegram.messenger.LocaleController.formatPluralStringComma(r9, r12);
        r18 = 0;
        r7[r18] = r12;
        r12 = r0.onlineCount;
        r2 = r0.chatInfo;
        r2 = r2.participants_count;
        r2 = java.lang.Math.min(r12, r2);
        r2 = org.telegram.messenger.LocaleController.formatPluralStringComma(r14, r2);
        r7[r4] = r2;
        r2 = java.lang.String.format(r15, r7);
        goto L_0x0341;
    L_0x02c6:
        r2 = r0.chatInfo;
        r2 = r2.participants_count;
        if (r2 != 0) goto L_0x02ff;
    L_0x02cc:
        r2 = r10.has_geo;
        if (r2 == 0) goto L_0x02dd;
    L_0x02d0:
        r2 = NUM; // 0x7f0e0638 float:1.8878266E38 double:1.053162943E-314;
        r5 = org.telegram.messenger.LocaleController.getString(r13, r2);
        r5 = r5.toLowerCase();
        goto L_0x038d;
    L_0x02dd:
        r2 = r10.username;
        r2 = android.text.TextUtils.isEmpty(r2);
        if (r2 != 0) goto L_0x02f2;
    L_0x02e5:
        r2 = NUM; // 0x7f0e063c float:1.8878275E38 double:1.053162945E-314;
        r2 = org.telegram.messenger.LocaleController.getString(r8, r2);
        r5 = r2.toLowerCase();
        goto L_0x038d;
    L_0x02f2:
        r2 = NUM; // 0x7f0e0639 float:1.8878269E38 double:1.0531629437E-314;
        r5 = org.telegram.messenger.LocaleController.getString(r11, r2);
        r5 = r5.toLowerCase();
        goto L_0x038d;
    L_0x02ff:
        r5 = org.telegram.messenger.LocaleController.formatPluralString(r9, r2);
        r2 = r0.chatInfo;
        r2 = r2.participants_count;
        r2 = org.telegram.messenger.LocaleController.formatPluralStringComma(r9, r2);
        goto L_0x0341;
    L_0x030c:
        r2 = new int[r4];
        r5 = r0.chatInfo;
        r5 = r5.participants_count;
        org.telegram.messenger.LocaleController.formatShortNumber(r5, r2);
        r2 = r0.currentChat;
        r2 = r2.megagroup;
        if (r2 == 0) goto L_0x032c;
    L_0x031b:
        r2 = r0.chatInfo;
        r2 = r2.participants_count;
        r2 = org.telegram.messenger.LocaleController.formatPluralString(r9, r2);
        r5 = r0.chatInfo;
        r5 = r5.participants_count;
        r5 = org.telegram.messenger.LocaleController.formatPluralStringComma(r9, r5);
        goto L_0x033c;
    L_0x032c:
        r2 = r0.chatInfo;
        r2 = r2.participants_count;
        r2 = org.telegram.messenger.LocaleController.formatPluralString(r3, r2);
        r5 = r0.chatInfo;
        r5 = r5.participants_count;
        r5 = org.telegram.messenger.LocaleController.formatPluralStringComma(r3, r5);
    L_0x033c:
        r23 = r5;
        r5 = r2;
        r2 = r23;
    L_0x0341:
        r23 = r5;
        r5 = r2;
        r2 = r23;
        goto L_0x03cf;
    L_0x0348:
        r2 = r0.currentChat;
        r2 = r2.megagroup;
        if (r2 == 0) goto L_0x035c;
    L_0x034e:
        r2 = NUM; // 0x7f0e05ee float:1.8878116E38 double:1.0531629066E-314;
        r5 = "Loading";
        r2 = org.telegram.messenger.LocaleController.getString(r5, r2);
        r5 = r2.toLowerCase();
        goto L_0x038d;
    L_0x035c:
        r2 = r10.flags;
        r2 = r2 & 64;
        if (r2 == 0) goto L_0x0370;
    L_0x0362:
        r2 = NUM; // 0x7f0e0285 float:1.8876346E38 double:1.0531624753E-314;
        r5 = "ChannelPublic";
        r2 = org.telegram.messenger.LocaleController.getString(r5, r2);
        r5 = r2.toLowerCase();
        goto L_0x038d;
    L_0x0370:
        r2 = NUM; // 0x7f0e0282 float:1.887634E38 double:1.053162474E-314;
        r5 = "ChannelPrivate";
        r2 = org.telegram.messenger.LocaleController.getString(r5, r2);
        r5 = r2.toLowerCase();
        goto L_0x038d;
    L_0x037e:
        r2 = org.telegram.messenger.ChatObject.isKickedFromChat(r10);
        if (r2 == 0) goto L_0x038f;
    L_0x0384:
        r2 = NUM; // 0x7f0e0cdc float:1.8881715E38 double:1.053163783E-314;
        r5 = "YouWereKicked";
        r5 = org.telegram.messenger.LocaleController.getString(r5, r2);
    L_0x038d:
        r2 = r5;
        goto L_0x03cf;
    L_0x038f:
        r2 = org.telegram.messenger.ChatObject.isLeftFromChat(r10);
        if (r2 == 0) goto L_0x039f;
    L_0x0395:
        r2 = NUM; // 0x7f0e0cdb float:1.8881712E38 double:1.0531637826E-314;
        r5 = "YouLeft";
        r5 = org.telegram.messenger.LocaleController.getString(r5, r2);
        goto L_0x038d;
    L_0x039f:
        r2 = r10.participants_count;
        r5 = r0.chatInfo;
        if (r5 == 0) goto L_0x03ad;
    L_0x03a5:
        r2 = r5.participants;
        r2 = r2.participants;
        r2 = r2.size();
    L_0x03ad:
        if (r2 == 0) goto L_0x03ca;
    L_0x03af:
        r5 = r0.onlineCount;
        if (r5 <= r4) goto L_0x03ca;
    L_0x03b3:
        r5 = 2;
        r7 = new java.lang.Object[r5];
        r2 = org.telegram.messenger.LocaleController.formatPluralString(r9, r2);
        r5 = 0;
        r7[r5] = r2;
        r2 = r0.onlineCount;
        r2 = org.telegram.messenger.LocaleController.formatPluralString(r14, r2);
        r7[r4] = r2;
        r5 = java.lang.String.format(r15, r7);
        goto L_0x038d;
    L_0x03ca:
        r5 = org.telegram.messenger.LocaleController.formatPluralString(r9, r2);
        goto L_0x038d;
    L_0x03cf:
        r7 = 0;
        r12 = 0;
    L_0x03d1:
        r14 = 2;
        if (r7 >= r14) goto L_0x056c;
    L_0x03d4:
        r15 = r0.nameTextView;
        r19 = r15[r7];
        if (r19 != 0) goto L_0x03e2;
    L_0x03da:
        r21 = r1;
        r22 = r2;
        r20 = r5;
        goto L_0x0561;
    L_0x03e2:
        r14 = r10.title;
        if (r14 == 0) goto L_0x03ef;
    L_0x03e6:
        r15 = r15[r7];
        r14 = r15.setText(r14);
        if (r14 == 0) goto L_0x03ef;
    L_0x03ee:
        r12 = 1;
    L_0x03ef:
        r14 = r0.nameTextView;
        r14 = r14[r7];
        r15 = 0;
        r14.setLeftDrawable(r15);
        if (r7 == 0) goto L_0x0422;
    L_0x03f9:
        r14 = r10.scam;
        if (r14 == 0) goto L_0x0409;
    L_0x03fd:
        r14 = r0.nameTextView;
        r14 = r14[r7];
        r15 = r24.getScamDrawable();
        r14.setRightDrawable(r15);
        goto L_0x0432;
    L_0x0409:
        r14 = r10.verified;
        if (r14 == 0) goto L_0x0419;
    L_0x040d:
        r14 = r0.nameTextView;
        r14 = r14[r7];
        r15 = r24.getVerifiedCrossfadeDrawable();
        r14.setRightDrawable(r15);
        goto L_0x0432;
    L_0x0419:
        r14 = r0.nameTextView;
        r14 = r14[r7];
        r15 = 0;
        r14.setRightDrawable(r15);
        goto L_0x0432;
    L_0x0422:
        r15 = 0;
        r14 = r10.scam;
        if (r14 == 0) goto L_0x0435;
    L_0x0427:
        r14 = r0.nameTextView;
        r14 = r14[r7];
        r15 = r24.getScamDrawable();
        r14.setRightDrawable(r15);
    L_0x0432:
        r20 = r5;
        goto L_0x0452;
    L_0x0435:
        r14 = r0.nameTextView;
        r14 = r14[r7];
        r15 = r0.currentAccount;
        r15 = org.telegram.messenger.MessagesController.getInstance(r15);
        r4 = r0.chat_id;
        r4 = -r4;
        r20 = r5;
        r4 = (long) r4;
        r4 = r15.isDialogMuted(r4);
        if (r4 == 0) goto L_0x044e;
    L_0x044b:
        r4 = org.telegram.ui.ActionBar.Theme.chat_muteIconDrawable;
        goto L_0x044f;
    L_0x044e:
        r4 = 0;
    L_0x044f:
        r14.setRightDrawable(r4);
    L_0x0452:
        if (r7 != 0) goto L_0x045e;
    L_0x0454:
        if (r1 == 0) goto L_0x045e;
    L_0x0456:
        r4 = r0.onlineTextView;
        r4 = r4[r7];
        r4.setText(r1);
        goto L_0x04c3;
    L_0x045e:
        r4 = r0.currentChat;
        r4 = r4.megagroup;
        if (r4 == 0) goto L_0x047a;
    L_0x0464:
        r4 = r0.chatInfo;
        if (r4 == 0) goto L_0x047a;
    L_0x0468:
        r4 = r0.onlineCount;
        if (r4 <= 0) goto L_0x047a;
    L_0x046c:
        r4 = r0.onlineTextView;
        r4 = r4[r7];
        if (r7 != 0) goto L_0x0474;
    L_0x0472:
        r5 = r2;
        goto L_0x0476;
    L_0x0474:
        r5 = r20;
    L_0x0476:
        r4.setText(r5);
        goto L_0x04c3;
    L_0x047a:
        if (r7 != 0) goto L_0x054f;
    L_0x047c:
        r4 = r0.currentChat;
        r4 = org.telegram.messenger.ChatObject.isChannel(r4);
        if (r4 == 0) goto L_0x054f;
    L_0x0484:
        r4 = r0.chatInfo;
        if (r4 == 0) goto L_0x054f;
    L_0x0488:
        r4 = r4.participants_count;
        if (r4 == 0) goto L_0x054f;
    L_0x048c:
        r4 = r0.currentChat;
        r5 = r4.megagroup;
        if (r5 != 0) goto L_0x0496;
    L_0x0492:
        r4 = r4.broadcast;
        if (r4 == 0) goto L_0x054f;
    L_0x0496:
        r4 = 1;
        r5 = new int[r4];
        r4 = r0.chatInfo;
        r4 = r4.participants_count;
        r4 = org.telegram.messenger.LocaleController.formatShortNumber(r4, r5);
        r14 = r0.currentChat;
        r14 = r14.megagroup;
        if (r14 == 0) goto L_0x0526;
    L_0x04a7:
        r14 = r0.chatInfo;
        r14 = r14.participants_count;
        if (r14 != 0) goto L_0x04fa;
    L_0x04ad:
        r4 = r10.has_geo;
        if (r4 == 0) goto L_0x04c9;
    L_0x04b1:
        r4 = r0.onlineTextView;
        r4 = r4[r7];
        r14 = NUM; // 0x7f0e0638 float:1.8878266E38 double:1.053162943E-314;
        r5 = org.telegram.messenger.LocaleController.getString(r13, r14);
        r5 = r5.toLowerCase();
        r4.setText(r5);
    L_0x04c3:
        r21 = r1;
        r22 = r2;
        goto L_0x0561;
    L_0x04c9:
        r14 = NUM; // 0x7f0e0638 float:1.8878266E38 double:1.053162943E-314;
        r4 = r10.username;
        r4 = android.text.TextUtils.isEmpty(r4);
        if (r4 != 0) goto L_0x04e7;
    L_0x04d4:
        r4 = r0.onlineTextView;
        r4 = r4[r7];
        r5 = NUM; // 0x7f0e063c float:1.8878275E38 double:1.053162945E-314;
        r5 = org.telegram.messenger.LocaleController.getString(r8, r5);
        r5 = r5.toLowerCase();
        r4.setText(r5);
        goto L_0x04c3;
    L_0x04e7:
        r4 = r0.onlineTextView;
        r4 = r4[r7];
        r15 = NUM; // 0x7f0e0639 float:1.8878269E38 double:1.0531629437E-314;
        r5 = org.telegram.messenger.LocaleController.getString(r11, r15);
        r5 = r5.toLowerCase();
        r4.setText(r5);
        goto L_0x04c3;
    L_0x04fa:
        r15 = NUM; // 0x7f0e0639 float:1.8878269E38 double:1.0531629437E-314;
        r14 = r0.onlineTextView;
        r14 = r14[r7];
        r17 = 0;
        r15 = r5[r17];
        r15 = org.telegram.messenger.LocaleController.formatPluralString(r9, r15);
        r21 = r1;
        r22 = r2;
        r1 = 1;
        r2 = new java.lang.Object[r1];
        r1 = r5[r17];
        r1 = java.lang.Integer.valueOf(r1);
        r2[r17] = r1;
        r1 = "%d";
        r1 = java.lang.String.format(r1, r2);
        r1 = r15.replace(r1, r4);
        r14.setText(r1);
        goto L_0x0561;
    L_0x0526:
        r21 = r1;
        r22 = r2;
        r17 = 0;
        r1 = r0.onlineTextView;
        r1 = r1[r7];
        r2 = r5[r17];
        r2 = org.telegram.messenger.LocaleController.formatPluralString(r3, r2);
        r14 = 1;
        r15 = new java.lang.Object[r14];
        r5 = r5[r17];
        r5 = java.lang.Integer.valueOf(r5);
        r15[r17] = r5;
        r5 = "%d";
        r5 = java.lang.String.format(r5, r15);
        r2 = r2.replace(r5, r4);
        r1.setText(r2);
        goto L_0x0561;
    L_0x054f:
        r21 = r1;
        r22 = r2;
        r1 = r0.onlineTextView;
        r1 = r1[r7];
        if (r7 != 0) goto L_0x055c;
    L_0x0559:
        r2 = r22;
        goto L_0x055e;
    L_0x055c:
        r2 = r20;
    L_0x055e:
        r1.setText(r2);
    L_0x0561:
        r7 = r7 + 1;
        r5 = r20;
        r1 = r21;
        r2 = r22;
        r4 = 1;
        goto L_0x03d1;
    L_0x056c:
        if (r12 == 0) goto L_0x0571;
    L_0x056e:
        r24.needLayout();
    L_0x0571:
        r1 = r10.photo;
        if (r1 == 0) goto L_0x057a;
    L_0x0575:
        r3 = r1.photo_big;
        r16 = r3;
        goto L_0x057c;
    L_0x057a:
        r16 = 0;
    L_0x057c:
        r1 = r0.avatarDrawable;
        r1.setInfo(r10);
        r1 = 1;
        r9 = org.telegram.messenger.ImageLocation.getForChat(r10, r1);
        r1 = 0;
        r2 = org.telegram.messenger.ImageLocation.getForChat(r10, r1);
        r1 = r0.avatarsViewPager;
        r1.initIfEmpty(r9, r2);
        r1 = r0.avatarImage;
        r3 = r0.avatarDrawable;
        r1.setImage(r2, r6, r3, r10);
        r1 = r0.currentAccount;
        r8 = org.telegram.messenger.FileLoader.getInstance(r1);
        r11 = 0;
        r12 = 0;
        r13 = 1;
        r8.loadFile(r9, r10, r11, r12, r13);
        r1 = r0.avatarImage;
        r1 = r1.getImageReceiver();
        r2 = org.telegram.ui.PhotoViewer.isShowingImage(r16);
        r3 = 1;
        r2 = r2 ^ r3;
        r3 = 0;
        r1.setVisible(r2, r3);
    L_0x05b3:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ProfileActivity.updateProfileData():void");
    }

    private void createActionBarMenu() {
        ActionBarMenu createMenu = this.actionBar.createMenu();
        createMenu.clearItems();
        ActionBarMenuItem actionBarMenuItem = null;
        this.animatingItem = null;
        if (this.user_id != 0) {
            ActionBarMenuItem addItem;
            String str = "ShareContact";
            if (UserConfig.getInstance(this.currentAccount).getClientUserId() != this.user_id) {
                User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.user_id));
                if (user != null) {
                    UserFull userFull = this.userInfo;
                    if (userFull != null && userFull.phone_calls_available) {
                        this.callItem = createMenu.addItem(15, NUM);
                    }
                    String str2 = "Unblock";
                    boolean z;
                    if (this.isBot || ContactsController.getInstance(this.currentAccount).contactsDict.get(Integer.valueOf(this.user_id)) == null) {
                        addItem = createMenu.addItem(10, NUM);
                        if (!MessagesController.isSupportUser(user)) {
                            if (this.isBot) {
                                if (!user.bot_nochats) {
                                    addItem.addSubItem(9, NUM, LocaleController.getString("BotInvite", NUM));
                                }
                                addItem.addSubItem(10, NUM, LocaleController.getString("BotShare", NUM));
                            } else {
                                addItem.addSubItem(1, NUM, LocaleController.getString("AddContact", NUM));
                            }
                            if (!TextUtils.isEmpty(user.phone)) {
                                addItem.addSubItem(3, NUM, LocaleController.getString(str, NUM));
                            }
                            if (this.isBot) {
                                int i;
                                String str3;
                                int i2 = !this.userBlocked ? NUM : NUM;
                                if (this.userBlocked) {
                                    i = NUM;
                                    str3 = "BotRestart";
                                } else {
                                    i = NUM;
                                    str3 = "BotStop";
                                }
                                addItem.addSubItem(2, i2, LocaleController.getString(str3, i));
                            } else {
                                z = this.userBlocked;
                                addItem.addSubItem(2, NUM, !this.userBlocked ? LocaleController.getString("BlockContact", NUM) : LocaleController.getString(str2, NUM));
                            }
                        } else if (this.userBlocked) {
                            addItem.addSubItem(2, NUM, LocaleController.getString(str2, NUM));
                        }
                    } else {
                        addItem = createMenu.addItem(10, NUM);
                        if (!TextUtils.isEmpty(user.phone)) {
                            addItem.addSubItem(3, NUM, LocaleController.getString(str, NUM));
                        }
                        z = this.userBlocked;
                        addItem.addSubItem(2, NUM, !this.userBlocked ? LocaleController.getString("BlockContact", NUM) : LocaleController.getString(str2, NUM));
                        addItem.addSubItem(4, NUM, LocaleController.getString("EditContact", NUM));
                        addItem.addSubItem(5, NUM, LocaleController.getString("DeleteContact", NUM));
                    }
                } else {
                    return;
                }
            }
            addItem = createMenu.addItem(10, NUM);
            addItem.addSubItem(3, NUM, LocaleController.getString(str, NUM));
            actionBarMenuItem = addItem;
        } else {
            int i3 = this.chat_id;
            if (i3 != 0 && i3 > 0) {
                Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.chat_id));
                if (ChatObject.isChannel(chat)) {
                    if (ChatObject.hasAdminRights(chat) || (chat.megagroup && ChatObject.canChangeChatInfo(chat))) {
                        this.editItem = createMenu.addItem(12, NUM);
                    }
                    if (!chat.megagroup) {
                        ChatFull chatFull = this.chatInfo;
                        if (chatFull != null && chatFull.can_view_stats) {
                            ActionBarMenuItem addItem2 = createMenu.addItem(10, NUM);
                            addItem2.addSubItem(19, NUM, LocaleController.getString("Statistics", NUM));
                            actionBarMenuItem = addItem2;
                        }
                    }
                    if (chat.megagroup) {
                        if (actionBarMenuItem == null) {
                            actionBarMenuItem = createMenu.addItem(10, NUM);
                        }
                        actionBarMenuItem.addSubItem(17, NUM, LocaleController.getString("SearchMembers", NUM));
                        if (!(chat.creator || chat.left || chat.kicked)) {
                            actionBarMenuItem.addSubItem(7, NUM, LocaleController.getString("LeaveMegaMenu", NUM));
                        }
                    } else if (!TextUtils.isEmpty(chat.username)) {
                        if (actionBarMenuItem == null) {
                            actionBarMenuItem = createMenu.addItem(10, NUM);
                        }
                        actionBarMenuItem.addSubItem(10, NUM, LocaleController.getString("BotShare", NUM));
                    }
                } else {
                    if (ChatObject.canChangeChatInfo(chat)) {
                        this.editItem = createMenu.addItem(12, NUM);
                    }
                    ActionBarMenuItem addItem3 = createMenu.addItem(10, NUM);
                    if (!(ChatObject.isKickedFromChat(chat) || ChatObject.isLeftFromChat(chat))) {
                        addItem3.addSubItem(17, NUM, LocaleController.getString("SearchMembers", NUM));
                    }
                    addItem3.addSubItem(7, NUM, LocaleController.getString("DeleteAndExit", NUM));
                    actionBarMenuItem = addItem3;
                }
            }
        }
        if (actionBarMenuItem == null) {
            actionBarMenuItem = createMenu.addItem(10, NUM);
        }
        actionBarMenuItem.addSubItem(14, NUM, LocaleController.getString("AddShortcut", NUM));
        actionBarMenuItem.setContentDescription(LocaleController.getString("AccDescrMoreOptions", NUM));
        ActionBarMenuItem actionBarMenuItem2 = this.editItem;
        if (actionBarMenuItem2 != null) {
            actionBarMenuItem2.setContentDescription(LocaleController.getString("Edit", NUM));
        }
        actionBarMenuItem2 = this.callItem;
        if (actionBarMenuItem2 != null) {
            actionBarMenuItem2.setContentDescription(LocaleController.getString("Call", NUM));
        }
        PagerIndicatorView pagerIndicatorView = this.avatarsViewPagerIndicatorView;
        if (pagerIndicatorView != null && pagerIndicatorView.isIndicatorFullyVisible()) {
            actionBarMenuItem2 = this.editItem;
            if (actionBarMenuItem2 != null) {
                actionBarMenuItem2.setVisibility(8);
            }
            actionBarMenuItem2 = this.callItem;
            if (actionBarMenuItem2 != null) {
                actionBarMenuItem2.setVisibility(8);
            }
        }
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
            SendMessagesHelper.getInstance(this.currentAccount).sendMessage(MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.user_id)), longValue, null, null, null, true, 0);
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
        -$$Lambda$ProfileActivity$ybA4dDgphBoDMFj9vMkmt8EIenI -__lambda_profileactivity_yba4ddgphbodmfj9vmkmt8eieni = new -$$Lambda$ProfileActivity$ybA4dDgphBoDMFj9vMkmt8EIenI(this);
        r11 = new ThemeDescription[67];
        -$$Lambda$ProfileActivity$ybA4dDgphBoDMFj9vMkmt8EIenI -__lambda_profileactivity_yba4ddgphbodmfj9vmkmt8eieni2 = -__lambda_profileactivity_yba4ddgphbodmfj9vmkmt8eieni;
        r11[5] = new ThemeDescription(null, 0, null, null, null, -__lambda_profileactivity_yba4ddgphbodmfj9vmkmt8eieni2, "actionBarDefaultIcon");
        r11[6] = new ThemeDescription(null, 0, null, null, null, -__lambda_profileactivity_yba4ddgphbodmfj9vmkmt8eieni2, "avatar_actionBarSelectorBlue");
        r11[7] = new ThemeDescription(null, 0, null, null, null, -__lambda_profileactivity_yba4ddgphbodmfj9vmkmt8eieni2, "chat_lockIcon");
        r11[8] = new ThemeDescription(null, 0, null, null, null, -__lambda_profileactivity_yba4ddgphbodmfj9vmkmt8eieni2, "avatar_subtitleInProfileBlue");
        r11[9] = new ThemeDescription(null, 0, null, null, null, -__lambda_profileactivity_yba4ddgphbodmfj9vmkmt8eieni2, "avatar_backgroundActionBarBlue");
        r11[10] = new ThemeDescription(null, 0, null, null, null, -__lambda_profileactivity_yba4ddgphbodmfj9vmkmt8eieni2, "profile_title");
        r11[11] = new ThemeDescription(null, 0, null, null, null, -__lambda_profileactivity_yba4ddgphbodmfj9vmkmt8eieni2, "profile_status");
        r11[12] = new ThemeDescription(null, 0, null, null, null, -__lambda_profileactivity_yba4ddgphbodmfj9vmkmt8eieni2, "avatar_subtitleInProfileBlue");
        r11[13] = new ThemeDescription(this.topView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "avatar_backgroundActionBarBlue");
        r11[14] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "avatar_backgroundActionBarBlue");
        r11[15] = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21");
        r11[16] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, "divider");
        r11[17] = new ThemeDescription(this.avatarImage, 0, null, null, new Drawable[]{Theme.avatar_savedDrawable}, null, "avatar_text");
        r11[18] = new ThemeDescription(this.avatarImage, 0, null, null, new Drawable[]{this.avatarDrawable}, null, "avatar_backgroundInProfileBlue");
        r11[19] = new ThemeDescription(this.writeButton, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, "profile_actionIcon");
        r11[20] = new ThemeDescription(this.writeButton, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "profile_actionBackground");
        r11[21] = new ThemeDescription(this.writeButton, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, "profile_actionPressedBackground");
        View view = this.listView;
        int i = ThemeDescription.FLAG_CHECKTAG;
        Class[] clsArr = new Class[]{TextCell.class};
        String[] strArr = new String[1];
        strArr[0] = "textView";
        r11[22] = new ThemeDescription(view, i, clsArr, strArr, null, null, null, "windowBackgroundWhiteBlackText");
        r11[23] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteGreenText2");
        r11[24] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteRedText5");
        r11[25] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlueText2");
        r11[26] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlueButton");
        view = this.listView;
        clsArr = new Class[]{TextCell.class};
        strArr = new String[1];
        strArr[0] = "valueTextView";
        r11[27] = new ThemeDescription(view, 0, clsArr, strArr, null, null, null, "windowBackgroundWhiteValueText");
        view = this.listView;
        i = ThemeDescription.FLAG_CHECKTAG;
        clsArr = new Class[]{TextCell.class};
        strArr = new String[1];
        strArr[0] = "imageView";
        r11[28] = new ThemeDescription(view, i, clsArr, strArr, null, null, null, "windowBackgroundWhiteGrayIcon");
        r11[29] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"imageView"}, null, null, null, "windowBackgroundWhiteBlueIcon");
        r11[30] = new ThemeDescription(this.listView, 0, new Class[]{TextDetailCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText");
        r11[31] = new ThemeDescription(this.listView, 0, new Class[]{TextDetailCell.class}, new String[]{"valueTextView"}, null, null, null, "windowBackgroundWhiteGrayText2");
        r11[32] = new ThemeDescription(this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlueHeader");
        r11[33] = new ThemeDescription(this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText");
        r11[34] = new ThemeDescription(this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"valueTextView"}, null, null, null, "windowBackgroundWhiteGrayText2");
        view = this.listView;
        clsArr = new Class[]{NotificationsCheckCell.class};
        strArr = new String[1];
        strArr[0] = "checkBox";
        r11[35] = new ThemeDescription(view, 0, clsArr, strArr, null, null, null, "switchTrack");
        r11[36] = new ThemeDescription(this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"checkBox"}, null, null, null, "switchTrackChecked");
        r11[37] = new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{UserCell.class}, new String[]{"adminTextView"}, null, null, null, "profile_creatorIcon");
        r11[38] = new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, new String[]{"imageView"}, null, null, null, "windowBackgroundWhiteGrayIcon");
        r11[39] = new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, new String[]{"nameTextView"}, null, null, null, "windowBackgroundWhiteBlackText");
        ThemeDescriptionDelegate themeDescriptionDelegate = -__lambda_profileactivity_yba4ddgphbodmfj9vmkmt8eieni;
        r11[40] = new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, new String[]{"statusColor"}, null, null, themeDescriptionDelegate, "windowBackgroundWhiteGrayText");
        r11[41] = new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, new String[]{"statusOnlineColor"}, null, null, themeDescriptionDelegate, "windowBackgroundWhiteBlueText");
        r11[42] = new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, null, new Drawable[]{Theme.avatar_savedDrawable}, null, "avatar_text");
        -__lambda_profileactivity_yba4ddgphbodmfj9vmkmt8eieni2 = -__lambda_profileactivity_yba4ddgphbodmfj9vmkmt8eieni;
        r11[43] = new ThemeDescription(null, 0, null, null, null, -__lambda_profileactivity_yba4ddgphbodmfj9vmkmt8eieni2, "avatar_backgroundRed");
        r11[44] = new ThemeDescription(null, 0, null, null, null, -__lambda_profileactivity_yba4ddgphbodmfj9vmkmt8eieni2, "avatar_backgroundOrange");
        r11[45] = new ThemeDescription(null, 0, null, null, null, -__lambda_profileactivity_yba4ddgphbodmfj9vmkmt8eieni2, "avatar_backgroundViolet");
        r11[46] = new ThemeDescription(null, 0, null, null, null, -__lambda_profileactivity_yba4ddgphbodmfj9vmkmt8eieni2, "avatar_backgroundGreen");
        r11[47] = new ThemeDescription(null, 0, null, null, null, -__lambda_profileactivity_yba4ddgphbodmfj9vmkmt8eieni2, "avatar_backgroundCyan");
        r11[48] = new ThemeDescription(null, 0, null, null, null, -__lambda_profileactivity_yba4ddgphbodmfj9vmkmt8eieni2, "avatar_backgroundBlue");
        r11[49] = new ThemeDescription(null, 0, null, null, null, -__lambda_profileactivity_yba4ddgphbodmfj9vmkmt8eieni2, "avatar_backgroundPink");
        r11[50] = new ThemeDescription(this.undoView, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "undo_background");
        r11[51] = new ThemeDescription(this.undoView, 0, new Class[]{UndoView.class}, new String[]{"undoImageView"}, null, null, null, "undo_cancelColor");
        r11[52] = new ThemeDescription(this.undoView, 0, new Class[]{UndoView.class}, new String[]{"undoTextView"}, null, null, null, "undo_cancelColor");
        r11[53] = new ThemeDescription(this.undoView, 0, new Class[]{UndoView.class}, new String[]{"infoTextView"}, null, null, null, "undo_infoColor");
        r11[54] = new ThemeDescription(this.undoView, 0, new Class[]{UndoView.class}, new String[]{"textPaint"}, null, null, null, "undo_infoColor");
        r11[55] = new ThemeDescription(this.undoView, 0, new Class[]{UndoView.class}, new String[]{"progressPaint"}, null, null, null, "undo_infoColor");
        r11[56] = new ThemeDescription(this.undoView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{UndoView.class}, new String[]{"leftImageView"}, null, null, null, "undo_infoColor");
        r11[57] = new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{AboutLinkCell.class}, Theme.profile_aboutTextPaint, null, null, "windowBackgroundWhiteBlackText");
        r11[58] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LINKCOLOR, new Class[]{AboutLinkCell.class}, Theme.profile_aboutTextPaint, null, null, "windowBackgroundWhiteLinkText");
        r11[59] = new ThemeDescription(this.listView, 0, new Class[]{AboutLinkCell.class}, Theme.linkSelectionPaint, null, null, "windowBackgroundWhiteLinkSelection");
        r11[60] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, "windowBackgroundGrayShadow");
        r11[61] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{ShadowSectionCell.class}, null, null, null, "windowBackgroundGray");
        r11[62] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, "windowBackgroundGrayShadow");
        r11[63] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextInfoPrivacyCell.class}, null, null, null, "windowBackgroundGray");
        r11[64] = new ThemeDescription(this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteGrayText4");
        r11[65] = new ThemeDescription(this.nameTextView[1], 0, null, null, new Drawable[]{this.verifiedCheckDrawable}, null, "profile_verifiedCheck");
        r11[66] = new ThemeDescription(this.nameTextView[1], 0, null, null, new Drawable[]{this.verifiedDrawable}, null, "profile_verifiedBackground");
        return r11;
    }

    public /* synthetic */ void lambda$getThemeDescriptions$22$ProfileActivity() {
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
        if (!this.isPulledDown) {
            Object tag = this.onlineTextView[1].getTag();
            if (tag instanceof String) {
                this.onlineTextView[1].setTextColor(Theme.getColor((String) tag));
            }
            Drawable drawable = this.lockIconDrawable;
            if (drawable != null) {
                drawable.setColorFilter(Theme.getColor("chat_lockIcon"), Mode.MULTIPLY);
            }
            ScamDrawable scamDrawable = this.scamDrawable;
            if (scamDrawable != null) {
                scamDrawable.setColor(Theme.getColor("avatar_subtitleInProfileBlue"));
            }
            this.nameTextView[1].setBackgroundColor(Theme.getColor("avatar_backgroundActionBarBlue"));
            this.nameTextView[1].setTextColor(Theme.getColor("profile_title"));
            this.actionBar.setItemsColor(Theme.getColor("actionBarDefaultIcon"), false);
            this.actionBar.setItemsBackgroundColor(Theme.getColor("avatar_actionBarSelectorBlue"), false);
        }
    }
}
