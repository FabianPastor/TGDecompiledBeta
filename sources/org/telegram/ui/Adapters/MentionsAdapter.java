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
    public boolean lastForSearch;
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
    public ArrayList<TLObject> searchResultUsernames;
    /* access modifiers changed from: private */
    public SparseArray<TLObject> searchResultUsernamesMap;
    /* access modifiers changed from: private */
    public String searchingContextQuery;
    /* access modifiers changed from: private */
    public String searchingContextUsername;

    public interface MentionsAdapterDelegate {
        void needChangePanelVisibility(boolean z);

        void onContextClick(TLRPC$BotInlineResult tLRPC$BotInlineResult);

        void onContextSearch(boolean z);
    }

    static /* synthetic */ int access$1704(MentionsAdapter mentionsAdapter) {
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

            public /* synthetic */ SparseArray getExcludeCallParticipants() {
                return SearchAdapterHelper.SearchAdapterHelperDelegate.CC.$default$getExcludeCallParticipants(this);
            }

            public /* synthetic */ SparseArray getExcludeUsers() {
                return SearchAdapterHelper.SearchAdapterHelperDelegate.CC.$default$getExcludeUsers(this);
            }

            public void onDataSetChanged(int i) {
                MentionsAdapter.this.notifyDataSetChanged();
            }

            public void onSetHashtags(ArrayList<SearchAdapterHelper.HashtagObject> arrayList, HashMap<String, SearchAdapterHelper.HashtagObject> hashMap) {
                if (MentionsAdapter.this.lastText != null) {
                    MentionsAdapter mentionsAdapter = MentionsAdapter.this;
                    mentionsAdapter.searchUsernameOrHashtag(mentionsAdapter.lastText, MentionsAdapter.this.lastPosition, MentionsAdapter.this.messages, MentionsAdapter.this.lastUsernameOnly, MentionsAdapter.this.lastForSearch);
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
            searchUsernameOrHashtag(str, this.lastPosition, this.messages, this.lastUsernameOnly, this.lastForSearch);
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
                        public final /* synthetic */ boolean[] f$1;
                        public final /* synthetic */ TLRPC$User f$2;

                        {
                            this.f$1 = r2;
                            this.f$2 = r3;
                        }

                        public final void onClick(DialogInterface dialogInterface, int i) {
                            MentionsAdapter.this.lambda$processFoundUser$0$MentionsAdapter(this.f$1, this.f$2, dialogInterface, i);
                        }
                    });
                    builder.setNegativeButton(LocaleController.getString("Cancel", NUM), new DialogInterface.OnClickListener(zArr) {
                        public final /* synthetic */ boolean[] f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void onClick(DialogInterface dialogInterface, int i) {
                            MentionsAdapter.this.lambda$processFoundUser$1$MentionsAdapter(this.f$1, dialogInterface, i);
                        }
                    });
                    this.parentFragment.showDialog(builder.create(), new DialogInterface.OnDismissListener(zArr) {
                        public final /* synthetic */ boolean[] f$1;

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

    /* access modifiers changed from: private */
    /* renamed from: lambda$processFoundUser$0 */
    public /* synthetic */ void lambda$processFoundUser$0$MentionsAdapter(boolean[] zArr, TLRPC$User tLRPC$User, DialogInterface dialogInterface, int i) {
        zArr[0] = true;
        if (tLRPC$User != null) {
            SharedPreferences.Editor edit = MessagesController.getNotificationsSettings(this.currentAccount).edit();
            edit.putBoolean("inlinegeo_" + tLRPC$User.id, true).commit();
            checkLocationPermissionsOrStart();
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$processFoundUser$1 */
    public /* synthetic */ void lambda$processFoundUser$1$MentionsAdapter(boolean[] zArr, DialogInterface dialogInterface, int i) {
        zArr[0] = true;
        onLocationUnavailable();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$processFoundUser$2 */
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
                                public final /* synthetic */ String f$1;
                                public final /* synthetic */ MessagesController f$2;
                                public final /* synthetic */ MessagesStorage f$3;

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

                /* access modifiers changed from: private */
                /* renamed from: lambda$run$1 */
                public /* synthetic */ void lambda$run$1$MentionsAdapter$4(String str, MessagesController messagesController, MessagesStorage messagesStorage, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    AndroidUtilities.runOnUIThread(new Runnable(str, tLRPC$TL_error, tLObject, messagesController, messagesStorage) {
                        public final /* synthetic */ String f$1;
                        public final /* synthetic */ TLRPC$TL_error f$2;
                        public final /* synthetic */ TLObject f$3;
                        public final /* synthetic */ MessagesController f$4;
                        public final /* synthetic */ MessagesStorage f$5;

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

                /* access modifiers changed from: private */
                /* renamed from: lambda$null$0 */
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
            org.telegram.ui.Adapters.-$$Lambda$MentionsAdapter$IL5-OQAQtFsbdezrCB664Fy_L14 r6 = new org.telegram.ui.Adapters.-$$Lambda$MentionsAdapter$IL5-OQAQtFsbdezrCB664Fy_L14
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
            org.telegram.tgnet.TLRPC$InputPeer r1 = r1.getInputPeer((int) r2)
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$searchForContextBotResults$4 */
    public /* synthetic */ void lambda$searchForContextBotResults$4$MentionsAdapter(String str, boolean z, TLRPC$User tLRPC$User, String str2, MessagesStorage messagesStorage, String str3, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable(str, z, tLObject, tLRPC$User, str2, messagesStorage, str3) {
            public final /* synthetic */ String f$1;
            public final /* synthetic */ boolean f$2;
            public final /* synthetic */ TLObject f$3;
            public final /* synthetic */ TLRPC$User f$4;
            public final /* synthetic */ String f$5;
            public final /* synthetic */ MessagesStorage f$6;
            public final /* synthetic */ String f$7;

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

    /* access modifiers changed from: private */
    /* renamed from: lambda$null$3 */
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

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v14, resolved type: org.telegram.tgnet.TLRPC$Chat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v22, resolved type: org.telegram.tgnet.TLRPC$User} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v25, resolved type: org.telegram.tgnet.TLRPC$Chat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v36, resolved type: org.telegram.tgnet.TLRPC$Chat} */
    /* JADX WARNING: Code restructure failed: missing block: B:86:0x014d, code lost:
        if (r8.info != null) goto L_0x015d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:87:0x014f, code lost:
        if (r5 == 0) goto L_0x015d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:88:0x0151, code lost:
        r8.lastText = r0;
        r8.lastPosition = r1;
        r8.messages = r2;
        r8.delegate.needChangePanelVisibility(false);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:89:0x015c, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:90:0x015d, code lost:
        r8.resultStartPosition = r5;
        r8.resultLength = r10.length() + 1;
        r0 = 0;
        r1 = 65535;
     */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void searchUsernameOrHashtag(java.lang.String r20, int r21, java.util.ArrayList<org.telegram.messenger.MessageObject> r22, boolean r23, boolean r24) {
        /*
            r19 = this;
            r8 = r19
            r0 = r20
            r1 = r21
            r2 = r22
            r3 = r23
            r4 = r24
            java.lang.Runnable r5 = r8.cancelDelayRunnable
            r6 = 0
            if (r5 == 0) goto L_0x0016
            org.telegram.messenger.AndroidUtilities.cancelRunOnUIThread(r5)
            r8.cancelDelayRunnable = r6
        L_0x0016:
            int r5 = r8.channelReqId
            r7 = 0
            r9 = 1
            if (r5 == 0) goto L_0x0029
            int r5 = r8.currentAccount
            org.telegram.tgnet.ConnectionsManager r5 = org.telegram.tgnet.ConnectionsManager.getInstance(r5)
            int r10 = r8.channelReqId
            r5.cancelRequest(r10, r9)
            r8.channelReqId = r7
        L_0x0029:
            java.lang.Runnable r5 = r8.searchGlobalRunnable
            if (r5 == 0) goto L_0x0032
            org.telegram.messenger.AndroidUtilities.cancelRunOnUIThread(r5)
            r8.searchGlobalRunnable = r6
        L_0x0032:
            boolean r5 = android.text.TextUtils.isEmpty(r20)
            if (r5 != 0) goto L_0x0506
            int r5 = r20.length()
            int r10 = r8.currentAccount
            org.telegram.messenger.MessagesController r10 = org.telegram.messenger.MessagesController.getInstance(r10)
            int r10 = r10.maxMessageLength
            if (r5 <= r10) goto L_0x004a
            r0 = r6
            r4 = 0
            goto L_0x0508
        L_0x004a:
            int r5 = r20.length()
            if (r5 <= 0) goto L_0x0053
            int r5 = r1 + -1
            goto L_0x0054
        L_0x0053:
            r5 = r1
        L_0x0054:
            r8.lastText = r6
            r8.lastUsernameOnly = r3
            r8.lastForSearch = r4
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            r11 = 64
            r12 = 3
            r13 = 32
            if (r3 != 0) goto L_0x00ec
            boolean r14 = r8.needBotContext
            if (r14 == 0) goto L_0x00ec
            char r14 = r0.charAt(r7)
            if (r14 != r11) goto L_0x00ec
            int r14 = r0.indexOf(r13)
            int r15 = r20.length()
            java.lang.String r16 = ""
            if (r14 <= 0) goto L_0x0086
            java.lang.String r15 = r0.substring(r9, r14)
            int r14 = r14 + r9
            java.lang.String r14 = r0.substring(r14)
            goto L_0x00af
        L_0x0086:
            int r14 = r15 + -1
            char r14 = r0.charAt(r14)
            r11 = 116(0x74, float:1.63E-43)
            if (r14 != r11) goto L_0x00aa
            int r11 = r15 + -2
            char r11 = r0.charAt(r11)
            r14 = 111(0x6f, float:1.56E-43)
            if (r11 != r14) goto L_0x00aa
            int r15 = r15 - r12
            char r11 = r0.charAt(r15)
            r14 = 98
            if (r11 != r14) goto L_0x00aa
            java.lang.String r15 = r0.substring(r9)
            r14 = r16
            goto L_0x00af
        L_0x00aa:
            r8.searchForContextBot(r6, r6)
            r14 = r6
            r15 = r14
        L_0x00af:
            if (r15 == 0) goto L_0x00e6
            int r11 = r15.length()
            if (r11 < r9) goto L_0x00e6
            r11 = 1
        L_0x00b8:
            int r12 = r15.length()
            if (r11 >= r12) goto L_0x00e4
            char r12 = r15.charAt(r11)
            r13 = 48
            if (r12 < r13) goto L_0x00ca
            r13 = 57
            if (r12 <= r13) goto L_0x00df
        L_0x00ca:
            r13 = 97
            if (r12 < r13) goto L_0x00d2
            r13 = 122(0x7a, float:1.71E-43)
            if (r12 <= r13) goto L_0x00df
        L_0x00d2:
            r13 = 65
            if (r12 < r13) goto L_0x00da
            r13 = 90
            if (r12 <= r13) goto L_0x00df
        L_0x00da:
            r13 = 95
            if (r12 == r13) goto L_0x00df
            goto L_0x00e6
        L_0x00df:
            int r11 = r11 + 1
            r13 = 32
            goto L_0x00b8
        L_0x00e4:
            r16 = r15
        L_0x00e6:
            r11 = r16
            r8.searchForContextBot(r11, r14)
            goto L_0x00ef
        L_0x00ec:
            r8.searchForContextBot(r6, r6)
        L_0x00ef:
            org.telegram.tgnet.TLRPC$User r11 = r8.foundContextBot
            if (r11 == 0) goto L_0x00f4
            return
        L_0x00f4:
            int r11 = r8.currentAccount
            org.telegram.messenger.MessagesController r11 = org.telegram.messenger.MessagesController.getInstance(r11)
            if (r3 == 0) goto L_0x0110
            java.lang.String r0 = r0.substring(r9)
            r10.append(r0)
            r8.resultStartPosition = r7
            int r0 = r10.length()
            r8.resultLength = r0
            r0 = 0
        L_0x010c:
            r1 = -1
            r5 = -1
            goto L_0x01dc
        L_0x0110:
            if (r5 < 0) goto L_0x01d9
            int r14 = r20.length()
            if (r5 < r14) goto L_0x011c
            r6 = 64
            goto L_0x01d4
        L_0x011c:
            char r14 = r0.charAt(r5)
            r15 = 58
            if (r5 == 0) goto L_0x013d
            int r12 = r5 + -1
            char r6 = r0.charAt(r12)
            r13 = 32
            if (r6 == r13) goto L_0x013d
            char r6 = r0.charAt(r12)
            r12 = 10
            if (r6 == r12) goto L_0x013d
            if (r14 != r15) goto L_0x0139
            goto L_0x013d
        L_0x0139:
            r6 = 64
            goto L_0x01d1
        L_0x013d:
            r6 = 64
            if (r14 != r6) goto L_0x016a
            boolean r12 = r8.needUsernames
            if (r12 != 0) goto L_0x014b
            boolean r12 = r8.needBotContext
            if (r12 == 0) goto L_0x01d1
            if (r5 != 0) goto L_0x01d1
        L_0x014b:
            org.telegram.tgnet.TLRPC$ChatFull r6 = r8.info
            if (r6 != 0) goto L_0x015d
            if (r5 == 0) goto L_0x015d
            r8.lastText = r0
            r8.lastPosition = r1
            r8.messages = r2
            org.telegram.ui.Adapters.MentionsAdapter$MentionsAdapterDelegate r0 = r8.delegate
            r0.needChangePanelVisibility(r7)
            return
        L_0x015d:
            r8.resultStartPosition = r5
            int r0 = r10.length()
            int r0 = r0 + r9
            r8.resultLength = r0
            r0 = 0
            r1 = -1
            goto L_0x01dc
        L_0x016a:
            r12 = 35
            if (r14 != r12) goto L_0x0190
            org.telegram.ui.Adapters.SearchAdapterHelper r6 = r8.searchAdapterHelper
            boolean r6 = r6.loadRecentHashtags()
            if (r6 == 0) goto L_0x0184
            r8.resultStartPosition = r5
            int r0 = r10.length()
            int r0 = r0 + r9
            r8.resultLength = r0
            r10.insert(r7, r14)
            r0 = 1
            goto L_0x010c
        L_0x0184:
            r8.lastText = r0
            r8.lastPosition = r1
            r8.messages = r2
            org.telegram.ui.Adapters.MentionsAdapter$MentionsAdapterDelegate r0 = r8.delegate
            r0.needChangePanelVisibility(r7)
            return
        L_0x0190:
            if (r5 != 0) goto L_0x01a6
            android.util.SparseArray<org.telegram.tgnet.TLRPC$BotInfo> r12 = r8.botInfo
            if (r12 == 0) goto L_0x01a6
            r12 = 47
            if (r14 != r12) goto L_0x01a6
            r8.resultStartPosition = r5
            int r0 = r10.length()
            int r0 = r0 + r9
            r8.resultLength = r0
            r0 = 2
            goto L_0x010c
        L_0x01a6:
            if (r14 != r15) goto L_0x01d1
            int r12 = r10.length()
            if (r12 <= 0) goto L_0x01d1
            char r12 = r10.charAt(r7)
            java.lang.String r13 = " !\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~\n"
            int r12 = r13.indexOf(r12)
            if (r12 < 0) goto L_0x01bc
            r12 = 1
            goto L_0x01bd
        L_0x01bc:
            r12 = 0
        L_0x01bd:
            if (r12 == 0) goto L_0x01c5
            int r12 = r10.length()
            if (r12 <= r9) goto L_0x01d1
        L_0x01c5:
            r8.resultStartPosition = r5
            int r0 = r10.length()
            int r0 = r0 + r9
            r8.resultLength = r0
            r0 = 3
            goto L_0x010c
        L_0x01d1:
            r10.insert(r7, r14)
        L_0x01d4:
            int r5 = r5 + -1
            r6 = 0
            goto L_0x0110
        L_0x01d9:
            r0 = -1
            goto L_0x010c
        L_0x01dc:
            if (r0 != r1) goto L_0x01e4
            org.telegram.ui.Adapters.MentionsAdapter$MentionsAdapterDelegate r0 = r8.delegate
            r0.needChangePanelVisibility(r7)
            return
        L_0x01e4:
            if (r0 != 0) goto L_0x03ec
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r1 = 0
        L_0x01ec:
            r6 = 100
            int r12 = r22.size()
            int r6 = java.lang.Math.min(r6, r12)
            if (r1 >= r6) goto L_0x0218
            java.lang.Object r6 = r2.get(r1)
            org.telegram.messenger.MessageObject r6 = (org.telegram.messenger.MessageObject) r6
            int r6 = r6.getFromChatId()
            if (r6 <= 0) goto L_0x0215
            java.lang.Integer r12 = java.lang.Integer.valueOf(r6)
            boolean r12 = r0.contains(r12)
            if (r12 != 0) goto L_0x0215
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            r0.add(r6)
        L_0x0215:
            int r1 = r1 + 1
            goto L_0x01ec
        L_0x0218:
            java.lang.String r1 = r10.toString()
            java.lang.String r6 = r1.toLowerCase()
            r1 = 32
            int r1 = r6.indexOf(r1)
            if (r1 < 0) goto L_0x022a
            r1 = 1
            goto L_0x022b
        L_0x022a:
            r1 = 0
        L_0x022b:
            java.util.ArrayList r10 = new java.util.ArrayList
            r10.<init>()
            android.util.SparseArray r2 = new android.util.SparseArray
            r2.<init>()
            android.util.SparseArray r12 = new android.util.SparseArray
            r12.<init>()
            int r13 = r8.currentAccount
            org.telegram.messenger.MediaDataController r13 = org.telegram.messenger.MediaDataController.getInstance(r13)
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_topPeer> r13 = r13.inlineBots
            r14 = 5
            if (r3 != 0) goto L_0x029e
            boolean r15 = r8.needBotContext
            if (r15 == 0) goto L_0x029e
            if (r5 != 0) goto L_0x029e
            boolean r5 = r13.isEmpty()
            if (r5 != 0) goto L_0x029e
            r5 = 0
            r15 = 0
        L_0x0253:
            int r7 = r13.size()
            if (r5 >= r7) goto L_0x029e
            java.lang.Object r7 = r13.get(r5)
            org.telegram.tgnet.TLRPC$TL_topPeer r7 = (org.telegram.tgnet.TLRPC$TL_topPeer) r7
            org.telegram.tgnet.TLRPC$Peer r7 = r7.peer
            int r7 = r7.user_id
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            org.telegram.tgnet.TLRPC$User r7 = r11.getUser(r7)
            if (r7 != 0) goto L_0x026e
            goto L_0x029a
        L_0x026e:
            java.lang.String r9 = r7.username
            boolean r9 = android.text.TextUtils.isEmpty(r9)
            if (r9 != 0) goto L_0x0297
            int r9 = r6.length()
            if (r9 == 0) goto L_0x0288
            java.lang.String r9 = r7.username
            java.lang.String r9 = r9.toLowerCase()
            boolean r9 = r9.startsWith(r6)
            if (r9 == 0) goto L_0x0297
        L_0x0288:
            r10.add(r7)
            int r9 = r7.id
            r2.put(r9, r7)
            int r9 = r7.id
            r12.put(r9, r7)
            int r15 = r15 + 1
        L_0x0297:
            if (r15 != r14) goto L_0x029a
            goto L_0x029e
        L_0x029a:
            int r5 = r5 + 1
            r9 = 1
            goto L_0x0253
        L_0x029e:
            org.telegram.ui.ChatActivity r5 = r8.parentFragment
            if (r5 == 0) goto L_0x02ad
            org.telegram.tgnet.TLRPC$Chat r5 = r5.getCurrentChat()
            org.telegram.ui.ChatActivity r7 = r8.parentFragment
            int r7 = r7.getThreadId()
            goto L_0x02be
        L_0x02ad:
            org.telegram.tgnet.TLRPC$ChatFull r5 = r8.info
            if (r5 == 0) goto L_0x02bc
            int r5 = r5.id
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            org.telegram.tgnet.TLRPC$Chat r5 = r11.getChat(r5)
            goto L_0x02bd
        L_0x02bc:
            r5 = 0
        L_0x02bd:
            r7 = 0
        L_0x02be:
            if (r5 == 0) goto L_0x0398
            org.telegram.tgnet.TLRPC$ChatFull r9 = r8.info
            if (r9 == 0) goto L_0x0398
            org.telegram.tgnet.TLRPC$ChatParticipants r9 = r9.participants
            if (r9 == 0) goto L_0x0398
            boolean r9 = org.telegram.messenger.ChatObject.isChannel(r5)
            if (r9 == 0) goto L_0x02d2
            boolean r9 = r5.megagroup
            if (r9 == 0) goto L_0x0398
        L_0x02d2:
            if (r4 == 0) goto L_0x02d7
            r17 = -1
            goto L_0x02d9
        L_0x02d7:
            r17 = 0
        L_0x02d9:
            r4 = r17
        L_0x02db:
            org.telegram.tgnet.TLRPC$ChatFull r9 = r8.info
            org.telegram.tgnet.TLRPC$ChatParticipants r9 = r9.participants
            java.util.ArrayList<org.telegram.tgnet.TLRPC$ChatParticipant> r9 = r9.participants
            int r9 = r9.size()
            if (r4 >= r9) goto L_0x0398
            r9 = -1
            if (r4 != r9) goto L_0x0302
            int r13 = r6.length()
            if (r13 != 0) goto L_0x02f5
            r10.add(r5)
            goto L_0x038f
        L_0x02f5:
            java.lang.String r13 = r5.title
            java.lang.String r15 = r5.username
            int r9 = r5.id
            int r9 = -r9
            r21 = r2
            r2 = r9
            r14 = 0
            r9 = r5
            goto L_0x0348
        L_0x0302:
            org.telegram.tgnet.TLRPC$ChatFull r9 = r8.info
            org.telegram.tgnet.TLRPC$ChatParticipants r9 = r9.participants
            java.util.ArrayList<org.telegram.tgnet.TLRPC$ChatParticipant> r9 = r9.participants
            java.lang.Object r9 = r9.get(r4)
            org.telegram.tgnet.TLRPC$ChatParticipant r9 = (org.telegram.tgnet.TLRPC$ChatParticipant) r9
            int r9 = r9.user_id
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            org.telegram.tgnet.TLRPC$User r9 = r11.getUser(r9)
            if (r9 == 0) goto L_0x038f
            if (r3 != 0) goto L_0x0322
            boolean r13 = org.telegram.messenger.UserObject.isUserSelf(r9)
            if (r13 != 0) goto L_0x038f
        L_0x0322:
            int r13 = r9.id
            int r13 = r2.indexOfKey(r13)
            if (r13 < 0) goto L_0x032b
            goto L_0x038f
        L_0x032b:
            int r13 = r6.length()
            if (r13 != 0) goto L_0x0339
            boolean r13 = r9.deleted
            if (r13 != 0) goto L_0x0339
            r10.add(r9)
            goto L_0x038f
        L_0x0339:
            java.lang.String r13 = r9.first_name
            java.lang.String r15 = r9.last_name
            java.lang.String r14 = r9.username
            r21 = r2
            int r2 = r9.id
            r18 = r15
            r15 = r14
            r14 = r18
        L_0x0348:
            boolean r16 = android.text.TextUtils.isEmpty(r15)
            if (r16 != 0) goto L_0x0358
            java.lang.String r15 = r15.toLowerCase()
            boolean r15 = r15.startsWith(r6)
            if (r15 != 0) goto L_0x0388
        L_0x0358:
            boolean r15 = android.text.TextUtils.isEmpty(r13)
            if (r15 != 0) goto L_0x0368
            java.lang.String r15 = r13.toLowerCase()
            boolean r15 = r15.startsWith(r6)
            if (r15 != 0) goto L_0x0388
        L_0x0368:
            boolean r15 = android.text.TextUtils.isEmpty(r14)
            if (r15 != 0) goto L_0x0378
            java.lang.String r15 = r14.toLowerCase()
            boolean r15 = r15.startsWith(r6)
            if (r15 != 0) goto L_0x0388
        L_0x0378:
            if (r1 == 0) goto L_0x0391
            java.lang.String r13 = org.telegram.messenger.ContactsController.formatName(r13, r14)
            java.lang.String r13 = r13.toLowerCase()
            boolean r13 = r13.startsWith(r6)
            if (r13 == 0) goto L_0x0391
        L_0x0388:
            r10.add(r9)
            r12.put(r2, r9)
            goto L_0x0391
        L_0x038f:
            r21 = r2
        L_0x0391:
            int r4 = r4 + 1
            r2 = r21
            r14 = 5
            goto L_0x02db
        L_0x0398:
            org.telegram.ui.Adapters.MentionsAdapter$5 r1 = new org.telegram.ui.Adapters.MentionsAdapter$5
            r1.<init>(r12, r0)
            java.util.Collections.sort(r10, r1)
            r0 = 0
            r8.searchResultHashtags = r0
            r8.searchResultCommands = r0
            r8.searchResultCommandsHelp = r0
            r8.searchResultCommandsUsers = r0
            r8.searchResultSuggestions = r0
            if (r5 == 0) goto L_0x03e6
            boolean r0 = r5.megagroup
            if (r0 == 0) goto L_0x03e6
            int r0 = r6.length()
            if (r0 <= 0) goto L_0x03e6
            int r0 = r10.size()
            r1 = 5
            if (r0 >= r1) goto L_0x03cb
            org.telegram.ui.Adapters.-$$Lambda$MentionsAdapter$pvr9tzj2yJuJolzxxTPD9oiIQ_8 r0 = new org.telegram.ui.Adapters.-$$Lambda$MentionsAdapter$pvr9tzj2yJuJolzxxTPD9oiIQ_8
            r0.<init>(r10, r12)
            r8.cancelDelayRunnable = r0
            r1 = 1000(0x3e8, double:4.94E-321)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r0, r1)
            goto L_0x03cf
        L_0x03cb:
            r0 = 1
            r8.showUsersResult(r10, r12, r0)
        L_0x03cf:
            org.telegram.ui.Adapters.MentionsAdapter$6 r9 = new org.telegram.ui.Adapters.MentionsAdapter$6
            r0 = r9
            r1 = r19
            r2 = r5
            r3 = r6
            r4 = r7
            r5 = r10
            r6 = r12
            r7 = r11
            r0.<init>(r2, r3, r4, r5, r6, r7)
            r8.searchGlobalRunnable = r9
            r0 = 200(0xc8, double:9.9E-322)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r9, r0)
            goto L_0x0505
        L_0x03e6:
            r1 = 1
            r8.showUsersResult(r10, r12, r1)
            goto L_0x0505
        L_0x03ec:
            r1 = 1
            if (r0 != r1) goto L_0x0442
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            java.lang.String r1 = r10.toString()
            java.lang.String r1 = r1.toLowerCase()
            org.telegram.ui.Adapters.SearchAdapterHelper r2 = r8.searchAdapterHelper
            java.util.ArrayList r2 = r2.getHashtags()
            r7 = 0
        L_0x0403:
            int r3 = r2.size()
            if (r7 >= r3) goto L_0x0423
            java.lang.Object r3 = r2.get(r7)
            org.telegram.ui.Adapters.SearchAdapterHelper$HashtagObject r3 = (org.telegram.ui.Adapters.SearchAdapterHelper.HashtagObject) r3
            if (r3 == 0) goto L_0x0420
            java.lang.String r4 = r3.hashtag
            if (r4 == 0) goto L_0x0420
            boolean r4 = r4.startsWith(r1)
            if (r4 == 0) goto L_0x0420
            java.lang.String r3 = r3.hashtag
            r0.add(r3)
        L_0x0420:
            int r7 = r7 + 1
            goto L_0x0403
        L_0x0423:
            r8.searchResultHashtags = r0
            r1 = 0
            r8.searchResultUsernames = r1
            r8.searchResultUsernamesMap = r1
            r8.searchResultCommands = r1
            r8.searchResultCommandsHelp = r1
            r8.searchResultCommandsUsers = r1
            r8.searchResultSuggestions = r1
            r19.notifyDataSetChanged()
            org.telegram.ui.Adapters.MentionsAdapter$MentionsAdapterDelegate r1 = r8.delegate
            boolean r0 = r0.isEmpty()
            r2 = 1
            r0 = r0 ^ r2
            r1.needChangePanelVisibility(r0)
            goto L_0x0505
        L_0x0442:
            r1 = 2
            if (r0 != r1) goto L_0x04d6
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
            java.util.ArrayList r2 = new java.util.ArrayList
            r2.<init>()
            java.lang.String r3 = r10.toString()
            java.lang.String r3 = r3.toLowerCase()
            r4 = 0
        L_0x045d:
            android.util.SparseArray<org.telegram.tgnet.TLRPC$BotInfo> r5 = r8.botInfo
            int r5 = r5.size()
            if (r4 >= r5) goto L_0x04b8
            android.util.SparseArray<org.telegram.tgnet.TLRPC$BotInfo> r5 = r8.botInfo
            java.lang.Object r5 = r5.valueAt(r4)
            org.telegram.tgnet.TLRPC$BotInfo r5 = (org.telegram.tgnet.TLRPC$BotInfo) r5
            r6 = 0
        L_0x046e:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_botCommand> r7 = r5.commands
            int r7 = r7.size()
            if (r6 >= r7) goto L_0x04b5
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_botCommand> r7 = r5.commands
            java.lang.Object r7 = r7.get(r6)
            org.telegram.tgnet.TLRPC$TL_botCommand r7 = (org.telegram.tgnet.TLRPC$TL_botCommand) r7
            if (r7 == 0) goto L_0x04b2
            java.lang.String r9 = r7.command
            if (r9 == 0) goto L_0x04b2
            boolean r9 = r9.startsWith(r3)
            if (r9 == 0) goto L_0x04b2
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            java.lang.String r10 = "/"
            r9.append(r10)
            java.lang.String r10 = r7.command
            r9.append(r10)
            java.lang.String r9 = r9.toString()
            r0.add(r9)
            java.lang.String r7 = r7.description
            r1.add(r7)
            int r7 = r5.user_id
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            org.telegram.tgnet.TLRPC$User r7 = r11.getUser(r7)
            r2.add(r7)
        L_0x04b2:
            int r6 = r6 + 1
            goto L_0x046e
        L_0x04b5:
            int r4 = r4 + 1
            goto L_0x045d
        L_0x04b8:
            r4 = 0
            r8.searchResultHashtags = r4
            r8.searchResultUsernames = r4
            r8.searchResultUsernamesMap = r4
            r8.searchResultSuggestions = r4
            r8.searchResultCommands = r0
            r8.searchResultCommandsHelp = r1
            r8.searchResultCommandsUsers = r2
            r19.notifyDataSetChanged()
            org.telegram.ui.Adapters.MentionsAdapter$MentionsAdapterDelegate r1 = r8.delegate
            boolean r0 = r0.isEmpty()
            r2 = 1
            r0 = r0 ^ r2
            r1.needChangePanelVisibility(r0)
            goto L_0x0505
        L_0x04d6:
            r1 = 3
            if (r0 != r1) goto L_0x0505
            java.lang.String[] r0 = org.telegram.messenger.AndroidUtilities.getCurrentKeyboardLanguage()
            java.lang.String[] r1 = r8.lastSearchKeyboardLanguage
            boolean r1 = java.util.Arrays.equals(r0, r1)
            if (r1 != 0) goto L_0x04ee
            int r1 = r8.currentAccount
            org.telegram.messenger.MediaDataController r1 = org.telegram.messenger.MediaDataController.getInstance(r1)
            r1.fetchNewEmojiKeywords(r0)
        L_0x04ee:
            r8.lastSearchKeyboardLanguage = r0
            int r0 = r8.currentAccount
            org.telegram.messenger.MediaDataController r0 = org.telegram.messenger.MediaDataController.getInstance(r0)
            java.lang.String[] r1 = r8.lastSearchKeyboardLanguage
            java.lang.String r2 = r10.toString()
            org.telegram.ui.Adapters.-$$Lambda$MentionsAdapter$BPDwlLrrTYvBx2slW-DPtWy21rg r3 = new org.telegram.ui.Adapters.-$$Lambda$MentionsAdapter$BPDwlLrrTYvBx2slW-DPtWy21rg
            r3.<init>()
            r4 = 0
            r0.getEmojiSuggestions(r1, r2, r4, r3)
        L_0x0505:
            return
        L_0x0506:
            r4 = 0
            r0 = r6
        L_0x0508:
            r8.searchForContextBot(r0, r0)
            org.telegram.ui.Adapters.MentionsAdapter$MentionsAdapterDelegate r1 = r8.delegate
            r1.needChangePanelVisibility(r4)
            r8.lastText = r0
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Adapters.MentionsAdapter.searchUsernameOrHashtag(java.lang.String, int, java.util.ArrayList, boolean, boolean):void");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$searchUsernameOrHashtag$5 */
    public /* synthetic */ void lambda$searchUsernameOrHashtag$5$MentionsAdapter(ArrayList arrayList, SparseArray sparseArray) {
        this.cancelDelayRunnable = null;
        showUsersResult(arrayList, sparseArray, true);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$searchUsernameOrHashtag$6 */
    public /* synthetic */ void lambda$searchUsernameOrHashtag$6$MentionsAdapter(ArrayList arrayList, String str) {
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
    public void showUsersResult(ArrayList<TLObject> arrayList, SparseArray<TLObject> sparseArray, boolean z) {
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
        ArrayList<TLObject> arrayList2 = this.searchResultUsernames;
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
        ArrayList<TLRPC$BotInlineResult> arrayList = this.searchResultBotContext;
        if (arrayList != null) {
            TLRPC$TL_inlineBotSwitchPM tLRPC$TL_inlineBotSwitchPM = this.searchResultBotContextSwitch;
            if (tLRPC$TL_inlineBotSwitchPM != null) {
                if (i == 0) {
                    return tLRPC$TL_inlineBotSwitchPM;
                }
                i--;
            }
            if (i < 0 || i >= arrayList.size()) {
                return null;
            }
            return this.searchResultBotContext.get(i);
        }
        ArrayList<TLObject> arrayList2 = this.searchResultUsernames;
        if (arrayList2 == null) {
            ArrayList<String> arrayList3 = this.searchResultHashtags;
            if (arrayList3 == null) {
                ArrayList<MediaDataController.KeywordResult> arrayList4 = this.searchResultSuggestions;
                if (arrayList4 == null) {
                    ArrayList<String> arrayList5 = this.searchResultCommands;
                    if (arrayList5 == null || i < 0 || i >= arrayList5.size()) {
                        return null;
                    }
                    ArrayList<TLRPC$User> arrayList6 = this.searchResultCommandsUsers;
                    if (arrayList6 == null || (this.botsCount == 1 && !(this.info instanceof TLRPC$TL_channelFull))) {
                        return this.searchResultCommands.get(i);
                    }
                    if (arrayList6.get(i) != null) {
                        Object[] objArr = new Object[2];
                        objArr[0] = this.searchResultCommands.get(i);
                        objArr[1] = this.searchResultCommandsUsers.get(i) != null ? this.searchResultCommandsUsers.get(i).username : "";
                        return String.format("%s@%s", objArr);
                    }
                    return String.format("%s", new Object[]{this.searchResultCommands.get(i)});
                } else if (i < 0 || i >= arrayList4.size()) {
                    return null;
                } else {
                    return this.searchResultSuggestions.get(i);
                }
            } else if (i < 0 || i >= arrayList3.size()) {
                return null;
            } else {
                return this.searchResultHashtags.get(i);
            }
        } else if (i < 0 || i >= arrayList2.size()) {
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

    /* access modifiers changed from: private */
    /* renamed from: lambda$onCreateViewHolder$7 */
    public /* synthetic */ void lambda$onCreateViewHolder$7$MentionsAdapter(ContextLinkCell contextLinkCell) {
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
            org.telegram.ui.Adapters.-$$Lambda$MentionsAdapter$Rs_gCeAnt0gqLkmAQV_mZgJj8Js r5 = new org.telegram.ui.Adapters.-$$Lambda$MentionsAdapter$Rs_gCeAnt0gqLkmAQV_mZgJj8Js
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
            boolean z = this.searchResultBotContextSwitch != null;
            if (viewHolder.getItemViewType() != 2) {
                if (z) {
                    i--;
                }
                ((ContextLinkCell) viewHolder.itemView).setLink(this.searchResultBotContext.get(i), this.foundContextBot, this.contextMedia, i != this.searchResultBotContext.size() - 1, z && i == 0, "gif".equals(this.searchingContextUsername));
            } else if (z) {
                ((BotSwitchCell) viewHolder.itemView).setText(this.searchResultBotContextSwitch.text);
            }
        } else {
            ArrayList<TLObject> arrayList = this.searchResultUsernames;
            if (arrayList != null) {
                TLObject tLObject = arrayList.get(i);
                if (tLObject instanceof TLRPC$User) {
                    ((MentionCell) viewHolder.itemView).setUser((TLRPC$User) tLObject);
                } else if (tLObject instanceof TLRPC$Chat) {
                    ((MentionCell) viewHolder.itemView).setChat((TLRPC$Chat) tLObject);
                }
            } else {
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
