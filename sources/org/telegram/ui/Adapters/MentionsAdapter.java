package org.telegram.ui.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Location;
import android.os.Build.VERSION;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import java.util.ArrayList;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.DataQuery.KeywordResult;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.SendMessagesHelper.LocationProvider;
import org.telegram.messenger.SendMessagesHelper.LocationProvider.LocationProviderDelegate;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.BotInfo;
import org.telegram.tgnet.TLRPC.BotInlineResult;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.ChatFull;
import org.telegram.tgnet.TLRPC.TL_botInlineMessageMediaAuto;
import org.telegram.tgnet.TLRPC.TL_channelFull;
import org.telegram.tgnet.TLRPC.TL_chatBannedRights;
import org.telegram.tgnet.TLRPC.TL_document;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_inlineBotSwitchPM;
import org.telegram.tgnet.TLRPC.TL_messages_botResults;
import org.telegram.tgnet.TLRPC.TL_photo;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Adapters.SearchAdapterHelper.HashtagObject;
import org.telegram.ui.Adapters.SearchAdapterHelper.SearchAdapterHelperDelegate;
import org.telegram.ui.Adapters.SearchAdapterHelper.SearchAdapterHelperDelegate.-CC;
import org.telegram.ui.Cells.BotSwitchCell;
import org.telegram.ui.Cells.ContextLinkCell;
import org.telegram.ui.Cells.MentionCell;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;

public class MentionsAdapter extends SelectionAdapter {
    private static final String punctuationsChars = " !\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~\n";
    private SparseArray<BotInfo> botInfo;
    private int botsCount;
    private Runnable cancelDelayRunnable;
    private int channelLastReqId;
    private int channelReqId;
    private boolean contextMedia;
    private int contextQueryReqid;
    private Runnable contextQueryRunnable;
    private int contextUsernameReqid;
    private int currentAccount = UserConfig.selectedAccount;
    private MentionsAdapterDelegate delegate;
    private long dialog_id;
    private User foundContextBot;
    private ChatFull info;
    private boolean inlineMediaEnabled = true;
    private boolean isDarkTheme;
    private boolean isSearchingMentions;
    private Location lastKnownLocation;
    private int lastPosition;
    private String[] lastSearchKeyboardLanguage;
    private String lastText;
    private boolean lastUsernameOnly;
    private LocationProvider locationProvider = new LocationProvider(new LocationProviderDelegate() {
        public void onLocationAcquired(Location location) {
            if (MentionsAdapter.this.foundContextBot != null && MentionsAdapter.this.foundContextBot.bot_inline_geo) {
                MentionsAdapter.this.lastKnownLocation = location;
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
            MentionsAdapter.this.lastKnownLocation = null;
        }
    };
    private Context mContext;
    private ArrayList<MessageObject> messages;
    private boolean needBotContext = true;
    private boolean needUsernames = true;
    private String nextQueryOffset;
    private boolean noUserName;
    private ChatActivity parentFragment;
    private int resultLength;
    private int resultStartPosition;
    private SearchAdapterHelper searchAdapterHelper;
    private Runnable searchGlobalRunnable;
    private ArrayList<BotInlineResult> searchResultBotContext;
    private TL_inlineBotSwitchPM searchResultBotContextSwitch;
    private ArrayList<String> searchResultCommands;
    private ArrayList<String> searchResultCommandsHelp;
    private ArrayList<User> searchResultCommandsUsers;
    private ArrayList<String> searchResultHashtags;
    private ArrayList<KeywordResult> searchResultSuggestions;
    private ArrayList<User> searchResultUsernames;
    private SparseArray<User> searchResultUsernamesMap;
    private String searchingContextQuery;
    private String searchingContextUsername;

    public interface MentionsAdapterDelegate {
        void needChangePanelVisibility(boolean z);

        void onContextClick(BotInlineResult botInlineResult);

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
        this.searchAdapterHelper = new SearchAdapterHelper(true);
        this.searchAdapterHelper.setDelegate(new SearchAdapterHelperDelegate() {
            public /* synthetic */ SparseArray<User> getExcludeUsers() {
                return -CC.$default$getExcludeUsers(this);
            }

            public void onDataSetChanged() {
                MentionsAdapter.this.notifyDataSetChanged();
            }

            public void onSetHashtags(ArrayList<HashtagObject> arrayList, HashMap<String, HashtagObject> hashMap) {
                if (MentionsAdapter.this.lastText != null) {
                    MentionsAdapter mentionsAdapter = MentionsAdapter.this;
                    mentionsAdapter.searchUsernameOrHashtag(mentionsAdapter.lastText, MentionsAdapter.this.lastPosition, MentionsAdapter.this.messages, MentionsAdapter.this.lastUsernameOnly);
                }
            }
        });
    }

    public void onDestroy() {
        LocationProvider locationProvider = this.locationProvider;
        if (locationProvider != null) {
            locationProvider.stop();
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

    public void setChatInfo(ChatFull chatFull) {
        this.currentAccount = UserConfig.selectedAccount;
        this.info = chatFull;
        if (!(this.inlineMediaEnabled || this.foundContextBot == null)) {
            ChatActivity chatActivity = this.parentFragment;
            if (chatActivity != null) {
                Chat currentChat = chatActivity.getCurrentChat();
                if (currentChat != null) {
                    this.inlineMediaEnabled = ChatObject.canSendStickers(currentChat);
                    if (this.inlineMediaEnabled) {
                        this.searchResultUsernames = null;
                        notifyDataSetChanged();
                        this.delegate.needChangePanelVisibility(false);
                        processFoundUser(this.foundContextBot);
                    }
                }
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

    public void setBotInfo(SparseArray<BotInfo> sparseArray) {
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

    public TL_inlineBotSwitchPM getBotContextSwitch() {
        return this.searchResultBotContextSwitch;
    }

    public int getContextBotId() {
        User user = this.foundContextBot;
        return user != null ? user.id : 0;
    }

    public User getContextBotUser() {
        return this.foundContextBot;
    }

    public String getContextBotName() {
        User user = this.foundContextBot;
        return user != null ? user.username : "";
    }

    private void processFoundUser(User user) {
        this.contextUsernameReqid = 0;
        this.locationProvider.stop();
        if (user == null || !user.bot || user.bot_inline_placeholder == null) {
            this.foundContextBot = null;
            this.inlineMediaEnabled = true;
        } else {
            this.foundContextBot = user;
            ChatActivity chatActivity = this.parentFragment;
            if (chatActivity != null) {
                Chat currentChat = chatActivity.getCurrentChat();
                if (currentChat != null) {
                    this.inlineMediaEnabled = ChatObject.canSendStickers(currentChat);
                    if (!this.inlineMediaEnabled) {
                        notifyDataSetChanged();
                        this.delegate.needChangePanelVisibility(true);
                        return;
                    }
                }
            }
            if (this.foundContextBot.bot_inline_geo) {
                SharedPreferences notificationsSettings = MessagesController.getNotificationsSettings(this.currentAccount);
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("inlinegeo_");
                stringBuilder.append(this.foundContextBot.id);
                if (!notificationsSettings.getBoolean(stringBuilder.toString(), false)) {
                    chatActivity = this.parentFragment;
                    if (!(chatActivity == null || chatActivity.getParentActivity() == null)) {
                        user = this.foundContextBot;
                        Builder builder = new Builder(this.parentFragment.getParentActivity());
                        builder.setTitle(LocaleController.getString("ShareYouLocationTitle", NUM));
                        builder.setMessage(LocaleController.getString("ShareYouLocationInline", NUM));
                        boolean[] zArr = new boolean[1];
                        builder.setPositiveButton(LocaleController.getString("OK", NUM), new -$$Lambda$MentionsAdapter$3hBy3ePtH3hOR9uoZtzoQOcheTs(this, zArr, user));
                        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), new -$$Lambda$MentionsAdapter$CT1DE5mOMJY5cACE3GAb-uXrprc(this, zArr));
                        this.parentFragment.showDialog(builder.create(), new -$$Lambda$MentionsAdapter$MgskdrDXUdlxFwtdPn3PQRymvWA(this, zArr));
                    }
                }
                checkLocationPermissionsOrStart();
            }
        }
        if (this.foundContextBot == null) {
            this.noUserName = true;
        } else {
            MentionsAdapterDelegate mentionsAdapterDelegate = this.delegate;
            if (mentionsAdapterDelegate != null) {
                mentionsAdapterDelegate.onContextSearch(true);
            }
            searchForContextBotResults(true, this.foundContextBot, this.searchingContextQuery, "");
        }
    }

    public /* synthetic */ void lambda$processFoundUser$0$MentionsAdapter(boolean[] zArr, User user, DialogInterface dialogInterface, int i) {
        zArr[0] = true;
        if (user != null) {
            Editor edit = MessagesController.getNotificationsSettings(this.currentAccount).edit();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("inlinegeo_");
            stringBuilder.append(user.id);
            edit.putBoolean(stringBuilder.toString(), true).commit();
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

    /* JADX WARNING: Missing block: B:27:0x004c, code skipped:
            if (r1.equals(r8) == false) goto L_0x004e;
     */
    /* JADX WARNING: Missing block: B:39:0x008c, code skipped:
            if (r8.length() != 0) goto L_0x008f;
     */
    private void searchForContextBot(java.lang.String r8, java.lang.String r9) {
        /*
        r7 = this;
        r0 = r7.foundContextBot;
        if (r0 == 0) goto L_0x0019;
    L_0x0004:
        r0 = r0.username;
        if (r0 == 0) goto L_0x0019;
    L_0x0008:
        r0 = r0.equals(r8);
        if (r0 == 0) goto L_0x0019;
    L_0x000e:
        r0 = r7.searchingContextQuery;
        if (r0 == 0) goto L_0x0019;
    L_0x0012:
        r0 = r0.equals(r9);
        if (r0 == 0) goto L_0x0019;
    L_0x0018:
        return;
    L_0x0019:
        r0 = 0;
        r7.searchResultBotContext = r0;
        r7.searchResultBotContextSwitch = r0;
        r7.notifyDataSetChanged();
        r1 = r7.foundContextBot;
        r2 = 0;
        if (r1 == 0) goto L_0x0034;
    L_0x0026:
        r1 = r7.inlineMediaEnabled;
        if (r1 != 0) goto L_0x002f;
    L_0x002a:
        if (r8 == 0) goto L_0x002f;
    L_0x002c:
        if (r9 == 0) goto L_0x002f;
    L_0x002e:
        return;
    L_0x002f:
        r1 = r7.delegate;
        r1.needChangePanelVisibility(r2);
    L_0x0034:
        r1 = r7.contextQueryRunnable;
        if (r1 == 0) goto L_0x003d;
    L_0x0038:
        org.telegram.messenger.AndroidUtilities.cancelRunOnUIThread(r1);
        r7.contextQueryRunnable = r0;
    L_0x003d:
        r1 = android.text.TextUtils.isEmpty(r8);
        r3 = 1;
        if (r1 != 0) goto L_0x004e;
    L_0x0044:
        r1 = r7.searchingContextUsername;
        if (r1 == 0) goto L_0x008f;
    L_0x0048:
        r1 = r1.equals(r8);
        if (r1 != 0) goto L_0x008f;
    L_0x004e:
        r1 = r7.contextUsernameReqid;
        if (r1 == 0) goto L_0x005f;
    L_0x0052:
        r1 = r7.currentAccount;
        r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r1);
        r4 = r7.contextUsernameReqid;
        r1.cancelRequest(r4, r3);
        r7.contextUsernameReqid = r2;
    L_0x005f:
        r1 = r7.contextQueryReqid;
        if (r1 == 0) goto L_0x0070;
    L_0x0063:
        r1 = r7.currentAccount;
        r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r1);
        r4 = r7.contextQueryReqid;
        r1.cancelRequest(r4, r3);
        r7.contextQueryReqid = r2;
    L_0x0070:
        r7.foundContextBot = r0;
        r7.inlineMediaEnabled = r3;
        r7.searchingContextUsername = r0;
        r7.searchingContextQuery = r0;
        r1 = r7.locationProvider;
        r1.stop();
        r7.noUserName = r2;
        r1 = r7.delegate;
        if (r1 == 0) goto L_0x0086;
    L_0x0083:
        r1.onContextSearch(r2);
    L_0x0086:
        if (r8 == 0) goto L_0x00e7;
    L_0x0088:
        r1 = r8.length();
        if (r1 != 0) goto L_0x008f;
    L_0x008e:
        goto L_0x00e7;
    L_0x008f:
        if (r9 != 0) goto L_0x00ac;
    L_0x0091:
        r8 = r7.contextQueryReqid;
        if (r8 == 0) goto L_0x00a2;
    L_0x0095:
        r8 = r7.currentAccount;
        r8 = org.telegram.tgnet.ConnectionsManager.getInstance(r8);
        r9 = r7.contextQueryReqid;
        r8.cancelRequest(r9, r3);
        r7.contextQueryReqid = r2;
    L_0x00a2:
        r7.searchingContextQuery = r0;
        r8 = r7.delegate;
        if (r8 == 0) goto L_0x00ab;
    L_0x00a8:
        r8.onContextSearch(r2);
    L_0x00ab:
        return;
    L_0x00ac:
        r0 = r7.delegate;
        if (r0 == 0) goto L_0x00c7;
    L_0x00b0:
        r1 = r7.foundContextBot;
        if (r1 == 0) goto L_0x00b8;
    L_0x00b4:
        r0.onContextSearch(r3);
        goto L_0x00c7;
    L_0x00b8:
        r0 = "gif";
        r1 = r8.equals(r0);
        if (r1 == 0) goto L_0x00c7;
    L_0x00c0:
        r7.searchingContextUsername = r0;
        r0 = r7.delegate;
        r0.onContextSearch(r2);
    L_0x00c7:
        r0 = r7.currentAccount;
        r5 = org.telegram.messenger.MessagesController.getInstance(r0);
        r0 = r7.currentAccount;
        r6 = org.telegram.messenger.MessagesStorage.getInstance(r0);
        r7.searchingContextQuery = r9;
        r0 = new org.telegram.ui.Adapters.MentionsAdapter$4;
        r1 = r0;
        r2 = r7;
        r3 = r9;
        r4 = r8;
        r1.<init>(r3, r4, r5, r6);
        r7.contextQueryRunnable = r0;
        r8 = r7.contextQueryRunnable;
        r0 = 400; // 0x190 float:5.6E-43 double:1.976E-321;
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r8, r0);
    L_0x00e7:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Adapters.MentionsAdapter.searchForContextBot(java.lang.String, java.lang.String):void");
    }

    private void onLocationUnavailable() {
        User user = this.foundContextBot;
        if (user != null && user.bot_inline_geo) {
            this.lastKnownLocation = new Location("network");
            this.lastKnownLocation.setLatitude(-1000.0d);
            this.lastKnownLocation.setLongitude(-1000.0d);
            searchForContextBotResults(true, this.foundContextBot, this.searchingContextQuery, "");
        }
    }

    private void checkLocationPermissionsOrStart() {
        ChatActivity chatActivity = this.parentFragment;
        if (!(chatActivity == null || chatActivity.getParentActivity() == null)) {
            if (VERSION.SDK_INT >= 23) {
                if (this.parentFragment.getParentActivity().checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION") != 0) {
                    this.parentFragment.getParentActivity().requestPermissions(new String[]{r1, "android.permission.ACCESS_FINE_LOCATION"}, 2);
                    return;
                }
            }
            User user = this.foundContextBot;
            if (user != null && user.bot_inline_geo) {
                this.locationProvider.start();
            }
        }
    }

    public void setSearchingMentions(boolean z) {
        this.isSearchingMentions = z;
    }

    public String getBotCaption() {
        User user = this.foundContextBot;
        if (user != null) {
            return user.bot_inline_placeholder;
        }
        String str = this.searchingContextUsername;
        return (str == null || !str.equals("gif")) ? null : "Search GIFs";
    }

    public void searchForContextBotForNextOffset() {
        if (this.contextQueryReqid == 0) {
            String str = this.nextQueryOffset;
            if (str != null && str.length() != 0) {
                User user = this.foundContextBot;
                if (user != null) {
                    String str2 = this.searchingContextQuery;
                    if (str2 != null) {
                        searchForContextBotResults(true, user, str2, this.nextQueryOffset);
                    }
                }
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:27:0x00b2  */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x00ad  */
    private void searchForContextBotResults(boolean r17, org.telegram.tgnet.TLRPC.User r18, java.lang.String r19, java.lang.String r20) {
        /*
        r16 = this;
        r8 = r16;
        r9 = r18;
        r10 = r19;
        r11 = r20;
        r0 = r8.contextQueryReqid;
        r1 = 0;
        r12 = 1;
        if (r0 == 0) goto L_0x001b;
    L_0x000e:
        r0 = r8.currentAccount;
        r0 = org.telegram.tgnet.ConnectionsManager.getInstance(r0);
        r2 = r8.contextQueryReqid;
        r0.cancelRequest(r2, r12);
        r8.contextQueryReqid = r1;
    L_0x001b:
        r0 = r8.inlineMediaEnabled;
        if (r0 != 0) goto L_0x0027;
    L_0x001f:
        r0 = r8.delegate;
        if (r0 == 0) goto L_0x0026;
    L_0x0023:
        r0.onContextSearch(r1);
    L_0x0026:
        return;
    L_0x0027:
        if (r10 == 0) goto L_0x012c;
    L_0x0029:
        if (r9 != 0) goto L_0x002d;
    L_0x002b:
        goto L_0x012c;
    L_0x002d:
        r0 = r9.bot_inline_geo;
        if (r0 == 0) goto L_0x0036;
    L_0x0031:
        r0 = r8.lastKnownLocation;
        if (r0 != 0) goto L_0x0036;
    L_0x0035:
        return;
    L_0x0036:
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r1 = r8.dialog_id;
        r0.append(r1);
        r1 = "_";
        r0.append(r1);
        r0.append(r10);
        r0.append(r1);
        r0.append(r11);
        r0.append(r1);
        r2 = r8.dialog_id;
        r0.append(r2);
        r0.append(r1);
        r2 = r9.id;
        r0.append(r2);
        r0.append(r1);
        r1 = r9.bot_inline_geo;
        r13 = -NUM; // 0xCLASSNAMEfNUM float:0.0 double:-1000.0;
        if (r1 == 0) goto L_0x0088;
    L_0x006a:
        r1 = r8.lastKnownLocation;
        if (r1 == 0) goto L_0x0088;
    L_0x006e:
        r1 = r1.getLatitude();
        r3 = (r1 > r13 ? 1 : (r1 == r13 ? 0 : -1));
        if (r3 == 0) goto L_0x0088;
    L_0x0076:
        r1 = r8.lastKnownLocation;
        r1 = r1.getLatitude();
        r3 = r8.lastKnownLocation;
        r3 = r3.getLongitude();
        r1 = r1 + r3;
        r1 = java.lang.Double.valueOf(r1);
        goto L_0x008a;
    L_0x0088:
        r1 = "";
    L_0x008a:
        r0.append(r1);
        r15 = r0.toString();
        r0 = r8.currentAccount;
        r7 = org.telegram.messenger.MessagesStorage.getInstance(r0);
        r6 = new org.telegram.ui.Adapters.-$$Lambda$MentionsAdapter$5habXq1r64920QdCkG9j6xMgM4U;
        r0 = r6;
        r1 = r16;
        r2 = r19;
        r3 = r17;
        r4 = r18;
        r5 = r20;
        r12 = r6;
        r6 = r7;
        r13 = r7;
        r7 = r15;
        r0.<init>(r1, r2, r3, r4, r5, r6, r7);
        if (r17 == 0) goto L_0x00b2;
    L_0x00ad:
        r13.getBotCache(r15, r12);
        goto L_0x012b;
    L_0x00b2:
        r0 = new org.telegram.tgnet.TLRPC$TL_messages_getInlineBotResults;
        r0.<init>();
        r1 = r8.currentAccount;
        r1 = org.telegram.messenger.MessagesController.getInstance(r1);
        r1 = r1.getInputUser(r9);
        r0.bot = r1;
        r0.query = r10;
        r0.offset = r11;
        r1 = r9.bot_inline_geo;
        if (r1 == 0) goto L_0x0105;
    L_0x00cb:
        r1 = r8.lastKnownLocation;
        if (r1 == 0) goto L_0x0105;
    L_0x00cf:
        r1 = r1.getLatitude();
        r3 = -NUM; // 0xCLASSNAMEfNUM float:0.0 double:-1000.0;
        r5 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1));
        if (r5 == 0) goto L_0x0105;
    L_0x00dc:
        r1 = r0.flags;
        r2 = 1;
        r1 = r1 | r2;
        r0.flags = r1;
        r1 = new org.telegram.tgnet.TLRPC$TL_inputGeoPoint;
        r1.<init>();
        r0.geo_point = r1;
        r1 = r0.geo_point;
        r2 = r8.lastKnownLocation;
        r2 = r2.getLatitude();
        r2 = org.telegram.messenger.AndroidUtilities.fixLocationCoord(r2);
        r1.lat = r2;
        r1 = r0.geo_point;
        r2 = r8.lastKnownLocation;
        r2 = r2.getLongitude();
        r2 = org.telegram.messenger.AndroidUtilities.fixLocationCoord(r2);
        r1._long = r2;
    L_0x0105:
        r1 = r8.dialog_id;
        r2 = (int) r1;
        if (r2 == 0) goto L_0x0117;
    L_0x010a:
        r1 = r8.currentAccount;
        r1 = org.telegram.messenger.MessagesController.getInstance(r1);
        r1 = r1.getInputPeer(r2);
        r0.peer = r1;
        goto L_0x011e;
    L_0x0117:
        r1 = new org.telegram.tgnet.TLRPC$TL_inputPeerEmpty;
        r1.<init>();
        r0.peer = r1;
    L_0x011e:
        r1 = r8.currentAccount;
        r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r1);
        r2 = 2;
        r0 = r1.sendRequest(r0, r12, r2);
        r8.contextQueryReqid = r0;
    L_0x012b:
        return;
    L_0x012c:
        r0 = 0;
        r8.searchingContextQuery = r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Adapters.MentionsAdapter.searchForContextBotResults(boolean, org.telegram.tgnet.TLRPC$User, java.lang.String, java.lang.String):void");
    }

    public /* synthetic */ void lambda$searchForContextBotResults$4$MentionsAdapter(String str, boolean z, User user, String str2, MessagesStorage messagesStorage, String str3, TLObject tLObject, TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new -$$Lambda$MentionsAdapter$HVDPv79fAM1RehGsWWysAxv-W1Q(this, str, z, tLObject, user, str2, messagesStorage, str3));
    }

    public /* synthetic */ void lambda$null$3$MentionsAdapter(String str, boolean z, TLObject tLObject, User user, String str2, MessagesStorage messagesStorage, String str3) {
        if (str.equals(this.searchingContextQuery)) {
            MentionsAdapterDelegate mentionsAdapterDelegate;
            this.contextQueryReqid = 0;
            if (z && tLObject == null) {
                searchForContextBotResults(false, user, str, str2);
            } else {
                mentionsAdapterDelegate = this.delegate;
                if (mentionsAdapterDelegate != null) {
                    mentionsAdapterDelegate.onContextSearch(false);
                }
            }
            if (tLObject instanceof TL_messages_botResults) {
                boolean z2;
                Object obj;
                TL_messages_botResults tL_messages_botResults = (TL_messages_botResults) tLObject;
                if (!(z || tL_messages_botResults.cache_time == 0)) {
                    messagesStorage.saveBotCache(str3, tL_messages_botResults);
                }
                this.nextQueryOffset = tL_messages_botResults.next_offset;
                if (this.searchResultBotContextSwitch == null) {
                    this.searchResultBotContextSwitch = tL_messages_botResults.switch_pm;
                }
                int i = 0;
                while (true) {
                    z2 = true;
                    if (i >= tL_messages_botResults.results.size()) {
                        break;
                    }
                    BotInlineResult botInlineResult = (BotInlineResult) tL_messages_botResults.results.get(i);
                    if (!((botInlineResult.document instanceof TL_document) || (botInlineResult.photo instanceof TL_photo))) {
                        if (!"game".equals(botInlineResult.type) && botInlineResult.content == null && (botInlineResult.send_message instanceof TL_botInlineMessageMediaAuto)) {
                            tL_messages_botResults.results.remove(i);
                            i--;
                        }
                    }
                    botInlineResult.query_id = tL_messages_botResults.query_id;
                    i++;
                }
                if (this.searchResultBotContext == null || str2.length() == 0) {
                    this.searchResultBotContext = tL_messages_botResults.results;
                    this.contextMedia = tL_messages_botResults.gallery;
                    obj = null;
                } else {
                    this.searchResultBotContext.addAll(tL_messages_botResults.results);
                    if (tL_messages_botResults.results.isEmpty()) {
                        this.nextQueryOffset = "";
                    }
                    obj = 1;
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
                if (obj != null) {
                    i = this.searchResultBotContextSwitch != null ? 1 : 0;
                    notifyItemChanged(((this.searchResultBotContext.size() - tL_messages_botResults.results.size()) + i) - 1);
                    notifyItemRangeInserted((this.searchResultBotContext.size() - tL_messages_botResults.results.size()) + i, tL_messages_botResults.results.size());
                } else {
                    notifyDataSetChanged();
                }
                mentionsAdapterDelegate = this.delegate;
                if (this.searchResultBotContext.isEmpty() && this.searchResultBotContextSwitch == null) {
                    z2 = false;
                }
                mentionsAdapterDelegate.needChangePanelVisibility(z2);
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:120:0x01d2  */
    /* JADX WARNING: Removed duplicated region for block: B:118:0x01cc  */
    public void searchUsernameOrHashtag(java.lang.String r17, int r18, java.util.ArrayList<org.telegram.messenger.MessageObject> r19, boolean r20) {
        /*
        r16 = this;
        r7 = r16;
        r0 = r17;
        r1 = r18;
        r2 = r19;
        r3 = r20;
        r4 = r7.cancelDelayRunnable;
        r5 = 0;
        if (r4 == 0) goto L_0x0014;
    L_0x000f:
        org.telegram.messenger.AndroidUtilities.cancelRunOnUIThread(r4);
        r7.cancelDelayRunnable = r5;
    L_0x0014:
        r4 = r7.channelReqId;
        r6 = 0;
        r8 = 1;
        if (r4 == 0) goto L_0x0027;
    L_0x001a:
        r4 = r7.currentAccount;
        r4 = org.telegram.tgnet.ConnectionsManager.getInstance(r4);
        r9 = r7.channelReqId;
        r4.cancelRequest(r9, r8);
        r7.channelReqId = r6;
    L_0x0027:
        r4 = r7.searchGlobalRunnable;
        if (r4 == 0) goto L_0x0030;
    L_0x002b:
        org.telegram.messenger.AndroidUtilities.cancelRunOnUIThread(r4);
        r7.searchGlobalRunnable = r5;
    L_0x0030:
        r4 = android.text.TextUtils.isEmpty(r17);
        if (r4 == 0) goto L_0x0041;
    L_0x0036:
        r7.searchForContextBot(r5, r5);
        r0 = r7.delegate;
        r0.needChangePanelVisibility(r6);
        r7.lastText = r5;
        return;
    L_0x0041:
        r4 = r17.length();
        if (r4 <= 0) goto L_0x004a;
    L_0x0047:
        r4 = r1 + -1;
        goto L_0x004b;
    L_0x004a:
        r4 = r1;
    L_0x004b:
        r7.lastText = r5;
        r7.lastUsernameOnly = r3;
        r9 = new java.lang.StringBuilder;
        r9.<init>();
        r10 = 64;
        r11 = 3;
        r12 = 32;
        if (r3 != 0) goto L_0x00de;
    L_0x005b:
        r13 = r7.needBotContext;
        if (r13 == 0) goto L_0x00de;
    L_0x005f:
        r13 = r0.charAt(r6);
        if (r13 != r10) goto L_0x00de;
    L_0x0065:
        r13 = r0.indexOf(r12);
        r14 = r17.length();
        r15 = "";
        if (r13 <= 0) goto L_0x007b;
    L_0x0071:
        r14 = r0.substring(r8, r13);
        r13 = r13 + r8;
        r13 = r0.substring(r13);
        goto L_0x00a4;
    L_0x007b:
        r13 = r14 + -1;
        r13 = r0.charAt(r13);
        r10 = 116; // 0x74 float:1.63E-43 double:5.73E-322;
        if (r13 != r10) goto L_0x009f;
    L_0x0085:
        r10 = r14 + -2;
        r10 = r0.charAt(r10);
        r13 = 111; // 0x6f float:1.56E-43 double:5.5E-322;
        if (r10 != r13) goto L_0x009f;
    L_0x008f:
        r14 = r14 - r11;
        r10 = r0.charAt(r14);
        r13 = 98;
        if (r10 != r13) goto L_0x009f;
    L_0x0098:
        r10 = r0.substring(r8);
        r14 = r10;
        r13 = r15;
        goto L_0x00a4;
    L_0x009f:
        r7.searchForContextBot(r5, r5);
        r13 = r5;
        r14 = r13;
    L_0x00a4:
        if (r14 == 0) goto L_0x00da;
    L_0x00a6:
        r10 = r14.length();
        if (r10 < r8) goto L_0x00da;
    L_0x00ac:
        r10 = 1;
    L_0x00ad:
        r11 = r14.length();
        if (r10 >= r11) goto L_0x00d9;
    L_0x00b3:
        r11 = r14.charAt(r10);
        r12 = 48;
        if (r11 < r12) goto L_0x00bf;
    L_0x00bb:
        r12 = 57;
        if (r11 <= r12) goto L_0x00d4;
    L_0x00bf:
        r12 = 97;
        if (r11 < r12) goto L_0x00c7;
    L_0x00c3:
        r12 = 122; // 0x7a float:1.71E-43 double:6.03E-322;
        if (r11 <= r12) goto L_0x00d4;
    L_0x00c7:
        r12 = 65;
        if (r11 < r12) goto L_0x00cf;
    L_0x00cb:
        r12 = 90;
        if (r11 <= r12) goto L_0x00d4;
    L_0x00cf:
        r12 = 95;
        if (r11 == r12) goto L_0x00d4;
    L_0x00d3:
        goto L_0x00da;
    L_0x00d4:
        r10 = r10 + 1;
        r12 = 32;
        goto L_0x00ad;
    L_0x00d9:
        r15 = r14;
    L_0x00da:
        r7.searchForContextBot(r15, r13);
        goto L_0x00e1;
    L_0x00de:
        r7.searchForContextBot(r5, r5);
    L_0x00e1:
        r10 = r7.foundContextBot;
        if (r10 == 0) goto L_0x00e6;
    L_0x00e5:
        return;
    L_0x00e6:
        r10 = r7.currentAccount;
        r10 = org.telegram.messenger.MessagesController.getInstance(r10);
        r12 = -1;
        if (r3 == 0) goto L_0x0102;
    L_0x00ef:
        r0 = r0.substring(r8);
        r9.append(r0);
        r7.resultStartPosition = r6;
        r0 = r9.length();
        r7.resultLength = r0;
        r0 = 0;
    L_0x00ff:
        r4 = -1;
        goto L_0x01ca;
    L_0x0102:
        if (r4 < 0) goto L_0x01c7;
    L_0x0104:
        r13 = r17.length();
        if (r4 < r13) goto L_0x010e;
    L_0x010a:
        r11 = 64;
        goto L_0x01c3;
    L_0x010e:
        r13 = r0.charAt(r4);
        if (r4 == 0) goto L_0x012b;
    L_0x0114:
        r14 = r4 + -1;
        r15 = r0.charAt(r14);
        r11 = 32;
        if (r15 == r11) goto L_0x012b;
    L_0x011e:
        r11 = r0.charAt(r14);
        r14 = 10;
        if (r11 != r14) goto L_0x0127;
    L_0x0126:
        goto L_0x012b;
    L_0x0127:
        r11 = 64;
        goto L_0x01c0;
    L_0x012b:
        r11 = 64;
        if (r13 != r11) goto L_0x0157;
    L_0x012f:
        r14 = r7.needUsernames;
        if (r14 != 0) goto L_0x0139;
    L_0x0133:
        r14 = r7.needBotContext;
        if (r14 == 0) goto L_0x01c0;
    L_0x0137:
        if (r4 != 0) goto L_0x01c0;
    L_0x0139:
        r11 = r7.info;
        if (r11 != 0) goto L_0x014b;
    L_0x013d:
        if (r4 == 0) goto L_0x014b;
    L_0x013f:
        r7.lastText = r0;
        r7.lastPosition = r1;
        r7.messages = r2;
        r0 = r7.delegate;
        r0.needChangePanelVisibility(r6);
        return;
    L_0x014b:
        r7.resultStartPosition = r4;
        r0 = r9.length();
        r0 = r0 + r8;
        r7.resultLength = r0;
        r0 = 0;
        goto L_0x01ca;
    L_0x0157:
        r14 = 35;
        if (r13 != r14) goto L_0x017d;
    L_0x015b:
        r11 = r7.searchAdapterHelper;
        r11 = r11.loadRecentHashtags();
        if (r11 == 0) goto L_0x0171;
    L_0x0163:
        r7.resultStartPosition = r4;
        r0 = r9.length();
        r0 = r0 + r8;
        r7.resultLength = r0;
        r9.insert(r6, r13);
        r0 = 1;
        goto L_0x00ff;
    L_0x0171:
        r7.lastText = r0;
        r7.lastPosition = r1;
        r7.messages = r2;
        r0 = r7.delegate;
        r0.needChangePanelVisibility(r6);
        return;
    L_0x017d:
        if (r4 != 0) goto L_0x0193;
    L_0x017f:
        r14 = r7.botInfo;
        if (r14 == 0) goto L_0x0193;
    L_0x0183:
        r14 = 47;
        if (r13 != r14) goto L_0x0193;
    L_0x0187:
        r7.resultStartPosition = r4;
        r0 = r9.length();
        r0 = r0 + r8;
        r7.resultLength = r0;
        r0 = 2;
        goto L_0x00ff;
    L_0x0193:
        r14 = 58;
        if (r13 != r14) goto L_0x01c0;
    L_0x0197:
        r14 = r9.length();
        if (r14 <= 0) goto L_0x01c0;
    L_0x019d:
        r14 = r9.charAt(r6);
        r15 = " !\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~\n";
        r14 = r15.indexOf(r14);
        if (r14 < 0) goto L_0x01ab;
    L_0x01a9:
        r14 = 1;
        goto L_0x01ac;
    L_0x01ab:
        r14 = 0;
    L_0x01ac:
        if (r14 == 0) goto L_0x01b4;
    L_0x01ae:
        r14 = r9.length();
        if (r14 <= r8) goto L_0x01c0;
    L_0x01b4:
        r7.resultStartPosition = r4;
        r0 = r9.length();
        r0 = r0 + r8;
        r7.resultLength = r0;
        r0 = 3;
        goto L_0x00ff;
    L_0x01c0:
        r9.insert(r6, r13);
    L_0x01c3:
        r4 = r4 + -1;
        goto L_0x0102;
    L_0x01c7:
        r0 = -1;
        goto L_0x00ff;
    L_0x01ca:
        if (r0 != r12) goto L_0x01d2;
    L_0x01cc:
        r0 = r7.delegate;
        r0.needChangePanelVisibility(r6);
        return;
    L_0x01d2:
        if (r0 != 0) goto L_0x03ce;
    L_0x01d4:
        r0 = new java.util.ArrayList;
        r0.<init>();
        r1 = 0;
    L_0x01da:
        r11 = 100;
        r12 = r19.size();
        r11 = java.lang.Math.min(r11, r12);
        if (r1 >= r11) goto L_0x0204;
    L_0x01e6:
        r11 = r2.get(r1);
        r11 = (org.telegram.messenger.MessageObject) r11;
        r11 = r11.messageOwner;
        r11 = r11.from_id;
        r12 = java.lang.Integer.valueOf(r11);
        r12 = r0.contains(r12);
        if (r12 != 0) goto L_0x0201;
    L_0x01fa:
        r11 = java.lang.Integer.valueOf(r11);
        r0.add(r11);
    L_0x0201:
        r1 = r1 + 1;
        goto L_0x01da;
    L_0x0204:
        r1 = r9.toString();
        r9 = r1.toLowerCase();
        r1 = 32;
        r1 = r9.indexOf(r1);
        if (r1 < 0) goto L_0x0216;
    L_0x0214:
        r1 = 1;
        goto L_0x0217;
    L_0x0216:
        r1 = 0;
    L_0x0217:
        r11 = new java.util.ArrayList;
        r11.<init>();
        r2 = new android.util.SparseArray;
        r2.<init>();
        r12 = new android.util.SparseArray;
        r12.<init>();
        r13 = r7.currentAccount;
        r13 = org.telegram.messenger.DataQuery.getInstance(r13);
        r13 = r13.inlineBots;
        r14 = 5;
        if (r3 != 0) goto L_0x028d;
    L_0x0231:
        r15 = r7.needBotContext;
        if (r15 == 0) goto L_0x028d;
    L_0x0235:
        if (r4 != 0) goto L_0x028d;
    L_0x0237:
        r4 = r13.isEmpty();
        if (r4 != 0) goto L_0x028d;
    L_0x023d:
        r4 = 0;
        r15 = 0;
    L_0x023f:
        r6 = r13.size();
        if (r4 >= r6) goto L_0x028d;
    L_0x0245:
        r6 = r13.get(r4);
        r6 = (org.telegram.tgnet.TLRPC.TL_topPeer) r6;
        r6 = r6.peer;
        r6 = r6.user_id;
        r6 = java.lang.Integer.valueOf(r6);
        r6 = r10.getUser(r6);
        if (r6 != 0) goto L_0x025a;
    L_0x0259:
        goto L_0x0289;
    L_0x025a:
        r8 = r6.username;
        if (r8 == 0) goto L_0x0286;
    L_0x025e:
        r8 = r8.length();
        if (r8 <= 0) goto L_0x0286;
    L_0x0264:
        r8 = r9.length();
        if (r8 <= 0) goto L_0x0276;
    L_0x026a:
        r8 = r6.username;
        r8 = r8.toLowerCase();
        r8 = r8.startsWith(r9);
        if (r8 != 0) goto L_0x027c;
    L_0x0276:
        r8 = r9.length();
        if (r8 != 0) goto L_0x0286;
    L_0x027c:
        r11.add(r6);
        r8 = r6.id;
        r2.put(r8, r6);
        r15 = r15 + 1;
    L_0x0286:
        if (r15 != r14) goto L_0x0289;
    L_0x0288:
        goto L_0x028d;
    L_0x0289:
        r4 = r4 + 1;
        r8 = 1;
        goto L_0x023f;
    L_0x028d:
        r4 = r7.parentFragment;
        if (r4 == 0) goto L_0x0296;
    L_0x0291:
        r4 = r4.getCurrentChat();
        goto L_0x02a6;
    L_0x0296:
        r4 = r7.info;
        if (r4 == 0) goto L_0x02a5;
    L_0x029a:
        r4 = r4.id;
        r4 = java.lang.Integer.valueOf(r4);
        r4 = r10.getChat(r4);
        goto L_0x02a6;
    L_0x02a5:
        r4 = r5;
    L_0x02a6:
        if (r4 == 0) goto L_0x037d;
    L_0x02a8:
        r6 = r7.info;
        if (r6 == 0) goto L_0x037d;
    L_0x02ac:
        r6 = r6.participants;
        if (r6 == 0) goto L_0x037d;
    L_0x02b0:
        r6 = org.telegram.messenger.ChatObject.isChannel(r4);
        if (r6 == 0) goto L_0x02ba;
    L_0x02b6:
        r6 = r4.megagroup;
        if (r6 == 0) goto L_0x037d;
    L_0x02ba:
        r6 = 0;
    L_0x02bb:
        r8 = r7.info;
        r8 = r8.participants;
        r8 = r8.participants;
        r8 = r8.size();
        if (r6 >= r8) goto L_0x037d;
    L_0x02c7:
        r8 = r7.info;
        r8 = r8.participants;
        r8 = r8.participants;
        r8 = r8.get(r6);
        r8 = (org.telegram.tgnet.TLRPC.ChatParticipant) r8;
        r8 = r8.user_id;
        r8 = java.lang.Integer.valueOf(r8);
        r8 = r10.getUser(r8);
        if (r8 == 0) goto L_0x0379;
    L_0x02df:
        if (r3 != 0) goto L_0x02e7;
    L_0x02e1:
        r13 = org.telegram.messenger.UserObject.isUserSelf(r8);
        if (r13 != 0) goto L_0x0379;
    L_0x02e7:
        r13 = r8.id;
        r13 = r2.indexOfKey(r13);
        if (r13 < 0) goto L_0x02f1;
    L_0x02ef:
        goto L_0x0379;
    L_0x02f1:
        r13 = r9.length();
        if (r13 != 0) goto L_0x0300;
    L_0x02f7:
        r13 = r8.deleted;
        if (r13 != 0) goto L_0x0379;
    L_0x02fb:
        r11.add(r8);
        goto L_0x0379;
    L_0x0300:
        r13 = r8.username;
        if (r13 == 0) goto L_0x031f;
    L_0x0304:
        r13 = r13.length();
        if (r13 <= 0) goto L_0x031f;
    L_0x030a:
        r13 = r8.username;
        r13 = r13.toLowerCase();
        r13 = r13.startsWith(r9);
        if (r13 == 0) goto L_0x031f;
    L_0x0316:
        r11.add(r8);
        r13 = r8.id;
        r12.put(r13, r8);
        goto L_0x0379;
    L_0x031f:
        r13 = r8.first_name;
        if (r13 == 0) goto L_0x033e;
    L_0x0323:
        r13 = r13.length();
        if (r13 <= 0) goto L_0x033e;
    L_0x0329:
        r13 = r8.first_name;
        r13 = r13.toLowerCase();
        r13 = r13.startsWith(r9);
        if (r13 == 0) goto L_0x033e;
    L_0x0335:
        r11.add(r8);
        r13 = r8.id;
        r12.put(r13, r8);
        goto L_0x0379;
    L_0x033e:
        r13 = r8.last_name;
        if (r13 == 0) goto L_0x035d;
    L_0x0342:
        r13 = r13.length();
        if (r13 <= 0) goto L_0x035d;
    L_0x0348:
        r13 = r8.last_name;
        r13 = r13.toLowerCase();
        r13 = r13.startsWith(r9);
        if (r13 == 0) goto L_0x035d;
    L_0x0354:
        r11.add(r8);
        r13 = r8.id;
        r12.put(r13, r8);
        goto L_0x0379;
    L_0x035d:
        if (r1 == 0) goto L_0x0379;
    L_0x035f:
        r13 = r8.first_name;
        r15 = r8.last_name;
        r13 = org.telegram.messenger.ContactsController.formatName(r13, r15);
        r13 = r13.toLowerCase();
        r13 = r13.startsWith(r9);
        if (r13 == 0) goto L_0x0379;
    L_0x0371:
        r11.add(r8);
        r13 = r8.id;
        r12.put(r13, r8);
    L_0x0379:
        r6 = r6 + 1;
        goto L_0x02bb;
    L_0x037d:
        r1 = new org.telegram.ui.Adapters.-$$Lambda$MentionsAdapter$RtUwcbbmysCllxWaHytbPO9oFzY;
        r1.<init>(r12, r0);
        java.util.Collections.sort(r11, r1);
        r7.searchResultHashtags = r5;
        r7.searchResultCommands = r5;
        r7.searchResultCommandsHelp = r5;
        r7.searchResultCommandsUsers = r5;
        r7.searchResultSuggestions = r5;
        if (r4 == 0) goto L_0x03c8;
    L_0x0391:
        r0 = r4.megagroup;
        if (r0 == 0) goto L_0x03c8;
    L_0x0395:
        r0 = r9.length();
        if (r0 <= 0) goto L_0x03c8;
    L_0x039b:
        r0 = r11.size();
        if (r0 >= r14) goto L_0x03ae;
    L_0x03a1:
        r0 = new org.telegram.ui.Adapters.-$$Lambda$MentionsAdapter$_MTgLbVZU0vjX841sg9FU0IKm0g;
        r0.<init>(r7, r11, r12);
        r7.cancelDelayRunnable = r0;
        r1 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r0, r1);
        goto L_0x03b2;
    L_0x03ae:
        r0 = 1;
        r7.showUsersResult(r11, r12, r0);
    L_0x03b2:
        r8 = new org.telegram.ui.Adapters.MentionsAdapter$5;
        r0 = r8;
        r1 = r16;
        r2 = r4;
        r3 = r9;
        r4 = r11;
        r5 = r12;
        r6 = r10;
        r0.<init>(r2, r3, r4, r5, r6);
        r7.searchGlobalRunnable = r8;
        r0 = 200; // 0xc8 float:2.8E-43 double:9.9E-322;
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r8, r0);
        goto L_0x04e5;
    L_0x03c8:
        r1 = 1;
        r7.showUsersResult(r11, r12, r1);
        goto L_0x04e5;
    L_0x03ce:
        r1 = 1;
        if (r0 != r1) goto L_0x0423;
    L_0x03d1:
        r0 = new java.util.ArrayList;
        r0.<init>();
        r1 = r9.toString();
        r1 = r1.toLowerCase();
        r2 = r7.searchAdapterHelper;
        r2 = r2.getHashtags();
        r3 = 0;
    L_0x03e5:
        r4 = r2.size();
        if (r3 >= r4) goto L_0x0405;
    L_0x03eb:
        r4 = r2.get(r3);
        r4 = (org.telegram.ui.Adapters.SearchAdapterHelper.HashtagObject) r4;
        if (r4 == 0) goto L_0x0402;
    L_0x03f3:
        r6 = r4.hashtag;
        if (r6 == 0) goto L_0x0402;
    L_0x03f7:
        r6 = r6.startsWith(r1);
        if (r6 == 0) goto L_0x0402;
    L_0x03fd:
        r4 = r4.hashtag;
        r0.add(r4);
    L_0x0402:
        r3 = r3 + 1;
        goto L_0x03e5;
    L_0x0405:
        r7.searchResultHashtags = r0;
        r7.searchResultUsernames = r5;
        r7.searchResultUsernamesMap = r5;
        r7.searchResultCommands = r5;
        r7.searchResultCommandsHelp = r5;
        r7.searchResultCommandsUsers = r5;
        r7.searchResultSuggestions = r5;
        r16.notifyDataSetChanged();
        r1 = r7.delegate;
        r0 = r0.isEmpty();
        r2 = 1;
        r0 = r0 ^ r2;
        r1.needChangePanelVisibility(r0);
        goto L_0x04e5;
    L_0x0423:
        r1 = 2;
        if (r0 != r1) goto L_0x04b6;
    L_0x0426:
        r0 = new java.util.ArrayList;
        r0.<init>();
        r1 = new java.util.ArrayList;
        r1.<init>();
        r2 = new java.util.ArrayList;
        r2.<init>();
        r3 = r9.toString();
        r3 = r3.toLowerCase();
        r4 = 0;
    L_0x043e:
        r6 = r7.botInfo;
        r6 = r6.size();
        if (r4 >= r6) goto L_0x0499;
    L_0x0446:
        r6 = r7.botInfo;
        r6 = r6.valueAt(r4);
        r6 = (org.telegram.tgnet.TLRPC.BotInfo) r6;
        r8 = 0;
    L_0x044f:
        r9 = r6.commands;
        r9 = r9.size();
        if (r8 >= r9) goto L_0x0496;
    L_0x0457:
        r9 = r6.commands;
        r9 = r9.get(r8);
        r9 = (org.telegram.tgnet.TLRPC.TL_botCommand) r9;
        if (r9 == 0) goto L_0x0493;
    L_0x0461:
        r11 = r9.command;
        if (r11 == 0) goto L_0x0493;
    L_0x0465:
        r11 = r11.startsWith(r3);
        if (r11 == 0) goto L_0x0493;
    L_0x046b:
        r11 = new java.lang.StringBuilder;
        r11.<init>();
        r12 = "/";
        r11.append(r12);
        r12 = r9.command;
        r11.append(r12);
        r11 = r11.toString();
        r0.add(r11);
        r9 = r9.description;
        r1.add(r9);
        r9 = r6.user_id;
        r9 = java.lang.Integer.valueOf(r9);
        r9 = r10.getUser(r9);
        r2.add(r9);
    L_0x0493:
        r8 = r8 + 1;
        goto L_0x044f;
    L_0x0496:
        r4 = r4 + 1;
        goto L_0x043e;
    L_0x0499:
        r7.searchResultHashtags = r5;
        r7.searchResultUsernames = r5;
        r7.searchResultUsernamesMap = r5;
        r7.searchResultSuggestions = r5;
        r7.searchResultCommands = r0;
        r7.searchResultCommandsHelp = r1;
        r7.searchResultCommandsUsers = r2;
        r16.notifyDataSetChanged();
        r1 = r7.delegate;
        r0 = r0.isEmpty();
        r2 = 1;
        r0 = r0 ^ r2;
        r1.needChangePanelVisibility(r0);
        goto L_0x04e5;
    L_0x04b6:
        r1 = 3;
        if (r0 != r1) goto L_0x04e5;
    L_0x04b9:
        r0 = org.telegram.messenger.AndroidUtilities.getCurrentKeyboardLanguage();
        r1 = r7.lastSearchKeyboardLanguage;
        r1 = java.util.Arrays.equals(r0, r1);
        if (r1 != 0) goto L_0x04ce;
    L_0x04c5:
        r1 = r7.currentAccount;
        r1 = org.telegram.messenger.DataQuery.getInstance(r1);
        r1.fetchNewEmojiKeywords(r0);
    L_0x04ce:
        r7.lastSearchKeyboardLanguage = r0;
        r0 = r7.currentAccount;
        r0 = org.telegram.messenger.DataQuery.getInstance(r0);
        r1 = r7.lastSearchKeyboardLanguage;
        r2 = r9.toString();
        r3 = new org.telegram.ui.Adapters.-$$Lambda$MentionsAdapter$rScwFUSTmUPNK4byYGdy0eHPN2M;
        r3.<init>(r7);
        r4 = 0;
        r0.getEmojiSuggestions(r1, r2, r4, r3);
    L_0x04e5:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Adapters.MentionsAdapter.searchUsernameOrHashtag(java.lang.String, int, java.util.ArrayList, boolean):void");
    }

    static /* synthetic */ int lambda$searchUsernameOrHashtag$5(SparseArray sparseArray, ArrayList arrayList, User user, User user2) {
        if (sparseArray.indexOfKey(user.id) >= 0 && sparseArray.indexOfKey(user2.id) >= 0) {
            return 0;
        }
        if (sparseArray.indexOfKey(user.id) >= 0) {
            return -1;
        }
        int i = 1;
        if (sparseArray.indexOfKey(user2.id) >= 0) {
            return 1;
        }
        int indexOf = arrayList.indexOf(Integer.valueOf(user.id));
        int indexOf2 = arrayList.indexOf(Integer.valueOf(user2.id));
        if (indexOf != -1 && indexOf2 != -1) {
            if (indexOf < indexOf2) {
                i = -1;
            } else if (indexOf == indexOf2) {
                i = 0;
            }
            return i;
        } else if (indexOf != -1 && indexOf2 == -1) {
            return -1;
        } else {
            if (indexOf != -1 || indexOf2 == -1) {
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
        ArrayList arrayList2 = this.searchResultSuggestions;
        boolean z = (arrayList2 == null || arrayList2.isEmpty()) ? false : true;
        mentionsAdapterDelegate.needChangePanelVisibility(z);
    }

    private void showUsersResult(ArrayList<User> arrayList, SparseArray<User> sparseArray, boolean z) {
        this.searchResultUsernames = arrayList;
        this.searchResultUsernamesMap = sparseArray;
        Runnable runnable = this.cancelDelayRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.cancelDelayRunnable = null;
        }
        if (z) {
            notifyDataSetChanged();
            this.delegate.needChangePanelVisibility(this.searchResultUsernames.isEmpty() ^ 1);
        }
    }

    public int getResultStartPosition() {
        return this.resultStartPosition;
    }

    public int getResultLength() {
        return this.resultLength;
    }

    public ArrayList<BotInlineResult> getSearchResultBotContext() {
        return this.searchResultBotContext;
    }

    public int getItemCount() {
        int i = 1;
        if (this.foundContextBot != null && !this.inlineMediaEnabled) {
            return 1;
        }
        ArrayList arrayList = this.searchResultBotContext;
        if (arrayList != null) {
            int size = arrayList.size();
            if (this.searchResultBotContextSwitch == null) {
                i = 0;
            }
            return size + i;
        }
        arrayList = this.searchResultUsernames;
        if (arrayList != null) {
            return arrayList.size();
        }
        arrayList = this.searchResultHashtags;
        if (arrayList != null) {
            return arrayList.size();
        }
        arrayList = this.searchResultCommands;
        if (arrayList != null) {
            return arrayList.size();
        }
        arrayList = this.searchResultSuggestions;
        if (arrayList != null) {
            return arrayList.size();
        }
        return 0;
    }

    public int getItemViewType(int i) {
        if (this.foundContextBot != null && !this.inlineMediaEnabled) {
            return 3;
        }
        if (this.searchResultBotContext != null) {
            return (i != 0 || this.searchResultBotContextSwitch == null) ? 1 : 2;
        } else {
            return 0;
        }
    }

    public void addHashtagsFromMessage(CharSequence charSequence) {
        this.searchAdapterHelper.addHashtagsFromMessage(charSequence);
    }

    public int getItemPosition(int i) {
        return (this.searchResultBotContext == null || this.searchResultBotContextSwitch == null) ? i : i - 1;
    }

    public Object getItem(int i) {
        if (this.searchResultBotContext != null) {
            TL_inlineBotSwitchPM tL_inlineBotSwitchPM = this.searchResultBotContextSwitch;
            if (tL_inlineBotSwitchPM != null) {
                if (i == 0) {
                    return tL_inlineBotSwitchPM;
                }
                i--;
            }
            if (i < 0 || i >= this.searchResultBotContext.size()) {
                return null;
            }
            return this.searchResultBotContext.get(i);
        }
        ArrayList arrayList = this.searchResultUsernames;
        if (arrayList == null) {
            arrayList = this.searchResultHashtags;
            if (arrayList == null) {
                arrayList = this.searchResultSuggestions;
                if (arrayList == null) {
                    arrayList = this.searchResultCommands;
                    if (arrayList == null || i < 0 || i >= arrayList.size()) {
                        return null;
                    }
                    if (this.searchResultCommandsUsers == null || (this.botsCount == 1 && !(this.info instanceof TL_channelFull))) {
                        return this.searchResultCommands.get(i);
                    }
                    if (this.searchResultCommandsUsers.get(i) != null) {
                        Object[] objArr = new Object[2];
                        objArr[0] = this.searchResultCommands.get(i);
                        objArr[1] = this.searchResultCommandsUsers.get(i) != null ? ((User) this.searchResultCommandsUsers.get(i)).username : "";
                        return String.format("%s@%s", objArr);
                    }
                    return String.format("%s", new Object[]{this.searchResultCommands.get(i)});
                } else if (i < 0 || i >= arrayList.size()) {
                    return null;
                } else {
                    return this.searchResultSuggestions.get(i);
                }
            } else if (i < 0 || i >= arrayList.size()) {
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
        return (this.foundContextBot == null || this.inlineMediaEnabled) ? false : true;
    }

    public boolean isMediaLayout() {
        return this.contextMedia;
    }

    public boolean isEnabled(ViewHolder viewHolder) {
        return this.foundContextBot == null || this.inlineMediaEnabled;
    }

    public /* synthetic */ void lambda$onCreateViewHolder$8$MentionsAdapter(ContextLinkCell contextLinkCell) {
        this.delegate.onContextClick(contextLinkCell.getResult());
    }

    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View mentionCell;
        if (i == 0) {
            mentionCell = new MentionCell(this.mContext);
            mentionCell.setIsDarkTheme(this.isDarkTheme);
        } else if (i == 1) {
            mentionCell = new ContextLinkCell(this.mContext);
            mentionCell.setDelegate(new -$$Lambda$MentionsAdapter$VoCNDYNNW-mhCQ-lScxh9-TCNkI(this));
        } else if (i != 2) {
            mentionCell = new TextView(this.mContext);
            mentionCell.setPadding(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f));
            mentionCell.setTextSize(1, 14.0f);
            mentionCell.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText2"));
        } else {
            mentionCell = new BotSwitchCell(this.mContext);
        }
        return new Holder(mentionCell);
    }

    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        boolean z = false;
        if (viewHolder.getItemViewType() == 3) {
            TextView textView = (TextView) viewHolder.itemView;
            Chat currentChat = this.parentFragment.getCurrentChat();
            if (currentChat != null) {
                if (!ChatObject.hasAdminRights(currentChat)) {
                    TL_chatBannedRights tL_chatBannedRights = currentChat.default_banned_rights;
                    if (tL_chatBannedRights != null && tL_chatBannedRights.send_inline) {
                        textView.setText(LocaleController.getString("GlobalAttachInlineRestricted", NUM));
                        return;
                    }
                }
                if (AndroidUtilities.isBannedForever(currentChat.banned_rights)) {
                    textView.setText(LocaleController.getString("AttachInlineRestrictedForever", NUM));
                } else {
                    textView.setText(LocaleController.formatString("AttachInlineRestricted", NUM, LocaleController.formatDateForBan((long) currentChat.banned_rights.until_date)));
                }
            }
        } else if (this.searchResultBotContext != null) {
            Object obj = this.searchResultBotContextSwitch != null ? 1 : null;
            if (viewHolder.getItemViewType() != 2) {
                if (obj != null) {
                    i--;
                }
                ContextLinkCell contextLinkCell = (ContextLinkCell) viewHolder.itemView;
                BotInlineResult botInlineResult = (BotInlineResult) this.searchResultBotContext.get(i);
                boolean z2 = this.contextMedia;
                boolean z3 = i != this.searchResultBotContext.size() - 1;
                if (obj != null && i == 0) {
                    z = true;
                }
                contextLinkCell.setLink(botInlineResult, z2, z3, z);
            } else if (obj != null) {
                ((BotSwitchCell) viewHolder.itemView).setText(this.searchResultBotContextSwitch.text);
            }
        } else {
            ArrayList arrayList = this.searchResultUsernames;
            if (arrayList != null) {
                ((MentionCell) viewHolder.itemView).setUser((User) arrayList.get(i));
                return;
            }
            arrayList = this.searchResultHashtags;
            if (arrayList != null) {
                ((MentionCell) viewHolder.itemView).setText((String) arrayList.get(i));
                return;
            }
            arrayList = this.searchResultSuggestions;
            if (arrayList != null) {
                ((MentionCell) viewHolder.itemView).setEmojiSuggestion((KeywordResult) arrayList.get(i));
                return;
            }
            arrayList = this.searchResultCommands;
            if (arrayList != null) {
                MentionCell mentionCell = (MentionCell) viewHolder.itemView;
                String str = (String) arrayList.get(i);
                String str2 = (String) this.searchResultCommandsHelp.get(i);
                ArrayList arrayList2 = this.searchResultCommandsUsers;
                mentionCell.setBotCommand(str, str2, arrayList2 != null ? (User) arrayList2.get(i) : null);
            }
        }
    }

    public void onRequestPermissionsResultFragment(int i, String[] strArr, int[] iArr) {
        if (i == 2) {
            User user = this.foundContextBot;
            if (user != null && user.bot_inline_geo) {
                if (iArr.length <= 0 || iArr[0] != 0) {
                    onLocationUnavailable();
                } else {
                    this.locationProvider.start();
                }
            }
        }
    }
}
