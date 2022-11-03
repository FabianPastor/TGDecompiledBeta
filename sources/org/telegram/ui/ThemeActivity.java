package org.telegram.ui;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Dialog;
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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import androidx.annotation.Keep;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.internal.mlkit_language_id.zzdp$$ExternalSyntheticBackport0;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.time.SunDate;
import org.telegram.tgnet.TLRPC$TL_theme;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.EmojiThemes;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.AppIconsSelectorCell;
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
import org.telegram.ui.Components.SimpleThemeDescription;
import org.telegram.ui.Components.SwipeGestureSettingsView;
import org.telegram.ui.ThemeActivity;
/* loaded from: classes3.dex */
public class ThemeActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private int appIconHeaderRow;
    private int appIconSelectorRow;
    private int appIconShadowRow;
    private int automaticBrightnessInfoRow;
    private int automaticBrightnessRow;
    private int automaticHeaderRow;
    private int backgroundRow;
    private int bluetoothScoRow;
    private int bubbleRadiusHeaderRow;
    private int bubbleRadiusInfoRow;
    private int bubbleRadiusRow;
    private int chatBlurRow;
    private int chatListHeaderRow;
    private int chatListInfoRow;
    private int chatListRow;
    private int contactsReimportRow;
    private int contactsSortRow;
    private int createNewThemeRow;
    private int currentType;
    private int customTabsRow;
    private int directShareRow;
    private int distanceRow;
    private int editThemeRow;
    private int enableAnimationsRow;
    boolean hasThemeAccents;
    boolean lastIsDarkTheme;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    private ActionBarMenuItem menuItem;
    private int newThemeInfoRow;
    private int nightAutomaticRow;
    private int nightDisabledRow;
    private int nightScheduledRow;
    private int nightSystemDefaultRow;
    private int nightThemeRow;
    private int nightTypeInfoRow;
    private int preferedHeaderRow;
    private boolean previousByLocation;
    private int previousUpdatedType;
    private int raiseToSpeakRow;
    private int rowCount;
    private int saveToGalleryOption1Row;
    private int saveToGalleryOption2Row;
    private int saveToGallerySectionRow;
    private int scheduleFromRow;
    private int scheduleFromToInfoRow;
    private int scheduleHeaderRow;
    private int scheduleLocationInfoRow;
    private int scheduleLocationRow;
    private int scheduleToRow;
    private int scheduleUpdateLocationRow;
    private int selectThemeHeaderRow;
    private int sendByEnterRow;
    private int settings2Row;
    private int settingsRow;
    private Theme.ThemeAccent sharingAccent;
    private AlertDialog sharingProgressDialog;
    private Theme.ThemeInfo sharingTheme;
    private RLottieDrawable sunDrawable;
    private int swipeGestureHeaderRow;
    private int swipeGestureInfoRow;
    private int swipeGestureRow;
    private int textSizeHeaderRow;
    private int textSizeRow;
    private int themeAccentListRow;
    private int themeHeaderRow;
    private int themeInfoRow;
    private int themeListRow;
    private int themeListRow2;
    private int themePreviewRow;
    private ThemesHorizontalListCell themesHorizontalListCell;
    private boolean updateDistance;
    private boolean updateRecordViaSco;
    private boolean updatingLocation;
    private ArrayList<Theme.ThemeInfo> darkThemes = new ArrayList<>();
    private ArrayList<Theme.ThemeInfo> defaultThemes = new ArrayList<>();
    private GpsLocationListener gpsLocationListener = new GpsLocationListener(this, null);
    private GpsLocationListener networkLocationListener = new GpsLocationListener(this, null);

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public class GpsLocationListener implements LocationListener {
        @Override // android.location.LocationListener
        public void onProviderDisabled(String str) {
        }

        @Override // android.location.LocationListener
        public void onProviderEnabled(String str) {
        }

        @Override // android.location.LocationListener
        public void onStatusChanged(String str, int i, Bundle bundle) {
        }

        private GpsLocationListener() {
        }

        /* synthetic */ GpsLocationListener(ThemeActivity themeActivity, AnonymousClass1 anonymousClass1) {
            this();
        }

        @Override // android.location.LocationListener
        public void onLocationChanged(Location location) {
            if (location == null) {
                return;
            }
            ThemeActivity.this.stopLocationUpdate();
            ThemeActivity.this.updateSunTime(location, false);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public class TextSizeCell extends FrameLayout {
        private int endFontSize;
        private int lastWidth;
        private ThemePreviewMessagesCell messagesCell;
        private SeekBarView sizeBar;
        private int startFontSize;
        private TextPaint textPaint;

        public TextSizeCell(Context context) {
            super(context);
            this.startFontSize = 12;
            this.endFontSize = 30;
            setWillNotDraw(false);
            TextPaint textPaint = new TextPaint(1);
            this.textPaint = textPaint;
            textPaint.setTextSize(AndroidUtilities.dp(16.0f));
            SeekBarView seekBarView = new SeekBarView(context);
            this.sizeBar = seekBarView;
            seekBarView.setReportChanges(true);
            this.sizeBar.setSeparatorsCount((this.endFontSize - this.startFontSize) + 1);
            this.sizeBar.setDelegate(new SeekBarView.SeekBarViewDelegate(ThemeActivity.this) { // from class: org.telegram.ui.ThemeActivity.TextSizeCell.1
                @Override // org.telegram.ui.Components.SeekBarView.SeekBarViewDelegate
                public void onSeekBarPressed(boolean z) {
                }

                @Override // org.telegram.ui.Components.SeekBarView.SeekBarViewDelegate
                public void onSeekBarDrag(boolean z, float f) {
                    TextSizeCell textSizeCell = TextSizeCell.this;
                    ThemeActivity.this.setFontSize(Math.round(textSizeCell.startFontSize + ((TextSizeCell.this.endFontSize - TextSizeCell.this.startFontSize) * f)));
                }

                @Override // org.telegram.ui.Components.SeekBarView.SeekBarViewDelegate
                public CharSequence getContentDescription() {
                    return String.valueOf(Math.round(TextSizeCell.this.startFontSize + ((TextSizeCell.this.endFontSize - TextSizeCell.this.startFontSize) * TextSizeCell.this.sizeBar.getProgress())));
                }

                @Override // org.telegram.ui.Components.SeekBarView.SeekBarViewDelegate
                public int getStepsCount() {
                    return TextSizeCell.this.endFontSize - TextSizeCell.this.startFontSize;
                }
            });
            this.sizeBar.setImportantForAccessibility(2);
            addView(this.sizeBar, LayoutHelper.createFrame(-1, 38.0f, 51, 5.0f, 5.0f, 39.0f, 0.0f));
            ThemePreviewMessagesCell themePreviewMessagesCell = new ThemePreviewMessagesCell(context, ((BaseFragment) ThemeActivity.this).parentLayout, 0);
            this.messagesCell = themePreviewMessagesCell;
            if (Build.VERSION.SDK_INT >= 19) {
                themePreviewMessagesCell.setImportantForAccessibility(4);
            }
            addView(this.messagesCell, LayoutHelper.createFrame(-1, -2.0f, 51, 0.0f, 53.0f, 0.0f, 0.0f));
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            this.textPaint.setColor(Theme.getColor("windowBackgroundWhiteValueText"));
            canvas.drawText("" + SharedConfig.fontSize, getMeasuredWidth() - AndroidUtilities.dp(39.0f), AndroidUtilities.dp(28.0f), this.textPaint);
        }

        @Override // android.widget.FrameLayout, android.view.View
        protected void onMeasure(int i, int i2) {
            super.onMeasure(i, i2);
            int size = View.MeasureSpec.getSize(i);
            if (this.lastWidth != size) {
                SeekBarView seekBarView = this.sizeBar;
                int i3 = SharedConfig.fontSize;
                int i4 = this.startFontSize;
                seekBarView.setProgress((i3 - i4) / (this.endFontSize - i4));
                this.lastWidth = size;
            }
        }

        @Override // android.view.View
        public void invalidate() {
            super.invalidate();
            this.messagesCell.invalidate();
            this.sizeBar.invalidate();
        }

        @Override // android.view.View
        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
            super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
            this.sizeBar.getSeekBarAccessibilityDelegate().onInitializeAccessibilityNodeInfoInternal(this, accessibilityNodeInfo);
        }

        @Override // android.view.View
        public boolean performAccessibilityAction(int i, Bundle bundle) {
            return super.performAccessibilityAction(i, bundle) || this.sizeBar.getSeekBarAccessibilityDelegate().performAccessibilityActionInternal(this, i, bundle);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public class BubbleRadiusCell extends FrameLayout {
        private int endRadius;
        private SeekBarView sizeBar;
        private int startRadius;
        private TextPaint textPaint;

        public BubbleRadiusCell(Context context) {
            super(context);
            this.startRadius = 0;
            this.endRadius = 17;
            setWillNotDraw(false);
            TextPaint textPaint = new TextPaint(1);
            this.textPaint = textPaint;
            textPaint.setTextSize(AndroidUtilities.dp(16.0f));
            SeekBarView seekBarView = new SeekBarView(context);
            this.sizeBar = seekBarView;
            seekBarView.setReportChanges(true);
            this.sizeBar.setSeparatorsCount((this.endRadius - this.startRadius) + 1);
            this.sizeBar.setDelegate(new SeekBarView.SeekBarViewDelegate(ThemeActivity.this) { // from class: org.telegram.ui.ThemeActivity.BubbleRadiusCell.1
                @Override // org.telegram.ui.Components.SeekBarView.SeekBarViewDelegate
                public void onSeekBarPressed(boolean z) {
                }

                @Override // org.telegram.ui.Components.SeekBarView.SeekBarViewDelegate
                public void onSeekBarDrag(boolean z, float f) {
                    BubbleRadiusCell bubbleRadiusCell = BubbleRadiusCell.this;
                    ThemeActivity.this.setBubbleRadius(Math.round(bubbleRadiusCell.startRadius + ((BubbleRadiusCell.this.endRadius - BubbleRadiusCell.this.startRadius) * f)), false);
                }

                @Override // org.telegram.ui.Components.SeekBarView.SeekBarViewDelegate
                public CharSequence getContentDescription() {
                    return String.valueOf(Math.round(BubbleRadiusCell.this.startRadius + ((BubbleRadiusCell.this.endRadius - BubbleRadiusCell.this.startRadius) * BubbleRadiusCell.this.sizeBar.getProgress())));
                }

                @Override // org.telegram.ui.Components.SeekBarView.SeekBarViewDelegate
                public int getStepsCount() {
                    return BubbleRadiusCell.this.endRadius - BubbleRadiusCell.this.startRadius;
                }
            });
            this.sizeBar.setImportantForAccessibility(2);
            addView(this.sizeBar, LayoutHelper.createFrame(-1, 38.0f, 51, 5.0f, 5.0f, 39.0f, 0.0f));
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            this.textPaint.setColor(Theme.getColor("windowBackgroundWhiteValueText"));
            canvas.drawText("" + SharedConfig.bubbleRadius, getMeasuredWidth() - AndroidUtilities.dp(39.0f), AndroidUtilities.dp(28.0f), this.textPaint);
        }

        @Override // android.widget.FrameLayout, android.view.View
        protected void onMeasure(int i, int i2) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM), i2);
            SeekBarView seekBarView = this.sizeBar;
            int i3 = SharedConfig.bubbleRadius;
            int i4 = this.startRadius;
            seekBarView.setProgress((i3 - i4) / (this.endRadius - i4));
        }

        @Override // android.view.View
        public void invalidate() {
            super.invalidate();
            this.sizeBar.invalidate();
        }

        @Override // android.view.View
        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
            super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
            this.sizeBar.getSeekBarAccessibilityDelegate().onInitializeAccessibilityNodeInfoInternal(this, accessibilityNodeInfo);
        }

        @Override // android.view.View
        public boolean performAccessibilityAction(int i, Bundle bundle) {
            return super.performAccessibilityAction(i, bundle) || this.sizeBar.getSeekBarAccessibilityDelegate().performAccessibilityActionInternal(this, i, bundle);
        }
    }

    public ThemeActivity(int i) {
        this.currentType = i;
        updateRows(true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean setBubbleRadius(int i, boolean z) {
        if (i != SharedConfig.bubbleRadius) {
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
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean setFontSize(int i) {
        if (i != SharedConfig.fontSize) {
            SharedConfig.fontSize = i;
            SharedConfig.fontSizeIsDefault = false;
            SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
            if (sharedPreferences == null) {
                return false;
            }
            SharedPreferences.Editor edit = sharedPreferences.edit();
            edit.putInt("fons_size", SharedConfig.fontSize);
            edit.commit();
            Theme.createCommonMessageResources();
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
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
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
        this.chatBlurRow = -1;
        this.textSizeRow = -1;
        this.backgroundRow = -1;
        this.settingsRow = -1;
        this.customTabsRow = -1;
        this.directShareRow = -1;
        this.enableAnimationsRow = -1;
        this.raiseToSpeakRow = -1;
        this.sendByEnterRow = -1;
        this.saveToGalleryOption1Row = -1;
        this.saveToGalleryOption2Row = -1;
        this.saveToGallerySectionRow = -1;
        this.distanceRow = -1;
        this.bluetoothScoRow = -1;
        this.settings2Row = -1;
        this.swipeGestureHeaderRow = -1;
        this.swipeGestureRow = -1;
        this.swipeGestureInfoRow = -1;
        this.selectThemeHeaderRow = -1;
        this.themePreviewRow = -1;
        this.editThemeRow = -1;
        this.createNewThemeRow = -1;
        this.appIconHeaderRow = -1;
        this.appIconSelectorRow = -1;
        this.appIconShadowRow = -1;
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
        Collections.sort(this.defaultThemes, ThemeActivity$$ExternalSyntheticLambda10.INSTANCE);
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
            ThemesHorizontalListCell themesHorizontalListCell = this.themesHorizontalListCell;
            if (themesHorizontalListCell != null) {
                themesHorizontalListCell.setDrawDivider(hasAccentColors);
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
            this.appIconHeaderRow = i40;
            int i42 = i41 + 1;
            this.rowCount = i42;
            this.appIconSelectorRow = i41;
            int i43 = i42 + 1;
            this.rowCount = i43;
            this.appIconShadowRow = i42;
            int i44 = i43 + 1;
            this.rowCount = i44;
            this.settingsRow = i43;
            int i45 = i44 + 1;
            this.rowCount = i45;
            this.nightThemeRow = i44;
            int i46 = i45 + 1;
            this.rowCount = i46;
            this.customTabsRow = i45;
            int i47 = i46 + 1;
            this.rowCount = i47;
            this.directShareRow = i46;
            int i48 = i47 + 1;
            this.rowCount = i48;
            this.enableAnimationsRow = i47;
            int i49 = i48 + 1;
            this.rowCount = i49;
            this.raiseToSpeakRow = i48;
            int i50 = i49 + 1;
            this.rowCount = i50;
            this.bluetoothScoRow = i49;
            this.rowCount = i50 + 1;
            this.sendByEnterRow = i50;
            if (SharedConfig.canBlurChat()) {
                int i51 = this.rowCount;
                this.rowCount = i51 + 1;
                this.chatBlurRow = i51;
            }
            int i52 = this.rowCount;
            int i53 = i52 + 1;
            this.rowCount = i53;
            this.distanceRow = i52;
            this.rowCount = i53 + 1;
            this.settings2Row = i53;
        } else {
            int i54 = this.rowCount;
            int i55 = i54 + 1;
            this.rowCount = i55;
            this.nightDisabledRow = i54;
            int i56 = i55 + 1;
            this.rowCount = i56;
            this.nightScheduledRow = i55;
            int i57 = i56 + 1;
            this.rowCount = i57;
            this.nightAutomaticRow = i56;
            if (Build.VERSION.SDK_INT >= 29) {
                this.rowCount = i57 + 1;
                this.nightSystemDefaultRow = i57;
            }
            int i58 = this.rowCount;
            int i59 = i58 + 1;
            this.rowCount = i59;
            this.nightTypeInfoRow = i58;
            int i60 = Theme.selectedAutoNightType;
            if (i60 == 1) {
                int i61 = i59 + 1;
                this.rowCount = i61;
                this.scheduleHeaderRow = i59;
                int i62 = i61 + 1;
                this.rowCount = i62;
                this.scheduleLocationRow = i61;
                if (Theme.autoNightScheduleByLocation) {
                    int i63 = i62 + 1;
                    this.rowCount = i63;
                    this.scheduleUpdateLocationRow = i62;
                    this.rowCount = i63 + 1;
                    this.scheduleLocationInfoRow = i63;
                } else {
                    int i64 = i62 + 1;
                    this.rowCount = i64;
                    this.scheduleFromRow = i62;
                    int i65 = i64 + 1;
                    this.rowCount = i65;
                    this.scheduleToRow = i64;
                    this.rowCount = i65 + 1;
                    this.scheduleFromToInfoRow = i65;
                }
            } else if (i60 == 2) {
                int i66 = i59 + 1;
                this.rowCount = i66;
                this.automaticHeaderRow = i59;
                int i67 = i66 + 1;
                this.rowCount = i67;
                this.automaticBrightnessRow = i66;
                this.rowCount = i67 + 1;
                this.automaticBrightnessInfoRow = i67;
            }
            if (Theme.selectedAutoNightType != 0) {
                int i68 = this.rowCount;
                int i69 = i68 + 1;
                this.rowCount = i69;
                this.preferedHeaderRow = i68;
                this.rowCount = i69 + 1;
                this.themeListRow = i69;
                boolean hasAccentColors2 = Theme.getCurrentNightTheme().hasAccentColors();
                this.hasThemeAccents = hasAccentColors2;
                ThemesHorizontalListCell themesHorizontalListCell2 = this.themesHorizontalListCell;
                if (themesHorizontalListCell2 != null) {
                    themesHorizontalListCell2.setDrawDivider(hasAccentColors2);
                }
                if (this.hasThemeAccents) {
                    int i70 = this.rowCount;
                    this.rowCount = i70 + 1;
                    this.themeAccentListRow = i70;
                }
                int i71 = this.rowCount;
                this.rowCount = i71 + 1;
                this.themeInfoRow = i71;
            }
        }
        ThemesHorizontalListCell themesHorizontalListCell3 = this.themesHorizontalListCell;
        if (themesHorizontalListCell3 != null) {
            themesHorizontalListCell3.notifyDataSetChanged(this.listView.getWidth());
        }
        ListAdapter listAdapter = this.listAdapter;
        if (listAdapter != null) {
            if (this.currentType != 1 || (i4 = this.previousUpdatedType) == (i5 = Theme.selectedAutoNightType) || i4 == -1) {
                if (z || this.previousUpdatedType == -1) {
                    listAdapter.notifyDataSetChanged();
                } else {
                    if (i8 == -1 && (i3 = this.themeAccentListRow) != -1) {
                        listAdapter.notifyItemInserted(i3);
                    } else if (i8 != -1 && this.themeAccentListRow == -1) {
                        listAdapter.notifyItemRemoved(i8);
                        if (i9 != -1) {
                            i9--;
                        }
                    } else {
                        int i72 = this.themeAccentListRow;
                        if (i72 != -1) {
                            listAdapter.notifyItemChanged(i72);
                        }
                    }
                    if (i9 == -1 && (i2 = this.editThemeRow) != -1) {
                        this.listAdapter.notifyItemInserted(i2);
                    } else if (i9 != -1 && this.editThemeRow == -1) {
                        this.listAdapter.notifyItemRemoved(i9);
                    }
                }
            } else {
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
                            ListAdapter listAdapter2 = this.listAdapter;
                            if (!Theme.autoNightScheduleByLocation) {
                                i6 = 5;
                            }
                            listAdapter2.notifyItemRangeInserted(i73, i6);
                        } else if (i76 == 3) {
                            ListAdapter listAdapter3 = this.listAdapter;
                            if (!Theme.autoNightScheduleByLocation) {
                                i6 = 5;
                            }
                            listAdapter3.notifyItemRangeInserted(i73, i6);
                        }
                    } else if (i75 == 2) {
                        int i77 = this.previousUpdatedType;
                        if (i77 == 0) {
                            this.listAdapter.notifyItemRangeInserted(i73, this.rowCount - i73);
                        } else if (i77 == 1) {
                            ListAdapter listAdapter4 = this.listAdapter;
                            if (!Theme.autoNightScheduleByLocation) {
                                i6 = 5;
                            }
                            listAdapter4.notifyItemRangeRemoved(i73, i6);
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
                            ListAdapter listAdapter5 = this.listAdapter;
                            if (!Theme.autoNightScheduleByLocation) {
                                i6 = 5;
                            }
                            listAdapter5.notifyItemRangeRemoved(i73, i6);
                        }
                    }
                } else {
                    boolean z2 = this.previousByLocation;
                    boolean z3 = Theme.autoNightScheduleByLocation;
                    if (z2 != z3) {
                        int i79 = i73 + 2;
                        listAdapter.notifyItemRangeRemoved(i79, z3 ? 3 : 2);
                        ListAdapter listAdapter6 = this.listAdapter;
                        if (Theme.autoNightScheduleByLocation) {
                            i = 2;
                        }
                        listAdapter6.notifyItemRangeInserted(i79, i);
                    }
                }
            }
        }
        if (this.currentType == 1) {
            this.previousByLocation = Theme.autoNightScheduleByLocation;
            this.previousUpdatedType = Theme.selectedAutoNightType;
        }
        updateMenuItem();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ int lambda$updateRows$0(Theme.ThemeInfo themeInfo, Theme.ThemeInfo themeInfo2) {
        return zzdp$$ExternalSyntheticBackport0.m(themeInfo.sortIndex, themeInfo2.sortIndex);
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
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

    @Override // org.telegram.ui.ActionBar.BaseFragment
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

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        int i3;
        AlertDialog alertDialog;
        int i4;
        if (i == NotificationCenter.locationPermissionGranted) {
            updateSunTime(null, true);
        } else if (i == NotificationCenter.didSetNewWallpapper || i == NotificationCenter.emojiLoaded) {
            RecyclerListView recyclerListView = this.listView;
            if (recyclerListView != null) {
                recyclerListView.invalidateViews();
            }
            updateMenuItem();
        } else if (i == NotificationCenter.themeAccentListUpdated) {
            ListAdapter listAdapter = this.listAdapter;
            if (listAdapter == null || (i4 = this.themeAccentListRow) == -1) {
                return;
            }
            listAdapter.notifyItemChanged(i4, new Object());
        } else if (i == NotificationCenter.themeListUpdated) {
            updateRows(true);
        } else if (i == NotificationCenter.themeUploadedToServer) {
            Theme.ThemeInfo themeInfo = (Theme.ThemeInfo) objArr[0];
            Theme.ThemeAccent themeAccent = (Theme.ThemeAccent) objArr[1];
            if (themeInfo != this.sharingTheme || themeAccent != this.sharingAccent) {
                return;
            }
            StringBuilder sb = new StringBuilder();
            sb.append("https://");
            sb.append(getMessagesController().linkPrefix);
            sb.append("/addtheme/");
            sb.append((themeAccent != null ? themeAccent.info : themeInfo.info).slug);
            String sb2 = sb.toString();
            showDialog(new ShareAlert(getParentActivity(), null, sb2, false, sb2, false));
            AlertDialog alertDialog2 = this.sharingProgressDialog;
            if (alertDialog2 == null) {
                return;
            }
            alertDialog2.dismiss();
        } else if (i == NotificationCenter.themeUploadError) {
            Theme.ThemeInfo themeInfo2 = (Theme.ThemeInfo) objArr[0];
            Theme.ThemeAccent themeAccent2 = (Theme.ThemeAccent) objArr[1];
            if (themeInfo2 != this.sharingTheme || themeAccent2 != this.sharingAccent || (alertDialog = this.sharingProgressDialog) != null) {
                return;
            }
            alertDialog.dismiss();
        } else if (i == NotificationCenter.needShareTheme) {
            if (getParentActivity() == null || this.isPaused) {
                return;
            }
            this.sharingTheme = (Theme.ThemeInfo) objArr[0];
            this.sharingAccent = (Theme.ThemeAccent) objArr[1];
            AlertDialog alertDialog3 = new AlertDialog(getParentActivity(), 3);
            this.sharingProgressDialog = alertDialog3;
            alertDialog3.setCanCancel(true);
            showDialog(this.sharingProgressDialog, new DialogInterface.OnDismissListener() { // from class: org.telegram.ui.ThemeActivity$$ExternalSyntheticLambda5
                @Override // android.content.DialogInterface.OnDismissListener
                public final void onDismiss(DialogInterface dialogInterface) {
                    ThemeActivity.this.lambda$didReceivedNotification$1(dialogInterface);
                }
            });
        } else if (i == NotificationCenter.needSetDayNightTheme) {
            updateMenuItem();
            checkCurrentDayNight();
        } else if (i != NotificationCenter.emojiPreviewThemesChanged || (i3 = this.themeListRow2) < 0) {
        } else {
            this.listAdapter.notifyItemChanged(i3);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$didReceivedNotification$1(DialogInterface dialogInterface) {
        this.sharingProgressDialog = null;
        this.sharingTheme = null;
        this.sharingAccent = null;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public View createView(final Context context) {
        this.lastIsDarkTheme = !Theme.isCurrentThemeDay();
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(false);
        if (AndroidUtilities.isTablet()) {
            this.actionBar.setOccupyStatusBar(false);
        }
        int i = this.currentType;
        if (i == 3) {
            this.actionBar.setTitle(LocaleController.getString("BrowseThemes", R.string.BrowseThemes));
            ActionBarMenu createMenu = this.actionBar.createMenu();
            int i2 = R.raw.sun;
            RLottieDrawable rLottieDrawable = new RLottieDrawable(i2, "" + i2, AndroidUtilities.dp(28.0f), AndroidUtilities.dp(28.0f), true, null);
            this.sunDrawable = rLottieDrawable;
            if (this.lastIsDarkTheme) {
                rLottieDrawable.setCurrentFrame(rLottieDrawable.getFramesCount() - 1);
            } else {
                rLottieDrawable.setCurrentFrame(0);
            }
            this.sunDrawable.setPlayInDirectionOfCustomEndFrame(true);
            this.menuItem = createMenu.addItem(5, this.sunDrawable);
        } else if (i == 0) {
            this.actionBar.setTitle(LocaleController.getString("ChatSettings", R.string.ChatSettings));
            ActionBarMenuItem addItem = this.actionBar.createMenu().addItem(0, R.drawable.ic_ab_other);
            this.menuItem = addItem;
            addItem.setContentDescription(LocaleController.getString("AccDescrMoreOptions", R.string.AccDescrMoreOptions));
            this.menuItem.addSubItem(2, R.drawable.msg_share, LocaleController.getString("ShareTheme", R.string.ShareTheme));
            this.menuItem.addSubItem(3, R.drawable.msg_edit, LocaleController.getString("EditThemeColors", R.string.EditThemeColors));
            this.menuItem.addSubItem(1, R.drawable.msg_palette, LocaleController.getString("CreateNewThemeMenu", R.string.CreateNewThemeMenu));
            this.menuItem.addSubItem(4, R.drawable.msg_reset, LocaleController.getString("ThemeResetToDefaults", R.string.ThemeResetToDefaults));
        } else {
            this.actionBar.setTitle(LocaleController.getString("AutoNightTheme", R.string.AutoNightTheme));
        }
        this.actionBar.setActionBarMenuOnItemClick(new AnonymousClass1());
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
        this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListenerExtended() { // from class: org.telegram.ui.ThemeActivity$$ExternalSyntheticLambda12
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListenerExtended
            public /* synthetic */ boolean hasDoubleTap(View view, int i3) {
                return RecyclerListView.OnItemClickListenerExtended.CC.$default$hasDoubleTap(this, view, i3);
            }

            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListenerExtended
            public /* synthetic */ void onDoubleTap(View view, int i3, float f, float f2) {
                RecyclerListView.OnItemClickListenerExtended.CC.$default$onDoubleTap(this, view, i3, f, f2);
            }

            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListenerExtended
            public final void onItemClick(View view, int i3, float f, float f2) {
                ThemeActivity.this.lambda$createView$7(context, view, i3, f, f2);
            }
        });
        return this.fragmentView;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: org.telegram.ui.ThemeActivity$1  reason: invalid class name */
    /* loaded from: classes3.dex */
    public class AnonymousClass1 extends ActionBar.ActionBarMenuOnItemClick {
        AnonymousClass1() {
        }

        /* JADX WARN: Removed duplicated region for block: B:50:0x0156  */
        /* JADX WARN: Removed duplicated region for block: B:51:0x015b  */
        @Override // org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public void onItemClick(int r13) {
            /*
                Method dump skipped, instructions count: 456
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ThemeActivity.AnonymousClass1.onItemClick(int):void");
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onItemClick$0(DialogInterface dialogInterface, int i) {
            boolean fontSize = ThemeActivity.this.setFontSize(AndroidUtilities.isTablet() ? 18 : 16);
            if (ThemeActivity.this.setBubbleRadius(10, true)) {
                fontSize = true;
            }
            if (fontSize) {
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
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$7(Context context, View view, final int i, float f, float f2) {
        int i2;
        int i3;
        String string;
        if (i == this.enableAnimationsRow) {
            SharedPreferences globalMainSettings = MessagesController.getGlobalMainSettings();
            boolean z = globalMainSettings.getBoolean("view_animations", true);
            SharedPreferences.Editor edit = globalMainSettings.edit();
            edit.putBoolean("view_animations", !z);
            SharedConfig.setAnimationsEnabled(!z);
            edit.commit();
            if (!(view instanceof TextCheckCell)) {
                return;
            }
            ((TextCheckCell) view).setChecked(!z);
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
            if (!(view instanceof TextCheckCell)) {
                return;
            }
            ((TextCheckCell) view).setChecked(!z3);
        } else if (i == this.raiseToSpeakRow) {
            SharedConfig.toogleRaiseToSpeak();
            if (!(view instanceof TextCheckCell)) {
                return;
            }
            ((TextCheckCell) view).setChecked(SharedConfig.raiseToSpeak);
        } else if (i == this.distanceRow) {
            if (getParentActivity() == null) {
                return;
            }
            showDialog(new AlertDialog.Builder(getParentActivity()).setTitle(LocaleController.getString("DistanceUnitsTitle", R.string.DistanceUnitsTitle)).setItems(new CharSequence[]{LocaleController.getString("DistanceUnitsAutomatic", R.string.DistanceUnitsAutomatic), LocaleController.getString("DistanceUnitsKilometers", R.string.DistanceUnitsKilometers), LocaleController.getString("DistanceUnitsMiles", R.string.DistanceUnitsMiles)}, new DialogInterface.OnClickListener() { // from class: org.telegram.ui.ThemeActivity$$ExternalSyntheticLambda2
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i4) {
                    ThemeActivity.this.lambda$createView$2(dialogInterface, i4);
                }
            }).setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null).create());
        } else if (i == this.bluetoothScoRow) {
            if (getParentActivity() == null) {
                return;
            }
            final AtomicReference atomicReference = new AtomicReference();
            LinearLayout linearLayout = new LinearLayout(context);
            linearLayout.setOrientation(1);
            TextView textView = new TextView(context);
            textView.setText(LocaleController.getString(R.string.MicrophoneForVoiceMessagesBuiltIn));
            textView.setTextColor(getThemedColor("windowBackgroundWhiteBlackText"));
            textView.setTextSize(1, 16.0f);
            textView.setPadding(AndroidUtilities.dp(21.0f), AndroidUtilities.dp(12.0f), AndroidUtilities.dp(21.0f), AndroidUtilities.dp(12.0f));
            textView.setBackground(Theme.AdaptiveRipple.rect(getThemedColor("windowBackgroundWhite")));
            textView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.ThemeActivity$$ExternalSyntheticLambda6
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    ThemeActivity.this.lambda$createView$3(atomicReference, view2);
                }
            });
            linearLayout.addView(textView);
            LinearLayout linearLayout2 = new LinearLayout(context);
            linearLayout2.setOrientation(1);
            linearLayout2.setPadding(AndroidUtilities.dp(21.0f), AndroidUtilities.dp(12.0f), AndroidUtilities.dp(21.0f), AndroidUtilities.dp(12.0f));
            linearLayout2.setBackground(Theme.AdaptiveRipple.rect(getThemedColor("windowBackgroundWhite")));
            linearLayout2.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.ThemeActivity$$ExternalSyntheticLambda7
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    ThemeActivity.this.lambda$createView$4(atomicReference, view2);
                }
            });
            linearLayout.addView(linearLayout2);
            TextView textView2 = new TextView(context);
            textView2.setText(LocaleController.getString(R.string.MicrophoneForVoiceMessagesScoIfConnected));
            textView2.setTextColor(getThemedColor("windowBackgroundWhiteBlackText"));
            textView2.setTextSize(1, 16.0f);
            linearLayout2.addView(textView2);
            TextView textView3 = new TextView(context);
            textView3.setText(LocaleController.getString(R.string.MicrophoneForVoiceMessagesScoHint));
            textView3.setTextColor(getThemedColor("windowBackgroundWhiteGrayText"));
            textView3.setTextSize(1, 14.0f);
            linearLayout2.addView(textView3);
            AlertDialog create = new AlertDialog.Builder(getParentActivity()).setTitle(LocaleController.getString(R.string.MicrophoneForVoiceMessages)).setView(linearLayout).setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null).create();
            atomicReference.set(create);
            showDialog(create);
        } else if (i == this.customTabsRow) {
            SharedConfig.toggleCustomTabs();
            if (!(view instanceof TextCheckCell)) {
                return;
            }
            ((TextCheckCell) view).setChecked(SharedConfig.customTabs);
        } else if (i == this.directShareRow) {
            SharedConfig.toggleDirectShare();
            if (!(view instanceof TextCheckCell)) {
                return;
            }
            ((TextCheckCell) view).setChecked(SharedConfig.directShare);
        } else if (i == this.contactsReimportRow) {
        } else {
            if (i == this.contactsSortRow) {
                if (getParentActivity() == null) {
                    return;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
                builder.setTitle(LocaleController.getString("SortBy", R.string.SortBy));
                builder.setItems(new CharSequence[]{LocaleController.getString("Default", R.string.Default), LocaleController.getString("SortFirstName", R.string.SortFirstName), LocaleController.getString("SortLastName", R.string.SortLastName)}, new DialogInterface.OnClickListener() { // from class: org.telegram.ui.ThemeActivity$$ExternalSyntheticLambda4
                    @Override // android.content.DialogInterface.OnClickListener
                    public final void onClick(DialogInterface dialogInterface, int i4) {
                        ThemeActivity.this.lambda$createView$5(i, dialogInterface, i4);
                    }
                });
                builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                showDialog(builder.create());
            } else if (i == this.chatBlurRow) {
                SharedConfig.toggleChatBlur();
                if (!(view instanceof TextCheckCell)) {
                    return;
                }
                ((TextCheckCell) view).setChecked(SharedConfig.chatBlurEnabled());
            } else if (i == this.nightThemeRow) {
                if ((LocaleController.isRTL && f <= AndroidUtilities.dp(76.0f)) || (!LocaleController.isRTL && f >= view.getMeasuredWidth() - AndroidUtilities.dp(76.0f))) {
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
                    String currentNightThemeName = z2 ? Theme.getCurrentNightThemeName() : LocaleController.getString("AutoNightThemeOff", R.string.AutoNightThemeOff);
                    if (z2) {
                        int i4 = Theme.selectedAutoNightType;
                        if (i4 == 1) {
                            string = LocaleController.getString("AutoNightScheduled", R.string.AutoNightScheduled);
                        } else if (i4 == 3) {
                            string = LocaleController.getString("AutoNightSystemDefault", R.string.AutoNightSystemDefault);
                        } else {
                            string = LocaleController.getString("AutoNightAdaptive", R.string.AutoNightAdaptive);
                        }
                        currentNightThemeName = string + " " + currentNightThemeName;
                    }
                    notificationsCheckCell.setTextAndValueAndCheck(LocaleController.getString("AutoNightTheme", R.string.AutoNightTheme), currentNightThemeName, z2, true);
                    return;
                }
                presentFragment(new ThemeActivity(1));
            } else if (i == this.nightDisabledRow) {
                if (Theme.selectedAutoNightType == 0) {
                    return;
                }
                Theme.selectedAutoNightType = 0;
                updateRows(true);
                Theme.checkAutoNightThemeConditions();
            } else if (i == this.nightScheduledRow) {
                if (Theme.selectedAutoNightType == 1) {
                    return;
                }
                Theme.selectedAutoNightType = 1;
                if (Theme.autoNightScheduleByLocation) {
                    updateSunTime(null, true);
                }
                updateRows(true);
                Theme.checkAutoNightThemeConditions();
            } else if (i == this.nightAutomaticRow) {
                if (Theme.selectedAutoNightType == 2) {
                    return;
                }
                Theme.selectedAutoNightType = 2;
                updateRows(true);
                Theme.checkAutoNightThemeConditions();
            } else if (i == this.nightSystemDefaultRow) {
                if (Theme.selectedAutoNightType == 3) {
                    return;
                }
                Theme.selectedAutoNightType = 3;
                updateRows(true);
                Theme.checkAutoNightThemeConditions();
            } else if (i == this.scheduleLocationRow) {
                boolean z4 = !Theme.autoNightScheduleByLocation;
                Theme.autoNightScheduleByLocation = z4;
                ((TextCheckCell) view).setChecked(z4);
                updateRows(true);
                if (Theme.autoNightScheduleByLocation) {
                    updateSunTime(null, true);
                }
                Theme.checkAutoNightThemeConditions();
            } else if (i == this.scheduleFromRow || i == this.scheduleToRow) {
                if (getParentActivity() == null) {
                    return;
                }
                if (i == this.scheduleFromRow) {
                    i2 = Theme.autoNightDayStartTime;
                    i3 = i2 / 60;
                } else {
                    i2 = Theme.autoNightDayEndTime;
                    i3 = i2 / 60;
                }
                final TextSettingsCell textSettingsCell = (TextSettingsCell) view;
                showDialog(new TimePickerDialog(getParentActivity(), new TimePickerDialog.OnTimeSetListener() { // from class: org.telegram.ui.ThemeActivity$$ExternalSyntheticLambda0
                    @Override // android.app.TimePickerDialog.OnTimeSetListener
                    public final void onTimeSet(TimePicker timePicker, int i5, int i6) {
                        ThemeActivity.this.lambda$createView$6(i, textSettingsCell, timePicker, i5, i6);
                    }
                }, i3, i2 - (i3 * 60), true));
            } else if (i == this.scheduleUpdateLocationRow) {
                updateSunTime(null, true);
            } else if (i == this.createNewThemeRow) {
                createNewTheme();
            } else if (i != this.editThemeRow) {
            } else {
                editTheme();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$2(DialogInterface dialogInterface, int i) {
        SharedConfig.setDistanceSystemType(i);
        this.updateDistance = true;
        RecyclerView.ViewHolder findViewHolderForAdapterPosition = this.listView.findViewHolderForAdapterPosition(this.distanceRow);
        if (findViewHolderForAdapterPosition != null) {
            this.listAdapter.onBindViewHolder(findViewHolderForAdapterPosition, this.distanceRow);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$3(AtomicReference atomicReference, View view) {
        SharedConfig.recordViaSco = false;
        SharedConfig.saveConfig();
        this.updateRecordViaSco = true;
        ((Dialog) atomicReference.get()).dismiss();
        RecyclerView.ViewHolder findViewHolderForAdapterPosition = this.listView.findViewHolderForAdapterPosition(this.bluetoothScoRow);
        if (findViewHolderForAdapterPosition != null) {
            this.listAdapter.onBindViewHolder(findViewHolderForAdapterPosition, this.bluetoothScoRow);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$4(AtomicReference atomicReference, View view) {
        SharedConfig.recordViaSco = true;
        SharedConfig.saveConfig();
        this.updateRecordViaSco = true;
        ((Dialog) atomicReference.get()).dismiss();
        RecyclerView.ViewHolder findViewHolderForAdapterPosition = this.listView.findViewHolderForAdapterPosition(this.bluetoothScoRow);
        if (findViewHolderForAdapterPosition != null) {
            this.listAdapter.onBindViewHolder(findViewHolderForAdapterPosition, this.bluetoothScoRow);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$5(int i, DialogInterface dialogInterface, int i2) {
        SharedPreferences.Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putInt("sortContactsBy", i2);
        edit.commit();
        ListAdapter listAdapter = this.listAdapter;
        if (listAdapter != null) {
            listAdapter.notifyItemChanged(i);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$6(int i, TextSettingsCell textSettingsCell, TimePicker timePicker, int i2, int i3) {
        int i4 = (i2 * 60) + i3;
        if (i == this.scheduleFromRow) {
            Theme.autoNightDayStartTime = i4;
            textSettingsCell.setTextAndValue(LocaleController.getString("AutoNightFrom", R.string.AutoNightFrom), String.format("%02d:%02d", Integer.valueOf(i2), Integer.valueOf(i3)), true);
            return;
        }
        Theme.autoNightDayEndTime = i4;
        textSettingsCell.setTextAndValue(LocaleController.getString("AutoNightTo", R.string.AutoNightTo), String.format("%02d:%02d", Integer.valueOf(i2), Integer.valueOf(i3)), true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void editTheme() {
        Theme.ThemeInfo currentTheme = Theme.getCurrentTheme();
        presentFragment(new ThemePreviewActivity(currentTheme, false, 1, currentTheme.getAccent(false).id >= 100, this.currentType == 1));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void createNewTheme() {
        if (getParentActivity() == null) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setTitle(LocaleController.getString("NewTheme", R.string.NewTheme));
        builder.setMessage(LocaleController.getString("CreateNewThemeAlert", R.string.CreateNewThemeAlert));
        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
        builder.setPositiveButton(LocaleController.getString("CreateTheme", R.string.CreateTheme), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.ThemeActivity$$ExternalSyntheticLambda3
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                ThemeActivity.this.lambda$createNewTheme$8(dialogInterface, i);
            }
        });
        showDialog(builder.create());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createNewTheme$8(DialogInterface dialogInterface, int i) {
        AlertsCreator.createThemeCreateDialog(this, 0, null, null);
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onResume() {
        super.onResume();
        if (this.listAdapter != null) {
            updateRows(true);
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onTransitionAnimationEnd(boolean z, boolean z2) {
        if (z) {
            AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
            AndroidUtilities.setAdjustResizeToNothing(getParentActivity(), this.classGuid);
        }
    }

    private void updateMenuItem() {
        Theme.OverrideWallpaperInfo overrideWallpaperInfo;
        if (this.menuItem == null) {
            return;
        }
        Theme.ThemeInfo currentTheme = Theme.getCurrentTheme();
        Theme.ThemeAccent accent = currentTheme.getAccent(false);
        ArrayList<Theme.ThemeAccent> arrayList = currentTheme.themeAccents;
        if (arrayList != null && !arrayList.isEmpty() && accent != null && accent.id >= 100) {
            this.menuItem.showSubItem(2);
            this.menuItem.showSubItem(3);
        } else {
            this.menuItem.hideSubItem(2);
            this.menuItem.hideSubItem(3);
        }
        int i = AndroidUtilities.isTablet() ? 18 : 16;
        Theme.ThemeInfo currentTheme2 = Theme.getCurrentTheme();
        if (SharedConfig.fontSize != i || SharedConfig.bubbleRadius != 10 || !currentTheme2.firstAccentIsDefault || currentTheme2.currentAccentId != Theme.DEFALT_THEME_ACCENT_ID || (accent != null && (overrideWallpaperInfo = accent.overrideWallpaper) != null && !"d".equals(overrideWallpaperInfo.slug))) {
            this.menuItem.showSubItem(4);
        } else {
            this.menuItem.hideSubItem(4);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateSunTime(Location location, boolean z) {
        Activity parentActivity;
        LocationManager locationManager = (LocationManager) ApplicationLoader.applicationContext.getSystemService("location");
        if (Build.VERSION.SDK_INT >= 23 && (parentActivity = getParentActivity()) != null && parentActivity.checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION") != 0) {
            parentActivity.requestPermissions(new String[]{"android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"}, 2);
            return;
        }
        if (getParentActivity() != null) {
            if (!getParentActivity().getPackageManager().hasSystemFeature("android.hardware.location.gps")) {
                return;
            }
            try {
                if (!((LocationManager) ApplicationLoader.applicationContext.getSystemService("location")).isProviderEnabled("gps")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
                    builder.setTopAnimation(R.raw.permission_request_location, 72, false, Theme.getColor("dialogTopBackground"));
                    builder.setMessage(LocaleController.getString("GpsDisabledAlertText", R.string.GpsDisabledAlertText));
                    builder.setPositiveButton(LocaleController.getString("ConnectingToProxyEnable", R.string.ConnectingToProxyEnable), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.ThemeActivity$$ExternalSyntheticLambda1
                        @Override // android.content.DialogInterface.OnClickListener
                        public final void onClick(DialogInterface dialogInterface, int i) {
                            ThemeActivity.this.lambda$updateSunTime$9(dialogInterface, i);
                        }
                    });
                    builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                    showDialog(builder.create());
                    return;
                }
            } catch (Exception e) {
                FileLog.e(e);
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
            FileLog.e(e2);
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
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        Theme.autoNightLastSunCheckDay = calendar.get(5);
        Utilities.globalQueue.postRunnable(new Runnable() { // from class: org.telegram.ui.ThemeActivity$$ExternalSyntheticLambda8
            @Override // java.lang.Runnable
            public final void run() {
                ThemeActivity.this.lambda$updateSunTime$11();
            }
        });
        RecyclerListView.Holder holder = (RecyclerListView.Holder) this.listView.findViewHolderForAdapterPosition(this.scheduleLocationInfoRow);
        if (holder != null) {
            View view = holder.itemView;
            if (view instanceof TextInfoPrivacyCell) {
                ((TextInfoPrivacyCell) view).setText(getLocationSunString());
            }
        }
        if (!Theme.autoNightScheduleByLocation || Theme.selectedAutoNightType != 1) {
            return;
        }
        Theme.checkAutoNightThemeConditions();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateSunTime$9(DialogInterface dialogInterface, int i) {
        if (getParentActivity() == null) {
            return;
        }
        try {
            getParentActivity().startActivity(new Intent("android.settings.LOCATION_SOURCE_SETTINGS"));
        } catch (Exception unused) {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateSunTime$11() {
        final String str = null;
        try {
            List<Address> fromLocation = new Geocoder(ApplicationLoader.applicationContext, Locale.getDefault()).getFromLocation(Theme.autoNightLocationLatitude, Theme.autoNightLocationLongitude, 1);
            if (fromLocation.size() > 0) {
                str = fromLocation.get(0).getLocality();
            }
        } catch (Exception unused) {
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ThemeActivity$$ExternalSyntheticLambda9
            @Override // java.lang.Runnable
            public final void run() {
                ThemeActivity.this.lambda$updateSunTime$10(str);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateSunTime$10(String str) {
        RecyclerListView.Holder holder;
        Theme.autoNightCityName = str;
        if (str == null) {
            Theme.autoNightCityName = String.format("(%.06f, %.06f)", Double.valueOf(Theme.autoNightLocationLatitude), Double.valueOf(Theme.autoNightLocationLongitude));
        }
        Theme.saveAutoNightThemeConfig();
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView == null || (holder = (RecyclerListView.Holder) recyclerListView.findViewHolderForAdapterPosition(this.scheduleUpdateLocationRow)) == null) {
            return;
        }
        View view = holder.itemView;
        if (!(view instanceof TextSettingsCell)) {
            return;
        }
        ((TextSettingsCell) view).setTextAndValue(LocaleController.getString("AutoNightUpdateLocation", R.string.AutoNightUpdateLocation), Theme.autoNightCityName, false);
    }

    private void startLocationUpdate() {
        if (this.updatingLocation) {
            return;
        }
        this.updatingLocation = true;
        LocationManager locationManager = (LocationManager) ApplicationLoader.applicationContext.getSystemService("location");
        try {
            locationManager.requestLocationUpdates("gps", 1L, 0.0f, this.gpsLocationListener);
        } catch (Exception e) {
            FileLog.e(e);
        }
        try {
            locationManager.requestLocationUpdates("network", 1L, 0.0f, this.networkLocationListener);
        } catch (Exception e2) {
            FileLog.e(e2);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void stopLocationUpdate() {
        this.updatingLocation = false;
        LocationManager locationManager = (LocationManager) ApplicationLoader.applicationContext.getSystemService("location");
        locationManager.removeUpdates(this.gpsLocationListener);
        locationManager.removeUpdates(this.networkLocationListener);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String getLocationSunString() {
        int i = Theme.autoNightSunriseTime;
        int i2 = i / 60;
        String format = String.format("%02d:%02d", Integer.valueOf(i2), Integer.valueOf(i - (i2 * 60)));
        int i3 = Theme.autoNightSunsetTime;
        int i4 = i3 / 60;
        return LocaleController.formatString("AutoNightUpdateLocationInfo", R.string.AutoNightUpdateLocationInfo, String.format("%02d:%02d", Integer.valueOf(i4), Integer.valueOf(i3 - (i4 * 60))), format);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public static class InnerAccentView extends View {
        private ObjectAnimator checkAnimator;
        private boolean checked;
        private float checkedState;
        private Theme.ThemeAccent currentAccent;
        private Theme.ThemeInfo currentTheme;
        private final Paint paint;

        InnerAccentView(Context context) {
            super(context);
            this.paint = new Paint(1);
        }

        void setThemeAndColor(Theme.ThemeInfo themeInfo, Theme.ThemeAccent themeAccent) {
            this.currentTheme = themeInfo;
            this.currentAccent = themeAccent;
            updateCheckedState(false);
        }

        void updateCheckedState(boolean z) {
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
                ofFloat.setDuration(200L);
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

        @Override // android.view.View
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            updateCheckedState(false);
        }

        @Override // android.view.View
        protected void onMeasure(int i, int i2) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(62.0f), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(62.0f), NUM));
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            float dp = AndroidUtilities.dp(20.0f);
            float measuredWidth = getMeasuredWidth() * 0.5f;
            float measuredHeight = getMeasuredHeight() * 0.5f;
            this.paint.setColor(this.currentAccent.accentColor);
            this.paint.setStyle(Paint.Style.STROKE);
            this.paint.setStrokeWidth(AndroidUtilities.dp(3.0f));
            this.paint.setAlpha(Math.round(this.checkedState * 255.0f));
            canvas.drawCircle(measuredWidth, measuredHeight, dp - (this.paint.getStrokeWidth() * 0.5f), this.paint);
            this.paint.setAlpha(255);
            this.paint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(measuredWidth, measuredHeight, dp - (AndroidUtilities.dp(5.0f) * this.checkedState), this.paint);
            if (this.checkedState != 0.0f) {
                this.paint.setColor(-1);
                this.paint.setAlpha(Math.round(this.checkedState * 255.0f));
                canvas.drawCircle(measuredWidth, measuredHeight, AndroidUtilities.dp(2.0f), this.paint);
                canvas.drawCircle(measuredWidth - (AndroidUtilities.dp(7.0f) * this.checkedState), measuredHeight, AndroidUtilities.dp(2.0f), this.paint);
                canvas.drawCircle((AndroidUtilities.dp(7.0f) * this.checkedState) + measuredWidth, measuredHeight, AndroidUtilities.dp(2.0f), this.paint);
            }
            int i = this.currentAccent.myMessagesAccentColor;
            if (i == 0 || this.checkedState == 1.0f) {
                return;
            }
            this.paint.setColor(i);
            canvas.drawCircle(measuredWidth, measuredHeight, AndroidUtilities.dp(8.0f) * (1.0f - this.checkedState), this.paint);
        }

        @Override // android.view.View
        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
            super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
            accessibilityNodeInfo.setText(LocaleController.getString("ColorPickerMainColor", R.string.ColorPickerMainColor));
            accessibilityNodeInfo.setClassName(Button.class.getName());
            accessibilityNodeInfo.setChecked(this.checked);
            accessibilityNodeInfo.setCheckable(true);
            accessibilityNodeInfo.setEnabled(true);
        }
    }

    /* loaded from: classes3.dex */
    private static class InnerCustomAccentView extends View {
        private int[] colors;
        private final Paint paint;

        InnerCustomAccentView(Context context) {
            super(context);
            this.paint = new Paint(1);
            this.colors = new int[7];
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setTheme(Theme.ThemeInfo themeInfo) {
            if (themeInfo.defaultAccentCount >= 8) {
                this.colors = new int[]{themeInfo.getAccentColor(6), themeInfo.getAccentColor(4), themeInfo.getAccentColor(7), themeInfo.getAccentColor(2), themeInfo.getAccentColor(0), themeInfo.getAccentColor(5), themeInfo.getAccentColor(3)};
            } else {
                this.colors = new int[7];
            }
        }

        @Override // android.view.View
        protected void onMeasure(int i, int i2) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(62.0f), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(62.0f), NUM));
        }

        @Override // android.view.View
        protected void onDraw(Canvas canvas) {
            float measuredWidth = getMeasuredWidth() * 0.5f;
            float measuredHeight = getMeasuredHeight() * 0.5f;
            float dp = AndroidUtilities.dp(5.0f);
            float dp2 = AndroidUtilities.dp(20.0f) - dp;
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

        @Override // android.view.View
        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
            super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
            accessibilityNodeInfo.setText(LocaleController.getString("ColorPickerMainColor", R.string.ColorPickerMainColor));
            accessibilityNodeInfo.setClassName(Button.class.getName());
            accessibilityNodeInfo.setEnabled(true);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public class ThemeAccentsListAdapter extends RecyclerListView.SelectionAdapter {
        private Theme.ThemeInfo currentTheme;
        private Context mContext;
        private ArrayList<Theme.ThemeAccent> themeAccents;

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return false;
        }

        ThemeAccentsListAdapter(Context context) {
            this.mContext = context;
            notifyDataSetChanged();
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void notifyDataSetChanged() {
            this.currentTheme = ThemeActivity.this.currentType == 1 ? Theme.getCurrentNightTheme() : Theme.getCurrentTheme();
            this.themeAccents = new ArrayList<>(this.currentTheme.themeAccents);
            super.notifyDataSetChanged();
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int i) {
            return i == getItemCount() - 1 ? 1 : 0;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        /* renamed from: onCreateViewHolder */
        public RecyclerView.ViewHolder mo1813onCreateViewHolder(ViewGroup viewGroup, int i) {
            if (i == 0) {
                return new RecyclerListView.Holder(new InnerAccentView(this.mContext));
            }
            return new RecyclerListView.Holder(new InnerCustomAccentView(this.mContext));
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            int itemViewType = getItemViewType(i);
            if (itemViewType == 0) {
                ((InnerAccentView) viewHolder.itemView).setThemeAndColor(this.currentTheme, this.themeAccents.get(i));
            } else if (itemViewType != 1) {
            } else {
                ((InnerCustomAccentView) viewHolder.itemView).setTheme(this.currentTheme);
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            if (this.themeAccents.isEmpty()) {
                return 0;
            }
            return this.themeAccents.size() + 1;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public int findCurrentAccent() {
            return this.themeAccents.indexOf(this.currentTheme.getAccent(false));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public class ListAdapter extends RecyclerListView.SelectionAdapter {
        private boolean first = true;
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            return ThemeActivity.this.rowCount;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            int itemViewType = viewHolder.getItemViewType();
            return itemViewType == 0 || itemViewType == 1 || itemViewType == 4 || itemViewType == 7 || itemViewType == 10 || itemViewType == 11 || itemViewType == 12 || itemViewType == 14 || itemViewType == 18 || itemViewType == 20;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void showOptionsForTheme(final Theme.ThemeInfo themeInfo) {
            int[] iArr;
            CharSequence[] charSequenceArr;
            if (ThemeActivity.this.getParentActivity() != null) {
                if ((themeInfo.info != null && !themeInfo.themeLoaded) || ThemeActivity.this.currentType == 1) {
                    return;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(ThemeActivity.this.getParentActivity());
                String str = null;
                boolean z = false;
                if (themeInfo.pathToFile == null) {
                    charSequenceArr = new CharSequence[]{null, LocaleController.getString("ExportTheme", R.string.ExportTheme)};
                    iArr = new int[]{0, R.drawable.msg_shareout};
                } else {
                    TLRPC$TL_theme tLRPC$TL_theme = themeInfo.info;
                    boolean z2 = tLRPC$TL_theme == null || !tLRPC$TL_theme.isDefault;
                    CharSequence[] charSequenceArr2 = new CharSequence[5];
                    charSequenceArr2[0] = LocaleController.getString("ShareFile", R.string.ShareFile);
                    charSequenceArr2[1] = LocaleController.getString("ExportTheme", R.string.ExportTheme);
                    TLRPC$TL_theme tLRPC$TL_theme2 = themeInfo.info;
                    charSequenceArr2[2] = (tLRPC$TL_theme2 == null || (!tLRPC$TL_theme2.isDefault && tLRPC$TL_theme2.creator)) ? LocaleController.getString("Edit", R.string.Edit) : null;
                    TLRPC$TL_theme tLRPC$TL_theme3 = themeInfo.info;
                    charSequenceArr2[3] = (tLRPC$TL_theme3 == null || !tLRPC$TL_theme3.creator) ? null : LocaleController.getString("ThemeSetUrl", R.string.ThemeSetUrl);
                    if (z2) {
                        str = LocaleController.getString("Delete", R.string.Delete);
                    }
                    charSequenceArr2[4] = str;
                    z = z2;
                    iArr = new int[]{R.drawable.msg_share, R.drawable.msg_shareout, R.drawable.msg_edit, R.drawable.msg_link, R.drawable.msg_delete};
                    charSequenceArr = charSequenceArr2;
                }
                builder.setItems(charSequenceArr, iArr, new DialogInterface.OnClickListener() { // from class: org.telegram.ui.ThemeActivity$ListAdapter$$ExternalSyntheticLambda2
                    @Override // android.content.DialogInterface.OnClickListener
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        ThemeActivity.ListAdapter.this.lambda$showOptionsForTheme$1(themeInfo, dialogInterface, i);
                    }
                });
                AlertDialog create = builder.create();
                ThemeActivity.this.showDialog(create);
                if (!z) {
                    return;
                }
                create.setItemColor(create.getItemsCount() - 1, Theme.getColor("dialogTextRed2"), Theme.getColor("dialogRedIcon"));
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* JADX WARN: Removed duplicated region for block: B:50:0x0106  */
        /* JADX WARN: Removed duplicated region for block: B:54:0x0129 A[RETURN] */
        /* JADX WARN: Removed duplicated region for block: B:55:0x012a A[Catch: Exception -> 0x0188, TRY_LEAVE, TryCatch #3 {Exception -> 0x0188, blocks: (B:52:0x0123, B:55:0x012a, B:62:0x0173, B:61:0x016c, B:60:0x0164, B:58:0x013e), top: B:86:0x0123, inners: #6 }] */
        /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:37:0x00dd -> B:80:0x00fc). Please submit an issue!!! */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public /* synthetic */ void lambda$showOptionsForTheme$1(final org.telegram.ui.ActionBar.Theme.ThemeInfo r8, android.content.DialogInterface r9, int r10) {
            /*
                Method dump skipped, instructions count: 546
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ThemeActivity.ListAdapter.lambda$showOptionsForTheme$1(org.telegram.ui.ActionBar.Theme$ThemeInfo, android.content.DialogInterface, int):void");
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$showOptionsForTheme$0(Theme.ThemeInfo themeInfo, DialogInterface dialogInterface, int i) {
            MessagesController.getInstance(themeInfo.account).saveTheme(themeInfo, null, themeInfo == Theme.getCurrentNightTheme(), true);
            if (Theme.deleteTheme(themeInfo)) {
                ((BaseFragment) ThemeActivity.this).parentLayout.rebuildAllFragmentViews(true, true);
            }
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.themeListUpdated, new Object[0]);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onCreateViewHolder$2(ThemeAccentsListAdapter themeAccentsListAdapter, RecyclerListView recyclerListView, View view, int i) {
            Theme.ThemeInfo currentNightTheme = ThemeActivity.this.currentType == 1 ? Theme.getCurrentNightTheme() : Theme.getCurrentTheme();
            if (i != themeAccentsListAdapter.getItemCount() - 1) {
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
                    ThemeActivity themeActivity = ThemeActivity.this;
                    themeActivity.presentFragment(new ThemePreviewActivity(currentNightTheme, false, 1, i3 >= 100, themeActivity.currentType == 1));
                }
            } else {
                ThemeActivity themeActivity2 = ThemeActivity.this;
                themeActivity2.presentFragment(new ThemePreviewActivity(currentNightTheme, false, 1, false, themeActivity2.currentType == 1));
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

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ boolean lambda$onCreateViewHolder$5(final ThemeAccentsListAdapter themeAccentsListAdapter, View view, int i) {
            if (i >= 0 && i < themeAccentsListAdapter.themeAccents.size()) {
                final Theme.ThemeAccent themeAccent = (Theme.ThemeAccent) themeAccentsListAdapter.themeAccents.get(i);
                if (themeAccent.id >= 100 && !themeAccent.isDefault) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ThemeActivity.this.getParentActivity());
                    CharSequence[] charSequenceArr = new CharSequence[4];
                    charSequenceArr[0] = LocaleController.getString("OpenInEditor", R.string.OpenInEditor);
                    charSequenceArr[1] = LocaleController.getString("ShareTheme", R.string.ShareTheme);
                    TLRPC$TL_theme tLRPC$TL_theme = themeAccent.info;
                    charSequenceArr[2] = (tLRPC$TL_theme == null || !tLRPC$TL_theme.creator) ? null : LocaleController.getString("ThemeSetUrl", R.string.ThemeSetUrl);
                    charSequenceArr[3] = LocaleController.getString("DeleteTheme", R.string.DeleteTheme);
                    builder.setItems(charSequenceArr, new int[]{R.drawable.msg_edit, R.drawable.msg_share, R.drawable.msg_link, R.drawable.msg_delete}, new DialogInterface.OnClickListener() { // from class: org.telegram.ui.ThemeActivity$ListAdapter$$ExternalSyntheticLambda0
                        @Override // android.content.DialogInterface.OnClickListener
                        public final void onClick(DialogInterface dialogInterface, int i2) {
                            ThemeActivity.ListAdapter.this.lambda$onCreateViewHolder$4(themeAccent, themeAccentsListAdapter, dialogInterface, i2);
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

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onCreateViewHolder$4(final Theme.ThemeAccent themeAccent, final ThemeAccentsListAdapter themeAccentsListAdapter, DialogInterface dialogInterface, int i) {
            if (ThemeActivity.this.getParentActivity() == null) {
                return;
            }
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
                ThemeActivity.this.showDialog(new ShareAlert(ThemeActivity.this.getParentActivity(), null, str, false, str, false));
            } else if (i == 2) {
                ThemeActivity.this.presentFragment(new ThemeSetUrlActivity(themeAccent.parentTheme, themeAccent, false));
            } else if (i != 3 || ThemeActivity.this.getParentActivity() == null) {
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(ThemeActivity.this.getParentActivity());
                builder.setTitle(LocaleController.getString("DeleteThemeTitle", R.string.DeleteThemeTitle));
                builder.setMessage(LocaleController.getString("DeleteThemeAlert", R.string.DeleteThemeAlert));
                builder.setPositiveButton(LocaleController.getString("Delete", R.string.Delete), new DialogInterface.OnClickListener() { // from class: org.telegram.ui.ThemeActivity$ListAdapter$$ExternalSyntheticLambda3
                    @Override // android.content.DialogInterface.OnClickListener
                    public final void onClick(DialogInterface dialogInterface2, int i3) {
                        ThemeActivity.ListAdapter.this.lambda$onCreateViewHolder$3(themeAccentsListAdapter, themeAccent, dialogInterface2, i3);
                    }
                });
                builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                AlertDialog create = builder.create();
                ThemeActivity.this.showDialog(create);
                TextView textView = (TextView) create.getButton(-1);
                if (textView == null) {
                    return;
                }
                textView.setTextColor(Theme.getColor("dialogTextRed2"));
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
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

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        /* renamed from: onCreateViewHolder */
        public RecyclerView.ViewHolder mo1813onCreateViewHolder(ViewGroup viewGroup, int i) {
            DefaultThemesPreviewCell defaultThemesPreviewCell;
            ThemePreviewMessagesCell themePreviewMessagesCell;
            switch (i) {
                case 1:
                    View textSettingsCell = new TextSettingsCell(this.mContext);
                    textSettingsCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    themePreviewMessagesCell = textSettingsCell;
                    break;
                case 2:
                    View textInfoPrivacyCell = new TextInfoPrivacyCell(this.mContext);
                    textInfoPrivacyCell.setBackground(Theme.getThemedDrawable(this.mContext, R.drawable.greydivider, "windowBackgroundGrayShadow"));
                    themePreviewMessagesCell = textInfoPrivacyCell;
                    break;
                case 3:
                    themePreviewMessagesCell = new ShadowSectionCell(this.mContext);
                    break;
                case 4:
                    View themeTypeCell = new ThemeTypeCell(this.mContext);
                    themeTypeCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    themePreviewMessagesCell = themeTypeCell;
                    break;
                case 5:
                    View headerCell = new HeaderCell(this.mContext);
                    headerCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    themePreviewMessagesCell = headerCell;
                    break;
                case 6:
                    View view = new BrightnessControlCell(this.mContext) { // from class: org.telegram.ui.ThemeActivity.ListAdapter.1
                        @Override // org.telegram.ui.Cells.BrightnessControlCell
                        protected void didChangedValue(float f) {
                            int i2 = (int) (Theme.autoNightBrighnessThreshold * 100.0f);
                            int i3 = (int) (f * 100.0f);
                            Theme.autoNightBrighnessThreshold = f;
                            if (i2 != i3) {
                                RecyclerListView.Holder holder = (RecyclerListView.Holder) ThemeActivity.this.listView.findViewHolderForAdapterPosition(ThemeActivity.this.automaticBrightnessInfoRow);
                                if (holder != null) {
                                    ((TextInfoPrivacyCell) holder.itemView).setText(LocaleController.formatString("AutoNightBrightnessInfo", R.string.AutoNightBrightnessInfo, Integer.valueOf((int) (Theme.autoNightBrighnessThreshold * 100.0f))));
                                }
                                Theme.checkAutoNightThemeConditions(true);
                            }
                        }
                    };
                    view.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    themePreviewMessagesCell = view;
                    break;
                case 7:
                    View textCheckCell = new TextCheckCell(this.mContext);
                    textCheckCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    themePreviewMessagesCell = textCheckCell;
                    break;
                case 8:
                    View textSizeCell = new TextSizeCell(this.mContext);
                    textSizeCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    themePreviewMessagesCell = textSizeCell;
                    break;
                case 9:
                    View view2 = new ChatListCell(this, this.mContext) { // from class: org.telegram.ui.ThemeActivity.ListAdapter.2
                        @Override // org.telegram.ui.Cells.ChatListCell
                        protected void didSelectChatType(boolean z) {
                            SharedConfig.setUseThreeLinesLayout(z);
                        }
                    };
                    view2.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    themePreviewMessagesCell = view2;
                    break;
                case 10:
                    View notificationsCheckCell = new NotificationsCheckCell(this.mContext, 21, 64, false);
                    notificationsCheckCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    themePreviewMessagesCell = notificationsCheckCell;
                    break;
                case 11:
                    this.first = true;
                    ThemeActivity.this.themesHorizontalListCell = new ThemesHorizontalListCell(this.mContext, ThemeActivity.this.currentType, ThemeActivity.this.defaultThemes, ThemeActivity.this.darkThemes) { // from class: org.telegram.ui.ThemeActivity.ListAdapter.3
                        @Override // org.telegram.ui.Cells.ThemesHorizontalListCell
                        protected void showOptionsForTheme(Theme.ThemeInfo themeInfo) {
                            ThemeActivity.this.listAdapter.showOptionsForTheme(themeInfo);
                        }

                        @Override // org.telegram.ui.Cells.ThemesHorizontalListCell
                        protected void presentFragment(BaseFragment baseFragment) {
                            ThemeActivity.this.presentFragment(baseFragment);
                        }

                        @Override // org.telegram.ui.Cells.ThemesHorizontalListCell
                        protected void updateRows() {
                            ThemeActivity.this.updateRows(false);
                        }
                    };
                    ThemeActivity.this.themesHorizontalListCell.setDrawDivider(ThemeActivity.this.hasThemeAccents);
                    ThemeActivity.this.themesHorizontalListCell.setFocusable(false);
                    View view3 = ThemeActivity.this.themesHorizontalListCell;
                    view3.setLayoutParams(new RecyclerView.LayoutParams(-1, AndroidUtilities.dp(148.0f)));
                    defaultThemesPreviewCell = view3;
                    themePreviewMessagesCell = defaultThemesPreviewCell;
                    break;
                case 12:
                    final TintRecyclerListView tintRecyclerListView = new TintRecyclerListView(this, this.mContext) { // from class: org.telegram.ui.ThemeActivity.ListAdapter.4
                        @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.ViewGroup
                        public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                            if (getParent() != null && getParent().getParent() != null) {
                                getParent().getParent().requestDisallowInterceptTouchEvent(canScrollHorizontally(-1));
                            }
                            return super.onInterceptTouchEvent(motionEvent);
                        }
                    };
                    tintRecyclerListView.setFocusable(false);
                    tintRecyclerListView.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    tintRecyclerListView.setItemAnimator(null);
                    tintRecyclerListView.setLayoutAnimation(null);
                    tintRecyclerListView.setPadding(AndroidUtilities.dp(11.0f), 0, AndroidUtilities.dp(11.0f), 0);
                    tintRecyclerListView.setClipToPadding(false);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.mContext);
                    linearLayoutManager.setOrientation(0);
                    tintRecyclerListView.setLayoutManager(linearLayoutManager);
                    final ThemeAccentsListAdapter themeAccentsListAdapter = new ThemeAccentsListAdapter(this.mContext);
                    tintRecyclerListView.setAdapter(themeAccentsListAdapter);
                    tintRecyclerListView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() { // from class: org.telegram.ui.ThemeActivity$ListAdapter$$ExternalSyntheticLambda4
                        @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
                        public final void onItemClick(View view4, int i2) {
                            ThemeActivity.ListAdapter.this.lambda$onCreateViewHolder$2(themeAccentsListAdapter, tintRecyclerListView, view4, i2);
                        }
                    });
                    tintRecyclerListView.setOnItemLongClickListener(new RecyclerListView.OnItemLongClickListener() { // from class: org.telegram.ui.ThemeActivity$ListAdapter$$ExternalSyntheticLambda5
                        @Override // org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener
                        public final boolean onItemClick(View view4, int i2) {
                            boolean lambda$onCreateViewHolder$5;
                            lambda$onCreateViewHolder$5 = ThemeActivity.ListAdapter.this.lambda$onCreateViewHolder$5(themeAccentsListAdapter, view4, i2);
                            return lambda$onCreateViewHolder$5;
                        }
                    });
                    tintRecyclerListView.setLayoutParams(new RecyclerView.LayoutParams(-1, AndroidUtilities.dp(62.0f)));
                    defaultThemesPreviewCell = tintRecyclerListView;
                    themePreviewMessagesCell = defaultThemesPreviewCell;
                    break;
                case 13:
                    View bubbleRadiusCell = new BubbleRadiusCell(this.mContext);
                    bubbleRadiusCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    themePreviewMessagesCell = bubbleRadiusCell;
                    break;
                case 14:
                case 18:
                default:
                    View textCell = new TextCell(this.mContext);
                    textCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    themePreviewMessagesCell = textCell;
                    break;
                case 15:
                    themePreviewMessagesCell = new SwipeGestureSettingsView(this.mContext, ((BaseFragment) ThemeActivity.this).currentAccount);
                    break;
                case 16:
                    ThemePreviewMessagesCell themePreviewMessagesCell2 = new ThemePreviewMessagesCell(this.mContext, ((BaseFragment) ThemeActivity.this).parentLayout, 0);
                    themePreviewMessagesCell = themePreviewMessagesCell2;
                    if (Build.VERSION.SDK_INT >= 19) {
                        themePreviewMessagesCell2.setImportantForAccessibility(4);
                        themePreviewMessagesCell = themePreviewMessagesCell2;
                        break;
                    }
                    break;
                case 17:
                    Context context = this.mContext;
                    ThemeActivity themeActivity = ThemeActivity.this;
                    DefaultThemesPreviewCell defaultThemesPreviewCell2 = new DefaultThemesPreviewCell(context, themeActivity, themeActivity.currentType);
                    defaultThemesPreviewCell2.setFocusable(false);
                    defaultThemesPreviewCell2.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
                    defaultThemesPreviewCell = defaultThemesPreviewCell2;
                    themePreviewMessagesCell = defaultThemesPreviewCell;
                    break;
                case 19:
                    themePreviewMessagesCell = new RadioButtonCell(this.mContext);
                    break;
                case 20:
                    Context context2 = this.mContext;
                    ThemeActivity themeActivity2 = ThemeActivity.this;
                    themePreviewMessagesCell = new AppIconsSelectorCell(context2, themeActivity2, ((BaseFragment) themeActivity2).currentAccount);
                    break;
            }
            return new RecyclerListView.Holder(themePreviewMessagesCell);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            String string;
            String string2;
            String string3;
            boolean z = false;
            boolean z2 = true;
            switch (viewHolder.getItemViewType()) {
                case 1:
                    TextSettingsCell textSettingsCell = (TextSettingsCell) viewHolder.itemView;
                    if (i != ThemeActivity.this.nightThemeRow) {
                        if (i != ThemeActivity.this.scheduleFromRow) {
                            if (i != ThemeActivity.this.scheduleToRow) {
                                if (i != ThemeActivity.this.scheduleUpdateLocationRow) {
                                    if (i != ThemeActivity.this.contactsSortRow) {
                                        if (i != ThemeActivity.this.contactsReimportRow) {
                                            if (i != ThemeActivity.this.distanceRow) {
                                                if (i != ThemeActivity.this.bluetoothScoRow) {
                                                    return;
                                                }
                                                textSettingsCell.setTextAndValue(LocaleController.getString(R.string.MicrophoneForVoiceMessages), LocaleController.getString(SharedConfig.recordViaSco ? R.string.MicrophoneForVoiceMessagesSco : R.string.MicrophoneForVoiceMessagesBuiltIn), ThemeActivity.this.updateRecordViaSco, true);
                                                ThemeActivity.this.updateRecordViaSco = false;
                                                return;
                                            }
                                            int i2 = SharedConfig.distanceSystemType;
                                            if (i2 == 0) {
                                                string = LocaleController.getString("DistanceUnitsAutomatic", R.string.DistanceUnitsAutomatic);
                                            } else if (i2 == 1) {
                                                string = LocaleController.getString("DistanceUnitsKilometers", R.string.DistanceUnitsKilometers);
                                            } else {
                                                string = LocaleController.getString("DistanceUnitsMiles", R.string.DistanceUnitsMiles);
                                            }
                                            textSettingsCell.setTextAndValue(LocaleController.getString("DistanceUnits", R.string.DistanceUnits), string, ThemeActivity.this.updateDistance, false);
                                            ThemeActivity.this.updateDistance = false;
                                            return;
                                        }
                                        textSettingsCell.setText(LocaleController.getString("ImportContacts", R.string.ImportContacts), true);
                                        return;
                                    }
                                    int i3 = MessagesController.getGlobalMainSettings().getInt("sortContactsBy", 0);
                                    if (i3 == 0) {
                                        string2 = LocaleController.getString("Default", R.string.Default);
                                    } else if (i3 == 1) {
                                        string2 = LocaleController.getString("FirstName", R.string.SortFirstName);
                                    } else {
                                        string2 = LocaleController.getString("LastName", R.string.SortLastName);
                                    }
                                    textSettingsCell.setTextAndValue(LocaleController.getString("SortBy", R.string.SortBy), string2, true);
                                    return;
                                }
                                textSettingsCell.setTextAndValue(LocaleController.getString("AutoNightUpdateLocation", R.string.AutoNightUpdateLocation), Theme.autoNightCityName, false);
                                return;
                            }
                            int i4 = Theme.autoNightDayEndTime;
                            int i5 = i4 / 60;
                            textSettingsCell.setTextAndValue(LocaleController.getString("AutoNightTo", R.string.AutoNightTo), String.format("%02d:%02d", Integer.valueOf(i5), Integer.valueOf(i4 - (i5 * 60))), false);
                            return;
                        }
                        int i6 = Theme.autoNightDayStartTime;
                        int i7 = i6 / 60;
                        textSettingsCell.setTextAndValue(LocaleController.getString("AutoNightFrom", R.string.AutoNightFrom), String.format("%02d:%02d", Integer.valueOf(i7), Integer.valueOf(i6 - (i7 * 60))), true);
                        return;
                    } else if (Theme.selectedAutoNightType == 0 || Theme.getCurrentNightTheme() == null) {
                        textSettingsCell.setTextAndValue(LocaleController.getString("AutoNightTheme", R.string.AutoNightTheme), LocaleController.getString("AutoNightThemeOff", R.string.AutoNightThemeOff), false);
                        return;
                    } else {
                        textSettingsCell.setTextAndValue(LocaleController.getString("AutoNightTheme", R.string.AutoNightTheme), Theme.getCurrentNightThemeName(), false);
                        return;
                    }
                case 2:
                    TextInfoPrivacyCell textInfoPrivacyCell = (TextInfoPrivacyCell) viewHolder.itemView;
                    if (i == ThemeActivity.this.automaticBrightnessInfoRow) {
                        textInfoPrivacyCell.setText(LocaleController.formatString("AutoNightBrightnessInfo", R.string.AutoNightBrightnessInfo, Integer.valueOf((int) (Theme.autoNightBrighnessThreshold * 100.0f))));
                        return;
                    } else if (i != ThemeActivity.this.scheduleLocationInfoRow) {
                        return;
                    } else {
                        textInfoPrivacyCell.setText(ThemeActivity.this.getLocationSunString());
                        return;
                    }
                case 3:
                    if ((i == ThemeActivity.this.nightTypeInfoRow && ThemeActivity.this.themeInfoRow == -1) || ((i == ThemeActivity.this.themeInfoRow && ThemeActivity.this.nightTypeInfoRow != -1) || i == ThemeActivity.this.saveToGallerySectionRow)) {
                        viewHolder.itemView.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, R.drawable.greydivider_bottom, "windowBackgroundGrayShadow"));
                        return;
                    } else {
                        viewHolder.itemView.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, R.drawable.greydivider, "windowBackgroundGrayShadow"));
                        return;
                    }
                case 4:
                    ThemeTypeCell themeTypeCell = (ThemeTypeCell) viewHolder.itemView;
                    if (i != ThemeActivity.this.nightDisabledRow) {
                        if (i != ThemeActivity.this.nightScheduledRow) {
                            if (i != ThemeActivity.this.nightAutomaticRow) {
                                if (i != ThemeActivity.this.nightSystemDefaultRow) {
                                    return;
                                }
                                String string4 = LocaleController.getString("AutoNightSystemDefault", R.string.AutoNightSystemDefault);
                                if (Theme.selectedAutoNightType != 3) {
                                    z2 = false;
                                }
                                themeTypeCell.setValue(string4, z2, false);
                                return;
                            }
                            String string5 = LocaleController.getString("AutoNightAdaptive", R.string.AutoNightAdaptive);
                            boolean z3 = Theme.selectedAutoNightType == 2;
                            if (ThemeActivity.this.nightSystemDefaultRow != -1) {
                                z = true;
                            }
                            themeTypeCell.setValue(string5, z3, z);
                            return;
                        }
                        String string6 = LocaleController.getString("AutoNightScheduled", R.string.AutoNightScheduled);
                        if (Theme.selectedAutoNightType == 1) {
                            z = true;
                        }
                        themeTypeCell.setValue(string6, z, true);
                        return;
                    }
                    String string7 = LocaleController.getString("AutoNightDisabled", R.string.AutoNightDisabled);
                    if (Theme.selectedAutoNightType == 0) {
                        z = true;
                    }
                    themeTypeCell.setValue(string7, z, true);
                    return;
                case 5:
                    HeaderCell headerCell = (HeaderCell) viewHolder.itemView;
                    if (i != ThemeActivity.this.scheduleHeaderRow) {
                        if (i != ThemeActivity.this.automaticHeaderRow) {
                            if (i != ThemeActivity.this.preferedHeaderRow) {
                                if (i != ThemeActivity.this.settingsRow) {
                                    if (i == ThemeActivity.this.themeHeaderRow) {
                                        if (ThemeActivity.this.currentType == 3) {
                                            headerCell.setText(LocaleController.getString("BuildMyOwnTheme", R.string.BuildMyOwnTheme));
                                            return;
                                        } else {
                                            headerCell.setText(LocaleController.getString("ColorTheme", R.string.ColorTheme));
                                            return;
                                        }
                                    } else if (i != ThemeActivity.this.textSizeHeaderRow) {
                                        if (i != ThemeActivity.this.chatListHeaderRow) {
                                            if (i != ThemeActivity.this.bubbleRadiusHeaderRow) {
                                                if (i != ThemeActivity.this.swipeGestureHeaderRow) {
                                                    if (i != ThemeActivity.this.selectThemeHeaderRow) {
                                                        if (i != ThemeActivity.this.appIconHeaderRow) {
                                                            return;
                                                        }
                                                        headerCell.setText(LocaleController.getString(R.string.AppIcon));
                                                        return;
                                                    }
                                                    headerCell.setText(LocaleController.getString("SelectTheme", R.string.SelectTheme));
                                                    return;
                                                }
                                                headerCell.setText(LocaleController.getString("ChatListSwipeGesture", R.string.ChatListSwipeGesture));
                                                return;
                                            }
                                            headerCell.setText(LocaleController.getString("BubbleRadius", R.string.BubbleRadius));
                                            return;
                                        }
                                        headerCell.setText(LocaleController.getString("ChatList", R.string.ChatList));
                                        return;
                                    } else {
                                        headerCell.setText(LocaleController.getString("TextSizeHeader", R.string.TextSizeHeader));
                                        return;
                                    }
                                }
                                headerCell.setText(LocaleController.getString("SETTINGS", R.string.SETTINGS));
                                return;
                            }
                            headerCell.setText(LocaleController.getString("AutoNightPreferred", R.string.AutoNightPreferred));
                            return;
                        }
                        headerCell.setText(LocaleController.getString("AutoNightBrightness", R.string.AutoNightBrightness));
                        return;
                    }
                    headerCell.setText(LocaleController.getString("AutoNightSchedule", R.string.AutoNightSchedule));
                    return;
                case 6:
                    ((BrightnessControlCell) viewHolder.itemView).setProgress(Theme.autoNightBrighnessThreshold);
                    return;
                case 7:
                    TextCheckCell textCheckCell = (TextCheckCell) viewHolder.itemView;
                    if (i != ThemeActivity.this.scheduleLocationRow) {
                        if (i != ThemeActivity.this.enableAnimationsRow) {
                            if (i != ThemeActivity.this.sendByEnterRow) {
                                if (i != ThemeActivity.this.raiseToSpeakRow) {
                                    if (i != ThemeActivity.this.customTabsRow) {
                                        if (i != ThemeActivity.this.directShareRow) {
                                            if (i != ThemeActivity.this.chatBlurRow) {
                                                return;
                                            }
                                            textCheckCell.setTextAndCheck(LocaleController.getString("BlurInChat", R.string.BlurInChat), SharedConfig.chatBlurEnabled(), true);
                                            return;
                                        }
                                        textCheckCell.setTextAndValueAndCheck(LocaleController.getString("DirectShare", R.string.DirectShare), LocaleController.getString("DirectShareInfo", R.string.DirectShareInfo), SharedConfig.directShare, false, true);
                                        return;
                                    }
                                    textCheckCell.setTextAndValueAndCheck(LocaleController.getString("ChromeCustomTabs", R.string.ChromeCustomTabs), LocaleController.getString("ChromeCustomTabsInfo", R.string.ChromeCustomTabsInfo), SharedConfig.customTabs, false, true);
                                    return;
                                }
                                textCheckCell.setTextAndCheck(LocaleController.getString("RaiseToSpeak", R.string.RaiseToSpeak), SharedConfig.raiseToSpeak, true);
                                return;
                            }
                            textCheckCell.setTextAndCheck(LocaleController.getString("SendByEnter", R.string.SendByEnter), MessagesController.getGlobalMainSettings().getBoolean("send_by_enter", false), true);
                            return;
                        }
                        textCheckCell.setTextAndCheck(LocaleController.getString("EnableAnimations", R.string.EnableAnimations), MessagesController.getGlobalMainSettings().getBoolean("view_animations", true), true);
                        return;
                    }
                    textCheckCell.setTextAndCheck(LocaleController.getString("AutoNightLocation", R.string.AutoNightLocation), Theme.autoNightScheduleByLocation, true);
                    return;
                case 8:
                case 9:
                case 13:
                case 15:
                case 16:
                case 18:
                default:
                    return;
                case 10:
                    NotificationsCheckCell notificationsCheckCell = (NotificationsCheckCell) viewHolder.itemView;
                    if (i != ThemeActivity.this.nightThemeRow) {
                        return;
                    }
                    if (Theme.selectedAutoNightType != 0) {
                        z = true;
                    }
                    String currentNightThemeName = z ? Theme.getCurrentNightThemeName() : LocaleController.getString("AutoNightThemeOff", R.string.AutoNightThemeOff);
                    if (z) {
                        int i8 = Theme.selectedAutoNightType;
                        if (i8 == 1) {
                            string3 = LocaleController.getString("AutoNightScheduled", R.string.AutoNightScheduled);
                        } else if (i8 == 3) {
                            string3 = LocaleController.getString("AutoNightSystemDefault", R.string.AutoNightSystemDefault);
                        } else {
                            string3 = LocaleController.getString("AutoNightAdaptive", R.string.AutoNightAdaptive);
                        }
                        currentNightThemeName = string3 + " " + currentNightThemeName;
                    }
                    notificationsCheckCell.setTextAndValueAndCheck(LocaleController.getString("AutoNightTheme", R.string.AutoNightTheme), currentNightThemeName, z, true);
                    return;
                case 11:
                    if (!this.first) {
                        return;
                    }
                    ThemeActivity.this.themesHorizontalListCell.scrollToCurrentTheme(ThemeActivity.this.listView.getMeasuredWidth(), false);
                    this.first = false;
                    return;
                case 12:
                    RecyclerListView recyclerListView = (RecyclerListView) viewHolder.itemView;
                    ThemeAccentsListAdapter themeAccentsListAdapter = (ThemeAccentsListAdapter) recyclerListView.getAdapter();
                    themeAccentsListAdapter.notifyDataSetChanged();
                    int findCurrentAccent = themeAccentsListAdapter.findCurrentAccent();
                    if (findCurrentAccent == -1) {
                        findCurrentAccent = themeAccentsListAdapter.getItemCount() - 1;
                    }
                    if (findCurrentAccent == -1) {
                        return;
                    }
                    ((LinearLayoutManager) recyclerListView.getLayoutManager()).scrollToPositionWithOffset(findCurrentAccent, (ThemeActivity.this.listView.getMeasuredWidth() / 2) - AndroidUtilities.dp(42.0f));
                    return;
                case 14:
                    TextCell textCell = (TextCell) viewHolder.itemView;
                    textCell.setColors("windowBackgroundWhiteBlueText4", "windowBackgroundWhiteBlueText4");
                    if (i != ThemeActivity.this.backgroundRow) {
                        if (i != ThemeActivity.this.editThemeRow) {
                            if (i != ThemeActivity.this.createNewThemeRow) {
                                return;
                            }
                            textCell.setTextAndIcon(LocaleController.getString("CreateNewTheme", R.string.CreateNewTheme), R.drawable.msg_colors, false);
                            return;
                        }
                        textCell.setTextAndIcon(LocaleController.getString("EditCurrentTheme", R.string.EditCurrentTheme), R.drawable.msg_theme, true);
                        return;
                    }
                    textCell.setTextAndIcon(LocaleController.getString("ChangeChatBackground", R.string.ChangeChatBackground), R.drawable.msg_background, false);
                    return;
                case 17:
                    ((DefaultThemesPreviewCell) viewHolder.itemView).updateDayNightMode();
                    return;
                case 19:
                    RadioButtonCell radioButtonCell = (RadioButtonCell) viewHolder.itemView;
                    if (i == ThemeActivity.this.saveToGalleryOption1Row) {
                        radioButtonCell.setTextAndValue("save media only from peer chats", "", true, false);
                        return;
                    } else {
                        radioButtonCell.setTextAndValue("save media from all chats", "", true, false);
                        return;
                    }
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onViewAttachedToWindow(RecyclerView.ViewHolder viewHolder) {
            int itemViewType = viewHolder.getItemViewType();
            if (itemViewType == 4) {
                ((ThemeTypeCell) viewHolder.itemView).setTypeChecked(viewHolder.getAdapterPosition() == Theme.selectedAutoNightType);
            }
            if (itemViewType == 2 || itemViewType == 3) {
                return;
            }
            viewHolder.itemView.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int i) {
            if (i == ThemeActivity.this.scheduleFromRow || i == ThemeActivity.this.distanceRow || i == ThemeActivity.this.scheduleToRow || i == ThemeActivity.this.scheduleUpdateLocationRow || i == ThemeActivity.this.contactsReimportRow || i == ThemeActivity.this.contactsSortRow || i == ThemeActivity.this.bluetoothScoRow) {
                return 1;
            }
            if (i == ThemeActivity.this.automaticBrightnessInfoRow || i == ThemeActivity.this.scheduleLocationInfoRow) {
                return 2;
            }
            if (i == ThemeActivity.this.themeInfoRow || i == ThemeActivity.this.nightTypeInfoRow || i == ThemeActivity.this.scheduleFromToInfoRow || i == ThemeActivity.this.settings2Row || i == ThemeActivity.this.newThemeInfoRow || i == ThemeActivity.this.chatListInfoRow || i == ThemeActivity.this.bubbleRadiusInfoRow || i == ThemeActivity.this.swipeGestureInfoRow || i == ThemeActivity.this.saveToGallerySectionRow || i == ThemeActivity.this.appIconShadowRow) {
                return 3;
            }
            if (i == ThemeActivity.this.nightDisabledRow || i == ThemeActivity.this.nightScheduledRow || i == ThemeActivity.this.nightAutomaticRow || i == ThemeActivity.this.nightSystemDefaultRow) {
                return 4;
            }
            if (i == ThemeActivity.this.scheduleHeaderRow || i == ThemeActivity.this.automaticHeaderRow || i == ThemeActivity.this.preferedHeaderRow || i == ThemeActivity.this.settingsRow || i == ThemeActivity.this.themeHeaderRow || i == ThemeActivity.this.textSizeHeaderRow || i == ThemeActivity.this.chatListHeaderRow || i == ThemeActivity.this.bubbleRadiusHeaderRow || i == ThemeActivity.this.swipeGestureHeaderRow || i == ThemeActivity.this.selectThemeHeaderRow || i == ThemeActivity.this.appIconHeaderRow) {
                return 5;
            }
            if (i == ThemeActivity.this.automaticBrightnessRow) {
                return 6;
            }
            if (i == ThemeActivity.this.scheduleLocationRow || i == ThemeActivity.this.enableAnimationsRow || i == ThemeActivity.this.sendByEnterRow || i == ThemeActivity.this.raiseToSpeakRow || i == ThemeActivity.this.customTabsRow || i == ThemeActivity.this.directShareRow || i == ThemeActivity.this.chatBlurRow) {
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
            if (i == ThemeActivity.this.saveToGalleryOption1Row || i == ThemeActivity.this.saveToGalleryOption2Row) {
                return 19;
            }
            return i == ThemeActivity.this.appIconSelectorRow ? 20 : 1;
        }
    }

    /* loaded from: classes3.dex */
    private static abstract class TintRecyclerListView extends RecyclerListView {
        TintRecyclerListView(Context context) {
            super(context);
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextSettingsCell.class, TextCheckCell.class, HeaderCell.class, BrightnessControlCell.class, ThemeTypeCell.class, TextSizeCell.class, BubbleRadiusCell.class, ChatListCell.class, NotificationsCheckCell.class, ThemesHorizontalListCell.class, TintRecyclerListView.class, TextCell.class, SwipeGestureSettingsView.class, DefaultThemesPreviewCell.class, AppIconsSelectorCell.class}, null, null, null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundGray"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUBACKGROUND, null, null, null, null, "actionBarDefaultSubmenuBackground"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM, null, null, null, null, "actionBarDefaultSubmenuItem"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM | ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, "actionBarDefaultSubmenuItemIcon"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, "divider"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, "windowBackgroundGrayShadow"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, "windowBackgroundGrayShadow"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText4"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteValueText"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueHeader"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueText4"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueText4"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrack"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrackChecked"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{BrightnessControlCell.class}, new String[]{"leftImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayIcon"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{BrightnessControlCell.class}, new String[]{"rightImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayIcon"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{BrightnessControlCell.class}, new String[]{"seekBarView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "player_progressBackground"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_PROGRESSBAR, new Class[]{BrightnessControlCell.class}, new String[]{"seekBarView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "player_progress"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{ThemeTypeCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{ThemeTypeCell.class}, new String[]{"checkImage"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "featuredStickers_addedIcon"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_PROGRESSBAR, new Class[]{TextSizeCell.class}, new String[]{"sizeBar"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "player_progress"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, new String[]{"sizeBar"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "player_progressBackground"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_PROGRESSBAR, new Class[]{BubbleRadiusCell.class}, new String[]{"sizeBar"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "player_progress"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{BubbleRadiusCell.class}, new String[]{"sizeBar"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "player_progressBackground"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{ChatListCell.class}, null, null, null, "radioBackground"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{ChatListCell.class}, null, null, null, "radioBackgroundChecked"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText2"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrack"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "switchTrackChecked"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, null, new Drawable[]{Theme.chat_msgInDrawable, Theme.chat_msgInMediaDrawable}, null, "chat_inBubble"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, null, new Drawable[]{Theme.chat_msgInSelectedDrawable, Theme.chat_msgInMediaSelectedDrawable}, null, "chat_inBubbleSelected"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, null, Theme.chat_msgInDrawable.getShadowDrawables(), null, "chat_inBubbleShadow"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, null, Theme.chat_msgInMediaDrawable.getShadowDrawables(), null, "chat_inBubbleShadow"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, null, new Drawable[]{Theme.chat_msgOutDrawable, Theme.chat_msgOutMediaDrawable}, null, "chat_outBubble"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, null, new Drawable[]{Theme.chat_msgOutDrawable, Theme.chat_msgOutMediaDrawable}, null, "chat_outBubbleGradient"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, null, new Drawable[]{Theme.chat_msgOutDrawable, Theme.chat_msgOutMediaDrawable}, null, "chat_outBubbleGradient2"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, null, new Drawable[]{Theme.chat_msgOutDrawable, Theme.chat_msgOutMediaDrawable}, null, "chat_outBubbleGradient3"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, null, new Drawable[]{Theme.chat_msgOutSelectedDrawable, Theme.chat_msgOutMediaSelectedDrawable}, null, "chat_outBubbleSelected"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, null, new Drawable[]{Theme.chat_msgOutDrawable, Theme.chat_msgOutMediaDrawable}, null, "chat_outBubbleShadow"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, null, new Drawable[]{Theme.chat_msgInDrawable, Theme.chat_msgInMediaDrawable}, null, "chat_inBubbleShadow"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, null, null, null, "chat_messageTextIn"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, null, null, null, "chat_messageTextOut"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, null, new Drawable[]{Theme.chat_msgOutCheckDrawable}, null, "chat_outSentCheck"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, null, new Drawable[]{Theme.chat_msgOutCheckSelectedDrawable}, null, "chat_outSentCheckSelected"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, null, new Drawable[]{Theme.chat_msgOutCheckReadDrawable, Theme.chat_msgOutHalfCheckDrawable}, null, "chat_outSentCheckRead"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, null, new Drawable[]{Theme.chat_msgOutCheckReadSelectedDrawable, Theme.chat_msgOutHalfCheckSelectedDrawable}, null, "chat_outSentCheckReadSelected"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, null, new Drawable[]{Theme.chat_msgMediaCheckDrawable, Theme.chat_msgMediaHalfCheckDrawable}, null, "chat_mediaSentCheck"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, null, null, null, "chat_inReplyLine"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, null, null, null, "chat_outReplyLine"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, null, null, null, "chat_inReplyNameText"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, null, null, null, "chat_outReplyNameText"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, null, null, null, "chat_inReplyMessageText"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, null, null, null, "chat_outReplyMessageText"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, null, null, null, "chat_inReplyMediaMessageSelectedText"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, null, null, null, "chat_outReplyMediaMessageSelectedText"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, null, null, null, "chat_inTimeText"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, null, null, null, "chat_outTimeText"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, null, null, null, "chat_inTimeSelectedText"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, null, null, null, "chat_outTimeSelectedText"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{AppIconsSelectorCell.class}, null, null, null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{AppIconsSelectorCell.class}, null, null, null, "windowBackgroundWhiteBlackText"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{AppIconsSelectorCell.class}, null, null, null, "windowBackgroundWhiteHintText"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{AppIconsSelectorCell.class}, null, null, null, "windowBackgroundWhiteValueText"));
        arrayList.addAll(SimpleThemeDescription.createThemeDescriptions(new ThemeDescription.ThemeDescriptionDelegate() { // from class: org.telegram.ui.ThemeActivity$$ExternalSyntheticLambda11
            @Override // org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate
            public final void didSetColor() {
                ThemeActivity.this.lambda$getThemeDescriptions$12();
            }

            @Override // org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate
            public /* synthetic */ void onAnimationProgress(float f) {
                ThemeDescription.ThemeDescriptionDelegate.CC.$default$onAnimationProgress(this, f);
            }
        }, "windowBackgroundWhiteHintText", "windowBackgroundWhiteBlackText", "windowBackgroundWhiteValueText"));
        return arrayList;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$getThemeDescriptions$12() {
        for (int i = 0; i < this.listView.getChildCount(); i++) {
            View childAt = this.listView.getChildAt(i);
            if (childAt instanceof AppIconsSelectorCell) {
                ((AppIconsSelectorCell) childAt).getAdapter().notifyDataSetChanged();
            }
        }
        for (int i2 = 0; i2 < this.listView.getCachedChildCount(); i2++) {
            View cachedChildAt = this.listView.getCachedChildAt(i2);
            if (cachedChildAt instanceof AppIconsSelectorCell) {
                ((AppIconsSelectorCell) cachedChildAt).getAdapter().notifyDataSetChanged();
            }
        }
        for (int i3 = 0; i3 < this.listView.getHiddenChildCount(); i3++) {
            View hiddenChildAt = this.listView.getHiddenChildAt(i3);
            if (hiddenChildAt instanceof AppIconsSelectorCell) {
                ((AppIconsSelectorCell) hiddenChildAt).getAdapter().notifyDataSetChanged();
            }
        }
        for (int i4 = 0; i4 < this.listView.getAttachedScrapChildCount(); i4++) {
            View attachedScrapChildAt = this.listView.getAttachedScrapChildAt(i4);
            if (attachedScrapChildAt instanceof AppIconsSelectorCell) {
                ((AppIconsSelectorCell) attachedScrapChildAt).getAdapter().notifyDataSetChanged();
            }
        }
    }

    public void checkCurrentDayNight() {
        RLottieDrawable rLottieDrawable;
        if (this.currentType != 3) {
            return;
        }
        boolean z = !Theme.isCurrentThemeDay();
        if (this.lastIsDarkTheme != z) {
            this.lastIsDarkTheme = z;
            this.sunDrawable.setCustomEndFrame(z ? rLottieDrawable.getFramesCount() - 1 : 0);
            this.menuItem.getIconView().playAnimation();
        }
        if (this.themeListRow2 < 0) {
            return;
        }
        for (int i = 0; i < this.listView.getChildCount(); i++) {
            if (this.listView.getChildAt(i) instanceof DefaultThemesPreviewCell) {
                ((DefaultThemesPreviewCell) this.listView.getChildAt(i)).updateDayNightMode();
            }
        }
    }
}
