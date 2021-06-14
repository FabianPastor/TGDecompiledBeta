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
import android.graphics.drawable.GradientDrawable;
import android.media.AudioManager;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.Property;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ListUpdateCallback;
import androidx.recyclerview.widget.RecyclerView;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
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
import org.telegram.ui.Components.FillLastGridLayoutManager;
import org.telegram.ui.Components.GroupCallFullscreenAdapter;
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
import org.telegram.ui.Components.TypefaceSpan;
import org.telegram.ui.Components.UndoView;
import org.telegram.ui.Components.voip.CellFlickerDrawable;
import org.telegram.ui.Components.voip.GroupCallGridCell;
import org.telegram.ui.Components.voip.GroupCallMiniTextureView;
import org.telegram.ui.Components.voip.GroupCallRenderersContainer;
import org.telegram.ui.Components.voip.GroupCallStatusIcon;
import org.telegram.ui.Components.voip.VideoPreviewDialog;
import org.telegram.ui.Components.voip.VoIPToggleButton;
import org.telegram.ui.GroupCallActivity;
import org.webrtc.VideoSink;

public class GroupCallActivity extends BottomSheet implements NotificationCenter.NotificationCenterDelegate, VoIPService.StateListener {
    public static final Property<GroupCallActivity, Float> COLOR_PROGRESS = new AnimationProperties.FloatProperty<GroupCallActivity>("colorProgress") {
        public void setValue(GroupCallActivity groupCallActivity, float f) {
            groupCallActivity.setColorProgress(f);
        }

        public Float get(GroupCallActivity groupCallActivity) {
            return Float.valueOf(groupCallActivity.getColorProgress());
        }
    };
    public static GroupCallActivity groupCallInstance;
    public static boolean groupCallUiVisible;
    public static boolean isLandscapeMode;
    public static boolean isTabletMode;
    public static boolean paused;
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
    /* access modifiers changed from: private */
    public View actionBarBackground;
    /* access modifiers changed from: private */
    public View actionBarShadow;
    /* access modifiers changed from: private */
    public ActionBarMenuSubItem adminItem;
    /* access modifiers changed from: private */
    public float amplitude;
    /* access modifiers changed from: private */
    public float animateAmplitudeDiff;
    boolean animateButtonsOnNextLayout;
    /* access modifiers changed from: private */
    public float animateToAmplitude;
    private boolean anyEnterEventSent;
    /* access modifiers changed from: private */
    public final ArrayList<GroupCallMiniTextureView> attachedRenderers = new ArrayList<>();
    /* access modifiers changed from: private */
    public final ArrayList<GroupCallMiniTextureView> attachedRenderersTmp = new ArrayList<>();
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
    /* access modifiers changed from: private */
    public View blurredView;
    /* access modifiers changed from: private */
    public HashMap<View, Float> buttonsAnimationParamsX = new HashMap<>();
    /* access modifiers changed from: private */
    public HashMap<View, Float> buttonsAnimationParamsY = new HashMap<>();
    private GradientDrawable buttonsBackgroundGradient;
    /* access modifiers changed from: private */
    public final View buttonsBackgroundGradientView;
    /* access modifiers changed from: private */
    public final View buttonsBackgroundGradientView2;
    /* access modifiers changed from: private */
    public FrameLayout buttonsContainer;
    private int buttonsVisibility;
    public ChatObject.Call call;
    private boolean callInitied;
    /* access modifiers changed from: private */
    public VoIPToggleButton cameraButton;
    private float cameraButtonScale;
    public CellFlickerDrawable cellFlickerDrawable = new CellFlickerDrawable();
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
            if (GroupCallActivity.this.listAdapter.videoGridDividerRow >= 0 && GroupCallActivity.this.listAdapter.videoGridDividerRow == i2 && i == GroupCallActivity.this.oldVideoDividerRow) {
                return true;
            }
            if (i == GroupCallActivity.this.oldCount - 1 && i2 == GroupCallActivity.this.listAdapter.rowsCount - 1) {
                return true;
            }
            if (!(i == GroupCallActivity.this.oldCount - 1 || i2 == GroupCallActivity.this.listAdapter.rowsCount - 1)) {
                if (i2 >= GroupCallActivity.this.listAdapter.usersVideoGridStartRow && i2 < GroupCallActivity.this.listAdapter.usersVideoGridEndRow && i >= GroupCallActivity.this.oldUsersVideoStartRow && i < GroupCallActivity.this.oldUsersVideoEndRow) {
                    GroupCallActivity groupCallActivity = GroupCallActivity.this;
                    return ((ChatObject.VideoParticipant) GroupCallActivity.this.oldVideoParticipants.get(i - GroupCallActivity.this.oldUsersVideoStartRow)).equals(groupCallActivity.visibleVideoParticipants.get(i2 - groupCallActivity.listAdapter.usersVideoGridStartRow));
                } else if (i2 >= GroupCallActivity.this.listAdapter.usersStartRow && i2 < GroupCallActivity.this.listAdapter.usersEndRow && i >= GroupCallActivity.this.oldUsersStartRow && i < GroupCallActivity.this.oldUsersEndRow) {
                    TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant = (TLRPC$TL_groupCallParticipant) GroupCallActivity.this.oldParticipants.get(i - GroupCallActivity.this.oldUsersStartRow);
                    GroupCallActivity groupCallActivity2 = GroupCallActivity.this;
                    if (MessageObject.getPeerId(tLRPC$TL_groupCallParticipant.peer) != MessageObject.getPeerId(groupCallActivity2.call.visibleParticipants.get(i2 - groupCallActivity2.listAdapter.usersStartRow).peer)) {
                        return false;
                    }
                    if (i == i2 || tLRPC$TL_groupCallParticipant.lastActiveDate == ((long) tLRPC$TL_groupCallParticipant.active_date)) {
                        return true;
                    }
                    return false;
                } else if (i2 >= GroupCallActivity.this.listAdapter.invitedStartRow && i2 < GroupCallActivity.this.listAdapter.invitedEndRow && i >= GroupCallActivity.this.oldInvitedStartRow && i < GroupCallActivity.this.oldInvitedEndRow) {
                    GroupCallActivity groupCallActivity3 = GroupCallActivity.this;
                    return ((Integer) GroupCallActivity.this.oldInvited.get(i - GroupCallActivity.this.oldInvitedStartRow)).equals(groupCallActivity3.call.invitedUsers.get(i2 - groupCallActivity3.listAdapter.invitedStartRow));
                }
            }
            return false;
        }
    };
    private boolean drawSpeakingSubtitle;
    public boolean drawingForBlur;
    /* access modifiers changed from: private */
    public ActionBarMenuSubItem editTitleItem;
    /* access modifiers changed from: private */
    public boolean enterEventSent;
    /* access modifiers changed from: private */
    public ActionBarMenuSubItem everyoneItem;
    /* access modifiers changed from: private */
    public VoIPToggleButton flipButton;
    private final RLottieDrawable flipIcon;
    private int flipIconCurrentEndFrame;
    GroupCallFullscreenAdapter fullscreenAdapter;
    /* access modifiers changed from: private */
    public final DefaultItemAnimator fullscreenListItemAnimator;
    RecyclerListView fullscreenUsersListView;
    private int[] gradientColors = new int[2];
    /* access modifiers changed from: private */
    public GroupVoipInviteAlert groupVoipInviteAlert;
    /* access modifiers changed from: private */
    public RLottieDrawable handDrawables;
    /* access modifiers changed from: private */
    public boolean hasScrimAnchorView;
    /* access modifiers changed from: private */
    public boolean hasVideo;
    /* access modifiers changed from: private */
    public boolean invalidateColors = true;
    /* access modifiers changed from: private */
    public ActionBarMenuSubItem inviteItem;
    private String[] invites = new String[2];
    /* access modifiers changed from: private */
    public GroupCallItemAnimator itemAnimator;
    /* access modifiers changed from: private */
    public long lastUpdateTime;
    /* access modifiers changed from: private */
    public FillLastGridLayoutManager layoutManager;
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
    /* access modifiers changed from: private */
    public boolean listViewVideoVisibility = true;
    /* access modifiers changed from: private */
    public final LinearLayout menuItemsContainer;
    /* access modifiers changed from: private */
    public RLottieImageView muteButton;
    /* access modifiers changed from: private */
    public ValueAnimator muteButtonAnimator;
    /* access modifiers changed from: private */
    public int muteButtonState = 0;
    /* access modifiers changed from: private */
    public TextView[] muteLabel = new TextView[2];
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
    public int oldUsersVideoEndRow;
    /* access modifiers changed from: private */
    public int oldUsersVideoStartRow;
    /* access modifiers changed from: private */
    public int oldVideoDividerRow;
    /* access modifiers changed from: private */
    public ArrayList<ChatObject.VideoParticipant> oldVideoParticipants = new ArrayList<>();
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
    VideoPreviewDialog previewDialog;
    /* access modifiers changed from: private */
    public boolean previewTextureTransitionEnabled;
    /* access modifiers changed from: private */
    public float progressToAvatarPreview;
    float progressToHideUi;
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
    public GroupCallRenderersContainer renderersContainer;
    ViewTreeObserver.OnPreDrawListener requestFullscreenListener;
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
    public ActionBarMenuSubItem screenItem;
    /* access modifiers changed from: private */
    public ActionBarMenuItem screenShareItem;
    /* access modifiers changed from: private */
    public AnimatorSet scrimAnimatorSet;
    /* access modifiers changed from: private */
    public GroupCallFullscreenAdapter.GroupCallUserCell scrimFullscreenView;
    private GroupCallGridCell scrimGridView;
    /* access modifiers changed from: private */
    public Paint scrimPaint;
    /* access modifiers changed from: private */
    public View scrimPopupLayout;
    /* access modifiers changed from: private */
    public ActionBarPopupWindow scrimPopupWindow;
    /* access modifiers changed from: private */
    public GroupCallMiniTextureView scrimRenderer;
    /* access modifiers changed from: private */
    public GroupCallUserCell scrimView;
    private boolean scrimViewAttached;
    /* access modifiers changed from: private */
    public float scrollOffsetY;
    /* access modifiers changed from: private */
    public TLRPC$Peer selfPeer;
    private int shaderBitmapSize = 200;
    /* access modifiers changed from: private */
    public Drawable shadowDrawable;
    private ShareAlert shareAlert;
    /* access modifiers changed from: private */
    public float showLightingProgress;
    /* access modifiers changed from: private */
    public float showWavesProgress;
    /* access modifiers changed from: private */
    public VoIPToggleButton soundButton;
    private float soundButtonScale;
    /* access modifiers changed from: private */
    public ActionBarMenuSubItem soundItem;
    private View soundItemDivider;
    /* access modifiers changed from: private */
    public final GridLayoutManager.SpanSizeLookup spanSizeLookup;
    /* access modifiers changed from: private */
    public boolean startingGroupCall;
    private WeavingState[] states = new WeavingState[8];
    public final ArrayList<GroupCallStatusIcon> statusIconPool = new ArrayList<>();
    /* access modifiers changed from: private */
    public float switchProgress = 1.0f;
    /* access modifiers changed from: private */
    public float switchToButtonInt2;
    /* access modifiers changed from: private */
    public float switchToButtonProgress;
    GroupCallTabletGridAdapter tabletGridAdapter;
    RecyclerListView tabletVideoGridView;
    /* access modifiers changed from: private */
    public final BlobDrawable tinyWaveDrawable;
    /* access modifiers changed from: private */
    public AudioPlayerAlert.ClippingTextViewSwitcher titleTextView;
    /* access modifiers changed from: private */
    public UndoView[] undoView = new UndoView[2];
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
    /* access modifiers changed from: private */
    public boolean useBlur;
    private TLObject userSwitchObject;
    public final ArrayList<ChatObject.VideoParticipant> visibleVideoParticipants = new ArrayList<>();

    public static boolean isGradientState(int i) {
        return i == 2 || i == 4 || i == 5 || i == 6 || i == 7;
    }

    static /* synthetic */ void lambda$new$18(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
    }

    static /* synthetic */ void lambda$processSelectedOption$59(DialogInterface dialogInterface) {
    }

    /* access modifiers changed from: protected */
    public boolean canDismissWithSwipe() {
        return false;
    }

    public /* synthetic */ void onMediaStateUpdated(int i, int i2) {
        VoIPService.StateListener.CC.$default$onMediaStateUpdated(this, i, i2);
    }

    public /* synthetic */ void onScreenOnChange(boolean z) {
        VoIPService.StateListener.CC.$default$onScreenOnChange(this, z);
    }

    public /* synthetic */ void onSignalBarsCountChanged(int i) {
        VoIPService.StateListener.CC.$default$onSignalBarsCountChanged(this, i);
    }

    public /* synthetic */ void onVideoAvailableChange(boolean z) {
        VoIPService.StateListener.CC.$default$onVideoAvailableChange(this, z);
    }

    static /* synthetic */ float access$12416(GroupCallActivity groupCallActivity, float f) {
        float f2 = groupCallActivity.switchProgress + f;
        groupCallActivity.switchProgress = f2;
        return f2;
    }

    static /* synthetic */ float access$13016(GroupCallActivity groupCallActivity, float f) {
        float f2 = groupCallActivity.showWavesProgress + f;
        groupCallActivity.showWavesProgress = f2;
        return f2;
    }

    static /* synthetic */ float access$13024(GroupCallActivity groupCallActivity, float f) {
        float f2 = groupCallActivity.showWavesProgress - f;
        groupCallActivity.showWavesProgress = f2;
        return f2;
    }

    static /* synthetic */ float access$13116(GroupCallActivity groupCallActivity, float f) {
        float f2 = groupCallActivity.showLightingProgress + f;
        groupCallActivity.showLightingProgress = f2;
        return f2;
    }

    static /* synthetic */ float access$13124(GroupCallActivity groupCallActivity, float f) {
        float f2 = groupCallActivity.showLightingProgress - f;
        groupCallActivity.showLightingProgress = f2;
        return f2;
    }

    static /* synthetic */ float access$9716(GroupCallActivity groupCallActivity, float f) {
        float f2 = groupCallActivity.amplitude + f;
        groupCallActivity.amplitude = f2;
        return f2;
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
                r9 = 2131558483(0x7f0d0053, float:1.8742283E38)
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
                if (r15 <= 0) goto L_0x0117
                r9 = 4607182418800017408(0x3ffNUM, double:1.0)
                double r9 = java.lang.Math.max(r6, r9)
            L_0x0117:
                int r6 = (int) r9
                java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
                r14[r4] = r6
                java.lang.String r6 = "%d%%"
                java.lang.String r6 = java.lang.String.format(r13, r6, r14)
                r1.setText(r6)
                android.widget.TextView r1 = r0.textView
                boolean r6 = org.telegram.messenger.LocaleController.isRTL
                r7 = 1110179840(0x422CLASSNAME, float:43.0)
                if (r6 == 0) goto L_0x0131
                r6 = 0
                goto L_0x0135
            L_0x0131:
                int r6 = org.telegram.messenger.AndroidUtilities.dp(r7)
            L_0x0135:
                boolean r9 = org.telegram.messenger.LocaleController.isRTL
                if (r9 == 0) goto L_0x013e
                int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
                goto L_0x013f
            L_0x013e:
                r7 = 0
            L_0x013f:
                r1.setPadding(r6, r4, r7, r4)
                android.widget.TextView r1 = r0.textView
                boolean r6 = org.telegram.messenger.LocaleController.isRTL
                if (r6 == 0) goto L_0x0149
                r2 = 5
            L_0x0149:
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
            L_0x0180:
                float[] r6 = r0.volumeAlphas
                int r7 = r6.length
                if (r2 >= r7) goto L_0x019b
                if (r2 != 0) goto L_0x0189
                r7 = 0
                goto L_0x0190
            L_0x0189:
                if (r2 != r3) goto L_0x018e
                r7 = 50
                goto L_0x0190
            L_0x018e:
                r7 = 150(0x96, float:2.1E-43)
            L_0x0190:
                if (r1 <= r7) goto L_0x0195
                r6[r2] = r5
                goto L_0x0198
            L_0x0195:
                r7 = 0
                r6[r2] = r7
            L_0x0198:
                int r2 = r2 + 1
                goto L_0x0180
            L_0x019b:
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
            double d2 = d;
            if (VoIPService.getSharedInstance() != null) {
                this.currentProgress = d2;
                TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant = this.currentParticipant;
                tLRPC$TL_groupCallParticipant.volume = (int) (d2 * 20000.0d);
                int i = 0;
                tLRPC$TL_groupCallParticipant.volume_by_admin = false;
                tLRPC$TL_groupCallParticipant.flags |= 128;
                double participantVolume = (double) ChatObject.getParticipantVolume(tLRPC$TL_groupCallParticipant);
                Double.isNaN(participantVolume);
                double d3 = participantVolume / 100.0d;
                TextView textView2 = this.textView;
                Locale locale = Locale.US;
                Object[] objArr = new Object[1];
                objArr[0] = Integer.valueOf((int) (d3 > 0.0d ? Math.max(d3, 1.0d) : 0.0d));
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
                    TLObject tLObject2 = tLObject;
                    if (this.currentParticipant.volume == 0) {
                        if (this.this$0.scrimPopupWindow != null) {
                            this.this$0.scrimPopupWindow.dismiss();
                            ActionBarPopupWindow unused = this.this$0.scrimPopupWindow = null;
                        }
                        this.this$0.dismissAvatarPreview(true);
                        GroupCallActivity groupCallActivity = this.this$0;
                        groupCallActivity.processSelectedOption(this.currentParticipant, peerId, ChatObject.canManageCalls(groupCallActivity.currentChat) ? 0 : 5);
                    } else {
                        VoIPService.getSharedInstance().editCallMember(tLObject2, (Boolean) null, (Boolean) null, Integer.valueOf(this.currentParticipant.volume), (Boolean) null, (Runnable) null);
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

    public static class WeavingState {
        public int currentState;
        private float duration;
        private Matrix matrix = new Matrix();
        public Shader shader;
        private float startX;
        private float startY;
        private float targetX = -1.0f;
        private float targetY = -1.0f;
        private float time;

        public WeavingState(int i) {
            this.currentState = i;
        }

        public void update(int i, int i2, int i3, long j, float f) {
            float f2;
            if (this.shader != null) {
                float f3 = this.duration;
                if (f3 == 0.0f || this.time >= f3) {
                    this.duration = (float) (Utilities.random.nextInt(200) + 1500);
                    this.time = 0.0f;
                    if (this.targetX == -1.0f) {
                        setTarget();
                    }
                    this.startX = this.targetX;
                    this.startY = this.targetY;
                    setTarget();
                }
                float f4 = (float) j;
                float f5 = this.time + ((BlobDrawable.GRADIENT_SPEED_MIN + 0.5f) * f4) + (f4 * BlobDrawable.GRADIENT_SPEED_MAX * 2.0f * f);
                this.time = f5;
                float f6 = this.duration;
                if (f5 > f6) {
                    this.time = f6;
                }
                float interpolation = CubicBezierInterpolator.EASE_OUT.getInterpolation(this.time / f6);
                float f7 = (float) i3;
                float f8 = this.startX;
                float f9 = (((float) i2) + ((f8 + ((this.targetX - f8) * interpolation)) * f7)) - 200.0f;
                float var_ = this.startY;
                float var_ = (((float) i) + (f7 * (var_ + ((this.targetY - var_) * interpolation)))) - 200.0f;
                if (GroupCallActivity.isGradientState(this.currentState)) {
                    f2 = 1.0f;
                } else {
                    f2 = this.currentState == 1 ? 4.0f : 2.5f;
                }
                float dp = (((float) AndroidUtilities.dp(122.0f)) / 400.0f) * f2;
                this.matrix.reset();
                this.matrix.postTranslate(f9, var_);
                this.matrix.postScale(dp, dp, f9 + 200.0f, var_ + 200.0f);
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
            this.drawingForBlur = true;
            this.containerView.draw(canvas);
            this.drawingForBlur = false;
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
        this.accountInstance.getNotificationCenter().removeObserver(this, NotificationCenter.groupCallScreencastStateChanged);
        this.accountInstance.getNotificationCenter().removeObserver(this, NotificationCenter.groupCallSpeakingUsersUpdated);
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
        TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant;
        int i3;
        String str;
        TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant2;
        int i4;
        String str2;
        String str3;
        ChatObject.VideoParticipant videoParticipant;
        int i5;
        int i6;
        int i7 = 0;
        if (i == NotificationCenter.groupCallUpdated) {
            Long l = objArr[1];
            ChatObject.Call call2 = this.call;
            if (call2 != null && call2.call.id == l.longValue()) {
                ChatObject.Call call3 = this.call;
                if (call3.call instanceof TLRPC$TL_groupCallDiscarded) {
                    dismiss();
                    return;
                }
                if (this.creatingServiceTime == 0 && (((i6 = this.muteButtonState) == 7 || i6 == 5 || i6 == 6) && !call3.isScheduled())) {
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
                for (int i8 = 0; i8 < childCount; i8++) {
                    View childAt = this.listView.getChildAt(i8);
                    if (childAt instanceof GroupCallUserCell) {
                        ((GroupCallUserCell) childAt).applyParticipantChanges(true);
                    }
                }
                if (this.scrimView != null) {
                    this.delayedGroupCallUpdated = true;
                } else {
                    applyCallParticipantUpdates();
                }
                updateSubtitle();
                boolean booleanValue = objArr[2].booleanValue();
                if (this.muteButtonState == 4) {
                    i7 = 1;
                }
                updateState(true, booleanValue);
                updateTitle(true);
                if (i7 != 0 && ((i5 = this.muteButtonState) == 1 || i5 == 0)) {
                    getUndoView().showWithAction(0, 38, (Runnable) null);
                    if (VoIPService.getSharedInstance() != null) {
                        VoIPService.getSharedInstance().playAllowTalkSound();
                    }
                }
                if (objArr.length >= 4) {
                    long longValue = objArr[3].longValue();
                    if (longValue == 0) {
                        return;
                    }
                    if (longValue > 0) {
                        TLRPC$User user = this.accountInstance.getMessagesController().getUser(Integer.valueOf((int) longValue));
                        if (this.call.call.participants_count < 250 || UserObject.isContact(user)) {
                            getUndoView().showWithAction(0, 44, (Object) user);
                            return;
                        }
                        return;
                    }
                    TLRPC$Chat chat = this.accountInstance.getMessagesController().getChat(Integer.valueOf((int) (-longValue)));
                    if (this.call.call.participants_count < 250 || !ChatObject.isNotInChat(chat)) {
                        getUndoView().showWithAction(0, 44, (Object) chat);
                    }
                }
            }
        } else if (i == NotificationCenter.groupCallSpeakingUsersUpdated) {
            GroupCallRenderersContainer groupCallRenderersContainer = this.renderersContainer;
            if (groupCallRenderersContainer.inFullscreenMode && this.call != null) {
                boolean autoPinEnabled = groupCallRenderersContainer.autoPinEnabled();
                ChatObject.Call call4 = this.call;
                if (call4 != null) {
                    GroupCallRenderersContainer groupCallRenderersContainer2 = this.renderersContainer;
                    if (groupCallRenderersContainer2.inFullscreenMode && (videoParticipant = groupCallRenderersContainer2.fullscreenParticipant) != null && call4.participants.get(MessageObject.getPeerId(videoParticipant.participant.peer)) == null) {
                        autoPinEnabled = true;
                    }
                }
                if (autoPinEnabled) {
                    ChatObject.VideoParticipant videoParticipant2 = null;
                    for (int i9 = 0; i9 < this.visibleVideoParticipants.size(); i9++) {
                        ChatObject.VideoParticipant videoParticipant3 = this.visibleVideoParticipants.get(i9);
                        if ((this.call.currentSpeakingPeers.get(MessageObject.getPeerId(videoParticipant3.participant.peer), (Object) null) != null) && !videoParticipant3.participant.muted_by_you) {
                            videoParticipant2 = videoParticipant3;
                        }
                    }
                    if (videoParticipant2 != null) {
                        fullscreenFor(videoParticipant2);
                    }
                }
            }
            this.renderersContainer.setVisibleParticipant(true);
            updateSubtitle();
        } else if (i == NotificationCenter.webRtcMicAmplitudeEvent) {
            setMicAmplitude(objArr[0].floatValue());
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
            if (call5 != null && tLRPC$ChatFull.id == (-peerId) && (tLRPC$TL_groupCallParticipant2 = call5.participants.get(peerId)) != null) {
                tLRPC$TL_groupCallParticipant2.about = tLRPC$ChatFull.about;
                applyCallParticipantUpdates();
                AndroidUtilities.updateVisibleRows(this.listView);
                if (this.currentOptionsLayout != null) {
                    while (i7 < this.currentOptionsLayout.getChildCount()) {
                        View childAt2 = this.currentOptionsLayout.getChildAt(i7);
                        if ((childAt2 instanceof ActionBarMenuSubItem) && childAt2.getTag() != null && ((Integer) childAt2.getTag()).intValue() == 10) {
                            ActionBarMenuSubItem actionBarMenuSubItem = (ActionBarMenuSubItem) childAt2;
                            if (TextUtils.isEmpty(tLRPC$TL_groupCallParticipant2.about)) {
                                i4 = NUM;
                                str2 = "VoipAddDescription";
                            } else {
                                i4 = NUM;
                                str2 = "VoipEditDescription";
                            }
                            actionBarMenuSubItem.setTextAndIcon(LocaleController.getString(str2, i4), TextUtils.isEmpty(tLRPC$TL_groupCallParticipant2.about) ? NUM : NUM);
                        }
                        i7++;
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
            long longValue2 = objArr[0].longValue();
            while (i7 < childCount2) {
                RecyclerView.ViewHolder findContainingViewHolder = this.listView.findContainingViewHolder(this.listView.getChildAt(i7));
                if (findContainingViewHolder != null) {
                    View view = findContainingViewHolder.itemView;
                    if (view instanceof GroupCallUserCell) {
                        ((GroupCallUserCell) view).getParticipant().lastVisibleDate = longValue2;
                    }
                }
                i7++;
            }
        } else if (i == NotificationCenter.userInfoDidLoad) {
            Integer num = objArr[0];
            int peerId2 = MessageObject.getPeerId(this.selfPeer);
            if (this.call != null && peerId2 == num.intValue() && (tLRPC$TL_groupCallParticipant = this.call.participants.get(peerId2)) != null) {
                tLRPC$TL_groupCallParticipant.about = objArr[1].about;
                applyCallParticipantUpdates();
                AndroidUtilities.updateVisibleRows(this.listView);
                if (this.currentOptionsLayout != null) {
                    while (i7 < this.currentOptionsLayout.getChildCount()) {
                        View childAt3 = this.currentOptionsLayout.getChildAt(i7);
                        if ((childAt3 instanceof ActionBarMenuSubItem) && childAt3.getTag() != null && ((Integer) childAt3.getTag()).intValue() == 10) {
                            ActionBarMenuSubItem actionBarMenuSubItem2 = (ActionBarMenuSubItem) childAt3;
                            if (TextUtils.isEmpty(tLRPC$TL_groupCallParticipant.about)) {
                                i3 = NUM;
                                str = "VoipAddBio";
                            } else {
                                i3 = NUM;
                                str = "VoipEditBio";
                            }
                            actionBarMenuSubItem2.setTextAndIcon(LocaleController.getString(str, i3), TextUtils.isEmpty(tLRPC$TL_groupCallParticipant.about) ? NUM : NUM);
                        }
                        i7++;
                    }
                }
            }
        } else if (i == NotificationCenter.mainUserInfoChanged) {
            applyCallParticipantUpdates();
            AndroidUtilities.updateVisibleRows(this.listView);
        } else if (i == NotificationCenter.updateInterfaces) {
            if ((objArr[0].intValue() & 16) != 0) {
                applyCallParticipantUpdates();
                AndroidUtilities.updateVisibleRows(this.listView);
            }
        } else if (i == NotificationCenter.groupCallScreencastStateChanged) {
            updateItems();
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

    private void setMicAmplitude(float f) {
        TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant;
        RecyclerView.ViewHolder findViewHolderForAdapterPosition;
        if (VoIPService.getSharedInstance() == null || VoIPService.getSharedInstance().isMicMute()) {
            f = 0.0f;
        }
        setAmplitude((double) (4000.0f * f));
        ChatObject.Call call2 = this.call;
        if (call2 != null && this.listView != null && (tLRPC$TL_groupCallParticipant = call2.participants.get(MessageObject.getPeerId(this.selfPeer))) != null) {
            if (!this.renderersContainer.inFullscreenMode) {
                int indexOf = (this.delayedGroupCallUpdated ? this.oldParticipants : this.call.visibleParticipants).indexOf(tLRPC$TL_groupCallParticipant);
                if (indexOf >= 0 && (findViewHolderForAdapterPosition = this.listView.findViewHolderForAdapterPosition(indexOf + this.listAdapter.usersStartRow)) != null) {
                    View view = findViewHolderForAdapterPosition.itemView;
                    if (view instanceof GroupCallUserCell) {
                        ((GroupCallUserCell) view).setAmplitude((double) (f * 15.0f));
                        if (findViewHolderForAdapterPosition.itemView == this.scrimView && !this.contentFullyOverlayed) {
                            this.containerView.invalidate();
                        }
                    }
                }
            } else {
                for (int i = 0; i < this.fullscreenUsersListView.getChildCount(); i++) {
                    GroupCallFullscreenAdapter.GroupCallUserCell groupCallUserCell = (GroupCallFullscreenAdapter.GroupCallUserCell) this.fullscreenUsersListView.getChildAt(i);
                    if (MessageObject.getPeerId(groupCallUserCell.getParticipant().peer) == MessageObject.getPeerId(tLRPC$TL_groupCallParticipant.peer)) {
                        groupCallUserCell.setAmplitude((double) (f * 15.0f));
                    }
                }
            }
            this.renderersContainer.setAmplitude(tLRPC$TL_groupCallParticipant, f * 15.0f);
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:112:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:37:0x00d7  */
    /* JADX WARNING: Removed duplicated region for block: B:40:0x011a  */
    /* JADX WARNING: Removed duplicated region for block: B:60:0x0171  */
    /* JADX WARNING: Removed duplicated region for block: B:63:0x017a  */
    /* JADX WARNING: Removed duplicated region for block: B:66:0x0189  */
    /* JADX WARNING: Removed duplicated region for block: B:70:0x01a3 A[LOOP:2: B:68:0x019b->B:70:0x01a3, LOOP_END] */
    /* JADX WARNING: Removed duplicated region for block: B:73:0x01bb  */
    /* JADX WARNING: Removed duplicated region for block: B:88:0x01f4  */
    /* JADX WARNING: Removed duplicated region for block: B:92:0x0204  */
    /* JADX WARNING: Removed duplicated region for block: B:95:0x0215  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void applyCallParticipantUpdates() {
        /*
            r17 = this;
            r10 = r17
            org.telegram.ui.Components.voip.GroupCallRenderersContainer r0 = r10.renderersContainer
            boolean r1 = r0.inFullscreenMode
            r11 = 1
            if (r1 == 0) goto L_0x000c
            r0.setVisibleParticipant(r11)
        L_0x000c:
            org.telegram.messenger.ChatObject$Call r0 = r10.call
            if (r0 == 0) goto L_0x0220
            boolean r1 = r10.delayedGroupCallUpdated
            if (r1 == 0) goto L_0x0016
            goto L_0x0220
        L_0x0016:
            org.telegram.tgnet.TLRPC$Peer r0 = r0.selfPeer
            int r0 = org.telegram.messenger.MessageObject.getPeerId(r0)
            org.telegram.tgnet.TLRPC$Peer r1 = r10.selfPeer
            int r1 = org.telegram.messenger.MessageObject.getPeerId(r1)
            if (r0 == r1) goto L_0x0034
            org.telegram.messenger.ChatObject$Call r1 = r10.call
            android.util.SparseArray<org.telegram.tgnet.TLRPC$TL_groupCallParticipant> r1 = r1.participants
            java.lang.Object r0 = r1.get(r0)
            if (r0 == 0) goto L_0x0034
            org.telegram.messenger.ChatObject$Call r0 = r10.call
            org.telegram.tgnet.TLRPC$Peer r0 = r0.selfPeer
            r10.selfPeer = r0
        L_0x0034:
            org.telegram.ui.Components.RecyclerListView r0 = r10.listView
            int r12 = r0.getChildCount()
            r0 = 2147483647(0x7fffffff, float:NaN)
            r13 = 0
            r14 = 0
            r15 = r13
            r1 = 0
            r9 = 0
        L_0x0042:
            if (r1 >= r12) goto L_0x0073
            org.telegram.ui.Components.RecyclerListView r2 = r10.listView
            android.view.View r2 = r2.getChildAt(r1)
            org.telegram.ui.Components.RecyclerListView r3 = r10.listView
            androidx.recyclerview.widget.RecyclerView$ViewHolder r3 = r3.findContainingViewHolder(r2)
            if (r3 == 0) goto L_0x0070
            int r4 = r3.getAdapterPosition()
            r5 = -1
            if (r4 == r5) goto L_0x0070
            int r4 = r3.getLayoutPosition()
            if (r4 == r5) goto L_0x0070
            if (r15 == 0) goto L_0x0067
            int r4 = r2.getTop()
            if (r4 >= r0) goto L_0x0070
        L_0x0067:
            int r9 = r3.getLayoutPosition()
            int r0 = r2.getTop()
            r15 = r2
        L_0x0070:
            int r1 = r1 + 1
            goto L_0x0042
        L_0x0073:
            r17.updateVideoParticipantList()
            org.telegram.ui.GroupCallActivity$UpdateCallback r0 = new org.telegram.ui.GroupCallActivity$UpdateCallback     // Catch:{ Exception -> 0x00c6 }
            org.telegram.ui.GroupCallActivity$ListAdapter r1 = r10.listAdapter     // Catch:{ Exception -> 0x00c6 }
            r0.<init>(r1)     // Catch:{ Exception -> 0x00c6 }
            org.telegram.ui.GroupCallActivity$ListAdapter r1 = r10.listAdapter     // Catch:{ Exception -> 0x00c6 }
            int r2 = r1.addMemberRow     // Catch:{ Exception -> 0x00c6 }
            org.telegram.ui.GroupCallActivity$ListAdapter r1 = r10.listAdapter     // Catch:{ Exception -> 0x00c6 }
            int r3 = r1.usersStartRow     // Catch:{ Exception -> 0x00c6 }
            org.telegram.ui.GroupCallActivity$ListAdapter r1 = r10.listAdapter     // Catch:{ Exception -> 0x00c6 }
            int r4 = r1.usersEndRow     // Catch:{ Exception -> 0x00c6 }
            org.telegram.ui.GroupCallActivity$ListAdapter r1 = r10.listAdapter     // Catch:{ Exception -> 0x00c6 }
            int r5 = r1.invitedStartRow     // Catch:{ Exception -> 0x00c6 }
            org.telegram.ui.GroupCallActivity$ListAdapter r1 = r10.listAdapter     // Catch:{ Exception -> 0x00c6 }
            int r6 = r1.invitedEndRow     // Catch:{ Exception -> 0x00c6 }
            org.telegram.ui.GroupCallActivity$ListAdapter r1 = r10.listAdapter     // Catch:{ Exception -> 0x00c6 }
            int r7 = r1.usersVideoGridStartRow     // Catch:{ Exception -> 0x00c6 }
            org.telegram.ui.GroupCallActivity$ListAdapter r1 = r10.listAdapter     // Catch:{ Exception -> 0x00c6 }
            int r8 = r1.usersVideoGridEndRow     // Catch:{ Exception -> 0x00c6 }
            org.telegram.ui.GroupCallActivity$ListAdapter r1 = r10.listAdapter     // Catch:{ Exception -> 0x00c6 }
            int r16 = r1.videoGridDividerRow     // Catch:{ Exception -> 0x00c6 }
            r1 = r17
            r13 = r9
            r9 = r16
            r1.setOldRows(r2, r3, r4, r5, r6, r7, r8, r9)     // Catch:{ Exception -> 0x00c4 }
            org.telegram.ui.GroupCallActivity$ListAdapter r1 = r10.listAdapter     // Catch:{ Exception -> 0x00c4 }
            r1.updateRows()     // Catch:{ Exception -> 0x00c4 }
            androidx.recyclerview.widget.DiffUtil$Callback r1 = r10.diffUtilsCallback     // Catch:{ Exception -> 0x00c4 }
            androidx.recyclerview.widget.DiffUtil$DiffResult r1 = androidx.recyclerview.widget.DiffUtil.calculateDiff(r1)     // Catch:{ Exception -> 0x00c4 }
            r1.dispatchUpdatesTo((androidx.recyclerview.widget.ListUpdateCallback) r0)     // Catch:{ Exception -> 0x00c4 }
            goto L_0x00d0
        L_0x00c4:
            r0 = move-exception
            goto L_0x00c8
        L_0x00c6:
            r0 = move-exception
            r13 = r9
        L_0x00c8:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            org.telegram.ui.GroupCallActivity$ListAdapter r0 = r10.listAdapter
            r0.notifyDataSetChanged()
        L_0x00d0:
            org.telegram.messenger.ChatObject$Call r0 = r10.call
            r0.saveActiveDates()
            if (r15 == 0) goto L_0x00e7
            org.telegram.ui.Components.FillLastGridLayoutManager r0 = r10.layoutManager
            int r1 = r15.getTop()
            org.telegram.ui.Components.RecyclerListView r2 = r10.listView
            int r2 = r2.getPaddingTop()
            int r1 = r1 - r2
            r0.scrollToPositionWithOffset(r13, r1)
        L_0x00e7:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_groupCallParticipant> r0 = r10.oldParticipants
            r0.clear()
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_groupCallParticipant> r0 = r10.oldParticipants
            org.telegram.messenger.ChatObject$Call r1 = r10.call
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_groupCallParticipant> r1 = r1.visibleParticipants
            r0.addAll(r1)
            java.util.ArrayList<org.telegram.messenger.ChatObject$VideoParticipant> r0 = r10.oldVideoParticipants
            r0.clear()
            java.util.ArrayList<org.telegram.messenger.ChatObject$VideoParticipant> r0 = r10.oldVideoParticipants
            java.util.ArrayList<org.telegram.messenger.ChatObject$VideoParticipant> r1 = r10.visibleVideoParticipants
            r0.addAll(r1)
            java.util.ArrayList<java.lang.Integer> r0 = r10.oldInvited
            r0.clear()
            java.util.ArrayList<java.lang.Integer> r0 = r10.oldInvited
            org.telegram.messenger.ChatObject$Call r1 = r10.call
            java.util.ArrayList<java.lang.Integer> r1 = r1.invitedUsers
            r0.addAll(r1)
            org.telegram.ui.GroupCallActivity$ListAdapter r0 = r10.listAdapter
            int r0 = r0.getItemCount()
            r10.oldCount = r0
            r0 = 0
        L_0x0118:
            if (r0 >= r12) goto L_0x0162
            org.telegram.ui.Components.RecyclerListView r1 = r10.listView
            android.view.View r1 = r1.getChildAt(r0)
            boolean r2 = r1 instanceof org.telegram.ui.Cells.GroupCallUserCell
            if (r2 != 0) goto L_0x0128
            boolean r3 = r1 instanceof org.telegram.ui.Cells.GroupCallInvitedCell
            if (r3 == 0) goto L_0x015f
        L_0x0128:
            org.telegram.ui.Components.RecyclerListView r3 = r10.listView
            androidx.recyclerview.widget.RecyclerView$ViewHolder r3 = r3.findContainingViewHolder(r1)
            if (r3 == 0) goto L_0x015f
            if (r2 == 0) goto L_0x0149
            org.telegram.ui.Cells.GroupCallUserCell r1 = (org.telegram.ui.Cells.GroupCallUserCell) r1
            int r2 = r3.getAdapterPosition()
            org.telegram.ui.GroupCallActivity$ListAdapter r3 = r10.listAdapter
            int r3 = r3.getItemCount()
            int r3 = r3 + -2
            if (r2 == r3) goto L_0x0144
            r2 = 1
            goto L_0x0145
        L_0x0144:
            r2 = 0
        L_0x0145:
            r1.setDrawDivider(r2)
            goto L_0x015f
        L_0x0149:
            org.telegram.ui.Cells.GroupCallInvitedCell r1 = (org.telegram.ui.Cells.GroupCallInvitedCell) r1
            int r2 = r3.getAdapterPosition()
            org.telegram.ui.GroupCallActivity$ListAdapter r3 = r10.listAdapter
            int r3 = r3.getItemCount()
            int r3 = r3 + -2
            if (r2 == r3) goto L_0x015b
            r2 = 1
            goto L_0x015c
        L_0x015b:
            r2 = 0
        L_0x015c:
            r1.setDrawDivider(r2)
        L_0x015f:
            int r0 = r0 + 1
            goto L_0x0118
        L_0x0162:
            org.telegram.ui.Components.GroupCallFullscreenAdapter r0 = r10.fullscreenAdapter
            org.telegram.ui.Components.RecyclerListView r1 = r10.fullscreenUsersListView
            r0.update(r11, r1)
            org.telegram.ui.Components.RecyclerListView r0 = r10.fullscreenUsersListView
            int r0 = r0.getVisibility()
            if (r0 != 0) goto L_0x0176
            org.telegram.ui.Components.RecyclerListView r0 = r10.fullscreenUsersListView
            org.telegram.messenger.AndroidUtilities.updateVisibleRows(r0)
        L_0x0176:
            boolean r0 = isTabletMode
            if (r0 == 0) goto L_0x0181
            org.telegram.ui.GroupCallTabletGridAdapter r0 = r10.tabletGridAdapter
            org.telegram.ui.Components.RecyclerListView r1 = r10.tabletVideoGridView
            r0.update(r11, r1)
        L_0x0181:
            org.telegram.ui.Components.RecyclerListView r0 = r10.listView
            int r0 = r0.getVisibility()
            if (r0 != 0) goto L_0x018e
            org.telegram.ui.Components.RecyclerListView r0 = r10.listView
            org.telegram.messenger.AndroidUtilities.updateVisibleRows(r0)
        L_0x018e:
            java.util.ArrayList<org.telegram.ui.Components.voip.GroupCallMiniTextureView> r0 = r10.attachedRenderersTmp
            r0.clear()
            java.util.ArrayList<org.telegram.ui.Components.voip.GroupCallMiniTextureView> r0 = r10.attachedRenderersTmp
            java.util.ArrayList<org.telegram.ui.Components.voip.GroupCallMiniTextureView> r1 = r10.attachedRenderers
            r0.addAll(r1)
            r0 = 0
        L_0x019b:
            java.util.ArrayList<org.telegram.ui.Components.voip.GroupCallMiniTextureView> r1 = r10.attachedRenderersTmp
            int r1 = r1.size()
            if (r0 >= r1) goto L_0x01b1
            java.util.ArrayList<org.telegram.ui.Components.voip.GroupCallMiniTextureView> r1 = r10.attachedRenderersTmp
            java.lang.Object r1 = r1.get(r0)
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r1 = (org.telegram.ui.Components.voip.GroupCallMiniTextureView) r1
            r1.updateAttachState(r11)
            int r0 = r0 + 1
            goto L_0x019b
        L_0x01b1:
            org.telegram.ui.Components.voip.GroupCallRenderersContainer r0 = r10.renderersContainer
            boolean r0 = r0.autoPinEnabled()
            org.telegram.messenger.ChatObject$Call r1 = r10.call
            if (r1 == 0) goto L_0x01d6
            org.telegram.ui.Components.voip.GroupCallRenderersContainer r2 = r10.renderersContainer
            boolean r3 = r2.inFullscreenMode
            if (r3 == 0) goto L_0x01d6
            org.telegram.messenger.ChatObject$VideoParticipant r2 = r2.fullscreenParticipant
            if (r2 == 0) goto L_0x01d6
            android.util.SparseArray<org.telegram.tgnet.TLRPC$TL_groupCallParticipant> r1 = r1.participants
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r2 = r2.participant
            org.telegram.tgnet.TLRPC$Peer r2 = r2.peer
            int r2 = org.telegram.messenger.MessageObject.getPeerId(r2)
            java.lang.Object r1 = r1.get(r2)
            if (r1 != 0) goto L_0x01d6
            r0 = 1
        L_0x01d6:
            org.telegram.ui.Components.voip.GroupCallRenderersContainer r1 = r10.renderersContainer
            boolean r2 = r1.inFullscreenMode
            if (r2 == 0) goto L_0x0208
            org.telegram.messenger.ChatObject$VideoParticipant r1 = r1.fullscreenParticipant
            if (r1 == 0) goto L_0x0208
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r2 = r1.participant
            boolean r1 = r1.presentation
            org.telegram.messenger.ChatObject$Call r3 = r10.call
            boolean r1 = videoIsActive(r2, r1, r3)
            if (r1 != 0) goto L_0x0208
            java.util.ArrayList<org.telegram.messenger.ChatObject$VideoParticipant> r1 = r10.visibleVideoParticipants
            boolean r1 = r1.isEmpty()
            if (r1 != 0) goto L_0x0202
            if (r0 == 0) goto L_0x0201
            java.util.ArrayList<org.telegram.messenger.ChatObject$VideoParticipant> r0 = r10.visibleVideoParticipants
            java.lang.Object r0 = r0.get(r14)
            org.telegram.messenger.ChatObject$VideoParticipant r0 = (org.telegram.messenger.ChatObject.VideoParticipant) r0
            r10.fullscreenFor(r0)
        L_0x0201:
            r14 = 1
        L_0x0202:
            if (r14 != 0) goto L_0x0208
            r1 = 0
            r10.fullscreenFor(r1)
        L_0x0208:
            org.telegram.messenger.ChatObject$Call r0 = r10.call
            java.util.ArrayList<org.telegram.messenger.ChatObject$VideoParticipant> r0 = r0.visibleVideoParticipants
            boolean r0 = r0.isEmpty()
            r0 = r0 ^ r11
            boolean r1 = r10.hasVideo
            if (r0 == r1) goto L_0x0220
            r10.hasVideo = r0
            boolean r0 = isTabletMode
            if (r0 == 0) goto L_0x0220
            android.view.ViewGroup r0 = r10.containerView
            r0.requestLayout()
        L_0x0220:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.GroupCallActivity.applyCallParticipantUpdates():void");
    }

    private void updateVideoParticipantList() {
        this.visibleVideoParticipants.clear();
        if (!isTabletMode) {
            this.visibleVideoParticipants.addAll(this.call.visibleVideoParticipants);
        } else if (this.renderersContainer.inFullscreenMode) {
            this.visibleVideoParticipants.addAll(this.call.visibleVideoParticipants);
            ChatObject.VideoParticipant videoParticipant = this.renderersContainer.fullscreenParticipant;
            if (videoParticipant != null) {
                this.visibleVideoParticipants.remove(videoParticipant);
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
            this.screenShareItem.setVisibility(8);
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
                    this.screenItem.setVisibility(8);
                } else {
                    this.recordItem.setVisibility(0);
                }
                ChatObject.Call call3 = this.call;
                if (!call3.canStreamVideo || call3.isScheduled() || Build.VERSION.SDK_INT < 21) {
                    this.screenItem.setVisibility(8);
                } else {
                    this.screenItem.setVisibility(0);
                }
                this.screenShareItem.setVisibility(8);
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
                if (VoIPService.getSharedInstance() == null || VoIPService.getSharedInstance().getVideoState(true) != 2) {
                    this.screenItem.setTextAndIcon(LocaleController.getString("VoipChatStartScreenCapture", NUM), NUM);
                } else {
                    this.screenItem.setTextAndIcon(LocaleController.getString("VoipChatStopScreenCapture", NUM), NUM);
                }
                updateRecordCallText();
                z = true;
            } else {
                TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant = this.call.participants.get(MessageObject.getPeerId(this.selfPeer));
                boolean z2 = tLRPC$TL_groupCallParticipant != null && !tLRPC$TL_groupCallParticipant.can_self_unmute && tLRPC$TL_groupCallParticipant.muted && !ChatObject.canManageCalls(this.currentChat);
                if (Build.VERSION.SDK_INT < 21 || z2 || !this.call.canStreamVideo) {
                    this.screenShareItem.setVisibility(8);
                    this.screenItem.setVisibility(8);
                } else {
                    if (VoIPService.getSharedInstance() == null || VoIPService.getSharedInstance().getVideoState(true) != 2) {
                        this.screenItem.setTextAndIcon(LocaleController.getString("VoipChatStartScreenCapture", NUM), NUM);
                        this.screenItem.setContentDescription(LocaleController.getString("VoipChatStartScreenCapture", NUM));
                        this.screenShareItem.setVisibility(8);
                        this.screenItem.setVisibility(0);
                    } else {
                        this.screenShareItem.setVisibility(8);
                        this.screenItem.setVisibility(0);
                        this.screenItem.setTextAndIcon(LocaleController.getString("VoipChatStopScreenCapture", NUM), NUM);
                        this.screenItem.setContentDescription(LocaleController.getString("VoipChatStopScreenCapture", NUM));
                    }
                    z = true;
                }
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
            if (this.soundButton.getVisibility() != 0) {
                this.soundItem.setVisibility(0);
                this.soundItemDivider.setVisibility(z ? 0 : 8);
            } else {
                this.soundItem.setVisibility(8);
                this.soundItemDivider.setVisibility(8);
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
                    this.accountSwitchImageView.setForUserOrChat(user, this.accountSwitchAvatarDrawable);
                } else {
                    TLRPC$Chat chat2 = this.accountInstance.getMessagesController().getChat(Integer.valueOf(-peerId));
                    this.accountSwitchAvatarDrawable.setInfo(chat2);
                    this.accountSwitchImageView.setForUserOrChat(chat2, this.accountSwitchAvatarDrawable);
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
            r10 = r43
            r11 = r45
            r12 = 0
            r8.<init>(r9, r12)
            r0 = 2
            android.widget.TextView[] r1 = new android.widget.TextView[r0]
            r8.muteLabel = r1
            org.telegram.ui.Components.UndoView[] r1 = new org.telegram.ui.Components.UndoView[r0]
            r8.undoView = r1
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
            r8.visibleVideoParticipants = r1
            android.graphics.RectF r1 = new android.graphics.RectF
            r1.<init>()
            r8.rect = r1
            android.graphics.Paint r1 = new android.graphics.Paint
            r13 = 1
            r1.<init>(r13)
            r8.listViewBackgroundPaint = r1
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
            r8.oldParticipants = r1
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
            r8.oldVideoParticipants = r1
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
            r8.oldInvited = r1
            r8.muteButtonState = r12
            android.graphics.Paint r1 = new android.graphics.Paint
            r2 = 7
            r1.<init>(r2)
            r8.paint = r1
            android.graphics.Paint r1 = new android.graphics.Paint
            r1.<init>(r2)
            r8.paintTmp = r1
            android.graphics.Paint r1 = new android.graphics.Paint
            r1.<init>(r13)
            r8.leaveBackgroundPaint = r1
            r14 = 8
            org.telegram.ui.GroupCallActivity$WeavingState[] r1 = new org.telegram.ui.GroupCallActivity.WeavingState[r14]
            r8.states = r1
            r15 = 1065353216(0x3var_, float:1.0)
            r8.switchProgress = r15
            r1 = 200(0xc8, float:2.8E-43)
            r8.shaderBitmapSize = r1
            r8.invalidateColors = r13
            r1 = 3
            int[] r1 = new int[r1]
            r8.colorsTmp = r1
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
            r8.attachedRenderers = r1
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
            r8.attachedRenderersTmp = r1
            org.telegram.ui.Components.voip.CellFlickerDrawable r1 = new org.telegram.ui.Components.voip.CellFlickerDrawable
            r1.<init>()
            r8.cellFlickerDrawable = r1
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
            r8.statusIconPool = r1
            java.util.HashMap r1 = new java.util.HashMap
            r1.<init>()
            r8.buttonsAnimationParamsX = r1
            java.util.HashMap r1 = new java.util.HashMap
            r1.<init>()
            r8.buttonsAnimationParamsY = r1
            org.telegram.ui.GroupCallActivity$1 r1 = new org.telegram.ui.GroupCallActivity$1
            r1.<init>()
            r8.updateSchedeulRunnable = r1
            org.telegram.ui.-$$Lambda$GroupCallActivity$Fejzw3-BitRkLCnwqEMTIYvTsgw r1 = org.telegram.ui.$$Lambda$GroupCallActivity$Fejzw3BitRkLCnwqEMTIYvTsgw.INSTANCE
            r8.unmuteRunnable = r1
            org.telegram.ui.-$$Lambda$GroupCallActivity$kOH_8vLsAK1_zQgyxcm7-Ylrcfc r1 = new org.telegram.ui.-$$Lambda$GroupCallActivity$kOH_8vLsAK1_zQgyxcm7-Ylrcfc
            r1.<init>()
            r8.pressRunnable = r1
            int[] r1 = new int[r0]
            r8.gradientColors = r1
            r8.listViewVideoVisibility = r13
            java.lang.String[] r1 = new java.lang.String[r0]
            r8.invites = r1
            r7 = -1
            r8.popupAnimationIndex = r7
            org.telegram.ui.GroupCallActivity$55 r1 = new org.telegram.ui.GroupCallActivity$55
            r1.<init>()
            r8.diffUtilsCallback = r1
            r6 = r42
            r8.accountInstance = r6
            r8.call = r10
            r8.schedulePeer = r11
            r5 = r44
            r8.currentChat = r5
            r1 = r47
            r8.scheduledHash = r1
            int r1 = r42.getCurrentAccount()
            r8.currentAccount = r1
            r1 = r46
            r8.scheduleHasFewPeers = r1
            r8.fullWidth = r13
            isTabletMode = r12
            isLandscapeMode = r12
            paused = r12
            org.telegram.ui.GroupCallActivity$3 r1 = new org.telegram.ui.GroupCallActivity$3
            r1.<init>()
            r8.setDelegate(r1)
            r8.drawNavigationBar = r13
            int r1 = android.os.Build.VERSION.SDK_INT
            r2 = 30
            if (r1 < r2) goto L_0x00f8
            android.view.Window r1 = r40.getWindow()
            r2 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            r1.setNavigationBarColor(r2)
        L_0x00f8:
            r8.scrollNavBar = r13
            r1 = 0
            r8.navBarColorKey = r1
            org.telegram.ui.GroupCallActivity$4 r2 = new org.telegram.ui.GroupCallActivity$4
            r2.<init>()
            r8.scrimPaint = r2
            org.telegram.ui.-$$Lambda$GroupCallActivity$vH22aYH9Z49k0KlgGDw4ZCDf7pg r2 = new org.telegram.ui.-$$Lambda$GroupCallActivity$vH22aYH9Z49k0KlgGDw4ZCDf7pg
            r2.<init>()
            r8.setOnDismissListener(r2)
            r2 = 75
            r8.setDimBehindAlpha(r2)
            org.telegram.ui.GroupCallActivity$ListAdapter r2 = new org.telegram.ui.GroupCallActivity$ListAdapter
            r2.<init>(r9)
            r8.listAdapter = r2
            org.telegram.ui.Components.RecordStatusDrawable r2 = new org.telegram.ui.Components.RecordStatusDrawable
            r2.<init>(r13)
            java.lang.String r3 = "voipgroup_speakingText"
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            r2.setColor(r3)
            r2.start()
            org.telegram.ui.GroupCallActivity$5 r3 = new org.telegram.ui.GroupCallActivity$5
            r3.<init>(r9, r2)
            r8.actionBar = r3
            java.lang.String r2 = ""
            r3.setSubtitle(r2)
            org.telegram.ui.ActionBar.ActionBar r2 = r8.actionBar
            org.telegram.ui.ActionBar.SimpleTextView r2 = r2.getSubtitleTextView()
            r2.setVisibility(r12)
            org.telegram.ui.ActionBar.ActionBar r2 = r8.actionBar
            r2.createAdditionalSubtitleTextView()
            org.telegram.ui.ActionBar.ActionBar r2 = r8.actionBar
            org.telegram.ui.ActionBar.SimpleTextView r2 = r2.getAdditionalSubtitleTextView()
            boolean r3 = r8.drawSpeakingSubtitle
            org.telegram.messenger.AndroidUtilities.updateViewVisibilityAnimated(r2, r3, r15, r12)
            org.telegram.ui.ActionBar.ActionBar r2 = r8.actionBar
            org.telegram.ui.ActionBar.SimpleTextView r2 = r2.getAdditionalSubtitleTextView()
            java.lang.String r3 = "voipgroup_speakingText"
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            r2.setTextColor(r3)
            org.telegram.ui.ActionBar.ActionBar r2 = r8.actionBar
            java.lang.String r3 = "voipgroup_lastSeenTextUnscrolled"
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            r2.setSubtitleColor(r3)
            org.telegram.ui.ActionBar.ActionBar r2 = r8.actionBar
            r3 = 2131165461(0x7var_, float:1.794514E38)
            r2.setBackButtonImage(r3)
            org.telegram.ui.ActionBar.ActionBar r2 = r8.actionBar
            r2.setOccupyStatusBar(r12)
            org.telegram.ui.ActionBar.ActionBar r2 = r8.actionBar
            r2.setAllowOverlayTitle(r12)
            org.telegram.ui.ActionBar.ActionBar r2 = r8.actionBar
            java.lang.String r3 = "voipgroup_actionBarItems"
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            r2.setItemsColor(r4, r12)
            org.telegram.ui.ActionBar.ActionBar r2 = r8.actionBar
            java.lang.String r4 = "actionBarActionModeDefaultSelector"
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r4)
            r2.setItemsBackgroundColor(r4, r12)
            org.telegram.ui.ActionBar.ActionBar r2 = r8.actionBar
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            r2.setTitleColor(r4)
            org.telegram.ui.ActionBar.ActionBar r2 = r8.actionBar
            java.lang.String r4 = "voipgroup_lastSeenTextUnscrolled"
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r4)
            r2.setSubtitleColor(r4)
            org.telegram.ui.ActionBar.ActionBar r2 = r8.actionBar
            org.telegram.ui.GroupCallActivity$6 r4 = new org.telegram.ui.GroupCallActivity$6
            r4.<init>(r9)
            r2.setActionBarMenuOnItemClick(r4)
            if (r11 == 0) goto L_0x01b2
            r4 = r11
            goto L_0x01bb
        L_0x01b2:
            org.telegram.messenger.voip.VoIPService r2 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            org.telegram.tgnet.TLRPC$InputPeer r2 = r2.getGroupCallPeer()
            r4 = r2
        L_0x01bb:
            if (r4 != 0) goto L_0x01d1
            org.telegram.tgnet.TLRPC$TL_peerUser r2 = new org.telegram.tgnet.TLRPC$TL_peerUser
            r2.<init>()
            r8.selfPeer = r2
            org.telegram.messenger.AccountInstance r15 = r8.accountInstance
            org.telegram.messenger.UserConfig r15 = r15.getUserConfig()
            int r15 = r15.getClientUserId()
            r2.user_id = r15
            goto L_0x0200
        L_0x01d1:
            boolean r2 = r4 instanceof org.telegram.tgnet.TLRPC$TL_inputPeerChannel
            if (r2 == 0) goto L_0x01e1
            org.telegram.tgnet.TLRPC$TL_peerChannel r2 = new org.telegram.tgnet.TLRPC$TL_peerChannel
            r2.<init>()
            r8.selfPeer = r2
            int r15 = r4.channel_id
            r2.channel_id = r15
            goto L_0x0200
        L_0x01e1:
            boolean r2 = r4 instanceof org.telegram.tgnet.TLRPC$TL_inputPeerUser
            if (r2 == 0) goto L_0x01f1
            org.telegram.tgnet.TLRPC$TL_peerUser r2 = new org.telegram.tgnet.TLRPC$TL_peerUser
            r2.<init>()
            r8.selfPeer = r2
            int r15 = r4.user_id
            r2.user_id = r15
            goto L_0x0200
        L_0x01f1:
            boolean r2 = r4 instanceof org.telegram.tgnet.TLRPC$TL_inputPeerChat
            if (r2 == 0) goto L_0x0200
            org.telegram.tgnet.TLRPC$TL_peerChat r2 = new org.telegram.tgnet.TLRPC$TL_peerChat
            r2.<init>()
            r8.selfPeer = r2
            int r15 = r4.chat_id
            r2.chat_id = r15
        L_0x0200:
            org.telegram.ui.-$$Lambda$GroupCallActivity$CDAstUeY1nlHMbUdC8gMFgpMZ6s r2 = new org.telegram.ui.-$$Lambda$GroupCallActivity$CDAstUeY1nlHMbUdC8gMFgpMZ6s
            r2.<init>()
            org.telegram.messenger.voip.VoIPService.audioLevelsCallback = r2
            org.telegram.messenger.AccountInstance r2 = r8.accountInstance
            org.telegram.messenger.NotificationCenter r2 = r2.getNotificationCenter()
            int r15 = org.telegram.messenger.NotificationCenter.groupCallUpdated
            r2.addObserver(r8, r15)
            org.telegram.messenger.AccountInstance r2 = r8.accountInstance
            org.telegram.messenger.NotificationCenter r2 = r2.getNotificationCenter()
            int r15 = org.telegram.messenger.NotificationCenter.needShowAlert
            r2.addObserver(r8, r15)
            org.telegram.messenger.AccountInstance r2 = r8.accountInstance
            org.telegram.messenger.NotificationCenter r2 = r2.getNotificationCenter()
            int r15 = org.telegram.messenger.NotificationCenter.chatInfoDidLoad
            r2.addObserver(r8, r15)
            org.telegram.messenger.AccountInstance r2 = r8.accountInstance
            org.telegram.messenger.NotificationCenter r2 = r2.getNotificationCenter()
            int r15 = org.telegram.messenger.NotificationCenter.didLoadChatAdmins
            r2.addObserver(r8, r15)
            org.telegram.messenger.AccountInstance r2 = r8.accountInstance
            org.telegram.messenger.NotificationCenter r2 = r2.getNotificationCenter()
            int r15 = org.telegram.messenger.NotificationCenter.applyGroupCallVisibleParticipants
            r2.addObserver(r8, r15)
            org.telegram.messenger.AccountInstance r2 = r8.accountInstance
            org.telegram.messenger.NotificationCenter r2 = r2.getNotificationCenter()
            int r15 = org.telegram.messenger.NotificationCenter.userInfoDidLoad
            r2.addObserver(r8, r15)
            org.telegram.messenger.AccountInstance r2 = r8.accountInstance
            org.telegram.messenger.NotificationCenter r2 = r2.getNotificationCenter()
            int r15 = org.telegram.messenger.NotificationCenter.mainUserInfoChanged
            r2.addObserver(r8, r15)
            org.telegram.messenger.AccountInstance r2 = r8.accountInstance
            org.telegram.messenger.NotificationCenter r2 = r2.getNotificationCenter()
            int r15 = org.telegram.messenger.NotificationCenter.updateInterfaces
            r2.addObserver(r8, r15)
            org.telegram.messenger.AccountInstance r2 = r8.accountInstance
            org.telegram.messenger.NotificationCenter r2 = r2.getNotificationCenter()
            int r15 = org.telegram.messenger.NotificationCenter.groupCallScreencastStateChanged
            r2.addObserver(r8, r15)
            org.telegram.messenger.AccountInstance r2 = r8.accountInstance
            org.telegram.messenger.NotificationCenter r2 = r2.getNotificationCenter()
            int r15 = org.telegram.messenger.NotificationCenter.groupCallSpeakingUsersUpdated
            r2.addObserver(r8, r15)
            org.telegram.messenger.NotificationCenter r2 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r15 = org.telegram.messenger.NotificationCenter.webRtcMicAmplitudeEvent
            r2.addObserver(r8, r15)
            org.telegram.messenger.NotificationCenter r2 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r15 = org.telegram.messenger.NotificationCenter.didEndCall
            r2.addObserver(r8, r15)
            android.content.res.Resources r2 = r41.getResources()
            r15 = 2131166022(0x7var_, float:1.7946278E38)
            android.graphics.drawable.Drawable r2 = r2.getDrawable(r15)
            android.graphics.drawable.Drawable r2 = r2.mutate()
            r8.shadowDrawable = r2
            org.telegram.ui.Components.RLottieDrawable r2 = new org.telegram.ui.Components.RLottieDrawable
            r17 = 2131558523(0x7f0d007b, float:1.8742364E38)
            r15 = 1116733440(0x42900000, float:72.0)
            int r19 = org.telegram.messenger.AndroidUtilities.dp(r15)
            int r20 = org.telegram.messenger.AndroidUtilities.dp(r15)
            r21 = 1
            r22 = 0
            java.lang.String r18 = "NUM"
            r16 = r2
            r16.<init>((int) r17, (java.lang.String) r18, (int) r19, (int) r20, (boolean) r21, (int[]) r22)
            r8.bigMicDrawable = r2
            org.telegram.ui.Components.RLottieDrawable r2 = new org.telegram.ui.Components.RLottieDrawable
            r24 = 2131558443(0x7f0d002b, float:1.8742202E38)
            int r26 = org.telegram.messenger.AndroidUtilities.dp(r15)
            int r27 = org.telegram.messenger.AndroidUtilities.dp(r15)
            r28 = 1
            r29 = 0
            java.lang.String r25 = "NUM"
            r23 = r2
            r23.<init>((int) r24, (java.lang.String) r25, (int) r26, (int) r27, (boolean) r28, (int[]) r29)
            r8.handDrawables = r2
            org.telegram.ui.GroupCallActivity$7 r2 = new org.telegram.ui.GroupCallActivity$7
            r2.<init>(r9)
            r8.containerView = r2
            r2.setFocusable(r13)
            android.view.ViewGroup r2 = r8.containerView
            r2.setFocusableInTouchMode(r13)
            android.view.ViewGroup r2 = r8.containerView
            r2.setWillNotDraw(r12)
            android.view.ViewGroup r2 = r8.containerView
            int r15 = r8.backgroundPaddingLeft
            r2.setPadding(r15, r12, r15, r12)
            android.view.ViewGroup r2 = r8.containerView
            r2.setKeepScreenOn(r13)
            android.view.ViewGroup r2 = r8.containerView
            r2.setClipChildren(r12)
            java.lang.String r15 = "fonts/rmedium.ttf"
            r2 = 17
            if (r11 == 0) goto L_0x0398
            org.telegram.ui.ActionBar.SimpleTextView r0 = new org.telegram.ui.ActionBar.SimpleTextView
            r0.<init>(r9)
            r8.scheduleStartInTextView = r0
            r0.setGravity(r2)
            org.telegram.ui.ActionBar.SimpleTextView r0 = r8.scheduleStartInTextView
            r0.setTextColor(r7)
            org.telegram.ui.ActionBar.SimpleTextView r0 = r8.scheduleStartInTextView
            android.graphics.Typeface r14 = org.telegram.messenger.AndroidUtilities.getTypeface(r15)
            r0.setTypeface(r14)
            org.telegram.ui.ActionBar.SimpleTextView r0 = r8.scheduleStartInTextView
            r14 = 18
            r0.setTextSize(r14)
            org.telegram.ui.ActionBar.SimpleTextView r0 = r8.scheduleStartInTextView
            r14 = 2131628060(0x7f0e101c, float:1.8883402E38)
            java.lang.String r13 = "VoipChatStartsIn"
            java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r13, r14)
            r0.setText(r13)
            android.view.ViewGroup r0 = r8.containerView
            org.telegram.ui.ActionBar.SimpleTextView r13 = r8.scheduleStartInTextView
            r19 = -2
            r20 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r21 = 49
            r22 = 1101529088(0x41a80000, float:21.0)
            r23 = 0
            r24 = 1101529088(0x41a80000, float:21.0)
            r25 = 1134264320(0x439b8000, float:311.0)
            android.widget.FrameLayout$LayoutParams r14 = org.telegram.ui.Components.LayoutHelper.createFrame(r19, r20, r21, r22, r23, r24, r25)
            r0.addView(r13, r14)
            org.telegram.ui.GroupCallActivity$8 r0 = new org.telegram.ui.GroupCallActivity$8
            r0.<init>(r9)
            r8.scheduleTimeTextView = r0
            r0.setGravity(r2)
            org.telegram.ui.ActionBar.SimpleTextView r0 = r8.scheduleTimeTextView
            r0.setTextColor(r7)
            org.telegram.ui.ActionBar.SimpleTextView r0 = r8.scheduleTimeTextView
            android.graphics.Typeface r13 = org.telegram.messenger.AndroidUtilities.getTypeface(r15)
            r0.setTypeface(r13)
            org.telegram.ui.ActionBar.SimpleTextView r0 = r8.scheduleTimeTextView
            r13 = 60
            r0.setTextSize(r13)
            android.view.ViewGroup r0 = r8.containerView
            org.telegram.ui.ActionBar.SimpleTextView r13 = r8.scheduleTimeTextView
            r25 = 1130823680(0x43670000, float:231.0)
            android.widget.FrameLayout$LayoutParams r14 = org.telegram.ui.Components.LayoutHelper.createFrame(r19, r20, r21, r22, r23, r24, r25)
            r0.addView(r13, r14)
            org.telegram.ui.ActionBar.SimpleTextView r0 = new org.telegram.ui.ActionBar.SimpleTextView
            r0.<init>(r9)
            r8.scheduleStartAtTextView = r0
            r0.setGravity(r2)
            org.telegram.ui.ActionBar.SimpleTextView r0 = r8.scheduleStartAtTextView
            r0.setTextColor(r7)
            org.telegram.ui.ActionBar.SimpleTextView r0 = r8.scheduleStartAtTextView
            android.graphics.Typeface r13 = org.telegram.messenger.AndroidUtilities.getTypeface(r15)
            r0.setTypeface(r13)
            org.telegram.ui.ActionBar.SimpleTextView r0 = r8.scheduleStartAtTextView
            r13 = 18
            r0.setTextSize(r13)
            android.view.ViewGroup r0 = r8.containerView
            org.telegram.ui.ActionBar.SimpleTextView r13 = r8.scheduleStartAtTextView
            r25 = 1128857600(0x43490000, float:201.0)
            android.widget.FrameLayout$LayoutParams r14 = org.telegram.ui.Components.LayoutHelper.createFrame(r19, r20, r21, r22, r23, r24, r25)
            r0.addView(r13, r14)
        L_0x0398:
            org.telegram.ui.GroupCallActivity$9 r0 = new org.telegram.ui.GroupCallActivity$9
            r0.<init>(r9)
            r8.listView = r0
            r0.setClipToPadding(r12)
            org.telegram.ui.Components.RecyclerListView r0 = r8.listView
            r0.setClipChildren(r12)
            org.telegram.ui.GroupCallActivity$GroupCallItemAnimator r0 = new org.telegram.ui.GroupCallActivity$GroupCallItemAnimator
            r0.<init>()
            r8.itemAnimator = r0
            org.telegram.ui.Components.CubicBezierInterpolator r13 = org.telegram.ui.Components.CubicBezierInterpolator.DEFAULT
            r0.setTranslationInterpolator(r13)
            org.telegram.ui.GroupCallActivity$GroupCallItemAnimator r0 = r8.itemAnimator
            r1 = 350(0x15e, double:1.73E-321)
            r0.setRemoveDuration(r1)
            org.telegram.ui.GroupCallActivity$GroupCallItemAnimator r0 = r8.itemAnimator
            r0.setAddDuration(r1)
            org.telegram.ui.GroupCallActivity$GroupCallItemAnimator r0 = r8.itemAnimator
            r0.setMoveDuration(r1)
            org.telegram.ui.GroupCallActivity$GroupCallItemAnimator r0 = r8.itemAnimator
            r0.setDelayAnimations(r12)
            org.telegram.ui.Components.RecyclerListView r0 = r8.listView
            org.telegram.ui.GroupCallActivity$GroupCallItemAnimator r14 = r8.itemAnimator
            r0.setItemAnimator(r14)
            org.telegram.ui.Components.RecyclerListView r0 = r8.listView
            org.telegram.ui.GroupCallActivity$10 r14 = new org.telegram.ui.GroupCallActivity$10
            r14.<init>()
            r0.setOnScrollListener(r14)
            org.telegram.ui.Components.RecyclerListView r0 = r8.listView
            r0.setVerticalScrollBarEnabled(r12)
            org.telegram.ui.Components.RecyclerListView r0 = r8.listView
            org.telegram.ui.Components.FillLastGridLayoutManager r14 = new org.telegram.ui.Components.FillLastGridLayoutManager
            android.content.Context r21 = r40.getContext()
            boolean r20 = isLandscapeMode
            r7 = 6
            if (r20 == 0) goto L_0x03ef
            r22 = 6
            goto L_0x03f1
        L_0x03ef:
            r22 = 2
        L_0x03f1:
            r23 = 1
            r24 = 0
            r25 = 0
            org.telegram.ui.Components.RecyclerListView r1 = r8.listView
            r20 = r14
            r26 = r1
            r20.<init>(r21, r22, r23, r24, r25, r26)
            r8.layoutManager = r14
            r0.setLayoutManager(r14)
            org.telegram.ui.Components.FillLastGridLayoutManager r0 = r8.layoutManager
            org.telegram.ui.GroupCallActivity$11 r1 = new org.telegram.ui.GroupCallActivity$11
            r1.<init>()
            r8.spanSizeLookup = r1
            r0.setSpanSizeLookup(r1)
            org.telegram.ui.Components.RecyclerListView r0 = r8.listView
            org.telegram.ui.GroupCallActivity$12 r1 = new org.telegram.ui.GroupCallActivity$12
            r1.<init>()
            r0.addItemDecoration(r1)
            org.telegram.ui.Components.FillLastGridLayoutManager r0 = r8.layoutManager
            r0.setBind(r12)
            android.view.ViewGroup r0 = r8.containerView
            org.telegram.ui.Components.RecyclerListView r1 = r8.listView
            r20 = -1
            r21 = -1082130432(0xffffffffbvar_, float:-1.0)
            r22 = 51
            r23 = 1096810496(0x41600000, float:14.0)
            r24 = 1096810496(0x41600000, float:14.0)
            r25 = 1096810496(0x41600000, float:14.0)
            r26 = 1130823680(0x43670000, float:231.0)
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r20, r21, r22, r23, r24, r25, r26)
            r0.addView(r1, r2)
            org.telegram.ui.Components.RecyclerListView r0 = r8.listView
            org.telegram.ui.GroupCallActivity$ListAdapter r1 = r8.listAdapter
            r0.setAdapter(r1)
            org.telegram.ui.Components.RecyclerListView r0 = r8.listView
            r1 = 13
            r0.setTopBottomSelectorRadius(r1)
            org.telegram.ui.Components.RecyclerListView r0 = r8.listView
            java.lang.String r1 = "voipgroup_listSelector"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.setSelectorDrawableColor(r2)
            org.telegram.ui.Components.RecyclerListView r0 = r8.listView
            org.telegram.ui.-$$Lambda$GroupCallActivity$ZokxvRSLQ4l6C7Xg_p-yCAOmdMQ r2 = new org.telegram.ui.-$$Lambda$GroupCallActivity$ZokxvRSLQ4l6C7Xg_p-yCAOmdMQ
            r2.<init>()
            r0.setOnItemClickListener((org.telegram.ui.Components.RecyclerListView.OnItemClickListenerExtended) r2)
            org.telegram.ui.Components.RecyclerListView r0 = r8.listView
            org.telegram.ui.-$$Lambda$GroupCallActivity$8t-jDhR8Ase3T3dlEEfyuX840Ow r2 = new org.telegram.ui.-$$Lambda$GroupCallActivity$8t-jDhR8Ase3T3dlEEfyuX840Ow
            r2.<init>()
            r0.setOnItemLongClickListener((org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener) r2)
            org.telegram.ui.Components.RecyclerListView r0 = new org.telegram.ui.Components.RecyclerListView
            r0.<init>(r9)
            r8.tabletVideoGridView = r0
            android.view.ViewGroup r2 = r8.containerView
            r25 = 1134690304(0x43a20000, float:324.0)
            r26 = 1096810496(0x41600000, float:14.0)
            android.widget.FrameLayout$LayoutParams r14 = org.telegram.ui.Components.LayoutHelper.createFrame(r20, r21, r22, r23, r24, r25, r26)
            r2.addView(r0, r14)
            org.telegram.ui.Components.RecyclerListView r0 = r8.tabletVideoGridView
            org.telegram.ui.GroupCallTabletGridAdapter r2 = new org.telegram.ui.GroupCallTabletGridAdapter
            int r14 = r8.currentAccount
            r2.<init>(r10, r14, r8)
            r8.tabletGridAdapter = r2
            r0.setAdapter(r2)
            androidx.recyclerview.widget.GridLayoutManager r0 = new androidx.recyclerview.widget.GridLayoutManager
            r2 = 1
            r0.<init>(r9, r7, r2, r12)
            org.telegram.ui.Components.RecyclerListView r2 = r8.tabletVideoGridView
            r2.setLayoutManager(r0)
            org.telegram.ui.GroupCallActivity$14 r2 = new org.telegram.ui.GroupCallActivity$14
            r2.<init>()
            r0.setSpanSizeLookup(r2)
            org.telegram.ui.Components.RecyclerListView r0 = r8.tabletVideoGridView
            org.telegram.ui.-$$Lambda$GroupCallActivity$pe2k4yt-ARLBGovrqJ8sJYRpRRM r2 = new org.telegram.ui.-$$Lambda$GroupCallActivity$pe2k4yt-ARLBGovrqJ8sJYRpRRM
            r2.<init>()
            r0.setOnItemClickListener((org.telegram.ui.Components.RecyclerListView.OnItemClickListener) r2)
            androidx.recyclerview.widget.DefaultItemAnimator r0 = new androidx.recyclerview.widget.DefaultItemAnimator
            r0.<init>()
            r0.setDelayAnimations(r12)
            r0.setTranslationInterpolator(r13)
            r13 = 350(0x15e, double:1.73E-321)
            r0.setRemoveDuration(r13)
            r0.setAddDuration(r13)
            r0.setMoveDuration(r13)
            org.telegram.ui.GroupCallActivity$15 r0 = new org.telegram.ui.GroupCallActivity$15
            r0.<init>()
            org.telegram.ui.Components.RecyclerListView r2 = r8.tabletVideoGridView
            r2.setItemAnimator(r0)
            org.telegram.ui.Components.RecyclerListView r0 = r8.tabletVideoGridView
            org.telegram.ui.GroupCallActivity$16 r2 = new org.telegram.ui.GroupCallActivity$16
            r2.<init>()
            r0.setOnScrollListener(r2)
            org.telegram.ui.GroupCallTabletGridAdapter r0 = r8.tabletGridAdapter
            org.telegram.ui.Components.RecyclerListView r2 = r8.tabletVideoGridView
            r0.setVisibility(r2, r12, r12)
            org.telegram.ui.Components.RecyclerListView r0 = r8.tabletVideoGridView
            r2 = 8
            r0.setVisibility(r2)
            org.telegram.ui.GroupCallActivity$17 r0 = new org.telegram.ui.GroupCallActivity$17
            r0.<init>(r9)
            r8.buttonsContainer = r0
            java.lang.String r0 = "voipgroup_unmuteButton2"
            int r0 = org.telegram.ui.ActionBar.Theme.getColor(r0)
            int r2 = android.graphics.Color.red(r0)
            int r13 = android.graphics.Color.green(r0)
            int r0 = android.graphics.Color.blue(r0)
            android.graphics.Matrix r14 = new android.graphics.Matrix
            r14.<init>()
            r8.radialMatrix = r14
            android.graphics.RadialGradient r14 = new android.graphics.RadialGradient
            r21 = 0
            r22 = 0
            r20 = 1126170624(0x43200000, float:160.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r20)
            float r7 = (float) r7
            r30 = r4
            r12 = 2
            int[] r4 = new int[r12]
            r12 = 50
            int r12 = android.graphics.Color.argb(r12, r2, r13, r0)
            r5 = 0
            r4[r5] = r12
            int r0 = android.graphics.Color.argb(r5, r2, r13, r0)
            r2 = 1
            r4[r2] = r0
            r25 = 0
            android.graphics.Shader$TileMode r26 = android.graphics.Shader.TileMode.CLAMP
            r20 = r14
            r23 = r7
            r24 = r4
            r20.<init>(r21, r22, r23, r24, r25, r26)
            r8.radialGradient = r14
            android.graphics.Paint r0 = new android.graphics.Paint
            r2 = 1
            r0.<init>(r2)
            r8.radialPaint = r0
            android.graphics.RadialGradient r2 = r8.radialGradient
            r0.setShader(r2)
            org.telegram.ui.Components.BlobDrawable r0 = new org.telegram.ui.Components.BlobDrawable
            r2 = 9
            r0.<init>(r2)
            r8.tinyWaveDrawable = r0
            org.telegram.ui.Components.BlobDrawable r2 = new org.telegram.ui.Components.BlobDrawable
            r12 = 12
            r2.<init>(r12)
            r8.bigWaveDrawable = r2
            r4 = 1115160576(0x42780000, float:62.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r4 = (float) r4
            r0.minRadius = r4
            r4 = 1116733440(0x42900000, float:72.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r4 = (float) r4
            r0.maxRadius = r4
            r0.generateBlob()
            r4 = 1115815936(0x42820000, float:65.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r4 = (float) r4
            r2.minRadius = r4
            r4 = 1117126656(0x42960000, float:75.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r4 = (float) r4
            r2.maxRadius = r4
            r2.generateBlob()
            android.graphics.Paint r0 = r0.paint
            java.lang.String r4 = "voipgroup_unmuteButton"
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r4)
            r5 = 38
            int r4 = androidx.core.graphics.ColorUtils.setAlphaComponent(r4, r5)
            r0.setColor(r4)
            android.graphics.Paint r0 = r2.paint
            java.lang.String r2 = "voipgroup_unmuteButton"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r4 = 76
            int r2 = androidx.core.graphics.ColorUtils.setAlphaComponent(r2, r4)
            r0.setColor(r2)
            org.telegram.ui.Components.voip.VoIPToggleButton r0 = new org.telegram.ui.Components.voip.VoIPToggleButton
            r0.<init>(r9)
            r8.soundButton = r0
            r2 = 1
            r0.setCheckable(r2)
            org.telegram.ui.Components.voip.VoIPToggleButton r0 = r8.soundButton
            r0.setTextSize(r12)
            android.widget.FrameLayout r0 = r8.buttonsContainer
            org.telegram.ui.Components.voip.VoIPToggleButton r2 = r8.soundButton
            r4 = 68
            r5 = 1117782016(0x42a00000, float:80.0)
            android.widget.FrameLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createFrame(r4, r5)
            r0.addView(r2, r7)
            org.telegram.ui.Components.voip.VoIPToggleButton r0 = r8.soundButton
            org.telegram.ui.-$$Lambda$GroupCallActivity$3_HHMGgm0DRyY23y619fQpe4boY r2 = new org.telegram.ui.-$$Lambda$GroupCallActivity$3_HHMGgm0DRyY23y619fQpe4boY
            r2.<init>()
            r0.setOnClickListener(r2)
            org.telegram.ui.Components.voip.VoIPToggleButton r0 = new org.telegram.ui.Components.voip.VoIPToggleButton
            r0.<init>(r9)
            r8.cameraButton = r0
            r2 = 1
            r0.setCheckable(r2)
            org.telegram.ui.Components.voip.VoIPToggleButton r0 = r8.cameraButton
            r0.setTextSize(r12)
            org.telegram.ui.Components.voip.VoIPToggleButton r0 = r8.cameraButton
            r2 = 0
            r0.showText(r2, r2)
            org.telegram.ui.Components.voip.VoIPToggleButton r0 = r8.cameraButton
            r2 = 1080033280(0x40600000, float:3.5)
            float r2 = org.telegram.messenger.AndroidUtilities.dpf2(r2)
            float r2 = -r2
            r0.setCrossOffset(r2)
            org.telegram.ui.Components.voip.VoIPToggleButton r0 = r8.cameraButton
            r2 = 8
            r0.setVisibility(r2)
            android.widget.FrameLayout r0 = r8.buttonsContainer
            org.telegram.ui.Components.voip.VoIPToggleButton r2 = r8.cameraButton
            android.widget.FrameLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createFrame(r4, r5)
            r0.addView(r2, r7)
            org.telegram.ui.Components.voip.VoIPToggleButton r0 = new org.telegram.ui.Components.voip.VoIPToggleButton
            r0.<init>(r9)
            r8.flipButton = r0
            r2 = 1
            r0.setCheckable(r2)
            org.telegram.ui.Components.voip.VoIPToggleButton r0 = r8.flipButton
            r0.setTextSize(r12)
            org.telegram.ui.Components.voip.VoIPToggleButton r0 = r8.flipButton
            r2 = 0
            r0.showText(r2, r2)
            org.telegram.ui.Components.RLottieImageView r0 = new org.telegram.ui.Components.RLottieImageView
            r0.<init>(r9)
            org.telegram.ui.Components.voip.VoIPToggleButton r2 = r8.flipButton
            r20 = 32
            r21 = 1107296256(0x42000000, float:32.0)
            r22 = 0
            r23 = 1099956224(0x41900000, float:18.0)
            r24 = 1092616192(0x41200000, float:10.0)
            r25 = 1099956224(0x41900000, float:18.0)
            r26 = 0
            android.widget.FrameLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createFrame(r20, r21, r22, r23, r24, r25, r26)
            r2.addView(r0, r7)
            org.telegram.ui.Components.RLottieDrawable r2 = new org.telegram.ui.Components.RLottieDrawable
            r21 = 2131558409(0x7f0d0009, float:1.8742133E38)
            r7 = 1103101952(0x41CLASSNAME, float:24.0)
            int r23 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r24 = org.telegram.messenger.AndroidUtilities.dp(r7)
            r25 = 1
            r26 = 0
            java.lang.String r22 = "NUM"
            r20 = r2
            r20.<init>((int) r21, (java.lang.String) r22, (int) r23, (int) r24, (boolean) r25, (int[]) r26)
            r8.flipIcon = r2
            r0.setAnimation(r2)
            org.telegram.ui.Components.voip.VoIPToggleButton r0 = r8.flipButton
            org.telegram.ui.-$$Lambda$GroupCallActivity$H8y3AsTTCzyy6Vg1B16yTAimQww r2 = new org.telegram.ui.-$$Lambda$GroupCallActivity$H8y3AsTTCzyy6Vg1B16yTAimQww
            r2.<init>()
            r0.setOnClickListener(r2)
            org.telegram.ui.Components.voip.VoIPToggleButton r0 = r8.flipButton
            r2 = 8
            r0.setVisibility(r2)
            android.widget.FrameLayout r0 = r8.buttonsContainer
            org.telegram.ui.Components.voip.VoIPToggleButton r2 = r8.flipButton
            android.widget.FrameLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createFrame(r4, r5)
            r0.addView(r2, r7)
            org.telegram.ui.Components.voip.VoIPToggleButton r0 = new org.telegram.ui.Components.voip.VoIPToggleButton
            r0.<init>(r9)
            r8.leaveButton = r0
            r2 = 0
            r0.setDrawBackground(r2)
            org.telegram.ui.Components.voip.VoIPToggleButton r0 = r8.leaveButton
            r0.setTextSize(r12)
            org.telegram.ui.Components.voip.VoIPToggleButton r0 = r8.leaveButton
            r32 = 2131165319(0x7var_, float:1.7944852E38)
            r33 = -1
            java.lang.String r2 = "voipgroup_leaveButton"
            int r34 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r35 = 1050253722(0x3e99999a, float:0.3)
            r36 = 0
            r2 = 2131628117(0x7f0e1055, float:1.8883518E38)
            java.lang.String r7 = "VoipGroupLeave"
            java.lang.String r37 = org.telegram.messenger.LocaleController.getString(r7, r2)
            r38 = 0
            r39 = 0
            r31 = r0
            r31.setData(r32, r33, r34, r35, r36, r37, r38, r39)
            android.widget.FrameLayout r0 = r8.buttonsContainer
            org.telegram.ui.Components.voip.VoIPToggleButton r2 = r8.leaveButton
            android.widget.FrameLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r4, r5)
            r0.addView(r2, r4)
            org.telegram.ui.Components.voip.VoIPToggleButton r0 = r8.leaveButton
            org.telegram.ui.-$$Lambda$GroupCallActivity$3LwK2SqZWB8Kv9symqRlKdsVoGU r2 = new org.telegram.ui.-$$Lambda$GroupCallActivity$3LwK2SqZWB8Kv9symqRlKdsVoGU
            r2.<init>(r9)
            r0.setOnClickListener(r2)
            org.telegram.ui.GroupCallActivity$18 r0 = new org.telegram.ui.GroupCallActivity$18
            r0.<init>(r9)
            r8.muteButton = r0
            org.telegram.ui.Components.RLottieDrawable r2 = r8.bigMicDrawable
            r0.setAnimation(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r8.muteButton
            android.widget.ImageView$ScaleType r2 = android.widget.ImageView.ScaleType.CENTER
            r0.setScaleType(r2)
            android.widget.FrameLayout r0 = r8.buttonsContainer
            org.telegram.ui.Components.RLottieImageView r2 = r8.muteButton
            r4 = 122(0x7a, float:1.71E-43)
            r5 = 122(0x7a, float:1.71E-43)
            r7 = 49
            android.widget.FrameLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r4, r5, r7)
            r0.addView(r2, r4)
            org.telegram.ui.Components.RLottieImageView r0 = r8.muteButton
            org.telegram.ui.GroupCallActivity$19 r2 = new org.telegram.ui.GroupCallActivity$19
            r2.<init>()
            r0.setOnClickListener(r2)
            org.telegram.ui.Components.RadialProgressView r0 = new org.telegram.ui.Components.RadialProgressView
            r0.<init>(r9)
            r8.radialProgressView = r0
            r2 = 1121714176(0x42dCLASSNAME, float:110.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r0.setSize(r2)
            org.telegram.ui.Components.RadialProgressView r0 = r8.radialProgressView
            r2 = 1082130432(0x40800000, float:4.0)
            r0.setStrokeWidth(r2)
            org.telegram.ui.Components.RadialProgressView r0 = r8.radialProgressView
            java.lang.String r2 = "voipgroup_connectingProgress"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r0.setProgressColor(r2)
            r0 = 0
        L_0x06fd:
            r2 = 2
            if (r0 >= r2) goto L_0x0743
            android.widget.TextView[] r2 = r8.muteLabel
            android.widget.TextView r4 = new android.widget.TextView
            r4.<init>(r9)
            r2[r0] = r4
            android.widget.TextView[] r2 = r8.muteLabel
            r2 = r2[r0]
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            r2.setTextColor(r4)
            android.widget.TextView[] r2 = r8.muteLabel
            r2 = r2[r0]
            r4 = 1099956224(0x41900000, float:18.0)
            r5 = 1
            r2.setTextSize(r5, r4)
            android.widget.TextView[] r2 = r8.muteLabel
            r2 = r2[r0]
            r2.setGravity(r5)
            android.widget.FrameLayout r2 = r8.buttonsContainer
            android.widget.TextView[] r4 = r8.muteLabel
            r4 = r4[r0]
            r20 = -2
            r21 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r22 = 81
            r23 = 0
            r24 = 0
            r25 = 0
            r26 = 1104150528(0x41d00000, float:26.0)
            android.widget.FrameLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createFrame(r20, r21, r22, r23, r24, r25, r26)
            r2.addView(r4, r5)
            int r0 = r0 + 1
            goto L_0x06fd
        L_0x0743:
            org.telegram.ui.ActionBar.ActionBar r0 = r8.actionBar
            r13 = 0
            r0.setAlpha(r13)
            org.telegram.ui.ActionBar.ActionBar r0 = r8.actionBar
            android.widget.ImageView r0 = r0.getBackButton()
            r2 = 1063675494(0x3var_, float:0.9)
            r0.setScaleX(r2)
            org.telegram.ui.ActionBar.ActionBar r0 = r8.actionBar
            android.widget.ImageView r0 = r0.getBackButton()
            r0.setScaleY(r2)
            org.telegram.ui.ActionBar.ActionBar r0 = r8.actionBar
            android.widget.ImageView r0 = r0.getBackButton()
            r14 = 1096810496(0x41600000, float:14.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r14)
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
            org.telegram.ui.ActionBar.ActionBar r0 = r8.actionBar
            org.telegram.ui.ActionBar.SimpleTextView r0 = r0.getAdditionalSubtitleTextView()
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
            org.telegram.ui.-$$Lambda$GroupCallActivity$WtHaoxiePrgL4BVQ6dMGRTU73cE r2 = new org.telegram.ui.-$$Lambda$GroupCallActivity$WtHaoxiePrgL4BVQ6dMGRTU73cE
            r2.<init>()
            r0.setOnClickListener(r2)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = new org.telegram.ui.ActionBar.ActionBarMenuItem
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            r4 = 0
            r5 = 0
            r0.<init>(r9, r4, r5, r2)
            r8.otherItem = r0
            r0.setLongClickEnabled(r5)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r8.otherItem
            r2 = 2131165468(0x7var_c, float:1.7945154E38)
            r0.setIcon((int) r2)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r8.otherItem
            r2 = 2131623986(0x7f0e0032, float:1.8875139E38)
            java.lang.String r4 = "AccDescrMoreOptions"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r4, r2)
            r0.setContentDescription(r2)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r8.otherItem
            r2 = 2
            r0.setSubMenuOpenSide(r2)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r8.otherItem
            org.telegram.ui.-$$Lambda$GroupCallActivity$VYavOKuRAigIYp4nNzmA6xeKTmw r2 = new org.telegram.ui.-$$Lambda$GroupCallActivity$VYavOKuRAigIYp4nNzmA6xeKTmw
            r2.<init>()
            r0.setDelegate(r2)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r8.otherItem
            java.lang.String r2 = "voipgroup_actionBarItemsSelector"
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r5 = 6
            android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r4, r5)
            r0.setBackgroundDrawable(r4)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r8.otherItem
            org.telegram.ui.-$$Lambda$GroupCallActivity$l68cadOPIXx1MAQzR8K1PJ2wIFQ r4 = new org.telegram.ui.-$$Lambda$GroupCallActivity$l68cadOPIXx1MAQzR8K1PJ2wIFQ
            r4.<init>(r9)
            r0.setOnClickListener(r4)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r8.otherItem
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            r5 = 0
            r0.setPopupItemsColor(r4, r5)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r8.otherItem
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            r7 = 1
            r0.setPopupItemsColor(r4, r7)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = new org.telegram.ui.ActionBar.ActionBarMenuItem
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            r7 = 0
            r0.<init>(r9, r7, r5, r4)
            r8.pipItem = r0
            r0.setLongClickEnabled(r5)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r8.pipItem
            r4 = 2131165844(0x7var_, float:1.7945917E38)
            r0.setIcon((int) r4)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r8.pipItem
            r4 = 2131624010(0x7f0e004a, float:1.8875188E38)
            java.lang.String r5 = "AccDescrPipMode"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r0.setContentDescription(r4)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r8.pipItem
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r5 = 6
            android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r4, r5)
            r0.setBackgroundDrawable(r4)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r8.pipItem
            org.telegram.ui.-$$Lambda$GroupCallActivity$GnpTA048eRdLuVIlz7XtQ0TKf2E r4 = new org.telegram.ui.-$$Lambda$GroupCallActivity$GnpTA048eRdLuVIlz7XtQ0TKf2E
            r4.<init>()
            r0.setOnClickListener(r4)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = new org.telegram.ui.ActionBar.ActionBarMenuItem
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            r5 = 0
            r7 = 0
            r0.<init>(r9, r5, r7, r4)
            r8.screenShareItem = r0
            r0.setLongClickEnabled(r7)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r8.screenShareItem
            r4 = 2131165811(0x7var_, float:1.794585E38)
            r0.setIcon((int) r4)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r8.screenShareItem
            r4 = 2131624010(0x7f0e004a, float:1.8875188E38)
            java.lang.String r5 = "AccDescrPipMode"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r0.setContentDescription(r4)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r8.screenShareItem
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r4 = 6
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r2, r4)
            r0.setBackgroundDrawable(r2)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r8.screenShareItem
            org.telegram.ui.-$$Lambda$GroupCallActivity$xOOJq9RxQSuCL4qcXzY50MqSZOk r2 = new org.telegram.ui.-$$Lambda$GroupCallActivity$xOOJq9RxQSuCL4qcXzY50MqSZOk
            r2.<init>()
            r0.setOnClickListener(r2)
            org.telegram.ui.GroupCallActivity$20 r0 = new org.telegram.ui.GroupCallActivity$20
            r0.<init>(r9, r9)
            r8.titleTextView = r0
            org.telegram.ui.GroupCallActivity$21 r0 = new org.telegram.ui.GroupCallActivity$21
            r0.<init>(r9)
            r8.actionBarBackground = r0
            r0.setAlpha(r13)
            android.view.ViewGroup r0 = r8.containerView
            android.view.View r2 = r8.actionBarBackground
            r20 = -1
            r21 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r22 = 51
            r23 = 0
            r24 = 0
            r25 = 0
            r26 = 0
            android.widget.FrameLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r20, r21, r22, r23, r24, r25, r26)
            r0.addView(r2, r4)
            android.view.ViewGroup r0 = r8.containerView
            org.telegram.ui.Components.AudioPlayerAlert$ClippingTextViewSwitcher r2 = r8.titleTextView
            r20 = -2
            r23 = 1102577664(0x41b80000, float:23.0)
            r25 = 1111490560(0x42400000, float:48.0)
            android.widget.FrameLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r20, r21, r22, r23, r24, r25, r26)
            r0.addView(r2, r4)
            android.view.ViewGroup r0 = r8.containerView
            org.telegram.ui.ActionBar.ActionBar r2 = r8.actionBar
            r20 = -1
            r23 = 0
            r25 = 0
            android.widget.FrameLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r20, r21, r22, r23, r24, r25, r26)
            r0.addView(r2, r4)
            android.widget.LinearLayout r0 = new android.widget.LinearLayout
            r0.<init>(r9)
            r8.menuItemsContainer = r0
            r2 = 0
            r0.setOrientation(r2)
            org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r8.screenShareItem
            r4 = 48
            android.widget.LinearLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createLinear(r4, r4)
            r0.addView(r2, r5)
            org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r8.pipItem
            android.widget.LinearLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createLinear(r4, r4)
            r0.addView(r2, r5)
            org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r8.otherItem
            android.widget.LinearLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createLinear(r4, r4)
            r0.addView(r2, r5)
            org.telegram.ui.Components.BackupImageView r2 = r8.accountSwitchImageView
            r20 = 32
            r21 = 32
            r22 = 16
            r23 = 2
            r24 = 0
            r25 = 12
            r26 = 0
            android.widget.LinearLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r20, (int) r21, (int) r22, (int) r23, (int) r24, (int) r25, (int) r26)
            r0.addView(r2, r5)
            android.view.ViewGroup r2 = r8.containerView
            r5 = -2
            r7 = 53
            android.widget.FrameLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r5, r4, r7)
            r2.addView(r0, r4)
            android.view.View r0 = new android.view.View
            r0.<init>(r9)
            r8.actionBarShadow = r0
            r0.setAlpha(r13)
            android.view.View r0 = r8.actionBarShadow
            java.lang.String r2 = "dialogShadowLine"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r0.setBackgroundColor(r2)
            android.view.ViewGroup r0 = r8.containerView
            android.view.View r2 = r8.actionBarShadow
            r4 = 1065353216(0x3var_, float:1.0)
            r7 = -1
            android.widget.FrameLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createFrame(r7, r4)
            r0.addView(r2, r5)
            r0 = 0
        L_0x0963:
            r2 = 2
            if (r0 >= r2) goto L_0x09af
            org.telegram.ui.Components.UndoView[] r2 = r8.undoView
            org.telegram.ui.GroupCallActivity$22 r4 = new org.telegram.ui.GroupCallActivity$22
            r4.<init>(r9)
            r2[r0] = r4
            org.telegram.ui.Components.UndoView[] r2 = r8.undoView
            r2 = r2[r0]
            r4 = 1092616192(0x41200000, float:10.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r4 = (float) r4
            r2.setAdditionalTranslationY(r4)
            int r2 = android.os.Build.VERSION.SDK_INT
            r4 = 21
            if (r2 < r4) goto L_0x0991
            org.telegram.ui.Components.UndoView[] r2 = r8.undoView
            r2 = r2[r0]
            r4 = 1084227584(0x40a00000, float:5.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r4 = (float) r4
            r2.setTranslationZ(r4)
        L_0x0991:
            android.view.ViewGroup r2 = r8.containerView
            org.telegram.ui.Components.UndoView[] r4 = r8.undoView
            r4 = r4[r0]
            r20 = -1
            r21 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r22 = 83
            r23 = 1090519040(0x41000000, float:8.0)
            r24 = 0
            r25 = 1090519040(0x41000000, float:8.0)
            r26 = 1090519040(0x41000000, float:8.0)
            android.widget.FrameLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createFrame(r20, r21, r22, r23, r24, r25, r26)
            r2.addView(r4, r5)
            int r0 = r0 + 1
            goto L_0x0963
        L_0x09af:
            org.telegram.ui.Cells.AccountSelectCell r0 = new org.telegram.ui.Cells.AccountSelectCell
            r2 = 1
            r0.<init>(r9, r2)
            r8.accountSelectCell = r0
            r2 = 2131230943(0x7var_df, float:1.8077953E38)
            r4 = 240(0xf0, float:3.36E-43)
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
            r0.setTag(r2, r4)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r8.otherItem
            org.telegram.ui.Cells.AccountSelectCell r2 = r8.accountSelectCell
            r4 = -2
            r5 = 1111490560(0x42400000, float:48.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            r7 = 8
            r0.addSubItem((int) r7, (android.view.View) r2, (int) r4, (int) r5)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r8.otherItem
            r2 = 0
            r0.setShowSubmenuByMove(r2)
            org.telegram.ui.Cells.AccountSelectCell r0 = r8.accountSelectCell
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r7 = 6
            android.graphics.drawable.Drawable r4 = org.telegram.ui.ActionBar.Theme.createRadSelectorDrawable(r4, r7, r7)
            r0.setBackground(r4)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r8.otherItem
            android.view.View r0 = r0.addGap(r2)
            r8.accountGap = r0
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r8.otherItem
            r4 = 2131628079(0x7f0e102f, float:1.888344E38)
            java.lang.String r5 = "VoipGroupAllCanSpeak"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r5 = 1
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r0 = r0.addSubItem((int) r5, (int) r2, (java.lang.CharSequence) r4, (boolean) r5)
            r8.everyoneItem = r0
            r0.updateSelectorBackground(r5, r2)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r8.otherItem
            r4 = 2131628128(0x7f0e1060, float:1.888354E38)
            java.lang.String r7 = "VoipGroupOnlyAdminsCanSpeak"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r7, r4)
            r7 = 2
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r0 = r0.addSubItem((int) r7, (int) r2, (java.lang.CharSequence) r4, (boolean) r5)
            r8.adminItem = r0
            r0.updateSelectorBackground(r2, r5)
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r0 = r8.everyoneItem
            java.lang.String r2 = "voipgroup_checkMenu"
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r0.setCheckColor(r4)
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r0 = r8.everyoneItem
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r0.setColors(r4, r5)
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r0 = r8.adminItem
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r0.setCheckColor(r4)
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r0 = r8.adminItem
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r0.setColors(r4, r2)
            android.graphics.Paint r0 = new android.graphics.Paint
            r2 = 1
            r0.<init>(r2)
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            r0.setColor(r2)
            android.graphics.Paint$Style r2 = android.graphics.Paint.Style.STROKE
            r0.setStyle(r2)
            r2 = 1069547520(0x3fCLASSNAME, float:1.5)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r2 = (float) r2
            r0.setStrokeWidth(r2)
            android.graphics.Paint$Cap r2 = android.graphics.Paint.Cap.ROUND
            r0.setStrokeCap(r2)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r8.otherItem
            r21 = 10
            r22 = 2131165845(0x7var_, float:1.7945919E38)
            r23 = 0
            r2 = 2131628081(0x7f0e1031, float:1.8883445E38)
            java.lang.String r4 = "VoipGroupAudio"
            java.lang.String r24 = org.telegram.messenger.LocaleController.getString(r4, r2)
            r25 = 1
            r26 = 0
            r20 = r0
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r0 = r20.addSubItem(r21, r22, r23, r24, r25, r26)
            r8.soundItem = r0
            r2 = 56
            r0.setItemHeight(r2)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r8.otherItem
            java.lang.String r2 = "voipgroup_actionBar"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r4 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            r5 = 1050253722(0x3e99999a, float:0.3)
            int r2 = androidx.core.graphics.ColorUtils.blendARGB(r2, r4, r5)
            android.view.View r0 = r0.addDivider(r2)
            r8.soundItemDivider = r0
            android.view.ViewGroup$LayoutParams r0 = r0.getLayoutParams()
            android.view.ViewGroup$MarginLayoutParams r0 = (android.view.ViewGroup.MarginLayoutParams) r0
            r2 = 0
            r0.topMargin = r2
            android.view.View r0 = r8.soundItemDivider
            android.view.ViewGroup$LayoutParams r0 = r0.getLayoutParams()
            android.view.ViewGroup$MarginLayoutParams r0 = (android.view.ViewGroup.MarginLayoutParams) r0
            r0.bottomMargin = r2
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r8.otherItem
            r21 = 6
            r22 = 2131165737(0x7var_, float:1.79457E38)
            org.telegram.ui.GroupCallActivity$RecordCallDrawable r2 = r8.recordCallDrawable
            r4 = 2131628097(0x7f0e1041, float:1.8883477E38)
            java.lang.String r5 = "VoipGroupEditTitle"
            java.lang.String r24 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r20 = r0
            r23 = r2
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r0 = r20.addSubItem(r21, r22, r23, r24, r25, r26)
            r8.editTitleItem = r0
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r8.otherItem
            r21 = 7
            r22 = 2131165779(0x7var_, float:1.7945785E38)
            org.telegram.ui.GroupCallActivity$RecordCallDrawable r2 = r8.recordCallDrawable
            r4 = 2131628096(0x7f0e1040, float:1.8883475E38)
            java.lang.String r5 = "VoipGroupEditPermissions"
            java.lang.String r24 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r25 = 0
            r20 = r0
            r23 = r2
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r0 = r20.addSubItem(r21, r22, r23, r24, r25, r26)
            r8.permissionItem = r0
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r8.otherItem
            r2 = 3
            r4 = 2131165754(0x7var_a, float:1.7945734E38)
            r5 = 2131628147(0x7f0e1073, float:1.8883578E38)
            java.lang.String r7 = "VoipGroupShareInviteLink"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r7, r5)
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r0 = r0.addSubItem(r2, r4, r5)
            r8.inviteItem = r0
            org.telegram.ui.GroupCallActivity$RecordCallDrawable r0 = new org.telegram.ui.GroupCallActivity$RecordCallDrawable
            r0.<init>()
            r8.recordCallDrawable = r0
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r8.otherItem
            r2 = 9
            r4 = 2131165811(0x7var_, float:1.794585E38)
            r5 = 2131628059(0x7f0e101b, float:1.88834E38)
            java.lang.String r7 = "VoipChatStartScreenCapture"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r7, r5)
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r0 = r0.addSubItem(r2, r4, r5)
            r8.screenItem = r0
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r8.otherItem
            r21 = 5
            r22 = 0
            org.telegram.ui.GroupCallActivity$RecordCallDrawable r2 = r8.recordCallDrawable
            r4 = 2131628135(0x7f0e1067, float:1.8883554E38)
            java.lang.String r5 = "VoipGroupRecordCall"
            java.lang.String r24 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r25 = 1
            r20 = r0
            r23 = r2
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r0 = r20.addSubItem(r21, r22, r23, r24, r25, r26)
            r8.recordItem = r0
            org.telegram.ui.GroupCallActivity$RecordCallDrawable r2 = r8.recordCallDrawable
            android.widget.ImageView r0 = r0.getImageView()
            r2.setParentView(r0)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r8.otherItem
            r2 = 4
            r4 = 2131165738(0x7var_a, float:1.7945702E38)
            r5 = 2131628101(0x7f0e1045, float:1.8883485E38)
            java.lang.String r7 = "VoipGroupEndChat"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r7, r5)
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r0 = r0.addSubItem(r2, r4, r5)
            r8.leaveItem = r0
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r8.otherItem
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.setPopupItemsSelectorColor(r1)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r8.otherItem
            org.telegram.ui.ActionBar.ActionBarPopupWindow$ActionBarPopupWindowLayout r0 = r0.getPopupLayout()
            r1 = 1
            r0.setFitItems(r1)
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r0 = r8.soundItem
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            r0.setColors(r1, r2)
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r0 = r8.leaveItem
            java.lang.String r1 = "voipgroup_leaveCallMenu"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            java.lang.String r2 = "voipgroup_leaveCallMenu"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r0.setColors(r1, r2)
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r0 = r8.inviteItem
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            r0.setColors(r1, r2)
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r0 = r8.editTitleItem
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            r0.setColors(r1, r2)
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r0 = r8.permissionItem
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            r0.setColors(r1, r2)
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r0 = r8.recordItem
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            r0.setColors(r1, r2)
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r0 = r8.screenItem
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            r0.setColors(r1, r2)
            org.telegram.messenger.ChatObject$Call r0 = r8.call
            if (r0 == 0) goto L_0x0bd5
            r40.initCreatedGroupCall()
        L_0x0bd5:
            android.graphics.Paint r0 = r8.leaveBackgroundPaint
            java.lang.String r1 = "voipgroup_leaveButton"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.setColor(r1)
            r0 = 0
            r8.updateTitle(r0)
            org.telegram.ui.ActionBar.ActionBar r0 = r8.actionBar
            org.telegram.ui.ActionBar.SimpleTextView r0 = r0.getTitleTextView()
            org.telegram.ui.-$$Lambda$GroupCallActivity$b3OniivOQz957CLASSNAMEQPGz0D5GbyY r1 = new org.telegram.ui.-$$Lambda$GroupCallActivity$b3OniivOQz957CLASSNAMEQPGz0D5GbyY
            r1.<init>()
            r0.setOnClickListener(r1)
            org.telegram.ui.GroupCallActivity$23 r0 = new org.telegram.ui.GroupCallActivity$23
            r0.<init>(r9)
            r8.fullscreenUsersListView = r0
            org.telegram.ui.GroupCallActivity$24 r0 = new org.telegram.ui.GroupCallActivity$24
            r0.<init>()
            r8.fullscreenListItemAnimator = r0
            org.telegram.ui.Components.RecyclerListView r1 = r8.fullscreenUsersListView
            r2 = 0
            r1.setClipToPadding(r2)
            r0.setDelayAnimations(r2)
            org.telegram.ui.Components.CubicBezierInterpolator r1 = org.telegram.ui.Components.CubicBezierInterpolator.DEFAULT
            r0.setTranslationInterpolator(r1)
            r1 = 350(0x15e, double:1.73E-321)
            r0.setRemoveDuration(r1)
            r0.setAddDuration(r1)
            r0.setMoveDuration(r1)
            org.telegram.ui.Components.RecyclerListView r1 = r8.fullscreenUsersListView
            r1.setItemAnimator(r0)
            org.telegram.ui.Components.RecyclerListView r0 = r8.fullscreenUsersListView
            org.telegram.ui.GroupCallActivity$25 r1 = new org.telegram.ui.GroupCallActivity$25
            r1.<init>()
            r0.setOnScrollListener(r1)
            org.telegram.ui.Components.RecyclerListView r0 = r8.fullscreenUsersListView
            r1 = 0
            r0.setClipChildren(r1)
            androidx.recyclerview.widget.LinearLayoutManager r0 = new androidx.recyclerview.widget.LinearLayoutManager
            r0.<init>(r9)
            r0.setOrientation(r1)
            org.telegram.ui.Components.RecyclerListView r2 = r8.fullscreenUsersListView
            r2.setLayoutManager(r0)
            org.telegram.ui.Components.RecyclerListView r0 = r8.fullscreenUsersListView
            org.telegram.ui.Components.GroupCallFullscreenAdapter r2 = new org.telegram.ui.Components.GroupCallFullscreenAdapter
            int r3 = r8.currentAccount
            r2.<init>(r10, r3, r8)
            r8.fullscreenAdapter = r2
            r0.setAdapter(r2)
            org.telegram.ui.Components.GroupCallFullscreenAdapter r0 = r8.fullscreenAdapter
            org.telegram.ui.Components.RecyclerListView r2 = r8.fullscreenUsersListView
            r0.setVisibility(r2, r1)
            org.telegram.ui.Components.RecyclerListView r0 = r8.fullscreenUsersListView
            org.telegram.ui.-$$Lambda$GroupCallActivity$GuURvv_-LLC1U7_viTRehDb_5Js r1 = new org.telegram.ui.-$$Lambda$GroupCallActivity$GuURvv_-LLC1U7_viTRehDb_5Js
            r1.<init>()
            r0.setOnItemClickListener((org.telegram.ui.Components.RecyclerListView.OnItemClickListener) r1)
            org.telegram.ui.Components.RecyclerListView r0 = r8.fullscreenUsersListView
            org.telegram.ui.-$$Lambda$GroupCallActivity$bFZjGfSN-NEYR78a6N4bgoas8bo r1 = new org.telegram.ui.-$$Lambda$GroupCallActivity$bFZjGfSN-NEYR78a6N4bgoas8bo
            r1.<init>()
            r0.setOnItemLongClickListener((org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener) r1)
            org.telegram.ui.Components.RecyclerListView r0 = r8.fullscreenUsersListView
            r1 = 8
            r0.setVisibility(r1)
            org.telegram.ui.Components.RecyclerListView r0 = r8.fullscreenUsersListView
            org.telegram.ui.GroupCallActivity$26 r1 = new org.telegram.ui.GroupCallActivity$26
            r1.<init>()
            r0.addItemDecoration(r1)
            org.telegram.ui.GroupCallActivity$27 r7 = new org.telegram.ui.GroupCallActivity$27
            org.telegram.ui.Components.RecyclerListView r3 = r8.listView
            org.telegram.ui.Components.RecyclerListView r4 = r8.fullscreenUsersListView
            java.util.ArrayList<org.telegram.ui.Components.voip.GroupCallMiniTextureView> r5 = r8.attachedRenderers
            org.telegram.messenger.ChatObject$Call r2 = r8.call
            r0 = r7
            r1 = r40
            r16 = r2
            r13 = 17
            r2 = r41
            r19 = r30
            r6 = r16
            r12 = r7
            r14 = -1
            r7 = r40
            r0.<init>(r2, r3, r4, r5, r6, r7)
            r8.renderersContainer = r12
            r0 = 0
            r12.setClipChildren(r0)
            org.telegram.ui.Components.GroupCallFullscreenAdapter r0 = r8.fullscreenAdapter
            java.util.ArrayList<org.telegram.ui.Components.voip.GroupCallMiniTextureView> r1 = r8.attachedRenderers
            org.telegram.ui.Components.voip.GroupCallRenderersContainer r2 = r8.renderersContainer
            r0.setRenderersPool(r1, r2)
            org.telegram.ui.Components.RecyclerListView r0 = r8.tabletVideoGridView
            if (r0 == 0) goto L_0x0caf
            org.telegram.ui.GroupCallTabletGridAdapter r0 = r8.tabletGridAdapter
            java.util.ArrayList<org.telegram.ui.Components.voip.GroupCallMiniTextureView> r1 = r8.attachedRenderers
            org.telegram.ui.Components.voip.GroupCallRenderersContainer r2 = r8.renderersContainer
            r0.setRenderersPool(r1, r2)
        L_0x0caf:
            org.telegram.ui.AvatarPreviewPagerIndicator r6 = new org.telegram.ui.AvatarPreviewPagerIndicator
            r6.<init>(r9)
            r8.avatarPagerIndicator = r6
            org.telegram.ui.GroupCallActivity$28 r12 = new org.telegram.ui.GroupCallActivity$28
            org.telegram.ui.ActionBar.ActionBar r3 = r8.actionBar
            org.telegram.ui.Components.RecyclerListView r4 = r8.listView
            r0 = r12
            r1 = r40
            r2 = r41
            r5 = r6
            r0.<init>(r2, r3, r4, r5)
            r8.avatarsViewPager = r12
            r0 = 1
            r12.setInvalidateWithParent(r0)
            r6.setProfileGalleryView(r12)
            org.telegram.ui.GroupCallActivity$29 r0 = new org.telegram.ui.GroupCallActivity$29
            r0.<init>(r9)
            r8.avatarPreviewContainer = r0
            r1 = 8
            r0.setVisibility(r1)
            r1 = 0
            r12.setVisibility(r1)
            org.telegram.ui.GroupCallActivity$30 r1 = new org.telegram.ui.GroupCallActivity$30
            r1.<init>()
            r12.addOnPageChangeListener(r1)
            org.telegram.ui.GroupCallActivity$31 r1 = new org.telegram.ui.GroupCallActivity$31
            r1.<init>(r9)
            r8.blurredView = r1
            android.view.ViewGroup r1 = r8.containerView
            org.telegram.ui.Components.voip.GroupCallRenderersContainer r2 = r8.renderersContainer
            r1.addView(r2)
            org.telegram.ui.Components.voip.GroupCallRenderersContainer r1 = r8.renderersContainer
            org.telegram.ui.Components.RecyclerListView r2 = r8.fullscreenUsersListView
            r20 = -1
            r21 = 1117782016(0x42a00000, float:80.0)
            r22 = 80
            r23 = 0
            r24 = 0
            r25 = 0
            r26 = 1120403456(0x42CLASSNAME, float:100.0)
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r20, r21, r22, r23, r24, r25, r26)
            r1.addView(r2, r3)
            android.widget.FrameLayout r1 = r8.buttonsContainer
            r2 = 0
            r1.setWillNotDraw(r2)
            android.view.View r1 = new android.view.View
            r1.<init>(r9)
            r8.buttonsBackgroundGradientView = r1
            int[] r3 = r8.gradientColors
            int r4 = r8.backgroundColor
            r3[r2] = r4
            r4 = 1
            r3[r4] = r2
            android.graphics.drawable.GradientDrawable r2 = new android.graphics.drawable.GradientDrawable
            android.graphics.drawable.GradientDrawable$Orientation r3 = android.graphics.drawable.GradientDrawable.Orientation.BOTTOM_TOP
            int[] r4 = r8.gradientColors
            r2.<init>(r3, r4)
            r8.buttonsBackgroundGradient = r2
            r1.setBackground(r2)
            android.view.ViewGroup r2 = r8.containerView
            r3 = 60
            r4 = 83
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r14, r3, r4)
            r2.addView(r1, r3)
            android.view.View r1 = new android.view.View
            r1.<init>(r9)
            r8.buttonsBackgroundGradientView2 = r1
            int[] r2 = r8.gradientColors
            r3 = 0
            r2 = r2[r3]
            r1.setBackgroundColor(r2)
            android.view.ViewGroup r2 = r8.containerView
            android.widget.FrameLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r14, r3, r4)
            r2.addView(r1, r4)
            android.view.ViewGroup r1 = r8.containerView
            android.widget.FrameLayout r2 = r8.buttonsContainer
            r3 = 200(0xc8, float:2.8E-43)
            r4 = 81
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r14, r3, r4)
            r1.addView(r2, r3)
            android.view.ViewGroup r1 = r8.containerView
            android.view.View r2 = r8.blurredView
            r1.addView(r2)
            r1 = -1082130432(0xffffffffbvar_, float:-1.0)
            android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame(r14, r1)
            r0.addView(r12, r1)
            r21 = -1082130432(0xffffffffbvar_, float:-1.0)
            r22 = 0
            r26 = 0
            android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame(r20, r21, r22, r23, r24, r25, r26)
            r0.addView(r6, r1)
            android.view.ViewGroup r1 = r8.containerView
            r23 = 1096810496(0x41600000, float:14.0)
            r24 = 1096810496(0x41600000, float:14.0)
            r25 = 1096810496(0x41600000, float:14.0)
            r26 = 1096810496(0x41600000, float:14.0)
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r20, r21, r22, r23, r24, r25, r26)
            r1.addView(r0, r2)
            r40.applyCallParticipantUpdates()
            org.telegram.ui.GroupCallActivity$ListAdapter r0 = r8.listAdapter
            r0.notifyDataSetChanged()
            boolean r0 = isTabletMode
            if (r0 == 0) goto L_0x0da7
            org.telegram.ui.GroupCallTabletGridAdapter r0 = r8.tabletGridAdapter
            org.telegram.ui.Components.RecyclerListView r1 = r8.tabletVideoGridView
            r2 = 0
            r0.update(r2, r1)
        L_0x0da7:
            org.telegram.ui.GroupCallActivity$ListAdapter r0 = r8.listAdapter
            int r0 = r0.getItemCount()
            r8.oldCount = r0
            if (r11 == 0) goto L_0x0fb3
            android.widget.TextView r0 = new android.widget.TextView
            r0.<init>(r9)
            r8.scheduleInfoTextView = r0
            r0.setGravity(r13)
            android.widget.TextView r0 = r8.scheduleInfoTextView
            r1 = -8682615(0xffffffffff7b8389, float:-3.343192E38)
            r0.setTextColor(r1)
            android.widget.TextView r0 = r8.scheduleInfoTextView
            r1 = 1096810496(0x41600000, float:14.0)
            r2 = 1
            r0.setTextSize(r2, r1)
            org.telegram.tgnet.TLRPC$Chat r0 = r8.currentChat
            boolean r0 = org.telegram.messenger.ChatObject.isChannel(r0)
            if (r0 == 0) goto L_0x0de2
            org.telegram.tgnet.TLRPC$Chat r0 = r8.currentChat
            boolean r0 = r0.megagroup
            if (r0 != 0) goto L_0x0de2
            android.widget.TextView r0 = r8.scheduleInfoTextView
            java.lang.Integer r1 = java.lang.Integer.valueOf(r2)
            r0.setTag(r1)
        L_0x0de2:
            android.view.ViewGroup r0 = r8.containerView
            android.widget.TextView r1 = r8.scheduleInfoTextView
            r20 = -2
            r21 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r22 = 81
            r23 = 1101529088(0x41a80000, float:21.0)
            r24 = 0
            r25 = 1101529088(0x41a80000, float:21.0)
            r26 = 1120403456(0x42CLASSNAME, float:100.0)
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r20, r21, r22, r23, r24, r25, r26)
            r0.addView(r1, r2)
            org.telegram.ui.Components.NumberPicker r11 = new org.telegram.ui.Components.NumberPicker
            r11.<init>(r9)
            r11.setTextColor(r14)
            r0 = -9598483(0xffffffffff6d89ed, float:-3.1574319E38)
            r11.setSelectorColor(r0)
            r0 = 1092616192(0x41200000, float:10.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r11.setTextOffset(r0)
            r0 = 5
            r11.setItemCount(r0)
            org.telegram.ui.GroupCallActivity$32 r7 = new org.telegram.ui.GroupCallActivity$32
            r7.<init>(r9)
            r7.setItemCount(r0)
            r7.setTextColor(r14)
            r0 = -9598483(0xffffffffff6d89ed, float:-3.1574319E38)
            r7.setSelectorColor(r0)
            r0 = 1092616192(0x41200000, float:10.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r0 = -r0
            r7.setTextOffset(r0)
            org.telegram.ui.GroupCallActivity$33 r6 = new org.telegram.ui.GroupCallActivity$33
            r6.<init>(r9)
            r0 = 5
            r6.setItemCount(r0)
            r6.setTextColor(r14)
            r0 = -9598483(0xffffffffff6d89ed, float:-3.1574319E38)
            r6.setSelectorColor(r0)
            r0 = 1107820544(0x42080000, float:34.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r0 = -r0
            r6.setTextOffset(r0)
            android.widget.TextView r0 = new android.widget.TextView
            r0.<init>(r9)
            r8.scheduleButtonTextView = r0
            r1 = 1
            r0.setLines(r1)
            android.widget.TextView r0 = r8.scheduleButtonTextView
            r0.setSingleLine(r1)
            android.widget.TextView r0 = r8.scheduleButtonTextView
            android.text.TextUtils$TruncateAt r1 = android.text.TextUtils.TruncateAt.END
            r0.setEllipsize(r1)
            android.widget.TextView r0 = r8.scheduleButtonTextView
            r0.setGravity(r13)
            android.widget.TextView r0 = r8.scheduleButtonTextView
            r1 = 1082130432(0x40800000, float:4.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r2 = 1056964608(0x3var_, float:0.5)
            r3 = 0
            android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.createSimpleSelectorRoundRectDrawable(r1, r3, r2)
            r0.setBackground(r1)
            android.widget.TextView r0 = r8.scheduleButtonTextView
            r0.setTextColor(r14)
            android.widget.TextView r0 = r8.scheduleButtonTextView
            android.graphics.Typeface r1 = org.telegram.messenger.AndroidUtilities.getTypeface(r15)
            r0.setTypeface(r1)
            android.widget.TextView r0 = r8.scheduleButtonTextView
            r1 = 1096810496(0x41600000, float:14.0)
            r2 = 1
            r0.setTextSize(r2, r1)
            android.view.ViewGroup r0 = r8.containerView
            android.widget.TextView r1 = r8.scheduleButtonTextView
            r20 = -1
            r21 = 1111490560(0x42400000, float:48.0)
            r26 = 1101266944(0x41a40000, float:20.5)
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r20, r21, r22, r23, r24, r25, r26)
            r0.addView(r1, r2)
            android.widget.TextView r13 = r8.scheduleButtonTextView
            org.telegram.ui.-$$Lambda$GroupCallActivity$9ZYORc_qcEDHlIfr7EozCcM6YeU r14 = new org.telegram.ui.-$$Lambda$GroupCallActivity$9ZYORc_qcEDHlIfr7EozCcM6YeU
            r0 = r14
            r1 = r40
            r2 = r11
            r3 = r7
            r4 = r6
            r5 = r44
            r15 = r6
            r6 = r42
            r42 = r7
            r7 = r19
            r0.<init>(r2, r3, r4, r5, r6, r7)
            r13.setOnClickListener(r14)
            org.telegram.ui.GroupCallActivity$35 r6 = new org.telegram.ui.GroupCallActivity$35
            r0 = r6
            r2 = r41
            r3 = r11
            r4 = r42
            r5 = r15
            r0.<init>(r2, r3, r4, r5)
            r8.scheduleTimerContainer = r6
            r0 = 1065353216(0x3var_, float:1.0)
            r6.setWeightSum(r0)
            android.widget.LinearLayout r0 = r8.scheduleTimerContainer
            r1 = 0
            r0.setOrientation(r1)
            android.view.ViewGroup r0 = r8.containerView
            android.widget.LinearLayout r1 = r8.scheduleTimerContainer
            r19 = -1
            r20 = 1132920832(0x43870000, float:270.0)
            r21 = 51
            r22 = 0
            r23 = 1112014848(0x42480000, float:50.0)
            r25 = 0
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r19, r20, r21, r22, r23, r24, r25)
            r0.addView(r1, r2)
            long r0 = java.lang.System.currentTimeMillis()
            java.util.Calendar r2 = java.util.Calendar.getInstance()
            r2.setTimeInMillis(r0)
            r3 = 1
            int r4 = r2.get(r3)
            r5 = 6
            int r6 = r2.get(r5)
            android.widget.LinearLayout r7 = r8.scheduleTimerContainer
            r13 = 270(0x10e, float:3.78E-43)
            r14 = 1056964608(0x3var_, float:0.5)
            r3 = 0
            android.widget.LinearLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r3, (int) r13, (float) r14)
            r7.addView(r11, r13)
            r11.setMinValue(r3)
            r7 = 365(0x16d, float:5.11E-43)
            r11.setMaxValue(r7)
            r11.setWrapSelectorWheel(r3)
            org.telegram.ui.-$$Lambda$GroupCallActivity$_GI1wTUsP8AvKPCH8VDQs15VGXk r7 = new org.telegram.ui.-$$Lambda$GroupCallActivity$_GI1wTUsP8AvKPCH8VDQs15VGXk
            r7.<init>(r0, r2, r4)
            r11.setFormatter(r7)
            org.telegram.ui.-$$Lambda$GroupCallActivity$lTnOViOBODamIannlM0ABeicQc0 r4 = new org.telegram.ui.-$$Lambda$GroupCallActivity$lTnOViOBODamIannlM0ABeicQc0
            r7 = r42
            r4.<init>(r11, r7, r15)
            r11.setOnValueChangedListener(r4)
            r7.setMinValue(r3)
            r13 = 23
            r7.setMaxValue(r13)
            android.widget.LinearLayout r13 = r8.scheduleTimerContainer
            r14 = 270(0x10e, float:3.78E-43)
            r5 = 1045220557(0x3e4ccccd, float:0.2)
            android.widget.LinearLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r3, (int) r14, (float) r5)
            r13.addView(r7, r5)
            org.telegram.ui.-$$Lambda$GroupCallActivity$7O59dK0nFagDr_sx_K0UnV5IntA r5 = org.telegram.ui.$$Lambda$GroupCallActivity$7O59dK0nFagDr_sx_K0UnV5IntA.INSTANCE
            r7.setFormatter(r5)
            r7.setOnValueChangedListener(r4)
            r15.setMinValue(r3)
            r5 = 59
            r15.setMaxValue(r5)
            r15.setValue(r3)
            org.telegram.ui.-$$Lambda$GroupCallActivity$YNzry6mtmkOD34nDD2x6PVUKyQQ r5 = org.telegram.ui.$$Lambda$GroupCallActivity$YNzry6mtmkOD34nDD2x6PVUKyQQ.INSTANCE
            r15.setFormatter(r5)
            android.widget.LinearLayout r5 = r8.scheduleTimerContainer
            r13 = 270(0x10e, float:3.78E-43)
            r14 = 1050253722(0x3e99999a, float:0.3)
            android.widget.LinearLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r3, (int) r13, (float) r14)
            r5.addView(r15, r13)
            r15.setOnValueChangedListener(r4)
            r4 = 10800000(0xa4cb80, double:5.335909E-317)
            long r0 = r0 + r4
            r2.setTimeInMillis(r0)
            r0 = 12
            r2.set(r0, r3)
            r1 = 13
            r2.set(r1, r3)
            r1 = 14
            r2.set(r1, r3)
            r1 = 6
            int r1 = r2.get(r1)
            int r0 = r2.get(r0)
            r3 = 11
            int r2 = r2.get(r3)
            if (r6 == r1) goto L_0x0var_
            r13 = 1
            goto L_0x0var_
        L_0x0var_:
            r13 = 0
        L_0x0var_:
            r11.setValue(r13)
            r15.setValue(r0)
            r7.setValue(r2)
            android.widget.TextView r0 = r8.scheduleButtonTextView
            android.widget.TextView r1 = r8.scheduleInfoTextView
            r22 = 604800(0x93a80, double:2.98811E-318)
            r24 = 2
            r20 = r0
            r21 = r1
            r25 = r11
            r26 = r7
            r27 = r15
            org.telegram.ui.Components.AlertsCreator.checkScheduleDate(r20, r21, r22, r24, r25, r26, r27)
        L_0x0fb3:
            int r0 = android.os.Build.VERSION.SDK_INT
            r1 = 21
            if (r0 < r1) goto L_0x0fc4
            android.view.Window r0 = r40.getWindow()
            android.view.View r0 = r0.getDecorView()
            android.view.ViewGroup r0 = (android.view.ViewGroup) r0
            goto L_0x0fc6
        L_0x0fc4:
            android.view.ViewGroup r0 = r8.containerView
        L_0x0fc6:
            org.telegram.ui.GroupCallActivity$36 r1 = new org.telegram.ui.GroupCallActivity$36
            r1.<init>(r0)
            r8.pinchToZoomHelper = r1
            org.telegram.ui.GroupCallActivity$37 r0 = new org.telegram.ui.GroupCallActivity$37
            r0.<init>()
            r1.setCallback(r0)
            org.telegram.ui.PinchToZoomHelper r0 = r8.pinchToZoomHelper
            r12.setPinchToZoomHelper(r0)
            org.telegram.ui.Components.voip.VoIPToggleButton r0 = r8.cameraButton
            org.telegram.ui.-$$Lambda$GroupCallActivity$HtzKuWGjBIU0fs5Gph3nId0YsVc r1 = new org.telegram.ui.-$$Lambda$GroupCallActivity$HtzKuWGjBIU0fs5Gph3nId0YsVc
            r1.<init>(r9, r10)
            r0.setOnClickListener(r1)
            r0 = 0
            r8.updateScheduleUI(r0)
            r40.updateItems()
            r8.updateSpeakerPhoneIcon(r0)
            r8.updateState(r0, r0)
            r0 = 0
            r8.setColorProgress(r0)
            r40.updateSubtitle()
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
                if (!this.renderersContainer.inFullscreenMode) {
                    int indexOf = (this.delayedGroupCallUpdated ? this.oldParticipants : this.call.visibleParticipants).indexOf(tLRPC$TL_groupCallParticipant);
                    if (indexOf >= 0 && (findViewHolderForAdapterPosition = this.listView.findViewHolderForAdapterPosition(indexOf + this.listAdapter.usersStartRow)) != null) {
                        View view = findViewHolderForAdapterPosition.itemView;
                        if (view instanceof GroupCallUserCell) {
                            ((GroupCallUserCell) view).setAmplitude((double) (fArr[i] * 15.0f));
                            if (findViewHolderForAdapterPosition.itemView == this.scrimView && !this.contentFullyOverlayed) {
                                this.containerView.invalidate();
                            }
                        }
                    }
                } else {
                    for (int i2 = 0; i2 < this.fullscreenUsersListView.getChildCount(); i2++) {
                        GroupCallFullscreenAdapter.GroupCallUserCell groupCallUserCell = (GroupCallFullscreenAdapter.GroupCallUserCell) this.fullscreenUsersListView.getChildAt(i2);
                        if (MessageObject.getPeerId(groupCallUserCell.getParticipant().peer) == MessageObject.getPeerId(tLRPC$TL_groupCallParticipant.peer)) {
                            groupCallUserCell.setAmplitude((double) (fArr[i] * 15.0f));
                        }
                    }
                }
                this.renderersContainer.setAmplitude(tLRPC$TL_groupCallParticipant, fArr[i] * 15.0f);
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$12 */
    public /* synthetic */ void lambda$new$12$GroupCallActivity(View view, int i, float f, float f2) {
        if (view instanceof GroupCallGridCell) {
            fullscreenFor(((GroupCallGridCell) view).getParticipant());
        } else if (view instanceof GroupCallUserCell) {
            showMenuForCell((GroupCallUserCell) view);
        } else if (view instanceof GroupCallInvitedCell) {
            GroupCallInvitedCell groupCallInvitedCell = (GroupCallInvitedCell) view;
            if (groupCallInvitedCell.getUser() != null) {
                Bundle bundle = new Bundle();
                bundle.putInt("user_id", groupCallInvitedCell.getUser().id);
                if (groupCallInvitedCell.hasAvatarSet()) {
                    bundle.putBoolean("expandPhoto", true);
                }
                this.parentActivity.lambda$runLinkRequest$42(new ProfileActivity(bundle));
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
                        GroupCallActivity.this.lambda$new$11$GroupCallActivity(dialogInterface);
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
    /* renamed from: lambda$new$11 */
    public /* synthetic */ void lambda$new$11$GroupCallActivity(DialogInterface dialogInterface) {
        this.groupVoipInviteAlert = null;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$13 */
    public /* synthetic */ boolean lambda$new$13$GroupCallActivity(View view, int i) {
        if (view instanceof GroupCallGridCell) {
            showMenuForCell(view);
            this.listView.performHapticFeedback(0);
            return true;
        } else if (!(view instanceof GroupCallUserCell)) {
            return false;
        } else {
            updateItems();
            return ((GroupCallUserCell) view).clickMuteButton();
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$14 */
    public /* synthetic */ void lambda$new$14$GroupCallActivity(View view, int i) {
        GroupCallGridCell groupCallGridCell = (GroupCallGridCell) view;
        if (groupCallGridCell.getParticipant() != null) {
            fullscreenFor(groupCallGridCell.getParticipant());
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$15 */
    public /* synthetic */ void lambda$new$15$GroupCallActivity(View view) {
        ChatObject.Call call2 = this.call;
        if (call2 == null || call2.isScheduled()) {
            getLink(false);
        } else if (VoIPService.getSharedInstance() != null) {
            VoIPService.getSharedInstance().toggleSpeakerphoneOrShowRouteSheet(getContext(), false);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$16 */
    public /* synthetic */ void lambda$new$16$GroupCallActivity(View view) {
        this.renderersContainer.delayHideUi();
        VoIPService sharedInstance = VoIPService.getSharedInstance();
        if (sharedInstance != null) {
            if (sharedInstance.getVideoState(false) == 2) {
                sharedInstance.switchCamera();
                if (this.flipIconCurrentEndFrame == 18) {
                    RLottieDrawable rLottieDrawable = this.flipIcon;
                    this.flipIconCurrentEndFrame = 39;
                    rLottieDrawable.setCustomEndFrame(39);
                    this.flipIcon.start();
                } else {
                    this.flipIcon.setCurrentFrame(0, false);
                    RLottieDrawable rLottieDrawable2 = this.flipIcon;
                    this.flipIconCurrentEndFrame = 18;
                    rLottieDrawable2.setCustomEndFrame(18);
                    this.flipIcon.start();
                }
                for (int i = 0; i < this.attachedRenderers.size(); i++) {
                    GroupCallMiniTextureView groupCallMiniTextureView = this.attachedRenderers.get(i);
                    ChatObject.VideoParticipant videoParticipant = groupCallMiniTextureView.participant;
                    if (videoParticipant.participant.self && !videoParticipant.presentation) {
                        groupCallMiniTextureView.startFlipAnimation();
                    }
                }
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$17 */
    public /* synthetic */ void lambda$new$17$GroupCallActivity(Context context, View view) {
        this.renderersContainer.delayHideUi();
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
    /* renamed from: lambda$new$20 */
    public /* synthetic */ void lambda$new$20$GroupCallActivity(View view) {
        JoinCallAlert.open(getContext(), -this.currentChat.id, this.accountInstance, (BaseFragment) null, 2, this.selfPeer, new JoinCallAlert.JoinCallAlertDelegate() {
            public final void didSelectChat(TLRPC$InputPeer tLRPC$InputPeer, boolean z, boolean z2) {
                GroupCallActivity.this.lambda$new$19$GroupCallActivity(tLRPC$InputPeer, z, z2);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$19 */
    public /* synthetic */ void lambda$new$19$GroupCallActivity(TLRPC$InputPeer tLRPC$InputPeer, boolean z, boolean z2) {
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
                this.accountInstance.getConnectionsManager().sendRequest(tLRPC$TL_phone_saveDefaultGroupCallJoinAs, $$Lambda$GroupCallActivity$wvLL9sN02H3zjFB9QoY7WX1NBaM.INSTANCE);
                updateItems();
            } else if (VoIPService.getSharedInstance() != null && z) {
                TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant = this.call.participants.get(MessageObject.getPeerId(this.selfPeer));
                VoIPService.getSharedInstance().setGroupCallPeer(tLRPC$InputPeer);
                this.userSwitchObject = tLObject;
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$21 */
    public /* synthetic */ void lambda$new$21$GroupCallActivity(int i) {
        this.actionBar.getActionBarMenuOnItemClick().onItemClick(i);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$22 */
    public /* synthetic */ void lambda$new$22$GroupCallActivity(Context context, View view) {
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
            if (VoIPService.getSharedInstance() == null || this.soundButton.getVisibility() == 0 || (!VoIPService.getSharedInstance().hasEarpiece() && !VoIPService.getSharedInstance().isBluetoothHeadsetConnected())) {
                this.soundItem.setVisibility(8);
            } else {
                this.soundItem.setVisibility(0);
                int currentAudioRoute = VoIPService.getSharedInstance().getCurrentAudioRoute();
                if (currentAudioRoute == 2) {
                    this.soundItem.setIcon(NUM);
                    this.soundItem.setSubtext(VoIPService.getSharedInstance().currentBluetoothDeviceName != null ? VoIPService.getSharedInstance().currentBluetoothDeviceName : LocaleController.getString("VoipAudioRoutingBluetooth", NUM));
                } else {
                    int i = NUM;
                    if (currentAudioRoute == 0) {
                        ActionBarMenuSubItem actionBarMenuSubItem = this.soundItem;
                        if (VoIPService.getSharedInstance().isHeadsetPlugged()) {
                            i = NUM;
                        }
                        actionBarMenuSubItem.setIcon(i);
                        this.soundItem.setSubtext(VoIPService.getSharedInstance().isHeadsetPlugged() ? LocaleController.getString("VoipAudioRoutingHeadset", NUM) : LocaleController.getString("VoipAudioRoutingPhone", NUM));
                    } else if (currentAudioRoute == 1) {
                        if (((AudioManager) context.getSystemService("audio")).isSpeakerphoneOn()) {
                            this.soundItem.setIcon(NUM);
                            this.soundItem.setSubtext(LocaleController.getString("VoipAudioRoutingSpeaker", NUM));
                        } else {
                            this.soundItem.setIcon(NUM);
                            this.soundItem.setSubtext(LocaleController.getString("VoipAudioRoutingPhone", NUM));
                        }
                    }
                }
            }
            updateItems();
            this.otherItem.toggleSubMenu();
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$23 */
    public /* synthetic */ void lambda$new$23$GroupCallActivity(View view) {
        if (Build.VERSION.SDK_INT < 23 || Settings.canDrawOverlays(this.parentActivity)) {
            GroupCallPip.clearForce();
            dismiss();
            return;
        }
        AlertsCreator.createDrawOverlayGroupCallPermissionDialog(getContext()).show();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$24 */
    public /* synthetic */ void lambda$new$24$GroupCallActivity(View view) {
        VoIPService sharedInstance = VoIPService.getSharedInstance();
        if (sharedInstance != null) {
            if (sharedInstance.getVideoState(true) == 2) {
                sharedInstance.stopScreenCapture();
            } else {
                startScreenCapture();
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$25 */
    public /* synthetic */ void lambda$new$25$GroupCallActivity(View view) {
        ChatObject.Call call2 = this.call;
        if (call2 != null && call2.recording) {
            showRecordHint(this.actionBar.getTitleTextView());
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$26 */
    public /* synthetic */ void lambda$new$26$GroupCallActivity(View view, int i) {
        GroupCallFullscreenAdapter.GroupCallUserCell groupCallUserCell = (GroupCallFullscreenAdapter.GroupCallUserCell) view;
        if (groupCallUserCell.getVideoParticipant() == null) {
            fullscreenFor(new ChatObject.VideoParticipant(groupCallUserCell.getParticipant(), false, false));
        } else {
            fullscreenFor(groupCallUserCell.getVideoParticipant());
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$27 */
    public /* synthetic */ boolean lambda$new$27$GroupCallActivity(View view, int i) {
        showMenuForCell(view);
        this.listView.performHapticFeedback(0);
        return false;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$32 */
    public /* synthetic */ void lambda$new$32$GroupCallActivity(NumberPicker numberPicker, NumberPicker numberPicker2, NumberPicker numberPicker3, TLRPC$Chat tLRPC$Chat, AccountInstance accountInstance2, TLRPC$InputPeer tLRPC$InputPeer, View view) {
        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
        this.scheduleAnimator = ofFloat;
        ofFloat.setDuration(600);
        this.scheduleAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                GroupCallActivity.this.lambda$new$28$GroupCallActivity(valueAnimator);
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
                GroupCallActivity.this.lambda$new$31$GroupCallActivity(this.f$1, this.f$2, tLObject, tLRPC$TL_error);
            }
        }, 2);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$28 */
    public /* synthetic */ void lambda$new$28$GroupCallActivity(ValueAnimator valueAnimator) {
        this.switchToButtonProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        updateScheduleUI(true);
        this.buttonsContainer.invalidate();
        this.listView.invalidate();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$31 */
    public /* synthetic */ void lambda$new$31$GroupCallActivity(TLRPC$Chat tLRPC$Chat, TLRPC$InputPeer tLRPC$InputPeer, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
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
                            GroupCallActivity.this.lambda$new$29$GroupCallActivity(this.f$1, this.f$2, this.f$3);
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
                GroupCallActivity.this.lambda$new$30$GroupCallActivity(this.f$1);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$29 */
    public /* synthetic */ void lambda$new$29$GroupCallActivity(TLRPC$Chat tLRPC$Chat, TLRPC$InputPeer tLRPC$InputPeer, TLRPC$TL_updateGroupCall tLRPC$TL_updateGroupCall) {
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
        ChatObject.Call call4 = this.call;
        TLRPC$GroupCall tLRPC$GroupCall2 = call4.call;
        TLRPC$GroupCall tLRPC$GroupCall3 = tLRPC$TL_updateGroupCall.call;
        tLRPC$GroupCall2.access_hash = tLRPC$GroupCall3.access_hash;
        tLRPC$GroupCall2.id = tLRPC$GroupCall3.id;
        this.fullscreenAdapter.setGroupCall(call4);
        this.renderersContainer.setGroupCall(this.call);
        this.tabletGridAdapter.setGroupCall(this.call);
        MessagesController messagesController = this.accountInstance.getMessagesController();
        ChatObject.Call call5 = this.call;
        messagesController.putGroupCall(call5.chatId, call5);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$30 */
    public /* synthetic */ void lambda$new$30$GroupCallActivity(TLRPC$TL_error tLRPC$TL_error) {
        this.accountInstance.getNotificationCenter().postNotificationName(NotificationCenter.needShowAlert, 6, tLRPC$TL_error.text);
        dismiss();
    }

    static /* synthetic */ String lambda$new$33(long j, Calendar calendar, int i, int i2) {
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
    /* renamed from: lambda$new$34 */
    public /* synthetic */ void lambda$new$34$GroupCallActivity(NumberPicker numberPicker, NumberPicker numberPicker2, NumberPicker numberPicker3, NumberPicker numberPicker4, int i, int i2) {
        try {
            this.container.performHapticFeedback(3, 2);
        } catch (Exception unused) {
        }
        AlertsCreator.checkScheduleDate(this.scheduleButtonTextView, this.scheduleInfoTextView, 604800, 2, numberPicker, numberPicker2, numberPicker3);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$37 */
    public /* synthetic */ void lambda$new$37$GroupCallActivity(Context context, ChatObject.Call call2, View view) {
        LaunchActivity launchActivity;
        if (Build.VERSION.SDK_INT >= 23 && (launchActivity = this.parentActivity) != null && launchActivity.checkSelfPermission("android.permission.CAMERA") != 0) {
            this.parentActivity.requestPermissions(new String[]{"android.permission.CAMERA"}, 104);
        } else if (VoIPService.getSharedInstance() != null) {
            if (VoIPService.getSharedInstance().getVideoState(false) != 2) {
                this.undoView[0].hide(false, 1);
                if (this.previewDialog == null) {
                    if (VoIPService.getSharedInstance() != null) {
                        VoIPService.getSharedInstance().createCaptureDevice(false);
                    }
                    AnonymousClass38 r5 = new VideoPreviewDialog(context, this.listView, this.fullscreenUsersListView) {
                        public void onDismiss(boolean z) {
                            GroupCallActivity groupCallActivity = GroupCallActivity.this;
                            boolean z2 = groupCallActivity.previewDialog.micEnabled;
                            groupCallActivity.previewDialog = null;
                            VoIPService sharedInstance = VoIPService.getSharedInstance();
                            if (z) {
                                if (sharedInstance != null) {
                                    sharedInstance.setupCaptureDevice(false, z2);
                                }
                                GroupCallActivity.this.updateState(true, false);
                                GroupCallActivity.this.call.sortParticipants();
                                GroupCallActivity.this.applyCallParticipantUpdates();
                                GroupCallActivity.this.buttonsContainer.requestLayout();
                            } else if (sharedInstance != null) {
                                VoIPService.getSharedInstance().setVideoState(false, 0);
                            }
                        }
                    };
                    this.previewDialog = r5;
                    this.containerView.addView(r5);
                    if (!VoIPService.getSharedInstance().isFrontFaceCamera()) {
                        VoIPService.getSharedInstance().switchCamera();
                        return;
                    }
                    return;
                }
                return;
            }
            VoIPService.getSharedInstance().setVideoState(false, 0);
            TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant = call2.fullscreenParticipant;
            if (tLRPC$TL_groupCallParticipant != null && tLRPC$TL_groupCallParticipant.self) {
                call2.setFullscreenParticipant((TLRPC$TL_groupCallParticipant) null);
                VoIPService.getSharedInstance().setLocalSink((VideoSink) null, false);
            }
            updateState(true, false);
            updateSpeakerPhoneIcon(false);
            this.call.sortParticipants();
            applyCallParticipantUpdates();
            this.buttonsContainer.requestLayout();
        }
    }

    public void fullscreenFor(final ChatObject.VideoParticipant videoParticipant) {
        ChatObject.VideoParticipant videoParticipant2;
        if (VoIPService.getSharedInstance() != null && !this.renderersContainer.isAnimating()) {
            if (isTabletMode) {
                if (this.requestFullscreenListener != null) {
                    this.listView.getViewTreeObserver().removeOnPreDrawListener(this.requestFullscreenListener);
                    this.requestFullscreenListener = null;
                }
                ArrayList arrayList = new ArrayList();
                if (videoParticipant == null) {
                    this.attachedRenderersTmp.clear();
                    this.attachedRenderersTmp.addAll(this.attachedRenderers);
                    for (int i = 0; i < this.attachedRenderersTmp.size(); i++) {
                        final GroupCallMiniTextureView groupCallMiniTextureView = this.attachedRenderersTmp.get(i);
                        GroupCallGridCell groupCallGridCell = groupCallMiniTextureView.primaryView;
                        if (groupCallGridCell != null) {
                            groupCallGridCell.setRenderer((GroupCallMiniTextureView) null);
                            GroupCallFullscreenAdapter.GroupCallUserCell groupCallUserCell = groupCallMiniTextureView.secondaryView;
                            if (groupCallUserCell != null) {
                                groupCallUserCell.setRenderer((GroupCallMiniTextureView) null);
                            }
                            GroupCallGridCell groupCallGridCell2 = groupCallMiniTextureView.tabletGridView;
                            if (groupCallGridCell2 != null) {
                                groupCallGridCell2.setRenderer((GroupCallMiniTextureView) null);
                            }
                            arrayList.add(groupCallMiniTextureView.participant);
                            groupCallMiniTextureView.forceDetach(false);
                            groupCallMiniTextureView.animate().alpha(0.0f).setListener(new AnimatorListenerAdapter() {
                                public void onAnimationEnd(Animator animator) {
                                    if (groupCallMiniTextureView.getParent() != null) {
                                        GroupCallActivity.this.containerView.removeView(groupCallMiniTextureView);
                                    }
                                }
                            });
                        }
                    }
                    this.listViewVideoVisibility = false;
                    this.tabletGridAdapter.setVisibility(this.tabletVideoGridView, true, true);
                } else {
                    this.attachedRenderersTmp.clear();
                    this.attachedRenderersTmp.addAll(this.attachedRenderers);
                    for (int i2 = 0; i2 < this.attachedRenderersTmp.size(); i2++) {
                        final GroupCallMiniTextureView groupCallMiniTextureView2 = this.attachedRenderersTmp.get(i2);
                        if (groupCallMiniTextureView2.tabletGridView != null && ((videoParticipant2 = groupCallMiniTextureView2.participant) == null || !videoParticipant2.equals(videoParticipant))) {
                            arrayList.add(groupCallMiniTextureView2.participant);
                            groupCallMiniTextureView2.forceDetach(false);
                            GroupCallFullscreenAdapter.GroupCallUserCell groupCallUserCell2 = groupCallMiniTextureView2.secondaryView;
                            if (groupCallUserCell2 != null) {
                                groupCallUserCell2.setRenderer((GroupCallMiniTextureView) null);
                            }
                            GroupCallGridCell groupCallGridCell3 = groupCallMiniTextureView2.primaryView;
                            if (groupCallGridCell3 != null) {
                                groupCallGridCell3.setRenderer((GroupCallMiniTextureView) null);
                            }
                            groupCallMiniTextureView2.animate().alpha(0.0f).setListener(new AnimatorListenerAdapter() {
                                public void onAnimationEnd(Animator animator) {
                                    if (groupCallMiniTextureView2.getParent() != null) {
                                        GroupCallActivity.this.containerView.removeView(groupCallMiniTextureView2);
                                    }
                                }
                            });
                        }
                    }
                    this.listViewVideoVisibility = true;
                    this.tabletGridAdapter.setVisibility(this.tabletVideoGridView, false, false);
                    if (!arrayList.isEmpty()) {
                        AndroidUtilities.runOnUIThread(new Runnable(arrayList) {
                            public final /* synthetic */ ArrayList f$1;

                            {
                                this.f$1 = r2;
                            }

                            public final void run() {
                                GroupCallActivity.this.lambda$fullscreenFor$38$GroupCallActivity(this.f$1);
                            }
                        });
                    }
                }
                final boolean z = !this.renderersContainer.inFullscreenMode;
                ViewTreeObserver viewTreeObserver = this.listView.getViewTreeObserver();
                AnonymousClass41 r2 = new ViewTreeObserver.OnPreDrawListener() {
                    public boolean onPreDraw() {
                        GroupCallActivity.this.listView.getViewTreeObserver().removeOnPreDrawListener(this);
                        GroupCallActivity groupCallActivity = GroupCallActivity.this;
                        groupCallActivity.requestFullscreenListener = null;
                        groupCallActivity.renderersContainer.requestFullscreen(videoParticipant);
                        if (GroupCallActivity.this.delayedGroupCallUpdated) {
                            boolean unused = GroupCallActivity.this.delayedGroupCallUpdated = false;
                            GroupCallActivity.this.applyCallParticipantUpdates();
                            if (z && videoParticipant != null) {
                                GroupCallActivity.this.listView.scrollToPosition(0);
                            }
                            boolean unused2 = GroupCallActivity.this.delayedGroupCallUpdated = true;
                        } else {
                            GroupCallActivity.this.applyCallParticipantUpdates();
                        }
                        return false;
                    }
                };
                this.requestFullscreenListener = r2;
                viewTreeObserver.addOnPreDrawListener(r2);
                return;
            }
            if (this.requestFullscreenListener != null) {
                this.listView.getViewTreeObserver().removeOnPreDrawListener(this.requestFullscreenListener);
                this.requestFullscreenListener = null;
            }
            if (videoParticipant != null) {
                if (this.fullscreenUsersListView.getVisibility() != 0) {
                    this.fullscreenUsersListView.setVisibility(0);
                    this.fullscreenAdapter.update(false, this.fullscreenUsersListView);
                    this.delayedGroupCallUpdated = true;
                    if (!this.renderersContainer.inFullscreenMode) {
                        this.fullscreenAdapter.scrollTo(videoParticipant, this.fullscreenUsersListView);
                    }
                    ViewTreeObserver viewTreeObserver2 = this.listView.getViewTreeObserver();
                    AnonymousClass42 r1 = new ViewTreeObserver.OnPreDrawListener() {
                        public boolean onPreDraw() {
                            GroupCallActivity.this.listView.getViewTreeObserver().removeOnPreDrawListener(this);
                            GroupCallActivity groupCallActivity = GroupCallActivity.this;
                            groupCallActivity.requestFullscreenListener = null;
                            groupCallActivity.renderersContainer.requestFullscreen(videoParticipant);
                            AndroidUtilities.updateVisibleRows(GroupCallActivity.this.fullscreenUsersListView);
                            return false;
                        }
                    };
                    this.requestFullscreenListener = r1;
                    viewTreeObserver2.addOnPreDrawListener(r1);
                    return;
                }
                this.renderersContainer.requestFullscreen(videoParticipant);
                AndroidUtilities.updateVisibleRows(this.fullscreenUsersListView);
            } else if (this.listView.getVisibility() != 0) {
                this.listView.setVisibility(0);
                applyCallParticipantUpdates();
                this.delayedGroupCallUpdated = true;
                ViewTreeObserver viewTreeObserver3 = this.listView.getViewTreeObserver();
                AnonymousClass43 r0 = new ViewTreeObserver.OnPreDrawListener() {
                    public boolean onPreDraw() {
                        GroupCallActivity.this.listView.getViewTreeObserver().removeOnPreDrawListener(this);
                        GroupCallActivity.this.renderersContainer.requestFullscreen((ChatObject.VideoParticipant) null);
                        AndroidUtilities.updateVisibleRows(GroupCallActivity.this.fullscreenUsersListView);
                        return false;
                    }
                };
                this.requestFullscreenListener = r0;
                viewTreeObserver3.addOnPreDrawListener(r0);
            } else {
                ViewTreeObserver viewTreeObserver4 = this.listView.getViewTreeObserver();
                AnonymousClass44 r02 = new ViewTreeObserver.OnPreDrawListener() {
                    public boolean onPreDraw() {
                        GroupCallActivity.this.listView.getViewTreeObserver().removeOnPreDrawListener(this);
                        GroupCallActivity.this.renderersContainer.requestFullscreen((ChatObject.VideoParticipant) null);
                        AndroidUtilities.updateVisibleRows(GroupCallActivity.this.fullscreenUsersListView);
                        return false;
                    }
                };
                this.requestFullscreenListener = r02;
                viewTreeObserver4.addOnPreDrawListener(r02);
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$fullscreenFor$38 */
    public /* synthetic */ void lambda$fullscreenFor$38$GroupCallActivity(ArrayList arrayList) {
        for (int i = 0; i < this.attachedRenderers.size(); i++) {
            if (this.attachedRenderers.get(i).participant != null) {
                arrayList.remove(this.attachedRenderers.get(i).participant);
            }
        }
        for (int i2 = 0; i2 < arrayList.size(); i2++) {
            ChatObject.VideoParticipant videoParticipant = (ChatObject.VideoParticipant) arrayList.get(i2);
            if (videoParticipant.participant.self) {
                if (VoIPService.getSharedInstance() != null) {
                    VoIPService.getSharedInstance().setLocalSink((VideoSink) null, videoParticipant.presentation);
                }
            } else if (VoIPService.getSharedInstance() != null) {
                VoIPService.getSharedInstance().removeRemoteSink(videoParticipant.participant, videoParticipant.presentation);
            }
        }
    }

    public void enableCamera() {
        this.cameraButton.callOnClick();
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
        this.cameraButton.setAlpha(f);
        this.cameraButton.setScaleY(f2);
        this.cameraButton.setScaleX(f2);
        this.flipButton.setAlpha(f);
        this.flipButton.setScaleY(f2);
        this.flipButton.setScaleX(f2);
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
            this.oldParticipants.addAll(this.call.visibleParticipants);
            this.oldVideoParticipants.addAll(this.visibleVideoParticipants);
            this.oldInvited.addAll(this.call.invitedUsers);
            this.currentCallState = sharedInstance.getCallState();
            if (this.call == null) {
                ChatObject.Call call2 = sharedInstance.groupCall;
                this.call = call2;
                this.fullscreenAdapter.setGroupCall(call2);
                this.renderersContainer.setGroupCall(this.call);
                this.tabletGridAdapter.setGroupCall(this.call);
            }
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

    private void updateSubtitle() {
        if (this.actionBar != null && this.call != null) {
            boolean z = false;
            SpannableStringBuilder spannableStringBuilder = null;
            int i = 0;
            for (int i2 = 0; i2 < this.call.currentSpeakingPeers.size(); i2++) {
                TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant = this.call.currentSpeakingPeers.get(this.call.currentSpeakingPeers.keyAt(i2));
                if (!tLRPC$TL_groupCallParticipant.self && !this.renderersContainer.isVisible(tLRPC$TL_groupCallParticipant)) {
                    int peerId = MessageObject.getPeerId(tLRPC$TL_groupCallParticipant.peer);
                    if (!(SystemClock.uptimeMillis() - tLRPC$TL_groupCallParticipant.lastSpeakTime < 500)) {
                        continue;
                    } else {
                        if (spannableStringBuilder == null) {
                            spannableStringBuilder = new SpannableStringBuilder();
                        }
                        if (i < 2) {
                            TLRPC$User user = peerId > 0 ? MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(peerId)) : null;
                            TLRPC$Chat chat = peerId <= 0 ? MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(peerId)) : null;
                            if (user != null || chat != null) {
                                if (i != 0) {
                                    spannableStringBuilder.append(", ");
                                }
                                if (user != null) {
                                    if (Build.VERSION.SDK_INT >= 21) {
                                        spannableStringBuilder.append(UserObject.getFirstName(user), new TypefaceSpan(AndroidUtilities.getTypeface("fonts/rmedium.ttf")), 0);
                                    } else {
                                        spannableStringBuilder.append(UserObject.getFirstName(user));
                                    }
                                } else if (Build.VERSION.SDK_INT >= 21) {
                                    spannableStringBuilder.append(chat.title, new TypefaceSpan(AndroidUtilities.getTypeface("fonts/rmedium.ttf")), 0);
                                } else {
                                    spannableStringBuilder.append(chat.title);
                                }
                            }
                        }
                        i++;
                        if (i == 2) {
                            break;
                        }
                    }
                }
            }
            if (i > 0) {
                String pluralString = LocaleController.getPluralString("MembersAreSpeakingToast", i);
                int indexOf = pluralString.indexOf("un1");
                SpannableStringBuilder spannableStringBuilder2 = new SpannableStringBuilder(pluralString);
                spannableStringBuilder2.replace(indexOf, indexOf + 3, spannableStringBuilder);
                this.actionBar.getAdditionalSubtitleTextView().setText(spannableStringBuilder2);
                this.actionBar.getAdditionalSubtitleTextView().setTranslationX((float) AndroidUtilities.dp(24.0f));
                z = true;
            }
            this.actionBar.getSubtitleTextView().setText(LocaleController.formatPluralString("Participants", this.call.call.participants_count + (this.listAdapter.addSelfToCounter() ? 1 : 0)));
            if (z != this.drawSpeakingSubtitle) {
                this.drawSpeakingSubtitle = z;
                this.actionBar.invalidate();
                float f = 0.0f;
                this.actionBar.getSubtitleTextView().setPivotX(0.0f);
                this.actionBar.getSubtitleTextView().setPivotY((float) (this.actionBar.getMeasuredHeight() >> 1));
                ViewPropertyAnimator scaleY = this.actionBar.getSubtitleTextView().animate().scaleX(this.drawSpeakingSubtitle ? 0.98f : 1.0f).scaleY(this.drawSpeakingSubtitle ? 0.9f : 1.0f);
                if (!this.drawSpeakingSubtitle) {
                    f = 1.0f;
                }
                scaleY.alpha(f).setDuration(150);
                AndroidUtilities.updateViewVisibilityAnimated(this.actionBar.getAdditionalSubtitleTextView(), this.drawSpeakingSubtitle);
            }
        }
    }

    public void dismissInternal() {
        if (this.renderersContainer != null) {
            if (this.requestFullscreenListener != null) {
                this.listView.getViewTreeObserver().removeOnPreDrawListener(this.requestFullscreenListener);
                this.requestFullscreenListener = null;
            }
            this.attachedRenderersTmp.clear();
            this.attachedRenderersTmp.addAll(this.attachedRenderers);
            for (int i = 0; i < this.attachedRenderersTmp.size(); i++) {
                this.attachedRenderersTmp.get(i).saveThumb();
                this.renderersContainer.removeView(this.attachedRenderersTmp.get(i));
                this.attachedRenderersTmp.get(i).release();
                this.attachedRenderersTmp.get(i).forceDetach(true);
            }
            this.attachedRenderers.clear();
            if (this.renderersContainer.getParent() != null) {
                this.attachedRenderers.clear();
                this.containerView.removeView(this.renderersContainer);
            }
        }
        super.dismissInternal();
        if (VoIPService.getSharedInstance() != null) {
            VoIPService.getSharedInstance().unregisterStateListener(this);
            VoIPService.getSharedInstance().setSinks((VideoSink) null, (VideoSink) null);
        }
        if (groupCallInstance == this) {
            groupCallInstance = null;
        }
        groupCallUiVisible = false;
        VoIPService.audioLevelsCallback = null;
        GroupCallPip.updateVisibility(getContext());
        this.call.setFullscreenParticipant((TLRPC$TL_groupCallParticipant) null);
        this.call.clearVideFramesInfo();
        if (VoIPService.getSharedInstance() != null) {
            VoIPService.getSharedInstance().clearRemoteSinks();
        }
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

    public UndoView getUndoView() {
        if (!isTabletMode) {
            GroupCallRenderersContainer groupCallRenderersContainer = this.renderersContainer;
            if (groupCallRenderersContainer.inFullscreenMode) {
                return groupCallRenderersContainer.getUndoView();
            }
        }
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
                            GroupCallActivity.this.lambda$updateTitle$39$GroupCallActivity(view);
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
                        GroupCallActivity.this.lambda$updateTitle$40$GroupCallActivity(view);
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
    /* renamed from: lambda$updateTitle$39 */
    public /* synthetic */ void lambda$updateTitle$39$GroupCallActivity(View view) {
        ChatObject.Call call2 = this.call;
        if (call2 != null && call2.recording) {
            showRecordHint(this.actionBar.getTitleTextView());
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$updateTitle$40 */
    public /* synthetic */ void lambda$updateTitle$40$GroupCallActivity(View view) {
        ChatObject.Call call2 = this.call;
        if (call2 != null && call2.recording) {
            showRecordHint(this.actionBar.getTitleTextView());
        }
    }

    /* access modifiers changed from: private */
    public void setColorProgress(float f) {
        String str;
        String str2;
        this.colorProgress = f;
        GroupCallRenderersContainer groupCallRenderersContainer = this.renderersContainer;
        float max = Math.max(f, groupCallRenderersContainer == null ? 0.0f : groupCallRenderersContainer.progressToFullscreenMode);
        int offsetColor = AndroidUtilities.getOffsetColor(Theme.getColor("voipgroup_actionBarUnscrolled"), Theme.getColor("voipgroup_actionBar"), f, 1.0f);
        this.backgroundColor = offsetColor;
        this.actionBarBackground.setBackgroundColor(offsetColor);
        this.otherItem.redrawPopup(-14472653);
        this.shadowDrawable.setColorFilter(new PorterDuffColorFilter(this.backgroundColor, PorterDuff.Mode.MULTIPLY));
        this.navBarColor = AndroidUtilities.getOffsetColor(Theme.getColor("voipgroup_actionBarUnscrolled"), Theme.getColor("voipgroup_actionBar"), max, 1.0f);
        int offsetColor2 = AndroidUtilities.getOffsetColor(Theme.getColor("voipgroup_listViewBackgroundUnscrolled"), Theme.getColor("voipgroup_listViewBackground"), f, 1.0f);
        this.listViewBackgroundPaint.setColor(offsetColor2);
        this.listView.setGlowColor(offsetColor2);
        int i = this.muteButtonState;
        if (i == 3 || isGradientState(i)) {
            this.muteButton.invalidate();
        }
        View view = this.buttonsBackgroundGradientView;
        if (view != null) {
            int[] iArr = this.gradientColors;
            iArr[0] = this.backgroundColor;
            iArr[1] = 0;
            if (Build.VERSION.SDK_INT > 29) {
                this.buttonsBackgroundGradient.setColors(iArr);
            } else {
                GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, this.gradientColors);
                this.buttonsBackgroundGradient = gradientDrawable;
                view.setBackground(gradientDrawable);
            }
            this.buttonsBackgroundGradientView2.setBackgroundColor(this.gradientColors[0]);
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
                        GroupCallActivity.this.lambda$getLink$42$GroupCallActivity(this.f$1, this.f$2, tLObject, tLRPC$TL_error);
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
                        GroupCallActivity.this.lambda$getLink$44$GroupCallActivity(this.f$1, this.f$2, tLObject, tLRPC$TL_error);
                    }
                });
                i++;
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$getLink$42 */
    public /* synthetic */ void lambda$getLink$42$GroupCallActivity(TLRPC$ChatFull tLRPC$ChatFull, boolean z, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
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
                GroupCallActivity.this.lambda$getLink$41$GroupCallActivity(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$getLink$41 */
    public /* synthetic */ void lambda$getLink$41$GroupCallActivity(TLObject tLObject, TLRPC$ChatFull tLRPC$ChatFull, boolean z) {
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
    /* renamed from: lambda$getLink$44 */
    public /* synthetic */ void lambda$getLink$44$GroupCallActivity(int i, boolean z, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
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
                GroupCallActivity.this.lambda$getLink$43$GroupCallActivity(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$getLink$43 */
    public /* synthetic */ void lambda$getLink$43$GroupCallActivity(TLObject tLObject, int i, boolean z) {
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
        if (!z && ChatObject.canManageCalls(this.currentChat) && !this.call.call.join_muted) {
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

    /* JADX WARNING: Removed duplicated region for block: B:22:0x009b  */
    /* JADX WARNING: Removed duplicated region for block: B:23:0x009e  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void openShareAlert(boolean r16, java.lang.String r17, java.lang.String r18, boolean r19) {
        /*
            r15 = this;
            r12 = r15
            if (r19 == 0) goto L_0x001e
            if (r17 == 0) goto L_0x0008
            r0 = r17
            goto L_0x000a
        L_0x0008:
            r0 = r18
        L_0x000a:
            org.telegram.messenger.AndroidUtilities.addToClipboard(r0)
            org.telegram.ui.Components.UndoView r1 = r15.getUndoView()
            r2 = 0
            r4 = 33
            r5 = 0
            r6 = 0
            r7 = 0
            r8 = 0
            r1.showWithAction(r2, r4, r5, r6, r7, r8)
            goto L_0x00a3
        L_0x001e:
            org.telegram.ui.LaunchActivity r0 = r12.parentActivity
            r1 = 0
            r2 = 1
            if (r0 == 0) goto L_0x004d
            org.telegram.ui.ActionBar.ActionBarLayout r0 = r0.getActionBarLayout()
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r0 = r0.fragmentsStack
            org.telegram.ui.LaunchActivity r3 = r12.parentActivity
            org.telegram.ui.ActionBar.ActionBarLayout r3 = r3.getActionBarLayout()
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r3 = r3.fragmentsStack
            int r3 = r3.size()
            int r3 = r3 - r2
            java.lang.Object r0 = r0.get(r3)
            org.telegram.ui.ActionBar.BaseFragment r0 = (org.telegram.ui.ActionBar.BaseFragment) r0
            boolean r3 = r0 instanceof org.telegram.ui.ChatActivity
            if (r3 == 0) goto L_0x004d
            org.telegram.ui.ChatActivity r0 = (org.telegram.ui.ChatActivity) r0
            boolean r0 = r0.needEnterText()
            r12.anyEnterEventSent = r2
            r12.enterEventSent = r2
            r13 = r0
            goto L_0x004e
        L_0x004d:
            r13 = 0
        L_0x004e:
            if (r17 == 0) goto L_0x0057
            if (r18 != 0) goto L_0x0057
            r0 = 0
            r8 = r17
            r9 = r0
            goto L_0x005b
        L_0x0057:
            r9 = r17
            r8 = r18
        L_0x005b:
            if (r9 != 0) goto L_0x006e
            if (r16 == 0) goto L_0x006e
            r0 = 2131628107(0x7f0e104b, float:1.8883497E38)
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r2[r1] = r8
            java.lang.String r1 = "VoipGroupInviteText"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            r5 = r0
            goto L_0x006f
        L_0x006e:
            r5 = r8
        L_0x006f:
            org.telegram.ui.GroupCallActivity$46 r14 = new org.telegram.ui.GroupCallActivity$46
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
            org.telegram.ui.GroupCallActivity$47 r0 = new org.telegram.ui.GroupCallActivity$47
            r0.<init>()
            r14.setDelegate(r0)
            org.telegram.ui.Components.ShareAlert r0 = r12.shareAlert
            org.telegram.ui.-$$Lambda$GroupCallActivity$4AdtfAj3lY3OKCUP-DwvcwOue70 r1 = new org.telegram.ui.-$$Lambda$GroupCallActivity$4AdtfAj3lY3OKCUP-DwvcwOue70
            r1.<init>()
            r0.setOnDismissListener(r1)
            org.telegram.ui.-$$Lambda$GroupCallActivity$s5Jzm-7unCEaCrBH4rACEPwbd6I r0 = new org.telegram.ui.-$$Lambda$GroupCallActivity$s5Jzm-7unCEaCrBH4rACEPwbd6I
            r0.<init>()
            if (r13 == 0) goto L_0x009e
            r1 = 200(0xc8, double:9.9E-322)
            goto L_0x00a0
        L_0x009e:
            r1 = 0
        L_0x00a0:
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r0, r1)
        L_0x00a3:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.GroupCallActivity.openShareAlert(boolean, java.lang.String, java.lang.String, boolean):void");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$openShareAlert$45 */
    public /* synthetic */ void lambda$openShareAlert$45$GroupCallActivity(DialogInterface dialogInterface) {
        this.shareAlert = null;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$openShareAlert$46 */
    public /* synthetic */ void lambda$openShareAlert$46$GroupCallActivity() {
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
                    GroupCallActivity.this.lambda$inviteUserToCall$49$GroupCallActivity(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, tLObject, tLRPC$TL_error);
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
                        GroupCallActivity.this.lambda$inviteUserToCall$51$GroupCallActivity(this.f$1, this.f$2);
                    }
                }, 500);
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$inviteUserToCall$49 */
    public /* synthetic */ void lambda$inviteUserToCall$49$GroupCallActivity(int i, AlertDialog[] alertDialogArr, TLRPC$User tLRPC$User, boolean z, TLRPC$TL_phone_inviteToGroupCall tLRPC$TL_phone_inviteToGroupCall, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
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
                    GroupCallActivity.this.lambda$inviteUserToCall$47$GroupCallActivity(this.f$1, this.f$2, this.f$3);
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
                GroupCallActivity.this.lambda$inviteUserToCall$48$GroupCallActivity(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$inviteUserToCall$47 */
    public /* synthetic */ void lambda$inviteUserToCall$47$GroupCallActivity(int i, AlertDialog[] alertDialogArr, TLRPC$User tLRPC$User) {
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
    /* renamed from: lambda$inviteUserToCall$48 */
    public /* synthetic */ void lambda$inviteUserToCall$48$GroupCallActivity(AlertDialog[] alertDialogArr, boolean z, TLRPC$TL_error tLRPC$TL_error, int i, TLRPC$TL_phone_inviteToGroupCall tLRPC$TL_phone_inviteToGroupCall) {
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
    /* renamed from: lambda$inviteUserToCall$51 */
    public /* synthetic */ void lambda$inviteUserToCall$51$GroupCallActivity(AlertDialog[] alertDialogArr, int i) {
        if (alertDialogArr[0] != null) {
            alertDialogArr[0].setOnCancelListener(new DialogInterface.OnCancelListener(i) {
                public final /* synthetic */ int f$1;

                {
                    this.f$1 = r2;
                }

                public final void onCancel(DialogInterface dialogInterface) {
                    GroupCallActivity.this.lambda$inviteUserToCall$50$GroupCallActivity(this.f$1, dialogInterface);
                }
            });
            alertDialogArr[0].show();
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$inviteUserToCall$50 */
    public /* synthetic */ void lambda$inviteUserToCall$50$GroupCallActivity(int i, DialogInterface dialogInterface) {
        this.accountInstance.getConnectionsManager().cancelRequest(i, true);
    }

    /* access modifiers changed from: private */
    public void updateLayout(boolean z) {
        float f;
        float f2;
        float f3;
        float f4;
        int childCount = this.listView.getChildCount();
        float f5 = 2.14748365E9f;
        for (int i = 0; i < childCount; i++) {
            View childAt = this.listView.getChildAt(i);
            if (this.listView.getChildAdapterPosition(childAt) >= 0) {
                f5 = Math.min(f5, (float) childAt.getTop());
            }
        }
        float f6 = 0.0f;
        if (f5 < 0.0f || f5 == 2.14748365E9f) {
            if (childCount != 0) {
                f5 = 0.0f;
            } else {
                f5 = (float) this.listView.getPaddingTop();
            }
        }
        boolean z2 = f5 <= ((float) (ActionBar.getCurrentActionBarHeight() - AndroidUtilities.dp(14.0f)));
        float currentActionBarHeight = f5 + ((float) (ActionBar.getCurrentActionBarHeight() + AndroidUtilities.dp(14.0f)));
        if ((z2 && this.actionBar.getTag() == null) || (!z2 && this.actionBar.getTag() != null)) {
            this.actionBar.setTag(z2 ? 1 : null);
            AnimatorSet animatorSet = this.actionBarAnimation;
            if (animatorSet != null) {
                animatorSet.cancel();
                this.actionBarAnimation = null;
            }
            setUseLightStatusBar(this.actionBar.getTag() == null);
            float f7 = 0.9f;
            ViewPropertyAnimator scaleX = this.actionBar.getBackButton().animate().scaleX(z2 ? 1.0f : 0.9f);
            if (z2) {
                f7 = 1.0f;
            }
            ViewPropertyAnimator scaleY = scaleX.scaleY(f7);
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
            ViewPropertyAnimator animate3 = this.actionBar.getAdditionalSubtitleTextView().animate();
            if (z2) {
                f4 = 0.0f;
            } else {
                f4 = (float) AndroidUtilities.dp(20.0f);
            }
            animate3.translationY(f4).setDuration(300).setInterpolator(cubicBezierInterpolator).start();
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
            if (z2) {
                f6 = 1.0f;
            }
            fArr3[0] = f6;
            animatorArr[2] = ObjectAnimator.ofFloat(view2, property3, fArr3);
            animatorSet3.playTogether(animatorArr);
            this.actionBarAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    AnimatorSet unused = GroupCallActivity.this.actionBarAnimation = null;
                }
            });
            this.actionBarAnimation.start();
        }
        if (this.scrollOffsetY != currentActionBarHeight) {
            setScrollOffsetY(currentActionBarHeight);
        }
    }

    private void setScrollOffsetY(float f) {
        int i;
        this.scrollOffsetY = f;
        this.listView.setTopGlowOffset((int) (f - ((float) ((FrameLayout.LayoutParams) this.listView.getLayoutParams()).topMargin)));
        int dp = AndroidUtilities.dp(74.0f);
        float f2 = f - ((float) dp);
        if (((float) this.backgroundPaddingTop) + f2 < ((float) (ActionBar.getCurrentActionBarHeight() * 2))) {
            float min = Math.min(1.0f, ((((float) (ActionBar.getCurrentActionBarHeight() * 2)) - f2) - ((float) this.backgroundPaddingTop)) / ((float) (((dp - this.backgroundPaddingTop) - AndroidUtilities.dp(14.0f)) + ActionBar.getCurrentActionBarHeight())));
            i = (int) (((float) AndroidUtilities.dp(AndroidUtilities.isTablet() ? 17.0f : 13.0f)) * min);
            if (Math.abs(Math.min(1.0f, min) - this.colorProgress) > 1.0E-4f) {
                setColorProgress(Math.min(1.0f, min));
            }
            float f3 = 1.0f - ((0.1f * min) * 1.2f);
            this.titleTextView.setScaleX(Math.max(0.9f, f3));
            this.titleTextView.setScaleY(Math.max(0.9f, f3));
            this.titleTextView.setAlpha(Math.max(0.0f, 1.0f - (min * 1.2f)));
        } else {
            i = 0;
            this.titleTextView.setScaleX(1.0f);
            this.titleTextView.setScaleY(1.0f);
            this.titleTextView.setAlpha(1.0f);
            if (this.colorProgress > 1.0E-4f) {
                setColorProgress(0.0f);
            }
        }
        float f4 = (float) i;
        this.menuItemsContainer.setTranslationY(Math.max((float) AndroidUtilities.dp(4.0f), (f - ((float) AndroidUtilities.dp(53.0f))) - f4));
        this.titleTextView.setTranslationY(Math.max((float) AndroidUtilities.dp(4.0f), (f - ((float) AndroidUtilities.dp(44.0f))) - f4));
        LinearLayout linearLayout = this.scheduleTimerContainer;
        if (linearLayout != null) {
            linearLayout.setTranslationY(Math.max((float) AndroidUtilities.dp(4.0f), (f - ((float) AndroidUtilities.dp(44.0f))) - f4));
        }
        this.containerView.invalidate();
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

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:135:0x0255  */
    /* JADX WARNING: Removed duplicated region for block: B:136:0x025d  */
    /* JADX WARNING: Removed duplicated region for block: B:139:0x0266  */
    /* JADX WARNING: Removed duplicated region for block: B:153:0x02b1  */
    /* JADX WARNING: Removed duplicated region for block: B:155:0x02b6  */
    /* JADX WARNING: Removed duplicated region for block: B:156:0x02ca  */
    /* JADX WARNING: Removed duplicated region for block: B:159:0x02eb  */
    /* JADX WARNING: Removed duplicated region for block: B:162:0x02f1  */
    /* JADX WARNING: Removed duplicated region for block: B:163:0x02f5  */
    /* JADX WARNING: Removed duplicated region for block: B:166:0x02fd  */
    /* JADX WARNING: Removed duplicated region for block: B:182:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void updateState(boolean r28, boolean r29) {
        /*
            r27 = this;
            r0 = r27
            r10 = r28
            org.telegram.messenger.ChatObject$Call r1 = r0.call
            r2 = 6
            r3 = 5
            r11 = 0
            if (r1 == 0) goto L_0x0331
            boolean r1 = r1.isScheduled()
            if (r1 == 0) goto L_0x0013
            goto L_0x0331
        L_0x0013:
            org.telegram.messenger.voip.VoIPService r1 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            if (r1 != 0) goto L_0x001a
            return
        L_0x001a:
            boolean r4 = r1.isSwitchingStream()
            r6 = 0
            r8 = 2
            r12 = 1
            if (r4 != 0) goto L_0x004f
            long r13 = r0.creatingServiceTime
            int r4 = (r13 > r6 ? 1 : (r13 == r6 ? 0 : -1))
            if (r4 == 0) goto L_0x003b
            long r13 = android.os.SystemClock.elapsedRealtime()
            long r5 = r0.creatingServiceTime
            long r13 = r13 - r5
            long r5 = java.lang.Math.abs(r13)
            r13 = 3000(0xbb8, double:1.482E-320)
            int r7 = (r5 > r13 ? 1 : (r5 == r13 ? 0 : -1))
            if (r7 <= 0) goto L_0x004f
        L_0x003b:
            int r5 = r0.currentCallState
            if (r5 == r12) goto L_0x0045
            if (r5 == r8) goto L_0x0045
            if (r5 == r2) goto L_0x0045
            if (r5 != r3) goto L_0x004f
        L_0x0045:
            r27.cancelMutePress()
            r1 = 3
            r0.updateMuteButton(r1, r10)
            r3 = 4
            goto L_0x00c4
        L_0x004f:
            org.telegram.tgnet.TLObject r2 = r0.userSwitchObject
            if (r2 == 0) goto L_0x0063
            org.telegram.ui.Components.UndoView r2 = r27.getUndoView()
            r3 = 37
            org.telegram.tgnet.TLObject r5 = r0.userSwitchObject
            r6 = 0
            r2.showWithAction((long) r6, (int) r3, (java.lang.Object) r5)
            r2 = 0
            r0.userSwitchObject = r2
        L_0x0063:
            org.telegram.messenger.ChatObject$Call r2 = r0.call
            android.util.SparseArray<org.telegram.tgnet.TLRPC$TL_groupCallParticipant> r2 = r2.participants
            org.telegram.tgnet.TLRPC$Peer r3 = r0.selfPeer
            int r3 = org.telegram.messenger.MessageObject.getPeerId(r3)
            java.lang.Object r2 = r2.get(r3)
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r2 = (org.telegram.tgnet.TLRPC$TL_groupCallParticipant) r2
            boolean r3 = r1.micSwitching
            if (r3 != 0) goto L_0x00a1
            if (r2 == 0) goto L_0x00a1
            boolean r3 = r2.can_self_unmute
            if (r3 != 0) goto L_0x00a1
            boolean r3 = r2.muted
            if (r3 == 0) goto L_0x00a1
            org.telegram.tgnet.TLRPC$Chat r3 = r0.currentChat
            boolean r3 = org.telegram.messenger.ChatObject.canManageCalls(r3)
            if (r3 != 0) goto L_0x00a1
            r27.cancelMutePress()
            long r2 = r2.raise_hand_rating
            r5 = 0
            int r7 = (r2 > r5 ? 1 : (r2 == r5 ? 0 : -1))
            if (r7 == 0) goto L_0x0099
            r3 = 4
            r0.updateMuteButton(r3, r10)
            goto L_0x009d
        L_0x0099:
            r3 = 4
            r0.updateMuteButton(r8, r10)
        L_0x009d:
            r1.setMicMute(r12, r11, r11)
            goto L_0x00c4
        L_0x00a1:
            r3 = 4
            boolean r4 = r1.isMicMute()
            boolean r5 = r1.micSwitching
            if (r5 != 0) goto L_0x00bb
            if (r29 == 0) goto L_0x00bb
            if (r2 == 0) goto L_0x00bb
            boolean r2 = r2.muted
            if (r2 == 0) goto L_0x00bb
            if (r4 != 0) goto L_0x00bb
            r27.cancelMutePress()
            r1.setMicMute(r12, r11, r11)
            r4 = 1
        L_0x00bb:
            if (r4 == 0) goto L_0x00c1
            r0.updateMuteButton(r11, r10)
            goto L_0x00c4
        L_0x00c1:
            r0.updateMuteButton(r12, r10)
        L_0x00c4:
            org.telegram.messenger.voip.VoIPService r1 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            if (r1 == 0) goto L_0x00d6
            org.telegram.messenger.voip.VoIPService r1 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            int r1 = r1.getVideoState(r11)
            if (r1 != r8) goto L_0x00d6
            r13 = 1
            goto L_0x00d7
        L_0x00d6:
            r13 = 0
        L_0x00d7:
            org.telegram.messenger.ChatObject$Call r1 = r0.call
            android.util.SparseArray<org.telegram.tgnet.TLRPC$TL_groupCallParticipant> r1 = r1.participants
            org.telegram.tgnet.TLRPC$Peer r2 = r0.selfPeer
            int r2 = org.telegram.messenger.MessageObject.getPeerId(r2)
            java.lang.Object r1 = r1.get(r2)
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r1 = (org.telegram.tgnet.TLRPC$TL_groupCallParticipant) r1
            if (r1 == 0) goto L_0x00fb
            boolean r2 = r1.can_self_unmute
            if (r2 != 0) goto L_0x00fb
            boolean r1 = r1.muted
            if (r1 == 0) goto L_0x00fb
            org.telegram.tgnet.TLRPC$Chat r1 = r0.currentChat
            boolean r1 = org.telegram.messenger.ChatObject.canManageCalls(r1)
            if (r1 != 0) goto L_0x00fb
            r1 = 1
            goto L_0x00fc
        L_0x00fb:
            r1 = 0
        L_0x00fc:
            if (r1 != 0) goto L_0x0104
            org.telegram.messenger.ChatObject$Call r1 = r0.call
            boolean r1 = r1.canStreamVideo
            if (r1 != 0) goto L_0x0106
        L_0x0104:
            if (r13 == 0) goto L_0x0109
        L_0x0106:
            r1 = 1
            r14 = 0
            goto L_0x010b
        L_0x0109:
            r1 = 0
            r14 = 1
        L_0x010b:
            r15 = 1050253722(0x3e99999a, float:0.3)
            if (r13 == 0) goto L_0x0127
            if (r10 == 0) goto L_0x0124
            org.telegram.ui.Components.voip.VoIPToggleButton r2 = r0.flipButton
            int r2 = r2.getVisibility()
            if (r2 == 0) goto L_0x0124
            org.telegram.ui.Components.voip.VoIPToggleButton r2 = r0.flipButton
            r2.setScaleX(r15)
            org.telegram.ui.Components.voip.VoIPToggleButton r2 = r0.flipButton
            r2.setScaleY(r15)
        L_0x0124:
            r16 = 1
            goto L_0x0129
        L_0x0127:
            r16 = 0
        L_0x0129:
            if (r14 == 0) goto L_0x012d
            r2 = 2
            goto L_0x012e
        L_0x012d:
            r2 = 0
        L_0x012e:
            int r2 = r16 + r2
            if (r1 == 0) goto L_0x0134
            r5 = 4
            goto L_0x0135
        L_0x0134:
            r5 = 0
        L_0x0135:
            int r2 = r2 + r5
            org.telegram.ui.Components.voip.GroupCallRenderersContainer r3 = r0.renderersContainer
            r9 = 8
            if (r3 == 0) goto L_0x0143
            boolean r3 = r3.inFullscreenMode
            if (r3 == 0) goto L_0x0143
            r3 = 8
            goto L_0x0144
        L_0x0143:
            r3 = 0
        L_0x0144:
            int r2 = r2 + r3
            int r3 = r0.buttonsVisibility
            if (r3 == 0) goto L_0x0181
            if (r3 == r2) goto L_0x0181
            if (r10 == 0) goto L_0x0181
            r3 = 0
        L_0x014e:
            android.widget.FrameLayout r4 = r0.buttonsContainer
            int r4 = r4.getChildCount()
            if (r3 >= r4) goto L_0x017f
            android.widget.FrameLayout r4 = r0.buttonsContainer
            android.view.View r4 = r4.getChildAt(r3)
            int r5 = r4.getVisibility()
            if (r5 != 0) goto L_0x017c
            java.util.HashMap<android.view.View, java.lang.Float> r5 = r0.buttonsAnimationParamsX
            float r6 = r4.getX()
            java.lang.Float r6 = java.lang.Float.valueOf(r6)
            r5.put(r4, r6)
            java.util.HashMap<android.view.View, java.lang.Float> r5 = r0.buttonsAnimationParamsY
            float r6 = r4.getY()
            java.lang.Float r6 = java.lang.Float.valueOf(r6)
            r5.put(r4, r6)
        L_0x017c:
            int r3 = r3 + 1
            goto L_0x014e
        L_0x017f:
            r0.animateButtonsOnNextLayout = r12
        L_0x0181:
            int r3 = r0.buttonsVisibility
            r3 = r3 | r8
            r4 = r2 | 2
            if (r3 == r4) goto L_0x018b
            r17 = 1
            goto L_0x018d
        L_0x018b:
            r17 = 0
        L_0x018d:
            r0.buttonsVisibility = r2
            if (r1 == 0) goto L_0x01b3
            org.telegram.ui.Components.voip.VoIPToggleButton r1 = r0.cameraButton
            r2 = 2131165332(0x7var_, float:1.7944878E38)
            r3 = -1
            r4 = 0
            r5 = 1065353216(0x3var_, float:1.0)
            r6 = 1
            r7 = 2131628048(0x7f0e1010, float:1.8883378E38)
            java.lang.String r8 = "VoipCamera"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r8, r7)
            r8 = r13 ^ 1
            r15 = 8
            r9 = r28
            r1.setData(r2, r3, r4, r5, r6, r7, r8, r9)
            org.telegram.ui.Components.voip.VoIPToggleButton r1 = r0.cameraButton
            r1.setChecked(r12, r11)
            goto L_0x01ba
        L_0x01b3:
            r15 = 8
            org.telegram.ui.Components.voip.VoIPToggleButton r1 = r0.cameraButton
            r1.setVisibility(r15)
        L_0x01ba:
            if (r16 == 0) goto L_0x01e0
            org.telegram.ui.Components.voip.VoIPToggleButton r1 = r0.flipButton
            r19 = 0
            r20 = -1
            r21 = 0
            r22 = 1065353216(0x3var_, float:1.0)
            r23 = 1
            r2 = 2131628075(0x7f0e102b, float:1.8883432E38)
            java.lang.String r3 = "VoipFlip"
            java.lang.String r24 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r25 = 0
            r26 = 0
            r18 = r1
            r18.setData(r19, r20, r21, r22, r23, r24, r25, r26)
            org.telegram.ui.Components.voip.VoIPToggleButton r1 = r0.flipButton
            r1.setChecked(r12, r11)
            goto L_0x01e5
        L_0x01e0:
            org.telegram.ui.Components.voip.VoIPToggleButton r1 = r0.flipButton
            r1.setVisibility(r15)
        L_0x01e5:
            org.telegram.ui.Components.voip.VoIPToggleButton r1 = r0.soundButton
            if (r14 == 0) goto L_0x01eb
            r9 = 0
            goto L_0x01ed
        L_0x01eb:
            r9 = 8
        L_0x01ed:
            r1.setVisibility(r9)
            if (r17 == 0) goto L_0x01f7
            if (r14 == 0) goto L_0x01f7
            r0.updateSpeakerPhoneIcon(r11)
        L_0x01f7:
            r1 = 350(0x15e, double:1.73E-321)
            r3 = 1065353216(0x3var_, float:1.0)
            if (r17 == 0) goto L_0x024a
            if (r14 == 0) goto L_0x0202
            r4 = 1065353216(0x3var_, float:1.0)
            goto L_0x0205
        L_0x0202:
            r4 = 1050253722(0x3e99999a, float:0.3)
        L_0x0205:
            if (r10 != 0) goto L_0x021b
            org.telegram.ui.Components.voip.VoIPToggleButton r5 = r0.soundButton
            android.view.ViewPropertyAnimator r5 = r5.animate()
            r5.cancel()
            org.telegram.ui.Components.voip.VoIPToggleButton r5 = r0.soundButton
            r5.setScaleX(r4)
            org.telegram.ui.Components.voip.VoIPToggleButton r5 = r0.soundButton
            r5.setScaleY(r4)
            goto L_0x024a
        L_0x021b:
            if (r14 == 0) goto L_0x022b
            org.telegram.ui.Components.voip.VoIPToggleButton r5 = r0.soundButton
            r6 = 1050253722(0x3e99999a, float:0.3)
            r5.setScaleX(r6)
            org.telegram.ui.Components.voip.VoIPToggleButton r5 = r0.soundButton
            r5.setScaleY(r6)
            goto L_0x022e
        L_0x022b:
            r6 = 1050253722(0x3e99999a, float:0.3)
        L_0x022e:
            org.telegram.ui.Components.voip.VoIPToggleButton r5 = r0.soundButton
            android.view.ViewPropertyAnimator r5 = r5.animate()
            android.view.ViewPropertyAnimator r5 = r5.scaleX(r4)
            android.view.ViewPropertyAnimator r4 = r5.scaleY(r4)
            android.view.ViewPropertyAnimator r4 = r4.setDuration(r1)
            org.telegram.ui.Components.CubicBezierInterpolator r5 = org.telegram.ui.Components.CubicBezierInterpolator.DEFAULT
            android.view.ViewPropertyAnimator r4 = r4.setInterpolator(r5)
            r4.start()
            goto L_0x024d
        L_0x024a:
            r6 = 1050253722(0x3e99999a, float:0.3)
        L_0x024d:
            org.telegram.ui.Components.voip.VoIPToggleButton r4 = r0.cameraButton
            int r4 = r4.getVisibility()
            if (r4 != 0) goto L_0x025d
            org.telegram.ui.Components.voip.VoIPToggleButton r4 = r0.cameraButton
            r4.showText(r12, r10)
            r4 = 1065353216(0x3var_, float:1.0)
            goto L_0x0260
        L_0x025d:
            r4 = 1050253722(0x3e99999a, float:0.3)
        L_0x0260:
            float r5 = r0.cameraButtonScale
            int r5 = (r5 > r4 ? 1 : (r5 == r4 ? 0 : -1))
            if (r5 == 0) goto L_0x0299
            r0.cameraButtonScale = r4
            if (r10 != 0) goto L_0x027e
            org.telegram.ui.Components.voip.VoIPToggleButton r5 = r0.cameraButton
            android.view.ViewPropertyAnimator r5 = r5.animate()
            r5.cancel()
            org.telegram.ui.Components.voip.VoIPToggleButton r5 = r0.cameraButton
            r5.setScaleX(r4)
            org.telegram.ui.Components.voip.VoIPToggleButton r5 = r0.cameraButton
            r5.setScaleY(r4)
            goto L_0x0299
        L_0x027e:
            org.telegram.ui.Components.voip.VoIPToggleButton r5 = r0.cameraButton
            android.view.ViewPropertyAnimator r5 = r5.animate()
            android.view.ViewPropertyAnimator r5 = r5.scaleX(r4)
            android.view.ViewPropertyAnimator r4 = r5.scaleY(r4)
            android.view.ViewPropertyAnimator r4 = r4.setDuration(r1)
            org.telegram.ui.Components.CubicBezierInterpolator r5 = org.telegram.ui.Components.CubicBezierInterpolator.DEFAULT
            android.view.ViewPropertyAnimator r4 = r4.setInterpolator(r5)
            r4.start()
        L_0x0299:
            boolean r4 = isTabletMode
            r5 = 1061997773(0x3f4ccccd, float:0.8)
            if (r4 == 0) goto L_0x02a1
            goto L_0x02af
        L_0x02a1:
            boolean r4 = isLandscapeMode
            if (r4 != 0) goto L_0x02ad
            org.telegram.ui.Components.voip.GroupCallRenderersContainer r4 = r0.renderersContainer
            if (r4 == 0) goto L_0x02af
            boolean r4 = r4.inFullscreenMode
            if (r4 == 0) goto L_0x02af
        L_0x02ad:
            r5 = 1065353216(0x3var_, float:1.0)
        L_0x02af:
            if (r13 != 0) goto L_0x02b4
            r5 = 1050253722(0x3e99999a, float:0.3)
        L_0x02b4:
            if (r10 != 0) goto L_0x02ca
            org.telegram.ui.Components.voip.VoIPToggleButton r4 = r0.flipButton
            android.view.ViewPropertyAnimator r4 = r4.animate()
            r4.cancel()
            org.telegram.ui.Components.voip.VoIPToggleButton r4 = r0.flipButton
            r4.setScaleX(r5)
            org.telegram.ui.Components.voip.VoIPToggleButton r4 = r0.flipButton
            r4.setScaleY(r5)
            goto L_0x02e5
        L_0x02ca:
            org.telegram.ui.Components.voip.VoIPToggleButton r4 = r0.flipButton
            android.view.ViewPropertyAnimator r4 = r4.animate()
            android.view.ViewPropertyAnimator r4 = r4.scaleX(r5)
            android.view.ViewPropertyAnimator r4 = r4.scaleY(r5)
            android.view.ViewPropertyAnimator r4 = r4.setDuration(r1)
            org.telegram.ui.Components.CubicBezierInterpolator r7 = org.telegram.ui.Components.CubicBezierInterpolator.DEFAULT
            android.view.ViewPropertyAnimator r4 = r4.setInterpolator(r7)
            r4.start()
        L_0x02e5:
            org.telegram.ui.Components.voip.VoIPToggleButton r4 = r0.flipButton
            int r5 = (r5 > r3 ? 1 : (r5 == r3 ? 0 : -1))
            if (r5 != 0) goto L_0x02ec
            r11 = 1
        L_0x02ec:
            r4.showText(r11, r10)
            if (r13 == 0) goto L_0x02f5
            r15 = 1050253722(0x3e99999a, float:0.3)
            goto L_0x02f7
        L_0x02f5:
            r15 = 1065353216(0x3var_, float:1.0)
        L_0x02f7:
            float r3 = r0.soundButtonScale
            int r3 = (r3 > r15 ? 1 : (r3 == r15 ? 0 : -1))
            if (r3 == 0) goto L_0x0330
            r0.soundButtonScale = r15
            if (r10 != 0) goto L_0x0315
            org.telegram.ui.Components.voip.VoIPToggleButton r1 = r0.soundButton
            android.view.ViewPropertyAnimator r1 = r1.animate()
            r1.cancel()
            org.telegram.ui.Components.voip.VoIPToggleButton r1 = r0.soundButton
            r1.setScaleX(r15)
            org.telegram.ui.Components.voip.VoIPToggleButton r1 = r0.soundButton
            r1.setScaleY(r15)
            goto L_0x0330
        L_0x0315:
            org.telegram.ui.Components.voip.VoIPToggleButton r3 = r0.soundButton
            android.view.ViewPropertyAnimator r3 = r3.animate()
            android.view.ViewPropertyAnimator r3 = r3.scaleX(r15)
            android.view.ViewPropertyAnimator r3 = r3.scaleY(r15)
            android.view.ViewPropertyAnimator r1 = r3.setDuration(r1)
            org.telegram.ui.Components.CubicBezierInterpolator r2 = org.telegram.ui.Components.CubicBezierInterpolator.DEFAULT
            android.view.ViewPropertyAnimator r1 = r1.setInterpolator(r2)
            r1.start()
        L_0x0330:
            return
        L_0x0331:
            org.telegram.tgnet.TLRPC$Chat r1 = r0.currentChat
            boolean r1 = org.telegram.messenger.ChatObject.canManageCalls(r1)
            if (r1 == 0) goto L_0x033a
            goto L_0x0344
        L_0x033a:
            org.telegram.messenger.ChatObject$Call r1 = r0.call
            org.telegram.tgnet.TLRPC$GroupCall r1 = r1.call
            boolean r1 = r1.schedule_start_subscribed
            if (r1 == 0) goto L_0x0343
            r2 = 7
        L_0x0343:
            r3 = r2
        L_0x0344:
            r0.updateMuteButton(r3, r10)
            org.telegram.ui.Components.voip.VoIPToggleButton r12 = r0.leaveButton
            r13 = 2131165319(0x7var_, float:1.7944852E38)
            r14 = -1
            java.lang.String r1 = "voipgroup_leaveButton"
            int r15 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r16 = 1050253722(0x3e99999a, float:0.3)
            r17 = 0
            r1 = 2131624928(0x7f0e03e0, float:1.887705E38)
            java.lang.String r2 = "Close"
            java.lang.String r18 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r19 = 0
            r20 = 0
            r12.setData(r13, r14, r15, r16, r17, r18, r19, r20)
            r0.updateScheduleUI(r11)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.GroupCallActivity.updateState(boolean, boolean):void");
    }

    public void onAudioSettingsChanged() {
        updateSpeakerPhoneIcon(true);
        if (VoIPService.getSharedInstance() == null || VoIPService.getSharedInstance().isMicMute()) {
            setMicAmplitude(0.0f);
        }
        if (this.listView.getVisibility() == 0) {
            AndroidUtilities.updateVisibleRows(this.listView);
        }
        if (this.fullscreenUsersListView.getVisibility() == 0) {
            AndroidUtilities.updateVisibleRows(this.fullscreenUsersListView);
        }
        this.attachedRenderersTmp.clear();
        this.attachedRenderersTmp.addAll(this.attachedRenderers);
        for (int i = 0; i < this.attachedRenderersTmp.size(); i++) {
            this.attachedRenderersTmp.get(i).updateAttachState(true);
        }
    }

    private void updateSpeakerPhoneIcon(boolean z) {
        VoIPToggleButton voIPToggleButton = this.soundButton;
        if (voIPToggleButton != null && voIPToggleButton.getVisibility() == 0) {
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
    /* JADX WARNING: Removed duplicated region for block: B:145:0x02ff  */
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
            r5 = 2131628088(0x7f0e1038, float:1.8883459E38)
            java.lang.String r12 = "VoipGroupCancelReminder"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r12, r5)
            org.telegram.ui.Components.RLottieDrawable r12 = r0.bigMicDrawable
            boolean r12 = r12.setCustomEndFrame(r9)
        L_0x003b:
            r8 = 0
            goto L_0x01b1
        L_0x003e:
            if (r1 != r3) goto L_0x0050
            r5 = 2131628146(0x7f0e1072, float:1.8883576E38)
            java.lang.String r12 = "VoipGroupSetReminder"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r12, r5)
            org.telegram.ui.Components.RLottieDrawable r12 = r0.bigMicDrawable
            boolean r12 = r12.setCustomEndFrame(r8)
            goto L_0x003b
        L_0x0050:
            if (r1 != r10) goto L_0x0064
            r5 = 2131628155(0x7f0e107b, float:1.8883595E38)
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
            r12 = 2131628164(0x7f0e1084, float:1.8883613E38)
            java.lang.String r14 = "VoipGroupUnmute"
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r14, r12)
            r14 = 2131628178(0x7f0e1092, float:1.8883641E38)
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
            r5 = 2131628228(0x7f0e10c4, float:1.8883743E38)
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
            r5 = 2131628189(0x7f0e109d, float:1.8883664E38)
            java.lang.String r8 = "VoipMutedTapedForSpeak"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r8, r5)
            r8 = 2131628190(0x7f0e109e, float:1.8883666E38)
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
            r9 = 2131624981(0x7f0e0415, float:1.8877157E38)
            java.lang.String r12 = "Connecting"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r12, r9)
            goto L_0x01af
        L_0x019d:
            r9 = 2131628185(0x7f0e1099, float:1.8883656E38)
            java.lang.String r12 = "VoipMutedByAdmin"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r12, r9)
            r12 = 2131628188(0x7f0e109c, float:1.8883662E38)
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
            if (r2 == 0) goto L_0x02ff
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
            r4 = 1084227584(0x40a00000, float:5.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r4 = -r4
            float r4 = (float) r4
            r3.setTranslationY(r4)
            android.widget.TextView[] r3 = r0.muteLabel
            r3 = r3[r6]
            r3.setText(r5)
            r3 = 2
            float[] r3 = new float[r3]
            r3 = {0, NUM} // fill-array
            android.animation.ValueAnimator r3 = android.animation.ValueAnimator.ofFloat(r3)
            r0.muteButtonAnimator = r3
            org.telegram.ui.-$$Lambda$GroupCallActivity$MZhM-1T1Yhy43TQWbTCQlaph1qY r4 = new org.telegram.ui.-$$Lambda$GroupCallActivity$MZhM-1T1Yhy43TQWbTCQlaph1qY
            r4.<init>()
            r3.addUpdateListener(r4)
            android.animation.ValueAnimator r3 = r0.muteButtonAnimator
            org.telegram.ui.GroupCallActivity$49 r4 = new org.telegram.ui.GroupCallActivity$49
            r4.<init>()
            r3.addListener(r4)
            android.animation.ValueAnimator r3 = r0.muteButtonAnimator
            r4 = 180(0xb4, double:8.9E-322)
            r3.setDuration(r4)
            android.animation.ValueAnimator r3 = r0.muteButtonAnimator
            r3.start()
            r0.muteButtonState = r1
            goto L_0x0314
        L_0x02ff:
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
        L_0x0314:
            r0.updateMuteButtonState(r2)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.GroupCallActivity.updateMuteButton(int, boolean):void");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$updateMuteButton$52 */
    public /* synthetic */ void lambda$updateMuteButton$52$GroupCallActivity(ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.muteLabel[0].setAlpha(1.0f - floatValue);
        this.muteLabel[0].setTranslationY(((float) AndroidUtilities.dp(5.0f)) * floatValue);
        this.muteLabel[1].setAlpha(floatValue);
        this.muteLabel[1].setTranslationY((float) AndroidUtilities.dp((floatValue * 5.0f) - 0.875f));
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
                this.states[i2].shader = null;
            } else if (isGradientState(i2)) {
                this.states[this.muteButtonState].shader = new LinearGradient(0.0f, 400.0f, 400.0f, 0.0f, new int[]{Theme.getColor("voipgroup_mutedByAdminGradient"), Theme.getColor("voipgroup_mutedByAdminGradient3"), Theme.getColor("voipgroup_mutedByAdminGradient2")}, (float[]) null, Shader.TileMode.CLAMP);
            } else {
                int i3 = this.muteButtonState;
                if (i3 == 1) {
                    this.states[i3].shader = new RadialGradient(200.0f, 200.0f, 200.0f, new int[]{Theme.getColor("voipgroup_muteButton"), Theme.getColor("voipgroup_muteButton3")}, (float[]) null, Shader.TileMode.CLAMP);
                } else {
                    this.states[i3].shader = new RadialGradient(200.0f, 200.0f, 200.0f, new int[]{Theme.getColor("voipgroup_unmuteButton2"), Theme.getColor("voipgroup_unmuteButton")}, (float[]) null, Shader.TileMode.CLAMP);
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
                int i5 = weavingState3.currentState;
                boolean z4 = i5 == 1 || i5 == 0;
                if (i5 == 3) {
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
        if (VoIPService.getSharedInstance() != null) {
            VoIPService.getSharedInstance().hangUp(z ? 1 : 0);
        }
        if (call2 != null) {
            TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant = call2.participants.get(i);
            if (tLRPC$TL_groupCallParticipant != null) {
                call2.participants.delete(i);
                call2.sortedParticipants.remove(tLRPC$TL_groupCallParticipant);
                call2.visibleParticipants.remove(tLRPC$TL_groupCallParticipant);
                int i2 = 0;
                while (i2 < call2.visibleVideoParticipants.size()) {
                    if (MessageObject.getPeerId(call2.visibleVideoParticipants.get(i2).participant.peer) == MessageObject.getPeerId(tLRPC$TL_groupCallParticipant.peer)) {
                        call2.visibleVideoParticipants.remove(i2);
                        i2--;
                    }
                    i2++;
                }
                TLRPC$GroupCall tLRPC$GroupCall = call2.call;
                tLRPC$GroupCall.participants_count--;
            }
            for (int i3 = 0; i3 < call2.sortedParticipants.size(); i3++) {
                TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant2 = call2.sortedParticipants.get(i3);
                tLRPC$TL_groupCallParticipant2.lastActiveDate = tLRPC$TL_groupCallParticipant2.lastSpeakTime;
            }
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
                    GroupCallActivity.lambda$onLeaveClick$53(this.f$0, view);
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

    static /* synthetic */ void lambda$onLeaveClick$53(CheckBoxCell[] checkBoxCellArr, View view) {
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
                TLObject tLObject3 = tLObject2;
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
                    avatarDrawable.setInfo(tLObject3);
                    boolean z2 = tLObject3 instanceof TLRPC$User;
                    if (z2) {
                        TLRPC$User tLRPC$User = (TLRPC$User) tLObject3;
                        backupImageView.setForUserOrChat(tLRPC$User, avatarDrawable);
                        str = UserObject.getFirstName(tLRPC$User);
                    } else {
                        TLRPC$Chat tLRPC$Chat = (TLRPC$Chat) tLObject3;
                        backupImageView.setForUserOrChat(tLRPC$Chat, avatarDrawable);
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
                        builder.setPositiveButton(LocaleController.getString("VoipGroupUserRemove", NUM), new DialogInterface.OnClickListener(tLObject3) {
                            public final /* synthetic */ TLObject f$1;

                            {
                                this.f$1 = r2;
                            }

                            public final void onClick(DialogInterface dialogInterface, int i) {
                                GroupCallActivity.this.lambda$processSelectedOption$55$GroupCallActivity(this.f$1, dialogInterface, i);
                            }
                        });
                    } else if (z2) {
                        builder.setPositiveButton(LocaleController.getString("VoipGroupAdd", NUM), new DialogInterface.OnClickListener((TLRPC$User) tLObject3, i3) {
                            public final /* synthetic */ TLRPC$User f$1;
                            public final /* synthetic */ int f$2;

                            {
                                this.f$1 = r2;
                                this.f$2 = r3;
                            }

                            public final void onClick(DialogInterface dialogInterface, int i) {
                                GroupCallActivity.this.lambda$processSelectedOption$57$GroupCallActivity(this.f$1, this.f$2, dialogInterface, i);
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
                    VoIPService.getSharedInstance().editCallMember(tLObject3, Boolean.TRUE, (Boolean) null, (Integer) null, (Boolean) null, (Runnable) null);
                    getUndoView().showWithAction(0, 30, tLObject3, (Object) null, (Runnable) null, (Runnable) null);
                }
            } else if (i4 == 6) {
                Bundle bundle = new Bundle();
                if (i3 > 0) {
                    bundle.putInt("user_id", i3);
                } else {
                    bundle.putInt("chat_id", -i3);
                }
                this.parentActivity.lambda$runLinkRequest$42(new ChatActivity(bundle));
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
                    this.parentActivity.lambda$runLinkRequest$42(new ChatActivity(bundle2));
                    dismiss();
                    return;
                }
                dismiss();
            } else if (i4 == 7) {
                sharedInstance.editCallMember(tLObject2, Boolean.TRUE, (Boolean) null, (Integer) null, Boolean.FALSE, (Runnable) null);
                updateMuteButton(2, true);
            } else if (i4 == 9) {
                ImageUpdater imageUpdater = this.currentAvatarUpdater;
                if (imageUpdater == null || !imageUpdater.isUploadingImage()) {
                    ImageUpdater imageUpdater2 = new ImageUpdater(true);
                    this.currentAvatarUpdater = imageUpdater2;
                    imageUpdater2.setOpenWithFrontfaceCamera(true);
                    this.currentAvatarUpdater.setForceDarkTheme(true);
                    this.currentAvatarUpdater.setSearchAvailable(true, true);
                    this.currentAvatarUpdater.setShowingFromDialog(true);
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
                            GroupCallActivity.this.lambda$processSelectedOption$58$GroupCallActivity();
                        }
                    }, $$Lambda$GroupCallActivity$eILgOaHrENJQR6K0XFYurp9LvbI.INSTANCE);
                }
            } else if (i4 == 10) {
                AlertsCreator.createChangeBioAlert(tLRPC$TL_groupCallParticipant2.about, i3, getContext(), this.currentAccount);
            } else if (i4 == 11) {
                AlertsCreator.createChangeNameAlert(i3, getContext(), this.currentAccount);
            } else if (i4 == 5) {
                sharedInstance.editCallMember(tLObject2, Boolean.TRUE, (Boolean) null, (Integer) null, (Boolean) null, (Runnable) null);
                getUndoView().showWithAction(0, 35, (Object) tLObject2);
                sharedInstance.setParticipantVolume(tLRPC$TL_groupCallParticipant2.source, 0);
            } else {
                if ((tLRPC$TL_groupCallParticipant2.flags & 128) == 0 || tLRPC$TL_groupCallParticipant2.volume != 0) {
                    sharedInstance.editCallMember(tLObject2, Boolean.FALSE, (Boolean) null, (Integer) null, (Boolean) null, (Runnable) null);
                } else {
                    tLRPC$TL_groupCallParticipant2.volume = 10000;
                    tLRPC$TL_groupCallParticipant2.volume_by_admin = false;
                    sharedInstance.editCallMember(tLObject2, Boolean.FALSE, (Boolean) null, 10000, (Boolean) null, (Runnable) null);
                }
                sharedInstance.setParticipantVolume(tLRPC$TL_groupCallParticipant2.source, ChatObject.getParticipantVolume(tLRPC$TL_groupCallParticipant));
                getUndoView().showWithAction(0, i4 == 1 ? 31 : 36, tLObject2, (Object) null, (Runnable) null, (Runnable) null);
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$processSelectedOption$55 */
    public /* synthetic */ void lambda$processSelectedOption$55$GroupCallActivity(TLObject tLObject, DialogInterface dialogInterface, int i) {
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
    /* renamed from: lambda$processSelectedOption$57 */
    public /* synthetic */ void lambda$processSelectedOption$57$GroupCallActivity(TLRPC$User tLRPC$User, int i, DialogInterface dialogInterface, int i2) {
        this.accountInstance.getMessagesController().addUserToChat(this.currentChat.id, tLRPC$User, 0, (String) null, this.parentActivity.getActionBarLayout().fragmentsStack.get(this.parentActivity.getActionBarLayout().fragmentsStack.size() - 1), new Runnable(i) {
            public final /* synthetic */ int f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                GroupCallActivity.this.lambda$processSelectedOption$56$GroupCallActivity(this.f$1);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$processSelectedOption$56 */
    public /* synthetic */ void lambda$processSelectedOption$56$GroupCallActivity(int i) {
        inviteUserToCall(i, false);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$processSelectedOption$58 */
    public /* synthetic */ void lambda$processSelectedOption$58$GroupCallActivity() {
        this.accountInstance.getMessagesController().deleteUserPhoto((TLRPC$InputPhoto) null);
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:71:0x0255, code lost:
        if (r1.admin_rights.manage_call != false) goto L_0x0257;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:85:0x0294, code lost:
        if ((r9 instanceof org.telegram.tgnet.TLRPC$TL_chatParticipantCreator) == false) goto L_0x0259;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:88:0x02a4, code lost:
        if (r3 == (-r7.currentChat.id)) goto L_0x0257;
     */
    /* JADX WARNING: Removed duplicated region for block: B:119:0x037c  */
    /* JADX WARNING: Removed duplicated region for block: B:157:0x04e8  */
    /* JADX WARNING: Removed duplicated region for block: B:172:0x058b  */
    /* JADX WARNING: Removed duplicated region for block: B:175:0x0592  */
    /* JADX WARNING: Removed duplicated region for block: B:176:0x05ae  */
    /* JADX WARNING: Removed duplicated region for block: B:181:0x05d3  */
    /* JADX WARNING: Removed duplicated region for block: B:182:0x05d5  */
    /* JADX WARNING: Removed duplicated region for block: B:184:0x05d8 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:187:0x05de  */
    /* JADX WARNING: Removed duplicated region for block: B:202:0x0639  */
    /* JADX WARNING: Removed duplicated region for block: B:208:0x0681  */
    /* JADX WARNING: Removed duplicated region for block: B:91:0x02ad  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean showMenuForCell(android.view.View r28) {
        /*
            r27 = this;
            r7 = r27
            r0 = r28
            org.telegram.ui.GroupCallActivity$GroupCallItemAnimator r1 = r7.itemAnimator
            boolean r1 = r1.isRunning()
            r8 = 0
            if (r1 == 0) goto L_0x000e
            return r8
        L_0x000e:
            boolean r1 = r7.avatarPriviewTransitionInProgress
            r9 = 1
            if (r1 != 0) goto L_0x07dc
            boolean r1 = r7.avatarsPreviewShowed
            if (r1 == 0) goto L_0x0019
            goto L_0x07dc
        L_0x0019:
            org.telegram.ui.ActionBar.ActionBarPopupWindow r1 = r7.scrimPopupWindow
            r2 = 0
            if (r1 == 0) goto L_0x0024
            r1.dismiss()
            r7.scrimPopupWindow = r2
            return r8
        L_0x0024:
            r27.clearScrimView()
            boolean r1 = r0 instanceof org.telegram.ui.Components.voip.GroupCallGridCell
            if (r1 == 0) goto L_0x0079
            org.telegram.ui.Components.voip.GroupCallGridCell r0 = (org.telegram.ui.Components.voip.GroupCallGridCell) r0
            org.telegram.ui.Cells.GroupCallUserCell r1 = new org.telegram.ui.Cells.GroupCallUserCell
            android.content.Context r3 = r0.getContext()
            r1.<init>(r3)
            org.telegram.tgnet.TLRPC$Peer r3 = r7.selfPeer
            int r14 = org.telegram.messenger.MessageObject.getPeerId(r3)
            org.telegram.messenger.AccountInstance r11 = r7.accountInstance
            org.telegram.messenger.ChatObject$VideoParticipant r3 = r0.getParticipant()
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r12 = r3.participant
            org.telegram.messenger.ChatObject$Call r13 = r7.call
            r15 = 0
            r16 = 0
            r10 = r1
            r10.setData(r11, r12, r13, r14, r15, r16)
            r7.hasScrimAnchorView = r8
            r7.scrimGridView = r0
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r0 = r0.getRenderer()
            r7.scrimRenderer = r0
            boolean r0 = isTabletMode
            if (r0 != 0) goto L_0x0076
            boolean r0 = isLandscapeMode
            if (r0 != 0) goto L_0x0076
            r7.scrimViewAttached = r9
            android.view.ViewGroup r0 = r7.containerView
            r10 = -1
            r11 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r12 = 0
            r13 = 1096810496(0x41600000, float:14.0)
            r14 = 0
            r15 = 1096810496(0x41600000, float:14.0)
            r16 = 0
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r10, r11, r12, r13, r14, r15, r16)
            r0.addView(r1, r3)
            goto L_0x00cd
        L_0x0076:
            r7.scrimViewAttached = r8
            goto L_0x00cd
        L_0x0079:
            boolean r1 = r0 instanceof org.telegram.ui.Components.GroupCallFullscreenAdapter.GroupCallUserCell
            if (r1 == 0) goto L_0x00c6
            org.telegram.ui.Components.GroupCallFullscreenAdapter$GroupCallUserCell r0 = (org.telegram.ui.Components.GroupCallFullscreenAdapter.GroupCallUserCell) r0
            org.telegram.ui.Cells.GroupCallUserCell r1 = new org.telegram.ui.Cells.GroupCallUserCell
            android.content.Context r3 = r0.getContext()
            r1.<init>(r3)
            org.telegram.tgnet.TLRPC$Peer r3 = r7.selfPeer
            int r14 = org.telegram.messenger.MessageObject.getPeerId(r3)
            org.telegram.messenger.AccountInstance r11 = r7.accountInstance
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r12 = r0.getParticipant()
            org.telegram.messenger.ChatObject$Call r13 = r7.call
            r15 = 0
            r16 = 0
            r10 = r1
            r10.setData(r11, r12, r13, r14, r15, r16)
            r7.hasScrimAnchorView = r8
            r7.scrimFullscreenView = r0
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r0 = r0.getRenderer()
            r7.scrimRenderer = r0
            if (r0 == 0) goto L_0x00af
            boolean r0 = r0.showingInFullscreen
            if (r0 == 0) goto L_0x00af
            r7.scrimRenderer = r2
        L_0x00af:
            android.view.ViewGroup r0 = r7.containerView
            r10 = -1
            r11 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r12 = 0
            r13 = 1096810496(0x41600000, float:14.0)
            r14 = 0
            r15 = 1096810496(0x41600000, float:14.0)
            r16 = 0
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r10, r11, r12, r13, r14, r15, r16)
            r0.addView(r1, r3)
            r7.scrimViewAttached = r9
            goto L_0x00cd
        L_0x00c6:
            r1 = r0
            org.telegram.ui.Cells.GroupCallUserCell r1 = (org.telegram.ui.Cells.GroupCallUserCell) r1
            r7.hasScrimAnchorView = r9
            r7.scrimViewAttached = r9
        L_0x00cd:
            r10 = r1
            if (r10 != 0) goto L_0x00d1
            return r8
        L_0x00d1:
            boolean r0 = isLandscapeMode
            if (r0 != 0) goto L_0x00df
            boolean r0 = isTabletMode
            if (r0 != 0) goto L_0x00df
            boolean r0 = org.telegram.messenger.AndroidUtilities.isInMultiwindow
            if (r0 != 0) goto L_0x00df
            r11 = 1
            goto L_0x00e0
        L_0x00df:
            r11 = 0
        L_0x00e0:
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r12 = r10.getParticipant()
            android.graphics.Rect r0 = new android.graphics.Rect
            r0.<init>()
            org.telegram.ui.ActionBar.ActionBarPopupWindow$ActionBarPopupWindowLayout r13 = new org.telegram.ui.ActionBar.ActionBarPopupWindow$ActionBarPopupWindowLayout
            android.content.Context r1 = r27.getContext()
            r13.<init>(r1)
            r13.setBackgroundDrawable(r2)
            r13.setPadding(r8, r8, r8, r8)
            org.telegram.ui.GroupCallActivity$50 r1 = new org.telegram.ui.GroupCallActivity$50
            r1.<init>(r0)
            r13.setOnTouchListener(r1)
            org.telegram.ui.-$$Lambda$GroupCallActivity$kzA-i4booN8IDH1ACwKmNiTQqxo r0 = new org.telegram.ui.-$$Lambda$GroupCallActivity$kzA-i4booN8IDH1ACwKmNiTQqxo
            r0.<init>()
            r13.setDispatchKeyEventListener(r0)
            android.widget.LinearLayout r14 = new android.widget.LinearLayout
            android.content.Context r0 = r27.getContext()
            r14.<init>(r0)
            boolean r0 = r12.muted_by_you
            if (r0 != 0) goto L_0x011f
            android.widget.LinearLayout r0 = new android.widget.LinearLayout
            android.content.Context r1 = r27.getContext()
            r0.<init>(r1)
            goto L_0x0120
        L_0x011f:
            r0 = r2
        L_0x0120:
            r7.currentOptionsLayout = r14
            org.telegram.ui.GroupCallActivity$51 r15 = new org.telegram.ui.GroupCallActivity$51
            android.content.Context r1 = r27.getContext()
            r15.<init>(r1, r14, r0)
            r1 = 1131413504(0x43700000, float:240.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r15.setMinimumWidth(r3)
            r15.setOrientation(r9)
            java.lang.String r3 = "voipgroup_listViewBackgroundUnscrolled"
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            java.lang.String r4 = "voipgroup_listViewBackground"
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r4)
            float r5 = r7.colorProgress
            r6 = 1065353216(0x3var_, float:1.0)
            int r3 = org.telegram.messenger.AndroidUtilities.getOffsetColor(r3, r4, r5, r6)
            r4 = 2131165956(0x7var_, float:1.7946144E38)
            if (r0 == 0) goto L_0x01a1
            boolean r5 = r10.isSelfUser()
            if (r5 != 0) goto L_0x01a1
            boolean r5 = r12.muted_by_you
            if (r5 != 0) goto L_0x01a1
            boolean r5 = r12.muted
            if (r5 == 0) goto L_0x0162
            boolean r5 = r12.can_self_unmute
            if (r5 == 0) goto L_0x01a1
        L_0x0162:
            android.content.Context r2 = r27.getContext()
            android.content.res.Resources r2 = r2.getResources()
            android.graphics.drawable.Drawable r2 = r2.getDrawable(r4)
            android.graphics.drawable.Drawable r2 = r2.mutate()
            android.graphics.PorterDuffColorFilter r5 = new android.graphics.PorterDuffColorFilter
            android.graphics.PorterDuff$Mode r6 = android.graphics.PorterDuff.Mode.MULTIPLY
            r5.<init>(r3, r6)
            r2.setColorFilter(r5)
            r0.setBackgroundDrawable(r2)
            r16 = -2
            r17 = -2
            r18 = 0
            r19 = 0
            r20 = 0
            r21 = 0
            android.widget.LinearLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createLinear(r16, r17, r18, r19, r20, r21)
            r15.addView(r0, r2)
            org.telegram.ui.GroupCallActivity$VolumeSlider r2 = new org.telegram.ui.GroupCallActivity$VolumeSlider
            android.content.Context r5 = r27.getContext()
            r2.<init>(r7, r5, r12)
            r5 = -1
            r6 = 48
            r0.addView(r2, r5, r6)
        L_0x01a1:
            r16 = r2
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r14.setMinimumWidth(r0)
            r14.setOrientation(r9)
            android.content.Context r0 = r27.getContext()
            android.content.res.Resources r0 = r0.getResources()
            android.graphics.drawable.Drawable r0 = r0.getDrawable(r4)
            android.graphics.drawable.Drawable r0 = r0.mutate()
            android.graphics.PorterDuffColorFilter r1 = new android.graphics.PorterDuffColorFilter
            android.graphics.PorterDuff$Mode r2 = android.graphics.PorterDuff.Mode.MULTIPLY
            r1.<init>(r3, r2)
            r0.setColorFilter(r1)
            r14.setBackgroundDrawable(r0)
            r17 = -2
            r18 = -2
            r19 = 0
            r6 = 0
            if (r16 == 0) goto L_0x01d8
            r0 = -1056964608(0xffffffffCLASSNAME, float:-8.0)
            r20 = -1056964608(0xffffffffCLASSNAME, float:-8.0)
            goto L_0x01da
        L_0x01d8:
            r20 = 0
        L_0x01da:
            r21 = 0
            r22 = 0
            android.widget.LinearLayout$LayoutParams r0 = org.telegram.ui.Components.LayoutHelper.createLinear(r17, r18, r19, r20, r21, r22)
            r15.addView(r14, r0)
            int r0 = android.os.Build.VERSION.SDK_INT
            r1 = 21
            if (r0 < r1) goto L_0x01ff
            org.telegram.ui.GroupCallActivity$52 r17 = new org.telegram.ui.GroupCallActivity$52
            android.content.Context r2 = r27.getContext()
            r3 = 0
            r4 = 0
            r5 = 2131689504(0x7f0var_, float:1.9008025E38)
            r0 = r17
            r1 = r27
            r6 = r15
            r0.<init>(r2, r3, r4, r5, r6)
            goto L_0x0208
        L_0x01ff:
            android.widget.ScrollView r0 = new android.widget.ScrollView
            android.content.Context r1 = r27.getContext()
            r0.<init>(r1)
        L_0x0208:
            r0.setClipToPadding(r8)
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
            if (r1 == 0) goto L_0x029f
            org.telegram.tgnet.TLRPC$Chat r1 = r7.currentChat
            boolean r1 = org.telegram.messenger.ChatObject.isChannel(r1)
            if (r1 == 0) goto L_0x025b
            org.telegram.messenger.AccountInstance r1 = r7.accountInstance
            org.telegram.messenger.MessagesController r1 = r1.getMessagesController()
            org.telegram.tgnet.TLRPC$Peer r5 = r12.peer
            int r5 = r5.user_id
            org.telegram.tgnet.TLRPC$Chat r8 = r7.currentChat
            int r8 = r8.id
            org.telegram.tgnet.TLRPC$ChannelParticipant r1 = r1.getAdminInChannel(r5, r8)
            if (r1 == 0) goto L_0x0259
            boolean r5 = r1 instanceof org.telegram.tgnet.TLRPC$TL_channelParticipantCreator
            if (r5 != 0) goto L_0x0257
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r1 = r1.admin_rights
            boolean r1 = r1.manage_call
            if (r1 == 0) goto L_0x0259
        L_0x0257:
            r1 = 1
            goto L_0x02a7
        L_0x0259:
            r1 = 0
            goto L_0x02a7
        L_0x025b:
            org.telegram.messenger.AccountInstance r1 = r7.accountInstance
            org.telegram.messenger.MessagesController r1 = r1.getMessagesController()
            org.telegram.tgnet.TLRPC$Chat r5 = r7.currentChat
            int r5 = r5.id
            org.telegram.tgnet.TLRPC$ChatFull r1 = r1.getChatFull(r5)
            if (r1 == 0) goto L_0x0259
            org.telegram.tgnet.TLRPC$ChatParticipants r5 = r1.participants
            if (r5 == 0) goto L_0x0259
            java.util.ArrayList<org.telegram.tgnet.TLRPC$ChatParticipant> r5 = r5.participants
            int r5 = r5.size()
            r8 = 0
        L_0x0276:
            if (r8 >= r5) goto L_0x0259
            org.telegram.tgnet.TLRPC$ChatParticipants r9 = r1.participants
            java.util.ArrayList<org.telegram.tgnet.TLRPC$ChatParticipant> r9 = r9.participants
            java.lang.Object r9 = r9.get(r8)
            org.telegram.tgnet.TLRPC$ChatParticipant r9 = (org.telegram.tgnet.TLRPC$ChatParticipant) r9
            r21 = r1
            int r1 = r9.user_id
            r22 = r5
            org.telegram.tgnet.TLRPC$Peer r5 = r12.peer
            int r5 = r5.user_id
            if (r1 != r5) goto L_0x0297
            boolean r1 = r9 instanceof org.telegram.tgnet.TLRPC$TL_chatParticipantAdmin
            if (r1 != 0) goto L_0x0257
            boolean r1 = r9 instanceof org.telegram.tgnet.TLRPC$TL_chatParticipantCreator
            if (r1 == 0) goto L_0x0259
            goto L_0x0257
        L_0x0297:
            int r8 = r8 + 1
            r1 = r21
            r5 = r22
            r9 = 1
            goto L_0x0276
        L_0x029f:
            org.telegram.tgnet.TLRPC$Chat r1 = r7.currentChat
            int r1 = r1.id
            int r1 = -r1
            if (r3 != r1) goto L_0x0259
            goto L_0x0257
        L_0x02a7:
            boolean r5 = r10.isSelfUser()
            if (r5 == 0) goto L_0x037c
            boolean r1 = r10.isHandRaised()
            if (r1 == 0) goto L_0x02d1
            r1 = 2131628087(0x7f0e1037, float:1.8883457E38)
            java.lang.String r5 = "VoipGroupCancelRaiseHand"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r5, r1)
            r4.add(r1)
            r1 = 2131165747(0x7var_, float:1.794572E38)
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            r6.add(r1)
            r1 = 7
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            r2.add(r1)
        L_0x02d1:
            boolean r1 = r10.hasAvatarSet()
            if (r1 == 0) goto L_0x02dd
            r1 = 2131628038(0x7f0e1006, float:1.8883357E38)
            java.lang.String r5 = "VoipAddPhoto"
            goto L_0x02e2
        L_0x02dd:
            r1 = 2131628220(0x7f0e10bc, float:1.8883727E38)
            java.lang.String r5 = "VoipSetNewPhoto"
        L_0x02e2:
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r5, r1)
            r4.add(r1)
            r1 = 2131165706(0x7var_a, float:1.7945637E38)
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            r6.add(r1)
            r1 = 9
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            r2.add(r1)
            if (r3 <= 0) goto L_0x0319
            java.lang.String r1 = r12.about
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 == 0) goto L_0x030c
            r1 = 2131628036(0x7f0e1004, float:1.8883353E38)
            java.lang.String r5 = "VoipAddBio"
            goto L_0x0311
        L_0x030c:
            r1 = 2131628065(0x7f0e1021, float:1.8883412E38)
            java.lang.String r5 = "VoipEditBio"
        L_0x0311:
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r5, r1)
            r4.add(r1)
            goto L_0x0333
        L_0x0319:
            java.lang.String r1 = r12.about
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 == 0) goto L_0x0327
            r1 = 2131628037(0x7f0e1005, float:1.8883355E38)
            java.lang.String r5 = "VoipAddDescription"
            goto L_0x032c
        L_0x0327:
            r1 = 2131628066(0x7f0e1022, float:1.8883414E38)
            java.lang.String r5 = "VoipEditDescription"
        L_0x032c:
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r5, r1)
            r4.add(r1)
        L_0x0333:
            java.lang.String r1 = r12.about
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 == 0) goto L_0x033f
            r1 = 2131165702(0x7var_, float:1.7945629E38)
            goto L_0x0342
        L_0x033f:
            r1 = 2131165711(0x7var_f, float:1.7945647E38)
        L_0x0342:
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            r6.add(r1)
            r1 = 10
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            r2.add(r1)
            if (r3 <= 0) goto L_0x035a
            r1 = 2131628067(0x7f0e1023, float:1.8883416E38)
            java.lang.String r5 = "VoipEditName"
            goto L_0x035f
        L_0x035a:
            r1 = 2131628068(0x7f0e1024, float:1.8883418E38)
            java.lang.String r5 = "VoipEditTitle"
        L_0x035f:
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r5, r1)
            r4.add(r1)
            r1 = 2131165737(0x7var_, float:1.79457E38)
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            r6.add(r1)
            r1 = 11
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            r2.add(r1)
            r5 = r10
            goto L_0x04e1
        L_0x037c:
            org.telegram.tgnet.TLRPC$Chat r5 = r7.currentChat
            boolean r5 = org.telegram.messenger.ChatObject.canManageCalls(r5)
            java.lang.String r8 = "VoipGroupOpenChannel"
            r22 = 6
            r23 = 2131165846(0x7var_, float:1.794592E38)
            r24 = 2131165842(0x7var_, float:1.7945913E38)
            if (r5 == 0) goto L_0x045f
            if (r1 == 0) goto L_0x0397
            boolean r5 = r12.muted
            if (r5 != 0) goto L_0x0395
            goto L_0x0397
        L_0x0395:
            r5 = r10
            goto L_0x03ec
        L_0x0397:
            boolean r5 = r12.muted
            if (r5 == 0) goto L_0x03d0
            boolean r5 = r12.can_self_unmute
            if (r5 == 0) goto L_0x03a0
            goto L_0x03d0
        L_0x03a0:
            r5 = 2131628080(0x7f0e1030, float:1.8883443E38)
            java.lang.String r9 = "VoipGroupAllowToSpeak"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r9, r5)
            r4.add(r5)
            r5 = r10
            long r9 = r12.raise_hand_rating
            r25 = 0
            int r24 = (r9 > r25 ? 1 : (r9 == r25 ? 0 : -1))
            if (r24 == 0) goto L_0x03c0
            r9 = 2131165707(0x7var_b, float:1.7945639E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            r6.add(r9)
            goto L_0x03c7
        L_0x03c0:
            java.lang.Integer r9 = java.lang.Integer.valueOf(r23)
            r6.add(r9)
        L_0x03c7:
            r9 = 1
            java.lang.Integer r10 = java.lang.Integer.valueOf(r9)
            r2.add(r10)
            goto L_0x03ec
        L_0x03d0:
            r5 = r10
            r9 = 2131628121(0x7f0e1059, float:1.8883526E38)
            java.lang.String r10 = "VoipGroupMute"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r10, r9)
            r4.add(r9)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r24)
            r6.add(r9)
            r9 = 0
            java.lang.Integer r10 = java.lang.Integer.valueOf(r9)
            r2.add(r10)
        L_0x03ec:
            org.telegram.tgnet.TLRPC$Peer r9 = r12.peer
            int r9 = r9.channel_id
            if (r9 == 0) goto L_0x0418
            int r10 = r7.currentAccount
            boolean r9 = org.telegram.messenger.ChatObject.isMegagroup(r10, r9)
            if (r9 != 0) goto L_0x0418
            r9 = 2131628129(0x7f0e1061, float:1.8883542E38)
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r8, r9)
            r4.add(r8)
            r8 = 2131165720(0x7var_, float:1.7945665E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            r6.add(r8)
            r8 = 8
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            r2.add(r8)
            goto L_0x0435
        L_0x0418:
            r8 = 2131628132(0x7f0e1064, float:1.8883548E38)
            java.lang.String r9 = "VoipGroupOpenProfile"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r9, r8)
            r4.add(r8)
            r8 = 2131165775(0x7var_f, float:1.7945777E38)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            r6.add(r8)
            java.lang.Integer r8 = java.lang.Integer.valueOf(r22)
            r2.add(r8)
        L_0x0435:
            if (r1 != 0) goto L_0x04e1
            org.telegram.tgnet.TLRPC$Chat r1 = r7.currentChat
            boolean r1 = org.telegram.messenger.ChatObject.canBlockUsers(r1)
            if (r1 == 0) goto L_0x04e1
            r1 = 2131628171(0x7f0e108b, float:1.8883627E38)
            java.lang.String r8 = "VoipGroupUserRemove"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r8, r1)
            r4.add(r1)
            r1 = 2131165713(0x7var_, float:1.794565E38)
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            r6.add(r1)
            r1 = 2
            java.lang.Integer r8 = java.lang.Integer.valueOf(r1)
            r2.add(r8)
            goto L_0x04e1
        L_0x045f:
            r5 = r10
            boolean r1 = r12.muted_by_you
            if (r1 == 0) goto L_0x0480
            r1 = 2131628165(0x7f0e1085, float:1.8883615E38)
            java.lang.String r9 = "VoipGroupUnmuteForMe"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r9, r1)
            r4.add(r1)
            java.lang.Integer r1 = java.lang.Integer.valueOf(r23)
            r6.add(r1)
            r1 = 4
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            r2.add(r1)
            goto L_0x049b
        L_0x0480:
            r1 = 2131628122(0x7f0e105a, float:1.8883528E38)
            java.lang.String r9 = "VoipGroupMuteForMe"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r9, r1)
            r4.add(r1)
            java.lang.Integer r1 = java.lang.Integer.valueOf(r24)
            r6.add(r1)
            r1 = 5
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            r2.add(r1)
        L_0x049b:
            org.telegram.tgnet.TLRPC$Peer r1 = r12.peer
            int r1 = r1.channel_id
            r9 = 2131165772(0x7var_c, float:1.794577E38)
            if (r1 == 0) goto L_0x04c7
            int r10 = r7.currentAccount
            boolean r1 = org.telegram.messenger.ChatObject.isMegagroup(r10, r1)
            if (r1 != 0) goto L_0x04c7
            r1 = 2131628129(0x7f0e1061, float:1.8883542E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r8, r1)
            r4.add(r1)
            java.lang.Integer r1 = java.lang.Integer.valueOf(r9)
            r6.add(r1)
            r1 = 8
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            r2.add(r1)
            goto L_0x04e1
        L_0x04c7:
            r1 = 2131628130(0x7f0e1062, float:1.8883544E38)
            java.lang.String r8 = "VoipGroupOpenChat"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r8, r1)
            r4.add(r1)
            java.lang.Integer r1 = java.lang.Integer.valueOf(r9)
            r6.add(r1)
            java.lang.Integer r1 = java.lang.Integer.valueOf(r22)
            r2.add(r1)
        L_0x04e1:
            int r1 = r4.size()
            r8 = 0
        L_0x04e6:
            if (r8 >= r1) goto L_0x055e
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r9 = new org.telegram.ui.ActionBar.ActionBarMenuSubItem
            android.content.Context r10 = r27.getContext()
            r21 = r11
            if (r8 != 0) goto L_0x04f4
            r11 = 1
            goto L_0x04f5
        L_0x04f4:
            r11 = 0
        L_0x04f5:
            r22 = r3
            int r3 = r1 + -1
            if (r8 != r3) goto L_0x04fd
            r3 = 1
            goto L_0x04fe
        L_0x04fd:
            r3 = 0
        L_0x04fe:
            r9.<init>(r10, r11, r3)
            java.lang.Object r3 = r2.get(r8)
            java.lang.Integer r3 = (java.lang.Integer) r3
            int r3 = r3.intValue()
            r10 = 2
            if (r3 == r10) goto L_0x051c
            java.lang.String r3 = "voipgroup_actionBarItems"
            int r10 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            r9.setColors(r10, r3)
            goto L_0x0529
        L_0x051c:
            java.lang.String r3 = "voipgroup_leaveCallMenu"
            int r10 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            r9.setColors(r10, r3)
        L_0x0529:
            java.lang.String r3 = "voipgroup_listSelector"
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            r9.setSelectorColor(r3)
            java.lang.Object r3 = r4.get(r8)
            java.lang.CharSequence r3 = (java.lang.CharSequence) r3
            java.lang.Object r10 = r6.get(r8)
            java.lang.Integer r10 = (java.lang.Integer) r10
            int r10 = r10.intValue()
            r9.setTextAndIcon(r3, r10)
            r14.addView(r9)
            java.lang.Object r3 = r2.get(r8)
            r9.setTag(r3)
            org.telegram.ui.-$$Lambda$GroupCallActivity$FR4-XBcAw_8Z8q26po2gfePla1g r3 = new org.telegram.ui.-$$Lambda$GroupCallActivity$FR4-XBcAw_8Z8q26po2gfePla1g
            r3.<init>(r8, r2, r12)
            r9.setOnClickListener(r3)
            int r8 = r8 + 1
            r11 = r21
            r3 = r22
            goto L_0x04e6
        L_0x055e:
            r22 = r3
            r21 = r11
            r1 = 51
            r2 = -2
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createScroll(r2, r2, r1)
            r0.addView(r15, r3)
            org.telegram.ui.Components.RecyclerListView r0 = r7.listView
            r0.stopScroll()
            org.telegram.ui.Components.FillLastGridLayoutManager r0 = r7.layoutManager
            r2 = 0
            r0.setCanScrollVertically(r2)
            r7.scrimView = r5
            r0 = 1
            r5.setAboutVisible(r0)
            android.view.ViewGroup r0 = r7.containerView
            r0.invalidate()
            org.telegram.ui.Components.RecyclerListView r0 = r7.listView
            r0.invalidate()
            android.animation.AnimatorSet r0 = r7.scrimAnimatorSet
            if (r0 == 0) goto L_0x058e
            r0.cancel()
        L_0x058e:
            r7.scrimPopupLayout = r13
            if (r22 <= 0) goto L_0x05ae
            org.telegram.messenger.AccountInstance r0 = r7.accountInstance
            org.telegram.messenger.MessagesController r0 = r0.getMessagesController()
            java.lang.Integer r2 = java.lang.Integer.valueOf(r22)
            org.telegram.tgnet.TLRPC$User r0 = r0.getUser(r2)
            r2 = 0
            org.telegram.messenger.ImageLocation r3 = org.telegram.messenger.ImageLocation.getForUserOrChat(r0, r2)
            r4 = 1
            org.telegram.messenger.ImageLocation r0 = org.telegram.messenger.ImageLocation.getForUserOrChat(r0, r4)
            r6 = r3
            r3 = r22
            goto L_0x05c9
        L_0x05ae:
            r2 = 0
            r4 = 1
            org.telegram.messenger.AccountInstance r0 = r7.accountInstance
            org.telegram.messenger.MessagesController r0 = r0.getMessagesController()
            r3 = r22
            int r6 = -r3
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            org.telegram.tgnet.TLRPC$Chat r0 = r0.getChat(r6)
            org.telegram.messenger.ImageLocation r6 = org.telegram.messenger.ImageLocation.getForUserOrChat(r0, r2)
            org.telegram.messenger.ImageLocation r0 = org.telegram.messenger.ImageLocation.getForUserOrChat(r0, r4)
        L_0x05c9:
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r2 = r7.scrimRenderer
            if (r2 == 0) goto L_0x05d5
            boolean r2 = r2.isAttached()
            if (r2 == 0) goto L_0x05d5
            r2 = 1
            goto L_0x05d6
        L_0x05d5:
            r2 = 0
        L_0x05d6:
            if (r6 != 0) goto L_0x05dc
            if (r2 != 0) goto L_0x05dc
            r11 = 0
            goto L_0x0633
        L_0x05dc:
            if (r21 == 0) goto L_0x0631
            org.telegram.ui.Components.ProfileGalleryView r4 = r7.avatarsViewPager
            org.telegram.ui.Cells.GroupCallUserCell r8 = r7.scrimView
            org.telegram.ui.Components.BackupImageView r8 = r8.getAvatarImageView()
            r4.setParentAvatarImage(r8)
            org.telegram.ui.Components.ProfileGalleryView r4 = r7.avatarsViewPager
            r4.setHasActiveVideo(r2)
            org.telegram.ui.Components.ProfileGalleryView r2 = r7.avatarsViewPager
            long r8 = (long) r3
            r4 = 1
            r2.setData(r8, r4)
            org.telegram.ui.Components.ProfileGalleryView r2 = r7.avatarsViewPager
            r2.setCreateThumbFromParent(r4)
            org.telegram.ui.Components.ProfileGalleryView r2 = r7.avatarsViewPager
            r2.initIfEmpty(r6, r0)
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r0 = r7.scrimRenderer
            if (r0 == 0) goto L_0x0606
            r0.setShowingAsScrimView(r4, r4)
        L_0x0606:
            org.telegram.tgnet.TLRPC$Peer r0 = r7.selfPeer
            int r0 = org.telegram.messenger.MessageObject.getPeerId(r0)
            if (r0 != r3) goto L_0x0631
            org.telegram.ui.Components.ImageUpdater r0 = r7.currentAvatarUpdater
            if (r0 == 0) goto L_0x0631
            org.telegram.ui.GroupCallActivity$AvatarUpdaterDelegate r0 = r7.avatarUpdaterDelegate
            if (r0 == 0) goto L_0x0631
            org.telegram.tgnet.TLRPC$FileLocation r0 = r0.avatar
            if (r0 == 0) goto L_0x0631
            org.telegram.ui.Components.ProfileGalleryView r0 = r7.avatarsViewPager
            org.telegram.ui.GroupCallActivity$AvatarUpdaterDelegate r2 = r7.avatarUpdaterDelegate
            org.telegram.messenger.ImageLocation r2 = r2.uploadingImageLocation
            org.telegram.ui.GroupCallActivity$AvatarUpdaterDelegate r3 = r7.avatarUpdaterDelegate
            org.telegram.tgnet.TLRPC$FileLocation r3 = r3.avatar
            org.telegram.messenger.ImageLocation r3 = org.telegram.messenger.ImageLocation.getForLocal(r3)
            r0.addUploadingImage(r2, r3)
        L_0x0631:
            r11 = r21
        L_0x0633:
            r0 = -2147483648(0xfffffffvar_, float:-0.0)
            r2 = 1148846080(0x447a0000, float:1000.0)
            if (r11 == 0) goto L_0x0681
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
            r7.useBlur = r3
            r27.prepareBlurBitmap()
            r7.avatarPriviewTransitionInProgress = r3
            android.widget.FrameLayout r0 = r7.avatarPreviewContainer
            r1 = 0
            r0.setVisibility(r1)
            if (r16 == 0) goto L_0x066f
            r16.invalidate()
        L_0x066f:
            r7.runAvatarPreviewTransition(r3, r5)
            org.telegram.ui.Components.GroupCallFullscreenAdapter$GroupCallUserCell r0 = r7.scrimFullscreenView
            if (r0 == 0) goto L_0x067e
            org.telegram.ui.Components.BackupImageView r0 = r0.getAvatarImageView()
            r1 = 0
            r0.setAlpha(r1)
        L_0x067e:
            r0 = 1
            goto L_0x07db
        L_0x0681:
            r3 = 1
            r4 = 0
            r7.avatarsPreviewShowed = r4
            org.telegram.ui.GroupCallActivity$53 r4 = new org.telegram.ui.GroupCallActivity$53
            r6 = -2
            r4.<init>(r13, r6, r6)
            r7.scrimPopupWindow = r4
            r4.setPauseNotifications(r3)
            org.telegram.ui.ActionBar.ActionBarPopupWindow r4 = r7.scrimPopupWindow
            r6 = 220(0xdc, float:3.08E-43)
            r4.setDismissAnimationDuration(r6)
            org.telegram.ui.ActionBar.ActionBarPopupWindow r4 = r7.scrimPopupWindow
            r4.setOutsideTouchable(r3)
            org.telegram.ui.ActionBar.ActionBarPopupWindow r4 = r7.scrimPopupWindow
            r4.setClippingEnabled(r3)
            org.telegram.ui.ActionBar.ActionBarPopupWindow r4 = r7.scrimPopupWindow
            r6 = 2131689477(0x7f0var_, float:1.900797E38)
            r4.setAnimationStyle(r6)
            org.telegram.ui.ActionBar.ActionBarPopupWindow r4 = r7.scrimPopupWindow
            r4.setFocusable(r3)
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
            org.telegram.ui.Components.GroupCallFullscreenAdapter$GroupCallUserCell r0 = r7.scrimFullscreenView
            if (r0 == 0) goto L_0x0751
            boolean r2 = isLandscapeMode
            if (r2 == 0) goto L_0x071c
            float r0 = r0.getX()
            org.telegram.ui.Components.RecyclerListView r2 = r7.fullscreenUsersListView
            float r2 = r2.getX()
            float r0 = r0 + r2
            org.telegram.ui.Components.voip.GroupCallRenderersContainer r2 = r7.renderersContainer
            float r2 = r2.getX()
            float r0 = r0 + r2
            int r0 = (int) r0
            int r2 = r13.getMeasuredWidth()
            int r0 = r0 - r2
            r2 = 1107296256(0x42000000, float:32.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r0 = r0 + r2
            org.telegram.ui.Components.GroupCallFullscreenAdapter$GroupCallUserCell r2 = r7.scrimFullscreenView
            float r2 = r2.getY()
            org.telegram.ui.Components.RecyclerListView r3 = r7.fullscreenUsersListView
            float r3 = r3.getY()
            float r2 = r2 + r3
            org.telegram.ui.Components.voip.GroupCallRenderersContainer r3 = r7.renderersContainer
            float r3 = r3.getY()
            float r2 = r2 + r3
            int r2 = (int) r2
            r3 = 1086324736(0x40CLASSNAME, float:6.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r2 = r2 - r3
            goto L_0x07a4
        L_0x071c:
            float r0 = r0.getX()
            org.telegram.ui.Components.RecyclerListView r2 = r7.fullscreenUsersListView
            float r2 = r2.getX()
            float r0 = r0 + r2
            org.telegram.ui.Components.voip.GroupCallRenderersContainer r2 = r7.renderersContainer
            float r2 = r2.getX()
            float r0 = r0 + r2
            int r0 = (int) r0
            r2 = 1096810496(0x41600000, float:14.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r0 = r0 - r2
            org.telegram.ui.Components.GroupCallFullscreenAdapter$GroupCallUserCell r2 = r7.scrimFullscreenView
            float r2 = r2.getY()
            org.telegram.ui.Components.RecyclerListView r3 = r7.fullscreenUsersListView
            float r3 = r3.getY()
            float r2 = r2 + r3
            org.telegram.ui.Components.voip.GroupCallRenderersContainer r3 = r7.renderersContainer
            float r3 = r3.getY()
            float r2 = r2 + r3
            int r3 = r13.getMeasuredHeight()
            float r3 = (float) r3
            float r2 = r2 - r3
            goto L_0x0783
        L_0x0751:
            org.telegram.ui.Components.RecyclerListView r0 = r7.listView
            float r0 = r0.getX()
            org.telegram.ui.Components.RecyclerListView r2 = r7.listView
            int r2 = r2.getMeasuredWidth()
            float r2 = (float) r2
            float r0 = r0 + r2
            r2 = 1090519040(0x41000000, float:8.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r2 = (float) r2
            float r0 = r0 + r2
            int r2 = r13.getMeasuredWidth()
            float r2 = (float) r2
            float r0 = r0 - r2
            int r0 = (int) r0
            boolean r2 = r7.hasScrimAnchorView
            if (r2 == 0) goto L_0x0785
            org.telegram.ui.Components.RecyclerListView r2 = r7.listView
            float r2 = r2.getY()
            float r3 = r5.getY()
            float r2 = r2 + r3
            int r3 = r5.getClipHeight()
        L_0x0781:
            float r3 = (float) r3
            float r2 = r2 + r3
        L_0x0783:
            int r2 = (int) r2
            goto L_0x07a4
        L_0x0785:
            org.telegram.ui.Components.voip.GroupCallGridCell r2 = r7.scrimGridView
            if (r2 == 0) goto L_0x079d
            org.telegram.ui.Components.RecyclerListView r2 = r7.listView
            float r2 = r2.getY()
            org.telegram.ui.Components.voip.GroupCallGridCell r3 = r7.scrimGridView
            float r3 = r3.getY()
            float r2 = r2 + r3
            org.telegram.ui.Components.voip.GroupCallGridCell r3 = r7.scrimGridView
            int r3 = r3.getMeasuredHeight()
            goto L_0x0781
        L_0x079d:
            org.telegram.ui.Components.RecyclerListView r2 = r7.listView
            float r2 = r2.getY()
            goto L_0x0783
        L_0x07a4:
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
            goto L_0x067e
        L_0x07db:
            return r0
        L_0x07dc:
            r0 = 1
            r7.dismissAvatarPreview(r0)
            r0 = 0
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.GroupCallActivity.showMenuForCell(android.view.View):boolean");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$showMenuForCell$60 */
    public /* synthetic */ void lambda$showMenuForCell$60$GroupCallActivity(KeyEvent keyEvent) {
        ActionBarPopupWindow actionBarPopupWindow;
        if (keyEvent.getKeyCode() == 4 && keyEvent.getRepeatCount() == 0 && (actionBarPopupWindow = this.scrimPopupWindow) != null && actionBarPopupWindow.isShowing()) {
            this.scrimPopupWindow.dismiss();
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$showMenuForCell$61 */
    public /* synthetic */ void lambda$showMenuForCell$61$GroupCallActivity(int i, ArrayList arrayList, TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant, View view) {
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
    public void clearScrimView() {
        GroupCallMiniTextureView groupCallMiniTextureView = this.scrimRenderer;
        if (groupCallMiniTextureView != null) {
            groupCallMiniTextureView.textureView.setRoundCorners((float) AndroidUtilities.dp(8.0f));
            this.scrimRenderer.setShowingAsScrimView(false, false);
            this.scrimRenderer.invalidate();
            this.renderersContainer.invalidate();
        }
        GroupCallUserCell groupCallUserCell = this.scrimView;
        if (groupCallUserCell != null && !this.hasScrimAnchorView) {
            groupCallUserCell.setProgressToAvatarPreview(0.0f);
            this.scrimView.setAboutVisible(false);
            if (this.scrimView.getParent() != null) {
                this.containerView.removeView(this.scrimView);
            }
        }
        GroupCallUserCell groupCallUserCell2 = this.scrimView;
        if (groupCallUserCell2 != null) {
            groupCallUserCell2.getAvatarImageView().setAlpha(1.0f);
        }
        GroupCallFullscreenAdapter.GroupCallUserCell groupCallUserCell3 = this.scrimFullscreenView;
        if (groupCallUserCell3 != null) {
            groupCallUserCell3.getAvatarImageView().setAlpha(1.0f);
        }
        this.scrimView = null;
        this.scrimGridView = null;
        this.scrimFullscreenView = null;
        this.scrimRenderer = null;
    }

    private void startScreenCapture() {
        LaunchActivity launchActivity = this.parentActivity;
        if (launchActivity != null && Build.VERSION.SDK_INT >= 21) {
            this.parentActivity.startActivityForResult(((MediaProjectionManager) launchActivity.getSystemService("media_projection")).createScreenCaptureIntent(), 520);
        }
    }

    private void runAvatarPreviewTransition(boolean z, GroupCallUserCell groupCallUserCell) {
        int i;
        float f;
        float f2;
        float f3;
        GroupCallMiniTextureView groupCallMiniTextureView;
        float f4;
        float f5;
        int i2;
        GroupCallMiniTextureView groupCallMiniTextureView2;
        float y;
        float y2;
        boolean z2;
        final boolean z3 = z;
        float dp = (float) (AndroidUtilities.dp(14.0f) + this.containerView.getPaddingLeft());
        float dp2 = (float) (AndroidUtilities.dp(14.0f) + this.containerView.getPaddingTop());
        float f6 = 1.0f;
        if (this.hasScrimAnchorView) {
            float x = ((groupCallUserCell.getAvatarImageView().getX() + groupCallUserCell.getX()) + this.listView.getX()) - dp;
            float y3 = ((groupCallUserCell.getAvatarImageView().getY() + groupCallUserCell.getY()) + this.listView.getY()) - dp2;
            float measuredHeight = ((float) groupCallUserCell.getAvatarImageView().getMeasuredHeight()) / ((float) this.listView.getMeasuredWidth());
            f = y3;
            i = (int) (((float) (groupCallUserCell.getAvatarImageView().getMeasuredHeight() >> 1)) / measuredHeight);
            f2 = x;
            f3 = measuredHeight;
        } else {
            if (this.scrimRenderer == null) {
                this.previewTextureTransitionEnabled = true;
            } else {
                if (!z3) {
                    ProfileGalleryView profileGalleryView = this.avatarsViewPager;
                    if (profileGalleryView.getRealPosition(profileGalleryView.getCurrentItem()) != 0) {
                        z2 = false;
                        this.previewTextureTransitionEnabled = z2;
                    }
                }
                z2 = true;
                this.previewTextureTransitionEnabled = z2;
            }
            GroupCallGridCell groupCallGridCell = this.scrimGridView;
            float f7 = 0.96f;
            if (groupCallGridCell == null || !this.previewTextureTransitionEnabled) {
                GroupCallFullscreenAdapter.GroupCallUserCell groupCallUserCell2 = this.scrimFullscreenView;
                if (groupCallUserCell2 != null) {
                    if (this.scrimRenderer == null) {
                        f4 = (((groupCallUserCell2.getAvatarImageView().getX() + this.scrimFullscreenView.getX()) + this.fullscreenUsersListView.getX()) + this.renderersContainer.getX()) - dp;
                        f5 = (((this.scrimFullscreenView.getAvatarImageView().getY() + this.scrimFullscreenView.getY()) + this.fullscreenUsersListView.getY()) + this.renderersContainer.getY()) - dp2;
                        f7 = ((float) this.scrimFullscreenView.getAvatarImageView().getMeasuredHeight()) / ((float) this.listView.getMeasuredWidth());
                        i2 = (int) (((float) (this.scrimFullscreenView.getAvatarImageView().getMeasuredHeight() >> 1)) / f7);
                        if (!this.previewTextureTransitionEnabled && (groupCallMiniTextureView2 = this.scrimRenderer) != null) {
                            groupCallMiniTextureView2.invalidate();
                            this.renderersContainer.invalidate();
                            this.scrimRenderer.setShowingAsScrimView(false, false);
                            this.scrimRenderer = null;
                        }
                        i = i2;
                        f = f5;
                        float f8 = f7;
                        f2 = f4;
                        f3 = f8;
                    } else if (this.previewTextureTransitionEnabled) {
                        f4 = ((groupCallUserCell2.getX() + this.fullscreenUsersListView.getX()) + this.renderersContainer.getX()) - dp;
                        y = this.scrimFullscreenView.getY() + this.fullscreenUsersListView.getY();
                        y2 = this.renderersContainer.getY();
                    }
                }
                i2 = 0;
                f5 = 0.0f;
                f4 = 0.0f;
                groupCallMiniTextureView2.invalidate();
                this.renderersContainer.invalidate();
                this.scrimRenderer.setShowingAsScrimView(false, false);
                this.scrimRenderer = null;
                i = i2;
                f = f5;
                float var_ = f7;
                f2 = f4;
                f3 = var_;
            } else {
                f4 = (groupCallGridCell.getX() + this.listView.getX()) - dp;
                y = this.scrimGridView.getY() + this.listView.getY();
                y2 = (float) AndroidUtilities.dp(2.0f);
            }
            f5 = (y + y2) - dp2;
            i2 = 0;
            f7 = 1.0f;
            groupCallMiniTextureView2.invalidate();
            this.renderersContainer.invalidate();
            this.scrimRenderer.setShowingAsScrimView(false, false);
            this.scrimRenderer = null;
            i = i2;
            f = f5;
            float var_ = f7;
            f2 = f4;
            f3 = var_;
        }
        if (z3) {
            this.avatarPreviewContainer.setScaleX(f3);
            this.avatarPreviewContainer.setScaleY(f3);
            this.avatarPreviewContainer.setTranslationX(f2);
            this.avatarPreviewContainer.setTranslationY(f);
            this.avatarPagerIndicator.setAlpha(0.0f);
        }
        this.avatarsViewPager.setRoundRadius(i, i);
        if (this.useBlur) {
            if (z3) {
                this.blurredView.setAlpha(0.0f);
            }
            this.blurredView.animate().alpha(z3 ? 1.0f : 0.0f).setDuration(220).start();
        }
        this.avatarPagerIndicator.animate().alpha(z3 ? 1.0f : 0.0f).setDuration(220).start();
        if (!z3 && (groupCallMiniTextureView = this.scrimRenderer) != null) {
            groupCallMiniTextureView.setShowingAsScrimView(false, true);
            ProfileGalleryView profileGalleryView2 = this.avatarsViewPager;
            if (profileGalleryView2.getRealPosition(profileGalleryView2.getCurrentItem()) != 0) {
                this.scrimRenderer.textureView.cancelAnimation();
                this.scrimGridView = null;
            }
        }
        float[] fArr = new float[2];
        fArr[0] = z3 ? 0.0f : 1.0f;
        if (!z3) {
            f6 = 0.0f;
        }
        fArr[1] = f6;
        $$Lambda$GroupCallActivity$gvRZliCiHQxd28itBwuU_geAdQ r8 = r0;
        float f9 = f;
        ValueAnimator ofFloat = ValueAnimator.ofFloat(fArr);
        $$Lambda$GroupCallActivity$gvRZliCiHQxd28itBwuU_geAdQ r0 = new ValueAnimator.AnimatorUpdateListener(f3, f2, f9, i) {
            public final /* synthetic */ float f$1;
            public final /* synthetic */ float f$2;
            public final /* synthetic */ float f$3;
            public final /* synthetic */ int f$4;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
            }

            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                GroupCallActivity.this.lambda$runAvatarPreviewTransition$62$GroupCallActivity(this.f$1, this.f$2, this.f$3, this.f$4, valueAnimator);
            }
        };
        ofFloat.addUpdateListener(r8);
        this.popupAnimationIndex = this.accountInstance.getNotificationCenter().setAnimationInProgress(this.popupAnimationIndex, new int[]{NotificationCenter.dialogPhotosLoaded, NotificationCenter.fileDidLoad, NotificationCenter.messagesDidLoad});
        final GroupCallMiniTextureView groupCallMiniTextureView3 = this.scrimGridView == null ? null : this.scrimRenderer;
        if (groupCallMiniTextureView3 != null) {
            groupCallMiniTextureView3.animateToScrimView = true;
        }
        ofFloat.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                GroupCallMiniTextureView groupCallMiniTextureView = groupCallMiniTextureView3;
                if (groupCallMiniTextureView != null) {
                    groupCallMiniTextureView.animateToScrimView = false;
                }
                GroupCallActivity.this.accountInstance.getNotificationCenter().onAnimationFinish(GroupCallActivity.this.popupAnimationIndex);
                boolean unused = GroupCallActivity.this.avatarPriviewTransitionInProgress = false;
                float unused2 = GroupCallActivity.this.progressToAvatarPreview = z3 ? 1.0f : 0.0f;
                GroupCallActivity.this.renderersContainer.progressToScrimView = GroupCallActivity.this.progressToAvatarPreview;
                if (!z3) {
                    GroupCallActivity.this.scrimPaint.setAlpha(0);
                    GroupCallActivity.this.clearScrimView();
                    if (GroupCallActivity.this.scrimPopupLayout.getParent() != null) {
                        GroupCallActivity.this.containerView.removeView(GroupCallActivity.this.scrimPopupLayout);
                    }
                    View unused3 = GroupCallActivity.this.scrimPopupLayout = null;
                    GroupCallActivity.this.avatarPreviewContainer.setVisibility(8);
                    boolean unused4 = GroupCallActivity.this.avatarsPreviewShowed = false;
                    GroupCallActivity.this.layoutManager.setCanScrollVertically(true);
                    GroupCallActivity.this.blurredView.setVisibility(8);
                    if (GroupCallActivity.this.delayedGroupCallUpdated) {
                        boolean unused5 = GroupCallActivity.this.delayedGroupCallUpdated = false;
                        GroupCallActivity.this.applyCallParticipantUpdates();
                    }
                    if (GroupCallActivity.this.scrimRenderer != null) {
                        GroupCallActivity.this.scrimRenderer.textureView.setRoundCorners(0.0f);
                    }
                } else {
                    GroupCallActivity.this.avatarPreviewContainer.setAlpha(1.0f);
                    GroupCallActivity.this.avatarPreviewContainer.setScaleX(1.0f);
                    GroupCallActivity.this.avatarPreviewContainer.setScaleY(1.0f);
                    GroupCallActivity.this.avatarPreviewContainer.setTranslationX(0.0f);
                    GroupCallActivity.this.avatarPreviewContainer.setTranslationY(0.0f);
                }
                GroupCallActivity.this.checkContentOverlayed();
                GroupCallActivity.this.containerView.invalidate();
                GroupCallActivity.this.avatarsViewPager.invalidate();
                GroupCallActivity.this.listView.invalidate();
            }
        });
        if (this.hasScrimAnchorView || this.scrimRenderer == null) {
            ofFloat.setInterpolator(CubicBezierInterpolator.DEFAULT);
            ofFloat.setDuration(220);
            ofFloat.start();
        } else {
            ofFloat.setInterpolator(CubicBezierInterpolator.DEFAULT);
            ofFloat.setDuration(220);
            this.scrimRenderer.textureView.setAnimateNextDuration(220);
            this.scrimRenderer.textureView.synchOrRunAnimation(ofFloat);
        }
        checkContentOverlayed();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$runAvatarPreviewTransition$62 */
    public /* synthetic */ void lambda$runAvatarPreviewTransition$62$GroupCallActivity(float f, float f2, float f3, int i, ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.progressToAvatarPreview = floatValue;
        this.renderersContainer.progressToScrimView = floatValue;
        float f4 = (f * (1.0f - floatValue)) + (floatValue * 1.0f);
        this.avatarPreviewContainer.setScaleX(f4);
        this.avatarPreviewContainer.setScaleY(f4);
        this.avatarPreviewContainer.setTranslationX(f2 * (1.0f - this.progressToAvatarPreview));
        this.avatarPreviewContainer.setTranslationY(f3 * (1.0f - this.progressToAvatarPreview));
        if (!this.useBlur) {
            this.scrimPaint.setAlpha((int) (this.progressToAvatarPreview * 100.0f));
        }
        GroupCallMiniTextureView groupCallMiniTextureView = this.scrimRenderer;
        if (groupCallMiniTextureView != null) {
            groupCallMiniTextureView.textureView.setRoundCorners(((float) AndroidUtilities.dp(8.0f)) * (1.0f - this.progressToAvatarPreview));
        }
        this.avatarPreviewContainer.invalidate();
        this.containerView.invalidate();
        ProfileGalleryView profileGalleryView = this.avatarsViewPager;
        float f5 = (float) i;
        float f6 = this.progressToAvatarPreview;
        profileGalleryView.setRoundRadius((int) ((1.0f - f6) * f5), (int) (f5 * (1.0f - f6)));
    }

    /* access modifiers changed from: private */
    public void dismissAvatarPreview(boolean z) {
        if (!this.avatarPriviewTransitionInProgress && this.avatarsPreviewShowed) {
            if (z) {
                this.avatarPriviewTransitionInProgress = true;
                runAvatarPreviewTransition(false, this.scrimView);
                return;
            }
            clearScrimView();
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
        /* access modifiers changed from: private */
        public int usersVideoGridEndRow;
        /* access modifiers changed from: private */
        public int usersVideoGridStartRow;
        private int videoCount;
        /* access modifiers changed from: private */
        public int videoGridDividerRow;

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
        /* JADX WARNING: Code restructure failed: missing block: B:28:0x00c4, code lost:
            if (android.text.TextUtils.isEmpty(r0.username) == false) goto L_0x00ca;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void updateRows() {
            /*
                r3 = this;
                org.telegram.ui.GroupCallActivity r0 = org.telegram.ui.GroupCallActivity.this
                org.telegram.messenger.ChatObject$Call r0 = r0.call
                if (r0 == 0) goto L_0x00da
                boolean r0 = r0.isScheduled()
                if (r0 != 0) goto L_0x00da
                org.telegram.ui.GroupCallActivity r0 = org.telegram.ui.GroupCallActivity.this
                boolean r0 = r0.delayedGroupCallUpdated
                if (r0 == 0) goto L_0x0016
                goto L_0x00da
            L_0x0016:
                r0 = 0
                r3.rowsCount = r0
                org.telegram.ui.GroupCallActivity r1 = org.telegram.ui.GroupCallActivity.this
                org.telegram.messenger.ChatObject$Call r2 = r1.call
                android.util.SparseArray<org.telegram.tgnet.TLRPC$TL_groupCallParticipant> r2 = r2.participants
                org.telegram.tgnet.TLRPC$Peer r1 = r1.selfPeer
                int r1 = org.telegram.messenger.MessageObject.getPeerId(r1)
                int r1 = r2.indexOfKey(r1)
                if (r1 < 0) goto L_0x002e
                r0 = 1
            L_0x002e:
                r3.hasSelfUser = r0
                int r0 = r3.rowsCount
                r3.usersVideoGridStartRow = r0
                org.telegram.ui.GroupCallActivity r1 = org.telegram.ui.GroupCallActivity.this
                java.util.ArrayList<org.telegram.messenger.ChatObject$VideoParticipant> r1 = r1.visibleVideoParticipants
                int r1 = r1.size()
                int r0 = r0 + r1
                r3.rowsCount = r0
                r3.usersVideoGridEndRow = r0
                org.telegram.ui.GroupCallActivity r0 = org.telegram.ui.GroupCallActivity.this
                java.util.ArrayList<org.telegram.messenger.ChatObject$VideoParticipant> r0 = r0.visibleVideoParticipants
                int r0 = r0.size()
                r3.videoCount = r0
                r1 = -1
                if (r0 <= 0) goto L_0x0057
                int r0 = r3.rowsCount
                int r2 = r0 + 1
                r3.rowsCount = r2
                r3.videoGridDividerRow = r0
                goto L_0x0059
            L_0x0057:
                r3.videoGridDividerRow = r1
            L_0x0059:
                int r0 = r3.rowsCount
                r3.usersStartRow = r0
                org.telegram.ui.GroupCallActivity r2 = org.telegram.ui.GroupCallActivity.this
                org.telegram.messenger.ChatObject$Call r2 = r2.call
                java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_groupCallParticipant> r2 = r2.visibleParticipants
                int r2 = r2.size()
                int r0 = r0 + r2
                r3.rowsCount = r0
                r3.usersEndRow = r0
                org.telegram.ui.GroupCallActivity r0 = org.telegram.ui.GroupCallActivity.this
                org.telegram.messenger.ChatObject$Call r0 = r0.call
                java.util.ArrayList<java.lang.Integer> r0 = r0.invitedUsers
                boolean r0 = r0.isEmpty()
                if (r0 == 0) goto L_0x007d
                r3.invitedStartRow = r1
                r3.invitedEndRow = r1
                goto L_0x0090
            L_0x007d:
                int r0 = r3.rowsCount
                r3.invitedStartRow = r0
                org.telegram.ui.GroupCallActivity r2 = org.telegram.ui.GroupCallActivity.this
                org.telegram.messenger.ChatObject$Call r2 = r2.call
                java.util.ArrayList<java.lang.Integer> r2 = r2.invitedUsers
                int r2 = r2.size()
                int r0 = r0 + r2
                r3.rowsCount = r0
                r3.invitedEndRow = r0
            L_0x0090:
                org.telegram.ui.GroupCallActivity r0 = org.telegram.ui.GroupCallActivity.this
                org.telegram.tgnet.TLRPC$Chat r0 = r0.currentChat
                boolean r0 = org.telegram.messenger.ChatObject.isChannel(r0)
                if (r0 == 0) goto L_0x00a2
                org.telegram.ui.GroupCallActivity r0 = org.telegram.ui.GroupCallActivity.this
                org.telegram.tgnet.TLRPC$Chat r0 = r0.currentChat
                boolean r0 = r0.megagroup
                if (r0 == 0) goto L_0x00ac
            L_0x00a2:
                org.telegram.ui.GroupCallActivity r0 = org.telegram.ui.GroupCallActivity.this
                org.telegram.tgnet.TLRPC$Chat r0 = r0.currentChat
                boolean r0 = org.telegram.messenger.ChatObject.canWriteToChat(r0)
                if (r0 != 0) goto L_0x00ca
            L_0x00ac:
                org.telegram.ui.GroupCallActivity r0 = org.telegram.ui.GroupCallActivity.this
                org.telegram.tgnet.TLRPC$Chat r0 = r0.currentChat
                boolean r0 = org.telegram.messenger.ChatObject.isChannel(r0)
                if (r0 == 0) goto L_0x00c7
                org.telegram.ui.GroupCallActivity r0 = org.telegram.ui.GroupCallActivity.this
                org.telegram.tgnet.TLRPC$Chat r0 = r0.currentChat
                boolean r2 = r0.megagroup
                if (r2 != 0) goto L_0x00c7
                java.lang.String r0 = r0.username
                boolean r0 = android.text.TextUtils.isEmpty(r0)
                if (r0 != 0) goto L_0x00c7
                goto L_0x00ca
            L_0x00c7:
                r3.addMemberRow = r1
                goto L_0x00d2
            L_0x00ca:
                int r0 = r3.rowsCount
                int r1 = r0 + 1
                r3.rowsCount = r1
                r3.addMemberRow = r0
            L_0x00d2:
                int r0 = r3.rowsCount
                int r1 = r0 + 1
                r3.rowsCount = r1
                r3.lastRow = r0
            L_0x00da:
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

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v29, resolved type: java.lang.Object} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v6, resolved type: java.lang.Integer} */
        /* JADX WARNING: type inference failed for: r10v0, types: [org.telegram.tgnet.TLRPC$FileLocation] */
        /* JADX WARNING: type inference failed for: r4v2, types: [org.telegram.tgnet.TLRPC$FileLocation] */
        /* JADX WARNING: type inference failed for: r4v11, types: [org.telegram.tgnet.TLRPC$FileLocation] */
        /* JADX WARNING: Multi-variable type inference failed */
        /* JADX WARNING: Removed duplicated region for block: B:20:0x006a  */
        /* JADX WARNING: Removed duplicated region for block: B:38:0x00bd  */
        /* JADX WARNING: Removed duplicated region for block: B:64:0x0165  */
        /* JADX WARNING: Removed duplicated region for block: B:94:? A[RETURN, SYNTHETIC] */
        /* JADX WARNING: Removed duplicated region for block: B:98:? A[RETURN, SYNTHETIC] */
        /* JADX WARNING: Unknown variable types count: 1 */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r13, int r14) {
            /*
                r12 = this;
                int r0 = r13.getItemViewType()
                r1 = 1065353216(0x3var_, float:1.0)
                r2 = 0
                if (r0 == 0) goto L_0x01b0
                r3 = 1
                r4 = 0
                if (r0 == r3) goto L_0x011c
                r1 = 2
                if (r0 == r1) goto L_0x00c9
                r1 = 4
                if (r0 == r1) goto L_0x0015
                goto L_0x0210
            L_0x0015:
                android.view.View r13 = r13.itemView
                org.telegram.ui.Components.voip.GroupCallGridCell r13 = (org.telegram.ui.Components.voip.GroupCallGridCell) r13
                org.telegram.messenger.ChatObject$VideoParticipant r0 = r13.getParticipant()
                int r1 = r12.usersVideoGridStartRow
                int r1 = r14 - r1
                org.telegram.ui.GroupCallActivity r5 = org.telegram.ui.GroupCallActivity.this
                androidx.recyclerview.widget.GridLayoutManager$SpanSizeLookup r5 = r5.spanSizeLookup
                int r14 = r5.getSpanSize(r14)
                r13.spanCount = r14
                org.telegram.ui.GroupCallActivity r14 = org.telegram.ui.GroupCallActivity.this
                boolean r14 = r14.delayedGroupCallUpdated
                if (r14 == 0) goto L_0x0050
                if (r1 < 0) goto L_0x0067
                org.telegram.ui.GroupCallActivity r14 = org.telegram.ui.GroupCallActivity.this
                java.util.ArrayList r14 = r14.oldVideoParticipants
                int r14 = r14.size()
                if (r1 >= r14) goto L_0x0067
                org.telegram.ui.GroupCallActivity r14 = org.telegram.ui.GroupCallActivity.this
                java.util.ArrayList r14 = r14.oldVideoParticipants
                java.lang.Object r14 = r14.get(r1)
                org.telegram.messenger.ChatObject$VideoParticipant r14 = (org.telegram.messenger.ChatObject.VideoParticipant) r14
                goto L_0x0068
            L_0x0050:
                if (r1 < 0) goto L_0x0067
                org.telegram.ui.GroupCallActivity r14 = org.telegram.ui.GroupCallActivity.this
                java.util.ArrayList<org.telegram.messenger.ChatObject$VideoParticipant> r14 = r14.visibleVideoParticipants
                int r14 = r14.size()
                if (r1 >= r14) goto L_0x0067
                org.telegram.ui.GroupCallActivity r14 = org.telegram.ui.GroupCallActivity.this
                java.util.ArrayList<org.telegram.messenger.ChatObject$VideoParticipant> r14 = r14.visibleVideoParticipants
                java.lang.Object r14 = r14.get(r1)
                org.telegram.messenger.ChatObject$VideoParticipant r14 = (org.telegram.messenger.ChatObject.VideoParticipant) r14
                goto L_0x0068
            L_0x0067:
                r14 = r4
            L_0x0068:
                if (r14 == 0) goto L_0x00ab
                org.telegram.tgnet.TLRPC$TL_groupCallParticipant r1 = r14.participant
                org.telegram.tgnet.TLRPC$Peer r1 = r1.peer
                int r1 = org.telegram.messenger.MessageObject.getPeerId(r1)
                org.telegram.ui.GroupCallActivity r5 = org.telegram.ui.GroupCallActivity.this
                org.telegram.tgnet.TLRPC$Peer r5 = r5.selfPeer
                int r5 = org.telegram.messenger.MessageObject.getPeerId(r5)
                if (r1 != r5) goto L_0x0088
                org.telegram.ui.GroupCallActivity r1 = org.telegram.ui.GroupCallActivity.this
                org.telegram.ui.GroupCallActivity$AvatarUpdaterDelegate r1 = r1.avatarUpdaterDelegate
                if (r1 == 0) goto L_0x0088
                org.telegram.tgnet.TLRPC$FileLocation r4 = r1.avatar
            L_0x0088:
                if (r4 == 0) goto L_0x0090
                org.telegram.ui.GroupCallActivity r1 = org.telegram.ui.GroupCallActivity.this
                org.telegram.ui.GroupCallActivity$AvatarUpdaterDelegate r1 = r1.avatarUpdaterDelegate
                float r1 = r1.uploadingProgress
            L_0x0090:
                org.telegram.messenger.ChatObject$VideoParticipant r1 = r13.getParticipant()
                if (r1 == 0) goto L_0x009e
                org.telegram.messenger.ChatObject$VideoParticipant r1 = r13.getParticipant()
                boolean r1 = r1.equals(r14)
            L_0x009e:
                org.telegram.ui.GroupCallActivity r1 = org.telegram.ui.GroupCallActivity.this
                org.telegram.messenger.AccountInstance r1 = r1.accountInstance
                org.telegram.ui.GroupCallActivity r4 = org.telegram.ui.GroupCallActivity.this
                org.telegram.messenger.ChatObject$Call r4 = r4.call
                r13.setData(r1, r14, r4, r5)
            L_0x00ab:
                if (r0 == 0) goto L_0x0210
                boolean r14 = r0.equals(r14)
                if (r14 != 0) goto L_0x0210
                boolean r14 = r13.attached
                if (r14 == 0) goto L_0x0210
                org.telegram.ui.Components.voip.GroupCallMiniTextureView r14 = r13.getRenderer()
                if (r14 == 0) goto L_0x0210
                org.telegram.ui.GroupCallActivity r14 = org.telegram.ui.GroupCallActivity.this
                r14.attachRenderer(r13, r2)
                org.telegram.ui.GroupCallActivity r14 = org.telegram.ui.GroupCallActivity.this
                r14.attachRenderer(r13, r3)
                goto L_0x0210
            L_0x00c9:
                android.view.View r13 = r13.itemView
                org.telegram.ui.Cells.GroupCallInvitedCell r13 = (org.telegram.ui.Cells.GroupCallInvitedCell) r13
                int r0 = r12.invitedStartRow
                int r14 = r14 - r0
                org.telegram.ui.GroupCallActivity r0 = org.telegram.ui.GroupCallActivity.this
                boolean r0 = r0.delayedGroupCallUpdated
                if (r0 == 0) goto L_0x00f4
                if (r14 < 0) goto L_0x010f
                org.telegram.ui.GroupCallActivity r0 = org.telegram.ui.GroupCallActivity.this
                java.util.ArrayList r0 = r0.oldInvited
                int r0 = r0.size()
                if (r14 >= r0) goto L_0x010f
                org.telegram.ui.GroupCallActivity r0 = org.telegram.ui.GroupCallActivity.this
                java.util.ArrayList r0 = r0.oldInvited
                java.lang.Object r14 = r0.get(r14)
                r4 = r14
                java.lang.Integer r4 = (java.lang.Integer) r4
                goto L_0x010f
            L_0x00f4:
                if (r14 < 0) goto L_0x010f
                org.telegram.ui.GroupCallActivity r0 = org.telegram.ui.GroupCallActivity.this
                org.telegram.messenger.ChatObject$Call r0 = r0.call
                java.util.ArrayList<java.lang.Integer> r0 = r0.invitedUsers
                int r0 = r0.size()
                if (r14 >= r0) goto L_0x010f
                org.telegram.ui.GroupCallActivity r0 = org.telegram.ui.GroupCallActivity.this
                org.telegram.messenger.ChatObject$Call r0 = r0.call
                java.util.ArrayList<java.lang.Integer> r0 = r0.invitedUsers
                java.lang.Object r14 = r0.get(r14)
                r4 = r14
                java.lang.Integer r4 = (java.lang.Integer) r4
            L_0x010f:
                if (r4 == 0) goto L_0x0210
                org.telegram.ui.GroupCallActivity r14 = org.telegram.ui.GroupCallActivity.this
                int r14 = r14.currentAccount
                r13.setData(r14, r4)
                goto L_0x0210
            L_0x011c:
                android.view.View r13 = r13.itemView
                org.telegram.ui.Cells.GroupCallUserCell r13 = (org.telegram.ui.Cells.GroupCallUserCell) r13
                int r0 = r12.usersStartRow
                int r14 = r14 - r0
                org.telegram.ui.GroupCallActivity r0 = org.telegram.ui.GroupCallActivity.this
                boolean r0 = r0.delayedGroupCallUpdated
                if (r0 == 0) goto L_0x0146
                if (r14 < 0) goto L_0x0162
                org.telegram.ui.GroupCallActivity r0 = org.telegram.ui.GroupCallActivity.this
                java.util.ArrayList r0 = r0.oldParticipants
                int r0 = r0.size()
                if (r14 >= r0) goto L_0x0162
                org.telegram.ui.GroupCallActivity r0 = org.telegram.ui.GroupCallActivity.this
                java.util.ArrayList r0 = r0.oldParticipants
                java.lang.Object r14 = r0.get(r14)
                org.telegram.tgnet.TLRPC$TL_groupCallParticipant r14 = (org.telegram.tgnet.TLRPC$TL_groupCallParticipant) r14
                goto L_0x0160
            L_0x0146:
                if (r14 < 0) goto L_0x0162
                org.telegram.ui.GroupCallActivity r0 = org.telegram.ui.GroupCallActivity.this
                org.telegram.messenger.ChatObject$Call r0 = r0.call
                java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_groupCallParticipant> r0 = r0.visibleParticipants
                int r0 = r0.size()
                if (r14 >= r0) goto L_0x0162
                org.telegram.ui.GroupCallActivity r0 = org.telegram.ui.GroupCallActivity.this
                org.telegram.messenger.ChatObject$Call r0 = r0.call
                java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_groupCallParticipant> r0 = r0.visibleParticipants
                java.lang.Object r14 = r0.get(r14)
                org.telegram.tgnet.TLRPC$TL_groupCallParticipant r14 = (org.telegram.tgnet.TLRPC$TL_groupCallParticipant) r14
            L_0x0160:
                r7 = r14
                goto L_0x0163
            L_0x0162:
                r7 = r4
            L_0x0163:
                if (r7 == 0) goto L_0x0210
                org.telegram.tgnet.TLRPC$Peer r14 = r7.peer
                int r14 = org.telegram.messenger.MessageObject.getPeerId(r14)
                org.telegram.ui.GroupCallActivity r0 = org.telegram.ui.GroupCallActivity.this
                org.telegram.tgnet.TLRPC$Peer r0 = r0.selfPeer
                int r9 = org.telegram.messenger.MessageObject.getPeerId(r0)
                if (r14 != r9) goto L_0x0181
                org.telegram.ui.GroupCallActivity r0 = org.telegram.ui.GroupCallActivity.this
                org.telegram.ui.GroupCallActivity$AvatarUpdaterDelegate r0 = r0.avatarUpdaterDelegate
                if (r0 == 0) goto L_0x0181
                org.telegram.tgnet.TLRPC$FileLocation r4 = r0.avatar
            L_0x0181:
                r10 = r4
                if (r10 == 0) goto L_0x018a
                org.telegram.ui.GroupCallActivity r0 = org.telegram.ui.GroupCallActivity.this
                org.telegram.ui.GroupCallActivity$AvatarUpdaterDelegate r0 = r0.avatarUpdaterDelegate
                float r1 = r0.uploadingProgress
            L_0x018a:
                org.telegram.tgnet.TLRPC$TL_groupCallParticipant r0 = r13.getParticipant()
                if (r0 == 0) goto L_0x019d
                org.telegram.tgnet.TLRPC$TL_groupCallParticipant r0 = r13.getParticipant()
                org.telegram.tgnet.TLRPC$Peer r0 = r0.peer
                int r0 = org.telegram.messenger.MessageObject.getPeerId(r0)
                if (r0 != r14) goto L_0x019d
                r2 = 1
            L_0x019d:
                org.telegram.ui.GroupCallActivity r14 = org.telegram.ui.GroupCallActivity.this
                org.telegram.messenger.AccountInstance r6 = r14.accountInstance
                org.telegram.ui.GroupCallActivity r14 = org.telegram.ui.GroupCallActivity.this
                org.telegram.messenger.ChatObject$Call r8 = r14.call
                r5 = r13
                r11 = r2
                r5.setData(r6, r7, r8, r9, r10, r11)
                r13.setUploadProgress(r1, r2)
                goto L_0x0210
            L_0x01b0:
                android.view.View r13 = r13.itemView
                org.telegram.ui.Cells.GroupCallTextCell r13 = (org.telegram.ui.Cells.GroupCallTextCell) r13
                java.lang.String r14 = "voipgroup_lastSeenTextUnscrolled"
                int r14 = org.telegram.ui.ActionBar.Theme.getColor(r14)
                java.lang.String r0 = "voipgroup_lastSeenText"
                int r0 = org.telegram.ui.ActionBar.Theme.getColor(r0)
                org.telegram.ui.GroupCallActivity r3 = org.telegram.ui.GroupCallActivity.this
                org.telegram.ui.ActionBar.ActionBar r3 = r3.actionBar
                java.lang.Object r3 = r3.getTag()
                if (r3 == 0) goto L_0x01cf
                r3 = 1065353216(0x3var_, float:1.0)
                goto L_0x01d0
            L_0x01cf:
                r3 = 0
            L_0x01d0:
                int r14 = org.telegram.messenger.AndroidUtilities.getOffsetColor(r14, r0, r3, r1)
                r13.setColors(r14, r14)
                org.telegram.ui.GroupCallActivity r14 = org.telegram.ui.GroupCallActivity.this
                org.telegram.tgnet.TLRPC$Chat r14 = r14.currentChat
                boolean r14 = org.telegram.messenger.ChatObject.isChannel(r14)
                if (r14 == 0) goto L_0x0201
                org.telegram.ui.GroupCallActivity r14 = org.telegram.ui.GroupCallActivity.this
                org.telegram.tgnet.TLRPC$Chat r14 = r14.currentChat
                boolean r0 = r14.megagroup
                if (r0 != 0) goto L_0x0201
                java.lang.String r14 = r14.username
                boolean r14 = android.text.TextUtils.isEmpty(r14)
                if (r14 != 0) goto L_0x0201
                r14 = 2131628148(0x7f0e1074, float:1.888358E38)
                java.lang.String r0 = "VoipGroupShareLink"
                java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r0, r14)
                r0 = 2131165754(0x7var_a, float:1.7945734E38)
                r13.setTextAndIcon(r14, r0, r2)
                goto L_0x0210
            L_0x0201:
                r14 = 2131628106(0x7f0e104a, float:1.8883495E38)
                java.lang.String r0 = "VoipGroupInviteMember"
                java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r0, r14)
                r0 = 2131165249(0x7var_, float:1.794471E38)
                r13.setTextAndIcon(r14, r0, r2)
            L_0x0210:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.GroupCallActivity.ListAdapter.onBindViewHolder(androidx.recyclerview.widget.RecyclerView$ViewHolder, int):void");
        }

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            int itemViewType = viewHolder.getItemViewType();
            if (itemViewType == 1) {
                return true;
            }
            return (itemViewType == 3 || itemViewType == 4 || itemViewType == 5) ? false : true;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view;
            if (i == 0) {
                view = new GroupCallTextCell(this.mContext) {
                    /* access modifiers changed from: protected */
                    public void onMeasure(int i, int i2) {
                        if (AndroidUtilities.isTablet()) {
                            super.onMeasure(View.MeasureSpec.makeMeasureSpec(Math.min(AndroidUtilities.dp(420.0f), View.MeasureSpec.getSize(i)), NUM), i2);
                        } else {
                            super.onMeasure(i, i2);
                        }
                    }
                };
            } else if (i == 1) {
                view = new GroupCallUserCell(this.mContext) {
                    /* access modifiers changed from: protected */
                    public void onMuteClick(GroupCallUserCell groupCallUserCell) {
                        boolean unused = GroupCallActivity.this.showMenuForCell(groupCallUserCell);
                    }

                    /* access modifiers changed from: protected */
                    public void onMeasure(int i, int i2) {
                        if (AndroidUtilities.isTablet()) {
                            super.onMeasure(View.MeasureSpec.makeMeasureSpec(Math.min(AndroidUtilities.dp(420.0f), View.MeasureSpec.getSize(i)), NUM), i2);
                        } else {
                            super.onMeasure(i, i2);
                        }
                    }
                };
            } else if (i == 2) {
                view = new GroupCallInvitedCell(this.mContext) {
                    /* access modifiers changed from: protected */
                    public void onMeasure(int i, int i2) {
                        if (AndroidUtilities.isTablet()) {
                            super.onMeasure(View.MeasureSpec.makeMeasureSpec(Math.min(AndroidUtilities.dp(420.0f), View.MeasureSpec.getSize(i)), NUM), i2);
                        } else {
                            super.onMeasure(i, i2);
                        }
                    }
                };
            } else if (i == 4) {
                view = new GroupCallGridCell(this.mContext, false) {
                    /* access modifiers changed from: protected */
                    public void onAttachedToWindow() {
                        super.onAttachedToWindow();
                        if (GroupCallActivity.this.listView.getVisibility() == 0 && GroupCallActivity.this.listViewVideoVisibility) {
                            GroupCallActivity.this.attachRenderer(this, true);
                        }
                    }

                    /* access modifiers changed from: protected */
                    public void onDetachedFromWindow() {
                        super.onDetachedFromWindow();
                        GroupCallActivity.this.attachRenderer(this, false);
                    }
                };
            } else if (i != 5) {
                view = new View(this.mContext);
            } else {
                view = new View(this.mContext) {
                    /* access modifiers changed from: protected */
                    public void onMeasure(int i, int i2) {
                        super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(GroupCallActivity.isLandscapeMode ? 0.0f : 8.0f), NUM));
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
            if (i == this.videoGridDividerRow) {
                return 5;
            }
            if (i < this.usersStartRow || i >= this.usersEndRow) {
                return (i < this.usersVideoGridStartRow || i >= this.usersVideoGridEndRow) ? 2 : 4;
            }
            return 1;
        }
    }

    /* access modifiers changed from: private */
    public void attachRenderer(GroupCallGridCell groupCallGridCell, boolean z) {
        if (!isDismissed()) {
            if (z && groupCallGridCell.getRenderer() == null) {
                groupCallGridCell.setRenderer(GroupCallMiniTextureView.getOrCreate(this.attachedRenderers, this.renderersContainer, groupCallGridCell, (GroupCallFullscreenAdapter.GroupCallUserCell) null, (GroupCallGridCell) null, groupCallGridCell.getParticipant(), this.call, this));
            } else if (!z && groupCallGridCell.getRenderer() != null) {
                groupCallGridCell.getRenderer().setPrimaryView((GroupCallGridCell) null);
                groupCallGridCell.setRenderer((GroupCallMiniTextureView) null);
            }
        }
    }

    public void setOldRows(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
        this.oldAddMemberRow = i;
        this.oldUsersStartRow = i2;
        this.oldUsersEndRow = i3;
        this.oldInvitedStartRow = i4;
        this.oldInvitedEndRow = i5;
        this.oldUsersVideoStartRow = i6;
        this.oldUsersVideoEndRow = i7;
        this.oldVideoDividerRow = i8;
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
                GroupCallActivity.this.lambda$toggleAdminSpeak$63$GroupCallActivity(tLObject, tLRPC$TL_error);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$toggleAdminSpeak$63 */
    public /* synthetic */ void lambda$toggleAdminSpeak$63$GroupCallActivity(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
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
        } else if (this.renderersContainer.inFullscreenMode) {
            fullscreenFor((ChatObject.VideoParticipant) null);
        } else {
            VideoPreviewDialog videoPreviewDialog = this.previewDialog;
            if (videoPreviewDialog != null) {
                videoPreviewDialog.dismiss(false);
            } else {
                super.onBackPressed();
            }
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
        /* renamed from: lambda$didUploadPhoto$1 */
        public /* synthetic */ void lambda$didUploadPhoto$1$GroupCallActivity$AvatarUpdaterDelegate(String str, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
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
                    GroupCallActivity.AvatarUpdaterDelegate.this.lambda$didUploadPhoto$0$GroupCallActivity$AvatarUpdaterDelegate(this.f$1, this.f$2, this.f$3);
                }
            });
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$didUploadPhoto$0 */
        public /* synthetic */ void lambda$didUploadPhoto$0$GroupCallActivity$AvatarUpdaterDelegate(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, String str) {
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
        /* renamed from: lambda$didUploadPhoto$2 */
        public /* synthetic */ void lambda$didUploadPhoto$2$GroupCallActivity$AvatarUpdaterDelegate() {
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
                        GroupCallActivity.AvatarUpdaterDelegate.this.lambda$didUploadPhoto$1$GroupCallActivity$AvatarUpdaterDelegate(this.f$1, tLObject, tLRPC$TL_error);
                    }
                });
            } else {
                GroupCallActivity.this.accountInstance.getMessagesController().changeChatAvatar(-this.peerId, (TLRPC$TL_inputChatPhoto) null, tLRPC$InputFile, tLRPC$InputFile2, d, str, tLRPC$PhotoSize3.location, tLRPC$PhotoSize4.location, new Runnable() {
                    public final void run() {
                        GroupCallActivity.AvatarUpdaterDelegate.this.lambda$didUploadPhoto$2$GroupCallActivity$AvatarUpdaterDelegate();
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

    public View getScrimView() {
        return this.scrimView;
    }

    public void onCameraSwitch(boolean z) {
        this.attachedRenderersTmp.clear();
        this.attachedRenderersTmp.addAll(this.attachedRenderers);
        for (int i = 0; i < this.attachedRenderersTmp.size(); i++) {
            this.attachedRenderersTmp.get(i).updateAttachState(true);
        }
        VideoPreviewDialog videoPreviewDialog = this.previewDialog;
        if (videoPreviewDialog != null) {
            videoPreviewDialog.update();
        }
    }

    public static boolean videoIsActive(TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant, boolean z, ChatObject.Call call2) {
        VoIPService sharedInstance = VoIPService.getSharedInstance();
        if (sharedInstance == null) {
            return false;
        }
        if (tLRPC$TL_groupCallParticipant.self) {
            if (sharedInstance.getVideoState(z) == 2) {
                return true;
            }
            return false;
        } else if (call2.participants.get(MessageObject.getPeerId(tLRPC$TL_groupCallParticipant.peer)) == null) {
            return false;
        } else {
            if (z) {
                if (tLRPC$TL_groupCallParticipant.presentation != null) {
                    return true;
                }
                return false;
            } else if (tLRPC$TL_groupCallParticipant.video != null) {
                return true;
            } else {
                return false;
            }
        }
    }

    private class GroupCallItemAnimator extends DefaultItemAnimator {
        HashSet<RecyclerView.ViewHolder> addingHolders;
        public float animationProgress;
        public ValueAnimator animator;
        float outMaxBottom;
        float outMinTop;
        HashSet<RecyclerView.ViewHolder> removingHolders;

        private GroupCallItemAnimator() {
            this.addingHolders = new HashSet<>();
            this.removingHolders = new HashSet<>();
        }

        public void endAnimations() {
            super.endAnimations();
            this.removingHolders.clear();
            this.addingHolders.clear();
            this.outMinTop = Float.MAX_VALUE;
            GroupCallActivity.this.listView.invalidate();
        }

        public void updateBackgroundBeforeAnimation() {
            if (this.animator == null) {
                this.addingHolders.clear();
                this.addingHolders.addAll(this.mPendingAdditions);
                this.removingHolders.clear();
                this.removingHolders.addAll(this.mPendingRemovals);
                this.outMaxBottom = 0.0f;
                this.outMinTop = Float.MAX_VALUE;
                if (!this.addingHolders.isEmpty() || !this.removingHolders.isEmpty()) {
                    int childCount = GroupCallActivity.this.listView.getChildCount();
                    for (int i = 0; i < childCount; i++) {
                        View childAt = GroupCallActivity.this.listView.getChildAt(i);
                        RecyclerView.ViewHolder findContainingViewHolder = GroupCallActivity.this.listView.findContainingViewHolder(childAt);
                        if (!(findContainingViewHolder == null || findContainingViewHolder.getItemViewType() == 3 || findContainingViewHolder.getItemViewType() == 4 || findContainingViewHolder.getItemViewType() == 5 || this.addingHolders.contains(findContainingViewHolder))) {
                            this.outMaxBottom = Math.max(this.outMaxBottom, childAt.getY() + ((float) childAt.getMeasuredHeight()));
                            this.outMinTop = Math.min(this.outMinTop, Math.max(0.0f, childAt.getY()));
                        }
                    }
                    this.animationProgress = 0.0f;
                    GroupCallActivity.this.listView.invalidate();
                }
            }
        }

        public void runPendingAnimations() {
            boolean z = !this.mPendingRemovals.isEmpty();
            boolean z2 = !this.mPendingMoves.isEmpty();
            boolean z3 = !this.mPendingAdditions.isEmpty();
            ValueAnimator valueAnimator = this.animator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
                this.animator = null;
            }
            if (z || z2 || z3) {
                this.animationProgress = 0.0f;
                ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
                this.animator = ofFloat;
                ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                        GroupCallActivity.GroupCallItemAnimator.this.lambda$runPendingAnimations$0$GroupCallActivity$GroupCallItemAnimator(valueAnimator);
                    }
                });
                this.animator.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        super.onAnimationEnd(animator);
                        GroupCallItemAnimator groupCallItemAnimator = GroupCallItemAnimator.this;
                        groupCallItemAnimator.animator = null;
                        GroupCallActivity.this.listView.invalidate();
                        GroupCallActivity.this.renderersContainer.invalidate();
                        GroupCallActivity.this.containerView.invalidate();
                        GroupCallActivity.this.updateLayout(true);
                        GroupCallItemAnimator.this.addingHolders.clear();
                        GroupCallItemAnimator.this.removingHolders.clear();
                    }
                });
                this.animator.setDuration(350);
                this.animator.setInterpolator(CubicBezierInterpolator.DEFAULT);
                this.animator.start();
                GroupCallActivity.this.listView.invalidate();
                GroupCallActivity.this.renderersContainer.invalidate();
            }
            super.runPendingAnimations();
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$runPendingAnimations$0 */
        public /* synthetic */ void lambda$runPendingAnimations$0$GroupCallActivity$GroupCallItemAnimator(ValueAnimator valueAnimator) {
            this.animationProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            GroupCallActivity.this.listView.invalidate();
            GroupCallActivity.this.renderersContainer.invalidate();
            GroupCallActivity.this.containerView.invalidate();
            GroupCallActivity.this.updateLayout(true);
        }
    }

    /* access modifiers changed from: protected */
    public boolean canDismissWithTouchOutside() {
        return !this.renderersContainer.inFullscreenMode;
    }

    public void onResume() {
        paused = false;
        this.listAdapter.notifyDataSetChanged();
        if (this.fullscreenUsersListView.getVisibility() == 0) {
            this.fullscreenAdapter.update(false, this.fullscreenUsersListView);
        }
        if (isTabletMode) {
            this.tabletGridAdapter.update(false, this.tabletVideoGridView);
        }
        this.attachedRenderersTmp.clear();
        this.attachedRenderersTmp.addAll(this.attachedRenderers);
        for (int i = 0; i < this.attachedRenderersTmp.size(); i++) {
            this.attachedRenderersTmp.get(i).updateAttachState(true);
        }
    }

    public void onPause() {
        paused = true;
        this.attachedRenderersTmp.clear();
        this.attachedRenderersTmp.addAll(this.attachedRenderers);
        for (int i = 0; i < this.attachedRenderersTmp.size(); i++) {
            this.attachedRenderersTmp.get(i).updateAttachState(false);
        }
    }
}
