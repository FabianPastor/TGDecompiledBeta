package org.telegram.ui.Wallet;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build.VERSION;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.OnScrollListener;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import drinkless.org.ton.TonApi.GenericAccountState;
import drinkless.org.ton.TonApi.InternalTransactionId;
import drinkless.org.ton.TonApi.RawTransaction;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MrzRecognizer.Result;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.TonController;
import org.telegram.messenger.UserConfig;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarLayout;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.CameraScanActivity;
import org.telegram.ui.CameraScanActivity.CameraScanActivityDelegate;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.PullForegroundDrawable;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.SectionsAdapter;
import org.telegram.ui.Wallet.WalletActionSheet.WalletActionSheetDelegate;
import org.telegram.ui.Wallet.WalletActionSheet.WalletActionSheetDelegate.-CC;

public class WalletActivity extends BaseFragment implements NotificationCenterDelegate, WalletActionSheetDelegate {
    public static final int CAMERA_PERMISSION_REQUEST_CODE = 34;
    private static final String PENDING_KEY = "pending";
    private static final int SHORT_POLL_DELAY = 3000;
    private static final int menu_settings = 1;
    public static float viewOffset;
    private GenericAccountState accountState;
    private boolean accountStateLoaded;
    private Adapter adapter;
    private GradientDrawable backgroundDrawable;
    private Paint blackPaint;
    private ActionBarLayout[] cameraScanLayout;
    private boolean canShowHiddenPull;
    private RawTransaction lastTransaction;
    private long lastUpdateTime;
    private LinearLayoutManager layoutManager;
    private PullRecyclerView listView;
    private boolean loadingAccountState;
    private boolean loadingTransactions;
    private String openTransferAfterOpen;
    private View paddingView;
    private Drawable pinnedHeaderShadowDrawable;
    private PullForegroundDrawable pullForegroundDrawable;
    private float[] radii;
    private HashMap<String, ArrayList<RawTransaction>> sectionArrays;
    private ArrayList<String> sections;
    private Runnable shortPollRunnable;
    private long startArchivePullingTime;
    private SimpleTextView statusTextView;
    private HashMap<Long, RawTransaction> transactionsDict;
    private boolean transactionsEndReached;
    private Runnable updateTimeRunnable;
    private WalletActionSheet walletActionSheet;
    private String walletAddress;
    private boolean wasPulled;

    private class Adapter extends SectionsAdapter {
        private Context context;

        public Object getItem(int i, int i2) {
            return null;
        }

        public String getLetter(int i) {
            return null;
        }

        public int getPositionForScrollProgress(float f) {
            return 0;
        }

        public boolean isEnabled(int i, int i2) {
            return (i == 0 || i2 == 0) ? false : true;
        }

        public Adapter(Context context) {
            this.context = context;
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View anonymousClass1;
            if (i == 0) {
                anonymousClass1 = new WalletBalanceCell(this.context) {
                    /* Access modifiers changed, original: protected */
                    public void onReceivePressed() {
                        WalletActivity walletActivity = WalletActivity.this;
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("ton://transfer/");
                        stringBuilder.append(WalletActivity.this.walletAddress);
                        walletActivity.showInvoiceSheet(stringBuilder.toString(), 0);
                    }

                    /* Access modifiers changed, original: protected */
                    public void onSendPressed() {
                        String str = "Wallet";
                        if (WalletActivity.this.getTonController().hasPendingTransactions()) {
                            AlertsCreator.showSimpleAlert(WalletActivity.this, LocaleController.getString(str, NUM), LocaleController.getString("WalletPendingWait", NUM));
                            return;
                        }
                        int syncProgress = WalletActivity.this.getTonController().getSyncProgress();
                        if (syncProgress == 0 || syncProgress == 100) {
                            WalletActivity walletActivity = WalletActivity.this;
                            walletActivity.walletActionSheet = new WalletActionSheet(walletActivity, 0, walletActivity.walletAddress);
                            WalletActivity.this.walletActionSheet.setDelegate(WalletActivity.this);
                            WalletActivity.this.walletActionSheet.setOnDismissListener(new -$$Lambda$WalletActivity$Adapter$1$hpVEDtMablSLzgEdhjXH4iOoeOQ(this));
                            WalletActivity.this.walletActionSheet.show();
                            return;
                        }
                        AlertsCreator.showSimpleAlert(WalletActivity.this, LocaleController.getString(str, NUM), LocaleController.getString("WalletSendSyncInProgress", NUM));
                    }

                    public /* synthetic */ void lambda$onSendPressed$0$WalletActivity$Adapter$1(DialogInterface dialogInterface) {
                        if (WalletActivity.this.walletActionSheet == dialogInterface) {
                            WalletActivity.this.walletActionSheet = null;
                        }
                    }
                };
            } else if (i == 1) {
                anonymousClass1 = new WalletTransactionCell(this.context);
            } else if (i == 2) {
                anonymousClass1 = new WalletCreatedCell(this.context) {
                    /* Access modifiers changed, original: protected */
                    public void onMeasure(int i, int i2) {
                        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(i), NUM), MeasureSpec.makeMeasureSpec(Math.max(AndroidUtilities.dp(280.0f), WalletActivity.this.fragmentView.getMeasuredHeight() - AndroidUtilities.dp(242.0f)), NUM));
                    }
                };
            } else if (i == 3) {
                anonymousClass1 = new WalletDateCell(this.context);
            } else if (i != 4) {
                anonymousClass1 = i != 5 ? new WalletSyncCell(this.context) {
                    /* Access modifiers changed, original: protected */
                    public void onMeasure(int i, int i2) {
                        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(i), NUM), MeasureSpec.makeMeasureSpec(Math.max(AndroidUtilities.dp(280.0f), WalletActivity.this.fragmentView.getMeasuredHeight() - AndroidUtilities.dp(242.0f)), NUM));
                    }
                } : WalletActivity.this.paddingView = new View(this.context) {
                    /* Access modifiers changed, original: protected */
                    public void onMeasure(int i, int i2) {
                        i = WalletActivity.this.listView.getChildCount();
                        i2 = WalletActivity.this.adapter.getItemCount();
                        int i3 = 0;
                        for (int i4 = 0; i4 < i; i4++) {
                            int childAdapterPosition = WalletActivity.this.listView.getChildAdapterPosition(WalletActivity.this.listView.getChildAt(i4));
                            if (!(childAdapterPosition == 0 || childAdapterPosition == i2 - 1)) {
                                i3 += WalletActivity.this.listView.getChildAt(i4).getMeasuredHeight();
                            }
                        }
                        i = WalletActivity.this.fragmentView.getMeasuredHeight() - i3;
                        if (i <= 0) {
                            i = 0;
                        }
                        setMeasuredDimension(WalletActivity.this.listView.getMeasuredWidth(), i);
                    }
                };
            } else {
                anonymousClass1 = new View(this.context) {
                    /* Access modifiers changed, original: protected */
                    public void onDraw(Canvas canvas) {
                        WalletActivity.this.pullForegroundDrawable.draw(canvas);
                    }

                    /* Access modifiers changed, original: protected */
                    public void onMeasure(int i, int i2) {
                        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(i), NUM), MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(AndroidUtilities.dp(72.0f)), NUM));
                    }
                };
                WalletActivity.this.pullForegroundDrawable.setCell(anonymousClass1);
            }
            return new Holder(anonymousClass1);
        }

        public void onBindViewHolder(int i, int i2, ViewHolder viewHolder) {
            int itemViewType = viewHolder.getItemViewType();
            if (itemViewType != 0) {
                boolean z = false;
                if (itemViewType == 1) {
                    WalletTransactionCell walletTransactionCell = (WalletTransactionCell) viewHolder.itemView;
                    ArrayList arrayList = (ArrayList) WalletActivity.this.sectionArrays.get((String) WalletActivity.this.sections.get(i - 1));
                    RawTransaction rawTransaction = (RawTransaction) arrayList.get(i2 - 1);
                    if (i2 != arrayList.size()) {
                        z = true;
                    }
                    walletTransactionCell.setTransaction(rawTransaction, z);
                    return;
                } else if (itemViewType == 2) {
                    ((WalletCreatedCell) viewHolder.itemView).setAddress(WalletActivity.this.walletAddress);
                    return;
                } else if (itemViewType == 3) {
                    WalletDateCell walletDateCell = (WalletDateCell) viewHolder.itemView;
                    String str = (String) WalletActivity.this.sections.get(i - 1);
                    if ("pending".equals(str)) {
                        walletDateCell.setText(LocaleController.getString("WalletPendingTransactions", NUM));
                        return;
                    } else {
                        walletDateCell.setDate(((RawTransaction) ((ArrayList) WalletActivity.this.sectionArrays.get(str)).get(0)).utime);
                        return;
                    }
                } else {
                    return;
                }
            }
            WalletBalanceCell walletBalanceCell = (WalletBalanceCell) viewHolder.itemView;
            if (WalletActivity.this.getTonController().isWalletLoaded()) {
                walletBalanceCell.setBalance(TonController.getBalance(WalletActivity.this.accountState));
            } else {
                walletBalanceCell.setBalance(-1);
            }
        }

        public int getItemViewType(int i, int i2) {
            int i3 = 1;
            if (i == 0) {
                if (i2 == 0) {
                    return 4;
                }
                if (i2 == 1) {
                    return 0;
                }
                return WalletActivity.this.getTonController().isWalletLoaded() ? 2 : 6;
            } else if (i - 1 >= WalletActivity.this.sections.size()) {
                return 5;
            } else {
                if (i2 == 0) {
                    i3 = 3;
                }
                return i3;
            }
        }

        public int getSectionCount() {
            if (WalletActivity.this.sections.isEmpty()) {
                return 1;
            }
            return 1 + (WalletActivity.this.sections.size() + 1);
        }

        public int getCountForSection(int i) {
            if (i == 0) {
                return WalletActivity.this.sections.isEmpty() ? 3 : 2;
            }
            i--;
            if (i < WalletActivity.this.sections.size()) {
                return ((ArrayList) WalletActivity.this.sectionArrays.get(WalletActivity.this.sections.get(i))).size() + 1;
            }
            return 1;
        }

        public View getSectionHeaderView(int i, View view) {
            if (view == null) {
                view = new WalletDateCell(this.context);
                view.setBackgroundColor(Theme.getColor("wallet_whiteBackground") & -NUM);
            }
            WalletDateCell walletDateCell = (WalletDateCell) view;
            if (i == 0) {
                walletDateCell.setAlpha(0.0f);
            } else {
                i--;
                if (i < WalletActivity.this.sections.size()) {
                    view.setAlpha(1.0f);
                    String str = (String) WalletActivity.this.sections.get(i);
                    if ("pending".equals(str)) {
                        walletDateCell.setText(LocaleController.getString("WalletPendingTransactions", NUM));
                    } else {
                        walletDateCell.setDate(((RawTransaction) ((ArrayList) WalletActivity.this.sectionArrays.get(str)).get(0)).utime);
                    }
                }
            }
            return view;
        }
    }

    public class PullRecyclerView extends RecyclerListView {
        private boolean firstLayout = true;
        private boolean ignoreLayout;

        public PullRecyclerView(Context context) {
            super(context);
        }

        public void setViewsOffset(float f) {
            WalletActivity.viewOffset = f;
            int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                getChildAt(i).setTranslationY(WalletActivity.viewOffset);
            }
            invalidate();
            WalletActivity.this.fragmentView.invalidate();
        }

        public float getViewOffset() {
            return WalletActivity.viewOffset;
        }

        /* Access modifiers changed, original: protected */
        public void onMeasure(int i, int i2) {
            if (this.firstLayout) {
                this.ignoreLayout = true;
                WalletActivity.this.layoutManager.scrollToPositionWithOffset(1, 0);
                this.ignoreLayout = false;
                this.firstLayout = false;
            }
            super.onMeasure(i, i2);
        }

        public void requestLayout() {
            if (!this.ignoreLayout) {
                super.requestLayout();
            }
        }

        public void setAdapter(androidx.recyclerview.widget.RecyclerView.Adapter adapter) {
            super.setAdapter(adapter);
            this.firstLayout = true;
        }

        public void addView(View view, int i, LayoutParams layoutParams) {
            super.addView(view, i, layoutParams);
            view.setTranslationY(WalletActivity.viewOffset);
        }

        public void removeView(View view) {
            super.removeView(view);
            view.setTranslationY(0.0f);
        }

        public void onDraw(Canvas canvas) {
            if (!(WalletActivity.this.pullForegroundDrawable == null || WalletActivity.viewOffset == 0.0f)) {
                WalletActivity.this.pullForegroundDrawable.drawOverScroll(canvas);
            }
            super.onDraw(canvas);
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            int action = motionEvent.getAction();
            if (action == 0) {
                WalletActivity.this.listView.setOverScrollMode(0);
                if (WalletActivity.this.wasPulled) {
                    WalletActivity.this.wasPulled = false;
                    if (WalletActivity.this.pullForegroundDrawable != null) {
                        WalletActivity.this.pullForegroundDrawable.doNotShow();
                    }
                    WalletActivity.this.canShowHiddenPull = false;
                }
            }
            boolean onTouchEvent = super.onTouchEvent(motionEvent);
            if (action == 1 || action == 3) {
                action = WalletActivity.this.layoutManager.findFirstVisibleItemPosition();
                if (action == 0) {
                    View findViewByPosition = WalletActivity.this.layoutManager.findViewByPosition(action);
                    int dp = (int) (((float) AndroidUtilities.dp(72.0f)) * 0.85f);
                    int top = findViewByPosition.getTop() + findViewByPosition.getMeasuredHeight();
                    if (findViewByPosition != null) {
                        long currentTimeMillis = System.currentTimeMillis() - WalletActivity.this.startArchivePullingTime;
                        WalletActivity.this.listView.smoothScrollBy(0, top, CubicBezierInterpolator.EASE_OUT_QUINT);
                        if (top >= dp && currentTimeMillis >= 200) {
                            WalletActivity.this.wasPulled = true;
                            AndroidUtilities.cancelRunOnUIThread(WalletActivity.this.updateTimeRunnable);
                            WalletActivity.this.lastUpdateTime = 0;
                            WalletActivity.this.loadAccountState();
                            WalletActivity.this.updateTime(false);
                        }
                        if (WalletActivity.viewOffset != 0.0f) {
                            ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{WalletActivity.viewOffset, 0.0f});
                            ofFloat.addUpdateListener(new -$$Lambda$WalletActivity$PullRecyclerView$lkXwd8GU-zP0QF_nYEqnNgLp6_Q(this));
                            ofFloat.setDuration((long) (350.0f - ((WalletActivity.viewOffset / ((float) PullForegroundDrawable.getMaxOverscroll())) * 120.0f)));
                            ofFloat.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
                            WalletActivity.this.listView.setScrollEnabled(false);
                            ofFloat.addListener(new AnimatorListenerAdapter() {
                                public void onAnimationEnd(Animator animator) {
                                    super.onAnimationEnd(animator);
                                    WalletActivity.this.listView.setScrollEnabled(true);
                                }
                            });
                            ofFloat.start();
                        }
                    }
                }
            }
            return onTouchEvent;
        }

        public /* synthetic */ void lambda$onTouchEvent$0$WalletActivity$PullRecyclerView(ValueAnimator valueAnimator) {
            WalletActivity.this.listView.setViewsOffset(((Float) valueAnimator.getAnimatedValue()).floatValue());
        }
    }

    public /* synthetic */ void openInvoice(String str, long j) {
        -CC.$default$openInvoice(this, str, j);
    }

    public /* synthetic */ void openSendToAddress(String str) {
        -CC.$default$openSendToAddress(this, str);
    }

    public /* synthetic */ void lambda$new$0$WalletActivity() {
        updateTime(true);
    }

    public WalletActivity() {
        this(null);
    }

    public WalletActivity(String str) {
        this.blackPaint = new Paint();
        this.transactionsDict = new HashMap();
        this.sections = new ArrayList();
        this.sectionArrays = new HashMap();
        this.updateTimeRunnable = new -$$Lambda$WalletActivity$JbR_f3lgnrp3kgfDQnFSBGZNQwI(this);
        this.shortPollRunnable = new -$$Lambda$WalletActivity$PSnZhfGT2VD9KuTPAX9CvDMc7jo(this);
        this.openTransferAfterOpen = str;
        loadAccountState();
    }

    public boolean onFragmentCreate() {
        getNotificationCenter().addObserver(this, NotificationCenter.walletPendingTransactionsChanged);
        getNotificationCenter().addObserver(this, NotificationCenter.walletSyncProgressChanged);
        getTonController().setTransactionCallback(new -$$Lambda$WalletActivity$RPsyFtGhSKDHPV34s5W2HRodoLg(this));
        return super.onFragmentCreate();
    }

    public /* synthetic */ void lambda$onFragmentCreate$1$WalletActivity(boolean z, ArrayList arrayList) {
        getTonController().checkPendingTransactionsForFailure(this.accountState);
        if (z) {
            onFinishGettingAccountState();
        }
        this.loadingTransactions = false;
        if (arrayList != null) {
            this.accountState = getTonController().getCachedAccountState();
            if (!(fillTransactions(arrayList, z) || z)) {
                this.transactionsEndReached = true;
            }
            if (!arrayList.isEmpty() && (this.lastTransaction == null || !z)) {
                this.lastTransaction = (RawTransaction) arrayList.get(arrayList.size() - 1);
            }
            Adapter adapter = this.adapter;
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
            View view = this.paddingView;
            if (view != null) {
                view.requestLayout();
            }
        }
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        AndroidUtilities.cancelRunOnUIThread(this.updateTimeRunnable);
        AndroidUtilities.cancelRunOnUIThread(this.shortPollRunnable);
        getNotificationCenter().removeObserver(this, NotificationCenter.walletPendingTransactionsChanged);
        getNotificationCenter().removeObserver(this, NotificationCenter.walletSyncProgressChanged);
        getTonController().setTransactionCallback(null);
    }

    /* Access modifiers changed, original: protected */
    public ActionBar createActionBar(Context context) {
        AnonymousClass1 anonymousClass1 = new ActionBar(context) {
            /* Access modifiers changed, original: protected */
            public void onMeasure(int i, int i2) {
                if (VERSION.SDK_INT >= 21 && WalletActivity.this.statusTextView != null) {
                    ((FrameLayout.LayoutParams) WalletActivity.this.statusTextView.getLayoutParams()).topMargin = AndroidUtilities.statusBarHeight;
                }
                super.onMeasure(i, i2);
            }
        };
        if (!BuildVars.TON_WALLET_STANDALONE) {
            anonymousClass1.setBackButtonImage(NUM);
        }
        anonymousClass1.setBackgroundColor(Theme.getColor("wallet_blackBackground"));
        String str = "wallet_whiteText";
        anonymousClass1.setTitleColor(Theme.getColor(str));
        anonymousClass1.setItemsColor(Theme.getColor(str), false);
        anonymousClass1.setItemsBackgroundColor(Theme.getColor("wallet_blackBackgroundSelector"), false);
        anonymousClass1.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    WalletActivity.this.finishFragment();
                } else if (i == 1) {
                    WalletActivity walletActivity = WalletActivity.this;
                    walletActivity.presentFragment(new WalletSettingsActivity(0, walletActivity));
                }
            }
        });
        anonymousClass1.createMenu().addItem(1, NUM);
        this.statusTextView = new SimpleTextView(context);
        this.statusTextView.setTextSize(14);
        this.statusTextView.setGravity(17);
        this.statusTextView.setTextColor(Theme.getColor("wallet_statusText"));
        anonymousClass1.addView(this.statusTextView, LayoutHelper.createFrame(-1, -1.0f, 83, 48.0f, 0.0f, 48.0f, 0.0f));
        return anonymousClass1;
    }

    public View createView(Context context) {
        this.pullForegroundDrawable = new PullForegroundDrawable(LocaleController.getString("WalletSwipeToRefresh", NUM), LocaleController.getString("WalletReleaseToRefresh", NUM)) {
            /* Access modifiers changed, original: protected */
            public float getViewOffset() {
                return WalletActivity.this.listView.getViewOffset();
            }
        };
        this.pullForegroundDrawable.setColors("wallet_pullBackground", "wallet_releaseBackground");
        this.pullForegroundDrawable.showHidden();
        this.pullForegroundDrawable.setWillDraw(true);
        String str = "wallet_blackBackground";
        this.blackPaint.setColor(Theme.getColor(str));
        this.backgroundDrawable = new GradientDrawable();
        this.backgroundDrawable.setShape(0);
        int dp = AndroidUtilities.dp(13.0f);
        GradientDrawable gradientDrawable = this.backgroundDrawable;
        r5 = new float[8];
        float f = (float) dp;
        r5[0] = f;
        r5[1] = f;
        r5[2] = f;
        r5[3] = f;
        r5[4] = 0.0f;
        r5[5] = 0.0f;
        r5[6] = 0.0f;
        r5[7] = 0.0f;
        this.radii = r5;
        gradientDrawable.setCornerRadii(r5);
        this.backgroundDrawable.setColor(Theme.getColor("wallet_whiteBackground"));
        AnonymousClass4 anonymousClass4 = new FrameLayout(context) {
            /* Access modifiers changed, original: protected */
            public void onDraw(Canvas canvas) {
                ViewHolder findViewHolderForAdapterPosition = WalletActivity.this.listView.findViewHolderForAdapterPosition(1);
                float dp = (float) AndroidUtilities.dp(13.0f);
                float bottom = (float) (findViewHolderForAdapterPosition != null ? findViewHolderForAdapterPosition.itemView.getBottom() : 0);
                if (bottom < dp) {
                    dp *= bottom / dp;
                }
                int i = (int) (bottom + WalletActivity.viewOffset);
                float[] access$1200 = WalletActivity.this.radii;
                float[] access$12002 = WalletActivity.this.radii;
                float[] access$12003 = WalletActivity.this.radii;
                WalletActivity.this.radii[3] = dp;
                access$12003[2] = dp;
                access$12002[1] = dp;
                access$1200[0] = dp;
                canvas.drawRect(0.0f, 0.0f, (float) getMeasuredWidth(), (float) (AndroidUtilities.dp(6.0f) + i), WalletActivity.this.blackPaint);
                WalletActivity.this.backgroundDrawable.setBounds(0, i - AndroidUtilities.dp(7.0f), getMeasuredWidth(), getMeasuredHeight());
                WalletActivity.this.backgroundDrawable.draw(canvas);
            }
        };
        anonymousClass4.setWillNotDraw(false);
        this.fragmentView = anonymousClass4;
        this.pinnedHeaderShadowDrawable = context.getResources().getDrawable(NUM);
        this.pinnedHeaderShadowDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor("windowBackgroundGrayShadow"), Mode.MULTIPLY));
        this.listView = new PullRecyclerView(context);
        this.listView.setSectionsType(2);
        this.listView.setPinnedHeaderShadowDrawable(this.pinnedHeaderShadowDrawable);
        PullRecyclerView pullRecyclerView = this.listView;
        AnonymousClass5 anonymousClass5 = new LinearLayoutManager(context, 1, false) {
            /* JADX WARNING: Removed duplicated region for block: B:32:0x00b8  */
            /* JADX WARNING: Removed duplicated region for block: B:31:0x00b4  */
            /* JADX WARNING: Removed duplicated region for block: B:36:0x00cf  */
            /* JADX WARNING: Removed duplicated region for block: B:39:0x00e4  */
            /* JADX WARNING: Removed duplicated region for block: B:76:0x01d5  */
            /* JADX WARNING: Removed duplicated region for block: B:78:0x01f7  */
            /* JADX WARNING: Removed duplicated region for block: B:31:0x00b4  */
            /* JADX WARNING: Removed duplicated region for block: B:32:0x00b8  */
            /* JADX WARNING: Removed duplicated region for block: B:36:0x00cf  */
            /* JADX WARNING: Removed duplicated region for block: B:39:0x00e4  */
            /* JADX WARNING: Removed duplicated region for block: B:42:0x00f2 A:{SKIP} */
            /* JADX WARNING: Removed duplicated region for block: B:76:0x01d5  */
            /* JADX WARNING: Removed duplicated region for block: B:78:0x01f7  */
            public int scrollVerticallyBy(int r13, androidx.recyclerview.widget.RecyclerView.Recycler r14, androidx.recyclerview.widget.RecyclerView.State r15) {
                /*
                r12 = this;
                r0 = org.telegram.ui.Wallet.WalletActivity.this;
                r0 = r0.listView;
                r0 = r0.getScrollState();
                r1 = 0;
                r2 = 1;
                if (r0 != r2) goto L_0x0010;
            L_0x000e:
                r0 = 1;
                goto L_0x0011;
            L_0x0010:
                r0 = 0;
            L_0x0011:
                r3 = -1;
                r4 = 2;
                r5 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
                if (r13 >= 0) goto L_0x00a0;
            L_0x0017:
                r6 = org.telegram.ui.Wallet.WalletActivity.this;
                r6 = r6.listView;
                r6.setOverScrollMode(r1);
                r6 = org.telegram.ui.Wallet.WalletActivity.this;
                r6 = r6.layoutManager;
                r6 = r6.findFirstVisibleItemPosition();
                if (r6 != 0) goto L_0x0043;
            L_0x002c:
                r7 = org.telegram.ui.Wallet.WalletActivity.this;
                r7 = r7.layoutManager;
                r7 = r7.findViewByPosition(r6);
                if (r7 == 0) goto L_0x0043;
            L_0x0038:
                r7 = r7.getBottom();
                r8 = org.telegram.messenger.AndroidUtilities.dp(r5);
                if (r7 > r8) goto L_0x0043;
            L_0x0042:
                r6 = 1;
            L_0x0043:
                if (r0 != 0) goto L_0x0067;
            L_0x0045:
                r3 = org.telegram.ui.Wallet.WalletActivity.this;
                r3 = r3.layoutManager;
                r3 = r3.findViewByPosition(r6);
                r7 = NUM; // 0x42900000 float:72.0 double:5.517396283E-315;
                r7 = org.telegram.messenger.AndroidUtilities.dp(r7);
                r7 = r7 + r2;
                r3 = r3.getTop();
                r3 = -r3;
                r6 = r6 - r2;
                r6 = r6 * r7;
                r3 = r3 + r6;
                r6 = java.lang.Math.abs(r13);
                if (r3 >= r6) goto L_0x00a0;
            L_0x0065:
                r3 = -r3;
                goto L_0x00a1;
            L_0x0067:
                if (r6 != 0) goto L_0x00a0;
            L_0x0069:
                r7 = org.telegram.ui.Wallet.WalletActivity.this;
                r7 = r7.layoutManager;
                r6 = r7.findViewByPosition(r6);
                r7 = r6.getTop();
                r7 = (float) r7;
                r6 = r6.getMeasuredHeight();
                r6 = (float) r6;
                r7 = r7 / r6;
                r6 = r7 + r5;
                r7 = (r6 > r5 ? 1 : (r6 == r5 ? 0 : -1));
                if (r7 <= 0) goto L_0x0086;
            L_0x0084:
                r6 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
            L_0x0086:
                r7 = org.telegram.ui.Wallet.WalletActivity.this;
                r7 = r7.listView;
                r7.setOverScrollMode(r4);
                r7 = (float) r13;
                r8 = NUM; // 0x3ee66666 float:0.45 double:5.21380997E-315;
                r9 = NUM; // 0x3e800000 float:0.25 double:5.180653787E-315;
                r6 = r6 * r9;
                r8 = r8 - r6;
                r7 = r7 * r8;
                r6 = (int) r7;
                if (r6 <= r3) goto L_0x009e;
            L_0x009d:
                goto L_0x00a1;
            L_0x009e:
                r3 = r6;
                goto L_0x00a1;
            L_0x00a0:
                r3 = r13;
            L_0x00a1:
                r6 = org.telegram.ui.Wallet.WalletActivity.viewOffset;
                r7 = 0;
                r8 = (r6 > r7 ? 1 : (r6 == r7 ? 0 : -1));
                if (r8 == 0) goto L_0x00c3;
            L_0x00a8:
                if (r13 <= 0) goto L_0x00c3;
            L_0x00aa:
                if (r0 == 0) goto L_0x00c3;
            L_0x00ac:
                r3 = (int) r6;
                r3 = (float) r3;
                r6 = (float) r13;
                r3 = r3 - r6;
                r6 = (r3 > r7 ? 1 : (r3 == r7 ? 0 : -1));
                if (r6 >= 0) goto L_0x00b8;
            L_0x00b4:
                r3 = (int) r3;
                r6 = r3;
                r3 = 0;
                goto L_0x00b9;
            L_0x00b8:
                r6 = 0;
            L_0x00b9:
                r8 = org.telegram.ui.Wallet.WalletActivity.this;
                r8 = r8.listView;
                r8.setViewsOffset(r3);
                r3 = r6;
            L_0x00c3:
                r14 = super.scrollVerticallyBy(r3, r14, r15);
                r15 = org.telegram.ui.Wallet.WalletActivity.this;
                r15 = r15.pullForegroundDrawable;
                if (r15 == 0) goto L_0x00d7;
            L_0x00cf:
                r15 = org.telegram.ui.Wallet.WalletActivity.this;
                r15 = r15.pullForegroundDrawable;
                r15.scrollDy = r14;
            L_0x00d7:
                r15 = org.telegram.ui.Wallet.WalletActivity.this;
                r15 = r15.layoutManager;
                r15 = r15.findFirstVisibleItemPosition();
                r6 = 0;
                if (r15 != 0) goto L_0x00ee;
            L_0x00e4:
                r6 = org.telegram.ui.Wallet.WalletActivity.this;
                r6 = r6.layoutManager;
                r6 = r6.findViewByPosition(r15);
            L_0x00ee:
                r8 = 0;
                if (r15 != 0) goto L_0x01c3;
            L_0x00f2:
                if (r6 == 0) goto L_0x01c3;
            L_0x00f4:
                r15 = r6.getBottom();
                r10 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
                r10 = org.telegram.messenger.AndroidUtilities.dp(r10);
                if (r15 < r10) goto L_0x01c3;
            L_0x0100:
                r15 = org.telegram.ui.Wallet.WalletActivity.this;
                r10 = r15.startArchivePullingTime;
                r15 = (r10 > r8 ? 1 : (r10 == r8 ? 0 : -1));
                if (r15 != 0) goto L_0x0113;
            L_0x010a:
                r15 = org.telegram.ui.Wallet.WalletActivity.this;
                r7 = java.lang.System.currentTimeMillis();
                r15.startArchivePullingTime = r7;
            L_0x0113:
                r15 = org.telegram.ui.Wallet.WalletActivity.this;
                r15 = r15.pullForegroundDrawable;
                if (r15 == 0) goto L_0x0124;
            L_0x011b:
                r15 = org.telegram.ui.Wallet.WalletActivity.this;
                r15 = r15.pullForegroundDrawable;
                r15.showHidden();
            L_0x0124:
                r15 = r6.getTop();
                r15 = (float) r15;
                r7 = r6.getMeasuredHeight();
                r7 = (float) r7;
                r15 = r15 / r7;
                r15 = r15 + r5;
                r7 = (r15 > r5 ? 1 : (r15 == r5 ? 0 : -1));
                if (r7 <= 0) goto L_0x0136;
            L_0x0134:
                r15 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
            L_0x0136:
                r7 = java.lang.System.currentTimeMillis();
                r9 = org.telegram.ui.Wallet.WalletActivity.this;
                r9 = r9.startArchivePullingTime;
                r7 = r7 - r9;
                r9 = NUM; // 0x3var_a float:0.85 double:5.25111068E-315;
                r9 = (r15 > r9 ? 1 : (r15 == r9 ? 0 : -1));
                if (r9 <= 0) goto L_0x014f;
            L_0x0148:
                r9 = 220; // 0xdc float:3.08E-43 double:1.087E-321;
                r11 = (r7 > r9 ? 1 : (r7 == r9 ? 0 : -1));
                if (r11 <= 0) goto L_0x014f;
            L_0x014e:
                r1 = 1;
            L_0x014f:
                r2 = org.telegram.ui.Wallet.WalletActivity.this;
                r2 = r2.canShowHiddenPull;
                if (r2 == r1) goto L_0x017f;
            L_0x0157:
                r2 = org.telegram.ui.Wallet.WalletActivity.this;
                r2.canShowHiddenPull = r1;
                r2 = org.telegram.ui.Wallet.WalletActivity.this;
                r2 = r2.wasPulled;
                if (r2 != 0) goto L_0x017f;
            L_0x0164:
                r2 = org.telegram.ui.Wallet.WalletActivity.this;
                r2 = r2.listView;
                r7 = 3;
                r2.performHapticFeedback(r7, r4);
                r2 = org.telegram.ui.Wallet.WalletActivity.this;
                r2 = r2.pullForegroundDrawable;
                if (r2 == 0) goto L_0x017f;
            L_0x0176:
                r2 = org.telegram.ui.Wallet.WalletActivity.this;
                r2 = r2.pullForegroundDrawable;
                r2.colorize(r1);
            L_0x017f:
                r3 = r3 - r14;
                if (r3 == 0) goto L_0x01a3;
            L_0x0182:
                if (r13 >= 0) goto L_0x01a3;
            L_0x0184:
                if (r0 == 0) goto L_0x01a3;
            L_0x0186:
                r0 = org.telegram.ui.Wallet.WalletActivity.viewOffset;
                r1 = org.telegram.ui.Components.PullForegroundDrawable.getMaxOverscroll();
                r1 = (float) r1;
                r0 = r0 / r1;
                r5 = r5 - r0;
                r0 = org.telegram.ui.Wallet.WalletActivity.viewOffset;
                r13 = (float) r13;
                r1 = NUM; // 0x3e4ccccd float:0.2 double:5.164075695E-315;
                r13 = r13 * r1;
                r13 = r13 * r5;
                r0 = r0 - r13;
                r13 = org.telegram.ui.Wallet.WalletActivity.this;
                r13 = r13.listView;
                r13.setViewsOffset(r0);
            L_0x01a3:
                r13 = org.telegram.ui.Wallet.WalletActivity.this;
                r13 = r13.pullForegroundDrawable;
                if (r13 == 0) goto L_0x01f5;
            L_0x01ab:
                r13 = org.telegram.ui.Wallet.WalletActivity.this;
                r13 = r13.pullForegroundDrawable;
                r13.pullProgress = r15;
                r13 = org.telegram.ui.Wallet.WalletActivity.this;
                r13 = r13.pullForegroundDrawable;
                r15 = org.telegram.ui.Wallet.WalletActivity.this;
                r15 = r15.listView;
                r13.setListView(r15);
                goto L_0x01f5;
            L_0x01c3:
                r13 = org.telegram.ui.Wallet.WalletActivity.this;
                r13.startArchivePullingTime = r8;
                r13 = org.telegram.ui.Wallet.WalletActivity.this;
                r13.canShowHiddenPull = r1;
                r13 = org.telegram.ui.Wallet.WalletActivity.this;
                r13 = r13.pullForegroundDrawable;
                if (r13 == 0) goto L_0x01f5;
            L_0x01d5:
                r13 = org.telegram.ui.Wallet.WalletActivity.this;
                r13 = r13.pullForegroundDrawable;
                r13.resetText();
                r13 = org.telegram.ui.Wallet.WalletActivity.this;
                r13 = r13.pullForegroundDrawable;
                r13.pullProgress = r7;
                r13 = org.telegram.ui.Wallet.WalletActivity.this;
                r13 = r13.pullForegroundDrawable;
                r15 = org.telegram.ui.Wallet.WalletActivity.this;
                r15 = r15.listView;
                r13.setListView(r15);
            L_0x01f5:
                if (r6 == 0) goto L_0x01fa;
            L_0x01f7:
                r6.invalidate();
            L_0x01fa:
                return r14;
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Wallet.WalletActivity$AnonymousClass5.scrollVerticallyBy(int, androidx.recyclerview.widget.RecyclerView$Recycler, androidx.recyclerview.widget.RecyclerView$State):int");
            }
        };
        this.layoutManager = anonymousClass5;
        pullRecyclerView.setLayoutManager(anonymousClass5);
        PullRecyclerView pullRecyclerView2 = this.listView;
        Adapter adapter = new Adapter(context);
        this.adapter = adapter;
        pullRecyclerView2.setAdapter(adapter);
        this.listView.setGlowColor(Theme.getColor(str));
        anonymousClass4.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setOnScrollListener(new OnScrollListener() {
            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                WalletActivity.this.fragmentView.invalidate();
                if (!WalletActivity.this.loadingTransactions && !WalletActivity.this.transactionsEndReached) {
                    int findLastVisibleItemPosition = WalletActivity.this.layoutManager.findLastVisibleItemPosition();
                    if ((findLastVisibleItemPosition == -1 ? 0 : findLastVisibleItemPosition) > 0 && findLastVisibleItemPosition > WalletActivity.this.adapter.getItemCount() - 4) {
                        WalletActivity.this.loadTransactions(false);
                    }
                }
            }
        });
        this.listView.setOnItemClickListener(new -$$Lambda$WalletActivity$J-hgkEvwdweERo9LOhPCx89bmcY(this));
        updateTime(false);
        return this.fragmentView;
    }

    public /* synthetic */ void lambda$createView$3$WalletActivity(View view, int i) {
        if (getParentActivity() != null && (view instanceof WalletTransactionCell)) {
            WalletTransactionCell walletTransactionCell = (WalletTransactionCell) view;
            if (!walletTransactionCell.isEmpty()) {
                this.walletActionSheet = new WalletActionSheet(this, this.walletAddress, walletTransactionCell.getAddress(), walletTransactionCell.getComment(), walletTransactionCell.getAmount(), walletTransactionCell.getDate(), walletTransactionCell.getStorageFee(), walletTransactionCell.getTransactionFee());
                this.walletActionSheet.setDelegate(new WalletActionSheetDelegate() {
                    public /* synthetic */ void openInvoice(String str, long j) {
                        -CC.$default$openInvoice(this, str, j);
                    }

                    public /* synthetic */ void openQrReader() {
                        -CC.$default$openQrReader(this);
                    }

                    public void openSendToAddress(String str) {
                        if (WalletActivity.this.getTonController().hasPendingTransactions()) {
                            AlertsCreator.showSimpleAlert(WalletActivity.this, LocaleController.getString("Wallet", NUM), LocaleController.getString("WalletPendingWait", NUM));
                            return;
                        }
                        WalletActivity walletActivity = WalletActivity.this;
                        walletActivity.walletActionSheet = new WalletActionSheet(walletActivity, 0, walletActivity.walletAddress);
                        WalletActivity.this.walletActionSheet.setDelegate(WalletActivity.this);
                        WalletActivity.this.walletActionSheet.setRecipientString(str, true);
                        WalletActivity.this.walletActionSheet.setOnDismissListener(new -$$Lambda$WalletActivity$7$yCxzN2z0uKMueQztQl-HyaB_H-c(this));
                        WalletActivity.this.walletActionSheet.show();
                    }

                    public /* synthetic */ void lambda$openSendToAddress$0$WalletActivity$7(DialogInterface dialogInterface) {
                        if (WalletActivity.this.walletActionSheet == dialogInterface) {
                            WalletActivity.this.walletActionSheet = null;
                        }
                    }
                });
                showDialog(this.walletActionSheet, new -$$Lambda$WalletActivity$vBHa8wRrF5O7692Gup3HxFeAGuU(this));
            }
        }
    }

    public /* synthetic */ void lambda$null$2$WalletActivity(DialogInterface dialogInterface) {
        if (this.walletActionSheet == dialogInterface) {
            this.walletActionSheet = null;
        }
    }

    public void onResume() {
        super.onResume();
        WalletActionSheet walletActionSheet = this.walletActionSheet;
        if (walletActionSheet != null) {
            walletActionSheet.onResume();
        }
        Adapter adapter = this.adapter;
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
        scheduleShortPoll();
    }

    public void onPause() {
        super.onPause();
        WalletActionSheet walletActionSheet = this.walletActionSheet;
        if (walletActionSheet != null) {
            walletActionSheet.onPause();
        }
        AndroidUtilities.cancelRunOnUIThread(this.shortPollRunnable);
    }

    public boolean dismissDialogOnPause(Dialog dialog) {
        if (dialog instanceof WalletActionSheet) {
            return false;
        }
        return super.dismissDialogOnPause(dialog);
    }

    public void onActivityResultFragment(int i, int i2, Intent intent) {
        WalletActionSheet walletActionSheet = this.walletActionSheet;
        if (walletActionSheet != null) {
            walletActionSheet.onActivityResultFragment(i, i2, intent);
        }
        ActionBarLayout[] actionBarLayoutArr = this.cameraScanLayout;
        if (actionBarLayoutArr != null && actionBarLayoutArr[0] != null) {
            ((BaseFragment) actionBarLayoutArr[0].fragmentsStack.get(0)).onActivityResultFragment(i, i2, intent);
        }
    }

    public void openQrReader() {
        if (getParentActivity() != null) {
            if (VERSION.SDK_INT >= 23) {
                if (getParentActivity().checkSelfPermission("android.permission.CAMERA") != 0) {
                    getParentActivity().requestPermissions(new String[]{r1}, 34);
                    return;
                }
            }
            processOpenQrReader();
        }
    }

    public void onRequestPermissionsResultFragment(int i, String[] strArr, int[] iArr) {
        if (getParentActivity() != null && i == 34) {
            if (iArr.length <= 0 || iArr[0] != 0) {
                Builder builder = new Builder(getParentActivity());
                builder.setTitle(LocaleController.getString("AppName", NUM));
                builder.setMessage(LocaleController.getString("WalletPermissionNoCamera", NUM));
                builder.setNegativeButton(LocaleController.getString("PermissionOpenSettings", NUM), new -$$Lambda$WalletActivity$Xk2t3CfKq-oCnmaXGQq5oimIBAs(this));
                builder.setPositiveButton(LocaleController.getString("OK", NUM), null);
                builder.show();
            } else {
                processOpenQrReader();
            }
        }
    }

    public /* synthetic */ void lambda$onRequestPermissionsResultFragment$4$WalletActivity(DialogInterface dialogInterface, int i) {
        try {
            Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("package:");
            stringBuilder.append(ApplicationLoader.applicationContext.getPackageName());
            intent.setData(Uri.parse(stringBuilder.toString()));
            getParentActivity().startActivity(intent);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    /* Access modifiers changed, original: protected */
    public void onTransitionAnimationEnd(boolean z, boolean z2) {
        if (z && !z2 && !TextUtils.isEmpty(this.openTransferAfterOpen)) {
            this.walletActionSheet = new WalletActionSheet(this, 0, this.walletAddress);
            this.walletActionSheet.setDelegate(this);
            this.walletActionSheet.parseTonUrl(null, this.openTransferAfterOpen);
            this.walletActionSheet.setOnDismissListener(new -$$Lambda$WalletActivity$VufTS87_xYs03HgifUAEVg5zPEM(this));
            this.walletActionSheet.show();
            this.openTransferAfterOpen = null;
        }
    }

    public /* synthetic */ void lambda$onTransitionAnimationEnd$5$WalletActivity(DialogInterface dialogInterface) {
        if (this.walletActionSheet == dialogInterface) {
            this.walletActionSheet = null;
        }
    }

    private void processOpenQrReader() {
        if (this.walletActionSheet != null) {
            this.cameraScanLayout = CameraScanActivity.showAsSheet(this, new CameraScanActivityDelegate() {
                public /* synthetic */ void didFindMrzInfo(Result result) {
                    CameraScanActivityDelegate.-CC.$default$didFindMrzInfo(this, result);
                }

                public void didFindQr(String str) {
                    WalletActivity.this.walletActionSheet.parseTonUrl(null, str);
                }
            });
        }
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.walletPendingTransactionsChanged) {
            fillTransactions(null, false);
            Adapter adapter = this.adapter;
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
        } else if (i == NotificationCenter.walletSyncProgressChanged) {
            updateTime(false);
        }
    }

    /* Access modifiers changed, original: protected */
    public void onBecomeFullyVisible() {
        super.onBecomeFullyVisible();
        getTonController().isKeyStoreInvalidated(new -$$Lambda$WalletActivity$L-5eg65WzXVtFdUQfDGJe4sJ6Mw(this));
    }

    public /* synthetic */ void lambda$onBecomeFullyVisible$7$WalletActivity(boolean z) {
        if (z && getParentActivity() != null) {
            Builder builder = new Builder(getParentActivity());
            builder.setTitle(LocaleController.getString("Wallet", NUM));
            builder.setMessage(LocaleController.getString("WalletKeystoreInvalidated", NUM));
            builder.setPositiveButton(LocaleController.getString("OK", NUM), null);
            builder.setOnDismissListener(new -$$Lambda$WalletActivity$rJEQ1WMRAYazwEQirlFIbvsUHQ4(this));
            builder.show();
        }
    }

    public /* synthetic */ void lambda$null$6$WalletActivity(DialogInterface dialogInterface) {
        getTonController().cleanup();
        UserConfig userConfig = getUserConfig();
        userConfig.clearTonConfig();
        userConfig.saveConfig(false);
        presentFragment(new WalletCreateActivity(0), true);
    }

    private void updateTime(boolean z) {
        if (this.statusTextView != null) {
            if (this.lastUpdateTime == 0) {
                int syncProgress = getTonController().getSyncProgress();
                if (syncProgress == 0 || syncProgress == 100) {
                    this.statusTextView.setText(LocaleController.getString("WalletUpdating", NUM));
                } else {
                    this.statusTextView.setText(LocaleController.formatString("WalletUpdatingProgress", NUM, Integer.valueOf(syncProgress)));
                }
            } else {
                long currentTime = ((long) getConnectionsManager().getCurrentTime()) - this.lastUpdateTime;
                if (currentTime < 60) {
                    this.statusTextView.setText(LocaleController.getString("WalletUpdatedFewSecondsAgo", NUM));
                } else {
                    String formatPluralString;
                    if (currentTime < 3600) {
                        formatPluralString = LocaleController.formatPluralString("Minutes", (int) (currentTime / 60));
                    } else if (currentTime < 86400) {
                        formatPluralString = LocaleController.formatPluralString("Hours", (int) ((currentTime / 60) / 60));
                    } else {
                        formatPluralString = LocaleController.formatPluralString("Days", (int) (((currentTime / 60) / 60) / 24));
                    }
                    this.statusTextView.setText(LocaleController.formatString("WalletUpdatedTimeAgo", NUM, formatPluralString));
                }
            }
        }
        if (z) {
            AndroidUtilities.runOnUIThread(this.updateTimeRunnable, 60000);
        }
    }

    private InternalTransactionId getLastTransactionId(boolean z) {
        if (!z) {
            RawTransaction rawTransaction = this.lastTransaction;
            if (rawTransaction != null) {
                return rawTransaction.transactionId;
            }
        }
        return TonController.getLastTransactionId(this.accountState);
    }

    private void scheduleShortPoll() {
        if (!this.isPaused) {
            AndroidUtilities.cancelRunOnUIThread(this.shortPollRunnable);
            AndroidUtilities.runOnUIThread(this.shortPollRunnable, 3000);
        }
    }

    private void loadAccountState() {
        if (!this.loadingAccountState) {
            this.loadingAccountState = true;
            AndroidUtilities.cancelRunOnUIThread(this.shortPollRunnable);
            if (this.accountState == null) {
                this.accountState = getTonController().getCachedAccountState();
                fillTransactions(getTonController().getCachedTransactions(), false);
            }
            this.walletAddress = getTonController().getWalletAddress(getUserConfig().tonPublicKey);
            getTonController().getAccountState(new -$$Lambda$WalletActivity$lzOFOMId1vEyD4Ft4kkxXA8SqEU(this));
            Adapter adapter = this.adapter;
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:19:0x0058  */
    /* JADX WARNING: Removed duplicated region for block: B:22:0x0061  */
    /* JADX WARNING: Removed duplicated region for block: B:21:0x005d  */
    public /* synthetic */ void lambda$loadAccountState$8$WalletActivity(drinkless.org.ton.TonApi.GenericAccountState r9) {
        /*
        r8 = this;
        r0 = 0;
        if (r9 == 0) goto L_0x006c;
    L_0x0003:
        r1 = r8.accountState;
        r2 = 1;
        if (r1 == 0) goto L_0x004f;
    L_0x0008:
        r1 = org.telegram.messenger.TonController.getLastTransactionId(r1);
        r3 = org.telegram.messenger.TonController.getLastTransactionId(r9);
        if (r1 == 0) goto L_0x004f;
    L_0x0012:
        if (r3 == 0) goto L_0x004f;
    L_0x0014:
        r4 = r1.lt;
        r6 = r3.lt;
        r1 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1));
        if (r1 != 0) goto L_0x004f;
    L_0x001c:
        r1 = r8.sections;
        r1 = r1.isEmpty();
        if (r1 != 0) goto L_0x004f;
    L_0x0024:
        r1 = r8.sections;
        r3 = r1.size();
        r3 = r3 - r2;
        r1 = r1.get(r3);
        r1 = (java.lang.String) r1;
        r3 = r8.sectionArrays;
        r1 = r3.get(r1);
        r1 = (java.util.ArrayList) r1;
        if (r1 == 0) goto L_0x0050;
    L_0x003b:
        r3 = r1.isEmpty();
        if (r3 != 0) goto L_0x0050;
    L_0x0041:
        r3 = r1.size();
        r3 = r3 - r2;
        r1 = r1.get(r3);
        r1 = (drinkless.org.ton.TonApi.RawTransaction) r1;
        r8.lastTransaction = r1;
        goto L_0x0050;
    L_0x004f:
        r0 = 1;
    L_0x0050:
        r8.accountState = r9;
        r8.accountStateLoaded = r2;
        r1 = r8.adapter;
        if (r1 == 0) goto L_0x005b;
    L_0x0058:
        r1.notifyDataSetChanged();
    L_0x005b:
        if (r0 == 0) goto L_0x0061;
    L_0x005d:
        r8.loadTransactions(r2);
        goto L_0x007a;
    L_0x0061:
        r8.onFinishGettingAccountState();
        r0 = r8.getTonController();
        r0.checkPendingTransactionsForFailure(r9);
        goto L_0x007a;
    L_0x006c:
        r8.loadingAccountState = r0;
        r8.scheduleShortPoll();
        r9 = r8.getParentActivity();
        if (r9 == 0) goto L_0x007a;
    L_0x0077:
        r8.loadAccountState();
    L_0x007a:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Wallet.WalletActivity.lambda$loadAccountState$8$WalletActivity(drinkless.org.ton.TonApi$GenericAccountState):void");
    }

    private void onFinishGettingAccountState() {
        this.lastUpdateTime = TonController.getLastSyncTime(this.accountState);
        updateTime(true);
        this.loadingAccountState = false;
        scheduleShortPoll();
    }

    private boolean fillTransactions(ArrayList<RawTransaction> arrayList, boolean z) {
        int size;
        int i;
        ArrayList<RawTransaction> arrayList2 = arrayList;
        boolean isEmpty = this.sections.isEmpty();
        String str = "pending";
        int i2 = 1;
        int i3 = 0;
        if (!(arrayList2 == null || arrayList.isEmpty() || !z)) {
            RawTransaction rawTransaction = (RawTransaction) arrayList2.get(arrayList.size() - 1);
            size = this.sections.size();
            i = 0;
            while (i < size) {
                if (str.equals(this.sections.get(i))) {
                    i++;
                } else {
                    if (((RawTransaction) ((ArrayList) this.sectionArrays.get((String) this.sections.get(i))).get(0)).utime < rawTransaction.utime) {
                        this.sections.clear();
                        this.sectionArrays.clear();
                        this.transactionsDict.clear();
                        this.lastTransaction = null;
                        this.transactionsEndReached = false;
                        getTonController().clearPendingCache();
                        isEmpty = true;
                    } else {
                        Collections.reverse(arrayList);
                    }
                }
            }
        }
        ArrayList pendingTransactions = getTonController().getPendingTransactions();
        if (pendingTransactions.isEmpty()) {
            if (this.sectionArrays.containsKey(str)) {
                this.sectionArrays.remove(str);
                this.sections.remove(0);
            }
        } else if (!this.sectionArrays.containsKey(str)) {
            this.sections.add(0, str);
            this.sectionArrays.put(str, pendingTransactions);
        }
        if (arrayList2 == null) {
            return false;
        }
        Calendar instance = Calendar.getInstance();
        size = arrayList.size();
        i = 0;
        boolean z2 = false;
        while (i < size) {
            RawTransaction rawTransaction2 = (RawTransaction) arrayList2.get(i);
            if (!this.transactionsDict.containsKey(Long.valueOf(rawTransaction2.transactionId.lt))) {
                instance.setTimeInMillis(rawTransaction2.utime * 1000);
                int i4 = instance.get(6);
                int i5 = instance.get(i2);
                int i6 = instance.get(2);
                String format = String.format(Locale.US, "%d_%02d_%02d", new Object[]{Integer.valueOf(i5), Integer.valueOf(i6), Integer.valueOf(i4)});
                ArrayList arrayList3 = (ArrayList) this.sectionArrays.get(format);
                if (arrayList3 == null) {
                    i5 = this.sections.size();
                    int size2 = this.sections.size();
                    i6 = 0;
                    while (i6 < size2) {
                        if (!str.equals(this.sections.get(i6))) {
                            if (((RawTransaction) ((ArrayList) this.sectionArrays.get((String) this.sections.get(i6))).get(i3)).utime < rawTransaction2.utime) {
                                break;
                            }
                        }
                        i6++;
                        i3 = 0;
                    }
                    i6 = i5;
                    arrayList3 = new ArrayList();
                    this.sections.add(i6, format);
                    this.sectionArrays.put(format, arrayList3);
                }
                if (!z || isEmpty) {
                    arrayList3.add(rawTransaction2);
                } else {
                    arrayList3.add(0, rawTransaction2);
                }
                this.transactionsDict.put(Long.valueOf(rawTransaction2.transactionId.lt), rawTransaction2);
                z2 = true;
            }
            i++;
            i2 = 1;
            i3 = 0;
        }
        return z2;
    }

    private void loadTransactions(boolean z) {
        if (!this.loadingTransactions && this.accountStateLoaded) {
            this.loadingTransactions = true;
            getTonController().getTransactions(z, getLastTransactionId(z));
        }
    }

    private void showInvoiceSheet(String str, long j) {
        String str2 = str;
        if (getParentActivity() != null) {
            Activity parentActivity = getParentActivity();
            BottomSheet.Builder builder = new BottomSheet.Builder(parentActivity);
            builder.setApplyBottomPadding(false);
            builder.setApplyTopPadding(false);
            builder.setUseFullWidth(false);
            AnonymousClass9 anonymousClass9 = new FrameLayout(parentActivity) {
                /* Access modifiers changed, original: protected */
                public void onMeasure(int i, int i2) {
                    super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(i), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(472.0f), NUM));
                }
            };
            String str3 = "dialogTextBlack";
            if (j == 0) {
                ActionBarMenuItem actionBarMenuItem = new ActionBarMenuItem(parentActivity, null, 0, Theme.getColor(str3));
                actionBarMenuItem.setLongClickEnabled(false);
                actionBarMenuItem.setIcon(NUM);
                actionBarMenuItem.setContentDescription(LocaleController.getString("AccDescrMoreOptions", NUM));
                actionBarMenuItem.addSubItem(1, LocaleController.getString("WalletCopyAddress", NUM));
                actionBarMenuItem.addSubItem(2, LocaleController.getString("WalletCreateInvoice", NUM));
                actionBarMenuItem.setSubMenuOpenSide(2);
                actionBarMenuItem.setDelegate(new -$$Lambda$WalletActivity$RhSn0vRs2B8-NN4eTvkKIjlDheY(this, builder, str2));
                actionBarMenuItem.setTranslationX((float) AndroidUtilities.dp(6.0f));
                actionBarMenuItem.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor("dialogButtonSelector"), 6));
                anonymousClass9.addView(actionBarMenuItem, LayoutHelper.createFrame(48, 48.0f, 53, 0.0f, 12.0f, 10.0f, 0.0f));
                actionBarMenuItem.setOnClickListener(new -$$Lambda$WalletActivity$YQSDTJfOW1NPmC_UP6kyldkDRIA(actionBarMenuItem));
            }
            TextView textView = new TextView(parentActivity);
            textView.setLines(1);
            textView.setSingleLine(true);
            if (j == 0) {
                textView.setText(LocaleController.getString("WalletReceiveYourAddress", NUM));
            } else {
                textView.setText(LocaleController.getString("WalletYourInvoice", NUM));
            }
            textView.setTextColor(Theme.getColor(str3));
            textView.setTextSize(1, 20.0f);
            String str4 = "fonts/rmedium.ttf";
            textView.setTypeface(AndroidUtilities.getTypeface(str4));
            textView.setEllipsize(TruncateAt.END);
            textView.setGravity(16);
            anonymousClass9.addView(textView, LayoutHelper.createFrame(-2, -2.0f, 51, 21.0f, 22.0f, 21.0f, 0.0f));
            LinearLayout linearLayout = new LinearLayout(parentActivity);
            linearLayout.setOrientation(1);
            anonymousClass9.addView(linearLayout, LayoutHelper.createFrame(-1, -2.0f, 51, 0.0f, 78.0f, 0.0f, 0.0f));
            TextView textView2 = new TextView(parentActivity);
            textView2.setTextColor(Theme.getColor("dialogTextGray2"));
            textView2.setGravity(1);
            textView2.setLineSpacing((float) AndroidUtilities.dp(2.0f), 1.0f);
            textView2.setTextSize(1, 14.0f);
            if (j == 0) {
                textView2.setText(LocaleController.getString("WalletTestShareInfo", NUM));
            } else {
                textView2.setText(AndroidUtilities.replaceTags(LocaleController.formatString("WalletTestShareInvoiceUrlInfo", NUM, TonController.formatCurrency(j))));
            }
            textView2.setPadding(AndroidUtilities.dp(32.0f), 0, AndroidUtilities.dp(32.0f), 0);
            linearLayout.addView(textView2, LayoutHelper.createLinear(-2, -2, 49, 0, 0, 0, 0));
            TextView textView3 = new TextView(parentActivity);
            ImageView imageView = new ImageView(parentActivity);
            imageView.setImageBitmap(getTonController().createTonQR(parentActivity, str2, null));
            linearLayout.addView(imageView, LayoutHelper.createLinear(190, 190, 49, 0, 16, 0, 0));
            imageView.setOnLongClickListener(new -$$Lambda$WalletActivity$xe-rvVOa-uaGihadyWRFqXeD2jk(this, str2));
            textView3.setTextSize(1, 17.0f);
            textView3.setTypeface(AndroidUtilities.getTypeface("fonts/rmono.ttf"));
            textView3.setTextColor(Theme.getColor(str3));
            StringBuilder stringBuilder = new StringBuilder(this.walletAddress);
            stringBuilder.insert(stringBuilder.length() / 2, 10);
            textView3.setText(stringBuilder);
            linearLayout.addView(textView3, LayoutHelper.createLinear(-2, -2, 1, 0, 16, 0, 0));
            textView3.setOnLongClickListener(new -$$Lambda$WalletActivity$j16-gEjkDeSXtg_1GL2QbZ0E_Kg(this, str2));
            TextView textView4 = new TextView(parentActivity);
            textView4.setPadding(AndroidUtilities.dp(34.0f), 0, AndroidUtilities.dp(34.0f), 0);
            textView4.setGravity(17);
            textView4.setTextColor(Theme.getColor("featuredStickers_buttonText"));
            textView4.setTextSize(1, 14.0f);
            if (j == 0) {
                textView4.setText(LocaleController.getString("WalletShareAddress", NUM));
            } else {
                textView4.setText(LocaleController.getString("WalletShareInvoiceUrl", NUM));
            }
            textView4.setTypeface(AndroidUtilities.getTypeface(str4));
            textView4.setBackground(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(4.0f), Theme.getColor("featuredStickers_addButton"), Theme.getColor("featuredStickers_addButtonPressed")));
            linearLayout.addView(textView4, LayoutHelper.createLinear(-1, 42, 51, 16, 20, 16, 16));
            textView4.setOnClickListener(new -$$Lambda$WalletActivity$YHM5zd56Ke-yRfMoyobmSmZiihA(this, str2));
            ScrollView scrollView = new ScrollView(parentActivity);
            scrollView.setVerticalScrollBarEnabled(false);
            scrollView.addView(anonymousClass9, LayoutHelper.createScroll(-1, -2, 51));
            if (VERSION.SDK_INT >= 21) {
                scrollView.setNestedScrollingEnabled(true);
            }
            builder.setCustomView(scrollView);
            BottomSheet create = builder.create();
            create.setCanDismissWithSwipe(false);
            showDialog(create);
        }
    }

    public /* synthetic */ void lambda$showInvoiceSheet$10$WalletActivity(BottomSheet.Builder builder, String str, int i) {
        builder.getDismissRunnable().run();
        if (i == 1) {
            AndroidUtilities.addToClipboard(str);
            Toast.makeText(getParentActivity(), LocaleController.getString("LinkCopied", NUM), 0).show();
        } else if (i == 2) {
            this.walletActionSheet = new WalletActionSheet(this, 1, this.walletAddress);
            this.walletActionSheet.setDelegate(new WalletActionSheetDelegate() {
                public /* synthetic */ void openQrReader() {
                    -CC.$default$openQrReader(this);
                }

                public /* synthetic */ void openSendToAddress(String str) {
                    -CC.$default$openSendToAddress(this, str);
                }

                public void openInvoice(String str, long j) {
                    WalletActivity.this.showInvoiceSheet(str, j);
                }
            });
            this.walletActionSheet.setOnDismissListener(new -$$Lambda$WalletActivity$Sx3AP-RJe1uxt4_889O9LY6XNdY(this));
            this.walletActionSheet.show();
        }
    }

    public /* synthetic */ void lambda$null$9$WalletActivity(DialogInterface dialogInterface) {
        if (this.walletActionSheet == dialogInterface) {
            this.walletActionSheet = null;
        }
    }

    public /* synthetic */ boolean lambda$showInvoiceSheet$12$WalletActivity(String str, View view) {
        TonController.shareBitmap(getParentActivity(), view, str);
        return true;
    }

    public /* synthetic */ boolean lambda$showInvoiceSheet$13$WalletActivity(String str, View view) {
        AndroidUtilities.addToClipboard(str);
        Toast.makeText(getParentActivity(), LocaleController.getString("WalletTransactionAddressCopied", NUM), 0).show();
        return true;
    }

    public /* synthetic */ void lambda$showInvoiceSheet$14$WalletActivity(String str, View view) {
        AndroidUtilities.openSharing(this, str);
    }
}
