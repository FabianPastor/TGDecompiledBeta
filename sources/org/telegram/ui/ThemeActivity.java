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
import org.telegram.messenger.DocumentObject;
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
import org.telegram.tgnet.TLRPC$TL_availableReaction;
import org.telegram.tgnet.TLRPC$TL_theme;
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

public class ThemeActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
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
                public void onSeekBarPressed(boolean z) {
                }

                public void onSeekBarDrag(boolean z, float f) {
                    TextSizeCell textSizeCell = TextSizeCell.this;
                    boolean unused = ThemeActivity.this.setFontSize(Math.round(((float) textSizeCell.startFontSize) + (((float) (TextSizeCell.this.endFontSize - TextSizeCell.this.startFontSize)) * f)));
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
            ThemePreviewMessagesCell themePreviewMessagesCell = new ThemePreviewMessagesCell(context, ThemeActivity.this.parentLayout, 0);
            this.messagesCell = themePreviewMessagesCell;
            if (Build.VERSION.SDK_INT >= 19) {
                themePreviewMessagesCell.setImportantForAccessibility(4);
            }
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

        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
            super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
            this.sizeBar.getSeekBarAccessibilityDelegate().onInitializeAccessibilityNodeInfoInternal(this, accessibilityNodeInfo);
        }

        public boolean performAccessibilityAction(int i, Bundle bundle) {
            return super.performAccessibilityAction(i, bundle) || this.sizeBar.getSeekBarAccessibilityDelegate().performAccessibilityActionInternal(this, i, bundle);
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
                public void onSeekBarPressed(boolean z) {
                }

                public void onSeekBarDrag(boolean z, float f) {
                    BubbleRadiusCell bubbleRadiusCell = BubbleRadiusCell.this;
                    boolean unused = ThemeActivity.this.setBubbleRadius(Math.round(((float) bubbleRadiusCell.startRadius) + (((float) (BubbleRadiusCell.this.endRadius - BubbleRadiusCell.this.startRadius)) * f)), false);
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

        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
            super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
            this.sizeBar.getSeekBarAccessibilityDelegate().onInitializeAccessibilityNodeInfoInternal(this, accessibilityNodeInfo);
        }

        public boolean performAccessibilityAction(int i, Bundle bundle) {
            return super.performAccessibilityAction(i, bundle) || this.sizeBar.getSeekBarAccessibilityDelegate().performAccessibilityActionInternal(this, i, bundle);
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
        int i4;
        int i5;
        int i6;
        TLRPC$TL_theme tLRPC$TL_theme;
        int i7 = this.rowCount;
        int i8 = this.themeAccentListRow;
        int i9 = this.editThemeRow;
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
        int size = Theme.themes.size();
        int i10 = 0;
        while (true) {
            i = 3;
            if (i10 >= size) {
                break;
            }
            Theme.ThemeInfo themeInfo = Theme.themes.get(i10);
            int i11 = this.currentType;
            if (i11 == 0 || i11 == 3 || (!themeInfo.isLight() && ((tLRPC$TL_theme = themeInfo.info) == null || tLRPC$TL_theme.document != null))) {
                if (themeInfo.pathToFile != null) {
                    this.darkThemes.add(themeInfo);
                } else {
                    this.defaultThemes.add(themeInfo);
                }
            }
            i10++;
        }
        Collections.sort(this.defaultThemes, ThemeActivity$$ExternalSyntheticLambda8.INSTANCE);
        int i12 = this.currentType;
        if (i12 == 3) {
            int i13 = this.rowCount;
            int i14 = i13 + 1;
            this.rowCount = i14;
            this.selectThemeHeaderRow = i13;
            int i15 = i14 + 1;
            this.rowCount = i15;
            this.themeListRow2 = i14;
            int i16 = i15 + 1;
            this.rowCount = i16;
            this.chatListInfoRow = i15;
            int i17 = i16 + 1;
            this.rowCount = i17;
            this.themePreviewRow = i16;
            int i18 = i17 + 1;
            this.rowCount = i18;
            this.themeHeaderRow = i17;
            this.rowCount = i18 + 1;
            this.themeListRow = i18;
            boolean hasAccentColors = Theme.getCurrentTheme().hasAccentColors();
            this.hasThemeAccents = hasAccentColors;
            ThemesHorizontalListCell themesHorizontalListCell2 = this.themesHorizontalListCell;
            if (themesHorizontalListCell2 != null) {
                themesHorizontalListCell2.setDrawDivider(hasAccentColors);
            }
            if (this.hasThemeAccents) {
                int i19 = this.rowCount;
                this.rowCount = i19 + 1;
                this.themeAccentListRow = i19;
            }
            int i20 = this.rowCount;
            this.rowCount = i20 + 1;
            this.bubbleRadiusInfoRow = i20;
            Theme.ThemeInfo currentTheme = Theme.getCurrentTheme();
            Theme.ThemeAccent accent = currentTheme.getAccent(false);
            ArrayList<Theme.ThemeAccent> arrayList = currentTheme.themeAccents;
            if (arrayList != null && !arrayList.isEmpty() && accent != null && accent.id >= 100) {
                int i21 = this.rowCount;
                this.rowCount = i21 + 1;
                this.editThemeRow = i21;
            }
            int i22 = this.rowCount;
            int i23 = i22 + 1;
            this.rowCount = i23;
            this.createNewThemeRow = i22;
            this.rowCount = i23 + 1;
            this.swipeGestureInfoRow = i23;
        } else if (i12 == 0) {
            int i24 = this.rowCount;
            int i25 = i24 + 1;
            this.rowCount = i25;
            this.textSizeHeaderRow = i24;
            int i26 = i25 + 1;
            this.rowCount = i26;
            this.textSizeRow = i25;
            int i27 = i26 + 1;
            this.rowCount = i27;
            this.backgroundRow = i26;
            int i28 = i27 + 1;
            this.rowCount = i28;
            this.newThemeInfoRow = i27;
            int i29 = i28 + 1;
            this.rowCount = i29;
            this.themeHeaderRow = i28;
            int i30 = i29 + 1;
            this.rowCount = i30;
            this.themeListRow2 = i29;
            int i31 = i30 + 1;
            this.rowCount = i31;
            this.themeInfoRow = i30;
            int i32 = i31 + 1;
            this.rowCount = i32;
            this.bubbleRadiusHeaderRow = i31;
            int i33 = i32 + 1;
            this.rowCount = i33;
            this.bubbleRadiusRow = i32;
            int i34 = i33 + 1;
            this.rowCount = i34;
            this.bubbleRadiusInfoRow = i33;
            int i35 = i34 + 1;
            this.rowCount = i35;
            this.chatListHeaderRow = i34;
            int i36 = i35 + 1;
            this.rowCount = i36;
            this.chatListRow = i35;
            int i37 = i36 + 1;
            this.rowCount = i37;
            this.chatListInfoRow = i36;
            int i38 = i37 + 1;
            this.rowCount = i38;
            this.swipeGestureHeaderRow = i37;
            int i39 = i38 + 1;
            this.rowCount = i39;
            this.swipeGestureRow = i38;
            int i40 = i39 + 1;
            this.rowCount = i40;
            this.swipeGestureInfoRow = i39;
            int i41 = i40 + 1;
            this.rowCount = i41;
            this.settingsRow = i40;
            int i42 = i41 + 1;
            this.rowCount = i42;
            this.nightThemeRow = i41;
            int i43 = i42 + 1;
            this.rowCount = i43;
            this.customTabsRow = i42;
            int i44 = i43 + 1;
            this.rowCount = i44;
            this.directShareRow = i43;
            int i45 = i44 + 1;
            this.rowCount = i45;
            this.enableAnimationsRow = i44;
            int i46 = i45 + 1;
            this.rowCount = i46;
            this.emojiRow = i45;
            int i47 = i46 + 1;
            this.rowCount = i47;
            this.raiseToSpeakRow = i46;
            int i48 = i47 + 1;
            this.rowCount = i48;
            this.sendByEnterRow = i47;
            this.rowCount = i48 + 1;
            this.saveToGalleryRow = i48;
            if (SharedConfig.canBlurChat()) {
                int i49 = this.rowCount;
                this.rowCount = i49 + 1;
                this.chatBlurRow = i49;
            }
            int i50 = this.rowCount;
            int i51 = i50 + 1;
            this.rowCount = i51;
            this.distanceRow = i50;
            int i52 = i51 + 1;
            this.rowCount = i52;
            this.reactionsDoubleTapRow = i51;
            int i53 = i52 + 1;
            this.rowCount = i53;
            this.settings2Row = i52;
            int i54 = i53 + 1;
            this.rowCount = i54;
            this.stickersRow = i53;
            this.rowCount = i54 + 1;
            this.stickersSection2Row = i54;
        } else {
            int i55 = this.rowCount;
            int i56 = i55 + 1;
            this.rowCount = i56;
            this.nightDisabledRow = i55;
            int i57 = i56 + 1;
            this.rowCount = i57;
            this.nightScheduledRow = i56;
            int i58 = i57 + 1;
            this.rowCount = i58;
            this.nightAutomaticRow = i57;
            if (Build.VERSION.SDK_INT >= 29) {
                this.rowCount = i58 + 1;
                this.nightSystemDefaultRow = i58;
            }
            int i59 = this.rowCount;
            int i60 = i59 + 1;
            this.rowCount = i60;
            this.nightTypeInfoRow = i59;
            int i61 = Theme.selectedAutoNightType;
            if (i61 == 1) {
                int i62 = i60 + 1;
                this.rowCount = i62;
                this.scheduleHeaderRow = i60;
                int i63 = i62 + 1;
                this.rowCount = i63;
                this.scheduleLocationRow = i62;
                if (Theme.autoNightScheduleByLocation) {
                    int i64 = i63 + 1;
                    this.rowCount = i64;
                    this.scheduleUpdateLocationRow = i63;
                    this.rowCount = i64 + 1;
                    this.scheduleLocationInfoRow = i64;
                } else {
                    int i65 = i63 + 1;
                    this.rowCount = i65;
                    this.scheduleFromRow = i63;
                    int i66 = i65 + 1;
                    this.rowCount = i66;
                    this.scheduleToRow = i65;
                    this.rowCount = i66 + 1;
                    this.scheduleFromToInfoRow = i66;
                }
            } else if (i61 == 2) {
                int i67 = i60 + 1;
                this.rowCount = i67;
                this.automaticHeaderRow = i60;
                int i68 = i67 + 1;
                this.rowCount = i68;
                this.automaticBrightnessRow = i67;
                this.rowCount = i68 + 1;
                this.automaticBrightnessInfoRow = i68;
            }
            if (Theme.selectedAutoNightType != 0) {
                int i69 = this.rowCount;
                int i70 = i69 + 1;
                this.rowCount = i70;
                this.preferedHeaderRow = i69;
                this.rowCount = i70 + 1;
                this.themeListRow = i70;
                boolean hasAccentColors2 = Theme.getCurrentNightTheme().hasAccentColors();
                this.hasThemeAccents = hasAccentColors2;
                ThemesHorizontalListCell themesHorizontalListCell3 = this.themesHorizontalListCell;
                if (themesHorizontalListCell3 != null) {
                    themesHorizontalListCell3.setDrawDivider(hasAccentColors2);
                }
                if (this.hasThemeAccents) {
                    int i71 = this.rowCount;
                    this.rowCount = i71 + 1;
                    this.themeAccentListRow = i71;
                }
                int i72 = this.rowCount;
                this.rowCount = i72 + 1;
                this.themeInfoRow = i72;
            }
        }
        ThemesHorizontalListCell themesHorizontalListCell4 = this.themesHorizontalListCell;
        if (themesHorizontalListCell4 != null) {
            themesHorizontalListCell4.notifyDataSetChanged(this.listView.getWidth());
        }
        ListAdapter listAdapter2 = this.listAdapter;
        if (listAdapter2 != null) {
            if (this.currentType == 1 && (i4 = this.previousUpdatedType) != (i5 = Theme.selectedAutoNightType) && i4 != -1) {
                int i73 = this.nightTypeInfoRow + 1;
                if (i4 != i5) {
                    int i74 = 0;
                    while (true) {
                        i6 = 4;
                        if (i74 >= 4) {
                            break;
                        }
                        RecyclerListView.Holder holder = (RecyclerListView.Holder) this.listView.findViewHolderForAdapterPosition(i74);
                        if (holder != null) {
                            View view = holder.itemView;
                            if (view instanceof ThemeTypeCell) {
                                ((ThemeTypeCell) view).setTypeChecked(i74 == Theme.selectedAutoNightType);
                            }
                        }
                        i74++;
                    }
                    int i75 = Theme.selectedAutoNightType;
                    if (i75 == 0) {
                        this.listAdapter.notifyItemRangeRemoved(i73, i7 - i73);
                    } else if (i75 == 1) {
                        int i76 = this.previousUpdatedType;
                        if (i76 == 0) {
                            this.listAdapter.notifyItemRangeInserted(i73, this.rowCount - i73);
                        } else if (i76 == 2) {
                            this.listAdapter.notifyItemRangeRemoved(i73, 3);
                            ListAdapter listAdapter3 = this.listAdapter;
                            if (!Theme.autoNightScheduleByLocation) {
                                i6 = 5;
                            }
                            listAdapter3.notifyItemRangeInserted(i73, i6);
                        } else if (i76 == 3) {
                            ListAdapter listAdapter4 = this.listAdapter;
                            if (!Theme.autoNightScheduleByLocation) {
                                i6 = 5;
                            }
                            listAdapter4.notifyItemRangeInserted(i73, i6);
                        }
                    } else if (i75 == 2) {
                        int i77 = this.previousUpdatedType;
                        if (i77 == 0) {
                            this.listAdapter.notifyItemRangeInserted(i73, this.rowCount - i73);
                        } else if (i77 == 1) {
                            ListAdapter listAdapter5 = this.listAdapter;
                            if (!Theme.autoNightScheduleByLocation) {
                                i6 = 5;
                            }
                            listAdapter5.notifyItemRangeRemoved(i73, i6);
                            this.listAdapter.notifyItemRangeInserted(i73, 3);
                        } else if (i77 == 3) {
                            this.listAdapter.notifyItemRangeInserted(i73, 3);
                        }
                    } else if (i75 == 3) {
                        int i78 = this.previousUpdatedType;
                        if (i78 == 0) {
                            this.listAdapter.notifyItemRangeInserted(i73, this.rowCount - i73);
                        } else if (i78 == 2) {
                            this.listAdapter.notifyItemRangeRemoved(i73, 3);
                        } else if (i78 == 1) {
                            ListAdapter listAdapter6 = this.listAdapter;
                            if (!Theme.autoNightScheduleByLocation) {
                                i6 = 5;
                            }
                            listAdapter6.notifyItemRangeRemoved(i73, i6);
                        }
                    }
                } else {
                    boolean z2 = this.previousByLocation;
                    boolean z3 = Theme.autoNightScheduleByLocation;
                    if (z2 != z3) {
                        int i79 = i73 + 2;
                        listAdapter2.notifyItemRangeRemoved(i79, z3 ? 3 : 2);
                        ListAdapter listAdapter7 = this.listAdapter;
                        if (Theme.autoNightScheduleByLocation) {
                            i = 2;
                        }
                        listAdapter7.notifyItemRangeInserted(i79, i);
                    }
                }
            } else if (z || this.previousUpdatedType == -1) {
                listAdapter2.notifyDataSetChanged();
            } else {
                if (i8 == -1 && (i3 = this.themeAccentListRow) != -1) {
                    listAdapter2.notifyItemInserted(i3);
                } else if (i8 == -1 || this.themeAccentListRow != -1) {
                    int i80 = this.themeAccentListRow;
                    if (i80 != -1) {
                        listAdapter2.notifyItemChanged(i80);
                    }
                } else {
                    listAdapter2.notifyItemRemoved(i8);
                    if (i9 != -1) {
                        i9--;
                    }
                }
                if (i9 == -1 && (i2 = this.editThemeRow) != -1) {
                    this.listAdapter.notifyItemInserted(i2);
                } else if (i9 != -1 && this.editThemeRow == -1) {
                    this.listAdapter.notifyItemRemoved(i9);
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

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        int i3;
        AlertDialog alertDialog;
        int i4;
        if (i == NotificationCenter.locationPermissionGranted) {
            updateSunTime((Location) null, true);
        } else if (i == NotificationCenter.didSetNewWallpapper || i == NotificationCenter.emojiLoaded) {
            RecyclerListView recyclerListView = this.listView;
            if (recyclerListView != null) {
                recyclerListView.invalidateViews();
            }
            updateMenuItem();
        } else if (i == NotificationCenter.themeAccentListUpdated) {
            ListAdapter listAdapter2 = this.listAdapter;
            if (listAdapter2 != null && (i4 = this.themeAccentListRow) != -1) {
                listAdapter2.notifyItemChanged(i4, new Object());
            }
        } else if (i == NotificationCenter.themeListUpdated) {
            updateRows(true);
        } else if (i == NotificationCenter.themeUploadedToServer) {
            Theme.ThemeInfo themeInfo = objArr[0];
            Theme.ThemeAccent themeAccent = objArr[1];
            if (themeInfo == this.sharingTheme && themeAccent == this.sharingAccent) {
                StringBuilder sb = new StringBuilder();
                sb.append("https://");
                sb.append(getMessagesController().linkPrefix);
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
                AlertDialog alertDialog3 = new AlertDialog(getParentActivity(), 3);
                this.sharingProgressDialog = alertDialog3;
                alertDialog3.setCanCancel(true);
                showDialog(this.sharingProgressDialog, new ThemeActivity$$ExternalSyntheticLambda5(this));
            }
        } else if (i == NotificationCenter.needSetDayNightTheme) {
            updateMenuItem();
            checkCurrentDayNight();
        } else if (i == NotificationCenter.emojiPreviewThemesChanged && (i3 = this.themeListRow2) >= 0) {
            this.listAdapter.notifyItemChanged(i3);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$didReceivedNotification$1(DialogInterface dialogInterface) {
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
            ActionBarMenu createMenu = this.actionBar.createMenu();
            RLottieDrawable rLottieDrawable = new RLottieDrawable(NUM, "NUM", AndroidUtilities.dp(28.0f), AndroidUtilities.dp(28.0f), true, (int[]) null);
            this.sunDrawable = rLottieDrawable;
            if (this.lastIsDarkTheme) {
                rLottieDrawable.setCurrentFrame(rLottieDrawable.getFramesCount() - 1);
            } else {
                rLottieDrawable.setCurrentFrame(0);
            }
            this.sunDrawable.setPlayInDirectionOfCustomEndFrame(true);
            this.menuItem = createMenu.addItem(5, (Drawable) this.sunDrawable);
        } else if (i == 0) {
            this.actionBar.setTitle(LocaleController.getString("ChatSettings", NUM));
            ActionBarMenuItem addItem = this.actionBar.createMenu().addItem(0, NUM);
            this.menuItem = addItem;
            addItem.setContentDescription(LocaleController.getString("AccDescrMoreOptions", NUM));
            this.menuItem.addSubItem(2, NUM, LocaleController.getString("ShareTheme", NUM));
            this.menuItem.addSubItem(3, NUM, LocaleController.getString("EditThemeColors", NUM));
            this.menuItem.addSubItem(1, NUM, LocaleController.getString("CreateNewThemeMenu", NUM));
            this.menuItem.addSubItem(4, NUM, LocaleController.getString("ThemeResetToDefaults", NUM));
        } else {
            this.actionBar.setTitle(LocaleController.getString("AutoNightTheme", NUM));
        }
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v2, resolved type: java.lang.Object[]} */
            /* JADX WARNING: Multi-variable type inference failed */
            /* JADX WARNING: Removed duplicated region for block: B:48:0x015a  */
            /* JADX WARNING: Removed duplicated region for block: B:49:0x015f  */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void onItemClick(int r13) {
                /*
                    r12 = this;
                    r0 = -1
                    if (r13 != r0) goto L_0x000a
                    org.telegram.ui.ThemeActivity r13 = org.telegram.ui.ThemeActivity.this
                    r13.finishFragment()
                    goto L_0x01cb
                L_0x000a:
                    r1 = 1
                    if (r13 != r1) goto L_0x0014
                    org.telegram.ui.ThemeActivity r13 = org.telegram.ui.ThemeActivity.this
                    r13.createNewTheme()
                    goto L_0x01cb
                L_0x0014:
                    r2 = 0
                    r3 = 2
                    if (r13 != r3) goto L_0x007e
                    org.telegram.ui.ActionBar.Theme$ThemeInfo r13 = org.telegram.ui.ActionBar.Theme.getCurrentTheme()
                    org.telegram.ui.ActionBar.Theme$ThemeAccent r13 = r13.getAccent(r2)
                    org.telegram.tgnet.TLRPC$TL_theme r0 = r13.info
                    if (r0 != 0) goto L_0x0042
                    org.telegram.ui.ThemeActivity r0 = org.telegram.ui.ThemeActivity.this
                    org.telegram.messenger.MessagesController r0 = r0.getMessagesController()
                    org.telegram.ui.ActionBar.Theme$ThemeInfo r4 = r13.parentTheme
                    r0.saveThemeToServer(r4, r13)
                    org.telegram.messenger.NotificationCenter r0 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
                    int r4 = org.telegram.messenger.NotificationCenter.needShareTheme
                    java.lang.Object[] r3 = new java.lang.Object[r3]
                    org.telegram.ui.ActionBar.Theme$ThemeInfo r5 = r13.parentTheme
                    r3[r2] = r5
                    r3[r1] = r13
                    r0.postNotificationName(r4, r3)
                    goto L_0x01cb
                L_0x0042:
                    java.lang.StringBuilder r0 = new java.lang.StringBuilder
                    r0.<init>()
                    java.lang.String r1 = "https://"
                    r0.append(r1)
                    org.telegram.ui.ThemeActivity r1 = org.telegram.ui.ThemeActivity.this
                    org.telegram.messenger.MessagesController r1 = r1.getMessagesController()
                    java.lang.String r1 = r1.linkPrefix
                    r0.append(r1)
                    java.lang.String r1 = "/addtheme/"
                    r0.append(r1)
                    org.telegram.tgnet.TLRPC$TL_theme r13 = r13.info
                    java.lang.String r13 = r13.slug
                    r0.append(r13)
                    java.lang.String r6 = r0.toString()
                    org.telegram.ui.ThemeActivity r13 = org.telegram.ui.ThemeActivity.this
                    org.telegram.ui.Components.ShareAlert r0 = new org.telegram.ui.Components.ShareAlert
                    org.telegram.ui.ThemeActivity r1 = org.telegram.ui.ThemeActivity.this
                    android.app.Activity r2 = r1.getParentActivity()
                    r3 = 0
                    r5 = 0
                    r7 = 0
                    r1 = r0
                    r4 = r6
                    r1.<init>(r2, r3, r4, r5, r6, r7)
                    r13.showDialog(r0)
                    goto L_0x01cb
                L_0x007e:
                    r4 = 3
                    if (r13 != r4) goto L_0x0088
                    org.telegram.ui.ThemeActivity r13 = org.telegram.ui.ThemeActivity.this
                    r13.editTheme()
                    goto L_0x01cb
                L_0x0088:
                    r5 = 4
                    if (r13 != r5) goto L_0x00f1
                    org.telegram.ui.ThemeActivity r13 = org.telegram.ui.ThemeActivity.this
                    android.app.Activity r13 = r13.getParentActivity()
                    if (r13 != 0) goto L_0x0094
                    return
                L_0x0094:
                    org.telegram.ui.ActionBar.AlertDialog$Builder r13 = new org.telegram.ui.ActionBar.AlertDialog$Builder
                    org.telegram.ui.ThemeActivity r1 = org.telegram.ui.ThemeActivity.this
                    android.app.Activity r1 = r1.getParentActivity()
                    r13.<init>((android.content.Context) r1)
                    r1 = 2131628319(0x7f0e111f, float:1.8883927E38)
                    java.lang.String r2 = "ThemeResetToDefaultsTitle"
                    java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
                    r13.setTitle(r1)
                    r1 = 2131628318(0x7f0e111e, float:1.8883925E38)
                    java.lang.String r2 = "ThemeResetToDefaultsText"
                    java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
                    r13.setMessage(r1)
                    r1 = 2131627691(0x7f0e0eab, float:1.8882654E38)
                    java.lang.String r2 = "Reset"
                    java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
                    org.telegram.ui.ThemeActivity$1$$ExternalSyntheticLambda0 r2 = new org.telegram.ui.ThemeActivity$1$$ExternalSyntheticLambda0
                    r2.<init>(r12)
                    r13.setPositiveButton(r1, r2)
                    r1 = 2131624705(0x7f0e0301, float:1.8876597E38)
                    java.lang.String r2 = "Cancel"
                    java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
                    r2 = 0
                    r13.setNegativeButton(r1, r2)
                    org.telegram.ui.ActionBar.AlertDialog r13 = r13.create()
                    org.telegram.ui.ThemeActivity r1 = org.telegram.ui.ThemeActivity.this
                    r1.showDialog(r13)
                    android.view.View r13 = r13.getButton(r0)
                    android.widget.TextView r13 = (android.widget.TextView) r13
                    if (r13 == 0) goto L_0x01cb
                    java.lang.String r0 = "dialogTextRed2"
                    int r0 = org.telegram.ui.ActionBar.Theme.getColor(r0)
                    r13.setTextColor(r0)
                    goto L_0x01cb
                L_0x00f1:
                    r6 = 5
                    if (r13 != r6) goto L_0x01cb
                    android.content.Context r13 = org.telegram.messenger.ApplicationLoader.applicationContext
                    java.lang.String r7 = "themeconfig"
                    android.content.SharedPreferences r13 = r13.getSharedPreferences(r7, r2)
                    java.lang.String r7 = "lastDayTheme"
                    java.lang.String r8 = "Blue"
                    java.lang.String r7 = r13.getString(r7, r8)
                    org.telegram.ui.ActionBar.Theme$ThemeInfo r9 = org.telegram.ui.ActionBar.Theme.getTheme(r7)
                    if (r9 == 0) goto L_0x0114
                    org.telegram.ui.ActionBar.Theme$ThemeInfo r9 = org.telegram.ui.ActionBar.Theme.getTheme(r7)
                    boolean r9 = r9.isDark()
                    if (r9 == 0) goto L_0x0115
                L_0x0114:
                    r7 = r8
                L_0x0115:
                    java.lang.String r9 = "lastDarkTheme"
                    java.lang.String r10 = "Dark Blue"
                    java.lang.String r13 = r13.getString(r9, r10)
                    org.telegram.ui.ActionBar.Theme$ThemeInfo r9 = org.telegram.ui.ActionBar.Theme.getTheme(r13)
                    if (r9 == 0) goto L_0x012d
                    org.telegram.ui.ActionBar.Theme$ThemeInfo r9 = org.telegram.ui.ActionBar.Theme.getTheme(r13)
                    boolean r9 = r9.isDark()
                    if (r9 != 0) goto L_0x012e
                L_0x012d:
                    r13 = r10
                L_0x012e:
                    org.telegram.ui.ActionBar.Theme$ThemeInfo r9 = org.telegram.ui.ActionBar.Theme.getActiveTheme()
                    boolean r11 = r7.equals(r13)
                    if (r11 == 0) goto L_0x014e
                    boolean r11 = r9.isDark()
                    if (r11 != 0) goto L_0x014c
                    boolean r11 = r7.equals(r10)
                    if (r11 != 0) goto L_0x014c
                    java.lang.String r11 = "Night"
                    boolean r11 = r7.equals(r11)
                    if (r11 == 0) goto L_0x014f
                L_0x014c:
                    r10 = r13
                    goto L_0x0150
                L_0x014e:
                    r10 = r13
                L_0x014f:
                    r8 = r7
                L_0x0150:
                    java.lang.String r13 = r9.getKey()
                    boolean r13 = r8.equals(r13)
                    if (r13 == 0) goto L_0x015f
                    org.telegram.ui.ActionBar.Theme$ThemeInfo r7 = org.telegram.ui.ActionBar.Theme.getTheme(r10)
                    goto L_0x0163
                L_0x015f:
                    org.telegram.ui.ActionBar.Theme$ThemeInfo r7 = org.telegram.ui.ActionBar.Theme.getTheme(r8)
                L_0x0163:
                    int[] r8 = new int[r3]
                    org.telegram.ui.ThemeActivity r9 = org.telegram.ui.ThemeActivity.this
                    org.telegram.ui.ActionBar.ActionBarMenuItem r9 = r9.menuItem
                    org.telegram.ui.Components.RLottieImageView r9 = r9.getIconView()
                    r9.getLocationInWindow(r8)
                    r9 = r8[r2]
                    org.telegram.ui.ThemeActivity r10 = org.telegram.ui.ThemeActivity.this
                    org.telegram.ui.ActionBar.ActionBarMenuItem r10 = r10.menuItem
                    org.telegram.ui.Components.RLottieImageView r10 = r10.getIconView()
                    int r10 = r10.getMeasuredWidth()
                    int r10 = r10 / r3
                    int r9 = r9 + r10
                    r8[r2] = r9
                    r9 = r8[r1]
                    org.telegram.ui.ThemeActivity r10 = org.telegram.ui.ThemeActivity.this
                    org.telegram.ui.ActionBar.ActionBarMenuItem r10 = r10.menuItem
                    org.telegram.ui.Components.RLottieImageView r10 = r10.getIconView()
                    int r10 = r10.getMeasuredHeight()
                    int r10 = r10 / r3
                    int r9 = r9 + r10
                    r8[r1] = r9
                    org.telegram.messenger.NotificationCenter r9 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
                    int r10 = org.telegram.messenger.NotificationCenter.needSetDayNightTheme
                    r11 = 6
                    java.lang.Object[] r11 = new java.lang.Object[r11]
                    r11[r2] = r7
                    java.lang.Boolean r2 = java.lang.Boolean.FALSE
                    r11[r1] = r2
                    r11[r3] = r8
                    java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
                    r11[r4] = r0
                    java.lang.Boolean r13 = java.lang.Boolean.valueOf(r13)
                    r11[r5] = r13
                    org.telegram.ui.ThemeActivity r13 = org.telegram.ui.ThemeActivity.this
                    org.telegram.ui.ActionBar.ActionBarMenuItem r13 = r13.menuItem
                    org.telegram.ui.Components.RLottieImageView r13 = r13.getIconView()
                    r11[r6] = r13
                    r9.postNotificationName(r10, r11)
                    org.telegram.ui.ThemeActivity r13 = org.telegram.ui.ThemeActivity.this
                    r13.updateRows(r1)
                L_0x01cb:
                    return
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ThemeActivity.AnonymousClass1.onItemClick(int):void");
            }

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$onItemClick$0(DialogInterface dialogInterface, int i) {
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
                    Theme.ThemeAccent themeAccent = theme.themeAccentsMap.get(Theme.DEFALT_THEME_ACCENT_ID);
                    if (themeAccent != null) {
                        Theme.OverrideWallpaperInfo overrideWallpaperInfo = new Theme.OverrideWallpaperInfo();
                        overrideWallpaperInfo.slug = "d";
                        overrideWallpaperInfo.fileName = "Blue_99_wp.jpg";
                        overrideWallpaperInfo.originalFileName = "Blue_99_wp.jpg";
                        themeAccent.overrideWallpaper = overrideWallpaperInfo;
                        theme.setOverrideWallpaper(overrideWallpaperInfo);
                    }
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
        recyclerListView.setLayoutManager(new LinearLayoutManager(context, 1, false));
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setAdapter(this.listAdapter);
        ((DefaultItemAnimator) this.listView.getItemAnimator()).setDelayAnimations(false);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListenerExtended) new ThemeActivity$$ExternalSyntheticLambda9(this));
        return this.fragmentView;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$5(View view, int i, float f, float f2) {
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
                builder.setItems(new CharSequence[]{LocaleController.getString("DistanceUnitsAutomatic", NUM), LocaleController.getString("DistanceUnitsKilometers", NUM), LocaleController.getString("DistanceUnitsMiles", NUM)}, new ThemeActivity$$ExternalSyntheticLambda1(this));
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
                    builder2.setItems(new CharSequence[]{LocaleController.getString("Default", NUM), LocaleController.getString("SortFirstName", NUM), LocaleController.getString("SortLastName", NUM)}, new ThemeActivity$$ExternalSyntheticLambda4(this, i));
                    builder2.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                    showDialog(builder2.create());
                }
            } else if (i == this.stickersRow) {
                presentFragment(new StickersActivity(0));
            } else if (i == this.reactionsDoubleTapRow) {
                presentFragment(new ReactionsDoubleTapManageActivity());
            } else if (i == this.emojiRow) {
                SharedConfig.toggleBigEmoji();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(SharedConfig.allowBigEmoji);
                }
            } else if (i == this.chatBlurRow) {
                SharedConfig.toggleChatBlur();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(SharedConfig.chatBlurEnabled());
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
                boolean z4 = !Theme.autoNightScheduleByLocation;
                Theme.autoNightScheduleByLocation = z4;
                ((TextCheckCell) view).setChecked(z4);
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
                    showDialog(new TimePickerDialog(getParentActivity(), new ThemeActivity$$ExternalSyntheticLambda0(this, i, (TextSettingsCell) view), i2, i3 - (i2 * 60), true));
                }
            } else if (i == this.scheduleUpdateLocationRow) {
                updateSunTime((Location) null, true);
            } else if (i == this.createNewThemeRow) {
                createNewTheme();
            } else if (i == this.editThemeRow) {
                editTheme();
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$2(DialogInterface dialogInterface, int i) {
        SharedConfig.setDistanceSystemType(i);
        RecyclerView.ViewHolder findViewHolderForAdapterPosition = this.listView.findViewHolderForAdapterPosition(this.distanceRow);
        if (findViewHolderForAdapterPosition != null) {
            this.listAdapter.onBindViewHolder(findViewHolderForAdapterPosition, this.distanceRow);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$3(int i, DialogInterface dialogInterface, int i2) {
        SharedPreferences.Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putInt("sortContactsBy", i2);
        edit.commit();
        ListAdapter listAdapter2 = this.listAdapter;
        if (listAdapter2 != null) {
            listAdapter2.notifyItemChanged(i);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$4(int i, TextSettingsCell textSettingsCell, TimePicker timePicker, int i2, int i3) {
        int i4 = (i2 * 60) + i3;
        if (i == this.scheduleFromRow) {
            Theme.autoNightDayStartTime = i4;
            textSettingsCell.setTextAndValue(LocaleController.getString("AutoNightFrom", NUM), String.format("%02d:%02d", new Object[]{Integer.valueOf(i2), Integer.valueOf(i3)}), true);
            return;
        }
        Theme.autoNightDayEndTime = i4;
        textSettingsCell.setTextAndValue(LocaleController.getString("AutoNightTo", NUM), String.format("%02d:%02d", new Object[]{Integer.valueOf(i2), Integer.valueOf(i3)}), true);
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
            builder.setPositiveButton(LocaleController.getString("CreateTheme", NUM), new ThemeActivity$$ExternalSyntheticLambda3(this));
            showDialog(builder.create());
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createNewTheme$6(DialogInterface dialogInterface, int i) {
        AlertsCreator.createThemeCreateDialog(this, 0, (Theme.ThemeInfo) null, (Theme.ThemeAccent) null);
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
        Theme.OverrideWallpaperInfo overrideWallpaperInfo;
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
            if (SharedConfig.fontSize == i && SharedConfig.bubbleRadius == 10 && currentTheme2.firstAccentIsDefault && currentTheme2.currentAccentId == Theme.DEFALT_THEME_ACCENT_ID && (accent == null || (overrideWallpaperInfo = accent.overrideWallpaper) == null || "d".equals(overrideWallpaperInfo.slug))) {
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
                            builder.setTopAnimation(NUM, 72, false, Theme.getColor("dialogTopBackground"));
                            builder.setMessage(LocaleController.getString("GpsDisabledAlertText", NUM));
                            builder.setPositiveButton(LocaleController.getString("ConnectingToProxyEnable", NUM), new ThemeActivity$$ExternalSyntheticLambda2(this));
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
            Utilities.globalQueue.postRunnable(new ThemeActivity$$ExternalSyntheticLambda6(this));
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

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$updateSunTime$7(DialogInterface dialogInterface, int i) {
        if (getParentActivity() != null) {
            try {
                getParentActivity().startActivity(new Intent("android.settings.LOCATION_SOURCE_SETTINGS"));
            } catch (Exception unused) {
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$updateSunTime$9() {
        String str = null;
        try {
            List<Address> fromLocation = new Geocoder(ApplicationLoader.applicationContext, Locale.getDefault()).getFromLocation(Theme.autoNightLocationLatitude, Theme.autoNightLocationLongitude, 1);
            if (fromLocation.size() > 0) {
                str = fromLocation.get(0).getLocality();
            }
        } catch (Exception unused) {
        }
        AndroidUtilities.runOnUIThread(new ThemeActivity$$ExternalSyntheticLambda7(this, str));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$updateSunTime$8(String str) {
        RecyclerListView.Holder holder;
        Theme.autoNightCityName = str;
        if (str == null) {
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
        private boolean checked;
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
            this.checked = this.currentTheme.currentAccentId == this.currentAccent.id;
            ObjectAnimator objectAnimator = this.checkAnimator;
            if (objectAnimator != null) {
                objectAnimator.cancel();
            }
            float f = 1.0f;
            if (z) {
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

        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
            super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
            accessibilityNodeInfo.setText(LocaleController.getString("ColorPickerMainColor", NUM));
            accessibilityNodeInfo.setClassName(Button.class.getName());
            accessibilityNodeInfo.setChecked(this.checked);
            accessibilityNodeInfo.setCheckable(true);
            accessibilityNodeInfo.setEnabled(true);
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

        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
            super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
            accessibilityNodeInfo.setText(LocaleController.getString("ColorPickerMainColor", NUM));
            accessibilityNodeInfo.setClassName(Button.class.getName());
            accessibilityNodeInfo.setEnabled(true);
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
            return itemViewType == 0 || itemViewType == 1 || itemViewType == 4 || itemViewType == 7 || itemViewType == 10 || itemViewType == 11 || itemViewType == 12 || itemViewType == 14 || itemViewType == 18;
        }

        /* access modifiers changed from: private */
        public void showOptionsForTheme(Theme.ThemeInfo themeInfo) {
            int[] iArr;
            CharSequence[] charSequenceArr;
            if (ThemeActivity.this.getParentActivity() == null) {
                return;
            }
            if ((themeInfo.info == null || themeInfo.themeLoaded) && ThemeActivity.this.currentType != 1) {
                AlertDialog.Builder builder = new AlertDialog.Builder((Context) ThemeActivity.this.getParentActivity());
                String str = null;
                boolean z = false;
                if (themeInfo.pathToFile == null) {
                    charSequenceArr = new CharSequence[]{null, LocaleController.getString("ExportTheme", NUM)};
                    iArr = new int[]{0, NUM};
                } else {
                    TLRPC$TL_theme tLRPC$TL_theme = themeInfo.info;
                    boolean z2 = tLRPC$TL_theme == null || !tLRPC$TL_theme.isDefault;
                    CharSequence[] charSequenceArr2 = new CharSequence[5];
                    charSequenceArr2[0] = LocaleController.getString("ShareFile", NUM);
                    charSequenceArr2[1] = LocaleController.getString("ExportTheme", NUM);
                    TLRPC$TL_theme tLRPC$TL_theme2 = themeInfo.info;
                    charSequenceArr2[2] = (tLRPC$TL_theme2 == null || (!tLRPC$TL_theme2.isDefault && tLRPC$TL_theme2.creator)) ? LocaleController.getString("Edit", NUM) : null;
                    TLRPC$TL_theme tLRPC$TL_theme3 = themeInfo.info;
                    charSequenceArr2[3] = (tLRPC$TL_theme3 == null || !tLRPC$TL_theme3.creator) ? null : LocaleController.getString("ThemeSetUrl", NUM);
                    if (z2) {
                        str = LocaleController.getString("Delete", NUM);
                    }
                    charSequenceArr2[4] = str;
                    iArr = new int[]{NUM, NUM, NUM, NUM, NUM};
                    z = z2;
                    charSequenceArr = charSequenceArr2;
                }
                builder.setItems(charSequenceArr, iArr, new ThemeActivity$ListAdapter$$ExternalSyntheticLambda2(this, themeInfo));
                AlertDialog create = builder.create();
                ThemeActivity.this.showDialog(create);
                if (z) {
                    create.setItemColor(create.getItemsCount() - 1, Theme.getColor("dialogTextRed2"), Theme.getColor("dialogRedIcon"));
                }
            }
        }

        /* access modifiers changed from: private */
        /* JADX WARNING: Can't wrap try/catch for region: R(4:59|60|61|62) */
        /* JADX WARNING: Missing exception handler attribute for start block: B:61:0x0151 */
        /* JADX WARNING: Removed duplicated region for block: B:34:0x00d8 A[SYNTHETIC, Splitter:B:34:0x00d8] */
        /* JADX WARNING: Removed duplicated region for block: B:39:0x00e3 A[SYNTHETIC, Splitter:B:39:0x00e3] */
        /* JADX WARNING: Removed duplicated region for block: B:50:0x0106  */
        /* JADX WARNING: Removed duplicated region for block: B:55:0x0129 A[Catch:{ Exception -> 0x0176 }, RETURN] */
        /* JADX WARNING: Removed duplicated region for block: B:56:0x012a A[Catch:{ Exception -> 0x0176 }] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public /* synthetic */ void lambda$showOptionsForTheme$1(org.telegram.ui.ActionBar.Theme.ThemeInfo r8, android.content.DialogInterface r9, int r10) {
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
                if (r10 != 0) goto L_0x0069
                org.telegram.tgnet.TLRPC$TL_theme r10 = r8.info
                if (r10 != 0) goto L_0x002d
                org.telegram.ui.ThemeActivity r10 = org.telegram.ui.ThemeActivity.this
                org.telegram.messenger.MessagesController r10 = r10.getMessagesController()
                r10.saveThemeToServer(r8, r1)
                org.telegram.messenger.NotificationCenter r10 = org.telegram.messenger.NotificationCenter.getGlobalInstance()
                int r3 = org.telegram.messenger.NotificationCenter.needShareTheme
                java.lang.Object[] r0 = new java.lang.Object[r0]
                r0[r9] = r8
                r0[r2] = r1
                r10.postNotificationName(r3, r0)
                goto L_0x0213
            L_0x002d:
                java.lang.StringBuilder r9 = new java.lang.StringBuilder
                r9.<init>()
                java.lang.String r10 = "https://"
                r9.append(r10)
                org.telegram.ui.ThemeActivity r10 = org.telegram.ui.ThemeActivity.this
                org.telegram.messenger.MessagesController r10 = r10.getMessagesController()
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
                org.telegram.ui.ThemeActivity r10 = org.telegram.ui.ThemeActivity.this
                android.app.Activity r1 = r10.getParentActivity()
                r2 = 0
                r4 = 0
                r6 = 0
                r0 = r9
                r3 = r5
                r0.<init>(r1, r2, r3, r4, r5, r6)
                r8.showDialog(r9)
                goto L_0x0213
            L_0x0069:
                if (r10 != r2) goto L_0x017c
                java.lang.String r9 = r8.pathToFile
                if (r9 != 0) goto L_0x00ec
                java.lang.String r9 = r8.assetName
                if (r9 != 0) goto L_0x00ec
                java.lang.StringBuilder r9 = new java.lang.StringBuilder
                r9.<init>()
                java.util.HashMap r10 = org.telegram.ui.ActionBar.Theme.getDefaultColors()
                java.util.Set r10 = r10.entrySet()
                java.util.Iterator r10 = r10.iterator()
            L_0x0084:
                boolean r0 = r10.hasNext()
                if (r0 == 0) goto L_0x00ab
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
                goto L_0x0084
            L_0x00ab:
                java.io.File r10 = new java.io.File
                java.io.File r0 = org.telegram.messenger.ApplicationLoader.getFilesDirFixed()
                java.lang.String r3 = "default_theme.attheme"
                r10.<init>(r0, r3)
                java.io.FileOutputStream r0 = new java.io.FileOutputStream     // Catch:{ Exception -> 0x00d2 }
                r0.<init>(r10)     // Catch:{ Exception -> 0x00d2 }
                java.lang.String r9 = r9.toString()     // Catch:{ Exception -> 0x00cd, all -> 0x00ca }
                byte[] r9 = org.telegram.messenger.AndroidUtilities.getStringBytes(r9)     // Catch:{ Exception -> 0x00cd, all -> 0x00ca }
                r0.write(r9)     // Catch:{ Exception -> 0x00cd, all -> 0x00ca }
                r0.close()     // Catch:{ Exception -> 0x00dc }
                goto L_0x00fc
            L_0x00ca:
                r8 = move-exception
                r1 = r0
                goto L_0x00e1
            L_0x00cd:
                r9 = move-exception
                r1 = r0
                goto L_0x00d3
            L_0x00d0:
                r8 = move-exception
                goto L_0x00e1
            L_0x00d2:
                r9 = move-exception
            L_0x00d3:
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r9)     // Catch:{ all -> 0x00d0 }
                if (r1 == 0) goto L_0x00fc
                r1.close()     // Catch:{ Exception -> 0x00dc }
                goto L_0x00fc
            L_0x00dc:
                r9 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r9)
                goto L_0x00fc
            L_0x00e1:
                if (r1 == 0) goto L_0x00eb
                r1.close()     // Catch:{ Exception -> 0x00e7 }
                goto L_0x00eb
            L_0x00e7:
                r9 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r9)
            L_0x00eb:
                throw r8
            L_0x00ec:
                java.lang.String r9 = r8.assetName
                if (r9 == 0) goto L_0x00f5
                java.io.File r10 = org.telegram.ui.ActionBar.Theme.getAssetFile(r9)
                goto L_0x00fc
            L_0x00f5:
                java.io.File r10 = new java.io.File
                java.lang.String r9 = r8.pathToFile
                r10.<init>(r9)
            L_0x00fc:
                java.lang.String r8 = r8.name
                java.lang.String r9 = ".attheme"
                boolean r0 = r8.endsWith(r9)
                if (r0 != 0) goto L_0x0115
                java.lang.StringBuilder r0 = new java.lang.StringBuilder
                r0.<init>()
                r0.append(r8)
                r0.append(r9)
                java.lang.String r8 = r0.toString()
            L_0x0115:
                java.io.File r9 = new java.io.File
                r0 = 4
                java.io.File r0 = org.telegram.messenger.FileLoader.getDirectory(r0)
                java.lang.String r8 = org.telegram.messenger.FileLoader.fixFileName(r8)
                r9.<init>(r0, r8)
                boolean r8 = org.telegram.messenger.AndroidUtilities.copyFile((java.io.File) r10, (java.io.File) r9)     // Catch:{ Exception -> 0x0176 }
                if (r8 != 0) goto L_0x012a
                return
            L_0x012a:
                android.content.Intent r8 = new android.content.Intent     // Catch:{ Exception -> 0x0176 }
                java.lang.String r10 = "android.intent.action.SEND"
                r8.<init>(r10)     // Catch:{ Exception -> 0x0176 }
                java.lang.String r10 = "text/xml"
                r8.setType(r10)     // Catch:{ Exception -> 0x0176 }
                int r10 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0176 }
                r0 = 24
                java.lang.String r1 = "android.intent.extra.STREAM"
                if (r10 < r0) goto L_0x0159
                org.telegram.ui.ThemeActivity r10 = org.telegram.ui.ThemeActivity.this     // Catch:{ Exception -> 0x0151 }
                android.app.Activity r10 = r10.getParentActivity()     // Catch:{ Exception -> 0x0151 }
                java.lang.String r0 = "org.telegram.messenger.beta.provider"
                android.net.Uri r10 = androidx.core.content.FileProvider.getUriForFile(r10, r0, r9)     // Catch:{ Exception -> 0x0151 }
                r8.putExtra(r1, r10)     // Catch:{ Exception -> 0x0151 }
                r8.setFlags(r2)     // Catch:{ Exception -> 0x0151 }
                goto L_0x0160
            L_0x0151:
                android.net.Uri r9 = android.net.Uri.fromFile(r9)     // Catch:{ Exception -> 0x0176 }
                r8.putExtra(r1, r9)     // Catch:{ Exception -> 0x0176 }
                goto L_0x0160
            L_0x0159:
                android.net.Uri r9 = android.net.Uri.fromFile(r9)     // Catch:{ Exception -> 0x0176 }
                r8.putExtra(r1, r9)     // Catch:{ Exception -> 0x0176 }
            L_0x0160:
                org.telegram.ui.ThemeActivity r9 = org.telegram.ui.ThemeActivity.this     // Catch:{ Exception -> 0x0176 }
                java.lang.String r10 = "ShareFile"
                r0 = 2131627966(0x7f0e0fbe, float:1.8883211E38)
                java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r10, r0)     // Catch:{ Exception -> 0x0176 }
                android.content.Intent r8 = android.content.Intent.createChooser(r8, r10)     // Catch:{ Exception -> 0x0176 }
                r10 = 500(0x1f4, float:7.0E-43)
                r9.startActivityForResult(r8, r10)     // Catch:{ Exception -> 0x0176 }
                goto L_0x0213
            L_0x0176:
                r8 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r8)
                goto L_0x0213
            L_0x017c:
                if (r10 != r0) goto L_0x01a1
                org.telegram.ui.ThemeActivity r9 = org.telegram.ui.ThemeActivity.this
                org.telegram.ui.ActionBar.ActionBarLayout r9 = r9.parentLayout
                if (r9 == 0) goto L_0x0213
                org.telegram.ui.ActionBar.Theme.applyTheme(r8)
                org.telegram.ui.ThemeActivity r9 = org.telegram.ui.ThemeActivity.this
                org.telegram.ui.ActionBar.ActionBarLayout r9 = r9.parentLayout
                r9.rebuildAllFragmentViews(r2, r2)
                org.telegram.ui.Components.ThemeEditorView r9 = new org.telegram.ui.Components.ThemeEditorView
                r9.<init>()
                org.telegram.ui.ThemeActivity r10 = org.telegram.ui.ThemeActivity.this
                android.app.Activity r10 = r10.getParentActivity()
                r9.show(r10, r8)
                goto L_0x0213
            L_0x01a1:
                r0 = 3
                if (r10 != r0) goto L_0x01af
                org.telegram.ui.ThemeActivity r10 = org.telegram.ui.ThemeActivity.this
                org.telegram.ui.ThemeSetUrlActivity r0 = new org.telegram.ui.ThemeSetUrlActivity
                r0.<init>(r8, r1, r9)
                r10.presentFragment(r0)
                goto L_0x0213
            L_0x01af:
                org.telegram.ui.ThemeActivity r9 = org.telegram.ui.ThemeActivity.this
                android.app.Activity r9 = r9.getParentActivity()
                if (r9 != 0) goto L_0x01b8
                return
            L_0x01b8:
                org.telegram.ui.ActionBar.AlertDialog$Builder r9 = new org.telegram.ui.ActionBar.AlertDialog$Builder
                org.telegram.ui.ThemeActivity r10 = org.telegram.ui.ThemeActivity.this
                android.app.Activity r10 = r10.getParentActivity()
                r9.<init>((android.content.Context) r10)
                r10 = 2131625283(0x7f0e0543, float:1.887777E38)
                java.lang.String r0 = "DeleteThemeTitle"
                java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r0, r10)
                r9.setTitle(r10)
                r10 = 2131625282(0x7f0e0542, float:1.8877768E38)
                java.lang.String r0 = "DeleteThemeAlert"
                java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r0, r10)
                r9.setMessage(r10)
                r10 = 2131625217(0x7f0e0501, float:1.8877636E38)
                java.lang.String r0 = "Delete"
                java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r0, r10)
                org.telegram.ui.ThemeActivity$ListAdapter$$ExternalSyntheticLambda1 r0 = new org.telegram.ui.ThemeActivity$ListAdapter$$ExternalSyntheticLambda1
                r0.<init>(r7, r8)
                r9.setPositiveButton(r10, r0)
                r8 = 2131624705(0x7f0e0301, float:1.8876597E38)
                java.lang.String r10 = "Cancel"
                java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r10, r8)
                r9.setNegativeButton(r8, r1)
                org.telegram.ui.ActionBar.AlertDialog r8 = r9.create()
                org.telegram.ui.ThemeActivity r9 = org.telegram.ui.ThemeActivity.this
                r9.showDialog(r8)
                r9 = -1
                android.view.View r8 = r8.getButton(r9)
                android.widget.TextView r8 = (android.widget.TextView) r8
                if (r8 == 0) goto L_0x0213
                java.lang.String r9 = "dialogTextRed2"
                int r9 = org.telegram.ui.ActionBar.Theme.getColor(r9)
                r8.setTextColor(r9)
            L_0x0213:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ThemeActivity.ListAdapter.lambda$showOptionsForTheme$1(org.telegram.ui.ActionBar.Theme$ThemeInfo, android.content.DialogInterface, int):void");
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$showOptionsForTheme$0(Theme.ThemeInfo themeInfo, DialogInterface dialogInterface, int i) {
            MessagesController.getInstance(themeInfo.account).saveTheme(themeInfo, (Theme.ThemeAccent) null, themeInfo == Theme.getCurrentNightTheme(), true);
            if (Theme.deleteTheme(themeInfo)) {
                ThemeActivity.this.parentLayout.rebuildAllFragmentViews(true, true);
            }
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.themeListUpdated, new Object[0]);
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onCreateViewHolder$2(ThemeAccentsListAdapter themeAccentsListAdapter, RecyclerListView recyclerListView, View view, int i) {
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
                    EmojiThemes.saveCustomTheme(currentNightTheme, themeAccent.id);
                } else {
                    ThemeActivity themeActivity2 = ThemeActivity.this;
                    themeActivity2.presentFragment(new ThemePreviewActivity(currentNightTheme, false, 1, i3 >= 100, themeActivity2.currentType == 1));
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

        /* access modifiers changed from: private */
        public /* synthetic */ boolean lambda$onCreateViewHolder$5(ThemeAccentsListAdapter themeAccentsListAdapter, View view, int i) {
            if (i >= 0 && i < themeAccentsListAdapter.themeAccents.size()) {
                Theme.ThemeAccent themeAccent = (Theme.ThemeAccent) themeAccentsListAdapter.themeAccents.get(i);
                if (themeAccent.id >= 100 && !themeAccent.isDefault) {
                    AlertDialog.Builder builder = new AlertDialog.Builder((Context) ThemeActivity.this.getParentActivity());
                    CharSequence[] charSequenceArr = new CharSequence[4];
                    charSequenceArr[0] = LocaleController.getString("OpenInEditor", NUM);
                    charSequenceArr[1] = LocaleController.getString("ShareTheme", NUM);
                    TLRPC$TL_theme tLRPC$TL_theme = themeAccent.info;
                    charSequenceArr[2] = (tLRPC$TL_theme == null || !tLRPC$TL_theme.creator) ? null : LocaleController.getString("ThemeSetUrl", NUM);
                    charSequenceArr[3] = LocaleController.getString("DeleteTheme", NUM);
                    builder.setItems(charSequenceArr, new int[]{NUM, NUM, NUM, NUM}, new ThemeActivity$ListAdapter$$ExternalSyntheticLambda0(this, themeAccent, themeAccentsListAdapter));
                    AlertDialog create = builder.create();
                    ThemeActivity.this.showDialog(create);
                    create.setItemColor(create.getItemsCount() - 1, Theme.getColor("dialogTextRed2"), Theme.getColor("dialogRedIcon"));
                    return true;
                }
            }
            return false;
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onCreateViewHolder$4(Theme.ThemeAccent themeAccent, ThemeAccentsListAdapter themeAccentsListAdapter, DialogInterface dialogInterface, int i) {
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
                        ThemeActivity.this.getMessagesController().saveThemeToServer(themeAccent.parentTheme, themeAccent);
                        NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.needShareTheme, themeAccent.parentTheme, themeAccent);
                        return;
                    }
                    String str = "https://" + ThemeActivity.this.getMessagesController().linkPrefix + "/addtheme/" + themeAccent.info.slug;
                    ThemeActivity.this.showDialog(new ShareAlert(ThemeActivity.this.getParentActivity(), (ArrayList<MessageObject>) null, str, false, str, false));
                } else if (i == 2) {
                    ThemeActivity.this.presentFragment(new ThemeSetUrlActivity(themeAccent.parentTheme, themeAccent, false));
                } else if (i == 3 && ThemeActivity.this.getParentActivity() != null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder((Context) ThemeActivity.this.getParentActivity());
                    builder.setTitle(LocaleController.getString("DeleteThemeTitle", NUM));
                    builder.setMessage(LocaleController.getString("DeleteThemeAlert", NUM));
                    builder.setPositiveButton(LocaleController.getString("Delete", NUM), new ThemeActivity$ListAdapter$$ExternalSyntheticLambda3(this, themeAccentsListAdapter, themeAccent));
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

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$onCreateViewHolder$3(ThemeAccentsListAdapter themeAccentsListAdapter, Theme.ThemeAccent themeAccent, DialogInterface dialogInterface, int i) {
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

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v13, resolved type: org.telegram.ui.Cells.ThemesHorizontalListCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v19, resolved type: org.telegram.ui.Cells.TextSettingsCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v20, resolved type: org.telegram.ui.Cells.TextInfoPrivacyCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v21, resolved type: org.telegram.ui.Cells.ShadowSectionCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v22, resolved type: org.telegram.ui.Cells.ThemeTypeCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v23, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v24, resolved type: org.telegram.ui.ThemeActivity$ListAdapter$1} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v25, resolved type: org.telegram.ui.Cells.TextCheckCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v26, resolved type: org.telegram.ui.ThemeActivity$TextSizeCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v27, resolved type: org.telegram.ui.ThemeActivity$ListAdapter$2} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v28, resolved type: org.telegram.ui.Cells.NotificationsCheckCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v29, resolved type: org.telegram.ui.ThemeActivity$BubbleRadiusCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v30, resolved type: org.telegram.ui.Components.SwipeGestureSettingsView} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v31, resolved type: org.telegram.ui.Cells.ThemePreviewMessagesCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v32, resolved type: org.telegram.ui.Cells.ThemePreviewMessagesCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v33, resolved type: org.telegram.ui.Cells.TextSettingsCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v34, resolved type: org.telegram.ui.Cells.TextCell} */
        /* JADX WARNING: type inference failed for: r10v31, types: [android.view.View, androidx.recyclerview.widget.RecyclerView, android.view.ViewGroup, org.telegram.ui.Components.RecyclerListView, org.telegram.ui.ThemeActivity$ListAdapter$4] */
        /* JADX WARNING: type inference failed for: r10v38, types: [android.widget.LinearLayout, org.telegram.ui.DefaultThemesPreviewCell, android.view.View] */
        /* JADX WARNING: Multi-variable type inference failed */
        /* JADX WARNING: Unknown variable types count: 2 */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public androidx.recyclerview.widget.RecyclerView.ViewHolder onCreateViewHolder(android.view.ViewGroup r9, int r10) {
            /*
                r8 = this;
                r9 = -1
                r0 = 0
                java.lang.String r1 = "windowBackgroundWhite"
                switch(r10) {
                    case 1: goto L_0x01b4;
                    case 2: goto L_0x019e;
                    case 3: goto L_0x0196;
                    case 4: goto L_0x0187;
                    case 5: goto L_0x0178;
                    case 6: goto L_0x0169;
                    case 7: goto L_0x015a;
                    case 8: goto L_0x0149;
                    case 9: goto L_0x0139;
                    case 10: goto L_0x0125;
                    case 11: goto L_0x00d5;
                    case 12: goto L_0x0075;
                    case 13: goto L_0x0063;
                    case 14: goto L_0x0007;
                    case 15: goto L_0x0054;
                    case 16: goto L_0x003b;
                    case 17: goto L_0x0020;
                    case 18: goto L_0x0017;
                    default: goto L_0x0007;
                }
            L_0x0007:
                org.telegram.ui.Cells.TextCell r9 = new org.telegram.ui.Cells.TextCell
                android.content.Context r10 = r8.mContext
                r9.<init>(r10)
                int r10 = org.telegram.ui.ActionBar.Theme.getColor(r1)
                r9.setBackgroundColor(r10)
                goto L_0x01c2
            L_0x0017:
                org.telegram.ui.Cells.TextSettingsCell r9 = new org.telegram.ui.Cells.TextSettingsCell
                android.content.Context r10 = r8.mContext
                r9.<init>(r10)
                goto L_0x01c2
            L_0x0020:
                org.telegram.ui.DefaultThemesPreviewCell r10 = new org.telegram.ui.DefaultThemesPreviewCell
                android.content.Context r1 = r8.mContext
                org.telegram.ui.ThemeActivity r2 = org.telegram.ui.ThemeActivity.this
                int r3 = r2.currentType
                r10.<init>(r1, r2, r3)
                r10.setFocusable(r0)
                androidx.recyclerview.widget.RecyclerView$LayoutParams r0 = new androidx.recyclerview.widget.RecyclerView$LayoutParams
                r1 = -2
                r0.<init>((int) r9, (int) r1)
                r10.setLayoutParams(r0)
                goto L_0x0122
            L_0x003b:
                org.telegram.ui.Cells.ThemePreviewMessagesCell r9 = new org.telegram.ui.Cells.ThemePreviewMessagesCell
                android.content.Context r10 = r8.mContext
                org.telegram.ui.ThemeActivity r1 = org.telegram.ui.ThemeActivity.this
                org.telegram.ui.ActionBar.ActionBarLayout r1 = r1.parentLayout
                r9.<init>(r10, r1, r0)
                int r10 = android.os.Build.VERSION.SDK_INT
                r0 = 19
                if (r10 < r0) goto L_0x01c2
                r10 = 4
                r9.setImportantForAccessibility(r10)
                goto L_0x01c2
            L_0x0054:
                org.telegram.ui.Components.SwipeGestureSettingsView r9 = new org.telegram.ui.Components.SwipeGestureSettingsView
                android.content.Context r10 = r8.mContext
                org.telegram.ui.ThemeActivity r0 = org.telegram.ui.ThemeActivity.this
                int r0 = r0.currentAccount
                r9.<init>(r10, r0)
                goto L_0x01c2
            L_0x0063:
                org.telegram.ui.ThemeActivity$BubbleRadiusCell r9 = new org.telegram.ui.ThemeActivity$BubbleRadiusCell
                org.telegram.ui.ThemeActivity r10 = org.telegram.ui.ThemeActivity.this
                android.content.Context r0 = r8.mContext
                r9.<init>(r0)
                int r10 = org.telegram.ui.ActionBar.Theme.getColor(r1)
                r9.setBackgroundColor(r10)
                goto L_0x01c2
            L_0x0075:
                org.telegram.ui.ThemeActivity$ListAdapter$4 r10 = new org.telegram.ui.ThemeActivity$ListAdapter$4
                android.content.Context r2 = r8.mContext
                r10.<init>(r8, r2)
                r10.setFocusable(r0)
                int r1 = org.telegram.ui.ActionBar.Theme.getColor(r1)
                r10.setBackgroundColor(r1)
                r1 = 0
                r10.setItemAnimator(r1)
                r10.setLayoutAnimation(r1)
                r1 = 1093664768(0x41300000, float:11.0)
                int r2 = org.telegram.messenger.AndroidUtilities.dp(r1)
                int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
                r10.setPadding(r2, r0, r1, r0)
                r10.setClipToPadding(r0)
                androidx.recyclerview.widget.LinearLayoutManager r1 = new androidx.recyclerview.widget.LinearLayoutManager
                android.content.Context r2 = r8.mContext
                r1.<init>(r2)
                r1.setOrientation(r0)
                r10.setLayoutManager(r1)
                org.telegram.ui.ThemeActivity$ThemeAccentsListAdapter r0 = new org.telegram.ui.ThemeActivity$ThemeAccentsListAdapter
                org.telegram.ui.ThemeActivity r1 = org.telegram.ui.ThemeActivity.this
                android.content.Context r2 = r8.mContext
                r0.<init>(r2)
                r10.setAdapter(r0)
                org.telegram.ui.ThemeActivity$ListAdapter$$ExternalSyntheticLambda4 r1 = new org.telegram.ui.ThemeActivity$ListAdapter$$ExternalSyntheticLambda4
                r1.<init>(r8, r0, r10)
                r10.setOnItemClickListener((org.telegram.ui.Components.RecyclerListView.OnItemClickListener) r1)
                org.telegram.ui.ThemeActivity$ListAdapter$$ExternalSyntheticLambda5 r1 = new org.telegram.ui.ThemeActivity$ListAdapter$$ExternalSyntheticLambda5
                r1.<init>(r8, r0)
                r10.setOnItemLongClickListener((org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener) r1)
                androidx.recyclerview.widget.RecyclerView$LayoutParams r0 = new androidx.recyclerview.widget.RecyclerView$LayoutParams
                r1 = 1115160576(0x42780000, float:62.0)
                int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
                r0.<init>((int) r9, (int) r1)
                r10.setLayoutParams(r0)
                goto L_0x0122
            L_0x00d5:
                r10 = 1
                r8.first = r10
                org.telegram.ui.ThemeActivity r10 = org.telegram.ui.ThemeActivity.this
                org.telegram.ui.ThemeActivity$ListAdapter$3 r7 = new org.telegram.ui.ThemeActivity$ListAdapter$3
                android.content.Context r3 = r8.mContext
                org.telegram.ui.ThemeActivity r1 = org.telegram.ui.ThemeActivity.this
                int r4 = r1.currentType
                org.telegram.ui.ThemeActivity r1 = org.telegram.ui.ThemeActivity.this
                java.util.ArrayList r5 = r1.defaultThemes
                org.telegram.ui.ThemeActivity r1 = org.telegram.ui.ThemeActivity.this
                java.util.ArrayList r6 = r1.darkThemes
                r1 = r7
                r2 = r8
                r1.<init>(r3, r4, r5, r6)
                org.telegram.ui.Cells.ThemesHorizontalListCell unused = r10.themesHorizontalListCell = r7
                org.telegram.ui.ThemeActivity r10 = org.telegram.ui.ThemeActivity.this
                org.telegram.ui.Cells.ThemesHorizontalListCell r10 = r10.themesHorizontalListCell
                org.telegram.ui.ThemeActivity r1 = org.telegram.ui.ThemeActivity.this
                boolean r1 = r1.hasThemeAccents
                r10.setDrawDivider(r1)
                org.telegram.ui.ThemeActivity r10 = org.telegram.ui.ThemeActivity.this
                org.telegram.ui.Cells.ThemesHorizontalListCell r10 = r10.themesHorizontalListCell
                r10.setFocusable(r0)
                org.telegram.ui.ThemeActivity r10 = org.telegram.ui.ThemeActivity.this
                org.telegram.ui.Cells.ThemesHorizontalListCell r10 = r10.themesHorizontalListCell
                androidx.recyclerview.widget.RecyclerView$LayoutParams r0 = new androidx.recyclerview.widget.RecyclerView$LayoutParams
                r1 = 1125384192(0x43140000, float:148.0)
                int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
                r0.<init>((int) r9, (int) r1)
                r10.setLayoutParams(r0)
            L_0x0122:
                r9 = r10
                goto L_0x01c2
            L_0x0125:
                org.telegram.ui.Cells.NotificationsCheckCell r9 = new org.telegram.ui.Cells.NotificationsCheckCell
                android.content.Context r10 = r8.mContext
                r2 = 21
                r3 = 64
                r9.<init>(r10, r2, r3, r0)
                int r10 = org.telegram.ui.ActionBar.Theme.getColor(r1)
                r9.setBackgroundColor(r10)
                goto L_0x01c2
            L_0x0139:
                org.telegram.ui.ThemeActivity$ListAdapter$2 r9 = new org.telegram.ui.ThemeActivity$ListAdapter$2
                android.content.Context r10 = r8.mContext
                r9.<init>(r8, r10)
                int r10 = org.telegram.ui.ActionBar.Theme.getColor(r1)
                r9.setBackgroundColor(r10)
                goto L_0x01c2
            L_0x0149:
                org.telegram.ui.ThemeActivity$TextSizeCell r9 = new org.telegram.ui.ThemeActivity$TextSizeCell
                org.telegram.ui.ThemeActivity r10 = org.telegram.ui.ThemeActivity.this
                android.content.Context r0 = r8.mContext
                r9.<init>(r0)
                int r10 = org.telegram.ui.ActionBar.Theme.getColor(r1)
                r9.setBackgroundColor(r10)
                goto L_0x01c2
            L_0x015a:
                org.telegram.ui.Cells.TextCheckCell r9 = new org.telegram.ui.Cells.TextCheckCell
                android.content.Context r10 = r8.mContext
                r9.<init>(r10)
                int r10 = org.telegram.ui.ActionBar.Theme.getColor(r1)
                r9.setBackgroundColor(r10)
                goto L_0x01c2
            L_0x0169:
                org.telegram.ui.ThemeActivity$ListAdapter$1 r9 = new org.telegram.ui.ThemeActivity$ListAdapter$1
                android.content.Context r10 = r8.mContext
                r9.<init>(r10)
                int r10 = org.telegram.ui.ActionBar.Theme.getColor(r1)
                r9.setBackgroundColor(r10)
                goto L_0x01c2
            L_0x0178:
                org.telegram.ui.Cells.HeaderCell r9 = new org.telegram.ui.Cells.HeaderCell
                android.content.Context r10 = r8.mContext
                r9.<init>(r10)
                int r10 = org.telegram.ui.ActionBar.Theme.getColor(r1)
                r9.setBackgroundColor(r10)
                goto L_0x01c2
            L_0x0187:
                org.telegram.ui.Cells.ThemeTypeCell r9 = new org.telegram.ui.Cells.ThemeTypeCell
                android.content.Context r10 = r8.mContext
                r9.<init>(r10)
                int r10 = org.telegram.ui.ActionBar.Theme.getColor(r1)
                r9.setBackgroundColor(r10)
                goto L_0x01c2
            L_0x0196:
                org.telegram.ui.Cells.ShadowSectionCell r9 = new org.telegram.ui.Cells.ShadowSectionCell
                android.content.Context r10 = r8.mContext
                r9.<init>(r10)
                goto L_0x01c2
            L_0x019e:
                org.telegram.ui.Cells.TextInfoPrivacyCell r9 = new org.telegram.ui.Cells.TextInfoPrivacyCell
                android.content.Context r10 = r8.mContext
                r9.<init>(r10)
                android.content.Context r10 = r8.mContext
                r0 = 2131165471(0x7var_f, float:1.794516E38)
                java.lang.String r1 = "windowBackgroundGrayShadow"
                android.graphics.drawable.Drawable r10 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r10, (int) r0, (java.lang.String) r1)
                r9.setBackgroundDrawable(r10)
                goto L_0x01c2
            L_0x01b4:
                org.telegram.ui.Cells.TextSettingsCell r9 = new org.telegram.ui.Cells.TextSettingsCell
                android.content.Context r10 = r8.mContext
                r9.<init>(r10)
                int r10 = org.telegram.ui.ActionBar.Theme.getColor(r1)
                r9.setBackgroundColor(r10)
            L_0x01c2:
                org.telegram.ui.Components.RecyclerListView$Holder r10 = new org.telegram.ui.Components.RecyclerListView$Holder
                r10.<init>(r9)
                return r10
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ThemeActivity.ListAdapter.onCreateViewHolder(android.view.ViewGroup, int):androidx.recyclerview.widget.RecyclerView$ViewHolder");
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            String str;
            String str2;
            String str3;
            TLRPC$TL_availableReaction tLRPC$TL_availableReaction;
            RecyclerView.ViewHolder viewHolder2 = viewHolder;
            int i2 = i;
            boolean z = false;
            boolean z2 = true;
            switch (viewHolder.getItemViewType()) {
                case 1:
                    TextSettingsCell textSettingsCell = (TextSettingsCell) viewHolder2.itemView;
                    if (i2 == ThemeActivity.this.nightThemeRow) {
                        if (Theme.selectedAutoNightType == 0 || Theme.getCurrentNightTheme() == null) {
                            textSettingsCell.setTextAndValue(LocaleController.getString("AutoNightTheme", NUM), LocaleController.getString("AutoNightThemeOff", NUM), false);
                            return;
                        } else {
                            textSettingsCell.setTextAndValue(LocaleController.getString("AutoNightTheme", NUM), Theme.getCurrentNightThemeName(), false);
                            return;
                        }
                    } else if (i2 == ThemeActivity.this.scheduleFromRow) {
                        int i3 = Theme.autoNightDayStartTime;
                        int i4 = i3 / 60;
                        textSettingsCell.setTextAndValue(LocaleController.getString("AutoNightFrom", NUM), String.format("%02d:%02d", new Object[]{Integer.valueOf(i4), Integer.valueOf(i3 - (i4 * 60))}), true);
                        return;
                    } else if (i2 == ThemeActivity.this.scheduleToRow) {
                        int i5 = Theme.autoNightDayEndTime;
                        int i6 = i5 / 60;
                        textSettingsCell.setTextAndValue(LocaleController.getString("AutoNightTo", NUM), String.format("%02d:%02d", new Object[]{Integer.valueOf(i6), Integer.valueOf(i5 - (i6 * 60))}), false);
                        return;
                    } else if (i2 == ThemeActivity.this.scheduleUpdateLocationRow) {
                        textSettingsCell.setTextAndValue(LocaleController.getString("AutoNightUpdateLocation", NUM), Theme.autoNightCityName, false);
                        return;
                    } else if (i2 == ThemeActivity.this.contactsSortRow) {
                        int i7 = MessagesController.getGlobalMainSettings().getInt("sortContactsBy", 0);
                        if (i7 == 0) {
                            str2 = LocaleController.getString("Default", NUM);
                        } else if (i7 == 1) {
                            str2 = LocaleController.getString("FirstName", NUM);
                        } else {
                            str2 = LocaleController.getString("LastName", NUM);
                        }
                        textSettingsCell.setTextAndValue(LocaleController.getString("SortBy", NUM), str2, true);
                        return;
                    } else if (i2 == ThemeActivity.this.contactsReimportRow) {
                        textSettingsCell.setText(LocaleController.getString("ImportContacts", NUM), true);
                        return;
                    } else if (i2 == ThemeActivity.this.stickersRow) {
                        textSettingsCell.setText(LocaleController.getString("StickersAndMasks", NUM), false);
                        return;
                    } else if (i2 == ThemeActivity.this.distanceRow) {
                        int i8 = SharedConfig.distanceSystemType;
                        if (i8 == 0) {
                            str = LocaleController.getString("DistanceUnitsAutomatic", NUM);
                        } else if (i8 == 1) {
                            str = LocaleController.getString("DistanceUnitsKilometers", NUM);
                        } else {
                            str = LocaleController.getString("DistanceUnitsMiles", NUM);
                        }
                        textSettingsCell.setTextAndValue(LocaleController.getString("DistanceUnits", NUM), str, true);
                        return;
                    } else {
                        return;
                    }
                case 2:
                    TextInfoPrivacyCell textInfoPrivacyCell = (TextInfoPrivacyCell) viewHolder2.itemView;
                    if (i2 == ThemeActivity.this.automaticBrightnessInfoRow) {
                        textInfoPrivacyCell.setText(LocaleController.formatString("AutoNightBrightnessInfo", NUM, Integer.valueOf((int) (Theme.autoNightBrighnessThreshold * 100.0f))));
                        return;
                    } else if (i2 == ThemeActivity.this.scheduleLocationInfoRow) {
                        textInfoPrivacyCell.setText(ThemeActivity.this.getLocationSunString());
                        return;
                    } else {
                        return;
                    }
                case 3:
                    if (i2 == ThemeActivity.this.stickersSection2Row || ((i2 == ThemeActivity.this.nightTypeInfoRow && ThemeActivity.this.themeInfoRow == -1) || (i2 == ThemeActivity.this.themeInfoRow && ThemeActivity.this.nightTypeInfoRow != -1))) {
                        viewHolder2.itemView.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                        return;
                    } else {
                        viewHolder2.itemView.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                        return;
                    }
                case 4:
                    ThemeTypeCell themeTypeCell = (ThemeTypeCell) viewHolder2.itemView;
                    if (i2 == ThemeActivity.this.nightDisabledRow) {
                        String string = LocaleController.getString("AutoNightDisabled", NUM);
                        if (Theme.selectedAutoNightType == 0) {
                            z = true;
                        }
                        themeTypeCell.setValue(string, z, true);
                        return;
                    } else if (i2 == ThemeActivity.this.nightScheduledRow) {
                        String string2 = LocaleController.getString("AutoNightScheduled", NUM);
                        if (Theme.selectedAutoNightType == 1) {
                            z = true;
                        }
                        themeTypeCell.setValue(string2, z, true);
                        return;
                    } else if (i2 == ThemeActivity.this.nightAutomaticRow) {
                        String string3 = LocaleController.getString("AutoNightAdaptive", NUM);
                        boolean z3 = Theme.selectedAutoNightType == 2;
                        if (ThemeActivity.this.nightSystemDefaultRow != -1) {
                            z = true;
                        }
                        themeTypeCell.setValue(string3, z3, z);
                        return;
                    } else if (i2 == ThemeActivity.this.nightSystemDefaultRow) {
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
                    HeaderCell headerCell = (HeaderCell) viewHolder2.itemView;
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
                    ((BrightnessControlCell) viewHolder2.itemView).setProgress(Theme.autoNightBrighnessThreshold);
                    return;
                case 7:
                    TextCheckCell textCheckCell = (TextCheckCell) viewHolder2.itemView;
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
                    NotificationsCheckCell notificationsCheckCell = (NotificationsCheckCell) viewHolder2.itemView;
                    if (i2 == ThemeActivity.this.nightThemeRow) {
                        if (Theme.selectedAutoNightType != 0) {
                            z = true;
                        }
                        String currentNightThemeName = z ? Theme.getCurrentNightThemeName() : LocaleController.getString("AutoNightThemeOff", NUM);
                        if (z) {
                            int i9 = Theme.selectedAutoNightType;
                            if (i9 == 1) {
                                str3 = LocaleController.getString("AutoNightScheduled", NUM);
                            } else if (i9 == 3) {
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
                    RecyclerListView recyclerListView = (RecyclerListView) viewHolder2.itemView;
                    ThemeAccentsListAdapter themeAccentsListAdapter = (ThemeAccentsListAdapter) recyclerListView.getAdapter();
                    themeAccentsListAdapter.notifyDataSetChanged();
                    int access$6800 = themeAccentsListAdapter.findCurrentAccent();
                    if (access$6800 == -1) {
                        access$6800 = themeAccentsListAdapter.getItemCount() - 1;
                    }
                    if (access$6800 != -1) {
                        ((LinearLayoutManager) recyclerListView.getLayoutManager()).scrollToPositionWithOffset(access$6800, (ThemeActivity.this.listView.getMeasuredWidth() / 2) - AndroidUtilities.dp(42.0f));
                        return;
                    }
                    return;
                case 14:
                    TextCell textCell = (TextCell) viewHolder2.itemView;
                    textCell.setColors("windowBackgroundWhiteBlueText4", "windowBackgroundWhiteBlueText4");
                    if (i2 == ThemeActivity.this.backgroundRow) {
                        textCell.setTextAndIcon(LocaleController.getString("ChangeChatBackground", NUM), NUM, false);
                        return;
                    } else if (i2 == ThemeActivity.this.editThemeRow) {
                        textCell.setTextAndIcon(LocaleController.getString("EditCurrentTheme", NUM), NUM, true);
                        return;
                    } else if (i2 == ThemeActivity.this.createNewThemeRow) {
                        textCell.setTextAndIcon(LocaleController.getString("CreateNewTheme", NUM), NUM, false);
                        return;
                    } else {
                        return;
                    }
                case 17:
                    ((DefaultThemesPreviewCell) viewHolder2.itemView).updateDayNightMode();
                    return;
                case 18:
                    TextSettingsCell textSettingsCell2 = (TextSettingsCell) viewHolder2.itemView;
                    textSettingsCell2.setText(LocaleController.getString("DoubleTapSetting", NUM), false);
                    String doubleTapReaction = MediaDataController.getInstance(ThemeActivity.this.currentAccount).getDoubleTapReaction();
                    if (doubleTapReaction != null && (tLRPC$TL_availableReaction = MediaDataController.getInstance(ThemeActivity.this.currentAccount).getReactionsMap().get(doubleTapReaction)) != null) {
                        textSettingsCell2.getValueBackupImageView().getImageReceiver().setImage(ImageLocation.getForDocument(tLRPC$TL_availableReaction.static_icon), "100_100", DocumentObject.getSvgThumb(tLRPC$TL_availableReaction.static_icon.thumbs, "windowBackgroundGray", 1.0f), "webp", tLRPC$TL_availableReaction, 1);
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
            if (i == ThemeActivity.this.scheduleFromRow || i == ThemeActivity.this.distanceRow || i == ThemeActivity.this.scheduleToRow || i == ThemeActivity.this.scheduleUpdateLocationRow || i == ThemeActivity.this.contactsReimportRow || i == ThemeActivity.this.contactsSortRow || i == ThemeActivity.this.stickersRow) {
                return 1;
            }
            if (i == ThemeActivity.this.automaticBrightnessInfoRow || i == ThemeActivity.this.scheduleLocationInfoRow) {
                return 2;
            }
            if (i == ThemeActivity.this.themeInfoRow || i == ThemeActivity.this.nightTypeInfoRow || i == ThemeActivity.this.scheduleFromToInfoRow || i == ThemeActivity.this.stickersSection2Row || i == ThemeActivity.this.settings2Row || i == ThemeActivity.this.newThemeInfoRow || i == ThemeActivity.this.chatListInfoRow || i == ThemeActivity.this.bubbleRadiusInfoRow || i == ThemeActivity.this.swipeGestureInfoRow) {
                return 3;
            }
            if (i == ThemeActivity.this.nightDisabledRow || i == ThemeActivity.this.nightScheduledRow || i == ThemeActivity.this.nightAutomaticRow || i == ThemeActivity.this.nightSystemDefaultRow) {
                return 4;
            }
            if (i == ThemeActivity.this.scheduleHeaderRow || i == ThemeActivity.this.automaticHeaderRow || i == ThemeActivity.this.preferedHeaderRow || i == ThemeActivity.this.settingsRow || i == ThemeActivity.this.themeHeaderRow || i == ThemeActivity.this.textSizeHeaderRow || i == ThemeActivity.this.chatListHeaderRow || i == ThemeActivity.this.bubbleRadiusHeaderRow || i == ThemeActivity.this.swipeGestureHeaderRow || i == ThemeActivity.this.selectThemeHeaderRow) {
                return 5;
            }
            if (i == ThemeActivity.this.automaticBrightnessRow) {
                return 6;
            }
            if (i == ThemeActivity.this.scheduleLocationRow || i == ThemeActivity.this.enableAnimationsRow || i == ThemeActivity.this.sendByEnterRow || i == ThemeActivity.this.saveToGalleryRow || i == ThemeActivity.this.raiseToSpeakRow || i == ThemeActivity.this.customTabsRow || i == ThemeActivity.this.directShareRow || i == ThemeActivity.this.emojiRow || i == ThemeActivity.this.chatBlurRow) {
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
            if (i == ThemeActivity.this.backgroundRow || i == ThemeActivity.this.editThemeRow || i == ThemeActivity.this.createNewThemeRow) {
                return 14;
            }
            if (i == ThemeActivity.this.swipeGestureRow) {
                return 15;
            }
            if (i == ThemeActivity.this.themePreviewRow) {
                return 16;
            }
            if (i == ThemeActivity.this.themeListRow2) {
                return 17;
            }
            if (i == ThemeActivity.this.reactionsDoubleTapRow) {
                return 18;
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
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextSettingsCell.class, TextCheckCell.class, HeaderCell.class, BrightnessControlCell.class, ThemeTypeCell.class, TextSizeCell.class, BubbleRadiusCell.class, ChatListCell.class, NotificationsCheckCell.class, ThemesHorizontalListCell.class, TintRecyclerListView.class, TextCell.class, SwipeGestureSettingsView.class, DefaultThemesPreviewCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultIcon"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultTitle"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUBACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSubmenuBackground"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSubmenuItem"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM | ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSubmenuItemIcon"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "divider"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText4"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteValueText"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueHeader"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueText4"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueText4"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrack"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrackChecked"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{BrightnessControlCell.class}, new String[]{"leftImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayIcon"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{BrightnessControlCell.class}, new String[]{"rightImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayIcon"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{BrightnessControlCell.class}, new String[]{"seekBarView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "player_progressBackground"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_PROGRESSBAR, new Class[]{BrightnessControlCell.class}, new String[]{"seekBarView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "player_progress"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{ThemeTypeCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{ThemeTypeCell.class}, new String[]{"checkImage"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "featuredStickers_addedIcon"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_PROGRESSBAR, new Class[]{TextSizeCell.class}, new String[]{"sizeBar"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "player_progress"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextSizeCell.class}, new String[]{"sizeBar"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "player_progressBackground"));
        arrayList.add(new ThemeDescription((View) this.listView, ThemeDescription.FLAG_PROGRESSBAR, new Class[]{BubbleRadiusCell.class}, new String[]{"sizeBar"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "player_progress"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{BubbleRadiusCell.class}, new String[]{"sizeBar"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "player_progressBackground"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{ChatListCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "radioBackground"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{ChatListCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "radioBackgroundChecked"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText2"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrack"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrackChecked"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgInDrawable, Theme.chat_msgInMediaDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inBubble"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgInSelectedDrawable, Theme.chat_msgInMediaSelectedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inBubbleSelected"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, (Paint) null, Theme.chat_msgInDrawable.getShadowDrawables(), (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inBubbleShadow"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, (Paint) null, Theme.chat_msgInMediaDrawable.getShadowDrawables(), (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inBubbleShadow"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgOutDrawable, Theme.chat_msgOutMediaDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outBubble"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgOutDrawable, Theme.chat_msgOutMediaDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outBubbleGradient"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgOutDrawable, Theme.chat_msgOutMediaDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outBubbleGradient2"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgOutDrawable, Theme.chat_msgOutMediaDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outBubbleGradient3"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgOutSelectedDrawable, Theme.chat_msgOutMediaSelectedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outBubbleSelected"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgOutDrawable, Theme.chat_msgOutMediaDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outBubbleShadow"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgInDrawable, Theme.chat_msgInMediaDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inBubbleShadow"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_messageTextIn"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_messageTextOut"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgOutCheckDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outSentCheck"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgOutCheckSelectedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outSentCheckSelected"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgOutCheckReadDrawable, Theme.chat_msgOutHalfCheckDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outSentCheckRead"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgOutCheckReadSelectedDrawable, Theme.chat_msgOutHalfCheckSelectedDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outSentCheckReadSelected"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, (Paint) null, new Drawable[]{Theme.chat_msgMediaCheckDrawable, Theme.chat_msgMediaHalfCheckDrawable}, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_mediaSentCheck"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inReplyLine"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outReplyLine"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inReplyNameText"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outReplyNameText"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inReplyMessageText"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outReplyMessageText"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inReplyMediaMessageSelectedText"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outReplyMediaMessageSelectedText"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inTimeText"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outTimeText"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_inTimeSelectedText"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_outTimeSelectedText"));
        return arrayList;
    }

    public void checkCurrentDayNight() {
        if (this.currentType == 3) {
            boolean z = !Theme.isCurrentThemeDay();
            if (this.lastIsDarkTheme != z) {
                this.lastIsDarkTheme = z;
                RLottieDrawable rLottieDrawable = this.sunDrawable;
                rLottieDrawable.setCustomEndFrame(z ? rLottieDrawable.getFramesCount() - 1 : 0);
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
