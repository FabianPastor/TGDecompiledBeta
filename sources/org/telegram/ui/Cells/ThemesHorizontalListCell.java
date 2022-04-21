package org.telegram.ui.Cells;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.SystemClock;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.FrameLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.EmojiThemes;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.MotionBackgroundDrawable;
import org.telegram.ui.Components.RadioButton;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.ThemeSetUrlActivity;

public class ThemesHorizontalListCell extends RecyclerListView implements NotificationCenter.NotificationCenterDelegate {
    public static byte[] bytes = new byte[1024];
    private ThemesListAdapter adapter;
    /* access modifiers changed from: private */
    public int currentType;
    /* access modifiers changed from: private */
    public ArrayList<Theme.ThemeInfo> customThemes;
    /* access modifiers changed from: private */
    public ArrayList<Theme.ThemeInfo> defaultThemes;
    private boolean drawDivider;
    private LinearLayoutManager horizontalLayoutManager;
    /* access modifiers changed from: private */
    public HashMap<String, Theme.ThemeInfo> loadingThemes = new HashMap<>();
    /* access modifiers changed from: private */
    public HashMap<Theme.ThemeInfo, String> loadingWallpapers = new HashMap<>();
    /* access modifiers changed from: private */
    public int prevCount;
    private Theme.ThemeInfo prevThemeInfo;

    private class ThemesListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        ThemesListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return false;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new RecyclerListView.Holder(new InnerThemeView(this.mContext));
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ArrayList<Theme.ThemeInfo> arrayList;
            InnerThemeView view = (InnerThemeView) holder.itemView;
            int p = position;
            if (position < ThemesHorizontalListCell.this.defaultThemes.size()) {
                arrayList = ThemesHorizontalListCell.this.defaultThemes;
            } else {
                arrayList = ThemesHorizontalListCell.this.customThemes;
                p -= ThemesHorizontalListCell.this.defaultThemes.size();
            }
            Theme.ThemeInfo themeInfo = arrayList.get(p);
            boolean z = true;
            boolean z2 = position == getItemCount() - 1;
            if (position != 0) {
                z = false;
            }
            view.setTheme(themeInfo, z2, z);
        }

        public int getItemCount() {
            ThemesHorizontalListCell themesHorizontalListCell = ThemesHorizontalListCell.this;
            return themesHorizontalListCell.prevCount = themesHorizontalListCell.defaultThemes.size() + ThemesHorizontalListCell.this.customThemes.size();
        }
    }

    private class InnerThemeView extends FrameLayout {
        private ObjectAnimator accentAnimator;
        private boolean accentColorChanged;
        private int accentId;
        private float accentState;
        private int backColor;
        private Drawable backgroundDrawable;
        private Paint bitmapPaint = new Paint(3);
        private BitmapShader bitmapShader;
        private RadioButton button;
        private int checkColor;
        private final ArgbEvaluator evaluator = new ArgbEvaluator();
        private boolean hasWhiteBackground;
        private int inColor;
        private Drawable inDrawable;
        private boolean isFirst;
        private boolean isLast;
        private long lastDrawTime;
        private int loadingColor;
        private Drawable loadingDrawable;
        private int oldBackColor;
        private int oldCheckColor;
        private int oldInColor;
        private int oldOutColor;
        private Drawable optionsDrawable;
        private int outColor;
        private Drawable outDrawable;
        private Paint paint = new Paint(1);
        private float placeholderAlpha;
        private boolean pressed;
        private RectF rect = new RectF();
        private Matrix shaderMatrix = new Matrix();
        private TextPaint textPaint = new TextPaint(1);
        /* access modifiers changed from: private */
        public Theme.ThemeInfo themeInfo;

        public InnerThemeView(Context context) {
            super(context);
            setWillNotDraw(false);
            this.inDrawable = context.getResources().getDrawable(NUM).mutate();
            this.outDrawable = context.getResources().getDrawable(NUM).mutate();
            this.textPaint.setTextSize((float) AndroidUtilities.dp(13.0f));
            RadioButton radioButton = new RadioButton(context);
            this.button = radioButton;
            radioButton.setSize(AndroidUtilities.dp(20.0f));
            addView(this.button, LayoutHelper.createFrame(22, 22.0f, 51, 27.0f, 75.0f, 0.0f, 0.0f));
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int i = 22;
            int i2 = (this.isLast ? 22 : 15) + 76;
            if (!this.isFirst) {
                i = 0;
            }
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp((float) (i2 + i)), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(148.0f), NUM));
        }

        public boolean onTouchEvent(MotionEvent event) {
            Theme.ThemeInfo themeInfo2;
            if (this.optionsDrawable == null || (themeInfo2 = this.themeInfo) == null || ((themeInfo2.info != null && !this.themeInfo.themeLoaded) || ThemesHorizontalListCell.this.currentType != 0)) {
                return super.onTouchEvent(event);
            }
            int action = event.getAction();
            if (action == 0 || action == 1) {
                float x = event.getX();
                float y = event.getY();
                if (x > this.rect.centerX() && y < this.rect.centerY() - ((float) AndroidUtilities.dp(10.0f))) {
                    if (action == 0) {
                        this.pressed = true;
                    } else {
                        performHapticFeedback(3);
                        ThemesHorizontalListCell.this.showOptionsForTheme(this.themeInfo);
                    }
                }
                if (action == 1) {
                    this.pressed = false;
                }
            }
            return this.pressed;
        }

        /* access modifiers changed from: private */
        /* JADX WARNING: Can't fix incorrect switch cases order */
        /* JADX WARNING: Removed duplicated region for block: B:132:0x027c A[Catch:{ Exception -> 0x025a, all -> 0x02d3 }, FALL_THROUGH] */
        /* JADX WARNING: Removed duplicated region for block: B:133:0x027d A[Catch:{ Exception -> 0x025a, all -> 0x02d3 }] */
        /* JADX WARNING: Removed duplicated region for block: B:136:0x0285 A[Catch:{ Exception -> 0x025a, all -> 0x02d3 }] */
        /* JADX WARNING: Removed duplicated region for block: B:139:0x028d A[Catch:{ Exception -> 0x025a, all -> 0x02d3 }] */
        /* JADX WARNING: Removed duplicated region for block: B:142:0x0295 A[Catch:{ Exception -> 0x025a, all -> 0x02d3 }] */
        /* JADX WARNING: Removed duplicated region for block: B:145:0x029d A[Catch:{ Exception -> 0x025a, all -> 0x02d3 }] */
        /* JADX WARNING: Removed duplicated region for block: B:148:0x02a5 A[Catch:{ Exception -> 0x025a, all -> 0x02d3 }] */
        /* JADX WARNING: Removed duplicated region for block: B:153:0x02b1 A[Catch:{ Exception -> 0x025a, all -> 0x02d3 }] */
        /* JADX WARNING: Removed duplicated region for block: B:154:0x02b2 A[Catch:{ Exception -> 0x025a, all -> 0x02d3 }] */
        /* JADX WARNING: Removed duplicated region for block: B:155:0x02b7 A[Catch:{ Exception -> 0x025a, all -> 0x02d3 }] */
        /* JADX WARNING: Removed duplicated region for block: B:156:0x02bc A[Catch:{ Exception -> 0x025a, all -> 0x02d3 }] */
        /* JADX WARNING: Removed duplicated region for block: B:157:0x02c1 A[Catch:{ Exception -> 0x025a, all -> 0x02d3 }] */
        /* JADX WARNING: Removed duplicated region for block: B:158:0x02c7 A[Catch:{ Exception -> 0x025a, all -> 0x02d3 }] */
        /* JADX WARNING: Removed duplicated region for block: B:159:0x02cd A[Catch:{ Exception -> 0x025a, all -> 0x02d3 }] */
        /* JADX WARNING: Removed duplicated region for block: B:194:0x0353  */
        /* JADX WARNING: Removed duplicated region for block: B:199:0x03a2  */
        /* JADX WARNING: Unknown top exception splitter block from list: {B:185:0x0340=Splitter:B:185:0x0340, B:177:0x032d=Splitter:B:177:0x032d} */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean parseTheme() {
            /*
                r25 = this;
                r1 = r25
                java.lang.String r2 = "chat_inBubble"
                org.telegram.ui.ActionBar.Theme$ThemeInfo r0 = r1.themeInfo
                if (r0 == 0) goto L_0x03aa
                java.lang.String r0 = r0.pathToFile
                if (r0 != 0) goto L_0x000e
                goto L_0x03aa
            L_0x000e:
                r4 = 0
                java.io.File r0 = new java.io.File
                org.telegram.ui.ActionBar.Theme$ThemeInfo r5 = r1.themeInfo
                java.lang.String r5 = r5.pathToFile
                r0.<init>(r5)
                r5 = r0
                r6 = 1
                java.io.FileInputStream r0 = new java.io.FileInputStream     // Catch:{ all -> 0x0341 }
                r0.<init>(r5)     // Catch:{ all -> 0x0341 }
                r7 = r0
                r0 = 0
                r8 = 0
            L_0x0022:
                byte[] r9 = org.telegram.ui.Cells.ThemesHorizontalListCell.bytes     // Catch:{ all -> 0x0333 }
                int r9 = r7.read(r9)     // Catch:{ all -> 0x0333 }
                r10 = r9
                r11 = -1
                if (r9 == r11) goto L_0x0325
                r9 = r0
                r12 = 0
                r13 = 0
                r24 = r8
                r8 = r0
                r0 = r24
            L_0x0034:
                if (r13 >= r10) goto L_0x0301
                byte[] r14 = org.telegram.ui.Cells.ThemesHorizontalListCell.bytes     // Catch:{ all -> 0x0333 }
                byte r14 = r14[r13]     // Catch:{ all -> 0x0333 }
                r15 = 10
                if (r14 != r15) goto L_0x02eb
                int r14 = r0 + 1
                int r0 = r13 - r12
                int r15 = r0 + 1
                java.lang.String r0 = new java.lang.String     // Catch:{ all -> 0x0333 }
                byte[] r11 = org.telegram.ui.Cells.ThemesHorizontalListCell.bytes     // Catch:{ all -> 0x0333 }
                int r3 = r15 + -1
                java.lang.String r6 = "UTF-8"
                r0.<init>(r11, r12, r3, r6)     // Catch:{ all -> 0x0333 }
                r3 = r0
                java.lang.String r0 = "WLS="
                boolean r0 = r3.startsWith(r0)     // Catch:{ all -> 0x0333 }
                r6 = 4
                if (r0 == 0) goto L_0x01cd
                java.lang.String r0 = r3.substring(r6)     // Catch:{ all -> 0x01c3 }
                r6 = r0
                android.net.Uri r0 = android.net.Uri.parse(r6)     // Catch:{ all -> 0x01c3 }
                r11 = r0
                org.telegram.ui.ActionBar.Theme$ThemeInfo r0 = r1.themeInfo     // Catch:{ all -> 0x01c3 }
                r16 = r4
                java.lang.String r4 = "slug"
                java.lang.String r4 = r11.getQueryParameter(r4)     // Catch:{ all -> 0x01b9 }
                r0.slug = r4     // Catch:{ all -> 0x01b9 }
                org.telegram.ui.ActionBar.Theme$ThemeInfo r0 = r1.themeInfo     // Catch:{ all -> 0x01b9 }
                java.io.File r4 = new java.io.File     // Catch:{ all -> 0x01b9 }
                r17 = r5
                java.io.File r5 = org.telegram.messenger.ApplicationLoader.getFilesDirFixed()     // Catch:{ all -> 0x01eb }
                r18 = r10
                java.lang.StringBuilder r10 = new java.lang.StringBuilder     // Catch:{ all -> 0x01eb }
                r10.<init>()     // Catch:{ all -> 0x01eb }
                r19 = r14
                java.lang.String r14 = org.telegram.messenger.Utilities.MD5(r6)     // Catch:{ all -> 0x01eb }
                r10.append(r14)     // Catch:{ all -> 0x01eb }
                java.lang.String r14 = ".wp"
                r10.append(r14)     // Catch:{ all -> 0x01eb }
                java.lang.String r10 = r10.toString()     // Catch:{ all -> 0x01eb }
                r4.<init>(r5, r10)     // Catch:{ all -> 0x01eb }
                java.lang.String r4 = r4.getAbsolutePath()     // Catch:{ all -> 0x01eb }
                r0.pathToWallpaper = r4     // Catch:{ all -> 0x01eb }
                java.lang.String r0 = "mode"
                java.lang.String r0 = r11.getQueryParameter(r0)     // Catch:{ all -> 0x01eb }
                if (r0 == 0) goto L_0x00cc
                java.lang.String r4 = r0.toLowerCase()     // Catch:{ all -> 0x01eb }
                r0 = r4
                java.lang.String r4 = " "
                java.lang.String[] r4 = r0.split(r4)     // Catch:{ all -> 0x01eb }
                if (r4 == 0) goto L_0x00ca
                int r5 = r4.length     // Catch:{ all -> 0x01eb }
                if (r5 <= 0) goto L_0x00ca
                r5 = 0
            L_0x00b4:
                int r10 = r4.length     // Catch:{ all -> 0x01eb }
                if (r5 >= r10) goto L_0x00ca
                java.lang.String r10 = "blur"
                r14 = r4[r5]     // Catch:{ all -> 0x01eb }
                boolean r10 = r10.equals(r14)     // Catch:{ all -> 0x01eb }
                if (r10 == 0) goto L_0x00c7
                org.telegram.ui.ActionBar.Theme$ThemeInfo r10 = r1.themeInfo     // Catch:{ all -> 0x01eb }
                r14 = 1
                r10.isBlured = r14     // Catch:{ all -> 0x01eb }
                goto L_0x00ca
            L_0x00c7:
                int r5 = r5 + 1
                goto L_0x00b4
            L_0x00ca:
                r4 = r0
                goto L_0x00cd
            L_0x00cc:
                r4 = r0
            L_0x00cd:
                java.lang.String r0 = "pattern"
                java.lang.String r0 = r11.getQueryParameter(r0)     // Catch:{ all -> 0x01eb }
                r5 = r0
                boolean r0 = android.text.TextUtils.isEmpty(r5)     // Catch:{ all -> 0x01eb }
                if (r0 != 0) goto L_0x01af
                java.lang.String r0 = "bg_color"
                java.lang.String r0 = r11.getQueryParameter(r0)     // Catch:{ Exception -> 0x016b }
                boolean r10 = android.text.TextUtils.isEmpty(r0)     // Catch:{ Exception -> 0x016b }
                if (r10 != 0) goto L_0x0166
                org.telegram.ui.ActionBar.Theme$ThemeInfo r10 = r1.themeInfo     // Catch:{ Exception -> 0x016b }
                r14 = 6
                r20 = r4
                r21 = r5
                r4 = 0
                java.lang.String r5 = r0.substring(r4, r14)     // Catch:{ Exception -> 0x0164 }
                r4 = 16
                int r5 = java.lang.Integer.parseInt(r5, r4)     // Catch:{ Exception -> 0x0164 }
                r22 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
                r5 = r5 | r22
                r10.patternBgColor = r5     // Catch:{ Exception -> 0x0164 }
                int r5 = r0.length()     // Catch:{ Exception -> 0x0164 }
                r10 = 13
                if (r5 < r10) goto L_0x011f
                char r5 = r0.charAt(r14)     // Catch:{ Exception -> 0x0164 }
                boolean r5 = org.telegram.messenger.AndroidUtilities.isValidWallChar(r5)     // Catch:{ Exception -> 0x0164 }
                if (r5 == 0) goto L_0x011f
                org.telegram.ui.ActionBar.Theme$ThemeInfo r5 = r1.themeInfo     // Catch:{ Exception -> 0x0164 }
                r14 = 7
                java.lang.String r14 = r0.substring(r14, r10)     // Catch:{ Exception -> 0x0164 }
                int r14 = java.lang.Integer.parseInt(r14, r4)     // Catch:{ Exception -> 0x0164 }
                r14 = r14 | r22
                r5.patternBgGradientColor1 = r14     // Catch:{ Exception -> 0x0164 }
            L_0x011f:
                int r5 = r0.length()     // Catch:{ Exception -> 0x0164 }
                r14 = 20
                if (r5 < r14) goto L_0x0141
                char r5 = r0.charAt(r10)     // Catch:{ Exception -> 0x0164 }
                boolean r5 = org.telegram.messenger.AndroidUtilities.isValidWallChar(r5)     // Catch:{ Exception -> 0x0164 }
                if (r5 == 0) goto L_0x0141
                org.telegram.ui.ActionBar.Theme$ThemeInfo r5 = r1.themeInfo     // Catch:{ Exception -> 0x0164 }
                r10 = 14
                java.lang.String r10 = r0.substring(r10, r14)     // Catch:{ Exception -> 0x0164 }
                int r10 = java.lang.Integer.parseInt(r10, r4)     // Catch:{ Exception -> 0x0164 }
                r10 = r10 | r22
                r5.patternBgGradientColor2 = r10     // Catch:{ Exception -> 0x0164 }
            L_0x0141:
                int r5 = r0.length()     // Catch:{ Exception -> 0x0164 }
                r10 = 27
                if (r5 != r10) goto L_0x016a
                char r5 = r0.charAt(r14)     // Catch:{ Exception -> 0x0164 }
                boolean r5 = org.telegram.messenger.AndroidUtilities.isValidWallChar(r5)     // Catch:{ Exception -> 0x0164 }
                if (r5 == 0) goto L_0x016a
                org.telegram.ui.ActionBar.Theme$ThemeInfo r5 = r1.themeInfo     // Catch:{ Exception -> 0x0164 }
                r10 = 21
                java.lang.String r10 = r0.substring(r10)     // Catch:{ Exception -> 0x0164 }
                int r4 = java.lang.Integer.parseInt(r10, r4)     // Catch:{ Exception -> 0x0164 }
                r4 = r4 | r22
                r5.patternBgGradientColor3 = r4     // Catch:{ Exception -> 0x0164 }
                goto L_0x016a
            L_0x0164:
                r0 = move-exception
                goto L_0x0170
            L_0x0166:
                r20 = r4
                r21 = r5
            L_0x016a:
                goto L_0x0170
            L_0x016b:
                r0 = move-exception
                r20 = r4
                r21 = r5
            L_0x0170:
                java.lang.String r0 = "rotation"
                java.lang.String r0 = r11.getQueryParameter(r0)     // Catch:{ Exception -> 0x0189 }
                boolean r4 = android.text.TextUtils.isEmpty(r0)     // Catch:{ Exception -> 0x0189 }
                if (r4 != 0) goto L_0x0188
                org.telegram.ui.ActionBar.Theme$ThemeInfo r4 = r1.themeInfo     // Catch:{ Exception -> 0x0189 }
                java.lang.Integer r5 = org.telegram.messenger.Utilities.parseInt(r0)     // Catch:{ Exception -> 0x0189 }
                int r5 = r5.intValue()     // Catch:{ Exception -> 0x0189 }
                r4.patternBgGradientRotation = r5     // Catch:{ Exception -> 0x0189 }
            L_0x0188:
                goto L_0x018a
            L_0x0189:
                r0 = move-exception
            L_0x018a:
                java.lang.String r0 = "intensity"
                java.lang.String r0 = r11.getQueryParameter(r0)     // Catch:{ all -> 0x01eb }
                boolean r4 = android.text.TextUtils.isEmpty(r0)     // Catch:{ all -> 0x01eb }
                if (r4 != 0) goto L_0x01a2
                org.telegram.ui.ActionBar.Theme$ThemeInfo r4 = r1.themeInfo     // Catch:{ all -> 0x01eb }
                java.lang.Integer r5 = org.telegram.messenger.Utilities.parseInt(r0)     // Catch:{ all -> 0x01eb }
                int r5 = r5.intValue()     // Catch:{ all -> 0x01eb }
                r4.patternIntensity = r5     // Catch:{ all -> 0x01eb }
            L_0x01a2:
                org.telegram.ui.ActionBar.Theme$ThemeInfo r4 = r1.themeInfo     // Catch:{ all -> 0x01eb }
                int r4 = r4.patternIntensity     // Catch:{ all -> 0x01eb }
                if (r4 != 0) goto L_0x01b3
                org.telegram.ui.ActionBar.Theme$ThemeInfo r4 = r1.themeInfo     // Catch:{ all -> 0x01eb }
                r5 = 50
                r4.patternIntensity = r5     // Catch:{ all -> 0x01eb }
                goto L_0x01b3
            L_0x01af:
                r20 = r4
                r21 = r5
            L_0x01b3:
                r23 = r3
                r21 = r7
                goto L_0x02df
            L_0x01b9:
                r0 = move-exception
                r17 = r5
                r2 = r0
                r21 = r7
                r4 = r16
                goto L_0x033b
            L_0x01c3:
                r0 = move-exception
                r16 = r4
                r17 = r5
                r2 = r0
                r21 = r7
                goto L_0x033b
            L_0x01cd:
                r16 = r4
                r17 = r5
                r18 = r10
                r19 = r14
                java.lang.String r0 = "WPS"
                boolean r0 = r3.startsWith(r0)     // Catch:{ all -> 0x02e4 }
                if (r0 == 0) goto L_0x01f3
                org.telegram.ui.ActionBar.Theme$ThemeInfo r0 = r1.themeInfo     // Catch:{ all -> 0x01eb }
                int r4 = r8 + r15
                r0.previewWallpaperOffset = r4     // Catch:{ all -> 0x01eb }
                r0 = 1
                r4 = r0
                r21 = r7
                r0 = r19
                goto L_0x0309
            L_0x01eb:
                r0 = move-exception
                r2 = r0
                r21 = r7
                r4 = r16
                goto L_0x033b
            L_0x01f3:
                r0 = 61
                int r0 = r3.indexOf(r0)     // Catch:{ all -> 0x02e4 }
                r4 = r0
                r5 = -1
                if (r0 == r5) goto L_0x02d9
                r10 = 0
                java.lang.String r0 = r3.substring(r10, r4)     // Catch:{ all -> 0x02e4 }
                r10 = r0
                boolean r0 = r10.equals(r2)     // Catch:{ all -> 0x02e4 }
                java.lang.String r11 = "key_chat_wallpaper_gradient_to3"
                java.lang.String r14 = "key_chat_wallpaper_gradient_to2"
                java.lang.String r5 = "chat_wallpaper_gradient_to"
                java.lang.String r6 = "chat_wallpaper"
                r21 = r7
                java.lang.String r7 = "chat_outBubble"
                if (r0 != 0) goto L_0x0238
                boolean r0 = r10.equals(r7)     // Catch:{ all -> 0x02d3 }
                if (r0 != 0) goto L_0x0238
                boolean r0 = r10.equals(r6)     // Catch:{ all -> 0x02d3 }
                if (r0 != 0) goto L_0x0238
                boolean r0 = r10.equals(r5)     // Catch:{ all -> 0x02d3 }
                if (r0 != 0) goto L_0x0238
                boolean r0 = r10.equals(r14)     // Catch:{ all -> 0x02d3 }
                if (r0 != 0) goto L_0x0238
                boolean r0 = r10.equals(r11)     // Catch:{ all -> 0x02d3 }
                if (r0 == 0) goto L_0x0234
                goto L_0x0238
            L_0x0234:
                r23 = r3
                goto L_0x02df
            L_0x0238:
                int r0 = r4 + 1
                java.lang.String r0 = r3.substring(r0)     // Catch:{ all -> 0x02d3 }
                r22 = r0
                int r0 = r22.length()     // Catch:{ all -> 0x02d3 }
                if (r0 <= 0) goto L_0x0267
                r23 = r3
                r3 = r22
                r22 = r4
                r4 = 0
                char r0 = r3.charAt(r4)     // Catch:{ all -> 0x02d3 }
                r4 = 35
                if (r0 != r4) goto L_0x026d
                int r0 = android.graphics.Color.parseColor(r3)     // Catch:{ Exception -> 0x025a }
            L_0x0259:
                goto L_0x0275
            L_0x025a:
                r0 = move-exception
                r4 = r0
                r0 = r4
                java.lang.Integer r4 = org.telegram.messenger.Utilities.parseInt(r3)     // Catch:{ all -> 0x02d3 }
                int r4 = r4.intValue()     // Catch:{ all -> 0x02d3 }
                r0 = r4
                goto L_0x0259
            L_0x0267:
                r23 = r3
                r3 = r22
                r22 = r4
            L_0x026d:
                java.lang.Integer r0 = org.telegram.messenger.Utilities.parseInt(r3)     // Catch:{ all -> 0x02d3 }
                int r0 = r0.intValue()     // Catch:{ all -> 0x02d3 }
            L_0x0275:
                int r4 = r10.hashCode()     // Catch:{ all -> 0x02d3 }
                switch(r4) {
                    case -1625862693: goto L_0x02a5;
                    case -633951866: goto L_0x029d;
                    case 1269980952: goto L_0x0295;
                    case 1381936524: goto L_0x028d;
                    case 1381936525: goto L_0x0285;
                    case 2052611411: goto L_0x027d;
                    default: goto L_0x027c;
                }     // Catch:{ all -> 0x02d3 }
            L_0x027c:
                goto L_0x02ad
            L_0x027d:
                boolean r4 = r10.equals(r7)     // Catch:{ all -> 0x02d3 }
                if (r4 == 0) goto L_0x027c
                r4 = 1
                goto L_0x02ae
            L_0x0285:
                boolean r4 = r10.equals(r11)     // Catch:{ all -> 0x02d3 }
                if (r4 == 0) goto L_0x027c
                r4 = 5
                goto L_0x02ae
            L_0x028d:
                boolean r4 = r10.equals(r14)     // Catch:{ all -> 0x02d3 }
                if (r4 == 0) goto L_0x027c
                r4 = 4
                goto L_0x02ae
            L_0x0295:
                boolean r4 = r10.equals(r2)     // Catch:{ all -> 0x02d3 }
                if (r4 == 0) goto L_0x027c
                r4 = 0
                goto L_0x02ae
            L_0x029d:
                boolean r4 = r10.equals(r5)     // Catch:{ all -> 0x02d3 }
                if (r4 == 0) goto L_0x027c
                r4 = 3
                goto L_0x02ae
            L_0x02a5:
                boolean r4 = r10.equals(r6)     // Catch:{ all -> 0x02d3 }
                if (r4 == 0) goto L_0x027c
                r4 = 2
                goto L_0x02ae
            L_0x02ad:
                r4 = -1
            L_0x02ae:
                switch(r4) {
                    case 0: goto L_0x02cd;
                    case 1: goto L_0x02c7;
                    case 2: goto L_0x02c1;
                    case 3: goto L_0x02bc;
                    case 4: goto L_0x02b7;
                    case 5: goto L_0x02b2;
                    default: goto L_0x02b1;
                }     // Catch:{ all -> 0x02d3 }
            L_0x02b1:
                goto L_0x02df
            L_0x02b2:
                org.telegram.ui.ActionBar.Theme$ThemeInfo r4 = r1.themeInfo     // Catch:{ all -> 0x02d3 }
                r4.previewBackgroundGradientColor3 = r0     // Catch:{ all -> 0x02d3 }
                goto L_0x02df
            L_0x02b7:
                org.telegram.ui.ActionBar.Theme$ThemeInfo r4 = r1.themeInfo     // Catch:{ all -> 0x02d3 }
                r4.previewBackgroundGradientColor2 = r0     // Catch:{ all -> 0x02d3 }
                goto L_0x02df
            L_0x02bc:
                org.telegram.ui.ActionBar.Theme$ThemeInfo r4 = r1.themeInfo     // Catch:{ all -> 0x02d3 }
                r4.previewBackgroundGradientColor1 = r0     // Catch:{ all -> 0x02d3 }
                goto L_0x02df
            L_0x02c1:
                org.telegram.ui.ActionBar.Theme$ThemeInfo r4 = r1.themeInfo     // Catch:{ all -> 0x02d3 }
                r4.setPreviewBackgroundColor(r0)     // Catch:{ all -> 0x02d3 }
                goto L_0x02df
            L_0x02c7:
                org.telegram.ui.ActionBar.Theme$ThemeInfo r4 = r1.themeInfo     // Catch:{ all -> 0x02d3 }
                r4.setPreviewOutColor(r0)     // Catch:{ all -> 0x02d3 }
                goto L_0x02df
            L_0x02cd:
                org.telegram.ui.ActionBar.Theme$ThemeInfo r4 = r1.themeInfo     // Catch:{ all -> 0x02d3 }
                r4.setPreviewInColor(r0)     // Catch:{ all -> 0x02d3 }
                goto L_0x02df
            L_0x02d3:
                r0 = move-exception
                r2 = r0
                r4 = r16
                goto L_0x033b
            L_0x02d9:
                r23 = r3
                r22 = r4
                r21 = r7
            L_0x02df:
                int r12 = r12 + r15
                int r8 = r8 + r15
                r0 = r19
                goto L_0x02f3
            L_0x02e4:
                r0 = move-exception
                r21 = r7
                r2 = r0
                r4 = r16
                goto L_0x033b
            L_0x02eb:
                r16 = r4
                r17 = r5
                r21 = r7
                r18 = r10
            L_0x02f3:
                int r13 = r13 + 1
                r4 = r16
                r5 = r17
                r10 = r18
                r7 = r21
                r6 = 1
                r11 = -1
                goto L_0x0034
            L_0x0301:
                r16 = r4
                r17 = r5
                r21 = r7
                r18 = r10
            L_0x0309:
                if (r4 != 0) goto L_0x032d
                if (r9 != r8) goto L_0x030e
                goto L_0x032d
            L_0x030e:
                java.nio.channels.FileChannel r3 = r21.getChannel()     // Catch:{ all -> 0x0322 }
                long r5 = (long) r8     // Catch:{ all -> 0x0322 }
                r3.position(r5)     // Catch:{ all -> 0x0322 }
                r5 = r17
                r7 = r21
                r6 = 1
                r24 = r8
                r8 = r0
                r0 = r24
                goto L_0x0022
            L_0x0322:
                r0 = move-exception
                r2 = r0
                goto L_0x033b
            L_0x0325:
                r16 = r4
                r17 = r5
                r21 = r7
                r18 = r10
            L_0x032d:
                r21.close()     // Catch:{ all -> 0x0331 }
                goto L_0x0347
            L_0x0331:
                r0 = move-exception
                goto L_0x0344
            L_0x0333:
                r0 = move-exception
                r16 = r4
                r17 = r5
                r21 = r7
                r2 = r0
            L_0x033b:
                r21.close()     // Catch:{ all -> 0x033f }
                goto L_0x0340
            L_0x033f:
                r0 = move-exception
            L_0x0340:
                throw r2     // Catch:{ all -> 0x0331 }
            L_0x0341:
                r0 = move-exception
                r17 = r5
            L_0x0344:
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            L_0x0347:
                org.telegram.ui.ActionBar.Theme$ThemeInfo r0 = r1.themeInfo
                java.lang.String r0 = r0.pathToWallpaper
                if (r0 == 0) goto L_0x03a2
                org.telegram.ui.ActionBar.Theme$ThemeInfo r0 = r1.themeInfo
                boolean r0 = r0.badWallpaper
                if (r0 != 0) goto L_0x03a2
                java.io.File r0 = new java.io.File
                org.telegram.ui.ActionBar.Theme$ThemeInfo r2 = r1.themeInfo
                java.lang.String r2 = r2.pathToWallpaper
                r0.<init>(r2)
                r5 = r0
                boolean r0 = r5.exists()
                if (r0 != 0) goto L_0x03a4
                org.telegram.ui.Cells.ThemesHorizontalListCell r0 = org.telegram.ui.Cells.ThemesHorizontalListCell.this
                java.util.HashMap r0 = r0.loadingWallpapers
                org.telegram.ui.ActionBar.Theme$ThemeInfo r2 = r1.themeInfo
                boolean r0 = r0.containsKey(r2)
                if (r0 != 0) goto L_0x03a0
                org.telegram.ui.Cells.ThemesHorizontalListCell r0 = org.telegram.ui.Cells.ThemesHorizontalListCell.this
                java.util.HashMap r0 = r0.loadingWallpapers
                org.telegram.ui.ActionBar.Theme$ThemeInfo r2 = r1.themeInfo
                java.lang.String r3 = r2.slug
                r0.put(r2, r3)
                org.telegram.tgnet.TLRPC$TL_account_getWallPaper r0 = new org.telegram.tgnet.TLRPC$TL_account_getWallPaper
                r0.<init>()
                org.telegram.tgnet.TLRPC$TL_inputWallPaperSlug r2 = new org.telegram.tgnet.TLRPC$TL_inputWallPaperSlug
                r2.<init>()
                org.telegram.ui.ActionBar.Theme$ThemeInfo r3 = r1.themeInfo
                java.lang.String r3 = r3.slug
                r2.slug = r3
                r0.wallpaper = r2
                org.telegram.ui.ActionBar.Theme$ThemeInfo r3 = r1.themeInfo
                int r3 = r3.account
                org.telegram.tgnet.ConnectionsManager r3 = org.telegram.tgnet.ConnectionsManager.getInstance(r3)
                org.telegram.ui.Cells.ThemesHorizontalListCell$InnerThemeView$$ExternalSyntheticLambda1 r6 = new org.telegram.ui.Cells.ThemesHorizontalListCell$InnerThemeView$$ExternalSyntheticLambda1
                r6.<init>(r1)
                r3.sendRequest(r0, r6)
            L_0x03a0:
                r2 = 0
                return r2
            L_0x03a2:
                r5 = r17
            L_0x03a4:
                org.telegram.ui.ActionBar.Theme$ThemeInfo r0 = r1.themeInfo
                r2 = 1
                r0.previewParsed = r2
                return r2
            L_0x03aa:
                r2 = 0
                return r2
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ThemesHorizontalListCell.InnerThemeView.parseTheme():boolean");
        }

        /* renamed from: lambda$parseTheme$1$org-telegram-ui-Cells-ThemesHorizontalListCell$InnerThemeView  reason: not valid java name */
        public /* synthetic */ void m1538xe745e278(TLObject response, TLRPC.TL_error error) {
            AndroidUtilities.runOnUIThread(new ThemesHorizontalListCell$InnerThemeView$$ExternalSyntheticLambda0(this, response));
        }

        /* renamed from: lambda$parseTheme$0$org-telegram-ui-Cells-ThemesHorizontalListCell$InnerThemeView  reason: not valid java name */
        public /* synthetic */ void m1537xf3b65e37(TLObject response) {
            if (response instanceof TLRPC.TL_wallPaper) {
                TLRPC.WallPaper wallPaper = (TLRPC.WallPaper) response;
                String name = FileLoader.getAttachFileName(wallPaper.document);
                if (!ThemesHorizontalListCell.this.loadingThemes.containsKey(name)) {
                    ThemesHorizontalListCell.this.loadingThemes.put(name, this.themeInfo);
                    FileLoader.getInstance(this.themeInfo.account).loadFile(wallPaper.document, wallPaper, 1, 1);
                    return;
                }
                return;
            }
            this.themeInfo.badWallpaper = true;
        }

        /* access modifiers changed from: private */
        public void applyTheme() {
            this.inDrawable.setColorFilter(new PorterDuffColorFilter(this.themeInfo.getPreviewInColor(), PorterDuff.Mode.MULTIPLY));
            this.outDrawable.setColorFilter(new PorterDuffColorFilter(this.themeInfo.getPreviewOutColor(), PorterDuff.Mode.MULTIPLY));
            if (this.themeInfo.pathToFile == null) {
                updateColors(false);
                this.optionsDrawable = null;
            } else {
                this.optionsDrawable = getResources().getDrawable(NUM).mutate();
                int previewBackgroundColor = this.themeInfo.getPreviewBackgroundColor();
                this.backColor = previewBackgroundColor;
                this.oldBackColor = previewBackgroundColor;
            }
            this.bitmapShader = null;
            this.backgroundDrawable = null;
            double[] hsv = null;
            if (this.themeInfo.previewBackgroundGradientColor1 != 0 && this.themeInfo.previewBackgroundGradientColor2 != 0) {
                MotionBackgroundDrawable motionBackgroundDrawable = new MotionBackgroundDrawable(this.themeInfo.getPreviewBackgroundColor(), this.themeInfo.previewBackgroundGradientColor1, this.themeInfo.previewBackgroundGradientColor2, this.themeInfo.previewBackgroundGradientColor3, true);
                motionBackgroundDrawable.setRoundRadius(AndroidUtilities.dp(6.0f));
                this.backgroundDrawable = motionBackgroundDrawable;
                hsv = AndroidUtilities.rgbToHsv(Color.red(this.themeInfo.getPreviewBackgroundColor()), Color.green(this.themeInfo.getPreviewBackgroundColor()), Color.blue(this.themeInfo.getPreviewBackgroundColor()));
            } else if (this.themeInfo.previewBackgroundGradientColor1 != 0) {
                GradientDrawable drawable = new GradientDrawable(GradientDrawable.Orientation.BL_TR, new int[]{this.themeInfo.getPreviewBackgroundColor(), this.themeInfo.previewBackgroundGradientColor1});
                drawable.setCornerRadius((float) AndroidUtilities.dp(6.0f));
                this.backgroundDrawable = drawable;
                hsv = AndroidUtilities.rgbToHsv(Color.red(this.themeInfo.getPreviewBackgroundColor()), Color.green(this.themeInfo.getPreviewBackgroundColor()), Color.blue(this.themeInfo.getPreviewBackgroundColor()));
            } else if (this.themeInfo.previewWallpaperOffset > 0 || this.themeInfo.pathToWallpaper != null) {
                Bitmap wallpaper = AndroidUtilities.getScaledBitmap((float) AndroidUtilities.dp(76.0f), (float) AndroidUtilities.dp(97.0f), this.themeInfo.pathToWallpaper, this.themeInfo.pathToFile, this.themeInfo.previewWallpaperOffset);
                if (wallpaper != null) {
                    this.backgroundDrawable = new BitmapDrawable(wallpaper);
                    BitmapShader bitmapShader2 = new BitmapShader(wallpaper, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
                    this.bitmapShader = bitmapShader2;
                    this.bitmapPaint.setShader(bitmapShader2);
                    int[] colors = AndroidUtilities.calcDrawableColor(this.backgroundDrawable);
                    hsv = AndroidUtilities.rgbToHsv(Color.red(colors[0]), Color.green(colors[0]), Color.blue(colors[0]));
                }
            } else if (this.themeInfo.getPreviewBackgroundColor() != 0) {
                hsv = AndroidUtilities.rgbToHsv(Color.red(this.themeInfo.getPreviewBackgroundColor()), Color.green(this.themeInfo.getPreviewBackgroundColor()), Color.blue(this.themeInfo.getPreviewBackgroundColor()));
            }
            if (hsv == null || hsv[1] > 0.10000000149011612d || hsv[2] < 0.9599999785423279d) {
                this.hasWhiteBackground = false;
            } else {
                this.hasWhiteBackground = true;
            }
            if (this.themeInfo.getPreviewBackgroundColor() == 0 && this.themeInfo.previewParsed && this.backgroundDrawable == null) {
                Drawable createDefaultWallpaper = Theme.createDefaultWallpaper(100, 200);
                this.backgroundDrawable = createDefaultWallpaper;
                if (createDefaultWallpaper instanceof MotionBackgroundDrawable) {
                    ((MotionBackgroundDrawable) createDefaultWallpaper).setRoundRadius(AndroidUtilities.dp(6.0f));
                }
            }
            invalidate();
        }

        public void setTheme(Theme.ThemeInfo theme, boolean last, boolean first) {
            this.themeInfo = theme;
            this.isFirst = first;
            this.isLast = last;
            this.accentId = theme.currentAccentId;
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.button.getLayoutParams();
            layoutParams.leftMargin = AndroidUtilities.dp(this.isFirst ? 49.0f : 27.0f);
            this.button.setLayoutParams(layoutParams);
            this.placeholderAlpha = 0.0f;
            if (this.themeInfo.pathToFile != null && !this.themeInfo.previewParsed) {
                this.themeInfo.setPreviewInColor(Theme.getDefaultColor("chat_inBubble"));
                this.themeInfo.setPreviewOutColor(Theme.getDefaultColor("chat_outBubble"));
                boolean fileExists = new File(this.themeInfo.pathToFile).exists();
                if ((!(fileExists && parseTheme()) || !fileExists) && this.themeInfo.info != null) {
                    if (this.themeInfo.info.document != null) {
                        this.themeInfo.themeLoaded = false;
                        this.placeholderAlpha = 1.0f;
                        Drawable mutate = getResources().getDrawable(NUM).mutate();
                        this.loadingDrawable = mutate;
                        int color = Theme.getColor("windowBackgroundWhiteGrayText7");
                        this.loadingColor = color;
                        Theme.setDrawableColor(mutate, color);
                        if (!fileExists) {
                            String name = FileLoader.getAttachFileName(this.themeInfo.info.document);
                            if (!ThemesHorizontalListCell.this.loadingThemes.containsKey(name)) {
                                ThemesHorizontalListCell.this.loadingThemes.put(name, this.themeInfo);
                                FileLoader.getInstance(this.themeInfo.account).loadFile(this.themeInfo.info.document, this.themeInfo.info, 1, 1);
                            }
                        }
                    } else {
                        Drawable mutate2 = getResources().getDrawable(NUM).mutate();
                        this.loadingDrawable = mutate2;
                        int color2 = Theme.getColor("windowBackgroundWhiteGrayText7");
                        this.loadingColor = color2;
                        Theme.setDrawableColor(mutate2, color2);
                    }
                }
            }
            applyTheme();
        }

        /* access modifiers changed from: protected */
        public void onAttachedToWindow() {
            super.onAttachedToWindow();
            this.button.setChecked(this.themeInfo == (ThemesHorizontalListCell.this.currentType == 1 ? Theme.getCurrentNightTheme() : Theme.getCurrentTheme()), false);
            Theme.ThemeInfo themeInfo2 = this.themeInfo;
            if (themeInfo2 != null && themeInfo2.info != null && !this.themeInfo.themeLoaded) {
                if (!ThemesHorizontalListCell.this.loadingThemes.containsKey(FileLoader.getAttachFileName(this.themeInfo.info.document)) && !ThemesHorizontalListCell.this.loadingWallpapers.containsKey(this.themeInfo)) {
                    this.themeInfo.themeLoaded = true;
                    this.placeholderAlpha = 0.0f;
                    parseTheme();
                    applyTheme();
                }
            }
        }

        public void updateCurrentThemeCheck() {
            this.button.setChecked(this.themeInfo == (ThemesHorizontalListCell.this.currentType == 1 ? Theme.getCurrentNightTheme() : Theme.getCurrentTheme()), true);
        }

        /* access modifiers changed from: package-private */
        public void updateColors(boolean animate) {
            int backAccent;
            int myAccentColor;
            int accentColor;
            this.oldInColor = this.inColor;
            this.oldOutColor = this.outColor;
            this.oldBackColor = this.backColor;
            this.oldCheckColor = this.checkColor;
            Theme.ThemeAccent accent = this.themeInfo.getAccent(false);
            if (accent != null) {
                accentColor = accent.accentColor;
                myAccentColor = accent.myMessagesAccentColor != 0 ? accent.myMessagesAccentColor : accentColor;
                int backgroundOverrideColor = (int) accent.backgroundOverrideColor;
                backAccent = backgroundOverrideColor != 0 ? backgroundOverrideColor : accentColor;
            } else {
                accentColor = 0;
                myAccentColor = 0;
                backAccent = 0;
            }
            Theme.ThemeInfo themeInfo2 = this.themeInfo;
            this.inColor = Theme.changeColorAccent(themeInfo2, accentColor, themeInfo2.getPreviewInColor());
            Theme.ThemeInfo themeInfo3 = this.themeInfo;
            this.outColor = Theme.changeColorAccent(themeInfo3, myAccentColor, themeInfo3.getPreviewOutColor());
            Theme.ThemeInfo themeInfo4 = this.themeInfo;
            this.backColor = Theme.changeColorAccent(themeInfo4, backAccent, themeInfo4.getPreviewBackgroundColor());
            this.checkColor = this.outColor;
            this.accentId = this.themeInfo.currentAccentId;
            ObjectAnimator objectAnimator = this.accentAnimator;
            if (objectAnimator != null) {
                objectAnimator.cancel();
            }
            if (animate) {
                ObjectAnimator ofFloat = ObjectAnimator.ofFloat(this, "accentState", new float[]{0.0f, 1.0f});
                this.accentAnimator = ofFloat;
                ofFloat.setDuration(200);
                this.accentAnimator.start();
                return;
            }
            setAccentState(1.0f);
        }

        public float getAccentState() {
            return this.accentState;
        }

        public void setAccentState(float state) {
            this.accentState = state;
            this.accentColorChanged = true;
            invalidate();
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            float f;
            float bitmapW;
            Canvas canvas2 = canvas;
            boolean drawContent = true;
            if (this.accentId != this.themeInfo.currentAccentId) {
                updateColors(true);
            }
            int x = this.isFirst ? AndroidUtilities.dp(22.0f) : 0;
            int y = AndroidUtilities.dp(11.0f);
            this.rect.set((float) x, (float) y, (float) (AndroidUtilities.dp(76.0f) + x), (float) (AndroidUtilities.dp(97.0f) + y));
            String text = TextUtils.ellipsize(getThemeName(), this.textPaint, (float) ((getMeasuredWidth() - AndroidUtilities.dp(this.isFirst ? 10.0f : 15.0f)) - (this.isLast ? AndroidUtilities.dp(7.0f) : 0)), TextUtils.TruncateAt.END).toString();
            this.textPaint.setColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            canvas2.drawText(text, (float) (((AndroidUtilities.dp(76.0f) - ((int) Math.ceil((double) this.textPaint.measureText(text)))) / 2) + x), (float) AndroidUtilities.dp(131.0f), this.textPaint);
            if (this.themeInfo.info != null && (this.themeInfo.info.document == null || !this.themeInfo.themeLoaded)) {
                drawContent = false;
            }
            if (drawContent) {
                this.paint.setColor(blend(this.oldBackColor, this.backColor));
                if (this.accentColorChanged) {
                    this.inDrawable.setColorFilter(new PorterDuffColorFilter(blend(this.oldInColor, this.inColor), PorterDuff.Mode.MULTIPLY));
                    this.outDrawable.setColorFilter(new PorterDuffColorFilter(blend(this.oldOutColor, this.outColor), PorterDuff.Mode.MULTIPLY));
                    this.accentColorChanged = false;
                }
                Drawable drawable = this.backgroundDrawable;
                if (drawable == null) {
                    canvas2.drawRoundRect(this.rect, (float) AndroidUtilities.dp(6.0f), (float) AndroidUtilities.dp(6.0f), this.paint);
                } else if (this.bitmapShader != null) {
                    BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
                    float bitmapW2 = (float) bitmapDrawable.getBitmap().getWidth();
                    float bitmapH = (float) bitmapDrawable.getBitmap().getHeight();
                    float scaleW = bitmapW2 / this.rect.width();
                    float scaleH = bitmapH / this.rect.height();
                    this.shaderMatrix.reset();
                    float scale = 1.0f / Math.min(scaleW, scaleH);
                    if (bitmapW2 / scaleH > this.rect.width()) {
                        bitmapW = bitmapW2 / scaleH;
                        BitmapDrawable bitmapDrawable2 = bitmapDrawable;
                        boolean z = drawContent;
                        this.shaderMatrix.setTranslate(((float) x) - ((bitmapW - this.rect.width()) / 2.0f), (float) y);
                    } else {
                        boolean z2 = drawContent;
                        this.shaderMatrix.setTranslate((float) x, ((float) y) - (((bitmapH / scaleW) - this.rect.height()) / 2.0f));
                        bitmapW = bitmapW2;
                    }
                    this.shaderMatrix.preScale(scale, scale);
                    this.bitmapShader.setLocalMatrix(this.shaderMatrix);
                    float f2 = bitmapW;
                    canvas2.drawRoundRect(this.rect, (float) AndroidUtilities.dp(6.0f), (float) AndroidUtilities.dp(6.0f), this.bitmapPaint);
                } else {
                    drawable.setBounds((int) this.rect.left, (int) this.rect.top, (int) this.rect.right, (int) this.rect.bottom);
                    this.backgroundDrawable.draw(canvas2);
                }
                this.button.setColor(NUM, -1);
                if (this.themeInfo.accentBaseColor != 0) {
                    if ("Day".equals(this.themeInfo.name) || "Arctic Blue".equals(this.themeInfo.name)) {
                        this.button.setColor(-5000269, blend(this.oldCheckColor, this.checkColor));
                        Theme.chat_instantViewRectPaint.setColor(NUM);
                        canvas2.drawRoundRect(this.rect, (float) AndroidUtilities.dp(6.0f), (float) AndroidUtilities.dp(6.0f), Theme.chat_instantViewRectPaint);
                        f = 6.0f;
                    } else {
                        f = 6.0f;
                    }
                } else if (this.hasWhiteBackground) {
                    this.button.setColor(-5000269, this.themeInfo.getPreviewOutColor());
                    Theme.chat_instantViewRectPaint.setColor(NUM);
                    f = 6.0f;
                    canvas2.drawRoundRect(this.rect, (float) AndroidUtilities.dp(6.0f), (float) AndroidUtilities.dp(6.0f), Theme.chat_instantViewRectPaint);
                } else {
                    f = 6.0f;
                }
                this.inDrawable.setBounds(AndroidUtilities.dp(f) + x, AndroidUtilities.dp(22.0f), AndroidUtilities.dp(49.0f) + x, AndroidUtilities.dp(36.0f));
                this.inDrawable.draw(canvas2);
                this.outDrawable.setBounds(AndroidUtilities.dp(27.0f) + x, AndroidUtilities.dp(41.0f), AndroidUtilities.dp(70.0f) + x, AndroidUtilities.dp(55.0f));
                this.outDrawable.draw(canvas2);
                if (this.optionsDrawable != null && ThemesHorizontalListCell.this.currentType == 0) {
                    int x2 = ((int) this.rect.right) - AndroidUtilities.dp(16.0f);
                    int y2 = ((int) this.rect.top) + AndroidUtilities.dp(6.0f);
                    Drawable drawable2 = this.optionsDrawable;
                    drawable2.setBounds(x2, y2, drawable2.getIntrinsicWidth() + x2, this.optionsDrawable.getIntrinsicHeight() + y2);
                    this.optionsDrawable.draw(canvas2);
                }
            }
            if (this.themeInfo.info != null && this.themeInfo.info.document == null) {
                this.button.setAlpha(0.0f);
                Theme.chat_instantViewRectPaint.setColor(NUM);
                canvas2.drawRoundRect(this.rect, (float) AndroidUtilities.dp(6.0f), (float) AndroidUtilities.dp(6.0f), Theme.chat_instantViewRectPaint);
                if (this.loadingDrawable != null) {
                    int newColor = Theme.getColor("windowBackgroundWhiteGrayText7");
                    if (this.loadingColor != newColor) {
                        Drawable drawable3 = this.loadingDrawable;
                        this.loadingColor = newColor;
                        Theme.setDrawableColor(drawable3, newColor);
                    }
                    int x3 = (int) (this.rect.centerX() - ((float) (this.loadingDrawable.getIntrinsicWidth() / 2)));
                    int y3 = (int) (this.rect.centerY() - ((float) (this.loadingDrawable.getIntrinsicHeight() / 2)));
                    Drawable drawable4 = this.loadingDrawable;
                    drawable4.setBounds(x3, y3, drawable4.getIntrinsicWidth() + x3, this.loadingDrawable.getIntrinsicHeight() + y3);
                    this.loadingDrawable.draw(canvas2);
                }
            } else if ((this.themeInfo.info != null && !this.themeInfo.themeLoaded) || this.placeholderAlpha > 0.0f) {
                this.button.setAlpha(1.0f - this.placeholderAlpha);
                this.paint.setColor(Theme.getColor("windowBackgroundGray"));
                this.paint.setAlpha((int) (this.placeholderAlpha * 255.0f));
                canvas2.drawRoundRect(this.rect, (float) AndroidUtilities.dp(6.0f), (float) AndroidUtilities.dp(6.0f), this.paint);
                if (this.loadingDrawable != null) {
                    int newColor2 = Theme.getColor("windowBackgroundWhiteGrayText7");
                    if (this.loadingColor != newColor2) {
                        Drawable drawable5 = this.loadingDrawable;
                        this.loadingColor = newColor2;
                        Theme.setDrawableColor(drawable5, newColor2);
                    }
                    int x4 = (int) (this.rect.centerX() - ((float) (this.loadingDrawable.getIntrinsicWidth() / 2)));
                    int y4 = (int) (this.rect.centerY() - ((float) (this.loadingDrawable.getIntrinsicHeight() / 2)));
                    this.loadingDrawable.setAlpha((int) (this.placeholderAlpha * 255.0f));
                    Drawable drawable6 = this.loadingDrawable;
                    drawable6.setBounds(x4, y4, drawable6.getIntrinsicWidth() + x4, this.loadingDrawable.getIntrinsicHeight() + y4);
                    this.loadingDrawable.draw(canvas2);
                }
                if (this.themeInfo.themeLoaded) {
                    long newTime = SystemClock.elapsedRealtime();
                    long dt = Math.min(17, newTime - this.lastDrawTime);
                    this.lastDrawTime = newTime;
                    float f3 = this.placeholderAlpha - (((float) dt) / 180.0f);
                    this.placeholderAlpha = f3;
                    if (f3 < 0.0f) {
                        this.placeholderAlpha = 0.0f;
                    }
                    invalidate();
                }
            } else if (this.button.getAlpha() != 1.0f) {
                this.button.setAlpha(1.0f);
            }
        }

        private String getThemeName() {
            String name = this.themeInfo.getName();
            if (name.toLowerCase().endsWith(".attheme")) {
                return name.substring(0, name.lastIndexOf(46));
            }
            return name;
        }

        private int blend(int color1, int color2) {
            float f = this.accentState;
            if (f == 1.0f) {
                return color2;
            }
            return ((Integer) this.evaluator.evaluate(f, Integer.valueOf(color1), Integer.valueOf(color2))).intValue();
        }

        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
            super.onInitializeAccessibilityNodeInfo(info);
            info.setText(getThemeName());
            info.setClassName(Button.class.getName());
            info.setChecked(this.button.isChecked());
            info.setCheckable(true);
            info.setEnabled(true);
            if (Build.VERSION.SDK_INT >= 21) {
                info.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_CLICK);
                info.addAction(new AccessibilityNodeInfo.AccessibilityAction(32, LocaleController.getString("AccDescrMoreOptions", NUM)));
            }
        }
    }

    public ThemesHorizontalListCell(Context context, int type, ArrayList<Theme.ThemeInfo> def, ArrayList<Theme.ThemeInfo> custom) {
        super(context);
        this.customThemes = custom;
        this.defaultThemes = def;
        this.currentType = type;
        if (type == 2) {
            setBackgroundColor(Theme.getColor("dialogBackground"));
        } else {
            setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        }
        setItemAnimator((RecyclerView.ItemAnimator) null);
        setLayoutAnimation((LayoutAnimationController) null);
        this.horizontalLayoutManager = new LinearLayoutManager(context) {
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        };
        setPadding(0, 0, 0, 0);
        setClipToPadding(false);
        this.horizontalLayoutManager.setOrientation(0);
        setLayoutManager(this.horizontalLayoutManager);
        ThemesListAdapter themesListAdapter = new ThemesListAdapter(context);
        this.adapter = themesListAdapter;
        setAdapter(themesListAdapter);
        setOnItemClickListener((RecyclerListView.OnItemClickListener) new ThemesHorizontalListCell$$ExternalSyntheticLambda2(this));
        setOnItemLongClickListener((RecyclerListView.OnItemLongClickListener) new ThemesHorizontalListCell$$ExternalSyntheticLambda3(this));
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Cells-ThemesHorizontalListCell  reason: not valid java name */
    public /* synthetic */ void m1535lambda$new$0$orgtelegramuiCellsThemesHorizontalListCell(View view1, int position) {
        selectTheme(((InnerThemeView) view1).themeInfo);
        int left = view1.getLeft();
        int right = view1.getRight();
        if (left < 0) {
            smoothScrollBy(left - AndroidUtilities.dp(8.0f), 0);
        } else if (right > getMeasuredWidth()) {
            smoothScrollBy(right - getMeasuredWidth(), 0);
        }
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Cells-ThemesHorizontalListCell  reason: not valid java name */
    public /* synthetic */ boolean m1536lambda$new$1$orgtelegramuiCellsThemesHorizontalListCell(View view12, int position) {
        showOptionsForTheme(((InnerThemeView) view12).themeInfo);
        return true;
    }

    public void selectTheme(Theme.ThemeInfo themeInfo) {
        if (themeInfo.info != null) {
            if (themeInfo.themeLoaded) {
                if (themeInfo.info.document == null) {
                    presentFragment(new ThemeSetUrlActivity(themeInfo, (Theme.ThemeAccent) null, true));
                    return;
                }
            } else {
                return;
            }
        }
        if (!TextUtils.isEmpty(themeInfo.assetName)) {
            Theme.PatternsLoader.createLoader(false);
        }
        SharedPreferences.Editor editor = ApplicationLoader.applicationContext.getSharedPreferences("themeconfig", 0).edit();
        editor.putString((this.currentType == 1 || themeInfo.isDark()) ? "lastDarkTheme" : "lastDayTheme", themeInfo.getKey());
        editor.commit();
        if (this.currentType == 1) {
            if (themeInfo != Theme.getCurrentNightTheme()) {
                Theme.setCurrentNightTheme(themeInfo);
            } else {
                return;
            }
        } else if (themeInfo != Theme.getCurrentTheme()) {
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.needSetDayNightTheme, themeInfo, false, null, -1);
        } else {
            return;
        }
        updateRows();
        int count = getChildCount();
        for (int a = 0; a < count; a++) {
            View child = getChildAt(a);
            if (child instanceof InnerThemeView) {
                ((InnerThemeView) child).updateCurrentThemeCheck();
            }
        }
        EmojiThemes.saveCustomTheme(themeInfo, themeInfo.currentAccentId);
    }

    public void setDrawDivider(boolean draw) {
        this.drawDivider = draw;
    }

    public void notifyDataSetChanged(int width) {
        if (this.prevCount != this.adapter.getItemCount()) {
            this.adapter.notifyDataSetChanged();
            if (this.prevThemeInfo != (this.currentType == 1 ? Theme.getCurrentNightTheme() : Theme.getCurrentTheme())) {
                scrollToCurrentTheme(width, false);
            }
        }
    }

    public boolean onInterceptTouchEvent(MotionEvent e) {
        if (!(getParent() == null || getParent().getParent() == null)) {
            getParent().getParent().requestDisallowInterceptTouchEvent(canScrollHorizontally(-1));
        }
        return super.onInterceptTouchEvent(e);
    }

    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.drawDivider) {
            canvas.drawLine(LocaleController.isRTL ? 0.0f : (float) AndroidUtilities.dp(20.0f), (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(20.0f) : 0)), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
        }
    }

    public void setBackgroundColor(int color) {
        super.setBackgroundColor(color);
        invalidateViews();
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        for (int a = 0; a < 3; a++) {
            NotificationCenter.getInstance(a).addObserver(this, NotificationCenter.fileLoaded);
            NotificationCenter.getInstance(a).addObserver(this, NotificationCenter.fileLoadFailed);
        }
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        for (int a = 0; a < 3; a++) {
            NotificationCenter.getInstance(a).removeObserver(this, NotificationCenter.fileLoaded);
            NotificationCenter.getInstance(a).removeObserver(this, NotificationCenter.fileLoadFailed);
        }
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.fileLoaded) {
            String fileName = args[0];
            File file = args[1];
            Theme.ThemeInfo info = this.loadingThemes.get(fileName);
            if (info != null) {
                this.loadingThemes.remove(fileName);
                if (this.loadingWallpapers.remove(info) != null) {
                    Utilities.globalQueue.postRunnable(new ThemesHorizontalListCell$$ExternalSyntheticLambda1(this, info, file));
                } else {
                    m1533x15840108(info);
                }
            }
        } else if (id == NotificationCenter.fileLoadFailed) {
            this.loadingThemes.remove(args[0]);
        }
    }

    /* renamed from: lambda$didReceivedNotification$3$org-telegram-ui-Cells-ThemesHorizontalListCell  reason: not valid java name */
    public /* synthetic */ void m1534x4f4ea2e7(Theme.ThemeInfo info, File file) {
        info.badWallpaper = !info.createBackground(file, info.pathToWallpaper);
        AndroidUtilities.runOnUIThread(new ThemesHorizontalListCell$$ExternalSyntheticLambda0(this, info));
    }

    /* access modifiers changed from: private */
    /* renamed from: checkVisibleTheme */
    public void m1533x15840108(Theme.ThemeInfo info) {
        int count = getChildCount();
        for (int a = 0; a < count; a++) {
            View child = getChildAt(a);
            if (child instanceof InnerThemeView) {
                InnerThemeView view = (InnerThemeView) child;
                if (view.themeInfo == info && view.parseTheme()) {
                    view.themeInfo.themeLoaded = true;
                    view.applyTheme();
                }
            }
        }
    }

    public void scrollToCurrentTheme(int width, boolean animated) {
        View parent;
        if (width == 0 && (parent = (View) getParent()) != null) {
            width = parent.getMeasuredWidth();
        }
        if (width != 0) {
            Theme.ThemeInfo currentNightTheme = this.currentType == 1 ? Theme.getCurrentNightTheme() : Theme.getCurrentTheme();
            this.prevThemeInfo = currentNightTheme;
            int index = this.defaultThemes.indexOf(currentNightTheme);
            if (index < 0 && (index = this.customThemes.indexOf(this.prevThemeInfo) + this.defaultThemes.size()) < 0) {
                return;
            }
            if (animated) {
                smoothScrollToPosition(index);
            } else {
                this.horizontalLayoutManager.scrollToPositionWithOffset(index, (width - AndroidUtilities.dp(76.0f)) / 2);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void showOptionsForTheme(Theme.ThemeInfo themeInfo) {
    }

    /* access modifiers changed from: protected */
    public void presentFragment(BaseFragment fragment) {
    }

    /* access modifiers changed from: protected */
    public void updateRows() {
    }
}
