package org.telegram.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
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
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$InputPeer;
import org.telegram.tgnet.TLRPC$TL_dialogFilter;
import org.telegram.tgnet.TLRPC$TL_dialogFilterSuggested;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_messages_updateDialogFilter;
import org.telegram.tgnet.TLRPC$TL_messages_updateDialogFiltersOrder;
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
import org.telegram.ui.FiltersSetupActivity;

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

    static /* synthetic */ void lambda$onFragmentDestroy$0(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
    }

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
        public void onMeasure(int i, int i2) {
            int size = View.MeasureSpec.getSize(i);
            AndroidUtilities.dp(48.0f);
            this.textView.measure(View.MeasureSpec.makeMeasureSpec(size - AndroidUtilities.dp(94.0f), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(20.0f), NUM));
            this.imageView.measure(View.MeasureSpec.makeMeasureSpec(size, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(50.0f), NUM));
            setMeasuredDimension(size, AndroidUtilities.dp(50.0f));
        }

        /* access modifiers changed from: protected */
        public void onLayout(boolean z, int i, int i2, int i3, int i4) {
            int i5;
            int i6 = i3 - i;
            int textHeight = ((i4 - i2) - this.textView.getTextHeight()) / 2;
            float f = 64.0f;
            if (LocaleController.isRTL) {
                int measuredWidth = getMeasuredWidth() - this.textView.getMeasuredWidth();
                if (this.imageView.getVisibility() != 0) {
                    f = 23.0f;
                }
                i5 = measuredWidth - AndroidUtilities.dp(f);
            } else {
                if (this.imageView.getVisibility() != 0) {
                    f = 23.0f;
                }
                i5 = AndroidUtilities.dp(f);
            }
            SimpleTextView simpleTextView = this.textView;
            simpleTextView.layout(i5, textHeight, simpleTextView.getMeasuredWidth() + i5, this.textView.getMeasuredHeight() + textHeight);
            int dp = !LocaleController.isRTL ? AndroidUtilities.dp(20.0f) : (i6 - this.imageView.getMeasuredWidth()) - AndroidUtilities.dp(20.0f);
            ImageView imageView2 = this.imageView;
            imageView2.layout(dp, 0, imageView2.getMeasuredWidth() + dp, this.imageView.getMeasuredHeight());
        }

        public void setTextAndIcon(String str, Drawable drawable, boolean z) {
            this.textView.setText(str);
            this.imageView.setImageDrawable(drawable);
        }
    }

    public static class SuggestedFilterCell extends FrameLayout {
        private ProgressButton addButton;
        private boolean needDivider;
        private TLRPC$TL_dialogFilterSuggested suggestedFilter;
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
        public void onMeasure(int i, int i2) {
            setMeasuredDimension(View.MeasureSpec.getSize(i), AndroidUtilities.dp(64.0f));
            int i3 = i;
            int i4 = i2;
            measureChildWithMargins(this.addButton, i3, 0, i4, 0);
            measureChildWithMargins(this.textView, i3, this.addButton.getMeasuredWidth(), i4, 0);
            measureChildWithMargins(this.valueTextView, i3, this.addButton.getMeasuredWidth(), i4, 0);
        }

        public void setFilter(TLRPC$TL_dialogFilterSuggested tLRPC$TL_dialogFilterSuggested, boolean z) {
            this.needDivider = z;
            this.suggestedFilter = tLRPC$TL_dialogFilterSuggested;
            setWillNotDraw(!z);
            this.textView.setText(tLRPC$TL_dialogFilterSuggested.filter.title);
            this.valueTextView.setText(tLRPC$TL_dialogFilterSuggested.description);
        }

        public TLRPC$TL_dialogFilterSuggested getSuggestedFilter() {
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
            this.imageView.setOnClickListener(new View.OnClickListener() {
                public final void onClick(View view) {
                    FiltersSetupActivity.HintInnerCell.this.lambda$new$0$FiltersSetupActivity$HintInnerCell(view);
                }
            });
            TextView textView = new TextView(context);
            this.messageTextView = textView;
            textView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText4"));
            this.messageTextView.setTextSize(1, 14.0f);
            this.messageTextView.setGravity(17);
            this.messageTextView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("CreateNewFilterInfo", NUM, new Object[0])));
            addView(this.messageTextView, LayoutHelper.createFrame(-1, -2.0f, 49, 40.0f, 121.0f, 40.0f, 24.0f));
        }

        public /* synthetic */ void lambda$new$0$FiltersSetupActivity$HintInnerCell(View view) {
            if (!this.imageView.isPlaying()) {
                this.imageView.setProgress(0.0f);
                this.imageView.playAnimation();
            }
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int i, int i2) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM), i2);
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
            addView(this.optionsImageView, LayoutHelper.createFrame(40, 40.0f, (LocaleController.isRTL ? 3 : i) | 16, 6.0f, 0.0f, 6.0f, 0.0f));
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int i, int i2) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(50.0f), NUM));
        }

        public void setFilter(MessagesController.DialogFilter dialogFilter, boolean z) {
            this.currentFilter = dialogFilter;
            StringBuilder sb = new StringBuilder();
            int i = dialogFilter.flags;
            int i2 = MessagesController.DIALOG_FILTER_FLAG_ALL_CHATS;
            if ((i & i2) == i2) {
                sb.append(LocaleController.getString("FilterAllChats", NUM));
            } else {
                if ((i & MessagesController.DIALOG_FILTER_FLAG_CONTACTS) != 0) {
                    if (sb.length() != 0) {
                        sb.append(", ");
                    }
                    sb.append(LocaleController.getString("FilterContacts", NUM));
                }
                if ((dialogFilter.flags & MessagesController.DIALOG_FILTER_FLAG_NON_CONTACTS) != 0) {
                    if (sb.length() != 0) {
                        sb.append(", ");
                    }
                    sb.append(LocaleController.getString("FilterNonContacts", NUM));
                }
                if ((dialogFilter.flags & MessagesController.DIALOG_FILTER_FLAG_GROUPS) != 0) {
                    if (sb.length() != 0) {
                        sb.append(", ");
                    }
                    sb.append(LocaleController.getString("FilterGroups", NUM));
                }
                if ((dialogFilter.flags & MessagesController.DIALOG_FILTER_FLAG_CHANNELS) != 0) {
                    if (sb.length() != 0) {
                        sb.append(", ");
                    }
                    sb.append(LocaleController.getString("FilterChannels", NUM));
                }
                if ((dialogFilter.flags & MessagesController.DIALOG_FILTER_FLAG_BOTS) != 0) {
                    if (sb.length() != 0) {
                        sb.append(", ");
                    }
                    sb.append(LocaleController.getString("FilterBots", NUM));
                }
            }
            if (!dialogFilter.alwaysShow.isEmpty() || !dialogFilter.neverShow.isEmpty()) {
                if (sb.length() != 0) {
                    sb.append(", ");
                }
                sb.append(LocaleController.formatPluralString("Exception", dialogFilter.alwaysShow.size() + dialogFilter.neverShow.size()));
            }
            if (sb.length() == 0) {
                sb.append(LocaleController.getString("FilterNoChats", NUM));
            }
            this.textView.setText(dialogFilter.name);
            this.valueTextView.setText(sb);
            this.needDivider = z;
        }

        public MessagesController.DialogFilter getCurrentFilter() {
            return this.currentFilter;
        }

        public void setOnOptionsClick(View.OnClickListener onClickListener) {
            this.optionsImageView.setOnClickListener(onClickListener);
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            if (this.needDivider) {
                canvas.drawLine(LocaleController.isRTL ? 0.0f : (float) AndroidUtilities.dp(62.0f), (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(62.0f) : 0)), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
            }
        }

        @SuppressLint({"ClickableViewAccessibility"})
        public void setOnReorderButtonTouchListener(View.OnTouchListener onTouchListener) {
            this.moveImageView.setOnTouchListener(onTouchListener);
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
    public void updateRows(boolean z) {
        ListAdapter listAdapter;
        this.recommendedHeaderRow = -1;
        this.recommendedStartRow = -1;
        this.recommendedEndRow = -1;
        this.recommendedSectionRow = -1;
        ArrayList<TLRPC$TL_dialogFilterSuggested> arrayList = getMessagesController().suggestedFilters;
        this.rowCount = 0;
        this.rowCount = 0 + 1;
        this.filterHelpRow = 0;
        int size = getMessagesController().dialogFilters.size();
        if (!arrayList.isEmpty()) {
            int i = this.rowCount;
            int i2 = i + 1;
            this.rowCount = i2;
            this.recommendedHeaderRow = i;
            this.recommendedStartRow = i2;
            int size2 = i2 + arrayList.size();
            this.rowCount = size2;
            this.recommendedEndRow = size2;
            this.rowCount = size2 + 1;
            this.recommendedSectionRow = size2;
        }
        if (size != 0) {
            int i3 = this.rowCount;
            int i4 = i3 + 1;
            this.rowCount = i4;
            this.filtersHeaderRow = i3;
            this.filtersStartRow = i4;
            int i5 = i4 + size;
            this.rowCount = i5;
            this.filtersEndRow = i5;
        } else {
            this.filtersHeaderRow = -1;
            this.filtersStartRow = -1;
            this.filtersEndRow = -1;
        }
        if (size < 10) {
            int i6 = this.rowCount;
            this.rowCount = i6 + 1;
            this.createFilterRow = i6;
        }
        int i7 = this.rowCount;
        this.rowCount = i7 + 1;
        this.createSectionRow = i7;
        if (z && (listAdapter = this.adapter) != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    public void onFragmentDestroy() {
        getNotificationCenter().removeObserver(this, NotificationCenter.dialogFiltersUpdated);
        getNotificationCenter().removeObserver(this, NotificationCenter.suggestedFiltersLoaded);
        if (this.orderChanged) {
            getNotificationCenter().postNotificationName(NotificationCenter.dialogFiltersUpdated, new Object[0]);
            getMessagesStorage().saveDialogFiltersOrder();
            TLRPC$TL_messages_updateDialogFiltersOrder tLRPC$TL_messages_updateDialogFiltersOrder = new TLRPC$TL_messages_updateDialogFiltersOrder();
            ArrayList<MessagesController.DialogFilter> arrayList = getMessagesController().dialogFilters;
            int size = arrayList.size();
            for (int i = 0; i < size; i++) {
                MessagesController.DialogFilter dialogFilter = arrayList.get(i);
                tLRPC$TL_messages_updateDialogFiltersOrder.order.add(Integer.valueOf(arrayList.get(i).id));
            }
            getConnectionsManager().sendRequest(tLRPC$TL_messages_updateDialogFiltersOrder, $$Lambda$FiltersSetupActivity$7RMcyJ2dere3xqViHa508bWjjRU.INSTANCE);
        }
        super.onFragmentDestroy();
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("Filters", NUM));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    FiltersSetupActivity.this.finishFragment();
                }
            }
        });
        FrameLayout frameLayout = new FrameLayout(context);
        this.fragmentView = frameLayout;
        FrameLayout frameLayout2 = frameLayout;
        frameLayout2.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
        RecyclerListView recyclerListView = new RecyclerListView(context);
        this.listView = recyclerListView;
        ((DefaultItemAnimator) recyclerListView.getItemAnimator()).setDelayAnimations(false);
        this.listView.setLayoutManager(new LinearLayoutManager(context, 1, false));
        this.listView.setVerticalScrollBarEnabled(false);
        ItemTouchHelper itemTouchHelper2 = new ItemTouchHelper(new TouchHelperCallback());
        this.itemTouchHelper = itemTouchHelper2;
        itemTouchHelper2.attachToRecyclerView(this.listView);
        frameLayout2.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        RecyclerListView recyclerListView2 = this.listView;
        ListAdapter listAdapter = new ListAdapter(context);
        this.adapter = listAdapter;
        recyclerListView2.setAdapter(listAdapter);
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListenerExtended) new RecyclerListView.OnItemClickListenerExtended() {
            public final void onItemClick(View view, int i, float f, float f2) {
                FiltersSetupActivity.this.lambda$createView$1$FiltersSetupActivity(view, i, f, f2);
            }
        });
        return this.fragmentView;
    }

    public /* synthetic */ void lambda$createView$1$FiltersSetupActivity(View view, int i, float f, float f2) {
        if (i >= this.filtersStartRow && i < this.filtersEndRow) {
            presentFragment(new FilterCreateActivity(getMessagesController().dialogFilters.get(i - this.filtersStartRow)));
        } else if (i == this.createFilterRow) {
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

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.dialogFiltersUpdated) {
            if (!this.ignoreUpdates) {
                updateRows(true);
            }
        } else if (i == NotificationCenter.suggestedFiltersLoaded) {
            updateRows(true);
        }
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            int itemViewType = viewHolder.getItemViewType();
            return (itemViewType == 3 || itemViewType == 0 || itemViewType == 5 || itemViewType == 1) ? false : true;
        }

        public int getItemCount() {
            return FiltersSetupActivity.this.rowCount;
        }

        public /* synthetic */ boolean lambda$onCreateViewHolder$0$FiltersSetupActivity$ListAdapter(FilterCell filterCell, View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() != 0) {
                return false;
            }
            FiltersSetupActivity.this.itemTouchHelper.startDrag(FiltersSetupActivity.this.listView.getChildViewHolder(filterCell));
            return false;
        }

        public /* synthetic */ void lambda$onCreateViewHolder$5$FiltersSetupActivity$ListAdapter(View view) {
            MessagesController.DialogFilter currentFilter = ((FilterCell) view.getParent()).getCurrentFilter();
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) FiltersSetupActivity.this.getParentActivity());
            builder.setTitle(currentFilter.name);
            builder.setItems(new CharSequence[]{LocaleController.getString("FilterEditItem", NUM), LocaleController.getString("FilterDeleteItem", NUM)}, new int[]{NUM, NUM}, new DialogInterface.OnClickListener(currentFilter) {
                private final /* synthetic */ MessagesController.DialogFilter f$1;

                {
                    this.f$1 = r2;
                }

                public final void onClick(DialogInterface dialogInterface, int i) {
                    FiltersSetupActivity.ListAdapter.this.lambda$null$4$FiltersSetupActivity$ListAdapter(this.f$1, dialogInterface, i);
                }
            });
            AlertDialog create = builder.create();
            FiltersSetupActivity.this.showDialog(create);
            create.setItemColor(1, Theme.getColor("dialogTextRed2"), Theme.getColor("dialogRedIcon"));
        }

        public /* synthetic */ void lambda$null$4$FiltersSetupActivity$ListAdapter(MessagesController.DialogFilter dialogFilter, DialogInterface dialogInterface, int i) {
            if (i == 0) {
                FiltersSetupActivity.this.presentFragment(new FilterCreateActivity(dialogFilter));
            } else if (i == 1) {
                AlertDialog.Builder builder = new AlertDialog.Builder((Context) FiltersSetupActivity.this.getParentActivity());
                builder.setTitle(LocaleController.getString("FilterDelete", NUM));
                builder.setMessage(LocaleController.getString("FilterDeleteAlert", NUM));
                builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                builder.setPositiveButton(LocaleController.getString("Delete", NUM), new DialogInterface.OnClickListener(dialogFilter) {
                    private final /* synthetic */ MessagesController.DialogFilter f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void onClick(DialogInterface dialogInterface, int i) {
                        FiltersSetupActivity.ListAdapter.this.lambda$null$3$FiltersSetupActivity$ListAdapter(this.f$1, dialogInterface, i);
                    }
                });
                AlertDialog create = builder.create();
                FiltersSetupActivity.this.showDialog(create);
                TextView textView = (TextView) create.getButton(-1);
                if (textView != null) {
                    textView.setTextColor(Theme.getColor("dialogTextRed2"));
                }
            }
        }

        public /* synthetic */ void lambda$null$3$FiltersSetupActivity$ListAdapter(MessagesController.DialogFilter dialogFilter, DialogInterface dialogInterface, int i) {
            AlertDialog alertDialog;
            if (FiltersSetupActivity.this.getParentActivity() != null) {
                alertDialog = new AlertDialog(FiltersSetupActivity.this.getParentActivity(), 3);
                alertDialog.setCanCacnel(false);
                alertDialog.show();
            } else {
                alertDialog = null;
            }
            TLRPC$TL_messages_updateDialogFilter tLRPC$TL_messages_updateDialogFilter = new TLRPC$TL_messages_updateDialogFilter();
            tLRPC$TL_messages_updateDialogFilter.id = dialogFilter.id;
            FiltersSetupActivity.this.getConnectionsManager().sendRequest(tLRPC$TL_messages_updateDialogFilter, new RequestDelegate(alertDialog, dialogFilter) {
                private final /* synthetic */ AlertDialog f$1;
                private final /* synthetic */ MessagesController.DialogFilter f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    FiltersSetupActivity.ListAdapter.this.lambda$null$2$FiltersSetupActivity$ListAdapter(this.f$1, this.f$2, tLObject, tLRPC$TL_error);
                }
            });
        }

        public /* synthetic */ void lambda$null$2$FiltersSetupActivity$ListAdapter(AlertDialog alertDialog, MessagesController.DialogFilter dialogFilter, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
            AndroidUtilities.runOnUIThread(new Runnable(alertDialog, dialogFilter) {
                private final /* synthetic */ AlertDialog f$1;
                private final /* synthetic */ MessagesController.DialogFilter f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    FiltersSetupActivity.ListAdapter.this.lambda$null$1$FiltersSetupActivity$ListAdapter(this.f$1, this.f$2);
                }
            });
        }

        public /* synthetic */ void lambda$null$1$FiltersSetupActivity$ListAdapter(AlertDialog alertDialog, MessagesController.DialogFilter dialogFilter) {
            if (alertDialog != null) {
                try {
                    alertDialog.dismiss();
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
            }
            int indexOf = FiltersSetupActivity.this.getMessagesController().dialogFilters.indexOf(dialogFilter);
            if (indexOf >= 0) {
                indexOf += FiltersSetupActivity.this.filtersStartRow;
            }
            boolean unused = FiltersSetupActivity.this.ignoreUpdates = true;
            FiltersSetupActivity.this.getMessagesController().removeFilter(dialogFilter);
            FiltersSetupActivity.this.getMessagesStorage().deleteDialogFilter(dialogFilter);
            boolean z = false;
            boolean unused2 = FiltersSetupActivity.this.ignoreUpdates = false;
            FiltersSetupActivity filtersSetupActivity = FiltersSetupActivity.this;
            if (indexOf == -1) {
                z = true;
            }
            filtersSetupActivity.updateRows(z);
            if (indexOf == -1) {
                return;
            }
            if (FiltersSetupActivity.this.filtersStartRow == -1) {
                FiltersSetupActivity.this.adapter.notifyItemRangeRemoved(indexOf - 1, 2);
            } else {
                FiltersSetupActivity.this.adapter.notifyItemRemoved(indexOf);
            }
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            SuggestedFilterCell suggestedFilterCell;
            if (i == 0) {
                HeaderCell headerCell = new HeaderCell(this.mContext);
                headerCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                suggestedFilterCell = headerCell;
            } else if (i == 1) {
                HintInnerCell hintInnerCell = new HintInnerCell(this.mContext);
                hintInnerCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                suggestedFilterCell = hintInnerCell;
            } else if (i == 2) {
                FilterCell filterCell = new FilterCell(this.mContext);
                filterCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                filterCell.setOnReorderButtonTouchListener(new View.OnTouchListener(filterCell) {
                    private final /* synthetic */ FiltersSetupActivity.FilterCell f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final boolean onTouch(View view, MotionEvent motionEvent) {
                        return FiltersSetupActivity.ListAdapter.this.lambda$onCreateViewHolder$0$FiltersSetupActivity$ListAdapter(this.f$1, view, motionEvent);
                    }
                });
                filterCell.setOnOptionsClick(new View.OnClickListener() {
                    public final void onClick(View view) {
                        FiltersSetupActivity.ListAdapter.this.lambda$onCreateViewHolder$5$FiltersSetupActivity$ListAdapter(view);
                    }
                });
                suggestedFilterCell = filterCell;
            } else if (i == 3) {
                suggestedFilterCell = new ShadowSectionCell(this.mContext);
            } else if (i != 4) {
                SuggestedFilterCell suggestedFilterCell2 = new SuggestedFilterCell(this.mContext);
                suggestedFilterCell2.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                suggestedFilterCell2.setAddOnClickListener(new View.OnClickListener(suggestedFilterCell2) {
                    private final /* synthetic */ FiltersSetupActivity.SuggestedFilterCell f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void onClick(View view) {
                        FiltersSetupActivity.ListAdapter.this.lambda$onCreateViewHolder$7$FiltersSetupActivity$ListAdapter(this.f$1, view);
                    }
                });
                suggestedFilterCell = suggestedFilterCell2;
            } else {
                TextCell textCell = new TextCell(this.mContext);
                textCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                suggestedFilterCell = textCell;
            }
            return new RecyclerListView.Holder(suggestedFilterCell);
        }

        public /* synthetic */ void lambda$onCreateViewHolder$7$FiltersSetupActivity$ListAdapter(SuggestedFilterCell suggestedFilterCell, View view) {
            TLRPC$TL_dialogFilterSuggested suggestedFilter = suggestedFilterCell.getSuggestedFilter();
            MessagesController.DialogFilter dialogFilter = new MessagesController.DialogFilter();
            dialogFilter.name = suggestedFilter.filter.title;
            dialogFilter.id = 2;
            while (FiltersSetupActivity.this.getMessagesController().dialogFiltersById.get(dialogFilter.id) != null) {
                dialogFilter.id++;
            }
            dialogFilter.unreadCount = -1;
            dialogFilter.pendingUnreadCount = -1;
            int i = 0;
            while (i < 2) {
                TLRPC$TL_dialogFilter tLRPC$TL_dialogFilter = suggestedFilter.filter;
                ArrayList<TLRPC$InputPeer> arrayList = i == 0 ? tLRPC$TL_dialogFilter.include_peers : tLRPC$TL_dialogFilter.exclude_peers;
                ArrayList<Integer> arrayList2 = i == 0 ? dialogFilter.alwaysShow : dialogFilter.neverShow;
                int size = arrayList.size();
                for (int i2 = 0; i2 < size; i2++) {
                    TLRPC$InputPeer tLRPC$InputPeer = arrayList.get(i2);
                    int i3 = tLRPC$InputPeer.user_id;
                    if (i3 == 0) {
                        int i4 = tLRPC$InputPeer.chat_id;
                        if (i4 != 0) {
                            i3 = -i4;
                        } else {
                            i3 = -tLRPC$InputPeer.channel_id;
                        }
                    }
                    arrayList2.add(Integer.valueOf(i3));
                }
                i++;
            }
            if (suggestedFilter.filter.groups) {
                dialogFilter.flags |= MessagesController.DIALOG_FILTER_FLAG_GROUPS;
            }
            if (suggestedFilter.filter.bots) {
                dialogFilter.flags |= MessagesController.DIALOG_FILTER_FLAG_BOTS;
            }
            if (suggestedFilter.filter.contacts) {
                dialogFilter.flags |= MessagesController.DIALOG_FILTER_FLAG_CONTACTS;
            }
            if (suggestedFilter.filter.non_contacts) {
                dialogFilter.flags |= MessagesController.DIALOG_FILTER_FLAG_NON_CONTACTS;
            }
            if (suggestedFilter.filter.broadcasts) {
                dialogFilter.flags |= MessagesController.DIALOG_FILTER_FLAG_CHANNELS;
            }
            if (suggestedFilter.filter.exclude_archived) {
                dialogFilter.flags |= MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_ARCHIVED;
            }
            if (suggestedFilter.filter.exclude_read) {
                dialogFilter.flags |= MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_READ;
            }
            if (suggestedFilter.filter.exclude_muted) {
                dialogFilter.flags |= MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_MUTED;
            }
            boolean unused = FiltersSetupActivity.this.ignoreUpdates = true;
            FilterCreateActivity.saveFilterToServer(dialogFilter, dialogFilter.flags, dialogFilter.name, dialogFilter.alwaysShow, dialogFilter.neverShow, dialogFilter.pinnedDialogs, true, true, true, false, FiltersSetupActivity.this, new Runnable(suggestedFilter) {
                private final /* synthetic */ TLRPC$TL_dialogFilterSuggested f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    FiltersSetupActivity.ListAdapter.this.lambda$null$6$FiltersSetupActivity$ListAdapter(this.f$1);
                }
            });
        }

        public /* synthetic */ void lambda$null$6$FiltersSetupActivity$ListAdapter(TLRPC$TL_dialogFilterSuggested tLRPC$TL_dialogFilterSuggested) {
            FiltersSetupActivity.this.getNotificationCenter().postNotificationName(NotificationCenter.dialogFiltersUpdated, new Object[0]);
            boolean unused = FiltersSetupActivity.this.ignoreUpdates = false;
            ArrayList<TLRPC$TL_dialogFilterSuggested> arrayList = FiltersSetupActivity.this.getMessagesController().suggestedFilters;
            int indexOf = arrayList.indexOf(tLRPC$TL_dialogFilterSuggested);
            if (indexOf != -1) {
                boolean z = FiltersSetupActivity.this.filtersStartRow == -1;
                arrayList.remove(indexOf);
                int access$700 = indexOf + FiltersSetupActivity.this.recommendedStartRow;
                FiltersSetupActivity.this.updateRows(false);
                if (arrayList.isEmpty()) {
                    FiltersSetupActivity.this.adapter.notifyItemRangeRemoved(access$700 - 1, 3);
                } else {
                    FiltersSetupActivity.this.adapter.notifyItemRemoved(access$700);
                }
                if (z) {
                    FiltersSetupActivity.this.adapter.notifyItemInserted(FiltersSetupActivity.this.filtersHeaderRow);
                }
                FiltersSetupActivity.this.adapter.notifyItemInserted(FiltersSetupActivity.this.filtersStartRow);
                return;
            }
            FiltersSetupActivity.this.updateRows(true);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            int itemViewType = viewHolder.getItemViewType();
            if (itemViewType != 0) {
                boolean z = true;
                if (itemViewType == 2) {
                    ((FilterCell) viewHolder.itemView).setFilter(FiltersSetupActivity.this.getMessagesController().dialogFilters.get(i - FiltersSetupActivity.this.filtersStartRow), true);
                } else if (itemViewType != 3) {
                    if (itemViewType == 4) {
                        TextCell textCell = (TextCell) viewHolder.itemView;
                        MessagesController.getNotificationsSettings(FiltersSetupActivity.this.currentAccount);
                        if (i == FiltersSetupActivity.this.createFilterRow) {
                            Drawable drawable = this.mContext.getResources().getDrawable(NUM);
                            Drawable drawable2 = this.mContext.getResources().getDrawable(NUM);
                            drawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor("switchTrackChecked"), PorterDuff.Mode.MULTIPLY));
                            drawable2.setColorFilter(new PorterDuffColorFilter(Theme.getColor("checkboxCheck"), PorterDuff.Mode.MULTIPLY));
                            textCell.setTextAndIcon(LocaleController.getString("CreateNewFilter", NUM), new CombinedDrawable(drawable, drawable2), false);
                        }
                    } else if (itemViewType == 5) {
                        SuggestedFilterCell suggestedFilterCell = (SuggestedFilterCell) viewHolder.itemView;
                        TLRPC$TL_dialogFilterSuggested tLRPC$TL_dialogFilterSuggested = FiltersSetupActivity.this.getMessagesController().suggestedFilters.get(i - FiltersSetupActivity.this.recommendedStartRow);
                        if (FiltersSetupActivity.this.recommendedStartRow == FiltersSetupActivity.this.recommendedEndRow - 1) {
                            z = false;
                        }
                        suggestedFilterCell.setFilter(tLRPC$TL_dialogFilterSuggested, z);
                    }
                } else if (i == FiltersSetupActivity.this.createSectionRow) {
                    viewHolder.itemView.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                } else {
                    viewHolder.itemView.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                }
            } else {
                HeaderCell headerCell = (HeaderCell) viewHolder.itemView;
                if (i == FiltersSetupActivity.this.filtersHeaderRow) {
                    headerCell.setText(LocaleController.getString("Filters", NUM));
                } else if (i == FiltersSetupActivity.this.recommendedHeaderRow) {
                    headerCell.setText(LocaleController.getString("FilterRecommended", NUM));
                }
            }
        }

        public int getItemViewType(int i) {
            if (i == FiltersSetupActivity.this.filtersHeaderRow || i == FiltersSetupActivity.this.recommendedHeaderRow) {
                return 0;
            }
            if (i == FiltersSetupActivity.this.filterHelpRow) {
                return 1;
            }
            if (i >= FiltersSetupActivity.this.filtersStartRow && i < FiltersSetupActivity.this.filtersEndRow) {
                return 2;
            }
            if (i == FiltersSetupActivity.this.createSectionRow || i == FiltersSetupActivity.this.recommendedSectionRow) {
                return 3;
            }
            return i == FiltersSetupActivity.this.createFilterRow ? 4 : 5;
        }

        public void swapElements(int i, int i2) {
            int access$300 = i - FiltersSetupActivity.this.filtersStartRow;
            int access$3002 = i2 - FiltersSetupActivity.this.filtersStartRow;
            int access$1000 = FiltersSetupActivity.this.filtersEndRow - FiltersSetupActivity.this.filtersStartRow;
            if (access$300 >= 0 && access$3002 >= 0 && access$300 < access$1000 && access$3002 < access$1000) {
                ArrayList<MessagesController.DialogFilter> arrayList = FiltersSetupActivity.this.getMessagesController().dialogFilters;
                MessagesController.DialogFilter dialogFilter = arrayList.get(access$300);
                MessagesController.DialogFilter dialogFilter2 = arrayList.get(access$3002);
                int i3 = dialogFilter.order;
                dialogFilter.order = dialogFilter2.order;
                dialogFilter2.order = i3;
                arrayList.set(access$300, dialogFilter2);
                arrayList.set(access$3002, dialogFilter);
                boolean unused = FiltersSetupActivity.this.orderChanged = true;
                notifyItemMoved(i, i2);
            }
        }
    }

    public class TouchHelperCallback extends ItemTouchHelper.Callback {
        public boolean isLongPressDragEnabled() {
            return true;
        }

        public void onSwiped(RecyclerView.ViewHolder viewHolder, int i) {
        }

        public TouchHelperCallback() {
        }

        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            if (viewHolder.getItemViewType() != 2) {
                return ItemTouchHelper.Callback.makeMovementFlags(0, 0);
            }
            return ItemTouchHelper.Callback.makeMovementFlags(3, 0);
        }

        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder viewHolder2) {
            if (viewHolder.getItemViewType() != viewHolder2.getItemViewType()) {
                return false;
            }
            FiltersSetupActivity.this.adapter.swapElements(viewHolder.getAdapterPosition(), viewHolder2.getAdapterPosition());
            return true;
        }

        public void onChildDraw(Canvas canvas, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float f, float f2, int i, boolean z) {
            super.onChildDraw(canvas, recyclerView, viewHolder, f, f2, i, z);
        }

        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int i) {
            if (i != 0) {
                FiltersSetupActivity.this.listView.cancelClickRunnables(false);
                viewHolder.itemView.setPressed(true);
            }
            super.onSelectedChanged(viewHolder, i);
        }

        public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            super.clearView(recyclerView, viewHolder);
            viewHolder.itemView.setPressed(false);
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        return new ThemeDescription[]{new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{HeaderCell.class, TextCell.class, FilterCell.class, SuggestedFilterCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"), new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"), new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultIcon"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultTitle"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"), new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"), new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "divider"), new ThemeDescription((View) this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueHeader"), new ThemeDescription((View) this.listView, 0, new Class[]{FilterCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"), new ThemeDescription((View) this.listView, 0, new Class[]{FilterCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText2"), new ThemeDescription((View) this.listView, 0, new Class[]{FilterCell.class}, new String[]{"moveImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "stickers_menu"), new ThemeDescription((View) this.listView, 0, new Class[]{FilterCell.class}, new String[]{"optionsImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "stickers_menu"), new ThemeDescription((View) this.listView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, new Class[]{FilterCell.class}, new String[]{"optionsImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "stickers_menuSelector"), new ThemeDescription((View) this.listView, 0, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueText2"), new ThemeDescription((View) this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrackChecked"), new ThemeDescription((View) this.listView, 0, new Class[]{TextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "checkboxCheck"), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow")};
    }
}
