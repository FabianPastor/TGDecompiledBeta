package org.telegram.ui;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LayoutAnimationController;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import androidx.annotation.Keep;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.time.SunDate;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.BrightnessControlCell;
import org.telegram.ui.Cells.ChatListCell;
import org.telegram.ui.Cells.ChatMessageCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.NotificationsCheckCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Cells.ThemePreviewMessagesCell;
import org.telegram.ui.Cells.ThemeTypeCell;
import org.telegram.ui.Cells.ThemesHorizontalListCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.SeekBarView;
import org.telegram.ui.Components.ShareAlert;
import org.telegram.ui.ThemeActivity;

public class ThemeActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    public static final int THEME_TYPE_BASIC = 0;
    public static final int THEME_TYPE_NIGHT = 1;
    public static final int THEME_TYPE_OTHER = 2;
    private static final int create_theme = 1;
    private static final int edit_theme = 3;
    private static final int reset_settings = 4;
    private static final int share_theme = 2;
    /* access modifiers changed from: private */
    public int automaticBrightnessInfoRow;
    /* access modifiers changed from: private */
    public int automaticBrightnessRow;
    /* access modifiers changed from: private */
    public int automaticHeaderRow;
    /* access modifiers changed from: private */
    public int backgroundRow;
    /* access modifiers changed from: private */
    public int bubbleRadiusHeaderRow;
    /* access modifiers changed from: private */
    public int bubbleRadiusInfoRow;
    /* access modifiers changed from: private */
    public int bubbleRadiusRow;
    /* access modifiers changed from: private */
    public int chatListHeaderRow;
    /* access modifiers changed from: private */
    public int chatListInfoRow;
    /* access modifiers changed from: private */
    public int chatListRow;
    /* access modifiers changed from: private */
    public int contactsReimportRow;
    /* access modifiers changed from: private */
    public int contactsSortRow;
    /* access modifiers changed from: private */
    public int currentType;
    /* access modifiers changed from: private */
    public int customTabsRow;
    /* access modifiers changed from: private */
    public ArrayList<Theme.ThemeInfo> darkThemes = new ArrayList<>();
    /* access modifiers changed from: private */
    public ArrayList<Theme.ThemeInfo> defaultThemes = new ArrayList<>();
    /* access modifiers changed from: private */
    public int directShareRow;
    /* access modifiers changed from: private */
    public int distanceRow;
    /* access modifiers changed from: private */
    public int emojiRow;
    /* access modifiers changed from: private */
    public int enableAnimationsRow;
    private GpsLocationListener gpsLocationListener = new GpsLocationListener();
    boolean hasThemeAccents;
    private LinearLayoutManager layoutManager;
    /* access modifiers changed from: private */
    public ListAdapter listAdapter;
    /* access modifiers changed from: private */
    public RecyclerListView listView;
    private ActionBarMenuItem menuItem;
    private GpsLocationListener networkLocationListener = new GpsLocationListener();
    /* access modifiers changed from: private */
    public int newThemeInfoRow;
    /* access modifiers changed from: private */
    public int nightAutomaticRow;
    /* access modifiers changed from: private */
    public int nightDisabledRow;
    /* access modifiers changed from: private */
    public int nightScheduledRow;
    /* access modifiers changed from: private */
    public int nightSystemDefaultRow;
    /* access modifiers changed from: private */
    public int nightThemeRow;
    /* access modifiers changed from: private */
    public int nightTypeInfoRow;
    /* access modifiers changed from: private */
    public int preferedHeaderRow;
    private boolean previousByLocation;
    private int previousUpdatedType;
    /* access modifiers changed from: private */
    public int raiseToSpeakRow;
    /* access modifiers changed from: private */
    public int rowCount;
    /* access modifiers changed from: private */
    public int saveToGalleryRow;
    /* access modifiers changed from: private */
    public int scheduleFromRow;
    /* access modifiers changed from: private */
    public int scheduleFromToInfoRow;
    /* access modifiers changed from: private */
    public int scheduleHeaderRow;
    /* access modifiers changed from: private */
    public int scheduleLocationInfoRow;
    /* access modifiers changed from: private */
    public int scheduleLocationRow;
    /* access modifiers changed from: private */
    public int scheduleToRow;
    /* access modifiers changed from: private */
    public int scheduleUpdateLocationRow;
    /* access modifiers changed from: private */
    public int sendByEnterRow;
    /* access modifiers changed from: private */
    public int settings2Row;
    /* access modifiers changed from: private */
    public int settingsRow;
    private Theme.ThemeAccent sharingAccent;
    private AlertDialog sharingProgressDialog;
    private Theme.ThemeInfo sharingTheme;
    /* access modifiers changed from: private */
    public int stickersRow;
    /* access modifiers changed from: private */
    public int stickersSection2Row;
    /* access modifiers changed from: private */
    public int textSizeHeaderRow;
    /* access modifiers changed from: private */
    public int textSizeRow;
    /* access modifiers changed from: private */
    public int themeAccentListRow;
    /* access modifiers changed from: private */
    public int themeHeaderRow;
    /* access modifiers changed from: private */
    public int themeInfoRow;
    /* access modifiers changed from: private */
    public int themeListRow;
    /* access modifiers changed from: private */
    public ThemesHorizontalListCell themesHorizontalListCell;
    private boolean updatingLocation;

    public interface SizeChooseViewDelegate {
        void onSizeChanged();
    }

    private class GpsLocationListener implements LocationListener {
        public void onProviderDisabled(String str) {
        }

        public void onProviderEnabled(String str) {
        }

        public void onStatusChanged(String str, int i, Bundle bundle) {
        }

        private GpsLocationListener() {
        }

        public void onLocationChanged(Location location) {
            if (location != null) {
                ThemeActivity.this.stopLocationUpdate();
                ThemeActivity.this.updateSunTime(location, false);
            }
        }
    }

    private class TextSizeCell extends FrameLayout {
        /* access modifiers changed from: private */
        public int endFontSize = 30;
        private int lastWidth;
        /* access modifiers changed from: private */
        public ThemePreviewMessagesCell messagesCell;
        private SeekBarView sizeBar;
        /* access modifiers changed from: private */
        public int startFontSize = 12;
        private TextPaint textPaint;

        public TextSizeCell(Context context) {
            super(context);
            setWillNotDraw(false);
            this.textPaint = new TextPaint(1);
            this.textPaint.setTextSize((float) AndroidUtilities.dp(16.0f));
            this.sizeBar = new SeekBarView(context);
            this.sizeBar.setReportChanges(true);
            this.sizeBar.setDelegate(new SeekBarView.SeekBarViewDelegate(ThemeActivity.this) {
                public void onSeekBarPressed(boolean z) {
                }

                public void onSeekBarDrag(boolean z, float f) {
                    TextSizeCell textSizeCell = TextSizeCell.this;
                    boolean unused = ThemeActivity.this.setFontSize(Math.round(((float) textSizeCell.startFontSize) + (((float) (TextSizeCell.this.endFontSize - TextSizeCell.this.startFontSize)) * f)));
                }
            });
            addView(this.sizeBar, LayoutHelper.createFrame(-1, 38.0f, 51, 5.0f, 5.0f, 39.0f, 0.0f));
            this.messagesCell = new ThemePreviewMessagesCell(context, ThemeActivity.this.parentLayout, 0);
            addView(this.messagesCell, LayoutHelper.createFrame(-1, -2.0f, 51, 0.0f, 53.0f, 0.0f, 0.0f));
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            this.textPaint.setColor(Theme.getColor("windowBackgroundWhiteValueText"));
            canvas.drawText("" + SharedConfig.fontSize, (float) (getMeasuredWidth() - AndroidUtilities.dp(39.0f)), (float) AndroidUtilities.dp(28.0f), this.textPaint);
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int i, int i2) {
            super.onMeasure(i, i2);
            int size = View.MeasureSpec.getSize(i);
            if (this.lastWidth != size) {
                SeekBarView seekBarView = this.sizeBar;
                int i3 = SharedConfig.fontSize;
                int i4 = this.startFontSize;
                seekBarView.setProgress(((float) (i3 - i4)) / ((float) (this.endFontSize - i4)));
                this.lastWidth = size;
            }
        }

        public void invalidate() {
            super.invalidate();
            this.messagesCell.invalidate();
            this.sizeBar.invalidate();
        }
    }

    private class BubbleRadiusCell extends FrameLayout {
        /* access modifiers changed from: private */
        public int endRadius = 17;
        private SeekBarView sizeBar;
        /* access modifiers changed from: private */
        public int startRadius = 0;
        private TextPaint textPaint;

        public BubbleRadiusCell(Context context) {
            super(context);
            setWillNotDraw(false);
            this.textPaint = new TextPaint(1);
            this.textPaint.setTextSize((float) AndroidUtilities.dp(16.0f));
            this.sizeBar = new SeekBarView(context);
            this.sizeBar.setReportChanges(true);
            this.sizeBar.setDelegate(new SeekBarView.SeekBarViewDelegate(ThemeActivity.this) {
                public void onSeekBarPressed(boolean z) {
                }

                public void onSeekBarDrag(boolean z, float f) {
                    BubbleRadiusCell bubbleRadiusCell = BubbleRadiusCell.this;
                    boolean unused = ThemeActivity.this.setBubbleRadius(Math.round(((float) bubbleRadiusCell.startRadius) + (((float) (BubbleRadiusCell.this.endRadius - BubbleRadiusCell.this.startRadius)) * f)), false);
                }
            });
            addView(this.sizeBar, LayoutHelper.createFrame(-1, 38.0f, 51, 5.0f, 5.0f, 39.0f, 0.0f));
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            this.textPaint.setColor(Theme.getColor("windowBackgroundWhiteValueText"));
            canvas.drawText("" + SharedConfig.bubbleRadius, (float) (getMeasuredWidth() - AndroidUtilities.dp(39.0f)), (float) AndroidUtilities.dp(28.0f), this.textPaint);
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int i, int i2) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM), i2);
            SeekBarView seekBarView = this.sizeBar;
            int i3 = SharedConfig.bubbleRadius;
            int i4 = this.startRadius;
            seekBarView.setProgress(((float) (i3 - i4)) / ((float) (this.endRadius - i4)));
        }

        public void invalidate() {
            super.invalidate();
            this.sizeBar.invalidate();
        }
    }

    public ThemeActivity(int i) {
        this.currentType = i;
        updateRows(true);
    }

    /* access modifiers changed from: private */
    public boolean setBubbleRadius(int i, boolean z) {
        if (i == SharedConfig.bubbleRadius) {
            return false;
        }
        SharedConfig.bubbleRadius = i;
        SharedPreferences.Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putInt("bubbleRadius", SharedConfig.bubbleRadius);
        edit.commit();
        RecyclerView.ViewHolder findViewHolderForAdapterPosition = this.listView.findViewHolderForAdapterPosition(this.textSizeRow);
        if (findViewHolderForAdapterPosition != null) {
            View view = findViewHolderForAdapterPosition.itemView;
            if (view instanceof TextSizeCell) {
                TextSizeCell textSizeCell = (TextSizeCell) view;
                ChatMessageCell[] cells = textSizeCell.messagesCell.getCells();
                for (int i2 = 0; i2 < cells.length; i2++) {
                    cells[i2].getMessageObject().resetLayout();
                    cells[i2].requestLayout();
                }
                textSizeCell.invalidate();
            }
        }
        RecyclerView.ViewHolder findViewHolderForAdapterPosition2 = this.listView.findViewHolderForAdapterPosition(this.bubbleRadiusRow);
        if (findViewHolderForAdapterPosition2 != null) {
            View view2 = findViewHolderForAdapterPosition2.itemView;
            if (view2 instanceof BubbleRadiusCell) {
                BubbleRadiusCell bubbleRadiusCell = (BubbleRadiusCell) view2;
                if (z) {
                    bubbleRadiusCell.requestLayout();
                } else {
                    bubbleRadiusCell.invalidate();
                }
            }
        }
        updateMenuItem();
        return true;
    }

    /* access modifiers changed from: private */
    public boolean setFontSize(int i) {
        if (i == SharedConfig.fontSize) {
            return false;
        }
        SharedConfig.fontSize = i;
        SharedPreferences.Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putInt("fons_size", SharedConfig.fontSize);
        edit.commit();
        Theme.chat_msgTextPaint.setTextSize((float) AndroidUtilities.dp((float) SharedConfig.fontSize));
        RecyclerView.ViewHolder findViewHolderForAdapterPosition = this.listView.findViewHolderForAdapterPosition(this.textSizeRow);
        if (findViewHolderForAdapterPosition != null) {
            View view = findViewHolderForAdapterPosition.itemView;
            if (view instanceof TextSizeCell) {
                ChatMessageCell[] cells = ((TextSizeCell) view).messagesCell.getCells();
                for (int i2 = 0; i2 < cells.length; i2++) {
                    cells[i2].getMessageObject().resetLayout();
                    cells[i2].requestLayout();
                }
            }
        }
        updateMenuItem();
        return true;
    }

    /* access modifiers changed from: private */
    public void updateRows(boolean z) {
        int i;
        int i2;
        int i3;
        TLRPC.TL_theme tL_theme;
        int i4 = this.rowCount;
        int i5 = this.themeAccentListRow;
        this.rowCount = 0;
        this.emojiRow = -1;
        this.contactsReimportRow = -1;
        this.contactsSortRow = -1;
        this.scheduleLocationRow = -1;
        this.scheduleUpdateLocationRow = -1;
        this.scheduleLocationInfoRow = -1;
        this.nightDisabledRow = -1;
        this.nightScheduledRow = -1;
        this.nightAutomaticRow = -1;
        this.nightSystemDefaultRow = -1;
        this.nightTypeInfoRow = -1;
        this.scheduleHeaderRow = -1;
        this.nightThemeRow = -1;
        this.newThemeInfoRow = -1;
        this.scheduleFromRow = -1;
        this.scheduleToRow = -1;
        this.scheduleFromToInfoRow = -1;
        this.themeListRow = -1;
        this.themeAccentListRow = -1;
        this.themeInfoRow = -1;
        this.preferedHeaderRow = -1;
        this.automaticHeaderRow = -1;
        this.automaticBrightnessRow = -1;
        this.automaticBrightnessInfoRow = -1;
        this.textSizeHeaderRow = -1;
        this.themeHeaderRow = -1;
        this.bubbleRadiusHeaderRow = -1;
        this.bubbleRadiusRow = -1;
        this.bubbleRadiusInfoRow = -1;
        this.chatListHeaderRow = -1;
        this.chatListRow = -1;
        this.chatListInfoRow = -1;
        this.textSizeRow = -1;
        this.backgroundRow = -1;
        this.settingsRow = -1;
        this.customTabsRow = -1;
        this.directShareRow = -1;
        this.enableAnimationsRow = -1;
        this.raiseToSpeakRow = -1;
        this.sendByEnterRow = -1;
        this.saveToGalleryRow = -1;
        this.distanceRow = -1;
        this.settings2Row = -1;
        this.stickersRow = -1;
        this.stickersSection2Row = -1;
        this.defaultThemes.clear();
        this.darkThemes.clear();
        int size = Theme.themes.size();
        for (int i6 = 0; i6 < size; i6++) {
            Theme.ThemeInfo themeInfo = Theme.themes.get(i6);
            if (this.currentType == 0 || (!themeInfo.isLight() && ((tL_theme = themeInfo.info) == null || tL_theme.document != null))) {
                if (themeInfo.pathToFile != null) {
                    this.darkThemes.add(themeInfo);
                } else {
                    this.defaultThemes.add(themeInfo);
                }
            }
        }
        Collections.sort(this.defaultThemes, $$Lambda$ThemeActivity$cs0N3OVBAa2T6bewE_YVZMeTCA.INSTANCE);
        if (this.currentType == 0) {
            int i7 = this.rowCount;
            this.rowCount = i7 + 1;
            this.textSizeHeaderRow = i7;
            int i8 = this.rowCount;
            this.rowCount = i8 + 1;
            this.textSizeRow = i8;
            int i9 = this.rowCount;
            this.rowCount = i9 + 1;
            this.backgroundRow = i9;
            int i10 = this.rowCount;
            this.rowCount = i10 + 1;
            this.newThemeInfoRow = i10;
            int i11 = this.rowCount;
            this.rowCount = i11 + 1;
            this.themeHeaderRow = i11;
            int i12 = this.rowCount;
            this.rowCount = i12 + 1;
            this.themeListRow = i12;
            this.hasThemeAccents = Theme.getCurrentTheme().hasAccentColors();
            ThemesHorizontalListCell themesHorizontalListCell2 = this.themesHorizontalListCell;
            if (themesHorizontalListCell2 != null) {
                themesHorizontalListCell2.setDrawDivider(this.hasThemeAccents);
            }
            if (this.hasThemeAccents) {
                int i13 = this.rowCount;
                this.rowCount = i13 + 1;
                this.themeAccentListRow = i13;
            }
            int i14 = this.rowCount;
            this.rowCount = i14 + 1;
            this.themeInfoRow = i14;
            int i15 = this.rowCount;
            this.rowCount = i15 + 1;
            this.bubbleRadiusHeaderRow = i15;
            int i16 = this.rowCount;
            this.rowCount = i16 + 1;
            this.bubbleRadiusRow = i16;
            int i17 = this.rowCount;
            this.rowCount = i17 + 1;
            this.bubbleRadiusInfoRow = i17;
            int i18 = this.rowCount;
            this.rowCount = i18 + 1;
            this.chatListHeaderRow = i18;
            int i19 = this.rowCount;
            this.rowCount = i19 + 1;
            this.chatListRow = i19;
            int i20 = this.rowCount;
            this.rowCount = i20 + 1;
            this.chatListInfoRow = i20;
            int i21 = this.rowCount;
            this.rowCount = i21 + 1;
            this.settingsRow = i21;
            int i22 = this.rowCount;
            this.rowCount = i22 + 1;
            this.nightThemeRow = i22;
            int i23 = this.rowCount;
            this.rowCount = i23 + 1;
            this.customTabsRow = i23;
            int i24 = this.rowCount;
            this.rowCount = i24 + 1;
            this.directShareRow = i24;
            int i25 = this.rowCount;
            this.rowCount = i25 + 1;
            this.enableAnimationsRow = i25;
            int i26 = this.rowCount;
            this.rowCount = i26 + 1;
            this.emojiRow = i26;
            int i27 = this.rowCount;
            this.rowCount = i27 + 1;
            this.raiseToSpeakRow = i27;
            int i28 = this.rowCount;
            this.rowCount = i28 + 1;
            this.sendByEnterRow = i28;
            int i29 = this.rowCount;
            this.rowCount = i29 + 1;
            this.saveToGalleryRow = i29;
            int i30 = this.rowCount;
            this.rowCount = i30 + 1;
            this.distanceRow = i30;
            int i31 = this.rowCount;
            this.rowCount = i31 + 1;
            this.settings2Row = i31;
            int i32 = this.rowCount;
            this.rowCount = i32 + 1;
            this.stickersRow = i32;
            int i33 = this.rowCount;
            this.rowCount = i33 + 1;
            this.stickersSection2Row = i33;
        } else {
            int i34 = this.rowCount;
            this.rowCount = i34 + 1;
            this.nightDisabledRow = i34;
            int i35 = this.rowCount;
            this.rowCount = i35 + 1;
            this.nightScheduledRow = i35;
            int i36 = this.rowCount;
            this.rowCount = i36 + 1;
            this.nightAutomaticRow = i36;
            if (Build.VERSION.SDK_INT >= 29) {
                int i37 = this.rowCount;
                this.rowCount = i37 + 1;
                this.nightSystemDefaultRow = i37;
            }
            int i38 = this.rowCount;
            this.rowCount = i38 + 1;
            this.nightTypeInfoRow = i38;
            int i39 = Theme.selectedAutoNightType;
            if (i39 == 1) {
                int i40 = this.rowCount;
                this.rowCount = i40 + 1;
                this.scheduleHeaderRow = i40;
                int i41 = this.rowCount;
                this.rowCount = i41 + 1;
                this.scheduleLocationRow = i41;
                if (Theme.autoNightScheduleByLocation) {
                    int i42 = this.rowCount;
                    this.rowCount = i42 + 1;
                    this.scheduleUpdateLocationRow = i42;
                    int i43 = this.rowCount;
                    this.rowCount = i43 + 1;
                    this.scheduleLocationInfoRow = i43;
                } else {
                    int i44 = this.rowCount;
                    this.rowCount = i44 + 1;
                    this.scheduleFromRow = i44;
                    int i45 = this.rowCount;
                    this.rowCount = i45 + 1;
                    this.scheduleToRow = i45;
                    int i46 = this.rowCount;
                    this.rowCount = i46 + 1;
                    this.scheduleFromToInfoRow = i46;
                }
            } else if (i39 == 2) {
                int i47 = this.rowCount;
                this.rowCount = i47 + 1;
                this.automaticHeaderRow = i47;
                int i48 = this.rowCount;
                this.rowCount = i48 + 1;
                this.automaticBrightnessRow = i48;
                int i49 = this.rowCount;
                this.rowCount = i49 + 1;
                this.automaticBrightnessInfoRow = i49;
            }
            if (Theme.selectedAutoNightType != 0) {
                int i50 = this.rowCount;
                this.rowCount = i50 + 1;
                this.preferedHeaderRow = i50;
                int i51 = this.rowCount;
                this.rowCount = i51 + 1;
                this.themeListRow = i51;
                this.hasThemeAccents = Theme.getCurrentNightTheme().hasAccentColors();
                ThemesHorizontalListCell themesHorizontalListCell3 = this.themesHorizontalListCell;
                if (themesHorizontalListCell3 != null) {
                    themesHorizontalListCell3.setDrawDivider(this.hasThemeAccents);
                }
                if (this.hasThemeAccents) {
                    int i52 = this.rowCount;
                    this.rowCount = i52 + 1;
                    this.themeAccentListRow = i52;
                }
                int i53 = this.rowCount;
                this.rowCount = i53 + 1;
                this.themeInfoRow = i53;
            }
        }
        ListAdapter listAdapter2 = this.listAdapter;
        if (listAdapter2 != null) {
            if (this.currentType == 1 && (i2 = this.previousUpdatedType) != (i3 = Theme.selectedAutoNightType) && i2 != -1) {
                int i54 = this.nightTypeInfoRow + 1;
                int i55 = 3;
                if (i2 != i3) {
                    int i56 = 0;
                    while (i56 < 4) {
                        RecyclerListView.Holder holder = (RecyclerListView.Holder) this.listView.findViewHolderForAdapterPosition(i56);
                        if (holder != null) {
                            View view = holder.itemView;
                            if (view instanceof ThemeTypeCell) {
                                ((ThemeTypeCell) view).setTypeChecked(i56 == Theme.selectedAutoNightType);
                            }
                        }
                        i56++;
                    }
                    int i57 = Theme.selectedAutoNightType;
                    if (i57 == 0) {
                        this.listAdapter.notifyItemRangeRemoved(i54, i4 - i54);
                    } else {
                        int i58 = 5;
                        if (i57 == 1) {
                            int i59 = this.previousUpdatedType;
                            if (i59 == 0) {
                                this.listAdapter.notifyItemRangeInserted(i54, this.rowCount - i54);
                            } else if (i59 == 2) {
                                this.listAdapter.notifyItemRangeRemoved(i54, 3);
                                ListAdapter listAdapter3 = this.listAdapter;
                                if (Theme.autoNightScheduleByLocation) {
                                    i58 = 4;
                                }
                                listAdapter3.notifyItemRangeInserted(i54, i58);
                            } else if (i59 == 3) {
                                ListAdapter listAdapter4 = this.listAdapter;
                                if (Theme.autoNightScheduleByLocation) {
                                    i58 = 4;
                                }
                                listAdapter4.notifyItemRangeInserted(i54, i58);
                            }
                        } else if (i57 == 2) {
                            int i60 = this.previousUpdatedType;
                            if (i60 == 0) {
                                this.listAdapter.notifyItemRangeInserted(i54, this.rowCount - i54);
                            } else if (i60 == 1) {
                                ListAdapter listAdapter5 = this.listAdapter;
                                if (Theme.autoNightScheduleByLocation) {
                                    i58 = 4;
                                }
                                listAdapter5.notifyItemRangeRemoved(i54, i58);
                                this.listAdapter.notifyItemRangeInserted(i54, 3);
                            } else if (i60 == 3) {
                                this.listAdapter.notifyItemRangeInserted(i54, 3);
                            }
                        } else if (i57 == 3) {
                            int i61 = this.previousUpdatedType;
                            if (i61 == 0) {
                                this.listAdapter.notifyItemRangeInserted(i54, this.rowCount - i54);
                            } else if (i61 == 2) {
                                this.listAdapter.notifyItemRangeRemoved(i54, 3);
                            } else if (i61 == 1) {
                                ListAdapter listAdapter6 = this.listAdapter;
                                if (Theme.autoNightScheduleByLocation) {
                                    i58 = 4;
                                }
                                listAdapter6.notifyItemRangeRemoved(i54, i58);
                            }
                        }
                    }
                } else {
                    boolean z2 = this.previousByLocation;
                    boolean z3 = Theme.autoNightScheduleByLocation;
                    if (z2 != z3) {
                        int i62 = i54 + 2;
                        listAdapter2.notifyItemRangeRemoved(i62, z3 ? 3 : 2);
                        ListAdapter listAdapter7 = this.listAdapter;
                        if (Theme.autoNightScheduleByLocation) {
                            i55 = 2;
                        }
                        listAdapter7.notifyItemRangeInserted(i62, i55);
                    }
                }
            } else if (z || this.previousUpdatedType == -1) {
                ThemesHorizontalListCell themesHorizontalListCell4 = this.themesHorizontalListCell;
                if (themesHorizontalListCell4 != null) {
                    themesHorizontalListCell4.notifyDataSetChanged(this.listView.getWidth());
                }
                this.listAdapter.notifyDataSetChanged();
            } else if (i5 == -1 && (i = this.themeAccentListRow) != -1) {
                this.listAdapter.notifyItemInserted(i);
            } else if (i5 == -1 || this.themeAccentListRow != -1) {
                int i63 = this.themeAccentListRow;
                if (i63 != -1) {
                    this.listAdapter.notifyItemChanged(i63);
                }
            } else {
                this.listAdapter.notifyItemRemoved(i5);
            }
        }
        if (this.currentType == 1) {
            this.previousByLocation = Theme.autoNightScheduleByLocation;
            this.previousUpdatedType = Theme.selectedAutoNightType;
        }
        updateMenuItem();
    }

    public boolean onFragmentCreate() {
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.locationPermissionGranted);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didSetNewWallpapper);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.themeListUpdated);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.themeAccentListUpdated);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiDidLoad);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.needShareTheme);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.needSetDayNightTheme);
        getNotificationCenter().addObserver(this, NotificationCenter.themeUploadedToServer);
        getNotificationCenter().addObserver(this, NotificationCenter.themeUploadError);
        if (this.currentType == 0) {
            Theme.loadRemoteThemes(this.currentAccount, true);
            Theme.checkCurrentRemoteTheme(true);
        }
        return super.onFragmentCreate();
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        stopLocationUpdate();
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.locationPermissionGranted);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didSetNewWallpapper);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.themeListUpdated);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.themeAccentListUpdated);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiDidLoad);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.needShareTheme);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.needSetDayNightTheme);
        getNotificationCenter().removeObserver(this, NotificationCenter.themeUploadedToServer);
        getNotificationCenter().removeObserver(this, NotificationCenter.themeUploadError);
        Theme.saveAutoNightThemeConfig();
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        AlertDialog alertDialog;
        int i3;
        if (i == NotificationCenter.locationPermissionGranted) {
            updateSunTime((Location) null, true);
        } else if (i == NotificationCenter.didSetNewWallpapper || i == NotificationCenter.emojiDidLoad) {
            RecyclerListView recyclerListView = this.listView;
            if (recyclerListView != null) {
                recyclerListView.invalidateViews();
            }
        } else if (i == NotificationCenter.themeAccentListUpdated) {
            ListAdapter listAdapter2 = this.listAdapter;
            if (listAdapter2 != null && (i3 = this.themeAccentListRow) != -1) {
                listAdapter2.notifyItemChanged(i3, new Object());
            }
        } else if (i == NotificationCenter.themeListUpdated) {
            updateRows(true);
        } else if (i == NotificationCenter.themeUploadedToServer) {
            Theme.ThemeInfo themeInfo = objArr[0];
            Theme.ThemeAccent themeAccent = objArr[1];
            if (themeInfo == this.sharingTheme && themeAccent == this.sharingAccent) {
                StringBuilder sb = new StringBuilder();
                sb.append("https://");
                sb.append(MessagesController.getInstance(this.currentAccount).linkPrefix);
                sb.append("/addtheme/");
                sb.append((themeAccent != null ? themeAccent.info : themeInfo.info).slug);
                String sb2 = sb.toString();
                showDialog(new ShareAlert(getParentActivity(), (ArrayList<MessageObject>) null, sb2, false, sb2, false));
                AlertDialog alertDialog2 = this.sharingProgressDialog;
                if (alertDialog2 != null) {
                    alertDialog2.dismiss();
                }
            }
        } else if (i == NotificationCenter.themeUploadError) {
            Theme.ThemeInfo themeInfo2 = objArr[0];
            Theme.ThemeAccent themeAccent2 = objArr[1];
            if (themeInfo2 == this.sharingTheme && themeAccent2 == this.sharingAccent && (alertDialog = this.sharingProgressDialog) == null) {
                alertDialog.dismiss();
            }
        } else if (i == NotificationCenter.needShareTheme) {
            if (getParentActivity() != null && !this.isPaused) {
                this.sharingTheme = objArr[0];
                this.sharingAccent = objArr[1];
                this.sharingProgressDialog = new AlertDialog(getParentActivity(), 3);
                this.sharingProgressDialog.setCanCacnel(true);
                showDialog(this.sharingProgressDialog, new DialogInterface.OnDismissListener() {
                    public final void onDismiss(DialogInterface dialogInterface) {
                        ThemeActivity.this.lambda$didReceivedNotification$1$ThemeActivity(dialogInterface);
                    }
                });
            }
        } else if (i == NotificationCenter.needSetDayNightTheme) {
            updateMenuItem();
        }
    }

    public /* synthetic */ void lambda$didReceivedNotification$1$ThemeActivity(DialogInterface dialogInterface) {
        this.sharingProgressDialog = null;
        this.sharingTheme = null;
        this.sharingAccent = null;
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setAllowOverlayTitle(false);
        if (AndroidUtilities.isTablet()) {
            this.actionBar.setOccupyStatusBar(false);
        }
        if (this.currentType == 0) {
            this.actionBar.setTitle(LocaleController.getString("ChatSettings", NUM));
            this.menuItem = this.actionBar.createMenu().addItem(0, NUM);
            this.menuItem.setContentDescription(LocaleController.getString("AccDescrMoreOptions", NUM));
            this.menuItem.addSubItem(2, NUM, (CharSequence) LocaleController.getString("ShareTheme", NUM));
            this.menuItem.addSubItem(3, NUM, (CharSequence) LocaleController.getString("EditThemeColors", NUM));
            this.menuItem.addSubItem(1, NUM, (CharSequence) LocaleController.getString("CreateNewThemeMenu", NUM));
            this.menuItem.addSubItem(4, NUM, (CharSequence) LocaleController.getString("ThemeResetToDefaults", NUM));
        } else {
            this.actionBar.setTitle(LocaleController.getString("AutoNightTheme", NUM));
        }
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    ThemeActivity.this.finishFragment();
                } else if (i == 1) {
                    if (ThemeActivity.this.getParentActivity() != null) {
                        AlertDialog.Builder builder = new AlertDialog.Builder((Context) ThemeActivity.this.getParentActivity());
                        builder.setTitle(LocaleController.getString("NewTheme", NUM));
                        builder.setMessage(LocaleController.getString("CreateNewThemeAlert", NUM));
                        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                        builder.setPositiveButton(LocaleController.getString("CreateTheme", NUM), new DialogInterface.OnClickListener() {
                            public final void onClick(DialogInterface dialogInterface, int i) {
                                ThemeActivity.AnonymousClass1.this.lambda$onItemClick$0$ThemeActivity$1(dialogInterface, i);
                            }
                        });
                        ThemeActivity.this.showDialog(builder.create());
                    }
                } else if (i == 2) {
                    Theme.ThemeAccent accent = Theme.getCurrentTheme().getAccent(false);
                    if (accent.info == null) {
                        MessagesController.getInstance(ThemeActivity.this.currentAccount).saveThemeToServer(accent.parentTheme, accent);
                        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.needShareTheme, accent.parentTheme, accent);
                        return;
                    }
                    String str = "https://" + MessagesController.getInstance(ThemeActivity.this.currentAccount).linkPrefix + "/addtheme/" + accent.info.slug;
                    ThemeActivity themeActivity = ThemeActivity.this;
                    themeActivity.showDialog(new ShareAlert(themeActivity.getParentActivity(), (ArrayList<MessageObject>) null, str, false, str, false));
                } else if (i == 3) {
                    Theme.ThemeInfo currentTheme = Theme.getCurrentTheme();
                    ThemeActivity.this.presentFragment(new ThemePreviewActivity(currentTheme, false, 1, currentTheme.getAccent(false).id >= 100, ThemeActivity.this.currentType == 1));
                } else if (i == 4 && ThemeActivity.this.getParentActivity() != null) {
                    AlertDialog.Builder builder2 = new AlertDialog.Builder((Context) ThemeActivity.this.getParentActivity());
                    builder2.setTitle(LocaleController.getString("ThemeResetToDefaultsTitle", NUM));
                    builder2.setMessage(LocaleController.getString("ThemeResetToDefaultsText", NUM));
                    builder2.setPositiveButton(LocaleController.getString("Reset", NUM), new DialogInterface.OnClickListener() {
                        public final void onClick(DialogInterface dialogInterface, int i) {
                            ThemeActivity.AnonymousClass1.this.lambda$onItemClick$1$ThemeActivity$1(dialogInterface, i);
                        }
                    });
                    builder2.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                    AlertDialog create = builder2.create();
                    ThemeActivity.this.showDialog(create);
                    TextView textView = (TextView) create.getButton(-1);
                    if (textView != null) {
                        textView.setTextColor(Theme.getColor("dialogTextRed2"));
                    }
                }
            }

            public /* synthetic */ void lambda$onItemClick$0$ThemeActivity$1(DialogInterface dialogInterface, int i) {
                AlertsCreator.createThemeCreateDialog(ThemeActivity.this, 0, (Theme.ThemeInfo) null, (Theme.ThemeAccent) null);
            }

            public /* synthetic */ void lambda$onItemClick$1$ThemeActivity$1(DialogInterface dialogInterface, int i) {
                boolean access$500 = ThemeActivity.this.setFontSize(AndroidUtilities.isTablet() ? 18 : 16);
                if (ThemeActivity.this.setBubbleRadius(10, true)) {
                    access$500 = true;
                }
                if (access$500) {
                    ThemeActivity.this.listAdapter.notifyItemChanged(ThemeActivity.this.textSizeRow, new Object());
                    ThemeActivity.this.listAdapter.notifyItemChanged(ThemeActivity.this.bubbleRadiusRow, new Object());
                }
                if (ThemeActivity.this.themesHorizontalListCell != null) {
                    Theme.ThemeInfo theme = Theme.getTheme("Blue");
                    Theme.ThemeInfo currentTheme = Theme.getCurrentTheme();
                    boolean z = false;
                    if (theme != currentTheme) {
                        theme.setCurrentAccentId(Theme.DEFALT_THEME_ACCENT_ID);
                        Theme.saveThemeAccents(theme, true, false, true, false);
                        ThemeActivity.this.themesHorizontalListCell.selectTheme(theme);
                        ThemeActivity.this.themesHorizontalListCell.smoothScrollToPosition(0);
                    } else if (theme.currentAccentId != Theme.DEFALT_THEME_ACCENT_ID) {
                        NotificationCenter globalInstance = NotificationCenter.getGlobalInstance();
                        int i2 = NotificationCenter.needSetDayNightTheme;
                        Object[] objArr = new Object[4];
                        objArr[0] = currentTheme;
                        if (ThemeActivity.this.currentType == 1) {
                            z = true;
                        }
                        objArr[1] = Boolean.valueOf(z);
                        objArr[2] = null;
                        objArr[3] = Integer.valueOf(Theme.DEFALT_THEME_ACCENT_ID);
                        globalInstance.postNotificationName(i2, objArr);
                        ThemeActivity.this.listAdapter.notifyItemChanged(ThemeActivity.this.themeAccentListRow);
                    }
                }
            }
        });
        this.listAdapter = new ListAdapter(context);
        FrameLayout frameLayout = new FrameLayout(context);
        frameLayout.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
        this.fragmentView = frameLayout;
        this.listView = new RecyclerListView(context);
        RecyclerListView recyclerListView = this.listView;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, 1, false);
        this.layoutManager = linearLayoutManager;
        recyclerListView.setLayoutManager(linearLayoutManager);
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setAdapter(this.listAdapter);
        ((DefaultItemAnimator) this.listView.getItemAnimator()).setDelayAnimations(false);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListenerExtended) new RecyclerListView.OnItemClickListenerExtended() {
            public final void onItemClick(View view, int i, float f, float f2) {
                ThemeActivity.this.lambda$createView$5$ThemeActivity(view, i, f, f2);
            }
        });
        return this.fragmentView;
    }

    public /* synthetic */ void lambda$createView$5$ThemeActivity(View view, int i, float f, float f2) {
        int i2;
        int i3;
        String str;
        if (i == this.enableAnimationsRow) {
            SharedPreferences globalMainSettings = MessagesController.getGlobalMainSettings();
            boolean z = globalMainSettings.getBoolean("view_animations", true);
            SharedPreferences.Editor edit = globalMainSettings.edit();
            edit.putBoolean("view_animations", !z);
            edit.commit();
            if (view instanceof TextCheckCell) {
                ((TextCheckCell) view).setChecked(!z);
                return;
            }
            return;
        }
        boolean z2 = false;
        if (i == this.backgroundRow) {
            presentFragment(new WallpapersListActivity(0));
        } else if (i == this.sendByEnterRow) {
            SharedPreferences globalMainSettings2 = MessagesController.getGlobalMainSettings();
            boolean z3 = globalMainSettings2.getBoolean("send_by_enter", false);
            SharedPreferences.Editor edit2 = globalMainSettings2.edit();
            edit2.putBoolean("send_by_enter", !z3);
            edit2.commit();
            if (view instanceof TextCheckCell) {
                ((TextCheckCell) view).setChecked(!z3);
            }
        } else if (i == this.raiseToSpeakRow) {
            SharedConfig.toogleRaiseToSpeak();
            if (view instanceof TextCheckCell) {
                ((TextCheckCell) view).setChecked(SharedConfig.raiseToSpeak);
            }
        } else if (i == this.saveToGalleryRow) {
            SharedConfig.toggleSaveToGallery();
            if (view instanceof TextCheckCell) {
                ((TextCheckCell) view).setChecked(SharedConfig.saveToGallery);
            }
        } else if (i == this.distanceRow) {
            if (getParentActivity() != null) {
                AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
                builder.setTitle(LocaleController.getString("DistanceUnitsTitle", NUM));
                builder.setItems(new CharSequence[]{LocaleController.getString("DistanceUnitsAutomatic", NUM), LocaleController.getString("DistanceUnitsKilometers", NUM), LocaleController.getString("DistanceUnitsMiles", NUM)}, new DialogInterface.OnClickListener() {
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        ThemeActivity.this.lambda$null$2$ThemeActivity(dialogInterface, i);
                    }
                });
                builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                showDialog(builder.create());
            }
        } else if (i == this.customTabsRow) {
            SharedConfig.toggleCustomTabs();
            if (view instanceof TextCheckCell) {
                ((TextCheckCell) view).setChecked(SharedConfig.customTabs);
            }
        } else if (i == this.directShareRow) {
            SharedConfig.toggleDirectShare();
            if (view instanceof TextCheckCell) {
                ((TextCheckCell) view).setChecked(SharedConfig.directShare);
            }
        } else if (i != this.contactsReimportRow) {
            if (i == this.contactsSortRow) {
                if (getParentActivity() != null) {
                    AlertDialog.Builder builder2 = new AlertDialog.Builder((Context) getParentActivity());
                    builder2.setTitle(LocaleController.getString("SortBy", NUM));
                    builder2.setItems(new CharSequence[]{LocaleController.getString("Default", NUM), LocaleController.getString("SortFirstName", NUM), LocaleController.getString("SortLastName", NUM)}, new DialogInterface.OnClickListener(i) {
                        private final /* synthetic */ int f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void onClick(DialogInterface dialogInterface, int i) {
                            ThemeActivity.this.lambda$null$3$ThemeActivity(this.f$1, dialogInterface, i);
                        }
                    });
                    builder2.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                    showDialog(builder2.create());
                }
            } else if (i == this.stickersRow) {
                presentFragment(new StickersActivity(0));
            } else if (i == this.emojiRow) {
                SharedConfig.toggleBigEmoji();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(SharedConfig.allowBigEmoji);
                }
            } else if (i == this.nightThemeRow) {
                if ((!LocaleController.isRTL || f > ((float) AndroidUtilities.dp(76.0f))) && (LocaleController.isRTL || f < ((float) (view.getMeasuredWidth() - AndroidUtilities.dp(76.0f))))) {
                    presentFragment(new ThemeActivity(1));
                    return;
                }
                NotificationsCheckCell notificationsCheckCell = (NotificationsCheckCell) view;
                if (Theme.selectedAutoNightType == 0) {
                    Theme.selectedAutoNightType = 2;
                    notificationsCheckCell.setChecked(true);
                } else {
                    Theme.selectedAutoNightType = 0;
                    notificationsCheckCell.setChecked(false);
                }
                Theme.saveAutoNightThemeConfig();
                Theme.checkAutoNightThemeConditions(true);
                if (Theme.selectedAutoNightType != 0) {
                    z2 = true;
                }
                String currentNightThemeName = z2 ? Theme.getCurrentNightThemeName() : LocaleController.getString("AutoNightThemeOff", NUM);
                if (z2) {
                    int i4 = Theme.selectedAutoNightType;
                    if (i4 == 1) {
                        str = LocaleController.getString("AutoNightScheduled", NUM);
                    } else if (i4 == 3) {
                        str = LocaleController.getString("AutoNightSystemDefault", NUM);
                    } else {
                        str = LocaleController.getString("AutoNightAdaptive", NUM);
                    }
                    currentNightThemeName = str + " " + currentNightThemeName;
                }
                notificationsCheckCell.setTextAndValueAndCheck(LocaleController.getString("AutoNightTheme", NUM), currentNightThemeName, z2, true);
            } else if (i == this.nightDisabledRow) {
                if (Theme.selectedAutoNightType != 0) {
                    Theme.selectedAutoNightType = 0;
                    updateRows(true);
                    Theme.checkAutoNightThemeConditions();
                }
            } else if (i == this.nightScheduledRow) {
                if (Theme.selectedAutoNightType != 1) {
                    Theme.selectedAutoNightType = 1;
                    if (Theme.autoNightScheduleByLocation) {
                        updateSunTime((Location) null, true);
                    }
                    updateRows(true);
                    Theme.checkAutoNightThemeConditions();
                }
            } else if (i == this.nightAutomaticRow) {
                if (Theme.selectedAutoNightType != 2) {
                    Theme.selectedAutoNightType = 2;
                    updateRows(true);
                    Theme.checkAutoNightThemeConditions();
                }
            } else if (i == this.nightSystemDefaultRow) {
                if (Theme.selectedAutoNightType != 3) {
                    Theme.selectedAutoNightType = 3;
                    updateRows(true);
                    Theme.checkAutoNightThemeConditions();
                }
            } else if (i == this.scheduleLocationRow) {
                Theme.autoNightScheduleByLocation = !Theme.autoNightScheduleByLocation;
                ((TextCheckCell) view).setChecked(Theme.autoNightScheduleByLocation);
                updateRows(true);
                if (Theme.autoNightScheduleByLocation) {
                    updateSunTime((Location) null, true);
                }
                Theme.checkAutoNightThemeConditions();
            } else if (i == this.scheduleFromRow || i == this.scheduleToRow) {
                if (getParentActivity() != null) {
                    if (i == this.scheduleFromRow) {
                        i3 = Theme.autoNightDayStartTime;
                        i2 = i3 / 60;
                    } else {
                        i3 = Theme.autoNightDayEndTime;
                        i2 = i3 / 60;
                    }
                    showDialog(new TimePickerDialog(getParentActivity(), new TimePickerDialog.OnTimeSetListener(i, (TextSettingsCell) view) {
                        private final /* synthetic */ int f$1;
                        private final /* synthetic */ TextSettingsCell f$2;

                        {
                            this.f$1 = r2;
                            this.f$2 = r3;
                        }

                        public final void onTimeSet(TimePicker timePicker, int i, int i2) {
                            ThemeActivity.this.lambda$null$4$ThemeActivity(this.f$1, this.f$2, timePicker, i, i2);
                        }
                    }, i2, i3 - (i2 * 60), true));
                }
            } else if (i == this.scheduleUpdateLocationRow) {
                updateSunTime((Location) null, true);
            }
        }
    }

    public /* synthetic */ void lambda$null$2$ThemeActivity(DialogInterface dialogInterface, int i) {
        SharedConfig.setDistanceSystemType(i);
        RecyclerView.ViewHolder findViewHolderForAdapterPosition = this.listView.findViewHolderForAdapterPosition(this.distanceRow);
        if (findViewHolderForAdapterPosition != null) {
            this.listAdapter.onBindViewHolder(findViewHolderForAdapterPosition, this.distanceRow);
        }
    }

    public /* synthetic */ void lambda$null$3$ThemeActivity(int i, DialogInterface dialogInterface, int i2) {
        SharedPreferences.Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putInt("sortContactsBy", i2);
        edit.commit();
        ListAdapter listAdapter2 = this.listAdapter;
        if (listAdapter2 != null) {
            listAdapter2.notifyItemChanged(i);
        }
    }

    public /* synthetic */ void lambda$null$4$ThemeActivity(int i, TextSettingsCell textSettingsCell, TimePicker timePicker, int i2, int i3) {
        int i4 = (i2 * 60) + i3;
        if (i == this.scheduleFromRow) {
            Theme.autoNightDayStartTime = i4;
            textSettingsCell.setTextAndValue(LocaleController.getString("AutoNightFrom", NUM), String.format("%02d:%02d", new Object[]{Integer.valueOf(i2), Integer.valueOf(i3)}), true);
            return;
        }
        Theme.autoNightDayEndTime = i4;
        textSettingsCell.setTextAndValue(LocaleController.getString("AutoNightTo", NUM), String.format("%02d:%02d", new Object[]{Integer.valueOf(i2), Integer.valueOf(i3)}), true);
    }

    public void onResume() {
        super.onResume();
        if (this.listAdapter != null) {
            updateRows(true);
        }
    }

    /* access modifiers changed from: protected */
    public void onTransitionAnimationEnd(boolean z, boolean z2) {
        if (z) {
            AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
            AndroidUtilities.setAdjustResizeToNothing(getParentActivity(), this.classGuid);
        }
    }

    private void updateMenuItem() {
        if (this.menuItem != null) {
            Theme.ThemeInfo currentTheme = Theme.getCurrentTheme();
            Theme.ThemeAccent accent = currentTheme.getAccent(false);
            ArrayList<Theme.ThemeAccent> arrayList = currentTheme.themeAccents;
            if (arrayList == null || arrayList.isEmpty() || accent == null || accent.id < 100) {
                this.menuItem.hideSubItem(2);
                this.menuItem.hideSubItem(3);
            } else {
                this.menuItem.showSubItem(2);
                this.menuItem.showSubItem(3);
            }
            int i = AndroidUtilities.isTablet() ? 18 : 16;
            Theme.ThemeInfo currentTheme2 = Theme.getCurrentTheme();
            if (SharedConfig.fontSize == i && SharedConfig.bubbleRadius == 10 && currentTheme2.firstAccentIsDefault && currentTheme2.currentAccentId == Theme.DEFALT_THEME_ACCENT_ID) {
                this.menuItem.hideSubItem(4);
            } else {
                this.menuItem.showSubItem(4);
            }
        }
    }

    /* access modifiers changed from: private */
    public void updateSunTime(Location location, boolean z) {
        Activity parentActivity;
        LocationManager locationManager = (LocationManager) ApplicationLoader.applicationContext.getSystemService("location");
        if (Build.VERSION.SDK_INT < 23 || (parentActivity = getParentActivity()) == null || parentActivity.checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION") == 0) {
            if (getParentActivity() != null) {
                if (getParentActivity().getPackageManager().hasSystemFeature("android.hardware.location.gps")) {
                    try {
                        if (!((LocationManager) ApplicationLoader.applicationContext.getSystemService("location")).isProviderEnabled("gps")) {
                            AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
                            builder.setTitle(LocaleController.getString("GpsDisabledAlertTitle", NUM));
                            builder.setMessage(LocaleController.getString("GpsDisabledAlertText", NUM));
                            builder.setPositiveButton(LocaleController.getString("ConnectingToProxyEnable", NUM), new DialogInterface.OnClickListener() {
                                public final void onClick(DialogInterface dialogInterface, int i) {
                                    ThemeActivity.this.lambda$updateSunTime$6$ThemeActivity(dialogInterface, i);
                                }
                            });
                            builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                            showDialog(builder.create());
                            return;
                        }
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                    }
                } else {
                    return;
                }
            }
            try {
                location = locationManager.getLastKnownLocation("gps");
                if (location == null) {
                    location = locationManager.getLastKnownLocation("network");
                }
                if (location == null) {
                    location = locationManager.getLastKnownLocation("passive");
                }
            } catch (Exception e2) {
                FileLog.e((Throwable) e2);
            }
            if (location == null || z) {
                startLocationUpdate();
                if (location == null) {
                    return;
                }
            }
            Theme.autoNightLocationLatitude = location.getLatitude();
            Theme.autoNightLocationLongitude = location.getLongitude();
            int[] calculateSunriseSunset = SunDate.calculateSunriseSunset(Theme.autoNightLocationLatitude, Theme.autoNightLocationLongitude);
            Theme.autoNightSunriseTime = calculateSunriseSunset[0];
            Theme.autoNightSunsetTime = calculateSunriseSunset[1];
            Theme.autoNightCityName = null;
            Calendar instance = Calendar.getInstance();
            instance.setTimeInMillis(System.currentTimeMillis());
            Theme.autoNightLastSunCheckDay = instance.get(5);
            Utilities.globalQueue.postRunnable(new Runnable() {
                public final void run() {
                    ThemeActivity.this.lambda$updateSunTime$8$ThemeActivity();
                }
            });
            RecyclerListView.Holder holder = (RecyclerListView.Holder) this.listView.findViewHolderForAdapterPosition(this.scheduleLocationInfoRow);
            if (holder != null) {
                View view = holder.itemView;
                if (view instanceof TextInfoPrivacyCell) {
                    ((TextInfoPrivacyCell) view).setText(getLocationSunString());
                }
            }
            if (Theme.autoNightScheduleByLocation && Theme.selectedAutoNightType == 1) {
                Theme.checkAutoNightThemeConditions();
                return;
            }
            return;
        }
        parentActivity.requestPermissions(new String[]{"android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"}, 2);
    }

    public /* synthetic */ void lambda$updateSunTime$6$ThemeActivity(DialogInterface dialogInterface, int i) {
        if (getParentActivity() != null) {
            try {
                getParentActivity().startActivity(new Intent("android.settings.LOCATION_SOURCE_SETTINGS"));
            } catch (Exception unused) {
            }
        }
    }

    public /* synthetic */ void lambda$updateSunTime$8$ThemeActivity() {
        String str = null;
        try {
            List<Address> fromLocation = new Geocoder(ApplicationLoader.applicationContext, Locale.getDefault()).getFromLocation(Theme.autoNightLocationLatitude, Theme.autoNightLocationLongitude, 1);
            if (fromLocation.size() > 0) {
                str = fromLocation.get(0).getLocality();
            }
        } catch (Exception unused) {
        }
        AndroidUtilities.runOnUIThread(new Runnable(str) {
            private final /* synthetic */ String f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                ThemeActivity.this.lambda$null$7$ThemeActivity(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$null$7$ThemeActivity(String str) {
        RecyclerListView.Holder holder;
        Theme.autoNightCityName = str;
        if (Theme.autoNightCityName == null) {
            Theme.autoNightCityName = String.format("(%.06f, %.06f)", new Object[]{Double.valueOf(Theme.autoNightLocationLatitude), Double.valueOf(Theme.autoNightLocationLongitude)});
        }
        Theme.saveAutoNightThemeConfig();
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null && (holder = (RecyclerListView.Holder) recyclerListView.findViewHolderForAdapterPosition(this.scheduleUpdateLocationRow)) != null) {
            View view = holder.itemView;
            if (view instanceof TextSettingsCell) {
                ((TextSettingsCell) view).setTextAndValue(LocaleController.getString("AutoNightUpdateLocation", NUM), Theme.autoNightCityName, false);
            }
        }
    }

    private void startLocationUpdate() {
        if (!this.updatingLocation) {
            this.updatingLocation = true;
            LocationManager locationManager = (LocationManager) ApplicationLoader.applicationContext.getSystemService("location");
            try {
                locationManager.requestLocationUpdates("gps", 1, 0.0f, this.gpsLocationListener);
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
            try {
                locationManager.requestLocationUpdates("network", 1, 0.0f, this.networkLocationListener);
            } catch (Exception e2) {
                FileLog.e((Throwable) e2);
            }
        }
    }

    /* access modifiers changed from: private */
    public void stopLocationUpdate() {
        this.updatingLocation = false;
        LocationManager locationManager = (LocationManager) ApplicationLoader.applicationContext.getSystemService("location");
        locationManager.removeUpdates(this.gpsLocationListener);
        locationManager.removeUpdates(this.networkLocationListener);
    }

    private void showPermissionAlert(boolean z) {
        if (getParentActivity() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
            builder.setTitle(LocaleController.getString("AppName", NUM));
            if (z) {
                builder.setMessage(LocaleController.getString("PermissionNoLocationPosition", NUM));
            } else {
                builder.setMessage(LocaleController.getString("PermissionNoLocation", NUM));
            }
            builder.setNegativeButton(LocaleController.getString("PermissionOpenSettings", NUM), new DialogInterface.OnClickListener() {
                public final void onClick(DialogInterface dialogInterface, int i) {
                    ThemeActivity.this.lambda$showPermissionAlert$9$ThemeActivity(dialogInterface, i);
                }
            });
            builder.setPositiveButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null);
            showDialog(builder.create());
        }
    }

    public /* synthetic */ void lambda$showPermissionAlert$9$ThemeActivity(DialogInterface dialogInterface, int i) {
        if (getParentActivity() != null) {
            try {
                Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
                intent.setData(Uri.parse("package:" + ApplicationLoader.applicationContext.getPackageName()));
                getParentActivity().startActivity(intent);
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    /* access modifiers changed from: private */
    public String getLocationSunString() {
        int i = Theme.autoNightSunriseTime;
        int i2 = i / 60;
        String format = String.format("%02d:%02d", new Object[]{Integer.valueOf(i2), Integer.valueOf(i - (i2 * 60))});
        int i3 = Theme.autoNightSunsetTime;
        int i4 = i3 / 60;
        return LocaleController.formatString("AutoNightUpdateLocationInfo", NUM, String.format("%02d:%02d", new Object[]{Integer.valueOf(i4), Integer.valueOf(i3 - (i4 * 60))}), format);
    }

    private static class InnerAccentView extends View {
        private ObjectAnimator checkAnimator;
        private float checkedState;
        private Theme.ThemeAccent currentAccent;
        private Theme.ThemeInfo currentTheme;
        private final Paint paint = new Paint(1);

        InnerAccentView(Context context) {
            super(context);
        }

        /* access modifiers changed from: package-private */
        public void setThemeAndColor(Theme.ThemeInfo themeInfo, Theme.ThemeAccent themeAccent) {
            this.currentTheme = themeInfo;
            this.currentAccent = themeAccent;
            updateCheckedState(false);
        }

        /* access modifiers changed from: package-private */
        public void updateCheckedState(boolean z) {
            boolean z2 = this.currentTheme.currentAccentId == this.currentAccent.id;
            ObjectAnimator objectAnimator = this.checkAnimator;
            if (objectAnimator != null) {
                objectAnimator.cancel();
            }
            float f = 1.0f;
            if (z) {
                float[] fArr = new float[1];
                if (!z2) {
                    f = 0.0f;
                }
                fArr[0] = f;
                this.checkAnimator = ObjectAnimator.ofFloat(this, "checkedState", fArr);
                this.checkAnimator.setDuration(200);
                this.checkAnimator.start();
                return;
            }
            if (!z2) {
                f = 0.0f;
            }
            setCheckedState(f);
        }

        @Keep
        public void setCheckedState(float f) {
            this.checkedState = f;
            invalidate();
        }

        @Keep
        public float getCheckedState() {
            return this.checkedState;
        }

        /* access modifiers changed from: protected */
        public void onAttachedToWindow() {
            super.onAttachedToWindow();
            updateCheckedState(false);
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int i, int i2) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(62.0f), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(62.0f), NUM));
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            float dp = (float) AndroidUtilities.dp(20.0f);
            float measuredWidth = ((float) getMeasuredWidth()) * 0.5f;
            float measuredHeight = ((float) getMeasuredHeight()) * 0.5f;
            this.paint.setColor(this.currentAccent.accentColor);
            this.paint.setStyle(Paint.Style.STROKE);
            this.paint.setStrokeWidth((float) AndroidUtilities.dp(3.0f));
            this.paint.setAlpha(Math.round(this.checkedState * 255.0f));
            canvas.drawCircle(measuredWidth, measuredHeight, dp - (this.paint.getStrokeWidth() * 0.5f), this.paint);
            this.paint.setAlpha(255);
            this.paint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(measuredWidth, measuredHeight, dp - (((float) AndroidUtilities.dp(5.0f)) * this.checkedState), this.paint);
            if (this.checkedState != 0.0f) {
                this.paint.setColor(-1);
                this.paint.setAlpha(Math.round(this.checkedState * 255.0f));
                canvas.drawCircle(measuredWidth, measuredHeight, (float) AndroidUtilities.dp(2.0f), this.paint);
                canvas.drawCircle(measuredWidth - (((float) AndroidUtilities.dp(7.0f)) * this.checkedState), measuredHeight, (float) AndroidUtilities.dp(2.0f), this.paint);
                canvas.drawCircle((((float) AndroidUtilities.dp(7.0f)) * this.checkedState) + measuredWidth, measuredHeight, (float) AndroidUtilities.dp(2.0f), this.paint);
            }
            int i = this.currentAccent.myMessagesAccentColor;
            if (i != 0 && this.checkedState != 1.0f) {
                this.paint.setColor(i);
                canvas.drawCircle(measuredWidth, measuredHeight, ((float) AndroidUtilities.dp(8.0f)) * (1.0f - this.checkedState), this.paint);
            }
        }
    }

    private static class InnerCustomAccentView extends View {
        private int[] colors = new int[7];
        private final Paint paint = new Paint(1);

        InnerCustomAccentView(Context context) {
            super(context);
        }

        /* access modifiers changed from: private */
        public void setTheme(Theme.ThemeInfo themeInfo) {
            if (themeInfo.defaultAccentCount >= 8) {
                this.colors = new int[]{themeInfo.getAccentColor(6), themeInfo.getAccentColor(4), themeInfo.getAccentColor(7), themeInfo.getAccentColor(2), themeInfo.getAccentColor(0), themeInfo.getAccentColor(5), themeInfo.getAccentColor(3)};
                return;
            }
            this.colors = new int[7];
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int i, int i2) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(62.0f), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(62.0f), NUM));
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            float measuredWidth = ((float) getMeasuredWidth()) * 0.5f;
            float measuredHeight = ((float) getMeasuredHeight()) * 0.5f;
            float dp = (float) AndroidUtilities.dp(5.0f);
            float dp2 = ((float) AndroidUtilities.dp(20.0f)) - dp;
            this.paint.setStyle(Paint.Style.FILL);
            int i = 0;
            this.paint.setColor(this.colors[0]);
            canvas.drawCircle(measuredWidth, measuredHeight, dp, this.paint);
            double d = 0.0d;
            while (i < 6) {
                i++;
                this.paint.setColor(this.colors[i]);
                canvas.drawCircle((((float) Math.sin(d)) * dp2) + measuredWidth, measuredHeight - (((float) Math.cos(d)) * dp2), dp, this.paint);
                d += 1.0471975511965976d;
            }
        }
    }

    private class ThemeAccentsListAdapter extends RecyclerListView.SelectionAdapter {
        /* access modifiers changed from: private */
        public Theme.ThemeInfo currentTheme;
        private Context mContext;
        /* access modifiers changed from: private */
        public ArrayList<Theme.ThemeAccent> themeAccents;

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return false;
        }

        ThemeAccentsListAdapter(Context context) {
            this.mContext = context;
            notifyDataSetChanged();
        }

        public void notifyDataSetChanged() {
            this.currentTheme = ThemeActivity.this.currentType == 1 ? Theme.getCurrentNightTheme() : Theme.getCurrentTheme();
            this.themeAccents = new ArrayList<>(this.currentTheme.themeAccents);
            super.notifyDataSetChanged();
        }

        public int getItemViewType(int i) {
            return i == getItemCount() - 1 ? 1 : 0;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            if (i != 0) {
                return new RecyclerListView.Holder(new InnerCustomAccentView(this.mContext));
            }
            return new RecyclerListView.Holder(new InnerAccentView(this.mContext));
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            int itemViewType = getItemViewType(i);
            if (itemViewType == 0) {
                ((InnerAccentView) viewHolder.itemView).setThemeAndColor(this.currentTheme, this.themeAccents.get(i));
            } else if (itemViewType == 1) {
                ((InnerCustomAccentView) viewHolder.itemView).setTheme(this.currentTheme);
            }
        }

        public int getItemCount() {
            if (this.themeAccents.isEmpty()) {
                return 0;
            }
            return this.themeAccents.size() + 1;
        }

        /* access modifiers changed from: private */
        public int findCurrentAccent() {
            return this.themeAccents.indexOf(this.currentTheme.getAccent(false));
        }
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private boolean first = true;
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public int getItemCount() {
            return ThemeActivity.this.rowCount;
        }

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            int itemViewType = viewHolder.getItemViewType();
            return itemViewType == 0 || itemViewType == 1 || itemViewType == 4 || itemViewType == 7 || itemViewType == 10 || itemViewType == 11 || itemViewType == 12;
        }

        /* access modifiers changed from: private */
        public void showOptionsForTheme(Theme.ThemeInfo themeInfo) {
            int[] iArr;
            CharSequence[] charSequenceArr;
            String str;
            if (ThemeActivity.this.getParentActivity() == null) {
                return;
            }
            if ((themeInfo.info == null || themeInfo.themeLoaded) && ThemeActivity.this.currentType != 1) {
                AlertDialog.Builder builder = new AlertDialog.Builder((Context) ThemeActivity.this.getParentActivity());
                String str2 = null;
                boolean z = false;
                if (themeInfo.pathToFile == null) {
                    charSequenceArr = new CharSequence[]{null, LocaleController.getString("ExportTheme", NUM)};
                    iArr = new int[]{0, NUM};
                } else {
                    TLRPC.TL_theme tL_theme = themeInfo.info;
                    boolean z2 = tL_theme == null || !tL_theme.isDefault;
                    CharSequence[] charSequenceArr2 = new CharSequence[5];
                    charSequenceArr2[0] = LocaleController.getString("ShareFile", NUM);
                    charSequenceArr2[1] = LocaleController.getString("ExportTheme", NUM);
                    TLRPC.TL_theme tL_theme2 = themeInfo.info;
                    if (tL_theme2 == null || (!tL_theme2.isDefault && tL_theme2.creator)) {
                        str = LocaleController.getString("Edit", NUM);
                    } else {
                        str = null;
                    }
                    charSequenceArr2[2] = str;
                    TLRPC.TL_theme tL_theme3 = themeInfo.info;
                    charSequenceArr2[3] = (tL_theme3 == null || !tL_theme3.creator) ? null : LocaleController.getString("ThemeSetUrl", NUM);
                    if (z2) {
                        str2 = LocaleController.getString("Delete", NUM);
                    }
                    charSequenceArr2[4] = str2;
                    iArr = new int[]{NUM, NUM, NUM, NUM, NUM};
                    z = z2;
                    charSequenceArr = charSequenceArr2;
                }
                builder.setItems(charSequenceArr, iArr, new DialogInterface.OnClickListener(themeInfo) {
                    private final /* synthetic */ Theme.ThemeInfo f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void onClick(DialogInterface dialogInterface, int i) {
                        ThemeActivity.ListAdapter.this.lambda$showOptionsForTheme$1$ThemeActivity$ListAdapter(this.f$1, dialogInterface, i);
                    }
                });
                AlertDialog create = builder.create();
                ThemeActivity.this.showDialog(create);
                if (z) {
                    create.setItemColor(create.getItemsCount() - 1, Theme.getColor("dialogTextRed2"), Theme.getColor("dialogRedIcon"));
                }
            }
        }

        /* JADX WARNING: Can't wrap try/catch for region: R(4:59|60|61|62) */
        /* JADX WARNING: Missing exception handler attribute for start block: B:61:0x0153 */
        /* JADX WARNING: Removed duplicated region for block: B:34:0x00da A[SYNTHETIC, Splitter:B:34:0x00da] */
        /* JADX WARNING: Removed duplicated region for block: B:39:0x00e5 A[SYNTHETIC, Splitter:B:39:0x00e5] */
        /* JADX WARNING: Removed duplicated region for block: B:50:0x0108  */
        /* JADX WARNING: Removed duplicated region for block: B:55:0x012b A[Catch:{ Exception -> 0x0178 }, RETURN] */
        /* JADX WARNING: Removed duplicated region for block: B:56:0x012c A[Catch:{ Exception -> 0x0178 }] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public /* synthetic */ void lambda$showOptionsForTheme$1$ThemeActivity$ListAdapter(org.telegram.ui.ActionBar.Theme.ThemeInfo r8, android.content.DialogInterface r9, int r10) {
            /*
                r7 = this;
                org.telegram.ui.ThemeActivity r9 = org.telegram.ui.ThemeActivity.this
                android.app.Activity r9 = r9.getParentActivity()
                if (r9 != 0) goto L_0x0009
                return
            L_0x0009:
                r9 = 0
                r0 = 2
                r1 = 0
                r2 = 1
                if (r10 != 0) goto L_0x006b
                org.telegram.tgnet.TLRPC$TL_theme r10 = r8.info
                if (r10 != 0) goto L_0x002d
                int r10 = r8.account
                org.telegram.messenger.MessagesController r10 = org.telegram.messenger.MessagesController.getInstance(r10)
                r10.saveThemeToServer(r8, r1)
                org.telegram.messenger.NotificationCenter r10 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
                int r3 = org.telegram.messenger.NotificationCenter.needShareTheme
                java.lang.Object[] r0 = new java.lang.Object[r0]
                r0[r9] = r8
                r0[r2] = r1
                r10.postNotificationName(r3, r0)
                goto L_0x0215
            L_0x002d:
                java.lang.StringBuilder r9 = new java.lang.StringBuilder
                r9.<init>()
                java.lang.String r10 = "https://"
                r9.append(r10)
                org.telegram.ui.ThemeActivity r10 = org.telegram.ui.ThemeActivity.this
                int r10 = r10.currentAccount
                org.telegram.messenger.MessagesController r10 = org.telegram.messenger.MessagesController.getInstance(r10)
                java.lang.String r10 = r10.linkPrefix
                r9.append(r10)
                java.lang.String r10 = "/addtheme/"
                r9.append(r10)
                org.telegram.tgnet.TLRPC$TL_theme r8 = r8.info
                java.lang.String r8 = r8.slug
                r9.append(r8)
                java.lang.String r5 = r9.toString()
                org.telegram.ui.ThemeActivity r8 = org.telegram.ui.ThemeActivity.this
                org.telegram.ui.Components.ShareAlert r9 = new org.telegram.ui.Components.ShareAlert
                android.app.Activity r1 = r8.getParentActivity()
                r2 = 0
                r4 = 0
                r6 = 0
                r0 = r9
                r3 = r5
                r0.<init>(r1, r2, r3, r4, r5, r6)
                r8.showDialog(r9)
                goto L_0x0215
            L_0x006b:
                if (r10 != r2) goto L_0x017e
                java.lang.String r9 = r8.pathToFile
                if (r9 != 0) goto L_0x00ee
                java.lang.String r9 = r8.assetName
                if (r9 != 0) goto L_0x00ee
                java.lang.StringBuilder r9 = new java.lang.StringBuilder
                r9.<init>()
                java.util.HashMap r10 = org.telegram.ui.ActionBar.Theme.getDefaultColors()
                java.util.Set r10 = r10.entrySet()
                java.util.Iterator r10 = r10.iterator()
            L_0x0086:
                boolean r0 = r10.hasNext()
                if (r0 == 0) goto L_0x00ad
                java.lang.Object r0 = r10.next()
                java.util.Map$Entry r0 = (java.util.Map.Entry) r0
                java.lang.Object r3 = r0.getKey()
                java.lang.String r3 = (java.lang.String) r3
                r9.append(r3)
                java.lang.String r3 = "="
                r9.append(r3)
                java.lang.Object r0 = r0.getValue()
                r9.append(r0)
                java.lang.String r0 = "\n"
                r9.append(r0)
                goto L_0x0086
            L_0x00ad:
                java.io.File r10 = new java.io.File
                java.io.File r0 = org.telegram.messenger.ApplicationLoader.getFilesDirFixed()
                java.lang.String r3 = "default_theme.attheme"
                r10.<init>(r0, r3)
                java.io.FileOutputStream r0 = new java.io.FileOutputStream     // Catch:{ Exception -> 0x00d4 }
                r0.<init>(r10)     // Catch:{ Exception -> 0x00d4 }
                java.lang.String r9 = r9.toString()     // Catch:{ Exception -> 0x00ce, all -> 0x00cc }
                byte[] r9 = org.telegram.messenger.AndroidUtilities.getStringBytes(r9)     // Catch:{ Exception -> 0x00ce, all -> 0x00cc }
                r0.write(r9)     // Catch:{ Exception -> 0x00ce, all -> 0x00cc }
                r0.close()     // Catch:{ Exception -> 0x00de }
                goto L_0x00fe
            L_0x00cc:
                r8 = move-exception
                goto L_0x00e3
            L_0x00ce:
                r9 = move-exception
                r1 = r0
                goto L_0x00d5
            L_0x00d1:
                r8 = move-exception
                r0 = r1
                goto L_0x00e3
            L_0x00d4:
                r9 = move-exception
            L_0x00d5:
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r9)     // Catch:{ all -> 0x00d1 }
                if (r1 == 0) goto L_0x00fe
                r1.close()     // Catch:{ Exception -> 0x00de }
                goto L_0x00fe
            L_0x00de:
                r9 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r9)
                goto L_0x00fe
            L_0x00e3:
                if (r0 == 0) goto L_0x00ed
                r0.close()     // Catch:{ Exception -> 0x00e9 }
                goto L_0x00ed
            L_0x00e9:
                r9 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r9)
            L_0x00ed:
                throw r8
            L_0x00ee:
                java.lang.String r9 = r8.assetName
                if (r9 == 0) goto L_0x00f7
                java.io.File r10 = org.telegram.ui.ActionBar.Theme.getAssetFile(r9)
                goto L_0x00fe
            L_0x00f7:
                java.io.File r10 = new java.io.File
                java.lang.String r9 = r8.pathToFile
                r10.<init>(r9)
            L_0x00fe:
                java.lang.String r8 = r8.name
                java.lang.String r9 = ".attheme"
                boolean r0 = r8.endsWith(r9)
                if (r0 != 0) goto L_0x0117
                java.lang.StringBuilder r0 = new java.lang.StringBuilder
                r0.<init>()
                r0.append(r8)
                r0.append(r9)
                java.lang.String r8 = r0.toString()
            L_0x0117:
                java.io.File r9 = new java.io.File
                r0 = 4
                java.io.File r0 = org.telegram.messenger.FileLoader.getDirectory(r0)
                java.lang.String r8 = org.telegram.messenger.FileLoader.fixFileName(r8)
                r9.<init>(r0, r8)
                boolean r8 = org.telegram.messenger.AndroidUtilities.copyFile((java.io.File) r10, (java.io.File) r9)     // Catch:{ Exception -> 0x0178 }
                if (r8 != 0) goto L_0x012c
                return
            L_0x012c:
                android.content.Intent r8 = new android.content.Intent     // Catch:{ Exception -> 0x0178 }
                java.lang.String r10 = "android.intent.action.SEND"
                r8.<init>(r10)     // Catch:{ Exception -> 0x0178 }
                java.lang.String r10 = "text/xml"
                r8.setType(r10)     // Catch:{ Exception -> 0x0178 }
                int r10 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0178 }
                r0 = 24
                java.lang.String r1 = "android.intent.extra.STREAM"
                if (r10 < r0) goto L_0x015b
                org.telegram.ui.ThemeActivity r10 = org.telegram.ui.ThemeActivity.this     // Catch:{ Exception -> 0x0153 }
                android.app.Activity r10 = r10.getParentActivity()     // Catch:{ Exception -> 0x0153 }
                java.lang.String r0 = "org.telegram.messenger.beta.provider"
                android.net.Uri r10 = androidx.core.content.FileProvider.getUriForFile(r10, r0, r9)     // Catch:{ Exception -> 0x0153 }
                r8.putExtra(r1, r10)     // Catch:{ Exception -> 0x0153 }
                r8.setFlags(r2)     // Catch:{ Exception -> 0x0153 }
                goto L_0x0162
            L_0x0153:
                android.net.Uri r9 = android.net.Uri.fromFile(r9)     // Catch:{ Exception -> 0x0178 }
                r8.putExtra(r1, r9)     // Catch:{ Exception -> 0x0178 }
                goto L_0x0162
            L_0x015b:
                android.net.Uri r9 = android.net.Uri.fromFile(r9)     // Catch:{ Exception -> 0x0178 }
                r8.putExtra(r1, r9)     // Catch:{ Exception -> 0x0178 }
            L_0x0162:
                org.telegram.ui.ThemeActivity r9 = org.telegram.ui.ThemeActivity.this     // Catch:{ Exception -> 0x0178 }
                java.lang.String r10 = "ShareFile"
                r0 = 2131626584(0x7f0e0a58, float:1.8880408E38)
                java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r10, r0)     // Catch:{ Exception -> 0x0178 }
                android.content.Intent r8 = android.content.Intent.createChooser(r8, r10)     // Catch:{ Exception -> 0x0178 }
                r10 = 500(0x1f4, float:7.0E-43)
                r9.startActivityForResult(r8, r10)     // Catch:{ Exception -> 0x0178 }
                goto L_0x0215
            L_0x0178:
                r8 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r8)
                goto L_0x0215
            L_0x017e:
                if (r10 != r0) goto L_0x01a3
                org.telegram.ui.ThemeActivity r9 = org.telegram.ui.ThemeActivity.this
                org.telegram.ui.ActionBar.ActionBarLayout r9 = r9.parentLayout
                if (r9 == 0) goto L_0x0215
                org.telegram.ui.ActionBar.Theme.applyTheme(r8)
                org.telegram.ui.ThemeActivity r9 = org.telegram.ui.ThemeActivity.this
                org.telegram.ui.ActionBar.ActionBarLayout r9 = r9.parentLayout
                r9.rebuildAllFragmentViews(r2, r2)
                org.telegram.ui.Components.ThemeEditorView r9 = new org.telegram.ui.Components.ThemeEditorView
                r9.<init>()
                org.telegram.ui.ThemeActivity r10 = org.telegram.ui.ThemeActivity.this
                android.app.Activity r10 = r10.getParentActivity()
                r9.show(r10, r8)
                goto L_0x0215
            L_0x01a3:
                r0 = 3
                if (r10 != r0) goto L_0x01b1
                org.telegram.ui.ThemeActivity r10 = org.telegram.ui.ThemeActivity.this
                org.telegram.ui.ThemeSetUrlActivity r0 = new org.telegram.ui.ThemeSetUrlActivity
                r0.<init>(r8, r1, r9)
                r10.presentFragment(r0)
                goto L_0x0215
            L_0x01b1:
                org.telegram.ui.ThemeActivity r9 = org.telegram.ui.ThemeActivity.this
                android.app.Activity r9 = r9.getParentActivity()
                if (r9 != 0) goto L_0x01ba
                return
            L_0x01ba:
                org.telegram.ui.ActionBar.AlertDialog$Builder r9 = new org.telegram.ui.ActionBar.AlertDialog$Builder
                org.telegram.ui.ThemeActivity r10 = org.telegram.ui.ThemeActivity.this
                android.app.Activity r10 = r10.getParentActivity()
                r9.<init>((android.content.Context) r10)
                r10 = 2131624885(0x7f0e03b5, float:1.8876962E38)
                java.lang.String r0 = "DeleteThemeTitle"
                java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r0, r10)
                r9.setTitle(r10)
                r10 = 2131624884(0x7f0e03b4, float:1.887696E38)
                java.lang.String r0 = "DeleteThemeAlert"
                java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r0, r10)
                r9.setMessage(r10)
                r10 = 2131624843(0x7f0e038b, float:1.8876877E38)
                java.lang.String r0 = "Delete"
                java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r0, r10)
                org.telegram.ui.-$$Lambda$ThemeActivity$ListAdapter$HjGrFd2877SP2gFmUCLASSNAMEvuRyOmw r0 = new org.telegram.ui.-$$Lambda$ThemeActivity$ListAdapter$HjGrFd2877SP2gFmUCLASSNAMEvuRyOmw
                r0.<init>(r8)
                r9.setPositiveButton(r10, r0)
                r8 = 2131624476(0x7f0e021c, float:1.8876133E38)
                java.lang.String r10 = "Cancel"
                java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r10, r8)
                r9.setNegativeButton(r8, r1)
                org.telegram.ui.ActionBar.AlertDialog r8 = r9.create()
                org.telegram.ui.ThemeActivity r9 = org.telegram.ui.ThemeActivity.this
                r9.showDialog(r8)
                r9 = -1
                android.view.View r8 = r8.getButton(r9)
                android.widget.TextView r8 = (android.widget.TextView) r8
                if (r8 == 0) goto L_0x0215
                java.lang.String r9 = "dialogTextRed2"
                int r9 = org.telegram.ui.ActionBar.Theme.getColor(r9)
                r8.setTextColor(r9)
            L_0x0215:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ThemeActivity.ListAdapter.lambda$showOptionsForTheme$1$ThemeActivity$ListAdapter(org.telegram.ui.ActionBar.Theme$ThemeInfo, android.content.DialogInterface, int):void");
        }

        public /* synthetic */ void lambda$null$0$ThemeActivity$ListAdapter(Theme.ThemeInfo themeInfo, DialogInterface dialogInterface, int i) {
            MessagesController.getInstance(themeInfo.account).saveTheme(themeInfo, (Theme.ThemeAccent) null, themeInfo == Theme.getCurrentNightTheme(), true);
            if (Theme.deleteTheme(themeInfo)) {
                ThemeActivity.this.parentLayout.rebuildAllFragmentViews(true, true);
            }
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.themeListUpdated, new Object[0]);
        }

        public /* synthetic */ void lambda$onCreateViewHolder$2$ThemeActivity$ListAdapter(ThemeAccentsListAdapter themeAccentsListAdapter, RecyclerListView recyclerListView, View view, int i) {
            Theme.ThemeInfo currentNightTheme = ThemeActivity.this.currentType == 1 ? Theme.getCurrentNightTheme() : Theme.getCurrentTheme();
            if (i == themeAccentsListAdapter.getItemCount() - 1) {
                ThemeActivity themeActivity = ThemeActivity.this;
                themeActivity.presentFragment(new ThemePreviewActivity(currentNightTheme, false, 1, false, themeActivity.currentType == 1));
            } else {
                Theme.ThemeAccent themeAccent = (Theme.ThemeAccent) themeAccentsListAdapter.themeAccents.get(i);
                if (!TextUtils.isEmpty(themeAccent.patternSlug) && themeAccent.id != Theme.DEFALT_THEME_ACCENT_ID) {
                    Theme.PatternsLoader.createLoader(false);
                }
                int i2 = currentNightTheme.currentAccentId;
                int i3 = themeAccent.id;
                if (i2 != i3) {
                    NotificationCenter globalInstance = NotificationCenter.getGlobalInstance();
                    int i4 = NotificationCenter.needSetDayNightTheme;
                    Object[] objArr = new Object[4];
                    objArr[0] = currentNightTheme;
                    objArr[1] = Boolean.valueOf(ThemeActivity.this.currentType == 1);
                    objArr[2] = null;
                    objArr[3] = Integer.valueOf(themeAccent.id);
                    globalInstance.postNotificationName(i4, objArr);
                } else {
                    ThemeActivity.this.presentFragment(new ThemePreviewActivity(currentNightTheme, false, 1, i3 >= 100, ThemeActivity.this.currentType == 1));
                }
            }
            int left = view.getLeft();
            int right = view.getRight();
            int dp = AndroidUtilities.dp(52.0f);
            int i5 = left - dp;
            if (i5 < 0) {
                recyclerListView.smoothScrollBy(i5, 0);
            } else {
                int i6 = right + dp;
                if (i6 > recyclerListView.getMeasuredWidth()) {
                    recyclerListView.smoothScrollBy(i6 - recyclerListView.getMeasuredWidth(), 0);
                }
            }
            int childCount = recyclerListView.getChildCount();
            for (int i7 = 0; i7 < childCount; i7++) {
                View childAt = recyclerListView.getChildAt(i7);
                if (childAt instanceof InnerAccentView) {
                    ((InnerAccentView) childAt).updateCheckedState(true);
                }
            }
        }

        public /* synthetic */ boolean lambda$onCreateViewHolder$5$ThemeActivity$ListAdapter(ThemeAccentsListAdapter themeAccentsListAdapter, View view, int i) {
            if (i >= 0 && i < themeAccentsListAdapter.themeAccents.size()) {
                Theme.ThemeAccent themeAccent = (Theme.ThemeAccent) themeAccentsListAdapter.themeAccents.get(i);
                if (themeAccent.id >= 100) {
                    AlertDialog.Builder builder = new AlertDialog.Builder((Context) ThemeActivity.this.getParentActivity());
                    CharSequence[] charSequenceArr = new CharSequence[4];
                    charSequenceArr[0] = LocaleController.getString("OpenInEditor", NUM);
                    charSequenceArr[1] = LocaleController.getString("ShareTheme", NUM);
                    TLRPC.TL_theme tL_theme = themeAccent.info;
                    charSequenceArr[2] = (tL_theme == null || !tL_theme.creator) ? null : LocaleController.getString("ThemeSetUrl", NUM);
                    charSequenceArr[3] = LocaleController.getString("DeleteTheme", NUM);
                    builder.setItems(charSequenceArr, new int[]{NUM, NUM, NUM, NUM}, new DialogInterface.OnClickListener(themeAccent, themeAccentsListAdapter) {
                        private final /* synthetic */ Theme.ThemeAccent f$1;
                        private final /* synthetic */ ThemeActivity.ThemeAccentsListAdapter f$2;

                        {
                            this.f$1 = r2;
                            this.f$2 = r3;
                        }

                        public final void onClick(DialogInterface dialogInterface, int i) {
                            ThemeActivity.ListAdapter.this.lambda$null$4$ThemeActivity$ListAdapter(this.f$1, this.f$2, dialogInterface, i);
                        }
                    });
                    AlertDialog create = builder.create();
                    ThemeActivity.this.showDialog(create);
                    create.setItemColor(create.getItemsCount() - 1, Theme.getColor("dialogTextRed2"), Theme.getColor("dialogRedIcon"));
                    return true;
                }
            }
            return false;
        }

        public /* synthetic */ void lambda$null$4$ThemeActivity$ListAdapter(Theme.ThemeAccent themeAccent, ThemeAccentsListAdapter themeAccentsListAdapter, DialogInterface dialogInterface, int i) {
            if (ThemeActivity.this.getParentActivity() != null) {
                int i2 = 2;
                if (i == 0) {
                    ThemeActivity themeActivity = ThemeActivity.this;
                    if (i != 1) {
                        i2 = 1;
                    }
                    AlertsCreator.createThemeCreateDialog(themeActivity, i2, themeAccent.parentTheme, themeAccent);
                } else if (i == 1) {
                    if (themeAccent.info == null) {
                        MessagesController.getInstance(ThemeActivity.this.currentAccount).saveThemeToServer(themeAccent.parentTheme, themeAccent);
                        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.needShareTheme, themeAccent.parentTheme, themeAccent);
                        return;
                    }
                    String str = "https://" + MessagesController.getInstance(ThemeActivity.this.currentAccount).linkPrefix + "/addtheme/" + themeAccent.info.slug;
                    ThemeActivity themeActivity2 = ThemeActivity.this;
                    themeActivity2.showDialog(new ShareAlert(themeActivity2.getParentActivity(), (ArrayList<MessageObject>) null, str, false, str, false));
                } else if (i == 2) {
                    ThemeActivity.this.presentFragment(new ThemeSetUrlActivity(themeAccent.parentTheme, themeAccent, false));
                } else if (i == 3 && ThemeActivity.this.getParentActivity() != null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder((Context) ThemeActivity.this.getParentActivity());
                    builder.setTitle(LocaleController.getString("DeleteThemeTitle", NUM));
                    builder.setMessage(LocaleController.getString("DeleteThemeAlert", NUM));
                    builder.setPositiveButton(LocaleController.getString("Delete", NUM), new DialogInterface.OnClickListener(themeAccentsListAdapter, themeAccent) {
                        private final /* synthetic */ ThemeActivity.ThemeAccentsListAdapter f$1;
                        private final /* synthetic */ Theme.ThemeAccent f$2;

                        {
                            this.f$1 = r2;
                            this.f$2 = r3;
                        }

                        public final void onClick(DialogInterface dialogInterface, int i) {
                            ThemeActivity.ListAdapter.this.lambda$null$3$ThemeActivity$ListAdapter(this.f$1, this.f$2, dialogInterface, i);
                        }
                    });
                    builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                    AlertDialog create = builder.create();
                    ThemeActivity.this.showDialog(create);
                    TextView textView = (TextView) create.getButton(-1);
                    if (textView != null) {
                        textView.setTextColor(Theme.getColor("dialogTextRed2"));
                    }
                }
            }
        }

        public /* synthetic */ void lambda$null$3$ThemeActivity$ListAdapter(ThemeAccentsListAdapter themeAccentsListAdapter, Theme.ThemeAccent themeAccent, DialogInterface dialogInterface, int i) {
            if (Theme.deleteThemeAccent(themeAccentsListAdapter.currentTheme, themeAccent, true)) {
                Theme.refreshThemeColors();
                NotificationCenter globalInstance = NotificationCenter.getGlobalInstance();
                int i2 = NotificationCenter.needSetDayNightTheme;
                Object[] objArr = new Object[4];
                boolean z = false;
                objArr[0] = Theme.getActiveTheme();
                if (ThemeActivity.this.currentType == 1) {
                    z = true;
                }
                objArr[1] = Boolean.valueOf(z);
                objArr[2] = null;
                objArr[3] = -1;
                globalInstance.postNotificationName(i2, objArr);
            }
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            BubbleRadiusCell bubbleRadiusCell;
            switch (i) {
                case 1:
                    TextSettingsCell textSettingsCell = new TextSettingsCell(this.mContext);
                    textSettingsCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    bubbleRadiusCell = textSettingsCell;
                    break;
                case 2:
                    TextInfoPrivacyCell textInfoPrivacyCell = new TextInfoPrivacyCell(this.mContext);
                    textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                    bubbleRadiusCell = textInfoPrivacyCell;
                    break;
                case 3:
                    bubbleRadiusCell = new ShadowSectionCell(this.mContext);
                    break;
                case 4:
                    ThemeTypeCell themeTypeCell = new ThemeTypeCell(this.mContext);
                    themeTypeCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    bubbleRadiusCell = themeTypeCell;
                    break;
                case 5:
                    HeaderCell headerCell = new HeaderCell(this.mContext);
                    headerCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    bubbleRadiusCell = headerCell;
                    break;
                case 6:
                    AnonymousClass1 r10 = new BrightnessControlCell(this.mContext) {
                        /* access modifiers changed from: protected */
                        public void didChangedValue(float f) {
                            int i = (int) (Theme.autoNightBrighnessThreshold * 100.0f);
                            int i2 = (int) (f * 100.0f);
                            Theme.autoNightBrighnessThreshold = f;
                            if (i != i2) {
                                RecyclerListView.Holder holder = (RecyclerListView.Holder) ThemeActivity.this.listView.findViewHolderForAdapterPosition(ThemeActivity.this.automaticBrightnessInfoRow);
                                if (holder != null) {
                                    ((TextInfoPrivacyCell) holder.itemView).setText(LocaleController.formatString("AutoNightBrightnessInfo", NUM, Integer.valueOf((int) (Theme.autoNightBrighnessThreshold * 100.0f))));
                                }
                                Theme.checkAutoNightThemeConditions(true);
                            }
                        }
                    };
                    r10.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    bubbleRadiusCell = r10;
                    break;
                case 7:
                    TextCheckCell textCheckCell = new TextCheckCell(this.mContext);
                    textCheckCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    bubbleRadiusCell = textCheckCell;
                    break;
                case 8:
                    TextSizeCell textSizeCell = new TextSizeCell(this.mContext);
                    textSizeCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    bubbleRadiusCell = textSizeCell;
                    break;
                case 9:
                    AnonymousClass2 r102 = new ChatListCell(this.mContext) {
                        /* access modifiers changed from: protected */
                        public void didSelectChatType(boolean z) {
                            SharedConfig.setUseThreeLinesLayout(z);
                        }
                    };
                    r102.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    bubbleRadiusCell = r102;
                    break;
                case 10:
                    NotificationsCheckCell notificationsCheckCell = new NotificationsCheckCell(this.mContext, 21, 64);
                    notificationsCheckCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    bubbleRadiusCell = notificationsCheckCell;
                    break;
                case 11:
                    this.first = true;
                    ThemeActivity themeActivity = ThemeActivity.this;
                    ThemesHorizontalListCell unused = themeActivity.themesHorizontalListCell = new ThemesHorizontalListCell(this.mContext, themeActivity.currentType, ThemeActivity.this.defaultThemes, ThemeActivity.this.darkThemes) {
                        /* access modifiers changed from: protected */
                        public void showOptionsForTheme(Theme.ThemeInfo themeInfo) {
                            ThemeActivity.this.listAdapter.showOptionsForTheme(themeInfo);
                        }

                        /* access modifiers changed from: protected */
                        public void presentFragment(BaseFragment baseFragment) {
                            ThemeActivity.this.presentFragment(baseFragment);
                        }

                        /* access modifiers changed from: protected */
                        public void updateRows() {
                            ThemeActivity.this.updateRows(false);
                        }
                    };
                    ThemeActivity.this.themesHorizontalListCell.setDrawDivider(ThemeActivity.this.hasThemeAccents);
                    ThemeActivity.this.themesHorizontalListCell.setFocusable(false);
                    ThemesHorizontalListCell access$1700 = ThemeActivity.this.themesHorizontalListCell;
                    access$1700.setLayoutParams(new RecyclerView.LayoutParams(-1, AndroidUtilities.dp(148.0f)));
                    bubbleRadiusCell = access$1700;
                    break;
                case 12:
                    AnonymousClass4 r103 = new TintRecyclerListView(this.mContext) {
                        public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                            if (!(getParent() == null || getParent().getParent() == null)) {
                                getParent().getParent().requestDisallowInterceptTouchEvent(canScrollHorizontally(-1));
                            }
                            return super.onInterceptTouchEvent(motionEvent);
                        }
                    };
                    r103.setFocusable(false);
                    r103.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    r103.setItemAnimator((RecyclerView.ItemAnimator) null);
                    r103.setLayoutAnimation((LayoutAnimationController) null);
                    r103.setPadding(AndroidUtilities.dp(11.0f), 0, AndroidUtilities.dp(11.0f), 0);
                    r103.setClipToPadding(false);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.mContext);
                    linearLayoutManager.setOrientation(0);
                    r103.setLayoutManager(linearLayoutManager);
                    ThemeAccentsListAdapter themeAccentsListAdapter = new ThemeAccentsListAdapter(this.mContext);
                    r103.setAdapter(themeAccentsListAdapter);
                    r103.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener(themeAccentsListAdapter, r103) {
                        private final /* synthetic */ ThemeActivity.ThemeAccentsListAdapter f$1;
                        private final /* synthetic */ RecyclerListView f$2;

                        {
                            this.f$1 = r2;
                            this.f$2 = r3;
                        }

                        public final void onItemClick(View view, int i) {
                            ThemeActivity.ListAdapter.this.lambda$onCreateViewHolder$2$ThemeActivity$ListAdapter(this.f$1, this.f$2, view, i);
                        }
                    });
                    r103.setOnItemLongClickListener((RecyclerListView.OnItemLongClickListener) new RecyclerListView.OnItemLongClickListener(themeAccentsListAdapter) {
                        private final /* synthetic */ ThemeActivity.ThemeAccentsListAdapter f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final boolean onItemClick(View view, int i) {
                            return ThemeActivity.ListAdapter.this.lambda$onCreateViewHolder$5$ThemeActivity$ListAdapter(this.f$1, view, i);
                        }
                    });
                    r103.setLayoutParams(new RecyclerView.LayoutParams(-1, AndroidUtilities.dp(62.0f)));
                    bubbleRadiusCell = r103;
                    break;
                default:
                    BubbleRadiusCell bubbleRadiusCell2 = new BubbleRadiusCell(this.mContext);
                    bubbleRadiusCell2.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    bubbleRadiusCell = bubbleRadiusCell2;
                    break;
            }
            return new RecyclerListView.Holder(bubbleRadiusCell);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            String str;
            String str2;
            String str3;
            boolean z = false;
            boolean z2 = true;
            switch (viewHolder.getItemViewType()) {
                case 1:
                    TextSettingsCell textSettingsCell = (TextSettingsCell) viewHolder.itemView;
                    if (i == ThemeActivity.this.nightThemeRow) {
                        if (Theme.selectedAutoNightType == 0 || Theme.getCurrentNightTheme() == null) {
                            textSettingsCell.setTextAndValue(LocaleController.getString("AutoNightTheme", NUM), LocaleController.getString("AutoNightThemeOff", NUM), false);
                            return;
                        } else {
                            textSettingsCell.setTextAndValue(LocaleController.getString("AutoNightTheme", NUM), Theme.getCurrentNightThemeName(), false);
                            return;
                        }
                    } else if (i == ThemeActivity.this.scheduleFromRow) {
                        int i2 = Theme.autoNightDayStartTime;
                        int i3 = i2 / 60;
                        textSettingsCell.setTextAndValue(LocaleController.getString("AutoNightFrom", NUM), String.format("%02d:%02d", new Object[]{Integer.valueOf(i3), Integer.valueOf(i2 - (i3 * 60))}), true);
                        return;
                    } else if (i == ThemeActivity.this.scheduleToRow) {
                        int i4 = Theme.autoNightDayEndTime;
                        int i5 = i4 / 60;
                        textSettingsCell.setTextAndValue(LocaleController.getString("AutoNightTo", NUM), String.format("%02d:%02d", new Object[]{Integer.valueOf(i5), Integer.valueOf(i4 - (i5 * 60))}), false);
                        return;
                    } else if (i == ThemeActivity.this.scheduleUpdateLocationRow) {
                        textSettingsCell.setTextAndValue(LocaleController.getString("AutoNightUpdateLocation", NUM), Theme.autoNightCityName, false);
                        return;
                    } else if (i == ThemeActivity.this.contactsSortRow) {
                        int i6 = MessagesController.getGlobalMainSettings().getInt("sortContactsBy", 0);
                        if (i6 == 0) {
                            str2 = LocaleController.getString("Default", NUM);
                        } else if (i6 == 1) {
                            str2 = LocaleController.getString("FirstName", NUM);
                        } else {
                            str2 = LocaleController.getString("LastName", NUM);
                        }
                        textSettingsCell.setTextAndValue(LocaleController.getString("SortBy", NUM), str2, true);
                        return;
                    } else if (i == ThemeActivity.this.backgroundRow) {
                        textSettingsCell.setText(LocaleController.getString("ChangeChatBackground", NUM), false);
                        return;
                    } else if (i == ThemeActivity.this.contactsReimportRow) {
                        textSettingsCell.setText(LocaleController.getString("ImportContacts", NUM), true);
                        return;
                    } else if (i == ThemeActivity.this.stickersRow) {
                        textSettingsCell.setText(LocaleController.getString("StickersAndMasks", NUM), false);
                        return;
                    } else if (i == ThemeActivity.this.distanceRow) {
                        int i7 = SharedConfig.distanceSystemType;
                        if (i7 == 0) {
                            str = LocaleController.getString("DistanceUnitsAutomatic", NUM);
                        } else if (i7 == 1) {
                            str = LocaleController.getString("DistanceUnitsKilometers", NUM);
                        } else {
                            str = LocaleController.getString("DistanceUnitsMiles", NUM);
                        }
                        textSettingsCell.setTextAndValue(LocaleController.getString("DistanceUnits", NUM), str, false);
                        return;
                    } else {
                        return;
                    }
                case 2:
                    TextInfoPrivacyCell textInfoPrivacyCell = (TextInfoPrivacyCell) viewHolder.itemView;
                    if (i == ThemeActivity.this.automaticBrightnessInfoRow) {
                        textInfoPrivacyCell.setText(LocaleController.formatString("AutoNightBrightnessInfo", NUM, Integer.valueOf((int) (Theme.autoNightBrighnessThreshold * 100.0f))));
                        return;
                    } else if (i == ThemeActivity.this.scheduleLocationInfoRow) {
                        textInfoPrivacyCell.setText(ThemeActivity.this.getLocationSunString());
                        return;
                    } else {
                        return;
                    }
                case 3:
                    if (i == ThemeActivity.this.stickersSection2Row || ((i == ThemeActivity.this.nightTypeInfoRow && ThemeActivity.this.themeInfoRow == -1) || (i == ThemeActivity.this.themeInfoRow && ThemeActivity.this.nightTypeInfoRow != -1))) {
                        viewHolder.itemView.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                        return;
                    } else {
                        viewHolder.itemView.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                        return;
                    }
                case 4:
                    ThemeTypeCell themeTypeCell = (ThemeTypeCell) viewHolder.itemView;
                    if (i == ThemeActivity.this.nightDisabledRow) {
                        String string = LocaleController.getString("AutoNightDisabled", NUM);
                        if (Theme.selectedAutoNightType == 0) {
                            z = true;
                        }
                        themeTypeCell.setValue(string, z, true);
                        return;
                    } else if (i == ThemeActivity.this.nightScheduledRow) {
                        String string2 = LocaleController.getString("AutoNightScheduled", NUM);
                        if (Theme.selectedAutoNightType == 1) {
                            z = true;
                        }
                        themeTypeCell.setValue(string2, z, true);
                        return;
                    } else if (i == ThemeActivity.this.nightAutomaticRow) {
                        String string3 = LocaleController.getString("AutoNightAdaptive", NUM);
                        boolean z3 = Theme.selectedAutoNightType == 2;
                        if (ThemeActivity.this.nightSystemDefaultRow != -1) {
                            z = true;
                        }
                        themeTypeCell.setValue(string3, z3, z);
                        return;
                    } else if (i == ThemeActivity.this.nightSystemDefaultRow) {
                        String string4 = LocaleController.getString("AutoNightSystemDefault", NUM);
                        if (Theme.selectedAutoNightType != 3) {
                            z2 = false;
                        }
                        themeTypeCell.setValue(string4, z2, false);
                        return;
                    } else {
                        return;
                    }
                case 5:
                    HeaderCell headerCell = (HeaderCell) viewHolder.itemView;
                    if (i == ThemeActivity.this.scheduleHeaderRow) {
                        headerCell.setText(LocaleController.getString("AutoNightSchedule", NUM));
                        return;
                    } else if (i == ThemeActivity.this.automaticHeaderRow) {
                        headerCell.setText(LocaleController.getString("AutoNightBrightness", NUM));
                        return;
                    } else if (i == ThemeActivity.this.preferedHeaderRow) {
                        headerCell.setText(LocaleController.getString("AutoNightPreferred", NUM));
                        return;
                    } else if (i == ThemeActivity.this.settingsRow) {
                        headerCell.setText(LocaleController.getString("SETTINGS", NUM));
                        return;
                    } else if (i == ThemeActivity.this.themeHeaderRow) {
                        headerCell.setText(LocaleController.getString("ColorTheme", NUM));
                        return;
                    } else if (i == ThemeActivity.this.textSizeHeaderRow) {
                        headerCell.setText(LocaleController.getString("TextSizeHeader", NUM));
                        return;
                    } else if (i == ThemeActivity.this.chatListHeaderRow) {
                        headerCell.setText(LocaleController.getString("ChatList", NUM));
                        return;
                    } else if (i == ThemeActivity.this.bubbleRadiusHeaderRow) {
                        headerCell.setText(LocaleController.getString("BubbleRadius", NUM));
                        return;
                    } else {
                        return;
                    }
                case 6:
                    ((BrightnessControlCell) viewHolder.itemView).setProgress(Theme.autoNightBrighnessThreshold);
                    return;
                case 7:
                    TextCheckCell textCheckCell = (TextCheckCell) viewHolder.itemView;
                    if (i == ThemeActivity.this.scheduleLocationRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString("AutoNightLocation", NUM), Theme.autoNightScheduleByLocation, true);
                        return;
                    } else if (i == ThemeActivity.this.enableAnimationsRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString("EnableAnimations", NUM), MessagesController.getGlobalMainSettings().getBoolean("view_animations", true), true);
                        return;
                    } else if (i == ThemeActivity.this.sendByEnterRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString("SendByEnter", NUM), MessagesController.getGlobalMainSettings().getBoolean("send_by_enter", false), true);
                        return;
                    } else if (i == ThemeActivity.this.saveToGalleryRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString("SaveToGallerySettings", NUM), SharedConfig.saveToGallery, true);
                        return;
                    } else if (i == ThemeActivity.this.raiseToSpeakRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString("RaiseToSpeak", NUM), SharedConfig.raiseToSpeak, true);
                        return;
                    } else if (i == ThemeActivity.this.customTabsRow) {
                        textCheckCell.setTextAndValueAndCheck(LocaleController.getString("ChromeCustomTabs", NUM), LocaleController.getString("ChromeCustomTabsInfo", NUM), SharedConfig.customTabs, false, true);
                        return;
                    } else if (i == ThemeActivity.this.directShareRow) {
                        textCheckCell.setTextAndValueAndCheck(LocaleController.getString("DirectShare", NUM), LocaleController.getString("DirectShareInfo", NUM), SharedConfig.directShare, false, true);
                        return;
                    } else if (i == ThemeActivity.this.emojiRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString("LargeEmoji", NUM), SharedConfig.allowBigEmoji, true);
                        return;
                    } else {
                        return;
                    }
                case 10:
                    NotificationsCheckCell notificationsCheckCell = (NotificationsCheckCell) viewHolder.itemView;
                    if (i == ThemeActivity.this.nightThemeRow) {
                        if (Theme.selectedAutoNightType != 0) {
                            z = true;
                        }
                        String currentNightThemeName = z ? Theme.getCurrentNightThemeName() : LocaleController.getString("AutoNightThemeOff", NUM);
                        if (z) {
                            int i8 = Theme.selectedAutoNightType;
                            if (i8 == 1) {
                                str3 = LocaleController.getString("AutoNightScheduled", NUM);
                            } else if (i8 == 3) {
                                str3 = LocaleController.getString("AutoNightSystemDefault", NUM);
                            } else {
                                str3 = LocaleController.getString("AutoNightAdaptive", NUM);
                            }
                            currentNightThemeName = str3 + " " + currentNightThemeName;
                        }
                        notificationsCheckCell.setTextAndValueAndCheck(LocaleController.getString("AutoNightTheme", NUM), currentNightThemeName, z, true);
                        return;
                    }
                    return;
                case 11:
                    if (this.first) {
                        ThemeActivity.this.themesHorizontalListCell.scrollToCurrentTheme(ThemeActivity.this.listView.getMeasuredWidth(), false);
                        this.first = false;
                        return;
                    }
                    return;
                case 12:
                    RecyclerListView recyclerListView = (RecyclerListView) viewHolder.itemView;
                    ThemeAccentsListAdapter themeAccentsListAdapter = (ThemeAccentsListAdapter) recyclerListView.getAdapter();
                    themeAccentsListAdapter.notifyDataSetChanged();
                    int access$6100 = themeAccentsListAdapter.findCurrentAccent();
                    if (access$6100 == -1) {
                        access$6100 = themeAccentsListAdapter.getItemCount() - 1;
                    }
                    if (access$6100 != -1) {
                        ((LinearLayoutManager) recyclerListView.getLayoutManager()).scrollToPositionWithOffset(access$6100, (ThemeActivity.this.listView.getMeasuredWidth() / 2) - AndroidUtilities.dp(42.0f));
                        return;
                    }
                    return;
                default:
                    return;
            }
        }

        public void onViewAttachedToWindow(RecyclerView.ViewHolder viewHolder) {
            int itemViewType = viewHolder.getItemViewType();
            if (itemViewType == 4) {
                ((ThemeTypeCell) viewHolder.itemView).setTypeChecked(viewHolder.getAdapterPosition() == Theme.selectedAutoNightType);
            }
            if (itemViewType != 2 && itemViewType != 3) {
                viewHolder.itemView.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            }
        }

        public int getItemViewType(int i) {
            if (i == ThemeActivity.this.scheduleFromRow || i == ThemeActivity.this.distanceRow || i == ThemeActivity.this.scheduleToRow || i == ThemeActivity.this.scheduleUpdateLocationRow || i == ThemeActivity.this.backgroundRow || i == ThemeActivity.this.contactsReimportRow || i == ThemeActivity.this.contactsSortRow || i == ThemeActivity.this.stickersRow) {
                return 1;
            }
            if (i == ThemeActivity.this.automaticBrightnessInfoRow || i == ThemeActivity.this.scheduleLocationInfoRow) {
                return 2;
            }
            if (i == ThemeActivity.this.themeInfoRow || i == ThemeActivity.this.nightTypeInfoRow || i == ThemeActivity.this.scheduleFromToInfoRow || i == ThemeActivity.this.stickersSection2Row || i == ThemeActivity.this.settings2Row || i == ThemeActivity.this.newThemeInfoRow || i == ThemeActivity.this.chatListInfoRow || i == ThemeActivity.this.bubbleRadiusInfoRow) {
                return 3;
            }
            if (i == ThemeActivity.this.nightDisabledRow || i == ThemeActivity.this.nightScheduledRow || i == ThemeActivity.this.nightAutomaticRow || i == ThemeActivity.this.nightSystemDefaultRow) {
                return 4;
            }
            if (i == ThemeActivity.this.scheduleHeaderRow || i == ThemeActivity.this.automaticHeaderRow || i == ThemeActivity.this.preferedHeaderRow || i == ThemeActivity.this.settingsRow || i == ThemeActivity.this.themeHeaderRow || i == ThemeActivity.this.textSizeHeaderRow || i == ThemeActivity.this.chatListHeaderRow || i == ThemeActivity.this.bubbleRadiusHeaderRow) {
                return 5;
            }
            if (i == ThemeActivity.this.automaticBrightnessRow) {
                return 6;
            }
            if (i == ThemeActivity.this.scheduleLocationRow || i == ThemeActivity.this.enableAnimationsRow || i == ThemeActivity.this.sendByEnterRow || i == ThemeActivity.this.saveToGalleryRow || i == ThemeActivity.this.raiseToSpeakRow || i == ThemeActivity.this.customTabsRow || i == ThemeActivity.this.directShareRow || i == ThemeActivity.this.emojiRow) {
                return 7;
            }
            if (i == ThemeActivity.this.textSizeRow) {
                return 8;
            }
            if (i == ThemeActivity.this.chatListRow) {
                return 9;
            }
            if (i == ThemeActivity.this.nightThemeRow) {
                return 10;
            }
            if (i == ThemeActivity.this.themeListRow) {
                return 11;
            }
            if (i == ThemeActivity.this.themeAccentListRow) {
                return 12;
            }
            if (i == ThemeActivity.this.bubbleRadiusRow) {
                return 13;
            }
            return 1;
        }
    }

    private static abstract class TintRecyclerListView extends RecyclerListView {
        TintRecyclerListView(Context context) {
            super(context);
        }
    }

    public ThemeDescription[] getThemeDescriptions() {
        return new ThemeDescription[]{new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextSettingsCell.class, TextCheckCell.class, HeaderCell.class, BrightnessControlCell.class, ThemeTypeCell.class, TextSizeCell.class, BubbleRadiusCell.class, ChatListCell.class, NotificationsCheckCell.class, ThemesHorizontalListCell.class, TintRecyclerListView.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"), new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"), new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultIcon"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultTitle"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUBACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSubmenuBackground"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSubmenuItem"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM | ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSubmenuItemIcon"), new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"), new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "divider"), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"), new ThemeDescription((View) this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText4"), new ThemeDescription((View) this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"), new ThemeDescription((View) this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteValueText"), new ThemeDescription((View) this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueHeader"), new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"), new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrack"), new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrackChecked"), new ThemeDescription((View) this.listView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{BrightnessControlCell.class}, new String[]{"leftImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayIcon"), new ThemeDescription((View) this.listView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{BrightnessControlCell.class}, new String[]{"rightImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayIcon"), new ThemeDescription((View) this.listView, 0, new Class[]{BrightnessControlCell.class}, new String[]{"seekBarView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "player_progressBackground"), new ThemeDescription((View) this.listView, ThemeDescription.FLAG_PROGRESSBAR, new Class[]{BrightnessControlCell.class}, new String[]{"seekBarView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "player_progress"), new ThemeDescription((View) this.listView, 0, new Class[]{ThemeTypeCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"), new ThemeDescription((View) this.listView, 0, new Class[]{ThemeTypeCell.class}, new String[]{"checkImage"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "featuredStickers_addedIcon"), new ThemeDescription((View) this.listView, ThemeDescription.FLAG_PROGRESSBAR, new Class[]{TextSizeCell.class}, new String[]{"sizeBar"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "player_progress"), new ThemeDescription((View) this.listView, 0, new Class[]{TextSizeCell.class}, new String[]{"sizeBar"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "player_progressBackground"), new ThemeDescription((View) this.listView, ThemeDescription.FLAG_PROGRESSBAR, new Class[]{BubbleRadiusCell.class}, new String[]{"sizeBar"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "player_progress"), new ThemeDescription((View) this.listView, 0, new Class[]{BubbleRadiusCell.class}, new String[]{"sizeBar"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "player_progressBackground"), new ThemeDescription(this.listView, 0, new Class[]{ChatListCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "radioBackground"), new ThemeDescription(this.listView, 0, new Class[]{ChatListCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "radioBackgroundChecked"), new ThemeDescription((View) this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"), new ThemeDescription((View) this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText2"), new ThemeDescription((View) this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrack"), new ThemeDescription((View) this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrackChecked"), new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, new Drawable[]{Theme.chat_msgInDrawable, Theme.chat_msgInMediaDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inBubble"), new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, new Drawable[]{Theme.chat_msgInSelectedDrawable, Theme.chat_msgInMediaSelectedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inBubbleSelected"), new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, new Drawable[]{Theme.chat_msgInDrawable.getShadowDrawable(), Theme.chat_msgInMediaDrawable.getShadowDrawable()}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inBubbleShadow"), new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, new Drawable[]{Theme.chat_msgOutDrawable, Theme.chat_msgOutMediaDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outBubble"), new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, new Drawable[]{Theme.chat_msgOutDrawable, Theme.chat_msgOutMediaDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outBubbleGradient"), new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, new Drawable[]{Theme.chat_msgOutSelectedDrawable, Theme.chat_msgOutMediaSelectedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outBubbleSelected"), new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, new Drawable[]{Theme.chat_msgOutDrawable.getShadowDrawable(), Theme.chat_msgOutMediaDrawable.getShadowDrawable()}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outBubbleShadow"), new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_messageTextIn"), new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_messageTextOut"), new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, new Drawable[]{Theme.chat_msgOutCheckDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outSentCheck"), new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, new Drawable[]{Theme.chat_msgOutCheckSelectedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outSentCheckSelected"), new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, new Drawable[]{Theme.chat_msgOutCheckReadDrawable, Theme.chat_msgOutHalfCheckDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outSentCheckRead"), new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, new Drawable[]{Theme.chat_msgOutCheckReadSelectedDrawable, Theme.chat_msgOutHalfCheckSelectedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outSentCheckReadSelected"), new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, new Drawable[]{Theme.chat_msgMediaCheckDrawable, Theme.chat_msgMediaHalfCheckDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_mediaSentCheck"), new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inReplyLine"), new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outReplyLine"), new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inReplyNameText"), new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outReplyNameText"), new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inReplyMessageText"), new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outReplyMessageText"), new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inReplyMediaMessageSelectedText"), new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outReplyMediaMessageSelectedText"), new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inTimeText"), new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outTimeText"), new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inTimeSelectedText"), new ThemeDescription(this.listView, 0, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outTimeSelectedText")};
    }
}
