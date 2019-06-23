package org.telegram.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView.LayoutParams;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.LocationController;
import org.telegram.messenger.LocationController.LocationFetchCallback;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.Peer;
import org.telegram.tgnet.TLRPC.TL_channels_getAdminedPublicChannels;
import org.telegram.tgnet.TLRPC.TL_contacts_getLocated;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_inputGeoPoint;
import org.telegram.tgnet.TLRPC.TL_peerChat;
import org.telegram.tgnet.TLRPC.TL_peerLocated;
import org.telegram.tgnet.TLRPC.TL_peerUser;
import org.telegram.tgnet.TLRPC.TL_updatePeerLocated;
import org.telegram.tgnet.TLRPC.TL_updates;
import org.telegram.tgnet.TLRPC.Update;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.ManageChatTextCell;
import org.telegram.ui.Cells.ManageChatUserCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;
import org.telegram.ui.Components.ShareLocationDrawable;

public class PeopleNearbyActivity extends BaseFragment implements NotificationCenterDelegate, LocationFetchCallback {
    private static final int SHORT_POLL_TIMEOUT = 25000;
    private boolean canCreateGroup;
    private ArrayList<TL_peerLocated> chats = new ArrayList(getLocationController().getCachedNearbyChats());
    private int chatsCreateRow;
    private int chatsEndRow;
    private int chatsHeaderRow;
    private int chatsSectionRow;
    private int chatsStartRow;
    private Runnable checkExpiredRunnable;
    private boolean checkingCanCreate;
    private int currentChatId;
    private String currentGroupCreateAddress;
    private Location currentGroupCreateLocation;
    private ActionIntroActivity groupCreateActivity;
    private int helpRow;
    private Location lastLoadedLocation;
    private RecyclerListView listView;
    private ListAdapter listViewAdapter;
    private AlertDialog loadingDialog;
    private int reqId;
    private int rowCount;
    private Runnable shortPollRunnable = new Runnable() {
        public void run() {
            if (PeopleNearbyActivity.this.shortPollRunnable != null) {
                PeopleNearbyActivity.this.sendRequest(true);
                AndroidUtilities.cancelRunOnUIThread(PeopleNearbyActivity.this.shortPollRunnable);
                AndroidUtilities.runOnUIThread(PeopleNearbyActivity.this.shortPollRunnable, 25000);
            }
        }
    };
    private ArrayList<TL_peerLocated> users = new ArrayList(getLocationController().getCachedNearbyUsers());
    private int usersEmptyRow;
    private int usersEndRow;
    private int usersHeaderRow;
    private int usersSectionRow;
    private int usersStartRow;

    public class HintInnerCell extends FrameLayout {
        private ImageView imageView;
        private TextView messageTextView;

        public HintInnerCell(Context context) {
            super(context);
            this.imageView = new ImageView(context);
            this.imageView.setBackgroundDrawable(Theme.createCircleDrawable(AndroidUtilities.dp(74.0f), Theme.getColor("chats_archiveBackground")));
            this.imageView.setImageDrawable(new ShareLocationDrawable(context, 2));
            this.imageView.setScaleType(ScaleType.CENTER);
            addView(this.imageView, LayoutHelper.createFrame(74, 74.0f, 49, 0.0f, 20.0f, 0.0f, 0.0f));
            this.messageTextView = new TextView(context);
            this.messageTextView.setTextColor(Theme.getColor("chats_message"));
            this.messageTextView.setTextSize(1, 14.0f);
            this.messageTextView.setGravity(17);
            this.messageTextView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("PeopleNearbyInfo", NUM, new Object[0])));
            addView(this.messageTextView, LayoutHelper.createFrame(-1, -2.0f, 51, 52.0f, 124.0f, 52.0f, 27.0f));
        }
    }

    private class ListAdapter extends SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean isEnabled(ViewHolder viewHolder) {
            int itemViewType = viewHolder.getItemViewType();
            return itemViewType == 0 || itemViewType == 2;
        }

        public int getItemCount() {
            return PeopleNearbyActivity.this.rowCount;
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view;
            String str = "windowBackgroundWhite";
            if (i == 0) {
                View manageChatUserCell = new ManageChatUserCell(this.mContext, 6, 2, false);
                manageChatUserCell.setBackgroundColor(Theme.getColor(str));
                view = manageChatUserCell;
            } else if (i == 1) {
                view = new ShadowSectionCell(this.mContext, 22);
            } else if (i == 2) {
                view = new ManageChatTextCell(this.mContext);
                view.setBackgroundColor(Theme.getColor(str));
            } else if (i == 3) {
                view = new HeaderCell(this.mContext);
                view.setBackgroundColor(Theme.getColor(str));
            } else if (i != 4) {
                view = new HintInnerCell(this.mContext);
            } else {
                view = new TextView(this.mContext) {
                    /* Access modifiers changed, original: protected */
                    public void onMeasure(int i, int i2) {
                        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(i), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(67.0f), NUM));
                    }
                };
                view.setBackgroundColor(Theme.getColor(str));
                view.setPadding(0, 0, AndroidUtilities.dp(3.0f), 0);
                view.setTextSize(1, 14.0f);
                view.setGravity(17);
                view.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText3"));
            }
            view.setLayoutParams(new LayoutParams(-1, -2));
            return new Holder(view);
        }

        private String formatDistance(TL_peerLocated tL_peerLocated) {
            int i = tL_peerLocated.distance;
            String str = "%d %s";
            if (i < 1000) {
                return String.format(str, new Object[]{Integer.valueOf(Math.max(1, i)), LocaleController.getString("MetersAway", NUM)});
            }
            String str2 = "KMetersAway";
            if (i % 1000 == 0) {
                return String.format(str, new Object[]{Integer.valueOf(i / 1000), LocaleController.getString(str2, NUM)});
            }
            return String.format("%.2f %s", new Object[]{Float.valueOf(((float) i) / 1000.0f), LocaleController.getString(str2, NUM)});
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            int itemViewType = viewHolder.getItemViewType();
            boolean z = false;
            if (itemViewType == 0) {
                ManageChatUserCell manageChatUserCell = (ManageChatUserCell) viewHolder.itemView;
                manageChatUserCell.setTag(Integer.valueOf(i));
                TL_peerLocated tL_peerLocated;
                if (i >= PeopleNearbyActivity.this.usersStartRow && i < PeopleNearbyActivity.this.usersEndRow) {
                    i -= PeopleNearbyActivity.this.usersStartRow;
                    tL_peerLocated = (TL_peerLocated) PeopleNearbyActivity.this.users.get(i);
                    User user = PeopleNearbyActivity.this.getMessagesController().getUser(Integer.valueOf(tL_peerLocated.peer.user_id));
                    if (user != null) {
                        String formatDistance = formatDistance(tL_peerLocated);
                        if (i != PeopleNearbyActivity.this.users.size() - 1) {
                            z = true;
                        }
                        manageChatUserCell.setData(user, null, formatDistance, z);
                    }
                } else if (i >= PeopleNearbyActivity.this.chatsStartRow && i < PeopleNearbyActivity.this.chatsEndRow) {
                    int i2;
                    i -= PeopleNearbyActivity.this.chatsStartRow;
                    tL_peerLocated = (TL_peerLocated) PeopleNearbyActivity.this.chats.get(i);
                    Peer peer = tL_peerLocated.peer;
                    if (peer instanceof TL_peerChat) {
                        i2 = peer.chat_id;
                    } else {
                        i2 = peer.channel_id;
                    }
                    Chat chat = PeopleNearbyActivity.this.getMessagesController().getChat(Integer.valueOf(i2));
                    if (chat != null) {
                        CharSequence formatDistance2 = formatDistance(tL_peerLocated);
                        if (chat.participants_count != 0) {
                            formatDistance2 = String.format("%1$s, %2$s", new Object[]{formatDistance2, LocaleController.formatPluralString("Members", chat.participants_count)});
                        }
                        if (i != PeopleNearbyActivity.this.chats.size() - 1) {
                            z = true;
                        }
                        manageChatUserCell.setData(chat, null, formatDistance2, z);
                    }
                }
            } else if (itemViewType == 1) {
                ShadowSectionCell shadowSectionCell = (ShadowSectionCell) viewHolder.itemView;
                String str = "windowBackgroundGrayShadow";
                if (i == PeopleNearbyActivity.this.usersSectionRow) {
                    shadowSectionCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, str));
                } else if (i == PeopleNearbyActivity.this.chatsSectionRow) {
                    shadowSectionCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, str));
                }
            } else if (itemViewType == 2) {
                ManageChatTextCell manageChatTextCell = (ManageChatTextCell) viewHolder.itemView;
                manageChatTextCell.setColors("windowBackgroundWhiteBlueIcon", "windowBackgroundWhiteBlueButton");
                if (i == PeopleNearbyActivity.this.chatsCreateRow) {
                    String string = LocaleController.getString("NearbyCreateGroup", NUM);
                    if (PeopleNearbyActivity.this.chatsStartRow != -1) {
                        z = true;
                    }
                    manageChatTextCell.setText(string, null, NUM, z);
                }
            } else if (itemViewType == 3) {
                HeaderCell headerCell = (HeaderCell) viewHolder.itemView;
                if (i == PeopleNearbyActivity.this.usersHeaderRow) {
                    headerCell.setText(LocaleController.getString("PeopleNearbyHeader", NUM));
                } else if (i == PeopleNearbyActivity.this.chatsHeaderRow) {
                    headerCell.setText(LocaleController.getString("ChatsNearbyHeader", NUM));
                }
            } else if (itemViewType == 4) {
                TextView textView = (TextView) viewHolder.itemView;
                if (i == PeopleNearbyActivity.this.usersEmptyRow) {
                    textView.setText(AndroidUtilities.replaceTags(LocaleController.getString("PeopleNearbyEmpty", NUM)));
                }
            }
        }

        public void onViewRecycled(ViewHolder viewHolder) {
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

    public PeopleNearbyActivity() {
        updateRows();
        checkForExpiredLocations();
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
        i = this.rowCount;
        this.rowCount = i + 1;
        this.usersHeaderRow = i;
        if (this.users.isEmpty()) {
            i = this.rowCount;
            this.rowCount = i + 1;
            this.usersEmptyRow = i;
        } else {
            i = this.rowCount;
            this.usersStartRow = i;
            this.rowCount = i + this.users.size();
            this.usersEndRow = this.rowCount;
        }
        i = this.rowCount;
        this.rowCount = i + 1;
        this.usersSectionRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.chatsHeaderRow = i;
        i = this.rowCount;
        this.rowCount = i + 1;
        this.chatsCreateRow = i;
        if (!this.chats.isEmpty()) {
            i = this.rowCount;
            this.chatsStartRow = i;
            this.rowCount = i + this.chats.size();
            this.chatsEndRow = this.rowCount;
        }
        i = this.rowCount;
        this.rowCount = i + 1;
        this.chatsSectionRow = i;
        ListAdapter listAdapter = this.listViewAdapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.newLocationAvailable);
        getNotificationCenter().addObserver(this, NotificationCenter.newPeopleNearbyAvailable);
        checkCanCreateGroup();
        sendRequest(false);
        AndroidUtilities.runOnUIThread(this.shortPollRunnable, 25000);
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.newLocationAvailable);
        getNotificationCenter().removeObserver(this, NotificationCenter.newPeopleNearbyAvailable);
        Runnable runnable = this.shortPollRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.shortPollRunnable = null;
        }
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(NUM);
        int i = 1;
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("PeopleNearby", NUM));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    PeopleNearbyActivity.this.finishFragment();
                }
            }
        });
        this.fragmentView = new FrameLayout(context);
        String str = "windowBackgroundGray";
        this.fragmentView.setBackgroundColor(Theme.getColor(str));
        this.fragmentView.setTag(str);
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
        this.listView.setOnItemClickListener(new -$$Lambda$PeopleNearbyActivity$Qap2kZjT1Phz_rvar_FyNJeztgy8(this));
        updateRows();
        return this.fragmentView;
    }

    public /* synthetic */ void lambda$createView$1$PeopleNearbyActivity(View view, int i) {
        int i2 = this.usersStartRow;
        TL_peerLocated tL_peerLocated;
        Bundle bundle;
        if (i < i2 || i >= this.usersEndRow) {
            i2 = this.chatsStartRow;
            if (i >= i2 && i < this.chatsEndRow) {
                tL_peerLocated = (TL_peerLocated) this.chats.get(i - i2);
                bundle = new Bundle();
                Peer peer = tL_peerLocated.peer;
                if (peer instanceof TL_peerChat) {
                    i2 = peer.chat_id;
                } else {
                    i2 = peer.channel_id;
                }
                bundle.putInt("chat_id", i2);
                presentFragment(new ChatActivity(bundle));
                return;
            } else if (i != this.chatsCreateRow) {
                return;
            } else {
                if (this.checkingCanCreate || this.currentGroupCreateAddress == null) {
                    this.loadingDialog = new AlertDialog(getParentActivity(), 3);
                    this.loadingDialog.setOnCancelListener(new -$$Lambda$PeopleNearbyActivity$-ClnBHYBP7TYqVqigBXtr1of8f8(this));
                    this.loadingDialog.show();
                    return;
                }
                openGroupCreate();
                return;
            }
        }
        tL_peerLocated = (TL_peerLocated) this.users.get(i - i2);
        bundle = new Bundle();
        bundle.putInt("user_id", tL_peerLocated.peer.user_id);
        presentFragment(new ChatActivity(bundle));
    }

    public /* synthetic */ void lambda$null$0$PeopleNearbyActivity(DialogInterface dialogInterface) {
        this.loadingDialog = null;
    }

    private void openGroupCreate() {
        if (this.canCreateGroup) {
            this.groupCreateActivity = new ActionIntroActivity(2);
            this.groupCreateActivity.setGroupCreateAddress(this.currentGroupCreateAddress, this.currentGroupCreateLocation);
            presentFragment(this.groupCreateActivity);
            return;
        }
        AlertsCreator.showSimpleAlert(this, LocaleController.getString("YourLocatedChannelsTooMuch", NUM));
    }

    private void checkCanCreateGroup() {
        if (!this.checkingCanCreate) {
            this.checkingCanCreate = true;
            TL_channels_getAdminedPublicChannels tL_channels_getAdminedPublicChannels = new TL_channels_getAdminedPublicChannels();
            tL_channels_getAdminedPublicChannels.by_location = true;
            tL_channels_getAdminedPublicChannels.check_limit = true;
            getConnectionsManager().bindRequestToGuid(getConnectionsManager().sendRequest(tL_channels_getAdminedPublicChannels, new -$$Lambda$PeopleNearbyActivity$Yfvar_Xio79WnEAYPatZFmIgc9CQ(this)), this.classGuid);
        }
    }

    public /* synthetic */ void lambda$checkCanCreateGroup$3$PeopleNearbyActivity(TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$PeopleNearbyActivity$3qpkBRD4hR3W2VkNxHgZfjSfzIc(this, tL_error));
    }

    public /* synthetic */ void lambda$null$2$PeopleNearbyActivity(TL_error tL_error) {
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

    private void sendRequest(boolean z) {
        Location lastKnownLocation = getLocationController().getLastKnownLocation();
        if (lastKnownLocation != null) {
            this.currentGroupCreateLocation = lastKnownLocation;
            if (!z) {
                Location location = this.lastLoadedLocation;
                if (location != null) {
                    if (location.distanceTo(lastKnownLocation) <= 7.0f) {
                        return;
                    }
                    if (this.reqId != 0) {
                        getConnectionsManager().cancelRequest(this.reqId, true);
                        this.reqId = 0;
                    }
                }
            }
            if (this.reqId == 0) {
                this.lastLoadedLocation = lastKnownLocation;
                LocationController.fetchLocationAddress(this.currentGroupCreateLocation, this);
                TL_contacts_getLocated tL_contacts_getLocated = new TL_contacts_getLocated();
                tL_contacts_getLocated.geo_point = new TL_inputGeoPoint();
                tL_contacts_getLocated.geo_point.lat = lastKnownLocation.getLatitude();
                tL_contacts_getLocated.geo_point._long = lastKnownLocation.getLongitude();
                this.reqId = getConnectionsManager().sendRequest(tL_contacts_getLocated, new -$$Lambda$PeopleNearbyActivity$jI_2r1jpiini1bd_Nd3sGKXYxYs(this));
                getConnectionsManager().bindRequestToGuid(this.reqId, this.classGuid);
            }
        }
    }

    public /* synthetic */ void lambda$sendRequest$5$PeopleNearbyActivity(TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$PeopleNearbyActivity$hS8cAtCfRm_fqqztwDkCaVnNaXo(this, tLObject));
    }

    public /* synthetic */ void lambda$null$4$PeopleNearbyActivity(TLObject tLObject) {
        this.reqId = 0;
        if (tLObject != null) {
            TL_updates tL_updates = (TL_updates) tLObject;
            getMessagesController().putUsers(tL_updates.users, false);
            getMessagesController().putChats(tL_updates.chats, false);
            this.users.clear();
            this.chats.clear();
            int size = tL_updates.updates.size();
            for (int i = 0; i < size; i++) {
                Update update = (Update) tL_updates.updates.get(i);
                if (update instanceof TL_updatePeerLocated) {
                    TL_updatePeerLocated tL_updatePeerLocated = (TL_updatePeerLocated) update;
                    int size2 = tL_updatePeerLocated.peers.size();
                    for (int i2 = 0; i2 < size2; i2++) {
                        TL_peerLocated tL_peerLocated = (TL_peerLocated) tL_updatePeerLocated.peers.get(i2);
                        if (tL_peerLocated.peer instanceof TL_peerUser) {
                            this.users.add(tL_peerLocated);
                        } else {
                            this.chats.add(tL_peerLocated);
                        }
                    }
                }
            }
            getLocationController().setCachedNearbyUsersAndChats(this.users, this.chats);
            checkForExpiredLocations();
            updateRows();
        }
        Runnable runnable = this.shortPollRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
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
        getLocationController().startLocationLookupForPeopleNearby(true);
    }

    public void onLocationAddressAvailable(String str, Location location) {
        this.currentGroupCreateAddress = str;
        this.currentGroupCreateLocation = location;
        ActionIntroActivity actionIntroActivity = this.groupCreateActivity;
        if (actionIntroActivity != null) {
            actionIntroActivity.setGroupCreateAddress(this.currentGroupCreateAddress, this.currentGroupCreateLocation);
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

    /* Access modifiers changed, original: protected */
    public void onBecomeFullyVisible() {
        super.onBecomeFullyVisible();
        this.groupCreateActivity = null;
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.newLocationAvailable) {
            sendRequest(false);
        } else if (i == NotificationCenter.newPeopleNearbyAvailable) {
            TL_updatePeerLocated tL_updatePeerLocated = (TL_updatePeerLocated) objArr[0];
            i2 = tL_updatePeerLocated.peers.size();
            for (int i3 = 0; i3 < i2; i3++) {
                ArrayList arrayList;
                TL_peerLocated tL_peerLocated = (TL_peerLocated) tL_updatePeerLocated.peers.get(i3);
                if (tL_peerLocated.peer instanceof TL_peerUser) {
                    arrayList = this.users;
                } else {
                    arrayList = this.chats;
                }
                int size = arrayList.size();
                Object obj = null;
                for (int i4 = 0; i4 < size; i4++) {
                    TL_peerLocated tL_peerLocated2 = (TL_peerLocated) arrayList.get(i4);
                    int i5 = tL_peerLocated2.peer.user_id;
                    if (i5 == 0 || i5 != tL_peerLocated.peer.user_id) {
                        i5 = tL_peerLocated2.peer.chat_id;
                        if (i5 == 0 || i5 != tL_peerLocated.peer.chat_id) {
                            int i6 = tL_peerLocated2.peer.channel_id;
                            if (i6 != 0) {
                                if (i6 != tL_peerLocated.peer.channel_id) {
                                }
                            }
                        }
                    }
                    arrayList.set(i4, tL_peerLocated);
                    obj = 1;
                }
                if (obj == null) {
                    arrayList.add(tL_peerLocated);
                }
            }
            getLocationController().setCachedNearbyUsersAndChats(this.users, this.chats);
            checkForExpiredLocations();
            updateRows();
        }
    }

    private void checkForExpiredLocations() {
        Runnable runnable = this.checkExpiredRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.checkExpiredRunnable = null;
        }
        int currentTime = getConnectionsManager().getCurrentTime();
        int i = 0;
        Object obj = null;
        int i2 = Integer.MAX_VALUE;
        while (i < 2) {
            ArrayList arrayList = i == 0 ? this.users : this.chats;
            int size = arrayList.size();
            Object obj2 = obj;
            int i3 = 0;
            while (i3 < size) {
                int i4 = ((TL_peerLocated) arrayList.get(i3)).expires;
                if (i4 <= currentTime) {
                    arrayList.remove(i3);
                    i3--;
                    size--;
                    obj2 = 1;
                } else {
                    i2 = Math.min(i2, i4);
                }
                i3++;
            }
            i++;
            obj = obj2;
        }
        if (!(obj == null || this.listViewAdapter == null)) {
            updateRows();
        }
        if (i2 != Integer.MAX_VALUE) {
            -$$Lambda$PeopleNearbyActivity$iLABDO3r6jvyupeMcaNenLlk8zY -__lambda_peoplenearbyactivity_ilabdo3r6jvyupemcanenllk8zy = new -$$Lambda$PeopleNearbyActivity$iLABDO3r6jvyupeMcaNenLlk8zY(this);
            this.checkExpiredRunnable = -__lambda_peoplenearbyactivity_ilabdo3r6jvyupemcanenllk8zy;
            AndroidUtilities.runOnUIThread(-__lambda_peoplenearbyactivity_ilabdo3r6jvyupemcanenllk8zy, (long) ((i2 - currentTime) * 1000));
        }
    }

    public /* synthetic */ void lambda$checkForExpiredLocations$6$PeopleNearbyActivity() {
        this.checkExpiredRunnable = null;
        checkForExpiredLocations();
    }

    public ThemeDescription[] getThemeDescriptions() {
        -$$Lambda$PeopleNearbyActivity$MByzzx_oaI0hBPBQHGQ3YioL0Q4 -__lambda_peoplenearbyactivity_mbyzzx_oai0hbpbqhgq3yiol0q4 = new -$$Lambda$PeopleNearbyActivity$MByzzx_oaI0hBPBQHGQ3YioL0Q4(this);
        r11 = new ThemeDescription[29];
        r11[0] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{ManageChatUserCell.class, ManageChatTextCell.class, HeaderCell.class, TextView.class}, null, null, null, "windowBackgroundWhite");
        r11[1] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND | ThemeDescription.FLAG_CHECKTAG, null, null, null, null, "windowBackgroundGray");
        r11[2] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND | ThemeDescription.FLAG_CHECKTAG, null, null, null, null, "windowBackgroundWhite");
        r11[3] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault");
        r11[4] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault");
        r11[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon");
        r11[6] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle");
        r11[7] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector");
        r11[8] = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21");
        r11[9] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, "divider");
        View view = this.listView;
        View view2 = view;
        r11[10] = new ThemeDescription(view2, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, "windowBackgroundGrayShadow");
        view = this.listView;
        Class[] clsArr = new Class[]{HeaderCell.class};
        String[] strArr = new String[1];
        strArr[0] = "textView";
        r11[11] = new ThemeDescription(view, 0, clsArr, strArr, null, null, null, "windowBackgroundWhiteBlueHeader");
        r11[12] = new ThemeDescription(this.listView, 0, new Class[]{ManageChatUserCell.class}, new String[]{"nameTextView"}, null, null, null, "windowBackgroundWhiteBlackText");
        ThemeDescriptionDelegate themeDescriptionDelegate = -__lambda_peoplenearbyactivity_mbyzzx_oai0hbpbqhgq3yiol0q4;
        r11[13] = new ThemeDescription(this.listView, 0, new Class[]{ManageChatUserCell.class}, new String[]{"statusColor"}, null, null, themeDescriptionDelegate, "windowBackgroundWhiteGrayText");
        r11[14] = new ThemeDescription(this.listView, 0, new Class[]{ManageChatUserCell.class}, new String[]{"statusOnlineColor"}, null, null, themeDescriptionDelegate, "windowBackgroundWhiteBlueText");
        r11[15] = new ThemeDescription(this.listView, 0, new Class[]{ManageChatUserCell.class}, null, new Drawable[]{Theme.avatar_broadcastDrawable, Theme.avatar_savedDrawable}, null, "avatar_text");
        -$$Lambda$PeopleNearbyActivity$MByzzx_oaI0hBPBQHGQ3YioL0Q4 -__lambda_peoplenearbyactivity_mbyzzx_oai0hbpbqhgq3yiol0q42 = -__lambda_peoplenearbyactivity_mbyzzx_oai0hbpbqhgq3yiol0q4;
        r11[16] = new ThemeDescription(null, 0, null, null, null, -__lambda_peoplenearbyactivity_mbyzzx_oai0hbpbqhgq3yiol0q42, "avatar_backgroundRed");
        r11[17] = new ThemeDescription(null, 0, null, null, null, -__lambda_peoplenearbyactivity_mbyzzx_oai0hbpbqhgq3yiol0q42, "avatar_backgroundOrange");
        r11[18] = new ThemeDescription(null, 0, null, null, null, -__lambda_peoplenearbyactivity_mbyzzx_oai0hbpbqhgq3yiol0q42, "avatar_backgroundViolet");
        r11[19] = new ThemeDescription(null, 0, null, null, null, -__lambda_peoplenearbyactivity_mbyzzx_oai0hbpbqhgq3yiol0q42, "avatar_backgroundGreen");
        r11[20] = new ThemeDescription(null, 0, null, null, null, -__lambda_peoplenearbyactivity_mbyzzx_oai0hbpbqhgq3yiol0q42, "avatar_backgroundCyan");
        r11[21] = new ThemeDescription(null, 0, null, null, null, -__lambda_peoplenearbyactivity_mbyzzx_oai0hbpbqhgq3yiol0q42, "avatar_backgroundBlue");
        r11[22] = new ThemeDescription(null, 0, null, null, null, -__lambda_peoplenearbyactivity_mbyzzx_oai0hbpbqhgq3yiol0q42, "avatar_backgroundPink");
        view = this.listView;
        int i = ThemeDescription.FLAG_BACKGROUNDFILTER;
        clsArr = new Class[]{HintInnerCell.class};
        strArr = new String[1];
        strArr[0] = "imageView";
        r11[23] = new ThemeDescription(view, i, clsArr, strArr, null, null, null, "chats_archiveBackground");
        r11[24] = new ThemeDescription(this.listView, 0, new Class[]{HintInnerCell.class}, new String[]{"messageTextView"}, null, null, null, "chats_message");
        view = this.listView;
        View view3 = view;
        r11[25] = new ThemeDescription(view3, ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText");
        view = this.listView;
        view3 = view;
        r11[26] = new ThemeDescription(view3, ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"imageView"}, null, null, null, "windowBackgroundWhiteGrayIcon");
        view = this.listView;
        view3 = view;
        r11[27] = new ThemeDescription(view3, ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"imageView"}, null, null, null, "windowBackgroundWhiteBlueButton");
        view = this.listView;
        view3 = view;
        r11[28] = new ThemeDescription(view3, ThemeDescription.FLAG_CHECKTAG, new Class[]{ManageChatTextCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlueIcon");
        return r11;
    }

    public /* synthetic */ void lambda$getThemeDescriptions$7$PeopleNearbyActivity() {
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
