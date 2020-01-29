package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Property;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.LocationController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
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
    private static final int SHORT_POLL_TIMEOUT = 25000;
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
    private boolean firstLoaded;
    private ActionIntroActivity groupCreateActivity;
    /* access modifiers changed from: private */
    public int helpRow;
    private Location lastLoadedLocation;
    private long lastLoadedLocationTime;
    private RecyclerListView listView;
    private ListAdapter listViewAdapter;
    private AlertDialog loadingDialog;
    private int reqId;
    /* access modifiers changed from: private */
    public int rowCount;
    /* access modifiers changed from: private */
    public Runnable shortPollRunnable = new Runnable() {
        public void run() {
            if (PeopleNearbyActivity.this.shortPollRunnable != null) {
                PeopleNearbyActivity.this.sendRequest(true);
                AndroidUtilities.cancelRunOnUIThread(PeopleNearbyActivity.this.shortPollRunnable);
                AndroidUtilities.runOnUIThread(PeopleNearbyActivity.this.shortPollRunnable, 25000);
            }
        }
    };
    /* access modifiers changed from: private */
    public AnimatorSet showProgressAnimation;
    private Runnable showProgressRunnable;
    /* access modifiers changed from: private */
    public boolean showingLoadingProgress;
    private UndoView undoView;
    /* access modifiers changed from: private */
    public ArrayList<TLRPC.TL_peerLocated> users = new ArrayList<>(getLocationController().getCachedNearbyUsers());
    /* access modifiers changed from: private */
    public int usersEmptyRow;
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
        updateRows();
    }

    private void updateRows() {
        this.rowCount = 0;
        this.usersStartRow = -1;
        this.usersEndRow = -1;
        this.usersEmptyRow = -1;
        this.chatsStartRow = -1;
        this.chatsEndRow = -1;
        this.chatsCreateRow = -1;
        int i = this.rowCount;
        this.rowCount = i + 1;
        this.helpRow = i;
        int i2 = this.rowCount;
        this.rowCount = i2 + 1;
        this.usersHeaderRow = i2;
        if (this.users.isEmpty()) {
            int i3 = this.rowCount;
            this.rowCount = i3 + 1;
            this.usersEmptyRow = i3;
        } else {
            int i4 = this.rowCount;
            this.usersStartRow = i4;
            this.rowCount = i4 + this.users.size();
            this.usersEndRow = this.rowCount;
        }
        int i5 = this.rowCount;
        this.rowCount = i5 + 1;
        this.usersSectionRow = i5;
        int i6 = this.rowCount;
        this.rowCount = i6 + 1;
        this.chatsHeaderRow = i6;
        int i7 = this.rowCount;
        this.rowCount = i7 + 1;
        this.chatsCreateRow = i7;
        if (!this.chats.isEmpty()) {
            int i8 = this.rowCount;
            this.chatsStartRow = i8;
            this.rowCount = i8 + this.chats.size();
            this.chatsEndRow = this.rowCount;
        }
        int i9 = this.rowCount;
        this.rowCount = i9 + 1;
        this.chatsSectionRow = i9;
        ListAdapter listAdapter = this.listViewAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.newLocationAvailable);
        getNotificationCenter().addObserver(this, NotificationCenter.newPeopleNearbyAvailable);
        getNotificationCenter().addObserver(this, NotificationCenter.needDeleteDialog);
        checkCanCreateGroup();
        sendRequest(false);
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
        int i = 1;
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("PeopleNearby", NUM));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    PeopleNearbyActivity.this.finishFragment();
                }
            }
        });
        this.fragmentView = new FrameLayout(context);
        this.fragmentView.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
        this.fragmentView.setTag("windowBackgroundGray");
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        this.listView = new RecyclerListView(context);
        this.listView.setLayoutManager(new LinearLayoutManager(context, 1, false));
        RecyclerListView recyclerListView = this.listView;
        ListAdapter listAdapter = new ListAdapter(context);
        this.listViewAdapter = listAdapter;
        recyclerListView.setAdapter(listAdapter);
        RecyclerListView recyclerListView2 = this.listView;
        if (!LocaleController.isRTL) {
            i = 2;
        }
        recyclerListView2.setVerticalScrollbarPosition(i);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
            public final void onItemClick(View view, int i) {
                PeopleNearbyActivity.this.lambda$createView$1$PeopleNearbyActivity(view, i);
            }
        });
        this.undoView = new UndoView(context);
        frameLayout.addView(this.undoView, LayoutHelper.createFrame(-1, -2.0f, 83, 8.0f, 0.0f, 8.0f, 8.0f));
        updateRows();
        return this.fragmentView;
    }

    public /* synthetic */ void lambda$createView$1$PeopleNearbyActivity(View view, int i) {
        int i2;
        int i3 = this.usersStartRow;
        if (i < i3 || i >= this.usersEndRow) {
            int i4 = this.chatsStartRow;
            if (i >= i4 && i < this.chatsEndRow) {
                Bundle bundle = new Bundle();
                TLRPC.Peer peer = this.chats.get(i - i4).peer;
                if (peer instanceof TLRPC.TL_peerChat) {
                    i2 = peer.chat_id;
                } else {
                    i2 = peer.channel_id;
                }
                bundle.putInt("chat_id", i2);
                presentFragment(new ChatActivity(bundle));
            } else if (i != this.chatsCreateRow) {
            } else {
                if (this.checkingCanCreate || this.currentGroupCreateAddress == null) {
                    this.loadingDialog = new AlertDialog(getParentActivity(), 3);
                    this.loadingDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        public final void onCancel(DialogInterface dialogInterface) {
                            PeopleNearbyActivity.this.lambda$null$0$PeopleNearbyActivity(dialogInterface);
                        }
                    });
                    this.loadingDialog.show();
                    return;
                }
                openGroupCreate();
            }
        } else {
            Bundle bundle2 = new Bundle();
            bundle2.putInt("user_id", this.users.get(i - i3).peer.user_id);
            presentFragment(new ChatActivity(bundle2));
        }
    }

    public /* synthetic */ void lambda$null$0$PeopleNearbyActivity(DialogInterface dialogInterface) {
        this.loadingDialog = null;
    }

    private void openGroupCreate() {
        if (!this.canCreateGroup) {
            AlertsCreator.showSimpleAlert(this, LocaleController.getString("YourLocatedChannelsTooMuch", NUM));
            return;
        }
        this.groupCreateActivity = new ActionIntroActivity(2);
        this.groupCreateActivity.setGroupCreateAddress(this.currentGroupCreateAddress, this.currentGroupCreateDisplayAddress, this.currentGroupCreateLocation);
        presentFragment(this.groupCreateActivity);
    }

    private void checkCanCreateGroup() {
        if (!this.checkingCanCreate) {
            this.checkingCanCreate = true;
            TLRPC.TL_channels_getAdminedPublicChannels tL_channels_getAdminedPublicChannels = new TLRPC.TL_channels_getAdminedPublicChannels();
            tL_channels_getAdminedPublicChannels.by_location = true;
            tL_channels_getAdminedPublicChannels.check_limit = true;
            getConnectionsManager().bindRequestToGuid(getConnectionsManager().sendRequest(tL_channels_getAdminedPublicChannels, new RequestDelegate() {
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    PeopleNearbyActivity.this.lambda$checkCanCreateGroup$3$PeopleNearbyActivity(tLObject, tL_error);
                }
            }), this.classGuid);
        }
    }

    public /* synthetic */ void lambda$checkCanCreateGroup$3$PeopleNearbyActivity(TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tL_error) {
            private final /* synthetic */ TLRPC.TL_error f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                PeopleNearbyActivity.this.lambda$null$2$PeopleNearbyActivity(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$null$2$PeopleNearbyActivity(TLRPC.TL_error tL_error) {
        this.canCreateGroup = tL_error == null;
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
                        RadialProgressView access$200 = headerCellProgress.progressView;
                        Property property = View.ALPHA;
                        float[] fArr = new float[1];
                        fArr[0] = z ? 1.0f : 0.0f;
                        arrayList.add(ObjectAnimator.ofFloat(access$200, property, fArr));
                    }
                }
                if (!arrayList.isEmpty()) {
                    this.showProgressAnimation = new AnimatorSet();
                    this.showProgressAnimation.playTogether(arrayList);
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
    public void sendRequest(boolean z) {
        Location location;
        if (!this.firstLoaded) {
            $$Lambda$PeopleNearbyActivity$bfDvDiy1N3xS6WCvcClVmtjhWZ4 r0 = new Runnable() {
                public final void run() {
                    PeopleNearbyActivity.this.lambda$sendRequest$4$PeopleNearbyActivity();
                }
            };
            this.showProgressRunnable = r0;
            AndroidUtilities.runOnUIThread(r0, 1000);
            this.firstLoaded = true;
        }
        Location lastKnownLocation = getLocationController().getLastKnownLocation();
        if (lastKnownLocation != null) {
            this.currentGroupCreateLocation = lastKnownLocation;
            if (!z && (location = this.lastLoadedLocation) != null) {
                float distanceTo = location.distanceTo(lastKnownLocation);
                if (BuildVars.DEBUG_VERSION) {
                    FileLog.d("located distance = " + distanceTo);
                }
                if (SystemClock.uptimeMillis() - this.lastLoadedLocationTime >= 3000 && this.lastLoadedLocation.distanceTo(lastKnownLocation) > 20.0f) {
                    if (this.reqId != 0) {
                        getConnectionsManager().cancelRequest(this.reqId, true);
                        this.reqId = 0;
                    }
                } else {
                    return;
                }
            }
            if (this.reqId == 0) {
                this.lastLoadedLocation = lastKnownLocation;
                this.lastLoadedLocationTime = SystemClock.uptimeMillis();
                LocationController.fetchLocationAddress(this.currentGroupCreateLocation, this);
                TLRPC.TL_contacts_getLocated tL_contacts_getLocated = new TLRPC.TL_contacts_getLocated();
                tL_contacts_getLocated.geo_point = new TLRPC.TL_inputGeoPoint();
                tL_contacts_getLocated.geo_point.lat = lastKnownLocation.getLatitude();
                tL_contacts_getLocated.geo_point._long = lastKnownLocation.getLongitude();
                this.reqId = getConnectionsManager().sendRequest(tL_contacts_getLocated, new RequestDelegate() {
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        PeopleNearbyActivity.this.lambda$sendRequest$6$PeopleNearbyActivity(tLObject, tL_error);
                    }
                });
                getConnectionsManager().bindRequestToGuid(this.reqId, this.classGuid);
            }
        }
    }

    public /* synthetic */ void lambda$sendRequest$4$PeopleNearbyActivity() {
        showLoadingProgress(true);
        this.showProgressRunnable = null;
    }

    public /* synthetic */ void lambda$sendRequest$6$PeopleNearbyActivity(TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(tLObject) {
            private final /* synthetic */ TLObject f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                PeopleNearbyActivity.this.lambda$null$5$PeopleNearbyActivity(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$null$5$PeopleNearbyActivity(TLObject tLObject) {
        this.reqId = 0;
        Runnable runnable = this.showProgressRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.showProgressRunnable = null;
        }
        showLoadingProgress(false);
        if (tLObject != null) {
            TLRPC.TL_updates tL_updates = (TLRPC.TL_updates) tLObject;
            getMessagesController().putUsers(tL_updates.users, false);
            getMessagesController().putChats(tL_updates.chats, false);
            this.users.clear();
            this.chats.clear();
            int size = tL_updates.updates.size();
            for (int i = 0; i < size; i++) {
                TLRPC.Update update = tL_updates.updates.get(i);
                if (update instanceof TLRPC.TL_updatePeerLocated) {
                    TLRPC.TL_updatePeerLocated tL_updatePeerLocated = (TLRPC.TL_updatePeerLocated) update;
                    int size2 = tL_updatePeerLocated.peers.size();
                    for (int i2 = 0; i2 < size2; i2++) {
                        TLRPC.TL_peerLocated tL_peerLocated = tL_updatePeerLocated.peers.get(i2);
                        if (tL_peerLocated.peer instanceof TLRPC.TL_peerUser) {
                            this.users.add(tL_peerLocated);
                        } else {
                            this.chats.add(tL_peerLocated);
                        }
                    }
                }
            }
            checkForExpiredLocations(true);
            updateRows();
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

    public void onLocationAddressAvailable(String str, String str2, Location location) {
        this.currentGroupCreateAddress = str;
        this.currentGroupCreateDisplayAddress = str2;
        this.currentGroupCreateLocation = location;
        ActionIntroActivity actionIntroActivity = this.groupCreateActivity;
        if (actionIntroActivity != null) {
            actionIntroActivity.setGroupCreateAddress(this.currentGroupCreateAddress, this.currentGroupCreateDisplayAddress, this.currentGroupCreateLocation);
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
        ArrayList<TLRPC.TL_peerLocated> arrayList;
        int i3;
        int i4;
        if (i == NotificationCenter.newLocationAvailable) {
            sendRequest(false);
        } else if (i == NotificationCenter.newPeopleNearbyAvailable) {
            TLRPC.TL_updatePeerLocated tL_updatePeerLocated = objArr[0];
            int size = tL_updatePeerLocated.peers.size();
            for (int i5 = 0; i5 < size; i5++) {
                TLRPC.TL_peerLocated tL_peerLocated = tL_updatePeerLocated.peers.get(i5);
                if (tL_peerLocated.peer instanceof TLRPC.TL_peerUser) {
                    arrayList = this.users;
                } else {
                    arrayList = this.chats;
                }
                int size2 = arrayList.size();
                boolean z = false;
                for (int i6 = 0; i6 < size2; i6++) {
                    TLRPC.TL_peerLocated tL_peerLocated2 = arrayList.get(i6);
                    int i7 = tL_peerLocated2.peer.user_id;
                    if ((i7 != 0 && i7 == tL_peerLocated.peer.user_id) || (((i3 = tL_peerLocated2.peer.chat_id) != 0 && i3 == tL_peerLocated.peer.chat_id) || ((i4 = tL_peerLocated2.peer.channel_id) != 0 && i4 == tL_peerLocated.peer.channel_id))) {
                        arrayList.set(i6, tL_peerLocated);
                        z = true;
                    }
                }
                if (!z) {
                    arrayList.add(tL_peerLocated);
                }
            }
            checkForExpiredLocations(true);
            updateRows();
        } else if (i == NotificationCenter.needDeleteDialog && this.fragmentView != null && !this.isPaused) {
            long longValue = objArr[0].longValue();
            TLRPC.User user = objArr[1];
            $$Lambda$PeopleNearbyActivity$l7oXjY0bwLmYvYWbRJsU21AcTA r2 = new Runnable(objArr[2], longValue, objArr[3].booleanValue()) {
                private final /* synthetic */ TLRPC.Chat f$1;
                private final /* synthetic */ long f$2;
                private final /* synthetic */ boolean f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r5;
                }

                public final void run() {
                    PeopleNearbyActivity.this.lambda$didReceivedNotification$7$PeopleNearbyActivity(this.f$1, this.f$2, this.f$3);
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

    public /* synthetic */ void lambda$didReceivedNotification$7$PeopleNearbyActivity(TLRPC.Chat chat, long j, boolean z) {
        if (chat == null) {
            getMessagesController().deleteDialog(j, 0, z);
        } else if (ChatObject.isNotInChat(chat)) {
            getMessagesController().deleteDialog(j, 0, z);
        } else {
            getMessagesController().deleteUserFromChat((int) (-j), getMessagesController().getUser(Integer.valueOf(getUserConfig().getClientUserId())), (TLRPC.ChatFull) null, false, z);
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
        boolean z2 = false;
        int i2 = Integer.MAX_VALUE;
        while (i < 2) {
            ArrayList<TLRPC.TL_peerLocated> arrayList = i == 0 ? this.users : this.chats;
            int size = arrayList.size();
            boolean z3 = z2;
            int i3 = 0;
            while (i3 < size) {
                int i4 = arrayList.get(i3).expires;
                if (i4 <= currentTime) {
                    arrayList.remove(i3);
                    i3--;
                    size--;
                    z3 = true;
                } else {
                    i2 = Math.min(i2, i4);
                }
                i3++;
            }
            i++;
            z2 = z3;
        }
        if (z2 && this.listViewAdapter != null) {
            updateRows();
        }
        if (z2 || z) {
            getLocationController().setCachedNearbyUsersAndChats(this.users, this.chats);
        }
        if (i2 != Integer.MAX_VALUE) {
            $$Lambda$PeopleNearbyActivity$9rkO2iqBs2f1u0FRyFN3brPpvY4 r12 = new Runnable() {
                public final void run() {
                    PeopleNearbyActivity.this.lambda$checkForExpiredLocations$8$PeopleNearbyActivity();
                }
            };
            this.checkExpiredRunnable = r12;
            AndroidUtilities.runOnUIThread(r12, (long) ((i2 - currentTime) * 1000));
        }
    }

    public /* synthetic */ void lambda$checkForExpiredLocations$8$PeopleNearbyActivity() {
        this.checkExpiredRunnable = null;
        checkForExpiredLocations(false);
    }

    public class HeaderCellProgress extends HeaderCell {
        /* access modifiers changed from: private */
        public RadialProgressView progressView;

        public HeaderCellProgress(Context context) {
            super(context);
            setClipChildren(false);
            this.progressView = new RadialProgressView(context);
            this.progressView.setSize(AndroidUtilities.dp(14.0f));
            this.progressView.setStrokeWidth(2.0f);
            this.progressView.setAlpha(0.0f);
            this.progressView.setProgressColor(Theme.getColor("windowBackgroundWhiteBlueHeader"));
            addView(this.progressView, LayoutHelper.createFrame(50, 40.0f, (LocaleController.isRTL ? 3 : 5) | 48, LocaleController.isRTL ? 2.0f : 0.0f, 3.0f, LocaleController.isRTL ? 0.0f : 2.0f, 0.0f));
        }
    }

    public class HintInnerCell extends FrameLayout {
        private ImageView imageView;
        private TextView messageTextView;

        public HintInnerCell(Context context) {
            super(context);
            this.imageView = new ImageView(context);
            this.imageView.setBackgroundDrawable(Theme.createCircleDrawable(AndroidUtilities.dp(74.0f), Theme.getColor("chats_archiveBackground")));
            this.imageView.setImageDrawable(new ShareLocationDrawable(context, 2));
            this.imageView.setScaleType(ImageView.ScaleType.CENTER);
            addView(this.imageView, LayoutHelper.createFrame(74, 74.0f, 49, 0.0f, 27.0f, 0.0f, 0.0f));
            this.messageTextView = new TextView(context);
            this.messageTextView.setTextColor(Theme.getColor("chats_message"));
            this.messageTextView.setTextSize(1, 14.0f);
            this.messageTextView.setGravity(17);
            this.messageTextView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("PeopleNearbyInfo", NUM, new Object[0])));
            addView(this.messageTextView, LayoutHelper.createFrame(-1, -2.0f, 51, 52.0f, 125.0f, 52.0f, 27.0f));
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
                hintInnerCell = new ShadowSectionCell(this.mContext, 22);
            } else if (i == 2) {
                ManageChatTextCell manageChatTextCell = new ManageChatTextCell(this.mContext);
                manageChatTextCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                hintInnerCell = manageChatTextCell;
            } else if (i == 3) {
                HeaderCellProgress headerCellProgress = new HeaderCellProgress(this.mContext);
                headerCellProgress.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                hintInnerCell = headerCellProgress;
            } else if (i != 4) {
                hintInnerCell = new HintInnerCell(this.mContext);
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

        private String formatDistance(TLRPC.TL_peerLocated tL_peerLocated) {
            return LocaleController.formatDistance((float) tL_peerLocated.distance);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            int i2;
            int itemViewType = viewHolder.getItemViewType();
            boolean z = false;
            if (itemViewType == 0) {
                ManageChatUserCell manageChatUserCell = (ManageChatUserCell) viewHolder.itemView;
                manageChatUserCell.setTag(Integer.valueOf(i));
                if (i >= PeopleNearbyActivity.this.usersStartRow && i < PeopleNearbyActivity.this.usersEndRow) {
                    int access$700 = i - PeopleNearbyActivity.this.usersStartRow;
                    TLRPC.TL_peerLocated tL_peerLocated = (TLRPC.TL_peerLocated) PeopleNearbyActivity.this.users.get(access$700);
                    TLRPC.User user = PeopleNearbyActivity.this.getMessagesController().getUser(Integer.valueOf(tL_peerLocated.peer.user_id));
                    if (user != null) {
                        String formatDistance = formatDistance(tL_peerLocated);
                        if (access$700 != PeopleNearbyActivity.this.users.size() - 1) {
                            z = true;
                        }
                        manageChatUserCell.setData(user, (CharSequence) null, formatDistance, z);
                    }
                } else if (i >= PeopleNearbyActivity.this.chatsStartRow && i < PeopleNearbyActivity.this.chatsEndRow) {
                    int access$1100 = i - PeopleNearbyActivity.this.chatsStartRow;
                    TLRPC.TL_peerLocated tL_peerLocated2 = (TLRPC.TL_peerLocated) PeopleNearbyActivity.this.chats.get(access$1100);
                    TLRPC.Peer peer = tL_peerLocated2.peer;
                    if (peer instanceof TLRPC.TL_peerChat) {
                        i2 = peer.chat_id;
                    } else {
                        i2 = peer.channel_id;
                    }
                    TLRPC.Chat chat = PeopleNearbyActivity.this.getMessagesController().getChat(Integer.valueOf(i2));
                    if (chat != null) {
                        String formatDistance2 = formatDistance(tL_peerLocated2);
                        int i3 = chat.participants_count;
                        if (i3 != 0) {
                            formatDistance2 = String.format("%1$s, %2$s", new Object[]{formatDistance2, LocaleController.formatPluralString("Members", i3)});
                        }
                        if (access$1100 != PeopleNearbyActivity.this.chats.size() - 1) {
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
                }
            } else if (itemViewType == 3) {
                HeaderCellProgress headerCellProgress = (HeaderCellProgress) viewHolder.itemView;
                if (i == PeopleNearbyActivity.this.usersHeaderRow) {
                    headerCellProgress.setText(LocaleController.getString("PeopleNearbyHeader", NUM));
                } else if (i == PeopleNearbyActivity.this.chatsHeaderRow) {
                    headerCellProgress.setText(LocaleController.getString("ChatsNearbyHeader", NUM));
                }
            } else if (itemViewType == 4) {
                TextView textView = (TextView) viewHolder.itemView;
                if (i == PeopleNearbyActivity.this.usersEmptyRow) {
                    textView.setText(AndroidUtilities.replaceTags(LocaleController.getString("PeopleNearbyEmpty", NUM)));
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
            if (i == PeopleNearbyActivity.this.chatsCreateRow) {
                return 2;
            }
            if (i == PeopleNearbyActivity.this.usersHeaderRow || i == PeopleNearbyActivity.this.chatsHeaderRow) {
                return 3;
            }
            if (i == PeopleNearbyActivity.this.usersSectionRow || i == PeopleNearbyActivity.this.chatsSectionRow) {
                return 1;
            }
            return i == PeopleNearbyActivity.this.usersEmptyRow ? 4 : 0;
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        $$Lambda$PeopleNearbyActivity$BD_IAr3r6B6E9m17l1OW7ZTzfLY r10 = new ThemeDescription.ThemeDescriptionDelegate() {
            public final void didSetColor() {
                PeopleNearbyActivity.this.lambda$getThemeDescriptions$9$PeopleNearbyActivity();
            }
        };
        $$Lambda$PeopleNearbyActivity$BD_IAr3r6B6E9m17l1OW7ZTzfLY r8 = r10;
        $$Lambda$PeopleNearbyActivity$BD_IAr3r6B6E9m17l1OW7ZTzfLY r7 = r10;
        return new ThemeDescription[]{new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{ManageChatUserCell.class, ManageChatTextCell.class, HeaderCell.class, TextView.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"), new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND | ThemeDescription.FLAG_CHECKTAG, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"), new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND | ThemeDescription.FLAG_CHECKTAG, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"), new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultIcon"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultTitle"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"), new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"), new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "divider"), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"), new ThemeDescription((View) this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueHeader"), new ThemeDescription((View) this.listView, ThemeDescription.FLAG_PROGRESSBAR, new Class[]{HeaderCellProgress.class}, new String[]{"progressView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueHeader"), new ThemeDescription((View) this.listView, 0, new Class[]{ManageChatUserCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"), new ThemeDescription((View) this.listView, 0, new Class[]{ManageChatUserCell.class}, new String[]{"statusColor"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) r8, "windowBackgroundWhiteGrayText"), new ThemeDescription((View) this.listView, 0, new Class[]{ManageChatUserCell.class}, new String[]{"statusOnlineColor"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) r8, "windowBackgroundWhiteBlueText"), new ThemeDescription(this.listView, 0, new Class[]{ManageChatUserCell.class}, (Paint) null, new Drawable[]{Theme.avatar_savedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "avatar_text"), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r7, "avatar_backgroundRed"), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r7, "avatar_backgroundOrange"), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r7, "avatar_backgroundViolet"), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r7, "avatar_backgroundGreen"), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r7, "avatar_backgroundCyan"), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r7, "avatar_backgroundBlue"), new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r7, "avatar_backgroundPink"), new ThemeDescription((View) this.listView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE, new Class[]{HintInnerCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_archiveBackground"), new ThemeDescription((View) this.listView, 0, new Class[]{HintInnerCell.class}, new String[]{"messageTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chats_message"), new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"), new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayIcon"), new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueButton"), new ThemeDescription((View) this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueIcon"), new ThemeDescription(this.undoView, ThemeDescription.FLAG_BACKGROUNDFILTER, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_background"), new ThemeDescription((View) this.undoView, 0, new Class[]{UndoView.class}, new String[]{"undoImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_cancelColor"), new ThemeDescription((View) this.undoView, 0, new Class[]{UndoView.class}, new String[]{"undoTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_cancelColor"), new ThemeDescription((View) this.undoView, 0, new Class[]{UndoView.class}, new String[]{"infoTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_infoColor"), new ThemeDescription((View) this.undoView, 0, new Class[]{UndoView.class}, new String[]{"subinfoTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_infoColor"), new ThemeDescription((View) this.undoView, 0, new Class[]{UndoView.class}, new String[]{"textPaint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_infoColor"), new ThemeDescription((View) this.undoView, 0, new Class[]{UndoView.class}, new String[]{"progressPaint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "undo_infoColor")};
    }

    public /* synthetic */ void lambda$getThemeDescriptions$9$PeopleNearbyActivity() {
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
