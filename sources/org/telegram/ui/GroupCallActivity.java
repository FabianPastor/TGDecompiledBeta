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
import android.graphics.Rect;
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
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Property;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
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
import java.util.Iterator;
import java.util.Locale;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.support.LongSparseIntArray;
import org.telegram.messenger.voip.VoIPService;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
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
import org.telegram.ui.Components.voip.PrivateVideoPreviewDialog;
import org.telegram.ui.Components.voip.RTMPStreamPipOverlay;
import org.telegram.ui.Components.voip.VoIPToggleButton;
import org.webrtc.VideoSink;

public class GroupCallActivity extends BottomSheet implements NotificationCenter.NotificationCenterDelegate, VoIPService.StateListener {
    public static final Property<GroupCallActivity, Float> COLOR_PROGRESS = new AnimationProperties.FloatProperty<GroupCallActivity>("colorProgress") {
        public void setValue(GroupCallActivity object, float value) {
            object.setColorProgress(value);
        }

        public Float get(GroupCallActivity object) {
            return Float.valueOf(object.getColorProgress());
        }
    };
    public static final float MAX_AMPLITUDE = 8500.0f;
    private static final int MUTE_BUTTON_STATE_CANCEL_REMINDER = 7;
    private static final int MUTE_BUTTON_STATE_CONNECTING = 3;
    private static final int MUTE_BUTTON_STATE_MUTE = 1;
    private static final int MUTE_BUTTON_STATE_MUTED_BY_ADMIN = 2;
    private static final int MUTE_BUTTON_STATE_RAISED_HAND = 4;
    private static final int MUTE_BUTTON_STATE_SET_REMINDER = 6;
    private static final int MUTE_BUTTON_STATE_START_NOW = 5;
    private static final int MUTE_BUTTON_STATE_UNMUTE = 0;
    public static final int TABLET_LIST_SIZE = 320;
    public static final long TRANSITION_DURATION = 350;
    private static final int admin_can_speak_item = 2;
    public static int currentScreenOrientation = 0;
    private static final int edit_item = 6;
    private static final int eveyone_can_speak_item = 1;
    public static GroupCallActivity groupCallInstance = null;
    public static boolean groupCallUiVisible = false;
    public static boolean isLandscapeMode = false;
    public static boolean isTabletMode = false;
    private static final int leave_item = 4;
    private static final int noise_item = 11;
    public static boolean paused = false;
    private static final int permission_item = 7;
    private static final int screen_capture_item = 9;
    private static final int share_invite_link_item = 3;
    private static final int sound_item = 10;
    private static final int start_record_item = 5;
    private static final int user_item = 8;
    private static final int user_item_gap = 0;
    /* access modifiers changed from: private */
    public View accountGap;
    /* access modifiers changed from: private */
    public AccountInstance accountInstance;
    /* access modifiers changed from: private */
    public AccountSelectCell accountSelectCell;
    /* access modifiers changed from: private */
    public ActionBar actionBar;
    /* access modifiers changed from: private */
    public AnimatorSet actionBarAnimation;
    /* access modifiers changed from: private */
    public View actionBarBackground;
    /* access modifiers changed from: private */
    public View actionBarShadow;
    ObjectAnimator additionalSubtitleYAnimator;
    /* access modifiers changed from: private */
    public ActionBarMenuSubItem adminItem;
    /* access modifiers changed from: private */
    public float amplitude;
    /* access modifiers changed from: private */
    public float animateAmplitudeDiff;
    boolean animateButtonsOnNextLayout;
    /* access modifiers changed from: private */
    public float animateToAmplitude;
    private boolean animatingToFullscreenExpand = false;
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
    public TLRPC.Chat currentChat;
    private ViewGroup currentOptionsLayout;
    /* access modifiers changed from: private */
    public WeavingState currentState;
    /* access modifiers changed from: private */
    public boolean delayedGroupCallUpdated;
    private DiffUtil.Callback diffUtilsCallback = new DiffUtil.Callback() {
        public int getOldListSize() {
            return GroupCallActivity.this.oldCount;
        }

        public int getNewListSize() {
            return GroupCallActivity.this.listAdapter.rowsCount;
        }

        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            if (GroupCallActivity.this.listAdapter.addMemberRow >= 0) {
                if (oldItemPosition == GroupCallActivity.this.oldAddMemberRow && newItemPosition == GroupCallActivity.this.listAdapter.addMemberRow) {
                    return true;
                }
                if ((oldItemPosition == GroupCallActivity.this.oldAddMemberRow && newItemPosition != GroupCallActivity.this.listAdapter.addMemberRow) || (oldItemPosition != GroupCallActivity.this.oldAddMemberRow && newItemPosition == GroupCallActivity.this.listAdapter.addMemberRow)) {
                    return false;
                }
            }
            if (GroupCallActivity.this.listAdapter.videoNotAvailableRow >= 0) {
                if (oldItemPosition == GroupCallActivity.this.oldVideoNotAvailableRow && newItemPosition == GroupCallActivity.this.listAdapter.videoNotAvailableRow) {
                    return true;
                }
                if ((oldItemPosition == GroupCallActivity.this.oldVideoNotAvailableRow && newItemPosition != GroupCallActivity.this.listAdapter.videoNotAvailableRow) || (oldItemPosition != GroupCallActivity.this.oldVideoNotAvailableRow && newItemPosition == GroupCallActivity.this.listAdapter.videoNotAvailableRow)) {
                    return false;
                }
            }
            if (GroupCallActivity.this.listAdapter.videoGridDividerRow >= 0 && GroupCallActivity.this.listAdapter.videoGridDividerRow == newItemPosition && oldItemPosition == GroupCallActivity.this.oldVideoDividerRow) {
                return true;
            }
            if (oldItemPosition == GroupCallActivity.this.oldCount - 1 && newItemPosition == GroupCallActivity.this.listAdapter.rowsCount - 1) {
                return true;
            }
            if (oldItemPosition == GroupCallActivity.this.oldCount - 1 || newItemPosition == GroupCallActivity.this.listAdapter.rowsCount - 1) {
                return false;
            }
            if (newItemPosition >= GroupCallActivity.this.listAdapter.usersVideoGridStartRow && newItemPosition < GroupCallActivity.this.listAdapter.usersVideoGridEndRow && oldItemPosition >= GroupCallActivity.this.oldUsersVideoStartRow && oldItemPosition < GroupCallActivity.this.oldUsersVideoEndRow) {
                return ((ChatObject.VideoParticipant) GroupCallActivity.this.oldVideoParticipants.get(oldItemPosition - GroupCallActivity.this.oldUsersVideoStartRow)).equals(GroupCallActivity.this.visibleVideoParticipants.get(newItemPosition - GroupCallActivity.this.listAdapter.usersVideoGridStartRow));
            }
            if (newItemPosition >= GroupCallActivity.this.listAdapter.usersStartRow && newItemPosition < GroupCallActivity.this.listAdapter.usersEndRow && oldItemPosition >= GroupCallActivity.this.oldUsersStartRow && oldItemPosition < GroupCallActivity.this.oldUsersEndRow) {
                TLRPC.TL_groupCallParticipant oldItem = (TLRPC.TL_groupCallParticipant) GroupCallActivity.this.oldParticipants.get(oldItemPosition - GroupCallActivity.this.oldUsersStartRow);
                if (MessageObject.getPeerId(oldItem.peer) != MessageObject.getPeerId(GroupCallActivity.this.call.visibleParticipants.get(newItemPosition - GroupCallActivity.this.listAdapter.usersStartRow).peer)) {
                    return false;
                }
                if (oldItemPosition == newItemPosition || oldItem.lastActiveDate == ((long) oldItem.active_date)) {
                    return true;
                }
                return false;
            } else if (newItemPosition < GroupCallActivity.this.listAdapter.invitedStartRow || newItemPosition >= GroupCallActivity.this.listAdapter.invitedEndRow || oldItemPosition < GroupCallActivity.this.oldInvitedStartRow || oldItemPosition >= GroupCallActivity.this.oldInvitedEndRow) {
                return false;
            } else {
                return ((Long) GroupCallActivity.this.oldInvited.get(oldItemPosition - GroupCallActivity.this.oldInvitedStartRow)).equals(GroupCallActivity.this.call.invitedUsers.get(newItemPosition - GroupCallActivity.this.listAdapter.invitedStartRow));
            }
        }

        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return true;
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
    public ValueAnimator expandAnimator;
    /* access modifiers changed from: private */
    public ImageView expandButton;
    /* access modifiers changed from: private */
    public ValueAnimator expandSizeAnimator;
    /* access modifiers changed from: private */
    public VoIPToggleButton flipButton;
    private final RLottieDrawable flipIcon;
    private int flipIconCurrentEndFrame;
    GroupCallFullscreenAdapter fullscreenAdapter;
    /* access modifiers changed from: private */
    public final DefaultItemAnimator fullscreenListItemAnimator;
    ValueAnimator fullscreenModeAnimator;
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
    public ImageView minimizeButton;
    /* access modifiers changed from: private */
    public RLottieImageView muteButton;
    /* access modifiers changed from: private */
    public ValueAnimator muteButtonAnimator;
    /* access modifiers changed from: private */
    public int muteButtonState = 0;
    /* access modifiers changed from: private */
    public TextView[] muteLabel = new TextView[2];
    /* access modifiers changed from: private */
    public ActionBarMenuSubItem noiseItem;
    /* access modifiers changed from: private */
    public int oldAddMemberRow;
    /* access modifiers changed from: private */
    public int oldCount;
    /* access modifiers changed from: private */
    public ArrayList<Long> oldInvited = new ArrayList<>();
    /* access modifiers changed from: private */
    public int oldInvitedEndRow;
    /* access modifiers changed from: private */
    public int oldInvitedStartRow;
    /* access modifiers changed from: private */
    public ArrayList<TLRPC.TL_groupCallParticipant> oldParticipants = new ArrayList<>();
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
    public int oldVideoNotAvailableRow;
    /* access modifiers changed from: private */
    public ArrayList<ChatObject.VideoParticipant> oldVideoParticipants = new ArrayList<>();
    private Runnable onUserLeaveHintListener = new GroupCallActivity$$ExternalSyntheticLambda30(this);
    /* access modifiers changed from: private */
    public ActionBarMenuItem otherItem;
    /* access modifiers changed from: private */
    public Paint paint = new Paint(7);
    /* access modifiers changed from: private */
    public Paint paintTmp = new Paint(7);
    /* access modifiers changed from: private */
    public LaunchActivity parentActivity;
    /* access modifiers changed from: private */
    public ActionBarMenuSubItem permissionItem;
    PinchToZoomHelper pinchToZoomHelper;
    private ActionBarMenuItem pipItem;
    /* access modifiers changed from: private */
    public boolean playingHandAnimation;
    /* access modifiers changed from: private */
    public int popupAnimationIndex = -1;
    /* access modifiers changed from: private */
    public Runnable pressRunnable = new GroupCallActivity$$ExternalSyntheticLambda26(this);
    /* access modifiers changed from: private */
    public boolean pressed;
    /* access modifiers changed from: private */
    public WeavingState prevState;
    PrivateVideoPreviewDialog previewDialog;
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
    /* access modifiers changed from: private */
    public TextView scheduleButtonTextView;
    /* access modifiers changed from: private */
    public float scheduleButtonsScale;
    private boolean scheduleHasFewPeers;
    /* access modifiers changed from: private */
    public TextView scheduleInfoTextView;
    /* access modifiers changed from: private */
    public TLRPC.InputPeer schedulePeer;
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
    public TLRPC.Peer selfPeer;
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
    ObjectAnimator subtitleYAnimator;
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
    public Runnable unmuteRunnable = GroupCallActivity$$ExternalSyntheticLambda42.INSTANCE;
    private Runnable updateCallRecordRunnable;
    /* access modifiers changed from: private */
    public Runnable updateSchedeulRunnable = new Runnable() {
        public void run() {
            int time;
            if (GroupCallActivity.this.scheduleTimeTextView != null && !GroupCallActivity.this.isDismissed()) {
                if (GroupCallActivity.this.call != null) {
                    time = GroupCallActivity.this.call.call.schedule_date;
                } else {
                    time = GroupCallActivity.this.scheduleStartAt;
                }
                if (time != 0) {
                    int diff = time - GroupCallActivity.this.accountInstance.getConnectionsManager().getCurrentTime();
                    if (diff >= 86400) {
                        GroupCallActivity.this.scheduleTimeTextView.setText(LocaleController.formatPluralString("Days", Math.round(((float) diff) / 86400.0f), new Object[0]));
                    } else {
                        GroupCallActivity.this.scheduleTimeTextView.setText(AndroidUtilities.formatFullDuration(Math.abs(diff)));
                        if (diff < 0 && GroupCallActivity.this.scheduleStartInTextView.getTag() == null) {
                            GroupCallActivity.this.scheduleStartInTextView.setTag(1);
                            GroupCallActivity.this.scheduleStartInTextView.setText(LocaleController.getString("VoipChatLateBy", NUM));
                        }
                    }
                    GroupCallActivity.this.scheduleStartAtTextView.setText(LocaleController.formatStartsTime((long) time, 3));
                    AndroidUtilities.runOnUIThread(GroupCallActivity.this.updateSchedeulRunnable, 1000);
                }
            }
        }
    };
    /* access modifiers changed from: private */
    public boolean useBlur;
    /* access modifiers changed from: private */
    public TLObject userSwitchObject;
    LongSparseIntArray visiblePeerIds = new LongSparseIntArray();
    public final ArrayList<ChatObject.VideoParticipant> visibleVideoParticipants = new ArrayList<>();
    /* access modifiers changed from: private */
    public Boolean wasExpandBigSize = true;
    /* access modifiers changed from: private */
    public Boolean wasNotInLayoutFullscreen;

    public /* synthetic */ void onCameraFirstFrameAvailable() {
        VoIPService.StateListener.CC.$default$onCameraFirstFrameAvailable(this);
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

    static /* synthetic */ float access$10516(GroupCallActivity x0, float x1) {
        float f = x0.amplitude + x1;
        x0.amplitude = f;
        return f;
    }

    static /* synthetic */ float access$13116(GroupCallActivity x0, float x1) {
        float f = x0.switchProgress + x1;
        x0.switchProgress = f;
        return f;
    }

    static /* synthetic */ float access$13716(GroupCallActivity x0, float x1) {
        float f = x0.showWavesProgress + x1;
        x0.showWavesProgress = f;
        return f;
    }

    static /* synthetic */ float access$13724(GroupCallActivity x0, float x1) {
        float f = x0.showWavesProgress - x1;
        x0.showWavesProgress = f;
        return f;
    }

    static /* synthetic */ float access$13816(GroupCallActivity x0, float x1) {
        float f = x0.showLightingProgress + x1;
        x0.showLightingProgress = f;
        return f;
    }

    static /* synthetic */ float access$13824(GroupCallActivity x0, float x1) {
        float f = x0.showLightingProgress - x1;
        x0.showLightingProgress = f;
        return f;
    }

    static /* synthetic */ void lambda$new$0() {
        if (VoIPService.getSharedInstance() != null) {
            VoIPService.getSharedInstance().setMicMute(false, true, false);
        }
    }

    /* renamed from: lambda$new$1$org-telegram-ui-GroupCallActivity  reason: not valid java name */
    public /* synthetic */ void m3511lambda$new$1$orgtelegramuiGroupCallActivity() {
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

        public SmallRecordCallDrawable(View parent) {
            this.parentView = parent;
        }

        public int getIntrinsicWidth() {
            return AndroidUtilities.dp(24.0f);
        }

        public int getIntrinsicHeight() {
            return AndroidUtilities.dp(24.0f);
        }

        public void draw(Canvas canvas) {
            int cy;
            int cx = getBounds().centerX();
            int cy2 = getBounds().centerY();
            if (this.parentView instanceof SimpleTextView) {
                cy = cy2 + AndroidUtilities.dp(1.0f);
                cx -= AndroidUtilities.dp(3.0f);
            } else {
                cy = cy2 + AndroidUtilities.dp(2.0f);
            }
            this.paint2.setColor(-1147527);
            this.paint2.setAlpha((int) (this.alpha * 255.0f));
            canvas.drawCircle((float) cx, (float) cy, (float) AndroidUtilities.dp(4.0f), this.paint2);
            long newTime = SystemClock.elapsedRealtime();
            long dt = newTime - this.lastUpdateTime;
            if (dt > 17) {
                dt = 17;
            }
            this.lastUpdateTime = newTime;
            int i = this.state;
            if (i == 0) {
                float f = this.alpha + (((float) dt) / 2000.0f);
                this.alpha = f;
                if (f >= 1.0f) {
                    this.alpha = 1.0f;
                    this.state = 1;
                }
            } else if (i == 1) {
                float f2 = this.alpha - (((float) dt) / 2000.0f);
                this.alpha = f2;
                if (f2 < 0.5f) {
                    this.alpha = 0.5f;
                    this.state = 0;
                }
            }
            this.parentView.invalidate();
        }

        public void setAlpha(int alpha2) {
        }

        public void setColorFilter(ColorFilter colorFilter) {
        }

        public int getOpacity() {
            return -2;
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

        public boolean isRecording() {
            return this.recording;
        }

        public void setRecording(boolean value) {
            this.recording = value;
            this.alpha = 1.0f;
            invalidateSelf();
        }

        public void draw(Canvas canvas) {
            int cx = getBounds().centerX();
            int cy = getBounds().centerY();
            canvas.drawCircle((float) cx, (float) cy, (float) AndroidUtilities.dp(10.0f), this.paint);
            this.paint2.setColor(this.recording ? -1147527 : -1);
            this.paint2.setAlpha((int) (this.alpha * 255.0f));
            canvas.drawCircle((float) cx, (float) cy, (float) AndroidUtilities.dp(5.0f), this.paint2);
            if (this.recording) {
                long newTime = SystemClock.elapsedRealtime();
                long dt = newTime - this.lastUpdateTime;
                if (dt > 17) {
                    dt = 17;
                }
                this.lastUpdateTime = newTime;
                int i = this.state;
                if (i == 0) {
                    float f = this.alpha + (((float) dt) / 2000.0f);
                    this.alpha = f;
                    if (f >= 1.0f) {
                        this.alpha = 1.0f;
                        this.state = 1;
                    }
                } else if (i == 1) {
                    float f2 = this.alpha - (((float) dt) / 2000.0f);
                    this.alpha = f2;
                    if (f2 < 0.5f) {
                        this.alpha = 0.5f;
                        this.state = 0;
                    }
                }
                this.parentView.invalidate();
            }
        }

        public void setAlpha(int alpha2) {
        }

        public void setColorFilter(ColorFilter colorFilter) {
        }

        public int getOpacity() {
            return -2;
        }
    }

    private class VolumeSlider extends FrameLayout {
        private boolean captured;
        private float colorChangeProgress;
        private int currentColor;
        private TLRPC.TL_groupCallParticipant currentParticipant;
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
        private float[] volumeAlphas = new float[3];

        /* JADX WARNING: Illegal instructions before constructor call */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public VolumeSlider(org.telegram.ui.GroupCallActivity r20, android.content.Context r21, org.telegram.tgnet.TLRPC.TL_groupCallParticipant r22) {
            /*
                r19 = this;
                r0 = r19
                r1 = r21
                r2 = r20
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
                r5 = r22
                r0.currentParticipant = r5
                int r6 = org.telegram.messenger.ChatObject.getParticipantVolume(r22)
                float r6 = (float) r6
                r7 = 1184645120(0x469CLASSNAME, float:20000.0)
                float r6 = r6 / r7
                double r6 = (double) r6
                r0.currentProgress = r6
                r6 = 1065353216(0x3var_, float:1.0)
                r0.colorChangeProgress = r6
                r7 = 1094713344(0x41400000, float:12.0)
                int r8 = org.telegram.messenger.AndroidUtilities.dp(r7)
                int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
                r0.setPadding(r8, r4, r7, r4)
                org.telegram.ui.Components.RLottieDrawable r7 = new org.telegram.ui.Components.RLottieDrawable
                r8 = 1103101952(0x41CLASSNAME, float:24.0)
                int r12 = org.telegram.messenger.AndroidUtilities.dp(r8)
                int r13 = org.telegram.messenger.AndroidUtilities.dp(r8)
                r10 = 2131558536(0x7f0d0088, float:1.874239E38)
                java.lang.String r11 = "NUM"
                r14 = 1
                r15 = 0
                r9 = r7
                r9.<init>(r10, r11, r12, r13, r14, r15)
                r0.speakerDrawable = r7
                org.telegram.ui.Components.RLottieImageView r7 = new org.telegram.ui.Components.RLottieImageView
                r7.<init>(r1)
                r0.imageView = r7
                android.widget.ImageView$ScaleType r8 = android.widget.ImageView.ScaleType.CENTER
                r7.setScaleType(r8)
                org.telegram.ui.Components.RLottieImageView r7 = r0.imageView
                org.telegram.ui.Components.RLottieDrawable r8 = r0.speakerDrawable
                r7.setAnimation(r8)
                org.telegram.ui.Components.RLottieImageView r7 = r0.imageView
                double r8 = r0.currentProgress
                r10 = 0
                int r12 = (r8 > r10 ? 1 : (r8 == r10 ? 0 : -1))
                if (r12 != 0) goto L_0x0093
                java.lang.Integer r8 = java.lang.Integer.valueOf(r3)
                goto L_0x0094
            L_0x0093:
                r8 = 0
            L_0x0094:
                r7.setTag(r8)
                org.telegram.ui.Components.RLottieImageView r7 = r0.imageView
                r12 = -2
                r13 = 1109393408(0x42200000, float:40.0)
                boolean r8 = org.telegram.messenger.LocaleController.isRTL
                r9 = 5
                if (r8 == 0) goto L_0x00a3
                r8 = 5
                goto L_0x00a4
            L_0x00a3:
                r8 = 3
            L_0x00a4:
                r14 = r8 | 16
                r15 = 0
                r16 = 0
                r17 = 0
                r18 = 0
                android.widget.FrameLayout$LayoutParams r8 = org.telegram.ui.Components.LayoutHelper.createFrame(r12, r13, r14, r15, r16, r17, r18)
                r0.addView(r7, r8)
                org.telegram.ui.Components.RLottieDrawable r7 = r0.speakerDrawable
                double r12 = r0.currentProgress
                int r8 = (r12 > r10 ? 1 : (r12 == r10 ? 0 : -1))
                if (r8 != 0) goto L_0x00bf
                r8 = 17
                goto L_0x00c1
            L_0x00bf:
                r8 = 34
            L_0x00c1:
                r7.setCustomEndFrame(r8)
                org.telegram.ui.Components.RLottieDrawable r7 = r0.speakerDrawable
                int r8 = r7.getCustomEndFrame()
                int r8 = r8 - r3
                r7.setCurrentFrame(r8, r4, r3)
                android.widget.TextView r7 = new android.widget.TextView
                r7.<init>(r1)
                r0.textView = r7
                r7.setLines(r3)
                android.widget.TextView r7 = r0.textView
                r7.setSingleLine(r3)
                android.widget.TextView r7 = r0.textView
                r7.setGravity(r2)
                android.widget.TextView r7 = r0.textView
                android.text.TextUtils$TruncateAt r8 = android.text.TextUtils.TruncateAt.END
                r7.setEllipsize(r8)
                android.widget.TextView r7 = r0.textView
                java.lang.String r8 = "voipgroup_actionBarItems"
                int r8 = org.telegram.ui.ActionBar.Theme.getColor(r8)
                r7.setTextColor(r8)
                android.widget.TextView r7 = r0.textView
                r8 = 1098907648(0x41800000, float:16.0)
                r7.setTextSize(r3, r8)
                org.telegram.tgnet.TLRPC$TL_groupCallParticipant r7 = r0.currentParticipant
                int r7 = org.telegram.messenger.ChatObject.getParticipantVolume(r7)
                double r7 = (double) r7
                r12 = 4636737291354636288(0xNUM, double:100.0)
                java.lang.Double.isNaN(r7)
                double r7 = r7 / r12
                android.widget.TextView r14 = r0.textView
                java.util.Locale r15 = java.util.Locale.US
                java.lang.Object[] r2 = new java.lang.Object[r3]
                int r16 = (r7 > r10 ? 1 : (r7 == r10 ? 0 : -1))
                if (r16 <= 0) goto L_0x0118
                r10 = 4607182418800017408(0x3ffNUM, double:1.0)
                double r10 = java.lang.Math.max(r7, r10)
            L_0x0118:
                int r10 = (int) r10
                java.lang.Integer r10 = java.lang.Integer.valueOf(r10)
                r2[r4] = r10
                java.lang.String r10 = "%d%%"
                java.lang.String r2 = java.lang.String.format(r15, r10, r2)
                r14.setText(r2)
                android.widget.TextView r2 = r0.textView
                boolean r10 = org.telegram.messenger.LocaleController.isRTL
                r11 = 1110179840(0x422CLASSNAME, float:43.0)
                if (r10 == 0) goto L_0x0132
                r10 = 0
                goto L_0x0136
            L_0x0132:
                int r10 = org.telegram.messenger.AndroidUtilities.dp(r11)
            L_0x0136:
                boolean r14 = org.telegram.messenger.LocaleController.isRTL
                if (r14 == 0) goto L_0x013f
                int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
                goto L_0x0140
            L_0x013f:
                r11 = 0
            L_0x0140:
                r2.setPadding(r10, r4, r11, r4)
                android.widget.TextView r2 = r0.textView
                boolean r4 = org.telegram.messenger.LocaleController.isRTL
                if (r4 == 0) goto L_0x014a
                goto L_0x014b
            L_0x014a:
                r9 = 3
            L_0x014b:
                r4 = r9 | 16
                r9 = -2
                android.widget.FrameLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r9, (int) r9, (int) r4)
                r0.addView(r2, r4)
                android.graphics.Paint r2 = r0.paint2
                android.graphics.Paint$Style r4 = android.graphics.Paint.Style.STROKE
                r2.setStyle(r4)
                android.graphics.Paint r2 = r0.paint2
                r4 = 1069547520(0x3fCLASSNAME, float:1.5)
                int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
                float r4 = (float) r4
                r2.setStrokeWidth(r4)
                android.graphics.Paint r2 = r0.paint2
                android.graphics.Paint$Cap r4 = android.graphics.Paint.Cap.ROUND
                r2.setStrokeCap(r4)
                android.graphics.Paint r2 = r0.paint2
                r4 = -1
                r2.setColor(r4)
                org.telegram.tgnet.TLRPC$TL_groupCallParticipant r2 = r0.currentParticipant
                int r2 = org.telegram.messenger.ChatObject.getParticipantVolume(r2)
                double r9 = (double) r2
                java.lang.Double.isNaN(r9)
                double r9 = r9 / r12
                int r2 = (int) r9
                r4 = 0
            L_0x0182:
                float[] r9 = r0.volumeAlphas
                int r10 = r9.length
                if (r4 >= r10) goto L_0x019d
                if (r4 != 0) goto L_0x018b
                r10 = 0
                goto L_0x0192
            L_0x018b:
                if (r4 != r3) goto L_0x0190
                r10 = 50
                goto L_0x0192
            L_0x0190:
                r10 = 150(0x96, float:2.1E-43)
            L_0x0192:
                if (r2 <= r10) goto L_0x0197
                r9[r4] = r6
                goto L_0x019a
            L_0x0197:
                r11 = 0
                r9[r4] = r11
            L_0x019a:
                int r4 = r4 + 1
                goto L_0x0182
            L_0x019d:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.GroupCallActivity.VolumeSlider.<init>(org.telegram.ui.GroupCallActivity, android.content.Context, org.telegram.tgnet.TLRPC$TL_groupCallParticipant):void");
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f), NUM));
            double size = (double) View.MeasureSpec.getSize(widthMeasureSpec);
            double d = this.currentProgress;
            Double.isNaN(size);
            this.thumbX = (int) (size * d);
        }

        public boolean onInterceptTouchEvent(MotionEvent ev) {
            return onTouch(ev);
        }

        public boolean onTouchEvent(MotionEvent event) {
            return onTouch(event);
        }

        /* access modifiers changed from: package-private */
        public boolean onTouch(MotionEvent ev) {
            if (ev.getAction() == 0) {
                this.sx = ev.getX();
                this.sy = ev.getY();
                return true;
            }
            if (ev.getAction() == 1 || ev.getAction() == 3) {
                this.captured = false;
                if (ev.getAction() == 1) {
                    if (Math.abs(ev.getY() - this.sy) < ((float) ViewConfiguration.get(getContext()).getScaledTouchSlop())) {
                        int x = (int) ev.getX();
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
                    if (ev.getAction() == 1) {
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
            } else if (ev.getAction() == 2) {
                if (!this.captured) {
                    ViewConfiguration vc = ViewConfiguration.get(getContext());
                    if (Math.abs(ev.getY() - this.sy) <= ((float) vc.getScaledTouchSlop()) && Math.abs(ev.getX() - this.sx) > ((float) vc.getScaledTouchSlop())) {
                        this.captured = true;
                        getParent().requestDisallowInterceptTouchEvent(true);
                        if (ev.getY() >= 0.0f && ev.getY() <= ((float) getMeasuredHeight())) {
                            int x2 = (int) ev.getX();
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
                    int x3 = (int) ev.getX();
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

        private void onSeekBarDrag(double progress, boolean finalMove) {
            TLObject object;
            double d = progress;
            if (VoIPService.getSharedInstance() != null) {
                this.currentProgress = d;
                this.currentParticipant.volume = (int) (20000.0d * d);
                int i = 0;
                this.currentParticipant.volume_by_admin = false;
                this.currentParticipant.flags |= 128;
                double participantVolume = (double) ChatObject.getParticipantVolume(this.currentParticipant);
                Double.isNaN(participantVolume);
                double vol = participantVolume / 100.0d;
                TextView textView2 = this.textView;
                Locale locale = Locale.US;
                Object[] objArr = new Object[1];
                objArr[0] = Integer.valueOf((int) (vol > 0.0d ? Math.max(vol, 1.0d) : 0.0d));
                textView2.setText(String.format(locale, "%d%%", objArr));
                VoIPService sharedInstance = VoIPService.getSharedInstance();
                TLRPC.TL_groupCallParticipant tL_groupCallParticipant = this.currentParticipant;
                sharedInstance.setParticipantVolume(tL_groupCallParticipant, tL_groupCallParticipant.volume);
                int newTag = null;
                if (finalMove) {
                    long id = MessageObject.getPeerId(this.currentParticipant.peer);
                    if (id > 0) {
                        object = this.this$0.accountInstance.getMessagesController().getUser(Long.valueOf(id));
                    } else {
                        object = this.this$0.accountInstance.getMessagesController().getChat(Long.valueOf(-id));
                    }
                    if (this.currentParticipant.volume == 0) {
                        if (this.this$0.scrimPopupWindow != null) {
                            this.this$0.scrimPopupWindow.dismiss();
                            ActionBarPopupWindow unused = this.this$0.scrimPopupWindow = null;
                        }
                        this.this$0.dismissAvatarPreview(true);
                        GroupCallActivity groupCallActivity = this.this$0;
                        groupCallActivity.processSelectedOption(this.currentParticipant, id, ChatObject.canManageCalls(groupCallActivity.currentChat) ? 0 : 5);
                    } else {
                        VoIPService.getSharedInstance().editCallMember(object, (Boolean) null, (Boolean) null, Integer.valueOf(this.currentParticipant.volume), (Boolean) null, (Runnable) null);
                    }
                }
                if (this.currentProgress == 0.0d) {
                    newTag = 1;
                }
                if ((this.imageView.getTag() == null && newTag != null) || (this.imageView.getTag() != null && newTag == null)) {
                    this.speakerDrawable.setCustomEndFrame(this.currentProgress == 0.0d ? 17 : 34);
                    RLottieDrawable rLottieDrawable = this.speakerDrawable;
                    if (this.currentProgress != 0.0d) {
                        i = 17;
                    }
                    rLottieDrawable.setCurrentFrame(i);
                    this.speakerDrawable.start();
                    this.imageView.setTag(newTag);
                }
            }
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            int color;
            int p;
            float rad;
            int prevColor = this.currentColor;
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
            if (prevColor == 0) {
                color = this.currentColor;
                this.colorChangeProgress = 1.0f;
            } else {
                color = AndroidUtilities.getOffsetColor(this.oldColor, prevColor, this.colorChangeProgress, 1.0f);
                if (prevColor != this.currentColor) {
                    this.colorChangeProgress = 0.0f;
                    this.oldColor = color;
                }
            }
            this.paint.setColor(color);
            long newTime = SystemClock.elapsedRealtime();
            long dt = newTime - this.lastUpdateTime;
            if (dt > 17) {
                dt = 17;
            }
            this.lastUpdateTime = newTime;
            float f = this.colorChangeProgress;
            if (f < 1.0f) {
                float f2 = f + (((float) dt) / 200.0f);
                this.colorChangeProgress = f2;
                if (f2 > 1.0f) {
                    this.colorChangeProgress = 1.0f;
                } else {
                    invalidate();
                }
            }
            this.path.reset();
            float[] fArr = this.radii;
            float f3 = 6.0f;
            float dp = (float) AndroidUtilities.dp(6.0f);
            fArr[7] = dp;
            fArr[6] = dp;
            fArr[1] = dp;
            fArr[0] = dp;
            float rad2 = this.thumbX < AndroidUtilities.dp(12.0f) ? Math.max(0.0f, ((float) (this.thumbX - AndroidUtilities.dp(6.0f))) / ((float) AndroidUtilities.dp(6.0f))) : 1.0f;
            float[] fArr2 = this.radii;
            float dp2 = ((float) AndroidUtilities.dp(6.0f)) * rad2;
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
            int percent = (int) (participantVolume / 100.0d);
            int cx = this.imageView.getLeft() + (this.imageView.getMeasuredWidth() / 2) + AndroidUtilities.dp(5.0f);
            int cy = this.imageView.getTop() + (this.imageView.getMeasuredHeight() / 2);
            int a = 0;
            while (a < this.volumeAlphas.length) {
                if (a == 0) {
                    p = 0;
                    rad = (float) AndroidUtilities.dp(f3);
                } else if (a == 1) {
                    p = 50;
                    rad = (float) AndroidUtilities.dp(10.0f);
                } else {
                    p = 150;
                    rad = (float) AndroidUtilities.dp(14.0f);
                }
                float[] fArr3 = this.volumeAlphas;
                float offset = ((float) AndroidUtilities.dp(2.0f)) * (1.0f - fArr3[a]);
                int prevColor2 = prevColor;
                this.paint2.setAlpha((int) (fArr3[a] * 255.0f));
                int color2 = color;
                long newTime2 = newTime;
                this.rect.set((((float) cx) - rad) + offset, (((float) cy) - rad) + offset, (((float) cx) + rad) - offset, (((float) cy) + rad) - offset);
                canvas.drawArc(this.rect, -50.0f, 100.0f, false, this.paint2);
                if (percent > p) {
                    float[] fArr4 = this.volumeAlphas;
                    if (fArr4[a] < 1.0f) {
                        fArr4[a] = fArr4[a] + (((float) dt) / 180.0f);
                        if (fArr4[a] > 1.0f) {
                            fArr4[a] = 1.0f;
                        }
                        invalidate();
                    }
                } else {
                    float[] fArr5 = this.volumeAlphas;
                    if (fArr5[a] > 0.0f) {
                        fArr5[a] = fArr5[a] - (((float) dt) / 180.0f);
                        if (fArr5[a] < 0.0f) {
                            fArr5[a] = 0.0f;
                        }
                        invalidate();
                    }
                }
                a++;
                prevColor = prevColor2;
                color = color2;
                newTime = newTime2;
                f3 = 6.0f;
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

        public WeavingState(int state) {
            this.currentState = state;
        }

        public void update(int top, int left, int size, long dt, float amplitude) {
            float s;
            if (this.shader != null) {
                float f = this.duration;
                if (f == 0.0f || this.time >= f) {
                    this.duration = (float) (Utilities.random.nextInt(200) + 1500);
                    this.time = 0.0f;
                    if (this.targetX == -1.0f) {
                        setTarget();
                    }
                    this.startX = this.targetX;
                    this.startY = this.targetY;
                    setTarget();
                }
                float f2 = this.time + (((float) dt) * (BlobDrawable.GRADIENT_SPEED_MIN + 0.5f)) + (((float) dt) * BlobDrawable.GRADIENT_SPEED_MAX * 2.0f * amplitude);
                this.time = f2;
                float f3 = this.duration;
                if (f2 > f3) {
                    this.time = f3;
                }
                float interpolation = CubicBezierInterpolator.EASE_OUT.getInterpolation(this.time / this.duration);
                float f4 = this.startX;
                float x = (((float) left) + (((float) size) * (f4 + ((this.targetX - f4) * interpolation)))) - 200.0f;
                float f5 = this.startY;
                float y = (((float) top) + (((float) size) * (f5 + ((this.targetY - f5) * interpolation)))) - 200.0f;
                if (GroupCallActivity.isGradientState(this.currentState)) {
                    s = 1.0f;
                } else {
                    s = this.currentState == 1 ? 4.0f : 2.5f;
                }
                float scale = (((float) AndroidUtilities.dp(122.0f)) / 400.0f) * s;
                this.matrix.reset();
                this.matrix.postTranslate(x, y);
                this.matrix.postScale(scale, scale, x + 200.0f, 200.0f + y);
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

    public static boolean isGradientState(int state) {
        return !(VoIPService.getSharedInstance() == null || VoIPService.getSharedInstance().groupCall == null || !VoIPService.getSharedInstance().groupCall.call.rtmp_stream) || state == 2 || state == 4 || state == 5 || state == 6 || state == 7;
    }

    private static class LabeledButton extends FrameLayout {
        private ImageView imageView;
        private TextView textView;

        public LabeledButton(Context context, String text, int resId, int color) {
            super(context);
            this.imageView = new ImageView(context);
            if (Build.VERSION.SDK_INT >= 21) {
                this.imageView.setBackground(Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(50.0f), color, NUM));
            } else {
                this.imageView.setBackground(Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(50.0f), color, color));
            }
            this.imageView.setImageResource(resId);
            this.imageView.setScaleType(ImageView.ScaleType.CENTER);
            addView(this.imageView, LayoutHelper.createFrame(50, 50, 49));
            TextView textView2 = new TextView(context);
            this.textView = textView2;
            textView2.setTextColor(Theme.getColor("voipgroup_actionBarItems"));
            this.textView.setTextSize(1, 12.0f);
            this.textView.setGravity(1);
            this.textView.setText(text);
            addView(this.textView, LayoutHelper.createFrame(-2, -2.0f, 49, 0.0f, 55.0f, 0.0f, 0.0f));
        }

        public void setColor(int color) {
            Theme.setSelectorDrawableColor(this.imageView.getBackground(), color, false);
            if (Build.VERSION.SDK_INT < 21) {
                Theme.setSelectorDrawableColor(this.imageView.getBackground(), color, true);
            }
            this.imageView.invalidate();
        }
    }

    private void prepareBlurBitmap() {
        if (this.blurredView != null) {
            int w = (int) (((float) (this.containerView.getMeasuredWidth() - (this.backgroundPaddingLeft * 2))) / 6.0f);
            int h = (int) (((float) (this.containerView.getMeasuredHeight() - AndroidUtilities.statusBarHeight)) / 6.0f);
            Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
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
            Utilities.stackBlurBitmap(bitmap, Math.max(7, Math.max(w, h) / 180));
            this.blurredView.setBackground(new BitmapDrawable(bitmap));
            this.blurredView.setAlpha(0.0f);
            this.blurredView.setVisibility(0);
        }
    }

    /* access modifiers changed from: protected */
    public boolean canDismissWithSwipe() {
        return false;
    }

    /* access modifiers changed from: protected */
    public boolean onCustomOpenAnimation() {
        groupCallUiVisible = true;
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.groupCallVisibilityChanged, new Object[0]);
        GroupCallPip.updateVisibility(getContext());
        return super.onCustomOpenAnimation();
    }

    public void dismiss() {
        this.parentActivity.removeOnUserLeaveHintListener(this.onUserLeaveHintListener);
        this.parentActivity.setRequestedOrientation(-1);
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

    public void didReceivedNotification(int id, int account, Object... args) {
        TLRPC.TL_groupCallParticipant participant;
        String str;
        int i;
        TLRPC.TL_groupCallParticipant participant2;
        String str2;
        int i2;
        String error;
        int i3;
        int i4;
        int i5 = id;
        Object[] objArr = args;
        boolean raisedHand = false;
        if (i5 == NotificationCenter.groupCallUpdated) {
            Long callId = (Long) objArr[1];
            ChatObject.Call call2 = this.call;
            if (call2 != null && call2.call.id == callId.longValue()) {
                if (this.call.call instanceof TLRPC.TL_groupCallDiscarded) {
                    dismiss();
                    return;
                }
                if (this.creatingServiceTime == 0 && (((i4 = this.muteButtonState) == 7 || i4 == 5 || i4 == 6) && !this.call.isScheduled())) {
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
                    } catch (Throwable e) {
                        FileLog.e(e);
                    }
                    this.creatingServiceTime = SystemClock.elapsedRealtime();
                    AndroidUtilities.runOnUIThread(new GroupCallActivity$$ExternalSyntheticLambda25(this), 3000);
                }
                if (!this.callInitied && VoIPService.getSharedInstance() != null) {
                    this.call.addSelfDummyParticipant(false);
                    initCreatedGroupCall();
                    VoIPService.getSharedInstance().playConnectedSound();
                }
                updateItems();
                int N = this.listView.getChildCount();
                for (int a = 0; a < N; a++) {
                    View child = this.listView.getChildAt(a);
                    if (child instanceof GroupCallUserCell) {
                        ((GroupCallUserCell) child).applyParticipantChanges(true);
                    }
                }
                if (this.scrimView != null) {
                    this.delayedGroupCallUpdated = true;
                } else {
                    applyCallParticipantUpdates(true);
                }
                updateSubtitle();
                boolean selfUpdate = ((Boolean) objArr[2]).booleanValue();
                if (this.muteButtonState == 4) {
                    raisedHand = true;
                }
                updateState(true, selfUpdate);
                updateTitle(true);
                if (raisedHand && ((i3 = this.muteButtonState) == 1 || i3 == 0)) {
                    getUndoView().showWithAction(0, 38, (Runnable) null);
                    if (VoIPService.getSharedInstance() != null) {
                        VoIPService.getSharedInstance().playAllowTalkSound();
                    }
                }
                if (objArr.length >= 4) {
                    long justJoinedId = ((Long) objArr[3]).longValue();
                    if (justJoinedId != 0 && !isRtmpStream()) {
                        boolean hasInDialogs = false;
                        try {
                            ArrayList<TLRPC.Dialog> dialogs = this.accountInstance.getMessagesController().getAllDialogs();
                            if (dialogs != null) {
                                Iterator<TLRPC.Dialog> it = dialogs.iterator();
                                while (true) {
                                    if (it.hasNext()) {
                                        if (it.next().id == justJoinedId) {
                                            hasInDialogs = true;
                                            break;
                                        }
                                    } else {
                                        break;
                                    }
                                }
                            }
                        } catch (Exception e2) {
                        }
                        if (DialogObject.isUserDialog(justJoinedId)) {
                            TLRPC.User user = this.accountInstance.getMessagesController().getUser(Long.valueOf(justJoinedId));
                            if (user == null) {
                                return;
                            }
                            if (this.call.call.participants_count < 250 || UserObject.isContact(user) || user.verified || hasInDialogs) {
                                getUndoView().showWithAction(0, 44, (Object) user, (Object) this.currentChat, (Runnable) null, (Runnable) null);
                                return;
                            }
                            return;
                        }
                        TLRPC.Chat chat = this.accountInstance.getMessagesController().getChat(Long.valueOf(-justJoinedId));
                        if (chat == null) {
                            return;
                        }
                        if (this.call.call.participants_count < 250 || !ChatObject.isNotInChat(chat) || chat.verified || hasInDialogs) {
                            getUndoView().showWithAction(0, 44, (Object) chat, (Object) this.currentChat, (Runnable) null, (Runnable) null);
                        }
                    }
                }
            }
        } else if (i5 == NotificationCenter.groupCallSpeakingUsersUpdated) {
            if (this.renderersContainer.inFullscreenMode && this.call != null) {
                boolean autoPinEnabled = this.renderersContainer.autoPinEnabled();
                if (this.call != null && this.renderersContainer.inFullscreenMode && this.renderersContainer.fullscreenParticipant != null && this.call.participants.get(MessageObject.getPeerId(this.renderersContainer.fullscreenParticipant.participant.peer)) == null) {
                    autoPinEnabled = true;
                }
                if (autoPinEnabled) {
                    ChatObject.VideoParticipant currentSpeaker = null;
                    for (int i6 = 0; i6 < this.visibleVideoParticipants.size(); i6++) {
                        ChatObject.VideoParticipant participant3 = this.visibleVideoParticipants.get(i6);
                        if ((this.call.currentSpeakingPeers.get(MessageObject.getPeerId(participant3.participant.peer), null) != null) && !participant3.participant.muted_by_you && this.renderersContainer.fullscreenPeerId != MessageObject.getPeerId(participant3.participant.peer)) {
                            currentSpeaker = participant3;
                        }
                    }
                    if (currentSpeaker != null) {
                        fullscreenFor(currentSpeaker);
                    }
                }
            }
            this.renderersContainer.setVisibleParticipant(true);
            updateSubtitle();
        } else if (i5 == NotificationCenter.webRtcMicAmplitudeEvent) {
            setMicAmplitude(((Float) objArr[0]).floatValue());
        } else if (i5 == NotificationCenter.needShowAlert) {
            if (((Integer) objArr[0]).intValue() == 6) {
                String text = (String) objArr[1];
                if ("GROUPCALL_PARTICIPANTS_TOO_MUCH".equals(text)) {
                    if (ChatObject.isChannelOrGiga(this.currentChat)) {
                        error = LocaleController.getString("VoipChannelTooMuch", NUM);
                    } else {
                        error = LocaleController.getString("VoipGroupTooMuch", NUM);
                    }
                } else if (!"ANONYMOUS_CALLS_DISABLED".equals(text) && !"GROUPCALL_ANONYMOUS_FORBIDDEN".equals(text)) {
                    error = LocaleController.getString("ErrorOccurred", NUM) + "\n" + text;
                } else if (ChatObject.isChannelOrGiga(this.currentChat)) {
                    error = LocaleController.getString("VoipChannelJoinAnonymousAdmin", NUM);
                } else {
                    error = LocaleController.getString("VoipGroupJoinAnonymousAdmin", NUM);
                }
                AlertDialog.Builder builder = AlertsCreator.createSimpleAlert(getContext(), LocaleController.getString("VoipGroupVoiceChat", NUM), error);
                builder.setOnDismissListener(new GroupCallActivity$$ExternalSyntheticLambda65(this));
                try {
                    builder.show();
                } catch (Exception e3) {
                    FileLog.e((Throwable) e3);
                }
            }
        } else if (i5 == NotificationCenter.didEndCall) {
            if (VoIPService.getSharedInstance() == null) {
                dismiss();
            }
        } else if (i5 == NotificationCenter.chatInfoDidLoad) {
            TLRPC.ChatFull chatFull = (TLRPC.ChatFull) objArr[0];
            if (chatFull.id == this.currentChat.id) {
                updateItems();
                updateState(isShowing(), false);
            }
            long selfId = MessageObject.getPeerId(this.selfPeer);
            if (this.call != null && chatFull.id == (-selfId) && (participant2 = this.call.participants.get(selfId)) != null) {
                participant2.about = chatFull.about;
                applyCallParticipantUpdates(true);
                AndroidUtilities.updateVisibleRows(this.listView);
                if (this.currentOptionsLayout != null) {
                    for (int i7 = 0; i7 < this.currentOptionsLayout.getChildCount(); i7++) {
                        View child2 = this.currentOptionsLayout.getChildAt(i7);
                        if ((child2 instanceof ActionBarMenuSubItem) && child2.getTag() != null && ((Integer) child2.getTag()).intValue() == 10) {
                            ActionBarMenuSubItem actionBarMenuSubItem = (ActionBarMenuSubItem) child2;
                            if (TextUtils.isEmpty(participant2.about)) {
                                i2 = NUM;
                                str2 = "VoipAddDescription";
                            } else {
                                i2 = NUM;
                                str2 = "VoipEditDescription";
                            }
                            actionBarMenuSubItem.setTextAndIcon(LocaleController.getString(str2, i2), TextUtils.isEmpty(participant2.about) ? NUM : NUM);
                        }
                    }
                }
            }
        } else if (i5 == NotificationCenter.didLoadChatAdmins) {
            if (((Long) objArr[0]).longValue() == this.currentChat.id) {
                updateItems();
                updateState(isShowing(), false);
            }
        } else if (i5 == NotificationCenter.applyGroupCallVisibleParticipants) {
            int count = this.listView.getChildCount();
            long time = ((Long) objArr[0]).longValue();
            for (int a2 = 0; a2 < count; a2++) {
                RecyclerView.ViewHolder holder = this.listView.findContainingViewHolder(this.listView.getChildAt(a2));
                if (holder != null && (holder.itemView instanceof GroupCallUserCell)) {
                    ((GroupCallUserCell) holder.itemView).getParticipant().lastVisibleDate = time;
                }
            }
        } else if (i5 == NotificationCenter.userInfoDidLoad) {
            Long uid = (Long) objArr[0];
            long selfId2 = MessageObject.getPeerId(this.selfPeer);
            if (this.call != null && selfId2 == uid.longValue() && (participant = this.call.participants.get(selfId2)) != null) {
                participant.about = ((TLRPC.UserFull) objArr[1]).about;
                applyCallParticipantUpdates(true);
                AndroidUtilities.updateVisibleRows(this.listView);
                if (this.currentOptionsLayout != null) {
                    for (int i8 = 0; i8 < this.currentOptionsLayout.getChildCount(); i8++) {
                        View child3 = this.currentOptionsLayout.getChildAt(i8);
                        if ((child3 instanceof ActionBarMenuSubItem) && child3.getTag() != null && ((Integer) child3.getTag()).intValue() == 10) {
                            ActionBarMenuSubItem actionBarMenuSubItem2 = (ActionBarMenuSubItem) child3;
                            if (TextUtils.isEmpty(participant.about)) {
                                i = NUM;
                                str = "VoipAddBio";
                            } else {
                                i = NUM;
                                str = "VoipEditBio";
                            }
                            actionBarMenuSubItem2.setTextAndIcon(LocaleController.getString(str, i), TextUtils.isEmpty(participant.about) ? NUM : NUM);
                        }
                    }
                }
            }
        } else if (i5 == NotificationCenter.mainUserInfoChanged) {
            applyCallParticipantUpdates(true);
            AndroidUtilities.updateVisibleRows(this.listView);
        } else if (i5 == NotificationCenter.updateInterfaces) {
            if ((MessagesController.UPDATE_MASK_CHAT_NAME & ((Integer) objArr[0]).intValue()) != 0) {
                applyCallParticipantUpdates(true);
                AndroidUtilities.updateVisibleRows(this.listView);
            }
        } else if (i5 == NotificationCenter.groupCallScreencastStateChanged) {
            PrivateVideoPreviewDialog privateVideoPreviewDialog = this.previewDialog;
            if (privateVideoPreviewDialog != null) {
                privateVideoPreviewDialog.dismiss(true, true);
            }
            updateItems();
        }
    }

    /* renamed from: lambda$didReceivedNotification$2$org-telegram-ui-GroupCallActivity  reason: not valid java name */
    public /* synthetic */ void m3499xf4b9f1ce() {
        if (isStillConnecting()) {
            updateState(true, false);
        }
    }

    /* renamed from: lambda$didReceivedNotification$3$org-telegram-ui-GroupCallActivity  reason: not valid java name */
    public /* synthetic */ void m3500x5b92b18f(DialogInterface dialog) {
        dismiss();
    }

    private void setMicAmplitude(float amplitude2) {
        TLRPC.TL_groupCallParticipant participant;
        RecyclerView.ViewHolder holder;
        if (VoIPService.getSharedInstance() == null || VoIPService.getSharedInstance().isMicMute()) {
            amplitude2 = 0.0f;
        }
        setAmplitude((double) (4000.0f * amplitude2));
        ChatObject.Call call2 = this.call;
        if (call2 != null && this.listView != null && (participant = call2.participants.get(MessageObject.getPeerId(this.selfPeer))) != null) {
            if (!this.renderersContainer.inFullscreenMode) {
                int idx = (this.delayedGroupCallUpdated ? this.oldParticipants : this.call.visibleParticipants).indexOf(participant);
                if (idx >= 0 && (holder = this.listView.findViewHolderForAdapterPosition(this.listAdapter.usersStartRow + idx)) != null && (holder.itemView instanceof GroupCallUserCell)) {
                    ((GroupCallUserCell) holder.itemView).setAmplitude((double) (amplitude2 * 15.0f));
                    if (holder.itemView == this.scrimView && !this.contentFullyOverlayed) {
                        this.containerView.invalidate();
                    }
                }
            } else {
                for (int i = 0; i < this.fullscreenUsersListView.getChildCount(); i++) {
                    GroupCallFullscreenAdapter.GroupCallUserCell cell = (GroupCallFullscreenAdapter.GroupCallUserCell) this.fullscreenUsersListView.getChildAt(i);
                    if (MessageObject.getPeerId(cell.getParticipant().peer) == MessageObject.getPeerId(participant.peer)) {
                        cell.setAmplitude((double) (amplitude2 * 15.0f));
                    }
                }
            }
            this.renderersContainer.setAmplitude(participant, 15.0f * amplitude2);
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:106:0x0278  */
    /* JADX WARNING: Removed duplicated region for block: B:115:0x01ae A[EDGE_INSN: B:115:0x01ae->B:69:0x01ae ?: BREAK  , SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:122:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:48:0x0122  */
    /* JADX WARNING: Removed duplicated region for block: B:52:0x0166  */
    /* JADX WARNING: Removed duplicated region for block: B:77:0x01dd  */
    /* JADX WARNING: Removed duplicated region for block: B:81:0x01ed  */
    /* JADX WARNING: Removed duplicated region for block: B:84:0x0201  */
    /* JADX WARNING: Removed duplicated region for block: B:87:0x020a  */
    /* JADX WARNING: Removed duplicated region for block: B:90:0x021a  */
    /* JADX WARNING: Removed duplicated region for block: B:94:0x0234 A[LOOP:2: B:92:0x022c->B:94:0x0234, LOOP_END] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void applyCallParticipantUpdates(boolean r25) {
        /*
            r24 = this;
            r11 = r24
            org.telegram.ui.Components.voip.GroupCallRenderersContainer r0 = r11.renderersContainer
            if (r0 == 0) goto L_0x0284
            org.telegram.ui.Components.RecyclerListView r1 = r11.listView
            if (r1 == 0) goto L_0x0284
            org.telegram.messenger.ChatObject$Call r1 = r11.call
            if (r1 == 0) goto L_0x0284
            boolean r1 = r11.delayedGroupCallUpdated
            if (r1 == 0) goto L_0x0014
            goto L_0x0284
        L_0x0014:
            boolean r0 = r0.inFullscreenMode
            r12 = 1
            if (r0 == 0) goto L_0x001e
            org.telegram.ui.Components.voip.GroupCallRenderersContainer r0 = r11.renderersContainer
            r0.setVisibleParticipant(r12)
        L_0x001e:
            org.telegram.messenger.ChatObject$Call r0 = r11.call
            org.telegram.tgnet.TLRPC$Peer r0 = r0.selfPeer
            long r13 = org.telegram.messenger.MessageObject.getPeerId(r0)
            org.telegram.tgnet.TLRPC$Peer r0 = r11.selfPeer
            long r15 = org.telegram.messenger.MessageObject.getPeerId(r0)
            int r0 = (r13 > r15 ? 1 : (r13 == r15 ? 0 : -1))
            if (r0 == 0) goto L_0x0040
            org.telegram.messenger.ChatObject$Call r0 = r11.call
            androidx.collection.LongSparseArray<org.telegram.tgnet.TLRPC$TL_groupCallParticipant> r0 = r0.participants
            java.lang.Object r0 = r0.get(r13)
            if (r0 == 0) goto L_0x0040
            org.telegram.messenger.ChatObject$Call r0 = r11.call
            org.telegram.tgnet.TLRPC$Peer r0 = r0.selfPeer
            r11.selfPeer = r0
        L_0x0040:
            org.telegram.ui.Components.RecyclerListView r0 = r11.listView
            int r10 = r0.getChildCount()
            r0 = 0
            r1 = 0
            r2 = 2147483647(0x7fffffff, float:NaN)
            r3 = 0
            r17 = r0
            r9 = r1
            r8 = r2
        L_0x0050:
            if (r3 >= r10) goto L_0x0085
            org.telegram.ui.Components.RecyclerListView r0 = r11.listView
            android.view.View r0 = r0.getChildAt(r3)
            org.telegram.ui.Components.RecyclerListView r1 = r11.listView
            androidx.recyclerview.widget.RecyclerView$ViewHolder r1 = r1.findContainingViewHolder(r0)
            if (r1 == 0) goto L_0x0082
            int r2 = r1.getAdapterPosition()
            r4 = -1
            if (r2 == r4) goto L_0x0082
            int r2 = r1.getLayoutPosition()
            if (r2 == r4) goto L_0x0082
            if (r17 == 0) goto L_0x0075
            int r2 = r0.getTop()
            if (r2 >= r8) goto L_0x0082
        L_0x0075:
            r2 = r0
            int r4 = r1.getLayoutPosition()
            int r5 = r0.getTop()
            r17 = r2
            r9 = r4
            r8 = r5
        L_0x0082:
            int r3 = r3 + 1
            goto L_0x0050
        L_0x0085:
            r24.updateVideoParticipantList()
            org.telegram.ui.Components.RecyclerListView r0 = r11.listView
            androidx.recyclerview.widget.RecyclerView$ItemAnimator r0 = r0.getItemAnimator()
            r7 = 0
            if (r0 == 0) goto L_0x0099
            if (r25 != 0) goto L_0x0099
            org.telegram.ui.Components.RecyclerListView r0 = r11.listView
            r0.setItemAnimator(r7)
            goto L_0x00aa
        L_0x0099:
            org.telegram.ui.Components.RecyclerListView r0 = r11.listView
            androidx.recyclerview.widget.RecyclerView$ItemAnimator r0 = r0.getItemAnimator()
            if (r0 != 0) goto L_0x00aa
            if (r25 == 0) goto L_0x00aa
            org.telegram.ui.Components.RecyclerListView r0 = r11.listView
            org.telegram.ui.GroupCallActivity$GroupCallItemAnimator r1 = r11.itemAnimator
            r0.setItemAnimator(r1)
        L_0x00aa:
            org.telegram.ui.GroupCallActivity$UpdateCallback r0 = new org.telegram.ui.GroupCallActivity$UpdateCallback     // Catch:{ Exception -> 0x010c }
            org.telegram.ui.GroupCallActivity$ListAdapter r1 = r11.listAdapter     // Catch:{ Exception -> 0x010c }
            r0.<init>(r1)     // Catch:{ Exception -> 0x010c }
            org.telegram.ui.GroupCallActivity$ListAdapter r1 = r11.listAdapter     // Catch:{ Exception -> 0x010c }
            int r2 = r1.addMemberRow     // Catch:{ Exception -> 0x010c }
            org.telegram.ui.GroupCallActivity$ListAdapter r1 = r11.listAdapter     // Catch:{ Exception -> 0x010c }
            int r3 = r1.usersStartRow     // Catch:{ Exception -> 0x010c }
            org.telegram.ui.GroupCallActivity$ListAdapter r1 = r11.listAdapter     // Catch:{ Exception -> 0x010c }
            int r4 = r1.usersEndRow     // Catch:{ Exception -> 0x010c }
            org.telegram.ui.GroupCallActivity$ListAdapter r1 = r11.listAdapter     // Catch:{ Exception -> 0x010c }
            int r5 = r1.invitedStartRow     // Catch:{ Exception -> 0x010c }
            org.telegram.ui.GroupCallActivity$ListAdapter r1 = r11.listAdapter     // Catch:{ Exception -> 0x010c }
            int r6 = r1.invitedEndRow     // Catch:{ Exception -> 0x010c }
            org.telegram.ui.GroupCallActivity$ListAdapter r1 = r11.listAdapter     // Catch:{ Exception -> 0x010c }
            int r18 = r1.usersVideoGridStartRow     // Catch:{ Exception -> 0x010c }
            org.telegram.ui.GroupCallActivity$ListAdapter r1 = r11.listAdapter     // Catch:{ Exception -> 0x010c }
            int r19 = r1.usersVideoGridEndRow     // Catch:{ Exception -> 0x010c }
            org.telegram.ui.GroupCallActivity$ListAdapter r1 = r11.listAdapter     // Catch:{ Exception -> 0x010c }
            int r20 = r1.videoGridDividerRow     // Catch:{ Exception -> 0x010c }
            org.telegram.ui.GroupCallActivity$ListAdapter r1 = r11.listAdapter     // Catch:{ Exception -> 0x010c }
            int r21 = r1.videoNotAvailableRow     // Catch:{ Exception -> 0x010c }
            r1 = r24
            r12 = r7
            r7 = r18
            r18 = r8
            r8 = r19
            r12 = r9
            r9 = r20
            r22 = r13
            r13 = r10
            r10 = r21
            r1.setOldRows(r2, r3, r4, r5, r6, r7, r8, r9, r10)     // Catch:{ Exception -> 0x010a }
            org.telegram.ui.GroupCallActivity$ListAdapter r1 = r11.listAdapter     // Catch:{ Exception -> 0x010a }
            r1.updateRows()     // Catch:{ Exception -> 0x010a }
            androidx.recyclerview.widget.DiffUtil$Callback r1 = r11.diffUtilsCallback     // Catch:{ Exception -> 0x010a }
            androidx.recyclerview.widget.DiffUtil$DiffResult r1 = androidx.recyclerview.widget.DiffUtil.calculateDiff(r1)     // Catch:{ Exception -> 0x010a }
            r1.dispatchUpdatesTo((androidx.recyclerview.widget.ListUpdateCallback) r0)     // Catch:{ Exception -> 0x010a }
            goto L_0x011b
        L_0x010a:
            r0 = move-exception
            goto L_0x0113
        L_0x010c:
            r0 = move-exception
            r18 = r8
            r12 = r9
            r22 = r13
            r13 = r10
        L_0x0113:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            org.telegram.ui.GroupCallActivity$ListAdapter r1 = r11.listAdapter
            r1.notifyDataSetChanged()
        L_0x011b:
            org.telegram.messenger.ChatObject$Call r0 = r11.call
            r0.saveActiveDates()
            if (r17 == 0) goto L_0x0132
            org.telegram.ui.Components.FillLastGridLayoutManager r0 = r11.layoutManager
            int r1 = r17.getTop()
            org.telegram.ui.Components.RecyclerListView r2 = r11.listView
            int r2 = r2.getPaddingTop()
            int r1 = r1 - r2
            r0.scrollToPositionWithOffset(r12, r1)
        L_0x0132:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_groupCallParticipant> r0 = r11.oldParticipants
            r0.clear()
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_groupCallParticipant> r0 = r11.oldParticipants
            org.telegram.messenger.ChatObject$Call r1 = r11.call
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_groupCallParticipant> r1 = r1.visibleParticipants
            r0.addAll(r1)
            java.util.ArrayList<org.telegram.messenger.ChatObject$VideoParticipant> r0 = r11.oldVideoParticipants
            r0.clear()
            java.util.ArrayList<org.telegram.messenger.ChatObject$VideoParticipant> r0 = r11.oldVideoParticipants
            java.util.ArrayList<org.telegram.messenger.ChatObject$VideoParticipant> r1 = r11.visibleVideoParticipants
            r0.addAll(r1)
            java.util.ArrayList<java.lang.Long> r0 = r11.oldInvited
            r0.clear()
            java.util.ArrayList<java.lang.Long> r0 = r11.oldInvited
            org.telegram.messenger.ChatObject$Call r1 = r11.call
            java.util.ArrayList<java.lang.Long> r1 = r1.invitedUsers
            r0.addAll(r1)
            org.telegram.ui.GroupCallActivity$ListAdapter r0 = r11.listAdapter
            int r0 = r0.getItemCount()
            r11.oldCount = r0
            r0 = 0
        L_0x0163:
            r1 = 0
            if (r0 >= r13) goto L_0x01ae
            org.telegram.ui.Components.RecyclerListView r2 = r11.listView
            android.view.View r2 = r2.getChildAt(r0)
            boolean r3 = r2 instanceof org.telegram.ui.Cells.GroupCallUserCell
            if (r3 != 0) goto L_0x0174
            boolean r3 = r2 instanceof org.telegram.ui.Cells.GroupCallInvitedCell
            if (r3 == 0) goto L_0x01ab
        L_0x0174:
            org.telegram.ui.Components.RecyclerListView r3 = r11.listView
            androidx.recyclerview.widget.RecyclerView$ViewHolder r3 = r3.findContainingViewHolder(r2)
            if (r3 == 0) goto L_0x01ab
            boolean r4 = r2 instanceof org.telegram.ui.Cells.GroupCallUserCell
            if (r4 == 0) goto L_0x0196
            r4 = r2
            org.telegram.ui.Cells.GroupCallUserCell r4 = (org.telegram.ui.Cells.GroupCallUserCell) r4
            int r5 = r3.getAdapterPosition()
            org.telegram.ui.GroupCallActivity$ListAdapter r6 = r11.listAdapter
            int r6 = r6.getItemCount()
            int r6 = r6 + -2
            if (r5 == r6) goto L_0x0192
            r1 = 1
        L_0x0192:
            r4.setDrawDivider(r1)
            goto L_0x01ab
        L_0x0196:
            r4 = r2
            org.telegram.ui.Cells.GroupCallInvitedCell r4 = (org.telegram.ui.Cells.GroupCallInvitedCell) r4
            int r5 = r3.getAdapterPosition()
            org.telegram.ui.GroupCallActivity$ListAdapter r6 = r11.listAdapter
            int r6 = r6.getItemCount()
            int r6 = r6 + -2
            if (r5 == r6) goto L_0x01a8
            r1 = 1
        L_0x01a8:
            r4.setDrawDivider(r1)
        L_0x01ab:
            int r0 = r0 + 1
            goto L_0x0163
        L_0x01ae:
            org.telegram.ui.Components.voip.GroupCallRenderersContainer r0 = r11.renderersContainer
            boolean r0 = r0.autoPinEnabled()
            org.telegram.ui.Components.voip.GroupCallRenderersContainer r2 = r11.renderersContainer
            boolean r2 = r2.inFullscreenMode
            if (r2 == 0) goto L_0x01f1
            org.telegram.ui.Components.voip.GroupCallRenderersContainer r2 = r11.renderersContainer
            org.telegram.messenger.ChatObject$VideoParticipant r2 = r2.fullscreenParticipant
            if (r2 == 0) goto L_0x01f1
            org.telegram.ui.Components.voip.GroupCallRenderersContainer r2 = r11.renderersContainer
            org.telegram.messenger.ChatObject$VideoParticipant r2 = r2.fullscreenParticipant
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r2 = r2.participant
            org.telegram.ui.Components.voip.GroupCallRenderersContainer r3 = r11.renderersContainer
            org.telegram.messenger.ChatObject$VideoParticipant r3 = r3.fullscreenParticipant
            boolean r3 = r3.presentation
            org.telegram.messenger.ChatObject$Call r4 = r11.call
            boolean r2 = org.telegram.messenger.ChatObject.Call.videoIsActive(r2, r3, r4)
            if (r2 != 0) goto L_0x01f1
            r2 = 0
            java.util.ArrayList<org.telegram.messenger.ChatObject$VideoParticipant> r3 = r11.visibleVideoParticipants
            boolean r3 = r3.isEmpty()
            if (r3 != 0) goto L_0x01eb
            r2 = 1
            if (r0 == 0) goto L_0x01eb
            java.util.ArrayList<org.telegram.messenger.ChatObject$VideoParticipant> r3 = r11.visibleVideoParticipants
            java.lang.Object r1 = r3.get(r1)
            org.telegram.messenger.ChatObject$VideoParticipant r1 = (org.telegram.messenger.ChatObject.VideoParticipant) r1
            r11.fullscreenFor(r1)
        L_0x01eb:
            if (r2 != 0) goto L_0x01f1
            r1 = 0
            r11.fullscreenFor(r1)
        L_0x01f1:
            org.telegram.ui.Components.GroupCallFullscreenAdapter r1 = r11.fullscreenAdapter
            org.telegram.ui.Components.RecyclerListView r2 = r11.fullscreenUsersListView
            r3 = 1
            r1.update(r3, r2)
            org.telegram.ui.Components.RecyclerListView r1 = r11.fullscreenUsersListView
            int r1 = r1.getVisibility()
            if (r1 != 0) goto L_0x0206
            org.telegram.ui.Components.RecyclerListView r1 = r11.fullscreenUsersListView
            org.telegram.messenger.AndroidUtilities.updateVisibleRows(r1)
        L_0x0206:
            boolean r1 = isTabletMode
            if (r1 == 0) goto L_0x0212
            org.telegram.ui.GroupCallTabletGridAdapter r1 = r11.tabletGridAdapter
            org.telegram.ui.Components.RecyclerListView r2 = r11.tabletVideoGridView
            r3 = 1
            r1.update(r3, r2)
        L_0x0212:
            org.telegram.ui.Components.RecyclerListView r1 = r11.listView
            int r1 = r1.getVisibility()
            if (r1 != 0) goto L_0x021f
            org.telegram.ui.Components.RecyclerListView r1 = r11.listView
            org.telegram.messenger.AndroidUtilities.updateVisibleRows(r1)
        L_0x021f:
            java.util.ArrayList<org.telegram.ui.Components.voip.GroupCallMiniTextureView> r1 = r11.attachedRenderersTmp
            r1.clear()
            java.util.ArrayList<org.telegram.ui.Components.voip.GroupCallMiniTextureView> r1 = r11.attachedRenderersTmp
            java.util.ArrayList<org.telegram.ui.Components.voip.GroupCallMiniTextureView> r2 = r11.attachedRenderers
            r1.addAll(r2)
            r1 = 0
        L_0x022c:
            java.util.ArrayList<org.telegram.ui.Components.voip.GroupCallMiniTextureView> r2 = r11.attachedRenderersTmp
            int r2 = r2.size()
            if (r1 >= r2) goto L_0x0243
            java.util.ArrayList<org.telegram.ui.Components.voip.GroupCallMiniTextureView> r2 = r11.attachedRenderersTmp
            java.lang.Object r2 = r2.get(r1)
            org.telegram.ui.Components.voip.GroupCallMiniTextureView r2 = (org.telegram.ui.Components.voip.GroupCallMiniTextureView) r2
            r3 = 1
            r2.updateAttachState(r3)
            int r1 = r1 + 1
            goto L_0x022c
        L_0x0243:
            org.telegram.messenger.ChatObject$Call r1 = r11.call
            if (r1 == 0) goto L_0x026a
            org.telegram.ui.Components.voip.GroupCallRenderersContainer r1 = r11.renderersContainer
            boolean r1 = r1.inFullscreenMode
            if (r1 == 0) goto L_0x026a
            org.telegram.ui.Components.voip.GroupCallRenderersContainer r1 = r11.renderersContainer
            org.telegram.messenger.ChatObject$VideoParticipant r1 = r1.fullscreenParticipant
            if (r1 == 0) goto L_0x026a
            org.telegram.messenger.ChatObject$Call r1 = r11.call
            androidx.collection.LongSparseArray<org.telegram.tgnet.TLRPC$TL_groupCallParticipant> r1 = r1.participants
            org.telegram.ui.Components.voip.GroupCallRenderersContainer r2 = r11.renderersContainer
            org.telegram.messenger.ChatObject$VideoParticipant r2 = r2.fullscreenParticipant
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r2 = r2.participant
            org.telegram.tgnet.TLRPC$Peer r2 = r2.peer
            long r2 = org.telegram.messenger.MessageObject.getPeerId(r2)
            java.lang.Object r1 = r1.get(r2)
            if (r1 != 0) goto L_0x026a
            r0 = 1
        L_0x026a:
            org.telegram.messenger.ChatObject$Call r1 = r11.call
            java.util.ArrayList<org.telegram.messenger.ChatObject$VideoParticipant> r1 = r1.visibleVideoParticipants
            boolean r1 = r1.isEmpty()
            r2 = 1
            r1 = r1 ^ r2
            boolean r2 = r11.hasVideo
            if (r1 == r2) goto L_0x0283
            r11.hasVideo = r1
            boolean r2 = isTabletMode
            if (r2 == 0) goto L_0x0283
            android.view.ViewGroup r2 = r11.containerView
            r2.requestLayout()
        L_0x0283:
            return
        L_0x0284:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.GroupCallActivity.applyCallParticipantUpdates(boolean):void");
    }

    private void updateVideoParticipantList() {
        this.visibleVideoParticipants.clear();
        if (!isTabletMode) {
            this.visibleVideoParticipants.addAll(this.call.visibleVideoParticipants);
        } else if (this.renderersContainer.inFullscreenMode) {
            this.visibleVideoParticipants.addAll(this.call.visibleVideoParticipants);
            if (this.renderersContainer.fullscreenParticipant != null) {
                this.visibleVideoParticipants.remove(this.renderersContainer.fullscreenParticipant);
            }
        }
    }

    private void updateRecordCallText() {
        if (this.call != null) {
            int time = this.accountInstance.getConnectionsManager().getCurrentTime() - this.call.call.record_start_date;
            if (this.call.recording) {
                this.recordItem.setSubtext(AndroidUtilities.formatDuration(time, false));
            } else {
                this.recordItem.setSubtext((String) null);
            }
        }
    }

    /* access modifiers changed from: private */
    public void updateItems() {
        String str;
        int i;
        int margin;
        TLObject object;
        boolean mutedByAdmin;
        int i2;
        TLRPC.TL_groupCallParticipant participant;
        ChatObject.Call call2 = this.call;
        if (call2 == null || call2.isScheduled()) {
            this.pipItem.setVisibility(4);
            this.screenShareItem.setVisibility(8);
            if (this.call == null) {
                this.otherItem.setVisibility(8);
                return;
            }
        }
        if (!this.changingPermissions) {
            TLRPC.Chat newChat = this.accountInstance.getMessagesController().getChat(Long.valueOf(this.currentChat.id));
            if (newChat != null) {
                this.currentChat = newChat;
            }
            if (ChatObject.canUserDoAdminAction(this.currentChat, 3) || (((!ChatObject.isChannel(this.currentChat) || this.currentChat.megagroup) && (!TextUtils.isEmpty(this.currentChat.username) || ChatObject.canUserDoAdminAction(this.currentChat, 3))) || (ChatObject.isChannel(this.currentChat) && !this.currentChat.megagroup && !TextUtils.isEmpty(this.currentChat.username)))) {
                this.inviteItem.setVisibility(0);
            } else {
                this.inviteItem.setVisibility(8);
            }
            TLRPC.TL_groupCallParticipant participant2 = this.call.participants.get(MessageObject.getPeerId(this.selfPeer));
            ChatObject.Call call3 = this.call;
            if (call3 == null || call3.isScheduled() || (participant2 != null && !participant2.can_self_unmute && participant2.muted)) {
                this.noiseItem.setVisibility(8);
            } else {
                this.noiseItem.setVisibility(0);
            }
            this.noiseItem.setIcon(SharedConfig.noiseSupression ? NUM : NUM);
            ActionBarMenuSubItem actionBarMenuSubItem = this.noiseItem;
            if (SharedConfig.noiseSupression) {
                i = NUM;
                str = "VoipNoiseCancellationEnabled";
            } else {
                i = NUM;
                str = "VoipNoiseCancellationDisabled";
            }
            actionBarMenuSubItem.setSubtext(LocaleController.getString(str, i));
            boolean z = true;
            if (ChatObject.canManageCalls(this.currentChat)) {
                this.leaveItem.setVisibility(0);
                this.editTitleItem.setVisibility(0);
                if (isRtmpStream()) {
                    this.recordItem.setVisibility(0);
                    this.screenItem.setVisibility(8);
                } else if (this.call.isScheduled()) {
                    this.recordItem.setVisibility(8);
                    this.screenItem.setVisibility(8);
                } else {
                    this.recordItem.setVisibility(0);
                }
                if (!this.call.canRecordVideo() || this.call.isScheduled() || Build.VERSION.SDK_INT < 21 || isRtmpStream()) {
                    this.screenItem.setVisibility(8);
                } else {
                    this.screenItem.setVisibility(0);
                }
                this.screenShareItem.setVisibility(8);
                this.recordCallDrawable.setRecording(this.call.recording);
                if (this.call.recording) {
                    if (this.updateCallRecordRunnable == null) {
                        GroupCallActivity$$ExternalSyntheticLambda29 groupCallActivity$$ExternalSyntheticLambda29 = new GroupCallActivity$$ExternalSyntheticLambda29(this);
                        this.updateCallRecordRunnable = groupCallActivity$$ExternalSyntheticLambda29;
                        participant = participant2;
                        AndroidUtilities.runOnUIThread(groupCallActivity$$ExternalSyntheticLambda29, 1000);
                    } else {
                        participant = participant2;
                    }
                    this.recordItem.setText(LocaleController.getString("VoipGroupStopRecordCall", NUM));
                } else {
                    participant = participant2;
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
                TLRPC.TL_groupCallParticipant tL_groupCallParticipant = participant;
            } else {
                TLRPC.TL_groupCallParticipant participant3 = participant2;
                if (participant3 != null) {
                    TLRPC.TL_groupCallParticipant participant4 = participant3;
                    if (!participant4.can_self_unmute && participant4.muted && !ChatObject.canManageCalls(this.currentChat)) {
                        mutedByAdmin = true;
                        if (VoIPService.getSharedInstance() == null || VoIPService.getSharedInstance().getVideoState(true) != 2) {
                            z = false;
                        }
                        boolean sharingScreen = z;
                        if (Build.VERSION.SDK_INT >= 21 || mutedByAdmin || ((!this.call.canRecordVideo() && !sharingScreen) || this.call.isScheduled() || isRtmpStream())) {
                            i2 = 8;
                            this.screenShareItem.setVisibility(8);
                            this.screenItem.setVisibility(8);
                        } else if (sharingScreen) {
                            this.screenShareItem.setVisibility(8);
                            this.screenItem.setVisibility(0);
                            this.screenItem.setTextAndIcon(LocaleController.getString("VoipChatStopScreenCapture", NUM), NUM);
                            this.screenItem.setContentDescription(LocaleController.getString("VoipChatStopScreenCapture", NUM));
                            i2 = 8;
                        } else {
                            this.screenItem.setTextAndIcon(LocaleController.getString("VoipChatStartScreenCapture", NUM), NUM);
                            this.screenItem.setContentDescription(LocaleController.getString("VoipChatStartScreenCapture", NUM));
                            this.screenShareItem.setVisibility(8);
                            this.screenItem.setVisibility(0);
                            i2 = 8;
                        }
                        this.leaveItem.setVisibility(i2);
                        this.editTitleItem.setVisibility(i2);
                        this.recordItem.setVisibility(i2);
                    }
                }
                mutedByAdmin = false;
                z = false;
                boolean sharingScreen2 = z;
                if (Build.VERSION.SDK_INT >= 21) {
                }
                i2 = 8;
                this.screenShareItem.setVisibility(8);
                this.screenItem.setVisibility(8);
                this.leaveItem.setVisibility(i2);
                this.editTitleItem.setVisibility(i2);
                this.recordItem.setVisibility(i2);
            }
            if (!ChatObject.canManageCalls(this.currentChat) || !this.call.call.can_change_join_muted) {
                this.permissionItem.setVisibility(8);
            } else {
                this.permissionItem.setVisibility(0);
            }
            this.soundItem.setVisibility(isRtmpStream() ? 8 : 0);
            if (this.editTitleItem.getVisibility() == 0 || this.permissionItem.getVisibility() == 0 || this.inviteItem.getVisibility() == 0 || this.screenItem.getVisibility() == 0 || this.recordItem.getVisibility() == 0 || this.leaveItem.getVisibility() == 0) {
                this.soundItemDivider.setVisibility(0);
            } else {
                this.soundItemDivider.setVisibility(8);
            }
            if (((VoIPService.getSharedInstance() == null || !VoIPService.getSharedInstance().hasFewPeers) && !this.scheduleHasFewPeers) || isRtmpStream()) {
                margin = 48 + 48;
                this.accountSelectCell.setVisibility(8);
                this.accountGap.setVisibility(8);
            } else {
                this.accountSelectCell.setVisibility(0);
                this.accountGap.setVisibility(0);
                long peerId = MessageObject.getPeerId(this.selfPeer);
                if (DialogObject.isUserDialog(peerId)) {
                    object = this.accountInstance.getMessagesController().getUser(Long.valueOf(peerId));
                } else {
                    object = this.accountInstance.getMessagesController().getChat(Long.valueOf(-peerId));
                }
                this.accountSelectCell.setObject(object);
                margin = 48 + 48;
            }
            TLRPC.Chat chat = this.currentChat;
            if (chat == null || ChatObject.isChannelOrGiga(chat) || !isRtmpStream() || this.inviteItem.getVisibility() != 8) {
                this.otherItem.setVisibility(0);
            } else {
                this.otherItem.setVisibility(8);
            }
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.titleTextView.getLayoutParams();
            if (layoutParams.rightMargin != AndroidUtilities.dp((float) margin)) {
                layoutParams.rightMargin = AndroidUtilities.dp((float) margin);
                this.titleTextView.requestLayout();
            }
            ((FrameLayout.LayoutParams) this.menuItemsContainer.getLayoutParams()).rightMargin = 0;
            this.actionBar.setTitleRightMargin(AndroidUtilities.dp(48.0f) * 2);
        }
    }

    /* renamed from: lambda$updateItems$4$org-telegram-ui-GroupCallActivity  reason: not valid java name */
    public /* synthetic */ void m3545lambda$updateItems$4$orgtelegramuiGroupCallActivity() {
        updateRecordCallText();
        AndroidUtilities.runOnUIThread(this.updateCallRecordRunnable, 1000);
    }

    /* access modifiers changed from: protected */
    public void makeFocusable(BottomSheet bottomSheet, AlertDialog alertDialog, EditTextBoldCursor editText, boolean showKeyboard) {
        if (!this.enterEventSent) {
            BaseFragment fragment = this.parentActivity.getActionBarLayout().fragmentsStack.get(this.parentActivity.getActionBarLayout().fragmentsStack.size() - 1);
            if (fragment instanceof ChatActivity) {
                boolean keyboardVisible = ((ChatActivity) fragment).needEnterText();
                this.enterEventSent = true;
                this.anyEnterEventSent = true;
                AndroidUtilities.runOnUIThread(new GroupCallActivity$$ExternalSyntheticLambda19(bottomSheet, editText, showKeyboard, alertDialog), keyboardVisible ? 200 : 0);
                return;
            }
            this.enterEventSent = true;
            this.anyEnterEventSent = true;
            if (bottomSheet != null) {
                bottomSheet.setFocusable(true);
            } else if (alertDialog != null) {
                alertDialog.setFocusable(true);
            }
            if (showKeyboard) {
                AndroidUtilities.runOnUIThread(new GroupCallActivity$$ExternalSyntheticLambda23(editText), 100);
            }
        }
    }

    static /* synthetic */ void lambda$makeFocusable$7(BottomSheet bottomSheet, EditTextBoldCursor editText, boolean showKeyboard, AlertDialog alertDialog) {
        if (bottomSheet != null && !bottomSheet.isDismissed()) {
            bottomSheet.setFocusable(true);
            editText.requestFocus();
            if (showKeyboard) {
                AndroidUtilities.runOnUIThread(new GroupCallActivity$$ExternalSyntheticLambda20(editText));
            }
        } else if (alertDialog != null && alertDialog.isShowing()) {
            alertDialog.setFocusable(true);
            editText.requestFocus();
            if (showKeyboard) {
                AndroidUtilities.runOnUIThread(new GroupCallActivity$$ExternalSyntheticLambda21(editText));
            }
        }
    }

    static /* synthetic */ void lambda$makeFocusable$8(EditTextBoldCursor editText) {
        editText.requestFocus();
        AndroidUtilities.showKeyboard(editText);
    }

    public static void create(LaunchActivity activity, AccountInstance account, TLRPC.Chat scheduleChat, TLRPC.InputPeer schedulePeer2, boolean hasFewPeers, String scheduledHash2) {
        TLRPC.Chat chat;
        if (groupCallInstance != null) {
            LaunchActivity launchActivity = activity;
            TLRPC.Chat chat2 = scheduleChat;
        } else if (schedulePeer2 == null && VoIPService.getSharedInstance() == null) {
            LaunchActivity launchActivity2 = activity;
            TLRPC.Chat chat3 = scheduleChat;
        } else {
            if (schedulePeer2 != null) {
                groupCallInstance = new GroupCallActivity(activity, account, account.getMessagesController().getGroupCall(scheduleChat.id, false), scheduleChat, schedulePeer2, hasFewPeers, scheduledHash2);
            } else {
                TLRPC.Chat chat4 = scheduleChat;
                ChatObject.Call call2 = VoIPService.getSharedInstance().groupCall;
                if (call2 != null && (chat = account.getMessagesController().getChat(Long.valueOf(call2.chatId))) != null) {
                    call2.addSelfDummyParticipant(true);
                    groupCallInstance = new GroupCallActivity(activity, account, call2, chat, (TLRPC.InputPeer) null, hasFewPeers, scheduledHash2);
                } else {
                    return;
                }
            }
            GroupCallActivity groupCallActivity = groupCallInstance;
            groupCallActivity.parentActivity = activity;
            groupCallActivity.show();
        }
    }

    /* JADX WARNING: Illegal instructions before constructor call */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private GroupCallActivity(android.content.Context r44, org.telegram.messenger.AccountInstance r45, org.telegram.messenger.ChatObject.Call r46, org.telegram.tgnet.TLRPC.Chat r47, org.telegram.tgnet.TLRPC.InputPeer r48, boolean r49, java.lang.String r50) {
        /*
            r43 = this;
            r8 = r43
            r9 = r44
            r10 = r46
            r11 = r48
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
            r8.animatingToFullscreenExpand = r12
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
            java.lang.Boolean r1 = java.lang.Boolean.valueOf(r13)
            r8.wasExpandBigSize = r1
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
            org.telegram.ui.GroupCallActivity$$ExternalSyntheticLambda30 r1 = new org.telegram.ui.GroupCallActivity$$ExternalSyntheticLambda30
            r1.<init>(r8)
            r8.onUserLeaveHintListener = r1
            org.telegram.ui.GroupCallActivity$1 r1 = new org.telegram.ui.GroupCallActivity$1
            r1.<init>()
            r8.updateSchedeulRunnable = r1
            org.telegram.ui.GroupCallActivity$$ExternalSyntheticLambda42 r1 = org.telegram.ui.GroupCallActivity$$ExternalSyntheticLambda42.INSTANCE
            r8.unmuteRunnable = r1
            org.telegram.ui.GroupCallActivity$$ExternalSyntheticLambda26 r1 = new org.telegram.ui.GroupCallActivity$$ExternalSyntheticLambda26
            r1.<init>(r8)
            r8.pressRunnable = r1
            org.telegram.messenger.support.LongSparseIntArray r1 = new org.telegram.messenger.support.LongSparseIntArray
            r1.<init>()
            r8.visiblePeerIds = r1
            int[] r1 = new int[r0]
            r8.gradientColors = r1
            r8.listViewVideoVisibility = r13
            java.lang.String[] r1 = new java.lang.String[r0]
            r8.invites = r1
            r7 = -1
            r8.popupAnimationIndex = r7
            org.telegram.ui.GroupCallActivity$58 r1 = new org.telegram.ui.GroupCallActivity$58
            r1.<init>()
            r8.diffUtilsCallback = r1
            r6 = r45
            r8.accountInstance = r6
            r8.call = r10
            r8.schedulePeer = r11
            r5 = r47
            r8.currentChat = r5
            r4 = r50
            r8.scheduledHash = r4
            int r1 = r45.getCurrentAccount()
            r8.currentAccount = r1
            r3 = r49
            r8.scheduleHasFewPeers = r3
            r8.fullWidth = r13
            isTabletMode = r12
            isLandscapeMode = r12
            paused = r12
            org.telegram.ui.GroupCallActivity$3 r1 = new org.telegram.ui.GroupCallActivity$3
            r1.<init>()
            r8.setDelegate(r1)
            r8.drawDoubleNavigationBar = r13
            r8.drawNavigationBar = r13
            int r1 = android.os.Build.VERSION.SDK_INT
            r2 = 30
            if (r1 < r2) goto L_0x0110
            android.view.Window r1 = r43.getWindow()
            r2 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            r1.setNavigationBarColor(r2)
        L_0x0110:
            r8.scrollNavBar = r13
            r1 = 0
            r8.navBarColorKey = r1
            org.telegram.ui.GroupCallActivity$4 r2 = new org.telegram.ui.GroupCallActivity$4
            r2.<init>()
            r8.scrimPaint = r2
            org.telegram.ui.GroupCallActivity$$ExternalSyntheticLambda2 r2 = new org.telegram.ui.GroupCallActivity$$ExternalSyntheticLambda2
            r2.<init>(r8)
            r8.setOnDismissListener(r2)
            r2 = 75
            r8.setDimBehindAlpha(r2)
            org.telegram.ui.GroupCallActivity$ListAdapter r2 = new org.telegram.ui.GroupCallActivity$ListAdapter
            r2.<init>(r9)
            r8.listAdapter = r2
            org.telegram.ui.Components.RecordStatusDrawable r2 = new org.telegram.ui.Components.RecordStatusDrawable
            r2.<init>(r13)
            java.lang.String r16 = "voipgroup_speakingText"
            int r0 = org.telegram.ui.ActionBar.Theme.getColor(r16)
            r2.setColor(r0)
            r2.start()
            org.telegram.ui.GroupCallActivity$5 r0 = new org.telegram.ui.GroupCallActivity$5
            r0.<init>(r9, r2)
            r8.actionBar = r0
            java.lang.String r14 = ""
            r0.setSubtitle(r14)
            org.telegram.ui.ActionBar.ActionBar r0 = r8.actionBar
            org.telegram.ui.ActionBar.SimpleTextView r0 = r0.getSubtitleTextView()
            r0.setVisibility(r12)
            org.telegram.ui.ActionBar.ActionBar r0 = r8.actionBar
            r0.createAdditionalSubtitleTextView()
            org.telegram.ui.ActionBar.ActionBar r0 = r8.actionBar
            org.telegram.ui.ActionBar.SimpleTextView r0 = r0.getAdditionalSubtitleTextView()
            r14 = 1103101952(0x41CLASSNAME, float:24.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r14)
            r0.setPadding(r1, r12, r12, r12)
            org.telegram.ui.ActionBar.ActionBar r0 = r8.actionBar
            org.telegram.ui.ActionBar.SimpleTextView r0 = r0.getAdditionalSubtitleTextView()
            boolean r1 = r8.drawSpeakingSubtitle
            org.telegram.messenger.AndroidUtilities.updateViewVisibilityAnimated(r0, r1, r15, r12)
            org.telegram.ui.ActionBar.ActionBar r0 = r8.actionBar
            org.telegram.ui.ActionBar.SimpleTextView r0 = r0.getAdditionalSubtitleTextView()
            java.lang.String r1 = "voipgroup_speakingText"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.setTextColor(r1)
            org.telegram.ui.ActionBar.ActionBar r0 = r8.actionBar
            java.lang.String r1 = "voipgroup_lastSeenTextUnscrolled"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.setSubtitleColor(r1)
            org.telegram.ui.ActionBar.ActionBar r0 = r8.actionBar
            r1 = 2131165449(0x7var_, float:1.7945115E38)
            r0.setBackButtonImage(r1)
            org.telegram.ui.ActionBar.ActionBar r0 = r8.actionBar
            r0.setOccupyStatusBar(r12)
            org.telegram.ui.ActionBar.ActionBar r0 = r8.actionBar
            r0.setAllowOverlayTitle(r12)
            org.telegram.ui.ActionBar.ActionBar r0 = r8.actionBar
            java.lang.String r1 = "voipgroup_actionBarItems"
            int r15 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.setItemsColor(r15, r12)
            org.telegram.ui.ActionBar.ActionBar r0 = r8.actionBar
            java.lang.String r15 = "actionBarActionModeDefaultSelector"
            int r15 = org.telegram.ui.ActionBar.Theme.getColor(r15)
            r0.setItemsBackgroundColor(r15, r12)
            org.telegram.ui.ActionBar.ActionBar r0 = r8.actionBar
            int r15 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.setTitleColor(r15)
            org.telegram.ui.ActionBar.ActionBar r0 = r8.actionBar
            java.lang.String r15 = "voipgroup_lastSeenTextUnscrolled"
            int r15 = org.telegram.ui.ActionBar.Theme.getColor(r15)
            r0.setSubtitleColor(r15)
            org.telegram.ui.ActionBar.ActionBar r0 = r8.actionBar
            org.telegram.ui.GroupCallActivity$6 r15 = new org.telegram.ui.GroupCallActivity$6
            r15.<init>(r9)
            r0.setActionBarMenuOnItemClick(r15)
            if (r11 == 0) goto L_0x01db
            r0 = r48
            r15 = r0
            goto L_0x01e4
        L_0x01db:
            org.telegram.messenger.voip.VoIPService r0 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            org.telegram.tgnet.TLRPC$InputPeer r0 = r0.getGroupCallPeer()
            r15 = r0
        L_0x01e4:
            if (r15 != 0) goto L_0x01fa
            org.telegram.tgnet.TLRPC$TL_peerUser r0 = new org.telegram.tgnet.TLRPC$TL_peerUser
            r0.<init>()
            r8.selfPeer = r0
            org.telegram.messenger.AccountInstance r14 = r8.accountInstance
            org.telegram.messenger.UserConfig r14 = r14.getUserConfig()
            long r12 = r14.getClientUserId()
            r0.user_id = r12
            goto L_0x0229
        L_0x01fa:
            boolean r0 = r15 instanceof org.telegram.tgnet.TLRPC.TL_inputPeerChannel
            if (r0 == 0) goto L_0x020a
            org.telegram.tgnet.TLRPC$TL_peerChannel r0 = new org.telegram.tgnet.TLRPC$TL_peerChannel
            r0.<init>()
            r8.selfPeer = r0
            long r12 = r15.channel_id
            r0.channel_id = r12
            goto L_0x0229
        L_0x020a:
            boolean r0 = r15 instanceof org.telegram.tgnet.TLRPC.TL_inputPeerUser
            if (r0 == 0) goto L_0x021a
            org.telegram.tgnet.TLRPC$TL_peerUser r0 = new org.telegram.tgnet.TLRPC$TL_peerUser
            r0.<init>()
            r8.selfPeer = r0
            long r12 = r15.user_id
            r0.user_id = r12
            goto L_0x0229
        L_0x021a:
            boolean r0 = r15 instanceof org.telegram.tgnet.TLRPC.TL_inputPeerChat
            if (r0 == 0) goto L_0x0229
            org.telegram.tgnet.TLRPC$TL_peerChat r0 = new org.telegram.tgnet.TLRPC$TL_peerChat
            r0.<init>()
            r8.selfPeer = r0
            long r12 = r15.chat_id
            r0.chat_id = r12
        L_0x0229:
            org.telegram.ui.GroupCallActivity$$ExternalSyntheticLambda43 r0 = new org.telegram.ui.GroupCallActivity$$ExternalSyntheticLambda43
            r0.<init>(r8)
            org.telegram.messenger.voip.VoIPService.audioLevelsCallback = r0
            org.telegram.messenger.AccountInstance r0 = r8.accountInstance
            org.telegram.messenger.NotificationCenter r0 = r0.getNotificationCenter()
            int r12 = org.telegram.messenger.NotificationCenter.groupCallUpdated
            r0.addObserver(r8, r12)
            org.telegram.messenger.AccountInstance r0 = r8.accountInstance
            org.telegram.messenger.NotificationCenter r0 = r0.getNotificationCenter()
            int r12 = org.telegram.messenger.NotificationCenter.needShowAlert
            r0.addObserver(r8, r12)
            org.telegram.messenger.AccountInstance r0 = r8.accountInstance
            org.telegram.messenger.NotificationCenter r0 = r0.getNotificationCenter()
            int r12 = org.telegram.messenger.NotificationCenter.chatInfoDidLoad
            r0.addObserver(r8, r12)
            org.telegram.messenger.AccountInstance r0 = r8.accountInstance
            org.telegram.messenger.NotificationCenter r0 = r0.getNotificationCenter()
            int r12 = org.telegram.messenger.NotificationCenter.didLoadChatAdmins
            r0.addObserver(r8, r12)
            org.telegram.messenger.AccountInstance r0 = r8.accountInstance
            org.telegram.messenger.NotificationCenter r0 = r0.getNotificationCenter()
            int r12 = org.telegram.messenger.NotificationCenter.applyGroupCallVisibleParticipants
            r0.addObserver(r8, r12)
            org.telegram.messenger.AccountInstance r0 = r8.accountInstance
            org.telegram.messenger.NotificationCenter r0 = r0.getNotificationCenter()
            int r12 = org.telegram.messenger.NotificationCenter.userInfoDidLoad
            r0.addObserver(r8, r12)
            org.telegram.messenger.AccountInstance r0 = r8.accountInstance
            org.telegram.messenger.NotificationCenter r0 = r0.getNotificationCenter()
            int r12 = org.telegram.messenger.NotificationCenter.mainUserInfoChanged
            r0.addObserver(r8, r12)
            org.telegram.messenger.AccountInstance r0 = r8.accountInstance
            org.telegram.messenger.NotificationCenter r0 = r0.getNotificationCenter()
            int r12 = org.telegram.messenger.NotificationCenter.updateInterfaces
            r0.addObserver(r8, r12)
            org.telegram.messenger.AccountInstance r0 = r8.accountInstance
            org.telegram.messenger.NotificationCenter r0 = r0.getNotificationCenter()
            int r12 = org.telegram.messenger.NotificationCenter.groupCallScreencastStateChanged
            r0.addObserver(r8, r12)
            org.telegram.messenger.AccountInstance r0 = r8.accountInstance
            org.telegram.messenger.NotificationCenter r0 = r0.getNotificationCenter()
            int r12 = org.telegram.messenger.NotificationCenter.groupCallSpeakingUsersUpdated
            r0.addObserver(r8, r12)
            org.telegram.messenger.NotificationCenter r0 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r12 = org.telegram.messenger.NotificationCenter.webRtcMicAmplitudeEvent
            r0.addObserver(r8, r12)
            org.telegram.messenger.NotificationCenter r0 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r12 = org.telegram.messenger.NotificationCenter.didEndCall
            r0.addObserver(r8, r12)
            android.content.res.Resources r0 = r44.getResources()
            r12 = 2131166138(0x7var_ba, float:1.7946513E38)
            android.graphics.drawable.Drawable r0 = r0.getDrawable(r12)
            android.graphics.drawable.Drawable r0 = r0.mutate()
            r8.shadowDrawable = r0
            org.telegram.ui.Components.RLottieDrawable r0 = new org.telegram.ui.Components.RLottieDrawable
            r22 = 2131558588(0x7f0d00bc, float:1.8742496E38)
            r12 = 1116733440(0x42900000, float:72.0)
            int r24 = org.telegram.messenger.AndroidUtilities.dp(r12)
            int r25 = org.telegram.messenger.AndroidUtilities.dp(r12)
            r26 = 1
            r27 = 0
            java.lang.String r23 = "NUM"
            r21 = r0
            r21.<init>(r22, r23, r24, r25, r26, r27)
            r8.bigMicDrawable = r0
            org.telegram.ui.Components.RLottieDrawable r0 = new org.telegram.ui.Components.RLottieDrawable
            r29 = 2131558459(0x7f0d003b, float:1.8742234E38)
            int r31 = org.telegram.messenger.AndroidUtilities.dp(r12)
            int r32 = org.telegram.messenger.AndroidUtilities.dp(r12)
            r33 = 1
            r34 = 0
            java.lang.String r30 = "NUM"
            r28 = r0
            r28.<init>(r29, r30, r31, r32, r33, r34)
            r8.handDrawables = r0
            org.telegram.ui.GroupCallActivity$7 r0 = new org.telegram.ui.GroupCallActivity$7
            r0.<init>(r9)
            r8.containerView = r0
            android.view.ViewGroup r0 = r8.containerView
            r13 = 1
            r0.setFocusable(r13)
            android.view.ViewGroup r0 = r8.containerView
            r0.setFocusableInTouchMode(r13)
            android.view.ViewGroup r0 = r8.containerView
            r14 = 0
            r0.setWillNotDraw(r14)
            android.view.ViewGroup r0 = r8.containerView
            int r12 = r8.backgroundPaddingLeft
            int r7 = r8.backgroundPaddingLeft
            r0.setPadding(r12, r14, r7, r14)
            android.view.ViewGroup r0 = r8.containerView
            r0.setKeepScreenOn(r13)
            android.view.ViewGroup r0 = r8.containerView
            r0.setClipChildren(r14)
            java.lang.String r12 = "fonts/rmedium.ttf"
            r13 = 17
            if (r11 == 0) goto L_0x03ca
            org.telegram.ui.ActionBar.SimpleTextView r0 = new org.telegram.ui.ActionBar.SimpleTextView
            r0.<init>(r9)
            r8.scheduleStartInTextView = r0
            r0.setGravity(r13)
            org.telegram.ui.ActionBar.SimpleTextView r0 = r8.scheduleStartInTextView
            r7 = -1
            r0.setTextColor(r7)
            org.telegram.ui.ActionBar.SimpleTextView r0 = r8.scheduleStartInTextView
            android.graphics.Typeface r7 = org.telegram.messenger.AndroidUtilities.getTypeface(r12)
            r0.setTypeface(r7)
            org.telegram.ui.ActionBar.SimpleTextView r0 = r8.scheduleStartInTextView
            r7 = 18
            r0.setTextSize(r7)
            org.telegram.ui.ActionBar.SimpleTextView r0 = r8.scheduleStartInTextView
            r7 = 2131629010(0x7f0e13d2, float:1.8885329E38)
            java.lang.String r14 = "VoipChatStartsIn"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r14, r7)
            r0.setText(r7)
            android.view.ViewGroup r0 = r8.containerView
            org.telegram.ui.ActionBar.SimpleTextView r7 = r8.scheduleStartInTextView
            r23 = -2
            r24 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r25 = 49
            r26 = 1101529088(0x41a80000, float:21.0)
            r27 = 0
            r28 = 1101529088(0x41a80000, float:21.0)
            r29 = 1134264320(0x439b8000, float:311.0)
            android.widget.FrameLayout$LayoutParams r14 = org.telegram.ui.Components.LayoutHelper.createFrame(r23, r24, r25, r26, r27, r28, r29)
            r0.addView(r7, r14)
            org.telegram.ui.GroupCallActivity$8 r0 = new org.telegram.ui.GroupCallActivity$8
            r0.<init>(r9)
            r8.scheduleTimeTextView = r0
            r0.setGravity(r13)
            org.telegram.ui.ActionBar.SimpleTextView r0 = r8.scheduleTimeTextView
            r7 = -1
            r0.setTextColor(r7)
            org.telegram.ui.ActionBar.SimpleTextView r0 = r8.scheduleTimeTextView
            android.graphics.Typeface r7 = org.telegram.messenger.AndroidUtilities.getTypeface(r12)
            r0.setTypeface(r7)
            org.telegram.ui.ActionBar.SimpleTextView r0 = r8.scheduleTimeTextView
            r7 = 60
            r0.setTextSize(r7)
            android.view.ViewGroup r0 = r8.containerView
            org.telegram.ui.ActionBar.SimpleTextView r7 = r8.scheduleTimeTextView
            r29 = 1130823680(0x43670000, float:231.0)
            android.widget.FrameLayout$LayoutParams r14 = org.telegram.ui.Components.LayoutHelper.createFrame(r23, r24, r25, r26, r27, r28, r29)
            r0.addView(r7, r14)
            org.telegram.ui.ActionBar.SimpleTextView r0 = new org.telegram.ui.ActionBar.SimpleTextView
            r0.<init>(r9)
            r8.scheduleStartAtTextView = r0
            r0.setGravity(r13)
            org.telegram.ui.ActionBar.SimpleTextView r0 = r8.scheduleStartAtTextView
            r7 = -1
            r0.setTextColor(r7)
            org.telegram.ui.ActionBar.SimpleTextView r0 = r8.scheduleStartAtTextView
            android.graphics.Typeface r7 = org.telegram.messenger.AndroidUtilities.getTypeface(r12)
            r0.setTypeface(r7)
            org.telegram.ui.ActionBar.SimpleTextView r0 = r8.scheduleStartAtTextView
            r7 = 18
            r0.setTextSize(r7)
            android.view.ViewGroup r0 = r8.containerView
            org.telegram.ui.ActionBar.SimpleTextView r7 = r8.scheduleStartAtTextView
            r29 = 1128857600(0x43490000, float:201.0)
            android.widget.FrameLayout$LayoutParams r14 = org.telegram.ui.Components.LayoutHelper.createFrame(r23, r24, r25, r26, r27, r28, r29)
            r0.addView(r7, r14)
        L_0x03ca:
            org.telegram.ui.GroupCallActivity$9 r0 = new org.telegram.ui.GroupCallActivity$9
            r0.<init>(r9)
            r8.listView = r0
            r7 = 0
            r0.setClipToPadding(r7)
            org.telegram.ui.Components.RecyclerListView r0 = r8.listView
            r0.setClipChildren(r7)
            org.telegram.ui.GroupCallActivity$GroupCallItemAnimator r0 = new org.telegram.ui.GroupCallActivity$GroupCallItemAnimator
            r7 = 0
            r0.<init>()
            r8.itemAnimator = r0
            org.telegram.ui.Components.CubicBezierInterpolator r7 = org.telegram.ui.Components.CubicBezierInterpolator.DEFAULT
            r0.setTranslationInterpolator(r7)
            org.telegram.ui.GroupCallActivity$GroupCallItemAnimator r0 = r8.itemAnimator
            r13 = 350(0x15e, double:1.73E-321)
            r0.setRemoveDuration(r13)
            org.telegram.ui.GroupCallActivity$GroupCallItemAnimator r0 = r8.itemAnimator
            r0.setAddDuration(r13)
            org.telegram.ui.GroupCallActivity$GroupCallItemAnimator r0 = r8.itemAnimator
            r0.setMoveDuration(r13)
            org.telegram.ui.GroupCallActivity$GroupCallItemAnimator r0 = r8.itemAnimator
            r7 = 0
            r0.setDelayAnimations(r7)
            org.telegram.ui.Components.RecyclerListView r0 = r8.listView
            org.telegram.ui.GroupCallActivity$GroupCallItemAnimator r7 = r8.itemAnimator
            r0.setItemAnimator(r7)
            org.telegram.ui.Components.RecyclerListView r0 = r8.listView
            org.telegram.ui.GroupCallActivity$10 r7 = new org.telegram.ui.GroupCallActivity$10
            r7.<init>()
            r0.setOnScrollListener(r7)
            org.telegram.ui.Components.RecyclerListView r0 = r8.listView
            r7 = 0
            r0.setVerticalScrollBarEnabled(r7)
            org.telegram.ui.Components.RecyclerListView r0 = r8.listView
            org.telegram.ui.Components.FillLastGridLayoutManager r7 = new org.telegram.ui.Components.FillLastGridLayoutManager
            android.content.Context r25 = r43.getContext()
            boolean r24 = isLandscapeMode
            r13 = 6
            if (r24 == 0) goto L_0x0425
            r26 = 6
            goto L_0x0427
        L_0x0425:
            r26 = 2
        L_0x0427:
            r27 = 1
            r28 = 0
            r29 = 0
            org.telegram.ui.Components.RecyclerListView r14 = r8.listView
            r24 = r7
            r30 = r14
            r24.<init>(r25, r26, r27, r28, r29, r30)
            r8.layoutManager = r7
            r0.setLayoutManager(r7)
            org.telegram.ui.Components.FillLastGridLayoutManager r0 = r8.layoutManager
            org.telegram.ui.GroupCallActivity$11 r7 = new org.telegram.ui.GroupCallActivity$11
            r7.<init>()
            r8.spanSizeLookup = r7
            r0.setSpanSizeLookup(r7)
            org.telegram.ui.Components.RecyclerListView r0 = r8.listView
            org.telegram.ui.GroupCallActivity$12 r7 = new org.telegram.ui.GroupCallActivity$12
            r7.<init>()
            r0.addItemDecoration(r7)
            org.telegram.ui.Components.FillLastGridLayoutManager r0 = r8.layoutManager
            r7 = 0
            r0.setBind(r7)
            android.view.ViewGroup r0 = r8.containerView
            org.telegram.ui.Components.RecyclerListView r7 = r8.listView
            r24 = -1
            r25 = -1082130432(0xffffffffbvar_, float:-1.0)
            r26 = 51
            r27 = 1096810496(0x41600000, float:14.0)
            r28 = 1096810496(0x41600000, float:14.0)
            r29 = 1096810496(0x41600000, float:14.0)
            r30 = 1130823680(0x43670000, float:231.0)
            android.widget.FrameLayout$LayoutParams r14 = org.telegram.ui.Components.LayoutHelper.createFrame(r24, r25, r26, r27, r28, r29, r30)
            r0.addView(r7, r14)
            org.telegram.ui.Components.RecyclerListView r0 = r8.listView
            org.telegram.ui.GroupCallActivity$ListAdapter r7 = r8.listAdapter
            r0.setAdapter(r7)
            org.telegram.ui.Components.RecyclerListView r0 = r8.listView
            r7 = 13
            r0.setTopBottomSelectorRadius(r7)
            org.telegram.ui.Components.RecyclerListView r0 = r8.listView
            java.lang.String r7 = "voipgroup_listSelector"
            int r7 = org.telegram.ui.ActionBar.Theme.getColor(r7)
            r0.setSelectorDrawableColor(r7)
            org.telegram.ui.Components.RecyclerListView r0 = r8.listView
            org.telegram.ui.GroupCallActivity$$ExternalSyntheticLambda59 r7 = new org.telegram.ui.GroupCallActivity$$ExternalSyntheticLambda59
            r7.<init>(r8)
            r0.setOnItemClickListener((org.telegram.ui.Components.RecyclerListView.OnItemClickListenerExtended) r7)
            org.telegram.ui.Components.RecyclerListView r0 = r8.listView
            org.telegram.ui.GroupCallActivity$$ExternalSyntheticLambda60 r7 = new org.telegram.ui.GroupCallActivity$$ExternalSyntheticLambda60
            r7.<init>(r8)
            r0.setOnItemLongClickListener((org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener) r7)
            org.telegram.ui.Components.RecyclerListView r0 = new org.telegram.ui.Components.RecyclerListView
            r0.<init>(r9)
            r8.tabletVideoGridView = r0
            android.view.ViewGroup r0 = r8.containerView
            org.telegram.ui.Components.RecyclerListView r7 = r8.tabletVideoGridView
            r29 = 1134690304(0x43a20000, float:324.0)
            r30 = 1096810496(0x41600000, float:14.0)
            android.widget.FrameLayout$LayoutParams r14 = org.telegram.ui.Components.LayoutHelper.createFrame(r24, r25, r26, r27, r28, r29, r30)
            r0.addView(r7, r14)
            org.telegram.ui.Components.RecyclerListView r0 = r8.tabletVideoGridView
            org.telegram.ui.GroupCallTabletGridAdapter r7 = new org.telegram.ui.GroupCallTabletGridAdapter
            int r14 = r8.currentAccount
            r7.<init>(r10, r14, r8)
            r8.tabletGridAdapter = r7
            r0.setAdapter(r7)
            androidx.recyclerview.widget.GridLayoutManager r0 = new androidx.recyclerview.widget.GridLayoutManager
            r7 = 1
            r14 = 0
            r0.<init>(r9, r13, r7, r14)
            r14 = r0
            org.telegram.ui.Components.RecyclerListView r0 = r8.tabletVideoGridView
            r0.setLayoutManager(r14)
            org.telegram.ui.GroupCallActivity$14 r0 = new org.telegram.ui.GroupCallActivity$14
            r0.<init>()
            r14.setSpanSizeLookup(r0)
            org.telegram.ui.Components.RecyclerListView r0 = r8.tabletVideoGridView
            org.telegram.ui.GroupCallActivity$$ExternalSyntheticLambda57 r7 = new org.telegram.ui.GroupCallActivity$$ExternalSyntheticLambda57
            r7.<init>(r8)
            r0.setOnItemClickListener((org.telegram.ui.Components.RecyclerListView.OnItemClickListener) r7)
            androidx.recyclerview.widget.DefaultItemAnimator r0 = new androidx.recyclerview.widget.DefaultItemAnimator
            r0.<init>()
            r7 = 0
            r0.setDelayAnimations(r7)
            org.telegram.ui.Components.CubicBezierInterpolator r7 = org.telegram.ui.Components.CubicBezierInterpolator.DEFAULT
            r0.setTranslationInterpolator(r7)
            r25 = r14
            r13 = 350(0x15e, double:1.73E-321)
            r0.setRemoveDuration(r13)
            r0.setAddDuration(r13)
            r0.setMoveDuration(r13)
            org.telegram.ui.GroupCallActivity$15 r7 = new org.telegram.ui.GroupCallActivity$15
            r7.<init>()
            r13 = r7
            org.telegram.ui.Components.RecyclerListView r0 = r8.tabletVideoGridView
            r0.setItemAnimator(r13)
            org.telegram.ui.Components.RecyclerListView r0 = r8.tabletVideoGridView
            org.telegram.ui.GroupCallActivity$16 r7 = new org.telegram.ui.GroupCallActivity$16
            r7.<init>()
            r0.setOnScrollListener(r7)
            org.telegram.ui.GroupCallTabletGridAdapter r0 = r8.tabletGridAdapter
            org.telegram.ui.Components.RecyclerListView r7 = r8.tabletVideoGridView
            r14 = 0
            r0.setVisibility(r7, r14, r14)
            org.telegram.ui.Components.RecyclerListView r0 = r8.tabletVideoGridView
            r7 = 8
            r0.setVisibility(r7)
            org.telegram.ui.GroupCallActivity$17 r0 = new org.telegram.ui.GroupCallActivity$17
            r0.<init>(r9)
            r8.buttonsContainer = r0
            java.lang.String r0 = "voipgroup_unmuteButton2"
            int r14 = org.telegram.ui.ActionBar.Theme.getColor(r0)
            int r7 = android.graphics.Color.red(r14)
            int r0 = android.graphics.Color.green(r14)
            r26 = r13
            int r13 = android.graphics.Color.blue(r14)
            r27 = r2
            android.graphics.Matrix r2 = new android.graphics.Matrix
            r2.<init>()
            r8.radialMatrix = r2
            android.graphics.RadialGradient r2 = new android.graphics.RadialGradient
            r34 = 0
            r35 = 0
            r28 = 1126170624(0x43200000, float:160.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r28)
            float r3 = (float) r3
            r4 = 2
            int[] r5 = new int[r4]
            r4 = 50
            int r4 = android.graphics.Color.argb(r4, r7, r0, r13)
            r6 = 0
            r5[r6] = r4
            int r4 = android.graphics.Color.argb(r6, r7, r0, r13)
            r6 = 1
            r5[r6] = r4
            r38 = 0
            android.graphics.Shader$TileMode r39 = android.graphics.Shader.TileMode.CLAMP
            r33 = r2
            r36 = r3
            r37 = r5
            r33.<init>(r34, r35, r36, r37, r38, r39)
            r8.radialGradient = r2
            android.graphics.Paint r2 = new android.graphics.Paint
            r3 = 1
            r2.<init>(r3)
            r8.radialPaint = r2
            android.graphics.RadialGradient r3 = r8.radialGradient
            r2.setShader(r3)
            org.telegram.ui.Components.BlobDrawable r2 = new org.telegram.ui.Components.BlobDrawable
            r3 = 9
            r2.<init>(r3)
            r8.tinyWaveDrawable = r2
            org.telegram.ui.Components.BlobDrawable r3 = new org.telegram.ui.Components.BlobDrawable
            r6 = 12
            r3.<init>(r6)
            r8.bigWaveDrawable = r3
            r4 = 1115160576(0x42780000, float:62.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r4 = (float) r4
            r2.minRadius = r4
            r4 = 1116733440(0x42900000, float:72.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r4 = (float) r4
            r2.maxRadius = r4
            r2.generateBlob()
            r4 = 1115815936(0x42820000, float:65.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r4 = (float) r4
            r3.minRadius = r4
            r4 = 1117126656(0x42960000, float:75.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r4 = (float) r4
            r3.maxRadius = r4
            r3.generateBlob()
            android.graphics.Paint r2 = r2.paint
            java.lang.String r4 = "voipgroup_unmuteButton"
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r4)
            r5 = 38
            int r4 = androidx.core.graphics.ColorUtils.setAlphaComponent(r4, r5)
            r2.setColor(r4)
            android.graphics.Paint r2 = r3.paint
            java.lang.String r3 = "voipgroup_unmuteButton"
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            r4 = 76
            int r3 = androidx.core.graphics.ColorUtils.setAlphaComponent(r3, r4)
            r2.setColor(r3)
            org.telegram.ui.Components.voip.VoIPToggleButton r2 = new org.telegram.ui.Components.voip.VoIPToggleButton
            r2.<init>(r9)
            r8.soundButton = r2
            r3 = 1
            r2.setCheckable(r3)
            org.telegram.ui.Components.voip.VoIPToggleButton r2 = r8.soundButton
            r2.setTextSize(r6)
            android.widget.FrameLayout r2 = r8.buttonsContainer
            org.telegram.ui.Components.voip.VoIPToggleButton r3 = r8.soundButton
            r4 = 68
            r5 = 1117782016(0x42a00000, float:80.0)
            android.widget.FrameLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createFrame(r4, r5)
            r2.addView(r3, r6)
            org.telegram.ui.Components.voip.VoIPToggleButton r2 = r8.soundButton
            org.telegram.ui.GroupCallActivity$$ExternalSyntheticLambda5 r3 = new org.telegram.ui.GroupCallActivity$$ExternalSyntheticLambda5
            r3.<init>(r8)
            r2.setOnClickListener(r3)
            org.telegram.ui.Components.voip.VoIPToggleButton r2 = new org.telegram.ui.Components.voip.VoIPToggleButton
            r2.<init>(r9)
            r8.cameraButton = r2
            r3 = 1
            r2.setCheckable(r3)
            org.telegram.ui.Components.voip.VoIPToggleButton r2 = r8.cameraButton
            r3 = 12
            r2.setTextSize(r3)
            org.telegram.ui.Components.voip.VoIPToggleButton r2 = r8.cameraButton
            r3 = 0
            r2.showText(r3, r3)
            org.telegram.ui.Components.voip.VoIPToggleButton r2 = r8.cameraButton
            r3 = 1080033280(0x40600000, float:3.5)
            float r3 = org.telegram.messenger.AndroidUtilities.dpf2(r3)
            float r3 = -r3
            r2.setCrossOffset(r3)
            org.telegram.ui.Components.voip.VoIPToggleButton r2 = r8.cameraButton
            r3 = 8
            r2.setVisibility(r3)
            android.widget.FrameLayout r2 = r8.buttonsContainer
            org.telegram.ui.Components.voip.VoIPToggleButton r3 = r8.cameraButton
            android.widget.FrameLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createFrame(r4, r5)
            r2.addView(r3, r6)
            org.telegram.ui.Components.voip.VoIPToggleButton r2 = new org.telegram.ui.Components.voip.VoIPToggleButton
            r2.<init>(r9)
            r8.flipButton = r2
            r3 = 1
            r2.setCheckable(r3)
            org.telegram.ui.Components.voip.VoIPToggleButton r2 = r8.flipButton
            r3 = 12
            r2.setTextSize(r3)
            org.telegram.ui.Components.voip.VoIPToggleButton r2 = r8.flipButton
            r3 = 0
            r2.showText(r3, r3)
            org.telegram.ui.Components.RLottieImageView r2 = new org.telegram.ui.Components.RLottieImageView
            r2.<init>(r9)
            r6 = r2
            org.telegram.ui.Components.voip.VoIPToggleButton r2 = r8.flipButton
            r33 = 32
            r34 = 1107296256(0x42000000, float:32.0)
            r35 = 0
            r36 = 1099956224(0x41900000, float:18.0)
            r37 = 1092616192(0x41200000, float:10.0)
            r38 = 1099956224(0x41900000, float:18.0)
            r39 = 0
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r33, r34, r35, r36, r37, r38, r39)
            r2.addView(r6, r3)
            org.telegram.ui.Components.RLottieDrawable r2 = new org.telegram.ui.Components.RLottieDrawable
            r34 = 2131558414(0x7f0d000e, float:1.8742143E38)
            r3 = 1103101952(0x41CLASSNAME, float:24.0)
            int r36 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r37 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r38 = 1
            r39 = 0
            java.lang.String r35 = "NUM"
            r33 = r2
            r33.<init>(r34, r35, r36, r37, r38, r39)
            r8.flipIcon = r2
            r6.setAnimation(r2)
            org.telegram.ui.Components.voip.VoIPToggleButton r2 = r8.flipButton
            org.telegram.ui.GroupCallActivity$$ExternalSyntheticLambda6 r3 = new org.telegram.ui.GroupCallActivity$$ExternalSyntheticLambda6
            r3.<init>(r8)
            r2.setOnClickListener(r3)
            org.telegram.ui.Components.voip.VoIPToggleButton r2 = r8.flipButton
            r3 = 8
            r2.setVisibility(r3)
            android.widget.FrameLayout r2 = r8.buttonsContainer
            org.telegram.ui.Components.voip.VoIPToggleButton r3 = r8.flipButton
            r20 = r0
            android.widget.FrameLayout$LayoutParams r0 = org.telegram.ui.Components.LayoutHelper.createFrame(r4, r5)
            r2.addView(r3, r0)
            org.telegram.ui.Components.voip.VoIPToggleButton r0 = new org.telegram.ui.Components.voip.VoIPToggleButton
            r0.<init>(r9)
            r8.leaveButton = r0
            r2 = 0
            r0.setDrawBackground(r2)
            org.telegram.ui.Components.voip.VoIPToggleButton r0 = r8.leaveButton
            r3 = 12
            r0.setTextSize(r3)
            org.telegram.ui.Components.voip.VoIPToggleButton r0 = r8.leaveButton
            org.telegram.messenger.ChatObject$Call r2 = r8.call
            if (r2 == 0) goto L_0x06d3
            boolean r2 = r43.isRtmpStream()
            if (r2 == 0) goto L_0x06d3
            r2 = 2131165986(0x7var_, float:1.7946205E38)
            r34 = 2131165986(0x7var_, float:1.7946205E38)
            goto L_0x06d9
        L_0x06d3:
            r2 = 2131165307(0x7var_b, float:1.7944827E38)
            r34 = 2131165307(0x7var_b, float:1.7944827E38)
        L_0x06d9:
            r35 = -1
            java.lang.String r2 = "voipgroup_leaveButton"
            int r36 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r37 = 1050253722(0x3e99999a, float:0.3)
            r38 = 0
            r2 = 2131629069(0x7f0e140d, float:1.8885449E38)
            java.lang.String r3 = "VoipGroupLeave"
            java.lang.String r39 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r40 = 0
            r41 = 0
            r33 = r0
            r33.setData(r34, r35, r36, r37, r38, r39, r40, r41)
            android.widget.FrameLayout r0 = r8.buttonsContainer
            org.telegram.ui.Components.voip.VoIPToggleButton r2 = r8.leaveButton
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r4, r5)
            r0.addView(r2, r3)
            org.telegram.ui.Components.voip.VoIPToggleButton r0 = r8.leaveButton
            org.telegram.ui.GroupCallActivity$$ExternalSyntheticLambda14 r2 = new org.telegram.ui.GroupCallActivity$$ExternalSyntheticLambda14
            r2.<init>(r8, r9)
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
            r3 = 49
            r4 = 122(0x7a, float:1.71E-43)
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r4, (int) r4, (int) r3)
            r0.addView(r2, r3)
            org.telegram.ui.Components.RLottieImageView r0 = r8.muteButton
            org.telegram.ui.GroupCallActivity$19 r2 = new org.telegram.ui.GroupCallActivity$19
            r2.<init>()
            r0.setOnClickListener(r2)
            r0 = 1108869120(0x42180000, float:38.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r0)
            android.widget.ImageView r0 = new android.widget.ImageView
            r0.<init>(r9)
            r8.expandButton = r0
            r2 = 1036831949(0x3dcccccd, float:0.1)
            r0.setScaleX(r2)
            android.widget.ImageView r0 = r8.expandButton
            r0.setScaleY(r2)
            android.widget.ImageView r0 = r8.expandButton
            r3 = 0
            r0.setAlpha(r3)
            android.widget.ImageView r0 = r8.expandButton
            r3 = 2131166207(0x7var_ff, float:1.7946653E38)
            r0.setImageResource(r3)
            android.widget.ImageView r0 = r8.expandButton
            r0.setPadding(r5, r5, r5, r5)
            android.widget.FrameLayout r0 = r8.buttonsContainer
            android.widget.ImageView r3 = r8.expandButton
            r2 = 49
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r4, (int) r4, (int) r2)
            r0.addView(r3, r2)
            android.widget.ImageView r0 = new android.widget.ImageView
            r0.<init>(r9)
            r8.minimizeButton = r0
            r2 = 1036831949(0x3dcccccd, float:0.1)
            r0.setScaleX(r2)
            android.widget.ImageView r0 = r8.minimizeButton
            r0.setScaleY(r2)
            android.widget.ImageView r0 = r8.minimizeButton
            r2 = 0
            r0.setAlpha(r2)
            android.widget.ImageView r0 = r8.minimizeButton
            r2 = 2131166211(0x7var_, float:1.794666E38)
            r0.setImageResource(r2)
            android.widget.ImageView r0 = r8.minimizeButton
            r0.setPadding(r5, r5, r5, r5)
            android.widget.FrameLayout r0 = r8.buttonsContainer
            android.widget.ImageView r2 = r8.minimizeButton
            r3 = 49
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r4, (int) r4, (int) r3)
            r0.addView(r2, r3)
            org.telegram.messenger.ChatObject$Call r0 = r8.call
            if (r0 == 0) goto L_0x07cc
            boolean r0 = r43.isRtmpStream()
            if (r0 == 0) goto L_0x07cc
            org.telegram.messenger.ChatObject$Call r0 = r8.call
            boolean r0 = r0.isScheduled()
            if (r0 != 0) goto L_0x07cc
            android.widget.ImageView r0 = r8.expandButton
            r2 = 1065353216(0x3var_, float:1.0)
            r0.setAlpha(r2)
            android.widget.ImageView r0 = r8.expandButton
            r0.setScaleX(r2)
            android.widget.ImageView r0 = r8.expandButton
            r0.setScaleY(r2)
            org.telegram.ui.Components.RLottieImageView r0 = r8.muteButton
            r2 = 0
            r0.setAlpha(r2)
        L_0x07cc:
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
        L_0x07ef:
            r2 = 2
            if (r0 >= r2) goto L_0x0835
            android.widget.TextView[] r2 = r8.muteLabel
            android.widget.TextView r3 = new android.widget.TextView
            r3.<init>(r9)
            r2[r0] = r3
            android.widget.TextView[] r2 = r8.muteLabel
            r2 = r2[r0]
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r2.setTextColor(r3)
            android.widget.TextView[] r2 = r8.muteLabel
            r2 = r2[r0]
            r3 = 1099956224(0x41900000, float:18.0)
            r4 = 1
            r2.setTextSize(r4, r3)
            android.widget.TextView[] r2 = r8.muteLabel
            r2 = r2[r0]
            r2.setGravity(r4)
            android.widget.FrameLayout r2 = r8.buttonsContainer
            android.widget.TextView[] r3 = r8.muteLabel
            r3 = r3[r0]
            r33 = -2
            r34 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r35 = 81
            r36 = 0
            r37 = 0
            r38 = 0
            r39 = 1104150528(0x41d00000, float:26.0)
            android.widget.FrameLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r33, r34, r35, r36, r37, r38, r39)
            r2.addView(r3, r4)
            int r0 = r0 + 1
            goto L_0x07ef
        L_0x0835:
            org.telegram.ui.ActionBar.ActionBar r0 = r8.actionBar
            r2 = 0
            r0.setAlpha(r2)
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
            org.telegram.ui.ActionBar.ActionBar r0 = r8.actionBar
            org.telegram.ui.ActionBar.SimpleTextView r0 = r0.getAdditionalSubtitleTextView()
            r2 = 1101004800(0x41a00000, float:20.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            float r2 = (float) r2
            r0.setTranslationY(r2)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = new org.telegram.ui.ActionBar.ActionBarMenuItem
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r3 = 0
            r4 = 0
            r0.<init>(r9, r3, r4, r2)
            r8.otherItem = r0
            r0.setLongClickEnabled(r4)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r8.otherItem
            r2 = 2131165453(0x7var_d, float:1.7945124E38)
            r0.setIcon((int) r2)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r8.otherItem
            r2 = 2131624003(0x7f0e0043, float:1.8875173E38)
            java.lang.String r3 = "AccDescrMoreOptions"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setContentDescription(r2)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r8.otherItem
            r2 = 2
            r0.setSubMenuOpenSide(r2)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r8.otherItem
            org.telegram.ui.GroupCallActivity$$ExternalSyntheticLambda50 r2 = new org.telegram.ui.GroupCallActivity$$ExternalSyntheticLambda50
            r2.<init>(r8)
            r0.setDelegate(r2)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r8.otherItem
            java.lang.String r2 = "voipgroup_actionBarItemsSelector"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r3 = 6
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r2, r3)
            r0.setBackgroundDrawable(r2)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r8.otherItem
            org.telegram.ui.GroupCallActivity$$ExternalSyntheticLambda15 r2 = new org.telegram.ui.GroupCallActivity$$ExternalSyntheticLambda15
            r2.<init>(r8, r9)
            r0.setOnClickListener(r2)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r8.otherItem
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r3 = 0
            r0.setPopupItemsColor(r2, r3)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r8.otherItem
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r4 = 1
            r0.setPopupItemsColor(r2, r4)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = new org.telegram.ui.ActionBar.ActionBarMenuItem
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r4 = 0
            r0.<init>(r9, r4, r3, r2)
            r8.pipItem = r0
            r0.setLongClickEnabled(r3)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r8.pipItem
            org.telegram.messenger.ChatObject$Call r2 = r8.call
            if (r2 == 0) goto L_0x0914
            boolean r2 = r43.isRtmpStream()
            if (r2 == 0) goto L_0x0914
            r2 = 2131165478(0x7var_, float:1.7945174E38)
            goto L_0x0917
        L_0x0914:
            r2 = 2131165981(0x7var_d, float:1.7946194E38)
        L_0x0917:
            r0.setIcon((int) r2)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r8.pipItem
            r2 = 2131624040(0x7f0e0068, float:1.8875248E38)
            java.lang.String r3 = "AccDescrPipMode"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setContentDescription(r2)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r8.pipItem
            java.lang.String r2 = "voipgroup_actionBarItemsSelector"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r3 = 6
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r2, r3)
            r0.setBackgroundDrawable(r2)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r8.pipItem
            org.telegram.ui.GroupCallActivity$$ExternalSyntheticLambda7 r2 = new org.telegram.ui.GroupCallActivity$$ExternalSyntheticLambda7
            r2.<init>(r8)
            r0.setOnClickListener(r2)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = new org.telegram.ui.ActionBar.ActionBarMenuItem
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r3 = 0
            r4 = 0
            r0.<init>(r9, r3, r4, r2)
            r8.screenShareItem = r0
            r0.setLongClickEnabled(r4)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r8.screenShareItem
            r2 = 2131165920(0x7var_e0, float:1.794607E38)
            r0.setIcon((int) r2)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r8.screenShareItem
            r2 = 2131624040(0x7f0e0068, float:1.8875248E38)
            java.lang.String r3 = "AccDescrPipMode"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r0.setContentDescription(r2)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r8.screenShareItem
            java.lang.String r2 = "voipgroup_actionBarItemsSelector"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r3 = 6
            android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r2, r3)
            r0.setBackgroundDrawable(r2)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r8.screenShareItem
            org.telegram.ui.GroupCallActivity$$ExternalSyntheticLambda8 r2 = new org.telegram.ui.GroupCallActivity$$ExternalSyntheticLambda8
            r2.<init>(r8)
            r0.setOnClickListener(r2)
            org.telegram.ui.GroupCallActivity$20 r0 = new org.telegram.ui.GroupCallActivity$20
            r0.<init>(r9, r9)
            r8.titleTextView = r0
            org.telegram.ui.GroupCallActivity$21 r0 = new org.telegram.ui.GroupCallActivity$21
            r0.<init>(r9)
            r8.actionBarBackground = r0
            r2 = 0
            r0.setAlpha(r2)
            android.view.ViewGroup r0 = r8.containerView
            android.view.View r2 = r8.actionBarBackground
            r33 = -1
            r34 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r35 = 51
            r36 = 0
            r37 = 0
            r38 = 0
            r39 = 0
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r33, r34, r35, r36, r37, r38, r39)
            r0.addView(r2, r3)
            android.view.ViewGroup r0 = r8.containerView
            org.telegram.ui.Components.AudioPlayerAlert$ClippingTextViewSwitcher r2 = r8.titleTextView
            r33 = -2
            r36 = 1102577664(0x41b80000, float:23.0)
            r38 = 1111490560(0x42400000, float:48.0)
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r33, r34, r35, r36, r37, r38, r39)
            r0.addView(r2, r3)
            android.view.ViewGroup r0 = r8.containerView
            org.telegram.ui.ActionBar.ActionBar r2 = r8.actionBar
            r33 = -1
            r36 = 0
            r38 = 0
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r33, r34, r35, r36, r37, r38, r39)
            r0.addView(r2, r3)
            android.widget.LinearLayout r0 = new android.widget.LinearLayout
            r0.<init>(r9)
            r8.menuItemsContainer = r0
            r2 = 0
            r0.setOrientation(r2)
            org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r8.screenShareItem
            r3 = 48
            android.widget.LinearLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createLinear(r3, r3)
            r0.addView(r2, r4)
            org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r8.pipItem
            android.widget.LinearLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createLinear(r3, r3)
            r0.addView(r2, r4)
            org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r8.otherItem
            android.widget.LinearLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createLinear(r3, r3)
            r0.addView(r2, r4)
            android.view.ViewGroup r2 = r8.containerView
            r4 = -2
            r18 = r5
            r5 = 53
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r4, (int) r3, (int) r5)
            r2.addView(r0, r3)
            android.view.View r0 = new android.view.View
            r0.<init>(r9)
            r8.actionBarShadow = r0
            r3 = 0
            r0.setAlpha(r3)
            android.view.View r0 = r8.actionBarShadow
            java.lang.String r2 = "dialogShadowLine"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r0.setBackgroundColor(r2)
            android.view.ViewGroup r0 = r8.containerView
            android.view.View r2 = r8.actionBarShadow
            r4 = 1065353216(0x3var_, float:1.0)
            r5 = -1
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r5, r4)
            r0.addView(r2, r3)
            r0 = 0
        L_0x0a2a:
            r2 = 2
            if (r0 >= r2) goto L_0x0a76
            org.telegram.ui.Components.UndoView[] r2 = r8.undoView
            org.telegram.ui.GroupCallActivity$22 r3 = new org.telegram.ui.GroupCallActivity$22
            r3.<init>(r9)
            r2[r0] = r3
            org.telegram.ui.Components.UndoView[] r2 = r8.undoView
            r2 = r2[r0]
            r3 = 1092616192(0x41200000, float:10.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r3 = (float) r3
            r2.setAdditionalTranslationY(r3)
            int r2 = android.os.Build.VERSION.SDK_INT
            r3 = 21
            if (r2 < r3) goto L_0x0a58
            org.telegram.ui.Components.UndoView[] r2 = r8.undoView
            r2 = r2[r0]
            r3 = 1084227584(0x40a00000, float:5.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            float r3 = (float) r3
            r2.setTranslationZ(r3)
        L_0x0a58:
            android.view.ViewGroup r2 = r8.containerView
            org.telegram.ui.Components.UndoView[] r3 = r8.undoView
            r3 = r3[r0]
            r33 = -1
            r34 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r35 = 83
            r36 = 1090519040(0x41000000, float:8.0)
            r37 = 0
            r38 = 1090519040(0x41000000, float:8.0)
            r39 = 1090519040(0x41000000, float:8.0)
            android.widget.FrameLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createFrame(r33, r34, r35, r36, r37, r38, r39)
            r2.addView(r3, r4)
            int r0 = r0 + 1
            goto L_0x0a2a
        L_0x0a76:
            org.telegram.ui.Cells.AccountSelectCell r0 = new org.telegram.ui.Cells.AccountSelectCell
            r2 = 1
            r0.<init>(r9, r2)
            r8.accountSelectCell = r0
            r2 = 2131230954(0x7var_ea, float:1.8077975E38)
            r3 = 240(0xf0, float:3.36E-43)
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            r0.setTag(r2, r3)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r8.otherItem
            org.telegram.ui.Cells.AccountSelectCell r2 = r8.accountSelectCell
            r3 = -2
            r4 = 1111490560(0x42400000, float:48.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r5 = 8
            r0.addSubItem((int) r5, (android.view.View) r2, (int) r3, (int) r4)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r8.otherItem
            r2 = 0
            r0.setShowSubmenuByMove(r2)
            org.telegram.ui.Cells.AccountSelectCell r0 = r8.accountSelectCell
            java.lang.String r3 = "voipgroup_listSelector"
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            r4 = 6
            android.graphics.drawable.Drawable r3 = org.telegram.ui.ActionBar.Theme.createRadSelectorDrawable(r3, r4, r4)
            r0.setBackground(r3)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r8.otherItem
            android.view.View r0 = r0.addGap(r2)
            r8.accountGap = r0
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r8.otherItem
            r3 = 2131629030(0x7f0e13e6, float:1.888537E38)
            java.lang.String r4 = "VoipGroupAllCanSpeak"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r4 = 1
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r0 = r0.addSubItem((int) r4, (int) r2, (java.lang.CharSequence) r3, (boolean) r4)
            r8.everyoneItem = r0
            r0.updateSelectorBackground(r4, r2)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r8.otherItem
            r3 = 2131629081(0x7f0e1419, float:1.8885473E38)
            java.lang.String r5 = "VoipGroupOnlyAdminsCanSpeak"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r5, r3)
            r5 = 2
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r0 = r0.addSubItem((int) r5, (int) r2, (java.lang.CharSequence) r3, (boolean) r4)
            r8.adminItem = r0
            r0.updateSelectorBackground(r2, r4)
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r0 = r8.everyoneItem
            java.lang.String r2 = "voipgroup_checkMenu"
            r0.setCheckColor(r2)
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r0 = r8.everyoneItem
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            int r4 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r0.setColors(r3, r4)
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r0 = r8.adminItem
            r0.setCheckColor(r2)
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r0 = r8.adminItem
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r0.setColors(r3, r2)
            android.graphics.Paint r0 = new android.graphics.Paint
            r2 = 1
            r0.<init>(r2)
            r5 = r0
            int r0 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r5.setColor(r0)
            android.graphics.Paint$Style r0 = android.graphics.Paint.Style.STROKE
            r5.setStyle(r0)
            r0 = 1069547520(0x3fCLASSNAME, float:1.5)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            float r0 = (float) r0
            r5.setStrokeWidth(r0)
            android.graphics.Paint$Cap r0 = android.graphics.Paint.Cap.ROUND
            r5.setStrokeCap(r0)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r8.otherItem
            r34 = 10
            r35 = 2131165982(0x7var_e, float:1.7946196E38)
            r36 = 0
            r2 = 2131629032(0x7f0e13e8, float:1.8885373E38)
            java.lang.String r3 = "VoipGroupAudio"
            java.lang.String r37 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r38 = 1
            r39 = 0
            r33 = r0
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r0 = r33.addSubItem(r34, r35, r36, r37, r38, r39)
            r8.soundItem = r0
            r2 = 56
            r0.setItemHeight(r2)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r8.otherItem
            r34 = 11
            r35 = 2131165829(0x7var_, float:1.7945886E38)
            r2 = 2131629154(0x7f0e1462, float:1.888562E38)
            java.lang.String r3 = "VoipNoiseCancellation"
            java.lang.String r37 = org.telegram.messenger.LocaleController.getString(r3, r2)
            r33 = r0
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r0 = r33.addSubItem(r34, r35, r36, r37, r38, r39)
            r8.noiseItem = r0
            r2 = 56
            r0.setItemHeight(r2)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r8.otherItem
            java.lang.String r2 = "voipgroup_actionBar"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r3 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            r4 = 1050253722(0x3e99999a, float:0.3)
            int r2 = androidx.core.graphics.ColorUtils.blendARGB(r2, r3, r4)
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
            r34 = 6
            r35 = 2131165714(0x7var_, float:1.7945653E38)
            org.telegram.ui.GroupCallActivity$RecordCallDrawable r2 = r8.recordCallDrawable
            org.telegram.tgnet.TLRPC$Chat r3 = r8.currentChat
            boolean r3 = org.telegram.messenger.ChatObject.isChannelOrGiga(r3)
            if (r3 == 0) goto L_0x0bab
            r3 = 2131628967(0x7f0e13a7, float:1.8885242E38)
            java.lang.String r4 = "VoipChannelEditTitle"
            goto L_0x0bb0
        L_0x0bab:
            r3 = 2131629048(0x7f0e13f8, float:1.8885406E38)
            java.lang.String r4 = "VoipGroupEditTitle"
        L_0x0bb0:
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r37 = r3
            r38 = 1
            r39 = 0
            r33 = r0
            r36 = r2
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r0 = r33.addSubItem(r34, r35, r36, r37, r38, r39)
            r8.editTitleItem = r0
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r8.otherItem
            r34 = 7
            r35 = 2131165841(0x7var_, float:1.794591E38)
            org.telegram.ui.GroupCallActivity$RecordCallDrawable r2 = r8.recordCallDrawable
            r3 = 2131629047(0x7f0e13f7, float:1.8885404E38)
            java.lang.String r4 = "VoipGroupEditPermissions"
            java.lang.String r37 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r38 = 0
            r33 = r0
            r36 = r2
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r0 = r33.addSubItem(r34, r35, r36, r37, r38, r39)
            r8.permissionItem = r0
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r8.otherItem
            r2 = 3
            r3 = 2131165782(0x7var_, float:1.794579E38)
            r4 = 2131629099(0x7f0e142b, float:1.888551E38)
            r17 = r5
            java.lang.String r5 = "VoipGroupShareInviteLink"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r0 = r0.addSubItem((int) r2, (int) r3, (java.lang.CharSequence) r4)
            r8.inviteItem = r0
            org.telegram.ui.GroupCallActivity$RecordCallDrawable r0 = new org.telegram.ui.GroupCallActivity$RecordCallDrawable
            r0.<init>()
            r8.recordCallDrawable = r0
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r8.otherItem
            r2 = 9
            r3 = 2131165920(0x7var_e0, float:1.794607E38)
            r4 = 2131629009(0x7f0e13d1, float:1.8885327E38)
            java.lang.String r5 = "VoipChatStartScreenCapture"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r0 = r0.addSubItem((int) r2, (int) r3, (java.lang.CharSequence) r4)
            r8.screenItem = r0
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r8.otherItem
            r34 = 5
            r35 = 0
            org.telegram.ui.GroupCallActivity$RecordCallDrawable r2 = r8.recordCallDrawable
            r3 = 2131629088(0x7f0e1420, float:1.8885487E38)
            java.lang.String r4 = "VoipGroupRecordCall"
            java.lang.String r37 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r38 = 1
            r33 = r0
            r36 = r2
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r0 = r33.addSubItem(r34, r35, r36, r37, r38, r39)
            r8.recordItem = r0
            org.telegram.ui.GroupCallActivity$RecordCallDrawable r2 = r8.recordCallDrawable
            android.widget.ImageView r0 = r0.getImageView()
            r2.setParentView(r0)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r8.otherItem
            r2 = 4
            r3 = 2131165726(0x7var_e, float:1.7945677E38)
            org.telegram.tgnet.TLRPC$Chat r4 = r8.currentChat
            boolean r4 = org.telegram.messenger.ChatObject.isChannelOrGiga(r4)
            if (r4 == 0) goto L_0x0CLASSNAME
            r4 = 2131628970(0x7f0e13aa, float:1.8885248E38)
            java.lang.String r5 = "VoipChannelEndChat"
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            r4 = 2131629052(0x7f0e13fc, float:1.8885414E38)
            java.lang.String r5 = "VoipGroupEndChat"
        L_0x0CLASSNAME:
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r0 = r0.addSubItem((int) r2, (int) r3, (java.lang.CharSequence) r4)
            r8.leaveItem = r0
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r8.otherItem
            java.lang.String r2 = "voipgroup_listSelector"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r0.setPopupItemsSelectorColor(r2)
            org.telegram.ui.ActionBar.ActionBarMenuItem r0 = r8.otherItem
            org.telegram.ui.ActionBar.ActionBarPopupWindow$ActionBarPopupWindowLayout r0 = r0.getPopupLayout()
            r2 = 1
            r0.setFitItems(r2)
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r0 = r8.soundItem
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.setColors(r2, r3)
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r0 = r8.noiseItem
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.setColors(r2, r3)
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r0 = r8.leaveItem
            java.lang.String r2 = "voipgroup_leaveCallMenu"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            java.lang.String r3 = "voipgroup_leaveCallMenu"
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            r0.setColors(r2, r3)
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r0 = r8.inviteItem
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.setColors(r2, r3)
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r0 = r8.editTitleItem
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.setColors(r2, r3)
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r0 = r8.permissionItem
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.setColors(r2, r3)
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r0 = r8.recordItem
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.setColors(r2, r3)
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r0 = r8.screenItem
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.setColors(r2, r1)
            org.telegram.messenger.ChatObject$Call r0 = r8.call
            if (r0 == 0) goto L_0x0ce7
            r43.initCreatedGroupCall()
        L_0x0ce7:
            android.graphics.Paint r0 = r8.leaveBackgroundPaint
            java.lang.String r1 = "voipgroup_leaveButton"
            int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
            r0.setColor(r1)
            r0 = 0
            r8.updateTitle(r0)
            org.telegram.ui.ActionBar.ActionBar r0 = r8.actionBar
            org.telegram.ui.ActionBar.SimpleTextView r0 = r0.getTitleTextView()
            org.telegram.ui.GroupCallActivity$$ExternalSyntheticLambda9 r1 = new org.telegram.ui.GroupCallActivity$$ExternalSyntheticLambda9
            r1.<init>(r8)
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
            r5 = r0
            r5.setOrientation(r1)
            org.telegram.ui.Components.RecyclerListView r0 = r8.fullscreenUsersListView
            r0.setLayoutManager(r5)
            org.telegram.ui.Components.RecyclerListView r0 = r8.fullscreenUsersListView
            org.telegram.ui.Components.GroupCallFullscreenAdapter r1 = new org.telegram.ui.Components.GroupCallFullscreenAdapter
            int r2 = r8.currentAccount
            r1.<init>(r10, r2, r8)
            r8.fullscreenAdapter = r1
            r0.setAdapter(r1)
            org.telegram.ui.Components.GroupCallFullscreenAdapter r0 = r8.fullscreenAdapter
            org.telegram.ui.Components.RecyclerListView r1 = r8.fullscreenUsersListView
            r2 = 0
            r0.setVisibility(r1, r2)
            org.telegram.ui.Components.RecyclerListView r0 = r8.fullscreenUsersListView
            org.telegram.ui.GroupCallActivity$$ExternalSyntheticLambda58 r1 = new org.telegram.ui.GroupCallActivity$$ExternalSyntheticLambda58
            r1.<init>(r8)
            r0.setOnItemClickListener((org.telegram.ui.Components.RecyclerListView.OnItemClickListener) r1)
            org.telegram.ui.Components.RecyclerListView r0 = r8.fullscreenUsersListView
            org.telegram.ui.GroupCallActivity$$ExternalSyntheticLambda61 r1 = new org.telegram.ui.GroupCallActivity$$ExternalSyntheticLambda61
            r1.<init>(r8)
            r0.setOnItemLongClickListener((org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener) r1)
            org.telegram.ui.Components.RecyclerListView r0 = r8.fullscreenUsersListView
            r1 = 8
            r0.setVisibility(r1)
            org.telegram.ui.Components.RecyclerListView r0 = r8.fullscreenUsersListView
            org.telegram.ui.GroupCallActivity$26 r1 = new org.telegram.ui.GroupCallActivity$26
            r1.<init>()
            r0.addItemDecoration(r1)
            org.telegram.ui.GroupCallActivity$27 r4 = new org.telegram.ui.GroupCallActivity$27
            org.telegram.ui.Components.RecyclerListView r3 = r8.listView
            org.telegram.ui.Components.RecyclerListView r2 = r8.fullscreenUsersListView
            java.util.ArrayList<org.telegram.ui.Components.voip.GroupCallMiniTextureView> r1 = r8.attachedRenderers
            org.telegram.messenger.ChatObject$Call r0 = r8.call
            r29 = r0
            r0 = r4
            r30 = r1
            r1 = r43
            r31 = r2
            r2 = r44
            r10 = 0
            r21 = 12
            r10 = r4
            r4 = r31
            r22 = r5
            r31 = -1
            r42 = r18
            r18 = r17
            r17 = r42
            r5 = r30
            r21 = r6
            r30 = r13
            r13 = 12
            r6 = r29
            r29 = r7
            r13 = -1
            r7 = r43
            r0.<init>(r2, r3, r4, r5, r6, r7)
            r8.renderersContainer = r10
            r0 = 0
            r10.setClipChildren(r0)
            org.telegram.ui.Components.GroupCallFullscreenAdapter r0 = r8.fullscreenAdapter
            java.util.ArrayList<org.telegram.ui.Components.voip.GroupCallMiniTextureView> r1 = r8.attachedRenderers
            org.telegram.ui.Components.voip.GroupCallRenderersContainer r2 = r8.renderersContainer
            r0.setRenderersPool(r1, r2)
            org.telegram.ui.Components.RecyclerListView r0 = r8.tabletVideoGridView
            if (r0 == 0) goto L_0x0ddc
            org.telegram.ui.GroupCallTabletGridAdapter r0 = r8.tabletGridAdapter
            java.util.ArrayList<org.telegram.ui.Components.voip.GroupCallMiniTextureView> r1 = r8.attachedRenderers
            org.telegram.ui.Components.voip.GroupCallRenderersContainer r2 = r8.renderersContainer
            r0.setRenderersPool(r1, r2)
        L_0x0ddc:
            org.telegram.ui.AvatarPreviewPagerIndicator r6 = new org.telegram.ui.AvatarPreviewPagerIndicator
            r6.<init>(r9)
            r8.avatarPagerIndicator = r6
            org.telegram.ui.GroupCallActivity$28 r10 = new org.telegram.ui.GroupCallActivity$28
            org.telegram.ui.ActionBar.ActionBar r3 = r8.actionBar
            org.telegram.ui.Components.RecyclerListView r4 = r8.listView
            r0 = r10
            r1 = r43
            r2 = r44
            r5 = r6
            r0.<init>(r2, r3, r4, r5)
            r8.avatarsViewPager = r10
            r0 = 8192(0x2000, float:1.14794E-41)
            r10.setImagesLayerNum(r0)
            r0 = 1
            r10.setInvalidateWithParent(r0)
            r6.setProfileGalleryView(r10)
            org.telegram.ui.GroupCallActivity$29 r0 = new org.telegram.ui.GroupCallActivity$29
            r0.<init>(r9)
            r8.avatarPreviewContainer = r0
            r1 = 8
            r0.setVisibility(r1)
            r1 = 0
            r10.setVisibility(r1)
            org.telegram.ui.GroupCallActivity$30 r1 = new org.telegram.ui.GroupCallActivity$30
            r1.<init>()
            r10.addOnPageChangeListener(r1)
            org.telegram.ui.GroupCallActivity$31 r1 = new org.telegram.ui.GroupCallActivity$31
            r1.<init>(r9)
            r8.blurredView = r1
            android.view.ViewGroup r1 = r8.containerView
            org.telegram.ui.Components.voip.GroupCallRenderersContainer r2 = r8.renderersContainer
            r1.addView(r2)
            org.telegram.ui.Components.voip.GroupCallRenderersContainer r1 = r8.renderersContainer
            org.telegram.ui.Components.RecyclerListView r2 = r8.fullscreenUsersListView
            r32 = -1
            r33 = 1117782016(0x42a00000, float:80.0)
            r34 = 80
            r35 = 0
            r36 = 0
            r37 = 0
            r38 = 1120403456(0x42CLASSNAME, float:100.0)
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame(r32, r33, r34, r35, r36, r37, r38)
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
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r13, (int) r3, (int) r4)
            r2.addView(r1, r3)
            android.view.View r1 = new android.view.View
            r1.<init>(r9)
            r8.buttonsBackgroundGradientView2 = r1
            int[] r2 = r8.gradientColors
            r3 = 0
            r2 = r2[r3]
            r1.setBackgroundColor(r2)
            android.view.ViewGroup r2 = r8.containerView
            android.widget.FrameLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r13, (int) r3, (int) r4)
            r2.addView(r1, r4)
            android.view.ViewGroup r1 = r8.containerView
            android.widget.FrameLayout r2 = r8.buttonsContainer
            r3 = 200(0xc8, float:2.8E-43)
            r4 = 81
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r13, (int) r3, (int) r4)
            r1.addView(r2, r3)
            android.view.ViewGroup r1 = r8.containerView
            android.view.View r2 = r8.blurredView
            r1.addView(r2)
            r1 = -1082130432(0xffffffffbvar_, float:-1.0)
            android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame(r13, r1)
            r0.addView(r10, r1)
            r33 = -1082130432(0xffffffffbvar_, float:-1.0)
            r34 = 0
            r38 = 0
            android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame(r32, r33, r34, r35, r36, r37, r38)
            r0.addView(r6, r1)
            android.view.ViewGroup r1 = r8.containerView
            r35 = 1096810496(0x41600000, float:14.0)
            r36 = 1096810496(0x41600000, float:14.0)
            r37 = 1096810496(0x41600000, float:14.0)
            r38 = 1096810496(0x41600000, float:14.0)
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r32, r33, r34, r35, r36, r37, r38)
            r1.addView(r0, r2)
            r0 = 0
            r8.applyCallParticipantUpdates(r0)
            org.telegram.ui.GroupCallActivity$ListAdapter r1 = r8.listAdapter
            r1.notifyDataSetChanged()
            boolean r1 = isTabletMode
            if (r1 == 0) goto L_0x0ed9
            org.telegram.ui.GroupCallTabletGridAdapter r1 = r8.tabletGridAdapter
            org.telegram.ui.Components.RecyclerListView r2 = r8.tabletVideoGridView
            r1.update(r0, r2)
        L_0x0ed9:
            org.telegram.ui.GroupCallActivity$ListAdapter r0 = r8.listAdapter
            int r0 = r0.getItemCount()
            r8.oldCount = r0
            if (r11 == 0) goto L_0x10fe
            android.widget.TextView r0 = new android.widget.TextView
            r0.<init>(r9)
            r8.scheduleInfoTextView = r0
            r1 = 17
            r0.setGravity(r1)
            android.widget.TextView r0 = r8.scheduleInfoTextView
            r1 = -8682615(0xffffffffff7b8389, float:-3.343192E38)
            r0.setTextColor(r1)
            android.widget.TextView r0 = r8.scheduleInfoTextView
            r1 = 1096810496(0x41600000, float:14.0)
            r2 = 1
            r0.setTextSize(r2, r1)
            org.telegram.tgnet.TLRPC$Chat r0 = r8.currentChat
            boolean r0 = org.telegram.messenger.ChatObject.isChannel(r0)
            if (r0 == 0) goto L_0x0var_
            org.telegram.tgnet.TLRPC$Chat r0 = r8.currentChat
            boolean r0 = r0.megagroup
            if (r0 != 0) goto L_0x0var_
            android.widget.TextView r0 = r8.scheduleInfoTextView
            java.lang.Integer r1 = java.lang.Integer.valueOf(r2)
            r0.setTag(r1)
        L_0x0var_:
            android.view.ViewGroup r0 = r8.containerView
            android.widget.TextView r1 = r8.scheduleInfoTextView
            r32 = -2
            r33 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r34 = 81
            r35 = 1101529088(0x41a80000, float:21.0)
            r36 = 0
            r37 = 1101529088(0x41a80000, float:21.0)
            r38 = 1120403456(0x42CLASSNAME, float:100.0)
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r32, r33, r34, r35, r36, r37, r38)
            r0.addView(r1, r2)
            org.telegram.ui.Components.NumberPicker r0 = new org.telegram.ui.Components.NumberPicker
            r0.<init>(r9)
            r7 = r0
            r7.setTextColor(r13)
            r0 = -9598483(0xffffffffff6d89ed, float:-3.1574319E38)
            r7.setSelectorColor(r0)
            r0 = 1092616192(0x41200000, float:10.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r7.setTextOffset(r0)
            r0 = 5
            r7.setItemCount(r0)
            org.telegram.ui.GroupCallActivity$32 r0 = new org.telegram.ui.GroupCallActivity$32
            r0.<init>(r9)
            r6 = r0
            r0 = 5
            r6.setItemCount(r0)
            r6.setTextColor(r13)
            r0 = -9598483(0xffffffffff6d89ed, float:-3.1574319E38)
            r6.setSelectorColor(r0)
            r0 = 1092616192(0x41200000, float:10.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r0 = -r0
            r6.setTextOffset(r0)
            org.telegram.ui.GroupCallActivity$33 r0 = new org.telegram.ui.GroupCallActivity$33
            r0.<init>(r9)
            r5 = r0
            r0 = 5
            r5.setItemCount(r0)
            r5.setTextColor(r13)
            r0 = -9598483(0xffffffffff6d89ed, float:-3.1574319E38)
            r5.setSelectorColor(r0)
            r0 = 1107820544(0x42080000, float:34.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r0 = -r0
            r5.setTextOffset(r0)
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
            r1 = 17
            r0.setGravity(r1)
            android.widget.TextView r0 = r8.scheduleButtonTextView
            r1 = 1082130432(0x40800000, float:4.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r2 = 1056964608(0x3var_, float:0.5)
            r3 = 0
            android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.createSimpleSelectorRoundRectDrawable(r1, r3, r2)
            r0.setBackground(r1)
            android.widget.TextView r0 = r8.scheduleButtonTextView
            r0.setTextColor(r13)
            android.widget.TextView r0 = r8.scheduleButtonTextView
            android.graphics.Typeface r1 = org.telegram.messenger.AndroidUtilities.getTypeface(r12)
            r0.setTypeface(r1)
            android.widget.TextView r0 = r8.scheduleButtonTextView
            r1 = 1096810496(0x41600000, float:14.0)
            r2 = 1
            r0.setTextSize(r2, r1)
            android.view.ViewGroup r0 = r8.containerView
            android.widget.TextView r1 = r8.scheduleButtonTextView
            r32 = -1
            r33 = 1111490560(0x42400000, float:48.0)
            r38 = 1101266944(0x41a40000, float:20.5)
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r32, r33, r34, r35, r36, r37, r38)
            r0.addView(r1, r2)
            android.widget.TextView r12 = r8.scheduleButtonTextView
            org.telegram.ui.GroupCallActivity$$ExternalSyntheticLambda17 r13 = new org.telegram.ui.GroupCallActivity$$ExternalSyntheticLambda17
            r0 = r13
            r1 = r43
            r2 = r7
            r3 = r6
            r4 = r5
            r16 = r5
            r5 = r47
            r23 = r6
            r6 = r45
            r40 = r7
            r7 = r15
            r0.<init>(r1, r2, r3, r4, r5, r6, r7)
            r12.setOnClickListener(r13)
            org.telegram.ui.GroupCallActivity$35 r6 = new org.telegram.ui.GroupCallActivity$35
            r0 = r6
            r2 = r44
            r3 = r40
            r4 = r23
            r5 = r16
            r0.<init>(r2, r3, r4, r5)
            r8.scheduleTimerContainer = r6
            r0 = 1065353216(0x3var_, float:1.0)
            r6.setWeightSum(r0)
            android.widget.LinearLayout r0 = r8.scheduleTimerContainer
            r1 = 0
            r0.setOrientation(r1)
            android.view.ViewGroup r0 = r8.containerView
            android.widget.LinearLayout r1 = r8.scheduleTimerContainer
            r33 = 1132920832(0x43870000, float:270.0)
            r34 = 51
            r35 = 0
            r36 = 1112014848(0x42480000, float:50.0)
            r37 = 0
            r38 = 0
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r32, r33, r34, r35, r36, r37, r38)
            r0.addView(r1, r2)
            long r0 = java.lang.System.currentTimeMillis()
            java.util.Calendar r2 = java.util.Calendar.getInstance()
            r2.setTimeInMillis(r0)
            r3 = 1
            int r4 = r2.get(r3)
            r5 = 6
            int r6 = r2.get(r5)
            android.widget.LinearLayout r5 = r8.scheduleTimerContainer
            r7 = 270(0x10e, float:3.78E-43)
            r12 = 1056964608(0x3var_, float:0.5)
            r13 = 0
            android.widget.LinearLayout$LayoutParams r7 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r13, (int) r7, (float) r12)
            r12 = r40
            r5.addView(r12, r7)
            r12.setMinValue(r13)
            r5 = 365(0x16d, float:5.11E-43)
            r12.setMaxValue(r5)
            r12.setWrapSelectorWheel(r13)
            org.telegram.ui.GroupCallActivity$$ExternalSyntheticLambda52 r5 = new org.telegram.ui.GroupCallActivity$$ExternalSyntheticLambda52
            r5.<init>(r0, r2, r4)
            r12.setFormatter(r5)
            org.telegram.ui.GroupCallActivity$$ExternalSyntheticLambda56 r5 = new org.telegram.ui.GroupCallActivity$$ExternalSyntheticLambda56
            r13 = r16
            r7 = r23
            r5.<init>(r8, r12, r7, r13)
            r12.setOnValueChangedListener(r5)
            r3 = 0
            r7.setMinValue(r3)
            r3 = 23
            r7.setMaxValue(r3)
            android.widget.LinearLayout r3 = r8.scheduleTimerContainer
            r16 = r4
            r4 = 270(0x10e, float:3.78E-43)
            r11 = 1045220557(0x3e4ccccd, float:0.2)
            r19 = r14
            r14 = 0
            android.widget.LinearLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r14, (int) r4, (float) r11)
            r3.addView(r7, r4)
            org.telegram.ui.GroupCallActivity$$ExternalSyntheticLambda53 r3 = org.telegram.ui.GroupCallActivity$$ExternalSyntheticLambda53.INSTANCE
            r7.setFormatter(r3)
            r7.setOnValueChangedListener(r5)
            r13.setMinValue(r14)
            r3 = 59
            r13.setMaxValue(r3)
            r13.setValue(r14)
            org.telegram.ui.GroupCallActivity$$ExternalSyntheticLambda54 r3 = org.telegram.ui.GroupCallActivity$$ExternalSyntheticLambda54.INSTANCE
            r13.setFormatter(r3)
            android.widget.LinearLayout r3 = r8.scheduleTimerContainer
            r4 = 270(0x10e, float:3.78E-43)
            r11 = 1050253722(0x3e99999a, float:0.3)
            android.widget.LinearLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r14, (int) r4, (float) r11)
            r3.addView(r13, r4)
            r13.setOnValueChangedListener(r5)
            r3 = 10800000(0xa4cb80, double:5.335909E-317)
            long r3 = r3 + r0
            r2.setTimeInMillis(r3)
            r3 = 12
            r2.set(r3, r14)
            r4 = 13
            r2.set(r4, r14)
            r4 = 14
            r2.set(r4, r14)
            r4 = 6
            int r4 = r2.get(r4)
            int r3 = r2.get(r3)
            r11 = 11
            int r11 = r2.get(r11)
            if (r6 == r4) goto L_0x10db
            r14 = 1
            goto L_0x10dc
        L_0x10db:
            r14 = 0
        L_0x10dc:
            r12.setValue(r14)
            r13.setValue(r3)
            r7.setValue(r11)
            android.widget.TextView r14 = r8.scheduleButtonTextView
            r23 = r0
            android.widget.TextView r0 = r8.scheduleInfoTextView
            r34 = 604800(0x93a80, double:2.98811E-318)
            r36 = 2
            r32 = r14
            r33 = r0
            r37 = r12
            r38 = r7
            r39 = r13
            org.telegram.ui.Components.AlertsCreator.checkScheduleDate(r32, r33, r34, r36, r37, r38, r39)
            goto L_0x1100
        L_0x10fe:
            r19 = r14
        L_0x1100:
            int r0 = android.os.Build.VERSION.SDK_INT
            r1 = 21
            if (r0 < r1) goto L_0x1111
            android.view.Window r0 = r43.getWindow()
            android.view.View r0 = r0.getDecorView()
            android.view.ViewGroup r0 = (android.view.ViewGroup) r0
            goto L_0x1113
        L_0x1111:
            android.view.ViewGroup r0 = r8.containerView
        L_0x1113:
            org.telegram.ui.GroupCallActivity$36 r1 = new org.telegram.ui.GroupCallActivity$36
            android.view.ViewGroup r2 = r8.containerView
            r1.<init>(r0, r2)
            r8.pinchToZoomHelper = r1
            org.telegram.ui.GroupCallActivity$37 r2 = new org.telegram.ui.GroupCallActivity$37
            r2.<init>()
            r1.setCallback(r2)
            org.telegram.ui.PinchToZoomHelper r1 = r8.pinchToZoomHelper
            r10.setPinchToZoomHelper(r1)
            org.telegram.ui.Components.voip.VoIPToggleButton r1 = r8.cameraButton
            org.telegram.ui.GroupCallActivity$$ExternalSyntheticLambda16 r2 = new org.telegram.ui.GroupCallActivity$$ExternalSyntheticLambda16
            r2.<init>(r8, r9)
            r1.setOnClickListener(r2)
            r1 = 0
            r8.updateScheduleUI(r1)
            r43.updateItems()
            r8.updateSpeakerPhoneIcon(r1)
            r8.updateState(r1, r1)
            r1 = 0
            r8.setColorProgress(r1)
            r43.updateSubtitle()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.GroupCallActivity.<init>(android.content.Context, org.telegram.messenger.AccountInstance, org.telegram.messenger.ChatObject$Call, org.telegram.tgnet.TLRPC$Chat, org.telegram.tgnet.TLRPC$InputPeer, boolean, java.lang.String):void");
    }

    /* renamed from: lambda$new$9$org-telegram-ui-GroupCallActivity  reason: not valid java name */
    public /* synthetic */ void m3534lambda$new$9$orgtelegramuiGroupCallActivity(DialogInterface dialog) {
        BaseFragment fragment = this.parentActivity.getActionBarLayout().fragmentsStack.get(this.parentActivity.getActionBarLayout().fragmentsStack.size() - 1);
        if (this.anyEnterEventSent && (fragment instanceof ChatActivity)) {
            ((ChatActivity) fragment).onEditTextDialogClose(true, true);
        }
    }

    /* renamed from: lambda$new$10$org-telegram-ui-GroupCallActivity  reason: not valid java name */
    public /* synthetic */ void m3512lambda$new$10$orgtelegramuiGroupCallActivity(int[] uids, float[] levels, boolean[] voice) {
        RecyclerView.ViewHolder holder;
        for (int a = 0; a < uids.length; a++) {
            TLRPC.TL_groupCallParticipant participant = this.call.participantsBySources.get(uids[a]);
            if (participant != null) {
                if (!this.renderersContainer.inFullscreenMode) {
                    int idx = (this.delayedGroupCallUpdated ? this.oldParticipants : this.call.visibleParticipants).indexOf(participant);
                    if (idx >= 0 && (holder = this.listView.findViewHolderForAdapterPosition(this.listAdapter.usersStartRow + idx)) != null && (holder.itemView instanceof GroupCallUserCell)) {
                        ((GroupCallUserCell) holder.itemView).setAmplitude((double) (levels[a] * 15.0f));
                        if (holder.itemView == this.scrimView && !this.contentFullyOverlayed) {
                            this.containerView.invalidate();
                        }
                    }
                } else {
                    for (int i = 0; i < this.fullscreenUsersListView.getChildCount(); i++) {
                        GroupCallFullscreenAdapter.GroupCallUserCell cell = (GroupCallFullscreenAdapter.GroupCallUserCell) this.fullscreenUsersListView.getChildAt(i);
                        if (MessageObject.getPeerId(cell.getParticipant().peer) == MessageObject.getPeerId(participant.peer)) {
                            cell.setAmplitude((double) (levels[a] * 15.0f));
                        }
                    }
                }
                this.renderersContainer.setAmplitude(participant, levels[a] * 15.0f);
            }
        }
    }

    /* renamed from: lambda$new$12$org-telegram-ui-GroupCallActivity  reason: not valid java name */
    public /* synthetic */ void m3514lambda$new$12$orgtelegramuiGroupCallActivity(View view, int position, float x, float y) {
        if (view instanceof GroupCallGridCell) {
            fullscreenFor(((GroupCallGridCell) view).getParticipant());
        } else if (view instanceof GroupCallUserCell) {
            showMenuForCell((GroupCallUserCell) view);
        } else if (view instanceof GroupCallInvitedCell) {
            GroupCallInvitedCell cell = (GroupCallInvitedCell) view;
            if (cell.getUser() != null) {
                this.parentActivity.switchToAccount(this.currentAccount, true);
                Bundle args = new Bundle();
                args.putLong("user_id", cell.getUser().id);
                if (cell.hasAvatarSet()) {
                    args.putBoolean("expandPhoto", true);
                }
                this.parentActivity.m3690lambda$runLinkRequest$59$orgtelegramuiLaunchActivity(new ProfileActivity(args));
                dismiss();
            }
        } else if (position != this.listAdapter.addMemberRow) {
        } else {
            if (!ChatObject.isChannel(this.currentChat) || this.currentChat.megagroup || TextUtils.isEmpty(this.currentChat.username)) {
                TLRPC.ChatFull chatFull = this.accountInstance.getMessagesController().getChatFull(this.currentChat.id);
                if (chatFull != null) {
                    this.enterEventSent = false;
                    GroupVoipInviteAlert groupVoipInviteAlert2 = new GroupVoipInviteAlert(getContext(), this.accountInstance.getCurrentAccount(), this.currentChat, chatFull, this.call.participants, this.call.invitedUsersMap);
                    this.groupVoipInviteAlert = groupVoipInviteAlert2;
                    groupVoipInviteAlert2.setOnDismissListener(new GroupCallActivity$$ExternalSyntheticLambda1(this));
                    this.groupVoipInviteAlert.setDelegate(new GroupVoipInviteAlert.GroupVoipInviteAlertDelegate() {
                        public void copyInviteLink() {
                            GroupCallActivity.this.getLink(true);
                        }

                        public void inviteUser(long id) {
                            GroupCallActivity.this.inviteUserToCall(id, true);
                        }

                        public void needOpenSearch(MotionEvent ev, EditTextBoldCursor editText) {
                            if (GroupCallActivity.this.enterEventSent) {
                                return;
                            }
                            if (ev.getX() <= ((float) editText.getLeft()) || ev.getX() >= ((float) editText.getRight()) || ev.getY() <= ((float) editText.getTop()) || ev.getY() >= ((float) editText.getBottom())) {
                                GroupCallActivity groupCallActivity = GroupCallActivity.this;
                                groupCallActivity.makeFocusable(groupCallActivity.groupVoipInviteAlert, (AlertDialog) null, editText, false);
                                return;
                            }
                            GroupCallActivity groupCallActivity2 = GroupCallActivity.this;
                            groupCallActivity2.makeFocusable(groupCallActivity2.groupVoipInviteAlert, (AlertDialog) null, editText, true);
                        }
                    });
                    this.groupVoipInviteAlert.show();
                    return;
                }
                return;
            }
            getLink(false);
        }
    }

    /* renamed from: lambda$new$11$org-telegram-ui-GroupCallActivity  reason: not valid java name */
    public /* synthetic */ void m3513lambda$new$11$orgtelegramuiGroupCallActivity(DialogInterface dialog) {
        this.groupVoipInviteAlert = null;
    }

    /* renamed from: lambda$new$13$org-telegram-ui-GroupCallActivity  reason: not valid java name */
    public /* synthetic */ boolean m3515lambda$new$13$orgtelegramuiGroupCallActivity(View view, int position) {
        if (isRtmpStream()) {
            return false;
        }
        if (view instanceof GroupCallGridCell) {
            return showMenuForCell(view);
        }
        if (!(view instanceof GroupCallUserCell)) {
            return false;
        }
        updateItems();
        return ((GroupCallUserCell) view).clickMuteButton();
    }

    /* renamed from: lambda$new$14$org-telegram-ui-GroupCallActivity  reason: not valid java name */
    public /* synthetic */ void m3516lambda$new$14$orgtelegramuiGroupCallActivity(View view, int position) {
        GroupCallGridCell cell = (GroupCallGridCell) view;
        if (cell.getParticipant() != null) {
            fullscreenFor(cell.getParticipant());
        }
    }

    /* renamed from: lambda$new$15$org-telegram-ui-GroupCallActivity  reason: not valid java name */
    public /* synthetic */ void m3517lambda$new$15$orgtelegramuiGroupCallActivity(View v) {
        ChatObject.Call call2 = this.call;
        if (call2 == null || call2.isScheduled() || isRtmpStream()) {
            getLink(false);
        } else if (VoIPService.getSharedInstance() != null) {
            VoIPService.getSharedInstance().toggleSpeakerphoneOrShowRouteSheet(getContext(), false);
        }
    }

    /* renamed from: lambda$new$16$org-telegram-ui-GroupCallActivity  reason: not valid java name */
    public /* synthetic */ void m3518lambda$new$16$orgtelegramuiGroupCallActivity(View view) {
        this.renderersContainer.delayHideUi();
        VoIPService service = VoIPService.getSharedInstance();
        if (service != null && service.getVideoState(false) == 2) {
            service.switchCamera();
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
                GroupCallMiniTextureView renderer = this.attachedRenderers.get(i);
                if (renderer.participant.participant.self && !renderer.participant.presentation) {
                    renderer.startFlipAnimation();
                }
            }
        }
    }

    /* renamed from: lambda$new$17$org-telegram-ui-GroupCallActivity  reason: not valid java name */
    public /* synthetic */ void m3519lambda$new$17$orgtelegramuiGroupCallActivity(Context context, View v) {
        this.renderersContainer.delayHideUi();
        ChatObject.Call call2 = this.call;
        if (call2 == null || call2.isScheduled()) {
            dismiss();
            return;
        }
        updateItems();
        onLeaveClick(context, new GroupCallActivity$$ExternalSyntheticLambda24(this), false);
    }

    /* renamed from: lambda$new$18$org-telegram-ui-GroupCallActivity  reason: not valid java name */
    public /* synthetic */ void m3520lambda$new$18$orgtelegramuiGroupCallActivity(int id) {
        this.actionBar.getActionBarMenuOnItemClick().onItemClick(id);
    }

    /* renamed from: lambda$new$19$org-telegram-ui-GroupCallActivity  reason: not valid java name */
    public /* synthetic */ void m3521lambda$new$19$orgtelegramuiGroupCallActivity(Context context, View v) {
        if (this.call != null && !this.renderersContainer.inFullscreenMode) {
            if (this.call.call.join_muted) {
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
            if (VoIPService.getSharedInstance() != null && (VoIPService.getSharedInstance().hasEarpiece() || VoIPService.getSharedInstance().isBluetoothHeadsetConnected())) {
                int rout = VoIPService.getSharedInstance().getCurrentAudioRoute();
                if (rout == 2) {
                    this.soundItem.setIcon(NUM);
                    this.soundItem.setSubtext(VoIPService.getSharedInstance().currentBluetoothDeviceName != null ? VoIPService.getSharedInstance().currentBluetoothDeviceName : LocaleController.getString("VoipAudioRoutingBluetooth", NUM));
                } else {
                    int i = NUM;
                    String str = "VoipAudioRoutingPhone";
                    int i2 = NUM;
                    if (rout == 0) {
                        ActionBarMenuSubItem actionBarMenuSubItem = this.soundItem;
                        if (VoIPService.getSharedInstance().isHeadsetPlugged()) {
                            i2 = NUM;
                        }
                        actionBarMenuSubItem.setIcon(i2);
                        ActionBarMenuSubItem actionBarMenuSubItem2 = this.soundItem;
                        if (VoIPService.getSharedInstance().isHeadsetPlugged()) {
                            i = NUM;
                            str = "VoipAudioRoutingHeadset";
                        }
                        actionBarMenuSubItem2.setSubtext(LocaleController.getString(str, i));
                    } else if (rout == 1) {
                        if (((AudioManager) context.getSystemService("audio")).isSpeakerphoneOn()) {
                            this.soundItem.setIcon(NUM);
                            this.soundItem.setSubtext(LocaleController.getString("VoipAudioRoutingSpeaker", NUM));
                        } else {
                            this.soundItem.setIcon(NUM);
                            this.soundItem.setSubtext(LocaleController.getString(str, NUM));
                        }
                    }
                }
            }
            updateItems();
            this.otherItem.toggleSubMenu();
        }
    }

    /* renamed from: lambda$new$20$org-telegram-ui-GroupCallActivity  reason: not valid java name */
    public /* synthetic */ void m3522lambda$new$20$orgtelegramuiGroupCallActivity(View v) {
        if (isRtmpStream()) {
            if (AndroidUtilities.checkInlinePermissions(this.parentActivity)) {
                RTMPStreamPipOverlay.show();
                dismiss();
                return;
            }
            AlertsCreator.createDrawOverlayPermissionDialog(this.parentActivity, (DialogInterface.OnClickListener) null).show();
        } else if (AndroidUtilities.checkInlinePermissions(this.parentActivity)) {
            GroupCallPip.clearForce();
            dismiss();
        } else {
            AlertsCreator.createDrawOverlayGroupCallPermissionDialog(getContext()).show();
        }
    }

    /* renamed from: lambda$new$21$org-telegram-ui-GroupCallActivity  reason: not valid java name */
    public /* synthetic */ void m3523lambda$new$21$orgtelegramuiGroupCallActivity(View v) {
        VoIPService voIPService = VoIPService.getSharedInstance();
        if (voIPService != null) {
            if (voIPService.getVideoState(true) == 2) {
                voIPService.stopScreenCapture();
            } else {
                startScreenCapture();
            }
        }
    }

    /* renamed from: lambda$new$22$org-telegram-ui-GroupCallActivity  reason: not valid java name */
    public /* synthetic */ void m3524lambda$new$22$orgtelegramuiGroupCallActivity(View v) {
        ChatObject.Call call2 = this.call;
        if (call2 != null && call2.recording) {
            showRecordHint(this.actionBar.getTitleTextView());
        }
    }

    /* renamed from: lambda$new$23$org-telegram-ui-GroupCallActivity  reason: not valid java name */
    public /* synthetic */ void m3525lambda$new$23$orgtelegramuiGroupCallActivity(View view, int position) {
        GroupCallFullscreenAdapter.GroupCallUserCell userCell = (GroupCallFullscreenAdapter.GroupCallUserCell) view;
        if (userCell.getVideoParticipant() == null) {
            fullscreenFor(new ChatObject.VideoParticipant(userCell.getParticipant(), false, false));
        } else {
            fullscreenFor(userCell.getVideoParticipant());
        }
    }

    /* renamed from: lambda$new$24$org-telegram-ui-GroupCallActivity  reason: not valid java name */
    public /* synthetic */ boolean m3526lambda$new$24$orgtelegramuiGroupCallActivity(View view, int position) {
        if (showMenuForCell(view)) {
            this.listView.performHapticFeedback(0);
        }
        return false;
    }

    /* renamed from: lambda$new$29$org-telegram-ui-GroupCallActivity  reason: not valid java name */
    public /* synthetic */ void m3531lambda$new$29$orgtelegramuiGroupCallActivity(NumberPicker dayPicker, NumberPicker hourPicker, NumberPicker minutePicker, TLRPC.Chat chat, AccountInstance account, TLRPC.InputPeer peer, View v) {
        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
        this.scheduleAnimator = ofFloat;
        ofFloat.setDuration(600);
        this.scheduleAnimator.addUpdateListener(new GroupCallActivity$$ExternalSyntheticLambda0(this));
        this.scheduleAnimator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                ValueAnimator unused = GroupCallActivity.this.scheduleAnimator = null;
            }
        });
        this.scheduleAnimator.start();
        if (ChatObject.isChannelOrGiga(this.currentChat)) {
            this.titleTextView.setText(LocaleController.getString("VoipChannelVoiceChat", NUM), true);
        } else {
            this.titleTextView.setText(LocaleController.getString("VoipGroupVoiceChat", NUM), true);
        }
        Calendar calendar = Calendar.getInstance();
        boolean setSeconds = AlertsCreator.checkScheduleDate((TextView) null, (TextView) null, 604800, 3, dayPicker, hourPicker, minutePicker);
        calendar.setTimeInMillis(System.currentTimeMillis() + (((long) dayPicker.getValue()) * 24 * 3600 * 1000));
        calendar.set(11, hourPicker.getValue());
        calendar.set(12, minutePicker.getValue());
        if (setSeconds) {
            calendar.set(13, 0);
        }
        this.scheduleStartAt = (int) (calendar.getTimeInMillis() / 1000);
        updateScheduleUI(false);
        TLRPC.TL_phone_createGroupCall req = new TLRPC.TL_phone_createGroupCall();
        req.peer = MessagesController.getInputPeer(chat);
        req.random_id = Utilities.random.nextInt();
        req.schedule_date = this.scheduleStartAt;
        req.flags |= 2;
        TLRPC.Chat chat2 = chat;
        account.getConnectionsManager().sendRequest(req, new GroupCallActivity$$ExternalSyntheticLambda48(this, chat, peer), 2);
    }

    /* renamed from: lambda$new$25$org-telegram-ui-GroupCallActivity  reason: not valid java name */
    public /* synthetic */ void m3527lambda$new$25$orgtelegramuiGroupCallActivity(ValueAnimator a) {
        this.switchToButtonProgress = ((Float) a.getAnimatedValue()).floatValue();
        updateScheduleUI(true);
        this.buttonsContainer.invalidate();
        this.listView.invalidate();
    }

    /* renamed from: lambda$new$28$org-telegram-ui-GroupCallActivity  reason: not valid java name */
    public /* synthetic */ void m3530lambda$new$28$orgtelegramuiGroupCallActivity(TLRPC.Chat chat, TLRPC.InputPeer peer, TLObject response, TLRPC.TL_error error) {
        if (response != null) {
            TLRPC.Updates updates = (TLRPC.Updates) response;
            int a = 0;
            while (true) {
                if (a >= updates.updates.size()) {
                    break;
                }
                TLRPC.Update update = updates.updates.get(a);
                if (update instanceof TLRPC.TL_updateGroupCall) {
                    AndroidUtilities.runOnUIThread(new GroupCallActivity$$ExternalSyntheticLambda37(this, chat, peer, (TLRPC.TL_updateGroupCall) update));
                    break;
                }
                a++;
            }
            this.accountInstance.getMessagesController().processUpdates(updates, false);
            return;
        }
        AndroidUtilities.runOnUIThread(new GroupCallActivity$$ExternalSyntheticLambda38(this, error));
    }

    /* renamed from: lambda$new$26$org-telegram-ui-GroupCallActivity  reason: not valid java name */
    public /* synthetic */ void m3528lambda$new$26$orgtelegramuiGroupCallActivity(TLRPC.Chat chat, TLRPC.InputPeer peer, TLRPC.TL_updateGroupCall updateGroupCall) {
        ChatObject.Call call2 = new ChatObject.Call();
        this.call = call2;
        call2.call = new TLRPC.TL_groupCall();
        this.call.call.participants_count = 0;
        this.call.call.version = 1;
        this.call.call.can_start_video = true;
        this.call.call.can_change_join_muted = true;
        this.call.chatId = chat.id;
        this.call.call.schedule_date = this.scheduleStartAt;
        this.call.call.flags |= 128;
        this.call.currentAccount = this.accountInstance;
        this.call.setSelfPeer(peer);
        this.call.call.access_hash = updateGroupCall.call.access_hash;
        this.call.call.id = updateGroupCall.call.id;
        this.call.createNoVideoParticipant();
        this.fullscreenAdapter.setGroupCall(this.call);
        this.renderersContainer.setGroupCall(this.call);
        this.tabletGridAdapter.setGroupCall(this.call);
        this.accountInstance.getMessagesController().putGroupCall(this.call.chatId, this.call);
    }

    /* renamed from: lambda$new$27$org-telegram-ui-GroupCallActivity  reason: not valid java name */
    public /* synthetic */ void m3529lambda$new$27$orgtelegramuiGroupCallActivity(TLRPC.TL_error error) {
        this.accountInstance.getNotificationCenter().postNotificationName(NotificationCenter.needShowAlert, 6, error.text);
        dismiss();
    }

    static /* synthetic */ String lambda$new$30(long currentTime, Calendar calendar, int currentYear, int value) {
        if (value == 0) {
            return LocaleController.getString("MessageScheduleToday", NUM);
        }
        long date = (((long) value) * 86400000) + currentTime;
        calendar.setTimeInMillis(date);
        if (calendar.get(1) == currentYear) {
            return LocaleController.getInstance().formatterScheduleDay.format(date);
        }
        return LocaleController.getInstance().formatterScheduleYear.format(date);
    }

    /* renamed from: lambda$new$31$org-telegram-ui-GroupCallActivity  reason: not valid java name */
    public /* synthetic */ void m3532lambda$new$31$orgtelegramuiGroupCallActivity(NumberPicker dayPicker, NumberPicker hourPicker, NumberPicker minutePicker, NumberPicker picker, int oldVal, int newVal) {
        try {
            this.container.performHapticFeedback(3, 2);
        } catch (Exception e) {
        }
        AlertsCreator.checkScheduleDate(this.scheduleButtonTextView, this.scheduleInfoTextView, 604800, 2, dayPicker, hourPicker, minutePicker);
    }

    /* renamed from: lambda$new$34$org-telegram-ui-GroupCallActivity  reason: not valid java name */
    public /* synthetic */ void m3533lambda$new$34$orgtelegramuiGroupCallActivity(Context context, View View) {
        LaunchActivity launchActivity;
        boolean z = false;
        if (Build.VERSION.SDK_INT >= 23 && (launchActivity = this.parentActivity) != null && launchActivity.checkSelfPermission("android.permission.CAMERA") != 0) {
            this.parentActivity.requestPermissions(new String[]{"android.permission.CAMERA"}, 104);
        } else if (VoIPService.getSharedInstance() != null) {
            if (VoIPService.getSharedInstance().getVideoState(false) != 2) {
                this.undoView[0].hide(false, 1);
                if (this.previewDialog == null) {
                    VoIPService voIPService = VoIPService.getSharedInstance();
                    if (voIPService != null) {
                        voIPService.createCaptureDevice(false);
                    }
                    if (VoIPService.getSharedInstance().getVideoState(true) != 2) {
                        z = true;
                    }
                    this.previewDialog = new PrivateVideoPreviewDialog(context, true, z) {
                        public void onDismiss(boolean screencast, boolean apply) {
                            boolean showMicIcon = GroupCallActivity.this.previewDialog.micEnabled;
                            GroupCallActivity.this.previewDialog = null;
                            VoIPService service = VoIPService.getSharedInstance();
                            if (apply) {
                                if (service != null) {
                                    service.setupCaptureDevice(screencast, showMicIcon);
                                }
                                if (screencast && service != null) {
                                    service.setVideoState(false, 0);
                                }
                                GroupCallActivity.this.updateState(true, false);
                                GroupCallActivity.this.call.sortParticipants();
                                GroupCallActivity.this.applyCallParticipantUpdates(true);
                                GroupCallActivity.this.buttonsContainer.requestLayout();
                            } else if (service != null) {
                                service.setVideoState(false, 0);
                            }
                        }
                    };
                    this.container.addView(this.previewDialog);
                    if (voIPService != null && !voIPService.isFrontFaceCamera()) {
                        voIPService.switchCamera();
                        return;
                    }
                    return;
                }
                return;
            }
            VoIPService.getSharedInstance().setVideoState(false, 0);
            updateState(true, false);
            updateSpeakerPhoneIcon(false);
            this.call.sortParticipants();
            applyCallParticipantUpdates(true);
            this.buttonsContainer.requestLayout();
        }
    }

    public LaunchActivity getParentActivity() {
        return this.parentActivity;
    }

    /* access modifiers changed from: private */
    public void invalidateLayoutFullscreen() {
        int systemUiVisibility;
        if (isRtmpStream()) {
            boolean notFullscreen = this.renderersContainer.isUiVisible() || !this.renderersContainer.inFullscreenMode || (isLandscapeMode != isRtmpLandscapeMode() && !AndroidUtilities.isTablet());
            Boolean bool = this.wasNotInLayoutFullscreen;
            if (bool == null || notFullscreen != bool.booleanValue()) {
                int systemUiVisibility2 = this.containerView.getSystemUiVisibility();
                if (notFullscreen) {
                    systemUiVisibility = systemUiVisibility2 & -5 & -3;
                    getWindow().clearFlags(1024);
                    setHideSystemVerticalInsets(false);
                } else {
                    setHideSystemVerticalInsets(true);
                    systemUiVisibility = systemUiVisibility2 | 4 | 2;
                    getWindow().addFlags(1024);
                }
                this.containerView.setSystemUiVisibility(systemUiVisibility);
                this.wasNotInLayoutFullscreen = Boolean.valueOf(notFullscreen);
            }
        }
    }

    public LinearLayout getMenuItemsContainer() {
        return this.menuItemsContainer;
    }

    public void fullscreenFor(final ChatObject.VideoParticipant videoParticipant) {
        if (videoParticipant == null) {
            this.parentActivity.setRequestedOrientation(-1);
        }
        if (VoIPService.getSharedInstance() != null && !this.renderersContainer.isAnimating()) {
            if (isTabletMode) {
                if (this.requestFullscreenListener != null) {
                    this.listView.getViewTreeObserver().removeOnPreDrawListener(this.requestFullscreenListener);
                    this.requestFullscreenListener = null;
                }
                ArrayList<ChatObject.VideoParticipant> activeSinks = new ArrayList<>();
                if (videoParticipant == null) {
                    this.attachedRenderersTmp.clear();
                    this.attachedRenderersTmp.addAll(this.attachedRenderers);
                    for (int i = 0; i < this.attachedRenderersTmp.size(); i++) {
                        final GroupCallMiniTextureView miniTextureView = this.attachedRenderersTmp.get(i);
                        if (miniTextureView.primaryView != null) {
                            miniTextureView.primaryView.setRenderer((GroupCallMiniTextureView) null);
                            if (miniTextureView.secondaryView != null) {
                                miniTextureView.secondaryView.setRenderer((GroupCallMiniTextureView) null);
                            }
                            if (miniTextureView.tabletGridView != null) {
                                miniTextureView.tabletGridView.setRenderer((GroupCallMiniTextureView) null);
                            }
                            activeSinks.add(miniTextureView.participant);
                            miniTextureView.forceDetach(false);
                            miniTextureView.animate().alpha(0.0f).setListener(new AnimatorListenerAdapter() {
                                public void onAnimationEnd(Animator animation) {
                                    if (miniTextureView.getParent() != null) {
                                        GroupCallActivity.this.containerView.removeView(miniTextureView);
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
                        final GroupCallMiniTextureView miniTextureView2 = this.attachedRenderersTmp.get(i2);
                        if (miniTextureView2.tabletGridView != null && (miniTextureView2.participant == null || !miniTextureView2.participant.equals(videoParticipant))) {
                            activeSinks.add(miniTextureView2.participant);
                            miniTextureView2.forceDetach(false);
                            if (miniTextureView2.secondaryView != null) {
                                miniTextureView2.secondaryView.setRenderer((GroupCallMiniTextureView) null);
                            }
                            if (miniTextureView2.primaryView != null) {
                                miniTextureView2.primaryView.setRenderer((GroupCallMiniTextureView) null);
                            }
                            miniTextureView2.animate().alpha(0.0f).setListener(new AnimatorListenerAdapter() {
                                public void onAnimationEnd(Animator animation) {
                                    if (miniTextureView2.getParent() != null) {
                                        GroupCallActivity.this.containerView.removeView(miniTextureView2);
                                    }
                                }
                            });
                        }
                    }
                    this.listViewVideoVisibility = true;
                    this.tabletGridAdapter.setVisibility(this.tabletVideoGridView, false, false);
                    if (!activeSinks.isEmpty()) {
                        AndroidUtilities.runOnUIThread(new GroupCallActivity$$ExternalSyntheticLambda34(this, activeSinks));
                    }
                }
                final boolean updateScroll = !this.renderersContainer.inFullscreenMode;
                ViewTreeObserver viewTreeObserver = this.listView.getViewTreeObserver();
                AnonymousClass41 r4 = new ViewTreeObserver.OnPreDrawListener() {
                    public boolean onPreDraw() {
                        GroupCallActivity.this.listView.getViewTreeObserver().removeOnPreDrawListener(this);
                        GroupCallActivity.this.requestFullscreenListener = null;
                        GroupCallActivity.this.renderersContainer.requestFullscreen(videoParticipant);
                        if (GroupCallActivity.this.delayedGroupCallUpdated) {
                            boolean unused = GroupCallActivity.this.delayedGroupCallUpdated = false;
                            GroupCallActivity.this.applyCallParticipantUpdates(true);
                            if (updateScroll && videoParticipant != null) {
                                GroupCallActivity.this.listView.scrollToPosition(0);
                            }
                            boolean unused2 = GroupCallActivity.this.delayedGroupCallUpdated = true;
                        } else {
                            GroupCallActivity.this.applyCallParticipantUpdates(true);
                        }
                        return false;
                    }
                };
                this.requestFullscreenListener = r4;
                viewTreeObserver.addOnPreDrawListener(r4);
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
                    AnonymousClass42 r2 = new ViewTreeObserver.OnPreDrawListener() {
                        public boolean onPreDraw() {
                            GroupCallActivity.this.listView.getViewTreeObserver().removeOnPreDrawListener(this);
                            GroupCallActivity.this.requestFullscreenListener = null;
                            GroupCallActivity.this.renderersContainer.requestFullscreen(videoParticipant);
                            AndroidUtilities.updateVisibleRows(GroupCallActivity.this.fullscreenUsersListView);
                            return false;
                        }
                    };
                    this.requestFullscreenListener = r2;
                    viewTreeObserver2.addOnPreDrawListener(r2);
                    return;
                }
                this.renderersContainer.requestFullscreen(videoParticipant);
                AndroidUtilities.updateVisibleRows(this.fullscreenUsersListView);
            } else if (this.listView.getVisibility() != 0) {
                this.listView.setVisibility(0);
                applyCallParticipantUpdates(false);
                this.delayedGroupCallUpdated = true;
                ViewTreeObserver viewTreeObserver3 = this.listView.getViewTreeObserver();
                AnonymousClass43 r22 = new ViewTreeObserver.OnPreDrawListener() {
                    public boolean onPreDraw() {
                        GroupCallActivity.this.listView.getViewTreeObserver().removeOnPreDrawListener(this);
                        GroupCallActivity.this.renderersContainer.requestFullscreen((ChatObject.VideoParticipant) null);
                        AndroidUtilities.updateVisibleRows(GroupCallActivity.this.fullscreenUsersListView);
                        return false;
                    }
                };
                this.requestFullscreenListener = r22;
                viewTreeObserver3.addOnPreDrawListener(r22);
            } else {
                ViewTreeObserver viewTreeObserver4 = this.listView.getViewTreeObserver();
                AnonymousClass44 r23 = new ViewTreeObserver.OnPreDrawListener() {
                    public boolean onPreDraw() {
                        GroupCallActivity.this.listView.getViewTreeObserver().removeOnPreDrawListener(this);
                        GroupCallActivity.this.renderersContainer.requestFullscreen((ChatObject.VideoParticipant) null);
                        AndroidUtilities.updateVisibleRows(GroupCallActivity.this.fullscreenUsersListView);
                        return false;
                    }
                };
                this.requestFullscreenListener = r23;
                viewTreeObserver4.addOnPreDrawListener(r23);
            }
        }
    }

    /* renamed from: lambda$fullscreenFor$35$org-telegram-ui-GroupCallActivity  reason: not valid java name */
    public /* synthetic */ void m3501lambda$fullscreenFor$35$orgtelegramuiGroupCallActivity(ArrayList activeSinks) {
        for (int i = 0; i < this.attachedRenderers.size(); i++) {
            if (this.attachedRenderers.get(i).participant != null) {
                activeSinks.remove(this.attachedRenderers.get(i).participant);
            }
        }
        for (int i2 = 0; i2 < activeSinks.size(); i2++) {
            ChatObject.VideoParticipant participant = (ChatObject.VideoParticipant) activeSinks.get(i2);
            if (participant.participant.self) {
                if (VoIPService.getSharedInstance() != null) {
                    VoIPService.getSharedInstance().setLocalSink((VideoSink) null, participant.presentation);
                }
            } else if (VoIPService.getSharedInstance() != null) {
                VoIPService.getSharedInstance().removeRemoteSink(participant.participant, participant.presentation);
            }
        }
    }

    public void enableCamera() {
        this.cameraButton.callOnClick();
    }

    /* access modifiers changed from: private */
    public void checkContentOverlayed() {
        boolean overlayed = !this.avatarPriviewTransitionInProgress && this.blurredView.getVisibility() == 0 && this.blurredView.getAlpha() == 1.0f;
        if (this.contentFullyOverlayed != overlayed) {
            this.contentFullyOverlayed = overlayed;
            this.buttonsContainer.invalidate();
            this.containerView.invalidate();
            this.listView.invalidate();
        }
    }

    private void updateScheduleUI(boolean animation) {
        float alpha;
        float scheduleButtonsScale2;
        LinearLayout linearLayout = this.scheduleTimerContainer;
        float f = 1.0f;
        if ((linearLayout == null || this.call != null) && this.scheduleAnimator == null) {
            this.scheduleButtonsScale = 1.0f;
            this.switchToButtonInt2 = 1.0f;
            this.switchToButtonProgress = 1.0f;
            if (linearLayout == null) {
                return;
            }
        }
        int newVisibility = 4;
        if (!animation) {
            AndroidUtilities.cancelRunOnUIThread(this.updateSchedeulRunnable);
            this.updateSchedeulRunnable.run();
            ChatObject.Call call2 = this.call;
            if (call2 == null || call2.isScheduled()) {
                this.listView.setVisibility(4);
            } else {
                this.listView.setVisibility(0);
            }
            if (ChatObject.isChannelOrGiga(this.currentChat)) {
                this.leaveItem.setText(LocaleController.getString("VoipChannelCancelChat", NUM));
            } else {
                this.leaveItem.setText(LocaleController.getString("VoipGroupCancelChat", NUM));
            }
        }
        if (this.switchToButtonProgress > 0.6f) {
            float interpolation = 1.05f - (CubicBezierInterpolator.DEFAULT.getInterpolation((this.switchToButtonProgress - 0.6f) / 0.4f) * 0.05f);
            scheduleButtonsScale2 = interpolation;
            this.scheduleButtonsScale = interpolation;
            this.switchToButtonInt2 = 1.0f;
            alpha = 1.0f;
        } else {
            this.scheduleButtonsScale = (CubicBezierInterpolator.DEFAULT.getInterpolation(this.switchToButtonProgress / 0.6f) * 0.05f) + 1.0f;
            this.switchToButtonInt2 = CubicBezierInterpolator.DEFAULT.getInterpolation(this.switchToButtonProgress / 0.6f);
            scheduleButtonsScale2 = CubicBezierInterpolator.DEFAULT.getInterpolation(this.switchToButtonProgress / 0.6f) * 1.05f;
            alpha = this.switchToButtonProgress / 0.6f;
        }
        float muteButtonScale = isLandscapeMode ? (((float) AndroidUtilities.dp(52.0f)) * scheduleButtonsScale2) / ((float) (this.muteButton.getMeasuredWidth() - AndroidUtilities.dp(8.0f))) : scheduleButtonsScale2;
        float reversedAlpha = 1.0f - alpha;
        this.leaveButton.setAlpha(alpha);
        VoIPToggleButton voIPToggleButton = this.soundButton;
        if (!voIPToggleButton.isEnabled()) {
            f = 0.5f;
        }
        voIPToggleButton.setAlpha(f * alpha);
        this.muteButton.setAlpha(alpha);
        this.scheduleTimerContainer.setAlpha(reversedAlpha);
        this.scheduleStartInTextView.setAlpha(alpha);
        this.scheduleStartAtTextView.setAlpha(alpha);
        this.scheduleTimeTextView.setAlpha(alpha);
        this.muteLabel[0].setAlpha(alpha);
        this.scheduleTimeTextView.setScaleX(scheduleButtonsScale2);
        this.scheduleTimeTextView.setScaleY(scheduleButtonsScale2);
        this.leaveButton.setScaleX(scheduleButtonsScale2);
        this.leaveButton.setScaleY(scheduleButtonsScale2);
        this.soundButton.setScaleX(scheduleButtonsScale2);
        this.soundButton.setScaleY(scheduleButtonsScale2);
        this.muteButton.setScaleX(muteButtonScale);
        this.muteButton.setScaleY(muteButtonScale);
        this.scheduleButtonTextView.setScaleX(reversedAlpha);
        this.scheduleButtonTextView.setScaleY(reversedAlpha);
        this.scheduleButtonTextView.setAlpha(reversedAlpha);
        this.scheduleInfoTextView.setAlpha(reversedAlpha);
        this.cameraButton.setAlpha(alpha);
        this.cameraButton.setScaleY(scheduleButtonsScale2);
        this.cameraButton.setScaleX(scheduleButtonsScale2);
        this.flipButton.setAlpha(alpha);
        this.flipButton.setScaleY(scheduleButtonsScale2);
        this.flipButton.setScaleX(scheduleButtonsScale2);
        this.otherItem.setAlpha(alpha);
        if (reversedAlpha != 0.0f) {
            newVisibility = 0;
        }
        if (newVisibility != this.scheduleTimerContainer.getVisibility()) {
            this.scheduleTimerContainer.setVisibility(newVisibility);
            this.scheduleButtonTextView.setVisibility(newVisibility);
        }
    }

    private void initCreatedGroupCall() {
        VoIPService service;
        String str;
        int i;
        if (!this.callInitied && (service = VoIPService.getSharedInstance()) != null) {
            this.callInitied = true;
            this.oldParticipants.addAll(this.call.visibleParticipants);
            this.oldVideoParticipants.addAll(this.visibleVideoParticipants);
            this.oldInvited.addAll(this.call.invitedUsers);
            this.currentCallState = service.getCallState();
            if (this.call == null) {
                ChatObject.Call call2 = service.groupCall;
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
                this.leaveButton.setData(isRtmpStream() ? NUM : NUM, -1, Theme.getColor("voipgroup_leaveButton"), 0.3f, false, LocaleController.getString("VoipGroupLeave", NUM), false, true);
                updateSpeakerPhoneIcon(true);
                ActionBarMenuSubItem actionBarMenuSubItem = this.leaveItem;
                if (ChatObject.isChannelOrGiga(this.currentChat)) {
                    i = NUM;
                    str = "VoipChannelEndChat";
                } else {
                    i = NUM;
                    str = "VoipGroupEndChat";
                }
                actionBarMenuSubItem.setText(LocaleController.getString(str, i));
                this.listView.setVisibility(0);
                this.pipItem.setVisibility(0);
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(new Animator[]{ObjectAnimator.ofFloat(this.listView, View.ALPHA, new float[]{0.0f, 1.0f}), ObjectAnimator.ofFloat(this.listView, View.TRANSLATION_Y, new float[]{(float) AndroidUtilities.dp(200.0f), 0.0f}), ObjectAnimator.ofFloat(this.scheduleTimeTextView, View.SCALE_X, new float[]{0.0f}), ObjectAnimator.ofFloat(this.scheduleTimeTextView, View.SCALE_Y, new float[]{0.0f}), ObjectAnimator.ofFloat(this.scheduleTimeTextView, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.scheduleStartInTextView, View.SCALE_X, new float[]{0.0f}), ObjectAnimator.ofFloat(this.scheduleStartInTextView, View.SCALE_Y, new float[]{0.0f}), ObjectAnimator.ofFloat(this.scheduleStartInTextView, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.scheduleStartAtTextView, View.SCALE_X, new float[]{0.0f}), ObjectAnimator.ofFloat(this.scheduleStartAtTextView, View.SCALE_Y, new float[]{0.0f}), ObjectAnimator.ofFloat(this.scheduleStartAtTextView, View.ALPHA, new float[]{0.0f}), ObjectAnimator.ofFloat(this.pipItem, View.SCALE_X, new float[]{0.0f, 1.0f}), ObjectAnimator.ofFloat(this.pipItem, View.SCALE_Y, new float[]{0.0f, 1.0f}), ObjectAnimator.ofFloat(this.pipItem, View.ALPHA, new float[]{0.0f, 1.0f})});
                animatorSet.setInterpolator(CubicBezierInterpolator.EASE_OUT);
                animatorSet.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
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

    /* access modifiers changed from: private */
    public void updateSubtitle() {
        boolean drawStatus;
        if (this.actionBar != null && this.call != null) {
            SpannableStringBuilder spannableStringBuilder = null;
            int speakingIndex = 0;
            for (int i = 0; i < this.call.currentSpeakingPeers.size(); i++) {
                long key = this.call.currentSpeakingPeers.keyAt(i);
                TLRPC.TL_groupCallParticipant participant = this.call.currentSpeakingPeers.get(key);
                if (!participant.self && !this.renderersContainer.isVisible(participant) && this.visiblePeerIds.get(key, 0) != 1) {
                    long peerId = MessageObject.getPeerId(participant.peer);
                    if (spannableStringBuilder == null) {
                        spannableStringBuilder = new SpannableStringBuilder();
                    }
                    if (speakingIndex < 2) {
                        TLRPC.User user = peerId > 0 ? MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(peerId)) : null;
                        TLRPC.Chat chat = peerId <= 0 ? MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(peerId)) : null;
                        if (user != null || chat != null) {
                            if (speakingIndex != 0) {
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
                    speakingIndex++;
                    if (speakingIndex == 2) {
                        break;
                    }
                }
            }
            if (speakingIndex > 0) {
                String s = LocaleController.getPluralString("MembersAreSpeakingToast", speakingIndex);
                int replaceIndex = s.indexOf("un1");
                SpannableStringBuilder spannableStringBuilder1 = new SpannableStringBuilder(s);
                spannableStringBuilder1.replace(replaceIndex, replaceIndex + 3, spannableStringBuilder);
                this.actionBar.getAdditionalSubtitleTextView().setText(spannableStringBuilder1);
                drawStatus = true;
            } else {
                drawStatus = false;
            }
            this.actionBar.getSubtitleTextView().setText(LocaleController.formatPluralString(isRtmpStream() ? "ViewersWatching" : "Participants", this.call.call.participants_count + (this.listAdapter.addSelfToCounter() ? 1 : 0), new Object[0]));
            if (drawStatus != this.drawSpeakingSubtitle) {
                this.drawSpeakingSubtitle = drawStatus;
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

    public void show() {
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.stopAllHeavyOperations, 2048);
        super.show();
        if (RTMPStreamPipOverlay.isVisible()) {
            RTMPStreamPipOverlay.dismiss();
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
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.startAllHeavyOperations, 2048);
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
        ChatObject.Call call2 = this.call;
        if (call2 != null) {
            call2.clearVideFramesInfo();
        }
        if (VoIPService.getSharedInstance() != null) {
            VoIPService.getSharedInstance().clearRemoteSinks();
        }
    }

    private void setAmplitude(double value) {
        float min = (float) (Math.min(8500.0d, value) / 8500.0d);
        this.animateToAmplitude = min;
        this.animateAmplitudeDiff = (min - this.amplitude) / ((BlobDrawable.AMPLITUDE_SPEED * 500.0f) + 100.0f);
    }

    public void onStateChanged(int state) {
        this.currentCallState = state;
        updateState(isShowing(), false);
    }

    public UndoView getUndoView() {
        if (!isTabletMode && this.renderersContainer.inFullscreenMode) {
            return this.renderersContainer.getUndoView();
        }
        if (this.undoView[0].getVisibility() == 0) {
            UndoView[] undoViewArr = this.undoView;
            UndoView old = undoViewArr[0];
            undoViewArr[0] = undoViewArr[1];
            undoViewArr[1] = old;
            old.hide(true, 2);
            this.containerView.removeView(this.undoView[0]);
            this.containerView.addView(this.undoView[0]);
        }
        return this.undoView[0];
    }

    /* access modifiers changed from: private */
    public float getColorProgress() {
        return this.colorProgress;
    }

    private void updateTitle(boolean animated) {
        ChatObject.Call call2 = this.call;
        if (call2 != null) {
            if (!TextUtils.isEmpty(call2.call.title)) {
                if (!this.call.call.title.equals(this.actionBar.getTitle())) {
                    if (animated) {
                        this.actionBar.setTitleAnimated(this.call.call.title, true, 180);
                        this.actionBar.getTitleTextView().setOnClickListener(new GroupCallActivity$$ExternalSyntheticLambda10(this));
                    } else {
                        this.actionBar.setTitle(this.call.call.title);
                    }
                    this.titleTextView.setText(this.call.call.title, animated);
                }
            } else if (!this.currentChat.title.equals(this.actionBar.getTitle())) {
                if (animated) {
                    this.actionBar.setTitleAnimated(this.currentChat.title, true, 180);
                    this.actionBar.getTitleTextView().setOnClickListener(new GroupCallActivity$$ExternalSyntheticLambda12(this));
                } else {
                    this.actionBar.setTitle(this.currentChat.title);
                }
                if (ChatObject.isChannelOrGiga(this.currentChat)) {
                    this.titleTextView.setText(LocaleController.getString("VoipChannelVoiceChat", NUM), animated);
                } else {
                    this.titleTextView.setText(LocaleController.getString("VoipGroupVoiceChat", NUM), animated);
                }
            }
            SimpleTextView textView = this.actionBar.getTitleTextView();
            if (this.call.recording) {
                if (textView.getRightDrawable() == null) {
                    textView.setRightDrawable((Drawable) new SmallRecordCallDrawable(textView));
                    TextView tv = this.titleTextView.getTextView();
                    tv.setCompoundDrawablesWithIntrinsicBounds((Drawable) null, (Drawable) null, new SmallRecordCallDrawable(tv), (Drawable) null);
                    TextView tv2 = this.titleTextView.getNextTextView();
                    tv2.setCompoundDrawablesWithIntrinsicBounds((Drawable) null, (Drawable) null, new SmallRecordCallDrawable(tv2), (Drawable) null);
                }
            } else if (textView.getRightDrawable() != null) {
                textView.setRightDrawable((Drawable) null);
                this.titleTextView.getTextView().setCompoundDrawablesWithIntrinsicBounds((Drawable) null, (Drawable) null, (Drawable) null, (Drawable) null);
                this.titleTextView.getNextTextView().setCompoundDrawablesWithIntrinsicBounds((Drawable) null, (Drawable) null, (Drawable) null, (Drawable) null);
            }
        } else if (ChatObject.isChannelOrGiga(this.currentChat)) {
            this.titleTextView.setText(LocaleController.getString("VoipChannelScheduleVoiceChat", NUM), animated);
        } else {
            this.titleTextView.setText(LocaleController.getString("VoipGroupScheduleVoiceChat", NUM), animated);
        }
    }

    /* renamed from: lambda$updateTitle$36$org-telegram-ui-GroupCallActivity  reason: not valid java name */
    public /* synthetic */ void m3549lambda$updateTitle$36$orgtelegramuiGroupCallActivity(View v) {
        ChatObject.Call call2 = this.call;
        if (call2 != null && call2.recording) {
            showRecordHint(this.actionBar.getTitleTextView());
        }
    }

    /* renamed from: lambda$updateTitle$37$org-telegram-ui-GroupCallActivity  reason: not valid java name */
    public /* synthetic */ void m3550lambda$updateTitle$37$orgtelegramuiGroupCallActivity(View v) {
        ChatObject.Call call2 = this.call;
        if (call2 != null && call2.recording) {
            showRecordHint(this.actionBar.getTitleTextView());
        }
    }

    /* access modifiers changed from: private */
    public void setColorProgress(float progress) {
        this.colorProgress = progress;
        float finalColorProgress = this.colorProgress;
        GroupCallRenderersContainer groupCallRenderersContainer = this.renderersContainer;
        float finalColorProgress2 = Math.max(progress, groupCallRenderersContainer == null ? 0.0f : groupCallRenderersContainer.progressToFullscreenMode);
        int offsetColor = AndroidUtilities.getOffsetColor(Theme.getColor("voipgroup_actionBarUnscrolled"), Theme.getColor("voipgroup_actionBar"), finalColorProgress, 1.0f);
        this.backgroundColor = offsetColor;
        this.actionBarBackground.setBackgroundColor(offsetColor);
        this.otherItem.redrawPopup(-14472653);
        this.shadowDrawable.setColorFilter(new PorterDuffColorFilter(this.backgroundColor, PorterDuff.Mode.MULTIPLY));
        this.navBarColor = AndroidUtilities.getOffsetColor(Theme.getColor("voipgroup_actionBarUnscrolled"), Theme.getColor("voipgroup_actionBar"), finalColorProgress2, 1.0f);
        int color = AndroidUtilities.getOffsetColor(Theme.getColor("voipgroup_listViewBackgroundUnscrolled"), Theme.getColor("voipgroup_listViewBackground"), finalColorProgress, 1.0f);
        this.listViewBackgroundPaint.setColor(color);
        this.listView.setGlowColor(color);
        int i = this.muteButtonState;
        if (i == 3 || isGradientState(i)) {
            this.muteButton.invalidate();
        }
        if (this.buttonsBackgroundGradientView != null) {
            int[] iArr = this.gradientColors;
            iArr[0] = this.backgroundColor;
            iArr[1] = 0;
            if (Build.VERSION.SDK_INT > 29) {
                this.buttonsBackgroundGradient.setColors(this.gradientColors);
            } else {
                View view = this.buttonsBackgroundGradientView;
                GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, this.gradientColors);
                this.buttonsBackgroundGradient = gradientDrawable;
                view.setBackground(gradientDrawable);
            }
            this.buttonsBackgroundGradientView2.setBackgroundColor(this.gradientColors[0]);
        }
        int color2 = AndroidUtilities.getOffsetColor(Theme.getColor("voipgroup_leaveButton"), Theme.getColor("voipgroup_leaveButtonScrolled"), finalColorProgress, 1.0f);
        this.leaveButton.setBackgroundColor(color2, color2);
        int color3 = AndroidUtilities.getOffsetColor(Theme.getColor("voipgroup_lastSeenTextUnscrolled"), Theme.getColor("voipgroup_lastSeenText"), finalColorProgress, 1.0f);
        int color22 = AndroidUtilities.getOffsetColor(Theme.getColor("voipgroup_mutedIconUnscrolled"), Theme.getColor("voipgroup_mutedIcon"), finalColorProgress, 1.0f);
        int N = this.listView.getChildCount();
        for (int a = 0; a < N; a++) {
            View child = this.listView.getChildAt(a);
            if (child instanceof GroupCallTextCell) {
                ((GroupCallTextCell) child).setColors(color22, color3);
            } else if (child instanceof GroupCallUserCell) {
                ((GroupCallUserCell) child).setGrayIconColor(this.actionBar.getTag() != null ? "voipgroup_mutedIcon" : "voipgroup_mutedIconUnscrolled", color22);
            } else if (child instanceof GroupCallInvitedCell) {
                ((GroupCallInvitedCell) child).setGrayIconColor(this.actionBar.getTag() != null ? "voipgroup_mutedIcon" : "voipgroup_mutedIconUnscrolled", color22);
            }
        }
        this.containerView.invalidate();
        this.listView.invalidate();
        this.container.invalidate();
    }

    /* access modifiers changed from: private */
    public void getLink(boolean copy) {
        String url;
        TLRPC.Chat newChat = this.accountInstance.getMessagesController().getChat(Long.valueOf(this.currentChat.id));
        if (newChat != null && TextUtils.isEmpty(newChat.username)) {
            TLRPC.ChatFull chatFull = this.accountInstance.getMessagesController().getChatFull(this.currentChat.id);
            if (!TextUtils.isEmpty(this.currentChat.username)) {
                url = this.accountInstance.getMessagesController().linkPrefix + "/" + this.currentChat.username;
            } else {
                url = (chatFull == null || chatFull.exported_invite == null) ? null : chatFull.exported_invite.link;
            }
            if (TextUtils.isEmpty(url)) {
                TLRPC.TL_messages_exportChatInvite req = new TLRPC.TL_messages_exportChatInvite();
                req.peer = MessagesController.getInputPeer(this.currentChat);
                this.accountInstance.getConnectionsManager().sendRequest(req, new GroupCallActivity$$ExternalSyntheticLambda49(this, chatFull, copy));
                return;
            }
            openShareAlert(true, (String) null, url, copy);
        } else if (this.call != null) {
            int a = 0;
            while (a < 2) {
                int num = a;
                TLRPC.TL_phone_exportGroupCallInvite req2 = new TLRPC.TL_phone_exportGroupCallInvite();
                req2.call = this.call.getInputGroupCall();
                req2.can_self_unmute = a == 1;
                this.accountInstance.getConnectionsManager().sendRequest(req2, new GroupCallActivity$$ExternalSyntheticLambda46(this, num, copy));
                a++;
            }
        }
    }

    /* renamed from: lambda$getLink$39$org-telegram-ui-GroupCallActivity  reason: not valid java name */
    public /* synthetic */ void m3503lambda$getLink$39$orgtelegramuiGroupCallActivity(TLRPC.ChatFull chatFull, boolean copy, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new GroupCallActivity$$ExternalSyntheticLambda36(this, response, chatFull, copy));
    }

    /* renamed from: lambda$getLink$38$org-telegram-ui-GroupCallActivity  reason: not valid java name */
    public /* synthetic */ void m3502lambda$getLink$38$orgtelegramuiGroupCallActivity(TLObject response, TLRPC.ChatFull chatFull, boolean copy) {
        if (response instanceof TLRPC.TL_chatInviteExported) {
            TLRPC.TL_chatInviteExported invite = (TLRPC.TL_chatInviteExported) response;
            if (chatFull != null) {
                chatFull.exported_invite = invite;
            } else {
                openShareAlert(true, (String) null, invite.link, copy);
            }
        }
    }

    /* renamed from: lambda$getLink$41$org-telegram-ui-GroupCallActivity  reason: not valid java name */
    public /* synthetic */ void m3505lambda$getLink$41$orgtelegramuiGroupCallActivity(int num, boolean copy, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new GroupCallActivity$$ExternalSyntheticLambda35(this, response, num, copy));
    }

    /* renamed from: lambda$getLink$40$org-telegram-ui-GroupCallActivity  reason: not valid java name */
    public /* synthetic */ void m3504lambda$getLink$40$orgtelegramuiGroupCallActivity(TLObject response, int num, boolean copy) {
        if (response instanceof TLRPC.TL_phone_exportedGroupCallInvite) {
            this.invites[num] = ((TLRPC.TL_phone_exportedGroupCallInvite) response).link;
        } else {
            this.invites[num] = "";
        }
        int b = 0;
        while (b < 2) {
            String[] strArr = this.invites;
            if (strArr[b] != null) {
                if (strArr[b].length() == 0) {
                    this.invites[b] = null;
                }
                b++;
            } else {
                return;
            }
        }
        if (!copy && ChatObject.canManageCalls(this.currentChat) && !this.call.call.join_muted) {
            this.invites[0] = null;
        }
        String[] strArr2 = this.invites;
        if (strArr2[0] == null && strArr2[1] == null && !TextUtils.isEmpty(this.currentChat.username)) {
            openShareAlert(true, (String) null, this.accountInstance.getMessagesController().linkPrefix + "/" + this.currentChat.username, copy);
            return;
        }
        String[] strArr3 = this.invites;
        openShareAlert(false, strArr3[0], strArr3[1], copy);
    }

    /* JADX WARNING: Removed duplicated region for block: B:31:0x00d6  */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x00d9  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void openShareAlert(boolean r19, java.lang.String r20, java.lang.String r21, boolean r22) {
        /*
            r18 = this;
            r12 = r18
            boolean r0 = r18.isRtmpStream()
            if (r0 == 0) goto L_0x000a
            r0 = 0
            goto L_0x000c
        L_0x000a:
            r0 = r21
        L_0x000c:
            if (r22 == 0) goto L_0x0031
            if (r20 == 0) goto L_0x0013
            r1 = r20
            goto L_0x0014
        L_0x0013:
            r1 = r0
        L_0x0014:
            org.telegram.messenger.AndroidUtilities.addToClipboard(r1)
            boolean r1 = org.telegram.messenger.AndroidUtilities.shouldShowClipboardToast()
            if (r1 == 0) goto L_0x002c
            org.telegram.ui.Components.UndoView r2 = r18.getUndoView()
            r3 = 0
            r5 = 33
            r6 = 0
            r7 = 0
            r8 = 0
            r9 = 0
            r2.showWithAction((long) r3, (int) r5, (java.lang.Object) r6, (java.lang.Object) r7, (java.lang.Runnable) r8, (java.lang.Runnable) r9)
        L_0x002c:
            r15 = r0
            r0 = r20
            goto L_0x00e0
        L_0x0031:
            r1 = 0
            org.telegram.ui.LaunchActivity r2 = r12.parentActivity
            r3 = 1
            if (r2 == 0) goto L_0x0061
            org.telegram.ui.ActionBar.ActionBarLayout r2 = r2.getActionBarLayout()
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r2 = r2.fragmentsStack
            org.telegram.ui.LaunchActivity r4 = r12.parentActivity
            org.telegram.ui.ActionBar.ActionBarLayout r4 = r4.getActionBarLayout()
            java.util.ArrayList<org.telegram.ui.ActionBar.BaseFragment> r4 = r4.fragmentsStack
            int r4 = r4.size()
            int r4 = r4 - r3
            java.lang.Object r2 = r2.get(r4)
            org.telegram.ui.ActionBar.BaseFragment r2 = (org.telegram.ui.ActionBar.BaseFragment) r2
            boolean r4 = r2 instanceof org.telegram.ui.ChatActivity
            if (r4 == 0) goto L_0x0061
            r4 = r2
            org.telegram.ui.ChatActivity r4 = (org.telegram.ui.ChatActivity) r4
            boolean r1 = r4.needEnterText()
            r12.anyEnterEventSent = r3
            r12.enterEventSent = r3
            r13 = r1
            goto L_0x0062
        L_0x0061:
            r13 = r1
        L_0x0062:
            if (r20 == 0) goto L_0x006c
            if (r0 != 0) goto L_0x006c
            r0 = r20
            r1 = 0
            r15 = r0
            r14 = r1
            goto L_0x006f
        L_0x006c:
            r14 = r20
            r15 = r0
        L_0x006f:
            if (r14 != 0) goto L_0x009c
            if (r19 == 0) goto L_0x009c
            org.telegram.tgnet.TLRPC$Chat r0 = r12.currentChat
            boolean r0 = org.telegram.messenger.ChatObject.isChannelOrGiga(r0)
            r1 = 0
            if (r0 == 0) goto L_0x008c
            r0 = 2131628971(0x7f0e13ab, float:1.888525E38)
            java.lang.Object[] r2 = new java.lang.Object[r3]
            r2[r1] = r15
            java.lang.String r1 = "VoipChannelInviteText"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            r16 = r0
            goto L_0x009f
        L_0x008c:
            r0 = 2131629059(0x7f0e1403, float:1.8885428E38)
            java.lang.Object[] r2 = new java.lang.Object[r3]
            r2[r1] = r15
            java.lang.String r1 = "VoipGroupInviteText"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r1, r0, r2)
            r16 = r0
            goto L_0x009f
        L_0x009c:
            r0 = r15
            r16 = r0
        L_0x009f:
            org.telegram.ui.GroupCallActivity$46 r11 = new org.telegram.ui.GroupCallActivity$46
            android.content.Context r2 = r18.getContext()
            r3 = 0
            r4 = 0
            r7 = 0
            r10 = 0
            r17 = 1
            r0 = r11
            r1 = r18
            r5 = r16
            r6 = r14
            r8 = r15
            r9 = r14
            r20 = r14
            r14 = r11
            r11 = r17
            r0.<init>(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11)
            r12.shareAlert = r14
            org.telegram.ui.GroupCallActivity$47 r0 = new org.telegram.ui.GroupCallActivity$47
            r0.<init>()
            r14.setDelegate(r0)
            org.telegram.ui.Components.ShareAlert r0 = r12.shareAlert
            org.telegram.ui.GroupCallActivity$$ExternalSyntheticLambda3 r1 = new org.telegram.ui.GroupCallActivity$$ExternalSyntheticLambda3
            r1.<init>(r12)
            r0.setOnDismissListener(r1)
            org.telegram.ui.GroupCallActivity$$ExternalSyntheticLambda27 r0 = new org.telegram.ui.GroupCallActivity$$ExternalSyntheticLambda27
            r0.<init>(r12)
            if (r13 == 0) goto L_0x00d9
            r1 = 200(0xc8, double:9.9E-322)
            goto L_0x00db
        L_0x00d9:
            r1 = 0
        L_0x00db:
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r0, r1)
            r0 = r20
        L_0x00e0:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.GroupCallActivity.openShareAlert(boolean, java.lang.String, java.lang.String, boolean):void");
    }

    /* renamed from: lambda$openShareAlert$42$org-telegram-ui-GroupCallActivity  reason: not valid java name */
    public /* synthetic */ void m3535lambda$openShareAlert$42$orgtelegramuiGroupCallActivity(DialogInterface dialog) {
        this.shareAlert = null;
    }

    /* renamed from: lambda$openShareAlert$43$org-telegram-ui-GroupCallActivity  reason: not valid java name */
    public /* synthetic */ void m3536lambda$openShareAlert$43$orgtelegramuiGroupCallActivity() {
        ShareAlert shareAlert2 = this.shareAlert;
        if (shareAlert2 != null) {
            shareAlert2.show();
        }
    }

    /* access modifiers changed from: private */
    public void inviteUserToCall(long id, boolean shouldAdd) {
        TLRPC.User user;
        if (this.call != null && (user = this.accountInstance.getMessagesController().getUser(Long.valueOf(id))) != null) {
            AlertDialog[] progressDialog = {new AlertDialog(getContext(), 3)};
            TLRPC.TL_phone_inviteToGroupCall req = new TLRPC.TL_phone_inviteToGroupCall();
            req.call = this.call.getInputGroupCall();
            TLRPC.TL_inputUser inputUser = new TLRPC.TL_inputUser();
            inputUser.user_id = user.id;
            inputUser.access_hash = user.access_hash;
            req.users.add(inputUser);
            int requestId = this.accountInstance.getConnectionsManager().sendRequest(req, new GroupCallActivity$$ExternalSyntheticLambda47(this, id, progressDialog, user, shouldAdd, req));
            if (requestId != 0) {
                AndroidUtilities.runOnUIThread(new GroupCallActivity$$ExternalSyntheticLambda39(this, progressDialog, requestId), 500);
            }
        }
    }

    /* renamed from: lambda$inviteUserToCall$46$org-telegram-ui-GroupCallActivity  reason: not valid java name */
    public /* synthetic */ void m3508lambda$inviteUserToCall$46$orgtelegramuiGroupCallActivity(long id, AlertDialog[] progressDialog, TLRPC.User user, boolean shouldAdd, TLRPC.TL_phone_inviteToGroupCall req, TLObject response, TLRPC.TL_error error) {
        if (response != null) {
            this.accountInstance.getMessagesController().processUpdates((TLRPC.Updates) response, false);
            AndroidUtilities.runOnUIThread(new GroupCallActivity$$ExternalSyntheticLambda32(this, id, progressDialog, user));
            return;
        }
        AndroidUtilities.runOnUIThread(new GroupCallActivity$$ExternalSyntheticLambda40(this, progressDialog, shouldAdd, error, id, req));
    }

    /* renamed from: lambda$inviteUserToCall$44$org-telegram-ui-GroupCallActivity  reason: not valid java name */
    public /* synthetic */ void m3506lambda$inviteUserToCall$44$orgtelegramuiGroupCallActivity(long id, AlertDialog[] progressDialog, TLRPC.User user) {
        ChatObject.Call call2 = this.call;
        if (call2 != null && !this.delayedGroupCallUpdated) {
            call2.addInvitedUser(id);
            applyCallParticipantUpdates(true);
            GroupVoipInviteAlert groupVoipInviteAlert2 = this.groupVoipInviteAlert;
            if (groupVoipInviteAlert2 != null) {
                groupVoipInviteAlert2.dismiss();
            }
            try {
                progressDialog[0].dismiss();
            } catch (Throwable th) {
            }
            progressDialog[0] = null;
            getUndoView().showWithAction(0, 34, (Object) user, (Object) this.currentChat, (Runnable) null, (Runnable) null);
        }
    }

    /* renamed from: lambda$inviteUserToCall$45$org-telegram-ui-GroupCallActivity  reason: not valid java name */
    public /* synthetic */ void m3507lambda$inviteUserToCall$45$orgtelegramuiGroupCallActivity(AlertDialog[] progressDialog, boolean shouldAdd, TLRPC.TL_error error, long id, TLRPC.TL_phone_inviteToGroupCall req) {
        try {
            progressDialog[0].dismiss();
        } catch (Throwable th) {
        }
        progressDialog[0] = null;
        if (!shouldAdd || !"USER_NOT_PARTICIPANT".equals(error.text)) {
            AlertsCreator.processError(this.currentAccount, error, this.parentActivity.getActionBarLayout().fragmentsStack.get(this.parentActivity.getActionBarLayout().fragmentsStack.size() - 1), req, new Object[0]);
            return;
        }
        processSelectedOption((TLRPC.TL_groupCallParticipant) null, id, 3);
    }

    /* renamed from: lambda$inviteUserToCall$48$org-telegram-ui-GroupCallActivity  reason: not valid java name */
    public /* synthetic */ void m3510lambda$inviteUserToCall$48$orgtelegramuiGroupCallActivity(AlertDialog[] progressDialog, int requestId) {
        if (progressDialog[0] != null) {
            progressDialog[0].setOnCancelListener(new GroupCallActivity$$ExternalSyntheticLambda55(this, requestId));
            progressDialog[0].show();
        }
    }

    /* renamed from: lambda$inviteUserToCall$47$org-telegram-ui-GroupCallActivity  reason: not valid java name */
    public /* synthetic */ void m3509lambda$inviteUserToCall$47$orgtelegramuiGroupCallActivity(int requestId, DialogInterface dialog) {
        this.accountInstance.getConnectionsManager().cancelRequest(requestId, true);
    }

    public void invalidateActionBarAlpha() {
        ActionBar actionBar2 = this.actionBar;
        actionBar2.setAlpha((actionBar2.getTag() != null ? 1.0f : 0.0f) * (1.0f - this.renderersContainer.progressToFullscreenMode));
    }

    /* access modifiers changed from: private */
    public void updateLayout(boolean animated) {
        float minY = 2.14748365E9f;
        int N = this.listView.getChildCount();
        for (int a = 0; a < N; a++) {
            View child = this.listView.getChildAt(a);
            if (this.listView.getChildAdapterPosition(child) >= 0) {
                minY = Math.min(minY, (float) child.getTop());
            }
        }
        int a2 = 0;
        if (minY < 0.0f || minY == 2.14748365E9f) {
            minY = N != 0 ? 0.0f : (float) this.listView.getPaddingTop();
        }
        boolean z = false;
        final boolean show = minY <= ((float) (ActionBar.getCurrentActionBarHeight() - AndroidUtilities.dp(14.0f)));
        float minY2 = minY + ((float) (ActionBar.getCurrentActionBarHeight() + AndroidUtilities.dp(14.0f)));
        if ((show && this.actionBar.getTag() == null) || (!show && this.actionBar.getTag() != null)) {
            this.actionBar.setTag(show ? 1 : null);
            AnimatorSet animatorSet = this.actionBarAnimation;
            if (animatorSet != null) {
                animatorSet.cancel();
                this.actionBarAnimation = null;
            }
            setUseLightStatusBar(this.actionBar.getTag() == null);
            float f = 0.9f;
            ViewPropertyAnimator scaleX = this.actionBar.getBackButton().animate().scaleX(show ? 1.0f : 0.9f);
            if (show) {
                f = 1.0f;
            }
            scaleX.scaleY(f).translationX(show ? 0.0f : (float) (-AndroidUtilities.dp(14.0f))).setDuration(300).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
            this.actionBar.getTitleTextView().animate().translationY(show ? 0.0f : (float) AndroidUtilities.dp(23.0f)).setDuration(300).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
            ObjectAnimator objectAnimator = this.subtitleYAnimator;
            if (objectAnimator != null) {
                objectAnimator.removeAllListeners();
                this.subtitleYAnimator.cancel();
            }
            SimpleTextView subtitleTextView = this.actionBar.getSubtitleTextView();
            Property property = View.TRANSLATION_Y;
            float[] fArr = new float[2];
            fArr[0] = this.actionBar.getSubtitleTextView().getTranslationY();
            fArr[1] = show ? 0.0f : (float) AndroidUtilities.dp(20.0f);
            ObjectAnimator ofFloat = ObjectAnimator.ofFloat(subtitleTextView, property, fArr);
            this.subtitleYAnimator = ofFloat;
            ofFloat.setDuration(300);
            this.subtitleYAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
            this.subtitleYAnimator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    GroupCallActivity.this.subtitleYAnimator = null;
                    GroupCallActivity.this.actionBar.getSubtitleTextView().setTranslationY(show ? 0.0f : (float) AndroidUtilities.dp(20.0f));
                }
            });
            this.subtitleYAnimator.start();
            ObjectAnimator objectAnimator2 = this.additionalSubtitleYAnimator;
            if (objectAnimator2 != null) {
                objectAnimator2.cancel();
            }
            SimpleTextView additionalSubtitleTextView = this.actionBar.getAdditionalSubtitleTextView();
            Property property2 = View.TRANSLATION_Y;
            float[] fArr2 = new float[1];
            fArr2[0] = show ? 0.0f : (float) AndroidUtilities.dp(20.0f);
            ObjectAnimator ofFloat2 = ObjectAnimator.ofFloat(additionalSubtitleTextView, property2, fArr2);
            this.additionalSubtitleYAnimator = ofFloat2;
            ofFloat2.setDuration(300);
            this.additionalSubtitleYAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
            this.additionalSubtitleYAnimator.start();
            AnimatorSet animatorSet2 = new AnimatorSet();
            this.actionBarAnimation = animatorSet2;
            animatorSet2.setDuration(140);
            AnimatorSet animatorSet3 = this.actionBarAnimation;
            Animator[] animatorArr = new Animator[3];
            ActionBar actionBar2 = this.actionBar;
            Property property3 = View.ALPHA;
            float[] fArr3 = new float[1];
            fArr3[0] = show ? 1.0f : 0.0f;
            animatorArr[0] = ObjectAnimator.ofFloat(actionBar2, property3, fArr3);
            View view = this.actionBarBackground;
            Property property4 = View.ALPHA;
            float[] fArr4 = new float[1];
            fArr4[0] = show ? 1.0f : 0.0f;
            animatorArr[1] = ObjectAnimator.ofFloat(view, property4, fArr4);
            View view2 = this.actionBarShadow;
            Property property5 = View.ALPHA;
            float[] fArr5 = new float[1];
            if (show) {
                a2 = NUM;
            }
            fArr5[0] = a2;
            animatorArr[2] = ObjectAnimator.ofFloat(view2, property5, fArr5);
            animatorSet3.playTogether(animatorArr);
            this.actionBarAnimation.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    AnimatorSet unused = GroupCallActivity.this.actionBarAnimation = null;
                }
            });
            this.actionBarAnimation.start();
            ImageView imageView = this.renderersContainer.pipView;
            if (!show || isLandscapeMode) {
                z = true;
            }
            imageView.setClickable(z);
        }
        if (this.scrollOffsetY != minY2) {
            setScrollOffsetY(minY2);
        }
    }

    public void invalidateScrollOffsetY() {
        setScrollOffsetY(this.scrollOffsetY);
    }

    private void setScrollOffsetY(float scrollOffsetY2) {
        int diff;
        this.scrollOffsetY = scrollOffsetY2;
        this.listView.setTopGlowOffset((int) (scrollOffsetY2 - ((float) ((FrameLayout.LayoutParams) this.listView.getLayoutParams()).topMargin)));
        int offset = AndroidUtilities.dp(74.0f);
        float t = scrollOffsetY2 - ((float) offset);
        if (((float) this.backgroundPaddingTop) + t < ((float) (ActionBar.getCurrentActionBarHeight() * 2))) {
            float moveProgress = Math.min(1.0f, ((((float) (ActionBar.getCurrentActionBarHeight() * 2)) - t) - ((float) this.backgroundPaddingTop)) / ((float) (((offset - this.backgroundPaddingTop) - AndroidUtilities.dp(14.0f)) + ActionBar.getCurrentActionBarHeight())));
            diff = (int) (((float) AndroidUtilities.dp(AndroidUtilities.isTablet() ? 17.0f : 13.0f)) * moveProgress);
            if (Math.abs(Math.min(1.0f, moveProgress) - this.colorProgress) > 1.0E-4f) {
                setColorProgress(Math.min(1.0f, moveProgress));
            }
            this.titleTextView.setScaleX(Math.max(0.9f, 1.0f - ((moveProgress * 0.1f) * 1.2f)));
            this.titleTextView.setScaleY(Math.max(0.9f, 1.0f - ((0.1f * moveProgress) * 1.2f)));
            this.titleTextView.setAlpha(Math.max(0.0f, 1.0f - (1.2f * moveProgress)) * (1.0f - this.renderersContainer.progressToFullscreenMode));
        } else {
            diff = 0;
            this.titleTextView.setScaleX(1.0f);
            this.titleTextView.setScaleY(1.0f);
            this.titleTextView.setAlpha(1.0f - this.renderersContainer.progressToFullscreenMode);
            if (this.colorProgress > 1.0E-4f) {
                setColorProgress(0.0f);
            }
        }
        this.menuItemsContainer.setTranslationY(Math.max((float) AndroidUtilities.dp(4.0f), (scrollOffsetY2 - ((float) AndroidUtilities.dp(53.0f))) - ((float) diff)));
        this.titleTextView.setTranslationY(Math.max((float) AndroidUtilities.dp(4.0f), (scrollOffsetY2 - ((float) AndroidUtilities.dp(44.0f))) - ((float) diff)));
        LinearLayout linearLayout = this.scheduleTimerContainer;
        if (linearLayout != null) {
            linearLayout.setTranslationY(Math.max((float) AndroidUtilities.dp(4.0f), (scrollOffsetY2 - ((float) AndroidUtilities.dp(44.0f))) - ((float) diff)));
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
            MotionEvent cancel = MotionEvent.obtain(0, 0, 3, 0.0f, 0.0f, 0);
            this.muteButton.onTouchEvent(cancel);
            cancel.recycle();
        }
    }

    /* access modifiers changed from: private */
    public void updateState(boolean animated, boolean selfUpdated) {
        int state;
        boolean soundButtonVisible;
        boolean cameraButtonVisible;
        boolean flipButtonVisible;
        int i;
        float cameraScale;
        float flipButtonScale;
        GroupCallRenderersContainer groupCallRenderersContainer;
        int i2;
        boolean z = animated;
        ChatObject.Call call2 = this.call;
        int i3 = 6;
        boolean z2 = false;
        if (call2 == null || call2.isScheduled()) {
            if (ChatObject.canManageCalls(this.currentChat)) {
                state = 5;
            } else {
                if (this.call.call.schedule_start_subscribed) {
                    i3 = 7;
                }
                state = i3;
            }
            updateMuteButton(state, z);
            this.leaveButton.setData(isRtmpStream() ? NUM : NUM, -1, Theme.getColor("voipgroup_leaveButton"), 0.3f, false, LocaleController.getString("Close", NUM), false, false);
            updateScheduleUI(false);
            return;
        }
        VoIPService voIPService = VoIPService.getSharedInstance();
        if (voIPService != null) {
            int i4 = 4;
            if (voIPService.isSwitchingStream() || ((this.creatingServiceTime != 0 && Math.abs(SystemClock.elapsedRealtime() - this.creatingServiceTime) <= 3000) || !((i2 = this.currentCallState) == 1 || i2 == 2 || i2 == 6 || i2 == 5))) {
                if (this.userSwitchObject != null) {
                    getUndoView().showWithAction(0, 37, (Object) this.userSwitchObject, (Object) this.currentChat, (Runnable) null, (Runnable) null);
                    this.userSwitchObject = null;
                }
                TLRPC.TL_groupCallParticipant participant = this.call.participants.get(MessageObject.getPeerId(this.selfPeer));
                if (voIPService.micSwitching || participant == null || participant.can_self_unmute || !participant.muted || ChatObject.canManageCalls(this.currentChat)) {
                    boolean micMuted = voIPService.isMicMute();
                    if (!voIPService.micSwitching && selfUpdated && participant != null && participant.muted && !micMuted) {
                        cancelMutePress();
                        voIPService.setMicMute(true, false, false);
                        micMuted = true;
                    }
                    if (micMuted) {
                        updateMuteButton(0, z);
                    } else {
                        updateMuteButton(1, z);
                    }
                } else {
                    cancelMutePress();
                    if (participant.raise_hand_rating != 0) {
                        updateMuteButton(4, z);
                    } else {
                        updateMuteButton(2, z);
                    }
                    voIPService.setMicMute(true, false, false);
                }
            } else {
                cancelMutePress();
                updateMuteButton(3, z);
            }
            boolean outgoingVideoIsActive = VoIPService.getSharedInstance() != null && VoIPService.getSharedInstance().getVideoState(false) == 2;
            TLRPC.TL_groupCallParticipant participant2 = this.call.participants.get(MessageObject.getPeerId(this.selfPeer));
            if ((((participant2 != null && !participant2.can_self_unmute && participant2.muted && !ChatObject.canManageCalls(this.currentChat)) || !this.call.canRecordVideo()) && !outgoingVideoIsActive) || isRtmpStream()) {
                cameraButtonVisible = false;
                soundButtonVisible = true;
            } else {
                cameraButtonVisible = true;
                soundButtonVisible = false;
            }
            if (outgoingVideoIsActive) {
                if (z && this.flipButton.getVisibility() != 0) {
                    this.flipButton.setScaleX(0.3f);
                    this.flipButton.setScaleY(0.3f);
                }
                flipButtonVisible = true;
            } else {
                flipButtonVisible = false;
            }
            int i5 = flipButtonVisible + (soundButtonVisible ? 2 : 0);
            if (!cameraButtonVisible) {
                i4 = 0;
            }
            int i6 = i5 + i4;
            GroupCallRenderersContainer groupCallRenderersContainer2 = this.renderersContainer;
            int newButtonsVisibility = i6 + ((groupCallRenderersContainer2 == null || !groupCallRenderersContainer2.inFullscreenMode) ? 0 : 8);
            int i7 = this.buttonsVisibility;
            if (!(i7 == 0 || i7 == newButtonsVisibility || !z)) {
                for (int i8 = 0; i8 < this.buttonsContainer.getChildCount(); i8++) {
                    View child = this.buttonsContainer.getChildAt(i8);
                    if (child.getVisibility() == 0) {
                        this.buttonsAnimationParamsX.put(child, Float.valueOf(child.getX()));
                        this.buttonsAnimationParamsY.put(child, Float.valueOf(child.getY()));
                    }
                }
                this.animateButtonsOnNextLayout = true;
            }
            boolean soundButtonChanged = (this.buttonsVisibility | 2) != (newButtonsVisibility | 2);
            this.buttonsVisibility = newButtonsVisibility;
            if (cameraButtonVisible) {
                int i9 = newButtonsVisibility;
                this.cameraButton.setData(NUM, -1, 0, 1.0f, true, LocaleController.getString("VoipCamera", NUM), !outgoingVideoIsActive, animated);
                this.cameraButton.setChecked(true, false);
                i = 8;
            } else {
                i = 8;
                this.cameraButton.setVisibility(8);
            }
            if (flipButtonVisible) {
                this.flipButton.setData(0, -1, 0, 1.0f, true, LocaleController.getString("VoipFlip", NUM), false, false);
                this.flipButton.setChecked(true, false);
            } else {
                this.flipButton.setVisibility(i);
            }
            boolean soundButtonWasVisible = this.soundButton.getVisibility() == 0;
            this.soundButton.setVisibility(soundButtonVisible ? 0 : 8);
            if (soundButtonChanged && soundButtonVisible) {
                updateSpeakerPhoneIcon(false);
            }
            float f = 1.0f;
            if (soundButtonChanged) {
                float s = soundButtonVisible ? 1.0f : 0.3f;
                if (!z) {
                    this.soundButton.animate().cancel();
                    this.soundButton.setScaleX(s);
                    this.soundButton.setScaleY(s);
                } else {
                    if (soundButtonVisible && !soundButtonWasVisible) {
                        this.soundButton.setScaleX(0.3f);
                        this.soundButton.setScaleY(0.3f);
                    }
                    this.soundButton.animate().scaleX(s).scaleY(s).setDuration(350).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
                }
            }
            if (this.cameraButton.getVisibility() == 0) {
                cameraScale = 1.0f;
                this.cameraButton.showText(1.0f == 1.0f, z);
            } else {
                cameraScale = 0.3f;
            }
            if (this.cameraButtonScale != cameraScale) {
                this.cameraButtonScale = cameraScale;
                if (!z) {
                    this.cameraButton.animate().cancel();
                    this.cameraButton.setScaleX(cameraScale);
                    this.cameraButton.setScaleY(cameraScale);
                } else {
                    this.cameraButton.animate().scaleX(cameraScale).scaleY(cameraScale).setDuration(350).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
                }
            }
            if (isTabletMode) {
                flipButtonScale = 0.8f;
            } else {
                flipButtonScale = (isLandscapeMode || ((groupCallRenderersContainer = this.renderersContainer) != null && groupCallRenderersContainer.inFullscreenMode)) ? 1.0f : 0.8f;
            }
            if (!outgoingVideoIsActive) {
                flipButtonScale = 0.3f;
            }
            if (!z) {
                this.flipButton.animate().cancel();
                this.flipButton.setScaleX(flipButtonScale);
                this.flipButton.setScaleY(flipButtonScale);
            } else {
                this.flipButton.animate().scaleX(flipButtonScale).scaleY(flipButtonScale).setDuration(350).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
            }
            VoIPToggleButton voIPToggleButton = this.flipButton;
            if (flipButtonScale == 1.0f) {
                z2 = true;
            }
            voIPToggleButton.showText(z2, z);
            if (outgoingVideoIsActive) {
                f = 0.3f;
            }
            float soundButtonScale2 = f;
            if (this.soundButtonScale != soundButtonScale2) {
                this.soundButtonScale = soundButtonScale2;
                if (!z) {
                    this.soundButton.animate().cancel();
                    this.soundButton.setScaleX(soundButtonScale2);
                    this.soundButton.setScaleY(soundButtonScale2);
                    return;
                }
                this.soundButton.animate().scaleX(soundButtonScale2).scaleY(soundButtonScale2).setDuration(350).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
            }
        }
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

    private void updateSpeakerPhoneIcon(boolean animated) {
        boolean z = animated;
        VoIPToggleButton voIPToggleButton = this.soundButton;
        if (voIPToggleButton != null && voIPToggleButton.getVisibility() == 0) {
            VoIPService service = VoIPService.getSharedInstance();
            boolean checked = false;
            if (service == null || isRtmpStream()) {
                this.soundButton.setData(NUM, -1, 0, 0.3f, true, LocaleController.getString("VoipChatShare", NUM), false, animated);
                this.soundButton.setEnabled(!TextUtils.isEmpty(this.currentChat.username) || (ChatObject.hasAdminRights(this.currentChat) && ChatObject.canAddUsers(this.currentChat)), false);
                this.soundButton.setChecked(true, false);
                return;
            }
            this.soundButton.setEnabled(true, z);
            boolean bluetooth = service.isBluetoothOn() || service.isBluetoothWillOn();
            if (!bluetooth && service.isSpeakerphoneOn()) {
                checked = true;
            }
            if (bluetooth) {
                this.soundButton.setData(NUM, -1, 0, 0.1f, true, LocaleController.getString("VoipAudioRoutingBluetooth", NUM), false, animated);
            } else if (checked) {
                this.soundButton.setData(NUM, -1, 0, 0.3f, true, LocaleController.getString("VoipSpeaker", NUM), false, animated);
            } else if (service.isHeadsetPlugged()) {
                this.soundButton.setData(NUM, -1, 0, 0.1f, true, LocaleController.getString("VoipAudioRoutingHeadset", NUM), false, animated);
            } else {
                this.soundButton.setData(NUM, -1, 0, 0.1f, true, LocaleController.getString("VoipSpeaker", NUM), false, animated);
            }
            this.soundButton.setChecked(checked, z);
        }
    }

    /* access modifiers changed from: private */
    public void updateMuteButton(int state, boolean animated) {
        boolean changed;
        String newSubtext;
        String newText;
        String contentDescription;
        int i;
        float f;
        float multiplier;
        float f2;
        float multiplier2;
        boolean changed2;
        int i2 = state;
        boolean z = animated;
        GroupCallRenderersContainer groupCallRenderersContainer = this.renderersContainer;
        boolean fullscreen = groupCallRenderersContainer != null && groupCallRenderersContainer.inFullscreenMode && (AndroidUtilities.isTablet() || isLandscapeMode == isRtmpLandscapeMode());
        if (isRtmpStream() || this.muteButtonState != i2 || !z) {
            ValueAnimator valueAnimator = this.muteButtonAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
                this.muteButtonAnimator = null;
            }
            ValueAnimator valueAnimator2 = this.expandAnimator;
            if (valueAnimator2 != null) {
                valueAnimator2.cancel();
                this.expandAnimator = null;
            }
            boolean mutedByAdmin = false;
            if (i2 == 7) {
                newText = LocaleController.getString("VoipGroupCancelReminder", NUM);
                newSubtext = "";
                changed = this.bigMicDrawable.setCustomEndFrame(202);
            } else if (i2 == 6) {
                newText = LocaleController.getString("VoipGroupSetReminder", NUM);
                newSubtext = "";
                changed = this.bigMicDrawable.setCustomEndFrame(344);
            } else if (i2 == 5) {
                newText = LocaleController.getString("VoipGroupStartNow", NUM);
                newSubtext = "";
                changed = this.bigMicDrawable.setCustomEndFrame(377);
            } else if (i2 == 0) {
                newText = LocaleController.getString("VoipGroupUnmute", NUM);
                newSubtext = LocaleController.getString("VoipHoldAndTalk", NUM);
                int i3 = this.muteButtonState;
                if (i3 == 3) {
                    int endFrame = this.bigMicDrawable.getCustomEndFrame();
                    if (endFrame == 136 || endFrame == 173 || endFrame == 274 || endFrame == 311) {
                        changed = this.bigMicDrawable.setCustomEndFrame(99);
                    } else {
                        changed = false;
                    }
                } else if (i3 == 5) {
                    changed = this.bigMicDrawable.setCustomEndFrame(404);
                } else if (i3 == 7) {
                    changed = this.bigMicDrawable.setCustomEndFrame(376);
                } else if (i3 == 6) {
                    changed = this.bigMicDrawable.setCustomEndFrame(237);
                } else if (i3 == 2) {
                    changed = this.bigMicDrawable.setCustomEndFrame(36);
                } else {
                    changed = this.bigMicDrawable.setCustomEndFrame(99);
                }
            } else if (i2 == 1) {
                newText = LocaleController.getString("VoipTapToMute", NUM);
                newSubtext = "";
                changed = this.bigMicDrawable.setCustomEndFrame(this.muteButtonState == 4 ? 99 : 69);
            } else if (i2 == 4) {
                newText = LocaleController.getString("VoipMutedTapedForSpeak", NUM);
                newSubtext = LocaleController.getString("VoipMutedTapedForSpeakInfo", NUM);
                changed = this.bigMicDrawable.setCustomEndFrame(136);
            } else {
                TLRPC.TL_groupCallParticipant participant = this.call.participants.get(MessageObject.getPeerId(this.selfPeer));
                boolean z2 = participant != null && !participant.can_self_unmute && participant.muted && !ChatObject.canManageCalls(this.currentChat);
                mutedByAdmin = z2;
                if (z2) {
                    int i4 = this.muteButtonState;
                    if (i4 == 7) {
                        changed2 = this.bigMicDrawable.setCustomEndFrame(311);
                    } else if (i4 == 6) {
                        changed2 = this.bigMicDrawable.setCustomEndFrame(274);
                    } else if (i4 == 1) {
                        changed2 = this.bigMicDrawable.setCustomEndFrame(173);
                    } else {
                        changed2 = this.bigMicDrawable.setCustomEndFrame(136);
                    }
                } else {
                    int i5 = this.muteButtonState;
                    if (i5 == 5) {
                        changed2 = this.bigMicDrawable.setCustomEndFrame(404);
                    } else if (i5 == 7) {
                        changed2 = this.bigMicDrawable.setCustomEndFrame(376);
                    } else if (i5 == 6) {
                        changed2 = this.bigMicDrawable.setCustomEndFrame(237);
                    } else if (i5 == 2 || i5 == 4) {
                        changed2 = this.bigMicDrawable.setCustomEndFrame(36);
                    } else {
                        changed2 = this.bigMicDrawable.setCustomEndFrame(99);
                    }
                }
                if (i2 == 3) {
                    changed = changed2;
                    newText = LocaleController.getString("Connecting", NUM);
                    newSubtext = "";
                } else {
                    newText = LocaleController.getString("VoipMutedByAdmin", NUM);
                    changed = changed2;
                    newSubtext = LocaleController.getString("VoipMutedTapForSpeak", NUM);
                }
            }
            if (isRtmpStream() && i2 != 3 && !this.call.isScheduled()) {
                newText = LocaleController.getString(fullscreen ? NUM : NUM);
                newSubtext = "";
                changed = this.animatingToFullscreenExpand != fullscreen;
                this.animatingToFullscreenExpand = fullscreen;
            }
            if (!TextUtils.isEmpty(newSubtext)) {
                contentDescription = newText + " " + newSubtext;
            } else {
                contentDescription = newText;
            }
            this.muteButton.setContentDescription(contentDescription);
            if (z) {
                if (!changed) {
                    i = 0;
                } else if (i2 == 5) {
                    this.bigMicDrawable.setCurrentFrame(376);
                    i = 0;
                } else if (i2 == 7) {
                    this.bigMicDrawable.setCurrentFrame(173);
                    i = 0;
                } else if (i2 == 6) {
                    this.bigMicDrawable.setCurrentFrame(311);
                    i = 0;
                } else if (i2 == 0) {
                    int i6 = this.muteButtonState;
                    if (i6 == 5) {
                        this.bigMicDrawable.setCurrentFrame(376);
                        i = 0;
                    } else if (i6 == 7) {
                        this.bigMicDrawable.setCurrentFrame(344);
                        i = 0;
                    } else if (i6 == 6) {
                        this.bigMicDrawable.setCurrentFrame(202);
                        i = 0;
                    } else if (i6 == 2) {
                        i = 0;
                        this.bigMicDrawable.setCurrentFrame(0);
                    } else {
                        this.bigMicDrawable.setCurrentFrame(69);
                        i = 0;
                    }
                } else if (i2 == 1) {
                    this.bigMicDrawable.setCurrentFrame(this.muteButtonState == 4 ? 69 : 36);
                    i = 0;
                } else if (i2 == 4) {
                    this.bigMicDrawable.setCurrentFrame(99);
                    i = 0;
                } else if (mutedByAdmin) {
                    int i7 = this.muteButtonState;
                    if (i7 == 7) {
                        this.bigMicDrawable.setCurrentFrame(274);
                        i = 0;
                    } else if (i7 == 6) {
                        this.bigMicDrawable.setCurrentFrame(237);
                        i = 0;
                    } else if (i7 == 1) {
                        this.bigMicDrawable.setCurrentFrame(136);
                        i = 0;
                    } else {
                        this.bigMicDrawable.setCurrentFrame(99);
                        i = 0;
                    }
                } else {
                    int i8 = this.muteButtonState;
                    if (i8 == 5) {
                        this.bigMicDrawable.setCurrentFrame(376);
                        i = 0;
                    } else if (i8 == 7) {
                        this.bigMicDrawable.setCurrentFrame(344);
                        i = 0;
                    } else if (i8 == 6) {
                        this.bigMicDrawable.setCurrentFrame(202);
                        i = 0;
                    } else if (i8 == 2 || i8 == 4) {
                        i = 0;
                        this.bigMicDrawable.setCurrentFrame(0);
                    } else {
                        this.bigMicDrawable.setCurrentFrame(69);
                        i = 0;
                    }
                }
                this.muteButton.playAnimation();
                this.muteLabel[1].setVisibility(i);
                this.muteLabel[1].setAlpha(0.0f);
                this.muteLabel[1].setTranslationY((float) (-AndroidUtilities.dp(5.0f)));
                this.muteLabel[1].setText(newText);
                if (!isRtmpStream() || this.call.isScheduled()) {
                    this.muteButton.setAlpha(1.0f);
                    this.expandButton.setAlpha(0.0f);
                    this.minimizeButton.setAlpha(0.0f);
                } else {
                    this.muteButton.setAlpha(0.0f);
                    boolean isExpanded = this.renderersContainer.inFullscreenMode && (AndroidUtilities.isTablet() || isLandscapeMode == isRtmpLandscapeMode());
                    View hideView = isExpanded ? this.expandButton : this.minimizeButton;
                    View showView = isExpanded ? this.minimizeButton : this.expandButton;
                    float muteButtonScale = ((float) AndroidUtilities.dp(52.0f)) / ((float) (this.muteButton.getMeasuredWidth() - AndroidUtilities.dp(8.0f)));
                    boolean bigSize = !AndroidUtilities.isTablet() ? !(this.renderersContainer.inFullscreenMode || isLandscapeMode) : !this.renderersContainer.inFullscreenMode;
                    Boolean bool = this.wasExpandBigSize;
                    boolean changedSize = bool == null || bigSize != bool.booleanValue();
                    this.wasExpandBigSize = Boolean.valueOf(bigSize);
                    ValueAnimator valueAnimator3 = this.expandSizeAnimator;
                    if (valueAnimator3 != null) {
                        valueAnimator3.cancel();
                        this.expandSizeAnimator = null;
                    }
                    if (changedSize) {
                        boolean z3 = fullscreen;
                        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
                        this.expandSizeAnimator = ofFloat;
                        ofFloat.addUpdateListener(new GroupCallActivity$$ExternalSyntheticLambda33(this, muteButtonScale, showView));
                        this.expandSizeAnimator.addListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animation) {
                                ValueAnimator unused = GroupCallActivity.this.expandSizeAnimator = null;
                            }
                        });
                        this.expandSizeAnimator.start();
                    } else {
                        if (isLandscapeMode) {
                            multiplier2 = muteButtonScale;
                            f2 = 1.0f;
                        } else {
                            f2 = 1.0f;
                            multiplier2 = AndroidUtilities.lerp(1.0f, muteButtonScale, this.renderersContainer.progressToFullscreenMode);
                        }
                        showView.setAlpha(f2);
                        showView.setScaleX(multiplier2);
                        showView.setScaleY(multiplier2);
                        hideView.setAlpha(0.0f);
                    }
                    if (changed) {
                        ValueAnimator ofFloat2 = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
                        this.expandAnimator = ofFloat2;
                        ofFloat2.addUpdateListener(new GroupCallActivity$$ExternalSyntheticLambda44(this, muteButtonScale, hideView, showView));
                        this.expandAnimator.addListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animation) {
                                ValueAnimator unused = GroupCallActivity.this.expandAnimator = null;
                            }
                        });
                        this.expandAnimator.start();
                    } else {
                        if (isLandscapeMode) {
                            multiplier = muteButtonScale;
                            f = 1.0f;
                        } else {
                            f = 1.0f;
                            multiplier = AndroidUtilities.lerp(1.0f, muteButtonScale, this.renderersContainer.progressToFullscreenMode);
                        }
                        showView.setAlpha(f);
                        showView.setScaleX(multiplier);
                        showView.setScaleY(multiplier);
                        hideView.setAlpha(0.0f);
                    }
                }
                if (changed) {
                    ValueAnimator ofFloat3 = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
                    this.muteButtonAnimator = ofFloat3;
                    ofFloat3.addUpdateListener(new GroupCallActivity$$ExternalSyntheticLambda11(this));
                    this.muteButtonAnimator.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animation) {
                            if (GroupCallActivity.this.muteButtonAnimator != null) {
                                ValueAnimator unused = GroupCallActivity.this.muteButtonAnimator = null;
                                TextView temp = GroupCallActivity.this.muteLabel[0];
                                GroupCallActivity.this.muteLabel[0] = GroupCallActivity.this.muteLabel[1];
                                GroupCallActivity.this.muteLabel[1] = temp;
                                temp.setVisibility(4);
                                for (int a = 0; a < 2; a++) {
                                    GroupCallActivity.this.muteLabel[a].setTranslationY(0.0f);
                                }
                            }
                        }
                    });
                    this.muteButtonAnimator.setDuration(180);
                    this.muteButtonAnimator.start();
                } else {
                    this.muteLabel[0].setAlpha(0.0f);
                    this.muteLabel[1].setAlpha(1.0f);
                    TextView[] textViewArr = this.muteLabel;
                    TextView temp = textViewArr[0];
                    textViewArr[0] = textViewArr[1];
                    textViewArr[1] = temp;
                    temp.setVisibility(4);
                    for (int a = 0; a < 2; a++) {
                        this.muteLabel[a].setTranslationY(0.0f);
                    }
                }
                this.muteButtonState = i2;
            } else {
                this.muteButtonState = i2;
                RLottieDrawable rLottieDrawable = this.bigMicDrawable;
                boolean z4 = true;
                rLottieDrawable.setCurrentFrame(rLottieDrawable.getCustomEndFrame() - 1, false, true);
                this.muteLabel[0].setText(newText);
                if (!isRtmpStream() || this.call.isScheduled()) {
                    this.muteButton.setAlpha(1.0f);
                    this.expandButton.setAlpha(0.0f);
                    this.minimizeButton.setAlpha(0.0f);
                } else {
                    this.muteButton.setAlpha(0.0f);
                    GroupCallRenderersContainer groupCallRenderersContainer2 = this.renderersContainer;
                    if (groupCallRenderersContainer2 == null || !groupCallRenderersContainer2.inFullscreenMode || (!AndroidUtilities.isTablet() && isLandscapeMode != isRtmpLandscapeMode())) {
                        z4 = false;
                    }
                    boolean isExpanded2 = z4;
                    View hideView2 = isExpanded2 ? this.expandButton : this.minimizeButton;
                    (isExpanded2 ? this.minimizeButton : this.expandButton).setAlpha(1.0f);
                    hideView2.setAlpha(0.0f);
                }
            }
            updateMuteButtonState(z);
        }
    }

    /* renamed from: lambda$updateMuteButton$49$org-telegram-ui-GroupCallActivity  reason: not valid java name */
    public /* synthetic */ void m3546lambda$updateMuteButton$49$orgtelegramuiGroupCallActivity(float muteButtonScale, View showView, ValueAnimator animation) {
        float multiplier = isLandscapeMode ? muteButtonScale : AndroidUtilities.lerp(1.0f, muteButtonScale, this.renderersContainer.progressToFullscreenMode);
        showView.setScaleX(multiplier);
        showView.setScaleY(multiplier);
    }

    /* renamed from: lambda$updateMuteButton$50$org-telegram-ui-GroupCallActivity  reason: not valid java name */
    public /* synthetic */ void m3547lambda$updateMuteButton$50$orgtelegramuiGroupCallActivity(float muteButtonScale, View hideView, View showView, ValueAnimator animation) {
        float val = ((Float) animation.getAnimatedValue()).floatValue();
        float multiplier = isLandscapeMode ? muteButtonScale : AndroidUtilities.lerp(1.0f, muteButtonScale, this.renderersContainer.progressToFullscreenMode);
        hideView.setAlpha(1.0f - val);
        float scale = (((1.0f - val) * 0.9f) + 0.1f) * multiplier;
        hideView.setScaleX(scale);
        hideView.setScaleY(scale);
        showView.setAlpha(val);
        float scale2 = ((0.9f * val) + 0.1f) * multiplier;
        showView.setScaleX(scale2);
        showView.setScaleY(scale2);
    }

    /* renamed from: lambda$updateMuteButton$51$org-telegram-ui-GroupCallActivity  reason: not valid java name */
    public /* synthetic */ void m3548lambda$updateMuteButton$51$orgtelegramuiGroupCallActivity(ValueAnimator animation) {
        float v = ((Float) animation.getAnimatedValue()).floatValue();
        this.muteLabel[0].setAlpha(1.0f - v);
        this.muteLabel[0].setTranslationY(((float) AndroidUtilities.dp(5.0f)) * v);
        this.muteLabel[1].setAlpha(v);
        this.muteLabel[1].setTranslationY((float) AndroidUtilities.dp((5.0f * v) - 0.875f));
    }

    /* access modifiers changed from: private */
    public void fillColors(int state, int[] colorsToSet) {
        if (state == 0) {
            colorsToSet[0] = Theme.getColor("voipgroup_unmuteButton2");
            colorsToSet[1] = AndroidUtilities.getOffsetColor(Theme.getColor("voipgroup_soundButtonActive"), Theme.getColor("voipgroup_soundButtonActiveScrolled"), this.colorProgress, 1.0f);
            colorsToSet[2] = Theme.getColor("voipgroup_soundButton");
        } else if (state == 1) {
            colorsToSet[0] = Theme.getColor("voipgroup_muteButton2");
            colorsToSet[1] = AndroidUtilities.getOffsetColor(Theme.getColor("voipgroup_soundButtonActive2"), Theme.getColor("voipgroup_soundButtonActive2Scrolled"), this.colorProgress, 1.0f);
            colorsToSet[2] = Theme.getColor("voipgroup_soundButton2");
        } else if (isGradientState(state)) {
            colorsToSet[0] = Theme.getColor("voipgroup_mutedByAdminGradient3");
            colorsToSet[1] = Theme.getColor("voipgroup_mutedByAdminMuteButton");
            colorsToSet[2] = Theme.getColor("voipgroup_mutedByAdminMuteButtonDisabled");
        } else {
            colorsToSet[0] = Theme.getColor("voipgroup_disabledButton");
            colorsToSet[1] = AndroidUtilities.getOffsetColor(Theme.getColor("voipgroup_disabledButtonActive"), Theme.getColor("voipgroup_disabledButtonActiveScrolled"), this.colorProgress, 1.0f);
            colorsToSet[2] = AndroidUtilities.getOffsetColor(Theme.getColor("voipgroup_listViewBackgroundUnscrolled"), Theme.getColor("voipgroup_disabledButton"), this.colorProgress, 1.0f);
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
            if (ChatObject.isChannelOrGiga(this.currentChat)) {
                this.recordHintView.setText(LocaleController.getString("VoipChannelRecording", NUM));
            } else {
                this.recordHintView.setText(LocaleController.getString("VoipGroupRecording", NUM));
            }
            this.recordHintView.setBackgroundColor(-NUM, -1);
        }
        this.recordHintView.setExtraTranslationY((float) (-AndroidUtilities.statusBarHeight));
        this.recordHintView.showForView(view, true);
    }

    /* access modifiers changed from: private */
    public void showReminderHint() {
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        if (!preferences.getBoolean("reminderhint", false)) {
            preferences.edit().putBoolean("reminderhint", true).commit();
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

    private void updateMuteButtonState(boolean animated) {
        this.muteButton.invalidate();
        WeavingState[] weavingStateArr = this.states;
        int i = this.muteButtonState;
        boolean z = false;
        if (weavingStateArr[i] == null) {
            weavingStateArr[i] = new WeavingState(this.muteButtonState);
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
            if (weavingState2 == null || !animated) {
                this.switchProgress = 1.0f;
                this.prevState = null;
            } else {
                this.switchProgress = 0.0f;
            }
        }
        if (!animated) {
            boolean showWaves = false;
            boolean showLighting = false;
            WeavingState weavingState3 = this.currentState;
            if (weavingState3 != null) {
                showWaves = weavingState3.currentState == 1 || this.currentState.currentState == 0;
                if (this.currentState.currentState != 3) {
                    z = true;
                }
                showLighting = z;
            }
            this.showWavesProgress = showWaves ? 1.0f : 0.0f;
            if (showLighting) {
                f = 1.0f;
            }
            this.showLightingProgress = f;
        }
        this.buttonsContainer.invalidate();
    }

    /* access modifiers changed from: private */
    public static void processOnLeave(ChatObject.Call call2, boolean discard, long selfId, Runnable onLeave) {
        if (VoIPService.getSharedInstance() != null) {
            VoIPService.getSharedInstance().hangUp((int) discard);
        }
        if (call2 != null) {
            TLRPC.TL_groupCallParticipant participant = call2.participants.get(selfId);
            if (participant != null) {
                call2.participants.delete(selfId);
                call2.sortedParticipants.remove(participant);
                call2.visibleParticipants.remove(participant);
                int i = 0;
                while (i < call2.visibleVideoParticipants.size()) {
                    if (MessageObject.getPeerId(call2.visibleVideoParticipants.get(i).participant.peer) == MessageObject.getPeerId(participant.peer)) {
                        call2.visibleVideoParticipants.remove(i);
                        i--;
                    }
                    i++;
                }
                TLRPC.GroupCall groupCall = call2.call;
                groupCall.participants_count--;
            }
            for (int i2 = 0; i2 < call2.sortedParticipants.size(); i2++) {
                TLRPC.TL_groupCallParticipant participant1 = call2.sortedParticipants.get(i2);
                participant1.lastActiveDate = participant1.lastSpeakTime;
            }
        }
        if (onLeave != null) {
            onLeave.run();
        }
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didStartedCall, new Object[0]);
    }

    public static void onLeaveClick(Context context, Runnable onLeave, boolean fromOverlayWindow) {
        Context context2 = context;
        VoIPService service = VoIPService.getSharedInstance();
        if (service != null) {
            TLRPC.Chat currentChat2 = service.getChat();
            ChatObject.Call call2 = service.groupCall;
            long selfId = service.getSelfId();
            if (!ChatObject.canManageCalls(currentChat2)) {
                processOnLeave(call2, false, selfId, onLeave);
                return;
            }
            Runnable runnable = onLeave;
            AlertDialog.Builder builder = new AlertDialog.Builder(context2);
            if (ChatObject.isChannelOrGiga(currentChat2)) {
                builder.setTitle(LocaleController.getString("VoipChannelLeaveAlertTitle", NUM));
                builder.setMessage(LocaleController.getString("VoipChannelLeaveAlertText", NUM));
            } else {
                builder.setTitle(LocaleController.getString("VoipGroupLeaveAlertTitle", NUM));
                builder.setMessage(LocaleController.getString("VoipGroupLeaveAlertText", NUM));
            }
            int account = service.getAccount();
            LinearLayout linearLayout = new LinearLayout(context2);
            linearLayout.setOrientation(1);
            CheckBoxCell[] cells = {new CheckBoxCell(context2, 1)};
            cells[0].setBackgroundDrawable(Theme.getSelectorDrawable(false));
            if (fromOverlayWindow) {
                cells[0].setTextColor(Theme.getColor("dialogTextBlack"));
            } else {
                cells[0].setTextColor(Theme.getColor("voipgroup_actionBarItems"));
                ((CheckBoxSquare) cells[0].getCheckBoxView()).setColors("voipgroup_mutedIcon", "voipgroup_listeningText", "voipgroup_nameText");
            }
            cells[0].setTag(0);
            if (ChatObject.isChannelOrGiga(currentChat2)) {
                cells[0].setText(LocaleController.getString("VoipChannelLeaveAlertEndChat", NUM), "", false, false);
            } else {
                cells[0].setText(LocaleController.getString("VoipGroupLeaveAlertEndChat", NUM), "", false, false);
            }
            cells[0].setPadding(LocaleController.isRTL ? AndroidUtilities.dp(16.0f) : AndroidUtilities.dp(8.0f), 0, LocaleController.isRTL ? AndroidUtilities.dp(8.0f) : AndroidUtilities.dp(16.0f), 0);
            linearLayout.addView(cells[0], LayoutHelper.createLinear(-1, -2));
            cells[0].setOnClickListener(new GroupCallActivity$$ExternalSyntheticLambda18(cells));
            builder.setCustomViewOffset(12);
            builder.setView(linearLayout);
            builder.setDialogButtonColorKey("voipgroup_listeningText");
            String string = LocaleController.getString("VoipGroupLeave", NUM);
            GroupCallActivity$$ExternalSyntheticLambda62 groupCallActivity$$ExternalSyntheticLambda62 = r3;
            VoIPService voIPService = service;
            LinearLayout linearLayout2 = linearLayout;
            CheckBoxCell[] checkBoxCellArr = cells;
            GroupCallActivity$$ExternalSyntheticLambda62 groupCallActivity$$ExternalSyntheticLambda622 = new GroupCallActivity$$ExternalSyntheticLambda62(call2, cells, selfId, onLeave);
            builder.setPositiveButton(string, groupCallActivity$$ExternalSyntheticLambda62);
            builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
            if (fromOverlayWindow) {
                builder.setDimEnabled(false);
            }
            AlertDialog dialog = builder.create();
            if (fromOverlayWindow) {
                if (Build.VERSION.SDK_INT >= 26) {
                    dialog.getWindow().setType(2038);
                } else {
                    dialog.getWindow().setType(2003);
                }
                dialog.getWindow().clearFlags(2);
            }
            if (!fromOverlayWindow) {
                dialog.setBackgroundColor(Theme.getColor("voipgroup_dialogBackground"));
            }
            dialog.show();
            if (!fromOverlayWindow) {
                TextView button = (TextView) dialog.getButton(-1);
                if (button != null) {
                    button.setTextColor(Theme.getColor("voipgroup_leaveCallMenu"));
                }
                dialog.setTextColor(Theme.getColor("voipgroup_actionBarItems"));
            }
        }
    }

    static /* synthetic */ void lambda$onLeaveClick$52(CheckBoxCell[] cells, View v) {
        Integer num = (Integer) v.getTag();
        cells[num.intValue()].setChecked(!cells[num.intValue()].isChecked(), true);
    }

    /* access modifiers changed from: private */
    public void processSelectedOption(TLRPC.TL_groupCallParticipant participant, long peerId, int option) {
        TLObject object;
        boolean z;
        TLObject object2;
        String name;
        TextView button;
        int i;
        TLRPC.TL_groupCallParticipant tL_groupCallParticipant = participant;
        long j = peerId;
        int i2 = option;
        VoIPService voIPService = VoIPService.getSharedInstance();
        if (voIPService != null) {
            if (j > 0) {
                object = this.accountInstance.getMessagesController().getUser(Long.valueOf(peerId));
            } else {
                object = this.accountInstance.getMessagesController().getChat(Long.valueOf(-j));
            }
            boolean z2 = false;
            if (i2 == 0 || i2 == 2) {
                object2 = object;
                z = true;
            } else if (i2 == 3) {
                object2 = object;
                z = true;
            } else if (i2 == 6) {
                this.parentActivity.switchToAccount(this.currentAccount, true);
                Bundle args = new Bundle();
                if (j > 0) {
                    args.putLong("user_id", j);
                } else {
                    args.putLong("chat_id", -j);
                }
                this.parentActivity.m3690lambda$runLinkRequest$59$orgtelegramuiLaunchActivity(new ChatActivity(args));
                dismiss();
                TLObject tLObject = object;
                return;
            } else if (i2 == 8) {
                this.parentActivity.switchToAccount(this.currentAccount, true);
                BaseFragment fragment = this.parentActivity.getActionBarLayout().fragmentsStack.get(this.parentActivity.getActionBarLayout().fragmentsStack.size() - 1);
                if (!(fragment instanceof ChatActivity) || ((ChatActivity) fragment).getDialogId() != j) {
                    Bundle args2 = new Bundle();
                    if (j > 0) {
                        args2.putLong("user_id", j);
                    } else {
                        args2.putLong("chat_id", -j);
                    }
                    this.parentActivity.m3690lambda$runLinkRequest$59$orgtelegramuiLaunchActivity(new ChatActivity(args2));
                    dismiss();
                    TLObject tLObject2 = object;
                    return;
                }
                dismiss();
                return;
            } else if (i2 == 7) {
                voIPService.editCallMember(object, true, (Boolean) null, (Integer) null, false, (Runnable) null);
                updateMuteButton(2, true);
                TLObject tLObject3 = object;
                return;
            } else if (i2 == 9) {
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
                    AvatarUpdaterDelegate avatarUpdaterDelegate2 = new AvatarUpdaterDelegate(j);
                    this.avatarUpdaterDelegate = avatarUpdaterDelegate2;
                    imageUpdater3.setDelegate(avatarUpdaterDelegate2);
                    TLRPC.User user = this.accountInstance.getUserConfig().getCurrentUser();
                    ImageUpdater imageUpdater4 = this.currentAvatarUpdater;
                    if (!(user.photo == null || user.photo.photo_big == null || (user.photo instanceof TLRPC.TL_userProfilePhotoEmpty))) {
                        z2 = true;
                    }
                    imageUpdater4.openMenu(z2, new GroupCallActivity$$ExternalSyntheticLambda28(this), GroupCallActivity$$ExternalSyntheticLambda4.INSTANCE);
                    TLObject tLObject4 = object;
                    return;
                }
                return;
            } else if (i2 == 10) {
                AlertsCreator.createChangeBioAlert(tL_groupCallParticipant.about, j, getContext(), this.currentAccount);
                TLObject tLObject5 = object;
                return;
            } else if (i2 == 11) {
                AlertsCreator.createChangeNameAlert(j, getContext(), this.currentAccount);
                TLObject tLObject6 = object;
                return;
            } else if (i2 == 5) {
                voIPService.editCallMember(object, true, (Boolean) null, (Integer) null, (Boolean) null, (Runnable) null);
                getUndoView().showWithAction(0, 35, (Object) object);
                voIPService.setParticipantVolume(tL_groupCallParticipant, 0);
                TLObject tLObject7 = object;
                return;
            } else {
                if ((tL_groupCallParticipant.flags & 128) == 0 || tL_groupCallParticipant.volume != 0) {
                    i = 1;
                    voIPService.editCallMember(object, false, (Boolean) null, (Integer) null, (Boolean) null, (Runnable) null);
                } else {
                    tL_groupCallParticipant.volume = 10000;
                    tL_groupCallParticipant.volume_by_admin = false;
                    i = 1;
                    voIPService.editCallMember(object, false, (Boolean) null, Integer.valueOf(tL_groupCallParticipant.volume), (Boolean) null, (Runnable) null);
                }
                voIPService.setParticipantVolume(tL_groupCallParticipant, ChatObject.getParticipantVolume(participant));
                getUndoView().showWithAction(0, i2 == i ? 31 : 36, (Object) object, (Object) null, (Runnable) null, (Runnable) null);
                return;
            }
            if (i2 != 0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setDialogButtonColorKey("voipgroup_listeningText");
                TextView messageTextView = new TextView(getContext());
                messageTextView.setTextColor(Theme.getColor("voipgroup_actionBarItems"));
                messageTextView.setTextSize(z ? 1 : 0, 16.0f);
                messageTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
                FrameLayout frameLayout = new FrameLayout(getContext());
                builder.setView(frameLayout);
                AvatarDrawable avatarDrawable = new AvatarDrawable();
                avatarDrawable.setTextSize(AndroidUtilities.dp(12.0f));
                BackupImageView imageView = new BackupImageView(getContext());
                imageView.setRoundRadius(AndroidUtilities.dp(20.0f));
                frameLayout.addView(imageView, LayoutHelper.createFrame(40, 40.0f, (LocaleController.isRTL ? 5 : 3) | 48, 22.0f, 5.0f, 22.0f, 0.0f));
                avatarDrawable.setInfo(object2);
                if (object2 instanceof TLRPC.User) {
                    TLRPC.User user2 = (TLRPC.User) object2;
                    imageView.setForUserOrChat(user2, avatarDrawable);
                    name = UserObject.getFirstName(user2);
                } else {
                    TLRPC.Chat chat = (TLRPC.Chat) object2;
                    imageView.setForUserOrChat(chat, avatarDrawable);
                    name = chat.title;
                }
                TextView textView = new TextView(getContext());
                textView.setTextColor(Theme.getColor("voipgroup_actionBarItems"));
                textView.setTextSize(1, 20.0f);
                textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                textView.setLines(1);
                textView.setMaxLines(1);
                textView.setSingleLine(true);
                textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
                textView.setEllipsize(TextUtils.TruncateAt.END);
                if (i2 == 2) {
                    textView.setText(LocaleController.getString("VoipGroupRemoveMemberAlertTitle2", NUM));
                    if (ChatObject.isChannelOrGiga(this.currentChat)) {
                        messageTextView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("VoipChannelRemoveMemberAlertText2", NUM, name, this.currentChat.title)));
                    } else {
                        messageTextView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("VoipGroupRemoveMemberAlertText2", NUM, name, this.currentChat.title)));
                    }
                } else {
                    textView.setText(LocaleController.getString("VoipGroupAddMemberTitle", NUM));
                    messageTextView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("VoipGroupAddMemberText", NUM, name, this.currentChat.title)));
                }
                int i3 = (LocaleController.isRTL ? 5 : 3) | 48;
                int i4 = 21;
                float f = (float) (LocaleController.isRTL ? 21 : 76);
                if (LocaleController.isRTL) {
                    i4 = 76;
                }
                frameLayout.addView(textView, LayoutHelper.createFrame(-1, -2.0f, i3, f, 11.0f, (float) i4, 0.0f));
                frameLayout.addView(messageTextView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, 24.0f, 57.0f, 24.0f, 9.0f));
                if (i2 == 2) {
                    builder.setPositiveButton(LocaleController.getString("VoipGroupUserRemove", NUM), new GroupCallActivity$$ExternalSyntheticLambda63(this, object2));
                } else if (object2 instanceof TLRPC.User) {
                    builder.setPositiveButton(LocaleController.getString("VoipGroupAdd", NUM), new GroupCallActivity$$ExternalSyntheticLambda64(this, (TLRPC.User) object2, j));
                }
                builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                AlertDialog dialog = builder.create();
                dialog.setBackgroundColor(Theme.getColor("voipgroup_dialogBackground"));
                dialog.show();
                if (i2 == 2 && (button = (TextView) dialog.getButton(-1)) != null) {
                    button.setTextColor(Theme.getColor("voipgroup_leaveCallMenu"));
                }
            } else if (VoIPService.getSharedInstance() != null) {
                VoIPService.getSharedInstance().editCallMember(object2, Boolean.valueOf(z), (Boolean) null, (Integer) null, (Boolean) null, (Runnable) null);
                getUndoView().showWithAction(0, 30, (Object) object2, (Object) null, (Runnable) null, (Runnable) null);
            }
        }
    }

    /* renamed from: lambda$processSelectedOption$54$org-telegram-ui-GroupCallActivity  reason: not valid java name */
    public /* synthetic */ void m3537x38663d33(TLObject object, DialogInterface dialogInterface, int i) {
        TLObject tLObject = object;
        if (tLObject instanceof TLRPC.User) {
            TLRPC.User user = (TLRPC.User) tLObject;
            this.accountInstance.getMessagesController().deleteParticipantFromChat(this.currentChat.id, user, (TLRPC.ChatFull) null);
            getUndoView().showWithAction(0, 32, (Object) user, (Object) null, (Runnable) null, (Runnable) null);
            return;
        }
        TLRPC.Chat chat = (TLRPC.Chat) tLObject;
        this.accountInstance.getMessagesController().deleteParticipantFromChat(this.currentChat.id, (TLRPC.User) null, chat, (TLRPC.ChatFull) null, false, false);
        getUndoView().showWithAction(0, 32, (Object) chat, (Object) null, (Runnable) null, (Runnable) null);
    }

    /* renamed from: lambda$processSelectedOption$56$org-telegram-ui-GroupCallActivity  reason: not valid java name */
    public /* synthetic */ void m3539x617bcb5(TLRPC.User user, long peerId, DialogInterface dialogInterface, int i) {
        this.accountInstance.getMessagesController().addUserToChat(this.currentChat.id, user, 0, (String) null, this.parentActivity.getActionBarLayout().fragmentsStack.get(this.parentActivity.getActionBarLayout().fragmentsStack.size() - 1), new GroupCallActivity$$ExternalSyntheticLambda31(this, peerId));
    }

    /* renamed from: lambda$processSelectedOption$55$org-telegram-ui-GroupCallActivity  reason: not valid java name */
    public /* synthetic */ void m3538x9f3efcf4(long peerId) {
        inviteUserToCall(peerId, false);
    }

    /* renamed from: lambda$processSelectedOption$57$org-telegram-ui-GroupCallActivity  reason: not valid java name */
    public /* synthetic */ void m3540x6cvar_CLASSNAME() {
        this.accountInstance.getMessagesController().deleteUserPhoto((TLRPC.InputPhoto) null);
    }

    static /* synthetic */ void lambda$processSelectedOption$58(DialogInterface dialog) {
    }

    /* access modifiers changed from: private */
    public boolean showMenuForCell(View rendererCell) {
        boolean z;
        GroupCallUserCell view;
        VolumeSlider volumeSlider;
        LinearLayout linearLayout;
        LinearLayout buttonsLayout;
        ScrollView scrollView;
        TLRPC.TL_groupCallParticipant participant;
        boolean showWithAvatarPreview;
        ArrayList<Integer> options;
        ActionBarPopupWindow.ActionBarPopupWindowLayout popupLayout;
        VolumeSlider volumeSlider2;
        ScrollView scrollView2;
        boolean isAdmin;
        ScrollView scrollView3;
        ArrayList<Integer> options2;
        TLRPC.TL_groupCallParticipant participant2;
        ImageLocation imageLocation;
        ImageLocation thumbLocation;
        int popupY;
        int popupX;
        AvatarUpdaterDelegate avatarUpdaterDelegate2;
        String str;
        int i;
        String str2;
        int i2;
        String str3;
        int i3;
        String str4;
        int i4;
        View view2 = rendererCell;
        if (this.itemAnimator.isRunning()) {
            return false;
        }
        if (this.avatarPriviewTransitionInProgress) {
            z = true;
        } else if (this.avatarsPreviewShowed) {
            z = true;
        } else {
            ActionBarPopupWindow actionBarPopupWindow = this.scrimPopupWindow;
            LinearLayout linearLayout2 = null;
            if (actionBarPopupWindow != null) {
                actionBarPopupWindow.dismiss();
                this.scrimPopupWindow = null;
                return false;
            }
            clearScrimView();
            if (view2 instanceof GroupCallGridCell) {
                GroupCallGridCell groupCallGridCell = (GroupCallGridCell) view2;
                if (groupCallGridCell.getParticipant() == this.call.videoNotAvailableParticipant) {
                    return false;
                }
                GroupCallUserCell view3 = new GroupCallUserCell(groupCallGridCell.getContext());
                view3.setData(this.accountInstance, groupCallGridCell.getParticipant().participant, this.call, MessageObject.getPeerId(this.selfPeer), (TLRPC.FileLocation) null, false);
                this.hasScrimAnchorView = false;
                this.scrimGridView = groupCallGridCell;
                this.scrimRenderer = groupCallGridCell.getRenderer();
                if (isTabletMode || isLandscapeMode) {
                    this.scrimViewAttached = false;
                } else {
                    this.scrimViewAttached = true;
                    this.containerView.addView(view3, LayoutHelper.createFrame(-1, -2.0f, 0, 14.0f, 0.0f, 14.0f, 0.0f));
                }
                view = view3;
            } else if (view2 instanceof GroupCallFullscreenAdapter.GroupCallUserCell) {
                GroupCallFullscreenAdapter.GroupCallUserCell groupCallFullscreenCell = (GroupCallFullscreenAdapter.GroupCallUserCell) view2;
                if (groupCallFullscreenCell.getParticipant() == this.call.videoNotAvailableParticipant.participant) {
                    return false;
                }
                GroupCallUserCell view4 = new GroupCallUserCell(groupCallFullscreenCell.getContext());
                view4.setData(this.accountInstance, groupCallFullscreenCell.getParticipant(), this.call, MessageObject.getPeerId(this.selfPeer), (TLRPC.FileLocation) null, false);
                this.hasScrimAnchorView = false;
                this.scrimFullscreenView = groupCallFullscreenCell;
                GroupCallMiniTextureView renderer = groupCallFullscreenCell.getRenderer();
                this.scrimRenderer = renderer;
                if (renderer != null && renderer.showingInFullscreen) {
                    this.scrimRenderer = null;
                }
                this.containerView.addView(view4, LayoutHelper.createFrame(-1, -2.0f, 0, 14.0f, 0.0f, 14.0f, 0.0f));
                this.scrimViewAttached = true;
                view = view4;
            } else {
                this.hasScrimAnchorView = true;
                this.scrimViewAttached = true;
                view = (GroupCallUserCell) view2;
            }
            if (view == null) {
                return false;
            }
            boolean showWithAvatarPreview2 = !isLandscapeMode && !isTabletMode && !AndroidUtilities.isInMultiwindow;
            TLRPC.TL_groupCallParticipant participant3 = view.getParticipant();
            final Rect rect2 = new Rect();
            ActionBarPopupWindow.ActionBarPopupWindowLayout popupLayout2 = new ActionBarPopupWindow.ActionBarPopupWindowLayout(getContext());
            popupLayout2.setBackgroundDrawable((Drawable) null);
            popupLayout2.setPadding(0, 0, 0, 0);
            popupLayout2.setOnTouchListener(new View.OnTouchListener() {
                private int[] pos = new int[2];

                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getActionMasked() == 0) {
                        if (GroupCallActivity.this.scrimPopupWindow != null && GroupCallActivity.this.scrimPopupWindow.isShowing()) {
                            View contentView = GroupCallActivity.this.scrimPopupWindow.getContentView();
                            contentView.getLocationInWindow(this.pos);
                            Rect rect = rect2;
                            int[] iArr = this.pos;
                            rect.set(iArr[0], iArr[1], iArr[0] + contentView.getMeasuredWidth(), this.pos[1] + contentView.getMeasuredHeight());
                            if (!rect2.contains((int) event.getX(), (int) event.getY())) {
                                GroupCallActivity.this.scrimPopupWindow.dismiss();
                            }
                        }
                    } else if (event.getActionMasked() == 4 && GroupCallActivity.this.scrimPopupWindow != null && GroupCallActivity.this.scrimPopupWindow.isShowing()) {
                        GroupCallActivity.this.scrimPopupWindow.dismiss();
                    }
                    return false;
                }
            });
            popupLayout2.setDispatchKeyEventListener(new GroupCallActivity$$ExternalSyntheticLambda51(this));
            final LinearLayout buttonsLayout2 = new LinearLayout(getContext());
            if (!participant3.muted_by_you) {
                linearLayout2 = new LinearLayout(getContext());
            }
            final LinearLayout volumeLayout = linearLayout2;
            this.currentOptionsLayout = buttonsLayout2;
            LinearLayout linearLayout3 = new LinearLayout(getContext()) {
                /* access modifiers changed from: protected */
                public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                    buttonsLayout2.measure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(0, 0));
                    LinearLayout linearLayout = volumeLayout;
                    if (linearLayout != null) {
                        linearLayout.measure(View.MeasureSpec.makeMeasureSpec(buttonsLayout2.getMeasuredWidth(), NUM), View.MeasureSpec.makeMeasureSpec(0, 0));
                        setMeasuredDimension(buttonsLayout2.getMeasuredWidth(), buttonsLayout2.getMeasuredHeight() + volumeLayout.getMeasuredHeight());
                        return;
                    }
                    setMeasuredDimension(buttonsLayout2.getMeasuredWidth(), buttonsLayout2.getMeasuredHeight());
                }
            };
            linearLayout3.setMinimumWidth(AndroidUtilities.dp(240.0f));
            linearLayout3.setOrientation(1);
            int color = AndroidUtilities.getOffsetColor(Theme.getColor("voipgroup_listViewBackgroundUnscrolled"), Theme.getColor("voipgroup_listViewBackground"), this.colorProgress, 1.0f);
            if (volumeLayout == null || view.isSelfUser() || participant3.muted_by_you || (participant3.muted && !participant3.can_self_unmute)) {
                volumeSlider = null;
            } else {
                Drawable shadowDrawable2 = getContext().getResources().getDrawable(NUM).mutate();
                shadowDrawable2.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
                volumeLayout.setBackgroundDrawable(shadowDrawable2);
                linearLayout3.addView(volumeLayout, LayoutHelper.createLinear(-2, -2, 0.0f, 0.0f, 0.0f, 0.0f));
                VolumeSlider volumeSlider3 = new VolumeSlider(this, getContext(), participant3);
                volumeLayout.addView(volumeSlider3, -1, 48);
                volumeSlider = volumeSlider3;
            }
            buttonsLayout2.setMinimumWidth(AndroidUtilities.dp(240.0f));
            buttonsLayout2.setOrientation(1);
            Drawable shadowDrawable3 = getContext().getResources().getDrawable(NUM).mutate();
            shadowDrawable3.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
            buttonsLayout2.setBackgroundDrawable(shadowDrawable3);
            linearLayout3.addView(buttonsLayout2, LayoutHelper.createLinear(-2, -2, 0.0f, volumeSlider != null ? -8.0f : 0.0f, 0.0f, 0.0f));
            if (Build.VERSION.SDK_INT >= 21) {
                Drawable drawable = shadowDrawable3;
                linearLayout = linearLayout3;
                LinearLayout linearLayout4 = volumeLayout;
                buttonsLayout = buttonsLayout2;
                final LinearLayout buttonsLayout3 = linearLayout;
                scrollView = new ScrollView(getContext(), (AttributeSet) null, 0, NUM) {
                    /* access modifiers changed from: protected */
                    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                        setMeasuredDimension(buttonsLayout3.getMeasuredWidth(), getMeasuredHeight());
                    }
                };
            } else {
                linearLayout = linearLayout3;
                LinearLayout linearLayout5 = volumeLayout;
                buttonsLayout = buttonsLayout2;
                scrollView = new ScrollView(getContext());
            }
            scrollView.setClipToPadding(false);
            popupLayout2.addView(scrollView, LayoutHelper.createFrame(-2, -2.0f));
            long peerId = MessageObject.getPeerId(participant3.peer);
            ArrayList<String> items = new ArrayList<>(2);
            ArrayList<Integer> icons = new ArrayList<>(2);
            ArrayList<Integer> options3 = new ArrayList<>(2);
            boolean isAdmin2 = false;
            if (!(participant3.peer instanceof TLRPC.TL_peerUser)) {
                options = options3;
                int i5 = color;
                volumeSlider2 = volumeSlider;
                showWithAvatarPreview = showWithAvatarPreview2;
                participant = participant3;
                Rect rect3 = rect2;
                popupLayout = popupLayout2;
                scrollView2 = scrollView;
                isAdmin = peerId == (-this.currentChat.id);
            } else if (ChatObject.isChannel(this.currentChat)) {
                int i6 = color;
                volumeSlider2 = volumeSlider;
                Rect rect4 = rect2;
                popupLayout = popupLayout2;
                TLRPC.ChannelParticipant p = this.accountInstance.getMessagesController().getAdminInChannel(participant3.peer.user_id, this.currentChat.id);
                isAdmin = p != null && ((p instanceof TLRPC.TL_channelParticipantCreator) || p.admin_rights.manage_call);
                scrollView2 = scrollView;
                options = options3;
                showWithAvatarPreview = showWithAvatarPreview2;
                participant = participant3;
            } else {
                volumeSlider2 = volumeSlider;
                Rect rect5 = rect2;
                popupLayout = popupLayout2;
                TLRPC.ChatFull chatFull = this.accountInstance.getMessagesController().getChatFull(this.currentChat.id);
                if (chatFull != null && chatFull.participants != null) {
                    int a = 0;
                    int N = chatFull.participants.participants.size();
                    while (true) {
                        if (a >= N) {
                            scrollView2 = scrollView;
                            options = options3;
                            TLRPC.ChatFull chatFull2 = chatFull;
                            showWithAvatarPreview = showWithAvatarPreview2;
                            participant = participant3;
                            break;
                        }
                        TLRPC.ChatParticipant chatParticipant = chatFull.participants.participants.get(a);
                        scrollView2 = scrollView;
                        options = options3;
                        TLRPC.ChatFull chatFull3 = chatFull;
                        showWithAvatarPreview = showWithAvatarPreview2;
                        participant = participant3;
                        if (chatParticipant.user_id == participant3.peer.user_id) {
                            isAdmin2 = (chatParticipant instanceof TLRPC.TL_chatParticipantAdmin) || (chatParticipant instanceof TLRPC.TL_chatParticipantCreator);
                        } else {
                            a++;
                            scrollView = scrollView2;
                            options3 = options;
                            chatFull = chatFull3;
                            showWithAvatarPreview2 = showWithAvatarPreview;
                            participant3 = participant;
                        }
                    }
                } else {
                    scrollView2 = scrollView;
                    options = options3;
                    TLRPC.ChatFull chatFull4 = chatFull;
                    showWithAvatarPreview = showWithAvatarPreview2;
                    participant = participant3;
                }
                isAdmin = isAdmin2;
            }
            if (view.isSelfUser()) {
                if (view.isHandRaised()) {
                    items.add(LocaleController.getString("VoipGroupCancelRaiseHand", NUM));
                    icons.add(NUM);
                    options2 = options;
                    options2.add(7);
                } else {
                    options2 = options;
                }
                if (view.hasAvatarSet()) {
                    i = NUM;
                    str = "VoipAddPhoto";
                } else {
                    i = NUM;
                    str = "VoipSetNewPhoto";
                }
                items.add(LocaleController.getString(str, i));
                icons.add(NUM);
                options2.add(9);
                if (peerId > 0) {
                    participant2 = participant;
                    if (TextUtils.isEmpty(participant2.about)) {
                        i4 = NUM;
                        str4 = "VoipAddBio";
                    } else {
                        i4 = NUM;
                        str4 = "VoipEditBio";
                    }
                    items.add(LocaleController.getString(str4, i4));
                } else {
                    participant2 = participant;
                    if (TextUtils.isEmpty(participant2.about)) {
                        i3 = NUM;
                        str3 = "VoipAddDescription";
                    } else {
                        i3 = NUM;
                        str3 = "VoipEditDescription";
                    }
                    items.add(LocaleController.getString(str3, i3));
                }
                icons.add(Integer.valueOf(TextUtils.isEmpty(participant2.about) ? NUM : NUM));
                options2.add(10);
                if (peerId > 0) {
                    i2 = NUM;
                    str2 = "VoipEditName";
                } else {
                    i2 = NUM;
                    str2 = "VoipEditTitle";
                }
                items.add(LocaleController.getString(str2, i2));
                icons.add(NUM);
                options2.add(11);
                scrollView3 = scrollView2;
            } else {
                options2 = options;
                participant2 = participant;
                if (ChatObject.canManageCalls(this.currentChat)) {
                    if (!isAdmin || !participant2.muted) {
                        if (!participant2.muted) {
                            scrollView3 = scrollView2;
                        } else if (participant2.can_self_unmute) {
                            scrollView3 = scrollView2;
                        } else {
                            items.add(LocaleController.getString("VoipGroupAllowToSpeak", NUM));
                            scrollView3 = scrollView2;
                            if (participant2.raise_hand_rating != 0) {
                                icons.add(NUM);
                            } else {
                                icons.add(NUM);
                            }
                            options2.add(1);
                        }
                        items.add(LocaleController.getString("VoipGroupMute", NUM));
                        icons.add(NUM);
                        options2.add(0);
                    } else {
                        scrollView3 = scrollView2;
                    }
                    if (participant2.peer.channel_id == 0 || ChatObject.isMegagroup(this.currentAccount, participant2.peer.channel_id)) {
                        items.add(LocaleController.getString("VoipGroupOpenProfile", NUM));
                        icons.add(NUM);
                        options2.add(6);
                    } else {
                        items.add(LocaleController.getString("VoipGroupOpenChannel", NUM));
                        icons.add(NUM);
                        options2.add(8);
                    }
                    if (!isAdmin && ChatObject.canBlockUsers(this.currentChat)) {
                        items.add(LocaleController.getString("VoipGroupUserRemove", NUM));
                        icons.add(NUM);
                        options2.add(2);
                    }
                } else {
                    scrollView3 = scrollView2;
                    if (participant2.muted_by_you) {
                        items.add(LocaleController.getString("VoipGroupUnmuteForMe", NUM));
                        icons.add(NUM);
                        options2.add(4);
                    } else {
                        items.add(LocaleController.getString("VoipGroupMuteForMe", NUM));
                        icons.add(NUM);
                        options2.add(5);
                    }
                    if (participant2.peer.channel_id == 0 || ChatObject.isMegagroup(this.currentAccount, participant2.peer.channel_id)) {
                        items.add(LocaleController.getString("VoipGroupOpenChat", NUM));
                        icons.add(NUM);
                        options2.add(6);
                    } else {
                        items.add(LocaleController.getString("VoipGroupOpenChannel", NUM));
                        icons.add(NUM);
                        options2.add(8);
                    }
                }
            }
            int a2 = 0;
            int N2 = items.size();
            while (a2 < N2) {
                boolean isAdmin3 = isAdmin;
                ActionBarMenuSubItem cell = new ActionBarMenuSubItem(getContext(), a2 == 0, a2 == N2 + -1);
                if (options2.get(a2).intValue() != 2) {
                    cell.setColors(Theme.getColor("voipgroup_actionBarItems"), Theme.getColor("voipgroup_actionBarItems"));
                } else {
                    cell.setColors(Theme.getColor("voipgroup_leaveCallMenu"), Theme.getColor("voipgroup_leaveCallMenu"));
                }
                cell.setSelectorColor(Theme.getColor("voipgroup_listSelector"));
                cell.setTextAndIcon(items.get(a2), icons.get(a2).intValue());
                buttonsLayout.addView(cell);
                cell.setTag(options2.get(a2));
                cell.setOnClickListener(new GroupCallActivity$$ExternalSyntheticLambda13(this, a2, options2, participant2));
                a2++;
                isAdmin = isAdmin3;
            }
            scrollView3.addView(linearLayout, LayoutHelper.createScroll(-2, -2, 51));
            this.listView.stopScroll();
            this.layoutManager.setCanScrollVertically(false);
            this.scrimView = view;
            view.setAboutVisible(true);
            this.containerView.invalidate();
            this.listView.invalidate();
            AnimatorSet animatorSet = this.scrimAnimatorSet;
            if (animatorSet != null) {
                animatorSet.cancel();
            }
            ActionBarPopupWindow.ActionBarPopupWindowLayout popupLayout3 = popupLayout;
            this.scrimPopupLayout = popupLayout3;
            if (peerId > 0) {
                TLRPC.User currentUser = this.accountInstance.getMessagesController().getUser(Long.valueOf(peerId));
                imageLocation = ImageLocation.getForUserOrChat(currentUser, 0);
                thumbLocation = ImageLocation.getForUserOrChat(currentUser, 1);
            } else {
                TLRPC.Chat currentChat2 = this.accountInstance.getMessagesController().getChat(Long.valueOf(-peerId));
                imageLocation = ImageLocation.getForUserOrChat(currentChat2, 0);
                thumbLocation = ImageLocation.getForUserOrChat(currentChat2, 1);
            }
            GroupCallMiniTextureView groupCallMiniTextureView = this.scrimRenderer;
            boolean hasAttachedRenderer = groupCallMiniTextureView != null && groupCallMiniTextureView.isAttached();
            if (imageLocation == null && !hasAttachedRenderer) {
                TLRPC.TL_groupCallParticipant tL_groupCallParticipant = participant2;
                ArrayList<Integer> arrayList = options2;
                showWithAvatarPreview = false;
            } else if (showWithAvatarPreview) {
                TLRPC.TL_groupCallParticipant tL_groupCallParticipant2 = participant2;
                this.avatarsViewPager.setParentAvatarImage(this.scrimView.getAvatarImageView());
                this.avatarsViewPager.setHasActiveVideo(hasAttachedRenderer);
                this.avatarsViewPager.setData(peerId, true);
                this.avatarsViewPager.setCreateThumbFromParent(true);
                this.avatarsViewPager.initIfEmpty(imageLocation, thumbLocation, true);
                GroupCallMiniTextureView groupCallMiniTextureView2 = this.scrimRenderer;
                if (groupCallMiniTextureView2 != null) {
                    groupCallMiniTextureView2.setShowingAsScrimView(true, true);
                }
                if (MessageObject.getPeerId(this.selfPeer) != peerId || this.currentAvatarUpdater == null || (avatarUpdaterDelegate2 = this.avatarUpdaterDelegate) == null || avatarUpdaterDelegate2.avatar == null) {
                } else {
                    ArrayList<Integer> arrayList2 = options2;
                    this.avatarsViewPager.addUploadingImage(this.avatarUpdaterDelegate.uploadingImageLocation, ImageLocation.getForLocal(this.avatarUpdaterDelegate.avatar));
                }
            } else {
                ArrayList<Integer> arrayList3 = options2;
            }
            if (showWithAvatarPreview) {
                this.avatarsPreviewShowed = true;
                popupLayout3.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE));
                ArrayList<Integer> arrayList4 = icons;
                this.containerView.addView(this.scrimPopupLayout, LayoutHelper.createFrame(-2, -2.0f));
                this.useBlur = true;
                prepareBlurBitmap();
                this.avatarPriviewTransitionInProgress = true;
                this.avatarPreviewContainer.setVisibility(0);
                if (volumeSlider2 != null) {
                    volumeSlider2.invalidate();
                }
                runAvatarPreviewTransition(true, view);
                GroupCallFullscreenAdapter.GroupCallUserCell groupCallUserCell = this.scrimFullscreenView;
                if (groupCallUserCell != null) {
                    groupCallUserCell.getAvatarImageView().setAlpha(0.0f);
                    long j = peerId;
                    return true;
                }
                long j2 = peerId;
                return true;
            }
            this.avatarsPreviewShowed = false;
            AnonymousClass56 r2 = new ActionBarPopupWindow(popupLayout3, -2, -2) {
                public void dismiss() {
                    super.dismiss();
                    if (GroupCallActivity.this.scrimPopupWindow == this) {
                        ActionBarPopupWindow unused = GroupCallActivity.this.scrimPopupWindow = null;
                        if (GroupCallActivity.this.scrimAnimatorSet != null) {
                            GroupCallActivity.this.scrimAnimatorSet.cancel();
                            AnimatorSet unused2 = GroupCallActivity.this.scrimAnimatorSet = null;
                        }
                        GroupCallActivity.this.layoutManager.setCanScrollVertically(true);
                        AnimatorSet unused3 = GroupCallActivity.this.scrimAnimatorSet = new AnimatorSet();
                        ArrayList<Animator> animators = new ArrayList<>();
                        animators.add(ObjectAnimator.ofInt(GroupCallActivity.this.scrimPaint, AnimationProperties.PAINT_ALPHA, new int[]{0}));
                        GroupCallActivity.this.scrimAnimatorSet.playTogether(animators);
                        GroupCallActivity.this.scrimAnimatorSet.setDuration(220);
                        GroupCallActivity.this.scrimAnimatorSet.addListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animation) {
                                GroupCallActivity.this.clearScrimView();
                                GroupCallActivity.this.containerView.invalidate();
                                GroupCallActivity.this.listView.invalidate();
                                if (GroupCallActivity.this.delayedGroupCallUpdated) {
                                    boolean unused = GroupCallActivity.this.delayedGroupCallUpdated = false;
                                    GroupCallActivity.this.applyCallParticipantUpdates(true);
                                }
                            }
                        });
                        GroupCallActivity.this.scrimAnimatorSet.start();
                    }
                }
            };
            this.scrimPopupWindow = r2;
            r2.setPauseNotifications(true);
            this.scrimPopupWindow.setDismissAnimationDuration(220);
            this.scrimPopupWindow.setOutsideTouchable(true);
            this.scrimPopupWindow.setClippingEnabled(true);
            this.scrimPopupWindow.setAnimationStyle(NUM);
            this.scrimPopupWindow.setFocusable(true);
            popupLayout3.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE));
            this.scrimPopupWindow.setInputMethodMode(2);
            this.scrimPopupWindow.setSoftInputMode(0);
            this.scrimPopupWindow.getContentView().setFocusableInTouchMode(true);
            GroupCallFullscreenAdapter.GroupCallUserCell groupCallUserCell2 = this.scrimFullscreenView;
            if (groupCallUserCell2 == null) {
                popupX = (int) (((this.listView.getX() + ((float) this.listView.getMeasuredWidth())) + ((float) AndroidUtilities.dp(8.0f))) - ((float) popupLayout3.getMeasuredWidth()));
                if (this.hasScrimAnchorView) {
                    popupY = (int) (this.listView.getY() + view.getY() + ((float) view.getClipHeight()));
                } else if (this.scrimGridView != null) {
                    popupY = (int) (this.listView.getY() + this.scrimGridView.getY() + ((float) this.scrimGridView.getMeasuredHeight()));
                } else {
                    popupY = (int) this.listView.getY();
                }
            } else if (isLandscapeMode) {
                popupX = (((int) ((groupCallUserCell2.getX() + this.fullscreenUsersListView.getX()) + this.renderersContainer.getX())) - popupLayout3.getMeasuredWidth()) + AndroidUtilities.dp(32.0f);
                popupY = ((int) ((this.scrimFullscreenView.getY() + this.fullscreenUsersListView.getY()) + this.renderersContainer.getY())) - AndroidUtilities.dp(6.0f);
            } else {
                popupX = ((int) ((groupCallUserCell2.getX() + this.fullscreenUsersListView.getX()) + this.renderersContainer.getX())) - AndroidUtilities.dp(14.0f);
                popupY = (int) (((this.scrimFullscreenView.getY() + this.fullscreenUsersListView.getY()) + this.renderersContainer.getY()) - ((float) popupLayout3.getMeasuredHeight()));
            }
            long j3 = peerId;
            this.scrimPopupWindow.showAtLocation(this.listView, 51, popupX, popupY);
            this.scrimAnimatorSet = new AnimatorSet();
            ArrayList<Animator> animators = new ArrayList<>();
            animators.add(ObjectAnimator.ofInt(this.scrimPaint, AnimationProperties.PAINT_ALPHA, new int[]{0, 100}));
            this.scrimAnimatorSet.playTogether(animators);
            int i7 = popupX;
            int i8 = popupY;
            this.scrimAnimatorSet.setDuration(150);
            this.scrimAnimatorSet.start();
            return true;
        }
        dismissAvatarPreview(z);
        return false;
    }

    /* renamed from: lambda$showMenuForCell$59$org-telegram-ui-GroupCallActivity  reason: not valid java name */
    public /* synthetic */ void m3542lambda$showMenuForCell$59$orgtelegramuiGroupCallActivity(KeyEvent keyEvent) {
        ActionBarPopupWindow actionBarPopupWindow;
        if (keyEvent.getKeyCode() == 4 && keyEvent.getRepeatCount() == 0 && (actionBarPopupWindow = this.scrimPopupWindow) != null && actionBarPopupWindow.isShowing()) {
            this.scrimPopupWindow.dismiss();
        }
    }

    /* renamed from: lambda$showMenuForCell$60$org-telegram-ui-GroupCallActivity  reason: not valid java name */
    public /* synthetic */ void m3543lambda$showMenuForCell$60$orgtelegramuiGroupCallActivity(int i, ArrayList options, TLRPC.TL_groupCallParticipant participant, View v1) {
        if (i < options.size()) {
            TLRPC.TL_groupCallParticipant participant1 = this.call.participants.get(MessageObject.getPeerId(participant.peer));
            if (participant1 == null) {
                participant1 = participant;
            }
            processSelectedOption(participant1, MessageObject.getPeerId(participant1.peer), ((Integer) options.get(i)).intValue());
            ActionBarPopupWindow actionBarPopupWindow = this.scrimPopupWindow;
            if (actionBarPopupWindow != null) {
                actionBarPopupWindow.dismiss();
            } else if (((Integer) options.get(i)).intValue() != 9 && ((Integer) options.get(i)).intValue() != 10 && ((Integer) options.get(i)).intValue() != 11) {
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
        if (!(groupCallUserCell == null || this.hasScrimAnchorView || groupCallUserCell.getParent() == null)) {
            this.containerView.removeView(this.scrimView);
        }
        GroupCallUserCell groupCallUserCell2 = this.scrimView;
        if (groupCallUserCell2 != null) {
            groupCallUserCell2.setProgressToAvatarPreview(0.0f);
            this.scrimView.setAboutVisible(false);
            this.scrimView.getAvatarImageView().setAlpha(1.0f);
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
        if (this.parentActivity != null && Build.VERSION.SDK_INT >= 21) {
            this.parentActivity.startActivityForResult(((MediaProjectionManager) this.parentActivity.getSystemService("media_projection")).createScreenCaptureIntent(), 520);
        }
    }

    private void runAvatarPreviewTransition(boolean enter, GroupCallUserCell view) {
        float fromScale;
        float fromY;
        float fromX;
        int fromRadius;
        GroupCallMiniTextureView groupCallMiniTextureView;
        int fromRadius2;
        float fromScale2;
        float fromY2;
        float fromX2;
        GroupCallMiniTextureView groupCallMiniTextureView2;
        boolean z;
        final boolean z2 = enter;
        float left = (float) (AndroidUtilities.dp(14.0f) + this.containerView.getPaddingLeft());
        float top = (float) (AndroidUtilities.dp(14.0f) + this.containerView.getPaddingTop());
        if (this.hasScrimAnchorView) {
            float fromScale3 = ((float) view.getAvatarImageView().getMeasuredHeight()) / ((float) this.listView.getMeasuredWidth());
            fromX = ((view.getAvatarImageView().getX() + view.getX()) + this.listView.getX()) - left;
            fromY = ((view.getAvatarImageView().getY() + view.getY()) + this.listView.getY()) - top;
            fromScale = fromScale3;
            fromRadius = (int) (((float) (view.getAvatarImageView().getMeasuredHeight() >> 1)) / fromScale3);
        } else {
            if (this.scrimRenderer == null) {
                this.previewTextureTransitionEnabled = true;
            } else {
                if (!z2) {
                    ProfileGalleryView profileGalleryView = this.avatarsViewPager;
                    if (profileGalleryView.getRealPosition(profileGalleryView.getCurrentItem()) != 0) {
                        z = false;
                        this.previewTextureTransitionEnabled = z;
                    }
                }
                z = true;
                this.previewTextureTransitionEnabled = z;
            }
            GroupCallGridCell groupCallGridCell = this.scrimGridView;
            if (groupCallGridCell == null || !this.previewTextureTransitionEnabled) {
                GroupCallFullscreenAdapter.GroupCallUserCell groupCallUserCell = this.scrimFullscreenView;
                if (groupCallUserCell == null) {
                    fromX2 = 0.0f;
                    fromY2 = 0.0f;
                    fromScale2 = 0.96f;
                    fromRadius2 = 0;
                } else if (this.scrimRenderer == null) {
                    fromX2 = (((groupCallUserCell.getAvatarImageView().getX() + this.scrimFullscreenView.getX()) + this.fullscreenUsersListView.getX()) + this.renderersContainer.getX()) - left;
                    fromY2 = (((this.scrimFullscreenView.getAvatarImageView().getY() + this.scrimFullscreenView.getY()) + this.fullscreenUsersListView.getY()) + this.renderersContainer.getY()) - top;
                    fromScale2 = ((float) this.scrimFullscreenView.getAvatarImageView().getMeasuredHeight()) / ((float) this.listView.getMeasuredWidth());
                    fromRadius2 = (int) (((float) (this.scrimFullscreenView.getAvatarImageView().getMeasuredHeight() >> 1)) / fromScale2);
                } else if (this.previewTextureTransitionEnabled) {
                    fromX2 = ((groupCallUserCell.getX() + this.fullscreenUsersListView.getX()) + this.renderersContainer.getX()) - left;
                    fromY2 = ((this.scrimFullscreenView.getY() + this.fullscreenUsersListView.getY()) + this.renderersContainer.getY()) - top;
                    fromScale2 = 1.0f;
                    fromRadius2 = 0;
                } else {
                    fromX2 = 0.0f;
                    fromY2 = 0.0f;
                    fromScale2 = 0.96f;
                    fromRadius2 = 0;
                }
            } else {
                fromX2 = (groupCallGridCell.getX() + this.listView.getX()) - left;
                fromY2 = ((this.scrimGridView.getY() + this.listView.getY()) + ((float) AndroidUtilities.dp(2.0f))) - top;
                fromScale2 = 1.0f;
                fromRadius2 = 0;
            }
            if (!this.previewTextureTransitionEnabled && (groupCallMiniTextureView2 = this.scrimRenderer) != null) {
                groupCallMiniTextureView2.invalidate();
                this.renderersContainer.invalidate();
                this.scrimRenderer.setShowingAsScrimView(false, false);
                this.scrimRenderer = null;
            }
            fromX = fromX2;
            fromY = fromY2;
            fromScale = fromScale2;
            fromRadius = fromRadius2;
        }
        float fromX3 = 0.0f;
        if (z2) {
            this.avatarPreviewContainer.setScaleX(fromScale);
            this.avatarPreviewContainer.setScaleY(fromScale);
            this.avatarPreviewContainer.setTranslationX(fromX);
            this.avatarPreviewContainer.setTranslationY(fromY);
            this.avatarPagerIndicator.setAlpha(0.0f);
        }
        this.avatarsViewPager.setRoundRadius(fromRadius, fromRadius);
        if (this.useBlur) {
            if (z2) {
                this.blurredView.setAlpha(0.0f);
            }
            ViewPropertyAnimator animate = this.blurredView.animate();
            if (z2) {
                fromX3 = 1.0f;
            }
            animate.alpha(fromX3).setDuration(220).start();
        }
        this.avatarPagerIndicator.animate().alpha(z2 ? 1.0f : 0.0f).setDuration(220).start();
        if (!z2 && (groupCallMiniTextureView = this.scrimRenderer) != null) {
            groupCallMiniTextureView.setShowingAsScrimView(false, true);
            ProfileGalleryView profileGalleryView2 = this.avatarsViewPager;
            if (profileGalleryView2.getRealPosition(profileGalleryView2.getCurrentItem()) != 0) {
                this.scrimRenderer.textureView.cancelAnimation();
                this.scrimGridView = null;
            }
        }
        float[] fArr = new float[2];
        fArr[0] = z2 ? 0.0f : 1.0f;
        fArr[1] = z2 ? 1.0f : 0.0f;
        GroupCallActivity$$ExternalSyntheticLambda22 groupCallActivity$$ExternalSyntheticLambda22 = r0;
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(fArr);
        int i = fromRadius;
        GroupCallActivity$$ExternalSyntheticLambda22 groupCallActivity$$ExternalSyntheticLambda222 = new GroupCallActivity$$ExternalSyntheticLambda22(this, fromScale, fromX, fromY, fromRadius);
        valueAnimator.addUpdateListener(groupCallActivity$$ExternalSyntheticLambda222);
        this.popupAnimationIndex = this.accountInstance.getNotificationCenter().setAnimationInProgress(this.popupAnimationIndex, new int[]{NotificationCenter.dialogPhotosLoaded, NotificationCenter.fileLoaded, NotificationCenter.messagesDidLoad});
        final GroupCallMiniTextureView videoRenderer = this.scrimGridView == null ? null : this.scrimRenderer;
        if (videoRenderer != null) {
            videoRenderer.animateToScrimView = true;
        }
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                GroupCallMiniTextureView groupCallMiniTextureView = videoRenderer;
                if (groupCallMiniTextureView != null) {
                    groupCallMiniTextureView.animateToScrimView = false;
                }
                GroupCallActivity.this.accountInstance.getNotificationCenter().onAnimationFinish(GroupCallActivity.this.popupAnimationIndex);
                boolean unused = GroupCallActivity.this.avatarPriviewTransitionInProgress = false;
                float unused2 = GroupCallActivity.this.progressToAvatarPreview = z2 ? 1.0f : 0.0f;
                GroupCallActivity.this.renderersContainer.progressToScrimView = GroupCallActivity.this.progressToAvatarPreview;
                if (!z2) {
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
                        GroupCallActivity.this.applyCallParticipantUpdates(true);
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
            valueAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
            valueAnimator.setDuration(220);
            valueAnimator.start();
        } else {
            valueAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
            valueAnimator.setDuration(220);
            this.scrimRenderer.textureView.setAnimateNextDuration(220);
            this.scrimRenderer.textureView.synchOrRunAnimation(valueAnimator);
        }
        checkContentOverlayed();
    }

    /* renamed from: lambda$runAvatarPreviewTransition$61$org-telegram-ui-GroupCallActivity  reason: not valid java name */
    public /* synthetic */ void m3541x7ab4ea1b(float fromScale, float fromX, float fromY, int fromRadius, ValueAnimator valueAnimator1) {
        float floatValue = ((Float) valueAnimator1.getAnimatedValue()).floatValue();
        this.progressToAvatarPreview = floatValue;
        this.renderersContainer.progressToScrimView = floatValue;
        float f = this.progressToAvatarPreview;
        float s = ((1.0f - f) * fromScale) + (f * 1.0f);
        this.avatarPreviewContainer.setScaleX(s);
        this.avatarPreviewContainer.setScaleY(s);
        this.avatarPreviewContainer.setTranslationX((1.0f - this.progressToAvatarPreview) * fromX);
        this.avatarPreviewContainer.setTranslationY((1.0f - this.progressToAvatarPreview) * fromY);
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
        float f2 = this.progressToAvatarPreview;
        profileGalleryView.setRoundRadius((int) (((float) fromRadius) * (1.0f - f2)), (int) (((float) fromRadius) * (1.0f - f2)));
    }

    /* access modifiers changed from: private */
    public void dismissAvatarPreview(boolean animated) {
        if (!this.avatarPriviewTransitionInProgress && this.avatarsPreviewShowed) {
            if (animated) {
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
                applyCallParticipantUpdates(true);
            }
            checkContentOverlayed();
        }
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private static final int VIEW_TYPE_CALL_INVITED = 2;
        private static final int VIEW_TYPE_GRID = 4;
        private static final int VIEW_TYPE_INVITE_MEMBERS = 0;
        private static final int VIEW_TYPE_LAST_PADDING = 3;
        private static final int VIEW_TYPE_PARTICIPANT = 1;
        private static final int VIEW_TYPE_VIDEO_GRID_DIVIDER = 5;
        private static final int VIEW_TYPE_VIDEO_NOT_AVAILABLE = 6;
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
        /* access modifiers changed from: private */
        public int videoGridDividerRow;
        /* access modifiers changed from: private */
        public int videoNotAvailableRow;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean addSelfToCounter() {
            if (!GroupCallActivity.this.isRtmpStream() && !this.hasSelfUser && VoIPService.getSharedInstance() != null) {
                return !VoIPService.getSharedInstance().isJoined();
            }
            return false;
        }

        public int getItemCount() {
            return this.rowsCount;
        }

        /* access modifiers changed from: private */
        public void updateRows() {
            if (GroupCallActivity.this.call != null && !GroupCallActivity.this.call.isScheduled() && !GroupCallActivity.this.delayedGroupCallUpdated) {
                boolean z = false;
                this.rowsCount = 0;
                if (GroupCallActivity.this.call.participants.indexOfKey(MessageObject.getPeerId(GroupCallActivity.this.selfPeer)) >= 0) {
                    z = true;
                }
                this.hasSelfUser = z;
                int i = this.rowsCount;
                this.usersVideoGridStartRow = i;
                int size = i + GroupCallActivity.this.visibleVideoParticipants.size();
                this.rowsCount = size;
                this.usersVideoGridEndRow = size;
                if (GroupCallActivity.this.visibleVideoParticipants.size() > 0) {
                    int i2 = this.rowsCount;
                    this.rowsCount = i2 + 1;
                    this.videoGridDividerRow = i2;
                } else {
                    this.videoGridDividerRow = -1;
                }
                if (GroupCallActivity.this.visibleVideoParticipants.isEmpty() || !ChatObject.canManageCalls(GroupCallActivity.this.currentChat) || GroupCallActivity.this.call.call.participants_count <= GroupCallActivity.this.accountInstance.getMessagesController().groupCallVideoMaxParticipants) {
                    this.videoNotAvailableRow = -1;
                } else {
                    int i3 = this.rowsCount;
                    this.rowsCount = i3 + 1;
                    this.videoNotAvailableRow = i3;
                }
                this.usersStartRow = this.rowsCount;
                if (!GroupCallActivity.this.isRtmpStream()) {
                    this.rowsCount += GroupCallActivity.this.call.visibleParticipants.size();
                }
                this.usersEndRow = this.rowsCount;
                if (GroupCallActivity.this.call.invitedUsers.isEmpty() || GroupCallActivity.this.isRtmpStream()) {
                    this.invitedStartRow = -1;
                    this.invitedEndRow = -1;
                } else {
                    int i4 = this.rowsCount;
                    this.invitedStartRow = i4;
                    int size2 = i4 + GroupCallActivity.this.call.invitedUsers.size();
                    this.rowsCount = size2;
                    this.invitedEndRow = size2;
                }
                if (GroupCallActivity.this.isRtmpStream() || (((ChatObject.isChannel(GroupCallActivity.this.currentChat) && !GroupCallActivity.this.currentChat.megagroup) || !ChatObject.canWriteToChat(GroupCallActivity.this.currentChat)) && (!ChatObject.isChannel(GroupCallActivity.this.currentChat) || GroupCallActivity.this.currentChat.megagroup || TextUtils.isEmpty(GroupCallActivity.this.currentChat.username)))) {
                    this.addMemberRow = -1;
                } else {
                    int i5 = this.rowsCount;
                    this.rowsCount = i5 + 1;
                    this.addMemberRow = i5;
                }
                int i6 = this.rowsCount;
                this.rowsCount = i6 + 1;
                this.lastRow = i6;
            }
        }

        public void notifyDataSetChanged() {
            updateRows();
            super.notifyDataSetChanged();
        }

        public void notifyItemChanged(int position) {
            updateRows();
            super.notifyItemChanged(position);
        }

        public void notifyItemChanged(int position, Object payload) {
            updateRows();
            super.notifyItemChanged(position, payload);
        }

        public void notifyItemRangeChanged(int positionStart, int itemCount) {
            updateRows();
            super.notifyItemRangeChanged(positionStart, itemCount);
        }

        public void notifyItemRangeChanged(int positionStart, int itemCount, Object payload) {
            updateRows();
            super.notifyItemRangeChanged(positionStart, itemCount, payload);
        }

        public void notifyItemInserted(int position) {
            updateRows();
            super.notifyItemInserted(position);
        }

        public void notifyItemMoved(int fromPosition, int toPosition) {
            updateRows();
            super.notifyItemMoved(fromPosition, toPosition);
        }

        public void notifyItemRangeInserted(int positionStart, int itemCount) {
            updateRows();
            super.notifyItemRangeInserted(positionStart, itemCount);
        }

        public void notifyItemRemoved(int position) {
            updateRows();
            super.notifyItemRemoved(position);
        }

        public void notifyItemRangeRemoved(int positionStart, int itemCount) {
            updateRows();
            super.notifyItemRangeRemoved(positionStart, itemCount);
        }

        public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
            int type = holder.getItemViewType();
            boolean z = false;
            String key = "voipgroup_mutedIcon";
            if (type == 1) {
                GroupCallUserCell cell = (GroupCallUserCell) holder.itemView;
                if (GroupCallActivity.this.actionBar.getTag() == null) {
                    key = "voipgroup_mutedIconUnscrolled";
                }
                cell.setGrayIconColor(key, Theme.getColor(key));
                if (holder.getAdapterPosition() != getItemCount() - 2) {
                    z = true;
                }
                cell.setDrawDivider(z);
            } else if (type == 2) {
                GroupCallInvitedCell cell2 = (GroupCallInvitedCell) holder.itemView;
                if (GroupCallActivity.this.actionBar.getTag() == null) {
                    key = "voipgroup_mutedIconUnscrolled";
                }
                cell2.setGrayIconColor(key, Theme.getColor(key));
                if (holder.getAdapterPosition() != getItemCount() - 2) {
                    z = true;
                }
                cell2.setDrawDivider(z);
            }
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            TLRPC.TL_groupCallParticipant participant;
            Long uid;
            ChatObject.VideoParticipant participant2;
            ChatObject.VideoParticipant participant3;
            RecyclerView.ViewHolder viewHolder = holder;
            int i = position;
            TLRPC.FileLocation uploadingAvatar = null;
            float uploadingProgress = 1.0f;
            boolean animated = false;
            switch (holder.getItemViewType()) {
                case 0:
                    GroupCallTextCell textCell = (GroupCallTextCell) viewHolder.itemView;
                    int color = AndroidUtilities.getOffsetColor(Theme.getColor("voipgroup_lastSeenTextUnscrolled"), Theme.getColor("voipgroup_lastSeenText"), GroupCallActivity.this.actionBar.getTag() != null ? 1.0f : 0.0f, 1.0f);
                    textCell.setColors(color, color);
                    if (!ChatObject.isChannel(GroupCallActivity.this.currentChat) || GroupCallActivity.this.currentChat.megagroup || TextUtils.isEmpty(GroupCallActivity.this.currentChat.username)) {
                        textCell.setTextAndIcon(LocaleController.getString("VoipGroupInviteMember", NUM), NUM, false);
                        return;
                    } else {
                        textCell.setTextAndIcon(LocaleController.getString("VoipGroupShareLink", NUM), NUM, false);
                        return;
                    }
                case 1:
                    GroupCallUserCell userCell = (GroupCallUserCell) viewHolder.itemView;
                    int row = i - this.usersStartRow;
                    if (GroupCallActivity.this.delayedGroupCallUpdated) {
                        if (row < 0 || row >= GroupCallActivity.this.oldParticipants.size()) {
                            participant = null;
                        } else {
                            participant = (TLRPC.TL_groupCallParticipant) GroupCallActivity.this.oldParticipants.get(row);
                        }
                    } else if (row < 0 || row >= GroupCallActivity.this.call.visibleParticipants.size()) {
                        participant = null;
                    } else {
                        participant = GroupCallActivity.this.call.visibleParticipants.get(row);
                    }
                    if (participant != null) {
                        long peerId = MessageObject.getPeerId(participant.peer);
                        long selfPeerId = MessageObject.getPeerId(GroupCallActivity.this.selfPeer);
                        if (peerId == selfPeerId && GroupCallActivity.this.avatarUpdaterDelegate != null) {
                            uploadingAvatar = GroupCallActivity.this.avatarUpdaterDelegate.avatar;
                        }
                        if (uploadingAvatar != null) {
                            uploadingProgress = GroupCallActivity.this.avatarUpdaterDelegate.uploadingProgress;
                        }
                        if (userCell.getParticipant() != null && MessageObject.getPeerId(userCell.getParticipant().peer) == peerId) {
                            animated = true;
                        }
                        TLRPC.TL_groupCallParticipant tL_groupCallParticipant = participant;
                        userCell.setData(GroupCallActivity.this.accountInstance, participant, GroupCallActivity.this.call, selfPeerId, uploadingAvatar, animated);
                        userCell.setUploadProgress(uploadingProgress, animated);
                        return;
                    }
                    return;
                case 2:
                    GroupCallInvitedCell invitedCell = (GroupCallInvitedCell) viewHolder.itemView;
                    int row2 = i - this.invitedStartRow;
                    if (GroupCallActivity.this.delayedGroupCallUpdated) {
                        if (row2 < 0 || row2 >= GroupCallActivity.this.oldInvited.size()) {
                            uid = null;
                        } else {
                            uid = (Long) GroupCallActivity.this.oldInvited.get(row2);
                        }
                    } else if (row2 < 0 || row2 >= GroupCallActivity.this.call.invitedUsers.size()) {
                        uid = null;
                    } else {
                        uid = GroupCallActivity.this.call.invitedUsers.get(row2);
                    }
                    if (uid != null) {
                        invitedCell.setData(GroupCallActivity.this.currentAccount, uid);
                        return;
                    }
                    return;
                case 4:
                    GroupCallGridCell userCell2 = (GroupCallGridCell) viewHolder.itemView;
                    ChatObject.VideoParticipant oldParticipant = userCell2.getParticipant();
                    int row3 = i - this.usersVideoGridStartRow;
                    userCell2.spanCount = GroupCallActivity.this.spanSizeLookup.getSpanSize(i);
                    if (GroupCallActivity.this.delayedGroupCallUpdated) {
                        if (row3 < 0 || row3 >= GroupCallActivity.this.oldVideoParticipants.size()) {
                            participant2 = null;
                        } else {
                            participant2 = (ChatObject.VideoParticipant) GroupCallActivity.this.oldVideoParticipants.get(row3);
                        }
                    } else if (row3 < 0 || row3 >= GroupCallActivity.this.visibleVideoParticipants.size()) {
                        participant2 = null;
                    } else {
                        participant2 = GroupCallActivity.this.visibleVideoParticipants.get(row3);
                    }
                    if (participant2 != null) {
                        long peerId2 = MessageObject.getPeerId(participant2.participant.peer);
                        long selfPeerId2 = MessageObject.getPeerId(GroupCallActivity.this.selfPeer);
                        if (peerId2 == selfPeerId2 && GroupCallActivity.this.avatarUpdaterDelegate != null) {
                            uploadingAvatar = GroupCallActivity.this.avatarUpdaterDelegate.avatar;
                        }
                        if (uploadingAvatar != null) {
                            float f = GroupCallActivity.this.avatarUpdaterDelegate.uploadingProgress;
                        }
                        boolean z = userCell2.getParticipant() != null && userCell2.getParticipant().equals(participant2);
                        participant3 = participant2;
                        userCell2.setData(GroupCallActivity.this.accountInstance, participant2, GroupCallActivity.this.call, selfPeerId2);
                    } else {
                        participant3 = participant2;
                    }
                    if (oldParticipant != null && !oldParticipant.equals(participant3) && userCell2.attached && userCell2.getRenderer() != null) {
                        GroupCallActivity.this.attachRenderer(userCell2, false);
                        GroupCallActivity.this.attachRenderer(userCell2, true);
                        return;
                    }
                    return;
                default:
                    return;
            }
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int type = holder.getItemViewType();
            return (type == 3 || type == 4 || type == 5 || type == 6) ? false : true;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    view = new GroupCallTextCell(this.mContext) {
                        /* access modifiers changed from: protected */
                        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                            if (AndroidUtilities.isTablet()) {
                                super.onMeasure(View.MeasureSpec.makeMeasureSpec(Math.min(AndroidUtilities.dp(420.0f), View.MeasureSpec.getSize(widthMeasureSpec)), NUM), heightMeasureSpec);
                            } else {
                                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                            }
                        }
                    };
                    break;
                case 1:
                    view = new GroupCallUserCell(this.mContext) {
                        /* access modifiers changed from: protected */
                        public void onMuteClick(GroupCallUserCell cell) {
                            boolean unused = GroupCallActivity.this.showMenuForCell(cell);
                        }

                        /* access modifiers changed from: protected */
                        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                            if (AndroidUtilities.isTablet()) {
                                super.onMeasure(View.MeasureSpec.makeMeasureSpec(Math.min(AndroidUtilities.dp(420.0f), View.MeasureSpec.getSize(widthMeasureSpec)), NUM), heightMeasureSpec);
                            } else {
                                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                            }
                        }
                    };
                    break;
                case 2:
                    view = new GroupCallInvitedCell(this.mContext) {
                        /* access modifiers changed from: protected */
                        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                            if (AndroidUtilities.isTablet()) {
                                super.onMeasure(View.MeasureSpec.makeMeasureSpec(Math.min(AndroidUtilities.dp(420.0f), View.MeasureSpec.getSize(widthMeasureSpec)), NUM), heightMeasureSpec);
                            } else {
                                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                            }
                        }
                    };
                    break;
                case 4:
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
                    break;
                case 5:
                    view = new View(this.mContext) {
                        /* access modifiers changed from: protected */
                        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                            super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(GroupCallActivity.isLandscapeMode ? 0.0f : 8.0f), NUM));
                        }
                    };
                    break;
                case 6:
                    TextView textView = new TextView(this.mContext);
                    textView.setTextColor(-8682615);
                    textView.setTextSize(1, 13.0f);
                    textView.setGravity(1);
                    textView.setPadding(0, 0, 0, AndroidUtilities.dp(10.0f));
                    if (ChatObject.isChannelOrGiga(GroupCallActivity.this.currentChat)) {
                        textView.setText(LocaleController.formatString("VoipChannelVideoNotAvailableAdmin", NUM, LocaleController.formatPluralString("Participants", GroupCallActivity.this.accountInstance.getMessagesController().groupCallVideoMaxParticipants, new Object[0])));
                    } else {
                        textView.setText(LocaleController.formatString("VoipVideoNotAvailableAdmin", NUM, LocaleController.formatPluralString("Members", GroupCallActivity.this.accountInstance.getMessagesController().groupCallVideoMaxParticipants, new Object[0])));
                    }
                    view = textView;
                    break;
                default:
                    view = new View(this.mContext);
                    break;
            }
            view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(view);
        }

        public int getItemViewType(int position) {
            if (position == this.lastRow) {
                return 3;
            }
            if (position == this.addMemberRow) {
                return 0;
            }
            if (position == this.videoGridDividerRow) {
                return 5;
            }
            if (position >= this.usersStartRow && position < this.usersEndRow) {
                return 1;
            }
            if (position >= this.usersVideoGridStartRow && position < this.usersVideoGridEndRow) {
                return 4;
            }
            if (position == this.videoNotAvailableRow) {
                return 6;
            }
            return 2;
        }
    }

    /* access modifiers changed from: private */
    public void attachRenderer(GroupCallGridCell cell, boolean attach) {
        if (!isDismissed()) {
            if (attach && cell.getRenderer() == null) {
                cell.setRenderer(GroupCallMiniTextureView.getOrCreate(this.attachedRenderers, this.renderersContainer, cell, (GroupCallFullscreenAdapter.GroupCallUserCell) null, (GroupCallGridCell) null, cell.getParticipant(), this.call, this));
            } else if (!attach && cell.getRenderer() != null) {
                cell.getRenderer().setPrimaryView((GroupCallGridCell) null);
                cell.setRenderer((GroupCallMiniTextureView) null);
            }
        }
    }

    public void setOldRows(int addMemberRow, int usersStartRow, int usersEndRow, int invitedStartRow, int invitedEndRow, int usersVideoStartRow, int usersVideoEndRow, int videoDividerRow, int videoNotAvailableRow) {
        this.oldAddMemberRow = addMemberRow;
        this.oldUsersStartRow = usersStartRow;
        this.oldUsersEndRow = usersEndRow;
        this.oldInvitedStartRow = invitedStartRow;
        this.oldInvitedEndRow = invitedEndRow;
        this.oldUsersVideoStartRow = usersVideoStartRow;
        this.oldUsersVideoEndRow = usersVideoEndRow;
        this.oldVideoDividerRow = videoDividerRow;
        this.oldVideoNotAvailableRow = videoNotAvailableRow;
    }

    private static class UpdateCallback implements ListUpdateCallback {
        final RecyclerView.Adapter adapter;

        private UpdateCallback(RecyclerView.Adapter adapter2) {
            this.adapter = adapter2;
        }

        public void onInserted(int position, int count) {
            this.adapter.notifyItemRangeInserted(position, count);
        }

        public void onRemoved(int position, int count) {
            this.adapter.notifyItemRangeRemoved(position, count);
        }

        public void onMoved(int fromPosition, int toPosition) {
            this.adapter.notifyItemMoved(fromPosition, toPosition);
        }

        public void onChanged(int position, int count, Object payload) {
            this.adapter.notifyItemRangeChanged(position, count, payload);
        }
    }

    /* access modifiers changed from: private */
    public void toggleAdminSpeak() {
        TLRPC.TL_phone_toggleGroupCallSettings req = new TLRPC.TL_phone_toggleGroupCallSettings();
        req.call = this.call.getInputGroupCall();
        req.join_muted = this.call.call.join_muted;
        req.flags |= 1;
        this.accountInstance.getConnectionsManager().sendRequest(req, new GroupCallActivity$$ExternalSyntheticLambda45(this));
    }

    /* renamed from: lambda$toggleAdminSpeak$62$org-telegram-ui-GroupCallActivity  reason: not valid java name */
    public /* synthetic */ void m3544lambda$toggleAdminSpeak$62$orgtelegramuiGroupCallActivity(TLObject response, TLRPC.TL_error error) {
        if (response != null) {
            this.accountInstance.getMessagesController().processUpdates((TLRPC.Updates) response, false);
        }
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        return new ArrayList<>();
    }

    public void onBackPressed() {
        PrivateVideoPreviewDialog privateVideoPreviewDialog = this.previewDialog;
        if (privateVideoPreviewDialog != null) {
            privateVideoPreviewDialog.dismiss(false, false);
        } else if (this.avatarsPreviewShowed) {
            dismissAvatarPreview(true);
        } else if (this.renderersContainer.inFullscreenMode) {
            fullscreenFor((ChatObject.VideoParticipant) null);
        } else {
            super.onBackPressed();
        }
    }

    private class AvatarUpdaterDelegate implements ImageUpdater.ImageUpdaterDelegate {
        /* access modifiers changed from: private */
        public TLRPC.FileLocation avatar;
        private TLRPC.FileLocation avatarBig;
        private final long peerId;
        /* access modifiers changed from: private */
        public ImageLocation uploadingImageLocation;
        public float uploadingProgress;

        public /* synthetic */ String getInitialSearchString() {
            return ImageUpdater.ImageUpdaterDelegate.CC.$default$getInitialSearchString(this);
        }

        private AvatarUpdaterDelegate(long peerId2) {
            this.peerId = peerId2;
        }

        public void didUploadPhoto(TLRPC.InputFile photo, TLRPC.InputFile video, double videoStartTimestamp, String videoPath, TLRPC.PhotoSize bigSize, TLRPC.PhotoSize smallSize) {
            AndroidUtilities.runOnUIThread(new GroupCallActivity$AvatarUpdaterDelegate$$ExternalSyntheticLambda1(this, photo, video, videoStartTimestamp, videoPath, smallSize, bigSize));
        }

        /* renamed from: lambda$didUploadPhoto$3$org-telegram-ui-GroupCallActivity$AvatarUpdaterDelegate  reason: not valid java name */
        public /* synthetic */ void m3567xfd6CLASSNAMEa1(TLRPC.InputFile photo, TLRPC.InputFile video, double videoStartTimestamp, String videoPath, TLRPC.PhotoSize smallSize, TLRPC.PhotoSize bigSize) {
            TLRPC.InputFile inputFile = photo;
            TLRPC.InputFile inputFile2 = video;
            TLRPC.PhotoSize photoSize = smallSize;
            TLRPC.PhotoSize photoSize2 = bigSize;
            if (inputFile == null && inputFile2 == null) {
                this.avatar = photoSize.location;
                TLRPC.FileLocation fileLocation = photoSize2.location;
                this.avatarBig = fileLocation;
                this.uploadingImageLocation = ImageLocation.getForLocal(fileLocation);
                GroupCallActivity.this.avatarsViewPager.addUploadingImage(this.uploadingImageLocation, ImageLocation.getForLocal(this.avatar));
                AndroidUtilities.updateVisibleRows(GroupCallActivity.this.listView);
            } else if (this.peerId > 0) {
                TLRPC.TL_photos_uploadProfilePhoto req = new TLRPC.TL_photos_uploadProfilePhoto();
                if (inputFile != null) {
                    req.file = inputFile;
                    req.flags |= 1;
                }
                if (inputFile2 != null) {
                    req.video = inputFile2;
                    req.flags |= 2;
                    req.video_start_ts = videoStartTimestamp;
                    req.flags |= 4;
                } else {
                    double d = videoStartTimestamp;
                }
                GroupCallActivity.this.accountInstance.getConnectionsManager().sendRequest(req, new GroupCallActivity$AvatarUpdaterDelegate$$ExternalSyntheticLambda3(this, videoPath));
            } else {
                double d2 = videoStartTimestamp;
                MessagesController messagesController = GroupCallActivity.this.accountInstance.getMessagesController();
                long j = -this.peerId;
                TLRPC.FileLocation fileLocation2 = photoSize.location;
                messagesController.changeChatAvatar(j, (TLRPC.TL_inputChatPhoto) null, photo, video, videoStartTimestamp, videoPath, fileLocation2, photoSize2.location, new GroupCallActivity$AvatarUpdaterDelegate$$ExternalSyntheticLambda0(this));
            }
        }

        /* renamed from: lambda$didUploadPhoto$1$org-telegram-ui-GroupCallActivity$AvatarUpdaterDelegate  reason: not valid java name */
        public /* synthetic */ void m3565xb2446e9f(String videoPath, TLObject response, TLRPC.TL_error error) {
            AndroidUtilities.runOnUIThread(new GroupCallActivity$AvatarUpdaterDelegate$$ExternalSyntheticLambda2(this, error, response, videoPath));
        }

        /* renamed from: lambda$didUploadPhoto$0$org-telegram-ui-GroupCallActivity$AvatarUpdaterDelegate  reason: not valid java name */
        public /* synthetic */ void m3564x8cb0659e(TLRPC.TL_error error, TLObject response, String videoPath) {
            ImageLocation thumb;
            String str = videoPath;
            if (this.uploadingImageLocation != null) {
                GroupCallActivity.this.avatarsViewPager.removeUploadingImage(this.uploadingImageLocation);
                this.uploadingImageLocation = null;
            }
            if (error == null) {
                TLRPC.User user = GroupCallActivity.this.accountInstance.getMessagesController().getUser(Long.valueOf(GroupCallActivity.this.accountInstance.getUserConfig().getClientUserId()));
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
                TLRPC.TL_photos_photo photos_photo = (TLRPC.TL_photos_photo) response;
                ArrayList<TLRPC.PhotoSize> sizes = photos_photo.photo.sizes;
                TLRPC.PhotoSize small = FileLoader.getClosestPhotoSizeWithSize(sizes, 150);
                TLRPC.PhotoSize big = FileLoader.getClosestPhotoSizeWithSize(sizes, 800);
                TLRPC.VideoSize videoSize = photos_photo.photo.video_sizes.isEmpty() ? null : photos_photo.photo.video_sizes.get(0);
                user.photo = new TLRPC.TL_userProfilePhoto();
                user.photo.photo_id = photos_photo.photo.id;
                if (small != null) {
                    user.photo.photo_small = small.location;
                }
                if (big != null) {
                    user.photo.photo_big = big.location;
                }
                if (!(small == null || this.avatar == null)) {
                    FileLoader.getInstance(GroupCallActivity.this.currentAccount).getPathToAttach(this.avatar, true).renameTo(FileLoader.getInstance(GroupCallActivity.this.currentAccount).getPathToAttach(small, true));
                    user = user;
                    ImageLoader.getInstance().replaceImageInCache(this.avatar.volume_id + "_" + this.avatar.local_id + "@50_50", small.location.volume_id + "_" + small.location.local_id + "@50_50", ImageLocation.getForUser(user, 1), false);
                }
                if (!(big == null || this.avatarBig == null)) {
                    FileLoader.getInstance(GroupCallActivity.this.currentAccount).getPathToAttach(this.avatarBig, true).renameTo(FileLoader.getInstance(GroupCallActivity.this.currentAccount).getPathToAttach(big, true));
                }
                if (!(videoSize == null || str == null)) {
                    new File(str).renameTo(FileLoader.getInstance(GroupCallActivity.this.currentAccount).getPathToAttach(videoSize, "mp4", true));
                }
                GroupCallActivity.this.accountInstance.getMessagesStorage().clearUserPhotos(user.id);
                ArrayList<TLRPC.User> users = new ArrayList<>();
                users.add(user);
                GroupCallActivity.this.accountInstance.getMessagesStorage().putUsersAndChats(users, (ArrayList<TLRPC.Chat>) null, false, true);
                TLRPC.User currentUser = GroupCallActivity.this.accountInstance.getMessagesController().getUser(Long.valueOf(this.peerId));
                ImageLocation imageLocation = ImageLocation.getForUser(currentUser, 0);
                ImageLocation thumbLocation = ImageLocation.getForUser(currentUser, 1);
                if (ImageLocation.getForLocal(this.avatarBig) == null) {
                    thumb = ImageLocation.getForLocal(this.avatar);
                } else {
                    thumb = thumbLocation;
                }
                GroupCallActivity.this.avatarsViewPager.setCreateThumbFromParent(false);
                GroupCallActivity.this.avatarsViewPager.initIfEmpty(imageLocation, thumb, true);
                this.avatar = null;
                this.avatarBig = null;
                AndroidUtilities.updateVisibleRows(GroupCallActivity.this.listView);
                updateAvatarUploadingProgress(1.0f);
            }
            GroupCallActivity.this.accountInstance.getNotificationCenter().postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(MessagesController.UPDATE_MASK_ALL));
            GroupCallActivity.this.accountInstance.getNotificationCenter().postNotificationName(NotificationCenter.mainUserInfoChanged, new Object[0]);
            GroupCallActivity.this.accountInstance.getUserConfig().saveConfig(true);
        }

        /* renamed from: lambda$didUploadPhoto$2$org-telegram-ui-GroupCallActivity$AvatarUpdaterDelegate  reason: not valid java name */
        public /* synthetic */ void m3566xd7d877a0() {
            ImageLocation thumb;
            if (this.uploadingImageLocation != null) {
                GroupCallActivity.this.avatarsViewPager.removeUploadingImage(this.uploadingImageLocation);
                this.uploadingImageLocation = null;
            }
            TLRPC.Chat currentChat = GroupCallActivity.this.accountInstance.getMessagesController().getChat(Long.valueOf(-this.peerId));
            ImageLocation imageLocation = ImageLocation.getForChat(currentChat, 0);
            ImageLocation thumbLocation = ImageLocation.getForChat(currentChat, 1);
            if (ImageLocation.getForLocal(this.avatarBig) == null) {
                thumb = ImageLocation.getForLocal(this.avatar);
            } else {
                thumb = thumbLocation;
            }
            GroupCallActivity.this.avatarsViewPager.setCreateThumbFromParent(false);
            GroupCallActivity.this.avatarsViewPager.initIfEmpty(imageLocation, thumb, true);
            this.avatar = null;
            this.avatarBig = null;
            AndroidUtilities.updateVisibleRows(GroupCallActivity.this.listView);
            updateAvatarUploadingProgress(1.0f);
        }

        public void didStartUpload(boolean isVideo) {
        }

        public void onUploadProgressChanged(float progress) {
            GroupCallActivity.this.avatarsViewPager.setUploadProgress(this.uploadingImageLocation, progress);
            updateAvatarUploadingProgress(progress);
        }

        public void updateAvatarUploadingProgress(float progress) {
            this.uploadingProgress = progress;
            if (GroupCallActivity.this.listView != null) {
                for (int i = 0; i < GroupCallActivity.this.listView.getChildCount(); i++) {
                    View child = GroupCallActivity.this.listView.getChildAt(i);
                    if (child instanceof GroupCallUserCell) {
                        GroupCallUserCell cell = (GroupCallUserCell) child;
                        if (cell.isSelfUser()) {
                            cell.setUploadProgress(progress, true);
                        }
                    }
                }
            }
        }
    }

    public View getScrimView() {
        return this.scrimView;
    }

    public void onCameraSwitch(boolean isFrontFace) {
        this.attachedRenderersTmp.clear();
        this.attachedRenderersTmp.addAll(this.attachedRenderers);
        for (int i = 0; i < this.attachedRenderersTmp.size(); i++) {
            this.attachedRenderersTmp.get(i).updateAttachState(true);
        }
        PrivateVideoPreviewDialog privateVideoPreviewDialog = this.previewDialog;
        if (privateVideoPreviewDialog != null) {
            privateVideoPreviewDialog.update();
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
                    int N = GroupCallActivity.this.listView.getChildCount();
                    for (int a = 0; a < N; a++) {
                        View child = GroupCallActivity.this.listView.getChildAt(a);
                        RecyclerView.ViewHolder holder = GroupCallActivity.this.listView.findContainingViewHolder(child);
                        if (!(holder == null || holder.getItemViewType() == 3 || holder.getItemViewType() == 4 || holder.getItemViewType() == 5 || this.addingHolders.contains(holder))) {
                            this.outMaxBottom = Math.max(this.outMaxBottom, child.getY() + ((float) child.getMeasuredHeight()));
                            this.outMinTop = Math.min(this.outMinTop, Math.max(0.0f, child.getY()));
                        }
                    }
                    this.animationProgress = 0.0f;
                    GroupCallActivity.this.listView.invalidate();
                }
            }
        }

        public void runPendingAnimations() {
            boolean removalsPending = !this.mPendingRemovals.isEmpty();
            boolean movesPending = !this.mPendingMoves.isEmpty();
            boolean additionsPending = !this.mPendingAdditions.isEmpty();
            ValueAnimator valueAnimator = this.animator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
                this.animator = null;
            }
            if (removalsPending || movesPending || additionsPending) {
                this.animationProgress = 0.0f;
                ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
                this.animator = ofFloat;
                ofFloat.addUpdateListener(new GroupCallActivity$GroupCallItemAnimator$$ExternalSyntheticLambda0(this));
                this.animator.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        GroupCallItemAnimator.this.animator = null;
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

        /* renamed from: lambda$runPendingAnimations$0$org-telegram-ui-GroupCallActivity$GroupCallItemAnimator  reason: not valid java name */
        public /* synthetic */ void m3568x8b80169(ValueAnimator valueAnimator) {
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

    /* access modifiers changed from: private */
    public void onUserLeaveHint() {
        if (isRtmpStream() && AndroidUtilities.checkInlinePermissions(this.parentActivity) && !RTMPStreamPipOverlay.isVisible()) {
            dismiss();
            AndroidUtilities.runOnUIThread(GroupCallActivity$$ExternalSyntheticLambda41.INSTANCE, 100);
        }
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.parentActivity.addOnUserLeaveHintListener(this.onUserLeaveHintListener);
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

    public boolean isRtmpLandscapeMode() {
        if (!isRtmpStream() || this.call.visibleVideoParticipants.isEmpty()) {
            return false;
        }
        return this.call.visibleVideoParticipants.get(0).aspectRatio == 0.0f || this.call.visibleVideoParticipants.get(0).aspectRatio >= 1.0f;
    }

    public boolean isRtmpStream() {
        ChatObject.Call call2 = this.call;
        return call2 != null && call2.call.rtmp_stream;
    }
}
