package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
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
import org.telegram.tgnet.TLRPC;
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
    public static final int TYPE_CHAT_ACTIVITY = 0;
    public static final int TYPE_MEDIA_CALENDAR = 1;
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

    public CalendarActivity(Bundle args, int photosVideosTypeFilter2, int selectedDate) {
        super(args);
        this.photosVideosTypeFilter = photosVideosTypeFilter2;
        if (selectedDate != 0) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(((long) selectedDate) * 1000);
            this.selectedYear = calendar.get(1);
            this.selectedMonth = calendar.get(2);
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
            public void onLayout(boolean changed, int left, int top, int right, int bottom) {
                super.onLayout(changed, left, top, right, bottom);
                int size = (getMeasuredHeight() + getMeasuredWidth()) << 16;
                if (this.lastSize != size) {
                    this.lastSize = size;
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
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                CalendarActivity.this.checkLoadNext();
            }
        });
        boolean showBottomPanel = this.calendarType == 0 && this.canClearHistory;
        this.contentView.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f, 0, 0.0f, 36.0f, 0.0f, showBottomPanel ? 48.0f : 0.0f));
        final String[] daysOfWeek = {LocaleController.getString("CalendarWeekNameShortMonday", NUM), LocaleController.getString("CalendarWeekNameShortTuesday", NUM), LocaleController.getString("CalendarWeekNameShortWednesday", NUM), LocaleController.getString("CalendarWeekNameShortThursday", NUM), LocaleController.getString("CalendarWeekNameShortFriday", NUM), LocaleController.getString("CalendarWeekNameShortSaturday", NUM), LocaleController.getString("CalendarWeekNameShortSunday", NUM)};
        final Drawable headerShadowDrawable = ContextCompat.getDrawable(context2, NUM).mutate();
        this.contentView.addView(new View(context2) {
            /* access modifiers changed from: protected */
            public void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                float xStep = ((float) getMeasuredWidth()) / 7.0f;
                for (int i = 0; i < 7; i++) {
                    canvas.drawText(daysOfWeek[i], (((float) i) * xStep) + (xStep / 2.0f), ((float) AndroidUtilities.dp(5.0f)) + (((float) (getMeasuredHeight() - AndroidUtilities.dp(2.0f))) / 2.0f), CalendarActivity.this.textPaint2);
                }
                headerShadowDrawable.setBounds(0, getMeasuredHeight() - AndroidUtilities.dp(3.0f), getMeasuredWidth(), getMeasuredHeight());
                headerShadowDrawable.draw(canvas);
            }
        }, LayoutHelper.createFrame(-1, 38.0f, 0, 0.0f, 0.0f, 0.0f, 0.0f));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id != -1) {
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
        this.backDrawable = new BackDrawable(false);
        this.actionBar.setBackButtonDrawable(this.backDrawable);
        this.backDrawable.setRotation(0.0f, false);
        loadNext();
        updateColors();
        this.activeTextPaint.setColor(-1);
        if (showBottomPanel) {
            AnonymousClass6 r9 = new FrameLayout(context2) {
                public void onDraw(Canvas canvas) {
                    canvas.drawRect(0.0f, 0.0f, (float) getMeasuredWidth(), (float) AndroidUtilities.getShadowHeight(), Theme.dividerPaint);
                }
            };
            this.bottomBar = r9;
            r9.setWillNotDraw(false);
            this.bottomBar.setPadding(0, AndroidUtilities.getShadowHeight(), 0, 0);
            this.bottomBar.setClipChildren(false);
            TextView textView = new TextView(context2);
            this.selectDaysButton = textView;
            textView.setGravity(17);
            this.selectDaysButton.setTextSize(1, 15.0f);
            this.selectDaysButton.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.selectDaysButton.setOnClickListener(new CalendarActivity$$ExternalSyntheticLambda1(this));
            this.selectDaysButton.setText(LocaleController.getString("SelectDays", NUM));
            this.selectDaysButton.setAllCaps(true);
            this.bottomBar.addView(this.selectDaysButton, LayoutHelper.createFrame(-1, -1.0f, 0, 0.0f, 0.0f, 0.0f, 0.0f));
            TextView textView2 = new TextView(context2);
            this.removeDaysButton = textView2;
            textView2.setGravity(17);
            this.removeDaysButton.setTextSize(1, 15.0f);
            this.removeDaysButton.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.removeDaysButton.setOnClickListener(new CalendarActivity$$ExternalSyntheticLambda2(this));
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

    /* renamed from: lambda$createView$0$org-telegram-ui-CalendarActivity  reason: not valid java name */
    public /* synthetic */ void m1478lambda$createView$0$orgtelegramuiCalendarActivity(View view) {
        this.inSelectionMode = true;
        updateTitle();
    }

    /* renamed from: lambda$createView$1$org-telegram-ui-CalendarActivity  reason: not valid java name */
    public /* synthetic */ void m1479lambda$createView$1$orgtelegramuiCalendarActivity(View view) {
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
            public void run(boolean forAll) {
                CalendarActivity.this.finishFragment();
                if (CalendarActivity.this.parentLayout.fragmentsStack.size() >= 2) {
                    BaseFragment fragment = CalendarActivity.this.parentLayout.fragmentsStack.get(CalendarActivity.this.parentLayout.fragmentsStack.size() - 2);
                    if (fragment instanceof ChatActivity) {
                        ((ChatActivity) fragment).deleteHistory(CalendarActivity.this.dateSelectedStart, CalendarActivity.this.dateSelectedEnd + 86400, forAll);
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
            TLRPC.TL_messages_getSearchResultsCalendar req = new TLRPC.TL_messages_getSearchResultsCalendar();
            int i = this.photosVideosTypeFilter;
            if (i == 1) {
                req.filter = new TLRPC.TL_inputMessagesFilterPhotos();
            } else if (i == 2) {
                req.filter = new TLRPC.TL_inputMessagesFilterVideo();
            } else {
                req.filter = new TLRPC.TL_inputMessagesFilterPhotoVideo();
            }
            req.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(this.dialogId);
            req.offset_id = this.lastId;
            Calendar calendar = Calendar.getInstance();
            this.listView.setItemAnimator((RecyclerView.ItemAnimator) null);
            getConnectionsManager().sendRequest(req, new CalendarActivity$$ExternalSyntheticLambda4(this, calendar));
        }
    }

    /* renamed from: lambda$loadNext$3$org-telegram-ui-CalendarActivity  reason: not valid java name */
    public /* synthetic */ void m1481lambda$loadNext$3$orgtelegramuiCalendarActivity(Calendar calendar, TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new CalendarActivity$$ExternalSyntheticLambda3(this, error, response, calendar));
    }

    /* renamed from: lambda$loadNext$2$org-telegram-ui-CalendarActivity  reason: not valid java name */
    public /* synthetic */ void m1480lambda$loadNext$2$orgtelegramuiCalendarActivity(TLRPC.TL_error error, TLObject response, Calendar calendar) {
        int i;
        int i2;
        Calendar calendar2 = calendar;
        if (error == null) {
            TLRPC.TL_messages_searchResultsCalendar res = (TLRPC.TL_messages_searchResultsCalendar) response;
            int i3 = 0;
            while (true) {
                i = 5;
                i2 = 2;
                if (i3 >= res.periods.size()) {
                    break;
                }
                calendar2.setTimeInMillis(((long) res.periods.get(i3).date) * 1000);
                int month = (calendar2.get(1) * 100) + calendar2.get(2);
                SparseArray<PeriodDay> messagesByDays = this.messagesByYearMounth.get(month);
                if (messagesByDays == null) {
                    messagesByDays = new SparseArray<>();
                    this.messagesByYearMounth.put(month, messagesByDays);
                }
                PeriodDay periodDay = new PeriodDay();
                periodDay.messageObject = new MessageObject(this.currentAccount, res.messages.get(i3), false, false);
                periodDay.date = (int) (calendar.getTimeInMillis() / 1000);
                int i4 = this.startOffset + res.periods.get(i3).count;
                this.startOffset = i4;
                periodDay.startOffset = i4;
                int index = calendar2.get(5) - 1;
                if (messagesByDays.get(index, (Object) null) == null || !messagesByDays.get(index, (Object) null).hasImage) {
                    messagesByDays.put(index, periodDay);
                }
                int i5 = this.minMontYear;
                if (month < i5 || i5 == 0) {
                    this.minMontYear = month;
                }
                i3++;
            }
            int maxDate = (int) (System.currentTimeMillis() / 1000);
            this.minDate = res.min_date;
            int date = res.min_date;
            while (date < maxDate) {
                calendar2.setTimeInMillis(((long) date) * 1000);
                calendar2.set(11, 0);
                calendar2.set(12, 0);
                calendar2.set(13, 0);
                calendar2.set(14, 0);
                int month2 = (calendar2.get(1) * 100) + calendar2.get(i2);
                SparseArray<PeriodDay> messagesByDays2 = this.messagesByYearMounth.get(month2);
                if (messagesByDays2 == null) {
                    messagesByDays2 = new SparseArray<>();
                    this.messagesByYearMounth.put(month2, messagesByDays2);
                }
                int index2 = calendar2.get(i) - 1;
                if (messagesByDays2.get(index2, (Object) null) == null) {
                    PeriodDay periodDay2 = new PeriodDay();
                    periodDay2.hasImage = false;
                    periodDay2.date = (int) (calendar.getTimeInMillis() / 1000);
                    messagesByDays2.put(index2, periodDay2);
                }
                date += 86400;
                i = 5;
                i2 = 2;
            }
            this.loading = false;
            if (!res.messages.isEmpty()) {
                this.lastId = res.messages.get(res.messages.size() - 1).id;
                this.endReached = false;
                checkLoadNext();
            } else {
                this.endReached = true;
            }
            if (this.isOpened) {
                this.checkEnterItems = true;
            }
            this.listView.invalidate();
            int newMonthCount = ((int) (((calendar.getTimeInMillis() / 1000) - ((long) res.min_date)) / 2629800)) + 1;
            this.adapter.notifyItemRangeChanged(0, this.monthCount);
            int i6 = this.monthCount;
            if (newMonthCount > i6) {
                this.adapter.notifyItemRangeInserted(i6 + 1, newMonthCount);
                this.monthCount = newMonthCount;
            }
            if (this.endReached) {
                resumeDelayedFragmentAnimation();
            }
        }
    }

    /* access modifiers changed from: private */
    public void checkLoadNext() {
        int currentMonth;
        if (!this.loading && !this.endReached) {
            int listMinMonth = Integer.MAX_VALUE;
            for (int i = 0; i < this.listView.getChildCount(); i++) {
                View child = this.listView.getChildAt(i);
                if ((child instanceof MonthView) && (currentMonth = (((MonthView) child).currentYear * 100) + ((MonthView) child).currentMonthInYear) < listMinMonth) {
                    listMinMonth = currentMonth;
                }
            }
            int i2 = this.minMontYear;
            if (((i2 / 100) * 12) + (i2 % 100) + 3 >= ((listMinMonth / 100) * 12) + (listMinMonth % 100)) {
                loadNext();
            }
        }
    }

    private class CalendarAdapter extends RecyclerView.Adapter {
        private CalendarAdapter() {
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new RecyclerListView.Holder(new MonthView(parent.getContext()));
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            MonthView monthView = (MonthView) holder.itemView;
            int year = CalendarActivity.this.startFromYear - (position / 12);
            int month = CalendarActivity.this.startFromMonth - (position % 12);
            if (month < 0) {
                month += 12;
                year--;
            }
            monthView.setDate(year, month, CalendarActivity.this.messagesByYearMounth.get((year * 100) + month), monthView.currentYear == year && monthView.currentMonthInYear == month);
            monthView.startSelectionAnimation(CalendarActivity.this.dateSelectedStart, CalendarActivity.this.dateSelectedEnd);
            monthView.setSelectionValue(1.0f);
            CalendarActivity.this.updateRowSelections(monthView, false);
        }

        public long getItemId(int position) {
            return (((long) (CalendarActivity.this.startFromYear - (position / 12))) * 100) + ((long) (CalendarActivity.this.startFromMonth - (position % 12)));
        }

        public int getItemCount() {
            return CalendarActivity.this.monthCount;
        }
    }

    private class MonthView extends FrameLayout {
        boolean attached;
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

        public MonthView(final Context context) {
            super(context);
            boolean z = false;
            setWillNotDraw(false);
            this.titleView = new SimpleTextView(context);
            if (CalendarActivity.this.calendarType == 0 && CalendarActivity.this.canClearHistory) {
                this.titleView.setOnLongClickListener(new CalendarActivity$MonthView$$ExternalSyntheticLambda1(this));
                this.titleView.setOnClickListener(new View.OnClickListener(CalendarActivity.this) {
                    public void onClick(View view) {
                        if (MonthView.this.messagesByDays != null && CalendarActivity.this.inSelectionMode) {
                            int start = -1;
                            int end = -1;
                            for (int i = 0; i < MonthView.this.daysInMonth; i++) {
                                PeriodDay day = MonthView.this.messagesByDays.get(i, (Object) null);
                                if (day != null) {
                                    if (start == -1) {
                                        start = day.date;
                                    }
                                    end = day.date;
                                }
                            }
                            if (start >= 0 && end >= 0) {
                                int unused = CalendarActivity.this.dateSelectedStart = start;
                                int unused2 = CalendarActivity.this.dateSelectedEnd = end;
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
            GestureDetectorCompat gestureDetectorCompat = new GestureDetectorCompat(context, new GestureDetector.SimpleOnGestureListener(CalendarActivity.this) {
                public boolean onDown(MotionEvent e) {
                    return true;
                }

                public boolean onSingleTapUp(MotionEvent e) {
                    PeriodDay day;
                    if (!(CalendarActivity.this.calendarType != 1 || MonthView.this.messagesByDays == null || (day = getDayAtCoord(e.getX(), e.getY())) == null || day.messageObject == null || CalendarActivity.this.callback == null)) {
                        CalendarActivity.this.callback.onDateSelected(day.messageObject.getId(), day.startOffset);
                        CalendarActivity.this.finishFragment();
                    }
                    if (MonthView.this.messagesByDays != null) {
                        if (CalendarActivity.this.inSelectionMode) {
                            PeriodDay day2 = getDayAtCoord(e.getX(), e.getY());
                            if (day2 != null) {
                                if (CalendarActivity.this.selectionAnimator != null) {
                                    CalendarActivity.this.selectionAnimator.cancel();
                                    ValueAnimator unused = CalendarActivity.this.selectionAnimator = null;
                                }
                                if (CalendarActivity.this.dateSelectedStart == 0 && CalendarActivity.this.dateSelectedEnd == 0) {
                                    int unused2 = CalendarActivity.this.dateSelectedStart = CalendarActivity.this.dateSelectedEnd = day2.date;
                                } else if (CalendarActivity.this.dateSelectedStart == day2.date && CalendarActivity.this.dateSelectedEnd == day2.date) {
                                    int unused3 = CalendarActivity.this.dateSelectedStart = CalendarActivity.this.dateSelectedEnd = 0;
                                } else if (CalendarActivity.this.dateSelectedStart == day2.date) {
                                    int unused4 = CalendarActivity.this.dateSelectedStart = CalendarActivity.this.dateSelectedEnd;
                                } else if (CalendarActivity.this.dateSelectedEnd == day2.date) {
                                    int unused5 = CalendarActivity.this.dateSelectedEnd = CalendarActivity.this.dateSelectedStart;
                                } else if (CalendarActivity.this.dateSelectedStart != CalendarActivity.this.dateSelectedEnd) {
                                    int unused6 = CalendarActivity.this.dateSelectedStart = CalendarActivity.this.dateSelectedEnd = day2.date;
                                } else if (day2.date > CalendarActivity.this.dateSelectedEnd) {
                                    int unused7 = CalendarActivity.this.dateSelectedEnd = day2.date;
                                } else {
                                    int unused8 = CalendarActivity.this.dateSelectedStart = day2.date;
                                }
                                CalendarActivity.this.updateTitle();
                                CalendarActivity.this.animateSelection();
                            }
                        } else {
                            PeriodDay day3 = getDayAtCoord(e.getX(), e.getY());
                            if (CalendarActivity.this.parentLayout.fragmentsStack.size() >= 2) {
                                BaseFragment fragment = CalendarActivity.this.parentLayout.fragmentsStack.get(CalendarActivity.this.parentLayout.fragmentsStack.size() - 2);
                                if (fragment instanceof ChatActivity) {
                                    CalendarActivity.this.finishFragment();
                                    ((ChatActivity) fragment).jumpToDate(day3.date);
                                }
                            }
                        }
                    }
                    return false;
                }

                private PeriodDay getDayAtCoord(float pressedX, float pressedY) {
                    PeriodDay day;
                    if (MonthView.this.messagesByDays == null) {
                        return null;
                    }
                    int currentCell = 0;
                    int currentColumn = MonthView.this.startDayOfWeek;
                    float xStep = ((float) MonthView.this.getMeasuredWidth()) / 7.0f;
                    float yStep = (float) AndroidUtilities.dp(52.0f);
                    int hrad = AndroidUtilities.dp(44.0f) / 2;
                    for (int i = 0; i < MonthView.this.daysInMonth; i++) {
                        float cx = (((float) currentColumn) * xStep) + (xStep / 2.0f);
                        float cy = (((float) currentCell) * yStep) + (yStep / 2.0f) + ((float) AndroidUtilities.dp(44.0f));
                        if (pressedX >= cx - ((float) hrad) && pressedX <= ((float) hrad) + cx && pressedY >= cy - ((float) hrad) && pressedY <= ((float) hrad) + cy && (day = MonthView.this.messagesByDays.get(i, (Object) null)) != null) {
                            return day;
                        }
                        currentColumn++;
                        if (currentColumn >= 7) {
                            currentColumn = 0;
                            currentCell++;
                        }
                    }
                    return null;
                }

                public void onLongPress(MotionEvent e) {
                    PeriodDay periodDay;
                    super.onLongPress(e);
                    if (CalendarActivity.this.calendarType == 0 && (periodDay = getDayAtCoord(e.getX(), e.getY())) != null) {
                        MonthView.this.performHapticFeedback(0);
                        Bundle bundle = new Bundle();
                        if (CalendarActivity.this.dialogId > 0) {
                            bundle.putLong("user_id", CalendarActivity.this.dialogId);
                        } else {
                            bundle.putLong("chat_id", -CalendarActivity.this.dialogId);
                        }
                        bundle.putInt("start_from_date", periodDay.date);
                        bundle.putBoolean("need_remove_previous_same_chat_activity", false);
                        ChatActivity chatActivity = new ChatActivity(bundle);
                        ActionBarPopupWindow.ActionBarPopupWindowLayout previewMenu = new ActionBarPopupWindow.ActionBarPopupWindowLayout(CalendarActivity.this.getParentActivity(), NUM, CalendarActivity.this.getResourceProvider());
                        previewMenu.setBackgroundColor(CalendarActivity.this.getThemedColor("actionBarDefaultSubmenuBackground"));
                        ActionBarMenuSubItem cellJump = new ActionBarMenuSubItem(CalendarActivity.this.getParentActivity(), true, false);
                        cellJump.setTextAndIcon(LocaleController.getString("JumpToDate", NUM), NUM);
                        cellJump.setMinimumWidth(160);
                        cellJump.setOnClickListener(new CalendarActivity$MonthView$2$$ExternalSyntheticLambda2(this, periodDay));
                        previewMenu.addView(cellJump);
                        if (CalendarActivity.this.canClearHistory) {
                            ActionBarMenuSubItem cellSelect = new ActionBarMenuSubItem(CalendarActivity.this.getParentActivity(), false, false);
                            cellSelect.setTextAndIcon(LocaleController.getString("SelectThisDay", NUM), NUM);
                            cellSelect.setMinimumWidth(160);
                            cellSelect.setOnClickListener(new CalendarActivity$MonthView$2$$ExternalSyntheticLambda3(this, periodDay));
                            previewMenu.addView(cellSelect);
                            ActionBarMenuSubItem cellDelete = new ActionBarMenuSubItem(CalendarActivity.this.getParentActivity(), false, true);
                            cellDelete.setTextAndIcon(LocaleController.getString("ClearHistory", NUM), NUM);
                            cellDelete.setMinimumWidth(160);
                            cellDelete.setOnClickListener(new CalendarActivity$MonthView$2$$ExternalSyntheticLambda0(this));
                            previewMenu.addView(cellDelete);
                        }
                        previewMenu.setFitItems(true);
                        View unused = CalendarActivity.this.blurredView = new View(context) {
                            public void setAlpha(float alpha) {
                                super.setAlpha(alpha);
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
                        CalendarActivity.this.presentFragmentAsPreviewWithMenu(chatActivity, previewMenu);
                    }
                }

                /* renamed from: lambda$onLongPress$1$org-telegram-ui-CalendarActivity$MonthView$2  reason: not valid java name */
                public /* synthetic */ void m1485x44edvar_e(PeriodDay periodDay, View view) {
                    if (CalendarActivity.this.parentLayout.fragmentsStack.size() >= 3) {
                        BaseFragment fragment = CalendarActivity.this.parentLayout.fragmentsStack.get(CalendarActivity.this.parentLayout.fragmentsStack.size() - 3);
                        if (fragment instanceof ChatActivity) {
                            AndroidUtilities.runOnUIThread(new CalendarActivity$MonthView$2$$ExternalSyntheticLambda4(this, fragment, periodDay), 300);
                        }
                    }
                    CalendarActivity.this.finishPreviewFragment();
                }

                /* renamed from: lambda$onLongPress$0$org-telegram-ui-CalendarActivity$MonthView$2  reason: not valid java name */
                public /* synthetic */ void m1484x8b76627f(BaseFragment fragment, PeriodDay periodDay) {
                    CalendarActivity.this.finishFragment();
                    ((ChatActivity) fragment).jumpToDate(periodDay.date);
                }

                /* renamed from: lambda$onLongPress$2$org-telegram-ui-CalendarActivity$MonthView$2  reason: not valid java name */
                public /* synthetic */ void m1486xfe657dbd(PeriodDay periodDay, View view) {
                    int unused = CalendarActivity.this.dateSelectedStart = CalendarActivity.this.dateSelectedEnd = periodDay.date;
                    boolean unused2 = CalendarActivity.this.inSelectionMode = true;
                    CalendarActivity.this.updateTitle();
                    CalendarActivity.this.animateSelection();
                    CalendarActivity.this.finishPreviewFragment();
                }

                /* renamed from: lambda$onLongPress$3$org-telegram-ui-CalendarActivity$MonthView$2  reason: not valid java name */
                public /* synthetic */ void m1487xb7dd0b5c(View view) {
                    if (CalendarActivity.this.parentLayout.fragmentsStack.size() >= 3) {
                        final BaseFragment fragment = CalendarActivity.this.parentLayout.fragmentsStack.get(CalendarActivity.this.parentLayout.fragmentsStack.size() - 3);
                        if (fragment instanceof ChatActivity) {
                            AlertsCreator.createClearDaysDialogAlert(CalendarActivity.this, 1, CalendarActivity.this.getMessagesController().getUser(Long.valueOf(CalendarActivity.this.dialogId)), new MessagesStorage.BooleanCallback() {
                                public void run(boolean forAll) {
                                    CalendarActivity.this.finishFragment();
                                    ((ChatActivity) fragment).deleteHistory(CalendarActivity.this.dateSelectedStart, CalendarActivity.this.dateSelectedEnd + 86400, forAll);
                                }
                            }, (Theme.ResourcesProvider) null);
                        }
                    }
                    CalendarActivity.this.finishPreviewFragment();
                }

                /* renamed from: lambda$onLongPress$4$org-telegram-ui-CalendarActivity$MonthView$2  reason: not valid java name */
                public /* synthetic */ void m1488x715498fb(View view) {
                    CalendarActivity.this.finishPreviewFragment();
                }
            });
            this.gestureDetector = gestureDetectorCompat;
            gestureDetectorCompat.setIsLongpressEnabled(CalendarActivity.this.calendarType == 0 ? true : z);
        }

        /* renamed from: lambda$new$0$org-telegram-ui-CalendarActivity$MonthView  reason: not valid java name */
        public /* synthetic */ boolean m1483lambda$new$0$orgtelegramuiCalendarActivity$MonthView(View view) {
            if (this.messagesByDays == null) {
                return false;
            }
            int start = -1;
            int end = -1;
            for (int i = 0; i < this.daysInMonth; i++) {
                PeriodDay day = this.messagesByDays.get(i, (Object) null);
                if (day != null) {
                    if (start == -1) {
                        start = day.date;
                    }
                    end = day.date;
                }
            }
            if (start >= 0 && end >= 0) {
                boolean unused = CalendarActivity.this.inSelectionMode = true;
                int unused2 = CalendarActivity.this.dateSelectedStart = start;
                int unused3 = CalendarActivity.this.dateSelectedEnd = end;
                CalendarActivity.this.updateTitle();
                CalendarActivity.this.animateSelection();
            }
            return false;
        }

        /* access modifiers changed from: private */
        public void startSelectionAnimation(int fromDate, int toDate) {
            if (this.messagesByDays != null) {
                for (int i = 0; i < this.daysInMonth; i++) {
                    PeriodDay day = this.messagesByDays.get(i, (Object) null);
                    if (day != null) {
                        day.fromSelProgress = day.selectProgress;
                        day.toSelProgress = (day.date < fromDate || day.date > toDate) ? 0.0f : 1.0f;
                        day.fromSelSEProgress = day.selectStartEndProgress;
                        if (day.date == fromDate || day.date == toDate) {
                            day.toSelSEProgress = 1.0f;
                        } else {
                            day.toSelSEProgress = 0.0f;
                        }
                    }
                }
            }
        }

        /* access modifiers changed from: private */
        public void setSelectionValue(float f) {
            if (this.messagesByDays != null) {
                for (int i = 0; i < this.daysInMonth; i++) {
                    PeriodDay day = this.messagesByDays.get(i, (Object) null);
                    if (day != null) {
                        day.selectProgress = day.fromSelProgress + ((day.toSelProgress - day.fromSelProgress) * f);
                        day.selectStartEndProgress = day.fromSelSEProgress + ((day.toSelSEProgress - day.fromSelSEProgress) * f);
                    }
                }
            }
            invalidate();
        }

        public void dismissRowAnimations(boolean animate) {
            for (int i = 0; i < this.rowSelectionPos.size(); i++) {
                animateRow(this.rowSelectionPos.keyAt(i), 0, 0, false, animate);
            }
        }

        public void animateRow(int row, int startColumn, int endColumn, boolean appear, boolean animate) {
            float fromAlpha;
            float cxFrom1;
            float cxFrom2;
            float f;
            int i = row;
            int i2 = startColumn;
            ValueAnimator a = this.rowAnimators.get(i);
            if (a != null) {
                a.cancel();
            }
            float xStep = ((float) getMeasuredWidth()) / 7.0f;
            RowAnimationValue p = this.rowSelectionPos.get(i);
            if (p != null) {
                cxFrom1 = p.startX;
                cxFrom2 = p.endX;
                fromAlpha = p.alpha;
            } else {
                cxFrom1 = (((float) i2) * xStep) + (xStep / 2.0f);
                cxFrom2 = (((float) i2) * xStep) + (xStep / 2.0f);
                fromAlpha = 0.0f;
            }
            float cxTo1 = appear ? (((float) i2) * xStep) + (xStep / 2.0f) : cxFrom1;
            if (appear) {
                f = (((float) endColumn) * xStep) + (xStep / 2.0f);
            } else {
                int i3 = endColumn;
                f = cxFrom2;
            }
            float cxTo2 = f;
            float toAlpha = appear ? 1.0f : 0.0f;
            RowAnimationValue pr = new RowAnimationValue(cxFrom1, cxFrom2);
            this.rowSelectionPos.put(i, pr);
            if (animate) {
                ValueAnimator anim = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f}).setDuration(300);
                anim.setInterpolator(Easings.easeInOutQuad);
                CalendarActivity$MonthView$$ExternalSyntheticLambda0 calendarActivity$MonthView$$ExternalSyntheticLambda0 = r0;
                ValueAnimator valueAnimator = a;
                ValueAnimator a2 = anim;
                RowAnimationValue pr2 = pr;
                float toAlpha2 = toAlpha;
                float cxTo22 = cxTo2;
                float cxTo12 = cxTo1;
                float f2 = cxFrom2;
                CalendarActivity$MonthView$$ExternalSyntheticLambda0 calendarActivity$MonthView$$ExternalSyntheticLambda02 = new CalendarActivity$MonthView$$ExternalSyntheticLambda0(this, pr, cxFrom1, cxTo1, cxFrom2, cxTo22, fromAlpha, toAlpha2);
                a2.addUpdateListener(calendarActivity$MonthView$$ExternalSyntheticLambda0);
                final RowAnimationValue rowAnimationValue = pr2;
                final float f3 = cxTo12;
                final float f4 = cxTo22;
                final float f5 = toAlpha2;
                final int i4 = row;
                final boolean z = appear;
                a2.addListener(new AnimatorListenerAdapter() {
                    public void onAnimationCancel(Animator animation) {
                        rowAnimationValue.startX = f3;
                        rowAnimationValue.endX = f4;
                        rowAnimationValue.alpha = f5;
                        MonthView.this.invalidate();
                    }

                    public void onAnimationEnd(Animator animation) {
                        MonthView.this.rowAnimators.remove(i4);
                        if (!z) {
                            MonthView.this.rowSelectionPos.remove(i4);
                        }
                    }
                });
                a2.start();
                this.rowAnimators.put(i, a2);
                RowAnimationValue rowAnimationValue2 = pr2;
                float f6 = toAlpha2;
                float f7 = cxTo22;
                float f8 = cxTo12;
                return;
            }
            float f9 = cxFrom2;
            ValueAnimator valueAnimator2 = a;
            RowAnimationValue pr3 = pr;
            pr3.startX = cxTo1;
            pr3.endX = cxTo2;
            pr3.alpha = toAlpha;
            invalidate();
        }

        /* renamed from: lambda$animateRow$1$org-telegram-ui-CalendarActivity$MonthView  reason: not valid java name */
        public /* synthetic */ void m1482lambda$animateRow$1$orgtelegramuiCalendarActivity$MonthView(RowAnimationValue pr, float cxFrom1, float cxTo1, float cxFrom2, float cxTo2, float fromAlpha, float toAlpha, ValueAnimator animation) {
            float val = ((Float) animation.getAnimatedValue()).floatValue();
            pr.startX = ((cxTo1 - cxFrom1) * val) + cxFrom1;
            pr.endX = ((cxTo2 - cxFrom2) * val) + cxFrom2;
            pr.alpha = ((toAlpha - fromAlpha) * val) + fromAlpha;
            invalidate();
        }

        public boolean onTouchEvent(MotionEvent event) {
            return this.gestureDetector.onTouchEvent(event);
        }

        public void setDate(int year, int monthInYear, SparseArray<PeriodDay> messagesByDays2, boolean animated) {
            Object obj;
            TLRPC.PhotoSize currentPhotoObject;
            TLRPC.PhotoSize qualityThumb;
            MessageObject messageObject;
            int i = year;
            int i2 = monthInYear;
            SparseArray<PeriodDay> sparseArray = messagesByDays2;
            boolean dateChanged = (i == this.currentYear && i2 == this.currentMonthInYear) ? false : true;
            this.currentYear = i;
            this.currentMonthInYear = i2;
            this.messagesByDays = sparseArray;
            Object obj2 = null;
            if (dateChanged && this.imagesByDays != null) {
                for (int i3 = 0; i3 < this.imagesByDays.size(); i3++) {
                    this.imagesByDays.valueAt(i3).onDetachedFromWindow();
                    this.imagesByDays.valueAt(i3).setParentView((View) null);
                }
                this.imagesByDays = null;
            }
            if (sparseArray != null) {
                if (this.imagesByDays == null) {
                    this.imagesByDays = new SparseArray<>();
                }
                int i4 = 0;
                while (i4 < messagesByDays2.size()) {
                    int key = sparseArray.keyAt(i4);
                    if (this.imagesByDays.get(key, obj2) != null) {
                        obj = obj2;
                    } else if (!sparseArray.get(key).hasImage) {
                        obj = obj2;
                    } else {
                        ImageReceiver receiver = new ImageReceiver();
                        receiver.setParentView(this);
                        PeriodDay periodDay = sparseArray.get(key);
                        MessageObject messageObject2 = periodDay.messageObject;
                        if (messageObject2 != null) {
                            if (messageObject2.isVideo()) {
                                TLRPC.Document document = messageObject2.getDocument();
                                TLRPC.PhotoSize thumb = FileLoader.getClosestPhotoSizeWithSize(document.thumbs, 50);
                                TLRPC.PhotoSize qualityThumb2 = FileLoader.getClosestPhotoSizeWithSize(document.thumbs, 320);
                                if (thumb == qualityThumb2) {
                                    qualityThumb = null;
                                } else {
                                    qualityThumb = qualityThumb2;
                                }
                                if (thumb == null) {
                                    TLRPC.PhotoSize photoSize = qualityThumb;
                                    messageObject = messageObject2;
                                    PeriodDay periodDay2 = periodDay;
                                } else if (messageObject2.strippedThumb != null) {
                                    TLRPC.Document document2 = document;
                                    TLRPC.PhotoSize photoSize2 = qualityThumb;
                                    messageObject = messageObject2;
                                    PeriodDay periodDay3 = periodDay;
                                    receiver.setImage(ImageLocation.getForDocument(qualityThumb, document), "44_44", messageObject2.strippedThumb, (String) null, messageObject, 0);
                                } else {
                                    TLRPC.Document document3 = document;
                                    messageObject = messageObject2;
                                    PeriodDay periodDay4 = periodDay;
                                    TLRPC.PhotoSize qualityThumb3 = qualityThumb;
                                    TLRPC.PhotoSize photoSize3 = qualityThumb3;
                                    receiver.setImage(ImageLocation.getForDocument(qualityThumb3, document3), "44_44", ImageLocation.getForDocument(thumb, document3), "b", (String) null, (Object) messageObject, 0);
                                }
                                MessageObject messageObject3 = messageObject;
                                obj = null;
                            } else {
                                PeriodDay periodDay5 = periodDay;
                                MessageObject messageObject4 = messageObject2;
                                if (!(messageObject4.messageOwner.media instanceof TLRPC.TL_messageMediaPhoto) || messageObject4.messageOwner.media.photo == null || messageObject4.photoThumbs.isEmpty()) {
                                    obj = null;
                                } else {
                                    TLRPC.PhotoSize currentPhotoObjectThumb = FileLoader.getClosestPhotoSizeWithSize(messageObject4.photoThumbs, 50);
                                    TLRPC.PhotoSize currentPhotoObject2 = FileLoader.getClosestPhotoSizeWithSize(messageObject4.photoThumbs, 320, false, currentPhotoObjectThumb, false);
                                    if (messageObject4.mediaExists) {
                                        currentPhotoObject = currentPhotoObject2;
                                        obj = null;
                                    } else if (DownloadController.getInstance(CalendarActivity.this.currentAccount).canDownloadMedia(messageObject4)) {
                                        currentPhotoObject = currentPhotoObject2;
                                        obj = null;
                                    } else if (messageObject4.strippedThumb != null) {
                                        TLRPC.PhotoSize photoSize4 = currentPhotoObject2;
                                        receiver.setImage((ImageLocation) null, (String) null, messageObject4.strippedThumb, (String) null, messageObject4, 0);
                                        obj = null;
                                    } else {
                                        obj = null;
                                        receiver.setImage((ImageLocation) null, (String) null, ImageLocation.getForObject(currentPhotoObjectThumb, messageObject4.photoThumbsObject), "b", (String) null, (Object) messageObject4, 0);
                                    }
                                    TLRPC.PhotoSize currentPhotoObject3 = currentPhotoObject;
                                    if (currentPhotoObject3 == currentPhotoObjectThumb) {
                                        currentPhotoObjectThumb = null;
                                    }
                                    if (messageObject4.strippedThumb != null) {
                                        TLRPC.PhotoSize photoSize5 = currentPhotoObject3;
                                        receiver.setImage(ImageLocation.getForObject(currentPhotoObject3, messageObject4.photoThumbsObject), "44_44", (ImageLocation) null, (String) null, messageObject4.strippedThumb, currentPhotoObject3 != null ? currentPhotoObject3.size : 0, (String) null, messageObject4, messageObject4.shouldEncryptPhotoOrVideo() ? 2 : 1);
                                    } else {
                                        TLRPC.PhotoSize photoSize6 = currentPhotoObject3;
                                        receiver.setImage(ImageLocation.getForObject(currentPhotoObject3, messageObject4.photoThumbsObject), "44_44", ImageLocation.getForObject(currentPhotoObjectThumb, messageObject4.photoThumbsObject), "b", currentPhotoObject3 != null ? currentPhotoObject3.size : 0, (String) null, messageObject4, messageObject4.shouldEncryptPhotoOrVideo() ? 2 : 1);
                                    }
                                }
                            }
                            receiver.setRoundRadius(AndroidUtilities.dp(22.0f));
                            this.imagesByDays.put(key, receiver);
                        } else {
                            obj = obj2;
                            MessageObject messageObject5 = messageObject2;
                            PeriodDay periodDay6 = periodDay;
                        }
                    }
                    i4++;
                    obj2 = obj;
                }
            }
            this.daysInMonth = YearMonth.of(i, i2 + 1).lengthOfMonth();
            Calendar calendar = Calendar.getInstance();
            calendar.set(i, i2, 0);
            this.startDayOfWeek = (calendar.get(7) + 6) % 7;
            this.startMonthTime = (int) (calendar.getTimeInMillis() / 1000);
            int totalColumns = this.daysInMonth + this.startDayOfWeek;
            this.cellCount = ((int) (((float) totalColumns) / 7.0f)) + (totalColumns % 7 == 0 ? 0 : 1);
            calendar.set(i, i2 + 1, 0);
            this.titleView.setText(LocaleController.formatYearMont(calendar.getTimeInMillis() / 1000, true));
            CalendarActivity.this.updateRowSelections(this, false);
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp((float) ((this.cellCount * 52) + 44)), NUM));
        }

        /* access modifiers changed from: protected */
        /* JADX WARNING: Removed duplicated region for block: B:74:0x04e9  */
        /* JADX WARNING: Removed duplicated region for block: B:82:0x04ed A[SYNTHETIC] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onDraw(android.graphics.Canvas r29) {
            /*
                r28 = this;
                r0 = r28
                r7 = r29
                super.onDraw(r29)
                r1 = 0
                int r2 = r0.startDayOfWeek
                int r3 = r28.getMeasuredWidth()
                float r3 = (float) r3
                r8 = 1088421888(0x40e00000, float:7.0)
                float r9 = r3 / r8
                r3 = 1112539136(0x42500000, float:52.0)
                int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
                float r10 = (float) r3
                r11 = 1110441984(0x42300000, float:44.0)
                int r12 = org.telegram.messenger.AndroidUtilities.dp(r11)
                r3 = 0
            L_0x0021:
                double r4 = (double) r3
                int r6 = r0.startDayOfWeek
                int r13 = r0.daysInMonth
                int r6 = r6 + r13
                float r6 = (float) r6
                float r6 = r6 / r8
                double r13 = (double) r6
                double r13 = java.lang.Math.ceil(r13)
                java.lang.String r15 = "chat_messagePanelVoiceBackground"
                r16 = 1073741824(0x40000000, float:2.0)
                int r6 = (r4 > r13 ? 1 : (r4 == r13 ? 0 : -1))
                if (r6 >= 0) goto L_0x009c
                float r4 = (float) r3
                float r4 = r4 * r10
                float r5 = r10 / r16
                float r4 = r4 + r5
                int r5 = org.telegram.messenger.AndroidUtilities.dp(r11)
                float r5 = (float) r5
                float r4 = r4 + r5
                android.util.SparseArray<org.telegram.ui.CalendarActivity$RowAnimationValue> r5 = r0.rowSelectionPos
                java.lang.Object r5 = r5.get(r3)
                org.telegram.ui.CalendarActivity$RowAnimationValue r5 = (org.telegram.ui.CalendarActivity.RowAnimationValue) r5
                if (r5 == 0) goto L_0x0097
                org.telegram.ui.CalendarActivity r6 = org.telegram.ui.CalendarActivity.this
                android.graphics.Paint r6 = r6.selectPaint
                int r13 = org.telegram.ui.ActionBar.Theme.getColor(r15)
                r6.setColor(r13)
                org.telegram.ui.CalendarActivity r6 = org.telegram.ui.CalendarActivity.this
                android.graphics.Paint r6 = r6.selectPaint
                float r13 = r5.alpha
                r14 = 1109603123(0x42233333, float:40.8)
                float r13 = r13 * r14
                int r13 = (int) r13
                r6.setAlpha(r13)
                android.graphics.RectF r6 = org.telegram.messenger.AndroidUtilities.rectTmp
                float r13 = r5.startX
                float r14 = (float) r12
                float r14 = r14 / r16
                float r13 = r13 - r14
                float r14 = (float) r12
                float r14 = r14 / r16
                float r14 = r4 - r14
                float r15 = r5.endX
                float r8 = (float) r12
                float r8 = r8 / r16
                float r15 = r15 + r8
                float r8 = (float) r12
                float r8 = r8 / r16
                float r8 = r8 + r4
                r6.set(r13, r14, r15, r8)
                r6 = 1107296256(0x42000000, float:32.0)
                int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
                android.graphics.RectF r8 = org.telegram.messenger.AndroidUtilities.rectTmp
                float r13 = (float) r6
                float r14 = (float) r6
                org.telegram.ui.CalendarActivity r15 = org.telegram.ui.CalendarActivity.this
                android.graphics.Paint r15 = r15.selectPaint
                r7.drawRoundRect(r8, r13, r14, r15)
            L_0x0097:
                int r3 = r3 + 1
                r8 = 1088421888(0x40e00000, float:7.0)
                goto L_0x0021
            L_0x009c:
                r3 = 0
                r8 = r1
                r13 = r2
                r14 = r3
            L_0x00a0:
                int r1 = r0.daysInMonth
                if (r14 >= r1) goto L_0x04f9
                float r1 = (float) r13
                float r1 = r1 * r9
                float r2 = r9 / r16
                float r6 = r1 + r2
                float r1 = (float) r8
                float r1 = r1 * r10
                float r2 = r10 / r16
                float r1 = r1 + r2
                int r2 = org.telegram.messenger.AndroidUtilities.dp(r11)
                float r2 = (float) r2
                float r5 = r1 + r2
                long r1 = java.lang.System.currentTimeMillis()
                r3 = 1000(0x3e8, double:4.94E-321)
                long r1 = r1 / r3
                int r4 = (int) r1
                android.util.SparseArray<org.telegram.ui.CalendarActivity$PeriodDay> r1 = r0.messagesByDays
                r2 = 0
                if (r1 == 0) goto L_0x00cc
                java.lang.Object r1 = r1.get(r14, r2)
                r2 = r1
                org.telegram.ui.CalendarActivity$PeriodDay r2 = (org.telegram.ui.CalendarActivity.PeriodDay) r2
            L_0x00cc:
                r3 = r2
                int r1 = r0.startMonthTime
                int r2 = r14 + 1
                r18 = 86400(0x15180, float:1.21072E-40)
                int r2 = r2 * r18
                int r1 = r1 + r2
                r19 = 1084227584(0x40a00000, float:5.0)
                if (r4 < r1) goto L_0x04a5
                org.telegram.ui.CalendarActivity r1 = org.telegram.ui.CalendarActivity.this
                int r1 = r1.minDate
                if (r1 <= 0) goto L_0x0102
                org.telegram.ui.CalendarActivity r1 = org.telegram.ui.CalendarActivity.this
                int r1 = r1.minDate
                int r2 = r0.startMonthTime
                int r20 = r14 + 2
                int r20 = r20 * r18
                int r2 = r2 + r20
                if (r1 <= r2) goto L_0x0102
                r22 = r4
                r24 = r9
                r25 = r10
                r23 = r12
                r10 = r3
                r12 = r5
                r9 = r6
                r3 = 1110441984(0x42300000, float:44.0)
                goto L_0x04b2
            L_0x0102:
                java.lang.String r2 = "windowBackgroundWhite"
                r18 = 1008981770(0x3CLASSNAMEd70a, float:0.01)
                r20 = 1132396544(0x437var_, float:255.0)
                if (r3 == 0) goto L_0x034f
                boolean r11 = r3.hasImage
                if (r11 == 0) goto L_0x034f
                r11 = 1065353216(0x3var_, float:1.0)
                android.util.SparseArray<org.telegram.messenger.ImageReceiver> r1 = r0.imagesByDays
                java.lang.Object r1 = r1.get(r14)
                if (r1 == 0) goto L_0x02c7
                org.telegram.ui.CalendarActivity r1 = org.telegram.ui.CalendarActivity.this
                boolean r1 = r1.checkEnterItems
                r22 = r4
                r4 = 0
                if (r1 == 0) goto L_0x0144
                boolean r1 = r3.wasDrawn
                if (r1 != 0) goto L_0x0144
                r3.enterAlpha = r4
                float r1 = r28.getY()
                float r1 = r1 + r5
                org.telegram.ui.CalendarActivity r4 = org.telegram.ui.CalendarActivity.this
                org.telegram.ui.Components.RecyclerListView r4 = r4.listView
                int r4 = r4.getMeasuredHeight()
                float r4 = (float) r4
                float r1 = r1 / r4
                r4 = 1125515264(0x43160000, float:150.0)
                float r1 = r1 * r4
                r4 = 0
                float r1 = java.lang.Math.max(r4, r1)
                r3.startEnterDelay = r1
            L_0x0144:
                float r1 = r3.startEnterDelay
                int r1 = (r1 > r4 ? 1 : (r1 == r4 ? 0 : -1))
                if (r1 <= 0) goto L_0x015e
                float r1 = r3.startEnterDelay
                r23 = 1098907648(0x41800000, float:16.0)
                float r1 = r1 - r23
                r3.startEnterDelay = r1
                float r1 = r3.startEnterDelay
                int r1 = (r1 > r4 ? 1 : (r1 == r4 ? 0 : -1))
                if (r1 >= 0) goto L_0x015b
                r3.startEnterDelay = r4
                goto L_0x015e
            L_0x015b:
                r28.invalidate()
            L_0x015e:
                float r1 = r3.startEnterDelay
                int r1 = (r1 > r4 ? 1 : (r1 == r4 ? 0 : -1))
                if (r1 < 0) goto L_0x0181
                float r1 = r3.enterAlpha
                r4 = 1065353216(0x3var_, float:1.0)
                int r1 = (r1 > r4 ? 1 : (r1 == r4 ? 0 : -1))
                if (r1 == 0) goto L_0x0181
                float r1 = r3.enterAlpha
                r21 = 1033171465(0x3d94var_, float:0.07272727)
                float r1 = r1 + r21
                r3.enterAlpha = r1
                float r1 = r3.enterAlpha
                int r1 = (r1 > r4 ? 1 : (r1 == r4 ? 0 : -1))
                if (r1 <= 0) goto L_0x017e
                r3.enterAlpha = r4
                goto L_0x0181
            L_0x017e:
                r28.invalidate()
            L_0x0181:
                float r11 = r3.enterAlpha
                r1 = 1065353216(0x3var_, float:1.0)
                int r4 = (r11 > r1 ? 1 : (r11 == r1 ? 0 : -1))
                if (r4 == 0) goto L_0x0199
                r29.save()
                r4 = 1061997773(0x3f4ccccd, float:0.8)
                r21 = 1045220557(0x3e4ccccd, float:0.2)
                float r21 = r21 * r11
                float r4 = r21 + r4
                r7.scale(r4, r4, r6, r5)
            L_0x0199:
                r4 = 1088421888(0x40e00000, float:7.0)
                int r1 = org.telegram.messenger.AndroidUtilities.dp(r4)
                float r1 = (float) r1
                float r4 = r3.selectProgress
                float r1 = r1 * r4
                int r4 = (int) r1
                float r1 = r3.selectStartEndProgress
                int r1 = (r1 > r18 ? 1 : (r1 == r18 ? 0 : -1))
                if (r1 < 0) goto L_0x023d
                org.telegram.ui.CalendarActivity r1 = org.telegram.ui.CalendarActivity.this
                android.graphics.Paint r1 = r1.selectPaint
                int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
                r1.setColor(r2)
                org.telegram.ui.CalendarActivity r1 = org.telegram.ui.CalendarActivity.this
                android.graphics.Paint r1 = r1.selectPaint
                float r2 = r3.selectStartEndProgress
                float r2 = r2 * r20
                int r2 = (int) r2
                r1.setAlpha(r2)
                r1 = 1110441984(0x42300000, float:44.0)
                int r2 = org.telegram.messenger.AndroidUtilities.dp(r1)
                float r1 = (float) r2
                float r1 = r1 / r16
                org.telegram.ui.CalendarActivity r2 = org.telegram.ui.CalendarActivity.this
                android.graphics.Paint r2 = r2.selectPaint
                r7.drawCircle(r6, r5, r1, r2)
                org.telegram.ui.CalendarActivity r1 = org.telegram.ui.CalendarActivity.this
                android.graphics.Paint r1 = r1.selectOutlinePaint
                int r2 = org.telegram.ui.ActionBar.Theme.getColor(r15)
                r1.setColor(r2)
                android.graphics.RectF r1 = org.telegram.messenger.AndroidUtilities.rectTmp
                r23 = r4
                r2 = 1110441984(0x42300000, float:44.0)
                int r4 = org.telegram.messenger.AndroidUtilities.dp(r2)
                float r4 = (float) r4
                float r4 = r4 / r16
                float r4 = r6 - r4
                r24 = r9
                int r9 = org.telegram.messenger.AndroidUtilities.dp(r2)
                float r9 = (float) r9
                float r9 = r9 / r16
                float r9 = r5 - r9
                r25 = r10
                int r10 = org.telegram.messenger.AndroidUtilities.dp(r2)
                float r10 = (float) r10
                float r10 = r10 / r16
                float r10 = r10 + r6
                r26 = r6
                int r6 = org.telegram.messenger.AndroidUtilities.dp(r2)
                float r2 = (float) r6
                float r2 = r2 / r16
                float r2 = r2 + r5
                r1.set(r4, r9, r10, r2)
                android.graphics.RectF r2 = org.telegram.messenger.AndroidUtilities.rectTmp
                r4 = -1028390912(0xffffffffc2b40000, float:-90.0)
                float r1 = r3.selectStartEndProgress
                r6 = 1135869952(0x43b40000, float:360.0)
                float r6 = r6 * r1
                r9 = 0
                org.telegram.ui.CalendarActivity r1 = org.telegram.ui.CalendarActivity.this
                android.graphics.Paint r10 = r1.selectOutlinePaint
                r21 = 1065353216(0x3var_, float:1.0)
                r1 = r29
                r27 = r3
                r3 = r4
                r18 = r23
                r4 = r6
                r6 = r5
                r5 = r9
                r23 = r12
                r9 = r26
                r12 = r6
                r6 = r10
                r1.drawArc(r2, r3, r4, r5, r6)
                goto L_0x024b
            L_0x023d:
                r27 = r3
                r18 = r4
                r24 = r9
                r25 = r10
                r23 = r12
                r21 = 1065353216(0x3var_, float:1.0)
                r12 = r5
                r9 = r6
            L_0x024b:
                android.util.SparseArray<org.telegram.messenger.ImageReceiver> r1 = r0.imagesByDays
                java.lang.Object r1 = r1.get(r14)
                org.telegram.messenger.ImageReceiver r1 = (org.telegram.messenger.ImageReceiver) r1
                r10 = r27
                float r2 = r10.enterAlpha
                r1.setAlpha(r2)
                android.util.SparseArray<org.telegram.messenger.ImageReceiver> r1 = r0.imagesByDays
                java.lang.Object r1 = r1.get(r14)
                org.telegram.messenger.ImageReceiver r1 = (org.telegram.messenger.ImageReceiver) r1
                r2 = 1110441984(0x42300000, float:44.0)
                int r3 = org.telegram.messenger.AndroidUtilities.dp(r2)
                int r3 = r3 - r18
                float r3 = (float) r3
                float r3 = r3 / r16
                float r6 = r9 - r3
                int r3 = org.telegram.messenger.AndroidUtilities.dp(r2)
                int r3 = r3 - r18
                float r3 = (float) r3
                float r3 = r3 / r16
                float r5 = r12 - r3
                int r3 = org.telegram.messenger.AndroidUtilities.dp(r2)
                int r3 = r3 - r18
                float r3 = (float) r3
                int r4 = org.telegram.messenger.AndroidUtilities.dp(r2)
                int r4 = r4 - r18
                float r2 = (float) r4
                r1.setImageCoords(r6, r5, r3, r2)
                android.util.SparseArray<org.telegram.messenger.ImageReceiver> r1 = r0.imagesByDays
                java.lang.Object r1 = r1.get(r14)
                org.telegram.messenger.ImageReceiver r1 = (org.telegram.messenger.ImageReceiver) r1
                r1.draw(r7)
                org.telegram.ui.CalendarActivity r1 = org.telegram.ui.CalendarActivity.this
                android.graphics.Paint r1 = r1.blackoutPaint
                r2 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
                float r3 = r10.enterAlpha
                r4 = 1117782016(0x42a00000, float:80.0)
                float r3 = r3 * r4
                int r3 = (int) r3
                int r2 = androidx.core.graphics.ColorUtils.setAlphaComponent(r2, r3)
                r1.setColor(r2)
                r1 = 1110441984(0x42300000, float:44.0)
                int r2 = org.telegram.messenger.AndroidUtilities.dp(r1)
                int r2 = r2 - r18
                float r1 = (float) r2
                float r1 = r1 / r16
                org.telegram.ui.CalendarActivity r2 = org.telegram.ui.CalendarActivity.this
                android.graphics.Paint r2 = r2.blackoutPaint
                r7.drawCircle(r9, r12, r1, r2)
                r1 = 1
                r10.wasDrawn = r1
                int r1 = (r11 > r21 ? 1 : (r11 == r21 ? 0 : -1))
                if (r1 == 0) goto L_0x02d4
                r29.restore()
                goto L_0x02d4
            L_0x02c7:
                r22 = r4
                r24 = r9
                r25 = r10
                r23 = r12
                r21 = 1065353216(0x3var_, float:1.0)
                r10 = r3
                r12 = r5
                r9 = r6
            L_0x02d4:
                int r1 = (r11 > r21 ? 1 : (r11 == r21 ? 0 : -1))
                if (r1 == 0) goto L_0x0337
                org.telegram.ui.CalendarActivity r1 = org.telegram.ui.CalendarActivity.this
                android.text.TextPaint r1 = r1.textPaint
                int r1 = r1.getAlpha()
                org.telegram.ui.CalendarActivity r2 = org.telegram.ui.CalendarActivity.this
                android.text.TextPaint r2 = r2.textPaint
                float r3 = (float) r1
                float r4 = r21 - r11
                float r3 = r3 * r4
                int r3 = (int) r3
                r2.setAlpha(r3)
                int r2 = r14 + 1
                java.lang.String r2 = java.lang.Integer.toString(r2)
                int r3 = org.telegram.messenger.AndroidUtilities.dp(r19)
                float r3 = (float) r3
                float r5 = r12 + r3
                org.telegram.ui.CalendarActivity r3 = org.telegram.ui.CalendarActivity.this
                android.text.TextPaint r3 = r3.textPaint
                r7.drawText(r2, r9, r5, r3)
                org.telegram.ui.CalendarActivity r2 = org.telegram.ui.CalendarActivity.this
                android.text.TextPaint r2 = r2.textPaint
                r2.setAlpha(r1)
                org.telegram.ui.CalendarActivity r2 = org.telegram.ui.CalendarActivity.this
                android.text.TextPaint r2 = r2.textPaint
                int r1 = r2.getAlpha()
                org.telegram.ui.CalendarActivity r2 = org.telegram.ui.CalendarActivity.this
                android.text.TextPaint r2 = r2.activeTextPaint
                float r3 = (float) r1
                float r3 = r3 * r11
                int r3 = (int) r3
                r2.setAlpha(r3)
                int r2 = r14 + 1
                java.lang.String r2 = java.lang.Integer.toString(r2)
                int r3 = org.telegram.messenger.AndroidUtilities.dp(r19)
                float r3 = (float) r3
                float r5 = r12 + r3
                org.telegram.ui.CalendarActivity r3 = org.telegram.ui.CalendarActivity.this
                android.text.TextPaint r3 = r3.activeTextPaint
                r7.drawText(r2, r9, r5, r3)
                org.telegram.ui.CalendarActivity r2 = org.telegram.ui.CalendarActivity.this
                android.text.TextPaint r2 = r2.activeTextPaint
                r2.setAlpha(r1)
                goto L_0x034b
            L_0x0337:
                int r1 = r14 + 1
                java.lang.String r1 = java.lang.Integer.toString(r1)
                int r2 = org.telegram.messenger.AndroidUtilities.dp(r19)
                float r2 = (float) r2
                float r5 = r12 + r2
                org.telegram.ui.CalendarActivity r2 = org.telegram.ui.CalendarActivity.this
                android.text.TextPaint r2 = r2.activeTextPaint
                r7.drawText(r1, r9, r5, r2)
            L_0x034b:
                r3 = 1110441984(0x42300000, float:44.0)
                goto L_0x04e4
            L_0x034f:
                r22 = r4
                r24 = r9
                r25 = r10
                r23 = r12
                r21 = 1065353216(0x3var_, float:1.0)
                r10 = r3
                r12 = r5
                r9 = r6
                if (r10 == 0) goto L_0x048e
                float r1 = r10.selectStartEndProgress
                int r1 = (r1 > r18 ? 1 : (r1 == r18 ? 0 : -1))
                if (r1 < 0) goto L_0x048e
                org.telegram.ui.CalendarActivity r1 = org.telegram.ui.CalendarActivity.this
                android.graphics.Paint r1 = r1.selectPaint
                int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
                r1.setColor(r2)
                org.telegram.ui.CalendarActivity r1 = org.telegram.ui.CalendarActivity.this
                android.graphics.Paint r1 = r1.selectPaint
                float r2 = r10.selectStartEndProgress
                float r2 = r2 * r20
                int r2 = (int) r2
                r1.setAlpha(r2)
                r1 = 1110441984(0x42300000, float:44.0)
                int r2 = org.telegram.messenger.AndroidUtilities.dp(r1)
                float r1 = (float) r2
                float r1 = r1 / r16
                org.telegram.ui.CalendarActivity r2 = org.telegram.ui.CalendarActivity.this
                android.graphics.Paint r2 = r2.selectPaint
                r7.drawCircle(r9, r12, r1, r2)
                org.telegram.ui.CalendarActivity r1 = org.telegram.ui.CalendarActivity.this
                android.graphics.Paint r1 = r1.selectOutlinePaint
                int r2 = org.telegram.ui.ActionBar.Theme.getColor(r15)
                r1.setColor(r2)
                android.graphics.RectF r1 = org.telegram.messenger.AndroidUtilities.rectTmp
                r2 = 1110441984(0x42300000, float:44.0)
                int r3 = org.telegram.messenger.AndroidUtilities.dp(r2)
                float r3 = (float) r3
                float r3 = r3 / r16
                float r6 = r9 - r3
                int r3 = org.telegram.messenger.AndroidUtilities.dp(r2)
                float r3 = (float) r3
                float r3 = r3 / r16
                float r5 = r12 - r3
                int r3 = org.telegram.messenger.AndroidUtilities.dp(r2)
                float r3 = (float) r3
                float r3 = r3 / r16
                float r3 = r3 + r9
                int r4 = org.telegram.messenger.AndroidUtilities.dp(r2)
                float r2 = (float) r4
                float r2 = r2 / r16
                float r2 = r2 + r12
                r1.set(r6, r5, r3, r2)
                android.graphics.RectF r2 = org.telegram.messenger.AndroidUtilities.rectTmp
                r3 = -1028390912(0xffffffffc2b40000, float:-90.0)
                float r1 = r10.selectStartEndProgress
                r4 = 1135869952(0x43b40000, float:360.0)
                float r4 = r4 * r1
                r5 = 0
                org.telegram.ui.CalendarActivity r1 = org.telegram.ui.CalendarActivity.this
                android.graphics.Paint r6 = r1.selectOutlinePaint
                r1 = r29
                r1.drawArc(r2, r3, r4, r5, r6)
                r1 = 1088421888(0x40e00000, float:7.0)
                int r2 = org.telegram.messenger.AndroidUtilities.dp(r1)
                float r2 = (float) r2
                float r3 = r10.selectStartEndProgress
                float r2 = r2 * r3
                int r2 = (int) r2
                org.telegram.ui.CalendarActivity r3 = org.telegram.ui.CalendarActivity.this
                android.graphics.Paint r3 = r3.selectPaint
                int r4 = org.telegram.ui.ActionBar.Theme.getColor(r15)
                r3.setColor(r4)
                org.telegram.ui.CalendarActivity r3 = org.telegram.ui.CalendarActivity.this
                android.graphics.Paint r3 = r3.selectPaint
                float r4 = r10.selectStartEndProgress
                float r4 = r4 * r20
                int r4 = (int) r4
                r3.setAlpha(r4)
                r3 = 1110441984(0x42300000, float:44.0)
                int r4 = org.telegram.messenger.AndroidUtilities.dp(r3)
                int r4 = r4 - r2
                float r4 = (float) r4
                float r4 = r4 / r16
                org.telegram.ui.CalendarActivity r5 = org.telegram.ui.CalendarActivity.this
                android.graphics.Paint r5 = r5.selectPaint
                r7.drawCircle(r9, r12, r4, r5)
                float r4 = r10.selectStartEndProgress
                int r5 = (r4 > r21 ? 1 : (r4 == r21 ? 0 : -1))
                if (r5 == 0) goto L_0x047a
                org.telegram.ui.CalendarActivity r5 = org.telegram.ui.CalendarActivity.this
                android.text.TextPaint r5 = r5.textPaint
                int r5 = r5.getAlpha()
                org.telegram.ui.CalendarActivity r6 = org.telegram.ui.CalendarActivity.this
                android.text.TextPaint r6 = r6.textPaint
                float r11 = (float) r5
                float r17 = r21 - r4
                float r11 = r11 * r17
                int r11 = (int) r11
                r6.setAlpha(r11)
                int r6 = r14 + 1
                java.lang.String r6 = java.lang.Integer.toString(r6)
                int r11 = org.telegram.messenger.AndroidUtilities.dp(r19)
                float r11 = (float) r11
                float r11 = r11 + r12
                org.telegram.ui.CalendarActivity r1 = org.telegram.ui.CalendarActivity.this
                android.text.TextPaint r1 = r1.textPaint
                r7.drawText(r6, r9, r11, r1)
                org.telegram.ui.CalendarActivity r1 = org.telegram.ui.CalendarActivity.this
                android.text.TextPaint r1 = r1.textPaint
                r1.setAlpha(r5)
                org.telegram.ui.CalendarActivity r1 = org.telegram.ui.CalendarActivity.this
                android.text.TextPaint r1 = r1.textPaint
                int r1 = r1.getAlpha()
                org.telegram.ui.CalendarActivity r5 = org.telegram.ui.CalendarActivity.this
                android.text.TextPaint r5 = r5.activeTextPaint
                float r6 = (float) r1
                float r6 = r6 * r4
                int r6 = (int) r6
                r5.setAlpha(r6)
                int r5 = r14 + 1
                java.lang.String r5 = java.lang.Integer.toString(r5)
                int r6 = org.telegram.messenger.AndroidUtilities.dp(r19)
                float r6 = (float) r6
                float r6 = r6 + r12
                org.telegram.ui.CalendarActivity r11 = org.telegram.ui.CalendarActivity.this
                android.text.TextPaint r11 = r11.activeTextPaint
                r7.drawText(r5, r9, r6, r11)
                org.telegram.ui.CalendarActivity r5 = org.telegram.ui.CalendarActivity.this
                android.text.TextPaint r5 = r5.activeTextPaint
                r5.setAlpha(r1)
                goto L_0x048d
            L_0x047a:
                int r1 = r14 + 1
                java.lang.String r1 = java.lang.Integer.toString(r1)
                int r5 = org.telegram.messenger.AndroidUtilities.dp(r19)
                float r5 = (float) r5
                float r5 = r5 + r12
                org.telegram.ui.CalendarActivity r6 = org.telegram.ui.CalendarActivity.this
                android.text.TextPaint r6 = r6.activeTextPaint
                r7.drawText(r1, r9, r5, r6)
            L_0x048d:
                goto L_0x04e4
            L_0x048e:
                r3 = 1110441984(0x42300000, float:44.0)
                int r1 = r14 + 1
                java.lang.String r1 = java.lang.Integer.toString(r1)
                int r2 = org.telegram.messenger.AndroidUtilities.dp(r19)
                float r2 = (float) r2
                float r5 = r12 + r2
                org.telegram.ui.CalendarActivity r2 = org.telegram.ui.CalendarActivity.this
                android.text.TextPaint r2 = r2.textPaint
                r7.drawText(r1, r9, r5, r2)
                goto L_0x04e4
            L_0x04a5:
                r22 = r4
                r24 = r9
                r25 = r10
                r23 = r12
                r10 = r3
                r12 = r5
                r9 = r6
                r3 = 1110441984(0x42300000, float:44.0)
            L_0x04b2:
                org.telegram.ui.CalendarActivity r1 = org.telegram.ui.CalendarActivity.this
                android.text.TextPaint r1 = r1.textPaint
                int r1 = r1.getAlpha()
                org.telegram.ui.CalendarActivity r2 = org.telegram.ui.CalendarActivity.this
                android.text.TextPaint r2 = r2.textPaint
                float r4 = (float) r1
                r5 = 1050253722(0x3e99999a, float:0.3)
                float r4 = r4 * r5
                int r4 = (int) r4
                r2.setAlpha(r4)
                int r2 = r14 + 1
                java.lang.String r2 = java.lang.Integer.toString(r2)
                int r4 = org.telegram.messenger.AndroidUtilities.dp(r19)
                float r4 = (float) r4
                float r5 = r12 + r4
                org.telegram.ui.CalendarActivity r4 = org.telegram.ui.CalendarActivity.this
                android.text.TextPaint r4 = r4.textPaint
                r7.drawText(r2, r9, r5, r4)
                org.telegram.ui.CalendarActivity r2 = org.telegram.ui.CalendarActivity.this
                android.text.TextPaint r2 = r2.textPaint
                r2.setAlpha(r1)
            L_0x04e4:
                int r13 = r13 + 1
                r1 = 7
                if (r13 < r1) goto L_0x04ed
                r1 = 0
                int r8 = r8 + 1
                r13 = r1
            L_0x04ed:
                int r14 = r14 + 1
                r12 = r23
                r9 = r24
                r10 = r25
                r11 = 1110441984(0x42300000, float:44.0)
                goto L_0x00a0
            L_0x04f9:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.CalendarActivity.MonthView.onDraw(android.graphics.Canvas):void");
        }

        /* access modifiers changed from: protected */
        public void onAttachedToWindow() {
            super.onAttachedToWindow();
            this.attached = true;
            if (this.imagesByDays != null) {
                for (int i = 0; i < this.imagesByDays.size(); i++) {
                    this.imagesByDays.valueAt(i).onAttachedToWindow();
                }
            }
        }

        /* access modifiers changed from: protected */
        public void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            this.attached = false;
            if (this.imagesByDays != null) {
                for (int i = 0; i < this.imagesByDays.size(); i++) {
                    this.imagesByDays.valueAt(i).onDetachedFromWindow();
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void updateTitle() {
        int daysSelected;
        String title;
        HintView hintView;
        if (!this.canClearHistory) {
            this.actionBar.setTitle(LocaleController.getString("Calendar", NUM));
            this.backDrawable.setRotation(0.0f, true);
            return;
        }
        int daysSelected2 = this.dateSelectedStart;
        int i = this.dateSelectedEnd;
        if (daysSelected2 == i && daysSelected2 == 0) {
            daysSelected = 0;
        } else {
            daysSelected = (Math.abs(daysSelected2 - i) / 86400) + 1;
        }
        boolean z = this.lastInSelectionMode;
        int i2 = this.lastDaysSelected;
        if (daysSelected != i2 || this.lastInSelectionMode != this.inSelectionMode) {
            boolean fromBottom = i2 > daysSelected;
            this.lastDaysSelected = daysSelected;
            boolean z2 = this.inSelectionMode;
            this.lastInSelectionMode = z2;
            float f = 1.0f;
            if (daysSelected > 0) {
                title = LocaleController.formatPluralString("Days", daysSelected);
                this.backDrawable.setRotation(1.0f, true);
            } else if (z2) {
                title = LocaleController.getString("SelectDays", NUM);
                this.backDrawable.setRotation(1.0f, true);
            } else {
                title = LocaleController.getString("Calendar", NUM);
                this.backDrawable.setRotation(0.0f, true);
            }
            if (daysSelected > 1) {
                this.removeDaysButton.setText(LocaleController.formatString("ClearHistoryForTheseDays", NUM, new Object[0]));
            } else if (daysSelected > 0 || this.inSelectionMode) {
                this.removeDaysButton.setText(LocaleController.formatString("ClearHistoryForThisDay", NUM, new Object[0]));
            }
            this.actionBar.setTitleAnimated(title, fromBottom, 150);
            if ((!this.inSelectionMode || daysSelected > 0) && (hintView = this.selectDaysHint) != null) {
                hintView.hide();
            }
            if (daysSelected > 0 || this.inSelectionMode) {
                if (this.removeDaysButton.getVisibility() == 8) {
                    this.removeDaysButton.setAlpha(0.0f);
                    this.removeDaysButton.setTranslationY((float) (-AndroidUtilities.dp(20.0f)));
                }
                this.removeDaysButton.setVisibility(0);
                this.selectDaysButton.animate().setListener((Animator.AnimatorListener) null).cancel();
                this.removeDaysButton.animate().setListener((Animator.AnimatorListener) null).cancel();
                this.selectDaysButton.animate().alpha(0.0f).translationY((float) AndroidUtilities.dp(20.0f)).setDuration(150).setListener(new HideViewAfterAnimation(this.selectDaysButton)).start();
                ViewPropertyAnimator animate = this.removeDaysButton.animate();
                if (daysSelected == 0) {
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

        private PeriodDay() {
            this.enterAlpha = 1.0f;
            this.startEnterDelay = 1.0f;
            this.hasImage = true;
        }
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ThemeDescription.ThemeDescriptionDelegate descriptionDelegate = new ThemeDescription.ThemeDescriptionDelegate() {
            public /* synthetic */ void onAnimationProgress(float f) {
                ThemeDescription.ThemeDescriptionDelegate.CC.$default$onAnimationProgress(this, f);
            }

            public void didSetColor() {
                CalendarActivity.this.updateColors();
            }
        };
        new ArrayList();
        new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, descriptionDelegate, "windowBackgroundWhite");
        new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, descriptionDelegate, "windowBackgroundWhiteBlackText");
        new ThemeDescription((View) null, 0, (Class[]) null, (Paint) null, (Drawable[]) null, descriptionDelegate, "listSelectorSDK21");
        return super.getThemeDescriptions();
    }

    public boolean needDelayOpenAnimation() {
        return true;
    }

    /* access modifiers changed from: protected */
    public void onTransitionAnimationStart(boolean isOpen, boolean backward) {
        super.onTransitionAnimationStart(isOpen, backward);
        this.isOpened = true;
    }

    /* access modifiers changed from: protected */
    public void onTransitionAnimationProgress(boolean isOpen, float progress) {
        super.onTransitionAnimationProgress(isOpen, progress);
        View view = this.blurredView;
        if (view != null && view.getVisibility() == 0) {
            if (isOpen) {
                this.blurredView.setAlpha(1.0f - progress);
            } else {
                this.blurredView.setAlpha(progress);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        View view;
        if (isOpen && (view = this.blurredView) != null && view.getVisibility() == 0) {
            this.blurredView.setVisibility(8);
            this.blurredView.setBackground((Drawable) null);
        }
    }

    /* access modifiers changed from: private */
    public void animateSelection() {
        ValueAnimator a = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f}).setDuration(300);
        a.setInterpolator(CubicBezierInterpolator.DEFAULT);
        a.addUpdateListener(new CalendarActivity$$ExternalSyntheticLambda0(this));
        a.addListener(new AnimatorListenerAdapter() {
            public void onAnimationStart(Animator animation) {
                for (int j = 0; j < CalendarActivity.this.listView.getChildCount(); j++) {
                    ((MonthView) CalendarActivity.this.listView.getChildAt(j)).startSelectionAnimation(CalendarActivity.this.dateSelectedStart, CalendarActivity.this.dateSelectedEnd);
                }
            }
        });
        a.start();
        this.selectionAnimator = a;
        for (int j = 0; j < this.listView.getChildCount(); j++) {
            updateRowSelections((MonthView) this.listView.getChildAt(j), true);
        }
        for (int j2 = 0; j2 < this.listView.getCachedChildCount(); j2++) {
            MonthView m = (MonthView) this.listView.getCachedChildAt(j2);
            updateRowSelections(m, false);
            m.startSelectionAnimation(this.dateSelectedStart, this.dateSelectedEnd);
            m.setSelectionValue(1.0f);
        }
        for (int j3 = 0; j3 < this.listView.getHiddenChildCount(); j3++) {
            MonthView m2 = (MonthView) this.listView.getHiddenChildAt(j3);
            updateRowSelections(m2, false);
            m2.startSelectionAnimation(this.dateSelectedStart, this.dateSelectedEnd);
            m2.setSelectionValue(1.0f);
        }
        for (int j4 = 0; j4 < this.listView.getAttachedScrapChildCount(); j4++) {
            MonthView m3 = (MonthView) this.listView.getAttachedScrapChildAt(j4);
            updateRowSelections(m3, false);
            m3.startSelectionAnimation(this.dateSelectedStart, this.dateSelectedEnd);
            m3.setSelectionValue(1.0f);
        }
    }

    /* renamed from: lambda$animateSelection$4$org-telegram-ui-CalendarActivity  reason: not valid java name */
    public /* synthetic */ void m1477lambda$animateSelection$4$orgtelegramuiCalendarActivity(ValueAnimator animation) {
        float selectProgress = ((Float) animation.getAnimatedValue()).floatValue();
        for (int j = 0; j < this.listView.getChildCount(); j++) {
            ((MonthView) this.listView.getChildAt(j)).setSelectionValue(selectProgress);
        }
    }

    /* access modifiers changed from: private */
    public void updateRowSelections(MonthView m, boolean animate) {
        if (this.dateSelectedStart == 0 || this.dateSelectedEnd == 0) {
            m.dismissRowAnimations(animate);
        } else if (m.messagesByDays != null) {
            if (!animate) {
                m.dismissRowAnimations(false);
            }
            int row = 0;
            int dayInRow = m.startDayOfWeek;
            int sDay = -1;
            int eDay = -1;
            for (int i = 0; i < m.daysInMonth; i++) {
                PeriodDay day = m.messagesByDays.get(i, (Object) null);
                if (day != null && day.date >= this.dateSelectedStart && day.date <= this.dateSelectedEnd) {
                    if (sDay == -1) {
                        sDay = dayInRow;
                    }
                    eDay = dayInRow;
                }
                dayInRow++;
                if (dayInRow >= 7) {
                    dayInRow = 0;
                    if (sDay == -1 || eDay == -1) {
                        m.animateRow(row, 0, 0, false, animate);
                    } else {
                        m.animateRow(row, sDay, eDay, true, animate);
                    }
                    row++;
                    sDay = -1;
                    eDay = -1;
                }
            }
            if (sDay == -1 || eDay == -1) {
                m.animateRow(row, 0, 0, false, animate);
            } else {
                m.animateRow(row, sDay, eDay, true, animate);
            }
        }
    }

    private static final class RowAnimationValue {
        float alpha;
        float endX;
        float startX;

        RowAnimationValue(float s, float e) {
            this.startX = s;
            this.endX = e;
        }
    }

    /* access modifiers changed from: private */
    public void prepareBlurBitmap() {
        if (this.blurredView != null) {
            int w = (int) (((float) this.parentLayout.getMeasuredWidth()) / 6.0f);
            int h = (int) (((float) this.parentLayout.getMeasuredHeight()) / 6.0f);
            Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            canvas.scale(0.16666667f, 0.16666667f);
            this.parentLayout.draw(canvas);
            Utilities.stackBlurBitmap(bitmap, Math.max(7, Math.max(w, h) / 180));
            this.blurredView.setBackground(new BitmapDrawable(bitmap));
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
}
