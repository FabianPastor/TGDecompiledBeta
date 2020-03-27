package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.collection.ArraySet;
import androidx.core.graphics.ColorUtils;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import org.json.JSONException;
import org.json.JSONObject;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.LruCache;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.QuickAckDelegate;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$ChatFull;
import org.telegram.tgnet.TLRPC$Message;
import org.telegram.tgnet.TLRPC$StatsGraph;
import org.telegram.tgnet.TLRPC$TL_channels_getMessages;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_messageInteractionCounters;
import org.telegram.tgnet.TLRPC$TL_statsGraph;
import org.telegram.tgnet.TLRPC$TL_statsGraphAsync;
import org.telegram.tgnet.TLRPC$TL_statsGraphError;
import org.telegram.tgnet.TLRPC$TL_stats_broadcastStats;
import org.telegram.tgnet.TLRPC$TL_stats_getBroadcastStats;
import org.telegram.tgnet.TLRPC$TL_stats_loadAsyncGraph;
import org.telegram.tgnet.TLRPC$messages_Messages;
import org.telegram.tgnet.WriteToSocketDelegate;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BackDrawable;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.StatisticPostInfoCell;
import org.telegram.ui.Charts.BarChartView;
import org.telegram.ui.Charts.BaseChartView;
import org.telegram.ui.Charts.ChartPickerDelegate;
import org.telegram.ui.Charts.DoubleLinearChartView;
import org.telegram.ui.Charts.LinearChartView;
import org.telegram.ui.Charts.PieChartView;
import org.telegram.ui.Charts.StackBarChartView;
import org.telegram.ui.Charts.StackLinearChartView;
import org.telegram.ui.Charts.data.ChartData;
import org.telegram.ui.Charts.data.DoubleLinearChartData;
import org.telegram.ui.Charts.data.StackBarChartData;
import org.telegram.ui.Charts.data.StackLinearChartData;
import org.telegram.ui.Charts.view_data.ChartHeaderView;
import org.telegram.ui.Charts.view_data.LegendSignatureView;
import org.telegram.ui.Charts.view_data.LineViewData;
import org.telegram.ui.Charts.view_data.TransitionParams;
import org.telegram.ui.Components.ChatAvatarContainer;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.FlatCheckBox;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RLottieImageView;
import org.telegram.ui.Components.RadialProgressView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.StatisticActivity;

public class StatisticActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    /* access modifiers changed from: private */
    public Adapter adapter;
    ChatAvatarContainer avatarContainer;
    /* access modifiers changed from: private */
    public final TLRPC$ChatFull chat;
    /* access modifiers changed from: private */
    public LruCache<ChartData> childDataCache = new LruCache<>(50);
    DiffUtilsCallback diffUtilsCallback = new DiffUtilsCallback();
    /* access modifiers changed from: private */
    public ChartViewData followersData;
    /* access modifiers changed from: private */
    public ChartViewData growthData;
    private RLottieImageView imageView;
    private boolean initialLoading = true;
    /* access modifiers changed from: private */
    public ChartViewData interactionsData;
    /* access modifiers changed from: private */
    public ChartViewData ivInteractionsData;
    /* access modifiers changed from: private */
    public ChartViewData languagesData;
    /* access modifiers changed from: private */
    public ZoomCancelable lastCancelable;
    /* access modifiers changed from: private */
    public LinearLayoutManager layoutManager;
    private int loadFromId = -1;
    /* access modifiers changed from: private */
    public boolean messagesIsLoading;
    /* access modifiers changed from: private */
    public ChartViewData newFollowersBySourceData;
    /* access modifiers changed from: private */
    public LinearLayout progressLayout;
    private final SparseIntArray recentPostIdtoIndexMap = new SparseIntArray();
    /* access modifiers changed from: private */
    public final ArrayList<RecentPostInfo> recentPostsAll = new ArrayList<>();
    /* access modifiers changed from: private */
    public final ArrayList<RecentPostInfo> recentPostsLoaded = new ArrayList<>();
    /* access modifiers changed from: private */
    public RecyclerListView recyclerListView;
    /* access modifiers changed from: private */
    public BaseChartView.SharedUiComponents sharedUi;
    private final Runnable showProgressbar = new Runnable() {
        public void run() {
            StatisticActivity.this.progressLayout.animate().alpha(1.0f).setDuration(230);
        }
    };
    /* access modifiers changed from: private */
    public ChartViewData topHoursData;
    /* access modifiers changed from: private */
    public ChartViewData viewsBySourceData;

    public static class RecentPostInfo {
        public TLRPC$TL_messageInteractionCounters counters;
        public MessageObject message;
    }

    public StatisticActivity(Bundle bundle) {
        super(bundle);
        this.chat = getMessagesController().getChatFull(bundle.getInt("chat_id"));
    }

    public boolean onFragmentCreate() {
        getNotificationCenter().addObserver(this, NotificationCenter.messagesDidLoad);
        TLRPC$TL_stats_getBroadcastStats tLRPC$TL_stats_getBroadcastStats = new TLRPC$TL_stats_getBroadcastStats();
        tLRPC$TL_stats_getBroadcastStats.channel = MessagesController.getInstance(this.currentAccount).getInputChannel(this.chat.id);
        getConnectionsManager().bindRequestToGuid(getConnectionsManager().sendRequest(tLRPC$TL_stats_getBroadcastStats, new RequestDelegate() {
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                StatisticActivity.this.lambda$onFragmentCreate$1$StatisticActivity(tLObject, tLRPC$TL_error);
            }
        }, (QuickAckDelegate) null, (WriteToSocketDelegate) null, 0, this.chat.stats_dc, 1, true), this.classGuid);
        return super.onFragmentCreate();
    }

    public /* synthetic */ void lambda$onFragmentCreate$1$StatisticActivity(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        ChartViewData[] chartViewDataArr = new ChartViewData[8];
        if (tLObject instanceof TLRPC$TL_stats_broadcastStats) {
            TLRPC$TL_stats_broadcastStats tLRPC$TL_stats_broadcastStats = (TLRPC$TL_stats_broadcastStats) tLObject;
            chartViewDataArr[0] = createViewData(tLRPC$TL_stats_broadcastStats.growth_graph, LocaleController.getString("GrowthChartTitle", NUM), 0);
            chartViewDataArr[1] = createViewData(tLRPC$TL_stats_broadcastStats.followers_graph, LocaleController.getString("FollowersChartTitle", NUM), 0);
            chartViewDataArr[2] = createViewData(tLRPC$TL_stats_broadcastStats.top_hours_graph, LocaleController.getString("TopHoursChartTitle", NUM), 0);
            chartViewDataArr[3] = createViewData(tLRPC$TL_stats_broadcastStats.interactions_graph, LocaleController.getString("InteractionsChartTitle", NUM), 1);
            chartViewDataArr[4] = createViewData(tLRPC$TL_stats_broadcastStats.iv_interactions_graph, LocaleController.getString("IVInteractionsChartTitle", NUM), 1);
            chartViewDataArr[5] = createViewData(tLRPC$TL_stats_broadcastStats.views_by_source_graph, LocaleController.getString("ViewsBySourceChartTitle", NUM), 2);
            chartViewDataArr[6] = createViewData(tLRPC$TL_stats_broadcastStats.new_followers_by_source_graph, LocaleController.getString("NewFollowersBySourceChartTitle", NUM), 2);
            chartViewDataArr[7] = createViewData(tLRPC$TL_stats_broadcastStats.languages_graph, LocaleController.getString("LanguagesChartTitle", NUM), 4);
            this.recentPostsAll.clear();
            for (int i = 0; i < tLRPC$TL_stats_broadcastStats.recent_message_interactions.size(); i++) {
                RecentPostInfo recentPostInfo = new RecentPostInfo();
                recentPostInfo.counters = tLRPC$TL_stats_broadcastStats.recent_message_interactions.get(i);
                this.recentPostsAll.add(recentPostInfo);
                this.recentPostIdtoIndexMap.put(recentPostInfo.counters.msg_id, i);
            }
            if (this.recentPostsAll.size() > 0) {
                int i2 = this.recentPostsAll.get(0).counters.msg_id;
                getMessagesStorage().getMessages((long) (-this.chat.id), this.recentPostsAll.size(), i2, 0, 0, this.classGuid, 0, true, false, 0);
            }
            AndroidUtilities.runOnUIThread(new Runnable(chartViewDataArr) {
                private final /* synthetic */ StatisticActivity.ChartViewData[] f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    StatisticActivity.this.lambda$null$0$StatisticActivity(this.f$1);
                }
            });
        }
    }

    public /* synthetic */ void lambda$null$0$StatisticActivity(ChartViewData[] chartViewDataArr) {
        this.growthData = chartViewDataArr[0];
        this.followersData = chartViewDataArr[1];
        this.topHoursData = chartViewDataArr[2];
        this.interactionsData = chartViewDataArr[3];
        this.ivInteractionsData = chartViewDataArr[4];
        this.viewsBySourceData = chartViewDataArr[5];
        this.newFollowersBySourceData = chartViewDataArr[6];
        this.languagesData = chartViewDataArr[7];
        Adapter adapter2 = this.adapter;
        if (adapter2 != null) {
            adapter2.update();
            this.adapter.notifyDataSetChanged();
        }
        this.initialLoading = false;
        LinearLayout linearLayout = this.progressLayout;
        if (linearLayout != null && linearLayout.getVisibility() == 0) {
            AndroidUtilities.cancelRunOnUIThread(this.showProgressbar);
            this.progressLayout.animate().alpha(0.0f).setDuration(230).setListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    StatisticActivity.this.progressLayout.setVisibility(8);
                }
            });
            this.recyclerListView.setVisibility(0);
            this.recyclerListView.setAlpha(0.0f);
            this.recyclerListView.animate().alpha(1.0f).setDuration(230).start();
            for (ChartViewData chartViewData : chartViewDataArr) {
                if (!(chartViewData == null || chartViewData.chartData != null || chartViewData.token == null)) {
                    chartViewData.load(this.currentAccount, this.classGuid, this.chat.stats_dc, this.recyclerListView, this.adapter, this.diffUtilsCallback);
                }
            }
        }
    }

    public void onFragmentDestroy() {
        getNotificationCenter().removeObserver(this, NotificationCenter.messagesDidLoad);
        super.onFragmentDestroy();
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.messagesDidLoad && objArr[10].intValue() == this.classGuid) {
            ArrayList arrayList = objArr[2];
            int size = arrayList.size();
            int i3 = 0;
            for (int i4 = 0; i4 < size; i4++) {
                MessageObject messageObject = (MessageObject) arrayList.get(i4);
                int i5 = this.recentPostIdtoIndexMap.get(messageObject.getId(), -1);
                if (i5 >= 0 && this.recentPostsAll.get(i5).counters.msg_id == messageObject.getId()) {
                    this.recentPostsAll.get(i5).message = messageObject;
                }
            }
            this.recentPostsLoaded.clear();
            int size2 = this.recentPostsAll.size();
            while (true) {
                if (i3 >= size2) {
                    break;
                }
                RecentPostInfo recentPostInfo = this.recentPostsAll.get(i3);
                if (recentPostInfo.message == null) {
                    this.loadFromId = recentPostInfo.counters.msg_id;
                    break;
                } else {
                    this.recentPostsLoaded.add(recentPostInfo);
                    i3++;
                }
            }
            if (this.recentPostsLoaded.size() < 20) {
                loadMessages();
            }
            Adapter adapter2 = this.adapter;
            if (adapter2 != null) {
                this.diffUtilsCallback.saveOldState(adapter2);
                this.adapter.update();
                DiffUtil.calculateDiff(this.diffUtilsCallback).dispatchUpdatesTo((RecyclerView.Adapter) this.adapter);
            }
        }
    }

    public View createView(Context context) {
        Context context2 = context;
        this.sharedUi = new BaseChartView.SharedUiComponents();
        FrameLayout frameLayout = new FrameLayout(context2);
        this.fragmentView = frameLayout;
        this.recyclerListView = new RecyclerListView(context2) {
            int lastH;

            /* access modifiers changed from: protected */
            public void onMeasure(int i, int i2) {
                super.onMeasure(i, i2);
                if (!(this.lastH == getMeasuredHeight() || StatisticActivity.this.adapter == null)) {
                    StatisticActivity.this.adapter.notifyDataSetChanged();
                }
                this.lastH = getMeasuredHeight();
            }
        };
        LinearLayout linearLayout = new LinearLayout(context2);
        this.progressLayout = linearLayout;
        linearLayout.setOrientation(1);
        RLottieImageView rLottieImageView = new RLottieImageView(context2);
        this.imageView = rLottieImageView;
        rLottieImageView.setAutoRepeat(true);
        this.imageView.setAnimation(NUM, 120, 120);
        this.imageView.playAnimation();
        TextView textView = new TextView(context2);
        textView.setTextSize(20.0f);
        textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        textView.setTextColor(Theme.getColor("player_actionBarTitle"));
        textView.setTag("player_actionBarTitle");
        textView.setText(LocaleController.getString("LoadingStats", NUM));
        textView.setGravity(1);
        TextView textView2 = new TextView(context2);
        textView2.setTextSize(15.0f);
        textView2.setTextColor(Theme.getColor("player_actionBarSubtitle"));
        textView2.setTag("player_actionBarSubtitle");
        textView2.setText(LocaleController.getString("LoadingStatsDescription", NUM));
        textView2.setGravity(1);
        this.progressLayout.addView(this.imageView, LayoutHelper.createLinear(120, 120, 1, 0, 0, 0, 20));
        this.progressLayout.addView(textView, LayoutHelper.createLinear(-2, -2, 1, 0, 0, 0, 10));
        this.progressLayout.addView(textView2, LayoutHelper.createLinear(-2, -2, 1));
        frameLayout.addView(this.progressLayout, LayoutHelper.createFrame(240, -2.0f, 17, 0.0f, 0.0f, 0.0f, 30.0f));
        RecyclerListView recyclerListView2 = this.recyclerListView;
        Adapter adapter2 = new Adapter();
        this.adapter = adapter2;
        recyclerListView2.setAdapter(adapter2);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context2);
        this.layoutManager = linearLayoutManager;
        this.recyclerListView.setLayoutManager(linearLayoutManager);
        DefaultItemAnimator defaultItemAnimator = new DefaultItemAnimator();
        defaultItemAnimator.setSupportsChangeAnimations(false);
        this.recyclerListView.setItemAnimator(defaultItemAnimator);
        this.recyclerListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                if (StatisticActivity.this.recentPostsAll.size() != StatisticActivity.this.recentPostsLoaded.size() && !StatisticActivity.this.messagesIsLoading && StatisticActivity.this.layoutManager.findLastVisibleItemPosition() > StatisticActivity.this.adapter.getItemCount() - 20) {
                    StatisticActivity.this.loadMessages();
                }
            }
        });
        this.recyclerListView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
            public final void onItemClick(View view, int i) {
                StatisticActivity.this.lambda$createView$2$StatisticActivity(view, i);
            }
        });
        frameLayout.addView(this.recyclerListView);
        ChatAvatarContainer chatAvatarContainer = new ChatAvatarContainer(context2, (ChatActivity) null, false);
        this.avatarContainer = chatAvatarContainer;
        this.actionBar.addView(chatAvatarContainer, 0, LayoutHelper.createFrame(-2, -1.0f, 51, !this.inPreviewMode ? 56.0f : 0.0f, 0.0f, 40.0f, 0.0f));
        TLRPC$Chat chat2 = getMessagesController().getChat(Integer.valueOf(this.chat.id));
        this.avatarContainer.setChatAvatar(chat2);
        this.avatarContainer.setTitle(chat2.title);
        this.avatarContainer.setSubtitle(LocaleController.getString("Statistics", NUM));
        this.actionBar.setBackButtonDrawable(new BackDrawable(false));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    StatisticActivity.this.finishFragment();
                }
            }
        });
        this.avatarContainer.setTitleColors(Theme.getColor("player_actionBarTitle"), Theme.getColor("player_actionBarSubtitle"));
        this.actionBar.setItemsColor(Theme.getColor("windowBackgroundWhiteGrayText2"), false);
        this.actionBar.setItemsBackgroundColor(Theme.getColor("actionBarActionModeDefaultSelector"), false);
        this.actionBar.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        if (this.initialLoading) {
            this.progressLayout.setAlpha(0.0f);
            AndroidUtilities.runOnUIThread(this.showProgressbar, 500);
            this.progressLayout.setVisibility(0);
            this.recyclerListView.setVisibility(8);
        } else {
            AndroidUtilities.cancelRunOnUIThread(this.showProgressbar);
            this.progressLayout.setVisibility(8);
            this.recyclerListView.setVisibility(0);
        }
        return this.fragmentView;
    }

    public /* synthetic */ void lambda$createView$2$StatisticActivity(View view, int i) {
        Adapter adapter2 = this.adapter;
        int i2 = adapter2.recentPostsStartRow;
        if (i >= i2 && i <= adapter2.recentPostsEndRow) {
            MessageObject messageObject = this.recentPostsLoaded.get(i - i2).message;
            Bundle bundle = new Bundle();
            bundle.putInt("chat_id", this.chat.id);
            bundle.putInt("message_id", messageObject.getId());
            bundle.putBoolean("need_remove_previous_same_chat_activity", false);
            presentFragment(new ChatActivity(bundle), false);
        }
    }

    private ChartViewData createViewData(TLRPC$StatsGraph tLRPC$StatsGraph, String str, int i) {
        if (tLRPC$StatsGraph == null || (tLRPC$StatsGraph instanceof TLRPC$TL_statsGraphError)) {
            return null;
        }
        ChartViewData chartViewData = new ChartViewData(str, i);
        if (tLRPC$StatsGraph instanceof TLRPC$TL_statsGraph) {
            try {
                ChartData createChartData = createChartData(new JSONObject(((TLRPC$TL_statsGraph) tLRPC$StatsGraph).json.data), i);
                chartViewData.chartData = createChartData;
                chartViewData.zoomToken = ((TLRPC$TL_statsGraph) tLRPC$StatsGraph).zoom_token;
                if (createChartData == null || createChartData.x == null || createChartData.x.length < 2) {
                    chartViewData.isEmpty = true;
                }
                if (i == 4 && chartViewData.chartData.x != null && chartViewData.chartData.x.length > 0) {
                    long j = chartViewData.chartData.x[chartViewData.chartData.x.length - 1];
                    chartViewData.childChartData = new StackLinearChartData(chartViewData.chartData, j);
                    chartViewData.activeZoom = j;
                }
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        } else if (tLRPC$StatsGraph instanceof TLRPC$TL_statsGraphAsync) {
            chartViewData.token = ((TLRPC$TL_statsGraphAsync) tLRPC$StatsGraph).token;
        }
        return chartViewData;
    }

    /* access modifiers changed from: private */
    public static ChartData createChartData(JSONObject jSONObject, int i) throws JSONException {
        if (i == 0) {
            return new ChartData(jSONObject);
        }
        if (i == 1) {
            return new DoubleLinearChartData(jSONObject);
        }
        if (i == 2) {
            return new StackBarChartData(jSONObject);
        }
        if (i == 4) {
            return new StackLinearChartData(jSONObject);
        }
        return null;
    }

    class Adapter extends RecyclerListView.SelectionAdapter {
        int count;
        int emptyCell = -1;
        int folowersCell = -1;
        int growCell = -1;
        int interactionsCell = -1;
        int ivInteractionsCell = -1;
        int languagesCell = -1;
        int newFollowersBySourceCell = -1;
        int progressCell = -1;
        int recentPostsEndRow = -1;
        int recentPostsHeaderCell = -1;
        int recentPostsStartRow = -1;
        ArraySet<Integer> shadowDivideCells = new ArraySet<>();
        int topHourseCell = -1;
        int viewsBySourceCell = -1;

        Adapter() {
        }

        public int getItemViewType(int i) {
            if (i == this.growCell || i == this.folowersCell || i == this.topHourseCell) {
                return 0;
            }
            if (i == this.interactionsCell || i == this.ivInteractionsCell) {
                return 1;
            }
            if (i == this.viewsBySourceCell || i == this.newFollowersBySourceCell) {
                return 2;
            }
            if (i == this.languagesCell) {
                return 4;
            }
            if (i >= this.recentPostsStartRow && i <= this.recentPostsEndRow) {
                return 9;
            }
            if (i == this.progressCell) {
                return 11;
            }
            if (i == this.emptyCell) {
                return 12;
            }
            return i == this.recentPostsHeaderCell ? 13 : 10;
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r10v2, resolved type: org.telegram.ui.Cells.ShadowSectionCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v4, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v6, resolved type: org.telegram.ui.Cells.EmptyCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v7, resolved type: org.telegram.ui.Cells.LoadingCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v8, resolved type: org.telegram.ui.Cells.StatisticPostInfoCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v10, resolved type: org.telegram.ui.StatisticActivity$ChartCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v3, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v11, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v12, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v13, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v14, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX WARNING: type inference failed for: r0v0, types: [android.view.View] */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public androidx.recyclerview.widget.RecyclerView.ViewHolder onCreateViewHolder(android.view.ViewGroup r9, int r10) {
            /*
                r8 = this;
                if (r10 < 0) goto L_0x0012
                r0 = 4
                if (r10 > r0) goto L_0x0012
                org.telegram.ui.StatisticActivity$ChartCell r0 = new org.telegram.ui.StatisticActivity$ChartCell
                org.telegram.ui.StatisticActivity r1 = org.telegram.ui.StatisticActivity.this
                android.content.Context r9 = r9.getContext()
                r0.<init>(r9, r10)
                goto L_0x009b
            L_0x0012:
                r0 = 9
                if (r10 != r0) goto L_0x0027
                org.telegram.ui.Cells.StatisticPostInfoCell r0 = new org.telegram.ui.Cells.StatisticPostInfoCell
                android.content.Context r9 = r9.getContext()
                org.telegram.ui.StatisticActivity r10 = org.telegram.ui.StatisticActivity.this
                org.telegram.tgnet.TLRPC$ChatFull r10 = r10.chat
                r0.<init>(r9, r10)
                goto L_0x009b
            L_0x0027:
                r0 = 11
                if (r10 != r0) goto L_0x003f
                org.telegram.ui.Cells.LoadingCell r0 = new org.telegram.ui.Cells.LoadingCell
                android.content.Context r9 = r9.getContext()
                r0.<init>(r9)
                java.lang.String r9 = "windowBackgroundWhite"
                int r9 = org.telegram.ui.ActionBar.Theme.getColor(r9)
                r0.setBackgroundColor(r9)
                goto L_0x009b
            L_0x003f:
                r0 = 12
                if (r10 != r0) goto L_0x0053
                org.telegram.ui.Cells.EmptyCell r0 = new org.telegram.ui.Cells.EmptyCell
                android.content.Context r9 = r9.getContext()
                r10 = 1097859072(0x41700000, float:15.0)
                int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
                r0.<init>(r9, r10)
                goto L_0x009b
            L_0x0053:
                r1 = 13
                if (r10 != r1) goto L_0x008a
                org.telegram.ui.Cells.HeaderCell r0 = new org.telegram.ui.Cells.HeaderCell
                android.content.Context r3 = r9.getContext()
                r5 = 16
                r6 = 15
                r7 = 0
                java.lang.String r4 = "dialogTextBlack"
                r2 = r0
                r2.<init>(r3, r4, r5, r6, r7)
                r9 = 2131626476(0x7f0e09ec, float:1.888019E38)
                java.lang.String r10 = "RecentPosts"
                java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r10, r9)
                r0.setText(r9)
                int r9 = r0.getPaddingLeft()
                int r10 = r0.getTop()
                int r1 = r0.getRight()
                r2 = 1090519040(0x41000000, float:8.0)
                int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
                r0.setPadding(r9, r10, r1, r2)
                goto L_0x009b
            L_0x008a:
                org.telegram.ui.Cells.ShadowSectionCell r10 = new org.telegram.ui.Cells.ShadowSectionCell
                android.content.Context r9 = r9.getContext()
                java.lang.String r1 = "windowBackgroundGray"
                int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
                r10.<init>(r9, r0, r1)
                r0 = r10
            L_0x009b:
                androidx.recyclerview.widget.RecyclerView$LayoutParams r9 = new androidx.recyclerview.widget.RecyclerView$LayoutParams
                r10 = -1
                r1 = -2
                r9.<init>((int) r10, (int) r1)
                r0.setLayoutParams(r9)
                org.telegram.ui.Components.RecyclerListView$Holder r9 = new org.telegram.ui.Components.RecyclerListView$Holder
                r9.<init>(r0)
                return r9
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.StatisticActivity.Adapter.onCreateViewHolder(android.view.ViewGroup, int):androidx.recyclerview.widget.RecyclerView$ViewHolder");
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            ChartViewData chartViewData;
            int itemViewType = getItemViewType(i);
            if (itemViewType >= 0 && itemViewType <= 4) {
                if (this.growCell == i) {
                    chartViewData = StatisticActivity.this.growthData;
                } else if (this.folowersCell == i) {
                    chartViewData = StatisticActivity.this.followersData;
                } else if (this.interactionsCell == i) {
                    chartViewData = StatisticActivity.this.interactionsData;
                } else if (this.viewsBySourceCell == i) {
                    chartViewData = StatisticActivity.this.viewsBySourceData;
                } else if (this.newFollowersBySourceCell == i) {
                    chartViewData = StatisticActivity.this.newFollowersBySourceData;
                } else if (this.ivInteractionsCell == i) {
                    chartViewData = StatisticActivity.this.ivInteractionsData;
                } else if (this.topHourseCell == i) {
                    chartViewData = StatisticActivity.this.topHoursData;
                } else {
                    chartViewData = StatisticActivity.this.languagesData;
                }
                ((ChartCell) viewHolder.itemView).updateData(chartViewData, false);
            } else if (itemViewType == 9) {
                ((StatisticPostInfoCell) viewHolder.itemView).setData((RecentPostInfo) StatisticActivity.this.recentPostsLoaded.get(i - this.recentPostsStartRow));
            }
        }

        public int getItemCount() {
            return this.count;
        }

        public void update() {
            this.growCell = -1;
            this.folowersCell = -1;
            this.interactionsCell = -1;
            this.viewsBySourceCell = -1;
            this.newFollowersBySourceCell = -1;
            this.languagesCell = -1;
            this.recentPostsStartRow = -1;
            this.recentPostsEndRow = -1;
            this.progressCell = -1;
            this.emptyCell = -1;
            this.recentPostsHeaderCell = -1;
            this.ivInteractionsCell = -1;
            this.topHourseCell = -1;
            this.count = 0;
            this.shadowDivideCells.clear();
            if (StatisticActivity.this.growthData != null && !StatisticActivity.this.growthData.isEmpty) {
                int i = this.count;
                this.count = i + 1;
                this.growCell = i;
            }
            if (StatisticActivity.this.followersData != null && !StatisticActivity.this.followersData.isEmpty) {
                int i2 = this.count;
                if (i2 > 0) {
                    ArraySet<Integer> arraySet = this.shadowDivideCells;
                    this.count = i2 + 1;
                    arraySet.add(Integer.valueOf(i2));
                }
                int i3 = this.count;
                this.count = i3 + 1;
                this.folowersCell = i3;
            }
            if (StatisticActivity.this.topHoursData != null && !StatisticActivity.this.topHoursData.isEmpty) {
                int i4 = this.count;
                if (i4 > 0) {
                    ArraySet<Integer> arraySet2 = this.shadowDivideCells;
                    this.count = i4 + 1;
                    arraySet2.add(Integer.valueOf(i4));
                }
                int i5 = this.count;
                this.count = i5 + 1;
                this.topHourseCell = i5;
            }
            if (StatisticActivity.this.interactionsData != null && !StatisticActivity.this.interactionsData.isEmpty) {
                int i6 = this.count;
                if (i6 > 0) {
                    ArraySet<Integer> arraySet3 = this.shadowDivideCells;
                    this.count = i6 + 1;
                    arraySet3.add(Integer.valueOf(i6));
                }
                int i7 = this.count;
                this.count = i7 + 1;
                this.interactionsCell = i7;
            }
            if ((StatisticActivity.this.ivInteractionsData != null && !StatisticActivity.this.ivInteractionsData.isEmpty && !StatisticActivity.this.ivInteractionsData.isError) || (StatisticActivity.this.ivInteractionsData != null && StatisticActivity.this.ivInteractionsData.viewShowed)) {
                int i8 = this.count;
                if (i8 > 0) {
                    ArraySet<Integer> arraySet4 = this.shadowDivideCells;
                    this.count = i8 + 1;
                    arraySet4.add(Integer.valueOf(i8));
                }
                int i9 = this.count;
                this.count = i9 + 1;
                this.ivInteractionsCell = i9;
            }
            if (StatisticActivity.this.viewsBySourceData != null && !StatisticActivity.this.viewsBySourceData.isEmpty) {
                int i10 = this.count;
                if (i10 > 0) {
                    ArraySet<Integer> arraySet5 = this.shadowDivideCells;
                    this.count = i10 + 1;
                    arraySet5.add(Integer.valueOf(i10));
                }
                int i11 = this.count;
                this.count = i11 + 1;
                this.viewsBySourceCell = i11;
            }
            if (StatisticActivity.this.newFollowersBySourceData != null && !StatisticActivity.this.newFollowersBySourceData.isEmpty) {
                int i12 = this.count;
                if (i12 > 0) {
                    ArraySet<Integer> arraySet6 = this.shadowDivideCells;
                    this.count = i12 + 1;
                    arraySet6.add(Integer.valueOf(i12));
                }
                int i13 = this.count;
                this.count = i13 + 1;
                this.newFollowersBySourceCell = i13;
            }
            if (StatisticActivity.this.languagesData != null && !StatisticActivity.this.languagesData.isEmpty) {
                int i14 = this.count;
                if (i14 > 0) {
                    ArraySet<Integer> arraySet7 = this.shadowDivideCells;
                    this.count = i14 + 1;
                    arraySet7.add(Integer.valueOf(i14));
                }
                int i15 = this.count;
                this.count = i15 + 1;
                this.languagesCell = i15;
            }
            ArraySet<Integer> arraySet8 = this.shadowDivideCells;
            int i16 = this.count;
            this.count = i16 + 1;
            arraySet8.add(Integer.valueOf(i16));
            if (StatisticActivity.this.recentPostsAll.size() > 0) {
                int i17 = this.count;
                int i18 = i17 + 1;
                this.count = i18;
                this.recentPostsHeaderCell = i17;
                this.count = i18 + 1;
                this.recentPostsStartRow = i18;
                int size = (i18 + StatisticActivity.this.recentPostsLoaded.size()) - 1;
                this.recentPostsEndRow = size;
                this.count = size;
                this.count = size + 1;
                if (StatisticActivity.this.recentPostsLoaded.size() != StatisticActivity.this.recentPostsAll.size()) {
                    int i19 = this.count;
                    this.count = i19 + 1;
                    this.progressCell = i19;
                } else {
                    int i20 = this.count;
                    this.count = i20 + 1;
                    this.emptyCell = i20;
                }
                ArraySet<Integer> arraySet9 = this.shadowDivideCells;
                int i21 = this.count;
                this.count = i21 + 1;
                arraySet9.add(Integer.valueOf(i21));
            }
        }

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return viewHolder.getItemViewType() == 9;
        }
    }

    public class ChartCell extends FrameLayout {
        ChartHeaderView chartHeaderView;
        int chartType;
        BaseChartView chartView;
        ArrayList<CheckBoxHolder> checkBoxes = new ArrayList<>();
        ViewGroup checkboxContainer;
        ChartViewData data;
        TextView errorTextView;
        RadialProgressView progressView;
        BaseChartView zoomedChartView;

        @SuppressLint({"ClickableViewAccessibility"})
        public ChartCell(Context context, int i) {
            super(context);
            setWillNotDraw(false);
            this.chartType = i;
            LinearLayout linearLayout = new LinearLayout(context);
            linearLayout.setOrientation(1);
            this.checkboxContainer = new FrameLayout(this, context, StatisticActivity.this) {
                /* access modifiers changed from: protected */
                public void onMeasure(int i, int i2) {
                    super.onMeasure(i, i2);
                    int childCount = getChildCount();
                    int measuredHeight = childCount > 0 ? getChildAt(0).getMeasuredHeight() : 0;
                    int i3 = 0;
                    int i4 = 0;
                    for (int i5 = 0; i5 < childCount; i5++) {
                        if (getChildAt(i5).getMeasuredWidth() + i4 > getMeasuredWidth()) {
                            i3 += getChildAt(i5).getMeasuredHeight();
                            i4 = 0;
                        }
                        i4 += getChildAt(i5).getMeasuredWidth();
                    }
                    setMeasuredDimension(getMeasuredWidth(), measuredHeight + i3 + AndroidUtilities.dp(16.0f));
                }

                /* access modifiers changed from: protected */
                public void onLayout(boolean z, int i, int i2, int i3, int i4) {
                    int childCount = getChildCount();
                    int i5 = 0;
                    int i6 = 0;
                    for (int i7 = 0; i7 < childCount; i7++) {
                        if (getChildAt(i7).getMeasuredWidth() + i5 > getMeasuredWidth()) {
                            i6 += getChildAt(i7).getMeasuredHeight();
                            i5 = 0;
                        }
                        getChildAt(i7).layout(i5, i6, getChildAt(i7).getMeasuredWidth() + i5, getChildAt(i7).getMeasuredHeight() + i6);
                        i5 += getChildAt(i7).getMeasuredWidth();
                    }
                }
            };
            ChartHeaderView chartHeaderView2 = new ChartHeaderView(getContext());
            this.chartHeaderView = chartHeaderView2;
            chartHeaderView2.back.setOnTouchListener(new RecyclerListView.FoucsableOnTouchListener());
            this.chartHeaderView.back.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    StatisticActivity.ChartCell.this.lambda$new$0$StatisticActivity$ChartCell(view);
                }
            });
            if (i == 1) {
                this.chartView = new DoubleLinearChartView(getContext());
                DoubleLinearChartView doubleLinearChartView = new DoubleLinearChartView(getContext());
                this.zoomedChartView = doubleLinearChartView;
                doubleLinearChartView.legendSignatureView.useHour = true;
            } else if (i == 2) {
                this.chartView = new StackBarChartView(getContext());
                StackBarChartView stackBarChartView = new StackBarChartView(getContext());
                this.zoomedChartView = stackBarChartView;
                stackBarChartView.legendSignatureView.useHour = true;
            } else if (i == 3) {
                this.chartView = new BarChartView(getContext());
                LinearChartView linearChartView = new LinearChartView(getContext());
                this.zoomedChartView = linearChartView;
                linearChartView.legendSignatureView.useHour = true;
            } else if (i != 4) {
                this.chartView = new LinearChartView(getContext());
                LinearChartView linearChartView2 = new LinearChartView(getContext());
                this.zoomedChartView = linearChartView2;
                linearChartView2.legendSignatureView.useHour = true;
            } else {
                StackLinearChartView stackLinearChartView = new StackLinearChartView(getContext());
                this.chartView = stackLinearChartView;
                stackLinearChartView.legendSignatureView.showPercentage = true;
                this.zoomedChartView = new PieChartView(getContext());
            }
            FrameLayout frameLayout = new FrameLayout(context);
            this.chartView.sharedUiComponents = StatisticActivity.this.sharedUi;
            this.zoomedChartView.sharedUiComponents = StatisticActivity.this.sharedUi;
            this.progressView = new RadialProgressView(context);
            frameLayout.addView(this.chartView);
            frameLayout.addView(this.chartView.legendSignatureView, -2, -2);
            frameLayout.addView(this.zoomedChartView);
            frameLayout.addView(this.zoomedChartView.legendSignatureView, -2, -2);
            frameLayout.addView(this.progressView, LayoutHelper.createFrame(44, 44.0f, 17, 0.0f, 0.0f, 0.0f, 60.0f));
            TextView textView = new TextView(context);
            this.errorTextView = textView;
            textView.setTextSize(15.0f);
            frameLayout.addView(this.errorTextView, LayoutHelper.createFrame(-2, -2.0f, 17, 0.0f, 0.0f, 0.0f, 30.0f));
            this.progressView.setVisibility(8);
            this.errorTextView.setTextColor(Theme.getColor("dialogTextGray4"));
            this.chartView.setDateSelectionListener(new BaseChartView.DateSelectionListener() {
                public final void onDateSelected(long j) {
                    StatisticActivity.ChartCell.this.lambda$new$1$StatisticActivity$ChartCell(j);
                }
            });
            this.chartView.legendSignatureView.showProgress(false, false);
            this.chartView.legendSignatureView.setOnTouchListener(new RecyclerListView.FoucsableOnTouchListener());
            this.chartView.legendSignatureView.setOnClickListener(new View.OnClickListener(context) {
                private final /* synthetic */ Context f$1;

                {
                    this.f$1 = r2;
                }

                public final void onClick(View view) {
                    StatisticActivity.ChartCell.this.lambda$new$4$StatisticActivity$ChartCell(this.f$1, view);
                }
            });
            this.zoomedChartView.legendSignatureView.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    StatisticActivity.ChartCell.this.lambda$new$5$StatisticActivity$ChartCell(view);
                }
            });
            this.chartView.setVisibility(0);
            this.zoomedChartView.setVisibility(4);
            this.chartView.setHeader(this.chartHeaderView);
            linearLayout.addView(this.chartHeaderView, LayoutHelper.createFrame(-1, 52.0f));
            linearLayout.addView(frameLayout, LayoutHelper.createFrame(-1, -2.0f));
            linearLayout.addView(this.checkboxContainer, LayoutHelper.createFrame(-1, -2.0f, 0, 16.0f, 0.0f, 16.0f, 0.0f));
            if (this.chartType == 4) {
                frameLayout.setClipChildren(false);
                frameLayout.setClipToPadding(false);
                linearLayout.setClipChildren(false);
                linearLayout.setClipToPadding(false);
            }
            addView(linearLayout);
        }

        public /* synthetic */ void lambda$new$0$StatisticActivity$ChartCell(View view) {
            zoomOut(true);
        }

        public /* synthetic */ void lambda$new$1$StatisticActivity$ChartCell(long j) {
            StatisticActivity.this.cancelZoom();
            this.chartView.legendSignatureView.showProgress(false, false);
        }

        public /* synthetic */ void lambda$new$4$StatisticActivity$ChartCell(Context context, View view) {
            if (this.data.activeZoom <= 0) {
                performClick();
                BaseChartView baseChartView = this.chartView;
                if (baseChartView.legendSignatureView.canGoZoom) {
                    long selectedDate = baseChartView.getSelectedDate();
                    if (this.chartType == 4) {
                        ChartViewData chartViewData = this.data;
                        chartViewData.childChartData = new StackLinearChartData(chartViewData.chartData, selectedDate);
                        zoomChart(false);
                    } else if (this.data.zoomToken != null) {
                        StatisticActivity.this.cancelZoom();
                        String str = this.data.zoomToken + "_" + selectedDate;
                        ChartData chartData = (ChartData) StatisticActivity.this.childDataCache.get(str);
                        if (chartData != null) {
                            this.data.childChartData = chartData;
                            zoomChart(false);
                            return;
                        }
                        TLRPC$TL_stats_loadAsyncGraph tLRPC$TL_stats_loadAsyncGraph = new TLRPC$TL_stats_loadAsyncGraph();
                        tLRPC$TL_stats_loadAsyncGraph.token = this.data.zoomToken;
                        if (selectedDate != 0) {
                            tLRPC$TL_stats_loadAsyncGraph.x = selectedDate;
                            tLRPC$TL_stats_loadAsyncGraph.flags |= 1;
                        }
                        StatisticActivity statisticActivity = StatisticActivity.this;
                        ZoomCancelable zoomCancelable = new ZoomCancelable();
                        ZoomCancelable unused = statisticActivity.lastCancelable = zoomCancelable;
                        zoomCancelable.adapterPosition = StatisticActivity.this.recyclerListView.getChildAdapterPosition(this);
                        this.chartView.legendSignatureView.showProgress(true, false);
                        ConnectionsManager.getInstance(StatisticActivity.this.currentAccount).bindRequestToGuid(ConnectionsManager.getInstance(StatisticActivity.this.currentAccount).sendRequest(tLRPC$TL_stats_loadAsyncGraph, new RequestDelegate(context, str, zoomCancelable) {
                            private final /* synthetic */ Context f$1;
                            private final /* synthetic */ String f$2;
                            private final /* synthetic */ StatisticActivity.ZoomCancelable f$3;

                            {
                                this.f$1 = r2;
                                this.f$2 = r3;
                                this.f$3 = r4;
                            }

                            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                                StatisticActivity.ChartCell.this.lambda$null$3$StatisticActivity$ChartCell(this.f$1, this.f$2, this.f$3, tLObject, tLRPC$TL_error);
                            }
                        }, (QuickAckDelegate) null, (WriteToSocketDelegate) null, 0, StatisticActivity.this.chat.stats_dc, 1, true), StatisticActivity.this.classGuid);
                    }
                }
            }
        }

        public /* synthetic */ void lambda$null$3$StatisticActivity$ChartCell(Context context, String str, ZoomCancelable zoomCancelable, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
            ChartData chartData = null;
            if (tLObject instanceof TLRPC$TL_statsGraph) {
                try {
                    chartData = StatisticActivity.createChartData(new JSONObject(((TLRPC$TL_statsGraph) tLObject).json.data), this.data.graphType);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (tLObject instanceof TLRPC$TL_statsGraphError) {
                Toast.makeText(context, ((TLRPC$TL_statsGraphError) tLObject).error, 1).show();
            }
            AndroidUtilities.runOnUIThread(new Runnable(chartData, str, zoomCancelable) {
                private final /* synthetic */ ChartData f$1;
                private final /* synthetic */ String f$2;
                private final /* synthetic */ StatisticActivity.ZoomCancelable f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void run() {
                    StatisticActivity.ChartCell.this.lambda$null$2$StatisticActivity$ChartCell(this.f$1, this.f$2, this.f$3);
                }
            });
        }

        public /* synthetic */ void lambda$null$2$StatisticActivity$ChartCell(ChartData chartData, String str, ZoomCancelable zoomCancelable) {
            if (chartData != null) {
                StatisticActivity.this.childDataCache.put(str, chartData);
            }
            if (chartData != null && !zoomCancelable.canceled && zoomCancelable.adapterPosition >= 0) {
                View findViewByPosition = StatisticActivity.this.layoutManager.findViewByPosition(zoomCancelable.adapterPosition);
                if (findViewByPosition instanceof ChartCell) {
                    this.data.childChartData = chartData;
                    ChartCell chartCell = (ChartCell) findViewByPosition;
                    chartCell.chartView.legendSignatureView.showProgress(false, false);
                    chartCell.zoomChart(false);
                }
            }
            StatisticActivity.this.cancelZoom();
        }

        public /* synthetic */ void lambda$new$5$StatisticActivity$ChartCell(View view) {
            this.zoomedChartView.animateLegend(false);
        }

        private void zoomChart(boolean z) {
            boolean z2;
            long selectedDate = this.chartView.getSelectedDate();
            ChartData chartData = this.data.childChartData;
            if (!z || this.zoomedChartView.getVisibility() != 0) {
                this.zoomedChartView.updatePicker(chartData, selectedDate);
            }
            this.zoomedChartView.setData(chartData);
            if (this.data.chartData.lines.size() > 1) {
                int i = 0;
                for (int i2 = 0; i2 < this.data.chartData.lines.size(); i2++) {
                    int i3 = 0;
                    while (true) {
                        if (i3 >= chartData.lines.size()) {
                            z2 = false;
                            break;
                        } else if (chartData.lines.get(i3).id.equals(this.data.chartData.lines.get(i2).id)) {
                            boolean z3 = this.checkBoxes.get(i2).checkBox.checked;
                            ((LineViewData) this.zoomedChartView.lines.get(i3)).enabled = z3;
                            ((LineViewData) this.zoomedChartView.lines.get(i3)).alpha = z3 ? 1.0f : 0.0f;
                            this.checkBoxes.get(i2).checkBox.enabled = true;
                            this.checkBoxes.get(i2).checkBox.animate().alpha(1.0f).start();
                            if (z3) {
                                i++;
                            }
                            z2 = true;
                        } else {
                            i3++;
                        }
                    }
                    if (!z2) {
                        this.checkBoxes.get(i2).checkBox.enabled = false;
                        this.checkBoxes.get(i2).checkBox.animate().alpha(0.0f).start();
                    }
                }
                if (i == 0) {
                    for (int i4 = 0; i4 < this.data.chartData.lines.size(); i4++) {
                        this.checkBoxes.get(i4).checkBox.enabled = true;
                        this.checkBoxes.get(i4).checkBox.animate().alpha(1.0f).start();
                    }
                    return;
                }
            }
            this.data.activeZoom = selectedDate;
            this.chartView.legendSignatureView.setAlpha(0.0f);
            BaseChartView baseChartView = this.chartView;
            baseChartView.selectionA = 0.0f;
            baseChartView.legendShowing = false;
            baseChartView.animateLegentTo = false;
            this.zoomedChartView.updateColors();
            if (!z) {
                this.zoomedChartView.clearSelection();
                this.chartHeaderView.zoomTo(this.zoomedChartView, selectedDate, true);
            }
            this.zoomedChartView.setHeader(this.chartHeaderView);
            this.chartView.setHeader((ChartHeaderView) null);
            if (z) {
                this.chartView.setVisibility(4);
                this.zoomedChartView.setVisibility(0);
                BaseChartView baseChartView2 = this.chartView;
                baseChartView2.transitionMode = 0;
                BaseChartView baseChartView3 = this.zoomedChartView;
                baseChartView3.transitionMode = 0;
                baseChartView2.enabled = false;
                baseChartView3.enabled = true;
                this.chartHeaderView.zoomTo(baseChartView3, selectedDate, false);
                return;
            }
            ValueAnimator createTransitionAnimator = createTransitionAnimator(selectedDate, true);
            createTransitionAnimator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    ChartCell.this.chartView.setVisibility(4);
                    ChartCell chartCell = ChartCell.this;
                    BaseChartView baseChartView = chartCell.chartView;
                    baseChartView.enabled = false;
                    BaseChartView baseChartView2 = chartCell.zoomedChartView;
                    baseChartView2.enabled = true;
                    baseChartView.transitionMode = 0;
                    baseChartView2.transitionMode = 0;
                    ((Activity) chartCell.getContext()).getWindow().clearFlags(16);
                }
            });
            createTransitionAnimator.start();
        }

        private void zoomOut(boolean z) {
            this.chartHeaderView.zoomOut(this.chartView, z);
            this.chartView.legendSignatureView.chevron.setAlpha(1.0f);
            this.zoomedChartView.setHeader((ChartHeaderView) null);
            long selectedDate = this.chartView.getSelectedDate();
            this.data.activeZoom = 0;
            this.chartView.setVisibility(0);
            this.zoomedChartView.clearSelection();
            this.zoomedChartView.setHeader((ChartHeaderView) null);
            this.chartView.setHeader(this.chartHeaderView);
            if (!z) {
                this.zoomedChartView.setVisibility(4);
                BaseChartView baseChartView = this.chartView;
                baseChartView.enabled = true;
                this.zoomedChartView.enabled = false;
                baseChartView.invalidate();
                ((Activity) getContext()).getWindow().clearFlags(16);
                Iterator<CheckBoxHolder> it = this.checkBoxes.iterator();
                while (it.hasNext()) {
                    CheckBoxHolder next = it.next();
                    next.checkBox.setAlpha(1.0f);
                    next.checkBox.enabled = true;
                }
                return;
            }
            ValueAnimator createTransitionAnimator = createTransitionAnimator(selectedDate, false);
            createTransitionAnimator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    ChartCell.this.zoomedChartView.setVisibility(4);
                    ChartCell chartCell = ChartCell.this;
                    BaseChartView baseChartView = chartCell.chartView;
                    baseChartView.transitionMode = 0;
                    BaseChartView baseChartView2 = chartCell.zoomedChartView;
                    baseChartView2.transitionMode = 0;
                    baseChartView.enabled = true;
                    baseChartView2.enabled = false;
                    baseChartView.legendShowing = true;
                    baseChartView.moveLegend();
                    ChartCell.this.chartView.animateLegend(true);
                    ChartCell.this.chartView.invalidate();
                    ((Activity) ChartCell.this.getContext()).getWindow().clearFlags(16);
                }
            });
            Iterator<CheckBoxHolder> it2 = this.checkBoxes.iterator();
            while (it2.hasNext()) {
                CheckBoxHolder next2 = it2.next();
                next2.checkBox.animate().alpha(1.0f).start();
                next2.checkBox.enabled = true;
            }
            createTransitionAnimator.start();
        }

        private ValueAnimator createTransitionAnimator(long j, boolean z) {
            ((Activity) getContext()).getWindow().setFlags(16, 16);
            BaseChartView baseChartView = this.chartView;
            baseChartView.enabled = false;
            BaseChartView baseChartView2 = this.zoomedChartView;
            baseChartView2.enabled = false;
            baseChartView.transitionMode = 2;
            baseChartView2.transitionMode = 1;
            TransitionParams transitionParams = new TransitionParams();
            ChartPickerDelegate chartPickerDelegate = this.chartView.pickerDelegate;
            transitionParams.pickerEndOut = chartPickerDelegate.pickerEnd;
            transitionParams.pickerStartOut = chartPickerDelegate.pickerStart;
            int binarySearch = Arrays.binarySearch(this.data.chartData.x, j);
            transitionParams.xPercentage = this.data.chartData.xPercentage[binarySearch];
            this.zoomedChartView.setVisibility(0);
            this.zoomedChartView.transitionParams = transitionParams;
            this.chartView.transitionParams = transitionParams;
            int i = Integer.MAX_VALUE;
            int i2 = 0;
            for (int i3 = 0; i3 < this.data.chartData.lines.size(); i3++) {
                if (this.data.chartData.lines.get(i3).y[binarySearch] > i2) {
                    i2 = this.data.chartData.lines.get(i3).y[binarySearch];
                }
                if (this.data.chartData.lines.get(i3).y[binarySearch] < i) {
                    i = this.data.chartData.lines.get(i3).y[binarySearch];
                }
            }
            float f = ((float) i) + ((float) (i2 - i));
            BaseChartView baseChartView3 = this.chartView;
            float f2 = baseChartView3.currentMinHeight;
            float f3 = (f - f2) / (baseChartView3.currentMaxHeight - f2);
            float[] fArr = new float[2];
            float f4 = 0.0f;
            fArr[0] = z ? 0.0f : 1.0f;
            if (z) {
                f4 = 1.0f;
            }
            fArr[1] = f4;
            ValueAnimator ofFloat = ValueAnimator.ofFloat(fArr);
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(transitionParams, f3) {
                private final /* synthetic */ TransitionParams f$1;
                private final /* synthetic */ float f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    StatisticActivity.ChartCell.this.lambda$createTransitionAnimator$6$StatisticActivity$ChartCell(this.f$1, this.f$2, valueAnimator);
                }
            });
            ofFloat.setDuration(this.chartType == 4 ? 600 : 400);
            ofFloat.setInterpolator(new FastOutSlowInInterpolator());
            return ofFloat;
        }

        public /* synthetic */ void lambda$createTransitionAnimator$6$StatisticActivity$ChartCell(TransitionParams transitionParams, float f, ValueAnimator valueAnimator) {
            BaseChartView baseChartView = this.chartView;
            ChartPickerDelegate chartPickerDelegate = baseChartView.pickerDelegate;
            float f2 = chartPickerDelegate.pickerEnd;
            float f3 = chartPickerDelegate.pickerStart;
            float f4 = ((((float) baseChartView.chartWidth) / (f2 - f3)) * f3) - ((float) BaseChartView.HORIZONTAL_PADDING);
            Rect rect = baseChartView.chartArea;
            transitionParams.pY = ((float) rect.top) + ((1.0f - f) * ((float) rect.height()));
            transitionParams.pX = (this.chartView.chartFullWidth * transitionParams.xPercentage) - f4;
            transitionParams.progress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            this.zoomedChartView.invalidate();
            this.chartView.invalidate();
        }

        public void updateData(ChartViewData chartViewData, boolean z) {
            if (chartViewData != null) {
                this.chartHeaderView.setTitle(chartViewData.title);
                boolean z2 = getContext().getResources().getConfiguration().orientation == 2;
                this.chartView.setLandscape(z2);
                chartViewData.viewShowed = true;
                this.zoomedChartView.setLandscape(z2);
                this.data = chartViewData;
                if (chartViewData.isEmpty || chartViewData.isError) {
                    this.progressView.setVisibility(8);
                    String str = chartViewData.errorMessage;
                    if (str != null) {
                        this.errorTextView.setText(str);
                        if (this.errorTextView.getVisibility() == 8) {
                            this.errorTextView.setAlpha(0.0f);
                            this.errorTextView.animate().alpha(1.0f);
                        }
                        this.errorTextView.setVisibility(0);
                    }
                    this.chartView.setData(null);
                    return;
                }
                this.errorTextView.setVisibility(8);
                this.chartView.legendSignatureView.isTopHourChart = chartViewData == StatisticActivity.this.topHoursData;
                this.chartHeaderView.showDate(chartViewData != StatisticActivity.this.topHoursData);
                if (chartViewData.chartData != null || chartViewData.token == null) {
                    if (!z) {
                        this.progressView.setVisibility(8);
                    }
                    this.chartView.setData(chartViewData.chartData);
                    this.chartView.legendSignatureView.zoomEnabled = this.data.zoomToken != null || this.chartType == 4;
                    this.zoomedChartView.legendSignatureView.zoomEnabled = false;
                    LegendSignatureView legendSignatureView = this.chartView.legendSignatureView;
                    legendSignatureView.setEnabled(legendSignatureView.zoomEnabled);
                    LegendSignatureView legendSignatureView2 = this.zoomedChartView.legendSignatureView;
                    legendSignatureView2.setEnabled(legendSignatureView2.zoomEnabled);
                    int size = this.chartView.lines.size();
                    this.checkboxContainer.removeAllViews();
                    this.checkBoxes.clear();
                    if (size > 1) {
                        for (int i = 0; i < size; i++) {
                            new CheckBoxHolder(i).setData((LineViewData) this.chartView.lines.get(i));
                        }
                    }
                    long j = this.data.activeZoom;
                    if (j > 0) {
                        this.chartView.selectDate(j);
                        zoomChart(true);
                    } else {
                        zoomOut(false);
                        this.chartView.invalidate();
                    }
                    recolor();
                    if (z) {
                        this.chartView.transitionMode = 3;
                        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
                        this.chartView.transitionParams = new TransitionParams();
                        this.chartView.transitionParams.progress = 0.0f;
                        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                                StatisticActivity.ChartCell.this.lambda$updateData$7$StatisticActivity$ChartCell(valueAnimator);
                            }
                        });
                        ofFloat.addListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animator) {
                                ChartCell chartCell = ChartCell.this;
                                chartCell.chartView.transitionMode = 0;
                                chartCell.progressView.setVisibility(8);
                            }
                        });
                        ofFloat.start();
                        return;
                    }
                    return;
                }
                this.progressView.setAlpha(1.0f);
                this.progressView.setVisibility(0);
                chartViewData.load(StatisticActivity.this.currentAccount, StatisticActivity.this.classGuid, StatisticActivity.this.chat.stats_dc, StatisticActivity.this.recyclerListView, StatisticActivity.this.adapter, StatisticActivity.this.diffUtilsCallback);
            }
        }

        public /* synthetic */ void lambda$updateData$7$StatisticActivity$ChartCell(ValueAnimator valueAnimator) {
            float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            this.progressView.setAlpha(1.0f - floatValue);
            this.chartView.transitionParams.progress = floatValue;
            this.zoomedChartView.invalidate();
            this.chartView.invalidate();
        }

        public void recolor() {
            ChartData chartData;
            ArrayList<ChartData.Line> arrayList;
            int i;
            this.chartView.updateColors();
            this.chartView.invalidate();
            this.zoomedChartView.updateColors();
            this.zoomedChartView.invalidate();
            this.chartHeaderView.recolor();
            this.chartHeaderView.invalidate();
            ChartViewData chartViewData = this.data;
            if (!(chartViewData == null || (chartData = chartViewData.chartData) == null || (arrayList = chartData.lines) == null || arrayList.size() <= 1)) {
                for (int i2 = 0; i2 < this.data.chartData.lines.size(); i2++) {
                    if (this.data.chartData.lines.get(i2).colorKey == null || !Theme.hasThemeKey(this.data.chartData.lines.get(i2).colorKey)) {
                        if (ColorUtils.calculateLuminance(Theme.getColor("windowBackgroundWhite")) < 0.5d) {
                            i = this.data.chartData.lines.get(i2).colorDark;
                        } else {
                            i = this.data.chartData.lines.get(i2).color;
                        }
                    } else {
                        i = Theme.getColor(this.data.chartData.lines.get(i2).colorKey);
                    }
                    if (i2 < this.checkBoxes.size()) {
                        this.checkBoxes.get(i2).recolor(i);
                    }
                }
            }
            this.progressView.setProgressColor(Theme.getColor("progressCircle"));
            this.errorTextView.setTextColor(Theme.getColor("dialogTextGray4"));
        }

        class CheckBoxHolder {
            final FlatCheckBox checkBox;
            LineViewData line;
            final int position;

            CheckBoxHolder(int i) {
                this.position = i;
                FlatCheckBox flatCheckBox = new FlatCheckBox(ChartCell.this.getContext());
                this.checkBox = flatCheckBox;
                flatCheckBox.setPadding(AndroidUtilities.dp(16.0f), 0, AndroidUtilities.dp(16.0f), 0);
                ChartCell.this.checkboxContainer.addView(this.checkBox);
                ChartCell.this.checkBoxes.add(this);
            }

            public void setData(LineViewData lineViewData) {
                this.line = lineViewData;
                this.checkBox.setText(lineViewData.line.name);
                this.checkBox.setChecked(lineViewData.enabled, false);
                this.checkBox.setOnTouchListener(new RecyclerListView.FoucsableOnTouchListener());
                this.checkBox.setOnClickListener(
                /*  JADX ERROR: Method code generation error
                    jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x0024: INVOKE  
                      (wrap: org.telegram.ui.Components.FlatCheckBox : 0x001d: IGET  (r0v3 org.telegram.ui.Components.FlatCheckBox) = 
                      (r3v0 'this' org.telegram.ui.StatisticActivity$ChartCell$CheckBoxHolder A[THIS])
                     org.telegram.ui.StatisticActivity.ChartCell.CheckBoxHolder.checkBox org.telegram.ui.Components.FlatCheckBox)
                      (wrap: org.telegram.ui.-$$Lambda$StatisticActivity$ChartCell$CheckBoxHolder$-ATCv5uDSIXDA3hgLHcbxjcI0ls : 0x0021: CONSTRUCTOR  (r1v4 org.telegram.ui.-$$Lambda$StatisticActivity$ChartCell$CheckBoxHolder$-ATCv5uDSIXDA3hgLHcbxjcI0ls) = 
                      (r3v0 'this' org.telegram.ui.StatisticActivity$ChartCell$CheckBoxHolder A[THIS])
                      (r4v0 'lineViewData' org.telegram.ui.Charts.view_data.LineViewData)
                     call: org.telegram.ui.-$$Lambda$StatisticActivity$ChartCell$CheckBoxHolder$-ATCv5uDSIXDA3hgLHcbxjcI0ls.<init>(org.telegram.ui.StatisticActivity$ChartCell$CheckBoxHolder, org.telegram.ui.Charts.view_data.LineViewData):void type: CONSTRUCTOR)
                     android.view.View.setOnClickListener(android.view.View$OnClickListener):void type: VIRTUAL in method: org.telegram.ui.StatisticActivity.ChartCell.CheckBoxHolder.setData(org.telegram.ui.Charts.view_data.LineViewData):void, dex: classes.dex
                    	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
                    	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
                    	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
                    	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
                    	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                    	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                    	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:211)
                    	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:204)
                    	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:318)
                    	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
                    	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
                    	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                    	at java.util.ArrayList.forEach(ArrayList.java:1257)
                    	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                    	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                    	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                    	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                    	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                    	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                    	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                    	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                    	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                    	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                    	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
                    	at jadx.core.codegen.ClassGen.addInnerClass(ClassGen.java:249)
                    	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:238)
                    	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                    	at java.util.ArrayList.forEach(ArrayList.java:1257)
                    	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                    	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                    	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                    	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                    	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                    	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                    	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                    	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                    	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                    	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                    	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
                    	at jadx.core.codegen.ClassGen.addInnerClass(ClassGen.java:249)
                    	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:238)
                    	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
                    	at java.util.ArrayList.forEach(ArrayList.java:1257)
                    	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:395)
                    	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                    	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:483)
                    	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
                    	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
                    	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
                    	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                    	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:485)
                    	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                    	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                    	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
                    	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:78)
                    	at jadx.core.codegen.CodeGen.wrapCodeGen(CodeGen.java:44)
                    	at jadx.core.codegen.CodeGen.generateJavaCode(CodeGen.java:33)
                    	at jadx.core.codegen.CodeGen.generate(CodeGen.java:21)
                    	at jadx.core.ProcessClass.generateCode(ProcessClass.java:61)
                    	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:273)
                    Caused by: jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x0021: CONSTRUCTOR  (r1v4 org.telegram.ui.-$$Lambda$StatisticActivity$ChartCell$CheckBoxHolder$-ATCv5uDSIXDA3hgLHcbxjcI0ls) = 
                      (r3v0 'this' org.telegram.ui.StatisticActivity$ChartCell$CheckBoxHolder A[THIS])
                      (r4v0 'lineViewData' org.telegram.ui.Charts.view_data.LineViewData)
                     call: org.telegram.ui.-$$Lambda$StatisticActivity$ChartCell$CheckBoxHolder$-ATCv5uDSIXDA3hgLHcbxjcI0ls.<init>(org.telegram.ui.StatisticActivity$ChartCell$CheckBoxHolder, org.telegram.ui.Charts.view_data.LineViewData):void type: CONSTRUCTOR in method: org.telegram.ui.StatisticActivity.ChartCell.CheckBoxHolder.setData(org.telegram.ui.Charts.view_data.LineViewData):void, dex: classes.dex
                    	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
                    	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                    	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                    	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:787)
                    	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:728)
                    	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
                    	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
                    	... 59 more
                    Caused by: jadx.core.utils.exceptions.JadxRuntimeException: Expected class to be processed at this point, class: org.telegram.ui.-$$Lambda$StatisticActivity$ChartCell$CheckBoxHolder$-ATCv5uDSIXDA3hgLHcbxjcI0ls, state: NOT_LOADED
                    	at jadx.core.dex.nodes.ClassNode.ensureProcessed(ClassNode.java:260)
                    	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:606)
                    	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
                    	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                    	... 65 more
                    */
                /*
                    this = this;
                    r3.line = r4
                    org.telegram.ui.Components.FlatCheckBox r0 = r3.checkBox
                    org.telegram.ui.Charts.data.ChartData$Line r1 = r4.line
                    java.lang.String r1 = r1.name
                    r0.setText(r1)
                    org.telegram.ui.Components.FlatCheckBox r0 = r3.checkBox
                    boolean r1 = r4.enabled
                    r2 = 0
                    r0.setChecked(r1, r2)
                    org.telegram.ui.Components.FlatCheckBox r0 = r3.checkBox
                    org.telegram.ui.Components.RecyclerListView$FoucsableOnTouchListener r1 = new org.telegram.ui.Components.RecyclerListView$FoucsableOnTouchListener
                    r1.<init>()
                    r0.setOnTouchListener(r1)
                    org.telegram.ui.Components.FlatCheckBox r0 = r3.checkBox
                    org.telegram.ui.-$$Lambda$StatisticActivity$ChartCell$CheckBoxHolder$-ATCv5uDSIXDA3hgLHcbxjcI0ls r1 = new org.telegram.ui.-$$Lambda$StatisticActivity$ChartCell$CheckBoxHolder$-ATCv5uDSIXDA3hgLHcbxjcI0ls
                    r1.<init>(r3, r4)
                    r0.setOnClickListener(r1)
                    org.telegram.ui.Components.FlatCheckBox r0 = r3.checkBox
                    org.telegram.ui.-$$Lambda$StatisticActivity$ChartCell$CheckBoxHolder$9a4wKcCG_gnE9Q0wXx1at-wt9R4 r1 = new org.telegram.ui.-$$Lambda$StatisticActivity$ChartCell$CheckBoxHolder$9a4wKcCG_gnE9Q0wXx1at-wt9R4
                    r1.<init>(r3, r4)
                    r0.setOnLongClickListener(r1)
                    return
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.StatisticActivity.ChartCell.CheckBoxHolder.setData(org.telegram.ui.Charts.view_data.LineViewData):void");
            }

            public /* synthetic */ void lambda$setData$0$StatisticActivity$ChartCell$CheckBoxHolder(LineViewData lineViewData, View view) {
                if (this.checkBox.enabled) {
                    int size = ChartCell.this.checkBoxes.size();
                    boolean z = false;
                    int i = 0;
                    while (true) {
                        if (i < size) {
                            if (i != this.position && ChartCell.this.checkBoxes.get(i).checkBox.enabled && ChartCell.this.checkBoxes.get(i).checkBox.checked) {
                                break;
                            }
                            i++;
                        } else {
                            z = true;
                            break;
                        }
                    }
                    StatisticActivity.this.cancelZoom();
                    if (z) {
                        this.checkBox.denied();
                        return;
                    }
                    FlatCheckBox flatCheckBox = this.checkBox;
                    flatCheckBox.setChecked(!flatCheckBox.checked);
                    lineViewData.enabled = this.checkBox.checked;
                    ChartCell.this.chartView.onCheckChanged();
                    ChartCell chartCell = ChartCell.this;
                    if (chartCell.data.activeZoom > 0 && this.position < chartCell.zoomedChartView.lines.size()) {
                        ((LineViewData) ChartCell.this.zoomedChartView.lines.get(this.position)).enabled = this.checkBox.checked;
                        ChartCell.this.zoomedChartView.onCheckChanged();
                    }
                }
            }

            public /* synthetic */ boolean lambda$setData$1$StatisticActivity$ChartCell$CheckBoxHolder(LineViewData lineViewData, View view) {
                if (!this.checkBox.enabled) {
                    return false;
                }
                StatisticActivity.this.cancelZoom();
                int size = ChartCell.this.checkBoxes.size();
                for (int i = 0; i < size; i++) {
                    ChartCell.this.checkBoxes.get(i).checkBox.setChecked(false);
                    ChartCell.this.checkBoxes.get(i).line.enabled = false;
                    ChartCell chartCell = ChartCell.this;
                    if (chartCell.data.activeZoom > 0 && i < chartCell.zoomedChartView.lines.size()) {
                        ((LineViewData) ChartCell.this.zoomedChartView.lines.get(i)).enabled = false;
                    }
                }
                this.checkBox.setChecked(true);
                lineViewData.enabled = true;
                ChartCell.this.chartView.onCheckChanged();
                ChartCell chartCell2 = ChartCell.this;
                if (chartCell2.data.activeZoom > 0) {
                    ((LineViewData) chartCell2.zoomedChartView.lines.get(this.position)).enabled = true;
                    ChartCell.this.zoomedChartView.onCheckChanged();
                }
                return true;
            }

            public void recolor(int i) {
                this.checkBox.recolor(i);
            }
        }
    }

    /* access modifiers changed from: private */
    public void cancelZoom() {
        ZoomCancelable zoomCancelable = this.lastCancelable;
        if (zoomCancelable != null) {
            zoomCancelable.canceled = true;
        }
        int childCount = this.recyclerListView.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = this.recyclerListView.getChildAt(i);
            if (childAt instanceof ChartCell) {
                ((ChartCell) childAt).chartView.legendSignatureView.showProgress(false, true);
            }
        }
    }

    private static class ZoomCancelable {
        int adapterPosition;
        boolean canceled;

        private ZoomCancelable() {
        }
    }

    private static class ChartViewData {
        public long activeZoom;
        ChartData chartData;
        ChartData childChartData;
        public String errorMessage;
        final int graphType;
        boolean isEmpty;
        public boolean isError;
        boolean loading;
        final String title;
        String token;
        public boolean viewShowed;
        String zoomToken;

        public ChartViewData(String str, int i) {
            this.title = str;
            this.graphType = i;
        }

        public void load(int i, int i2, int i3, RecyclerListView recyclerListView, Adapter adapter, DiffUtilsCallback diffUtilsCallback) {
            if (!this.loading) {
                this.loading = true;
                TLRPC$TL_stats_loadAsyncGraph tLRPC$TL_stats_loadAsyncGraph = new TLRPC$TL_stats_loadAsyncGraph();
                tLRPC$TL_stats_loadAsyncGraph.token = this.token;
                RecyclerListView recyclerListView2 = recyclerListView;
                int sendRequest = ConnectionsManager.getInstance(i).sendRequest(tLRPC$TL_stats_loadAsyncGraph, new RequestDelegate(recyclerListView, diffUtilsCallback, adapter) {
                    private final /* synthetic */ RecyclerListView f$1;
                    private final /* synthetic */ StatisticActivity.DiffUtilsCallback f$2;
                    private final /* synthetic */ StatisticActivity.Adapter f$3;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                        this.f$3 = r4;
                    }

                    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                        StatisticActivity.ChartViewData.this.lambda$load$1$StatisticActivity$ChartViewData(this.f$1, this.f$2, this.f$3, tLObject, tLRPC$TL_error);
                    }
                }, (QuickAckDelegate) null, (WriteToSocketDelegate) null, 0, i3, 1, true);
                int i4 = i2;
                ConnectionsManager.getInstance(i).bindRequestToGuid(sendRequest, i2);
            }
        }

        /* JADX WARNING: Removed duplicated region for block: B:25:0x0052  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public /* synthetic */ void lambda$load$1$StatisticActivity$ChartViewData(org.telegram.ui.Components.RecyclerListView r10, org.telegram.ui.StatisticActivity.DiffUtilsCallback r11, org.telegram.ui.StatisticActivity.Adapter r12, org.telegram.tgnet.TLObject r13, org.telegram.tgnet.TLRPC$TL_error r14) {
            /*
                r9 = this;
                r0 = 0
                if (r14 != 0) goto L_0x0060
                boolean r14 = r13 instanceof org.telegram.tgnet.TLRPC$TL_statsGraph
                r1 = 1
                if (r14 == 0) goto L_0x004d
                r14 = r13
                org.telegram.tgnet.TLRPC$TL_statsGraph r14 = (org.telegram.tgnet.TLRPC$TL_statsGraph) r14
                org.telegram.tgnet.TLRPC$TL_dataJSON r14 = r14.json
                java.lang.String r14 = r14.data
                org.json.JSONObject r2 = new org.json.JSONObject     // Catch:{ JSONException -> 0x0047 }
                r2.<init>(r14)     // Catch:{ JSONException -> 0x0047 }
                int r14 = r9.graphType     // Catch:{ JSONException -> 0x0047 }
                org.telegram.ui.Charts.data.ChartData r14 = org.telegram.ui.StatisticActivity.createChartData(r2, r14)     // Catch:{ JSONException -> 0x0047 }
                r2 = r13
                org.telegram.tgnet.TLRPC$TL_statsGraph r2 = (org.telegram.tgnet.TLRPC$TL_statsGraph) r2     // Catch:{ JSONException -> 0x0042 }
                java.lang.String r0 = r2.zoom_token     // Catch:{ JSONException -> 0x0042 }
                int r2 = r9.graphType     // Catch:{ JSONException -> 0x0042 }
                r3 = 4
                if (r2 != r3) goto L_0x003e
                long[] r2 = r14.x     // Catch:{ JSONException -> 0x0042 }
                if (r2 == 0) goto L_0x003e
                long[] r2 = r14.x     // Catch:{ JSONException -> 0x0042 }
                int r2 = r2.length     // Catch:{ JSONException -> 0x0042 }
                if (r2 <= 0) goto L_0x003e
                long[] r2 = r14.x     // Catch:{ JSONException -> 0x0042 }
                long[] r3 = r14.x     // Catch:{ JSONException -> 0x0042 }
                int r3 = r3.length     // Catch:{ JSONException -> 0x0042 }
                int r3 = r3 - r1
                r3 = r2[r3]     // Catch:{ JSONException -> 0x0042 }
                org.telegram.ui.Charts.data.StackLinearChartData r2 = new org.telegram.ui.Charts.data.StackLinearChartData     // Catch:{ JSONException -> 0x0042 }
                r2.<init>(r14, r3)     // Catch:{ JSONException -> 0x0042 }
                r9.childChartData = r2     // Catch:{ JSONException -> 0x0042 }
                r9.activeZoom = r3     // Catch:{ JSONException -> 0x0042 }
            L_0x003e:
                r8 = r0
                r0 = r14
                r14 = r8
                goto L_0x004e
            L_0x0042:
                r2 = move-exception
                r8 = r0
                r0 = r14
                r14 = r8
                goto L_0x0049
            L_0x0047:
                r2 = move-exception
                r14 = r0
            L_0x0049:
                r2.printStackTrace()
                goto L_0x004e
            L_0x004d:
                r14 = r0
            L_0x004e:
                boolean r2 = r13 instanceof org.telegram.tgnet.TLRPC$TL_statsGraphError
                if (r2 == 0) goto L_0x005d
                r2 = 0
                r9.isEmpty = r2
                r9.isError = r1
                org.telegram.tgnet.TLRPC$TL_statsGraphError r13 = (org.telegram.tgnet.TLRPC$TL_statsGraphError) r13
                java.lang.String r13 = r13.error
                r9.errorMessage = r13
            L_0x005d:
                r4 = r14
                r3 = r0
                goto L_0x0062
            L_0x0060:
                r3 = r0
                r4 = r3
            L_0x0062:
                org.telegram.ui.-$$Lambda$StatisticActivity$ChartViewData$sprgknZtXiqkLi_8TMZCUET0gCQ r13 = new org.telegram.ui.-$$Lambda$StatisticActivity$ChartViewData$sprgknZtXiqkLi_8TMZCUET0gCQ
                r1 = r13
                r2 = r9
                r5 = r10
                r6 = r11
                r7 = r12
                r1.<init>(r3, r4, r5, r6, r7)
                org.telegram.messenger.AndroidUtilities.runOnUIThread(r13)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.StatisticActivity.ChartViewData.lambda$load$1$StatisticActivity$ChartViewData(org.telegram.ui.Components.RecyclerListView, org.telegram.ui.StatisticActivity$DiffUtilsCallback, org.telegram.ui.StatisticActivity$Adapter, org.telegram.tgnet.TLObject, org.telegram.tgnet.TLRPC$TL_error):void");
        }

        public /* synthetic */ void lambda$null$0$StatisticActivity$ChartViewData(ChartData chartData2, String str, RecyclerListView recyclerListView, DiffUtilsCallback diffUtilsCallback, Adapter adapter) {
            boolean z = false;
            this.loading = false;
            this.chartData = chartData2;
            this.zoomToken = str;
            int childCount = recyclerListView.getChildCount();
            int i = 0;
            while (true) {
                if (i >= childCount) {
                    break;
                }
                View childAt = recyclerListView.getChildAt(i);
                if (childAt instanceof ChartCell) {
                    ChartCell chartCell = (ChartCell) childAt;
                    if (chartCell.data == this) {
                        chartCell.updateData(this, true);
                        z = true;
                        break;
                    }
                }
                i++;
            }
            if (!z) {
                diffUtilsCallback.saveOldState(adapter);
                adapter.update();
                DiffUtil.calculateDiff(diffUtilsCallback).dispatchUpdatesTo((RecyclerView.Adapter) adapter);
            }
        }
    }

    /* access modifiers changed from: private */
    public void loadMessages() {
        TLRPC$TL_channels_getMessages tLRPC$TL_channels_getMessages = new TLRPC$TL_channels_getMessages();
        tLRPC$TL_channels_getMessages.id = new ArrayList<>();
        int size = this.recentPostsAll.size();
        int i = 0;
        for (int i2 = this.recentPostIdtoIndexMap.get(this.loadFromId); i2 < size; i2++) {
            if (this.recentPostsAll.get(i2).message == null) {
                tLRPC$TL_channels_getMessages.id.add(Integer.valueOf(this.recentPostsAll.get(i2).counters.msg_id));
                i++;
                if (i > 50) {
                    break;
                }
            }
        }
        tLRPC$TL_channels_getMessages.channel = MessagesController.getInstance(this.currentAccount).getInputChannel(this.chat.id);
        this.messagesIsLoading = true;
        getConnectionsManager().sendRequest(tLRPC$TL_channels_getMessages, new RequestDelegate() {
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                StatisticActivity.this.lambda$loadMessages$4$StatisticActivity(tLObject, tLRPC$TL_error);
            }
        });
    }

    public /* synthetic */ void lambda$loadMessages$4$StatisticActivity(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        ArrayList arrayList = new ArrayList();
        if (tLObject instanceof TLRPC$messages_Messages) {
            ArrayList<TLRPC$Message> arrayList2 = ((TLRPC$messages_Messages) tLObject).messages;
            for (int i = 0; i < arrayList2.size(); i++) {
                arrayList.add(new MessageObject(this.currentAccount, arrayList2.get(i), false));
            }
            getMessagesStorage().putMessages(arrayList2, false, true, true, 0, false);
        }
        AndroidUtilities.runOnUIThread(new Runnable(arrayList) {
            private final /* synthetic */ ArrayList f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                StatisticActivity.this.lambda$null$3$StatisticActivity(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$null$3$StatisticActivity(ArrayList arrayList) {
        int i = 0;
        this.messagesIsLoading = false;
        if (!arrayList.isEmpty()) {
            int size = arrayList.size();
            for (int i2 = 0; i2 < size; i2++) {
                MessageObject messageObject = (MessageObject) arrayList.get(i2);
                int i3 = this.recentPostIdtoIndexMap.get(messageObject.getId(), -1);
                if (i3 >= 0 && this.recentPostsAll.get(i3).counters.msg_id == messageObject.getId()) {
                    this.recentPostsAll.get(i3).message = messageObject;
                }
            }
            this.recentPostsLoaded.clear();
            int size2 = this.recentPostsAll.size();
            while (true) {
                if (i >= size2) {
                    break;
                }
                RecentPostInfo recentPostInfo = this.recentPostsAll.get(i);
                if (recentPostInfo.message == null) {
                    this.loadFromId = recentPostInfo.counters.msg_id;
                    break;
                } else {
                    this.recentPostsLoaded.add(recentPostInfo);
                    i++;
                }
            }
            this.diffUtilsCallback.saveOldState(this.adapter);
            this.adapter.update();
            DiffUtil.calculateDiff(this.diffUtilsCallback).dispatchUpdatesTo((RecyclerView.Adapter) this.adapter);
        }
    }

    private void recolorRecyclerItem(View view) {
        if (view instanceof ChartCell) {
            ((ChartCell) view).recolor();
        }
        if (view instanceof ShadowSectionCell) {
            CombinedDrawable combinedDrawable = new CombinedDrawable(new ColorDrawable(Theme.getColor("windowBackgroundGray")), Theme.getThemedDrawable(ApplicationLoader.applicationContext, NUM, "windowBackgroundGrayShadow"), 0, 0);
            combinedDrawable.setFullsize(true);
            view.setBackground(combinedDrawable);
        }
    }

    private static class DiffUtilsCallback extends DiffUtil.Callback {
        Adapter adapter;
        int count;
        int folowersCell;
        int growCell;
        int interactionsCell;
        int ivInteractionsCell;
        int languagesCell;
        int newFollowersBySourceCell;
        SparseIntArray positionToTypeMap;
        int topHourseCell;
        int viewsBySourceCell;

        private DiffUtilsCallback() {
            this.positionToTypeMap = new SparseIntArray();
            this.growCell = -1;
            this.folowersCell = -1;
            this.interactionsCell = -1;
            this.ivInteractionsCell = -1;
            this.viewsBySourceCell = -1;
            this.newFollowersBySourceCell = -1;
            this.languagesCell = -1;
            this.topHourseCell = -1;
        }

        public void saveOldState(Adapter adapter2) {
            this.adapter = adapter2;
            this.positionToTypeMap.clear();
            this.count = adapter2.getItemCount();
            for (int i = 0; i < this.count; i++) {
                this.positionToTypeMap.put(i, adapter2.getItemViewType(i));
            }
            this.growCell = adapter2.growCell;
            this.folowersCell = adapter2.folowersCell;
            this.interactionsCell = adapter2.interactionsCell;
            this.ivInteractionsCell = adapter2.ivInteractionsCell;
            this.viewsBySourceCell = adapter2.viewsBySourceCell;
            this.newFollowersBySourceCell = adapter2.newFollowersBySourceCell;
            this.languagesCell = adapter2.languagesCell;
            this.topHourseCell = adapter2.topHourseCell;
        }

        public int getOldListSize() {
            return this.count;
        }

        public int getNewListSize() {
            return this.adapter.count;
        }

        public boolean areItemsTheSame(int i, int i2) {
            return this.positionToTypeMap.get(i) == this.adapter.getItemViewType(i2);
        }

        public boolean areContentsTheSame(int i, int i2) {
            if (i == this.growCell && i2 == this.adapter.growCell) {
                return true;
            }
            if (i == this.folowersCell && i2 == this.adapter.folowersCell) {
                return true;
            }
            if (i == this.interactionsCell && i2 == this.adapter.interactionsCell) {
                return true;
            }
            if (i == this.ivInteractionsCell && i2 == this.adapter.ivInteractionsCell) {
                return true;
            }
            if (i == this.viewsBySourceCell && i2 == this.adapter.viewsBySourceCell) {
                return true;
            }
            if (i == this.newFollowersBySourceCell && i2 == this.adapter.newFollowersBySourceCell) {
                return true;
            }
            if (i == this.languagesCell && i2 == this.adapter.languagesCell) {
                return true;
            }
            if (i == this.topHourseCell && i2 == this.adapter.topHourseCell) {
                return true;
            }
            return false;
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        ChartViewData chartViewData;
        ChartData chartData;
        $$Lambda$StatisticActivity$CqWRJOatjWarjR1aKS5KtRf4UcY r10 = new ThemeDescription.ThemeDescriptionDelegate() {
            public final void didSetColor() {
                StatisticActivity.this.lambda$getThemeDescriptions$5$StatisticActivity();
            }
        };
        ArrayList arrayList = new ArrayList();
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription((View) this.recyclerListView, 0, new Class[]{StatisticPostInfoCell.class}, new String[]{"message"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogTextBlack"));
        arrayList.add(new ThemeDescription((View) this.recyclerListView, 0, new Class[]{StatisticPostInfoCell.class}, new String[]{"views"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogTextBlack"));
        arrayList.add(new ThemeDescription((View) this.recyclerListView, 0, new Class[]{StatisticPostInfoCell.class}, new String[]{"shares"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogTextGray4"));
        arrayList.add(new ThemeDescription((View) this.recyclerListView, 0, new Class[]{StatisticPostInfoCell.class}, new String[]{"date"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogTextGray4"));
        arrayList.add(new ThemeDescription((View) this.recyclerListView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogTextBlack"));
        $$Lambda$StatisticActivity$CqWRJOatjWarjR1aKS5KtRf4UcY r8 = r10;
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (String[]) null, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) r8, "dialogTextBlack"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (String[]) null, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) r8, "statisticChartSignature"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (String[]) null, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) r8, "statisticChartHintLine"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (String[]) null, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) r8, "statisticChartActiveLine"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (String[]) null, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) r8, "statisticChartInactivePickerChart"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (String[]) null, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) r8, "statisticChartActivePickerChart"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (String[]) null, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) r8, "dialogBackground"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (String[]) null, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) r8, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (String[]) null, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) r8, "windowBackgroundWhiteGrayText2"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (String[]) null, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) r8, "actionBarActionModeDefaultSelector"));
        $$Lambda$StatisticActivity$CqWRJOatjWarjR1aKS5KtRf4UcY r7 = r10;
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r7, "windowBackgroundGray"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r7, "windowBackgroundGrayShadow"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        ChatAvatarContainer chatAvatarContainer = this.avatarContainer;
        SimpleTextView simpleTextView = null;
        arrayList.add(new ThemeDescription(chatAvatarContainer != null ? chatAvatarContainer.getTitleTextView() : null, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "player_actionBarTitle"));
        ChatAvatarContainer chatAvatarContainer2 = this.avatarContainer;
        if (chatAvatarContainer2 != null) {
            simpleTextView = chatAvatarContainer2.getSubtitleTextView();
        }
        arrayList.add(new ThemeDescription((View) simpleTextView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, (Class[]) null, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "player_actionBarSubtitle", (Object) null));
        for (int i = 0; i < 7; i++) {
            if (i == 0) {
                chartViewData = this.growthData;
            } else if (i == 1) {
                chartViewData = this.followersData;
            } else if (i == 2) {
                chartViewData = this.interactionsData;
            } else if (i == 3) {
                chartViewData = this.ivInteractionsData;
            } else if (i == 4) {
                chartViewData = this.viewsBySourceData;
            } else if (i == 5) {
                chartViewData = this.newFollowersBySourceData;
            } else {
                chartViewData = this.languagesData;
            }
            if (!(chartViewData == null || (chartData = chartViewData.chartData) == null)) {
                Iterator<ChartData.Line> it = chartData.lines.iterator();
                while (it.hasNext()) {
                    ChartData.Line next = it.next();
                    String str = next.colorKey;
                    if (str != null) {
                        if (!Theme.hasThemeKey(str)) {
                            Theme.setColor(next.colorKey, Theme.isCurrentThemeNight() ? next.colorDark : next.color, false);
                            Theme.setDefaultColor(next.colorKey, next.color);
                        }
                        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r10, next.colorKey));
                    }
                }
            }
        }
        return (ThemeDescription[]) arrayList.toArray(new ThemeDescription[0]);
    }

    public /* synthetic */ void lambda$getThemeDescriptions$5$StatisticActivity() {
        RecyclerListView recyclerListView2 = this.recyclerListView;
        if (recyclerListView2 != null) {
            int childCount = recyclerListView2.getChildCount();
            for (int i = 0; i < childCount; i++) {
                recolorRecyclerItem(this.recyclerListView.getChildAt(i));
            }
            int hiddenChildCount = this.recyclerListView.getHiddenChildCount();
            for (int i2 = 0; i2 < hiddenChildCount; i2++) {
                recolorRecyclerItem(this.recyclerListView.getHiddenChildAt(i2));
            }
            int cachedChildCount = this.recyclerListView.getCachedChildCount();
            for (int i3 = 0; i3 < cachedChildCount; i3++) {
                recolorRecyclerItem(this.recyclerListView.getCachedChildAt(i3));
            }
            int attachedScrapChildCount = this.recyclerListView.getAttachedScrapChildCount();
            for (int i4 = 0; i4 < attachedScrapChildCount; i4++) {
                recolorRecyclerItem(this.recyclerListView.getAttachedScrapChildAt(i4));
            }
            this.recyclerListView.getRecycledViewPool().clear();
        }
        BaseChartView.SharedUiComponents sharedUiComponents = this.sharedUi;
        if (sharedUiComponents != null) {
            sharedUiComponents.invalidate();
        }
    }
}
