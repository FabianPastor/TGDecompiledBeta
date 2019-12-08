package org.telegram.ui.Wallet;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build.VERSION;
import android.text.TextUtils.TruncateAt;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.OnScrollListener;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import drinkless.org.ton.TonApi.GenericAccountState;
import drinkless.org.ton.TonApi.GenericAccountStateRaw;
import drinkless.org.ton.TonApi.GenericAccountStateTestGiver;
import drinkless.org.ton.TonApi.GenericAccountStateTestWallet;
import drinkless.org.ton.TonApi.GenericAccountStateUninited;
import drinkless.org.ton.TonApi.GenericAccountStateWallet;
import drinkless.org.ton.TonApi.InternalTransactionId;
import drinkless.org.ton.TonApi.RawTransaction;
import java.util.ArrayList;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.TonController;
import org.telegram.messenger.UserConfig;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.BottomSheet.Builder;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.PullForegroundDrawable;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.SectionsAdapter;
import org.telegram.ui.Components.ShareAlert;

public class WalletActivity extends BaseFragment implements NotificationCenterDelegate {
    private static final String PENDING_KEY = "pending";
    private static final int menu_settings = 1;
    public static float viewOffset;
    private GenericAccountState accountState;
    private boolean accountStateLoaded;
    private Adapter adapter;
    private GradientDrawable backgroundDrawable;
    private Paint blackPaint = new Paint();
    private boolean canShowHiddenPull;
    private AlertDialog keyInvalidDialog;
    private RawTransaction lastTransaction;
    private long lastUpdateTime;
    private LinearLayoutManager layoutManager;
    private PullRecyclerView listView;
    private boolean loadingAccountState;
    private boolean loadingTransactions;
    private View paddingView;
    private Drawable pinnedHeaderShadowDrawable;
    private PullForegroundDrawable pullForegroundDrawable;
    private float[] radii;
    private HashMap<String, ArrayList<RawTransaction>> sectionArrays = new HashMap();
    private ArrayList<String> sections = new ArrayList();
    private Runnable shortPollRunnable = new Runnable() {
        public void run() {
            WalletActivity.this.loadAccountState();
            AndroidUtilities.runOnUIThread(WalletActivity.this.shortPollRunnable, 10000);
        }
    };
    private long startArchivePullingTime;
    private SimpleTextView statusTextView;
    private HashMap<Long, RawTransaction> transactionsDict = new HashMap();
    private boolean transactionsEndReached;
    private Runnable updateTimeRunnable = new -$$Lambda$WalletActivity$JbR_f3lgnrp3kgfDQnFSBGZNQwI(this);
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
                        Builder builder = new Builder(Adapter.this.context);
                        builder.setApplyBottomPadding(false);
                        builder.setApplyTopPadding(false);
                        builder.setUseFullWidth(false);
                        AnonymousClass1 anonymousClass1 = new FrameLayout(Adapter.this.context) {
                            /* Access modifiers changed, original: protected */
                            public void onMeasure(int i, int i2) {
                                super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(i), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(472.0f), NUM));
                            }
                        };
                        String str = "dialogTextBlack";
                        ActionBarMenuItem actionBarMenuItem = new ActionBarMenuItem(Adapter.this.context, null, 0, Theme.getColor(str));
                        actionBarMenuItem.setLongClickEnabled(false);
                        actionBarMenuItem.setIcon(NUM);
                        actionBarMenuItem.setContentDescription(LocaleController.getString("AccDescrMoreOptions", NUM));
                        actionBarMenuItem.addSubItem(1, LocaleController.getString("WalletCopyAddress", NUM));
                        actionBarMenuItem.addSubItem(2, LocaleController.getString("WalletCreateInvoice", NUM));
                        actionBarMenuItem.setSubMenuOpenSide(2);
                        actionBarMenuItem.setDelegate(new -$$Lambda$WalletActivity$Adapter$1$8W2rdFTTImIt1RW0bcrsdz9gi1Q(this, builder));
                        actionBarMenuItem.setTranslationX((float) AndroidUtilities.dp(6.0f));
                        actionBarMenuItem.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor("dialogButtonSelector"), 6));
                        anonymousClass1.addView(actionBarMenuItem, LayoutHelper.createFrame(48, 48.0f, 53, 0.0f, 12.0f, 10.0f, 0.0f));
                        actionBarMenuItem.setOnClickListener(new -$$Lambda$WalletActivity$Adapter$1$wIKIfH9eMhC8H1b2e3axPPqXQ5c(actionBarMenuItem));
                        TextView textView = new TextView(getContext());
                        textView.setLines(1);
                        textView.setSingleLine(true);
                        textView.setText(LocaleController.getString("WalletReceiveYourAddress", NUM));
                        textView.setTextColor(Theme.getColor(str));
                        textView.setTextSize(1, 20.0f);
                        String str2 = "fonts/rmedium.ttf";
                        textView.setTypeface(AndroidUtilities.getTypeface(str2));
                        textView.setEllipsize(TruncateAt.END);
                        textView.setGravity(16);
                        anonymousClass1.addView(textView, LayoutHelper.createFrame(-2, -2.0f, 51, 21.0f, 22.0f, 21.0f, 0.0f));
                        textView = new TextView(Adapter.this.context);
                        textView.setTextColor(Theme.getColor("dialogTextGray2"));
                        textView.setGravity(1);
                        textView.setLineSpacing((float) AndroidUtilities.dp(2.0f), 1.0f);
                        textView.setTextSize(1, 14.0f);
                        textView.setText(LocaleController.getString("WalletShareInfo", NUM));
                        textView.setPadding(AndroidUtilities.dp(32.0f), 0, AndroidUtilities.dp(32.0f), 0);
                        anonymousClass1.addView(textView, LayoutHelper.createFrame(-2, -2.0f, 49, 0.0f, 78.0f, 0.0f, 0.0f));
                        textView = new TextView(Adapter.this.context);
                        ImageView imageView = new ImageView(Adapter.this.context);
                        imageView.setImageBitmap(WalletActivity.this.getTonController().createTonQR(Adapter.this.context, WalletActivity.this.getTonUrl(), null));
                        anonymousClass1.addView(imageView, LayoutHelper.createFrame(190, 190.0f, 49, 0.0f, 127.0f, 0.0f, 0.0f));
                        imageView.setOnLongClickListener(new -$$Lambda$WalletActivity$Adapter$1$0O46dSAinUuAaH3tp7nHtMiH-Ew(this, textView));
                        textView.setTextSize(1, 17.0f);
                        textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmono.ttf"));
                        textView.setTextColor(Theme.getColor(str));
                        StringBuilder stringBuilder = new StringBuilder(WalletActivity.this.walletAddress);
                        stringBuilder.insert(stringBuilder.length() / 2, 10);
                        textView.setText(stringBuilder);
                        anonymousClass1.addView(textView, LayoutHelper.createFrame(-2, -2.0f, 1, 0.0f, 329.0f, 0.0f, 0.0f));
                        textView.setOnLongClickListener(new -$$Lambda$WalletActivity$Adapter$1$D2CFM7cphGt52x6oeA-KuGV7IYs(this, textView));
                        textView = new TextView(Adapter.this.context);
                        textView.setPadding(AndroidUtilities.dp(34.0f), 0, AndroidUtilities.dp(34.0f), 0);
                        textView.setGravity(17);
                        textView.setTextColor(Theme.getColor("featuredStickers_buttonText"));
                        textView.setTextSize(1, 14.0f);
                        textView.setText(LocaleController.getString("WalletShareAddress", NUM));
                        textView.setTypeface(AndroidUtilities.getTypeface(str2));
                        textView.setBackgroundDrawable(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(4.0f), Theme.getColor("featuredStickers_addButton"), Theme.getColor("featuredStickers_addButtonPressed")));
                        anonymousClass1.addView(textView, LayoutHelper.createFrame(-1, 42.0f, 83, 16.0f, 0.0f, 16.0f, 16.0f));
                        textView.setOnClickListener(new -$$Lambda$WalletActivity$Adapter$1$0y5_Jga2halHiCWg4aj2oA6EuTU(this));
                        ScrollView scrollView = new ScrollView(Adapter.this.context);
                        scrollView.setVerticalScrollBarEnabled(false);
                        scrollView.addView(anonymousClass1, LayoutHelper.createScroll(-1, -2, 51));
                        if (VERSION.SDK_INT >= 21) {
                            scrollView.setNestedScrollingEnabled(true);
                        }
                        builder.setCustomView(scrollView);
                        BottomSheet create = builder.create();
                        create.setCanDismissWithSwipe(false);
                        WalletActivity.this.showDialog(create);
                    }

                    public /* synthetic */ void lambda$onReceivePressed$0$WalletActivity$Adapter$1(Builder builder, int i) {
                        builder.getDismissRunnable().run();
                        if (i == 1) {
                            AndroidUtilities.addToClipboard(WalletActivity.this.getTonUrl());
                            Toast.makeText(WalletActivity.this.getParentActivity(), LocaleController.getString("LinkCopied", NUM), 0).show();
                        } else if (i == 2) {
                            WalletActivity walletActivity = WalletActivity.this;
                            walletActivity.presentFragment(new WalletActionActivity(1, walletActivity.walletAddress));
                        }
                    }

                    public /* synthetic */ boolean lambda$onReceivePressed$2$WalletActivity$Adapter$1(TextView textView, View view) {
                        Activity parentActivity = WalletActivity.this.getParentActivity();
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("ton://transfer/");
                        stringBuilder.append(textView.getText().toString().replace("\n", ""));
                        TonController.shareBitmap(parentActivity, view, stringBuilder.toString());
                        return true;
                    }

                    public /* synthetic */ boolean lambda$onReceivePressed$3$WalletActivity$Adapter$1(TextView textView, View view) {
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("ton://transfer/");
                        stringBuilder.append(textView.getText().toString().replace("\n", ""));
                        AndroidUtilities.addToClipboard(stringBuilder.toString());
                        Toast.makeText(WalletActivity.this.getParentActivity(), LocaleController.getString("WalletTransactionAddressCopied", NUM), 0).show();
                        return true;
                    }

                    public /* synthetic */ void lambda$onReceivePressed$4$WalletActivity$Adapter$1(View view) {
                        String access$2200 = WalletActivity.this.getTonUrl();
                        WalletActivity walletActivity = WalletActivity.this;
                        walletActivity.showDialog(new ShareAlert(walletActivity.getParentActivity(), null, access$2200, false, access$2200, false));
                    }

                    /* Access modifiers changed, original: protected */
                    public void onSendPressed() {
                        WalletActivity walletActivity = WalletActivity.this;
                        walletActivity.presentFragment(new WalletActionActivity(0, walletActivity.walletAddress));
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
                anonymousClass1 = WalletActivity.this.paddingView = new View(this.context) {
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
            ((WalletBalanceCell) viewHolder.itemView).setBalance(TonController.getBalance(WalletActivity.this.accountState));
        }

        public int getItemViewType(int i, int i2) {
            int i3 = 1;
            if (i == 0) {
                if (i2 == 0) {
                    return 4;
                }
                return i2 == 1 ? 0 : 2;
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
                            WalletActivity.this.lastUpdateTime = 0;
                            WalletActivity.this.updateTime(false);
                            AndroidUtilities.cancelRunOnUIThread(WalletActivity.this.updateTimeRunnable);
                            WalletActivity.this.loadAccountState();
                        }
                        if (WalletActivity.viewOffset != 0.0f) {
                            ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{WalletActivity.viewOffset, 0.0f});
                            ofFloat.addUpdateListener(new -$$Lambda$WalletActivity$PullRecyclerView$lkXwd8GU-zP0QF_nYEqnNgLp6_Q(this));
                            ofFloat.setDuration((long) (350.0f - ((WalletActivity.viewOffset / PullForegroundDrawable.maxOverScroll) * 120.0f)));
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

    public /* synthetic */ void lambda$new$0$WalletActivity() {
        updateTime(true);
    }

    public WalletActivity() {
        loadAccountState();
    }

    public boolean onFragmentCreate() {
        getNotificationCenter().addObserver(this, NotificationCenter.walletPendingTransactionsChanged);
        return super.onFragmentCreate();
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        AndroidUtilities.cancelRunOnUIThread(this.updateTimeRunnable);
        AndroidUtilities.cancelRunOnUIThread(this.shortPollRunnable);
        getNotificationCenter().removeObserver(this, NotificationCenter.walletPendingTransactionsChanged);
    }

    /* Access modifiers changed, original: protected */
    public ActionBar createActionBar(Context context) {
        AnonymousClass2 anonymousClass2 = new ActionBar(context) {
            /* Access modifiers changed, original: protected */
            public void onMeasure(int i, int i2) {
                if (VERSION.SDK_INT >= 21 && WalletActivity.this.statusTextView != null) {
                    ((FrameLayout.LayoutParams) WalletActivity.this.statusTextView.getLayoutParams()).topMargin = AndroidUtilities.statusBarHeight;
                }
                super.onMeasure(i, i2);
            }
        };
        anonymousClass2.setBackButtonImage(NUM);
        anonymousClass2.setBackgroundColor(Theme.getColor("wallet_blackBackground"));
        String str = "wallet_whiteText";
        anonymousClass2.setTitleColor(Theme.getColor(str));
        anonymousClass2.setItemsColor(Theme.getColor(str), false);
        anonymousClass2.setItemsBackgroundColor(Theme.getColor("wallet_blackBackgroundSelector"), false);
        anonymousClass2.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    WalletActivity.this.finishFragment();
                } else if (i == 1) {
                    WalletActivity walletActivity = WalletActivity.this;
                    walletActivity.presentFragment(new WalletSettingsActivity(walletActivity));
                }
            }
        });
        anonymousClass2.createMenu().addItem(1, NUM);
        this.statusTextView = new SimpleTextView(context);
        this.statusTextView.setTextSize(14);
        this.statusTextView.setGravity(17);
        this.statusTextView.setTextColor(Theme.getColor("wallet_statusText"));
        anonymousClass2.addView(this.statusTextView, LayoutHelper.createFrame(-1, -1.0f, 83, 48.0f, 0.0f, 48.0f, 0.0f));
        return anonymousClass2;
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
        AnonymousClass5 anonymousClass5 = new FrameLayout(context) {
            /* Access modifiers changed, original: protected */
            public void onDraw(Canvas canvas) {
                ViewHolder findViewHolderForAdapterPosition = WalletActivity.this.listView.findViewHolderForAdapterPosition(1);
                float dp = (float) AndroidUtilities.dp(13.0f);
                float bottom = (float) (findViewHolderForAdapterPosition != null ? findViewHolderForAdapterPosition.itemView.getBottom() : 0);
                if (bottom < dp) {
                    dp *= bottom / dp;
                }
                int i = (int) (bottom + WalletActivity.viewOffset);
                float[] access$1300 = WalletActivity.this.radii;
                float[] access$13002 = WalletActivity.this.radii;
                float[] access$13003 = WalletActivity.this.radii;
                WalletActivity.this.radii[3] = dp;
                access$13003[2] = dp;
                access$13002[1] = dp;
                access$1300[0] = dp;
                canvas.drawRect(0.0f, 0.0f, (float) getMeasuredWidth(), (float) (AndroidUtilities.dp(6.0f) + i), WalletActivity.this.blackPaint);
                WalletActivity.this.backgroundDrawable.setBounds(0, i - AndroidUtilities.dp(7.0f), getMeasuredWidth(), getMeasuredHeight());
                WalletActivity.this.backgroundDrawable.draw(canvas);
            }
        };
        anonymousClass5.setWillNotDraw(false);
        this.fragmentView = anonymousClass5;
        this.pinnedHeaderShadowDrawable = context.getResources().getDrawable(NUM);
        this.pinnedHeaderShadowDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor("windowBackgroundGrayShadow"), Mode.MULTIPLY));
        this.listView = new PullRecyclerView(context);
        this.listView.setSectionsType(2);
        this.listView.setPinnedHeaderShadowDrawable(this.pinnedHeaderShadowDrawable);
        PullRecyclerView pullRecyclerView = this.listView;
        AnonymousClass6 anonymousClass6 = new LinearLayoutManager(context, 1, false) {
            /* JADX WARNING: Removed duplicated region for block: B:32:0x00b8  */
            /* JADX WARNING: Removed duplicated region for block: B:31:0x00b4  */
            /* JADX WARNING: Removed duplicated region for block: B:36:0x00cf  */
            /* JADX WARNING: Removed duplicated region for block: B:39:0x00e4  */
            /* JADX WARNING: Removed duplicated region for block: B:76:0x01d2  */
            /* JADX WARNING: Removed duplicated region for block: B:78:0x01f4  */
            /* JADX WARNING: Removed duplicated region for block: B:31:0x00b4  */
            /* JADX WARNING: Removed duplicated region for block: B:32:0x00b8  */
            /* JADX WARNING: Removed duplicated region for block: B:36:0x00cf  */
            /* JADX WARNING: Removed duplicated region for block: B:39:0x00e4  */
            /* JADX WARNING: Removed duplicated region for block: B:42:0x00f2 A:{SKIP} */
            /* JADX WARNING: Removed duplicated region for block: B:76:0x01d2  */
            /* JADX WARNING: Removed duplicated region for block: B:78:0x01f4  */
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
                if (r15 != 0) goto L_0x01c0;
            L_0x00f2:
                if (r6 == 0) goto L_0x01c0;
            L_0x00f4:
                r15 = r6.getBottom();
                r10 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
                r10 = org.telegram.messenger.AndroidUtilities.dp(r10);
                if (r15 < r10) goto L_0x01c0;
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
                if (r3 == 0) goto L_0x01a0;
            L_0x0182:
                if (r13 >= 0) goto L_0x01a0;
            L_0x0184:
                if (r0 == 0) goto L_0x01a0;
            L_0x0186:
                r0 = org.telegram.ui.Wallet.WalletActivity.viewOffset;
                r1 = org.telegram.ui.Components.PullForegroundDrawable.maxOverScroll;
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
            L_0x01a0:
                r13 = org.telegram.ui.Wallet.WalletActivity.this;
                r13 = r13.pullForegroundDrawable;
                if (r13 == 0) goto L_0x01f2;
            L_0x01a8:
                r13 = org.telegram.ui.Wallet.WalletActivity.this;
                r13 = r13.pullForegroundDrawable;
                r13.pullProgress = r15;
                r13 = org.telegram.ui.Wallet.WalletActivity.this;
                r13 = r13.pullForegroundDrawable;
                r15 = org.telegram.ui.Wallet.WalletActivity.this;
                r15 = r15.listView;
                r13.setListView(r15);
                goto L_0x01f2;
            L_0x01c0:
                r13 = org.telegram.ui.Wallet.WalletActivity.this;
                r13.startArchivePullingTime = r8;
                r13 = org.telegram.ui.Wallet.WalletActivity.this;
                r13.canShowHiddenPull = r1;
                r13 = org.telegram.ui.Wallet.WalletActivity.this;
                r13 = r13.pullForegroundDrawable;
                if (r13 == 0) goto L_0x01f2;
            L_0x01d2:
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
            L_0x01f2:
                if (r6 == 0) goto L_0x01f7;
            L_0x01f4:
                r6.invalidate();
            L_0x01f7:
                return r14;
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Wallet.WalletActivity$AnonymousClass6.scrollVerticallyBy(int, androidx.recyclerview.widget.RecyclerView$Recycler, androidx.recyclerview.widget.RecyclerView$State):int");
            }
        };
        this.layoutManager = anonymousClass6;
        pullRecyclerView.setLayoutManager(anonymousClass6);
        PullRecyclerView pullRecyclerView2 = this.listView;
        Adapter adapter = new Adapter(context);
        this.adapter = adapter;
        pullRecyclerView2.setAdapter(adapter);
        this.listView.setGlowColor(Theme.getColor(str));
        anonymousClass5.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
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
        this.listView.setOnItemClickListener(new -$$Lambda$WalletActivity$AdwaE6FUbC5bTRoO5_7tVR4wDsU(this));
        updateTime(false);
        return this.fragmentView;
    }

    public /* synthetic */ void lambda$createView$1$WalletActivity(View view, int i) {
        if (getParentActivity() != null && (view instanceof WalletTransactionCell)) {
            WalletTransactionCell walletTransactionCell = (WalletTransactionCell) view;
            if (!walletTransactionCell.isEmpty()) {
                showDialog(new WalletTransactionSheet(this, this.walletAddress, walletTransactionCell.getAddress(), walletTransactionCell.getComment(), walletTransactionCell.getAmount(), walletTransactionCell.getDate(), walletTransactionCell.getStorageFee(), walletTransactionCell.getTransactionFee()));
            }
        }
    }

    public void onResume() {
        super.onResume();
        getTonController().isKeyStoreInvalidated(new -$$Lambda$WalletActivity$ghVqqP4FI4q1tdbIinmQH6y9ZCs(this));
    }

    public /* synthetic */ void lambda$onResume$2$WalletActivity(boolean z) {
        if (z && getParentActivity() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
            builder.setTitle(LocaleController.getString("Wallet", NUM));
            builder.setMessage(LocaleController.getString("WalletKeystoreInvalidated", NUM));
            builder.setPositiveButton(LocaleController.getString("OK", NUM), null);
            AlertDialog create = builder.create();
            this.keyInvalidDialog = create;
            showDialog(create);
        }
    }

    public void onPause() {
        super.onPause();
    }

    /* Access modifiers changed, original: protected */
    public void onDialogDismiss(Dialog dialog) {
        Dialog dialog2 = this.keyInvalidDialog;
        if (dialog2 != null && dialog == dialog2) {
            getTonController().cleanup();
            UserConfig userConfig = getUserConfig();
            userConfig.clearTonConfig();
            userConfig.saveConfig(false);
            finishFragment();
        }
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.walletPendingTransactionsChanged) {
            fillTransactions(null, false);
            Adapter adapter = this.adapter;
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
        }
    }

    private void updateTime(boolean z) {
        SimpleTextView simpleTextView = this.statusTextView;
        if (simpleTextView != null) {
            if (this.lastUpdateTime == 0) {
                simpleTextView.setText(LocaleController.getString("WalletUpdating", NUM));
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

    private InternalTransactionId getLastTransactionId() {
        RawTransaction rawTransaction = this.lastTransaction;
        if (rawTransaction != null) {
            return rawTransaction.transactionId;
        }
        return TonController.getLastTransactionId(this.accountState);
    }

    private long getLastUpdateTime() {
        GenericAccountState genericAccountState = this.accountState;
        if (genericAccountState instanceof GenericAccountStateRaw) {
            return ((GenericAccountStateRaw) genericAccountState).accountState.syncUtime;
        }
        if (genericAccountState instanceof GenericAccountStateTestWallet) {
            return ((GenericAccountStateTestWallet) genericAccountState).accountState.syncUtime;
        }
        if (genericAccountState instanceof GenericAccountStateTestGiver) {
            return ((GenericAccountStateTestGiver) genericAccountState).accountState.syncUtime;
        }
        if (genericAccountState instanceof GenericAccountStateUninited) {
            return ((GenericAccountStateUninited) genericAccountState).accountState.syncUtime;
        }
        return genericAccountState instanceof GenericAccountStateWallet ? ((GenericAccountStateWallet) genericAccountState).accountState.syncUtime : 0;
    }

    private void scheduleShortPoll() {
        AndroidUtilities.cancelRunOnUIThread(this.shortPollRunnable);
        AndroidUtilities.runOnUIThread(this.shortPollRunnable, 10000);
    }

    private void loadAccountState() {
        if (!this.loadingAccountState) {
            this.loadingAccountState = true;
            AndroidUtilities.cancelRunOnUIThread(this.shortPollRunnable);
            this.lastTransaction = null;
            if (this.accountState == null) {
                this.accountState = getTonController().getCachedAccountState();
                this.lastUpdateTime = getLastUpdateTime();
                fillTransactions(getTonController().getCachedTransactions(), false);
            }
            this.walletAddress = getTonController().getWalletAddress(getUserConfig().tonPublicKey);
            getTonController().getAccountState(new -$$Lambda$WalletActivity$DRsPr-qhBjTgCLASSNAMEMKi1w3nVnTH8(this));
            Adapter adapter = this.adapter;
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:20:0x005f  */
    /* JADX WARNING: Removed duplicated region for block: B:19:0x005b  */
    public /* synthetic */ void lambda$loadAccountState$3$WalletActivity(drinkless.org.ton.TonApi.GenericAccountState r9) {
        /*
        r8 = this;
        r0 = 0;
        if (r9 == 0) goto L_0x0063;
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
        r9 = r8.adapter;
        r9.notifyDataSetChanged();
        if (r0 == 0) goto L_0x005f;
    L_0x005b:
        r8.loadTransactions(r2);
        goto L_0x0071;
    L_0x005f:
        r8.onFinishGettingAccountState();
        goto L_0x0071;
    L_0x0063:
        r8.loadingAccountState = r0;
        r8.scheduleShortPoll();
        r9 = r8.getParentActivity();
        if (r9 == 0) goto L_0x0071;
    L_0x006e:
        r8.loadAccountState();
    L_0x0071:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Wallet.WalletActivity.lambda$loadAccountState$3$WalletActivity(drinkless.org.ton.TonApi$GenericAccountState):void");
    }

    private void onFinishGettingAccountState() {
        this.lastUpdateTime = getLastUpdateTime();
        updateTime(true);
        this.loadingAccountState = false;
        scheduleShortPoll();
    }

    /* JADX WARNING: Removed duplicated region for block: B:20:0x0092  */
    /* JADX WARNING: Removed duplicated region for block: B:17:0x007f  */
    /* JADX WARNING: Removed duplicated region for block: B:25:0x00a7  */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x00a6 A:{RETURN} */
    private boolean fillTransactions(java.util.ArrayList<drinkless.org.ton.TonApi.RawTransaction> r18, boolean r19) {
        /*
        r17 = this;
        r0 = r17;
        r1 = r18;
        r2 = "pending";
        r3 = 1;
        r4 = 0;
        if (r1 == 0) goto L_0x0070;
    L_0x000a:
        r5 = r18.isEmpty();
        if (r5 != 0) goto L_0x0070;
    L_0x0010:
        if (r19 == 0) goto L_0x0070;
    L_0x0012:
        r5 = r18.size();
        r5 = r5 - r3;
        r5 = r1.get(r5);
        r5 = (drinkless.org.ton.TonApi.RawTransaction) r5;
        r6 = r0.sections;
        r6 = r6.size();
        r7 = 0;
    L_0x0024:
        if (r7 >= r6) goto L_0x0070;
    L_0x0026:
        r8 = r0.sections;
        r8 = r8.get(r7);
        r8 = r2.equals(r8);
        if (r8 == 0) goto L_0x0035;
    L_0x0032:
        r7 = r7 + 1;
        goto L_0x0024;
    L_0x0035:
        r6 = r0.sections;
        r6 = r6.get(r7);
        r6 = (java.lang.String) r6;
        r7 = r0.sectionArrays;
        r6 = r7.get(r6);
        r6 = (java.util.ArrayList) r6;
        r6 = r6.get(r4);
        r6 = (drinkless.org.ton.TonApi.RawTransaction) r6;
        r6 = r6.utime;
        r8 = r5.utime;
        r5 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1));
        if (r5 >= 0) goto L_0x006d;
    L_0x0053:
        r5 = r0.sections;
        r5.clear();
        r5 = r0.sectionArrays;
        r5.clear();
        r5 = r0.transactionsDict;
        r5.clear();
        r0.transactionsEndReached = r4;
        r5 = r17.getTonController();
        r5.clearPendingCache();
        r5 = 1;
        goto L_0x0071;
    L_0x006d:
        java.util.Collections.reverse(r18);
    L_0x0070:
        r5 = 0;
    L_0x0071:
        r6 = r17.getTonController();
        r6 = r6.getPendingTransactions();
        r7 = r6.isEmpty();
        if (r7 == 0) goto L_0x0092;
    L_0x007f:
        r6 = r0.sectionArrays;
        r6 = r6.containsKey(r2);
        if (r6 == 0) goto L_0x00a4;
    L_0x0087:
        r6 = r0.sectionArrays;
        r6.remove(r2);
        r6 = r0.sections;
        r6.remove(r4);
        goto L_0x00a4;
    L_0x0092:
        r7 = r0.sectionArrays;
        r7 = r7.containsKey(r2);
        if (r7 != 0) goto L_0x00a4;
    L_0x009a:
        r7 = r0.sections;
        r7.add(r4, r2);
        r7 = r0.sectionArrays;
        r7.put(r2, r6);
    L_0x00a4:
        if (r1 != 0) goto L_0x00a7;
    L_0x00a6:
        return r4;
    L_0x00a7:
        r6 = java.util.Calendar.getInstance();
        r7 = r18.size();
        r8 = 0;
        r9 = 0;
    L_0x00b1:
        if (r8 >= r7) goto L_0x0179;
    L_0x00b3:
        r10 = r1.get(r8);
        r10 = (drinkless.org.ton.TonApi.RawTransaction) r10;
        r11 = r0.transactionsDict;
        r12 = r10.transactionId;
        r12 = r12.lt;
        r12 = java.lang.Long.valueOf(r12);
        r11 = r11.containsKey(r12);
        if (r11 == 0) goto L_0x00cc;
    L_0x00c9:
        r3 = 0;
        goto L_0x0173;
    L_0x00cc:
        r11 = r10.utime;
        r13 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r11 = r11 * r13;
        r6.setTimeInMillis(r11);
        r9 = 6;
        r9 = r6.get(r9);
        r11 = r6.get(r3);
        r12 = 2;
        r13 = r6.get(r12);
        r14 = 3;
        r14 = new java.lang.Object[r14];
        r11 = java.lang.Integer.valueOf(r11);
        r14[r4] = r11;
        r11 = java.lang.Integer.valueOf(r13);
        r14[r3] = r11;
        r9 = java.lang.Integer.valueOf(r9);
        r14[r12] = r9;
        r9 = "%d_%02d_%02d";
        r9 = java.lang.String.format(r9, r14);
        r11 = r0.sectionArrays;
        r11 = r11.get(r9);
        r11 = (java.util.ArrayList) r11;
        if (r11 != 0) goto L_0x0158;
    L_0x0108:
        r11 = r0.sections;
        r11 = r11.size();
        r12 = r0.sections;
        r12 = r12.size();
        r13 = 0;
    L_0x0115:
        if (r13 >= r12) goto L_0x0148;
    L_0x0117:
        r14 = r0.sections;
        r14 = r14.get(r13);
        r14 = r2.equals(r14);
        if (r14 == 0) goto L_0x0124;
    L_0x0123:
        goto L_0x0143;
    L_0x0124:
        r14 = r0.sections;
        r14 = r14.get(r13);
        r14 = (java.lang.String) r14;
        r15 = r0.sectionArrays;
        r14 = r15.get(r14);
        r14 = (java.util.ArrayList) r14;
        r14 = r14.get(r4);
        r14 = (drinkless.org.ton.TonApi.RawTransaction) r14;
        r14 = r14.utime;
        r3 = r10.utime;
        r16 = (r14 > r3 ? 1 : (r14 == r3 ? 0 : -1));
        if (r16 >= 0) goto L_0x0143;
    L_0x0142:
        goto L_0x0149;
    L_0x0143:
        r13 = r13 + 1;
        r3 = 1;
        r4 = 0;
        goto L_0x0115;
    L_0x0148:
        r13 = r11;
    L_0x0149:
        r11 = new java.util.ArrayList;
        r11.<init>();
        r3 = r0.sections;
        r3.add(r13, r9);
        r3 = r0.sectionArrays;
        r3.put(r9, r11);
    L_0x0158:
        if (r19 == 0) goto L_0x0161;
    L_0x015a:
        if (r5 != 0) goto L_0x0161;
    L_0x015c:
        r3 = 0;
        r11.add(r3, r10);
        goto L_0x0165;
    L_0x0161:
        r3 = 0;
        r11.add(r10);
    L_0x0165:
        r4 = r0.transactionsDict;
        r9 = r10.transactionId;
        r11 = r9.lt;
        r9 = java.lang.Long.valueOf(r11);
        r4.put(r9, r10);
        r9 = 1;
    L_0x0173:
        r8 = r8 + 1;
        r3 = 1;
        r4 = 0;
        goto L_0x00b1;
    L_0x0179:
        return r9;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Wallet.WalletActivity.fillTransactions(java.util.ArrayList, boolean):boolean");
    }

    private void loadTransactions(boolean z) {
        if (!this.loadingTransactions && this.accountStateLoaded) {
            this.loadingTransactions = true;
            getTonController().getTransactions(z, getLastTransactionId(), new -$$Lambda$WalletActivity$PkhskOdzX-UYRFCdTegzQkxUPLQ(this, z));
        }
    }

    public /* synthetic */ void lambda$loadTransactions$4$WalletActivity(boolean z, ArrayList arrayList) {
        if (z) {
            onFinishGettingAccountState();
        }
        this.loadingTransactions = false;
        if (arrayList != null) {
            if (!(fillTransactions(arrayList, z) || z)) {
                this.transactionsEndReached = true;
            }
            if (!arrayList.isEmpty()) {
                this.lastTransaction = (RawTransaction) arrayList.get(arrayList.size() - 1);
            }
            this.adapter.notifyDataSetChanged();
            View view = this.paddingView;
            if (view != null) {
                view.requestLayout();
            }
        }
    }

    private String getTonUrl() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("ton://transfer/");
        stringBuilder.append(this.walletAddress);
        return stringBuilder.toString();
    }
}
