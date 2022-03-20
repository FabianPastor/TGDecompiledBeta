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
import org.telegram.messenger.Utilities;
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
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.Easings;
import org.telegram.ui.Components.HideViewAfterAnimation;
import org.telegram.ui.Components.HintView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;

public class CalendarActivity extends BaseFragment {
    TextPaint activeTextPaint = new TextPaint(1);
    CalendarAdapter adapter;
    BackDrawable backDrawable;
    Paint blackoutPaint = new Paint(1);
    /* access modifiers changed from: private */
    public View blurredView;
    private FrameLayout bottomBar;
    /* access modifiers changed from: private */
    public int calendarType;
    Callback callback;
    /* access modifiers changed from: private */
    public boolean canClearHistory;
    /* access modifiers changed from: private */
    public boolean checkEnterItems;
    FrameLayout contentView;
    /* access modifiers changed from: private */
    public int dateSelectedEnd;
    /* access modifiers changed from: private */
    public int dateSelectedStart;
    /* access modifiers changed from: private */
    public long dialogId;
    boolean endReached;
    /* access modifiers changed from: private */
    public boolean inSelectionMode;
    private boolean isOpened;
    int lastDaysSelected;
    int lastId;
    boolean lastInSelectionMode;
    LinearLayoutManager layoutManager;
    RecyclerListView listView;
    private boolean loading;
    SparseArray<SparseArray<PeriodDay>> messagesByYearMounth = new SparseArray<>();
    /* access modifiers changed from: private */
    public int minDate;
    int minMontYear;
    int monthCount;
    private int photosVideosTypeFilter;
    TextView removeDaysButton;
    TextView selectDaysButton;
    HintView selectDaysHint;
    /* access modifiers changed from: private */
    public Paint selectOutlinePaint = new Paint(1);
    /* access modifiers changed from: private */
    public Paint selectPaint = new Paint(1);
    int selectedMonth;
    int selectedYear;
    /* access modifiers changed from: private */
    public ValueAnimator selectionAnimator;
    int startFromMonth;
    int startFromYear;
    int startOffset = 0;
    TextPaint textPaint = new TextPaint(1);
    TextPaint textPaint2 = new TextPaint(1);

    public interface Callback {
        void onDateSelected(int i, int i2);
    }

    public boolean needDelayOpenAnimation() {
        return true;
    }

    public CalendarActivity(Bundle bundle, int i, int i2) {
        super(bundle);
        this.photosVideosTypeFilter = i;
        if (i2 != 0) {
            Calendar instance = Calendar.getInstance();
            instance.setTimeInMillis(((long) i2) * 1000);
            this.selectedYear = instance.get(1);
            this.selectedMonth = instance.get(2);
        }
        this.selectOutlinePaint.setStyle(Paint.Style.STROKE);
        this.selectOutlinePaint.setStrokeCap(Paint.Cap.ROUND);
        this.selectOutlinePaint.setStrokeWidth((float) AndroidUtilities.dp(2.0f));
    }

    public boolean onFragmentCreate() {
        this.dialogId = getArguments().getLong("dialog_id");
        this.calendarType = getArguments().getInt("type");
        if (this.dialogId >= 0) {
            this.canClearHistory = true;
        } else {
            this.canClearHistory = false;
        }
        return super.onFragmentCreate();
    }

    public View createView(Context context) {
        Context context2 = context;
        this.textPaint.setTextSize((float) AndroidUtilities.dp(16.0f));
        this.textPaint.setTextAlign(Paint.Align.CENTER);
        this.textPaint2.setTextSize((float) AndroidUtilities.dp(11.0f));
        this.textPaint2.setTextAlign(Paint.Align.CENTER);
        this.textPaint2.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.activeTextPaint.setTextSize((float) AndroidUtilities.dp(16.0f));
        this.activeTextPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.activeTextPaint.setTextAlign(Paint.Align.CENTER);
        this.contentView = new FrameLayout(context2) {
            int lastSize;

            /* access modifiers changed from: protected */
            public void onLayout(boolean z, int i, int i2, int i3, int i4) {
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
        this.actionBar.setTitle(LocaleController.getString("Calendar", NUM));
        this.actionBar.setCastShadows(false);
        AnonymousClass2 r2 = new RecyclerListView(context2) {
            /* access modifiers changed from: protected */
            public void dispatchDraw(Canvas canvas) {
                super.dispatchDraw(canvas);
                boolean unused = CalendarActivity.this.checkEnterItems = false;
            }
        };
        this.listView = r2;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context2);
        this.layoutManager = linearLayoutManager;
        r2.setLayoutManager(linearLayoutManager);
        this.layoutManager.setReverseLayout(true);
        RecyclerListView recyclerListView = this.listView;
        CalendarAdapter calendarAdapter = new CalendarAdapter();
        this.adapter = calendarAdapter;
        recyclerListView.setAdapter(calendarAdapter);
        this.listView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                super.onScrolled(recyclerView, i, i2);
                CalendarActivity.this.checkLoadNext();
            }
        });
        boolean z = this.calendarType == 0 && this.canClearHistory;
        this.contentView.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f, 0, 0.0f, 36.0f, 0.0f, z ? 48.0f : 0.0f));
        final String[] strArr = {LocaleController.getString("CalendarWeekNameShortMonday", NUM), LocaleController.getString("CalendarWeekNameShortTuesday", NUM), LocaleController.getString("CalendarWeekNameShortWednesday", NUM), LocaleController.getString("CalendarWeekNameShortThursday", NUM), LocaleController.getString("CalendarWeekNameShortFriday", NUM), LocaleController.getString("CalendarWeekNameShortSaturday", NUM), LocaleController.getString("CalendarWeekNameShortSunday", NUM)};
        final Drawable mutate = ContextCompat.getDrawable(context2, NUM).mutate();
        this.contentView.addView(new View(context2) {
            /* access modifiers changed from: protected */
            public void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                float measuredWidth = ((float) getMeasuredWidth()) / 7.0f;
                for (int i = 0; i < 7; i++) {
                    canvas.drawText(strArr[i], (((float) i) * measuredWidth) + (measuredWidth / 2.0f), (((float) (getMeasuredHeight() - AndroidUtilities.dp(2.0f))) / 2.0f) + ((float) AndroidUtilities.dp(5.0f)), CalendarActivity.this.textPaint2);
                }
                mutate.setBounds(0, getMeasuredHeight() - AndroidUtilities.dp(3.0f), getMeasuredWidth(), getMeasuredHeight());
                mutate.draw(canvas);
            }
        }, LayoutHelper.createFrame(-1, 38.0f, 0, 0.0f, 0.0f, 0.0f, 0.0f));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i != -1) {
                    return;
                }
                if (CalendarActivity.this.dateSelectedStart == 0 && CalendarActivity.this.dateSelectedEnd == 0 && !CalendarActivity.this.inSelectionMode) {
                    CalendarActivity.this.finishFragment();
                    return;
                }
                boolean unused = CalendarActivity.this.inSelectionMode = false;
                int unused2 = CalendarActivity.this.dateSelectedStart = 0;
                int unused3 = CalendarActivity.this.dateSelectedEnd = 0;
                CalendarActivity.this.updateTitle();
                CalendarActivity.this.animateSelection();
            }
        });
        this.fragmentView = this.contentView;
        Calendar instance = Calendar.getInstance();
        this.startFromYear = instance.get(1);
        int i = instance.get(2);
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
        BackDrawable backDrawable2 = new BackDrawable(false);
        this.backDrawable = backDrawable2;
        this.actionBar.setBackButtonDrawable(backDrawable2);
        this.backDrawable.setRotation(0.0f, false);
        loadNext();
        updateColors();
        this.activeTextPaint.setColor(-1);
        if (z) {
            AnonymousClass6 r22 = new FrameLayout(this, context2) {
                public void onDraw(Canvas canvas) {
                    canvas.drawRect(0.0f, 0.0f, (float) getMeasuredWidth(), (float) AndroidUtilities.getShadowHeight(), Theme.dividerPaint);
                }
            };
            this.bottomBar = r22;
            r22.setWillNotDraw(false);
            this.bottomBar.setPadding(0, AndroidUtilities.getShadowHeight(), 0, 0);
            this.bottomBar.setClipChildren(false);
            TextView textView = new TextView(context2);
            this.selectDaysButton = textView;
            textView.setGravity(17);
            this.selectDaysButton.setTextSize(1, 15.0f);
            this.selectDaysButton.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.selectDaysButton.setOnClickListener(new CalendarActivity$$ExternalSyntheticLambda2(this));
            this.selectDaysButton.setText(LocaleController.getString("SelectDays", NUM));
            this.selectDaysButton.setAllCaps(true);
            this.bottomBar.addView(this.selectDaysButton, LayoutHelper.createFrame(-1, -1.0f, 0, 0.0f, 0.0f, 0.0f, 0.0f));
            TextView textView2 = new TextView(context2);
            this.removeDaysButton = textView2;
            textView2.setGravity(17);
            this.removeDaysButton.setTextSize(1, 15.0f);
            this.removeDaysButton.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.removeDaysButton.setOnClickListener(new CalendarActivity$$ExternalSyntheticLambda1(this));
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

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$0(View view) {
        this.inSelectionMode = true;
        updateTitle();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$1(View view) {
        int i = this.lastDaysSelected;
        if (i == 0) {
            if (this.selectDaysHint == null) {
                HintView hintView = new HintView(this.contentView.getContext(), 8);
                this.selectDaysHint = hintView;
                hintView.setExtraTranslationY((float) AndroidUtilities.dp(24.0f));
                this.contentView.addView(this.selectDaysHint, LayoutHelper.createFrame(-2, -2.0f, 51, 19.0f, 0.0f, 19.0f, 0.0f));
                this.selectDaysHint.setText(LocaleController.getString("SelectDaysTooltip", NUM));
            }
            this.selectDaysHint.showForView(this.bottomBar, true);
            return;
        }
        AlertsCreator.createClearDaysDialogAlert(this, i, getMessagesController().getUser(Long.valueOf(this.dialogId)), new MessagesStorage.BooleanCallback() {
            public void run(boolean z) {
                CalendarActivity.this.finishFragment();
                if (CalendarActivity.this.parentLayout.fragmentsStack.size() >= 2) {
                    BaseFragment baseFragment = CalendarActivity.this.parentLayout.fragmentsStack.get(CalendarActivity.this.parentLayout.fragmentsStack.size() - 2);
                    if (baseFragment instanceof ChatActivity) {
                        ((ChatActivity) baseFragment).deleteHistory(CalendarActivity.this.dateSelectedStart, CalendarActivity.this.dateSelectedEnd + 86400, z);
                    }
                }
            }
        }, (Theme.ResourcesProvider) null);
    }

    /* access modifiers changed from: private */
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
        if (!this.loading && !this.endReached) {
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
            Calendar instance = Calendar.getInstance();
            this.listView.setItemAnimator((RecyclerView.ItemAnimator) null);
            getConnectionsManager().sendRequest(tLRPC$TL_messages_getSearchResultsCalendar, new CalendarActivity$$ExternalSyntheticLambda4(this, instance));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadNext$3(Calendar calendar, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new CalendarActivity$$ExternalSyntheticLambda3(this, tLRPC$TL_error, tLObject, calendar));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadNext$2(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, Calendar calendar) {
        int i;
        int i2;
        Calendar calendar2 = calendar;
        if (tLRPC$TL_error == null) {
            TLRPC$TL_messages_searchResultsCalendar tLRPC$TL_messages_searchResultsCalendar = (TLRPC$TL_messages_searchResultsCalendar) tLObject;
            int i3 = 0;
            while (true) {
                i = 5;
                i2 = 2;
                if (i3 >= tLRPC$TL_messages_searchResultsCalendar.periods.size()) {
                    break;
                }
                calendar2.setTimeInMillis(((long) tLRPC$TL_messages_searchResultsCalendar.periods.get(i3).date) * 1000);
                int i4 = (calendar2.get(1) * 100) + calendar2.get(2);
                SparseArray sparseArray = this.messagesByYearMounth.get(i4);
                if (sparseArray == null) {
                    sparseArray = new SparseArray();
                    this.messagesByYearMounth.put(i4, sparseArray);
                }
                PeriodDay periodDay = new PeriodDay();
                periodDay.messageObject = new MessageObject(this.currentAccount, tLRPC$TL_messages_searchResultsCalendar.messages.get(i3), false, false);
                periodDay.date = (int) (calendar.getTimeInMillis() / 1000);
                int i5 = this.startOffset + tLRPC$TL_messages_searchResultsCalendar.periods.get(i3).count;
                this.startOffset = i5;
                periodDay.startOffset = i5;
                int i6 = calendar2.get(5) - 1;
                if (sparseArray.get(i6, (Object) null) == null || !((PeriodDay) sparseArray.get(i6, (Object) null)).hasImage) {
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
                calendar2.setTimeInMillis(((long) i8) * 1000);
                calendar2.set(11, 0);
                calendar2.set(12, 0);
                calendar2.set(13, 0);
                calendar2.set(14, 0);
                int i9 = (calendar2.get(1) * 100) + calendar2.get(i2);
                SparseArray sparseArray2 = this.messagesByYearMounth.get(i9);
                if (sparseArray2 == null) {
                    sparseArray2 = new SparseArray();
                    this.messagesByYearMounth.put(i9, sparseArray2);
                }
                int i10 = calendar2.get(i) - 1;
                if (sparseArray2.get(i10, (Object) null) == null) {
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
            int timeInMillis = ((int) (((calendar.getTimeInMillis() / 1000) - ((long) tLRPC$TL_messages_searchResultsCalendar.min_date)) / 2629800)) + 1;
            this.adapter.notifyItemRangeChanged(0, this.monthCount);
            int i11 = this.monthCount;
            if (timeInMillis > i11) {
                this.adapter.notifyItemRangeInserted(i11 + 1, timeInMillis);
                this.monthCount = timeInMillis;
            }
            if (this.endReached) {
                resumeDelayedFragmentAnimation();
            }
        }
    }

    /* access modifiers changed from: private */
    public void checkLoadNext() {
        if (!this.loading && !this.endReached) {
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
            if (((i4 / 100) * 12) + (i4 % 100) + 3 >= ((i / 100) * 12) + (i % 100)) {
                loadNext();
            }
        }
    }

    private class CalendarAdapter extends RecyclerView.Adapter {
        private CalendarAdapter() {
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            return new RecyclerListView.Holder(new MonthView(viewGroup.getContext()));
        }

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

        public long getItemId(int i) {
            CalendarActivity calendarActivity = CalendarActivity.this;
            return (((long) (calendarActivity.startFromYear - (i / 12))) * 100) + ((long) (calendarActivity.startFromMonth - (i % 12)));
        }

        public int getItemCount() {
            return CalendarActivity.this.monthCount;
        }
    }

    private class MonthView extends FrameLayout {
        int cellCount;
        int currentMonthInYear;
        int currentYear;
        int daysInMonth;
        GestureDetectorCompat gestureDetector;
        SparseArray<ImageReceiver> imagesByDays = new SparseArray<>();
        SparseArray<PeriodDay> messagesByDays = new SparseArray<>();
        /* access modifiers changed from: private */
        public SparseArray<ValueAnimator> rowAnimators = new SparseArray<>();
        /* access modifiers changed from: private */
        public SparseArray<RowAnimationValue> rowSelectionPos = new SparseArray<>();
        int startDayOfWeek;
        int startMonthTime;
        SimpleTextView titleView;

        public MonthView(Context context) {
            super(context);
            boolean z = false;
            setWillNotDraw(false);
            this.titleView = new SimpleTextView(context);
            if (CalendarActivity.this.calendarType == 0 && CalendarActivity.this.canClearHistory) {
                this.titleView.setOnLongClickListener(new CalendarActivity$MonthView$$ExternalSyntheticLambda1(this));
                this.titleView.setOnClickListener(new View.OnClickListener(CalendarActivity.this) {
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
                                PeriodDay periodDay = monthView.messagesByDays.get(i, (Object) null);
                                if (periodDay != null) {
                                    if (i2 == -1) {
                                        i2 = periodDay.date;
                                    }
                                    i3 = periodDay.date;
                                }
                                i++;
                            }
                            if (i2 >= 0 && i3 >= 0) {
                                int unused = CalendarActivity.this.dateSelectedStart = i2;
                                int unused2 = CalendarActivity.this.dateSelectedEnd = i3;
                                CalendarActivity.this.updateTitle();
                                CalendarActivity.this.animateSelection();
                            }
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
            GestureDetectorCompat gestureDetectorCompat = new GestureDetectorCompat(context, new GestureDetector.SimpleOnGestureListener(CalendarActivity.this, context) {
                final /* synthetic */ Context val$context;

                public boolean onDown(MotionEvent motionEvent) {
                    return true;
                }

                {
                    this.val$context = r3;
                }

                @SuppressLint({"NotifyDataSetChanged"})
                public boolean onSingleTapUp(MotionEvent motionEvent) {
                    PeriodDay dayAtCoord;
                    MessageObject messageObject;
                    Callback callback;
                    if (!(CalendarActivity.this.calendarType != 1 || MonthView.this.messagesByDays == null || (dayAtCoord = getDayAtCoord(motionEvent.getX(), motionEvent.getY())) == null || (messageObject = dayAtCoord.messageObject) == null || (callback = CalendarActivity.this.callback) == null)) {
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
                                    ValueAnimator unused = CalendarActivity.this.selectionAnimator = null;
                                }
                                if (CalendarActivity.this.dateSelectedStart == 0 && CalendarActivity.this.dateSelectedEnd == 0) {
                                    CalendarActivity calendarActivity = CalendarActivity.this;
                                    int unused2 = calendarActivity.dateSelectedStart = calendarActivity.dateSelectedEnd = dayAtCoord2.date;
                                } else if (CalendarActivity.this.dateSelectedStart == dayAtCoord2.date && CalendarActivity.this.dateSelectedEnd == dayAtCoord2.date) {
                                    CalendarActivity calendarActivity2 = CalendarActivity.this;
                                    int unused3 = calendarActivity2.dateSelectedStart = calendarActivity2.dateSelectedEnd = 0;
                                } else if (CalendarActivity.this.dateSelectedStart == dayAtCoord2.date) {
                                    CalendarActivity calendarActivity3 = CalendarActivity.this;
                                    int unused4 = calendarActivity3.dateSelectedStart = calendarActivity3.dateSelectedEnd;
                                } else if (CalendarActivity.this.dateSelectedEnd == dayAtCoord2.date) {
                                    CalendarActivity calendarActivity4 = CalendarActivity.this;
                                    int unused5 = calendarActivity4.dateSelectedEnd = calendarActivity4.dateSelectedStart;
                                } else if (CalendarActivity.this.dateSelectedStart != CalendarActivity.this.dateSelectedEnd) {
                                    CalendarActivity calendarActivity5 = CalendarActivity.this;
                                    int unused6 = calendarActivity5.dateSelectedStart = calendarActivity5.dateSelectedEnd = dayAtCoord2.date;
                                } else if (dayAtCoord2.date > CalendarActivity.this.dateSelectedEnd) {
                                    int unused7 = CalendarActivity.this.dateSelectedEnd = dayAtCoord2.date;
                                } else {
                                    int unused8 = CalendarActivity.this.dateSelectedStart = dayAtCoord2.date;
                                }
                                CalendarActivity.this.updateTitle();
                                CalendarActivity.this.animateSelection();
                            }
                        } else {
                            PeriodDay dayAtCoord3 = getDayAtCoord(motionEvent.getX(), motionEvent.getY());
                            if (CalendarActivity.this.parentLayout.fragmentsStack.size() >= 2) {
                                BaseFragment baseFragment = CalendarActivity.this.parentLayout.fragmentsStack.get(CalendarActivity.this.parentLayout.fragmentsStack.size() - 2);
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
                    float measuredWidth = ((float) monthView.getMeasuredWidth()) / 7.0f;
                    float dp = (float) AndroidUtilities.dp(52.0f);
                    int dp2 = AndroidUtilities.dp(44.0f) / 2;
                    int i2 = 0;
                    for (int i3 = 0; i3 < MonthView.this.daysInMonth; i3++) {
                        float f3 = (((float) i) * measuredWidth) + (measuredWidth / 2.0f);
                        float dp3 = (((float) i2) * dp) + (dp / 2.0f) + ((float) AndroidUtilities.dp(44.0f));
                        float f4 = (float) dp2;
                        if (f >= f3 - f4 && f <= f3 + f4 && f2 >= dp3 - f4 && f2 <= dp3 + f4 && (periodDay = MonthView.this.messagesByDays.get(i3, (Object) null)) != null) {
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

                public void onLongPress(MotionEvent motionEvent) {
                    PeriodDay dayAtCoord;
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
                        ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = new ActionBarPopupWindow.ActionBarPopupWindowLayout(CalendarActivity.this.getParentActivity(), NUM, CalendarActivity.this.getResourceProvider());
                        actionBarPopupWindowLayout.setBackgroundColor(CalendarActivity.this.getThemedColor("actionBarDefaultSubmenuBackground"));
                        ActionBarMenuSubItem actionBarMenuSubItem = new ActionBarMenuSubItem(CalendarActivity.this.getParentActivity(), true, false);
                        actionBarMenuSubItem.setTextAndIcon(LocaleController.getString("JumpToDate", NUM), NUM);
                        actionBarMenuSubItem.setMinimumWidth(160);
                        actionBarMenuSubItem.setOnClickListener(new CalendarActivity$MonthView$2$$ExternalSyntheticLambda2(this, dayAtCoord));
                        actionBarPopupWindowLayout.addView(actionBarMenuSubItem);
                        if (CalendarActivity.this.canClearHistory) {
                            ActionBarMenuSubItem actionBarMenuSubItem2 = new ActionBarMenuSubItem(CalendarActivity.this.getParentActivity(), false, false);
                            actionBarMenuSubItem2.setTextAndIcon(LocaleController.getString("SelectThisDay", NUM), NUM);
                            actionBarMenuSubItem2.setMinimumWidth(160);
                            actionBarMenuSubItem2.setOnClickListener(new CalendarActivity$MonthView$2$$ExternalSyntheticLambda3(this, dayAtCoord));
                            actionBarPopupWindowLayout.addView(actionBarMenuSubItem2);
                            ActionBarMenuSubItem actionBarMenuSubItem3 = new ActionBarMenuSubItem(CalendarActivity.this.getParentActivity(), false, true);
                            actionBarMenuSubItem3.setTextAndIcon(LocaleController.getString("ClearHistory", NUM), NUM);
                            actionBarMenuSubItem3.setMinimumWidth(160);
                            actionBarMenuSubItem3.setOnClickListener(new CalendarActivity$MonthView$2$$ExternalSyntheticLambda0(this));
                            actionBarPopupWindowLayout.addView(actionBarMenuSubItem3);
                        }
                        actionBarPopupWindowLayout.setFitItems(true);
                        View unused = CalendarActivity.this.blurredView = new View(this.val$context) {
                            public void setAlpha(float f) {
                                super.setAlpha(f);
                                if (CalendarActivity.this.fragmentView != null) {
                                    CalendarActivity.this.fragmentView.invalidate();
                                }
                            }
                        };
                        CalendarActivity.this.blurredView.setOnClickListener(new CalendarActivity$MonthView$2$$ExternalSyntheticLambda1(this));
                        CalendarActivity.this.blurredView.setVisibility(8);
                        CalendarActivity.this.blurredView.setFitsSystemWindows(true);
                        CalendarActivity.this.parentLayout.containerView.addView(CalendarActivity.this.blurredView, LayoutHelper.createFrame(-1, -1.0f));
                        CalendarActivity.this.prepareBlurBitmap();
                        CalendarActivity.this.presentFragmentAsPreviewWithMenu(chatActivity, actionBarPopupWindowLayout);
                    }
                }

                /* access modifiers changed from: private */
                public /* synthetic */ void lambda$onLongPress$1(PeriodDay periodDay, View view) {
                    if (CalendarActivity.this.parentLayout.fragmentsStack.size() >= 3) {
                        BaseFragment baseFragment = CalendarActivity.this.parentLayout.fragmentsStack.get(CalendarActivity.this.parentLayout.fragmentsStack.size() - 3);
                        if (baseFragment instanceof ChatActivity) {
                            AndroidUtilities.runOnUIThread(new CalendarActivity$MonthView$2$$ExternalSyntheticLambda4(this, baseFragment, periodDay), 300);
                        }
                    }
                    CalendarActivity.this.finishPreviewFragment();
                }

                /* access modifiers changed from: private */
                public /* synthetic */ void lambda$onLongPress$0(BaseFragment baseFragment, PeriodDay periodDay) {
                    CalendarActivity.this.finishFragment();
                    ((ChatActivity) baseFragment).jumpToDate(periodDay.date);
                }

                /* access modifiers changed from: private */
                public /* synthetic */ void lambda$onLongPress$2(PeriodDay periodDay, View view) {
                    CalendarActivity calendarActivity = CalendarActivity.this;
                    int unused = calendarActivity.dateSelectedStart = calendarActivity.dateSelectedEnd = periodDay.date;
                    boolean unused2 = CalendarActivity.this.inSelectionMode = true;
                    CalendarActivity.this.updateTitle();
                    CalendarActivity.this.animateSelection();
                    CalendarActivity.this.finishPreviewFragment();
                }

                /* access modifiers changed from: private */
                public /* synthetic */ void lambda$onLongPress$3(View view) {
                    if (CalendarActivity.this.parentLayout.fragmentsStack.size() >= 3) {
                        final BaseFragment baseFragment = CalendarActivity.this.parentLayout.fragmentsStack.get(CalendarActivity.this.parentLayout.fragmentsStack.size() - 3);
                        if (baseFragment instanceof ChatActivity) {
                            CalendarActivity calendarActivity = CalendarActivity.this;
                            AlertsCreator.createClearDaysDialogAlert(calendarActivity, 1, calendarActivity.getMessagesController().getUser(Long.valueOf(CalendarActivity.this.dialogId)), new MessagesStorage.BooleanCallback() {
                                public void run(boolean z) {
                                    CalendarActivity.this.finishFragment();
                                    ((ChatActivity) baseFragment).deleteHistory(CalendarActivity.this.dateSelectedStart, CalendarActivity.this.dateSelectedEnd + 86400, z);
                                }
                            }, (Theme.ResourcesProvider) null);
                        }
                    }
                    CalendarActivity.this.finishPreviewFragment();
                }

                /* access modifiers changed from: private */
                public /* synthetic */ void lambda$onLongPress$4(View view) {
                    CalendarActivity.this.finishPreviewFragment();
                }
            });
            this.gestureDetector = gestureDetectorCompat;
            gestureDetectorCompat.setIsLongpressEnabled(CalendarActivity.this.calendarType == 0 ? true : z);
        }

        /* access modifiers changed from: private */
        public /* synthetic */ boolean lambda$new$0(View view) {
            if (this.messagesByDays == null) {
                return false;
            }
            int i = -1;
            int i2 = -1;
            for (int i3 = 0; i3 < this.daysInMonth; i3++) {
                PeriodDay periodDay = this.messagesByDays.get(i3, (Object) null);
                if (periodDay != null) {
                    if (i == -1) {
                        i = periodDay.date;
                    }
                    i2 = periodDay.date;
                }
            }
            if (i >= 0 && i2 >= 0) {
                boolean unused = CalendarActivity.this.inSelectionMode = true;
                int unused2 = CalendarActivity.this.dateSelectedStart = i;
                int unused3 = CalendarActivity.this.dateSelectedEnd = i2;
                CalendarActivity.this.updateTitle();
                CalendarActivity.this.animateSelection();
            }
            return false;
        }

        /* access modifiers changed from: private */
        public void startSelectionAnimation(int i, int i2) {
            if (this.messagesByDays != null) {
                for (int i3 = 0; i3 < this.daysInMonth; i3++) {
                    PeriodDay periodDay = this.messagesByDays.get(i3, (Object) null);
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

        /* access modifiers changed from: private */
        public void setSelectionValue(float f) {
            if (this.messagesByDays != null) {
                for (int i = 0; i < this.daysInMonth; i++) {
                    PeriodDay periodDay = this.messagesByDays.get(i, (Object) null);
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

        public void animateRow(int i, int i2, int i3, boolean z, boolean z2) {
            float f;
            float f2;
            float f3;
            int i4 = i;
            int i5 = i2;
            ValueAnimator valueAnimator = this.rowAnimators.get(i4);
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            float measuredWidth = ((float) getMeasuredWidth()) / 7.0f;
            RowAnimationValue rowAnimationValue = this.rowSelectionPos.get(i4);
            if (rowAnimationValue != null) {
                f3 = rowAnimationValue.startX;
                f2 = rowAnimationValue.endX;
                f = rowAnimationValue.alpha;
            } else {
                f3 = (((float) i5) * measuredWidth) + (measuredWidth / 2.0f);
                f2 = f3;
                f = 0.0f;
            }
            float f4 = z ? (((float) i5) * measuredWidth) + (measuredWidth / 2.0f) : f3;
            float f5 = z ? (((float) i3) * measuredWidth) + (measuredWidth / 2.0f) : f2;
            float f6 = z ? 1.0f : 0.0f;
            RowAnimationValue rowAnimationValue2 = new RowAnimationValue(f3, f2);
            this.rowSelectionPos.put(i4, rowAnimationValue2);
            if (z2) {
                ValueAnimator duration = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f}).setDuration(300);
                duration.setInterpolator(Easings.easeInOutQuad);
                final RowAnimationValue rowAnimationValue3 = rowAnimationValue2;
                CalendarActivity$MonthView$$ExternalSyntheticLambda0 calendarActivity$MonthView$$ExternalSyntheticLambda0 = r0;
                CalendarActivity$MonthView$$ExternalSyntheticLambda0 calendarActivity$MonthView$$ExternalSyntheticLambda02 = new CalendarActivity$MonthView$$ExternalSyntheticLambda0(this, rowAnimationValue3, f3, f4, f2, f5, f, f6);
                duration.addUpdateListener(calendarActivity$MonthView$$ExternalSyntheticLambda0);
                final float f7 = f4;
                final float f8 = f5;
                final float f9 = f6;
                final int i6 = i;
                final boolean z3 = z;
                duration.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationCancel(Animator animator) {
                        RowAnimationValue rowAnimationValue = rowAnimationValue3;
                        rowAnimationValue.startX = f7;
                        rowAnimationValue.endX = f8;
                        rowAnimationValue.alpha = f9;
                        MonthView.this.invalidate();
                    }

                    public void onAnimationEnd(Animator animator) {
                        MonthView.this.rowAnimators.remove(i6);
                        if (!z3) {
                            MonthView.this.rowSelectionPos.remove(i6);
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

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$animateRow$1(RowAnimationValue rowAnimationValue, float f, float f2, float f3, float f4, float f5, float f6, ValueAnimator valueAnimator) {
            float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            rowAnimationValue.startX = f + ((f2 - f) * floatValue);
            rowAnimationValue.endX = f3 + ((f4 - f3) * floatValue);
            rowAnimationValue.alpha = f5 + ((f6 - f5) * floatValue);
            invalidate();
        }

        @SuppressLint({"ClickableViewAccessibility"})
        public boolean onTouchEvent(MotionEvent motionEvent) {
            return this.gestureDetector.onTouchEvent(motionEvent);
        }

        public void setDate(int i, int i2, SparseArray<PeriodDay> sparseArray, boolean z) {
            int i3 = i;
            int i4 = i2;
            SparseArray<PeriodDay> sparseArray2 = sparseArray;
            boolean z2 = (i3 == this.currentYear && i4 == this.currentMonthInYear) ? false : true;
            this.currentYear = i3;
            this.currentMonthInYear = i4;
            this.messagesByDays = sparseArray2;
            if (z2 && this.imagesByDays != null) {
                for (int i5 = 0; i5 < this.imagesByDays.size(); i5++) {
                    this.imagesByDays.valueAt(i5).onDetachedFromWindow();
                    this.imagesByDays.valueAt(i5).setParentView((View) null);
                }
                this.imagesByDays = null;
            }
            if (sparseArray2 != null) {
                if (this.imagesByDays == null) {
                    this.imagesByDays = new SparseArray<>();
                }
                for (int i6 = 0; i6 < sparseArray.size(); i6++) {
                    int keyAt = sparseArray2.keyAt(i6);
                    if (this.imagesByDays.get(keyAt, (Object) null) == null && sparseArray2.get(keyAt).hasImage) {
                        ImageReceiver imageReceiver = new ImageReceiver();
                        imageReceiver.setParentView(this);
                        MessageObject messageObject = sparseArray2.get(keyAt).messageObject;
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
                                        imageReceiver.setImage(ImageLocation.getForDocument(closestPhotoSizeWithSize2, document), "44_44", messageObject.strippedThumb, (String) null, messageObject, 0);
                                    } else {
                                        imageReceiver.setImage(ImageLocation.getForDocument(closestPhotoSizeWithSize2, document), "44_44", ImageLocation.getForDocument(closestPhotoSizeWithSize, document), "b", (String) null, (Object) messageObject, 0);
                                    }
                                }
                            } else {
                                TLRPC$MessageMedia tLRPC$MessageMedia = messageObject.messageOwner.media;
                                if ((tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaPhoto) && tLRPC$MessageMedia.photo != null && !messageObject.photoThumbs.isEmpty()) {
                                    TLRPC$PhotoSize closestPhotoSizeWithSize3 = FileLoader.getClosestPhotoSizeWithSize(messageObject.photoThumbs, 50);
                                    TLRPC$PhotoSize closestPhotoSizeWithSize4 = FileLoader.getClosestPhotoSizeWithSize(messageObject.photoThumbs, 320, false, closestPhotoSizeWithSize3, false);
                                    if (messageObject.mediaExists || DownloadController.getInstance(CalendarActivity.this.currentAccount).canDownloadMedia(messageObject)) {
                                        MessageObject messageObject2 = messageObject;
                                        if (closestPhotoSizeWithSize4 == closestPhotoSizeWithSize3) {
                                            closestPhotoSizeWithSize3 = null;
                                        }
                                        if (messageObject2.strippedThumb != null) {
                                            imageReceiver.setImage(ImageLocation.getForObject(closestPhotoSizeWithSize4, messageObject2.photoThumbsObject), "44_44", (ImageLocation) null, (String) null, messageObject2.strippedThumb, closestPhotoSizeWithSize4 != null ? closestPhotoSizeWithSize4.size : 0, (String) null, messageObject2, messageObject2.shouldEncryptPhotoOrVideo() ? 2 : 1);
                                        } else {
                                            imageReceiver.setImage(ImageLocation.getForObject(closestPhotoSizeWithSize4, messageObject2.photoThumbsObject), "44_44", ImageLocation.getForObject(closestPhotoSizeWithSize3, messageObject2.photoThumbsObject), "b", closestPhotoSizeWithSize4 != null ? closestPhotoSizeWithSize4.size : 0, (String) null, messageObject2, messageObject2.shouldEncryptPhotoOrVideo() ? 2 : 1);
                                        }
                                    } else {
                                        BitmapDrawable bitmapDrawable = messageObject.strippedThumb;
                                        if (bitmapDrawable != null) {
                                            imageReceiver.setImage((ImageLocation) null, (String) null, bitmapDrawable, (String) null, messageObject, 0);
                                        } else {
                                            imageReceiver.setImage((ImageLocation) null, (String) null, ImageLocation.getForObject(closestPhotoSizeWithSize3, messageObject.photoThumbsObject), "b", (String) null, (Object) messageObject, 0);
                                        }
                                    }
                                }
                            }
                            imageReceiver.setRoundRadius(AndroidUtilities.dp(22.0f));
                            this.imagesByDays.put(keyAt, imageReceiver);
                        }
                    }
                }
            }
            int i7 = i4 + 1;
            this.daysInMonth = YearMonth.of(i3, i7).lengthOfMonth();
            Calendar instance = Calendar.getInstance();
            instance.set(i3, i4, 0);
            this.startDayOfWeek = (instance.get(7) + 6) % 7;
            this.startMonthTime = (int) (instance.getTimeInMillis() / 1000);
            int i8 = this.daysInMonth + this.startDayOfWeek;
            this.cellCount = ((int) (((float) i8) / 7.0f)) + (i8 % 7 == 0 ? 0 : 1);
            instance.set(i3, i7, 0);
            this.titleView.setText(LocaleController.formatYearMont(instance.getTimeInMillis() / 1000, true));
            CalendarActivity.this.updateRowSelections(this, false);
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int i, int i2) {
            super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp((float) ((this.cellCount * 52) + 44)), NUM));
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            float f;
            float f2;
            int i;
            float f3;
            float f4;
            float f5;
            float f6;
            float f7;
            PeriodDay periodDay;
            Canvas canvas2 = canvas;
            super.onDraw(canvas);
            int i2 = this.startDayOfWeek;
            float measuredWidth = ((float) getMeasuredWidth()) / 7.0f;
            float dp = (float) AndroidUtilities.dp(52.0f);
            float f8 = 44.0f;
            int dp2 = AndroidUtilities.dp(44.0f);
            for (int i3 = 0; ((double) i3) < Math.ceil((double) (((float) (this.startDayOfWeek + this.daysInMonth)) / 7.0f)); i3++) {
                float dp3 = (((float) i3) * dp) + (dp / 2.0f) + ((float) AndroidUtilities.dp(44.0f));
                RowAnimationValue rowAnimationValue = this.rowSelectionPos.get(i3);
                if (rowAnimationValue != null) {
                    CalendarActivity.this.selectPaint.setColor(Theme.getColor("chat_messagePanelVoiceBackground"));
                    CalendarActivity.this.selectPaint.setAlpha((int) (rowAnimationValue.alpha * 40.8f));
                    RectF rectF = AndroidUtilities.rectTmp;
                    float f9 = ((float) dp2) / 2.0f;
                    rectF.set(rowAnimationValue.startX - f9, dp3 - f9, rowAnimationValue.endX + f9, dp3 + f9);
                    float dp4 = (float) AndroidUtilities.dp(32.0f);
                    canvas2.drawRoundRect(rectF, dp4, dp4, CalendarActivity.this.selectPaint);
                }
            }
            int i4 = i2;
            int i5 = 0;
            int i6 = 0;
            while (i6 < this.daysInMonth) {
                float var_ = (((float) i4) * measuredWidth) + (measuredWidth / 2.0f);
                float dp5 = (((float) i5) * dp) + (dp / 2.0f) + ((float) AndroidUtilities.dp(f8));
                int currentTimeMillis = (int) (System.currentTimeMillis() / 1000);
                SparseArray<PeriodDay> sparseArray = this.messagesByDays;
                PeriodDay periodDay2 = null;
                if (sparseArray != null) {
                    periodDay2 = sparseArray.get(i6, (Object) null);
                }
                int i7 = i6 + 1;
                if (currentTimeMillis < this.startMonthTime + (i7 * 86400) || (CalendarActivity.this.minDate > 0 && CalendarActivity.this.minDate > this.startMonthTime + ((i6 + 2) * 86400))) {
                    f = measuredWidth;
                    f2 = dp;
                    float var_ = var_;
                    i = i5;
                    int alpha = CalendarActivity.this.textPaint.getAlpha();
                    CalendarActivity.this.textPaint.setAlpha((int) (((float) alpha) * 0.3f));
                    canvas2.drawText(Integer.toString(i7), var_, ((float) AndroidUtilities.dp(5.0f)) + dp5, CalendarActivity.this.textPaint);
                    CalendarActivity.this.textPaint.setAlpha(alpha);
                } else if (periodDay2 == null || !periodDay2.hasImage) {
                    PeriodDay periodDay3 = periodDay2;
                    f = measuredWidth;
                    f2 = dp;
                    float var_ = var_;
                    i = i5;
                    float var_ = dp5;
                    if (periodDay3 == null || periodDay3.selectStartEndProgress < 0.01f) {
                        canvas2.drawText(Integer.toString(i7), var_, ((float) AndroidUtilities.dp(5.0f)) + var_, CalendarActivity.this.textPaint);
                    } else {
                        CalendarActivity.this.selectPaint.setColor(Theme.getColor("windowBackgroundWhite"));
                        CalendarActivity.this.selectPaint.setAlpha((int) (periodDay3.selectStartEndProgress * 255.0f));
                        canvas2.drawCircle(var_, var_, ((float) AndroidUtilities.dp(44.0f)) / 2.0f, CalendarActivity.this.selectPaint);
                        CalendarActivity.this.selectOutlinePaint.setColor(Theme.getColor("chat_messagePanelVoiceBackground"));
                        RectF rectF2 = AndroidUtilities.rectTmp;
                        rectF2.set(var_ - (((float) AndroidUtilities.dp(44.0f)) / 2.0f), var_ - (((float) AndroidUtilities.dp(44.0f)) / 2.0f), (((float) AndroidUtilities.dp(44.0f)) / 2.0f) + var_, (((float) AndroidUtilities.dp(44.0f)) / 2.0f) + var_);
                        float var_ = var_;
                        canvas.drawArc(rectF2, -90.0f, 360.0f * periodDay3.selectStartEndProgress, false, CalendarActivity.this.selectOutlinePaint);
                        CalendarActivity.this.selectPaint.setColor(Theme.getColor("chat_messagePanelVoiceBackground"));
                        CalendarActivity.this.selectPaint.setAlpha((int) (periodDay3.selectStartEndProgress * 255.0f));
                        canvas2.drawCircle(var_, var_, ((float) (AndroidUtilities.dp(44.0f) - ((int) (((float) AndroidUtilities.dp(7.0f)) * periodDay3.selectStartEndProgress)))) / 2.0f, CalendarActivity.this.selectPaint);
                        float var_ = periodDay3.selectStartEndProgress;
                        if (var_ != 1.0f) {
                            int alpha2 = CalendarActivity.this.textPaint.getAlpha();
                            CalendarActivity.this.textPaint.setAlpha((int) (((float) alpha2) * (1.0f - var_)));
                            canvas2.drawText(Integer.toString(i7), var_, ((float) AndroidUtilities.dp(5.0f)) + var_, CalendarActivity.this.textPaint);
                            CalendarActivity.this.textPaint.setAlpha(alpha2);
                            int alpha3 = CalendarActivity.this.textPaint.getAlpha();
                            CalendarActivity.this.activeTextPaint.setAlpha((int) (((float) alpha3) * var_));
                            canvas2.drawText(Integer.toString(i7), var_, ((float) AndroidUtilities.dp(5.0f)) + var_, CalendarActivity.this.activeTextPaint);
                            CalendarActivity.this.activeTextPaint.setAlpha(alpha3);
                        } else {
                            canvas2.drawText(Integer.toString(i7), var_, ((float) AndroidUtilities.dp(5.0f)) + var_, CalendarActivity.this.activeTextPaint);
                        }
                    }
                } else {
                    if (this.imagesByDays.get(i6) != null) {
                        if (CalendarActivity.this.checkEnterItems && !periodDay2.wasDrawn) {
                            periodDay2.enterAlpha = 0.0f;
                            periodDay2.startEnterDelay = Math.max(0.0f, ((getY() + dp5) / ((float) CalendarActivity.this.listView.getMeasuredHeight())) * 150.0f);
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
                        f4 = periodDay2.enterAlpha;
                        if (f4 != 1.0f) {
                            canvas.save();
                            float var_ = (0.2f * f4) + 0.8f;
                            canvas2.scale(var_, var_, var_, dp5);
                        }
                        int dp6 = (int) (((float) AndroidUtilities.dp(7.0f)) * periodDay2.selectProgress);
                        if (periodDay2.selectStartEndProgress >= 0.01f) {
                            CalendarActivity.this.selectPaint.setColor(Theme.getColor("windowBackgroundWhite"));
                            CalendarActivity.this.selectPaint.setAlpha((int) (periodDay2.selectStartEndProgress * 255.0f));
                            canvas2.drawCircle(var_, dp5, ((float) AndroidUtilities.dp(44.0f)) / 2.0f, CalendarActivity.this.selectPaint);
                            CalendarActivity.this.selectOutlinePaint.setColor(Theme.getColor("chat_messagePanelVoiceBackground"));
                            RectF rectF3 = AndroidUtilities.rectTmp;
                            int i8 = i5;
                            f = measuredWidth;
                            float var_ = var_;
                            rectF3.set(var_ - (((float) AndroidUtilities.dp(44.0f)) / 2.0f), dp5 - (((float) AndroidUtilities.dp(44.0f)) / 2.0f), (((float) AndroidUtilities.dp(44.0f)) / 2.0f) + var_, (((float) AndroidUtilities.dp(44.0f)) / 2.0f) + dp5);
                            float var_ = 360.0f * periodDay2.selectStartEndProgress;
                            periodDay = periodDay2;
                            float var_ = dp5;
                            float var_ = var_;
                            f7 = var_;
                            i = i8;
                            f2 = dp;
                            f3 = var_;
                            canvas.drawArc(rectF3, -90.0f, var_, false, CalendarActivity.this.selectOutlinePaint);
                        } else {
                            periodDay = periodDay2;
                            f7 = dp5;
                            f = measuredWidth;
                            f2 = dp;
                            f3 = var_;
                            i = i5;
                        }
                        PeriodDay periodDay4 = periodDay;
                        this.imagesByDays.get(i6).setAlpha(periodDay4.enterAlpha);
                        f5 = f7;
                        this.imagesByDays.get(i6).setImageCoords(f3 - (((float) (AndroidUtilities.dp(44.0f) - dp6)) / 2.0f), f5 - (((float) (AndroidUtilities.dp(44.0f) - dp6)) / 2.0f), (float) (AndroidUtilities.dp(44.0f) - dp6), (float) (AndroidUtilities.dp(44.0f) - dp6));
                        this.imagesByDays.get(i6).draw(canvas2);
                        CalendarActivity.this.blackoutPaint.setColor(ColorUtils.setAlphaComponent(-16777216, (int) (periodDay4.enterAlpha * 80.0f)));
                        canvas2.drawCircle(f3, f5, ((float) (AndroidUtilities.dp(44.0f) - dp6)) / 2.0f, CalendarActivity.this.blackoutPaint);
                        periodDay4.wasDrawn = true;
                        f6 = 1.0f;
                        if (f4 != 1.0f) {
                            canvas.restore();
                        }
                    } else {
                        f = measuredWidth;
                        f2 = dp;
                        f6 = 1.0f;
                        f3 = var_;
                        i = i5;
                        f5 = dp5;
                        f4 = 1.0f;
                    }
                    if (f4 != f6) {
                        int alpha4 = CalendarActivity.this.textPaint.getAlpha();
                        CalendarActivity.this.textPaint.setAlpha((int) (((float) alpha4) * (f6 - f4)));
                        canvas2.drawText(Integer.toString(i7), f3, f5 + ((float) AndroidUtilities.dp(5.0f)), CalendarActivity.this.textPaint);
                        CalendarActivity.this.textPaint.setAlpha(alpha4);
                        int alpha5 = CalendarActivity.this.textPaint.getAlpha();
                        CalendarActivity.this.activeTextPaint.setAlpha((int) (((float) alpha5) * f4));
                        canvas2.drawText(Integer.toString(i7), f3, f5 + ((float) AndroidUtilities.dp(5.0f)), CalendarActivity.this.activeTextPaint);
                        CalendarActivity.this.activeTextPaint.setAlpha(alpha5);
                    } else {
                        canvas2.drawText(Integer.toString(i7), f3, f5 + ((float) AndroidUtilities.dp(5.0f)), CalendarActivity.this.activeTextPaint);
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

        /* access modifiers changed from: protected */
        public void onAttachedToWindow() {
            super.onAttachedToWindow();
            if (this.imagesByDays != null) {
                for (int i = 0; i < this.imagesByDays.size(); i++) {
                    this.imagesByDays.valueAt(i).onAttachedToWindow();
                }
            }
        }

        /* access modifiers changed from: protected */
        public void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            if (this.imagesByDays != null) {
                for (int i = 0; i < this.imagesByDays.size(); i++) {
                    this.imagesByDays.valueAt(i).onDetachedFromWindow();
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void updateTitle() {
        int i;
        String str;
        HintView hintView;
        if (!this.canClearHistory) {
            this.actionBar.setTitle(LocaleController.getString("Calendar", NUM));
            this.backDrawable.setRotation(0.0f, true);
            return;
        }
        int i2 = this.dateSelectedStart;
        int i3 = this.dateSelectedEnd;
        if (i2 == i3 && i2 == 0) {
            i = 0;
        } else {
            i = (Math.abs(i2 - i3) / 86400) + 1;
        }
        boolean z = this.lastInSelectionMode;
        int i4 = this.lastDaysSelected;
        if (i != i4 || z != this.inSelectionMode) {
            boolean z2 = i4 > i;
            this.lastDaysSelected = i;
            boolean z3 = this.inSelectionMode;
            this.lastInSelectionMode = z3;
            float f = 1.0f;
            if (i > 0) {
                str = LocaleController.formatPluralString("Days", i);
                this.backDrawable.setRotation(1.0f, true);
            } else if (z3) {
                str = LocaleController.getString("SelectDays", NUM);
                this.backDrawable.setRotation(1.0f, true);
            } else {
                str = LocaleController.getString("Calendar", NUM);
                this.backDrawable.setRotation(0.0f, true);
            }
            if (i > 1) {
                this.removeDaysButton.setText(LocaleController.formatString("ClearHistoryForTheseDays", NUM, new Object[0]));
            } else if (i > 0 || this.inSelectionMode) {
                this.removeDaysButton.setText(LocaleController.formatString("ClearHistoryForThisDay", NUM, new Object[0]));
            }
            this.actionBar.setTitleAnimated(str, z2, 150);
            if ((!this.inSelectionMode || i > 0) && (hintView = this.selectDaysHint) != null) {
                hintView.hide();
            }
            if (i > 0 || this.inSelectionMode) {
                if (this.removeDaysButton.getVisibility() == 8) {
                    this.removeDaysButton.setAlpha(0.0f);
                    this.removeDaysButton.setTranslationY((float) (-AndroidUtilities.dp(20.0f)));
                }
                this.removeDaysButton.setVisibility(0);
                this.selectDaysButton.animate().setListener((Animator.AnimatorListener) null).cancel();
                this.removeDaysButton.animate().setListener((Animator.AnimatorListener) null).cancel();
                this.selectDaysButton.animate().alpha(0.0f).translationY((float) AndroidUtilities.dp(20.0f)).setDuration(150).setListener(new HideViewAfterAnimation(this.selectDaysButton)).start();
                ViewPropertyAnimator animate = this.removeDaysButton.animate();
                if (i == 0) {
                    f = 0.5f;
                }
                animate.alpha(f).translationY(0.0f).start();
                this.selectDaysButton.setEnabled(false);
                this.removeDaysButton.setEnabled(true);
                return;
            }
            if (this.selectDaysButton.getVisibility() == 8) {
                this.selectDaysButton.setAlpha(0.0f);
                this.selectDaysButton.setTranslationY((float) AndroidUtilities.dp(20.0f));
            }
            this.selectDaysButton.setVisibility(0);
            this.selectDaysButton.animate().setListener((Animator.AnimatorListener) null).cancel();
            this.removeDaysButton.animate().setListener((Animator.AnimatorListener) null).cancel();
            this.selectDaysButton.animate().alpha(1.0f).translationY(0.0f).start();
            this.removeDaysButton.animate().alpha(0.0f).translationY((float) (-AndroidUtilities.dp(20.0f))).setDuration(150).setListener(new HideViewAfterAnimation(this.removeDaysButton)).start();
            this.selectDaysButton.setEnabled(true);
            this.removeDaysButton.setEnabled(false);
        }
    }

    public void setCallback(Callback callback2) {
        this.callback = callback2;
    }

    private class PeriodDay {
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

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        AnonymousClass8 r8 = new ThemeDescription.ThemeDescriptionDelegate() {
            public /* synthetic */ void onAnimationProgress(float f) {
                ThemeDescription.ThemeDescriptionDelegate.CC.$default$onAnimationProgress(this, f);
            }

            public void didSetColor() {
                CalendarActivity.this.updateColors();
            }
        };
        new ArrayList();
        AnonymousClass8 r6 = r8;
        new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r6, "windowBackgroundWhite");
        new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r6, "windowBackgroundWhiteBlackText");
        new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, r6, "listSelectorSDK21");
        return super.getThemeDescriptions();
    }

    /* access modifiers changed from: protected */
    public void onTransitionAnimationStart(boolean z, boolean z2) {
        super.onTransitionAnimationStart(z, z2);
        this.isOpened = true;
    }

    /* access modifiers changed from: protected */
    public void onTransitionAnimationProgress(boolean z, float f) {
        super.onTransitionAnimationProgress(z, f);
        View view = this.blurredView;
        if (view != null && view.getVisibility() == 0) {
            if (z) {
                this.blurredView.setAlpha(1.0f - f);
            } else {
                this.blurredView.setAlpha(f);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onTransitionAnimationEnd(boolean z, boolean z2) {
        View view;
        if (z && (view = this.blurredView) != null && view.getVisibility() == 0) {
            this.blurredView.setVisibility(8);
            this.blurredView.setBackground((Drawable) null);
        }
    }

    /* access modifiers changed from: private */
    public void animateSelection() {
        ValueAnimator duration = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f}).setDuration(300);
        duration.setInterpolator(CubicBezierInterpolator.DEFAULT);
        duration.addUpdateListener(new CalendarActivity$$ExternalSyntheticLambda0(this));
        duration.addListener(new AnimatorListenerAdapter() {
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

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$animateSelection$4(ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        for (int i = 0; i < this.listView.getChildCount(); i++) {
            ((MonthView) this.listView.getChildAt(i)).setSelectionValue(floatValue);
        }
    }

    /* access modifiers changed from: private */
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
                PeriodDay periodDay = monthView.messagesByDays.get(i8, (Object) null);
                if (periodDay == null || (i3 = periodDay.date) < this.dateSelectedStart || i3 > this.dateSelectedEnd) {
                    i2 = i6;
                    i = i7;
                } else {
                    if (i6 == -1) {
                        i6 = i4;
                    }
                    i = i4;
                    i2 = i6;
                }
                i4++;
                if (i4 >= 7) {
                    if (i2 == -1 || i == -1) {
                        monthView.animateRow(i5, 0, 0, false, z);
                    } else {
                        monthView.animateRow(i5, i2, i, true, z);
                    }
                    i5++;
                    i4 = 0;
                    i6 = -1;
                    i7 = -1;
                } else {
                    i6 = i2;
                    i7 = i;
                }
            }
            if (i6 == -1 || i7 == -1) {
                monthView.animateRow(i5, 0, 0, false, z);
            } else {
                monthView.animateRow(i5, i6, i7, true, z);
            }
        }
    }

    private static final class RowAnimationValue {
        float alpha;
        float endX;
        float startX;

        RowAnimationValue(float f, float f2) {
            this.startX = f;
            this.endX = f2;
        }
    }

    /* access modifiers changed from: private */
    public void prepareBlurBitmap() {
        if (this.blurredView != null) {
            int measuredWidth = (int) (((float) this.parentLayout.getMeasuredWidth()) / 6.0f);
            int measuredHeight = (int) (((float) this.parentLayout.getMeasuredHeight()) / 6.0f);
            Bitmap createBitmap = Bitmap.createBitmap(measuredWidth, measuredHeight, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(createBitmap);
            canvas.scale(0.16666667f, 0.16666667f);
            this.parentLayout.draw(canvas);
            Utilities.stackBlurBitmap(createBitmap, Math.max(7, Math.max(measuredWidth, measuredHeight) / 180));
            this.blurredView.setBackground(new BitmapDrawable(createBitmap));
            this.blurredView.setAlpha(0.0f);
            this.blurredView.setVisibility(0);
        }
    }

    public boolean onBackPressed() {
        if (!this.inSelectionMode) {
            return super.onBackPressed();
        }
        this.inSelectionMode = false;
        this.dateSelectedEnd = 0;
        this.dateSelectedStart = 0;
        updateTitle();
        animateSelection();
        return false;
    }

    public boolean isLightStatusBar() {
        if (ColorUtils.calculateLuminance(Theme.getColor("windowBackgroundWhite", (boolean[]) null, true)) > 0.699999988079071d) {
            return true;
        }
        return false;
    }
}
