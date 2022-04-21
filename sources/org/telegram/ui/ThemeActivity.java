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
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.DocumentObject;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.time.SunDate;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.EmojiThemes;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.BrightnessControlCell;
import org.telegram.ui.Cells.ChatListCell;
import org.telegram.ui.Cells.ChatMessageCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.NotificationsCheckCell;
import org.telegram.ui.Cells.RadioButtonCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Cells.ThemePreviewMessagesCell;
import org.telegram.ui.Cells.ThemeTypeCell;
import org.telegram.ui.Cells.ThemesHorizontalListCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.SeekBarView;
import org.telegram.ui.Components.ShareAlert;
import org.telegram.ui.Components.SwipeGestureSettingsView;
import org.telegram.ui.Components.ThemeEditorView;

public class ThemeActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    public static final int THEME_TYPE_BASIC = 0;
    public static final int THEME_TYPE_NIGHT = 1;
    public static final int THEME_TYPE_OTHER = 2;
    public static final int THEME_TYPE_THEMES_BROWSER = 3;
    private static final int create_theme = 1;
    private static final int day_night_switch = 5;
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
    public int chatBlurRow;
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
    public int createNewThemeRow;
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
    public int editThemeRow;
    /* access modifiers changed from: private */
    public int emojiRow;
    /* access modifiers changed from: private */
    public int enableAnimationsRow;
    private GpsLocationListener gpsLocationListener = new GpsLocationListener();
    boolean hasThemeAccents;
    boolean lastIsDarkTheme;
    private LinearLayoutManager layoutManager;
    /* access modifiers changed from: private */
    public ListAdapter listAdapter;
    /* access modifiers changed from: private */
    public RecyclerListView listView;
    /* access modifiers changed from: private */
    public ActionBarMenuItem menuItem;
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
    public int reactionsDoubleTapRow;
    /* access modifiers changed from: private */
    public int rowCount;
    /* access modifiers changed from: private */
    public int saveToGalleryOption1Row;
    /* access modifiers changed from: private */
    public int saveToGalleryOption2Row;
    /* access modifiers changed from: private */
    public int saveToGalleryRow;
    /* access modifiers changed from: private */
    public int saveToGallerySectionRow;
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
    public int selectThemeHeaderRow;
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
    private RLottieDrawable sunDrawable;
    /* access modifiers changed from: private */
    public int swipeGestureHeaderRow;
    /* access modifiers changed from: private */
    public int swipeGestureInfoRow;
    /* access modifiers changed from: private */
    public int swipeGestureRow;
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
    public int themeListRow2;
    /* access modifiers changed from: private */
    public int themePreviewRow;
    /* access modifiers changed from: private */
    public ThemesHorizontalListCell themesHorizontalListCell;
    private boolean updatingLocation;

    public interface SizeChooseViewDelegate {
        void onSizeChanged();
    }

    private class GpsLocationListener implements LocationListener {
        private GpsLocationListener() {
        }

        public void onLocationChanged(Location location) {
            if (location != null) {
                ThemeActivity.this.stopLocationUpdate();
                ThemeActivity.this.updateSunTime(location, false);
            }
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onProviderDisabled(String provider) {
        }
    }

    private class TextSizeCell extends FrameLayout {
        /* access modifiers changed from: private */
        public int endFontSize = 30;
        private int lastWidth;
        /* access modifiers changed from: private */
        public ThemePreviewMessagesCell messagesCell;
        /* access modifiers changed from: private */
        public SeekBarView sizeBar;
        /* access modifiers changed from: private */
        public int startFontSize = 12;
        private TextPaint textPaint;

        public TextSizeCell(Context context) {
            super(context);
            setWillNotDraw(false);
            TextPaint textPaint2 = new TextPaint(1);
            this.textPaint = textPaint2;
            textPaint2.setTextSize((float) AndroidUtilities.dp(16.0f));
            SeekBarView seekBarView = new SeekBarView(context);
            this.sizeBar = seekBarView;
            seekBarView.setReportChanges(true);
            this.sizeBar.setDelegate(new SeekBarView.SeekBarViewDelegate(ThemeActivity.this) {
                public void onSeekBarDrag(boolean stop, float progress) {
                    boolean unused = ThemeActivity.this.setFontSize(Math.round(((float) TextSizeCell.this.startFontSize) + (((float) (TextSizeCell.this.endFontSize - TextSizeCell.this.startFontSize)) * progress)));
                }

                public void onSeekBarPressed(boolean pressed) {
                }

                public CharSequence getContentDescription() {
                    return String.valueOf(Math.round(((float) TextSizeCell.this.startFontSize) + (((float) (TextSizeCell.this.endFontSize - TextSizeCell.this.startFontSize)) * TextSizeCell.this.sizeBar.getProgress())));
                }

                public int getStepsCount() {
                    return TextSizeCell.this.endFontSize - TextSizeCell.this.startFontSize;
                }
            });
            this.sizeBar.setImportantForAccessibility(2);
            addView(this.sizeBar, LayoutHelper.createFrame(-1, 38.0f, 51, 5.0f, 5.0f, 39.0f, 0.0f));
            this.messagesCell = new ThemePreviewMessagesCell(context, ThemeActivity.this.parentLayout, 0);
            if (Build.VERSION.SDK_INT >= 19) {
                this.messagesCell.setImportantForAccessibility(4);
            }
            addView(this.messagesCell, LayoutHelper.createFrame(-1, -2.0f, 51, 0.0f, 53.0f, 0.0f, 0.0f));
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            this.textPaint.setColor(Theme.getColor("windowBackgroundWhiteValueText"));
            canvas.drawText("" + SharedConfig.fontSize, (float) (getMeasuredWidth() - AndroidUtilities.dp(39.0f)), (float) AndroidUtilities.dp(28.0f), this.textPaint);
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            int width = View.MeasureSpec.getSize(widthMeasureSpec);
            if (this.lastWidth != width) {
                SeekBarView seekBarView = this.sizeBar;
                int i = SharedConfig.fontSize;
                int i2 = this.startFontSize;
                seekBarView.setProgress(((float) (i - i2)) / ((float) (this.endFontSize - i2)));
                this.lastWidth = width;
            }
        }

        public void invalidate() {
            super.invalidate();
            this.messagesCell.invalidate();
            this.sizeBar.invalidate();
        }

        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
            super.onInitializeAccessibilityNodeInfo(info);
            this.sizeBar.getSeekBarAccessibilityDelegate().onInitializeAccessibilityNodeInfoInternal(this, info);
        }

        public boolean performAccessibilityAction(int action, Bundle arguments) {
            return super.performAccessibilityAction(action, arguments) || this.sizeBar.getSeekBarAccessibilityDelegate().performAccessibilityActionInternal(this, action, arguments);
        }
    }

    private class BubbleRadiusCell extends FrameLayout {
        /* access modifiers changed from: private */
        public int endRadius = 17;
        /* access modifiers changed from: private */
        public SeekBarView sizeBar;
        /* access modifiers changed from: private */
        public int startRadius = 0;
        private TextPaint textPaint;

        public BubbleRadiusCell(Context context) {
            super(context);
            setWillNotDraw(false);
            TextPaint textPaint2 = new TextPaint(1);
            this.textPaint = textPaint2;
            textPaint2.setTextSize((float) AndroidUtilities.dp(16.0f));
            SeekBarView seekBarView = new SeekBarView(context);
            this.sizeBar = seekBarView;
            seekBarView.setReportChanges(true);
            this.sizeBar.setDelegate(new SeekBarView.SeekBarViewDelegate(ThemeActivity.this) {
                public void onSeekBarDrag(boolean stop, float progress) {
                    boolean unused = ThemeActivity.this.setBubbleRadius(Math.round(((float) BubbleRadiusCell.this.startRadius) + (((float) (BubbleRadiusCell.this.endRadius - BubbleRadiusCell.this.startRadius)) * progress)), false);
                }

                public void onSeekBarPressed(boolean pressed) {
                }

                public CharSequence getContentDescription() {
                    return String.valueOf(Math.round(((float) BubbleRadiusCell.this.startRadius) + (((float) (BubbleRadiusCell.this.endRadius - BubbleRadiusCell.this.startRadius)) * BubbleRadiusCell.this.sizeBar.getProgress())));
                }

                public int getStepsCount() {
                    return BubbleRadiusCell.this.endRadius - BubbleRadiusCell.this.startRadius;
                }
            });
            this.sizeBar.setImportantForAccessibility(2);
            addView(this.sizeBar, LayoutHelper.createFrame(-1, 38.0f, 51, 5.0f, 5.0f, 39.0f, 0.0f));
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            this.textPaint.setColor(Theme.getColor("windowBackgroundWhiteValueText"));
            canvas.drawText("" + SharedConfig.bubbleRadius, (float) (getMeasuredWidth() - AndroidUtilities.dp(39.0f)), (float) AndroidUtilities.dp(28.0f), this.textPaint);
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), NUM), heightMeasureSpec);
            SeekBarView seekBarView = this.sizeBar;
            int i = SharedConfig.bubbleRadius;
            int i2 = this.startRadius;
            seekBarView.setProgress(((float) (i - i2)) / ((float) (this.endRadius - i2)));
        }

        public void invalidate() {
            super.invalidate();
            this.sizeBar.invalidate();
        }

        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
            super.onInitializeAccessibilityNodeInfo(info);
            this.sizeBar.getSeekBarAccessibilityDelegate().onInitializeAccessibilityNodeInfoInternal(this, info);
        }

        public boolean performAccessibilityAction(int action, Bundle arguments) {
            return super.performAccessibilityAction(action, arguments) || this.sizeBar.getSeekBarAccessibilityDelegate().performAccessibilityActionInternal(this, action, arguments);
        }
    }

    public ThemeActivity(int type) {
        this.currentType = type;
        updateRows(true);
    }

    /* access modifiers changed from: private */
    public boolean setBubbleRadius(int size, boolean layout) {
        if (size == SharedConfig.bubbleRadius) {
            return false;
        }
        SharedConfig.bubbleRadius = size;
        SharedPreferences.Editor editor = MessagesController.getGlobalMainSettings().edit();
        editor.putInt("bubbleRadius", SharedConfig.bubbleRadius);
        editor.commit();
        RecyclerView.ViewHolder holder = this.listView.findViewHolderForAdapterPosition(this.textSizeRow);
        if (holder != null && (holder.itemView instanceof TextSizeCell)) {
            TextSizeCell cell = (TextSizeCell) holder.itemView;
            ChatMessageCell[] cells = cell.messagesCell.getCells();
            for (int a = 0; a < cells.length; a++) {
                cells[a].getMessageObject().resetLayout();
                cells[a].requestLayout();
            }
            cell.invalidate();
        }
        RecyclerView.ViewHolder holder2 = this.listView.findViewHolderForAdapterPosition(this.bubbleRadiusRow);
        if (holder2 != null && (holder2.itemView instanceof BubbleRadiusCell)) {
            BubbleRadiusCell cell2 = (BubbleRadiusCell) holder2.itemView;
            if (layout) {
                cell2.requestLayout();
            } else {
                cell2.invalidate();
            }
        }
        updateMenuItem();
        return true;
    }

    /* access modifiers changed from: private */
    public boolean setFontSize(int size) {
        if (size == SharedConfig.fontSize) {
            return false;
        }
        SharedConfig.fontSize = size;
        SharedPreferences.Editor editor = MessagesController.getGlobalMainSettings().edit();
        editor.putInt("fons_size", SharedConfig.fontSize);
        editor.commit();
        Theme.chat_msgTextPaint.setTextSize((float) AndroidUtilities.dp((float) SharedConfig.fontSize));
        RecyclerView.ViewHolder holder = this.listView.findViewHolderForAdapterPosition(this.textSizeRow);
        if (holder != null && (holder.itemView instanceof TextSizeCell)) {
            ChatMessageCell[] cells = ((TextSizeCell) holder.itemView).messagesCell.getCells();
            for (int a = 0; a < cells.length; a++) {
                cells[a].getMessageObject().resetLayout();
                cells[a].requestLayout();
            }
        }
        updateMenuItem();
        return true;
    }

    /* access modifiers changed from: private */
    public void updateRows(boolean notify) {
        int i;
        int i2;
        int i3;
        int i4;
        int i5;
        int oldRowCount = this.rowCount;
        int prevThemeAccentListRow = this.themeAccentListRow;
        int prevEditThemeRow = this.editThemeRow;
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
        this.themeListRow2 = -1;
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
        this.reactionsDoubleTapRow = -1;
        this.chatBlurRow = -1;
        this.textSizeRow = -1;
        this.backgroundRow = -1;
        this.settingsRow = -1;
        this.customTabsRow = -1;
        this.directShareRow = -1;
        this.enableAnimationsRow = -1;
        this.raiseToSpeakRow = -1;
        this.sendByEnterRow = -1;
        this.saveToGalleryRow = -1;
        this.saveToGalleryOption1Row = -1;
        this.saveToGalleryOption2Row = -1;
        this.saveToGallerySectionRow = -1;
        this.distanceRow = -1;
        this.settings2Row = -1;
        this.stickersRow = -1;
        this.stickersSection2Row = -1;
        this.swipeGestureHeaderRow = -1;
        this.swipeGestureRow = -1;
        this.swipeGestureInfoRow = -1;
        this.selectThemeHeaderRow = -1;
        this.themePreviewRow = -1;
        this.editThemeRow = -1;
        this.createNewThemeRow = -1;
        this.defaultThemes.clear();
        this.darkThemes.clear();
        int a = 0;
        int N = Theme.themes.size();
        while (true) {
            i = 3;
            if (a >= N) {
                break;
            }
            Theme.ThemeInfo themeInfo = Theme.themes.get(a);
            int i6 = this.currentType;
            if (i6 == 0 || i6 == 3 || (!themeInfo.isLight() && (themeInfo.info == null || themeInfo.info.document != null))) {
                if (themeInfo.pathToFile != null) {
                    this.darkThemes.add(themeInfo);
                } else {
                    this.defaultThemes.add(themeInfo);
                }
            }
            a++;
        }
        Collections.sort(this.defaultThemes, ThemeActivity$$ExternalSyntheticLambda9.INSTANCE);
        int i7 = this.currentType;
        if (i7 == 3) {
            int i8 = this.rowCount;
            int i9 = i8 + 1;
            this.rowCount = i9;
            this.selectThemeHeaderRow = i8;
            int i10 = i9 + 1;
            this.rowCount = i10;
            this.themeListRow2 = i9;
            int i11 = i10 + 1;
            this.rowCount = i11;
            this.chatListInfoRow = i10;
            int i12 = i11 + 1;
            this.rowCount = i12;
            this.themePreviewRow = i11;
            int i13 = i12 + 1;
            this.rowCount = i13;
            this.themeHeaderRow = i12;
            this.rowCount = i13 + 1;
            this.themeListRow = i13;
            boolean hasAccentColors = Theme.getCurrentTheme().hasAccentColors();
            this.hasThemeAccents = hasAccentColors;
            ThemesHorizontalListCell themesHorizontalListCell2 = this.themesHorizontalListCell;
            if (themesHorizontalListCell2 != null) {
                themesHorizontalListCell2.setDrawDivider(hasAccentColors);
            }
            if (this.hasThemeAccents) {
                int i14 = this.rowCount;
                this.rowCount = i14 + 1;
                this.themeAccentListRow = i14;
            }
            int i15 = this.rowCount;
            this.rowCount = i15 + 1;
            this.bubbleRadiusInfoRow = i15;
            Theme.ThemeInfo themeInfo2 = Theme.getCurrentTheme();
            Theme.ThemeAccent accent = themeInfo2.getAccent(false);
            if (themeInfo2.themeAccents != null && !themeInfo2.themeAccents.isEmpty() && accent != null && accent.id >= 100) {
                int i16 = this.rowCount;
                this.rowCount = i16 + 1;
                this.editThemeRow = i16;
            }
            int i17 = this.rowCount;
            int i18 = i17 + 1;
            this.rowCount = i18;
            this.createNewThemeRow = i17;
            this.rowCount = i18 + 1;
            this.swipeGestureInfoRow = i18;
        } else if (i7 == 0) {
            int i19 = this.rowCount;
            int i20 = i19 + 1;
            this.rowCount = i20;
            this.textSizeHeaderRow = i19;
            int i21 = i20 + 1;
            this.rowCount = i21;
            this.textSizeRow = i20;
            int i22 = i21 + 1;
            this.rowCount = i22;
            this.backgroundRow = i21;
            int i23 = i22 + 1;
            this.rowCount = i23;
            this.newThemeInfoRow = i22;
            int i24 = i23 + 1;
            this.rowCount = i24;
            this.themeHeaderRow = i23;
            int i25 = i24 + 1;
            this.rowCount = i25;
            this.themeListRow2 = i24;
            int i26 = i25 + 1;
            this.rowCount = i26;
            this.themeInfoRow = i25;
            int i27 = i26 + 1;
            this.rowCount = i27;
            this.bubbleRadiusHeaderRow = i26;
            int i28 = i27 + 1;
            this.rowCount = i28;
            this.bubbleRadiusRow = i27;
            int i29 = i28 + 1;
            this.rowCount = i29;
            this.bubbleRadiusInfoRow = i28;
            int i30 = i29 + 1;
            this.rowCount = i30;
            this.chatListHeaderRow = i29;
            int i31 = i30 + 1;
            this.rowCount = i31;
            this.chatListRow = i30;
            int i32 = i31 + 1;
            this.rowCount = i32;
            this.chatListInfoRow = i31;
            int i33 = i32 + 1;
            this.rowCount = i33;
            this.swipeGestureHeaderRow = i32;
            int i34 = i33 + 1;
            this.rowCount = i34;
            this.swipeGestureRow = i33;
            int i35 = i34 + 1;
            this.rowCount = i35;
            this.swipeGestureInfoRow = i34;
            int i36 = i35 + 1;
            this.rowCount = i36;
            this.settingsRow = i35;
            int i37 = i36 + 1;
            this.rowCount = i37;
            this.nightThemeRow = i36;
            int i38 = i37 + 1;
            this.rowCount = i38;
            this.customTabsRow = i37;
            int i39 = i38 + 1;
            this.rowCount = i39;
            this.directShareRow = i38;
            int i40 = i39 + 1;
            this.rowCount = i40;
            this.enableAnimationsRow = i39;
            int i41 = i40 + 1;
            this.rowCount = i41;
            this.emojiRow = i40;
            int i42 = i41 + 1;
            this.rowCount = i42;
            this.raiseToSpeakRow = i41;
            int i43 = i42 + 1;
            this.rowCount = i43;
            this.sendByEnterRow = i42;
            this.rowCount = i43 + 1;
            this.saveToGalleryRow = i43;
            if (SharedConfig.canBlurChat()) {
                int i44 = this.rowCount;
                this.rowCount = i44 + 1;
                this.chatBlurRow = i44;
            }
            int i45 = this.rowCount;
            int i46 = i45 + 1;
            this.rowCount = i46;
            this.distanceRow = i45;
            int i47 = i46 + 1;
            this.rowCount = i47;
            this.reactionsDoubleTapRow = i46;
            int i48 = i47 + 1;
            this.rowCount = i48;
            this.settings2Row = i47;
            int i49 = i48 + 1;
            this.rowCount = i49;
            this.stickersRow = i48;
            this.rowCount = i49 + 1;
            this.stickersSection2Row = i49;
        } else {
            int i50 = this.rowCount;
            int i51 = i50 + 1;
            this.rowCount = i51;
            this.nightDisabledRow = i50;
            int i52 = i51 + 1;
            this.rowCount = i52;
            this.nightScheduledRow = i51;
            this.rowCount = i52 + 1;
            this.nightAutomaticRow = i52;
            if (Build.VERSION.SDK_INT >= 29) {
                int i53 = this.rowCount;
                this.rowCount = i53 + 1;
                this.nightSystemDefaultRow = i53;
            }
            int i54 = this.rowCount;
            this.rowCount = i54 + 1;
            this.nightTypeInfoRow = i54;
            if (Theme.selectedAutoNightType == 1) {
                int i55 = this.rowCount;
                int i56 = i55 + 1;
                this.rowCount = i56;
                this.scheduleHeaderRow = i55;
                this.rowCount = i56 + 1;
                this.scheduleLocationRow = i56;
                if (Theme.autoNightScheduleByLocation) {
                    int i57 = this.rowCount;
                    int i58 = i57 + 1;
                    this.rowCount = i58;
                    this.scheduleUpdateLocationRow = i57;
                    this.rowCount = i58 + 1;
                    this.scheduleLocationInfoRow = i58;
                } else {
                    int i59 = this.rowCount;
                    int i60 = i59 + 1;
                    this.rowCount = i60;
                    this.scheduleFromRow = i59;
                    int i61 = i60 + 1;
                    this.rowCount = i61;
                    this.scheduleToRow = i60;
                    this.rowCount = i61 + 1;
                    this.scheduleFromToInfoRow = i61;
                }
            } else if (Theme.selectedAutoNightType == 2) {
                int i62 = this.rowCount;
                int i63 = i62 + 1;
                this.rowCount = i63;
                this.automaticHeaderRow = i62;
                int i64 = i63 + 1;
                this.rowCount = i64;
                this.automaticBrightnessRow = i63;
                this.rowCount = i64 + 1;
                this.automaticBrightnessInfoRow = i64;
            }
            if (Theme.selectedAutoNightType != 0) {
                int i65 = this.rowCount;
                int i66 = i65 + 1;
                this.rowCount = i66;
                this.preferedHeaderRow = i65;
                this.rowCount = i66 + 1;
                this.themeListRow = i66;
                boolean hasAccentColors2 = Theme.getCurrentNightTheme().hasAccentColors();
                this.hasThemeAccents = hasAccentColors2;
                ThemesHorizontalListCell themesHorizontalListCell3 = this.themesHorizontalListCell;
                if (themesHorizontalListCell3 != null) {
                    themesHorizontalListCell3.setDrawDivider(hasAccentColors2);
                }
                if (this.hasThemeAccents) {
                    int i67 = this.rowCount;
                    this.rowCount = i67 + 1;
                    this.themeAccentListRow = i67;
                }
                int i68 = this.rowCount;
                this.rowCount = i68 + 1;
                this.themeInfoRow = i68;
            }
        }
        ThemesHorizontalListCell themesHorizontalListCell4 = this.themesHorizontalListCell;
        if (themesHorizontalListCell4 != null) {
            themesHorizontalListCell4.notifyDataSetChanged(this.listView.getWidth());
        }
        if (this.listAdapter != null) {
            if (this.currentType == 1 && this.previousUpdatedType != Theme.selectedAutoNightType && (i4 = this.previousUpdatedType) != -1) {
                int start = this.nightTypeInfoRow + 1;
                if (i4 != Theme.selectedAutoNightType) {
                    int a2 = 0;
                    while (true) {
                        i5 = 4;
                        if (a2 >= 4) {
                            break;
                        }
                        RecyclerListView.Holder holder = (RecyclerListView.Holder) this.listView.findViewHolderForAdapterPosition(a2);
                        if (holder != null && (holder.itemView instanceof ThemeTypeCell)) {
                            ((ThemeTypeCell) holder.itemView).setTypeChecked(a2 == Theme.selectedAutoNightType);
                        }
                        a2++;
                    }
                    if (Theme.selectedAutoNightType == 0) {
                        this.listAdapter.notifyItemRangeRemoved(start, oldRowCount - start);
                    } else if (Theme.selectedAutoNightType == 1) {
                        int i69 = this.previousUpdatedType;
                        if (i69 == 0) {
                            this.listAdapter.notifyItemRangeInserted(start, this.rowCount - start);
                        } else if (i69 == 2) {
                            this.listAdapter.notifyItemRangeRemoved(start, 3);
                            ListAdapter listAdapter2 = this.listAdapter;
                            if (!Theme.autoNightScheduleByLocation) {
                                i5 = 5;
                            }
                            listAdapter2.notifyItemRangeInserted(start, i5);
                        } else if (i69 == 3) {
                            ListAdapter listAdapter3 = this.listAdapter;
                            if (!Theme.autoNightScheduleByLocation) {
                                i5 = 5;
                            }
                            listAdapter3.notifyItemRangeInserted(start, i5);
                        }
                    } else if (Theme.selectedAutoNightType == 2) {
                        int i70 = this.previousUpdatedType;
                        if (i70 == 0) {
                            this.listAdapter.notifyItemRangeInserted(start, this.rowCount - start);
                        } else if (i70 == 1) {
                            ListAdapter listAdapter4 = this.listAdapter;
                            if (!Theme.autoNightScheduleByLocation) {
                                i5 = 5;
                            }
                            listAdapter4.notifyItemRangeRemoved(start, i5);
                            this.listAdapter.notifyItemRangeInserted(start, 3);
                        } else if (i70 == 3) {
                            this.listAdapter.notifyItemRangeInserted(start, 3);
                        }
                    } else if (Theme.selectedAutoNightType == 3) {
                        int i71 = this.previousUpdatedType;
                        if (i71 == 0) {
                            this.listAdapter.notifyItemRangeInserted(start, this.rowCount - start);
                        } else if (i71 == 2) {
                            this.listAdapter.notifyItemRangeRemoved(start, 3);
                        } else if (i71 == 1) {
                            ListAdapter listAdapter5 = this.listAdapter;
                            if (!Theme.autoNightScheduleByLocation) {
                                i5 = 5;
                            }
                            listAdapter5.notifyItemRangeRemoved(start, i5);
                        }
                    }
                } else if (this.previousByLocation != Theme.autoNightScheduleByLocation) {
                    this.listAdapter.notifyItemRangeRemoved(start + 2, Theme.autoNightScheduleByLocation ? 3 : 2);
                    ListAdapter listAdapter6 = this.listAdapter;
                    int i72 = start + 2;
                    if (Theme.autoNightScheduleByLocation) {
                        i = 2;
                    }
                    listAdapter6.notifyItemRangeInserted(i72, i);
                }
            } else if (notify || this.previousUpdatedType == -1) {
                this.listAdapter.notifyDataSetChanged();
            } else {
                if (prevThemeAccentListRow == -1 && (i3 = this.themeAccentListRow) != -1) {
                    this.listAdapter.notifyItemInserted(i3);
                } else if (prevThemeAccentListRow == -1 || this.themeAccentListRow != -1) {
                    int i73 = this.themeAccentListRow;
                    if (i73 != -1) {
                        this.listAdapter.notifyItemChanged(i73);
                    }
                } else {
                    this.listAdapter.notifyItemRemoved(prevThemeAccentListRow);
                    if (prevEditThemeRow != -1) {
                        prevEditThemeRow--;
                    }
                }
                if (prevEditThemeRow == -1 && (i2 = this.editThemeRow) != -1) {
                    this.listAdapter.notifyItemInserted(i2);
                } else if (prevEditThemeRow != -1 && this.editThemeRow == -1) {
                    this.listAdapter.notifyItemRemoved(prevEditThemeRow);
                }
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
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiLoaded);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.needShareTheme);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.needSetDayNightTheme);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiPreviewThemesChanged);
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
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiLoaded);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.needShareTheme);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.needSetDayNightTheme);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiPreviewThemesChanged);
        getNotificationCenter().removeObserver(this, NotificationCenter.themeUploadedToServer);
        getNotificationCenter().removeObserver(this, NotificationCenter.themeUploadError);
        Theme.saveAutoNightThemeConfig();
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        int i;
        AlertDialog alertDialog;
        int i2;
        if (id == NotificationCenter.locationPermissionGranted) {
            updateSunTime((Location) null, true);
        } else if (id == NotificationCenter.didSetNewWallpapper || id == NotificationCenter.emojiLoaded) {
            RecyclerListView recyclerListView = this.listView;
            if (recyclerListView != null) {
                recyclerListView.invalidateViews();
            }
            updateMenuItem();
        } else if (id == NotificationCenter.themeAccentListUpdated) {
            ListAdapter listAdapter2 = this.listAdapter;
            if (listAdapter2 != null && (i2 = this.themeAccentListRow) != -1) {
                listAdapter2.notifyItemChanged(i2, new Object());
            }
        } else if (id == NotificationCenter.themeListUpdated) {
            updateRows(true);
        } else if (id == NotificationCenter.themeUploadedToServer) {
            Theme.ThemeInfo themeInfo = args[0];
            Theme.ThemeAccent accent = args[1];
            if (themeInfo == this.sharingTheme && accent == this.sharingAccent) {
                StringBuilder sb = new StringBuilder();
                sb.append("https://");
                sb.append(getMessagesController().linkPrefix);
                sb.append("/addtheme/");
                sb.append((accent != null ? accent.info : themeInfo.info).slug);
                String link = sb.toString();
                showDialog(new ShareAlert(getParentActivity(), (ArrayList<MessageObject>) null, link, false, link, false));
                AlertDialog alertDialog2 = this.sharingProgressDialog;
                if (alertDialog2 != null) {
                    alertDialog2.dismiss();
                }
            }
        } else if (id == NotificationCenter.themeUploadError) {
            Theme.ThemeInfo themeInfo2 = args[0];
            Theme.ThemeAccent accent2 = args[1];
            if (themeInfo2 == this.sharingTheme && accent2 == this.sharingAccent && (alertDialog = this.sharingProgressDialog) == null) {
                alertDialog.dismiss();
            }
        } else if (id == NotificationCenter.needShareTheme) {
            if (getParentActivity() != null && !this.isPaused) {
                this.sharingTheme = args[0];
                this.sharingAccent = args[1];
                AlertDialog alertDialog3 = new AlertDialog(getParentActivity(), 3);
                this.sharingProgressDialog = alertDialog3;
                alertDialog3.setCanCancel(true);
                showDialog(this.sharingProgressDialog, new ThemeActivity$$ExternalSyntheticLambda6(this));
            }
        } else if (id == NotificationCenter.needSetDayNightTheme) {
            updateMenuItem();
            checkCurrentDayNight();
        } else if (id == NotificationCenter.emojiPreviewThemesChanged && (i = this.themeListRow2) >= 0) {
            this.listAdapter.notifyItemChanged(i);
        }
    }

    /* renamed from: lambda$didReceivedNotification$1$org-telegram-ui-ThemeActivity  reason: not valid java name */
    public /* synthetic */ void m3293lambda$didReceivedNotification$1$orgtelegramuiThemeActivity(DialogInterface dialog) {
        this.sharingProgressDialog = null;
        this.sharingTheme = null;
        this.sharingAccent = null;
    }

    public View createView(Context context) {
        this.lastIsDarkTheme = !Theme.isCurrentThemeDay();
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setAllowOverlayTitle(false);
        if (AndroidUtilities.isTablet()) {
            this.actionBar.setOccupyStatusBar(false);
        }
        int i = this.currentType;
        if (i == 3) {
            this.actionBar.setTitle(LocaleController.getString("BrowseThemes", NUM));
            ActionBarMenu menu = this.actionBar.createMenu();
            RLottieDrawable rLottieDrawable = new RLottieDrawable(NUM, "NUM", AndroidUtilities.dp(28.0f), AndroidUtilities.dp(28.0f), true, (int[]) null);
            this.sunDrawable = rLottieDrawable;
            if (this.lastIsDarkTheme) {
                rLottieDrawable.setCurrentFrame(rLottieDrawable.getFramesCount() - 1);
            } else {
                rLottieDrawable.setCurrentFrame(0);
            }
            this.sunDrawable.setPlayInDirectionOfCustomEndFrame(true);
            this.menuItem = menu.addItem(5, (Drawable) this.sunDrawable);
        } else if (i == 0) {
            this.actionBar.setTitle(LocaleController.getString("ChatSettings", NUM));
            ActionBarMenuItem addItem = this.actionBar.createMenu().addItem(0, NUM);
            this.menuItem = addItem;
            addItem.setContentDescription(LocaleController.getString("AccDescrMoreOptions", NUM));
            this.menuItem.addSubItem(2, NUM, (CharSequence) LocaleController.getString("ShareTheme", NUM));
            this.menuItem.addSubItem(3, NUM, (CharSequence) LocaleController.getString("EditThemeColors", NUM));
            this.menuItem.addSubItem(1, NUM, (CharSequence) LocaleController.getString("CreateNewThemeMenu", NUM));
            this.menuItem.addSubItem(4, NUM, (CharSequence) LocaleController.getString("ThemeResetToDefaults", NUM));
        } else {
            this.actionBar.setTitle(LocaleController.getString("AutoNightTheme", NUM));
        }
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v2, resolved type: java.lang.Object[]} */
            /* JADX WARNING: Multi-variable type inference failed */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void onItemClick(int r17) {
                /*
                    r16 = this;
                    r0 = r16
                    r1 = r17
                    r2 = -1
                    if (r1 != r2) goto L_0x000e
                    org.telegram.ui.ThemeActivity r2 = org.telegram.ui.ThemeActivity.this
                    r2.finishFragment()
                    goto L_0x01da
                L_0x000e:
                    r3 = 1
                    if (r1 != r3) goto L_0x0018
                    org.telegram.ui.ThemeActivity r2 = org.telegram.ui.ThemeActivity.this
                    r2.createNewTheme()
                    goto L_0x01da
                L_0x0018:
                    r4 = 0
                    r5 = 2
                    if (r1 != r5) goto L_0x0082
                    org.telegram.ui.ActionBar.Theme$ThemeInfo r2 = org.telegram.ui.ActionBar.Theme.getCurrentTheme()
                    org.telegram.ui.ActionBar.Theme$ThemeAccent r6 = r2.getAccent(r4)
                    org.telegram.tgnet.TLRPC$TL_theme r7 = r6.info
                    if (r7 != 0) goto L_0x0045
                    org.telegram.ui.ThemeActivity r7 = org.telegram.ui.ThemeActivity.this
                    org.telegram.messenger.MessagesController r7 = r7.getMessagesController()
                    org.telegram.ui.ActionBar.Theme$ThemeInfo r8 = r6.parentTheme
                    r7.saveThemeToServer(r8, r6)
                    org.telegram.messenger.NotificationCenter r7 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
                    int r8 = org.telegram.messenger.NotificationCenter.needShareTheme
                    java.lang.Object[] r5 = new java.lang.Object[r5]
                    org.telegram.ui.ActionBar.Theme$ThemeInfo r9 = r6.parentTheme
                    r5[r4] = r9
                    r5[r3] = r6
                    r7.postNotificationName(r8, r5)
                    goto L_0x0080
                L_0x0045:
                    java.lang.StringBuilder r3 = new java.lang.StringBuilder
                    r3.<init>()
                    java.lang.String r4 = "https://"
                    r3.append(r4)
                    org.telegram.ui.ThemeActivity r4 = org.telegram.ui.ThemeActivity.this
                    org.telegram.messenger.MessagesController r4 = r4.getMessagesController()
                    java.lang.String r4 = r4.linkPrefix
                    r3.append(r4)
                    java.lang.String r4 = "/addtheme/"
                    r3.append(r4)
                    org.telegram.tgnet.TLRPC$TL_theme r4 = r6.info
                    java.lang.String r4 = r4.slug
                    r3.append(r4)
                    java.lang.String r3 = r3.toString()
                    org.telegram.ui.ThemeActivity r4 = org.telegram.ui.ThemeActivity.this
                    org.telegram.ui.Components.ShareAlert r5 = new org.telegram.ui.Components.ShareAlert
                    org.telegram.ui.ThemeActivity r7 = org.telegram.ui.ThemeActivity.this
                    android.app.Activity r8 = r7.getParentActivity()
                    r9 = 0
                    r11 = 0
                    r13 = 0
                    r7 = r5
                    r10 = r3
                    r12 = r3
                    r7.<init>(r8, r9, r10, r11, r12, r13)
                    r4.showDialog(r5)
                L_0x0080:
                    goto L_0x01da
                L_0x0082:
                    r6 = 3
                    if (r1 != r6) goto L_0x008c
                    org.telegram.ui.ThemeActivity r2 = org.telegram.ui.ThemeActivity.this
                    r2.editTheme()
                    goto L_0x01da
                L_0x008c:
                    r7 = 4
                    if (r1 != r7) goto L_0x00f5
                    org.telegram.ui.ThemeActivity r3 = org.telegram.ui.ThemeActivity.this
                    android.app.Activity r3 = r3.getParentActivity()
                    if (r3 != 0) goto L_0x0098
                    return
                L_0x0098:
                    org.telegram.ui.ActionBar.AlertDialog$Builder r3 = new org.telegram.ui.ActionBar.AlertDialog$Builder
                    org.telegram.ui.ThemeActivity r4 = org.telegram.ui.ThemeActivity.this
                    android.app.Activity r4 = r4.getParentActivity()
                    r3.<init>((android.content.Context) r4)
                    r4 = 2131628423(0x7f0e1187, float:1.8884138E38)
                    java.lang.String r5 = "ThemeResetToDefaultsTitle"
                    java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
                    r3.setTitle(r4)
                    r4 = 2131628422(0x7f0e1186, float:1.8884136E38)
                    java.lang.String r5 = "ThemeResetToDefaultsText"
                    java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
                    r3.setMessage(r4)
                    r4 = 2131627785(0x7f0e0var_, float:1.8882844E38)
                    java.lang.String r5 = "Reset"
                    java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
                    org.telegram.ui.ThemeActivity$1$$ExternalSyntheticLambda0 r5 = new org.telegram.ui.ThemeActivity$1$$ExternalSyntheticLambda0
                    r5.<init>(r0)
                    r3.setPositiveButton(r4, r5)
                    r4 = 2131624753(0x7f0e0331, float:1.8876695E38)
                    java.lang.String r5 = "Cancel"
                    java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
                    r5 = 0
                    r3.setNegativeButton(r4, r5)
                    org.telegram.ui.ActionBar.AlertDialog r4 = r3.create()
                    org.telegram.ui.ThemeActivity r5 = org.telegram.ui.ThemeActivity.this
                    r5.showDialog(r4)
                    android.view.View r2 = r4.getButton(r2)
                    android.widget.TextView r2 = (android.widget.TextView) r2
                    if (r2 == 0) goto L_0x01d9
                    java.lang.String r5 = "dialogTextRed2"
                    int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
                    r2.setTextColor(r5)
                    goto L_0x01d9
                L_0x00f5:
                    r8 = 5
                    if (r1 != r8) goto L_0x01d9
                    android.content.Context r9 = org.telegram.messenger.ApplicationLoader.applicationContext
                    java.lang.String r10 = "themeconfig"
                    android.content.SharedPreferences r9 = r9.getSharedPreferences(r10, r4)
                    java.lang.String r10 = "lastDayTheme"
                    java.lang.String r11 = "Blue"
                    java.lang.String r10 = r9.getString(r10, r11)
                    org.telegram.ui.ActionBar.Theme$ThemeInfo r11 = org.telegram.ui.ActionBar.Theme.getTheme(r10)
                    if (r11 == 0) goto L_0x0118
                    org.telegram.ui.ActionBar.Theme$ThemeInfo r11 = org.telegram.ui.ActionBar.Theme.getTheme(r10)
                    boolean r11 = r11.isDark()
                    if (r11 == 0) goto L_0x011a
                L_0x0118:
                    java.lang.String r10 = "Blue"
                L_0x011a:
                    java.lang.String r11 = "lastDarkTheme"
                    java.lang.String r12 = "Dark Blue"
                    java.lang.String r11 = r9.getString(r11, r12)
                    org.telegram.ui.ActionBar.Theme$ThemeInfo r13 = org.telegram.ui.ActionBar.Theme.getTheme(r11)
                    if (r13 == 0) goto L_0x0132
                    org.telegram.ui.ActionBar.Theme$ThemeInfo r13 = org.telegram.ui.ActionBar.Theme.getTheme(r11)
                    boolean r13 = r13.isDark()
                    if (r13 != 0) goto L_0x0134
                L_0x0132:
                    java.lang.String r11 = "Dark Blue"
                L_0x0134:
                    org.telegram.ui.ActionBar.Theme$ThemeInfo r13 = org.telegram.ui.ActionBar.Theme.getActiveTheme()
                    boolean r14 = r10.equals(r11)
                    if (r14 == 0) goto L_0x0158
                    boolean r14 = r13.isDark()
                    if (r14 != 0) goto L_0x0156
                    boolean r12 = r10.equals(r12)
                    if (r12 != 0) goto L_0x0156
                    java.lang.String r12 = "Night"
                    boolean r12 = r10.equals(r12)
                    if (r12 == 0) goto L_0x0153
                    goto L_0x0156
                L_0x0153:
                    java.lang.String r11 = "Dark Blue"
                    goto L_0x0158
                L_0x0156:
                    java.lang.String r10 = "Blue"
                L_0x0158:
                    java.lang.String r12 = r13.getKey()
                    boolean r12 = r10.equals(r12)
                    r14 = r12
                    if (r12 == 0) goto L_0x0168
                    org.telegram.ui.ActionBar.Theme$ThemeInfo r12 = org.telegram.ui.ActionBar.Theme.getTheme(r11)
                    goto L_0x016c
                L_0x0168:
                    org.telegram.ui.ActionBar.Theme$ThemeInfo r12 = org.telegram.ui.ActionBar.Theme.getTheme(r10)
                L_0x016c:
                    int[] r13 = new int[r5]
                    org.telegram.ui.ThemeActivity r15 = org.telegram.ui.ThemeActivity.this
                    org.telegram.ui.ActionBar.ActionBarMenuItem r15 = r15.menuItem
                    org.telegram.ui.Components.RLottieImageView r15 = r15.getIconView()
                    r15.getLocationInWindow(r13)
                    r15 = r13[r4]
                    org.telegram.ui.ThemeActivity r8 = org.telegram.ui.ThemeActivity.this
                    org.telegram.ui.ActionBar.ActionBarMenuItem r8 = r8.menuItem
                    org.telegram.ui.Components.RLottieImageView r8 = r8.getIconView()
                    int r8 = r8.getMeasuredWidth()
                    int r8 = r8 / r5
                    int r15 = r15 + r8
                    r13[r4] = r15
                    r8 = r13[r3]
                    org.telegram.ui.ThemeActivity r15 = org.telegram.ui.ThemeActivity.this
                    org.telegram.ui.ActionBar.ActionBarMenuItem r15 = r15.menuItem
                    org.telegram.ui.Components.RLottieImageView r15 = r15.getIconView()
                    int r15 = r15.getMeasuredHeight()
                    int r15 = r15 / r5
                    int r8 = r8 + r15
                    r13[r3] = r8
                    org.telegram.messenger.NotificationCenter r8 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
                    int r15 = org.telegram.messenger.NotificationCenter.needSetDayNightTheme
                    r7 = 6
                    java.lang.Object[] r7 = new java.lang.Object[r7]
                    r7[r4] = r12
                    java.lang.Boolean r4 = java.lang.Boolean.valueOf(r4)
                    r7[r3] = r4
                    r7[r5] = r13
                    java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
                    r7[r6] = r2
                    java.lang.Boolean r2 = java.lang.Boolean.valueOf(r14)
                    r4 = 4
                    r7[r4] = r2
                    org.telegram.ui.ThemeActivity r2 = org.telegram.ui.ThemeActivity.this
                    org.telegram.ui.ActionBar.ActionBarMenuItem r2 = r2.menuItem
                    org.telegram.ui.Components.RLottieImageView r2 = r2.getIconView()
                    r4 = 5
                    r7[r4] = r2
                    r8.postNotificationName(r15, r7)
                    org.telegram.ui.ThemeActivity r2 = org.telegram.ui.ThemeActivity.this
                    r2.updateRows(r3)
                    goto L_0x01da
                L_0x01d9:
                L_0x01da:
                    return
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ThemeActivity.AnonymousClass1.onItemClick(int):void");
            }

            /* renamed from: lambda$onItemClick$0$org-telegram-ui-ThemeActivity$1  reason: not valid java name */
            public /* synthetic */ void m3297lambda$onItemClick$0$orgtelegramuiThemeActivity$1(DialogInterface dialogInterface, int i) {
                boolean changed = false;
                if (ThemeActivity.this.setFontSize(AndroidUtilities.isTablet() ? 18 : 16)) {
                    changed = true;
                }
                if (ThemeActivity.this.setBubbleRadius(10, true)) {
                    changed = true;
                }
                if (changed) {
                    ThemeActivity.this.listAdapter.notifyItemChanged(ThemeActivity.this.textSizeRow, new Object());
                    ThemeActivity.this.listAdapter.notifyItemChanged(ThemeActivity.this.bubbleRadiusRow, new Object());
                }
                if (ThemeActivity.this.themesHorizontalListCell != null) {
                    Theme.ThemeInfo themeInfo = Theme.getTheme("Blue");
                    Theme.ThemeInfo currentTheme = Theme.getCurrentTheme();
                    Theme.ThemeAccent accent = themeInfo.themeAccentsMap.get(Theme.DEFALT_THEME_ACCENT_ID);
                    if (accent != null) {
                        Theme.OverrideWallpaperInfo info = new Theme.OverrideWallpaperInfo();
                        info.slug = "d";
                        info.fileName = "Blue_99_wp.jpg";
                        info.originalFileName = "Blue_99_wp.jpg";
                        accent.overrideWallpaper = info;
                        themeInfo.setOverrideWallpaper(info);
                    }
                    boolean z = false;
                    if (themeInfo != currentTheme) {
                        themeInfo.setCurrentAccentId(Theme.DEFALT_THEME_ACCENT_ID);
                        Theme.saveThemeAccents(themeInfo, true, false, true, false);
                        ThemeActivity.this.themesHorizontalListCell.selectTheme(themeInfo);
                        ThemeActivity.this.themesHorizontalListCell.smoothScrollToPosition(0);
                    } else if (themeInfo.currentAccentId != Theme.DEFALT_THEME_ACCENT_ID) {
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
                    } else {
                        Theme.reloadWallpaper();
                    }
                }
            }
        });
        this.listAdapter = new ListAdapter(context);
        FrameLayout frameLayout = new FrameLayout(context);
        frameLayout.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
        this.fragmentView = frameLayout;
        RecyclerListView recyclerListView = new RecyclerListView(context);
        this.listView = recyclerListView;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, 1, false);
        this.layoutManager = linearLayoutManager;
        recyclerListView.setLayoutManager(linearLayoutManager);
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setAdapter(this.listAdapter);
        ((DefaultItemAnimator) this.listView.getItemAnimator()).setDelayAnimations(false);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListenerExtended) new ThemeActivity$$ExternalSyntheticLambda0(this));
        return this.fragmentView;
    }

    /* renamed from: lambda$createView$5$org-telegram-ui-ThemeActivity  reason: not valid java name */
    public /* synthetic */ void m3292lambda$createView$5$orgtelegramuiThemeActivity(View view, int position, float x, float y) {
        int currentMinute;
        int currentHour;
        String type;
        if (position == this.enableAnimationsRow) {
            SharedPreferences preferences = MessagesController.getGlobalMainSettings();
            boolean animations = preferences.getBoolean("view_animations", true);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("view_animations", !animations);
            editor.commit();
            if (view instanceof TextCheckCell) {
                ((TextCheckCell) view).setChecked(!animations);
                return;
            }
            return;
        }
        boolean enabled = false;
        if (position == this.backgroundRow) {
            presentFragment(new WallpapersListActivity(0));
        } else if (position == this.sendByEnterRow) {
            SharedPreferences preferences2 = MessagesController.getGlobalMainSettings();
            boolean send = preferences2.getBoolean("send_by_enter", false);
            SharedPreferences.Editor editor2 = preferences2.edit();
            editor2.putBoolean("send_by_enter", !send);
            editor2.commit();
            if (view instanceof TextCheckCell) {
                ((TextCheckCell) view).setChecked(!send);
            }
        } else if (position == this.raiseToSpeakRow) {
            SharedConfig.toogleRaiseToSpeak();
            if (view instanceof TextCheckCell) {
                ((TextCheckCell) view).setChecked(SharedConfig.raiseToSpeak);
            }
        } else if (position == this.saveToGalleryRow) {
            SharedConfig.toggleSaveToGallery();
            if (view instanceof TextCheckCell) {
                ((TextCheckCell) view).setChecked(SharedConfig.saveToGallery);
            }
        } else if (position == this.distanceRow) {
            if (getParentActivity() != null) {
                AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
                builder.setTitle(LocaleController.getString("DistanceUnitsTitle", NUM));
                builder.setItems(new CharSequence[]{LocaleController.getString("DistanceUnitsAutomatic", NUM), LocaleController.getString("DistanceUnitsKilometers", NUM), LocaleController.getString("DistanceUnitsMiles", NUM)}, new ThemeActivity$$ExternalSyntheticLambda3(this));
                builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                showDialog(builder.create());
            }
        } else if (position == this.customTabsRow) {
            SharedConfig.toggleCustomTabs();
            if (view instanceof TextCheckCell) {
                ((TextCheckCell) view).setChecked(SharedConfig.customTabs);
            }
        } else if (position == this.directShareRow) {
            SharedConfig.toggleDirectShare();
            if (view instanceof TextCheckCell) {
                ((TextCheckCell) view).setChecked(SharedConfig.directShare);
            }
        } else if (position != this.contactsReimportRow) {
            if (position == this.contactsSortRow) {
                if (getParentActivity() != null) {
                    AlertDialog.Builder builder2 = new AlertDialog.Builder((Context) getParentActivity());
                    builder2.setTitle(LocaleController.getString("SortBy", NUM));
                    builder2.setItems(new CharSequence[]{LocaleController.getString("Default", NUM), LocaleController.getString("SortFirstName", NUM), LocaleController.getString("SortLastName", NUM)}, new ThemeActivity$$ExternalSyntheticLambda5(this, position));
                    builder2.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                    showDialog(builder2.create());
                }
            } else if (position == this.stickersRow) {
                presentFragment(new StickersActivity(0));
            } else if (position == this.reactionsDoubleTapRow) {
                presentFragment(new ReactionsDoubleTapManageActivity());
            } else if (position == this.emojiRow) {
                SharedConfig.toggleBigEmoji();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(SharedConfig.allowBigEmoji);
                }
            } else if (position == this.chatBlurRow) {
                SharedConfig.toggleChatBlur();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(SharedConfig.chatBlurEnabled());
                }
            } else if (position == this.nightThemeRow) {
                if ((!LocaleController.isRTL || x > ((float) AndroidUtilities.dp(76.0f))) && (LocaleController.isRTL || x < ((float) (view.getMeasuredWidth() - AndroidUtilities.dp(76.0f))))) {
                    presentFragment(new ThemeActivity(1));
                    return;
                }
                NotificationsCheckCell checkCell = (NotificationsCheckCell) view;
                if (Theme.selectedAutoNightType == 0) {
                    Theme.selectedAutoNightType = 2;
                    checkCell.setChecked(true);
                } else {
                    Theme.selectedAutoNightType = 0;
                    checkCell.setChecked(false);
                }
                Theme.saveAutoNightThemeConfig();
                Theme.checkAutoNightThemeConditions(true);
                if (Theme.selectedAutoNightType != 0) {
                    enabled = true;
                }
                String value = enabled ? Theme.getCurrentNightThemeName() : LocaleController.getString("AutoNightThemeOff", NUM);
                if (enabled) {
                    if (Theme.selectedAutoNightType == 1) {
                        type = LocaleController.getString("AutoNightScheduled", NUM);
                    } else if (Theme.selectedAutoNightType == 3) {
                        type = LocaleController.getString("AutoNightSystemDefault", NUM);
                    } else {
                        type = LocaleController.getString("AutoNightAdaptive", NUM);
                    }
                    value = type + " " + value;
                }
                checkCell.setTextAndValueAndCheck(LocaleController.getString("AutoNightTheme", NUM), value, enabled, true);
            } else if (position == this.nightDisabledRow) {
                if (Theme.selectedAutoNightType != 0) {
                    Theme.selectedAutoNightType = 0;
                    updateRows(true);
                    Theme.checkAutoNightThemeConditions();
                }
            } else if (position == this.nightScheduledRow) {
                if (Theme.selectedAutoNightType != 1) {
                    Theme.selectedAutoNightType = 1;
                    if (Theme.autoNightScheduleByLocation) {
                        updateSunTime((Location) null, true);
                    }
                    updateRows(true);
                    Theme.checkAutoNightThemeConditions();
                }
            } else if (position == this.nightAutomaticRow) {
                if (Theme.selectedAutoNightType != 2) {
                    Theme.selectedAutoNightType = 2;
                    updateRows(true);
                    Theme.checkAutoNightThemeConditions();
                }
            } else if (position == this.nightSystemDefaultRow) {
                if (Theme.selectedAutoNightType != 3) {
                    Theme.selectedAutoNightType = 3;
                    updateRows(true);
                    Theme.checkAutoNightThemeConditions();
                }
            } else if (position == this.scheduleLocationRow) {
                Theme.autoNightScheduleByLocation = !Theme.autoNightScheduleByLocation;
                ((TextCheckCell) view).setChecked(Theme.autoNightScheduleByLocation);
                updateRows(true);
                if (Theme.autoNightScheduleByLocation) {
                    updateSunTime((Location) null, true);
                }
                Theme.checkAutoNightThemeConditions();
            } else if (position == this.scheduleFromRow || position == this.scheduleToRow) {
                if (getParentActivity() != null) {
                    if (position == this.scheduleFromRow) {
                        currentHour = Theme.autoNightDayStartTime / 60;
                        currentMinute = Theme.autoNightDayStartTime - (currentHour * 60);
                    } else {
                        currentHour = Theme.autoNightDayEndTime / 60;
                        currentMinute = Theme.autoNightDayEndTime - (currentHour * 60);
                    }
                    showDialog(new TimePickerDialog(getParentActivity(), new ThemeActivity$$ExternalSyntheticLambda1(this, position, (TextSettingsCell) view), currentHour, currentMinute, true));
                }
            } else if (position == this.scheduleUpdateLocationRow) {
                updateSunTime((Location) null, true);
            } else if (position == this.createNewThemeRow) {
                createNewTheme();
            } else if (position == this.editThemeRow) {
                editTheme();
            }
        }
    }

    /* renamed from: lambda$createView$2$org-telegram-ui-ThemeActivity  reason: not valid java name */
    public /* synthetic */ void m3289lambda$createView$2$orgtelegramuiThemeActivity(DialogInterface dialog, int which) {
        SharedConfig.setDistanceSystemType(which);
        RecyclerView.ViewHolder holder = this.listView.findViewHolderForAdapterPosition(this.distanceRow);
        if (holder != null) {
            this.listAdapter.onBindViewHolder(holder, this.distanceRow);
        }
    }

    /* renamed from: lambda$createView$3$org-telegram-ui-ThemeActivity  reason: not valid java name */
    public /* synthetic */ void m3290lambda$createView$3$orgtelegramuiThemeActivity(int position, DialogInterface dialog, int which) {
        SharedPreferences.Editor editor = MessagesController.getGlobalMainSettings().edit();
        editor.putInt("sortContactsBy", which);
        editor.commit();
        ListAdapter listAdapter2 = this.listAdapter;
        if (listAdapter2 != null) {
            listAdapter2.notifyItemChanged(position);
        }
    }

    /* renamed from: lambda$createView$4$org-telegram-ui-ThemeActivity  reason: not valid java name */
    public /* synthetic */ void m3291lambda$createView$4$orgtelegramuiThemeActivity(int position, TextSettingsCell cell, TimePicker view1, int hourOfDay, int minute) {
        int time = (hourOfDay * 60) + minute;
        if (position == this.scheduleFromRow) {
            Theme.autoNightDayStartTime = time;
            cell.setTextAndValue(LocaleController.getString("AutoNightFrom", NUM), String.format("%02d:%02d", new Object[]{Integer.valueOf(hourOfDay), Integer.valueOf(minute)}), true);
            return;
        }
        Theme.autoNightDayEndTime = time;
        cell.setTextAndValue(LocaleController.getString("AutoNightTo", NUM), String.format("%02d:%02d", new Object[]{Integer.valueOf(hourOfDay), Integer.valueOf(minute)}), true);
    }

    /* access modifiers changed from: private */
    public void editTheme() {
        Theme.ThemeInfo currentTheme = Theme.getCurrentTheme();
        presentFragment(new ThemePreviewActivity(currentTheme, false, 1, currentTheme.getAccent(false).id >= 100, this.currentType == 1));
    }

    /* access modifiers changed from: private */
    public void createNewTheme() {
        if (getParentActivity() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
            builder.setTitle(LocaleController.getString("NewTheme", NUM));
            builder.setMessage(LocaleController.getString("CreateNewThemeAlert", NUM));
            builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
            builder.setPositiveButton(LocaleController.getString("CreateTheme", NUM), new ThemeActivity$$ExternalSyntheticLambda2(this));
            showDialog(builder.create());
        }
    }

    /* renamed from: lambda$createNewTheme$6$org-telegram-ui-ThemeActivity  reason: not valid java name */
    public /* synthetic */ void m3288lambda$createNewTheme$6$orgtelegramuiThemeActivity(DialogInterface dialog, int which) {
        AlertsCreator.createThemeCreateDialog(this, 0, (Theme.ThemeInfo) null, (Theme.ThemeAccent) null);
    }

    public void onResume() {
        super.onResume();
        if (this.listAdapter != null) {
            updateRows(true);
        }
    }

    /* access modifiers changed from: protected */
    public void onTransitionAnimationEnd(boolean isOpen, boolean backward) {
        if (isOpen) {
            AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
            AndroidUtilities.setAdjustResizeToNothing(getParentActivity(), this.classGuid);
        }
    }

    private void updateMenuItem() {
        if (this.menuItem != null) {
            Theme.ThemeInfo themeInfo = Theme.getCurrentTheme();
            Theme.ThemeAccent accent = themeInfo.getAccent(false);
            if (themeInfo.themeAccents == null || themeInfo.themeAccents.isEmpty() || accent == null || accent.id < 100) {
                this.menuItem.hideSubItem(2);
                this.menuItem.hideSubItem(3);
            } else {
                this.menuItem.showSubItem(2);
                this.menuItem.showSubItem(3);
            }
            int fontSize = AndroidUtilities.isTablet() ? 18 : 16;
            Theme.ThemeInfo currentTheme = Theme.getCurrentTheme();
            if (SharedConfig.fontSize == fontSize && SharedConfig.bubbleRadius == 10 && currentTheme.firstAccentIsDefault && currentTheme.currentAccentId == Theme.DEFALT_THEME_ACCENT_ID && (accent == null || accent.overrideWallpaper == null || "d".equals(accent.overrideWallpaper.slug))) {
                this.menuItem.hideSubItem(4);
            } else {
                this.menuItem.showSubItem(4);
            }
        }
    }

    /* access modifiers changed from: private */
    public void updateSunTime(Location lastKnownLocation, boolean forceUpdate) {
        Activity activity;
        LocationManager locationManager = (LocationManager) ApplicationLoader.applicationContext.getSystemService("location");
        if (Build.VERSION.SDK_INT < 23 || (activity = getParentActivity()) == null || activity.checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION") == 0) {
            if (getParentActivity() != null) {
                if (getParentActivity().getPackageManager().hasSystemFeature("android.hardware.location.gps")) {
                    try {
                        if (!((LocationManager) ApplicationLoader.applicationContext.getSystemService("location")).isProviderEnabled("gps")) {
                            AlertDialog.Builder builder = new AlertDialog.Builder((Context) getParentActivity());
                            builder.setTopAnimation(NUM, 72, false, Theme.getColor("dialogTopBackground"));
                            builder.setMessage(LocaleController.getString("GpsDisabledAlertText", NUM));
                            builder.setPositiveButton(LocaleController.getString("ConnectingToProxyEnable", NUM), new ThemeActivity$$ExternalSyntheticLambda4(this));
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
                lastKnownLocation = locationManager.getLastKnownLocation("gps");
                if (lastKnownLocation == null) {
                    lastKnownLocation = locationManager.getLastKnownLocation("network");
                }
                if (lastKnownLocation == null) {
                    lastKnownLocation = locationManager.getLastKnownLocation("passive");
                }
            } catch (Exception e2) {
                FileLog.e((Throwable) e2);
            }
            if (lastKnownLocation == null || forceUpdate) {
                startLocationUpdate();
                if (lastKnownLocation == null) {
                    return;
                }
            }
            Theme.autoNightLocationLatitude = lastKnownLocation.getLatitude();
            Theme.autoNightLocationLongitude = lastKnownLocation.getLongitude();
            int[] time = SunDate.calculateSunriseSunset(Theme.autoNightLocationLatitude, Theme.autoNightLocationLongitude);
            Theme.autoNightSunriseTime = time[0];
            Theme.autoNightSunsetTime = time[1];
            Theme.autoNightCityName = null;
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            Theme.autoNightLastSunCheckDay = calendar.get(5);
            Utilities.globalQueue.postRunnable(new ThemeActivity$$ExternalSyntheticLambda7(this));
            RecyclerListView.Holder holder = (RecyclerListView.Holder) this.listView.findViewHolderForAdapterPosition(this.scheduleLocationInfoRow);
            if (holder != null && (holder.itemView instanceof TextInfoPrivacyCell)) {
                ((TextInfoPrivacyCell) holder.itemView).setText(getLocationSunString());
            }
            if (Theme.autoNightScheduleByLocation && Theme.selectedAutoNightType == 1) {
                Theme.checkAutoNightThemeConditions();
                return;
            }
            return;
        }
        activity.requestPermissions(new String[]{"android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"}, 2);
    }

    /* renamed from: lambda$updateSunTime$7$org-telegram-ui-ThemeActivity  reason: not valid java name */
    public /* synthetic */ void m3294lambda$updateSunTime$7$orgtelegramuiThemeActivity(DialogInterface dialog, int id) {
        if (getParentActivity() != null) {
            try {
                getParentActivity().startActivity(new Intent("android.settings.LOCATION_SOURCE_SETTINGS"));
            } catch (Exception e) {
            }
        }
    }

    /* renamed from: lambda$updateSunTime$9$org-telegram-ui-ThemeActivity  reason: not valid java name */
    public /* synthetic */ void m3296lambda$updateSunTime$9$orgtelegramuiThemeActivity() {
        String name;
        try {
            List<Address> addresses = new Geocoder(ApplicationLoader.applicationContext, Locale.getDefault()).getFromLocation(Theme.autoNightLocationLatitude, Theme.autoNightLocationLongitude, 1);
            if (addresses.size() > 0) {
                name = addresses.get(0).getLocality();
            } else {
                name = null;
            }
        } catch (Exception e) {
            name = null;
        }
        AndroidUtilities.runOnUIThread(new ThemeActivity$$ExternalSyntheticLambda8(this, name));
    }

    /* renamed from: lambda$updateSunTime$8$org-telegram-ui-ThemeActivity  reason: not valid java name */
    public /* synthetic */ void m3295lambda$updateSunTime$8$orgtelegramuiThemeActivity(String nameFinal) {
        RecyclerListView.Holder holder;
        Theme.autoNightCityName = nameFinal;
        if (Theme.autoNightCityName == null) {
            Theme.autoNightCityName = String.format("(%.06f, %.06f)", new Object[]{Double.valueOf(Theme.autoNightLocationLatitude), Double.valueOf(Theme.autoNightLocationLongitude)});
        }
        Theme.saveAutoNightThemeConfig();
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null && (holder = (RecyclerListView.Holder) recyclerListView.findViewHolderForAdapterPosition(this.scheduleUpdateLocationRow)) != null && (holder.itemView instanceof TextSettingsCell)) {
            ((TextSettingsCell) holder.itemView).setTextAndValue(LocaleController.getString("AutoNightUpdateLocation", NUM), Theme.autoNightCityName, false);
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

    /* access modifiers changed from: private */
    public String getLocationSunString() {
        int currentHour = Theme.autoNightSunriseTime / 60;
        String sunriseTimeStr = String.format("%02d:%02d", new Object[]{Integer.valueOf(currentHour), Integer.valueOf(Theme.autoNightSunriseTime - (currentHour * 60))});
        int currentHour2 = Theme.autoNightSunsetTime / 60;
        return LocaleController.formatString("AutoNightUpdateLocationInfo", NUM, String.format("%02d:%02d", new Object[]{Integer.valueOf(currentHour2), Integer.valueOf(Theme.autoNightSunsetTime - (currentHour2 * 60))}), sunriseTimeStr);
    }

    private static class InnerAccentView extends View {
        private ObjectAnimator checkAnimator;
        private boolean checked;
        private float checkedState;
        private Theme.ThemeAccent currentAccent;
        private Theme.ThemeInfo currentTheme;
        private final Paint paint = new Paint(1);

        InnerAccentView(Context context) {
            super(context);
        }

        /* access modifiers changed from: package-private */
        public void setThemeAndColor(Theme.ThemeInfo themeInfo, Theme.ThemeAccent accent) {
            this.currentTheme = themeInfo;
            this.currentAccent = accent;
            updateCheckedState(false);
        }

        /* access modifiers changed from: package-private */
        public void updateCheckedState(boolean animate) {
            this.checked = this.currentTheme.currentAccentId == this.currentAccent.id;
            ObjectAnimator objectAnimator = this.checkAnimator;
            if (objectAnimator != null) {
                objectAnimator.cancel();
            }
            float f = 1.0f;
            if (animate) {
                float[] fArr = new float[1];
                if (!this.checked) {
                    f = 0.0f;
                }
                fArr[0] = f;
                ObjectAnimator ofFloat = ObjectAnimator.ofFloat(this, "checkedState", fArr);
                this.checkAnimator = ofFloat;
                ofFloat.setDuration(200);
                this.checkAnimator.start();
                return;
            }
            if (!this.checked) {
                f = 0.0f;
            }
            setCheckedState(f);
        }

        public void setCheckedState(float state) {
            this.checkedState = state;
            invalidate();
        }

        public float getCheckedState() {
            return this.checkedState;
        }

        /* access modifiers changed from: protected */
        public void onAttachedToWindow() {
            super.onAttachedToWindow();
            updateCheckedState(false);
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(62.0f), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(62.0f), NUM));
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            float radius = (float) AndroidUtilities.dp(20.0f);
            float cx = ((float) getMeasuredWidth()) * 0.5f;
            float cy = ((float) getMeasuredHeight()) * 0.5f;
            this.paint.setColor(this.currentAccent.accentColor);
            this.paint.setStyle(Paint.Style.STROKE);
            this.paint.setStrokeWidth((float) AndroidUtilities.dp(3.0f));
            this.paint.setAlpha(Math.round(this.checkedState * 255.0f));
            canvas.drawCircle(cx, cy, radius - (this.paint.getStrokeWidth() * 0.5f), this.paint);
            this.paint.setAlpha(255);
            this.paint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(cx, cy, radius - (((float) AndroidUtilities.dp(5.0f)) * this.checkedState), this.paint);
            if (this.checkedState != 0.0f) {
                this.paint.setColor(-1);
                this.paint.setAlpha(Math.round(this.checkedState * 255.0f));
                canvas.drawCircle(cx, cy, (float) AndroidUtilities.dp(2.0f), this.paint);
                canvas.drawCircle(cx - (((float) AndroidUtilities.dp(7.0f)) * this.checkedState), cy, (float) AndroidUtilities.dp(2.0f), this.paint);
                canvas.drawCircle((((float) AndroidUtilities.dp(7.0f)) * this.checkedState) + cx, cy, (float) AndroidUtilities.dp(2.0f), this.paint);
            }
            if (this.currentAccent.myMessagesAccentColor != 0 && this.checkedState != 1.0f) {
                this.paint.setColor(this.currentAccent.myMessagesAccentColor);
                canvas.drawCircle(cx, cy, ((float) AndroidUtilities.dp(8.0f)) * (1.0f - this.checkedState), this.paint);
            }
        }

        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
            super.onInitializeAccessibilityNodeInfo(info);
            info.setText(LocaleController.getString("ColorPickerMainColor", NUM));
            info.setClassName(Button.class.getName());
            info.setChecked(this.checked);
            info.setCheckable(true);
            info.setEnabled(true);
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
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(62.0f), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(62.0f), NUM));
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            float centerX = ((float) getMeasuredWidth()) * 0.5f;
            float centerY = ((float) getMeasuredHeight()) * 0.5f;
            float radSmall = (float) AndroidUtilities.dp(5.0f);
            float radRing = ((float) AndroidUtilities.dp(20.0f)) - radSmall;
            this.paint.setStyle(Paint.Style.FILL);
            this.paint.setColor(this.colors[0]);
            canvas.drawCircle(centerX, centerY, radSmall, this.paint);
            double angle = 0.0d;
            for (int a = 0; a < 6; a++) {
                this.paint.setColor(this.colors[a + 1]);
                canvas.drawCircle((((float) Math.sin(angle)) * radRing) + centerX, centerY - (((float) Math.cos(angle)) * radRing), radSmall, this.paint);
                angle += 1.0471975511965976d;
            }
        }

        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
            super.onInitializeAccessibilityNodeInfo(info);
            info.setText(LocaleController.getString("ColorPickerMainColor", NUM));
            info.setClassName(Button.class.getName());
            info.setEnabled(true);
        }
    }

    private class ThemeAccentsListAdapter extends RecyclerListView.SelectionAdapter {
        /* access modifiers changed from: private */
        public Theme.ThemeInfo currentTheme;
        private Context mContext;
        /* access modifiers changed from: private */
        public ArrayList<Theme.ThemeAccent> themeAccents;

        ThemeAccentsListAdapter(Context context) {
            this.mContext = context;
            notifyDataSetChanged();
        }

        public void notifyDataSetChanged() {
            this.currentTheme = ThemeActivity.this.currentType == 1 ? Theme.getCurrentNightTheme() : Theme.getCurrentTheme();
            this.themeAccents = new ArrayList<>(this.currentTheme.themeAccents);
            super.notifyDataSetChanged();
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return false;
        }

        public int getItemViewType(int position) {
            return position == getItemCount() - 1 ? 1 : 0;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            switch (viewType) {
                case 0:
                    return new RecyclerListView.Holder(new InnerAccentView(this.mContext));
                default:
                    return new RecyclerListView.Holder(new InnerCustomAccentView(this.mContext));
            }
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            switch (getItemViewType(position)) {
                case 0:
                    ((InnerAccentView) holder.itemView).setThemeAndColor(this.currentTheme, this.themeAccents.get(position));
                    return;
                case 1:
                    ((InnerCustomAccentView) holder.itemView).setTheme(this.currentTheme);
                    return;
                default:
                    return;
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

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int type = holder.getItemViewType();
            return type == 0 || type == 1 || type == 4 || type == 7 || type == 10 || type == 11 || type == 12 || type == 14 || type == 18;
        }

        /* access modifiers changed from: private */
        public void showOptionsForTheme(Theme.ThemeInfo themeInfo) {
            int[] icons;
            CharSequence[] items;
            boolean hasDelete;
            if (ThemeActivity.this.getParentActivity() == null) {
                return;
            }
            if ((themeInfo.info == null || themeInfo.themeLoaded) && ThemeActivity.this.currentType != 1) {
                AlertDialog.Builder builder = new AlertDialog.Builder((Context) ThemeActivity.this.getParentActivity());
                String str = null;
                if (themeInfo.pathToFile == null) {
                    hasDelete = false;
                    items = new CharSequence[]{null, LocaleController.getString("ExportTheme", NUM)};
                    icons = new int[]{0, NUM};
                } else {
                    hasDelete = themeInfo.info == null || !themeInfo.info.isDefault;
                    CharSequence[] charSequenceArr = new CharSequence[5];
                    charSequenceArr[0] = LocaleController.getString("ShareFile", NUM);
                    charSequenceArr[1] = LocaleController.getString("ExportTheme", NUM);
                    charSequenceArr[2] = (themeInfo.info == null || (!themeInfo.info.isDefault && themeInfo.info.creator)) ? LocaleController.getString("Edit", NUM) : null;
                    charSequenceArr[3] = (themeInfo.info == null || !themeInfo.info.creator) ? null : LocaleController.getString("ThemeSetUrl", NUM);
                    if (hasDelete) {
                        str = LocaleController.getString("Delete", NUM);
                    }
                    charSequenceArr[4] = str;
                    items = charSequenceArr;
                    icons = new int[]{NUM, NUM, NUM, NUM, NUM};
                }
                builder.setItems(items, icons, new ThemeActivity$ListAdapter$$ExternalSyntheticLambda2(this, themeInfo));
                AlertDialog alertDialog = builder.create();
                ThemeActivity.this.showDialog(alertDialog);
                if (hasDelete) {
                    alertDialog.setItemColor(alertDialog.getItemsCount() - 1, Theme.getColor("dialogTextRed2"), Theme.getColor("dialogRedIcon"));
                }
            }
        }

        /* renamed from: lambda$showOptionsForTheme$1$org-telegram-ui-ThemeActivity$ListAdapter  reason: not valid java name */
        public /* synthetic */ void m3303x1cb31ddc(Theme.ThemeInfo themeInfo, DialogInterface dialog, int which) {
            File currentFile;
            if (ThemeActivity.this.getParentActivity() != null) {
                if (which == 0) {
                    if (themeInfo.info == null) {
                        ThemeActivity.this.getMessagesController().saveThemeToServer(themeInfo, (Theme.ThemeAccent) null);
                        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.needShareTheme, themeInfo, null);
                        return;
                    }
                    String link = "https://" + ThemeActivity.this.getMessagesController().linkPrefix + "/addtheme/" + themeInfo.info.slug;
                    ThemeActivity.this.showDialog(new ShareAlert(ThemeActivity.this.getParentActivity(), (ArrayList<MessageObject>) null, link, false, link, false));
                } else if (which == 1) {
                    if (themeInfo.pathToFile == null && themeInfo.assetName == null) {
                        StringBuilder result = new StringBuilder();
                        for (Map.Entry<String, Integer> entry : Theme.getDefaultColors().entrySet()) {
                            result.append(entry.getKey());
                            result.append("=");
                            result.append(entry.getValue());
                            result.append("\n");
                        }
                        currentFile = new File(ApplicationLoader.getFilesDirFixed(), "default_theme.attheme");
                        FileOutputStream stream = null;
                        try {
                            stream = new FileOutputStream(currentFile);
                            stream.write(AndroidUtilities.getStringBytes(result.toString()));
                            try {
                                stream.close();
                            } catch (Exception e) {
                                FileLog.e((Throwable) e);
                            }
                        } catch (Exception e2) {
                            FileLog.e((Throwable) e2);
                            if (stream != null) {
                                stream.close();
                            }
                        } catch (Throwable th) {
                            if (stream != null) {
                                try {
                                    stream.close();
                                } catch (Exception e3) {
                                    FileLog.e((Throwable) e3);
                                }
                            }
                            throw th;
                        }
                    } else {
                        currentFile = themeInfo.assetName != null ? Theme.getAssetFile(themeInfo.assetName) : new File(themeInfo.pathToFile);
                    }
                    String name = themeInfo.name;
                    if (!name.endsWith(".attheme")) {
                        name = name + ".attheme";
                    }
                    File finalFile = new File(FileLoader.getDirectory(4), FileLoader.fixFileName(name));
                    try {
                        if (AndroidUtilities.copyFile(currentFile, finalFile)) {
                            Intent intent = new Intent("android.intent.action.SEND");
                            intent.setType("text/xml");
                            if (Build.VERSION.SDK_INT >= 24) {
                                try {
                                    intent.putExtra("android.intent.extra.STREAM", FileProvider.getUriForFile(ThemeActivity.this.getParentActivity(), "org.telegram.messenger.beta.provider", finalFile));
                                    intent.setFlags(1);
                                } catch (Exception e4) {
                                    intent.putExtra("android.intent.extra.STREAM", Uri.fromFile(finalFile));
                                }
                            } else {
                                intent.putExtra("android.intent.extra.STREAM", Uri.fromFile(finalFile));
                            }
                            ThemeActivity.this.startActivityForResult(Intent.createChooser(intent, LocaleController.getString("ShareFile", NUM)), 500);
                        }
                    } catch (Exception e5) {
                        FileLog.e((Throwable) e5);
                    }
                } else if (which == 2) {
                    if (ThemeActivity.this.parentLayout != null) {
                        Theme.applyTheme(themeInfo);
                        ThemeActivity.this.parentLayout.rebuildAllFragmentViews(true, true);
                        new ThemeEditorView().show(ThemeActivity.this.getParentActivity(), themeInfo);
                    }
                } else if (which == 3) {
                    ThemeActivity.this.presentFragment(new ThemeSetUrlActivity(themeInfo, (Theme.ThemeAccent) null, false));
                } else if (ThemeActivity.this.getParentActivity() != null) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder((Context) ThemeActivity.this.getParentActivity());
                    builder1.setTitle(LocaleController.getString("DeleteThemeTitle", NUM));
                    builder1.setMessage(LocaleController.getString("DeleteThemeAlert", NUM));
                    builder1.setPositiveButton(LocaleController.getString("Delete", NUM), new ThemeActivity$ListAdapter$$ExternalSyntheticLambda1(this, themeInfo));
                    builder1.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                    AlertDialog alertDialog = builder1.create();
                    ThemeActivity.this.showDialog(alertDialog);
                    TextView button = (TextView) alertDialog.getButton(-1);
                    if (button != null) {
                        button.setTextColor(Theme.getColor("dialogTextRed2"));
                    }
                }
            }
        }

        /* renamed from: lambda$showOptionsForTheme$0$org-telegram-ui-ThemeActivity$ListAdapter  reason: not valid java name */
        public /* synthetic */ void m3302x3771af1b(Theme.ThemeInfo themeInfo, DialogInterface dialogInterface, int i) {
            MessagesController.getInstance(themeInfo.account).saveTheme(themeInfo, (Theme.ThemeAccent) null, themeInfo == Theme.getCurrentNightTheme(), true);
            if (Theme.deleteTheme(themeInfo)) {
                ThemeActivity.this.parentLayout.rebuildAllFragmentViews(true, true);
            }
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.themeListUpdated, new Object[0]);
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v1, resolved type: org.telegram.ui.Cells.ThemesHorizontalListCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v12, resolved type: org.telegram.ui.Cells.ThemesHorizontalListCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v13, resolved type: org.telegram.ui.Cells.ThemesHorizontalListCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v18, resolved type: org.telegram.ui.DefaultThemesPreviewCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v16, resolved type: org.telegram.ui.Cells.ThemesHorizontalListCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v17, resolved type: org.telegram.ui.Cells.ThemesHorizontalListCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v21, resolved type: org.telegram.ui.Cells.TextSettingsCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v22, resolved type: org.telegram.ui.Cells.TextInfoPrivacyCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v23, resolved type: org.telegram.ui.Cells.ShadowSectionCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v24, resolved type: org.telegram.ui.Cells.ThemeTypeCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v25, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v26, resolved type: org.telegram.ui.ThemeActivity$ListAdapter$1} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v27, resolved type: org.telegram.ui.Cells.TextCheckCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v28, resolved type: org.telegram.ui.ThemeActivity$TextSizeCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v29, resolved type: org.telegram.ui.ThemeActivity$ListAdapter$2} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v30, resolved type: org.telegram.ui.Cells.NotificationsCheckCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v31, resolved type: org.telegram.ui.ThemeActivity$BubbleRadiusCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v32, resolved type: org.telegram.ui.Components.SwipeGestureSettingsView} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v33, resolved type: org.telegram.ui.Cells.ThemePreviewMessagesCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v34, resolved type: org.telegram.ui.Cells.TextSettingsCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v35, resolved type: org.telegram.ui.Cells.RadioButtonCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v36, resolved type: org.telegram.ui.Cells.TextCell} */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public androidx.recyclerview.widget.RecyclerView.ViewHolder onCreateViewHolder(android.view.ViewGroup r11, int r12) {
            /*
                r10 = this;
                r0 = -1
                r1 = 0
                java.lang.String r2 = "windowBackgroundWhite"
                switch(r12) {
                    case 1: goto L_0x01c3;
                    case 2: goto L_0x01ad;
                    case 3: goto L_0x01a5;
                    case 4: goto L_0x0196;
                    case 5: goto L_0x0187;
                    case 6: goto L_0x0178;
                    case 7: goto L_0x0169;
                    case 8: goto L_0x0158;
                    case 9: goto L_0x0148;
                    case 10: goto L_0x0134;
                    case 11: goto L_0x00e4;
                    case 12: goto L_0x0081;
                    case 13: goto L_0x006f;
                    case 14: goto L_0x0007;
                    case 15: goto L_0x0060;
                    case 16: goto L_0x0046;
                    case 17: goto L_0x0029;
                    case 18: goto L_0x0020;
                    case 19: goto L_0x0017;
                    default: goto L_0x0007;
                }
            L_0x0007:
                org.telegram.ui.Cells.TextCell r0 = new org.telegram.ui.Cells.TextCell
                android.content.Context r1 = r10.mContext
                r0.<init>(r1)
                int r1 = org.telegram.ui.ActionBar.Theme.getColor(r2)
                r0.setBackgroundColor(r1)
                goto L_0x01d2
            L_0x0017:
                org.telegram.ui.Cells.RadioButtonCell r0 = new org.telegram.ui.Cells.RadioButtonCell
                android.content.Context r1 = r10.mContext
                r0.<init>(r1)
                goto L_0x01d2
            L_0x0020:
                org.telegram.ui.Cells.TextSettingsCell r0 = new org.telegram.ui.Cells.TextSettingsCell
                android.content.Context r1 = r10.mContext
                r0.<init>(r1)
                goto L_0x01d2
            L_0x0029:
                org.telegram.ui.DefaultThemesPreviewCell r2 = new org.telegram.ui.DefaultThemesPreviewCell
                android.content.Context r3 = r10.mContext
                org.telegram.ui.ThemeActivity r4 = org.telegram.ui.ThemeActivity.this
                int r5 = r4.currentType
                r2.<init>(r3, r4, r5)
                r3 = r2
                r2.setFocusable(r1)
                androidx.recyclerview.widget.RecyclerView$LayoutParams r1 = new androidx.recyclerview.widget.RecyclerView$LayoutParams
                r4 = -2
                r1.<init>((int) r0, (int) r4)
                r3.setLayoutParams(r1)
                r0 = r3
                goto L_0x01d2
            L_0x0046:
                org.telegram.ui.Cells.ThemePreviewMessagesCell r0 = new org.telegram.ui.Cells.ThemePreviewMessagesCell
                android.content.Context r2 = r10.mContext
                org.telegram.ui.ThemeActivity r3 = org.telegram.ui.ThemeActivity.this
                org.telegram.ui.ActionBar.ActionBarLayout r3 = r3.parentLayout
                r0.<init>(r2, r3, r1)
                int r1 = android.os.Build.VERSION.SDK_INT
                r2 = 19
                if (r1 < r2) goto L_0x005d
                r1 = 4
                r0.setImportantForAccessibility(r1)
            L_0x005d:
                r1 = r0
                goto L_0x01d2
            L_0x0060:
                org.telegram.ui.Components.SwipeGestureSettingsView r0 = new org.telegram.ui.Components.SwipeGestureSettingsView
                android.content.Context r1 = r10.mContext
                org.telegram.ui.ThemeActivity r2 = org.telegram.ui.ThemeActivity.this
                int r2 = r2.currentAccount
                r0.<init>(r1, r2)
                goto L_0x01d2
            L_0x006f:
                org.telegram.ui.ThemeActivity$BubbleRadiusCell r0 = new org.telegram.ui.ThemeActivity$BubbleRadiusCell
                org.telegram.ui.ThemeActivity r1 = org.telegram.ui.ThemeActivity.this
                android.content.Context r3 = r10.mContext
                r0.<init>(r3)
                int r1 = org.telegram.ui.ActionBar.Theme.getColor(r2)
                r0.setBackgroundColor(r1)
                goto L_0x01d2
            L_0x0081:
                org.telegram.ui.ThemeActivity$ListAdapter$4 r3 = new org.telegram.ui.ThemeActivity$ListAdapter$4
                android.content.Context r4 = r10.mContext
                r3.<init>(r4)
                r3.setFocusable(r1)
                int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
                r3.setBackgroundColor(r2)
                r2 = 0
                r3.setItemAnimator(r2)
                r3.setLayoutAnimation(r2)
                r2 = 1093664768(0x41300000, float:11.0)
                int r4 = org.telegram.messenger.AndroidUtilities.dp(r2)
                int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
                r3.setPadding(r4, r1, r2, r1)
                r3.setClipToPadding(r1)
                androidx.recyclerview.widget.LinearLayoutManager r2 = new androidx.recyclerview.widget.LinearLayoutManager
                android.content.Context r4 = r10.mContext
                r2.<init>(r4)
                r2.setOrientation(r1)
                r3.setLayoutManager(r2)
                org.telegram.ui.ThemeActivity$ThemeAccentsListAdapter r1 = new org.telegram.ui.ThemeActivity$ThemeAccentsListAdapter
                org.telegram.ui.ThemeActivity r4 = org.telegram.ui.ThemeActivity.this
                android.content.Context r5 = r10.mContext
                r1.<init>(r5)
                r3.setAdapter(r1)
                org.telegram.ui.ThemeActivity$ListAdapter$$ExternalSyntheticLambda4 r4 = new org.telegram.ui.ThemeActivity$ListAdapter$$ExternalSyntheticLambda4
                r4.<init>(r10, r1, r3)
                r3.setOnItemClickListener((org.telegram.ui.Components.RecyclerListView.OnItemClickListener) r4)
                org.telegram.ui.ThemeActivity$ListAdapter$$ExternalSyntheticLambda5 r4 = new org.telegram.ui.ThemeActivity$ListAdapter$$ExternalSyntheticLambda5
                r4.<init>(r10, r1)
                r3.setOnItemLongClickListener((org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener) r4)
                r4 = r3
                androidx.recyclerview.widget.RecyclerView$LayoutParams r5 = new androidx.recyclerview.widget.RecyclerView$LayoutParams
                r6 = 1115160576(0x42780000, float:62.0)
                int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
                r5.<init>((int) r0, (int) r6)
                r4.setLayoutParams(r5)
                r0 = r4
                goto L_0x01d2
            L_0x00e4:
                r2 = 1
                r10.first = r2
                org.telegram.ui.ThemeActivity r2 = org.telegram.ui.ThemeActivity.this
                org.telegram.ui.ThemeActivity$ListAdapter$3 r9 = new org.telegram.ui.ThemeActivity$ListAdapter$3
                android.content.Context r5 = r10.mContext
                org.telegram.ui.ThemeActivity r3 = org.telegram.ui.ThemeActivity.this
                int r6 = r3.currentType
                org.telegram.ui.ThemeActivity r3 = org.telegram.ui.ThemeActivity.this
                java.util.ArrayList r7 = r3.defaultThemes
                org.telegram.ui.ThemeActivity r3 = org.telegram.ui.ThemeActivity.this
                java.util.ArrayList r8 = r3.darkThemes
                r3 = r9
                r4 = r10
                r3.<init>(r5, r6, r7, r8)
                org.telegram.ui.Cells.ThemesHorizontalListCell unused = r2.themesHorizontalListCell = r9
                org.telegram.ui.ThemeActivity r2 = org.telegram.ui.ThemeActivity.this
                org.telegram.ui.Cells.ThemesHorizontalListCell r2 = r2.themesHorizontalListCell
                org.telegram.ui.ThemeActivity r3 = org.telegram.ui.ThemeActivity.this
                boolean r3 = r3.hasThemeAccents
                r2.setDrawDivider(r3)
                org.telegram.ui.ThemeActivity r2 = org.telegram.ui.ThemeActivity.this
                org.telegram.ui.Cells.ThemesHorizontalListCell r2 = r2.themesHorizontalListCell
                r2.setFocusable(r1)
                org.telegram.ui.ThemeActivity r1 = org.telegram.ui.ThemeActivity.this
                org.telegram.ui.Cells.ThemesHorizontalListCell r1 = r1.themesHorizontalListCell
                androidx.recyclerview.widget.RecyclerView$LayoutParams r2 = new androidx.recyclerview.widget.RecyclerView$LayoutParams
                r3 = 1125384192(0x43140000, float:148.0)
                int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
                r2.<init>((int) r0, (int) r3)
                r1.setLayoutParams(r2)
                r0 = r1
                goto L_0x01d2
            L_0x0134:
                org.telegram.ui.Cells.NotificationsCheckCell r0 = new org.telegram.ui.Cells.NotificationsCheckCell
                android.content.Context r3 = r10.mContext
                r4 = 21
                r5 = 64
                r0.<init>(r3, r4, r5, r1)
                int r1 = org.telegram.ui.ActionBar.Theme.getColor(r2)
                r0.setBackgroundColor(r1)
                goto L_0x01d2
            L_0x0148:
                org.telegram.ui.ThemeActivity$ListAdapter$2 r0 = new org.telegram.ui.ThemeActivity$ListAdapter$2
                android.content.Context r1 = r10.mContext
                r0.<init>(r1)
                int r1 = org.telegram.ui.ActionBar.Theme.getColor(r2)
                r0.setBackgroundColor(r1)
                goto L_0x01d2
            L_0x0158:
                org.telegram.ui.ThemeActivity$TextSizeCell r0 = new org.telegram.ui.ThemeActivity$TextSizeCell
                org.telegram.ui.ThemeActivity r1 = org.telegram.ui.ThemeActivity.this
                android.content.Context r3 = r10.mContext
                r0.<init>(r3)
                int r1 = org.telegram.ui.ActionBar.Theme.getColor(r2)
                r0.setBackgroundColor(r1)
                goto L_0x01d2
            L_0x0169:
                org.telegram.ui.Cells.TextCheckCell r0 = new org.telegram.ui.Cells.TextCheckCell
                android.content.Context r1 = r10.mContext
                r0.<init>(r1)
                int r1 = org.telegram.ui.ActionBar.Theme.getColor(r2)
                r0.setBackgroundColor(r1)
                goto L_0x01d2
            L_0x0178:
                org.telegram.ui.ThemeActivity$ListAdapter$1 r0 = new org.telegram.ui.ThemeActivity$ListAdapter$1
                android.content.Context r1 = r10.mContext
                r0.<init>(r1)
                int r1 = org.telegram.ui.ActionBar.Theme.getColor(r2)
                r0.setBackgroundColor(r1)
                goto L_0x01d2
            L_0x0187:
                org.telegram.ui.Cells.HeaderCell r0 = new org.telegram.ui.Cells.HeaderCell
                android.content.Context r1 = r10.mContext
                r0.<init>(r1)
                int r1 = org.telegram.ui.ActionBar.Theme.getColor(r2)
                r0.setBackgroundColor(r1)
                goto L_0x01d2
            L_0x0196:
                org.telegram.ui.Cells.ThemeTypeCell r0 = new org.telegram.ui.Cells.ThemeTypeCell
                android.content.Context r1 = r10.mContext
                r0.<init>(r1)
                int r1 = org.telegram.ui.ActionBar.Theme.getColor(r2)
                r0.setBackgroundColor(r1)
                goto L_0x01d2
            L_0x01a5:
                org.telegram.ui.Cells.ShadowSectionCell r0 = new org.telegram.ui.Cells.ShadowSectionCell
                android.content.Context r1 = r10.mContext
                r0.<init>(r1)
                goto L_0x01d2
            L_0x01ad:
                org.telegram.ui.Cells.TextInfoPrivacyCell r0 = new org.telegram.ui.Cells.TextInfoPrivacyCell
                android.content.Context r1 = r10.mContext
                r0.<init>(r1)
                android.content.Context r1 = r10.mContext
                r2 = 2131165483(0x7var_b, float:1.7945184E38)
                java.lang.String r3 = "windowBackgroundGrayShadow"
                android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r1, (int) r2, (java.lang.String) r3)
                r0.setBackgroundDrawable(r1)
                goto L_0x01d2
            L_0x01c3:
                org.telegram.ui.Cells.TextSettingsCell r0 = new org.telegram.ui.Cells.TextSettingsCell
                android.content.Context r1 = r10.mContext
                r0.<init>(r1)
                int r1 = org.telegram.ui.ActionBar.Theme.getColor(r2)
                r0.setBackgroundColor(r1)
            L_0x01d2:
                org.telegram.ui.Components.RecyclerListView$Holder r1 = new org.telegram.ui.Components.RecyclerListView$Holder
                r1.<init>(r0)
                return r1
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ThemeActivity.ListAdapter.onCreateViewHolder(android.view.ViewGroup, int):androidx.recyclerview.widget.RecyclerView$ViewHolder");
        }

        /* renamed from: lambda$onCreateViewHolder$2$org-telegram-ui-ThemeActivity$ListAdapter  reason: not valid java name */
        public /* synthetic */ void m3298x25CLASSNAMEc2(ThemeAccentsListAdapter accentsAdapter, RecyclerListView accentsListView, View view1, int position) {
            RecyclerListView recyclerListView = accentsListView;
            int i = position;
            Theme.ThemeInfo currentTheme = ThemeActivity.this.currentType == 1 ? Theme.getCurrentNightTheme() : Theme.getCurrentTheme();
            if (i == accentsAdapter.getItemCount() - 1) {
                ThemeActivity.this.presentFragment(new ThemePreviewActivity(currentTheme, false, 1, false, ThemeActivity.this.currentType == 1));
            } else {
                Theme.ThemeAccent accent = (Theme.ThemeAccent) accentsAdapter.themeAccents.get(i);
                if (!TextUtils.isEmpty(accent.patternSlug) && accent.id != Theme.DEFALT_THEME_ACCENT_ID) {
                    Theme.PatternsLoader.createLoader(false);
                }
                if (currentTheme.currentAccentId != accent.id) {
                    NotificationCenter globalInstance = NotificationCenter.getGlobalInstance();
                    int i2 = NotificationCenter.needSetDayNightTheme;
                    Object[] objArr = new Object[4];
                    objArr[0] = currentTheme;
                    objArr[1] = Boolean.valueOf(ThemeActivity.this.currentType == 1);
                    objArr[2] = null;
                    objArr[3] = Integer.valueOf(accent.id);
                    globalInstance.postNotificationName(i2, objArr);
                    EmojiThemes.saveCustomTheme(currentTheme, accent.id);
                } else {
                    ThemeActivity.this.presentFragment(new ThemePreviewActivity(currentTheme, false, 1, accent.id >= 100, ThemeActivity.this.currentType == 1));
                }
            }
            int left = view1.getLeft();
            int right = view1.getRight();
            int extra = AndroidUtilities.dp(52.0f);
            if (left - extra < 0) {
                recyclerListView.smoothScrollBy(left - extra, 0);
            } else if (right + extra > accentsListView.getMeasuredWidth()) {
                recyclerListView.smoothScrollBy((right + extra) - accentsListView.getMeasuredWidth(), 0);
            }
            int count = accentsListView.getChildCount();
            for (int a = 0; a < count; a++) {
                View child = recyclerListView.getChildAt(a);
                if (child instanceof InnerAccentView) {
                    ((InnerAccentView) child).updateCheckedState(true);
                }
            }
        }

        /* renamed from: lambda$onCreateViewHolder$5$org-telegram-ui-ThemeActivity$ListAdapter  reason: not valid java name */
        public /* synthetic */ boolean m3301xb2208205(ThemeAccentsListAdapter accentsAdapter, View view12, int position) {
            if (position < 0 || position >= accentsAdapter.themeAccents.size()) {
                return false;
            }
            Theme.ThemeAccent accent = (Theme.ThemeAccent) accentsAdapter.themeAccents.get(position);
            if (accent.id < 100 || accent.isDefault) {
                return false;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) ThemeActivity.this.getParentActivity());
            CharSequence[] items = new CharSequence[4];
            items[0] = LocaleController.getString("OpenInEditor", NUM);
            items[1] = LocaleController.getString("ShareTheme", NUM);
            items[2] = (accent.info == null || !accent.info.creator) ? null : LocaleController.getString("ThemeSetUrl", NUM);
            items[3] = LocaleController.getString("DeleteTheme", NUM);
            builder.setItems(items, new int[]{NUM, NUM, NUM, NUM}, new ThemeActivity$ListAdapter$$ExternalSyntheticLambda0(this, accent, accentsAdapter));
            AlertDialog alertDialog = builder.create();
            ThemeActivity.this.showDialog(alertDialog);
            alertDialog.setItemColor(alertDialog.getItemsCount() - 1, Theme.getColor("dialogTextRed2"), Theme.getColor("dialogRedIcon"));
            return true;
        }

        /* renamed from: lambda$onCreateViewHolder$4$org-telegram-ui-ThemeActivity$ListAdapter  reason: not valid java name */
        public /* synthetic */ void m3300xccdvar_(Theme.ThemeAccent accent, ThemeAccentsListAdapter accentsAdapter, DialogInterface dialog, int which) {
            if (ThemeActivity.this.getParentActivity() != null) {
                int i = 2;
                if (which == 0) {
                    ThemeActivity themeActivity = ThemeActivity.this;
                    if (which != 1) {
                        i = 1;
                    }
                    AlertsCreator.createThemeCreateDialog(themeActivity, i, accent.parentTheme, accent);
                } else if (which == 1) {
                    if (accent.info == null) {
                        ThemeActivity.this.getMessagesController().saveThemeToServer(accent.parentTheme, accent);
                        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.needShareTheme, accent.parentTheme, accent);
                        return;
                    }
                    String link = "https://" + ThemeActivity.this.getMessagesController().linkPrefix + "/addtheme/" + accent.info.slug;
                    ThemeActivity.this.showDialog(new ShareAlert(ThemeActivity.this.getParentActivity(), (ArrayList<MessageObject>) null, link, false, link, false));
                } else if (which == 2) {
                    ThemeActivity.this.presentFragment(new ThemeSetUrlActivity(accent.parentTheme, accent, false));
                } else if (which == 3 && ThemeActivity.this.getParentActivity() != null) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder((Context) ThemeActivity.this.getParentActivity());
                    builder1.setTitle(LocaleController.getString("DeleteThemeTitle", NUM));
                    builder1.setMessage(LocaleController.getString("DeleteThemeAlert", NUM));
                    builder1.setPositiveButton(LocaleController.getString("Delete", NUM), new ThemeActivity$ListAdapter$$ExternalSyntheticLambda3(this, accentsAdapter, accent));
                    builder1.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                    AlertDialog alertDialog = builder1.create();
                    ThemeActivity.this.showDialog(alertDialog);
                    TextView button = (TextView) alertDialog.getButton(-1);
                    if (button != null) {
                        button.setTextColor(Theme.getColor("dialogTextRed2"));
                    }
                }
            }
        }

        /* renamed from: lambda$onCreateViewHolder$3$org-telegram-ui-ThemeActivity$ListAdapter  reason: not valid java name */
        public /* synthetic */ void m3299xe79da483(ThemeAccentsListAdapter accentsAdapter, Theme.ThemeAccent accent, DialogInterface dialogInterface, int i) {
            if (Theme.deleteThemeAccent(accentsAdapter.currentTheme, accent, true)) {
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

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            String value;
            String value2;
            int i;
            String type;
            TLRPC.TL_availableReaction availableReaction;
            RecyclerView.ViewHolder viewHolder = holder;
            int i2 = position;
            boolean enabled = false;
            boolean z = true;
            switch (holder.getItemViewType()) {
                case 1:
                    TextSettingsCell cell = (TextSettingsCell) viewHolder.itemView;
                    if (i2 == ThemeActivity.this.nightThemeRow) {
                        if (Theme.selectedAutoNightType == 0) {
                            i = NUM;
                        } else if (Theme.getCurrentNightTheme() == null) {
                            i = NUM;
                        } else {
                            cell.setTextAndValue(LocaleController.getString("AutoNightTheme", NUM), Theme.getCurrentNightThemeName(), false);
                            return;
                        }
                        cell.setTextAndValue(LocaleController.getString("AutoNightTheme", i), LocaleController.getString("AutoNightThemeOff", NUM), false);
                        return;
                    } else if (i2 == ThemeActivity.this.scheduleFromRow) {
                        int currentHour = Theme.autoNightDayStartTime / 60;
                        cell.setTextAndValue(LocaleController.getString("AutoNightFrom", NUM), String.format("%02d:%02d", new Object[]{Integer.valueOf(currentHour), Integer.valueOf(Theme.autoNightDayStartTime - (currentHour * 60))}), true);
                        return;
                    } else if (i2 == ThemeActivity.this.scheduleToRow) {
                        int currentHour2 = Theme.autoNightDayEndTime / 60;
                        cell.setTextAndValue(LocaleController.getString("AutoNightTo", NUM), String.format("%02d:%02d", new Object[]{Integer.valueOf(currentHour2), Integer.valueOf(Theme.autoNightDayEndTime - (currentHour2 * 60))}), false);
                        return;
                    } else if (i2 == ThemeActivity.this.scheduleUpdateLocationRow) {
                        cell.setTextAndValue(LocaleController.getString("AutoNightUpdateLocation", NUM), Theme.autoNightCityName, false);
                        return;
                    } else if (i2 == ThemeActivity.this.contactsSortRow) {
                        int sort = MessagesController.getGlobalMainSettings().getInt("sortContactsBy", 0);
                        if (sort == 0) {
                            value2 = LocaleController.getString("Default", NUM);
                        } else if (sort == 1) {
                            value2 = LocaleController.getString("FirstName", NUM);
                        } else {
                            value2 = LocaleController.getString("LastName", NUM);
                        }
                        cell.setTextAndValue(LocaleController.getString("SortBy", NUM), value2, true);
                        return;
                    } else if (i2 == ThemeActivity.this.contactsReimportRow) {
                        cell.setText(LocaleController.getString("ImportContacts", NUM), true);
                        return;
                    } else if (i2 == ThemeActivity.this.stickersRow) {
                        cell.setText(LocaleController.getString("StickersAndMasks", NUM), false);
                        return;
                    } else if (i2 == ThemeActivity.this.distanceRow) {
                        if (SharedConfig.distanceSystemType == 0) {
                            value = LocaleController.getString("DistanceUnitsAutomatic", NUM);
                        } else if (SharedConfig.distanceSystemType == 1) {
                            value = LocaleController.getString("DistanceUnitsKilometers", NUM);
                        } else {
                            value = LocaleController.getString("DistanceUnitsMiles", NUM);
                        }
                        cell.setTextAndValue(LocaleController.getString("DistanceUnits", NUM), value, true);
                        return;
                    } else {
                        return;
                    }
                case 2:
                    TextInfoPrivacyCell cell2 = (TextInfoPrivacyCell) viewHolder.itemView;
                    if (i2 == ThemeActivity.this.automaticBrightnessInfoRow) {
                        cell2.setText(LocaleController.formatString("AutoNightBrightnessInfo", NUM, Integer.valueOf((int) (Theme.autoNightBrighnessThreshold * 100.0f))));
                        return;
                    } else if (i2 == ThemeActivity.this.scheduleLocationInfoRow) {
                        cell2.setText(ThemeActivity.this.getLocationSunString());
                        return;
                    } else {
                        return;
                    }
                case 3:
                    if (i2 == ThemeActivity.this.stickersSection2Row || ((i2 == ThemeActivity.this.nightTypeInfoRow && ThemeActivity.this.themeInfoRow == -1) || ((i2 == ThemeActivity.this.themeInfoRow && ThemeActivity.this.nightTypeInfoRow != -1) || i2 == ThemeActivity.this.saveToGallerySectionRow))) {
                        viewHolder.itemView.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                        return;
                    } else {
                        viewHolder.itemView.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                        return;
                    }
                case 4:
                    ThemeTypeCell typeCell = (ThemeTypeCell) viewHolder.itemView;
                    if (i2 == ThemeActivity.this.nightDisabledRow) {
                        String string = LocaleController.getString("AutoNightDisabled", NUM);
                        if (Theme.selectedAutoNightType == 0) {
                            enabled = true;
                        }
                        typeCell.setValue(string, enabled, true);
                        return;
                    } else if (i2 == ThemeActivity.this.nightScheduledRow) {
                        String string2 = LocaleController.getString("AutoNightScheduled", NUM);
                        if (Theme.selectedAutoNightType == 1) {
                            enabled = true;
                        }
                        typeCell.setValue(string2, enabled, true);
                        return;
                    } else if (i2 == ThemeActivity.this.nightAutomaticRow) {
                        String string3 = LocaleController.getString("AutoNightAdaptive", NUM);
                        boolean z2 = Theme.selectedAutoNightType == 2;
                        if (ThemeActivity.this.nightSystemDefaultRow != -1) {
                            enabled = true;
                        }
                        typeCell.setValue(string3, z2, enabled);
                        return;
                    } else if (i2 == ThemeActivity.this.nightSystemDefaultRow) {
                        String string4 = LocaleController.getString("AutoNightSystemDefault", NUM);
                        if (Theme.selectedAutoNightType != 3) {
                            z = false;
                        }
                        typeCell.setValue(string4, z, false);
                        return;
                    } else {
                        return;
                    }
                case 5:
                    HeaderCell headerCell = (HeaderCell) viewHolder.itemView;
                    if (i2 == ThemeActivity.this.scheduleHeaderRow) {
                        headerCell.setText(LocaleController.getString("AutoNightSchedule", NUM));
                        return;
                    } else if (i2 == ThemeActivity.this.automaticHeaderRow) {
                        headerCell.setText(LocaleController.getString("AutoNightBrightness", NUM));
                        return;
                    } else if (i2 == ThemeActivity.this.preferedHeaderRow) {
                        headerCell.setText(LocaleController.getString("AutoNightPreferred", NUM));
                        return;
                    } else if (i2 == ThemeActivity.this.settingsRow) {
                        headerCell.setText(LocaleController.getString("SETTINGS", NUM));
                        return;
                    } else if (i2 == ThemeActivity.this.themeHeaderRow) {
                        if (ThemeActivity.this.currentType == 3) {
                            headerCell.setText(LocaleController.getString("BuildMyOwnTheme", NUM));
                            return;
                        } else {
                            headerCell.setText(LocaleController.getString("ColorTheme", NUM));
                            return;
                        }
                    } else if (i2 == ThemeActivity.this.textSizeHeaderRow) {
                        headerCell.setText(LocaleController.getString("TextSizeHeader", NUM));
                        return;
                    } else if (i2 == ThemeActivity.this.chatListHeaderRow) {
                        headerCell.setText(LocaleController.getString("ChatList", NUM));
                        return;
                    } else if (i2 == ThemeActivity.this.bubbleRadiusHeaderRow) {
                        headerCell.setText(LocaleController.getString("BubbleRadius", NUM));
                        return;
                    } else if (i2 == ThemeActivity.this.swipeGestureHeaderRow) {
                        headerCell.setText(LocaleController.getString("ChatListSwipeGesture", NUM));
                        return;
                    } else if (i2 == ThemeActivity.this.selectThemeHeaderRow) {
                        headerCell.setText(LocaleController.getString("SelectTheme", NUM));
                        return;
                    } else {
                        return;
                    }
                case 6:
                    ((BrightnessControlCell) viewHolder.itemView).setProgress(Theme.autoNightBrighnessThreshold);
                    return;
                case 7:
                    TextCheckCell textCheckCell = (TextCheckCell) viewHolder.itemView;
                    if (i2 == ThemeActivity.this.scheduleLocationRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString("AutoNightLocation", NUM), Theme.autoNightScheduleByLocation, true);
                        return;
                    } else if (i2 == ThemeActivity.this.enableAnimationsRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString("EnableAnimations", NUM), MessagesController.getGlobalMainSettings().getBoolean("view_animations", true), true);
                        return;
                    } else if (i2 == ThemeActivity.this.sendByEnterRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString("SendByEnter", NUM), MessagesController.getGlobalMainSettings().getBoolean("send_by_enter", false), true);
                        return;
                    } else if (i2 == ThemeActivity.this.saveToGalleryRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString("SaveToGallerySettings", NUM), SharedConfig.saveToGallery, true);
                        return;
                    } else if (i2 == ThemeActivity.this.raiseToSpeakRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString("RaiseToSpeak", NUM), SharedConfig.raiseToSpeak, true);
                        return;
                    } else if (i2 == ThemeActivity.this.customTabsRow) {
                        textCheckCell.setTextAndValueAndCheck(LocaleController.getString("ChromeCustomTabs", NUM), LocaleController.getString("ChromeCustomTabsInfo", NUM), SharedConfig.customTabs, false, true);
                        return;
                    } else if (i2 == ThemeActivity.this.directShareRow) {
                        textCheckCell.setTextAndValueAndCheck(LocaleController.getString("DirectShare", NUM), LocaleController.getString("DirectShareInfo", NUM), SharedConfig.directShare, false, true);
                        return;
                    } else if (i2 == ThemeActivity.this.emojiRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString("LargeEmoji", NUM), SharedConfig.allowBigEmoji, true);
                        return;
                    } else if (i2 == ThemeActivity.this.chatBlurRow) {
                        textCheckCell.setTextAndCheck(LocaleController.getString("BlurInChat", NUM), SharedConfig.chatBlurEnabled(), true);
                        return;
                    } else {
                        return;
                    }
                case 10:
                    NotificationsCheckCell checkCell = (NotificationsCheckCell) viewHolder.itemView;
                    if (i2 == ThemeActivity.this.nightThemeRow) {
                        if (Theme.selectedAutoNightType != 0) {
                            enabled = true;
                        }
                        String value3 = enabled ? Theme.getCurrentNightThemeName() : LocaleController.getString("AutoNightThemeOff", NUM);
                        if (enabled) {
                            if (Theme.selectedAutoNightType == 1) {
                                type = LocaleController.getString("AutoNightScheduled", NUM);
                            } else if (Theme.selectedAutoNightType == 3) {
                                type = LocaleController.getString("AutoNightSystemDefault", NUM);
                            } else {
                                type = LocaleController.getString("AutoNightAdaptive", NUM);
                            }
                            value3 = type + " " + value3;
                        }
                        checkCell.setTextAndValueAndCheck(LocaleController.getString("AutoNightTheme", NUM), value3, enabled, true);
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
                    RecyclerListView accentsList = (RecyclerListView) viewHolder.itemView;
                    ThemeAccentsListAdapter adapter = (ThemeAccentsListAdapter) accentsList.getAdapter();
                    adapter.notifyDataSetChanged();
                    int pos = adapter.findCurrentAccent();
                    if (pos == -1) {
                        pos = adapter.getItemCount() - 1;
                    }
                    if (pos != -1) {
                        ((LinearLayoutManager) accentsList.getLayoutManager()).scrollToPositionWithOffset(pos, (ThemeActivity.this.listView.getMeasuredWidth() / 2) - AndroidUtilities.dp(42.0f));
                        return;
                    }
                    return;
                case 14:
                    TextCell cell3 = (TextCell) viewHolder.itemView;
                    cell3.setColors("windowBackgroundWhiteBlueText4", "windowBackgroundWhiteBlueText4");
                    if (i2 == ThemeActivity.this.backgroundRow) {
                        cell3.setTextAndIcon(LocaleController.getString("ChangeChatBackground", NUM), NUM, false);
                        return;
                    } else if (i2 == ThemeActivity.this.editThemeRow) {
                        cell3.setTextAndIcon(LocaleController.getString("EditCurrentTheme", NUM), NUM, true);
                        return;
                    } else if (i2 == ThemeActivity.this.createNewThemeRow) {
                        cell3.setTextAndIcon(LocaleController.getString("CreateNewTheme", NUM), NUM, false);
                        return;
                    } else {
                        return;
                    }
                case 17:
                    ((DefaultThemesPreviewCell) viewHolder.itemView).updateDayNightMode();
                    return;
                case 18:
                    TextSettingsCell settingsCell = (TextSettingsCell) viewHolder.itemView;
                    settingsCell.setText(LocaleController.getString("DoubleTapSetting", NUM), false);
                    String reaction = MediaDataController.getInstance(ThemeActivity.this.currentAccount).getDoubleTapReaction();
                    if (reaction != null && (availableReaction = MediaDataController.getInstance(ThemeActivity.this.currentAccount).getReactionsMap().get(reaction)) != null) {
                        settingsCell.getValueBackupImageView().getImageReceiver().setImage(ImageLocation.getForDocument(availableReaction.static_icon), "100_100", DocumentObject.getSvgThumb(availableReaction.static_icon.thumbs, "windowBackgroundGray", 1.0f), "webp", availableReaction, 1);
                        return;
                    }
                    return;
                case 19:
                    RadioButtonCell radioCell = (RadioButtonCell) viewHolder.itemView;
                    if (i2 == ThemeActivity.this.saveToGalleryOption1Row) {
                        radioCell.setTextAndValue("save media only from peer chats", "", true, false);
                        return;
                    } else {
                        radioCell.setTextAndValue("save media from all chats", "", true, false);
                        return;
                    }
                default:
                    return;
            }
        }

        public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
            int type = holder.getItemViewType();
            if (type == 4) {
                ((ThemeTypeCell) holder.itemView).setTypeChecked(holder.getAdapterPosition() == Theme.selectedAutoNightType);
            }
            if (type != 2 && type != 3) {
                holder.itemView.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            }
        }

        public int getItemViewType(int position) {
            if (position == ThemeActivity.this.scheduleFromRow || position == ThemeActivity.this.distanceRow || position == ThemeActivity.this.scheduleToRow || position == ThemeActivity.this.scheduleUpdateLocationRow || position == ThemeActivity.this.contactsReimportRow || position == ThemeActivity.this.contactsSortRow || position == ThemeActivity.this.stickersRow) {
                return 1;
            }
            if (position == ThemeActivity.this.automaticBrightnessInfoRow || position == ThemeActivity.this.scheduleLocationInfoRow) {
                return 2;
            }
            if (position == ThemeActivity.this.themeInfoRow || position == ThemeActivity.this.nightTypeInfoRow || position == ThemeActivity.this.scheduleFromToInfoRow || position == ThemeActivity.this.stickersSection2Row || position == ThemeActivity.this.settings2Row || position == ThemeActivity.this.newThemeInfoRow || position == ThemeActivity.this.chatListInfoRow || position == ThemeActivity.this.bubbleRadiusInfoRow || position == ThemeActivity.this.swipeGestureInfoRow || position == ThemeActivity.this.saveToGallerySectionRow) {
                return 3;
            }
            if (position == ThemeActivity.this.nightDisabledRow || position == ThemeActivity.this.nightScheduledRow || position == ThemeActivity.this.nightAutomaticRow || position == ThemeActivity.this.nightSystemDefaultRow) {
                return 4;
            }
            if (position == ThemeActivity.this.scheduleHeaderRow || position == ThemeActivity.this.automaticHeaderRow || position == ThemeActivity.this.preferedHeaderRow || position == ThemeActivity.this.settingsRow || position == ThemeActivity.this.themeHeaderRow || position == ThemeActivity.this.textSizeHeaderRow || position == ThemeActivity.this.chatListHeaderRow || position == ThemeActivity.this.bubbleRadiusHeaderRow || position == ThemeActivity.this.swipeGestureHeaderRow || position == ThemeActivity.this.selectThemeHeaderRow) {
                return 5;
            }
            if (position == ThemeActivity.this.automaticBrightnessRow) {
                return 6;
            }
            if (position == ThemeActivity.this.scheduleLocationRow || position == ThemeActivity.this.enableAnimationsRow || position == ThemeActivity.this.sendByEnterRow || position == ThemeActivity.this.saveToGalleryRow || position == ThemeActivity.this.raiseToSpeakRow || position == ThemeActivity.this.customTabsRow || position == ThemeActivity.this.directShareRow || position == ThemeActivity.this.emojiRow || position == ThemeActivity.this.chatBlurRow) {
                return 7;
            }
            if (position == ThemeActivity.this.textSizeRow) {
                return 8;
            }
            if (position == ThemeActivity.this.chatListRow) {
                return 9;
            }
            if (position == ThemeActivity.this.nightThemeRow) {
                return 10;
            }
            if (position == ThemeActivity.this.themeListRow) {
                return 11;
            }
            if (position == ThemeActivity.this.themeAccentListRow) {
                return 12;
            }
            if (position == ThemeActivity.this.bubbleRadiusRow) {
                return 13;
            }
            if (position == ThemeActivity.this.backgroundRow || position == ThemeActivity.this.editThemeRow || position == ThemeActivity.this.createNewThemeRow) {
                return 14;
            }
            if (position == ThemeActivity.this.swipeGestureRow) {
                return 15;
            }
            if (position == ThemeActivity.this.themePreviewRow) {
                return 16;
            }
            if (position == ThemeActivity.this.themeListRow2) {
                return 17;
            }
            if (position == ThemeActivity.this.reactionsDoubleTapRow) {
                return 18;
            }
            if (position == ThemeActivity.this.saveToGalleryOption1Row || position == ThemeActivity.this.saveToGalleryOption2Row) {
                return 19;
            }
            return 1;
        }
    }

    private static abstract class TintRecyclerListView extends RecyclerListView {
        TintRecyclerListView(Context context) {
            super(context);
        }
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextSettingsCell.class, TextCheckCell.class, HeaderCell.class, BrightnessControlCell.class, ThemeTypeCell.class, TextSizeCell.class, BubbleRadiusCell.class, ChatListCell.class, NotificationsCheckCell.class, ThemesHorizontalListCell.class, TintRecyclerListView.class, TextCell.class, SwipeGestureSettingsView.class, DefaultThemesPreviewCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        themeDescriptions.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultIcon"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultTitle"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUBACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSubmenuBackground"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSubmenuItem"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM | ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSubmenuItemIcon"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "divider"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText4"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteValueText"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueHeader"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueText4"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueText4"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrack"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrackChecked"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{BrightnessControlCell.class}, new String[]{"leftImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayIcon"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{BrightnessControlCell.class}, new String[]{"rightImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayIcon"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{BrightnessControlCell.class}, new String[]{"seekBarView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "player_progressBackground"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_PROGRESSBAR, new Class[]{BrightnessControlCell.class}, new String[]{"seekBarView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "player_progress"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{ThemeTypeCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{ThemeTypeCell.class}, new String[]{"checkImage"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "featuredStickers_addedIcon"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_PROGRESSBAR, new Class[]{TextSizeCell.class}, new String[]{"sizeBar"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "player_progress"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextSizeCell.class}, new String[]{"sizeBar"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "player_progressBackground"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_PROGRESSBAR, new Class[]{BubbleRadiusCell.class}, new String[]{"sizeBar"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "player_progress"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{BubbleRadiusCell.class}, new String[]{"sizeBar"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "player_progressBackground"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{ChatListCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "radioBackground"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{ChatListCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "radioBackgroundChecked"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText2"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrack"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrackChecked"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgInDrawable, Theme.chat_msgInMediaDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inBubble"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgInSelectedDrawable, Theme.chat_msgInMediaSelectedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inBubbleSelected"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, (Paint) null, Theme.chat_msgInDrawable.getShadowDrawables(), (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inBubbleShadow"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, (Paint) null, Theme.chat_msgInMediaDrawable.getShadowDrawables(), (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inBubbleShadow"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgOutDrawable, Theme.chat_msgOutMediaDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outBubble"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgOutDrawable, Theme.chat_msgOutMediaDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outBubbleGradient"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgOutDrawable, Theme.chat_msgOutMediaDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outBubbleGradient2"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgOutDrawable, Theme.chat_msgOutMediaDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outBubbleGradient3"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgOutSelectedDrawable, Theme.chat_msgOutMediaSelectedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outBubbleSelected"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgOutDrawable, Theme.chat_msgOutMediaDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outBubbleShadow"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgInDrawable, Theme.chat_msgInMediaDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inBubbleShadow"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_messageTextIn"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_messageTextOut"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgOutCheckDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outSentCheck"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgOutCheckSelectedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outSentCheckSelected"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgOutCheckReadDrawable, Theme.chat_msgOutHalfCheckDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outSentCheckRead"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgOutCheckReadSelectedDrawable, Theme.chat_msgOutHalfCheckSelectedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outSentCheckReadSelected"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgMediaCheckDrawable, Theme.chat_msgMediaHalfCheckDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_mediaSentCheck"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inReplyLine"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outReplyLine"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inReplyNameText"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outReplyNameText"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inReplyMessageText"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outReplyMessageText"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inReplyMediaMessageSelectedText"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outReplyMediaMessageSelectedText"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inTimeText"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outTimeText"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inTimeSelectedText"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outTimeSelectedText"));
        return themeDescriptions;
    }

    public void checkCurrentDayNight() {
        if (this.currentType == 3) {
            boolean toDark = !Theme.isCurrentThemeDay();
            if (this.lastIsDarkTheme != toDark) {
                this.lastIsDarkTheme = toDark;
                RLottieDrawable rLottieDrawable = this.sunDrawable;
                rLottieDrawable.setCustomEndFrame(toDark ? rLottieDrawable.getFramesCount() - 1 : 0);
                this.menuItem.getIconView().playAnimation();
            }
            if (this.themeListRow2 >= 0) {
                for (int i = 0; i < this.listView.getChildCount(); i++) {
                    if (this.listView.getChildAt(i) instanceof DefaultThemesPreviewCell) {
                        ((DefaultThemesPreviewCell) this.listView.getChildAt(i)).updateDayNightMode();
                    }
                }
            }
        }
    }
}
