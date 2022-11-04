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
import android.content.res.Resources;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.style.ImageSpan;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.collection.LongSparseArray;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Iterator;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$ChatFull;
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
import org.telegram.ui.Components.FlickerLoadingView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.NumberTextView;
import org.telegram.ui.Components.ProgressButton;
import org.telegram.ui.Components.RLottieImageView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.voip.VoIPHelper;
import org.telegram.ui.ContactsActivity;
/* loaded from: classes3.dex */
public class CallLogActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private ArrayList<Long> activeGroupCalls;
    private EmptyTextProgressView emptyView;
    private boolean endReached;
    private boolean firstLoaded;
    private FlickerLoadingView flickerLoadingView;
    private ImageView floatingButton;
    private boolean floatingHidden;
    private Drawable greenDrawable;
    private Drawable greenDrawable2;
    private ImageSpan iconIn;
    private ImageSpan iconMissed;
    private ImageSpan iconOut;
    private TLRPC$Chat lastCallChat;
    private TLRPC$User lastCallUser;
    private LinearLayoutManager layoutManager;
    private RecyclerListView listView;
    private ListAdapter listViewAdapter;
    private boolean loading;
    private boolean openTransitionStarted;
    private ActionBarMenuItem otherItem;
    private int prevPosition;
    private int prevTop;
    private Drawable redDrawable;
    private boolean scrollUpdated;
    private NumberTextView selectedDialogsCountTextView;
    private Long waitingForCallChatId;
    private ArrayList<View> actionModeViews = new ArrayList<>();
    private ArrayList<CallLogRow> calls = new ArrayList<>();
    private ArrayList<Integer> selectedIds = new ArrayList<>();
    private final AccelerateDecelerateInterpolator floatingInterpolator = new AccelerateDecelerateInterpolator();

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$createActionMode$7(View view, MotionEvent motionEvent) {
        return true;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean needDelayOpenAnimation() {
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public static class EmptyTextProgressView extends FrameLayout {
        private TextView emptyTextView1;
        private TextView emptyTextView2;
        private RLottieImageView imageView;
        private View progressView;

        /* JADX INFO: Access modifiers changed from: private */
        public static /* synthetic */ boolean lambda$new$1(View view, MotionEvent motionEvent) {
            return true;
        }

        @Override // android.view.View
        public boolean hasOverlappingRendering() {
            return false;
        }

        public EmptyTextProgressView(Context context, View view) {
            super(context);
            addView(view, LayoutHelper.createFrame(-1, -1.0f));
            this.progressView = view;
            RLottieImageView rLottieImageView = new RLottieImageView(context);
            this.imageView = rLottieImageView;
            rLottieImageView.setAnimation(R.raw.utyan_call, 120, 120);
            this.imageView.setAutoRepeat(false);
            addView(this.imageView, LayoutHelper.createFrame(140, 140.0f, 17, 52.0f, 4.0f, 52.0f, 60.0f));
            this.imageView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.CallLogActivity$EmptyTextProgressView$$ExternalSyntheticLambda0
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    CallLogActivity.EmptyTextProgressView.this.lambda$new$0(view2);
                }
            });
            TextView textView = new TextView(context);
            this.emptyTextView1 = textView;
            textView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.emptyTextView1.setText(LocaleController.getString("NoRecentCalls", R.string.NoRecentCalls));
            this.emptyTextView1.setTextSize(1, 20.0f);
            this.emptyTextView1.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.emptyTextView1.setGravity(17);
            addView(this.emptyTextView1, LayoutHelper.createFrame(-1, -2.0f, 17, 17.0f, 40.0f, 17.0f, 0.0f));
            this.emptyTextView2 = new TextView(context);
            String string = LocaleController.getString("NoRecentCallsInfo", R.string.NoRecentCallsInfo);
            if (AndroidUtilities.isTablet() && !AndroidUtilities.isSmallTablet()) {
                string = string.replace('\n', ' ');
            }
            this.emptyTextView2.setText(string);
            this.emptyTextView2.setTextColor(Theme.getColor("emptyListPlaceholder"));
            this.emptyTextView2.setTextSize(1, 14.0f);
            this.emptyTextView2.setGravity(17);
            this.emptyTextView2.setLineSpacing(AndroidUtilities.dp(2.0f), 1.0f);
            addView(this.emptyTextView2, LayoutHelper.createFrame(-1, -2.0f, 17, 17.0f, 80.0f, 17.0f, 0.0f));
            view.setAlpha(0.0f);
            this.imageView.setAlpha(0.0f);
            this.emptyTextView1.setAlpha(0.0f);
            this.emptyTextView2.setAlpha(0.0f);
            setOnTouchListener(CallLogActivity$EmptyTextProgressView$$ExternalSyntheticLambda1.INSTANCE);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$0(View view) {
            if (!this.imageView.isPlaying()) {
                this.imageView.setProgress(0.0f);
                this.imageView.playAnimation();
            }
        }

        public void showProgress() {
            this.imageView.animate().alpha(0.0f).setDuration(150L).start();
            this.emptyTextView1.animate().alpha(0.0f).setDuration(150L).start();
            this.emptyTextView2.animate().alpha(0.0f).setDuration(150L).start();
            this.progressView.animate().alpha(1.0f).setDuration(150L).start();
        }

        public void showTextView() {
            this.imageView.animate().alpha(1.0f).setDuration(150L).start();
            this.emptyTextView1.animate().alpha(1.0f).setDuration(150L).start();
            this.emptyTextView2.animate().alpha(1.0f).setDuration(150L).start();
            this.progressView.animate().alpha(0.0f).setDuration(150L).start();
            this.imageView.playAnimation();
        }
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        Long l;
        ListAdapter listAdapter;
        int i3 = 0;
        if (i == NotificationCenter.didReceiveNewMessages) {
            if (!this.firstLoaded || ((Boolean) objArr[2]).booleanValue()) {
                return;
            }
            Iterator it = ((ArrayList) objArr[1]).iterator();
            while (it.hasNext()) {
                MessageObject messageObject = (MessageObject) it.next();
                if (messageObject.messageOwner.action instanceof TLRPC$TL_messageActionPhoneCall) {
                    long fromChatId = messageObject.getFromChatId();
                    long j = fromChatId == getUserConfig().getClientUserId() ? messageObject.messageOwner.peer_id.user_id : fromChatId;
                    int i4 = fromChatId == getUserConfig().getClientUserId() ? 0 : 1;
                    TLRPC$PhoneCallDiscardReason tLRPC$PhoneCallDiscardReason = messageObject.messageOwner.action.reason;
                    if (i4 == 1 && ((tLRPC$PhoneCallDiscardReason instanceof TLRPC$TL_phoneCallDiscardReasonMissed) || (tLRPC$PhoneCallDiscardReason instanceof TLRPC$TL_phoneCallDiscardReasonBusy))) {
                        i4 = 2;
                    }
                    if (this.calls.size() > 0) {
                        CallLogRow callLogRow = this.calls.get(0);
                        if (callLogRow.user.id == j && callLogRow.type == i4) {
                            callLogRow.calls.add(0, messageObject.messageOwner);
                            this.listViewAdapter.notifyItemChanged(0);
                        }
                    }
                    CallLogRow callLogRow2 = new CallLogRow();
                    ArrayList<TLRPC$Message> arrayList = new ArrayList<>();
                    callLogRow2.calls = arrayList;
                    arrayList.add(messageObject.messageOwner);
                    callLogRow2.user = getMessagesController().getUser(Long.valueOf(j));
                    callLogRow2.type = i4;
                    callLogRow2.video = messageObject.isVideoCall();
                    this.calls.add(0, callLogRow2);
                    this.listViewAdapter.notifyItemInserted(0);
                }
            }
            ActionBarMenuItem actionBarMenuItem = this.otherItem;
            if (actionBarMenuItem == null) {
                return;
            }
            if (this.calls.isEmpty()) {
                i3 = 8;
            }
            actionBarMenuItem.setVisibility(i3);
        } else if (i == NotificationCenter.messagesDeleted) {
            if (!this.firstLoaded || ((Boolean) objArr[2]).booleanValue()) {
                return;
            }
            ArrayList arrayList2 = (ArrayList) objArr[0];
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
            if (i3 == 0 || (listAdapter = this.listViewAdapter) == null) {
                return;
            }
            listAdapter.notifyDataSetChanged();
        } else if (i == NotificationCenter.activeGroupCallsUpdated) {
            this.activeGroupCalls = getMessagesController().getActiveGroupCalls();
            ListAdapter listAdapter2 = this.listViewAdapter;
            if (listAdapter2 == null) {
                return;
            }
            listAdapter2.notifyDataSetChanged();
        } else if (i == NotificationCenter.chatInfoDidLoad) {
            Long l2 = this.waitingForCallChatId;
            if (l2 == null || ((TLRPC$ChatFull) objArr[0]).id != l2.longValue() || getMessagesController().getGroupCall(this.waitingForCallChatId.longValue(), true) == null) {
                return;
            }
            VoIPHelper.startCall(this.lastCallChat, null, null, false, getParentActivity(), this, getAccountInstance());
            this.waitingForCallChatId = null;
        } else if (i != NotificationCenter.groupCallUpdated || (l = this.waitingForCallChatId) == null || !l.equals((Long) objArr[0])) {
        } else {
            VoIPHelper.startCall(this.lastCallChat, null, null, false, getParentActivity(), this, getAccountInstance());
            this.waitingForCallChatId = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public class CallCell extends FrameLayout {
        private CheckBox2 checkBox;
        private ImageView imageView;
        private ProfileSearchCell profileSearchCell;

        public CallCell(Context context) {
            super(context);
            setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            ProfileSearchCell profileSearchCell = new ProfileSearchCell(context);
            this.profileSearchCell = profileSearchCell;
            profileSearchCell.setPadding(LocaleController.isRTL ? AndroidUtilities.dp(32.0f) : 0, 0, LocaleController.isRTL ? 0 : AndroidUtilities.dp(32.0f), 0);
            this.profileSearchCell.setSublabelOffset(AndroidUtilities.dp(LocaleController.isRTL ? 2.0f : -2.0f), -AndroidUtilities.dp(4.0f));
            addView(this.profileSearchCell, LayoutHelper.createFrame(-1, -1.0f));
            ImageView imageView = new ImageView(context);
            this.imageView = imageView;
            imageView.setAlpha(214);
            this.imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("featuredStickers_addButton"), PorterDuff.Mode.MULTIPLY));
            this.imageView.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor("listSelectorSDK21"), 1));
            this.imageView.setScaleType(ImageView.ScaleType.CENTER);
            this.imageView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.CallLogActivity$CallCell$$ExternalSyntheticLambda0
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    CallLogActivity.CallCell.this.lambda$new$0(view);
                }
            });
            this.imageView.setContentDescription(LocaleController.getString("Call", R.string.Call));
            int i = 5;
            addView(this.imageView, LayoutHelper.createFrame(48, 48.0f, (LocaleController.isRTL ? 3 : 5) | 16, 8.0f, 0.0f, 8.0f, 0.0f));
            CheckBox2 checkBox2 = new CheckBox2(context, 21);
            this.checkBox = checkBox2;
            checkBox2.setColor(null, "windowBackgroundWhite", "checkboxCheck");
            this.checkBox.setDrawUnchecked(false);
            this.checkBox.setDrawBackgroundAsArc(3);
            addView(this.checkBox, LayoutHelper.createFrame(24, 24.0f, (!LocaleController.isRTL ? 3 : i) | 48, 42.0f, 32.0f, 42.0f, 0.0f));
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$0(View view) {
            CallLogRow callLogRow = (CallLogRow) view.getTag();
            TLRPC$UserFull userFull = CallLogActivity.this.getMessagesController().getUserFull(callLogRow.user.id);
            TLRPC$User tLRPC$User = CallLogActivity.this.lastCallUser = callLogRow.user;
            boolean z = callLogRow.video;
            VoIPHelper.startCall(tLRPC$User, z, z || (userFull != null && userFull.video_calls_available), CallLogActivity.this.getParentActivity(), null, CallLogActivity.this.getAccountInstance());
        }

        public void setChecked(boolean z, boolean z2) {
            CheckBox2 checkBox2 = this.checkBox;
            if (checkBox2 == null) {
                return;
            }
            checkBox2.setChecked(z, z2);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public class GroupCallCell extends FrameLayout {
        private ProgressButton button;
        private TLRPC$Chat currentChat;
        private ProfileSearchCell profileSearchCell;

        public GroupCallCell(Context context) {
            super(context);
            setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            String string = LocaleController.getString("VoipChatJoin", R.string.VoipChatJoin);
            ProgressButton progressButton = new ProgressButton(context);
            this.button = progressButton;
            int ceil = (int) Math.ceil(progressButton.getPaint().measureText(string));
            ProfileSearchCell profileSearchCell = new ProfileSearchCell(context);
            this.profileSearchCell = profileSearchCell;
            profileSearchCell.setPadding(LocaleController.isRTL ? AndroidUtilities.dp(44.0f) + ceil : 0, 0, LocaleController.isRTL ? 0 : AndroidUtilities.dp(44.0f) + ceil, 0);
            this.profileSearchCell.setSublabelOffset(0, -AndroidUtilities.dp(1.0f));
            addView(this.profileSearchCell, LayoutHelper.createFrame(-1, -1.0f));
            this.button.setText(string);
            this.button.setTextSize(1, 14.0f);
            this.button.setTextColor(Theme.getColor("featuredStickers_buttonText"));
            this.button.setProgressColor(Theme.getColor("featuredStickers_buttonProgress"));
            this.button.setBackgroundRoundRect(Theme.getColor("featuredStickers_addButton"), Theme.getColor("featuredStickers_addButtonPressed"), 16.0f);
            this.button.setPadding(AndroidUtilities.dp(14.0f), 0, AndroidUtilities.dp(14.0f), 0);
            addView(this.button, LayoutHelper.createFrameRelatively(-2.0f, 28.0f, 8388661, 0.0f, 16.0f, 14.0f, 0.0f));
            this.button.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.CallLogActivity$GroupCallCell$$ExternalSyntheticLambda0
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    CallLogActivity.GroupCallCell.this.lambda$new$0(view);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$0(View view) {
            Long l = (Long) view.getTag();
            ChatObject.Call groupCall = CallLogActivity.this.getMessagesController().getGroupCall(l.longValue(), false);
            CallLogActivity callLogActivity = CallLogActivity.this;
            callLogActivity.lastCallChat = callLogActivity.getMessagesController().getChat(l);
            if (groupCall != null) {
                TLRPC$Chat tLRPC$Chat = CallLogActivity.this.lastCallChat;
                Activity parentActivity = CallLogActivity.this.getParentActivity();
                CallLogActivity callLogActivity2 = CallLogActivity.this;
                VoIPHelper.startCall(tLRPC$Chat, null, null, false, parentActivity, callLogActivity2, callLogActivity2.getAccountInstance());
                return;
            }
            CallLogActivity.this.waitingForCallChatId = l;
            CallLogActivity.this.getMessagesController().loadFullChat(l.longValue(), 0, true);
        }

        public void setChat(TLRPC$Chat tLRPC$Chat) {
            this.currentChat = tLRPC$Chat;
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
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

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        getNotificationCenter().removeObserver(this, NotificationCenter.didReceiveNewMessages);
        getNotificationCenter().removeObserver(this, NotificationCenter.messagesDeleted);
        getNotificationCenter().removeObserver(this, NotificationCenter.activeGroupCallsUpdated);
        getNotificationCenter().removeObserver(this, NotificationCenter.chatInfoDidLoad);
        getNotificationCenter().removeObserver(this, NotificationCenter.groupCallUpdated);
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public View createView(Context context) {
        Drawable mutate = getParentActivity().getResources().getDrawable(R.drawable.ic_call_made_green_18dp).mutate();
        this.greenDrawable = mutate;
        mutate.setBounds(0, 0, mutate.getIntrinsicWidth(), this.greenDrawable.getIntrinsicHeight());
        this.greenDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor("calls_callReceivedGreenIcon"), PorterDuff.Mode.MULTIPLY));
        this.iconOut = new ImageSpan(this.greenDrawable, 0);
        Resources resources = getParentActivity().getResources();
        int i = R.drawable.ic_call_received_green_18dp;
        Drawable mutate2 = resources.getDrawable(i).mutate();
        this.greenDrawable2 = mutate2;
        mutate2.setBounds(0, 0, mutate2.getIntrinsicWidth(), this.greenDrawable2.getIntrinsicHeight());
        this.greenDrawable2.setColorFilter(new PorterDuffColorFilter(Theme.getColor("calls_callReceivedGreenIcon"), PorterDuff.Mode.MULTIPLY));
        this.iconIn = new ImageSpan(this.greenDrawable2, 0);
        Drawable mutate3 = getParentActivity().getResources().getDrawable(i).mutate();
        this.redDrawable = mutate3;
        mutate3.setBounds(0, 0, mutate3.getIntrinsicWidth(), this.redDrawable.getIntrinsicHeight());
        this.redDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor("calls_callReceivedRedIcon"), PorterDuff.Mode.MULTIPLY));
        this.iconMissed = new ImageSpan(this.redDrawable, 0);
        this.actionBar.setBackButtonDrawable(new BackDrawable(false));
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("Calls", R.string.Calls));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() { // from class: org.telegram.ui.CallLogActivity.1
            @Override // org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick
            public void onItemClick(int i2) {
                if (i2 == -1) {
                    if (((BaseFragment) CallLogActivity.this).actionBar.isActionModeShowed()) {
                        CallLogActivity.this.hideActionMode(true);
                    } else {
                        CallLogActivity.this.finishFragment();
                    }
                } else if (i2 == 1) {
                    CallLogActivity.this.showDeleteAlert(true);
                } else if (i2 != 2) {
                } else {
                    CallLogActivity.this.showDeleteAlert(false);
                }
            }
        });
        ActionBarMenuItem addItem = this.actionBar.createMenu().addItem(10, R.drawable.ic_ab_other);
        this.otherItem = addItem;
        addItem.setContentDescription(LocaleController.getString("AccDescrMoreOptions", R.string.AccDescrMoreOptions));
        this.otherItem.addSubItem(1, R.drawable.msg_delete, LocaleController.getString("DeleteAllCalls", R.string.DeleteAllCalls));
        FrameLayout frameLayout = new FrameLayout(context);
        this.fragmentView = frameLayout;
        frameLayout.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
        FrameLayout frameLayout2 = (FrameLayout) this.fragmentView;
        FlickerLoadingView flickerLoadingView = new FlickerLoadingView(context);
        this.flickerLoadingView = flickerLoadingView;
        flickerLoadingView.setViewType(8);
        this.flickerLoadingView.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        this.flickerLoadingView.showDate(false);
        EmptyTextProgressView emptyTextProgressView = new EmptyTextProgressView(context, this.flickerLoadingView);
        this.emptyView = emptyTextProgressView;
        frameLayout2.addView(emptyTextProgressView, LayoutHelper.createFrame(-1, -1.0f));
        RecyclerListView recyclerListView = new RecyclerListView(context);
        this.listView = recyclerListView;
        recyclerListView.setEmptyView(this.emptyView);
        RecyclerListView recyclerListView2 = this.listView;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, 1, false);
        this.layoutManager = linearLayoutManager;
        recyclerListView2.setLayoutManager(linearLayoutManager);
        RecyclerListView recyclerListView3 = this.listView;
        ListAdapter listAdapter = new ListAdapter(context);
        this.listViewAdapter = listAdapter;
        recyclerListView3.setAdapter(listAdapter);
        this.listView.setVerticalScrollbarPosition(LocaleController.isRTL ? 1 : 2);
        frameLayout2.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() { // from class: org.telegram.ui.CallLogActivity$$ExternalSyntheticLambda8
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
            public final void onItemClick(View view, int i2) {
                CallLogActivity.this.lambda$createView$0(view, i2);
            }
        });
        this.listView.setOnItemLongClickListener(new RecyclerListView.OnItemLongClickListener() { // from class: org.telegram.ui.CallLogActivity$$ExternalSyntheticLambda9
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener
            public final boolean onItemClick(View view, int i2) {
                boolean lambda$createView$1;
                lambda$createView$1 = CallLogActivity.this.lambda$createView$1(view, i2);
                return lambda$createView$1;
            }
        });
        this.listView.setOnScrollListener(new AnonymousClass2());
        if (this.loading) {
            this.emptyView.showProgress();
        } else {
            this.emptyView.showTextView();
        }
        ImageView imageView = new ImageView(context);
        this.floatingButton = imageView;
        imageView.setVisibility(0);
        this.floatingButton.setScaleType(ImageView.ScaleType.CENTER);
        Drawable createSimpleSelectorCircleDrawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56.0f), Theme.getColor("chats_actionBackground"), Theme.getColor("chats_actionPressedBackground"));
        int i2 = Build.VERSION.SDK_INT;
        if (i2 < 21) {
            Drawable mutate4 = context.getResources().getDrawable(R.drawable.floating_shadow).mutate();
            mutate4.setColorFilter(new PorterDuffColorFilter(-16777216, PorterDuff.Mode.MULTIPLY));
            CombinedDrawable combinedDrawable = new CombinedDrawable(mutate4, createSimpleSelectorCircleDrawable, 0, 0);
            combinedDrawable.setIconSize(AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
            createSimpleSelectorCircleDrawable = combinedDrawable;
        }
        this.floatingButton.setBackgroundDrawable(createSimpleSelectorCircleDrawable);
        this.floatingButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chats_actionIcon"), PorterDuff.Mode.MULTIPLY));
        this.floatingButton.setImageResource(R.drawable.ic_call);
        this.floatingButton.setContentDescription(LocaleController.getString("Call", R.string.Call));
        if (i2 >= 21) {
            StateListAnimator stateListAnimator = new StateListAnimator();
            stateListAnimator.addState(new int[]{16842919}, ObjectAnimator.ofFloat(this.floatingButton, "translationZ", AndroidUtilities.dp(2.0f), AndroidUtilities.dp(4.0f)).setDuration(200L));
            stateListAnimator.addState(new int[0], ObjectAnimator.ofFloat(this.floatingButton, "translationZ", AndroidUtilities.dp(4.0f), AndroidUtilities.dp(2.0f)).setDuration(200L));
            this.floatingButton.setStateListAnimator(stateListAnimator);
            this.floatingButton.setOutlineProvider(new ViewOutlineProvider(this) { // from class: org.telegram.ui.CallLogActivity.3
                @Override // android.view.ViewOutlineProvider
                @SuppressLint({"NewApi"})
                public void getOutline(View view, Outline outline) {
                    outline.setOval(0, 0, AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
                }
            });
        }
        ImageView imageView2 = this.floatingButton;
        int i3 = i2 >= 21 ? 56 : 60;
        float f = i2 >= 21 ? 56.0f : 60.0f;
        boolean z = LocaleController.isRTL;
        frameLayout2.addView(imageView2, LayoutHelper.createFrame(i3, f, (z ? 3 : 5) | 80, z ? 14.0f : 0.0f, 0.0f, z ? 0.0f : 14.0f, 14.0f));
        this.floatingButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.CallLogActivity$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                CallLogActivity.this.lambda$createView$3(view);
            }
        });
        return this.fragmentView;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$0(View view, int i) {
        if (!(view instanceof CallCell)) {
            if (!(view instanceof GroupCallCell)) {
                return;
            }
            Bundle bundle = new Bundle();
            bundle.putLong("chat_id", ((GroupCallCell) view).currentChat.id);
            getNotificationCenter().postNotificationName(NotificationCenter.closeChats, new Object[0]);
            presentFragment(new ChatActivity(bundle), true);
            return;
        }
        CallLogRow callLogRow = this.calls.get(i - this.listViewAdapter.callsStartRow);
        if (this.actionBar.isActionModeShowed()) {
            addOrRemoveSelectedDialog(callLogRow.calls, (CallCell) view);
            return;
        }
        Bundle bundle2 = new Bundle();
        bundle2.putLong("user_id", callLogRow.user.id);
        bundle2.putInt("message_id", callLogRow.calls.get(0).id);
        getNotificationCenter().postNotificationName(NotificationCenter.closeChats, new Object[0]);
        presentFragment(new ChatActivity(bundle2), true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$createView$1(View view, int i) {
        if (view instanceof CallCell) {
            addOrRemoveSelectedDialog(this.calls.get(i - this.listViewAdapter.callsStartRow).calls, (CallCell) view);
            return true;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: org.telegram.ui.CallLogActivity$2  reason: invalid class name */
    /* loaded from: classes3.dex */
    public class AnonymousClass2 extends RecyclerView.OnScrollListener {
        AnonymousClass2() {
        }

        /* JADX WARN: Code restructure failed: missing block: B:29:0x00a5, code lost:
            if (java.lang.Math.abs(r1) > 1) goto L33;
         */
        @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public void onScrolled(androidx.recyclerview.widget.RecyclerView r5, int r6, int r7) {
            /*
                r4 = this;
                org.telegram.ui.CallLogActivity r6 = org.telegram.ui.CallLogActivity.this
                androidx.recyclerview.widget.LinearLayoutManager r6 = org.telegram.ui.CallLogActivity.access$700(r6)
                int r6 = r6.findFirstVisibleItemPosition()
                r7 = 0
                r0 = 1
                r1 = -1
                if (r6 != r1) goto L11
                r1 = 0
                goto L21
            L11:
                org.telegram.ui.CallLogActivity r1 = org.telegram.ui.CallLogActivity.this
                androidx.recyclerview.widget.LinearLayoutManager r1 = org.telegram.ui.CallLogActivity.access$700(r1)
                int r1 = r1.findLastVisibleItemPosition()
                int r1 = r1 - r6
                int r1 = java.lang.Math.abs(r1)
                int r1 = r1 + r0
            L21:
                if (r1 <= 0) goto L6d
                org.telegram.ui.CallLogActivity r2 = org.telegram.ui.CallLogActivity.this
                org.telegram.ui.CallLogActivity$ListAdapter r2 = org.telegram.ui.CallLogActivity.access$800(r2)
                int r2 = r2.getItemCount()
                org.telegram.ui.CallLogActivity r3 = org.telegram.ui.CallLogActivity.this
                boolean r3 = org.telegram.ui.CallLogActivity.access$900(r3)
                if (r3 != 0) goto L6d
                org.telegram.ui.CallLogActivity r3 = org.telegram.ui.CallLogActivity.this
                boolean r3 = org.telegram.ui.CallLogActivity.access$1000(r3)
                if (r3 != 0) goto L6d
                org.telegram.ui.CallLogActivity r3 = org.telegram.ui.CallLogActivity.this
                java.util.ArrayList r3 = org.telegram.ui.CallLogActivity.access$1100(r3)
                boolean r3 = r3.isEmpty()
                if (r3 != 0) goto L6d
                int r1 = r1 + r6
                int r2 = r2 + (-5)
                if (r1 < r2) goto L6d
                org.telegram.ui.CallLogActivity r1 = org.telegram.ui.CallLogActivity.this
                java.util.ArrayList r1 = org.telegram.ui.CallLogActivity.access$1100(r1)
                org.telegram.ui.CallLogActivity r2 = org.telegram.ui.CallLogActivity.this
                java.util.ArrayList r2 = org.telegram.ui.CallLogActivity.access$1100(r2)
                int r2 = r2.size()
                int r2 = r2 - r0
                java.lang.Object r1 = r1.get(r2)
                org.telegram.ui.CallLogActivity$CallLogRow r1 = (org.telegram.ui.CallLogActivity.CallLogRow) r1
                org.telegram.ui.CallLogActivity$2$$ExternalSyntheticLambda0 r2 = new org.telegram.ui.CallLogActivity$2$$ExternalSyntheticLambda0
                r2.<init>()
                org.telegram.messenger.AndroidUtilities.runOnUIThread(r2)
            L6d:
                org.telegram.ui.CallLogActivity r1 = org.telegram.ui.CallLogActivity.this
                android.widget.ImageView r1 = org.telegram.ui.CallLogActivity.access$1200(r1)
                int r1 = r1.getVisibility()
                r2 = 8
                if (r1 == r2) goto Ld1
                android.view.View r5 = r5.getChildAt(r7)
                if (r5 == 0) goto L86
                int r5 = r5.getTop()
                goto L87
            L86:
                r5 = 0
            L87:
                org.telegram.ui.CallLogActivity r1 = org.telegram.ui.CallLogActivity.this
                int r1 = org.telegram.ui.CallLogActivity.access$1300(r1)
                if (r1 != r6) goto La8
                org.telegram.ui.CallLogActivity r1 = org.telegram.ui.CallLogActivity.this
                int r1 = org.telegram.ui.CallLogActivity.access$1400(r1)
                int r1 = r1 - r5
                org.telegram.ui.CallLogActivity r2 = org.telegram.ui.CallLogActivity.this
                int r2 = org.telegram.ui.CallLogActivity.access$1400(r2)
                if (r5 >= r2) goto La0
                r2 = 1
                goto La1
            La0:
                r2 = 0
            La1:
                int r1 = java.lang.Math.abs(r1)
                if (r1 <= r0) goto Lb3
                goto Lb2
            La8:
                org.telegram.ui.CallLogActivity r1 = org.telegram.ui.CallLogActivity.this
                int r1 = org.telegram.ui.CallLogActivity.access$1300(r1)
                if (r6 <= r1) goto Lb1
                r7 = 1
            Lb1:
                r2 = r7
            Lb2:
                r7 = 1
            Lb3:
                if (r7 == 0) goto Lc2
                org.telegram.ui.CallLogActivity r7 = org.telegram.ui.CallLogActivity.this
                boolean r7 = org.telegram.ui.CallLogActivity.access$1500(r7)
                if (r7 == 0) goto Lc2
                org.telegram.ui.CallLogActivity r7 = org.telegram.ui.CallLogActivity.this
                org.telegram.ui.CallLogActivity.access$1600(r7, r2)
            Lc2:
                org.telegram.ui.CallLogActivity r7 = org.telegram.ui.CallLogActivity.this
                org.telegram.ui.CallLogActivity.access$1302(r7, r6)
                org.telegram.ui.CallLogActivity r6 = org.telegram.ui.CallLogActivity.this
                org.telegram.ui.CallLogActivity.access$1402(r6, r5)
                org.telegram.ui.CallLogActivity r5 = org.telegram.ui.CallLogActivity.this
                org.telegram.ui.CallLogActivity.access$1502(r5, r0)
            Ld1:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.CallLogActivity.AnonymousClass2.onScrolled(androidx.recyclerview.widget.RecyclerView, int, int):void");
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onScrolled$0(CallLogRow callLogRow) {
            CallLogActivity callLogActivity = CallLogActivity.this;
            ArrayList<TLRPC$Message> arrayList = callLogRow.calls;
            callLogActivity.getCalls(arrayList.get(arrayList.size() - 1).id, 100);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$3(View view) {
        Bundle bundle = new Bundle();
        bundle.putBoolean("destroyAfterSelect", true);
        bundle.putBoolean("returnAsResult", true);
        bundle.putBoolean("onlyUsers", true);
        bundle.putBoolean("allowSelf", false);
        ContactsActivity contactsActivity = new ContactsActivity(bundle);
        contactsActivity.setDelegate(new ContactsActivity.ContactsActivityDelegate() { // from class: org.telegram.ui.CallLogActivity$$ExternalSyntheticLambda10
            @Override // org.telegram.ui.ContactsActivity.ContactsActivityDelegate
            public final void didSelectContact(TLRPC$User tLRPC$User, String str, ContactsActivity contactsActivity2) {
                CallLogActivity.this.lambda$createView$2(tLRPC$User, str, contactsActivity2);
            }
        });
        presentFragment(contactsActivity);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$2(TLRPC$User tLRPC$User, String str, ContactsActivity contactsActivity) {
        TLRPC$UserFull userFull = getMessagesController().getUserFull(tLRPC$User.id);
        this.lastCallUser = tLRPC$User;
        VoIPHelper.startCall(tLRPC$User, false, userFull != null && userFull.video_calls_available, getParentActivity(), null, getAccountInstance());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showDeleteAlert(final boolean z) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        if (z) {
            builder.setTitle(LocaleController.getString("DeleteAllCalls", R.string.DeleteAllCalls));
            builder.setMessage(LocaleController.getString("DeleteAllCallsText", R.string.DeleteAllCallsText));
        } else {
            builder.setTitle(LocaleController.getString("DeleteCalls", R.string.DeleteCalls));
            builder.setMessage(LocaleController.getString("DeleteSelectedCallsText", R.string.DeleteSelectedCallsText));
        }
        final boolean[] zArr = {false};
        FrameLayout frameLayout = new FrameLayout(getParentActivity());
        CheckBoxCell checkBoxCell = new CheckBoxCell(getParentActivity(), 1);
        checkBoxCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
        checkBoxCell.setText(LocaleController.getString("DeleteCallsForEveryone", R.string.DeleteCallsForEveryone), "", false, false);
        checkBoxCell.setPadding(LocaleController.isRTL ? AndroidUtilities.dp(8.0f) : 0, 0, LocaleController.isRTL ? 0 : AndroidUtilities.dp(8.0f), 0);
        frameLayout.addView(checkBoxCell, LayoutHelper.createFrame(-1, 48.0f, 51, 8.0f, 0.0f, 8.0f, 0.0f));
        checkBoxCell.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.CallLogActivity$$ExternalSyntheticLambda2
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                CallLogActivity.lambda$showDeleteAlert$4(zArr, view);
            }
        });
        builder.setView(frameLayout);
        builder.setPositiveButton(LocaleController.getString("Delete", R.string.Delete), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.CallLogActivity$$ExternalSyntheticLambda0
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                CallLogActivity.this.lambda$showDeleteAlert$5(z, zArr, dialogInterface, i);
            }
        });
        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
        AlertDialog create = builder.create();
        showDialog(create);
        TextView textView = (TextView) create.getButton(-1);
        if (textView != null) {
            textView.setTextColor(Theme.getColor("dialogTextRed2"));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$showDeleteAlert$4(boolean[] zArr, View view) {
        zArr[0] = !zArr[0];
        ((CheckBoxCell) view).setChecked(zArr[0], true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$showDeleteAlert$5(boolean z, boolean[] zArr, DialogInterface dialogInterface, int i) {
        if (z) {
            deleteAllMessages(zArr[0]);
            this.calls.clear();
            this.loading = false;
            this.endReached = true;
            this.otherItem.setVisibility(8);
            this.listViewAdapter.notifyDataSetChanged();
        } else {
            getMessagesController().deleteMessages(new ArrayList<>(this.selectedIds), null, null, 0L, zArr[0], false);
        }
        hideActionMode(false);
    }

    private void deleteAllMessages(final boolean z) {
        TLRPC$TL_messages_deletePhoneCallHistory tLRPC$TL_messages_deletePhoneCallHistory = new TLRPC$TL_messages_deletePhoneCallHistory();
        tLRPC$TL_messages_deletePhoneCallHistory.revoke = z;
        getConnectionsManager().sendRequest(tLRPC$TL_messages_deletePhoneCallHistory, new RequestDelegate() { // from class: org.telegram.ui.CallLogActivity$$ExternalSyntheticLambda6
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                CallLogActivity.this.lambda$deleteAllMessages$6(z, tLObject, tLRPC$TL_error);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$deleteAllMessages$6(boolean z, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLObject != null) {
            TLRPC$TL_messages_affectedFoundMessages tLRPC$TL_messages_affectedFoundMessages = (TLRPC$TL_messages_affectedFoundMessages) tLObject;
            TLRPC$TL_updateDeleteMessages tLRPC$TL_updateDeleteMessages = new TLRPC$TL_updateDeleteMessages();
            tLRPC$TL_updateDeleteMessages.messages = tLRPC$TL_messages_affectedFoundMessages.messages;
            tLRPC$TL_updateDeleteMessages.pts = tLRPC$TL_messages_affectedFoundMessages.pts;
            tLRPC$TL_updateDeleteMessages.pts_count = tLRPC$TL_messages_affectedFoundMessages.pts_count;
            TLRPC$TL_updates tLRPC$TL_updates = new TLRPC$TL_updates();
            tLRPC$TL_updates.updates.add(tLRPC$TL_updateDeleteMessages);
            getMessagesController().processUpdates(tLRPC$TL_updates, false);
            if (tLRPC$TL_messages_affectedFoundMessages.offset == 0) {
                return;
            }
            deleteAllMessages(z);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
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

    /* JADX INFO: Access modifiers changed from: private */
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
        if (this.actionBar.actionModeIsExist(null)) {
            return;
        }
        ActionBarMenu createActionMode = this.actionBar.createActionMode();
        NumberTextView numberTextView = new NumberTextView(createActionMode.getContext());
        this.selectedDialogsCountTextView = numberTextView;
        numberTextView.setTextSize(18);
        this.selectedDialogsCountTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.selectedDialogsCountTextView.setTextColor(Theme.getColor("actionBarActionModeDefaultIcon"));
        createActionMode.addView(this.selectedDialogsCountTextView, LayoutHelper.createLinear(0, -1, 1.0f, 72, 0, 0, 0));
        this.selectedDialogsCountTextView.setOnTouchListener(CallLogActivity$$ExternalSyntheticLambda3.INSTANCE);
        this.actionModeViews.add(createActionMode.addItemWithWidth(2, R.drawable.msg_delete, AndroidUtilities.dp(54.0f), LocaleController.getString("Delete", R.string.Delete)));
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
        if (this.actionBar.isActionModeShowed()) {
            if (this.selectedIds.isEmpty()) {
                hideActionMode(true);
                return;
            }
            z = true;
        } else {
            createActionMode();
            this.actionBar.showActionMode();
            AnimatorSet animatorSet = new AnimatorSet();
            ArrayList arrayList = new ArrayList();
            for (int i = 0; i < this.actionModeViews.size(); i++) {
                View view = this.actionModeViews.get(i);
                view.setPivotY(ActionBar.getCurrentActionBarHeight() / 2);
                AndroidUtilities.clearDrawableAnimation(view);
                arrayList.add(ObjectAnimator.ofFloat(view, View.SCALE_Y, 0.1f, 1.0f));
            }
            animatorSet.playTogether(arrayList);
            animatorSet.setDuration(200L);
            animatorSet.start();
        }
        this.selectedDialogsCountTextView.setNumber(this.selectedIds.size(), z);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void hideFloatingButton(boolean z) {
        if (this.floatingHidden == z) {
            return;
        }
        this.floatingHidden = z;
        ImageView imageView = this.floatingButton;
        float[] fArr = new float[1];
        fArr[0] = z ? AndroidUtilities.dp(100.0f) : 0.0f;
        ObjectAnimator duration = ObjectAnimator.ofFloat(imageView, "translationY", fArr).setDuration(300L);
        duration.setInterpolator(this.floatingInterpolator);
        this.floatingButton.setClickable(!z);
        duration.start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void getCalls(int i, int i2) {
        if (this.loading) {
            return;
        }
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
        getConnectionsManager().bindRequestToGuid(getConnectionsManager().sendRequest(tLRPC$TL_messages_search, new RequestDelegate() { // from class: org.telegram.ui.CallLogActivity$$ExternalSyntheticLambda5
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                CallLogActivity.this.lambda$getCalls$9(tLObject, tLRPC$TL_error);
            }
        }, 2), this.classGuid);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$getCalls$9(final TLObject tLObject, final TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.CallLogActivity$$ExternalSyntheticLambda4
            @Override // java.lang.Runnable
            public final void run() {
                CallLogActivity.this.lambda$getCalls$8(tLRPC$TL_error, tLObject);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$getCalls$8(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject) {
        CallLogRow callLogRow;
        int i = 0;
        int max = Math.max(this.listViewAdapter.callsStartRow, 0) + this.calls.size();
        if (tLRPC$TL_error == null) {
            LongSparseArray longSparseArray = new LongSparseArray();
            TLRPC$messages_Messages tLRPC$messages_Messages = (TLRPC$messages_Messages) tLObject;
            this.endReached = tLRPC$messages_Messages.messages.isEmpty();
            for (int i2 = 0; i2 < tLRPC$messages_Messages.users.size(); i2++) {
                TLRPC$User tLRPC$User = tLRPC$messages_Messages.users.get(i2);
                longSparseArray.put(tLRPC$User.id, tLRPC$User);
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
                    long fromChatId = MessageObject.getFromChatId(tLRPC$Message);
                    if (fromChatId == getUserConfig().getClientUserId()) {
                        fromChatId = tLRPC$Message.peer_id.user_id;
                    }
                    if (callLogRow == null || callLogRow.user.id != fromChatId || callLogRow.type != i4) {
                        if (callLogRow != null && !this.calls.contains(callLogRow)) {
                            this.calls.add(callLogRow);
                        }
                        callLogRow = new CallLogRow();
                        callLogRow.calls = new ArrayList<>();
                        callLogRow.user = (TLRPC$User) longSparseArray.get(fromChatId);
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

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onResume() {
        super.onResume();
        ListAdapter listAdapter = this.listViewAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
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
                VoIPHelper.permissionDenied(getParentActivity(), null, i);
            } else if (i == 103) {
                VoIPHelper.startCall(this.lastCallChat, null, null, false, getParentActivity(), this, getAccountInstance());
            } else {
                if (this.lastCallUser != null) {
                    tLRPC$UserFull = getMessagesController().getUserFull(this.lastCallUser.id);
                }
                VoIPHelper.startCall(this.lastCallUser, i == 102, i == 102 || (tLRPC$UserFull != null && tLRPC$UserFull.video_calls_available), getParentActivity(), null, getAccountInstance());
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public class ListAdapter extends RecyclerListView.SelectionAdapter {
        private int activeEndRow;
        private int activeHeaderRow;
        private int activeStartRow;
        private int callsEndRow;
        private int callsHeaderRow;
        private int callsStartRow;
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
                if (CallLogActivity.this.endReached) {
                    return;
                }
                int i6 = this.rowsCount;
                this.rowsCount = i6 + 1;
                this.loadingCallsRow = i6;
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void notifyDataSetChanged() {
            updateRows();
            super.notifyDataSetChanged();
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void notifyItemChanged(int i) {
            updateRows();
            super.notifyItemChanged(i);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void notifyItemRangeChanged(int i, int i2) {
            updateRows();
            super.notifyItemRangeChanged(i, i2);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void notifyItemRangeChanged(int i, int i2, Object obj) {
            updateRows();
            super.notifyItemRangeChanged(i, i2, obj);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void notifyItemInserted(int i) {
            updateRows();
            super.notifyItemInserted(i);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void notifyItemMoved(int i, int i2) {
            updateRows();
            super.notifyItemMoved(i, i2);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void notifyItemRangeInserted(int i, int i2) {
            updateRows();
            super.notifyItemRangeInserted(i, i2);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void notifyItemRemoved(int i) {
            updateRows();
            super.notifyItemRemoved(i);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void notifyItemRangeRemoved(int i, int i2) {
            updateRows();
            super.notifyItemRangeRemoved(i, i2);
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            int itemViewType = viewHolder.getItemViewType();
            return itemViewType == 0 || itemViewType == 4;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            return this.rowsCount;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        /* renamed from: onCreateViewHolder */
        public RecyclerView.ViewHolder mo1817onCreateViewHolder(ViewGroup viewGroup, int i) {
            View callCell;
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
                    callCell = new TextInfoPrivacyCell(this.mContext);
                    callCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, R.drawable.greydivider_bottom, "windowBackgroundGrayShadow"));
                } else if (i == 3) {
                    HeaderCell headerCell2 = new HeaderCell(this.mContext, "windowBackgroundWhiteBlueHeader", 21, 15, 2, false, CallLogActivity.this.getResourceProvider());
                    headerCell2.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    headerCell = headerCell2;
                } else if (i == 4) {
                    callCell = new GroupCallCell(this.mContext);
                } else {
                    callCell = new ShadowSectionCell(this.mContext);
                }
                callCell = headerCell;
            } else {
                callCell = new CallCell(this.mContext);
            }
            return new RecyclerListView.Holder(callCell);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onViewAttachedToWindow(RecyclerView.ViewHolder viewHolder) {
            if (viewHolder.itemView instanceof CallCell) {
                ((CallCell) viewHolder.itemView).setChecked(CallLogActivity.this.isSelected(((CallLogRow) CallLogActivity.this.calls.get(viewHolder.getAdapterPosition() - this.callsStartRow)).calls), false);
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            SpannableString spannableString;
            String lowerCase;
            int itemViewType = viewHolder.getItemViewType();
            boolean z = false;
            if (itemViewType == 0) {
                int i2 = i - this.callsStartRow;
                CallLogRow callLogRow = (CallLogRow) CallLogActivity.this.calls.get(i2);
                CallCell callCell = (CallCell) viewHolder.itemView;
                callCell.imageView.setImageResource(callLogRow.video ? R.drawable.profile_video : R.drawable.profile_phone);
                TLRPC$Message tLRPC$Message = callLogRow.calls.get(0);
                String str = LocaleController.isRTL ? "\u202b" : "";
                if (callLogRow.calls.size() == 1) {
                    spannableString = new SpannableString(str + "  " + LocaleController.formatDateCallLog(tLRPC$Message.date));
                } else {
                    spannableString = new SpannableString(String.format(str + "  (%d) %s", Integer.valueOf(callLogRow.calls.size()), LocaleController.formatDateCallLog(tLRPC$Message.date)));
                }
                SpannableString spannableString2 = spannableString;
                int i3 = callLogRow.type;
                if (i3 == 0) {
                    spannableString2.setSpan(CallLogActivity.this.iconOut, str.length(), str.length() + 1, 0);
                } else if (i3 == 1) {
                    spannableString2.setSpan(CallLogActivity.this.iconIn, str.length(), str.length() + 1, 0);
                } else if (i3 == 2) {
                    spannableString2.setSpan(CallLogActivity.this.iconMissed, str.length(), str.length() + 1, 0);
                }
                callCell.profileSearchCell.setData(callLogRow.user, null, null, spannableString2, false, false);
                ProfileSearchCell profileSearchCell = callCell.profileSearchCell;
                if (i2 != CallLogActivity.this.calls.size() - 1 || !CallLogActivity.this.endReached) {
                    z = true;
                }
                profileSearchCell.useSeparator = z;
                callCell.imageView.setTag(callLogRow);
            } else if (itemViewType == 3) {
                HeaderCell headerCell = (HeaderCell) viewHolder.itemView;
                if (i == this.activeHeaderRow) {
                    headerCell.setText(LocaleController.getString("VoipChatActiveChats", R.string.VoipChatActiveChats));
                } else if (i != this.callsHeaderRow) {
                } else {
                    headerCell.setText(LocaleController.getString("VoipChatRecentCalls", R.string.VoipChatRecentCalls));
                }
            } else if (itemViewType != 4) {
            } else {
                int i4 = i - this.activeStartRow;
                TLRPC$Chat chat = CallLogActivity.this.getMessagesController().getChat((Long) CallLogActivity.this.activeGroupCalls.get(i4));
                GroupCallCell groupCallCell = (GroupCallCell) viewHolder.itemView;
                groupCallCell.setChat(chat);
                groupCallCell.button.setTag(Long.valueOf(chat.id));
                if (ChatObject.isChannel(chat) && !chat.megagroup) {
                    if (!ChatObject.isPublic(chat)) {
                        lowerCase = LocaleController.getString("ChannelPrivate", R.string.ChannelPrivate).toLowerCase();
                    } else {
                        lowerCase = LocaleController.getString("ChannelPublic", R.string.ChannelPublic).toLowerCase();
                    }
                } else if (chat.has_geo) {
                    lowerCase = LocaleController.getString("MegaLocation", R.string.MegaLocation);
                } else if (!ChatObject.isPublic(chat)) {
                    lowerCase = LocaleController.getString("MegaPrivate", R.string.MegaPrivate).toLowerCase();
                } else {
                    lowerCase = LocaleController.getString("MegaPublic", R.string.MegaPublic).toLowerCase();
                }
                String str2 = lowerCase;
                ProfileSearchCell profileSearchCell2 = groupCallCell.profileSearchCell;
                if (i4 != CallLogActivity.this.activeGroupCalls.size() - 1 && !CallLogActivity.this.endReached) {
                    z = true;
                }
                profileSearchCell2.useSeparator = z;
                groupCallCell.profileSearchCell.setData(chat, null, null, str2, false, false);
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
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

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public static class CallLogRow {
        public ArrayList<TLRPC$Message> calls;
        public int type;
        public TLRPC$User user;
        public boolean video;

        private CallLogRow() {
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onTransitionAnimationStart(boolean z, boolean z2) {
        super.onTransitionAnimationStart(z, z2);
        if (z) {
            this.openTransitionStarted = true;
        }
    }

    private void showItemsAnimated(final int i) {
        if (this.isPaused || !this.openTransitionStarted) {
            return;
        }
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
        this.listView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() { // from class: org.telegram.ui.CallLogActivity.4
            @Override // android.view.ViewTreeObserver.OnPreDrawListener
            public boolean onPreDraw() {
                CallLogActivity.this.listView.getViewTreeObserver().removeOnPreDrawListener(this);
                int childCount = CallLogActivity.this.listView.getChildCount();
                AnimatorSet animatorSet = new AnimatorSet();
                for (int i3 = 0; i3 < childCount; i3++) {
                    View childAt2 = CallLogActivity.this.listView.getChildAt(i3);
                    RecyclerView.ViewHolder childViewHolder = CallLogActivity.this.listView.getChildViewHolder(childAt2);
                    if (childAt2 != view && CallLogActivity.this.listView.getChildAdapterPosition(childAt2) >= i && !(childAt2 instanceof GroupCallCell) && (!(childAt2 instanceof HeaderCell) || childViewHolder.getAdapterPosition() != CallLogActivity.this.listViewAdapter.activeHeaderRow)) {
                        childAt2.setAlpha(0.0f);
                        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(childAt2, View.ALPHA, 0.0f, 1.0f);
                        ofFloat.setStartDelay((int) ((Math.min(CallLogActivity.this.listView.getMeasuredHeight(), Math.max(0, childAt2.getTop())) / CallLogActivity.this.listView.getMeasuredHeight()) * 100.0f));
                        ofFloat.setDuration(200L);
                        animatorSet.playTogether(ofFloat);
                    }
                }
                View view2 = view;
                if (view2 != null && view2.getParent() == null) {
                    CallLogActivity.this.listView.addView(view);
                    final RecyclerView.LayoutManager layoutManager = CallLogActivity.this.listView.getLayoutManager();
                    if (layoutManager != null) {
                        layoutManager.ignoreView(view);
                        View view3 = view;
                        ObjectAnimator ofFloat2 = ObjectAnimator.ofFloat(view3, View.ALPHA, view3.getAlpha(), 0.0f);
                        ofFloat2.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.CallLogActivity.4.1
                            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
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

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate = new ThemeDescription.ThemeDescriptionDelegate() { // from class: org.telegram.ui.CallLogActivity$$ExternalSyntheticLambda7
            @Override // org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate
            public final void didSetColor() {
                CallLogActivity.this.lambda$getThemeDescriptions$10();
            }

            @Override // org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate
            public /* synthetic */ void onAnimationProgress(float f) {
                ThemeDescription.ThemeDescriptionDelegate.CC.$default$onAnimationProgress(this, f);
            }
        };
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{LocationCell.class, CallCell.class, HeaderCell.class, GroupCallCell.class}, null, null, null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundGray"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, "divider"));
        arrayList.add(new ThemeDescription(this.emptyView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{EmptyTextProgressView.class}, new String[]{"emptyTextView1"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(this.emptyView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{EmptyTextProgressView.class}, new String[]{"emptyTextView2"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "emptyListPlaceholder"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{LoadingCell.class}, new String[]{"progressBar"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "progressCircle"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, "windowBackgroundGrayShadow"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText4"));
        arrayList.add(new ThemeDescription(this.floatingButton, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, "chats_actionIcon"));
        arrayList.add(new ThemeDescription(this.floatingButton, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "chats_actionBackground"));
        arrayList.add(new ThemeDescription(this.floatingButton, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, "chats_actionPressedBackground"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{CallCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "featuredStickers_addButton"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{CallCell.class}, null, new Drawable[]{Theme.dialogs_verifiedCheckDrawable}, null, "chats_verifiedCheck"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{CallCell.class}, null, new Drawable[]{Theme.dialogs_verifiedDrawable}, null, "chats_verifiedBackground"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{CallCell.class}, Theme.dialogs_offlinePaint, null, null, "windowBackgroundWhiteGrayText3"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{CallCell.class}, Theme.dialogs_onlinePaint, null, null, "windowBackgroundWhiteBlueText3"));
        TextPaint[] textPaintArr = Theme.dialogs_namePaint;
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{CallCell.class}, (String[]) null, new Paint[]{textPaintArr[0], textPaintArr[1], Theme.dialogs_searchNamePaint}, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_name"));
        TextPaint[] textPaintArr2 = Theme.dialogs_nameEncryptedPaint;
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{CallCell.class}, (String[]) null, new Paint[]{textPaintArr2[0], textPaintArr2[1], Theme.dialogs_searchNameEncryptedPaint}, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_secretName"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{CallCell.class}, null, Theme.avatarDrawables, null, "avatar_text"));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, "avatar_backgroundRed"));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, "avatar_backgroundOrange"));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, "avatar_backgroundViolet"));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, "avatar_backgroundGreen"));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, "avatar_backgroundCyan"));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, "avatar_backgroundBlue"));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, "avatar_backgroundPink"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, null, new Drawable[]{this.greenDrawable, this.greenDrawable2, Theme.calllog_msgCallUpRedDrawable, Theme.calllog_msgCallDownRedDrawable}, null, "calls_callReceivedGreenIcon"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, null, new Drawable[]{this.redDrawable, Theme.calllog_msgCallUpGreenDrawable, Theme.calllog_msgCallDownGreenDrawable}, null, "calls_callReceivedRedIcon"));
        arrayList.add(new ThemeDescription(this.flickerLoadingView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, "windowBackgroundGrayShadow"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueHeader"));
        return arrayList;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$getThemeDescriptions$10() {
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
