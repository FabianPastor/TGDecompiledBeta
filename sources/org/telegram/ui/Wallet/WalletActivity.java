package org.telegram.ui.Wallet;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.text.Editable;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import drinkless.org.ton.TonApi;
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
import org.telegram.messenger.MrzRecognizer;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.TonController;
import org.telegram.messenger.UserConfig;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarLayout;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.CameraScanActivity;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.PullForegroundDrawable;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Wallet.WalletActionSheet;
import org.telegram.ui.Wallet.WalletActivity;

public class WalletActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate, WalletActionSheet.WalletActionSheetDelegate {
    public static final int CAMERA_PERMISSION_REQUEST_CODE = 34;
    private static final String PENDING_KEY = "pending";
    private static final int SHORT_POLL_DELAY = 3000;
    private static final int menu_settings = 1;
    public static float viewOffset;
    /* access modifiers changed from: private */
    public TonApi.GenericAccountState accountState;
    private boolean accountStateLoaded;
    /* access modifiers changed from: private */
    public Adapter adapter;
    /* access modifiers changed from: private */
    public GradientDrawable backgroundDrawable;
    /* access modifiers changed from: private */
    public Paint blackPaint;
    private ActionBarLayout[] cameraScanLayout;
    /* access modifiers changed from: private */
    public boolean canShowHiddenPull;
    private TonApi.RawTransaction lastTransaction;
    /* access modifiers changed from: private */
    public long lastUpdateTime;
    /* access modifiers changed from: private */
    public LinearLayoutManager layoutManager;
    /* access modifiers changed from: private */
    public PullRecyclerView listView;
    private boolean loadingAccountState;
    /* access modifiers changed from: private */
    public boolean loadingTransactions;
    private String openTransferAfterOpen;
    /* access modifiers changed from: private */
    public View paddingView;
    private Drawable pinnedHeaderShadowDrawable;
    /* access modifiers changed from: private */
    public PullForegroundDrawable pullForegroundDrawable;
    /* access modifiers changed from: private */
    public float[] radii;
    /* access modifiers changed from: private */
    public HashMap<String, ArrayList<TonApi.RawTransaction>> sectionArrays;
    /* access modifiers changed from: private */
    public ArrayList<String> sections;
    private Runnable shortPollRunnable;
    /* access modifiers changed from: private */
    public long startArchivePullingTime;
    /* access modifiers changed from: private */
    public SimpleTextView statusTextView;
    private HashMap<Long, TonApi.RawTransaction> transactionsDict;
    /* access modifiers changed from: private */
    public boolean transactionsEndReached;
    /* access modifiers changed from: private */
    public Runnable updateTimeRunnable;
    /* access modifiers changed from: private */
    public WalletActionSheet walletActionSheet;
    /* access modifiers changed from: private */
    public String walletAddress;
    /* access modifiers changed from: private */
    public boolean wasPulled;

    public /* synthetic */ void openInvoice(String str, long j) {
        WalletActionSheet.WalletActionSheetDelegate.CC.$default$openInvoice(this, str, j);
    }

    public /* synthetic */ void openSendToAddress(String str) {
        WalletActionSheet.WalletActionSheetDelegate.CC.$default$openSendToAddress(this, str);
    }

    public /* synthetic */ void lambda$new$0$WalletActivity() {
        updateTime(true);
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

        /* access modifiers changed from: protected */
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

        public void setAdapter(RecyclerView.Adapter adapter) {
            super.setAdapter(adapter);
            this.firstLayout = true;
        }

        public void addView(View view, int i, ViewGroup.LayoutParams layoutParams) {
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
            int findFirstVisibleItemPosition;
            int action = motionEvent.getAction();
            if (action == 0) {
                WalletActivity.this.listView.setOverScrollMode(0);
                if (WalletActivity.this.wasPulled) {
                    boolean unused = WalletActivity.this.wasPulled = false;
                    if (WalletActivity.this.pullForegroundDrawable != null) {
                        WalletActivity.this.pullForegroundDrawable.doNotShow();
                    }
                    boolean unused2 = WalletActivity.this.canShowHiddenPull = false;
                }
            }
            boolean onTouchEvent = super.onTouchEvent(motionEvent);
            if ((action == 1 || action == 3) && (findFirstVisibleItemPosition = WalletActivity.this.layoutManager.findFirstVisibleItemPosition()) == 0) {
                View findViewByPosition = WalletActivity.this.layoutManager.findViewByPosition(findFirstVisibleItemPosition);
                int dp = (int) (((float) AndroidUtilities.dp(72.0f)) * 0.85f);
                int top = findViewByPosition.getTop() + findViewByPosition.getMeasuredHeight();
                if (findViewByPosition != null) {
                    long currentTimeMillis = System.currentTimeMillis() - WalletActivity.this.startArchivePullingTime;
                    WalletActivity.this.listView.smoothScrollBy(0, top, CubicBezierInterpolator.EASE_OUT_QUINT);
                    if (top >= dp && currentTimeMillis >= 200) {
                        boolean unused3 = WalletActivity.this.wasPulled = true;
                        AndroidUtilities.cancelRunOnUIThread(WalletActivity.this.updateTimeRunnable);
                        long unused4 = WalletActivity.this.lastUpdateTime = 0;
                        WalletActivity.this.loadAccountState();
                        WalletActivity.this.updateTime(false);
                    }
                    float f = WalletActivity.viewOffset;
                    if (f != 0.0f) {
                        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{f, 0.0f});
                        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                                WalletActivity.PullRecyclerView.this.lambda$onTouchEvent$0$WalletActivity$PullRecyclerView(valueAnimator);
                            }
                        });
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
            return onTouchEvent;
        }

        public /* synthetic */ void lambda$onTouchEvent$0$WalletActivity$PullRecyclerView(ValueAnimator valueAnimator) {
            WalletActivity.this.listView.setViewsOffset(((Float) valueAnimator.getAnimatedValue()).floatValue());
        }
    }

    public WalletActivity() {
        this((String) null);
    }

    public WalletActivity(String str) {
        this.blackPaint = new Paint();
        this.transactionsDict = new HashMap<>();
        this.sections = new ArrayList<>();
        this.sectionArrays = new HashMap<>();
        this.updateTimeRunnable = new Runnable() {
            public final void run() {
                WalletActivity.this.lambda$new$0$WalletActivity();
            }
        };
        this.shortPollRunnable = new Runnable() {
            public final void run() {
                WalletActivity.this.loadAccountState();
            }
        };
        this.openTransferAfterOpen = str;
        loadAccountState();
    }

    public boolean onFragmentCreate() {
        getNotificationCenter().addObserver(this, NotificationCenter.walletPendingTransactionsChanged);
        getNotificationCenter().addObserver(this, NotificationCenter.walletSyncProgressChanged);
        getTonController().setTransactionCallback(new TonController.GetTransactionsCallback() {
            public final void run(boolean z, ArrayList arrayList) {
                WalletActivity.this.lambda$onFragmentCreate$1$WalletActivity(z, arrayList);
            }
        });
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
            if (!fillTransactions(arrayList, z) && !z) {
                this.transactionsEndReached = true;
            }
            if (!arrayList.isEmpty() && (this.lastTransaction == null || !z)) {
                this.lastTransaction = (TonApi.RawTransaction) arrayList.get(arrayList.size() - 1);
            }
            Adapter adapter2 = this.adapter;
            if (adapter2 != null) {
                adapter2.notifyDataSetChanged();
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
        getTonController().setTransactionCallback((TonController.GetTransactionsCallback) null);
    }

    /* access modifiers changed from: protected */
    public ActionBar createActionBar(Context context) {
        AnonymousClass1 r0 = new ActionBar(context) {
            /* access modifiers changed from: protected */
            public void onMeasure(int i, int i2) {
                if (Build.VERSION.SDK_INT >= 21 && WalletActivity.this.statusTextView != null) {
                    ((FrameLayout.LayoutParams) WalletActivity.this.statusTextView.getLayoutParams()).topMargin = AndroidUtilities.statusBarHeight;
                }
                super.onMeasure(i, i2);
            }
        };
        if (!BuildVars.TON_WALLET_STANDALONE) {
            r0.setBackButtonImage(NUM);
        }
        r0.setBackgroundColor(Theme.getColor("wallet_blackBackground"));
        r0.setTitleColor(Theme.getColor("wallet_whiteText"));
        r0.setItemsColor(Theme.getColor("wallet_whiteText"), false);
        r0.setItemsBackgroundColor(Theme.getColor("wallet_blackBackgroundSelector"), false);
        r0.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    WalletActivity.this.finishFragment();
                } else if (i == 1) {
                    WalletActivity walletActivity = WalletActivity.this;
                    walletActivity.presentFragment(new WalletSettingsActivity(0, walletActivity));
                }
            }
        });
        r0.createMenu().addItem(1, NUM);
        this.statusTextView = new SimpleTextView(context);
        this.statusTextView.setTextSize(14);
        this.statusTextView.setGravity(17);
        this.statusTextView.setTextColor(Theme.getColor("wallet_statusText"));
        r0.addView(this.statusTextView, LayoutHelper.createFrame(-1, -1.0f, 83, 48.0f, 0.0f, 48.0f, 0.0f));
        return r0;
    }

    public View createView(Context context) {
        this.pullForegroundDrawable = new PullForegroundDrawable(LocaleController.getString("WalletSwipeToRefresh", NUM), LocaleController.getString("WalletReleaseToRefresh", NUM)) {
            /* access modifiers changed from: protected */
            public float getViewOffset() {
                return WalletActivity.this.listView.getViewOffset();
            }
        };
        this.pullForegroundDrawable.setColors("wallet_pullBackground", "wallet_releaseBackground");
        this.pullForegroundDrawable.showHidden();
        this.pullForegroundDrawable.setWillDraw(true);
        this.blackPaint.setColor(Theme.getColor("wallet_blackBackground"));
        this.backgroundDrawable = new GradientDrawable();
        this.backgroundDrawable.setShape(0);
        int dp = AndroidUtilities.dp(13.0f);
        GradientDrawable gradientDrawable = this.backgroundDrawable;
        float f = (float) dp;
        float[] fArr = {f, f, f, f, 0.0f, 0.0f, 0.0f, 0.0f};
        this.radii = fArr;
        gradientDrawable.setCornerRadii(fArr);
        this.backgroundDrawable.setColor(Theme.getColor("wallet_whiteBackground"));
        AnonymousClass4 r0 = new FrameLayout(context) {
            /* access modifiers changed from: protected */
            public void onDraw(Canvas canvas) {
                RecyclerView.ViewHolder findViewHolderForAdapterPosition = WalletActivity.this.listView.findViewHolderForAdapterPosition(1);
                int bottom = findViewHolderForAdapterPosition != null ? findViewHolderForAdapterPosition.itemView.getBottom() : 0;
                float dp = (float) AndroidUtilities.dp(13.0f);
                float f = (float) bottom;
                if (f < dp) {
                    dp *= f / dp;
                }
                int i = (int) (f + WalletActivity.viewOffset);
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
        r0.setWillNotDraw(false);
        this.fragmentView = r0;
        this.pinnedHeaderShadowDrawable = context.getResources().getDrawable(NUM);
        this.pinnedHeaderShadowDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor("windowBackgroundGrayShadow"), PorterDuff.Mode.MULTIPLY));
        this.listView = new PullRecyclerView(context);
        this.listView.setSectionsType(2);
        this.listView.setPinnedHeaderShadowDrawable(this.pinnedHeaderShadowDrawable);
        PullRecyclerView pullRecyclerView = this.listView;
        AnonymousClass5 r5 = new LinearLayoutManager(context, 1, false) {
            /* JADX WARNING: Removed duplicated region for block: B:31:0x00b4  */
            /* JADX WARNING: Removed duplicated region for block: B:32:0x00b8  */
            /* JADX WARNING: Removed duplicated region for block: B:36:0x00cf  */
            /* JADX WARNING: Removed duplicated region for block: B:39:0x00e4  */
            /* JADX WARNING: Removed duplicated region for block: B:42:0x00f2 A[ADDED_TO_REGION] */
            /* JADX WARNING: Removed duplicated region for block: B:76:0x01d5  */
            /* JADX WARNING: Removed duplicated region for block: B:78:0x01f7  */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public int scrollVerticallyBy(int r13, androidx.recyclerview.widget.RecyclerView.Recycler r14, androidx.recyclerview.widget.RecyclerView.State r15) {
                /*
                    r12 = this;
                    org.telegram.ui.Wallet.WalletActivity r0 = org.telegram.ui.Wallet.WalletActivity.this
                    org.telegram.ui.Wallet.WalletActivity$PullRecyclerView r0 = r0.listView
                    int r0 = r0.getScrollState()
                    r1 = 0
                    r2 = 1
                    if (r0 != r2) goto L_0x0010
                    r0 = 1
                    goto L_0x0011
                L_0x0010:
                    r0 = 0
                L_0x0011:
                    r3 = -1
                    r4 = 2
                    r5 = 1065353216(0x3var_, float:1.0)
                    if (r13 >= 0) goto L_0x00a0
                    org.telegram.ui.Wallet.WalletActivity r6 = org.telegram.ui.Wallet.WalletActivity.this
                    org.telegram.ui.Wallet.WalletActivity$PullRecyclerView r6 = r6.listView
                    r6.setOverScrollMode(r1)
                    org.telegram.ui.Wallet.WalletActivity r6 = org.telegram.ui.Wallet.WalletActivity.this
                    androidx.recyclerview.widget.LinearLayoutManager r6 = r6.layoutManager
                    int r6 = r6.findFirstVisibleItemPosition()
                    if (r6 != 0) goto L_0x0043
                    org.telegram.ui.Wallet.WalletActivity r7 = org.telegram.ui.Wallet.WalletActivity.this
                    androidx.recyclerview.widget.LinearLayoutManager r7 = r7.layoutManager
                    android.view.View r7 = r7.findViewByPosition(r6)
                    if (r7 == 0) goto L_0x0043
                    int r7 = r7.getBottom()
                    int r8 = org.telegram.messenger.AndroidUtilities.dp(r5)
                    if (r7 > r8) goto L_0x0043
                    r6 = 1
                L_0x0043:
                    if (r0 != 0) goto L_0x0067
                    org.telegram.ui.Wallet.WalletActivity r3 = org.telegram.ui.Wallet.WalletActivity.this
                    androidx.recyclerview.widget.LinearLayoutManager r3 = r3.layoutManager
                    android.view.View r3 = r3.findViewByPosition(r6)
                    r7 = 1116733440(0x42900000, float:72.0)
                    int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
                    int r7 = r7 + r2
                    int r3 = r3.getTop()
                    int r3 = -r3
                    int r6 = r6 - r2
                    int r6 = r6 * r7
                    int r3 = r3 + r6
                    int r6 = java.lang.Math.abs(r13)
                    if (r3 >= r6) goto L_0x00a0
                    int r3 = -r3
                    goto L_0x00a1
                L_0x0067:
                    if (r6 != 0) goto L_0x00a0
                    org.telegram.ui.Wallet.WalletActivity r7 = org.telegram.ui.Wallet.WalletActivity.this
                    androidx.recyclerview.widget.LinearLayoutManager r7 = r7.layoutManager
                    android.view.View r6 = r7.findViewByPosition(r6)
                    int r7 = r6.getTop()
                    float r7 = (float) r7
                    int r6 = r6.getMeasuredHeight()
                    float r6 = (float) r6
                    float r7 = r7 / r6
                    float r6 = r7 + r5
                    int r7 = (r6 > r5 ? 1 : (r6 == r5 ? 0 : -1))
                    if (r7 <= 0) goto L_0x0086
                    r6 = 1065353216(0x3var_, float:1.0)
                L_0x0086:
                    org.telegram.ui.Wallet.WalletActivity r7 = org.telegram.ui.Wallet.WalletActivity.this
                    org.telegram.ui.Wallet.WalletActivity$PullRecyclerView r7 = r7.listView
                    r7.setOverScrollMode(r4)
                    float r7 = (float) r13
                    r8 = 1055286886(0x3ee66666, float:0.45)
                    r9 = 1048576000(0x3e800000, float:0.25)
                    float r6 = r6 * r9
                    float r8 = r8 - r6
                    float r7 = r7 * r8
                    int r6 = (int) r7
                    if (r6 <= r3) goto L_0x009e
                    goto L_0x00a1
                L_0x009e:
                    r3 = r6
                    goto L_0x00a1
                L_0x00a0:
                    r3 = r13
                L_0x00a1:
                    float r6 = org.telegram.ui.Wallet.WalletActivity.viewOffset
                    r7 = 0
                    int r8 = (r6 > r7 ? 1 : (r6 == r7 ? 0 : -1))
                    if (r8 == 0) goto L_0x00c3
                    if (r13 <= 0) goto L_0x00c3
                    if (r0 == 0) goto L_0x00c3
                    int r3 = (int) r6
                    float r3 = (float) r3
                    float r6 = (float) r13
                    float r3 = r3 - r6
                    int r6 = (r3 > r7 ? 1 : (r3 == r7 ? 0 : -1))
                    if (r6 >= 0) goto L_0x00b8
                    int r3 = (int) r3
                    r6 = r3
                    r3 = 0
                    goto L_0x00b9
                L_0x00b8:
                    r6 = 0
                L_0x00b9:
                    org.telegram.ui.Wallet.WalletActivity r8 = org.telegram.ui.Wallet.WalletActivity.this
                    org.telegram.ui.Wallet.WalletActivity$PullRecyclerView r8 = r8.listView
                    r8.setViewsOffset(r3)
                    r3 = r6
                L_0x00c3:
                    int r14 = super.scrollVerticallyBy(r3, r14, r15)
                    org.telegram.ui.Wallet.WalletActivity r15 = org.telegram.ui.Wallet.WalletActivity.this
                    org.telegram.ui.Components.PullForegroundDrawable r15 = r15.pullForegroundDrawable
                    if (r15 == 0) goto L_0x00d7
                    org.telegram.ui.Wallet.WalletActivity r15 = org.telegram.ui.Wallet.WalletActivity.this
                    org.telegram.ui.Components.PullForegroundDrawable r15 = r15.pullForegroundDrawable
                    r15.scrollDy = r14
                L_0x00d7:
                    org.telegram.ui.Wallet.WalletActivity r15 = org.telegram.ui.Wallet.WalletActivity.this
                    androidx.recyclerview.widget.LinearLayoutManager r15 = r15.layoutManager
                    int r15 = r15.findFirstVisibleItemPosition()
                    r6 = 0
                    if (r15 != 0) goto L_0x00ee
                    org.telegram.ui.Wallet.WalletActivity r6 = org.telegram.ui.Wallet.WalletActivity.this
                    androidx.recyclerview.widget.LinearLayoutManager r6 = r6.layoutManager
                    android.view.View r6 = r6.findViewByPosition(r15)
                L_0x00ee:
                    r8 = 0
                    if (r15 != 0) goto L_0x01c3
                    if (r6 == 0) goto L_0x01c3
                    int r15 = r6.getBottom()
                    r10 = 1082130432(0x40800000, float:4.0)
                    int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
                    if (r15 < r10) goto L_0x01c3
                    org.telegram.ui.Wallet.WalletActivity r15 = org.telegram.ui.Wallet.WalletActivity.this
                    long r10 = r15.startArchivePullingTime
                    int r15 = (r10 > r8 ? 1 : (r10 == r8 ? 0 : -1))
                    if (r15 != 0) goto L_0x0113
                    org.telegram.ui.Wallet.WalletActivity r15 = org.telegram.ui.Wallet.WalletActivity.this
                    long r7 = java.lang.System.currentTimeMillis()
                    long unused = r15.startArchivePullingTime = r7
                L_0x0113:
                    org.telegram.ui.Wallet.WalletActivity r15 = org.telegram.ui.Wallet.WalletActivity.this
                    org.telegram.ui.Components.PullForegroundDrawable r15 = r15.pullForegroundDrawable
                    if (r15 == 0) goto L_0x0124
                    org.telegram.ui.Wallet.WalletActivity r15 = org.telegram.ui.Wallet.WalletActivity.this
                    org.telegram.ui.Components.PullForegroundDrawable r15 = r15.pullForegroundDrawable
                    r15.showHidden()
                L_0x0124:
                    int r15 = r6.getTop()
                    float r15 = (float) r15
                    int r7 = r6.getMeasuredHeight()
                    float r7 = (float) r7
                    float r15 = r15 / r7
                    float r15 = r15 + r5
                    int r7 = (r15 > r5 ? 1 : (r15 == r5 ? 0 : -1))
                    if (r7 <= 0) goto L_0x0136
                    r15 = 1065353216(0x3var_, float:1.0)
                L_0x0136:
                    long r7 = java.lang.System.currentTimeMillis()
                    org.telegram.ui.Wallet.WalletActivity r9 = org.telegram.ui.Wallet.WalletActivity.this
                    long r9 = r9.startArchivePullingTime
                    long r7 = r7 - r9
                    r9 = 1062836634(0x3var_a, float:0.85)
                    int r9 = (r15 > r9 ? 1 : (r15 == r9 ? 0 : -1))
                    if (r9 <= 0) goto L_0x014f
                    r9 = 220(0xdc, double:1.087E-321)
                    int r11 = (r7 > r9 ? 1 : (r7 == r9 ? 0 : -1))
                    if (r11 <= 0) goto L_0x014f
                    r1 = 1
                L_0x014f:
                    org.telegram.ui.Wallet.WalletActivity r2 = org.telegram.ui.Wallet.WalletActivity.this
                    boolean r2 = r2.canShowHiddenPull
                    if (r2 == r1) goto L_0x017f
                    org.telegram.ui.Wallet.WalletActivity r2 = org.telegram.ui.Wallet.WalletActivity.this
                    boolean unused = r2.canShowHiddenPull = r1
                    org.telegram.ui.Wallet.WalletActivity r2 = org.telegram.ui.Wallet.WalletActivity.this
                    boolean r2 = r2.wasPulled
                    if (r2 != 0) goto L_0x017f
                    org.telegram.ui.Wallet.WalletActivity r2 = org.telegram.ui.Wallet.WalletActivity.this
                    org.telegram.ui.Wallet.WalletActivity$PullRecyclerView r2 = r2.listView
                    r7 = 3
                    r2.performHapticFeedback(r7, r4)
                    org.telegram.ui.Wallet.WalletActivity r2 = org.telegram.ui.Wallet.WalletActivity.this
                    org.telegram.ui.Components.PullForegroundDrawable r2 = r2.pullForegroundDrawable
                    if (r2 == 0) goto L_0x017f
                    org.telegram.ui.Wallet.WalletActivity r2 = org.telegram.ui.Wallet.WalletActivity.this
                    org.telegram.ui.Components.PullForegroundDrawable r2 = r2.pullForegroundDrawable
                    r2.colorize(r1)
                L_0x017f:
                    int r3 = r3 - r14
                    if (r3 == 0) goto L_0x01a3
                    if (r13 >= 0) goto L_0x01a3
                    if (r0 == 0) goto L_0x01a3
                    float r0 = org.telegram.ui.Wallet.WalletActivity.viewOffset
                    int r1 = org.telegram.ui.Components.PullForegroundDrawable.getMaxOverscroll()
                    float r1 = (float) r1
                    float r0 = r0 / r1
                    float r5 = r5 - r0
                    float r0 = org.telegram.ui.Wallet.WalletActivity.viewOffset
                    float r13 = (float) r13
                    r1 = 1045220557(0x3e4ccccd, float:0.2)
                    float r13 = r13 * r1
                    float r13 = r13 * r5
                    float r0 = r0 - r13
                    org.telegram.ui.Wallet.WalletActivity r13 = org.telegram.ui.Wallet.WalletActivity.this
                    org.telegram.ui.Wallet.WalletActivity$PullRecyclerView r13 = r13.listView
                    r13.setViewsOffset(r0)
                L_0x01a3:
                    org.telegram.ui.Wallet.WalletActivity r13 = org.telegram.ui.Wallet.WalletActivity.this
                    org.telegram.ui.Components.PullForegroundDrawable r13 = r13.pullForegroundDrawable
                    if (r13 == 0) goto L_0x01f5
                    org.telegram.ui.Wallet.WalletActivity r13 = org.telegram.ui.Wallet.WalletActivity.this
                    org.telegram.ui.Components.PullForegroundDrawable r13 = r13.pullForegroundDrawable
                    r13.pullProgress = r15
                    org.telegram.ui.Wallet.WalletActivity r13 = org.telegram.ui.Wallet.WalletActivity.this
                    org.telegram.ui.Components.PullForegroundDrawable r13 = r13.pullForegroundDrawable
                    org.telegram.ui.Wallet.WalletActivity r15 = org.telegram.ui.Wallet.WalletActivity.this
                    org.telegram.ui.Wallet.WalletActivity$PullRecyclerView r15 = r15.listView
                    r13.setListView(r15)
                    goto L_0x01f5
                L_0x01c3:
                    org.telegram.ui.Wallet.WalletActivity r13 = org.telegram.ui.Wallet.WalletActivity.this
                    long unused = r13.startArchivePullingTime = r8
                    org.telegram.ui.Wallet.WalletActivity r13 = org.telegram.ui.Wallet.WalletActivity.this
                    boolean unused = r13.canShowHiddenPull = r1
                    org.telegram.ui.Wallet.WalletActivity r13 = org.telegram.ui.Wallet.WalletActivity.this
                    org.telegram.ui.Components.PullForegroundDrawable r13 = r13.pullForegroundDrawable
                    if (r13 == 0) goto L_0x01f5
                    org.telegram.ui.Wallet.WalletActivity r13 = org.telegram.ui.Wallet.WalletActivity.this
                    org.telegram.ui.Components.PullForegroundDrawable r13 = r13.pullForegroundDrawable
                    r13.resetText()
                    org.telegram.ui.Wallet.WalletActivity r13 = org.telegram.ui.Wallet.WalletActivity.this
                    org.telegram.ui.Components.PullForegroundDrawable r13 = r13.pullForegroundDrawable
                    r13.pullProgress = r7
                    org.telegram.ui.Wallet.WalletActivity r13 = org.telegram.ui.Wallet.WalletActivity.this
                    org.telegram.ui.Components.PullForegroundDrawable r13 = r13.pullForegroundDrawable
                    org.telegram.ui.Wallet.WalletActivity r15 = org.telegram.ui.Wallet.WalletActivity.this
                    org.telegram.ui.Wallet.WalletActivity$PullRecyclerView r15 = r15.listView
                    r13.setListView(r15)
                L_0x01f5:
                    if (r6 == 0) goto L_0x01fa
                    r6.invalidate()
                L_0x01fa:
                    return r14
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Wallet.WalletActivity.AnonymousClass5.scrollVerticallyBy(int, androidx.recyclerview.widget.RecyclerView$Recycler, androidx.recyclerview.widget.RecyclerView$State):int");
            }
        };
        this.layoutManager = r5;
        pullRecyclerView.setLayoutManager(r5);
        PullRecyclerView pullRecyclerView2 = this.listView;
        Adapter adapter2 = new Adapter(context);
        this.adapter = adapter2;
        pullRecyclerView2.setAdapter(adapter2);
        this.listView.setGlowColor(Theme.getColor("wallet_blackBackground"));
        r0.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
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
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
            public final void onItemClick(View view, int i) {
                WalletActivity.this.lambda$createView$3$WalletActivity(view, i);
            }
        });
        updateTime(false);
        return this.fragmentView;
    }

    public /* synthetic */ void lambda$createView$3$WalletActivity(View view, int i) {
        if (getParentActivity() != null && (view instanceof WalletTransactionCell)) {
            WalletTransactionCell walletTransactionCell = (WalletTransactionCell) view;
            if (!walletTransactionCell.isEmpty()) {
                this.walletActionSheet = new WalletActionSheet(this, this.walletAddress, walletTransactionCell.getAddress(), walletTransactionCell.getComment(), walletTransactionCell.getAmount(), walletTransactionCell.getDate(), walletTransactionCell.getStorageFee(), walletTransactionCell.getTransactionFee());
                this.walletActionSheet.setDelegate(new WalletActionSheet.WalletActionSheetDelegate() {
                    public /* synthetic */ void openInvoice(String str, long j) {
                        WalletActionSheet.WalletActionSheetDelegate.CC.$default$openInvoice(this, str, j);
                    }

                    public /* synthetic */ void openQrReader() {
                        WalletActionSheet.WalletActionSheetDelegate.CC.$default$openQrReader(this);
                    }

                    public void openSendToAddress(String str) {
                        if (WalletActivity.this.getTonController().hasPendingTransactions()) {
                            AlertsCreator.showSimpleAlert(WalletActivity.this, LocaleController.getString("Wallet", NUM), LocaleController.getString("WalletPendingWait", NUM));
                            return;
                        }
                        WalletActivity walletActivity = WalletActivity.this;
                        WalletActionSheet unused = walletActivity.walletActionSheet = new WalletActionSheet(walletActivity, 0, walletActivity.walletAddress);
                        WalletActivity.this.walletActionSheet.setDelegate(WalletActivity.this);
                        WalletActivity.this.walletActionSheet.setRecipientString(str, true);
                        WalletActivity.this.walletActionSheet.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            public final void onDismiss(DialogInterface dialogInterface) {
                                WalletActivity.AnonymousClass7.this.lambda$openSendToAddress$0$WalletActivity$7(dialogInterface);
                            }
                        });
                        WalletActivity.this.walletActionSheet.show();
                    }

                    public /* synthetic */ void lambda$openSendToAddress$0$WalletActivity$7(DialogInterface dialogInterface) {
                        if (WalletActivity.this.walletActionSheet == dialogInterface) {
                            WalletActionSheet unused = WalletActivity.this.walletActionSheet = null;
                        }
                    }
                });
                showDialog(this.walletActionSheet, new DialogInterface.OnDismissListener() {
                    public final void onDismiss(DialogInterface dialogInterface) {
                        WalletActivity.this.lambda$null$2$WalletActivity(dialogInterface);
                    }
                });
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
        WalletActionSheet walletActionSheet2 = this.walletActionSheet;
        if (walletActionSheet2 != null) {
            walletActionSheet2.onResume();
        }
        Adapter adapter2 = this.adapter;
        if (adapter2 != null) {
            adapter2.notifyDataSetChanged();
        }
        scheduleShortPoll();
    }

    public void onPause() {
        super.onPause();
        WalletActionSheet walletActionSheet2 = this.walletActionSheet;
        if (walletActionSheet2 != null) {
            walletActionSheet2.onPause();
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
        WalletActionSheet walletActionSheet2 = this.walletActionSheet;
        if (walletActionSheet2 != null) {
            walletActionSheet2.onActivityResultFragment(i, i2, intent);
        }
        ActionBarLayout[] actionBarLayoutArr = this.cameraScanLayout;
        if (actionBarLayoutArr != null && actionBarLayoutArr[0] != null) {
            actionBarLayoutArr[0].fragmentsStack.get(0).onActivityResultFragment(i, i2, intent);
        }
    }

    public void openQrReader() {
        if (getParentActivity() != null) {
            if (Build.VERSION.SDK_INT < 23 || getParentActivity().checkSelfPermission("android.permission.CAMERA") == 0) {
                processOpenQrReader();
                return;
            }
            getParentActivity().requestPermissions(new String[]{"android.permission.CAMERA"}, 34);
        }
    }

    public void onRequestPermissionsResultFragment(int i, String[] strArr, int[] iArr) {
        if (getParentActivity() == null || i != 34) {
            return;
        }
        if (iArr.length <= 0 || iArr[0] != 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
            builder.setTitle(LocaleController.getString("AppName", NUM));
            builder.setMessage(LocaleController.getString("WalletPermissionNoCamera", NUM));
            builder.setNegativeButton(LocaleController.getString("PermissionOpenSettings", NUM), new DialogInterface.OnClickListener() {
                public final void onClick(DialogInterface dialogInterface, int i) {
                    WalletActivity.this.lambda$onRequestPermissionsResultFragment$4$WalletActivity(dialogInterface, i);
                }
            });
            builder.setPositiveButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null);
            builder.show();
            return;
        }
        processOpenQrReader();
    }

    public /* synthetic */ void lambda$onRequestPermissionsResultFragment$4$WalletActivity(DialogInterface dialogInterface, int i) {
        try {
            Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.parse("package:" + ApplicationLoader.applicationContext.getPackageName()));
            getParentActivity().startActivity(intent);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    /* access modifiers changed from: protected */
    public void onTransitionAnimationEnd(boolean z, boolean z2) {
        if (z && !z2 && !TextUtils.isEmpty(this.openTransferAfterOpen)) {
            this.walletActionSheet = new WalletActionSheet(this, 0, this.walletAddress);
            this.walletActionSheet.setDelegate(this);
            this.walletActionSheet.parseTonUrl((Editable) null, this.openTransferAfterOpen);
            this.walletActionSheet.setOnDismissListener(new DialogInterface.OnDismissListener() {
                public final void onDismiss(DialogInterface dialogInterface) {
                    WalletActivity.this.lambda$onTransitionAnimationEnd$5$WalletActivity(dialogInterface);
                }
            });
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
            this.cameraScanLayout = CameraScanActivity.showAsSheet(this, true, new CameraScanActivity.CameraScanActivityDelegate() {
                public /* synthetic */ void didFindMrzInfo(MrzRecognizer.Result result) {
                    CameraScanActivity.CameraScanActivityDelegate.CC.$default$didFindMrzInfo(this, result);
                }

                public void didFindQr(String str) {
                    WalletActivity.this.walletActionSheet.parseTonUrl((Editable) null, str);
                }
            });
        }
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.walletPendingTransactionsChanged) {
            fillTransactions((ArrayList<TonApi.RawTransaction>) null, false);
            Adapter adapter2 = this.adapter;
            if (adapter2 != null) {
                adapter2.notifyDataSetChanged();
            }
        } else if (i == NotificationCenter.walletSyncProgressChanged) {
            updateTime(false);
        }
    }

    /* access modifiers changed from: protected */
    public void onBecomeFullyVisible() {
        super.onBecomeFullyVisible();
        getTonController().isKeyStoreInvalidated(new TonController.BooleanCallback() {
            public final void run(boolean z) {
                WalletActivity.this.lambda$onBecomeFullyVisible$7$WalletActivity(z);
            }
        });
    }

    public /* synthetic */ void lambda$onBecomeFullyVisible$7$WalletActivity(boolean z) {
        if (z && getParentActivity() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
            builder.setTitle(LocaleController.getString("Wallet", NUM));
            builder.setMessage(LocaleController.getString("WalletKeystoreInvalidated", NUM));
            builder.setPositiveButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null);
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                public final void onDismiss(DialogInterface dialogInterface) {
                    WalletActivity.this.lambda$null$6$WalletActivity(dialogInterface);
                }
            });
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

    /* access modifiers changed from: private */
    public void updateTime(boolean z) {
        String str;
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
                    if (currentTime < 3600) {
                        str = LocaleController.formatPluralString("Minutes", (int) (currentTime / 60));
                    } else if (currentTime < 86400) {
                        str = LocaleController.formatPluralString("Hours", (int) ((currentTime / 60) / 60));
                    } else {
                        str = LocaleController.formatPluralString("Days", (int) (((currentTime / 60) / 60) / 24));
                    }
                    this.statusTextView.setText(LocaleController.formatString("WalletUpdatedTimeAgo", NUM, str));
                }
            }
        }
        if (z) {
            AndroidUtilities.runOnUIThread(this.updateTimeRunnable, 60000);
        }
    }

    private TonApi.InternalTransactionId getLastTransactionId(boolean z) {
        TonApi.RawTransaction rawTransaction;
        if (z || (rawTransaction = this.lastTransaction) == null) {
            return TonController.getLastTransactionId(this.accountState);
        }
        return rawTransaction.transactionId;
    }

    private void scheduleShortPoll() {
        if (!this.isPaused) {
            AndroidUtilities.cancelRunOnUIThread(this.shortPollRunnable);
            AndroidUtilities.runOnUIThread(this.shortPollRunnable, 3000);
        }
    }

    /* access modifiers changed from: private */
    public void loadAccountState() {
        if (!this.loadingAccountState) {
            this.loadingAccountState = true;
            AndroidUtilities.cancelRunOnUIThread(this.shortPollRunnable);
            if (this.accountState == null) {
                this.accountState = getTonController().getCachedAccountState();
                fillTransactions(getTonController().getCachedTransactions(), false);
            }
            this.walletAddress = getTonController().getWalletAddress(getUserConfig().tonPublicKey);
            getTonController().getAccountState(new TonController.AccountStateCallback() {
                public final void run(TonApi.GenericAccountState genericAccountState) {
                    WalletActivity.this.lambda$loadAccountState$8$WalletActivity(genericAccountState);
                }
            });
            Adapter adapter2 = this.adapter;
            if (adapter2 != null) {
                adapter2.notifyDataSetChanged();
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:19:0x0058  */
    /* JADX WARNING: Removed duplicated region for block: B:21:0x005d  */
    /* JADX WARNING: Removed duplicated region for block: B:22:0x0061  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$loadAccountState$8$WalletActivity(drinkless.org.ton.TonApi.GenericAccountState r9) {
        /*
            r8 = this;
            r0 = 0
            if (r9 == 0) goto L_0x006c
            drinkless.org.ton.TonApi$GenericAccountState r1 = r8.accountState
            r2 = 1
            if (r1 == 0) goto L_0x004f
            drinkless.org.ton.TonApi$InternalTransactionId r1 = org.telegram.messenger.TonController.getLastTransactionId(r1)
            drinkless.org.ton.TonApi$InternalTransactionId r3 = org.telegram.messenger.TonController.getLastTransactionId(r9)
            if (r1 == 0) goto L_0x004f
            if (r3 == 0) goto L_0x004f
            long r4 = r1.lt
            long r6 = r3.lt
            int r1 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r1 != 0) goto L_0x004f
            java.util.ArrayList<java.lang.String> r1 = r8.sections
            boolean r1 = r1.isEmpty()
            if (r1 != 0) goto L_0x004f
            java.util.ArrayList<java.lang.String> r1 = r8.sections
            int r3 = r1.size()
            int r3 = r3 - r2
            java.lang.Object r1 = r1.get(r3)
            java.lang.String r1 = (java.lang.String) r1
            java.util.HashMap<java.lang.String, java.util.ArrayList<drinkless.org.ton.TonApi$RawTransaction>> r3 = r8.sectionArrays
            java.lang.Object r1 = r3.get(r1)
            java.util.ArrayList r1 = (java.util.ArrayList) r1
            if (r1 == 0) goto L_0x0050
            boolean r3 = r1.isEmpty()
            if (r3 != 0) goto L_0x0050
            int r3 = r1.size()
            int r3 = r3 - r2
            java.lang.Object r1 = r1.get(r3)
            drinkless.org.ton.TonApi$RawTransaction r1 = (drinkless.org.ton.TonApi.RawTransaction) r1
            r8.lastTransaction = r1
            goto L_0x0050
        L_0x004f:
            r0 = 1
        L_0x0050:
            r8.accountState = r9
            r8.accountStateLoaded = r2
            org.telegram.ui.Wallet.WalletActivity$Adapter r1 = r8.adapter
            if (r1 == 0) goto L_0x005b
            r1.notifyDataSetChanged()
        L_0x005b:
            if (r0 == 0) goto L_0x0061
            r8.loadTransactions(r2)
            goto L_0x007a
        L_0x0061:
            r8.onFinishGettingAccountState()
            org.telegram.messenger.TonController r0 = r8.getTonController()
            r0.checkPendingTransactionsForFailure(r9)
            goto L_0x007a
        L_0x006c:
            r8.loadingAccountState = r0
            r8.scheduleShortPoll()
            android.app.Activity r9 = r8.getParentActivity()
            if (r9 == 0) goto L_0x007a
            r8.loadAccountState()
        L_0x007a:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Wallet.WalletActivity.lambda$loadAccountState$8$WalletActivity(drinkless.org.ton.TonApi$GenericAccountState):void");
    }

    private void onFinishGettingAccountState() {
        this.lastUpdateTime = TonController.getLastSyncTime(this.accountState);
        updateTime(true);
        this.loadingAccountState = false;
        scheduleShortPoll();
    }

    private boolean fillTransactions(ArrayList<TonApi.RawTransaction> arrayList, boolean z) {
        ArrayList<TonApi.RawTransaction> arrayList2 = arrayList;
        boolean isEmpty = this.sections.isEmpty();
        int i = 1;
        int i2 = 0;
        if (arrayList2 != null && !arrayList.isEmpty() && z) {
            TonApi.RawTransaction rawTransaction = arrayList2.get(arrayList.size() - 1);
            int size = this.sections.size();
            int i3 = 0;
            while (true) {
                if (i3 >= size) {
                    break;
                } else if ("pending".equals(this.sections.get(i3))) {
                    i3++;
                } else {
                    if (((TonApi.RawTransaction) this.sectionArrays.get(this.sections.get(i3)).get(0)).utime < rawTransaction.utime) {
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
        ArrayList<TonApi.RawTransaction> pendingTransactions = getTonController().getPendingTransactions();
        if (pendingTransactions.isEmpty()) {
            if (this.sectionArrays.containsKey("pending")) {
                this.sectionArrays.remove("pending");
                this.sections.remove(0);
            }
        } else if (!this.sectionArrays.containsKey("pending")) {
            this.sections.add(0, "pending");
            this.sectionArrays.put("pending", pendingTransactions);
        }
        if (arrayList2 == null) {
            return false;
        }
        Calendar instance = Calendar.getInstance();
        int size2 = arrayList.size();
        int i4 = 0;
        boolean z2 = false;
        while (i4 < size2) {
            TonApi.RawTransaction rawTransaction2 = arrayList2.get(i4);
            if (!this.transactionsDict.containsKey(Long.valueOf(rawTransaction2.transactionId.lt))) {
                instance.setTimeInMillis(rawTransaction2.utime * 1000);
                int i5 = instance.get(6);
                int i6 = instance.get(i);
                int i7 = instance.get(2);
                Locale locale = Locale.US;
                Object[] objArr = new Object[3];
                objArr[i2] = Integer.valueOf(i6);
                objArr[i] = Integer.valueOf(i7);
                objArr[2] = Integer.valueOf(i5);
                String format = String.format(locale, "%d_%02d_%02d", objArr);
                ArrayList arrayList3 = this.sectionArrays.get(format);
                if (arrayList3 == null) {
                    int size3 = this.sections.size();
                    int size4 = this.sections.size();
                    int i8 = 0;
                    while (true) {
                        if (i8 >= size4) {
                            i8 = size3;
                            break;
                        }
                        if (!"pending".equals(this.sections.get(i8))) {
                            if (((TonApi.RawTransaction) this.sectionArrays.get(this.sections.get(i8)).get(i2)).utime < rawTransaction2.utime) {
                                break;
                            }
                        }
                        i8++;
                        i2 = 0;
                    }
                    arrayList3 = new ArrayList();
                    this.sections.add(i8, format);
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
            i4++;
            i = 1;
            i2 = 0;
        }
        return z2;
    }

    /* access modifiers changed from: private */
    public void loadTransactions(boolean z) {
        if (!this.loadingTransactions && this.accountStateLoaded) {
            this.loadingTransactions = true;
            getTonController().getTransactions(z, getLastTransactionId(z));
        }
    }

    /* access modifiers changed from: private */
    public void showInvoiceSheet(String str, long j) {
        String str2 = str;
        if (getParentActivity() != null) {
            Activity parentActivity = getParentActivity();
            BottomSheet.Builder builder = new BottomSheet.Builder(parentActivity);
            builder.setApplyBottomPadding(false);
            builder.setApplyTopPadding(false);
            builder.setUseFullWidth(false);
            AnonymousClass9 r5 = new FrameLayout(parentActivity) {
                /* access modifiers changed from: protected */
                public void onMeasure(int i, int i2) {
                    super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(472.0f), NUM));
                }
            };
            if (j == 0) {
                ActionBarMenuItem actionBarMenuItem = new ActionBarMenuItem(parentActivity, (ActionBarMenu) null, 0, Theme.getColor("dialogTextBlack"));
                actionBarMenuItem.setLongClickEnabled(false);
                actionBarMenuItem.setIcon(NUM);
                actionBarMenuItem.setContentDescription(LocaleController.getString("AccDescrMoreOptions", NUM));
                actionBarMenuItem.addSubItem(1, LocaleController.getString("WalletCopyAddress", NUM));
                actionBarMenuItem.addSubItem(2, LocaleController.getString("WalletCreateInvoice", NUM));
                actionBarMenuItem.setSubMenuOpenSide(2);
                actionBarMenuItem.setDelegate(new ActionBarMenuItem.ActionBarMenuItemDelegate(builder, str2) {
                    private final /* synthetic */ BottomSheet.Builder f$1;
                    private final /* synthetic */ String f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void onItemClick(int i) {
                        WalletActivity.this.lambda$showInvoiceSheet$10$WalletActivity(this.f$1, this.f$2, i);
                    }
                });
                actionBarMenuItem.setTranslationX((float) AndroidUtilities.dp(6.0f));
                actionBarMenuItem.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor("dialogButtonSelector"), 6));
                r5.addView(actionBarMenuItem, LayoutHelper.createFrame(48, 48.0f, 53, 0.0f, 12.0f, 10.0f, 0.0f));
                actionBarMenuItem.setOnClickListener(new View.OnClickListener() {
                    public final void onClick(View view) {
                        ActionBarMenuItem.this.toggleSubMenu();
                    }
                });
            }
            TextView textView = new TextView(parentActivity);
            textView.setLines(1);
            textView.setSingleLine(true);
            if (j == 0) {
                textView.setText(LocaleController.getString("WalletReceiveYourAddress", NUM));
            } else {
                textView.setText(LocaleController.getString("WalletYourInvoice", NUM));
            }
            textView.setTextColor(Theme.getColor("dialogTextBlack"));
            textView.setTextSize(1, 20.0f);
            textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            textView.setEllipsize(TextUtils.TruncateAt.END);
            textView.setGravity(16);
            r5.addView(textView, LayoutHelper.createFrame(-2, -2.0f, 51, 21.0f, 22.0f, 21.0f, 0.0f));
            LinearLayout linearLayout = new LinearLayout(parentActivity);
            linearLayout.setOrientation(1);
            r5.addView(linearLayout, LayoutHelper.createFrame(-1, -2.0f, 51, 0.0f, 78.0f, 0.0f, 0.0f));
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
            imageView.setImageBitmap(getTonController().createTonQR(parentActivity, str2, (Bitmap) null));
            linearLayout.addView(imageView, LayoutHelper.createLinear(190, 190, 49, 0, 16, 0, 0));
            imageView.setOnLongClickListener(new View.OnLongClickListener(str2) {
                private final /* synthetic */ String f$1;

                {
                    this.f$1 = r2;
                }

                public final boolean onLongClick(View view) {
                    return WalletActivity.this.lambda$showInvoiceSheet$12$WalletActivity(this.f$1, view);
                }
            });
            textView3.setTextSize(1, 17.0f);
            textView3.setTypeface(AndroidUtilities.getTypeface("fonts/rmono.ttf"));
            textView3.setTextColor(Theme.getColor("dialogTextBlack"));
            StringBuilder sb = new StringBuilder(this.walletAddress);
            sb.insert(sb.length() / 2, 10);
            textView3.setText(sb);
            linearLayout.addView(textView3, LayoutHelper.createLinear(-2, -2, 1, 0, 16, 0, 0));
            textView3.setOnLongClickListener(new View.OnLongClickListener(str2) {
                private final /* synthetic */ String f$1;

                {
                    this.f$1 = r2;
                }

                public final boolean onLongClick(View view) {
                    return WalletActivity.this.lambda$showInvoiceSheet$13$WalletActivity(this.f$1, view);
                }
            });
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
            textView4.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            textView4.setBackground(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(4.0f), Theme.getColor("featuredStickers_addButton"), Theme.getColor("featuredStickers_addButtonPressed")));
            linearLayout.addView(textView4, LayoutHelper.createLinear(-1, 42, 51, 16, 20, 16, 16));
            textView4.setOnClickListener(new View.OnClickListener(str2) {
                private final /* synthetic */ String f$1;

                {
                    this.f$1 = r2;
                }

                public final void onClick(View view) {
                    WalletActivity.this.lambda$showInvoiceSheet$14$WalletActivity(this.f$1, view);
                }
            });
            ScrollView scrollView = new ScrollView(parentActivity);
            scrollView.setVerticalScrollBarEnabled(false);
            scrollView.addView(r5, LayoutHelper.createScroll(-1, -2, 51));
            if (Build.VERSION.SDK_INT >= 21) {
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
            this.walletActionSheet.setDelegate(new WalletActionSheet.WalletActionSheetDelegate() {
                public /* synthetic */ void openQrReader() {
                    WalletActionSheet.WalletActionSheetDelegate.CC.$default$openQrReader(this);
                }

                public /* synthetic */ void openSendToAddress(String str) {
                    WalletActionSheet.WalletActionSheetDelegate.CC.$default$openSendToAddress(this, str);
                }

                public void openInvoice(String str, long j) {
                    WalletActivity.this.showInvoiceSheet(str, j);
                }
            });
            this.walletActionSheet.setOnDismissListener(new DialogInterface.OnDismissListener() {
                public final void onDismiss(DialogInterface dialogInterface) {
                    WalletActivity.this.lambda$null$9$WalletActivity(dialogInterface);
                }
            });
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

    private class Adapter extends RecyclerListView.SectionsAdapter {
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

        public Adapter(Context context2) {
            this.context = context2;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view;
            if (i == 0) {
                view = new WalletBalanceCell(this.context) {
                    /* access modifiers changed from: protected */
                    public void onReceivePressed() {
                        WalletActivity walletActivity = WalletActivity.this;
                        walletActivity.showInvoiceSheet("ton://transfer/" + WalletActivity.this.walletAddress, 0);
                    }

                    /* access modifiers changed from: protected */
                    public void onSendPressed() {
                        if (WalletActivity.this.getTonController().hasPendingTransactions()) {
                            AlertsCreator.showSimpleAlert(WalletActivity.this, LocaleController.getString("Wallet", NUM), LocaleController.getString("WalletPendingWait", NUM));
                            return;
                        }
                        int syncProgress = WalletActivity.this.getTonController().getSyncProgress();
                        if (syncProgress == 0 || syncProgress == 100) {
                            WalletActivity walletActivity = WalletActivity.this;
                            WalletActionSheet unused = walletActivity.walletActionSheet = new WalletActionSheet(walletActivity, 0, walletActivity.walletAddress);
                            WalletActivity.this.walletActionSheet.setDelegate(WalletActivity.this);
                            WalletActivity.this.walletActionSheet.setOnDismissListener(
                            /*  JADX ERROR: Method code generation error
                                jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x007c: INVOKE  
                                  (wrap: org.telegram.ui.Wallet.WalletActionSheet : 0x0073: INVOKE  (r0v15 org.telegram.ui.Wallet.WalletActionSheet) = 
                                  (wrap: org.telegram.ui.Wallet.WalletActivity : 0x0071: IGET  (r0v14 org.telegram.ui.Wallet.WalletActivity) = 
                                  (wrap: org.telegram.ui.Wallet.WalletActivity$Adapter : 0x006f: IGET  (r0v13 org.telegram.ui.Wallet.WalletActivity$Adapter) = 
                                  (r4v0 'this' org.telegram.ui.Wallet.WalletActivity$Adapter$1 A[THIS])
                                 org.telegram.ui.Wallet.WalletActivity.Adapter.1.this$1 org.telegram.ui.Wallet.WalletActivity$Adapter)
                                 org.telegram.ui.Wallet.WalletActivity.Adapter.this$0 org.telegram.ui.Wallet.WalletActivity)
                                 org.telegram.ui.Wallet.WalletActivity.access$2000(org.telegram.ui.Wallet.WalletActivity):org.telegram.ui.Wallet.WalletActionSheet type: STATIC)
                                  (wrap: org.telegram.ui.Wallet.-$$Lambda$WalletActivity$Adapter$1$hpVEDtMablSLzgEdhjXH4iOoeOQ : 0x0079: CONSTRUCTOR  (r1v4 org.telegram.ui.Wallet.-$$Lambda$WalletActivity$Adapter$1$hpVEDtMablSLzgEdhjXH4iOoeOQ) = 
                                  (r4v0 'this' org.telegram.ui.Wallet.WalletActivity$Adapter$1 A[THIS])
                                 call: org.telegram.ui.Wallet.-$$Lambda$WalletActivity$Adapter$1$hpVEDtMablSLzgEdhjXH4iOoeOQ.<init>(org.telegram.ui.Wallet.WalletActivity$Adapter$1):void type: CONSTRUCTOR)
                                 android.app.Dialog.setOnDismissListener(android.content.DialogInterface$OnDismissListener):void type: VIRTUAL in method: org.telegram.ui.Wallet.WalletActivity.Adapter.1.onSendPressed():void, dex: classes.dex
                                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
                                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
                                	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
                                	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
                                	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                                	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                                	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:98)
                                	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:142)
                                	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:62)
                                	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                                	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                                	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                                	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                                	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                                	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
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
                                	at jadx.core.codegen.InsnGen.inlineAnonymousConstructor(InsnGen.java:676)
                                	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:607)
                                	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
                                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
                                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
                                	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
                                	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
                                	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                                	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                                	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:98)
                                	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:142)
                                	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:62)
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
                                	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:78)
                                	at jadx.core.codegen.CodeGen.wrapCodeGen(CodeGen.java:44)
                                	at jadx.core.codegen.CodeGen.generateJavaCode(CodeGen.java:33)
                                	at jadx.core.codegen.CodeGen.generate(CodeGen.java:21)
                                	at jadx.core.ProcessClass.generateCode(ProcessClass.java:61)
                                	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:273)
                                Caused by: jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x0079: CONSTRUCTOR  (r1v4 org.telegram.ui.Wallet.-$$Lambda$WalletActivity$Adapter$1$hpVEDtMablSLzgEdhjXH4iOoeOQ) = 
                                  (r4v0 'this' org.telegram.ui.Wallet.WalletActivity$Adapter$1 A[THIS])
                                 call: org.telegram.ui.Wallet.-$$Lambda$WalletActivity$Adapter$1$hpVEDtMablSLzgEdhjXH4iOoeOQ.<init>(org.telegram.ui.Wallet.WalletActivity$Adapter$1):void type: CONSTRUCTOR in method: org.telegram.ui.Wallet.WalletActivity.Adapter.1.onSendPressed():void, dex: classes.dex
                                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
                                	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                                	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                                	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:787)
                                	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:728)
                                	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
                                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
                                	... 86 more
                                Caused by: jadx.core.utils.exceptions.JadxRuntimeException: Expected class to be processed at this point, class: org.telegram.ui.Wallet.-$$Lambda$WalletActivity$Adapter$1$hpVEDtMablSLzgEdhjXH4iOoeOQ, state: NOT_LOADED
                                	at jadx.core.dex.nodes.ClassNode.ensureProcessed(ClassNode.java:260)
                                	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:606)
                                	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
                                	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                                	... 92 more
                                */
                            /*
                                this = this;
                                org.telegram.ui.Wallet.WalletActivity$Adapter r0 = org.telegram.ui.Wallet.WalletActivity.Adapter.this
                                org.telegram.ui.Wallet.WalletActivity r0 = org.telegram.ui.Wallet.WalletActivity.this
                                org.telegram.messenger.TonController r0 = r0.getTonController()
                                boolean r0 = r0.hasPendingTransactions()
                                r1 = 2131626992(0x7f0e0bf0, float:1.8881236E38)
                                java.lang.String r2 = "Wallet"
                                if (r0 == 0) goto L_0x0028
                                org.telegram.ui.Wallet.WalletActivity$Adapter r0 = org.telegram.ui.Wallet.WalletActivity.Adapter.this
                                org.telegram.ui.Wallet.WalletActivity r0 = org.telegram.ui.Wallet.WalletActivity.this
                                java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
                                r2 = 2131627048(0x7f0e0CLASSNAME, float:1.888135E38)
                                java.lang.String r3 = "WalletPendingWait"
                                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                                org.telegram.ui.Components.AlertsCreator.showSimpleAlert(r0, r1, r2)
                                return
                            L_0x0028:
                                org.telegram.ui.Wallet.WalletActivity$Adapter r0 = org.telegram.ui.Wallet.WalletActivity.Adapter.this
                                org.telegram.ui.Wallet.WalletActivity r0 = org.telegram.ui.Wallet.WalletActivity.this
                                org.telegram.messenger.TonController r0 = r0.getTonController()
                                int r0 = r0.getSyncProgress()
                                if (r0 == 0) goto L_0x004f
                                r3 = 100
                                if (r0 == r3) goto L_0x004f
                                org.telegram.ui.Wallet.WalletActivity$Adapter r0 = org.telegram.ui.Wallet.WalletActivity.Adapter.this
                                org.telegram.ui.Wallet.WalletActivity r0 = org.telegram.ui.Wallet.WalletActivity.this
                                java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
                                r2 = 2131627084(0x7f0e0c4c, float:1.8881422E38)
                                java.lang.String r3 = "WalletSendSyncInProgress"
                                java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
                                org.telegram.ui.Components.AlertsCreator.showSimpleAlert(r0, r1, r2)
                                return
                            L_0x004f:
                                org.telegram.ui.Wallet.WalletActivity$Adapter r0 = org.telegram.ui.Wallet.WalletActivity.Adapter.this
                                org.telegram.ui.Wallet.WalletActivity r0 = org.telegram.ui.Wallet.WalletActivity.this
                                org.telegram.ui.Wallet.WalletActionSheet r1 = new org.telegram.ui.Wallet.WalletActionSheet
                                r2 = 0
                                java.lang.String r3 = r0.walletAddress
                                r1.<init>(r0, r2, r3)
                                org.telegram.ui.Wallet.WalletActionSheet unused = r0.walletActionSheet = r1
                                org.telegram.ui.Wallet.WalletActivity$Adapter r0 = org.telegram.ui.Wallet.WalletActivity.Adapter.this
                                org.telegram.ui.Wallet.WalletActivity r0 = org.telegram.ui.Wallet.WalletActivity.this
                                org.telegram.ui.Wallet.WalletActionSheet r0 = r0.walletActionSheet
                                org.telegram.ui.Wallet.WalletActivity$Adapter r1 = org.telegram.ui.Wallet.WalletActivity.Adapter.this
                                org.telegram.ui.Wallet.WalletActivity r1 = org.telegram.ui.Wallet.WalletActivity.this
                                r0.setDelegate(r1)
                                org.telegram.ui.Wallet.WalletActivity$Adapter r0 = org.telegram.ui.Wallet.WalletActivity.Adapter.this
                                org.telegram.ui.Wallet.WalletActivity r0 = org.telegram.ui.Wallet.WalletActivity.this
                                org.telegram.ui.Wallet.WalletActionSheet r0 = r0.walletActionSheet
                                org.telegram.ui.Wallet.-$$Lambda$WalletActivity$Adapter$1$hpVEDtMablSLzgEdhjXH4iOoeOQ r1 = new org.telegram.ui.Wallet.-$$Lambda$WalletActivity$Adapter$1$hpVEDtMablSLzgEdhjXH4iOoeOQ
                                r1.<init>(r4)
                                r0.setOnDismissListener(r1)
                                org.telegram.ui.Wallet.WalletActivity$Adapter r0 = org.telegram.ui.Wallet.WalletActivity.Adapter.this
                                org.telegram.ui.Wallet.WalletActivity r0 = org.telegram.ui.Wallet.WalletActivity.this
                                org.telegram.ui.Wallet.WalletActionSheet r0 = r0.walletActionSheet
                                r0.show()
                                return
                            */
                            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Wallet.WalletActivity.Adapter.AnonymousClass1.onSendPressed():void");
                        }

                        public /* synthetic */ void lambda$onSendPressed$0$WalletActivity$Adapter$1(DialogInterface dialogInterface) {
                            if (WalletActivity.this.walletActionSheet == dialogInterface) {
                                WalletActionSheet unused = WalletActivity.this.walletActionSheet = null;
                            }
                        }
                    };
                } else if (i == 1) {
                    view = new WalletTransactionCell(this.context);
                } else if (i == 2) {
                    view = new WalletCreatedCell(this.context) {
                        /* access modifiers changed from: protected */
                        public void onMeasure(int i, int i2) {
                            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM), View.MeasureSpec.makeMeasureSpec(Math.max(AndroidUtilities.dp(280.0f), WalletActivity.this.fragmentView.getMeasuredHeight() - AndroidUtilities.dp(242.0f)), NUM));
                        }
                    };
                } else if (i == 3) {
                    view = new WalletDateCell(this.context);
                } else if (i != 4) {
                    view = i != 5 ? new WalletSyncCell(this.context) {
                        /* access modifiers changed from: protected */
                        public void onMeasure(int i, int i2) {
                            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM), View.MeasureSpec.makeMeasureSpec(Math.max(AndroidUtilities.dp(280.0f), WalletActivity.this.fragmentView.getMeasuredHeight() - AndroidUtilities.dp(242.0f)), NUM));
                        }
                    } : WalletActivity.this.paddingView = new View(this.context) {
                        /* access modifiers changed from: protected */
                        public void onMeasure(int i, int i2) {
                            int childCount = WalletActivity.this.listView.getChildCount();
                            int itemCount = WalletActivity.this.adapter.getItemCount();
                            int i3 = 0;
                            for (int i4 = 0; i4 < childCount; i4++) {
                                int childAdapterPosition = WalletActivity.this.listView.getChildAdapterPosition(WalletActivity.this.listView.getChildAt(i4));
                                if (!(childAdapterPosition == 0 || childAdapterPosition == itemCount - 1)) {
                                    i3 += WalletActivity.this.listView.getChildAt(i4).getMeasuredHeight();
                                }
                            }
                            int measuredHeight = WalletActivity.this.fragmentView.getMeasuredHeight() - i3;
                            if (measuredHeight <= 0) {
                                measuredHeight = 0;
                            }
                            setMeasuredDimension(WalletActivity.this.listView.getMeasuredWidth(), measuredHeight);
                        }
                    };
                } else {
                    view = new View(this.context) {
                        /* access modifiers changed from: protected */
                        public void onDraw(Canvas canvas) {
                            WalletActivity.this.pullForegroundDrawable.draw(canvas);
                        }

                        /* access modifiers changed from: protected */
                        public void onMeasure(int i, int i2) {
                            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM), View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(AndroidUtilities.dp(72.0f)), NUM));
                        }
                    };
                    WalletActivity.this.pullForegroundDrawable.setCell(view);
                }
                return new RecyclerListView.Holder(view);
            }

            public void onBindViewHolder(int i, int i2, RecyclerView.ViewHolder viewHolder) {
                int itemViewType = viewHolder.getItemViewType();
                if (itemViewType != 0) {
                    boolean z = false;
                    if (itemViewType == 1) {
                        WalletTransactionCell walletTransactionCell = (WalletTransactionCell) viewHolder.itemView;
                        ArrayList access$2800 = WalletActivity.this.sections;
                        ArrayList arrayList = (ArrayList) WalletActivity.this.sectionArrays.get((String) access$2800.get(i - 1));
                        TonApi.RawTransaction rawTransaction = (TonApi.RawTransaction) arrayList.get(i2 - 1);
                        if (i2 != arrayList.size()) {
                            z = true;
                        }
                        walletTransactionCell.setTransaction(rawTransaction, z);
                    } else if (itemViewType == 2) {
                        ((WalletCreatedCell) viewHolder.itemView).setAddress(WalletActivity.this.walletAddress);
                    } else if (itemViewType == 3) {
                        WalletDateCell walletDateCell = (WalletDateCell) viewHolder.itemView;
                        String str = (String) WalletActivity.this.sections.get(i - 1);
                        if ("pending".equals(str)) {
                            walletDateCell.setText(LocaleController.getString("WalletPendingTransactions", NUM));
                        } else {
                            walletDateCell.setDate(((TonApi.RawTransaction) ((ArrayList) WalletActivity.this.sectionArrays.get(str)).get(0)).utime);
                        }
                    }
                } else {
                    WalletBalanceCell walletBalanceCell = (WalletBalanceCell) viewHolder.itemView;
                    if (WalletActivity.this.getTonController().isWalletLoaded()) {
                        walletBalanceCell.setBalance(TonController.getBalance(WalletActivity.this.accountState));
                    } else {
                        walletBalanceCell.setBalance(-1);
                    }
                }
            }

            public int getItemViewType(int i, int i2) {
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
                        return 3;
                    }
                    return 1;
                }
            }

            public int getSectionCount() {
                if (!WalletActivity.this.sections.isEmpty()) {
                    return 1 + WalletActivity.this.sections.size() + 1;
                }
                return 1;
            }

            public int getCountForSection(int i) {
                if (i == 0) {
                    return WalletActivity.this.sections.isEmpty() ? 3 : 2;
                }
                int i2 = i - 1;
                if (i2 < WalletActivity.this.sections.size()) {
                    return ((ArrayList) WalletActivity.this.sectionArrays.get(WalletActivity.this.sections.get(i2))).size() + 1;
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
                    int i2 = i - 1;
                    if (i2 < WalletActivity.this.sections.size()) {
                        view.setAlpha(1.0f);
                        String str = (String) WalletActivity.this.sections.get(i2);
                        if ("pending".equals(str)) {
                            walletDateCell.setText(LocaleController.getString("WalletPendingTransactions", NUM));
                        } else {
                            walletDateCell.setDate(((TonApi.RawTransaction) ((ArrayList) WalletActivity.this.sectionArrays.get(str)).get(0)).utime);
                        }
                    }
                }
                return view;
            }
        }
    }
