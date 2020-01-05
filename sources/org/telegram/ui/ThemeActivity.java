package org.telegram.ui;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import androidx.annotation.Keep;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView.LayoutParams;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.time.SunDate;
import org.telegram.tgnet.TLRPC.TL_theme;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.Theme.PatternsLoader;
import org.telegram.ui.ActionBar.Theme.ThemeAccent;
import org.telegram.ui.ActionBar.Theme.ThemeInfo;
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
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;
import org.telegram.ui.Components.SeekBarView;
import org.telegram.ui.Components.SeekBarView.SeekBarViewDelegate;
import org.telegram.ui.Components.ShareAlert;

public class ThemeActivity extends BaseFragment implements NotificationCenterDelegate {
    public static final int THEME_TYPE_BASIC = 0;
    public static final int THEME_TYPE_NIGHT = 1;
    public static final int THEME_TYPE_OTHER = 2;
    private static final int create_theme = 1;
    private static final int edit_theme = 3;
    private static final int reset_settings = 4;
    private static final int share_theme = 2;
    private int automaticBrightnessInfoRow;
    private int automaticBrightnessRow;
    private int automaticHeaderRow;
    private int backgroundRow;
    private int chatListHeaderRow;
    private int chatListInfoRow;
    private int chatListRow;
    private int contactsReimportRow;
    private int contactsSortRow;
    private int currentType;
    private int customTabsRow;
    private ArrayList<ThemeInfo> darkThemes = new ArrayList();
    private ArrayList<ThemeInfo> defaultThemes = new ArrayList();
    private int directShareRow;
    private int distanceRow;
    private int emojiRow;
    private int enableAnimationsRow;
    private GpsLocationListener gpsLocationListener = new GpsLocationListener(this, null);
    boolean hasThemeAccents;
    private LinearLayoutManager layoutManager;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    private ActionBarMenuItem menuItem;
    private GpsLocationListener networkLocationListener = new GpsLocationListener(this, null);
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
    private int saveToGalleryRow;
    private int scheduleFromRow;
    private int scheduleFromToInfoRow;
    private int scheduleHeaderRow;
    private int scheduleLocationInfoRow;
    private int scheduleLocationRow;
    private int scheduleToRow;
    private int scheduleUpdateLocationRow;
    private int sendByEnterRow;
    private int settings2Row;
    private int settingsRow;
    private ThemeAccent sharingAccent;
    private AlertDialog sharingProgressDialog;
    private ThemeInfo sharingTheme;
    private int stickersRow;
    private int stickersSection2Row;
    private int textSizeHeaderRow;
    private int textSizeRow;
    private int themeAccentListRow;
    private int themeHeaderRow;
    private int themeInfoRow;
    private int themeListRow;
    private ThemesHorizontalListCell themesHorizontalListCell;
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

        /* synthetic */ GpsLocationListener(ThemeActivity themeActivity, AnonymousClass1 anonymousClass1) {
            this();
        }

        public void onLocationChanged(Location location) {
            if (location != null) {
                ThemeActivity.this.stopLocationUpdate();
                ThemeActivity.this.updateSunTime(location, false);
            }
        }
    }

    private static class InnerAccentView extends View {
        private ObjectAnimator checkAnimator;
        private float checkedState;
        private ThemeAccent currentAccent;
        private ThemeInfo currentTheme;
        private final Paint paint = new Paint(1);

        InnerAccentView(Context context) {
            super(context);
        }

        /* Access modifiers changed, original: 0000 */
        public void setThemeAndColor(ThemeInfo themeInfo, ThemeAccent themeAccent) {
            this.currentTheme = themeInfo;
            this.currentAccent = themeAccent;
            updateCheckedState(false);
        }

        /* Access modifiers changed, original: 0000 */
        public void updateCheckedState(boolean z) {
            Object obj = this.currentTheme.currentAccentId == this.currentAccent.id ? 1 : null;
            ObjectAnimator objectAnimator = this.checkAnimator;
            if (objectAnimator != null) {
                objectAnimator.cancel();
            }
            float f = 1.0f;
            if (z) {
                float[] fArr = new float[1];
                if (obj == null) {
                    f = 0.0f;
                }
                fArr[0] = f;
                this.checkAnimator = ObjectAnimator.ofFloat(this, "checkedState", fArr);
                this.checkAnimator.setDuration(200);
                this.checkAnimator.start();
                return;
            }
            if (obj == null) {
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

        /* Access modifiers changed, original: protected */
        public void onAttachedToWindow() {
            super.onAttachedToWindow();
            updateCheckedState(false);
        }

        /* Access modifiers changed, original: protected */
        public void onMeasure(int i, int i2) {
            super.onMeasure(MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(62.0f), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(62.0f), NUM));
        }

        /* Access modifiers changed, original: protected */
        public void onDraw(Canvas canvas) {
            float dp = (float) AndroidUtilities.dp(20.0f);
            float measuredWidth = ((float) getMeasuredWidth()) * 0.5f;
            float measuredHeight = ((float) getMeasuredHeight()) * 0.5f;
            this.paint.setColor(this.currentAccent.accentColor);
            this.paint.setStyle(Style.STROKE);
            this.paint.setStrokeWidth((float) AndroidUtilities.dp(3.0f));
            this.paint.setAlpha(Math.round(this.checkedState * 255.0f));
            canvas.drawCircle(measuredWidth, measuredHeight, dp - (this.paint.getStrokeWidth() * 0.5f), this.paint);
            this.paint.setAlpha(255);
            this.paint.setStyle(Style.FILL);
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

        private void setTheme(ThemeInfo themeInfo) {
            if (themeInfo.defaultAccentCount >= 8) {
                this.colors = new int[]{themeInfo.getAccentColor(6), themeInfo.getAccentColor(4), themeInfo.getAccentColor(7), themeInfo.getAccentColor(2), themeInfo.getAccentColor(0), themeInfo.getAccentColor(5), themeInfo.getAccentColor(3)};
                return;
            }
            this.colors = new int[7];
        }

        /* Access modifiers changed, original: protected */
        public void onMeasure(int i, int i2) {
            super.onMeasure(MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(62.0f), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(62.0f), NUM));
        }

        /* Access modifiers changed, original: protected */
        public void onDraw(Canvas canvas) {
            float measuredWidth = ((float) getMeasuredWidth()) * 0.5f;
            float measuredHeight = ((float) getMeasuredHeight()) * 0.5f;
            float dp = (float) AndroidUtilities.dp(5.0f);
            float dp2 = ((float) AndroidUtilities.dp(20.0f)) - dp;
            this.paint.setStyle(Style.FILL);
            int i = 0;
            this.paint.setColor(this.colors[0]);
            canvas.drawCircle(measuredWidth, measuredHeight, dp, this.paint);
            double d = 0.0d;
            while (i < 6) {
                float sin = (((float) Math.sin(d)) * dp2) + measuredWidth;
                float cos = measuredHeight - (((float) Math.cos(d)) * dp2);
                i++;
                this.paint.setColor(this.colors[i]);
                canvas.drawCircle(sin, cos, dp, this.paint);
                d += 1.0471975511965976d;
            }
        }
    }

    public interface SizeChooseViewDelegate {
        void onSizeChanged();
    }

    private class TextSizeCell extends FrameLayout {
        private int endFontSize = 30;
        private int lastWidth;
        private ThemePreviewMessagesCell messagesCell;
        private SeekBarView sizeBar;
        private int startFontSize = 12;
        private TextPaint textPaint;

        public TextSizeCell(Context context) {
            super(context);
            setWillNotDraw(false);
            this.textPaint = new TextPaint(1);
            this.textPaint.setTextSize((float) AndroidUtilities.dp(16.0f));
            this.sizeBar = new SeekBarView(context);
            this.sizeBar.setReportChanges(true);
            this.sizeBar.setDelegate(new SeekBarViewDelegate(ThemeActivity.this) {
                public void onSeekBarPressed(boolean z) {
                }

                public void onSeekBarDrag(boolean z, float f) {
                    TextSizeCell textSizeCell = TextSizeCell.this;
                    ThemeActivity.this.setFontSize(Math.round(((float) textSizeCell.startFontSize) + (((float) (TextSizeCell.this.endFontSize - TextSizeCell.this.startFontSize)) * f)));
                }
            });
            addView(this.sizeBar, LayoutHelper.createFrame(-1, 38.0f, 51, 9.0f, 5.0f, 43.0f, 0.0f));
            this.messagesCell = new ThemePreviewMessagesCell(context, ThemeActivity.this.parentLayout, 0);
            addView(this.messagesCell, LayoutHelper.createFrame(-1, -2.0f, 51, 0.0f, 53.0f, 0.0f, 0.0f));
        }

        /* Access modifiers changed, original: protected */
        public void onDraw(Canvas canvas) {
            this.textPaint.setColor(Theme.getColor("windowBackgroundWhiteValueText"));
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("");
            stringBuilder.append(SharedConfig.fontSize);
            canvas.drawText(stringBuilder.toString(), (float) (getMeasuredWidth() - AndroidUtilities.dp(39.0f)), (float) AndroidUtilities.dp(28.0f), this.textPaint);
        }

        /* Access modifiers changed, original: protected */
        public void onMeasure(int i, int i2) {
            super.onMeasure(i, i2);
            MeasureSpec.getSize(i);
            SeekBarView seekBarView = this.sizeBar;
            i2 = SharedConfig.fontSize;
            int i3 = this.startFontSize;
            seekBarView.setProgress(((float) (i2 - i3)) / ((float) (this.endFontSize - i3)));
        }

        public void invalidate() {
            super.invalidate();
            this.messagesCell.invalidate();
            this.sizeBar.invalidate();
        }
    }

    private class ListAdapter extends SelectionAdapter {
        private boolean first = true;
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public int getItemCount() {
            return ThemeActivity.this.rowCount;
        }

        public boolean isEnabled(ViewHolder viewHolder) {
            int itemViewType = viewHolder.getItemViewType();
            return itemViewType == 0 || itemViewType == 1 || itemViewType == 4 || itemViewType == 7 || itemViewType == 10 || itemViewType == 11 || itemViewType == 12;
        }

        private void showOptionsForTheme(ThemeInfo themeInfo) {
            if (ThemeActivity.this.getParentActivity() == null) {
                return;
            }
            if ((themeInfo.info == null || themeInfo.themeLoaded) && ThemeActivity.this.currentType != 1) {
                CharSequence[] charSequenceArr;
                int[] iArr;
                Builder builder = new Builder(ThemeActivity.this.getParentActivity());
                String str = "ExportTheme";
                String str2 = null;
                int i = 0;
                if (themeInfo.pathToFile == null) {
                    charSequenceArr = new CharSequence[]{null, LocaleController.getString(str, NUM)};
                    iArr = new int[]{0, NUM};
                } else {
                    String string;
                    TL_theme tL_theme = themeInfo.info;
                    int i2 = (tL_theme == null || !tL_theme.isDefault) ? 1 : 0;
                    CharSequence[] charSequenceArr2 = new CharSequence[5];
                    charSequenceArr2[0] = LocaleController.getString("ShareFile", NUM);
                    charSequenceArr2[1] = LocaleController.getString(str, NUM);
                    TL_theme tL_theme2 = themeInfo.info;
                    if (tL_theme2 == null || (!tL_theme2.isDefault && tL_theme2.creator)) {
                        string = LocaleController.getString("Edit", NUM);
                    } else {
                        string = null;
                    }
                    charSequenceArr2[2] = string;
                    TL_theme tL_theme3 = themeInfo.info;
                    str = (tL_theme3 == null || !tL_theme3.creator) ? null : LocaleController.getString("ThemeSetUrl", NUM);
                    charSequenceArr2[3] = str;
                    if (i2 != 0) {
                        str2 = LocaleController.getString("Delete", NUM);
                    }
                    charSequenceArr2[4] = str2;
                    iArr = new int[]{NUM, NUM, NUM, NUM, NUM};
                    i = i2;
                    charSequenceArr = charSequenceArr2;
                }
                builder.setItems(charSequenceArr, iArr, new -$$Lambda$ThemeActivity$ListAdapter$mOT1foTAY8nRoymoRurolXzJymU(this, themeInfo));
                AlertDialog create = builder.create();
                ThemeActivity.this.showDialog(create);
                if (i != 0) {
                    create.setItemColor(create.getItemsCount() - 1, Theme.getColor("dialogTextRed2"), Theme.getColor("dialogRedIcon"));
                }
            }
        }

        /* JADX WARNING: Removed duplicated region for block: B:50:0x0108  */
        /* JADX WARNING: Removed duplicated region for block: B:56:0x012c A:{Catch:{ Exception -> 0x0178 }} */
        /* JADX WARNING: Removed duplicated region for block: B:55:0x012b A:{RETURN, Catch:{ Exception -> 0x0178 }} */
        /* JADX WARNING: Removed duplicated region for block: B:39:0x00e5 A:{SYNTHETIC, Splitter:B:39:0x00e5} */
        /* JADX WARNING: Removed duplicated region for block: B:34:0x00da A:{SYNTHETIC, Splitter:B:34:0x00da} */
        /* JADX WARNING: Removed duplicated region for block: B:50:0x0108  */
        /* JADX WARNING: Removed duplicated region for block: B:55:0x012b A:{RETURN, Catch:{ Exception -> 0x0178 }} */
        /* JADX WARNING: Removed duplicated region for block: B:56:0x012c A:{Catch:{ Exception -> 0x0178 }} */
        /* JADX WARNING: Missing exception handler attribute for start block: B:61:0x0153 */
        /* JADX WARNING: Can't wrap try/catch for region: R(4:59|60|61|62) */
        public /* synthetic */ void lambda$showOptionsForTheme$1$ThemeActivity$ListAdapter(org.telegram.ui.ActionBar.Theme.ThemeInfo r8, android.content.DialogInterface r9, int r10) {
            /*
            r7 = this;
            r9 = org.telegram.ui.ThemeActivity.this;
            r9 = r9.getParentActivity();
            if (r9 != 0) goto L_0x0009;
        L_0x0008:
            return;
        L_0x0009:
            r9 = 0;
            r0 = 2;
            r1 = 0;
            r2 = 1;
            if (r10 != 0) goto L_0x006b;
        L_0x000f:
            r10 = r8.info;
            if (r10 != 0) goto L_0x002d;
        L_0x0013:
            r10 = r8.account;
            r10 = org.telegram.messenger.MessagesController.getInstance(r10);
            r10.saveThemeToServer(r8, r1);
            r10 = org.telegram.messenger.NotificationCenter.getGlobalInstance();
            r3 = org.telegram.messenger.NotificationCenter.needShareTheme;
            r0 = new java.lang.Object[r0];
            r0[r9] = r8;
            r0[r2] = r1;
            r10.postNotificationName(r3, r0);
            goto L_0x0215;
        L_0x002d:
            r9 = new java.lang.StringBuilder;
            r9.<init>();
            r10 = "https://";
            r9.append(r10);
            r10 = org.telegram.ui.ThemeActivity.this;
            r10 = r10.currentAccount;
            r10 = org.telegram.messenger.MessagesController.getInstance(r10);
            r10 = r10.linkPrefix;
            r9.append(r10);
            r10 = "/addtheme/";
            r9.append(r10);
            r8 = r8.info;
            r8 = r8.slug;
            r9.append(r8);
            r5 = r9.toString();
            r8 = org.telegram.ui.ThemeActivity.this;
            r9 = new org.telegram.ui.Components.ShareAlert;
            r1 = r8.getParentActivity();
            r2 = 0;
            r4 = 0;
            r6 = 0;
            r0 = r9;
            r3 = r5;
            r0.<init>(r1, r2, r3, r4, r5, r6);
            r8.showDialog(r9);
            goto L_0x0215;
        L_0x006b:
            if (r10 != r2) goto L_0x017e;
        L_0x006d:
            r9 = r8.pathToFile;
            if (r9 != 0) goto L_0x00ee;
        L_0x0071:
            r9 = r8.assetName;
            if (r9 != 0) goto L_0x00ee;
        L_0x0075:
            r9 = new java.lang.StringBuilder;
            r9.<init>();
            r10 = org.telegram.ui.ActionBar.Theme.getDefaultColors();
            r10 = r10.entrySet();
            r10 = r10.iterator();
        L_0x0086:
            r0 = r10.hasNext();
            if (r0 == 0) goto L_0x00ad;
        L_0x008c:
            r0 = r10.next();
            r0 = (java.util.Map.Entry) r0;
            r3 = r0.getKey();
            r3 = (java.lang.String) r3;
            r9.append(r3);
            r3 = "=";
            r9.append(r3);
            r0 = r0.getValue();
            r9.append(r0);
            r0 = "\n";
            r9.append(r0);
            goto L_0x0086;
        L_0x00ad:
            r10 = new java.io.File;
            r0 = org.telegram.messenger.ApplicationLoader.getFilesDirFixed();
            r3 = "default_theme.attheme";
            r10.<init>(r0, r3);
            r0 = new java.io.FileOutputStream;	 Catch:{ Exception -> 0x00d4 }
            r0.<init>(r10);	 Catch:{ Exception -> 0x00d4 }
            r9 = r9.toString();	 Catch:{ Exception -> 0x00ce, all -> 0x00cc }
            r9 = org.telegram.messenger.AndroidUtilities.getStringBytes(r9);	 Catch:{ Exception -> 0x00ce, all -> 0x00cc }
            r0.write(r9);	 Catch:{ Exception -> 0x00ce, all -> 0x00cc }
            r0.close();	 Catch:{ Exception -> 0x00de }
            goto L_0x00fe;
        L_0x00cc:
            r8 = move-exception;
            goto L_0x00e3;
        L_0x00ce:
            r9 = move-exception;
            r1 = r0;
            goto L_0x00d5;
        L_0x00d1:
            r8 = move-exception;
            r0 = r1;
            goto L_0x00e3;
        L_0x00d4:
            r9 = move-exception;
        L_0x00d5:
            org.telegram.messenger.FileLog.e(r9);	 Catch:{ all -> 0x00d1 }
            if (r1 == 0) goto L_0x00fe;
        L_0x00da:
            r1.close();	 Catch:{ Exception -> 0x00de }
            goto L_0x00fe;
        L_0x00de:
            r9 = move-exception;
            org.telegram.messenger.FileLog.e(r9);
            goto L_0x00fe;
        L_0x00e3:
            if (r0 == 0) goto L_0x00ed;
        L_0x00e5:
            r0.close();	 Catch:{ Exception -> 0x00e9 }
            goto L_0x00ed;
        L_0x00e9:
            r9 = move-exception;
            org.telegram.messenger.FileLog.e(r9);
        L_0x00ed:
            throw r8;
        L_0x00ee:
            r9 = r8.assetName;
            if (r9 == 0) goto L_0x00f7;
        L_0x00f2:
            r10 = org.telegram.ui.ActionBar.Theme.getAssetFile(r9);
            goto L_0x00fe;
        L_0x00f7:
            r10 = new java.io.File;
            r9 = r8.pathToFile;
            r10.<init>(r9);
        L_0x00fe:
            r8 = r8.name;
            r9 = ".attheme";
            r0 = r8.endsWith(r9);
            if (r0 != 0) goto L_0x0117;
        L_0x0108:
            r0 = new java.lang.StringBuilder;
            r0.<init>();
            r0.append(r8);
            r0.append(r9);
            r8 = r0.toString();
        L_0x0117:
            r9 = new java.io.File;
            r0 = 4;
            r0 = org.telegram.messenger.FileLoader.getDirectory(r0);
            r8 = org.telegram.messenger.FileLoader.fixFileName(r8);
            r9.<init>(r0, r8);
            r8 = org.telegram.messenger.AndroidUtilities.copyFile(r10, r9);	 Catch:{ Exception -> 0x0178 }
            if (r8 != 0) goto L_0x012c;
        L_0x012b:
            return;
        L_0x012c:
            r8 = new android.content.Intent;	 Catch:{ Exception -> 0x0178 }
            r10 = "android.intent.action.SEND";
            r8.<init>(r10);	 Catch:{ Exception -> 0x0178 }
            r10 = "text/xml";
            r8.setType(r10);	 Catch:{ Exception -> 0x0178 }
            r10 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0178 }
            r0 = 24;
            r1 = "android.intent.extra.STREAM";
            if (r10 < r0) goto L_0x015b;
        L_0x0140:
            r10 = org.telegram.ui.ThemeActivity.this;	 Catch:{ Exception -> 0x0153 }
            r10 = r10.getParentActivity();	 Catch:{ Exception -> 0x0153 }
            r0 = "org.telegram.messenger.beta.provider";
            r10 = androidx.core.content.FileProvider.getUriForFile(r10, r0, r9);	 Catch:{ Exception -> 0x0153 }
            r8.putExtra(r1, r10);	 Catch:{ Exception -> 0x0153 }
            r8.setFlags(r2);	 Catch:{ Exception -> 0x0153 }
            goto L_0x0162;
        L_0x0153:
            r9 = android.net.Uri.fromFile(r9);	 Catch:{ Exception -> 0x0178 }
            r8.putExtra(r1, r9);	 Catch:{ Exception -> 0x0178 }
            goto L_0x0162;
        L_0x015b:
            r9 = android.net.Uri.fromFile(r9);	 Catch:{ Exception -> 0x0178 }
            r8.putExtra(r1, r9);	 Catch:{ Exception -> 0x0178 }
        L_0x0162:
            r9 = org.telegram.ui.ThemeActivity.this;	 Catch:{ Exception -> 0x0178 }
            r10 = "ShareFile";
            r0 = NUM; // 0x7f0e0a31 float:1.888033E38 double:1.0531634456E-314;
            r10 = org.telegram.messenger.LocaleController.getString(r10, r0);	 Catch:{ Exception -> 0x0178 }
            r8 = android.content.Intent.createChooser(r8, r10);	 Catch:{ Exception -> 0x0178 }
            r10 = 500; // 0x1f4 float:7.0E-43 double:2.47E-321;
            r9.startActivityForResult(r8, r10);	 Catch:{ Exception -> 0x0178 }
            goto L_0x0215;
        L_0x0178:
            r8 = move-exception;
            org.telegram.messenger.FileLog.e(r8);
            goto L_0x0215;
        L_0x017e:
            if (r10 != r0) goto L_0x01a3;
        L_0x0180:
            r9 = org.telegram.ui.ThemeActivity.this;
            r9 = r9.parentLayout;
            if (r9 == 0) goto L_0x0215;
        L_0x0188:
            org.telegram.ui.ActionBar.Theme.applyTheme(r8);
            r9 = org.telegram.ui.ThemeActivity.this;
            r9 = r9.parentLayout;
            r9.rebuildAllFragmentViews(r2, r2);
            r9 = new org.telegram.ui.Components.ThemeEditorView;
            r9.<init>();
            r10 = org.telegram.ui.ThemeActivity.this;
            r10 = r10.getParentActivity();
            r9.show(r10, r8);
            goto L_0x0215;
        L_0x01a3:
            r0 = 3;
            if (r10 != r0) goto L_0x01b1;
        L_0x01a6:
            r10 = org.telegram.ui.ThemeActivity.this;
            r0 = new org.telegram.ui.ThemeSetUrlActivity;
            r0.<init>(r8, r1, r9);
            r10.presentFragment(r0);
            goto L_0x0215;
        L_0x01b1:
            r9 = org.telegram.ui.ThemeActivity.this;
            r9 = r9.getParentActivity();
            if (r9 != 0) goto L_0x01ba;
        L_0x01b9:
            return;
        L_0x01ba:
            r9 = new org.telegram.ui.ActionBar.AlertDialog$Builder;
            r10 = org.telegram.ui.ThemeActivity.this;
            r10 = r10.getParentActivity();
            r9.<init>(r10);
            r10 = NUM; // 0x7f0e03ab float:1.8876942E38 double:1.0531626206E-314;
            r0 = "DeleteThemeTitle";
            r10 = org.telegram.messenger.LocaleController.getString(r0, r10);
            r9.setTitle(r10);
            r10 = NUM; // 0x7f0e03aa float:1.887694E38 double:1.05316262E-314;
            r0 = "DeleteThemeAlert";
            r10 = org.telegram.messenger.LocaleController.getString(r0, r10);
            r9.setMessage(r10);
            r10 = NUM; // 0x7f0e0381 float:1.8876857E38 double:1.0531626E-314;
            r0 = "Delete";
            r10 = org.telegram.messenger.LocaleController.getString(r0, r10);
            r0 = new org.telegram.ui.-$$Lambda$ThemeActivity$ListAdapter$HjGrFd2877SP2gFmUCLASSNAMEvuRyOmw;
            r0.<init>(r7, r8);
            r9.setPositiveButton(r10, r0);
            r8 = NUM; // 0x7f0e0213 float:1.8876115E38 double:1.053162419E-314;
            r10 = "Cancel";
            r8 = org.telegram.messenger.LocaleController.getString(r10, r8);
            r9.setNegativeButton(r8, r1);
            r8 = r9.create();
            r9 = org.telegram.ui.ThemeActivity.this;
            r9.showDialog(r8);
            r9 = -1;
            r8 = r8.getButton(r9);
            r8 = (android.widget.TextView) r8;
            if (r8 == 0) goto L_0x0215;
        L_0x020c:
            r9 = "dialogTextRed2";
            r9 = org.telegram.ui.ActionBar.Theme.getColor(r9);
            r8.setTextColor(r9);
        L_0x0215:
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ThemeActivity$ListAdapter.lambda$showOptionsForTheme$1$ThemeActivity$ListAdapter(org.telegram.ui.ActionBar.Theme$ThemeInfo, android.content.DialogInterface, int):void");
        }

        public /* synthetic */ void lambda$null$0$ThemeActivity$ListAdapter(ThemeInfo themeInfo, DialogInterface dialogInterface, int i) {
            MessagesController.getInstance(themeInfo.account).saveTheme(themeInfo, null, themeInfo == Theme.getCurrentNightTheme(), true);
            if (Theme.deleteTheme(themeInfo)) {
                ThemeActivity.this.parentLayout.rebuildAllFragmentViews(true, true);
            }
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.themeListUpdated, new Object[0]);
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View textSettingsCell;
            String str = "windowBackgroundWhite";
            switch (i) {
                case 1:
                    textSettingsCell = new TextSettingsCell(this.mContext);
                    textSettingsCell.setBackgroundColor(Theme.getColor(str));
                    break;
                case 2:
                    textSettingsCell = new TextInfoPrivacyCell(this.mContext);
                    textSettingsCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                    break;
                case 3:
                    textSettingsCell = new ShadowSectionCell(this.mContext);
                    break;
                case 4:
                    textSettingsCell = new ThemeTypeCell(this.mContext);
                    textSettingsCell.setBackgroundColor(Theme.getColor(str));
                    break;
                case 5:
                    textSettingsCell = new HeaderCell(this.mContext);
                    textSettingsCell.setBackgroundColor(Theme.getColor(str));
                    break;
                case 6:
                    textSettingsCell = new BrightnessControlCell(this.mContext) {
                        /* Access modifiers changed, original: protected */
                        public void didChangedValue(float f) {
                            int i = (int) (Theme.autoNightBrighnessThreshold * 100.0f);
                            int i2 = (int) (f * 100.0f);
                            Theme.autoNightBrighnessThreshold = f;
                            if (i != i2) {
                                Holder holder = (Holder) ThemeActivity.this.listView.findViewHolderForAdapterPosition(ThemeActivity.this.automaticBrightnessInfoRow);
                                if (holder != null) {
                                    ((TextInfoPrivacyCell) holder.itemView).setText(LocaleController.formatString("AutoNightBrightnessInfo", NUM, Integer.valueOf((int) (Theme.autoNightBrighnessThreshold * 100.0f))));
                                }
                                Theme.checkAutoNightThemeConditions(true);
                            }
                        }
                    };
                    textSettingsCell.setBackgroundColor(Theme.getColor(str));
                    break;
                case 7:
                    textSettingsCell = new TextCheckCell(this.mContext);
                    textSettingsCell.setBackgroundColor(Theme.getColor(str));
                    break;
                case 8:
                    textSettingsCell = new TextSizeCell(this.mContext);
                    textSettingsCell.setBackgroundColor(Theme.getColor(str));
                    break;
                case 9:
                    textSettingsCell = new ChatListCell(this.mContext) {
                        /* Access modifiers changed, original: protected */
                        public void didSelectChatType(boolean z) {
                            SharedConfig.setUseThreeLinesLayout(z);
                        }
                    };
                    textSettingsCell.setBackgroundColor(Theme.getColor(str));
                    break;
                case 10:
                    textSettingsCell = new NotificationsCheckCell(this.mContext, 21, 64);
                    textSettingsCell.setBackgroundColor(Theme.getColor(str));
                    break;
                case 11:
                    this.first = true;
                    ThemeActivity themeActivity = ThemeActivity.this;
                    themeActivity.themesHorizontalListCell = new ThemesHorizontalListCell(this.mContext, themeActivity.currentType, ThemeActivity.this.defaultThemes, ThemeActivity.this.darkThemes) {
                        /* Access modifiers changed, original: protected */
                        public void showOptionsForTheme(ThemeInfo themeInfo) {
                            ThemeActivity.this.listAdapter.showOptionsForTheme(themeInfo);
                        }

                        /* Access modifiers changed, original: protected */
                        public void presentFragment(BaseFragment baseFragment) {
                            ThemeActivity.this.presentFragment(baseFragment);
                        }

                        /* Access modifiers changed, original: protected */
                        public void updateRows() {
                            ThemeActivity.this.updateRows(false);
                        }
                    };
                    ThemeActivity.this.themesHorizontalListCell.setDrawDivider(ThemeActivity.this.hasThemeAccents);
                    ThemeActivity.this.themesHorizontalListCell.setFocusable(false);
                    textSettingsCell = ThemeActivity.this.themesHorizontalListCell;
                    textSettingsCell.setLayoutParams(new LayoutParams(-1, AndroidUtilities.dp(148.0f)));
                    break;
                default:
                    textSettingsCell = new TintRecyclerListView(this.mContext) {
                        public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                            if (!(getParent() == null || getParent().getParent() == null)) {
                                getParent().getParent().requestDisallowInterceptTouchEvent(canScrollHorizontally(-1));
                            }
                            return super.onInterceptTouchEvent(motionEvent);
                        }
                    };
                    textSettingsCell.setFocusable(false);
                    textSettingsCell.setBackgroundColor(Theme.getColor(str));
                    textSettingsCell.setItemAnimator(null);
                    textSettingsCell.setLayoutAnimation(null);
                    textSettingsCell.setPadding(AndroidUtilities.dp(11.0f), 0, AndroidUtilities.dp(11.0f), 0);
                    textSettingsCell.setClipToPadding(false);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.mContext);
                    linearLayoutManager.setOrientation(0);
                    textSettingsCell.setLayoutManager(linearLayoutManager);
                    ThemeAccentsListAdapter themeAccentsListAdapter = new ThemeAccentsListAdapter(this.mContext);
                    textSettingsCell.setAdapter(themeAccentsListAdapter);
                    textSettingsCell.setOnItemClickListener(new -$$Lambda$ThemeActivity$ListAdapter$37GYc2ZgypZubBbNYS34Yq2aS8g(this, themeAccentsListAdapter, textSettingsCell));
                    textSettingsCell.setOnItemLongClickListener(new -$$Lambda$ThemeActivity$ListAdapter$cfae1HOWszm_UVN2RQ8ct-ooT1E(this, themeAccentsListAdapter));
                    textSettingsCell.setLayoutParams(new LayoutParams(-1, AndroidUtilities.dp(62.0f)));
                    break;
            }
            return new Holder(textSettingsCell);
        }

        public /* synthetic */ void lambda$onCreateViewHolder$2$ThemeActivity$ListAdapter(ThemeAccentsListAdapter themeAccentsListAdapter, RecyclerListView recyclerListView, View view, int i) {
            ThemeInfo currentNightTheme = ThemeActivity.this.currentType == 1 ? Theme.getCurrentNightTheme() : Theme.getCurrentTheme();
            int i2 = 0;
            if (i == themeAccentsListAdapter.getItemCount() - 1) {
                ThemeActivity themeActivity = ThemeActivity.this;
                themeActivity.presentFragment(new ThemePreviewActivity(currentNightTheme, false, 1, false, themeActivity.currentType == 1));
            } else {
                ThemeAccent themeAccent = (ThemeAccent) themeAccentsListAdapter.themeAccents.get(i);
                if (!(TextUtils.isEmpty(themeAccent.patternSlug) || themeAccent.id == Theme.DEFALT_THEME_ACCENT_ID)) {
                    PatternsLoader.createLoader(false);
                }
                i = currentNightTheme.currentAccentId;
                int i3 = themeAccent.id;
                if (i != i3) {
                    NotificationCenter globalInstance = NotificationCenter.getGlobalInstance();
                    i3 = NotificationCenter.needSetDayNightTheme;
                    Object[] objArr = new Object[4];
                    objArr[0] = currentNightTheme;
                    objArr[1] = Boolean.valueOf(ThemeActivity.this.currentType == 1);
                    objArr[2] = null;
                    objArr[3] = Integer.valueOf(themeAccent.id);
                    globalInstance.postNotificationName(i3, objArr);
                } else {
                    ThemeActivity.this.presentFragment(new ThemePreviewActivity(currentNightTheme, false, 1, i3 >= 100, ThemeActivity.this.currentType == 1));
                }
            }
            int left = view.getLeft();
            int right = view.getRight();
            i = AndroidUtilities.dp(52.0f);
            left -= i;
            if (left < 0) {
                recyclerListView.smoothScrollBy(left, 0);
            } else {
                right += i;
                if (right > recyclerListView.getMeasuredWidth()) {
                    recyclerListView.smoothScrollBy(right - recyclerListView.getMeasuredWidth(), 0);
                }
            }
            left = recyclerListView.getChildCount();
            while (i2 < left) {
                view = recyclerListView.getChildAt(i2);
                if (view instanceof InnerAccentView) {
                    ((InnerAccentView) view).updateCheckedState(true);
                }
                i2++;
            }
        }

        public /* synthetic */ boolean lambda$onCreateViewHolder$5$ThemeActivity$ListAdapter(ThemeAccentsListAdapter themeAccentsListAdapter, View view, int i) {
            if (i >= 0 && i < themeAccentsListAdapter.themeAccents.size()) {
                ThemeAccent themeAccent = (ThemeAccent) themeAccentsListAdapter.themeAccents.get(i);
                if (themeAccent.id >= 100) {
                    Builder builder = new Builder(ThemeActivity.this.getParentActivity());
                    CharSequence[] charSequenceArr = new CharSequence[4];
                    charSequenceArr[0] = LocaleController.getString("OpenInEditor", NUM);
                    charSequenceArr[1] = LocaleController.getString("ShareTheme", NUM);
                    TL_theme tL_theme = themeAccent.info;
                    String string = (tL_theme == null || !tL_theme.creator) ? null : LocaleController.getString("ThemeSetUrl", NUM);
                    charSequenceArr[2] = string;
                    charSequenceArr[3] = LocaleController.getString("DeleteTheme", NUM);
                    builder.setItems(charSequenceArr, new int[]{NUM, NUM, NUM, NUM}, new -$$Lambda$ThemeActivity$ListAdapter$bBxbJopnBfliVbn7S3U5sGJ24Z4(this, themeAccent, themeAccentsListAdapter));
                    AlertDialog create = builder.create();
                    ThemeActivity.this.showDialog(create);
                    create.setItemColor(create.getItemsCount() - 1, Theme.getColor("dialogTextRed2"), Theme.getColor("dialogRedIcon"));
                    return true;
                }
            }
            return false;
        }

        public /* synthetic */ void lambda$null$4$ThemeActivity$ListAdapter(ThemeAccent themeAccent, ThemeAccentsListAdapter themeAccentsListAdapter, DialogInterface dialogInterface, int i) {
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
                    } else {
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("https://");
                        stringBuilder.append(MessagesController.getInstance(ThemeActivity.this.currentAccount).linkPrefix);
                        stringBuilder.append("/addtheme/");
                        stringBuilder.append(themeAccent.info.slug);
                        String stringBuilder2 = stringBuilder.toString();
                        ThemeActivity themeActivity2 = ThemeActivity.this;
                        themeActivity2.showDialog(new ShareAlert(themeActivity2.getParentActivity(), null, stringBuilder2, false, stringBuilder2, false));
                    }
                } else if (i == 2) {
                    ThemeActivity.this.presentFragment(new ThemeSetUrlActivity(themeAccent.parentTheme, themeAccent, false));
                } else if (i == 3 && ThemeActivity.this.getParentActivity() != null) {
                    Builder builder = new Builder(ThemeActivity.this.getParentActivity());
                    builder.setTitle(LocaleController.getString("DeleteThemeTitle", NUM));
                    builder.setMessage(LocaleController.getString("DeleteThemeAlert", NUM));
                    builder.setPositiveButton(LocaleController.getString("Delete", NUM), new -$$Lambda$ThemeActivity$ListAdapter$BC0NFm3_zJfJjk282QVjgcIgzU4(this, themeAccentsListAdapter, themeAccent));
                    builder.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
                    AlertDialog create = builder.create();
                    ThemeActivity.this.showDialog(create);
                    TextView textView = (TextView) create.getButton(-1);
                    if (textView != null) {
                        textView.setTextColor(Theme.getColor("dialogTextRed2"));
                    }
                }
            }
        }

        public /* synthetic */ void lambda$null$3$ThemeActivity$ListAdapter(ThemeAccentsListAdapter themeAccentsListAdapter, ThemeAccent themeAccent, DialogInterface dialogInterface, int i) {
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
                objArr[3] = Integer.valueOf(-1);
                globalInstance.postNotificationName(i2, objArr);
            }
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            String str = "AutoNightAdaptive";
            String str2 = "AutoNightScheduled";
            String str3 = "AutoNightThemeOff";
            String str4 = "AutoNightTheme";
            boolean z = false;
            boolean z2 = true;
            String str5;
            int i2;
            String string;
            switch (viewHolder.getItemViewType()) {
                case 1:
                    TextSettingsCell textSettingsCell = (TextSettingsCell) viewHolder.itemView;
                    if (i != ThemeActivity.this.nightThemeRow) {
                        str5 = "%02d:%02d";
                        if (i == ThemeActivity.this.scheduleFromRow) {
                            i = Theme.autoNightDayStartTime;
                            i -= (i / 60) * 60;
                            textSettingsCell.setTextAndValue(LocaleController.getString("AutoNightFrom", NUM), String.format(str5, new Object[]{Integer.valueOf(i2), Integer.valueOf(i)}), true);
                            return;
                        } else if (i == ThemeActivity.this.scheduleToRow) {
                            i = Theme.autoNightDayEndTime;
                            i -= (i / 60) * 60;
                            textSettingsCell.setTextAndValue(LocaleController.getString("AutoNightTo", NUM), String.format(str5, new Object[]{Integer.valueOf(i2), Integer.valueOf(i)}), false);
                            return;
                        } else if (i == ThemeActivity.this.scheduleUpdateLocationRow) {
                            textSettingsCell.setTextAndValue(LocaleController.getString("AutoNightUpdateLocation", NUM), Theme.autoNightCityName, false);
                            return;
                        } else if (i == ThemeActivity.this.contactsSortRow) {
                            i = MessagesController.getGlobalMainSettings().getInt("sortContactsBy", 0);
                            if (i == 0) {
                                string = LocaleController.getString("Default", NUM);
                            } else if (i == 1) {
                                string = LocaleController.getString("FirstName", NUM);
                            } else {
                                string = LocaleController.getString("LastName", NUM);
                            }
                            textSettingsCell.setTextAndValue(LocaleController.getString("SortBy", NUM), string, true);
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
                            i = SharedConfig.distanceSystemType;
                            if (i == 0) {
                                string = LocaleController.getString("DistanceUnitsAutomatic", NUM);
                            } else if (i == 1) {
                                string = LocaleController.getString("DistanceUnitsKilometers", NUM);
                            } else {
                                string = LocaleController.getString("DistanceUnitsMiles", NUM);
                            }
                            textSettingsCell.setTextAndValue(LocaleController.getString("DistanceUnits", NUM), string, false);
                            return;
                        } else {
                            return;
                        }
                    } else if (Theme.selectedAutoNightType == 0 || Theme.getCurrentNightTheme() == null) {
                        textSettingsCell.setTextAndValue(LocaleController.getString(str4, NUM), LocaleController.getString(str3, NUM), false);
                        return;
                    } else {
                        textSettingsCell.setTextAndValue(LocaleController.getString(str4, NUM), Theme.getCurrentNightThemeName(), false);
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
                    str5 = "windowBackgroundGrayShadow";
                    if (i == ThemeActivity.this.stickersSection2Row || ((i == ThemeActivity.this.nightTypeInfoRow && ThemeActivity.this.themeInfoRow == -1) || (i == ThemeActivity.this.themeInfoRow && ThemeActivity.this.nightTypeInfoRow != -1))) {
                        viewHolder.itemView.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, str5));
                        return;
                    } else {
                        viewHolder.itemView.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, str5));
                        return;
                    }
                case 4:
                    ThemeTypeCell themeTypeCell = (ThemeTypeCell) viewHolder.itemView;
                    if (i == ThemeActivity.this.nightDisabledRow) {
                        string = LocaleController.getString("AutoNightDisabled", NUM);
                        if (Theme.selectedAutoNightType == 0) {
                            z = true;
                        }
                        themeTypeCell.setValue(string, z, true);
                        return;
                    } else if (i == ThemeActivity.this.nightScheduledRow) {
                        string = LocaleController.getString(str2, NUM);
                        if (Theme.selectedAutoNightType == 1) {
                            z = true;
                        }
                        themeTypeCell.setValue(string, z, true);
                        return;
                    } else if (i == ThemeActivity.this.nightAutomaticRow) {
                        string = LocaleController.getString(str, NUM);
                        boolean z3 = Theme.selectedAutoNightType == 2;
                        if (ThemeActivity.this.nightSystemDefaultRow != -1) {
                            z = true;
                        }
                        themeTypeCell.setValue(string, z3, z);
                        return;
                    } else if (i == ThemeActivity.this.nightSystemDefaultRow) {
                        string = LocaleController.getString("AutoNightSystemDefault", NUM);
                        if (Theme.selectedAutoNightType != 3) {
                            z2 = false;
                        }
                        themeTypeCell.setValue(string, z2, false);
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
                        CharSequence currentNightThemeName = z ? Theme.getCurrentNightThemeName() : LocaleController.getString(str3, NUM);
                        if (z) {
                            String string2;
                            i2 = Theme.selectedAutoNightType;
                            if (i2 == 1) {
                                string2 = LocaleController.getString(str2, NUM);
                            } else if (i2 == 3) {
                                string2 = LocaleController.getString("AutoNightSystemDefault", NUM);
                            } else {
                                string2 = LocaleController.getString(str, NUM);
                            }
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append(string2);
                            stringBuilder.append(" ");
                            stringBuilder.append(currentNightThemeName);
                            currentNightThemeName = stringBuilder.toString();
                        }
                        notificationsCheckCell.setTextAndValueAndCheck(LocaleController.getString(str4, NUM), currentNightThemeName, z, true);
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
                    i2 = themeAccentsListAdapter.findCurrentAccent();
                    if (i2 == -1) {
                        i2 = themeAccentsListAdapter.getItemCount() - 1;
                    }
                    if (i2 != -1) {
                        ((LinearLayoutManager) recyclerListView.getLayoutManager()).scrollToPositionWithOffset(i2, (ThemeActivity.this.listView.getMeasuredWidth() / 2) - AndroidUtilities.dp(42.0f));
                        return;
                    }
                    return;
                default:
                    return;
            }
        }

        public void onViewAttachedToWindow(ViewHolder viewHolder) {
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
            if (i == ThemeActivity.this.themeInfoRow || i == ThemeActivity.this.nightTypeInfoRow || i == ThemeActivity.this.scheduleFromToInfoRow || i == ThemeActivity.this.stickersSection2Row || i == ThemeActivity.this.settings2Row || i == ThemeActivity.this.newThemeInfoRow || i == ThemeActivity.this.chatListInfoRow) {
                return 3;
            }
            if (i == ThemeActivity.this.nightDisabledRow || i == ThemeActivity.this.nightScheduledRow || i == ThemeActivity.this.nightAutomaticRow || i == ThemeActivity.this.nightSystemDefaultRow) {
                return 4;
            }
            if (i == ThemeActivity.this.scheduleHeaderRow || i == ThemeActivity.this.automaticHeaderRow || i == ThemeActivity.this.preferedHeaderRow || i == ThemeActivity.this.settingsRow || i == ThemeActivity.this.themeHeaderRow || i == ThemeActivity.this.textSizeHeaderRow || i == ThemeActivity.this.chatListHeaderRow) {
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
            return 1;
        }
    }

    private class ThemeAccentsListAdapter extends SelectionAdapter {
        private ThemeInfo currentTheme;
        private Context mContext;
        private ArrayList<ThemeAccent> themeAccents;

        public boolean isEnabled(ViewHolder viewHolder) {
            return false;
        }

        ThemeAccentsListAdapter(Context context) {
            this.mContext = context;
            notifyDataSetChanged();
        }

        public void notifyDataSetChanged() {
            this.currentTheme = ThemeActivity.this.currentType == 1 ? Theme.getCurrentNightTheme() : Theme.getCurrentTheme();
            this.themeAccents = new ArrayList(this.currentTheme.themeAccents);
            super.notifyDataSetChanged();
        }

        public int getItemViewType(int i) {
            return i == getItemCount() - 1 ? 1 : 0;
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            if (i != 0) {
                return new Holder(new InnerCustomAccentView(this.mContext));
            }
            return new Holder(new InnerAccentView(this.mContext));
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            int itemViewType = getItemViewType(i);
            if (itemViewType == 0) {
                ((InnerAccentView) viewHolder.itemView).setThemeAndColor(this.currentTheme, (ThemeAccent) this.themeAccents.get(i));
            } else if (itemViewType == 1) {
                ((InnerCustomAccentView) viewHolder.itemView).setTheme(this.currentTheme);
            }
        }

        public int getItemCount() {
            return this.themeAccents.isEmpty() ? 0 : this.themeAccents.size() + 1;
        }

        private int findCurrentAccent() {
            return this.themeAccents.indexOf(this.currentTheme.getAccent(false));
        }
    }

    private static abstract class TintRecyclerListView extends RecyclerListView {
        TintRecyclerListView(Context context) {
            super(context);
        }
    }

    public ThemeActivity(int i) {
        this.currentType = i;
        updateRows(true);
    }

    private boolean setFontSize(int i) {
        int i2 = 0;
        if (i == SharedConfig.fontSize) {
            return false;
        }
        SharedConfig.fontSize = i;
        Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putInt("fons_size", SharedConfig.fontSize);
        edit.commit();
        Theme.chat_msgTextPaint.setTextSize((float) AndroidUtilities.dp((float) SharedConfig.fontSize));
        ViewHolder findViewHolderForAdapterPosition = this.listView.findViewHolderForAdapterPosition(this.textSizeRow);
        if (findViewHolderForAdapterPosition != null) {
            View view = findViewHolderForAdapterPosition.itemView;
            if (view instanceof TextSizeCell) {
                ChatMessageCell[] cells = ((TextSizeCell) view).messagesCell.getCells();
                while (i2 < cells.length) {
                    cells[i2].getMessageObject().resetLayout();
                    cells[i2].requestLayout();
                    i2++;
                }
            }
        }
        updateMenuItem();
        return true;
    }

    private void updateRows(boolean z) {
        int i = this.rowCount;
        int i2 = this.themeAccentListRow;
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
        for (int i3 = 0; i3 < size; i3++) {
            ThemeInfo themeInfo = (ThemeInfo) Theme.themes.get(i3);
            if (this.currentType != 0) {
                if (!themeInfo.isLight()) {
                    TL_theme tL_theme = themeInfo.info;
                    if (tL_theme != null && tL_theme.document == null) {
                    }
                }
            }
            if (themeInfo.pathToFile != null) {
                this.darkThemes.add(themeInfo);
            } else {
                this.defaultThemes.add(themeInfo);
            }
        }
        Collections.sort(this.defaultThemes, -$$Lambda$ThemeActivity$cs0N3OVBAa2T6bewE_YVZM-eTCA.INSTANCE);
        ThemesHorizontalListCell themesHorizontalListCell;
        if (this.currentType == 0) {
            size = this.rowCount;
            this.rowCount = size + 1;
            this.textSizeHeaderRow = size;
            size = this.rowCount;
            this.rowCount = size + 1;
            this.textSizeRow = size;
            size = this.rowCount;
            this.rowCount = size + 1;
            this.backgroundRow = size;
            size = this.rowCount;
            this.rowCount = size + 1;
            this.newThemeInfoRow = size;
            size = this.rowCount;
            this.rowCount = size + 1;
            this.themeHeaderRow = size;
            size = this.rowCount;
            this.rowCount = size + 1;
            this.themeListRow = size;
            this.hasThemeAccents = Theme.getCurrentTheme().hasAccentColors();
            themesHorizontalListCell = this.themesHorizontalListCell;
            if (themesHorizontalListCell != null) {
                themesHorizontalListCell.setDrawDivider(this.hasThemeAccents);
            }
            if (this.hasThemeAccents) {
                size = this.rowCount;
                this.rowCount = size + 1;
                this.themeAccentListRow = size;
            }
            size = this.rowCount;
            this.rowCount = size + 1;
            this.themeInfoRow = size;
            size = this.rowCount;
            this.rowCount = size + 1;
            this.chatListHeaderRow = size;
            size = this.rowCount;
            this.rowCount = size + 1;
            this.chatListRow = size;
            size = this.rowCount;
            this.rowCount = size + 1;
            this.chatListInfoRow = size;
            size = this.rowCount;
            this.rowCount = size + 1;
            this.settingsRow = size;
            size = this.rowCount;
            this.rowCount = size + 1;
            this.nightThemeRow = size;
            size = this.rowCount;
            this.rowCount = size + 1;
            this.customTabsRow = size;
            size = this.rowCount;
            this.rowCount = size + 1;
            this.directShareRow = size;
            size = this.rowCount;
            this.rowCount = size + 1;
            this.enableAnimationsRow = size;
            size = this.rowCount;
            this.rowCount = size + 1;
            this.emojiRow = size;
            size = this.rowCount;
            this.rowCount = size + 1;
            this.raiseToSpeakRow = size;
            size = this.rowCount;
            this.rowCount = size + 1;
            this.sendByEnterRow = size;
            size = this.rowCount;
            this.rowCount = size + 1;
            this.saveToGalleryRow = size;
            size = this.rowCount;
            this.rowCount = size + 1;
            this.distanceRow = size;
            size = this.rowCount;
            this.rowCount = size + 1;
            this.settings2Row = size;
            size = this.rowCount;
            this.rowCount = size + 1;
            this.stickersRow = size;
            size = this.rowCount;
            this.rowCount = size + 1;
            this.stickersSection2Row = size;
        } else {
            size = this.rowCount;
            this.rowCount = size + 1;
            this.nightDisabledRow = size;
            size = this.rowCount;
            this.rowCount = size + 1;
            this.nightScheduledRow = size;
            size = this.rowCount;
            this.rowCount = size + 1;
            this.nightAutomaticRow = size;
            if (VERSION.SDK_INT >= 29) {
                size = this.rowCount;
                this.rowCount = size + 1;
                this.nightSystemDefaultRow = size;
            }
            size = this.rowCount;
            this.rowCount = size + 1;
            this.nightTypeInfoRow = size;
            size = Theme.selectedAutoNightType;
            if (size == 1) {
                size = this.rowCount;
                this.rowCount = size + 1;
                this.scheduleHeaderRow = size;
                size = this.rowCount;
                this.rowCount = size + 1;
                this.scheduleLocationRow = size;
                if (Theme.autoNightScheduleByLocation) {
                    size = this.rowCount;
                    this.rowCount = size + 1;
                    this.scheduleUpdateLocationRow = size;
                    size = this.rowCount;
                    this.rowCount = size + 1;
                    this.scheduleLocationInfoRow = size;
                } else {
                    size = this.rowCount;
                    this.rowCount = size + 1;
                    this.scheduleFromRow = size;
                    size = this.rowCount;
                    this.rowCount = size + 1;
                    this.scheduleToRow = size;
                    size = this.rowCount;
                    this.rowCount = size + 1;
                    this.scheduleFromToInfoRow = size;
                }
            } else if (size == 2) {
                size = this.rowCount;
                this.rowCount = size + 1;
                this.automaticHeaderRow = size;
                size = this.rowCount;
                this.rowCount = size + 1;
                this.automaticBrightnessRow = size;
                size = this.rowCount;
                this.rowCount = size + 1;
                this.automaticBrightnessInfoRow = size;
            }
            if (Theme.selectedAutoNightType != 0) {
                size = this.rowCount;
                this.rowCount = size + 1;
                this.preferedHeaderRow = size;
                size = this.rowCount;
                this.rowCount = size + 1;
                this.themeListRow = size;
                this.hasThemeAccents = Theme.getCurrentNightTheme().hasAccentColors();
                themesHorizontalListCell = this.themesHorizontalListCell;
                if (themesHorizontalListCell != null) {
                    themesHorizontalListCell.setDrawDivider(this.hasThemeAccents);
                }
                if (this.hasThemeAccents) {
                    size = this.rowCount;
                    this.rowCount = size + 1;
                    this.themeAccentListRow = size;
                }
                size = this.rowCount;
                this.rowCount = size + 1;
                this.themeInfoRow = size;
            }
        }
        ListAdapter listAdapter = this.listAdapter;
        if (listAdapter != null) {
            int i4;
            if (this.currentType == 1) {
                int i5 = this.previousUpdatedType;
                int i6 = Theme.selectedAutoNightType;
                if (!(i5 == i6 || i5 == -1)) {
                    i4 = this.nightTypeInfoRow + 1;
                    i2 = 3;
                    if (i5 != i6) {
                        int i7 = 0;
                        while (i7 < 4) {
                            Holder holder = (Holder) this.listView.findViewHolderForAdapterPosition(i7);
                            if (holder != null) {
                                View view = holder.itemView;
                                if (view instanceof ThemeTypeCell) {
                                    ((ThemeTypeCell) view).setTypeChecked(i7 == Theme.selectedAutoNightType);
                                }
                            }
                            i7++;
                        }
                        int i8 = Theme.selectedAutoNightType;
                        if (i8 == 0) {
                            this.listAdapter.notifyItemRangeRemoved(i4, i - i4);
                        } else {
                            i = 5;
                            ListAdapter listAdapter2;
                            if (i8 == 1) {
                                i8 = this.previousUpdatedType;
                                if (i8 == 0) {
                                    this.listAdapter.notifyItemRangeInserted(i4, this.rowCount - i4);
                                } else if (i8 == 2) {
                                    this.listAdapter.notifyItemRangeRemoved(i4, 3);
                                    listAdapter2 = this.listAdapter;
                                    if (Theme.autoNightScheduleByLocation) {
                                        i = 4;
                                    }
                                    listAdapter2.notifyItemRangeInserted(i4, i);
                                } else if (i8 == 3) {
                                    listAdapter2 = this.listAdapter;
                                    if (Theme.autoNightScheduleByLocation) {
                                        i = 4;
                                    }
                                    listAdapter2.notifyItemRangeInserted(i4, i);
                                }
                            } else if (i8 == 2) {
                                i8 = this.previousUpdatedType;
                                if (i8 == 0) {
                                    this.listAdapter.notifyItemRangeInserted(i4, this.rowCount - i4);
                                } else if (i8 == 1) {
                                    ListAdapter listAdapter3 = this.listAdapter;
                                    if (Theme.autoNightScheduleByLocation) {
                                        i = 4;
                                    }
                                    listAdapter3.notifyItemRangeRemoved(i4, i);
                                    this.listAdapter.notifyItemRangeInserted(i4, 3);
                                } else if (i8 == 3) {
                                    this.listAdapter.notifyItemRangeInserted(i4, 3);
                                }
                            } else if (i8 == 3) {
                                i8 = this.previousUpdatedType;
                                if (i8 == 0) {
                                    this.listAdapter.notifyItemRangeInserted(i4, this.rowCount - i4);
                                } else if (i8 == 2) {
                                    this.listAdapter.notifyItemRangeRemoved(i4, 3);
                                } else if (i8 == 1) {
                                    listAdapter2 = this.listAdapter;
                                    if (Theme.autoNightScheduleByLocation) {
                                        i = 4;
                                    }
                                    listAdapter2.notifyItemRangeRemoved(i4, i);
                                }
                            }
                        }
                    } else {
                        boolean z2 = this.previousByLocation;
                        boolean z3 = Theme.autoNightScheduleByLocation;
                        if (z2 != z3) {
                            i4 += 2;
                            listAdapter.notifyItemRangeRemoved(i4, z3 ? 3 : 2);
                            ListAdapter listAdapter4 = this.listAdapter;
                            if (Theme.autoNightScheduleByLocation) {
                                i2 = 2;
                            }
                            listAdapter4.notifyItemRangeInserted(i4, i2);
                        }
                    }
                }
            }
            if (z || this.previousUpdatedType == -1) {
                ThemesHorizontalListCell themesHorizontalListCell2 = this.themesHorizontalListCell;
                if (themesHorizontalListCell2 != null) {
                    themesHorizontalListCell2.notifyDataSetChanged(this.listView.getWidth());
                }
                this.listAdapter.notifyDataSetChanged();
            } else {
                if (i2 == -1) {
                    i4 = this.themeAccentListRow;
                    if (i4 != -1) {
                        this.listAdapter.notifyItemInserted(i4);
                    }
                }
                if (i2 == -1 || this.themeAccentListRow != -1) {
                    i4 = this.themeAccentListRow;
                    if (i4 != -1) {
                        this.listAdapter.notifyItemChanged(i4);
                    }
                } else {
                    this.listAdapter.notifyItemRemoved(i2);
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
        ThemeAccent themeAccent;
        AlertDialog alertDialog;
        if (i == NotificationCenter.locationPermissionGranted) {
            updateSunTime(null, true);
        } else if (i == NotificationCenter.didSetNewWallpapper || i == NotificationCenter.emojiDidLoad) {
            RecyclerListView recyclerListView = this.listView;
            if (recyclerListView != null) {
                recyclerListView.invalidateViews();
            }
        } else if (i == NotificationCenter.themeAccentListUpdated) {
            ListAdapter listAdapter = this.listAdapter;
            if (listAdapter != null) {
                i2 = this.themeAccentListRow;
                if (i2 != -1) {
                    listAdapter.notifyItemChanged(i2, new Object());
                }
            }
        } else if (i == NotificationCenter.themeListUpdated) {
            updateRows(true);
        } else if (i == NotificationCenter.themeUploadedToServer) {
            ThemeInfo themeInfo = (ThemeInfo) objArr[0];
            themeAccent = (ThemeAccent) objArr[1];
            if (themeInfo == this.sharingTheme && themeAccent == this.sharingAccent) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("https://");
                stringBuilder.append(MessagesController.getInstance(this.currentAccount).linkPrefix);
                stringBuilder.append("/addtheme/");
                stringBuilder.append((themeAccent != null ? themeAccent.info : themeInfo.info).slug);
                String stringBuilder2 = stringBuilder.toString();
                showDialog(new ShareAlert(getParentActivity(), null, stringBuilder2, false, stringBuilder2, false));
                alertDialog = this.sharingProgressDialog;
                if (alertDialog != null) {
                    alertDialog.dismiss();
                }
            }
        } else if (i == NotificationCenter.themeUploadError) {
            themeAccent = (ThemeAccent) objArr[1];
            if (((ThemeInfo) objArr[0]) == this.sharingTheme && themeAccent == this.sharingAccent) {
                alertDialog = this.sharingProgressDialog;
                if (alertDialog == null) {
                    alertDialog.dismiss();
                }
            }
        } else if (i == NotificationCenter.needShareTheme) {
            if (getParentActivity() != null && !this.isPaused) {
                this.sharingTheme = (ThemeInfo) objArr[0];
                this.sharingAccent = (ThemeAccent) objArr[1];
                this.sharingProgressDialog = new AlertDialog(getParentActivity(), 3);
                this.sharingProgressDialog.setCanCacnel(true);
                showDialog(this.sharingProgressDialog, new -$$Lambda$ThemeActivity$oyONSSYRN1Vtk5NxWcatYkA4zK4(this));
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
            this.menuItem.addSubItem(2, NUM, LocaleController.getString("ShareTheme", NUM));
            this.menuItem.addSubItem(3, NUM, LocaleController.getString("EditThemeColors", NUM));
            this.menuItem.addSubItem(1, NUM, LocaleController.getString("CreateNewThemeMenu", NUM));
            this.menuItem.addSubItem(4, NUM, LocaleController.getString("ThemeResetToDefaults", NUM));
        } else {
            this.actionBar.setTitle(LocaleController.getString("AutoNightTheme", NUM));
        }
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    ThemeActivity.this.finishFragment();
                } else {
                    String str = "Cancel";
                    Builder builder;
                    if (i == 1) {
                        if (ThemeActivity.this.getParentActivity() != null) {
                            builder = new Builder(ThemeActivity.this.getParentActivity());
                            builder.setTitle(LocaleController.getString("NewTheme", NUM));
                            builder.setMessage(LocaleController.getString("CreateNewThemeAlert", NUM));
                            builder.setNegativeButton(LocaleController.getString(str, NUM), null);
                            builder.setPositiveButton(LocaleController.getString("CreateTheme", NUM), new -$$Lambda$ThemeActivity$1$ZQnhOSOAx8cfjiv91xqtf3q-RU0(this));
                            ThemeActivity.this.showDialog(builder.create());
                        }
                    } else if (i == 2) {
                        ThemeAccent accent = Theme.getCurrentTheme().getAccent(false);
                        if (accent.info == null) {
                            MessagesController.getInstance(ThemeActivity.this.currentAccount).saveThemeToServer(accent.parentTheme, accent);
                            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.needShareTheme, accent.parentTheme, accent);
                        } else {
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append("https://");
                            stringBuilder.append(MessagesController.getInstance(ThemeActivity.this.currentAccount).linkPrefix);
                            stringBuilder.append("/addtheme/");
                            stringBuilder.append(accent.info.slug);
                            String stringBuilder2 = stringBuilder.toString();
                            ThemeActivity themeActivity = ThemeActivity.this;
                            themeActivity.showDialog(new ShareAlert(themeActivity.getParentActivity(), null, stringBuilder2, false, stringBuilder2, false));
                        }
                    } else if (i == 3) {
                        ThemeInfo currentTheme = Theme.getCurrentTheme();
                        ThemeActivity.this.presentFragment(new ThemePreviewActivity(currentTheme, false, 1, currentTheme.getAccent(false).id >= 100, ThemeActivity.this.currentType == 1));
                    } else if (i == 4 && ThemeActivity.this.getParentActivity() != null) {
                        builder = new Builder(ThemeActivity.this.getParentActivity());
                        builder.setTitle(LocaleController.getString("ThemeResetToDefaultsTitle", NUM));
                        builder.setMessage(LocaleController.getString("ThemeResetToDefaultsText", NUM));
                        builder.setPositiveButton(LocaleController.getString("Reset", NUM), new -$$Lambda$ThemeActivity$1$ZjLqg5uqldYRZdTrS2y-olG7SJM(this));
                        builder.setNegativeButton(LocaleController.getString(str, NUM), null);
                        AlertDialog create = builder.create();
                        ThemeActivity.this.showDialog(create);
                        TextView textView = (TextView) create.getButton(-1);
                        if (textView != null) {
                            textView.setTextColor(Theme.getColor("dialogTextRed2"));
                        }
                    }
                }
            }

            public /* synthetic */ void lambda$onItemClick$0$ThemeActivity$1(DialogInterface dialogInterface, int i) {
                AlertsCreator.createThemeCreateDialog(ThemeActivity.this, 0, null, null);
            }

            public /* synthetic */ void lambda$onItemClick$1$ThemeActivity$1(DialogInterface dialogInterface, int i) {
                if (ThemeActivity.this.setFontSize(AndroidUtilities.isTablet() ? 18 : 16)) {
                    ThemeActivity.this.listAdapter.notifyItemChanged(ThemeActivity.this.textSizeRow, new Object());
                }
                if (ThemeActivity.this.themesHorizontalListCell != null) {
                    ThemeInfo theme = Theme.getTheme("Blue");
                    ThemeInfo currentTheme = Theme.getCurrentTheme();
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
        this.listView.setOnItemClickListener(new -$$Lambda$ThemeActivity$6AbNGVXM3fzlqnkK4ORVG2-WTt4(this));
        return this.fragmentView;
    }

    public /* synthetic */ void lambda$createView$5$ThemeActivity(View view, int i, float f, float f2) {
        SharedPreferences globalMainSettings;
        String str;
        boolean z;
        Editor edit;
        if (i == this.enableAnimationsRow) {
            globalMainSettings = MessagesController.getGlobalMainSettings();
            str = "view_animations";
            z = globalMainSettings.getBoolean(str, true);
            edit = globalMainSettings.edit();
            edit.putBoolean(str, z ^ 1);
            edit.commit();
            if (view instanceof TextCheckCell) {
                ((TextCheckCell) view).setChecked(z ^ 1);
            }
        } else {
            boolean z2 = false;
            if (i == this.backgroundRow) {
                presentFragment(new WallpapersListActivity(0));
            } else if (i == this.sendByEnterRow) {
                globalMainSettings = MessagesController.getGlobalMainSettings();
                str = "send_by_enter";
                z = globalMainSettings.getBoolean(str, false);
                edit = globalMainSettings.edit();
                edit.putBoolean(str, z ^ 1);
                edit.commit();
                if (view instanceof TextCheckCell) {
                    ((TextCheckCell) view).setChecked(z ^ 1);
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
            } else {
                String str2 = "Cancel";
                Builder builder;
                if (i == this.distanceRow) {
                    if (getParentActivity() != null) {
                        builder = new Builder(getParentActivity());
                        builder.setTitle(LocaleController.getString("DistanceUnitsTitle", NUM));
                        builder.setItems(new CharSequence[]{LocaleController.getString("DistanceUnitsAutomatic", NUM), LocaleController.getString("DistanceUnitsKilometers", NUM), LocaleController.getString("DistanceUnitsMiles", NUM)}, new -$$Lambda$ThemeActivity$za3_wgArrfAtv6MGtBg73fu6fik(this));
                        builder.setNegativeButton(LocaleController.getString(str2, NUM), null);
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
                    int i2;
                    if (i == this.contactsSortRow) {
                        if (getParentActivity() != null) {
                            builder = new Builder(getParentActivity());
                            builder.setTitle(LocaleController.getString("SortBy", NUM));
                            builder.setItems(new CharSequence[]{LocaleController.getString("Default", NUM), LocaleController.getString("SortFirstName", NUM), LocaleController.getString("SortLastName", NUM)}, new -$$Lambda$ThemeActivity$RjT8DJtE1iE_zTnZYaFUMd6MoZY(this, i));
                            builder.setNegativeButton(LocaleController.getString(str2, NUM), null);
                            showDialog(builder.create());
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
                        } else {
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
                            CharSequence currentNightThemeName = z2 ? Theme.getCurrentNightThemeName() : LocaleController.getString("AutoNightThemeOff", NUM);
                            if (z2) {
                                i2 = Theme.selectedAutoNightType;
                                if (i2 == 1) {
                                    str = LocaleController.getString("AutoNightScheduled", NUM);
                                } else if (i2 == 3) {
                                    str = LocaleController.getString("AutoNightSystemDefault", NUM);
                                } else {
                                    str = LocaleController.getString("AutoNightAdaptive", NUM);
                                }
                                StringBuilder stringBuilder = new StringBuilder();
                                stringBuilder.append(str);
                                stringBuilder.append(" ");
                                stringBuilder.append(currentNightThemeName);
                                currentNightThemeName = stringBuilder.toString();
                            }
                            notificationsCheckCell.setTextAndValueAndCheck(LocaleController.getString("AutoNightTheme", NUM), currentNightThemeName, z2, true);
                        }
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
                                updateSunTime(null, true);
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
                        Theme.autoNightScheduleByLocation ^= 1;
                        ((TextCheckCell) view).setChecked(Theme.autoNightScheduleByLocation);
                        updateRows(true);
                        if (Theme.autoNightScheduleByLocation) {
                            updateSunTime(null, true);
                        }
                        Theme.checkAutoNightThemeConditions();
                    } else if (i == this.scheduleFromRow || i == this.scheduleToRow) {
                        if (getParentActivity() != null) {
                            int i3;
                            if (i == this.scheduleFromRow) {
                                i2 = Theme.autoNightDayStartTime;
                                i3 = i2 / 60;
                            } else {
                                i2 = Theme.autoNightDayEndTime;
                                i3 = i2 / 60;
                            }
                            showDialog(new TimePickerDialog(getParentActivity(), new -$$Lambda$ThemeActivity$NM7fAI0FGrIygn_Tl1Tnvhrr91Y(this, i, (TextSettingsCell) view), i3, i2 - (i3 * 60), true));
                        }
                    } else if (i == this.scheduleUpdateLocationRow) {
                        updateSunTime(null, true);
                    }
                }
            }
        }
    }

    public /* synthetic */ void lambda$null$2$ThemeActivity(DialogInterface dialogInterface, int i) {
        SharedConfig.setDistanceSystemType(i);
        ViewHolder findViewHolderForAdapterPosition = this.listView.findViewHolderForAdapterPosition(this.distanceRow);
        if (findViewHolderForAdapterPosition != null) {
            this.listAdapter.onBindViewHolder(findViewHolderForAdapterPosition, this.distanceRow);
        }
    }

    public /* synthetic */ void lambda$null$3$ThemeActivity(int i, DialogInterface dialogInterface, int i2) {
        Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putInt("sortContactsBy", i2);
        edit.commit();
        ListAdapter listAdapter = this.listAdapter;
        if (listAdapter != null) {
            listAdapter.notifyItemChanged(i);
        }
    }

    public /* synthetic */ void lambda$null$4$ThemeActivity(int i, TextSettingsCell textSettingsCell, TimePicker timePicker, int i2, int i3) {
        int i4 = (i2 * 60) + i3;
        String str = "%02d:%02d";
        if (i == this.scheduleFromRow) {
            Theme.autoNightDayStartTime = i4;
            textSettingsCell.setTextAndValue(LocaleController.getString("AutoNightFrom", NUM), String.format(str, new Object[]{Integer.valueOf(i2), Integer.valueOf(i3)}), true);
            return;
        }
        Theme.autoNightDayEndTime = i4;
        textSettingsCell.setTextAndValue(LocaleController.getString("AutoNightTo", NUM), String.format(str, new Object[]{Integer.valueOf(i2), Integer.valueOf(i3)}), true);
    }

    public void onResume() {
        super.onResume();
        if (this.listAdapter != null) {
            updateRows(true);
        }
    }

    /* Access modifiers changed, original: protected */
    public void onTransitionAnimationEnd(boolean z, boolean z2) {
        if (z) {
            AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
            AndroidUtilities.setAdjustResizeToNothing(getParentActivity(), this.classGuid);
        }
    }

    private void updateMenuItem() {
        if (this.menuItem != null) {
            ThemeInfo currentTheme = Theme.getCurrentTheme();
            ThemeAccent accent = currentTheme.getAccent(false);
            ArrayList arrayList = currentTheme.themeAccents;
            if (arrayList == null || arrayList.isEmpty() || accent == null || accent.id < 100) {
                this.menuItem.hideSubItem(2);
                this.menuItem.hideSubItem(3);
            } else {
                this.menuItem.showSubItem(2);
                this.menuItem.showSubItem(3);
            }
            int i = AndroidUtilities.isTablet() ? 18 : 16;
            ThemeInfo currentTheme2 = Theme.getCurrentTheme();
            if (SharedConfig.fontSize == i && currentTheme2.firstAccentIsDefault && currentTheme2.currentAccentId == Theme.DEFALT_THEME_ACCENT_ID) {
                this.menuItem.hideSubItem(4);
            } else {
                this.menuItem.showSubItem(4);
            }
        }
    }

    private void updateSunTime(Location location, boolean z) {
        String str = "location";
        LocationManager locationManager = (LocationManager) ApplicationLoader.applicationContext.getSystemService(str);
        if (VERSION.SDK_INT >= 23) {
            Activity parentActivity = getParentActivity();
            if (parentActivity != null) {
                if (parentActivity.checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION") != 0) {
                    parentActivity.requestPermissions(new String[]{"android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"}, 2);
                    return;
                }
            }
        }
        String str2 = "gps";
        if (getParentActivity() != null) {
            if (getParentActivity().getPackageManager().hasSystemFeature("android.hardware.location.gps")) {
                try {
                    if (!((LocationManager) ApplicationLoader.applicationContext.getSystemService(str)).isProviderEnabled(str2)) {
                        Builder builder = new Builder(getParentActivity());
                        builder.setTitle(LocaleController.getString("GpsDisabledAlertTitle", NUM));
                        builder.setMessage(LocaleController.getString("GpsDisabledAlertText", NUM));
                        builder.setPositiveButton(LocaleController.getString("ConnectingToProxyEnable", NUM), new -$$Lambda$ThemeActivity$XVcHtg-4BK_c7car6Fs8oR62zjU(this));
                        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
                        showDialog(builder.create());
                        return;
                    }
                } catch (Exception e) {
                    FileLog.e(e);
                }
            } else {
                return;
            }
        }
        try {
            location = locationManager.getLastKnownLocation(str2);
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
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(System.currentTimeMillis());
        Theme.autoNightLastSunCheckDay = instance.get(5);
        Utilities.globalQueue.postRunnable(new -$$Lambda$ThemeActivity$fQ62mNO1U2a_gMLGY2n-QJTHR0E(this));
        Holder holder = (Holder) this.listView.findViewHolderForAdapterPosition(this.scheduleLocationInfoRow);
        if (holder != null) {
            View view = holder.itemView;
            if (view instanceof TextInfoPrivacyCell) {
                ((TextInfoPrivacyCell) view).setText(getLocationSunString());
            }
        }
        if (Theme.autoNightScheduleByLocation && Theme.selectedAutoNightType == 1) {
            Theme.checkAutoNightThemeConditions();
        }
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
            List fromLocation = new Geocoder(ApplicationLoader.applicationContext, Locale.getDefault()).getFromLocation(Theme.autoNightLocationLatitude, Theme.autoNightLocationLongitude, 1);
            if (fromLocation.size() > 0) {
                str = ((Address) fromLocation.get(0)).getLocality();
            }
        } catch (Exception unused) {
        }
        AndroidUtilities.runOnUIThread(new -$$Lambda$ThemeActivity$AOfSXT6-giBklCncjW_-WdBhAIk(this, str));
    }

    public /* synthetic */ void lambda$null$7$ThemeActivity(String str) {
        Theme.autoNightCityName = str;
        if (Theme.autoNightCityName == null) {
            Theme.autoNightCityName = String.format("(%.06f, %.06f)", new Object[]{Double.valueOf(Theme.autoNightLocationLatitude), Double.valueOf(Theme.autoNightLocationLongitude)});
        }
        Theme.saveAutoNightThemeConfig();
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            Holder holder = (Holder) recyclerListView.findViewHolderForAdapterPosition(this.scheduleUpdateLocationRow);
            if (holder != null) {
                View view = holder.itemView;
                if (view instanceof TextSettingsCell) {
                    ((TextSettingsCell) view).setTextAndValue(LocaleController.getString("AutoNightUpdateLocation", NUM), Theme.autoNightCityName, false);
                }
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
                FileLog.e(e);
            }
            try {
                locationManager.requestLocationUpdates("network", 1, 0.0f, this.networkLocationListener);
            } catch (Exception e2) {
                FileLog.e(e2);
            }
        }
    }

    private void stopLocationUpdate() {
        this.updatingLocation = false;
        LocationManager locationManager = (LocationManager) ApplicationLoader.applicationContext.getSystemService("location");
        locationManager.removeUpdates(this.gpsLocationListener);
        locationManager.removeUpdates(this.networkLocationListener);
    }

    private void showPermissionAlert(boolean z) {
        if (getParentActivity() != null) {
            Builder builder = new Builder(getParentActivity());
            builder.setTitle(LocaleController.getString("AppName", NUM));
            if (z) {
                builder.setMessage(LocaleController.getString("PermissionNoLocationPosition", NUM));
            } else {
                builder.setMessage(LocaleController.getString("PermissionNoLocation", NUM));
            }
            builder.setNegativeButton(LocaleController.getString("PermissionOpenSettings", NUM), new -$$Lambda$ThemeActivity$zmCWNR0z5CTNSpEYmswP5pqFJ2k(this));
            builder.setPositiveButton(LocaleController.getString("OK", NUM), null);
            showDialog(builder.create());
        }
    }

    public /* synthetic */ void lambda$showPermissionAlert$9$ThemeActivity(DialogInterface dialogInterface, int i) {
        if (getParentActivity() != null) {
            try {
                Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("package:");
                stringBuilder.append(ApplicationLoader.applicationContext.getPackageName());
                intent.setData(Uri.parse(stringBuilder.toString()));
                getParentActivity().startActivity(intent);
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    private String getLocationSunString() {
        int i = Theme.autoNightSunriseTime;
        i -= (i / 60) * 60;
        Object[] objArr = new Object[]{Integer.valueOf(r1), Integer.valueOf(i)};
        String format = String.format("%02d:%02d", objArr);
        int i2 = Theme.autoNightSunsetTime;
        i2 -= (i2 / 60) * 60;
        Object[] objArr2 = new Object[]{Integer.valueOf(r6), Integer.valueOf(i2)};
        return LocaleController.formatString("AutoNightUpdateLocationInfo", NUM, String.format("%02d:%02d", objArr2), format);
    }

    public ThemeDescription[] getThemeDescriptions() {
        ThemeDescription[] themeDescriptionArr = new ThemeDescription[61];
        themeDescriptionArr[0] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextSettingsCell.class, TextCheckCell.class, HeaderCell.class, BrightnessControlCell.class, ThemeTypeCell.class, TextSizeCell.class, ChatListCell.class, NotificationsCheckCell.class, ThemesHorizontalListCell.class, TintRecyclerListView.class}, null, null, null, "windowBackgroundWhite");
        themeDescriptionArr[1] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundGray");
        themeDescriptionArr[2] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault");
        themeDescriptionArr[3] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault");
        themeDescriptionArr[4] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon");
        themeDescriptionArr[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle");
        themeDescriptionArr[6] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector");
        themeDescriptionArr[7] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUBACKGROUND, null, null, null, null, "actionBarDefaultSubmenuBackground");
        themeDescriptionArr[8] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM, null, null, null, null, "actionBarDefaultSubmenuItem");
        themeDescriptionArr[9] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM | ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, "actionBarDefaultSubmenuItemIcon");
        themeDescriptionArr[10] = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21");
        themeDescriptionArr[11] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, "divider");
        themeDescriptionArr[12] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, "windowBackgroundGrayShadow");
        themeDescriptionArr[13] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, "windowBackgroundGrayShadow");
        View view = this.listView;
        Class[] clsArr = new Class[]{TextInfoPrivacyCell.class};
        String[] strArr = new String[1];
        strArr[0] = "textView";
        themeDescriptionArr[14] = new ThemeDescription(view, 0, clsArr, strArr, null, null, null, "windowBackgroundWhiteGrayText4");
        themeDescriptionArr[15] = new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText");
        view = this.listView;
        clsArr = new Class[]{TextSettingsCell.class};
        strArr = new String[1];
        strArr[0] = "valueTextView";
        themeDescriptionArr[16] = new ThemeDescription(view, 0, clsArr, strArr, null, null, null, "windowBackgroundWhiteValueText");
        themeDescriptionArr[17] = new ThemeDescription(this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlueHeader");
        themeDescriptionArr[18] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText");
        view = this.listView;
        clsArr = new Class[]{TextCheckCell.class};
        strArr = new String[1];
        strArr[0] = "checkBox";
        themeDescriptionArr[19] = new ThemeDescription(view, 0, clsArr, strArr, null, null, null, "switchTrack");
        themeDescriptionArr[20] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, "switchTrackChecked");
        view = this.listView;
        View view2 = view;
        themeDescriptionArr[21] = new ThemeDescription(view2, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{BrightnessControlCell.class}, new String[]{"leftImageView"}, null, null, null, "windowBackgroundWhiteGrayIcon");
        view = this.listView;
        view2 = view;
        themeDescriptionArr[22] = new ThemeDescription(view2, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{BrightnessControlCell.class}, new String[]{"rightImageView"}, null, null, null, "windowBackgroundWhiteGrayIcon");
        view = this.listView;
        clsArr = new Class[]{BrightnessControlCell.class};
        strArr = new String[1];
        strArr[0] = "seekBarView";
        themeDescriptionArr[23] = new ThemeDescription(view, 0, clsArr, strArr, null, null, null, "player_progressBackground");
        view = this.listView;
        view2 = view;
        themeDescriptionArr[24] = new ThemeDescription(view2, ThemeDescription.FLAG_PROGRESSBAR, new Class[]{BrightnessControlCell.class}, new String[]{"seekBarView"}, null, null, null, "player_progress");
        themeDescriptionArr[25] = new ThemeDescription(this.listView, 0, new Class[]{ThemeTypeCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText");
        themeDescriptionArr[26] = new ThemeDescription(this.listView, 0, new Class[]{ThemeTypeCell.class}, new String[]{"checkImage"}, null, null, null, "featuredStickers_addedIcon");
        view = this.listView;
        view2 = view;
        themeDescriptionArr[27] = new ThemeDescription(view2, ThemeDescription.FLAG_PROGRESSBAR, new Class[]{TextSizeCell.class}, new String[]{"sizeBar"}, null, null, null, "player_progress");
        themeDescriptionArr[28] = new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, new String[]{"sizeBar"}, null, null, null, "player_progressBackground");
        themeDescriptionArr[29] = new ThemeDescription(this.listView, 0, new Class[]{ChatListCell.class}, null, null, null, "radioBackground");
        themeDescriptionArr[30] = new ThemeDescription(this.listView, 0, new Class[]{ChatListCell.class}, null, null, null, "radioBackgroundChecked");
        themeDescriptionArr[31] = new ThemeDescription(this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText");
        themeDescriptionArr[32] = new ThemeDescription(this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"valueTextView"}, null, null, null, "windowBackgroundWhiteGrayText2");
        themeDescriptionArr[33] = new ThemeDescription(this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"checkBox"}, null, null, null, "switchTrack");
        themeDescriptionArr[34] = new ThemeDescription(this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"checkBox"}, null, null, null, "switchTrackChecked");
        themeDescriptionArr[35] = new ThemeDescription(this.listView, 0, null, null, new Drawable[]{Theme.chat_msgInDrawable, Theme.chat_msgInMediaDrawable}, null, "chat_inBubble");
        themeDescriptionArr[36] = new ThemeDescription(this.listView, 0, null, null, new Drawable[]{Theme.chat_msgInSelectedDrawable, Theme.chat_msgInMediaSelectedDrawable}, null, "chat_inBubbleSelected");
        themeDescriptionArr[37] = new ThemeDescription(this.listView, 0, null, null, new Drawable[]{Theme.chat_msgInShadowDrawable, Theme.chat_msgInMediaShadowDrawable}, null, "chat_inBubbleShadow");
        themeDescriptionArr[38] = new ThemeDescription(this.listView, 0, null, null, new Drawable[]{Theme.chat_msgOutDrawable, Theme.chat_msgOutMediaDrawable}, null, "chat_outBubble");
        themeDescriptionArr[39] = new ThemeDescription(this.listView, 0, null, null, new Drawable[]{Theme.chat_msgOutDrawable, Theme.chat_msgOutMediaDrawable}, null, "chat_outBubbleGradient");
        themeDescriptionArr[40] = new ThemeDescription(this.listView, 0, null, null, new Drawable[]{Theme.chat_msgOutSelectedDrawable, Theme.chat_msgOutMediaSelectedDrawable}, null, "chat_outBubbleSelected");
        themeDescriptionArr[41] = new ThemeDescription(this.listView, 0, null, null, new Drawable[]{Theme.chat_msgOutShadowDrawable, Theme.chat_msgOutMediaShadowDrawable}, null, "chat_outBubbleShadow");
        themeDescriptionArr[42] = new ThemeDescription(this.listView, 0, null, null, null, null, "chat_messageTextIn");
        themeDescriptionArr[43] = new ThemeDescription(this.listView, 0, null, null, null, null, "chat_messageTextOut");
        themeDescriptionArr[44] = new ThemeDescription(this.listView, 0, null, null, new Drawable[]{Theme.chat_msgOutCheckDrawable}, null, "chat_outSentCheck");
        themeDescriptionArr[45] = new ThemeDescription(this.listView, 0, null, null, new Drawable[]{Theme.chat_msgOutCheckSelectedDrawable}, null, "chat_outSentCheckSelected");
        themeDescriptionArr[46] = new ThemeDescription(this.listView, 0, null, null, new Drawable[]{Theme.chat_msgOutCheckReadDrawable, Theme.chat_msgOutHalfCheckDrawable}, null, "chat_outSentCheckRead");
        themeDescriptionArr[47] = new ThemeDescription(this.listView, 0, null, null, new Drawable[]{Theme.chat_msgOutCheckReadSelectedDrawable, Theme.chat_msgOutHalfCheckSelectedDrawable}, null, "chat_outSentCheckReadSelected");
        themeDescriptionArr[48] = new ThemeDescription(this.listView, 0, null, null, new Drawable[]{Theme.chat_msgMediaCheckDrawable, Theme.chat_msgMediaHalfCheckDrawable}, null, "chat_mediaSentCheck");
        themeDescriptionArr[49] = new ThemeDescription(this.listView, 0, null, null, null, null, "chat_inReplyLine");
        themeDescriptionArr[50] = new ThemeDescription(this.listView, 0, null, null, null, null, "chat_outReplyLine");
        themeDescriptionArr[51] = new ThemeDescription(this.listView, 0, null, null, null, null, "chat_inReplyNameText");
        themeDescriptionArr[52] = new ThemeDescription(this.listView, 0, null, null, null, null, "chat_outReplyNameText");
        themeDescriptionArr[53] = new ThemeDescription(this.listView, 0, null, null, null, null, "chat_inReplyMessageText");
        themeDescriptionArr[54] = new ThemeDescription(this.listView, 0, null, null, null, null, "chat_outReplyMessageText");
        themeDescriptionArr[55] = new ThemeDescription(this.listView, 0, null, null, null, null, "chat_inReplyMediaMessageSelectedText");
        themeDescriptionArr[56] = new ThemeDescription(this.listView, 0, null, null, null, null, "chat_outReplyMediaMessageSelectedText");
        themeDescriptionArr[57] = new ThemeDescription(this.listView, 0, null, null, null, null, "chat_inTimeText");
        themeDescriptionArr[58] = new ThemeDescription(this.listView, 0, null, null, null, null, "chat_outTimeText");
        themeDescriptionArr[59] = new ThemeDescription(this.listView, 0, null, null, null, null, "chat_inTimeSelectedText");
        themeDescriptionArr[60] = new ThemeDescription(this.listView, 0, null, null, null, null, "chat_outTimeSelectedText");
        return themeDescriptionArr;
    }
}
