package org.telegram.ui.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Build;
import android.text.TextUtils;
import android.util.SparseArray;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$BotInfo;
import org.telegram.tgnet.TLRPC$BotInlineResult;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$ChatFull;
import org.telegram.tgnet.TLRPC$TL_botInlineMessageMediaAuto;
import org.telegram.tgnet.TLRPC$TL_channelFull;
import org.telegram.tgnet.TLRPC$TL_chatBannedRights;
import org.telegram.tgnet.TLRPC$TL_contacts_resolveUsername;
import org.telegram.tgnet.TLRPC$TL_contacts_resolvedPeer;
import org.telegram.tgnet.TLRPC$TL_document;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_inlineBotSwitchPM;
import org.telegram.tgnet.TLRPC$TL_messages_botResults;
import org.telegram.tgnet.TLRPC$TL_photo;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.Adapters.MentionsAdapter;
import org.telegram.ui.Adapters.SearchAdapterHelper;
import org.telegram.ui.Cells.BotSwitchCell;
import org.telegram.ui.Cells.ContextLinkCell;
import org.telegram.ui.Cells.MentionCell;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.RecyclerListView;

public class MentionsAdapter extends RecyclerListView.SelectionAdapter {
    private SparseArray<TLRPC$BotInfo> botInfo;
    private int botsCount;
    private Runnable cancelDelayRunnable;
    /* access modifiers changed from: private */
    public int channelLastReqId;
    /* access modifiers changed from: private */
    public int channelReqId;
    private boolean contextMedia;
    private int contextQueryReqid;
    /* access modifiers changed from: private */
    public Runnable contextQueryRunnable;
    /* access modifiers changed from: private */
    public int contextUsernameReqid;
    /* access modifiers changed from: private */
    public int currentAccount = UserConfig.selectedAccount;
    /* access modifiers changed from: private */
    public MentionsAdapterDelegate delegate;
    private long dialog_id;
    /* access modifiers changed from: private */
    public TLRPC$User foundContextBot;
    private TLRPC$ChatFull info;
    private boolean inlineMediaEnabled = true;
    private boolean isDarkTheme;
    /* access modifiers changed from: private */
    public boolean isSearchingMentions;
    /* access modifiers changed from: private */
    public Location lastKnownLocation;
    /* access modifiers changed from: private */
    public int lastPosition;
    private String[] lastSearchKeyboardLanguage;
    /* access modifiers changed from: private */
    public String lastText;
    /* access modifiers changed from: private */
    public boolean lastUsernameOnly;
    private SendMessagesHelper.LocationProvider locationProvider = new SendMessagesHelper.LocationProvider(new SendMessagesHelper.LocationProvider.LocationProviderDelegate() {
        public void onLocationAcquired(Location location) {
            if (MentionsAdapter.this.foundContextBot != null && MentionsAdapter.this.foundContextBot.bot_inline_geo) {
                Location unused = MentionsAdapter.this.lastKnownLocation = location;
                MentionsAdapter mentionsAdapter = MentionsAdapter.this;
                mentionsAdapter.searchForContextBotResults(true, mentionsAdapter.foundContextBot, MentionsAdapter.this.searchingContextQuery, "");
            }
        }

        public void onUnableLocationAcquire() {
            MentionsAdapter.this.onLocationUnavailable();
        }
    }) {
        public void stop() {
            super.stop();
            Location unused = MentionsAdapter.this.lastKnownLocation = null;
        }
    };
    private Context mContext;
    /* access modifiers changed from: private */
    public ArrayList<MessageObject> messages;
    private boolean needBotContext = true;
    private boolean needUsernames = true;
    private String nextQueryOffset;
    /* access modifiers changed from: private */
    public boolean noUserName;
    private ChatActivity parentFragment;
    private int resultLength;
    private int resultStartPosition;
    private SearchAdapterHelper searchAdapterHelper;
    /* access modifiers changed from: private */
    public Runnable searchGlobalRunnable;
    private ArrayList<TLRPC$BotInlineResult> searchResultBotContext;
    private TLRPC$TL_inlineBotSwitchPM searchResultBotContextSwitch;
    private ArrayList<String> searchResultCommands;
    private ArrayList<String> searchResultCommandsHelp;
    private ArrayList<TLRPC$User> searchResultCommandsUsers;
    private ArrayList<String> searchResultHashtags;
    private ArrayList<MediaDataController.KeywordResult> searchResultSuggestions;
    /* access modifiers changed from: private */
    public ArrayList<TLRPC$User> searchResultUsernames;
    /* access modifiers changed from: private */
    public SparseArray<TLRPC$User> searchResultUsernamesMap;
    /* access modifiers changed from: private */
    public String searchingContextQuery;
    /* access modifiers changed from: private */
    public String searchingContextUsername;

    public interface MentionsAdapterDelegate {
        void needChangePanelVisibility(boolean z);

        void onContextClick(TLRPC$BotInlineResult tLRPC$BotInlineResult);

        void onContextSearch(boolean z);
    }

    static /* synthetic */ int access$1604(MentionsAdapter mentionsAdapter) {
        int i = mentionsAdapter.channelLastReqId + 1;
        mentionsAdapter.channelLastReqId = i;
        return i;
    }

    public MentionsAdapter(Context context, boolean z, long j, MentionsAdapterDelegate mentionsAdapterDelegate) {
        this.mContext = context;
        this.delegate = mentionsAdapterDelegate;
        this.isDarkTheme = z;
        this.dialog_id = j;
        SearchAdapterHelper searchAdapterHelper2 = new SearchAdapterHelper(true);
        this.searchAdapterHelper = searchAdapterHelper2;
        searchAdapterHelper2.setDelegate(new SearchAdapterHelper.SearchAdapterHelperDelegate() {
            public /* synthetic */ boolean canApplySearchResults(int i) {
                return SearchAdapterHelper.SearchAdapterHelperDelegate.CC.$default$canApplySearchResults(this, i);
            }

            public /* synthetic */ SparseArray<TLRPC$User> getExcludeUsers() {
                return SearchAdapterHelper.SearchAdapterHelperDelegate.CC.$default$getExcludeUsers(this);
            }

            public void onDataSetChanged(int i) {
                MentionsAdapter.this.notifyDataSetChanged();
            }

            public void onSetHashtags(ArrayList<SearchAdapterHelper.HashtagObject> arrayList, HashMap<String, SearchAdapterHelper.HashtagObject> hashMap) {
                if (MentionsAdapter.this.lastText != null) {
                    MentionsAdapter mentionsAdapter = MentionsAdapter.this;
                    mentionsAdapter.searchUsernameOrHashtag(mentionsAdapter.lastText, MentionsAdapter.this.lastPosition, MentionsAdapter.this.messages, MentionsAdapter.this.lastUsernameOnly);
                }
            }
        });
    }

    public void onDestroy() {
        SendMessagesHelper.LocationProvider locationProvider2 = this.locationProvider;
        if (locationProvider2 != null) {
            locationProvider2.stop();
        }
        Runnable runnable = this.contextQueryRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.contextQueryRunnable = null;
        }
        if (this.contextUsernameReqid != 0) {
            ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.contextUsernameReqid, true);
            this.contextUsernameReqid = 0;
        }
        if (this.contextQueryReqid != 0) {
            ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.contextQueryReqid, true);
            this.contextQueryReqid = 0;
        }
        this.foundContextBot = null;
        this.inlineMediaEnabled = true;
        this.searchingContextUsername = null;
        this.searchingContextQuery = null;
        this.noUserName = false;
    }

    public void setParentFragment(ChatActivity chatActivity) {
        this.parentFragment = chatActivity;
    }

    public void setChatInfo(TLRPC$ChatFull tLRPC$ChatFull) {
        ChatActivity chatActivity;
        TLRPC$Chat currentChat;
        this.currentAccount = UserConfig.selectedAccount;
        this.info = tLRPC$ChatFull;
        if (!(this.inlineMediaEnabled || this.foundContextBot == null || (chatActivity = this.parentFragment) == null || (currentChat = chatActivity.getCurrentChat()) == null)) {
            boolean canSendStickers = ChatObject.canSendStickers(currentChat);
            this.inlineMediaEnabled = canSendStickers;
            if (canSendStickers) {
                this.searchResultUsernames = null;
                notifyDataSetChanged();
                this.delegate.needChangePanelVisibility(false);
                processFoundUser(this.foundContextBot);
            }
        }
        String str = this.lastText;
        if (str != null) {
            searchUsernameOrHashtag(str, this.lastPosition, this.messages, this.lastUsernameOnly);
        }
    }

    public void setNeedUsernames(boolean z) {
        this.needUsernames = z;
    }

    public void setNeedBotContext(boolean z) {
        this.needBotContext = z;
    }

    public void setBotInfo(SparseArray<TLRPC$BotInfo> sparseArray) {
        this.botInfo = sparseArray;
    }

    public void setBotsCount(int i) {
        this.botsCount = i;
    }

    public void clearRecentHashtags() {
        this.searchAdapterHelper.clearRecentHashtags();
        this.searchResultHashtags.clear();
        notifyDataSetChanged();
        MentionsAdapterDelegate mentionsAdapterDelegate = this.delegate;
        if (mentionsAdapterDelegate != null) {
            mentionsAdapterDelegate.needChangePanelVisibility(false);
        }
    }

    public TLRPC$TL_inlineBotSwitchPM getBotContextSwitch() {
        return this.searchResultBotContextSwitch;
    }

    public int getContextBotId() {
        TLRPC$User tLRPC$User = this.foundContextBot;
        if (tLRPC$User != null) {
            return tLRPC$User.id;
        }
        return 0;
    }

    public TLRPC$User getContextBotUser() {
        return this.foundContextBot;
    }

    public String getContextBotName() {
        TLRPC$User tLRPC$User = this.foundContextBot;
        return tLRPC$User != null ? tLRPC$User.username : "";
    }

    /* access modifiers changed from: private */
    public void processFoundUser(TLRPC$User tLRPC$User) {
        ChatActivity chatActivity;
        TLRPC$Chat currentChat;
        this.contextUsernameReqid = 0;
        this.locationProvider.stop();
        if (tLRPC$User == null || !tLRPC$User.bot || tLRPC$User.bot_inline_placeholder == null) {
            this.foundContextBot = null;
            this.inlineMediaEnabled = true;
        } else {
            this.foundContextBot = tLRPC$User;
            ChatActivity chatActivity2 = this.parentFragment;
            if (!(chatActivity2 == null || (currentChat = chatActivity2.getCurrentChat()) == null)) {
                boolean canSendStickers = ChatObject.canSendStickers(currentChat);
                this.inlineMediaEnabled = canSendStickers;
                if (!canSendStickers) {
                    notifyDataSetChanged();
                    this.delegate.needChangePanelVisibility(true);
                    return;
                }
            }
            if (this.foundContextBot.bot_inline_geo) {
                SharedPreferences notificationsSettings = MessagesController.getNotificationsSettings(this.currentAccount);
                if (notificationsSettings.getBoolean("inlinegeo_" + this.foundContextBot.id, false) || (chatActivity = this.parentFragment) == null || chatActivity.getParentActivity() == null) {
                    checkLocationPermissionsOrStart();
                } else {
                    TLRPC$User tLRPC$User2 = this.foundContextBot;
                    AlertDialog.Builder builder = new AlertDialog.Builder((Context) this.parentFragment.getParentActivity());
                    builder.setTitle(LocaleController.getString("ShareYouLocationTitle", NUM));
                    builder.setMessage(LocaleController.getString("ShareYouLocationInline", NUM));
                    boolean[] zArr = new boolean[1];
                    builder.setPositiveButton(LocaleController.getString("OK", NUM), new DialogInterface.OnClickListener(zArr, tLRPC$User2) {
                        private final /* synthetic */ boolean[] f$1;
                        private final /* synthetic */ TLRPC$User f$2;

                        {
                            this.f$1 = r2;
                            this.f$2 = r3;
                        }

                        public final void onClick(DialogInterface dialogInterface, int i) {
                            MentionsAdapter.this.lambda$processFoundUser$0$MentionsAdapter(this.f$1, this.f$2, dialogInterface, i);
                        }
                    });
                    builder.setNegativeButton(LocaleController.getString("Cancel", NUM), new DialogInterface.OnClickListener(zArr) {
                        private final /* synthetic */ boolean[] f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void onClick(DialogInterface dialogInterface, int i) {
                            MentionsAdapter.this.lambda$processFoundUser$1$MentionsAdapter(this.f$1, dialogInterface, i);
                        }
                    });
                    this.parentFragment.showDialog(builder.create(), new DialogInterface.OnDismissListener(zArr) {
                        private final /* synthetic */ boolean[] f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void onDismiss(DialogInterface dialogInterface) {
                            MentionsAdapter.this.lambda$processFoundUser$2$MentionsAdapter(this.f$1, dialogInterface);
                        }
                    });
                }
            }
        }
        if (this.foundContextBot == null) {
            this.noUserName = true;
            return;
        }
        MentionsAdapterDelegate mentionsAdapterDelegate = this.delegate;
        if (mentionsAdapterDelegate != null) {
            mentionsAdapterDelegate.onContextSearch(true);
        }
        searchForContextBotResults(true, this.foundContextBot, this.searchingContextQuery, "");
    }

    public /* synthetic */ void lambda$processFoundUser$0$MentionsAdapter(boolean[] zArr, TLRPC$User tLRPC$User, DialogInterface dialogInterface, int i) {
        zArr[0] = true;
        if (tLRPC$User != null) {
            SharedPreferences.Editor edit = MessagesController.getNotificationsSettings(this.currentAccount).edit();
            edit.putBoolean("inlinegeo_" + tLRPC$User.id, true).commit();
            checkLocationPermissionsOrStart();
        }
    }

    public /* synthetic */ void lambda$processFoundUser$1$MentionsAdapter(boolean[] zArr, DialogInterface dialogInterface, int i) {
        zArr[0] = true;
        onLocationUnavailable();
    }

    public /* synthetic */ void lambda$processFoundUser$2$MentionsAdapter(boolean[] zArr, DialogInterface dialogInterface) {
        if (!zArr[0]) {
            onLocationUnavailable();
        }
    }

    private void searchForContextBot(String str, String str2) {
        String str3;
        String str4;
        String str5;
        TLRPC$User tLRPC$User = this.foundContextBot;
        if (tLRPC$User == null || (str4 = tLRPC$User.username) == null || !str4.equals(str) || (str5 = this.searchingContextQuery) == null || !str5.equals(str2)) {
            this.searchResultBotContext = null;
            this.searchResultBotContextSwitch = null;
            notifyDataSetChanged();
            if (this.foundContextBot != null) {
                if (this.inlineMediaEnabled || str == null || str2 == null) {
                    this.delegate.needChangePanelVisibility(false);
                } else {
                    return;
                }
            }
            Runnable runnable = this.contextQueryRunnable;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
                this.contextQueryRunnable = null;
            }
            if (TextUtils.isEmpty(str) || ((str3 = this.searchingContextUsername) != null && !str3.equals(str))) {
                if (this.contextUsernameReqid != 0) {
                    ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.contextUsernameReqid, true);
                    this.contextUsernameReqid = 0;
                }
                if (this.contextQueryReqid != 0) {
                    ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.contextQueryReqid, true);
                    this.contextQueryReqid = 0;
                }
                this.foundContextBot = null;
                this.inlineMediaEnabled = true;
                this.searchingContextUsername = null;
                this.searchingContextQuery = null;
                this.locationProvider.stop();
                this.noUserName = false;
                MentionsAdapterDelegate mentionsAdapterDelegate = this.delegate;
                if (mentionsAdapterDelegate != null) {
                    mentionsAdapterDelegate.onContextSearch(false);
                }
                if (str == null || str.length() == 0) {
                    return;
                }
            }
            if (str2 == null) {
                if (this.contextQueryReqid != 0) {
                    ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.contextQueryReqid, true);
                    this.contextQueryReqid = 0;
                }
                this.searchingContextQuery = null;
                MentionsAdapterDelegate mentionsAdapterDelegate2 = this.delegate;
                if (mentionsAdapterDelegate2 != null) {
                    mentionsAdapterDelegate2.onContextSearch(false);
                    return;
                }
                return;
            }
            MentionsAdapterDelegate mentionsAdapterDelegate3 = this.delegate;
            if (mentionsAdapterDelegate3 != null) {
                if (this.foundContextBot != null) {
                    mentionsAdapterDelegate3.onContextSearch(true);
                } else if (str.equals("gif")) {
                    this.searchingContextUsername = "gif";
                    this.delegate.onContextSearch(false);
                }
            }
            final MessagesController instance = MessagesController.getInstance(this.currentAccount);
            final MessagesStorage instance2 = MessagesStorage.getInstance(this.currentAccount);
            this.searchingContextQuery = str2;
            final String str6 = str2;
            final String str7 = str;
            AnonymousClass4 r1 = new Runnable() {
                public void run() {
                    if (MentionsAdapter.this.contextQueryRunnable == this) {
                        Runnable unused = MentionsAdapter.this.contextQueryRunnable = null;
                        if (MentionsAdapter.this.foundContextBot == null && !MentionsAdapter.this.noUserName) {
                            String unused2 = MentionsAdapter.this.searchingContextUsername = str7;
                            TLObject userOrChat = instance.getUserOrChat(MentionsAdapter.this.searchingContextUsername);
                            if (userOrChat instanceof TLRPC$User) {
                                MentionsAdapter.this.processFoundUser((TLRPC$User) userOrChat);
                                return;
                            }
                            TLRPC$TL_contacts_resolveUsername tLRPC$TL_contacts_resolveUsername = new TLRPC$TL_contacts_resolveUsername();
                            tLRPC$TL_contacts_resolveUsername.username = MentionsAdapter.this.searchingContextUsername;
                            MentionsAdapter mentionsAdapter = MentionsAdapter.this;
                            int unused3 = mentionsAdapter.contextUsernameReqid = ConnectionsManager.getInstance(mentionsAdapter.currentAccount).sendRequest(tLRPC$TL_contacts_resolveUsername, new RequestDelegate(str7, instance, instance2) {
                                private final /* synthetic */ String f$1;
                                private final /* synthetic */ MessagesController f$2;
                                private final /* synthetic */ MessagesStorage f$3;

                                {
                                    this.f$1 = r2;
                                    this.f$2 = r3;
                                    this.f$3 = r4;
                                }

                                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                                    MentionsAdapter.AnonymousClass4.this.lambda$run$1$MentionsAdapter$4(this.f$1, this.f$2, this.f$3, tLObject, tLRPC$TL_error);
                                }
                            });
                        } else if (!MentionsAdapter.this.noUserName) {
                            MentionsAdapter mentionsAdapter2 = MentionsAdapter.this;
                            mentionsAdapter2.searchForContextBotResults(true, mentionsAdapter2.foundContextBot, str6, "");
                        }
                    }
                }

                public /* synthetic */ void lambda$run$1$MentionsAdapter$4(String str, MessagesController messagesController, MessagesStorage messagesStorage, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    AndroidUtilities.runOnUIThread(new Runnable(str, tLRPC$TL_error, tLObject, messagesController, messagesStorage) {
                        private final /* synthetic */ String f$1;
                        private final /* synthetic */ TLRPC$TL_error f$2;
                        private final /* synthetic */ TLObject f$3;
                        private final /* synthetic */ MessagesController f$4;
                        private final /* synthetic */ MessagesStorage f$5;

                        {
                            this.f$1 = r2;
                            this.f$2 = r3;
                            this.f$3 = r4;
                            this.f$4 = r5;
                            this.f$5 = r6;
                        }

                        public final void run() {
                            MentionsAdapter.AnonymousClass4.this.lambda$null$0$MentionsAdapter$4(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
                        }
                    });
                }

                public /* synthetic */ void lambda$null$0$MentionsAdapter$4(String str, TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, MessagesController messagesController, MessagesStorage messagesStorage) {
                    if (MentionsAdapter.this.searchingContextUsername != null && MentionsAdapter.this.searchingContextUsername.equals(str)) {
                        TLRPC$User tLRPC$User = null;
                        if (tLRPC$TL_error == null) {
                            TLRPC$TL_contacts_resolvedPeer tLRPC$TL_contacts_resolvedPeer = (TLRPC$TL_contacts_resolvedPeer) tLObject;
                            if (!tLRPC$TL_contacts_resolvedPeer.users.isEmpty()) {
                                TLRPC$User tLRPC$User2 = tLRPC$TL_contacts_resolvedPeer.users.get(0);
                                messagesController.putUser(tLRPC$User2, false);
                                messagesStorage.putUsersAndChats(tLRPC$TL_contacts_resolvedPeer.users, (ArrayList<TLRPC$Chat>) null, true, true);
                                tLRPC$User = tLRPC$User2;
                            }
                        }
                        MentionsAdapter.this.processFoundUser(tLRPC$User);
                    }
                }
            };
            this.contextQueryRunnable = r1;
            AndroidUtilities.runOnUIThread(r1, 400);
        }
    }

    /* access modifiers changed from: private */
    public void onLocationUnavailable() {
        TLRPC$User tLRPC$User = this.foundContextBot;
        if (tLRPC$User != null && tLRPC$User.bot_inline_geo) {
            Location location = new Location("network");
            this.lastKnownLocation = location;
            location.setLatitude(-1000.0d);
            this.lastKnownLocation.setLongitude(-1000.0d);
            searchForContextBotResults(true, this.foundContextBot, this.searchingContextQuery, "");
        }
    }

    private void checkLocationPermissionsOrStart() {
        ChatActivity chatActivity = this.parentFragment;
        if (chatActivity != null && chatActivity.getParentActivity() != null) {
            if (Build.VERSION.SDK_INT < 23 || this.parentFragment.getParentActivity().checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION") == 0) {
                TLRPC$User tLRPC$User = this.foundContextBot;
                if (tLRPC$User != null && tLRPC$User.bot_inline_geo) {
                    this.locationProvider.start();
                    return;
                }
                return;
            }
            this.parentFragment.getParentActivity().requestPermissions(new String[]{"android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"}, 2);
        }
    }

    public void setSearchingMentions(boolean z) {
        this.isSearchingMentions = z;
    }

    public String getBotCaption() {
        TLRPC$User tLRPC$User = this.foundContextBot;
        if (tLRPC$User != null) {
            return tLRPC$User.bot_inline_placeholder;
        }
        String str = this.searchingContextUsername;
        if (str == null || !str.equals("gif")) {
            return null;
        }
        return "Search GIFs";
    }

    public void searchForContextBotForNextOffset() {
        String str;
        TLRPC$User tLRPC$User;
        String str2;
        if (this.contextQueryReqid == 0 && (str = this.nextQueryOffset) != null && str.length() != 0 && (tLRPC$User = this.foundContextBot) != null && (str2 = this.searchingContextQuery) != null) {
            searchForContextBotResults(true, tLRPC$User, str2, this.nextQueryOffset);
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x006a, code lost:
        r1 = r8.lastKnownLocation;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void searchForContextBotResults(boolean r17, org.telegram.tgnet.TLRPC$User r18, java.lang.String r19, java.lang.String r20) {
        /*
            r16 = this;
            r8 = r16
            r9 = r18
            r10 = r19
            r11 = r20
            int r0 = r8.contextQueryReqid
            r1 = 0
            r12 = 1
            if (r0 == 0) goto L_0x001b
            int r0 = r8.currentAccount
            org.telegram.tgnet.ConnectionsManager r0 = org.telegram.tgnet.ConnectionsManager.getInstance(r0)
            int r2 = r8.contextQueryReqid
            r0.cancelRequest(r2, r12)
            r8.contextQueryReqid = r1
        L_0x001b:
            boolean r0 = r8.inlineMediaEnabled
            if (r0 != 0) goto L_0x0027
            org.telegram.ui.Adapters.MentionsAdapter$MentionsAdapterDelegate r0 = r8.delegate
            if (r0 == 0) goto L_0x0026
            r0.onContextSearch(r1)
        L_0x0026:
            return
        L_0x0027:
            if (r10 == 0) goto L_0x012a
            if (r9 != 0) goto L_0x002d
            goto L_0x012a
        L_0x002d:
            boolean r0 = r9.bot_inline_geo
            if (r0 == 0) goto L_0x0036
            android.location.Location r0 = r8.lastKnownLocation
            if (r0 != 0) goto L_0x0036
            return
        L_0x0036:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            long r1 = r8.dialog_id
            r0.append(r1)
            java.lang.String r1 = "_"
            r0.append(r1)
            r0.append(r10)
            r0.append(r1)
            r0.append(r11)
            r0.append(r1)
            long r2 = r8.dialog_id
            r0.append(r2)
            r0.append(r1)
            int r2 = r9.id
            r0.append(r2)
            r0.append(r1)
            boolean r1 = r9.bot_inline_geo
            r13 = -4571364728013586432(0xCLASSNAMEfNUM, double:-1000.0)
            if (r1 == 0) goto L_0x0088
            android.location.Location r1 = r8.lastKnownLocation
            if (r1 == 0) goto L_0x0088
            double r1 = r1.getLatitude()
            int r3 = (r1 > r13 ? 1 : (r1 == r13 ? 0 : -1))
            if (r3 == 0) goto L_0x0088
            android.location.Location r1 = r8.lastKnownLocation
            double r1 = r1.getLatitude()
            android.location.Location r3 = r8.lastKnownLocation
            double r3 = r3.getLongitude()
            double r1 = r1 + r3
            java.lang.Double r1 = java.lang.Double.valueOf(r1)
            goto L_0x008a
        L_0x0088:
            java.lang.String r1 = ""
        L_0x008a:
            r0.append(r1)
            java.lang.String r15 = r0.toString()
            int r0 = r8.currentAccount
            org.telegram.messenger.MessagesStorage r7 = org.telegram.messenger.MessagesStorage.getInstance(r0)
            org.telegram.ui.Adapters.-$$Lambda$MentionsAdapter$5habXq1r64920QdCkG9j6xMgM4U r6 = new org.telegram.ui.Adapters.-$$Lambda$MentionsAdapter$5habXq1r64920QdCkG9j6xMgM4U
            r0 = r6
            r1 = r16
            r2 = r19
            r3 = r17
            r4 = r18
            r5 = r20
            r12 = r6
            r6 = r7
            r13 = r7
            r7 = r15
            r0.<init>(r2, r3, r4, r5, r6, r7)
            if (r17 == 0) goto L_0x00b2
            r13.getBotCache(r15, r12)
            goto L_0x0129
        L_0x00b2:
            org.telegram.tgnet.TLRPC$TL_messages_getInlineBotResults r0 = new org.telegram.tgnet.TLRPC$TL_messages_getInlineBotResults
            r0.<init>()
            int r1 = r8.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
            org.telegram.tgnet.TLRPC$InputUser r1 = r1.getInputUser((org.telegram.tgnet.TLRPC$User) r9)
            r0.bot = r1
            r0.query = r10
            r0.offset = r11
            boolean r1 = r9.bot_inline_geo
            if (r1 == 0) goto L_0x0103
            android.location.Location r1 = r8.lastKnownLocation
            if (r1 == 0) goto L_0x0103
            double r1 = r1.getLatitude()
            r3 = -4571364728013586432(0xCLASSNAMEfNUM, double:-1000.0)
            int r5 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r5 == 0) goto L_0x0103
            int r1 = r0.flags
            r2 = 1
            r1 = r1 | r2
            r0.flags = r1
            org.telegram.tgnet.TLRPC$TL_inputGeoPoint r1 = new org.telegram.tgnet.TLRPC$TL_inputGeoPoint
            r1.<init>()
            r0.geo_point = r1
            android.location.Location r2 = r8.lastKnownLocation
            double r2 = r2.getLatitude()
            double r2 = org.telegram.messenger.AndroidUtilities.fixLocationCoord(r2)
            r1.lat = r2
            org.telegram.tgnet.TLRPC$InputGeoPoint r1 = r0.geo_point
            android.location.Location r2 = r8.lastKnownLocation
            double r2 = r2.getLongitude()
            double r2 = org.telegram.messenger.AndroidUtilities.fixLocationCoord(r2)
            r1._long = r2
        L_0x0103:
            long r1 = r8.dialog_id
            int r2 = (int) r1
            if (r2 == 0) goto L_0x0115
            int r1 = r8.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
            org.telegram.tgnet.TLRPC$InputPeer r1 = r1.getInputPeer(r2)
            r0.peer = r1
            goto L_0x011c
        L_0x0115:
            org.telegram.tgnet.TLRPC$TL_inputPeerEmpty r1 = new org.telegram.tgnet.TLRPC$TL_inputPeerEmpty
            r1.<init>()
            r0.peer = r1
        L_0x011c:
            int r1 = r8.currentAccount
            org.telegram.tgnet.ConnectionsManager r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r1)
            r2 = 2
            int r0 = r1.sendRequest(r0, r12, r2)
            r8.contextQueryReqid = r0
        L_0x0129:
            return
        L_0x012a:
            r0 = 0
            r8.searchingContextQuery = r0
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Adapters.MentionsAdapter.searchForContextBotResults(boolean, org.telegram.tgnet.TLRPC$User, java.lang.String, java.lang.String):void");
    }

    public /* synthetic */ void lambda$searchForContextBotResults$4$MentionsAdapter(String str, boolean z, TLRPC$User tLRPC$User, String str2, MessagesStorage messagesStorage, String str3, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(str, z, tLObject, tLRPC$User, str2, messagesStorage, str3) {
            private final /* synthetic */ String f$1;
            private final /* synthetic */ boolean f$2;
            private final /* synthetic */ TLObject f$3;
            private final /* synthetic */ TLRPC$User f$4;
            private final /* synthetic */ String f$5;
            private final /* synthetic */ MessagesStorage f$6;
            private final /* synthetic */ String f$7;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
                this.f$5 = r6;
                this.f$6 = r7;
                this.f$7 = r8;
            }

            public final void run() {
                MentionsAdapter.this.lambda$null$3$MentionsAdapter(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7);
            }
        });
    }

    public /* synthetic */ void lambda$null$3$MentionsAdapter(String str, boolean z, TLObject tLObject, TLRPC$User tLRPC$User, String str2, MessagesStorage messagesStorage, String str3) {
        boolean z2;
        if (str.equals(this.searchingContextQuery)) {
            boolean z3 = false;
            this.contextQueryReqid = 0;
            if (!z || tLObject != null) {
                MentionsAdapterDelegate mentionsAdapterDelegate = this.delegate;
                if (mentionsAdapterDelegate != null) {
                    mentionsAdapterDelegate.onContextSearch(false);
                }
            } else {
                searchForContextBotResults(false, tLRPC$User, str, str2);
            }
            if (tLObject instanceof TLRPC$TL_messages_botResults) {
                TLRPC$TL_messages_botResults tLRPC$TL_messages_botResults = (TLRPC$TL_messages_botResults) tLObject;
                if (!z && tLRPC$TL_messages_botResults.cache_time != 0) {
                    messagesStorage.saveBotCache(str3, tLRPC$TL_messages_botResults);
                }
                this.nextQueryOffset = tLRPC$TL_messages_botResults.next_offset;
                if (this.searchResultBotContextSwitch == null) {
                    this.searchResultBotContextSwitch = tLRPC$TL_messages_botResults.switch_pm;
                }
                int i = 0;
                while (i < tLRPC$TL_messages_botResults.results.size()) {
                    TLRPC$BotInlineResult tLRPC$BotInlineResult = tLRPC$TL_messages_botResults.results.get(i);
                    if (!(tLRPC$BotInlineResult.document instanceof TLRPC$TL_document) && !(tLRPC$BotInlineResult.photo instanceof TLRPC$TL_photo) && !"game".equals(tLRPC$BotInlineResult.type) && tLRPC$BotInlineResult.content == null && (tLRPC$BotInlineResult.send_message instanceof TLRPC$TL_botInlineMessageMediaAuto)) {
                        tLRPC$TL_messages_botResults.results.remove(i);
                        i--;
                    }
                    tLRPC$BotInlineResult.query_id = tLRPC$TL_messages_botResults.query_id;
                    i++;
                }
                if (this.searchResultBotContext == null || str2.length() == 0) {
                    this.searchResultBotContext = tLRPC$TL_messages_botResults.results;
                    this.contextMedia = tLRPC$TL_messages_botResults.gallery;
                    z2 = false;
                } else {
                    this.searchResultBotContext.addAll(tLRPC$TL_messages_botResults.results);
                    if (tLRPC$TL_messages_botResults.results.isEmpty()) {
                        this.nextQueryOffset = "";
                    }
                    z2 = true;
                }
                Runnable runnable = this.cancelDelayRunnable;
                if (runnable != null) {
                    AndroidUtilities.cancelRunOnUIThread(runnable);
                    this.cancelDelayRunnable = null;
                }
                this.searchResultHashtags = null;
                this.searchResultUsernames = null;
                this.searchResultUsernamesMap = null;
                this.searchResultCommands = null;
                this.searchResultSuggestions = null;
                this.searchResultCommandsHelp = null;
                this.searchResultCommandsUsers = null;
                if (z2) {
                    int i2 = this.searchResultBotContextSwitch != null ? 1 : 0;
                    notifyItemChanged(((this.searchResultBotContext.size() - tLRPC$TL_messages_botResults.results.size()) + i2) - 1);
                    notifyItemRangeInserted((this.searchResultBotContext.size() - tLRPC$TL_messages_botResults.results.size()) + i2, tLRPC$TL_messages_botResults.results.size());
                } else {
                    notifyDataSetChanged();
                }
                MentionsAdapterDelegate mentionsAdapterDelegate2 = this.delegate;
                if (!this.searchResultBotContext.isEmpty() || this.searchResultBotContextSwitch != null) {
                    z3 = true;
                }
                mentionsAdapterDelegate2.needChangePanelVisibility(z3);
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:118:0x01ce  */
    /* JADX WARNING: Removed duplicated region for block: B:120:0x01d4  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void searchUsernameOrHashtag(java.lang.String r17, int r18, java.util.ArrayList<org.telegram.messenger.MessageObject> r19, boolean r20) {
        /*
            r16 = this;
            r7 = r16
            r0 = r17
            r1 = r18
            r2 = r19
            r3 = r20
            java.lang.Runnable r4 = r7.cancelDelayRunnable
            r5 = 0
            if (r4 == 0) goto L_0x0014
            org.telegram.messenger.AndroidUtilities.cancelRunOnUIThread(r4)
            r7.cancelDelayRunnable = r5
        L_0x0014:
            int r4 = r7.channelReqId
            r6 = 0
            r8 = 1
            if (r4 == 0) goto L_0x0027
            int r4 = r7.currentAccount
            org.telegram.tgnet.ConnectionsManager r4 = org.telegram.tgnet.ConnectionsManager.getInstance(r4)
            int r9 = r7.channelReqId
            r4.cancelRequest(r9, r8)
            r7.channelReqId = r6
        L_0x0027:
            java.lang.Runnable r4 = r7.searchGlobalRunnable
            if (r4 == 0) goto L_0x0030
            org.telegram.messenger.AndroidUtilities.cancelRunOnUIThread(r4)
            r7.searchGlobalRunnable = r5
        L_0x0030:
            boolean r4 = android.text.TextUtils.isEmpty(r17)
            if (r4 == 0) goto L_0x0041
            r7.searchForContextBot(r5, r5)
            org.telegram.ui.Adapters.MentionsAdapter$MentionsAdapterDelegate r0 = r7.delegate
            r0.needChangePanelVisibility(r6)
            r7.lastText = r5
            return
        L_0x0041:
            int r4 = r17.length()
            if (r4 <= 0) goto L_0x004a
            int r4 = r1 + -1
            goto L_0x004b
        L_0x004a:
            r4 = r1
        L_0x004b:
            r7.lastText = r5
            r7.lastUsernameOnly = r3
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            r10 = 64
            r11 = 3
            r12 = 32
            if (r3 != 0) goto L_0x00dd
            boolean r13 = r7.needBotContext
            if (r13 == 0) goto L_0x00dd
            char r13 = r0.charAt(r6)
            if (r13 != r10) goto L_0x00dd
            int r13 = r0.indexOf(r12)
            int r14 = r17.length()
            java.lang.String r15 = ""
            if (r13 <= 0) goto L_0x007b
            java.lang.String r14 = r0.substring(r8, r13)
            int r13 = r13 + r8
            java.lang.String r13 = r0.substring(r13)
            goto L_0x00a3
        L_0x007b:
            int r13 = r14 + -1
            char r13 = r0.charAt(r13)
            r10 = 116(0x74, float:1.63E-43)
            if (r13 != r10) goto L_0x009e
            int r10 = r14 + -2
            char r10 = r0.charAt(r10)
            r13 = 111(0x6f, float:1.56E-43)
            if (r10 != r13) goto L_0x009e
            int r14 = r14 - r11
            char r10 = r0.charAt(r14)
            r13 = 98
            if (r10 != r13) goto L_0x009e
            java.lang.String r14 = r0.substring(r8)
            r13 = r15
            goto L_0x00a3
        L_0x009e:
            r7.searchForContextBot(r5, r5)
            r13 = r5
            r14 = r13
        L_0x00a3:
            if (r14 == 0) goto L_0x00d9
            int r10 = r14.length()
            if (r10 < r8) goto L_0x00d9
            r10 = 1
        L_0x00ac:
            int r11 = r14.length()
            if (r10 >= r11) goto L_0x00d8
            char r11 = r14.charAt(r10)
            r12 = 48
            if (r11 < r12) goto L_0x00be
            r12 = 57
            if (r11 <= r12) goto L_0x00d3
        L_0x00be:
            r12 = 97
            if (r11 < r12) goto L_0x00c6
            r12 = 122(0x7a, float:1.71E-43)
            if (r11 <= r12) goto L_0x00d3
        L_0x00c6:
            r12 = 65
            if (r11 < r12) goto L_0x00ce
            r12 = 90
            if (r11 <= r12) goto L_0x00d3
        L_0x00ce:
            r12 = 95
            if (r11 == r12) goto L_0x00d3
            goto L_0x00d9
        L_0x00d3:
            int r10 = r10 + 1
            r12 = 32
            goto L_0x00ac
        L_0x00d8:
            r15 = r14
        L_0x00d9:
            r7.searchForContextBot(r15, r13)
            goto L_0x00e0
        L_0x00dd:
            r7.searchForContextBot(r5, r5)
        L_0x00e0:
            org.telegram.tgnet.TLRPC$User r10 = r7.foundContextBot
            if (r10 == 0) goto L_0x00e5
            return
        L_0x00e5:
            int r10 = r7.currentAccount
            org.telegram.messenger.MessagesController r10 = org.telegram.messenger.MessagesController.getInstance(r10)
            r12 = -1
            if (r3 == 0) goto L_0x0101
            java.lang.String r0 = r0.substring(r8)
            r9.append(r0)
            r7.resultStartPosition = r6
            int r0 = r9.length()
            r7.resultLength = r0
            r0 = 0
        L_0x00fe:
            r4 = -1
            goto L_0x01cc
        L_0x0101:
            if (r4 < 0) goto L_0x01c9
            int r13 = r17.length()
            if (r4 < r13) goto L_0x010d
            r5 = 64
            goto L_0x01c4
        L_0x010d:
            char r13 = r0.charAt(r4)
            r14 = 58
            if (r4 == 0) goto L_0x012e
            int r15 = r4 + -1
            char r11 = r0.charAt(r15)
            r5 = 32
            if (r11 == r5) goto L_0x012e
            char r5 = r0.charAt(r15)
            r11 = 10
            if (r5 == r11) goto L_0x012e
            if (r13 != r14) goto L_0x012a
            goto L_0x012e
        L_0x012a:
            r5 = 64
            goto L_0x01c1
        L_0x012e:
            r5 = 64
            if (r13 != r5) goto L_0x015a
            boolean r11 = r7.needUsernames
            if (r11 != 0) goto L_0x013c
            boolean r11 = r7.needBotContext
            if (r11 == 0) goto L_0x01c1
            if (r4 != 0) goto L_0x01c1
        L_0x013c:
            org.telegram.tgnet.TLRPC$ChatFull r5 = r7.info
            if (r5 != 0) goto L_0x014e
            if (r4 == 0) goto L_0x014e
            r7.lastText = r0
            r7.lastPosition = r1
            r7.messages = r2
            org.telegram.ui.Adapters.MentionsAdapter$MentionsAdapterDelegate r0 = r7.delegate
            r0.needChangePanelVisibility(r6)
            return
        L_0x014e:
            r7.resultStartPosition = r4
            int r0 = r9.length()
            int r0 = r0 + r8
            r7.resultLength = r0
            r0 = 0
            goto L_0x01cc
        L_0x015a:
            r11 = 35
            if (r13 != r11) goto L_0x0180
            org.telegram.ui.Adapters.SearchAdapterHelper r5 = r7.searchAdapterHelper
            boolean r5 = r5.loadRecentHashtags()
            if (r5 == 0) goto L_0x0174
            r7.resultStartPosition = r4
            int r0 = r9.length()
            int r0 = r0 + r8
            r7.resultLength = r0
            r9.insert(r6, r13)
            r0 = 1
            goto L_0x00fe
        L_0x0174:
            r7.lastText = r0
            r7.lastPosition = r1
            r7.messages = r2
            org.telegram.ui.Adapters.MentionsAdapter$MentionsAdapterDelegate r0 = r7.delegate
            r0.needChangePanelVisibility(r6)
            return
        L_0x0180:
            if (r4 != 0) goto L_0x0196
            android.util.SparseArray<org.telegram.tgnet.TLRPC$BotInfo> r11 = r7.botInfo
            if (r11 == 0) goto L_0x0196
            r11 = 47
            if (r13 != r11) goto L_0x0196
            r7.resultStartPosition = r4
            int r0 = r9.length()
            int r0 = r0 + r8
            r7.resultLength = r0
            r0 = 2
            goto L_0x00fe
        L_0x0196:
            if (r13 != r14) goto L_0x01c1
            int r11 = r9.length()
            if (r11 <= 0) goto L_0x01c1
            char r11 = r9.charAt(r6)
            java.lang.String r14 = " !\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~\n"
            int r11 = r14.indexOf(r11)
            if (r11 < 0) goto L_0x01ac
            r11 = 1
            goto L_0x01ad
        L_0x01ac:
            r11 = 0
        L_0x01ad:
            if (r11 == 0) goto L_0x01b5
            int r11 = r9.length()
            if (r11 <= r8) goto L_0x01c1
        L_0x01b5:
            r7.resultStartPosition = r4
            int r0 = r9.length()
            int r0 = r0 + r8
            r7.resultLength = r0
            r0 = 3
            goto L_0x00fe
        L_0x01c1:
            r9.insert(r6, r13)
        L_0x01c4:
            int r4 = r4 + -1
            r5 = 0
            goto L_0x0101
        L_0x01c9:
            r0 = -1
            goto L_0x00fe
        L_0x01cc:
            if (r0 != r12) goto L_0x01d4
            org.telegram.ui.Adapters.MentionsAdapter$MentionsAdapterDelegate r0 = r7.delegate
            r0.needChangePanelVisibility(r6)
            return
        L_0x01d4:
            if (r0 != 0) goto L_0x03cf
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r1 = 0
        L_0x01dc:
            r5 = 100
            int r11 = r19.size()
            int r5 = java.lang.Math.min(r5, r11)
            if (r1 >= r5) goto L_0x0206
            java.lang.Object r5 = r2.get(r1)
            org.telegram.messenger.MessageObject r5 = (org.telegram.messenger.MessageObject) r5
            org.telegram.tgnet.TLRPC$Message r5 = r5.messageOwner
            int r5 = r5.from_id
            java.lang.Integer r11 = java.lang.Integer.valueOf(r5)
            boolean r11 = r0.contains(r11)
            if (r11 != 0) goto L_0x0203
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            r0.add(r5)
        L_0x0203:
            int r1 = r1 + 1
            goto L_0x01dc
        L_0x0206:
            java.lang.String r1 = r9.toString()
            java.lang.String r5 = r1.toLowerCase()
            r1 = 32
            int r1 = r5.indexOf(r1)
            if (r1 < 0) goto L_0x0218
            r1 = 1
            goto L_0x0219
        L_0x0218:
            r1 = 0
        L_0x0219:
            java.util.ArrayList r9 = new java.util.ArrayList
            r9.<init>()
            android.util.SparseArray r2 = new android.util.SparseArray
            r2.<init>()
            android.util.SparseArray r11 = new android.util.SparseArray
            r11.<init>()
            int r12 = r7.currentAccount
            org.telegram.messenger.MediaDataController r12 = org.telegram.messenger.MediaDataController.getInstance(r12)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_topPeer> r12 = r12.inlineBots
            r13 = 5
            if (r3 != 0) goto L_0x028f
            boolean r14 = r7.needBotContext
            if (r14 == 0) goto L_0x028f
            if (r4 != 0) goto L_0x028f
            boolean r4 = r12.isEmpty()
            if (r4 != 0) goto L_0x028f
            r4 = 0
            r14 = 0
        L_0x0241:
            int r15 = r12.size()
            if (r4 >= r15) goto L_0x028f
            java.lang.Object r15 = r12.get(r4)
            org.telegram.tgnet.TLRPC$TL_topPeer r15 = (org.telegram.tgnet.TLRPC$TL_topPeer) r15
            org.telegram.tgnet.TLRPC$Peer r15 = r15.peer
            int r15 = r15.user_id
            java.lang.Integer r15 = java.lang.Integer.valueOf(r15)
            org.telegram.tgnet.TLRPC$User r15 = r10.getUser(r15)
            if (r15 != 0) goto L_0x025c
            goto L_0x028b
        L_0x025c:
            java.lang.String r6 = r15.username
            if (r6 == 0) goto L_0x0288
            int r6 = r6.length()
            if (r6 <= 0) goto L_0x0288
            int r6 = r5.length()
            if (r6 <= 0) goto L_0x0278
            java.lang.String r6 = r15.username
            java.lang.String r6 = r6.toLowerCase()
            boolean r6 = r6.startsWith(r5)
            if (r6 != 0) goto L_0x027e
        L_0x0278:
            int r6 = r5.length()
            if (r6 != 0) goto L_0x0288
        L_0x027e:
            r9.add(r15)
            int r6 = r15.id
            r2.put(r6, r15)
            int r14 = r14 + 1
        L_0x0288:
            if (r14 != r13) goto L_0x028b
            goto L_0x028f
        L_0x028b:
            int r4 = r4 + 1
            r6 = 0
            goto L_0x0241
        L_0x028f:
            org.telegram.ui.ChatActivity r4 = r7.parentFragment
            if (r4 == 0) goto L_0x0298
            org.telegram.tgnet.TLRPC$Chat r4 = r4.getCurrentChat()
            goto L_0x02a8
        L_0x0298:
            org.telegram.tgnet.TLRPC$ChatFull r4 = r7.info
            if (r4 == 0) goto L_0x02a7
            int r4 = r4.id
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
            org.telegram.tgnet.TLRPC$Chat r4 = r10.getChat(r4)
            goto L_0x02a8
        L_0x02a7:
            r4 = 0
        L_0x02a8:
            if (r4 == 0) goto L_0x037f
            org.telegram.tgnet.TLRPC$ChatFull r6 = r7.info
            if (r6 == 0) goto L_0x037f
            org.telegram.tgnet.TLRPC$ChatParticipants r6 = r6.participants
            if (r6 == 0) goto L_0x037f
            boolean r6 = org.telegram.messenger.ChatObject.isChannel(r4)
            if (r6 == 0) goto L_0x02bc
            boolean r6 = r4.megagroup
            if (r6 == 0) goto L_0x037f
        L_0x02bc:
            r6 = 0
        L_0x02bd:
            org.telegram.tgnet.TLRPC$ChatFull r12 = r7.info
            org.telegram.tgnet.TLRPC$ChatParticipants r12 = r12.participants
            java.util.ArrayList<org.telegram.tgnet.TLRPC$ChatParticipant> r12 = r12.participants
            int r12 = r12.size()
            if (r6 >= r12) goto L_0x037f
            org.telegram.tgnet.TLRPC$ChatFull r12 = r7.info
            org.telegram.tgnet.TLRPC$ChatParticipants r12 = r12.participants
            java.util.ArrayList<org.telegram.tgnet.TLRPC$ChatParticipant> r12 = r12.participants
            java.lang.Object r12 = r12.get(r6)
            org.telegram.tgnet.TLRPC$ChatParticipant r12 = (org.telegram.tgnet.TLRPC$ChatParticipant) r12
            int r12 = r12.user_id
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            org.telegram.tgnet.TLRPC$User r12 = r10.getUser(r12)
            if (r12 == 0) goto L_0x037b
            if (r3 != 0) goto L_0x02e9
            boolean r14 = org.telegram.messenger.UserObject.isUserSelf(r12)
            if (r14 != 0) goto L_0x037b
        L_0x02e9:
            int r14 = r12.id
            int r14 = r2.indexOfKey(r14)
            if (r14 < 0) goto L_0x02f3
            goto L_0x037b
        L_0x02f3:
            int r14 = r5.length()
            if (r14 != 0) goto L_0x0302
            boolean r14 = r12.deleted
            if (r14 != 0) goto L_0x037b
            r9.add(r12)
            goto L_0x037b
        L_0x0302:
            java.lang.String r14 = r12.username
            if (r14 == 0) goto L_0x0321
            int r14 = r14.length()
            if (r14 <= 0) goto L_0x0321
            java.lang.String r14 = r12.username
            java.lang.String r14 = r14.toLowerCase()
            boolean r14 = r14.startsWith(r5)
            if (r14 == 0) goto L_0x0321
            r9.add(r12)
            int r14 = r12.id
            r11.put(r14, r12)
            goto L_0x037b
        L_0x0321:
            java.lang.String r14 = r12.first_name
            if (r14 == 0) goto L_0x0340
            int r14 = r14.length()
            if (r14 <= 0) goto L_0x0340
            java.lang.String r14 = r12.first_name
            java.lang.String r14 = r14.toLowerCase()
            boolean r14 = r14.startsWith(r5)
            if (r14 == 0) goto L_0x0340
            r9.add(r12)
            int r14 = r12.id
            r11.put(r14, r12)
            goto L_0x037b
        L_0x0340:
            java.lang.String r14 = r12.last_name
            if (r14 == 0) goto L_0x035f
            int r14 = r14.length()
            if (r14 <= 0) goto L_0x035f
            java.lang.String r14 = r12.last_name
            java.lang.String r14 = r14.toLowerCase()
            boolean r14 = r14.startsWith(r5)
            if (r14 == 0) goto L_0x035f
            r9.add(r12)
            int r14 = r12.id
            r11.put(r14, r12)
            goto L_0x037b
        L_0x035f:
            if (r1 == 0) goto L_0x037b
            java.lang.String r14 = r12.first_name
            java.lang.String r15 = r12.last_name
            java.lang.String r14 = org.telegram.messenger.ContactsController.formatName(r14, r15)
            java.lang.String r14 = r14.toLowerCase()
            boolean r14 = r14.startsWith(r5)
            if (r14 == 0) goto L_0x037b
            r9.add(r12)
            int r14 = r12.id
            r11.put(r14, r12)
        L_0x037b:
            int r6 = r6 + 1
            goto L_0x02bd
        L_0x037f:
            org.telegram.ui.Adapters.-$$Lambda$MentionsAdapter$RtUwcbbmysCllxWaHytbPO9oFzY r1 = new org.telegram.ui.Adapters.-$$Lambda$MentionsAdapter$RtUwcbbmysCllxWaHytbPO9oFzY
            r1.<init>(r11, r0)
            java.util.Collections.sort(r9, r1)
            r0 = 0
            r7.searchResultHashtags = r0
            r7.searchResultCommands = r0
            r7.searchResultCommandsHelp = r0
            r7.searchResultCommandsUsers = r0
            r7.searchResultSuggestions = r0
            if (r4 == 0) goto L_0x03ca
            boolean r0 = r4.megagroup
            if (r0 == 0) goto L_0x03ca
            int r0 = r5.length()
            if (r0 <= 0) goto L_0x03ca
            int r0 = r9.size()
            if (r0 >= r13) goto L_0x03b1
            org.telegram.ui.Adapters.-$$Lambda$MentionsAdapter$_MTgLbVZU0vjX841sg9FU0IKm0g r0 = new org.telegram.ui.Adapters.-$$Lambda$MentionsAdapter$_MTgLbVZU0vjX841sg9FU0IKm0g
            r0.<init>(r9, r11)
            r7.cancelDelayRunnable = r0
            r1 = 1000(0x3e8, double:4.94E-321)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r0, r1)
            goto L_0x03b4
        L_0x03b1:
            r7.showUsersResult(r9, r11, r8)
        L_0x03b4:
            org.telegram.ui.Adapters.MentionsAdapter$5 r8 = new org.telegram.ui.Adapters.MentionsAdapter$5
            r0 = r8
            r1 = r16
            r2 = r4
            r3 = r5
            r4 = r9
            r5 = r11
            r6 = r10
            r0.<init>(r2, r3, r4, r5, r6)
            r7.searchGlobalRunnable = r8
            r0 = 200(0xc8, double:9.9E-322)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r8, r0)
            goto L_0x04e5
        L_0x03ca:
            r7.showUsersResult(r9, r11, r8)
            goto L_0x04e5
        L_0x03cf:
            if (r0 != r8) goto L_0x0423
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            java.lang.String r1 = r9.toString()
            java.lang.String r1 = r1.toLowerCase()
            org.telegram.ui.Adapters.SearchAdapterHelper r2 = r7.searchAdapterHelper
            java.util.ArrayList r2 = r2.getHashtags()
            r6 = 0
        L_0x03e5:
            int r3 = r2.size()
            if (r6 >= r3) goto L_0x0405
            java.lang.Object r3 = r2.get(r6)
            org.telegram.ui.Adapters.SearchAdapterHelper$HashtagObject r3 = (org.telegram.ui.Adapters.SearchAdapterHelper.HashtagObject) r3
            if (r3 == 0) goto L_0x0402
            java.lang.String r4 = r3.hashtag
            if (r4 == 0) goto L_0x0402
            boolean r4 = r4.startsWith(r1)
            if (r4 == 0) goto L_0x0402
            java.lang.String r3 = r3.hashtag
            r0.add(r3)
        L_0x0402:
            int r6 = r6 + 1
            goto L_0x03e5
        L_0x0405:
            r7.searchResultHashtags = r0
            r1 = 0
            r7.searchResultUsernames = r1
            r7.searchResultUsernamesMap = r1
            r7.searchResultCommands = r1
            r7.searchResultCommandsHelp = r1
            r7.searchResultCommandsUsers = r1
            r7.searchResultSuggestions = r1
            r16.notifyDataSetChanged()
            org.telegram.ui.Adapters.MentionsAdapter$MentionsAdapterDelegate r1 = r7.delegate
            boolean r0 = r0.isEmpty()
            r0 = r0 ^ r8
            r1.needChangePanelVisibility(r0)
            goto L_0x04e5
        L_0x0423:
            r1 = 2
            if (r0 != r1) goto L_0x04b6
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
            java.util.ArrayList r2 = new java.util.ArrayList
            r2.<init>()
            java.lang.String r3 = r9.toString()
            java.lang.String r3 = r3.toLowerCase()
            r4 = 0
        L_0x043e:
            android.util.SparseArray<org.telegram.tgnet.TLRPC$BotInfo> r5 = r7.botInfo
            int r5 = r5.size()
            if (r4 >= r5) goto L_0x0499
            android.util.SparseArray<org.telegram.tgnet.TLRPC$BotInfo> r5 = r7.botInfo
            java.lang.Object r5 = r5.valueAt(r4)
            org.telegram.tgnet.TLRPC$BotInfo r5 = (org.telegram.tgnet.TLRPC$BotInfo) r5
            r6 = 0
        L_0x044f:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_botCommand> r9 = r5.commands
            int r9 = r9.size()
            if (r6 >= r9) goto L_0x0496
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_botCommand> r9 = r5.commands
            java.lang.Object r9 = r9.get(r6)
            org.telegram.tgnet.TLRPC$TL_botCommand r9 = (org.telegram.tgnet.TLRPC$TL_botCommand) r9
            if (r9 == 0) goto L_0x0493
            java.lang.String r11 = r9.command
            if (r11 == 0) goto L_0x0493
            boolean r11 = r11.startsWith(r3)
            if (r11 == 0) goto L_0x0493
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            java.lang.String r12 = "/"
            r11.append(r12)
            java.lang.String r12 = r9.command
            r11.append(r12)
            java.lang.String r11 = r11.toString()
            r0.add(r11)
            java.lang.String r9 = r9.description
            r1.add(r9)
            int r9 = r5.user_id
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            org.telegram.tgnet.TLRPC$User r9 = r10.getUser(r9)
            r2.add(r9)
        L_0x0493:
            int r6 = r6 + 1
            goto L_0x044f
        L_0x0496:
            int r4 = r4 + 1
            goto L_0x043e
        L_0x0499:
            r4 = 0
            r7.searchResultHashtags = r4
            r7.searchResultUsernames = r4
            r7.searchResultUsernamesMap = r4
            r7.searchResultSuggestions = r4
            r7.searchResultCommands = r0
            r7.searchResultCommandsHelp = r1
            r7.searchResultCommandsUsers = r2
            r16.notifyDataSetChanged()
            org.telegram.ui.Adapters.MentionsAdapter$MentionsAdapterDelegate r1 = r7.delegate
            boolean r0 = r0.isEmpty()
            r0 = r0 ^ r8
            r1.needChangePanelVisibility(r0)
            goto L_0x04e5
        L_0x04b6:
            r1 = 3
            if (r0 != r1) goto L_0x04e5
            java.lang.String[] r0 = org.telegram.messenger.AndroidUtilities.getCurrentKeyboardLanguage()
            java.lang.String[] r1 = r7.lastSearchKeyboardLanguage
            boolean r1 = java.util.Arrays.equals(r0, r1)
            if (r1 != 0) goto L_0x04ce
            int r1 = r7.currentAccount
            org.telegram.messenger.MediaDataController r1 = org.telegram.messenger.MediaDataController.getInstance(r1)
            r1.fetchNewEmojiKeywords(r0)
        L_0x04ce:
            r7.lastSearchKeyboardLanguage = r0
            int r0 = r7.currentAccount
            org.telegram.messenger.MediaDataController r0 = org.telegram.messenger.MediaDataController.getInstance(r0)
            java.lang.String[] r1 = r7.lastSearchKeyboardLanguage
            java.lang.String r2 = r9.toString()
            org.telegram.ui.Adapters.-$$Lambda$MentionsAdapter$791x1Cqm32fnVWdshTaskYBglKQ r3 = new org.telegram.ui.Adapters.-$$Lambda$MentionsAdapter$791x1Cqm32fnVWdshTaskYBglKQ
            r3.<init>()
            r4 = 0
            r0.getEmojiSuggestions(r1, r2, r4, r3)
        L_0x04e5:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Adapters.MentionsAdapter.searchUsernameOrHashtag(java.lang.String, int, java.util.ArrayList, boolean):void");
    }

    static /* synthetic */ int lambda$searchUsernameOrHashtag$5(SparseArray sparseArray, ArrayList arrayList, TLRPC$User tLRPC$User, TLRPC$User tLRPC$User2) {
        if (sparseArray.indexOfKey(tLRPC$User.id) >= 0 && sparseArray.indexOfKey(tLRPC$User2.id) >= 0) {
            return 0;
        }
        if (sparseArray.indexOfKey(tLRPC$User.id) >= 0) {
            return -1;
        }
        if (sparseArray.indexOfKey(tLRPC$User2.id) >= 0) {
            return 1;
        }
        int indexOf = arrayList.indexOf(Integer.valueOf(tLRPC$User.id));
        int indexOf2 = arrayList.indexOf(Integer.valueOf(tLRPC$User2.id));
        if (indexOf == -1 || indexOf2 == -1) {
            if (indexOf != -1 && indexOf2 == -1) {
                return -1;
            }
            if (indexOf != -1 || indexOf2 == -1) {
                return 0;
            }
            return 1;
        } else if (indexOf < indexOf2) {
            return -1;
        } else {
            if (indexOf == indexOf2) {
                return 0;
            }
            return 1;
        }
    }

    public /* synthetic */ void lambda$searchUsernameOrHashtag$6$MentionsAdapter(ArrayList arrayList, SparseArray sparseArray) {
        this.cancelDelayRunnable = null;
        showUsersResult(arrayList, sparseArray, true);
    }

    public /* synthetic */ void lambda$searchUsernameOrHashtag$7$MentionsAdapter(ArrayList arrayList, String str) {
        this.searchResultSuggestions = arrayList;
        this.searchResultHashtags = null;
        this.searchResultUsernames = null;
        this.searchResultUsernamesMap = null;
        this.searchResultCommands = null;
        this.searchResultCommandsHelp = null;
        this.searchResultCommandsUsers = null;
        notifyDataSetChanged();
        MentionsAdapterDelegate mentionsAdapterDelegate = this.delegate;
        ArrayList<MediaDataController.KeywordResult> arrayList2 = this.searchResultSuggestions;
        mentionsAdapterDelegate.needChangePanelVisibility(arrayList2 != null && !arrayList2.isEmpty());
    }

    /* access modifiers changed from: private */
    public void showUsersResult(ArrayList<TLRPC$User> arrayList, SparseArray<TLRPC$User> sparseArray, boolean z) {
        this.searchResultUsernames = arrayList;
        this.searchResultUsernamesMap = sparseArray;
        Runnable runnable = this.cancelDelayRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.cancelDelayRunnable = null;
        }
        if (z) {
            notifyDataSetChanged();
            this.delegate.needChangePanelVisibility(!this.searchResultUsernames.isEmpty());
        }
    }

    public int getResultStartPosition() {
        return this.resultStartPosition;
    }

    public int getResultLength() {
        return this.resultLength;
    }

    public ArrayList<TLRPC$BotInlineResult> getSearchResultBotContext() {
        return this.searchResultBotContext;
    }

    public int getItemCount() {
        int i = 1;
        if (this.foundContextBot != null && !this.inlineMediaEnabled) {
            return 1;
        }
        ArrayList<TLRPC$BotInlineResult> arrayList = this.searchResultBotContext;
        if (arrayList != null) {
            int size = arrayList.size();
            if (this.searchResultBotContextSwitch == null) {
                i = 0;
            }
            return size + i;
        }
        ArrayList<TLRPC$User> arrayList2 = this.searchResultUsernames;
        if (arrayList2 != null) {
            return arrayList2.size();
        }
        ArrayList<String> arrayList3 = this.searchResultHashtags;
        if (arrayList3 != null) {
            return arrayList3.size();
        }
        ArrayList<String> arrayList4 = this.searchResultCommands;
        if (arrayList4 != null) {
            return arrayList4.size();
        }
        ArrayList<MediaDataController.KeywordResult> arrayList5 = this.searchResultSuggestions;
        if (arrayList5 != null) {
            return arrayList5.size();
        }
        return 0;
    }

    public int getItemViewType(int i) {
        if (this.foundContextBot != null && !this.inlineMediaEnabled) {
            return 3;
        }
        if (this.searchResultBotContext != null) {
            return (i != 0 || this.searchResultBotContextSwitch == null) ? 1 : 2;
        }
        return 0;
    }

    public void addHashtagsFromMessage(CharSequence charSequence) {
        this.searchAdapterHelper.addHashtagsFromMessage(charSequence);
    }

    public int getItemPosition(int i) {
        return (this.searchResultBotContext == null || this.searchResultBotContextSwitch == null) ? i : i - 1;
    }

    public Object getItem(int i) {
        if (this.searchResultBotContext != null) {
            TLRPC$TL_inlineBotSwitchPM tLRPC$TL_inlineBotSwitchPM = this.searchResultBotContextSwitch;
            if (tLRPC$TL_inlineBotSwitchPM != null) {
                if (i == 0) {
                    return tLRPC$TL_inlineBotSwitchPM;
                }
                i--;
            }
            if (i < 0 || i >= this.searchResultBotContext.size()) {
                return null;
            }
            return this.searchResultBotContext.get(i);
        }
        ArrayList<TLRPC$User> arrayList = this.searchResultUsernames;
        if (arrayList == null) {
            ArrayList<String> arrayList2 = this.searchResultHashtags;
            if (arrayList2 == null) {
                ArrayList<MediaDataController.KeywordResult> arrayList3 = this.searchResultSuggestions;
                if (arrayList3 == null) {
                    ArrayList<String> arrayList4 = this.searchResultCommands;
                    if (arrayList4 == null || i < 0 || i >= arrayList4.size()) {
                        return null;
                    }
                    if (this.searchResultCommandsUsers == null || (this.botsCount == 1 && !(this.info instanceof TLRPC$TL_channelFull))) {
                        return this.searchResultCommands.get(i);
                    }
                    if (this.searchResultCommandsUsers.get(i) != null) {
                        Object[] objArr = new Object[2];
                        objArr[0] = this.searchResultCommands.get(i);
                        objArr[1] = this.searchResultCommandsUsers.get(i) != null ? this.searchResultCommandsUsers.get(i).username : "";
                        return String.format("%s@%s", objArr);
                    }
                    return String.format("%s", new Object[]{this.searchResultCommands.get(i)});
                } else if (i < 0 || i >= arrayList3.size()) {
                    return null;
                } else {
                    return this.searchResultSuggestions.get(i);
                }
            } else if (i < 0 || i >= arrayList2.size()) {
                return null;
            } else {
                return this.searchResultHashtags.get(i);
            }
        } else if (i < 0 || i >= arrayList.size()) {
            return null;
        } else {
            return this.searchResultUsernames.get(i);
        }
    }

    public boolean isLongClickEnabled() {
        return (this.searchResultHashtags == null && this.searchResultCommands == null) ? false : true;
    }

    public boolean isBotCommands() {
        return this.searchResultCommands != null;
    }

    public boolean isBotContext() {
        return this.searchResultBotContext != null;
    }

    public boolean isBannedInline() {
        return this.foundContextBot != null && !this.inlineMediaEnabled;
    }

    public boolean isMediaLayout() {
        return this.contextMedia;
    }

    public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
        return this.foundContextBot == null || this.inlineMediaEnabled;
    }

    public /* synthetic */ void lambda$onCreateViewHolder$8$MentionsAdapter(ContextLinkCell contextLinkCell) {
        this.delegate.onContextClick(contextLinkCell.getResult());
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v1, resolved type: org.telegram.ui.Cells.MentionCell} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v3, resolved type: org.telegram.ui.Cells.ContextLinkCell} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v6, resolved type: org.telegram.ui.Cells.MentionCell} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v7, resolved type: org.telegram.ui.Cells.MentionCell} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v8, resolved type: org.telegram.ui.Cells.BotSwitchCell} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r6v9, resolved type: android.widget.TextView} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public androidx.recyclerview.widget.RecyclerView.ViewHolder onCreateViewHolder(android.view.ViewGroup r5, int r6) {
        /*
            r4 = this;
            if (r6 == 0) goto L_0x004b
            r5 = 1
            if (r6 == r5) goto L_0x003b
            r0 = 2
            if (r6 == r0) goto L_0x0033
            android.widget.TextView r6 = new android.widget.TextView
            android.content.Context r0 = r4.mContext
            r6.<init>(r0)
            r0 = 1090519040(0x41000000, float:8.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r6.setPadding(r1, r2, r3, r0)
            r0 = 1096810496(0x41600000, float:14.0)
            r6.setTextSize(r5, r0)
            java.lang.String r5 = "windowBackgroundWhiteGrayText2"
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            r6.setTextColor(r5)
            goto L_0x0057
        L_0x0033:
            org.telegram.ui.Cells.BotSwitchCell r6 = new org.telegram.ui.Cells.BotSwitchCell
            android.content.Context r5 = r4.mContext
            r6.<init>(r5)
            goto L_0x0057
        L_0x003b:
            org.telegram.ui.Cells.ContextLinkCell r6 = new org.telegram.ui.Cells.ContextLinkCell
            android.content.Context r5 = r4.mContext
            r6.<init>(r5)
            org.telegram.ui.Adapters.-$$Lambda$MentionsAdapter$VoCNDYNNW-mhCQ-lScxh9-TCNkI r5 = new org.telegram.ui.Adapters.-$$Lambda$MentionsAdapter$VoCNDYNNW-mhCQ-lScxh9-TCNkI
            r5.<init>()
            r6.setDelegate(r5)
            goto L_0x0057
        L_0x004b:
            org.telegram.ui.Cells.MentionCell r6 = new org.telegram.ui.Cells.MentionCell
            android.content.Context r5 = r4.mContext
            r6.<init>(r5)
            boolean r5 = r4.isDarkTheme
            r6.setIsDarkTheme(r5)
        L_0x0057:
            org.telegram.ui.Components.RecyclerListView$Holder r5 = new org.telegram.ui.Components.RecyclerListView$Holder
            r5.<init>(r6)
            return r5
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Adapters.MentionsAdapter.onCreateViewHolder(android.view.ViewGroup, int):androidx.recyclerview.widget.RecyclerView$ViewHolder");
    }

    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights;
        boolean z = false;
        if (viewHolder.getItemViewType() == 3) {
            TextView textView = (TextView) viewHolder.itemView;
            TLRPC$Chat currentChat = this.parentFragment.getCurrentChat();
            if (currentChat == null) {
                return;
            }
            if (!ChatObject.hasAdminRights(currentChat) && (tLRPC$TL_chatBannedRights = currentChat.default_banned_rights) != null && tLRPC$TL_chatBannedRights.send_inline) {
                textView.setText(LocaleController.getString("GlobalAttachInlineRestricted", NUM));
            } else if (AndroidUtilities.isBannedForever(currentChat.banned_rights)) {
                textView.setText(LocaleController.getString("AttachInlineRestrictedForever", NUM));
            } else {
                textView.setText(LocaleController.formatString("AttachInlineRestricted", NUM, LocaleController.formatDateForBan((long) currentChat.banned_rights.until_date)));
            }
        } else if (this.searchResultBotContext != null) {
            boolean z2 = this.searchResultBotContextSwitch != null;
            if (viewHolder.getItemViewType() != 2) {
                if (z2) {
                    i--;
                }
                ContextLinkCell contextLinkCell = (ContextLinkCell) viewHolder.itemView;
                TLRPC$BotInlineResult tLRPC$BotInlineResult = this.searchResultBotContext.get(i);
                boolean z3 = this.contextMedia;
                boolean z4 = i != this.searchResultBotContext.size() - 1;
                if (z2 && i == 0) {
                    z = true;
                }
                contextLinkCell.setLink(tLRPC$BotInlineResult, z3, z4, z);
            } else if (z2) {
                ((BotSwitchCell) viewHolder.itemView).setText(this.searchResultBotContextSwitch.text);
            }
        } else {
            ArrayList<TLRPC$User> arrayList = this.searchResultUsernames;
            if (arrayList != null) {
                ((MentionCell) viewHolder.itemView).setUser(arrayList.get(i));
                return;
            }
            ArrayList<String> arrayList2 = this.searchResultHashtags;
            if (arrayList2 != null) {
                ((MentionCell) viewHolder.itemView).setText(arrayList2.get(i));
                return;
            }
            ArrayList<MediaDataController.KeywordResult> arrayList3 = this.searchResultSuggestions;
            if (arrayList3 != null) {
                ((MentionCell) viewHolder.itemView).setEmojiSuggestion(arrayList3.get(i));
                return;
            }
            ArrayList<String> arrayList4 = this.searchResultCommands;
            if (arrayList4 != null) {
                MentionCell mentionCell = (MentionCell) viewHolder.itemView;
                String str = arrayList4.get(i);
                String str2 = this.searchResultCommandsHelp.get(i);
                ArrayList<TLRPC$User> arrayList5 = this.searchResultCommandsUsers;
                mentionCell.setBotCommand(str, str2, arrayList5 != null ? arrayList5.get(i) : null);
            }
        }
    }

    public void onRequestPermissionsResultFragment(int i, String[] strArr, int[] iArr) {
        TLRPC$User tLRPC$User;
        if (i == 2 && (tLRPC$User = this.foundContextBot) != null && tLRPC$User.bot_inline_geo) {
            if (iArr.length <= 0 || iArr[0] != 0) {
                onLocationUnavailable();
            } else {
                this.locationProvider.start();
            }
        }
    }
}
