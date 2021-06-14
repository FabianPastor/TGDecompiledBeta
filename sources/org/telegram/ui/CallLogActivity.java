package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Iterator;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.NotificationCenter;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$EncryptedChat;
import org.telegram.tgnet.TLRPC$InputPeer;
import org.telegram.tgnet.TLRPC$Message;
import org.telegram.tgnet.TLRPC$MessageAction;
import org.telegram.tgnet.TLRPC$PhoneCallDiscardReason;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_inputMessagesFilterPhoneCalls;
import org.telegram.tgnet.TLRPC$TL_inputPeerEmpty;
import org.telegram.tgnet.TLRPC$TL_messageActionHistoryClear;
import org.telegram.tgnet.TLRPC$TL_messageActionPhoneCall;
import org.telegram.tgnet.TLRPC$TL_messages_affectedFoundMessages;
import org.telegram.tgnet.TLRPC$TL_messages_deletePhoneCallHistory;
import org.telegram.tgnet.TLRPC$TL_messages_search;
import org.telegram.tgnet.TLRPC$TL_phoneCallDiscardReasonBusy;
import org.telegram.tgnet.TLRPC$TL_phoneCallDiscardReasonMissed;
import org.telegram.tgnet.TLRPC$TL_updateDeleteMessages;
import org.telegram.tgnet.TLRPC$TL_updates;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.tgnet.TLRPC$UserFull;
import org.telegram.tgnet.TLRPC$messages_Messages;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BackDrawable;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.CallLogActivity;
import org.telegram.ui.Cells.CheckBoxCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.LoadingCell;
import org.telegram.ui.Cells.LocationCell;
import org.telegram.ui.Cells.ProfileSearchCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Components.CheckBox2;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.FlickerLoadingView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.NumberTextView;
import org.telegram.ui.Components.ProgressButton;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.voip.VoIPHelper;
import org.telegram.ui.ContactsActivity;

public class CallLogActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private ArrayList<View> actionModeViews = new ArrayList<>();
    /* access modifiers changed from: private */
    public ArrayList<Integer> activeGroupCalls;
    /* access modifiers changed from: private */
    public ArrayList<CallLogRow> calls = new ArrayList<>();
    private EmptyTextProgressView emptyView;
    /* access modifiers changed from: private */
    public boolean endReached;
    private boolean firstLoaded;
    private FlickerLoadingView flickerLoadingView;
    /* access modifiers changed from: private */
    public ImageView floatingButton;
    private boolean floatingHidden;
    private final AccelerateDecelerateInterpolator floatingInterpolator = new AccelerateDecelerateInterpolator();
    private Drawable greenDrawable;
    private Drawable greenDrawable2;
    /* access modifiers changed from: private */
    public ImageSpan iconIn;
    /* access modifiers changed from: private */
    public ImageSpan iconMissed;
    /* access modifiers changed from: private */
    public ImageSpan iconOut;
    /* access modifiers changed from: private */
    public TLRPC$Chat lastCallChat;
    /* access modifiers changed from: private */
    public TLRPC$User lastCallUser;
    /* access modifiers changed from: private */
    public LinearLayoutManager layoutManager;
    /* access modifiers changed from: private */
    public RecyclerListView listView;
    /* access modifiers changed from: private */
    public ListAdapter listViewAdapter;
    /* access modifiers changed from: private */
    public boolean loading;
    private boolean openTransitionStarted;
    private ActionBarMenuItem otherItem;
    /* access modifiers changed from: private */
    public int prevPosition;
    /* access modifiers changed from: private */
    public int prevTop;
    private Drawable redDrawable;
    /* access modifiers changed from: private */
    public boolean scrollUpdated;
    private NumberTextView selectedDialogsCountTextView;
    private ArrayList<Integer> selectedIds = new ArrayList<>();
    /* access modifiers changed from: private */
    public Integer waitingForCallChatId;
    /* access modifiers changed from: private */
    public ProgressButton waitingForLoadButton;

    static /* synthetic */ boolean lambda$createActionMode$7(View view, MotionEvent motionEvent) {
        return true;
    }

    public boolean needDelayOpenAnimation() {
        return true;
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        Integer num;
        ListAdapter listAdapter;
        int i3 = 0;
        if (i == NotificationCenter.didReceiveNewMessages) {
            if (this.firstLoaded && !objArr[2].booleanValue()) {
                Iterator it = objArr[1].iterator();
                while (it.hasNext()) {
                    MessageObject messageObject = (MessageObject) it.next();
                    if (messageObject.messageOwner.action instanceof TLRPC$TL_messageActionPhoneCall) {
                        int fromChatId = messageObject.getFromChatId();
                        int i4 = fromChatId == getUserConfig().getClientUserId() ? messageObject.messageOwner.peer_id.user_id : fromChatId;
                        int i5 = fromChatId == getUserConfig().getClientUserId() ? 0 : 1;
                        TLRPC$PhoneCallDiscardReason tLRPC$PhoneCallDiscardReason = messageObject.messageOwner.action.reason;
                        if (i5 == 1 && ((tLRPC$PhoneCallDiscardReason instanceof TLRPC$TL_phoneCallDiscardReasonMissed) || (tLRPC$PhoneCallDiscardReason instanceof TLRPC$TL_phoneCallDiscardReasonBusy))) {
                            i5 = 2;
                        }
                        if (this.calls.size() > 0) {
                            CallLogRow callLogRow = this.calls.get(0);
                            if (callLogRow.user.id == i4 && callLogRow.type == i5) {
                                callLogRow.calls.add(0, messageObject.messageOwner);
                                this.listViewAdapter.notifyItemChanged(0);
                            }
                        }
                        CallLogRow callLogRow2 = new CallLogRow();
                        ArrayList<TLRPC$Message> arrayList = new ArrayList<>();
                        callLogRow2.calls = arrayList;
                        arrayList.add(messageObject.messageOwner);
                        callLogRow2.user = getMessagesController().getUser(Integer.valueOf(i4));
                        callLogRow2.type = i5;
                        callLogRow2.video = messageObject.isVideoCall();
                        this.calls.add(0, callLogRow2);
                        this.listViewAdapter.notifyItemInserted(0);
                    }
                }
                ActionBarMenuItem actionBarMenuItem = this.otherItem;
                if (actionBarMenuItem != null) {
                    if (this.calls.isEmpty()) {
                        i3 = 8;
                    }
                    actionBarMenuItem.setVisibility(i3);
                }
            }
        } else if (i == NotificationCenter.messagesDeleted) {
            if (this.firstLoaded && !objArr[2].booleanValue()) {
                ArrayList arrayList2 = objArr[0];
                Iterator<CallLogRow> it2 = this.calls.iterator();
                while (it2.hasNext()) {
                    CallLogRow next = it2.next();
                    Iterator<TLRPC$Message> it3 = next.calls.iterator();
                    while (it3.hasNext()) {
                        if (arrayList2.contains(Integer.valueOf(it3.next().id))) {
                            it3.remove();
                            i3 = 1;
                        }
                    }
                    if (next.calls.size() == 0) {
                        it2.remove();
                    }
                }
                if (i3 != 0 && (listAdapter = this.listViewAdapter) != null) {
                    listAdapter.notifyDataSetChanged();
                }
            }
        } else if (i == NotificationCenter.activeGroupCallsUpdated) {
            this.activeGroupCalls = getMessagesController().getActiveGroupCalls();
            ListAdapter listAdapter2 = this.listViewAdapter;
            if (listAdapter2 != null) {
                listAdapter2.notifyDataSetChanged();
            }
        } else if (i == NotificationCenter.chatInfoDidLoad) {
            Integer num2 = this.waitingForCallChatId;
            if (num2 != null && objArr[0].id == num2.intValue() && getMessagesController().getGroupCall(this.waitingForCallChatId.intValue(), true) != null) {
                ProgressButton progressButton = this.waitingForLoadButton;
                if (progressButton != null) {
                    progressButton.setDrawProgress(false, false);
                }
                VoIPHelper.startCall(this.lastCallChat, (TLRPC$InputPeer) null, (String) null, false, getParentActivity(), this, getAccountInstance());
                this.waitingForCallChatId = null;
            }
        } else if (i == NotificationCenter.groupCallUpdated && (num = this.waitingForCallChatId) != null && num.equals(objArr[0])) {
            ProgressButton progressButton2 = this.waitingForLoadButton;
            if (progressButton2 != null) {
                progressButton2.setDrawProgress(false, false);
            }
            VoIPHelper.startCall(this.lastCallChat, (TLRPC$InputPeer) null, (String) null, false, getParentActivity(), this, getAccountInstance());
            this.waitingForCallChatId = null;
        }
    }

    private class CallCell extends FrameLayout {
        private CheckBox2 checkBox;
        /* access modifiers changed from: private */
        public ImageView imageView;
        /* access modifiers changed from: private */
        public ProfileSearchCell profileSearchCell;

        public CallCell(Context context) {
            super(context);
            setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            ProfileSearchCell profileSearchCell2 = new ProfileSearchCell(context);
            this.profileSearchCell = profileSearchCell2;
            profileSearchCell2.setPadding(LocaleController.isRTL ? AndroidUtilities.dp(32.0f) : 0, 0, LocaleController.isRTL ? 0 : AndroidUtilities.dp(32.0f), 0);
            this.profileSearchCell.setSublabelOffset(AndroidUtilities.dp(LocaleController.isRTL ? 2.0f : -2.0f), -AndroidUtilities.dp(4.0f));
            addView(this.profileSearchCell, LayoutHelper.createFrame(-1, -1.0f));
            ImageView imageView2 = new ImageView(context);
            this.imageView = imageView2;
            imageView2.setAlpha(214);
            this.imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("featuredStickers_addButton"), PorterDuff.Mode.MULTIPLY));
            this.imageView.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor("listSelectorSDK21"), 1));
            this.imageView.setScaleType(ImageView.ScaleType.CENTER);
            this.imageView.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    CallLogActivity.CallCell.this.lambda$new$0$CallLogActivity$CallCell(view);
                }
            });
            this.imageView.setContentDescription(LocaleController.getString("Call", NUM));
            int i = 5;
            addView(this.imageView, LayoutHelper.createFrame(48, 48.0f, (LocaleController.isRTL ? 3 : 5) | 16, 8.0f, 0.0f, 8.0f, 0.0f));
            CheckBox2 checkBox2 = new CheckBox2(context, 21);
            this.checkBox = checkBox2;
            checkBox2.setColor((String) null, "windowBackgroundWhite", "checkboxCheck");
            this.checkBox.setDrawUnchecked(false);
            this.checkBox.setDrawBackgroundAsArc(3);
            addView(this.checkBox, LayoutHelper.createFrame(24, 24.0f, (!LocaleController.isRTL ? 3 : i) | 48, 42.0f, 32.0f, 42.0f, 0.0f));
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$new$0 */
        public /* synthetic */ void lambda$new$0$CallLogActivity$CallCell(View view) {
            CallLogRow callLogRow = (CallLogRow) view.getTag();
            TLRPC$UserFull userFull = CallLogActivity.this.getMessagesController().getUserFull(callLogRow.user.id);
            TLRPC$User access$102 = CallLogActivity.this.lastCallUser = callLogRow.user;
            boolean z = callLogRow.video;
            VoIPHelper.startCall(access$102, z, z || (userFull != null && userFull.video_calls_available), CallLogActivity.this.getParentActivity(), (TLRPC$UserFull) null, CallLogActivity.this.getAccountInstance());
        }

        public void setChecked(boolean z, boolean z2) {
            CheckBox2 checkBox2 = this.checkBox;
            if (checkBox2 != null) {
                checkBox2.setChecked(z, z2);
            }
        }
    }

    private class GroupCallCell extends FrameLayout {
        /* access modifiers changed from: private */
        public ProgressButton button;
        /* access modifiers changed from: private */
        public ProfileSearchCell profileSearchCell;

        public GroupCallCell(Context context) {
            super(context);
            setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            String string = LocaleController.getString("VoipChatJoin", NUM);
            ProgressButton progressButton = new ProgressButton(context);
            this.button = progressButton;
            int ceil = (int) Math.ceil((double) progressButton.getPaint().measureText(string));
            ProfileSearchCell profileSearchCell2 = new ProfileSearchCell(context);
            this.profileSearchCell = profileSearchCell2;
            profileSearchCell2.setPadding(LocaleController.isRTL ? AndroidUtilities.dp(44.0f) + ceil : 0, 0, LocaleController.isRTL ? 0 : AndroidUtilities.dp(44.0f) + ceil, 0);
            this.profileSearchCell.setSublabelOffset(0, -AndroidUtilities.dp(1.0f));
            addView(this.profileSearchCell, LayoutHelper.createFrame(-1, -1.0f));
            this.button.setText(string);
            this.button.setTextColor(Theme.getColor("featuredStickers_buttonText"));
            this.button.setProgressColor(Theme.getColor("featuredStickers_buttonProgress"));
            this.button.setBackgroundRoundRect(Theme.getColor("featuredStickers_addButton"), Theme.getColor("featuredStickers_addButtonPressed"));
            addView(this.button, LayoutHelper.createFrameRelatively(-2.0f, 28.0f, 8388661, 0.0f, 18.0f, 14.0f, 0.0f));
            this.button.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    CallLogActivity.GroupCallCell.this.lambda$new$0$CallLogActivity$GroupCallCell(view);
                }
            });
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$new$0 */
        public /* synthetic */ void lambda$new$0$CallLogActivity$GroupCallCell(View view) {
            if (CallLogActivity.this.waitingForLoadButton != null) {
                CallLogActivity.this.waitingForLoadButton.setDrawProgress(false, true);
            }
            Integer num = (Integer) view.getTag();
            ChatObject.Call groupCall = CallLogActivity.this.getMessagesController().getGroupCall(num.intValue(), false);
            CallLogActivity callLogActivity = CallLogActivity.this;
            TLRPC$Chat unused = callLogActivity.lastCallChat = callLogActivity.getMessagesController().getChat(num);
            if (groupCall != null) {
                TLRPC$Chat access$300 = CallLogActivity.this.lastCallChat;
                Activity parentActivity = CallLogActivity.this.getParentActivity();
                CallLogActivity callLogActivity2 = CallLogActivity.this;
                VoIPHelper.startCall(access$300, (TLRPC$InputPeer) null, (String) null, false, parentActivity, callLogActivity2, callLogActivity2.getAccountInstance());
                return;
            }
            Integer unused2 = CallLogActivity.this.waitingForCallChatId = num;
            CallLogActivity.this.getMessagesController().loadFullChat(num.intValue(), 0, true);
            this.button.setDrawProgress(true, true);
            ProgressButton unused3 = CallLogActivity.this.waitingForLoadButton = this.button;
        }
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        getCalls(0, 50);
        this.activeGroupCalls = getMessagesController().getActiveGroupCalls();
        getNotificationCenter().addObserver(this, NotificationCenter.didReceiveNewMessages);
        getNotificationCenter().addObserver(this, NotificationCenter.messagesDeleted);
        getNotificationCenter().addObserver(this, NotificationCenter.activeGroupCallsUpdated);
        getNotificationCenter().addObserver(this, NotificationCenter.chatInfoDidLoad);
        getNotificationCenter().addObserver(this, NotificationCenter.groupCallUpdated);
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        getNotificationCenter().removeObserver(this, NotificationCenter.didReceiveNewMessages);
        getNotificationCenter().removeObserver(this, NotificationCenter.messagesDeleted);
        getNotificationCenter().removeObserver(this, NotificationCenter.activeGroupCallsUpdated);
        getNotificationCenter().removeObserver(this, NotificationCenter.chatInfoDidLoad);
        getNotificationCenter().removeObserver(this, NotificationCenter.groupCallUpdated);
    }

    public View createView(Context context) {
        Context context2 = context;
        Drawable mutate = getParentActivity().getResources().getDrawable(NUM).mutate();
        this.greenDrawable = mutate;
        mutate.setBounds(0, 0, mutate.getIntrinsicWidth(), this.greenDrawable.getIntrinsicHeight());
        this.greenDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor("calls_callReceivedGreenIcon"), PorterDuff.Mode.MULTIPLY));
        this.iconOut = new ImageSpan(this.greenDrawable, 0);
        Drawable mutate2 = getParentActivity().getResources().getDrawable(NUM).mutate();
        this.greenDrawable2 = mutate2;
        mutate2.setBounds(0, 0, mutate2.getIntrinsicWidth(), this.greenDrawable2.getIntrinsicHeight());
        this.greenDrawable2.setColorFilter(new PorterDuffColorFilter(Theme.getColor("calls_callReceivedGreenIcon"), PorterDuff.Mode.MULTIPLY));
        this.iconIn = new ImageSpan(this.greenDrawable2, 0);
        Drawable mutate3 = getParentActivity().getResources().getDrawable(NUM).mutate();
        this.redDrawable = mutate3;
        mutate3.setBounds(0, 0, mutate3.getIntrinsicWidth(), this.redDrawable.getIntrinsicHeight());
        this.redDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor("calls_callReceivedRedIcon"), PorterDuff.Mode.MULTIPLY));
        this.iconMissed = new ImageSpan(this.redDrawable, 0);
        this.actionBar.setBackButtonDrawable(new BackDrawable(false));
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("Calls", NUM));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    if (CallLogActivity.this.actionBar.isActionModeShowed()) {
                        CallLogActivity.this.hideActionMode(true);
                    } else {
                        CallLogActivity.this.finishFragment();
                    }
                } else if (i == 1) {
                    CallLogActivity.this.showDeleteAlert(true);
                } else if (i == 2) {
                    CallLogActivity.this.showDeleteAlert(false);
                }
            }
        });
        ActionBarMenuItem addItem = this.actionBar.createMenu().addItem(10, NUM);
        this.otherItem = addItem;
        addItem.setContentDescription(LocaleController.getString("AccDescrMoreOptions", NUM));
        this.otherItem.addSubItem(1, NUM, LocaleController.getString("DeleteAllCalls", NUM));
        FrameLayout frameLayout = new FrameLayout(context2);
        this.fragmentView = frameLayout;
        frameLayout.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
        FrameLayout frameLayout2 = (FrameLayout) this.fragmentView;
        FlickerLoadingView flickerLoadingView2 = new FlickerLoadingView(context2);
        this.flickerLoadingView = flickerLoadingView2;
        flickerLoadingView2.setViewType(8);
        this.flickerLoadingView.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        this.flickerLoadingView.showDate(false);
        EmptyTextProgressView emptyTextProgressView = new EmptyTextProgressView(context2, this.flickerLoadingView);
        this.emptyView = emptyTextProgressView;
        emptyTextProgressView.setText(LocaleController.getString("NoCallLog", NUM));
        frameLayout2.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0f));
        RecyclerListView recyclerListView = new RecyclerListView(context2);
        this.listView = recyclerListView;
        recyclerListView.setEmptyView(this.emptyView);
        RecyclerListView recyclerListView2 = this.listView;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context2, 1, false);
        this.layoutManager = linearLayoutManager;
        recyclerListView2.setLayoutManager(linearLayoutManager);
        RecyclerListView recyclerListView3 = this.listView;
        ListAdapter listAdapter = new ListAdapter(context2);
        this.listViewAdapter = listAdapter;
        recyclerListView3.setAdapter(listAdapter);
        this.listView.setVerticalScrollbarPosition(LocaleController.isRTL ? 1 : 2);
        frameLayout2.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
            public final void onItemClick(View view, int i) {
                CallLogActivity.this.lambda$createView$0$CallLogActivity(view, i);
            }
        });
        this.listView.setOnItemLongClickListener((RecyclerListView.OnItemLongClickListener) new RecyclerListView.OnItemLongClickListener() {
            public final boolean onItemClick(View view, int i) {
                return CallLogActivity.this.lambda$createView$1$CallLogActivity(view, i);
            }
        });
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            /* JADX WARNING: Code restructure failed: missing block: B:27:0x00a5, code lost:
                if (java.lang.Math.abs(r1) > 1) goto L_0x00b2;
             */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void onScrolled(androidx.recyclerview.widget.RecyclerView r5, int r6, int r7) {
                /*
                    r4 = this;
                    org.telegram.ui.CallLogActivity r6 = org.telegram.ui.CallLogActivity.this
                    androidx.recyclerview.widget.LinearLayoutManager r6 = r6.layoutManager
                    int r6 = r6.findFirstVisibleItemPosition()
                    r7 = 0
                    r0 = 1
                    r1 = -1
                    if (r6 != r1) goto L_0x0011
                    r1 = 0
                    goto L_0x0021
                L_0x0011:
                    org.telegram.ui.CallLogActivity r1 = org.telegram.ui.CallLogActivity.this
                    androidx.recyclerview.widget.LinearLayoutManager r1 = r1.layoutManager
                    int r1 = r1.findLastVisibleItemPosition()
                    int r1 = r1 - r6
                    int r1 = java.lang.Math.abs(r1)
                    int r1 = r1 + r0
                L_0x0021:
                    if (r1 <= 0) goto L_0x006d
                    org.telegram.ui.CallLogActivity r2 = org.telegram.ui.CallLogActivity.this
                    org.telegram.ui.CallLogActivity$ListAdapter r2 = r2.listViewAdapter
                    int r2 = r2.getItemCount()
                    org.telegram.ui.CallLogActivity r3 = org.telegram.ui.CallLogActivity.this
                    boolean r3 = r3.endReached
                    if (r3 != 0) goto L_0x006d
                    org.telegram.ui.CallLogActivity r3 = org.telegram.ui.CallLogActivity.this
                    boolean r3 = r3.loading
                    if (r3 != 0) goto L_0x006d
                    org.telegram.ui.CallLogActivity r3 = org.telegram.ui.CallLogActivity.this
                    java.util.ArrayList r3 = r3.calls
                    boolean r3 = r3.isEmpty()
                    if (r3 != 0) goto L_0x006d
                    int r1 = r1 + r6
                    int r2 = r2 + -5
                    if (r1 < r2) goto L_0x006d
                    org.telegram.ui.CallLogActivity r1 = org.telegram.ui.CallLogActivity.this
                    java.util.ArrayList r1 = r1.calls
                    org.telegram.ui.CallLogActivity r2 = org.telegram.ui.CallLogActivity.this
                    java.util.ArrayList r2 = r2.calls
                    int r2 = r2.size()
                    int r2 = r2 - r0
                    java.lang.Object r1 = r1.get(r2)
                    org.telegram.ui.CallLogActivity$CallLogRow r1 = (org.telegram.ui.CallLogActivity.CallLogRow) r1
                    org.telegram.ui.-$$Lambda$CallLogActivity$2$e0ui-Zkgemrmp7bj51jRN57XoN8 r2 = new org.telegram.ui.-$$Lambda$CallLogActivity$2$e0ui-Zkgemrmp7bj51jRN57XoN8
                    r2.<init>(r1)
                    org.telegram.messenger.AndroidUtilities.runOnUIThread(r2)
                L_0x006d:
                    org.telegram.ui.CallLogActivity r1 = org.telegram.ui.CallLogActivity.this
                    android.widget.ImageView r1 = r1.floatingButton
                    int r1 = r1.getVisibility()
                    r2 = 8
                    if (r1 == r2) goto L_0x00d1
                    android.view.View r5 = r5.getChildAt(r7)
                    if (r5 == 0) goto L_0x0086
                    int r5 = r5.getTop()
                    goto L_0x0087
                L_0x0086:
                    r5 = 0
                L_0x0087:
                    org.telegram.ui.CallLogActivity r1 = org.telegram.ui.CallLogActivity.this
                    int r1 = r1.prevPosition
                    if (r1 != r6) goto L_0x00a8
                    org.telegram.ui.CallLogActivity r1 = org.telegram.ui.CallLogActivity.this
                    int r1 = r1.prevTop
                    int r1 = r1 - r5
                    org.telegram.ui.CallLogActivity r2 = org.telegram.ui.CallLogActivity.this
                    int r2 = r2.prevTop
                    if (r5 >= r2) goto L_0x00a0
                    r2 = 1
                    goto L_0x00a1
                L_0x00a0:
                    r2 = 0
                L_0x00a1:
                    int r1 = java.lang.Math.abs(r1)
                    if (r1 <= r0) goto L_0x00b3
                    goto L_0x00b2
                L_0x00a8:
                    org.telegram.ui.CallLogActivity r1 = org.telegram.ui.CallLogActivity.this
                    int r1 = r1.prevPosition
                    if (r6 <= r1) goto L_0x00b1
                    r7 = 1
                L_0x00b1:
                    r2 = r7
                L_0x00b2:
                    r7 = 1
                L_0x00b3:
                    if (r7 == 0) goto L_0x00c2
                    org.telegram.ui.CallLogActivity r7 = org.telegram.ui.CallLogActivity.this
                    boolean r7 = r7.scrollUpdated
                    if (r7 == 0) goto L_0x00c2
                    org.telegram.ui.CallLogActivity r7 = org.telegram.ui.CallLogActivity.this
                    r7.hideFloatingButton(r2)
                L_0x00c2:
                    org.telegram.ui.CallLogActivity r7 = org.telegram.ui.CallLogActivity.this
                    int unused = r7.prevPosition = r6
                    org.telegram.ui.CallLogActivity r6 = org.telegram.ui.CallLogActivity.this
                    int unused = r6.prevTop = r5
                    org.telegram.ui.CallLogActivity r5 = org.telegram.ui.CallLogActivity.this
                    boolean unused = r5.scrollUpdated = r0
                L_0x00d1:
                    return
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.CallLogActivity.AnonymousClass2.onScrolled(androidx.recyclerview.widget.RecyclerView, int, int):void");
            }

            /* access modifiers changed from: private */
            /* renamed from: lambda$onScrolled$0 */
            public /* synthetic */ void lambda$onScrolled$0$CallLogActivity$2(CallLogRow callLogRow) {
                CallLogActivity callLogActivity = CallLogActivity.this;
                ArrayList<TLRPC$Message> arrayList = callLogRow.calls;
                callLogActivity.getCalls(arrayList.get(arrayList.size() - 1).id, 100);
            }
        });
        if (this.loading) {
            this.emptyView.showProgress();
        } else {
            this.emptyView.showTextView();
        }
        ImageView imageView = new ImageView(context2);
        this.floatingButton = imageView;
        imageView.setVisibility(0);
        this.floatingButton.setScaleType(ImageView.ScaleType.CENTER);
        Drawable createSimpleSelectorCircleDrawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56.0f), Theme.getColor("chats_actionBackground"), Theme.getColor("chats_actionPressedBackground"));
        int i = Build.VERSION.SDK_INT;
        if (i < 21) {
            Drawable mutate4 = context.getResources().getDrawable(NUM).mutate();
            mutate4.setColorFilter(new PorterDuffColorFilter(-16777216, PorterDuff.Mode.MULTIPLY));
            CombinedDrawable combinedDrawable = new CombinedDrawable(mutate4, createSimpleSelectorCircleDrawable, 0, 0);
            combinedDrawable.setIconSize(AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
            createSimpleSelectorCircleDrawable = combinedDrawable;
        }
        this.floatingButton.setBackgroundDrawable(createSimpleSelectorCircleDrawable);
        this.floatingButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chats_actionIcon"), PorterDuff.Mode.MULTIPLY));
        this.floatingButton.setImageResource(NUM);
        this.floatingButton.setContentDescription(LocaleController.getString("Call", NUM));
        if (i >= 21) {
            StateListAnimator stateListAnimator = new StateListAnimator();
            stateListAnimator.addState(new int[]{16842919}, ObjectAnimator.ofFloat(this.floatingButton, "translationZ", new float[]{(float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(4.0f)}).setDuration(200));
            stateListAnimator.addState(new int[0], ObjectAnimator.ofFloat(this.floatingButton, "translationZ", new float[]{(float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(2.0f)}).setDuration(200));
            this.floatingButton.setStateListAnimator(stateListAnimator);
            this.floatingButton.setOutlineProvider(new ViewOutlineProvider() {
                @SuppressLint({"NewApi"})
                public void getOutline(View view, Outline outline) {
                    outline.setOval(0, 0, AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
                }
            });
        }
        ImageView imageView2 = this.floatingButton;
        int i2 = i >= 21 ? 56 : 60;
        float f = i >= 21 ? 56.0f : 60.0f;
        boolean z = LocaleController.isRTL;
        frameLayout2.addView(imageView2, LayoutHelper.createFrame(i2, f, (z ? 3 : 5) | 80, z ? 14.0f : 0.0f, 0.0f, z ? 0.0f : 14.0f, 14.0f));
        this.floatingButton.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                CallLogActivity.this.lambda$createView$3$CallLogActivity(view);
            }
        });
        return this.fragmentView;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$0 */
    public /* synthetic */ void lambda$createView$0$CallLogActivity(View view, int i) {
        if (view instanceof CallCell) {
            CallLogRow callLogRow = this.calls.get(i - this.listViewAdapter.callsStartRow);
            if (this.actionBar.isActionModeShowed()) {
                addOrRemoveSelectedDialog(callLogRow.calls, (CallCell) view);
                return;
            }
            Bundle bundle = new Bundle();
            bundle.putInt("user_id", callLogRow.user.id);
            bundle.putInt("message_id", callLogRow.calls.get(0).id);
            getNotificationCenter().postNotificationName(NotificationCenter.closeChats, new Object[0]);
            presentFragment(new ChatActivity(bundle), true);
        } else if (view instanceof GroupCallCell) {
            Bundle bundle2 = new Bundle();
            bundle2.putInt("chat_id", this.activeGroupCalls.get(i - this.listViewAdapter.activeStartRow).intValue());
            getNotificationCenter().postNotificationName(NotificationCenter.closeChats, new Object[0]);
            presentFragment(new ChatActivity(bundle2), true);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$1 */
    public /* synthetic */ boolean lambda$createView$1$CallLogActivity(View view, int i) {
        if (!(view instanceof CallCell)) {
            return false;
        }
        addOrRemoveSelectedDialog(this.calls.get(i - this.listViewAdapter.callsStartRow).calls, (CallCell) view);
        return true;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$3 */
    public /* synthetic */ void lambda$createView$3$CallLogActivity(View view) {
        Bundle bundle = new Bundle();
        bundle.putBoolean("destroyAfterSelect", true);
        bundle.putBoolean("returnAsResult", true);
        bundle.putBoolean("onlyUsers", true);
        bundle.putBoolean("allowSelf", false);
        ContactsActivity contactsActivity = new ContactsActivity(bundle);
        contactsActivity.setDelegate(new ContactsActivity.ContactsActivityDelegate() {
            public final void didSelectContact(TLRPC$User tLRPC$User, String str, ContactsActivity contactsActivity) {
                CallLogActivity.this.lambda$createView$2$CallLogActivity(tLRPC$User, str, contactsActivity);
            }
        });
        presentFragment(contactsActivity);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$2 */
    public /* synthetic */ void lambda$createView$2$CallLogActivity(TLRPC$User tLRPC$User, String str, ContactsActivity contactsActivity) {
        TLRPC$UserFull userFull = getMessagesController().getUserFull(tLRPC$User.id);
        this.lastCallUser = tLRPC$User;
        VoIPHelper.startCall(tLRPC$User, false, userFull != null && userFull.video_calls_available, getParentActivity(), (TLRPC$UserFull) null, getAccountInstance());
    }

    /* access modifiers changed from: private */
    public void showDeleteAlert(boolean z) {
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
        if (z) {
            builder.setTitle(LocaleController.getString("DeleteAllCalls", NUM));
            builder.setMessage(LocaleController.getString("DeleteAllCallsText", NUM));
        } else {
            builder.setTitle(LocaleController.getString("DeleteCalls", NUM));
            builder.setMessage(LocaleController.getString("DeleteSelectedCallsText", NUM));
        }
        boolean[] zArr = {false};
        FrameLayout frameLayout = new FrameLayout(getParentActivity());
        CheckBoxCell checkBoxCell = new CheckBoxCell(getParentActivity(), 1);
        checkBoxCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
        checkBoxCell.setText(LocaleController.getString("DeleteCallsForEveryone", NUM), "", false, false);
        checkBoxCell.setPadding(LocaleController.isRTL ? AndroidUtilities.dp(8.0f) : 0, 0, LocaleController.isRTL ? 0 : AndroidUtilities.dp(8.0f), 0);
        frameLayout.addView(checkBoxCell, LayoutHelper.createFrame(-1, 48.0f, 51, 8.0f, 0.0f, 8.0f, 0.0f));
        checkBoxCell.setOnClickListener(new View.OnClickListener(zArr) {
            public final /* synthetic */ boolean[] f$0;

            {
                this.f$0 = r1;
            }

            public final void onClick(View view) {
                CallLogActivity.lambda$showDeleteAlert$4(this.f$0, view);
            }
        });
        builder.setView(frameLayout);
        builder.setPositiveButton(LocaleController.getString("Delete", NUM), new DialogInterface.OnClickListener(z, zArr) {
            public final /* synthetic */ boolean f$1;
            public final /* synthetic */ boolean[] f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void onClick(DialogInterface dialogInterface, int i) {
                CallLogActivity.this.lambda$showDeleteAlert$5$CallLogActivity(this.f$1, this.f$2, dialogInterface, i);
            }
        });
        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
        AlertDialog create = builder.create();
        showDialog(create);
        TextView textView = (TextView) create.getButton(-1);
        if (textView != null) {
            textView.setTextColor(Theme.getColor("dialogTextRed2"));
        }
    }

    static /* synthetic */ void lambda$showDeleteAlert$4(boolean[] zArr, View view) {
        zArr[0] = !zArr[0];
        ((CheckBoxCell) view).setChecked(zArr[0], true);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$showDeleteAlert$5 */
    public /* synthetic */ void lambda$showDeleteAlert$5$CallLogActivity(boolean z, boolean[] zArr, DialogInterface dialogInterface, int i) {
        if (z) {
            deleteAllMessages(zArr[0]);
            this.calls.clear();
            this.loading = false;
            this.endReached = true;
            this.otherItem.setVisibility(8);
            this.listViewAdapter.notifyDataSetChanged();
        } else {
            getMessagesController().deleteMessages(new ArrayList(this.selectedIds), (ArrayList<Long>) null, (TLRPC$EncryptedChat) null, 0, 0, zArr[0], false);
        }
        hideActionMode(false);
    }

    private void deleteAllMessages(boolean z) {
        TLRPC$TL_messages_deletePhoneCallHistory tLRPC$TL_messages_deletePhoneCallHistory = new TLRPC$TL_messages_deletePhoneCallHistory();
        tLRPC$TL_messages_deletePhoneCallHistory.revoke = z;
        getConnectionsManager().sendRequest(tLRPC$TL_messages_deletePhoneCallHistory, new RequestDelegate(z) {
            public final /* synthetic */ boolean f$1;

            {
                this.f$1 = r2;
            }

            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                CallLogActivity.this.lambda$deleteAllMessages$6$CallLogActivity(this.f$1, tLObject, tLRPC$TL_error);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$deleteAllMessages$6 */
    public /* synthetic */ void lambda$deleteAllMessages$6$CallLogActivity(boolean z, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLObject != null) {
            TLRPC$TL_messages_affectedFoundMessages tLRPC$TL_messages_affectedFoundMessages = (TLRPC$TL_messages_affectedFoundMessages) tLObject;
            TLRPC$TL_updateDeleteMessages tLRPC$TL_updateDeleteMessages = new TLRPC$TL_updateDeleteMessages();
            tLRPC$TL_updateDeleteMessages.messages = tLRPC$TL_messages_affectedFoundMessages.messages;
            tLRPC$TL_updateDeleteMessages.pts = tLRPC$TL_messages_affectedFoundMessages.pts;
            tLRPC$TL_updateDeleteMessages.pts_count = tLRPC$TL_messages_affectedFoundMessages.pts_count;
            TLRPC$TL_updates tLRPC$TL_updates = new TLRPC$TL_updates();
            tLRPC$TL_updates.updates.add(tLRPC$TL_updateDeleteMessages);
            getMessagesController().processUpdates(tLRPC$TL_updates, false);
            if (tLRPC$TL_messages_affectedFoundMessages.offset != 0) {
                deleteAllMessages(z);
            }
        }
    }

    /* access modifiers changed from: private */
    public void hideActionMode(boolean z) {
        this.actionBar.hideActionMode();
        this.selectedIds.clear();
        int childCount = this.listView.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = this.listView.getChildAt(i);
            if (childAt instanceof CallCell) {
                ((CallCell) childAt).setChecked(false, z);
            }
        }
    }

    /* access modifiers changed from: private */
    public boolean isSelected(ArrayList<TLRPC$Message> arrayList) {
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            if (this.selectedIds.contains(Integer.valueOf(arrayList.get(i).id))) {
                return true;
            }
        }
        return false;
    }

    private void createActionMode() {
        if (!this.actionBar.actionModeIsExist((String) null)) {
            ActionBarMenu createActionMode = this.actionBar.createActionMode();
            NumberTextView numberTextView = new NumberTextView(createActionMode.getContext());
            this.selectedDialogsCountTextView = numberTextView;
            numberTextView.setTextSize(18);
            this.selectedDialogsCountTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.selectedDialogsCountTextView.setTextColor(Theme.getColor("actionBarActionModeDefaultIcon"));
            createActionMode.addView(this.selectedDialogsCountTextView, LayoutHelper.createLinear(0, -1, 1.0f, 72, 0, 0, 0));
            this.selectedDialogsCountTextView.setOnTouchListener($$Lambda$CallLogActivity$eZ0Kp0kBy9R5VaLHzktibQGtU38.INSTANCE);
            this.actionModeViews.add(createActionMode.addItemWithWidth(2, NUM, AndroidUtilities.dp(54.0f), LocaleController.getString("Delete", NUM)));
        }
    }

    private boolean addOrRemoveSelectedDialog(ArrayList<TLRPC$Message> arrayList, CallCell callCell) {
        if (arrayList.isEmpty()) {
            return false;
        }
        if (isSelected(arrayList)) {
            int size = arrayList.size();
            for (int i = 0; i < size; i++) {
                this.selectedIds.remove(Integer.valueOf(arrayList.get(i).id));
            }
            callCell.setChecked(false, true);
            showOrUpdateActionMode();
            return false;
        }
        int size2 = arrayList.size();
        for (int i2 = 0; i2 < size2; i2++) {
            Integer valueOf = Integer.valueOf(arrayList.get(i2).id);
            if (!this.selectedIds.contains(valueOf)) {
                this.selectedIds.add(valueOf);
            }
        }
        callCell.setChecked(true, true);
        showOrUpdateActionMode();
        return true;
    }

    private void showOrUpdateActionMode() {
        boolean z = false;
        if (!this.actionBar.isActionModeShowed()) {
            createActionMode();
            this.actionBar.showActionMode();
            AnimatorSet animatorSet = new AnimatorSet();
            ArrayList arrayList = new ArrayList();
            for (int i = 0; i < this.actionModeViews.size(); i++) {
                View view = this.actionModeViews.get(i);
                view.setPivotY((float) (ActionBar.getCurrentActionBarHeight() / 2));
                AndroidUtilities.clearDrawableAnimation(view);
                arrayList.add(ObjectAnimator.ofFloat(view, View.SCALE_Y, new float[]{0.1f, 1.0f}));
            }
            animatorSet.playTogether(arrayList);
            animatorSet.setDuration(200);
            animatorSet.start();
        } else if (this.selectedIds.isEmpty()) {
            hideActionMode(true);
            return;
        } else {
            z = true;
        }
        this.selectedDialogsCountTextView.setNumber(this.selectedIds.size(), z);
    }

    /* access modifiers changed from: private */
    public void hideFloatingButton(boolean z) {
        if (this.floatingHidden != z) {
            this.floatingHidden = z;
            ImageView imageView = this.floatingButton;
            float[] fArr = new float[1];
            fArr[0] = z ? (float) AndroidUtilities.dp(100.0f) : 0.0f;
            ObjectAnimator duration = ObjectAnimator.ofFloat(imageView, "translationY", fArr).setDuration(300);
            duration.setInterpolator(this.floatingInterpolator);
            this.floatingButton.setClickable(!z);
            duration.start();
        }
    }

    /* access modifiers changed from: private */
    public void getCalls(int i, int i2) {
        if (!this.loading) {
            this.loading = true;
            EmptyTextProgressView emptyTextProgressView = this.emptyView;
            if (emptyTextProgressView != null && !this.firstLoaded) {
                emptyTextProgressView.showProgress();
            }
            ListAdapter listAdapter = this.listViewAdapter;
            if (listAdapter != null) {
                listAdapter.notifyDataSetChanged();
            }
            TLRPC$TL_messages_search tLRPC$TL_messages_search = new TLRPC$TL_messages_search();
            tLRPC$TL_messages_search.limit = i2;
            tLRPC$TL_messages_search.peer = new TLRPC$TL_inputPeerEmpty();
            tLRPC$TL_messages_search.filter = new TLRPC$TL_inputMessagesFilterPhoneCalls();
            tLRPC$TL_messages_search.q = "";
            tLRPC$TL_messages_search.offset_id = i;
            getConnectionsManager().bindRequestToGuid(getConnectionsManager().sendRequest(tLRPC$TL_messages_search, new RequestDelegate() {
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    CallLogActivity.this.lambda$getCalls$9$CallLogActivity(tLObject, tLRPC$TL_error);
                }
            }, 2), this.classGuid);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$getCalls$9 */
    public /* synthetic */ void lambda$getCalls$9$CallLogActivity(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLRPC$TL_error, tLObject) {
            public final /* synthetic */ TLRPC$TL_error f$1;
            public final /* synthetic */ TLObject f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                CallLogActivity.this.lambda$getCalls$8$CallLogActivity(this.f$1, this.f$2);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$getCalls$8 */
    public /* synthetic */ void lambda$getCalls$8$CallLogActivity(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject) {
        CallLogRow callLogRow;
        int i = 0;
        int max = Math.max(this.listViewAdapter.callsStartRow, 0) + this.calls.size();
        if (tLRPC$TL_error == null) {
            SparseArray sparseArray = new SparseArray();
            TLRPC$messages_Messages tLRPC$messages_Messages = (TLRPC$messages_Messages) tLObject;
            this.endReached = tLRPC$messages_Messages.messages.isEmpty();
            for (int i2 = 0; i2 < tLRPC$messages_Messages.users.size(); i2++) {
                TLRPC$User tLRPC$User = tLRPC$messages_Messages.users.get(i2);
                sparseArray.put(tLRPC$User.id, tLRPC$User);
            }
            if (this.calls.size() > 0) {
                ArrayList<CallLogRow> arrayList = this.calls;
                callLogRow = arrayList.get(arrayList.size() - 1);
            } else {
                callLogRow = null;
            }
            for (int i3 = 0; i3 < tLRPC$messages_Messages.messages.size(); i3++) {
                TLRPC$Message tLRPC$Message = tLRPC$messages_Messages.messages.get(i3);
                TLRPC$MessageAction tLRPC$MessageAction = tLRPC$Message.action;
                if (tLRPC$MessageAction != null && !(tLRPC$MessageAction instanceof TLRPC$TL_messageActionHistoryClear)) {
                    int i4 = MessageObject.getFromChatId(tLRPC$Message) == getUserConfig().getClientUserId() ? 0 : 1;
                    TLRPC$PhoneCallDiscardReason tLRPC$PhoneCallDiscardReason = tLRPC$Message.action.reason;
                    if (i4 == 1 && ((tLRPC$PhoneCallDiscardReason instanceof TLRPC$TL_phoneCallDiscardReasonMissed) || (tLRPC$PhoneCallDiscardReason instanceof TLRPC$TL_phoneCallDiscardReasonBusy))) {
                        i4 = 2;
                    }
                    int fromChatId = MessageObject.getFromChatId(tLRPC$Message);
                    if (fromChatId == getUserConfig().getClientUserId()) {
                        fromChatId = tLRPC$Message.peer_id.user_id;
                    }
                    if (!(callLogRow != null && callLogRow.user.id == fromChatId && callLogRow.type == i4)) {
                        if (callLogRow != null && !this.calls.contains(callLogRow)) {
                            this.calls.add(callLogRow);
                        }
                        callLogRow = new CallLogRow();
                        callLogRow.calls = new ArrayList<>();
                        callLogRow.user = (TLRPC$User) sparseArray.get(fromChatId);
                        callLogRow.type = i4;
                        TLRPC$MessageAction tLRPC$MessageAction2 = tLRPC$Message.action;
                        callLogRow.video = tLRPC$MessageAction2 != null && tLRPC$MessageAction2.video;
                    }
                    callLogRow.calls.add(tLRPC$Message);
                }
            }
            if (callLogRow != null && callLogRow.calls.size() > 0 && !this.calls.contains(callLogRow)) {
                this.calls.add(callLogRow);
            }
        } else {
            this.endReached = true;
        }
        this.loading = false;
        showItemsAnimated(max);
        if (!this.firstLoaded) {
            resumeDelayedFragmentAnimation();
        }
        this.firstLoaded = true;
        ActionBarMenuItem actionBarMenuItem = this.otherItem;
        if (this.calls.isEmpty()) {
            i = 8;
        }
        actionBarMenuItem.setVisibility(i);
        EmptyTextProgressView emptyTextProgressView = this.emptyView;
        if (emptyTextProgressView != null) {
            emptyTextProgressView.showTextView();
        }
        ListAdapter listAdapter = this.listViewAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    public void onResume() {
        super.onResume();
        ListAdapter listAdapter = this.listViewAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    public void onRequestPermissionsResultFragment(int i, String[] strArr, int[] iArr) {
        boolean z;
        if (i == 101 || i == 102 || i == 103) {
            int i2 = 0;
            while (true) {
                if (i2 >= iArr.length) {
                    z = true;
                    break;
                } else if (iArr[i2] != 0) {
                    z = false;
                    break;
                } else {
                    i2++;
                }
            }
            TLRPC$UserFull tLRPC$UserFull = null;
            if (iArr.length <= 0 || !z) {
                VoIPHelper.permissionDenied(getParentActivity(), (Runnable) null, i);
            } else if (i == 103) {
                VoIPHelper.startCall(this.lastCallChat, (TLRPC$InputPeer) null, (String) null, false, getParentActivity(), this, getAccountInstance());
            } else {
                if (this.lastCallUser != null) {
                    tLRPC$UserFull = getMessagesController().getUserFull(this.lastCallUser.id);
                }
                VoIPHelper.startCall(this.lastCallUser, i == 102, i == 102 || (tLRPC$UserFull != null && tLRPC$UserFull.video_calls_available), getParentActivity(), (TLRPC$UserFull) null, getAccountInstance());
            }
        }
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private int activeEndRow;
        /* access modifiers changed from: private */
        public int activeHeaderRow;
        /* access modifiers changed from: private */
        public int activeStartRow;
        private int callsEndRow;
        private int callsHeaderRow;
        /* access modifiers changed from: private */
        public int callsStartRow;
        private int loadingCallsRow;
        private Context mContext;
        private int rowsCount;
        private int sectionRow;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        private void updateRows() {
            this.activeHeaderRow = -1;
            this.callsHeaderRow = -1;
            this.activeStartRow = -1;
            this.activeEndRow = -1;
            this.callsStartRow = -1;
            this.callsEndRow = -1;
            this.loadingCallsRow = -1;
            this.sectionRow = -1;
            this.rowsCount = 0;
            if (!CallLogActivity.this.activeGroupCalls.isEmpty()) {
                int i = this.rowsCount;
                int i2 = i + 1;
                this.rowsCount = i2;
                this.activeHeaderRow = i;
                this.activeStartRow = i2;
                int size = i2 + CallLogActivity.this.activeGroupCalls.size();
                this.rowsCount = size;
                this.activeEndRow = size;
            }
            if (!CallLogActivity.this.calls.isEmpty()) {
                if (this.activeHeaderRow != -1) {
                    int i3 = this.rowsCount;
                    int i4 = i3 + 1;
                    this.rowsCount = i4;
                    this.sectionRow = i3;
                    this.rowsCount = i4 + 1;
                    this.callsHeaderRow = i4;
                }
                int i5 = this.rowsCount;
                this.callsStartRow = i5;
                int size2 = i5 + CallLogActivity.this.calls.size();
                this.rowsCount = size2;
                this.callsEndRow = size2;
                if (!CallLogActivity.this.endReached) {
                    int i6 = this.rowsCount;
                    this.rowsCount = i6 + 1;
                    this.loadingCallsRow = i6;
                }
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

        public void notifyItemInserted(int i) {
            updateRows();
            super.notifyItemInserted(i);
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

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            int itemViewType = viewHolder.getItemViewType();
            return itemViewType == 0 || itemViewType == 4;
        }

        public int getItemCount() {
            return this.rowsCount;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view;
            HeaderCell headerCell;
            if (i != 0) {
                if (i == 1) {
                    FlickerLoadingView flickerLoadingView = new FlickerLoadingView(this.mContext);
                    flickerLoadingView.setIsSingleCell(true);
                    flickerLoadingView.setViewType(8);
                    flickerLoadingView.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    flickerLoadingView.showDate(false);
                    headerCell = flickerLoadingView;
                } else if (i == 2) {
                    view = new TextInfoPrivacyCell(this.mContext);
                    view.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                } else if (i == 3) {
                    HeaderCell headerCell2 = new HeaderCell(this.mContext);
                    headerCell2.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    headerCell = headerCell2;
                } else if (i != 4) {
                    view = new ShadowSectionCell(this.mContext);
                } else {
                    view = new GroupCallCell(this.mContext);
                }
                view = headerCell;
            } else {
                view = new CallCell(this.mContext);
            }
            return new RecyclerListView.Holder(view);
        }

        public void onViewAttachedToWindow(RecyclerView.ViewHolder viewHolder) {
            View view = viewHolder.itemView;
            if (view instanceof CallCell) {
                ((CallCell) viewHolder.itemView).setChecked(CallLogActivity.this.isSelected(((CallLogRow) CallLogActivity.this.calls.get(viewHolder.getAdapterPosition() - this.callsStartRow)).calls), false);
            } else if (view instanceof GroupCallCell) {
                GroupCallCell groupCallCell = (GroupCallCell) view;
                TLRPC$Chat chat = groupCallCell.profileSearchCell.getChat();
                if (CallLogActivity.this.waitingForCallChatId == null || chat.id != CallLogActivity.this.waitingForCallChatId.intValue()) {
                    groupCallCell.button.setDrawProgress(false, false);
                    return;
                }
                ProgressButton unused = CallLogActivity.this.waitingForLoadButton = groupCallCell.button;
                groupCallCell.button.setDrawProgress(true, false);
            }
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            SpannableString spannableString;
            String str;
            RecyclerView.ViewHolder viewHolder2 = viewHolder;
            int i2 = i;
            int itemViewType = viewHolder.getItemViewType();
            boolean z = false;
            if (itemViewType == 0) {
                int i3 = i2 - this.callsStartRow;
                CallLogRow callLogRow = (CallLogRow) CallLogActivity.this.calls.get(i3);
                CallCell callCell = (CallCell) viewHolder2.itemView;
                callCell.imageView.setImageResource(callLogRow.video ? NUM : NUM);
                TLRPC$Message tLRPC$Message = callLogRow.calls.get(0);
                String str2 = LocaleController.isRTL ? "‫" : "";
                if (callLogRow.calls.size() == 1) {
                    spannableString = new SpannableString(str2 + "  " + LocaleController.formatDateCallLog((long) tLRPC$Message.date));
                } else {
                    spannableString = new SpannableString(String.format(str2 + "  (%d) %s", new Object[]{Integer.valueOf(callLogRow.calls.size()), LocaleController.formatDateCallLog((long) tLRPC$Message.date)}));
                }
                SpannableString spannableString2 = spannableString;
                int i4 = callLogRow.type;
                if (i4 == 0) {
                    spannableString2.setSpan(CallLogActivity.this.iconOut, str2.length(), str2.length() + 1, 0);
                } else if (i4 == 1) {
                    spannableString2.setSpan(CallLogActivity.this.iconIn, str2.length(), str2.length() + 1, 0);
                } else if (i4 == 2) {
                    spannableString2.setSpan(CallLogActivity.this.iconMissed, str2.length(), str2.length() + 1, 0);
                }
                callCell.profileSearchCell.setData(callLogRow.user, (TLRPC$EncryptedChat) null, (CharSequence) null, spannableString2, false, false);
                ProfileSearchCell access$2700 = callCell.profileSearchCell;
                if (i3 != CallLogActivity.this.calls.size() - 1 || !CallLogActivity.this.endReached) {
                    z = true;
                }
                access$2700.useSeparator = z;
                callCell.imageView.setTag(callLogRow);
            } else if (itemViewType == 3) {
                HeaderCell headerCell = (HeaderCell) viewHolder2.itemView;
                if (i2 == this.activeHeaderRow) {
                    headerCell.setText(LocaleController.getString("VoipChatActiveChats", NUM));
                } else if (i2 == this.callsHeaderRow) {
                    headerCell.setText(LocaleController.getString("VoipChatRecentCalls", NUM));
                }
            } else if (itemViewType == 4) {
                int i5 = i2 - this.activeStartRow;
                TLRPC$Chat chat = CallLogActivity.this.getMessagesController().getChat((Integer) CallLogActivity.this.activeGroupCalls.get(i5));
                GroupCallCell groupCallCell = (GroupCallCell) viewHolder2.itemView;
                groupCallCell.button.setTag(Integer.valueOf(chat.id));
                if (!ChatObject.isChannel(chat) || chat.megagroup) {
                    if (chat.has_geo) {
                        str = LocaleController.getString("MegaLocation", NUM);
                    } else if (TextUtils.isEmpty(chat.username)) {
                        str = LocaleController.getString("MegaPrivate", NUM).toLowerCase();
                    } else {
                        str = LocaleController.getString("MegaPublic", NUM).toLowerCase();
                    }
                } else if (TextUtils.isEmpty(chat.username)) {
                    str = LocaleController.getString("ChannelPrivate", NUM).toLowerCase();
                } else {
                    str = LocaleController.getString("ChannelPublic", NUM).toLowerCase();
                }
                groupCallCell.profileSearchCell.setData(chat, (TLRPC$EncryptedChat) null, (CharSequence) null, str, false, false);
                ProfileSearchCell access$2100 = groupCallCell.profileSearchCell;
                if (i5 != CallLogActivity.this.activeGroupCalls.size() - 1 || !CallLogActivity.this.endReached) {
                    z = true;
                }
                access$2100.useSeparator = z;
            }
        }

        public int getItemViewType(int i) {
            if (i == this.activeHeaderRow || i == this.callsHeaderRow) {
                return 3;
            }
            if (i >= this.callsStartRow && i < this.callsEndRow) {
                return 0;
            }
            if (i >= this.activeStartRow && i < this.activeEndRow) {
                return 4;
            }
            if (i == this.loadingCallsRow) {
                return 1;
            }
            return i == this.sectionRow ? 5 : 2;
        }
    }

    private static class CallLogRow {
        public ArrayList<TLRPC$Message> calls;
        public int type;
        public TLRPC$User user;
        public boolean video;

        private CallLogRow() {
        }
    }

    /* access modifiers changed from: protected */
    public void onTransitionAnimationStart(boolean z, boolean z2) {
        super.onTransitionAnimationStart(z, z2);
        if (z) {
            this.openTransitionStarted = true;
        }
    }

    private void showItemsAnimated(final int i) {
        if (!this.isPaused && this.openTransitionStarted) {
            final View view = null;
            for (int i2 = 0; i2 < this.listView.getChildCount(); i2++) {
                View childAt = this.listView.getChildAt(i2);
                if (childAt instanceof FlickerLoadingView) {
                    view = childAt;
                }
            }
            if (view != null) {
                this.listView.removeView(view);
            }
            this.listView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                public boolean onPreDraw() {
                    CallLogActivity.this.listView.getViewTreeObserver().removeOnPreDrawListener(this);
                    int childCount = CallLogActivity.this.listView.getChildCount();
                    AnimatorSet animatorSet = new AnimatorSet();
                    for (int i = 0; i < childCount; i++) {
                        View childAt = CallLogActivity.this.listView.getChildAt(i);
                        RecyclerView.ViewHolder childViewHolder = CallLogActivity.this.listView.getChildViewHolder(childAt);
                        if (childAt != view && CallLogActivity.this.listView.getChildAdapterPosition(childAt) >= i && !(childAt instanceof GroupCallCell) && (!(childAt instanceof HeaderCell) || childViewHolder.getAdapterPosition() != CallLogActivity.this.listViewAdapter.activeHeaderRow)) {
                            childAt.setAlpha(0.0f);
                            ObjectAnimator ofFloat = ObjectAnimator.ofFloat(childAt, View.ALPHA, new float[]{0.0f, 1.0f});
                            ofFloat.setStartDelay((long) ((int) ((((float) Math.min(CallLogActivity.this.listView.getMeasuredHeight(), Math.max(0, childAt.getTop()))) / ((float) CallLogActivity.this.listView.getMeasuredHeight())) * 100.0f)));
                            ofFloat.setDuration(200);
                            animatorSet.playTogether(new Animator[]{ofFloat});
                        }
                    }
                    View view = view;
                    if (view != null && view.getParent() == null) {
                        CallLogActivity.this.listView.addView(view);
                        final RecyclerView.LayoutManager layoutManager = CallLogActivity.this.listView.getLayoutManager();
                        if (layoutManager != null) {
                            layoutManager.ignoreView(view);
                            View view2 = view;
                            ObjectAnimator ofFloat2 = ObjectAnimator.ofFloat(view2, View.ALPHA, new float[]{view2.getAlpha(), 0.0f});
                            ofFloat2.addListener(new AnimatorListenerAdapter() {
                                public void onAnimationEnd(Animator animator) {
                                    view.setAlpha(1.0f);
                                    layoutManager.stopIgnoringView(view);
                                    CallLogActivity.this.listView.removeView(view);
                                }
                            });
                            ofFloat2.start();
                        }
                    }
                    animatorSet.start();
                    return true;
                }
            });
        }
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        $$Lambda$CallLogActivity$BqlyWakImEy8BoEsmIz9D_UoumI r10 = new ThemeDescription.ThemeDescriptionDelegate() {
            public final void didSetColor() {
                CallLogActivity.this.lambda$getThemeDescriptions$10$CallLogActivity();
            }
        };
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{LocationCell.class, CallCell.class, HeaderCell.class, GroupCallCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultIcon"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultTitle"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "divider"));
        arrayList.add(new ThemeDescription(this.emptyView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "emptyListPlaceholder"));
        arrayList.add(new ThemeDescription(this.emptyView, ThemeDescription.FLAG_PROGRESSBAR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "progressCircle"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{LoadingCell.class}, new String[]{"progressBar"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "progressCircle"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText4"));
        arrayList.add(new ThemeDescription(this.floatingButton, ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_actionIcon"));
        arrayList.add(new ThemeDescription(this.floatingButton, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_actionBackground"));
        arrayList.add(new ThemeDescription(this.floatingButton, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_actionPressedBackground"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{CallCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "featuredStickers_addButton"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{CallCell.class}, (Paint) null, new Drawable[]{Theme.dialogs_verifiedCheckDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_verifiedCheck"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{CallCell.class}, (Paint) null, new Drawable[]{Theme.dialogs_verifiedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_verifiedBackground"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{CallCell.class}, Theme.dialogs_offlinePaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText3"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{CallCell.class}, Theme.dialogs_onlinePaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueText3"));
        TextPaint[] textPaintArr = Theme.dialogs_namePaint;
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{CallCell.class}, (String[]) null, new Paint[]{textPaintArr[0], textPaintArr[1], Theme.dialogs_searchNamePaint}, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_name"));
        TextPaint[] textPaintArr2 = Theme.dialogs_nameEncryptedPaint;
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{CallCell.class}, (String[]) null, new Paint[]{textPaintArr2[0], textPaintArr2[1], Theme.dialogs_searchNameEncryptedPaint}, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_secretName"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{CallCell.class}, (Paint) null, Theme.avatarDrawables, (ThemeDescription.ThemeDescriptionDelegate) null, "avatar_text"));
        $$Lambda$CallLogActivity$BqlyWakImEy8BoEsmIz9D_UoumI r8 = r10;
        ThemeDescription themeDescription = r2;
        ThemeDescription themeDescription2 = new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "avatar_backgroundRed");
        arrayList.add(themeDescription);
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "avatar_backgroundOrange"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "avatar_backgroundViolet"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "avatar_backgroundGreen"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "avatar_backgroundCyan"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "avatar_backgroundBlue"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "avatar_backgroundPink"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, (Paint) null, new Drawable[]{this.greenDrawable, this.greenDrawable2, Theme.calllog_msgCallUpRedDrawable, Theme.calllog_msgCallDownRedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "calls_callReceivedGreenIcon"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, (Paint) null, new Drawable[]{this.redDrawable, Theme.calllog_msgCallUpGreenDrawable, Theme.calllog_msgCallDownGreenDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "calls_callReceivedRedIcon"));
        arrayList.add(new ThemeDescription(this.flickerLoadingView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueHeader"));
        return arrayList;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$getThemeDescriptions$10 */
    public /* synthetic */ void lambda$getThemeDescriptions$10$CallLogActivity() {
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            int childCount = recyclerListView.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = this.listView.getChildAt(i);
                if (childAt instanceof CallCell) {
                    ((CallCell) childAt).profileSearchCell.update(0);
                }
            }
        }
    }
}
