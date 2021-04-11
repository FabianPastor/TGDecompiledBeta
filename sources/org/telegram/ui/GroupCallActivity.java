package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Property;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListUpdateCallback;
import androidx.recyclerview.widget.RecyclerView;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.voip.VoIPBaseService;
import org.telegram.messenger.voip.VoIPService;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$ChatFull;
import org.telegram.tgnet.TLRPC$FileLocation;
import org.telegram.tgnet.TLRPC$GroupCall;
import org.telegram.tgnet.TLRPC$InputFile;
import org.telegram.tgnet.TLRPC$InputPeer;
import org.telegram.tgnet.TLRPC$InputPhoto;
import org.telegram.tgnet.TLRPC$Peer;
import org.telegram.tgnet.TLRPC$PhotoSize;
import org.telegram.tgnet.TLRPC$TL_chatFull;
import org.telegram.tgnet.TLRPC$TL_chatInviteExported;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_groupCall;
import org.telegram.tgnet.TLRPC$TL_groupCallDiscarded;
import org.telegram.tgnet.TLRPC$TL_groupCallParticipant;
import org.telegram.tgnet.TLRPC$TL_inputChatPhoto;
import org.telegram.tgnet.TLRPC$TL_inputPeerChannel;
import org.telegram.tgnet.TLRPC$TL_inputPeerChat;
import org.telegram.tgnet.TLRPC$TL_inputPeerUser;
import org.telegram.tgnet.TLRPC$TL_inputUser;
import org.telegram.tgnet.TLRPC$TL_messages_exportChatInvite;
import org.telegram.tgnet.TLRPC$TL_peerChannel;
import org.telegram.tgnet.TLRPC$TL_peerChat;
import org.telegram.tgnet.TLRPC$TL_peerUser;
import org.telegram.tgnet.TLRPC$TL_phone_createGroupCall;
import org.telegram.tgnet.TLRPC$TL_phone_exportGroupCallInvite;
import org.telegram.tgnet.TLRPC$TL_phone_exportedGroupCallInvite;
import org.telegram.tgnet.TLRPC$TL_phone_inviteToGroupCall;
import org.telegram.tgnet.TLRPC$TL_phone_saveDefaultGroupCallJoinAs;
import org.telegram.tgnet.TLRPC$TL_phone_toggleGroupCallSettings;
import org.telegram.tgnet.TLRPC$TL_photos_photo;
import org.telegram.tgnet.TLRPC$TL_photos_uploadProfilePhoto;
import org.telegram.tgnet.TLRPC$TL_updateGroupCall;
import org.telegram.tgnet.TLRPC$TL_userProfilePhoto;
import org.telegram.tgnet.TLRPC$TL_userProfilePhotoEmpty;
import org.telegram.tgnet.TLRPC$Update;
import org.telegram.tgnet.TLRPC$Updates;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.tgnet.TLRPC$UserProfilePhoto;
import org.telegram.tgnet.TLRPC$VideoSize;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.AccountSelectCell;
import org.telegram.ui.Cells.CheckBoxCell;
import org.telegram.ui.Cells.GroupCallInvitedCell;
import org.telegram.ui.Cells.GroupCallTextCell;
import org.telegram.ui.Cells.GroupCallUserCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.AnimationProperties;
import org.telegram.ui.Components.AudioPlayerAlert;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.BlobDrawable;
import org.telegram.ui.Components.CheckBoxSquare;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.FillLastLinearLayoutManager;
import org.telegram.ui.Components.GroupCallPip;
import org.telegram.ui.Components.GroupVoipInviteAlert;
import org.telegram.ui.Components.HintView;
import org.telegram.ui.Components.ImageUpdater;
import org.telegram.ui.Components.JoinCallAlert;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.NumberPicker;
import org.telegram.ui.Components.ProfileGalleryView;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.RLottieImageView;
import org.telegram.ui.Components.RadialProgressView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.ShareAlert;
import org.telegram.ui.Components.UndoView;
import org.telegram.ui.Components.voip.VoIPToggleButton;
import org.telegram.ui.GroupCallActivity;

public class GroupCallActivity extends BottomSheet implements NotificationCenter.NotificationCenterDelegate, VoIPBaseService.StateListener {
    public static GroupCallActivity groupCallInstance;
    public static boolean groupCallUiVisible;
    /* access modifiers changed from: private */
    public View accountGap;
    /* access modifiers changed from: private */
    public AccountInstance accountInstance;
    /* access modifiers changed from: private */
    public AccountSelectCell accountSelectCell;
    private AvatarDrawable accountSwitchAvatarDrawable;
    /* access modifiers changed from: private */
    public BackupImageView accountSwitchImageView;
    /* access modifiers changed from: private */
    public ActionBar actionBar;
    /* access modifiers changed from: private */
    public AnimatorSet actionBarAnimation;
    private View actionBarBackground;
    /* access modifiers changed from: private */
    public View actionBarShadow;
    /* access modifiers changed from: private */
    public ActionBarMenuSubItem adminItem;
    /* access modifiers changed from: private */
    public float amplitude;
    /* access modifiers changed from: private */
    public float animateAmplitudeDiff;
    /* access modifiers changed from: private */
    public float animateToAmplitude;
    private boolean anyEnterEventSent;
    /* access modifiers changed from: private */
    public final AvatarPreviewPagerIndicator avatarPagerIndicator;
    /* access modifiers changed from: private */
    public final FrameLayout avatarPreviewContainer;
    /* access modifiers changed from: private */
    public boolean avatarPriviewTransitionInProgress;
    AvatarUpdaterDelegate avatarUpdaterDelegate;
    /* access modifiers changed from: private */
    public boolean avatarsPreviewShowed;
    /* access modifiers changed from: private */
    public final ProfileGalleryView avatarsViewPager;
    /* access modifiers changed from: private */
    public int backgroundColor;
    /* access modifiers changed from: private */
    public RLottieDrawable bigMicDrawable;
    /* access modifiers changed from: private */
    public final BlobDrawable bigWaveDrawable;
    View blurredView;
    /* access modifiers changed from: private */
    public FrameLayout buttonsContainer;
    public ChatObject.Call call;
    private boolean callInitied;
    /* access modifiers changed from: private */
    public boolean changingPermissions;
    /* access modifiers changed from: private */
    public float colorProgress;
    /* access modifiers changed from: private */
    public final int[] colorsTmp = new int[3];
    /* access modifiers changed from: private */
    public boolean contentFullyOverlayed;
    private long creatingServiceTime;
    ImageUpdater currentAvatarUpdater;
    private int currentCallState;
    public TLRPC$Chat currentChat;
    private ViewGroup currentOptionsLayout;
    /* access modifiers changed from: private */
    public WeavingState currentState;
    /* access modifiers changed from: private */
    public boolean delayedGroupCallUpdated;
    private DiffUtil.Callback diffUtilsCallback = new DiffUtil.Callback() {
        public boolean areContentsTheSame(int i, int i2) {
            return true;
        }

        public int getOldListSize() {
            return GroupCallActivity.this.oldCount;
        }

        public int getNewListSize() {
            return GroupCallActivity.this.listAdapter.rowsCount;
        }

        public boolean areItemsTheSame(int i, int i2) {
            if (GroupCallActivity.this.listAdapter.addMemberRow >= 0) {
                if (i == GroupCallActivity.this.oldAddMemberRow && i2 == GroupCallActivity.this.listAdapter.addMemberRow) {
                    return true;
                }
                if ((i == GroupCallActivity.this.oldAddMemberRow && i2 != GroupCallActivity.this.listAdapter.addMemberRow) || (i != GroupCallActivity.this.oldAddMemberRow && i2 == GroupCallActivity.this.listAdapter.addMemberRow)) {
                    return false;
                }
            }
            if (i == GroupCallActivity.this.oldCount - 1 && i2 == GroupCallActivity.this.listAdapter.rowsCount - 1) {
                return true;
            }
            if (!(i == GroupCallActivity.this.oldCount - 1 || i2 == GroupCallActivity.this.listAdapter.rowsCount - 1)) {
                if (i2 >= GroupCallActivity.this.listAdapter.usersStartRow && i2 < GroupCallActivity.this.listAdapter.usersEndRow && i >= GroupCallActivity.this.oldUsersStartRow && i < GroupCallActivity.this.oldUsersEndRow) {
                    TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant = (TLRPC$TL_groupCallParticipant) GroupCallActivity.this.oldParticipants.get(i - GroupCallActivity.this.oldUsersStartRow);
                    GroupCallActivity groupCallActivity = GroupCallActivity.this;
                    if (MessageObject.getPeerId(tLRPC$TL_groupCallParticipant.peer) != MessageObject.getPeerId(groupCallActivity.call.sortedParticipants.get(i2 - groupCallActivity.listAdapter.usersStartRow).peer)) {
                        return false;
                    }
                    if (i == i2 || tLRPC$TL_groupCallParticipant.lastActiveDate == ((long) tLRPC$TL_groupCallParticipant.active_date)) {
                        return true;
                    }
                    return false;
                } else if (i2 >= GroupCallActivity.this.listAdapter.invitedStartRow && i2 < GroupCallActivity.this.listAdapter.invitedEndRow && i >= GroupCallActivity.this.oldInvitedStartRow && i < GroupCallActivity.this.oldInvitedEndRow) {
                    GroupCallActivity groupCallActivity2 = GroupCallActivity.this;
                    return ((Integer) GroupCallActivity.this.oldInvited.get(i - GroupCallActivity.this.oldInvitedStartRow)).equals(groupCallActivity2.call.invitedUsers.get(i2 - groupCallActivity2.listAdapter.invitedStartRow));
                }
            }
            return false;
        }
    };
    /* access modifiers changed from: private */
    public ActionBarMenuSubItem editTitleItem;
    /* access modifiers changed from: private */
    public boolean enterEventSent;
    /* access modifiers changed from: private */
    public ActionBarMenuSubItem everyoneItem;
    /* access modifiers changed from: private */
    public GroupVoipInviteAlert groupVoipInviteAlert;
    /* access modifiers changed from: private */
    public RLottieDrawable handDrawables;
    /* access modifiers changed from: private */
    public boolean invalidateColors = true;
    /* access modifiers changed from: private */
    public ActionBarMenuSubItem inviteItem;
    private String[] invites = new String[2];
    private DefaultItemAnimator itemAnimator;
    /* access modifiers changed from: private */
    public long lastUpdateTime;
    /* access modifiers changed from: private */
    public FillLastLinearLayoutManager layoutManager;
    /* access modifiers changed from: private */
    public Paint leaveBackgroundPaint = new Paint(1);
    /* access modifiers changed from: private */
    public VoIPToggleButton leaveButton;
    /* access modifiers changed from: private */
    public ActionBarMenuSubItem leaveItem;
    /* access modifiers changed from: private */
    public ListAdapter listAdapter;
    /* access modifiers changed from: private */
    public RecyclerListView listView;
    /* access modifiers changed from: private */
    public Paint listViewBackgroundPaint = new Paint(1);
    private final LinearLayout menuItemsContainer;
    /* access modifiers changed from: private */
    public RLottieImageView muteButton;
    /* access modifiers changed from: private */
    public ValueAnimator muteButtonAnimator;
    /* access modifiers changed from: private */
    public int muteButtonState = 0;
    /* access modifiers changed from: private */
    public TextView[] muteLabel = new TextView[2];
    /* access modifiers changed from: private */
    public TextView[] muteSubLabel = new TextView[2];
    /* access modifiers changed from: private */
    public int oldAddMemberRow;
    /* access modifiers changed from: private */
    public int oldCount;
    /* access modifiers changed from: private */
    public ArrayList<Integer> oldInvited = new ArrayList<>();
    /* access modifiers changed from: private */
    public int oldInvitedEndRow;
    /* access modifiers changed from: private */
    public int oldInvitedStartRow;
    /* access modifiers changed from: private */
    public ArrayList<TLRPC$TL_groupCallParticipant> oldParticipants = new ArrayList<>();
    /* access modifiers changed from: private */
    public int oldUsersEndRow;
    /* access modifiers changed from: private */
    public int oldUsersStartRow;
    /* access modifiers changed from: private */
    public ActionBarMenuItem otherItem;
    /* access modifiers changed from: private */
    public Paint paint = new Paint(7);
    /* access modifiers changed from: private */
    public Paint paintTmp = new Paint(7);
    private LaunchActivity parentActivity;
    /* access modifiers changed from: private */
    public ActionBarMenuSubItem permissionItem;
    PinchToZoomHelper pinchToZoomHelper;
    private ActionBarMenuItem pipItem;
    /* access modifiers changed from: private */
    public boolean playingHandAnimation;
    /* access modifiers changed from: private */
    public int popupAnimationIndex = -1;
    /* access modifiers changed from: private */
    public Runnable pressRunnable = new Runnable() {
        public final void run() {
            GroupCallActivity.this.lambda$new$1$GroupCallActivity();
        }
    };
    /* access modifiers changed from: private */
    public boolean pressed;
    /* access modifiers changed from: private */
    public WeavingState prevState;
    /* access modifiers changed from: private */
    public float progressToAvatarPreview;
    /* access modifiers changed from: private */
    public RadialGradient radialGradient;
    /* access modifiers changed from: private */
    public final Matrix radialMatrix;
    /* access modifiers changed from: private */
    public final Paint radialPaint;
    /* access modifiers changed from: private */
    public RadialProgressView radialProgressView;
    private RecordCallDrawable recordCallDrawable;
    /* access modifiers changed from: private */
    public HintView recordHintView;
    /* access modifiers changed from: private */
    public ActionBarMenuSubItem recordItem;
    /* access modifiers changed from: private */
    public RectF rect = new RectF();
    /* access modifiers changed from: private */
    public HintView reminderHintView;
    /* access modifiers changed from: private */
    public ValueAnimator scheduleAnimator;
    private TextView scheduleButtonTextView;
    /* access modifiers changed from: private */
    public float scheduleButtonsScale;
    private boolean scheduleHasFewPeers;
    private TextView scheduleInfoTextView;
    private TLRPC$InputPeer schedulePeer;
    /* access modifiers changed from: private */
    public int scheduleStartAt;
    /* access modifiers changed from: private */
    public SimpleTextView scheduleStartAtTextView;
    /* access modifiers changed from: private */
    public SimpleTextView scheduleStartInTextView;
    /* access modifiers changed from: private */
    public SimpleTextView scheduleTimeTextView;
    private LinearLayout scheduleTimerContainer;
    /* access modifiers changed from: private */
    public boolean scheduled;
    private String scheduledHash;
    /* access modifiers changed from: private */
    public AnimatorSet scrimAnimatorSet;
    /* access modifiers changed from: private */
    public Paint scrimPaint;
    /* access modifiers changed from: private */
    public View scrimPopupLayout;
    /* access modifiers changed from: private */
    public ActionBarPopupWindow scrimPopupWindow;
    /* access modifiers changed from: private */
    public GroupCallUserCell scrimView;
    /* access modifiers changed from: private */
    public float scrollOffsetY;
    /* access modifiers changed from: private */
    public boolean scrolling;
    /* access modifiers changed from: private */
    public TLRPC$Peer selfPeer;
    /* access modifiers changed from: private */
    public Drawable shadowDrawable;
    private ShareAlert shareAlert;
    /* access modifiers changed from: private */
    public float showLightingProgress;
    /* access modifiers changed from: private */
    public float showWavesProgress;
    /* access modifiers changed from: private */
    public VoIPToggleButton soundButton;
    /* access modifiers changed from: private */
    public boolean startingGroupCall;
    private WeavingState[] states = new WeavingState[8];
    /* access modifiers changed from: private */
    public float switchProgress = 1.0f;
    /* access modifiers changed from: private */
    public float switchToButtonInt2;
    /* access modifiers changed from: private */
    public float switchToButtonProgress;
    /* access modifiers changed from: private */
    public final BlobDrawable tinyWaveDrawable;
    private AudioPlayerAlert.ClippingTextViewSwitcher titleTextView;
    private UndoView[] undoView = new UndoView[2];
    /* access modifiers changed from: private */
    public Runnable unmuteRunnable = $$Lambda$GroupCallActivity$Fejzw3BitRkLCnwqEMTIYvTsgw.INSTANCE;
    private Runnable updateCallRecordRunnable;
    /* access modifiers changed from: private */
    public Runnable updateSchedeulRunnable = new Runnable() {
        public void run() {
            int i;
            if (GroupCallActivity.this.scheduleTimeTextView != null && !GroupCallActivity.this.isDismissed()) {
                GroupCallActivity groupCallActivity = GroupCallActivity.this;
                ChatObject.Call call = groupCallActivity.call;
                if (call != null) {
                    i = call.call.schedule_date;
                } else {
                    i = groupCallActivity.scheduleStartAt;
                }
                if (i != 0) {
                    int currentTime = i - GroupCallActivity.this.accountInstance.getConnectionsManager().getCurrentTime();
                    if (currentTime >= 86400) {
                        GroupCallActivity.this.scheduleTimeTextView.setText(LocaleController.formatPluralString("Days", Math.round(((float) currentTime) / 86400.0f)));
                    } else {
                        GroupCallActivity.this.scheduleTimeTextView.setText(AndroidUtilities.formatFullDuration(Math.abs(currentTime)));
                        if (currentTime < 0 && GroupCallActivity.this.scheduleStartInTextView.getTag() == null) {
                            GroupCallActivity.this.scheduleStartInTextView.setTag(1);
                            GroupCallActivity.this.scheduleStartInTextView.setText(LocaleController.getString("VoipChatLateBy", NUM));
                        }
                    }
                    GroupCallActivity.this.scheduleStartAtTextView.setText(LocaleController.formatStartsTime((long) i, 3));
                    AndroidUtilities.runOnUIThread(GroupCallActivity.this.updateSchedeulRunnable, 1000);
                }
            }
        }
    };
    private TLObject userSwitchObject;

    /* access modifiers changed from: private */
    public static boolean isGradientState(int i) {
        return i == 2 || i == 4 || i == 5 || i == 6 || i == 7;
    }

    static /* synthetic */ void lambda$null$25(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
    }

    static /* synthetic */ void lambda$processSelectedOption$52(DialogInterface dialogInterface) {
    }

    /* access modifiers changed from: protected */
    public boolean canDismissWithSwipe() {
        return false;
    }

    public /* synthetic */ void onCameraSwitch(boolean z) {
        VoIPBaseService.StateListener.CC.$default$onCameraSwitch(this, z);
    }

    public /* synthetic */ void onMediaStateUpdated(int i, int i2) {
        VoIPBaseService.StateListener.CC.$default$onMediaStateUpdated(this, i, i2);
    }

    public /* synthetic */ void onScreenOnChange(boolean z) {
        VoIPBaseService.StateListener.CC.$default$onScreenOnChange(this, z);
    }

    public /* synthetic */ void onSignalBarsCountChanged(int i) {
        VoIPBaseService.StateListener.CC.$default$onSignalBarsCountChanged(this, i);
    }

    public /* synthetic */ void onVideoAvailableChange(boolean z) {
        VoIPBaseService.StateListener.CC.$default$onVideoAvailableChange(this, z);
    }

    static /* synthetic */ void lambda$new$0() {
        if (VoIPService.getSharedInstance() != null) {
            VoIPService.getSharedInstance().setMicMute(false, true, false);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$1 */
    public /* synthetic */ void lambda$new$1$GroupCallActivity() {
        if (this.call != null && this.scheduled && VoIPService.getSharedInstance() != null) {
            this.muteButton.performHapticFeedback(3, 2);
            updateMuteButton(1, true);
            AndroidUtilities.runOnUIThread(this.unmuteRunnable, 80);
            this.scheduled = false;
            this.pressed = true;
        }
    }

    static {
        new AnimationProperties.FloatProperty<GroupCallActivity>("colorProgress") {
            public void setValue(GroupCallActivity groupCallActivity, float f) {
                groupCallActivity.setColorProgress(f);
            }

            public Float get(GroupCallActivity groupCallActivity) {
                return Float.valueOf(groupCallActivity.getColorProgress());
            }
        };
    }

    private static class SmallRecordCallDrawable extends Drawable {
        private float alpha = 1.0f;
        private long lastUpdateTime;
        private Paint paint2 = new Paint(1);
        private View parentView;
        private int state;

        public int getOpacity() {
            return -2;
        }

        public void setAlpha(int i) {
        }

        public void setColorFilter(ColorFilter colorFilter) {
        }

        public SmallRecordCallDrawable(View view) {
            this.parentView = view;
        }

        public int getIntrinsicWidth() {
            return AndroidUtilities.dp(24.0f);
        }

        public int getIntrinsicHeight() {
            return AndroidUtilities.dp(24.0f);
        }

        public void draw(Canvas canvas) {
            int i;
            int centerX = getBounds().centerX();
            int centerY = getBounds().centerY();
            if (this.parentView instanceof SimpleTextView) {
                i = centerY + AndroidUtilities.dp(1.0f);
                centerX -= AndroidUtilities.dp(3.0f);
            } else {
                i = centerY + AndroidUtilities.dp(2.0f);
            }
            this.paint2.setColor(-1147527);
            this.paint2.setAlpha((int) (this.alpha * 255.0f));
            canvas.drawCircle((float) centerX, (float) i, (float) AndroidUtilities.dp(4.0f), this.paint2);
            long elapsedRealtime = SystemClock.elapsedRealtime();
            long j = elapsedRealtime - this.lastUpdateTime;
            if (j > 17) {
                j = 17;
            }
            this.lastUpdateTime = elapsedRealtime;
            int i2 = this.state;
            if (i2 == 0) {
                float f = this.alpha + (((float) j) / 2000.0f);
                this.alpha = f;
                if (f >= 1.0f) {
                    this.alpha = 1.0f;
                    this.state = 1;
                }
            } else if (i2 == 1) {
                float f2 = this.alpha - (((float) j) / 2000.0f);
                this.alpha = f2;
                if (f2 < 0.5f) {
                    this.alpha = 0.5f;
                    this.state = 0;
                }
            }
            this.parentView.invalidate();
        }
    }

    private static class RecordCallDrawable extends Drawable {
        private float alpha = 1.0f;
        private long lastUpdateTime;
        private Paint paint = new Paint(1);
        private Paint paint2 = new Paint(1);
        private View parentView;
        private boolean recording;
        private int state;

        public int getOpacity() {
            return -2;
        }

        public void setAlpha(int i) {
        }

        public void setColorFilter(ColorFilter colorFilter) {
        }

        public RecordCallDrawable() {
            this.paint.setColor(-1);
            this.paint.setStyle(Paint.Style.STROKE);
            this.paint.setStrokeWidth((float) AndroidUtilities.dp(1.5f));
        }

        public void setParentView(View view) {
            this.parentView = view;
        }

        public int getIntrinsicWidth() {
            return AndroidUtilities.dp(24.0f);
        }

        public int getIntrinsicHeight() {
            return AndroidUtilities.dp(24.0f);
        }

        public void setRecording(boolean z) {
            this.recording = z;
            this.alpha = 1.0f;
            invalidateSelf();
        }

        public void draw(Canvas canvas) {
            float centerX = (float) getBounds().centerX();
            float centerY = (float) getBounds().centerY();
            canvas.drawCircle(centerX, centerY, (float) AndroidUtilities.dp(10.0f), this.paint);
            this.paint2.setColor(this.recording ? -1147527 : -1);
            this.paint2.setAlpha((int) (this.alpha * 255.0f));
            canvas.drawCircle(centerX, centerY, (float) AndroidUtilities.dp(5.0f), this.paint2);
            if (this.recording) {
                long elapsedRealtime = SystemClock.elapsedRealtime();
                long j = elapsedRealtime - this.lastUpdateTime;
                if (j > 17) {
                    j = 17;
                }
                this.lastUpdateTime = elapsedRealtime;
                int i = this.state;
                if (i == 0) {
                    float f = this.alpha + (((float) j) / 2000.0f);
                    this.alpha = f;
                    if (f >= 1.0f) {
                        this.alpha = 1.0f;
                        this.state = 1;
                    }
                } else if (i == 1) {
                    float f2 = this.alpha - (((float) j) / 2000.0f);
                    this.alpha = f2;
                    if (f2 < 0.5f) {
                        this.alpha = 0.5f;
                        this.state = 0;
                    }
                }
                this.parentView.invalidate();
            }
        }
    }

    private class VolumeSlider extends FrameLayout {
        private boolean captured;
        private float colorChangeProgress;
        private int currentColor;
        private TLRPC$TL_groupCallParticipant currentParticipant;
        private double currentProgress;
        private boolean dragging;
        private RLottieImageView imageView;
        private long lastUpdateTime;
        private int oldColor;
        private Paint paint = new Paint(1);
        private Paint paint2 = new Paint(1);
        private Path path = new Path();
        private float[] radii = new float[8];
        private RectF rect = new RectF();
        private RLottieDrawable speakerDrawable;
        private float sx;
        private float sy;
        private TextView textView;
        final /* synthetic */ GroupCallActivity this$0;
        private int thumbX;
        private float[] volumeAlphas;

        /* JADX WARNING: Illegal instructions before constructor call */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public VolumeSlider(org.telegram.ui.GroupCallActivity r19, android.content.Context r20, org.telegram.tgnet.TLRPC$TL_groupCallParticipant r21) {
            /*
                r18 = this;
                r0 = r18
                r1 = r20
                r2 = r19
                r0.this$0 = r2
                r0.<init>(r1)
                android.graphics.Paint r2 = new android.graphics.Paint
                r3 = 1
                r2.<init>(r3)
                r0.paint = r2
                android.graphics.Paint r2 = new android.graphics.Paint
                r2.<init>(r3)
                r0.paint2 = r2
                android.graphics.Path r2 = new android.graphics.Path
                r2.<init>()
                r0.path = r2
                r2 = 8
                float[] r2 = new float[r2]
                r0.radii = r2
                android.graphics.RectF r2 = new android.graphics.RectF
                r2.<init>()
                r0.rect = r2
                r2 = 3
                float[] r4 = new float[r2]
                r0.volumeAlphas = r4
                r4 = 0
                r0.setWillNotDraw(r4)
                r5 = r21
                r0.currentParticipant = r5
                int r5 = org.telegram.messenger.ChatObject.getParticipantVolume(r21)
                float r5 = (float) r5
                r6 = 1184645120(0x469CLASSNAME, float:20000.0)
                float r5 = r5 / r6
                double r5 = (double) r5
                r0.currentProgress = r5
                r5 = 1065353216(0x3var_, float:1.0)
                r0.colorChangeProgress = r5
                r6 = 1094713344(0x41400000, float:12.0)
                int r7 = org.telegram.messenger.AndroidUtilities.dp(r6)
                int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
                r0.setPadding(r7, r4, r6, r4)
                org.telegram.ui.Components.RLottieDrawable r6 = new org.telegram.ui.Components.RLottieDrawable
                r7 = 1103101952(0x41CLASSNAME, float:24.0)
                int r11 = org.telegram.messenger.AndroidUtilities.dp(r7)
                int r12 = org.telegram.messenger.AndroidUtilities.dp(r7)
                r9 = 2131558481(0x7f0d0051, float:1.874228E38)
                java.lang.String r10 = "NUM"
                r13 = 1
                r14 = 0
                r8 = r6
                r8.<init>((int) r9, (java.lang.String) r10, (int) r11, (int) r12, (boolean) r13, (int[]) r14)
                r0.speakerDrawable = r6
                org.telegram.ui.Components.RLottieImageView r6 = new org.telegram.ui.Components.RLottieImageView
                r6.<init>(r1)
                r0.imageView = r6
                android.widget.ImageView$ScaleType r7 = android.widget.ImageView.ScaleType.CENTER
                r6.setScaleType(r7)
                org.telegram.ui.Components.RLottieImageView r6 = r0.imageView
                org.telegram.ui.Components.RLottieDrawable r7 = r0.speakerDrawable
                r6.setAnimation(r7)
                org.telegram.ui.Components.RLottieImageView r6 = r0.imageView
                double r7 = r0.currentProgress
                r9 = 0
                int r11 = (r7 > r9 ? 1 : (r7 == r9 ? 0 : -1))
                if (r11 != 0) goto L_0x0093
                java.lang.Integer r7 = java.lang.Integer.valueOf(r3)
                goto L_0x0094
            L_0x0093:
                r7 = 0
            L_0x0094:
                r6.setTag(r7)
                org.telegram.ui.Components.RLottieImageView r6 = r0.imageView
                r11 = -2
                r12 = 1109393408(0x42200000, float:40.0)
                boolean r7 = org.telegram.messenger.LocaleController.isRTL
                r8 = 5
                if (r7 == 0) goto L_0x00a3
                r7 = 5
                goto L_0x00a4
            L_0x00a3:
                r7 = 3
            L_0x00a4:
                r13 = r7 | 16
                r14 = 0
                r15 = 0
                r16 = 0
                r17 = 0
                android.widget.FrameLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createFrame(r11, r12, r13, r14, r15, r16, r17)
                r0.addView(r6, r7)
                org.telegram.ui.Components.RLottieDrawable r6 = r0.speakerDrawable
                double r11 = r0.currentProgress
                int r7 = (r11 > r9 ? 1 : (r11 == r9 ? 0 : -1))
                if (r7 != 0) goto L_0x00be
                r7 = 17
                goto L_0x00c0
            L_0x00be:
                r7 = 34
            L_0x00c0:
                r6.setCustomEndFrame(r7)
                org.telegram.ui.Components.RLottieDrawable r6 = r0.speakerDrawable
                int r7 = r6.getCustomEndFrame()
                int r7 = r7 - r3
                r6.setCurrentFrame(r7, r4, r3)
                android.widget.TextView r6 = new android.widget.TextView
                r6.<init>(r1)
                r0.textView = r6
                r6.setLines(r3)
                android.widget.TextView r1 = r0.textView
                r1.setSingleLine(r3)
                android.widget.TextView r1 = r0.textView
                r1.setGravity(r2)
                android.widget.TextView r1 = r0.textView
                android.text.TextUtils$TruncateAt r6 = android.text.TextUtils.TruncateAt.END
                r1.setEllipsize(r6)
                android.widget.TextView r1 = r0.textView
                java.lang.String r6 = "voipgroup_actionBarItems"
                int r6 = org.telegram.ui.ActionBar.Theme.getColor(r6)
                r1.setTextColor(r6)
                android.widget.TextView r1 = r0.textView
                r6 = 1098907648(0x41800000, float:16.0)
                r1.setTextSize(r3, r6)
                org.telegram.tgnet.TLRPC$TL_groupCallParticipant r1 = r0.currentParticipant
                int r1 = org.telegram.messenger.ChatObject.getParticipantVolume(r1)
                double r6 = (double) r1
                r11 = 4636737291354636288(0xNUM, double:100.0)
                java.lang.Double.isNaN(r6)
                double r6 = r6 / r11
                android.widget.TextView r1 = r0.textView
                java.util.Locale r13 = java.util.Locale.US
                java.lang.Object[] r14 = new java.lang.Object[r3]
                int r15 = (r6 > r9 ? 1 : (r6 == r9 ? 0 : -1))
                if (r15 <= 0) goto L_0x0118
                r9 = 4607182418800017408(0x3ffNUM, double:1.0)
                double r9 = java.lang.Math.max(r6, r9)
            L_0x0118:
                int r6 = (int) r9
                java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
                r14[r4] = r6
                java.lang.String r6 = "%d%%"
                java.lang.String r6 = java.lang.String.format(r13, r6, r14)
                r1.setText(r6)
                android.widget.TextView r1 = r0.textView
                boolean r6 = org.telegram.messenger.LocaleController.isRTL
                r7 = 1110179840(0x422CLASSNAME, float:43.0)
                if (r6 == 0) goto L_0x0132
                r6 = 0
                goto L_0x0136
            L_0x0132:
                int r6 = org.telegram.messenger.AndroidUtilities.dp(r7)
            L_0x0136:
                boolean r9 = org.telegram.messenger.LocaleController.isRTL
                if (r9 == 0) goto L_0x013f
                int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
                goto L_0x0140
            L_0x013f:
                r7 = 0
            L_0x0140:
                r1.setPadding(r6, r4, r7, r4)
                android.widget.TextView r1 = r0.textView
                boolean r6 = org.telegram.messenger.LocaleController.isRTL
                if (r6 == 0) goto L_0x014a
                r2 = 5
            L_0x014a:
                r2 = r2 | 16
                r6 = -2
                android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r6, r6, r2)
                r0.addView(r1, r2)
                android.graphics.Paint r1 = r0.paint2
                android.graphics.Paint$Style r2 = android.graphics.Paint.Style.STROKE
                r1.setStyle(r2)
                android.graphics.Paint r1 = r0.paint2
                r2 = 1069547520(0x3fCLASSNAME, float:1.5)
                int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
                float r2 = (float) r2
                r1.setStrokeWidth(r2)
                android.graphics.Paint r1 = r0.paint2
                android.graphics.Paint$Cap r2 = android.graphics.Paint.Cap.ROUND
                r1.setStrokeCap(r2)
                android.graphics.Paint r1 = r0.paint2
                r2 = -1
                r1.setColor(r2)
                org.telegram.tgnet.TLRPC$TL_groupCallParticipant r1 = r0.currentParticipant
                int r1 = org.telegram.messenger.ChatObject.getParticipantVolume(r1)
                double r1 = (double) r1
                java.lang.Double.isNaN(r1)
                double r1 = r1 / r11
                int r1 = (int) r1
                r2 = 0
            L_0x0181:
                float[] r6 = r0.volumeAlphas
                int r7 = r6.length
                if (r2 >= r7) goto L_0x019c
                if (r2 != 0) goto L_0x018a
                r7 = 0
                goto L_0x0191
            L_0x018a:
                if (r2 != r3) goto L_0x018f
                r7 = 50
                goto L_0x0191
            L_0x018f:
                r7 = 150(0x96, float:2.1E-43)
            L_0x0191:
                if (r1 <= r7) goto L_0x0196
                r6[r2] = r5
                goto L_0x0199
            L_0x0196:
                r7 = 0
                r6[r2] = r7
            L_0x0199:
                int r2 = r2 + 1
                goto L_0x0181
            L_0x019c:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.GroupCallActivity.VolumeSlider.<init>(org.telegram.ui.GroupCallActivity, android.content.Context, org.telegram.tgnet.TLRPC$TL_groupCallParticipant):void");
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int i, int i2) {
            super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f), NUM));
            double size = (double) View.MeasureSpec.getSize(i);
            double d = this.currentProgress;
            Double.isNaN(size);
            this.thumbX = (int) (size * d);
        }

        public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
            return onTouch(motionEvent);
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            return onTouch(motionEvent);
        }

        /* access modifiers changed from: package-private */
        public boolean onTouch(MotionEvent motionEvent) {
            if (motionEvent.getAction() == 0) {
                this.sx = motionEvent.getX();
                this.sy = motionEvent.getY();
                return true;
            }
            if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
                this.captured = false;
                if (motionEvent.getAction() == 1) {
                    if (Math.abs(motionEvent.getY() - this.sy) < ((float) ViewConfiguration.get(getContext()).getScaledTouchSlop())) {
                        int x = (int) motionEvent.getX();
                        this.thumbX = x;
                        if (x < 0) {
                            this.thumbX = 0;
                        } else if (x > getMeasuredWidth()) {
                            this.thumbX = getMeasuredWidth();
                        }
                        this.dragging = true;
                    }
                }
                if (this.dragging) {
                    if (motionEvent.getAction() == 1) {
                        double d = (double) this.thumbX;
                        double measuredWidth = (double) getMeasuredWidth();
                        Double.isNaN(d);
                        Double.isNaN(measuredWidth);
                        onSeekBarDrag(d / measuredWidth, true);
                    }
                    this.dragging = false;
                    invalidate();
                    return true;
                }
            } else if (motionEvent.getAction() == 2) {
                if (!this.captured) {
                    ViewConfiguration viewConfiguration = ViewConfiguration.get(getContext());
                    if (Math.abs(motionEvent.getY() - this.sy) <= ((float) viewConfiguration.getScaledTouchSlop()) && Math.abs(motionEvent.getX() - this.sx) > ((float) viewConfiguration.getScaledTouchSlop())) {
                        this.captured = true;
                        getParent().requestDisallowInterceptTouchEvent(true);
                        if (motionEvent.getY() >= 0.0f && motionEvent.getY() <= ((float) getMeasuredHeight())) {
                            int x2 = (int) motionEvent.getX();
                            this.thumbX = x2;
                            if (x2 < 0) {
                                this.thumbX = 0;
                            } else if (x2 > getMeasuredWidth()) {
                                this.thumbX = getMeasuredWidth();
                            }
                            this.dragging = true;
                            invalidate();
                            return true;
                        }
                    }
                } else if (this.dragging) {
                    int x3 = (int) motionEvent.getX();
                    this.thumbX = x3;
                    if (x3 < 0) {
                        this.thumbX = 0;
                    } else if (x3 > getMeasuredWidth()) {
                        this.thumbX = getMeasuredWidth();
                    }
                    double d2 = (double) this.thumbX;
                    double measuredWidth2 = (double) getMeasuredWidth();
                    Double.isNaN(d2);
                    Double.isNaN(measuredWidth2);
                    onSeekBarDrag(d2 / measuredWidth2, false);
                    invalidate();
                    return true;
                }
            }
            return false;
        }

        private void onSeekBarDrag(double d, boolean z) {
            TLObject tLObject;
            if (VoIPService.getSharedInstance() != null) {
                this.currentProgress = d;
                TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant = this.currentParticipant;
                tLRPC$TL_groupCallParticipant.volume = (int) (d * 20000.0d);
                int i = 0;
                tLRPC$TL_groupCallParticipant.volume_by_admin = false;
                tLRPC$TL_groupCallParticipant.flags |= 128;
                double participantVolume = (double) ChatObject.getParticipantVolume(tLRPC$TL_groupCallParticipant);
                Double.isNaN(participantVolume);
                double d2 = participantVolume / 100.0d;
                TextView textView2 = this.textView;
                Locale locale = Locale.US;
                Object[] objArr = new Object[1];
                objArr[0] = Integer.valueOf((int) (d2 > 0.0d ? Math.max(d2, 1.0d) : 0.0d));
                textView2.setText(String.format(locale, "%d%%", objArr));
                VoIPService sharedInstance = VoIPService.getSharedInstance();
                TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant2 = this.currentParticipant;
                sharedInstance.setParticipantVolume(tLRPC$TL_groupCallParticipant2.source, tLRPC$TL_groupCallParticipant2.volume);
                int i2 = null;
                if (z) {
                    int peerId = MessageObject.getPeerId(this.currentParticipant.peer);
                    if (peerId > 0) {
                        tLObject = this.this$0.accountInstance.getMessagesController().getUser(Integer.valueOf(peerId));
                    } else {
                        tLObject = this.this$0.accountInstance.getMessagesController().getChat(Integer.valueOf(-peerId));
                    }
                    if (this.currentParticipant.volume == 0) {
                        if (this.this$0.scrimPopupWindow != null) {
                            this.this$0.scrimPopupWindow.dismiss();
                            ActionBarPopupWindow unused = this.this$0.scrimPopupWindow = null;
                        }
                        this.this$0.dismissAvatarPreview(true);
                        GroupCallActivity groupCallActivity = this.this$0;
                        groupCallActivity.processSelectedOption(this.currentParticipant, peerId, ChatObject.canManageCalls(groupCallActivity.currentChat) ? 0 : 5);
                    } else {
                        VoIPService.getSharedInstance().editCallMember(tLObject, false, this.currentParticipant.volume, (Boolean) null);
                    }
                }
                if (this.currentProgress == 0.0d) {
                    i2 = 1;
                }
                if ((this.imageView.getTag() == null && i2 != null) || (this.imageView.getTag() != null && i2 == null)) {
                    this.speakerDrawable.setCustomEndFrame(this.currentProgress == 0.0d ? 17 : 34);
                    RLottieDrawable rLottieDrawable = this.speakerDrawable;
                    if (this.currentProgress != 0.0d) {
                        i = 17;
                    }
                    rLottieDrawable.setCurrentFrame(i);
                    this.speakerDrawable.start();
                    this.imageView.setTag(i2);
                }
            }
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            int i;
            int i2;
            float f;
            int i3 = this.currentColor;
            double d = this.currentProgress;
            if (d < 0.25d) {
                this.currentColor = -3385513;
            } else if (d > 0.25d && d < 0.5d) {
                this.currentColor = -3562181;
            } else if (d < 0.5d || d > 0.75d) {
                this.currentColor = -11688225;
            } else {
                this.currentColor = -11027349;
            }
            float f2 = 1.0f;
            if (i3 == 0) {
                i = this.currentColor;
                this.colorChangeProgress = 1.0f;
            } else {
                int offsetColor = AndroidUtilities.getOffsetColor(this.oldColor, i3, this.colorChangeProgress, 1.0f);
                if (i3 != this.currentColor) {
                    this.colorChangeProgress = 0.0f;
                    this.oldColor = offsetColor;
                }
                i = offsetColor;
            }
            this.paint.setColor(i);
            long elapsedRealtime = SystemClock.elapsedRealtime();
            long j = elapsedRealtime - this.lastUpdateTime;
            if (j > 17) {
                j = 17;
            }
            this.lastUpdateTime = elapsedRealtime;
            float f3 = this.colorChangeProgress;
            if (f3 < 1.0f) {
                float f4 = f3 + (((float) j) / 200.0f);
                this.colorChangeProgress = f4;
                if (f4 > 1.0f) {
                    this.colorChangeProgress = 1.0f;
                } else {
                    invalidate();
                }
            }
            this.path.reset();
            float[] fArr = this.radii;
            float f5 = 6.0f;
            float dp = (float) AndroidUtilities.dp(6.0f);
            fArr[7] = dp;
            fArr[6] = dp;
            int i4 = 1;
            fArr[1] = dp;
            fArr[0] = dp;
            float max = this.thumbX < AndroidUtilities.dp(12.0f) ? Math.max(0.0f, ((float) (this.thumbX - AndroidUtilities.dp(6.0f))) / ((float) AndroidUtilities.dp(6.0f))) : 1.0f;
            float[] fArr2 = this.radii;
            float dp2 = ((float) AndroidUtilities.dp(6.0f)) * max;
            fArr2[5] = dp2;
            fArr2[4] = dp2;
            fArr2[3] = dp2;
            fArr2[2] = dp2;
            this.rect.set(0.0f, 0.0f, (float) this.thumbX, (float) getMeasuredHeight());
            this.path.addRoundRect(this.rect, this.radii, Path.Direction.CW);
            this.path.close();
            canvas.drawPath(this.path, this.paint);
            double participantVolume = (double) ChatObject.getParticipantVolume(this.currentParticipant);
            Double.isNaN(participantVolume);
            int i5 = (int) (participantVolume / 100.0d);
            int left = this.imageView.getLeft() + (this.imageView.getMeasuredWidth() / 2) + AndroidUtilities.dp(5.0f);
            int top = this.imageView.getTop() + (this.imageView.getMeasuredHeight() / 2);
            int i6 = 0;
            while (i6 < this.volumeAlphas.length) {
                if (i6 == 0) {
                    f = (float) AndroidUtilities.dp(f5);
                    i2 = 0;
                } else if (i6 == i4) {
                    f = (float) AndroidUtilities.dp(10.0f);
                    i2 = 50;
                } else {
                    f = (float) AndroidUtilities.dp(14.0f);
                    i2 = 150;
                }
                float[] fArr3 = this.volumeAlphas;
                float dp3 = ((float) AndroidUtilities.dp(2.0f)) * (f2 - fArr3[i6]);
                this.paint2.setAlpha((int) (fArr3[i6] * 255.0f));
                float f6 = (float) left;
                float f7 = (float) top;
                this.rect.set((f6 - f) + dp3, (f7 - f) + dp3, (f6 + f) - dp3, (f7 + f) - dp3);
                int i7 = i2;
                int i8 = i6;
                canvas.drawArc(this.rect, -50.0f, 100.0f, false, this.paint2);
                if (i5 > i7) {
                    float[] fArr4 = this.volumeAlphas;
                    if (fArr4[i8] < 1.0f) {
                        fArr4[i8] = fArr4[i8] + (((float) j) / 180.0f);
                        if (fArr4[i8] > 1.0f) {
                            fArr4[i8] = 1.0f;
                        }
                        invalidate();
                    }
                } else {
                    float[] fArr5 = this.volumeAlphas;
                    if (fArr5[i8] > 0.0f) {
                        fArr5[i8] = fArr5[i8] - (((float) j) / 180.0f);
                        if (fArr5[i8] < 0.0f) {
                            fArr5[i8] = 0.0f;
                        }
                        invalidate();
                    }
                }
                i6 = i8 + 1;
                Canvas canvas2 = canvas;
                f2 = 1.0f;
                i4 = 1;
                f5 = 6.0f;
            }
        }
    }

    private class WeavingState {
        /* access modifiers changed from: private */
        public int currentState;
        private float duration;
        private Matrix matrix = new Matrix();
        /* access modifiers changed from: private */
        public Shader shader;
        private float startX;
        private float startY;
        private float targetX = -1.0f;
        private float targetY = -1.0f;
        private float time;

        public WeavingState(int i) {
            this.currentState = i;
        }

        public void update(int i, int i2, int i3, long j) {
            float f;
            if (this.shader != null) {
                float f2 = this.duration;
                if (f2 == 0.0f || this.time >= f2) {
                    this.duration = (float) (Utilities.random.nextInt(200) + 1500);
                    this.time = 0.0f;
                    if (this.targetX == -1.0f) {
                        setTarget();
                    }
                    this.startX = this.targetX;
                    this.startY = this.targetY;
                    setTarget();
                }
                float f3 = (float) j;
                float access$1100 = this.time + ((BlobDrawable.GRADIENT_SPEED_MIN + 0.5f) * f3) + (f3 * BlobDrawable.GRADIENT_SPEED_MAX * 2.0f * GroupCallActivity.this.amplitude);
                this.time = access$1100;
                float f4 = this.duration;
                if (access$1100 > f4) {
                    this.time = f4;
                }
                float interpolation = CubicBezierInterpolator.EASE_OUT.getInterpolation(this.time / f4);
                float f5 = (float) i3;
                float f6 = this.startX;
                float f7 = (((float) i2) + ((f6 + ((this.targetX - f6) * interpolation)) * f5)) - 200.0f;
                float f8 = this.startY;
                float f9 = (((float) i) + (f5 * (f8 + ((this.targetY - f8) * interpolation)))) - 200.0f;
                if (GroupCallActivity.isGradientState(this.currentState)) {
                    f = 1.0f;
                } else {
                    f = this.currentState == 1 ? 4.0f : 2.5f;
                }
                float dp = (((float) AndroidUtilities.dp(122.0f)) / 400.0f) * f;
                this.matrix.reset();
                this.matrix.postTranslate(f7, f9);
                this.matrix.postScale(dp, dp, f7 + 200.0f, f9 + 200.0f);
                this.shader.setLocalMatrix(this.matrix);
            }
        }

        private void setTarget() {
            if (GroupCallActivity.isGradientState(this.currentState)) {
                this.targetX = ((((float) Utilities.random.nextInt(100)) * 0.2f) / 100.0f) + 0.85f;
                this.targetY = 1.0f;
            } else if (this.currentState == 1) {
                this.targetX = ((((float) Utilities.random.nextInt(100)) * 0.3f) / 100.0f) + 0.2f;
                this.targetY = ((((float) Utilities.random.nextInt(100)) * 0.3f) / 100.0f) + 0.7f;
            } else {
                this.targetX = ((((float) Utilities.random.nextInt(100)) / 100.0f) * 0.2f) + 0.8f;
                this.targetY = ((float) Utilities.random.nextInt(100)) / 100.0f;
            }
        }
    }

    private void prepareBlurBitmap() {
        if (this.blurredView != null) {
            int measuredWidth = (int) (((float) (this.containerView.getMeasuredWidth() - (this.backgroundPaddingLeft * 2))) / 6.0f);
            int measuredHeight = (int) (((float) (this.containerView.getMeasuredHeight() - AndroidUtilities.statusBarHeight)) / 6.0f);
            Bitmap createBitmap = Bitmap.createBitmap(measuredWidth, measuredHeight, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(createBitmap);
            canvas.scale(0.16666667f, 0.16666667f);
            canvas.save();
            canvas.translate(0.0f, (float) (-AndroidUtilities.statusBarHeight));
            this.parentActivity.getActionBarLayout().draw(canvas);
            canvas.drawColor(ColorUtils.setAlphaComponent(-16777216, 76));
            canvas.restore();
            canvas.save();
            canvas.translate(this.containerView.getX(), (float) (-AndroidUtilities.statusBarHeight));
            this.containerView.draw(canvas);
            Utilities.stackBlurBitmap(createBitmap, Math.max(7, Math.max(measuredWidth, measuredHeight) / 180));
            this.blurredView.setBackground(new BitmapDrawable(createBitmap));
            this.blurredView.setAlpha(0.0f);
            this.blurredView.setVisibility(0);
        }
    }

    /* access modifiers changed from: protected */
    public boolean onCustomOpenAnimation() {
        groupCallUiVisible = true;
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.groupCallVisibilityChanged, new Object[0]);
        GroupCallPip.updateVisibility(getContext());
        return super.onCustomOpenAnimation();
    }

    public void dismiss() {
        groupCallUiVisible = false;
        GroupVoipInviteAlert groupVoipInviteAlert2 = this.groupVoipInviteAlert;
        if (groupVoipInviteAlert2 != null) {
            groupVoipInviteAlert2.dismiss();
        }
        this.delayedGroupCallUpdated = true;
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.groupCallVisibilityChanged, new Object[0]);
        this.accountInstance.getNotificationCenter().removeObserver(this, NotificationCenter.needShowAlert);
        this.accountInstance.getNotificationCenter().removeObserver(this, NotificationCenter.groupCallUpdated);
        this.accountInstance.getNotificationCenter().removeObserver(this, NotificationCenter.chatInfoDidLoad);
        this.accountInstance.getNotificationCenter().removeObserver(this, NotificationCenter.didLoadChatAdmins);
        this.accountInstance.getNotificationCenter().removeObserver(this, NotificationCenter.applyGroupCallVisibleParticipants);
        this.accountInstance.getNotificationCenter().removeObserver(this, NotificationCenter.userInfoDidLoad);
        this.accountInstance.getNotificationCenter().removeObserver(this, NotificationCenter.mainUserInfoChanged);
        this.accountInstance.getNotificationCenter().removeObserver(this, NotificationCenter.updateInterfaces);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.webRtcMicAmplitudeEvent);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didEndCall);
        super.dismiss();
    }

    /* access modifiers changed from: private */
    public boolean isStillConnecting() {
        int i = this.currentCallState;
        return i == 1 || i == 2 || i == 6 || i == 5;
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        int i3;
        String str;
        TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant;
        int i4;
        String str2;
        String str3;
        TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant2;
        RecyclerView.ViewHolder findViewHolderForAdapterPosition;
        int i5;
        int i6 = 0;
        if (i == NotificationCenter.groupCallUpdated) {
            Long l = objArr[1];
            ChatObject.Call call2 = this.call;
            if (call2 != null && call2.call.id == l.longValue()) {
                ChatObject.Call call3 = this.call;
                if (call3.call instanceof TLRPC$TL_groupCallDiscarded) {
                    dismiss();
                    return;
                }
                if (this.creatingServiceTime == 0 && (((i5 = this.muteButtonState) == 7 || i5 == 5 || i5 == 6) && !call3.isScheduled())) {
                    try {
                        Intent intent = new Intent(this.parentActivity, VoIPService.class);
                        intent.putExtra("chat_id", this.currentChat.id);
                        intent.putExtra("createGroupCall", false);
                        intent.putExtra("hasFewPeers", this.scheduleHasFewPeers);
                        intent.putExtra("peerChannelId", this.schedulePeer.channel_id);
                        intent.putExtra("peerChatId", this.schedulePeer.chat_id);
                        intent.putExtra("peerUserId", this.schedulePeer.user_id);
                        intent.putExtra("hash", this.scheduledHash);
                        intent.putExtra("peerAccessHash", this.schedulePeer.access_hash);
                        intent.putExtra("is_outgoing", true);
                        intent.putExtra("start_incall_activity", false);
                        intent.putExtra("account", this.accountInstance.getCurrentAccount());
                        intent.putExtra("scheduleDate", this.scheduleStartAt);
                        this.parentActivity.startService(intent);
                    } catch (Throwable th) {
                        FileLog.e(th);
                    }
                    this.creatingServiceTime = SystemClock.elapsedRealtime();
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public final void run() {
                            GroupCallActivity.this.lambda$didReceivedNotification$2$GroupCallActivity();
                        }
                    }, 3000);
                }
                if (!this.callInitied && VoIPService.getSharedInstance() != null) {
                    this.call.addSelfDummyParticipant(false);
                    initCreatedGroupCall();
                    VoIPService.getSharedInstance().playConnectedSound();
                }
                updateItems();
                int childCount = this.listView.getChildCount();
                for (int i7 = 0; i7 < childCount; i7++) {
                    View childAt = this.listView.getChildAt(i7);
                    if (childAt instanceof GroupCallUserCell) {
                        ((GroupCallUserCell) childAt).applyParticipantChanges(true);
                    }
                }
                if (this.scrimView != null) {
                    this.delayedGroupCallUpdated = true;
                } else {
                    applyCallParticipantUpdates();
                }
                if (this.actionBar != null) {
                    this.actionBar.setSubtitle(LocaleController.formatPluralString("Participants", this.call.call.participants_count + (this.listAdapter.addSelfToCounter() ? 1 : 0)));
                }
                boolean booleanValue = objArr[2].booleanValue();
                if (this.muteButtonState == 4) {
                    i6 = 1;
                }
                updateState(true, booleanValue);
                updateTitle(true);
                if (i6 != 0) {
                    int i8 = this.muteButtonState;
                    if (i8 == 1 || i8 == 0) {
                        getUndoView().showWithAction(0, 38, (Runnable) null);
                        if (VoIPService.getSharedInstance() != null) {
                            VoIPService.getSharedInstance().playAllowTalkSound();
                        }
                    }
                }
            }
        } else if (i == NotificationCenter.webRtcMicAmplitudeEvent) {
            float floatValue = objArr[0].floatValue();
            setAmplitude((double) (4000.0f * floatValue));
            ChatObject.Call call4 = this.call;
            if (call4 != null && this.listView != null && (tLRPC$TL_groupCallParticipant2 = call4.participants.get(MessageObject.getPeerId(this.selfPeer))) != null) {
                int indexOf = (this.delayedGroupCallUpdated ? this.oldParticipants : this.call.sortedParticipants).indexOf(tLRPC$TL_groupCallParticipant2);
                if (indexOf >= 0 && (findViewHolderForAdapterPosition = this.listView.findViewHolderForAdapterPosition(indexOf + this.listAdapter.usersStartRow)) != null) {
                    View view = findViewHolderForAdapterPosition.itemView;
                    if (view instanceof GroupCallUserCell) {
                        ((GroupCallUserCell) view).setAmplitude((double) (floatValue * 15.0f));
                        if (findViewHolderForAdapterPosition.itemView == this.scrimView && !this.contentFullyOverlayed) {
                            this.containerView.invalidate();
                        }
                    }
                }
            }
        } else if (i == NotificationCenter.needShowAlert) {
            if (objArr[0].intValue() == 6) {
                String str4 = objArr[1];
                if ("GROUPCALL_PARTICIPANTS_TOO_MUCH".equals(str4)) {
                    str3 = LocaleController.getString("VoipGroupTooMuch", NUM);
                } else if ("ANONYMOUS_CALLS_DISABLED".equals(str4) || "GROUPCALL_ANONYMOUS_FORBIDDEN".equals(str4)) {
                    str3 = LocaleController.getString("VoipGroupJoinAnonymousAdmin", NUM);
                } else {
                    str3 = LocaleController.getString("ErrorOccurred", NUM) + "\n" + str4;
                }
                AlertDialog.Builder createSimpleAlert = AlertsCreator.createSimpleAlert(getContext(), LocaleController.getString("VoipGroupVoiceChat", NUM), str3);
                createSimpleAlert.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    public final void onDismiss(DialogInterface dialogInterface) {
                        GroupCallActivity.this.lambda$didReceivedNotification$3$GroupCallActivity(dialogInterface);
                    }
                });
                try {
                    createSimpleAlert.show();
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
            }
        } else if (i == NotificationCenter.didEndCall) {
            if (VoIPService.getSharedInstance() == null) {
                dismiss();
            }
        } else if (i == NotificationCenter.chatInfoDidLoad) {
            TLRPC$ChatFull tLRPC$ChatFull = objArr[0];
            if (tLRPC$ChatFull.id == this.currentChat.id) {
                updateItems();
                updateState(isShowing(), false);
            }
            int peerId = MessageObject.getPeerId(this.selfPeer);
            ChatObject.Call call5 = this.call;
            if (call5 != null && tLRPC$ChatFull.id == (-peerId) && (tLRPC$TL_groupCallParticipant = call5.participants.get(peerId)) != null) {
                tLRPC$TL_groupCallParticipant.about = tLRPC$ChatFull.about;
                applyCallParticipantUpdates();
                AndroidUtilities.updateVisibleRows(this.listView);
                if (this.currentOptionsLayout != null) {
                    while (i6 < this.currentOptionsLayout.getChildCount()) {
                        View childAt2 = this.currentOptionsLayout.getChildAt(i6);
                        if ((childAt2 instanceof ActionBarMenuSubItem) && childAt2.getTag() != null && ((Integer) childAt2.getTag()).intValue() == 10) {
                            ActionBarMenuSubItem actionBarMenuSubItem = (ActionBarMenuSubItem) childAt2;
                            if (TextUtils.isEmpty(tLRPC$TL_groupCallParticipant.about)) {
                                i4 = NUM;
                                str2 = "VoipAddDescription";
                            } else {
                                i4 = NUM;
                                str2 = "VoipEditDescription";
                            }
                            actionBarMenuSubItem.setTextAndIcon(LocaleController.getString(str2, i4), TextUtils.isEmpty(tLRPC$TL_groupCallParticipant.about) ? NUM : NUM);
                        }
                        i6++;
                    }
                }
            }
        } else if (i == NotificationCenter.didLoadChatAdmins) {
            if (objArr[0].intValue() == this.currentChat.id) {
                updateItems();
                updateState(isShowing(), false);
            }
        } else if (i == NotificationCenter.applyGroupCallVisibleParticipants) {
            int childCount2 = this.listView.getChildCount();
            long longValue = objArr[0].longValue();
            while (i6 < childCount2) {
                RecyclerView.ViewHolder findContainingViewHolder = this.listView.findContainingViewHolder(this.listView.getChildAt(i6));
                if (findContainingViewHolder != null) {
                    View view2 = findContainingViewHolder.itemView;
                    if (view2 instanceof GroupCallUserCell) {
                        ((GroupCallUserCell) view2).getParticipant().lastVisibleDate = longValue;
                    }
                }
                i6++;
            }
        } else if (i == NotificationCenter.userInfoDidLoad) {
            Integer num = objArr[0];
            int peerId2 = MessageObject.getPeerId(this.selfPeer);
            if (this.call != null && peerId2 == num.intValue()) {
                TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant3 = this.call.participants.get(peerId2);
                tLRPC$TL_groupCallParticipant3.about = objArr[1].about;
                applyCallParticipantUpdates();
                AndroidUtilities.updateVisibleRows(this.listView);
                if (this.currentOptionsLayout != null) {
                    while (i6 < this.currentOptionsLayout.getChildCount()) {
                        View childAt3 = this.currentOptionsLayout.getChildAt(i6);
                        if ((childAt3 instanceof ActionBarMenuSubItem) && childAt3.getTag() != null && ((Integer) childAt3.getTag()).intValue() == 10) {
                            ActionBarMenuSubItem actionBarMenuSubItem2 = (ActionBarMenuSubItem) childAt3;
                            if (TextUtils.isEmpty(tLRPC$TL_groupCallParticipant3.about)) {
                                i3 = NUM;
                                str = "VoipAddBio";
                            } else {
                                i3 = NUM;
                                str = "VoipEditBio";
                            }
                            actionBarMenuSubItem2.setTextAndIcon(LocaleController.getString(str, i3), TextUtils.isEmpty(tLRPC$TL_groupCallParticipant3.about) ? NUM : NUM);
                        }
                        i6++;
                    }
                }
            }
        } else if (i == NotificationCenter.mainUserInfoChanged) {
            applyCallParticipantUpdates();
            AndroidUtilities.updateVisibleRows(this.listView);
        } else if (i == NotificationCenter.updateInterfaces && (objArr[0].intValue() & 16) != 0) {
            applyCallParticipantUpdates();
            AndroidUtilities.updateVisibleRows(this.listView);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$didReceivedNotification$2 */
    public /* synthetic */ void lambda$didReceivedNotification$2$GroupCallActivity() {
        if (isStillConnecting()) {
            updateState(true, false);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$didReceivedNotification$3 */
    public /* synthetic */ void lambda$didReceivedNotification$3$GroupCallActivity(DialogInterface dialogInterface) {
        dismiss();
    }

    /* access modifiers changed from: private */
    public void applyCallParticipantUpdates() {
        RecyclerView.ViewHolder findContainingViewHolder;
        ChatObject.Call call2 = this.call;
        if (call2 != null && !this.delayedGroupCallUpdated) {
            int peerId = MessageObject.getPeerId(call2.selfPeer);
            if (!(peerId == MessageObject.getPeerId(this.selfPeer) || this.call.participants.get(peerId) == null)) {
                this.selfPeer = this.call.selfPeer;
            }
            int childCount = this.listView.getChildCount();
            View view = null;
            int i = 0;
            for (int i2 = 0; i2 < childCount; i2++) {
                View childAt = this.listView.getChildAt(i2);
                RecyclerView.ViewHolder findContainingViewHolder2 = this.listView.findContainingViewHolder(childAt);
                if (!(findContainingViewHolder2 == null || findContainingViewHolder2.getAdapterPosition() == -1 || (view != null && i <= findContainingViewHolder2.getAdapterPosition()))) {
                    i = findContainingViewHolder2.getAdapterPosition();
                    view = childAt;
                }
            }
            try {
                ListAdapter listAdapter2 = this.listAdapter;
                UpdateCallback updateCallback = new UpdateCallback(listAdapter2);
                setOldRows(listAdapter2.addMemberRow, this.listAdapter.usersStartRow, this.listAdapter.usersEndRow, this.listAdapter.invitedStartRow, this.listAdapter.invitedEndRow);
                this.listAdapter.updateRows();
                DiffUtil.calculateDiff(this.diffUtilsCallback).dispatchUpdatesTo((ListUpdateCallback) updateCallback);
            } catch (Exception e) {
                FileLog.e((Throwable) e);
                this.listAdapter.notifyDataSetChanged();
            }
            this.call.saveActiveDates();
            if (view != null) {
                this.layoutManager.scrollToPositionWithOffset(i, view.getTop() - this.listView.getPaddingTop());
            }
            this.oldParticipants.clear();
            this.oldParticipants.addAll(this.call.sortedParticipants);
            this.oldInvited.clear();
            this.oldInvited.addAll(this.call.invitedUsers);
            this.oldCount = this.listAdapter.getItemCount();
            for (int i3 = 0; i3 < childCount; i3++) {
                View childAt2 = this.listView.getChildAt(i3);
                boolean z = childAt2 instanceof GroupCallUserCell;
                if ((z || (childAt2 instanceof GroupCallInvitedCell)) && (findContainingViewHolder = this.listView.findContainingViewHolder(childAt2)) != null) {
                    boolean z2 = true;
                    if (z) {
                        GroupCallUserCell groupCallUserCell = (GroupCallUserCell) childAt2;
                        if (findContainingViewHolder.getAdapterPosition() == this.listAdapter.getItemCount() - 2) {
                            z2 = false;
                        }
                        groupCallUserCell.setDrawDivider(z2);
                    } else {
                        GroupCallInvitedCell groupCallInvitedCell = (GroupCallInvitedCell) childAt2;
                        if (findContainingViewHolder.getAdapterPosition() == this.listAdapter.getItemCount() - 2) {
                            z2 = false;
                        }
                        groupCallInvitedCell.setDrawDivider(z2);
                    }
                }
            }
        }
    }

    private void updateRecordCallText() {
        if (this.call != null) {
            int currentTime = this.accountInstance.getConnectionsManager().getCurrentTime();
            ChatObject.Call call2 = this.call;
            int i = currentTime - call2.call.record_start_date;
            if (call2.recording) {
                this.recordItem.setSubtext(AndroidUtilities.formatDuration(i, false));
            } else {
                this.recordItem.setSubtext((String) null);
            }
        }
    }

    private void updateItems() {
        boolean z;
        TLObject tLObject;
        ChatObject.Call call2 = this.call;
        if (call2 == null || call2.isScheduled()) {
            this.pipItem.setVisibility(4);
            if (this.call == null) {
                this.otherItem.setVisibility(8);
                this.accountSwitchImageView.setVisibility(8);
                return;
            }
        }
        if (!this.changingPermissions) {
            TLRPC$Chat chat = this.accountInstance.getMessagesController().getChat(Integer.valueOf(this.currentChat.id));
            if (chat != null) {
                this.currentChat = chat;
            }
            int i = 1;
            int i2 = 0;
            if (ChatObject.canUserDoAdminAction(this.currentChat, 3)) {
                this.inviteItem.setVisibility(0);
                z = true;
            } else {
                this.inviteItem.setVisibility(8);
                z = false;
            }
            if (ChatObject.canManageCalls(this.currentChat)) {
                this.leaveItem.setVisibility(0);
                this.editTitleItem.setVisibility(0);
                if (this.call.isScheduled()) {
                    this.recordItem.setVisibility(8);
                } else {
                    this.recordItem.setVisibility(0);
                }
                this.recordCallDrawable.setRecording(this.call.recording);
                if (this.call.recording) {
                    if (this.updateCallRecordRunnable == null) {
                        $$Lambda$GroupCallActivity$SYpmSznsPaUV6Yu8SzC3tSphwmQ r0 = new Runnable() {
                            public final void run() {
                                GroupCallActivity.this.lambda$updateItems$4$GroupCallActivity();
                            }
                        };
                        this.updateCallRecordRunnable = r0;
                        AndroidUtilities.runOnUIThread(r0, 1000);
                    }
                    this.recordItem.setText(LocaleController.getString("VoipGroupStopRecordCall", NUM));
                } else {
                    Runnable runnable = this.updateCallRecordRunnable;
                    if (runnable != null) {
                        AndroidUtilities.cancelRunOnUIThread(runnable);
                        this.updateCallRecordRunnable = null;
                    }
                    this.recordItem.setText(LocaleController.getString("VoipGroupRecordCall", NUM));
                }
                updateRecordCallText();
                z = true;
            } else {
                this.leaveItem.setVisibility(8);
                this.editTitleItem.setVisibility(8);
                this.recordItem.setVisibility(8);
            }
            if (!ChatObject.canManageCalls(this.currentChat) || !this.call.call.can_change_join_muted) {
                this.permissionItem.setVisibility(8);
            } else {
                this.permissionItem.setVisibility(0);
                z = true;
            }
            this.otherItem.setVisibility(z ? 0 : 8);
            int i3 = 48;
            int i4 = 96;
            if ((VoIPService.getSharedInstance() == null || !VoIPService.getSharedInstance().hasFewPeers) && !this.scheduleHasFewPeers) {
                if (z) {
                    i3 = 96;
                }
                this.accountSelectCell.setVisibility(8);
                this.accountGap.setVisibility(8);
                this.accountSwitchImageView.setVisibility(8);
                i4 = i3;
            } else if (!z) {
                this.accountSwitchImageView.getImageReceiver().setCurrentAccount(this.currentAccount);
                int peerId = MessageObject.getPeerId(this.selfPeer);
                if (peerId > 0) {
                    TLRPC$User user = this.accountInstance.getMessagesController().getUser(Integer.valueOf(peerId));
                    this.accountSwitchAvatarDrawable.setInfo(user);
                    this.accountSwitchImageView.setImage(ImageLocation.getForUserOrChat(user, 1), "50_50", ImageLocation.getForUserOrChat(user, 2), "50_50", (Drawable) this.accountSwitchAvatarDrawable, (Object) user);
                } else {
                    TLRPC$Chat chat2 = this.accountInstance.getMessagesController().getChat(Integer.valueOf(-peerId));
                    this.accountSwitchAvatarDrawable.setInfo(chat2);
                    this.accountSwitchImageView.setImage(ImageLocation.getForUserOrChat(chat2, 1), "50_50", ImageLocation.getForUserOrChat(chat2, 2), "50_50", (Drawable) this.accountSwitchAvatarDrawable, (Object) chat2);
                }
                this.accountSelectCell.setVisibility(8);
                this.accountGap.setVisibility(8);
                this.accountSwitchImageView.setVisibility(0);
                z = true;
            } else {
                this.accountSwitchImageView.setVisibility(8);
                this.accountSelectCell.setVisibility(0);
                this.accountGap.setVisibility(0);
                int peerId2 = MessageObject.getPeerId(this.selfPeer);
                if (peerId2 > 0) {
                    tLObject = this.accountInstance.getMessagesController().getUser(Integer.valueOf(peerId2));
                } else {
                    tLObject = this.accountInstance.getMessagesController().getChat(Integer.valueOf(-peerId2));
                }
                this.accountSelectCell.setObject(tLObject);
            }
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.titleTextView.getLayoutParams();
            float f = (float) i4;
            if (layoutParams.rightMargin != AndroidUtilities.dp(f)) {
                layoutParams.rightMargin = AndroidUtilities.dp(f);
                this.titleTextView.requestLayout();
            }
            FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) this.menuItemsContainer.getLayoutParams();
            if (!z) {
                i2 = AndroidUtilities.dp(6.0f);
            }
            layoutParams2.rightMargin = i2;
            ActionBar actionBar2 = this.actionBar;
            int dp = AndroidUtilities.dp(48.0f);
            if (z) {
                i = 2;
            }
            actionBar2.setTitleRightMargin(dp * i);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$updateItems$4 */
    public /* synthetic */ void lambda$updateItems$4$GroupCallActivity() {
        updateRecordCallText();
        AndroidUtilities.runOnUIThread(this.updateCallRecordRunnable, 1000);
    }

    /* access modifiers changed from: protected */
    public void makeFocusable(BottomSheet bottomSheet, AlertDialog alertDialog, EditTextBoldCursor editTextBoldCursor, boolean z) {
        if (!this.enterEventSent) {
            BaseFragment baseFragment = this.parentActivity.getActionBarLayout().fragmentsStack.get(this.parentActivity.getActionBarLayout().fragmentsStack.size() - 1);
            if (baseFragment instanceof ChatActivity) {
                boolean needEnterText = ((ChatActivity) baseFragment).needEnterText();
                this.enterEventSent = true;
                this.anyEnterEventSent = true;
                AndroidUtilities.runOnUIThread(new Runnable(editTextBoldCursor, z, alertDialog) {
                    public final /* synthetic */ EditTextBoldCursor f$1;
                    public final /* synthetic */ boolean f$2;
                    public final /* synthetic */ AlertDialog f$3;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                        this.f$3 = r4;
                    }

                    public final void run() {
                        GroupCallActivity.lambda$makeFocusable$7(BottomSheet.this, this.f$1, this.f$2, this.f$3);
                    }
                }, needEnterText ? 200 : 0);
                return;
            }
            this.enterEventSent = true;
            this.anyEnterEventSent = true;
            if (bottomSheet != null) {
                bottomSheet.setFocusable(true);
            } else if (alertDialog != null) {
                alertDialog.setFocusable(true);
            }
            if (z) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public final void run() {
                        GroupCallActivity.lambda$makeFocusable$8(EditTextBoldCursor.this);
                    }
                }, 100);
            }
        }
    }

    static /* synthetic */ void lambda$makeFocusable$7(BottomSheet bottomSheet, EditTextBoldCursor editTextBoldCursor, boolean z, AlertDialog alertDialog) {
        if (bottomSheet != null && !bottomSheet.isDismissed()) {
            bottomSheet.setFocusable(true);
            editTextBoldCursor.requestFocus();
            if (z) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public final void run() {
                        AndroidUtilities.showKeyboard(EditTextBoldCursor.this);
                    }
                });
            }
        } else if (alertDialog != null && alertDialog.isShowing()) {
            alertDialog.setFocusable(true);
            editTextBoldCursor.requestFocus();
            if (z) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public final void run() {
                        AndroidUtilities.showKeyboard(EditTextBoldCursor.this);
                    }
                });
            }
        }
    }

    static /* synthetic */ void lambda$makeFocusable$8(EditTextBoldCursor editTextBoldCursor) {
        editTextBoldCursor.requestFocus();
        AndroidUtilities.showKeyboard(editTextBoldCursor);
    }

    public static void create(LaunchActivity launchActivity, AccountInstance accountInstance2, TLRPC$Chat tLRPC$Chat, TLRPC$InputPeer tLRPC$InputPeer, boolean z, String str) {
        TLRPC$Chat chat;
        if (groupCallInstance != null) {
            return;
        }
        if (tLRPC$InputPeer != null || VoIPService.getSharedInstance() != null) {
            if (tLRPC$InputPeer != null) {
                groupCallInstance = new GroupCallActivity(launchActivity, accountInstance2, accountInstance2.getMessagesController().getGroupCall(tLRPC$Chat.id, false), tLRPC$Chat, tLRPC$InputPeer, z, str);
            } else {
                ChatObject.Call call2 = VoIPService.getSharedInstance().groupCall;
                if (call2 != null && (chat = accountInstance2.getMessagesController().getChat(Integer.valueOf(call2.chatId))) != null) {
                    call2.addSelfDummyParticipant(true);
                    groupCallInstance = new GroupCallActivity(launchActivity, accountInstance2, call2, chat, (TLRPC$InputPeer) null, z, str);
                } else {
                    return;
                }
            }
            GroupCallActivity groupCallActivity = groupCallInstance;
            groupCallActivity.parentActivity = launchActivity;
            groupCallActivity.show();
        }
    }

    /* JADX WARNING: Illegal instructions before constructor call */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private GroupCallActivity(android.content.Context r41, org.telegram.messenger.AccountInstance r42, org.telegram.messenger.ChatObject.Call r43, org.telegram.tgnet.TLRPC$Chat r44, org.telegram.tgnet.TLRPC$InputPeer r45, boolean r46, java.lang.String r47) {
        /*
            r40 = this;
            r8 = r40
            r9 = r41
            r0 = r45
            r10 = 0
            r8.<init>(r9, r10)
            r11 = 2
            android.widget.TextView[] r1 = new android.widget.TextView[r11]
            r8.muteLabel = r1
            android.widget.TextView[] r1 = new android.widget.TextView[r11]
            r8.muteSubLabel = r1
            org.telegram.ui.Components.UndoView[] r1 = new org.telegram.ui.Components.UndoView[r11]
            r8.undoView = r1
            android.graphics.RectF r1 = new android.graphics.RectF
            r1.<init>()
            r8.rect = r1
            android.graphics.Paint r1 = new android.graphics.Paint
            r12 = 1
            r1.<init>(r12)
            r8.listViewBackgroundPaint = r1
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
            r8.oldParticipants = r1
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
            r8.oldInvited = r1
            r8.muteButtonState = r10
            android.graphics.Paint r1 = new android.graphics.Paint
            r2 = 7
            r1.<init>(r2)
            r8.paint = r1
            android.graphics.Paint r1 = new android.graphics.Paint
            r1.<init>(r2)
            r8.paintTmp = r1
            android.graphics.Paint r1 = new android.graphics.Paint
            r1.<init>(r12)
            r8.leaveBackgroundPaint = r1
            r13 = 8
            org.telegram.ui.GroupCallActivity$WeavingState[] r1 = new org.telegram.ui.GroupCallActivity.WeavingState[r13]
            r8.states = r1
            r14 = 1065353216(0x3var_, float:1.0)
            r8.switchProgress = r14
            r8.invalidateColors = r12
            r1 = 3
            int[] r1 = new int[r1]
            r8.colorsTmp = r1
            org.telegram.ui.GroupCallActivity$1 r1 = new org.telegram.ui.GroupCallActivity$1
            r1.<init>()
            r8.updateSchedeulRunnable = r1
            org.telegram.ui.-$$Lambda$GroupCallActivity$Fejzw3-BitRkLCnwqEMTIYvTsgw r1 = org.telegram.ui.$$Lambda$GroupCallActivity$Fejzw3BitRkLCnwqEMTIYvTsgw.INSTANCE
            r8.unmuteRunnable = r1
            org.telegram.ui.-$$Lambda$GroupCallActivity$kOH_8vLsAK1_zQgyxcm7-Ylrcfc r1 = new org.telegram.ui.-$$Lambda$GroupCallActivity$kOH_8vLsAK1_zQgyxcm7-Ylrcfc
            r1.<init>()
            r8.pressRunnable = r1
            java.lang.String[] r1 = new java.lang.String[r11]
            r8.invites = r1
            r15 = -1
            r8.popupAnimationIndex = r15
            org.telegram.ui.GroupCallActivity$39 r1 = new org.telegram.ui.GroupCallActivity$39
            r1.<init>()
            r8.diffUtilsCallback = r1
            r6 = r42
            r8.accountInstance = r6
            r1 = r43
            r8.call = r1
            r8.schedulePeer = r0
            r5 = r44
            r8.currentChat = r5
            r1 = r47
            r8.scheduledHash = r1
            int r1 = r42.getCurrentAccount()
            r8.currentAccount = r1
            r1 = r46
            r8.scheduleHasFewPeers = r1
            org.telegram.ui.GroupCallActivity$3 r1 = new org.telegram.ui.GroupCallActivity$3
            r1.<init>()
            r8.setDelegate(r1)
            r8.drawNavigationBar = r12
            int r1 = android.os.Build.VERSION.SDK_INT
            r2 = 30
            if (r1 < r2) goto L_0x00b2
            android.view.Window r1 = r40.getWindow()
            r2 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            r1.setNavigationBarColor(r2)
        L_0x00b2:
            r8.scrollNavBar = r12
            r7 = 0
            r8.navBarColorKey = r7
            org.telegram.ui.GroupCallActivity$4 r1 = new org.telegram.ui.GroupCallActivity$4
            r1.<init>()
            r8.scrimPaint = r1
            org.telegram.ui.-$$Lambda$GroupCallActivity$vH22aYH9Z49k0KlgGDw4ZCDf7pg r1 = new org.telegram.ui.-$$Lambda$GroupCallActivity$vH22aYH9Z49k0KlgGDw4ZCDf7pg
            r1.<init>()
            r8.setOnDismissListener(r1)
            r1 = 75
            r8.setDimBehindAlpha(r1)
            org.telegram.ui.GroupCallActivity$ListAdapter r1 = new org.telegram.ui.GroupCallActivity$ListAdapter
            r1.<init>(r9)
            r8.listAdapter = r1
            org.telegram.ui.GroupCallActivity$5 r1 = new org.telegram.ui.GroupCallActivity$5
            r1.<init>(r9)
            r8.actionBar = r1
            r2 = 2131165469(0x7var_d, float:1.7945156E38)
            r1.setBackButtonImage(r2)
            org.telegram.ui.ActionBar.ActionBar r1 = r8.actionBar
            r1.setOccupyStatusBar(r10)
            org.telegram.ui.ActionBar.ActionBar r1 = r8.actionBar
            r1.setAllowOverlayTitle(r10)
            org.telegram.ui.ActionBar.ActionBar r1 = r8.actionBar
            java.lang.String r16 = "voipgroup_actionBarItems"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r16)
            r1.setItemsColor(r2, r10)
            org.telegram.ui.ActionBar.ActionBar r1 = r8.actionBar
            java.lang.String r2 = "actionBarActionModeDefaultSelector"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r1.setItemsBackgroundColor(r2, r10)
            org.telegram.ui.ActionBar.ActionBar r1 = r8.actionBar
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r16)
            r1.setTitleColor(r2)
            org.telegram.ui.ActionBar.ActionBar r1 = r8.actionBar
            java.lang.String r2 = "voipgroup_lastSeenTextUnscrolled"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r1.setSubtitleColor(r2)
            org.telegram.ui.ActionBar.ActionBar r1 = r8.actionBar
            org.telegram.ui.GroupCallActivity$6 r2 = new org.telegram.ui.GroupCallActivity$6
            r2.<init>()
            r1.setActionBarMenuOnItemClick(r2)
            if (r0 == 0) goto L_0x0123
            r4 = r0
            goto L_0x012c
        L_0x0123:
            org.telegram.messenger.voip.VoIPService r1 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            org.telegram.tgnet.TLRPC$InputPeer r1 = r1.getGroupCallPeer()
            r4 = r1
        L_0x012c:
            if (r4 != 0) goto L_0x0142
            org.telegram.tgnet.TLRPC$TL_peerUser r1 = new org.telegram.tgnet.TLRPC$TL_peerUser
            r1.<init>()
            r8.selfPeer = r1
            org.telegram.messenger.AccountInstance r2 = r8.accountInstance
            org.telegram.messenger.UserConfig r2 = r2.getUserConfig()
            int r2 = r2.getClientUserId()
            r1.user_id = r2
            goto L_0x0171
        L_0x0142:
            boolean r1 = r4 instanceof org.telegram.tgnet.TLRPC$TL_inputPeerChannel
            if (r1 == 0) goto L_0x0152
            org.telegram.tgnet.TLRPC$TL_peerChannel r1 = new org.telegram.tgnet.TLRPC$TL_peerChannel
            r1.<init>()
            r8.selfPeer = r1
            int r2 = r4.channel_id
            r1.channel_id = r2
            goto L_0x0171
        L_0x0152:
            boolean r1 = r4 instanceof org.telegram.tgnet.TLRPC$TL_inputPeerUser
            if (r1 == 0) goto L_0x0162
            org.telegram.tgnet.TLRPC$TL_peerUser r1 = new org.telegram.tgnet.TLRPC$TL_peerUser
            r1.<init>()
            r8.selfPeer = r1
            int r2 = r4.user_id
            r1.user_id = r2
            goto L_0x0171
        L_0x0162:
            boolean r1 = r4 instanceof org.telegram.tgnet.TLRPC$TL_inputPeerChat
            if (r1 == 0) goto L_0x0171
            org.telegram.tgnet.TLRPC$TL_peerChat r1 = new org.telegram.tgnet.TLRPC$TL_peerChat
            r1.<init>()
            r8.selfPeer = r1
            int r2 = r4.chat_id
            r1.chat_id = r2
        L_0x0171:
            org.telegram.ui.-$$Lambda$GroupCallActivity$CDAstUeY1nlHMbUdC8gMFgpMZ6s r1 = new org.telegram.ui.-$$Lambda$GroupCallActivity$CDAstUeY1nlHMbUdC8gMFgpMZ6s
            r1.<init>()
            org.telegram.messenger.voip.VoIPService.audioLevelsCallback = r1
            org.telegram.messenger.AccountInstance r1 = r8.accountInstance
            org.telegram.messenger.NotificationCenter r1 = r1.getNotificationCenter()
            int r2 = org.telegram.messenger.NotificationCenter.groupCallUpdated
            r1.addObserver(r8, r2)
            org.telegram.messenger.AccountInstance r1 = r8.accountInstance
            org.telegram.messenger.NotificationCenter r1 = r1.getNotificationCenter()
            int r2 = org.telegram.messenger.NotificationCenter.needShowAlert
            r1.addObserver(r8, r2)
            org.telegram.messenger.AccountInstance r1 = r8.accountInstance
            org.telegram.messenger.NotificationCenter r1 = r1.getNotificationCenter()
            int r2 = org.telegram.messenger.NotificationCenter.chatInfoDidLoad
            r1.addObserver(r8, r2)
            org.telegram.messenger.AccountInstance r1 = r8.accountInstance
            org.telegram.messenger.NotificationCenter r1 = r1.getNotificationCenter()
            int r2 = org.telegram.messenger.NotificationCenter.didLoadChatAdmins
            r1.addObserver(r8, r2)
            org.telegram.messenger.AccountInstance r1 = r8.accountInstance
            org.telegram.messenger.NotificationCenter r1 = r1.getNotificationCenter()
            int r2 = org.telegram.messenger.NotificationCenter.applyGroupCallVisibleParticipants
            r1.addObserver(r8, r2)
            org.telegram.messenger.AccountInstance r1 = r8.accountInstance
            org.telegram.messenger.NotificationCenter r1 = r1.getNotificationCenter()
            int r2 = org.telegram.messenger.NotificationCenter.userInfoDidLoad
            r1.addObserver(r8, r2)
            org.telegram.messenger.AccountInstance r1 = r8.accountInstance
            org.telegram.messenger.NotificationCenter r1 = r1.getNotificationCenter()
            int r2 = org.telegram.messenger.NotificationCenter.mainUserInfoChanged
            r1.addObserver(r8, r2)
            org.telegram.messenger.AccountInstance r1 = r8.accountInstance
            org.telegram.messenger.NotificationCenter r1 = r1.getNotificationCenter()
            int r2 = org.telegram.messenger.NotificationCenter.updateInterfaces
            r1.addObserver(r8, r2)
            org.telegram.messenger.NotificationCenter r1 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r2 = org.telegram.messenger.NotificationCenter.webRtcMicAmplitudeEvent
            r1.addObserver(r8, r2)
            org.telegram.messenger.NotificationCenter r1 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r2 = org.telegram.messenger.NotificationCenter.didEndCall
            r1.addObserver(r8, r2)
            android.content.res.Resources r1 = r41.getResources()
            r2 = 2131166018(0x7var_, float:1.794627E38)
            android.graphics.drawable.Drawable r1 = r1.getDrawable(r2)
            android.graphics.drawable.Drawable r1 = r1.mutate()
            r8.shadowDrawable = r1
            org.telegram.ui.Components.RLottieDrawable r1 = new org.telegram.ui.Components.RLottieDrawable
            r18 = 2131558519(0x7f0d0077, float:1.8742356E38)
            r24 = 1116733440(0x42900000, float:72.0)
            int r20 = org.telegram.messenger.AndroidUtilities.dp(r24)
            int r21 = org.telegram.messenger.AndroidUtilities.dp(r24)
            r22 = 1
            r23 = 0
            java.lang.String r19 = "NUM"
            r17 = r1
            r17.<init>((int) r18, (java.lang.String) r19, (int) r20, (int) r21, (boolean) r22, (int[]) r23)
            r8.bigMicDrawable = r1
            org.telegram.ui.Components.RLottieDrawable r1 = new org.telegram.ui.Components.RLottieDrawable
            r26 = 2131558441(0x7f0d0029, float:1.8742198E38)
            int r28 = org.telegram.messenger.AndroidUtilities.dp(r24)
            int r29 = org.telegram.messenger.AndroidUtilities.dp(r24)
            r30 = 1
            r31 = 0
            java.lang.String r27 = "NUM"
            r25 = r1
            r25.<init>((int) r26, (java.lang.String) r27, (int) r28, (int) r29, (boolean) r30, (int[]) r31)
            r8.handDrawables = r1
            org.telegram.ui.GroupCallActivity$7 r1 = new org.telegram.ui.GroupCallActivity$7
            r1.<init>(r9)
            r8.containerView = r1
            r1.setFocusable(r12)
            android.view.ViewGroup r1 = r8.containerView
            r1.setFocusableInTouchMode(r12)
            android.view.ViewGroup r1 = r8.containerView
            r1.setWillNotDraw(r10)
            android.view.ViewGroup r1 = r8.containerView
            int r2 = r8.backgroundPaddingLeft
            r1.setPadding(r2, r10, r2, r10)
            android.view.ViewGroup r1 = r8.containerView
            r1.setKeepScreenOn(r12)
            android.view.ViewGroup r1 = r8.containerView
            r1.setClipChildren(r10)
            java.lang.String r1 = "fonts/rmedium.ttf"
            r2 = 17
            if (r0 == 0) goto L_0x02f3
            org.telegram.ui.ActionBar.SimpleTextView r3 = new org.telegram.ui.ActionBar.SimpleTextView
            r3.<init>(r9)
            r8.scheduleStartInTextView = r3
            r3.setGravity(r2)
            org.telegram.ui.ActionBar.SimpleTextView r3 = r8.scheduleStartInTextView
            r3.setTextColor(r15)
            org.telegram.ui.ActionBar.SimpleTextView r3 = r8.scheduleStartInTextView
            android.graphics.Typeface r7 = org.telegram.messenger.AndroidUtilities.getTypeface(r1)
            r3.setTypeface(r7)
            org.telegram.ui.ActionBar.SimpleTextView r3 = r8.scheduleStartInTextView
            r7 = 18
            r3.setTextSize(r7)
            org.telegram.ui.ActionBar.SimpleTextView r3 = r8.scheduleStartInTextView
            r7 = 2131628008(0x7f0e0fe8, float:1.8883297E38)
            java.lang.String r13 = "VoipChatStartsIn"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r13, r7)
            r3.setText(r7)
            android.view.ViewGroup r3 = r8.containerView
            org.telegram.ui.ActionBar.SimpleTextView r7 = r8.scheduleStartInTextView
            r25 = -2
            r26 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r27 = 49
            r28 = 1101529088(0x41a80000, float:21.0)
            r29 = 0
            r30 = 1101529088(0x41a80000, float:21.0)
            r31 = 1134264320(0x439b8000, float:311.0)
            android.widget.FrameLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createFrame(r25, r26, r27, r28, r29, r30, r31)
            r3.addView(r7, r13)
            org.telegram.ui.GroupCallActivity$8 r3 = new org.telegram.ui.GroupCallActivity$8
            r3.<init>(r9)
            r8.scheduleTimeTextView = r3
            r3.setGravity(r2)
            org.telegram.ui.ActionBar.SimpleTextView r3 = r8.scheduleTimeTextView
            r3.setTextColor(r15)
            org.telegram.ui.ActionBar.SimpleTextView r3 = r8.scheduleTimeTextView
            android.graphics.Typeface r7 = org.telegram.messenger.AndroidUtilities.getTypeface(r1)
            r3.setTypeface(r7)
            org.telegram.ui.ActionBar.SimpleTextView r3 = r8.scheduleTimeTextView
            r7 = 60
            r3.setTextSize(r7)
            android.view.ViewGroup r3 = r8.containerView
            org.telegram.ui.ActionBar.SimpleTextView r7 = r8.scheduleTimeTextView
            r31 = 1130823680(0x43670000, float:231.0)
            android.widget.FrameLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createFrame(r25, r26, r27, r28, r29, r30, r31)
            r3.addView(r7, r13)
            org.telegram.ui.ActionBar.SimpleTextView r3 = new org.telegram.ui.ActionBar.SimpleTextView
            r3.<init>(r9)
            r8.scheduleStartAtTextView = r3
            r3.setGravity(r2)
            org.telegram.ui.ActionBar.SimpleTextView r3 = r8.scheduleStartAtTextView
            r3.setTextColor(r15)
            org.telegram.ui.ActionBar.SimpleTextView r3 = r8.scheduleStartAtTextView
            android.graphics.Typeface r7 = org.telegram.messenger.AndroidUtilities.getTypeface(r1)
            r3.setTypeface(r7)
            org.telegram.ui.ActionBar.SimpleTextView r3 = r8.scheduleStartAtTextView
            r7 = 18
            r3.setTextSize(r7)
            android.view.ViewGroup r3 = r8.containerView
            org.telegram.ui.ActionBar.SimpleTextView r7 = r8.scheduleStartAtTextView
            r31 = 1128857600(0x43490000, float:201.0)
            android.widget.FrameLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createFrame(r25, r26, r27, r28, r29, r30, r31)
            r3.addView(r7, r13)
        L_0x02f3:
            org.telegram.ui.GroupCallActivity$9 r3 = new org.telegram.ui.GroupCallActivity$9
            r3.<init>(r9)
            r8.listView = r3
            r3.setClipToPadding(r10)
            org.telegram.ui.Components.RecyclerListView r3 = r8.listView
            r3.setClipChildren(r10)
            org.telegram.ui.GroupCallActivity$10 r3 = new org.telegram.ui.GroupCallActivity$10
            r3.<init>()
            r8.itemAnimator = r3
            r3.setDelayAnimations(r10)
            org.telegram.ui.Components.RecyclerListView r3 = r8.listView
            androidx.recyclerview.widget.DefaultItemAnimator r7 = r8.itemAnimator
            r3.setItemAnimator(r7)
            org.telegram.ui.Components.RecyclerListView r3 = r8.listView
            org.telegram.ui.GroupCallActivity$11 r7 = new org.telegram.ui.GroupCallActivity$11
            r7.<init>()
            r3.setOnScrollListener(r7)
            org.telegram.ui.Components.RecyclerListView r3 = r8.listView
            r3.setVerticalScrollBarEnabled(r10)
            org.telegram.ui.Components.RecyclerListView r3 = r8.listView
            org.telegram.ui.Components.FillLastLinearLayoutManager r7 = new org.telegram.ui.Components.FillLastLinearLayoutManager
            android.content.Context r19 = r40.getContext()
            r20 = 1
            r21 = 0
            r22 = 0
            org.telegram.ui.Components.RecyclerListView r13 = r8.listView
            r18 = r7
            r23 = r13
            r18.<init>(r19, r20, r21, r22, r23)
            r8.layoutManager = r7
            r3.setLayoutManager(r7)
            org.telegram.ui.Components.FillLastLinearLayoutManager r3 = r8.layoutManager
            r3.setBind(r10)
            android.view.ViewGroup r3 = r8.containerView
            org.telegram.ui.Components.RecyclerListView r7 = r8.listView
            r25 = -1
            r26 = -1082130432(0xffffffffbvar_, float:-1.0)
            r27 = 51
            r28 = 1096810496(0x41600000, float:14.0)
            r29 = 1096810496(0x41600000, float:14.0)
            r30 = 1096810496(0x41600000, float:14.0)
            r31 = 1130823680(0x43670000, float:231.0)
            android.widget.FrameLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createFrame(r25, r26, r27, r28, r29, r30, r31)
            r3.addView(r7, r13)
            org.telegram.ui.Components.RecyclerListView r3 = r8.listView
            org.telegram.ui.GroupCallActivity$ListAdapter r7 = r8.listAdapter
            r3.setAdapter(r7)
            org.telegram.ui.Components.RecyclerListView r3 = r8.listView
            r7 = 13
            r3.setTopBottomSelectorRadius(r7)
            org.telegram.ui.Components.RecyclerListView r3 = r8.listView
            java.lang.String r13 = "voipgroup_listSelector"
            int r7 = org.telegram.ui.ActionBar.Theme.getColor(r13)
            r3.setSelectorDrawableColor(r7)
            org.telegram.ui.Components.RecyclerListView r3 = r8.listView
            org.telegram.ui.-$$Lambda$GroupCallActivity$ZokxvRSLQ4l6C7Xg_p-yCAOmdMQ r7 = new org.telegram.ui.-$$Lambda$GroupCallActivity$ZokxvRSLQ4l6C7Xg_p-yCAOmdMQ
            r7.<init>()
            r3.setOnItemClickListener((org.telegram.ui.Components.RecyclerListView.OnItemClickListenerExtended) r7)
            org.telegram.ui.Components.RecyclerListView r3 = r8.listView
            org.telegram.ui.-$$Lambda$GroupCallActivity$8t-jDhR8Ase3T3dlEEfyuX840Ow r7 = new org.telegram.ui.-$$Lambda$GroupCallActivity$8t-jDhR8Ase3T3dlEEfyuX840Ow
            r7.<init>()
            r3.setOnItemLongClickListener((org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener) r7)
            org.telegram.ui.GroupCallActivity$13 r3 = new org.telegram.ui.GroupCallActivity$13
            r3.<init>(r9)
            r8.buttonsContainer = r3
            r3.setWillNotDraw(r10)
            android.view.ViewGroup r3 = r8.containerView
            android.widget.FrameLayout r7 = r8.buttonsContainer
            r11 = 231(0xe7, float:3.24E-43)
            r14 = 83
            android.widget.FrameLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createFrame(r15, r11, r14)
            r3.addView(r7, r11)
            r11 = 1092616192(0x41200000, float:10.0)
            r14 = 1096810496(0x41600000, float:14.0)
            if (r0 == 0) goto L_0x059f
            android.widget.TextView r0 = new android.widget.TextView
            r0.<init>(r9)
            r8.scheduleInfoTextView = r0
            r0.setGravity(r2)
            android.widget.TextView r0 = r8.scheduleInfoTextView
            r3 = -8682615(0xffffffffff7b8389, float:-3.343192E38)
            r0.setTextColor(r3)
            android.widget.TextView r0 = r8.scheduleInfoTextView
            r0.setTextSize(r12, r14)
            org.telegram.tgnet.TLRPC$Chat r0 = r8.currentChat
            boolean r0 = org.telegram.messenger.ChatObject.isChannel(r0)
            if (r0 == 0) goto L_0x03d7
            org.telegram.tgnet.TLRPC$Chat r0 = r8.currentChat
            boolean r0 = r0.megagroup
            if (r0 != 0) goto L_0x03d7
            android.widget.TextView r0 = r8.scheduleInfoTextView
            java.lang.Integer r3 = java.lang.Integer.valueOf(r12)
            r0.setTag(r3)
        L_0x03d7:
            android.view.ViewGroup r0 = r8.containerView
            android.widget.TextView r3 = r8.scheduleInfoTextView
            r25 = -2
            r26 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r27 = 81
            r28 = 1101529088(0x41a80000, float:21.0)
            r29 = 0
            r30 = 1101529088(0x41a80000, float:21.0)
            r31 = 1120403456(0x42CLASSNAME, float:100.0)
            android.widget.FrameLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createFrame(r25, r26, r27, r28, r29, r30, r31)
            r0.addView(r3, r7)
            org.telegram.ui.Components.NumberPicker r7 = new org.telegram.ui.Components.NumberPicker
            r7.<init>(r9)
            r7.setTextColor(r15)
            r0 = -9598483(0xffffffffff6d89ed, float:-3.1574319E38)
            r7.setSelectorColor(r0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r11)
            r7.setTextOffset(r3)
            r3 = 5
            r7.setItemCount(r3)
            org.telegram.ui.GroupCallActivity$14 r14 = new org.telegram.ui.GroupCallActivity$14
            r14.<init>(r8, r9)
            r14.setItemCount(r3)
            r14.setTextColor(r15)
            r14.setSelectorColor(r0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r11)
            int r10 = -r10
            r14.setTextOffset(r10)
            org.telegram.ui.GroupCallActivity$15 r10 = new org.telegram.ui.GroupCallActivity$15
            r10.<init>(r8, r9)
            r10.setItemCount(r3)
            r10.setTextColor(r15)
            r10.setSelectorColor(r0)
            r0 = 1107820544(0x42080000, float:34.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r0 = -r0
            r10.setTextOffset(r0)
            android.widget.TextView r0 = new android.widget.TextView
            r0.<init>(r9)
            r8.scheduleButtonTextView = r0
            r0.setLines(r12)
            android.widget.TextView r0 = r8.scheduleButtonTextView
            r0.setSingleLine(r12)
            android.widget.TextView r0 = r8.scheduleButtonTextView
            android.text.TextUtils$TruncateAt r3 = android.text.TextUtils.TruncateAt.END
            r0.setEllipsize(r3)
            android.widget.TextView r0 = r8.scheduleButtonTextView
            r0.setGravity(r2)
            android.widget.TextView r0 = r8.scheduleButtonTextView
            r2 = 1082130432(0x40800000, float:4.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r3 = 1056964608(0x3var_, float:0.5)
            r11 = 0
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.createSimpleSelectorRoundRectDrawable(r2, r11, r3)
            r0.setBackground(r2)
            android.widget.TextView r0 = r8.scheduleButtonTextView
            r0.setTextColor(r15)
            android.widget.TextView r0 = r8.scheduleButtonTextView
            android.graphics.Typeface r1 = org.telegram.messenger.AndroidUtilities.getTypeface(r1)
            r0.setTypeface(r1)
            android.widget.TextView r0 = r8.scheduleButtonTextView
            r1 = 1096810496(0x41600000, float:14.0)
            r0.setTextSize(r12, r1)
            android.view.ViewGroup r0 = r8.containerView
            android.widget.TextView r1 = r8.scheduleButtonTextView
            r25 = -1
            r26 = 1111490560(0x42400000, float:48.0)
            r31 = 1101266944(0x41a40000, float:20.5)
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r25, r26, r27, r28, r29, r30, r31)
            r0.addView(r1, r2)
            android.widget.TextView r11 = r8.scheduleButtonTextView
            org.telegram.ui.-$$Lambda$GroupCallActivity$KpeDb7b4E978_kuhMbqLqjaO_w4 r3 = new org.telegram.ui.-$$Lambda$GroupCallActivity$KpeDb7b4E978_kuhMbqLqjaO_w4
            r0 = r3
            r1 = r40
            r2 = r7
            r15 = r3
            r3 = r14
            r25 = r4
            r4 = r10
            r5 = r44
            r6 = r42
            r30 = r7
            r7 = r25
            r0.<init>(r2, r3, r4, r5, r6, r7)
            r11.setOnClickListener(r15)
            org.telegram.ui.GroupCallActivity$17 r0 = new org.telegram.ui.GroupCallActivity$17
            r42 = r0
            r43 = r40
            r44 = r41
            r45 = r30
            r46 = r14
            r47 = r10
            r42.<init>(r43, r44, r45, r46, r47)
            r8.scheduleTimerContainer = r0
            r1 = 1065353216(0x3var_, float:1.0)
            r0.setWeightSum(r1)
            android.widget.LinearLayout r0 = r8.scheduleTimerContainer
            r1 = 0
            r0.setOrientation(r1)
            android.view.ViewGroup r0 = r8.containerView
            android.widget.LinearLayout r1 = r8.scheduleTimerContainer
            r33 = -1
            r34 = 1132920832(0x43870000, float:270.0)
            r35 = 51
            r36 = 0
            r37 = 1112014848(0x42480000, float:50.0)
            r38 = 0
            r39 = 0
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r33, r34, r35, r36, r37, r38, r39)
            r0.addView(r1, r2)
            long r0 = java.lang.System.currentTimeMillis()
            java.util.Calendar r2 = java.util.Calendar.getInstance()
            r2.setTimeInMillis(r0)
            int r3 = r2.get(r12)
            r4 = 6
            int r5 = r2.get(r4)
            android.widget.LinearLayout r6 = r8.scheduleTimerContainer
            r7 = 1056964608(0x3var_, float:0.5)
            r11 = 270(0x10e, float:3.78E-43)
            r15 = 0
            android.widget.LinearLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r15, (int) r11, (float) r7)
            r12 = r30
            r6.addView(r12, r7)
            r12.setMinValue(r15)
            r6 = 365(0x16d, float:5.11E-43)
            r12.setMaxValue(r6)
            r12.setWrapSelectorWheel(r15)
            org.telegram.ui.-$$Lambda$GroupCallActivity$w-4972JwXi19f4vW5Syloa202-U r6 = new org.telegram.ui.-$$Lambda$GroupCallActivity$w-4972JwXi19f4vW5Syloa202-U
            r6.<init>(r0, r2, r3)
            r12.setFormatter(r6)
            org.telegram.ui.-$$Lambda$GroupCallActivity$omKiIomFeU-G4xXcwrVqEAcgEkw r3 = new org.telegram.ui.-$$Lambda$GroupCallActivity$omKiIomFeU-G4xXcwrVqEAcgEkw
            r3.<init>(r12, r14, r10)
            r12.setOnValueChangedListener(r3)
            r14.setMinValue(r15)
            r6 = 23
            r14.setMaxValue(r6)
            android.widget.LinearLayout r6 = r8.scheduleTimerContainer
            r7 = 1045220557(0x3e4ccccd, float:0.2)
            android.widget.LinearLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r15, (int) r11, (float) r7)
            r6.addView(r14, r7)
            org.telegram.ui.-$$Lambda$GroupCallActivity$fOL22tMu14a6QhojURWV0AtqSaE r6 = org.telegram.ui.$$Lambda$GroupCallActivity$fOL22tMu14a6QhojURWV0AtqSaE.INSTANCE
            r14.setFormatter(r6)
            r14.setOnValueChangedListener(r3)
            r10.setMinValue(r15)
            r6 = 59
            r10.setMaxValue(r6)
            r10.setValue(r15)
            org.telegram.ui.-$$Lambda$GroupCallActivity$guKcDY4FB79Ujfjkfp1068WmrUk r6 = org.telegram.ui.$$Lambda$GroupCallActivity$guKcDY4FB79Ujfjkfp1068WmrUk.INSTANCE
            r10.setFormatter(r6)
            android.widget.LinearLayout r6 = r8.scheduleTimerContainer
            r7 = 1050253722(0x3e99999a, float:0.3)
            android.widget.LinearLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r15, (int) r11, (float) r7)
            r6.addView(r10, r7)
            r10.setOnValueChangedListener(r3)
            r6 = 10800000(0xa4cb80, double:5.335909E-317)
            long r0 = r0 + r6
            r2.setTimeInMillis(r0)
            r0 = 12
            r2.set(r0, r15)
            r1 = 13
            r2.set(r1, r15)
            r1 = 14
            r2.set(r1, r15)
            int r1 = r2.get(r4)
            int r3 = r2.get(r0)
            r6 = 11
            int r2 = r2.get(r6)
            if (r5 == r1) goto L_0x057e
            r1 = 1
            goto L_0x057f
        L_0x057e:
            r1 = 0
        L_0x057f:
            r12.setValue(r1)
            r10.setValue(r3)
            r14.setValue(r2)
            android.widget.TextView r1 = r8.scheduleButtonTextView
            android.widget.TextView r2 = r8.scheduleInfoTextView
            r27 = 604800(0x93a80, double:2.98811E-318)
            r29 = 2
            r25 = r1
            r26 = r2
            r30 = r12
            r31 = r14
            r32 = r10
            org.telegram.ui.Components.AlertsCreator.checkScheduleDate(r25, r26, r27, r29, r30, r31, r32)
            goto L_0x05a2
        L_0x059f:
            r0 = 12
            r4 = 6
        L_0x05a2:
            java.lang.String r1 = "voipgroup_unmuteButton2"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            int r2 = android.graphics.Color.red(r1)
            int r3 = android.graphics.Color.green(r1)
            int r1 = android.graphics.Color.blue(r1)
            android.graphics.Matrix r5 = new android.graphics.Matrix
            r5.<init>()
            r8.radialMatrix = r5
            android.graphics.RadialGradient r5 = new android.graphics.RadialGradient
            r26 = 0
            r27 = 0
            r6 = 1126170624(0x43200000, float:160.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            float r6 = (float) r6
            r7 = 2
            int[] r10 = new int[r7]
            r7 = 50
            int r7 = android.graphics.Color.argb(r7, r2, r3, r1)
            r11 = 0
            r10[r11] = r7
            int r1 = android.graphics.Color.argb(r11, r2, r3, r1)
            r2 = 1
            r10[r2] = r1
            r30 = 0
            android.graphics.Shader$TileMode r31 = android.graphics.Shader.TileMode.CLAMP
            r25 = r5
            r28 = r6
            r29 = r10
            r25.<init>(r26, r27, r28, r29, r30, r31)
            r8.radialGradient = r5
            android.graphics.Paint r1 = new android.graphics.Paint
            r2 = 1
            r1.<init>(r2)
            r8.radialPaint = r1
            android.graphics.RadialGradient r2 = r8.radialGradient
            r1.setShader(r2)
            org.telegram.ui.Components.BlobDrawable r1 = new org.telegram.ui.Components.BlobDrawable
            r2 = 9
            r1.<init>(r2)
            r8.tinyWaveDrawable = r1
            org.telegram.ui.Components.BlobDrawable r2 = new org.telegram.ui.Components.BlobDrawable
            r2.<init>(r0)
            r8.bigWaveDrawable = r2
            r3 = 1115160576(0x42780000, float:62.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r3 = (float) r3
            r1.minRadius = r3
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r24)
            float r3 = (float) r3
            r1.maxRadius = r3
            r1.generateBlob()
            r3 = 1115815936(0x42820000, float:65.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r3 = (float) r3
            r2.minRadius = r3
            r3 = 1117126656(0x42960000, float:75.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r3 = (float) r3
            r2.maxRadius = r3
            r2.generateBlob()
            android.graphics.Paint r1 = r1.paint
            java.lang.String r3 = "voipgroup_unmuteButton"
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            r5 = 38
            int r3 = androidx.core.graphics.ColorUtils.setAlphaComponent(r3, r5)
            r1.setColor(r3)
            android.graphics.Paint r1 = r2.paint
            java.lang.String r2 = "voipgroup_unmuteButton"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r3 = 76
            int r2 = androidx.core.graphics.ColorUtils.setAlphaComponent(r2, r3)
            r1.setColor(r2)
            org.telegram.ui.Components.voip.VoIPToggleButton r1 = new org.telegram.ui.Components.voip.VoIPToggleButton
            r1.<init>(r9)
            r8.soundButton = r1
            r2 = 1
            r1.setCheckable(r2)
            org.telegram.ui.Components.voip.VoIPToggleButton r1 = r8.soundButton
            r1.setTextSize(r0)
            android.widget.FrameLayout r1 = r8.buttonsContainer
            org.telegram.ui.Components.voip.VoIPToggleButton r2 = r8.soundButton
            r3 = 68
            r5 = 1119092736(0x42b40000, float:90.0)
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r3, r5)
            r1.addView(r2, r3)
            org.telegram.ui.Components.voip.VoIPToggleButton r1 = r8.soundButton
            org.telegram.ui.-$$Lambda$GroupCallActivity$GnpTA048eRdLuVIlz7XtQ0TKf2E r2 = new org.telegram.ui.-$$Lambda$GroupCallActivity$GnpTA048eRdLuVIlz7XtQ0TKf2E
            r2.<init>()
            r1.setOnClickListener(r2)
            org.telegram.ui.Components.voip.VoIPToggleButton r1 = new org.telegram.ui.Components.voip.VoIPToggleButton
            r1.<init>(r9)
            r8.leaveButton = r1
            r2 = 0
            r1.setDrawBackground(r2)
            org.telegram.ui.Components.voip.VoIPToggleButton r1 = r8.leaveButton
            r1.setTextSize(r0)
            org.telegram.ui.Components.voip.VoIPToggleButton r0 = r8.leaveButton
            r25 = 2131165319(0x7var_, float:1.7944852E38)
            r26 = -1
            java.lang.String r1 = "voipgroup_leaveButton"
            int r27 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r28 = 1050253722(0x3e99999a, float:0.3)
            r29 = 0
            r1 = 2131628062(0x7f0e101e, float:1.8883406E38)
            java.lang.String r2 = "VoipGroupLeave"
            java.lang.String r30 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r31 = 0
            r32 = 0
            r24 = r0
            r24.setData(r25, r26, r27, r28, r29, r30, r31, r32)
            android.widget.FrameLayout r0 = r8.buttonsContainer
            org.telegram.ui.Components.voip.VoIPToggleButton r1 = r8.leaveButton
            r2 = 68
            r3 = 1117782016(0x42a00000, float:80.0)
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r2, r3)
            r0.addView(r1, r2)
            org.telegram.ui.Components.voip.VoIPToggleButton r0 = r8.leaveButton
            org.telegram.ui.-$$Lambda$GroupCallActivity$06vwSm96g1YTPvoLkLqWvar_VMdY r1 = new org.telegram.ui.-$$Lambda$GroupCallActivity$06vwSm96g1YTPvoLkLqWvar_VMdY
            r1.<init>(r9)
            r0.setOnClickListener(r1)
            org.telegram.ui.GroupCallActivity$18 r0 = new org.telegram.ui.GroupCallActivity$18
            r0.<init>(r9)
            r8.muteButton = r0
            org.telegram.ui.Components.RLottieDrawable r1 = r8.bigMicDrawable
            r0.setAnimation(r1)
            org.telegram.ui.Components.RLottieImageView r0 = r8.muteButton
            android.widget.ImageView$ScaleType r1 = android.widget.ImageView.ScaleType.CENTER
            r0.setScaleType(r1)
            android.widget.FrameLayout r0 = r8.buttonsContainer
            org.telegram.ui.Components.RLottieImageView r1 = r8.muteButton
            r2 = 122(0x7a, float:1.71E-43)
            r3 = 122(0x7a, float:1.71E-43)
            r5 = 49
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r2, r3, r5)
            r0.addView(r1, r2)
            org.telegram.ui.Components.RLottieImageView r0 = r8.muteButton
            org.telegram.ui.GroupCallActivity$19 r1 = new org.telegram.ui.GroupCallActivity$19
            r1.<init>()
            r0.setOnClickListener(r1)
            org.telegram.ui.Components.RadialProgressView r0 = new org.telegram.ui.Components.RadialProgressView
            r0.<init>(r9)
            r8.radialProgressView = r0
            r1 = 1121714176(0x42dCLASSNAME, float:110.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r0.setSize(r1)
            org.telegram.ui.Components.RadialProgressView r0 = r8.radialProgressView
            r1 = 1082130432(0x40800000, float:4.0)
            r0.setStrokeWidth(r1)
            org.telegram.ui.Components.RadialProgressView r0 = r8.radialProgressView
            java.lang.String r1 = "voipgroup_connectingProgress"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.setProgressColor(r1)
            r0 = 0
        L_0x071d:
            r1 = 2
            if (r0 >= r1) goto L_0x07aa
            android.widget.TextView[] r1 = r8.muteLabel
            android.widget.TextView r2 = new android.widget.TextView
            r2.<init>(r9)
            r1[r0] = r2
            android.widget.TextView[] r1 = r8.muteLabel
            r1 = r1[r0]
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r16)
            r1.setTextColor(r2)
            android.widget.TextView[] r1 = r8.muteLabel
            r1 = r1[r0]
            r2 = 1099956224(0x41900000, float:18.0)
            r3 = 1
            r1.setTextSize(r3, r2)
            android.widget.TextView[] r1 = r8.muteLabel
            r1 = r1[r0]
            r1.setGravity(r3)
            android.widget.FrameLayout r1 = r8.buttonsContainer
            android.widget.TextView[] r2 = r8.muteLabel
            r2 = r2[r0]
            r24 = -2
            r25 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r26 = 81
            r27 = 0
            r28 = 0
            r29 = 0
            r30 = 1104150528(0x41d00000, float:26.0)
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r24, r25, r26, r27, r28, r29, r30)
            r1.addView(r2, r3)
            android.widget.TextView[] r1 = r8.muteSubLabel
            android.widget.TextView r2 = new android.widget.TextView
            r2.<init>(r9)
            r1[r0] = r2
            android.widget.TextView[] r1 = r8.muteSubLabel
            r1 = r1[r0]
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r16)
            r1.setTextColor(r2)
            android.widget.TextView[] r1 = r8.muteSubLabel
            r1 = r1[r0]
            r2 = 1094713344(0x41400000, float:12.0)
            r3 = 1
            r1.setTextSize(r3, r2)
            android.widget.TextView[] r1 = r8.muteSubLabel
            r1 = r1[r0]
            r1.setGravity(r3)
            android.widget.FrameLayout r1 = r8.buttonsContainer
            android.widget.TextView[] r2 = r8.muteSubLabel
            r2 = r2[r0]
            r30 = 1092616192(0x41200000, float:10.0)
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r24, r25, r26, r27, r28, r29, r30)
            r1.addView(r2, r3)
            r1 = 1
            if (r0 != r1) goto L_0x07a6
            android.widget.TextView[] r1 = r8.muteLabel
            r1 = r1[r0]
            r2 = 4
            r1.setVisibility(r2)
            android.widget.TextView[] r1 = r8.muteSubLabel
            r1 = r1[r0]
            r1.setVisibility(r2)
        L_0x07a6:
            int r0 = r0 + 1
            goto L_0x071d
        L_0x07aa:
            org.telegram.ui.ActionBar.ActionBar r0 = r8.actionBar
            r1 = 0
            r0.setAlpha(r1)
            org.telegram.ui.ActionBar.ActionBar r0 = r8.actionBar
            android.widget.ImageView r0 = r0.getBackButton()
            r2 = 1063675494(0x3var_, float:0.9)
            r0.setScaleX(r2)
            org.telegram.ui.ActionBar.ActionBar r0 = r8.actionBar
            android.widget.ImageView r0 = r0.getBackButton()
            r0.setScaleY(r2)
            org.telegram.ui.ActionBar.ActionBar r0 = r8.actionBar
            android.widget.ImageView r0 = r0.getBackButton()
            r2 = 1096810496(0x41600000, float:14.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r2 = -r2
            float r2 = (float) r2
            r0.setTranslationX(r2)
            org.telegram.ui.ActionBar.ActionBar r0 = r8.actionBar
            org.telegram.ui.ActionBar.SimpleTextView r0 = r0.getTitleTextView()
            r2 = 1102577664(0x41b80000, float:23.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r2 = (float) r2
            r0.setTranslationY(r2)
            org.telegram.ui.ActionBar.ActionBar r0 = r8.actionBar
            org.telegram.ui.ActionBar.SimpleTextView r0 = r0.getSubtitleTextView()
            r2 = 1101004800(0x41a00000, float:20.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r2 = (float) r2
            r0.setTranslationY(r2)
            org.telegram.ui.Components.AvatarDrawable r0 = new org.telegram.ui.Components.AvatarDrawable
            r0.<init>()
            r8.accountSwitchAvatarDrawable = r0
            r2 = 1094713344(0x41400000, float:12.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r0.setTextSize(r2)
            org.telegram.ui.Components.BackupImageView r0 = new org.telegram.ui.Components.BackupImageView
            r0.<init>(r9)
            r8.accountSwitchImageView = r0
            r2 = 1098907648(0x41800000, float:16.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r0.setRoundRadius(r2)
            org.telegram.ui.Components.BackupImageView r0 = r8.accountSwitchImageView
            org.telegram.ui.-$$Lambda$GroupCallActivity$exjg08dOcJt3PtJv6vRmPJyQeqQ r2 = new org.telegram.ui.-$$Lambda$GroupCallActivity$exjg08dOcJt3PtJv6vRmPJyQeqQ
            r2.<init>()
            r0.setOnClickListener(r2)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = new org.telegram.ui.ActionBar.ActionBarMenuItem
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r16)
            r3 = 0
            r5 = 0
            r0.<init>(r9, r3, r5, r2)
            r8.otherItem = r0
            r0.setLongClickEnabled(r5)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r8.otherItem
            r2 = 2131165476(0x7var_, float:1.794517E38)
            r0.setIcon((int) r2)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r8.otherItem
            r2 = 2131623986(0x7f0e0032, float:1.8875139E38)
            java.lang.String r5 = "AccDescrMoreOptions"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r5, r2)
            r0.setContentDescription(r2)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r8.otherItem
            r2 = 2
            r0.setSubMenuOpenSide(r2)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r8.otherItem
            org.telegram.ui.-$$Lambda$GroupCallActivity$7O8--nuvh6rJZh8OUPMf2K7Q_Ug r2 = new org.telegram.ui.-$$Lambda$GroupCallActivity$7O8--nuvh6rJZh8OUPMf2K7Q_Ug
            r2.<init>()
            r0.setDelegate(r2)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r8.otherItem
            java.lang.String r2 = "voipgroup_actionBarItemsSelector"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r2, r4)
            r0.setBackgroundDrawable(r2)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r8.otherItem
            org.telegram.ui.-$$Lambda$GroupCallActivity$etAMuwsixesxosOr56-3BfEgSvc r2 = new org.telegram.ui.-$$Lambda$GroupCallActivity$etAMuwsixesxosOr56-3BfEgSvc
            r2.<init>()
            r0.setOnClickListener(r2)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r8.otherItem
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r16)
            r5 = 0
            r0.setPopupItemsColor(r2, r5)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r8.otherItem
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r16)
            r6 = 1
            r0.setPopupItemsColor(r2, r6)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = new org.telegram.ui.ActionBar.ActionBarMenuItem
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r16)
            r0.<init>(r9, r3, r5, r2)
            r8.pipItem = r0
            r0.setLongClickEnabled(r5)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r8.pipItem
            r2 = 2131165843(0x7var_, float:1.7945915E38)
            r0.setIcon((int) r2)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r8.pipItem
            r2 = 2131624010(0x7f0e004a, float:1.8875188E38)
            java.lang.String r3 = "AccDescrPipMode"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setContentDescription(r2)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r8.pipItem
            java.lang.String r2 = "voipgroup_actionBarItemsSelector"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r2, r4)
            r0.setBackgroundDrawable(r2)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r8.pipItem
            org.telegram.ui.-$$Lambda$GroupCallActivity$2IB5dVSFe9QEHe9wTbp0tEcYJz0 r2 = new org.telegram.ui.-$$Lambda$GroupCallActivity$2IB5dVSFe9QEHe9wTbp0tEcYJz0
            r2.<init>()
            r0.setOnClickListener(r2)
            org.telegram.ui.GroupCallActivity$20 r0 = new org.telegram.ui.GroupCallActivity$20
            r0.<init>(r9, r9)
            r8.titleTextView = r0
            org.telegram.ui.GroupCallActivity$21 r0 = new org.telegram.ui.GroupCallActivity$21
            r0.<init>(r8, r9)
            r8.actionBarBackground = r0
            r0.setAlpha(r1)
            android.view.ViewGroup r0 = r8.containerView
            android.view.View r2 = r8.actionBarBackground
            r24 = -1
            r25 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r26 = 51
            r27 = 0
            r28 = 0
            r29 = 0
            r30 = 0
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r24, r25, r26, r27, r28, r29, r30)
            r0.addView(r2, r3)
            android.view.ViewGroup r0 = r8.containerView
            org.telegram.ui.Components.AudioPlayerAlert$ClippingTextViewSwitcher r2 = r8.titleTextView
            r24 = -2
            r27 = 1102577664(0x41b80000, float:23.0)
            r29 = 1111490560(0x42400000, float:48.0)
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r24, r25, r26, r27, r28, r29, r30)
            r0.addView(r2, r3)
            android.view.ViewGroup r0 = r8.containerView
            org.telegram.ui.ActionBar.ActionBar r2 = r8.actionBar
            r24 = -1
            r27 = 0
            r29 = 0
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r24, r25, r26, r27, r28, r29, r30)
            r0.addView(r2, r3)
            android.widget.LinearLayout r0 = new android.widget.LinearLayout
            r0.<init>(r9)
            r8.menuItemsContainer = r0
            r2 = 0
            r0.setOrientation(r2)
            org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r8.pipItem
            r3 = 48
            android.widget.LinearLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createLinear(r3, r3)
            r0.addView(r2, r5)
            org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r8.otherItem
            android.widget.LinearLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createLinear(r3, r3)
            r0.addView(r2, r5)
            org.telegram.ui.Components.BackupImageView r2 = r8.accountSwitchImageView
            r24 = 32
            r25 = 32
            r26 = 16
            r27 = 2
            r28 = 0
            r29 = 12
            r30 = 0
            android.widget.LinearLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r24, (int) r25, (int) r26, (int) r27, (int) r28, (int) r29, (int) r30)
            r0.addView(r2, r5)
            android.view.ViewGroup r2 = r8.containerView
            r5 = -2
            r6 = 53
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r5, r3, r6)
            r2.addView(r0, r3)
            android.view.View r0 = new android.view.View
            r0.<init>(r9)
            r8.actionBarShadow = r0
            r0.setAlpha(r1)
            android.view.View r0 = r8.actionBarShadow
            java.lang.String r2 = "dialogShadowLine"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r0.setBackgroundColor(r2)
            android.view.ViewGroup r0 = r8.containerView
            android.view.View r2 = r8.actionBarShadow
            r3 = 1065353216(0x3var_, float:1.0)
            r5 = -1
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r5, r3)
            r0.addView(r2, r3)
            r0 = 0
        L_0x0974:
            r2 = 2
            if (r0 >= r2) goto L_0x09c0
            org.telegram.ui.Components.UndoView[] r2 = r8.undoView
            org.telegram.ui.Components.UndoView r3 = new org.telegram.ui.Components.UndoView
            r3.<init>(r9)
            r2[r0] = r3
            org.telegram.ui.Components.UndoView[] r2 = r8.undoView
            r2 = r2[r0]
            r3 = 1092616192(0x41200000, float:10.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r5 = (float) r5
            r2.setAdditionalTranslationY(r5)
            int r2 = android.os.Build.VERSION.SDK_INT
            r5 = 21
            if (r2 < r5) goto L_0x09a2
            org.telegram.ui.Components.UndoView[] r2 = r8.undoView
            r2 = r2[r0]
            r5 = 1084227584(0x40a00000, float:5.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r5 = (float) r5
            r2.setTranslationZ(r5)
        L_0x09a2:
            android.view.ViewGroup r2 = r8.containerView
            org.telegram.ui.Components.UndoView[] r5 = r8.undoView
            r5 = r5[r0]
            r24 = -1
            r25 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r26 = 83
            r27 = 1090519040(0x41000000, float:8.0)
            r28 = 0
            r29 = 1090519040(0x41000000, float:8.0)
            r30 = 1090519040(0x41000000, float:8.0)
            android.widget.FrameLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createFrame(r24, r25, r26, r27, r28, r29, r30)
            r2.addView(r5, r6)
            int r0 = r0 + 1
            goto L_0x0974
        L_0x09c0:
            org.telegram.ui.Cells.AccountSelectCell r0 = new org.telegram.ui.Cells.AccountSelectCell
            r2 = 1
            r0.<init>(r9, r2)
            r8.accountSelectCell = r0
            r2 = 2131230938(0x7var_da, float:1.8077943E38)
            r3 = 240(0xf0, float:3.36E-43)
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            r0.setTag(r2, r3)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r8.otherItem
            org.telegram.ui.Cells.AccountSelectCell r2 = r8.accountSelectCell
            r3 = -2
            r5 = 1111490560(0x42400000, float:48.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r6 = 8
            r0.addSubItem((int) r6, (android.view.View) r2, (int) r3, (int) r5)
            org.telegram.ui.Cells.AccountSelectCell r0 = r8.accountSelectCell
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r13)
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.createRadSelectorDrawable(r2, r4, r4)
            r0.setBackground(r2)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r8.otherItem
            r2 = 0
            android.view.View r0 = r0.addGap(r2)
            r8.accountGap = r0
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r8.otherItem
            r3 = 2131628025(0x7f0e0ff9, float:1.8883331E38)
            java.lang.String r4 = "VoipGroupAllCanSpeak"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r4 = 1
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r0 = r0.addSubItem((int) r4, (int) r2, (java.lang.CharSequence) r3, (boolean) r4)
            r8.everyoneItem = r0
            r0.updateSelectorBackground(r4, r2)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r8.otherItem
            r3 = 2131628073(0x7f0e1029, float:1.8883428E38)
            java.lang.String r5 = "VoipGroupOnlyAdminsCanSpeak"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r5, r3)
            r5 = 2
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r0 = r0.addSubItem((int) r5, (int) r2, (java.lang.CharSequence) r3, (boolean) r4)
            r8.adminItem = r0
            r0.updateSelectorBackground(r2, r4)
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r0 = r8.everyoneItem
            java.lang.String r2 = "voipgroup_checkMenu"
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r0.setCheckColor(r3)
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r0 = r8.everyoneItem
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r0.setColors(r3, r4)
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r0 = r8.adminItem
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r0.setCheckColor(r3)
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r0 = r8.adminItem
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r0.setColors(r3, r2)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r8.otherItem
            r25 = 6
            r26 = 2131165742(0x7var_e, float:1.794571E38)
            org.telegram.ui.GroupCallActivity$RecordCallDrawable r2 = r8.recordCallDrawable
            r3 = 2131628042(0x7f0e100a, float:1.8883365E38)
            java.lang.String r4 = "VoipGroupEditTitle"
            java.lang.String r28 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r29 = 1
            r30 = 0
            r24 = r0
            r27 = r2
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r0 = r24.addSubItem(r25, r26, r27, r28, r29, r30)
            r8.editTitleItem = r0
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r8.otherItem
            r25 = 7
            r26 = 2131165784(0x7var_, float:1.7945795E38)
            org.telegram.ui.GroupCallActivity$RecordCallDrawable r2 = r8.recordCallDrawable
            r3 = 2131628041(0x7f0e1009, float:1.8883363E38)
            java.lang.String r4 = "VoipGroupEditPermissions"
            java.lang.String r28 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r29 = 0
            r24 = r0
            r27 = r2
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r0 = r24.addSubItem(r25, r26, r27, r28, r29, r30)
            r8.permissionItem = r0
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r8.otherItem
            r2 = 3
            r3 = 2131165759(0x7var_f, float:1.7945744E38)
            r4 = 2131628092(0x7f0e103c, float:1.8883467E38)
            java.lang.String r5 = "VoipGroupShareInviteLink"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r0 = r0.addSubItem(r2, r3, r4)
            r8.inviteItem = r0
            org.telegram.ui.GroupCallActivity$RecordCallDrawable r0 = new org.telegram.ui.GroupCallActivity$RecordCallDrawable
            r0.<init>()
            r8.recordCallDrawable = r0
            org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r8.otherItem
            r25 = 5
            r26 = 0
            r3 = 2131628080(0x7f0e1030, float:1.8883443E38)
            java.lang.String r4 = "VoipGroupRecordCall"
            java.lang.String r28 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r29 = 1
            r24 = r2
            r27 = r0
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r0 = r24.addSubItem(r25, r26, r27, r28, r29, r30)
            r8.recordItem = r0
            org.telegram.ui.GroupCallActivity$RecordCallDrawable r2 = r8.recordCallDrawable
            android.widget.ImageView r0 = r0.getImageView()
            r2.setParentView(r0)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r8.otherItem
            r2 = 4
            r3 = 2131165743(0x7var_f, float:1.7945712E38)
            r4 = 2131628046(0x7f0e100e, float:1.8883374E38)
            java.lang.String r5 = "VoipGroupEndChat"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r0 = r0.addSubItem(r2, r3, r4)
            r8.leaveItem = r0
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r8.otherItem
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r13)
            r0.setPopupItemsSelectorColor(r2)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r8.otherItem
            org.telegram.ui.ActionBar.ActionBarPopupWindow$ActionBarPopupWindowLayout r0 = r0.getPopupLayout()
            r2 = 1
            r0.setFitItems(r2)
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r0 = r8.leaveItem
            java.lang.String r2 = "voipgroup_leaveCallMenu"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            java.lang.String r3 = "voipgroup_leaveCallMenu"
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            r0.setColors(r2, r3)
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r0 = r8.inviteItem
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r16)
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r16)
            r0.setColors(r2, r3)
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r0 = r8.editTitleItem
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r16)
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r16)
            r0.setColors(r2, r3)
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r0 = r8.permissionItem
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r16)
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r16)
            r0.setColors(r2, r3)
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r0 = r8.recordItem
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r16)
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r16)
            r0.setColors(r2, r3)
            org.telegram.messenger.ChatObject$Call r0 = r8.call
            if (r0 == 0) goto L_0x0b47
            r40.initCreatedGroupCall()
        L_0x0b47:
            org.telegram.ui.GroupCallActivity$ListAdapter r0 = r8.listAdapter
            r0.notifyDataSetChanged()
            org.telegram.ui.GroupCallActivity$ListAdapter r0 = r8.listAdapter
            int r0 = r0.getItemCount()
            r8.oldCount = r0
            r40.updateItems()
            r0 = 0
            r8.updateSpeakerPhoneIcon(r0)
            r8.updateState(r0, r0)
            r8.updateScheduleUI(r0)
            r8.setColorProgress(r1)
            android.graphics.Paint r1 = r8.leaveBackgroundPaint
            java.lang.String r2 = "voipgroup_leaveButton"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r1.setColor(r2)
            r8.updateTitle(r0)
            org.telegram.ui.ActionBar.ActionBar r0 = r8.actionBar
            org.telegram.ui.ActionBar.SimpleTextView r0 = r0.getTitleTextView()
            org.telegram.ui.-$$Lambda$GroupCallActivity$qBQu7ty2cMk0JR3EAaOnlQdKZ8w r1 = new org.telegram.ui.-$$Lambda$GroupCallActivity$qBQu7ty2cMk0JR3EAaOnlQdKZ8w
            r1.<init>()
            r0.setOnClickListener(r1)
            org.telegram.ui.AvatarPreviewPagerIndicator r0 = new org.telegram.ui.AvatarPreviewPagerIndicator
            r0.<init>(r9)
            r8.avatarPagerIndicator = r0
            org.telegram.ui.GroupCallActivity$22 r1 = new org.telegram.ui.GroupCallActivity$22
            org.telegram.ui.ActionBar.ActionBar r2 = r8.actionBar
            org.telegram.ui.Components.RecyclerListView r3 = r8.listView
            r42 = r1
            r43 = r40
            r44 = r41
            r45 = r2
            r46 = r3
            r47 = r0
            r42.<init>(r44, r45, r46, r47)
            r8.avatarsViewPager = r1
            r2 = 1
            r1.setInvalidateWithParent(r2)
            r0.setProfileGalleryView(r1)
            org.telegram.ui.GroupCallActivity$23 r2 = new org.telegram.ui.GroupCallActivity$23
            r2.<init>(r9)
            r8.avatarPreviewContainer = r2
            r3 = 8
            r2.setVisibility(r3)
            r3 = 0
            r1.setVisibility(r3)
            org.telegram.ui.GroupCallActivity$24 r3 = new org.telegram.ui.GroupCallActivity$24
            r3.<init>()
            r1.addOnPageChangeListener(r3)
            org.telegram.ui.GroupCallActivity$25 r3 = new org.telegram.ui.GroupCallActivity$25
            r3.<init>(r9)
            r8.blurredView = r3
            android.view.ViewGroup r4 = r8.containerView
            r4.addView(r3)
            r3 = -1082130432(0xffffffffbvar_, float:-1.0)
            r4 = -1
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r4, r3)
            r2.addView(r1, r3)
            r3 = -1
            r4 = -1082130432(0xffffffffbvar_, float:-1.0)
            r5 = 0
            r6 = 0
            r7 = 0
            r9 = 0
            r10 = 0
            r41 = r3
            r42 = r4
            r43 = r5
            r44 = r6
            r45 = r7
            r46 = r9
            r47 = r10
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r41, r42, r43, r44, r45, r46, r47)
            r2.addView(r0, r3)
            android.view.ViewGroup r0 = r8.containerView
            r3 = -1
            r6 = 1096810496(0x41600000, float:14.0)
            r7 = 1096810496(0x41600000, float:14.0)
            r9 = 1096810496(0x41600000, float:14.0)
            r10 = 1096810496(0x41600000, float:14.0)
            r41 = r3
            r44 = r6
            r45 = r7
            r46 = r9
            r47 = r10
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r41, r42, r43, r44, r45, r46, r47)
            r0.addView(r2, r3)
            int r0 = android.os.Build.VERSION.SDK_INT
            r2 = 21
            if (r0 < r2) goto L_0x0c1e
            android.view.Window r0 = r40.getWindow()
            android.view.View r0 = r0.getDecorView()
            android.view.ViewGroup r0 = (android.view.ViewGroup) r0
            goto L_0x0CLASSNAME
        L_0x0c1e:
            android.view.ViewGroup r0 = r8.containerView
        L_0x0CLASSNAME:
            org.telegram.ui.GroupCallActivity$26 r2 = new org.telegram.ui.GroupCallActivity$26
            r2.<init>(r0)
            r8.pinchToZoomHelper = r2
            org.telegram.ui.GroupCallActivity$27 r0 = new org.telegram.ui.GroupCallActivity$27
            r0.<init>()
            r2.setCallback(r0)
            org.telegram.ui.PinchToZoomHelper r0 = r8.pinchToZoomHelper
            r1.setPinchToZoomHelper(r0)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.GroupCallActivity.<init>(android.content.Context, org.telegram.messenger.AccountInstance, org.telegram.messenger.ChatObject$Call, org.telegram.tgnet.TLRPC$Chat, org.telegram.tgnet.TLRPC$InputPeer, boolean, java.lang.String):void");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$9 */
    public /* synthetic */ void lambda$new$9$GroupCallActivity(DialogInterface dialogInterface) {
        BaseFragment baseFragment = this.parentActivity.getActionBarLayout().fragmentsStack.get(this.parentActivity.getActionBarLayout().fragmentsStack.size() - 1);
        if (this.anyEnterEventSent && (baseFragment instanceof ChatActivity)) {
            ((ChatActivity) baseFragment).onEditTextDialogClose(true);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$10 */
    public /* synthetic */ void lambda$new$10$GroupCallActivity(int[] iArr, float[] fArr, boolean[] zArr) {
        RecyclerView.ViewHolder findViewHolderForAdapterPosition;
        for (int i = 0; i < iArr.length; i++) {
            TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant = this.call.participantsBySources.get(iArr[i]);
            if (tLRPC$TL_groupCallParticipant != null) {
                int indexOf = (this.delayedGroupCallUpdated ? this.oldParticipants : this.call.sortedParticipants).indexOf(tLRPC$TL_groupCallParticipant);
                if (indexOf >= 0 && (findViewHolderForAdapterPosition = this.listView.findViewHolderForAdapterPosition(indexOf + this.listAdapter.usersStartRow)) != null) {
                    View view = findViewHolderForAdapterPosition.itemView;
                    if (view instanceof GroupCallUserCell) {
                        ((GroupCallUserCell) view).setAmplitude((double) (fArr[i] * 15.0f));
                        if (findViewHolderForAdapterPosition.itemView == this.scrimView) {
                            this.containerView.invalidate();
                        }
                    }
                }
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$12 */
    public /* synthetic */ void lambda$new$12$GroupCallActivity(View view, int i, float f, float f2) {
        if (view instanceof GroupCallUserCell) {
            showMenuForCell((GroupCallUserCell) view);
        } else if (view instanceof GroupCallInvitedCell) {
            GroupCallInvitedCell groupCallInvitedCell = (GroupCallInvitedCell) view;
            if (groupCallInvitedCell.getUser() != null) {
                Bundle bundle = new Bundle();
                bundle.putInt("user_id", groupCallInvitedCell.getUser().id);
                if (groupCallInvitedCell.hasAvatarSet()) {
                    bundle.putBoolean("expandPhoto", true);
                }
                this.parentActivity.lambda$runLinkRequest$41(new ProfileActivity(bundle));
                dismiss();
            }
        } else if (i == this.listAdapter.addMemberRow) {
            if (ChatObject.isChannel(this.currentChat)) {
                TLRPC$Chat tLRPC$Chat = this.currentChat;
                if (!tLRPC$Chat.megagroup && !TextUtils.isEmpty(tLRPC$Chat.username)) {
                    getLink(false);
                    return;
                }
            }
            TLRPC$ChatFull chatFull = this.accountInstance.getMessagesController().getChatFull(this.currentChat.id);
            if (chatFull != null) {
                this.enterEventSent = false;
                Context context = getContext();
                int currentAccount = this.accountInstance.getCurrentAccount();
                TLRPC$Chat tLRPC$Chat2 = this.currentChat;
                ChatObject.Call call2 = this.call;
                GroupVoipInviteAlert groupVoipInviteAlert2 = new GroupVoipInviteAlert(context, currentAccount, tLRPC$Chat2, chatFull, call2.participants, call2.invitedUsersMap);
                this.groupVoipInviteAlert = groupVoipInviteAlert2;
                groupVoipInviteAlert2.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    public final void onDismiss(DialogInterface dialogInterface) {
                        GroupCallActivity.this.lambda$null$11$GroupCallActivity(dialogInterface);
                    }
                });
                this.groupVoipInviteAlert.setDelegate(new GroupVoipInviteAlert.GroupVoipInviteAlertDelegate() {
                    public void copyInviteLink() {
                        GroupCallActivity.this.getLink(true);
                    }

                    public void inviteUser(int i) {
                        GroupCallActivity.this.inviteUserToCall(i, true);
                    }

                    public void needOpenSearch(MotionEvent motionEvent, EditTextBoldCursor editTextBoldCursor) {
                        if (GroupCallActivity.this.enterEventSent) {
                            return;
                        }
                        if (motionEvent.getX() <= ((float) editTextBoldCursor.getLeft()) || motionEvent.getX() >= ((float) editTextBoldCursor.getRight()) || motionEvent.getY() <= ((float) editTextBoldCursor.getTop()) || motionEvent.getY() >= ((float) editTextBoldCursor.getBottom())) {
                            GroupCallActivity groupCallActivity = GroupCallActivity.this;
                            groupCallActivity.makeFocusable(groupCallActivity.groupVoipInviteAlert, (AlertDialog) null, editTextBoldCursor, false);
                            return;
                        }
                        GroupCallActivity groupCallActivity2 = GroupCallActivity.this;
                        groupCallActivity2.makeFocusable(groupCallActivity2.groupVoipInviteAlert, (AlertDialog) null, editTextBoldCursor, true);
                    }
                });
                this.groupVoipInviteAlert.show();
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$11 */
    public /* synthetic */ void lambda$null$11$GroupCallActivity(DialogInterface dialogInterface) {
        this.groupVoipInviteAlert = null;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$13 */
    public /* synthetic */ boolean lambda$new$13$GroupCallActivity(View view, int i) {
        if (!(view instanceof GroupCallUserCell)) {
            return false;
        }
        updateItems();
        return ((GroupCallUserCell) view).clickMuteButton();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$18 */
    public /* synthetic */ void lambda$new$18$GroupCallActivity(NumberPicker numberPicker, NumberPicker numberPicker2, NumberPicker numberPicker3, TLRPC$Chat tLRPC$Chat, AccountInstance accountInstance2, TLRPC$InputPeer tLRPC$InputPeer, View view) {
        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
        this.scheduleAnimator = ofFloat;
        ofFloat.setDuration(600);
        this.scheduleAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                GroupCallActivity.this.lambda$null$14$GroupCallActivity(valueAnimator);
            }
        });
        this.scheduleAnimator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                ValueAnimator unused = GroupCallActivity.this.scheduleAnimator = null;
            }
        });
        this.scheduleAnimator.start();
        this.titleTextView.setText(LocaleController.getString("VoipGroupVoiceChat", NUM), true);
        Calendar instance = Calendar.getInstance();
        boolean checkScheduleDate = AlertsCreator.checkScheduleDate((TextView) null, (TextView) null, 604800, 3, numberPicker, numberPicker2, numberPicker3);
        instance.setTimeInMillis(System.currentTimeMillis() + (((long) numberPicker.getValue()) * 24 * 3600 * 1000));
        instance.set(11, numberPicker2.getValue());
        instance.set(12, numberPicker3.getValue());
        if (checkScheduleDate) {
            instance.set(13, 0);
        }
        this.scheduleStartAt = (int) (instance.getTimeInMillis() / 1000);
        updateScheduleUI(false);
        TLRPC$TL_phone_createGroupCall tLRPC$TL_phone_createGroupCall = new TLRPC$TL_phone_createGroupCall();
        tLRPC$TL_phone_createGroupCall.peer = MessagesController.getInputPeer(tLRPC$Chat);
        tLRPC$TL_phone_createGroupCall.random_id = Utilities.random.nextInt();
        tLRPC$TL_phone_createGroupCall.schedule_date = this.scheduleStartAt;
        tLRPC$TL_phone_createGroupCall.flags |= 2;
        TLRPC$Chat tLRPC$Chat2 = tLRPC$Chat;
        accountInstance2.getConnectionsManager().sendRequest(tLRPC$TL_phone_createGroupCall, new RequestDelegate(tLRPC$Chat, tLRPC$InputPeer) {
            public final /* synthetic */ TLRPC$Chat f$1;
            public final /* synthetic */ TLRPC$InputPeer f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                GroupCallActivity.this.lambda$null$17$GroupCallActivity(this.f$1, this.f$2, tLObject, tLRPC$TL_error);
            }
        }, 2);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$14 */
    public /* synthetic */ void lambda$null$14$GroupCallActivity(ValueAnimator valueAnimator) {
        this.switchToButtonProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        updateScheduleUI(true);
        this.buttonsContainer.invalidate();
        this.listView.invalidate();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$17 */
    public /* synthetic */ void lambda$null$17$GroupCallActivity(TLRPC$Chat tLRPC$Chat, TLRPC$InputPeer tLRPC$InputPeer, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLObject != null) {
            TLRPC$Updates tLRPC$Updates = (TLRPC$Updates) tLObject;
            int i = 0;
            while (true) {
                if (i >= tLRPC$Updates.updates.size()) {
                    break;
                }
                TLRPC$Update tLRPC$Update = tLRPC$Updates.updates.get(i);
                if (tLRPC$Update instanceof TLRPC$TL_updateGroupCall) {
                    AndroidUtilities.runOnUIThread(new Runnable(tLRPC$Chat, tLRPC$InputPeer, (TLRPC$TL_updateGroupCall) tLRPC$Update) {
                        public final /* synthetic */ TLRPC$Chat f$1;
                        public final /* synthetic */ TLRPC$InputPeer f$2;
                        public final /* synthetic */ TLRPC$TL_updateGroupCall f$3;

                        {
                            this.f$1 = r2;
                            this.f$2 = r3;
                            this.f$3 = r4;
                        }

                        public final void run() {
                            GroupCallActivity.this.lambda$null$15$GroupCallActivity(this.f$1, this.f$2, this.f$3);
                        }
                    });
                    break;
                }
                i++;
            }
            this.accountInstance.getMessagesController().processUpdates(tLRPC$Updates, false);
            return;
        }
        AndroidUtilities.runOnUIThread(new Runnable(tLRPC$TL_error) {
            public final /* synthetic */ TLRPC$TL_error f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                GroupCallActivity.this.lambda$null$16$GroupCallActivity(this.f$1);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$15 */
    public /* synthetic */ void lambda$null$15$GroupCallActivity(TLRPC$Chat tLRPC$Chat, TLRPC$InputPeer tLRPC$InputPeer, TLRPC$TL_updateGroupCall tLRPC$TL_updateGroupCall) {
        ChatObject.Call call2 = new ChatObject.Call();
        this.call = call2;
        call2.call = new TLRPC$TL_groupCall();
        ChatObject.Call call3 = this.call;
        TLRPC$GroupCall tLRPC$GroupCall = call3.call;
        tLRPC$GroupCall.participants_count = 0;
        tLRPC$GroupCall.version = 1;
        tLRPC$GroupCall.can_change_join_muted = true;
        call3.chatId = tLRPC$Chat.id;
        tLRPC$GroupCall.schedule_date = this.scheduleStartAt;
        tLRPC$GroupCall.flags |= 128;
        call3.currentAccount = this.accountInstance;
        call3.setSelfPeer(tLRPC$InputPeer);
        TLRPC$GroupCall tLRPC$GroupCall2 = this.call.call;
        TLRPC$GroupCall tLRPC$GroupCall3 = tLRPC$TL_updateGroupCall.call;
        tLRPC$GroupCall2.access_hash = tLRPC$GroupCall3.access_hash;
        tLRPC$GroupCall2.id = tLRPC$GroupCall3.id;
        MessagesController messagesController = this.accountInstance.getMessagesController();
        ChatObject.Call call4 = this.call;
        messagesController.putGroupCall(call4.chatId, call4);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$16 */
    public /* synthetic */ void lambda$null$16$GroupCallActivity(TLRPC$TL_error tLRPC$TL_error) {
        this.accountInstance.getNotificationCenter().postNotificationName(NotificationCenter.needShowAlert, 6, tLRPC$TL_error.text);
        dismiss();
    }

    static /* synthetic */ String lambda$new$19(long j, Calendar calendar, int i, int i2) {
        if (i2 == 0) {
            return LocaleController.getString("MessageScheduleToday", NUM);
        }
        long j2 = j + (((long) i2) * 86400000);
        calendar.setTimeInMillis(j2);
        if (calendar.get(1) == i) {
            return LocaleController.getInstance().formatterScheduleDay.format(j2);
        }
        return LocaleController.getInstance().formatterScheduleYear.format(j2);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$20 */
    public /* synthetic */ void lambda$new$20$GroupCallActivity(NumberPicker numberPicker, NumberPicker numberPicker2, NumberPicker numberPicker3, NumberPicker numberPicker4, int i, int i2) {
        try {
            this.container.performHapticFeedback(3, 2);
        } catch (Exception unused) {
        }
        AlertsCreator.checkScheduleDate(this.scheduleButtonTextView, this.scheduleInfoTextView, 604800, 2, numberPicker, numberPicker2, numberPicker3);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$23 */
    public /* synthetic */ void lambda$new$23$GroupCallActivity(View view) {
        ChatObject.Call call2 = this.call;
        if (call2 == null || call2.isScheduled()) {
            getLink(false);
        } else if (VoIPService.getSharedInstance() != null) {
            VoIPService.getSharedInstance().toggleSpeakerphoneOrShowRouteSheet(getContext(), false);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$24 */
    public /* synthetic */ void lambda$new$24$GroupCallActivity(Context context, View view) {
        ChatObject.Call call2 = this.call;
        if (call2 == null || call2.isScheduled()) {
            dismiss();
            return;
        }
        updateItems();
        onLeaveClick(context, new Runnable() {
            public final void run() {
                GroupCallActivity.this.dismiss();
            }
        }, false);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$27 */
    public /* synthetic */ void lambda$new$27$GroupCallActivity(View view) {
        JoinCallAlert.open(getContext(), -this.currentChat.id, this.accountInstance, (BaseFragment) null, 2, this.selfPeer, new JoinCallAlert.JoinCallAlertDelegate() {
            public final void didSelectChat(TLRPC$InputPeer tLRPC$InputPeer, boolean z, boolean z2) {
                GroupCallActivity.this.lambda$null$26$GroupCallActivity(tLRPC$InputPeer, z, z2);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$26 */
    public /* synthetic */ void lambda$null$26$GroupCallActivity(TLRPC$InputPeer tLRPC$InputPeer, boolean z, boolean z2) {
        TLObject tLObject;
        if (this.call != null) {
            boolean z3 = tLRPC$InputPeer instanceof TLRPC$TL_inputPeerUser;
            if (z3) {
                tLObject = this.accountInstance.getMessagesController().getUser(Integer.valueOf(tLRPC$InputPeer.user_id));
            } else if (tLRPC$InputPeer instanceof TLRPC$TL_inputPeerChat) {
                tLObject = this.accountInstance.getMessagesController().getChat(Integer.valueOf(tLRPC$InputPeer.chat_id));
            } else {
                tLObject = this.accountInstance.getMessagesController().getChat(Integer.valueOf(tLRPC$InputPeer.channel_id));
            }
            if (this.call.isScheduled()) {
                getUndoView().showWithAction(0, 37, (Object) tLObject);
                if (tLRPC$InputPeer instanceof TLRPC$TL_inputPeerChannel) {
                    TLRPC$TL_peerChannel tLRPC$TL_peerChannel = new TLRPC$TL_peerChannel();
                    this.selfPeer = tLRPC$TL_peerChannel;
                    tLRPC$TL_peerChannel.channel_id = tLRPC$InputPeer.channel_id;
                } else if (z3) {
                    TLRPC$TL_peerUser tLRPC$TL_peerUser = new TLRPC$TL_peerUser();
                    this.selfPeer = tLRPC$TL_peerUser;
                    tLRPC$TL_peerUser.user_id = tLRPC$InputPeer.user_id;
                } else if (tLRPC$InputPeer instanceof TLRPC$TL_inputPeerChat) {
                    TLRPC$TL_peerChat tLRPC$TL_peerChat = new TLRPC$TL_peerChat();
                    this.selfPeer = tLRPC$TL_peerChat;
                    tLRPC$TL_peerChat.chat_id = tLRPC$InputPeer.chat_id;
                }
                this.schedulePeer = tLRPC$InputPeer;
                TLRPC$ChatFull chatFull = this.accountInstance.getMessagesController().getChatFull(this.currentChat.id);
                if (chatFull != null) {
                    chatFull.groupcall_default_join_as = this.selfPeer;
                    if (chatFull instanceof TLRPC$TL_chatFull) {
                        chatFull.flags |= 32768;
                    } else {
                        chatFull.flags |= 67108864;
                    }
                }
                TLRPC$TL_phone_saveDefaultGroupCallJoinAs tLRPC$TL_phone_saveDefaultGroupCallJoinAs = new TLRPC$TL_phone_saveDefaultGroupCallJoinAs();
                tLRPC$TL_phone_saveDefaultGroupCallJoinAs.peer = MessagesController.getInputPeer(this.currentChat);
                tLRPC$TL_phone_saveDefaultGroupCallJoinAs.join_as = tLRPC$InputPeer;
                this.accountInstance.getConnectionsManager().sendRequest(tLRPC$TL_phone_saveDefaultGroupCallJoinAs, $$Lambda$GroupCallActivity$Fi7AFfTntMT0aVdtjy7srIVYT4.INSTANCE);
                updateItems();
            } else if (VoIPService.getSharedInstance() != null && z) {
                TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant = this.call.participants.get(MessageObject.getPeerId(this.selfPeer));
                VoIPService.getSharedInstance().setGroupCallPeer(tLRPC$InputPeer);
                this.userSwitchObject = tLObject;
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$28 */
    public /* synthetic */ void lambda$new$28$GroupCallActivity(int i) {
        this.actionBar.getActionBarMenuOnItemClick().onItemClick(i);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$29 */
    public /* synthetic */ void lambda$new$29$GroupCallActivity(View view) {
        ChatObject.Call call2 = this.call;
        if (call2 != null) {
            if (call2.call.join_muted) {
                this.everyoneItem.setColors(Theme.getColor("voipgroup_actionBarItems"), Theme.getColor("voipgroup_actionBarItems"));
                this.everyoneItem.setChecked(false);
                this.adminItem.setColors(Theme.getColor("voipgroup_checkMenu"), Theme.getColor("voipgroup_checkMenu"));
                this.adminItem.setChecked(true);
            } else {
                this.everyoneItem.setColors(Theme.getColor("voipgroup_checkMenu"), Theme.getColor("voipgroup_checkMenu"));
                this.everyoneItem.setChecked(true);
                this.adminItem.setColors(Theme.getColor("voipgroup_actionBarItems"), Theme.getColor("voipgroup_actionBarItems"));
                this.adminItem.setChecked(false);
            }
            this.changingPermissions = false;
            this.otherItem.hideSubItem(1);
            this.otherItem.hideSubItem(2);
            updateItems();
            this.otherItem.toggleSubMenu();
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$30 */
    public /* synthetic */ void lambda$new$30$GroupCallActivity(View view) {
        if (Build.VERSION.SDK_INT < 23 || Settings.canDrawOverlays(this.parentActivity)) {
            GroupCallPip.clearForce();
            dismiss();
            return;
        }
        AlertsCreator.createDrawOverlayGroupCallPermissionDialog(getContext()).show();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$31 */
    public /* synthetic */ void lambda$new$31$GroupCallActivity(View view) {
        ChatObject.Call call2 = this.call;
        if (call2 != null && call2.recording) {
            showRecordHint(this.actionBar.getTitleTextView());
        }
    }

    /* access modifiers changed from: private */
    public void checkContentOverlayed() {
        boolean z = !this.avatarPriviewTransitionInProgress && this.blurredView.getVisibility() == 0 && this.blurredView.getAlpha() == 1.0f;
        if (this.contentFullyOverlayed != z) {
            this.contentFullyOverlayed = z;
            this.buttonsContainer.invalidate();
            this.containerView.invalidate();
            this.listView.invalidate();
        }
    }

    private void updateScheduleUI(boolean z) {
        float f;
        float f2;
        LinearLayout linearLayout = this.scheduleTimerContainer;
        float f3 = 1.0f;
        if ((linearLayout == null || this.call != null) && this.scheduleAnimator == null) {
            this.scheduleButtonsScale = 1.0f;
            this.switchToButtonInt2 = 1.0f;
            this.switchToButtonProgress = 1.0f;
            if (linearLayout == null) {
                return;
            }
        }
        int i = 4;
        if (!z) {
            AndroidUtilities.cancelRunOnUIThread(this.updateSchedeulRunnable);
            this.updateSchedeulRunnable.run();
            ChatObject.Call call2 = this.call;
            if (call2 == null || call2.isScheduled()) {
                this.listView.setVisibility(4);
            } else {
                this.listView.setVisibility(0);
            }
            this.leaveItem.setText(LocaleController.getString("VoipGroupCancelChat", NUM));
        }
        float f4 = this.switchToButtonProgress;
        if (f4 > 0.6f) {
            f2 = 1.05f - (CubicBezierInterpolator.DEFAULT.getInterpolation((f4 - 0.6f) / 0.4f) * 0.05f);
            this.scheduleButtonsScale = f2;
            this.switchToButtonInt2 = 1.0f;
            f = 1.0f;
        } else {
            CubicBezierInterpolator cubicBezierInterpolator = CubicBezierInterpolator.DEFAULT;
            this.scheduleButtonsScale = (cubicBezierInterpolator.getInterpolation(f4 / 0.6f) * 0.05f) + 1.0f;
            this.switchToButtonInt2 = cubicBezierInterpolator.getInterpolation(this.switchToButtonProgress / 0.6f);
            f2 = 1.05f * cubicBezierInterpolator.getInterpolation(this.switchToButtonProgress / 0.6f);
            f = this.switchToButtonProgress / 0.6f;
        }
        float f5 = 1.0f - f;
        this.leaveButton.setAlpha(f);
        VoIPToggleButton voIPToggleButton = this.soundButton;
        if (!voIPToggleButton.isEnabled()) {
            f3 = 0.5f;
        }
        voIPToggleButton.setAlpha(f3 * f);
        this.muteButton.setAlpha(f);
        this.scheduleTimerContainer.setAlpha(f5);
        this.scheduleStartInTextView.setAlpha(f);
        this.scheduleStartAtTextView.setAlpha(f);
        this.scheduleTimeTextView.setAlpha(f);
        this.muteLabel[0].setAlpha(f);
        this.muteSubLabel[0].setAlpha(f);
        this.scheduleTimeTextView.setScaleX(f2);
        this.scheduleTimeTextView.setScaleY(f2);
        this.leaveButton.setScaleX(f2);
        this.leaveButton.setScaleY(f2);
        this.soundButton.setScaleX(f2);
        this.soundButton.setScaleY(f2);
        this.muteButton.setScaleX(f2);
        this.muteButton.setScaleY(f2);
        this.scheduleButtonTextView.setScaleX(f5);
        this.scheduleButtonTextView.setScaleY(f5);
        this.scheduleButtonTextView.setAlpha(f5);
        this.scheduleInfoTextView.setAlpha(f5);
        this.otherItem.setAlpha(f);
        if (f5 != 0.0f) {
            i = 0;
        }
        if (i != this.scheduleTimerContainer.getVisibility()) {
            this.scheduleTimerContainer.setVisibility(i);
            this.scheduleButtonTextView.setVisibility(i);
        }
    }

    private void initCreatedGroupCall() {
        VoIPService sharedInstance;
        if (!this.callInitied && (sharedInstance = VoIPService.getSharedInstance()) != null) {
            this.callInitied = true;
            this.oldParticipants.addAll(this.call.sortedParticipants);
            this.oldInvited.addAll(this.call.invitedUsers);
            this.currentCallState = sharedInstance.getCallState();
            if (this.call == null) {
                this.call = sharedInstance.groupCall;
            }
            this.actionBar.setSubtitle(LocaleController.formatPluralString("Participants", this.call.call.participants_count + (this.listAdapter.addSelfToCounter() ? 1 : 0)));
            this.actionBar.setTitleRightMargin(AndroidUtilities.dp(48.0f) * 2);
            this.call.saveActiveDates();
            VoIPService.getSharedInstance().registerStateListener(this);
            SimpleTextView simpleTextView = this.scheduleTimeTextView;
            if (simpleTextView != null && simpleTextView.getVisibility() == 0) {
                this.leaveButton.setData(NUM, -1, Theme.getColor("voipgroup_leaveButton"), 0.3f, false, LocaleController.getString("VoipGroupLeave", NUM), false, true);
                updateSpeakerPhoneIcon(true);
                this.leaveItem.setText(LocaleController.getString("VoipGroupEndChat", NUM));
                this.listView.setVisibility(0);
                this.pipItem.setVisibility(0);
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.listView, View.ALPHA, new float[]{0.0f, 1.0f}), ObjectAnimator.ofFloat(this.listView, View.TRANSLATION_Y, new float[]{(float) AndroidUtilities.dp(200.0f), 0.0f}), ObjectAnimator.ofFloat(this.scheduleTimeTextView, View.SCALE_X, new float[]{0.0f}), ObjectAnimator.ofFloat(this.scheduleTimeTextView, View.SCALE_Y, new float[]{0.0f}), ObjectAnimator.ofFloat(this.scheduleTimeTextView, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.scheduleStartInTextView, View.SCALE_X, new float[]{0.0f}), ObjectAnimator.ofFloat(this.scheduleStartInTextView, View.SCALE_Y, new float[]{0.0f}), ObjectAnimator.ofFloat(this.scheduleStartInTextView, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.scheduleStartAtTextView, View.SCALE_X, new float[]{0.0f}), ObjectAnimator.ofFloat(this.scheduleStartAtTextView, View.SCALE_Y, new float[]{0.0f}), ObjectAnimator.ofFloat(this.scheduleStartAtTextView, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.pipItem, View.SCALE_X, new float[]{0.0f, 1.0f}), ObjectAnimator.ofFloat(this.pipItem, View.SCALE_Y, new float[]{0.0f, 1.0f}), ObjectAnimator.ofFloat(this.pipItem, View.ALPHA, new float[]{0.0f, 1.0f})});
                animatorSet.setInterpolator(CubicBezierInterpolator.EASE_OUT);
                animatorSet.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        GroupCallActivity.this.scheduleTimeTextView.setVisibility(4);
                        GroupCallActivity.this.scheduleStartAtTextView.setVisibility(4);
                        GroupCallActivity.this.scheduleStartInTextView.setVisibility(4);
                    }
                });
                animatorSet.setDuration(300);
                animatorSet.start();
            }
        }
    }

    public void dismissInternal() {
        super.dismissInternal();
        if (VoIPService.getSharedInstance() != null) {
            VoIPService.getSharedInstance().unregisterStateListener(this);
        }
        if (groupCallInstance == this) {
            groupCallInstance = null;
        }
        groupCallUiVisible = false;
        VoIPService.audioLevelsCallback = null;
        GroupCallPip.updateVisibility(getContext());
    }

    private void setAmplitude(double d) {
        float min = (float) (Math.min(8500.0d, d) / 8500.0d);
        this.animateToAmplitude = min;
        this.animateAmplitudeDiff = (min - this.amplitude) / ((BlobDrawable.AMPLITUDE_SPEED * 500.0f) + 100.0f);
    }

    public void onStateChanged(int i) {
        this.currentCallState = i;
        updateState(isShowing(), false);
    }

    /* access modifiers changed from: private */
    public UndoView getUndoView() {
        if (this.undoView[0].getVisibility() == 0) {
            UndoView[] undoViewArr = this.undoView;
            UndoView undoView2 = undoViewArr[0];
            undoViewArr[0] = undoViewArr[1];
            undoViewArr[1] = undoView2;
            undoView2.hide(true, 2);
            this.containerView.removeView(this.undoView[0]);
            this.containerView.addView(this.undoView[0]);
        }
        return this.undoView[0];
    }

    /* access modifiers changed from: private */
    public float getColorProgress() {
        return this.colorProgress;
    }

    private void updateTitle(boolean z) {
        ChatObject.Call call2 = this.call;
        if (call2 == null) {
            this.titleTextView.setText(LocaleController.getString("VoipGroupScheduleVoiceChat", NUM), z);
            return;
        }
        if (!TextUtils.isEmpty(call2.call.title)) {
            if (!this.call.call.title.equals(this.actionBar.getTitle())) {
                if (z) {
                    this.actionBar.setTitleAnimated(this.call.call.title, true, 180);
                    this.actionBar.getTitleTextView().setOnClickListener(new View.OnClickListener() {
                        public final void onClick(View view) {
                            GroupCallActivity.this.lambda$updateTitle$32$GroupCallActivity(view);
                        }
                    });
                } else {
                    this.actionBar.setTitle(this.call.call.title);
                }
                this.titleTextView.setText(this.call.call.title, z);
            }
        } else if (!this.currentChat.title.equals(this.actionBar.getTitle())) {
            if (z) {
                this.actionBar.setTitleAnimated(this.currentChat.title, true, 180);
                this.actionBar.getTitleTextView().setOnClickListener(new View.OnClickListener() {
                    public final void onClick(View view) {
                        GroupCallActivity.this.lambda$updateTitle$33$GroupCallActivity(view);
                    }
                });
            } else {
                this.actionBar.setTitle(this.currentChat.title);
            }
            this.titleTextView.setText(LocaleController.getString("VoipGroupVoiceChat", NUM), z);
        }
        SimpleTextView titleTextView2 = this.actionBar.getTitleTextView();
        if (this.call.recording) {
            if (titleTextView2.getRightDrawable() == null) {
                titleTextView2.setRightDrawable((Drawable) new SmallRecordCallDrawable(titleTextView2));
                TextView textView = this.titleTextView.getTextView();
                textView.setCompoundDrawablesWithIntrinsicBounds((Drawable) null, (Drawable) null, new SmallRecordCallDrawable(textView), (Drawable) null);
                TextView nextTextView = this.titleTextView.getNextTextView();
                nextTextView.setCompoundDrawablesWithIntrinsicBounds((Drawable) null, (Drawable) null, new SmallRecordCallDrawable(nextTextView), (Drawable) null);
            }
        } else if (titleTextView2.getRightDrawable() != null) {
            titleTextView2.setRightDrawable((Drawable) null);
            this.titleTextView.getTextView().setCompoundDrawablesWithIntrinsicBounds((Drawable) null, (Drawable) null, (Drawable) null, (Drawable) null);
            this.titleTextView.getNextTextView().setCompoundDrawablesWithIntrinsicBounds((Drawable) null, (Drawable) null, (Drawable) null, (Drawable) null);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$updateTitle$32 */
    public /* synthetic */ void lambda$updateTitle$32$GroupCallActivity(View view) {
        showRecordHint(this.actionBar.getTitleTextView());
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$updateTitle$33 */
    public /* synthetic */ void lambda$updateTitle$33$GroupCallActivity(View view) {
        showRecordHint(this.actionBar.getTitleTextView());
    }

    /* access modifiers changed from: private */
    public void setColorProgress(float f) {
        String str;
        String str2;
        this.colorProgress = f;
        int offsetColor = AndroidUtilities.getOffsetColor(Theme.getColor("voipgroup_actionBarUnscrolled"), Theme.getColor("voipgroup_actionBar"), f, 1.0f);
        this.backgroundColor = offsetColor;
        this.actionBarBackground.setBackgroundColor(offsetColor);
        this.otherItem.redrawPopup(-14472653);
        this.shadowDrawable.setColorFilter(new PorterDuffColorFilter(this.backgroundColor, PorterDuff.Mode.MULTIPLY));
        this.navBarColor = this.backgroundColor;
        int offsetColor2 = AndroidUtilities.getOffsetColor(Theme.getColor("voipgroup_listViewBackgroundUnscrolled"), Theme.getColor("voipgroup_listViewBackground"), f, 1.0f);
        this.listViewBackgroundPaint.setColor(offsetColor2);
        this.listView.setGlowColor(offsetColor2);
        int i = this.muteButtonState;
        if (i == 3 || isGradientState(i)) {
            this.muteButton.invalidate();
        }
        int offsetColor3 = AndroidUtilities.getOffsetColor(Theme.getColor("voipgroup_leaveButton"), Theme.getColor("voipgroup_leaveButtonScrolled"), f, 1.0f);
        this.leaveButton.setBackgroundColor(offsetColor3, offsetColor3);
        int offsetColor4 = AndroidUtilities.getOffsetColor(Theme.getColor("voipgroup_lastSeenTextUnscrolled"), Theme.getColor("voipgroup_lastSeenText"), f, 1.0f);
        int offsetColor5 = AndroidUtilities.getOffsetColor(Theme.getColor("voipgroup_mutedIconUnscrolled"), Theme.getColor("voipgroup_mutedIcon"), f, 1.0f);
        int childCount = this.listView.getChildCount();
        for (int i2 = 0; i2 < childCount; i2++) {
            View childAt = this.listView.getChildAt(i2);
            if (childAt instanceof GroupCallTextCell) {
                ((GroupCallTextCell) childAt).setColors(offsetColor5, offsetColor4);
            } else if (childAt instanceof GroupCallUserCell) {
                GroupCallUserCell groupCallUserCell = (GroupCallUserCell) childAt;
                if (this.actionBar.getTag() != null) {
                    str2 = "voipgroup_mutedIcon";
                } else {
                    str2 = "voipgroup_mutedIconUnscrolled";
                }
                groupCallUserCell.setGrayIconColor(str2, offsetColor5);
            } else if (childAt instanceof GroupCallInvitedCell) {
                GroupCallInvitedCell groupCallInvitedCell = (GroupCallInvitedCell) childAt;
                if (this.actionBar.getTag() != null) {
                    str = "voipgroup_mutedIcon";
                } else {
                    str = "voipgroup_mutedIconUnscrolled";
                }
                groupCallInvitedCell.setGrayIconColor(str, offsetColor5);
            }
        }
        this.containerView.invalidate();
        this.listView.invalidate();
        this.container.invalidate();
    }

    /* access modifiers changed from: private */
    public void getLink(boolean z) {
        String str;
        TLRPC$TL_chatInviteExported tLRPC$TL_chatInviteExported;
        TLRPC$Chat chat = this.accountInstance.getMessagesController().getChat(Integer.valueOf(this.currentChat.id));
        if (chat != null && TextUtils.isEmpty(chat.username)) {
            TLRPC$ChatFull chatFull = this.accountInstance.getMessagesController().getChatFull(this.currentChat.id);
            if (!TextUtils.isEmpty(this.currentChat.username)) {
                str = this.accountInstance.getMessagesController().linkPrefix + "/" + this.currentChat.username;
            } else {
                str = (chatFull == null || (tLRPC$TL_chatInviteExported = chatFull.exported_invite) == null) ? null : tLRPC$TL_chatInviteExported.link;
            }
            if (TextUtils.isEmpty(str)) {
                TLRPC$TL_messages_exportChatInvite tLRPC$TL_messages_exportChatInvite = new TLRPC$TL_messages_exportChatInvite();
                tLRPC$TL_messages_exportChatInvite.peer = MessagesController.getInputPeer(this.currentChat);
                this.accountInstance.getConnectionsManager().sendRequest(tLRPC$TL_messages_exportChatInvite, new RequestDelegate(chatFull, z) {
                    public final /* synthetic */ TLRPC$ChatFull f$1;
                    public final /* synthetic */ boolean f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                        GroupCallActivity.this.lambda$getLink$35$GroupCallActivity(this.f$1, this.f$2, tLObject, tLRPC$TL_error);
                    }
                });
                return;
            }
            openShareAlert(true, (String) null, str, z);
        } else if (this.call != null) {
            int i = 0;
            while (i < 2) {
                TLRPC$TL_phone_exportGroupCallInvite tLRPC$TL_phone_exportGroupCallInvite = new TLRPC$TL_phone_exportGroupCallInvite();
                tLRPC$TL_phone_exportGroupCallInvite.call = this.call.getInputGroupCall();
                tLRPC$TL_phone_exportGroupCallInvite.can_self_unmute = i == 1;
                this.accountInstance.getConnectionsManager().sendRequest(tLRPC$TL_phone_exportGroupCallInvite, new RequestDelegate(i, z) {
                    public final /* synthetic */ int f$1;
                    public final /* synthetic */ boolean f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                        GroupCallActivity.this.lambda$getLink$37$GroupCallActivity(this.f$1, this.f$2, tLObject, tLRPC$TL_error);
                    }
                });
                i++;
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$getLink$35 */
    public /* synthetic */ void lambda$getLink$35$GroupCallActivity(TLRPC$ChatFull tLRPC$ChatFull, boolean z, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLObject, tLRPC$ChatFull, z) {
            public final /* synthetic */ TLObject f$1;
            public final /* synthetic */ TLRPC$ChatFull f$2;
            public final /* synthetic */ boolean f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                GroupCallActivity.this.lambda$null$34$GroupCallActivity(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$34 */
    public /* synthetic */ void lambda$null$34$GroupCallActivity(TLObject tLObject, TLRPC$ChatFull tLRPC$ChatFull, boolean z) {
        if (tLObject instanceof TLRPC$TL_chatInviteExported) {
            TLRPC$TL_chatInviteExported tLRPC$TL_chatInviteExported = (TLRPC$TL_chatInviteExported) tLObject;
            if (tLRPC$ChatFull != null) {
                tLRPC$ChatFull.exported_invite = tLRPC$TL_chatInviteExported;
            } else {
                openShareAlert(true, (String) null, tLRPC$TL_chatInviteExported.link, z);
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$getLink$37 */
    public /* synthetic */ void lambda$getLink$37$GroupCallActivity(int i, boolean z, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLObject, i, z) {
            public final /* synthetic */ TLObject f$1;
            public final /* synthetic */ int f$2;
            public final /* synthetic */ boolean f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                GroupCallActivity.this.lambda$null$36$GroupCallActivity(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$36 */
    public /* synthetic */ void lambda$null$36$GroupCallActivity(TLObject tLObject, int i, boolean z) {
        if (tLObject instanceof TLRPC$TL_phone_exportedGroupCallInvite) {
            this.invites[i] = ((TLRPC$TL_phone_exportedGroupCallInvite) tLObject).link;
        } else {
            this.invites[i] = "";
        }
        int i2 = 0;
        while (i2 < 2) {
            String[] strArr = this.invites;
            if (strArr[i2] != null) {
                if (strArr[i2].length() == 0) {
                    this.invites[i2] = null;
                }
                i2++;
            } else {
                return;
            }
        }
        if (ChatObject.canManageCalls(this.currentChat) && !this.call.call.join_muted) {
            this.invites[0] = null;
        }
        String[] strArr2 = this.invites;
        if (strArr2[0] == null && strArr2[1] == null && !TextUtils.isEmpty(this.currentChat.username)) {
            openShareAlert(true, (String) null, this.accountInstance.getMessagesController().linkPrefix + "/" + this.currentChat.username, z);
            return;
        }
        String[] strArr3 = this.invites;
        openShareAlert(false, strArr3[0], strArr3[1], z);
    }

    /* JADX WARNING: Removed duplicated region for block: B:19:0x0098  */
    /* JADX WARNING: Removed duplicated region for block: B:20:0x009b  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void openShareAlert(boolean r16, java.lang.String r17, java.lang.String r18, boolean r19) {
        /*
            r15 = this;
            r12 = r15
            r0 = 0
            if (r19 == 0) goto L_0x001c
            java.lang.String[] r1 = r12.invites
            r0 = r1[r0]
            org.telegram.messenger.AndroidUtilities.addToClipboard(r0)
            org.telegram.ui.Components.UndoView r1 = r15.getUndoView()
            r2 = 0
            r4 = 33
            r5 = 0
            r6 = 0
            r7 = 0
            r8 = 0
            r1.showWithAction(r2, r4, r5, r6, r7, r8)
            goto L_0x00a0
        L_0x001c:
            org.telegram.ui.LaunchActivity r1 = r12.parentActivity
            r2 = 1
            if (r1 == 0) goto L_0x004a
            org.telegram.ui.ActionBar.ActionBarLayout r1 = r1.getActionBarLayout()
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r1 = r1.fragmentsStack
            org.telegram.ui.LaunchActivity r3 = r12.parentActivity
            org.telegram.ui.ActionBar.ActionBarLayout r3 = r3.getActionBarLayout()
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r3 = r3.fragmentsStack
            int r3 = r3.size()
            int r3 = r3 - r2
            java.lang.Object r1 = r1.get(r3)
            org.telegram.ui.ActionBar.BaseFragment r1 = (org.telegram.ui.ActionBar.BaseFragment) r1
            boolean r3 = r1 instanceof org.telegram.ui.ChatActivity
            if (r3 == 0) goto L_0x004a
            org.telegram.ui.ChatActivity r1 = (org.telegram.ui.ChatActivity) r1
            boolean r1 = r1.needEnterText()
            r12.anyEnterEventSent = r2
            r12.enterEventSent = r2
            r13 = r1
            goto L_0x004b
        L_0x004a:
            r13 = 0
        L_0x004b:
            if (r17 == 0) goto L_0x0054
            if (r18 != 0) goto L_0x0054
            r1 = 0
            r8 = r17
            r9 = r1
            goto L_0x0058
        L_0x0054:
            r9 = r17
            r8 = r18
        L_0x0058:
            if (r9 != 0) goto L_0x006b
            if (r16 == 0) goto L_0x006b
            r1 = 2131628052(0x7f0e1014, float:1.8883386E38)
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r2[r0] = r8
            java.lang.String r0 = "VoipGroupInviteText"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            r5 = r0
            goto L_0x006c
        L_0x006b:
            r5 = r8
        L_0x006c:
            org.telegram.ui.GroupCallActivity$29 r14 = new org.telegram.ui.GroupCallActivity$29
            android.content.Context r2 = r15.getContext()
            r3 = 0
            r4 = 0
            r7 = 0
            r10 = 0
            r11 = 1
            r0 = r14
            r1 = r15
            r6 = r9
            r0.<init>(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11)
            r12.shareAlert = r14
            org.telegram.ui.GroupCallActivity$30 r0 = new org.telegram.ui.GroupCallActivity$30
            r0.<init>()
            r14.setDelegate(r0)
            org.telegram.ui.Components.ShareAlert r0 = r12.shareAlert
            org.telegram.ui.-$$Lambda$GroupCallActivity$ZAISsH0UxVjRkr_P5L78xNsg-pQ r1 = new org.telegram.ui.-$$Lambda$GroupCallActivity$ZAISsH0UxVjRkr_P5L78xNsg-pQ
            r1.<init>()
            r0.setOnDismissListener(r1)
            org.telegram.ui.-$$Lambda$GroupCallActivity$jCr6VFZXeU1Ui0I8gnYN8PYyt4Y r0 = new org.telegram.ui.-$$Lambda$GroupCallActivity$jCr6VFZXeU1Ui0I8gnYN8PYyt4Y
            r0.<init>()
            if (r13 == 0) goto L_0x009b
            r1 = 200(0xc8, double:9.9E-322)
            goto L_0x009d
        L_0x009b:
            r1 = 0
        L_0x009d:
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r0, r1)
        L_0x00a0:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.GroupCallActivity.openShareAlert(boolean, java.lang.String, java.lang.String, boolean):void");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$openShareAlert$38 */
    public /* synthetic */ void lambda$openShareAlert$38$GroupCallActivity(DialogInterface dialogInterface) {
        this.shareAlert = null;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$openShareAlert$39 */
    public /* synthetic */ void lambda$openShareAlert$39$GroupCallActivity() {
        ShareAlert shareAlert2 = this.shareAlert;
        if (shareAlert2 != null) {
            shareAlert2.show();
        }
    }

    /* access modifiers changed from: private */
    public void inviteUserToCall(int i, boolean z) {
        TLRPC$User user;
        if (this.call != null && (user = this.accountInstance.getMessagesController().getUser(Integer.valueOf(i))) != null) {
            AlertDialog[] alertDialogArr = {new AlertDialog(getContext(), 3)};
            TLRPC$TL_phone_inviteToGroupCall tLRPC$TL_phone_inviteToGroupCall = new TLRPC$TL_phone_inviteToGroupCall();
            tLRPC$TL_phone_inviteToGroupCall.call = this.call.getInputGroupCall();
            TLRPC$TL_inputUser tLRPC$TL_inputUser = new TLRPC$TL_inputUser();
            tLRPC$TL_inputUser.user_id = user.id;
            tLRPC$TL_inputUser.access_hash = user.access_hash;
            tLRPC$TL_phone_inviteToGroupCall.users.add(tLRPC$TL_inputUser);
            int sendRequest = this.accountInstance.getConnectionsManager().sendRequest(tLRPC$TL_phone_inviteToGroupCall, new RequestDelegate(i, alertDialogArr, user, z, tLRPC$TL_phone_inviteToGroupCall) {
                public final /* synthetic */ int f$1;
                public final /* synthetic */ AlertDialog[] f$2;
                public final /* synthetic */ TLRPC$User f$3;
                public final /* synthetic */ boolean f$4;
                public final /* synthetic */ TLRPC$TL_phone_inviteToGroupCall f$5;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r5;
                    this.f$5 = r6;
                }

                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    GroupCallActivity.this.lambda$inviteUserToCall$42$GroupCallActivity(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, tLObject, tLRPC$TL_error);
                }
            });
            if (sendRequest != 0) {
                AndroidUtilities.runOnUIThread(new Runnable(alertDialogArr, sendRequest) {
                    public final /* synthetic */ AlertDialog[] f$1;
                    public final /* synthetic */ int f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run() {
                        GroupCallActivity.this.lambda$inviteUserToCall$44$GroupCallActivity(this.f$1, this.f$2);
                    }
                }, 500);
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$inviteUserToCall$42 */
    public /* synthetic */ void lambda$inviteUserToCall$42$GroupCallActivity(int i, AlertDialog[] alertDialogArr, TLRPC$User tLRPC$User, boolean z, TLRPC$TL_phone_inviteToGroupCall tLRPC$TL_phone_inviteToGroupCall, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLObject != null) {
            this.accountInstance.getMessagesController().processUpdates((TLRPC$Updates) tLObject, false);
            AndroidUtilities.runOnUIThread(new Runnable(i, alertDialogArr, tLRPC$User) {
                public final /* synthetic */ int f$1;
                public final /* synthetic */ AlertDialog[] f$2;
                public final /* synthetic */ TLRPC$User f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void run() {
                    GroupCallActivity.this.lambda$null$40$GroupCallActivity(this.f$1, this.f$2, this.f$3);
                }
            });
            return;
        }
        AndroidUtilities.runOnUIThread(new Runnable(alertDialogArr, z, tLRPC$TL_error, i, tLRPC$TL_phone_inviteToGroupCall) {
            public final /* synthetic */ AlertDialog[] f$1;
            public final /* synthetic */ boolean f$2;
            public final /* synthetic */ TLRPC$TL_error f$3;
            public final /* synthetic */ int f$4;
            public final /* synthetic */ TLRPC$TL_phone_inviteToGroupCall f$5;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
                this.f$5 = r6;
            }

            public final void run() {
                GroupCallActivity.this.lambda$null$41$GroupCallActivity(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$40 */
    public /* synthetic */ void lambda$null$40$GroupCallActivity(int i, AlertDialog[] alertDialogArr, TLRPC$User tLRPC$User) {
        ChatObject.Call call2 = this.call;
        if (call2 != null && !this.delayedGroupCallUpdated) {
            call2.addInvitedUser(i);
            applyCallParticipantUpdates();
            GroupVoipInviteAlert groupVoipInviteAlert2 = this.groupVoipInviteAlert;
            if (groupVoipInviteAlert2 != null) {
                groupVoipInviteAlert2.dismiss();
            }
            try {
                alertDialogArr[0].dismiss();
            } catch (Throwable unused) {
            }
            alertDialogArr[0] = null;
            getUndoView().showWithAction(0, 34, tLRPC$User, (Object) null, (Runnable) null, (Runnable) null);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$41 */
    public /* synthetic */ void lambda$null$41$GroupCallActivity(AlertDialog[] alertDialogArr, boolean z, TLRPC$TL_error tLRPC$TL_error, int i, TLRPC$TL_phone_inviteToGroupCall tLRPC$TL_phone_inviteToGroupCall) {
        try {
            alertDialogArr[0].dismiss();
        } catch (Throwable unused) {
        }
        alertDialogArr[0] = null;
        if (!z || !"USER_NOT_PARTICIPANT".equals(tLRPC$TL_error.text)) {
            AlertsCreator.processError(this.currentAccount, tLRPC$TL_error, this.parentActivity.getActionBarLayout().fragmentsStack.get(this.parentActivity.getActionBarLayout().fragmentsStack.size() - 1), tLRPC$TL_phone_inviteToGroupCall, new Object[0]);
            return;
        }
        processSelectedOption((TLRPC$TL_groupCallParticipant) null, i, 3);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$inviteUserToCall$44 */
    public /* synthetic */ void lambda$inviteUserToCall$44$GroupCallActivity(AlertDialog[] alertDialogArr, int i) {
        if (alertDialogArr[0] != null) {
            alertDialogArr[0].setOnCancelListener(new DialogInterface.OnCancelListener(i) {
                public final /* synthetic */ int f$1;

                {
                    this.f$1 = r2;
                }

                public final void onCancel(DialogInterface dialogInterface) {
                    GroupCallActivity.this.lambda$null$43$GroupCallActivity(this.f$1, dialogInterface);
                }
            });
            alertDialogArr[0].show();
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$43 */
    public /* synthetic */ void lambda$null$43$GroupCallActivity(int i, DialogInterface dialogInterface) {
        this.accountInstance.getConnectionsManager().cancelRequest(i, true);
    }

    /* access modifiers changed from: private */
    public void updateLayout(boolean z) {
        float f;
        float f2;
        float f3;
        int childCount = this.listView.getChildCount();
        int i = 0;
        float f4 = 2.14748365E9f;
        for (int i2 = 0; i2 < childCount; i2++) {
            f4 = Math.min(f4, this.itemAnimator.getTargetY(this.listView.getChildAt(i2)));
        }
        if (f4 < 0.0f || f4 == 2.14748365E9f) {
            if (childCount != 0) {
                f4 = 0.0f;
            } else {
                f4 = (float) this.listView.getPaddingTop();
            }
        }
        boolean z2 = f4 <= ((float) (ActionBar.getCurrentActionBarHeight() - AndroidUtilities.dp(14.0f)));
        if ((z2 && this.actionBar.getTag() == null) || (!z2 && this.actionBar.getTag() != null)) {
            this.actionBar.setTag(z2 ? 1 : null);
            AnimatorSet animatorSet = this.actionBarAnimation;
            if (animatorSet != null) {
                animatorSet.cancel();
                this.actionBarAnimation = null;
            }
            setUseLightStatusBar(this.actionBar.getTag() == null);
            ViewPropertyAnimator scaleY = this.actionBar.getBackButton().animate().scaleX(z2 ? 1.0f : 0.9f).scaleY(z2 ? 1.0f : 0.9f);
            if (z2) {
                f = 0.0f;
            } else {
                f = (float) (-AndroidUtilities.dp(14.0f));
            }
            ViewPropertyAnimator duration = scaleY.translationX(f).setDuration(300);
            CubicBezierInterpolator cubicBezierInterpolator = CubicBezierInterpolator.DEFAULT;
            duration.setInterpolator(cubicBezierInterpolator).start();
            ViewPropertyAnimator animate = this.actionBar.getTitleTextView().animate();
            if (z2) {
                f2 = 0.0f;
            } else {
                f2 = (float) AndroidUtilities.dp(23.0f);
            }
            animate.translationY(f2).setDuration(300).setInterpolator(cubicBezierInterpolator).start();
            ViewPropertyAnimator animate2 = this.actionBar.getSubtitleTextView().animate();
            if (z2) {
                f3 = 0.0f;
            } else {
                f3 = (float) AndroidUtilities.dp(20.0f);
            }
            animate2.translationY(f3).setDuration(300).setInterpolator(cubicBezierInterpolator).start();
            AnimatorSet animatorSet2 = new AnimatorSet();
            this.actionBarAnimation = animatorSet2;
            animatorSet2.setDuration(140);
            AnimatorSet animatorSet3 = this.actionBarAnimation;
            Animator[] animatorArr = new Animator[3];
            ActionBar actionBar2 = this.actionBar;
            Property property = View.ALPHA;
            float[] fArr = new float[1];
            fArr[0] = z2 ? 1.0f : 0.0f;
            animatorArr[0] = ObjectAnimator.ofFloat(actionBar2, property, fArr);
            View view = this.actionBarBackground;
            Property property2 = View.ALPHA;
            float[] fArr2 = new float[1];
            fArr2[0] = z2 ? 1.0f : 0.0f;
            animatorArr[1] = ObjectAnimator.ofFloat(view, property2, fArr2);
            View view2 = this.actionBarShadow;
            Property property3 = View.ALPHA;
            float[] fArr3 = new float[1];
            fArr3[0] = z2 ? 1.0f : 0.0f;
            animatorArr[2] = ObjectAnimator.ofFloat(view2, property3, fArr3);
            animatorSet3.playTogether(animatorArr);
            this.actionBarAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    AnimatorSet unused = GroupCallActivity.this.actionBarAnimation = null;
                }
            });
            this.actionBarAnimation.start();
        }
        int i3 = ((FrameLayout.LayoutParams) this.listView.getLayoutParams()).topMargin;
        float f5 = f4 + ((float) i3);
        if (this.scrollOffsetY != f5) {
            RecyclerListView recyclerListView = this.listView;
            this.scrollOffsetY = f5;
            recyclerListView.setTopGlowOffset((int) (f5 - ((float) i3)));
            int dp = AndroidUtilities.dp(74.0f);
            float f6 = this.scrollOffsetY - ((float) dp);
            if (((float) this.backgroundPaddingTop) + f6 < ((float) (ActionBar.getCurrentActionBarHeight() * 2))) {
                float min = Math.min(1.0f, ((((float) (ActionBar.getCurrentActionBarHeight() * 2)) - f6) - ((float) this.backgroundPaddingTop)) / ((float) (((dp - this.backgroundPaddingTop) - AndroidUtilities.dp(14.0f)) + ActionBar.getCurrentActionBarHeight())));
                i = (int) (((float) AndroidUtilities.dp(AndroidUtilities.isTablet() ? 17.0f : 13.0f)) * min);
                if (Math.abs(Math.min(1.0f, min) - this.colorProgress) > 1.0E-4f) {
                    setColorProgress(Math.min(1.0f, min));
                }
                float f7 = 1.0f - ((0.1f * min) * 1.2f);
                this.titleTextView.setScaleX(Math.max(0.9f, f7));
                this.titleTextView.setScaleY(Math.max(0.9f, f7));
                this.titleTextView.setAlpha(Math.max(0.0f, 1.0f - (min * 1.2f)));
            } else {
                this.titleTextView.setScaleX(1.0f);
                this.titleTextView.setScaleY(1.0f);
                this.titleTextView.setAlpha(1.0f);
                if (this.colorProgress > 1.0E-4f) {
                    setColorProgress(0.0f);
                }
            }
            float f8 = (float) i;
            this.menuItemsContainer.setTranslationY(Math.max((float) AndroidUtilities.dp(4.0f), (this.scrollOffsetY - ((float) AndroidUtilities.dp(53.0f))) - f8));
            this.titleTextView.setTranslationY(Math.max((float) AndroidUtilities.dp(4.0f), (this.scrollOffsetY - ((float) AndroidUtilities.dp(44.0f))) - f8));
            LinearLayout linearLayout = this.scheduleTimerContainer;
            if (linearLayout != null) {
                linearLayout.setTranslationY(Math.max((float) AndroidUtilities.dp(4.0f), (this.scrollOffsetY - ((float) AndroidUtilities.dp(44.0f))) - f8));
            }
            this.containerView.invalidate();
        }
    }

    private void cancelMutePress() {
        if (this.scheduled) {
            this.scheduled = false;
            AndroidUtilities.cancelRunOnUIThread(this.pressRunnable);
        }
        if (this.pressed) {
            this.pressed = false;
            MotionEvent obtain = MotionEvent.obtain(0, 0, 3, 0.0f, 0.0f, 0);
            this.muteButton.onTouchEvent(obtain);
            obtain.recycle();
        }
    }

    private void updateState(boolean z, boolean z2) {
        int i;
        ChatObject.Call call2 = this.call;
        int i2 = 6;
        int i3 = 5;
        if (call2 == null || call2.isScheduled()) {
            if (!ChatObject.canManageCalls(this.currentChat)) {
                if (this.call.call.schedule_start_subscribed) {
                    i2 = 7;
                }
                i3 = i2;
            }
            updateMuteButton(i3, z);
            this.leaveButton.setData(NUM, -1, Theme.getColor("voipgroup_leaveButton"), 0.3f, false, LocaleController.getString("Close", NUM), false, false);
            updateScheduleUI(false);
            return;
        }
        VoIPService sharedInstance = VoIPService.getSharedInstance();
        if (sharedInstance != null) {
            if (sharedInstance.isSwitchingStream() || ((this.creatingServiceTime != 0 && Math.abs(SystemClock.elapsedRealtime() - this.creatingServiceTime) <= 3000) || !((i = this.currentCallState) == 1 || i == 2 || i == 6 || i == 5))) {
                if (this.userSwitchObject != null) {
                    getUndoView().showWithAction(0, 37, (Object) this.userSwitchObject);
                    this.userSwitchObject = null;
                }
                TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant = this.call.participants.get(MessageObject.getPeerId(this.selfPeer));
                if (tLRPC$TL_groupCallParticipant == null || tLRPC$TL_groupCallParticipant.can_self_unmute || !tLRPC$TL_groupCallParticipant.muted || ChatObject.canManageCalls(this.currentChat)) {
                    boolean isMicMute = sharedInstance.isMicMute();
                    if (z2 && tLRPC$TL_groupCallParticipant != null && tLRPC$TL_groupCallParticipant.muted && !isMicMute) {
                        cancelMutePress();
                        sharedInstance.setMicMute(true, false, false);
                        isMicMute = true;
                    }
                    if (isMicMute) {
                        updateMuteButton(0, z);
                    } else {
                        updateMuteButton(1, z);
                    }
                } else {
                    cancelMutePress();
                    if (tLRPC$TL_groupCallParticipant.raise_hand_rating != 0) {
                        updateMuteButton(4, z);
                    } else {
                        updateMuteButton(2, z);
                    }
                    sharedInstance.setMicMute(true, false, false);
                }
            } else {
                cancelMutePress();
                updateMuteButton(3, z);
            }
        }
    }

    public void onAudioSettingsChanged() {
        updateSpeakerPhoneIcon(true);
        int childCount = this.listView.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = this.listView.getChildAt(i);
            if (childAt instanceof GroupCallUserCell) {
                ((GroupCallUserCell) childAt).applyParticipantChanges(true);
            }
        }
    }

    private void updateSpeakerPhoneIcon(boolean z) {
        if (this.soundButton != null) {
            VoIPService sharedInstance = VoIPService.getSharedInstance();
            boolean z2 = false;
            if (sharedInstance == null) {
                this.soundButton.setData(NUM, -1, 0, 0.3f, true, LocaleController.getString("VoipChatShare", NUM), false, z);
                this.soundButton.setEnabled(!TextUtils.isEmpty(this.currentChat.username) || (ChatObject.hasAdminRights(this.currentChat) && ChatObject.canAddUsers(this.currentChat)), false);
                this.soundButton.setChecked(true, false);
                return;
            }
            this.soundButton.setEnabled(true, z);
            boolean z3 = sharedInstance.isBluetoothOn() || sharedInstance.isBluetoothWillOn();
            if (!z3 && sharedInstance.isSpeakerphoneOn()) {
                z2 = true;
            }
            if (z3) {
                this.soundButton.setData(NUM, -1, 0, 0.1f, true, LocaleController.getString("VoipAudioRoutingBluetooth", NUM), false, z);
            } else if (z2) {
                this.soundButton.setData(NUM, -1, 0, 0.3f, true, LocaleController.getString("VoipSpeaker", NUM), false, z);
            } else if (sharedInstance.isHeadsetPlugged()) {
                this.soundButton.setData(NUM, -1, 0, 0.1f, true, LocaleController.getString("VoipAudioRoutingHeadset", NUM), false, z);
            } else {
                this.soundButton.setData(NUM, -1, 0, 0.1f, true, LocaleController.getString("VoipSpeaker", NUM), false, z);
            }
            this.soundButton.setChecked(z2, z);
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:145:0x0324  */
    /* JADX WARNING: Removed duplicated region for block: B:82:0x0193  */
    /* JADX WARNING: Removed duplicated region for block: B:83:0x019d  */
    /* JADX WARNING: Removed duplicated region for block: B:87:0x01b7  */
    /* JADX WARNING: Removed duplicated region for block: B:88:0x01cc  */
    /* JADX WARNING: Removed duplicated region for block: B:91:0x01d4  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void updateMuteButton(int r18, boolean r19) {
        /*
            r17 = this;
            r0 = r17
            r1 = r18
            r2 = r19
            int r3 = r0.muteButtonState
            if (r3 != r1) goto L_0x000d
            if (r2 == 0) goto L_0x000d
            return
        L_0x000d:
            android.animation.ValueAnimator r3 = r0.muteButtonAnimator
            if (r3 == 0) goto L_0x0017
            r3.cancel()
            r3 = 0
            r0.muteButtonAnimator = r3
        L_0x0017:
            r4 = 311(0x137, float:4.36E-43)
            r6 = 274(0x112, float:3.84E-43)
            r7 = 173(0xad, float:2.42E-43)
            r8 = 344(0x158, float:4.82E-43)
            r9 = 202(0xca, float:2.83E-43)
            r11 = 136(0x88, float:1.9E-43)
            java.lang.String r14 = ""
            r15 = 99
            r10 = 5
            r3 = 6
            r13 = 7
            if (r1 != r13) goto L_0x003e
            r5 = 2131628033(0x7f0e1001, float:1.8883347E38)
            java.lang.String r12 = "VoipGroupCancelReminder"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r12, r5)
            org.telegram.ui.Components.RLottieDrawable r12 = r0.bigMicDrawable
            boolean r12 = r12.setCustomEndFrame(r9)
        L_0x003b:
            r8 = 0
            goto L_0x01b1
        L_0x003e:
            if (r1 != r3) goto L_0x0050
            r5 = 2131628091(0x7f0e103b, float:1.8883465E38)
            java.lang.String r12 = "VoipGroupSetReminder"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r12, r5)
            org.telegram.ui.Components.RLottieDrawable r12 = r0.bigMicDrawable
            boolean r12 = r12.setCustomEndFrame(r8)
            goto L_0x003b
        L_0x0050:
            if (r1 != r10) goto L_0x0064
            r5 = 2131628100(0x7f0e1044, float:1.8883483E38)
            java.lang.String r12 = "VoipGroupStartNow"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r12, r5)
            org.telegram.ui.Components.RLottieDrawable r12 = r0.bigMicDrawable
            r9 = 377(0x179, float:5.28E-43)
            boolean r12 = r12.setCustomEndFrame(r9)
            goto L_0x003b
        L_0x0064:
            r5 = 404(0x194, float:5.66E-43)
            r9 = 3
            if (r1 != 0) goto L_0x00d0
            r12 = 2131628109(0x7f0e104d, float:1.8883501E38)
            java.lang.String r14 = "VoipGroupUnmute"
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r14, r12)
            r14 = 2131628123(0x7f0e105b, float:1.888353E38)
            java.lang.String r8 = "VoipHoldAndTalk"
            java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r8, r14)
            int r8 = r0.muteButtonState
            if (r8 != r9) goto L_0x0097
            org.telegram.ui.Components.RLottieDrawable r5 = r0.bigMicDrawable
            int r5 = r5.getCustomEndFrame()
            if (r5 == r11) goto L_0x0090
            if (r5 == r7) goto L_0x0090
            if (r5 == r6) goto L_0x0090
            if (r5 != r4) goto L_0x008e
            goto L_0x0090
        L_0x008e:
            r5 = 0
            goto L_0x009f
        L_0x0090:
            org.telegram.ui.Components.RLottieDrawable r5 = r0.bigMicDrawable
            boolean r5 = r5.setCustomEndFrame(r15)
            goto L_0x009f
        L_0x0097:
            if (r8 != r10) goto L_0x00a7
            org.telegram.ui.Components.RLottieDrawable r8 = r0.bigMicDrawable
            boolean r5 = r8.setCustomEndFrame(r5)
        L_0x009f:
            r8 = 0
            r16 = r12
            r12 = r5
            r5 = r16
            goto L_0x01b1
        L_0x00a7:
            if (r8 != r13) goto L_0x00b2
            org.telegram.ui.Components.RLottieDrawable r5 = r0.bigMicDrawable
            r8 = 376(0x178, float:5.27E-43)
            boolean r5 = r5.setCustomEndFrame(r8)
            goto L_0x009f
        L_0x00b2:
            if (r8 != r3) goto L_0x00bd
            org.telegram.ui.Components.RLottieDrawable r5 = r0.bigMicDrawable
            r8 = 237(0xed, float:3.32E-43)
            boolean r5 = r5.setCustomEndFrame(r8)
            goto L_0x009f
        L_0x00bd:
            r5 = 2
            if (r8 != r5) goto L_0x00c9
            org.telegram.ui.Components.RLottieDrawable r5 = r0.bigMicDrawable
            r8 = 36
            boolean r5 = r5.setCustomEndFrame(r8)
            goto L_0x009f
        L_0x00c9:
            org.telegram.ui.Components.RLottieDrawable r5 = r0.bigMicDrawable
            boolean r5 = r5.setCustomEndFrame(r15)
            goto L_0x009f
        L_0x00d0:
            r8 = 1
            if (r1 != r8) goto L_0x00ef
            r5 = 2131628171(0x7f0e108b, float:1.8883627E38)
            java.lang.String r8 = "VoipTapToMute"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r8, r5)
            org.telegram.ui.Components.RLottieDrawable r8 = r0.bigMicDrawable
            int r9 = r0.muteButtonState
            r12 = 4
            if (r9 != r12) goto L_0x00e6
            r9 = 99
            goto L_0x00e8
        L_0x00e6:
            r9 = 69
        L_0x00e8:
            boolean r8 = r8.setCustomEndFrame(r9)
            r12 = r8
            goto L_0x003b
        L_0x00ef:
            r12 = 4
            if (r1 != r12) goto L_0x010c
            r5 = 2131628134(0x7f0e1066, float:1.8883552E38)
            java.lang.String r8 = "VoipMutedTapedForSpeak"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r8, r5)
            r8 = 2131628135(0x7f0e1067, float:1.8883554E38)
            java.lang.String r9 = "VoipMutedTapedForSpeakInfo"
            java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r9, r8)
            org.telegram.ui.Components.RLottieDrawable r8 = r0.bigMicDrawable
            boolean r12 = r8.setCustomEndFrame(r11)
            goto L_0x003b
        L_0x010c:
            org.telegram.messenger.ChatObject$Call r8 = r0.call
            android.util.SparseArray<org.telegram.tgnet.TLRPC$TL_groupCallParticipant> r8 = r8.participants
            org.telegram.tgnet.TLRPC$Peer r12 = r0.selfPeer
            int r12 = org.telegram.messenger.MessageObject.getPeerId(r12)
            java.lang.Object r8 = r8.get(r12)
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r8 = (org.telegram.tgnet.TLRPC$TL_groupCallParticipant) r8
            if (r8 == 0) goto L_0x0130
            boolean r12 = r8.can_self_unmute
            if (r12 != 0) goto L_0x0130
            boolean r8 = r8.muted
            if (r8 == 0) goto L_0x0130
            org.telegram.tgnet.TLRPC$Chat r8 = r0.currentChat
            boolean r8 = org.telegram.messenger.ChatObject.canManageCalls(r8)
            if (r8 != 0) goto L_0x0130
            r8 = 1
            goto L_0x0131
        L_0x0130:
            r8 = 0
        L_0x0131:
            if (r8 == 0) goto L_0x0158
            int r5 = r0.muteButtonState
            if (r5 != r13) goto L_0x013e
            org.telegram.ui.Components.RLottieDrawable r5 = r0.bigMicDrawable
            boolean r5 = r5.setCustomEndFrame(r4)
            goto L_0x0162
        L_0x013e:
            if (r5 != r3) goto L_0x0147
            org.telegram.ui.Components.RLottieDrawable r5 = r0.bigMicDrawable
            boolean r5 = r5.setCustomEndFrame(r6)
            goto L_0x0162
        L_0x0147:
            r12 = 1
            if (r5 != r12) goto L_0x0151
            org.telegram.ui.Components.RLottieDrawable r5 = r0.bigMicDrawable
            boolean r5 = r5.setCustomEndFrame(r7)
            goto L_0x0162
        L_0x0151:
            org.telegram.ui.Components.RLottieDrawable r5 = r0.bigMicDrawable
            boolean r5 = r5.setCustomEndFrame(r11)
            goto L_0x0162
        L_0x0158:
            int r12 = r0.muteButtonState
            if (r12 != r10) goto L_0x0165
            org.telegram.ui.Components.RLottieDrawable r12 = r0.bigMicDrawable
            boolean r5 = r12.setCustomEndFrame(r5)
        L_0x0162:
            r12 = 36
            goto L_0x0191
        L_0x0165:
            if (r12 != r13) goto L_0x0170
            org.telegram.ui.Components.RLottieDrawable r5 = r0.bigMicDrawable
            r12 = 376(0x178, float:5.27E-43)
            boolean r5 = r5.setCustomEndFrame(r12)
            goto L_0x0162
        L_0x0170:
            if (r12 != r3) goto L_0x017b
            org.telegram.ui.Components.RLottieDrawable r5 = r0.bigMicDrawable
            r12 = 237(0xed, float:3.32E-43)
            boolean r5 = r5.setCustomEndFrame(r12)
            goto L_0x0162
        L_0x017b:
            r5 = 2
            if (r12 == r5) goto L_0x0189
            r5 = 4
            if (r12 != r5) goto L_0x0182
            goto L_0x0189
        L_0x0182:
            org.telegram.ui.Components.RLottieDrawable r5 = r0.bigMicDrawable
            boolean r5 = r5.setCustomEndFrame(r15)
            goto L_0x0162
        L_0x0189:
            org.telegram.ui.Components.RLottieDrawable r5 = r0.bigMicDrawable
            r12 = 36
            boolean r5 = r5.setCustomEndFrame(r12)
        L_0x0191:
            if (r1 != r9) goto L_0x019d
            r9 = 2131624965(0x7f0e0405, float:1.8877125E38)
            java.lang.String r12 = "Connecting"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r12, r9)
            goto L_0x01af
        L_0x019d:
            r9 = 2131628130(0x7f0e1062, float:1.8883544E38)
            java.lang.String r12 = "VoipMutedByAdmin"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r12, r9)
            r12 = 2131628133(0x7f0e1065, float:1.888355E38)
            java.lang.String r14 = "VoipMutedTapForSpeak"
            java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r14, r12)
        L_0x01af:
            r12 = r5
            r5 = r9
        L_0x01b1:
            boolean r9 = android.text.TextUtils.isEmpty(r14)
            if (r9 != 0) goto L_0x01cc
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            r9.append(r5)
            java.lang.String r11 = " "
            r9.append(r11)
            r9.append(r14)
            java.lang.String r9 = r9.toString()
            goto L_0x01cd
        L_0x01cc:
            r9 = r5
        L_0x01cd:
            org.telegram.ui.Components.RLottieImageView r11 = r0.muteButton
            r11.setContentDescription(r9)
            if (r2 == 0) goto L_0x0324
            if (r12 == 0) goto L_0x02a6
            if (r1 != r10) goto L_0x01e1
            org.telegram.ui.Components.RLottieDrawable r3 = r0.bigMicDrawable
            r4 = 376(0x178, float:5.27E-43)
            r3.setCurrentFrame(r4)
            goto L_0x02a6
        L_0x01e1:
            if (r1 != r13) goto L_0x01ea
            org.telegram.ui.Components.RLottieDrawable r3 = r0.bigMicDrawable
            r3.setCurrentFrame(r7)
            goto L_0x02a6
        L_0x01ea:
            if (r1 != r3) goto L_0x01f3
            org.telegram.ui.Components.RLottieDrawable r3 = r0.bigMicDrawable
            r3.setCurrentFrame(r4)
            goto L_0x02a6
        L_0x01f3:
            if (r1 != 0) goto L_0x022c
            int r4 = r0.muteButtonState
            if (r4 != r10) goto L_0x0202
            org.telegram.ui.Components.RLottieDrawable r3 = r0.bigMicDrawable
            r4 = 376(0x178, float:5.27E-43)
            r3.setCurrentFrame(r4)
            goto L_0x02a6
        L_0x0202:
            if (r4 != r13) goto L_0x020d
            org.telegram.ui.Components.RLottieDrawable r3 = r0.bigMicDrawable
            r4 = 344(0x158, float:4.82E-43)
            r3.setCurrentFrame(r4)
            goto L_0x02a6
        L_0x020d:
            if (r4 != r3) goto L_0x0218
            org.telegram.ui.Components.RLottieDrawable r3 = r0.bigMicDrawable
            r4 = 202(0xca, float:2.83E-43)
            r3.setCurrentFrame(r4)
            goto L_0x02a6
        L_0x0218:
            r3 = 2
            if (r4 != r3) goto L_0x0223
            org.telegram.ui.Components.RLottieDrawable r3 = r0.bigMicDrawable
            r4 = 0
            r3.setCurrentFrame(r4)
            goto L_0x02a7
        L_0x0223:
            org.telegram.ui.Components.RLottieDrawable r3 = r0.bigMicDrawable
            r4 = 69
            r3.setCurrentFrame(r4)
            goto L_0x02a6
        L_0x022c:
            r4 = 1
            if (r1 != r4) goto L_0x0240
            org.telegram.ui.Components.RLottieDrawable r3 = r0.bigMicDrawable
            int r4 = r0.muteButtonState
            r7 = 4
            if (r4 != r7) goto L_0x0239
            r4 = 69
            goto L_0x023b
        L_0x0239:
            r4 = 36
        L_0x023b:
            r3.setCurrentFrame(r4)
            goto L_0x02a6
        L_0x0240:
            r7 = 4
            if (r1 != r7) goto L_0x0249
            org.telegram.ui.Components.RLottieDrawable r3 = r0.bigMicDrawable
            r3.setCurrentFrame(r15)
            goto L_0x02a6
        L_0x0249:
            if (r8 == 0) goto L_0x0270
            int r4 = r0.muteButtonState
            if (r4 != r13) goto L_0x0255
            org.telegram.ui.Components.RLottieDrawable r3 = r0.bigMicDrawable
            r3.setCurrentFrame(r6)
            goto L_0x02a6
        L_0x0255:
            if (r4 != r3) goto L_0x025f
            org.telegram.ui.Components.RLottieDrawable r3 = r0.bigMicDrawable
            r4 = 237(0xed, float:3.32E-43)
            r3.setCurrentFrame(r4)
            goto L_0x02a6
        L_0x025f:
            r3 = 1
            if (r4 != r3) goto L_0x026a
            org.telegram.ui.Components.RLottieDrawable r3 = r0.bigMicDrawable
            r4 = 136(0x88, float:1.9E-43)
            r3.setCurrentFrame(r4)
            goto L_0x02a6
        L_0x026a:
            org.telegram.ui.Components.RLottieDrawable r3 = r0.bigMicDrawable
            r3.setCurrentFrame(r15)
            goto L_0x02a6
        L_0x0270:
            int r4 = r0.muteButtonState
            if (r4 != r10) goto L_0x027c
            org.telegram.ui.Components.RLottieDrawable r3 = r0.bigMicDrawable
            r4 = 376(0x178, float:5.27E-43)
            r3.setCurrentFrame(r4)
            goto L_0x02a6
        L_0x027c:
            if (r4 != r13) goto L_0x0286
            org.telegram.ui.Components.RLottieDrawable r3 = r0.bigMicDrawable
            r4 = 344(0x158, float:4.82E-43)
            r3.setCurrentFrame(r4)
            goto L_0x02a6
        L_0x0286:
            if (r4 != r3) goto L_0x0290
            org.telegram.ui.Components.RLottieDrawable r3 = r0.bigMicDrawable
            r4 = 202(0xca, float:2.83E-43)
            r3.setCurrentFrame(r4)
            goto L_0x02a6
        L_0x0290:
            r3 = 2
            if (r4 == r3) goto L_0x029f
            r3 = 4
            if (r4 != r3) goto L_0x0297
            goto L_0x029f
        L_0x0297:
            org.telegram.ui.Components.RLottieDrawable r3 = r0.bigMicDrawable
            r4 = 69
            r3.setCurrentFrame(r4)
            goto L_0x02a6
        L_0x029f:
            org.telegram.ui.Components.RLottieDrawable r3 = r0.bigMicDrawable
            r4 = 0
            r3.setCurrentFrame(r4)
            goto L_0x02a7
        L_0x02a6:
            r4 = 0
        L_0x02a7:
            org.telegram.ui.Components.RLottieImageView r3 = r0.muteButton
            r3.playAnimation()
            android.widget.TextView[] r3 = r0.muteLabel
            r6 = 1
            r3 = r3[r6]
            r3.setVisibility(r4)
            android.widget.TextView[] r3 = r0.muteLabel
            r3 = r3[r6]
            r4 = 0
            r3.setAlpha(r4)
            android.widget.TextView[] r3 = r0.muteLabel
            r3 = r3[r6]
            r7 = 1084227584(0x40a00000, float:5.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r7 = -r7
            float r7 = (float) r7
            r3.setTranslationY(r7)
            android.widget.TextView[] r3 = r0.muteLabel
            r3 = r3[r6]
            r3.setText(r5)
            android.widget.TextView[] r3 = r0.muteSubLabel
            r3 = r3[r6]
            r5 = 0
            r3.setVisibility(r5)
            android.widget.TextView[] r3 = r0.muteSubLabel
            r3 = r3[r6]
            r3.setAlpha(r4)
            android.widget.TextView[] r3 = r0.muteSubLabel
            r3 = r3[r6]
            r4 = 1084227584(0x40a00000, float:5.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r4 = -r4
            float r4 = (float) r4
            r3.setTranslationY(r4)
            android.widget.TextView[] r3 = r0.muteSubLabel
            r3 = r3[r6]
            r3.setText(r14)
            r3 = 2
            float[] r3 = new float[r3]
            r3 = {0, NUM} // fill-array
            android.animation.ValueAnimator r3 = android.animation.ValueAnimator.ofFloat(r3)
            r0.muteButtonAnimator = r3
            org.telegram.ui.-$$Lambda$GroupCallActivity$hN-GMBczmfW5eNfu_ltYOsWbSB8 r4 = new org.telegram.ui.-$$Lambda$GroupCallActivity$hN-GMBczmfW5eNfu_ltYOsWbSB8
            r4.<init>()
            r3.addUpdateListener(r4)
            android.animation.ValueAnimator r3 = r0.muteButtonAnimator
            org.telegram.ui.GroupCallActivity$32 r4 = new org.telegram.ui.GroupCallActivity$32
            r4.<init>()
            r3.addListener(r4)
            android.animation.ValueAnimator r3 = r0.muteButtonAnimator
            r4 = 180(0xb4, double:8.9E-322)
            r3.setDuration(r4)
            android.animation.ValueAnimator r3 = r0.muteButtonAnimator
            r3.start()
            r0.muteButtonState = r1
            goto L_0x0340
        L_0x0324:
            r0.muteButtonState = r1
            org.telegram.ui.Components.RLottieDrawable r1 = r0.bigMicDrawable
            int r3 = r1.getCustomEndFrame()
            r4 = 1
            int r3 = r3 - r4
            r6 = 0
            r1.setCurrentFrame(r3, r6, r4)
            android.widget.TextView[] r1 = r0.muteLabel
            r1 = r1[r6]
            r1.setText(r5)
            android.widget.TextView[] r1 = r0.muteSubLabel
            r1 = r1[r6]
            r1.setText(r14)
        L_0x0340:
            r0.updateMuteButtonState(r2)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.GroupCallActivity.updateMuteButton(int, boolean):void");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$updateMuteButton$45 */
    public /* synthetic */ void lambda$updateMuteButton$45$GroupCallActivity(ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        float f = 1.0f - floatValue;
        this.muteLabel[0].setAlpha(f);
        this.muteLabel[0].setTranslationY(((float) AndroidUtilities.dp(5.0f)) * floatValue);
        this.muteSubLabel[0].setAlpha(f);
        this.muteSubLabel[0].setTranslationY(((float) AndroidUtilities.dp(5.0f)) * floatValue);
        this.muteLabel[1].setAlpha(floatValue);
        float f2 = (5.0f * floatValue) - 0.875f;
        this.muteLabel[1].setTranslationY((float) AndroidUtilities.dp(f2));
        this.muteSubLabel[1].setAlpha(floatValue);
        this.muteSubLabel[1].setTranslationY((float) AndroidUtilities.dp(f2));
    }

    /* access modifiers changed from: private */
    public void fillColors(int i, int[] iArr) {
        if (i == 0) {
            iArr[0] = Theme.getColor("voipgroup_unmuteButton2");
            iArr[1] = AndroidUtilities.getOffsetColor(Theme.getColor("voipgroup_soundButtonActive"), Theme.getColor("voipgroup_soundButtonActiveScrolled"), this.colorProgress, 1.0f);
            iArr[2] = Theme.getColor("voipgroup_soundButton");
        } else if (i == 1) {
            iArr[0] = Theme.getColor("voipgroup_muteButton2");
            iArr[1] = AndroidUtilities.getOffsetColor(Theme.getColor("voipgroup_soundButtonActive2"), Theme.getColor("voipgroup_soundButtonActive2Scrolled"), this.colorProgress, 1.0f);
            iArr[2] = Theme.getColor("voipgroup_soundButton2");
        } else if (isGradientState(i)) {
            iArr[0] = Theme.getColor("voipgroup_mutedByAdminGradient3");
            iArr[1] = Theme.getColor("voipgroup_mutedByAdminMuteButton");
            iArr[2] = Theme.getColor("voipgroup_mutedByAdminMuteButtonDisabled");
        } else {
            iArr[0] = Theme.getColor("voipgroup_disabledButton");
            iArr[1] = AndroidUtilities.getOffsetColor(Theme.getColor("voipgroup_disabledButtonActive"), Theme.getColor("voipgroup_disabledButtonActiveScrolled"), this.colorProgress, 1.0f);
            iArr[2] = AndroidUtilities.getOffsetColor(Theme.getColor("voipgroup_listViewBackgroundUnscrolled"), Theme.getColor("voipgroup_disabledButton"), this.colorProgress, 1.0f);
        }
    }

    /* access modifiers changed from: private */
    public void showRecordHint(View view) {
        if (this.recordHintView == null) {
            HintView hintView = new HintView(getContext(), 8, true);
            this.recordHintView = hintView;
            hintView.setAlpha(0.0f);
            this.recordHintView.setVisibility(4);
            this.recordHintView.setShowingDuration(3000);
            this.containerView.addView(this.recordHintView, LayoutHelper.createFrame(-2, -2.0f, 51, 19.0f, 0.0f, 19.0f, 0.0f));
            this.recordHintView.setText(LocaleController.getString("VoipGroupRecording", NUM));
            this.recordHintView.setBackgroundColor(-NUM, -1);
        }
        this.recordHintView.setExtraTranslationY((float) (-AndroidUtilities.statusBarHeight));
        this.recordHintView.showForView(view, true);
    }

    /* access modifiers changed from: private */
    public void showReminderHint() {
        SharedPreferences globalMainSettings = MessagesController.getGlobalMainSettings();
        if (!globalMainSettings.getBoolean("reminderhint", false)) {
            globalMainSettings.edit().putBoolean("reminderhint", true).commit();
            if (this.reminderHintView == null) {
                HintView hintView = new HintView(getContext(), 8);
                this.reminderHintView = hintView;
                hintView.setAlpha(0.0f);
                this.reminderHintView.setVisibility(4);
                this.reminderHintView.setShowingDuration(4000);
                this.containerView.addView(this.reminderHintView, LayoutHelper.createFrame(-2, -2.0f, 51, 19.0f, 0.0f, 19.0f, 0.0f));
                this.reminderHintView.setText(LocaleController.getString("VoipChatReminderHint", NUM));
                this.reminderHintView.setBackgroundColor(-NUM, -1);
            }
            this.reminderHintView.setExtraTranslationY((float) (-AndroidUtilities.statusBarHeight));
            this.reminderHintView.showForView(this.muteButton, true);
        }
    }

    private void updateMuteButtonState(boolean z) {
        this.muteButton.invalidate();
        WeavingState[] weavingStateArr = this.states;
        int i = this.muteButtonState;
        boolean z2 = true;
        boolean z3 = false;
        if (weavingStateArr[i] == null) {
            weavingStateArr[i] = new WeavingState(i);
            int i2 = this.muteButtonState;
            if (i2 == 3) {
                Shader unused = this.states[i2].shader = null;
            } else if (isGradientState(i2)) {
                Shader unused2 = this.states[this.muteButtonState].shader = new LinearGradient(0.0f, 400.0f, 400.0f, 0.0f, new int[]{Theme.getColor("voipgroup_mutedByAdminGradient"), Theme.getColor("voipgroup_mutedByAdminGradient3"), Theme.getColor("voipgroup_mutedByAdminGradient2")}, (float[]) null, Shader.TileMode.CLAMP);
            } else {
                int i3 = this.muteButtonState;
                if (i3 == 1) {
                    Shader unused3 = this.states[i3].shader = new RadialGradient(200.0f, 200.0f, 200.0f, new int[]{Theme.getColor("voipgroup_muteButton"), Theme.getColor("voipgroup_muteButton3")}, (float[]) null, Shader.TileMode.CLAMP);
                } else {
                    Shader unused4 = this.states[i3].shader = new RadialGradient(200.0f, 200.0f, 200.0f, new int[]{Theme.getColor("voipgroup_unmuteButton2"), Theme.getColor("voipgroup_unmuteButton")}, (float[]) null, Shader.TileMode.CLAMP);
                }
            }
        }
        WeavingState[] weavingStateArr2 = this.states;
        int i4 = this.muteButtonState;
        WeavingState weavingState = weavingStateArr2[i4];
        WeavingState weavingState2 = this.currentState;
        float f = 0.0f;
        if (weavingState != weavingState2) {
            this.prevState = weavingState2;
            this.currentState = weavingStateArr2[i4];
            if (weavingState2 == null || !z) {
                this.switchProgress = 1.0f;
                this.prevState = null;
            } else {
                this.switchProgress = 0.0f;
            }
        }
        if (!z) {
            WeavingState weavingState3 = this.currentState;
            if (weavingState3 != null) {
                boolean z4 = weavingState3.currentState == 1 || this.currentState.currentState == 0;
                if (this.currentState.currentState == 3) {
                    z2 = false;
                }
                z3 = z4;
            } else {
                z2 = false;
            }
            this.showWavesProgress = z3 ? 1.0f : 0.0f;
            if (z2) {
                f = 1.0f;
            }
            this.showLightingProgress = f;
        }
        this.buttonsContainer.invalidate();
    }

    /* access modifiers changed from: private */
    public static void processOnLeave(ChatObject.Call call2, boolean z, int i, Runnable runnable) {
        TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant;
        if (VoIPService.getSharedInstance() != null) {
            VoIPService.getSharedInstance().hangUp(z ? 1 : 0);
        }
        if (!(call2 == null || (tLRPC$TL_groupCallParticipant = call2.participants.get(i)) == null)) {
            call2.participants.delete(i);
            call2.sortedParticipants.remove(tLRPC$TL_groupCallParticipant);
            TLRPC$GroupCall tLRPC$GroupCall = call2.call;
            tLRPC$GroupCall.participants_count--;
        }
        if (runnable != null) {
            runnable.run();
        }
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didStartedCall, new Object[0]);
    }

    public static void onLeaveClick(Context context, Runnable runnable, boolean z) {
        VoIPService sharedInstance = VoIPService.getSharedInstance();
        if (sharedInstance != null) {
            TLRPC$Chat chat = sharedInstance.getChat();
            ChatObject.Call call2 = sharedInstance.groupCall;
            int selfId = sharedInstance.getSelfId();
            if (!ChatObject.canManageCalls(chat)) {
                processOnLeave(call2, false, selfId, runnable);
                return;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(LocaleController.getString("VoipGroupLeaveAlertTitle", NUM));
            builder.setMessage(LocaleController.getString("VoipGroupLeaveAlertText", NUM));
            sharedInstance.getAccount();
            LinearLayout linearLayout = new LinearLayout(context);
            linearLayout.setOrientation(1);
            CheckBoxCell[] checkBoxCellArr = {new CheckBoxCell(context, 1)};
            checkBoxCellArr[0].setBackgroundDrawable(Theme.getSelectorDrawable(false));
            if (z) {
                checkBoxCellArr[0].setTextColor(Theme.getColor("dialogTextBlack"));
            } else {
                checkBoxCellArr[0].setTextColor(Theme.getColor("voipgroup_actionBarItems"));
                ((CheckBoxSquare) checkBoxCellArr[0].getCheckBoxView()).setColors("voipgroup_mutedIcon", "voipgroup_listeningText", "voipgroup_nameText");
            }
            checkBoxCellArr[0].setTag(0);
            checkBoxCellArr[0].setText(LocaleController.getString("VoipGroupLeaveAlertEndChat", NUM), "", false, false);
            checkBoxCellArr[0].setPadding(LocaleController.isRTL ? AndroidUtilities.dp(16.0f) : AndroidUtilities.dp(8.0f), 0, LocaleController.isRTL ? AndroidUtilities.dp(8.0f) : AndroidUtilities.dp(16.0f), 0);
            linearLayout.addView(checkBoxCellArr[0], LayoutHelper.createLinear(-1, -2));
            checkBoxCellArr[0].setOnClickListener(new View.OnClickListener(checkBoxCellArr) {
                public final /* synthetic */ CheckBoxCell[] f$0;

                {
                    this.f$0 = r1;
                }

                public final void onClick(View view) {
                    GroupCallActivity.lambda$onLeaveClick$46(this.f$0, view);
                }
            });
            builder.setCustomViewOffset(12);
            builder.setView(linearLayout);
            builder.setDialogButtonColorKey("voipgroup_listeningText");
            builder.setPositiveButton(LocaleController.getString("VoipGroupLeave", NUM), new DialogInterface.OnClickListener(checkBoxCellArr, selfId, runnable) {
                public final /* synthetic */ CheckBoxCell[] f$1;
                public final /* synthetic */ int f$2;
                public final /* synthetic */ Runnable f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void onClick(DialogInterface dialogInterface, int i) {
                    GroupCallActivity.processOnLeave(ChatObject.Call.this, this.f$1[0].isChecked(), this.f$2, this.f$3);
                }
            });
            builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
            if (z) {
                builder.setDimEnabled(false);
            }
            AlertDialog create = builder.create();
            if (z) {
                if (Build.VERSION.SDK_INT >= 26) {
                    create.getWindow().setType(2038);
                } else {
                    create.getWindow().setType(2003);
                }
                create.getWindow().clearFlags(2);
            }
            if (!z) {
                create.setBackgroundColor(Theme.getColor("voipgroup_dialogBackground"));
            }
            create.show();
            if (!z) {
                TextView textView = (TextView) create.getButton(-1);
                if (textView != null) {
                    textView.setTextColor(Theme.getColor("voipgroup_leaveCallMenu"));
                }
                create.setTextColor(Theme.getColor("voipgroup_actionBarItems"));
            }
        }
    }

    static /* synthetic */ void lambda$onLeaveClick$46(CheckBoxCell[] checkBoxCellArr, View view) {
        Integer num = (Integer) view.getTag();
        checkBoxCellArr[num.intValue()].setChecked(!checkBoxCellArr[num.intValue()].isChecked(), true);
    }

    /* access modifiers changed from: private */
    public void processSelectedOption(TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant, int i, int i2) {
        TLObject tLObject;
        String str;
        TextView textView;
        TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant2 = tLRPC$TL_groupCallParticipant;
        int i3 = i;
        int i4 = i2;
        VoIPService sharedInstance = VoIPService.getSharedInstance();
        if (sharedInstance != null) {
            if (i3 > 0) {
                tLObject = this.accountInstance.getMessagesController().getUser(Integer.valueOf(i));
            } else {
                tLObject = this.accountInstance.getMessagesController().getChat(Integer.valueOf(-i3));
            }
            TLObject tLObject2 = tLObject;
            boolean z = false;
            if (i4 == 0 || i4 == 2 || i4 == 3) {
                if (i4 != 0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setDialogButtonColorKey("voipgroup_listeningText");
                    TextView textView2 = new TextView(getContext());
                    textView2.setTextColor(Theme.getColor("voipgroup_actionBarItems"));
                    textView2.setTextSize(1, 16.0f);
                    textView2.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
                    FrameLayout frameLayout = new FrameLayout(getContext());
                    builder.setView(frameLayout);
                    AvatarDrawable avatarDrawable = new AvatarDrawable();
                    avatarDrawable.setTextSize(AndroidUtilities.dp(12.0f));
                    BackupImageView backupImageView = new BackupImageView(getContext());
                    backupImageView.setRoundRadius(AndroidUtilities.dp(20.0f));
                    frameLayout.addView(backupImageView, LayoutHelper.createFrame(40, 40.0f, (LocaleController.isRTL ? 5 : 3) | 48, 22.0f, 5.0f, 22.0f, 0.0f));
                    avatarDrawable.setInfo(tLObject2);
                    boolean z2 = tLObject2 instanceof TLRPC$User;
                    if (z2) {
                        TLRPC$User tLRPC$User = (TLRPC$User) tLObject2;
                        backupImageView.setImage(ImageLocation.getForUserOrChat(tLRPC$User, 1), "50_50", ImageLocation.getForUserOrChat(tLRPC$User, 2), "50_50", (Drawable) avatarDrawable, (Object) tLRPC$User);
                        str = UserObject.getFirstName(tLRPC$User);
                    } else {
                        TLRPC$Chat tLRPC$Chat = (TLRPC$Chat) tLObject2;
                        backupImageView.setImage(ImageLocation.getForUserOrChat(tLRPC$Chat, 1), "50_50", ImageLocation.getForUserOrChat(tLRPC$Chat, 2), "50_50", (Drawable) avatarDrawable, (Object) tLRPC$Chat);
                        str = tLRPC$Chat.title;
                    }
                    TextView textView3 = new TextView(getContext());
                    textView3.setTextColor(Theme.getColor("voipgroup_actionBarItems"));
                    textView3.setTextSize(1, 20.0f);
                    textView3.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                    textView3.setLines(1);
                    textView3.setMaxLines(1);
                    textView3.setSingleLine(true);
                    textView3.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
                    textView3.setEllipsize(TextUtils.TruncateAt.END);
                    if (i4 == 2) {
                        textView3.setText(LocaleController.getString("VoipGroupRemoveMemberAlertTitle2", NUM));
                        textView2.setText(AndroidUtilities.replaceTags(LocaleController.formatString("VoipGroupRemoveMemberAlertText2", NUM, str, this.currentChat.title)));
                    } else {
                        textView3.setText(LocaleController.getString("VoipGroupAddMemberTitle", NUM));
                        textView2.setText(AndroidUtilities.replaceTags(LocaleController.formatString("VoipGroupAddMemberText", NUM, str, this.currentChat.title)));
                    }
                    boolean z3 = LocaleController.isRTL;
                    int i5 = (z3 ? 5 : 3) | 48;
                    int i6 = 21;
                    float f = (float) (z3 ? 21 : 76);
                    if (z3) {
                        i6 = 76;
                    }
                    frameLayout.addView(textView3, LayoutHelper.createFrame(-1, -2.0f, i5, f, 11.0f, (float) i6, 0.0f));
                    frameLayout.addView(textView2, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, 24.0f, 57.0f, 24.0f, 9.0f));
                    if (i4 == 2) {
                        builder.setPositiveButton(LocaleController.getString("VoipGroupUserRemove", NUM), new DialogInterface.OnClickListener(tLObject2) {
                            public final /* synthetic */ TLObject f$1;

                            {
                                this.f$1 = r2;
                            }

                            public final void onClick(DialogInterface dialogInterface, int i) {
                                GroupCallActivity.this.lambda$processSelectedOption$48$GroupCallActivity(this.f$1, dialogInterface, i);
                            }
                        });
                    } else if (z2) {
                        builder.setPositiveButton(LocaleController.getString("VoipGroupAdd", NUM), new DialogInterface.OnClickListener((TLRPC$User) tLObject2, i3) {
                            public final /* synthetic */ TLRPC$User f$1;
                            public final /* synthetic */ int f$2;

                            {
                                this.f$1 = r2;
                                this.f$2 = r3;
                            }

                            public final void onClick(DialogInterface dialogInterface, int i) {
                                GroupCallActivity.this.lambda$processSelectedOption$50$GroupCallActivity(this.f$1, this.f$2, dialogInterface, i);
                            }
                        });
                    }
                    builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                    AlertDialog create = builder.create();
                    create.setBackgroundColor(Theme.getColor("voipgroup_dialogBackground"));
                    create.show();
                    if (i4 == 2 && (textView = (TextView) create.getButton(-1)) != null) {
                        textView.setTextColor(Theme.getColor("voipgroup_leaveCallMenu"));
                    }
                } else if (VoIPService.getSharedInstance() != null) {
                    VoIPService.getSharedInstance().editCallMember(tLObject2, true, -1, (Boolean) null);
                    getUndoView().showWithAction(0, 30, tLObject2, (Object) null, (Runnable) null, (Runnable) null);
                }
            } else if (i4 == 6) {
                Bundle bundle = new Bundle();
                if (i3 > 0) {
                    bundle.putInt("user_id", i3);
                } else {
                    bundle.putInt("chat_id", -i3);
                }
                this.parentActivity.lambda$runLinkRequest$41(new ChatActivity(bundle));
                dismiss();
            } else if (i4 == 8) {
                BaseFragment baseFragment = this.parentActivity.getActionBarLayout().fragmentsStack.get(this.parentActivity.getActionBarLayout().fragmentsStack.size() - 1);
                if (!(baseFragment instanceof ChatActivity) || ((ChatActivity) baseFragment).getDialogId() != ((long) i3)) {
                    Bundle bundle2 = new Bundle();
                    if (i3 > 0) {
                        bundle2.putInt("user_id", i3);
                    } else {
                        bundle2.putInt("chat_id", -i3);
                    }
                    this.parentActivity.lambda$runLinkRequest$41(new ChatActivity(bundle2));
                    dismiss();
                    return;
                }
                dismiss();
            } else if (i4 == 7) {
                sharedInstance.editCallMember(tLObject2, true, -1, Boolean.FALSE);
                updateMuteButton(2, true);
            } else if (i4 == 9) {
                ImageUpdater imageUpdater = this.currentAvatarUpdater;
                if (imageUpdater == null || !imageUpdater.isUploadingImage()) {
                    ImageUpdater imageUpdater2 = new ImageUpdater(true);
                    this.currentAvatarUpdater = imageUpdater2;
                    imageUpdater2.setOpenWithFrontfaceCamera(true);
                    this.currentAvatarUpdater.setForceDarkTheme(true);
                    this.currentAvatarUpdater.setSearchAvailable(false, true);
                    this.currentAvatarUpdater.parentFragment = this.parentActivity.getActionBarLayout().getLastFragment();
                    ImageUpdater imageUpdater3 = this.currentAvatarUpdater;
                    AvatarUpdaterDelegate avatarUpdaterDelegate2 = new AvatarUpdaterDelegate(i3);
                    this.avatarUpdaterDelegate = avatarUpdaterDelegate2;
                    imageUpdater3.setDelegate(avatarUpdaterDelegate2);
                    TLRPC$User currentUser = this.accountInstance.getUserConfig().getCurrentUser();
                    ImageUpdater imageUpdater4 = this.currentAvatarUpdater;
                    TLRPC$UserProfilePhoto tLRPC$UserProfilePhoto = currentUser.photo;
                    if (!(tLRPC$UserProfilePhoto == null || tLRPC$UserProfilePhoto.photo_big == null || (tLRPC$UserProfilePhoto instanceof TLRPC$TL_userProfilePhotoEmpty))) {
                        z = true;
                    }
                    imageUpdater4.openMenu(z, new Runnable() {
                        public final void run() {
                            GroupCallActivity.this.lambda$processSelectedOption$51$GroupCallActivity();
                        }
                    }, $$Lambda$GroupCallActivity$FHK_eAhMnzsYUtqHtTxauAj9D5w.INSTANCE);
                }
            } else if (i4 == 10) {
                AlertsCreator.createChangeBioAlert(tLRPC$TL_groupCallParticipant2.about, i3, getContext(), this.currentAccount);
            } else if (i4 == 11) {
                AlertsCreator.createChangeNameAlert(i3, getContext(), this.currentAccount);
            } else if (i4 == 5) {
                sharedInstance.editCallMember(tLObject2, true, -1, (Boolean) null);
                getUndoView().showWithAction(0, 35, (Object) tLObject2);
                sharedInstance.setParticipantVolume(tLRPC$TL_groupCallParticipant2.source, 0);
            } else {
                if ((tLRPC$TL_groupCallParticipant2.flags & 128) == 0 || tLRPC$TL_groupCallParticipant2.volume != 0) {
                    sharedInstance.editCallMember(tLObject2, false, -1, (Boolean) null);
                } else {
                    tLRPC$TL_groupCallParticipant2.volume = 10000;
                    tLRPC$TL_groupCallParticipant2.volume_by_admin = false;
                    sharedInstance.editCallMember(tLObject2, false, 10000, (Boolean) null);
                }
                sharedInstance.setParticipantVolume(tLRPC$TL_groupCallParticipant2.source, ChatObject.getParticipantVolume(tLRPC$TL_groupCallParticipant));
                getUndoView().showWithAction(0, i4 == 1 ? 31 : 36, tLObject2, (Object) null, (Runnable) null, (Runnable) null);
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$processSelectedOption$48 */
    public /* synthetic */ void lambda$processSelectedOption$48$GroupCallActivity(TLObject tLObject, DialogInterface dialogInterface, int i) {
        TLObject tLObject2 = tLObject;
        if (tLObject2 instanceof TLRPC$User) {
            TLRPC$User tLRPC$User = (TLRPC$User) tLObject2;
            this.accountInstance.getMessagesController().deleteParticipantFromChat(this.currentChat.id, tLRPC$User, (TLRPC$ChatFull) null);
            getUndoView().showWithAction(0, 32, tLRPC$User, (Object) null, (Runnable) null, (Runnable) null);
            return;
        }
        TLRPC$Chat tLRPC$Chat = (TLRPC$Chat) tLObject2;
        this.accountInstance.getMessagesController().deleteParticipantFromChat(this.currentChat.id, (TLRPC$User) null, tLRPC$Chat, (TLRPC$ChatFull) null, false, false);
        getUndoView().showWithAction(0, 32, tLRPC$Chat, (Object) null, (Runnable) null, (Runnable) null);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$processSelectedOption$50 */
    public /* synthetic */ void lambda$processSelectedOption$50$GroupCallActivity(TLRPC$User tLRPC$User, int i, DialogInterface dialogInterface, int i2) {
        this.accountInstance.getMessagesController().addUserToChat(this.currentChat.id, tLRPC$User, 0, (String) null, this.parentActivity.getActionBarLayout().fragmentsStack.get(this.parentActivity.getActionBarLayout().fragmentsStack.size() - 1), new Runnable(i) {
            public final /* synthetic */ int f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                GroupCallActivity.this.lambda$null$49$GroupCallActivity(this.f$1);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$49 */
    public /* synthetic */ void lambda$null$49$GroupCallActivity(int i) {
        inviteUserToCall(i, false);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$processSelectedOption$51 */
    public /* synthetic */ void lambda$processSelectedOption$51$GroupCallActivity() {
        this.accountInstance.getMessagesController().deleteUserPhoto((TLRPC$InputPhoto) null);
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:51:0x01b9, code lost:
        if (r1.admin_rights.manage_call != false) goto L_0x01bb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:65:0x01f8, code lost:
        if ((r10 instanceof org.telegram.tgnet.TLRPC$TL_chatParticipantCreator) == false) goto L_0x01bd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:68:0x0208, code lost:
        if (r3 == (-r7.currentChat.id)) goto L_0x01bb;
     */
    /* JADX WARNING: Removed duplicated region for block: B:137:0x044c  */
    /* JADX WARNING: Removed duplicated region for block: B:152:0x04f2  */
    /* JADX WARNING: Removed duplicated region for block: B:155:0x04f9  */
    /* JADX WARNING: Removed duplicated region for block: B:156:0x0515  */
    /* JADX WARNING: Removed duplicated region for block: B:158:0x0532  */
    /* JADX WARNING: Removed duplicated region for block: B:159:0x0534  */
    /* JADX WARNING: Removed duplicated region for block: B:171:0x0583  */
    /* JADX WARNING: Removed duplicated region for block: B:172:0x05c4  */
    /* JADX WARNING: Removed duplicated region for block: B:71:0x0211  */
    /* JADX WARNING: Removed duplicated region for block: B:99:0x02e0  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean showMenuForCell(org.telegram.ui.Cells.GroupCallUserCell r29) {
        /*
            r28 = this;
            r7 = r28
            r8 = r29
            androidx.recyclerview.widget.DefaultItemAnimator r0 = r7.itemAnimator
            boolean r0 = r0.isRunning()
            r9 = 0
            if (r0 == 0) goto L_0x000e
            return r9
        L_0x000e:
            boolean r0 = r7.avatarPriviewTransitionInProgress
            r10 = 1
            if (r0 != 0) goto L_0x067c
            boolean r0 = r7.avatarsPreviewShowed
            if (r0 == 0) goto L_0x0019
            goto L_0x067c
        L_0x0019:
            org.telegram.ui.ActionBar.ActionBarPopupWindow r0 = r7.scrimPopupWindow
            r1 = 0
            if (r0 == 0) goto L_0x0024
            r0.dismiss()
            r7.scrimPopupWindow = r1
            return r9
        L_0x0024:
            android.view.ViewGroup r0 = r7.containerView
            int r0 = r0.getMeasuredHeight()
            android.view.ViewGroup r2 = r7.containerView
            int r2 = r2.getMeasuredWidth()
            if (r0 <= r2) goto L_0x003e
            boolean r0 = org.telegram.messenger.AndroidUtilities.isTablet()
            if (r0 != 0) goto L_0x003e
            boolean r0 = org.telegram.messenger.AndroidUtilities.isInMultiwindow
            if (r0 != 0) goto L_0x003e
            r11 = 1
            goto L_0x003f
        L_0x003e:
            r11 = 0
        L_0x003f:
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r12 = r29.getParticipant()
            android.graphics.Rect r0 = new android.graphics.Rect
            r0.<init>()
            org.telegram.ui.ActionBar.ActionBarPopupWindow$ActionBarPopupWindowLayout r13 = new org.telegram.ui.ActionBar.ActionBarPopupWindow$ActionBarPopupWindowLayout
            android.content.Context r2 = r28.getContext()
            r13.<init>(r2)
            r13.setBackgroundDrawable(r1)
            r13.setPadding(r9, r9, r9, r9)
            org.telegram.ui.GroupCallActivity$33 r2 = new org.telegram.ui.GroupCallActivity$33
            r2.<init>(r0)
            r13.setOnTouchListener(r2)
            org.telegram.ui.-$$Lambda$GroupCallActivity$a0vlbkqao60ZeRhDkg23YrXISzg r0 = new org.telegram.ui.-$$Lambda$GroupCallActivity$a0vlbkqao60ZeRhDkg23YrXISzg
            r0.<init>()
            r13.setDispatchKeyEventListener(r0)
            android.widget.LinearLayout r14 = new android.widget.LinearLayout
            android.content.Context r0 = r28.getContext()
            r14.<init>(r0)
            boolean r0 = r12.muted_by_you
            if (r0 != 0) goto L_0x007e
            android.widget.LinearLayout r0 = new android.widget.LinearLayout
            android.content.Context r2 = r28.getContext()
            r0.<init>(r2)
            goto L_0x007f
        L_0x007e:
            r0 = r1
        L_0x007f:
            r7.currentOptionsLayout = r14
            org.telegram.ui.GroupCallActivity$34 r15 = new org.telegram.ui.GroupCallActivity$34
            android.content.Context r2 = r28.getContext()
            r15.<init>(r7, r2, r14, r0)
            r2 = 1131413504(0x43700000, float:240.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r15.setMinimumWidth(r3)
            r15.setOrientation(r10)
            java.lang.String r3 = "voipgroup_listViewBackgroundUnscrolled"
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            java.lang.String r4 = "voipgroup_listViewBackground"
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r4)
            float r5 = r7.colorProgress
            r6 = 1065353216(0x3var_, float:1.0)
            int r3 = org.telegram.messenger.AndroidUtilities.getOffsetColor(r3, r4, r5, r6)
            r4 = 2131165954(0x7var_, float:1.794614E38)
            if (r0 == 0) goto L_0x0102
            boolean r5 = r29.isSelfUser()
            if (r5 != 0) goto L_0x0102
            boolean r5 = r12.muted_by_you
            if (r5 != 0) goto L_0x0102
            boolean r5 = r12.muted
            if (r5 == 0) goto L_0x00c3
            boolean r5 = r12.can_self_unmute
            if (r5 == 0) goto L_0x0102
        L_0x00c3:
            android.content.Context r1 = r28.getContext()
            android.content.res.Resources r1 = r1.getResources()
            android.graphics.drawable.Drawable r1 = r1.getDrawable(r4)
            android.graphics.drawable.Drawable r1 = r1.mutate()
            android.graphics.PorterDuffColorFilter r5 = new android.graphics.PorterDuffColorFilter
            android.graphics.PorterDuff$Mode r6 = android.graphics.PorterDuff.Mode.MULTIPLY
            r5.<init>(r3, r6)
            r1.setColorFilter(r5)
            r0.setBackgroundDrawable(r1)
            r16 = -2
            r17 = -2
            r18 = 0
            r19 = 0
            r20 = 0
            r21 = 0
            android.widget.LinearLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createLinear(r16, r17, r18, r19, r20, r21)
            r15.addView(r0, r1)
            org.telegram.ui.GroupCallActivity$VolumeSlider r1 = new org.telegram.ui.GroupCallActivity$VolumeSlider
            android.content.Context r5 = r28.getContext()
            r1.<init>(r7, r5, r12)
            r5 = -1
            r6 = 48
            r0.addView(r1, r5, r6)
        L_0x0102:
            r6 = r1
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r14.setMinimumWidth(r0)
            r14.setOrientation(r10)
            android.content.Context r0 = r28.getContext()
            android.content.res.Resources r0 = r0.getResources()
            android.graphics.drawable.Drawable r0 = r0.getDrawable(r4)
            android.graphics.drawable.Drawable r0 = r0.mutate()
            android.graphics.PorterDuffColorFilter r1 = new android.graphics.PorterDuffColorFilter
            android.graphics.PorterDuff$Mode r2 = android.graphics.PorterDuff.Mode.MULTIPLY
            r1.<init>(r3, r2)
            r0.setColorFilter(r1)
            r14.setBackgroundDrawable(r0)
            r16 = -2
            r17 = -2
            r18 = 0
            if (r6 == 0) goto L_0x0137
            r0 = -1056964608(0xffffffffCLASSNAME, float:-8.0)
            r19 = -1056964608(0xffffffffCLASSNAME, float:-8.0)
            goto L_0x013a
        L_0x0137:
            r0 = 0
            r19 = 0
        L_0x013a:
            r20 = 0
            r21 = 0
            android.widget.LinearLayout$LayoutParams r0 = org.telegram.ui.Components.LayoutHelper.createLinear(r16, r17, r18, r19, r20, r21)
            r15.addView(r14, r0)
            int r0 = android.os.Build.VERSION.SDK_INT
            r1 = 21
            if (r0 < r1) goto L_0x0161
            org.telegram.ui.GroupCallActivity$35 r16 = new org.telegram.ui.GroupCallActivity$35
            android.content.Context r2 = r28.getContext()
            r3 = 0
            r4 = 0
            r5 = 2131689504(0x7f0var_, float:1.9008025E38)
            r0 = r16
            r1 = r28
            r22 = r6
            r6 = r15
            r0.<init>(r1, r2, r3, r4, r5, r6)
            goto L_0x016c
        L_0x0161:
            r22 = r6
            android.widget.ScrollView r0 = new android.widget.ScrollView
            android.content.Context r1 = r28.getContext()
            r0.<init>(r1)
        L_0x016c:
            r0.setClipToPadding(r9)
            r1 = -2
            r2 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r1, r2)
            r13.addView(r0, r3)
            org.telegram.tgnet.TLRPC$Peer r3 = r12.peer
            int r3 = org.telegram.messenger.MessageObject.getPeerId(r3)
            java.util.ArrayList r4 = new java.util.ArrayList
            r5 = 2
            r4.<init>(r5)
            java.util.ArrayList r6 = new java.util.ArrayList
            r6.<init>(r5)
            java.util.ArrayList r2 = new java.util.ArrayList
            r2.<init>(r5)
            org.telegram.tgnet.TLRPC$Peer r1 = r12.peer
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC$TL_peerUser
            if (r1 == 0) goto L_0x0203
            org.telegram.tgnet.TLRPC$Chat r1 = r7.currentChat
            boolean r1 = org.telegram.messenger.ChatObject.isChannel(r1)
            if (r1 == 0) goto L_0x01bf
            org.telegram.messenger.AccountInstance r1 = r7.accountInstance
            org.telegram.messenger.MessagesController r1 = r1.getMessagesController()
            org.telegram.tgnet.TLRPC$Peer r5 = r12.peer
            int r5 = r5.user_id
            org.telegram.tgnet.TLRPC$Chat r9 = r7.currentChat
            int r9 = r9.id
            org.telegram.tgnet.TLRPC$ChannelParticipant r1 = r1.getAdminInChannel(r5, r9)
            if (r1 == 0) goto L_0x01bd
            boolean r5 = r1 instanceof org.telegram.tgnet.TLRPC$TL_channelParticipantCreator
            if (r5 != 0) goto L_0x01bb
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r1 = r1.admin_rights
            boolean r1 = r1.manage_call
            if (r1 == 0) goto L_0x01bd
        L_0x01bb:
            r1 = 1
            goto L_0x020b
        L_0x01bd:
            r1 = 0
            goto L_0x020b
        L_0x01bf:
            org.telegram.messenger.AccountInstance r1 = r7.accountInstance
            org.telegram.messenger.MessagesController r1 = r1.getMessagesController()
            org.telegram.tgnet.TLRPC$Chat r5 = r7.currentChat
            int r5 = r5.id
            org.telegram.tgnet.TLRPC$ChatFull r1 = r1.getChatFull(r5)
            if (r1 == 0) goto L_0x01bd
            org.telegram.tgnet.TLRPC$ChatParticipants r5 = r1.participants
            if (r5 == 0) goto L_0x01bd
            java.util.ArrayList<org.telegram.tgnet.TLRPC$ChatParticipant> r5 = r5.participants
            int r5 = r5.size()
            r9 = 0
        L_0x01da:
            if (r9 >= r5) goto L_0x01bd
            org.telegram.tgnet.TLRPC$ChatParticipants r10 = r1.participants
            java.util.ArrayList<org.telegram.tgnet.TLRPC$ChatParticipant> r10 = r10.participants
            java.lang.Object r10 = r10.get(r9)
            org.telegram.tgnet.TLRPC$ChatParticipant r10 = (org.telegram.tgnet.TLRPC$ChatParticipant) r10
            r21 = r1
            int r1 = r10.user_id
            r23 = r5
            org.telegram.tgnet.TLRPC$Peer r5 = r12.peer
            int r5 = r5.user_id
            if (r1 != r5) goto L_0x01fb
            boolean r1 = r10 instanceof org.telegram.tgnet.TLRPC$TL_chatParticipantAdmin
            if (r1 != 0) goto L_0x01bb
            boolean r1 = r10 instanceof org.telegram.tgnet.TLRPC$TL_chatParticipantCreator
            if (r1 == 0) goto L_0x01bd
            goto L_0x01bb
        L_0x01fb:
            int r9 = r9 + 1
            r1 = r21
            r5 = r23
            r10 = 1
            goto L_0x01da
        L_0x0203:
            org.telegram.tgnet.TLRPC$Chat r1 = r7.currentChat
            int r1 = r1.id
            int r1 = -r1
            if (r3 != r1) goto L_0x01bd
            goto L_0x01bb
        L_0x020b:
            boolean r5 = r29.isSelfUser()
            if (r5 == 0) goto L_0x02e0
            boolean r1 = r29.isHandRaised()
            if (r1 == 0) goto L_0x0235
            r1 = 2131628032(0x7f0e1000, float:1.8883345E38)
            java.lang.String r5 = "VoipGroupCancelRaiseHand"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r5, r1)
            r4.add(r1)
            r1 = 2131165752(0x7var_, float:1.794573E38)
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            r6.add(r1)
            r1 = 7
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            r2.add(r1)
        L_0x0235:
            boolean r1 = r29.hasAvatarSet()
            if (r1 == 0) goto L_0x0241
            r1 = 2131627992(0x7f0e0fd8, float:1.8883264E38)
            java.lang.String r5 = "VoipAddPhoto"
            goto L_0x0246
        L_0x0241:
            r1 = 2131628163(0x7f0e1083, float:1.888361E38)
            java.lang.String r5 = "VoipSetNewPhoto"
        L_0x0246:
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r5, r1)
            r4.add(r1)
            r1 = 2131165714(0x7var_, float:1.7945653E38)
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            r6.add(r1)
            r1 = 9
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            r2.add(r1)
            if (r3 <= 0) goto L_0x027d
            java.lang.String r1 = r12.about
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 == 0) goto L_0x0270
            r1 = 2131627990(0x7f0e0fd6, float:1.888326E38)
            java.lang.String r5 = "VoipAddBio"
            goto L_0x0275
        L_0x0270:
            r1 = 2131628011(0x7f0e0feb, float:1.8883303E38)
            java.lang.String r5 = "VoipEditBio"
        L_0x0275:
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r5, r1)
            r4.add(r1)
            goto L_0x0297
        L_0x027d:
            java.lang.String r1 = r12.about
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 == 0) goto L_0x028b
            r1 = 2131627991(0x7f0e0fd7, float:1.8883262E38)
            java.lang.String r5 = "VoipAddDescription"
            goto L_0x0290
        L_0x028b:
            r1 = 2131628012(0x7f0e0fec, float:1.8883305E38)
            java.lang.String r5 = "VoipEditDescription"
        L_0x0290:
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r5, r1)
            r4.add(r1)
        L_0x0297:
            java.lang.String r1 = r12.about
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 == 0) goto L_0x02a3
            r1 = 2131165710(0x7var_e, float:1.7945645E38)
            goto L_0x02a6
        L_0x02a3:
            r1 = 2131165719(0x7var_, float:1.7945663E38)
        L_0x02a6:
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            r6.add(r1)
            r1 = 10
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            r2.add(r1)
            if (r3 <= 0) goto L_0x02be
            r1 = 2131628013(0x7f0e0fed, float:1.8883307E38)
            java.lang.String r5 = "VoipEditName"
            goto L_0x02c3
        L_0x02be:
            r1 = 2131628014(0x7f0e0fee, float:1.8883309E38)
            java.lang.String r5 = "VoipEditTitle"
        L_0x02c3:
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r5, r1)
            r4.add(r1)
            r1 = 2131165742(0x7var_e, float:1.794571E38)
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            r6.add(r1)
            r1 = 11
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            r2.add(r1)
            r5 = r11
            goto L_0x0445
        L_0x02e0:
            org.telegram.tgnet.TLRPC$Chat r5 = r7.currentChat
            boolean r5 = org.telegram.messenger.ChatObject.canManageCalls(r5)
            java.lang.String r9 = "VoipGroupOpenChannel"
            r23 = 6
            r24 = 2131165844(0x7var_, float:1.7945917E38)
            r25 = 2131165842(0x7var_, float:1.7945913E38)
            if (r5 == 0) goto L_0x03c3
            if (r1 == 0) goto L_0x02fb
            boolean r5 = r12.muted
            if (r5 != 0) goto L_0x02f9
            goto L_0x02fb
        L_0x02f9:
            r5 = r11
            goto L_0x0350
        L_0x02fb:
            boolean r5 = r12.muted
            if (r5 == 0) goto L_0x0334
            boolean r5 = r12.can_self_unmute
            if (r5 == 0) goto L_0x0304
            goto L_0x0334
        L_0x0304:
            r5 = 2131628026(0x7f0e0ffa, float:1.8883333E38)
            java.lang.String r10 = "VoipGroupAllowToSpeak"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r10, r5)
            r4.add(r5)
            r5 = r11
            long r10 = r12.raise_hand_rating
            r26 = 0
            int r25 = (r10 > r26 ? 1 : (r10 == r26 ? 0 : -1))
            if (r25 == 0) goto L_0x0324
            r10 = 2131165715(0x7var_, float:1.7945655E38)
            java.lang.Integer r10 = java.lang.Integer.valueOf(r10)
            r6.add(r10)
            goto L_0x032b
        L_0x0324:
            java.lang.Integer r10 = java.lang.Integer.valueOf(r24)
            r6.add(r10)
        L_0x032b:
            r10 = 1
            java.lang.Integer r11 = java.lang.Integer.valueOf(r10)
            r2.add(r11)
            goto L_0x0350
        L_0x0334:
            r5 = r11
            r10 = 2131628066(0x7f0e1022, float:1.8883414E38)
            java.lang.String r11 = "VoipGroupMute"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r11, r10)
            r4.add(r10)
            java.lang.Integer r10 = java.lang.Integer.valueOf(r25)
            r6.add(r10)
            r10 = 0
            java.lang.Integer r11 = java.lang.Integer.valueOf(r10)
            r2.add(r11)
        L_0x0350:
            org.telegram.tgnet.TLRPC$Peer r10 = r12.peer
            int r10 = r10.channel_id
            if (r10 == 0) goto L_0x037c
            int r11 = r7.currentAccount
            boolean r10 = org.telegram.messenger.ChatObject.isMegagroup(r11, r10)
            if (r10 != 0) goto L_0x037c
            r10 = 2131628074(0x7f0e102a, float:1.888343E38)
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r9, r10)
            r4.add(r9)
            r9 = 2131165725(0x7var_d, float:1.7945675E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            r6.add(r9)
            r9 = 8
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            r2.add(r9)
            goto L_0x0399
        L_0x037c:
            r9 = 2131628077(0x7f0e102d, float:1.8883436E38)
            java.lang.String r10 = "VoipGroupOpenProfile"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r10, r9)
            r4.add(r9)
            r9 = 2131165780(0x7var_, float:1.7945787E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            r6.add(r9)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r23)
            r2.add(r9)
        L_0x0399:
            if (r1 != 0) goto L_0x0445
            org.telegram.tgnet.TLRPC$Chat r1 = r7.currentChat
            boolean r1 = org.telegram.messenger.ChatObject.canBlockUsers(r1)
            if (r1 == 0) goto L_0x0445
            r1 = 2131628116(0x7f0e1054, float:1.8883516E38)
            java.lang.String r9 = "VoipGroupUserRemove"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r9, r1)
            r4.add(r1)
            r1 = 2131165721(0x7var_, float:1.7945667E38)
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            r6.add(r1)
            r1 = 2
            java.lang.Integer r9 = java.lang.Integer.valueOf(r1)
            r2.add(r9)
            goto L_0x0445
        L_0x03c3:
            r5 = r11
            boolean r1 = r12.muted_by_you
            if (r1 == 0) goto L_0x03e4
            r1 = 2131628110(0x7f0e104e, float:1.8883503E38)
            java.lang.String r10 = "VoipGroupUnmuteForMe"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r10, r1)
            r4.add(r1)
            java.lang.Integer r1 = java.lang.Integer.valueOf(r24)
            r6.add(r1)
            r1 = 4
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            r2.add(r1)
            goto L_0x03ff
        L_0x03e4:
            r1 = 2131628067(0x7f0e1023, float:1.8883416E38)
            java.lang.String r10 = "VoipGroupMuteForMe"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r10, r1)
            r4.add(r1)
            java.lang.Integer r1 = java.lang.Integer.valueOf(r25)
            r6.add(r1)
            r1 = 5
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            r2.add(r1)
        L_0x03ff:
            org.telegram.tgnet.TLRPC$Peer r1 = r12.peer
            int r1 = r1.channel_id
            r10 = 2131165777(0x7var_, float:1.794578E38)
            if (r1 == 0) goto L_0x042b
            int r11 = r7.currentAccount
            boolean r1 = org.telegram.messenger.ChatObject.isMegagroup(r11, r1)
            if (r1 != 0) goto L_0x042b
            r1 = 2131628074(0x7f0e102a, float:1.888343E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r9, r1)
            r4.add(r1)
            java.lang.Integer r1 = java.lang.Integer.valueOf(r10)
            r6.add(r1)
            r1 = 8
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            r2.add(r1)
            goto L_0x0445
        L_0x042b:
            r1 = 2131628075(0x7f0e102b, float:1.8883432E38)
            java.lang.String r9 = "VoipGroupOpenChat"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r9, r1)
            r4.add(r1)
            java.lang.Integer r1 = java.lang.Integer.valueOf(r10)
            r6.add(r1)
            java.lang.Integer r1 = java.lang.Integer.valueOf(r23)
            r2.add(r1)
        L_0x0445:
            int r1 = r4.size()
            r9 = 0
        L_0x044a:
            if (r9 >= r1) goto L_0x04c5
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r10 = new org.telegram.ui.ActionBar.ActionBarMenuSubItem
            android.content.Context r11 = r28.getContext()
            r21 = r5
            if (r9 != 0) goto L_0x0458
            r5 = 1
            goto L_0x0459
        L_0x0458:
            r5 = 0
        L_0x0459:
            r23 = r3
            int r3 = r1 + -1
            if (r9 != r3) goto L_0x0461
            r3 = 1
            goto L_0x0462
        L_0x0461:
            r3 = 0
        L_0x0462:
            r10.<init>(r11, r5, r3)
            java.lang.Object r3 = r2.get(r9)
            java.lang.Integer r3 = (java.lang.Integer) r3
            int r3 = r3.intValue()
            r5 = 2
            if (r3 == r5) goto L_0x0481
            java.lang.String r3 = "voipgroup_actionBarItems"
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            r10.setColors(r5, r3)
            goto L_0x048f
        L_0x0481:
            java.lang.String r3 = "voipgroup_leaveCallMenu"
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            r10.setColors(r5, r3)
        L_0x048f:
            java.lang.String r3 = "voipgroup_listSelector"
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            r10.setSelectorColor(r3)
            java.lang.Object r3 = r4.get(r9)
            java.lang.CharSequence r3 = (java.lang.CharSequence) r3
            java.lang.Object r5 = r6.get(r9)
            java.lang.Integer r5 = (java.lang.Integer) r5
            int r5 = r5.intValue()
            r10.setTextAndIcon(r3, r5)
            r14.addView(r10)
            java.lang.Object r3 = r2.get(r9)
            r10.setTag(r3)
            org.telegram.ui.-$$Lambda$GroupCallActivity$7FXjTASWqfHO2HFcqsdBKNRW1VU r3 = new org.telegram.ui.-$$Lambda$GroupCallActivity$7FXjTASWqfHO2HFcqsdBKNRW1VU
            r3.<init>(r9, r2, r12)
            r10.setOnClickListener(r3)
            int r9 = r9 + 1
            r5 = r21
            r3 = r23
            goto L_0x044a
        L_0x04c5:
            r23 = r3
            r21 = r5
            r1 = 51
            r2 = -2
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createScroll(r2, r2, r1)
            r0.addView(r15, r3)
            org.telegram.ui.Components.RecyclerListView r0 = r7.listView
            r0.stopScroll()
            org.telegram.ui.Components.FillLastLinearLayoutManager r0 = r7.layoutManager
            r2 = 0
            r0.setCanScrollVertically(r2)
            r7.scrimView = r8
            r0 = 1
            r8.setAboutVisible(r0)
            android.view.ViewGroup r0 = r7.containerView
            r0.invalidate()
            org.telegram.ui.Components.RecyclerListView r0 = r7.listView
            r0.invalidate()
            android.animation.AnimatorSet r0 = r7.scrimAnimatorSet
            if (r0 == 0) goto L_0x04f5
            r0.cancel()
        L_0x04f5:
            r7.scrimPopupLayout = r13
            if (r23 <= 0) goto L_0x0515
            org.telegram.messenger.AccountInstance r0 = r7.accountInstance
            org.telegram.messenger.MessagesController r0 = r0.getMessagesController()
            java.lang.Integer r2 = java.lang.Integer.valueOf(r23)
            org.telegram.tgnet.TLRPC$User r0 = r0.getUser(r2)
            r2 = 0
            org.telegram.messenger.ImageLocation r3 = org.telegram.messenger.ImageLocation.getForUserOrChat(r0, r2)
            r4 = 1
            org.telegram.messenger.ImageLocation r0 = org.telegram.messenger.ImageLocation.getForUserOrChat(r0, r4)
            r5 = r3
            r3 = r23
            goto L_0x0530
        L_0x0515:
            r2 = 0
            r4 = 1
            org.telegram.messenger.AccountInstance r0 = r7.accountInstance
            org.telegram.messenger.MessagesController r0 = r0.getMessagesController()
            r3 = r23
            int r5 = -r3
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            org.telegram.tgnet.TLRPC$Chat r0 = r0.getChat(r5)
            org.telegram.messenger.ImageLocation r5 = org.telegram.messenger.ImageLocation.getForUserOrChat(r0, r2)
            org.telegram.messenger.ImageLocation r0 = org.telegram.messenger.ImageLocation.getForUserOrChat(r0, r4)
        L_0x0530:
            if (r5 != 0) goto L_0x0534
            r11 = 0
            goto L_0x057d
        L_0x0534:
            org.telegram.ui.Components.ProfileGalleryView r2 = r7.avatarsViewPager
            org.telegram.ui.Cells.GroupCallUserCell r4 = r7.scrimView
            org.telegram.ui.Components.BackupImageView r4 = r4.getAvatarImageView()
            r2.setParentAvatarImage(r4)
            org.telegram.ui.Components.ProfileGalleryView r2 = r7.avatarsViewPager
            long r9 = (long) r3
            r2.setData(r9)
            org.telegram.ui.Components.ProfileGalleryView r2 = r7.avatarsViewPager
            r4 = 1
            r2.setCreateThumbFromParent(r4)
            org.telegram.ui.Components.ProfileGalleryView r2 = r7.avatarsViewPager
            r2.initIfEmpty(r5, r0)
            org.telegram.tgnet.TLRPC$Peer r0 = r7.selfPeer
            int r0 = org.telegram.messenger.MessageObject.getPeerId(r0)
            if (r0 != r3) goto L_0x057b
            org.telegram.ui.Components.ImageUpdater r0 = r7.currentAvatarUpdater
            if (r0 == 0) goto L_0x057b
            org.telegram.ui.GroupCallActivity$AvatarUpdaterDelegate r0 = r7.avatarUpdaterDelegate
            if (r0 == 0) goto L_0x057b
            org.telegram.tgnet.TLRPC$FileLocation r0 = r0.avatar
            if (r0 == 0) goto L_0x057b
            org.telegram.ui.Components.ProfileGalleryView r0 = r7.avatarsViewPager
            org.telegram.ui.GroupCallActivity$AvatarUpdaterDelegate r2 = r7.avatarUpdaterDelegate
            org.telegram.messenger.ImageLocation r2 = r2.uploadingImageLocation
            org.telegram.ui.GroupCallActivity$AvatarUpdaterDelegate r3 = r7.avatarUpdaterDelegate
            org.telegram.tgnet.TLRPC$FileLocation r3 = r3.avatar
            org.telegram.messenger.ImageLocation r3 = org.telegram.messenger.ImageLocation.getForLocal(r3)
            r0.addUploadingImage(r2, r3)
        L_0x057b:
            r11 = r21
        L_0x057d:
            r0 = -2147483648(0xfffffffvar_, float:-0.0)
            r2 = 1148846080(0x447a0000, float:1000.0)
            if (r11 == 0) goto L_0x05c4
            r3 = 1
            r7.avatarsPreviewShowed = r3
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r1 = android.view.View.MeasureSpec.makeMeasureSpec(r1, r0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r0 = android.view.View.MeasureSpec.makeMeasureSpec(r2, r0)
            r13.measure(r1, r0)
            android.view.ViewGroup r0 = r7.containerView
            android.view.View r1 = r7.scrimPopupLayout
            r2 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r4 = -2
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r4, r2)
            r0.addView(r1, r2)
            r28.prepareBlurBitmap()
            r7.avatarPriviewTransitionInProgress = r3
            android.widget.FrameLayout r0 = r7.avatarPreviewContainer
            r3 = 0
            r0.setVisibility(r3)
            android.widget.FrameLayout r0 = r7.avatarPreviewContainer
            android.view.ViewTreeObserver r0 = r0.getViewTreeObserver()
            org.telegram.ui.GroupCallActivity$36 r1 = new org.telegram.ui.GroupCallActivity$36
            r2 = r22
            r1.<init>(r2, r8)
            r0.addOnPreDrawListener(r1)
            goto L_0x067a
        L_0x05c4:
            r3 = 0
            r7.avatarsPreviewShowed = r3
            org.telegram.ui.GroupCallActivity$37 r3 = new org.telegram.ui.GroupCallActivity$37
            r4 = -2
            r3.<init>(r13, r4, r4)
            r7.scrimPopupWindow = r3
            r4 = 1
            r3.setPauseNotifications(r4)
            org.telegram.ui.ActionBar.ActionBarPopupWindow r3 = r7.scrimPopupWindow
            r5 = 220(0xdc, float:3.08E-43)
            r3.setDismissAnimationDuration(r5)
            org.telegram.ui.ActionBar.ActionBarPopupWindow r3 = r7.scrimPopupWindow
            r3.setOutsideTouchable(r4)
            org.telegram.ui.ActionBar.ActionBarPopupWindow r3 = r7.scrimPopupWindow
            r3.setClippingEnabled(r4)
            org.telegram.ui.ActionBar.ActionBarPopupWindow r3 = r7.scrimPopupWindow
            r5 = 2131689477(0x7f0var_, float:1.900797E38)
            r3.setAnimationStyle(r5)
            org.telegram.ui.ActionBar.ActionBarPopupWindow r3 = r7.scrimPopupWindow
            r3.setFocusable(r4)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r3 = android.view.View.MeasureSpec.makeMeasureSpec(r3, r0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r0 = android.view.View.MeasureSpec.makeMeasureSpec(r2, r0)
            r13.measure(r3, r0)
            org.telegram.ui.ActionBar.ActionBarPopupWindow r0 = r7.scrimPopupWindow
            r2 = 2
            r0.setInputMethodMode(r2)
            org.telegram.ui.ActionBar.ActionBarPopupWindow r0 = r7.scrimPopupWindow
            r2 = 0
            r0.setSoftInputMode(r2)
            org.telegram.ui.ActionBar.ActionBarPopupWindow r0 = r7.scrimPopupWindow
            android.view.View r0 = r0.getContentView()
            r2 = 1
            r0.setFocusableInTouchMode(r2)
            r0 = 1096810496(0x41600000, float:14.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            org.telegram.ui.Components.RecyclerListView r2 = r7.listView
            int r2 = r2.getMeasuredWidth()
            int r0 = r0 + r2
            r2 = 1090519040(0x41000000, float:8.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r0 = r0 + r2
            int r2 = r13.getMeasuredWidth()
            int r0 = r0 - r2
            org.telegram.ui.Components.RecyclerListView r2 = r7.listView
            float r2 = r2.getY()
            float r3 = r29.getY()
            float r2 = r2 + r3
            int r3 = r29.getClipHeight()
            float r3 = (float) r3
            float r2 = r2 + r3
            int r2 = (int) r2
            org.telegram.ui.ActionBar.ActionBarPopupWindow r3 = r7.scrimPopupWindow
            org.telegram.ui.Components.RecyclerListView r4 = r7.listView
            r3.showAtLocation(r4, r1, r0, r2)
            android.animation.AnimatorSet r0 = new android.animation.AnimatorSet
            r0.<init>()
            r7.scrimAnimatorSet = r0
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            android.graphics.Paint r1 = r7.scrimPaint
            android.util.Property<android.graphics.Paint, java.lang.Integer> r2 = org.telegram.ui.Components.AnimationProperties.PAINT_ALPHA
            r3 = 2
            int[] r3 = new int[r3]
            r3 = {0, 100} // fill-array
            android.animation.ObjectAnimator r1 = android.animation.ObjectAnimator.ofInt(r1, r2, r3)
            r0.add(r1)
            android.animation.AnimatorSet r1 = r7.scrimAnimatorSet
            r1.playTogether(r0)
            android.animation.AnimatorSet r0 = r7.scrimAnimatorSet
            r1 = 150(0x96, double:7.4E-322)
            r0.setDuration(r1)
            android.animation.AnimatorSet r0 = r7.scrimAnimatorSet
            r0.start()
        L_0x067a:
            r0 = 1
            return r0
        L_0x067c:
            r0 = 1
            r7.dismissAvatarPreview(r0)
            r0 = 0
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.GroupCallActivity.showMenuForCell(org.telegram.ui.Cells.GroupCallUserCell):boolean");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$showMenuForCell$53 */
    public /* synthetic */ void lambda$showMenuForCell$53$GroupCallActivity(KeyEvent keyEvent) {
        ActionBarPopupWindow actionBarPopupWindow;
        if (keyEvent.getKeyCode() == 4 && keyEvent.getRepeatCount() == 0 && (actionBarPopupWindow = this.scrimPopupWindow) != null && actionBarPopupWindow.isShowing()) {
            this.scrimPopupWindow.dismiss();
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$showMenuForCell$54 */
    public /* synthetic */ void lambda$showMenuForCell$54$GroupCallActivity(int i, ArrayList arrayList, TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant, View view) {
        if (i < arrayList.size()) {
            TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant2 = this.call.participants.get(MessageObject.getPeerId(tLRPC$TL_groupCallParticipant.peer));
            if (tLRPC$TL_groupCallParticipant2 != null) {
                tLRPC$TL_groupCallParticipant = tLRPC$TL_groupCallParticipant2;
            }
            processSelectedOption(tLRPC$TL_groupCallParticipant, MessageObject.getPeerId(tLRPC$TL_groupCallParticipant.peer), ((Integer) arrayList.get(i)).intValue());
            ActionBarPopupWindow actionBarPopupWindow = this.scrimPopupWindow;
            if (actionBarPopupWindow != null) {
                actionBarPopupWindow.dismiss();
            } else if (((Integer) arrayList.get(i)).intValue() != 9 && ((Integer) arrayList.get(i)).intValue() != 10 && ((Integer) arrayList.get(i)).intValue() != 11) {
                dismissAvatarPreview(true);
            }
        }
    }

    /* access modifiers changed from: private */
    public void runAvatarPreviewTransition(final boolean z, GroupCallUserCell groupCallUserCell) {
        float x = ((groupCallUserCell.getAvatarImageView().getX() + groupCallUserCell.getX()) + this.listView.getX()) - ((float) this.avatarPreviewContainer.getLeft());
        float y = ((groupCallUserCell.getAvatarImageView().getY() + groupCallUserCell.getY()) + this.listView.getY()) - ((float) this.avatarPreviewContainer.getTop());
        float measuredHeight = ((float) groupCallUserCell.getAvatarImageView().getMeasuredHeight()) / ((float) this.avatarPreviewContainer.getMeasuredWidth());
        float f = 0.0f;
        if (z) {
            this.avatarPreviewContainer.setScaleX(measuredHeight);
            this.avatarPreviewContainer.setScaleY(measuredHeight);
            this.avatarPreviewContainer.setTranslationX(x);
            this.avatarPreviewContainer.setTranslationY(y);
            this.avatarPagerIndicator.setAlpha(0.0f);
        }
        int measuredHeight2 = (int) (((float) (groupCallUserCell.getAvatarImageView().getMeasuredHeight() >> 1)) / measuredHeight);
        this.avatarsViewPager.setRoundRadius(measuredHeight2, measuredHeight2);
        if (z) {
            this.blurredView.setAlpha(0.0f);
        }
        this.blurredView.animate().alpha(z ? 1.0f : 0.0f).setDuration(220).start();
        ViewPropertyAnimator scaleX = this.avatarPreviewContainer.animate().scaleX(z ? 1.0f : measuredHeight);
        if (z) {
            measuredHeight = 1.0f;
        }
        ViewPropertyAnimator scaleY = scaleX.scaleY(measuredHeight);
        if (z) {
            y = 0.0f;
        }
        ViewPropertyAnimator translationY = scaleY.translationY(y);
        if (z) {
            x = 0.0f;
        }
        ViewPropertyAnimator translationX = translationY.translationX(x);
        CubicBezierInterpolator cubicBezierInterpolator = CubicBezierInterpolator.DEFAULT;
        translationX.setInterpolator(cubicBezierInterpolator).setDuration(220).start();
        this.avatarPagerIndicator.animate().alpha(z ? 1.0f : 0.0f).setDuration(220).start();
        float[] fArr = new float[2];
        fArr[0] = z ? 0.0f : 1.0f;
        if (z) {
            f = 1.0f;
        }
        fArr[1] = f;
        ValueAnimator ofFloat = ValueAnimator.ofFloat(fArr);
        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(measuredHeight2) {
            public final /* synthetic */ int f$1;

            {
                this.f$1 = r2;
            }

            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                GroupCallActivity.this.lambda$runAvatarPreviewTransition$55$GroupCallActivity(this.f$1, valueAnimator);
            }
        });
        this.popupAnimationIndex = this.accountInstance.getNotificationCenter().setAnimationInProgress(this.popupAnimationIndex, new int[]{NotificationCenter.dialogPhotosLoaded, NotificationCenter.fileDidLoad, NotificationCenter.messagesDidLoad});
        ofFloat.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                GroupCallActivity.this.accountInstance.getNotificationCenter().onAnimationFinish(GroupCallActivity.this.popupAnimationIndex);
                boolean unused = GroupCallActivity.this.avatarPriviewTransitionInProgress = false;
                float unused2 = GroupCallActivity.this.progressToAvatarPreview = z ? 1.0f : 0.0f;
                if (!z) {
                    if (GroupCallActivity.this.scrimView != null) {
                        GroupCallActivity.this.scrimView.setProgressToAvatarPreview(0.0f);
                        GroupCallActivity.this.scrimView.setAboutVisible(false);
                        GroupCallUserCell unused3 = GroupCallActivity.this.scrimView = null;
                    }
                    if (GroupCallActivity.this.scrimPopupLayout.getParent() != null) {
                        GroupCallActivity.this.containerView.removeView(GroupCallActivity.this.scrimPopupLayout);
                    }
                    View unused4 = GroupCallActivity.this.scrimPopupLayout = null;
                    GroupCallActivity.this.avatarPreviewContainer.setVisibility(8);
                    boolean unused5 = GroupCallActivity.this.avatarsPreviewShowed = false;
                    GroupCallActivity.this.layoutManager.setCanScrollVertically(true);
                    GroupCallActivity.this.blurredView.setVisibility(8);
                    if (GroupCallActivity.this.delayedGroupCallUpdated) {
                        boolean unused6 = GroupCallActivity.this.delayedGroupCallUpdated = false;
                        GroupCallActivity.this.applyCallParticipantUpdates();
                    }
                } else {
                    GroupCallActivity.this.avatarPreviewContainer.animate().cancel();
                    GroupCallActivity.this.avatarPreviewContainer.setAlpha(1.0f);
                    GroupCallActivity.this.avatarPreviewContainer.setScaleX(1.0f);
                    GroupCallActivity.this.avatarPreviewContainer.setScaleY(1.0f);
                    GroupCallActivity.this.avatarPreviewContainer.setTranslationX(1.0f);
                    GroupCallActivity.this.avatarPreviewContainer.setTranslationY(1.0f);
                }
                GroupCallActivity.this.checkContentOverlayed();
                GroupCallActivity.this.containerView.invalidate();
                GroupCallActivity.this.avatarsViewPager.invalidate();
                GroupCallActivity.this.listView.invalidate();
            }
        });
        ofFloat.setInterpolator(cubicBezierInterpolator);
        ofFloat.setDuration(220);
        ofFloat.start();
        checkContentOverlayed();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$runAvatarPreviewTransition$55 */
    public /* synthetic */ void lambda$runAvatarPreviewTransition$55$GroupCallActivity(int i, ValueAnimator valueAnimator) {
        this.progressToAvatarPreview = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.avatarPreviewContainer.invalidate();
        this.containerView.invalidate();
        ProfileGalleryView profileGalleryView = this.avatarsViewPager;
        float f = (float) i;
        float f2 = this.progressToAvatarPreview;
        profileGalleryView.setRoundRadius((int) ((1.0f - f2) * f), (int) (f * (1.0f - f2)));
    }

    /* access modifiers changed from: private */
    public void dismissAvatarPreview(boolean z) {
        if (!this.avatarPriviewTransitionInProgress && this.avatarsPreviewShowed) {
            if (z) {
                this.avatarPriviewTransitionInProgress = true;
                runAvatarPreviewTransition(false, this.scrimView);
                return;
            }
            GroupCallUserCell groupCallUserCell = this.scrimView;
            if (groupCallUserCell != null) {
                groupCallUserCell.setProgressToAvatarPreview(0.0f);
                this.scrimView.setAboutVisible(false);
                this.scrimView = null;
            }
            this.containerView.removeView(this.scrimPopupLayout);
            this.scrimPopupLayout = null;
            this.avatarPreviewContainer.setVisibility(8);
            this.containerView.invalidate();
            this.avatarsPreviewShowed = false;
            this.layoutManager.setCanScrollVertically(true);
            this.listView.invalidate();
            this.blurredView.setVisibility(8);
            if (this.delayedGroupCallUpdated) {
                this.delayedGroupCallUpdated = false;
                applyCallParticipantUpdates();
            }
            checkContentOverlayed();
        }
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        /* access modifiers changed from: private */
        public int addMemberRow;
        private boolean hasSelfUser;
        /* access modifiers changed from: private */
        public int invitedEndRow;
        /* access modifiers changed from: private */
        public int invitedStartRow;
        private int lastRow;
        private Context mContext;
        /* access modifiers changed from: private */
        public int rowsCount;
        /* access modifiers changed from: private */
        public int usersEndRow;
        /* access modifiers changed from: private */
        public int usersStartRow;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean addSelfToCounter() {
            if (this.hasSelfUser || VoIPService.getSharedInstance() == null) {
                return false;
            }
            return !VoIPService.getSharedInstance().isJoined();
        }

        public int getItemCount() {
            return this.rowsCount;
        }

        /* access modifiers changed from: private */
        /* JADX WARNING: Code restructure failed: missing block: B:17:0x004e, code lost:
            if (android.text.TextUtils.isEmpty(r1.username) == false) goto L_0x0054;
         */
        /* JADX WARNING: Removed duplicated region for block: B:22:0x0070  */
        /* JADX WARNING: Removed duplicated region for block: B:25:0x0092  */
        /* JADX WARNING: Removed duplicated region for block: B:26:0x0097  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void updateRows() {
            /*
                r4 = this;
                org.telegram.ui.GroupCallActivity r0 = org.telegram.ui.GroupCallActivity.this
                org.telegram.messenger.ChatObject$Call r0 = r0.call
                if (r0 == 0) goto L_0x00b2
                boolean r0 = r0.isScheduled()
                if (r0 != 0) goto L_0x00b2
                org.telegram.ui.GroupCallActivity r0 = org.telegram.ui.GroupCallActivity.this
                boolean r0 = r0.delayedGroupCallUpdated
                if (r0 == 0) goto L_0x0016
                goto L_0x00b2
            L_0x0016:
                r0 = 0
                r4.rowsCount = r0
                org.telegram.ui.GroupCallActivity r1 = org.telegram.ui.GroupCallActivity.this
                org.telegram.tgnet.TLRPC$Chat r1 = r1.currentChat
                boolean r1 = org.telegram.messenger.ChatObject.isChannel(r1)
                r2 = -1
                if (r1 == 0) goto L_0x002c
                org.telegram.ui.GroupCallActivity r1 = org.telegram.ui.GroupCallActivity.this
                org.telegram.tgnet.TLRPC$Chat r1 = r1.currentChat
                boolean r1 = r1.megagroup
                if (r1 == 0) goto L_0x0036
            L_0x002c:
                org.telegram.ui.GroupCallActivity r1 = org.telegram.ui.GroupCallActivity.this
                org.telegram.tgnet.TLRPC$Chat r1 = r1.currentChat
                boolean r1 = org.telegram.messenger.ChatObject.canWriteToChat(r1)
                if (r1 != 0) goto L_0x0054
            L_0x0036:
                org.telegram.ui.GroupCallActivity r1 = org.telegram.ui.GroupCallActivity.this
                org.telegram.tgnet.TLRPC$Chat r1 = r1.currentChat
                boolean r1 = org.telegram.messenger.ChatObject.isChannel(r1)
                if (r1 == 0) goto L_0x0051
                org.telegram.ui.GroupCallActivity r1 = org.telegram.ui.GroupCallActivity.this
                org.telegram.tgnet.TLRPC$Chat r1 = r1.currentChat
                boolean r3 = r1.megagroup
                if (r3 != 0) goto L_0x0051
                java.lang.String r1 = r1.username
                boolean r1 = android.text.TextUtils.isEmpty(r1)
                if (r1 != 0) goto L_0x0051
                goto L_0x0054
            L_0x0051:
                r4.addMemberRow = r2
                goto L_0x005c
            L_0x0054:
                int r1 = r4.rowsCount
                int r3 = r1 + 1
                r4.rowsCount = r3
                r4.addMemberRow = r1
            L_0x005c:
                org.telegram.ui.GroupCallActivity r1 = org.telegram.ui.GroupCallActivity.this
                org.telegram.messenger.ChatObject$Call r3 = r1.call
                android.util.SparseArray<org.telegram.tgnet.TLRPC$TL_groupCallParticipant> r3 = r3.participants
                org.telegram.tgnet.TLRPC$Peer r1 = r1.selfPeer
                int r1 = org.telegram.messenger.MessageObject.getPeerId(r1)
                int r1 = r3.indexOfKey(r1)
                if (r1 < 0) goto L_0x0071
                r0 = 1
            L_0x0071:
                r4.hasSelfUser = r0
                int r0 = r4.rowsCount
                r4.usersStartRow = r0
                org.telegram.ui.GroupCallActivity r1 = org.telegram.ui.GroupCallActivity.this
                org.telegram.messenger.ChatObject$Call r1 = r1.call
                java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_groupCallParticipant> r1 = r1.sortedParticipants
                int r1 = r1.size()
                int r0 = r0 + r1
                r4.rowsCount = r0
                r4.usersEndRow = r0
                org.telegram.ui.GroupCallActivity r0 = org.telegram.ui.GroupCallActivity.this
                org.telegram.messenger.ChatObject$Call r0 = r0.call
                java.util.ArrayList<java.lang.Integer> r0 = r0.invitedUsers
                boolean r0 = r0.isEmpty()
                if (r0 == 0) goto L_0x0097
                r4.invitedStartRow = r2
                r4.invitedEndRow = r2
                goto L_0x00aa
            L_0x0097:
                int r0 = r4.rowsCount
                r4.invitedStartRow = r0
                org.telegram.ui.GroupCallActivity r1 = org.telegram.ui.GroupCallActivity.this
                org.telegram.messenger.ChatObject$Call r1 = r1.call
                java.util.ArrayList<java.lang.Integer> r1 = r1.invitedUsers
                int r1 = r1.size()
                int r0 = r0 + r1
                r4.rowsCount = r0
                r4.invitedEndRow = r0
            L_0x00aa:
                int r0 = r4.rowsCount
                int r1 = r0 + 1
                r4.rowsCount = r1
                r4.lastRow = r0
            L_0x00b2:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.GroupCallActivity.ListAdapter.updateRows():void");
        }

        public void notifyDataSetChanged() {
            updateRows();
            super.notifyDataSetChanged();
        }

        public void notifyItemChanged(int i) {
            updateRows();
            super.notifyItemChanged(i);
        }

        public void notifyItemRangeChanged(int i, int i2, Object obj) {
            updateRows();
            super.notifyItemRangeChanged(i, i2, obj);
        }

        public void notifyItemMoved(int i, int i2) {
            updateRows();
            super.notifyItemMoved(i, i2);
        }

        public void notifyItemRangeInserted(int i, int i2) {
            updateRows();
            super.notifyItemRangeInserted(i, i2);
        }

        public void notifyItemRemoved(int i) {
            updateRows();
            super.notifyItemRemoved(i);
        }

        public void notifyItemRangeRemoved(int i, int i2) {
            updateRows();
            super.notifyItemRangeRemoved(i, i2);
        }

        public void onViewAttachedToWindow(RecyclerView.ViewHolder viewHolder) {
            int itemViewType = viewHolder.getItemViewType();
            boolean z = false;
            String str = "voipgroup_mutedIcon";
            if (itemViewType == 1) {
                GroupCallUserCell groupCallUserCell = (GroupCallUserCell) viewHolder.itemView;
                if (GroupCallActivity.this.actionBar.getTag() == null) {
                    str = "voipgroup_mutedIconUnscrolled";
                }
                groupCallUserCell.setGrayIconColor(str, Theme.getColor(str));
                if (viewHolder.getAdapterPosition() != getItemCount() - 2) {
                    z = true;
                }
                groupCallUserCell.setDrawDivider(z);
            } else if (itemViewType == 2) {
                GroupCallInvitedCell groupCallInvitedCell = (GroupCallInvitedCell) viewHolder.itemView;
                if (GroupCallActivity.this.actionBar.getTag() == null) {
                    str = "voipgroup_mutedIconUnscrolled";
                }
                groupCallInvitedCell.setGrayIconColor(str, Theme.getColor(str));
                if (viewHolder.getAdapterPosition() != getItemCount() - 2) {
                    z = true;
                }
                groupCallInvitedCell.setDrawDivider(z);
            }
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v29, resolved type: java.lang.Object} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v12, resolved type: java.lang.Integer} */
        /* JADX WARNING: type inference failed for: r9v0, types: [org.telegram.tgnet.TLRPC$FileLocation] */
        /* JADX WARNING: type inference failed for: r3v8, types: [org.telegram.tgnet.TLRPC$FileLocation] */
        /* JADX WARNING: Multi-variable type inference failed */
        /* JADX WARNING: Removed duplicated region for block: B:31:0x00ad  */
        /* JADX WARNING: Removed duplicated region for block: B:60:? A[RETURN, SYNTHETIC] */
        /* JADX WARNING: Unknown variable types count: 1 */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r11, int r12) {
            /*
                r10 = this;
                int r0 = r11.getItemViewType()
                r1 = 1065353216(0x3var_, float:1.0)
                r2 = 1
                if (r0 == 0) goto L_0x00f8
                r3 = 0
                if (r0 == r2) goto L_0x0064
                r1 = 2
                if (r0 == r1) goto L_0x0011
                goto L_0x015a
            L_0x0011:
                android.view.View r11 = r11.itemView
                org.telegram.ui.Cells.GroupCallInvitedCell r11 = (org.telegram.ui.Cells.GroupCallInvitedCell) r11
                int r0 = r10.invitedStartRow
                int r12 = r12 - r0
                org.telegram.ui.GroupCallActivity r0 = org.telegram.ui.GroupCallActivity.this
                boolean r0 = r0.delayedGroupCallUpdated
                if (r0 == 0) goto L_0x003c
                if (r12 < 0) goto L_0x0057
                org.telegram.ui.GroupCallActivity r0 = org.telegram.ui.GroupCallActivity.this
                java.util.ArrayList r0 = r0.oldInvited
                int r0 = r0.size()
                if (r12 >= r0) goto L_0x0057
                org.telegram.ui.GroupCallActivity r0 = org.telegram.ui.GroupCallActivity.this
                java.util.ArrayList r0 = r0.oldInvited
                java.lang.Object r12 = r0.get(r12)
                r3 = r12
                java.lang.Integer r3 = (java.lang.Integer) r3
                goto L_0x0057
            L_0x003c:
                if (r12 < 0) goto L_0x0057
                org.telegram.ui.GroupCallActivity r0 = org.telegram.ui.GroupCallActivity.this
                org.telegram.messenger.ChatObject$Call r0 = r0.call
                java.util.ArrayList<java.lang.Integer> r0 = r0.invitedUsers
                int r0 = r0.size()
                if (r12 >= r0) goto L_0x0057
                org.telegram.ui.GroupCallActivity r0 = org.telegram.ui.GroupCallActivity.this
                org.telegram.messenger.ChatObject$Call r0 = r0.call
                java.util.ArrayList<java.lang.Integer> r0 = r0.invitedUsers
                java.lang.Object r12 = r0.get(r12)
                r3 = r12
                java.lang.Integer r3 = (java.lang.Integer) r3
            L_0x0057:
                if (r3 == 0) goto L_0x015a
                org.telegram.ui.GroupCallActivity r12 = org.telegram.ui.GroupCallActivity.this
                int r12 = r12.currentAccount
                r11.setData(r12, r3)
                goto L_0x015a
            L_0x0064:
                android.view.View r11 = r11.itemView
                org.telegram.ui.Cells.GroupCallUserCell r11 = (org.telegram.ui.Cells.GroupCallUserCell) r11
                int r0 = r10.usersStartRow
                int r12 = r12 - r0
                org.telegram.ui.GroupCallActivity r0 = org.telegram.ui.GroupCallActivity.this
                boolean r0 = r0.delayedGroupCallUpdated
                if (r0 == 0) goto L_0x008e
                if (r12 < 0) goto L_0x00aa
                org.telegram.ui.GroupCallActivity r0 = org.telegram.ui.GroupCallActivity.this
                java.util.ArrayList r0 = r0.oldParticipants
                int r0 = r0.size()
                if (r12 >= r0) goto L_0x00aa
                org.telegram.ui.GroupCallActivity r0 = org.telegram.ui.GroupCallActivity.this
                java.util.ArrayList r0 = r0.oldParticipants
                java.lang.Object r12 = r0.get(r12)
                org.telegram.tgnet.TLRPC$TL_groupCallParticipant r12 = (org.telegram.tgnet.TLRPC$TL_groupCallParticipant) r12
                goto L_0x00a8
            L_0x008e:
                if (r12 < 0) goto L_0x00aa
                org.telegram.ui.GroupCallActivity r0 = org.telegram.ui.GroupCallActivity.this
                org.telegram.messenger.ChatObject$Call r0 = r0.call
                java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_groupCallParticipant> r0 = r0.sortedParticipants
                int r0 = r0.size()
                if (r12 >= r0) goto L_0x00aa
                org.telegram.ui.GroupCallActivity r0 = org.telegram.ui.GroupCallActivity.this
                org.telegram.messenger.ChatObject$Call r0 = r0.call
                java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_groupCallParticipant> r0 = r0.sortedParticipants
                java.lang.Object r12 = r0.get(r12)
                org.telegram.tgnet.TLRPC$TL_groupCallParticipant r12 = (org.telegram.tgnet.TLRPC$TL_groupCallParticipant) r12
            L_0x00a8:
                r6 = r12
                goto L_0x00ab
            L_0x00aa:
                r6 = r3
            L_0x00ab:
                if (r6 == 0) goto L_0x015a
                org.telegram.tgnet.TLRPC$Peer r12 = r6.peer
                int r12 = org.telegram.messenger.MessageObject.getPeerId(r12)
                org.telegram.ui.GroupCallActivity r0 = org.telegram.ui.GroupCallActivity.this
                org.telegram.tgnet.TLRPC$Peer r0 = r0.selfPeer
                int r8 = org.telegram.messenger.MessageObject.getPeerId(r0)
                if (r12 != r8) goto L_0x00c9
                org.telegram.ui.GroupCallActivity r0 = org.telegram.ui.GroupCallActivity.this
                org.telegram.ui.GroupCallActivity$AvatarUpdaterDelegate r0 = r0.avatarUpdaterDelegate
                if (r0 == 0) goto L_0x00c9
                org.telegram.tgnet.TLRPC$FileLocation r3 = r0.avatar
            L_0x00c9:
                r9 = r3
                if (r9 == 0) goto L_0x00d2
                org.telegram.ui.GroupCallActivity r0 = org.telegram.ui.GroupCallActivity.this
                org.telegram.ui.GroupCallActivity$AvatarUpdaterDelegate r0 = r0.avatarUpdaterDelegate
                float r1 = r0.uploadingProgress
            L_0x00d2:
                org.telegram.tgnet.TLRPC$TL_groupCallParticipant r0 = r11.getParticipant()
                if (r0 == 0) goto L_0x00e5
                org.telegram.tgnet.TLRPC$TL_groupCallParticipant r0 = r11.getParticipant()
                org.telegram.tgnet.TLRPC$Peer r0 = r0.peer
                int r0 = org.telegram.messenger.MessageObject.getPeerId(r0)
                if (r0 != r12) goto L_0x00e5
                goto L_0x00e6
            L_0x00e5:
                r2 = 0
            L_0x00e6:
                org.telegram.ui.GroupCallActivity r12 = org.telegram.ui.GroupCallActivity.this
                org.telegram.messenger.AccountInstance r5 = r12.accountInstance
                org.telegram.ui.GroupCallActivity r12 = org.telegram.ui.GroupCallActivity.this
                org.telegram.messenger.ChatObject$Call r7 = r12.call
                r4 = r11
                r4.setData(r5, r6, r7, r8, r9)
                r11.setUploadProgress(r1, r2)
                goto L_0x015a
            L_0x00f8:
                android.view.View r11 = r11.itemView
                org.telegram.ui.Cells.GroupCallTextCell r11 = (org.telegram.ui.Cells.GroupCallTextCell) r11
                java.lang.String r12 = "voipgroup_lastSeenTextUnscrolled"
                int r12 = org.telegram.ui.ActionBar.Theme.getColor(r12)
                java.lang.String r0 = "voipgroup_lastSeenText"
                int r0 = org.telegram.ui.ActionBar.Theme.getColor(r0)
                org.telegram.ui.GroupCallActivity r3 = org.telegram.ui.GroupCallActivity.this
                org.telegram.ui.ActionBar.ActionBar r3 = r3.actionBar
                java.lang.Object r3 = r3.getTag()
                if (r3 == 0) goto L_0x0119
                r3 = 1065353216(0x3var_, float:1.0)
                goto L_0x011a
            L_0x0119:
                r3 = 0
            L_0x011a:
                int r12 = org.telegram.messenger.AndroidUtilities.getOffsetColor(r12, r0, r3, r1)
                r11.setColors(r12, r12)
                org.telegram.ui.GroupCallActivity r12 = org.telegram.ui.GroupCallActivity.this
                org.telegram.tgnet.TLRPC$Chat r12 = r12.currentChat
                boolean r12 = org.telegram.messenger.ChatObject.isChannel(r12)
                if (r12 == 0) goto L_0x014b
                org.telegram.ui.GroupCallActivity r12 = org.telegram.ui.GroupCallActivity.this
                org.telegram.tgnet.TLRPC$Chat r12 = r12.currentChat
                boolean r0 = r12.megagroup
                if (r0 != 0) goto L_0x014b
                java.lang.String r12 = r12.username
                boolean r12 = android.text.TextUtils.isEmpty(r12)
                if (r12 != 0) goto L_0x014b
                r12 = 2131628093(0x7f0e103d, float:1.8883469E38)
                java.lang.String r0 = "VoipGroupShareLink"
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r0, r12)
                r0 = 2131165759(0x7var_f, float:1.7945744E38)
                r11.setTextAndIcon(r12, r0, r2)
                goto L_0x015a
            L_0x014b:
                r12 = 2131628051(0x7f0e1013, float:1.8883384E38)
                java.lang.String r0 = "VoipGroupInviteMember"
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r0, r12)
                r0 = 2131165249(0x7var_, float:1.794471E38)
                r11.setTextAndIcon(r12, r0, r2)
            L_0x015a:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.GroupCallActivity.ListAdapter.onBindViewHolder(androidx.recyclerview.widget.RecyclerView$ViewHolder, int):void");
        }

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            int itemViewType = viewHolder.getItemViewType();
            if (itemViewType != 1) {
                return itemViewType != 3;
            }
            GroupCallUserCell groupCallUserCell = (GroupCallUserCell) viewHolder.itemView;
            if (!groupCallUserCell.isSelfUser() || groupCallUserCell.isHandRaised()) {
                return true;
            }
            return false;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view;
            if (i == 0) {
                view = new GroupCallTextCell(this.mContext);
            } else if (i != 1) {
                view = i != 2 ? new View(this.mContext) : new GroupCallInvitedCell(this.mContext);
            } else {
                view = new GroupCallUserCell(this.mContext) {
                    /* access modifiers changed from: protected */
                    public void onMuteClick(GroupCallUserCell groupCallUserCell) {
                        boolean unused = GroupCallActivity.this.showMenuForCell(groupCallUserCell);
                    }
                };
            }
            view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(view);
        }

        public int getItemViewType(int i) {
            if (i == this.lastRow) {
                return 3;
            }
            if (i == this.addMemberRow) {
                return 0;
            }
            return (i < this.usersStartRow || i >= this.usersEndRow) ? 2 : 1;
        }
    }

    public void setOldRows(int i, int i2, int i3, int i4, int i5) {
        this.oldAddMemberRow = i;
        this.oldUsersStartRow = i2;
        this.oldUsersEndRow = i3;
        this.oldInvitedStartRow = i4;
        this.oldInvitedEndRow = i5;
    }

    private static class UpdateCallback implements ListUpdateCallback {
        final RecyclerView.Adapter adapter;

        private UpdateCallback(RecyclerView.Adapter adapter2) {
            this.adapter = adapter2;
        }

        public void onInserted(int i, int i2) {
            this.adapter.notifyItemRangeInserted(i, i2);
        }

        public void onRemoved(int i, int i2) {
            this.adapter.notifyItemRangeRemoved(i, i2);
        }

        public void onMoved(int i, int i2) {
            this.adapter.notifyItemMoved(i, i2);
        }

        public void onChanged(int i, int i2, Object obj) {
            this.adapter.notifyItemRangeChanged(i, i2, obj);
        }
    }

    /* access modifiers changed from: private */
    public void toggleAdminSpeak() {
        TLRPC$TL_phone_toggleGroupCallSettings tLRPC$TL_phone_toggleGroupCallSettings = new TLRPC$TL_phone_toggleGroupCallSettings();
        tLRPC$TL_phone_toggleGroupCallSettings.call = this.call.getInputGroupCall();
        tLRPC$TL_phone_toggleGroupCallSettings.join_muted = this.call.call.join_muted;
        tLRPC$TL_phone_toggleGroupCallSettings.flags |= 1;
        this.accountInstance.getConnectionsManager().sendRequest(tLRPC$TL_phone_toggleGroupCallSettings, new RequestDelegate() {
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                GroupCallActivity.this.lambda$toggleAdminSpeak$56$GroupCallActivity(tLObject, tLRPC$TL_error);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$toggleAdminSpeak$56 */
    public /* synthetic */ void lambda$toggleAdminSpeak$56$GroupCallActivity(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLObject != null) {
            this.accountInstance.getMessagesController().processUpdates((TLRPC$Updates) tLObject, false);
        }
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        return new ArrayList<>();
    }

    public void onBackPressed() {
        if (this.avatarsPreviewShowed) {
            dismissAvatarPreview(true);
        } else {
            super.onBackPressed();
        }
    }

    private class AvatarUpdaterDelegate implements ImageUpdater.ImageUpdaterDelegate {
        /* access modifiers changed from: private */
        public TLRPC$FileLocation avatar;
        private TLRPC$FileLocation avatarBig;
        private final int peerId;
        /* access modifiers changed from: private */
        public ImageLocation uploadingImageLocation;
        public float uploadingProgress;

        public void didStartUpload(boolean z) {
        }

        public /* synthetic */ String getInitialSearchString() {
            return ImageUpdater.ImageUpdaterDelegate.CC.$default$getInitialSearchString(this);
        }

        private AvatarUpdaterDelegate(int i) {
            this.peerId = i;
        }

        public void didUploadPhoto(TLRPC$InputFile tLRPC$InputFile, TLRPC$InputFile tLRPC$InputFile2, double d, String str, TLRPC$PhotoSize tLRPC$PhotoSize, TLRPC$PhotoSize tLRPC$PhotoSize2) {
            AndroidUtilities.runOnUIThread(new Runnable(tLRPC$InputFile, tLRPC$InputFile2, d, str, tLRPC$PhotoSize2, tLRPC$PhotoSize) {
                public final /* synthetic */ TLRPC$InputFile f$1;
                public final /* synthetic */ TLRPC$InputFile f$2;
                public final /* synthetic */ double f$3;
                public final /* synthetic */ String f$4;
                public final /* synthetic */ TLRPC$PhotoSize f$5;
                public final /* synthetic */ TLRPC$PhotoSize f$6;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r6;
                    this.f$5 = r7;
                    this.f$6 = r8;
                }

                public final void run() {
                    GroupCallActivity.AvatarUpdaterDelegate.this.lambda$didUploadPhoto$3$GroupCallActivity$AvatarUpdaterDelegate(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
                }
            });
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$null$1 */
        public /* synthetic */ void lambda$null$1$GroupCallActivity$AvatarUpdaterDelegate(String str, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
            AndroidUtilities.runOnUIThread(new Runnable(tLRPC$TL_error, tLObject, str) {
                public final /* synthetic */ TLRPC$TL_error f$1;
                public final /* synthetic */ TLObject f$2;
                public final /* synthetic */ String f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void run() {
                    GroupCallActivity.AvatarUpdaterDelegate.this.lambda$null$0$GroupCallActivity$AvatarUpdaterDelegate(this.f$1, this.f$2, this.f$3);
                }
            });
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$null$0 */
        public /* synthetic */ void lambda$null$0$GroupCallActivity$AvatarUpdaterDelegate(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, String str) {
            if (this.uploadingImageLocation != null) {
                GroupCallActivity.this.avatarsViewPager.removeUploadingImage(this.uploadingImageLocation);
                this.uploadingImageLocation = null;
            }
            if (tLRPC$TL_error == null) {
                TLRPC$User user = GroupCallActivity.this.accountInstance.getMessagesController().getUser(Integer.valueOf(GroupCallActivity.this.accountInstance.getUserConfig().getClientUserId()));
                if (user == null) {
                    user = GroupCallActivity.this.accountInstance.getUserConfig().getCurrentUser();
                    if (user != null) {
                        GroupCallActivity.this.accountInstance.getMessagesController().putUser(user, false);
                    } else {
                        return;
                    }
                } else {
                    GroupCallActivity.this.accountInstance.getUserConfig().setCurrentUser(user);
                }
                TLRPC$TL_photos_photo tLRPC$TL_photos_photo = (TLRPC$TL_photos_photo) tLObject;
                ArrayList<TLRPC$PhotoSize> arrayList = tLRPC$TL_photos_photo.photo.sizes;
                TLRPC$PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(arrayList, 150);
                TLRPC$PhotoSize closestPhotoSizeWithSize2 = FileLoader.getClosestPhotoSizeWithSize(arrayList, 800);
                TLRPC$VideoSize tLRPC$VideoSize = tLRPC$TL_photos_photo.photo.video_sizes.isEmpty() ? null : tLRPC$TL_photos_photo.photo.video_sizes.get(0);
                TLRPC$TL_userProfilePhoto tLRPC$TL_userProfilePhoto = new TLRPC$TL_userProfilePhoto();
                user.photo = tLRPC$TL_userProfilePhoto;
                tLRPC$TL_userProfilePhoto.photo_id = tLRPC$TL_photos_photo.photo.id;
                if (closestPhotoSizeWithSize != null) {
                    tLRPC$TL_userProfilePhoto.photo_small = closestPhotoSizeWithSize.location;
                }
                if (closestPhotoSizeWithSize2 != null) {
                    tLRPC$TL_userProfilePhoto.photo_big = closestPhotoSizeWithSize2.location;
                }
                if (!(closestPhotoSizeWithSize == null || this.avatar == null)) {
                    FileLoader.getPathToAttach(this.avatar, true).renameTo(FileLoader.getPathToAttach(closestPhotoSizeWithSize, true));
                    ImageLoader.getInstance().replaceImageInCache(this.avatar.volume_id + "_" + this.avatar.local_id + "@50_50", closestPhotoSizeWithSize.location.volume_id + "_" + closestPhotoSizeWithSize.location.local_id + "@50_50", ImageLocation.getForUser(user, 1), false);
                }
                if (!(closestPhotoSizeWithSize2 == null || this.avatarBig == null)) {
                    FileLoader.getPathToAttach(this.avatarBig, true).renameTo(FileLoader.getPathToAttach(closestPhotoSizeWithSize2, true));
                }
                if (!(tLRPC$VideoSize == null || str == null)) {
                    new File(str).renameTo(FileLoader.getPathToAttach(tLRPC$VideoSize, "mp4", true));
                }
                GroupCallActivity.this.accountInstance.getMessagesStorage().clearUserPhotos(user.id);
                ArrayList arrayList2 = new ArrayList();
                arrayList2.add(user);
                GroupCallActivity.this.accountInstance.getMessagesStorage().putUsersAndChats(arrayList2, (ArrayList<TLRPC$Chat>) null, false, true);
                TLRPC$User user2 = GroupCallActivity.this.accountInstance.getMessagesController().getUser(Integer.valueOf(this.peerId));
                ImageLocation forUser = ImageLocation.getForUser(user2, 0);
                ImageLocation forUser2 = ImageLocation.getForUser(user2, 1);
                if (ImageLocation.getForLocal(this.avatarBig) == null) {
                    forUser2 = ImageLocation.getForLocal(this.avatar);
                }
                GroupCallActivity.this.avatarsViewPager.setCreateThumbFromParent(false);
                GroupCallActivity.this.avatarsViewPager.initIfEmpty(forUser, forUser2);
                this.avatar = null;
                this.avatarBig = null;
                AndroidUtilities.updateVisibleRows(GroupCallActivity.this.listView);
                updateAvatarUploadingProgress(1.0f);
            }
            GroupCallActivity.this.accountInstance.getNotificationCenter().postNotificationName(NotificationCenter.updateInterfaces, 1535);
            GroupCallActivity.this.accountInstance.getNotificationCenter().postNotificationName(NotificationCenter.mainUserInfoChanged, new Object[0]);
            GroupCallActivity.this.accountInstance.getUserConfig().saveConfig(true);
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$null$2 */
        public /* synthetic */ void lambda$null$2$GroupCallActivity$AvatarUpdaterDelegate() {
            if (this.uploadingImageLocation != null) {
                GroupCallActivity.this.avatarsViewPager.removeUploadingImage(this.uploadingImageLocation);
                this.uploadingImageLocation = null;
            }
            TLRPC$Chat chat = GroupCallActivity.this.accountInstance.getMessagesController().getChat(Integer.valueOf(-this.peerId));
            ImageLocation forChat = ImageLocation.getForChat(chat, 0);
            ImageLocation forChat2 = ImageLocation.getForChat(chat, 1);
            if (ImageLocation.getForLocal(this.avatarBig) == null) {
                forChat2 = ImageLocation.getForLocal(this.avatar);
            }
            GroupCallActivity.this.avatarsViewPager.setCreateThumbFromParent(false);
            GroupCallActivity.this.avatarsViewPager.initIfEmpty(forChat, forChat2);
            this.avatar = null;
            this.avatarBig = null;
            AndroidUtilities.updateVisibleRows(GroupCallActivity.this.listView);
            updateAvatarUploadingProgress(1.0f);
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$didUploadPhoto$3 */
        public /* synthetic */ void lambda$didUploadPhoto$3$GroupCallActivity$AvatarUpdaterDelegate(TLRPC$InputFile tLRPC$InputFile, TLRPC$InputFile tLRPC$InputFile2, double d, String str, TLRPC$PhotoSize tLRPC$PhotoSize, TLRPC$PhotoSize tLRPC$PhotoSize2) {
            TLRPC$InputFile tLRPC$InputFile3 = tLRPC$InputFile;
            TLRPC$InputFile tLRPC$InputFile4 = tLRPC$InputFile2;
            TLRPC$PhotoSize tLRPC$PhotoSize3 = tLRPC$PhotoSize;
            TLRPC$PhotoSize tLRPC$PhotoSize4 = tLRPC$PhotoSize2;
            if (tLRPC$InputFile3 == null && tLRPC$InputFile4 == null) {
                this.avatar = tLRPC$PhotoSize3.location;
                TLRPC$FileLocation tLRPC$FileLocation = tLRPC$PhotoSize4.location;
                this.avatarBig = tLRPC$FileLocation;
                this.uploadingImageLocation = ImageLocation.getForLocal(tLRPC$FileLocation);
                GroupCallActivity.this.avatarsViewPager.addUploadingImage(this.uploadingImageLocation, ImageLocation.getForLocal(this.avatar));
                AndroidUtilities.updateVisibleRows(GroupCallActivity.this.listView);
            } else if (this.peerId > 0) {
                TLRPC$TL_photos_uploadProfilePhoto tLRPC$TL_photos_uploadProfilePhoto = new TLRPC$TL_photos_uploadProfilePhoto();
                if (tLRPC$InputFile3 != null) {
                    tLRPC$TL_photos_uploadProfilePhoto.file = tLRPC$InputFile3;
                    tLRPC$TL_photos_uploadProfilePhoto.flags |= 1;
                }
                if (tLRPC$InputFile4 != null) {
                    tLRPC$TL_photos_uploadProfilePhoto.video = tLRPC$InputFile4;
                    int i = tLRPC$TL_photos_uploadProfilePhoto.flags | 2;
                    tLRPC$TL_photos_uploadProfilePhoto.flags = i;
                    tLRPC$TL_photos_uploadProfilePhoto.video_start_ts = d;
                    tLRPC$TL_photos_uploadProfilePhoto.flags = i | 4;
                }
                GroupCallActivity.this.accountInstance.getConnectionsManager().sendRequest(tLRPC$TL_photos_uploadProfilePhoto, new RequestDelegate(str) {
                    public final /* synthetic */ String f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                        GroupCallActivity.AvatarUpdaterDelegate.this.lambda$null$1$GroupCallActivity$AvatarUpdaterDelegate(this.f$1, tLObject, tLRPC$TL_error);
                    }
                });
            } else {
                GroupCallActivity.this.accountInstance.getMessagesController().changeChatAvatar(-this.peerId, (TLRPC$TL_inputChatPhoto) null, tLRPC$InputFile, tLRPC$InputFile2, d, str, tLRPC$PhotoSize3.location, tLRPC$PhotoSize4.location, new Runnable() {
                    public final void run() {
                        GroupCallActivity.AvatarUpdaterDelegate.this.lambda$null$2$GroupCallActivity$AvatarUpdaterDelegate();
                    }
                });
            }
        }

        public void onUploadProgressChanged(float f) {
            GroupCallActivity.this.avatarsViewPager.setUploadProgress(this.uploadingImageLocation, f);
            updateAvatarUploadingProgress(f);
        }

        public void updateAvatarUploadingProgress(float f) {
            this.uploadingProgress = f;
            if (GroupCallActivity.this.listView != null) {
                for (int i = 0; i < GroupCallActivity.this.listView.getChildCount(); i++) {
                    View childAt = GroupCallActivity.this.listView.getChildAt(i);
                    if (childAt instanceof GroupCallUserCell) {
                        GroupCallUserCell groupCallUserCell = (GroupCallUserCell) childAt;
                        if (groupCallUserCell.isSelfUser()) {
                            groupCallUserCell.setUploadProgress(f, true);
                        }
                    }
                }
            }
        }
    }
}
