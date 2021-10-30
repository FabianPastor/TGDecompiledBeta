package org.telegram.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextPaint;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
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
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;

public class MediaCalendarActivity extends BaseFragment {
    TextPaint activeTextPaint = new TextPaint(1);
    CalendarAdapter adapter;
    Paint blackoutPaint = new Paint(1);
    Callback callback;
    /* access modifiers changed from: private */
    public boolean checkEnterItems;
    FrameLayout contentView;
    private long dialogId;
    boolean endReached;
    private boolean isOpened;
    int lastId;
    LinearLayoutManager layoutManager;
    RecyclerListView listView;
    private boolean loading;
    SparseArray<SparseArray<PeriodDay>> messagesByYearMounth = new SparseArray<>();
    int minMontYear;
    int monthCount;
    private int photosVideosTypeFilter;
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

    public MediaCalendarActivity(Bundle bundle, int i) {
        super(bundle);
        this.photosVideosTypeFilter = i;
    }

    public boolean onFragmentCreate() {
        this.dialogId = getArguments().getLong("dialog_id");
        return super.onFragmentCreate();
    }

    public View createView(Context context) {
        this.textPaint.setTextSize((float) AndroidUtilities.dp(16.0f));
        this.textPaint.setTextAlign(Paint.Align.CENTER);
        this.textPaint2.setTextSize((float) AndroidUtilities.dp(11.0f));
        this.textPaint2.setTextAlign(Paint.Align.CENTER);
        this.activeTextPaint.setTextSize((float) AndroidUtilities.dp(16.0f));
        this.activeTextPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.activeTextPaint.setTextAlign(Paint.Align.CENTER);
        this.contentView = new FrameLayout(context);
        createActionBar(context);
        this.contentView.addView(this.actionBar);
        this.actionBar.setTitle(LocaleController.getString("Calendar", NUM));
        this.actionBar.setCastShadows(false);
        AnonymousClass1 r0 = new RecyclerListView(context) {
            /* access modifiers changed from: protected */
            public void dispatchDraw(Canvas canvas) {
                super.dispatchDraw(canvas);
                boolean unused = MediaCalendarActivity.this.checkEnterItems = false;
            }
        };
        this.listView = r0;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        this.layoutManager = linearLayoutManager;
        r0.setLayoutManager(linearLayoutManager);
        this.layoutManager.setReverseLayout(true);
        RecyclerListView recyclerListView = this.listView;
        CalendarAdapter calendarAdapter = new CalendarAdapter();
        this.adapter = calendarAdapter;
        recyclerListView.setAdapter(calendarAdapter);
        this.listView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                super.onScrolled(recyclerView, i, i2);
                MediaCalendarActivity.this.checkLoadNext();
            }
        });
        this.contentView.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f, 0, 0.0f, 36.0f, 0.0f, 0.0f));
        final String[] strArr = {"M", "T", "W", "T", "F", "S", "S"};
        final Drawable mutate = ContextCompat.getDrawable(context, NUM).mutate();
        this.contentView.addView(new View(context) {
            /* access modifiers changed from: protected */
            public void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                float measuredWidth = ((float) getMeasuredWidth()) / 7.0f;
                for (int i = 0; i < 7; i++) {
                    canvas.drawText(strArr[i], (((float) i) * measuredWidth) + (measuredWidth / 2.0f), (((float) (getMeasuredHeight() - AndroidUtilities.dp(2.0f))) / 2.0f) + ((float) AndroidUtilities.dp(5.0f)), MediaCalendarActivity.this.textPaint2);
                }
                mutate.setBounds(0, getMeasuredHeight() - AndroidUtilities.dp(3.0f), getMeasuredWidth(), getMeasuredHeight());
                mutate.draw(canvas);
            }
        }, LayoutHelper.createFrame(-1, 38.0f, 0, 0.0f, 0.0f, 0.0f, 0.0f));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    MediaCalendarActivity.this.finishFragment();
                }
            }
        });
        this.fragmentView = this.contentView;
        Calendar instance = Calendar.getInstance();
        this.startFromYear = instance.get(1);
        this.startFromMonth = instance.get(2);
        this.monthCount = 3;
        loadNext();
        updateColors();
        this.activeTextPaint.setColor(-1);
        this.actionBar.setBackButtonImage(NUM);
        return this.fragmentView;
    }

    /* access modifiers changed from: private */
    public void updateColors() {
        this.actionBar.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        this.activeTextPaint.setColor(-1);
        this.textPaint.setColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.textPaint.setColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.actionBar.setTitleColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.actionBar.setBackButtonImage(NUM);
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
            getConnectionsManager().sendRequest(tLRPC$TL_messages_getSearchResultsCalendar, new MediaCalendarActivity$$ExternalSyntheticLambda1(this, Calendar.getInstance()));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadNext$1(Calendar calendar, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new MediaCalendarActivity$$ExternalSyntheticLambda0(this, tLRPC$TL_error, tLObject, calendar));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadNext$0(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject, Calendar calendar) {
        if (tLRPC$TL_error == null) {
            TLRPC$TL_messages_searchResultsCalendar tLRPC$TL_messages_searchResultsCalendar = (TLRPC$TL_messages_searchResultsCalendar) tLObject;
            int timeInMillis = ((int) (((calendar.getTimeInMillis() / 1000) - ((long) tLRPC$TL_messages_searchResultsCalendar.min_date)) / 2629800)) + 1;
            this.monthCount = timeInMillis;
            if (timeInMillis < 3) {
                this.monthCount = 3;
            }
            for (int i = 0; i < tLRPC$TL_messages_searchResultsCalendar.periods.size(); i++) {
                calendar.setTimeInMillis(((long) tLRPC$TL_messages_searchResultsCalendar.periods.get(i).date) * 1000);
                int i2 = (calendar.get(1) * 100) + calendar.get(2);
                SparseArray sparseArray = this.messagesByYearMounth.get(i2);
                if (sparseArray == null) {
                    sparseArray = new SparseArray();
                    this.messagesByYearMounth.put(i2, sparseArray);
                }
                PeriodDay periodDay = new PeriodDay();
                periodDay.messageObject = new MessageObject(this.currentAccount, tLRPC$TL_messages_searchResultsCalendar.messages.get(i), false, false);
                int i3 = this.startOffset + tLRPC$TL_messages_searchResultsCalendar.periods.get(i).count;
                this.startOffset = i3;
                periodDay.startOffset = i3;
                sparseArray.put(calendar.get(5) - 1, periodDay);
                int i4 = this.minMontYear;
                if (i2 < i4 || i4 == 0) {
                    this.minMontYear = i2;
                }
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
            this.adapter.notifyDataSetChanged();
            resumeDelayedFragmentAnimation();
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
            MediaCalendarActivity mediaCalendarActivity = MediaCalendarActivity.this;
            int i2 = mediaCalendarActivity.startFromYear - (i / 12);
            int i3 = mediaCalendarActivity.startFromMonth - (i % 12);
            if (i3 < 0) {
                i3 += 12;
                i2--;
            }
            monthView.setDate(i2, i3, mediaCalendarActivity.messagesByYearMounth.get((i2 * 100) + i3), monthView.currentYear == i2 && monthView.currentMonthInYear == i3);
        }

        public int getItemCount() {
            return MediaCalendarActivity.this.monthCount;
        }
    }

    private class MonthView extends FrameLayout {
        int cellCount;
        int currentMonthInYear;
        int currentYear;
        int daysInMonth;
        SparseArray<ImageReceiver> imagesByDays = new SparseArray<>();
        SparseArray<PeriodDay> messagesByDays = new SparseArray<>();
        boolean pressed;
        float pressedX;
        float pressedY;
        int startDayOfWeek;
        int startMonthTime;
        SimpleTextView titleView;

        public MonthView(Context context) {
            super(context);
            new SparseArray();
            new SparseArray();
            setWillNotDraw(false);
            SimpleTextView simpleTextView = new SimpleTextView(context);
            this.titleView = simpleTextView;
            simpleTextView.setTextSize(15);
            this.titleView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            this.titleView.setGravity(17);
            this.titleView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            addView(this.titleView, LayoutHelper.createFrame(-1, 28.0f, 0, 0.0f, 12.0f, 0.0f, 4.0f));
        }

        public void setDate(int i, int i2, SparseArray<PeriodDay> sparseArray, boolean z) {
            ImageReceiver imageReceiver;
            int i3 = i;
            int i4 = i2;
            SparseArray<PeriodDay> sparseArray2 = sparseArray;
            this.currentYear = i3;
            this.currentMonthInYear = i4;
            this.messagesByDays = sparseArray2;
            if (this.imagesByDays != null) {
                for (int i5 = 0; i5 < this.imagesByDays.size(); i5++) {
                    this.imagesByDays.valueAt(i5).onDetachedFromWindow();
                    this.imagesByDays.valueAt(i5).setParentView((View) null);
                }
                this.imagesByDays = null;
            }
            if (sparseArray2 != null) {
                this.imagesByDays = new SparseArray<>();
                for (int i6 = 0; i6 < sparseArray.size(); i6++) {
                    int keyAt = sparseArray2.keyAt(i6);
                    ImageReceiver imageReceiver2 = new ImageReceiver();
                    imageReceiver2.setParentView(this);
                    MessageObject messageObject = sparseArray2.get(keyAt).messageObject;
                    if (messageObject != null) {
                        if (messageObject.isVideo()) {
                            TLRPC$Document document = messageObject.getDocument();
                            TLRPC$PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(document.thumbs, 50);
                            TLRPC$PhotoSize closestPhotoSizeWithSize2 = FileLoader.getClosestPhotoSizeWithSize(document.thumbs, 320);
                            if (closestPhotoSizeWithSize == closestPhotoSizeWithSize2) {
                                closestPhotoSizeWithSize2 = null;
                            }
                            if (closestPhotoSizeWithSize == null) {
                                imageReceiver = imageReceiver2;
                            } else if (messageObject.strippedThumb != null) {
                                imageReceiver = imageReceiver2;
                                imageReceiver2.setImage(ImageLocation.getForDocument(closestPhotoSizeWithSize2, document), "44_44", messageObject.strippedThumb, (String) null, messageObject, 0);
                            } else {
                                imageReceiver = imageReceiver2;
                                imageReceiver.setImage(ImageLocation.getForDocument(closestPhotoSizeWithSize2, document), "44_44", ImageLocation.getForDocument(closestPhotoSizeWithSize, document), "b", (String) null, (Object) messageObject, 0);
                            }
                        } else {
                            imageReceiver = imageReceiver2;
                            MessageObject messageObject2 = messageObject;
                            TLRPC$MessageMedia tLRPC$MessageMedia = messageObject2.messageOwner.media;
                            if ((tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaPhoto) && tLRPC$MessageMedia.photo != null && !messageObject2.photoThumbs.isEmpty()) {
                                TLRPC$PhotoSize closestPhotoSizeWithSize3 = FileLoader.getClosestPhotoSizeWithSize(messageObject2.photoThumbs, 50);
                                TLRPC$PhotoSize closestPhotoSizeWithSize4 = FileLoader.getClosestPhotoSizeWithSize(messageObject2.photoThumbs, 320, false, closestPhotoSizeWithSize3, false);
                                if (messageObject2.mediaExists || DownloadController.getInstance(MediaCalendarActivity.this.currentAccount).canDownloadMedia(messageObject2)) {
                                    if (closestPhotoSizeWithSize4 == closestPhotoSizeWithSize3) {
                                        closestPhotoSizeWithSize3 = null;
                                    }
                                    if (messageObject2.strippedThumb != null) {
                                        imageReceiver.setImage(ImageLocation.getForObject(closestPhotoSizeWithSize4, messageObject2.photoThumbsObject), "44_44", (ImageLocation) null, (String) null, messageObject2.strippedThumb, closestPhotoSizeWithSize4 != null ? closestPhotoSizeWithSize4.size : 0, (String) null, messageObject2, messageObject2.shouldEncryptPhotoOrVideo() ? 2 : 1);
                                    } else {
                                        imageReceiver.setImage(ImageLocation.getForObject(closestPhotoSizeWithSize4, messageObject2.photoThumbsObject), "44_44", ImageLocation.getForObject(closestPhotoSizeWithSize3, messageObject2.photoThumbsObject), "b", closestPhotoSizeWithSize4 != null ? closestPhotoSizeWithSize4.size : 0, (String) null, messageObject2, messageObject2.shouldEncryptPhotoOrVideo() ? 2 : 1);
                                    }
                                } else {
                                    BitmapDrawable bitmapDrawable = messageObject2.strippedThumb;
                                    if (bitmapDrawable != null) {
                                        imageReceiver.setImage((ImageLocation) null, (String) null, bitmapDrawable, (String) null, messageObject2, 0);
                                    } else {
                                        imageReceiver.setImage((ImageLocation) null, (String) null, ImageLocation.getForObject(closestPhotoSizeWithSize3, messageObject2.photoThumbsObject), "b", (String) null, (Object) messageObject2, 0);
                                    }
                                }
                            }
                        }
                        ImageReceiver imageReceiver3 = imageReceiver;
                        imageReceiver3.setRoundRadius(AndroidUtilities.dp(22.0f));
                        this.imagesByDays.put(keyAt, imageReceiver3);
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
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int i, int i2) {
            super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp((float) ((this.cellCount * 52) + 44)), NUM));
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            if (motionEvent.getAction() == 0) {
                this.pressed = true;
                this.pressedX = motionEvent.getX();
                this.pressedY = motionEvent.getY();
            } else if (motionEvent.getAction() == 1) {
                if (this.pressed) {
                    int i = 0;
                    while (true) {
                        if (i < this.imagesByDays.size()) {
                            if (this.imagesByDays.valueAt(i).getDrawRegion().contains(this.pressedX, this.pressedY) && MediaCalendarActivity.this.callback != null) {
                                PeriodDay valueAt = this.messagesByDays.valueAt(i);
                                MediaCalendarActivity.this.callback.onDateSelected(valueAt.messageObject.getId(), valueAt.startOffset);
                                MediaCalendarActivity.this.finishFragment();
                                break;
                            }
                            i++;
                        } else {
                            break;
                        }
                    }
                }
                this.pressed = false;
            } else if (motionEvent.getAction() == 3) {
                this.pressed = false;
            }
            return this.pressed;
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            float f;
            float f2;
            float f3;
            Canvas canvas2 = canvas;
            super.onDraw(canvas);
            int i = this.startDayOfWeek;
            float measuredWidth = ((float) getMeasuredWidth()) / 7.0f;
            float dp = (float) AndroidUtilities.dp(52.0f);
            int i2 = 0;
            int i3 = 0;
            while (i2 < this.daysInMonth) {
                float f4 = (((float) i) * measuredWidth) + (measuredWidth / 2.0f);
                float dp2 = (((float) i3) * dp) + (dp / 2.0f) + ((float) AndroidUtilities.dp(44.0f));
                int i4 = i2 + 1;
                if (((int) (System.currentTimeMillis() / 1000)) < this.startMonthTime + (86400 * i4)) {
                    int alpha = MediaCalendarActivity.this.textPaint.getAlpha();
                    MediaCalendarActivity.this.textPaint.setAlpha((int) (((float) alpha) * 0.3f));
                    canvas2.drawText(Integer.toString(i4), f4, dp2 + ((float) AndroidUtilities.dp(5.0f)), MediaCalendarActivity.this.textPaint);
                    MediaCalendarActivity.this.textPaint.setAlpha(alpha);
                    f = measuredWidth;
                } else {
                    SparseArray<PeriodDay> sparseArray = this.messagesByDays;
                    if (sparseArray == null || sparseArray.get(i2, (Object) null) == null) {
                        f = measuredWidth;
                        canvas2.drawText(Integer.toString(i4), f4, dp2 + ((float) AndroidUtilities.dp(5.0f)), MediaCalendarActivity.this.textPaint);
                    } else {
                        if (this.imagesByDays.get(i2) != null) {
                            if (MediaCalendarActivity.this.checkEnterItems && !this.messagesByDays.get(i2).wasDrawn) {
                                this.messagesByDays.get(i2).enterAlpha = 0.0f;
                                this.messagesByDays.get(i2).startEnterDelay = 150.0f * ((dp2 + getY()) / ((float) MediaCalendarActivity.this.listView.getMeasuredHeight()));
                            }
                            if (this.messagesByDays.get(i2).startEnterDelay > 0.0f) {
                                this.messagesByDays.get(i2).startEnterDelay -= 16.0f;
                                if (this.messagesByDays.get(i2).startEnterDelay < 0.0f) {
                                    this.messagesByDays.get(i2).startEnterDelay = 0.0f;
                                } else {
                                    invalidate();
                                }
                            }
                            if (this.messagesByDays.get(i2).startEnterDelay == 0.0f && this.messagesByDays.get(i2).enterAlpha != 1.0f) {
                                this.messagesByDays.get(i2).enterAlpha += 0.07272727f;
                                if (this.messagesByDays.get(i2).enterAlpha > 1.0f) {
                                    this.messagesByDays.get(i2).enterAlpha = 1.0f;
                                } else {
                                    invalidate();
                                }
                            }
                            f2 = this.messagesByDays.get(i2).enterAlpha;
                            this.imagesByDays.get(i2).setAlpha(this.messagesByDays.get(i2).enterAlpha);
                            f = measuredWidth;
                            this.imagesByDays.get(i2).setImageCoords(f4 - (((float) AndroidUtilities.dp(44.0f)) / 2.0f), dp2 - (((float) AndroidUtilities.dp(44.0f)) / 2.0f), (float) AndroidUtilities.dp(44.0f), (float) AndroidUtilities.dp(44.0f));
                            this.imagesByDays.get(i2).draw(canvas2);
                            MediaCalendarActivity.this.blackoutPaint.setColor(ColorUtils.setAlphaComponent(-16777216, (int) (this.messagesByDays.get(i2).enterAlpha * 80.0f)));
                            canvas2.drawCircle(f4, dp2, ((float) AndroidUtilities.dp(44.0f)) / 2.0f, MediaCalendarActivity.this.blackoutPaint);
                            this.messagesByDays.get(i2).wasDrawn = true;
                            f3 = 1.0f;
                        } else {
                            f = measuredWidth;
                            f3 = 1.0f;
                            f2 = 1.0f;
                        }
                        if (f2 != f3) {
                            int alpha2 = MediaCalendarActivity.this.textPaint.getAlpha();
                            MediaCalendarActivity.this.textPaint.setAlpha((int) (((float) alpha2) * (f3 - f2)));
                            canvas2.drawText(Integer.toString(i4), f4, ((float) AndroidUtilities.dp(5.0f)) + dp2, MediaCalendarActivity.this.textPaint);
                            MediaCalendarActivity.this.textPaint.setAlpha(alpha2);
                            int alpha3 = MediaCalendarActivity.this.textPaint.getAlpha();
                            MediaCalendarActivity.this.activeTextPaint.setAlpha((int) (((float) alpha3) * f2));
                            canvas2.drawText(Integer.toString(i4), f4, dp2 + ((float) AndroidUtilities.dp(5.0f)), MediaCalendarActivity.this.activeTextPaint);
                            MediaCalendarActivity.this.activeTextPaint.setAlpha(alpha3);
                        } else {
                            canvas2.drawText(Integer.toString(i4), f4, dp2 + ((float) AndroidUtilities.dp(5.0f)), MediaCalendarActivity.this.activeTextPaint);
                        }
                    }
                }
                i++;
                if (i >= 7) {
                    i3++;
                    i = 0;
                }
                i2 = i4;
                measuredWidth = f;
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

    public void setCallback(Callback callback2) {
        this.callback = callback2;
    }

    private class PeriodDay {
        float enterAlpha;
        MessageObject messageObject;
        float startEnterDelay;
        int startOffset;
        boolean wasDrawn;

        private PeriodDay(MediaCalendarActivity mediaCalendarActivity) {
            this.enterAlpha = 1.0f;
            this.startEnterDelay = 1.0f;
        }
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        AnonymousClass5 r8 = new ThemeDescription.ThemeDescriptionDelegate() {
            public /* synthetic */ void onAnimationProgress(float f) {
                ThemeDescription.ThemeDescriptionDelegate.CC.$default$onAnimationProgress(this, f);
            }

            public void didSetColor() {
                MediaCalendarActivity.this.updateColors();
            }
        };
        new ArrayList();
        AnonymousClass5 r6 = r8;
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
}
