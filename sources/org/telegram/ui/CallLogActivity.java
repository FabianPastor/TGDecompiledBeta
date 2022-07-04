package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
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
import android.text.TextUtils;
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
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BackDrawable;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
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

public class CallLogActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private static final int TYPE_IN = 1;
    private static final int TYPE_MISSED = 2;
    private static final int TYPE_OUT = 0;
    private static final int delete = 2;
    private static final int delete_all_calls = 1;
    private ArrayList<View> actionModeViews = new ArrayList<>();
    /* access modifiers changed from: private */
    public ArrayList<Long> activeGroupCalls;
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
    public TLRPC.Chat lastCallChat;
    /* access modifiers changed from: private */
    public TLRPC.User lastCallUser;
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
    public Long waitingForCallChatId;

    private static class EmptyTextProgressView extends FrameLayout {
        private TextView emptyTextView1;
        private TextView emptyTextView2;
        private RLottieImageView imageView;
        private View progressView;

        public EmptyTextProgressView(Context context) {
            this(context, (View) null);
        }

        public EmptyTextProgressView(Context context, View progressView2) {
            super(context);
            addView(progressView2, LayoutHelper.createFrame(-1, -1.0f));
            this.progressView = progressView2;
            RLottieImageView rLottieImageView = new RLottieImageView(context);
            this.imageView = rLottieImageView;
            rLottieImageView.setAnimation(NUM, 120, 120);
            this.imageView.setAutoRepeat(false);
            addView(this.imageView, LayoutHelper.createFrame(140, 140.0f, 17, 52.0f, 4.0f, 52.0f, 60.0f));
            this.imageView.setOnClickListener(new CallLogActivity$EmptyTextProgressView$$ExternalSyntheticLambda0(this));
            TextView textView = new TextView(context);
            this.emptyTextView1 = textView;
            textView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.emptyTextView1.setText(LocaleController.getString("NoRecentCalls", NUM));
            this.emptyTextView1.setTextSize(1, 20.0f);
            this.emptyTextView1.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.emptyTextView1.setGravity(17);
            addView(this.emptyTextView1, LayoutHelper.createFrame(-1, -2.0f, 17, 17.0f, 40.0f, 17.0f, 0.0f));
            this.emptyTextView2 = new TextView(context);
            String help = LocaleController.getString("NoRecentCallsInfo", NUM);
            if (AndroidUtilities.isTablet() && !AndroidUtilities.isSmallTablet()) {
                help = help.replace(10, ' ');
            }
            this.emptyTextView2.setText(help);
            this.emptyTextView2.setTextColor(Theme.getColor("emptyListPlaceholder"));
            this.emptyTextView2.setTextSize(1, 14.0f);
            this.emptyTextView2.setGravity(17);
            this.emptyTextView2.setLineSpacing((float) AndroidUtilities.dp(2.0f), 1.0f);
            addView(this.emptyTextView2, LayoutHelper.createFrame(-1, -2.0f, 17, 17.0f, 80.0f, 17.0f, 0.0f));
            progressView2.setAlpha(0.0f);
            this.imageView.setAlpha(0.0f);
            this.emptyTextView1.setAlpha(0.0f);
            this.emptyTextView2.setAlpha(0.0f);
            setOnTouchListener(CallLogActivity$EmptyTextProgressView$$ExternalSyntheticLambda1.INSTANCE);
        }

        /* renamed from: lambda$new$0$org-telegram-ui-CallLogActivity$EmptyTextProgressView  reason: not valid java name */
        public /* synthetic */ void m2754x52cvar_(View v) {
            if (!this.imageView.isPlaying()) {
                this.imageView.setProgress(0.0f);
                this.imageView.playAnimation();
            }
        }

        static /* synthetic */ boolean lambda$new$1(View v, MotionEvent event) {
            return true;
        }

        public void showProgress() {
            this.imageView.animate().alpha(0.0f).setDuration(150).start();
            this.emptyTextView1.animate().alpha(0.0f).setDuration(150).start();
            this.emptyTextView2.animate().alpha(0.0f).setDuration(150).start();
            this.progressView.animate().alpha(1.0f).setDuration(150).start();
        }

        public void showTextView() {
            this.imageView.animate().alpha(1.0f).setDuration(150).start();
            this.emptyTextView1.animate().alpha(1.0f).setDuration(150).start();
            this.emptyTextView2.animate().alpha(1.0f).setDuration(150).start();
            this.progressView.animate().alpha(0.0f).setDuration(150).start();
            this.imageView.playAnimation();
        }

        public boolean hasOverlappingRendering() {
            return false;
        }
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        Long l;
        ListAdapter listAdapter;
        int i = id;
        int i2 = 1;
        int i3 = 0;
        if (i == NotificationCenter.didReceiveNewMessages) {
            if (this.firstLoaded && !args[2].booleanValue()) {
                Iterator<MessageObject> it = args[1].iterator();
                while (it.hasNext()) {
                    MessageObject msg = it.next();
                    if (msg.messageOwner.action instanceof TLRPC.TL_messageActionPhoneCall) {
                        long fromId = msg.getFromChatId();
                        long userID = fromId == getUserConfig().getClientUserId() ? msg.messageOwner.peer_id.user_id : fromId;
                        int callType = fromId == getUserConfig().getClientUserId() ? 0 : 1;
                        TLRPC.PhoneCallDiscardReason reason = msg.messageOwner.action.reason;
                        if (callType == i2 && ((reason instanceof TLRPC.TL_phoneCallDiscardReasonMissed) || (reason instanceof TLRPC.TL_phoneCallDiscardReasonBusy))) {
                            callType = 2;
                        }
                        if (this.calls.size() > 0) {
                            CallLogRow topRow = this.calls.get(0);
                            long j = fromId;
                            if (topRow.user.id == userID && topRow.type == callType) {
                                topRow.calls.add(0, msg.messageOwner);
                                this.listViewAdapter.notifyItemChanged(0);
                                i2 = 1;
                            }
                        }
                        CallLogRow row = new CallLogRow();
                        row.calls = new ArrayList<>();
                        row.calls.add(msg.messageOwner);
                        row.user = getMessagesController().getUser(Long.valueOf(userID));
                        row.type = callType;
                        row.video = msg.isVideoCall();
                        this.calls.add(0, row);
                        this.listViewAdapter.notifyItemInserted(0);
                    }
                    i2 = 1;
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
            if (this.firstLoaded && !args[2].booleanValue()) {
                boolean didChange = false;
                ArrayList<Integer> ids = args[0];
                Iterator<CallLogRow> itrtr = this.calls.iterator();
                while (itrtr.hasNext()) {
                    CallLogRow row2 = itrtr.next();
                    Iterator<TLRPC.Message> msgs = row2.calls.iterator();
                    while (msgs.hasNext()) {
                        if (ids.contains(Integer.valueOf(msgs.next().id))) {
                            didChange = true;
                            msgs.remove();
                        }
                    }
                    if (row2.calls.size() == 0) {
                        itrtr.remove();
                    }
                }
                if (didChange && (listAdapter = this.listViewAdapter) != null) {
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
            if (this.waitingForCallChatId != null && args[0].id == this.waitingForCallChatId.longValue() && getMessagesController().getGroupCall(this.waitingForCallChatId.longValue(), true) != null) {
                VoIPHelper.startCall(this.lastCallChat, (TLRPC.InputPeer) null, (String) null, false, getParentActivity(), this, getAccountInstance());
                this.waitingForCallChatId = null;
            }
        } else if (i == NotificationCenter.groupCallUpdated && (l = this.waitingForCallChatId) != null && l.equals(args[0])) {
            VoIPHelper.startCall(this.lastCallChat, (TLRPC.InputPeer) null, (String) null, false, getParentActivity(), this, getAccountInstance());
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
            this.imageView.setOnClickListener(new CallLogActivity$CallCell$$ExternalSyntheticLambda0(this));
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

        /* renamed from: lambda$new$0$org-telegram-ui-CallLogActivity$CallCell  reason: not valid java name */
        public /* synthetic */ void m2753lambda$new$0$orgtelegramuiCallLogActivity$CallCell(View v) {
            CallLogRow row = (CallLogRow) v.getTag();
            TLRPC.UserFull userFull = CallLogActivity.this.getMessagesController().getUserFull(row.user.id);
            VoIPHelper.startCall(CallLogActivity.this.lastCallUser = row.user, row.video, row.video || (userFull != null && userFull.video_calls_available), CallLogActivity.this.getParentActivity(), (TLRPC.UserFull) null, CallLogActivity.this.getAccountInstance());
        }

        public void setChecked(boolean checked, boolean animated) {
            CheckBox2 checkBox2 = this.checkBox;
            if (checkBox2 != null) {
                checkBox2.setChecked(checked, animated);
            }
        }
    }

    private class GroupCallCell extends FrameLayout {
        /* access modifiers changed from: private */
        public ProgressButton button;
        /* access modifiers changed from: private */
        public TLRPC.Chat currentChat;
        /* access modifiers changed from: private */
        public ProfileSearchCell profileSearchCell;

        public GroupCallCell(Context context) {
            super(context);
            setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            String text = LocaleController.getString("VoipChatJoin", NUM);
            ProgressButton progressButton = new ProgressButton(context);
            this.button = progressButton;
            int width = (int) Math.ceil((double) progressButton.getPaint().measureText(text));
            ProfileSearchCell profileSearchCell2 = new ProfileSearchCell(context);
            this.profileSearchCell = profileSearchCell2;
            profileSearchCell2.setPadding(LocaleController.isRTL ? AndroidUtilities.dp(44.0f) + width : 0, 0, LocaleController.isRTL ? 0 : AndroidUtilities.dp(44.0f) + width, 0);
            this.profileSearchCell.setSublabelOffset(0, -AndroidUtilities.dp(1.0f));
            addView(this.profileSearchCell, LayoutHelper.createFrame(-1, -1.0f));
            this.button.setText(text);
            this.button.setTextSize(1, 14.0f);
            this.button.setTextColor(Theme.getColor("featuredStickers_buttonText"));
            this.button.setProgressColor(Theme.getColor("featuredStickers_buttonProgress"));
            this.button.setBackgroundRoundRect(Theme.getColor("featuredStickers_addButton"), Theme.getColor("featuredStickers_addButtonPressed"), 16.0f);
            this.button.setPadding(AndroidUtilities.dp(14.0f), 0, AndroidUtilities.dp(14.0f), 0);
            addView(this.button, LayoutHelper.createFrameRelatively(-2.0f, 28.0f, 8388661, 0.0f, 16.0f, 14.0f, 0.0f));
            this.button.setOnClickListener(new CallLogActivity$GroupCallCell$$ExternalSyntheticLambda0(this));
        }

        /* renamed from: lambda$new$0$org-telegram-ui-CallLogActivity$GroupCallCell  reason: not valid java name */
        public /* synthetic */ void m2755lambda$new$0$orgtelegramuiCallLogActivity$GroupCallCell(View v) {
            Long tag = (Long) v.getTag();
            ChatObject.Call call = CallLogActivity.this.getMessagesController().getGroupCall(tag.longValue(), false);
            CallLogActivity callLogActivity = CallLogActivity.this;
            TLRPC.Chat unused = callLogActivity.lastCallChat = callLogActivity.getMessagesController().getChat(tag);
            if (call != null) {
                TLRPC.Chat access$200 = CallLogActivity.this.lastCallChat;
                Activity parentActivity = CallLogActivity.this.getParentActivity();
                CallLogActivity callLogActivity2 = CallLogActivity.this;
                VoIPHelper.startCall(access$200, (TLRPC.InputPeer) null, (String) null, false, parentActivity, callLogActivity2, callLogActivity2.getAccountInstance());
                return;
            }
            Long unused2 = CallLogActivity.this.waitingForCallChatId = tag;
            CallLogActivity.this.getMessagesController().loadFullChat(tag.longValue(), 0, true);
        }

        public void setChat(TLRPC.Chat chat) {
            this.currentChat = chat;
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
        FrameLayout frameLayout;
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
            public void onItemClick(int id) {
                if (id == -1) {
                    if (CallLogActivity.this.actionBar.isActionModeShowed()) {
                        CallLogActivity.this.hideActionMode(true);
                    } else {
                        CallLogActivity.this.finishFragment();
                    }
                } else if (id == 1) {
                    CallLogActivity.this.showDeleteAlert(true);
                } else if (id == 2) {
                    CallLogActivity.this.showDeleteAlert(false);
                }
            }
        });
        ActionBarMenuItem addItem = this.actionBar.createMenu().addItem(10, NUM);
        this.otherItem = addItem;
        addItem.setContentDescription(LocaleController.getString("AccDescrMoreOptions", NUM));
        this.otherItem.addSubItem(1, NUM, (CharSequence) LocaleController.getString("DeleteAllCalls", NUM));
        this.fragmentView = new FrameLayout(context2);
        this.fragmentView.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
        FrameLayout frameLayout2 = (FrameLayout) this.fragmentView;
        FlickerLoadingView flickerLoadingView2 = new FlickerLoadingView(context2);
        this.flickerLoadingView = flickerLoadingView2;
        flickerLoadingView2.setViewType(8);
        this.flickerLoadingView.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        this.flickerLoadingView.showDate(false);
        EmptyTextProgressView emptyTextProgressView = new EmptyTextProgressView(context2, this.flickerLoadingView);
        this.emptyView = emptyTextProgressView;
        frameLayout2.addView(emptyTextProgressView, LayoutHelper.createFrame(-1, -1.0f));
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
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new CallLogActivity$$ExternalSyntheticLambda9(this));
        this.listView.setOnItemLongClickListener((RecyclerListView.OnItemLongClickListener) new CallLogActivity$$ExternalSyntheticLambda10(this));
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                boolean goingDown;
                int firstVisibleItem = CallLogActivity.this.layoutManager.findFirstVisibleItemPosition();
                boolean z = false;
                int visibleItemCount = firstVisibleItem == -1 ? 0 : Math.abs(CallLogActivity.this.layoutManager.findLastVisibleItemPosition() - firstVisibleItem) + 1;
                if (visibleItemCount > 0) {
                    int totalItemCount = CallLogActivity.this.listViewAdapter.getItemCount();
                    if (!CallLogActivity.this.endReached && !CallLogActivity.this.loading && !CallLogActivity.this.calls.isEmpty() && firstVisibleItem + visibleItemCount >= totalItemCount - 5) {
                        AndroidUtilities.runOnUIThread(new CallLogActivity$2$$ExternalSyntheticLambda0(this, (CallLogRow) CallLogActivity.this.calls.get(CallLogActivity.this.calls.size() - 1)));
                    }
                }
                if (CallLogActivity.this.floatingButton.getVisibility() != 8) {
                    View topChild = recyclerView.getChildAt(0);
                    int firstViewTop = 0;
                    if (topChild != null) {
                        firstViewTop = topChild.getTop();
                    }
                    boolean changed = true;
                    if (CallLogActivity.this.prevPosition == firstVisibleItem) {
                        int topDelta = CallLogActivity.this.prevTop - firstViewTop;
                        goingDown = firstViewTop < CallLogActivity.this.prevTop;
                        if (Math.abs(topDelta) > 1) {
                            z = true;
                        }
                        changed = z;
                    } else {
                        if (firstVisibleItem > CallLogActivity.this.prevPosition) {
                            z = true;
                        }
                        goingDown = z;
                    }
                    if (changed && CallLogActivity.this.scrollUpdated) {
                        CallLogActivity.this.hideFloatingButton(goingDown);
                    }
                    int unused = CallLogActivity.this.prevPosition = firstVisibleItem;
                    int unused2 = CallLogActivity.this.prevTop = firstViewTop;
                    boolean unused3 = CallLogActivity.this.scrollUpdated = true;
                }
            }

            /* renamed from: lambda$onScrolled$0$org-telegram-ui-CallLogActivity$2  reason: not valid java name */
            public /* synthetic */ void m2752lambda$onScrolled$0$orgtelegramuiCallLogActivity$2(CallLogRow row) {
                CallLogActivity.this.getCalls(row.calls.get(row.calls.size() - 1).id, 100);
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
        Drawable drawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56.0f), Theme.getColor("chats_actionBackground"), Theme.getColor("chats_actionPressedBackground"));
        if (Build.VERSION.SDK_INT < 21) {
            Drawable shadowDrawable = context.getResources().getDrawable(NUM).mutate();
            shadowDrawable.setColorFilter(new PorterDuffColorFilter(-16777216, PorterDuff.Mode.MULTIPLY));
            CombinedDrawable combinedDrawable = new CombinedDrawable(shadowDrawable, drawable, 0, 0);
            combinedDrawable.setIconSize(AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
            drawable = combinedDrawable;
        }
        this.floatingButton.setBackgroundDrawable(drawable);
        this.floatingButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chats_actionIcon"), PorterDuff.Mode.MULTIPLY));
        this.floatingButton.setImageResource(NUM);
        this.floatingButton.setContentDescription(LocaleController.getString("Call", NUM));
        if (Build.VERSION.SDK_INT >= 21) {
            StateListAnimator animator = new StateListAnimator();
            frameLayout = frameLayout2;
            animator.addState(new int[]{16842919}, ObjectAnimator.ofFloat(this.floatingButton, "translationZ", new float[]{(float) AndroidUtilities.dp(2.0f), (float) AndroidUtilities.dp(4.0f)}).setDuration(200));
            animator.addState(new int[0], ObjectAnimator.ofFloat(this.floatingButton, "translationZ", new float[]{(float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(2.0f)}).setDuration(200));
            this.floatingButton.setStateListAnimator(animator);
            this.floatingButton.setOutlineProvider(new ViewOutlineProvider() {
                public void getOutline(View view, Outline outline) {
                    outline.setOval(0, 0, AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
                }
            });
        } else {
            frameLayout = frameLayout2;
        }
        frameLayout.addView(this.floatingButton, LayoutHelper.createFrame(Build.VERSION.SDK_INT >= 21 ? 56 : 60, Build.VERSION.SDK_INT >= 21 ? 56.0f : 60.0f, (LocaleController.isRTL ? 3 : 5) | 80, LocaleController.isRTL ? 14.0f : 0.0f, 0.0f, LocaleController.isRTL ? 0.0f : 14.0f, 14.0f));
        this.floatingButton.setOnClickListener(new CallLogActivity$$ExternalSyntheticLambda2(this));
        return this.fragmentView;
    }

    /* renamed from: lambda$createView$0$org-telegram-ui-CallLogActivity  reason: not valid java name */
    public /* synthetic */ void m2743lambda$createView$0$orgtelegramuiCallLogActivity(View view, int position) {
        if (view instanceof CallCell) {
            CallLogRow row = this.calls.get(position - this.listViewAdapter.callsStartRow);
            if (this.actionBar.isActionModeShowed()) {
                addOrRemoveSelectedDialog(row.calls, (CallCell) view);
                return;
            }
            Bundle args = new Bundle();
            args.putLong("user_id", row.user.id);
            args.putInt("message_id", row.calls.get(0).id);
            getNotificationCenter().postNotificationName(NotificationCenter.closeChats, new Object[0]);
            presentFragment(new ChatActivity(args), true);
        } else if (view instanceof GroupCallCell) {
            Bundle args2 = new Bundle();
            args2.putLong("chat_id", ((GroupCallCell) view).currentChat.id);
            getNotificationCenter().postNotificationName(NotificationCenter.closeChats, new Object[0]);
            presentFragment(new ChatActivity(args2), true);
        }
    }

    /* renamed from: lambda$createView$1$org-telegram-ui-CallLogActivity  reason: not valid java name */
    public /* synthetic */ boolean m2744lambda$createView$1$orgtelegramuiCallLogActivity(View view, int position) {
        if (!(view instanceof CallCell)) {
            return false;
        }
        addOrRemoveSelectedDialog(this.calls.get(position - this.listViewAdapter.callsStartRow).calls, (CallCell) view);
        return true;
    }

    /* renamed from: lambda$createView$3$org-telegram-ui-CallLogActivity  reason: not valid java name */
    public /* synthetic */ void m2746lambda$createView$3$orgtelegramuiCallLogActivity(View v) {
        Bundle args = new Bundle();
        args.putBoolean("destroyAfterSelect", true);
        args.putBoolean("returnAsResult", true);
        args.putBoolean("onlyUsers", true);
        args.putBoolean("allowSelf", false);
        ContactsActivity contactsFragment = new ContactsActivity(args);
        contactsFragment.setDelegate(new CallLogActivity$$ExternalSyntheticLambda1(this));
        presentFragment(contactsFragment);
    }

    /* renamed from: lambda$createView$2$org-telegram-ui-CallLogActivity  reason: not valid java name */
    public /* synthetic */ void m2745lambda$createView$2$orgtelegramuiCallLogActivity(TLRPC.User user, String param, ContactsActivity activity) {
        TLRPC.UserFull userFull = getMessagesController().getUserFull(user.id);
        this.lastCallUser = user;
        VoIPHelper.startCall(user, false, userFull != null && userFull.video_calls_available, getParentActivity(), (TLRPC.UserFull) null, getAccountInstance());
    }

    /* access modifiers changed from: private */
    public void showDeleteAlert(boolean all) {
        AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
        if (all) {
            builder.setTitle(LocaleController.getString("DeleteAllCalls", NUM));
            builder.setMessage(LocaleController.getString("DeleteAllCallsText", NUM));
        } else {
            builder.setTitle(LocaleController.getString("DeleteCalls", NUM));
            builder.setMessage(LocaleController.getString("DeleteSelectedCallsText", NUM));
        }
        boolean[] checks = {false};
        FrameLayout frameLayout = new FrameLayout(getParentActivity());
        CheckBoxCell cell = new CheckBoxCell(getParentActivity(), 1);
        cell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
        cell.setText(LocaleController.getString("DeleteCallsForEveryone", NUM), "", false, false);
        cell.setPadding(LocaleController.isRTL ? AndroidUtilities.dp(8.0f) : 0, 0, LocaleController.isRTL ? 0 : AndroidUtilities.dp(8.0f), 0);
        frameLayout.addView(cell, LayoutHelper.createFrame(-1, 48.0f, 51, 8.0f, 0.0f, 8.0f, 0.0f));
        cell.setOnClickListener(new CallLogActivity$$ExternalSyntheticLambda3(checks));
        builder.setView(frameLayout);
        builder.setPositiveButton(LocaleController.getString("Delete", NUM), new CallLogActivity$$ExternalSyntheticLambda0(this, all, checks));
        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
        AlertDialog alertDialog = builder.create();
        showDialog(alertDialog);
        TextView button = (TextView) alertDialog.getButton(-1);
        if (button != null) {
            button.setTextColor(Theme.getColor("dialogTextRed2"));
        }
    }

    static /* synthetic */ void lambda$showDeleteAlert$4(boolean[] checks, View v) {
        checks[0] = !checks[0];
        ((CheckBoxCell) v).setChecked(checks[0], true);
    }

    /* renamed from: lambda$showDeleteAlert$5$org-telegram-ui-CallLogActivity  reason: not valid java name */
    public /* synthetic */ void m2751lambda$showDeleteAlert$5$orgtelegramuiCallLogActivity(boolean all, boolean[] checks, DialogInterface dialogInterface, int i) {
        if (all) {
            deleteAllMessages(checks[0]);
            this.calls.clear();
            this.loading = false;
            this.endReached = true;
            this.otherItem.setVisibility(8);
            this.listViewAdapter.notifyDataSetChanged();
        } else {
            getMessagesController().deleteMessages(new ArrayList(this.selectedIds), (ArrayList<Long>) null, (TLRPC.EncryptedChat) null, 0, checks[0], false);
        }
        hideActionMode(false);
    }

    private void deleteAllMessages(boolean revoke) {
        TLRPC.TL_messages_deletePhoneCallHistory req = new TLRPC.TL_messages_deletePhoneCallHistory();
        req.revoke = revoke;
        getConnectionsManager().sendRequest(req, new CallLogActivity$$ExternalSyntheticLambda7(this, revoke));
    }

    /* renamed from: lambda$deleteAllMessages$6$org-telegram-ui-CallLogActivity  reason: not valid java name */
    public /* synthetic */ void m2747lambda$deleteAllMessages$6$orgtelegramuiCallLogActivity(boolean revoke, TLObject response, TLRPC.TL_error error) {
        if (response != null) {
            TLRPC.TL_messages_affectedFoundMessages res = (TLRPC.TL_messages_affectedFoundMessages) response;
            TLRPC.TL_updateDeleteMessages updateDeleteMessages = new TLRPC.TL_updateDeleteMessages();
            updateDeleteMessages.messages = res.messages;
            updateDeleteMessages.pts = res.pts;
            updateDeleteMessages.pts_count = res.pts_count;
            TLRPC.TL_updates updates = new TLRPC.TL_updates();
            updates.updates.add(updateDeleteMessages);
            getMessagesController().processUpdates(updates, false);
            if (res.offset != 0) {
                deleteAllMessages(revoke);
            }
        }
    }

    /* access modifiers changed from: private */
    public void hideActionMode(boolean animated) {
        this.actionBar.hideActionMode();
        this.selectedIds.clear();
        int N = this.listView.getChildCount();
        for (int a = 0; a < N; a++) {
            View child = this.listView.getChildAt(a);
            if (child instanceof CallCell) {
                ((CallCell) child).setChecked(false, animated);
            }
        }
    }

    /* access modifiers changed from: private */
    public boolean isSelected(ArrayList<TLRPC.Message> messages) {
        int N = messages.size();
        for (int a = 0; a < N; a++) {
            if (this.selectedIds.contains(Integer.valueOf(messages.get(a).id))) {
                return true;
            }
        }
        return false;
    }

    private void createActionMode() {
        if (!this.actionBar.actionModeIsExist((String) null)) {
            ActionBarMenu actionMode = this.actionBar.createActionMode();
            NumberTextView numberTextView = new NumberTextView(actionMode.getContext());
            this.selectedDialogsCountTextView = numberTextView;
            numberTextView.setTextSize(18);
            this.selectedDialogsCountTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.selectedDialogsCountTextView.setTextColor(Theme.getColor("actionBarActionModeDefaultIcon"));
            actionMode.addView(this.selectedDialogsCountTextView, LayoutHelper.createLinear(0, -1, 1.0f, 72, 0, 0, 0));
            this.selectedDialogsCountTextView.setOnTouchListener(CallLogActivity$$ExternalSyntheticLambda4.INSTANCE);
            this.actionModeViews.add(actionMode.addItemWithWidth(2, NUM, AndroidUtilities.dp(54.0f), (CharSequence) LocaleController.getString("Delete", NUM)));
        }
    }

    static /* synthetic */ boolean lambda$createActionMode$7(View v, MotionEvent event) {
        return true;
    }

    private boolean addOrRemoveSelectedDialog(ArrayList<TLRPC.Message> messages, CallCell cell) {
        if (messages.isEmpty()) {
            return false;
        }
        if (isSelected(messages)) {
            int N = messages.size();
            for (int a = 0; a < N; a++) {
                this.selectedIds.remove(Integer.valueOf(messages.get(a).id));
            }
            cell.setChecked(false, true);
            showOrUpdateActionMode();
            return false;
        }
        int N2 = messages.size();
        for (int a2 = 0; a2 < N2; a2++) {
            Integer id = Integer.valueOf(messages.get(a2).id);
            if (!this.selectedIds.contains(id)) {
                this.selectedIds.add(id);
            }
        }
        cell.setChecked(true, true);
        showOrUpdateActionMode();
        return true;
    }

    private void showOrUpdateActionMode() {
        boolean updateAnimated = false;
        if (!this.actionBar.isActionModeShowed()) {
            createActionMode();
            this.actionBar.showActionMode();
            AnimatorSet animatorSet = new AnimatorSet();
            ArrayList<Animator> animators = new ArrayList<>();
            for (int a = 0; a < this.actionModeViews.size(); a++) {
                View view = this.actionModeViews.get(a);
                view.setPivotY((float) (ActionBar.getCurrentActionBarHeight() / 2));
                AndroidUtilities.clearDrawableAnimation(view);
                animators.add(ObjectAnimator.ofFloat(view, View.SCALE_Y, new float[]{0.1f, 1.0f}));
            }
            animatorSet.playTogether(animators);
            animatorSet.setDuration(200);
            animatorSet.start();
        } else if (this.selectedIds.isEmpty()) {
            hideActionMode(true);
            return;
        } else {
            updateAnimated = true;
        }
        this.selectedDialogsCountTextView.setNumber(this.selectedIds.size(), updateAnimated);
    }

    /* access modifiers changed from: private */
    public void hideFloatingButton(boolean hide) {
        if (this.floatingHidden != hide) {
            this.floatingHidden = hide;
            ImageView imageView = this.floatingButton;
            float[] fArr = new float[1];
            fArr[0] = hide ? (float) AndroidUtilities.dp(100.0f) : 0.0f;
            ObjectAnimator animator = ObjectAnimator.ofFloat(imageView, "translationY", fArr).setDuration(300);
            animator.setInterpolator(this.floatingInterpolator);
            this.floatingButton.setClickable(!hide);
            animator.start();
        }
    }

    /* access modifiers changed from: private */
    public void getCalls(int max_id, int count) {
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
            TLRPC.TL_messages_search req = new TLRPC.TL_messages_search();
            req.limit = count;
            req.peer = new TLRPC.TL_inputPeerEmpty();
            req.filter = new TLRPC.TL_inputMessagesFilterPhoneCalls();
            req.q = "";
            req.offset_id = max_id;
            getConnectionsManager().bindRequestToGuid(getConnectionsManager().sendRequest(req, new CallLogActivity$$ExternalSyntheticLambda6(this), 2), this.classGuid);
        }
    }

    /* renamed from: lambda$getCalls$9$org-telegram-ui-CallLogActivity  reason: not valid java name */
    public /* synthetic */ void m2749lambda$getCalls$9$orgtelegramuiCallLogActivity(TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new CallLogActivity$$ExternalSyntheticLambda5(this, error, response));
    }

    /* renamed from: lambda$getCalls$8$org-telegram-ui-CallLogActivity  reason: not valid java name */
    public /* synthetic */ void m2748lambda$getCalls$8$orgtelegramuiCallLogActivity(TLRPC.TL_error error, TLObject response) {
        CallLogRow currentRow;
        LongSparseArray<TLRPC.User> users;
        LongSparseArray<TLRPC.User> users2;
        int oldCount = Math.max(this.listViewAdapter.callsStartRow, 0) + this.calls.size();
        int i = 1;
        if (error == null) {
            LongSparseArray<TLRPC.User> users3 = new LongSparseArray<>();
            TLRPC.messages_Messages msgs = (TLRPC.messages_Messages) response;
            this.endReached = msgs.messages.isEmpty();
            for (int a = 0; a < msgs.users.size(); a++) {
                TLRPC.User user = msgs.users.get(a);
                users3.put(user.id, user);
            }
            if (this.calls.size() > 0) {
                ArrayList<CallLogRow> arrayList = this.calls;
                currentRow = arrayList.get(arrayList.size() - 1);
            } else {
                currentRow = null;
            }
            int a2 = 0;
            while (a2 < msgs.messages.size()) {
                TLRPC.Message msg = msgs.messages.get(a2);
                if (msg.action == null) {
                    users = users3;
                } else if (msg.action instanceof TLRPC.TL_messageActionHistoryClear) {
                    users = users3;
                } else {
                    int callType = MessageObject.getFromChatId(msg) == getUserConfig().getClientUserId() ? 0 : 1;
                    TLRPC.PhoneCallDiscardReason reason = msg.action.reason;
                    if (callType == i && ((reason instanceof TLRPC.TL_phoneCallDiscardReasonMissed) || (reason instanceof TLRPC.TL_phoneCallDiscardReasonBusy))) {
                        callType = 2;
                    }
                    long fromId = MessageObject.getFromChatId(msg);
                    long userID = fromId == getUserConfig().getClientUserId() ? msg.peer_id.user_id : fromId;
                    if (currentRow != null) {
                        users2 = users3;
                        if (currentRow.user.id == userID && currentRow.type == callType) {
                            users = users2;
                            currentRow.calls.add(msg);
                        }
                    } else {
                        users2 = users3;
                    }
                    if (currentRow != null && !this.calls.contains(currentRow)) {
                        this.calls.add(currentRow);
                    }
                    CallLogRow row = new CallLogRow();
                    row.calls = new ArrayList<>();
                    users = users2;
                    row.user = users.get(userID);
                    row.type = callType;
                    row.video = msg.action != null && msg.action.video;
                    currentRow = row;
                    currentRow.calls.add(msg);
                }
                a2++;
                users3 = users;
                i = 1;
            }
            if (currentRow != null && currentRow.calls.size() > 0 && !this.calls.contains(currentRow)) {
                this.calls.add(currentRow);
            }
        } else {
            this.endReached = true;
        }
        int i2 = 0;
        this.loading = false;
        showItemsAnimated(oldCount);
        if (!this.firstLoaded) {
            resumeDelayedFragmentAnimation();
        }
        this.firstLoaded = true;
        ActionBarMenuItem actionBarMenuItem = this.otherItem;
        if (this.calls.isEmpty()) {
            i2 = 8;
        }
        actionBarMenuItem.setVisibility(i2);
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

    public void onRequestPermissionsResultFragment(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 101 || requestCode == 102 || requestCode == 103) {
            boolean allGranted = true;
            int a = 0;
            while (true) {
                if (a >= grantResults.length) {
                    break;
                } else if (grantResults[a] != 0) {
                    allGranted = false;
                    break;
                } else {
                    a++;
                }
            }
            TLRPC.UserFull userFull = null;
            if (grantResults.length <= 0 || !allGranted) {
                VoIPHelper.permissionDenied(getParentActivity(), (Runnable) null, requestCode);
            } else if (requestCode == 103) {
                VoIPHelper.startCall(this.lastCallChat, (TLRPC.InputPeer) null, (String) null, false, getParentActivity(), this, getAccountInstance());
            } else {
                if (this.lastCallUser != null) {
                    userFull = getMessagesController().getUserFull(this.lastCallUser.id);
                }
                TLRPC.UserFull userFull2 = userFull;
                TLRPC.User user = this.lastCallUser;
                boolean z = true;
                boolean z2 = requestCode == 102;
                if (requestCode != 102 && (userFull2 == null || !userFull2.video_calls_available)) {
                    z = false;
                }
                VoIPHelper.startCall(user, z2, z, getParentActivity(), (TLRPC.UserFull) null, getAccountInstance());
            }
        }
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private int activeEndRow;
        /* access modifiers changed from: private */
        public int activeHeaderRow;
        private int activeStartRow;
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

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int type = holder.getItemViewType();
            return type == 0 || type == 4;
        }

        public int getItemCount() {
            return this.rowsCount;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    view = new CallCell(this.mContext);
                    break;
                case 1:
                    FlickerLoadingView flickerLoadingView = new FlickerLoadingView(this.mContext);
                    flickerLoadingView.setIsSingleCell(true);
                    flickerLoadingView.setViewType(8);
                    flickerLoadingView.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    flickerLoadingView.showDate(false);
                    view = flickerLoadingView;
                    break;
                case 2:
                    view = new TextInfoPrivacyCell(this.mContext);
                    view.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                    break;
                case 3:
                    View view2 = new HeaderCell(this.mContext, "windowBackgroundWhiteBlueHeader", 21, 15, 2, false, CallLogActivity.this.getResourceProvider());
                    view2.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    view = view2;
                    break;
                case 4:
                    view = new GroupCallCell(this.mContext);
                    break;
                default:
                    view = new ShadowSectionCell(this.mContext);
                    break;
            }
            return new RecyclerListView.Holder(view);
        }

        public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
            if (holder.itemView instanceof CallCell) {
                ((CallCell) holder.itemView).setChecked(CallLogActivity.this.isSelected(((CallLogRow) CallLogActivity.this.calls.get(holder.getAdapterPosition() - this.callsStartRow)).calls), false);
            }
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            SpannableString subtitle;
            String text;
            RecyclerView.ViewHolder viewHolder = holder;
            int i = position;
            boolean z = false;
            switch (holder.getItemViewType()) {
                case 0:
                    int position2 = i - this.callsStartRow;
                    CallLogRow row = (CallLogRow) CallLogActivity.this.calls.get(position2);
                    CallCell cell = (CallCell) viewHolder.itemView;
                    cell.imageView.setImageResource(row.video ? NUM : NUM);
                    TLRPC.Message last = row.calls.get(0);
                    String ldir = LocaleController.isRTL ? "‫" : "";
                    if (row.calls.size() == 1) {
                        subtitle = new SpannableString(ldir + "  " + LocaleController.formatDateCallLog((long) last.date));
                    } else {
                        subtitle = new SpannableString(String.format(ldir + "  (%d) %s", new Object[]{Integer.valueOf(row.calls.size()), LocaleController.formatDateCallLog((long) last.date)}));
                    }
                    switch (row.type) {
                        case 0:
                            subtitle.setSpan(CallLogActivity.this.iconOut, ldir.length(), ldir.length() + 1, 0);
                            break;
                        case 1:
                            subtitle.setSpan(CallLogActivity.this.iconIn, ldir.length(), ldir.length() + 1, 0);
                            break;
                        case 2:
                            subtitle.setSpan(CallLogActivity.this.iconMissed, ldir.length(), ldir.length() + 1, 0);
                            break;
                    }
                    cell.profileSearchCell.setData(row.user, (TLRPC.EncryptedChat) null, (CharSequence) null, subtitle, false, false);
                    ProfileSearchCell access$2400 = cell.profileSearchCell;
                    if (position2 != CallLogActivity.this.calls.size() - 1 || !CallLogActivity.this.endReached) {
                        z = true;
                    }
                    access$2400.useSeparator = z;
                    cell.imageView.setTag(row);
                    return;
                case 3:
                    HeaderCell cell2 = (HeaderCell) viewHolder.itemView;
                    if (i == this.activeHeaderRow) {
                        cell2.setText(LocaleController.getString("VoipChatActiveChats", NUM));
                        return;
                    } else if (i == this.callsHeaderRow) {
                        cell2.setText(LocaleController.getString("VoipChatRecentCalls", NUM));
                        return;
                    } else {
                        return;
                    }
                case 4:
                    int position3 = i - this.activeStartRow;
                    TLRPC.Chat chat = CallLogActivity.this.getMessagesController().getChat((Long) CallLogActivity.this.activeGroupCalls.get(position3));
                    GroupCallCell cell3 = (GroupCallCell) viewHolder.itemView;
                    cell3.setChat(chat);
                    cell3.button.setTag(Long.valueOf(chat.id));
                    if (!ChatObject.isChannel(chat) || chat.megagroup) {
                        if (chat.has_geo) {
                            text = LocaleController.getString("MegaLocation", NUM);
                        } else if (TextUtils.isEmpty(chat.username)) {
                            text = LocaleController.getString("MegaPrivate", NUM).toLowerCase();
                        } else {
                            text = LocaleController.getString("MegaPublic", NUM).toLowerCase();
                        }
                    } else if (TextUtils.isEmpty(chat.username)) {
                        text = LocaleController.getString("ChannelPrivate", NUM).toLowerCase();
                    } else {
                        text = LocaleController.getString("ChannelPublic", NUM).toLowerCase();
                    }
                    ProfileSearchCell access$2600 = cell3.profileSearchCell;
                    if (position3 != CallLogActivity.this.activeGroupCalls.size() - 1 && !CallLogActivity.this.endReached) {
                        z = true;
                    }
                    access$2600.useSeparator = z;
                    cell3.profileSearchCell.setData(chat, (TLRPC.EncryptedChat) null, (CharSequence) null, text, false, false);
                    return;
                default:
                    return;
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
            if (i == this.sectionRow) {
                return 5;
            }
            return 2;
        }
    }

    private static class CallLogRow {
        public ArrayList<TLRPC.Message> calls;
        public int type;
        public TLRPC.User user;
        public boolean video;

        private CallLogRow() {
        }
    }

    /* access modifiers changed from: protected */
    public void onTransitionAnimationStart(boolean isOpen, boolean backward) {
        super.onTransitionAnimationStart(isOpen, backward);
        if (isOpen) {
            this.openTransitionStarted = true;
        }
    }

    public boolean needDelayOpenAnimation() {
        return true;
    }

    private void showItemsAnimated(final int from) {
        if (!this.isPaused && this.openTransitionStarted) {
            View progressView = null;
            for (int i = 0; i < this.listView.getChildCount(); i++) {
                View child = this.listView.getChildAt(i);
                if (child instanceof FlickerLoadingView) {
                    progressView = child;
                }
            }
            final View finalProgressView = progressView;
            if (progressView != null) {
                this.listView.removeView(progressView);
            }
            this.listView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                public boolean onPreDraw() {
                    CallLogActivity.this.listView.getViewTreeObserver().removeOnPreDrawListener(this);
                    int n = CallLogActivity.this.listView.getChildCount();
                    AnimatorSet animatorSet = new AnimatorSet();
                    for (int i = 0; i < n; i++) {
                        View child = CallLogActivity.this.listView.getChildAt(i);
                        RecyclerView.ViewHolder holder = CallLogActivity.this.listView.getChildViewHolder(child);
                        if (child != finalProgressView && CallLogActivity.this.listView.getChildAdapterPosition(child) >= from && !(child instanceof GroupCallCell) && (!(child instanceof HeaderCell) || holder.getAdapterPosition() != CallLogActivity.this.listViewAdapter.activeHeaderRow)) {
                            child.setAlpha(0.0f);
                            ObjectAnimator a = ObjectAnimator.ofFloat(child, View.ALPHA, new float[]{0.0f, 1.0f});
                            a.setStartDelay((long) ((int) ((((float) Math.min(CallLogActivity.this.listView.getMeasuredHeight(), Math.max(0, child.getTop()))) / ((float) CallLogActivity.this.listView.getMeasuredHeight())) * 100.0f)));
                            a.setDuration(200);
                            animatorSet.playTogether(new Animator[]{a});
                        }
                    }
                    View view = finalProgressView;
                    if (view != null && view.getParent() == null) {
                        CallLogActivity.this.listView.addView(finalProgressView);
                        final RecyclerView.LayoutManager layoutManager = CallLogActivity.this.listView.getLayoutManager();
                        if (layoutManager != null) {
                            layoutManager.ignoreView(finalProgressView);
                            Animator animator = ObjectAnimator.ofFloat(finalProgressView, View.ALPHA, new float[]{finalProgressView.getAlpha(), 0.0f});
                            animator.addListener(new AnimatorListenerAdapter() {
                                public void onAnimationEnd(Animator animation) {
                                    finalProgressView.setAlpha(1.0f);
                                    layoutManager.stopIgnoringView(finalProgressView);
                                    CallLogActivity.this.listView.removeView(finalProgressView);
                                }
                            });
                            animator.start();
                        }
                    }
                    animatorSet.start();
                    return true;
                }
            });
        }
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();
        ThemeDescription.ThemeDescriptionDelegate cellDelegate = new CallLogActivity$$ExternalSyntheticLambda8(this);
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{LocationCell.class, CallCell.class, HeaderCell.class, GroupCallCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        themeDescriptions.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultIcon"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultTitle"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "divider"));
        themeDescriptions.add(new ThemeDescription((View) this.emptyView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{EmptyTextProgressView.class}, new String[]{"emptyTextView1"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription((View) this.emptyView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{EmptyTextProgressView.class}, new String[]{"emptyTextView2"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "emptyListPlaceholder"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{LoadingCell.class}, new String[]{"progressBar"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "progressCircle"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText4"));
        themeDescriptions.add(new ThemeDescription(this.floatingButton, ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_actionIcon"));
        themeDescriptions.add(new ThemeDescription(this.floatingButton, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_actionBackground"));
        themeDescriptions.add(new ThemeDescription(this.floatingButton, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_actionPressedBackground"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{CallCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "featuredStickers_addButton"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{CallCell.class}, (Paint) null, new Drawable[]{Theme.dialogs_verifiedCheckDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_verifiedCheck"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{CallCell.class}, (Paint) null, new Drawable[]{Theme.dialogs_verifiedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_verifiedBackground"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{CallCell.class}, Theme.dialogs_offlinePaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText3"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{CallCell.class}, Theme.dialogs_onlinePaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueText3"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{CallCell.class}, (String[]) null, new Paint[]{Theme.dialogs_namePaint[0], Theme.dialogs_namePaint[1], Theme.dialogs_searchNamePaint}, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_name"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{CallCell.class}, (String[]) null, new Paint[]{Theme.dialogs_nameEncryptedPaint[0], Theme.dialogs_nameEncryptedPaint[1], Theme.dialogs_searchNameEncryptedPaint}, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_secretName"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{CallCell.class}, (Paint) null, Theme.avatarDrawables, (ThemeDescription.ThemeDescriptionDelegate) null, "avatar_text"));
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, cellDelegate, "avatar_backgroundRed"));
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate = cellDelegate;
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, "avatar_backgroundOrange"));
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate2 = cellDelegate;
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, "avatar_backgroundViolet"));
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, "avatar_backgroundGreen"));
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, "avatar_backgroundCyan"));
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, "avatar_backgroundBlue"));
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, "avatar_backgroundPink"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, (Paint) null, new Drawable[]{this.greenDrawable, this.greenDrawable2, Theme.calllog_msgCallUpRedDrawable, Theme.calllog_msgCallDownRedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "calls_callReceivedGreenIcon"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, (Paint) null, new Drawable[]{this.redDrawable, Theme.calllog_msgCallUpGreenDrawable, Theme.calllog_msgCallDownGreenDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "calls_callReceivedRedIcon"));
        themeDescriptions.add(new ThemeDescription(this.flickerLoadingView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueHeader"));
        return themeDescriptions;
    }

    /* renamed from: lambda$getThemeDescriptions$10$org-telegram-ui-CallLogActivity  reason: not valid java name */
    public /* synthetic */ void m2750lambda$getThemeDescriptions$10$orgtelegramuiCallLogActivity() {
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            int count = recyclerListView.getChildCount();
            for (int a = 0; a < count; a++) {
                View child = this.listView.getChildAt(a);
                if (child instanceof CallCell) {
                    ((CallCell) child).profileSearchCell.update(0);
                }
            }
        }
    }
}
