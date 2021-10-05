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
import org.telegram.tgnet.TLRPC$MessageMedia;
import org.telegram.tgnet.TLRPC$PhotoSize;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_inputMessagesFilterPhotos;
import org.telegram.tgnet.TLRPC$TL_messageMediaPhoto;
import org.telegram.tgnet.TLRPC$TL_messages_getSearchResultsCalendar;
import org.telegram.tgnet.TLRPC$TL_messages_searchResultsCalendar;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;

public class MediaCalendarActivity extends BaseFragment {
    TextPaint activeTextPaint = new TextPaint(1);
    CalendarAdapter adapter;
    Paint blackoutPaint = new Paint(1);
    Callback callback;
    FrameLayout contentView;
    private long dialogId;
    LinearLayoutManager layoutManager;
    RecyclerListView listView;
    SparseArray<SparseArray<PeriodDay>> messagesByYearMounth = new SparseArray<>();
    int monthCount;
    int startFromMonth;
    int startFromYear;
    TextPaint textPaint = new TextPaint(1);

    public interface Callback {
        void onDateSelected(int i, int i2);
    }

    public MediaCalendarActivity(Bundle bundle) {
        super(bundle);
    }

    public boolean onFragmentCreate() {
        this.dialogId = getArguments().getLong("dialog_id");
        return super.onFragmentCreate();
    }

    public View createView(Context context) {
        this.blackoutPaint.setColor(ColorUtils.setAlphaComponent(-16777216, 80));
        this.textPaint.setTextSize((float) AndroidUtilities.dp(16.0f));
        this.textPaint.setTextAlign(Paint.Align.CENTER);
        this.activeTextPaint.setTextSize((float) AndroidUtilities.dp(16.0f));
        this.activeTextPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.activeTextPaint.setTextAlign(Paint.Align.CENTER);
        this.contentView = new FrameLayout(context);
        createActionBar(context);
        this.contentView.addView(this.actionBar);
        this.actionBar.setTitle(LocaleController.getString("Calendar", NUM));
        this.actionBar.setCastShadows(false);
        RecyclerListView recyclerListView = new RecyclerListView(context);
        this.listView = recyclerListView;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        this.layoutManager = linearLayoutManager;
        recyclerListView.setLayoutManager(linearLayoutManager);
        this.layoutManager.setReverseLayout(true);
        RecyclerListView recyclerListView2 = this.listView;
        CalendarAdapter calendarAdapter = new CalendarAdapter();
        this.adapter = calendarAdapter;
        recyclerListView2.setAdapter(calendarAdapter);
        this.contentView.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f, 0, 0.0f, 36.0f, 0.0f, 0.0f));
        final String[] strArr = {"M", "T", "W", "T", "F", "S", "S"};
        final Drawable mutate = ContextCompat.getDrawable(context, NUM).mutate();
        this.contentView.addView(new View(context) {
            /* access modifiers changed from: protected */
            public void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                float measuredWidth = ((float) getMeasuredWidth()) / 7.0f;
                for (int i = 0; i < 7; i++) {
                    canvas.drawText(strArr[i], (((float) i) * measuredWidth) + (measuredWidth / 2.0f), (((float) (getMeasuredHeight() - AndroidUtilities.dp(2.0f))) / 2.0f) + ((float) AndroidUtilities.dp(5.0f)), MediaCalendarActivity.this.textPaint);
                }
                mutate.setBounds(0, getMeasuredHeight() - AndroidUtilities.dp(3.0f), getMeasuredWidth(), getMeasuredHeight());
                mutate.draw(canvas);
            }
        }, LayoutHelper.createFrame(-1, 38.0f, 0, 0.0f, 0.0f, 0.0f, 0.0f));
        this.actionBar.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        this.activeTextPaint.setColor(-1);
        this.textPaint.setColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.actionBar.setTitleColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setItemsColor(Theme.getColor("windowBackgroundWhiteBlackText"), false);
        this.actionBar.setItemsBackgroundColor(Theme.getColor("listSelectorSDK21"), false);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    MediaCalendarActivity.this.finishFragment();
                }
            }
        });
        this.fragmentView = this.contentView;
        TLRPC$TL_messages_getSearchResultsCalendar tLRPC$TL_messages_getSearchResultsCalendar = new TLRPC$TL_messages_getSearchResultsCalendar();
        tLRPC$TL_messages_getSearchResultsCalendar.filter = new TLRPC$TL_inputMessagesFilterPhotos();
        tLRPC$TL_messages_getSearchResultsCalendar.by_months = false;
        tLRPC$TL_messages_getSearchResultsCalendar.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(this.dialogId);
        getConnectionsManager().sendRequest(tLRPC$TL_messages_getSearchResultsCalendar, new MediaCalendarActivity$$ExternalSyntheticLambda1(this));
        return this.fragmentView;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$1(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new MediaCalendarActivity$$ExternalSyntheticLambda0(this, tLObject));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$0(TLObject tLObject) {
        TLRPC$TL_messages_searchResultsCalendar tLRPC$TL_messages_searchResultsCalendar = (TLRPC$TL_messages_searchResultsCalendar) tLObject;
        Calendar instance = Calendar.getInstance();
        this.startFromYear = instance.get(1);
        this.startFromMonth = instance.get(2);
        this.monthCount = ((int) (((instance.getTimeInMillis() / 1000) - ((long) tLRPC$TL_messages_searchResultsCalendar.min_date)) / 2629800)) + 2;
        int i = 0;
        for (int i2 = 0; i2 < tLRPC$TL_messages_searchResultsCalendar.periods.size(); i2++) {
            instance.setTimeInMillis(((long) tLRPC$TL_messages_searchResultsCalendar.periods.get(i2).date) * 1000);
            int i3 = (instance.get(1) * 100) + instance.get(2);
            SparseArray sparseArray = this.messagesByYearMounth.get(i3);
            if (sparseArray == null) {
                sparseArray = new SparseArray();
                this.messagesByYearMounth.put(i3, sparseArray);
            }
            PeriodDay periodDay = new PeriodDay();
            periodDay.messageObject = new MessageObject(this.currentAccount, tLRPC$TL_messages_searchResultsCalendar.messages.get(i2), false, false);
            periodDay.startOffset = i;
            i += tLRPC$TL_messages_searchResultsCalendar.periods.get(i2).count;
            sparseArray.put(instance.get(5) - 1, periodDay);
        }
        this.adapter.notifyDataSetChanged();
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
            monthView.setDate(i2, i3, mediaCalendarActivity.messagesByYearMounth.get((i2 * 100) + i3));
        }

        public int getItemCount() {
            return MediaCalendarActivity.this.monthCount;
        }
    }

    private class MonthView extends FrameLayout {
        int cellCount;
        int daysInMonth;
        SparseArray<ImageReceiver> imagesByDays = new SparseArray<>();
        SparseArray<PeriodDay> messagesByDays = new SparseArray<>();
        boolean pressed;
        float pressedX;
        float pressedY;
        int startDayOfWeek;
        SimpleTextView titleView;

        public MonthView(Context context) {
            super(context);
            setWillNotDraw(false);
            SimpleTextView simpleTextView = new SimpleTextView(context);
            this.titleView = simpleTextView;
            simpleTextView.setTextSize(15);
            this.titleView.setGravity(17);
            addView(this.titleView, LayoutHelper.createFrame(-1, 28.0f, 0, 0.0f, 12.0f, 0.0f, 4.0f));
        }

        public void setDate(int i, int i2, SparseArray<PeriodDay> sparseArray) {
            ImageReceiver imageReceiver;
            int i3 = i;
            int i4 = i2;
            SparseArray<PeriodDay> sparseArray2 = sparseArray;
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
            int i7 = i4 + 1;
            this.daysInMonth = YearMonth.of(i3, i7).lengthOfMonth();
            Calendar instance = Calendar.getInstance();
            instance.set(i3, i4, 0);
            int i8 = (instance.get(7) + 6) % 7;
            this.startDayOfWeek = i8;
            int i9 = this.daysInMonth + i8;
            this.cellCount = ((int) (((float) i9) / 7.0f)) + (i9 % 7 == 0 ? 0 : 1);
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
            Canvas canvas2 = canvas;
            super.onDraw(canvas);
            int i = this.startDayOfWeek;
            float measuredWidth = ((float) getMeasuredWidth()) / 7.0f;
            float dp = (float) AndroidUtilities.dp(52.0f);
            int i2 = 0;
            for (int i3 = 0; i3 < this.daysInMonth; i3++) {
                float f = (((float) i) * measuredWidth) + (measuredWidth / 2.0f);
                float dp2 = (((float) i2) * dp) + (dp / 2.0f) + ((float) AndroidUtilities.dp(44.0f));
                SparseArray<PeriodDay> sparseArray = this.messagesByDays;
                if (sparseArray == null || sparseArray.get(i3, (Object) null) == null) {
                    canvas2.drawText(Integer.toString(i3 + 1), f, dp2 + ((float) AndroidUtilities.dp(5.0f)), MediaCalendarActivity.this.textPaint);
                } else {
                    this.imagesByDays.get(i3).setImageCoords(f - (((float) AndroidUtilities.dp(44.0f)) / 2.0f), dp2 - (((float) AndroidUtilities.dp(44.0f)) / 2.0f), (float) AndroidUtilities.dp(44.0f), (float) AndroidUtilities.dp(44.0f));
                    this.imagesByDays.get(i3).draw(canvas2);
                    canvas2.drawCircle(f, dp2, ((float) AndroidUtilities.dp(44.0f)) / 2.0f, MediaCalendarActivity.this.blackoutPaint);
                    canvas2.drawText(Integer.toString(i3 + 1), f, dp2 + ((float) AndroidUtilities.dp(5.0f)), MediaCalendarActivity.this.activeTextPaint);
                }
                i++;
                if (i >= 7) {
                    i2++;
                    i = 0;
                }
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
        MessageObject messageObject;
        int startOffset;

        private PeriodDay(MediaCalendarActivity mediaCalendarActivity) {
        }
    }
}
