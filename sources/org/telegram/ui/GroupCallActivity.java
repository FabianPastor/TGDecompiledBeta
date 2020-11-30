package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
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
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.voip.NativeInstance;
import org.telegram.messenger.voip.VoIPBaseService;
import org.telegram.messenger.voip.VoIPService;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$ChatFull;
import org.telegram.tgnet.TLRPC$ExportedChatInvite;
import org.telegram.tgnet.TLRPC$TL_chatInviteExported;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_groupCallDiscarded;
import org.telegram.tgnet.TLRPC$TL_groupCallParticipant;
import org.telegram.tgnet.TLRPC$TL_inputUser;
import org.telegram.tgnet.TLRPC$TL_inputUserSelf;
import org.telegram.tgnet.TLRPC$TL_messages_exportChatInvite;
import org.telegram.tgnet.TLRPC$TL_phone_editGroupCallMember;
import org.telegram.tgnet.TLRPC$TL_phone_inviteToGroupCall;
import org.telegram.tgnet.TLRPC$TL_phone_toggleGroupCallSettings;
import org.telegram.tgnet.TLRPC$Updates;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarLayout;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.CheckBoxCell;
import org.telegram.ui.Cells.GroupCallTextCell;
import org.telegram.ui.Cells.GroupCallUserCell;
import org.telegram.ui.ChatUsersActivity;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.BlobDrawable;
import org.telegram.ui.Components.ChatAvatarContainer;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.RLottieImageView;
import org.telegram.ui.Components.RadialProgressView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.ShareAlert;
import org.telegram.ui.Components.voip.VoIPToggleButton;
import org.telegram.ui.GroupCallActivity;

public class GroupCallActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate, VoIPBaseService.StateListener {
    private static int MUTE_BUTTON_STATE_CONNECTING = 3;
    /* access modifiers changed from: private */
    public static int MUTE_BUTTON_STATE_MUTE = 1;
    private static int MUTE_BUTTON_STATE_MUTED_BY_ADMIN = 2;
    /* access modifiers changed from: private */
    public static int MUTE_BUTTON_STATE_UNMUTE;
    /* access modifiers changed from: private */
    public ActionBarMenuSubItem adminItem;
    /* access modifiers changed from: private */
    public float amplitude;
    /* access modifiers changed from: private */
    public float animateAmplitudeDiff;
    /* access modifiers changed from: private */
    public float animateToAmplitude;
    private ChatAvatarContainer avatarContainer;
    private RLottieDrawable bigMicDrawable;
    /* access modifiers changed from: private */
    public BlobDrawable bigWaveDrawable;
    /* access modifiers changed from: private */
    public BlobDrawable buttonWaveDrawable;
    private FrameLayout buttonsContainer;
    public ChatObject.Call call;
    private int currentCallState;
    public TLRPC$Chat currentChat;
    /* access modifiers changed from: private */
    public WeavingState currentState;
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
        }
    };
    private View dividerItem;
    /* access modifiers changed from: private */
    public ActionBarMenuSubItem everyoneItem;
    private ActionBarMenuSubItem inviteItem;
    /* access modifiers changed from: private */
    public long lastUpdateTime;
    /* access modifiers changed from: private */
    public LinearLayoutManager layoutManager;
    /* access modifiers changed from: private */
    public Paint leaveBackgroundPaint = new Paint(1);
    /* access modifiers changed from: private */
    public VoIPToggleButton leaveButton;
    /* access modifiers changed from: private */
    public ListAdapter listAdapter;
    /* access modifiers changed from: private */
    public RecyclerListView listView;
    /* access modifiers changed from: private */
    public Paint listViewBackgroundPaint = new Paint(1);
    /* access modifiers changed from: private */
    public RLottieImageView muteButton;
    /* access modifiers changed from: private */
    public ValueAnimator muteButtonAnimator;
    /* access modifiers changed from: private */
    public int muteButtonState = MUTE_BUTTON_STATE_UNMUTE;
    /* access modifiers changed from: private */
    public TextView[] muteLabel = new TextView[2];
    /* access modifiers changed from: private */
    public TextView[] muteSubLabel = new TextView[2];
    /* access modifiers changed from: private */
    public int oldAddMemberRow;
    /* access modifiers changed from: private */
    public int oldCount;
    /* access modifiers changed from: private */
    public ArrayList<TLRPC$TL_groupCallParticipant> oldParticipants = new ArrayList<>();
    /* access modifiers changed from: private */
    public int oldSelfUserRow;
    /* access modifiers changed from: private */
    public int oldUsersStartRow;
    /* access modifiers changed from: private */
    public Paint paint = new Paint(7);
    /* access modifiers changed from: private */
    public WeavingState prevState;
    /* access modifiers changed from: private */
    public RadialGradient radialGradient;
    /* access modifiers changed from: private */
    public Matrix radialMatrix;
    /* access modifiers changed from: private */
    public Paint radialPaint;
    /* access modifiers changed from: private */
    public RadialProgressView radialProgressView;
    /* access modifiers changed from: private */
    public TLRPC$TL_groupCallParticipant selfDummyParticipant;
    /* access modifiers changed from: private */
    public float showWavesProgress;
    /* access modifiers changed from: private */
    public VoIPToggleButton soundButton;
    private WeavingState[] states = new WeavingState[4];
    /* access modifiers changed from: private */
    public float switchProgress = 1.0f;
    /* access modifiers changed from: private */
    public BlobDrawable tinyWaveDrawable;

    static /* synthetic */ boolean lambda$createView$2(View view, MotionEvent motionEvent) {
        return true;
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
            float f = this.duration;
            if (f == 0.0f || this.time >= f) {
                this.duration = (float) (Utilities.random.nextInt(700) + 500);
                this.time = 0.0f;
                if (this.targetX == -1.0f) {
                    if (this.currentState == GroupCallActivity.MUTE_BUTTON_STATE_MUTE) {
                        this.targetX = ((((float) Utilities.random.nextInt(100)) * 0.3f) / 100.0f) + 0.2f;
                        this.targetY = ((((float) Utilities.random.nextInt(100)) * 0.3f) / 100.0f) + 0.7f;
                    } else {
                        this.targetX = ((((float) Utilities.random.nextInt(100)) / 100.0f) * 0.2f) + 0.8f;
                        this.targetY = ((float) Utilities.random.nextInt(100)) / 100.0f;
                    }
                }
                this.startX = this.targetX;
                this.startY = this.targetY;
                if (this.currentState == GroupCallActivity.MUTE_BUTTON_STATE_MUTE) {
                    this.targetX = ((((float) Utilities.random.nextInt(100)) * 0.3f) / 100.0f) + 0.2f;
                    this.targetY = ((((float) Utilities.random.nextInt(100)) * 0.3f) / 100.0f) + 0.7f;
                } else {
                    this.targetX = ((((float) Utilities.random.nextInt(100)) / 100.0f) * 0.2f) + 0.8f;
                    this.targetY = ((float) Utilities.random.nextInt(100)) / 100.0f;
                }
            }
            float f2 = (float) j;
            float access$100 = this.time + ((BlobDrawable.GRADIENT_SPEED_MIN + 0.5f) * f2) + (f2 * BlobDrawable.GRADIENT_SPEED_MAX * 2.0f * GroupCallActivity.this.amplitude);
            this.time = access$100;
            float f3 = this.duration;
            if (access$100 > f3) {
                this.time = f3;
            }
            float interpolation = CubicBezierInterpolator.EASE_OUT.getInterpolation(this.time / f3);
            float f4 = (float) i3;
            float f5 = this.startX;
            float f6 = (((float) i2) + ((f5 + ((this.targetX - f5) * interpolation)) * f4)) - 200.0f;
            float f7 = this.startY;
            float f8 = (((float) i) + (f4 * (f7 + ((this.targetY - f7) * interpolation)))) - 200.0f;
            float dp = (((float) AndroidUtilities.dp(122.0f)) / 400.0f) * (this.currentState == GroupCallActivity.MUTE_BUTTON_STATE_MUTE ? 4.0f : 2.5f);
            this.matrix.reset();
            this.matrix.postTranslate(f6, f8);
            this.matrix.postScale(dp, dp, f6 + 200.0f, f8 + 200.0f);
            this.shader.setLocalMatrix(this.matrix);
        }
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        String str;
        TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant;
        int indexOf;
        RecyclerView.ViewHolder findViewHolderForAdapterPosition;
        if (i == NotificationCenter.groupCallUpdated) {
            Long l = objArr[1];
            ChatObject.Call call2 = this.call;
            if (call2 != null && call2.call.id == l.longValue()) {
                if (this.call.call instanceof TLRPC$TL_groupCallDiscarded) {
                    ArrayList<BaseFragment> arrayList = this.parentLayout.fragmentsStack;
                    if (arrayList.get(arrayList.size() - 1) == this) {
                        finishFragment();
                    } else {
                        removeSelfFromStack();
                    }
                } else {
                    updateItems();
                    if (this.listAdapter != null) {
                        int childCount = this.listView.getChildCount();
                        for (int i3 = 0; i3 < childCount; i3++) {
                            View childAt = this.listView.getChildAt(i3);
                            if (childAt instanceof GroupCallUserCell) {
                                ((GroupCallUserCell) childAt).applyParticipantChanges(true);
                            }
                        }
                        try {
                            ListAdapter listAdapter2 = this.listAdapter;
                            UpdateCallback updateCallback = new UpdateCallback(listAdapter2);
                            setOldRows(listAdapter2.addMemberRow, this.listAdapter.selfUserRow, this.listAdapter.usersStartRow);
                            this.listAdapter.updateRows();
                            DiffUtil.calculateDiff(this.diffUtilsCallback).dispatchUpdatesTo((ListUpdateCallback) updateCallback);
                        } catch (Exception e) {
                            FileLog.e((Throwable) e);
                            this.listAdapter.notifyDataSetChanged();
                        }
                    }
                    this.oldParticipants.clear();
                    this.oldParticipants.addAll(this.call.sortedParticipants);
                    this.oldCount = this.listAdapter.getItemCount();
                    if (this.avatarContainer != null) {
                        int i4 = this.call.call.participants_count + (this.listAdapter.addSelfToCounter() ? 1 : 0);
                        if (i4 == 2) {
                            FileLog.d("test");
                        }
                        this.avatarContainer.setSubtitle(LocaleController.formatPluralString("Members", i4));
                    }
                    updateState(true);
                }
            }
        } else if (i == NotificationCenter.webRtcAmplitudeEvent) {
            float floatValue = objArr[0].floatValue();
            setAmplitude((double) floatValue);
            if (this.listView != null && (tLRPC$TL_groupCallParticipant = this.call.participants.get(getUserConfig().getClientUserId())) != null && (indexOf = this.call.sortedParticipants.indexOf(tLRPC$TL_groupCallParticipant)) >= 0 && (findViewHolderForAdapterPosition = this.listView.findViewHolderForAdapterPosition(indexOf + this.listAdapter.usersStartRow)) != null) {
                ((GroupCallUserCell) findViewHolderForAdapterPosition.itemView).setAmplitude((double) (floatValue * 15.0f));
            }
        } else if (i == NotificationCenter.needShowAlert && objArr[0].intValue() == 6) {
            String str2 = objArr[1];
            if ("GROUP_CALL_ANONYMOUS_FORBIDDEN".equals(str2)) {
                str = LocaleController.getString("VoipGroupJoinAnonymousAdmin", NUM);
            } else {
                str = LocaleController.getString("ErrorOccurred", NUM) + "\n" + str2;
            }
            showDialog(AlertsCreator.createSimpleAlert(getParentActivity(), LocaleController.getString("VoipGroupVoiceChat", NUM), str).create(), new DialogInterface.OnDismissListener() {
                public final void onDismiss(DialogInterface dialogInterface) {
                    GroupCallActivity.this.lambda$didReceivedNotification$0$GroupCallActivity(dialogInterface);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$didReceivedNotification$0 */
    public /* synthetic */ void lambda$didReceivedNotification$0$GroupCallActivity(DialogInterface dialogInterface) {
        finishFragment();
    }

    private void updateItems() {
        if (!TextUtils.isEmpty(this.currentChat.username) || ChatObject.canUserDoAdminAction(this.currentChat, 3)) {
            this.inviteItem.setVisibility(0);
        } else {
            this.inviteItem.setVisibility(8);
        }
        if (!ChatObject.canManageCalls(this.currentChat) || !this.call.call.can_change_join_muted) {
            this.everyoneItem.setVisibility(8);
            this.adminItem.setVisibility(8);
            this.dividerItem.setVisibility(8);
            return;
        }
        this.everyoneItem.setVisibility(0);
        this.adminItem.setVisibility(0);
        this.dividerItem.setVisibility(0);
    }

    public boolean onFragmentCreate() {
        if (VoIPService.getSharedInstance() == null) {
            return false;
        }
        ChatObject.Call call2 = VoIPService.getSharedInstance().groupCall;
        this.call = call2;
        this.oldParticipants.addAll(call2.sortedParticipants);
        if (this.call == null) {
            return false;
        }
        TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant = new TLRPC$TL_groupCallParticipant();
        this.selfDummyParticipant = tLRPC$TL_groupCallParticipant;
        tLRPC$TL_groupCallParticipant.user_id = getUserConfig().getClientUserId();
        TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant2 = this.selfDummyParticipant;
        tLRPC$TL_groupCallParticipant2.muted = true;
        tLRPC$TL_groupCallParticipant2.can_self_unmute = true;
        tLRPC$TL_groupCallParticipant2.date = getConnectionsManager().getCurrentTime();
        this.currentCallState = VoIPService.getSharedInstance().getCallState();
        VoIPService.getSharedInstance().registerStateListener(this);
        TLRPC$Chat chat = getMessagesController().getChat(Integer.valueOf(this.call.chatId));
        this.currentChat = chat;
        if (chat == null) {
            return false;
        }
        VoIPService.audioLevelsCallback = new NativeInstance.AudioLevelsCallback() {
            public final void run(int[] iArr, float[] fArr) {
                GroupCallActivity.this.lambda$onFragmentCreate$1$GroupCallActivity(iArr, fArr);
            }
        };
        getNotificationCenter().addObserver(this, NotificationCenter.groupCallUpdated);
        getNotificationCenter().addObserver(this, NotificationCenter.needShowAlert);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.webRtcAmplitudeEvent);
        return super.onFragmentCreate();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onFragmentCreate$1 */
    public /* synthetic */ void lambda$onFragmentCreate$1$GroupCallActivity(int[] iArr, float[] fArr) {
        int indexOf;
        RecyclerView.ViewHolder findViewHolderForAdapterPosition;
        for (int i = 0; i < iArr.length; i++) {
            TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant = this.call.participantsBySources.get(iArr[i]);
            if (!(tLRPC$TL_groupCallParticipant == null || (indexOf = this.call.sortedParticipants.indexOf(tLRPC$TL_groupCallParticipant)) < 0 || (findViewHolderForAdapterPosition = this.listView.findViewHolderForAdapterPosition(indexOf + this.listAdapter.usersStartRow)) == null)) {
                ((GroupCallUserCell) findViewHolderForAdapterPosition.itemView).setAmplitude((double) (fArr[i] * 15.0f));
            }
        }
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        if (VoIPService.getSharedInstance() != null) {
            VoIPService.getSharedInstance().unregisterStateListener(this);
        }
        getNotificationCenter().removeObserver(this, NotificationCenter.groupCallUpdated);
        getNotificationCenter().removeObserver(this, NotificationCenter.needShowAlert);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.webRtcAmplitudeEvent);
        VoIPService.audioLevelsCallback = null;
    }

    private void setAmplitude(double d) {
        float min = (float) (Math.min(8500.0d, d) / 8500.0d);
        this.animateToAmplitude = min;
        this.animateAmplitudeDiff = (min - this.amplitude) / ((BlobDrawable.AMPLITUDE_SPEED * 500.0f) + 100.0f);
    }

    public void onStateChanged(int i) {
        this.currentCallState = i;
        updateState(this.fragmentView != null && !this.isPaused);
    }

    private void updateState(boolean z) {
        int i = this.currentCallState;
        if (i == 1 || i == 2 || i == 6 || i == 5) {
            updateMuteButton(MUTE_BUTTON_STATE_CONNECTING, z);
        } else if (VoIPService.getSharedInstance() != null) {
            TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant = this.call.participants.get(getUserConfig().getClientUserId());
            if (tLRPC$TL_groupCallParticipant != null && !tLRPC$TL_groupCallParticipant.can_self_unmute && tLRPC$TL_groupCallParticipant.muted && !ChatObject.canManageCalls(this.currentChat)) {
                updateMuteButton(MUTE_BUTTON_STATE_MUTED_BY_ADMIN, z);
                VoIPService.getSharedInstance().setMicMute(true);
            } else if (VoIPService.getSharedInstance().isMicMute()) {
                updateMuteButton(MUTE_BUTTON_STATE_UNMUTE, z);
            } else {
                updateMuteButton(MUTE_BUTTON_STATE_MUTE, z);
            }
        }
    }

    public View createView(Context context) {
        Context context2 = context;
        this.actionBar.setBackButtonImage(NUM);
        if (AndroidUtilities.isTablet()) {
            this.actionBar.setOccupyStatusBar(false);
        }
        this.actionBar.setAllowOverlayTitle(false);
        this.actionBar.setBackgroundColor(Theme.getColor("voipgroup_actionBar"));
        this.actionBar.setItemsColor(Theme.getColor("voipgroup_actionBarItems"), false);
        this.actionBar.setItemsBackgroundColor(Theme.getColor("actionBarActionModeDefaultSelector"), false);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                String str;
                if (i == -1) {
                    GroupCallActivity.this.finishFragment();
                } else if (i == 1) {
                    GroupCallActivity groupCallActivity = GroupCallActivity.this;
                    groupCallActivity.call.call.join_muted = false;
                    groupCallActivity.toggleAdminSpeak();
                } else if (i == 2) {
                    GroupCallActivity groupCallActivity2 = GroupCallActivity.this;
                    groupCallActivity2.call.call.join_muted = true;
                    groupCallActivity2.toggleAdminSpeak();
                } else {
                    String str2 = null;
                    if (i == 3) {
                        TLRPC$ChatFull chatFull = GroupCallActivity.this.getMessagesController().getChatFull(GroupCallActivity.this.currentChat.id);
                        if (!TextUtils.isEmpty(GroupCallActivity.this.currentChat.username)) {
                            str = GroupCallActivity.this.getMessagesController().linkPrefix + "/" + GroupCallActivity.this.currentChat.username;
                        } else {
                            if (chatFull != null) {
                                TLRPC$ExportedChatInvite tLRPC$ExportedChatInvite = chatFull.exported_invite;
                                if (tLRPC$ExportedChatInvite instanceof TLRPC$TL_chatInviteExported) {
                                    str2 = tLRPC$ExportedChatInvite.link;
                                }
                            }
                            str = str2;
                        }
                        if (TextUtils.isEmpty(str)) {
                            TLRPC$TL_messages_exportChatInvite tLRPC$TL_messages_exportChatInvite = new TLRPC$TL_messages_exportChatInvite();
                            tLRPC$TL_messages_exportChatInvite.peer = MessagesController.getInputPeer(GroupCallActivity.this.currentChat);
                            GroupCallActivity.this.getConnectionsManager().bindRequestToGuid(GroupCallActivity.this.getConnectionsManager().sendRequest(tLRPC$TL_messages_exportChatInvite, new RequestDelegate(chatFull) {
                                public final /* synthetic */ TLRPC$ChatFull f$1;

                                {
                                    this.f$1 = r2;
                                }

                                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                                    GroupCallActivity.AnonymousClass1.this.lambda$onItemClick$1$GroupCallActivity$1(this.f$1, tLObject, tLRPC$TL_error);
                                }
                            }), GroupCallActivity.this.classGuid);
                            return;
                        }
                        openShareAlert(str);
                    } else if (i == 4) {
                        AlertDialog.Builder builder = new AlertDialog.Builder((Context) GroupCallActivity.this.getParentActivity());
                        builder.setTitle(LocaleController.getString("VoipGroupEndAlertTitle", NUM));
                        builder.setMessage(LocaleController.getString("VoipGroupEndAlertText", NUM));
                        builder.setPositiveButton(LocaleController.getString("VoipGroupEnd", NUM), new DialogInterface.OnClickListener() {
                            public final void onClick(DialogInterface dialogInterface, int i) {
                                GroupCallActivity.AnonymousClass1.this.lambda$onItemClick$2$GroupCallActivity$1(dialogInterface, i);
                            }
                        });
                        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                        AlertDialog create = builder.create();
                        create.setBackgroundColor(Theme.getColor("voipgroup_actionBar"));
                        GroupCallActivity.this.showDialog(create);
                        TextView textView = (TextView) create.getButton(-1);
                        if (textView != null) {
                            textView.setTextColor(Theme.getColor("dialogTextRed2"));
                        }
                        create.setTextColor(Theme.getColor("voipgroup_actionBarItems"));
                    }
                }
            }

            /* access modifiers changed from: private */
            /* renamed from: lambda$onItemClick$1 */
            public /* synthetic */ void lambda$onItemClick$1$GroupCallActivity$1(TLRPC$ChatFull tLRPC$ChatFull, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                AndroidUtilities.runOnUIThread(new Runnable(tLObject, tLRPC$ChatFull) {
                    public final /* synthetic */ TLObject f$1;
                    public final /* synthetic */ TLRPC$ChatFull f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run() {
                        GroupCallActivity.AnonymousClass1.this.lambda$null$0$GroupCallActivity$1(this.f$1, this.f$2);
                    }
                });
            }

            /* access modifiers changed from: private */
            /* renamed from: lambda$null$0 */
            public /* synthetic */ void lambda$null$0$GroupCallActivity$1(TLObject tLObject, TLRPC$ChatFull tLRPC$ChatFull) {
                if (tLObject instanceof TLRPC$TL_chatInviteExported) {
                    TLRPC$ExportedChatInvite tLRPC$ExportedChatInvite = (TLRPC$ExportedChatInvite) tLObject;
                    tLRPC$ChatFull.exported_invite = tLRPC$ExportedChatInvite;
                    openShareAlert(tLRPC$ExportedChatInvite.link);
                }
            }

            /* access modifiers changed from: private */
            /* renamed from: lambda$onItemClick$2 */
            public /* synthetic */ void lambda$onItemClick$2$GroupCallActivity$1(DialogInterface dialogInterface, int i) {
                if (VoIPService.getSharedInstance() != null) {
                    VoIPService.getSharedInstance().hangUp(1);
                }
                GroupCallActivity.this.finishFragment();
                NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didStartedCall, new Object[0]);
            }

            public boolean canOpenMenu() {
                GroupCallActivity groupCallActivity = GroupCallActivity.this;
                if (groupCallActivity.call.call.join_muted) {
                    groupCallActivity.everyoneItem.setColors(Theme.getColor("voipgroup_actionBarItems"), Theme.getColor("voipgroup_actionBarItems"));
                    GroupCallActivity.this.everyoneItem.setChecked(false);
                    GroupCallActivity.this.adminItem.setColors(Theme.getColor("voipgroup_checkMenu"), Theme.getColor("voipgroup_checkMenu"));
                    GroupCallActivity.this.adminItem.setChecked(true);
                } else {
                    groupCallActivity.everyoneItem.setColors(Theme.getColor("voipgroup_checkMenu"), Theme.getColor("voipgroup_checkMenu"));
                    GroupCallActivity.this.everyoneItem.setChecked(true);
                    GroupCallActivity.this.adminItem.setColors(Theme.getColor("voipgroup_actionBarItems"), Theme.getColor("voipgroup_actionBarItems"));
                    GroupCallActivity.this.adminItem.setChecked(false);
                }
                return true;
            }

            private void openShareAlert(String str) {
                if (GroupCallActivity.this.getParentActivity() != null) {
                    GroupCallActivity.this.showDialog(new ShareAlert(GroupCallActivity.this.getParentActivity(), (ArrayList<MessageObject>) null, LocaleController.formatString("VoipGroupInviteText", NUM, str), false, str, false));
                }
            }
        });
        ActionBarMenuItem addItem = this.actionBar.createMenu().addItem(10, NUM);
        addItem.setContentDescription(LocaleController.getString("AccDescrMoreOptions", NUM));
        this.everyoneItem = addItem.addSubItem(1, 0, (CharSequence) LocaleController.getString("VoipGroupAllCanSpeak", NUM), true);
        this.adminItem = addItem.addSubItem(2, 0, (CharSequence) LocaleController.getString("VoipGroupOnlyAdminsCanSpeak", NUM), true);
        this.everyoneItem.setCheckColor(Theme.getColor("voipgroup_checkMenu"));
        this.everyoneItem.setColors(Theme.getColor("voipgroup_checkMenu"), Theme.getColor("voipgroup_checkMenu"));
        this.adminItem.setCheckColor(Theme.getColor("voipgroup_checkMenu"));
        this.adminItem.setColors(Theme.getColor("voipgroup_checkMenu"), Theme.getColor("voipgroup_checkMenu"));
        this.dividerItem = addItem.addDivider(Theme.getColor("voipgroup_listViewBackground"));
        this.inviteItem = addItem.addSubItem(3, NUM, LocaleController.getString("VoipGroupShareInviteLink", NUM));
        ActionBarMenuSubItem addSubItem = addItem.addSubItem(4, NUM, LocaleController.getString("VoipGroupEndChat", NUM));
        addItem.setPopupItemsColor(Theme.getColor("voipgroup_actionBarItems"), false);
        addItem.setPopupItemsColor(Theme.getColor("voipgroup_actionBarItems"), true);
        addItem.setPopupItemsSelectorColor(Theme.getColor("voipgroup_actionBarItemsSelector"));
        addSubItem.setColors(Theme.getColor("voipgroup_leaveCallMenu"), Theme.getColor("voipgroup_leaveCallMenu"));
        addItem.redrawPopup(Theme.getColor("voipgroup_actionBar"));
        ChatAvatarContainer chatAvatarContainer = new ChatAvatarContainer(context2, (ChatActivity) null, false);
        this.avatarContainer = chatAvatarContainer;
        this.actionBar.addView(chatAvatarContainer, 0, LayoutHelper.createFrame(-2, -1.0f, 51, !this.inPreviewMode ? 56.0f : 0.0f, 0.0f, 40.0f, 0.0f));
        ListAdapter listAdapter2 = new ListAdapter(context2);
        this.listAdapter = listAdapter2;
        listAdapter2.notifyDataSetChanged();
        this.oldCount = this.listAdapter.getItemCount();
        this.avatarContainer.setChatAvatar(this.currentChat);
        this.avatarContainer.setTitle(this.currentChat.title);
        this.avatarContainer.setSubtitle(LocaleController.formatPluralString("Members", this.call.call.participants_count + (this.listAdapter.addSelfToCounter() ? 1 : 0)));
        this.avatarContainer.setTitleColors(Theme.getColor("voipgroup_actionBarItems"), Theme.getColor("voipgroup_actionBarItems"));
        FrameLayout frameLayout = new FrameLayout(context2);
        this.fragmentView = frameLayout;
        frameLayout.setBackgroundColor(Theme.getColor("voipgroup_actionBar"));
        this.fragmentView.setKeepScreenOn(true);
        this.fragmentView.setOnTouchListener($$Lambda$GroupCallActivity$kpKj2OHUClkoACsgp5ZtSs7T2Bg.INSTANCE);
        FrameLayout frameLayout2 = (FrameLayout) this.fragmentView;
        frameLayout2.setClipChildren(false);
        AnonymousClass2 r4 = new RecyclerListView(context2) {
            private RectF rect = new RectF();

            /* access modifiers changed from: protected */
            public void dispatchDraw(Canvas canvas) {
                int childCount = getChildCount();
                float f = 0.0f;
                for (int i = 0; i < childCount; i++) {
                    View childAt = getChildAt(i);
                    f = Math.max(f, childAt.getY() + ((float) childAt.getMeasuredHeight()));
                }
                this.rect.set(0.0f, 0.0f, (float) getMeasuredWidth(), Math.min((float) getMeasuredHeight(), f));
                canvas.drawRoundRect(this.rect, (float) AndroidUtilities.dp(13.0f), (float) AndroidUtilities.dp(13.0f), GroupCallActivity.this.listViewBackgroundPaint);
                canvas.save();
                canvas.clipRect(0, 0, getMeasuredWidth(), getMeasuredHeight());
                super.dispatchDraw(canvas);
                canvas.restore();
            }
        };
        this.listView = r4;
        r4.setItemAnimator(new DefaultItemAnimator() {
            /* access modifiers changed from: protected */
            public void onMoveAnimationUpdate(RecyclerView.ViewHolder viewHolder) {
                GroupCallActivity.this.listView.invalidate();
            }
        });
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                GroupCallActivity groupCallActivity = GroupCallActivity.this;
                ChatObject.Call call = groupCallActivity.call;
                if (!call.loadingMembers && !call.membersLoadEndReached && groupCallActivity.layoutManager.findLastVisibleItemPosition() > GroupCallActivity.this.listAdapter.getItemCount() - 5) {
                    GroupCallActivity.this.call.loadMembers(false);
                }
            }
        });
        this.listViewBackgroundPaint.setColor(Theme.getColor("voipgroup_listViewBackground"));
        if (this.bigMicDrawable == null) {
            RLottieDrawable rLottieDrawable = new RLottieDrawable(NUM, "NUM", AndroidUtilities.dp(28.0f), AndroidUtilities.dp(38.0f), true, (int[]) null);
            this.bigMicDrawable = rLottieDrawable;
            rLottieDrawable.setPlayInDirectionOfCustomEndFrame(true);
        }
        this.listView.setVerticalScrollBarEnabled(false);
        RecyclerListView recyclerListView = this.listView;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context2, 1, false);
        this.layoutManager = linearLayoutManager;
        recyclerListView.setLayoutManager(linearLayoutManager);
        frameLayout2.addView(this.listView, LayoutHelper.createFrame(-1, -2.0f, 51, 14.0f, 14.0f, 14.0f, 231.0f));
        this.listView.setAdapter(this.listAdapter);
        this.listView.setGlowColor(Theme.getColor("voipgroup_listViewBackground"));
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListenerExtended) new RecyclerListView.OnItemClickListenerExtended() {
            public final void onItemClick(View view, int i, float f, float f2) {
                GroupCallActivity.this.lambda$createView$3$GroupCallActivity(view, i, f, f2);
            }
        });
        AnonymousClass6 r42 = new FrameLayout(context2) {
            /* access modifiers changed from: protected */
            public void onLayout(boolean z, int i, int i2, int i3, int i4) {
                int measuredWidth = (getMeasuredWidth() - AndroidUtilities.dp(122.0f)) / 2;
                int measuredHeight = getMeasuredHeight();
                int measuredWidth2 = (measuredWidth - GroupCallActivity.this.soundButton.getMeasuredWidth()) / 2;
                int measuredHeight2 = ((measuredHeight - GroupCallActivity.this.soundButton.getMeasuredHeight()) / 2) - AndroidUtilities.dp(9.0f);
                GroupCallActivity.this.soundButton.layout(measuredWidth2, measuredHeight2, GroupCallActivity.this.soundButton.getMeasuredWidth() + measuredWidth2, GroupCallActivity.this.soundButton.getMeasuredHeight() + measuredHeight2);
                int measuredWidth3 = (getMeasuredWidth() - measuredWidth) + ((measuredWidth - GroupCallActivity.this.leaveButton.getMeasuredWidth()) / 2);
                GroupCallActivity.this.leaveButton.layout(measuredWidth3, measuredHeight2, GroupCallActivity.this.leaveButton.getMeasuredWidth() + measuredWidth3, GroupCallActivity.this.leaveButton.getMeasuredHeight() + measuredHeight2);
                int measuredWidth4 = (getMeasuredWidth() - GroupCallActivity.this.muteButton.getMeasuredWidth()) / 2;
                int measuredHeight3 = ((measuredHeight - GroupCallActivity.this.muteButton.getMeasuredHeight()) / 2) - AndroidUtilities.dp(18.0f);
                GroupCallActivity.this.muteButton.layout(measuredWidth4, measuredHeight3, GroupCallActivity.this.muteButton.getMeasuredWidth() + measuredWidth4, GroupCallActivity.this.muteButton.getMeasuredHeight() + measuredHeight3);
                int measuredWidth5 = (getMeasuredWidth() - GroupCallActivity.this.radialProgressView.getMeasuredWidth()) / 2;
                int measuredHeight4 = ((measuredHeight - GroupCallActivity.this.radialProgressView.getMeasuredHeight()) / 2) - AndroidUtilities.dp(18.0f);
                GroupCallActivity.this.radialProgressView.layout(measuredWidth5, measuredHeight4, GroupCallActivity.this.radialProgressView.getMeasuredWidth() + measuredWidth5, GroupCallActivity.this.radialProgressView.getMeasuredHeight() + measuredHeight4);
                for (int i5 = 0; i5 < 2; i5++) {
                    int measuredWidth6 = (getMeasuredWidth() - GroupCallActivity.this.muteLabel[i5].getMeasuredWidth()) / 2;
                    int dp = (measuredHeight - AndroidUtilities.dp(35.0f)) - GroupCallActivity.this.muteLabel[i5].getMeasuredHeight();
                    GroupCallActivity.this.muteLabel[i5].layout(measuredWidth6, dp, GroupCallActivity.this.muteLabel[i5].getMeasuredWidth() + measuredWidth6, GroupCallActivity.this.muteLabel[i5].getMeasuredHeight() + dp);
                    int measuredWidth7 = (getMeasuredWidth() - GroupCallActivity.this.muteSubLabel[i5].getMeasuredWidth()) / 2;
                    int dp2 = (measuredHeight - AndroidUtilities.dp(17.0f)) - GroupCallActivity.this.muteSubLabel[i5].getMeasuredHeight();
                    GroupCallActivity.this.muteSubLabel[i5].layout(measuredWidth7, dp2, GroupCallActivity.this.muteSubLabel[i5].getMeasuredWidth() + measuredWidth7, GroupCallActivity.this.muteSubLabel[i5].getMeasuredHeight() + dp2);
                }
            }

            /* access modifiers changed from: protected */
            public void onDraw(Canvas canvas) {
                float f;
                int measuredWidth = (getMeasuredWidth() - getMeasuredHeight()) / 2;
                long elapsedRealtime = SystemClock.elapsedRealtime();
                long access$2100 = elapsedRealtime - GroupCallActivity.this.lastUpdateTime;
                long unused = GroupCallActivity.this.lastUpdateTime = elapsedRealtime;
                if (access$2100 > 20) {
                    access$2100 = 17;
                }
                long j = access$2100;
                if (GroupCallActivity.this.currentState != null) {
                    GroupCallActivity.this.currentState.update(0, measuredWidth, getMeasuredHeight(), j);
                }
                GroupCallActivity.this.tinyWaveDrawable.minRadius = (float) AndroidUtilities.dp(62.0f);
                GroupCallActivity.this.tinyWaveDrawable.maxRadius = ((float) AndroidUtilities.dp(62.0f)) + (((float) AndroidUtilities.dp(20.0f)) * BlobDrawable.FORM_SMALL_MAX);
                GroupCallActivity.this.bigWaveDrawable.minRadius = (float) AndroidUtilities.dp(65.0f);
                GroupCallActivity.this.bigWaveDrawable.maxRadius = ((float) AndroidUtilities.dp(65.0f)) + (((float) AndroidUtilities.dp(20.0f)) * BlobDrawable.FORM_BIG_MAX);
                GroupCallActivity.this.buttonWaveDrawable.minRadius = (float) AndroidUtilities.dp(57.0f);
                GroupCallActivity.this.buttonWaveDrawable.maxRadius = ((float) AndroidUtilities.dp(57.0f)) + (((float) AndroidUtilities.dp(12.0f)) * BlobDrawable.FORM_BUTTON_MAX);
                if (GroupCallActivity.this.animateToAmplitude != GroupCallActivity.this.amplitude) {
                    GroupCallActivity groupCallActivity = GroupCallActivity.this;
                    float unused2 = groupCallActivity.amplitude = groupCallActivity.amplitude + (GroupCallActivity.this.animateAmplitudeDiff * ((float) j));
                    if (GroupCallActivity.this.animateAmplitudeDiff > 0.0f) {
                        if (GroupCallActivity.this.amplitude > GroupCallActivity.this.animateToAmplitude) {
                            GroupCallActivity groupCallActivity2 = GroupCallActivity.this;
                            float unused3 = groupCallActivity2.amplitude = groupCallActivity2.animateToAmplitude;
                        }
                    } else if (GroupCallActivity.this.amplitude < GroupCallActivity.this.animateToAmplitude) {
                        GroupCallActivity groupCallActivity3 = GroupCallActivity.this;
                        float unused4 = groupCallActivity3.amplitude = groupCallActivity3.animateToAmplitude;
                    }
                    invalidate();
                }
                boolean z = GroupCallActivity.this.currentState != null && (GroupCallActivity.this.currentState.currentState == GroupCallActivity.MUTE_BUTTON_STATE_MUTE || GroupCallActivity.this.currentState.currentState == GroupCallActivity.MUTE_BUTTON_STATE_UNMUTE);
                if (z && GroupCallActivity.this.showWavesProgress != 1.0f) {
                    GroupCallActivity groupCallActivity4 = GroupCallActivity.this;
                    float unused5 = groupCallActivity4.showWavesProgress = groupCallActivity4.showWavesProgress + (((float) j) / 250.0f);
                    if (GroupCallActivity.this.showWavesProgress > 1.0f) {
                        float unused6 = GroupCallActivity.this.showWavesProgress = 1.0f;
                    }
                    invalidate();
                } else if (!z && GroupCallActivity.this.showWavesProgress != 0.0f) {
                    GroupCallActivity groupCallActivity5 = GroupCallActivity.this;
                    float unused7 = groupCallActivity5.showWavesProgress = groupCallActivity5.showWavesProgress - (((float) j) / 250.0f);
                    if (GroupCallActivity.this.showWavesProgress < 0.0f) {
                        float unused8 = GroupCallActivity.this.showWavesProgress = 0.0f;
                    }
                }
                float interpolation = CubicBezierInterpolator.EASE_OUT.getInterpolation(GroupCallActivity.this.showWavesProgress);
                GroupCallActivity.this.bigWaveDrawable.update(GroupCallActivity.this.amplitude, 1.0f);
                GroupCallActivity.this.tinyWaveDrawable.update(GroupCallActivity.this.amplitude, 1.0f);
                GroupCallActivity.this.buttonWaveDrawable.update(GroupCallActivity.this.amplitude, 0.4f);
                for (int i = 0; i < 2; i++) {
                    if (i == 0 && GroupCallActivity.this.prevState != null) {
                        GroupCallActivity.this.paint.setShader(GroupCallActivity.this.prevState.shader);
                        f = 1.0f - GroupCallActivity.this.switchProgress;
                    } else if (i == 1) {
                        GroupCallActivity.this.paint.setShader(GroupCallActivity.this.currentState.shader);
                        f = GroupCallActivity.this.switchProgress;
                    }
                    float left = (float) (GroupCallActivity.this.muteButton.getLeft() + (GroupCallActivity.this.muteButton.getMeasuredWidth() / 2));
                    float top = (float) (GroupCallActivity.this.muteButton.getTop() + (GroupCallActivity.this.muteButton.getMeasuredHeight() / 2));
                    GroupCallActivity.this.radialMatrix.setTranslate(left, top);
                    GroupCallActivity.this.radialGradient.setLocalMatrix(GroupCallActivity.this.radialMatrix);
                    GroupCallActivity.this.paint.setAlpha((int) (76.0f * f));
                    float dp = ((float) AndroidUtilities.dp(52.0f)) / 2.0f;
                    canvas.drawCircle(GroupCallActivity.this.soundButton.getX() + ((float) (GroupCallActivity.this.soundButton.getMeasuredWidth() / 2)), GroupCallActivity.this.soundButton.getY() + dp, dp, GroupCallActivity.this.paint);
                    if (i == 1) {
                        canvas.drawCircle(GroupCallActivity.this.leaveButton.getX() + ((float) (GroupCallActivity.this.leaveButton.getMeasuredWidth() / 2)), GroupCallActivity.this.leaveButton.getY() + dp, dp, GroupCallActivity.this.leaveBackgroundPaint);
                    }
                    canvas.save();
                    float f2 = BlobDrawable.GLOBAL_SCALE;
                    canvas.scale(f2, f2, left, top);
                    canvas.save();
                    float access$100 = (BlobDrawable.SCALE_BIG_MIN + (BlobDrawable.SCALE_BIG * GroupCallActivity.this.amplitude)) * interpolation;
                    canvas.scale(access$100, access$100, left, top);
                    if (i == 1) {
                        float f3 = BlobDrawable.LIGHT_GRADIENT_SIZE + 0.7f;
                        canvas.save();
                        canvas.scale(f3, f3, left, top);
                        canvas.drawCircle(left, top, (float) AndroidUtilities.dp(95.0f), GroupCallActivity.this.radialPaint);
                        canvas.restore();
                    }
                    GroupCallActivity.this.bigWaveDrawable.draw(left, top, canvas, GroupCallActivity.this.paint);
                    canvas.restore();
                    canvas.save();
                    float access$1002 = (BlobDrawable.SCALE_SMALL_MIN + (BlobDrawable.SCALE_SMALL * GroupCallActivity.this.amplitude)) * interpolation;
                    canvas.scale(access$1002, access$1002, left, top);
                    GroupCallActivity.this.tinyWaveDrawable.draw(left, top, canvas, GroupCallActivity.this.paint);
                    canvas.restore();
                    if (i == 0) {
                        GroupCallActivity.this.paint.setAlpha(255);
                    } else {
                        GroupCallActivity.this.paint.setAlpha((int) (f * 255.0f));
                    }
                    GroupCallActivity.this.buttonWaveDrawable.draw(left, top, canvas, GroupCallActivity.this.paint);
                    canvas.restore();
                }
                invalidate();
            }
        };
        this.buttonsContainer = r42;
        r42.setWillNotDraw(false);
        frameLayout2.addView(this.buttonsContainer, LayoutHelper.createFrame(-1, 231, 83));
        int color = Theme.getColor("voipgroup_unmuteButton2");
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        this.radialMatrix = new Matrix();
        this.radialGradient = new RadialGradient(0.0f, 0.0f, (float) AndroidUtilities.dp(95.0f), new int[]{Color.argb(150, red, green, blue), Color.argb(0, red, green, blue)}, (float[]) null, Shader.TileMode.CLAMP);
        Paint paint2 = new Paint(1);
        this.radialPaint = paint2;
        paint2.setShader(this.radialGradient);
        this.buttonWaveDrawable = new BlobDrawable(6);
        this.tinyWaveDrawable = new BlobDrawable(9);
        this.bigWaveDrawable = new BlobDrawable(12);
        this.buttonWaveDrawable.minRadius = (float) AndroidUtilities.dp(57.0f);
        this.buttonWaveDrawable.maxRadius = (float) AndroidUtilities.dp(63.0f);
        this.buttonWaveDrawable.generateBlob();
        this.tinyWaveDrawable.minRadius = (float) AndroidUtilities.dp(62.0f);
        this.tinyWaveDrawable.maxRadius = (float) AndroidUtilities.dp(72.0f);
        this.tinyWaveDrawable.generateBlob();
        this.bigWaveDrawable.minRadius = (float) AndroidUtilities.dp(65.0f);
        this.bigWaveDrawable.maxRadius = (float) AndroidUtilities.dp(75.0f);
        this.bigWaveDrawable.generateBlob();
        this.tinyWaveDrawable.paint.setColor(ColorUtils.setAlphaComponent(Theme.getColor("voipgroup_unmuteButton"), 38));
        this.bigWaveDrawable.paint.setColor(ColorUtils.setAlphaComponent(Theme.getColor("voipgroup_unmuteButton"), 76));
        this.buttonWaveDrawable.paint.setColor(Theme.getColor("voipgroup_unmuteButton"));
        VoIPToggleButton voIPToggleButton = new VoIPToggleButton(context2);
        this.soundButton = voIPToggleButton;
        voIPToggleButton.setTextSize(12);
        this.buttonsContainer.addView(this.soundButton, LayoutHelper.createFrame(68, 80.0f));
        this.soundButton.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                GroupCallActivity.this.lambda$createView$4$GroupCallActivity(view);
            }
        });
        VoIPToggleButton voIPToggleButton2 = new VoIPToggleButton(context2);
        this.leaveButton = voIPToggleButton2;
        voIPToggleButton2.setDrawBackground(false);
        this.leaveButton.setTextSize(12);
        this.leaveButton.setData(NUM, -1, Theme.getColor("voipgroup_leaveButton"), 0.5f, false, LocaleController.getString("VoipGroupLeave", NUM), false, false);
        this.buttonsContainer.addView(this.leaveButton, LayoutHelper.createFrame(68, 80.0f));
        this.leaveButton.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                GroupCallActivity.this.lambda$createView$5$GroupCallActivity(view);
            }
        });
        AnonymousClass7 r43 = new RLottieImageView(context2) {
            private Runnable pressRunnable = new Runnable() {
                public final void run() {
                    GroupCallActivity.AnonymousClass7.this.lambda$$0$GroupCallActivity$7();
                }
            };
            private boolean pressed;
            private boolean scheduled;

            /* access modifiers changed from: private */
            /* renamed from: lambda$$0 */
            public /* synthetic */ void lambda$$0$GroupCallActivity$7() {
                if (this.scheduled && VoIPService.getSharedInstance() != null) {
                    GroupCallActivity.this.updateMuteButton(GroupCallActivity.MUTE_BUTTON_STATE_MUTE, true);
                    VoIPService.getSharedInstance().setMicMute(false);
                    GroupCallActivity.this.muteButton.performHapticFeedback(3, 2);
                    AccountInstance accountInstance = GroupCallActivity.this.getAccountInstance();
                    GroupCallActivity groupCallActivity = GroupCallActivity.this;
                    GroupCallActivity.editCallMember(accountInstance, groupCallActivity.call, groupCallActivity.getUserConfig().getCurrentUser(), false);
                    this.scheduled = false;
                    this.pressed = true;
                }
            }

            public boolean onTouchEvent(MotionEvent motionEvent) {
                if (motionEvent.getAction() == 0 && GroupCallActivity.this.muteButtonState == GroupCallActivity.MUTE_BUTTON_STATE_UNMUTE) {
                    AndroidUtilities.runOnUIThread(this.pressRunnable, (long) ViewConfiguration.getTapTimeout());
                    this.scheduled = true;
                } else if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
                    if (this.scheduled) {
                        AndroidUtilities.cancelRunOnUIThread(this.pressRunnable);
                        this.scheduled = false;
                    } else if (this.pressed) {
                        GroupCallActivity.this.updateMuteButton(GroupCallActivity.MUTE_BUTTON_STATE_UNMUTE, true);
                        if (VoIPService.getSharedInstance() != null) {
                            VoIPService.getSharedInstance().setMicMute(true);
                            GroupCallActivity.this.muteButton.performHapticFeedback(3, 2);
                            AccountInstance accountInstance = GroupCallActivity.this.getAccountInstance();
                            GroupCallActivity groupCallActivity = GroupCallActivity.this;
                            GroupCallActivity.editCallMember(accountInstance, groupCallActivity.call, groupCallActivity.getUserConfig().getCurrentUser(), true);
                        }
                        this.pressed = false;
                        MotionEvent obtain = MotionEvent.obtain(0, 0, 3, 0.0f, 0.0f, 0);
                        super.onTouchEvent(obtain);
                        obtain.recycle();
                        return true;
                    }
                }
                return super.onTouchEvent(motionEvent);
            }
        };
        this.muteButton = r43;
        r43.setAnimation(this.bigMicDrawable);
        this.muteButton.setScaleType(ImageView.ScaleType.CENTER);
        this.buttonsContainer.addView(this.muteButton, LayoutHelper.createFrame(122, 122, 49));
        this.muteButton.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                GroupCallActivity.this.lambda$createView$6$GroupCallActivity(view);
            }
        });
        RadialProgressView radialProgressView2 = new RadialProgressView(context2);
        this.radialProgressView = radialProgressView2;
        radialProgressView2.setSize(AndroidUtilities.dp(116.0f));
        this.radialProgressView.setStrokeWidth(4.0f);
        this.radialProgressView.setProgressColor(Theme.getColor("voipgroup_connectingProgress"));
        this.buttonsContainer.addView(this.radialProgressView, LayoutHelper.createFrame(126, 126, 49));
        for (int i = 0; i < 2; i++) {
            this.muteLabel[i] = new TextView(context2);
            this.muteLabel[i].setTextColor(Theme.getColor("voipgroup_actionBarItems"));
            this.muteLabel[i].setTextSize(1, 18.0f);
            this.muteLabel[i].setGravity(1);
            this.buttonsContainer.addView(this.muteLabel[i], LayoutHelper.createFrame(-2, -2.0f, 81, 0.0f, 0.0f, 0.0f, 26.0f));
            this.muteSubLabel[i] = new TextView(context2);
            this.muteSubLabel[i].setTextColor(Theme.getColor("voipgroup_actionBarItems"));
            this.muteSubLabel[i].setTextSize(1, 12.0f);
            this.muteSubLabel[i].setGravity(1);
            this.buttonsContainer.addView(this.muteSubLabel[i], LayoutHelper.createFrame(-2, -2.0f, 81, 0.0f, 0.0f, 0.0f, 10.0f));
            if (i == 1) {
                this.muteLabel[i].setVisibility(4);
                this.muteSubLabel[i].setVisibility(4);
            }
        }
        updateItems();
        updateSpeakerPhoneIcon(false);
        updateState(false);
        this.leaveBackgroundPaint.setColor(Theme.getColor("voipgroup_leaveButton"));
        return this.fragmentView;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$3 */
    public /* synthetic */ void lambda$createView$3$GroupCallActivity(View view, int i, float f, float f2) {
        if (view instanceof GroupCallUserCell) {
            if (ChatObject.canManageCalls(this.currentChat)) {
                GroupCallUserCell groupCallUserCell = (GroupCallUserCell) view;
                if (!groupCallUserCell.isSelfUser()) {
                    groupCallUserCell.clickMuteButton();
                }
            }
        } else if (i == this.listAdapter.addMemberRow) {
            Bundle bundle = new Bundle();
            bundle.putInt("chat_id", this.currentChat.id);
            bundle.putInt("type", 2);
            bundle.putInt("selectType", 4);
            ChatUsersActivity chatUsersActivity = new ChatUsersActivity(bundle);
            chatUsersActivity.setIgnoresUsers(this.call.participants);
            chatUsersActivity.setInfo(getMessagesController().getChatFull(this.currentChat.id));
            chatUsersActivity.setDelegate(new ChatUsersActivity.ChatUsersActivityDelegate() {
                public /* synthetic */ void didAddParticipantToList(int i, TLObject tLObject) {
                    ChatUsersActivity.ChatUsersActivityDelegate.CC.$default$didAddParticipantToList(this, i, tLObject);
                }

                public /* synthetic */ void didChangeOwner(TLRPC$User tLRPC$User) {
                    ChatUsersActivity.ChatUsersActivityDelegate.CC.$default$didChangeOwner(this, tLRPC$User);
                }

                public void didSelectUser(int i) {
                    TLRPC$User user = GroupCallActivity.this.getMessagesController().getUser(Integer.valueOf(i));
                    if (user != null) {
                        TLRPC$TL_phone_inviteToGroupCall tLRPC$TL_phone_inviteToGroupCall = new TLRPC$TL_phone_inviteToGroupCall();
                        tLRPC$TL_phone_inviteToGroupCall.call = GroupCallActivity.this.call.getInputGroupCall();
                        TLRPC$TL_inputUser tLRPC$TL_inputUser = new TLRPC$TL_inputUser();
                        tLRPC$TL_inputUser.user_id = user.id;
                        tLRPC$TL_inputUser.access_hash = user.access_hash;
                        tLRPC$TL_phone_inviteToGroupCall.users.add(tLRPC$TL_inputUser);
                        GroupCallActivity.this.getConnectionsManager().sendRequest(tLRPC$TL_phone_inviteToGroupCall, new RequestDelegate(tLRPC$TL_phone_inviteToGroupCall) {
                            public final /* synthetic */ TLRPC$TL_phone_inviteToGroupCall f$1;

                            {
                                this.f$1 = r2;
                            }

                            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                                GroupCallActivity.AnonymousClass5.this.lambda$didSelectUser$1$GroupCallActivity$5(this.f$1, tLObject, tLRPC$TL_error);
                            }
                        });
                    }
                }

                /* access modifiers changed from: private */
                /* renamed from: lambda$didSelectUser$1 */
                public /* synthetic */ void lambda$didSelectUser$1$GroupCallActivity$5(TLRPC$TL_phone_inviteToGroupCall tLRPC$TL_phone_inviteToGroupCall, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    if (tLObject != null) {
                        GroupCallActivity.this.getMessagesController().processUpdates((TLRPC$Updates) tLObject, false);
                    } else {
                        AndroidUtilities.runOnUIThread(new Runnable(tLRPC$TL_error, tLRPC$TL_phone_inviteToGroupCall) {
                            public final /* synthetic */ TLRPC$TL_error f$1;
                            public final /* synthetic */ TLRPC$TL_phone_inviteToGroupCall f$2;

                            {
                                this.f$1 = r2;
                                this.f$2 = r3;
                            }

                            public final void run() {
                                GroupCallActivity.AnonymousClass5.this.lambda$null$0$GroupCallActivity$5(this.f$1, this.f$2);
                            }
                        });
                    }
                }

                /* access modifiers changed from: private */
                /* renamed from: lambda$null$0 */
                public /* synthetic */ void lambda$null$0$GroupCallActivity$5(TLRPC$TL_error tLRPC$TL_error, TLRPC$TL_phone_inviteToGroupCall tLRPC$TL_phone_inviteToGroupCall) {
                    AlertsCreator.processError(GroupCallActivity.this.currentAccount, tLRPC$TL_error, GroupCallActivity.this, tLRPC$TL_phone_inviteToGroupCall, Boolean.TRUE);
                }
            });
            chatUsersActivity.setCurrentAccount(this.currentAccount);
            presentFragment(chatUsersActivity);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$4 */
    public /* synthetic */ void lambda$createView$4$GroupCallActivity(View view) {
        if (VoIPService.getSharedInstance() != null) {
            VoIPService.getSharedInstance().toggleSpeakerphoneOrShowRouteSheet(getParentActivity());
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$5 */
    public /* synthetic */ void lambda$createView$5$GroupCallActivity(View view) {
        onLeaveClick();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$6 */
    public /* synthetic */ void lambda$createView$6$GroupCallActivity(View view) {
        int i;
        if (VoIPService.getSharedInstance() != null && (i = this.muteButtonState) != MUTE_BUTTON_STATE_MUTED_BY_ADMIN && i != MUTE_BUTTON_STATE_CONNECTING) {
            int i2 = MUTE_BUTTON_STATE_UNMUTE;
            if (i == i2) {
                updateMuteButton(MUTE_BUTTON_STATE_MUTE, true);
                VoIPService.getSharedInstance().setMicMute(false);
                this.muteButton.performHapticFeedback(3, 2);
                editCallMember(getAccountInstance(), this.call, getUserConfig().getCurrentUser(), false);
                return;
            }
            updateMuteButton(i2, true);
            VoIPService.getSharedInstance().setMicMute(true);
            this.muteButton.performHapticFeedback(3, 2);
            editCallMember(getAccountInstance(), this.call, getUserConfig().getCurrentUser(), true);
        }
    }

    public void onAudioSettingsChanged() {
        updateSpeakerPhoneIcon(true);
    }

    /* access modifiers changed from: protected */
    public void onTransitionAnimationEnd(boolean z, boolean z2) {
        ActionBarLayout actionBarLayout;
        if (z && !z2 && (actionBarLayout = this.parentLayout) != null) {
            int i = 0;
            int size = actionBarLayout.fragmentsStack.size() - 1;
            while (i < size) {
                BaseFragment baseFragment = this.parentLayout.fragmentsStack.get(i);
                if (baseFragment == this || !(baseFragment instanceof GroupCallActivity)) {
                    i++;
                } else {
                    baseFragment.removeSelfFromStack();
                    return;
                }
            }
        }
    }

    private void updateSpeakerPhoneIcon(boolean z) {
        VoIPService sharedInstance = VoIPService.getSharedInstance();
        if (sharedInstance != null) {
            if (sharedInstance.isBluetoothOn()) {
                this.soundButton.setData(NUM, -1, 0, 0.5f, false, LocaleController.getString("VoipAudioRoutingBluetooth", NUM), false, z);
                this.soundButton.setChecked(false);
            } else if (sharedInstance.isSpeakerphoneOn()) {
                this.soundButton.setData(NUM, -1, ColorUtils.setAlphaComponent(-1, 60), 0.5f, false, LocaleController.getString("VoipSpeaker", NUM), false, z);
                this.soundButton.setChecked(true);
            } else if (sharedInstance.isHeadsetPlugged()) {
                this.soundButton.setData(NUM, -1, 0, 0.5f, false, LocaleController.getString("VoipAudioRoutingHeadset", NUM), false, z);
                this.soundButton.setChecked(false);
            } else {
                this.soundButton.setData(NUM, -1, 0, 0.5f, false, LocaleController.getString("VoipSpeaker", NUM), false, z);
                this.soundButton.setChecked(false);
            }
        }
    }

    /* access modifiers changed from: private */
    public void updateMuteButton(int i, boolean z) {
        int i2;
        String str;
        int i3;
        int i4;
        String str2;
        int i5;
        int color;
        int color2;
        int color3;
        String str3;
        int i6 = i;
        if ((this.muteButtonState != i6 || !z) && this.fragmentView != null) {
            ValueAnimator valueAnimator = this.muteButtonAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
                this.muteButtonAnimator = null;
            }
            String str4 = "";
            if (i6 == MUTE_BUTTON_STATE_UNMUTE) {
                int color4 = Theme.getColor("voipgroup_unmuteButton");
                int color5 = Theme.getColor("voipgroup_unmuteButton2");
                i2 = Theme.getColor("voipgroup_soundButton");
                String string = LocaleController.getString("VoipUnmute", NUM);
                String string2 = LocaleController.getString("VoipHoldAndTalk", NUM);
                this.bigMicDrawable.setCustomEndFrame(12);
                this.radialProgressView.setVisibility(4);
                this.buttonWaveDrawable.minRadius = (float) AndroidUtilities.dp(57.0f);
                this.buttonWaveDrawable.maxRadius = (float) AndroidUtilities.dp(63.0f);
                i3 = color4;
                str2 = string2;
                str = string;
                i5 = i3;
                i4 = color5;
            } else if (i6 == MUTE_BUTTON_STATE_MUTE) {
                int color6 = Theme.getColor("voipgroup_muteButton");
                int color7 = Theme.getColor("voipgroup_muteButton2");
                int color8 = Theme.getColor("voipgroup_soundButton2");
                String string3 = LocaleController.getString("VoipTapToMute", NUM);
                this.bigMicDrawable.setCustomEndFrame(0);
                this.radialProgressView.setVisibility(4);
                this.buttonWaveDrawable.minRadius = (float) AndroidUtilities.dp(57.0f);
                this.buttonWaveDrawable.maxRadius = (float) AndroidUtilities.dp(63.0f);
                i3 = color6;
                i2 = color8;
                str = string3;
                str2 = str4;
                i4 = color7;
                i5 = i3;
            } else {
                if (i6 == MUTE_BUTTON_STATE_CONNECTING) {
                    this.radialProgressView.setVisibility(0);
                    str3 = LocaleController.getString("Connecting", NUM);
                } else {
                    this.radialProgressView.setVisibility(4);
                    str3 = LocaleController.getString("VoipMutedByAdmin", NUM);
                    str4 = LocaleController.getString("VoipMutedByAdminInfo", NUM);
                }
                String str5 = str3;
                BlobDrawable blobDrawable = this.buttonWaveDrawable;
                float dp = (float) AndroidUtilities.dp(63.0f);
                blobDrawable.maxRadius = dp;
                blobDrawable.minRadius = dp;
                int color9 = Theme.getColor("voipgroup_disabledButton");
                int color10 = Theme.getColor("voipgroup_disabledButton");
                int color11 = Theme.getColor("voipgroup_disabledButton");
                this.bigMicDrawable.setCustomEndFrame(12);
                str2 = str4;
                str = str5;
                i2 = color11;
                i3 = 0;
                i4 = color10;
                i5 = color9;
            }
            if (z) {
                int i7 = this.muteButtonState;
                if (i7 == MUTE_BUTTON_STATE_UNMUTE) {
                    color = Theme.getColor("voipgroup_unmuteButton");
                    color2 = Theme.getColor("voipgroup_unmuteButton2");
                    color3 = Theme.getColor("voipgroup_soundButton");
                } else if (i7 == MUTE_BUTTON_STATE_MUTE) {
                    color = Theme.getColor("voipgroup_muteButton");
                    color2 = Theme.getColor("voipgroup_muteButton2");
                    color3 = Theme.getColor("voipgroup_soundButton2");
                } else {
                    color = Theme.getColor("voipgroup_disabledButton");
                    color2 = Theme.getColor("voipgroup_disabledButton");
                    color3 = Theme.getColor("voipgroup_disabledButton");
                }
                int i8 = color2;
                int i9 = color3;
                int i10 = color;
                this.muteButton.playAnimation();
                this.muteLabel[1].setVisibility(0);
                this.muteLabel[1].setAlpha(0.0f);
                this.muteLabel[1].setTranslationY((float) (-AndroidUtilities.dp(5.0f)));
                this.muteLabel[1].setText(str);
                this.muteSubLabel[1].setVisibility(0);
                this.muteSubLabel[1].setAlpha(0.0f);
                this.muteSubLabel[1].setTranslationY((float) (-AndroidUtilities.dp(5.0f)));
                this.muteSubLabel[1].setText(str2);
                ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
                this.muteButtonAnimator = ofFloat;
                ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(i10, i5, i8, i4, i3, i9, i2) {
                    public final /* synthetic */ int f$1;
                    public final /* synthetic */ int f$2;
                    public final /* synthetic */ int f$3;
                    public final /* synthetic */ int f$4;
                    public final /* synthetic */ int f$5;
                    public final /* synthetic */ int f$6;
                    public final /* synthetic */ int f$7;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                        this.f$3 = r4;
                        this.f$4 = r5;
                        this.f$5 = r6;
                        this.f$6 = r7;
                        this.f$7 = r8;
                    }

                    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                        GroupCallActivity.this.lambda$updateMuteButton$7$GroupCallActivity(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, valueAnimator);
                    }
                });
                this.muteButtonAnimator.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        ValueAnimator unused = GroupCallActivity.this.muteButtonAnimator = null;
                        TextView textView = GroupCallActivity.this.muteLabel[0];
                        GroupCallActivity.this.muteLabel[0] = GroupCallActivity.this.muteLabel[1];
                        GroupCallActivity.this.muteLabel[1] = textView;
                        textView.setVisibility(4);
                        TextView textView2 = GroupCallActivity.this.muteSubLabel[0];
                        GroupCallActivity.this.muteSubLabel[0] = GroupCallActivity.this.muteSubLabel[1];
                        GroupCallActivity.this.muteSubLabel[1] = textView2;
                        textView2.setVisibility(4);
                        for (int i = 0; i < 2; i++) {
                            GroupCallActivity.this.muteLabel[i].setTranslationY(0.0f);
                            GroupCallActivity.this.muteSubLabel[i].setTranslationY(0.0f);
                        }
                    }
                });
                this.muteButtonAnimator.setDuration(180);
                this.muteButtonAnimator.start();
                this.muteButtonState = i6;
                return;
            }
            this.muteButtonState = i6;
            RLottieDrawable rLottieDrawable = this.bigMicDrawable;
            rLottieDrawable.setCurrentFrame(rLottieDrawable.getCustomEndFrame(), false, true);
            setColors(i5, i4, i3, i2, 1.0f);
            this.muteLabel[0].setText(str);
            this.muteSubLabel[0].setText(str2);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$updateMuteButton$7 */
    public /* synthetic */ void lambda$updateMuteButton$7$GroupCallActivity(int i, int i2, int i3, int i4, int i5, int i6, int i7, ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        setColors(AndroidUtilities.getOffsetColor(i, i2, floatValue, 1.0f), AndroidUtilities.getOffsetColor(i3, i4, floatValue, 1.0f), AndroidUtilities.getOffsetColor(i, i5, floatValue, 1.0f), AndroidUtilities.getOffsetColor(i6, i7, floatValue, 1.0f), floatValue);
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

    private void setColors(int i, int i2, int i3, int i4, float f) {
        int red = Color.red(i2);
        int green = Color.green(i2);
        int blue = Color.blue(i2);
        RadialGradient radialGradient2 = new RadialGradient(0.0f, 0.0f, (float) AndroidUtilities.dp(95.0f), new int[]{Color.argb(150, red, green, blue), Color.argb(0, red, green, blue)}, (float[]) null, Shader.TileMode.CLAMP);
        this.radialGradient = radialGradient2;
        this.radialPaint.setShader(radialGradient2);
        this.muteButton.invalidate();
        this.tinyWaveDrawable.paint.setColor(ColorUtils.setAlphaComponent(i3, 38));
        this.bigWaveDrawable.paint.setColor(ColorUtils.setAlphaComponent(i3, 76));
        this.buttonWaveDrawable.paint.setColor(i3);
        WeavingState[] weavingStateArr = this.states;
        int i5 = this.muteButtonState;
        if (weavingStateArr[i5] == null) {
            weavingStateArr[i5] = new WeavingState(i5);
            int i6 = this.muteButtonState;
            if (i6 == MUTE_BUTTON_STATE_MUTED_BY_ADMIN || i6 == MUTE_BUTTON_STATE_CONNECTING) {
                Shader unused = this.states[i6].shader = new LinearGradient(0.0f, 0.0f, 0.0f, (float) AndroidUtilities.dp(122.0f), new int[]{Theme.getColor("voipgroup_disabledButton"), Theme.getColor("voipgroup_disabledButton")}, (float[]) null, Shader.TileMode.CLAMP);
            } else if (i6 == MUTE_BUTTON_STATE_MUTE) {
                Shader unused2 = this.states[i6].shader = new RadialGradient(200.0f, 200.0f, 200.0f, new int[]{Theme.getColor("voipgroup_muteButton"), Theme.getColor("voipgroup_unmuteButton")}, (float[]) null, Shader.TileMode.CLAMP);
            } else {
                Shader unused3 = this.states[i6].shader = new RadialGradient(200.0f, 200.0f, 200.0f, new int[]{Theme.getColor("voipgroup_unmuteButton2"), Theme.getColor("voipgroup_unmuteButton")}, (float[]) null, Shader.TileMode.CLAMP);
            }
        }
        WeavingState[] weavingStateArr2 = this.states;
        int i7 = this.muteButtonState;
        WeavingState weavingState = weavingStateArr2[i7];
        WeavingState weavingState2 = this.currentState;
        if (weavingState != weavingState2) {
            this.prevState = weavingState2;
            this.currentState = weavingStateArr2[i7];
        }
        this.switchProgress = f;
        if (f >= 1.0f) {
            this.prevState = null;
        }
        this.buttonsContainer.invalidate();
    }

    private void onLeaveClick() {
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
        builder.setTitle(LocaleController.getString("VoipGroupLeaveAlertTitle", NUM));
        builder.setMessage(LocaleController.getString("VoipGroupLeaveAlertText", NUM));
        CheckBoxCell[] checkBoxCellArr = new CheckBoxCell[1];
        if (ChatObject.canManageCalls(this.currentChat)) {
            LinearLayout linearLayout = new LinearLayout(getParentActivity());
            linearLayout.setOrientation(1);
            checkBoxCellArr[0] = new CheckBoxCell(getParentActivity(), 1);
            checkBoxCellArr[0].setBackgroundDrawable(Theme.getSelectorDrawable(false));
            checkBoxCellArr[0].setTextColor(Theme.getColor("voipgroup_actionBarItems"));
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
                    GroupCallActivity.lambda$onLeaveClick$8(this.f$0, view);
                }
            });
            builder.setCustomViewOffset(12);
            builder.setView(linearLayout);
        }
        builder.setPositiveButton(LocaleController.getString("VoipGroupLeave", NUM), new DialogInterface.OnClickListener(checkBoxCellArr) {
            public final /* synthetic */ CheckBoxCell[] f$1;

            {
                this.f$1 = r2;
            }

            public final void onClick(DialogInterface dialogInterface, int i) {
                GroupCallActivity.this.lambda$onLeaveClick$9$GroupCallActivity(this.f$1, dialogInterface, i);
            }
        });
        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
        AlertDialog create = builder.create();
        create.setBackgroundColor(Theme.getColor("voipgroup_actionBar"));
        showDialog(create);
        TextView textView = (TextView) create.getButton(-1);
        if (textView != null) {
            textView.setTextColor(Theme.getColor("dialogTextRed2"));
        }
        create.setTextColor(Theme.getColor("voipgroup_actionBarItems"));
    }

    static /* synthetic */ void lambda$onLeaveClick$8(CheckBoxCell[] checkBoxCellArr, View view) {
        Integer num = (Integer) view.getTag();
        checkBoxCellArr[num.intValue()].setChecked(!checkBoxCellArr[num.intValue()].isChecked(), true);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onLeaveClick$9 */
    public /* synthetic */ void lambda$onLeaveClick$9$GroupCallActivity(CheckBoxCell[] checkBoxCellArr, DialogInterface dialogInterface, int i) {
        if (VoIPService.getSharedInstance() != null) {
            VoIPService.getSharedInstance().hangUp((checkBoxCellArr[0] == null || !checkBoxCellArr[0].isChecked()) ? 0 : 1);
        }
        finishFragment();
        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.didStartedCall, new Object[0]);
    }

    public void onResume() {
        super.onResume();
        ListAdapter listAdapter2 = this.listAdapter;
        if (listAdapter2 != null) {
            listAdapter2.notifyDataSetChanged();
        }
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        /* access modifiers changed from: private */
        public int addMemberRow;
        private Context mContext;
        /* access modifiers changed from: private */
        public int rowsCount;
        /* access modifiers changed from: private */
        public int selfUserRow;
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
            this.rowsCount = i3 + GroupCallActivity.this.call.sortedParticipants.size();
        }

        public void notifyDataSetChanged() {
            updateRows();
            super.notifyDataSetChanged();
        }

        public void notifyItemChanged(int i) {
            updateRows();
            super.notifyItemChanged(i);
        }

        public void notifyItemRangeChanged(int i, int i2) {
            updateRows();
            super.notifyItemRangeChanged(i, i2);
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

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            TLRPC$TL_groupCallParticipant tLRPC$TL_groupCallParticipant;
            int itemViewType = viewHolder.getItemViewType();
            boolean z = true;
            if (itemViewType == 0) {
                GroupCallTextCell groupCallTextCell = (GroupCallTextCell) viewHolder.itemView;
                groupCallTextCell.setColors("voipgroup_blueText", "voipgroup_blueText");
                groupCallTextCell.setTextAndIcon(LocaleController.getString("AddMember", NUM), NUM, true);
            } else if (itemViewType == 1) {
                GroupCallUserCell groupCallUserCell = (GroupCallUserCell) viewHolder.itemView;
                if (i == this.selfUserRow) {
                    tLRPC$TL_groupCallParticipant = GroupCallActivity.this.selfDummyParticipant;
                } else {
                    tLRPC$TL_groupCallParticipant = GroupCallActivity.this.call.sortedParticipants.get(i - this.usersStartRow);
                }
                GroupCallActivity groupCallActivity = GroupCallActivity.this;
                ChatObject.Call call = groupCallActivity.call;
                boolean canManageCalls = ChatObject.canManageCalls(groupCallActivity.currentChat);
                if (i == getItemCount() - 1) {
                    z = false;
                }
                groupCallUserCell.setData(tLRPC$TL_groupCallParticipant, call, canManageCalls, z);
            }
        }

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            if (viewHolder.getItemViewType() != 1) {
                return true;
            }
            if (!ChatObject.canManageCalls(GroupCallActivity.this.currentChat)) {
                return false;
            }
            return !((GroupCallUserCell) viewHolder.itemView).isSelfUser();
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view;
            if (i != 0) {
                view = new GroupCallUserCell(this.mContext) {
                    /* access modifiers changed from: protected */
                    public void onMuteClick(GroupCallUserCell groupCallUserCell) {
                        if (GroupCallActivity.this.getParentActivity() != null) {
                            TLRPC$TL_groupCallParticipant participant = groupCallUserCell.getParticipant();
                            TLRPC$User user = GroupCallActivity.this.getMessagesController().getUser(Integer.valueOf(participant.user_id));
                            if (participant == null || !participant.muted || participant.can_self_unmute) {
                                AlertDialog.Builder builder = new AlertDialog.Builder((Context) GroupCallActivity.this.getParentActivity());
                                TextView textView = new TextView(GroupCallActivity.this.getParentActivity());
                                textView.setTextColor(Theme.getColor("voipgroup_actionBarItems"));
                                textView.setTextSize(1, 16.0f);
                                textView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
                                FrameLayout frameLayout = new FrameLayout(GroupCallActivity.this.getParentActivity());
                                builder.setView(frameLayout);
                                AvatarDrawable avatarDrawable = new AvatarDrawable();
                                avatarDrawable.setTextSize(AndroidUtilities.dp(12.0f));
                                BackupImageView backupImageView = new BackupImageView(GroupCallActivity.this.getParentActivity());
                                backupImageView.setRoundRadius(AndroidUtilities.dp(20.0f));
                                frameLayout.addView(backupImageView, LayoutHelper.createFrame(40, 40.0f, (LocaleController.isRTL ? 5 : 3) | 48, 22.0f, 5.0f, 22.0f, 0.0f));
                                avatarDrawable.setInfo(user);
                                backupImageView.setImage(ImageLocation.getForUser(user, false), "50_50", (Drawable) avatarDrawable, (Object) user);
                                String firstName = UserObject.getFirstName(user);
                                TextView textView2 = new TextView(GroupCallActivity.this.getParentActivity());
                                textView2.setTextColor(Theme.getColor("voipgroup_actionBarItems"));
                                textView2.setTextSize(1, 20.0f);
                                textView2.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                                textView2.setLines(1);
                                textView2.setMaxLines(1);
                                textView2.setSingleLine(true);
                                textView2.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
                                textView2.setEllipsize(TextUtils.TruncateAt.END);
                                textView2.setText(LocaleController.getString("VoipGroupMuteMemberAlertTitle", NUM));
                                boolean z = LocaleController.isRTL;
                                int i = (z ? 5 : 3) | 48;
                                int i2 = 21;
                                float f = (float) (z ? 21 : 76);
                                if (z) {
                                    i2 = 76;
                                }
                                frameLayout.addView(textView2, LayoutHelper.createFrame(-1, -2.0f, i, f, 11.0f, (float) i2, 0.0f));
                                frameLayout.addView(textView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, 24.0f, 57.0f, 24.0f, 9.0f));
                                textView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("VoipGroupMuteMemberAlertText", NUM, firstName)));
                                builder.setPositiveButton(LocaleController.getString("VoipMute", NUM), 
                                /*  JADX ERROR: Method code generation error
                                    jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x0187: INVOKE  
                                      (r1v4 'builder' org.telegram.ui.ActionBar.AlertDialog$Builder)
                                      (wrap: java.lang.String : 0x017e: INVOKE  (r3v7 java.lang.String) = ("VoipMute"), (NUM int) org.telegram.messenger.LocaleController.getString(java.lang.String, int):java.lang.String type: STATIC)
                                      (wrap: org.telegram.ui.-$$Lambda$GroupCallActivity$ListAdapter$1$9aqKi3RGOWEkDvuhPLUVqqHn-O0 : 0x0184: CONSTRUCTOR  (r4v5 org.telegram.ui.-$$Lambda$GroupCallActivity$ListAdapter$1$9aqKi3RGOWEkDvuhPLUVqqHn-O0) = 
                                      (r0v0 'this' org.telegram.ui.GroupCallActivity$ListAdapter$1 A[THIS])
                                      (r2v3 'user' org.telegram.tgnet.TLRPC$User)
                                     call: org.telegram.ui.-$$Lambda$GroupCallActivity$ListAdapter$1$9aqKi3RGOWEkDvuhPLUVqqHn-O0.<init>(org.telegram.ui.GroupCallActivity$ListAdapter$1, org.telegram.tgnet.TLRPC$User):void type: CONSTRUCTOR)
                                     org.telegram.ui.ActionBar.AlertDialog.Builder.setPositiveButton(java.lang.CharSequence, android.content.DialogInterface$OnClickListener):org.telegram.ui.ActionBar.AlertDialog$Builder type: VIRTUAL in method: org.telegram.ui.GroupCallActivity.ListAdapter.1.onMuteClick(org.telegram.ui.Cells.GroupCallUserCell):void, dex: classes.dex
                                    	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
                                    	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
                                    	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
                                    	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
                                    	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                                    	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                                    	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:98)
                                    	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:142)
                                    	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:62)
                                    	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                                    	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                                    	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                                    	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                                    	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:98)
                                    	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:142)
                                    	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:62)
                                    	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                                    	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                                    	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                                    	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                                    	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:211)
                                    	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:204)
                                    	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:318)
                                    	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
                                    	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
                                    	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                                    	at java.util.ArrayList.forEach(ArrayList.java:1259)
                                    	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                                    	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                                    	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                                    	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                                    	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                                    	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                                    	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                                    	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                                    	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                                    	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                                    	at jadx.core.codegen.InsnGen.inlineAnonymousConstructor(InsnGen.java:676)
                                    	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:607)
                                    	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
                                    	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
                                    	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
                                    	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
                                    	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
                                    	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                                    	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                                    	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:98)
                                    	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:142)
                                    	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:62)
                                    	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                                    	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                                    	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:211)
                                    	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:204)
                                    	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:318)
                                    	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
                                    	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
                                    	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                                    	at java.util.ArrayList.forEach(ArrayList.java:1259)
                                    	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                                    	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                                    	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                                    	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                                    	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                                    	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                                    	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                                    	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                                    	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                                    	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                                    	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
                                    	at jadx.core.codegen.ClassGen.addInnerClass(ClassGen.java:249)
                                    	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:238)
                                    	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                                    	at java.util.ArrayList.forEach(ArrayList.java:1259)
                                    	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                                    	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                                    	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                                    	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                                    	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                                    	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                                    	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                                    	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                                    	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                                    	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                                    	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
                                    	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:78)
                                    	at jadx.core.codegen.CodeGen.wrapCodeGen(CodeGen.java:44)
                                    	at jadx.core.codegen.CodeGen.generateJavaCode(CodeGen.java:33)
                                    	at jadx.core.codegen.CodeGen.generate(CodeGen.java:21)
                                    	at jadx.core.ProcessClass.generateCode(ProcessClass.java:61)
                                    	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:273)
                                    Caused by: jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x0184: CONSTRUCTOR  (r4v5 org.telegram.ui.-$$Lambda$GroupCallActivity$ListAdapter$1$9aqKi3RGOWEkDvuhPLUVqqHn-O0) = 
                                      (r0v0 'this' org.telegram.ui.GroupCallActivity$ListAdapter$1 A[THIS])
                                      (r2v3 'user' org.telegram.tgnet.TLRPC$User)
                                     call: org.telegram.ui.-$$Lambda$GroupCallActivity$ListAdapter$1$9aqKi3RGOWEkDvuhPLUVqqHn-O0.<init>(org.telegram.ui.GroupCallActivity$ListAdapter$1, org.telegram.tgnet.TLRPC$User):void type: CONSTRUCTOR in method: org.telegram.ui.GroupCallActivity.ListAdapter.1.onMuteClick(org.telegram.ui.Cells.GroupCallUserCell):void, dex: classes.dex
                                    	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
                                    	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                                    	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                                    	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:787)
                                    	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:728)
                                    	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
                                    	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
                                    	... 89 more
                                    Caused by: jadx.core.utils.exceptions.JadxRuntimeException: Expected class to be processed at this point, class: org.telegram.ui.-$$Lambda$GroupCallActivity$ListAdapter$1$9aqKi3RGOWEkDvuhPLUVqqHn-O0, state: NOT_LOADED
                                    	at jadx.core.dex.nodes.ClassNode.ensureProcessed(ClassNode.java:260)
                                    	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:606)
                                    	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
                                    	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                                    	... 95 more
                                    */
                                /*
                                    this = this;
                                    r0 = r21
                                    org.telegram.ui.GroupCallActivity$ListAdapter r1 = org.telegram.ui.GroupCallActivity.ListAdapter.this
                                    org.telegram.ui.GroupCallActivity r1 = org.telegram.ui.GroupCallActivity.this
                                    android.app.Activity r1 = r1.getParentActivity()
                                    if (r1 != 0) goto L_0x000d
                                    return
                                L_0x000d:
                                    org.telegram.tgnet.TLRPC$TL_groupCallParticipant r1 = r22.getParticipant()
                                    org.telegram.ui.GroupCallActivity$ListAdapter r2 = org.telegram.ui.GroupCallActivity.ListAdapter.this
                                    org.telegram.ui.GroupCallActivity r2 = org.telegram.ui.GroupCallActivity.this
                                    org.telegram.messenger.MessagesController r2 = r2.getMessagesController()
                                    int r3 = r1.user_id
                                    java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
                                    org.telegram.tgnet.TLRPC$User r2 = r2.getUser(r3)
                                    r3 = 0
                                    if (r1 == 0) goto L_0x0040
                                    boolean r4 = r1.muted
                                    if (r4 == 0) goto L_0x0040
                                    boolean r1 = r1.can_self_unmute
                                    if (r1 != 0) goto L_0x0040
                                    org.telegram.ui.GroupCallActivity$ListAdapter r1 = org.telegram.ui.GroupCallActivity.ListAdapter.this
                                    org.telegram.ui.GroupCallActivity r1 = org.telegram.ui.GroupCallActivity.this
                                    org.telegram.messenger.AccountInstance r1 = r1.getAccountInstance()
                                    org.telegram.ui.GroupCallActivity$ListAdapter r4 = org.telegram.ui.GroupCallActivity.ListAdapter.this
                                    org.telegram.ui.GroupCallActivity r4 = org.telegram.ui.GroupCallActivity.this
                                    org.telegram.messenger.ChatObject$Call r4 = r4.call
                                    org.telegram.ui.GroupCallActivity.editCallMember(r1, r4, r2, r3)
                                    return
                                L_0x0040:
                                    org.telegram.ui.ActionBar.AlertDialog$Builder r1 = new org.telegram.ui.ActionBar.AlertDialog$Builder
                                    org.telegram.ui.GroupCallActivity$ListAdapter r4 = org.telegram.ui.GroupCallActivity.ListAdapter.this
                                    org.telegram.ui.GroupCallActivity r4 = org.telegram.ui.GroupCallActivity.this
                                    android.app.Activity r4 = r4.getParentActivity()
                                    r1.<init>((android.content.Context) r4)
                                    android.widget.TextView r4 = new android.widget.TextView
                                    org.telegram.ui.GroupCallActivity$ListAdapter r5 = org.telegram.ui.GroupCallActivity.ListAdapter.this
                                    org.telegram.ui.GroupCallActivity r5 = org.telegram.ui.GroupCallActivity.this
                                    android.app.Activity r5 = r5.getParentActivity()
                                    r4.<init>(r5)
                                    java.lang.String r5 = "voipgroup_actionBarItems"
                                    int r6 = org.telegram.ui.ActionBar.Theme.getColor(r5)
                                    r4.setTextColor(r6)
                                    r6 = 1098907648(0x41800000, float:16.0)
                                    r7 = 1
                                    r4.setTextSize(r7, r6)
                                    boolean r6 = org.telegram.messenger.LocaleController.isRTL
                                    r9 = 3
                                    if (r6 == 0) goto L_0x0071
                                    r6 = 5
                                    goto L_0x0072
                                L_0x0071:
                                    r6 = 3
                                L_0x0072:
                                    r6 = r6 | 48
                                    r4.setGravity(r6)
                                    android.widget.FrameLayout r6 = new android.widget.FrameLayout
                                    org.telegram.ui.GroupCallActivity$ListAdapter r10 = org.telegram.ui.GroupCallActivity.ListAdapter.this
                                    org.telegram.ui.GroupCallActivity r10 = org.telegram.ui.GroupCallActivity.this
                                    android.app.Activity r10 = r10.getParentActivity()
                                    r6.<init>(r10)
                                    r1.setView(r6)
                                    org.telegram.ui.Components.AvatarDrawable r10 = new org.telegram.ui.Components.AvatarDrawable
                                    r10.<init>()
                                    r11 = 1094713344(0x41400000, float:12.0)
                                    int r11 = org.telegram.messenger.AndroidUtilities.dp(r11)
                                    r10.setTextSize(r11)
                                    org.telegram.ui.Components.BackupImageView r11 = new org.telegram.ui.Components.BackupImageView
                                    org.telegram.ui.GroupCallActivity$ListAdapter r12 = org.telegram.ui.GroupCallActivity.ListAdapter.this
                                    org.telegram.ui.GroupCallActivity r12 = org.telegram.ui.GroupCallActivity.this
                                    android.app.Activity r12 = r12.getParentActivity()
                                    r11.<init>(r12)
                                    r12 = 1101004800(0x41a00000, float:20.0)
                                    int r13 = org.telegram.messenger.AndroidUtilities.dp(r12)
                                    r11.setRoundRadius(r13)
                                    r14 = 40
                                    r15 = 1109393408(0x42200000, float:40.0)
                                    boolean r13 = org.telegram.messenger.LocaleController.isRTL
                                    if (r13 == 0) goto L_0x00b5
                                    r13 = 5
                                    goto L_0x00b6
                                L_0x00b5:
                                    r13 = 3
                                L_0x00b6:
                                    r16 = r13 | 48
                                    r17 = 1102053376(0x41b00000, float:22.0)
                                    r18 = 1084227584(0x40a00000, float:5.0)
                                    r19 = 1102053376(0x41b00000, float:22.0)
                                    r20 = 0
                                    android.widget.FrameLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createFrame(r14, r15, r16, r17, r18, r19, r20)
                                    r6.addView(r11, r13)
                                    r10.setInfo((org.telegram.tgnet.TLRPC$User) r2)
                                    org.telegram.messenger.ImageLocation r13 = org.telegram.messenger.ImageLocation.getForUser(r2, r3)
                                    java.lang.String r14 = "50_50"
                                    r11.setImage((org.telegram.messenger.ImageLocation) r13, (java.lang.String) r14, (android.graphics.drawable.Drawable) r10, (java.lang.Object) r2)
                                    java.lang.String r10 = org.telegram.messenger.UserObject.getFirstName(r2)
                                    android.widget.TextView r11 = new android.widget.TextView
                                    org.telegram.ui.GroupCallActivity$ListAdapter r13 = org.telegram.ui.GroupCallActivity.ListAdapter.this
                                    org.telegram.ui.GroupCallActivity r13 = org.telegram.ui.GroupCallActivity.this
                                    android.app.Activity r13 = r13.getParentActivity()
                                    r11.<init>(r13)
                                    int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
                                    r11.setTextColor(r5)
                                    r11.setTextSize(r7, r12)
                                    java.lang.String r5 = "fonts/rmedium.ttf"
                                    android.graphics.Typeface r5 = org.telegram.messenger.AndroidUtilities.getTypeface(r5)
                                    r11.setTypeface(r5)
                                    r11.setLines(r7)
                                    r11.setMaxLines(r7)
                                    r11.setSingleLine(r7)
                                    boolean r5 = org.telegram.messenger.LocaleController.isRTL
                                    if (r5 == 0) goto L_0x0106
                                    r5 = 5
                                    goto L_0x0107
                                L_0x0106:
                                    r5 = 3
                                L_0x0107:
                                    r5 = r5 | 16
                                    r11.setGravity(r5)
                                    android.text.TextUtils$TruncateAt r5 = android.text.TextUtils.TruncateAt.END
                                    r11.setEllipsize(r5)
                                    r5 = 2131627700(0x7f0e0eb4, float:1.8882672E38)
                                    java.lang.String r12 = "VoipGroupMuteMemberAlertTitle"
                                    java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r12, r5)
                                    r11.setText(r5)
                                    r12 = -1
                                    r13 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
                                    boolean r5 = org.telegram.messenger.LocaleController.isRTL
                                    if (r5 == 0) goto L_0x0126
                                    r14 = 5
                                    goto L_0x0127
                                L_0x0126:
                                    r14 = 3
                                L_0x0127:
                                    r14 = r14 | 48
                                    r15 = 21
                                    r16 = 76
                                    if (r5 == 0) goto L_0x0132
                                    r8 = 21
                                    goto L_0x0134
                                L_0x0132:
                                    r8 = 76
                                L_0x0134:
                                    float r8 = (float) r8
                                    r17 = 1093664768(0x41300000, float:11.0)
                                    if (r5 == 0) goto L_0x013b
                                    r15 = 76
                                L_0x013b:
                                    float r5 = (float) r15
                                    r18 = 0
                                    r15 = r8
                                    r16 = r17
                                    r17 = r5
                                    android.widget.FrameLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createFrame(r12, r13, r14, r15, r16, r17, r18)
                                    r6.addView(r11, r5)
                                    r12 = -2
                                    r13 = -1073741824(0xffffffffCLASSNAME, float:-2.0)
                                    boolean r5 = org.telegram.messenger.LocaleController.isRTL
                                    if (r5 == 0) goto L_0x0153
                                    r8 = 5
                                    goto L_0x0154
                                L_0x0153:
                                    r8 = 3
                                L_0x0154:
                                    r14 = r8 | 48
                                    r15 = 1103101952(0x41CLASSNAME, float:24.0)
                                    r16 = 1113849856(0x42640000, float:57.0)
                                    r17 = 1103101952(0x41CLASSNAME, float:24.0)
                                    r18 = 1091567616(0x41100000, float:9.0)
                                    android.widget.FrameLayout$LayoutParams r5 = org.telegram.ui.Components.LayoutHelper.createFrame(r12, r13, r14, r15, r16, r17, r18)
                                    r6.addView(r4, r5)
                                    r5 = 2131627699(0x7f0e0eb3, float:1.888267E38)
                                    java.lang.Object[] r6 = new java.lang.Object[r7]
                                    r6[r3] = r10
                                    java.lang.String r3 = "VoipGroupMuteMemberAlertText"
                                    java.lang.String r3 = org.telegram.messenger.LocaleController.formatString(r3, r5, r6)
                                    android.text.SpannableStringBuilder r3 = org.telegram.messenger.AndroidUtilities.replaceTags(r3)
                                    r4.setText(r3)
                                    r3 = 2131627712(0x7f0e0ec0, float:1.8882696E38)
                                    java.lang.String r4 = "VoipMute"
                                    java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
                                    org.telegram.ui.-$$Lambda$GroupCallActivity$ListAdapter$1$9aqKi3RGOWEkDvuhPLUVqqHn-O0 r4 = new org.telegram.ui.-$$Lambda$GroupCallActivity$ListAdapter$1$9aqKi3RGOWEkDvuhPLUVqqHn-O0
                                    r4.<init>(r0, r2)
                                    r1.setPositiveButton(r3, r4)
                                    r2 = 2131624583(0x7f0e0287, float:1.887635E38)
                                    java.lang.String r3 = "Cancel"
                                    java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                                    r3 = 0
                                    r1.setNegativeButton(r2, r3)
                                    org.telegram.ui.ActionBar.AlertDialog r1 = r1.create()
                                    java.lang.String r2 = "voipgroup_actionBar"
                                    int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
                                    r1.setBackgroundColor(r2)
                                    org.telegram.ui.GroupCallActivity$ListAdapter r2 = org.telegram.ui.GroupCallActivity.ListAdapter.this
                                    org.telegram.ui.GroupCallActivity r2 = org.telegram.ui.GroupCallActivity.this
                                    r2.showDialog(r1)
                                    return
                                */
                                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.GroupCallActivity.ListAdapter.AnonymousClass1.onMuteClick(org.telegram.ui.Cells.GroupCallUserCell):void");
                            }

                            /* access modifiers changed from: private */
                            /* renamed from: lambda$onMuteClick$0 */
                            public /* synthetic */ void lambda$onMuteClick$0$GroupCallActivity$ListAdapter$1(TLRPC$User tLRPC$User, DialogInterface dialogInterface, int i) {
                                GroupCallActivity.editCallMember(GroupCallActivity.this.getAccountInstance(), GroupCallActivity.this.call, tLRPC$User, true);
                            }
                        };
                    } else {
                        view = new GroupCallTextCell(this.mContext);
                    }
                    view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
                    return new RecyclerListView.Holder(view);
                }

                public int getItemViewType(int i) {
                    return i == this.addMemberRow ? 0 : 1;
                }
            }

            public void setOldRows(int i, int i2, int i3) {
                this.oldAddMemberRow = i;
                this.oldSelfUserRow = i2;
                this.oldUsersStartRow = i3;
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

            public static void editCallMember(AccountInstance accountInstance, ChatObject.Call call2, TLObject tLObject, boolean z) {
                TLRPC$TL_phone_editGroupCallMember tLRPC$TL_phone_editGroupCallMember = new TLRPC$TL_phone_editGroupCallMember();
                tLRPC$TL_phone_editGroupCallMember.call = call2.getInputGroupCall();
                if (tLObject instanceof TLRPC$User) {
                    TLRPC$User tLRPC$User = (TLRPC$User) tLObject;
                    if (UserObject.isUserSelf(tLRPC$User)) {
                        tLRPC$TL_phone_editGroupCallMember.user_id = new TLRPC$TL_inputUserSelf();
                    } else {
                        TLRPC$TL_inputUser tLRPC$TL_inputUser = new TLRPC$TL_inputUser();
                        tLRPC$TL_phone_editGroupCallMember.user_id = tLRPC$TL_inputUser;
                        tLRPC$TL_inputUser.user_id = tLRPC$User.id;
                        tLRPC$TL_inputUser.access_hash = tLRPC$User.access_hash;
                    }
                }
                tLRPC$TL_phone_editGroupCallMember.muted = z;
                accountInstance.getConnectionsManager().sendRequest(tLRPC$TL_phone_editGroupCallMember, new RequestDelegate() {
                    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                        GroupCallActivity.lambda$editCallMember$10(AccountInstance.this, tLObject, tLRPC$TL_error);
                    }
                });
            }

            static /* synthetic */ void lambda$editCallMember$10(AccountInstance accountInstance, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                if (tLObject != null) {
                    accountInstance.getMessagesController().processUpdates((TLRPC$Updates) tLObject, false);
                }
            }

            /* access modifiers changed from: private */
            public void toggleAdminSpeak() {
                TLRPC$TL_phone_toggleGroupCallSettings tLRPC$TL_phone_toggleGroupCallSettings = new TLRPC$TL_phone_toggleGroupCallSettings();
                tLRPC$TL_phone_toggleGroupCallSettings.call = this.call.getInputGroupCall();
                tLRPC$TL_phone_toggleGroupCallSettings.join_muted = this.call.call.join_muted;
                getConnectionsManager().sendRequest(tLRPC$TL_phone_toggleGroupCallSettings, new RequestDelegate() {
                    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                        GroupCallActivity.this.lambda$toggleAdminSpeak$11$GroupCallActivity(tLObject, tLRPC$TL_error);
                    }
                });
            }

            /* access modifiers changed from: private */
            /* renamed from: lambda$toggleAdminSpeak$11 */
            public /* synthetic */ void lambda$toggleAdminSpeak$11$GroupCallActivity(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                if (tLObject != null) {
                    getMessagesController().processUpdates((TLRPC$Updates) tLObject, false);
                }
            }

            public ArrayList<ThemeDescription> getThemeDescriptions() {
                return new ArrayList<>();
            }
        }
