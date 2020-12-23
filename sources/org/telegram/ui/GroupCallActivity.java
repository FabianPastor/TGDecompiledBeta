package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Property;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListUpdateCallback;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.voip.VoIPBaseService;
import org.telegram.messenger.voip.VoIPService;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$ChatFull;
import org.telegram.tgnet.TLRPC$ExportedChatInvite;
import org.telegram.tgnet.TLRPC$GroupCall;
import org.telegram.tgnet.TLRPC$TL_chatInviteExported;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_groupCallDiscarded;
import org.telegram.tgnet.TLRPC$TL_groupCallParticipant;
import org.telegram.tgnet.TLRPC$TL_inputUser;
import org.telegram.tgnet.TLRPC$TL_messages_exportChatInvite;
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
                if (tLRPC$TL_groupCallParticipant.user_id == tLRPC$TL_groupCallParticipant2.user_id) {
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
                float access$200 = this.time + ((BlobDrawable.GRADIENT_SPEED_MIN + 0.5f) * f2) + (f2 * BlobDrawable.GRADIENT_SPEED_MAX * 2.0f * GroupCallActivity.this.amplitude);
                this.time = access$200;
                float f3 = this.duration;
                if (access$200 > f3) {
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
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.webRtcMicAmplitudeEvent);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didEndCall);
        super.dismiss();
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        String str;
        TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant;
        RecyclerView.ViewHolder findViewHolderForAdapterPosition;
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
                for (int i3 = 0; i3 < childCount; i3++) {
                    View childAt = this.listView.getChildAt(i3);
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
        } else if (i == NotificationCenter.didLoadChatAdmins && objArr[0].intValue() == this.currentChat.id) {
            updateItems();
            updateState(isShowing(), false);
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
            if (findContainingViewHolder2 != null && (view == null || i > findContainingViewHolder2.getAdapterPosition())) {
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
                } else if (childAt2 instanceof GroupCallInvitedCell) {
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
            org.telegram.ui.GroupCallActivity$19 r9 = new org.telegram.ui.GroupCallActivity$19
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
            org.telegram.messenger.NotificationCenter r10 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r11 = org.telegram.messenger.NotificationCenter.webRtcMicAmplitudeEvent
            r10.addObserver(r0, r11)
            org.telegram.messenger.NotificationCenter r10 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
            int r11 = org.telegram.messenger.NotificationCenter.didEndCall
            r10.addObserver(r0, r11)
            android.content.res.Resources r10 = r29.getResources()
            r11 = 2131165981(0x7var_d, float:1.7946194E38)
            android.graphics.drawable.Drawable r10 = r10.getDrawable(r11)
            android.graphics.drawable.Drawable r10 = r10.mutate()
            r0.shadowDrawable = r10
            org.telegram.ui.Components.RLottieDrawable r10 = new org.telegram.ui.Components.RLottieDrawable
            r11 = 1105199104(0x41e00000, float:28.0)
            int r14 = org.telegram.messenger.AndroidUtilities.dp(r11)
            r11 = 1108869120(0x42180000, float:38.0)
            int r15 = org.telegram.messenger.AndroidUtilities.dp(r11)
            r12 = 2131558459(0x7f0d003b, float:1.8742234E38)
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
            r13 = 2131627727(0x7f0e0ecf, float:1.8882727E38)
            java.lang.String r25 = org.telegram.messenger.LocaleController.getString(r11, r13)
            r20 = 2131165316(0x7var_, float:1.7944846E38)
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
        L_0x038c:
            java.lang.String r11 = "voipgroup_actionBarItems"
            if (r7 >= r4) goto L_0x0417
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
            if (r7 != r6) goto L_0x0413
            android.widget.TextView[] r11 = r0.muteLabel
            r11 = r11[r7]
            r11.setVisibility(r5)
            android.widget.TextView[] r11 = r0.muteSubLabel
            r11 = r11[r7]
            r11.setVisibility(r5)
        L_0x0413:
            int r7 = r7 + 1
            goto L_0x038c
        L_0x0417:
            org.telegram.ui.GroupCallActivity$10 r7 = new org.telegram.ui.GroupCallActivity$10
            r7.<init>(r1)
            r0.actionBar = r7
            r12 = 2131165466(0x7var_a, float:1.794515E38)
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
            r14 = 2131165473(0x7var_, float:1.7945164E38)
            r7.setIcon((int) r14)
            org.telegram.ui.ActionBar.ActionBarMenuItem r7 = r0.otherItem
            r14 = 2131623985(0x7f0e0031, float:1.8875137E38)
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
            r7 = 2131165815(0x7var_, float:1.7945858E38)
            r5.setIcon((int) r7)
            org.telegram.ui.ActionBar.ActionBarMenuItem r5 = r0.pipItem
            r7 = 2131624008(0x7f0e0048, float:1.8875184E38)
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
            r7 = 2131627748(0x7f0e0ee4, float:1.888277E38)
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
        L_0x0610:
            if (r5 >= r4) goto L_0x065b
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
            if (r7 < r9) goto L_0x063d
            org.telegram.ui.Components.UndoView[] r7 = r0.undoView
            r7 = r7[r5]
            r9 = 1084227584(0x40a00000, float:5.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            float r9 = (float) r9
            r7.setTranslationZ(r9)
        L_0x063d:
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
            goto L_0x0610
        L_0x065b:
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.otherItem
            r5 = 2131627710(0x7f0e0ebe, float:1.8882692E38)
            java.lang.String r7 = "VoipGroupAllCanSpeak"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r7, r5)
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r1 = r1.addSubItem((int) r6, (int) r3, (java.lang.CharSequence) r5, (boolean) r6)
            r0.everyoneItem = r1
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.otherItem
            r5 = 2131627736(0x7f0e0ed8, float:1.8882745E38)
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
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.otherItem
            r5 = 2131165748(0x7var_, float:1.7945722E38)
            r6 = 2131627742(0x7f0e0ede, float:1.8882757E38)
            java.lang.String r7 = "VoipGroupShareInviteLink"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
            r7 = 3
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r1 = r1.addSubItem(r7, r5, r6)
            r0.inviteItem = r1
            org.telegram.ui.ActionBar.ActionBarMenuItem r1 = r0.otherItem
            r5 = 2131165733(0x7var_, float:1.7945691E38)
            r6 = 2131627718(0x7f0e0ec6, float:1.8882708E38)
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
            org.telegram.tgnet.TLRPC$GroupCall r2 = r2.call
            int r2 = r2.participants_count
            org.telegram.ui.GroupCallActivity$ListAdapter r5 = r0.listAdapter
            boolean r5 = r5.addSelfToCounter()
            int r2 = r2 + r5
            java.lang.String r5 = "Participants"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatPluralString(r5, r2)
            r1.setSubtitle(r2)
            org.telegram.ui.ActionBar.ActionBar r1 = r0.actionBar
            r2 = 1111490560(0x42400000, float:48.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r2 = r2 * 2
            r1.setTitleRightMargin(r2)
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
                    ((GroupCallUserCell) findViewHolderForAdapterPosition.itemView).setAmplitude((double) (fArr[i] * 15.0f));
                    if (findViewHolderForAdapterPosition.itemView == this.scrimView) {
                        this.containerView.invalidate();
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
                Bundle bundle = new Bundle();
                bundle.putInt("user_id", groupCallUserCell.getParticipant().user_id);
                this.parentActivity.lambda$runLinkRequest$38(new ProfileActivity(bundle));
                dismiss();
            }
        } else if (view instanceof GroupCallInvitedCell) {
            GroupCallInvitedCell groupCallInvitedCell = (GroupCallInvitedCell) view;
            if (groupCallInvitedCell.getUser() != null) {
                Bundle bundle2 = new Bundle();
                bundle2.putInt("user_id", groupCallInvitedCell.getUser().id);
                this.parentActivity.lambda$runLinkRequest$38(new ProfileActivity(bundle2));
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
        if (!ChatObject.canManageCalls(this.currentChat)) {
            return false;
        }
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
    public void getLink(boolean z) {
        String str;
        TLRPC$ChatFull chatFull = this.accountInstance.getMessagesController().getChatFull(this.currentChat.id);
        if (!TextUtils.isEmpty(this.currentChat.username)) {
            str = this.accountInstance.getMessagesController().linkPrefix + "/" + this.currentChat.username;
        } else {
            if (chatFull != null) {
                TLRPC$ExportedChatInvite tLRPC$ExportedChatInvite = chatFull.exported_invite;
                if (tLRPC$ExportedChatInvite instanceof TLRPC$TL_chatInviteExported) {
                    str = tLRPC$ExportedChatInvite.link;
                }
            }
            str = null;
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
                    GroupCallActivity.this.lambda$getLink$18$GroupCallActivity(this.f$1, this.f$2, tLObject, tLRPC$TL_error);
                }
            });
            return;
        }
        openShareAlert(str, z);
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
            TLRPC$ExportedChatInvite tLRPC$ExportedChatInvite = (TLRPC$ExportedChatInvite) tLObject;
            if (tLRPC$ChatFull != null) {
                tLRPC$ChatFull.exported_invite = tLRPC$ExportedChatInvite;
            }
            openShareAlert(tLRPC$ExportedChatInvite.link, z);
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
        processSelectedOption(i, 3);
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
            r0 = 2131627743(0x7f0e0edf, float:1.888276E38)
            java.lang.String r3 = "VoipGroupUnmute"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r3, r0)
            r3 = 2131627750(0x7f0e0ee6, float:1.8882773E38)
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
            r1 = 2131627793(0x7f0e0var_, float:1.888286E38)
            java.lang.String r3 = "VoipTapToMute"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r3, r1)
            org.telegram.ui.Components.RLottieDrawable r3 = r9.bigMicDrawable
            r4 = 24
            boolean r3 = r3.setCustomEndFrame(r4)
            goto L_0x0072
        L_0x0049:
            r3 = 3
            if (r10 != r3) goto L_0x0059
            r3 = 2131624906(0x7f0e03ca, float:1.8877005E38)
            java.lang.String r4 = "Connecting"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r8 = r3
            r3 = r0
            r0 = r8
            goto L_0x006b
        L_0x0059:
            r0 = 2131627757(0x7f0e0eed, float:1.8882787E38)
            java.lang.String r3 = "VoipMutedByAdmin"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r3, r0)
            r3 = 2131627758(0x7f0e0eee, float:1.888279E38)
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
            call2.participants.delete((i = UserConfig.getInstance(call2.currentAccount).clientUserId));
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

    private void processSelectedOption(int i, int i2) {
        TextView textView;
        int i3 = i2;
        TLRPC$User user = this.accountInstance.getMessagesController().getUser(Integer.valueOf(i));
        if (i3 != 0 && i3 != 2 && i3 != 3) {
            VoIPService.getSharedInstance().editCallMember(user, false);
            getUndoView().showWithAction(0, 31, user, (Object) null, (Runnable) null, (Runnable) null);
        } else if (i3 != 0) {
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
            if (i3 == 0) {
                textView3.setText(LocaleController.getString("VoipGroupMuteMemberAlertTitle", NUM));
                textView2.setText(AndroidUtilities.replaceTags(LocaleController.formatString("VoipGroupMuteMemberAlertText", NUM, firstName)));
            } else if (i3 == 2) {
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
            if (i3 == 0) {
                builder.setPositiveButton(LocaleController.getString("VoipGroupMute", NUM), new DialogInterface.OnClickListener(user) {
                    public final /* synthetic */ TLRPC$User f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void onClick(DialogInterface dialogInterface, int i) {
                        GroupCallActivity.this.lambda$processSelectedOption$29$GroupCallActivity(this.f$1, dialogInterface, i);
                    }
                });
            } else if (i3 == 2) {
                builder.setPositiveButton(LocaleController.getString("VoipGroupUserRemove", NUM), new DialogInterface.OnClickListener(user) {
                    public final /* synthetic */ TLRPC$User f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void onClick(DialogInterface dialogInterface, int i) {
                        GroupCallActivity.this.lambda$processSelectedOption$30$GroupCallActivity(this.f$1, dialogInterface, i);
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
                        GroupCallActivity.this.lambda$processSelectedOption$32$GroupCallActivity(this.f$1, this.f$2, dialogInterface, i);
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
            VoIPService.getSharedInstance().editCallMember(user, true);
            getUndoView().showWithAction(0, 30, user, (Object) null, (Runnable) null, (Runnable) null);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$processSelectedOption$29 */
    public /* synthetic */ void lambda$processSelectedOption$29$GroupCallActivity(TLRPC$User tLRPC$User, DialogInterface dialogInterface, int i) {
        if (VoIPService.getSharedInstance() != null) {
            VoIPService.getSharedInstance().editCallMember(tLRPC$User, true);
            getUndoView().showWithAction(0, 30, tLRPC$User, (Object) null, (Runnable) null, (Runnable) null);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$processSelectedOption$30 */
    public /* synthetic */ void lambda$processSelectedOption$30$GroupCallActivity(TLRPC$User tLRPC$User, DialogInterface dialogInterface, int i) {
        this.accountInstance.getMessagesController().deleteUserFromChat(this.currentChat.id, tLRPC$User, (TLRPC$ChatFull) null);
        getUndoView().showWithAction(0, 32, tLRPC$User, (Object) null, (Runnable) null, (Runnable) null);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$processSelectedOption$32 */
    public /* synthetic */ void lambda$processSelectedOption$32$GroupCallActivity(TLRPC$User tLRPC$User, int i, DialogInterface dialogInterface, int i2) {
        this.accountInstance.getMessagesController().addUserToChat(this.currentChat.id, tLRPC$User, 0, (String) null, this.parentActivity.getActionBarLayout().fragmentsStack.get(this.parentActivity.getActionBarLayout().fragmentsStack.size() - 1), new Runnable(i) {
            public final /* synthetic */ int f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                GroupCallActivity.this.lambda$null$31$GroupCallActivity(this.f$1);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$31 */
    public /* synthetic */ void lambda$null$31$GroupCallActivity(int i) {
        inviteUserToCall(i, false);
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v0, resolved type: org.telegram.ui.GroupCallActivity$17} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v1, resolved type: android.widget.ScrollView} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v2, resolved type: org.telegram.ui.GroupCallActivity$17} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v14, resolved type: org.telegram.ui.GroupCallActivity$17} */
    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:11:0x00c0, code lost:
        if (r7.accountInstance.getMessagesController().getAdminRank(r7.currentChat.id, r2.user_id) != null) goto L_0x00c2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x00f5, code lost:
        if ((r15 instanceof org.telegram.tgnet.TLRPC$TL_chatParticipantCreator) == false) goto L_0x00fd;
     */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x010d  */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x012b  */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x014a  */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x016e A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:39:0x016f  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean showMenuForCell(org.telegram.ui.Cells.GroupCallUserCell r17) {
        /*
            r16 = this;
            r7 = r16
            org.telegram.ui.ActionBar.ActionBarPopupWindow r0 = r7.scrimPopupWindow
            r8 = 0
            if (r0 == 0) goto L_0x0010
            r0.dismiss()
            r0 = 0
            r7.scrimPopupWindow = r0
            r7.scrimPopupWindowItems = r0
            return r8
        L_0x0010:
            android.graphics.Rect r0 = new android.graphics.Rect
            r0.<init>()
            org.telegram.ui.ActionBar.ActionBarPopupWindow$ActionBarPopupWindowLayout r9 = new org.telegram.ui.ActionBar.ActionBarPopupWindow$ActionBarPopupWindowLayout
            android.content.Context r1 = r16.getContext()
            r9.<init>(r1)
            org.telegram.ui.GroupCallActivity$16 r1 = new org.telegram.ui.GroupCallActivity$16
            r1.<init>(r0)
            r9.setOnTouchListener(r1)
            org.telegram.ui.-$$Lambda$GroupCallActivity$eGz0L42ZNq-g6JIixlCAiHxuJAU r0 = new org.telegram.ui.-$$Lambda$GroupCallActivity$eGz0L42ZNq-g6JIixlCAiHxuJAU
            r0.<init>()
            r9.setDispatchKeyEventListener(r0)
            android.graphics.Rect r0 = new android.graphics.Rect
            r0.<init>()
            android.content.Context r1 = r16.getContext()
            android.content.res.Resources r1 = r1.getResources()
            r2 = 2131165917(0x7var_dd, float:1.7946065E38)
            android.graphics.drawable.Drawable r1 = r1.getDrawable(r2)
            android.graphics.drawable.Drawable r1 = r1.mutate()
            r1.getPadding(r0)
            r9.setBackgroundDrawable(r1)
            int r0 = r7.backgroundColor
            r9.setBackgroundColor(r0)
            android.widget.LinearLayout r10 = new android.widget.LinearLayout
            android.content.Context r0 = r16.getContext()
            r10.<init>(r0)
            int r0 = android.os.Build.VERSION.SDK_INT
            r1 = 21
            if (r0 < r1) goto L_0x0073
            org.telegram.ui.GroupCallActivity$17 r11 = new org.telegram.ui.GroupCallActivity$17
            android.content.Context r2 = r16.getContext()
            r3 = 0
            r4 = 0
            r5 = 2131689504(0x7f0var_, float:1.9008025E38)
            r0 = r11
            r1 = r16
            r6 = r10
            r0.<init>(r1, r2, r3, r4, r5, r6)
            goto L_0x007c
        L_0x0073:
            android.widget.ScrollView r11 = new android.widget.ScrollView
            android.content.Context r0 = r16.getContext()
            r11.<init>(r0)
        L_0x007c:
            r11.setClipToPadding(r8)
            r0 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
            r1 = -2
            android.widget.FrameLayout$LayoutParams r0 = org.telegram.ui.Components.LayoutHelper.createFrame(r1, r0)
            r9.addView(r11, r0)
            r0 = 1128792064(0x43480000, float:200.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r10.setMinimumWidth(r0)
            r0 = 1
            r10.setOrientation(r0)
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r2 = r17.getParticipant()
            java.util.ArrayList r3 = new java.util.ArrayList
            r4 = 2
            r3.<init>(r4)
            java.util.ArrayList r5 = new java.util.ArrayList
            r5.<init>(r4)
            java.util.ArrayList r6 = new java.util.ArrayList
            r6.<init>(r4)
            org.telegram.tgnet.TLRPC$Chat r12 = r7.currentChat
            boolean r12 = r12.megagroup
            if (r12 == 0) goto L_0x00c4
            org.telegram.messenger.AccountInstance r12 = r7.accountInstance
            org.telegram.messenger.MessagesController r12 = r12.getMessagesController()
            org.telegram.tgnet.TLRPC$Chat r13 = r7.currentChat
            int r13 = r13.id
            int r14 = r2.user_id
            java.lang.String r12 = r12.getAdminRank(r13, r14)
            if (r12 == 0) goto L_0x00fd
        L_0x00c2:
            r12 = 1
            goto L_0x00fe
        L_0x00c4:
            org.telegram.messenger.AccountInstance r12 = r7.accountInstance
            org.telegram.messenger.MessagesController r12 = r12.getMessagesController()
            org.telegram.tgnet.TLRPC$Chat r13 = r7.currentChat
            int r13 = r13.id
            org.telegram.tgnet.TLRPC$ChatFull r12 = r12.getChatFull(r13)
            if (r12 == 0) goto L_0x00fd
            org.telegram.tgnet.TLRPC$ChatParticipants r13 = r12.participants
            java.util.ArrayList<org.telegram.tgnet.TLRPC$ChatParticipant> r13 = r13.participants
            int r13 = r13.size()
            r14 = 0
        L_0x00dd:
            if (r14 >= r13) goto L_0x00fd
            org.telegram.tgnet.TLRPC$ChatParticipants r15 = r12.participants
            java.util.ArrayList<org.telegram.tgnet.TLRPC$ChatParticipant> r15 = r15.participants
            java.lang.Object r15 = r15.get(r14)
            org.telegram.tgnet.TLRPC$ChatParticipant r15 = (org.telegram.tgnet.TLRPC$ChatParticipant) r15
            int r1 = r15.user_id
            int r4 = r2.user_id
            if (r1 != r4) goto L_0x00f8
            boolean r1 = r15 instanceof org.telegram.tgnet.TLRPC$TL_chatParticipantAdmin
            if (r1 != 0) goto L_0x00c2
            boolean r1 = r15 instanceof org.telegram.tgnet.TLRPC$TL_chatParticipantCreator
            if (r1 == 0) goto L_0x00fd
            goto L_0x00c2
        L_0x00f8:
            int r14 = r14 + 1
            r1 = -2
            r4 = 2
            goto L_0x00dd
        L_0x00fd:
            r12 = 0
        L_0x00fe:
            if (r12 == 0) goto L_0x0104
            boolean r1 = r2.muted
            if (r1 != 0) goto L_0x0148
        L_0x0104:
            boolean r1 = r2.muted
            if (r1 == 0) goto L_0x012b
            boolean r1 = r2.can_self_unmute
            if (r1 == 0) goto L_0x010d
            goto L_0x012b
        L_0x010d:
            r1 = 2131627711(0x7f0e0ebf, float:1.8882694E38)
            java.lang.String r4 = "VoipGroupAllowToSpeak"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r4, r1)
            r3.add(r1)
            r1 = 2131165816(0x7var_, float:1.794586E38)
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            r5.add(r1)
            java.lang.Integer r1 = java.lang.Integer.valueOf(r0)
            r6.add(r1)
            goto L_0x0148
        L_0x012b:
            r1 = 2131627731(0x7f0e0ed3, float:1.8882735E38)
            java.lang.String r4 = "VoipGroupMute"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r4, r1)
            r3.add(r1)
            r1 = 2131165814(0x7var_, float:1.7945856E38)
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            r5.add(r1)
            java.lang.Integer r1 = java.lang.Integer.valueOf(r8)
            r6.add(r1)
        L_0x0148:
            if (r12 != 0) goto L_0x0168
            r1 = 2131627746(0x7f0e0ee2, float:1.8882765E38)
            java.lang.String r4 = "VoipGroupUserRemove"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r4, r1)
            r3.add(r1)
            r1 = 2131165714(0x7var_, float:1.7945653E38)
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            r5.add(r1)
            r1 = 2
            java.lang.Integer r4 = java.lang.Integer.valueOf(r1)
            r6.add(r4)
        L_0x0168:
            boolean r1 = r6.isEmpty()
            if (r1 == 0) goto L_0x016f
            return r8
        L_0x016f:
            int r1 = r3.size()
            org.telegram.ui.ActionBar.ActionBarMenuSubItem[] r1 = new org.telegram.ui.ActionBar.ActionBarMenuSubItem[r1]
            r7.scrimPopupWindowItems = r1
            int r1 = r3.size()
            r4 = 0
        L_0x017c:
            if (r4 >= r1) goto L_0x01ec
            org.telegram.ui.ActionBar.ActionBarMenuSubItem r12 = new org.telegram.ui.ActionBar.ActionBarMenuSubItem
            android.content.Context r13 = r16.getContext()
            if (r4 != 0) goto L_0x0188
            r14 = 1
            goto L_0x0189
        L_0x0188:
            r14 = 0
        L_0x0189:
            int r15 = r1 + -1
            if (r4 != r15) goto L_0x018f
            r15 = 1
            goto L_0x0190
        L_0x018f:
            r15 = 0
        L_0x0190:
            r12.<init>(r13, r14, r15)
            java.lang.Object r13 = r6.get(r4)
            java.lang.Integer r13 = (java.lang.Integer) r13
            int r13 = r13.intValue()
            r14 = 2
            if (r13 == r14) goto L_0x01af
            java.lang.String r13 = "voipgroup_actionBarItems"
            int r14 = org.telegram.ui.ActionBar.Theme.getColor(r13)
            int r13 = org.telegram.ui.ActionBar.Theme.getColor(r13)
            r12.setColors(r14, r13)
            goto L_0x01bd
        L_0x01af:
            java.lang.String r13 = "voipgroup_leaveCallMenu"
            int r14 = org.telegram.ui.ActionBar.Theme.getColor(r13)
            int r13 = org.telegram.ui.ActionBar.Theme.getColor(r13)
            r12.setColors(r14, r13)
        L_0x01bd:
            java.lang.String r13 = "voipgroup_listSelector"
            int r13 = org.telegram.ui.ActionBar.Theme.getColor(r13)
            r12.setSelectorColor(r13)
            java.lang.Object r13 = r3.get(r4)
            java.lang.CharSequence r13 = (java.lang.CharSequence) r13
            java.lang.Object r14 = r5.get(r4)
            java.lang.Integer r14 = (java.lang.Integer) r14
            int r14 = r14.intValue()
            r12.setTextAndIcon(r13, r14)
            org.telegram.ui.ActionBar.ActionBarMenuSubItem[] r13 = r7.scrimPopupWindowItems
            r13[r4] = r12
            r10.addView(r12)
            org.telegram.ui.-$$Lambda$GroupCallActivity$HWLmZ4y0yiJbQIIuwXXs8Js-ru4 r13 = new org.telegram.ui.-$$Lambda$GroupCallActivity$HWLmZ4y0yiJbQIIuwXXs8Js-ru4
            r13.<init>(r4, r6, r2)
            r12.setOnClickListener(r13)
            int r4 = r4 + 1
            goto L_0x017c
        L_0x01ec:
            r1 = 51
            r2 = -2
            android.widget.FrameLayout$LayoutParams r3 = org.telegram.ui.Components.LayoutHelper.createScroll(r2, r2, r1)
            r11.addView(r10, r3)
            org.telegram.ui.GroupCallActivity$18 r3 = new org.telegram.ui.GroupCallActivity$18
            r3.<init>(r9, r2, r2)
            r7.scrimPopupWindow = r3
            r3.setPauseNotifications(r0)
            org.telegram.ui.ActionBar.ActionBarPopupWindow r2 = r7.scrimPopupWindow
            r3 = 220(0xdc, float:3.08E-43)
            r2.setDismissAnimationDuration(r3)
            org.telegram.ui.ActionBar.ActionBarPopupWindow r2 = r7.scrimPopupWindow
            r2.setOutsideTouchable(r0)
            org.telegram.ui.ActionBar.ActionBarPopupWindow r2 = r7.scrimPopupWindow
            r2.setClippingEnabled(r0)
            org.telegram.ui.ActionBar.ActionBarPopupWindow r2 = r7.scrimPopupWindow
            r3 = 2131689477(0x7f0var_, float:1.900797E38)
            r2.setAnimationStyle(r3)
            org.telegram.ui.ActionBar.ActionBarPopupWindow r2 = r7.scrimPopupWindow
            r2.setFocusable(r0)
            r2 = 1148846080(0x447a0000, float:1000.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r4 = -2147483648(0xfffffffvar_, float:-0.0)
            int r3 = android.view.View.MeasureSpec.makeMeasureSpec(r3, r4)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            int r2 = android.view.View.MeasureSpec.makeMeasureSpec(r2, r4)
            r9.measure(r3, r2)
            org.telegram.ui.ActionBar.ActionBarPopupWindow r2 = r7.scrimPopupWindow
            r3 = 2
            r2.setInputMethodMode(r3)
            org.telegram.ui.ActionBar.ActionBarPopupWindow r2 = r7.scrimPopupWindow
            r2.setSoftInputMode(r8)
            org.telegram.ui.ActionBar.ActionBarPopupWindow r2 = r7.scrimPopupWindow
            android.view.View r2 = r2.getContentView()
            r2.setFocusableInTouchMode(r0)
            r2 = 1096810496(0x41600000, float:14.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            org.telegram.ui.Components.RecyclerListView r3 = r7.listView
            int r3 = r3.getMeasuredWidth()
            int r2 = r2 + r3
            r3 = 1086324736(0x40CLASSNAME, float:6.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r2 = r2 + r3
            int r3 = r9.getMeasuredWidth()
            int r2 = r2 - r3
            org.telegram.ui.Components.RecyclerListView r3 = r7.listView
            float r3 = r3.getY()
            float r4 = r17.getY()
            float r3 = r3 + r4
            int r4 = r17.getMeasuredHeight()
            float r4 = (float) r4
            float r3 = r3 + r4
            int r3 = (int) r3
            org.telegram.ui.ActionBar.ActionBarPopupWindow r4 = r7.scrimPopupWindow
            org.telegram.ui.Components.RecyclerListView r5 = r7.listView
            r4.showAtLocation(r5, r1, r2, r3)
            org.telegram.ui.Components.RecyclerListView r1 = r7.listView
            r1.stopScroll()
            org.telegram.ui.Components.FillLastLinearLayoutManager r1 = r7.layoutManager
            r1.setCanScrollVertically(r8)
            r1 = r17
            r7.scrimView = r1
            android.view.ViewGroup r1 = r7.containerView
            r1.invalidate()
            org.telegram.ui.Components.RecyclerListView r1 = r7.listView
            r1.invalidate()
            android.animation.AnimatorSet r1 = r7.scrimAnimatorSet
            if (r1 == 0) goto L_0x029a
            r1.cancel()
        L_0x029a:
            android.animation.AnimatorSet r1 = new android.animation.AnimatorSet
            r1.<init>()
            r7.scrimAnimatorSet = r1
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
            android.graphics.Paint r2 = r7.scrimPaint
            android.util.Property<android.graphics.Paint, java.lang.Integer> r3 = org.telegram.ui.Components.AnimationProperties.PAINT_ALPHA
            r4 = 2
            int[] r4 = new int[r4]
            r4 = {0, 100} // fill-array
            android.animation.ObjectAnimator r2 = android.animation.ObjectAnimator.ofInt(r2, r3, r4)
            r1.add(r2)
            android.animation.AnimatorSet r2 = r7.scrimAnimatorSet
            r2.playTogether(r1)
            android.animation.AnimatorSet r1 = r7.scrimAnimatorSet
            r2 = 150(0x96, double:7.4E-322)
            r1.setDuration(r2)
            android.animation.AnimatorSet r1 = r7.scrimAnimatorSet
            r1.start()
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.GroupCallActivity.showMenuForCell(org.telegram.ui.Cells.GroupCallUserCell):boolean");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$showMenuForCell$33 */
    public /* synthetic */ void lambda$showMenuForCell$33$GroupCallActivity(KeyEvent keyEvent) {
        ActionBarPopupWindow actionBarPopupWindow;
        if (keyEvent.getKeyCode() == 4 && keyEvent.getRepeatCount() == 0 && (actionBarPopupWindow = this.scrimPopupWindow) != null && actionBarPopupWindow.isShowing()) {
            this.scrimPopupWindow.dismiss();
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$showMenuForCell$34 */
    public /* synthetic */ void lambda$showMenuForCell$34$GroupCallActivity(int i, ArrayList arrayList, TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant, View view) {
        if (i < arrayList.size()) {
            processSelectedOption(tLRPC$TL_groupCallParticipant.user_id, ((Integer) arrayList.get(i)).intValue());
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
                r6 = 2131627721(0x7f0e0ec9, float:1.8882714E38)
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
                GroupCallActivity.this.lambda$toggleAdminSpeak$35$GroupCallActivity(tLObject, tLRPC$TL_error);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$toggleAdminSpeak$35 */
    public /* synthetic */ void lambda$toggleAdminSpeak$35$GroupCallActivity(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLObject != null) {
            this.accountInstance.getMessagesController().processUpdates((TLRPC$Updates) tLObject, false);
        }
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        return new ArrayList<>();
    }
}
