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
import androidx.annotation.Keep;
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
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_theme;
import org.telegram.tgnet.TLRPC$TL_wallPaper;
import org.telegram.tgnet.TLRPC$WallPaper;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.ThemesHorizontalListCell;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.MotionBackgroundDrawable;
import org.telegram.ui.Components.RadioButton;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.ThemeSetUrlActivity;

public class ThemesHorizontalListCell extends RecyclerListView implements NotificationCenter.NotificationCenterDelegate {
    /* access modifiers changed from: private */
    public static byte[] bytes = new byte[1024];
    private ThemesListAdapter adapter;
    /* access modifiers changed from: private */
    public int currentType;
    /* access modifiers changed from: private */
    public ArrayList<Theme.ThemeInfo> darkThemes;
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

    /* access modifiers changed from: protected */
    public void presentFragment(BaseFragment baseFragment) {
    }

    /* access modifiers changed from: protected */
    public void showOptionsForTheme(Theme.ThemeInfo themeInfo) {
    }

    /* access modifiers changed from: protected */
    public void updateRows() {
    }

    private class ThemesListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return false;
        }

        ThemesListAdapter(Context context) {
            this.mContext = context;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            return new RecyclerListView.Holder(new InnerThemeView(this.mContext));
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            int i2;
            ArrayList arrayList;
            InnerThemeView innerThemeView = (InnerThemeView) viewHolder.itemView;
            if (i < ThemesHorizontalListCell.this.defaultThemes.size()) {
                arrayList = ThemesHorizontalListCell.this.defaultThemes;
                i2 = i;
            } else {
                arrayList = ThemesHorizontalListCell.this.darkThemes;
                i2 = i - ThemesHorizontalListCell.this.defaultThemes.size();
            }
            Theme.ThemeInfo themeInfo = (Theme.ThemeInfo) arrayList.get(i2);
            boolean z = true;
            boolean z2 = i == getItemCount() - 1;
            if (i != 0) {
                z = false;
            }
            innerThemeView.setTheme(themeInfo, z2, z);
        }

        public int getItemCount() {
            ThemesHorizontalListCell themesHorizontalListCell = ThemesHorizontalListCell.this;
            return themesHorizontalListCell.prevCount = themesHorizontalListCell.defaultThemes.size() + ThemesHorizontalListCell.this.darkThemes.size();
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
        public void onMeasure(int i, int i2) {
            int i3 = 22;
            int i4 = (this.isLast ? 22 : 15) + 76;
            if (!this.isFirst) {
                i3 = 0;
            }
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp((float) (i4 + i3)), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(148.0f), NUM));
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            Theme.ThemeInfo themeInfo2;
            if (this.optionsDrawable == null || (themeInfo2 = this.themeInfo) == null || ((themeInfo2.info != null && !themeInfo2.themeLoaded) || ThemesHorizontalListCell.this.currentType != 0)) {
                return super.onTouchEvent(motionEvent);
            }
            int action = motionEvent.getAction();
            if (action == 0 || action == 1) {
                float x = motionEvent.getX();
                float y = motionEvent.getY();
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
        /* JADX WARNING: Can't wrap try/catch for region: R(2:92|93) */
        /* JADX WARNING: Can't wrap try/catch for region: R(5:144|150|151|152|153) */
        /* JADX WARNING: Code restructure failed: missing block: B:116:0x023d, code lost:
            r4 = 65535;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:117:0x023e, code lost:
            if (r4 == 0) goto L_0x026a;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:119:0x0241, code lost:
            if (r4 == 1) goto L_0x0264;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:120:0x0243, code lost:
            if (r4 == 2) goto L_0x025e;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:122:0x0246, code lost:
            if (r4 == 3) goto L_0x0259;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:124:0x0249, code lost:
            if (r4 == 4) goto L_0x0254;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:126:0x024c, code lost:
            if (r4 == 5) goto L_0x024f;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:128:0x024f, code lost:
            r1.themeInfo.previewBackgroundGradientColor3 = r3;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:129:0x0254, code lost:
            r1.themeInfo.previewBackgroundGradientColor2 = r3;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:130:0x0259, code lost:
            r1.themeInfo.previewBackgroundGradientColor1 = r3;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:131:0x025e, code lost:
            r1.themeInfo.setPreviewBackgroundColor(r3);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:132:0x0264, code lost:
            r1.themeInfo.setPreviewOutColor(r3);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:133:0x026a, code lost:
            r1.themeInfo.setPreviewInColor(r3);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:84:0x01d7, code lost:
            if (r15.equals("key_chat_wallpaper_gradient_to3") == false) goto L_0x0276;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:93:?, code lost:
            r3 = org.telegram.messenger.Utilities.parseInt(r3).intValue();
         */
        /* JADX WARNING: Missing exception handler attribute for start block: B:152:0x02b0 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:92:0x01f3 */
        /* JADX WARNING: Unknown top exception splitter block from list: {B:152:0x02b0=Splitter:B:152:0x02b0, B:146:0x02a6=Splitter:B:146:0x02a6} */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean parseTheme() {
            /*
                r20 = this;
                r1 = r20
                java.lang.String r0 = "chat_inBubble"
                org.telegram.ui.ActionBar.Theme$ThemeInfo r2 = r1.themeInfo
                if (r2 == 0) goto L_0x0311
                java.lang.String r2 = r2.pathToFile
                if (r2 != 0) goto L_0x000e
                goto L_0x0311
            L_0x000e:
                java.io.File r2 = new java.io.File
                org.telegram.ui.ActionBar.Theme$ThemeInfo r4 = r1.themeInfo
                java.lang.String r4 = r4.pathToFile
                r2.<init>(r4)
                r4 = 1
                java.io.FileInputStream r5 = new java.io.FileInputStream     // Catch:{ all -> 0x02b1 }
                r5.<init>(r2)     // Catch:{ all -> 0x02b1 }
                r2 = 0
                r6 = 0
            L_0x001f:
                byte[] r7 = org.telegram.ui.Cells.ThemesHorizontalListCell.bytes     // Catch:{ all -> 0x02aa }
                int r7 = r5.read(r7)     // Catch:{ all -> 0x02aa }
                r8 = -1
                if (r7 == r8) goto L_0x02a4
                r11 = r2
                r9 = 0
                r10 = 0
            L_0x002d:
                if (r9 >= r7) goto L_0x028b
                byte[] r12 = org.telegram.ui.Cells.ThemesHorizontalListCell.bytes     // Catch:{ all -> 0x02aa }
                byte r12 = r12[r9]     // Catch:{ all -> 0x02aa }
                r13 = 10
                if (r12 != r13) goto L_0x0279
                int r12 = r9 - r10
                int r12 = r12 + r4
                java.lang.String r13 = new java.lang.String     // Catch:{ all -> 0x02aa }
                byte[] r14 = org.telegram.ui.Cells.ThemesHorizontalListCell.bytes     // Catch:{ all -> 0x02aa }
                int r15 = r12 + -1
                java.lang.String r8 = "UTF-8"
                r13.<init>(r14, r10, r15, r8)     // Catch:{ all -> 0x02aa }
                java.lang.String r8 = "WLS="
                boolean r8 = r13.startsWith(r8)     // Catch:{ all -> 0x02aa }
                r14 = 4
                if (r8 == 0) goto L_0x0185
                java.lang.String r8 = r13.substring(r14)     // Catch:{ all -> 0x02aa }
                android.net.Uri r13 = android.net.Uri.parse(r8)     // Catch:{ all -> 0x02aa }
                org.telegram.ui.ActionBar.Theme$ThemeInfo r14 = r1.themeInfo     // Catch:{ all -> 0x02aa }
                java.lang.String r15 = "slug"
                java.lang.String r15 = r13.getQueryParameter(r15)     // Catch:{ all -> 0x02aa }
                r14.slug = r15     // Catch:{ all -> 0x02aa }
                org.telegram.ui.ActionBar.Theme$ThemeInfo r14 = r1.themeInfo     // Catch:{ all -> 0x02aa }
                java.io.File r15 = new java.io.File     // Catch:{ all -> 0x02aa }
                java.io.File r3 = org.telegram.messenger.ApplicationLoader.getFilesDirFixed()     // Catch:{ all -> 0x02aa }
                java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x02aa }
                r4.<init>()     // Catch:{ all -> 0x02aa }
                java.lang.String r8 = org.telegram.messenger.Utilities.MD5(r8)     // Catch:{ all -> 0x02aa }
                r4.append(r8)     // Catch:{ all -> 0x02aa }
                java.lang.String r8 = ".wp"
                r4.append(r8)     // Catch:{ all -> 0x02aa }
                java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x02aa }
                r15.<init>(r3, r4)     // Catch:{ all -> 0x02aa }
                java.lang.String r3 = r15.getAbsolutePath()     // Catch:{ all -> 0x02aa }
                r14.pathToWallpaper = r3     // Catch:{ all -> 0x02aa }
                java.lang.String r3 = "mode"
                java.lang.String r3 = r13.getQueryParameter(r3)     // Catch:{ all -> 0x02aa }
                if (r3 == 0) goto L_0x00b8
                java.lang.String r3 = r3.toLowerCase()     // Catch:{ all -> 0x02aa }
                java.lang.String r4 = " "
                java.lang.String[] r3 = r3.split(r4)     // Catch:{ all -> 0x02aa }
                if (r3 == 0) goto L_0x00b8
                int r4 = r3.length     // Catch:{ all -> 0x02aa }
                if (r4 <= 0) goto L_0x00b8
                r4 = 0
            L_0x00a2:
                int r8 = r3.length     // Catch:{ all -> 0x02aa }
                if (r4 >= r8) goto L_0x00b8
                java.lang.String r8 = "blur"
                r14 = r3[r4]     // Catch:{ all -> 0x02aa }
                boolean r8 = r8.equals(r14)     // Catch:{ all -> 0x02aa }
                if (r8 == 0) goto L_0x00b5
                org.telegram.ui.ActionBar.Theme$ThemeInfo r3 = r1.themeInfo     // Catch:{ all -> 0x02aa }
                r4 = 1
                r3.isBlured = r4     // Catch:{ all -> 0x02aa }
                goto L_0x00b8
            L_0x00b5:
                int r4 = r4 + 1
                goto L_0x00a2
            L_0x00b8:
                java.lang.String r3 = "pattern"
                java.lang.String r3 = r13.getQueryParameter(r3)     // Catch:{ all -> 0x02aa }
                boolean r3 = android.text.TextUtils.isEmpty(r3)     // Catch:{ all -> 0x02aa }
                if (r3 != 0) goto L_0x0270
                java.lang.String r3 = "bg_color"
                java.lang.String r3 = r13.getQueryParameter(r3)     // Catch:{ Exception -> 0x0149 }
                boolean r4 = android.text.TextUtils.isEmpty(r3)     // Catch:{ Exception -> 0x0149 }
                if (r4 != 0) goto L_0x0149
                org.telegram.ui.ActionBar.Theme$ThemeInfo r4 = r1.themeInfo     // Catch:{ Exception -> 0x0149 }
                r8 = 6
                r14 = 0
                java.lang.String r15 = r3.substring(r14, r8)     // Catch:{ Exception -> 0x0149 }
                r14 = 16
                int r15 = java.lang.Integer.parseInt(r15, r14)     // Catch:{ Exception -> 0x0149 }
                r16 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
                r15 = r15 | r16
                r4.patternBgColor = r15     // Catch:{ Exception -> 0x0149 }
                int r4 = r3.length()     // Catch:{ Exception -> 0x0149 }
                r15 = 13
                if (r4 < r15) goto L_0x0105
                char r4 = r3.charAt(r8)     // Catch:{ Exception -> 0x0149 }
                boolean r4 = org.telegram.messenger.AndroidUtilities.isValidWallChar(r4)     // Catch:{ Exception -> 0x0149 }
                if (r4 == 0) goto L_0x0105
                org.telegram.ui.ActionBar.Theme$ThemeInfo r4 = r1.themeInfo     // Catch:{ Exception -> 0x0149 }
                r8 = 7
                java.lang.String r8 = r3.substring(r8, r15)     // Catch:{ Exception -> 0x0149 }
                int r8 = java.lang.Integer.parseInt(r8, r14)     // Catch:{ Exception -> 0x0149 }
                r8 = r8 | r16
                r4.patternBgGradientColor1 = r8     // Catch:{ Exception -> 0x0149 }
            L_0x0105:
                int r4 = r3.length()     // Catch:{ Exception -> 0x0149 }
                r8 = 20
                if (r4 < r8) goto L_0x0127
                char r4 = r3.charAt(r15)     // Catch:{ Exception -> 0x0149 }
                boolean r4 = org.telegram.messenger.AndroidUtilities.isValidWallChar(r4)     // Catch:{ Exception -> 0x0149 }
                if (r4 == 0) goto L_0x0127
                org.telegram.ui.ActionBar.Theme$ThemeInfo r4 = r1.themeInfo     // Catch:{ Exception -> 0x0149 }
                r15 = 14
                java.lang.String r15 = r3.substring(r15, r8)     // Catch:{ Exception -> 0x0149 }
                int r15 = java.lang.Integer.parseInt(r15, r14)     // Catch:{ Exception -> 0x0149 }
                r15 = r15 | r16
                r4.patternBgGradientColor2 = r15     // Catch:{ Exception -> 0x0149 }
            L_0x0127:
                int r4 = r3.length()     // Catch:{ Exception -> 0x0149 }
                r15 = 27
                if (r4 != r15) goto L_0x0149
                char r4 = r3.charAt(r8)     // Catch:{ Exception -> 0x0149 }
                boolean r4 = org.telegram.messenger.AndroidUtilities.isValidWallChar(r4)     // Catch:{ Exception -> 0x0149 }
                if (r4 == 0) goto L_0x0149
                org.telegram.ui.ActionBar.Theme$ThemeInfo r4 = r1.themeInfo     // Catch:{ Exception -> 0x0149 }
                r8 = 21
                java.lang.String r3 = r3.substring(r8)     // Catch:{ Exception -> 0x0149 }
                int r3 = java.lang.Integer.parseInt(r3, r14)     // Catch:{ Exception -> 0x0149 }
                r3 = r3 | r16
                r4.patternBgGradientColor3 = r3     // Catch:{ Exception -> 0x0149 }
            L_0x0149:
                java.lang.String r3 = "rotation"
                java.lang.String r3 = r13.getQueryParameter(r3)     // Catch:{ Exception -> 0x0161 }
                boolean r4 = android.text.TextUtils.isEmpty(r3)     // Catch:{ Exception -> 0x0161 }
                if (r4 != 0) goto L_0x0161
                org.telegram.ui.ActionBar.Theme$ThemeInfo r4 = r1.themeInfo     // Catch:{ Exception -> 0x0161 }
                java.lang.Integer r3 = org.telegram.messenger.Utilities.parseInt(r3)     // Catch:{ Exception -> 0x0161 }
                int r3 = r3.intValue()     // Catch:{ Exception -> 0x0161 }
                r4.patternBgGradientRotation = r3     // Catch:{ Exception -> 0x0161 }
            L_0x0161:
                java.lang.String r3 = "intensity"
                java.lang.String r3 = r13.getQueryParameter(r3)     // Catch:{ all -> 0x02aa }
                boolean r4 = android.text.TextUtils.isEmpty(r3)     // Catch:{ all -> 0x02aa }
                if (r4 != 0) goto L_0x0179
                org.telegram.ui.ActionBar.Theme$ThemeInfo r4 = r1.themeInfo     // Catch:{ all -> 0x02aa }
                java.lang.Integer r3 = org.telegram.messenger.Utilities.parseInt(r3)     // Catch:{ all -> 0x02aa }
                int r3 = r3.intValue()     // Catch:{ all -> 0x02aa }
                r4.patternIntensity = r3     // Catch:{ all -> 0x02aa }
            L_0x0179:
                org.telegram.ui.ActionBar.Theme$ThemeInfo r3 = r1.themeInfo     // Catch:{ all -> 0x02aa }
                int r4 = r3.patternIntensity     // Catch:{ all -> 0x02aa }
                if (r4 != 0) goto L_0x0270
                r4 = 50
                r3.patternIntensity = r4     // Catch:{ all -> 0x02aa }
                goto L_0x0270
            L_0x0185:
                java.lang.String r3 = "WPS"
                boolean r3 = r13.startsWith(r3)     // Catch:{ all -> 0x02aa }
                if (r3 == 0) goto L_0x0197
                org.telegram.ui.ActionBar.Theme$ThemeInfo r3 = r1.themeInfo     // Catch:{ all -> 0x02aa }
                int r12 = r12 + r11
                r3.previewWallpaperOffset = r12     // Catch:{ all -> 0x02aa }
                r19 = r5
                r6 = 1
                goto L_0x028f
            L_0x0197:
                r3 = 61
                int r3 = r13.indexOf(r3)     // Catch:{ all -> 0x02aa }
                r4 = -1
                if (r3 == r4) goto L_0x0270
                r8 = 0
                java.lang.String r15 = r13.substring(r8, r3)     // Catch:{ all -> 0x02aa }
                boolean r8 = r15.equals(r0)     // Catch:{ all -> 0x02aa }
                java.lang.String r4 = "key_chat_wallpaper_gradient_to3"
                java.lang.String r14 = "key_chat_wallpaper_gradient_to2"
                r17 = r6
                java.lang.String r6 = "chat_wallpaper_gradient_to"
                r18 = r7
                java.lang.String r7 = "chat_wallpaper"
                r19 = r5
                java.lang.String r5 = "chat_outBubble"
                if (r8 != 0) goto L_0x01d9
                boolean r8 = r15.equals(r5)     // Catch:{ all -> 0x02a2 }
                if (r8 != 0) goto L_0x01d9
                boolean r8 = r15.equals(r7)     // Catch:{ all -> 0x02a2 }
                if (r8 != 0) goto L_0x01d9
                boolean r8 = r15.equals(r6)     // Catch:{ all -> 0x02a2 }
                if (r8 != 0) goto L_0x01d9
                boolean r8 = r15.equals(r14)     // Catch:{ all -> 0x02a2 }
                if (r8 != 0) goto L_0x01d9
                boolean r8 = r15.equals(r4)     // Catch:{ all -> 0x02a2 }
                if (r8 == 0) goto L_0x0276
            L_0x01d9:
                int r3 = r3 + 1
                java.lang.String r3 = r13.substring(r3)     // Catch:{ all -> 0x02a2 }
                int r8 = r3.length()     // Catch:{ all -> 0x02a2 }
                if (r8 <= 0) goto L_0x01fc
                r8 = 0
                char r13 = r3.charAt(r8)     // Catch:{ all -> 0x02a2 }
                r8 = 35
                if (r13 != r8) goto L_0x01fc
                int r3 = android.graphics.Color.parseColor(r3)     // Catch:{ Exception -> 0x01f3 }
                goto L_0x0204
            L_0x01f3:
                java.lang.Integer r3 = org.telegram.messenger.Utilities.parseInt(r3)     // Catch:{ all -> 0x02a2 }
                int r3 = r3.intValue()     // Catch:{ all -> 0x02a2 }
                goto L_0x0204
            L_0x01fc:
                java.lang.Integer r3 = org.telegram.messenger.Utilities.parseInt(r3)     // Catch:{ all -> 0x02a2 }
                int r3 = r3.intValue()     // Catch:{ all -> 0x02a2 }
            L_0x0204:
                int r8 = r15.hashCode()     // Catch:{ all -> 0x02a2 }
                r13 = 2
                switch(r8) {
                    case -1625862693: goto L_0x0235;
                    case -633951866: goto L_0x022d;
                    case 1269980952: goto L_0x0225;
                    case 1381936524: goto L_0x021d;
                    case 1381936525: goto L_0x0215;
                    case 2052611411: goto L_0x020d;
                    default: goto L_0x020c;
                }     // Catch:{ all -> 0x02a2 }
            L_0x020c:
                goto L_0x023d
            L_0x020d:
                boolean r4 = r15.equals(r5)     // Catch:{ all -> 0x02a2 }
                if (r4 == 0) goto L_0x023d
                r4 = 1
                goto L_0x023e
            L_0x0215:
                boolean r4 = r15.equals(r4)     // Catch:{ all -> 0x02a2 }
                if (r4 == 0) goto L_0x023d
                r4 = 5
                goto L_0x023e
            L_0x021d:
                boolean r4 = r15.equals(r14)     // Catch:{ all -> 0x02a2 }
                if (r4 == 0) goto L_0x023d
                r4 = 4
                goto L_0x023e
            L_0x0225:
                boolean r4 = r15.equals(r0)     // Catch:{ all -> 0x02a2 }
                if (r4 == 0) goto L_0x023d
                r4 = 0
                goto L_0x023e
            L_0x022d:
                boolean r4 = r15.equals(r6)     // Catch:{ all -> 0x02a2 }
                if (r4 == 0) goto L_0x023d
                r4 = 3
                goto L_0x023e
            L_0x0235:
                boolean r4 = r15.equals(r7)     // Catch:{ all -> 0x02a2 }
                if (r4 == 0) goto L_0x023d
                r4 = 2
                goto L_0x023e
            L_0x023d:
                r4 = -1
            L_0x023e:
                if (r4 == 0) goto L_0x026a
                r5 = 1
                if (r4 == r5) goto L_0x0264
                if (r4 == r13) goto L_0x025e
                r5 = 3
                if (r4 == r5) goto L_0x0259
                r5 = 4
                if (r4 == r5) goto L_0x0254
                r5 = 5
                if (r4 == r5) goto L_0x024f
                goto L_0x0276
            L_0x024f:
                org.telegram.ui.ActionBar.Theme$ThemeInfo r4 = r1.themeInfo     // Catch:{ all -> 0x02a2 }
                r4.previewBackgroundGradientColor3 = r3     // Catch:{ all -> 0x02a2 }
                goto L_0x0276
            L_0x0254:
                org.telegram.ui.ActionBar.Theme$ThemeInfo r4 = r1.themeInfo     // Catch:{ all -> 0x02a2 }
                r4.previewBackgroundGradientColor2 = r3     // Catch:{ all -> 0x02a2 }
                goto L_0x0276
            L_0x0259:
                org.telegram.ui.ActionBar.Theme$ThemeInfo r4 = r1.themeInfo     // Catch:{ all -> 0x02a2 }
                r4.previewBackgroundGradientColor1 = r3     // Catch:{ all -> 0x02a2 }
                goto L_0x0276
            L_0x025e:
                org.telegram.ui.ActionBar.Theme$ThemeInfo r4 = r1.themeInfo     // Catch:{ all -> 0x02a2 }
                r4.setPreviewBackgroundColor(r3)     // Catch:{ all -> 0x02a2 }
                goto L_0x0276
            L_0x0264:
                org.telegram.ui.ActionBar.Theme$ThemeInfo r4 = r1.themeInfo     // Catch:{ all -> 0x02a2 }
                r4.setPreviewOutColor(r3)     // Catch:{ all -> 0x02a2 }
                goto L_0x0276
            L_0x026a:
                org.telegram.ui.ActionBar.Theme$ThemeInfo r4 = r1.themeInfo     // Catch:{ all -> 0x02a2 }
                r4.setPreviewInColor(r3)     // Catch:{ all -> 0x02a2 }
                goto L_0x0276
            L_0x0270:
                r19 = r5
                r17 = r6
                r18 = r7
            L_0x0276:
                int r10 = r10 + r12
                int r11 = r11 + r12
                goto L_0x027f
            L_0x0279:
                r19 = r5
                r17 = r6
                r18 = r7
            L_0x027f:
                int r9 = r9 + 1
                r6 = r17
                r7 = r18
                r5 = r19
                r4 = 1
                r8 = -1
                goto L_0x002d
            L_0x028b:
                r19 = r5
                r17 = r6
            L_0x028f:
                if (r6 != 0) goto L_0x02a6
                if (r2 != r11) goto L_0x0294
                goto L_0x02a6
            L_0x0294:
                java.nio.channels.FileChannel r2 = r19.getChannel()     // Catch:{ all -> 0x02a2 }
                long r3 = (long) r11     // Catch:{ all -> 0x02a2 }
                r2.position(r3)     // Catch:{ all -> 0x02a2 }
                r2 = r11
                r5 = r19
                r4 = 1
                goto L_0x001f
            L_0x02a2:
                r0 = move-exception
                goto L_0x02ad
            L_0x02a4:
                r19 = r5
            L_0x02a6:
                r19.close()     // Catch:{ all -> 0x02b1 }
                goto L_0x02b5
            L_0x02aa:
                r0 = move-exception
                r19 = r5
            L_0x02ad:
                r19.close()     // Catch:{ all -> 0x02b0 }
            L_0x02b0:
                throw r0     // Catch:{ all -> 0x02b1 }
            L_0x02b1:
                r0 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            L_0x02b5:
                org.telegram.ui.ActionBar.Theme$ThemeInfo r0 = r1.themeInfo
                java.lang.String r2 = r0.pathToWallpaper
                if (r2 == 0) goto L_0x030b
                boolean r0 = r0.badWallpaper
                if (r0 != 0) goto L_0x030b
                java.io.File r0 = new java.io.File
                org.telegram.ui.ActionBar.Theme$ThemeInfo r2 = r1.themeInfo
                java.lang.String r2 = r2.pathToWallpaper
                r0.<init>(r2)
                boolean r0 = r0.exists()
                if (r0 != 0) goto L_0x030b
                org.telegram.ui.Cells.ThemesHorizontalListCell r0 = org.telegram.ui.Cells.ThemesHorizontalListCell.this
                java.util.HashMap r0 = r0.loadingWallpapers
                org.telegram.ui.ActionBar.Theme$ThemeInfo r2 = r1.themeInfo
                boolean r0 = r0.containsKey(r2)
                if (r0 != 0) goto L_0x0309
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
                java.lang.String r4 = r3.slug
                r2.slug = r4
                r0.wallpaper = r2
                int r2 = r3.account
                org.telegram.tgnet.ConnectionsManager r2 = org.telegram.tgnet.ConnectionsManager.getInstance(r2)
                org.telegram.ui.Cells.-$$Lambda$ThemesHorizontalListCell$InnerThemeView$fddPqOuVBiJytpNKVrusBtqrcbM r3 = new org.telegram.ui.Cells.-$$Lambda$ThemesHorizontalListCell$InnerThemeView$fddPqOuVBiJytpNKVrusBtqrcbM
                r3.<init>()
                r2.sendRequest(r0, r3)
            L_0x0309:
                r2 = 0
                return r2
            L_0x030b:
                org.telegram.ui.ActionBar.Theme$ThemeInfo r0 = r1.themeInfo
                r2 = 1
                r0.previewParsed = r2
                return r2
            L_0x0311:
                r2 = 0
                return r2
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ThemesHorizontalListCell.InnerThemeView.parseTheme():boolean");
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$parseTheme$1 */
        public /* synthetic */ void lambda$parseTheme$1$ThemesHorizontalListCell$InnerThemeView(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
            AndroidUtilities.runOnUIThread(new Runnable(tLObject) {
                public final /* synthetic */ TLObject f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    ThemesHorizontalListCell.InnerThemeView.this.lambda$parseTheme$0$ThemesHorizontalListCell$InnerThemeView(this.f$1);
                }
            });
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$parseTheme$0 */
        public /* synthetic */ void lambda$parseTheme$0$ThemesHorizontalListCell$InnerThemeView(TLObject tLObject) {
            if (tLObject instanceof TLRPC$TL_wallPaper) {
                TLRPC$WallPaper tLRPC$WallPaper = (TLRPC$WallPaper) tLObject;
                String attachFileName = FileLoader.getAttachFileName(tLRPC$WallPaper.document);
                if (!ThemesHorizontalListCell.this.loadingThemes.containsKey(attachFileName)) {
                    ThemesHorizontalListCell.this.loadingThemes.put(attachFileName, this.themeInfo);
                    FileLoader.getInstance(this.themeInfo.account).loadFile(tLRPC$WallPaper.document, tLRPC$WallPaper, 1, 1);
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
            double[] dArr = null;
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
            Theme.ThemeInfo themeInfo2 = this.themeInfo;
            int i = themeInfo2.previewBackgroundGradientColor1;
            if (i != 0 && themeInfo2.previewBackgroundGradientColor2 != 0) {
                int previewBackgroundColor2 = this.themeInfo.getPreviewBackgroundColor();
                Theme.ThemeInfo themeInfo3 = this.themeInfo;
                MotionBackgroundDrawable motionBackgroundDrawable = new MotionBackgroundDrawable(previewBackgroundColor2, themeInfo3.previewBackgroundGradientColor1, themeInfo3.previewBackgroundGradientColor2, themeInfo3.previewBackgroundGradientColor3, true);
                motionBackgroundDrawable.setRoundRadius(AndroidUtilities.dp(6.0f));
                this.backgroundDrawable = motionBackgroundDrawable;
                dArr = AndroidUtilities.rgbToHsv(Color.red(this.themeInfo.getPreviewBackgroundColor()), Color.green(this.themeInfo.getPreviewBackgroundColor()), Color.blue(this.themeInfo.getPreviewBackgroundColor()));
            } else if (i != 0) {
                GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.BL_TR, new int[]{this.themeInfo.getPreviewBackgroundColor(), this.themeInfo.previewBackgroundGradientColor1});
                gradientDrawable.setCornerRadius((float) AndroidUtilities.dp(6.0f));
                this.backgroundDrawable = gradientDrawable;
                dArr = AndroidUtilities.rgbToHsv(Color.red(this.themeInfo.getPreviewBackgroundColor()), Color.green(this.themeInfo.getPreviewBackgroundColor()), Color.blue(this.themeInfo.getPreviewBackgroundColor()));
            } else if (themeInfo2.previewWallpaperOffset > 0 || themeInfo2.pathToWallpaper != null) {
                Theme.ThemeInfo themeInfo4 = this.themeInfo;
                Bitmap scaledBitmap = ThemesHorizontalListCell.getScaledBitmap((float) AndroidUtilities.dp(76.0f), (float) AndroidUtilities.dp(97.0f), themeInfo4.pathToWallpaper, themeInfo4.pathToFile, themeInfo4.previewWallpaperOffset);
                if (scaledBitmap != null) {
                    this.backgroundDrawable = new BitmapDrawable(scaledBitmap);
                    Shader.TileMode tileMode = Shader.TileMode.CLAMP;
                    BitmapShader bitmapShader2 = new BitmapShader(scaledBitmap, tileMode, tileMode);
                    this.bitmapShader = bitmapShader2;
                    this.bitmapPaint.setShader(bitmapShader2);
                    int[] calcDrawableColor = AndroidUtilities.calcDrawableColor(this.backgroundDrawable);
                    dArr = AndroidUtilities.rgbToHsv(Color.red(calcDrawableColor[0]), Color.green(calcDrawableColor[0]), Color.blue(calcDrawableColor[0]));
                }
            } else if (themeInfo2.getPreviewBackgroundColor() != 0) {
                dArr = AndroidUtilities.rgbToHsv(Color.red(this.themeInfo.getPreviewBackgroundColor()), Color.green(this.themeInfo.getPreviewBackgroundColor()), Color.blue(this.themeInfo.getPreviewBackgroundColor()));
            }
            if (dArr == null || dArr[1] > 0.10000000149011612d || dArr[2] < 0.9599999785423279d) {
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

        public void setTheme(Theme.ThemeInfo themeInfo2, boolean z, boolean z2) {
            Theme.ThemeInfo themeInfo3;
            TLRPC$TL_theme tLRPC$TL_theme;
            this.themeInfo = themeInfo2;
            this.isFirst = z2;
            this.isLast = z;
            this.accentId = themeInfo2.currentAccentId;
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.button.getLayoutParams();
            layoutParams.leftMargin = AndroidUtilities.dp(this.isFirst ? 49.0f : 27.0f);
            this.button.setLayoutParams(layoutParams);
            this.placeholderAlpha = 0.0f;
            Theme.ThemeInfo themeInfo4 = this.themeInfo;
            if (themeInfo4.pathToFile != null && !themeInfo4.previewParsed) {
                themeInfo4.setPreviewInColor(Theme.getDefaultColor("chat_inBubble"));
                this.themeInfo.setPreviewOutColor(Theme.getDefaultColor("chat_outBubble"));
                boolean exists = new File(this.themeInfo.pathToFile).exists();
                if ((!(exists && parseTheme()) || !exists) && (tLRPC$TL_theme = themeInfo3.info) != null) {
                    if (tLRPC$TL_theme.document != null) {
                        (themeInfo3 = this.themeInfo).themeLoaded = false;
                        this.placeholderAlpha = 1.0f;
                        Drawable mutate = getResources().getDrawable(NUM).mutate();
                        this.loadingDrawable = mutate;
                        int color = Theme.getColor("windowBackgroundWhiteGrayText7");
                        this.loadingColor = color;
                        Theme.setDrawableColor(mutate, color);
                        if (!exists) {
                            String attachFileName = FileLoader.getAttachFileName(this.themeInfo.info.document);
                            if (!ThemesHorizontalListCell.this.loadingThemes.containsKey(attachFileName)) {
                                ThemesHorizontalListCell.this.loadingThemes.put(attachFileName, this.themeInfo);
                                FileLoader instance = FileLoader.getInstance(this.themeInfo.account);
                                TLRPC$TL_theme tLRPC$TL_theme2 = this.themeInfo.info;
                                instance.loadFile(tLRPC$TL_theme2.document, tLRPC$TL_theme2, 1, 1);
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
            TLRPC$TL_theme tLRPC$TL_theme;
            super.onAttachedToWindow();
            this.button.setChecked(this.themeInfo == (ThemesHorizontalListCell.this.currentType == 1 ? Theme.getCurrentNightTheme() : Theme.getCurrentTheme()), false);
            Theme.ThemeInfo themeInfo2 = this.themeInfo;
            if (themeInfo2 != null && (tLRPC$TL_theme = themeInfo2.info) != null && !themeInfo2.themeLoaded) {
                if (!ThemesHorizontalListCell.this.loadingThemes.containsKey(FileLoader.getAttachFileName(tLRPC$TL_theme.document)) && !ThemesHorizontalListCell.this.loadingWallpapers.containsKey(this.themeInfo)) {
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
        public void updateColors(boolean z) {
            int i;
            int i2;
            this.oldInColor = this.inColor;
            this.oldOutColor = this.outColor;
            this.oldBackColor = this.backColor;
            this.oldCheckColor = this.checkColor;
            int i3 = 0;
            Theme.ThemeAccent accent = this.themeInfo.getAccent(false);
            if (accent != null) {
                i3 = accent.accentColor;
                i = accent.myMessagesAccentColor;
                if (i == 0) {
                    i = i3;
                }
                i2 = (int) accent.backgroundOverrideColor;
                if (i2 == 0) {
                    i2 = i3;
                }
            } else {
                i2 = 0;
                i = 0;
            }
            Theme.ThemeInfo themeInfo2 = this.themeInfo;
            this.inColor = Theme.changeColorAccent(themeInfo2, i3, themeInfo2.getPreviewInColor());
            Theme.ThemeInfo themeInfo3 = this.themeInfo;
            this.outColor = Theme.changeColorAccent(themeInfo3, i, themeInfo3.getPreviewOutColor());
            Theme.ThemeInfo themeInfo4 = this.themeInfo;
            this.backColor = Theme.changeColorAccent(themeInfo4, i2, themeInfo4.getPreviewBackgroundColor());
            this.checkColor = this.outColor;
            this.accentId = this.themeInfo.currentAccentId;
            ObjectAnimator objectAnimator = this.accentAnimator;
            if (objectAnimator != null) {
                objectAnimator.cancel();
            }
            if (z) {
                ObjectAnimator ofFloat = ObjectAnimator.ofFloat(this, "accentState", new float[]{0.0f, 1.0f});
                this.accentAnimator = ofFloat;
                ofFloat.setDuration(200);
                this.accentAnimator.start();
                return;
            }
            setAccentState(1.0f);
        }

        @Keep
        public float getAccentState() {
            return this.accentState;
        }

        @Keep
        public void setAccentState(float f) {
            this.accentState = f;
            this.accentColorChanged = true;
            invalidate();
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            boolean z = true;
            if (this.accentId != this.themeInfo.currentAccentId) {
                updateColors(true);
            }
            int dp = this.isFirst ? AndroidUtilities.dp(22.0f) : 0;
            int dp2 = AndroidUtilities.dp(11.0f);
            float f = (float) dp;
            float f2 = (float) dp2;
            this.rect.set(f, f2, (float) (AndroidUtilities.dp(76.0f) + dp), (float) (dp2 + AndroidUtilities.dp(97.0f)));
            String charSequence = TextUtils.ellipsize(getThemeName(), this.textPaint, (float) ((getMeasuredWidth() - AndroidUtilities.dp(this.isFirst ? 10.0f : 15.0f)) - (this.isLast ? AndroidUtilities.dp(7.0f) : 0)), TextUtils.TruncateAt.END).toString();
            int ceil = (int) Math.ceil((double) this.textPaint.measureText(charSequence));
            this.textPaint.setColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            canvas.drawText(charSequence, (float) (((AndroidUtilities.dp(76.0f) - ceil) / 2) + dp), (float) AndroidUtilities.dp(131.0f), this.textPaint);
            Theme.ThemeInfo themeInfo2 = this.themeInfo;
            TLRPC$TL_theme tLRPC$TL_theme = themeInfo2.info;
            if (tLRPC$TL_theme != null && (tLRPC$TL_theme.document == null || !themeInfo2.themeLoaded)) {
                z = false;
            }
            if (z) {
                this.paint.setColor(blend(this.oldBackColor, this.backColor));
                if (this.accentColorChanged) {
                    this.inDrawable.setColorFilter(new PorterDuffColorFilter(blend(this.oldInColor, this.inColor), PorterDuff.Mode.MULTIPLY));
                    this.outDrawable.setColorFilter(new PorterDuffColorFilter(blend(this.oldOutColor, this.outColor), PorterDuff.Mode.MULTIPLY));
                    this.accentColorChanged = false;
                }
                Drawable drawable = this.backgroundDrawable;
                if (drawable == null) {
                    canvas.drawRoundRect(this.rect, (float) AndroidUtilities.dp(6.0f), (float) AndroidUtilities.dp(6.0f), this.paint);
                } else if (this.bitmapShader != null) {
                    BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
                    float width = (float) bitmapDrawable.getBitmap().getWidth();
                    float height = (float) bitmapDrawable.getBitmap().getHeight();
                    float width2 = width / this.rect.width();
                    float height2 = height / this.rect.height();
                    this.shaderMatrix.reset();
                    float min = 1.0f / Math.min(width2, height2);
                    float f3 = width / height2;
                    if (f3 > this.rect.width()) {
                        this.shaderMatrix.setTranslate(f - ((f3 - this.rect.width()) / 2.0f), f2);
                    } else {
                        this.shaderMatrix.setTranslate(f, f2 - (((height / width2) - this.rect.height()) / 2.0f));
                    }
                    this.shaderMatrix.preScale(min, min);
                    this.bitmapShader.setLocalMatrix(this.shaderMatrix);
                    canvas.drawRoundRect(this.rect, (float) AndroidUtilities.dp(6.0f), (float) AndroidUtilities.dp(6.0f), this.bitmapPaint);
                } else {
                    RectF rectF = this.rect;
                    drawable.setBounds((int) rectF.left, (int) rectF.top, (int) rectF.right, (int) rectF.bottom);
                    this.backgroundDrawable.draw(canvas);
                }
                this.button.setColor(NUM, -1);
                Theme.ThemeInfo themeInfo3 = this.themeInfo;
                if (themeInfo3.accentBaseColor != 0) {
                    if ("Day".equals(themeInfo3.name) || "Arctic Blue".equals(this.themeInfo.name)) {
                        this.button.setColor(-5000269, blend(this.oldCheckColor, this.checkColor));
                        Theme.chat_instantViewRectPaint.setColor(NUM);
                        canvas.drawRoundRect(this.rect, (float) AndroidUtilities.dp(6.0f), (float) AndroidUtilities.dp(6.0f), Theme.chat_instantViewRectPaint);
                    }
                } else if (this.hasWhiteBackground) {
                    this.button.setColor(-5000269, themeInfo3.getPreviewOutColor());
                    Theme.chat_instantViewRectPaint.setColor(NUM);
                    canvas.drawRoundRect(this.rect, (float) AndroidUtilities.dp(6.0f), (float) AndroidUtilities.dp(6.0f), Theme.chat_instantViewRectPaint);
                }
                this.inDrawable.setBounds(AndroidUtilities.dp(6.0f) + dp, AndroidUtilities.dp(22.0f), AndroidUtilities.dp(49.0f) + dp, AndroidUtilities.dp(36.0f));
                this.inDrawable.draw(canvas);
                this.outDrawable.setBounds(AndroidUtilities.dp(27.0f) + dp, AndroidUtilities.dp(41.0f), dp + AndroidUtilities.dp(70.0f), AndroidUtilities.dp(55.0f));
                this.outDrawable.draw(canvas);
                if (this.optionsDrawable != null && ThemesHorizontalListCell.this.currentType == 0) {
                    int dp3 = ((int) this.rect.right) - AndroidUtilities.dp(16.0f);
                    int dp4 = ((int) this.rect.top) + AndroidUtilities.dp(6.0f);
                    Drawable drawable2 = this.optionsDrawable;
                    drawable2.setBounds(dp3, dp4, drawable2.getIntrinsicWidth() + dp3, this.optionsDrawable.getIntrinsicHeight() + dp4);
                    this.optionsDrawable.draw(canvas);
                }
            }
            Theme.ThemeInfo themeInfo4 = this.themeInfo;
            TLRPC$TL_theme tLRPC$TL_theme2 = themeInfo4.info;
            if (tLRPC$TL_theme2 != null && tLRPC$TL_theme2.document == null) {
                this.button.setAlpha(0.0f);
                Theme.chat_instantViewRectPaint.setColor(NUM);
                canvas.drawRoundRect(this.rect, (float) AndroidUtilities.dp(6.0f), (float) AndroidUtilities.dp(6.0f), Theme.chat_instantViewRectPaint);
                if (this.loadingDrawable != null) {
                    int color = Theme.getColor("windowBackgroundWhiteGrayText7");
                    if (this.loadingColor != color) {
                        Drawable drawable3 = this.loadingDrawable;
                        this.loadingColor = color;
                        Theme.setDrawableColor(drawable3, color);
                    }
                    int centerX = (int) (this.rect.centerX() - ((float) (this.loadingDrawable.getIntrinsicWidth() / 2)));
                    int centerY = (int) (this.rect.centerY() - ((float) (this.loadingDrawable.getIntrinsicHeight() / 2)));
                    Drawable drawable4 = this.loadingDrawable;
                    drawable4.setBounds(centerX, centerY, drawable4.getIntrinsicWidth() + centerX, this.loadingDrawable.getIntrinsicHeight() + centerY);
                    this.loadingDrawable.draw(canvas);
                }
            } else if ((tLRPC$TL_theme2 != null && !themeInfo4.themeLoaded) || this.placeholderAlpha > 0.0f) {
                this.button.setAlpha(1.0f - this.placeholderAlpha);
                this.paint.setColor(Theme.getColor("windowBackgroundGray"));
                this.paint.setAlpha((int) (this.placeholderAlpha * 255.0f));
                canvas.drawRoundRect(this.rect, (float) AndroidUtilities.dp(6.0f), (float) AndroidUtilities.dp(6.0f), this.paint);
                if (this.loadingDrawable != null) {
                    int color2 = Theme.getColor("windowBackgroundWhiteGrayText7");
                    if (this.loadingColor != color2) {
                        Drawable drawable5 = this.loadingDrawable;
                        this.loadingColor = color2;
                        Theme.setDrawableColor(drawable5, color2);
                    }
                    int centerX2 = (int) (this.rect.centerX() - ((float) (this.loadingDrawable.getIntrinsicWidth() / 2)));
                    int centerY2 = (int) (this.rect.centerY() - ((float) (this.loadingDrawable.getIntrinsicHeight() / 2)));
                    this.loadingDrawable.setAlpha((int) (this.placeholderAlpha * 255.0f));
                    Drawable drawable6 = this.loadingDrawable;
                    drawable6.setBounds(centerX2, centerY2, drawable6.getIntrinsicWidth() + centerX2, this.loadingDrawable.getIntrinsicHeight() + centerY2);
                    this.loadingDrawable.draw(canvas);
                }
                if (this.themeInfo.themeLoaded) {
                    long elapsedRealtime = SystemClock.elapsedRealtime();
                    long min2 = Math.min(17, elapsedRealtime - this.lastDrawTime);
                    this.lastDrawTime = elapsedRealtime;
                    float f4 = this.placeholderAlpha - (((float) min2) / 180.0f);
                    this.placeholderAlpha = f4;
                    if (f4 < 0.0f) {
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
            return name.toLowerCase().endsWith(".attheme") ? name.substring(0, name.lastIndexOf(46)) : name;
        }

        private int blend(int i, int i2) {
            float f = this.accentState;
            if (f == 1.0f) {
                return i2;
            }
            return ((Integer) this.evaluator.evaluate(f, Integer.valueOf(i), Integer.valueOf(i2))).intValue();
        }

        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
            super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
            accessibilityNodeInfo.setText(getThemeName());
            accessibilityNodeInfo.setClassName(Button.class.getName());
            accessibilityNodeInfo.setChecked(this.button.isChecked());
            accessibilityNodeInfo.setCheckable(true);
            accessibilityNodeInfo.setEnabled(true);
            if (Build.VERSION.SDK_INT >= 21) {
                accessibilityNodeInfo.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_CLICK);
                accessibilityNodeInfo.addAction(new AccessibilityNodeInfo.AccessibilityAction(32, LocaleController.getString("AccDescrMoreOptions", NUM)));
            }
        }
    }

    public ThemesHorizontalListCell(Context context, int i, ArrayList<Theme.ThemeInfo> arrayList, ArrayList<Theme.ThemeInfo> arrayList2) {
        super(context);
        this.darkThemes = arrayList2;
        this.defaultThemes = arrayList;
        this.currentType = i;
        if (i == 2) {
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
        setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener() {
            public final void onItemClick(View view, int i) {
                ThemesHorizontalListCell.this.lambda$new$0$ThemesHorizontalListCell(view, i);
            }
        });
        setOnItemLongClickListener((RecyclerListView.OnItemLongClickListener) new RecyclerListView.OnItemLongClickListener() {
            public final boolean onItemClick(View view, int i) {
                return ThemesHorizontalListCell.this.lambda$new$1$ThemesHorizontalListCell(view, i);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ void lambda$new$0$ThemesHorizontalListCell(View view, int i) {
        selectTheme(((InnerThemeView) view).themeInfo);
        int left = view.getLeft();
        int right = view.getRight();
        if (left < 0) {
            smoothScrollBy(left - AndroidUtilities.dp(8.0f), 0);
        } else if (right > getMeasuredWidth()) {
            smoothScrollBy(right - getMeasuredWidth(), 0);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$1 */
    public /* synthetic */ boolean lambda$new$1$ThemesHorizontalListCell(View view, int i) {
        showOptionsForTheme(((InnerThemeView) view).themeInfo);
        return true;
    }

    public void selectTheme(Theme.ThemeInfo themeInfo) {
        TLRPC$TL_theme tLRPC$TL_theme = themeInfo.info;
        if (tLRPC$TL_theme != null) {
            if (themeInfo.themeLoaded) {
                if (tLRPC$TL_theme.document == null) {
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
        if (this.currentType != 2) {
            SharedPreferences.Editor edit = ApplicationLoader.applicationContext.getSharedPreferences("themeconfig", 0).edit();
            edit.putString((this.currentType == 1 || themeInfo.isDark()) ? "lastDarkTheme" : "lastDayTheme", themeInfo.getKey());
            edit.commit();
        }
        if (this.currentType == 1) {
            if (themeInfo != Theme.getCurrentNightTheme()) {
                Theme.setCurrentNightTheme(themeInfo);
            } else {
                return;
            }
        } else if (themeInfo != Theme.getCurrentTheme()) {
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.needSetDayNightTheme, themeInfo, Boolean.FALSE, null, -1);
        } else {
            return;
        }
        updateRows();
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = getChildAt(i);
            if (childAt instanceof InnerThemeView) {
                ((InnerThemeView) childAt).updateCurrentThemeCheck();
            }
        }
    }

    public void setDrawDivider(boolean z) {
        this.drawDivider = z;
    }

    public void notifyDataSetChanged(int i) {
        if (this.prevCount != this.adapter.getItemCount()) {
            this.adapter.notifyDataSetChanged();
            if (this.prevThemeInfo != (this.currentType == 1 ? Theme.getCurrentNightTheme() : Theme.getCurrentTheme())) {
                scrollToCurrentTheme(i, false);
            }
        }
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        if (!(getParent() == null || getParent().getParent() == null)) {
            getParent().getParent().requestDisallowInterceptTouchEvent(canScrollHorizontally(-1));
        }
        return super.onInterceptTouchEvent(motionEvent);
    }

    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.drawDivider) {
            canvas.drawLine(LocaleController.isRTL ? 0.0f : (float) AndroidUtilities.dp(20.0f), (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(20.0f) : 0)), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:39:0x007c A[SYNTHETIC, Splitter:B:39:0x007c] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static android.graphics.Bitmap getScaledBitmap(float r7, float r8, java.lang.String r9, java.lang.String r10, int r11) {
        /*
            r0 = 0
            android.graphics.BitmapFactory$Options r1 = new android.graphics.BitmapFactory$Options     // Catch:{ all -> 0x0075 }
            r1.<init>()     // Catch:{ all -> 0x0075 }
            r2 = 1
            r1.inJustDecodeBounds = r2     // Catch:{ all -> 0x0075 }
            if (r9 == 0) goto L_0x0010
            android.graphics.BitmapFactory.decodeFile(r9, r1)     // Catch:{ all -> 0x0075 }
            r3 = r0
            goto L_0x0020
        L_0x0010:
            java.io.FileInputStream r3 = new java.io.FileInputStream     // Catch:{ all -> 0x0075 }
            r3.<init>(r10)     // Catch:{ all -> 0x0075 }
            java.nio.channels.FileChannel r10 = r3.getChannel()     // Catch:{ all -> 0x0073 }
            long r4 = (long) r11     // Catch:{ all -> 0x0073 }
            r10.position(r4)     // Catch:{ all -> 0x0073 }
            android.graphics.BitmapFactory.decodeStream(r3, r0, r1)     // Catch:{ all -> 0x0073 }
        L_0x0020:
            int r10 = r1.outWidth     // Catch:{ all -> 0x0073 }
            if (r10 <= 0) goto L_0x006d
            int r4 = r1.outHeight     // Catch:{ all -> 0x0073 }
            if (r4 <= 0) goto L_0x006d
            int r5 = (r7 > r8 ? 1 : (r7 == r8 ? 0 : -1))
            if (r5 <= 0) goto L_0x0031
            if (r10 >= r4) goto L_0x0031
            r6 = r8
            r8 = r7
            r7 = r6
        L_0x0031:
            float r10 = (float) r10     // Catch:{ all -> 0x0073 }
            float r10 = r10 / r7
            float r7 = (float) r4     // Catch:{ all -> 0x0073 }
            float r7 = r7 / r8
            float r7 = java.lang.Math.min(r10, r7)     // Catch:{ all -> 0x0073 }
            r1.inSampleSize = r2     // Catch:{ all -> 0x0073 }
            r8 = 1065353216(0x3var_, float:1.0)
            int r8 = (r7 > r8 ? 1 : (r7 == r8 ? 0 : -1))
            if (r8 <= 0) goto L_0x004c
        L_0x0041:
            int r8 = r1.inSampleSize     // Catch:{ all -> 0x0073 }
            int r8 = r8 * 2
            r1.inSampleSize = r8     // Catch:{ all -> 0x0073 }
            float r8 = (float) r8     // Catch:{ all -> 0x0073 }
            int r8 = (r8 > r7 ? 1 : (r8 == r7 ? 0 : -1))
            if (r8 < 0) goto L_0x0041
        L_0x004c:
            r7 = 0
            r1.inJustDecodeBounds = r7     // Catch:{ all -> 0x0073 }
            if (r9 == 0) goto L_0x0056
            android.graphics.Bitmap r7 = android.graphics.BitmapFactory.decodeFile(r9, r1)     // Catch:{ all -> 0x0073 }
            goto L_0x0062
        L_0x0056:
            java.nio.channels.FileChannel r7 = r3.getChannel()     // Catch:{ all -> 0x0073 }
            long r8 = (long) r11     // Catch:{ all -> 0x0073 }
            r7.position(r8)     // Catch:{ all -> 0x0073 }
            android.graphics.Bitmap r7 = android.graphics.BitmapFactory.decodeStream(r3, r0, r1)     // Catch:{ all -> 0x0073 }
        L_0x0062:
            if (r3 == 0) goto L_0x006c
            r3.close()     // Catch:{ Exception -> 0x0068 }
            goto L_0x006c
        L_0x0068:
            r8 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r8)
        L_0x006c:
            return r7
        L_0x006d:
            if (r3 == 0) goto L_0x0084
            r3.close()     // Catch:{ Exception -> 0x0080 }
            goto L_0x0084
        L_0x0073:
            r7 = move-exception
            goto L_0x0077
        L_0x0075:
            r7 = move-exception
            r3 = r0
        L_0x0077:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r7)     // Catch:{ all -> 0x0085 }
            if (r3 == 0) goto L_0x0084
            r3.close()     // Catch:{ Exception -> 0x0080 }
            goto L_0x0084
        L_0x0080:
            r7 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r7)
        L_0x0084:
            return r0
        L_0x0085:
            r7 = move-exception
            if (r3 == 0) goto L_0x0090
            r3.close()     // Catch:{ Exception -> 0x008c }
            goto L_0x0090
        L_0x008c:
            r8 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r8)
        L_0x0090:
            goto L_0x0092
        L_0x0091:
            throw r7
        L_0x0092:
            goto L_0x0091
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ThemesHorizontalListCell.getScaledBitmap(float, float, java.lang.String, java.lang.String, int):android.graphics.Bitmap");
    }

    public void setBackgroundColor(int i) {
        super.setBackgroundColor(i);
        invalidateViews();
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        for (int i = 0; i < 3; i++) {
            NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.fileDidLoad);
            NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.fileDidFailToLoad);
        }
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        for (int i = 0; i < 3; i++) {
            NotificationCenter.getInstance(i).removeObserver(this, NotificationCenter.fileDidLoad);
            NotificationCenter.getInstance(i).removeObserver(this, NotificationCenter.fileDidFailToLoad);
        }
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.fileDidLoad) {
            String str = objArr[0];
            File file = objArr[1];
            Theme.ThemeInfo themeInfo = this.loadingThemes.get(str);
            if (themeInfo != null) {
                this.loadingThemes.remove(str);
                if (this.loadingWallpapers.remove(themeInfo) != null) {
                    Utilities.globalQueue.postRunnable(new Runnable(themeInfo, file) {
                        public final /* synthetic */ Theme.ThemeInfo f$1;
                        public final /* synthetic */ File f$2;

                        {
                            this.f$1 = r2;
                            this.f$2 = r3;
                        }

                        public final void run() {
                            ThemesHorizontalListCell.this.lambda$didReceivedNotification$3$ThemesHorizontalListCell(this.f$1, this.f$2);
                        }
                    });
                } else {
                    lambda$didReceivedNotification$2(themeInfo);
                }
            }
        } else if (i == NotificationCenter.fileDidFailToLoad) {
            this.loadingThemes.remove(objArr[0]);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$didReceivedNotification$3 */
    public /* synthetic */ void lambda$didReceivedNotification$3$ThemesHorizontalListCell(Theme.ThemeInfo themeInfo, File file) {
        themeInfo.badWallpaper = !themeInfo.createBackground(file, themeInfo.pathToWallpaper);
        AndroidUtilities.runOnUIThread(new Runnable(themeInfo) {
            public final /* synthetic */ Theme.ThemeInfo f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                ThemesHorizontalListCell.this.lambda$didReceivedNotification$2$ThemesHorizontalListCell(this.f$1);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: checkVisibleTheme */
    public void lambda$didReceivedNotification$2(Theme.ThemeInfo themeInfo) {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = getChildAt(i);
            if (childAt instanceof InnerThemeView) {
                InnerThemeView innerThemeView = (InnerThemeView) childAt;
                if (innerThemeView.themeInfo == themeInfo && innerThemeView.parseTheme()) {
                    innerThemeView.themeInfo.themeLoaded = true;
                    innerThemeView.applyTheme();
                }
            }
        }
    }

    public void scrollToCurrentTheme(int i, boolean z) {
        View view;
        if (i == 0 && (view = (View) getParent()) != null) {
            i = view.getMeasuredWidth();
        }
        if (i != 0) {
            Theme.ThemeInfo currentNightTheme = this.currentType == 1 ? Theme.getCurrentNightTheme() : Theme.getCurrentTheme();
            this.prevThemeInfo = currentNightTheme;
            int indexOf = this.defaultThemes.indexOf(currentNightTheme);
            if (indexOf < 0 && (indexOf = this.darkThemes.indexOf(this.prevThemeInfo) + this.defaultThemes.size()) < 0) {
                return;
            }
            if (z) {
                smoothScrollToPosition(indexOf);
            } else {
                this.horizontalLayoutManager.scrollToPositionWithOffset(indexOf, (i - AndroidUtilities.dp(76.0f)) / 2);
            }
        }
    }
}
