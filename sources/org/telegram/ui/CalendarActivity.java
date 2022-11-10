package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextPaint;
import android.util.SparseArray;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.core.view.GestureDetectorCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import j$.time.YearMonth;
import java.util.ArrayList;
import java.util.Calendar;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.R;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$Message;
import org.telegram.tgnet.TLRPC$MessageMedia;
import org.telegram.tgnet.TLRPC$PhotoSize;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_inputMessagesFilterPhotoVideo;
import org.telegram.tgnet.TLRPC$TL_inputMessagesFilterPhotos;
import org.telegram.tgnet.TLRPC$TL_inputMessagesFilterVideo;
import org.telegram.tgnet.TLRPC$TL_messageMediaPhoto;
import org.telegram.tgnet.TLRPC$TL_messages_getSearchResultsCalendar;
import org.telegram.tgnet.TLRPC$TL_messages_searchResultsCalendar;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.BackDrawable;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.CalendarActivity;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.Easings;
import org.telegram.ui.Components.HideViewAfterAnimation;
import org.telegram.ui.Components.HintView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
/* loaded from: classes3.dex */
public class CalendarActivity extends BaseFragment {
    TextPaint activeTextPaint;
    CalendarAdapter adapter;
    BackDrawable backDrawable;
    Paint blackoutPaint;
    private View blurredView;
    private FrameLayout bottomBar;
    private int calendarType;
    Callback callback;
    private boolean canClearHistory;
    private boolean checkEnterItems;
    FrameLayout contentView;
    private int dateSelectedEnd;
    private int dateSelectedStart;
    private long dialogId;
    boolean endReached;
    private boolean inSelectionMode;
    private boolean isOpened;
    int lastDaysSelected;
    int lastId;
    boolean lastInSelectionMode;
    LinearLayoutManager layoutManager;
    RecyclerListView listView;
    private boolean loading;
    SparseArray<SparseArray<PeriodDay>> messagesByYearMounth;
    private int minDate;
    int minMontYear;
    int monthCount;
    private int photosVideosTypeFilter;
    TextView removeDaysButton;
    TextView selectDaysButton;
    HintView selectDaysHint;
    private Paint selectOutlinePaint;
    private Paint selectPaint;
    int selectedMonth;
    int selectedYear;
    private ValueAnimator selectionAnimator;
    int startFromMonth;
    int startFromYear;
    int startOffset;
    TextPaint textPaint;
    TextPaint textPaint2;

    /* loaded from: classes3.dex */
    public interface Callback {
        void onDateSelected(int i, int i2);
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean needDelayOpenAnimation() {
        return true;
    }

    public CalendarActivity(Bundle bundle, int i, int i2) {
        super(bundle);
        this.textPaint = new TextPaint(1);
        this.activeTextPaint = new TextPaint(1);
        this.textPaint2 = new TextPaint(1);
        this.selectOutlinePaint = new Paint(1);
        this.selectPaint = new Paint(1);
        this.blackoutPaint = new Paint(1);
        this.messagesByYearMounth = new SparseArray<>();
        this.startOffset = 0;
        this.photosVideosTypeFilter = i;
        if (i2 != 0) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(i2 * 1000);
            this.selectedYear = calendar.get(1);
            this.selectedMonth = calendar.get(2);
        }
        this.selectOutlinePaint.setStyle(Paint.Style.STROKE);
        this.selectOutlinePaint.setStrokeCap(Paint.Cap.ROUND);
        this.selectOutlinePaint.setStrokeWidth(AndroidUtilities.dp(2.0f));
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean onFragmentCreate() {
        this.dialogId = getArguments().getLong("dialog_id");
        getArguments().getInt("topic_id");
        this.calendarType = getArguments().getInt("type");
        if (this.dialogId >= 0) {
            this.canClearHistory = true;
        } else {
            this.canClearHistory = false;
        }
        return super.onFragmentCreate();
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public View createView(Context context) {
        this.textPaint.setTextSize(AndroidUtilities.dp(16.0f));
        this.textPaint.setTextAlign(Paint.Align.CENTER);
        this.textPaint2.setTextSize(AndroidUtilities.dp(11.0f));
        this.textPaint2.setTextAlign(Paint.Align.CENTER);
        this.textPaint2.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.activeTextPaint.setTextSize(AndroidUtilities.dp(16.0f));
        this.activeTextPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.activeTextPaint.setTextAlign(Paint.Align.CENTER);
        this.contentView = new FrameLayout(context) { // from class: org.telegram.ui.CalendarActivity.1
            int lastSize;

            @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
            protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
                super.onLayout(z, i, i2, i3, i4);
                int measuredHeight = (getMeasuredHeight() + getMeasuredWidth()) << 16;
                if (this.lastSize != measuredHeight) {
                    this.lastSize = measuredHeight;
                    CalendarActivity.this.adapter.notifyDataSetChanged();
                }
            }
        };
        createActionBar(context);
        this.contentView.addView(this.actionBar);
        this.actionBar.setTitle(LocaleController.getString("Calendar", R.string.Calendar));
        this.actionBar.setCastShadows(false);
        RecyclerListView recyclerListView = new RecyclerListView(context) { // from class: org.telegram.ui.CalendarActivity.2
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // org.telegram.ui.Components.RecyclerListView, android.view.ViewGroup, android.view.View
            public void dispatchDraw(Canvas canvas) {
                super.dispatchDraw(canvas);
                CalendarActivity.this.checkEnterItems = false;
            }
        };
        this.listView = recyclerListView;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        this.layoutManager = linearLayoutManager;
        recyclerListView.setLayoutManager(linearLayoutManager);
        this.layoutManager.setReverseLayout(true);
        RecyclerListView recyclerListView2 = this.listView;
        CalendarAdapter calendarAdapter = new CalendarAdapter();
        this.adapter = calendarAdapter;
        recyclerListView2.setAdapter(calendarAdapter);
        this.listView.addOnScrollListener(new RecyclerView.OnScrollListener() { // from class: org.telegram.ui.CalendarActivity.3
            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                super.onScrolled(recyclerView, i, i2);
                CalendarActivity.this.checkLoadNext();
            }
        });
        boolean z = this.calendarType == 0 && this.canClearHistory;
        this.contentView.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f, 0, 0.0f, 36.0f, 0.0f, z ? 48.0f : 0.0f));
        final String[] strArr = {LocaleController.getString("CalendarWeekNameShortMonday", R.string.CalendarWeekNameShortMonday), LocaleController.getString("CalendarWeekNameShortTuesday", R.string.CalendarWeekNameShortTuesday), LocaleController.getString("CalendarWeekNameShortWednesday", R.string.CalendarWeekNameShortWednesday), LocaleController.getString("CalendarWeekNameShortThursday", R.string.CalendarWeekNameShortThursday), LocaleController.getString("CalendarWeekNameShortFriday", R.string.CalendarWeekNameShortFriday), LocaleController.getString("CalendarWeekNameShortSaturday", R.string.CalendarWeekNameShortSaturday), LocaleController.getString("CalendarWeekNameShortSunday", R.string.CalendarWeekNameShortSunday)};
        final Drawable mutate = ContextCompat.getDrawable(context, R.drawable.header_shadow).mutate();
        this.contentView.addView(new View(context) { // from class: org.telegram.ui.CalendarActivity.4
            @Override // android.view.View
            protected void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                float measuredWidth = getMeasuredWidth() / 7.0f;
                for (int i = 0; i < 7; i++) {
                    canvas.drawText(strArr[i], (i * measuredWidth) + (measuredWidth / 2.0f), ((getMeasuredHeight() - AndroidUtilities.dp(2.0f)) / 2.0f) + AndroidUtilities.dp(5.0f), CalendarActivity.this.textPaint2);
                }
                mutate.setBounds(0, getMeasuredHeight() - AndroidUtilities.dp(3.0f), getMeasuredWidth(), getMeasuredHeight());
                mutate.draw(canvas);
            }
        }, LayoutHelper.createFrame(-1, 38.0f, 0, 0.0f, 0.0f, 0.0f, 0.0f));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() { // from class: org.telegram.ui.CalendarActivity.5
            @Override // org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick
            public void onItemClick(int i) {
                if (i == -1) {
                    if (CalendarActivity.this.dateSelectedStart != 0 || CalendarActivity.this.dateSelectedEnd != 0 || CalendarActivity.this.inSelectionMode) {
                        CalendarActivity.this.inSelectionMode = false;
                        CalendarActivity.this.dateSelectedStart = 0;
                        CalendarActivity.this.dateSelectedEnd = 0;
                        CalendarActivity.this.updateTitle();
                        CalendarActivity.this.animateSelection();
                        return;
                    }
                    CalendarActivity.this.finishFragment();
                }
            }
        });
        this.fragmentView = this.contentView;
        Calendar calendar = Calendar.getInstance();
        this.startFromYear = calendar.get(1);
        int i = calendar.get(2);
        this.startFromMonth = i;
        int i2 = this.selectedYear;
        if (i2 != 0) {
            int i3 = ((((this.startFromYear - i2) * 12) + i) - this.selectedMonth) + 1;
            this.monthCount = i3;
            this.layoutManager.scrollToPositionWithOffset(i3 - 1, AndroidUtilities.dp(120.0f));
        }
        if (this.monthCount < 3) {
            this.monthCount = 3;
        }
        BackDrawable backDrawable = new BackDrawable(false);
        this.backDrawable = backDrawable;
        this.actionBar.setBackButtonDrawable(backDrawable);
        this.backDrawable.setRotation(0.0f, false);
        loadNext();
        updateColors();
        this.activeTextPaint.setColor(-1);
        if (z) {
            FrameLayout frameLayout = new FrameLayout(this, context) { // from class: org.telegram.ui.CalendarActivity.6
                @Override // android.view.View
                public void onDraw(Canvas canvas) {
                    canvas.drawRect(0.0f, 0.0f, getMeasuredWidth(), AndroidUtilities.getShadowHeight(), Theme.dividerPaint);
                }
            };
            this.bottomBar = frameLayout;
            frameLayout.setWillNotDraw(false);
            this.bottomBar.setPadding(0, AndroidUtilities.getShadowHeight(), 0, 0);
            this.bottomBar.setClipChildren(false);
            TextView textView = new TextView(context);
            this.selectDaysButton = textView;
            textView.setGravity(17);
            this.selectDaysButton.setTextSize(1, 15.0f);
            this.selectDaysButton.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.selectDaysButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.CalendarActivity$$ExternalSyntheticLambda2
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    CalendarActivity.this.lambda$createView$0(view);
                }
            });
            this.selectDaysButton.setText(LocaleController.getString("SelectDays", R.string.SelectDays));
            this.selectDaysButton.setAllCaps(true);
            this.bottomBar.addView(this.selectDaysButton, LayoutHelper.createFrame(-1, -1.0f, 0, 0.0f, 0.0f, 0.0f, 0.0f));
            TextView textView2 = new TextView(context);
            this.removeDaysButton = textView2;
            textView2.setGravity(17);
            this.removeDaysButton.setTextSize(1, 15.0f);
            this.removeDaysButton.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.removeDaysButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.CalendarActivity$$ExternalSyntheticLambda1
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    CalendarActivity.this.lambda$createView$1(view);
                }
            });
            this.removeDaysButton.setAllCaps(true);
            this.removeDaysButton.setVisibility(8);
            this.bottomBar.addView(this.removeDaysButton, LayoutHelper.createFrame(-1, -1.0f, 0, 0.0f, 0.0f, 0.0f, 0.0f));
            this.contentView.addView(this.bottomBar, LayoutHelper.createFrame(-1, 48.0f, 80, 0.0f, 0.0f, 0.0f, 0.0f));
            this.selectDaysButton.setBackground(Theme.createSelectorDrawable(ColorUtils.setAlphaComponent(Theme.getColor("chat_fieldOverlayText"), 51), 2));
            this.removeDaysButton.setBackground(Theme.createSelectorDrawable(ColorUtils.setAlphaComponent(Theme.getColor("dialogTextRed"), 51), 2));
            this.selectDaysButton.setTextColor(Theme.getColor("chat_fieldOverlayText"));
            this.removeDaysButton.setTextColor(Theme.getColor("dialogTextRed"));
        }
        return this.fragmentView;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$0(View view) {
        this.inSelectionMode = true;
        updateTitle();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$1(View view) {
        int i = this.lastDaysSelected;
        if (i == 0) {
            if (this.selectDaysHint == null) {
                HintView hintView = new HintView(this.contentView.getContext(), 8);
                this.selectDaysHint = hintView;
                hintView.setExtraTranslationY(AndroidUtilities.dp(24.0f));
                this.contentView.addView(this.selectDaysHint, LayoutHelper.createFrame(-2, -2.0f, 51, 19.0f, 0.0f, 19.0f, 0.0f));
                this.selectDaysHint.setText(LocaleController.getString("SelectDaysTooltip", R.string.SelectDaysTooltip));
            }
            this.selectDaysHint.showForView(this.bottomBar, true);
            return;
        }
        AlertsCreator.createClearDaysDialogAlert(this, i, getMessagesController().getUser(Long.valueOf(this.dialogId)), null, false, new MessagesStorage.BooleanCallback() { // from class: org.telegram.ui.CalendarActivity.7
            @Override // org.telegram.messenger.MessagesStorage.BooleanCallback
            public void run(boolean z) {
                CalendarActivity.this.finishFragment();
                if (((BaseFragment) CalendarActivity.this).parentLayout.getFragmentStack().size() >= 2) {
                    BaseFragment baseFragment = ((BaseFragment) CalendarActivity.this).parentLayout.getFragmentStack().get(((BaseFragment) CalendarActivity.this).parentLayout.getFragmentStack().size() - 2);
                    if (!(baseFragment instanceof ChatActivity)) {
                        return;
                    }
                    ((ChatActivity) baseFragment).deleteHistory(CalendarActivity.this.dateSelectedStart, CalendarActivity.this.dateSelectedEnd + 86400, z);
                }
            }
        }, null);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateColors() {
        this.actionBar.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        this.activeTextPaint.setColor(-1);
        this.textPaint.setColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.textPaint2.setColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.actionBar.setTitleColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.backDrawable.setColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.actionBar.setItemsColor(Theme.getColor("windowBackgroundWhiteBlackText"), false);
        this.actionBar.setItemsBackgroundColor(Theme.getColor("listSelectorSDK21"), false);
    }

    private void loadNext() {
        if (this.loading || this.endReached) {
            return;
        }
        this.loading = true;
        TLRPC$TL_messages_getSearchResultsCalendar tLRPC$TL_messages_getSearchResultsCalendar = new TLRPC$TL_messages_getSearchResultsCalendar();
        int i = this.photosVideosTypeFilter;
        if (i == 1) {
            tLRPC$TL_messages_getSearchResultsCalendar.filter = new TLRPC$TL_inputMessagesFilterPhotos();
        } else if (i == 2) {
            tLRPC$TL_messages_getSearchResultsCalendar.filter = new TLRPC$TL_inputMessagesFilterVideo();
        } else {
            tLRPC$TL_messages_getSearchResultsCalendar.filter = new TLRPC$TL_inputMessagesFilterPhotoVideo();
        }
        tLRPC$TL_messages_getSearchResultsCalendar.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(this.dialogId);
        tLRPC$TL_messages_getSearchResultsCalendar.offset_id = this.lastId;
        final Calendar calendar = Calendar.getInstance();
        this.listView.setItemAnimator(null);
        getConnectionsManager().sendRequest(tLRPC$TL_messages_getSearchResultsCalendar, new RequestDelegate() { // from class: org.telegram.ui.CalendarActivity$$ExternalSyntheticLambda4
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                CalendarActivity.this.lambda$loadNext$3(calendar, tLObject, tLRPC$TL_error);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$loadNext$3(final Calendar calendar, final TLObject tLObject, final TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.CalendarActivity$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                CalendarActivity.this.lambda$loadNext$2(tLRPC$TL_error, tLObject, calendar);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$loadNext$2(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, Calendar calendar) {
        int i;
        int i2;
        if (tLRPC$TL_error == null) {
            TLRPC$TL_messages_searchResultsCalendar tLRPC$TL_messages_searchResultsCalendar = (TLRPC$TL_messages_searchResultsCalendar) tLObject;
            int i3 = 0;
            while (true) {
                i = 5;
                i2 = 2;
                if (i3 >= tLRPC$TL_messages_searchResultsCalendar.periods.size()) {
                    break;
                }
                calendar.setTimeInMillis(tLRPC$TL_messages_searchResultsCalendar.periods.get(i3).date * 1000);
                int i4 = (calendar.get(1) * 100) + calendar.get(2);
                SparseArray<PeriodDay> sparseArray = this.messagesByYearMounth.get(i4);
                if (sparseArray == null) {
                    sparseArray = new SparseArray<>();
                    this.messagesByYearMounth.put(i4, sparseArray);
                }
                PeriodDay periodDay = new PeriodDay();
                periodDay.messageObject = new MessageObject(this.currentAccount, tLRPC$TL_messages_searchResultsCalendar.messages.get(i3), false, false);
                periodDay.date = (int) (calendar.getTimeInMillis() / 1000);
                int i5 = this.startOffset + tLRPC$TL_messages_searchResultsCalendar.periods.get(i3).count;
                this.startOffset = i5;
                periodDay.startOffset = i5;
                int i6 = calendar.get(5) - 1;
                if (sparseArray.get(i6, null) == null || !sparseArray.get(i6, null).hasImage) {
                    sparseArray.put(i6, periodDay);
                }
                int i7 = this.minMontYear;
                if (i4 < i7 || i7 == 0) {
                    this.minMontYear = i4;
                }
                i3++;
            }
            int currentTimeMillis = (int) (System.currentTimeMillis() / 1000);
            int i8 = tLRPC$TL_messages_searchResultsCalendar.min_date;
            this.minDate = i8;
            while (i8 < currentTimeMillis) {
                calendar.setTimeInMillis(i8 * 1000);
                calendar.set(11, 0);
                calendar.set(12, 0);
                calendar.set(13, 0);
                calendar.set(14, 0);
                int i9 = (calendar.get(1) * 100) + calendar.get(i2);
                SparseArray<PeriodDay> sparseArray2 = this.messagesByYearMounth.get(i9);
                if (sparseArray2 == null) {
                    sparseArray2 = new SparseArray<>();
                    this.messagesByYearMounth.put(i9, sparseArray2);
                }
                int i10 = calendar.get(i) - 1;
                if (sparseArray2.get(i10, null) == null) {
                    PeriodDay periodDay2 = new PeriodDay();
                    periodDay2.hasImage = false;
                    periodDay2.date = (int) (calendar.getTimeInMillis() / 1000);
                    sparseArray2.put(i10, periodDay2);
                }
                i8 += 86400;
                i = 5;
                i2 = 2;
            }
            this.loading = false;
            if (!tLRPC$TL_messages_searchResultsCalendar.messages.isEmpty()) {
                ArrayList<TLRPC$Message> arrayList = tLRPC$TL_messages_searchResultsCalendar.messages;
                this.lastId = arrayList.get(arrayList.size() - 1).id;
                this.endReached = false;
                checkLoadNext();
            } else {
                this.endReached = true;
            }
            if (this.isOpened) {
                this.checkEnterItems = true;
            }
            this.listView.invalidate();
            int timeInMillis = ((int) (((calendar.getTimeInMillis() / 1000) - tLRPC$TL_messages_searchResultsCalendar.min_date) / 2629800)) + 1;
            this.adapter.notifyItemRangeChanged(0, this.monthCount);
            int i11 = this.monthCount;
            if (timeInMillis > i11) {
                this.adapter.notifyItemRangeInserted(i11 + 1, timeInMillis);
                this.monthCount = timeInMillis;
            }
            if (!this.endReached) {
                return;
            }
            resumeDelayedFragmentAnimation();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void checkLoadNext() {
        if (this.loading || this.endReached) {
            return;
        }
        int i = Integer.MAX_VALUE;
        for (int i2 = 0; i2 < this.listView.getChildCount(); i2++) {
            View childAt = this.listView.getChildAt(i2);
            if (childAt instanceof MonthView) {
                MonthView monthView = (MonthView) childAt;
                int i3 = (monthView.currentYear * 100) + monthView.currentMonthInYear;
                if (i3 < i) {
                    i = i3;
                }
            }
        }
        int i4 = this.minMontYear;
        if (((i4 / 100) * 12) + (i4 % 100) + 3 < ((i / 100) * 12) + (i % 100)) {
            return;
        }
        loadNext();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public class CalendarAdapter extends RecyclerView.Adapter {
        private CalendarAdapter() {
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        /* renamed from: onCreateViewHolder */
        public RecyclerView.ViewHolder mo1805onCreateViewHolder(ViewGroup viewGroup, int i) {
            return new RecyclerListView.Holder(new MonthView(viewGroup.getContext()));
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            MonthView monthView = (MonthView) viewHolder.itemView;
            CalendarActivity calendarActivity = CalendarActivity.this;
            int i2 = calendarActivity.startFromYear - (i / 12);
            int i3 = calendarActivity.startFromMonth - (i % 12);
            if (i3 < 0) {
                i3 += 12;
                i2--;
            }
            monthView.setDate(i2, i3, calendarActivity.messagesByYearMounth.get((i2 * 100) + i3), monthView.currentYear == i2 && monthView.currentMonthInYear == i3);
            monthView.startSelectionAnimation(CalendarActivity.this.dateSelectedStart, CalendarActivity.this.dateSelectedEnd);
            monthView.setSelectionValue(1.0f);
            CalendarActivity.this.updateRowSelections(monthView, false);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public long getItemId(int i) {
            CalendarActivity calendarActivity = CalendarActivity.this;
            return ((calendarActivity.startFromYear - (i / 12)) * 100) + (calendarActivity.startFromMonth - (i % 12));
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            return CalendarActivity.this.monthCount;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public class MonthView extends FrameLayout {
        int cellCount;
        int currentMonthInYear;
        int currentYear;
        int daysInMonth;
        GestureDetectorCompat gestureDetector;
        SparseArray<ImageReceiver> imagesByDays;
        SparseArray<PeriodDay> messagesByDays;
        private SparseArray<ValueAnimator> rowAnimators;
        private SparseArray<RowAnimationValue> rowSelectionPos;
        int startDayOfWeek;
        int startMonthTime;
        SimpleTextView titleView;

        public MonthView(Context context) {
            super(context);
            this.messagesByDays = new SparseArray<>();
            this.imagesByDays = new SparseArray<>();
            this.rowAnimators = new SparseArray<>();
            this.rowSelectionPos = new SparseArray<>();
            boolean z = false;
            setWillNotDraw(false);
            this.titleView = new SimpleTextView(context);
            if (CalendarActivity.this.calendarType == 0 && CalendarActivity.this.canClearHistory) {
                this.titleView.setOnLongClickListener(new View.OnLongClickListener() { // from class: org.telegram.ui.CalendarActivity$MonthView$$ExternalSyntheticLambda1
                    @Override // android.view.View.OnLongClickListener
                    public final boolean onLongClick(View view) {
                        boolean lambda$new$0;
                        lambda$new$0 = CalendarActivity.MonthView.this.lambda$new$0(view);
                        return lambda$new$0;
                    }
                });
                this.titleView.setOnClickListener(new View.OnClickListener(CalendarActivity.this) { // from class: org.telegram.ui.CalendarActivity.MonthView.1
                    @Override // android.view.View.OnClickListener
                    public void onClick(View view) {
                        MonthView monthView;
                        MonthView monthView2 = MonthView.this;
                        if (monthView2.messagesByDays != null && CalendarActivity.this.inSelectionMode) {
                            int i = 0;
                            int i2 = -1;
                            int i3 = -1;
                            while (true) {
                                monthView = MonthView.this;
                                if (i >= monthView.daysInMonth) {
                                    break;
                                }
                                PeriodDay periodDay = monthView.messagesByDays.get(i, null);
                                if (periodDay != null) {
                                    if (i2 == -1) {
                                        i2 = periodDay.date;
                                    }
                                    i3 = periodDay.date;
                                }
                                i++;
                            }
                            if (i2 < 0 || i3 < 0) {
                                return;
                            }
                            CalendarActivity.this.dateSelectedStart = i2;
                            CalendarActivity.this.dateSelectedEnd = i3;
                            CalendarActivity.this.updateTitle();
                            CalendarActivity.this.animateSelection();
                        }
                    }
                });
            }
            this.titleView.setBackground(Theme.createSelectorDrawable(Theme.getColor("listSelectorSDK21"), 2));
            this.titleView.setTextSize(15);
            this.titleView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.titleView.setGravity(17);
            this.titleView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            addView(this.titleView, LayoutHelper.createFrame(-1, 28.0f, 0, 0.0f, 12.0f, 0.0f, 4.0f));
            GestureDetectorCompat gestureDetectorCompat = new GestureDetectorCompat(context, new AnonymousClass2(CalendarActivity.this, context));
            this.gestureDetector = gestureDetectorCompat;
            gestureDetectorCompat.setIsLongpressEnabled(CalendarActivity.this.calendarType == 0 ? true : z);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ boolean lambda$new$0(View view) {
            if (this.messagesByDays == null) {
                return false;
            }
            int i = -1;
            int i2 = -1;
            for (int i3 = 0; i3 < this.daysInMonth; i3++) {
                PeriodDay periodDay = this.messagesByDays.get(i3, null);
                if (periodDay != null) {
                    if (i == -1) {
                        i = periodDay.date;
                    }
                    i2 = periodDay.date;
                }
            }
            if (i >= 0 && i2 >= 0) {
                CalendarActivity.this.inSelectionMode = true;
                CalendarActivity.this.dateSelectedStart = i;
                CalendarActivity.this.dateSelectedEnd = i2;
                CalendarActivity.this.updateTitle();
                CalendarActivity.this.animateSelection();
            }
            return false;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* renamed from: org.telegram.ui.CalendarActivity$MonthView$2  reason: invalid class name */
        /* loaded from: classes3.dex */
        public class AnonymousClass2 extends GestureDetector.SimpleOnGestureListener {
            final /* synthetic */ Context val$context;

            @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
            public boolean onDown(MotionEvent motionEvent) {
                return true;
            }

            AnonymousClass2(CalendarActivity calendarActivity, Context context) {
                this.val$context = context;
            }

            @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
            @SuppressLint({"NotifyDataSetChanged"})
            public boolean onSingleTapUp(MotionEvent motionEvent) {
                PeriodDay dayAtCoord;
                MessageObject messageObject;
                Callback callback;
                if (((BaseFragment) CalendarActivity.this).parentLayout == null) {
                    return false;
                }
                if (CalendarActivity.this.calendarType == 1 && MonthView.this.messagesByDays != null && (dayAtCoord = getDayAtCoord(motionEvent.getX(), motionEvent.getY())) != null && (messageObject = dayAtCoord.messageObject) != null && (callback = CalendarActivity.this.callback) != null) {
                    callback.onDateSelected(messageObject.getId(), dayAtCoord.startOffset);
                    CalendarActivity.this.finishFragment();
                }
                MonthView monthView = MonthView.this;
                if (monthView.messagesByDays != null) {
                    if (CalendarActivity.this.inSelectionMode) {
                        PeriodDay dayAtCoord2 = getDayAtCoord(motionEvent.getX(), motionEvent.getY());
                        if (dayAtCoord2 != null) {
                            if (CalendarActivity.this.selectionAnimator != null) {
                                CalendarActivity.this.selectionAnimator.cancel();
                                CalendarActivity.this.selectionAnimator = null;
                            }
                            if (CalendarActivity.this.dateSelectedStart != 0 || CalendarActivity.this.dateSelectedEnd != 0) {
                                if (CalendarActivity.this.dateSelectedStart != dayAtCoord2.date || CalendarActivity.this.dateSelectedEnd != dayAtCoord2.date) {
                                    if (CalendarActivity.this.dateSelectedStart != dayAtCoord2.date) {
                                        if (CalendarActivity.this.dateSelectedEnd != dayAtCoord2.date) {
                                            if (CalendarActivity.this.dateSelectedStart == CalendarActivity.this.dateSelectedEnd) {
                                                if (dayAtCoord2.date > CalendarActivity.this.dateSelectedEnd) {
                                                    CalendarActivity.this.dateSelectedEnd = dayAtCoord2.date;
                                                } else {
                                                    CalendarActivity.this.dateSelectedStart = dayAtCoord2.date;
                                                }
                                            } else {
                                                CalendarActivity calendarActivity = CalendarActivity.this;
                                                calendarActivity.dateSelectedStart = calendarActivity.dateSelectedEnd = dayAtCoord2.date;
                                            }
                                        } else {
                                            CalendarActivity calendarActivity2 = CalendarActivity.this;
                                            calendarActivity2.dateSelectedEnd = calendarActivity2.dateSelectedStart;
                                        }
                                    } else {
                                        CalendarActivity calendarActivity3 = CalendarActivity.this;
                                        calendarActivity3.dateSelectedStart = calendarActivity3.dateSelectedEnd;
                                    }
                                } else {
                                    CalendarActivity calendarActivity4 = CalendarActivity.this;
                                    calendarActivity4.dateSelectedStart = calendarActivity4.dateSelectedEnd = 0;
                                }
                            } else {
                                CalendarActivity calendarActivity5 = CalendarActivity.this;
                                calendarActivity5.dateSelectedStart = calendarActivity5.dateSelectedEnd = dayAtCoord2.date;
                            }
                            CalendarActivity.this.updateTitle();
                            CalendarActivity.this.animateSelection();
                        }
                    } else {
                        PeriodDay dayAtCoord3 = getDayAtCoord(motionEvent.getX(), motionEvent.getY());
                        if (dayAtCoord3 != null && ((BaseFragment) CalendarActivity.this).parentLayout != null && ((BaseFragment) CalendarActivity.this).parentLayout.getFragmentStack().size() >= 2) {
                            BaseFragment baseFragment = ((BaseFragment) CalendarActivity.this).parentLayout.getFragmentStack().get(((BaseFragment) CalendarActivity.this).parentLayout.getFragmentStack().size() - 2);
                            if (baseFragment instanceof ChatActivity) {
                                CalendarActivity.this.finishFragment();
                                ((ChatActivity) baseFragment).jumpToDate(dayAtCoord3.date);
                            }
                        }
                    }
                }
                return false;
            }

            private PeriodDay getDayAtCoord(float f, float f2) {
                PeriodDay periodDay;
                MonthView monthView = MonthView.this;
                if (monthView.messagesByDays == null) {
                    return null;
                }
                int i = monthView.startDayOfWeek;
                float measuredWidth = monthView.getMeasuredWidth() / 7.0f;
                float dp = AndroidUtilities.dp(52.0f);
                int dp2 = AndroidUtilities.dp(44.0f) / 2;
                int i2 = 0;
                for (int i3 = 0; i3 < MonthView.this.daysInMonth; i3++) {
                    float f3 = (i * measuredWidth) + (measuredWidth / 2.0f);
                    float dp3 = (i2 * dp) + (dp / 2.0f) + AndroidUtilities.dp(44.0f);
                    float f4 = dp2;
                    if (f >= f3 - f4 && f <= f3 + f4 && f2 >= dp3 - f4 && f2 <= dp3 + f4 && (periodDay = MonthView.this.messagesByDays.get(i3, null)) != null) {
                        return periodDay;
                    }
                    i++;
                    if (i >= 7) {
                        i2++;
                        i = 0;
                    }
                }
                return null;
            }

            @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
            public void onLongPress(MotionEvent motionEvent) {
                final PeriodDay dayAtCoord;
                super.onLongPress(motionEvent);
                if (CalendarActivity.this.calendarType == 0 && (dayAtCoord = getDayAtCoord(motionEvent.getX(), motionEvent.getY())) != null) {
                    MonthView.this.performHapticFeedback(0);
                    Bundle bundle = new Bundle();
                    if (CalendarActivity.this.dialogId > 0) {
                        bundle.putLong("user_id", CalendarActivity.this.dialogId);
                    } else {
                        bundle.putLong("chat_id", -CalendarActivity.this.dialogId);
                    }
                    bundle.putInt("start_from_date", dayAtCoord.date);
                    bundle.putBoolean("need_remove_previous_same_chat_activity", false);
                    ChatActivity chatActivity = new ChatActivity(bundle);
                    ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = new ActionBarPopupWindow.ActionBarPopupWindowLayout(CalendarActivity.this.getParentActivity(), R.drawable.popup_fixed_alert, CalendarActivity.this.getResourceProvider());
                    actionBarPopupWindowLayout.setBackgroundColor(CalendarActivity.this.getThemedColor("actionBarDefaultSubmenuBackground"));
                    ActionBarMenuSubItem actionBarMenuSubItem = new ActionBarMenuSubItem(CalendarActivity.this.getParentActivity(), true, false);
                    actionBarMenuSubItem.setTextAndIcon(LocaleController.getString("JumpToDate", R.string.JumpToDate), R.drawable.msg_message);
                    actionBarMenuSubItem.setMinimumWidth(160);
                    actionBarMenuSubItem.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.CalendarActivity$MonthView$2$$ExternalSyntheticLambda2
                        @Override // android.view.View.OnClickListener
                        public final void onClick(View view) {
                            CalendarActivity.MonthView.AnonymousClass2.this.lambda$onLongPress$1(dayAtCoord, view);
                        }
                    });
                    actionBarPopupWindowLayout.addView(actionBarMenuSubItem);
                    if (CalendarActivity.this.canClearHistory) {
                        ActionBarMenuSubItem actionBarMenuSubItem2 = new ActionBarMenuSubItem(CalendarActivity.this.getParentActivity(), false, false);
                        actionBarMenuSubItem2.setTextAndIcon(LocaleController.getString("SelectThisDay", R.string.SelectThisDay), R.drawable.msg_select);
                        actionBarMenuSubItem2.setMinimumWidth(160);
                        actionBarMenuSubItem2.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.CalendarActivity$MonthView$2$$ExternalSyntheticLambda3
                            @Override // android.view.View.OnClickListener
                            public final void onClick(View view) {
                                CalendarActivity.MonthView.AnonymousClass2.this.lambda$onLongPress$2(dayAtCoord, view);
                            }
                        });
                        actionBarPopupWindowLayout.addView(actionBarMenuSubItem2);
                        ActionBarMenuSubItem actionBarMenuSubItem3 = new ActionBarMenuSubItem(CalendarActivity.this.getParentActivity(), false, true);
                        actionBarMenuSubItem3.setTextAndIcon(LocaleController.getString("ClearHistory", R.string.ClearHistory), R.drawable.msg_delete);
                        actionBarMenuSubItem3.setMinimumWidth(160);
                        actionBarMenuSubItem3.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.CalendarActivity$MonthView$2$$ExternalSyntheticLambda0
                            @Override // android.view.View.OnClickListener
                            public final void onClick(View view) {
                                CalendarActivity.MonthView.AnonymousClass2.this.lambda$onLongPress$3(view);
                            }
                        });
                        actionBarPopupWindowLayout.addView(actionBarMenuSubItem3);
                    }
                    actionBarPopupWindowLayout.setFitItems(true);
                    CalendarActivity.this.blurredView = new View(this.val$context) { // from class: org.telegram.ui.CalendarActivity.MonthView.2.2
                        @Override // android.view.View
                        public void setAlpha(float f) {
                            super.setAlpha(f);
                            if (((BaseFragment) CalendarActivity.this).fragmentView != null) {
                                ((BaseFragment) CalendarActivity.this).fragmentView.invalidate();
                            }
                        }
                    };
                    CalendarActivity.this.blurredView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.CalendarActivity$MonthView$2$$ExternalSyntheticLambda1
                        @Override // android.view.View.OnClickListener
                        public final void onClick(View view) {
                            CalendarActivity.MonthView.AnonymousClass2.this.lambda$onLongPress$4(view);
                        }
                    });
                    CalendarActivity.this.blurredView.setVisibility(8);
                    CalendarActivity.this.blurredView.setFitsSystemWindows(true);
                    ((BaseFragment) CalendarActivity.this).parentLayout.getOverlayContainerView().addView(CalendarActivity.this.blurredView, LayoutHelper.createFrame(-1, -1.0f));
                    CalendarActivity.this.prepareBlurBitmap();
                    CalendarActivity.this.presentFragmentAsPreviewWithMenu(chatActivity, actionBarPopupWindowLayout);
                }
            }

            /* JADX INFO: Access modifiers changed from: private */
            public /* synthetic */ void lambda$onLongPress$1(final PeriodDay periodDay, View view) {
                if (((BaseFragment) CalendarActivity.this).parentLayout.getFragmentStack().size() >= 3) {
                    final BaseFragment baseFragment = ((BaseFragment) CalendarActivity.this).parentLayout.getFragmentStack().get(((BaseFragment) CalendarActivity.this).parentLayout.getFragmentStack().size() - 3);
                    if (baseFragment instanceof ChatActivity) {
                        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.CalendarActivity$MonthView$2$$ExternalSyntheticLambda4
                            @Override // java.lang.Runnable
                            public final void run() {
                                CalendarActivity.MonthView.AnonymousClass2.this.lambda$onLongPress$0(baseFragment, periodDay);
                            }
                        }, 300L);
                    }
                }
                CalendarActivity.this.finishPreviewFragment();
            }

            /* JADX INFO: Access modifiers changed from: private */
            public /* synthetic */ void lambda$onLongPress$0(BaseFragment baseFragment, PeriodDay periodDay) {
                CalendarActivity.this.finishFragment();
                ((ChatActivity) baseFragment).jumpToDate(periodDay.date);
            }

            /* JADX INFO: Access modifiers changed from: private */
            public /* synthetic */ void lambda$onLongPress$2(PeriodDay periodDay, View view) {
                CalendarActivity calendarActivity = CalendarActivity.this;
                calendarActivity.dateSelectedStart = calendarActivity.dateSelectedEnd = periodDay.date;
                CalendarActivity.this.inSelectionMode = true;
                CalendarActivity.this.updateTitle();
                CalendarActivity.this.animateSelection();
                CalendarActivity.this.finishPreviewFragment();
            }

            /* JADX INFO: Access modifiers changed from: private */
            public /* synthetic */ void lambda$onLongPress$3(View view) {
                if (((BaseFragment) CalendarActivity.this).parentLayout.getFragmentStack().size() >= 3) {
                    final BaseFragment baseFragment = ((BaseFragment) CalendarActivity.this).parentLayout.getFragmentStack().get(((BaseFragment) CalendarActivity.this).parentLayout.getFragmentStack().size() - 3);
                    if (baseFragment instanceof ChatActivity) {
                        CalendarActivity calendarActivity = CalendarActivity.this;
                        AlertsCreator.createClearDaysDialogAlert(calendarActivity, 1, calendarActivity.getMessagesController().getUser(Long.valueOf(CalendarActivity.this.dialogId)), null, false, new MessagesStorage.BooleanCallback() { // from class: org.telegram.ui.CalendarActivity.MonthView.2.1
                            @Override // org.telegram.messenger.MessagesStorage.BooleanCallback
                            public void run(boolean z) {
                                CalendarActivity.this.finishFragment();
                                ((ChatActivity) baseFragment).deleteHistory(CalendarActivity.this.dateSelectedStart, CalendarActivity.this.dateSelectedEnd + 86400, z);
                            }
                        }, null);
                    }
                }
                CalendarActivity.this.finishPreviewFragment();
            }

            /* JADX INFO: Access modifiers changed from: private */
            public /* synthetic */ void lambda$onLongPress$4(View view) {
                CalendarActivity.this.finishPreviewFragment();
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void startSelectionAnimation(int i, int i2) {
            if (this.messagesByDays != null) {
                for (int i3 = 0; i3 < this.daysInMonth; i3++) {
                    PeriodDay periodDay = this.messagesByDays.get(i3, null);
                    if (periodDay != null) {
                        periodDay.fromSelProgress = periodDay.selectProgress;
                        int i4 = periodDay.date;
                        periodDay.toSelProgress = (i4 < i || i4 > i2) ? 0.0f : 1.0f;
                        periodDay.fromSelSEProgress = periodDay.selectStartEndProgress;
                        if (i4 == i || i4 == i2) {
                            periodDay.toSelSEProgress = 1.0f;
                        } else {
                            periodDay.toSelSEProgress = 0.0f;
                        }
                    }
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setSelectionValue(float f) {
            if (this.messagesByDays != null) {
                for (int i = 0; i < this.daysInMonth; i++) {
                    PeriodDay periodDay = this.messagesByDays.get(i, null);
                    if (periodDay != null) {
                        float f2 = periodDay.fromSelProgress;
                        periodDay.selectProgress = f2 + ((periodDay.toSelProgress - f2) * f);
                        float f3 = periodDay.fromSelSEProgress;
                        periodDay.selectStartEndProgress = f3 + ((periodDay.toSelSEProgress - f3) * f);
                    }
                }
            }
            invalidate();
        }

        public void dismissRowAnimations(boolean z) {
            for (int i = 0; i < this.rowSelectionPos.size(); i++) {
                animateRow(this.rowSelectionPos.keyAt(i), 0, 0, false, z);
            }
        }

        public void animateRow(final int i, int i2, int i3, final boolean z, boolean z2) {
            float f;
            float f2;
            final float f3;
            ValueAnimator valueAnimator = this.rowAnimators.get(i);
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            float measuredWidth = getMeasuredWidth() / 7.0f;
            RowAnimationValue rowAnimationValue = this.rowSelectionPos.get(i);
            if (rowAnimationValue != null) {
                f = rowAnimationValue.startX;
                f2 = rowAnimationValue.endX;
                f3 = rowAnimationValue.alpha;
            } else {
                f = (i2 * measuredWidth) + (measuredWidth / 2.0f);
                f2 = f;
                f3 = 0.0f;
            }
            float f4 = z ? (i2 * measuredWidth) + (measuredWidth / 2.0f) : f;
            float f5 = z ? (i3 * measuredWidth) + (measuredWidth / 2.0f) : f2;
            float f6 = z ? 1.0f : 0.0f;
            final RowAnimationValue rowAnimationValue2 = new RowAnimationValue(f, f2);
            this.rowSelectionPos.put(i, rowAnimationValue2);
            if (z2) {
                ValueAnimator duration = ValueAnimator.ofFloat(0.0f, 1.0f).setDuration(300L);
                duration.setInterpolator(Easings.easeInOutQuad);
                final float f7 = f;
                final float f8 = f4;
                final float f9 = f2;
                final float var_ = f5;
                final float var_ = f6;
                duration.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.CalendarActivity$MonthView$$ExternalSyntheticLambda0
                    @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                    public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                        CalendarActivity.MonthView.this.lambda$animateRow$1(rowAnimationValue2, f7, f8, f9, var_, f3, var_, valueAnimator2);
                    }
                });
                final float var_ = f4;
                final float var_ = f5;
                final float var_ = f6;
                duration.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.CalendarActivity.MonthView.3
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationCancel(Animator animator) {
                        RowAnimationValue rowAnimationValue3 = rowAnimationValue2;
                        rowAnimationValue3.startX = var_;
                        rowAnimationValue3.endX = var_;
                        rowAnimationValue3.alpha = var_;
                        MonthView.this.invalidate();
                    }

                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animator) {
                        MonthView.this.rowAnimators.remove(i);
                        if (!z) {
                            MonthView.this.rowSelectionPos.remove(i);
                        }
                    }
                });
                duration.start();
                this.rowAnimators.put(i, duration);
                return;
            }
            rowAnimationValue2.startX = f4;
            rowAnimationValue2.endX = f5;
            rowAnimationValue2.alpha = f6;
            invalidate();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$animateRow$1(RowAnimationValue rowAnimationValue, float f, float f2, float f3, float f4, float f5, float f6, ValueAnimator valueAnimator) {
            float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            rowAnimationValue.startX = f + ((f2 - f) * floatValue);
            rowAnimationValue.endX = f3 + ((f4 - f3) * floatValue);
            rowAnimationValue.alpha = f5 + ((f6 - f5) * floatValue);
            invalidate();
        }

        @Override // android.view.View
        @SuppressLint({"ClickableViewAccessibility"})
        public boolean onTouchEvent(MotionEvent motionEvent) {
            return this.gestureDetector.onTouchEvent(motionEvent);
        }

        public void setDate(int i, int i2, SparseArray<PeriodDay> sparseArray, boolean z) {
            boolean z2 = false;
            boolean z3 = (i == this.currentYear && i2 == this.currentMonthInYear) ? false : true;
            this.currentYear = i;
            this.currentMonthInYear = i2;
            this.messagesByDays = sparseArray;
            if (z3 && this.imagesByDays != null) {
                for (int i3 = 0; i3 < this.imagesByDays.size(); i3++) {
                    this.imagesByDays.valueAt(i3).onDetachedFromWindow();
                    this.imagesByDays.valueAt(i3).setParentView(null);
                }
                this.imagesByDays = null;
            }
            if (sparseArray != null) {
                if (this.imagesByDays == null) {
                    this.imagesByDays = new SparseArray<>();
                }
                int i4 = 0;
                while (i4 < sparseArray.size()) {
                    int keyAt = sparseArray.keyAt(i4);
                    if (this.imagesByDays.get(keyAt, null) == null && sparseArray.get(keyAt).hasImage) {
                        ImageReceiver imageReceiver = new ImageReceiver();
                        imageReceiver.setParentView(this);
                        MessageObject messageObject = sparseArray.get(keyAt).messageObject;
                        if (messageObject != null) {
                            if (messageObject.isVideo()) {
                                TLRPC$Document document = messageObject.getDocument();
                                TLRPC$PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(document.thumbs, 50);
                                TLRPC$PhotoSize closestPhotoSizeWithSize2 = FileLoader.getClosestPhotoSizeWithSize(document.thumbs, 320);
                                if (closestPhotoSizeWithSize == closestPhotoSizeWithSize2) {
                                    closestPhotoSizeWithSize2 = null;
                                }
                                if (closestPhotoSizeWithSize != null) {
                                    if (messageObject.strippedThumb != null) {
                                        imageReceiver.setImage(ImageLocation.getForDocument(closestPhotoSizeWithSize2, document), "44_44", messageObject.strippedThumb, null, messageObject, 0);
                                    } else {
                                        imageReceiver.setImage(ImageLocation.getForDocument(closestPhotoSizeWithSize2, document), "44_44", ImageLocation.getForDocument(closestPhotoSizeWithSize, document), "b", (String) null, messageObject, 0);
                                    }
                                }
                            } else {
                                TLRPC$MessageMedia tLRPC$MessageMedia = messageObject.messageOwner.media;
                                if ((tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaPhoto) && tLRPC$MessageMedia.photo != null && !messageObject.photoThumbs.isEmpty()) {
                                    TLRPC$PhotoSize closestPhotoSizeWithSize3 = FileLoader.getClosestPhotoSizeWithSize(messageObject.photoThumbs, 50);
                                    TLRPC$PhotoSize closestPhotoSizeWithSize4 = FileLoader.getClosestPhotoSizeWithSize(messageObject.photoThumbs, 320, z2, closestPhotoSizeWithSize3, z2);
                                    if (messageObject.mediaExists || DownloadController.getInstance(((BaseFragment) CalendarActivity.this).currentAccount).canDownloadMedia(messageObject)) {
                                        if (closestPhotoSizeWithSize4 == closestPhotoSizeWithSize3) {
                                            closestPhotoSizeWithSize3 = null;
                                        }
                                        long j = 0;
                                        if (messageObject.strippedThumb != null) {
                                            ImageLocation forObject = ImageLocation.getForObject(closestPhotoSizeWithSize4, messageObject.photoThumbsObject);
                                            BitmapDrawable bitmapDrawable = messageObject.strippedThumb;
                                            if (closestPhotoSizeWithSize4 != null) {
                                                j = closestPhotoSizeWithSize4.size;
                                            }
                                            imageReceiver.setImage(forObject, "44_44", null, null, bitmapDrawable, j, null, messageObject, messageObject.shouldEncryptPhotoOrVideo() ? 2 : 1);
                                        } else {
                                            imageReceiver.setImage(ImageLocation.getForObject(closestPhotoSizeWithSize4, messageObject.photoThumbsObject), "44_44", ImageLocation.getForObject(closestPhotoSizeWithSize3, messageObject.photoThumbsObject), "b", closestPhotoSizeWithSize4 != null ? closestPhotoSizeWithSize4.size : 0L, null, messageObject, messageObject.shouldEncryptPhotoOrVideo() ? 2 : 1);
                                        }
                                    } else {
                                        BitmapDrawable bitmapDrawable2 = messageObject.strippedThumb;
                                        if (bitmapDrawable2 != null) {
                                            imageReceiver.setImage(null, null, bitmapDrawable2, null, messageObject, 0);
                                        } else {
                                            imageReceiver.setImage((ImageLocation) null, (String) null, ImageLocation.getForObject(closestPhotoSizeWithSize3, messageObject.photoThumbsObject), "b", (String) null, messageObject, 0);
                                        }
                                    }
                                }
                            }
                            imageReceiver.setRoundRadius(AndroidUtilities.dp(22.0f));
                            this.imagesByDays.put(keyAt, imageReceiver);
                        }
                    }
                    i4++;
                    z2 = false;
                }
            }
            int i5 = i2 + 1;
            this.daysInMonth = YearMonth.of(i, i5).lengthOfMonth();
            Calendar calendar = Calendar.getInstance();
            calendar.set(i, i2, 0);
            this.startDayOfWeek = (calendar.get(7) + 6) % 7;
            this.startMonthTime = (int) (calendar.getTimeInMillis() / 1000);
            int i6 = this.daysInMonth + this.startDayOfWeek;
            this.cellCount = ((int) (i6 / 7.0f)) + (i6 % 7 == 0 ? 0 : 1);
            calendar.set(i, i5, 0);
            this.titleView.setText(LocaleController.formatYearMont(calendar.getTimeInMillis() / 1000, true));
            CalendarActivity.this.updateRowSelections(this, false);
        }

        @Override // android.widget.FrameLayout, android.view.View
        protected void onMeasure(int i, int i2) {
            super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp((this.cellCount * 52) + 44), NUM));
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            float f;
            float f2;
            int i;
            float f3;
            float f4;
            float f5;
            float f6;
            PeriodDay periodDay;
            float f7;
            super.onDraw(canvas);
            int i2 = this.startDayOfWeek;
            float measuredWidth = getMeasuredWidth() / 7.0f;
            float dp = AndroidUtilities.dp(52.0f);
            float f8 = 44.0f;
            int dp2 = AndroidUtilities.dp(44.0f);
            for (int i3 = 0; i3 < Math.ceil((this.startDayOfWeek + this.daysInMonth) / 7.0f); i3++) {
                float dp3 = (i3 * dp) + (dp / 2.0f) + AndroidUtilities.dp(44.0f);
                RowAnimationValue rowAnimationValue = this.rowSelectionPos.get(i3);
                if (rowAnimationValue != null) {
                    CalendarActivity.this.selectPaint.setColor(Theme.getColor("chat_messagePanelVoiceBackground"));
                    CalendarActivity.this.selectPaint.setAlpha((int) (rowAnimationValue.alpha * 40.8f));
                    RectF rectF = AndroidUtilities.rectTmp;
                    float f9 = dp2 / 2.0f;
                    rectF.set(rowAnimationValue.startX - f9, dp3 - f9, rowAnimationValue.endX + f9, dp3 + f9);
                    float dp4 = AndroidUtilities.dp(32.0f);
                    canvas.drawRoundRect(rectF, dp4, dp4, CalendarActivity.this.selectPaint);
                }
            }
            int i4 = i2;
            int i5 = 0;
            int i6 = 0;
            while (i6 < this.daysInMonth) {
                float var_ = (i4 * measuredWidth) + (measuredWidth / 2.0f);
                float dp5 = (i5 * dp) + (dp / 2.0f) + AndroidUtilities.dp(f8);
                int currentTimeMillis = (int) (System.currentTimeMillis() / 1000);
                SparseArray<PeriodDay> sparseArray = this.messagesByDays;
                PeriodDay periodDay2 = null;
                if (sparseArray != null) {
                    periodDay2 = sparseArray.get(i6, null);
                }
                int i7 = i6 + 1;
                if (currentTimeMillis < this.startMonthTime + (i7 * 86400) || (CalendarActivity.this.minDate > 0 && CalendarActivity.this.minDate > this.startMonthTime + ((i6 + 2) * 86400))) {
                    f = measuredWidth;
                    f2 = dp;
                    i = i5;
                    int alpha = CalendarActivity.this.textPaint.getAlpha();
                    CalendarActivity.this.textPaint.setAlpha((int) (alpha * 0.3f));
                    canvas.drawText(Integer.toString(i7), var_, AndroidUtilities.dp(5.0f) + dp5, CalendarActivity.this.textPaint);
                    CalendarActivity.this.textPaint.setAlpha(alpha);
                } else if (periodDay2 != null && periodDay2.hasImage) {
                    if (this.imagesByDays.get(i6) != null) {
                        if (CalendarActivity.this.checkEnterItems && !periodDay2.wasDrawn) {
                            periodDay2.enterAlpha = 0.0f;
                            periodDay2.startEnterDelay = Math.max(0.0f, ((getY() + dp5) / CalendarActivity.this.listView.getMeasuredHeight()) * 150.0f);
                        }
                        float var_ = periodDay2.startEnterDelay;
                        if (var_ > 0.0f) {
                            float var_ = var_ - 16.0f;
                            periodDay2.startEnterDelay = var_;
                            if (var_ < 0.0f) {
                                periodDay2.startEnterDelay = 0.0f;
                            } else {
                                invalidate();
                            }
                        }
                        if (periodDay2.startEnterDelay >= 0.0f) {
                            float var_ = periodDay2.enterAlpha;
                            if (var_ != 1.0f) {
                                float var_ = var_ + 0.07272727f;
                                periodDay2.enterAlpha = var_;
                                if (var_ > 1.0f) {
                                    periodDay2.enterAlpha = 1.0f;
                                } else {
                                    invalidate();
                                }
                            }
                        }
                        f6 = periodDay2.enterAlpha;
                        if (f6 != 1.0f) {
                            canvas.save();
                            float var_ = (0.2f * f6) + 0.8f;
                            canvas.scale(var_, var_, var_, dp5);
                        }
                        int dp6 = (int) (AndroidUtilities.dp(7.0f) * periodDay2.selectProgress);
                        if (periodDay2.selectStartEndProgress >= 0.01f) {
                            CalendarActivity.this.selectPaint.setColor(Theme.getColor("windowBackgroundWhite"));
                            CalendarActivity.this.selectPaint.setAlpha((int) (periodDay2.selectStartEndProgress * 255.0f));
                            canvas.drawCircle(var_, dp5, AndroidUtilities.dp(44.0f) / 2.0f, CalendarActivity.this.selectPaint);
                            CalendarActivity.this.selectOutlinePaint.setColor(Theme.getColor("chat_messagePanelVoiceBackground"));
                            RectF rectF2 = AndroidUtilities.rectTmp;
                            f = measuredWidth;
                            rectF2.set(var_ - (AndroidUtilities.dp(44.0f) / 2.0f), dp5 - (AndroidUtilities.dp(44.0f) / 2.0f), (AndroidUtilities.dp(44.0f) / 2.0f) + var_, (AndroidUtilities.dp(44.0f) / 2.0f) + dp5);
                            periodDay = periodDay2;
                            f7 = dp5;
                            i = i5;
                            f2 = dp;
                            f4 = var_;
                            canvas.drawArc(rectF2, -90.0f, 360.0f * periodDay2.selectStartEndProgress, false, CalendarActivity.this.selectOutlinePaint);
                        } else {
                            periodDay = periodDay2;
                            f7 = dp5;
                            f = measuredWidth;
                            f2 = dp;
                            f4 = var_;
                            i = i5;
                        }
                        PeriodDay periodDay3 = periodDay;
                        this.imagesByDays.get(i6).setAlpha(periodDay3.enterAlpha);
                        f5 = f7;
                        this.imagesByDays.get(i6).setImageCoords(f4 - ((AndroidUtilities.dp(44.0f) - dp6) / 2.0f), f5 - ((AndroidUtilities.dp(44.0f) - dp6) / 2.0f), AndroidUtilities.dp(44.0f) - dp6, AndroidUtilities.dp(44.0f) - dp6);
                        this.imagesByDays.get(i6).draw(canvas);
                        CalendarActivity.this.blackoutPaint.setColor(ColorUtils.setAlphaComponent(-16777216, (int) (periodDay3.enterAlpha * 80.0f)));
                        canvas.drawCircle(f4, f5, (AndroidUtilities.dp(44.0f) - dp6) / 2.0f, CalendarActivity.this.blackoutPaint);
                        periodDay3.wasDrawn = true;
                        f3 = 1.0f;
                        if (f6 != 1.0f) {
                            canvas.restore();
                        }
                    } else {
                        f = measuredWidth;
                        f2 = dp;
                        f3 = 1.0f;
                        f4 = var_;
                        i = i5;
                        f5 = dp5;
                        f6 = 1.0f;
                    }
                    if (f6 != f3) {
                        int alpha2 = CalendarActivity.this.textPaint.getAlpha();
                        CalendarActivity.this.textPaint.setAlpha((int) (alpha2 * (f3 - f6)));
                        canvas.drawText(Integer.toString(i7), f4, f5 + AndroidUtilities.dp(5.0f), CalendarActivity.this.textPaint);
                        CalendarActivity.this.textPaint.setAlpha(alpha2);
                        int alpha3 = CalendarActivity.this.textPaint.getAlpha();
                        CalendarActivity.this.activeTextPaint.setAlpha((int) (alpha3 * f6));
                        canvas.drawText(Integer.toString(i7), f4, f5 + AndroidUtilities.dp(5.0f), CalendarActivity.this.activeTextPaint);
                        CalendarActivity.this.activeTextPaint.setAlpha(alpha3);
                    } else {
                        canvas.drawText(Integer.toString(i7), f4, f5 + AndroidUtilities.dp(5.0f), CalendarActivity.this.activeTextPaint);
                    }
                } else {
                    PeriodDay periodDay4 = periodDay2;
                    f = measuredWidth;
                    f2 = dp;
                    i = i5;
                    if (periodDay4 != null && periodDay4.selectStartEndProgress >= 0.01f) {
                        CalendarActivity.this.selectPaint.setColor(Theme.getColor("windowBackgroundWhite"));
                        CalendarActivity.this.selectPaint.setAlpha((int) (periodDay4.selectStartEndProgress * 255.0f));
                        canvas.drawCircle(var_, dp5, AndroidUtilities.dp(44.0f) / 2.0f, CalendarActivity.this.selectPaint);
                        CalendarActivity.this.selectOutlinePaint.setColor(Theme.getColor("chat_messagePanelVoiceBackground"));
                        RectF rectF3 = AndroidUtilities.rectTmp;
                        rectF3.set(var_ - (AndroidUtilities.dp(44.0f) / 2.0f), dp5 - (AndroidUtilities.dp(44.0f) / 2.0f), (AndroidUtilities.dp(44.0f) / 2.0f) + var_, (AndroidUtilities.dp(44.0f) / 2.0f) + dp5);
                        canvas.drawArc(rectF3, -90.0f, 360.0f * periodDay4.selectStartEndProgress, false, CalendarActivity.this.selectOutlinePaint);
                        CalendarActivity.this.selectPaint.setColor(Theme.getColor("chat_messagePanelVoiceBackground"));
                        CalendarActivity.this.selectPaint.setAlpha((int) (periodDay4.selectStartEndProgress * 255.0f));
                        canvas.drawCircle(var_, dp5, (AndroidUtilities.dp(44.0f) - ((int) (AndroidUtilities.dp(7.0f) * periodDay4.selectStartEndProgress))) / 2.0f, CalendarActivity.this.selectPaint);
                        float var_ = periodDay4.selectStartEndProgress;
                        if (var_ != 1.0f) {
                            int alpha4 = CalendarActivity.this.textPaint.getAlpha();
                            CalendarActivity.this.textPaint.setAlpha((int) (alpha4 * (1.0f - var_)));
                            canvas.drawText(Integer.toString(i7), var_, AndroidUtilities.dp(5.0f) + dp5, CalendarActivity.this.textPaint);
                            CalendarActivity.this.textPaint.setAlpha(alpha4);
                            int alpha5 = CalendarActivity.this.textPaint.getAlpha();
                            CalendarActivity.this.activeTextPaint.setAlpha((int) (alpha5 * var_));
                            canvas.drawText(Integer.toString(i7), var_, AndroidUtilities.dp(5.0f) + dp5, CalendarActivity.this.activeTextPaint);
                            CalendarActivity.this.activeTextPaint.setAlpha(alpha5);
                        } else {
                            canvas.drawText(Integer.toString(i7), var_, AndroidUtilities.dp(5.0f) + dp5, CalendarActivity.this.activeTextPaint);
                        }
                    } else {
                        canvas.drawText(Integer.toString(i7), var_, AndroidUtilities.dp(5.0f) + dp5, CalendarActivity.this.textPaint);
                    }
                }
                i4++;
                if (i4 >= 7) {
                    i5 = i + 1;
                    i4 = 0;
                } else {
                    i5 = i;
                }
                i6 = i7;
                dp = f2;
                measuredWidth = f;
                f8 = 44.0f;
            }
        }

        @Override // android.view.ViewGroup, android.view.View
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            if (this.imagesByDays != null) {
                for (int i = 0; i < this.imagesByDays.size(); i++) {
                    this.imagesByDays.valueAt(i).onAttachedToWindow();
                }
            }
        }

        @Override // android.view.ViewGroup, android.view.View
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            if (this.imagesByDays != null) {
                for (int i = 0; i < this.imagesByDays.size(); i++) {
                    this.imagesByDays.valueAt(i).onDetachedFromWindow();
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateTitle() {
        String string;
        HintView hintView;
        if (!this.canClearHistory) {
            this.actionBar.setTitle(LocaleController.getString("Calendar", R.string.Calendar));
            this.backDrawable.setRotation(0.0f, true);
            return;
        }
        int i = this.dateSelectedStart;
        int i2 = this.dateSelectedEnd;
        int abs = (i == i2 && i == 0) ? 0 : (Math.abs(i - i2) / 86400) + 1;
        boolean z = this.lastInSelectionMode;
        int i3 = this.lastDaysSelected;
        if (abs == i3 && z == this.inSelectionMode) {
            return;
        }
        boolean z2 = i3 > abs;
        this.lastDaysSelected = abs;
        boolean z3 = this.inSelectionMode;
        this.lastInSelectionMode = z3;
        float f = 1.0f;
        if (abs > 0) {
            string = LocaleController.formatPluralString("Days", abs, new Object[0]);
            this.backDrawable.setRotation(1.0f, true);
        } else if (z3) {
            string = LocaleController.getString("SelectDays", R.string.SelectDays);
            this.backDrawable.setRotation(1.0f, true);
        } else {
            string = LocaleController.getString("Calendar", R.string.Calendar);
            this.backDrawable.setRotation(0.0f, true);
        }
        if (abs > 1) {
            this.removeDaysButton.setText(LocaleController.formatString("ClearHistoryForTheseDays", R.string.ClearHistoryForTheseDays, new Object[0]));
        } else if (abs > 0 || this.inSelectionMode) {
            this.removeDaysButton.setText(LocaleController.formatString("ClearHistoryForThisDay", R.string.ClearHistoryForThisDay, new Object[0]));
        }
        this.actionBar.setTitleAnimated(string, z2, 150L);
        if ((!this.inSelectionMode || abs > 0) && (hintView = this.selectDaysHint) != null) {
            hintView.hide();
        }
        if (abs > 0 || this.inSelectionMode) {
            if (this.removeDaysButton.getVisibility() == 8) {
                this.removeDaysButton.setAlpha(0.0f);
                this.removeDaysButton.setTranslationY(-AndroidUtilities.dp(20.0f));
            }
            this.removeDaysButton.setVisibility(0);
            this.selectDaysButton.animate().setListener(null).cancel();
            this.removeDaysButton.animate().setListener(null).cancel();
            this.selectDaysButton.animate().alpha(0.0f).translationY(AndroidUtilities.dp(20.0f)).setDuration(150L).setListener(new HideViewAfterAnimation(this.selectDaysButton)).start();
            ViewPropertyAnimator animate = this.removeDaysButton.animate();
            if (abs == 0) {
                f = 0.5f;
            }
            animate.alpha(f).translationY(0.0f).start();
            this.selectDaysButton.setEnabled(false);
            this.removeDaysButton.setEnabled(true);
            return;
        }
        if (this.selectDaysButton.getVisibility() == 8) {
            this.selectDaysButton.setAlpha(0.0f);
            this.selectDaysButton.setTranslationY(AndroidUtilities.dp(20.0f));
        }
        this.selectDaysButton.setVisibility(0);
        this.selectDaysButton.animate().setListener(null).cancel();
        this.removeDaysButton.animate().setListener(null).cancel();
        this.selectDaysButton.animate().alpha(1.0f).translationY(0.0f).start();
        this.removeDaysButton.animate().alpha(0.0f).translationY(-AndroidUtilities.dp(20.0f)).setDuration(150L).setListener(new HideViewAfterAnimation(this.removeDaysButton)).start();
        this.selectDaysButton.setEnabled(true);
        this.removeDaysButton.setEnabled(false);
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public class PeriodDay {
        int date;
        float enterAlpha;
        float fromSelProgress;
        float fromSelSEProgress;
        boolean hasImage;
        MessageObject messageObject;
        float selectProgress;
        float selectStartEndProgress;
        float startEnterDelay;
        int startOffset;
        float toSelProgress;
        float toSelSEProgress;
        boolean wasDrawn;

        private PeriodDay(CalendarActivity calendarActivity) {
            this.enterAlpha = 1.0f;
            this.startEnterDelay = 1.0f;
            this.hasImage = true;
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate = new ThemeDescription.ThemeDescriptionDelegate() { // from class: org.telegram.ui.CalendarActivity.8
            @Override // org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate
            public /* synthetic */ void onAnimationProgress(float f) {
                ThemeDescription.ThemeDescriptionDelegate.CC.$default$onAnimationProgress(this, f);
            }

            @Override // org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate
            public void didSetColor() {
                CalendarActivity.this.updateColors();
            }
        };
        new ArrayList();
        new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, "windowBackgroundWhite");
        new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, "windowBackgroundWhiteBlackText");
        new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, "listSelectorSDK21");
        return super.getThemeDescriptions();
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onTransitionAnimationStart(boolean z, boolean z2) {
        super.onTransitionAnimationStart(z, z2);
        this.isOpened = true;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onTransitionAnimationProgress(boolean z, float f) {
        super.onTransitionAnimationProgress(z, f);
        View view = this.blurredView;
        if (view == null || view.getVisibility() != 0) {
            return;
        }
        if (z) {
            this.blurredView.setAlpha(1.0f - f);
        } else {
            this.blurredView.setAlpha(f);
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onTransitionAnimationEnd(boolean z, boolean z2) {
        View view;
        if (!z || (view = this.blurredView) == null || view.getVisibility() != 0) {
            return;
        }
        this.blurredView.setVisibility(8);
        this.blurredView.setBackground(null);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void animateSelection() {
        ValueAnimator duration = ValueAnimator.ofFloat(0.0f, 1.0f).setDuration(300L);
        duration.setInterpolator(CubicBezierInterpolator.DEFAULT);
        duration.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.CalendarActivity$$ExternalSyntheticLambda0
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                CalendarActivity.this.lambda$animateSelection$4(valueAnimator);
            }
        });
        duration.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.CalendarActivity.9
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationStart(Animator animator) {
                for (int i = 0; i < CalendarActivity.this.listView.getChildCount(); i++) {
                    ((MonthView) CalendarActivity.this.listView.getChildAt(i)).startSelectionAnimation(CalendarActivity.this.dateSelectedStart, CalendarActivity.this.dateSelectedEnd);
                }
            }
        });
        duration.start();
        this.selectionAnimator = duration;
        for (int i = 0; i < this.listView.getChildCount(); i++) {
            updateRowSelections((MonthView) this.listView.getChildAt(i), true);
        }
        for (int i2 = 0; i2 < this.listView.getCachedChildCount(); i2++) {
            MonthView monthView = (MonthView) this.listView.getCachedChildAt(i2);
            updateRowSelections(monthView, false);
            monthView.startSelectionAnimation(this.dateSelectedStart, this.dateSelectedEnd);
            monthView.setSelectionValue(1.0f);
        }
        for (int i3 = 0; i3 < this.listView.getHiddenChildCount(); i3++) {
            MonthView monthView2 = (MonthView) this.listView.getHiddenChildAt(i3);
            updateRowSelections(monthView2, false);
            monthView2.startSelectionAnimation(this.dateSelectedStart, this.dateSelectedEnd);
            monthView2.setSelectionValue(1.0f);
        }
        for (int i4 = 0; i4 < this.listView.getAttachedScrapChildCount(); i4++) {
            MonthView monthView3 = (MonthView) this.listView.getAttachedScrapChildAt(i4);
            updateRowSelections(monthView3, false);
            monthView3.startSelectionAnimation(this.dateSelectedStart, this.dateSelectedEnd);
            monthView3.setSelectionValue(1.0f);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$animateSelection$4(ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        for (int i = 0; i < this.listView.getChildCount(); i++) {
            ((MonthView) this.listView.getChildAt(i)).setSelectionValue(floatValue);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateRowSelections(MonthView monthView, boolean z) {
        int i;
        int i2;
        int i3;
        if (this.dateSelectedStart == 0 || this.dateSelectedEnd == 0) {
            monthView.dismissRowAnimations(z);
        } else if (monthView.messagesByDays != null) {
            if (!z) {
                monthView.dismissRowAnimations(false);
            }
            int i4 = monthView.startDayOfWeek;
            int i5 = 0;
            int i6 = -1;
            int i7 = -1;
            for (int i8 = 0; i8 < monthView.daysInMonth; i8++) {
                PeriodDay periodDay = monthView.messagesByDays.get(i8, null);
                if (periodDay == null || (i3 = periodDay.date) < this.dateSelectedStart || i3 > this.dateSelectedEnd) {
                    i = i6;
                    i2 = i7;
                } else {
                    if (i6 == -1) {
                        i6 = i4;
                    }
                    i2 = i4;
                    i = i6;
                }
                i4++;
                if (i4 >= 7) {
                    if (i != -1 && i2 != -1) {
                        monthView.animateRow(i5, i, i2, true, z);
                    } else {
                        monthView.animateRow(i5, 0, 0, false, z);
                    }
                    i5++;
                    i4 = 0;
                    i6 = -1;
                    i7 = -1;
                } else {
                    i6 = i;
                    i7 = i2;
                }
            }
            if (i6 != -1 && i7 != -1) {
                monthView.animateRow(i5, i6, i7, true, z);
            } else {
                monthView.animateRow(i5, 0, 0, false, z);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public static final class RowAnimationValue {
        float alpha;
        float endX;
        float startX;

        RowAnimationValue(float f, float f2) {
            this.startX = f;
            this.endX = f2;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void prepareBlurBitmap() {
        if (this.blurredView == null) {
            return;
        }
        int measuredWidth = (int) (this.parentLayout.getView().getMeasuredWidth() / 6.0f);
        int measuredHeight = (int) (this.parentLayout.getView().getMeasuredHeight() / 6.0f);
        Bitmap createBitmap = Bitmap.createBitmap(measuredWidth, measuredHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        canvas.scale(0.16666667f, 0.16666667f);
        this.parentLayout.getView().draw(canvas);
        Utilities.stackBlurBitmap(createBitmap, Math.max(7, Math.max(measuredWidth, measuredHeight) / 180));
        this.blurredView.setBackground(new BitmapDrawable(createBitmap));
        this.blurredView.setAlpha(0.0f);
        this.blurredView.setVisibility(0);
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean onBackPressed() {
        if (this.inSelectionMode) {
            this.inSelectionMode = false;
            this.dateSelectedEnd = 0;
            this.dateSelectedStart = 0;
            updateTitle();
            animateSelection();
            return false;
        }
        return super.onBackPressed();
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean isLightStatusBar() {
        return ColorUtils.calculateLuminance(Theme.getColor("windowBackgroundWhite", null, true)) > 0.699999988079071d;
    }
}
