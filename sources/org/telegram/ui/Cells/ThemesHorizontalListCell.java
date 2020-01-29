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
import android.os.SystemClock;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LayoutAnimationController;
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
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.ThemesHorizontalListCell;
import org.telegram.ui.Components.LayoutHelper;
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
            return ThemesHorizontalListCell.this.defaultThemes.size() + ThemesHorizontalListCell.this.darkThemes.size();
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
            this.button = new RadioButton(context);
            this.button.setSize(AndroidUtilities.dp(20.0f));
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
        /* JADX WARNING: Code restructure failed: missing block: B:100:0x01cd, code lost:
            if (r3 == 3) goto L_0x01d0;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:102:0x01d0, code lost:
            r1.themeInfo.previewBackgroundGradientColor = r2;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:103:0x01d5, code lost:
            r1.themeInfo.setPreviewBackgroundColor(r2);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:104:0x01db, code lost:
            r1.themeInfo.setPreviewOutColor(r2);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:105:0x01e1, code lost:
            r1.themeInfo.setPreviewInColor(r2);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:121:0x020f, code lost:
            r0 = move-exception;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:122:0x0210, code lost:
            r2 = r0;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:124:?, code lost:
            r5.close();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:68:0x016e, code lost:
            if (r14.equals("chat_wallpaper_gradient_to") == false) goto L_0x01e9;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:77:?, code lost:
            r2 = org.telegram.messenger.Utilities.parseInt(r2).intValue();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:94:0x01c4, code lost:
            r3 = 65535;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:95:0x01c5, code lost:
            if (r3 == 0) goto L_0x01e1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:97:0x01c8, code lost:
            if (r3 == 1) goto L_0x01db;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:98:0x01ca, code lost:
            if (r3 == 2) goto L_0x01d5;
         */
        /* JADX WARNING: Exception block dominator not found, dom blocks: [] */
        /* JADX WARNING: Missing exception handler attribute for start block: B:125:0x0214 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:76:0x018a */
        /* JADX WARNING: Unknown top exception splitter block from list: {B:125:0x0214=Splitter:B:125:0x0214, B:116:0x0209=Splitter:B:116:0x0209} */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean parseTheme() {
            /*
                r17 = this;
                r1 = r17
                java.lang.String r0 = "chat_inBubble"
                org.telegram.ui.ActionBar.Theme$ThemeInfo r2 = r1.themeInfo
                if (r2 == 0) goto L_0x0271
                java.lang.String r2 = r2.pathToFile
                if (r2 != 0) goto L_0x000e
                goto L_0x0271
            L_0x000e:
                java.io.File r4 = new java.io.File
                r4.<init>(r2)
                r2 = 1
                java.io.FileInputStream r5 = new java.io.FileInputStream     // Catch:{ all -> 0x0215 }
                r5.<init>(r4)     // Catch:{ all -> 0x0215 }
                r4 = 0
                r6 = 0
            L_0x001b:
                byte[] r7 = org.telegram.ui.Cells.ThemesHorizontalListCell.bytes     // Catch:{ all -> 0x020d }
                int r7 = r5.read(r7)     // Catch:{ all -> 0x020d }
                r8 = -1
                if (r7 == r8) goto L_0x0209
                r11 = r4
                r9 = 0
                r10 = 0
            L_0x0029:
                if (r9 >= r7) goto L_0x01f6
                byte[] r12 = org.telegram.ui.Cells.ThemesHorizontalListCell.bytes     // Catch:{ all -> 0x020d }
                byte r12 = r12[r9]     // Catch:{ all -> 0x020d }
                r13 = 10
                if (r12 != r13) goto L_0x01ec
                int r12 = r9 - r10
                int r12 = r12 + r2
                java.lang.String r13 = new java.lang.String     // Catch:{ all -> 0x020d }
                byte[] r14 = org.telegram.ui.Cells.ThemesHorizontalListCell.bytes     // Catch:{ all -> 0x020d }
                int r15 = r12 + -1
                java.lang.String r8 = "UTF-8"
                r13.<init>(r14, r10, r15, r8)     // Catch:{ all -> 0x020d }
                java.lang.String r8 = "WLS="
                boolean r8 = r13.startsWith(r8)     // Catch:{ all -> 0x020d }
                if (r8 == 0) goto L_0x0132
                r8 = 4
                java.lang.String r8 = r13.substring(r8)     // Catch:{ all -> 0x020d }
                android.net.Uri r13 = android.net.Uri.parse(r8)     // Catch:{ all -> 0x020d }
                org.telegram.ui.ActionBar.Theme$ThemeInfo r14 = r1.themeInfo     // Catch:{ all -> 0x020d }
                java.lang.String r15 = "slug"
                java.lang.String r15 = r13.getQueryParameter(r15)     // Catch:{ all -> 0x020d }
                r14.slug = r15     // Catch:{ all -> 0x020d }
                org.telegram.ui.ActionBar.Theme$ThemeInfo r14 = r1.themeInfo     // Catch:{ all -> 0x020d }
                java.io.File r15 = new java.io.File     // Catch:{ all -> 0x020d }
                java.io.File r3 = org.telegram.messenger.ApplicationLoader.getFilesDirFixed()     // Catch:{ all -> 0x020d }
                java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x020d }
                r2.<init>()     // Catch:{ all -> 0x020d }
                java.lang.String r8 = org.telegram.messenger.Utilities.MD5(r8)     // Catch:{ all -> 0x020d }
                r2.append(r8)     // Catch:{ all -> 0x020d }
                java.lang.String r8 = ".wp"
                r2.append(r8)     // Catch:{ all -> 0x020d }
                java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x020d }
                r15.<init>(r3, r2)     // Catch:{ all -> 0x020d }
                java.lang.String r2 = r15.getAbsolutePath()     // Catch:{ all -> 0x020d }
                r14.pathToWallpaper = r2     // Catch:{ all -> 0x020d }
                java.lang.String r2 = "mode"
                java.lang.String r2 = r13.getQueryParameter(r2)     // Catch:{ all -> 0x020d }
                if (r2 == 0) goto L_0x00b3
                java.lang.String r2 = r2.toLowerCase()     // Catch:{ all -> 0x020d }
                java.lang.String r3 = " "
                java.lang.String[] r2 = r2.split(r3)     // Catch:{ all -> 0x020d }
                if (r2 == 0) goto L_0x00b3
                int r3 = r2.length     // Catch:{ all -> 0x020d }
                if (r3 <= 0) goto L_0x00b3
                r3 = 0
            L_0x009e:
                int r8 = r2.length     // Catch:{ all -> 0x020d }
                if (r3 >= r8) goto L_0x00b3
                java.lang.String r8 = "blur"
                r14 = r2[r3]     // Catch:{ all -> 0x020d }
                boolean r8 = r8.equals(r14)     // Catch:{ all -> 0x020d }
                if (r8 == 0) goto L_0x00b0
                org.telegram.ui.ActionBar.Theme$ThemeInfo r8 = r1.themeInfo     // Catch:{ all -> 0x020d }
                r14 = 1
                r8.isBlured = r14     // Catch:{ all -> 0x020d }
            L_0x00b0:
                int r3 = r3 + 1
                goto L_0x009e
            L_0x00b3:
                java.lang.String r2 = "pattern"
                java.lang.String r2 = r13.getQueryParameter(r2)     // Catch:{ all -> 0x020d }
                boolean r2 = android.text.TextUtils.isEmpty(r2)     // Catch:{ all -> 0x020d }
                if (r2 != 0) goto L_0x01e7
                java.lang.String r2 = "bg_color"
                java.lang.String r2 = r13.getQueryParameter(r2)     // Catch:{ Exception -> 0x00f4 }
                boolean r3 = android.text.TextUtils.isEmpty(r2)     // Catch:{ Exception -> 0x00f4 }
                if (r3 != 0) goto L_0x00f4
                org.telegram.ui.ActionBar.Theme$ThemeInfo r3 = r1.themeInfo     // Catch:{ Exception -> 0x00f4 }
                r8 = 6
                r14 = 0
                java.lang.String r15 = r2.substring(r14, r8)     // Catch:{ Exception -> 0x00f4 }
                r14 = 16
                int r15 = java.lang.Integer.parseInt(r15, r14)     // Catch:{ Exception -> 0x00f4 }
                r16 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
                r15 = r15 | r16
                r3.patternBgColor = r15     // Catch:{ Exception -> 0x00f4 }
                int r3 = r2.length()     // Catch:{ Exception -> 0x00f4 }
                if (r3 <= r8) goto L_0x00f4
                org.telegram.ui.ActionBar.Theme$ThemeInfo r3 = r1.themeInfo     // Catch:{ Exception -> 0x00f4 }
                r8 = 7
                java.lang.String r2 = r2.substring(r8)     // Catch:{ Exception -> 0x00f4 }
                int r2 = java.lang.Integer.parseInt(r2, r14)     // Catch:{ Exception -> 0x00f4 }
                r2 = r2 | r16
                r3.patternBgGradientColor = r2     // Catch:{ Exception -> 0x00f4 }
            L_0x00f4:
                java.lang.String r2 = "rotation"
                java.lang.String r2 = r13.getQueryParameter(r2)     // Catch:{ Exception -> 0x010c }
                boolean r3 = android.text.TextUtils.isEmpty(r2)     // Catch:{ Exception -> 0x010c }
                if (r3 != 0) goto L_0x010c
                org.telegram.ui.ActionBar.Theme$ThemeInfo r3 = r1.themeInfo     // Catch:{ Exception -> 0x010c }
                java.lang.Integer r2 = org.telegram.messenger.Utilities.parseInt(r2)     // Catch:{ Exception -> 0x010c }
                int r2 = r2.intValue()     // Catch:{ Exception -> 0x010c }
                r3.patternBgGradientRotation = r2     // Catch:{ Exception -> 0x010c }
            L_0x010c:
                java.lang.String r2 = "intensity"
                java.lang.String r2 = r13.getQueryParameter(r2)     // Catch:{ all -> 0x020d }
                boolean r3 = android.text.TextUtils.isEmpty(r2)     // Catch:{ all -> 0x020d }
                if (r3 != 0) goto L_0x0124
                org.telegram.ui.ActionBar.Theme$ThemeInfo r3 = r1.themeInfo     // Catch:{ all -> 0x020d }
                java.lang.Integer r2 = org.telegram.messenger.Utilities.parseInt(r2)     // Catch:{ all -> 0x020d }
                int r2 = r2.intValue()     // Catch:{ all -> 0x020d }
                r3.patternIntensity = r2     // Catch:{ all -> 0x020d }
            L_0x0124:
                org.telegram.ui.ActionBar.Theme$ThemeInfo r2 = r1.themeInfo     // Catch:{ all -> 0x020d }
                int r2 = r2.patternIntensity     // Catch:{ all -> 0x020d }
                if (r2 != 0) goto L_0x01e7
                org.telegram.ui.ActionBar.Theme$ThemeInfo r2 = r1.themeInfo     // Catch:{ all -> 0x020d }
                r3 = 50
                r2.patternIntensity = r3     // Catch:{ all -> 0x020d }
                goto L_0x01e7
            L_0x0132:
                java.lang.String r2 = "WPS"
                boolean r2 = r13.startsWith(r2)     // Catch:{ all -> 0x020d }
                if (r2 == 0) goto L_0x0142
                org.telegram.ui.ActionBar.Theme$ThemeInfo r2 = r1.themeInfo     // Catch:{ all -> 0x020d }
                int r12 = r12 + r11
                r2.previewWallpaperOffset = r12     // Catch:{ all -> 0x020d }
                r6 = 1
                goto L_0x01f8
            L_0x0142:
                r2 = 61
                int r2 = r13.indexOf(r2)     // Catch:{ all -> 0x020d }
                r3 = -1
                if (r2 == r3) goto L_0x01e7
                r8 = 0
                java.lang.String r14 = r13.substring(r8, r2)     // Catch:{ all -> 0x020d }
                boolean r8 = r14.equals(r0)     // Catch:{ all -> 0x020d }
                java.lang.String r15 = "chat_wallpaper_gradient_to"
                java.lang.String r3 = "chat_wallpaper"
                r16 = r6
                java.lang.String r6 = "chat_outBubble"
                if (r8 != 0) goto L_0x0170
                boolean r8 = r14.equals(r6)     // Catch:{ all -> 0x020d }
                if (r8 != 0) goto L_0x0170
                boolean r8 = r14.equals(r3)     // Catch:{ all -> 0x020d }
                if (r8 != 0) goto L_0x0170
                boolean r8 = r14.equals(r15)     // Catch:{ all -> 0x020d }
                if (r8 == 0) goto L_0x01e9
            L_0x0170:
                int r2 = r2 + 1
                java.lang.String r2 = r13.substring(r2)     // Catch:{ all -> 0x020d }
                int r8 = r2.length()     // Catch:{ all -> 0x020d }
                if (r8 <= 0) goto L_0x0193
                r8 = 0
                char r13 = r2.charAt(r8)     // Catch:{ all -> 0x020d }
                r8 = 35
                if (r13 != r8) goto L_0x0193
                int r2 = android.graphics.Color.parseColor(r2)     // Catch:{ Exception -> 0x018a }
                goto L_0x019b
            L_0x018a:
                java.lang.Integer r2 = org.telegram.messenger.Utilities.parseInt(r2)     // Catch:{ all -> 0x020d }
                int r2 = r2.intValue()     // Catch:{ all -> 0x020d }
                goto L_0x019b
            L_0x0193:
                java.lang.Integer r2 = org.telegram.messenger.Utilities.parseInt(r2)     // Catch:{ all -> 0x020d }
                int r2 = r2.intValue()     // Catch:{ all -> 0x020d }
            L_0x019b:
                int r8 = r14.hashCode()     // Catch:{ all -> 0x020d }
                r13 = 2
                switch(r8) {
                    case -1625862693: goto L_0x01bc;
                    case -633951866: goto L_0x01b4;
                    case 1269980952: goto L_0x01ac;
                    case 2052611411: goto L_0x01a4;
                    default: goto L_0x01a3;
                }     // Catch:{ all -> 0x020d }
            L_0x01a3:
                goto L_0x01c4
            L_0x01a4:
                boolean r3 = r14.equals(r6)     // Catch:{ all -> 0x020d }
                if (r3 == 0) goto L_0x01c4
                r3 = 1
                goto L_0x01c5
            L_0x01ac:
                boolean r3 = r14.equals(r0)     // Catch:{ all -> 0x020d }
                if (r3 == 0) goto L_0x01c4
                r3 = 0
                goto L_0x01c5
            L_0x01b4:
                boolean r3 = r14.equals(r15)     // Catch:{ all -> 0x020d }
                if (r3 == 0) goto L_0x01c4
                r3 = 3
                goto L_0x01c5
            L_0x01bc:
                boolean r3 = r14.equals(r3)     // Catch:{ all -> 0x020d }
                if (r3 == 0) goto L_0x01c4
                r3 = 2
                goto L_0x01c5
            L_0x01c4:
                r3 = -1
            L_0x01c5:
                if (r3 == 0) goto L_0x01e1
                r6 = 1
                if (r3 == r6) goto L_0x01db
                if (r3 == r13) goto L_0x01d5
                r6 = 3
                if (r3 == r6) goto L_0x01d0
                goto L_0x01e9
            L_0x01d0:
                org.telegram.ui.ActionBar.Theme$ThemeInfo r3 = r1.themeInfo     // Catch:{ all -> 0x020d }
                r3.previewBackgroundGradientColor = r2     // Catch:{ all -> 0x020d }
                goto L_0x01e9
            L_0x01d5:
                org.telegram.ui.ActionBar.Theme$ThemeInfo r3 = r1.themeInfo     // Catch:{ all -> 0x020d }
                r3.setPreviewBackgroundColor(r2)     // Catch:{ all -> 0x020d }
                goto L_0x01e9
            L_0x01db:
                org.telegram.ui.ActionBar.Theme$ThemeInfo r3 = r1.themeInfo     // Catch:{ all -> 0x020d }
                r3.setPreviewOutColor(r2)     // Catch:{ all -> 0x020d }
                goto L_0x01e9
            L_0x01e1:
                org.telegram.ui.ActionBar.Theme$ThemeInfo r3 = r1.themeInfo     // Catch:{ all -> 0x020d }
                r3.setPreviewInColor(r2)     // Catch:{ all -> 0x020d }
                goto L_0x01e9
            L_0x01e7:
                r16 = r6
            L_0x01e9:
                int r10 = r10 + r12
                int r11 = r11 + r12
                goto L_0x01ee
            L_0x01ec:
                r16 = r6
            L_0x01ee:
                int r9 = r9 + 1
                r6 = r16
                r2 = 1
                r8 = -1
                goto L_0x0029
            L_0x01f6:
                r16 = r6
            L_0x01f8:
                if (r6 != 0) goto L_0x0209
                if (r4 != r11) goto L_0x01fd
                goto L_0x0209
            L_0x01fd:
                java.nio.channels.FileChannel r2 = r5.getChannel()     // Catch:{ all -> 0x020d }
                long r3 = (long) r11     // Catch:{ all -> 0x020d }
                r2.position(r3)     // Catch:{ all -> 0x020d }
                r4 = r11
                r2 = 1
                goto L_0x001b
            L_0x0209:
                r5.close()     // Catch:{ all -> 0x0215 }
                goto L_0x0219
            L_0x020d:
                r0 = move-exception
                throw r0     // Catch:{ all -> 0x020f }
            L_0x020f:
                r0 = move-exception
                r2 = r0
                r5.close()     // Catch:{ all -> 0x0214 }
            L_0x0214:
                throw r2     // Catch:{ all -> 0x0215 }
            L_0x0215:
                r0 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            L_0x0219:
                org.telegram.ui.ActionBar.Theme$ThemeInfo r0 = r1.themeInfo
                java.lang.String r2 = r0.pathToWallpaper
                if (r2 == 0) goto L_0x026b
                boolean r0 = r0.badWallpaper
                if (r0 != 0) goto L_0x026b
                java.io.File r0 = new java.io.File
                r0.<init>(r2)
                boolean r0 = r0.exists()
                if (r0 != 0) goto L_0x026b
                org.telegram.ui.Cells.ThemesHorizontalListCell r0 = org.telegram.ui.Cells.ThemesHorizontalListCell.this
                java.util.HashMap r0 = r0.loadingWallpapers
                org.telegram.ui.ActionBar.Theme$ThemeInfo r2 = r1.themeInfo
                boolean r0 = r0.containsKey(r2)
                if (r0 != 0) goto L_0x0269
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
                org.telegram.ui.Cells.-$$Lambda$ThemesHorizontalListCell$InnerThemeView$aojGvqqAAssFRVGdS8VxlzJW2g8 r3 = new org.telegram.ui.Cells.-$$Lambda$ThemesHorizontalListCell$InnerThemeView$aojGvqqAAssFRVGdS8VxlzJW2g8
                r3.<init>()
                r2.sendRequest(r0, r3)
            L_0x0269:
                r2 = 0
                return r2
            L_0x026b:
                org.telegram.ui.ActionBar.Theme$ThemeInfo r0 = r1.themeInfo
                r2 = 1
                r0.previewParsed = r2
                return r2
            L_0x0271:
                r2 = 0
                return r2
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ThemesHorizontalListCell.InnerThemeView.parseTheme():boolean");
        }

        public /* synthetic */ void lambda$parseTheme$1$ThemesHorizontalListCell$InnerThemeView(TLObject tLObject, TLRPC.TL_error tL_error) {
            AndroidUtilities.runOnUIThread(new Runnable(tLObject) {
                private final /* synthetic */ TLObject f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    ThemesHorizontalListCell.InnerThemeView.this.lambda$null$0$ThemesHorizontalListCell$InnerThemeView(this.f$1);
                }
            });
        }

        public /* synthetic */ void lambda$null$0$ThemesHorizontalListCell$InnerThemeView(TLObject tLObject) {
            if (tLObject instanceof TLRPC.TL_wallPaper) {
                TLRPC.TL_wallPaper tL_wallPaper = (TLRPC.TL_wallPaper) tLObject;
                String attachFileName = FileLoader.getAttachFileName(tL_wallPaper.document);
                if (!ThemesHorizontalListCell.this.loadingThemes.containsKey(attachFileName)) {
                    ThemesHorizontalListCell.this.loadingThemes.put(attachFileName, this.themeInfo);
                    FileLoader.getInstance(this.themeInfo.account).loadFile(tL_wallPaper.document, tL_wallPaper, 1, 1);
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
            if (themeInfo2.previewBackgroundGradientColor != 0) {
                GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.BL_TR, new int[]{themeInfo2.getPreviewBackgroundColor(), this.themeInfo.previewBackgroundGradientColor});
                gradientDrawable.setCornerRadius((float) AndroidUtilities.dp(6.0f));
                this.backgroundDrawable = gradientDrawable;
                dArr = AndroidUtilities.rgbToHsv(Color.red(this.themeInfo.getPreviewBackgroundColor()), Color.green(this.themeInfo.getPreviewBackgroundColor()), Color.blue(this.themeInfo.getPreviewBackgroundColor()));
            } else if (themeInfo2.previewWallpaperOffset > 0 || themeInfo2.pathToWallpaper != null) {
                Theme.ThemeInfo themeInfo3 = this.themeInfo;
                Bitmap scaledBitmap = ThemesHorizontalListCell.getScaledBitmap((float) AndroidUtilities.dp(76.0f), (float) AndroidUtilities.dp(97.0f), themeInfo3.pathToWallpaper, themeInfo3.pathToFile, themeInfo3.previewWallpaperOffset);
                if (scaledBitmap != null) {
                    this.backgroundDrawable = new BitmapDrawable(scaledBitmap);
                    Shader.TileMode tileMode = Shader.TileMode.CLAMP;
                    this.bitmapShader = new BitmapShader(scaledBitmap, tileMode, tileMode);
                    this.bitmapPaint.setShader(this.bitmapShader);
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
                BitmapDrawable bitmapDrawable = (BitmapDrawable) getResources().getDrawable(NUM).mutate();
                Bitmap bitmap = bitmapDrawable.getBitmap();
                Shader.TileMode tileMode2 = Shader.TileMode.REPEAT;
                this.bitmapShader = new BitmapShader(bitmap, tileMode2, tileMode2);
                this.bitmapPaint.setShader(this.bitmapShader);
                this.backgroundDrawable = bitmapDrawable;
            }
            invalidate();
        }

        public void setTheme(Theme.ThemeInfo themeInfo2, boolean z, boolean z2) {
            Theme.ThemeInfo themeInfo3;
            TLRPC.TL_theme tL_theme;
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
                if ((!(exists && parseTheme()) || !exists) && (tL_theme = themeInfo3.info) != null) {
                    if (tL_theme.document != null) {
                        (themeInfo3 = this.themeInfo).themeLoaded = false;
                        this.placeholderAlpha = 1.0f;
                        this.loadingDrawable = getResources().getDrawable(NUM).mutate();
                        Drawable drawable = this.loadingDrawable;
                        int color = Theme.getColor("windowBackgroundWhiteGrayText7");
                        this.loadingColor = color;
                        Theme.setDrawableColor(drawable, color);
                        if (!exists) {
                            String attachFileName = FileLoader.getAttachFileName(this.themeInfo.info.document);
                            if (!ThemesHorizontalListCell.this.loadingThemes.containsKey(attachFileName)) {
                                ThemesHorizontalListCell.this.loadingThemes.put(attachFileName, this.themeInfo);
                                FileLoader instance = FileLoader.getInstance(this.themeInfo.account);
                                TLRPC.TL_theme tL_theme2 = this.themeInfo.info;
                                instance.loadFile(tL_theme2.document, tL_theme2, 1, 1);
                            }
                        }
                    } else {
                        this.loadingDrawable = getResources().getDrawable(NUM).mutate();
                        Drawable drawable2 = this.loadingDrawable;
                        int color2 = Theme.getColor("windowBackgroundWhiteGrayText7");
                        this.loadingColor = color2;
                        Theme.setDrawableColor(drawable2, color2);
                    }
                }
            }
            applyTheme();
        }

        /* access modifiers changed from: protected */
        public void onAttachedToWindow() {
            TLRPC.TL_theme tL_theme;
            super.onAttachedToWindow();
            this.button.setChecked(this.themeInfo == (ThemesHorizontalListCell.this.currentType == 1 ? Theme.getCurrentNightTheme() : Theme.getCurrentTheme()), false);
            Theme.ThemeInfo themeInfo2 = this.themeInfo;
            if (themeInfo2 != null && (tL_theme = themeInfo2.info) != null && !themeInfo2.themeLoaded) {
                if (!ThemesHorizontalListCell.this.loadingThemes.containsKey(FileLoader.getAttachFileName(tL_theme.document)) && !ThemesHorizontalListCell.this.loadingWallpapers.containsKey(this.themeInfo)) {
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
                this.accentAnimator = ObjectAnimator.ofFloat(this, "accentState", new float[]{0.0f, 1.0f});
                this.accentAnimator.setDuration(200);
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
            String name = this.themeInfo.getName();
            if (name.toLowerCase().endsWith(".attheme")) {
                name = name.substring(0, name.lastIndexOf(46));
            }
            String charSequence = TextUtils.ellipsize(name, this.textPaint, (float) ((getMeasuredWidth() - AndroidUtilities.dp(this.isFirst ? 10.0f : 15.0f)) - (this.isLast ? AndroidUtilities.dp(7.0f) : 0)), TextUtils.TruncateAt.END).toString();
            int ceil = (int) Math.ceil((double) this.textPaint.measureText(charSequence));
            this.textPaint.setColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            canvas.drawText(charSequence, (float) (((AndroidUtilities.dp(76.0f) - ceil) / 2) + dp), (float) AndroidUtilities.dp(131.0f), this.textPaint);
            Theme.ThemeInfo themeInfo2 = this.themeInfo;
            TLRPC.TL_theme tL_theme = themeInfo2.info;
            if (tL_theme != null && (tL_theme.document == null || !themeInfo2.themeLoaded)) {
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
            TLRPC.TL_theme tL_theme2 = this.themeInfo.info;
            if (tL_theme2 == null || tL_theme2.document != null) {
                Theme.ThemeInfo themeInfo4 = this.themeInfo;
                if ((themeInfo4.info != null && !themeInfo4.themeLoaded) || this.placeholderAlpha > 0.0f) {
                    this.button.setAlpha(1.0f - this.placeholderAlpha);
                    this.paint.setColor(Theme.getColor("windowBackgroundGray"));
                    this.paint.setAlpha((int) (this.placeholderAlpha * 255.0f));
                    canvas.drawRoundRect(this.rect, (float) AndroidUtilities.dp(6.0f), (float) AndroidUtilities.dp(6.0f), this.paint);
                    if (this.loadingDrawable != null) {
                        int color = Theme.getColor("windowBackgroundWhiteGrayText7");
                        if (this.loadingColor != color) {
                            Drawable drawable3 = this.loadingDrawable;
                            this.loadingColor = color;
                            Theme.setDrawableColor(drawable3, color);
                        }
                        int centerX = (int) (this.rect.centerX() - ((float) (this.loadingDrawable.getIntrinsicWidth() / 2)));
                        int centerY = (int) (this.rect.centerY() - ((float) (this.loadingDrawable.getIntrinsicHeight() / 2)));
                        this.loadingDrawable.setAlpha((int) (this.placeholderAlpha * 255.0f));
                        Drawable drawable4 = this.loadingDrawable;
                        drawable4.setBounds(centerX, centerY, drawable4.getIntrinsicWidth() + centerX, this.loadingDrawable.getIntrinsicHeight() + centerY);
                        this.loadingDrawable.draw(canvas);
                    }
                    if (this.themeInfo.themeLoaded) {
                        long elapsedRealtime = SystemClock.elapsedRealtime();
                        long min2 = Math.min(17, elapsedRealtime - this.lastDrawTime);
                        this.lastDrawTime = elapsedRealtime;
                        this.placeholderAlpha -= ((float) min2) / 180.0f;
                        if (this.placeholderAlpha < 0.0f) {
                            this.placeholderAlpha = 0.0f;
                        }
                        invalidate();
                    }
                } else if (this.button.getAlpha() != 1.0f) {
                    this.button.setAlpha(1.0f);
                }
            } else {
                this.button.setAlpha(0.0f);
                Theme.chat_instantViewRectPaint.setColor(NUM);
                canvas.drawRoundRect(this.rect, (float) AndroidUtilities.dp(6.0f), (float) AndroidUtilities.dp(6.0f), Theme.chat_instantViewRectPaint);
                if (this.loadingDrawable != null) {
                    int color2 = Theme.getColor("windowBackgroundWhiteGrayText7");
                    if (this.loadingColor != color2) {
                        Drawable drawable5 = this.loadingDrawable;
                        this.loadingColor = color2;
                        Theme.setDrawableColor(drawable5, color2);
                    }
                    int centerX2 = (int) (this.rect.centerX() - ((float) (this.loadingDrawable.getIntrinsicWidth() / 2)));
                    int centerY2 = (int) (this.rect.centerY() - ((float) (this.loadingDrawable.getIntrinsicHeight() / 2)));
                    Drawable drawable6 = this.loadingDrawable;
                    drawable6.setBounds(centerX2, centerY2, drawable6.getIntrinsicWidth() + centerX2, this.loadingDrawable.getIntrinsicHeight() + centerY2);
                    this.loadingDrawable.draw(canvas);
                }
            }
        }

        private int blend(int i, int i2) {
            float f = this.accentState;
            if (f == 1.0f) {
                return i2;
            }
            return ((Integer) this.evaluator.evaluate(f, Integer.valueOf(i), Integer.valueOf(i2))).intValue();
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

    public /* synthetic */ boolean lambda$new$1$ThemesHorizontalListCell(View view, int i) {
        showOptionsForTheme(((InnerThemeView) view).themeInfo);
        return true;
    }

    public void selectTheme(Theme.ThemeInfo themeInfo) {
        TLRPC.TL_theme tL_theme = themeInfo.info;
        if (tL_theme != null) {
            if (themeInfo.themeLoaded) {
                if (tL_theme.document == null) {
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
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.needSetDayNightTheme, themeInfo, false, null, -1);
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
        this.adapter.notifyDataSetChanged();
        if (this.prevThemeInfo != (this.currentType == 1 ? Theme.getCurrentNightTheme() : Theme.getCurrentTheme())) {
            scrollToCurrentTheme(i, false);
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

    /* JADX WARNING: Removed duplicated region for block: B:40:0x0086 A[SYNTHETIC, Splitter:B:40:0x0086] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static android.graphics.Bitmap getScaledBitmap(float r7, float r8, java.lang.String r9, java.lang.String r10, int r11) {
        /*
            r0 = 0
            android.graphics.BitmapFactory$Options r1 = new android.graphics.BitmapFactory$Options     // Catch:{ all -> 0x007f }
            r1.<init>()     // Catch:{ all -> 0x007f }
            r2 = 1
            r1.inJustDecodeBounds = r2     // Catch:{ all -> 0x007f }
            if (r9 == 0) goto L_0x0010
            android.graphics.BitmapFactory.decodeFile(r9, r1)     // Catch:{ all -> 0x007f }
            r3 = r0
            goto L_0x0020
        L_0x0010:
            java.io.FileInputStream r3 = new java.io.FileInputStream     // Catch:{ all -> 0x007f }
            r3.<init>(r10)     // Catch:{ all -> 0x007f }
            java.nio.channels.FileChannel r10 = r3.getChannel()     // Catch:{ all -> 0x007d }
            long r4 = (long) r11     // Catch:{ all -> 0x007d }
            r10.position(r4)     // Catch:{ all -> 0x007d }
            android.graphics.BitmapFactory.decodeStream(r3, r0, r1)     // Catch:{ all -> 0x007d }
        L_0x0020:
            int r10 = r1.outWidth     // Catch:{ all -> 0x007d }
            if (r10 <= 0) goto L_0x0077
            int r10 = r1.outHeight     // Catch:{ all -> 0x007d }
            if (r10 <= 0) goto L_0x0077
            int r10 = (r7 > r8 ? 1 : (r7 == r8 ? 0 : -1))
            if (r10 <= 0) goto L_0x0035
            int r10 = r1.outWidth     // Catch:{ all -> 0x007d }
            int r4 = r1.outHeight     // Catch:{ all -> 0x007d }
            if (r10 >= r4) goto L_0x0035
            r6 = r8
            r8 = r7
            r7 = r6
        L_0x0035:
            int r10 = r1.outWidth     // Catch:{ all -> 0x007d }
            float r10 = (float) r10     // Catch:{ all -> 0x007d }
            float r10 = r10 / r7
            int r7 = r1.outHeight     // Catch:{ all -> 0x007d }
            float r7 = (float) r7     // Catch:{ all -> 0x007d }
            float r7 = r7 / r8
            float r7 = java.lang.Math.min(r10, r7)     // Catch:{ all -> 0x007d }
            r1.inSampleSize = r2     // Catch:{ all -> 0x007d }
            r8 = 1065353216(0x3var_, float:1.0)
            int r8 = (r7 > r8 ? 1 : (r7 == r8 ? 0 : -1))
            if (r8 <= 0) goto L_0x0056
        L_0x0049:
            int r8 = r1.inSampleSize     // Catch:{ all -> 0x007d }
            int r8 = r8 * 2
            r1.inSampleSize = r8     // Catch:{ all -> 0x007d }
            int r8 = r1.inSampleSize     // Catch:{ all -> 0x007d }
            float r8 = (float) r8     // Catch:{ all -> 0x007d }
            int r8 = (r8 > r7 ? 1 : (r8 == r7 ? 0 : -1))
            if (r8 < 0) goto L_0x0049
        L_0x0056:
            r7 = 0
            r1.inJustDecodeBounds = r7     // Catch:{ all -> 0x007d }
            if (r9 == 0) goto L_0x0060
            android.graphics.Bitmap r7 = android.graphics.BitmapFactory.decodeFile(r9, r1)     // Catch:{ all -> 0x007d }
            goto L_0x006c
        L_0x0060:
            java.nio.channels.FileChannel r7 = r3.getChannel()     // Catch:{ all -> 0x007d }
            long r8 = (long) r11     // Catch:{ all -> 0x007d }
            r7.position(r8)     // Catch:{ all -> 0x007d }
            android.graphics.Bitmap r7 = android.graphics.BitmapFactory.decodeStream(r3, r0, r1)     // Catch:{ all -> 0x007d }
        L_0x006c:
            if (r3 == 0) goto L_0x0076
            r3.close()     // Catch:{ Exception -> 0x0072 }
            goto L_0x0076
        L_0x0072:
            r8 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r8)
        L_0x0076:
            return r7
        L_0x0077:
            if (r3 == 0) goto L_0x008e
            r3.close()     // Catch:{ Exception -> 0x008a }
            goto L_0x008e
        L_0x007d:
            r7 = move-exception
            goto L_0x0081
        L_0x007f:
            r7 = move-exception
            r3 = r0
        L_0x0081:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r7)     // Catch:{ all -> 0x008f }
            if (r3 == 0) goto L_0x008e
            r3.close()     // Catch:{ Exception -> 0x008a }
            goto L_0x008e
        L_0x008a:
            r7 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r7)
        L_0x008e:
            return r0
        L_0x008f:
            r7 = move-exception
            if (r3 == 0) goto L_0x009a
            r3.close()     // Catch:{ Exception -> 0x0096 }
            goto L_0x009a
        L_0x0096:
            r8 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r8)
        L_0x009a:
            goto L_0x009c
        L_0x009b:
            throw r7
        L_0x009c:
            goto L_0x009b
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
                        private final /* synthetic */ Theme.ThemeInfo f$1;
                        private final /* synthetic */ File f$2;

                        {
                            this.f$1 = r2;
                            this.f$2 = r3;
                        }

                        public final void run() {
                            ThemesHorizontalListCell.this.lambda$didReceivedNotification$3$ThemesHorizontalListCell(this.f$1, this.f$2);
                        }
                    });
                } else {
                    lambda$null$2$ThemesHorizontalListCell(themeInfo);
                }
            }
        } else if (i == NotificationCenter.fileDidFailToLoad) {
            this.loadingThemes.remove(objArr[0]);
        }
    }

    public /* synthetic */ void lambda$didReceivedNotification$3$ThemesHorizontalListCell(Theme.ThemeInfo themeInfo, File file) {
        themeInfo.badWallpaper = !themeInfo.createBackground(file, themeInfo.pathToWallpaper);
        AndroidUtilities.runOnUIThread(new Runnable(themeInfo) {
            private final /* synthetic */ Theme.ThemeInfo f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                ThemesHorizontalListCell.this.lambda$null$2$ThemesHorizontalListCell(this.f$1);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: checkVisibleTheme */
    public void lambda$null$2$ThemesHorizontalListCell(Theme.ThemeInfo themeInfo) {
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
            this.prevThemeInfo = this.currentType == 1 ? Theme.getCurrentNightTheme() : Theme.getCurrentTheme();
            int indexOf = this.defaultThemes.indexOf(this.prevThemeInfo);
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
