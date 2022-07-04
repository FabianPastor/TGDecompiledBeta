package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Property;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.LocationController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.ManageChatTextCell;
import org.telegram.ui.Cells.ManageChatUserCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RadialProgressView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.ShareLocationDrawable;
import org.telegram.ui.Components.UndoView;

public class PeopleNearbyActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate, LocationController.LocationFetchCallback {
    private static final int SHORT_POLL_TIMEOUT = 25000;
    /* access modifiers changed from: private */
    public AnimatorSet actionBarAnimator;
    /* access modifiers changed from: private */
    public View actionBarBackground;
    /* access modifiers changed from: private */
    public ArrayList<View> animatingViews = new ArrayList<>();
    private boolean canCreateGroup;
    /* access modifiers changed from: private */
    public ArrayList<TLRPC.TL_peerLocated> chats = new ArrayList<>(getLocationController().getCachedNearbyChats());
    /* access modifiers changed from: private */
    public int chatsCreateRow;
    /* access modifiers changed from: private */
    public int chatsEndRow;
    /* access modifiers changed from: private */
    public int chatsHeaderRow;
    /* access modifiers changed from: private */
    public int chatsSectionRow;
    /* access modifiers changed from: private */
    public int chatsStartRow;
    private Runnable checkExpiredRunnable;
    private boolean checkingCanCreate;
    private int currentChatId;
    private String currentGroupCreateAddress;
    private String currentGroupCreateDisplayAddress;
    private Location currentGroupCreateLocation;
    private boolean expanded;
    private boolean firstLoaded;
    private ActionIntroActivity groupCreateActivity;
    /* access modifiers changed from: private */
    public int helpRow;
    /* access modifiers changed from: private */
    public int helpSectionRow;
    private DefaultItemAnimator itemAnimator;
    private Location lastLoadedLocation;
    private long lastLoadedLocationTime;
    private LinearLayoutManager layoutManager;
    private RecyclerListView listView;
    private ListAdapter listViewAdapter;
    private AlertDialog loadingDialog;
    private int[] location = new int[2];
    private int reqId;
    /* access modifiers changed from: private */
    public int rowCount;
    /* access modifiers changed from: private */
    public Runnable shortPollRunnable = new Runnable() {
        public void run() {
            if (PeopleNearbyActivity.this.shortPollRunnable != null) {
                PeopleNearbyActivity.this.sendRequest(true, 0);
                AndroidUtilities.cancelRunOnUIThread(PeopleNearbyActivity.this.shortPollRunnable);
                AndroidUtilities.runOnUIThread(PeopleNearbyActivity.this.shortPollRunnable, 25000);
            }
        }
    };
    /* access modifiers changed from: private */
    public int showMeRow;
    /* access modifiers changed from: private */
    public int showMoreRow;
    /* access modifiers changed from: private */
    public AnimatorSet showProgressAnimation;
    private Runnable showProgressRunnable;
    /* access modifiers changed from: private */
    public boolean showingLoadingProgress;
    /* access modifiers changed from: private */
    public boolean showingMe;
    private UndoView undoView;
    /* access modifiers changed from: private */
    public ArrayList<TLRPC.TL_peerLocated> users = new ArrayList<>(getLocationController().getCachedNearbyUsers());
    /* access modifiers changed from: private */
    public int usersEndRow;
    /* access modifiers changed from: private */
    public int usersHeaderRow;
    /* access modifiers changed from: private */
    public int usersSectionRow;
    /* access modifiers changed from: private */
    public int usersStartRow;

    public PeopleNearbyActivity() {
        checkForExpiredLocations(false);
        updateRows((DiffCallback) null);
    }

    private void updateRows(DiffCallback diffCallback) {
        int count;
        this.rowCount = 0;
        this.usersStartRow = -1;
        this.usersEndRow = -1;
        this.showMoreRow = -1;
        this.chatsStartRow = -1;
        this.chatsEndRow = -1;
        this.chatsCreateRow = -1;
        this.showMeRow = -1;
        int i = 0 + 1;
        this.rowCount = i;
        this.helpRow = 0;
        int i2 = i + 1;
        this.rowCount = i2;
        this.helpSectionRow = i;
        int i3 = i2 + 1;
        this.rowCount = i3;
        this.usersHeaderRow = i2;
        this.rowCount = i3 + 1;
        this.showMeRow = i3;
        if (!this.users.isEmpty()) {
            if (this.expanded) {
                count = this.users.size();
            } else {
                count = Math.min(5, this.users.size());
            }
            int i4 = this.rowCount;
            this.usersStartRow = i4;
            int i5 = i4 + count;
            this.rowCount = i5;
            this.usersEndRow = i5;
            if (count != this.users.size()) {
                int i6 = this.rowCount;
                this.rowCount = i6 + 1;
                this.showMoreRow = i6;
            }
        }
        int count2 = this.rowCount;
        int i7 = count2 + 1;
        this.rowCount = i7;
        this.usersSectionRow = count2;
        int i8 = i7 + 1;
        this.rowCount = i8;
        this.chatsHeaderRow = i7;
        this.rowCount = i8 + 1;
        this.chatsCreateRow = i8;
        if (!this.chats.isEmpty()) {
            int i9 = this.rowCount;
            this.chatsStartRow = i9;
            int size = i9 + this.chats.size();
            this.rowCount = size;
            this.chatsEndRow = size;
        }
        int i10 = this.rowCount;
        this.rowCount = i10 + 1;
        this.chatsSectionRow = i10;
        if (this.listViewAdapter == null) {
            return;
        }
        if (diffCallback == null) {
            this.listView.setItemAnimator((RecyclerView.ItemAnimator) null);
            this.listViewAdapter.notifyDataSetChanged();
            return;
        }
        this.listView.setItemAnimator(this.itemAnimator);
        diffCallback.fillPositions(diffCallback.newPositionToItem);
        DiffUtil.calculateDiff(diffCallback).dispatchUpdatesTo((RecyclerView.Adapter) this.listViewAdapter);
    }

    private class DiffCallback extends DiffUtil.Callback {
        SparseIntArray newPositionToItem;
        private final ArrayList<TLRPC.TL_peerLocated> oldChats;
        int oldChatsEndRow;
        int oldChatsStartRow;
        SparseIntArray oldPositionToItem;
        int oldRowCount;
        private final ArrayList<TLRPC.TL_peerLocated> oldUsers;
        int oldUsersEndRow;
        int oldUsersStartRow;

        private DiffCallback() {
            this.oldPositionToItem = new SparseIntArray();
            this.newPositionToItem = new SparseIntArray();
            this.oldUsers = new ArrayList<>();
            this.oldChats = new ArrayList<>();
        }

        public int getOldListSize() {
            return this.oldRowCount;
        }

        public int getNewListSize() {
            return PeopleNearbyActivity.this.rowCount;
        }

        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            int i;
            int i2;
            if (newItemPosition < PeopleNearbyActivity.this.usersStartRow || newItemPosition >= PeopleNearbyActivity.this.usersEndRow || oldItemPosition < (i2 = this.oldUsersStartRow) || oldItemPosition >= this.oldUsersEndRow) {
                if (newItemPosition < PeopleNearbyActivity.this.chatsStartRow || newItemPosition >= PeopleNearbyActivity.this.chatsEndRow || oldItemPosition < (i = this.oldChatsStartRow) || oldItemPosition >= this.oldChatsEndRow) {
                    int oldIndex = this.oldPositionToItem.get(oldItemPosition, -1);
                    if (oldIndex != this.newPositionToItem.get(newItemPosition, -1) || oldIndex < 0) {
                        return false;
                    }
                    return true;
                } else if (MessageObject.getPeerId(this.oldChats.get(oldItemPosition - i).peer) == MessageObject.getPeerId(((TLRPC.TL_peerLocated) PeopleNearbyActivity.this.chats.get(newItemPosition - PeopleNearbyActivity.this.chatsStartRow)).peer)) {
                    return true;
                } else {
                    return false;
                }
            } else if (MessageObject.getPeerId(this.oldUsers.get(oldItemPosition - i2).peer) == MessageObject.getPeerId(((TLRPC.TL_peerLocated) PeopleNearbyActivity.this.users.get(newItemPosition - PeopleNearbyActivity.this.usersStartRow)).peer)) {
                return true;
            } else {
                return false;
            }
        }

        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return areItemsTheSame(oldItemPosition, newItemPosition);
        }

        public void fillPositions(SparseIntArray sparseIntArray) {
            sparseIntArray.clear();
            int pointer = 0 + 1;
            put(pointer, PeopleNearbyActivity.this.helpRow, sparseIntArray);
            int pointer2 = pointer + 1;
            put(pointer2, PeopleNearbyActivity.this.helpSectionRow, sparseIntArray);
            int pointer3 = pointer2 + 1;
            put(pointer3, PeopleNearbyActivity.this.usersHeaderRow, sparseIntArray);
            int pointer4 = pointer3 + 1;
            put(pointer4, PeopleNearbyActivity.this.showMoreRow, sparseIntArray);
            int pointer5 = pointer4 + 1;
            put(pointer5, PeopleNearbyActivity.this.usersSectionRow, sparseIntArray);
            int pointer6 = pointer5 + 1;
            put(pointer6, PeopleNearbyActivity.this.chatsHeaderRow, sparseIntArray);
            int pointer7 = pointer6 + 1;
            put(pointer7, PeopleNearbyActivity.this.chatsCreateRow, sparseIntArray);
            int pointer8 = pointer7 + 1;
            put(pointer8, PeopleNearbyActivity.this.chatsSectionRow, sparseIntArray);
            put(pointer8 + 1, PeopleNearbyActivity.this.showMeRow, sparseIntArray);
        }

        public void saveCurrentState() {
            this.oldRowCount = PeopleNearbyActivity.this.rowCount;
            this.oldUsersStartRow = PeopleNearbyActivity.this.usersStartRow;
            this.oldUsersEndRow = PeopleNearbyActivity.this.usersEndRow;
            this.oldChatsStartRow = PeopleNearbyActivity.this.chatsStartRow;
            this.oldChatsEndRow = PeopleNearbyActivity.this.chatsEndRow;
            this.oldUsers.addAll(PeopleNearbyActivity.this.users);
            this.oldChats.addAll(PeopleNearbyActivity.this.chats);
            fillPositions(this.oldPositionToItem);
        }

        private void put(int id, int position, SparseIntArray sparseIntArray) {
            if (position >= 0) {
                sparseIntArray.put(position, id);
            }
        }
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.newLocationAvailable);
        getNotificationCenter().addObserver(this, NotificationCenter.newPeopleNearbyAvailable);
        getNotificationCenter().addObserver(this, NotificationCenter.needDeleteDialog);
        checkCanCreateGroup();
        sendRequest(false, 0);
        AndroidUtilities.runOnUIThread(this.shortPollRunnable, 25000);
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.newLocationAvailable);
        getNotificationCenter().removeObserver(this, NotificationCenter.newPeopleNearbyAvailable);
        getNotificationCenter().removeObserver(this, NotificationCenter.needDeleteDialog);
        Runnable runnable = this.shortPollRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.shortPollRunnable = null;
        }
        Runnable runnable2 = this.checkExpiredRunnable;
        if (runnable2 != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable2);
            this.checkExpiredRunnable = null;
        }
        Runnable runnable3 = this.showProgressRunnable;
        if (runnable3 != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable3);
            this.showProgressRunnable = null;
        }
        UndoView undoView2 = this.undoView;
        if (undoView2 != null) {
            undoView2.hide(true, 0);
        }
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setBackgroundDrawable((Drawable) null);
        this.actionBar.setTitleColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.actionBar.setItemsColor(Theme.getColor("windowBackgroundWhiteBlackText"), false);
        this.actionBar.setItemsBackgroundColor(Theme.getColor("listSelectorSDK21"), false);
        this.actionBar.setCastShadows(false);
        this.actionBar.setAddToContainer(false);
        int i = 1;
        this.actionBar.setOccupyStatusBar(Build.VERSION.SDK_INT >= 21 && !AndroidUtilities.isTablet());
        this.actionBar.setTitle(LocaleController.getString("PeopleNearby", NUM));
        this.actionBar.getTitleTextView().setAlpha(0.0f);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    PeopleNearbyActivity.this.finishFragment();
                }
            }
        });
        this.fragmentView = new FrameLayout(context) {
            /* access modifiers changed from: protected */
            public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                ((FrameLayout.LayoutParams) PeopleNearbyActivity.this.actionBarBackground.getLayoutParams()).height = ActionBar.getCurrentActionBarHeight() + (PeopleNearbyActivity.this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0) + AndroidUtilities.dp(3.0f);
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            }

            /* access modifiers changed from: protected */
            public void onLayout(boolean changed, int left, int top, int right, int bottom) {
                super.onLayout(changed, left, top, right, bottom);
                PeopleNearbyActivity.this.checkScroll(false);
            }
        };
        this.fragmentView.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
        this.fragmentView.setTag("windowBackgroundGray");
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        RecyclerListView recyclerListView = new RecyclerListView(context);
        this.listView = recyclerListView;
        recyclerListView.setGlowColor(0);
        RecyclerListView recyclerListView2 = this.listView;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, 1, false);
        this.layoutManager = linearLayoutManager;
        recyclerListView2.setLayoutManager(linearLayoutManager);
        RecyclerListView recyclerListView3 = this.listView;
        ListAdapter listAdapter = new ListAdapter(context);
        this.listViewAdapter = listAdapter;
        recyclerListView3.setAdapter(listAdapter);
        RecyclerListView recyclerListView4 = this.listView;
        if (!LocaleController.isRTL) {
            i = 2;
        }
        recyclerListView4.setVerticalScrollbarPosition(i);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.itemAnimator = new DefaultItemAnimator() {
            /* access modifiers changed from: protected */
            public long getAddAnimationDelay(long removeDuration, long moveDuration, long changeDuration) {
                return removeDuration;
            }
        };
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new PeopleNearbyActivity$$ExternalSyntheticLambda1(this));
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                PeopleNearbyActivity.this.checkScroll(true);
            }
        });
        AnonymousClass6 r3 = new View(context) {
            private Paint paint = new Paint();

            /* access modifiers changed from: protected */
            public void onDraw(Canvas canvas) {
                this.paint.setColor(Theme.getColor("windowBackgroundWhite"));
                int h = getMeasuredHeight() - AndroidUtilities.dp(3.0f);
                canvas.drawRect(0.0f, 0.0f, (float) getMeasuredWidth(), (float) h, this.paint);
                PeopleNearbyActivity.this.parentLayout.drawHeaderShadow(canvas, h);
            }
        };
        this.actionBarBackground = r3;
        r3.setAlpha(0.0f);
        frameLayout.addView(this.actionBarBackground, LayoutHelper.createFrame(-1, -2.0f));
        frameLayout.addView(this.actionBar, LayoutHelper.createFrame(-1, -2.0f));
        UndoView undoView2 = new UndoView(context);
        this.undoView = undoView2;
        frameLayout.addView(undoView2, LayoutHelper.createFrame(-1, -2.0f, 83, 8.0f, 0.0f, 8.0f, 8.0f));
        updateRows((DiffCallback) null);
        return this.fragmentView;
    }

    /* renamed from: lambda$createView$2$org-telegram-ui-PeopleNearbyActivity  reason: not valid java name */
    public /* synthetic */ void m4203lambda$createView$2$orgtelegramuiPeopleNearbyActivity(View view, int position) {
        long chatId;
        if (getParentActivity() != null) {
            int i = this.usersStartRow;
            if (position < i || position >= this.usersEndRow) {
                int i2 = this.chatsStartRow;
                if (position >= i2 && position < this.chatsEndRow) {
                    TLRPC.TL_peerLocated peerLocated = this.chats.get(position - i2);
                    Bundle args1 = new Bundle();
                    if (peerLocated.peer instanceof TLRPC.TL_peerChat) {
                        chatId = peerLocated.peer.chat_id;
                    } else {
                        chatId = peerLocated.peer.channel_id;
                    }
                    args1.putLong("chat_id", chatId);
                    presentFragment(new ChatActivity(args1));
                } else if (position == this.chatsCreateRow) {
                    if (this.checkingCanCreate || this.currentGroupCreateAddress == null) {
                        AlertDialog alertDialog = new AlertDialog(getParentActivity(), 3);
                        this.loadingDialog = alertDialog;
                        alertDialog.setOnCancelListener(new PeopleNearbyActivity$$ExternalSyntheticLambda0(this));
                        this.loadingDialog.show();
                        return;
                    }
                    openGroupCreate();
                } else if (position == this.showMeRow) {
                    UserConfig userConfig = getUserConfig();
                    if (this.showingMe) {
                        userConfig.sharingMyLocationUntil = 0;
                        userConfig.saveConfig(false);
                        sendRequest(false, 2);
                        updateRows((DiffCallback) null);
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
                        builder.setTitle(LocaleController.getString("MakeMyselfVisibleTitle", NUM));
                        builder.setMessage(LocaleController.getString("MakeMyselfVisibleInfo", NUM));
                        builder.setPositiveButton(LocaleController.getString("OK", NUM), new PeopleNearbyActivity$$ExternalSyntheticLambda2(this, userConfig));
                        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                        showDialog(builder.create());
                    }
                    userConfig.saveConfig(false);
                } else if (position == this.showMoreRow) {
                    this.expanded = true;
                    DiffCallback diffCallback = new DiffCallback();
                    diffCallback.saveCurrentState();
                    updateRows(diffCallback);
                }
            } else if (view instanceof ManageChatUserCell) {
                TLRPC.TL_peerLocated peerLocated2 = this.users.get(position - i);
                Bundle args12 = new Bundle();
                args12.putLong("user_id", peerLocated2.peer.user_id);
                if (((ManageChatUserCell) view).hasAvatarSet()) {
                    args12.putBoolean("expandPhoto", true);
                }
                args12.putInt("nearby_distance", peerLocated2.distance);
                MessagesController.getInstance(this.currentAccount).ensureMessagesLoaded(peerLocated2.peer.user_id, 0, (MessagesController.MessagesLoadedCallback) null);
                presentFragment(new ProfileActivity(args12));
            }
        }
    }

    /* renamed from: lambda$createView$0$org-telegram-ui-PeopleNearbyActivity  reason: not valid java name */
    public /* synthetic */ void m4201lambda$createView$0$orgtelegramuiPeopleNearbyActivity(DialogInterface dialog) {
        this.loadingDialog = null;
    }

    /* renamed from: lambda$createView$1$org-telegram-ui-PeopleNearbyActivity  reason: not valid java name */
    public /* synthetic */ void m4202lambda$createView$1$orgtelegramuiPeopleNearbyActivity(UserConfig userConfig, DialogInterface dialog, int which) {
        userConfig.sharingMyLocationUntil = Integer.MAX_VALUE;
        userConfig.saveConfig(false);
        sendRequest(false, 1);
        updateRows((DiffCallback) null);
    }

    /* access modifiers changed from: private */
    public void checkScroll(boolean animated) {
        boolean show;
        int first = this.layoutManager.findFirstVisibleItemPosition();
        if (first != 0) {
            show = true;
        } else {
            RecyclerView.ViewHolder holder = this.listView.findViewHolderForAdapterPosition(first);
            if (holder == null) {
                show = true;
            } else {
                HintInnerCell hintInnerCell = (HintInnerCell) holder.itemView;
                hintInnerCell.titleTextView.getLocationOnScreen(this.location);
                show = this.location[1] + hintInnerCell.titleTextView.getMeasuredHeight() < this.actionBar.getBottom();
            }
        }
        if (show != (this.actionBarBackground.getTag() == null)) {
            this.actionBarBackground.setTag(show ? null : 1);
            AnimatorSet animatorSet = this.actionBarAnimator;
            if (animatorSet != null) {
                animatorSet.cancel();
                this.actionBarAnimator = null;
            }
            float f = 1.0f;
            if (animated) {
                AnimatorSet animatorSet2 = new AnimatorSet();
                this.actionBarAnimator = animatorSet2;
                Animator[] animatorArr = new Animator[2];
                View view = this.actionBarBackground;
                Property property = View.ALPHA;
                float[] fArr = new float[1];
                fArr[0] = show ? 1.0f : 0.0f;
                animatorArr[0] = ObjectAnimator.ofFloat(view, property, fArr);
                SimpleTextView titleTextView = this.actionBar.getTitleTextView();
                Property property2 = View.ALPHA;
                float[] fArr2 = new float[1];
                if (!show) {
                    f = 0.0f;
                }
                fArr2[0] = f;
                animatorArr[1] = ObjectAnimator.ofFloat(titleTextView, property2, fArr2);
                animatorSet2.playTogether(animatorArr);
                this.actionBarAnimator.setDuration(150);
                this.actionBarAnimator.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animation) {
                        if (animation.equals(PeopleNearbyActivity.this.actionBarAnimator)) {
                            AnimatorSet unused = PeopleNearbyActivity.this.actionBarAnimator = null;
                        }
                    }
                });
                this.actionBarAnimator.start();
                return;
            }
            this.actionBarBackground.setAlpha(show ? 1.0f : 0.0f);
            SimpleTextView titleTextView2 = this.actionBar.getTitleTextView();
            if (!show) {
                f = 0.0f;
            }
            titleTextView2.setAlpha(f);
        }
    }

    private void openGroupCreate() {
        if (!this.canCreateGroup) {
            AlertsCreator.showSimpleAlert(this, LocaleController.getString("YourLocatedChannelsTooMuch", NUM));
            return;
        }
        ActionIntroActivity actionIntroActivity = new ActionIntroActivity(2);
        this.groupCreateActivity = actionIntroActivity;
        actionIntroActivity.setGroupCreateAddress(this.currentGroupCreateAddress, this.currentGroupCreateDisplayAddress, this.currentGroupCreateLocation);
        presentFragment(this.groupCreateActivity);
    }

    private void checkCanCreateGroup() {
        if (!this.checkingCanCreate) {
            this.checkingCanCreate = true;
            TLRPC.TL_channels_getAdminedPublicChannels req = new TLRPC.TL_channels_getAdminedPublicChannels();
            req.by_location = true;
            req.check_limit = true;
            getConnectionsManager().bindRequestToGuid(getConnectionsManager().sendRequest(req, new PeopleNearbyActivity$$ExternalSyntheticLambda8(this)), this.classGuid);
        }
    }

    /* renamed from: lambda$checkCanCreateGroup$4$org-telegram-ui-PeopleNearbyActivity  reason: not valid java name */
    public /* synthetic */ void m4199x84var_e9(TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new PeopleNearbyActivity$$ExternalSyntheticLambda7(this, error));
    }

    /* renamed from: lambda$checkCanCreateGroup$3$org-telegram-ui-PeopleNearbyActivity  reason: not valid java name */
    public /* synthetic */ void m4198x23a6bb4a(TLRPC.TL_error error) {
        this.canCreateGroup = error == null;
        this.checkingCanCreate = false;
        AlertDialog alertDialog = this.loadingDialog;
        if (alertDialog != null && this.currentGroupCreateAddress != null) {
            try {
                alertDialog.dismiss();
            } catch (Throwable e) {
                FileLog.e(e);
            }
            this.loadingDialog = null;
            openGroupCreate();
        }
    }

    private void showLoadingProgress(boolean show) {
        if (this.showingLoadingProgress != show) {
            this.showingLoadingProgress = show;
            AnimatorSet animatorSet = this.showProgressAnimation;
            if (animatorSet != null) {
                animatorSet.cancel();
                this.showProgressAnimation = null;
            }
            if (this.listView != null) {
                ArrayList<Animator> animators = new ArrayList<>();
                int count = this.listView.getChildCount();
                for (int a = 0; a < count; a++) {
                    View child = this.listView.getChildAt(a);
                    if (child instanceof HeaderCellProgress) {
                        HeaderCellProgress cell = (HeaderCellProgress) child;
                        this.animatingViews.add(cell);
                        RadialProgressView access$2400 = cell.progressView;
                        Property property = View.ALPHA;
                        float[] fArr = new float[1];
                        fArr[0] = show ? 1.0f : 0.0f;
                        animators.add(ObjectAnimator.ofFloat(access$2400, property, fArr));
                    }
                }
                if (animators.isEmpty() == 0) {
                    AnimatorSet animatorSet2 = new AnimatorSet();
                    this.showProgressAnimation = animatorSet2;
                    animatorSet2.playTogether(animators);
                    this.showProgressAnimation.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animation) {
                            AnimatorSet unused = PeopleNearbyActivity.this.showProgressAnimation = null;
                            PeopleNearbyActivity.this.animatingViews.clear();
                        }
                    });
                    this.showProgressAnimation.setDuration(180);
                    this.showProgressAnimation.start();
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void sendRequest(boolean shortpoll, int share) {
        Location location2;
        if (!this.firstLoaded) {
            PeopleNearbyActivity$$ExternalSyntheticLambda4 peopleNearbyActivity$$ExternalSyntheticLambda4 = new PeopleNearbyActivity$$ExternalSyntheticLambda4(this);
            this.showProgressRunnable = peopleNearbyActivity$$ExternalSyntheticLambda4;
            AndroidUtilities.runOnUIThread(peopleNearbyActivity$$ExternalSyntheticLambda4, 1000);
            this.firstLoaded = true;
        }
        Location location3 = getLocationController().getLastKnownLocation();
        if (location3 != null) {
            this.currentGroupCreateLocation = location3;
            int i = 0;
            if (!shortpoll && (location2 = this.lastLoadedLocation) != null) {
                float distance = location2.distanceTo(location3);
                if (BuildVars.DEBUG_VERSION) {
                    FileLog.d("located distance = " + distance);
                }
                if (share == 0 && (SystemClock.elapsedRealtime() - this.lastLoadedLocationTime < 3000 || this.lastLoadedLocation.distanceTo(location3) <= 20.0f)) {
                    return;
                }
                if (this.reqId != 0) {
                    getConnectionsManager().cancelRequest(this.reqId, true);
                    this.reqId = 0;
                }
            }
            if (this.reqId == 0) {
                this.lastLoadedLocation = location3;
                this.lastLoadedLocationTime = SystemClock.elapsedRealtime();
                LocationController.fetchLocationAddress(this.currentGroupCreateLocation, this);
                TLRPC.TL_contacts_getLocated req = new TLRPC.TL_contacts_getLocated();
                req.geo_point = new TLRPC.TL_inputGeoPoint();
                req.geo_point.lat = location3.getLatitude();
                req.geo_point._long = location3.getLongitude();
                if (share != 0) {
                    req.flags |= 1;
                    if (share == 1) {
                        i = Integer.MAX_VALUE;
                    }
                    req.self_expires = i;
                }
                this.reqId = getConnectionsManager().sendRequest(req, new PeopleNearbyActivity$$ExternalSyntheticLambda9(this, share));
                getConnectionsManager().bindRequestToGuid(this.reqId, this.classGuid);
            }
        }
    }

    /* renamed from: lambda$sendRequest$5$org-telegram-ui-PeopleNearbyActivity  reason: not valid java name */
    public /* synthetic */ void m4206lambda$sendRequest$5$orgtelegramuiPeopleNearbyActivity() {
        showLoadingProgress(true);
        this.showProgressRunnable = null;
    }

    /* renamed from: lambda$sendRequest$7$org-telegram-ui-PeopleNearbyActivity  reason: not valid java name */
    public /* synthetic */ void m4208lambda$sendRequest$7$orgtelegramuiPeopleNearbyActivity(int share, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new PeopleNearbyActivity$$ExternalSyntheticLambda5(this, share, error, response));
    }

    /* renamed from: lambda$sendRequest$6$org-telegram-ui-PeopleNearbyActivity  reason: not valid java name */
    public /* synthetic */ void m4207lambda$sendRequest$6$orgtelegramuiPeopleNearbyActivity(int share, TLRPC.TL_error error, TLObject response) {
        int i = share;
        this.reqId = 0;
        Runnable runnable = this.showProgressRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.showProgressRunnable = null;
        }
        showLoadingProgress(false);
        boolean saveConfig = false;
        UserConfig userConfig = getUserConfig();
        if (i == 1 && error != null) {
            userConfig.sharingMyLocationUntil = 0;
            saveConfig = true;
            updateRows((DiffCallback) null);
        }
        if (!(response == null || i == 2)) {
            TLRPC.Updates updates = (TLRPC.TL_updates) response;
            getMessagesController().putUsers(updates.users, false);
            getMessagesController().putChats(updates.chats, false);
            DiffCallback diffCallback = new DiffCallback();
            diffCallback.saveCurrentState();
            this.users.clear();
            this.chats.clear();
            if (userConfig.sharingMyLocationUntil != 0) {
                userConfig.lastMyLocationShareTime = (int) (System.currentTimeMillis() / 1000);
                saveConfig = true;
            }
            boolean hasSelf = false;
            int a = 0;
            int N = updates.updates.size();
            while (a < N) {
                TLRPC.Update baseUpdate = updates.updates.get(a);
                if (baseUpdate instanceof TLRPC.TL_updatePeerLocated) {
                    TLRPC.TL_updatePeerLocated update = (TLRPC.TL_updatePeerLocated) baseUpdate;
                    int b = 0;
                    int N2 = update.peers.size();
                    while (b < N2) {
                        TLRPC.PeerLocated object = update.peers.get(b);
                        if (object instanceof TLRPC.TL_peerLocated) {
                            TLRPC.TL_peerLocated peerLocated = (TLRPC.TL_peerLocated) object;
                            if (peerLocated.peer instanceof TLRPC.TL_peerUser) {
                                this.users.add(peerLocated);
                            } else {
                                this.chats.add(peerLocated);
                            }
                        } else if (object instanceof TLRPC.TL_peerSelfLocated) {
                            TLRPC.TL_peerSelfLocated peerSelfLocated = (TLRPC.TL_peerSelfLocated) object;
                            if (userConfig.sharingMyLocationUntil != peerSelfLocated.expires) {
                                userConfig.sharingMyLocationUntil = peerSelfLocated.expires;
                                saveConfig = true;
                                hasSelf = true;
                            } else {
                                hasSelf = true;
                            }
                        }
                        b++;
                        int i2 = share;
                    }
                }
                a++;
                int i3 = share;
            }
            if (!hasSelf && userConfig.sharingMyLocationUntil != 0) {
                userConfig.sharingMyLocationUntil = 0;
                saveConfig = true;
            }
            checkForExpiredLocations(true);
            updateRows(diffCallback);
        }
        if (saveConfig) {
            userConfig.saveConfig(false);
        }
        Runnable runnable2 = this.shortPollRunnable;
        if (runnable2 != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable2);
            AndroidUtilities.runOnUIThread(this.shortPollRunnable, 25000);
        }
    }

    public void onResume() {
        super.onResume();
        ListAdapter listAdapter = this.listViewAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
        getLocationController().startLocationLookupForPeopleNearby(false);
    }

    public void onPause() {
        super.onPause();
        UndoView undoView2 = this.undoView;
        if (undoView2 != null) {
            undoView2.hide(true, 0);
        }
        getLocationController().startLocationLookupForPeopleNearby(true);
    }

    /* access modifiers changed from: protected */
    public void onBecomeFullyHidden() {
        super.onBecomeFullyHidden();
        UndoView undoView2 = this.undoView;
        if (undoView2 != null) {
            undoView2.hide(true, 0);
        }
    }

    public void onLocationAddressAvailable(String address, String displayAddress, Location location2) {
        this.currentGroupCreateAddress = address;
        this.currentGroupCreateDisplayAddress = displayAddress;
        this.currentGroupCreateLocation = location2;
        ActionIntroActivity actionIntroActivity = this.groupCreateActivity;
        if (actionIntroActivity != null) {
            actionIntroActivity.setGroupCreateAddress(address, displayAddress, location2);
        }
        AlertDialog alertDialog = this.loadingDialog;
        if (alertDialog != null && !this.checkingCanCreate) {
            try {
                alertDialog.dismiss();
            } catch (Throwable e) {
                FileLog.e(e);
            }
            this.loadingDialog = null;
            openGroupCreate();
        }
    }

    /* access modifiers changed from: protected */
    public void onBecomeFullyVisible() {
        super.onBecomeFullyVisible();
        this.groupCreateActivity = null;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0068, code lost:
        if (r13.peer.user_id != r5.peer.user_id) goto L_0x006f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x0081, code lost:
        if (r13.peer.chat_id == r5.peer.chat_id) goto L_0x0097;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void didReceivedNotification(int r22, int r23, java.lang.Object... r24) {
        /*
            r21 = this;
            r6 = r21
            r7 = r22
            int r0 = org.telegram.messenger.NotificationCenter.newLocationAvailable
            r1 = 0
            if (r7 != r0) goto L_0x000e
            r6.sendRequest(r1, r1)
            goto L_0x0105
        L_0x000e:
            int r0 = org.telegram.messenger.NotificationCenter.newPeopleNearbyAvailable
            if (r7 != r0) goto L_0x00c1
            r0 = r24[r1]
            org.telegram.tgnet.TLRPC$TL_updatePeerLocated r0 = (org.telegram.tgnet.TLRPC.TL_updatePeerLocated) r0
            org.telegram.ui.PeopleNearbyActivity$DiffCallback r1 = new org.telegram.ui.PeopleNearbyActivity$DiffCallback
            r2 = 0
            r1.<init>()
            r1.saveCurrentState()
            r2 = 0
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PeerLocated> r3 = r0.peers
            int r3 = r3.size()
        L_0x0026:
            if (r2 >= r3) goto L_0x00b7
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PeerLocated> r4 = r0.peers
            java.lang.Object r4 = r4.get(r2)
            org.telegram.tgnet.TLRPC$PeerLocated r4 = (org.telegram.tgnet.TLRPC.PeerLocated) r4
            boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC.TL_peerLocated
            if (r5 == 0) goto L_0x00ad
            r5 = r4
            org.telegram.tgnet.TLRPC$TL_peerLocated r5 = (org.telegram.tgnet.TLRPC.TL_peerLocated) r5
            r9 = 0
            org.telegram.tgnet.TLRPC$Peer r10 = r5.peer
            boolean r10 = r10 instanceof org.telegram.tgnet.TLRPC.TL_peerUser
            if (r10 == 0) goto L_0x0041
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_peerLocated> r10 = r6.users
            goto L_0x0043
        L_0x0041:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_peerLocated> r10 = r6.chats
        L_0x0043:
            r11 = 0
            int r12 = r10.size()
        L_0x0048:
            if (r11 >= r12) goto L_0x00a3
            java.lang.Object r13 = r10.get(r11)
            org.telegram.tgnet.TLRPC$TL_peerLocated r13 = (org.telegram.tgnet.TLRPC.TL_peerLocated) r13
            org.telegram.tgnet.TLRPC$Peer r14 = r13.peer
            long r14 = r14.user_id
            r16 = 0
            int r18 = (r14 > r16 ? 1 : (r14 == r16 ? 0 : -1))
            if (r18 == 0) goto L_0x006b
            org.telegram.tgnet.TLRPC$Peer r14 = r13.peer
            long r14 = r14.user_id
            org.telegram.tgnet.TLRPC$Peer r8 = r5.peer
            r19 = r3
            r20 = r4
            long r3 = r8.user_id
            int r8 = (r14 > r3 ? 1 : (r14 == r3 ? 0 : -1))
            if (r8 == 0) goto L_0x0097
            goto L_0x006f
        L_0x006b:
            r19 = r3
            r20 = r4
        L_0x006f:
            org.telegram.tgnet.TLRPC$Peer r3 = r13.peer
            long r3 = r3.chat_id
            int r8 = (r3 > r16 ? 1 : (r3 == r16 ? 0 : -1))
            if (r8 == 0) goto L_0x0083
            org.telegram.tgnet.TLRPC$Peer r3 = r13.peer
            long r3 = r3.chat_id
            org.telegram.tgnet.TLRPC$Peer r8 = r5.peer
            long r14 = r8.chat_id
            int r8 = (r3 > r14 ? 1 : (r3 == r14 ? 0 : -1))
            if (r8 == 0) goto L_0x0097
        L_0x0083:
            org.telegram.tgnet.TLRPC$Peer r3 = r13.peer
            long r3 = r3.channel_id
            int r8 = (r3 > r16 ? 1 : (r3 == r16 ? 0 : -1))
            if (r8 == 0) goto L_0x009c
            org.telegram.tgnet.TLRPC$Peer r3 = r13.peer
            long r3 = r3.channel_id
            org.telegram.tgnet.TLRPC$Peer r8 = r5.peer
            long r14 = r8.channel_id
            int r8 = (r3 > r14 ? 1 : (r3 == r14 ? 0 : -1))
            if (r8 != 0) goto L_0x009c
        L_0x0097:
            r10.set(r11, r5)
            r3 = 1
            r9 = r3
        L_0x009c:
            int r11 = r11 + 1
            r3 = r19
            r4 = r20
            goto L_0x0048
        L_0x00a3:
            r19 = r3
            r20 = r4
            if (r9 != 0) goto L_0x00b1
            r10.add(r5)
            goto L_0x00b1
        L_0x00ad:
            r19 = r3
            r20 = r4
        L_0x00b1:
            int r2 = r2 + 1
            r3 = r19
            goto L_0x0026
        L_0x00b7:
            r19 = r3
            r2 = 1
            r6.checkForExpiredLocations(r2)
            r6.updateRows(r1)
            goto L_0x0104
        L_0x00c1:
            int r0 = org.telegram.messenger.NotificationCenter.needDeleteDialog
            if (r7 != r0) goto L_0x0104
            android.view.View r0 = r6.fragmentView
            if (r0 == 0) goto L_0x0103
            boolean r0 = r6.isPaused
            if (r0 == 0) goto L_0x00ce
            goto L_0x0103
        L_0x00ce:
            r0 = r24[r1]
            java.lang.Long r0 = (java.lang.Long) r0
            long r8 = r0.longValue()
            r0 = 1
            r1 = r24[r0]
            r10 = r1
            org.telegram.tgnet.TLRPC$User r10 = (org.telegram.tgnet.TLRPC.User) r10
            r0 = 2
            r0 = r24[r0]
            r11 = r0
            org.telegram.tgnet.TLRPC$Chat r11 = (org.telegram.tgnet.TLRPC.Chat) r11
            r0 = 3
            r0 = r24[r0]
            java.lang.Boolean r0 = (java.lang.Boolean) r0
            boolean r12 = r0.booleanValue()
            org.telegram.ui.PeopleNearbyActivity$$ExternalSyntheticLambda6 r13 = new org.telegram.ui.PeopleNearbyActivity$$ExternalSyntheticLambda6
            r0 = r13
            r1 = r21
            r2 = r11
            r3 = r8
            r5 = r12
            r0.<init>(r1, r2, r3, r5)
            org.telegram.ui.Components.UndoView r1 = r6.undoView
            if (r1 == 0) goto L_0x00ff
            r2 = 1
            r1.showWithAction((long) r8, (int) r2, (java.lang.Runnable) r0)
            goto L_0x0105
        L_0x00ff:
            r0.run()
            goto L_0x0105
        L_0x0103:
            return
        L_0x0104:
        L_0x0105:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PeopleNearbyActivity.didReceivedNotification(int, int, java.lang.Object[]):void");
    }

    /* renamed from: lambda$didReceivedNotification$8$org-telegram-ui-PeopleNearbyActivity  reason: not valid java name */
    public /* synthetic */ void m4204xa0217175(TLRPC.Chat chat, long dialogId, boolean revoke) {
        if (chat == null) {
            getMessagesController().deleteDialog(dialogId, 0, revoke);
        } else if (ChatObject.isNotInChat(chat)) {
            getMessagesController().deleteDialog(dialogId, 0, revoke);
        } else {
            getMessagesController().deleteParticipantFromChat(-dialogId, getMessagesController().getUser(Long.valueOf(getUserConfig().getClientUserId())), (TLRPC.Chat) null, (TLRPC.ChatFull) null, revoke, revoke);
        }
    }

    private void checkForExpiredLocations(boolean cache) {
        Runnable runnable = this.checkExpiredRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.checkExpiredRunnable = null;
        }
        int currentTime = getConnectionsManager().getCurrentTime();
        int minExpired = Integer.MAX_VALUE;
        boolean changed = false;
        DiffCallback callback = null;
        int a = 0;
        while (a < 2) {
            ArrayList<TLRPC.TL_peerLocated> arrayList = a == 0 ? this.users : this.chats;
            int b = 0;
            int N = arrayList.size();
            while (b < N) {
                TLRPC.TL_peerLocated peer = arrayList.get(b);
                if (peer.expires <= currentTime) {
                    if (callback == null) {
                        callback = new DiffCallback();
                        callback.saveCurrentState();
                    }
                    arrayList.remove(b);
                    b--;
                    N--;
                    changed = true;
                } else {
                    minExpired = Math.min(minExpired, peer.expires);
                }
                b++;
            }
            a++;
        }
        if (changed && this.listViewAdapter != null) {
            updateRows(callback);
        }
        if (changed || cache) {
            getLocationController().setCachedNearbyUsersAndChats(this.users, this.chats);
        }
        if (minExpired != Integer.MAX_VALUE) {
            PeopleNearbyActivity$$ExternalSyntheticLambda3 peopleNearbyActivity$$ExternalSyntheticLambda3 = new PeopleNearbyActivity$$ExternalSyntheticLambda3(this);
            this.checkExpiredRunnable = peopleNearbyActivity$$ExternalSyntheticLambda3;
            AndroidUtilities.runOnUIThread(peopleNearbyActivity$$ExternalSyntheticLambda3, (long) ((minExpired - currentTime) * 1000));
        }
    }

    /* renamed from: lambda$checkForExpiredLocations$9$org-telegram-ui-PeopleNearbyActivity  reason: not valid java name */
    public /* synthetic */ void m4200x726f6feb() {
        this.checkExpiredRunnable = null;
        checkForExpiredLocations(false);
    }

    public static class HeaderCellProgress extends HeaderCell {
        /* access modifiers changed from: private */
        public RadialProgressView progressView;

        public HeaderCellProgress(Context context) {
            super(context);
            setClipChildren(false);
            RadialProgressView radialProgressView = new RadialProgressView(context);
            this.progressView = radialProgressView;
            radialProgressView.setSize(AndroidUtilities.dp(14.0f));
            this.progressView.setStrokeWidth(2.0f);
            this.progressView.setAlpha(0.0f);
            this.progressView.setProgressColor(Theme.getColor("windowBackgroundWhiteBlueHeader"));
            addView(this.progressView, LayoutHelper.createFrame(50, 40.0f, (LocaleController.isRTL ? 3 : 5) | 48, LocaleController.isRTL ? 2.0f : 0.0f, 3.0f, LocaleController.isRTL ? 0.0f : 2.0f, 0.0f));
        }
    }

    public class HintInnerCell extends FrameLayout {
        private ImageView imageView;
        private TextView messageTextView;
        /* access modifiers changed from: private */
        public TextView titleTextView;

        public HintInnerCell(Context context) {
            super(context);
            int top = ((int) (((float) (ActionBar.getCurrentActionBarHeight() + (PeopleNearbyActivity.this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0))) / AndroidUtilities.density)) - 44;
            ImageView imageView2 = new ImageView(context);
            this.imageView = imageView2;
            imageView2.setBackgroundDrawable(Theme.createCircleDrawable(AndroidUtilities.dp(74.0f), Theme.getColor("chats_archiveBackground")));
            this.imageView.setImageDrawable(new ShareLocationDrawable(context, 2));
            this.imageView.setScaleType(ImageView.ScaleType.CENTER);
            addView(this.imageView, LayoutHelper.createFrame(74, 74.0f, 49, 0.0f, (float) (top + 27), 0.0f, 0.0f));
            TextView textView = new TextView(context);
            this.titleTextView = textView;
            textView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.titleTextView.setTextSize(1, 24.0f);
            this.titleTextView.setGravity(17);
            this.titleTextView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("PeopleNearby", NUM, new Object[0])));
            addView(this.titleTextView, LayoutHelper.createFrame(-1, -2.0f, 51, 17.0f, (float) (top + 120), 17.0f, 27.0f));
            TextView textView2 = new TextView(context);
            this.messageTextView = textView2;
            textView2.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText"));
            this.messageTextView.setTextSize(1, 15.0f);
            this.messageTextView.setGravity(17);
            this.messageTextView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("PeopleNearbyInfo2", NUM, new Object[0])));
            addView(this.messageTextView, LayoutHelper.createFrame(-1, -2.0f, 51, 40.0f, (float) (top + 161), 40.0f, 27.0f));
        }
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int type = holder.getItemViewType();
            return type == 0 || type == 2;
        }

        public int getItemCount() {
            return PeopleNearbyActivity.this.rowCount;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    view = new ManageChatUserCell(this.mContext, 6, 2, false);
                    view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    break;
                case 1:
                    view = new ShadowSectionCell(this.mContext);
                    break;
                case 2:
                    view = new ManageChatTextCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    break;
                case 3:
                    view = new HeaderCellProgress(this.mContext);
                    view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    break;
                case 4:
                    TextView textView = new TextView(this.mContext) {
                        /* access modifiers changed from: protected */
                        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(67.0f), NUM));
                        }
                    };
                    textView.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    textView.setPadding(0, 0, AndroidUtilities.dp(3.0f), 0);
                    textView.setTextSize(1, 14.0f);
                    textView.setGravity(17);
                    textView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText3"));
                    view = textView;
                    break;
                default:
                    view = new HintInnerCell(this.mContext);
                    view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    break;
            }
            view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(view);
        }

        public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
            if (holder.getItemViewType() == 3 && !PeopleNearbyActivity.this.animatingViews.contains(holder.itemView)) {
                ((HeaderCellProgress) holder.itemView).progressView.setAlpha(PeopleNearbyActivity.this.showingLoadingProgress ? 1.0f : 0.0f);
            }
        }

        private String formatDistance(TLRPC.TL_peerLocated located) {
            return LocaleController.formatDistance((float) located.distance, 0);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            long chatId;
            RecyclerView.ViewHolder viewHolder = holder;
            int i = position;
            boolean z = true;
            switch (holder.getItemViewType()) {
                case 0:
                    ManageChatUserCell userCell = (ManageChatUserCell) viewHolder.itemView;
                    userCell.setTag(Integer.valueOf(position));
                    if (i >= PeopleNearbyActivity.this.usersStartRow && i < PeopleNearbyActivity.this.usersEndRow) {
                        TLRPC.TL_peerLocated peerLocated = (TLRPC.TL_peerLocated) PeopleNearbyActivity.this.users.get(i - PeopleNearbyActivity.this.usersStartRow);
                        TLRPC.User user = PeopleNearbyActivity.this.getMessagesController().getUser(Long.valueOf(peerLocated.peer.user_id));
                        if (user != null) {
                            String formatDistance = formatDistance(peerLocated);
                            if (PeopleNearbyActivity.this.showMoreRow == -1 && i == PeopleNearbyActivity.this.usersEndRow - 1) {
                                z = false;
                            }
                            userCell.setData(user, (CharSequence) null, formatDistance, z);
                            return;
                        }
                        return;
                    } else if (i >= PeopleNearbyActivity.this.chatsStartRow && i < PeopleNearbyActivity.this.chatsEndRow) {
                        int index = i - PeopleNearbyActivity.this.chatsStartRow;
                        TLRPC.TL_peerLocated peerLocated2 = (TLRPC.TL_peerLocated) PeopleNearbyActivity.this.chats.get(index);
                        if (peerLocated2.peer instanceof TLRPC.TL_peerChat) {
                            chatId = peerLocated2.peer.chat_id;
                        } else {
                            chatId = peerLocated2.peer.channel_id;
                        }
                        TLRPC.Chat chat = PeopleNearbyActivity.this.getMessagesController().getChat(Long.valueOf(chatId));
                        if (chat != null) {
                            String subtitle = formatDistance(peerLocated2);
                            if (chat.participants_count != 0) {
                                subtitle = String.format("%1$s, %2$s", new Object[]{subtitle, LocaleController.formatPluralString("Members", chat.participants_count, new Object[0])});
                            }
                            if (index == PeopleNearbyActivity.this.chats.size() - 1) {
                                z = false;
                            }
                            userCell.setData(chat, (CharSequence) null, subtitle, z);
                            return;
                        }
                        return;
                    } else {
                        return;
                    }
                case 1:
                    ShadowSectionCell privacyCell = (ShadowSectionCell) viewHolder.itemView;
                    if (i == PeopleNearbyActivity.this.usersSectionRow) {
                        privacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                        return;
                    } else if (i == PeopleNearbyActivity.this.chatsSectionRow) {
                        privacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                        return;
                    } else if (i == PeopleNearbyActivity.this.helpSectionRow) {
                        privacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                        return;
                    } else {
                        return;
                    }
                case 2:
                    ManageChatTextCell actionCell = (ManageChatTextCell) viewHolder.itemView;
                    actionCell.setColors("windowBackgroundWhiteBlueIcon", "windowBackgroundWhiteBlueButton");
                    if (i == PeopleNearbyActivity.this.chatsCreateRow) {
                        String string = LocaleController.getString("NearbyCreateGroup", NUM);
                        if (PeopleNearbyActivity.this.chatsStartRow == -1) {
                            z = false;
                        }
                        actionCell.setText(string, (String) null, NUM, z);
                        return;
                    } else if (i == PeopleNearbyActivity.this.showMeRow) {
                        PeopleNearbyActivity peopleNearbyActivity = PeopleNearbyActivity.this;
                        if (peopleNearbyActivity.showingMe = peopleNearbyActivity.getUserConfig().sharingMyLocationUntil > PeopleNearbyActivity.this.getConnectionsManager().getCurrentTime()) {
                            String string2 = LocaleController.getString("StopShowingMe", NUM);
                            if (PeopleNearbyActivity.this.usersStartRow == -1) {
                                z = false;
                            }
                            actionCell.setText(string2, (String) null, NUM, z);
                            actionCell.setColors("windowBackgroundWhiteRedText5", "windowBackgroundWhiteRedText5");
                            return;
                        }
                        String string3 = LocaleController.getString("MakeMyselfVisible", NUM);
                        if (PeopleNearbyActivity.this.usersStartRow == -1) {
                            z = false;
                        }
                        actionCell.setText(string3, (String) null, NUM, z);
                        return;
                    } else if (i == PeopleNearbyActivity.this.showMoreRow) {
                        actionCell.setText(LocaleController.formatPluralString("ShowVotes", PeopleNearbyActivity.this.users.size() - 5, new Object[0]), (String) null, NUM, false);
                        return;
                    } else {
                        return;
                    }
                case 3:
                    HeaderCellProgress headerCell = (HeaderCellProgress) viewHolder.itemView;
                    if (i == PeopleNearbyActivity.this.usersHeaderRow) {
                        headerCell.setText(LocaleController.getString("PeopleNearbyHeader", NUM));
                        return;
                    } else if (i == PeopleNearbyActivity.this.chatsHeaderRow) {
                        headerCell.setText(LocaleController.getString("ChatsNearbyHeader", NUM));
                        return;
                    } else {
                        return;
                    }
                default:
                    return;
            }
        }

        public void onViewRecycled(RecyclerView.ViewHolder holder) {
            if (holder.itemView instanceof ManageChatUserCell) {
                ((ManageChatUserCell) holder.itemView).recycle();
            }
        }

        public int getItemViewType(int position) {
            if (position == PeopleNearbyActivity.this.helpRow) {
                return 5;
            }
            if (position == PeopleNearbyActivity.this.chatsCreateRow || position == PeopleNearbyActivity.this.showMeRow || position == PeopleNearbyActivity.this.showMoreRow) {
                return 2;
            }
            if (position == PeopleNearbyActivity.this.usersHeaderRow || position == PeopleNearbyActivity.this.chatsHeaderRow) {
                return 3;
            }
            if (position == PeopleNearbyActivity.this.usersSectionRow || position == PeopleNearbyActivity.this.chatsSectionRow || position == PeopleNearbyActivity.this.helpSectionRow) {
                return 1;
            }
            return 0;
        }
    }

    public boolean isLightStatusBar() {
        if (ColorUtils.calculateLuminance(Theme.getColor("windowBackgroundWhite", (boolean[]) null, true)) > 0.699999988079071d) {
            return true;
        }
        return false;
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();
        ThemeDescription.ThemeDescriptionDelegate cellDelegate = new PeopleNearbyActivity$$ExternalSyntheticLambda10(this);
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{ManageChatUserCell.class, ManageChatTextCell.class, HeaderCell.class, TextView.class, HintInnerCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        themeDescriptions.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND | ThemeDescription.FLAG_CHECKTAG, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"));
        themeDescriptions.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND | ThemeDescription.FLAG_CHECKTAG, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        themeDescriptions.add(new ThemeDescription(this.actionBarBackground, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "divider"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueHeader"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_PROGRESSBAR, new Class[]{HeaderCellProgress.class}, new String[]{"progressView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueHeader"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{ManageChatUserCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{ManageChatUserCell.class}, new String[]{"statusColor"}, (Paint[]) null, (Drawable[]) null, cellDelegate, "windowBackgroundWhiteGrayText"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{ManageChatUserCell.class}, new String[]{"statusOnlineColor"}, (Paint[]) null, (Drawable[]) null, cellDelegate, "windowBackgroundWhiteBlueText"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{ManageChatUserCell.class}, (Paint) null, Theme.avatarDrawables, (ThemeDescription.ThemeDescriptionDelegate) null, "avatar_text"));
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate = cellDelegate;
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, "avatar_backgroundRed"));
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate2 = cellDelegate;
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, "avatar_backgroundOrange"));
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, "avatar_backgroundViolet"));
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, "avatar_backgroundGreen"));
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, "avatar_backgroundCyan"));
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate2, "avatar_backgroundBlue"));
        themeDescriptions.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, themeDescriptionDelegate, "avatar_backgroundPink"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE, new Class[]{HintInnerCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_archiveBackground"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{HintInnerCell.class}, new String[]{"messageTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_message"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayIcon"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueButton"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueIcon"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteRedText5"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteRedText5"));
        themeDescriptions.add(new ThemeDescription(this.undoView, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_background"));
        themeDescriptions.add(new ThemeDescription((View) this.undoView, 0, new Class[]{UndoView.class}, new String[]{"undoImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_cancelColor"));
        themeDescriptions.add(new ThemeDescription((View) this.undoView, 0, new Class[]{UndoView.class}, new String[]{"undoTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_cancelColor"));
        themeDescriptions.add(new ThemeDescription((View) this.undoView, 0, new Class[]{UndoView.class}, new String[]{"infoTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_infoColor"));
        themeDescriptions.add(new ThemeDescription((View) this.undoView, 0, new Class[]{UndoView.class}, new String[]{"subinfoTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_infoColor"));
        themeDescriptions.add(new ThemeDescription((View) this.undoView, 0, new Class[]{UndoView.class}, new String[]{"textPaint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_infoColor"));
        themeDescriptions.add(new ThemeDescription((View) this.undoView, 0, new Class[]{UndoView.class}, new String[]{"progressPaint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_infoColor"));
        return themeDescriptions;
    }

    /* renamed from: lambda$getThemeDescriptions$10$org-telegram-ui-PeopleNearbyActivity  reason: not valid java name */
    public /* synthetic */ void m4205xb9b21fb() {
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            int count = recyclerListView.getChildCount();
            for (int a = 0; a < count; a++) {
                View child = this.listView.getChildAt(a);
                if (child instanceof ManageChatUserCell) {
                    ((ManageChatUserCell) child).update(0);
                }
            }
        }
    }
}
