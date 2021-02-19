package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.DialogInterface;
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
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.os.Vibrator;
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
import org.telegram.tgnet.TLRPC$TL_chatInviteExported;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_groupCallDiscarded;
import org.telegram.tgnet.TLRPC$TL_groupCallParticipant;
import org.telegram.tgnet.TLRPC$TL_inputUser;
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
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.CheckBoxCell;
import org.telegram.ui.Cells.GroupCallInvitedCell;
import org.telegram.ui.Cells.GroupCallTextCell;
import org.telegram.ui.Cells.GroupCallUserCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.AnimationProperties;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.BlobDrawable;
import org.telegram.ui.Components.CheckBoxSquare;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.FillLastLinearLayoutManager;
import org.telegram.ui.Components.GroupCallPip;
import org.telegram.ui.Components.GroupVoipInviteAlert;
import org.telegram.ui.Components.LayoutHelper;
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
    public AccountInstance accountInstance;
    /* access modifiers changed from: private */
    public ActionBar actionBar;
    /* access modifiers changed from: private */
    public AnimatorSet actionBarAnimation;
    private View actionBarBackground;
    /* access modifiers changed from: private */
    public View actionBarShadow;
    private ActionBarMenuSubItem adminItem;
    /* access modifiers changed from: private */
    public float amplitude;
    /* access modifiers changed from: private */
    public float animateAmplitudeDiff;
    /* access modifiers changed from: private */
    public float animateToAmplitude;
    private boolean anyEnterEventSent;
    /* access modifiers changed from: private */
    public int backgroundColor;
    private RLottieDrawable bigMicDrawable;
    /* access modifiers changed from: private */
    public final BlobDrawable bigWaveDrawable;
    private FrameLayout buttonsContainer;
    public ChatObject.Call call;
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
            TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant;
            TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant2;
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
            if (i == GroupCallActivity.this.oldCount - 1 || i2 == GroupCallActivity.this.listAdapter.rowsCount - 1) {
                return false;
            }
            if ((i2 == GroupCallActivity.this.listAdapter.selfUserRow || (i2 >= GroupCallActivity.this.listAdapter.usersStartRow && i2 < GroupCallActivity.this.listAdapter.usersEndRow)) && (i == GroupCallActivity.this.oldSelfUserRow || (i >= GroupCallActivity.this.oldUsersStartRow && i < GroupCallActivity.this.oldUsersEndRow))) {
                if (i == GroupCallActivity.this.oldSelfUserRow) {
                    tLRPC$TL_groupCallParticipant = GroupCallActivity.this.selfDummyParticipant;
                } else {
                    tLRPC$TL_groupCallParticipant = (TLRPC$TL_groupCallParticipant) GroupCallActivity.this.oldParticipants.get(i - GroupCallActivity.this.oldUsersStartRow);
                }
                if (i2 == GroupCallActivity.this.listAdapter.selfUserRow) {
                    tLRPC$TL_groupCallParticipant2 = GroupCallActivity.this.selfDummyParticipant;
                } else {
                    GroupCallActivity groupCallActivity = GroupCallActivity.this;
                    tLRPC$TL_groupCallParticipant2 = groupCallActivity.call.sortedParticipants.get(i2 - groupCallActivity.listAdapter.usersStartRow);
                }
                if (tLRPC$TL_groupCallParticipant.user_id == tLRPC$TL_groupCallParticipant2.user_id && tLRPC$TL_groupCallParticipant.lastActiveDate == ((long) tLRPC$TL_groupCallParticipant.active_date)) {
                    return true;
                }
                return false;
            } else if (i2 < GroupCallActivity.this.listAdapter.invitedStartRow || i2 >= GroupCallActivity.this.listAdapter.invitedEndRow || i < GroupCallActivity.this.oldInvitedStartRow || i >= GroupCallActivity.this.oldInvitedEndRow) {
                return false;
            } else {
                GroupCallActivity groupCallActivity2 = GroupCallActivity.this;
                return ((Integer) GroupCallActivity.this.oldInvited.get(i - GroupCallActivity.this.oldInvitedStartRow)).equals(groupCallActivity2.call.invitedUsers.get(i2 - groupCallActivity2.listAdapter.invitedStartRow));
            }
        }
    };
    private View dividerItem;
    /* access modifiers changed from: private */
    public boolean enterEventSent;
    private ActionBarMenuSubItem everyoneItem;
    private GroupVoipInviteAlert groupVoipInviteAlert;
    /* access modifiers changed from: private */
    public boolean invalidateColors = true;
    private ActionBarMenuSubItem inviteItem;
    /* access modifiers changed from: private */
    public long lastUpdateTime;
    /* access modifiers changed from: private */
    public FillLastLinearLayoutManager layoutManager;
    /* access modifiers changed from: private */
    public Paint leaveBackgroundPaint = new Paint(1);
    /* access modifiers changed from: private */
    public VoIPToggleButton leaveButton;
    private ActionBarMenuSubItem leaveItem;
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
    public int oldSelfUserRow;
    /* access modifiers changed from: private */
    public int oldUsersEndRow;
    /* access modifiers changed from: private */
    public int oldUsersStartRow;
    private ActionBarMenuItem otherItem;
    /* access modifiers changed from: private */
    public Paint paint = new Paint(7);
    /* access modifiers changed from: private */
    public Paint paintTmp = new Paint(7);
    private LaunchActivity parentActivity;
    private ActionBarMenuItem pipItem;
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
    public RadialGradient radialGradient;
    /* access modifiers changed from: private */
    public final Matrix radialMatrix;
    /* access modifiers changed from: private */
    public final Paint radialPaint;
    /* access modifiers changed from: private */
    public RadialProgressView radialProgressView;
    /* access modifiers changed from: private */
    public RecordCallDrawable recordCallDrawable;
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
    public ActionBarPopupWindow scrimPopupWindow;
    /* access modifiers changed from: private */
    public ActionBarMenuSubItem[] scrimPopupWindowItems;
    /* access modifiers changed from: private */
    public View scrimView;
    /* access modifiers changed from: private */
    public float scrollOffsetY;
    /* access modifiers changed from: private */
    public boolean scrolling;
    /* access modifiers changed from: private */
    public TLRPC$TL_groupCallParticipant selfDummyParticipant;
    /* access modifiers changed from: private */
    public Drawable shadowDrawable;
    private ShareAlert shareAlert;
    /* access modifiers changed from: private */
    public float showLightingProgress;
    /* access modifiers changed from: private */
    public float showWavesProgress;
    /* access modifiers changed from: private */
    public VoIPToggleButton soundButton;
    private WeavingState[] states = new WeavingState[4];
    /* access modifiers changed from: private */
    public float switchProgress = 1.0f;
    /* access modifiers changed from: private */
    public final BlobDrawable tinyWaveDrawable;
    private TextView titleTextView;
    private UndoView[] undoView = new UndoView[2];
    /* access modifiers changed from: private */
    public Runnable unmuteRunnable = $$Lambda$GroupCallActivity$Fejzw3BitRkLCnwqEMTIYvTsgw.INSTANCE;

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

        public boolean isRecording() {
            return this.recording;
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
                    float f = this.alpha + (((float) j) / 500.0f);
                    this.alpha = f;
                    if (f >= 1.0f) {
                        this.alpha = 1.0f;
                        this.state = 1;
                    }
                } else if (i == 1) {
                    float f2 = this.alpha - (((float) j) / 500.0f);
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
                r9 = 2131558474(0x7f0d004a, float:1.8742265E38)
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
            if (VoIPService.getSharedInstance() != null) {
                this.currentProgress = d;
                TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant = this.currentParticipant;
                tLRPC$TL_groupCallParticipant.volume = (int) (d * 20000.0d);
                tLRPC$TL_groupCallParticipant.flags |= 128;
                double participantVolume = (double) ChatObject.getParticipantVolume(tLRPC$TL_groupCallParticipant);
                Double.isNaN(participantVolume);
                double d2 = participantVolume / 100.0d;
                TextView textView2 = this.textView;
                Locale locale = Locale.US;
                Object[] objArr = new Object[1];
                int i = 0;
                objArr[0] = Integer.valueOf((int) (d2 > 0.0d ? Math.max(d2, 1.0d) : 0.0d));
                textView2.setText(String.format(locale, "%d%%", objArr));
                VoIPService sharedInstance = VoIPService.getSharedInstance();
                TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant2 = this.currentParticipant;
                sharedInstance.setParticipantVolume(tLRPC$TL_groupCallParticipant2.source, tLRPC$TL_groupCallParticipant2.volume);
                int i2 = null;
                if (z) {
                    TLRPC$User user = MessagesController.getInstance(this.this$0.currentAccount).getUser(Integer.valueOf(this.currentParticipant.user_id));
                    if (this.currentParticipant.volume == 0) {
                        this.this$0.scrimPopupWindow.dismiss();
                        ActionBarPopupWindow unused = this.this$0.scrimPopupWindow = null;
                        ActionBarMenuSubItem[] unused2 = this.this$0.scrimPopupWindowItems = null;
                        GroupCallActivity groupCallActivity = this.this$0;
                        TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant3 = this.currentParticipant;
                        groupCallActivity.processSelectedOption(tLRPC$TL_groupCallParticipant3, tLRPC$TL_groupCallParticipant3.user_id, ChatObject.canManageCalls(groupCallActivity.currentChat) ? 0 : 5);
                    } else {
                        VoIPService.getSharedInstance().editCallMember(user, false, this.currentParticipant.volume);
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
            float f;
            int i2 = this.currentColor;
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
            int offsetColor = AndroidUtilities.getOffsetColor(this.oldColor, i2, this.colorChangeProgress, 1.0f);
            if (!(i2 == 0 || i2 == this.currentColor)) {
                this.colorChangeProgress = 0.0f;
                this.oldColor = offsetColor;
            }
            this.paint.setColor(offsetColor);
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
            int i3 = 1;
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
            int i4 = (int) (participantVolume / 100.0d);
            int left = this.imageView.getLeft() + (this.imageView.getMeasuredWidth() / 2) + AndroidUtilities.dp(5.0f);
            int top = this.imageView.getTop() + (this.imageView.getMeasuredHeight() / 2);
            int i5 = 0;
            while (i5 < this.volumeAlphas.length) {
                if (i5 == 0) {
                    f = (float) AndroidUtilities.dp(f5);
                    i = 0;
                } else if (i5 == i3) {
                    f = (float) AndroidUtilities.dp(10.0f);
                    i = 50;
                } else {
                    f = (float) AndroidUtilities.dp(14.0f);
                    i = 150;
                }
                float[] fArr3 = this.volumeAlphas;
                float dp3 = ((float) AndroidUtilities.dp(2.0f)) * (f2 - fArr3[i5]);
                this.paint2.setAlpha((int) (fArr3[i5] * 255.0f));
                float f6 = (float) left;
                float f7 = (float) top;
                this.rect.set((f6 - f) + dp3, (f7 - f) + dp3, (f6 + f) - dp3, (f7 + f) - dp3);
                int i6 = i;
                int i7 = i5;
                canvas.drawArc(this.rect, -50.0f, 100.0f, false, this.paint2);
                if (i4 > i6) {
                    float[] fArr4 = this.volumeAlphas;
                    if (fArr4[i7] < 1.0f) {
                        fArr4[i7] = fArr4[i7] + (((float) j) / 180.0f);
                        if (fArr4[i7] > 1.0f) {
                            fArr4[i7] = 1.0f;
                        }
                        invalidate();
                    }
                } else {
                    float[] fArr5 = this.volumeAlphas;
                    if (fArr5[i7] > 0.0f) {
                        fArr5[i7] = fArr5[i7] - (((float) j) / 180.0f);
                        if (fArr5[i7] < 0.0f) {
                            fArr5[i7] = 0.0f;
                        }
                        invalidate();
                    }
                }
                i5 = i7 + 1;
                Canvas canvas2 = canvas;
                i3 = 1;
                f2 = 1.0f;
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
                    this.duration = (float) (Utilities.random.nextInt(700) + 500);
                    this.time = 0.0f;
                    if (this.targetX == -1.0f) {
                        setTarget();
                    }
                    this.startX = this.targetX;
                    this.startY = this.targetY;
                    setTarget();
                }
                float f2 = (float) j;
                float access$600 = this.time + ((BlobDrawable.GRADIENT_SPEED_MIN + 0.5f) * f2) + (f2 * BlobDrawable.GRADIENT_SPEED_MAX * 2.0f * GroupCallActivity.this.amplitude);
                this.time = access$600;
                float f3 = this.duration;
                if (access$600 > f3) {
                    this.time = f3;
                }
                float interpolation = CubicBezierInterpolator.EASE_OUT.getInterpolation(this.time / f3);
                float f4 = (float) i3;
                float f5 = this.startX;
                float f6 = (((float) i2) + ((f5 + ((this.targetX - f5) * interpolation)) * f4)) - 200.0f;
                float f7 = this.startY;
                float f8 = (((float) i) + (f4 * (f7 + ((this.targetY - f7) * interpolation)))) - 200.0f;
                int i4 = this.currentState;
                float dp = (((float) AndroidUtilities.dp(122.0f)) / 400.0f) * (i4 == 2 ? 1.0f : i4 == 1 ? 4.0f : 2.5f);
                this.matrix.reset();
                this.matrix.postTranslate(f6, f8);
                this.matrix.postScale(dp, dp, f6 + 200.0f, f8 + 200.0f);
                this.shader.setLocalMatrix(this.matrix);
            }
        }

        private void setTarget() {
            int i = this.currentState;
            if (i == 2) {
                this.targetX = ((((float) Utilities.random.nextInt(100)) * 0.25f) / 100.0f) + 0.85f;
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
                while (i3 < childCount) {
                    View childAt = this.listView.getChildAt(i3);
                    if (childAt instanceof GroupCallUserCell) {
                        ((GroupCallUserCell) childAt).applyParticipantChanges(true);
                    }
                    i3++;
                }
                if (this.scrimView != null) {
                    this.delayedGroupCallUpdated = true;
                } else {
                    applyCallParticipantUpdates();
                }
                if (this.actionBar != null) {
                    this.actionBar.setSubtitle(LocaleController.formatPluralString("Members", this.call.call.participants_count + (this.listAdapter.addSelfToCounter() ? 1 : 0)));
                }
                updateState(true, objArr[2].booleanValue());
            }
        } else if (i == NotificationCenter.webRtcMicAmplitudeEvent) {
            float floatValue = objArr[0].floatValue();
            setAmplitude((double) (4000.0f * floatValue));
            if (this.listView != null && (tLRPC$TL_groupCallParticipant = this.call.participants.get(this.accountInstance.getUserConfig().getClientUserId())) != null) {
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
                if ("ANONYMOUS_CALLS_DISABLED".equals(str2) || "GROUPCALL_ANONYMOUS_FORBIDDEN".equals(str2)) {
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
            setOldRows(listAdapter2.addMemberRow, this.listAdapter.selfUserRow, this.listAdapter.usersStartRow, this.listAdapter.usersEndRow, this.listAdapter.invitedStartRow, this.listAdapter.invitedEndRow);
            this.listAdapter.updateRows();
            DiffUtil.calculateDiff(this.diffUtilsCallback).dispatchUpdatesTo((ListUpdateCallback) updateCallback);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            this.listAdapter.notifyDataSetChanged();
        }
        this.call.saveActiveDates();
        if (view != null) {
            FileLog.d("scroll to " + i + " top = " + (view.getTop() - this.listView.getPaddingTop()));
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

    private void updateItems() {
        boolean z;
        TLRPC$Chat chat = this.accountInstance.getMessagesController().getChat(Integer.valueOf(this.currentChat.id));
        if (chat != null) {
            this.currentChat = chat;
        }
        int i = 1;
        int i2 = 8;
        int i3 = 0;
        if (ChatObject.canUserDoAdminAction(this.currentChat, 3)) {
            this.inviteItem.setVisibility(0);
            z = true;
        } else {
            this.inviteItem.setVisibility(8);
            z = false;
        }
        if (ChatObject.canManageCalls(this.currentChat)) {
            this.leaveItem.setVisibility(0);
            z = true;
        } else {
            this.leaveItem.setVisibility(8);
        }
        if (!ChatObject.canManageCalls(this.currentChat) || !this.call.call.can_change_join_muted) {
            this.everyoneItem.setVisibility(8);
            this.adminItem.setVisibility(8);
            this.dividerItem.setVisibility(8);
        } else {
            this.everyoneItem.setVisibility(0);
            this.adminItem.setVisibility(0);
            this.dividerItem.setVisibility(0);
            z = true;
        }
        ActionBarMenuItem actionBarMenuItem = this.otherItem;
        if (z) {
            i2 = 0;
        }
        actionBarMenuItem.setVisibility(i2);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.menuItemsContainer.getLayoutParams();
        if (!z) {
            i3 = AndroidUtilities.dp(6.0f);
        }
        layoutParams.rightMargin = i3;
        ActionBar actionBar2 = this.actionBar;
        int dp = AndroidUtilities.dp(48.0f);
        if (z) {
            i = 2;
        }
        actionBar2.setTitleRightMargin(dp * i);
    }

    /* access modifiers changed from: protected */
    public void makeFocusable(EditTextBoldCursor editTextBoldCursor, boolean z) {
        if (!this.enterEventSent) {
            BaseFragment baseFragment = this.parentActivity.getActionBarLayout().fragmentsStack.get(this.parentActivity.getActionBarLayout().fragmentsStack.size() - 1);
            if (baseFragment instanceof ChatActivity) {
                boolean needEnterText = ((ChatActivity) baseFragment).needEnterText();
                this.enterEventSent = true;
                this.anyEnterEventSent = true;
                AndroidUtilities.runOnUIThread(new Runnable(editTextBoldCursor, z) {
                    public final /* synthetic */ EditTextBoldCursor f$1;
                    public final /* synthetic */ boolean f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run() {
                        GroupCallActivity.this.lambda$makeFocusable$4$GroupCallActivity(this.f$1, this.f$2);
                    }
                }, needEnterText ? 200 : 0);
                return;
            }
            this.enterEventSent = true;
            this.anyEnterEventSent = true;
            this.groupVoipInviteAlert.setFocusable(true);
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$makeFocusable$4 */
    public /* synthetic */ void lambda$makeFocusable$4$GroupCallActivity(EditTextBoldCursor editTextBoldCursor, boolean z) {
        GroupVoipInviteAlert groupVoipInviteAlert2 = this.groupVoipInviteAlert;
        if (groupVoipInviteAlert2 != null) {
            groupVoipInviteAlert2.setFocusable(true);
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

    public static void create(LaunchActivity launchActivity, AccountInstance accountInstance2) {
        ChatObject.Call call2;
        TLRPC$Chat chat;
        if (groupCallInstance == null && VoIPService.getSharedInstance() != null && (call2 = VoIPService.getSharedInstance().groupCall) != null && (chat = accountInstance2.getMessagesController().getChat(Integer.valueOf(call2.chatId))) != null) {
            GroupCallActivity groupCallActivity = new GroupCallActivity(launchActivity, accountInstance2, call2, chat);
            groupCallInstance = groupCallActivity;
            groupCallActivity.parentActivity = launchActivity;
            groupCallActivity.show();
        }
    }

    /* JADX WARNING: Illegal instructions before constructor call */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public GroupCallActivity(android.content.Context r29, org.telegram.messenger.AccountInstance r30, org.telegram.messenger.ChatObject.Call r31, org.telegram.tgnet.TLRPC$Chat r32) {
        /*
            r28 = this;
            r0 = r28
            r1 = r29
            r2 = r31
            r3 = 0
            r0.<init>(r1, r3)
            r4 = 2
            android.widget.TextView[] r5 = new android.widget.TextView[r4]
            r0.muteLabel = r5
            android.widget.TextView[] r5 = new android.widget.TextView[r4]
            r0.muteSubLabel = r5
            org.telegram.ui.Components.UndoView[] r5 = new org.telegram.ui.Components.UndoView[r4]
            r0.undoView = r5
            android.graphics.RectF r5 = new android.graphics.RectF
            r5.<init>()
            r0.rect = r5
            android.graphics.Paint r5 = new android.graphics.Paint
            r6 = 1
            r5.<init>(r6)
            r0.listViewBackgroundPaint = r5
            java.util.ArrayList r5 = new java.util.ArrayList
            r5.<init>()
            r0.oldParticipants = r5
            java.util.ArrayList r5 = new java.util.ArrayList
            r5.<init>()
            r0.oldInvited = r5
            r0.muteButtonState = r3
            android.graphics.Paint r5 = new android.graphics.Paint
            r7 = 7
            r5.<init>(r7)
            r0.paint = r5
            android.graphics.Paint r5 = new android.graphics.Paint
            r5.<init>(r7)
            r0.paintTmp = r5
            android.graphics.Paint r5 = new android.graphics.Paint
            r5.<init>(r6)
            r0.leaveBackgroundPaint = r5
            r5 = 4
            org.telegram.ui.GroupCallActivity$WeavingState[] r7 = new org.telegram.ui.GroupCallActivity.WeavingState[r5]
            r0.states = r7
            r7 = 1065353216(0x3var_, float:1.0)
            r0.switchProgress = r7
            r0.invalidateColors = r6
            r8 = 3
            int[] r9 = new int[r8]
            r0.colorsTmp = r9
            org.telegram.ui.-$$Lambda$GroupCallActivity$Fejzw3-BitRkLCnwqEMTIYvTsgw r9 = org.telegram.ui.$$Lambda$GroupCallActivity$Fejzw3BitRkLCnwqEMTIYvTsgw.INSTANCE
            r0.unmuteRunnable = r9
            org.telegram.ui.-$$Lambda$GroupCallActivity$kOH_8vLsAK1_zQgyxcm7-Ylrcfc r9 = new org.telegram.ui.-$$Lambda$GroupCallActivity$kOH_8vLsAK1_zQgyxcm7-Ylrcfc
            r9.<init>()
            r0.pressRunnable = r9
            org.telegram.ui.GroupCallActivity$20 r9 = new org.telegram.ui.GroupCallActivity$20
            r9.<init>()
            r0.diffUtilsCallback = r9
            r9 = r30
            r0.accountInstance = r9
            r0.call = r2
            r10 = r32
            r0.currentChat = r10
            int r9 = r30.getCurrentAccount()
            r0.currentAccount = r9
            r0.drawNavigationBar = r6
            r0.scrollNavBar = r6
            r9 = 0
            r0.navBarColorKey = r9
            org.telegram.ui.GroupCallActivity$2 r10 = new org.telegram.ui.GroupCallActivity$2
            r10.<init>()
            r0.scrimPaint = r10
            org.telegram.ui.-$$Lambda$GroupCallActivity$tEYP7wtzr5YiwMmulTrNZ3C8BBQ r10 = new org.telegram.ui.-$$Lambda$GroupCallActivity$tEYP7wtzr5YiwMmulTrNZ3C8BBQ
            r10.<init>()
            r0.setOnDismissListener(r10)
            r10 = 75
            r0.setDimBehindAlpha(r10)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_groupCallParticipant> r10 = r0.oldParticipants
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_groupCallParticipant> r11 = r2.sortedParticipants
            r10.addAll(r11)
            java.util.ArrayList<java.lang.Integer> r10 = r0.oldInvited
            java.util.ArrayList<java.lang.Integer> r11 = r2.invitedUsers
            r10.addAll(r11)
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r10 = new org.telegram.tgnet.TLRPC$TL_groupCallParticipant
            r10.<init>()
            r0.selfDummyParticipant = r10
            org.telegram.messenger.AccountInstance r11 = r0.accountInstance
            org.telegram.messenger.UserConfig r11 = r11.getUserConfig()
            int r11 = r11.getClientUserId()
            r10.user_id = r11
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r10 = r0.selfDummyParticipant
            r10.muted = r6
            r10.can_self_unmute = r6
            org.telegram.messenger.AccountInstance r11 = r0.accountInstance
            org.telegram.tgnet.ConnectionsManager r11 = r11.getConnectionsManager()
            int r11 = r11.getCurrentTime()
            r10.date = r11
            org.telegram.messenger.voip.VoIPService r10 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            int r10 = r10.getCallState()
            r0.currentCallState = r10
            org.telegram.ui.-$$Lambda$GroupCallActivity$7sxUaVWkb7WBE2tDv2GtdDe0WPE r10 = new org.telegram.ui.-$$Lambda$GroupCallActivity$7sxUaVWkb7WBE2tDv2GtdDe0WPE
            r10.<init>(r2)
            org.telegram.messenger.voip.VoIPService.audioLevelsCallback = r10
            org.telegram.messenger.AccountInstance r10 = r0.accountInstance
            org.telegram.messenger.NotificationCenter r10 = r10.getNotificationCenter()
            int r11 = org.telegram.messenger.NotificationCenter.groupCallUpdated
            r10.addObserver(r0, r11)
            org.telegram.messenger.AccountInstance r10 = r0.accountInstance
            org.telegram.messenger.NotificationCenter r10 = r10.getNotificationCenter()
            int r11 = org.telegram.messenger.NotificationCenter.needShowAlert
            r10.addObserver(r0, r11)
            org.telegram.messenger.AccountInstance r10 = r0.accountInstance
            org.telegram.messenger.NotificationCenter r10 = r10.getNotificationCenter()
            int r11 = org.telegram.messenger.NotificationCenter.chatInfoDidLoad
            r10.addObserver(r0, r11)
            org.telegram.messenger.AccountInstance r10 = r0.accountInstance
            org.telegram.messenger.NotificationCenter r10 = r10.getNotificationCenter()
            int r11 = org.telegram.messenger.NotificationCenter.didLoadChatAdmins
            r10.addObserver(r0, r11)
            org.telegram.messenger.AccountInstance r10 = r0.accountInstance
            org.telegram.messenger.NotificationCenter r10 = r10.getNotificationCenter()
            int r11 = org.telegram.messenger.NotificationCenter.applyGroupCallVisibleParticipants
            r10.addObserver(r0, r11)
            org.telegram.messenger.NotificationCenter r10 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r11 = org.telegram.messenger.NotificationCenter.webRtcMicAmplitudeEvent
            r10.addObserver(r0, r11)
            org.telegram.messenger.NotificationCenter r10 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r11 = org.telegram.messenger.NotificationCenter.didEndCall
            r10.addObserver(r0, r11)
            android.content.res.Resources r10 = r29.getResources()
            r11 = 2131165994(0x7var_a, float:1.794622E38)
            android.graphics.drawable.Drawable r10 = r10.getDrawable(r11)
            android.graphics.drawable.Drawable r10 = r10.mutate()
            r0.shadowDrawable = r10
            org.telegram.ui.Components.RLottieDrawable r10 = new org.telegram.ui.Components.RLottieDrawable
            r11 = 1105199104(0x41e00000, float:28.0)
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r11)
            r11 = 1108869120(0x42180000, float:38.0)
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r11)
            r12 = 2131558493(0x7f0d005d, float:1.8742303E38)
            java.lang.String r13 = "NUM"
            r16 = 1
            r17 = 0
            r11 = r10
            r11.<init>((int) r12, (java.lang.String) r13, (int) r14, (int) r15, (boolean) r16, (int[]) r17)
            r0.bigMicDrawable = r10
            org.telegram.ui.GroupCallActivity$3 r10 = new org.telegram.ui.GroupCallActivity$3
            r10.<init>(r1)
            r0.containerView = r10
            r10.setWillNotDraw(r3)
            android.view.ViewGroup r10 = r0.containerView
            int r11 = r0.backgroundPaddingLeft
            r10.setPadding(r11, r3, r11, r3)
            android.view.ViewGroup r10 = r0.containerView
            r10.setKeepScreenOn(r6)
            android.view.ViewGroup r10 = r0.containerView
            r10.setClipChildren(r3)
            org.telegram.ui.GroupCallActivity$4 r10 = new org.telegram.ui.GroupCallActivity$4
            r10.<init>(r1)
            r0.listView = r10
            r10.setClipToPadding(r3)
            org.telegram.ui.Components.RecyclerListView r10 = r0.listView
            r10.setClipChildren(r3)
            org.telegram.ui.GroupCallActivity$5 r10 = new org.telegram.ui.GroupCallActivity$5
            r10.<init>()
            r10.setDelayAnimations(r3)
            org.telegram.ui.Components.RecyclerListView r11 = r0.listView
            r11.setItemAnimator(r10)
            org.telegram.ui.Components.RecyclerListView r10 = r0.listView
            org.telegram.ui.GroupCallActivity$6 r11 = new org.telegram.ui.GroupCallActivity$6
            r11.<init>(r2)
            r10.setOnScrollListener(r11)
            org.telegram.ui.Components.RecyclerListView r10 = r0.listView
            r10.setVerticalScrollBarEnabled(r3)
            org.telegram.ui.Components.RecyclerListView r10 = r0.listView
            org.telegram.ui.Components.FillLastLinearLayoutManager r15 = new org.telegram.ui.Components.FillLastLinearLayoutManager
            android.content.Context r12 = r28.getContext()
            org.telegram.ui.Components.RecyclerListView r14 = r0.listView
            r13 = 1
            r16 = 0
            r17 = 0
            r11 = r15
            r18 = r14
            r14 = r16
            r8 = r15
            r15 = r17
            r16 = r18
            r11.<init>(r12, r13, r14, r15, r16)
            r0.layoutManager = r8
            r10.setLayoutManager(r8)
            org.telegram.ui.Components.FillLastLinearLayoutManager r8 = r0.layoutManager
            r8.setBind(r3)
            android.view.ViewGroup r8 = r0.containerView
            org.telegram.ui.Components.RecyclerListView r10 = r0.listView
            r11 = -1
            r12 = -1082130432(0xffffffffbvar_, float:-1.0)
            r13 = 51
            r14 = 1096810496(0x41600000, float:14.0)
            r15 = 1096810496(0x41600000, float:14.0)
            r16 = 1096810496(0x41600000, float:14.0)
            r17 = 1130823680(0x43670000, float:231.0)
            android.widget.FrameLayout$LayoutParams r11 = org.telegram.ui.Components.LayoutHelper.createFrame(r11, r12, r13, r14, r15, r16, r17)
            r8.addView(r10, r11)
            org.telegram.ui.Components.RecyclerListView r8 = r0.listView
            org.telegram.ui.GroupCallActivity$ListAdapter r10 = new org.telegram.ui.GroupCallActivity$ListAdapter
            r10.<init>(r1)
            r0.listAdapter = r10
            r8.setAdapter(r10)
            org.telegram.ui.Components.RecyclerListView r8 = r0.listView
            r10 = 13
            r8.setTopBottomSelectorRadius(r10)
            org.telegram.ui.Components.RecyclerListView r8 = r0.listView
            java.lang.String r10 = "voipgroup_listSelector"
            int r11 = org.telegram.ui.ActionBar.Theme.getColor(r10)
            r8.setSelectorDrawableColor(r11)
            org.telegram.ui.Components.RecyclerListView r8 = r0.listView
            org.telegram.ui.-$$Lambda$GroupCallActivity$Qvri_Tr-KlpIvJfe0RJhjxpsesY r11 = new org.telegram.ui.-$$Lambda$GroupCallActivity$Qvri_Tr-KlpIvJfe0RJhjxpsesY
            r11.<init>(r2)
            r8.setOnItemClickListener((org.telegram.ui.Components.RecyclerListView.OnItemClickListenerExtended) r11)
            org.telegram.ui.Components.RecyclerListView r8 = r0.listView
            org.telegram.ui.-$$Lambda$GroupCallActivity$_H6ECLASSNAMERerQtEYBDA3QiVTDX3Kg r11 = new org.telegram.ui.-$$Lambda$GroupCallActivity$_H6ECLASSNAMERerQtEYBDA3QiVTDX3Kg
            r11.<init>()
            r8.setOnItemLongClickListener((org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener) r11)
            org.telegram.ui.GroupCallActivity$8 r8 = new org.telegram.ui.GroupCallActivity$8
            r8.<init>(r1)
            r0.buttonsContainer = r8
            r8.setWillNotDraw(r3)
            android.view.ViewGroup r8 = r0.containerView
            android.widget.FrameLayout r11 = r0.buttonsContainer
            r12 = -1
            r13 = 231(0xe7, float:3.24E-43)
            r14 = 83
            android.widget.FrameLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createFrame(r12, r13, r14)
            r8.addView(r11, r13)
            java.lang.String r8 = "voipgroup_unmuteButton2"
            int r8 = org.telegram.ui.ActionBar.Theme.getColor(r8)
            int r11 = android.graphics.Color.red(r8)
            int r13 = android.graphics.Color.green(r8)
            int r8 = android.graphics.Color.blue(r8)
            android.graphics.Matrix r14 = new android.graphics.Matrix
            r14.<init>()
            r0.radialMatrix = r14
            android.graphics.RadialGradient r14 = new android.graphics.RadialGradient
            r15 = 1126170624(0x43200000, float:160.0)
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r15)
            float r15 = (float) r15
            int[] r7 = new int[r4]
            r12 = 50
            int r12 = android.graphics.Color.argb(r12, r11, r13, r8)
            r7[r3] = r12
            int r8 = android.graphics.Color.argb(r3, r11, r13, r8)
            r7[r6] = r8
            android.graphics.Shader$TileMode r25 = android.graphics.Shader.TileMode.CLAMP
            r20 = 0
            r21 = 0
            r24 = 0
            r19 = r14
            r22 = r15
            r23 = r7
            r19.<init>(r20, r21, r22, r23, r24, r25)
            r0.radialGradient = r14
            android.graphics.Paint r7 = new android.graphics.Paint
            r7.<init>(r6)
            r0.radialPaint = r7
            android.graphics.RadialGradient r8 = r0.radialGradient
            r7.setShader(r8)
            org.telegram.ui.Components.BlobDrawable r7 = new org.telegram.ui.Components.BlobDrawable
            r8 = 9
            r7.<init>(r8)
            r0.tinyWaveDrawable = r7
            org.telegram.ui.Components.BlobDrawable r8 = new org.telegram.ui.Components.BlobDrawable
            r11 = 12
            r8.<init>(r11)
            r0.bigWaveDrawable = r8
            r12 = 1115160576(0x42780000, float:62.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            float r12 = (float) r12
            r7.minRadius = r12
            r12 = 1116733440(0x42900000, float:72.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            float r12 = (float) r12
            r7.maxRadius = r12
            r7.generateBlob()
            r12 = 1115815936(0x42820000, float:65.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            float r12 = (float) r12
            r8.minRadius = r12
            r12 = 1117126656(0x42960000, float:75.0)
            int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
            float r12 = (float) r12
            r8.maxRadius = r12
            r8.generateBlob()
            android.graphics.Paint r7 = r7.paint
            java.lang.String r12 = "voipgroup_unmuteButton"
            int r13 = org.telegram.ui.ActionBar.Theme.getColor(r12)
            r14 = 38
            int r13 = androidx.core.graphics.ColorUtils.setAlphaComponent(r13, r14)
            r7.setColor(r13)
            android.graphics.Paint r7 = r8.paint
            int r8 = org.telegram.ui.ActionBar.Theme.getColor(r12)
            r12 = 76
            int r8 = androidx.core.graphics.ColorUtils.setAlphaComponent(r8, r12)
            r7.setColor(r8)
            org.telegram.ui.Components.voip.VoIPToggleButton r7 = new org.telegram.ui.Components.voip.VoIPToggleButton
            r7.<init>(r1)
            r0.soundButton = r7
            r7.setCheckable(r6)
            org.telegram.ui.Components.voip.VoIPToggleButton r7 = r0.soundButton
            r7.setTextSize(r11)
            android.widget.FrameLayout r7 = r0.buttonsContainer
            org.telegram.ui.Components.voip.VoIPToggleButton r8 = r0.soundButton
            r12 = 68
            r13 = 1119092736(0x42b40000, float:90.0)
            android.widget.FrameLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createFrame(r12, r13)
            r7.addView(r8, r13)
            org.telegram.ui.Components.voip.VoIPToggleButton r7 = r0.soundButton
            org.telegram.ui.-$$Lambda$GroupCallActivity$onClwEf4q4phWZrpZQ1GtYC_tCo r8 = new org.telegram.ui.-$$Lambda$GroupCallActivity$onClwEf4q4phWZrpZQ1GtYC_tCo
            r8.<init>()
            r7.setOnClickListener(r8)
            org.telegram.ui.Components.voip.VoIPToggleButton r7 = new org.telegram.ui.Components.voip.VoIPToggleButton
            r7.<init>(r1)
            r0.leaveButton = r7
            r7.setDrawBackground(r3)
            org.telegram.ui.Components.voip.VoIPToggleButton r7 = r0.leaveButton
            r7.setTextSize(r11)
            org.telegram.ui.Components.voip.VoIPToggleButton r7 = r0.leaveButton
            java.lang.String r8 = "voipgroup_leaveButton"
            int r22 = org.telegram.ui.ActionBar.Theme.getColor(r8)
            java.lang.String r11 = "VoipGroupLeave"
            r13 = 2131627942(0x7f0e0fa6, float:1.8883163E38)
            java.lang.String r25 = org.telegram.messenger.LocaleController.getString(r11, r13)
            r20 = 2131165318(0x7var_, float:1.794485E38)
            r21 = -1
            r23 = 1050253722(0x3e99999a, float:0.3)
            r24 = 0
            r26 = 0
            r27 = 0
            r19 = r7
            r19.setData(r20, r21, r22, r23, r24, r25, r26, r27)
            android.widget.FrameLayout r7 = r0.buttonsContainer
            org.telegram.ui.Components.voip.VoIPToggleButton r11 = r0.leaveButton
            r13 = 1117782016(0x42a00000, float:80.0)
            android.widget.FrameLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createFrame(r12, r13)
            r7.addView(r11, r12)
            org.telegram.ui.Components.voip.VoIPToggleButton r7 = r0.leaveButton
            org.telegram.ui.-$$Lambda$GroupCallActivity$tSjI6SD3O_hXLBkiFode-FDcY9Y r11 = new org.telegram.ui.-$$Lambda$GroupCallActivity$tSjI6SD3O_hXLBkiFode-FDcY9Y
            r11.<init>(r1)
            r7.setOnClickListener(r11)
            org.telegram.ui.GroupCallActivity$9 r7 = new org.telegram.ui.GroupCallActivity$9
            r7.<init>(r1)
            r0.muteButton = r7
            org.telegram.ui.Components.RLottieDrawable r11 = r0.bigMicDrawable
            r7.setAnimation(r11)
            org.telegram.ui.Components.RLottieImageView r7 = r0.muteButton
            android.widget.ImageView$ScaleType r11 = android.widget.ImageView.ScaleType.CENTER
            r7.setScaleType(r11)
            android.widget.FrameLayout r7 = r0.buttonsContainer
            org.telegram.ui.Components.RLottieImageView r11 = r0.muteButton
            r12 = 122(0x7a, float:1.71E-43)
            r13 = 49
            android.widget.FrameLayout$LayoutParams r12 = org.telegram.ui.Components.LayoutHelper.createFrame(r12, r12, r13)
            r7.addView(r11, r12)
            org.telegram.ui.Components.RLottieImageView r7 = r0.muteButton
            org.telegram.ui.-$$Lambda$GroupCallActivity$u4roEPnPeT7DyJ-i_bWGO552ynY r11 = new org.telegram.ui.-$$Lambda$GroupCallActivity$u4roEPnPeT7DyJ-i_bWGO552ynY
            r11.<init>()
            r7.setOnClickListener(r11)
            org.telegram.ui.Components.RadialProgressView r7 = new org.telegram.ui.Components.RadialProgressView
            r7.<init>(r1)
            r0.radialProgressView = r7
            r11 = 1121714176(0x42dCLASSNAME, float:110.0)
            int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
            r7.setSize(r11)
            org.telegram.ui.Components.RadialProgressView r7 = r0.radialProgressView
            r11 = 1082130432(0x40800000, float:4.0)
            r7.setStrokeWidth(r11)
            org.telegram.ui.Components.RadialProgressView r7 = r0.radialProgressView
            java.lang.String r11 = "voipgroup_connectingProgress"
            int r11 = org.telegram.ui.ActionBar.Theme.getColor(r11)
            r7.setProgressColor(r11)
            r7 = 0
        L_0x0397:
            java.lang.String r11 = "voipgroup_actionBarItems"
            if (r7 >= r4) goto L_0x0422
            android.widget.TextView[] r12 = r0.muteLabel
            android.widget.TextView r13 = new android.widget.TextView
            r13.<init>(r1)
            r12[r7] = r13
            android.widget.TextView[] r12 = r0.muteLabel
            r12 = r12[r7]
            int r13 = org.telegram.ui.ActionBar.Theme.getColor(r11)
            r12.setTextColor(r13)
            android.widget.TextView[] r12 = r0.muteLabel
            r12 = r12[r7]
            r13 = 1099956224(0x41900000, float:18.0)
            r12.setTextSize(r6, r13)
            android.widget.TextView[] r12 = r0.muteLabel
            r12 = r12[r7]
            r12.setGravity(r6)
            android.widget.FrameLayout r12 = r0.buttonsContainer
            android.widget.TextView[] r13 = r0.muteLabel
            r13 = r13[r7]
            r19 = -2
            r20 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r21 = 81
            r22 = 0
            r23 = 0
            r24 = 0
            r25 = 1104150528(0x41d00000, float:26.0)
            android.widget.FrameLayout$LayoutParams r14 = org.telegram.ui.Components.LayoutHelper.createFrame(r19, r20, r21, r22, r23, r24, r25)
            r12.addView(r13, r14)
            android.widget.TextView[] r12 = r0.muteSubLabel
            android.widget.TextView r13 = new android.widget.TextView
            r13.<init>(r1)
            r12[r7] = r13
            android.widget.TextView[] r12 = r0.muteSubLabel
            r12 = r12[r7]
            int r11 = org.telegram.ui.ActionBar.Theme.getColor(r11)
            r12.setTextColor(r11)
            android.widget.TextView[] r11 = r0.muteSubLabel
            r11 = r11[r7]
            r12 = 1094713344(0x41400000, float:12.0)
            r11.setTextSize(r6, r12)
            android.widget.TextView[] r11 = r0.muteSubLabel
            r11 = r11[r7]
            r11.setGravity(r6)
            android.widget.FrameLayout r11 = r0.buttonsContainer
            android.widget.TextView[] r12 = r0.muteSubLabel
            r12 = r12[r7]
            r25 = 1092616192(0x41200000, float:10.0)
            android.widget.FrameLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createFrame(r19, r20, r21, r22, r23, r24, r25)
            r11.addView(r12, r13)
            if (r7 != r6) goto L_0x041e
            android.widget.TextView[] r11 = r0.muteLabel
            r11 = r11[r7]
            r11.setVisibility(r5)
            android.widget.TextView[] r11 = r0.muteSubLabel
            r11 = r11[r7]
            r11.setVisibility(r5)
        L_0x041e:
            int r7 = r7 + 1
            goto L_0x0397
        L_0x0422:
            org.telegram.ui.GroupCallActivity$10 r7 = new org.telegram.ui.GroupCallActivity$10
            r7.<init>(r1)
            r0.actionBar = r7
            r12 = 2131165468(0x7var_c, float:1.7945154E38)
            r7.setBackButtonImage(r12)
            org.telegram.ui.ActionBar.ActionBar r7 = r0.actionBar
            r7.setOccupyStatusBar(r3)
            org.telegram.ui.ActionBar.ActionBar r7 = r0.actionBar
            r7.setAllowOverlayTitle(r3)
            org.telegram.ui.ActionBar.ActionBar r7 = r0.actionBar
            int r12 = org.telegram.ui.ActionBar.Theme.getColor(r11)
            r7.setItemsColor(r12, r3)
            org.telegram.ui.ActionBar.ActionBar r7 = r0.actionBar
            java.lang.String r12 = "actionBarActionModeDefaultSelector"
            int r12 = org.telegram.ui.ActionBar.Theme.getColor(r12)
            r7.setItemsBackgroundColor(r12, r3)
            org.telegram.ui.ActionBar.ActionBar r7 = r0.actionBar
            int r12 = org.telegram.ui.ActionBar.Theme.getColor(r11)
            r7.setTitleColor(r12)
            org.telegram.ui.ActionBar.ActionBar r7 = r0.actionBar
            java.lang.String r12 = "voipgroup_lastSeenTextUnscrolled"
            int r12 = org.telegram.ui.ActionBar.Theme.getColor(r12)
            r7.setSubtitleColor(r12)
            org.telegram.ui.ActionBar.ActionBar r7 = r0.actionBar
            org.telegram.ui.GroupCallActivity$11 r12 = new org.telegram.ui.GroupCallActivity$11
            r12.<init>(r2)
            r7.setActionBarMenuOnItemClick(r12)
            org.telegram.ui.ActionBar.ActionBar r7 = r0.actionBar
            r12 = 0
            r7.setAlpha(r12)
            org.telegram.ui.ActionBar.ActionBar r7 = r0.actionBar
            android.widget.ImageView r7 = r7.getBackButton()
            r13 = 1063675494(0x3var_, float:0.9)
            r7.setScaleX(r13)
            org.telegram.ui.ActionBar.ActionBar r7 = r0.actionBar
            android.widget.ImageView r7 = r7.getBackButton()
            r7.setScaleY(r13)
            org.telegram.ui.ActionBar.ActionBar r7 = r0.actionBar
            android.widget.ImageView r7 = r7.getBackButton()
            r13 = 1096810496(0x41600000, float:14.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
            int r13 = -r13
            float r13 = (float) r13
            r7.setTranslationX(r13)
            org.telegram.ui.ActionBar.ActionBar r7 = r0.actionBar
            org.telegram.ui.ActionBar.SimpleTextView r7 = r7.getTitleTextView()
            r13 = 1102577664(0x41b80000, float:23.0)
            int r13 = org.telegram.messenger.AndroidUtilities.dp(r13)
            float r13 = (float) r13
            r7.setTranslationY(r13)
            org.telegram.ui.ActionBar.ActionBar r7 = r0.actionBar
            org.telegram.ui.ActionBar.SimpleTextView r7 = r7.getSubtitleTextView()
            r13 = 1101004800(0x41a00000, float:20.0)
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r13)
            float r14 = (float) r14
            r7.setTranslationY(r14)
            org.telegram.ui.ActionBar.ActionBarMenuItem r7 = new org.telegram.ui.ActionBar.ActionBarMenuItem
            int r14 = org.telegram.ui.ActionBar.Theme.getColor(r11)
            r7.<init>(r1, r9, r3, r14)
            r0.otherItem = r7
            r7.setLongClickEnabled(r3)
            org.telegram.ui.ActionBar.ActionBarMenuItem r7 = r0.otherItem
            r14 = 2131165475(0x7var_, float:1.7945168E38)
            r7.setIcon((int) r14)
            org.telegram.ui.ActionBar.ActionBarMenuItem r7 = r0.otherItem
            r14 = 2131623986(0x7f0e0032, float:1.8875139E38)
            java.lang.String r15 = "AccDescrMoreOptions"
            java.lang.String r14 = org.telegram.messenger.LocaleController.getString(r15, r14)
            r7.setContentDescription(r14)
            org.telegram.ui.ActionBar.ActionBarMenuItem r7 = r0.otherItem
            r7.setSubMenuOpenSide(r4)
            org.telegram.ui.ActionBar.ActionBarMenuItem r7 = r0.otherItem
            org.telegram.ui.-$$Lambda$GroupCallActivity$swq6kQSduBPuxEvWAnKzenFLW3g r14 = new org.telegram.ui.-$$Lambda$GroupCallActivity$swq6kQSduBPuxEvWAnKzenFLW3g
            r14.<init>()
            r7.setDelegate(r14)
            org.telegram.ui.ActionBar.ActionBarMenuItem r7 = r0.otherItem
            java.lang.String r14 = "voipgroup_actionBarItemsSelector"
            int r15 = org.telegram.ui.ActionBar.Theme.getColor(r14)
            r5 = 6
            android.graphics.drawable.Drawable r5 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r15, r5)
            r7.setBackgroundDrawable(r5)
            org.telegram.ui.ActionBar.ActionBarMenuItem r5 = r0.otherItem
            org.telegram.ui.-$$Lambda$GroupCallActivity$7jXM7rTvd0NFVgLsvfHI1ZCDFFo r7 = new org.telegram.ui.-$$Lambda$GroupCallActivity$7jXM7rTvd0NFVgLsvfHI1ZCDFFo
            r7.<init>(r2)
            r5.setOnClickListener(r7)
            org.telegram.ui.ActionBar.ActionBarMenuItem r5 = r0.otherItem
            int r7 = org.telegram.ui.ActionBar.Theme.getColor(r11)
            r5.setPopupItemsColor(r7, r3)
            org.telegram.ui.ActionBar.ActionBarMenuItem r5 = r0.otherItem
            int r7 = org.telegram.ui.ActionBar.Theme.getColor(r11)
            r5.setPopupItemsColor(r7, r6)
            org.telegram.ui.ActionBar.ActionBarMenuItem r5 = new org.telegram.ui.ActionBar.ActionBarMenuItem
            int r7 = org.telegram.ui.ActionBar.Theme.getColor(r11)
            r5.<init>(r1, r9, r3, r7)
            r0.pipItem = r5
            r5.setLongClickEnabled(r3)
            org.telegram.ui.ActionBar.ActionBarMenuItem r5 = r0.pipItem
            r7 = 2131165827(0x7var_, float:1.7945882E38)
            r5.setIcon((int) r7)
            org.telegram.ui.ActionBar.ActionBarMenuItem r5 = r0.pipItem
            r7 = 2131624010(0x7f0e004a, float:1.8875188E38)
            java.lang.String r9 = "AccDescrPipMode"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r9, r7)
            r5.setContentDescription(r7)
            org.telegram.ui.ActionBar.ActionBarMenuItem r5 = r0.pipItem
            int r7 = org.telegram.ui.ActionBar.Theme.getColor(r14)
            r9 = 6
            android.graphics.drawable.Drawable r7 = org.telegram.ui.ActionBar.Theme.createSelectorDrawable(r7, r9)
            r5.setBackgroundDrawable(r7)
            org.telegram.ui.ActionBar.ActionBarMenuItem r5 = r0.pipItem
            org.telegram.ui.-$$Lambda$GroupCallActivity$H8y3AsTTCzyy6Vg1B16yTAimQww r7 = new org.telegram.ui.-$$Lambda$GroupCallActivity$H8y3AsTTCzyy6Vg1B16yTAimQww
            r7.<init>()
            r5.setOnClickListener(r7)
            android.widget.TextView r5 = new android.widget.TextView
            r5.<init>(r1)
            r0.titleTextView = r5
            int r7 = org.telegram.ui.ActionBar.Theme.getColor(r11)
            r5.setTextColor(r7)
            android.widget.TextView r5 = r0.titleTextView
            r5.setTextSize(r6, r13)
            android.widget.TextView r5 = r0.titleTextView
            java.lang.String r7 = "fonts/rmedium.ttf"
            android.graphics.Typeface r7 = org.telegram.messenger.AndroidUtilities.getTypeface(r7)
            r5.setTypeface(r7)
            android.widget.TextView r5 = r0.titleTextView
            r7 = 51
            r5.setGravity(r7)
            android.widget.TextView r5 = r0.titleTextView
            r7 = 2131627971(0x7f0e0fc3, float:1.8883221E38)
            java.lang.String r9 = "VoipGroupVoiceChat"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r9, r7)
            r5.setText(r7)
            org.telegram.ui.GroupCallActivity$12 r5 = new org.telegram.ui.GroupCallActivity$12
            r5.<init>(r0, r1)
            r0.actionBarBackground = r5
            r5.setAlpha(r12)
            android.view.ViewGroup r5 = r0.containerView
            android.view.View r7 = r0.actionBarBackground
            r19 = -1
            r20 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r21 = 51
            r22 = 0
            r23 = 0
            r24 = 0
            r25 = 0
            android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r19, r20, r21, r22, r23, r24, r25)
            r5.addView(r7, r9)
            android.view.ViewGroup r5 = r0.containerView
            android.widget.TextView r7 = r0.titleTextView
            r19 = -2
            r22 = 1102577664(0x41b80000, float:23.0)
            r24 = 1111490560(0x42400000, float:48.0)
            android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r19, r20, r21, r22, r23, r24, r25)
            r5.addView(r7, r9)
            android.view.ViewGroup r5 = r0.containerView
            org.telegram.ui.ActionBar.ActionBar r7 = r0.actionBar
            r19 = -1
            r22 = 0
            r24 = 0
            android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r19, r20, r21, r22, r23, r24, r25)
            r5.addView(r7, r9)
            android.widget.LinearLayout r5 = new android.widget.LinearLayout
            r5.<init>(r1)
            r0.menuItemsContainer = r5
            r5.setOrientation(r3)
            org.telegram.ui.ActionBar.ActionBarMenuItem r7 = r0.pipItem
            r9 = 48
            android.widget.LinearLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createLinear(r9, r9)
            r5.addView(r7, r13)
            org.telegram.ui.ActionBar.ActionBarMenuItem r7 = r0.otherItem
            android.widget.LinearLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createLinear(r9, r9)
            r5.addView(r7, r9)
            android.view.ViewGroup r7 = r0.containerView
            r9 = -2
            r13 = -2
            r14 = 53
            android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r9, r13, r14)
            r7.addView(r5, r9)
            android.view.View r5 = new android.view.View
            r5.<init>(r1)
            r0.actionBarShadow = r5
            r5.setAlpha(r12)
            android.view.View r5 = r0.actionBarShadow
            java.lang.String r7 = "dialogShadowLine"
            int r7 = org.telegram.ui.ActionBar.Theme.getColor(r7)
            r5.setBackgroundColor(r7)
            android.view.ViewGroup r5 = r0.containerView
            android.view.View r7 = r0.actionBarShadow
            r9 = -1
            r13 = 1065353216(0x3var_, float:1.0)
            android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r9, r13)
            r5.addView(r7, r9)
            r5 = 0
        L_0x061b:
            if (r5 >= r4) goto L_0x0666
            org.telegram.ui.Components.UndoView[] r7 = r0.undoView
            org.telegram.ui.Components.UndoView r9 = new org.telegram.ui.Components.UndoView
            r9.<init>(r1)
            r7[r5] = r9
            org.telegram.ui.Components.UndoView[] r7 = r0.undoView
            r7 = r7[r5]
            r9 = 1092616192(0x41200000, float:10.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            float r9 = (float) r9
            r7.setAdditionalTranslationY(r9)
            int r7 = android.os.Build.VERSION.SDK_INT
            r9 = 21
            if (r7 < r9) goto L_0x0648
            org.telegram.ui.Components.UndoView[] r7 = r0.undoView
            r7 = r7[r5]
            r9 = 1084227584(0x40a00000, float:5.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            float r9 = (float) r9
            r7.setTranslationZ(r9)
        L_0x0648:
            android.view.ViewGroup r7 = r0.containerView
            org.telegram.ui.Components.UndoView[] r9 = r0.undoView
            r9 = r9[r5]
            r19 = -1
            r20 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r21 = 83
            r22 = 1090519040(0x41000000, float:8.0)
            r23 = 0
            r24 = 1090519040(0x41000000, float:8.0)
            r25 = 1090519040(0x41000000, float:8.0)
            android.widget.FrameLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createFrame(r19, r20, r21, r22, r23, r24, r25)
            r7.addView(r9, r13)
            int r5 = r5 + 1
            goto L_0x061b
        L_0x0666:
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.otherItem
            r5 = 2131627925(0x7f0e0var_, float:1.8883128E38)
            java.lang.String r7 = "VoipGroupAllCanSpeak"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r7, r5)
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r1 = r1.addSubItem((int) r6, (int) r3, (java.lang.CharSequence) r5, (boolean) r6)
            r0.everyoneItem = r1
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.otherItem
            r5 = 2131627953(0x7f0e0fb1, float:1.8883185E38)
            java.lang.String r7 = "VoipGroupOnlyAdminsCanSpeak"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r7, r5)
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r1 = r1.addSubItem((int) r4, (int) r3, (java.lang.CharSequence) r5, (boolean) r6)
            r0.adminItem = r1
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r1 = r0.everyoneItem
            java.lang.String r5 = "voipgroup_checkMenu"
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            r1.setCheckColor(r6)
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r1 = r0.everyoneItem
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            int r7 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            r1.setColors(r6, r7)
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r1 = r0.adminItem
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            r1.setCheckColor(r6)
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r1 = r0.adminItem
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            r1.setColors(r6, r5)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.otherItem
            java.lang.String r5 = "voipgroup_listViewBackground"
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            android.view.View r1 = r1.addDivider(r5)
            r0.dividerItem = r1
            org.telegram.ui.GroupCallActivity$RecordCallDrawable r1 = new org.telegram.ui.GroupCallActivity$RecordCallDrawable
            r1.<init>()
            r0.recordCallDrawable = r1
            org.telegram.ui.ActionBar.ActionBarMenuItem r5 = r0.otherItem
            r20 = 5
            r21 = 0
            r6 = 2131627956(0x7f0e0fb4, float:1.8883191E38)
            java.lang.String r7 = "VoipGroupRecordCall"
            java.lang.String r23 = org.telegram.messenger.LocaleController.getString(r7, r6)
            r24 = 0
            r19 = r5
            r22 = r1
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r1 = r19.addSubItem(r20, r21, r22, r23, r24)
            r0.recordItem = r1
            org.telegram.ui.GroupCallActivity$RecordCallDrawable r5 = r0.recordCallDrawable
            android.widget.ImageView r1 = r1.getImageView()
            r5.setParentView(r1)
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.otherItem
            r5 = 2131165754(0x7var_a, float:1.7945734E38)
            r6 = 2131627961(0x7f0e0fb9, float:1.8883201E38)
            java.lang.String r7 = "VoipGroupShareInviteLink"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
            r7 = 3
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r1 = r1.addSubItem(r7, r5, r6)
            r0.inviteItem = r1
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.otherItem
            r5 = 2131165739(0x7var_b, float:1.7945704E38)
            r6 = 2131627933(0x7f0e0f9d, float:1.8883144E38)
            java.lang.String r7 = "VoipGroupEndChat"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
            r7 = 4
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r1 = r1.addSubItem(r7, r5, r6)
            r0.leaveItem = r1
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.otherItem
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r10)
            r1.setPopupItemsSelectorColor(r5)
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r1 = r0.leaveItem
            java.lang.String r5 = "voipgroup_leaveCallMenu"
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            java.lang.String r6 = "voipgroup_leaveCallMenu"
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r6)
            r1.setColors(r5, r6)
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r1 = r0.inviteItem
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r11)
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r11)
            r1.setColors(r5, r6)
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r1 = r0.recordItem
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r11)
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r11)
            r1.setColors(r5, r6)
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r1 = r0.recordItem
            r5 = 8
            r1.setVisibility(r5)
            org.telegram.ui.GroupCallActivity$ListAdapter r1 = r0.listAdapter
            r1.notifyDataSetChanged()
            org.telegram.ui.GroupCallActivity$ListAdapter r1 = r0.listAdapter
            int r1 = r1.getItemCount()
            r0.oldCount = r1
            org.telegram.ui.ActionBar.ActionBar r1 = r0.actionBar
            org.telegram.tgnet.TLRPC$Chat r5 = r0.currentChat
            java.lang.String r5 = r5.title
            r1.setTitle(r5)
            org.telegram.ui.ActionBar.ActionBar r1 = r0.actionBar
            org.telegram.tgnet.TLRPC$GroupCall r5 = r2.call
            int r5 = r5.participants_count
            org.telegram.ui.GroupCallActivity$ListAdapter r6 = r0.listAdapter
            boolean r6 = r6.addSelfToCounter()
            int r5 = r5 + r6
            java.lang.String r6 = "Participants"
            java.lang.String r5 = org.telegram.messenger.LocaleController.formatPluralString(r6, r5)
            r1.setSubtitle(r5)
            org.telegram.ui.ActionBar.ActionBar r1 = r0.actionBar
            r5 = 1111490560(0x42400000, float:48.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r5 = r5 * 2
            r1.setTitleRightMargin(r5)
            r31.saveActiveDates()
            org.telegram.messenger.voip.VoIPService r1 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            r1.registerStateListener(r0)
            r28.updateItems()
            r0.updateSpeakerPhoneIcon(r3)
            r0.updateState(r3, r3)
            r0.setColorProgress(r12)
            android.graphics.Paint r1 = r0.leaveBackgroundPaint
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r8)
            r1.setColor(r2)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.GroupCallActivity.<init>(android.content.Context, org.telegram.messenger.AccountInstance, org.telegram.messenger.ChatObject$Call, org.telegram.tgnet.TLRPC$Chat):void");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$6 */
    public /* synthetic */ void lambda$new$6$GroupCallActivity(DialogInterface dialogInterface) {
        BaseFragment baseFragment = this.parentActivity.getActionBarLayout().fragmentsStack.get(this.parentActivity.getActionBarLayout().fragmentsStack.size() - 1);
        if (this.anyEnterEventSent && (baseFragment instanceof ChatActivity)) {
            ((ChatActivity) baseFragment).onEditTextDialogClose(true);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$7 */
    public /* synthetic */ void lambda$new$7$GroupCallActivity(ChatObject.Call call2, int[] iArr, float[] fArr, boolean[] zArr) {
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
    /* renamed from: lambda$new$9 */
    public /* synthetic */ void lambda$new$9$GroupCallActivity(ChatObject.Call call2, View view, int i, float f, float f2) {
        TLRPC$ChatFull chatFull;
        if (view instanceof GroupCallUserCell) {
            GroupCallUserCell groupCallUserCell = (GroupCallUserCell) view;
            if (!groupCallUserCell.isSelfUser()) {
                showMenuForCell(groupCallUserCell);
            }
        } else if (view instanceof GroupCallInvitedCell) {
            GroupCallInvitedCell groupCallInvitedCell = (GroupCallInvitedCell) view;
            if (groupCallInvitedCell.getUser() != null) {
                Bundle bundle = new Bundle();
                bundle.putInt("user_id", groupCallInvitedCell.getUser().id);
                this.parentActivity.lambda$runLinkRequest$41(new ProfileActivity(bundle));
                dismiss();
            }
        } else if (i == this.listAdapter.addMemberRow && (chatFull = this.accountInstance.getMessagesController().getChatFull(this.currentChat.id)) != null) {
            this.enterEventSent = false;
            GroupVoipInviteAlert groupVoipInviteAlert2 = new GroupVoipInviteAlert(getContext(), this.accountInstance.getCurrentAccount(), this.currentChat, chatFull, call2.participants, call2.invitedUsersMap);
            this.groupVoipInviteAlert = groupVoipInviteAlert2;
            groupVoipInviteAlert2.setOnDismissListener(new DialogInterface.OnDismissListener() {
                public final void onDismiss(DialogInterface dialogInterface) {
                    GroupCallActivity.this.lambda$null$8$GroupCallActivity(dialogInterface);
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
                        GroupCallActivity.this.makeFocusable(editTextBoldCursor, false);
                    } else {
                        GroupCallActivity.this.makeFocusable(editTextBoldCursor, true);
                    }
                }
            });
            this.groupVoipInviteAlert.show();
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$8 */
    public /* synthetic */ void lambda$null$8$GroupCallActivity(DialogInterface dialogInterface) {
        this.groupVoipInviteAlert = null;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$10 */
    public /* synthetic */ boolean lambda$new$10$GroupCallActivity(View view, int i) {
        if (!(view instanceof GroupCallUserCell)) {
            return false;
        }
        updateItems();
        return ((GroupCallUserCell) view).clickMuteButton();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$11 */
    public /* synthetic */ void lambda$new$11$GroupCallActivity(View view) {
        if (VoIPService.getSharedInstance() != null) {
            VoIPService.getSharedInstance().toggleSpeakerphoneOrShowRouteSheet(getContext(), false);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$12 */
    public /* synthetic */ void lambda$new$12$GroupCallActivity(Context context, View view) {
        updateItems();
        onLeaveClick(context, new Runnable() {
            public final void run() {
                GroupCallActivity.this.dismiss();
            }
        }, false);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$13 */
    public /* synthetic */ void lambda$new$13$GroupCallActivity(View view) {
        int i;
        if (VoIPService.getSharedInstance() != null && (i = this.muteButtonState) != 3) {
            if (i == 2) {
                AndroidUtilities.shakeView(this.muteLabel[0], 2.0f, 0);
                AndroidUtilities.shakeView(this.muteSubLabel[0], 2.0f, 0);
                try {
                    Vibrator vibrator = (Vibrator) this.parentActivity.getSystemService("vibrator");
                    if (vibrator != null) {
                        vibrator.vibrate(200);
                    }
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
            } else if (i == 0) {
                updateMuteButton(1, true);
                VoIPService.getSharedInstance().setMicMute(false, false, true);
                this.muteButton.performHapticFeedback(3, 2);
            } else {
                updateMuteButton(0, true);
                VoIPService.getSharedInstance().setMicMute(true, false, true);
                this.muteButton.performHapticFeedback(3, 2);
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$14 */
    public /* synthetic */ void lambda$new$14$GroupCallActivity(int i) {
        this.actionBar.getActionBarMenuOnItemClick().onItemClick(i);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$15 */
    public /* synthetic */ void lambda$new$15$GroupCallActivity(ChatObject.Call call2, View view) {
        updateItems();
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
        this.otherItem.toggleSubMenu();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$16 */
    public /* synthetic */ void lambda$new$16$GroupCallActivity(View view) {
        if (Build.VERSION.SDK_INT < 23 || Settings.canDrawOverlays(this.parentActivity)) {
            GroupCallPip.clearForce();
            dismiss();
            return;
        }
        AlertsCreator.createDrawOverlayGroupCallPermissionDialog(getContext()).show();
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
        this.dividerItem.setBackgroundColor(offsetColor2);
        this.listViewBackgroundPaint.setColor(offsetColor2);
        this.listView.setGlowColor(offsetColor2);
        int i = this.muteButtonState;
        if (i == 3 || i == 2) {
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
    /* JADX WARNING: Code restructure failed: missing block: B:4:0x003b, code lost:
        r1 = r0.exported_invite;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void getLink(boolean r5) {
        /*
            r4 = this;
            org.telegram.messenger.AccountInstance r0 = r4.accountInstance
            org.telegram.messenger.MessagesController r0 = r0.getMessagesController()
            org.telegram.tgnet.TLRPC$Chat r1 = r4.currentChat
            int r1 = r1.id
            org.telegram.tgnet.TLRPC$ChatFull r0 = r0.getChatFull(r1)
            org.telegram.tgnet.TLRPC$Chat r1 = r4.currentChat
            java.lang.String r1 = r1.username
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0039
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            org.telegram.messenger.AccountInstance r2 = r4.accountInstance
            org.telegram.messenger.MessagesController r2 = r2.getMessagesController()
            java.lang.String r2 = r2.linkPrefix
            r1.append(r2)
            java.lang.String r2 = "/"
            r1.append(r2)
            org.telegram.tgnet.TLRPC$Chat r2 = r4.currentChat
            java.lang.String r2 = r2.username
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            goto L_0x0043
        L_0x0039:
            if (r0 == 0) goto L_0x0042
            org.telegram.tgnet.TLRPC$TL_chatInviteExported r1 = r0.exported_invite
            if (r1 == 0) goto L_0x0042
            java.lang.String r1 = r1.link
            goto L_0x0043
        L_0x0042:
            r1 = 0
        L_0x0043:
            boolean r2 = android.text.TextUtils.isEmpty(r1)
            if (r2 == 0) goto L_0x0065
            org.telegram.tgnet.TLRPC$TL_messages_exportChatInvite r1 = new org.telegram.tgnet.TLRPC$TL_messages_exportChatInvite
            r1.<init>()
            org.telegram.tgnet.TLRPC$Chat r2 = r4.currentChat
            org.telegram.tgnet.TLRPC$InputPeer r2 = org.telegram.messenger.MessagesController.getInputPeer((org.telegram.tgnet.TLRPC$Chat) r2)
            r1.peer = r2
            org.telegram.messenger.AccountInstance r2 = r4.accountInstance
            org.telegram.tgnet.ConnectionsManager r2 = r2.getConnectionsManager()
            org.telegram.ui.-$$Lambda$GroupCallActivity$LBeMqMpbKcIc7QVcAxRLfrJHcyI r3 = new org.telegram.ui.-$$Lambda$GroupCallActivity$LBeMqMpbKcIc7QVcAxRLfrJHcyI
            r3.<init>(r0, r5)
            r2.sendRequest(r1, r3)
            goto L_0x0068
        L_0x0065:
            r4.openShareAlert(r1, r5)
        L_0x0068:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.GroupCallActivity.getLink(boolean):void");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$getLink$18 */
    public /* synthetic */ void lambda$getLink$18$GroupCallActivity(TLRPC$ChatFull tLRPC$ChatFull, boolean z, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
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
                GroupCallActivity.this.lambda$null$17$GroupCallActivity(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$17 */
    public /* synthetic */ void lambda$null$17$GroupCallActivity(TLObject tLObject, TLRPC$ChatFull tLRPC$ChatFull, boolean z) {
        if (tLObject instanceof TLRPC$TL_chatInviteExported) {
            TLRPC$TL_chatInviteExported tLRPC$TL_chatInviteExported = (TLRPC$TL_chatInviteExported) tLObject;
            if (tLRPC$ChatFull != null) {
                tLRPC$ChatFull.exported_invite = tLRPC$TL_chatInviteExported;
            }
            openShareAlert(tLRPC$TL_chatInviteExported.link, z);
        }
    }

    private void openShareAlert(String str, boolean z) {
        if (z) {
            AndroidUtilities.addToClipboard(str);
            getUndoView().showWithAction(0, 33, (Object) null, (Object) null, (Runnable) null, (Runnable) null);
            return;
        }
        boolean z2 = false;
        String formatString = LocaleController.formatString("VoipGroupInviteText", NUM, str);
        LaunchActivity launchActivity = this.parentActivity;
        if (launchActivity != null) {
            BaseFragment baseFragment = launchActivity.getActionBarLayout().fragmentsStack.get(this.parentActivity.getActionBarLayout().fragmentsStack.size() - 1);
            if (baseFragment instanceof ChatActivity) {
                z2 = ((ChatActivity) baseFragment).needEnterText();
                this.anyEnterEventSent = true;
                this.enterEventSent = true;
            }
        }
        ShareAlert shareAlert2 = new ShareAlert(getContext(), (ArrayList<MessageObject>) null, formatString, false, str, false);
        this.shareAlert = shareAlert2;
        shareAlert2.setDelegate(new ShareAlert.ShareAlertDelegate() {
            public /* synthetic */ void didShare() {
                ShareAlert.ShareAlertDelegate.CC.$default$didShare(this);
            }

            public boolean didCopy() {
                GroupCallActivity.this.getUndoView().showWithAction(0, 33, (Object) null, (Object) null, (Runnable) null, (Runnable) null);
                return true;
            }
        });
        this.shareAlert.setOnDismissListener(new DialogInterface.OnDismissListener() {
            public final void onDismiss(DialogInterface dialogInterface) {
                GroupCallActivity.this.lambda$openShareAlert$19$GroupCallActivity(dialogInterface);
            }
        });
        AndroidUtilities.runOnUIThread(new Runnable() {
            public final void run() {
                GroupCallActivity.this.lambda$openShareAlert$20$GroupCallActivity();
            }
        }, z2 ? 200 : 0);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$openShareAlert$19 */
    public /* synthetic */ void lambda$openShareAlert$19$GroupCallActivity(DialogInterface dialogInterface) {
        this.shareAlert = null;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$openShareAlert$20 */
    public /* synthetic */ void lambda$openShareAlert$20$GroupCallActivity() {
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
                    GroupCallActivity.this.lambda$inviteUserToCall$23$GroupCallActivity(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, tLObject, tLRPC$TL_error);
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
                        GroupCallActivity.this.lambda$inviteUserToCall$25$GroupCallActivity(this.f$1, this.f$2);
                    }
                }, 500);
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$inviteUserToCall$23 */
    public /* synthetic */ void lambda$inviteUserToCall$23$GroupCallActivity(int i, AlertDialog[] alertDialogArr, TLRPC$User tLRPC$User, boolean z, TLRPC$TL_phone_inviteToGroupCall tLRPC$TL_phone_inviteToGroupCall, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
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
                    GroupCallActivity.this.lambda$null$21$GroupCallActivity(this.f$1, this.f$2, this.f$3);
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
                GroupCallActivity.this.lambda$null$22$GroupCallActivity(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$21 */
    public /* synthetic */ void lambda$null$21$GroupCallActivity(int i, AlertDialog[] alertDialogArr, TLRPC$User tLRPC$User) {
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
    /* renamed from: lambda$null$22 */
    public /* synthetic */ void lambda$null$22$GroupCallActivity(AlertDialog[] alertDialogArr, boolean z, TLRPC$TL_error tLRPC$TL_error, int i, TLRPC$TL_phone_inviteToGroupCall tLRPC$TL_phone_inviteToGroupCall) {
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
    /* renamed from: lambda$inviteUserToCall$25 */
    public /* synthetic */ void lambda$inviteUserToCall$25$GroupCallActivity(AlertDialog[] alertDialogArr, int i) {
        if (alertDialogArr[0] != null) {
            alertDialogArr[0].setOnCancelListener(new DialogInterface.OnCancelListener(i) {
                public final /* synthetic */ int f$1;

                {
                    this.f$1 = r2;
                }

                public final void onCancel(DialogInterface dialogInterface) {
                    GroupCallActivity.this.lambda$null$24$GroupCallActivity(this.f$1, dialogInterface);
                }
            });
            alertDialogArr[0].show();
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$24 */
    public /* synthetic */ void lambda$null$24$GroupCallActivity(int i, DialogInterface dialogInterface) {
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
        RecyclerListView.Holder holder = null;
        for (int i2 = 0; i2 < childCount; i2++) {
            holder = (RecyclerListView.Holder) this.listView.findContainingViewHolder(this.listView.getChildAt(i2));
            if (holder != null) {
                if (holder.getAdapterPosition() == 0) {
                    break;
                }
                holder = null;
            }
        }
        float max = holder != null ? Math.max(0.0f, holder.itemView.getY()) : 0.0f;
        boolean z2 = max <= ((float) (ActionBar.getCurrentActionBarHeight() - AndroidUtilities.dp(14.0f)));
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
        float f4 = max + ((float) i3);
        if (this.scrollOffsetY != f4) {
            RecyclerListView recyclerListView2 = this.listView;
            this.scrollOffsetY = f4;
            recyclerListView2.setTopGlowOffset((int) (f4 - ((float) i3)));
            int dp = AndroidUtilities.dp(74.0f);
            float f5 = this.scrollOffsetY - ((float) dp);
            if (((float) this.backgroundPaddingTop) + f5 < ((float) (ActionBar.getCurrentActionBarHeight() * 2))) {
                float min = Math.min(1.0f, ((((float) (ActionBar.getCurrentActionBarHeight() * 2)) - f5) - ((float) this.backgroundPaddingTop)) / ((float) (((dp - this.backgroundPaddingTop) - AndroidUtilities.dp(14.0f)) + ActionBar.getCurrentActionBarHeight())));
                i = (int) (((float) AndroidUtilities.dp(AndroidUtilities.isTablet() ? 17.0f : 13.0f)) * min);
                if (Math.abs(Math.min(1.0f, min) - this.colorProgress) > 1.0E-4f) {
                    setColorProgress(Math.min(1.0f, min));
                }
                float f6 = 1.0f - ((0.1f * min) * 1.2f);
                this.titleTextView.setScaleX(Math.max(0.9f, f6));
                this.titleTextView.setScaleY(Math.max(0.9f, f6));
                this.titleTextView.setAlpha(Math.max(0.0f, 1.0f - (min * 1.2f)));
            } else {
                this.titleTextView.setScaleX(1.0f);
                this.titleTextView.setScaleY(1.0f);
                this.titleTextView.setAlpha(1.0f);
                if (this.colorProgress > 1.0E-4f) {
                    setColorProgress(0.0f);
                }
            }
            float f7 = (float) i;
            this.menuItemsContainer.setTranslationY(Math.max((float) AndroidUtilities.dp(4.0f), (this.scrollOffsetY - ((float) AndroidUtilities.dp(53.0f))) - f7));
            this.titleTextView.setTranslationY(Math.max((float) AndroidUtilities.dp(4.0f), (this.scrollOffsetY - ((float) AndroidUtilities.dp(44.0f))) - f7));
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
        int i = this.currentCallState;
        if (i == 1 || i == 2 || i == 6 || i == 5) {
            cancelMutePress();
            updateMuteButton(3, z);
        } else if (VoIPService.getSharedInstance() != null) {
            TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant = this.call.participants.get(this.accountInstance.getUserConfig().getClientUserId());
            if (tLRPC$TL_groupCallParticipant == null || tLRPC$TL_groupCallParticipant.can_self_unmute || !tLRPC$TL_groupCallParticipant.muted || ChatObject.canManageCalls(this.currentChat)) {
                boolean isMicMute = VoIPService.getSharedInstance().isMicMute();
                if (z2 && tLRPC$TL_groupCallParticipant != null && tLRPC$TL_groupCallParticipant.muted && !isMicMute) {
                    cancelMutePress();
                    VoIPService.getSharedInstance().setMicMute(true, false, false);
                    isMicMute = true;
                }
                if (isMicMute) {
                    updateMuteButton(0, z);
                } else {
                    updateMuteButton(1, z);
                }
            } else {
                cancelMutePress();
                updateMuteButton(2, z);
                VoIPService.getSharedInstance().setMicMute(true, false, false);
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
    /* JADX WARNING: Removed duplicated region for block: B:20:0x0078  */
    /* JADX WARNING: Removed duplicated region for block: B:21:0x008d  */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x0096  */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x0120  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void updateMuteButton(int r10, boolean r11) {
        /*
            r9 = this;
            int r0 = r9.muteButtonState
            if (r0 != r10) goto L_0x0007
            if (r11 == 0) goto L_0x0007
            return
        L_0x0007:
            android.animation.ValueAnimator r0 = r9.muteButtonAnimator
            if (r0 == 0) goto L_0x0011
            r0.cancel()
            r0 = 0
            r9.muteButtonAnimator = r0
        L_0x0011:
            java.lang.String r0 = ""
            r1 = 13
            r2 = 1
            if (r10 != 0) goto L_0x0035
            r0 = 2131627963(0x7f0e0fbb, float:1.8883205E38)
            java.lang.String r3 = "VoipGroupUnmute"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r3, r0)
            r3 = 2131627974(0x7f0e0fc6, float:1.8883228E38)
            java.lang.String r4 = "VoipHoldAndTalk"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            org.telegram.ui.Components.RLottieDrawable r4 = r9.bigMicDrawable
            boolean r1 = r4.setCustomEndFrame(r1)
        L_0x0030:
            r8 = r1
            r1 = r0
            r0 = r3
            r3 = r8
            goto L_0x0072
        L_0x0035:
            if (r10 != r2) goto L_0x0049
            r1 = 2131628017(0x7f0e0ff1, float:1.8883315E38)
            java.lang.String r3 = "VoipTapToMute"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r3, r1)
            org.telegram.ui.Components.RLottieDrawable r3 = r9.bigMicDrawable
            r4 = 24
            boolean r3 = r3.setCustomEndFrame(r4)
            goto L_0x0072
        L_0x0049:
            r3 = 3
            if (r10 != r3) goto L_0x0059
            r3 = 2131624955(0x7f0e03fb, float:1.8877104E38)
            java.lang.String r4 = "Connecting"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r8 = r3
            r3 = r0
            r0 = r8
            goto L_0x006b
        L_0x0059:
            r0 = 2131627981(0x7f0e0fcd, float:1.8883242E38)
            java.lang.String r3 = "VoipMutedByAdmin"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r3, r0)
            r3 = 2131627982(0x7f0e0fce, float:1.8883244E38)
            java.lang.String r4 = "VoipMutedByAdminInfo"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
        L_0x006b:
            org.telegram.ui.Components.RLottieDrawable r4 = r9.bigMicDrawable
            boolean r1 = r4.setCustomEndFrame(r1)
            goto L_0x0030
        L_0x0072:
            boolean r4 = android.text.TextUtils.isEmpty(r0)
            if (r4 != 0) goto L_0x008d
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r1)
            java.lang.String r5 = " "
            r4.append(r5)
            r4.append(r0)
            java.lang.String r4 = r4.toString()
            goto L_0x008e
        L_0x008d:
            r4 = r1
        L_0x008e:
            org.telegram.ui.Components.RLottieImageView r5 = r9.muteButton
            r5.setContentDescription(r4)
            r4 = 0
            if (r11 == 0) goto L_0x0120
            if (r3 == 0) goto L_0x00a7
            if (r10 != r2) goto L_0x00a2
            org.telegram.ui.Components.RLottieDrawable r3 = r9.bigMicDrawable
            r5 = 12
            r3.setCurrentFrame(r5)
            goto L_0x00a7
        L_0x00a2:
            org.telegram.ui.Components.RLottieDrawable r3 = r9.bigMicDrawable
            r3.setCurrentFrame(r4)
        L_0x00a7:
            org.telegram.ui.Components.RLottieImageView r3 = r9.muteButton
            r3.playAnimation()
            android.widget.TextView[] r3 = r9.muteLabel
            r3 = r3[r2]
            r3.setVisibility(r4)
            android.widget.TextView[] r3 = r9.muteLabel
            r3 = r3[r2]
            r5 = 0
            r3.setAlpha(r5)
            android.widget.TextView[] r3 = r9.muteLabel
            r3 = r3[r2]
            r6 = 1084227584(0x40a00000, float:5.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r7 = -r7
            float r7 = (float) r7
            r3.setTranslationY(r7)
            android.widget.TextView[] r3 = r9.muteLabel
            r3 = r3[r2]
            r3.setText(r1)
            android.widget.TextView[] r1 = r9.muteSubLabel
            r1 = r1[r2]
            r1.setVisibility(r4)
            android.widget.TextView[] r1 = r9.muteSubLabel
            r1 = r1[r2]
            r1.setAlpha(r5)
            android.widget.TextView[] r1 = r9.muteSubLabel
            r1 = r1[r2]
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r3 = -r3
            float r3 = (float) r3
            r1.setTranslationY(r3)
            android.widget.TextView[] r1 = r9.muteSubLabel
            r1 = r1[r2]
            r1.setText(r0)
            r0 = 2
            float[] r0 = new float[r0]
            r0 = {0, NUM} // fill-array
            android.animation.ValueAnimator r0 = android.animation.ValueAnimator.ofFloat(r0)
            r9.muteButtonAnimator = r0
            org.telegram.ui.-$$Lambda$GroupCallActivity$Ez4oa0ryHEZLjuwbtGc_kxq9Zs0 r1 = new org.telegram.ui.-$$Lambda$GroupCallActivity$Ez4oa0ryHEZLjuwbtGc_kxq9Zs0
            r1.<init>()
            r0.addUpdateListener(r1)
            android.animation.ValueAnimator r0 = r9.muteButtonAnimator
            org.telegram.ui.GroupCallActivity$15 r1 = new org.telegram.ui.GroupCallActivity$15
            r1.<init>()
            r0.addListener(r1)
            android.animation.ValueAnimator r0 = r9.muteButtonAnimator
            r1 = 180(0xb4, double:8.9E-322)
            r0.setDuration(r1)
            android.animation.ValueAnimator r0 = r9.muteButtonAnimator
            r0.start()
            r9.muteButtonState = r10
            goto L_0x013a
        L_0x0120:
            r9.muteButtonState = r10
            org.telegram.ui.Components.RLottieDrawable r10 = r9.bigMicDrawable
            int r3 = r10.getCustomEndFrame()
            int r3 = r3 - r2
            r10.setCurrentFrame(r3, r4, r2)
            android.widget.TextView[] r10 = r9.muteLabel
            r10 = r10[r4]
            r10.setText(r1)
            android.widget.TextView[] r10 = r9.muteSubLabel
            r10 = r10[r4]
            r10.setText(r0)
        L_0x013a:
            r9.updateMuteButtonState(r11)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.GroupCallActivity.updateMuteButton(int, boolean):void");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$updateMuteButton$26 */
    public /* synthetic */ void lambda$updateMuteButton$26$GroupCallActivity(ValueAnimator valueAnimator) {
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
        } else if (i == 2) {
            iArr[0] = Theme.getColor("voipgroup_mutedByAdminGradient3");
            iArr[1] = Theme.getColor("voipgroup_mutedByAdminMuteButton");
            iArr[2] = Theme.getColor("voipgroup_mutedByAdminMuteButtonDisabled");
        } else {
            iArr[0] = Theme.getColor("voipgroup_disabledButton");
            iArr[1] = AndroidUtilities.getOffsetColor(Theme.getColor("voipgroup_disabledButtonActive"), Theme.getColor("voipgroup_disabledButtonActiveScrolled"), this.colorProgress, 1.0f);
            iArr[2] = AndroidUtilities.getOffsetColor(Theme.getColor("voipgroup_listViewBackgroundUnscrolled"), Theme.getColor("voipgroup_disabledButton"), this.colorProgress, 1.0f);
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
            } else if (i2 == 2) {
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
    public static void processOnLeave(ChatObject.Call call2, boolean z, Runnable runnable) {
        int i;
        TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant;
        if (VoIPService.getSharedInstance() != null) {
            VoIPService.getSharedInstance().hangUp(z ? 1 : 0);
        }
        if (!(call2 == null || (tLRPC$TL_groupCallParticipant = call2.participants.get(i)) == null)) {
            call2.participants.delete((i = call2.currentAccount.getUserConfig().clientUserId));
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
            if (!ChatObject.canManageCalls(chat)) {
                processOnLeave(call2, false, runnable);
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
                    GroupCallActivity.lambda$onLeaveClick$27(this.f$0, view);
                }
            });
            builder.setCustomViewOffset(12);
            builder.setView(linearLayout);
            builder.setPositiveButton(LocaleController.getString("VoipGroupLeave", NUM), new DialogInterface.OnClickListener(checkBoxCellArr, runnable) {
                public final /* synthetic */ CheckBoxCell[] f$1;
                public final /* synthetic */ Runnable f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void onClick(DialogInterface dialogInterface, int i) {
                    GroupCallActivity.processOnLeave(ChatObject.Call.this, this.f$1[0].isChecked(), this.f$2);
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

    static /* synthetic */ void lambda$onLeaveClick$27(CheckBoxCell[] checkBoxCellArr, View view) {
        Integer num = (Integer) view.getTag();
        checkBoxCellArr[num.intValue()].setChecked(!checkBoxCellArr[num.intValue()].isChecked(), true);
    }

    /* access modifiers changed from: private */
    public void processSelectedOption(TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant, int i, int i2) {
        TextView textView;
        TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant2 = tLRPC$TL_groupCallParticipant;
        int i3 = i2;
        TLRPC$User user = this.accountInstance.getMessagesController().getUser(Integer.valueOf(i));
        if (i3 == 0 || i3 == 2 || i3 == 3) {
            if (i3 != 0) {
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
                avatarDrawable.setInfo(user);
                backupImageView.setImage(ImageLocation.getForUser(user, false), "50_50", (Drawable) avatarDrawable, (Object) user);
                String firstName = UserObject.getFirstName(user);
                TextView textView3 = new TextView(getContext());
                textView3.setTextColor(Theme.getColor("voipgroup_actionBarItems"));
                textView3.setTextSize(1, 20.0f);
                textView3.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                textView3.setLines(1);
                textView3.setMaxLines(1);
                textView3.setSingleLine(true);
                textView3.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
                textView3.setEllipsize(TextUtils.TruncateAt.END);
                if (i3 == 2) {
                    textView3.setText(LocaleController.getString("VoipGroupRemoveMemberAlertTitle", NUM));
                    textView2.setText(AndroidUtilities.replaceTags(LocaleController.formatString("VoipGroupRemoveMemberAlertText", NUM, firstName)));
                } else {
                    textView3.setText(LocaleController.getString("VoipGroupAddMemberTitle", NUM));
                    textView2.setText(AndroidUtilities.replaceTags(LocaleController.formatString("VoipGroupAddMemberText", NUM, firstName, this.currentChat.title)));
                }
                boolean z = LocaleController.isRTL;
                int i4 = (z ? 5 : 3) | 48;
                int i5 = 21;
                float f = (float) (z ? 21 : 76);
                if (z) {
                    i5 = 76;
                }
                frameLayout.addView(textView3, LayoutHelper.createFrame(-1, -2.0f, i4, f, 11.0f, (float) i5, 0.0f));
                frameLayout.addView(textView2, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, 24.0f, 57.0f, 24.0f, 9.0f));
                if (i3 == 2) {
                    builder.setPositiveButton(LocaleController.getString("VoipGroupUserRemove", NUM), new DialogInterface.OnClickListener(user) {
                        public final /* synthetic */ TLRPC$User f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void onClick(DialogInterface dialogInterface, int i) {
                            GroupCallActivity.this.lambda$processSelectedOption$29$GroupCallActivity(this.f$1, dialogInterface, i);
                        }
                    });
                } else {
                    builder.setPositiveButton(LocaleController.getString("VoipGroupAdd", NUM), new DialogInterface.OnClickListener(user, i) {
                        public final /* synthetic */ TLRPC$User f$1;
                        public final /* synthetic */ int f$2;

                        {
                            this.f$1 = r2;
                            this.f$2 = r3;
                        }

                        public final void onClick(DialogInterface dialogInterface, int i) {
                            GroupCallActivity.this.lambda$processSelectedOption$31$GroupCallActivity(this.f$1, this.f$2, dialogInterface, i);
                        }
                    });
                }
                builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                AlertDialog create = builder.create();
                create.setBackgroundColor(Theme.getColor("voipgroup_dialogBackground"));
                create.show();
                if (i3 == 2 && (textView = (TextView) create.getButton(-1)) != null) {
                    textView.setTextColor(Theme.getColor("voipgroup_leaveCallMenu"));
                }
            } else if (VoIPService.getSharedInstance() != null) {
                VoIPService.getSharedInstance().editCallMember(user, true, -1);
                getUndoView().showWithAction(0, 30, user, (Object) null, (Runnable) null, (Runnable) null);
            }
        } else if (i3 == 6) {
            Bundle bundle = new Bundle();
            bundle.putInt("user_id", tLRPC$TL_groupCallParticipant2.user_id);
            this.parentActivity.lambda$runLinkRequest$41(new ProfileActivity(bundle));
            dismiss();
        } else if (i3 == 5) {
            VoIPService.getSharedInstance().editCallMember(user, true, -1);
            getUndoView().showWithAction(0, 35, user, (Object) null, (Runnable) null, (Runnable) null);
            VoIPService.getSharedInstance().setParticipantVolume(tLRPC$TL_groupCallParticipant2.source, 0);
        } else {
            if ((tLRPC$TL_groupCallParticipant2.flags & 128) == 0 || tLRPC$TL_groupCallParticipant2.volume != 0) {
                VoIPService.getSharedInstance().editCallMember(user, false, -1);
            } else {
                tLRPC$TL_groupCallParticipant2.volume = 10000;
                VoIPService.getSharedInstance().editCallMember(user, false, tLRPC$TL_groupCallParticipant2.volume);
            }
            VoIPService.getSharedInstance().setParticipantVolume(tLRPC$TL_groupCallParticipant2.source, ChatObject.getParticipantVolume(tLRPC$TL_groupCallParticipant));
            getUndoView().showWithAction(0, i3 == 1 ? 31 : 36, user, (Object) null, (Runnable) null, (Runnable) null);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$processSelectedOption$29 */
    public /* synthetic */ void lambda$processSelectedOption$29$GroupCallActivity(TLRPC$User tLRPC$User, DialogInterface dialogInterface, int i) {
        this.accountInstance.getMessagesController().deleteUserFromChat(this.currentChat.id, tLRPC$User, (TLRPC$ChatFull) null);
        getUndoView().showWithAction(0, 32, tLRPC$User, (Object) null, (Runnable) null, (Runnable) null);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$processSelectedOption$31 */
    public /* synthetic */ void lambda$processSelectedOption$31$GroupCallActivity(TLRPC$User tLRPC$User, int i, DialogInterface dialogInterface, int i2) {
        this.accountInstance.getMessagesController().addUserToChat(this.currentChat.id, tLRPC$User, 0, (String) null, this.parentActivity.getActionBarLayout().fragmentsStack.get(this.parentActivity.getActionBarLayout().fragmentsStack.size() - 1), new Runnable(i) {
            public final /* synthetic */ int f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                GroupCallActivity.this.lambda$null$30$GroupCallActivity(this.f$1);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$30 */
    public /* synthetic */ void lambda$null$30$GroupCallActivity(int i) {
        inviteUserToCall(i, false);
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v1, resolved type: org.telegram.ui.GroupCallActivity$18} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v2, resolved type: android.widget.ScrollView} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r14v3, resolved type: org.telegram.ui.GroupCallActivity$18} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v33, resolved type: org.telegram.ui.GroupCallActivity$18} */
    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x014b, code lost:
        if (r7.accountInstance.getMessagesController().getAdminRank(r7.currentChat.id, r9.user_id) != null) goto L_0x014d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:37:0x0180, code lost:
        if ((r1 instanceof org.telegram.tgnet.TLRPC$TL_chatParticipantCreator) == false) goto L_0x0189;
     */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:42:0x01a0 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:54:0x021c  */
    /* JADX WARNING: Removed duplicated region for block: B:61:0x027c  */
    /* JADX WARNING: Removed duplicated region for block: B:76:0x0397  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean showMenuForCell(org.telegram.ui.Cells.GroupCallUserCell r22) {
        /*
            r21 = this;
            r7 = r21
            org.telegram.ui.ActionBar.ActionBarPopupWindow r0 = r7.scrimPopupWindow
            r1 = 0
            r8 = 0
            if (r0 == 0) goto L_0x0010
            r0.dismiss()
            r7.scrimPopupWindow = r1
            r7.scrimPopupWindowItems = r1
            return r8
        L_0x0010:
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r9 = r22.getParticipant()
            android.graphics.Rect r0 = new android.graphics.Rect
            r0.<init>()
            org.telegram.ui.ActionBar.ActionBarPopupWindow$ActionBarPopupWindowLayout r10 = new org.telegram.ui.ActionBar.ActionBarPopupWindow$ActionBarPopupWindowLayout
            android.content.Context r2 = r21.getContext()
            r10.<init>(r2)
            r10.setBackgroundDrawable(r1)
            r10.setPadding(r8, r8, r8, r8)
            org.telegram.ui.GroupCallActivity$16 r2 = new org.telegram.ui.GroupCallActivity$16
            r2.<init>(r0)
            r10.setOnTouchListener(r2)
            org.telegram.ui.-$$Lambda$GroupCallActivity$VjBoVqIPGHTdbhnSKVeE8SHSV7Y r0 = new org.telegram.ui.-$$Lambda$GroupCallActivity$VjBoVqIPGHTdbhnSKVeE8SHSV7Y
            r0.<init>()
            r10.setDispatchKeyEventListener(r0)
            android.widget.LinearLayout r11 = new android.widget.LinearLayout
            android.content.Context r0 = r21.getContext()
            r11.<init>(r0)
            boolean r0 = r9.muted_by_you
            if (r0 != 0) goto L_0x004f
            android.widget.LinearLayout r0 = new android.widget.LinearLayout
            android.content.Context r2 = r21.getContext()
            r0.<init>(r2)
            goto L_0x0050
        L_0x004f:
            r0 = r1
        L_0x0050:
            org.telegram.ui.GroupCallActivity$17 r12 = new org.telegram.ui.GroupCallActivity$17
            android.content.Context r2 = r21.getContext()
            r12.<init>(r7, r2, r11, r0)
            r2 = 1131413504(0x43700000, float:240.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r12.setMinimumWidth(r3)
            r13 = 1
            r12.setOrientation(r13)
            boolean r3 = r9.muted_by_you
            r4 = 2131165930(0x7var_ea, float:1.794609E38)
            if (r3 != 0) goto L_0x00b4
            boolean r3 = r9.muted
            if (r3 == 0) goto L_0x0075
            boolean r3 = r9.can_self_unmute
            if (r3 == 0) goto L_0x00b4
        L_0x0075:
            android.content.Context r1 = r21.getContext()
            android.content.res.Resources r1 = r1.getResources()
            android.graphics.drawable.Drawable r1 = r1.getDrawable(r4)
            android.graphics.drawable.Drawable r1 = r1.mutate()
            android.graphics.PorterDuffColorFilter r3 = new android.graphics.PorterDuffColorFilter
            int r5 = r7.backgroundColor
            android.graphics.PorterDuff$Mode r6 = android.graphics.PorterDuff.Mode.MULTIPLY
            r3.<init>(r5, r6)
            r1.setColorFilter(r3)
            r0.setBackgroundDrawable(r1)
            r14 = -2
            r15 = -2
            r16 = 0
            r17 = 0
            r18 = 0
            r19 = 0
            android.widget.LinearLayout$LayoutParams r1 = org.telegram.ui.Components.LayoutHelper.createLinear(r14, r15, r16, r17, r18, r19)
            r12.addView(r0, r1)
            org.telegram.ui.GroupCallActivity$VolumeSlider r1 = new org.telegram.ui.GroupCallActivity$VolumeSlider
            android.content.Context r3 = r21.getContext()
            r1.<init>(r7, r3, r9)
            r3 = -1
            r5 = 48
            r0.addView(r1, r3, r5)
        L_0x00b4:
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r11.setMinimumWidth(r0)
            r11.setOrientation(r13)
            android.content.Context r0 = r21.getContext()
            android.content.res.Resources r0 = r0.getResources()
            android.graphics.drawable.Drawable r0 = r0.getDrawable(r4)
            android.graphics.drawable.Drawable r0 = r0.mutate()
            android.graphics.PorterDuffColorFilter r2 = new android.graphics.PorterDuffColorFilter
            int r3 = r7.backgroundColor
            android.graphics.PorterDuff$Mode r4 = android.graphics.PorterDuff.Mode.MULTIPLY
            r2.<init>(r3, r4)
            r0.setColorFilter(r2)
            r11.setBackgroundDrawable(r0)
            r14 = -2
            r15 = -2
            r16 = 0
            if (r1 == 0) goto L_0x00e8
            r0 = -1056964608(0xffffffffCLASSNAME, float:-8.0)
            r17 = -1056964608(0xffffffffCLASSNAME, float:-8.0)
            goto L_0x00eb
        L_0x00e8:
            r0 = 0
            r17 = 0
        L_0x00eb:
            r18 = 0
            r19 = 0
            android.widget.LinearLayout$LayoutParams r0 = org.telegram.ui.Components.LayoutHelper.createLinear(r14, r15, r16, r17, r18, r19)
            r12.addView(r11, r0)
            int r0 = android.os.Build.VERSION.SDK_INT
            r1 = 21
            if (r0 < r1) goto L_0x010f
            org.telegram.ui.GroupCallActivity$18 r14 = new org.telegram.ui.GroupCallActivity$18
            android.content.Context r2 = r21.getContext()
            r3 = 0
            r4 = 0
            r5 = 2131689504(0x7f0var_, float:1.9008025E38)
            r0 = r14
            r1 = r21
            r6 = r12
            r0.<init>(r1, r2, r3, r4, r5, r6)
            goto L_0x0118
        L_0x010f:
            android.widget.ScrollView r14 = new android.widget.ScrollView
            android.content.Context r0 = r21.getContext()
            r14.<init>(r0)
        L_0x0118:
            r14.setClipToPadding(r8)
            r0 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r1 = -2
            android.widget.FrameLayout$LayoutParams r0 = org.telegram.ui.Components.LayoutHelper.createFrame(r1, r0)
            r10.addView(r14, r0)
            java.util.ArrayList r0 = new java.util.ArrayList
            r2 = 2
            r0.<init>(r2)
            java.util.ArrayList r3 = new java.util.ArrayList
            r3.<init>(r2)
            java.util.ArrayList r4 = new java.util.ArrayList
            r4.<init>(r2)
            org.telegram.tgnet.TLRPC$Chat r5 = r7.currentChat
            boolean r5 = r5.megagroup
            if (r5 == 0) goto L_0x014f
            org.telegram.messenger.AccountInstance r5 = r7.accountInstance
            org.telegram.messenger.MessagesController r5 = r5.getMessagesController()
            org.telegram.tgnet.TLRPC$Chat r6 = r7.currentChat
            int r6 = r6.id
            int r15 = r9.user_id
            java.lang.String r5 = r5.getAdminRank(r6, r15)
            if (r5 == 0) goto L_0x0189
        L_0x014d:
            r5 = 1
            goto L_0x018a
        L_0x014f:
            org.telegram.messenger.AccountInstance r5 = r7.accountInstance
            org.telegram.messenger.MessagesController r5 = r5.getMessagesController()
            org.telegram.tgnet.TLRPC$Chat r6 = r7.currentChat
            int r6 = r6.id
            org.telegram.tgnet.TLRPC$ChatFull r5 = r5.getChatFull(r6)
            if (r5 == 0) goto L_0x0189
            org.telegram.tgnet.TLRPC$ChatParticipants r6 = r5.participants
            java.util.ArrayList<org.telegram.tgnet.TLRPC$ChatParticipant> r6 = r6.participants
            int r6 = r6.size()
            r15 = 0
        L_0x0168:
            if (r15 >= r6) goto L_0x0189
            org.telegram.tgnet.TLRPC$ChatParticipants r1 = r5.participants
            java.util.ArrayList<org.telegram.tgnet.TLRPC$ChatParticipant> r1 = r1.participants
            java.lang.Object r1 = r1.get(r15)
            org.telegram.tgnet.TLRPC$ChatParticipant r1 = (org.telegram.tgnet.TLRPC$ChatParticipant) r1
            int r2 = r1.user_id
            int r8 = r9.user_id
            if (r2 != r8) goto L_0x0183
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$TL_chatParticipantAdmin
            if (r2 != 0) goto L_0x014d
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC$TL_chatParticipantCreator
            if (r1 == 0) goto L_0x0189
            goto L_0x014d
        L_0x0183:
            int r15 = r15 + 1
            r1 = -2
            r2 = 2
            r8 = 0
            goto L_0x0168
        L_0x0189:
            r5 = 0
        L_0x018a:
            org.telegram.tgnet.TLRPC$Chat r1 = r7.currentChat
            boolean r1 = org.telegram.messenger.ChatObject.canManageCalls(r1)
            r6 = 2131165774(0x7var_e, float:1.7945775E38)
            r8 = 2131627954(0x7f0e0fb2, float:1.8883187E38)
            java.lang.String r15 = "VoipGroupOpenProfile"
            r19 = 2131165828(0x7var_, float:1.7945884E38)
            r20 = 2131165826(0x7var_, float:1.794588E38)
            if (r1 == 0) goto L_0x021c
            if (r5 == 0) goto L_0x01a6
            boolean r1 = r9.muted
            if (r1 != 0) goto L_0x01e5
        L_0x01a6:
            boolean r1 = r9.muted
            if (r1 == 0) goto L_0x01ca
            boolean r1 = r9.can_self_unmute
            if (r1 == 0) goto L_0x01af
            goto L_0x01ca
        L_0x01af:
            r1 = 2131627926(0x7f0e0var_, float:1.888313E38)
            java.lang.String r2 = "VoipGroupAllowToSpeak"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.add(r1)
            java.lang.Integer r1 = java.lang.Integer.valueOf(r19)
            r3.add(r1)
            java.lang.Integer r1 = java.lang.Integer.valueOf(r13)
            r4.add(r1)
            goto L_0x01e5
        L_0x01ca:
            r1 = 2131627946(0x7f0e0faa, float:1.888317E38)
            java.lang.String r2 = "VoipGroupMute"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.add(r1)
            java.lang.Integer r1 = java.lang.Integer.valueOf(r20)
            r3.add(r1)
            r1 = 0
            java.lang.Integer r2 = java.lang.Integer.valueOf(r1)
            r4.add(r2)
        L_0x01e5:
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r15, r8)
            r0.add(r1)
            java.lang.Integer r1 = java.lang.Integer.valueOf(r6)
            r3.add(r1)
            r1 = 6
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            r4.add(r1)
            if (r5 != 0) goto L_0x026d
            r1 = 2131627969(0x7f0e0fc1, float:1.8883217E38)
            java.lang.String r2 = "VoipGroupUserRemove"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.add(r1)
            r1 = 2131165717(0x7var_, float:1.794566E38)
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            r3.add(r1)
            r1 = 2
            java.lang.Integer r2 = java.lang.Integer.valueOf(r1)
            r4.add(r2)
            goto L_0x026d
        L_0x021c:
            boolean r1 = r9.muted_by_you
            if (r1 == 0) goto L_0x023c
            r1 = 2131627964(0x7f0e0fbc, float:1.8883207E38)
            java.lang.String r2 = "VoipGroupUnmuteForMe"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.add(r1)
            java.lang.Integer r1 = java.lang.Integer.valueOf(r19)
            r3.add(r1)
            r1 = 4
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            r4.add(r1)
            goto L_0x0257
        L_0x023c:
            r1 = 2131627947(0x7f0e0fab, float:1.8883173E38)
            java.lang.String r2 = "VoipGroupMuteForMe"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r0.add(r1)
            java.lang.Integer r1 = java.lang.Integer.valueOf(r20)
            r3.add(r1)
            r1 = 5
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            r4.add(r1)
        L_0x0257:
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r15, r8)
            r0.add(r1)
            java.lang.Integer r1 = java.lang.Integer.valueOf(r6)
            r3.add(r1)
            r1 = 6
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            r4.add(r1)
        L_0x026d:
            int r1 = r0.size()
            org.telegram.ui.ActionBar.ActionBarMenuSubItem[] r1 = new org.telegram.ui.ActionBar.ActionBarMenuSubItem[r1]
            r7.scrimPopupWindowItems = r1
            int r1 = r0.size()
            r2 = 0
        L_0x027a:
            if (r2 >= r1) goto L_0x02ea
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r5 = new org.telegram.ui.ActionBar.ActionBarMenuSubItem
            android.content.Context r6 = r21.getContext()
            if (r2 != 0) goto L_0x0286
            r8 = 1
            goto L_0x0287
        L_0x0286:
            r8 = 0
        L_0x0287:
            int r15 = r1 + -1
            if (r2 != r15) goto L_0x028d
            r15 = 1
            goto L_0x028e
        L_0x028d:
            r15 = 0
        L_0x028e:
            r5.<init>(r6, r8, r15)
            java.lang.Object r6 = r4.get(r2)
            java.lang.Integer r6 = (java.lang.Integer) r6
            int r6 = r6.intValue()
            r8 = 2
            if (r6 == r8) goto L_0x02ad
            java.lang.String r6 = "voipgroup_actionBarItems"
            int r8 = org.telegram.ui.ActionBar.Theme.getColor(r6)
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r6)
            r5.setColors(r8, r6)
            goto L_0x02bb
        L_0x02ad:
            java.lang.String r6 = "voipgroup_leaveCallMenu"
            int r8 = org.telegram.ui.ActionBar.Theme.getColor(r6)
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r6)
            r5.setColors(r8, r6)
        L_0x02bb:
            java.lang.String r6 = "voipgroup_listSelector"
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r6)
            r5.setSelectorColor(r6)
            java.lang.Object r6 = r0.get(r2)
            java.lang.CharSequence r6 = (java.lang.CharSequence) r6
            java.lang.Object r8 = r3.get(r2)
            java.lang.Integer r8 = (java.lang.Integer) r8
            int r8 = r8.intValue()
            r5.setTextAndIcon(r6, r8)
            org.telegram.ui.ActionBar.ActionBarMenuSubItem[] r6 = r7.scrimPopupWindowItems
            r6[r2] = r5
            r11.addView(r5)
            org.telegram.ui.-$$Lambda$GroupCallActivity$hSkBsVNUdd_13-2yQAh06bNNCiA r6 = new org.telegram.ui.-$$Lambda$GroupCallActivity$hSkBsVNUdd_13-2yQAh06bNNCiA
            r6.<init>(r2, r4, r9)
            r5.setOnClickListener(r6)
            int r2 = r2 + 1
            goto L_0x027a
        L_0x02ea:
            r0 = 51
            r1 = -2
            android.widget.FrameLayout$LayoutParams r2 = org.telegram.ui.Components.LayoutHelper.createScroll(r1, r1, r0)
            r14.addView(r12, r2)
            org.telegram.ui.GroupCallActivity$19 r2 = new org.telegram.ui.GroupCallActivity$19
            r2.<init>(r10, r1, r1)
            r7.scrimPopupWindow = r2
            r2.setPauseNotifications(r13)
            org.telegram.ui.ActionBar.ActionBarPopupWindow r1 = r7.scrimPopupWindow
            r2 = 220(0xdc, float:3.08E-43)
            r1.setDismissAnimationDuration(r2)
            org.telegram.ui.ActionBar.ActionBarPopupWindow r1 = r7.scrimPopupWindow
            r1.setOutsideTouchable(r13)
            org.telegram.ui.ActionBar.ActionBarPopupWindow r1 = r7.scrimPopupWindow
            r1.setClippingEnabled(r13)
            org.telegram.ui.ActionBar.ActionBarPopupWindow r1 = r7.scrimPopupWindow
            r2 = 2131689477(0x7f0var_, float:1.900797E38)
            r1.setAnimationStyle(r2)
            org.telegram.ui.ActionBar.ActionBarPopupWindow r1 = r7.scrimPopupWindow
            r1.setFocusable(r13)
            r1 = 1148846080(0x447a0000, float:1000.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r1)
            r3 = -2147483648(0xfffffffvar_, float:-0.0)
            int r2 = android.view.View.MeasureSpec.makeMeasureSpec(r2, r3)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r1 = android.view.View.MeasureSpec.makeMeasureSpec(r1, r3)
            r10.measure(r2, r1)
            org.telegram.ui.ActionBar.ActionBarPopupWindow r1 = r7.scrimPopupWindow
            r2 = 2
            r1.setInputMethodMode(r2)
            org.telegram.ui.ActionBar.ActionBarPopupWindow r1 = r7.scrimPopupWindow
            r2 = 0
            r1.setSoftInputMode(r2)
            org.telegram.ui.ActionBar.ActionBarPopupWindow r1 = r7.scrimPopupWindow
            android.view.View r1 = r1.getContentView()
            r1.setFocusableInTouchMode(r13)
            r1 = 1096810496(0x41600000, float:14.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            org.telegram.ui.Components.RecyclerListView r2 = r7.listView
            int r2 = r2.getMeasuredWidth()
            int r1 = r1 + r2
            r2 = 1090519040(0x41000000, float:8.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r1 = r1 + r2
            int r2 = r10.getMeasuredWidth()
            int r1 = r1 - r2
            org.telegram.ui.Components.RecyclerListView r2 = r7.listView
            float r2 = r2.getY()
            float r3 = r22.getY()
            float r2 = r2 + r3
            int r3 = r22.getMeasuredHeight()
            float r3 = (float) r3
            float r2 = r2 + r3
            int r2 = (int) r2
            org.telegram.ui.ActionBar.ActionBarPopupWindow r3 = r7.scrimPopupWindow
            org.telegram.ui.Components.RecyclerListView r4 = r7.listView
            r3.showAtLocation(r4, r0, r1, r2)
            org.telegram.ui.Components.RecyclerListView r0 = r7.listView
            r0.stopScroll()
            org.telegram.ui.Components.FillLastLinearLayoutManager r0 = r7.layoutManager
            r1 = 0
            r0.setCanScrollVertically(r1)
            r0 = r22
            r7.scrimView = r0
            android.view.ViewGroup r0 = r7.containerView
            r0.invalidate()
            org.telegram.ui.Components.RecyclerListView r0 = r7.listView
            r0.invalidate()
            android.animation.AnimatorSet r0 = r7.scrimAnimatorSet
            if (r0 == 0) goto L_0x039a
            r0.cancel()
        L_0x039a:
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
            return r13
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.GroupCallActivity.showMenuForCell(org.telegram.ui.Cells.GroupCallUserCell):boolean");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$showMenuForCell$32 */
    public /* synthetic */ void lambda$showMenuForCell$32$GroupCallActivity(KeyEvent keyEvent) {
        ActionBarPopupWindow actionBarPopupWindow;
        if (keyEvent.getKeyCode() == 4 && keyEvent.getRepeatCount() == 0 && (actionBarPopupWindow = this.scrimPopupWindow) != null && actionBarPopupWindow.isShowing()) {
            this.scrimPopupWindow.dismiss();
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$showMenuForCell$33 */
    public /* synthetic */ void lambda$showMenuForCell$33$GroupCallActivity(int i, ArrayList arrayList, TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant, View view) {
        if (i < arrayList.size()) {
            processSelectedOption(tLRPC$TL_groupCallParticipant, tLRPC$TL_groupCallParticipant.user_id, ((Integer) arrayList.get(i)).intValue());
            ActionBarPopupWindow actionBarPopupWindow = this.scrimPopupWindow;
            if (actionBarPopupWindow != null) {
                actionBarPopupWindow.dismiss();
            }
        }
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        /* access modifiers changed from: private */
        public int addMemberRow;
        /* access modifiers changed from: private */
        public int invitedEndRow;
        /* access modifiers changed from: private */
        public int invitedStartRow;
        private int lastRow;
        private Context mContext;
        /* access modifiers changed from: private */
        public int rowsCount;
        /* access modifiers changed from: private */
        public int selfUserRow;
        /* access modifiers changed from: private */
        public int usersEndRow;
        /* access modifiers changed from: private */
        public int usersStartRow;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean addSelfToCounter() {
            if (this.selfUserRow >= 0 && VoIPService.getSharedInstance() != null) {
                return !VoIPService.getSharedInstance().isJoined();
            }
            return false;
        }

        public int getItemCount() {
            return this.rowsCount;
        }

        /* access modifiers changed from: private */
        public void updateRows() {
            if (!GroupCallActivity.this.delayedGroupCallUpdated) {
                this.rowsCount = 0;
                if (ChatObject.canWriteToChat(GroupCallActivity.this.currentChat)) {
                    int i = this.rowsCount;
                    this.rowsCount = i + 1;
                    this.addMemberRow = i;
                } else {
                    this.addMemberRow = -1;
                }
                GroupCallActivity groupCallActivity = GroupCallActivity.this;
                if (groupCallActivity.call.participants.indexOfKey(groupCallActivity.selfDummyParticipant.user_id) < 0) {
                    int i2 = this.rowsCount;
                    this.rowsCount = i2 + 1;
                    this.selfUserRow = i2;
                } else {
                    this.selfUserRow = -1;
                }
                int i3 = this.rowsCount;
                this.usersStartRow = i3;
                int size = i3 + GroupCallActivity.this.call.sortedParticipants.size();
                this.rowsCount = size;
                this.usersEndRow = size;
                if (GroupCallActivity.this.call.invitedUsers.isEmpty()) {
                    this.invitedStartRow = -1;
                    this.invitedEndRow = -1;
                } else {
                    int i4 = this.rowsCount;
                    this.invitedStartRow = i4;
                    int size2 = i4 + GroupCallActivity.this.call.invitedUsers.size();
                    this.rowsCount = size2;
                    this.invitedEndRow = size2;
                }
                int i5 = this.rowsCount;
                this.rowsCount = i5 + 1;
                this.lastRow = i5;
            }
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
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v13, resolved type: java.lang.Integer} */
        /* JADX WARNING: type inference failed for: r2v6 */
        /* JADX WARNING: type inference failed for: r2v18 */
        /* JADX WARNING: type inference failed for: r2v19 */
        /* JADX WARNING: type inference failed for: r2v20 */
        /* JADX WARNING: type inference failed for: r2v21 */
        /* JADX WARNING: type inference failed for: r2v22 */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r5, int r6) {
            /*
                r4 = this;
                int r0 = r5.getItemViewType()
                r1 = 1
                if (r0 == 0) goto L_0x00c3
                r2 = 0
                if (r0 == r1) goto L_0x0062
                r1 = 2
                if (r0 == r1) goto L_0x000f
                goto L_0x00fd
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
                if (r2 == 0) goto L_0x00fd
                org.telegram.ui.GroupCallActivity r6 = org.telegram.ui.GroupCallActivity.this
                int r6 = r6.currentAccount
                r5.setData(r6, r2)
                goto L_0x00fd
            L_0x0062:
                android.view.View r5 = r5.itemView
                org.telegram.ui.Cells.GroupCallUserCell r5 = (org.telegram.ui.Cells.GroupCallUserCell) r5
                int r0 = r4.selfUserRow
                if (r6 != r0) goto L_0x0071
                org.telegram.ui.GroupCallActivity r6 = org.telegram.ui.GroupCallActivity.this
                org.telegram.tgnet.TLRPC$TL_groupCallParticipant r2 = r6.selfDummyParticipant
                goto L_0x00b3
            L_0x0071:
                int r0 = r4.usersStartRow
                int r6 = r6 - r0
                org.telegram.ui.GroupCallActivity r0 = org.telegram.ui.GroupCallActivity.this
                boolean r0 = r0.delayedGroupCallUpdated
                if (r0 == 0) goto L_0x0098
                if (r6 < 0) goto L_0x00b3
                org.telegram.ui.GroupCallActivity r0 = org.telegram.ui.GroupCallActivity.this
                java.util.ArrayList r0 = r0.oldParticipants
                int r0 = r0.size()
                if (r6 >= r0) goto L_0x00b3
                org.telegram.ui.GroupCallActivity r0 = org.telegram.ui.GroupCallActivity.this
                java.util.ArrayList r0 = r0.oldParticipants
                java.lang.Object r6 = r0.get(r6)
                r2 = r6
                org.telegram.tgnet.TLRPC$TL_groupCallParticipant r2 = (org.telegram.tgnet.TLRPC$TL_groupCallParticipant) r2
                goto L_0x00b3
            L_0x0098:
                if (r6 < 0) goto L_0x00b3
                org.telegram.ui.GroupCallActivity r0 = org.telegram.ui.GroupCallActivity.this
                org.telegram.messenger.ChatObject$Call r0 = r0.call
                java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_groupCallParticipant> r0 = r0.sortedParticipants
                int r0 = r0.size()
                if (r6 >= r0) goto L_0x00b3
                org.telegram.ui.GroupCallActivity r0 = org.telegram.ui.GroupCallActivity.this
                org.telegram.messenger.ChatObject$Call r0 = r0.call
                java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_groupCallParticipant> r0 = r0.sortedParticipants
                java.lang.Object r6 = r0.get(r6)
                r2 = r6
                org.telegram.tgnet.TLRPC$TL_groupCallParticipant r2 = (org.telegram.tgnet.TLRPC$TL_groupCallParticipant) r2
            L_0x00b3:
                if (r2 == 0) goto L_0x00fd
                org.telegram.ui.GroupCallActivity r6 = org.telegram.ui.GroupCallActivity.this
                org.telegram.messenger.AccountInstance r6 = r6.accountInstance
                org.telegram.ui.GroupCallActivity r0 = org.telegram.ui.GroupCallActivity.this
                org.telegram.messenger.ChatObject$Call r0 = r0.call
                r5.setData(r6, r2, r0)
                goto L_0x00fd
            L_0x00c3:
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
                if (r2 == 0) goto L_0x00e6
                r2 = 1065353216(0x3var_, float:1.0)
                goto L_0x00e7
            L_0x00e6:
                r2 = 0
            L_0x00e7:
                int r6 = org.telegram.messenger.AndroidUtilities.getOffsetColor(r6, r0, r2, r3)
                r5.setColors(r6, r6)
                r6 = 2131627936(0x7f0e0fa0, float:1.888315E38)
                java.lang.String r0 = "VoipGroupInviteMember"
                java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r0, r6)
                r0 = 2131165249(0x7var_, float:1.794471E38)
                r5.setTextAndIcon(r6, r0, r1)
            L_0x00fd:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.GroupCallActivity.ListAdapter.onBindViewHolder(androidx.recyclerview.widget.RecyclerView$ViewHolder, int):void");
        }

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            int itemViewType = viewHolder.getItemViewType();
            if (itemViewType == 1) {
                return !((GroupCallUserCell) viewHolder.itemView).isSelfUser();
            }
            if (itemViewType == 3) {
                return false;
            }
            return true;
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
            if (i != this.selfUserRow) {
                return (i < this.usersStartRow || i >= this.usersEndRow) ? 2 : 1;
            }
            return 1;
        }
    }

    public void setOldRows(int i, int i2, int i3, int i4, int i5, int i6) {
        this.oldAddMemberRow = i;
        this.oldSelfUserRow = i2;
        this.oldUsersStartRow = i3;
        this.oldUsersEndRow = i4;
        this.oldInvitedStartRow = i5;
        this.oldInvitedEndRow = i6;
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
                GroupCallActivity.this.lambda$toggleAdminSpeak$34$GroupCallActivity(tLObject, tLRPC$TL_error);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$toggleAdminSpeak$34 */
    public /* synthetic */ void lambda$toggleAdminSpeak$34$GroupCallActivity(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLObject != null) {
            this.accountInstance.getMessagesController().processUpdates((TLRPC$Updates) tLObject, false);
        }
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        return new ArrayList<>();
    }
}
