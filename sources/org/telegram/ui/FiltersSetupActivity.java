package org.telegram.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.ProgressButton;
import org.telegram.ui.Components.RLottieImageView;
import org.telegram.ui.Components.RecyclerListView;

public class FiltersSetupActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    /* access modifiers changed from: private */
    public ListAdapter adapter;
    /* access modifiers changed from: private */
    public int createFilterRow;
    /* access modifiers changed from: private */
    public int createSectionRow;
    /* access modifiers changed from: private */
    public int filterHelpRow;
    /* access modifiers changed from: private */
    public int filtersEndRow;
    /* access modifiers changed from: private */
    public int filtersHeaderRow;
    /* access modifiers changed from: private */
    public int filtersStartRow;
    /* access modifiers changed from: private */
    public boolean ignoreUpdates;
    /* access modifiers changed from: private */
    public ItemTouchHelper itemTouchHelper;
    /* access modifiers changed from: private */
    public RecyclerListView listView;
    /* access modifiers changed from: private */
    public boolean orderChanged;
    /* access modifiers changed from: private */
    public int recommendedEndRow;
    /* access modifiers changed from: private */
    public int recommendedHeaderRow;
    /* access modifiers changed from: private */
    public int recommendedSectionRow;
    /* access modifiers changed from: private */
    public int recommendedStartRow;
    /* access modifiers changed from: private */
    public int rowCount = 0;

    public static class TextCell extends FrameLayout {
        private ImageView imageView;
        private SimpleTextView textView;

        public TextCell(Context context) {
            super(context);
            SimpleTextView simpleTextView = new SimpleTextView(context);
            this.textView = simpleTextView;
            simpleTextView.setTextSize(16);
            this.textView.setGravity(LocaleController.isRTL ? 5 : 3);
            this.textView.setTextColor(Theme.getColor("windowBackgroundWhiteBlueText2"));
            this.textView.setTag("windowBackgroundWhiteBlueText2");
            addView(this.textView);
            ImageView imageView2 = new ImageView(context);
            this.imageView = imageView2;
            imageView2.setScaleType(ImageView.ScaleType.CENTER);
            addView(this.imageView);
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int width = View.MeasureSpec.getSize(widthMeasureSpec);
            int dp = AndroidUtilities.dp(48.0f);
            this.textView.measure(View.MeasureSpec.makeMeasureSpec(width - AndroidUtilities.dp(94.0f), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(20.0f), NUM));
            this.imageView.measure(View.MeasureSpec.makeMeasureSpec(width, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(50.0f), NUM));
            setMeasuredDimension(width, AndroidUtilities.dp(50.0f));
        }

        /* access modifiers changed from: protected */
        public void onLayout(boolean changed, int left, int top, int right, int bottom) {
            int viewLeft;
            int width = right - left;
            int viewTop = ((bottom - top) - this.textView.getTextHeight()) / 2;
            float f = 64.0f;
            if (LocaleController.isRTL) {
                int measuredWidth = getMeasuredWidth() - this.textView.getMeasuredWidth();
                if (this.imageView.getVisibility() != 0) {
                    f = 23.0f;
                }
                viewLeft = measuredWidth - AndroidUtilities.dp(f);
            } else {
                if (this.imageView.getVisibility() != 0) {
                    f = 23.0f;
                }
                viewLeft = AndroidUtilities.dp(f);
            }
            SimpleTextView simpleTextView = this.textView;
            simpleTextView.layout(viewLeft, viewTop, simpleTextView.getMeasuredWidth() + viewLeft, this.textView.getMeasuredHeight() + viewTop);
            int viewLeft2 = !LocaleController.isRTL ? AndroidUtilities.dp(20.0f) : (width - this.imageView.getMeasuredWidth()) - AndroidUtilities.dp(20.0f);
            ImageView imageView2 = this.imageView;
            imageView2.layout(viewLeft2, 0, imageView2.getMeasuredWidth() + viewLeft2, this.imageView.getMeasuredHeight());
        }

        public void setTextAndIcon(String text, Drawable icon, boolean divider) {
            this.textView.setText(text);
            this.imageView.setImageDrawable(icon);
        }
    }

    public static class SuggestedFilterCell extends FrameLayout {
        private ProgressButton addButton;
        private boolean needDivider;
        private TLRPC.TL_dialogFilterSuggested suggestedFilter;
        private TextView textView;
        private TextView valueTextView;

        public SuggestedFilterCell(Context context) {
            super(context);
            TextView textView2 = new TextView(context);
            this.textView = textView2;
            textView2.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.textView.setTextSize(1, 16.0f);
            this.textView.setLines(1);
            this.textView.setMaxLines(1);
            this.textView.setSingleLine(true);
            this.textView.setEllipsize(TextUtils.TruncateAt.END);
            this.textView.setGravity(LocaleController.isRTL ? 5 : 3);
            addView(this.textView, LayoutHelper.createFrame(-2, -2.0f, LocaleController.isRTL ? 5 : 3, 22.0f, 10.0f, 22.0f, 0.0f));
            TextView textView3 = new TextView(context);
            this.valueTextView = textView3;
            textView3.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText2"));
            this.valueTextView.setTextSize(1, 13.0f);
            this.valueTextView.setLines(1);
            this.valueTextView.setMaxLines(1);
            this.valueTextView.setSingleLine(true);
            this.valueTextView.setEllipsize(TextUtils.TruncateAt.END);
            this.valueTextView.setGravity(LocaleController.isRTL ? 5 : 3);
            addView(this.valueTextView, LayoutHelper.createFrame(-2, -2.0f, LocaleController.isRTL ? 5 : 3, 22.0f, 35.0f, 22.0f, 0.0f));
            ProgressButton progressButton = new ProgressButton(context);
            this.addButton = progressButton;
            progressButton.setText(LocaleController.getString("Add", NUM));
            this.addButton.setTextColor(Theme.getColor("featuredStickers_buttonText"));
            this.addButton.setProgressColor(Theme.getColor("featuredStickers_buttonProgress"));
            this.addButton.setBackgroundRoundRect(Theme.getColor("featuredStickers_addButton"), Theme.getColor("featuredStickers_addButtonPressed"));
            addView(this.addButton, LayoutHelper.createFrameRelatively(-2.0f, 28.0f, 8388661, 0.0f, 18.0f, 14.0f, 0.0f));
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            setMeasuredDimension(View.MeasureSpec.getSize(widthMeasureSpec), AndroidUtilities.dp(64.0f));
            int i = widthMeasureSpec;
            int i2 = heightMeasureSpec;
            measureChildWithMargins(this.addButton, i, 0, i2, 0);
            measureChildWithMargins(this.textView, i, this.addButton.getMeasuredWidth(), i2, 0);
            measureChildWithMargins(this.valueTextView, i, this.addButton.getMeasuredWidth(), i2, 0);
        }

        public void setFilter(TLRPC.TL_dialogFilterSuggested filter, boolean divider) {
            this.needDivider = divider;
            this.suggestedFilter = filter;
            setWillNotDraw(!divider);
            this.textView.setText(filter.filter.title);
            this.valueTextView.setText(filter.description);
        }

        public TLRPC.TL_dialogFilterSuggested getSuggestedFilter() {
            return this.suggestedFilter;
        }

        public void setAddOnClickListener(View.OnClickListener onClickListener) {
            this.addButton.setOnClickListener(onClickListener);
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            if (this.needDivider) {
                canvas.drawLine(0.0f, (float) (getHeight() - 1), (float) (getWidth() - getPaddingRight()), (float) (getHeight() - 1), Theme.dividerPaint);
            }
        }
    }

    public static class HintInnerCell extends FrameLayout {
        private RLottieImageView imageView;
        private TextView messageTextView;

        public HintInnerCell(Context context) {
            super(context);
            RLottieImageView rLottieImageView = new RLottieImageView(context);
            this.imageView = rLottieImageView;
            rLottieImageView.setAnimation(NUM, 90, 90);
            this.imageView.setScaleType(ImageView.ScaleType.CENTER);
            this.imageView.playAnimation();
            addView(this.imageView, LayoutHelper.createFrame(90, 90.0f, 49, 0.0f, 14.0f, 0.0f, 0.0f));
            this.imageView.setOnClickListener(new FiltersSetupActivity$HintInnerCell$$ExternalSyntheticLambda0(this));
            TextView textView = new TextView(context);
            this.messageTextView = textView;
            textView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText4"));
            this.messageTextView.setTextSize(1, 14.0f);
            this.messageTextView.setGravity(17);
            this.messageTextView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("CreateNewFilterInfo", NUM, new Object[0])));
            addView(this.messageTextView, LayoutHelper.createFrame(-1, -2.0f, 49, 40.0f, 121.0f, 40.0f, 24.0f));
        }

        /* renamed from: lambda$new$0$org-telegram-ui-FiltersSetupActivity$HintInnerCell  reason: not valid java name */
        public /* synthetic */ void m2936lambda$new$0$orgtelegramuiFiltersSetupActivity$HintInnerCell(View v) {
            if (!this.imageView.isPlaying()) {
                this.imageView.setProgress(0.0f);
                this.imageView.playAnimation();
            }
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), NUM), heightMeasureSpec);
        }
    }

    public static class FilterCell extends FrameLayout {
        private MessagesController.DialogFilter currentFilter;
        private ImageView moveImageView;
        private boolean needDivider;
        private ImageView optionsImageView;
        private TextView textView;
        private TextView valueTextView;

        /* JADX INFO: super call moved to the top of the method (can break code semantics) */
        public FilterCell(Context context) {
            super(context);
            Context context2 = context;
            setWillNotDraw(false);
            ImageView imageView = new ImageView(context2);
            this.moveImageView = imageView;
            imageView.setFocusable(false);
            this.moveImageView.setScaleType(ImageView.ScaleType.CENTER);
            this.moveImageView.setImageResource(NUM);
            this.moveImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("stickers_menu"), PorterDuff.Mode.MULTIPLY));
            this.moveImageView.setContentDescription(LocaleController.getString("FilterReorder", NUM));
            this.moveImageView.setClickable(true);
            int i = 5;
            addView(this.moveImageView, LayoutHelper.createFrame(48, 48.0f, (LocaleController.isRTL ? 5 : 3) | 16, 6.0f, 0.0f, 6.0f, 0.0f));
            TextView textView2 = new TextView(context2);
            this.textView = textView2;
            textView2.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.textView.setTextSize(1, 16.0f);
            this.textView.setLines(1);
            this.textView.setMaxLines(1);
            this.textView.setSingleLine(true);
            this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
            this.textView.setEllipsize(TextUtils.TruncateAt.END);
            addView(this.textView, LayoutHelper.createFrame(-1, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 80.0f : 64.0f, 14.0f, LocaleController.isRTL ? 64.0f : 80.0f, 0.0f));
            TextView textView3 = new TextView(context2);
            this.valueTextView = textView3;
            textView3.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText2"));
            this.valueTextView.setTextSize(1, 13.0f);
            this.valueTextView.setGravity(LocaleController.isRTL ? 5 : 3);
            this.valueTextView.setLines(1);
            this.valueTextView.setMaxLines(1);
            this.valueTextView.setSingleLine(true);
            this.valueTextView.setPadding(0, 0, 0, 0);
            this.valueTextView.setEllipsize(TextUtils.TruncateAt.END);
            addView(this.valueTextView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 80.0f : 64.0f, 35.0f, LocaleController.isRTL ? 64.0f : 80.0f, 0.0f));
            this.valueTextView.setVisibility(8);
            ImageView imageView2 = new ImageView(context2);
            this.optionsImageView = imageView2;
            imageView2.setFocusable(false);
            this.optionsImageView.setScaleType(ImageView.ScaleType.CENTER);
            this.optionsImageView.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor("stickers_menuSelector")));
            this.optionsImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("stickers_menu"), PorterDuff.Mode.MULTIPLY));
            this.optionsImageView.setImageResource(NUM);
            this.optionsImageView.setContentDescription(LocaleController.getString("AccDescrMoreOptions", NUM));
            addView(this.optionsImageView, LayoutHelper.createFrame(40, 40.0f, (LocaleController.isRTL ? 3 : i) | 16, 6.0f, 0.0f, 6.0f, 0.0f));
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(50.0f), NUM));
        }

        public void setFilter(MessagesController.DialogFilter filter, boolean divider) {
            this.currentFilter = filter;
            StringBuilder info = new StringBuilder();
            if ((filter.flags & MessagesController.DIALOG_FILTER_FLAG_ALL_CHATS) == MessagesController.DIALOG_FILTER_FLAG_ALL_CHATS) {
                info.append(LocaleController.getString("FilterAllChats", NUM));
            } else {
                if ((filter.flags & MessagesController.DIALOG_FILTER_FLAG_CONTACTS) != 0) {
                    if (info.length() != 0) {
                        info.append(", ");
                    }
                    info.append(LocaleController.getString("FilterContacts", NUM));
                }
                if ((filter.flags & MessagesController.DIALOG_FILTER_FLAG_NON_CONTACTS) != 0) {
                    if (info.length() != 0) {
                        info.append(", ");
                    }
                    info.append(LocaleController.getString("FilterNonContacts", NUM));
                }
                if ((filter.flags & MessagesController.DIALOG_FILTER_FLAG_GROUPS) != 0) {
                    if (info.length() != 0) {
                        info.append(", ");
                    }
                    info.append(LocaleController.getString("FilterGroups", NUM));
                }
                if ((filter.flags & MessagesController.DIALOG_FILTER_FLAG_CHANNELS) != 0) {
                    if (info.length() != 0) {
                        info.append(", ");
                    }
                    info.append(LocaleController.getString("FilterChannels", NUM));
                }
                if ((filter.flags & MessagesController.DIALOG_FILTER_FLAG_BOTS) != 0) {
                    if (info.length() != 0) {
                        info.append(", ");
                    }
                    info.append(LocaleController.getString("FilterBots", NUM));
                }
            }
            if (!filter.alwaysShow.isEmpty() || !filter.neverShow.isEmpty()) {
                if (info.length() != 0) {
                    info.append(", ");
                }
                info.append(LocaleController.formatPluralString("Exception", filter.alwaysShow.size() + filter.neverShow.size()));
            }
            if (info.length() == 0) {
                info.append(LocaleController.getString("FilterNoChats", NUM));
            }
            this.textView.setText(Emoji.replaceEmoji(filter.name, this.textView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(20.0f), false));
            this.valueTextView.setText(info);
            this.needDivider = divider;
        }

        public MessagesController.DialogFilter getCurrentFilter() {
            return this.currentFilter;
        }

        public void setOnOptionsClick(View.OnClickListener listener) {
            this.optionsImageView.setOnClickListener(listener);
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            if (this.needDivider) {
                canvas.drawLine(LocaleController.isRTL ? 0.0f : (float) AndroidUtilities.dp(62.0f), (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(62.0f) : 0)), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
            }
        }

        public void setOnReorderButtonTouchListener(View.OnTouchListener listener) {
            this.moveImageView.setOnTouchListener(listener);
        }
    }

    public boolean onFragmentCreate() {
        updateRows(true);
        getMessagesController().loadRemoteFilters(true);
        getNotificationCenter().addObserver(this, NotificationCenter.dialogFiltersUpdated);
        getNotificationCenter().addObserver(this, NotificationCenter.suggestedFiltersLoaded);
        if (getMessagesController().suggestedFilters.isEmpty()) {
            getMessagesController().loadSuggestedFilters();
        }
        return super.onFragmentCreate();
    }

    /* access modifiers changed from: private */
    public void updateRows(boolean notify) {
        ListAdapter listAdapter;
        this.recommendedHeaderRow = -1;
        this.recommendedStartRow = -1;
        this.recommendedEndRow = -1;
        this.recommendedSectionRow = -1;
        ArrayList<TLRPC.TL_dialogFilterSuggested> suggestedFilters = getMessagesController().suggestedFilters;
        this.rowCount = 0;
        this.rowCount = 0 + 1;
        this.filterHelpRow = 0;
        int count = getMessagesController().dialogFilters.size();
        if (!suggestedFilters.isEmpty() && count < 10) {
            int i = this.rowCount;
            int i2 = i + 1;
            this.rowCount = i2;
            this.recommendedHeaderRow = i;
            this.recommendedStartRow = i2;
            int size = i2 + suggestedFilters.size();
            this.rowCount = size;
            this.recommendedEndRow = size;
            this.rowCount = size + 1;
            this.recommendedSectionRow = size;
        }
        if (count != 0) {
            int i3 = this.rowCount;
            int i4 = i3 + 1;
            this.rowCount = i4;
            this.filtersHeaderRow = i3;
            this.filtersStartRow = i4;
            int i5 = i4 + count;
            this.rowCount = i5;
            this.filtersEndRow = i5;
        } else {
            this.filtersHeaderRow = -1;
            this.filtersStartRow = -1;
            this.filtersEndRow = -1;
        }
        if (count < 10) {
            int i6 = this.rowCount;
            this.rowCount = i6 + 1;
            this.createFilterRow = i6;
        } else {
            this.createFilterRow = -1;
        }
        int i7 = this.rowCount;
        this.rowCount = i7 + 1;
        this.createSectionRow = i7;
        if (notify && (listAdapter = this.adapter) != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    public void onFragmentDestroy() {
        getNotificationCenter().removeObserver(this, NotificationCenter.dialogFiltersUpdated);
        getNotificationCenter().removeObserver(this, NotificationCenter.suggestedFiltersLoaded);
        if (this.orderChanged) {
            getNotificationCenter().postNotificationName(NotificationCenter.dialogFiltersUpdated, new Object[0]);
            getMessagesStorage().saveDialogFiltersOrder();
            TLRPC.TL_messages_updateDialogFiltersOrder req = new TLRPC.TL_messages_updateDialogFiltersOrder();
            ArrayList<MessagesController.DialogFilter> filters = getMessagesController().dialogFilters;
            int N = filters.size();
            for (int a = 0; a < N; a++) {
                MessagesController.DialogFilter dialogFilter = filters.get(a);
                req.order.add(Integer.valueOf(filters.get(a).id));
            }
            getConnectionsManager().sendRequest(req, FiltersSetupActivity$$ExternalSyntheticLambda0.INSTANCE);
        }
        super.onFragmentDestroy();
    }

    static /* synthetic */ void lambda$onFragmentDestroy$0(TLObject response, TLRPC.TL_error error) {
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("Filters", NUM));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    FiltersSetupActivity.this.finishFragment();
                }
            }
        });
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        frameLayout.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
        RecyclerListView recyclerListView = new RecyclerListView(context);
        this.listView = recyclerListView;
        ((DefaultItemAnimator) recyclerListView.getItemAnimator()).setDelayAnimations(false);
        this.listView.setLayoutManager(new LinearLayoutManager(context, 1, false));
        this.listView.setVerticalScrollBarEnabled(false);
        ItemTouchHelper itemTouchHelper2 = new ItemTouchHelper(new TouchHelperCallback());
        this.itemTouchHelper = itemTouchHelper2;
        itemTouchHelper2.attachToRecyclerView(this.listView);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        RecyclerListView recyclerListView2 = this.listView;
        ListAdapter listAdapter = new ListAdapter(context);
        this.adapter = listAdapter;
        recyclerListView2.setAdapter(listAdapter);
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListenerExtended) new FiltersSetupActivity$$ExternalSyntheticLambda1(this));
        return this.fragmentView;
    }

    /* renamed from: lambda$createView$1$org-telegram-ui-FiltersSetupActivity  reason: not valid java name */
    public /* synthetic */ void m2935lambda$createView$1$orgtelegramuiFiltersSetupActivity(View view, int position, float x, float y) {
        if (position >= this.filtersStartRow && position < this.filtersEndRow) {
            presentFragment(new FilterCreateActivity(getMessagesController().dialogFilters.get(position - this.filtersStartRow)));
        } else if (position == this.createFilterRow) {
            presentFragment(new FilterCreateActivity());
        }
    }

    public void onResume() {
        super.onResume();
        ListAdapter listAdapter = this.adapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.dialogFiltersUpdated) {
            if (!this.ignoreUpdates) {
                updateRows(true);
            }
        } else if (id == NotificationCenter.suggestedFiltersLoaded) {
            updateRows(true);
        }
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int type = holder.getItemViewType();
            return (type == 3 || type == 0 || type == 5 || type == 1) ? false : true;
        }

        public int getItemCount() {
            return FiltersSetupActivity.this.rowCount;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    View headerCell = new HeaderCell(this.mContext);
                    headerCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    view = headerCell;
                    break;
                case 1:
                    View view2 = new HintInnerCell(this.mContext);
                    view2.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                    view = view2;
                    break;
                case 2:
                    FilterCell filterCell = new FilterCell(this.mContext);
                    filterCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    filterCell.setOnReorderButtonTouchListener(new FiltersSetupActivity$ListAdapter$$ExternalSyntheticLambda4(this, filterCell));
                    filterCell.setOnOptionsClick(new FiltersSetupActivity$ListAdapter$$ExternalSyntheticLambda2(this));
                    FilterCell filterCell2 = filterCell;
                    view = filterCell;
                    break;
                case 3:
                    view = new ShadowSectionCell(this.mContext);
                    break;
                case 4:
                    View textCell = new TextCell(this.mContext);
                    textCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    view = textCell;
                    break;
                default:
                    SuggestedFilterCell suggestedFilterCell = new SuggestedFilterCell(this.mContext);
                    suggestedFilterCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    suggestedFilterCell.setAddOnClickListener(new FiltersSetupActivity$ListAdapter$$ExternalSyntheticLambda3(this, suggestedFilterCell));
                    SuggestedFilterCell suggestedFilterCell2 = suggestedFilterCell;
                    view = suggestedFilterCell;
                    break;
            }
            return new RecyclerListView.Holder(view);
        }

        /* renamed from: lambda$onCreateViewHolder$0$org-telegram-ui-FiltersSetupActivity$ListAdapter  reason: not valid java name */
        public /* synthetic */ boolean m2937x834c8a63(FilterCell filterCell, View v, MotionEvent event) {
            if (event.getAction() != 0) {
                return false;
            }
            FiltersSetupActivity.this.itemTouchHelper.startDrag(FiltersSetupActivity.this.listView.getChildViewHolder(filterCell));
            return false;
        }

        /* renamed from: lambda$onCreateViewHolder$5$org-telegram-ui-FiltersSetupActivity$ListAdapter  reason: not valid java name */
        public /* synthetic */ void m2942x3b9cc8fe(View v) {
            MessagesController.DialogFilter filter = ((FilterCell) v.getParent()).getCurrentFilter();
            AlertDialog.Builder builder1 = new AlertDialog.Builder((Context) FiltersSetupActivity.this.getParentActivity());
            TextPaint paint = new TextPaint(1);
            paint.setTextSize((float) AndroidUtilities.dp(20.0f));
            builder1.setTitle(Emoji.replaceEmoji(filter.name, paint.getFontMetricsInt(), AndroidUtilities.dp(20.0f), false));
            CharSequence[] items = {LocaleController.getString("FilterEditItem", NUM), LocaleController.getString("FilterDeleteItem", NUM)};
            builder1.setItems(items, new int[]{NUM, NUM}, new FiltersSetupActivity$ListAdapter$$ExternalSyntheticLambda1(this, filter));
            AlertDialog dialog = builder1.create();
            FiltersSetupActivity.this.showDialog(dialog);
            dialog.setItemColor(items.length - 1, Theme.getColor("dialogTextRed2"), Theme.getColor("dialogRedIcon"));
        }

        /* renamed from: lambda$onCreateViewHolder$4$org-telegram-ui-FiltersSetupActivity$ListAdapter  reason: not valid java name */
        public /* synthetic */ void m2941x49var_df(MessagesController.DialogFilter filter, DialogInterface dialog, int which) {
            if (which == 0) {
                FiltersSetupActivity.this.presentFragment(new FilterCreateActivity(filter));
            } else if (which == 1) {
                AlertDialog.Builder builder = new AlertDialog.Builder((Context) FiltersSetupActivity.this.getParentActivity());
                builder.setTitle(LocaleController.getString("FilterDelete", NUM));
                builder.setMessage(LocaleController.getString("FilterDeleteAlert", NUM));
                builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                builder.setPositiveButton(LocaleController.getString("Delete", NUM), new FiltersSetupActivity$ListAdapter$$ExternalSyntheticLambda0(this, filter));
                AlertDialog alertDialog = builder.create();
                FiltersSetupActivity.this.showDialog(alertDialog);
                TextView button = (TextView) alertDialog.getButton(-1);
                if (button != null) {
                    button.setTextColor(Theme.getColor("dialogTextRed2"));
                }
            }
        }

        /* renamed from: lambda$onCreateViewHolder$3$org-telegram-ui-FiltersSetupActivity$ListAdapter  reason: not valid java name */
        public /* synthetic */ void m2940x58497cc0(MessagesController.DialogFilter filter, DialogInterface dialog2, int which2) {
            AlertDialog progressDialog = null;
            if (FiltersSetupActivity.this.getParentActivity() != null) {
                progressDialog = new AlertDialog(FiltersSetupActivity.this.getParentActivity(), 3);
                progressDialog.setCanCacnel(false);
                progressDialog.show();
            }
            TLRPC.TL_messages_updateDialogFilter req = new TLRPC.TL_messages_updateDialogFilter();
            req.id = filter.id;
            FiltersSetupActivity.this.getConnectionsManager().sendRequest(req, new FiltersSetupActivity$ListAdapter$$ExternalSyntheticLambda7(this, progressDialog, filter));
        }

        /* renamed from: lambda$onCreateViewHolder$2$org-telegram-ui-FiltersSetupActivity$ListAdapter  reason: not valid java name */
        public /* synthetic */ void m2939x669fd6a1(AlertDialog progressDialogFinal, MessagesController.DialogFilter filter, TLObject response, TLRPC.TL_error error) {
            AndroidUtilities.runOnUIThread(new FiltersSetupActivity$ListAdapter$$ExternalSyntheticLambda6(this, progressDialogFinal, filter));
        }

        /* renamed from: lambda$onCreateViewHolder$1$org-telegram-ui-FiltersSetupActivity$ListAdapter  reason: not valid java name */
        public /* synthetic */ void m2938x74var_(AlertDialog progressDialogFinal, MessagesController.DialogFilter filter) {
            if (progressDialogFinal != null) {
                try {
                    progressDialogFinal.dismiss();
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
            }
            int idx = FiltersSetupActivity.this.getMessagesController().dialogFilters.indexOf(filter);
            if (idx >= 0) {
                idx += FiltersSetupActivity.this.filtersStartRow;
            }
            boolean unused = FiltersSetupActivity.this.ignoreUpdates = true;
            FiltersSetupActivity.this.getMessagesController().removeFilter(filter);
            FiltersSetupActivity.this.getMessagesStorage().deleteDialogFilter(filter);
            boolean z = false;
            boolean unused2 = FiltersSetupActivity.this.ignoreUpdates = false;
            int prevAddRow = FiltersSetupActivity.this.createFilterRow;
            int prevRecommendedHeaderRow = FiltersSetupActivity.this.recommendedHeaderRow;
            FiltersSetupActivity filtersSetupActivity = FiltersSetupActivity.this;
            if (idx == -1) {
                z = true;
            }
            filtersSetupActivity.updateRows(z);
            if (idx != -1) {
                if (FiltersSetupActivity.this.filtersStartRow == -1) {
                    FiltersSetupActivity.this.adapter.notifyItemRangeRemoved(idx - 1, 2);
                } else {
                    FiltersSetupActivity.this.adapter.notifyItemRemoved(idx);
                }
                if (prevRecommendedHeaderRow == -1 && FiltersSetupActivity.this.recommendedHeaderRow != -1) {
                    FiltersSetupActivity.this.adapter.notifyItemRangeInserted(prevRecommendedHeaderRow, (FiltersSetupActivity.this.recommendedSectionRow - FiltersSetupActivity.this.recommendedHeaderRow) + 1);
                }
                if (prevAddRow == -1 && FiltersSetupActivity.this.createFilterRow != -1) {
                    FiltersSetupActivity.this.adapter.notifyItemInserted(FiltersSetupActivity.this.createFilterRow);
                }
            }
        }

        /* renamed from: lambda$onCreateViewHolder$7$org-telegram-ui-FiltersSetupActivity$ListAdapter  reason: not valid java name */
        public /* synthetic */ void m2944x1evar_c(SuggestedFilterCell suggestedFilterCell, View v) {
            long lowerId;
            TLRPC.TL_dialogFilterSuggested suggested = suggestedFilterCell.getSuggestedFilter();
            MessagesController.DialogFilter filter = new MessagesController.DialogFilter();
            filter.name = suggested.filter.title;
            filter.id = 2;
            while (FiltersSetupActivity.this.getMessagesController().dialogFiltersById.get(filter.id) != null) {
                filter.id++;
            }
            filter.unreadCount = -1;
            filter.pendingUnreadCount = -1;
            int b = 0;
            while (b < 2) {
                TLRPC.TL_dialogFilter tL_dialogFilter = suggested.filter;
                ArrayList<TLRPC.InputPeer> fromArray = b == 0 ? tL_dialogFilter.include_peers : tL_dialogFilter.exclude_peers;
                ArrayList<Long> toArray = b == 0 ? filter.alwaysShow : filter.neverShow;
                int N = fromArray.size();
                for (int a = 0; a < N; a++) {
                    TLRPC.InputPeer peer = fromArray.get(a);
                    if (peer.user_id != 0) {
                        lowerId = peer.user_id;
                    } else if (peer.chat_id != 0) {
                        lowerId = -peer.chat_id;
                    } else {
                        lowerId = -peer.channel_id;
                    }
                    toArray.add(Long.valueOf(lowerId));
                }
                b++;
            }
            if (suggested.filter.groups) {
                filter.flags |= MessagesController.DIALOG_FILTER_FLAG_GROUPS;
            }
            if (suggested.filter.bots) {
                filter.flags |= MessagesController.DIALOG_FILTER_FLAG_BOTS;
            }
            if (suggested.filter.contacts) {
                filter.flags |= MessagesController.DIALOG_FILTER_FLAG_CONTACTS;
            }
            if (suggested.filter.non_contacts) {
                filter.flags |= MessagesController.DIALOG_FILTER_FLAG_NON_CONTACTS;
            }
            if (suggested.filter.broadcasts) {
                filter.flags |= MessagesController.DIALOG_FILTER_FLAG_CHANNELS;
            }
            if (suggested.filter.exclude_archived) {
                filter.flags |= MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_ARCHIVED;
            }
            if (suggested.filter.exclude_read) {
                filter.flags |= MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_READ;
            }
            if (suggested.filter.exclude_muted) {
                filter.flags |= MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_MUTED;
            }
            boolean unused = FiltersSetupActivity.this.ignoreUpdates = true;
            FilterCreateActivity.saveFilterToServer(filter, filter.flags, filter.name, filter.alwaysShow, filter.neverShow, filter.pinnedDialogs, true, true, true, true, false, FiltersSetupActivity.this, new FiltersSetupActivity$ListAdapter$$ExternalSyntheticLambda5(this, suggested));
        }

        /* renamed from: lambda$onCreateViewHolder$6$org-telegram-ui-FiltersSetupActivity$ListAdapter  reason: not valid java name */
        public /* synthetic */ void m2943x2d466f1d(TLRPC.TL_dialogFilterSuggested suggested) {
            FiltersSetupActivity.this.getNotificationCenter().postNotificationName(NotificationCenter.dialogFiltersUpdated, new Object[0]);
            boolean unused = FiltersSetupActivity.this.ignoreUpdates = false;
            ArrayList<TLRPC.TL_dialogFilterSuggested> suggestedFilters = FiltersSetupActivity.this.getMessagesController().suggestedFilters;
            int index = suggestedFilters.indexOf(suggested);
            if (index != -1) {
                boolean wasEmpty = FiltersSetupActivity.this.filtersStartRow == -1;
                suggestedFilters.remove(index);
                int index2 = index + FiltersSetupActivity.this.recommendedStartRow;
                int prevAddRow = FiltersSetupActivity.this.createFilterRow;
                int prevRecommendedHeaderRow = FiltersSetupActivity.this.recommendedHeaderRow;
                int prevRecommendedSectionRow = FiltersSetupActivity.this.recommendedSectionRow;
                FiltersSetupActivity.this.updateRows(false);
                if (prevAddRow != -1 && FiltersSetupActivity.this.createFilterRow == -1) {
                    FiltersSetupActivity.this.adapter.notifyItemRemoved(prevAddRow);
                }
                if (prevRecommendedHeaderRow == -1 || FiltersSetupActivity.this.recommendedHeaderRow != -1) {
                    FiltersSetupActivity.this.adapter.notifyItemRemoved(index2);
                } else {
                    FiltersSetupActivity.this.adapter.notifyItemRangeRemoved(prevRecommendedHeaderRow, (prevRecommendedSectionRow - prevRecommendedHeaderRow) + 1);
                }
                if (wasEmpty) {
                    FiltersSetupActivity.this.adapter.notifyItemInserted(FiltersSetupActivity.this.filtersHeaderRow);
                }
                FiltersSetupActivity.this.adapter.notifyItemInserted(FiltersSetupActivity.this.filtersStartRow);
                return;
            }
            FiltersSetupActivity.this.updateRows(true);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            boolean z = false;
            switch (holder.getItemViewType()) {
                case 0:
                    HeaderCell headerCell = (HeaderCell) holder.itemView;
                    if (position == FiltersSetupActivity.this.filtersHeaderRow) {
                        headerCell.setText(LocaleController.getString("Filters", NUM));
                        return;
                    } else if (position == FiltersSetupActivity.this.recommendedHeaderRow) {
                        headerCell.setText(LocaleController.getString("FilterRecommended", NUM));
                        return;
                    } else {
                        return;
                    }
                case 2:
                    ((FilterCell) holder.itemView).setFilter(FiltersSetupActivity.this.getMessagesController().dialogFilters.get(position - FiltersSetupActivity.this.filtersStartRow), true);
                    return;
                case 3:
                    if (position == FiltersSetupActivity.this.createSectionRow) {
                        holder.itemView.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                        return;
                    } else {
                        holder.itemView.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                        return;
                    }
                case 4:
                    TextCell textCell = (TextCell) holder.itemView;
                    SharedPreferences notificationsSettings = MessagesController.getNotificationsSettings(FiltersSetupActivity.this.currentAccount);
                    if (position == FiltersSetupActivity.this.createFilterRow) {
                        Drawable drawable1 = this.mContext.getResources().getDrawable(NUM);
                        Drawable drawable2 = this.mContext.getResources().getDrawable(NUM);
                        drawable1.setColorFilter(new PorterDuffColorFilter(Theme.getColor("switchTrackChecked"), PorterDuff.Mode.MULTIPLY));
                        drawable2.setColorFilter(new PorterDuffColorFilter(Theme.getColor("checkboxCheck"), PorterDuff.Mode.MULTIPLY));
                        textCell.setTextAndIcon(LocaleController.getString("CreateNewFilter", NUM), new CombinedDrawable(drawable1, drawable2), false);
                        return;
                    }
                    return;
                case 5:
                    SuggestedFilterCell filterCell = (SuggestedFilterCell) holder.itemView;
                    TLRPC.TL_dialogFilterSuggested tL_dialogFilterSuggested = FiltersSetupActivity.this.getMessagesController().suggestedFilters.get(position - FiltersSetupActivity.this.recommendedStartRow);
                    if (FiltersSetupActivity.this.recommendedStartRow != FiltersSetupActivity.this.recommendedEndRow - 1) {
                        z = true;
                    }
                    filterCell.setFilter(tL_dialogFilterSuggested, z);
                    return;
                default:
                    return;
            }
        }

        public int getItemViewType(int position) {
            if (position == FiltersSetupActivity.this.filtersHeaderRow || position == FiltersSetupActivity.this.recommendedHeaderRow) {
                return 0;
            }
            if (position == FiltersSetupActivity.this.filterHelpRow) {
                return 1;
            }
            if (position >= FiltersSetupActivity.this.filtersStartRow && position < FiltersSetupActivity.this.filtersEndRow) {
                return 2;
            }
            if (position == FiltersSetupActivity.this.createSectionRow || position == FiltersSetupActivity.this.recommendedSectionRow) {
                return 3;
            }
            if (position == FiltersSetupActivity.this.createFilterRow) {
                return 4;
            }
            return 5;
        }

        public void swapElements(int fromIndex, int toIndex) {
            int idx1 = fromIndex - FiltersSetupActivity.this.filtersStartRow;
            int idx2 = toIndex - FiltersSetupActivity.this.filtersStartRow;
            int count = FiltersSetupActivity.this.filtersEndRow - FiltersSetupActivity.this.filtersStartRow;
            if (idx1 >= 0 && idx2 >= 0 && idx1 < count && idx2 < count) {
                ArrayList<MessagesController.DialogFilter> filters = FiltersSetupActivity.this.getMessagesController().dialogFilters;
                MessagesController.DialogFilter filter1 = filters.get(idx1);
                MessagesController.DialogFilter filter2 = filters.get(idx2);
                int temp = filter1.order;
                filter1.order = filter2.order;
                filter2.order = temp;
                filters.set(idx1, filter2);
                filters.set(idx2, filter1);
                boolean unused = FiltersSetupActivity.this.orderChanged = true;
                notifyItemMoved(fromIndex, toIndex);
            }
        }
    }

    public class TouchHelperCallback extends ItemTouchHelper.Callback {
        public TouchHelperCallback() {
        }

        public boolean isLongPressDragEnabled() {
            return true;
        }

        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            if (viewHolder.getItemViewType() != 2) {
                return makeMovementFlags(0, 0);
            }
            return makeMovementFlags(3, 0);
        }

        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder source, RecyclerView.ViewHolder target) {
            if (source.getItemViewType() != target.getItemViewType()) {
                return false;
            }
            FiltersSetupActivity.this.adapter.swapElements(source.getAdapterPosition(), target.getAdapterPosition());
            return true;
        }

        public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }

        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
            if (actionState != 0) {
                FiltersSetupActivity.this.listView.cancelClickRunnables(false);
                viewHolder.itemView.setPressed(true);
            }
            super.onSelectedChanged(viewHolder, actionState);
        }

        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        }

        public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            super.clearView(recyclerView, viewHolder);
            viewHolder.itemView.setPressed(false);
        }
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{HeaderCell.class, TextCell.class, FilterCell.class, SuggestedFilterCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        themeDescriptions.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultIcon"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultTitle"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "divider"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueHeader"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{FilterCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{FilterCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText2"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{FilterCell.class}, new String[]{"moveImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "stickers_menu"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{FilterCell.class}, new String[]{"optionsImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "stickers_menu"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, new Class[]{FilterCell.class}, new String[]{"optionsImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "stickers_menuSelector"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueText2"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrackChecked"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "checkboxCheck"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
        return themeDescriptions;
    }
}
