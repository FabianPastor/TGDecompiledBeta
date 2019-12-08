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
import android.graphics.Color;
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
import android.os.Vibrator;
import android.text.TextPaint;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.annotation.Keep;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView.LayoutParams;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.support.ArrayUtils;
import org.telegram.messenger.time.SunDate;
import org.telegram.tgnet.TLRPC.TL_theme;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
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
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;
import org.telegram.ui.Components.SeekBarView;
import org.telegram.ui.Components.ThemeEditorView;

public class ThemeActivity extends BaseFragment implements NotificationCenterDelegate {
    public static final int THEME_TYPE_BASIC = 0;
    public static final int THEME_TYPE_NIGHT = 1;
    public static final int THEME_TYPE_OTHER = 2;
    private static final int create_theme = 1;
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
    private GpsLocationListener networkLocationListener = new GpsLocationListener(this, null);
    private int newThemeInfoRow;
    private int nightAutomaticRow;
    private int nightDisabledRow;
    private int nightScheduledRow;
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
        private int currentColor;
        private ThemeInfo currentTheme;
        private final Paint paint = new Paint(1);

        InnerAccentView(Context context) {
            super(context);
        }

        /* Access modifiers changed, original: 0000 */
        public void setThemeAndColor(ThemeInfo themeInfo, int i) {
            this.currentTheme = themeInfo;
            this.currentColor = i;
            updateCheckedState(false);
        }

        /* Access modifiers changed, original: 0000 */
        public void updateCheckedState(boolean z) {
            Object obj = this.currentTheme.accentColor == this.currentColor ? 1 : null;
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
            this.paint.setColor(this.currentColor);
            this.paint.setStyle(Style.STROKE);
            this.paint.setStrokeWidth((float) AndroidUtilities.dp(3.0f));
            this.paint.setAlpha(Math.round(this.checkedState * 255.0f));
            canvas.drawCircle(((float) getMeasuredWidth()) * 0.5f, ((float) getMeasuredHeight()) * 0.5f, dp - (this.paint.getStrokeWidth() * 0.5f), this.paint);
            this.paint.setAlpha(255);
            this.paint.setStyle(Style.FILL);
            canvas.drawCircle(((float) getMeasuredWidth()) * 0.5f, ((float) getMeasuredHeight()) * 0.5f, dp - (((float) AndroidUtilities.dp(5.0f)) * this.checkedState), this.paint);
        }
    }

    private static class InnerCustomAccentView extends View {
        private int[] colors = new int[7];
        private final Paint paint = new Paint(1);

        InnerCustomAccentView(Context context) {
            super(context);
        }

        private void setTheme(ThemeInfo themeInfo) {
            int[] iArr = themeInfo == null ? null : themeInfo.accentColorOptions;
            if (iArr == null || iArr.length < 8) {
                this.colors = new int[7];
            } else {
                this.colors = new int[]{iArr[6], iArr[4], iArr[7], iArr[2], iArr[0], iArr[5], iArr[3]};
            }
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
            this.sizeBar.setDelegate(new -$$Lambda$ThemeActivity$TextSizeCell$Ci0_0LdqTC4U6xi9evAA0pUhylM(this));
            addView(this.sizeBar, LayoutHelper.createFrame(-1, 38.0f, 51, 9.0f, 5.0f, 43.0f, 0.0f));
            this.messagesCell = new ThemePreviewMessagesCell(context, ThemeActivity.this.parentLayout, 0);
            addView(this.messagesCell, LayoutHelper.createFrame(-1, -2.0f, 51, 0.0f, 53.0f, 0.0f, 0.0f));
        }

        public /* synthetic */ void lambda$new$0$ThemeActivity$TextSizeCell(float f) {
            int i = this.startFontSize;
            int round = Math.round(((float) i) + (((float) (this.endFontSize - i)) * f));
            if (round != SharedConfig.fontSize) {
                SharedConfig.fontSize = round;
                Editor edit = MessagesController.getGlobalMainSettings().edit();
                edit.putInt("fons_size", SharedConfig.fontSize);
                edit.commit();
                Theme.chat_msgTextPaint.setTextSize((float) AndroidUtilities.dp((float) SharedConfig.fontSize));
                round = ThemeActivity.this.layoutManager.findFirstVisibleItemPosition();
                View findViewByPosition = round != -1 ? ThemeActivity.this.layoutManager.findViewByPosition(round) : null;
                int top = findViewByPosition != null ? findViewByPosition.getTop() : 0;
                ChatMessageCell[] cells = this.messagesCell.getCells();
                for (int i2 = 0; i2 < cells.length; i2++) {
                    cells[i2].getMessageObject().resetLayout();
                    cells[i2].requestLayout();
                }
                if (findViewByPosition != null) {
                    ThemeActivity.this.layoutManager.scrollToPositionWithOffset(round, top);
                }
            }
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
            i = MeasureSpec.getSize(i);
            if (this.lastWidth != i) {
                SeekBarView seekBarView = this.sizeBar;
                int i3 = SharedConfig.fontSize;
                int i4 = this.startFontSize;
                seekBarView.setProgress(((float) (i3 - i4)) / ((float) (this.endFontSize - i4)));
                this.lastWidth = i;
            }
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
                int i = 0;
                String str2 = null;
                if (themeInfo.pathToFile == null) {
                    charSequenceArr = new CharSequence[]{null, LocaleController.getString(str, NUM)};
                    iArr = new int[]{0, NUM};
                } else {
                    String string;
                    TL_theme tL_theme = themeInfo.info;
                    int i2 = (tL_theme == null || !tL_theme.isDefault) ? 1 : 0;
                    CharSequence[] charSequenceArr2 = new CharSequence[5];
                    charSequenceArr2[0] = themeInfo.info != null ? LocaleController.getString("ShareFile", NUM) : null;
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

        /* JADX WARNING: Missing exception handler attribute for start block: B:55:0x011e */
        /* JADX WARNING: Removed duplicated region for block: B:50:0x00f7 A:{Catch:{ Exception -> 0x0143 }} */
        /* JADX WARNING: Removed duplicated region for block: B:49:0x00f6 A:{RETURN, Catch:{ Exception -> 0x0143 }} */
        /* JADX WARNING: Removed duplicated region for block: B:36:0x00c5 A:{SYNTHETIC, Splitter:B:36:0x00c5} */
        /* JADX WARNING: Removed duplicated region for block: B:31:0x00ba A:{SYNTHETIC, Splitter:B:31:0x00ba} */
        /* JADX WARNING: Removed duplicated region for block: B:49:0x00f6 A:{RETURN, Catch:{ Exception -> 0x0143 }} */
        /* JADX WARNING: Removed duplicated region for block: B:50:0x00f7 A:{Catch:{ Exception -> 0x0143 }} */
        /* JADX WARNING: Can't wrap try/catch for region: R(4:53|54|55|56) */
        public /* synthetic */ void lambda$showOptionsForTheme$1$ThemeActivity$ListAdapter(org.telegram.ui.ActionBar.Theme.ThemeInfo r8, android.content.DialogInterface r9, int r10) {
            /*
            r7 = this;
            r9 = org.telegram.ui.ThemeActivity.this;
            r9 = r9.getParentActivity();
            if (r9 != 0) goto L_0x0009;
        L_0x0008:
            return;
        L_0x0009:
            if (r10 != 0) goto L_0x0049;
        L_0x000b:
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
            goto L_0x01d0;
        L_0x0049:
            r9 = 0;
            r0 = 1;
            if (r10 != r0) goto L_0x0149;
        L_0x004d:
            r10 = r8.pathToFile;
            if (r10 != 0) goto L_0x00ce;
        L_0x0051:
            r10 = r8.assetName;
            if (r10 != 0) goto L_0x00ce;
        L_0x0055:
            r8 = new java.lang.StringBuilder;
            r8.<init>();
            r10 = org.telegram.ui.ActionBar.Theme.getDefaultColors();
            r10 = r10.entrySet();
            r10 = r10.iterator();
        L_0x0066:
            r1 = r10.hasNext();
            if (r1 == 0) goto L_0x008d;
        L_0x006c:
            r1 = r10.next();
            r1 = (java.util.Map.Entry) r1;
            r2 = r1.getKey();
            r2 = (java.lang.String) r2;
            r8.append(r2);
            r2 = "=";
            r8.append(r2);
            r1 = r1.getValue();
            r8.append(r1);
            r1 = "\n";
            r8.append(r1);
            goto L_0x0066;
        L_0x008d:
            r10 = new java.io.File;
            r1 = org.telegram.messenger.ApplicationLoader.getFilesDirFixed();
            r2 = "default_theme.attheme";
            r10.<init>(r1, r2);
            r1 = new java.io.FileOutputStream;	 Catch:{ Exception -> 0x00b4 }
            r1.<init>(r10);	 Catch:{ Exception -> 0x00b4 }
            r8 = r8.toString();	 Catch:{ Exception -> 0x00af, all -> 0x00ac }
            r8 = org.telegram.messenger.AndroidUtilities.getStringBytes(r8);	 Catch:{ Exception -> 0x00af, all -> 0x00ac }
            r1.write(r8);	 Catch:{ Exception -> 0x00af, all -> 0x00ac }
            r1.close();	 Catch:{ Exception -> 0x00be }
            goto L_0x00de;
        L_0x00ac:
            r8 = move-exception;
            r9 = r1;
            goto L_0x00c3;
        L_0x00af:
            r8 = move-exception;
            r9 = r1;
            goto L_0x00b5;
        L_0x00b2:
            r8 = move-exception;
            goto L_0x00c3;
        L_0x00b4:
            r8 = move-exception;
        L_0x00b5:
            org.telegram.messenger.FileLog.e(r8);	 Catch:{ all -> 0x00b2 }
            if (r9 == 0) goto L_0x00de;
        L_0x00ba:
            r9.close();	 Catch:{ Exception -> 0x00be }
            goto L_0x00de;
        L_0x00be:
            r8 = move-exception;
            org.telegram.messenger.FileLog.e(r8);
            goto L_0x00de;
        L_0x00c3:
            if (r9 == 0) goto L_0x00cd;
        L_0x00c5:
            r9.close();	 Catch:{ Exception -> 0x00c9 }
            goto L_0x00cd;
        L_0x00c9:
            r9 = move-exception;
            org.telegram.messenger.FileLog.e(r9);
        L_0x00cd:
            throw r8;
        L_0x00ce:
            r9 = r8.assetName;
            if (r9 == 0) goto L_0x00d7;
        L_0x00d2:
            r10 = org.telegram.ui.ActionBar.Theme.getAssetFile(r9);
            goto L_0x00de;
        L_0x00d7:
            r10 = new java.io.File;
            r8 = r8.pathToFile;
            r10.<init>(r8);
        L_0x00de:
            r8 = new java.io.File;
            r9 = 4;
            r9 = org.telegram.messenger.FileLoader.getDirectory(r9);
            r1 = r10.getName();
            r1 = org.telegram.messenger.FileLoader.fixFileName(r1);
            r8.<init>(r9, r1);
            r9 = org.telegram.messenger.AndroidUtilities.copyFile(r10, r8);	 Catch:{ Exception -> 0x0143 }
            if (r9 != 0) goto L_0x00f7;
        L_0x00f6:
            return;
        L_0x00f7:
            r9 = new android.content.Intent;	 Catch:{ Exception -> 0x0143 }
            r10 = "android.intent.action.SEND";
            r9.<init>(r10);	 Catch:{ Exception -> 0x0143 }
            r10 = "text/xml";
            r9.setType(r10);	 Catch:{ Exception -> 0x0143 }
            r10 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0143 }
            r1 = 24;
            r2 = "android.intent.extra.STREAM";
            if (r10 < r1) goto L_0x0126;
        L_0x010b:
            r10 = org.telegram.ui.ThemeActivity.this;	 Catch:{ Exception -> 0x011e }
            r10 = r10.getParentActivity();	 Catch:{ Exception -> 0x011e }
            r1 = "org.telegram.messenger.beta.provider";
            r10 = androidx.core.content.FileProvider.getUriForFile(r10, r1, r8);	 Catch:{ Exception -> 0x011e }
            r9.putExtra(r2, r10);	 Catch:{ Exception -> 0x011e }
            r9.setFlags(r0);	 Catch:{ Exception -> 0x011e }
            goto L_0x012d;
        L_0x011e:
            r8 = android.net.Uri.fromFile(r8);	 Catch:{ Exception -> 0x0143 }
            r9.putExtra(r2, r8);	 Catch:{ Exception -> 0x0143 }
            goto L_0x012d;
        L_0x0126:
            r8 = android.net.Uri.fromFile(r8);	 Catch:{ Exception -> 0x0143 }
            r9.putExtra(r2, r8);	 Catch:{ Exception -> 0x0143 }
        L_0x012d:
            r8 = org.telegram.ui.ThemeActivity.this;	 Catch:{ Exception -> 0x0143 }
            r10 = "ShareFile";
            r0 = NUM; // 0x7f0d09bc float:1.874717E38 double:1.053131009E-314;
            r10 = org.telegram.messenger.LocaleController.getString(r10, r0);	 Catch:{ Exception -> 0x0143 }
            r9 = android.content.Intent.createChooser(r9, r10);	 Catch:{ Exception -> 0x0143 }
            r10 = 500; // 0x1f4 float:7.0E-43 double:2.47E-321;
            r8.startActivityForResult(r9, r10);	 Catch:{ Exception -> 0x0143 }
            goto L_0x01d0;
        L_0x0143:
            r8 = move-exception;
            org.telegram.messenger.FileLog.e(r8);
            goto L_0x01d0;
        L_0x0149:
            r1 = 2;
            if (r10 != r1) goto L_0x016f;
        L_0x014c:
            r9 = org.telegram.ui.ThemeActivity.this;
            r9 = r9.parentLayout;
            if (r9 == 0) goto L_0x01d0;
        L_0x0154:
            org.telegram.ui.ActionBar.Theme.applyTheme(r8);
            r9 = org.telegram.ui.ThemeActivity.this;
            r9 = r9.parentLayout;
            r9.rebuildAllFragmentViews(r0, r0);
            r9 = new org.telegram.ui.Components.ThemeEditorView;
            r9.<init>();
            r10 = org.telegram.ui.ThemeActivity.this;
            r10 = r10.getParentActivity();
            r9.show(r10, r8);
            goto L_0x01d0;
        L_0x016f:
            r0 = 3;
            if (r10 != r0) goto L_0x017e;
        L_0x0172:
            r9 = org.telegram.ui.ThemeActivity.this;
            r10 = new org.telegram.ui.ThemeSetUrlActivity;
            r0 = 0;
            r10.<init>(r8, r0);
            r9.presentFragment(r10);
            goto L_0x01d0;
        L_0x017e:
            r10 = org.telegram.ui.ThemeActivity.this;
            r10 = r10.getParentActivity();
            if (r10 != 0) goto L_0x0187;
        L_0x0186:
            return;
        L_0x0187:
            r10 = new org.telegram.ui.ActionBar.AlertDialog$Builder;
            r0 = org.telegram.ui.ThemeActivity.this;
            r0 = r0.getParentActivity();
            r10.<init>(r0);
            r0 = NUM; // 0x7f0d0372 float:1.8743904E38 double:1.0531302133E-314;
            r1 = "DeleteThemeAlert";
            r0 = org.telegram.messenger.LocaleController.getString(r1, r0);
            r10.setMessage(r0);
            r0 = NUM; // 0x7f0d00ef float:1.87426E38 double:1.0531298956E-314;
            r1 = "AppName";
            r0 = org.telegram.messenger.LocaleController.getString(r1, r0);
            r10.setTitle(r0);
            r0 = NUM; // 0x7f0d0351 float:1.8743837E38 double:1.053130197E-314;
            r1 = "Delete";
            r0 = org.telegram.messenger.LocaleController.getString(r1, r0);
            r1 = new org.telegram.ui.-$$Lambda$ThemeActivity$ListAdapter$HjGrFd2877SP2gFmUCLASSNAMEvuRyOmw;
            r1.<init>(r7, r8);
            r10.setPositiveButton(r0, r1);
            r8 = NUM; // 0x7f0d01f7 float:1.8743135E38 double:1.053130026E-314;
            r0 = "Cancel";
            r8 = org.telegram.messenger.LocaleController.getString(r0, r8);
            r10.setNegativeButton(r8, r9);
            r8 = org.telegram.ui.ThemeActivity.this;
            r9 = r10.create();
            r8.showDialog(r9);
        L_0x01d0:
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ThemeActivity$ListAdapter.lambda$showOptionsForTheme$1$ThemeActivity$ListAdapter(org.telegram.ui.ActionBar.Theme$ThemeInfo, android.content.DialogInterface, int):void");
        }

        public /* synthetic */ void lambda$null$0$ThemeActivity$ListAdapter(ThemeInfo themeInfo, DialogInterface dialogInterface, int i) {
            ThemeActivity.this.getMessagesController().saveTheme(themeInfo, themeInfo == Theme.getCurrentNightTheme(), true);
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
                    textSettingsCell = ThemeActivity.this.themesHorizontalListCell;
                    textSettingsCell.setLayoutParams(new LayoutParams(-1, AndroidUtilities.dp(148.0f)));
                    break;
                default:
                    textSettingsCell = new TintRecyclerListView(this.mContext) {
                        public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                            if (!(getParent() == null || getParent().getParent() == null)) {
                                getParent().getParent().requestDisallowInterceptTouchEvent(true);
                            }
                            return super.onInterceptTouchEvent(motionEvent);
                        }
                    };
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
                    textSettingsCell.setLayoutParams(new LayoutParams(-1, AndroidUtilities.dp(62.0f)));
                    break;
            }
            return new Holder(textSettingsCell);
        }

        public /* synthetic */ void lambda$onCreateViewHolder$2$ThemeActivity$ListAdapter(ThemeAccentsListAdapter themeAccentsListAdapter, RecyclerListView recyclerListView, View view, int i) {
            int accentColor;
            ThemeInfo currentNightTheme = ThemeActivity.this.currentType == 1 ? Theme.getCurrentNightTheme() : Theme.getCurrentTheme();
            int i2 = 0;
            if (i == themeAccentsListAdapter.getItemCount() - 1) {
                ThemeActivity themeActivity = ThemeActivity.this;
                themeActivity.presentFragment(new ThemePreviewActivity(currentNightTheme, false, 1, themeActivity.currentType == 1));
            } else {
                accentColor = themeAccentsListAdapter.getAccentColor(i);
                if (currentNightTheme.accentColor != accentColor) {
                    Theme.saveThemeAccent(currentNightTheme, accentColor);
                    NotificationCenter globalInstance = NotificationCenter.getGlobalInstance();
                    i = NotificationCenter.needSetDayNightTheme;
                    Object[] objArr = new Object[2];
                    objArr[0] = currentNightTheme;
                    objArr[1] = Boolean.valueOf(ThemeActivity.this.currentType == 1);
                    globalInstance.postNotificationName(i, objArr);
                }
            }
            accentColor = view.getLeft();
            int right = view.getRight();
            i = AndroidUtilities.dp(52.0f);
            accentColor -= i;
            if (accentColor < 0) {
                recyclerListView.smoothScrollBy(accentColor, 0);
            } else {
                right += i;
                if (right > recyclerListView.getMeasuredWidth()) {
                    recyclerListView.smoothScrollBy(right - recyclerListView.getMeasuredWidth(), 0);
                }
            }
            accentColor = recyclerListView.getChildCount();
            while (i2 < accentColor) {
                view = recyclerListView.getChildAt(i2);
                if (view instanceof InnerAccentView) {
                    ((InnerAccentView) view).updateCheckedState(true);
                }
                i2++;
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
            int findCurrentAccent;
            String string;
            switch (viewHolder.getItemViewType()) {
                case 1:
                    TextSettingsCell textSettingsCell = (TextSettingsCell) viewHolder.itemView;
                    if (i != ThemeActivity.this.nightThemeRow) {
                        str5 = "%02d:%02d";
                        if (i == ThemeActivity.this.scheduleFromRow) {
                            i = Theme.autoNightDayStartTime;
                            i -= (i / 60) * 60;
                            textSettingsCell.setTextAndValue(LocaleController.getString("AutoNightFrom", NUM), String.format(str5, new Object[]{Integer.valueOf(findCurrentAccent), Integer.valueOf(i)}), true);
                            return;
                        } else if (i == ThemeActivity.this.scheduleToRow) {
                            i = Theme.autoNightDayEndTime;
                            i -= (i / 60) * 60;
                            textSettingsCell.setTextAndValue(LocaleController.getString("AutoNightTo", NUM), String.format(str5, new Object[]{Integer.valueOf(findCurrentAccent), Integer.valueOf(i)}), false);
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
                        if (Theme.selectedAutoNightType != 2) {
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
                            String string2 = Theme.selectedAutoNightType == 1 ? LocaleController.getString(str2, NUM) : LocaleController.getString(str, NUM);
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
                    findCurrentAccent = themeAccentsListAdapter.findCurrentAccent();
                    if (findCurrentAccent == -1) {
                        findCurrentAccent = themeAccentsListAdapter.getItemCount() - 1;
                    }
                    if (findCurrentAccent != -1) {
                        ((LinearLayoutManager) recyclerListView.getLayoutManager()).scrollToPositionWithOffset(findCurrentAccent, (ThemeActivity.this.listView.getMeasuredWidth() / 2) - AndroidUtilities.dp(42.0f));
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
            if (i == ThemeActivity.this.nightDisabledRow || i == ThemeActivity.this.nightScheduledRow || i == ThemeActivity.this.nightAutomaticRow) {
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
        private int extraColor;
        private boolean hasExtraColor;
        private Context mContext;
        private int[] options;

        public boolean isEnabled(ViewHolder viewHolder) {
            return false;
        }

        ThemeAccentsListAdapter(Context context) {
            this.mContext = context;
            setHasStableIds(true);
            notifyDataSetChanged();
        }

        public void notifyDataSetChanged() {
            this.currentTheme = ThemeActivity.this.currentType == 1 ? Theme.getCurrentNightTheme() : Theme.getCurrentTheme();
            ThemeInfo themeInfo = this.currentTheme;
            this.options = themeInfo.accentColorOptions;
            int[] iArr = this.options;
            if (iArr != null && ArrayUtils.indexOf(iArr, themeInfo.accentColor) == -1) {
                this.extraColor = this.currentTheme.accentColor;
                this.hasExtraColor = true;
            }
            super.notifyDataSetChanged();
        }

        public long getItemId(int i) {
            return (long) getAccentColor(i);
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
                ((InnerAccentView) viewHolder.itemView).setThemeAndColor(this.currentTheme, getAccentColor(i));
            } else if (itemViewType == 1) {
                ((InnerCustomAccentView) viewHolder.itemView).setTheme(this.currentTheme);
            }
        }

        public int getItemCount() {
            int[] iArr = this.options;
            return iArr == null ? 0 : (iArr.length + this.hasExtraColor) + 1;
        }

        /* Access modifiers changed, original: 0000 */
        public int getAccentColor(int i) {
            int[] iArr = this.options;
            if (iArr == null) {
                return 0;
            }
            if (this.hasExtraColor && i == iArr.length) {
                return this.extraColor;
            }
            iArr = this.options;
            if (i < iArr.length) {
                return iArr[i];
            }
            return 0;
        }

        /* Access modifiers changed, original: 0000 */
        public int findCurrentAccent() {
            if (this.hasExtraColor && this.extraColor == this.currentTheme.accentColor) {
                return this.options.length;
            }
            return ArrayUtils.indexOf(this.options, this.currentTheme.accentColor);
        }
    }

    private static abstract class TintRecyclerListView extends RecyclerListView {
        TintRecyclerListView(Context context) {
            super(context);
        }
    }

    static /* synthetic */ void lambda$openThemeCreate$5(DialogInterface dialogInterface, int i) {
    }

    public ThemeActivity(int i) {
        this.currentType = i;
        updateRows(true);
    }

    private void updateRows(boolean z) {
        int i;
        int i2 = this.rowCount;
        int i3 = this.themeAccentListRow;
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
        int size;
        ThemeInfo themeInfo;
        ThemesHorizontalListCell themesHorizontalListCell;
        if (this.currentType == 0) {
            this.defaultThemes.clear();
            this.darkThemes.clear();
            size = Theme.themes.size();
            for (i = 0; i < size; i++) {
                themeInfo = (ThemeInfo) Theme.themes.get(i);
                if (themeInfo.pathToFile != null) {
                    this.darkThemes.add(themeInfo);
                } else {
                    this.defaultThemes.add(themeInfo);
                }
            }
            Collections.sort(this.defaultThemes, -$$Lambda$ThemeActivity$cs0N3OVBAa2T6bewE_YVZM-eTCA.INSTANCE);
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
            this.hasThemeAccents = Theme.getCurrentTheme().accentColorOptions != null;
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
            this.darkThemes.clear();
            size = Theme.themes.size();
            for (i = 0; i < size; i++) {
                themeInfo = (ThemeInfo) Theme.themes.get(i);
                if (!themeInfo.isLight()) {
                    TL_theme tL_theme = themeInfo.info;
                    if (tL_theme == null || tL_theme.document != null) {
                        this.darkThemes.add(themeInfo);
                    }
                }
            }
            size = this.rowCount;
            this.rowCount = size + 1;
            this.nightDisabledRow = size;
            size = this.rowCount;
            this.rowCount = size + 1;
            this.nightScheduledRow = size;
            size = this.rowCount;
            this.rowCount = size + 1;
            this.nightAutomaticRow = size;
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
                this.hasThemeAccents = Theme.getCurrentNightTheme().accentColorOptions != null;
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
                i = this.previousUpdatedType;
                int i5 = Theme.selectedAutoNightType;
                if (!(i == i5 || i == -1)) {
                    i4 = this.nightTypeInfoRow + 1;
                    i3 = 3;
                    if (i != i5) {
                        int i6 = 0;
                        while (i6 < 3) {
                            Holder holder = (Holder) this.listView.findViewHolderForAdapterPosition(i6);
                            if (holder != null) {
                                ((ThemeTypeCell) holder.itemView).setTypeChecked(i6 == Theme.selectedAutoNightType);
                            }
                            i6++;
                        }
                        int i7 = Theme.selectedAutoNightType;
                        if (i7 == 0) {
                            this.listAdapter.notifyItemRangeRemoved(i4, i2 - i4);
                        } else {
                            i2 = 4;
                            if (i7 == 1) {
                                i7 = this.previousUpdatedType;
                                if (i7 == 0) {
                                    this.listAdapter.notifyItemRangeInserted(i4, this.rowCount - i4);
                                } else if (i7 == 2) {
                                    this.listAdapter.notifyItemRangeRemoved(i4, 3);
                                    ListAdapter listAdapter2 = this.listAdapter;
                                    if (!Theme.autoNightScheduleByLocation) {
                                        i2 = 5;
                                    }
                                    listAdapter2.notifyItemRangeInserted(i4, i2);
                                }
                            } else if (i7 == 2) {
                                i7 = this.previousUpdatedType;
                                if (i7 == 0) {
                                    this.listAdapter.notifyItemRangeInserted(i4, this.rowCount - i4);
                                } else if (i7 == 1) {
                                    ListAdapter listAdapter3 = this.listAdapter;
                                    if (!Theme.autoNightScheduleByLocation) {
                                        i2 = 5;
                                    }
                                    listAdapter3.notifyItemRangeRemoved(i4, i2);
                                    this.listAdapter.notifyItemRangeInserted(i4, 3);
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
                                i3 = 2;
                            }
                            listAdapter4.notifyItemRangeInserted(i4, i3);
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
                if (i3 == -1) {
                    i4 = this.themeAccentListRow;
                    if (i4 != -1) {
                        this.listAdapter.notifyItemInserted(i4);
                    }
                }
                if (i3 == -1 || this.themeAccentListRow != -1) {
                    i4 = this.themeAccentListRow;
                    if (i4 != -1) {
                        this.listAdapter.notifyItemChanged(i4);
                    }
                } else {
                    this.listAdapter.notifyItemRemoved(i3);
                }
            }
        }
        if (this.currentType == 1) {
            this.previousByLocation = Theme.autoNightScheduleByLocation;
            this.previousUpdatedType = Theme.selectedAutoNightType;
        }
    }

    public boolean onFragmentCreate() {
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.locationPermissionGranted);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didSetNewWallpapper);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.themeListUpdated);
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
        Theme.saveAutoNightThemeConfig();
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.locationPermissionGranted) {
            updateSunTime(null, true);
        } else if (i == NotificationCenter.didSetNewWallpapper) {
            RecyclerListView recyclerListView = this.listView;
            if (recyclerListView != null) {
                recyclerListView.invalidateViews();
            }
        } else if (i == NotificationCenter.themeListUpdated) {
            updateRows(true);
        }
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setAllowOverlayTitle(false);
        if (AndroidUtilities.isTablet()) {
            this.actionBar.setOccupyStatusBar(false);
        }
        if (this.currentType == 0) {
            this.actionBar.setTitle(LocaleController.getString("ChatSettings", NUM));
            ActionBarMenuItem addItem = this.actionBar.createMenu().addItem(0, NUM);
            addItem.setContentDescription(LocaleController.getString("AccDescrMoreOptions", NUM));
            addItem.addSubItem(1, NUM, LocaleController.getString("CreateNewThemeMenu", NUM));
        } else {
            this.actionBar.setTitle(LocaleController.getString("AutoNightTheme", NUM));
        }
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    ThemeActivity.this.finishFragment();
                } else if (i == 1 && ThemeActivity.this.getParentActivity() != null) {
                    Builder builder = new Builder(ThemeActivity.this.getParentActivity());
                    builder.setTitle(LocaleController.getString("NewTheme", NUM));
                    builder.setMessage(LocaleController.getString("CreateNewThemeAlert", NUM));
                    builder.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
                    builder.setPositiveButton(LocaleController.getString("CreateTheme", NUM), new -$$Lambda$ThemeActivity$1$ZQnhOSOAx8cfjiv91xqtf3q-RU0(this));
                    ThemeActivity.this.showDialog(builder.create());
                }
            }

            public /* synthetic */ void lambda$onItemClick$0$ThemeActivity$1(DialogInterface dialogInterface, int i) {
                ThemeActivity.this.openThemeCreate();
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
        this.listView.setOnItemClickListener(new -$$Lambda$ThemeActivity$bWJNl-gm0og-Q62tjg4YIxA8Www(this));
        return this.fragmentView;
    }

    public /* synthetic */ void lambda$createView$4$ThemeActivity(View view, int i, float f, float f2) {
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
                        builder.setItems(new CharSequence[]{LocaleController.getString("DistanceUnitsAutomatic", NUM), LocaleController.getString("DistanceUnitsKilometers", NUM), LocaleController.getString("DistanceUnitsMiles", NUM)}, new -$$Lambda$ThemeActivity$VVfPYsGSwRhI82htSahIdZWO_zQ(this));
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
                            builder.setItems(new CharSequence[]{LocaleController.getString("Default", NUM), LocaleController.getString("SortFirstName", NUM), LocaleController.getString("SortLastName", NUM)}, new -$$Lambda$ThemeActivity$6SZnAHP7Ptnel1TqPa7stHg_Y4w(this, i));
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
                            Theme.checkAutoNightThemeConditions();
                            if (Theme.selectedAutoNightType != 0) {
                                z2 = true;
                            }
                            CharSequence currentNightThemeName = z2 ? Theme.getCurrentNightThemeName() : LocaleController.getString("AutoNightThemeOff", NUM);
                            if (z2) {
                                String str3;
                                if (Theme.selectedAutoNightType == 1) {
                                    i2 = NUM;
                                    str3 = "AutoNightScheduled";
                                } else {
                                    i2 = NUM;
                                    str3 = "AutoNightAdaptive";
                                }
                                str = LocaleController.getString(str3, i2);
                                StringBuilder stringBuilder = new StringBuilder();
                                stringBuilder.append(str);
                                stringBuilder.append(" ");
                                stringBuilder.append(currentNightThemeName);
                                currentNightThemeName = stringBuilder.toString();
                            }
                            notificationsCheckCell.setTextAndValueAndCheck(LocaleController.getString("AutoNightTheme", NUM), currentNightThemeName, z2, true);
                        }
                    } else if (i == this.nightDisabledRow) {
                        Theme.selectedAutoNightType = 0;
                        updateRows(true);
                        Theme.checkAutoNightThemeConditions();
                    } else if (i == this.nightScheduledRow) {
                        Theme.selectedAutoNightType = 1;
                        if (Theme.autoNightScheduleByLocation) {
                            updateSunTime(null, true);
                        }
                        updateRows(true);
                        Theme.checkAutoNightThemeConditions();
                    } else if (i == this.nightAutomaticRow) {
                        Theme.selectedAutoNightType = 2;
                        updateRows(true);
                        Theme.checkAutoNightThemeConditions();
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
                            showDialog(new TimePickerDialog(getParentActivity(), new -$$Lambda$ThemeActivity$Vm53Z0hPZ6cQlgJQ4_8I1uGYaeQ(this, i, (TextSettingsCell) view), i3, i2 - (i3 * 60), true));
                        }
                    } else if (i == this.scheduleUpdateLocationRow) {
                        updateSunTime(null, true);
                    }
                }
            }
        }
    }

    public /* synthetic */ void lambda$null$1$ThemeActivity(DialogInterface dialogInterface, int i) {
        SharedConfig.setDistanceSystemType(i);
        ViewHolder findViewHolderForAdapterPosition = this.listView.findViewHolderForAdapterPosition(this.distanceRow);
        if (findViewHolderForAdapterPosition != null) {
            this.listAdapter.onBindViewHolder(findViewHolderForAdapterPosition, this.distanceRow);
        }
    }

    public /* synthetic */ void lambda$null$2$ThemeActivity(int i, DialogInterface dialogInterface, int i2) {
        Editor edit = MessagesController.getGlobalMainSettings().edit();
        edit.putInt("sortContactsBy", i2);
        edit.commit();
        ListAdapter listAdapter = this.listAdapter;
        if (listAdapter != null) {
            listAdapter.notifyItemChanged(i);
        }
    }

    public /* synthetic */ void lambda$null$3$ThemeActivity(int i, TextSettingsCell textSettingsCell, TimePicker timePicker, int i2, int i3) {
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
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
    }

    private void openThemeCreate() {
        EditTextBoldCursor editTextBoldCursor = new EditTextBoldCursor(getParentActivity());
        editTextBoldCursor.setBackgroundDrawable(Theme.createEditTextDrawable(getParentActivity(), true));
        Builder builder = new Builder(getParentActivity());
        builder.setTitle(LocaleController.getString("NewTheme", NUM));
        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
        builder.setPositiveButton(LocaleController.getString("Create", NUM), -$$Lambda$ThemeActivity$T2DEwxCT8S71lYsgAFvNuKCLASSNAMEQo.INSTANCE);
        LinearLayout linearLayout = new LinearLayout(getParentActivity());
        linearLayout.setOrientation(1);
        builder.setView(linearLayout);
        TextView textView = new TextView(getParentActivity());
        textView.setText(LocaleController.formatString("EnterThemeName", NUM, new Object[0]));
        textView.setTextSize(16.0f);
        textView.setPadding(AndroidUtilities.dp(23.0f), AndroidUtilities.dp(12.0f), AndroidUtilities.dp(23.0f), AndroidUtilities.dp(6.0f));
        String str = "dialogTextBlack";
        textView.setTextColor(Theme.getColor(str));
        linearLayout.addView(textView, LayoutHelper.createLinear(-1, -2));
        editTextBoldCursor.setTextSize(1, 16.0f);
        editTextBoldCursor.setTextColor(Theme.getColor(str));
        editTextBoldCursor.setMaxLines(1);
        editTextBoldCursor.setLines(1);
        editTextBoldCursor.setInputType(16385);
        editTextBoldCursor.setGravity(51);
        editTextBoldCursor.setSingleLine(true);
        editTextBoldCursor.setImeOptions(6);
        editTextBoldCursor.setCursorColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        editTextBoldCursor.setCursorSize(AndroidUtilities.dp(20.0f));
        editTextBoldCursor.setCursorWidth(1.5f);
        editTextBoldCursor.setPadding(0, AndroidUtilities.dp(4.0f), 0, 0);
        linearLayout.addView(editTextBoldCursor, LayoutHelper.createLinear(-1, 36, 51, 24, 6, 24, 0));
        editTextBoldCursor.setOnEditorActionListener(-$$Lambda$ThemeActivity$VWCUOR2j_GKIfMXwZfHRrJ8b5fU.INSTANCE);
        editTextBoldCursor.setText(generateThemeName());
        editTextBoldCursor.setSelection(editTextBoldCursor.length());
        AlertDialog create = builder.create();
        create.setOnShowListener(new -$$Lambda$ThemeActivity$1vEC6O3lueqPvsr0HLElXf1QyPI(editTextBoldCursor));
        showDialog(create);
        create.getButton(-1).setOnClickListener(new -$$Lambda$ThemeActivity$wZ4-Th-MCpzrvar_lsEjlTx06zvs(this, editTextBoldCursor, create));
    }

    static /* synthetic */ void lambda$null$7(EditTextBoldCursor editTextBoldCursor) {
        editTextBoldCursor.requestFocus();
        AndroidUtilities.showKeyboard(editTextBoldCursor);
    }

    public /* synthetic */ void lambda$openThemeCreate$9$ThemeActivity(EditTextBoldCursor editTextBoldCursor, AlertDialog alertDialog, View view) {
        if (editTextBoldCursor.length() == 0) {
            Vibrator vibrator = (Vibrator) ApplicationLoader.applicationContext.getSystemService("vibrator");
            if (vibrator != null) {
                vibrator.vibrate(200);
            }
            AndroidUtilities.shakeView(editTextBoldCursor, 2.0f, 0);
            return;
        }
        new ThemeEditorView().show(getParentActivity(), Theme.createNewTheme(editTextBoldCursor.getText().toString()));
        updateRows(true);
        alertDialog.dismiss();
        SharedPreferences globalMainSettings = MessagesController.getGlobalMainSettings();
        String str = "themehint";
        if (!globalMainSettings.getBoolean(str, false)) {
            globalMainSettings.edit().putBoolean(str, true).commit();
            try {
                Toast.makeText(getParentActivity(), LocaleController.getString("CreateNewThemeHelp", NUM), 1).show();
            } catch (Exception e) {
                FileLog.e(e);
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
                        builder.setTitle(LocaleController.getString("AppName", NUM));
                        builder.setMessage(LocaleController.getString("GpsDisabledAlert", NUM));
                        builder.setPositiveButton(LocaleController.getString("ConnectingToProxyEnable", NUM), new -$$Lambda$ThemeActivity$oEXZvbxqKHkZY6ZgCJTwtSLiYYk(this));
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
        Utilities.globalQueue.postRunnable(new -$$Lambda$ThemeActivity$TGRjhun5kuM4-l-qFwZrzxfz7ho(this));
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

    public /* synthetic */ void lambda$updateSunTime$10$ThemeActivity(DialogInterface dialogInterface, int i) {
        if (getParentActivity() != null) {
            try {
                getParentActivity().startActivity(new Intent("android.settings.LOCATION_SOURCE_SETTINGS"));
            } catch (Exception unused) {
            }
        }
    }

    public /* synthetic */ void lambda$updateSunTime$12$ThemeActivity() {
        String str = null;
        try {
            List fromLocation = new Geocoder(ApplicationLoader.applicationContext, Locale.getDefault()).getFromLocation(Theme.autoNightLocationLatitude, Theme.autoNightLocationLongitude, 1);
            if (fromLocation.size() > 0) {
                str = ((Address) fromLocation.get(0)).getLocality();
            }
        } catch (Exception unused) {
        }
        AndroidUtilities.runOnUIThread(new -$$Lambda$ThemeActivity$aYbaWuvP_GFKk6bpoq7IXffT-AE(this, str));
    }

    public /* synthetic */ void lambda$null$11$ThemeActivity(String str) {
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
            builder.setNegativeButton(LocaleController.getString("PermissionOpenSettings", NUM), new -$$Lambda$ThemeActivity$6BTFbEaGAqWQVckpQGbDShbZmy0(this));
            builder.setPositiveButton(LocaleController.getString("OK", NUM), null);
            showDialog(builder.create());
        }
    }

    public /* synthetic */ void lambda$showPermissionAlert$13$ThemeActivity(DialogInterface dialogInterface, int i) {
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

    private String generateThemeName() {
        List asList = Arrays.asList(new String[]{"Ancient", "Antique", "Autumn", "Baby", "Barely", "Baroque", "Blazing", "Blushing", "Bohemian", "Bubbly", "Burning", "Buttered", "Classic", "Clear", "Cool", "Cosmic", "Cotton", "Cozy", "Crystal", "Dark", "Daring", "Darling", "Dawn", "Dazzling", "Deep", "Deepest", "Delicate", "Delightful", "Divine", "Double", "Downtown", "Dreamy", "Dusky", "Dusty", "Electric", "Enchanted", "Endless", "Evening", "Fantastic", "Flirty", "Forever", "Frigid", "Frosty", "Frozen", "Gentle", "Heavenly", "Hyper", "Icy", "Infinite", "Innocent", "Instant", "Luscious", "Lunar", "Lustrous", "Magic", "Majestic", "Mambo", "Midnight", "Millenium", "Morning", "Mystic", "Natural", "Neon", "Night", "Opaque", "Paradise", "Perfect", "Perky", "Polished", "Powerful", "Rich", "Royal", "Sheer", "Simply", "Sizzling", "Solar", "Sparkling", "Splendid", "Spicy", "Spring", "Stellar", "Sugared", "Summer", "Sunny", "Super", "Sweet", "Tender", "Tenacious", "Tidal", "Toasted", "Totally", "Tranquil", "Tropical", "True", "Twilight", "Twinkling", "Ultimate", "Ultra", "Velvety", "Vibrant", "Vintage", "Virtual", "Warm", "Warmest", "Whipped", "Wild", "Winsome"});
        List asList2 = Arrays.asList(new String[]{"Ambrosia", "Attack", "Avalanche", "Blast", "Bliss", "Blossom", "Blush", "Burst", "Butter", "Candy", "Carnival", "Charm", "Chiffon", "Cloud", "Comet", "Delight", "Dream", "Dust", "Fantasy", "Flame", "Flash", "Fire", "Freeze", "Frost", "Glade", "Glaze", "Gleam", "Glimmer", "Glitter", "Glow", "Grande", "Haze", "Highlight", "Ice", "Illusion", "Intrigue", "Jewel", "Jubilee", "Kiss", "Lights", "Lollypop", "Love", "Luster", "Madness", "Matte", "Mirage", "Mist", "Moon", "Muse", "Myth", "Nectar", "Nova", "Parfait", "Passion", "Pop", "Rain", "Reflection", "Rhapsody", "Romance", "Satin", "Sensation", "Silk", "Shine", "Shadow", "Shimmer", "Sky", "Spice", "Star", "Sugar", "Sunrise", "Sunset", "Sun", "Twist", "Unbound", "Velvet", "Vibrant", "Waters", "Wine", "Wink", "Wonder", "Zone"});
        HashMap hashMap = new HashMap();
        hashMap.put(Integer.valueOf(9306112), "Berry");
        hashMap.put(Integer.valueOf(14598550), "Brandy");
        hashMap.put(Integer.valueOf(8391495), "Cherry");
        hashMap.put(Integer.valueOf(16744272), "Coral");
        hashMap.put(Integer.valueOf(14372985), "Cranberry");
        hashMap.put(Integer.valueOf(14423100), "Crimson");
        hashMap.put(Integer.valueOf(14725375), "Mauve");
        hashMap.put(Integer.valueOf(16761035), "Pink");
        hashMap.put(Integer.valueOf(16711680), "Red");
        hashMap.put(Integer.valueOf(16711807), "Rose");
        hashMap.put(Integer.valueOf(8406555), "Russet");
        hashMap.put(Integer.valueOf(16720896), "Scarlet");
        hashMap.put(Integer.valueOf(15856113), "Seashell");
        hashMap.put(Integer.valueOf(16724889), "Strawberry");
        hashMap.put(Integer.valueOf(16760576), "Amber");
        hashMap.put(Integer.valueOf(15438707), "Apricot");
        hashMap.put(Integer.valueOf(16508850), "Banana");
        hashMap.put(Integer.valueOf(10601738), "Citrus");
        hashMap.put(Integer.valueOf(11560192), "Ginger");
        hashMap.put(Integer.valueOf(16766720), "Gold");
        hashMap.put(Integer.valueOf(16640272), "Lemon");
        hashMap.put(Integer.valueOf(16753920), "Orange");
        hashMap.put(Integer.valueOf(16770484), "Peach");
        hashMap.put(Integer.valueOf(16739155), "Persimmon");
        hashMap.put(Integer.valueOf(14996514), "Sunflower");
        hashMap.put(Integer.valueOf(15893760), "Tangerine");
        hashMap.put(Integer.valueOf(16763004), "Topaz");
        hashMap.put(Integer.valueOf(16776960), "Yellow");
        hashMap.put(Integer.valueOf(3688720), "Clover");
        hashMap.put(Integer.valueOf(8628829), "Cucumber");
        hashMap.put(Integer.valueOf(5294200), "Emerald");
        hashMap.put(Integer.valueOf(11907932), "Olive");
        hashMap.put(Integer.valueOf(65280), "Green");
        hashMap.put(Integer.valueOf(43115), "Jade");
        hashMap.put(Integer.valueOf(2730887), "Jungle");
        hashMap.put(Integer.valueOf(12582656), "Lime");
        hashMap.put(Integer.valueOf(776785), "Malachite");
        hashMap.put(Integer.valueOf(10026904), "Mint");
        hashMap.put(Integer.valueOf(11394989), "Moss");
        hashMap.put(Integer.valueOf(3234721), "Azure");
        hashMap.put(Integer.valueOf(255), "Blue");
        hashMap.put(Integer.valueOf(18347), "Cobalt");
        hashMap.put(Integer.valueOf(5204422), "Indigo");
        hashMap.put(Integer.valueOf(96647), "Lagoon");
        hashMap.put(Integer.valueOf(7461346), "Aquamarine");
        hashMap.put(Integer.valueOf(1182351), "Ultramarine");
        hashMap.put(Integer.valueOf(128), "Navy");
        hashMap.put(Integer.valueOf(3101086), "Sapphire");
        hashMap.put(Integer.valueOf(7788522), "Sky");
        hashMap.put(Integer.valueOf(32896), "Teal");
        hashMap.put(Integer.valueOf(4251856), "Turquoise");
        hashMap.put(Integer.valueOf(10053324), "Amethyst");
        hashMap.put(Integer.valueOf(5046581), "Blackberry");
        hashMap.put(Integer.valueOf(6373457), "Eggplant");
        hashMap.put(Integer.valueOf(13148872), "Lilac");
        hashMap.put(Integer.valueOf(11894492), "Lavender");
        hashMap.put(Integer.valueOf(13421823), "Periwinkle");
        hashMap.put(Integer.valueOf(8663417), "Plum");
        hashMap.put(Integer.valueOf(6684825), "Purple");
        hashMap.put(Integer.valueOf(14204888), "Thistle");
        hashMap.put(Integer.valueOf(14315734), "Orchid");
        hashMap.put(Integer.valueOf(2361920), "Violet");
        hashMap.put(Integer.valueOf(4137225), "Bronze");
        hashMap.put(Integer.valueOf(3604994), "Chocolate");
        hashMap.put(Integer.valueOf(8077056), "Cinnamon");
        hashMap.put(Integer.valueOf(3153694), "Cocoa");
        hashMap.put(Integer.valueOf(7365973), "Coffee");
        hashMap.put(Integer.valueOf(7956873), "Rum");
        hashMap.put(Integer.valueOf(5113350), "Mahogany");
        hashMap.put(Integer.valueOf(7875865), "Mocha");
        hashMap.put(Integer.valueOf(12759680), "Sand");
        hashMap.put(Integer.valueOf(8924439), "Sienna");
        hashMap.put(Integer.valueOf(7864585), "Maple");
        hashMap.put(Integer.valueOf(15787660), "Khaki");
        hashMap.put(Integer.valueOf(12088115), "Copper");
        hashMap.put(Integer.valueOf(12144200), "Chestnut");
        hashMap.put(Integer.valueOf(15653316), "Almond");
        hashMap.put(Integer.valueOf(16776656), "Cream");
        hashMap.put(Integer.valueOf(12186367), "Diamond");
        hashMap.put(Integer.valueOf(11109127), "Honey");
        hashMap.put(Integer.valueOf(16777200), "Ivory");
        hashMap.put(Integer.valueOf(15392968), "Pearl");
        hashMap.put(Integer.valueOf(15725299), "Porcelain");
        hashMap.put(Integer.valueOf(13745832), "Vanilla");
        hashMap.put(Integer.valueOf(16777215), "White");
        hashMap.put(Integer.valueOf(8421504), "Gray");
        hashMap.put(Integer.valueOf(0), "Black");
        hashMap.put(Integer.valueOf(15266260), "Chrome");
        hashMap.put(Integer.valueOf(3556687), "Charcoal");
        hashMap.put(Integer.valueOf(789277), "Ebony");
        hashMap.put(Integer.valueOf(12632256), "Silver");
        hashMap.put(Integer.valueOf(16119285), "Smoke");
        hashMap.put(Integer.valueOf(2499381), "Steel");
        hashMap.put(Integer.valueOf(5220413), "Apple");
        hashMap.put(Integer.valueOf(8434628), "Glacier");
        hashMap.put(Integer.valueOf(16693933), "Melon");
        hashMap.put(Integer.valueOf(12929932), "Mulberry");
        hashMap.put(Integer.valueOf(11126466), "Opal");
        hashMap.put(Integer.valueOf(5547512), "Blue");
        int i = Theme.getCurrentTheme().accentColor;
        if (i == 0) {
            i = AndroidUtilities.calcDrawableColor(Theme.getCachedWallpaper())[0];
        }
        String str = null;
        int i2 = Integer.MAX_VALUE;
        int red = Color.red(i);
        int green = Color.green(i);
        i = Color.blue(i);
        for (Entry entry : hashMap.entrySet()) {
            Integer num = (Integer) entry.getKey();
            int red2 = Color.red(num.intValue());
            int i3 = (red + red2) / 2;
            red2 = red - red2;
            int green2 = green - Color.green(num.intValue());
            int blue = i - Color.blue(num.intValue());
            red2 = (((((i3 + 512) * red2) * red2) >> 8) + ((green2 * 4) * green2)) + ((((767 - i3) * blue) * blue) >> 8);
            if (red2 < i2) {
                str = (String) entry.getValue();
                i2 = red2;
            }
        }
        if (Utilities.random.nextInt() % 2 == 0) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append((String) asList.get(Utilities.random.nextInt(asList.size())));
            stringBuilder.append(" ");
            stringBuilder.append(str);
            return stringBuilder.toString();
        }
        StringBuilder stringBuilder2 = new StringBuilder();
        stringBuilder2.append(str);
        stringBuilder2.append(" ");
        stringBuilder2.append((String) asList2.get(Utilities.random.nextInt(asList2.size())));
        return stringBuilder2.toString();
    }

    public ThemeDescription[] getThemeDescriptions() {
        r1 = new ThemeDescription[60];
        r1[0] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextSettingsCell.class, TextCheckCell.class, HeaderCell.class, BrightnessControlCell.class, ThemeTypeCell.class, TextSizeCell.class, ChatListCell.class, NotificationsCheckCell.class, ThemesHorizontalListCell.class, TintRecyclerListView.class}, null, null, null, "windowBackgroundWhite");
        r1[1] = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundGray");
        r1[2] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault");
        r1[3] = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault");
        r1[4] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon");
        r1[5] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle");
        r1[6] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector");
        r1[7] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUBACKGROUND, null, null, null, null, "actionBarDefaultSubmenuBackground");
        r1[8] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM, null, null, null, null, "actionBarDefaultSubmenuItem");
        r1[9] = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM | ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, "actionBarDefaultSubmenuItemIcon");
        r1[10] = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21");
        r1[11] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, "divider");
        r1[12] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, "windowBackgroundGrayShadow");
        r1[13] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, "windowBackgroundGrayShadow");
        View view = this.listView;
        Class[] clsArr = new Class[]{TextInfoPrivacyCell.class};
        String[] strArr = new String[1];
        strArr[0] = "textView";
        r1[14] = new ThemeDescription(view, 0, clsArr, strArr, null, null, null, "windowBackgroundWhiteGrayText4");
        r1[15] = new ThemeDescription(this.listView, 0, new Class[]{TextSettingsCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText");
        view = this.listView;
        clsArr = new Class[]{TextSettingsCell.class};
        strArr = new String[1];
        strArr[0] = "valueTextView";
        r1[16] = new ThemeDescription(view, 0, clsArr, strArr, null, null, null, "windowBackgroundWhiteValueText");
        r1[17] = new ThemeDescription(this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlueHeader");
        r1[18] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText");
        view = this.listView;
        clsArr = new Class[]{TextCheckCell.class};
        strArr = new String[1];
        strArr[0] = "checkBox";
        r1[19] = new ThemeDescription(view, 0, clsArr, strArr, null, null, null, "switchTrack");
        r1[20] = new ThemeDescription(this.listView, 0, new Class[]{TextCheckCell.class}, new String[]{"checkBox"}, null, null, null, "switchTrackChecked");
        view = this.listView;
        View view2 = view;
        r1[21] = new ThemeDescription(view2, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{BrightnessControlCell.class}, new String[]{"leftImageView"}, null, null, null, "profile_actionIcon");
        view = this.listView;
        view2 = view;
        r1[22] = new ThemeDescription(view2, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{BrightnessControlCell.class}, new String[]{"rightImageView"}, null, null, null, "profile_actionIcon");
        view = this.listView;
        clsArr = new Class[]{BrightnessControlCell.class};
        strArr = new String[1];
        strArr[0] = "seekBarView";
        r1[23] = new ThemeDescription(view, 0, clsArr, strArr, null, null, null, "player_progressBackground");
        view = this.listView;
        view2 = view;
        r1[24] = new ThemeDescription(view2, ThemeDescription.FLAG_PROGRESSBAR, new Class[]{BrightnessControlCell.class}, new String[]{"seekBarView"}, null, null, null, "player_progress");
        r1[25] = new ThemeDescription(this.listView, 0, new Class[]{ThemeTypeCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText");
        r1[26] = new ThemeDescription(this.listView, 0, new Class[]{ThemeTypeCell.class}, new String[]{"checkImage"}, null, null, null, "featuredStickers_addedIcon");
        view = this.listView;
        view2 = view;
        r1[27] = new ThemeDescription(view2, ThemeDescription.FLAG_PROGRESSBAR, new Class[]{TextSizeCell.class}, new String[]{"sizeBar"}, null, null, null, "player_progress");
        r1[28] = new ThemeDescription(this.listView, 0, new Class[]{TextSizeCell.class}, new String[]{"sizeBar"}, null, null, null, "player_progressBackground");
        r1[29] = new ThemeDescription(this.listView, 0, new Class[]{ChatListCell.class}, null, null, null, "radioBackground");
        r1[30] = new ThemeDescription(this.listView, 0, new Class[]{ChatListCell.class}, null, null, null, "radioBackgroundChecked");
        r1[31] = new ThemeDescription(this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"textView"}, null, null, null, "windowBackgroundWhiteBlackText");
        r1[32] = new ThemeDescription(this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"valueTextView"}, null, null, null, "windowBackgroundWhiteGrayText2");
        r1[33] = new ThemeDescription(this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"checkBox"}, null, null, null, "switchTrack");
        r1[34] = new ThemeDescription(this.listView, 0, new Class[]{NotificationsCheckCell.class}, new String[]{"checkBox"}, null, null, null, "switchTrackChecked");
        r1[35] = new ThemeDescription(this.listView, 0, null, null, new Drawable[]{Theme.chat_msgInDrawable, Theme.chat_msgInMediaDrawable}, null, "chat_inBubble");
        r1[36] = new ThemeDescription(this.listView, 0, null, null, new Drawable[]{Theme.chat_msgInSelectedDrawable, Theme.chat_msgInMediaSelectedDrawable}, null, "chat_inBubbleSelected");
        r1[37] = new ThemeDescription(this.listView, 0, null, null, new Drawable[]{Theme.chat_msgInShadowDrawable, Theme.chat_msgInMediaShadowDrawable}, null, "chat_inBubbleShadow");
        r1[38] = new ThemeDescription(this.listView, 0, null, null, new Drawable[]{Theme.chat_msgOutDrawable, Theme.chat_msgOutMediaDrawable}, null, "chat_outBubble");
        r1[39] = new ThemeDescription(this.listView, 0, null, null, new Drawable[]{Theme.chat_msgOutSelectedDrawable, Theme.chat_msgOutMediaSelectedDrawable}, null, "chat_outBubbleSelected");
        r1[40] = new ThemeDescription(this.listView, 0, null, null, new Drawable[]{Theme.chat_msgOutShadowDrawable, Theme.chat_msgOutMediaShadowDrawable}, null, "chat_outBubbleShadow");
        r1[41] = new ThemeDescription(this.listView, 0, null, null, null, null, "chat_messageTextIn");
        r1[42] = new ThemeDescription(this.listView, 0, null, null, null, null, "chat_messageTextOut");
        r1[43] = new ThemeDescription(this.listView, 0, null, null, new Drawable[]{Theme.chat_msgOutCheckDrawable}, null, "chat_outSentCheck");
        r1[44] = new ThemeDescription(this.listView, 0, null, null, new Drawable[]{Theme.chat_msgOutCheckSelectedDrawable}, null, "chat_outSentCheckSelected");
        r1[45] = new ThemeDescription(this.listView, 0, null, null, new Drawable[]{Theme.chat_msgOutCheckReadDrawable, Theme.chat_msgOutHalfCheckDrawable}, null, "chat_outSentCheckRead");
        r1[46] = new ThemeDescription(this.listView, 0, null, null, new Drawable[]{Theme.chat_msgOutCheckReadSelectedDrawable, Theme.chat_msgOutHalfCheckSelectedDrawable}, null, "chat_outSentCheckReadSelected");
        r1[47] = new ThemeDescription(this.listView, 0, null, null, new Drawable[]{Theme.chat_msgMediaCheckDrawable, Theme.chat_msgMediaHalfCheckDrawable}, null, "chat_mediaSentCheck");
        r1[48] = new ThemeDescription(this.listView, 0, null, null, null, null, "chat_inReplyLine");
        r1[49] = new ThemeDescription(this.listView, 0, null, null, null, null, "chat_outReplyLine");
        r1[50] = new ThemeDescription(this.listView, 0, null, null, null, null, "chat_inReplyNameText");
        r1[51] = new ThemeDescription(this.listView, 0, null, null, null, null, "chat_outReplyNameText");
        r1[52] = new ThemeDescription(this.listView, 0, null, null, null, null, "chat_inReplyMessageText");
        r1[53] = new ThemeDescription(this.listView, 0, null, null, null, null, "chat_outReplyMessageText");
        r1[54] = new ThemeDescription(this.listView, 0, null, null, null, null, "chat_inReplyMediaMessageSelectedText");
        r1[55] = new ThemeDescription(this.listView, 0, null, null, null, null, "chat_outReplyMediaMessageSelectedText");
        r1[56] = new ThemeDescription(this.listView, 0, null, null, null, null, "chat_inTimeText");
        r1[57] = new ThemeDescription(this.listView, 0, null, null, null, null, "chat_outTimeText");
        r1[58] = new ThemeDescription(this.listView, 0, null, null, null, null, "chat_inTimeSelectedText");
        r1[59] = new ThemeDescription(this.listView, 0, null, null, null, null, "chat_outTimeSelectedText");
        return r1;
    }
}
