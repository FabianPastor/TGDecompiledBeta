package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.DialogInterface;
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
import java.util.ArrayList;
import java.util.Locale;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.FileLog;
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
import org.telegram.tgnet.TLRPC$GroupCall;
import org.telegram.tgnet.TLRPC$InputPeer;
import org.telegram.tgnet.TLRPC$Peer;
import org.telegram.tgnet.TLRPC$TL_chatInviteExported;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_groupCallDiscarded;
import org.telegram.tgnet.TLRPC$TL_groupCallParticipant;
import org.telegram.tgnet.TLRPC$TL_inputPeerChat;
import org.telegram.tgnet.TLRPC$TL_inputPeerUser;
import org.telegram.tgnet.TLRPC$TL_inputUser;
import org.telegram.tgnet.TLRPC$TL_messages_exportChatInvite;
import org.telegram.tgnet.TLRPC$TL_phone_exportGroupCallInvite;
import org.telegram.tgnet.TLRPC$TL_phone_exportedGroupCallInvite;
import org.telegram.tgnet.TLRPC$TL_phone_inviteToGroupCall;
import org.telegram.tgnet.TLRPC$TL_phone_toggleGroupCallSettings;
import org.telegram.tgnet.TLRPC$Updates;
import org.telegram.tgnet.TLRPC$User;
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
import org.telegram.ui.Components.JoinCallAlert;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.ProfileGalleryView;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.RLottieImageView;
import org.telegram.ui.Components.RadialProgressView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.ShareAlert;
import org.telegram.ui.Components.UndoView;
import org.telegram.ui.Components.voip.VoIPToggleButton;

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
    private FrameLayout buttonsContainer;
    public ChatObject.Call call;
    /* access modifiers changed from: private */
    public boolean changingPermissions;
    /* access modifiers changed from: private */
    public float colorProgress;
    /* access modifiers changed from: private */
    public final int[] colorsTmp = new int[3];
    private int currentCallState;
    public TLRPC$Chat currentChat;
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
    public boolean scheduled;
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
    private WeavingState[] states = new WeavingState[5];
    /* access modifiers changed from: private */
    public float switchProgress = 1.0f;
    /* access modifiers changed from: private */
    public final BlobDrawable tinyWaveDrawable;
    private AudioPlayerAlert.ClippingTextViewSwitcher titleTextView;
    private UndoView[] undoView = new UndoView[2];
    /* access modifiers changed from: private */
    public Runnable unmuteRunnable = $$Lambda$GroupCallActivity$Fejzw3BitRkLCnwqEMTIYvTsgw.INSTANCE;
    private Runnable updateCallRecordRunnable;
    private TLObject userSwitchObject;

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
        if (this.scheduled && VoIPService.getSharedInstance() != null) {
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
                r9 = 2131558475(0x7f0d004b, float:1.8742267E38)
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
                        tLObject = MessagesController.getInstance(this.this$0.currentAccount).getUser(Integer.valueOf(peerId));
                    } else {
                        tLObject = MessagesController.getInstance(this.this$0.currentAccount).getChat(Integer.valueOf(-peerId));
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
                float f2 = (float) j;
                float access$700 = this.time + ((BlobDrawable.GRADIENT_SPEED_MIN + 0.5f) * f2) + (f2 * BlobDrawable.GRADIENT_SPEED_MAX * 2.0f * GroupCallActivity.this.amplitude);
                this.time = access$700;
                float f3 = this.duration;
                if (access$700 > f3) {
                    this.time = f3;
                }
                float interpolation = CubicBezierInterpolator.EASE_OUT.getInterpolation(this.time / f3);
                float f4 = (float) i3;
                float f5 = this.startX;
                float f6 = (((float) i2) + ((f5 + ((this.targetX - f5) * interpolation)) * f4)) - 200.0f;
                float f7 = this.startY;
                float f8 = (((float) i) + (f4 * (f7 + ((this.targetY - f7) * interpolation)))) - 200.0f;
                int i4 = this.currentState;
                float dp = (((float) AndroidUtilities.dp(122.0f)) / 400.0f) * ((i4 == 2 || i4 == 4) ? 1.0f : i4 == 1 ? 4.0f : 2.5f);
                this.matrix.reset();
                this.matrix.postTranslate(f6, f8);
                this.matrix.postScale(dp, dp, f6 + 200.0f, f8 + 200.0f);
                this.shader.setLocalMatrix(this.matrix);
            }
        }

        private void setTarget() {
            int i = this.currentState;
            if (i == 2 || i == 4) {
                this.targetX = ((((float) Utilities.random.nextInt(100)) * 0.2f) / 100.0f) + 0.85f;
                this.targetY = 1.0f;
            } else if (i == 1) {
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
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.webRtcMicAmplitudeEvent);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didEndCall);
        super.dismiss();
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        String str;
        TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant;
        RecyclerView.ViewHolder findViewHolderForAdapterPosition;
        int i3 = 0;
        if (i == NotificationCenter.groupCallUpdated) {
            Long l = objArr[1];
            ChatObject.Call call2 = this.call;
            if (call2 != null && call2.call.id == l.longValue()) {
                if (this.call.call instanceof TLRPC$TL_groupCallDiscarded) {
                    dismiss();
                    return;
                }
                updateItems();
                int childCount = this.listView.getChildCount();
                for (int i4 = 0; i4 < childCount; i4++) {
                    View childAt = this.listView.getChildAt(i4);
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
                    i3 = 1;
                }
                updateState(true, booleanValue);
                updateTitle(true);
                if (i3 != 0) {
                    int i5 = this.muteButtonState;
                    if (i5 == 1 || i5 == 0) {
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
            if (this.listView != null && (tLRPC$TL_groupCallParticipant = this.call.participants.get(MessageObject.getPeerId(this.selfPeer))) != null) {
                int indexOf = (this.delayedGroupCallUpdated ? this.oldParticipants : this.call.sortedParticipants).indexOf(tLRPC$TL_groupCallParticipant);
                if (indexOf >= 0 && (findViewHolderForAdapterPosition = this.listView.findViewHolderForAdapterPosition(indexOf + this.listAdapter.usersStartRow)) != null) {
                    View view = findViewHolderForAdapterPosition.itemView;
                    if (view instanceof GroupCallUserCell) {
                        ((GroupCallUserCell) view).setAmplitude((double) (floatValue * 15.0f));
                        if (findViewHolderForAdapterPosition.itemView == this.scrimView) {
                            this.containerView.invalidate();
                        }
                    }
                }
            }
        } else if (i == NotificationCenter.needShowAlert) {
            if (objArr[0].intValue() == 6) {
                String str2 = objArr[1];
                if ("GROUPCALL_PARTICIPANTS_TOO_MUCH".equals(str2)) {
                    str = LocaleController.getString("VoipGroupTooMuch", NUM);
                } else if ("ANONYMOUS_CALLS_DISABLED".equals(str2) || "GROUPCALL_ANONYMOUS_FORBIDDEN".equals(str2)) {
                    str = LocaleController.getString("VoipGroupJoinAnonymousAdmin", NUM);
                } else {
                    str = LocaleController.getString("ErrorOccurred", NUM) + "\n" + str2;
                }
                AlertDialog.Builder createSimpleAlert = AlertsCreator.createSimpleAlert(getContext(), LocaleController.getString("VoipGroupVoiceChat", NUM), str);
                createSimpleAlert.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    public final void onDismiss(DialogInterface dialogInterface) {
                        GroupCallActivity.this.lambda$didReceivedNotification$2$GroupCallActivity(dialogInterface);
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
            if (objArr[0].id == this.currentChat.id) {
                updateItems();
                updateState(isShowing(), false);
            }
        } else if (i == NotificationCenter.didLoadChatAdmins) {
            if (objArr[0].intValue() == this.currentChat.id) {
                updateItems();
                updateState(isShowing(), false);
            }
        } else if (i == NotificationCenter.applyGroupCallVisibleParticipants) {
            int childCount2 = this.listView.getChildCount();
            long longValue = objArr[0].longValue();
            while (i3 < childCount2) {
                RecyclerView.ViewHolder findContainingViewHolder = this.listView.findContainingViewHolder(this.listView.getChildAt(i3));
                if (findContainingViewHolder != null) {
                    View view2 = findContainingViewHolder.itemView;
                    if (view2 instanceof GroupCallUserCell) {
                        ((GroupCallUserCell) view2).getParticipant().lastVisibleDate = longValue;
                    }
                }
                i3++;
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$didReceivedNotification$2 */
    public /* synthetic */ void lambda$didReceivedNotification$2$GroupCallActivity(DialogInterface dialogInterface) {
        dismiss();
    }

    /* access modifiers changed from: private */
    public void applyCallParticipantUpdates() {
        RecyclerView.ViewHolder findContainingViewHolder;
        if (!this.delayedGroupCallUpdated) {
            int peerId = MessageObject.getPeerId(this.call.selfPeer);
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
        int currentTime = this.accountInstance.getConnectionsManager().getCurrentTime();
        ChatObject.Call call2 = this.call;
        int i = currentTime - call2.call.record_start_date;
        if (call2.recording) {
            this.recordItem.setSubtext(AndroidUtilities.formatDuration(i, false));
        } else {
            this.recordItem.setSubtext((String) null);
        }
    }

    private void updateItems() {
        boolean z;
        TLObject tLObject;
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
                this.recordItem.setVisibility(0);
                this.recordCallDrawable.setRecording(this.call.recording);
                if (this.call.recording) {
                    if (this.updateCallRecordRunnable == null) {
                        $$Lambda$GroupCallActivity$cXEIKfe1499toRfCuxD8yROd5j0 r0 = new Runnable() {
                            public final void run() {
                                GroupCallActivity.this.lambda$updateItems$3$GroupCallActivity();
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
            if (VoIPService.getSharedInstance() == null || !VoIPService.getSharedInstance().hasFewPeers) {
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
                    this.accountSwitchImageView.setImage(ImageLocation.getForUser(user, false), "50_50", (Drawable) this.accountSwitchAvatarDrawable, (Object) user);
                } else {
                    TLRPC$Chat chat2 = this.accountInstance.getMessagesController().getChat(Integer.valueOf(-peerId));
                    this.accountSwitchAvatarDrawable.setInfo(chat2);
                    this.accountSwitchImageView.setImage(ImageLocation.getForChat(chat2, false), "50_50", (Drawable) this.accountSwitchAvatarDrawable, (Object) chat2);
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
    /* renamed from: lambda$updateItems$3 */
    public /* synthetic */ void lambda$updateItems$3$GroupCallActivity() {
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
                        GroupCallActivity.lambda$makeFocusable$6(BottomSheet.this, this.f$1, this.f$2, this.f$3);
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
                        GroupCallActivity.lambda$makeFocusable$7(EditTextBoldCursor.this);
                    }
                }, 100);
            }
        }
    }

    static /* synthetic */ void lambda$makeFocusable$6(BottomSheet bottomSheet, EditTextBoldCursor editTextBoldCursor, boolean z, AlertDialog alertDialog) {
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

    static /* synthetic */ void lambda$makeFocusable$7(EditTextBoldCursor editTextBoldCursor) {
        editTextBoldCursor.requestFocus();
        AndroidUtilities.showKeyboard(editTextBoldCursor);
    }

    public static void create(LaunchActivity launchActivity, AccountInstance accountInstance2) {
        ChatObject.Call call2;
        TLRPC$Chat chat;
        if (groupCallInstance == null && VoIPService.getSharedInstance() != null && (call2 = VoIPService.getSharedInstance().groupCall) != null && (chat = accountInstance2.getMessagesController().getChat(Integer.valueOf(call2.chatId))) != null) {
            call2.addSelfDummyParticipant();
            GroupCallActivity groupCallActivity = new GroupCallActivity(launchActivity, accountInstance2, call2, chat);
            groupCallInstance = groupCallActivity;
            groupCallActivity.parentActivity = launchActivity;
            groupCallActivity.show();
        }
    }

    /* JADX WARNING: Illegal instructions before constructor call */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private GroupCallActivity(android.content.Context r30, org.telegram.messenger.AccountInstance r31, org.telegram.messenger.ChatObject.Call r32, org.telegram.tgnet.TLRPC$Chat r33) {
        /*
            r29 = this;
            r0 = r29
            r1 = r30
            r2 = r32
            r3 = r33
            r4 = 0
            r0.<init>(r1, r4)
            r5 = 2
            android.widget.TextView[] r6 = new android.widget.TextView[r5]
            r0.muteLabel = r6
            android.widget.TextView[] r6 = new android.widget.TextView[r5]
            r0.muteSubLabel = r6
            org.telegram.ui.Components.UndoView[] r6 = new org.telegram.ui.Components.UndoView[r5]
            r0.undoView = r6
            android.graphics.RectF r6 = new android.graphics.RectF
            r6.<init>()
            r0.rect = r6
            android.graphics.Paint r6 = new android.graphics.Paint
            r7 = 1
            r6.<init>(r7)
            r0.listViewBackgroundPaint = r6
            java.util.ArrayList r6 = new java.util.ArrayList
            r6.<init>()
            r0.oldParticipants = r6
            java.util.ArrayList r6 = new java.util.ArrayList
            r6.<init>()
            r0.oldInvited = r6
            r0.muteButtonState = r4
            android.graphics.Paint r6 = new android.graphics.Paint
            r8 = 7
            r6.<init>(r8)
            r0.paint = r6
            android.graphics.Paint r6 = new android.graphics.Paint
            r6.<init>(r8)
            r0.paintTmp = r6
            android.graphics.Paint r6 = new android.graphics.Paint
            r6.<init>(r7)
            r0.leaveBackgroundPaint = r6
            r6 = 5
            org.telegram.ui.GroupCallActivity$WeavingState[] r6 = new org.telegram.ui.GroupCallActivity.WeavingState[r6]
            r0.states = r6
            r6 = 1065353216(0x3var_, float:1.0)
            r0.switchProgress = r6
            r0.invalidateColors = r7
            r8 = 3
            int[] r9 = new int[r8]
            r0.colorsTmp = r9
            org.telegram.ui.-$$Lambda$GroupCallActivity$Fejzw3-BitRkLCnwqEMTIYvTsgw r9 = org.telegram.ui.$$Lambda$GroupCallActivity$Fejzw3BitRkLCnwqEMTIYvTsgw.INSTANCE
            r0.unmuteRunnable = r9
            org.telegram.ui.-$$Lambda$GroupCallActivity$kOH_8vLsAK1_zQgyxcm7-Ylrcfc r9 = new org.telegram.ui.-$$Lambda$GroupCallActivity$kOH_8vLsAK1_zQgyxcm7-Ylrcfc
            r9.<init>()
            r0.pressRunnable = r9
            java.lang.String[] r9 = new java.lang.String[r5]
            r0.invites = r9
            r9 = -1
            r0.popupAnimationIndex = r9
            org.telegram.ui.GroupCallActivity$27 r10 = new org.telegram.ui.GroupCallActivity$27
            r10.<init>()
            r0.diffUtilsCallback = r10
            r10 = r31
            r0.accountInstance = r10
            r0.call = r2
            r0.currentChat = r3
            int r10 = r31.getCurrentAccount()
            r0.currentAccount = r10
            r0.drawNavigationBar = r7
            int r10 = android.os.Build.VERSION.SDK_INT
            r11 = 30
            if (r10 < r11) goto L_0x0096
            android.view.Window r10 = r29.getWindow()
            r11 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            r10.setNavigationBarColor(r11)
        L_0x0096:
            r0.scrollNavBar = r7
            r10 = 0
            r0.navBarColorKey = r10
            org.telegram.ui.GroupCallActivity$2 r11 = new org.telegram.ui.GroupCallActivity$2
            r11.<init>()
            r0.scrimPaint = r11
            org.telegram.ui.-$$Lambda$GroupCallActivity$AxW6qSe6lAaev_iBP9PCk08J8Vo r11 = new org.telegram.ui.-$$Lambda$GroupCallActivity$AxW6qSe6lAaev_iBP9PCk08J8Vo
            r11.<init>()
            r0.setOnDismissListener(r11)
            r11 = 75
            r0.setDimBehindAlpha(r11)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_groupCallParticipant> r11 = r0.oldParticipants
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_groupCallParticipant> r12 = r2.sortedParticipants
            r11.addAll(r12)
            java.util.ArrayList<java.lang.Integer> r11 = r0.oldInvited
            java.util.ArrayList<java.lang.Integer> r12 = r2.invitedUsers
            r11.addAll(r12)
            org.telegram.messenger.voip.VoIPService r11 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            org.telegram.tgnet.TLRPC$InputPeer r11 = r11.getGroupCallPeer()
            if (r11 != 0) goto L_0x00db
            org.telegram.tgnet.TLRPC$TL_peerUser r11 = new org.telegram.tgnet.TLRPC$TL_peerUser
            r11.<init>()
            r0.selfPeer = r11
            org.telegram.messenger.AccountInstance r12 = r0.accountInstance
            org.telegram.messenger.UserConfig r12 = r12.getUserConfig()
            int r12 = r12.getClientUserId()
            r11.user_id = r12
            goto L_0x010a
        L_0x00db:
            boolean r12 = r11 instanceof org.telegram.tgnet.TLRPC$TL_inputPeerChannel
            if (r12 == 0) goto L_0x00eb
            org.telegram.tgnet.TLRPC$TL_peerChannel r12 = new org.telegram.tgnet.TLRPC$TL_peerChannel
            r12.<init>()
            r0.selfPeer = r12
            int r11 = r11.channel_id
            r12.channel_id = r11
            goto L_0x010a
        L_0x00eb:
            boolean r12 = r11 instanceof org.telegram.tgnet.TLRPC$TL_inputPeerUser
            if (r12 == 0) goto L_0x00fb
            org.telegram.tgnet.TLRPC$TL_peerUser r12 = new org.telegram.tgnet.TLRPC$TL_peerUser
            r12.<init>()
            r0.selfPeer = r12
            int r11 = r11.user_id
            r12.user_id = r11
            goto L_0x010a
        L_0x00fb:
            boolean r12 = r11 instanceof org.telegram.tgnet.TLRPC$TL_inputPeerChat
            if (r12 == 0) goto L_0x010a
            org.telegram.tgnet.TLRPC$TL_peerChat r12 = new org.telegram.tgnet.TLRPC$TL_peerChat
            r12.<init>()
            r0.selfPeer = r12
            int r11 = r11.chat_id
            r12.chat_id = r11
        L_0x010a:
            org.telegram.messenger.voip.VoIPService r11 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            int r11 = r11.getCallState()
            r0.currentCallState = r11
            org.telegram.ui.-$$Lambda$GroupCallActivity$DjkQsRy3jvsu-ui4m6ygfn5H_CQ r11 = new org.telegram.ui.-$$Lambda$GroupCallActivity$DjkQsRy3jvsu-ui4m6ygfn5H_CQ
            r11.<init>(r2)
            org.telegram.messenger.voip.VoIPService.audioLevelsCallback = r11
            org.telegram.messenger.AccountInstance r11 = r0.accountInstance
            org.telegram.messenger.NotificationCenter r11 = r11.getNotificationCenter()
            int r12 = org.telegram.messenger.NotificationCenter.groupCallUpdated
            r11.addObserver(r0, r12)
            org.telegram.messenger.AccountInstance r11 = r0.accountInstance
            org.telegram.messenger.NotificationCenter r11 = r11.getNotificationCenter()
            int r12 = org.telegram.messenger.NotificationCenter.needShowAlert
            r11.addObserver(r0, r12)
            org.telegram.messenger.AccountInstance r11 = r0.accountInstance
            org.telegram.messenger.NotificationCenter r11 = r11.getNotificationCenter()
            int r12 = org.telegram.messenger.NotificationCenter.chatInfoDidLoad
            r11.addObserver(r0, r12)
            org.telegram.messenger.AccountInstance r11 = r0.accountInstance
            org.telegram.messenger.NotificationCenter r11 = r11.getNotificationCenter()
            int r12 = org.telegram.messenger.NotificationCenter.didLoadChatAdmins
            r11.addObserver(r0, r12)
            org.telegram.messenger.AccountInstance r11 = r0.accountInstance
            org.telegram.messenger.NotificationCenter r11 = r11.getNotificationCenter()
            int r12 = org.telegram.messenger.NotificationCenter.applyGroupCallVisibleParticipants
            r11.addObserver(r0, r12)
            org.telegram.messenger.NotificationCenter r11 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r12 = org.telegram.messenger.NotificationCenter.webRtcMicAmplitudeEvent
            r11.addObserver(r0, r12)
            org.telegram.messenger.NotificationCenter r11 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r12 = org.telegram.messenger.NotificationCenter.didEndCall
            r11.addObserver(r0, r12)
            android.content.res.Resources r11 = r30.getResources()
            r12 = 2131166006(0x7var_, float:1.7946245E38)
            android.graphics.drawable.Drawable r11 = r11.getDrawable(r12)
            android.graphics.drawable.Drawable r11 = r11.mutate()
            r0.shadowDrawable = r11
            org.telegram.ui.Components.RLottieDrawable r11 = new org.telegram.ui.Components.RLottieDrawable
            r13 = 2131558503(0x7f0d0067, float:1.8742324E38)
            r19 = 1113849856(0x42640000, float:57.0)
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r19)
            r20 = 1113325568(0x425CLASSNAME, float:55.0)
            int r16 = org.telegram.messenger.AndroidUtilities.dp(r20)
            r17 = 1
            r18 = 0
            java.lang.String r14 = "NUM"
            r12 = r11
            r12.<init>((int) r13, (java.lang.String) r14, (int) r15, (int) r16, (boolean) r17, (int[]) r18)
            r0.bigMicDrawable = r11
            org.telegram.ui.Components.RLottieDrawable r11 = new org.telegram.ui.Components.RLottieDrawable
            r22 = 2131558436(0x7f0d0024, float:1.8742188E38)
            int r24 = org.telegram.messenger.AndroidUtilities.dp(r19)
            int r25 = org.telegram.messenger.AndroidUtilities.dp(r20)
            r26 = 1
            r27 = 0
            java.lang.String r23 = "NUM"
            r21 = r11
            r21.<init>((int) r22, (java.lang.String) r23, (int) r24, (int) r25, (boolean) r26, (int[]) r27)
            r0.handDrawables = r11
            org.telegram.ui.GroupCallActivity$3 r11 = new org.telegram.ui.GroupCallActivity$3
            r11.<init>(r1)
            r0.containerView = r11
            r11.setFocusable(r7)
            android.view.ViewGroup r11 = r0.containerView
            r11.setFocusableInTouchMode(r7)
            android.view.ViewGroup r11 = r0.containerView
            r11.setWillNotDraw(r4)
            android.view.ViewGroup r11 = r0.containerView
            int r12 = r0.backgroundPaddingLeft
            r11.setPadding(r12, r4, r12, r4)
            android.view.ViewGroup r11 = r0.containerView
            r11.setKeepScreenOn(r7)
            android.view.ViewGroup r11 = r0.containerView
            r11.setClipChildren(r4)
            org.telegram.ui.GroupCallActivity$4 r11 = new org.telegram.ui.GroupCallActivity$4
            r11.<init>(r1)
            r0.listView = r11
            r11.setClipToPadding(r4)
            org.telegram.ui.Components.RecyclerListView r11 = r0.listView
            r11.setClipChildren(r4)
            org.telegram.ui.GroupCallActivity$5 r11 = new org.telegram.ui.GroupCallActivity$5
            r11.<init>()
            r0.itemAnimator = r11
            r11.setDelayAnimations(r4)
            org.telegram.ui.Components.RecyclerListView r11 = r0.listView
            androidx.recyclerview.widget.DefaultItemAnimator r12 = r0.itemAnimator
            r11.setItemAnimator(r12)
            org.telegram.ui.Components.RecyclerListView r11 = r0.listView
            org.telegram.ui.GroupCallActivity$6 r12 = new org.telegram.ui.GroupCallActivity$6
            r12.<init>(r2)
            r11.setOnScrollListener(r12)
            org.telegram.ui.Components.RecyclerListView r11 = r0.listView
            r11.setVerticalScrollBarEnabled(r4)
            org.telegram.ui.Components.RecyclerListView r11 = r0.listView
            org.telegram.ui.Components.FillLastLinearLayoutManager r15 = new org.telegram.ui.Components.FillLastLinearLayoutManager
            android.content.Context r13 = r29.getContext()
            r14 = 1
            r16 = 0
            r17 = 0
            org.telegram.ui.Components.RecyclerListView r12 = r0.listView
            r18 = r12
            r12 = r15
            r8 = r15
            r15 = r16
            r16 = r17
            r17 = r18
            r12.<init>(r13, r14, r15, r16, r17)
            r0.layoutManager = r8
            r11.setLayoutManager(r8)
            org.telegram.ui.Components.FillLastLinearLayoutManager r8 = r0.layoutManager
            r8.setBind(r4)
            android.view.ViewGroup r8 = r0.containerView
            org.telegram.ui.Components.RecyclerListView r11 = r0.listView
            r12 = -1
            r13 = -1082130432(0xffffffffbvar_, float:-1.0)
            r14 = 51
            r15 = 1096810496(0x41600000, float:14.0)
            r16 = 1096810496(0x41600000, float:14.0)
            r17 = 1096810496(0x41600000, float:14.0)
            r18 = 1130823680(0x43670000, float:231.0)
            android.widget.FrameLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createFrame(r12, r13, r14, r15, r16, r17, r18)
            r8.addView(r11, r12)
            org.telegram.ui.Components.RecyclerListView r8 = r0.listView
            org.telegram.ui.GroupCallActivity$ListAdapter r11 = new org.telegram.ui.GroupCallActivity$ListAdapter
            r11.<init>(r1)
            r0.listAdapter = r11
            r8.setAdapter(r11)
            org.telegram.ui.Components.RecyclerListView r8 = r0.listView
            r11 = 13
            r8.setTopBottomSelectorRadius(r11)
            org.telegram.ui.Components.RecyclerListView r8 = r0.listView
            java.lang.String r11 = "voipgroup_listSelector"
            int r12 = org.telegram.ui.ActionBar.Theme.getColor(r11)
            r8.setSelectorDrawableColor(r12)
            org.telegram.ui.Components.RecyclerListView r8 = r0.listView
            org.telegram.ui.-$$Lambda$GroupCallActivity$oMs4ki43Z5yT7FG0mzojTq6MFls r12 = new org.telegram.ui.-$$Lambda$GroupCallActivity$oMs4ki43Z5yT7FG0mzojTq6MFls
            r12.<init>(r2)
            r8.setOnItemClickListener((org.telegram.ui.Components.RecyclerListView.OnItemClickListenerExtended) r12)
            org.telegram.ui.Components.RecyclerListView r8 = r0.listView
            org.telegram.ui.-$$Lambda$GroupCallActivity$43BxV6498h4PWrg5e3aUrrFmTu0 r12 = new org.telegram.ui.-$$Lambda$GroupCallActivity$43BxV6498h4PWrg5e3aUrrFmTu0
            r12.<init>()
            r8.setOnItemLongClickListener((org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener) r12)
            org.telegram.ui.GroupCallActivity$8 r8 = new org.telegram.ui.GroupCallActivity$8
            r8.<init>(r1)
            r0.buttonsContainer = r8
            r8.setWillNotDraw(r4)
            android.view.ViewGroup r8 = r0.containerView
            android.widget.FrameLayout r12 = r0.buttonsContainer
            r13 = 231(0xe7, float:3.24E-43)
            r14 = 83
            android.widget.FrameLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createFrame(r9, r13, r14)
            r8.addView(r12, r13)
            java.lang.String r8 = "voipgroup_unmuteButton2"
            int r8 = org.telegram.ui.ActionBar.Theme.getColor(r8)
            int r12 = android.graphics.Color.red(r8)
            int r13 = android.graphics.Color.green(r8)
            int r8 = android.graphics.Color.blue(r8)
            android.graphics.Matrix r14 = new android.graphics.Matrix
            r14.<init>()
            r0.radialMatrix = r14
            android.graphics.RadialGradient r14 = new android.graphics.RadialGradient
            r21 = 0
            r22 = 0
            r15 = 1126170624(0x43200000, float:160.0)
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r15)
            float r15 = (float) r15
            int[] r6 = new int[r5]
            r9 = 50
            int r9 = android.graphics.Color.argb(r9, r12, r13, r8)
            r6[r4] = r9
            int r8 = android.graphics.Color.argb(r4, r12, r13, r8)
            r6[r7] = r8
            r25 = 0
            android.graphics.Shader$TileMode r26 = android.graphics.Shader.TileMode.CLAMP
            r20 = r14
            r23 = r15
            r24 = r6
            r20.<init>(r21, r22, r23, r24, r25, r26)
            r0.radialGradient = r14
            android.graphics.Paint r6 = new android.graphics.Paint
            r6.<init>(r7)
            r0.radialPaint = r6
            android.graphics.RadialGradient r8 = r0.radialGradient
            r6.setShader(r8)
            org.telegram.ui.Components.BlobDrawable r6 = new org.telegram.ui.Components.BlobDrawable
            r8 = 9
            r6.<init>(r8)
            r0.tinyWaveDrawable = r6
            org.telegram.ui.Components.BlobDrawable r8 = new org.telegram.ui.Components.BlobDrawable
            r9 = 12
            r8.<init>(r9)
            r0.bigWaveDrawable = r8
            r12 = 1115160576(0x42780000, float:62.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            float r12 = (float) r12
            r6.minRadius = r12
            r12 = 1116733440(0x42900000, float:72.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            float r12 = (float) r12
            r6.maxRadius = r12
            r6.generateBlob()
            r12 = 1115815936(0x42820000, float:65.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            float r12 = (float) r12
            r8.minRadius = r12
            r12 = 1117126656(0x42960000, float:75.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            float r12 = (float) r12
            r8.maxRadius = r12
            r8.generateBlob()
            android.graphics.Paint r6 = r6.paint
            java.lang.String r12 = "voipgroup_unmuteButton"
            int r13 = org.telegram.ui.ActionBar.Theme.getColor(r12)
            r14 = 38
            int r13 = androidx.core.graphics.ColorUtils.setAlphaComponent(r13, r14)
            r6.setColor(r13)
            android.graphics.Paint r6 = r8.paint
            int r8 = org.telegram.ui.ActionBar.Theme.getColor(r12)
            r12 = 76
            int r8 = androidx.core.graphics.ColorUtils.setAlphaComponent(r8, r12)
            r6.setColor(r8)
            org.telegram.ui.Components.voip.VoIPToggleButton r6 = new org.telegram.ui.Components.voip.VoIPToggleButton
            r6.<init>(r1)
            r0.soundButton = r6
            r6.setCheckable(r7)
            org.telegram.ui.Components.voip.VoIPToggleButton r6 = r0.soundButton
            r6.setTextSize(r9)
            android.widget.FrameLayout r6 = r0.buttonsContainer
            org.telegram.ui.Components.voip.VoIPToggleButton r8 = r0.soundButton
            r12 = 1119092736(0x42b40000, float:90.0)
            r13 = 68
            android.widget.FrameLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createFrame(r13, r12)
            r6.addView(r8, r12)
            org.telegram.ui.Components.voip.VoIPToggleButton r6 = r0.soundButton
            org.telegram.ui.-$$Lambda$GroupCallActivity$u4roEPnPeT7DyJ-i_bWGO552ynY r8 = new org.telegram.ui.-$$Lambda$GroupCallActivity$u4roEPnPeT7DyJ-i_bWGO552ynY
            r8.<init>()
            r6.setOnClickListener(r8)
            org.telegram.ui.Components.voip.VoIPToggleButton r6 = new org.telegram.ui.Components.voip.VoIPToggleButton
            r6.<init>(r1)
            r0.leaveButton = r6
            r6.setDrawBackground(r4)
            org.telegram.ui.Components.voip.VoIPToggleButton r6 = r0.leaveButton
            r6.setTextSize(r9)
            org.telegram.ui.Components.voip.VoIPToggleButton r6 = r0.leaveButton
            r21 = 2131165318(0x7var_, float:1.794485E38)
            r22 = -1
            java.lang.String r8 = "voipgroup_leaveButton"
            int r23 = org.telegram.ui.ActionBar.Theme.getColor(r8)
            r24 = 1050253722(0x3e99999a, float:0.3)
            r25 = 0
            r9 = 2131627995(0x7f0e0fdb, float:1.888327E38)
            java.lang.String r12 = "VoipGroupLeave"
            java.lang.String r26 = org.telegram.messenger.LocaleController.getString(r12, r9)
            r27 = 0
            r28 = 0
            r20 = r6
            r20.setData(r21, r22, r23, r24, r25, r26, r27, r28)
            android.widget.FrameLayout r6 = r0.buttonsContainer
            org.telegram.ui.Components.voip.VoIPToggleButton r9 = r0.leaveButton
            r12 = 1117782016(0x42a00000, float:80.0)
            android.widget.FrameLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createFrame(r13, r12)
            r6.addView(r9, r12)
            org.telegram.ui.Components.voip.VoIPToggleButton r6 = r0.leaveButton
            org.telegram.ui.-$$Lambda$GroupCallActivity$YTy4--O9CNQ8PcDBIYVJUd9VXss r9 = new org.telegram.ui.-$$Lambda$GroupCallActivity$YTy4--O9CNQ8PcDBIYVJUd9VXss
            r9.<init>(r1)
            r6.setOnClickListener(r9)
            org.telegram.ui.GroupCallActivity$9 r6 = new org.telegram.ui.GroupCallActivity$9
            r6.<init>(r1)
            r0.muteButton = r6
            org.telegram.ui.Components.RLottieDrawable r9 = r0.bigMicDrawable
            r6.setAnimation(r9)
            org.telegram.ui.Components.RLottieImageView r6 = r0.muteButton
            android.widget.ImageView$ScaleType r9 = android.widget.ImageView.ScaleType.CENTER
            r6.setScaleType(r9)
            android.widget.FrameLayout r6 = r0.buttonsContainer
            org.telegram.ui.Components.RLottieImageView r9 = r0.muteButton
            r12 = 49
            r13 = 122(0x7a, float:1.71E-43)
            android.widget.FrameLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createFrame(r13, r13, r12)
            r6.addView(r9, r12)
            org.telegram.ui.Components.RLottieImageView r6 = r0.muteButton
            org.telegram.ui.GroupCallActivity$10 r9 = new org.telegram.ui.GroupCallActivity$10
            r9.<init>(r2)
            r6.setOnClickListener(r9)
            org.telegram.ui.Components.RadialProgressView r6 = new org.telegram.ui.Components.RadialProgressView
            r6.<init>(r1)
            r0.radialProgressView = r6
            r9 = 1121714176(0x42dCLASSNAME, float:110.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            r6.setSize(r9)
            org.telegram.ui.Components.RadialProgressView r6 = r0.radialProgressView
            r9 = 1082130432(0x40800000, float:4.0)
            r6.setStrokeWidth(r9)
            org.telegram.ui.Components.RadialProgressView r6 = r0.radialProgressView
            java.lang.String r9 = "voipgroup_connectingProgress"
            int r9 = org.telegram.ui.ActionBar.Theme.getColor(r9)
            r6.setProgressColor(r9)
            r6 = 0
        L_0x03fc:
            r9 = 4
            java.lang.String r12 = "voipgroup_actionBarItems"
            if (r6 >= r5) goto L_0x0488
            android.widget.TextView[] r13 = r0.muteLabel
            android.widget.TextView r14 = new android.widget.TextView
            r14.<init>(r1)
            r13[r6] = r14
            android.widget.TextView[] r13 = r0.muteLabel
            r13 = r13[r6]
            int r14 = org.telegram.ui.ActionBar.Theme.getColor(r12)
            r13.setTextColor(r14)
            android.widget.TextView[] r13 = r0.muteLabel
            r13 = r13[r6]
            r14 = 1099956224(0x41900000, float:18.0)
            r13.setTextSize(r7, r14)
            android.widget.TextView[] r13 = r0.muteLabel
            r13 = r13[r6]
            r13.setGravity(r7)
            android.widget.FrameLayout r13 = r0.buttonsContainer
            android.widget.TextView[] r14 = r0.muteLabel
            r14 = r14[r6]
            r20 = -2
            r21 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r22 = 81
            r23 = 0
            r24 = 0
            r25 = 0
            r26 = 1104150528(0x41d00000, float:26.0)
            android.widget.FrameLayout$LayoutParams r15 = org.telegram.ui.Components.LayoutHelper.createFrame(r20, r21, r22, r23, r24, r25, r26)
            r13.addView(r14, r15)
            android.widget.TextView[] r13 = r0.muteSubLabel
            android.widget.TextView r14 = new android.widget.TextView
            r14.<init>(r1)
            r13[r6] = r14
            android.widget.TextView[] r13 = r0.muteSubLabel
            r13 = r13[r6]
            int r12 = org.telegram.ui.ActionBar.Theme.getColor(r12)
            r13.setTextColor(r12)
            android.widget.TextView[] r12 = r0.muteSubLabel
            r12 = r12[r6]
            r13 = 1094713344(0x41400000, float:12.0)
            r12.setTextSize(r7, r13)
            android.widget.TextView[] r12 = r0.muteSubLabel
            r12 = r12[r6]
            r12.setGravity(r7)
            android.widget.FrameLayout r12 = r0.buttonsContainer
            android.widget.TextView[] r13 = r0.muteSubLabel
            r13 = r13[r6]
            r26 = 1092616192(0x41200000, float:10.0)
            android.widget.FrameLayout$LayoutParams r14 = org.telegram.ui.Components.LayoutHelper.createFrame(r20, r21, r22, r23, r24, r25, r26)
            r12.addView(r13, r14)
            if (r6 != r7) goto L_0x0484
            android.widget.TextView[] r12 = r0.muteLabel
            r12 = r12[r6]
            r12.setVisibility(r9)
            android.widget.TextView[] r12 = r0.muteSubLabel
            r12 = r12[r6]
            r12.setVisibility(r9)
        L_0x0484:
            int r6 = r6 + 1
            goto L_0x03fc
        L_0x0488:
            org.telegram.ui.GroupCallActivity$11 r6 = new org.telegram.ui.GroupCallActivity$11
            r6.<init>(r1)
            r0.actionBar = r6
            r13 = 2131165468(0x7var_c, float:1.7945154E38)
            r6.setBackButtonImage(r13)
            org.telegram.ui.ActionBar.ActionBar r6 = r0.actionBar
            r6.setOccupyStatusBar(r4)
            org.telegram.ui.ActionBar.ActionBar r6 = r0.actionBar
            r6.setAllowOverlayTitle(r4)
            org.telegram.ui.ActionBar.ActionBar r6 = r0.actionBar
            int r13 = org.telegram.ui.ActionBar.Theme.getColor(r12)
            r6.setItemsColor(r13, r4)
            org.telegram.ui.ActionBar.ActionBar r6 = r0.actionBar
            java.lang.String r13 = "actionBarActionModeDefaultSelector"
            int r13 = org.telegram.ui.ActionBar.Theme.getColor(r13)
            r6.setItemsBackgroundColor(r13, r4)
            org.telegram.ui.ActionBar.ActionBar r6 = r0.actionBar
            int r13 = org.telegram.ui.ActionBar.Theme.getColor(r12)
            r6.setTitleColor(r13)
            org.telegram.ui.ActionBar.ActionBar r6 = r0.actionBar
            java.lang.String r13 = "voipgroup_lastSeenTextUnscrolled"
            int r13 = org.telegram.ui.ActionBar.Theme.getColor(r13)
            r6.setSubtitleColor(r13)
            org.telegram.ui.ActionBar.ActionBar r6 = r0.actionBar
            org.telegram.ui.GroupCallActivity$12 r13 = new org.telegram.ui.GroupCallActivity$12
            r13.<init>(r2)
            r6.setActionBarMenuOnItemClick(r13)
            org.telegram.ui.ActionBar.ActionBar r6 = r0.actionBar
            r13 = 0
            r6.setAlpha(r13)
            org.telegram.ui.ActionBar.ActionBar r6 = r0.actionBar
            android.widget.ImageView r6 = r6.getBackButton()
            r14 = 1063675494(0x3var_, float:0.9)
            r6.setScaleX(r14)
            org.telegram.ui.ActionBar.ActionBar r6 = r0.actionBar
            android.widget.ImageView r6 = r6.getBackButton()
            r6.setScaleY(r14)
            org.telegram.ui.ActionBar.ActionBar r6 = r0.actionBar
            android.widget.ImageView r6 = r6.getBackButton()
            r14 = 1096810496(0x41600000, float:14.0)
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r14)
            int r14 = -r14
            float r14 = (float) r14
            r6.setTranslationX(r14)
            org.telegram.ui.ActionBar.ActionBar r6 = r0.actionBar
            org.telegram.ui.ActionBar.SimpleTextView r6 = r6.getTitleTextView()
            r14 = 1102577664(0x41b80000, float:23.0)
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r14)
            float r14 = (float) r14
            r6.setTranslationY(r14)
            org.telegram.ui.ActionBar.ActionBar r6 = r0.actionBar
            org.telegram.ui.ActionBar.SimpleTextView r6 = r6.getSubtitleTextView()
            r14 = 1101004800(0x41a00000, float:20.0)
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r14)
            float r14 = (float) r14
            r6.setTranslationY(r14)
            org.telegram.ui.Components.AvatarDrawable r6 = new org.telegram.ui.Components.AvatarDrawable
            r6.<init>()
            r0.accountSwitchAvatarDrawable = r6
            r14 = 1094713344(0x41400000, float:12.0)
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r14)
            r6.setTextSize(r14)
            org.telegram.ui.Components.BackupImageView r6 = new org.telegram.ui.Components.BackupImageView
            r6.<init>(r1)
            r0.accountSwitchImageView = r6
            r14 = 1098907648(0x41800000, float:16.0)
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r14)
            r6.setRoundRadius(r14)
            org.telegram.ui.Components.BackupImageView r6 = r0.accountSwitchImageView
            org.telegram.ui.-$$Lambda$GroupCallActivity$3KK94yZJyte7OeygsSaDprXlKGs r14 = new org.telegram.ui.-$$Lambda$GroupCallActivity$3KK94yZJyte7OeygsSaDprXlKGs
            r14.<init>(r3, r2)
            r6.setOnClickListener(r14)
            org.telegram.ui.ActionBar.ActionBarMenuItem r3 = new org.telegram.ui.ActionBar.ActionBarMenuItem
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r12)
            r3.<init>(r1, r10, r4, r6)
            r0.otherItem = r3
            r3.setLongClickEnabled(r4)
            org.telegram.ui.ActionBar.ActionBarMenuItem r3 = r0.otherItem
            r6 = 2131165475(0x7var_, float:1.7945168E38)
            r3.setIcon((int) r6)
            org.telegram.ui.ActionBar.ActionBarMenuItem r3 = r0.otherItem
            r6 = 2131623986(0x7f0e0032, float:1.8875139E38)
            java.lang.String r14 = "AccDescrMoreOptions"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r14, r6)
            r3.setContentDescription(r6)
            org.telegram.ui.ActionBar.ActionBarMenuItem r3 = r0.otherItem
            r3.setSubMenuOpenSide(r5)
            org.telegram.ui.ActionBar.ActionBarMenuItem r3 = r0.otherItem
            org.telegram.ui.-$$Lambda$GroupCallActivity$aBk-hYHKoVa_nLXwC-R62pAurG4 r6 = new org.telegram.ui.-$$Lambda$GroupCallActivity$aBk-hYHKoVa_nLXwC-R62pAurG4
            r6.<init>()
            r3.setDelegate(r6)
            org.telegram.ui.ActionBar.ActionBarMenuItem r3 = r0.otherItem
            java.lang.String r6 = "voipgroup_actionBarItemsSelector"
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r6)
            r14 = 6
            android.graphics.drawable.Drawable r6 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r6, r14)
            r3.setBackgroundDrawable(r6)
            org.telegram.ui.ActionBar.ActionBarMenuItem r3 = r0.otherItem
            org.telegram.ui.-$$Lambda$GroupCallActivity$WLqTe3X4_VbgdQpD_Vl2EGXCV-Q r6 = new org.telegram.ui.-$$Lambda$GroupCallActivity$WLqTe3X4_VbgdQpD_Vl2EGXCV-Q
            r6.<init>(r2)
            r3.setOnClickListener(r6)
            org.telegram.ui.ActionBar.ActionBarMenuItem r3 = r0.otherItem
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r12)
            r3.setPopupItemsColor(r6, r4)
            org.telegram.ui.ActionBar.ActionBarMenuItem r3 = r0.otherItem
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r12)
            r3.setPopupItemsColor(r6, r7)
            org.telegram.ui.ActionBar.ActionBarMenuItem r3 = new org.telegram.ui.ActionBar.ActionBarMenuItem
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r12)
            r3.<init>(r1, r10, r4, r6)
            r0.pipItem = r3
            r3.setLongClickEnabled(r4)
            org.telegram.ui.ActionBar.ActionBarMenuItem r3 = r0.pipItem
            r6 = 2131165839(0x7var_f, float:1.7945906E38)
            r3.setIcon((int) r6)
            org.telegram.ui.ActionBar.ActionBarMenuItem r3 = r0.pipItem
            r6 = 2131624010(0x7f0e004a, float:1.8875188E38)
            java.lang.String r10 = "AccDescrPipMode"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r10, r6)
            r3.setContentDescription(r6)
            org.telegram.ui.ActionBar.ActionBarMenuItem r3 = r0.pipItem
            java.lang.String r6 = "voipgroup_actionBarItemsSelector"
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r6)
            android.graphics.drawable.Drawable r6 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r6, r14)
            r3.setBackgroundDrawable(r6)
            org.telegram.ui.ActionBar.ActionBarMenuItem r3 = r0.pipItem
            org.telegram.ui.-$$Lambda$GroupCallActivity$mDr651-dTAYHac5QlYQ2Or1J0F0 r6 = new org.telegram.ui.-$$Lambda$GroupCallActivity$mDr651-dTAYHac5QlYQ2Or1J0F0
            r6.<init>()
            r3.setOnClickListener(r6)
            org.telegram.ui.GroupCallActivity$13 r3 = new org.telegram.ui.GroupCallActivity$13
            r3.<init>(r1, r1, r2)
            r0.titleTextView = r3
            org.telegram.ui.GroupCallActivity$14 r3 = new org.telegram.ui.GroupCallActivity$14
            r3.<init>(r0, r1)
            r0.actionBarBackground = r3
            r3.setAlpha(r13)
            android.view.ViewGroup r3 = r0.containerView
            android.view.View r6 = r0.actionBarBackground
            r20 = -1
            r21 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r22 = 51
            r23 = 0
            r24 = 0
            r25 = 0
            r26 = 0
            android.widget.FrameLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createFrame(r20, r21, r22, r23, r24, r25, r26)
            r3.addView(r6, r10)
            android.view.ViewGroup r3 = r0.containerView
            org.telegram.ui.Components.AudioPlayerAlert$ClippingTextViewSwitcher r6 = r0.titleTextView
            r20 = -2
            r23 = 1102577664(0x41b80000, float:23.0)
            r25 = 1111490560(0x42400000, float:48.0)
            android.widget.FrameLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createFrame(r20, r21, r22, r23, r24, r25, r26)
            r3.addView(r6, r10)
            android.view.ViewGroup r3 = r0.containerView
            org.telegram.ui.ActionBar.ActionBar r6 = r0.actionBar
            r20 = -1
            r23 = 0
            r25 = 0
            android.widget.FrameLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createFrame(r20, r21, r22, r23, r24, r25, r26)
            r3.addView(r6, r10)
            android.widget.LinearLayout r3 = new android.widget.LinearLayout
            r3.<init>(r1)
            r0.menuItemsContainer = r3
            r3.setOrientation(r4)
            org.telegram.ui.ActionBar.ActionBarMenuItem r6 = r0.pipItem
            r10 = 48
            android.widget.LinearLayout$LayoutParams r15 = org.telegram.ui.Components.LayoutHelper.createLinear(r10, r10)
            r3.addView(r6, r15)
            org.telegram.ui.ActionBar.ActionBarMenuItem r6 = r0.otherItem
            android.widget.LinearLayout$LayoutParams r15 = org.telegram.ui.Components.LayoutHelper.createLinear(r10, r10)
            r3.addView(r6, r15)
            org.telegram.ui.Components.BackupImageView r6 = r0.accountSwitchImageView
            r20 = 32
            r21 = 32
            r22 = 16
            r23 = 2
            r24 = 0
            r25 = 12
            r26 = 0
            android.widget.LinearLayout$LayoutParams r15 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r20, (int) r21, (int) r22, (int) r23, (int) r24, (int) r25, (int) r26)
            r3.addView(r6, r15)
            android.view.ViewGroup r6 = r0.containerView
            r15 = -2
            r9 = 53
            android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r15, r10, r9)
            r6.addView(r3, r9)
            android.view.View r3 = new android.view.View
            r3.<init>(r1)
            r0.actionBarShadow = r3
            r3.setAlpha(r13)
            android.view.View r3 = r0.actionBarShadow
            java.lang.String r6 = "dialogShadowLine"
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r6)
            r3.setBackgroundColor(r6)
            android.view.ViewGroup r3 = r0.containerView
            android.view.View r6 = r0.actionBarShadow
            r9 = 1065353216(0x3var_, float:1.0)
            r10 = -1
            android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r10, r9)
            r3.addView(r6, r9)
            r3 = 0
        L_0x0697:
            if (r3 >= r5) goto L_0x06e2
            org.telegram.ui.Components.UndoView[] r6 = r0.undoView
            org.telegram.ui.Components.UndoView r9 = new org.telegram.ui.Components.UndoView
            r9.<init>(r1)
            r6[r3] = r9
            org.telegram.ui.Components.UndoView[] r6 = r0.undoView
            r6 = r6[r3]
            r9 = 1092616192(0x41200000, float:10.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            float r9 = (float) r9
            r6.setAdditionalTranslationY(r9)
            int r6 = android.os.Build.VERSION.SDK_INT
            r9 = 21
            if (r6 < r9) goto L_0x06c4
            org.telegram.ui.Components.UndoView[] r6 = r0.undoView
            r6 = r6[r3]
            r9 = 1084227584(0x40a00000, float:5.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            float r9 = (float) r9
            r6.setTranslationZ(r9)
        L_0x06c4:
            android.view.ViewGroup r6 = r0.containerView
            org.telegram.ui.Components.UndoView[] r9 = r0.undoView
            r9 = r9[r3]
            r20 = -1
            r21 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r22 = 83
            r23 = 1090519040(0x41000000, float:8.0)
            r24 = 0
            r25 = 1090519040(0x41000000, float:8.0)
            r26 = 1090519040(0x41000000, float:8.0)
            android.widget.FrameLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createFrame(r20, r21, r22, r23, r24, r25, r26)
            r6.addView(r9, r10)
            int r3 = r3 + 1
            goto L_0x0697
        L_0x06e2:
            org.telegram.ui.Cells.AccountSelectCell r3 = new org.telegram.ui.Cells.AccountSelectCell
            r3.<init>(r1, r7)
            r0.accountSelectCell = r3
            r6 = 2131230938(0x7var_da, float:1.8077943E38)
            r9 = 240(0xf0, float:3.36E-43)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            r3.setTag(r6, r9)
            org.telegram.ui.ActionBar.ActionBarMenuItem r3 = r0.otherItem
            r6 = 8
            org.telegram.ui.Cells.AccountSelectCell r9 = r0.accountSelectCell
            r10 = -2
            r15 = 1111490560(0x42400000, float:48.0)
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r15)
            r3.addSubItem((int) r6, (android.view.View) r9, (int) r10, (int) r15)
            org.telegram.ui.Cells.AccountSelectCell r3 = r0.accountSelectCell
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r11)
            android.graphics.drawable.Drawable r6 = org.telegram.ui.ActionBar.Theme.createRadSelectorDrawable(r6, r14, r14)
            r3.setBackground(r6)
            org.telegram.ui.ActionBar.ActionBarMenuItem r3 = r0.otherItem
            android.view.View r3 = r3.addGap(r4)
            r0.accountGap = r3
            org.telegram.ui.ActionBar.ActionBarMenuItem r3 = r0.otherItem
            r6 = 2131627962(0x7f0e0fba, float:1.8883203E38)
            java.lang.String r9 = "VoipGroupAllCanSpeak"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r9, r6)
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r3 = r3.addSubItem((int) r7, (int) r4, (java.lang.CharSequence) r6, (boolean) r7)
            r0.everyoneItem = r3
            r3.updateSelectorBackground(r7, r4)
            org.telegram.ui.ActionBar.ActionBarMenuItem r3 = r0.otherItem
            r6 = 2131628006(0x7f0e0fe6, float:1.8883292E38)
            java.lang.String r9 = "VoipGroupOnlyAdminsCanSpeak"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r9, r6)
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r3 = r3.addSubItem((int) r5, (int) r4, (java.lang.CharSequence) r6, (boolean) r7)
            r0.adminItem = r3
            r3.updateSelectorBackground(r4, r7)
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r3 = r0.everyoneItem
            java.lang.String r6 = "voipgroup_checkMenu"
            int r9 = org.telegram.ui.ActionBar.Theme.getColor(r6)
            r3.setCheckColor(r9)
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r3 = r0.everyoneItem
            int r9 = org.telegram.ui.ActionBar.Theme.getColor(r6)
            int r10 = org.telegram.ui.ActionBar.Theme.getColor(r6)
            r3.setColors(r9, r10)
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r3 = r0.adminItem
            int r9 = org.telegram.ui.ActionBar.Theme.getColor(r6)
            r3.setCheckColor(r9)
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r3 = r0.adminItem
            int r9 = org.telegram.ui.ActionBar.Theme.getColor(r6)
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r6)
            r3.setColors(r9, r6)
            org.telegram.ui.ActionBar.ActionBarMenuItem r3 = r0.otherItem
            r21 = 6
            r22 = 2131165739(0x7var_b, float:1.7945704E38)
            org.telegram.ui.GroupCallActivity$RecordCallDrawable r6 = r0.recordCallDrawable
            r9 = 2131627975(0x7f0e0fc7, float:1.888323E38)
            java.lang.String r10 = "VoipGroupEditTitle"
            java.lang.String r24 = org.telegram.messenger.LocaleController.getString(r10, r9)
            r25 = 1
            r26 = 0
            r20 = r3
            r23 = r6
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r3 = r20.addSubItem(r21, r22, r23, r24, r25, r26)
            r0.editTitleItem = r3
            org.telegram.ui.ActionBar.ActionBarMenuItem r3 = r0.otherItem
            r21 = 7
            r22 = 2131165781(0x7var_, float:1.7945789E38)
            org.telegram.ui.GroupCallActivity$RecordCallDrawable r6 = r0.recordCallDrawable
            r9 = 2131627974(0x7f0e0fc6, float:1.8883228E38)
            java.lang.String r10 = "VoipGroupEditPermissions"
            java.lang.String r24 = org.telegram.messenger.LocaleController.getString(r10, r9)
            r25 = 0
            r20 = r3
            r23 = r6
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r3 = r20.addSubItem(r21, r22, r23, r24, r25, r26)
            r0.permissionItem = r3
            org.telegram.ui.ActionBar.ActionBarMenuItem r3 = r0.otherItem
            r6 = 2131165756(0x7var_c, float:1.7945738E38)
            r9 = 2131628021(0x7f0e0ff5, float:1.8883323E38)
            java.lang.String r10 = "VoipGroupShareInviteLink"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r10, r9)
            r10 = 3
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r3 = r3.addSubItem(r10, r6, r9)
            r0.inviteItem = r3
            org.telegram.ui.GroupCallActivity$RecordCallDrawable r3 = new org.telegram.ui.GroupCallActivity$RecordCallDrawable
            r3.<init>()
            r0.recordCallDrawable = r3
            org.telegram.ui.ActionBar.ActionBarMenuItem r6 = r0.otherItem
            r19 = 5
            r20 = 0
            r9 = 2131628013(0x7f0e0fed, float:1.8883307E38)
            java.lang.String r10 = "VoipGroupRecordCall"
            java.lang.String r22 = org.telegram.messenger.LocaleController.getString(r10, r9)
            r23 = 1
            r24 = 0
            r18 = r6
            r21 = r3
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r3 = r18.addSubItem(r19, r20, r21, r22, r23, r24)
            r0.recordItem = r3
            org.telegram.ui.GroupCallActivity$RecordCallDrawable r6 = r0.recordCallDrawable
            android.widget.ImageView r3 = r3.getImageView()
            r6.setParentView(r3)
            org.telegram.ui.ActionBar.ActionBarMenuItem r3 = r0.otherItem
            r6 = 2131165740(0x7var_c, float:1.7945706E38)
            r9 = 2131627979(0x7f0e0fcb, float:1.8883238E38)
            java.lang.String r10 = "VoipGroupEndChat"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r10, r9)
            r10 = 4
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r3 = r3.addSubItem(r10, r6, r9)
            r0.leaveItem = r3
            org.telegram.ui.ActionBar.ActionBarMenuItem r3 = r0.otherItem
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r11)
            r3.setPopupItemsSelectorColor(r6)
            org.telegram.ui.ActionBar.ActionBarMenuItem r3 = r0.otherItem
            org.telegram.ui.ActionBar.ActionBarPopupWindow$ActionBarPopupWindowLayout r3 = r3.getPopupLayout()
            r3.setFitItems(r7)
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r3 = r0.leaveItem
            java.lang.String r6 = "voipgroup_leaveCallMenu"
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r6)
            java.lang.String r7 = "voipgroup_leaveCallMenu"
            int r7 = org.telegram.ui.ActionBar.Theme.getColor(r7)
            r3.setColors(r6, r7)
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r3 = r0.inviteItem
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r12)
            int r7 = org.telegram.ui.ActionBar.Theme.getColor(r12)
            r3.setColors(r6, r7)
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r3 = r0.editTitleItem
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r12)
            int r7 = org.telegram.ui.ActionBar.Theme.getColor(r12)
            r3.setColors(r6, r7)
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r3 = r0.permissionItem
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r12)
            int r7 = org.telegram.ui.ActionBar.Theme.getColor(r12)
            r3.setColors(r6, r7)
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r3 = r0.recordItem
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r12)
            int r7 = org.telegram.ui.ActionBar.Theme.getColor(r12)
            r3.setColors(r6, r7)
            org.telegram.ui.GroupCallActivity$ListAdapter r3 = r0.listAdapter
            r3.notifyDataSetChanged()
            org.telegram.ui.GroupCallActivity$ListAdapter r3 = r0.listAdapter
            int r3 = r3.getItemCount()
            r0.oldCount = r3
            org.telegram.ui.ActionBar.ActionBar r3 = r0.actionBar
            org.telegram.tgnet.TLRPC$GroupCall r6 = r2.call
            int r6 = r6.participants_count
            org.telegram.ui.GroupCallActivity$ListAdapter r7 = r0.listAdapter
            boolean r7 = r7.addSelfToCounter()
            int r6 = r6 + r7
            java.lang.String r7 = "Participants"
            java.lang.String r6 = org.telegram.messenger.LocaleController.formatPluralString(r7, r6)
            r3.setSubtitle(r6)
            org.telegram.ui.ActionBar.ActionBar r3 = r0.actionBar
            r6 = 1111490560(0x42400000, float:48.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r6 = r6 * 2
            r3.setTitleRightMargin(r6)
            r32.saveActiveDates()
            org.telegram.messenger.voip.VoIPService r3 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            r3.registerStateListener(r0)
            r29.updateItems()
            r0.updateSpeakerPhoneIcon(r4)
            r0.updateState(r4, r4)
            r0.setColorProgress(r13)
            android.graphics.Paint r3 = r0.leaveBackgroundPaint
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r8)
            r3.setColor(r5)
            r0.updateTitle(r4)
            org.telegram.ui.ActionBar.ActionBar r3 = r0.actionBar
            org.telegram.ui.ActionBar.SimpleTextView r3 = r3.getTitleTextView()
            org.telegram.ui.-$$Lambda$GroupCallActivity$ssYXzmaOJ3p3A4b-fJwJh7Xk7hU r5 = new org.telegram.ui.-$$Lambda$GroupCallActivity$ssYXzmaOJ3p3A4b-fJwJh7Xk7hU
            r5.<init>(r2)
            r3.setOnClickListener(r5)
            org.telegram.ui.AvatarPreviewPagerIndicator r2 = new org.telegram.ui.AvatarPreviewPagerIndicator
            r2.<init>(r1)
            r0.avatarPagerIndicator = r2
            org.telegram.ui.Components.ProfileGalleryView r3 = new org.telegram.ui.Components.ProfileGalleryView
            org.telegram.ui.ActionBar.ActionBar r5 = r0.actionBar
            org.telegram.ui.Components.RecyclerListView r6 = r0.listView
            r3.<init>(r1, r5, r6, r2)
            r0.avatarsViewPager = r3
            r2.setProfileGalleryView(r3)
            org.telegram.ui.GroupCallActivity$15 r5 = new org.telegram.ui.GroupCallActivity$15
            r5.<init>(r1)
            r0.avatarPreviewContainer = r5
            r6 = 8
            r5.setVisibility(r6)
            r3.setVisibility(r4)
            org.telegram.ui.GroupCallActivity$16 r4 = new org.telegram.ui.GroupCallActivity$16
            r4.<init>()
            r3.addOnPageChangeListener(r4)
            android.view.View r4 = new android.view.View
            r4.<init>(r1)
            r0.blurredView = r4
            android.view.ViewGroup r1 = r0.containerView
            r1.addView(r4)
            r1 = -1082130432(0xffffffffbvar_, float:-1.0)
            r4 = -1
            android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame(r4, r1)
            r5.addView(r3, r1)
            r6 = -1
            r7 = -1082130432(0xffffffffbvar_, float:-1.0)
            r8 = 0
            r9 = 0
            r10 = 0
            r11 = 0
            r12 = 0
            android.widget.FrameLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createFrame(r6, r7, r8, r9, r10, r11, r12)
            r5.addView(r2, r1)
            android.view.ViewGroup r1 = r0.containerView
            r9 = 1096810496(0x41600000, float:14.0)
            r10 = 1096810496(0x41600000, float:14.0)
            r11 = 1096810496(0x41600000, float:14.0)
            r12 = 1096810496(0x41600000, float:14.0)
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createFrame(r6, r7, r8, r9, r10, r11, r12)
            r1.addView(r5, r2)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.GroupCallActivity.<init>(android.content.Context, org.telegram.messenger.AccountInstance, org.telegram.messenger.ChatObject$Call, org.telegram.tgnet.TLRPC$Chat):void");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$8 */
    public /* synthetic */ void lambda$new$8$GroupCallActivity(DialogInterface dialogInterface) {
        BaseFragment baseFragment = this.parentActivity.getActionBarLayout().fragmentsStack.get(this.parentActivity.getActionBarLayout().fragmentsStack.size() - 1);
        if (this.anyEnterEventSent && (baseFragment instanceof ChatActivity)) {
            ((ChatActivity) baseFragment).onEditTextDialogClose(true);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$9 */
    public /* synthetic */ void lambda$new$9$GroupCallActivity(ChatObject.Call call2, int[] iArr, float[] fArr, boolean[] zArr) {
        RecyclerView.ViewHolder findViewHolderForAdapterPosition;
        for (int i = 0; i < iArr.length; i++) {
            TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant = call2.participantsBySources.get(iArr[i]);
            if (tLRPC$TL_groupCallParticipant != null) {
                int indexOf = (this.delayedGroupCallUpdated ? this.oldParticipants : call2.sortedParticipants).indexOf(tLRPC$TL_groupCallParticipant);
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
    /* renamed from: lambda$new$11 */
    public /* synthetic */ void lambda$new$11$GroupCallActivity(ChatObject.Call call2, View view, int i, float f, float f2) {
        if (view instanceof GroupCallUserCell) {
            GroupCallUserCell groupCallUserCell = (GroupCallUserCell) view;
            if (!groupCallUserCell.isSelfUser() || groupCallUserCell.isHandRaised()) {
                showMenuForCell(groupCallUserCell);
            }
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
                GroupVoipInviteAlert groupVoipInviteAlert2 = new GroupVoipInviteAlert(getContext(), this.accountInstance.getCurrentAccount(), this.currentChat, chatFull, call2.participants, call2.invitedUsersMap);
                this.groupVoipInviteAlert = groupVoipInviteAlert2;
                groupVoipInviteAlert2.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    public final void onDismiss(DialogInterface dialogInterface) {
                        GroupCallActivity.this.lambda$null$10$GroupCallActivity(dialogInterface);
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
    /* renamed from: lambda$null$10 */
    public /* synthetic */ void lambda$null$10$GroupCallActivity(DialogInterface dialogInterface) {
        this.groupVoipInviteAlert = null;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$12 */
    public /* synthetic */ boolean lambda$new$12$GroupCallActivity(View view, int i) {
        if (!(view instanceof GroupCallUserCell)) {
            return false;
        }
        updateItems();
        return ((GroupCallUserCell) view).clickMuteButton();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$13 */
    public /* synthetic */ void lambda$new$13$GroupCallActivity(View view) {
        if (VoIPService.getSharedInstance() != null) {
            VoIPService.getSharedInstance().toggleSpeakerphoneOrShowRouteSheet(getContext(), false);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$14 */
    public /* synthetic */ void lambda$new$14$GroupCallActivity(Context context, View view) {
        updateItems();
        onLeaveClick(context, new Runnable() {
            public final void run() {
                GroupCallActivity.this.dismiss();
            }
        }, false);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$16 */
    public /* synthetic */ void lambda$new$16$GroupCallActivity(TLRPC$Chat tLRPC$Chat, ChatObject.Call call2, View view) {
        JoinCallAlert.open(getContext(), -tLRPC$Chat.id, this.accountInstance, (BaseFragment) null, 2, new JoinCallAlert.JoinCallAlertDelegate(call2) {
            public final /* synthetic */ ChatObject.Call f$1;

            {
                this.f$1 = r2;
            }

            public final void didSelectChat(TLRPC$InputPeer tLRPC$InputPeer, boolean z) {
                GroupCallActivity.this.lambda$null$15$GroupCallActivity(this.f$1, tLRPC$InputPeer, z);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$15 */
    public /* synthetic */ void lambda$null$15$GroupCallActivity(ChatObject.Call call2, TLRPC$InputPeer tLRPC$InputPeer, boolean z) {
        if (VoIPService.getSharedInstance() != null && z) {
            TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant = call2.participants.get(MessageObject.getPeerId(this.selfPeer));
            VoIPService.getSharedInstance().setGroupCallPeer(tLRPC$InputPeer);
            if (tLRPC$InputPeer instanceof TLRPC$TL_inputPeerUser) {
                this.userSwitchObject = this.accountInstance.getMessagesController().getUser(Integer.valueOf(tLRPC$InputPeer.user_id));
            } else if (tLRPC$InputPeer instanceof TLRPC$TL_inputPeerChat) {
                this.userSwitchObject = this.accountInstance.getMessagesController().getChat(Integer.valueOf(tLRPC$InputPeer.chat_id));
            } else {
                this.userSwitchObject = this.accountInstance.getMessagesController().getChat(Integer.valueOf(tLRPC$InputPeer.channel_id));
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$17 */
    public /* synthetic */ void lambda$new$17$GroupCallActivity(int i) {
        this.actionBar.getActionBarMenuOnItemClick().onItemClick(i);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$18 */
    public /* synthetic */ void lambda$new$18$GroupCallActivity(ChatObject.Call call2, View view) {
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$19 */
    public /* synthetic */ void lambda$new$19$GroupCallActivity(View view) {
        if (Build.VERSION.SDK_INT < 23 || Settings.canDrawOverlays(this.parentActivity)) {
            GroupCallPip.clearForce();
            dismiss();
            return;
        }
        AlertsCreator.createDrawOverlayGroupCallPermissionDialog(getContext()).show();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$20 */
    public /* synthetic */ void lambda$new$20$GroupCallActivity(ChatObject.Call call2, View view) {
        if (call2.recording) {
            showRecordHint(this.actionBar.getTitleTextView());
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
        if (!TextUtils.isEmpty(this.call.call.title)) {
            if (!this.call.call.title.equals(this.actionBar.getTitle())) {
                if (z) {
                    this.actionBar.setTitleAnimated(this.call.call.title, true, 180);
                    this.actionBar.getTitleTextView().setOnClickListener(new View.OnClickListener() {
                        public final void onClick(View view) {
                            GroupCallActivity.this.lambda$updateTitle$21$GroupCallActivity(view);
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
                        GroupCallActivity.this.lambda$updateTitle$22$GroupCallActivity(view);
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
    /* renamed from: lambda$updateTitle$21 */
    public /* synthetic */ void lambda$updateTitle$21$GroupCallActivity(View view) {
        showRecordHint(this.actionBar.getTitleTextView());
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$updateTitle$22 */
    public /* synthetic */ void lambda$updateTitle$22$GroupCallActivity(View view) {
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
        if (i == 3 || i == 2 || i == 4) {
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
        if (chat == null || !TextUtils.isEmpty(chat.username)) {
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
                        GroupCallActivity.this.lambda$getLink$26$GroupCallActivity(this.f$1, this.f$2, tLObject, tLRPC$TL_error);
                    }
                });
                i++;
            }
            return;
        }
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
                    GroupCallActivity.this.lambda$getLink$24$GroupCallActivity(this.f$1, this.f$2, tLObject, tLRPC$TL_error);
                }
            });
            return;
        }
        openShareAlert(true, (String) null, str, z);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$getLink$24 */
    public /* synthetic */ void lambda$getLink$24$GroupCallActivity(TLRPC$ChatFull tLRPC$ChatFull, boolean z, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
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
                GroupCallActivity.this.lambda$null$23$GroupCallActivity(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$23 */
    public /* synthetic */ void lambda$null$23$GroupCallActivity(TLObject tLObject, TLRPC$ChatFull tLRPC$ChatFull, boolean z) {
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
    /* renamed from: lambda$getLink$26 */
    public /* synthetic */ void lambda$getLink$26$GroupCallActivity(int i, boolean z, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
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
                GroupCallActivity.this.lambda$null$25$GroupCallActivity(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$25 */
    public /* synthetic */ void lambda$null$25$GroupCallActivity(TLObject tLObject, int i, boolean z) {
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
        openShareAlert(false, strArr2[0], strArr2[1], z);
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
            r1 = 2131627985(0x7f0e0fd1, float:1.888325E38)
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r2[r0] = r8
            java.lang.String r0 = "VoipGroupInviteText"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            r5 = r0
            goto L_0x006c
        L_0x006b:
            r5 = r8
        L_0x006c:
            org.telegram.ui.GroupCallActivity$17 r14 = new org.telegram.ui.GroupCallActivity$17
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
            org.telegram.ui.GroupCallActivity$18 r0 = new org.telegram.ui.GroupCallActivity$18
            r0.<init>()
            r14.setDelegate(r0)
            org.telegram.ui.Components.ShareAlert r0 = r12.shareAlert
            org.telegram.ui.-$$Lambda$GroupCallActivity$4P5OebN96dYOfVFmwDnaeRLnvar_ r1 = new org.telegram.ui.-$$Lambda$GroupCallActivity$4P5OebN96dYOfVFmwDnaeRLnvar_
            r1.<init>()
            r0.setOnDismissListener(r1)
            org.telegram.ui.-$$Lambda$GroupCallActivity$F_526cDqOi7r6sgk-CmtXGXrNPA r0 = new org.telegram.ui.-$$Lambda$GroupCallActivity$F_526cDqOi7r6sgk-CmtXGXrNPA
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
    /* renamed from: lambda$openShareAlert$27 */
    public /* synthetic */ void lambda$openShareAlert$27$GroupCallActivity(DialogInterface dialogInterface) {
        this.shareAlert = null;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$openShareAlert$28 */
    public /* synthetic */ void lambda$openShareAlert$28$GroupCallActivity() {
        ShareAlert shareAlert2 = this.shareAlert;
        if (shareAlert2 != null) {
            shareAlert2.show();
        }
    }

    /* access modifiers changed from: private */
    public void inviteUserToCall(int i, boolean z) {
        TLRPC$User user = this.accountInstance.getMessagesController().getUser(Integer.valueOf(i));
        if (user != null) {
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
                    GroupCallActivity.this.lambda$inviteUserToCall$31$GroupCallActivity(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, tLObject, tLRPC$TL_error);
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
                        GroupCallActivity.this.lambda$inviteUserToCall$33$GroupCallActivity(this.f$1, this.f$2);
                    }
                }, 500);
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$inviteUserToCall$31 */
    public /* synthetic */ void lambda$inviteUserToCall$31$GroupCallActivity(int i, AlertDialog[] alertDialogArr, TLRPC$User tLRPC$User, boolean z, TLRPC$TL_phone_inviteToGroupCall tLRPC$TL_phone_inviteToGroupCall, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
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
                    GroupCallActivity.this.lambda$null$29$GroupCallActivity(this.f$1, this.f$2, this.f$3);
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
                GroupCallActivity.this.lambda$null$30$GroupCallActivity(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$29 */
    public /* synthetic */ void lambda$null$29$GroupCallActivity(int i, AlertDialog[] alertDialogArr, TLRPC$User tLRPC$User) {
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
    /* renamed from: lambda$null$30 */
    public /* synthetic */ void lambda$null$30$GroupCallActivity(AlertDialog[] alertDialogArr, boolean z, TLRPC$TL_error tLRPC$TL_error, int i, TLRPC$TL_phone_inviteToGroupCall tLRPC$TL_phone_inviteToGroupCall) {
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
    /* renamed from: lambda$inviteUserToCall$33 */
    public /* synthetic */ void lambda$inviteUserToCall$33$GroupCallActivity(AlertDialog[] alertDialogArr, int i) {
        if (alertDialogArr[0] != null) {
            alertDialogArr[0].setOnCancelListener(new DialogInterface.OnCancelListener(i) {
                public final /* synthetic */ int f$1;

                {
                    this.f$1 = r2;
                }

                public final void onCancel(DialogInterface dialogInterface) {
                    GroupCallActivity.this.lambda$null$32$GroupCallActivity(this.f$1, dialogInterface);
                }
            });
            alertDialogArr[0].show();
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$32 */
    public /* synthetic */ void lambda$null$32$GroupCallActivity(int i, DialogInterface dialogInterface) {
        this.accountInstance.getConnectionsManager().cancelRequest(i, true);
    }

    /* access modifiers changed from: private */
    public void updateLayout(boolean z) {
        float f;
        float f2;
        float f3;
        if (this.listView.getChildCount() <= 0) {
            RecyclerListView recyclerListView = this.listView;
            float paddingTop = (float) recyclerListView.getPaddingTop();
            this.scrollOffsetY = paddingTop;
            recyclerListView.setTopGlowOffset((int) paddingTop);
            this.containerView.invalidate();
            return;
        }
        int childCount = this.listView.getChildCount();
        int i = 0;
        float f4 = 2.14748365E9f;
        for (int i2 = 0; i2 < childCount; i2++) {
            f4 = Math.min(f4, this.itemAnimator.getTargetY(this.listView.getChildAt(i2)));
        }
        if (f4 < 0.0f || f4 == 2.14748365E9f) {
            f4 = 0.0f;
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
            RecyclerListView recyclerListView2 = this.listView;
            this.scrollOffsetY = f5;
            recyclerListView2.setTopGlowOffset((int) (f5 - ((float) i3)));
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
        VoIPService sharedInstance = VoIPService.getSharedInstance();
        if (sharedInstance != null) {
            if (sharedInstance.isSwitchingStream() || !((i = this.currentCallState) == 1 || i == 2 || i == 6 || i == 5)) {
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
        VoIPService sharedInstance;
        if (this.soundButton != null && (sharedInstance = VoIPService.getSharedInstance()) != null) {
            boolean z2 = false;
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
    /* JADX WARNING: Removed duplicated region for block: B:44:0x00e1  */
    /* JADX WARNING: Removed duplicated region for block: B:45:0x00f6  */
    /* JADX WARNING: Removed duplicated region for block: B:48:0x00fe  */
    /* JADX WARNING: Removed duplicated region for block: B:69:0x01af  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void updateMuteButton(int r14, boolean r15) {
        /*
            r13 = this;
            int r0 = r13.muteButtonState
            if (r0 != r14) goto L_0x0007
            if (r15 == 0) goto L_0x0007
            return
        L_0x0007:
            android.animation.ValueAnimator r0 = r13.muteButtonAnimator
            if (r0 == 0) goto L_0x0011
            r0.cancel()
            r0 = 0
            r13.muteButtonAnimator = r0
        L_0x0011:
            java.lang.String r0 = ""
            r1 = 21
            r2 = 64
            r3 = 2
            r4 = 42
            r5 = 4
            r6 = 0
            r7 = 1
            if (r14 != 0) goto L_0x0043
            r0 = 2131628034(0x7f0e1002, float:1.888335E38)
            java.lang.String r8 = "VoipGroupUnmute"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r8, r0)
            r8 = 2131628048(0x7f0e1010, float:1.8883378E38)
            java.lang.String r9 = "VoipHoldAndTalk"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r9, r8)
            org.telegram.ui.Components.RLottieDrawable r9 = r13.bigMicDrawable
            int r10 = r13.muteButtonState
            if (r10 != r3) goto L_0x0039
            r2 = 21
        L_0x0039:
            boolean r2 = r9.setCustomEndFrame(r2)
            r9 = 0
        L_0x003e:
            r12 = r8
            r8 = r0
            r0 = r12
            goto L_0x00db
        L_0x0043:
            if (r14 != r7) goto L_0x005e
            r8 = 2131628094(0x7f0e103e, float:1.888347E38)
            java.lang.String r9 = "VoipTapToMute"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r9, r8)
            org.telegram.ui.Components.RLottieDrawable r9 = r13.bigMicDrawable
            int r10 = r13.muteButtonState
            if (r10 != r5) goto L_0x0055
            goto L_0x0057
        L_0x0055:
            r2 = 42
        L_0x0057:
            boolean r2 = r9.setCustomEndFrame(r2)
            r9 = 0
            goto L_0x00db
        L_0x005e:
            r8 = 84
            if (r14 != r5) goto L_0x0080
            r0 = 2131628059(0x7f0e101b, float:1.88834E38)
            java.lang.String r2 = "VoipMutedTapedForSpeak"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r2 = 2131628060(0x7f0e101c, float:1.8883402E38)
            java.lang.String r9 = "VoipMutedTapedForSpeakInfo"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r9, r2)
            org.telegram.ui.Components.RLottieDrawable r9 = r13.bigMicDrawable
            boolean r8 = r9.setCustomEndFrame(r8)
            r9 = 0
            r12 = r8
            r8 = r0
            r0 = r2
            r2 = r12
            goto L_0x00db
        L_0x0080:
            org.telegram.messenger.ChatObject$Call r9 = r13.call
            android.util.SparseArray<org.telegram.tgnet.TLRPC$TL_groupCallParticipant> r9 = r9.participants
            org.telegram.tgnet.TLRPC$Peer r10 = r13.selfPeer
            int r10 = org.telegram.messenger.MessageObject.getPeerId(r10)
            java.lang.Object r9 = r9.get(r10)
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r9 = (org.telegram.tgnet.TLRPC$TL_groupCallParticipant) r9
            if (r9 == 0) goto L_0x00a4
            boolean r10 = r9.can_self_unmute
            if (r10 != 0) goto L_0x00a4
            boolean r9 = r9.muted
            if (r9 == 0) goto L_0x00a4
            org.telegram.tgnet.TLRPC$Chat r9 = r13.currentChat
            boolean r9 = org.telegram.messenger.ChatObject.canManageCalls(r9)
            if (r9 != 0) goto L_0x00a4
            r9 = 1
            goto L_0x00a5
        L_0x00a4:
            r9 = 0
        L_0x00a5:
            if (r9 == 0) goto L_0x00ae
            org.telegram.ui.Components.RLottieDrawable r2 = r13.bigMicDrawable
            boolean r2 = r2.setCustomEndFrame(r8)
            goto L_0x00ba
        L_0x00ae:
            org.telegram.ui.Components.RLottieDrawable r8 = r13.bigMicDrawable
            int r10 = r13.muteButtonState
            if (r10 != r5) goto L_0x00b6
            r2 = 21
        L_0x00b6:
            boolean r2 = r8.setCustomEndFrame(r2)
        L_0x00ba:
            r8 = 3
            if (r14 != r8) goto L_0x00c7
            r8 = 2131624962(0x7f0e0402, float:1.8877119E38)
            java.lang.String r10 = "Connecting"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r10, r8)
            goto L_0x00db
        L_0x00c7:
            r0 = 2131628055(0x7f0e1017, float:1.8883392E38)
            java.lang.String r8 = "VoipMutedByAdmin"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r8, r0)
            r8 = 2131628058(0x7f0e101a, float:1.8883398E38)
            java.lang.String r10 = "VoipMutedTapForSpeak"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r10, r8)
            goto L_0x003e
        L_0x00db:
            boolean r10 = android.text.TextUtils.isEmpty(r0)
            if (r10 != 0) goto L_0x00f6
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            r10.append(r8)
            java.lang.String r11 = " "
            r10.append(r11)
            r10.append(r0)
            java.lang.String r10 = r10.toString()
            goto L_0x00f7
        L_0x00f6:
            r10 = r8
        L_0x00f7:
            org.telegram.ui.Components.RLottieImageView r11 = r13.muteButton
            r11.setContentDescription(r10)
            if (r15 == 0) goto L_0x01af
            if (r2 == 0) goto L_0x0137
            if (r14 != 0) goto L_0x010d
            org.telegram.ui.Components.RLottieDrawable r1 = r13.bigMicDrawable
            int r2 = r13.muteButtonState
            if (r2 != r3) goto L_0x0109
            r4 = 0
        L_0x0109:
            r1.setCurrentFrame(r4)
            goto L_0x0137
        L_0x010d:
            if (r14 != r7) goto L_0x011b
            org.telegram.ui.Components.RLottieDrawable r2 = r13.bigMicDrawable
            int r9 = r13.muteButtonState
            if (r9 != r5) goto L_0x0117
            r1 = 42
        L_0x0117:
            r2.setCurrentFrame(r1)
            goto L_0x0137
        L_0x011b:
            r1 = 63
            if (r14 != r5) goto L_0x0125
            org.telegram.ui.Components.RLottieDrawable r2 = r13.bigMicDrawable
            r2.setCurrentFrame(r1)
            goto L_0x0137
        L_0x0125:
            if (r9 == 0) goto L_0x012d
            org.telegram.ui.Components.RLottieDrawable r2 = r13.bigMicDrawable
            r2.setCurrentFrame(r1)
            goto L_0x0137
        L_0x012d:
            org.telegram.ui.Components.RLottieDrawable r1 = r13.bigMicDrawable
            int r2 = r13.muteButtonState
            if (r2 != r5) goto L_0x0134
            r4 = 0
        L_0x0134:
            r1.setCurrentFrame(r4)
        L_0x0137:
            org.telegram.ui.Components.RLottieImageView r1 = r13.muteButton
            r1.playAnimation()
            android.widget.TextView[] r1 = r13.muteLabel
            r1 = r1[r7]
            r1.setVisibility(r6)
            android.widget.TextView[] r1 = r13.muteLabel
            r1 = r1[r7]
            r2 = 0
            r1.setAlpha(r2)
            android.widget.TextView[] r1 = r13.muteLabel
            r1 = r1[r7]
            r4 = 1084227584(0x40a00000, float:5.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r5 = -r5
            float r5 = (float) r5
            r1.setTranslationY(r5)
            android.widget.TextView[] r1 = r13.muteLabel
            r1 = r1[r7]
            r1.setText(r8)
            android.widget.TextView[] r1 = r13.muteSubLabel
            r1 = r1[r7]
            r1.setVisibility(r6)
            android.widget.TextView[] r1 = r13.muteSubLabel
            r1 = r1[r7]
            r1.setAlpha(r2)
            android.widget.TextView[] r1 = r13.muteSubLabel
            r1 = r1[r7]
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r2 = -r2
            float r2 = (float) r2
            r1.setTranslationY(r2)
            android.widget.TextView[] r1 = r13.muteSubLabel
            r1 = r1[r7]
            r1.setText(r0)
            float[] r0 = new float[r3]
            r0 = {0, NUM} // fill-array
            android.animation.ValueAnimator r0 = android.animation.ValueAnimator.ofFloat(r0)
            r13.muteButtonAnimator = r0
            org.telegram.ui.-$$Lambda$GroupCallActivity$RSdlaR2e6vqbHKsqkKRJabHeaXA r1 = new org.telegram.ui.-$$Lambda$GroupCallActivity$RSdlaR2e6vqbHKsqkKRJabHeaXA
            r1.<init>()
            r0.addUpdateListener(r1)
            android.animation.ValueAnimator r0 = r13.muteButtonAnimator
            org.telegram.ui.GroupCallActivity$20 r1 = new org.telegram.ui.GroupCallActivity$20
            r1.<init>()
            r0.addListener(r1)
            android.animation.ValueAnimator r0 = r13.muteButtonAnimator
            r1 = 180(0xb4, double:8.9E-322)
            r0.setDuration(r1)
            android.animation.ValueAnimator r0 = r13.muteButtonAnimator
            r0.start()
            r13.muteButtonState = r14
            goto L_0x01c9
        L_0x01af:
            r13.muteButtonState = r14
            org.telegram.ui.Components.RLottieDrawable r14 = r13.bigMicDrawable
            int r1 = r14.getCustomEndFrame()
            int r1 = r1 - r7
            r14.setCurrentFrame(r1, r6, r7)
            android.widget.TextView[] r14 = r13.muteLabel
            r14 = r14[r6]
            r14.setText(r8)
            android.widget.TextView[] r14 = r13.muteSubLabel
            r14 = r14[r6]
            r14.setText(r0)
        L_0x01c9:
            r13.updateMuteButtonState(r15)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.GroupCallActivity.updateMuteButton(int, boolean):void");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$updateMuteButton$34 */
    public /* synthetic */ void lambda$updateMuteButton$34$GroupCallActivity(ValueAnimator valueAnimator) {
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
        } else if (i == 2 || i == 4) {
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
            } else if (i2 == 2 || i2 == 4) {
                Shader unused2 = this.states[i2].shader = new LinearGradient(0.0f, 400.0f, 400.0f, 0.0f, new int[]{Theme.getColor("voipgroup_mutedByAdminGradient"), Theme.getColor("voipgroup_mutedByAdminGradient3"), Theme.getColor("voipgroup_mutedByAdminGradient2")}, (float[]) null, Shader.TileMode.CLAMP);
            } else if (i2 == 1) {
                Shader unused3 = this.states[i2].shader = new RadialGradient(200.0f, 200.0f, 200.0f, new int[]{Theme.getColor("voipgroup_muteButton"), Theme.getColor("voipgroup_muteButton3")}, (float[]) null, Shader.TileMode.CLAMP);
            } else {
                Shader unused4 = this.states[i2].shader = new RadialGradient(200.0f, 200.0f, 200.0f, new int[]{Theme.getColor("voipgroup_unmuteButton2"), Theme.getColor("voipgroup_unmuteButton")}, (float[]) null, Shader.TileMode.CLAMP);
            }
        }
        WeavingState[] weavingStateArr2 = this.states;
        int i3 = this.muteButtonState;
        WeavingState weavingState = weavingStateArr2[i3];
        WeavingState weavingState2 = this.currentState;
        float f = 0.0f;
        if (weavingState != weavingState2) {
            this.prevState = weavingState2;
            this.currentState = weavingStateArr2[i3];
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
                    GroupCallActivity.lambda$onLeaveClick$35(this.f$0, view);
                }
            });
            builder.setCustomViewOffset(12);
            builder.setView(linearLayout);
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

    static /* synthetic */ void lambda$onLeaveClick$35(CheckBoxCell[] checkBoxCellArr, View view) {
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
            if (i4 == 0 || i4 == 2 || i4 == 3) {
                if (i4 != 0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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
                    boolean z = tLObject2 instanceof TLRPC$User;
                    if (z) {
                        TLRPC$User tLRPC$User = (TLRPC$User) tLObject2;
                        backupImageView.setImage(ImageLocation.getForUser(tLRPC$User, false), "50_50", (Drawable) avatarDrawable, (Object) tLRPC$User);
                        str = UserObject.getFirstName(tLRPC$User);
                    } else {
                        TLRPC$Chat tLRPC$Chat = (TLRPC$Chat) tLObject2;
                        backupImageView.setImage(ImageLocation.getForChat(tLRPC$Chat, false), "50_50", (Drawable) avatarDrawable, (Object) tLRPC$Chat);
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
                    boolean z2 = LocaleController.isRTL;
                    int i5 = (z2 ? 5 : 3) | 48;
                    int i6 = 21;
                    float f = (float) (z2 ? 21 : 76);
                    if (z2) {
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
                                GroupCallActivity.this.lambda$processSelectedOption$37$GroupCallActivity(this.f$1, dialogInterface, i);
                            }
                        });
                    } else if (z) {
                        builder.setPositiveButton(LocaleController.getString("VoipGroupAdd", NUM), new DialogInterface.OnClickListener((TLRPC$User) tLObject2, i3) {
                            public final /* synthetic */ TLRPC$User f$1;
                            public final /* synthetic */ int f$2;

                            {
                                this.f$1 = r2;
                                this.f$2 = r3;
                            }

                            public final void onClick(DialogInterface dialogInterface, int i) {
                                GroupCallActivity.this.lambda$processSelectedOption$39$GroupCallActivity(this.f$1, this.f$2, dialogInterface, i);
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
    /* renamed from: lambda$processSelectedOption$37 */
    public /* synthetic */ void lambda$processSelectedOption$37$GroupCallActivity(TLObject tLObject, DialogInterface dialogInterface, int i) {
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
    /* renamed from: lambda$processSelectedOption$39 */
    public /* synthetic */ void lambda$processSelectedOption$39$GroupCallActivity(TLRPC$User tLRPC$User, int i, DialogInterface dialogInterface, int i2) {
        this.accountInstance.getMessagesController().addUserToChat(this.currentChat.id, tLRPC$User, 0, (String) null, this.parentActivity.getActionBarLayout().fragmentsStack.get(this.parentActivity.getActionBarLayout().fragmentsStack.size() - 1), new Runnable(i) {
            public final /* synthetic */ int f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                GroupCallActivity.this.lambda$null$38$GroupCallActivity(this.f$1);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$38 */
    public /* synthetic */ void lambda$null$38$GroupCallActivity(int i) {
        inviteUserToCall(i, false);
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:47:0x01af, code lost:
        if (r1.admin_rights.manage_call != false) goto L_0x01b1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:61:0x01ee, code lost:
        if ((r10 instanceof org.telegram.tgnet.TLRPC$TL_chatParticipantCreator) == false) goto L_0x01b3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:64:0x01fe, code lost:
        if (r3 == (-r7.currentChat.id)) goto L_0x01b1;
     */
    /* JADX WARNING: Removed duplicated region for block: B:107:0x039b  */
    /* JADX WARNING: Removed duplicated region for block: B:122:0x043a  */
    /* JADX WARNING: Removed duplicated region for block: B:125:0x0441  */
    /* JADX WARNING: Removed duplicated region for block: B:126:0x045d  */
    /* JADX WARNING: Removed duplicated region for block: B:128:0x047a  */
    /* JADX WARNING: Removed duplicated region for block: B:129:0x047d  */
    /* JADX WARNING: Removed duplicated region for block: B:132:0x0499  */
    /* JADX WARNING: Removed duplicated region for block: B:133:0x04da  */
    /* JADX WARNING: Removed duplicated region for block: B:67:0x0207  */
    /* JADX WARNING: Removed duplicated region for block: B:68:0x0228  */
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
            if (r0 != 0) goto L_0x0592
            boolean r0 = r7.avatarsPreviewShowed
            if (r0 == 0) goto L_0x0019
            goto L_0x0592
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
            org.telegram.ui.GroupCallActivity$21 r2 = new org.telegram.ui.GroupCallActivity$21
            r2.<init>(r0)
            r13.setOnTouchListener(r2)
            org.telegram.ui.-$$Lambda$GroupCallActivity$LFEo_xTxbDUreq20IO36hxPn7-c r0 = new org.telegram.ui.-$$Lambda$GroupCallActivity$LFEo_xTxbDUreq20IO36hxPn7-c
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
            org.telegram.ui.GroupCallActivity$22 r15 = new org.telegram.ui.GroupCallActivity$22
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
            boolean r4 = r12.muted_by_you
            r5 = 2131165942(0x7var_f6, float:1.7946115E38)
            if (r4 != 0) goto L_0x00f8
            boolean r4 = r12.muted
            if (r4 == 0) goto L_0x00b9
            boolean r4 = r12.can_self_unmute
            if (r4 == 0) goto L_0x00f8
        L_0x00b9:
            android.content.Context r1 = r28.getContext()
            android.content.res.Resources r1 = r1.getResources()
            android.graphics.drawable.Drawable r1 = r1.getDrawable(r5)
            android.graphics.drawable.Drawable r1 = r1.mutate()
            android.graphics.PorterDuffColorFilter r4 = new android.graphics.PorterDuffColorFilter
            android.graphics.PorterDuff$Mode r6 = android.graphics.PorterDuff.Mode.MULTIPLY
            r4.<init>(r3, r6)
            r1.setColorFilter(r4)
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
            android.content.Context r4 = r28.getContext()
            r1.<init>(r7, r4, r12)
            r4 = -1
            r6 = 48
            r0.addView(r1, r4, r6)
        L_0x00f8:
            r6 = r1
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r14.setMinimumWidth(r0)
            r14.setOrientation(r10)
            android.content.Context r0 = r28.getContext()
            android.content.res.Resources r0 = r0.getResources()
            android.graphics.drawable.Drawable r0 = r0.getDrawable(r5)
            android.graphics.drawable.Drawable r0 = r0.mutate()
            android.graphics.PorterDuffColorFilter r1 = new android.graphics.PorterDuffColorFilter
            android.graphics.PorterDuff$Mode r2 = android.graphics.PorterDuff.Mode.MULTIPLY
            r1.<init>(r3, r2)
            r0.setColorFilter(r1)
            r14.setBackgroundDrawable(r0)
            r16 = -2
            r17 = -2
            r18 = 0
            if (r6 == 0) goto L_0x012d
            r0 = -1056964608(0xffffffffCLASSNAME, float:-8.0)
            r19 = -1056964608(0xffffffffCLASSNAME, float:-8.0)
            goto L_0x0130
        L_0x012d:
            r0 = 0
            r19 = 0
        L_0x0130:
            r20 = 0
            r21 = 0
            android.widget.LinearLayout$LayoutParams r0 = org.telegram.ui.Components.LayoutHelper.createLinear(r16, r17, r18, r19, r20, r21)
            r15.addView(r14, r0)
            int r0 = android.os.Build.VERSION.SDK_INT
            r1 = 21
            if (r0 < r1) goto L_0x0157
            org.telegram.ui.GroupCallActivity$23 r16 = new org.telegram.ui.GroupCallActivity$23
            android.content.Context r2 = r28.getContext()
            r3 = 0
            r4 = 0
            r5 = 2131689504(0x7f0var_, float:1.9008025E38)
            r0 = r16
            r1 = r28
            r22 = r6
            r6 = r15
            r0.<init>(r1, r2, r3, r4, r5, r6)
            goto L_0x0162
        L_0x0157:
            r22 = r6
            android.widget.ScrollView r0 = new android.widget.ScrollView
            android.content.Context r1 = r28.getContext()
            r0.<init>(r1)
        L_0x0162:
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
            if (r1 == 0) goto L_0x01f9
            org.telegram.tgnet.TLRPC$Chat r1 = r7.currentChat
            boolean r1 = org.telegram.messenger.ChatObject.isChannel(r1)
            if (r1 == 0) goto L_0x01b5
            org.telegram.messenger.AccountInstance r1 = r7.accountInstance
            org.telegram.messenger.MessagesController r1 = r1.getMessagesController()
            org.telegram.tgnet.TLRPC$Peer r5 = r12.peer
            int r5 = r5.user_id
            org.telegram.tgnet.TLRPC$Chat r9 = r7.currentChat
            int r9 = r9.id
            org.telegram.tgnet.TLRPC$ChannelParticipant r1 = r1.getAdminInChannel(r5, r9)
            if (r1 == 0) goto L_0x01b3
            boolean r5 = r1 instanceof org.telegram.tgnet.TLRPC$TL_channelParticipantCreator
            if (r5 != 0) goto L_0x01b1
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r1 = r1.admin_rights
            boolean r1 = r1.manage_call
            if (r1 == 0) goto L_0x01b3
        L_0x01b1:
            r1 = 1
            goto L_0x0201
        L_0x01b3:
            r1 = 0
            goto L_0x0201
        L_0x01b5:
            org.telegram.messenger.AccountInstance r1 = r7.accountInstance
            org.telegram.messenger.MessagesController r1 = r1.getMessagesController()
            org.telegram.tgnet.TLRPC$Chat r5 = r7.currentChat
            int r5 = r5.id
            org.telegram.tgnet.TLRPC$ChatFull r1 = r1.getChatFull(r5)
            if (r1 == 0) goto L_0x01b3
            org.telegram.tgnet.TLRPC$ChatParticipants r5 = r1.participants
            if (r5 == 0) goto L_0x01b3
            java.util.ArrayList<org.telegram.tgnet.TLRPC$ChatParticipant> r5 = r5.participants
            int r5 = r5.size()
            r9 = 0
        L_0x01d0:
            if (r9 >= r5) goto L_0x01b3
            org.telegram.tgnet.TLRPC$ChatParticipants r10 = r1.participants
            java.util.ArrayList<org.telegram.tgnet.TLRPC$ChatParticipant> r10 = r10.participants
            java.lang.Object r10 = r10.get(r9)
            org.telegram.tgnet.TLRPC$ChatParticipant r10 = (org.telegram.tgnet.TLRPC$ChatParticipant) r10
            r21 = r1
            int r1 = r10.user_id
            r23 = r5
            org.telegram.tgnet.TLRPC$Peer r5 = r12.peer
            int r5 = r5.user_id
            if (r1 != r5) goto L_0x01f1
            boolean r1 = r10 instanceof org.telegram.tgnet.TLRPC$TL_chatParticipantAdmin
            if (r1 != 0) goto L_0x01b1
            boolean r1 = r10 instanceof org.telegram.tgnet.TLRPC$TL_chatParticipantCreator
            if (r1 == 0) goto L_0x01b3
            goto L_0x01b1
        L_0x01f1:
            int r9 = r9 + 1
            r1 = r21
            r5 = r23
            r10 = 1
            goto L_0x01d0
        L_0x01f9:
            org.telegram.tgnet.TLRPC$Chat r1 = r7.currentChat
            int r1 = r1.id
            int r1 = -r1
            if (r3 != r1) goto L_0x01b3
            goto L_0x01b1
        L_0x0201:
            boolean r5 = r29.isSelfUser()
            if (r5 == 0) goto L_0x0228
            r1 = 2131627966(0x7f0e0fbe, float:1.8883211E38)
            java.lang.String r5 = "VoipGroupCancelRaiseHand"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r5, r1)
            r4.add(r1)
            r1 = 2131165749(0x7var_, float:1.7945724E38)
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            r6.add(r1)
            r1 = 7
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            r2.add(r1)
            r11 = 0
            goto L_0x038e
        L_0x0228:
            org.telegram.tgnet.TLRPC$Chat r5 = r7.currentChat
            boolean r5 = org.telegram.messenger.ChatObject.canManageCalls(r5)
            java.lang.String r9 = "VoipGroupOpenChannel"
            r23 = 6
            r24 = 2131165840(0x7var_, float:1.7945908E38)
            r25 = 2131165838(0x7var_e, float:1.7945904E38)
            if (r5 == 0) goto L_0x030b
            if (r1 == 0) goto L_0x0243
            boolean r5 = r12.muted
            if (r5 != 0) goto L_0x0241
            goto L_0x0243
        L_0x0241:
            r5 = r11
            goto L_0x0298
        L_0x0243:
            boolean r5 = r12.muted
            if (r5 == 0) goto L_0x027c
            boolean r5 = r12.can_self_unmute
            if (r5 == 0) goto L_0x024c
            goto L_0x027c
        L_0x024c:
            r5 = 2131627963(0x7f0e0fbb, float:1.8883205E38)
            java.lang.String r10 = "VoipGroupAllowToSpeak"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r10, r5)
            r4.add(r5)
            r5 = r11
            long r10 = r12.raise_hand_rating
            r26 = 0
            int r25 = (r10 > r26 ? 1 : (r10 == r26 ? 0 : -1))
            if (r25 == 0) goto L_0x026c
            r10 = 2131165713(0x7var_, float:1.794565E38)
            java.lang.Integer r10 = java.lang.Integer.valueOf(r10)
            r6.add(r10)
            goto L_0x0273
        L_0x026c:
            java.lang.Integer r10 = java.lang.Integer.valueOf(r24)
            r6.add(r10)
        L_0x0273:
            r10 = 1
            java.lang.Integer r11 = java.lang.Integer.valueOf(r10)
            r2.add(r11)
            goto L_0x0298
        L_0x027c:
            r5 = r11
            r10 = 2131627999(0x7f0e0fdf, float:1.8883278E38)
            java.lang.String r11 = "VoipGroupMute"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r11, r10)
            r4.add(r10)
            java.lang.Integer r10 = java.lang.Integer.valueOf(r25)
            r6.add(r10)
            r10 = 0
            java.lang.Integer r11 = java.lang.Integer.valueOf(r10)
            r2.add(r11)
        L_0x0298:
            org.telegram.tgnet.TLRPC$Peer r10 = r12.peer
            int r10 = r10.channel_id
            if (r10 == 0) goto L_0x02c4
            int r11 = r7.currentAccount
            boolean r10 = org.telegram.messenger.ChatObject.isMegagroup(r11, r10)
            if (r10 != 0) goto L_0x02c4
            r10 = 2131628007(0x7f0e0fe7, float:1.8883295E38)
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r9, r10)
            r4.add(r9)
            r9 = 2131165722(0x7var_a, float:1.794567E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            r6.add(r9)
            r9 = 8
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            r2.add(r9)
            goto L_0x02e1
        L_0x02c4:
            r9 = 2131628010(0x7f0e0fea, float:1.88833E38)
            java.lang.String r10 = "VoipGroupOpenProfile"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r10, r9)
            r4.add(r9)
            r9 = 2131165777(0x7var_, float:1.794578E38)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            r6.add(r9)
            java.lang.Integer r9 = java.lang.Integer.valueOf(r23)
            r2.add(r9)
        L_0x02e1:
            if (r1 != 0) goto L_0x038d
            org.telegram.tgnet.TLRPC$Chat r1 = r7.currentChat
            boolean r1 = org.telegram.messenger.ChatObject.canBlockUsers(r1)
            if (r1 == 0) goto L_0x038d
            r1 = 2131628041(0x7f0e1009, float:1.8883363E38)
            java.lang.String r9 = "VoipGroupUserRemove"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r9, r1)
            r4.add(r1)
            r1 = 2131165718(0x7var_, float:1.7945661E38)
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            r6.add(r1)
            r1 = 2
            java.lang.Integer r9 = java.lang.Integer.valueOf(r1)
            r2.add(r9)
            goto L_0x038d
        L_0x030b:
            r5 = r11
            boolean r1 = r12.muted_by_you
            if (r1 == 0) goto L_0x032c
            r1 = 2131628035(0x7f0e1003, float:1.8883351E38)
            java.lang.String r10 = "VoipGroupUnmuteForMe"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r10, r1)
            r4.add(r1)
            java.lang.Integer r1 = java.lang.Integer.valueOf(r24)
            r6.add(r1)
            r1 = 4
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            r2.add(r1)
            goto L_0x0347
        L_0x032c:
            r1 = 2131628000(0x7f0e0fe0, float:1.888328E38)
            java.lang.String r10 = "VoipGroupMuteForMe"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r10, r1)
            r4.add(r1)
            java.lang.Integer r1 = java.lang.Integer.valueOf(r25)
            r6.add(r1)
            r1 = 5
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            r2.add(r1)
        L_0x0347:
            org.telegram.tgnet.TLRPC$Peer r1 = r12.peer
            int r1 = r1.channel_id
            r10 = 2131165774(0x7var_e, float:1.7945775E38)
            if (r1 == 0) goto L_0x0373
            int r11 = r7.currentAccount
            boolean r1 = org.telegram.messenger.ChatObject.isMegagroup(r11, r1)
            if (r1 != 0) goto L_0x0373
            r1 = 2131628007(0x7f0e0fe7, float:1.8883295E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r9, r1)
            r4.add(r1)
            java.lang.Integer r1 = java.lang.Integer.valueOf(r10)
            r6.add(r1)
            r1 = 8
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            r2.add(r1)
            goto L_0x038d
        L_0x0373:
            r1 = 2131628008(0x7f0e0fe8, float:1.8883297E38)
            java.lang.String r9 = "VoipGroupOpenChat"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r9, r1)
            r4.add(r1)
            java.lang.Integer r1 = java.lang.Integer.valueOf(r10)
            r6.add(r1)
            java.lang.Integer r1 = java.lang.Integer.valueOf(r23)
            r2.add(r1)
        L_0x038d:
            r11 = r5
        L_0x038e:
            int r1 = r4.size()
            org.telegram.ui.ActionBar.ActionBarMenuSubItem[] r1 = new org.telegram.ui.ActionBar.ActionBarMenuSubItem[r1]
            int r1 = r4.size()
            r5 = 0
        L_0x0399:
            if (r5 >= r1) goto L_0x040d
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r9 = new org.telegram.ui.ActionBar.ActionBarMenuSubItem
            android.content.Context r10 = r28.getContext()
            r21 = r11
            if (r5 != 0) goto L_0x03a7
            r11 = 1
            goto L_0x03a8
        L_0x03a7:
            r11 = 0
        L_0x03a8:
            r23 = r3
            int r3 = r1 + -1
            if (r5 != r3) goto L_0x03b0
            r3 = 1
            goto L_0x03b1
        L_0x03b0:
            r3 = 0
        L_0x03b1:
            r9.<init>(r10, r11, r3)
            java.lang.Object r3 = r2.get(r5)
            java.lang.Integer r3 = (java.lang.Integer) r3
            int r3 = r3.intValue()
            r10 = 2
            if (r3 == r10) goto L_0x03d0
            java.lang.String r3 = "voipgroup_actionBarItems"
            int r10 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            r9.setColors(r10, r3)
            goto L_0x03de
        L_0x03d0:
            java.lang.String r3 = "voipgroup_leaveCallMenu"
            int r10 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            r9.setColors(r10, r3)
        L_0x03de:
            java.lang.String r3 = "voipgroup_listSelector"
            int r3 = org.telegram.ui.ActionBar.Theme.getColor(r3)
            r9.setSelectorColor(r3)
            java.lang.Object r3 = r4.get(r5)
            java.lang.CharSequence r3 = (java.lang.CharSequence) r3
            java.lang.Object r10 = r6.get(r5)
            java.lang.Integer r10 = (java.lang.Integer) r10
            int r10 = r10.intValue()
            r9.setTextAndIcon(r3, r10)
            r14.addView(r9)
            org.telegram.ui.-$$Lambda$GroupCallActivity$JukaJOrfLwyGr6RRaB45knp0lEU r3 = new org.telegram.ui.-$$Lambda$GroupCallActivity$JukaJOrfLwyGr6RRaB45knp0lEU
            r3.<init>(r5, r2, r12)
            r9.setOnClickListener(r3)
            int r5 = r5 + 1
            r11 = r21
            r3 = r23
            goto L_0x0399
        L_0x040d:
            r23 = r3
            r21 = r11
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
            if (r0 == 0) goto L_0x043d
            r0.cancel()
        L_0x043d:
            r7.scrimPopupLayout = r13
            if (r23 <= 0) goto L_0x045d
            org.telegram.messenger.AccountInstance r0 = r7.accountInstance
            org.telegram.messenger.MessagesController r0 = r0.getMessagesController()
            java.lang.Integer r2 = java.lang.Integer.valueOf(r23)
            org.telegram.tgnet.TLRPC$User r0 = r0.getUser(r2)
            r2 = 1
            org.telegram.messenger.ImageLocation r3 = org.telegram.messenger.ImageLocation.getForUser(r0, r2)
            r4 = 0
            org.telegram.messenger.ImageLocation r0 = org.telegram.messenger.ImageLocation.getForUser(r0, r4)
            r5 = r3
            r3 = r23
            goto L_0x0478
        L_0x045d:
            r2 = 1
            r4 = 0
            org.telegram.messenger.AccountInstance r0 = r7.accountInstance
            org.telegram.messenger.MessagesController r0 = r0.getMessagesController()
            r3 = r23
            int r5 = -r3
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            org.telegram.tgnet.TLRPC$Chat r0 = r0.getChat(r5)
            org.telegram.messenger.ImageLocation r5 = org.telegram.messenger.ImageLocation.getForChat(r0, r2)
            org.telegram.messenger.ImageLocation r0 = org.telegram.messenger.ImageLocation.getForChat(r0, r4)
        L_0x0478:
            if (r5 != 0) goto L_0x047d
            r21 = 0
            goto L_0x0493
        L_0x047d:
            org.telegram.ui.Components.ProfileGalleryView r2 = r7.avatarsViewPager
            org.telegram.ui.Cells.GroupCallUserCell r4 = r7.scrimView
            org.telegram.ui.Components.BackupImageView r4 = r4.getAvatarImageView()
            r2.setParentAvatarImage(r4)
            org.telegram.ui.Components.ProfileGalleryView r2 = r7.avatarsViewPager
            long r3 = (long) r3
            r2.setData(r3)
            org.telegram.ui.Components.ProfileGalleryView r2 = r7.avatarsViewPager
            r2.initIfEmpty(r5, r0)
        L_0x0493:
            r0 = -2147483648(0xfffffffvar_, float:-0.0)
            r2 = 1148846080(0x447a0000, float:1000.0)
            if (r21 == 0) goto L_0x04da
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
            org.telegram.ui.GroupCallActivity$24 r1 = new org.telegram.ui.GroupCallActivity$24
            r2 = r22
            r1.<init>(r2, r8)
            r0.addOnPreDrawListener(r1)
            goto L_0x0590
        L_0x04da:
            r3 = 0
            r7.avatarsPreviewShowed = r3
            org.telegram.ui.GroupCallActivity$25 r3 = new org.telegram.ui.GroupCallActivity$25
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
        L_0x0590:
            r0 = 1
            return r0
        L_0x0592:
            r0 = 1
            r7.dismissAvatarPreview(r0)
            r0 = 0
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.GroupCallActivity.showMenuForCell(org.telegram.ui.Cells.GroupCallUserCell):boolean");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$showMenuForCell$40 */
    public /* synthetic */ void lambda$showMenuForCell$40$GroupCallActivity(KeyEvent keyEvent) {
        ActionBarPopupWindow actionBarPopupWindow;
        if (keyEvent.getKeyCode() == 4 && keyEvent.getRepeatCount() == 0 && (actionBarPopupWindow = this.scrimPopupWindow) != null && actionBarPopupWindow.isShowing()) {
            this.scrimPopupWindow.dismiss();
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$showMenuForCell$41 */
    public /* synthetic */ void lambda$showMenuForCell$41$GroupCallActivity(int i, ArrayList arrayList, TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant, View view) {
        if (i < arrayList.size()) {
            processSelectedOption(tLRPC$TL_groupCallParticipant, MessageObject.getPeerId(tLRPC$TL_groupCallParticipant.peer), ((Integer) arrayList.get(i)).intValue());
            ActionBarPopupWindow actionBarPopupWindow = this.scrimPopupWindow;
            if (actionBarPopupWindow != null) {
                actionBarPopupWindow.dismiss();
            } else {
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
                GroupCallActivity.this.lambda$runAvatarPreviewTransition$42$GroupCallActivity(this.f$1, valueAnimator);
            }
        });
        this.popupAnimationIndex = NotificationCenter.getInstance(this.currentAccount).setAnimationInProgress(this.popupAnimationIndex, new int[]{NotificationCenter.dialogPhotosLoaded, NotificationCenter.fileDidLoad, NotificationCenter.messagesDidLoad});
        ofFloat.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                NotificationCenter.getInstance(GroupCallActivity.this.currentAccount).onAnimationFinish(GroupCallActivity.this.popupAnimationIndex);
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
                GroupCallActivity.this.containerView.invalidate();
                GroupCallActivity.this.avatarsViewPager.invalidate();
                GroupCallActivity.this.listView.invalidate();
            }
        });
        ofFloat.setInterpolator(cubicBezierInterpolator);
        ofFloat.setDuration(220);
        ofFloat.start();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$runAvatarPreviewTransition$42 */
    public /* synthetic */ void lambda$runAvatarPreviewTransition$42$GroupCallActivity(int i, ValueAnimator valueAnimator) {
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
        /* JADX WARNING: Code restructure failed: missing block: B:14:0x0041, code lost:
            if (android.text.TextUtils.isEmpty(r1.username) == false) goto L_0x0047;
         */
        /* JADX WARNING: Removed duplicated region for block: B:19:0x0063  */
        /* JADX WARNING: Removed duplicated region for block: B:22:0x0085  */
        /* JADX WARNING: Removed duplicated region for block: B:23:0x008a  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void updateRows() {
            /*
                r4 = this;
                org.telegram.ui.GroupCallActivity r0 = org.telegram.ui.GroupCallActivity.this
                boolean r0 = r0.delayedGroupCallUpdated
                if (r0 == 0) goto L_0x0009
                return
            L_0x0009:
                r0 = 0
                r4.rowsCount = r0
                org.telegram.ui.GroupCallActivity r1 = org.telegram.ui.GroupCallActivity.this
                org.telegram.tgnet.TLRPC$Chat r1 = r1.currentChat
                boolean r1 = org.telegram.messenger.ChatObject.isChannel(r1)
                r2 = -1
                if (r1 == 0) goto L_0x001f
                org.telegram.ui.GroupCallActivity r1 = org.telegram.ui.GroupCallActivity.this
                org.telegram.tgnet.TLRPC$Chat r1 = r1.currentChat
                boolean r1 = r1.megagroup
                if (r1 == 0) goto L_0x0029
            L_0x001f:
                org.telegram.ui.GroupCallActivity r1 = org.telegram.ui.GroupCallActivity.this
                org.telegram.tgnet.TLRPC$Chat r1 = r1.currentChat
                boolean r1 = org.telegram.messenger.ChatObject.canWriteToChat(r1)
                if (r1 != 0) goto L_0x0047
            L_0x0029:
                org.telegram.ui.GroupCallActivity r1 = org.telegram.ui.GroupCallActivity.this
                org.telegram.tgnet.TLRPC$Chat r1 = r1.currentChat
                boolean r1 = org.telegram.messenger.ChatObject.isChannel(r1)
                if (r1 == 0) goto L_0x0044
                org.telegram.ui.GroupCallActivity r1 = org.telegram.ui.GroupCallActivity.this
                org.telegram.tgnet.TLRPC$Chat r1 = r1.currentChat
                boolean r3 = r1.megagroup
                if (r3 != 0) goto L_0x0044
                java.lang.String r1 = r1.username
                boolean r1 = android.text.TextUtils.isEmpty(r1)
                if (r1 != 0) goto L_0x0044
                goto L_0x0047
            L_0x0044:
                r4.addMemberRow = r2
                goto L_0x004f
            L_0x0047:
                int r1 = r4.rowsCount
                int r3 = r1 + 1
                r4.rowsCount = r3
                r4.addMemberRow = r1
            L_0x004f:
                org.telegram.ui.GroupCallActivity r1 = org.telegram.ui.GroupCallActivity.this
                org.telegram.messenger.ChatObject$Call r3 = r1.call
                android.util.SparseArray<org.telegram.tgnet.TLRPC$TL_groupCallParticipant> r3 = r3.participants
                org.telegram.tgnet.TLRPC$Peer r1 = r1.selfPeer
                int r1 = org.telegram.messenger.MessageObject.getPeerId(r1)
                int r1 = r3.indexOfKey(r1)
                if (r1 < 0) goto L_0x0064
                r0 = 1
            L_0x0064:
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
                if (r0 == 0) goto L_0x008a
                r4.invitedStartRow = r2
                r4.invitedEndRow = r2
                goto L_0x009d
            L_0x008a:
                int r0 = r4.rowsCount
                r4.invitedStartRow = r0
                org.telegram.ui.GroupCallActivity r1 = org.telegram.ui.GroupCallActivity.this
                org.telegram.messenger.ChatObject$Call r1 = r1.call
                java.util.ArrayList<java.lang.Integer> r1 = r1.invitedUsers
                int r1 = r1.size()
                int r0 = r0 + r1
                r4.rowsCount = r0
                r4.invitedEndRow = r0
            L_0x009d:
                int r0 = r4.rowsCount
                int r1 = r0 + 1
                r4.rowsCount = r1
                r4.lastRow = r0
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

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v7, resolved type: org.telegram.tgnet.TLRPC$TL_groupCallParticipant} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v12, resolved type: java.lang.Integer} */
        /* JADX WARNING: type inference failed for: r2v6 */
        /* JADX WARNING: type inference failed for: r2v17 */
        /* JADX WARNING: type inference failed for: r2v18 */
        /* JADX WARNING: type inference failed for: r2v19 */
        /* JADX WARNING: type inference failed for: r2v20 */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r5, int r6) {
            /*
                r4 = this;
                int r0 = r5.getItemViewType()
                r1 = 1
                if (r0 == 0) goto L_0x00c0
                r2 = 0
                if (r0 == r1) goto L_0x0062
                r1 = 2
                if (r0 == r1) goto L_0x000f
                goto L_0x00fa
            L_0x000f:
                android.view.View r5 = r5.itemView
                org.telegram.ui.Cells.GroupCallInvitedCell r5 = (org.telegram.ui.Cells.GroupCallInvitedCell) r5
                int r0 = r4.invitedStartRow
                int r6 = r6 - r0
                org.telegram.ui.GroupCallActivity r0 = org.telegram.ui.GroupCallActivity.this
                boolean r0 = r0.delayedGroupCallUpdated
                if (r0 == 0) goto L_0x003a
                if (r6 < 0) goto L_0x0055
                org.telegram.ui.GroupCallActivity r0 = org.telegram.ui.GroupCallActivity.this
                java.util.ArrayList r0 = r0.oldInvited
                int r0 = r0.size()
                if (r6 >= r0) goto L_0x0055
                org.telegram.ui.GroupCallActivity r0 = org.telegram.ui.GroupCallActivity.this
                java.util.ArrayList r0 = r0.oldInvited
                java.lang.Object r6 = r0.get(r6)
                r2 = r6
                java.lang.Integer r2 = (java.lang.Integer) r2
                goto L_0x0055
            L_0x003a:
                if (r6 < 0) goto L_0x0055
                org.telegram.ui.GroupCallActivity r0 = org.telegram.ui.GroupCallActivity.this
                org.telegram.messenger.ChatObject$Call r0 = r0.call
                java.util.ArrayList<java.lang.Integer> r0 = r0.invitedUsers
                int r0 = r0.size()
                if (r6 >= r0) goto L_0x0055
                org.telegram.ui.GroupCallActivity r0 = org.telegram.ui.GroupCallActivity.this
                org.telegram.messenger.ChatObject$Call r0 = r0.call
                java.util.ArrayList<java.lang.Integer> r0 = r0.invitedUsers
                java.lang.Object r6 = r0.get(r6)
                r2 = r6
                java.lang.Integer r2 = (java.lang.Integer) r2
            L_0x0055:
                if (r2 == 0) goto L_0x00fa
                org.telegram.ui.GroupCallActivity r6 = org.telegram.ui.GroupCallActivity.this
                int r6 = r6.currentAccount
                r5.setData(r6, r2)
                goto L_0x00fa
            L_0x0062:
                android.view.View r5 = r5.itemView
                org.telegram.ui.Cells.GroupCallUserCell r5 = (org.telegram.ui.Cells.GroupCallUserCell) r5
                int r0 = r4.usersStartRow
                int r6 = r6 - r0
                org.telegram.ui.GroupCallActivity r0 = org.telegram.ui.GroupCallActivity.this
                boolean r0 = r0.delayedGroupCallUpdated
                if (r0 == 0) goto L_0x008d
                if (r6 < 0) goto L_0x00a8
                org.telegram.ui.GroupCallActivity r0 = org.telegram.ui.GroupCallActivity.this
                java.util.ArrayList r0 = r0.oldParticipants
                int r0 = r0.size()
                if (r6 >= r0) goto L_0x00a8
                org.telegram.ui.GroupCallActivity r0 = org.telegram.ui.GroupCallActivity.this
                java.util.ArrayList r0 = r0.oldParticipants
                java.lang.Object r6 = r0.get(r6)
                r2 = r6
                org.telegram.tgnet.TLRPC$TL_groupCallParticipant r2 = (org.telegram.tgnet.TLRPC$TL_groupCallParticipant) r2
                goto L_0x00a8
            L_0x008d:
                if (r6 < 0) goto L_0x00a8
                org.telegram.ui.GroupCallActivity r0 = org.telegram.ui.GroupCallActivity.this
                org.telegram.messenger.ChatObject$Call r0 = r0.call
                java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_groupCallParticipant> r0 = r0.sortedParticipants
                int r0 = r0.size()
                if (r6 >= r0) goto L_0x00a8
                org.telegram.ui.GroupCallActivity r0 = org.telegram.ui.GroupCallActivity.this
                org.telegram.messenger.ChatObject$Call r0 = r0.call
                java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_groupCallParticipant> r0 = r0.sortedParticipants
                java.lang.Object r6 = r0.get(r6)
                r2 = r6
                org.telegram.tgnet.TLRPC$TL_groupCallParticipant r2 = (org.telegram.tgnet.TLRPC$TL_groupCallParticipant) r2
            L_0x00a8:
                if (r2 == 0) goto L_0x00fa
                org.telegram.ui.GroupCallActivity r6 = org.telegram.ui.GroupCallActivity.this
                org.telegram.messenger.AccountInstance r6 = r6.accountInstance
                org.telegram.ui.GroupCallActivity r0 = org.telegram.ui.GroupCallActivity.this
                org.telegram.messenger.ChatObject$Call r1 = r0.call
                org.telegram.tgnet.TLRPC$Peer r0 = r0.selfPeer
                int r0 = org.telegram.messenger.MessageObject.getPeerId(r0)
                r5.setData(r6, r2, r1, r0)
                goto L_0x00fa
            L_0x00c0:
                android.view.View r5 = r5.itemView
                org.telegram.ui.Cells.GroupCallTextCell r5 = (org.telegram.ui.Cells.GroupCallTextCell) r5
                java.lang.String r6 = "voipgroup_lastSeenTextUnscrolled"
                int r6 = org.telegram.ui.ActionBar.Theme.getColor(r6)
                java.lang.String r0 = "voipgroup_lastSeenText"
                int r0 = org.telegram.ui.ActionBar.Theme.getColor(r0)
                org.telegram.ui.GroupCallActivity r2 = org.telegram.ui.GroupCallActivity.this
                org.telegram.ui.ActionBar.ActionBar r2 = r2.actionBar
                java.lang.Object r2 = r2.getTag()
                r3 = 1065353216(0x3var_, float:1.0)
                if (r2 == 0) goto L_0x00e3
                r2 = 1065353216(0x3var_, float:1.0)
                goto L_0x00e4
            L_0x00e3:
                r2 = 0
            L_0x00e4:
                int r6 = org.telegram.messenger.AndroidUtilities.getOffsetColor(r6, r0, r2, r3)
                r5.setColors(r6, r6)
                r6 = 2131627984(0x7f0e0fd0, float:1.8883248E38)
                java.lang.String r0 = "VoipGroupInviteMember"
                java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r0, r6)
                r0 = 2131165249(0x7var_, float:1.794471E38)
                r5.setTextAndIcon(r6, r0, r1)
            L_0x00fa:
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
                GroupCallActivity.this.lambda$toggleAdminSpeak$43$GroupCallActivity(tLObject, tLRPC$TL_error);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$toggleAdminSpeak$43 */
    public /* synthetic */ void lambda$toggleAdminSpeak$43$GroupCallActivity(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
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
}
