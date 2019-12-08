package org.telegram.ui.Cells;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.os.SystemClock;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import androidx.annotation.Keep;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_theme;
import org.telegram.tgnet.TLRPC.TL_wallPaper;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.Theme.ThemeInfo;
import org.telegram.ui.Components.BackgroundGradientDrawable;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RadioButton;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;
import org.telegram.ui.ThemeSetUrlActivity;

public class ThemesHorizontalListCell extends RecyclerListView implements NotificationCenterDelegate {
    private static byte[] bytes = new byte[1024];
    private ThemesListAdapter adapter;
    private int currentType;
    private ArrayList<ThemeInfo> darkThemes;
    private ArrayList<ThemeInfo> defaultThemes;
    private boolean drawDivider;
    private LinearLayoutManager horizontalLayoutManager;
    private HashMap<String, ThemeInfo> loadingThemes = new HashMap();
    private HashMap<ThemeInfo, String> loadingWallpapers = new HashMap();
    private ThemeInfo prevThemeInfo;

    private class InnerThemeView extends FrameLayout {
        private ObjectAnimator accentAnimator;
        private int accentColor;
        private boolean accentColorChanged;
        private float accentState;
        private Drawable backgroundDrawable;
        private Paint bitmapPaint = new Paint(3);
        private BitmapShader bitmapShader;
        private RadioButton button;
        private final ArgbEvaluator evaluator = new ArgbEvaluator();
        private Drawable inDrawable;
        private boolean isFirst;
        private boolean isLast;
        private long lastDrawTime;
        private int loadingColor;
        private Drawable loadingDrawable;
        private int oldAccentColor;
        private Drawable optionsDrawable;
        private Drawable outDrawable;
        private Paint paint = new Paint(1);
        private float placeholderAlpha;
        private boolean pressed;
        private RectF rect = new RectF();
        private Matrix shaderMatrix = new Matrix();
        private TextPaint textPaint = new TextPaint(1);
        private ThemeInfo themeInfo;

        public InnerThemeView(Context context) {
            super(context);
            setWillNotDraw(false);
            this.inDrawable = context.getResources().getDrawable(NUM).mutate();
            this.outDrawable = context.getResources().getDrawable(NUM).mutate();
            this.textPaint.setTextSize((float) AndroidUtilities.dp(13.0f));
            this.button = new RadioButton(context, ThemesHorizontalListCell.this) {
                public void invalidate() {
                    super.invalidate();
                }
            };
            this.button.setSize(AndroidUtilities.dp(20.0f));
            addView(this.button, LayoutHelper.createFrame(22, 22.0f, 51, 27.0f, 75.0f, 0.0f, 0.0f));
        }

        /* Access modifiers changed, original: protected */
        public void onMeasure(int i, int i2) {
            i2 = 22;
            i = (this.isLast ? 22 : 15) + 76;
            if (!this.isFirst) {
                i2 = 0;
            }
            super.onMeasure(MeasureSpec.makeMeasureSpec(AndroidUtilities.dp((float) (i + i2)), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(148.0f), NUM));
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            if (this.optionsDrawable != null) {
                ThemeInfo themeInfo = this.themeInfo;
                if (themeInfo != null && ((themeInfo.info == null || themeInfo.themeLoaded) && ThemesHorizontalListCell.this.currentType == 0)) {
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
            }
            return super.onTouchEvent(motionEvent);
        }

        /* JADX WARNING: Unknown top exception splitter block from list: {B:88:0x0181=Splitter:B:88:0x0181, B:97:0x018c=Splitter:B:97:0x018c} */
        /* JADX WARNING: Removed duplicated region for block: B:84:0x0172 A:{Catch:{ all -> 0x0187 }} */
        /* JADX WARNING: Missing exception handler attribute for start block: B:97:0x018c */
        /* JADX WARNING: Missing exception handler attribute for start block: B:48:0x0105 */
        /* JADX WARNING: Can't wrap try/catch for region: R(4:46|47|48|49) */
        /* JADX WARNING: Missing block: B:40:0x00e9, code skipped:
            if (r14.equals(r15) == false) goto L_0x0161;
     */
        /* JADX WARNING: Missing block: B:66:0x013f, code skipped:
            r4 = -1;
     */
        /* JADX WARNING: Missing block: B:67:0x0140, code skipped:
            if (r4 == 0) goto L_0x015a;
     */
        /* JADX WARNING: Missing block: B:69:0x0143, code skipped:
            if (r4 == 1) goto L_0x0155;
     */
        /* JADX WARNING: Missing block: B:70:0x0145, code skipped:
            if (r4 == 2) goto L_0x0150;
     */
        /* JADX WARNING: Missing block: B:72:0x0148, code skipped:
            if (r4 == 3) goto L_0x014b;
     */
        /* JADX WARNING: Missing block: B:74:0x014b, code skipped:
            r1.themeInfo.previewBackgroundGradientColor = r3;
     */
        /* JADX WARNING: Missing block: B:75:0x0150, code skipped:
            r1.themeInfo.previewBackgroundColor = r3;
     */
        /* JADX WARNING: Missing block: B:76:0x0155, code skipped:
            r1.themeInfo.previewOutColor = r3;
     */
        /* JADX WARNING: Missing block: B:77:0x015a, code skipped:
            r1.themeInfo.previewInColor = r3;
     */
        /* JADX WARNING: Missing block: B:93:0x0187, code skipped:
            r0 = move-exception;
     */
        /* JADX WARNING: Missing block: B:94:0x0188, code skipped:
            r2 = r0;
     */
        /* JADX WARNING: Missing block: B:96:?, code skipped:
            r5.close();
     */
        private boolean parseTheme() {
            /*
            r17 = this;
            r1 = r17;
            r0 = "chat_inBubble";
            r2 = new java.io.File;
            r3 = r1.themeInfo;
            r3 = r3.pathToFile;
            r2.<init>(r3);
            r4 = 1;
            r5 = new java.io.FileInputStream;	 Catch:{ all -> 0x018d }
            r5.<init>(r2);	 Catch:{ all -> 0x018d }
            r2 = 0;
            r6 = 0;
        L_0x0015:
            r7 = org.telegram.ui.Cells.ThemesHorizontalListCell.bytes;	 Catch:{ all -> 0x0185 }
            r7 = r5.read(r7);	 Catch:{ all -> 0x0185 }
            r8 = -1;
            if (r7 == r8) goto L_0x0181;
        L_0x0020:
            r11 = r2;
            r9 = 0;
            r10 = 0;
        L_0x0023:
            if (r9 >= r7) goto L_0x016e;
        L_0x0025:
            r12 = org.telegram.ui.Cells.ThemesHorizontalListCell.bytes;	 Catch:{ all -> 0x0185 }
            r12 = r12[r9];	 Catch:{ all -> 0x0185 }
            r13 = 10;
            if (r12 != r13) goto L_0x0164;
        L_0x002f:
            r12 = r9 - r10;
            r12 = r12 + r4;
            r13 = new java.lang.String;	 Catch:{ all -> 0x0185 }
            r14 = org.telegram.ui.Cells.ThemesHorizontalListCell.bytes;	 Catch:{ all -> 0x0185 }
            r15 = r12 + -1;
            r3 = "UTF-8";
            r13.<init>(r14, r10, r15, r3);	 Catch:{ all -> 0x0185 }
            r3 = "WLS=";
            r3 = r13.startsWith(r3);	 Catch:{ all -> 0x0185 }
            if (r3 == 0) goto L_0x00ad;
        L_0x0047:
            r3 = 4;
            r3 = r13.substring(r3);	 Catch:{ all -> 0x0185 }
            r13 = android.net.Uri.parse(r3);	 Catch:{ all -> 0x0185 }
            r14 = r1.themeInfo;	 Catch:{ all -> 0x0185 }
            r15 = "slug";
            r15 = r13.getQueryParameter(r15);	 Catch:{ all -> 0x0185 }
            r14.slug = r15;	 Catch:{ all -> 0x0185 }
            r14 = r1.themeInfo;	 Catch:{ all -> 0x0185 }
            r15 = new java.io.File;	 Catch:{ all -> 0x0185 }
            r8 = org.telegram.messenger.ApplicationLoader.getFilesDirFixed();	 Catch:{ all -> 0x0185 }
            r4 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0185 }
            r4.<init>();	 Catch:{ all -> 0x0185 }
            r3 = org.telegram.messenger.Utilities.MD5(r3);	 Catch:{ all -> 0x0185 }
            r4.append(r3);	 Catch:{ all -> 0x0185 }
            r3 = ".wp";
            r4.append(r3);	 Catch:{ all -> 0x0185 }
            r3 = r4.toString();	 Catch:{ all -> 0x0185 }
            r15.<init>(r8, r3);	 Catch:{ all -> 0x0185 }
            r3 = r15.getAbsolutePath();	 Catch:{ all -> 0x0185 }
            r14.pathToWallpaper = r3;	 Catch:{ all -> 0x0185 }
            r3 = "mode";
            r3 = r13.getQueryParameter(r3);	 Catch:{ all -> 0x0185 }
            if (r3 == 0) goto L_0x015f;
        L_0x0088:
            r3 = r3.toLowerCase();	 Catch:{ all -> 0x0185 }
            r4 = " ";
            r3 = r3.split(r4);	 Catch:{ all -> 0x0185 }
            if (r3 == 0) goto L_0x015f;
        L_0x0094:
            r4 = r3.length;	 Catch:{ all -> 0x0185 }
            if (r4 <= 0) goto L_0x015f;
        L_0x0097:
            r4 = 0;
        L_0x0098:
            r8 = r3.length;	 Catch:{ all -> 0x0185 }
            if (r4 >= r8) goto L_0x015f;
        L_0x009b:
            r8 = "blur";
            r13 = r3[r4];	 Catch:{ all -> 0x0185 }
            r8 = r8.equals(r13);	 Catch:{ all -> 0x0185 }
            if (r8 == 0) goto L_0x00aa;
        L_0x00a5:
            r8 = r1.themeInfo;	 Catch:{ all -> 0x0185 }
            r13 = 1;
            r8.isBlured = r13;	 Catch:{ all -> 0x0185 }
        L_0x00aa:
            r4 = r4 + 1;
            goto L_0x0098;
        L_0x00ad:
            r3 = "WPS";
            r3 = r13.startsWith(r3);	 Catch:{ all -> 0x0185 }
            if (r3 == 0) goto L_0x00bd;
        L_0x00b5:
            r3 = r1.themeInfo;	 Catch:{ all -> 0x0185 }
            r12 = r12 + r11;
            r3.previewWallpaperOffset = r12;	 Catch:{ all -> 0x0185 }
            r6 = 1;
            goto L_0x0170;
        L_0x00bd:
            r3 = 61;
            r3 = r13.indexOf(r3);	 Catch:{ all -> 0x0185 }
            r4 = -1;
            if (r3 == r4) goto L_0x015f;
        L_0x00c6:
            r8 = 0;
            r14 = r13.substring(r8, r3);	 Catch:{ all -> 0x0185 }
            r8 = r14.equals(r0);	 Catch:{ all -> 0x0185 }
            r15 = "chat_wallpaper_gradient_to";
            r4 = "chat_wallpaper";
            r16 = r6;
            r6 = "chat_outBubble";
            if (r8 != 0) goto L_0x00eb;
        L_0x00d9:
            r8 = r14.equals(r6);	 Catch:{ all -> 0x0185 }
            if (r8 != 0) goto L_0x00eb;
        L_0x00df:
            r8 = r14.equals(r4);	 Catch:{ all -> 0x0185 }
            if (r8 != 0) goto L_0x00eb;
        L_0x00e5:
            r8 = r14.equals(r15);	 Catch:{ all -> 0x0185 }
            if (r8 == 0) goto L_0x0161;
        L_0x00eb:
            r3 = r3 + 1;
            r3 = r13.substring(r3);	 Catch:{ all -> 0x0185 }
            r8 = r3.length();	 Catch:{ all -> 0x0185 }
            if (r8 <= 0) goto L_0x010e;
        L_0x00f7:
            r8 = 0;
            r13 = r3.charAt(r8);	 Catch:{ all -> 0x0185 }
            r8 = 35;
            if (r13 != r8) goto L_0x010e;
        L_0x0100:
            r3 = android.graphics.Color.parseColor(r3);	 Catch:{ Exception -> 0x0105 }
            goto L_0x0116;
        L_0x0105:
            r3 = org.telegram.messenger.Utilities.parseInt(r3);	 Catch:{ all -> 0x0185 }
            r3 = r3.intValue();	 Catch:{ all -> 0x0185 }
            goto L_0x0116;
        L_0x010e:
            r3 = org.telegram.messenger.Utilities.parseInt(r3);	 Catch:{ all -> 0x0185 }
            r3 = r3.intValue();	 Catch:{ all -> 0x0185 }
        L_0x0116:
            r8 = r14.hashCode();	 Catch:{ all -> 0x0185 }
            r13 = 2;
            switch(r8) {
                case -1625862693: goto L_0x0137;
                case -633951866: goto L_0x012f;
                case 1269980952: goto L_0x0127;
                case 2052611411: goto L_0x011f;
                default: goto L_0x011e;
            };	 Catch:{ all -> 0x0185 }
        L_0x011e:
            goto L_0x013f;
        L_0x011f:
            r4 = r14.equals(r6);	 Catch:{ all -> 0x0185 }
            if (r4 == 0) goto L_0x013f;
        L_0x0125:
            r4 = 1;
            goto L_0x0140;
        L_0x0127:
            r4 = r14.equals(r0);	 Catch:{ all -> 0x0185 }
            if (r4 == 0) goto L_0x013f;
        L_0x012d:
            r4 = 0;
            goto L_0x0140;
        L_0x012f:
            r4 = r14.equals(r15);	 Catch:{ all -> 0x0185 }
            if (r4 == 0) goto L_0x013f;
        L_0x0135:
            r4 = 3;
            goto L_0x0140;
        L_0x0137:
            r4 = r14.equals(r4);	 Catch:{ all -> 0x0185 }
            if (r4 == 0) goto L_0x013f;
        L_0x013d:
            r4 = 2;
            goto L_0x0140;
        L_0x013f:
            r4 = -1;
        L_0x0140:
            if (r4 == 0) goto L_0x015a;
        L_0x0142:
            r6 = 1;
            if (r4 == r6) goto L_0x0155;
        L_0x0145:
            if (r4 == r13) goto L_0x0150;
        L_0x0147:
            r6 = 3;
            if (r4 == r6) goto L_0x014b;
        L_0x014a:
            goto L_0x0161;
        L_0x014b:
            r4 = r1.themeInfo;	 Catch:{ all -> 0x0185 }
            r4.previewBackgroundGradientColor = r3;	 Catch:{ all -> 0x0185 }
            goto L_0x0161;
        L_0x0150:
            r4 = r1.themeInfo;	 Catch:{ all -> 0x0185 }
            r4.previewBackgroundColor = r3;	 Catch:{ all -> 0x0185 }
            goto L_0x0161;
        L_0x0155:
            r4 = r1.themeInfo;	 Catch:{ all -> 0x0185 }
            r4.previewOutColor = r3;	 Catch:{ all -> 0x0185 }
            goto L_0x0161;
        L_0x015a:
            r4 = r1.themeInfo;	 Catch:{ all -> 0x0185 }
            r4.previewInColor = r3;	 Catch:{ all -> 0x0185 }
            goto L_0x0161;
        L_0x015f:
            r16 = r6;
        L_0x0161:
            r10 = r10 + r12;
            r11 = r11 + r12;
            goto L_0x0166;
        L_0x0164:
            r16 = r6;
        L_0x0166:
            r9 = r9 + 1;
            r6 = r16;
            r4 = 1;
            r8 = -1;
            goto L_0x0023;
        L_0x016e:
            r16 = r6;
        L_0x0170:
            if (r6 != 0) goto L_0x0181;
        L_0x0172:
            if (r2 != r11) goto L_0x0175;
        L_0x0174:
            goto L_0x0181;
        L_0x0175:
            r2 = r5.getChannel();	 Catch:{ all -> 0x0185 }
            r3 = (long) r11;	 Catch:{ all -> 0x0185 }
            r2.position(r3);	 Catch:{ all -> 0x0185 }
            r2 = r11;
            r4 = 1;
            goto L_0x0015;
        L_0x0181:
            r5.close();	 Catch:{ all -> 0x018d }
            goto L_0x0191;
        L_0x0185:
            r0 = move-exception;
            throw r0;	 Catch:{ all -> 0x0187 }
        L_0x0187:
            r0 = move-exception;
            r2 = r0;
            r5.close();	 Catch:{ all -> 0x018c }
        L_0x018c:
            throw r2;	 Catch:{ all -> 0x018d }
        L_0x018d:
            r0 = move-exception;
            org.telegram.messenger.FileLog.e(r0);
        L_0x0191:
            r0 = r1.themeInfo;
            r2 = r0.pathToWallpaper;
            if (r2 == 0) goto L_0x01e3;
        L_0x0197:
            r0 = r0.badWallpaper;
            if (r0 != 0) goto L_0x01e3;
        L_0x019b:
            r0 = new java.io.File;
            r0.<init>(r2);
            r0 = r0.exists();
            if (r0 != 0) goto L_0x01e3;
        L_0x01a6:
            r0 = org.telegram.ui.Cells.ThemesHorizontalListCell.this;
            r0 = r0.loadingWallpapers;
            r2 = r1.themeInfo;
            r0 = r0.containsKey(r2);
            if (r0 != 0) goto L_0x01e1;
        L_0x01b4:
            r0 = org.telegram.ui.Cells.ThemesHorizontalListCell.this;
            r0 = r0.loadingWallpapers;
            r2 = r1.themeInfo;
            r3 = r2.slug;
            r0.put(r2, r3);
            r0 = new org.telegram.tgnet.TLRPC$TL_account_getWallPaper;
            r0.<init>();
            r2 = new org.telegram.tgnet.TLRPC$TL_inputWallPaperSlug;
            r2.<init>();
            r3 = r1.themeInfo;
            r4 = r3.slug;
            r2.slug = r4;
            r0.wallpaper = r2;
            r2 = r3.account;
            r2 = org.telegram.tgnet.ConnectionsManager.getInstance(r2);
            r3 = new org.telegram.ui.Cells.-$$Lambda$ThemesHorizontalListCell$InnerThemeView$aojGvqqAAssFRVGdS8VxlzJW2g8;
            r3.<init>(r1);
            r2.sendRequest(r0, r3);
        L_0x01e1:
            r2 = 0;
            return r2;
        L_0x01e3:
            r0 = r1.themeInfo;
            r2 = 1;
            r0.previewParsed = r2;
            return r2;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ThemesHorizontalListCell$InnerThemeView.parseTheme():boolean");
        }

        public /* synthetic */ void lambda$parseTheme$1$ThemesHorizontalListCell$InnerThemeView(TLObject tLObject, TL_error tL_error) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$ThemesHorizontalListCell$InnerThemeView$SGGiBLgrvilcsL-R2fYeELV9PyM(this, tLObject));
        }

        public /* synthetic */ void lambda$null$0$ThemesHorizontalListCell$InnerThemeView(TLObject tLObject) {
            if (tLObject instanceof TL_wallPaper) {
                TL_wallPaper tL_wallPaper = (TL_wallPaper) tLObject;
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

        private void applyTheme() {
            this.inDrawable.setColorFilter(new PorterDuffColorFilter(this.themeInfo.previewInColor, Mode.MULTIPLY));
            this.outDrawable.setColorFilter(new PorterDuffColorFilter(this.themeInfo.previewOutColor, Mode.MULTIPLY));
            ThemeInfo themeInfo = this.themeInfo;
            if (themeInfo.pathToFile == null) {
                updateAccentColor(themeInfo.accentColor, false);
                this.optionsDrawable = null;
            } else {
                this.optionsDrawable = getResources().getDrawable(NUM).mutate();
            }
            this.bitmapShader = null;
            this.backgroundDrawable = null;
            themeInfo = this.themeInfo;
            if (themeInfo.previewBackgroundGradientColor != 0) {
                BackgroundGradientDrawable backgroundGradientDrawable = new BackgroundGradientDrawable(Orientation.BL_TR, new int[]{themeInfo.previewBackgroundColor, r1});
                backgroundGradientDrawable.setCornerRadius((float) AndroidUtilities.dp(6.0f));
                this.backgroundDrawable = backgroundGradientDrawable;
            } else if (themeInfo.previewWallpaperOffset > 0 || themeInfo.pathToWallpaper != null) {
                float dp = (float) AndroidUtilities.dp(76.0f);
                float dp2 = (float) AndroidUtilities.dp(97.0f);
                ThemeInfo themeInfo2 = this.themeInfo;
                Bitmap scaledBitmap = ThemesHorizontalListCell.getScaledBitmap(dp, dp2, themeInfo2.pathToWallpaper, themeInfo2.pathToFile, themeInfo2.previewWallpaperOffset);
                if (scaledBitmap != null) {
                    this.backgroundDrawable = new BitmapDrawable(scaledBitmap);
                    TileMode tileMode = TileMode.CLAMP;
                    this.bitmapShader = new BitmapShader(scaledBitmap, tileMode, tileMode);
                    this.bitmapPaint.setShader(this.bitmapShader);
                }
            }
            themeInfo = this.themeInfo;
            if (themeInfo.previewBackgroundColor == 0 && themeInfo.previewParsed && this.backgroundDrawable == null) {
                BitmapDrawable bitmapDrawable = (BitmapDrawable) getResources().getDrawable(NUM).mutate();
                Bitmap bitmap = bitmapDrawable.getBitmap();
                TileMode tileMode2 = TileMode.REPEAT;
                this.bitmapShader = new BitmapShader(bitmap, tileMode2, tileMode2);
                this.bitmapPaint.setShader(this.bitmapShader);
                this.backgroundDrawable = bitmapDrawable;
            }
            invalidate();
        }

        public void setTheme(ThemeInfo themeInfo, boolean z, boolean z2) {
            this.themeInfo = themeInfo;
            this.isFirst = z2;
            this.isLast = z;
            LayoutParams layoutParams = (LayoutParams) this.button.getLayoutParams();
            layoutParams.leftMargin = AndroidUtilities.dp(this.isFirst ? 49.0f : 27.0f);
            this.button.setLayoutParams(layoutParams);
            this.placeholderAlpha = 0.0f;
            themeInfo = this.themeInfo;
            if (!(themeInfo.pathToFile == null || themeInfo.previewParsed)) {
                themeInfo.previewInColor = Theme.getDefaultColor("chat_inBubble");
                this.themeInfo.previewOutColor = Theme.getDefaultColor("chat_outBubble");
                boolean exists = new File(this.themeInfo.pathToFile).exists();
                Object obj = (exists && parseTheme()) ? 1 : null;
                if (obj == null || !exists) {
                    ThemeInfo themeInfo2 = this.themeInfo;
                    TL_theme tL_theme = themeInfo2.info;
                    if (tL_theme != null) {
                        String str = "windowBackgroundWhiteGrayText7";
                        if (tL_theme.document != null) {
                            themeInfo2.themeLoaded = false;
                            this.placeholderAlpha = 1.0f;
                            this.loadingDrawable = getResources().getDrawable(NUM).mutate();
                            Drawable drawable = this.loadingDrawable;
                            int color = Theme.getColor(str);
                            this.loadingColor = color;
                            Theme.setDrawableColor(drawable, color);
                            if (!exists) {
                                String attachFileName = FileLoader.getAttachFileName(this.themeInfo.info.document);
                                if (!ThemesHorizontalListCell.this.loadingThemes.containsKey(attachFileName)) {
                                    ThemesHorizontalListCell.this.loadingThemes.put(attachFileName, this.themeInfo);
                                    FileLoader instance = FileLoader.getInstance(this.themeInfo.account);
                                    TL_theme tL_theme2 = this.themeInfo.info;
                                    instance.loadFile(tL_theme2.document, tL_theme2, 1, 1);
                                }
                            }
                        } else {
                            this.loadingDrawable = getResources().getDrawable(NUM).mutate();
                            Drawable drawable2 = this.loadingDrawable;
                            int color2 = Theme.getColor(str);
                            this.loadingColor = color2;
                            Theme.setDrawableColor(drawable2, color2);
                        }
                    }
                }
            }
            applyTheme();
        }

        /* Access modifiers changed, original: protected */
        public void onAttachedToWindow() {
            super.onAttachedToWindow();
            this.button.setChecked(this.themeInfo == (ThemesHorizontalListCell.this.currentType == 1 ? Theme.getCurrentNightTheme() : Theme.getCurrentTheme()), false);
            ThemeInfo themeInfo = this.themeInfo;
            if (themeInfo != null) {
                TL_theme tL_theme = themeInfo.info;
                if (tL_theme != null && !themeInfo.themeLoaded) {
                    if (!ThemesHorizontalListCell.this.loadingThemes.containsKey(FileLoader.getAttachFileName(tL_theme.document)) && !ThemesHorizontalListCell.this.loadingWallpapers.containsKey(this.themeInfo)) {
                        this.themeInfo.themeLoaded = true;
                        this.placeholderAlpha = 0.0f;
                        parseTheme();
                        applyTheme();
                    }
                }
            }
        }

        public void updateCurrentThemeCheck() {
            this.button.setChecked(this.themeInfo == (ThemesHorizontalListCell.this.currentType == 1 ? Theme.getCurrentNightTheme() : Theme.getCurrentTheme()), true);
        }

        /* Access modifiers changed, original: 0000 */
        public void updateAccentColor(int i, boolean z) {
            this.oldAccentColor = this.accentColor;
            this.accentColor = i;
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
            return (float) this.accentColor;
        }

        @Keep
        public void setAccentState(float f) {
            this.accentState = f;
            this.accentColorChanged = true;
            invalidate();
        }

        /* Access modifiers changed, original: protected */
        public void onDraw(Canvas canvas) {
            Drawable drawable;
            int i = this.accentColor;
            int i2 = this.themeInfo.accentColor;
            boolean z = true;
            if (i != i2) {
                updateAccentColor(i2, true);
            }
            i = this.isFirst ? AndroidUtilities.dp(22.0f) : 0;
            int dp = AndroidUtilities.dp(11.0f);
            float f = (float) i;
            float f2 = (float) dp;
            this.rect.set(f, f2, (float) (AndroidUtilities.dp(76.0f) + i), (float) (dp + AndroidUtilities.dp(97.0f)));
            CharSequence name = this.themeInfo.getName();
            if (name.toLowerCase().endsWith(".attheme")) {
                name = name.substring(0, name.lastIndexOf(46));
            }
            String charSequence = TextUtils.ellipsize(name, this.textPaint, (float) (getMeasuredWidth() - AndroidUtilities.dp(this.isFirst ? 10.0f : 15.0f)), TruncateAt.END).toString();
            int ceil = (int) Math.ceil((double) this.textPaint.measureText(charSequence));
            this.textPaint.setColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            canvas.drawText(charSequence, (float) (((AndroidUtilities.dp(76.0f) - ceil) / 2) + i), (float) AndroidUtilities.dp(131.0f), this.textPaint);
            ThemeInfo themeInfo = this.themeInfo;
            TL_theme tL_theme = themeInfo.info;
            if (tL_theme != null && (tL_theme.document == null || !themeInfo.themeLoaded)) {
                z = false;
            }
            if (z) {
                this.paint.setColor(tint(this.themeInfo.previewBackgroundColor));
                if (this.accentColorChanged) {
                    this.inDrawable.setColorFilter(new PorterDuffColorFilter(tint(this.themeInfo.previewInColor), Mode.MULTIPLY));
                    this.outDrawable.setColorFilter(new PorterDuffColorFilter(tint(this.themeInfo.previewOutColor), Mode.MULTIPLY));
                    this.accentColorChanged = false;
                }
                drawable = this.backgroundDrawable;
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
                    width /= height2;
                    if (width > this.rect.width()) {
                        this.shaderMatrix.setTranslate(f - ((width - this.rect.width()) / 2.0f), f2);
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
                ThemeInfo themeInfo2 = this.themeInfo;
                if (themeInfo2.accentBaseColor != 0) {
                    if ("Arctic Blue".equals(themeInfo2.name)) {
                        this.button.setColor(-5000269, tint(this.themeInfo.accentBaseColor));
                        Theme.chat_instantViewRectPaint.setColor(NUM);
                        canvas.drawRoundRect(this.rect, (float) AndroidUtilities.dp(6.0f), (float) AndroidUtilities.dp(6.0f), Theme.chat_instantViewRectPaint);
                    }
                }
                this.inDrawable.setBounds(AndroidUtilities.dp(6.0f) + i, AndroidUtilities.dp(22.0f), AndroidUtilities.dp(49.0f) + i, AndroidUtilities.dp(36.0f));
                this.inDrawable.draw(canvas);
                this.outDrawable.setBounds(AndroidUtilities.dp(27.0f) + i, AndroidUtilities.dp(41.0f), i + AndroidUtilities.dp(70.0f), AndroidUtilities.dp(55.0f));
                this.outDrawable.draw(canvas);
                if (this.optionsDrawable != null && ThemesHorizontalListCell.this.currentType == 0) {
                    i = ((int) this.rect.right) - AndroidUtilities.dp(16.0f);
                    i2 = ((int) this.rect.top) + AndroidUtilities.dp(6.0f);
                    drawable = this.optionsDrawable;
                    drawable.setBounds(i, i2, drawable.getIntrinsicWidth() + i, this.optionsDrawable.getIntrinsicHeight() + i2);
                    this.optionsDrawable.draw(canvas);
                }
            }
            TL_theme tL_theme2 = this.themeInfo.info;
            String str = "windowBackgroundWhiteGrayText7";
            Drawable drawable2;
            if (tL_theme2 == null || tL_theme2.document != null) {
                ThemeInfo themeInfo3 = this.themeInfo;
                if ((themeInfo3.info != null && !themeInfo3.themeLoaded) || this.placeholderAlpha > 0.0f) {
                    this.button.setAlpha(1.0f - this.placeholderAlpha);
                    this.paint.setColor(Theme.getColor("windowBackgroundGray"));
                    this.paint.setAlpha((int) (this.placeholderAlpha * 255.0f));
                    canvas.drawRoundRect(this.rect, (float) AndroidUtilities.dp(6.0f), (float) AndroidUtilities.dp(6.0f), this.paint);
                    if (this.loadingDrawable != null) {
                        i = Theme.getColor(str);
                        if (this.loadingColor != i) {
                            drawable2 = this.loadingDrawable;
                            this.loadingColor = i;
                            Theme.setDrawableColor(drawable2, i);
                        }
                        i = (int) (this.rect.centerX() - ((float) (this.loadingDrawable.getIntrinsicWidth() / 2)));
                        i2 = (int) (this.rect.centerY() - ((float) (this.loadingDrawable.getIntrinsicHeight() / 2)));
                        this.loadingDrawable.setAlpha((int) (this.placeholderAlpha * 255.0f));
                        Drawable drawable3 = this.loadingDrawable;
                        drawable3.setBounds(i, i2, drawable3.getIntrinsicWidth() + i, this.loadingDrawable.getIntrinsicHeight() + i2);
                        this.loadingDrawable.draw(canvas);
                    }
                    if (this.themeInfo.themeLoaded) {
                        long uptimeMillis = SystemClock.uptimeMillis();
                        long min2 = Math.min(17, uptimeMillis - this.lastDrawTime);
                        this.lastDrawTime = uptimeMillis;
                        this.placeholderAlpha -= ((float) min2) / 180.0f;
                        if (this.placeholderAlpha < 0.0f) {
                            this.placeholderAlpha = 0.0f;
                        }
                        invalidate();
                        return;
                    }
                    return;
                } else if (this.button.getAlpha() != 1.0f) {
                    this.button.setAlpha(1.0f);
                    return;
                } else {
                    return;
                }
            }
            this.button.setAlpha(0.0f);
            Theme.chat_instantViewRectPaint.setColor(NUM);
            canvas.drawRoundRect(this.rect, (float) AndroidUtilities.dp(6.0f), (float) AndroidUtilities.dp(6.0f), Theme.chat_instantViewRectPaint);
            if (this.loadingDrawable != null) {
                i = Theme.getColor(str);
                if (this.loadingColor != i) {
                    drawable2 = this.loadingDrawable;
                    this.loadingColor = i;
                    Theme.setDrawableColor(drawable2, i);
                }
                i = (int) (this.rect.centerX() - ((float) (this.loadingDrawable.getIntrinsicWidth() / 2)));
                i2 = (int) (this.rect.centerY() - ((float) (this.loadingDrawable.getIntrinsicHeight() / 2)));
                drawable = this.loadingDrawable;
                drawable.setBounds(i, i2, drawable.getIntrinsicWidth() + i, this.loadingDrawable.getIntrinsicHeight() + i2);
                this.loadingDrawable.draw(canvas);
            }
        }

        private int tint(int i) {
            if (this.accentState == 1.0f) {
                return Theme.changeColorAccent(this.themeInfo, this.accentColor, i);
            }
            return ((Integer) this.evaluator.evaluate(this.accentState, Integer.valueOf(Theme.changeColorAccent(this.themeInfo, this.oldAccentColor, i)), Integer.valueOf(Theme.changeColorAccent(this.themeInfo, this.accentColor, i)))).intValue();
        }
    }

    private class ThemesListAdapter extends SelectionAdapter {
        private Context mContext;

        public boolean isEnabled(ViewHolder viewHolder) {
            return false;
        }

        ThemesListAdapter(Context context) {
            this.mContext = context;
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            return new Holder(new InnerThemeView(this.mContext));
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            ArrayList access$000;
            int i2;
            InnerThemeView innerThemeView = (InnerThemeView) viewHolder.itemView;
            if (i < ThemesHorizontalListCell.this.defaultThemes.size()) {
                access$000 = ThemesHorizontalListCell.this.defaultThemes;
                i2 = i;
            } else {
                access$000 = ThemesHorizontalListCell.this.darkThemes;
                i2 = i - ThemesHorizontalListCell.this.defaultThemes.size();
            }
            ThemeInfo themeInfo = (ThemeInfo) access$000.get(i2);
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

    /* Access modifiers changed, original: protected */
    public void presentFragment(BaseFragment baseFragment) {
    }

    /* Access modifiers changed, original: protected */
    public void showOptionsForTheme(ThemeInfo themeInfo) {
    }

    /* Access modifiers changed, original: protected */
    public void updateRows() {
    }

    public ThemesHorizontalListCell(Context context, int i, ArrayList<ThemeInfo> arrayList, ArrayList<ThemeInfo> arrayList2) {
        super(context);
        this.darkThemes = arrayList2;
        this.defaultThemes = arrayList;
        this.currentType = i;
        if (i == 2) {
            setBackgroundColor(Theme.getColor("dialogBackground"));
        } else {
            setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        }
        setItemAnimator(null);
        setLayoutAnimation(null);
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
        setOnItemClickListener(new -$$Lambda$ThemesHorizontalListCell$zF3xkvq6GaLbq4e3vubmnfe_soM(this));
        setOnItemLongClickListener(new -$$Lambda$ThemesHorizontalListCell$h-TEPC1cxxtVxI1TYZVRcA8z36g(this));
    }

    public /* synthetic */ void lambda$new$0$ThemesHorizontalListCell(View view, int i) {
        ThemeInfo access$600 = ((InnerThemeView) view).themeInfo;
        TL_theme tL_theme = access$600.info;
        if (tL_theme != null) {
            if (!access$600.themeLoaded) {
                return;
            }
            if (tL_theme.document == null) {
                presentFragment(new ThemeSetUrlActivity(access$600, true));
                return;
            }
        }
        int i2 = 0;
        if (this.currentType == 1) {
            if (access$600 != Theme.getCurrentNightTheme()) {
                Theme.setCurrentNightTheme(access$600);
            } else {
                return;
            }
        } else if (access$600 != Theme.getCurrentTheme()) {
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.needSetDayNightTheme, access$600, Boolean.valueOf(false));
        } else {
            return;
        }
        updateRows();
        i = view.getLeft();
        int right = view.getRight();
        if (i < 0) {
            smoothScrollBy(i - AndroidUtilities.dp(8.0f), 0);
        } else if (right > getMeasuredWidth()) {
            smoothScrollBy(right - getMeasuredWidth(), 0);
        }
        right = getChildCount();
        while (i2 < right) {
            View childAt = getChildAt(i2);
            if (childAt instanceof InnerThemeView) {
                ((InnerThemeView) childAt).updateCurrentThemeCheck();
            }
            i2++;
        }
    }

    public /* synthetic */ boolean lambda$new$1$ThemesHorizontalListCell(View view, int i) {
        showOptionsForTheme(((InnerThemeView) view).themeInfo);
        return true;
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
            getParent().getParent().requestDisallowInterceptTouchEvent(true);
        }
        return super.onInterceptTouchEvent(motionEvent);
    }

    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.drawDivider) {
            canvas.drawLine(LocaleController.isRTL ? 0.0f : (float) AndroidUtilities.dp(20.0f), (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(20.0f) : 0)), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:40:0x0086 A:{SYNTHETIC, Splitter:B:40:0x0086} */
    public static android.graphics.Bitmap getScaledBitmap(float r7, float r8, java.lang.String r9, java.lang.String r10, int r11) {
        /*
        r0 = 0;
        r1 = new android.graphics.BitmapFactory$Options;	 Catch:{ all -> 0x007f }
        r1.<init>();	 Catch:{ all -> 0x007f }
        r2 = 1;
        r1.inJustDecodeBounds = r2;	 Catch:{ all -> 0x007f }
        if (r9 == 0) goto L_0x0010;
    L_0x000b:
        android.graphics.BitmapFactory.decodeFile(r9, r1);	 Catch:{ all -> 0x007f }
        r3 = r0;
        goto L_0x0020;
    L_0x0010:
        r3 = new java.io.FileInputStream;	 Catch:{ all -> 0x007f }
        r3.<init>(r10);	 Catch:{ all -> 0x007f }
        r10 = r3.getChannel();	 Catch:{ all -> 0x007d }
        r4 = (long) r11;	 Catch:{ all -> 0x007d }
        r10.position(r4);	 Catch:{ all -> 0x007d }
        android.graphics.BitmapFactory.decodeStream(r3, r0, r1);	 Catch:{ all -> 0x007d }
    L_0x0020:
        r10 = r1.outWidth;	 Catch:{ all -> 0x007d }
        if (r10 <= 0) goto L_0x0077;
    L_0x0024:
        r10 = r1.outHeight;	 Catch:{ all -> 0x007d }
        if (r10 <= 0) goto L_0x0077;
    L_0x0028:
        r10 = (r7 > r8 ? 1 : (r7 == r8 ? 0 : -1));
        if (r10 <= 0) goto L_0x0035;
    L_0x002c:
        r10 = r1.outWidth;	 Catch:{ all -> 0x007d }
        r4 = r1.outHeight;	 Catch:{ all -> 0x007d }
        if (r10 >= r4) goto L_0x0035;
    L_0x0032:
        r6 = r8;
        r8 = r7;
        r7 = r6;
    L_0x0035:
        r10 = r1.outWidth;	 Catch:{ all -> 0x007d }
        r10 = (float) r10;	 Catch:{ all -> 0x007d }
        r10 = r10 / r7;
        r7 = r1.outHeight;	 Catch:{ all -> 0x007d }
        r7 = (float) r7;	 Catch:{ all -> 0x007d }
        r7 = r7 / r8;
        r7 = java.lang.Math.min(r10, r7);	 Catch:{ all -> 0x007d }
        r1.inSampleSize = r2;	 Catch:{ all -> 0x007d }
        r8 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r8 = (r7 > r8 ? 1 : (r7 == r8 ? 0 : -1));
        if (r8 <= 0) goto L_0x0056;
    L_0x0049:
        r8 = r1.inSampleSize;	 Catch:{ all -> 0x007d }
        r8 = r8 * 2;
        r1.inSampleSize = r8;	 Catch:{ all -> 0x007d }
        r8 = r1.inSampleSize;	 Catch:{ all -> 0x007d }
        r8 = (float) r8;	 Catch:{ all -> 0x007d }
        r8 = (r8 > r7 ? 1 : (r8 == r7 ? 0 : -1));
        if (r8 < 0) goto L_0x0049;
    L_0x0056:
        r7 = 0;
        r1.inJustDecodeBounds = r7;	 Catch:{ all -> 0x007d }
        if (r9 == 0) goto L_0x0060;
    L_0x005b:
        r7 = android.graphics.BitmapFactory.decodeFile(r9, r1);	 Catch:{ all -> 0x007d }
        goto L_0x006c;
    L_0x0060:
        r7 = r3.getChannel();	 Catch:{ all -> 0x007d }
        r8 = (long) r11;	 Catch:{ all -> 0x007d }
        r7.position(r8);	 Catch:{ all -> 0x007d }
        r7 = android.graphics.BitmapFactory.decodeStream(r3, r0, r1);	 Catch:{ all -> 0x007d }
    L_0x006c:
        if (r3 == 0) goto L_0x0076;
    L_0x006e:
        r3.close();	 Catch:{ Exception -> 0x0072 }
        goto L_0x0076;
    L_0x0072:
        r8 = move-exception;
        org.telegram.messenger.FileLog.e(r8);
    L_0x0076:
        return r7;
    L_0x0077:
        if (r3 == 0) goto L_0x008e;
    L_0x0079:
        r3.close();	 Catch:{ Exception -> 0x008a }
        goto L_0x008e;
    L_0x007d:
        r7 = move-exception;
        goto L_0x0081;
    L_0x007f:
        r7 = move-exception;
        r3 = r0;
    L_0x0081:
        org.telegram.messenger.FileLog.e(r7);	 Catch:{ all -> 0x008f }
        if (r3 == 0) goto L_0x008e;
    L_0x0086:
        r3.close();	 Catch:{ Exception -> 0x008a }
        goto L_0x008e;
    L_0x008a:
        r7 = move-exception;
        org.telegram.messenger.FileLog.e(r7);
    L_0x008e:
        return r0;
    L_0x008f:
        r7 = move-exception;
        if (r3 == 0) goto L_0x009a;
    L_0x0092:
        r3.close();	 Catch:{ Exception -> 0x0096 }
        goto L_0x009a;
    L_0x0096:
        r8 = move-exception;
        org.telegram.messenger.FileLog.e(r8);
    L_0x009a:
        goto L_0x009c;
    L_0x009b:
        throw r7;
    L_0x009c:
        goto L_0x009b;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ThemesHorizontalListCell.getScaledBitmap(float, float, java.lang.String, java.lang.String, int):android.graphics.Bitmap");
    }

    public void setBackgroundColor(int i) {
        super.setBackgroundColor(i);
        invalidateViews();
    }

    /* Access modifiers changed, original: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        NotificationCenter.getInstance(UserConfig.selectedAccount).addObserver(this, NotificationCenter.fileDidLoad);
        NotificationCenter.getInstance(UserConfig.selectedAccount).addObserver(this, NotificationCenter.fileDidFailToLoad);
    }

    /* Access modifiers changed, original: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        NotificationCenter.getInstance(UserConfig.selectedAccount).removeObserver(this, NotificationCenter.fileDidLoad);
        NotificationCenter.getInstance(UserConfig.selectedAccount).removeObserver(this, NotificationCenter.fileDidFailToLoad);
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.fileDidLoad) {
            String str = (String) objArr[0];
            File file = (File) objArr[1];
            ThemeInfo themeInfo = (ThemeInfo) this.loadingThemes.get(str);
            if (themeInfo != null) {
                this.loadingThemes.remove(str);
                if (this.loadingWallpapers.remove(themeInfo) != null) {
                    Utilities.globalQueue.postRunnable(new -$$Lambda$ThemesHorizontalListCell$cRgrOawQmmS6s43ziNB7cZl_7Fw(this, file, themeInfo));
                } else {
                    checkVisibleTheme(themeInfo);
                }
            }
        } else if (i == NotificationCenter.fileDidFailToLoad) {
            this.loadingThemes.remove((String) objArr[0]);
        }
    }

    public /* synthetic */ void lambda$didReceivedNotification$3$ThemesHorizontalListCell(File file, ThemeInfo themeInfo) {
        try {
            Bitmap scaledBitmap = getScaledBitmap((float) AndroidUtilities.dp(640.0f), (float) AndroidUtilities.dp(360.0f), file.getAbsolutePath(), null, 0);
            if (themeInfo.isBlured) {
                scaledBitmap = Utilities.blurWallpaper(scaledBitmap);
            }
            FileOutputStream fileOutputStream = new FileOutputStream(themeInfo.pathToWallpaper);
            scaledBitmap.compress(CompressFormat.JPEG, 87, fileOutputStream);
            fileOutputStream.close();
        } catch (Throwable th) {
            FileLog.e(th);
            themeInfo.badWallpaper = true;
        }
        AndroidUtilities.runOnUIThread(new -$$Lambda$ThemesHorizontalListCell$JjJ5zvhtO0g-DB75a_yZxv59SwE(this, themeInfo));
    }

    public /* synthetic */ void lambda$null$2$ThemesHorizontalListCell(ThemeInfo themeInfo) {
        checkVisibleTheme(themeInfo);
    }

    private void checkVisibleTheme(ThemeInfo themeInfo) {
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
        if (i == 0) {
            View view = (View) getParent();
            if (view != null) {
                i = view.getMeasuredWidth();
            }
        }
        if (i != 0) {
            this.prevThemeInfo = this.currentType == 1 ? Theme.getCurrentNightTheme() : Theme.getCurrentTheme();
            int indexOf = this.defaultThemes.indexOf(this.prevThemeInfo);
            if (indexOf < 0) {
                indexOf = this.darkThemes.indexOf(this.prevThemeInfo) + this.defaultThemes.size();
                if (indexOf < 0) {
                    return;
                }
            }
            if (z) {
                smoothScrollToPosition(indexOf);
            } else {
                this.horizontalLayoutManager.scrollToPositionWithOffset(indexOf, (i - AndroidUtilities.dp(76.0f)) / 2);
            }
        }
    }
}
