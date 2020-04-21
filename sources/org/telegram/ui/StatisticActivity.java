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
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Locale;
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
import org.telegram.tgnet.TLRPC$TL_statsAbsValueAndPrev;
import org.telegram.tgnet.TLRPC$TL_statsGraph;
import org.telegram.tgnet.TLRPC$TL_statsGraphAsync;
import org.telegram.tgnet.TLRPC$TL_statsGraphError;
import org.telegram.tgnet.TLRPC$TL_statsPercentValue;
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
    /* access modifiers changed from: private */
    public DiffUtilsCallback diffUtilsCallback;
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
    public ChartViewData notificationsData;
    /* access modifiers changed from: private */
    public OverviewData overviewData;
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
        ChartViewData[] chartViewDataArr = new ChartViewData[9];
        if (tLObject instanceof TLRPC$TL_stats_broadcastStats) {
            TLRPC$TL_stats_broadcastStats tLRPC$TL_stats_broadcastStats = (TLRPC$TL_stats_broadcastStats) tLObject;
            chartViewDataArr[0] = createViewData(tLRPC$TL_stats_broadcastStats.iv_interactions_graph, LocaleController.getString("IVInteractionsChartTitle", NUM), 1);
            chartViewDataArr[1] = createViewData(tLRPC$TL_stats_broadcastStats.followers_graph, LocaleController.getString("FollowersChartTitle", NUM), 0);
            chartViewDataArr[2] = createViewData(tLRPC$TL_stats_broadcastStats.top_hours_graph, LocaleController.getString("TopHoursChartTitle", NUM), 0);
            chartViewDataArr[3] = createViewData(tLRPC$TL_stats_broadcastStats.interactions_graph, LocaleController.getString("InteractionsChartTitle", NUM), 1);
            chartViewDataArr[4] = createViewData(tLRPC$TL_stats_broadcastStats.growth_graph, LocaleController.getString("GrowthChartTitle", NUM), 0);
            chartViewDataArr[5] = createViewData(tLRPC$TL_stats_broadcastStats.views_by_source_graph, LocaleController.getString("ViewsBySourceChartTitle", NUM), 2);
            chartViewDataArr[6] = createViewData(tLRPC$TL_stats_broadcastStats.new_followers_by_source_graph, LocaleController.getString("NewFollowersBySourceChartTitle", NUM), 2);
            chartViewDataArr[7] = createViewData(tLRPC$TL_stats_broadcastStats.languages_graph, LocaleController.getString("LanguagesChartTitle", NUM), 4);
            chartViewDataArr[8] = createViewData(tLRPC$TL_stats_broadcastStats.mute_graph, LocaleController.getString("NotificationsChartTitle", NUM), 0);
            this.overviewData = new OverviewData(tLRPC$TL_stats_broadcastStats);
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
        this.ivInteractionsData = chartViewDataArr[0];
        this.followersData = chartViewDataArr[1];
        this.topHoursData = chartViewDataArr[2];
        this.interactionsData = chartViewDataArr[3];
        this.growthData = chartViewDataArr[4];
        this.viewsBySourceData = chartViewDataArr[5];
        this.newFollowersBySourceData = chartViewDataArr[6];
        this.languagesData = chartViewDataArr[7];
        this.notificationsData = chartViewDataArr[8];
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
            ArrayList arrayList2 = new ArrayList();
            int size = arrayList.size();
            int i3 = 0;
            for (int i4 = 0; i4 < size; i4++) {
                MessageObject messageObject = (MessageObject) arrayList.get(i4);
                int i5 = this.recentPostIdtoIndexMap.get(messageObject.getId(), -1);
                if (i5 >= 0 && this.recentPostsAll.get(i5).counters.msg_id == messageObject.getId()) {
                    if (messageObject.deleted) {
                        arrayList2.add(this.recentPostsAll.get(i5));
                    } else {
                        this.recentPostsAll.get(i5).message = messageObject;
                    }
                }
            }
            this.recentPostsAll.removeAll(arrayList2);
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
            if (this.adapter != null) {
                this.diffUtilsCallback.update();
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
        if (this.adapter == null) {
            this.adapter = new Adapter();
        }
        this.recyclerListView.setAdapter(this.adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context2);
        this.layoutManager = linearLayoutManager;
        this.recyclerListView.setLayoutManager(linearLayoutManager);
        this.recyclerListView.setItemAnimator((RecyclerView.ItemAnimator) null);
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
        this.diffUtilsCallback = new DiffUtilsCallback(this.adapter, this.layoutManager);
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
                if (i == 4 && chartViewData.chartData != null && chartViewData.chartData.x != null && chartViewData.chartData.x.length > 0) {
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
        int notificationsCell = -1;
        int overviewCell;
        int overviewHeaderCell = -1;
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
            if (i == this.growCell || i == this.folowersCell || i == this.topHourseCell || i == this.notificationsCell) {
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
            if (i == this.recentPostsHeaderCell || i == this.overviewHeaderCell) {
                return 13;
            }
            return i == this.overviewCell ? 14 : 10;
        }

        public long getItemId(int i) {
            if (i >= this.recentPostsStartRow && i <= this.recentPostsEndRow) {
                return (long) ((RecentPostInfo) StatisticActivity.this.recentPostsLoaded.get(i - this.recentPostsStartRow)).counters.msg_id;
            }
            if (i == this.growCell) {
                return 1;
            }
            if (i == this.folowersCell) {
                return 2;
            }
            if (i == this.topHourseCell) {
                return 3;
            }
            if (i == this.interactionsCell) {
                return 4;
            }
            if (i == this.notificationsCell) {
                return 5;
            }
            if (i == this.ivInteractionsCell) {
                return 6;
            }
            if (i == this.viewsBySourceCell) {
                return 7;
            }
            if (i == this.newFollowersBySourceCell) {
                return 8;
            }
            if (i == this.languagesCell) {
                return 9;
            }
            return super.getItemId(i);
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v2, resolved type: org.telegram.ui.Cells.ShadowSectionCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v4, resolved type: org.telegram.ui.StatisticActivity$Adapter$3} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v5, resolved type: org.telegram.ui.StatisticActivity$OverviewCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v7, resolved type: org.telegram.ui.Cells.EmptyCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v8, resolved type: org.telegram.ui.Cells.LoadingCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v9, resolved type: org.telegram.ui.StatisticActivity$Adapter$2} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v11, resolved type: org.telegram.ui.StatisticActivity$Adapter$1} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v12, resolved type: org.telegram.ui.StatisticActivity$Adapter$3} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v1, resolved type: org.telegram.ui.StatisticActivity$Adapter$3} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v13, resolved type: org.telegram.ui.StatisticActivity$Adapter$3} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v14, resolved type: org.telegram.ui.StatisticActivity$Adapter$3} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v15, resolved type: org.telegram.ui.StatisticActivity$Adapter$3} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v16, resolved type: org.telegram.ui.StatisticActivity$Adapter$3} */
        /* JADX WARNING: type inference failed for: r1v0, types: [android.view.View] */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public androidx.recyclerview.widget.RecyclerView.ViewHolder onCreateViewHolder(android.view.ViewGroup r11, int r12) {
            /*
                r10 = this;
                r0 = 0
                if (r12 < 0) goto L_0x0014
                r1 = 4
                if (r12 > r1) goto L_0x0014
                org.telegram.ui.StatisticActivity$Adapter$1 r1 = new org.telegram.ui.StatisticActivity$Adapter$1
                android.content.Context r11 = r11.getContext()
                r1.<init>(r10, r11, r12)
                r1.setWillNotDraw(r0)
                goto L_0x00a4
            L_0x0014:
                r1 = 9
                if (r12 != r1) goto L_0x002c
                org.telegram.ui.StatisticActivity$Adapter$2 r1 = new org.telegram.ui.StatisticActivity$Adapter$2
                android.content.Context r11 = r11.getContext()
                org.telegram.ui.StatisticActivity r12 = org.telegram.ui.StatisticActivity.this
                org.telegram.tgnet.TLRPC$ChatFull r12 = r12.chat
                r1.<init>(r10, r11, r12)
                r1.setWillNotDraw(r0)
                goto L_0x00a4
            L_0x002c:
                r1 = 11
                if (r12 != r1) goto L_0x0043
                org.telegram.ui.Cells.LoadingCell r1 = new org.telegram.ui.Cells.LoadingCell
                android.content.Context r11 = r11.getContext()
                r1.<init>(r11)
                java.lang.String r11 = "windowBackgroundWhite"
                int r11 = org.telegram.ui.ActionBar.Theme.getColor(r11)
                r1.setBackgroundColor(r11)
                goto L_0x00a4
            L_0x0043:
                r1 = 12
                if (r12 != r1) goto L_0x0057
                org.telegram.ui.Cells.EmptyCell r1 = new org.telegram.ui.Cells.EmptyCell
                android.content.Context r11 = r11.getContext()
                r12 = 1097859072(0x41700000, float:15.0)
                int r12 = org.telegram.messenger.AndroidUtilities.dp(r12)
                r1.<init>(r11, r12)
                goto L_0x00a4
            L_0x0057:
                r2 = 13
                if (r12 != r2) goto L_0x0086
                org.telegram.ui.StatisticActivity$Adapter$3 r1 = new org.telegram.ui.StatisticActivity$Adapter$3
                android.content.Context r5 = r11.getContext()
                r7 = 16
                r8 = 15
                r9 = 0
                java.lang.String r6 = "dialogTextBlack"
                r3 = r1
                r4 = r10
                r3.<init>(r4, r5, r6, r7, r8, r9)
                r1.setWillNotDraw(r0)
                int r11 = r1.getPaddingLeft()
                int r12 = r1.getTop()
                int r0 = r1.getRight()
                r2 = 1090519040(0x41000000, float:8.0)
                int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
                r1.setPadding(r11, r12, r0, r2)
                goto L_0x00a4
            L_0x0086:
                r0 = 14
                if (r12 != r0) goto L_0x0094
                org.telegram.ui.StatisticActivity$OverviewCell r1 = new org.telegram.ui.StatisticActivity$OverviewCell
                android.content.Context r11 = r11.getContext()
                r1.<init>(r11)
                goto L_0x00a4
            L_0x0094:
                org.telegram.ui.Cells.ShadowSectionCell r12 = new org.telegram.ui.Cells.ShadowSectionCell
                android.content.Context r11 = r11.getContext()
                java.lang.String r0 = "windowBackgroundGray"
                int r0 = org.telegram.ui.ActionBar.Theme.getColor(r0)
                r12.<init>(r11, r1, r0)
                r1 = r12
            L_0x00a4:
                androidx.recyclerview.widget.RecyclerView$LayoutParams r11 = new androidx.recyclerview.widget.RecyclerView$LayoutParams
                r12 = -1
                r0 = -2
                r11.<init>((int) r12, (int) r0)
                r1.setLayoutParams(r11)
                org.telegram.ui.Components.RecyclerListView$Holder r11 = new org.telegram.ui.Components.RecyclerListView$Holder
                r11.<init>(r1)
                return r11
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
                } else if (this.notificationsCell == i) {
                    chartViewData = StatisticActivity.this.notificationsData;
                } else {
                    chartViewData = StatisticActivity.this.languagesData;
                }
                ((ChartCell) viewHolder.itemView).updateData(chartViewData, false);
            } else if (itemViewType == 9) {
                ((StatisticPostInfoCell) viewHolder.itemView).setData((RecentPostInfo) StatisticActivity.this.recentPostsLoaded.get(i - this.recentPostsStartRow));
            } else if (itemViewType == 13) {
                HeaderCell headerCell = (HeaderCell) viewHolder.itemView;
                if (i == this.overviewHeaderCell) {
                    headerCell.setText(LocaleController.getString("StatisticOverview", NUM));
                } else {
                    headerCell.setText(LocaleController.getString("RecentPosts", NUM));
                }
            } else if (itemViewType == 14) {
                ((OverviewCell) viewHolder.itemView).setData(StatisticActivity.this.overviewData);
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
            this.notificationsCell = -1;
            this.count = 0;
            this.shadowDivideCells.clear();
            if (StatisticActivity.this.overviewData != null) {
                int i = this.count;
                int i2 = i + 1;
                this.count = i2;
                this.overviewHeaderCell = i;
                this.count = i2 + 1;
                this.overviewCell = i2;
            }
            if (StatisticActivity.this.growthData != null && !StatisticActivity.this.growthData.isEmpty) {
                int i3 = this.count;
                if (i3 > 0) {
                    ArraySet<Integer> arraySet = this.shadowDivideCells;
                    this.count = i3 + 1;
                    arraySet.add(Integer.valueOf(i3));
                }
                int i4 = this.count;
                this.count = i4 + 1;
                this.growCell = i4;
            }
            if (StatisticActivity.this.followersData != null && !StatisticActivity.this.followersData.isEmpty) {
                int i5 = this.count;
                if (i5 > 0) {
                    ArraySet<Integer> arraySet2 = this.shadowDivideCells;
                    this.count = i5 + 1;
                    arraySet2.add(Integer.valueOf(i5));
                }
                int i6 = this.count;
                this.count = i6 + 1;
                this.folowersCell = i6;
            }
            if (StatisticActivity.this.notificationsData != null && !StatisticActivity.this.notificationsData.isEmpty) {
                int i7 = this.count;
                if (i7 > 0) {
                    ArraySet<Integer> arraySet3 = this.shadowDivideCells;
                    this.count = i7 + 1;
                    arraySet3.add(Integer.valueOf(i7));
                }
                int i8 = this.count;
                this.count = i8 + 1;
                this.notificationsCell = i8;
            }
            if (StatisticActivity.this.topHoursData != null && !StatisticActivity.this.topHoursData.isEmpty) {
                int i9 = this.count;
                if (i9 > 0) {
                    ArraySet<Integer> arraySet4 = this.shadowDivideCells;
                    this.count = i9 + 1;
                    arraySet4.add(Integer.valueOf(i9));
                }
                int i10 = this.count;
                this.count = i10 + 1;
                this.topHourseCell = i10;
            }
            if (StatisticActivity.this.viewsBySourceData != null && !StatisticActivity.this.viewsBySourceData.isEmpty) {
                int i11 = this.count;
                if (i11 > 0) {
                    ArraySet<Integer> arraySet5 = this.shadowDivideCells;
                    this.count = i11 + 1;
                    arraySet5.add(Integer.valueOf(i11));
                }
                int i12 = this.count;
                this.count = i12 + 1;
                this.viewsBySourceCell = i12;
            }
            if (StatisticActivity.this.newFollowersBySourceData != null && !StatisticActivity.this.newFollowersBySourceData.isEmpty) {
                int i13 = this.count;
                if (i13 > 0) {
                    ArraySet<Integer> arraySet6 = this.shadowDivideCells;
                    this.count = i13 + 1;
                    arraySet6.add(Integer.valueOf(i13));
                }
                int i14 = this.count;
                this.count = i14 + 1;
                this.newFollowersBySourceCell = i14;
            }
            if (StatisticActivity.this.languagesData != null && !StatisticActivity.this.languagesData.isEmpty) {
                int i15 = this.count;
                if (i15 > 0) {
                    ArraySet<Integer> arraySet7 = this.shadowDivideCells;
                    this.count = i15 + 1;
                    arraySet7.add(Integer.valueOf(i15));
                }
                int i16 = this.count;
                this.count = i16 + 1;
                this.languagesCell = i16;
            }
            if (StatisticActivity.this.interactionsData != null && !StatisticActivity.this.interactionsData.isEmpty) {
                int i17 = this.count;
                if (i17 > 0) {
                    ArraySet<Integer> arraySet8 = this.shadowDivideCells;
                    this.count = i17 + 1;
                    arraySet8.add(Integer.valueOf(i17));
                }
                int i18 = this.count;
                this.count = i18 + 1;
                this.interactionsCell = i18;
            }
            if (StatisticActivity.this.ivInteractionsData != null && !StatisticActivity.this.ivInteractionsData.loading && !StatisticActivity.this.ivInteractionsData.isError) {
                int i19 = this.count;
                if (i19 > 0) {
                    ArraySet<Integer> arraySet9 = this.shadowDivideCells;
                    this.count = i19 + 1;
                    arraySet9.add(Integer.valueOf(i19));
                }
                int i20 = this.count;
                this.count = i20 + 1;
                this.ivInteractionsCell = i20;
            }
            ArraySet<Integer> arraySet10 = this.shadowDivideCells;
            int i21 = this.count;
            this.count = i21 + 1;
            arraySet10.add(Integer.valueOf(i21));
            if (StatisticActivity.this.recentPostsAll.size() > 0) {
                int i22 = this.count;
                int i23 = i22 + 1;
                this.count = i23;
                this.recentPostsHeaderCell = i22;
                this.count = i23 + 1;
                this.recentPostsStartRow = i23;
                int size = (i23 + StatisticActivity.this.recentPostsLoaded.size()) - 1;
                this.recentPostsEndRow = size;
                this.count = size;
                this.count = size + 1;
                if (StatisticActivity.this.recentPostsLoaded.size() != StatisticActivity.this.recentPostsAll.size()) {
                    int i24 = this.count;
                    this.count = i24 + 1;
                    this.progressCell = i24;
                } else {
                    int i25 = this.count;
                    this.count = i25 + 1;
                    this.emptyCell = i25;
                }
                ArraySet<Integer> arraySet11 = this.shadowDivideCells;
                int i26 = this.count;
                this.count = i26 + 1;
                arraySet11.add(Integer.valueOf(i26));
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
                ConnectionsManager.getInstance(i).bindRequestToGuid(ConnectionsManager.getInstance(i).sendRequest(tLRPC$TL_stats_loadAsyncGraph, new RequestDelegate(recyclerListView, diffUtilsCallback) {
                    private final /* synthetic */ RecyclerListView f$1;
                    private final /* synthetic */ StatisticActivity.DiffUtilsCallback f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                        StatisticActivity.ChartViewData.this.lambda$load$1$StatisticActivity$ChartViewData(this.f$1, this.f$2, tLObject, tLRPC$TL_error);
                    }
                }, (QuickAckDelegate) null, (WriteToSocketDelegate) null, 0, i3, 1, true), i2);
            }
        }

        /* JADX WARNING: Removed duplicated region for block: B:25:0x0052  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public /* synthetic */ void lambda$load$1$StatisticActivity$ChartViewData(org.telegram.ui.Components.RecyclerListView r9, org.telegram.ui.StatisticActivity.DiffUtilsCallback r10, org.telegram.tgnet.TLObject r11, org.telegram.tgnet.TLRPC$TL_error r12) {
            /*
                r8 = this;
                r0 = 0
                if (r12 != 0) goto L_0x0060
                boolean r12 = r11 instanceof org.telegram.tgnet.TLRPC$TL_statsGraph
                r1 = 1
                if (r12 == 0) goto L_0x004d
                r12 = r11
                org.telegram.tgnet.TLRPC$TL_statsGraph r12 = (org.telegram.tgnet.TLRPC$TL_statsGraph) r12
                org.telegram.tgnet.TLRPC$TL_dataJSON r12 = r12.json
                java.lang.String r12 = r12.data
                org.json.JSONObject r2 = new org.json.JSONObject     // Catch:{ JSONException -> 0x0047 }
                r2.<init>(r12)     // Catch:{ JSONException -> 0x0047 }
                int r12 = r8.graphType     // Catch:{ JSONException -> 0x0047 }
                org.telegram.ui.Charts.data.ChartData r12 = org.telegram.ui.StatisticActivity.createChartData(r2, r12)     // Catch:{ JSONException -> 0x0047 }
                r2 = r11
                org.telegram.tgnet.TLRPC$TL_statsGraph r2 = (org.telegram.tgnet.TLRPC$TL_statsGraph) r2     // Catch:{ JSONException -> 0x0042 }
                java.lang.String r0 = r2.zoom_token     // Catch:{ JSONException -> 0x0042 }
                int r2 = r8.graphType     // Catch:{ JSONException -> 0x0042 }
                r3 = 4
                if (r2 != r3) goto L_0x003e
                long[] r2 = r12.x     // Catch:{ JSONException -> 0x0042 }
                if (r2 == 0) goto L_0x003e
                long[] r2 = r12.x     // Catch:{ JSONException -> 0x0042 }
                int r2 = r2.length     // Catch:{ JSONException -> 0x0042 }
                if (r2 <= 0) goto L_0x003e
                long[] r2 = r12.x     // Catch:{ JSONException -> 0x0042 }
                long[] r3 = r12.x     // Catch:{ JSONException -> 0x0042 }
                int r3 = r3.length     // Catch:{ JSONException -> 0x0042 }
                int r3 = r3 - r1
                r3 = r2[r3]     // Catch:{ JSONException -> 0x0042 }
                org.telegram.ui.Charts.data.StackLinearChartData r2 = new org.telegram.ui.Charts.data.StackLinearChartData     // Catch:{ JSONException -> 0x0042 }
                r2.<init>(r12, r3)     // Catch:{ JSONException -> 0x0042 }
                r8.childChartData = r2     // Catch:{ JSONException -> 0x0042 }
                r8.activeZoom = r3     // Catch:{ JSONException -> 0x0042 }
            L_0x003e:
                r7 = r0
                r0 = r12
                r12 = r7
                goto L_0x004e
            L_0x0042:
                r2 = move-exception
                r7 = r0
                r0 = r12
                r12 = r7
                goto L_0x0049
            L_0x0047:
                r2 = move-exception
                r12 = r0
            L_0x0049:
                r2.printStackTrace()
                goto L_0x004e
            L_0x004d:
                r12 = r0
            L_0x004e:
                boolean r2 = r11 instanceof org.telegram.tgnet.TLRPC$TL_statsGraphError
                if (r2 == 0) goto L_0x005d
                r2 = 0
                r8.isEmpty = r2
                r8.isError = r1
                org.telegram.tgnet.TLRPC$TL_statsGraphError r11 = (org.telegram.tgnet.TLRPC$TL_statsGraphError) r11
                java.lang.String r11 = r11.error
                r8.errorMessage = r11
            L_0x005d:
                r4 = r12
                r3 = r0
                goto L_0x0062
            L_0x0060:
                r3 = r0
                r4 = r3
            L_0x0062:
                org.telegram.ui.-$$Lambda$StatisticActivity$ChartViewData$vMH7ubLdDDtfG2fUC-nS18g4dGo r11 = new org.telegram.ui.-$$Lambda$StatisticActivity$ChartViewData$vMH7ubLdDDtfG2fUC-nS18g4dGo
                r1 = r11
                r2 = r8
                r5 = r9
                r6 = r10
                r1.<init>(r3, r4, r5, r6)
                org.telegram.messenger.AndroidUtilities.runOnUIThread(r11)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.StatisticActivity.ChartViewData.lambda$load$1$StatisticActivity$ChartViewData(org.telegram.ui.Components.RecyclerListView, org.telegram.ui.StatisticActivity$DiffUtilsCallback, org.telegram.tgnet.TLObject, org.telegram.tgnet.TLRPC$TL_error):void");
        }

        public /* synthetic */ void lambda$null$0$StatisticActivity$ChartViewData(ChartData chartData2, String str, RecyclerListView recyclerListView, DiffUtilsCallback diffUtilsCallback) {
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
                diffUtilsCallback.update();
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
            this.diffUtilsCallback.update();
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
        if (view instanceof OverviewCell) {
            ((OverviewCell) view).updateColors();
        }
    }

    private static class DiffUtilsCallback extends DiffUtil.Callback {
        private final Adapter adapter;
        int count;
        int endPosts;
        int folowersCell;
        int growCell;
        int interactionsCell;
        int ivInteractionsCell;
        int languagesCell;
        private final LinearLayoutManager layoutManager;
        int newFollowersBySourceCell;
        int notificationsCell;
        SparseIntArray positionToTypeMap;
        int startPosts;
        int topHourseCell;
        int viewsBySourceCell;

        private DiffUtilsCallback(Adapter adapter2, LinearLayoutManager linearLayoutManager) {
            this.positionToTypeMap = new SparseIntArray();
            this.growCell = -1;
            this.folowersCell = -1;
            this.interactionsCell = -1;
            this.ivInteractionsCell = -1;
            this.viewsBySourceCell = -1;
            this.newFollowersBySourceCell = -1;
            this.languagesCell = -1;
            this.topHourseCell = -1;
            this.notificationsCell = -1;
            this.startPosts = -1;
            this.endPosts = -1;
            this.adapter = adapter2;
            this.layoutManager = linearLayoutManager;
        }

        public void saveOldState() {
            this.positionToTypeMap.clear();
            this.count = this.adapter.getItemCount();
            for (int i = 0; i < this.count; i++) {
                this.positionToTypeMap.put(i, this.adapter.getItemViewType(i));
            }
            Adapter adapter2 = this.adapter;
            this.growCell = adapter2.growCell;
            this.folowersCell = adapter2.folowersCell;
            this.interactionsCell = adapter2.interactionsCell;
            this.ivInteractionsCell = adapter2.ivInteractionsCell;
            this.viewsBySourceCell = adapter2.viewsBySourceCell;
            this.newFollowersBySourceCell = adapter2.newFollowersBySourceCell;
            this.languagesCell = adapter2.languagesCell;
            this.topHourseCell = adapter2.topHourseCell;
            this.notificationsCell = adapter2.notificationsCell;
            this.startPosts = adapter2.recentPostsStartRow;
            this.endPosts = adapter2.recentPostsEndRow;
        }

        public int getOldListSize() {
            return this.count;
        }

        public int getNewListSize() {
            return this.adapter.count;
        }

        public boolean areItemsTheSame(int i, int i2) {
            if (this.positionToTypeMap.get(i) == 13 && this.adapter.getItemViewType(i2) == 13) {
                return true;
            }
            if (this.positionToTypeMap.get(i) == 10 && this.adapter.getItemViewType(i2) == 10) {
                return true;
            }
            int i3 = this.startPosts;
            if (i < i3 || i > this.endPosts) {
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
                if (i == this.notificationsCell && i2 == this.adapter.notificationsCell) {
                    return true;
                }
                return false;
            } else if (i - i3 == i2 - this.adapter.recentPostsStartRow) {
                return true;
            } else {
                return false;
            }
        }

        public boolean areContentsTheSame(int i, int i2) {
            return this.positionToTypeMap.get(i) == this.adapter.getItemViewType(i2);
        }

        public void update() {
            int i;
            long j;
            int i2;
            View findViewByPosition;
            saveOldState();
            this.adapter.update();
            int findFirstVisibleItemPosition = this.layoutManager.findFirstVisibleItemPosition();
            int findLastVisibleItemPosition = this.layoutManager.findLastVisibleItemPosition();
            while (true) {
                i = 0;
                if (findFirstVisibleItemPosition <= findLastVisibleItemPosition) {
                    if (this.adapter.getItemId(findFirstVisibleItemPosition) != -1 && (findViewByPosition = this.layoutManager.findViewByPosition(findFirstVisibleItemPosition)) != null) {
                        j = this.adapter.getItemId(findFirstVisibleItemPosition);
                        i2 = findViewByPosition.getTop();
                        break;
                    }
                    findFirstVisibleItemPosition++;
                } else {
                    j = -1;
                    i2 = 0;
                    break;
                }
            }
            DiffUtil.calculateDiff(this).dispatchUpdatesTo((RecyclerView.Adapter) this.adapter);
            if (j != -1) {
                int i3 = -1;
                while (true) {
                    if (i >= this.adapter.getItemCount()) {
                        break;
                    } else if (this.adapter.getItemId(i) == j) {
                        i3 = i;
                        break;
                    } else {
                        i++;
                    }
                }
                if (i3 > 0) {
                    this.layoutManager.scrollToPositionWithOffset(i3, i2);
                }
            }
        }
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ChartViewData chartViewData;
        ChartData chartData;
        $$Lambda$StatisticActivity$CqWRJOatjWarjR1aKS5KtRf4UcY r10 = new ThemeDescription.ThemeDescriptionDelegate() {
            public final void didSetColor() {
                StatisticActivity.this.lambda$getThemeDescriptions$5$StatisticActivity();
            }
        };
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription((View) this.recyclerListView, 0, new Class[]{StatisticPostInfoCell.class}, new String[]{"message"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogTextBlack"));
        arrayList.add(new ThemeDescription((View) this.recyclerListView, 0, new Class[]{StatisticPostInfoCell.class}, new String[]{"views"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogTextBlack"));
        arrayList.add(new ThemeDescription((View) this.recyclerListView, 0, new Class[]{StatisticPostInfoCell.class}, new String[]{"shares"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogTextGray4"));
        arrayList.add(new ThemeDescription((View) this.recyclerListView, 0, new Class[]{StatisticPostInfoCell.class}, new String[]{"date"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogTextGray4"));
        arrayList.add(new ThemeDescription((View) this.recyclerListView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "dialogTextBlack"));
        $$Lambda$StatisticActivity$CqWRJOatjWarjR1aKS5KtRf4UcY r8 = r10;
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (String[]) null, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) r8, "dialogTextBlack"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (String[]) null, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) r8, "statisticChartSignature"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (String[]) null, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) r8, "statisticChartSignatureAlpha"));
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
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r7, "windowBackgroundWhiteGreenText2"));
        arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r7, "windowBackgroundWhiteRedText5"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        ChatAvatarContainer chatAvatarContainer = this.avatarContainer;
        SimpleTextView simpleTextView = null;
        arrayList.add(new ThemeDescription(chatAvatarContainer != null ? chatAvatarContainer.getTitleTextView() : null, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "player_actionBarTitle"));
        ChatAvatarContainer chatAvatarContainer2 = this.avatarContainer;
        if (chatAvatarContainer2 != null) {
            simpleTextView = chatAvatarContainer2.getSubtitleTextView();
        }
        arrayList.add(new ThemeDescription((View) simpleTextView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, (Class[]) null, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "player_actionBarSubtitle", (Object) null));
        for (int i = 0; i < 9; i++) {
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
            } else if (i == 6) {
                chartViewData = this.notificationsData;
            } else if (i == 7) {
                chartViewData = this.topHoursData;
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
        return arrayList;
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

    public static class OverviewData {
        String followersPrimary;
        String followersSecondary;
        String followersTitle = LocaleController.getString("FollowersChartTitle", NUM);
        boolean followersUp;
        String notificationsPrimary;
        String notificationsTitle;
        String sharesPrimary;
        String sharesSecondary;
        String sharesTitle;
        boolean sharesUp;
        String viewsPrimary;
        String viewsSecondary;
        String viewsTitle;
        boolean viewsUp;

        public OverviewData(TLRPC$TL_stats_broadcastStats tLRPC$TL_stats_broadcastStats) {
            String str;
            String str2;
            String str3;
            String str4;
            TLRPC$TL_stats_broadcastStats tLRPC$TL_stats_broadcastStats2 = tLRPC$TL_stats_broadcastStats;
            TLRPC$TL_statsAbsValueAndPrev tLRPC$TL_statsAbsValueAndPrev = tLRPC$TL_stats_broadcastStats2.followers;
            double d = tLRPC$TL_statsAbsValueAndPrev.current;
            double d2 = tLRPC$TL_statsAbsValueAndPrev.previous;
            int i = (int) (d - d2);
            float abs = Math.abs((((float) i) / ((float) d2)) * 100.0f);
            this.followersPrimary = AndroidUtilities.formatWholeNumber((int) tLRPC$TL_stats_broadcastStats2.followers.current, 0);
            String str5 = "+";
            if (i == 0) {
                this.followersSecondary = "";
            } else {
                int i2 = (int) abs;
                if (abs == ((float) i2)) {
                    Locale locale = Locale.ENGLISH;
                    Object[] objArr = new Object[3];
                    StringBuilder sb = new StringBuilder();
                    if (i > 0) {
                        str4 = str5;
                    } else {
                        str4 = "";
                    }
                    sb.append(str4);
                    sb.append(AndroidUtilities.formatWholeNumber(i, 0));
                    objArr[0] = sb.toString();
                    objArr[1] = Integer.valueOf(i2);
                    objArr[2] = "%";
                    this.followersSecondary = String.format(locale, "%s (%d%s)", objArr);
                } else {
                    Locale locale2 = Locale.ENGLISH;
                    Object[] objArr2 = new Object[3];
                    StringBuilder sb2 = new StringBuilder();
                    if (i > 0) {
                        str3 = str5;
                    } else {
                        str3 = "";
                    }
                    sb2.append(str3);
                    sb2.append(AndroidUtilities.formatWholeNumber(i, 0));
                    objArr2[0] = sb2.toString();
                    objArr2[1] = Float.valueOf(abs);
                    objArr2[2] = "%";
                    this.followersSecondary = String.format(locale2, "%s (%.1f%s)", objArr2);
                }
            }
            this.followersUp = i >= 0;
            TLRPC$TL_statsAbsValueAndPrev tLRPC$TL_statsAbsValueAndPrev2 = tLRPC$TL_stats_broadcastStats2.shares_per_post;
            double d3 = tLRPC$TL_statsAbsValueAndPrev2.current;
            double d4 = tLRPC$TL_statsAbsValueAndPrev2.previous;
            int i3 = (int) (d3 - d4);
            float abs2 = Math.abs((((float) i3) / ((float) d4)) * 100.0f);
            this.sharesTitle = LocaleController.getString("SharesPerPost", NUM);
            this.sharesPrimary = AndroidUtilities.formatWholeNumber((int) tLRPC$TL_stats_broadcastStats2.shares_per_post.current, 0);
            if (i3 == 0) {
                this.sharesSecondary = "";
            } else {
                int i4 = (int) abs2;
                if (abs2 == ((float) i4)) {
                    Locale locale3 = Locale.ENGLISH;
                    Object[] objArr3 = new Object[3];
                    StringBuilder sb3 = new StringBuilder();
                    if (i3 > 0) {
                        str2 = str5;
                    } else {
                        str2 = "";
                    }
                    sb3.append(str2);
                    sb3.append(AndroidUtilities.formatWholeNumber(i3, 0));
                    objArr3[0] = sb3.toString();
                    objArr3[1] = Integer.valueOf(i4);
                    objArr3[2] = "%";
                    this.sharesSecondary = String.format(locale3, "%s (%d%s)", objArr3);
                } else {
                    Locale locale4 = Locale.ENGLISH;
                    Object[] objArr4 = new Object[3];
                    StringBuilder sb4 = new StringBuilder();
                    if (i3 > 0) {
                        str = str5;
                    } else {
                        str = "";
                    }
                    sb4.append(str);
                    sb4.append(AndroidUtilities.formatWholeNumber(i3, 0));
                    objArr4[0] = sb4.toString();
                    objArr4[1] = Float.valueOf(abs2);
                    objArr4[2] = "%";
                    this.sharesSecondary = String.format(locale4, "%s (%.1f%s)", objArr4);
                }
            }
            this.sharesUp = i3 >= 0;
            TLRPC$TL_statsAbsValueAndPrev tLRPC$TL_statsAbsValueAndPrev3 = tLRPC$TL_stats_broadcastStats2.views_per_post;
            double d5 = tLRPC$TL_statsAbsValueAndPrev3.current;
            double d6 = tLRPC$TL_statsAbsValueAndPrev3.previous;
            int i5 = (int) (d5 - d6);
            float abs3 = Math.abs((((float) i5) / ((float) d6)) * 100.0f);
            this.viewsTitle = LocaleController.getString("ViewsPerPost", NUM);
            this.viewsPrimary = AndroidUtilities.formatWholeNumber((int) tLRPC$TL_stats_broadcastStats2.views_per_post.current, 0);
            if (i5 == 0) {
                this.viewsSecondary = "";
            } else {
                int i6 = (int) abs3;
                if (abs3 == ((float) i6)) {
                    Locale locale5 = Locale.ENGLISH;
                    Object[] objArr5 = new Object[3];
                    StringBuilder sb5 = new StringBuilder();
                    sb5.append(i5 <= 0 ? "" : str5);
                    sb5.append(AndroidUtilities.formatWholeNumber(i5, 0));
                    objArr5[0] = sb5.toString();
                    objArr5[1] = Integer.valueOf(i6);
                    objArr5[2] = "%";
                    this.viewsSecondary = String.format(locale5, "%s (%d%s)", objArr5);
                } else {
                    Locale locale6 = Locale.ENGLISH;
                    Object[] objArr6 = new Object[3];
                    StringBuilder sb6 = new StringBuilder();
                    sb6.append(i5 <= 0 ? "" : str5);
                    sb6.append(AndroidUtilities.formatWholeNumber(i5, 0));
                    objArr6[0] = sb6.toString();
                    objArr6[1] = Float.valueOf(abs3);
                    objArr6[2] = "%";
                    this.viewsSecondary = String.format(locale6, "%s (%.1f%s)", objArr6);
                }
            }
            this.viewsUp = i5 >= 0;
            TLRPC$TL_statsPercentValue tLRPC$TL_statsPercentValue = tLRPC$TL_stats_broadcastStats2.enabled_notifications;
            float f = (float) ((tLRPC$TL_statsPercentValue.part / tLRPC$TL_statsPercentValue.total) * 100.0d);
            this.notificationsTitle = LocaleController.getString("EnabledNotifications", NUM);
            int i7 = (int) f;
            if (f == ((float) i7)) {
                this.notificationsPrimary = String.format(Locale.ENGLISH, "%d%s", new Object[]{Integer.valueOf(i7), "%"});
            } else {
                this.notificationsPrimary = String.format(Locale.ENGLISH, "%.1f%s", new Object[]{Float.valueOf(f), "%"});
            }
        }
    }

    public static class OverviewCell extends LinearLayout {
        TextView[] primary = new TextView[4];
        TextView[] secondary = new TextView[4];
        TextView[] title = new TextView[4];

        public OverviewCell(Context context) {
            super(context);
            setOrientation(1);
            setPadding(AndroidUtilities.dp(16.0f), 0, AndroidUtilities.dp(16.0f), 0);
            for (int i = 0; i < 2; i++) {
                LinearLayout linearLayout = new LinearLayout(context);
                linearLayout.setOrientation(0);
                for (int i2 = 0; i2 < 2; i2++) {
                    LinearLayout linearLayout2 = new LinearLayout(context);
                    linearLayout2.setOrientation(1);
                    LinearLayout linearLayout3 = new LinearLayout(context);
                    linearLayout3.setOrientation(0);
                    int i3 = (i * 2) + i2;
                    this.primary[i3] = new TextView(context);
                    this.secondary[i3] = new TextView(context);
                    this.title[i3] = new TextView(context);
                    this.primary[i3].setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
                    this.primary[i3].setTextSize(17.0f);
                    this.title[i3].setTextSize(13.0f);
                    this.secondary[i3].setTextSize(13.0f);
                    this.secondary[i3].setPadding(AndroidUtilities.dp(4.0f), 0, 0, 0);
                    linearLayout3.addView(this.primary[i3]);
                    linearLayout3.addView(this.secondary[i3]);
                    linearLayout2.addView(linearLayout3);
                    linearLayout2.addView(this.title[i3]);
                    linearLayout.addView(linearLayout2, LayoutHelper.createLinear(-1, -2, 1.0f));
                }
                addView(linearLayout, LayoutHelper.createFrame(-1, -2.0f, 0, 0.0f, 0.0f, 0.0f, 16.0f));
            }
        }

        public void setData(OverviewData overviewData) {
            String str;
            this.primary[0].setText(overviewData.followersPrimary);
            this.primary[1].setText(overviewData.notificationsPrimary);
            this.primary[2].setText(overviewData.viewsPrimary);
            this.primary[3].setText(overviewData.sharesPrimary);
            this.secondary[0].setText(overviewData.followersSecondary);
            String str2 = "windowBackgroundWhiteGreenText2";
            this.secondary[0].setTag(overviewData.followersUp ? str2 : "windowBackgroundWhiteRedText5");
            this.secondary[1].setText("");
            this.secondary[2].setText(overviewData.viewsSecondary);
            TextView textView = this.secondary[2];
            if (overviewData.viewsUp) {
                str = str2;
            } else {
                str = "windowBackgroundWhiteRedText5";
            }
            textView.setTag(str);
            this.secondary[3].setText(overviewData.sharesSecondary);
            TextView textView2 = this.secondary[3];
            if (!overviewData.sharesUp) {
                str2 = "windowBackgroundWhiteRedText5";
            }
            textView2.setTag(str2);
            this.title[0].setText(overviewData.followersTitle);
            this.title[1].setText(overviewData.notificationsTitle);
            this.title[2].setText(overviewData.viewsTitle);
            this.title[3].setText(overviewData.sharesTitle);
            updateColors();
        }

        /* access modifiers changed from: private */
        public void updateColors() {
            for (int i = 0; i < 4; i++) {
                this.primary[i].setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
                this.title[i].setTextColor(Theme.getColor("windowBackgroundWhiteGrayText2"));
                String str = (String) this.secondary[i].getTag();
                if (str != null) {
                    this.secondary[i].setTextColor(Theme.getColor(str));
                }
            }
        }
    }
}
