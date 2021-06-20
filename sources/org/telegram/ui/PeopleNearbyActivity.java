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
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$ChatFull;
import org.telegram.tgnet.TLRPC$Peer;
import org.telegram.tgnet.TLRPC$PeerLocated;
import org.telegram.tgnet.TLRPC$TL_channels_getAdminedPublicChannels;
import org.telegram.tgnet.TLRPC$TL_contacts_getLocated;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_inputGeoPoint;
import org.telegram.tgnet.TLRPC$TL_peerChat;
import org.telegram.tgnet.TLRPC$TL_peerLocated;
import org.telegram.tgnet.TLRPC$TL_peerSelfLocated;
import org.telegram.tgnet.TLRPC$TL_peerUser;
import org.telegram.tgnet.TLRPC$TL_updatePeerLocated;
import org.telegram.tgnet.TLRPC$TL_updates;
import org.telegram.tgnet.TLRPC$Update;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
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
    /* access modifiers changed from: private */
    public AnimatorSet actionBarAnimator;
    /* access modifiers changed from: private */
    public View actionBarBackground;
    /* access modifiers changed from: private */
    public ArrayList<View> animatingViews = new ArrayList<>();
    private boolean canCreateGroup;
    /* access modifiers changed from: private */
    public ArrayList<TLRPC$TL_peerLocated> chats = new ArrayList<>(getLocationController().getCachedNearbyChats());
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
    public ArrayList<TLRPC$TL_peerLocated> users = new ArrayList<>(getLocationController().getCachedNearbyUsers());
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
        int i;
        this.rowCount = 0;
        this.usersStartRow = -1;
        this.usersEndRow = -1;
        this.showMoreRow = -1;
        this.chatsStartRow = -1;
        this.chatsEndRow = -1;
        this.chatsCreateRow = -1;
        this.showMeRow = -1;
        int i2 = 0 + 1;
        this.rowCount = i2;
        this.helpRow = 0;
        int i3 = i2 + 1;
        this.rowCount = i3;
        this.helpSectionRow = i2;
        int i4 = i3 + 1;
        this.rowCount = i4;
        this.usersHeaderRow = i3;
        this.rowCount = i4 + 1;
        this.showMeRow = i4;
        if (!this.users.isEmpty()) {
            if (this.expanded) {
                i = this.users.size();
            } else {
                i = Math.min(5, this.users.size());
            }
            int i5 = this.rowCount;
            this.usersStartRow = i5;
            int i6 = i5 + i;
            this.rowCount = i6;
            this.usersEndRow = i6;
            if (i != this.users.size()) {
                int i7 = this.rowCount;
                this.rowCount = i7 + 1;
                this.showMoreRow = i7;
            }
        }
        int i8 = this.rowCount;
        int i9 = i8 + 1;
        this.rowCount = i9;
        this.usersSectionRow = i8;
        int i10 = i9 + 1;
        this.rowCount = i10;
        this.chatsHeaderRow = i9;
        this.rowCount = i10 + 1;
        this.chatsCreateRow = i10;
        if (!this.chats.isEmpty()) {
            int i11 = this.rowCount;
            this.chatsStartRow = i11;
            int size = i11 + this.chats.size();
            this.rowCount = size;
            this.chatsEndRow = size;
        }
        int i12 = this.rowCount;
        this.rowCount = i12 + 1;
        this.chatsSectionRow = i12;
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
        private final ArrayList<TLRPC$TL_peerLocated> oldChats;
        int oldChatsEndRow;
        int oldChatsStartRow;
        SparseIntArray oldPositionToItem;
        int oldRowCount;
        private final ArrayList<TLRPC$TL_peerLocated> oldUsers;
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

        public boolean areItemsTheSame(int i, int i2) {
            int i3;
            int i4;
            if (i2 < PeopleNearbyActivity.this.usersStartRow || i2 >= PeopleNearbyActivity.this.usersEndRow || i < (i4 = this.oldUsersStartRow) || i >= this.oldUsersEndRow) {
                if (i2 < PeopleNearbyActivity.this.chatsStartRow || i2 >= PeopleNearbyActivity.this.chatsEndRow || i < (i3 = this.oldChatsStartRow) || i >= this.oldChatsEndRow) {
                    int i5 = this.oldPositionToItem.get(i, -1);
                    if (i5 != this.newPositionToItem.get(i2, -1) || i5 < 0) {
                        return false;
                    }
                    return true;
                } else if (MessageObject.getPeerId(this.oldChats.get(i - i3).peer) == MessageObject.getPeerId(((TLRPC$TL_peerLocated) PeopleNearbyActivity.this.chats.get(i2 - PeopleNearbyActivity.this.chatsStartRow)).peer)) {
                    return true;
                } else {
                    return false;
                }
            } else if (MessageObject.getPeerId(this.oldUsers.get(i - i4).peer) == MessageObject.getPeerId(((TLRPC$TL_peerLocated) PeopleNearbyActivity.this.users.get(i2 - PeopleNearbyActivity.this.usersStartRow)).peer)) {
                return true;
            } else {
                return false;
            }
        }

        public boolean areContentsTheSame(int i, int i2) {
            return areItemsTheSame(i, i2);
        }

        public void fillPositions(SparseIntArray sparseIntArray) {
            sparseIntArray.clear();
            put(1, PeopleNearbyActivity.this.helpRow, sparseIntArray);
            put(2, PeopleNearbyActivity.this.helpSectionRow, sparseIntArray);
            put(3, PeopleNearbyActivity.this.usersHeaderRow, sparseIntArray);
            put(4, PeopleNearbyActivity.this.showMoreRow, sparseIntArray);
            put(5, PeopleNearbyActivity.this.usersSectionRow, sparseIntArray);
            put(6, PeopleNearbyActivity.this.chatsHeaderRow, sparseIntArray);
            put(7, PeopleNearbyActivity.this.chatsCreateRow, sparseIntArray);
            put(8, PeopleNearbyActivity.this.chatsSectionRow, sparseIntArray);
            put(9, PeopleNearbyActivity.this.showMeRow, sparseIntArray);
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

        private void put(int i, int i2, SparseIntArray sparseIntArray) {
            if (i2 >= 0) {
                sparseIntArray.put(i2, i);
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
        if (!AndroidUtilities.isTablet()) {
            this.actionBar.showActionModeTop();
        }
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    PeopleNearbyActivity.this.finishFragment();
                }
            }
        });
        AnonymousClass3 r0 = new FrameLayout(context) {
            /* access modifiers changed from: protected */
            public void onMeasure(int i, int i2) {
                ((FrameLayout.LayoutParams) PeopleNearbyActivity.this.actionBarBackground.getLayoutParams()).height = ActionBar.getCurrentActionBarHeight() + (PeopleNearbyActivity.this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0) + AndroidUtilities.dp(3.0f);
                super.onMeasure(i, i2);
            }

            /* access modifiers changed from: protected */
            public void onLayout(boolean z, int i, int i2, int i3, int i4) {
                super.onLayout(z, i, i2, i3, i4);
                PeopleNearbyActivity.this.checkScroll(false);
            }
        };
        this.fragmentView = r0;
        r0.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
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
            public long getAddAnimationDelay(long j, long j2, long j3) {
                return j;
            }
        };
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
            public final void onItemClick(View view, int i) {
                PeopleNearbyActivity.this.lambda$createView$2$PeopleNearbyActivity(view, i);
            }
        });
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                PeopleNearbyActivity.this.checkScroll(true);
            }
        });
        AnonymousClass6 r3 = new View(context) {
            private Paint paint = new Paint();

            /* access modifiers changed from: protected */
            public void onDraw(Canvas canvas) {
                this.paint.setColor(Theme.getColor("windowBackgroundWhite"));
                int measuredHeight = getMeasuredHeight() - AndroidUtilities.dp(3.0f);
                canvas.drawRect(0.0f, 0.0f, (float) getMeasuredWidth(), (float) measuredHeight, this.paint);
                PeopleNearbyActivity.this.parentLayout.drawHeaderShadow(canvas, measuredHeight);
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$2 */
    public /* synthetic */ void lambda$createView$2$PeopleNearbyActivity(View view, int i) {
        int i2;
        if (getParentActivity() != null) {
            int i3 = this.usersStartRow;
            if (i < i3 || i >= this.usersEndRow) {
                int i4 = this.chatsStartRow;
                if (i >= i4 && i < this.chatsEndRow) {
                    Bundle bundle = new Bundle();
                    TLRPC$Peer tLRPC$Peer = this.chats.get(i - i4).peer;
                    if (tLRPC$Peer instanceof TLRPC$TL_peerChat) {
                        i2 = tLRPC$Peer.chat_id;
                    } else {
                        i2 = tLRPC$Peer.channel_id;
                    }
                    bundle.putInt("chat_id", i2);
                    presentFragment(new ChatActivity(bundle));
                } else if (i == this.chatsCreateRow) {
                    if (this.checkingCanCreate || this.currentGroupCreateAddress == null) {
                        AlertDialog alertDialog = new AlertDialog(getParentActivity(), 3);
                        this.loadingDialog = alertDialog;
                        alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                            public final void onCancel(DialogInterface dialogInterface) {
                                PeopleNearbyActivity.this.lambda$createView$0$PeopleNearbyActivity(dialogInterface);
                            }
                        });
                        this.loadingDialog.show();
                        return;
                    }
                    openGroupCreate();
                } else if (i == this.showMeRow) {
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
                        builder.setPositiveButton(LocaleController.getString("OK", NUM), new DialogInterface.OnClickListener(userConfig) {
                            public final /* synthetic */ UserConfig f$1;

                            {
                                this.f$1 = r2;
                            }

                            public final void onClick(DialogInterface dialogInterface, int i) {
                                PeopleNearbyActivity.this.lambda$createView$1$PeopleNearbyActivity(this.f$1, dialogInterface, i);
                            }
                        });
                        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                        showDialog(builder.create());
                    }
                    userConfig.saveConfig(false);
                } else if (i == this.showMoreRow) {
                    this.expanded = true;
                    DiffCallback diffCallback = new DiffCallback();
                    diffCallback.saveCurrentState();
                    updateRows(diffCallback);
                }
            } else if (view instanceof ManageChatUserCell) {
                TLRPC$TL_peerLocated tLRPC$TL_peerLocated = this.users.get(i - i3);
                Bundle bundle2 = new Bundle();
                bundle2.putInt("user_id", tLRPC$TL_peerLocated.peer.user_id);
                if (((ManageChatUserCell) view).hasAvatarSet()) {
                    bundle2.putBoolean("expandPhoto", true);
                }
                bundle2.putInt("nearby_distance", tLRPC$TL_peerLocated.distance);
                MessagesController.getInstance(this.currentAccount).ensureMessagesLoaded((long) tLRPC$TL_peerLocated.peer.user_id, 0, (MessagesController.MessagesLoadedCallback) null);
                presentFragment(new ProfileActivity(bundle2));
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$0 */
    public /* synthetic */ void lambda$createView$0$PeopleNearbyActivity(DialogInterface dialogInterface) {
        this.loadingDialog = null;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$1 */
    public /* synthetic */ void lambda$createView$1$PeopleNearbyActivity(UserConfig userConfig, DialogInterface dialogInterface, int i) {
        userConfig.sharingMyLocationUntil = Integer.MAX_VALUE;
        userConfig.saveConfig(false);
        sendRequest(false, 1);
        updateRows((DiffCallback) null);
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:10:0x0041  */
    /* JADX WARNING: Removed duplicated region for block: B:11:0x0043  */
    /* JADX WARNING: Removed duplicated region for block: B:13:0x0046  */
    /* JADX WARNING: Removed duplicated region for block: B:39:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void checkScroll(boolean r11) {
        /*
            r10 = this;
            androidx.recyclerview.widget.LinearLayoutManager r0 = r10.layoutManager
            int r0 = r0.findFirstVisibleItemPosition()
            r1 = 0
            r2 = 1
            if (r0 == 0) goto L_0x000c
        L_0x000a:
            r0 = 1
            goto L_0x0039
        L_0x000c:
            org.telegram.ui.Components.RecyclerListView r3 = r10.listView
            androidx.recyclerview.widget.RecyclerView$ViewHolder r0 = r3.findViewHolderForAdapterPosition(r0)
            if (r0 != 0) goto L_0x0015
            goto L_0x000a
        L_0x0015:
            android.view.View r0 = r0.itemView
            org.telegram.ui.PeopleNearbyActivity$HintInnerCell r0 = (org.telegram.ui.PeopleNearbyActivity.HintInnerCell) r0
            android.widget.TextView r3 = r0.titleTextView
            int[] r4 = r10.location
            r3.getLocationOnScreen(r4)
            int[] r3 = r10.location
            r3 = r3[r2]
            android.widget.TextView r0 = r0.titleTextView
            int r0 = r0.getMeasuredHeight()
            int r3 = r3 + r0
            org.telegram.ui.ActionBar.ActionBar r0 = r10.actionBar
            int r0 = r0.getBottom()
            if (r3 >= r0) goto L_0x0038
            goto L_0x000a
        L_0x0038:
            r0 = 0
        L_0x0039:
            android.view.View r3 = r10.actionBarBackground
            java.lang.Object r3 = r3.getTag()
            if (r3 != 0) goto L_0x0043
            r3 = 1
            goto L_0x0044
        L_0x0043:
            r3 = 0
        L_0x0044:
            if (r0 == r3) goto L_0x00c8
            android.view.View r3 = r10.actionBarBackground
            r4 = 0
            if (r0 == 0) goto L_0x004d
            r5 = r4
            goto L_0x0051
        L_0x004d:
            java.lang.Integer r5 = java.lang.Integer.valueOf(r2)
        L_0x0051:
            r3.setTag(r5)
            android.animation.AnimatorSet r3 = r10.actionBarAnimator
            if (r3 == 0) goto L_0x005d
            r3.cancel()
            r10.actionBarAnimator = r4
        L_0x005d:
            r3 = 1065353216(0x3var_, float:1.0)
            r4 = 0
            if (r11 == 0) goto L_0x00b0
            android.animation.AnimatorSet r11 = new android.animation.AnimatorSet
            r11.<init>()
            r10.actionBarAnimator = r11
            r5 = 2
            android.animation.Animator[] r5 = new android.animation.Animator[r5]
            android.view.View r6 = r10.actionBarBackground
            android.util.Property r7 = android.view.View.ALPHA
            float[] r8 = new float[r2]
            if (r0 == 0) goto L_0x0077
            r9 = 1065353216(0x3var_, float:1.0)
            goto L_0x0078
        L_0x0077:
            r9 = 0
        L_0x0078:
            r8[r1] = r9
            android.animation.ObjectAnimator r6 = android.animation.ObjectAnimator.ofFloat(r6, r7, r8)
            r5[r1] = r6
            org.telegram.ui.ActionBar.ActionBar r6 = r10.actionBar
            org.telegram.ui.ActionBar.SimpleTextView r6 = r6.getTitleTextView()
            android.util.Property r7 = android.view.View.ALPHA
            float[] r8 = new float[r2]
            if (r0 == 0) goto L_0x008d
            goto L_0x008e
        L_0x008d:
            r3 = 0
        L_0x008e:
            r8[r1] = r3
            android.animation.ObjectAnimator r0 = android.animation.ObjectAnimator.ofFloat(r6, r7, r8)
            r5[r2] = r0
            r11.playTogether(r5)
            android.animation.AnimatorSet r11 = r10.actionBarAnimator
            r0 = 150(0x96, double:7.4E-322)
            r11.setDuration(r0)
            android.animation.AnimatorSet r11 = r10.actionBarAnimator
            org.telegram.ui.PeopleNearbyActivity$7 r0 = new org.telegram.ui.PeopleNearbyActivity$7
            r0.<init>()
            r11.addListener(r0)
            android.animation.AnimatorSet r11 = r10.actionBarAnimator
            r11.start()
            goto L_0x00c8
        L_0x00b0:
            android.view.View r11 = r10.actionBarBackground
            if (r0 == 0) goto L_0x00b7
            r1 = 1065353216(0x3var_, float:1.0)
            goto L_0x00b8
        L_0x00b7:
            r1 = 0
        L_0x00b8:
            r11.setAlpha(r1)
            org.telegram.ui.ActionBar.ActionBar r11 = r10.actionBar
            org.telegram.ui.ActionBar.SimpleTextView r11 = r11.getTitleTextView()
            if (r0 == 0) goto L_0x00c4
            goto L_0x00c5
        L_0x00c4:
            r3 = 0
        L_0x00c5:
            r11.setAlpha(r3)
        L_0x00c8:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PeopleNearbyActivity.checkScroll(boolean):void");
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
            TLRPC$TL_channels_getAdminedPublicChannels tLRPC$TL_channels_getAdminedPublicChannels = new TLRPC$TL_channels_getAdminedPublicChannels();
            tLRPC$TL_channels_getAdminedPublicChannels.by_location = true;
            tLRPC$TL_channels_getAdminedPublicChannels.check_limit = true;
            getConnectionsManager().bindRequestToGuid(getConnectionsManager().sendRequest(tLRPC$TL_channels_getAdminedPublicChannels, new RequestDelegate() {
                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    PeopleNearbyActivity.this.lambda$checkCanCreateGroup$4$PeopleNearbyActivity(tLObject, tLRPC$TL_error);
                }
            }), this.classGuid);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$checkCanCreateGroup$4 */
    public /* synthetic */ void lambda$checkCanCreateGroup$4$PeopleNearbyActivity(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLRPC$TL_error) {
            public final /* synthetic */ TLRPC$TL_error f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                PeopleNearbyActivity.this.lambda$checkCanCreateGroup$3$PeopleNearbyActivity(this.f$1);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$checkCanCreateGroup$3 */
    public /* synthetic */ void lambda$checkCanCreateGroup$3$PeopleNearbyActivity(TLRPC$TL_error tLRPC$TL_error) {
        this.canCreateGroup = tLRPC$TL_error == null;
        this.checkingCanCreate = false;
        AlertDialog alertDialog = this.loadingDialog;
        if (alertDialog != null && this.currentGroupCreateAddress != null) {
            try {
                alertDialog.dismiss();
            } catch (Throwable th) {
                FileLog.e(th);
            }
            this.loadingDialog = null;
            openGroupCreate();
        }
    }

    private void showLoadingProgress(boolean z) {
        if (this.showingLoadingProgress != z) {
            this.showingLoadingProgress = z;
            AnimatorSet animatorSet = this.showProgressAnimation;
            if (animatorSet != null) {
                animatorSet.cancel();
                this.showProgressAnimation = null;
            }
            if (this.listView != null) {
                ArrayList arrayList = new ArrayList();
                int childCount = this.listView.getChildCount();
                for (int i = 0; i < childCount; i++) {
                    View childAt = this.listView.getChildAt(i);
                    if (childAt instanceof HeaderCellProgress) {
                        HeaderCellProgress headerCellProgress = (HeaderCellProgress) childAt;
                        this.animatingViews.add(headerCellProgress);
                        RadialProgressView access$2400 = headerCellProgress.progressView;
                        Property property = View.ALPHA;
                        float[] fArr = new float[1];
                        fArr[0] = z ? 1.0f : 0.0f;
                        arrayList.add(ObjectAnimator.ofFloat(access$2400, property, fArr));
                    }
                }
                if (!arrayList.isEmpty()) {
                    AnimatorSet animatorSet2 = new AnimatorSet();
                    this.showProgressAnimation = animatorSet2;
                    animatorSet2.playTogether(arrayList);
                    this.showProgressAnimation.addListener(new AnimatorListenerAdapter() {
                        public void onAnimationEnd(Animator animator) {
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
    public void sendRequest(boolean z, int i) {
        Location location2;
        if (!this.firstLoaded) {
            $$Lambda$PeopleNearbyActivity$cBxPChAbEY73YmrCx1LFVVO0wwU r0 = new Runnable() {
                public final void run() {
                    PeopleNearbyActivity.this.lambda$sendRequest$5$PeopleNearbyActivity();
                }
            };
            this.showProgressRunnable = r0;
            AndroidUtilities.runOnUIThread(r0, 1000);
            this.firstLoaded = true;
        }
        Location lastKnownLocation = getLocationController().getLastKnownLocation();
        if (lastKnownLocation != null) {
            this.currentGroupCreateLocation = lastKnownLocation;
            int i2 = 0;
            if (!z && (location2 = this.lastLoadedLocation) != null) {
                float distanceTo = location2.distanceTo(lastKnownLocation);
                if (BuildVars.DEBUG_VERSION) {
                    FileLog.d("located distance = " + distanceTo);
                }
                if (i == 0 && (SystemClock.elapsedRealtime() - this.lastLoadedLocationTime < 3000 || this.lastLoadedLocation.distanceTo(lastKnownLocation) <= 20.0f)) {
                    return;
                }
                if (this.reqId != 0) {
                    getConnectionsManager().cancelRequest(this.reqId, true);
                    this.reqId = 0;
                }
            }
            if (this.reqId == 0) {
                this.lastLoadedLocation = lastKnownLocation;
                this.lastLoadedLocationTime = SystemClock.elapsedRealtime();
                LocationController.fetchLocationAddress(this.currentGroupCreateLocation, this);
                TLRPC$TL_contacts_getLocated tLRPC$TL_contacts_getLocated = new TLRPC$TL_contacts_getLocated();
                TLRPC$TL_inputGeoPoint tLRPC$TL_inputGeoPoint = new TLRPC$TL_inputGeoPoint();
                tLRPC$TL_contacts_getLocated.geo_point = tLRPC$TL_inputGeoPoint;
                tLRPC$TL_inputGeoPoint.lat = lastKnownLocation.getLatitude();
                tLRPC$TL_contacts_getLocated.geo_point._long = lastKnownLocation.getLongitude();
                if (i != 0) {
                    tLRPC$TL_contacts_getLocated.flags |= 1;
                    if (i == 1) {
                        i2 = Integer.MAX_VALUE;
                    }
                    tLRPC$TL_contacts_getLocated.self_expires = i2;
                }
                this.reqId = getConnectionsManager().sendRequest(tLRPC$TL_contacts_getLocated, new RequestDelegate(i) {
                    public final /* synthetic */ int f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                        PeopleNearbyActivity.this.lambda$sendRequest$7$PeopleNearbyActivity(this.f$1, tLObject, tLRPC$TL_error);
                    }
                });
                getConnectionsManager().bindRequestToGuid(this.reqId, this.classGuid);
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$sendRequest$5 */
    public /* synthetic */ void lambda$sendRequest$5$PeopleNearbyActivity() {
        showLoadingProgress(true);
        this.showProgressRunnable = null;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$sendRequest$7 */
    public /* synthetic */ void lambda$sendRequest$7$PeopleNearbyActivity(int i, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(i, tLRPC$TL_error, tLObject) {
            public final /* synthetic */ int f$1;
            public final /* synthetic */ TLRPC$TL_error f$2;
            public final /* synthetic */ TLObject f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                PeopleNearbyActivity.this.lambda$sendRequest$6$PeopleNearbyActivity(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$sendRequest$6 */
    public /* synthetic */ void lambda$sendRequest$6$PeopleNearbyActivity(int i, TLRPC$TL_error tLRPC$TL_error, TLObject tLObject) {
        boolean z;
        this.reqId = 0;
        Runnable runnable = this.showProgressRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.showProgressRunnable = null;
        }
        showLoadingProgress(false);
        UserConfig userConfig = getUserConfig();
        if (i != 1 || tLRPC$TL_error == null) {
            z = false;
        } else {
            userConfig.sharingMyLocationUntil = 0;
            updateRows((DiffCallback) null);
            z = true;
        }
        if (!(tLObject == null || i == 2)) {
            TLRPC$TL_updates tLRPC$TL_updates = (TLRPC$TL_updates) tLObject;
            getMessagesController().putUsers(tLRPC$TL_updates.users, false);
            getMessagesController().putChats(tLRPC$TL_updates.chats, false);
            DiffCallback diffCallback = new DiffCallback();
            diffCallback.saveCurrentState();
            this.users.clear();
            this.chats.clear();
            if (userConfig.sharingMyLocationUntil != 0) {
                userConfig.lastMyLocationShareTime = (int) (System.currentTimeMillis() / 1000);
                z = true;
            }
            int size = tLRPC$TL_updates.updates.size();
            boolean z2 = false;
            for (int i2 = 0; i2 < size; i2++) {
                TLRPC$Update tLRPC$Update = tLRPC$TL_updates.updates.get(i2);
                if (tLRPC$Update instanceof TLRPC$TL_updatePeerLocated) {
                    TLRPC$TL_updatePeerLocated tLRPC$TL_updatePeerLocated = (TLRPC$TL_updatePeerLocated) tLRPC$Update;
                    int size2 = tLRPC$TL_updatePeerLocated.peers.size();
                    for (int i3 = 0; i3 < size2; i3++) {
                        TLRPC$PeerLocated tLRPC$PeerLocated = tLRPC$TL_updatePeerLocated.peers.get(i3);
                        if (tLRPC$PeerLocated instanceof TLRPC$TL_peerLocated) {
                            TLRPC$TL_peerLocated tLRPC$TL_peerLocated = (TLRPC$TL_peerLocated) tLRPC$PeerLocated;
                            if (tLRPC$TL_peerLocated.peer instanceof TLRPC$TL_peerUser) {
                                this.users.add(tLRPC$TL_peerLocated);
                            } else {
                                this.chats.add(tLRPC$TL_peerLocated);
                            }
                        } else if (tLRPC$PeerLocated instanceof TLRPC$TL_peerSelfLocated) {
                            int i4 = userConfig.sharingMyLocationUntil;
                            int i5 = ((TLRPC$TL_peerSelfLocated) tLRPC$PeerLocated).expires;
                            if (i4 != i5) {
                                userConfig.sharingMyLocationUntil = i5;
                                z = true;
                            }
                            z2 = true;
                        }
                    }
                }
            }
            if (!z2 && userConfig.sharingMyLocationUntil != 0) {
                userConfig.sharingMyLocationUntil = 0;
                z = true;
            }
            checkForExpiredLocations(true);
            updateRows(diffCallback);
        }
        if (z) {
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

    public void onLocationAddressAvailable(String str, String str2, Location location2) {
        this.currentGroupCreateAddress = str;
        this.currentGroupCreateDisplayAddress = str2;
        this.currentGroupCreateLocation = location2;
        ActionIntroActivity actionIntroActivity = this.groupCreateActivity;
        if (actionIntroActivity != null) {
            actionIntroActivity.setGroupCreateAddress(str, str2, location2);
        }
        AlertDialog alertDialog = this.loadingDialog;
        if (alertDialog != null && !this.checkingCanCreate) {
            try {
                alertDialog.dismiss();
            } catch (Throwable th) {
                FileLog.e(th);
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

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        ArrayList<TLRPC$TL_peerLocated> arrayList;
        int i3;
        int i4;
        if (i == NotificationCenter.newLocationAvailable) {
            sendRequest(false, 0);
        } else if (i == NotificationCenter.newPeopleNearbyAvailable) {
            TLRPC$TL_updatePeerLocated tLRPC$TL_updatePeerLocated = objArr[0];
            DiffCallback diffCallback = new DiffCallback();
            diffCallback.saveCurrentState();
            int size = tLRPC$TL_updatePeerLocated.peers.size();
            for (int i5 = 0; i5 < size; i5++) {
                TLRPC$PeerLocated tLRPC$PeerLocated = tLRPC$TL_updatePeerLocated.peers.get(i5);
                if (tLRPC$PeerLocated instanceof TLRPC$TL_peerLocated) {
                    TLRPC$TL_peerLocated tLRPC$TL_peerLocated = (TLRPC$TL_peerLocated) tLRPC$PeerLocated;
                    if (tLRPC$TL_peerLocated.peer instanceof TLRPC$TL_peerUser) {
                        arrayList = this.users;
                    } else {
                        arrayList = this.chats;
                    }
                    int size2 = arrayList.size();
                    boolean z = false;
                    for (int i6 = 0; i6 < size2; i6++) {
                        TLRPC$Peer tLRPC$Peer = arrayList.get(i6).peer;
                        int i7 = tLRPC$Peer.user_id;
                        if ((i7 != 0 && i7 == tLRPC$TL_peerLocated.peer.user_id) || (((i3 = tLRPC$Peer.chat_id) != 0 && i3 == tLRPC$TL_peerLocated.peer.chat_id) || ((i4 = tLRPC$Peer.channel_id) != 0 && i4 == tLRPC$TL_peerLocated.peer.channel_id))) {
                            arrayList.set(i6, tLRPC$TL_peerLocated);
                            z = true;
                        }
                    }
                    if (!z) {
                        arrayList.add(tLRPC$TL_peerLocated);
                    }
                }
            }
            checkForExpiredLocations(true);
            updateRows(diffCallback);
        } else if (i == NotificationCenter.needDeleteDialog && this.fragmentView != null && !this.isPaused) {
            long longValue = objArr[0].longValue();
            TLRPC$User tLRPC$User = objArr[1];
            $$Lambda$PeopleNearbyActivity$S3ayKggji5BRbEYwN4ZV2vbDZnU r2 = new Runnable(objArr[2], longValue, objArr[3].booleanValue()) {
                public final /* synthetic */ TLRPC$Chat f$1;
                public final /* synthetic */ long f$2;
                public final /* synthetic */ boolean f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r5;
                }

                public final void run() {
                    PeopleNearbyActivity.this.lambda$didReceivedNotification$8$PeopleNearbyActivity(this.f$1, this.f$2, this.f$3);
                }
            };
            UndoView undoView2 = this.undoView;
            if (undoView2 != null) {
                undoView2.showWithAction(longValue, 1, (Runnable) r2);
            } else {
                r2.run();
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$didReceivedNotification$8 */
    public /* synthetic */ void lambda$didReceivedNotification$8$PeopleNearbyActivity(TLRPC$Chat tLRPC$Chat, long j, boolean z) {
        if (tLRPC$Chat == null) {
            getMessagesController().deleteDialog(j, 0, z);
        } else if (ChatObject.isNotInChat(tLRPC$Chat)) {
            getMessagesController().deleteDialog(j, 0, z);
        } else {
            getMessagesController().deleteParticipantFromChat((int) (-j), getMessagesController().getUser(Integer.valueOf(getUserConfig().getClientUserId())), (TLRPC$Chat) null, (TLRPC$ChatFull) null, z, z);
        }
    }

    private void checkForExpiredLocations(boolean z) {
        Runnable runnable = this.checkExpiredRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.checkExpiredRunnable = null;
        }
        int currentTime = getConnectionsManager().getCurrentTime();
        int i = 0;
        int i2 = Integer.MAX_VALUE;
        while (i < 2) {
            ArrayList<TLRPC$TL_peerLocated> arrayList = i == 0 ? this.users : this.chats;
            int size = arrayList.size();
            int i3 = 0;
            while (i3 < size) {
                int i4 = arrayList.get(i3).expires;
                if (i4 > currentTime) {
                    i2 = Math.min(i2, i4);
                    i3++;
                } else {
                    new DiffCallback();
                    throw null;
                }
            }
            i++;
        }
        if (z) {
            getLocationController().setCachedNearbyUsersAndChats(this.users, this.chats);
        }
        if (i2 != Integer.MAX_VALUE) {
            $$Lambda$PeopleNearbyActivity$R_UluTvOElowLB2XSMbtq4RY7gk r11 = new Runnable() {
                public final void run() {
                    PeopleNearbyActivity.this.lambda$checkForExpiredLocations$9$PeopleNearbyActivity();
                }
            };
            this.checkExpiredRunnable = r11;
            AndroidUtilities.runOnUIThread(r11, (long) ((i2 - currentTime) * 1000));
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$checkForExpiredLocations$9 */
    public /* synthetic */ void lambda$checkForExpiredLocations$9$PeopleNearbyActivity() {
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
            RadialProgressView radialProgressView2 = this.progressView;
            boolean z = LocaleController.isRTL;
            addView(radialProgressView2, LayoutHelper.createFrame(50, 40.0f, (z ? 3 : 5) | 48, z ? 2.0f : 0.0f, 3.0f, z ? 0.0f : 2.0f, 0.0f));
        }
    }

    public class HintInnerCell extends FrameLayout {
        private ImageView imageView;
        private TextView messageTextView;
        /* access modifiers changed from: private */
        public TextView titleTextView;

        public HintInnerCell(Context context) {
            super(context);
            int currentActionBarHeight = ((int) (((float) (ActionBar.getCurrentActionBarHeight() + (PeopleNearbyActivity.this.actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0))) / AndroidUtilities.density)) - 44;
            ImageView imageView2 = new ImageView(context);
            this.imageView = imageView2;
            imageView2.setBackgroundDrawable(Theme.createCircleDrawable(AndroidUtilities.dp(74.0f), Theme.getColor("chats_archiveBackground")));
            this.imageView.setImageDrawable(new ShareLocationDrawable(context, 2));
            this.imageView.setScaleType(ImageView.ScaleType.CENTER);
            addView(this.imageView, LayoutHelper.createFrame(74, 74.0f, 49, 0.0f, (float) (currentActionBarHeight + 27), 0.0f, 0.0f));
            TextView textView = new TextView(context);
            this.titleTextView = textView;
            textView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.titleTextView.setTextSize(1, 24.0f);
            this.titleTextView.setGravity(17);
            this.titleTextView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("PeopleNearby", NUM, new Object[0])));
            addView(this.titleTextView, LayoutHelper.createFrame(-1, -2.0f, 51, 17.0f, (float) (currentActionBarHeight + 120), 17.0f, 27.0f));
            TextView textView2 = new TextView(context);
            this.messageTextView = textView2;
            textView2.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText"));
            this.messageTextView.setTextSize(1, 15.0f);
            this.messageTextView.setGravity(17);
            this.messageTextView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("PeopleNearbyInfo2", NUM, new Object[0])));
            addView(this.messageTextView, LayoutHelper.createFrame(-1, -2.0f, 51, 40.0f, (float) (currentActionBarHeight + 161), 40.0f, 27.0f));
        }
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            int itemViewType = viewHolder.getItemViewType();
            return itemViewType == 0 || itemViewType == 2;
        }

        public int getItemCount() {
            return PeopleNearbyActivity.this.rowCount;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            HintInnerCell hintInnerCell;
            if (i == 0) {
                ManageChatUserCell manageChatUserCell = new ManageChatUserCell(this.mContext, 6, 2, false);
                manageChatUserCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                hintInnerCell = manageChatUserCell;
            } else if (i == 1) {
                hintInnerCell = new ShadowSectionCell(this.mContext);
            } else if (i == 2) {
                ManageChatTextCell manageChatTextCell = new ManageChatTextCell(this.mContext);
                manageChatTextCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                hintInnerCell = manageChatTextCell;
            } else if (i == 3) {
                HeaderCellProgress headerCellProgress = new HeaderCellProgress(this.mContext);
                headerCellProgress.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                hintInnerCell = headerCellProgress;
            } else if (i != 4) {
                HintInnerCell hintInnerCell2 = new HintInnerCell(this.mContext);
                hintInnerCell2.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                hintInnerCell = hintInnerCell2;
            } else {
                AnonymousClass1 r5 = new TextView(this.mContext) {
                    /* access modifiers changed from: protected */
                    public void onMeasure(int i, int i2) {
                        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(67.0f), NUM));
                    }
                };
                r5.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                r5.setPadding(0, 0, AndroidUtilities.dp(3.0f), 0);
                r5.setTextSize(1, 14.0f);
                r5.setGravity(17);
                r5.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText3"));
                hintInnerCell = r5;
            }
            hintInnerCell.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(hintInnerCell);
        }

        public void onViewAttachedToWindow(RecyclerView.ViewHolder viewHolder) {
            if (viewHolder.getItemViewType() == 3 && !PeopleNearbyActivity.this.animatingViews.contains(viewHolder.itemView)) {
                ((HeaderCellProgress) viewHolder.itemView).progressView.setAlpha(PeopleNearbyActivity.this.showingLoadingProgress ? 1.0f : 0.0f);
            }
        }

        private String formatDistance(TLRPC$TL_peerLocated tLRPC$TL_peerLocated) {
            return LocaleController.formatDistance((float) tLRPC$TL_peerLocated.distance, 0);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            int i2;
            int itemViewType = viewHolder.getItemViewType();
            boolean z = false;
            if (itemViewType == 0) {
                ManageChatUserCell manageChatUserCell = (ManageChatUserCell) viewHolder.itemView;
                manageChatUserCell.setTag(Integer.valueOf(i));
                if (i >= PeopleNearbyActivity.this.usersStartRow && i < PeopleNearbyActivity.this.usersEndRow) {
                    TLRPC$TL_peerLocated tLRPC$TL_peerLocated = (TLRPC$TL_peerLocated) PeopleNearbyActivity.this.users.get(i - PeopleNearbyActivity.this.usersStartRow);
                    TLRPC$User user = PeopleNearbyActivity.this.getMessagesController().getUser(Integer.valueOf(tLRPC$TL_peerLocated.peer.user_id));
                    if (user != null) {
                        String formatDistance = formatDistance(tLRPC$TL_peerLocated);
                        if (!(PeopleNearbyActivity.this.showMoreRow == -1 && i == PeopleNearbyActivity.this.usersEndRow - 1)) {
                            z = true;
                        }
                        manageChatUserCell.setData(user, (CharSequence) null, formatDistance, z);
                    }
                } else if (i >= PeopleNearbyActivity.this.chatsStartRow && i < PeopleNearbyActivity.this.chatsEndRow) {
                    int access$600 = i - PeopleNearbyActivity.this.chatsStartRow;
                    TLRPC$TL_peerLocated tLRPC$TL_peerLocated2 = (TLRPC$TL_peerLocated) PeopleNearbyActivity.this.chats.get(access$600);
                    TLRPC$Peer tLRPC$Peer = tLRPC$TL_peerLocated2.peer;
                    if (tLRPC$Peer instanceof TLRPC$TL_peerChat) {
                        i2 = tLRPC$Peer.chat_id;
                    } else {
                        i2 = tLRPC$Peer.channel_id;
                    }
                    TLRPC$Chat chat = PeopleNearbyActivity.this.getMessagesController().getChat(Integer.valueOf(i2));
                    if (chat != null) {
                        String formatDistance2 = formatDistance(tLRPC$TL_peerLocated2);
                        int i3 = chat.participants_count;
                        if (i3 != 0) {
                            formatDistance2 = String.format("%1$s, %2$s", new Object[]{formatDistance2, LocaleController.formatPluralString("Members", i3)});
                        }
                        if (access$600 != PeopleNearbyActivity.this.chats.size() - 1) {
                            z = true;
                        }
                        manageChatUserCell.setData(chat, (CharSequence) null, formatDistance2, z);
                    }
                }
            } else if (itemViewType == 1) {
                ShadowSectionCell shadowSectionCell = (ShadowSectionCell) viewHolder.itemView;
                if (i == PeopleNearbyActivity.this.usersSectionRow) {
                    shadowSectionCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                } else if (i == PeopleNearbyActivity.this.chatsSectionRow) {
                    shadowSectionCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                } else if (i == PeopleNearbyActivity.this.helpSectionRow) {
                    shadowSectionCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                }
            } else if (itemViewType == 2) {
                ManageChatTextCell manageChatTextCell = (ManageChatTextCell) viewHolder.itemView;
                manageChatTextCell.setColors("windowBackgroundWhiteBlueIcon", "windowBackgroundWhiteBlueButton");
                if (i == PeopleNearbyActivity.this.chatsCreateRow) {
                    String string = LocaleController.getString("NearbyCreateGroup", NUM);
                    if (PeopleNearbyActivity.this.chatsStartRow != -1) {
                        z = true;
                    }
                    manageChatTextCell.setText(string, (String) null, NUM, z);
                } else if (i == PeopleNearbyActivity.this.showMeRow) {
                    PeopleNearbyActivity peopleNearbyActivity = PeopleNearbyActivity.this;
                    if (peopleNearbyActivity.showingMe = peopleNearbyActivity.getUserConfig().sharingMyLocationUntil > PeopleNearbyActivity.this.getConnectionsManager().getCurrentTime()) {
                        String string2 = LocaleController.getString("StopShowingMe", NUM);
                        if (PeopleNearbyActivity.this.usersStartRow != -1) {
                            z = true;
                        }
                        manageChatTextCell.setText(string2, (String) null, NUM, z);
                        manageChatTextCell.setColors("windowBackgroundWhiteRedText5", "windowBackgroundWhiteRedText5");
                        return;
                    }
                    String string3 = LocaleController.getString("MakeMyselfVisible", NUM);
                    if (PeopleNearbyActivity.this.usersStartRow != -1) {
                        z = true;
                    }
                    manageChatTextCell.setText(string3, (String) null, NUM, z);
                } else if (i == PeopleNearbyActivity.this.showMoreRow) {
                    manageChatTextCell.setText(LocaleController.formatPluralString("ShowVotes", PeopleNearbyActivity.this.users.size() - 5), (String) null, NUM, false);
                }
            } else if (itemViewType == 3) {
                HeaderCellProgress headerCellProgress = (HeaderCellProgress) viewHolder.itemView;
                if (i == PeopleNearbyActivity.this.usersHeaderRow) {
                    headerCellProgress.setText(LocaleController.getString("PeopleNearbyHeader", NUM));
                } else if (i == PeopleNearbyActivity.this.chatsHeaderRow) {
                    headerCellProgress.setText(LocaleController.getString("ChatsNearbyHeader", NUM));
                }
            }
        }

        public void onViewRecycled(RecyclerView.ViewHolder viewHolder) {
            View view = viewHolder.itemView;
            if (view instanceof ManageChatUserCell) {
                ((ManageChatUserCell) view).recycle();
            }
        }

        public int getItemViewType(int i) {
            if (i == PeopleNearbyActivity.this.helpRow) {
                return 5;
            }
            if (i == PeopleNearbyActivity.this.chatsCreateRow || i == PeopleNearbyActivity.this.showMeRow || i == PeopleNearbyActivity.this.showMoreRow) {
                return 2;
            }
            if (i == PeopleNearbyActivity.this.usersHeaderRow || i == PeopleNearbyActivity.this.chatsHeaderRow) {
                return 3;
            }
            return (i == PeopleNearbyActivity.this.usersSectionRow || i == PeopleNearbyActivity.this.chatsSectionRow || i == PeopleNearbyActivity.this.helpSectionRow) ? 1 : 0;
        }
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        $$Lambda$PeopleNearbyActivity$B0cmbZ_zmST2EPtV2ECW8175vpA r11 = new ThemeDescription.ThemeDescriptionDelegate() {
            public final void didSetColor() {
                PeopleNearbyActivity.this.lambda$getThemeDescriptions$10$PeopleNearbyActivity();
            }
        };
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{ManageChatUserCell.class, ManageChatTextCell.class, HeaderCell.class, TextView.class, HintInnerCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND | ThemeDescription.FLAG_CHECKTAG, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"));
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_CHECKTAG | ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.actionBarBackground, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "divider"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueHeader"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_PROGRESSBAR, new Class[]{HeaderCellProgress.class}, new String[]{"progressView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueHeader"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{ManageChatUserCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        $$Lambda$PeopleNearbyActivity$B0cmbZ_zmST2EPtV2ECW8175vpA r9 = r11;
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{ManageChatUserCell.class}, new String[]{"statusColor"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) r9, "windowBackgroundWhiteGrayText"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{ManageChatUserCell.class}, new String[]{"statusOnlineColor"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) r9, "windowBackgroundWhiteBlueText"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{ManageChatUserCell.class}, (Paint) null, Theme.avatarDrawables, (ThemeDescription.ThemeDescriptionDelegate) null, "avatar_text"));
        $$Lambda$PeopleNearbyActivity$B0cmbZ_zmST2EPtV2ECW8175vpA r8 = r11;
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "avatar_backgroundRed"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "avatar_backgroundOrange"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "avatar_backgroundViolet"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "avatar_backgroundGreen"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "avatar_backgroundCyan"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "avatar_backgroundBlue"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r8, "avatar_backgroundPink"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE, new Class[]{HintInnerCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_archiveBackground"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{HintInnerCell.class}, new String[]{"messageTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_message"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayIcon"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueButton"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueIcon"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteRedText5"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteRedText5"));
        arrayList.add(new ThemeDescription(this.undoView, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_background"));
        arrayList.add(new ThemeDescription((View) this.undoView, 0, new Class[]{UndoView.class}, new String[]{"undoImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_cancelColor"));
        arrayList.add(new ThemeDescription((View) this.undoView, 0, new Class[]{UndoView.class}, new String[]{"undoTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_cancelColor"));
        arrayList.add(new ThemeDescription((View) this.undoView, 0, new Class[]{UndoView.class}, new String[]{"infoTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_infoColor"));
        arrayList.add(new ThemeDescription((View) this.undoView, 0, new Class[]{UndoView.class}, new String[]{"subinfoTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_infoColor"));
        arrayList.add(new ThemeDescription((View) this.undoView, 0, new Class[]{UndoView.class}, new String[]{"textPaint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_infoColor"));
        arrayList.add(new ThemeDescription((View) this.undoView, 0, new Class[]{UndoView.class}, new String[]{"progressPaint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_infoColor"));
        return arrayList;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$getThemeDescriptions$10 */
    public /* synthetic */ void lambda$getThemeDescriptions$10$PeopleNearbyActivity() {
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            int childCount = recyclerListView.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = this.listView.getChildAt(i);
                if (childAt instanceof ManageChatUserCell) {
                    ((ManageChatUserCell) childAt).update(0);
                }
            }
        }
    }
}
