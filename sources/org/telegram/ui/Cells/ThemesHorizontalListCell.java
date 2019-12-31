package org.telegram.ui.Cells;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.text.TextPaint;
import android.text.TextUtils;
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
import java.util.ArrayList;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_theme;
import org.telegram.tgnet.TLRPC.TL_wallPaper;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.Theme.PatternsLoader;
import org.telegram.ui.ActionBar.Theme.ThemeAccent;
import org.telegram.ui.ActionBar.Theme.ThemeInfo;
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
        private ThemeInfo themeInfo;

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

        /* JADX WARNING: Unknown top exception splitter block from list: {B:107:0x0203=Splitter:B:107:0x0203, B:116:0x020e=Splitter:B:116:0x020e} */
        /* JADX WARNING: Removed duplicated region for block: B:103:0x01f4 A:{Catch:{ all -> 0x0209 }} */
        /* JADX WARNING: Missing exception handler attribute for start block: B:116:0x020e */
        /* JADX WARNING: Removed duplicated region for block: B:37:0x00fa A:{Catch:{ Exception -> 0x0106 }} */
        /* JADX WARNING: Removed duplicated region for block: B:41:0x0112 A:{Catch:{ all -> 0x0209 }} */
        /* JADX WARNING: Removed duplicated region for block: B:44:0x0124 A:{Catch:{ all -> 0x0209 }} */
        /* JADX WARNING: Missing exception handler attribute for start block: B:34:0x00ee */
        /* JADX WARNING: Removed duplicated region for block: B:41:0x0112 A:{Catch:{ all -> 0x0209 }} */
        /* JADX WARNING: Removed duplicated region for block: B:44:0x0124 A:{Catch:{ all -> 0x0209 }} */
        /* JADX WARNING: Missing exception handler attribute for start block: B:38:0x0106 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:67:0x0184 */
        /* JADX WARNING: Can't wrap try/catch for region: R(11:28|29|(2:31|(1:33))|34|35|(1:37)|38|39|(1:41)|42|(1:44)) */
        /* JADX WARNING: Can't wrap try/catch for region: R(11:28|29|(2:31|(1:33))|34|35|(1:37)|38|39|(1:41)|42|(1:44)) */
        /* JADX WARNING: Can't wrap try/catch for region: R(4:65|66|67|68) */
        /* JADX WARNING: Missing block: B:59:0x0168, code skipped:
            if (r14.equals(r15) == false) goto L_0x01e3;
     */
        /* JADX WARNING: Missing block: B:85:0x01be, code skipped:
            r4 = -1;
     */
        /* JADX WARNING: Missing block: B:86:0x01bf, code skipped:
            if (r4 == 0) goto L_0x01db;
     */
        /* JADX WARNING: Missing block: B:88:0x01c2, code skipped:
            if (r4 == 1) goto L_0x01d5;
     */
        /* JADX WARNING: Missing block: B:89:0x01c4, code skipped:
            if (r4 == 2) goto L_0x01cf;
     */
        /* JADX WARNING: Missing block: B:91:0x01c7, code skipped:
            if (r4 == 3) goto L_0x01ca;
     */
        /* JADX WARNING: Missing block: B:93:0x01ca, code skipped:
            r1.themeInfo.previewBackgroundGradientColor = r3;
     */
        /* JADX WARNING: Missing block: B:94:0x01cf, code skipped:
            r1.themeInfo.setPreviewBackgroundColor(r3);
     */
        /* JADX WARNING: Missing block: B:95:0x01d5, code skipped:
            r1.themeInfo.setPreviewOutColor(r3);
     */
        /* JADX WARNING: Missing block: B:96:0x01db, code skipped:
            r1.themeInfo.setPreviewInColor(r3);
     */
        /* JADX WARNING: Missing block: B:112:0x0209, code skipped:
            r0 = move-exception;
     */
        /* JADX WARNING: Missing block: B:113:0x020a, code skipped:
            r2 = r0;
     */
        /* JADX WARNING: Missing block: B:115:?, code skipped:
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
            r5 = new java.io.FileInputStream;	 Catch:{ all -> 0x020f }
            r5.<init>(r2);	 Catch:{ all -> 0x020f }
            r2 = 0;
            r6 = 0;
        L_0x0015:
            r7 = org.telegram.ui.Cells.ThemesHorizontalListCell.bytes;	 Catch:{ all -> 0x0207 }
            r7 = r5.read(r7);	 Catch:{ all -> 0x0207 }
            r8 = -1;
            if (r7 == r8) goto L_0x0203;
        L_0x0020:
            r11 = r2;
            r9 = 0;
            r10 = 0;
        L_0x0023:
            if (r9 >= r7) goto L_0x01f0;
        L_0x0025:
            r12 = org.telegram.ui.Cells.ThemesHorizontalListCell.bytes;	 Catch:{ all -> 0x0207 }
            r12 = r12[r9];	 Catch:{ all -> 0x0207 }
            r13 = 10;
            if (r12 != r13) goto L_0x01e6;
        L_0x002f:
            r12 = r9 - r10;
            r12 = r12 + r4;
            r13 = new java.lang.String;	 Catch:{ all -> 0x0207 }
            r14 = org.telegram.ui.Cells.ThemesHorizontalListCell.bytes;	 Catch:{ all -> 0x0207 }
            r15 = r12 + -1;
            r8 = "UTF-8";
            r13.<init>(r14, r10, r15, r8);	 Catch:{ all -> 0x0207 }
            r8 = "WLS=";
            r8 = r13.startsWith(r8);	 Catch:{ all -> 0x0207 }
            if (r8 == 0) goto L_0x012c;
        L_0x0047:
            r8 = 4;
            r8 = r13.substring(r8);	 Catch:{ all -> 0x0207 }
            r13 = android.net.Uri.parse(r8);	 Catch:{ all -> 0x0207 }
            r14 = r1.themeInfo;	 Catch:{ all -> 0x0207 }
            r15 = "slug";
            r15 = r13.getQueryParameter(r15);	 Catch:{ all -> 0x0207 }
            r14.slug = r15;	 Catch:{ all -> 0x0207 }
            r14 = r1.themeInfo;	 Catch:{ all -> 0x0207 }
            r15 = new java.io.File;	 Catch:{ all -> 0x0207 }
            r3 = org.telegram.messenger.ApplicationLoader.getFilesDirFixed();	 Catch:{ all -> 0x0207 }
            r4 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0207 }
            r4.<init>();	 Catch:{ all -> 0x0207 }
            r8 = org.telegram.messenger.Utilities.MD5(r8);	 Catch:{ all -> 0x0207 }
            r4.append(r8);	 Catch:{ all -> 0x0207 }
            r8 = ".wp";
            r4.append(r8);	 Catch:{ all -> 0x0207 }
            r4 = r4.toString();	 Catch:{ all -> 0x0207 }
            r15.<init>(r3, r4);	 Catch:{ all -> 0x0207 }
            r3 = r15.getAbsolutePath();	 Catch:{ all -> 0x0207 }
            r14.pathToWallpaper = r3;	 Catch:{ all -> 0x0207 }
            r3 = "mode";
            r3 = r13.getQueryParameter(r3);	 Catch:{ all -> 0x0207 }
            if (r3 == 0) goto L_0x00ad;
        L_0x0088:
            r3 = r3.toLowerCase();	 Catch:{ all -> 0x0207 }
            r4 = " ";
            r3 = r3.split(r4);	 Catch:{ all -> 0x0207 }
            if (r3 == 0) goto L_0x00ad;
        L_0x0094:
            r4 = r3.length;	 Catch:{ all -> 0x0207 }
            if (r4 <= 0) goto L_0x00ad;
        L_0x0097:
            r4 = 0;
        L_0x0098:
            r8 = r3.length;	 Catch:{ all -> 0x0207 }
            if (r4 >= r8) goto L_0x00ad;
        L_0x009b:
            r8 = "blur";
            r14 = r3[r4];	 Catch:{ all -> 0x0207 }
            r8 = r8.equals(r14);	 Catch:{ all -> 0x0207 }
            if (r8 == 0) goto L_0x00aa;
        L_0x00a5:
            r8 = r1.themeInfo;	 Catch:{ all -> 0x0207 }
            r14 = 1;
            r8.isBlured = r14;	 Catch:{ all -> 0x0207 }
        L_0x00aa:
            r4 = r4 + 1;
            goto L_0x0098;
        L_0x00ad:
            r3 = "pattern";
            r3 = r13.getQueryParameter(r3);	 Catch:{ all -> 0x0207 }
            r3 = android.text.TextUtils.isEmpty(r3);	 Catch:{ all -> 0x0207 }
            if (r3 != 0) goto L_0x01e1;
        L_0x00b9:
            r3 = "bg_color";
            r3 = r13.getQueryParameter(r3);	 Catch:{ Exception -> 0x00ee }
            r4 = android.text.TextUtils.isEmpty(r3);	 Catch:{ Exception -> 0x00ee }
            if (r4 != 0) goto L_0x00ee;
        L_0x00c5:
            r4 = r1.themeInfo;	 Catch:{ Exception -> 0x00ee }
            r8 = 6;
            r14 = 0;
            r15 = r3.substring(r14, r8);	 Catch:{ Exception -> 0x00ee }
            r14 = 16;
            r15 = java.lang.Integer.parseInt(r15, r14);	 Catch:{ Exception -> 0x00ee }
            r16 = -16777216; // 0xfffffffffvar_ float:-1.7014118E38 double:NaN;
            r15 = r15 | r16;
            r4.patternBgColor = r15;	 Catch:{ Exception -> 0x00ee }
            r4 = r3.length();	 Catch:{ Exception -> 0x00ee }
            if (r4 <= r8) goto L_0x00ee;
        L_0x00df:
            r4 = r1.themeInfo;	 Catch:{ Exception -> 0x00ee }
            r8 = 7;
            r3 = r3.substring(r8);	 Catch:{ Exception -> 0x00ee }
            r3 = java.lang.Integer.parseInt(r3, r14);	 Catch:{ Exception -> 0x00ee }
            r3 = r3 | r16;
            r4.patternBgGradientColor = r3;	 Catch:{ Exception -> 0x00ee }
        L_0x00ee:
            r3 = "rotation";
            r3 = r13.getQueryParameter(r3);	 Catch:{ Exception -> 0x0106 }
            r4 = android.text.TextUtils.isEmpty(r3);	 Catch:{ Exception -> 0x0106 }
            if (r4 != 0) goto L_0x0106;
        L_0x00fa:
            r4 = r1.themeInfo;	 Catch:{ Exception -> 0x0106 }
            r3 = org.telegram.messenger.Utilities.parseInt(r3);	 Catch:{ Exception -> 0x0106 }
            r3 = r3.intValue();	 Catch:{ Exception -> 0x0106 }
            r4.patternBgGradientRotation = r3;	 Catch:{ Exception -> 0x0106 }
        L_0x0106:
            r3 = "intensity";
            r3 = r13.getQueryParameter(r3);	 Catch:{ all -> 0x0207 }
            r4 = android.text.TextUtils.isEmpty(r3);	 Catch:{ all -> 0x0207 }
            if (r4 != 0) goto L_0x011e;
        L_0x0112:
            r4 = r1.themeInfo;	 Catch:{ all -> 0x0207 }
            r3 = org.telegram.messenger.Utilities.parseInt(r3);	 Catch:{ all -> 0x0207 }
            r3 = r3.intValue();	 Catch:{ all -> 0x0207 }
            r4.patternIntensity = r3;	 Catch:{ all -> 0x0207 }
        L_0x011e:
            r3 = r1.themeInfo;	 Catch:{ all -> 0x0207 }
            r3 = r3.patternIntensity;	 Catch:{ all -> 0x0207 }
            if (r3 != 0) goto L_0x01e1;
        L_0x0124:
            r3 = r1.themeInfo;	 Catch:{ all -> 0x0207 }
            r4 = 50;
            r3.patternIntensity = r4;	 Catch:{ all -> 0x0207 }
            goto L_0x01e1;
        L_0x012c:
            r3 = "WPS";
            r3 = r13.startsWith(r3);	 Catch:{ all -> 0x0207 }
            if (r3 == 0) goto L_0x013c;
        L_0x0134:
            r3 = r1.themeInfo;	 Catch:{ all -> 0x0207 }
            r12 = r12 + r11;
            r3.previewWallpaperOffset = r12;	 Catch:{ all -> 0x0207 }
            r6 = 1;
            goto L_0x01f2;
        L_0x013c:
            r3 = 61;
            r3 = r13.indexOf(r3);	 Catch:{ all -> 0x0207 }
            r4 = -1;
            if (r3 == r4) goto L_0x01e1;
        L_0x0145:
            r8 = 0;
            r14 = r13.substring(r8, r3);	 Catch:{ all -> 0x0207 }
            r8 = r14.equals(r0);	 Catch:{ all -> 0x0207 }
            r15 = "chat_wallpaper_gradient_to";
            r4 = "chat_wallpaper";
            r16 = r6;
            r6 = "chat_outBubble";
            if (r8 != 0) goto L_0x016a;
        L_0x0158:
            r8 = r14.equals(r6);	 Catch:{ all -> 0x0207 }
            if (r8 != 0) goto L_0x016a;
        L_0x015e:
            r8 = r14.equals(r4);	 Catch:{ all -> 0x0207 }
            if (r8 != 0) goto L_0x016a;
        L_0x0164:
            r8 = r14.equals(r15);	 Catch:{ all -> 0x0207 }
            if (r8 == 0) goto L_0x01e3;
        L_0x016a:
            r3 = r3 + 1;
            r3 = r13.substring(r3);	 Catch:{ all -> 0x0207 }
            r8 = r3.length();	 Catch:{ all -> 0x0207 }
            if (r8 <= 0) goto L_0x018d;
        L_0x0176:
            r8 = 0;
            r13 = r3.charAt(r8);	 Catch:{ all -> 0x0207 }
            r8 = 35;
            if (r13 != r8) goto L_0x018d;
        L_0x017f:
            r3 = android.graphics.Color.parseColor(r3);	 Catch:{ Exception -> 0x0184 }
            goto L_0x0195;
        L_0x0184:
            r3 = org.telegram.messenger.Utilities.parseInt(r3);	 Catch:{ all -> 0x0207 }
            r3 = r3.intValue();	 Catch:{ all -> 0x0207 }
            goto L_0x0195;
        L_0x018d:
            r3 = org.telegram.messenger.Utilities.parseInt(r3);	 Catch:{ all -> 0x0207 }
            r3 = r3.intValue();	 Catch:{ all -> 0x0207 }
        L_0x0195:
            r8 = r14.hashCode();	 Catch:{ all -> 0x0207 }
            r13 = 2;
            switch(r8) {
                case -1625862693: goto L_0x01b6;
                case -633951866: goto L_0x01ae;
                case 1269980952: goto L_0x01a6;
                case 2052611411: goto L_0x019e;
                default: goto L_0x019d;
            };	 Catch:{ all -> 0x0207 }
        L_0x019d:
            goto L_0x01be;
        L_0x019e:
            r4 = r14.equals(r6);	 Catch:{ all -> 0x0207 }
            if (r4 == 0) goto L_0x01be;
        L_0x01a4:
            r4 = 1;
            goto L_0x01bf;
        L_0x01a6:
            r4 = r14.equals(r0);	 Catch:{ all -> 0x0207 }
            if (r4 == 0) goto L_0x01be;
        L_0x01ac:
            r4 = 0;
            goto L_0x01bf;
        L_0x01ae:
            r4 = r14.equals(r15);	 Catch:{ all -> 0x0207 }
            if (r4 == 0) goto L_0x01be;
        L_0x01b4:
            r4 = 3;
            goto L_0x01bf;
        L_0x01b6:
            r4 = r14.equals(r4);	 Catch:{ all -> 0x0207 }
            if (r4 == 0) goto L_0x01be;
        L_0x01bc:
            r4 = 2;
            goto L_0x01bf;
        L_0x01be:
            r4 = -1;
        L_0x01bf:
            if (r4 == 0) goto L_0x01db;
        L_0x01c1:
            r6 = 1;
            if (r4 == r6) goto L_0x01d5;
        L_0x01c4:
            if (r4 == r13) goto L_0x01cf;
        L_0x01c6:
            r6 = 3;
            if (r4 == r6) goto L_0x01ca;
        L_0x01c9:
            goto L_0x01e3;
        L_0x01ca:
            r4 = r1.themeInfo;	 Catch:{ all -> 0x0207 }
            r4.previewBackgroundGradientColor = r3;	 Catch:{ all -> 0x0207 }
            goto L_0x01e3;
        L_0x01cf:
            r4 = r1.themeInfo;	 Catch:{ all -> 0x0207 }
            r4.setPreviewBackgroundColor(r3);	 Catch:{ all -> 0x0207 }
            goto L_0x01e3;
        L_0x01d5:
            r4 = r1.themeInfo;	 Catch:{ all -> 0x0207 }
            r4.setPreviewOutColor(r3);	 Catch:{ all -> 0x0207 }
            goto L_0x01e3;
        L_0x01db:
            r4 = r1.themeInfo;	 Catch:{ all -> 0x0207 }
            r4.setPreviewInColor(r3);	 Catch:{ all -> 0x0207 }
            goto L_0x01e3;
        L_0x01e1:
            r16 = r6;
        L_0x01e3:
            r10 = r10 + r12;
            r11 = r11 + r12;
            goto L_0x01e8;
        L_0x01e6:
            r16 = r6;
        L_0x01e8:
            r9 = r9 + 1;
            r6 = r16;
            r4 = 1;
            r8 = -1;
            goto L_0x0023;
        L_0x01f0:
            r16 = r6;
        L_0x01f2:
            if (r6 != 0) goto L_0x0203;
        L_0x01f4:
            if (r2 != r11) goto L_0x01f7;
        L_0x01f6:
            goto L_0x0203;
        L_0x01f7:
            r2 = r5.getChannel();	 Catch:{ all -> 0x0207 }
            r3 = (long) r11;	 Catch:{ all -> 0x0207 }
            r2.position(r3);	 Catch:{ all -> 0x0207 }
            r2 = r11;
            r4 = 1;
            goto L_0x0015;
        L_0x0203:
            r5.close();	 Catch:{ all -> 0x020f }
            goto L_0x0213;
        L_0x0207:
            r0 = move-exception;
            throw r0;	 Catch:{ all -> 0x0209 }
        L_0x0209:
            r0 = move-exception;
            r2 = r0;
            r5.close();	 Catch:{ all -> 0x020e }
        L_0x020e:
            throw r2;	 Catch:{ all -> 0x020f }
        L_0x020f:
            r0 = move-exception;
            org.telegram.messenger.FileLog.e(r0);
        L_0x0213:
            r0 = r1.themeInfo;
            r2 = r0.pathToWallpaper;
            if (r2 == 0) goto L_0x0265;
        L_0x0219:
            r0 = r0.badWallpaper;
            if (r0 != 0) goto L_0x0265;
        L_0x021d:
            r0 = new java.io.File;
            r0.<init>(r2);
            r0 = r0.exists();
            if (r0 != 0) goto L_0x0265;
        L_0x0228:
            r0 = org.telegram.ui.Cells.ThemesHorizontalListCell.this;
            r0 = r0.loadingWallpapers;
            r2 = r1.themeInfo;
            r0 = r0.containsKey(r2);
            if (r0 != 0) goto L_0x0263;
        L_0x0236:
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
        L_0x0263:
            r2 = 0;
            return r2;
        L_0x0265:
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
            TileMode tileMode;
            this.inDrawable.setColorFilter(new PorterDuffColorFilter(this.themeInfo.getPreviewInColor(), Mode.MULTIPLY));
            this.outDrawable.setColorFilter(new PorterDuffColorFilter(this.themeInfo.getPreviewOutColor(), Mode.MULTIPLY));
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
            ThemeInfo themeInfo = this.themeInfo;
            if (themeInfo.previewBackgroundGradientColor != 0) {
                GradientDrawable gradientDrawable = new GradientDrawable(Orientation.BL_TR, new int[]{themeInfo.getPreviewBackgroundColor(), this.themeInfo.previewBackgroundGradientColor});
                gradientDrawable.setCornerRadius((float) AndroidUtilities.dp(6.0f));
                this.backgroundDrawable = gradientDrawable;
                dArr = AndroidUtilities.rgbToHsv(Color.red(this.themeInfo.getPreviewBackgroundColor()), Color.green(this.themeInfo.getPreviewBackgroundColor()), Color.blue(this.themeInfo.getPreviewBackgroundColor()));
            } else if (themeInfo.previewWallpaperOffset > 0 || themeInfo.pathToWallpaper != null) {
                float dp = (float) AndroidUtilities.dp(76.0f);
                float dp2 = (float) AndroidUtilities.dp(97.0f);
                ThemeInfo themeInfo2 = this.themeInfo;
                Bitmap scaledBitmap = ThemesHorizontalListCell.getScaledBitmap(dp, dp2, themeInfo2.pathToWallpaper, themeInfo2.pathToFile, themeInfo2.previewWallpaperOffset);
                if (scaledBitmap != null) {
                    this.backgroundDrawable = new BitmapDrawable(scaledBitmap);
                    tileMode = TileMode.CLAMP;
                    this.bitmapShader = new BitmapShader(scaledBitmap, tileMode, tileMode);
                    this.bitmapPaint.setShader(this.bitmapShader);
                    int[] calcDrawableColor = AndroidUtilities.calcDrawableColor(this.backgroundDrawable);
                    dArr = AndroidUtilities.rgbToHsv(Color.red(calcDrawableColor[0]), Color.green(calcDrawableColor[0]), Color.blue(calcDrawableColor[0]));
                }
            } else if (themeInfo.getPreviewBackgroundColor() != 0) {
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
                tileMode = TileMode.REPEAT;
                this.bitmapShader = new BitmapShader(bitmap, tileMode, tileMode);
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
                themeInfo.setPreviewInColor(Theme.getDefaultColor("chat_inBubble"));
                this.themeInfo.setPreviewOutColor(Theme.getDefaultColor("chat_outBubble"));
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
        public void updateColors(boolean z) {
            int i;
            int i2;
            this.oldInColor = this.inColor;
            this.oldOutColor = this.outColor;
            this.oldBackColor = this.backColor;
            this.oldCheckColor = this.checkColor;
            int i3 = 0;
            ThemeAccent accent = this.themeInfo.getAccent(false);
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
            ThemeInfo themeInfo = this.themeInfo;
            this.inColor = Theme.changeColorAccent(themeInfo, i3, themeInfo.getPreviewInColor());
            ThemeInfo themeInfo2 = this.themeInfo;
            this.outColor = Theme.changeColorAccent(themeInfo2, i, themeInfo2.getPreviewOutColor());
            themeInfo2 = this.themeInfo;
            this.backColor = Theme.changeColorAccent(themeInfo2, i2, themeInfo2.getPreviewBackgroundColor());
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

        /* Access modifiers changed, original: protected */
        /* JADX WARNING: Missing block: B:46:0x01cd, code skipped:
            if ("Arctic Blue".equals(r13.themeInfo.name) != false) goto L_0x01cf;
     */
        public void onDraw(android.graphics.Canvas r14) {
            /*
            r13 = this;
            r0 = r13.accentId;
            r1 = r13.themeInfo;
            r1 = r1.currentAccentId;
            r2 = 1;
            if (r0 == r1) goto L_0x000c;
        L_0x0009:
            r13.updateColors(r2);
        L_0x000c:
            r0 = r13.isFirst;
            r1 = NUM; // 0x41b00000 float:22.0 double:5.44486713E-315;
            r3 = 0;
            if (r0 == 0) goto L_0x0018;
        L_0x0013:
            r0 = org.telegram.messenger.AndroidUtilities.dp(r1);
            goto L_0x0019;
        L_0x0018:
            r0 = 0;
        L_0x0019:
            r4 = NUM; // 0x41300000 float:11.0 double:5.4034219E-315;
            r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
            r5 = r13.rect;
            r6 = (float) r0;
            r7 = (float) r4;
            r8 = NUM; // 0x42980000 float:76.0 double:5.51998661E-315;
            r9 = org.telegram.messenger.AndroidUtilities.dp(r8);
            r9 = r9 + r0;
            r9 = (float) r9;
            r10 = NUM; // 0x42CLASSNAME float:97.0 double:5.533585826E-315;
            r10 = org.telegram.messenger.AndroidUtilities.dp(r10);
            r4 = r4 + r10;
            r4 = (float) r4;
            r5.set(r6, r7, r9, r4);
            r4 = r13.themeInfo;
            r4 = r4.getName();
            r5 = r4.toLowerCase();
            r9 = ".attheme";
            r5 = r5.endsWith(r9);
            if (r5 == 0) goto L_0x0052;
        L_0x0048:
            r5 = 46;
            r5 = r4.lastIndexOf(r5);
            r4 = r4.substring(r3, r5);
        L_0x0052:
            r5 = r13.getMeasuredWidth();
            r9 = r13.isFirst;
            if (r9 == 0) goto L_0x005d;
        L_0x005a:
            r9 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
            goto L_0x005f;
        L_0x005d:
            r9 = NUM; // 0x41700000 float:15.0 double:5.424144515E-315;
        L_0x005f:
            r9 = org.telegram.messenger.AndroidUtilities.dp(r9);
            r5 = r5 - r9;
            r9 = r13.isLast;
            if (r9 == 0) goto L_0x006f;
        L_0x0068:
            r9 = NUM; // 0x40e00000 float:7.0 double:5.37751863E-315;
            r9 = org.telegram.messenger.AndroidUtilities.dp(r9);
            goto L_0x0070;
        L_0x006f:
            r9 = 0;
        L_0x0070:
            r5 = r5 - r9;
            r9 = r13.textPaint;
            r5 = (float) r5;
            r10 = android.text.TextUtils.TruncateAt.END;
            r4 = android.text.TextUtils.ellipsize(r4, r9, r5, r10);
            r4 = r4.toString();
            r5 = r13.textPaint;
            r5 = r5.measureText(r4);
            r9 = (double) r5;
            r9 = java.lang.Math.ceil(r9);
            r5 = (int) r9;
            r9 = r13.textPaint;
            r10 = "windowBackgroundWhiteBlackText";
            r10 = org.telegram.ui.ActionBar.Theme.getColor(r10);
            r9.setColor(r10);
            r8 = org.telegram.messenger.AndroidUtilities.dp(r8);
            r8 = r8 - r5;
            r8 = r8 / 2;
            r8 = r8 + r0;
            r5 = (float) r8;
            r8 = NUM; // 0x43030000 float:131.0 double:5.55463223E-315;
            r8 = org.telegram.messenger.AndroidUtilities.dp(r8);
            r8 = (float) r8;
            r9 = r13.textPaint;
            r14.drawText(r4, r5, r8, r9);
            r4 = r13.themeInfo;
            r5 = r4.info;
            if (r5 == 0) goto L_0x00bb;
        L_0x00b1:
            r5 = r5.document;
            if (r5 == 0) goto L_0x00ba;
        L_0x00b5:
            r4 = r4.themeLoaded;
            if (r4 == 0) goto L_0x00ba;
        L_0x00b9:
            goto L_0x00bb;
        L_0x00ba:
            r2 = 0;
        L_0x00bb:
            r4 = NUM; // 0x2bb0b5ba float:1.2555991E-12 double:3.621506846E-315;
            r5 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
            r8 = NUM; // 0x40CLASSNAME float:6.0 double:5.367157323E-315;
            if (r2 == 0) goto L_0x0292;
        L_0x00c4:
            r2 = r13.paint;
            r9 = r13.oldBackColor;
            r10 = r13.backColor;
            r9 = r13.blend(r9, r10);
            r2.setColor(r9);
            r2 = r13.accentColorChanged;
            if (r2 == 0) goto L_0x00ff;
        L_0x00d5:
            r2 = r13.inDrawable;
            r9 = new android.graphics.PorterDuffColorFilter;
            r10 = r13.oldInColor;
            r11 = r13.inColor;
            r10 = r13.blend(r10, r11);
            r11 = android.graphics.PorterDuff.Mode.MULTIPLY;
            r9.<init>(r10, r11);
            r2.setColorFilter(r9);
            r2 = r13.outDrawable;
            r9 = new android.graphics.PorterDuffColorFilter;
            r10 = r13.oldOutColor;
            r11 = r13.outColor;
            r10 = r13.blend(r10, r11);
            r11 = android.graphics.PorterDuff.Mode.MULTIPLY;
            r9.<init>(r10, r11);
            r2.setColorFilter(r9);
            r13.accentColorChanged = r3;
        L_0x00ff:
            r2 = r13.backgroundDrawable;
            if (r2 == 0) goto L_0x0196;
        L_0x0103:
            r3 = r13.bitmapShader;
            if (r3 == 0) goto L_0x017f;
        L_0x0107:
            r2 = (android.graphics.drawable.BitmapDrawable) r2;
            r3 = r2.getBitmap();
            r3 = r3.getWidth();
            r3 = (float) r3;
            r2 = r2.getBitmap();
            r2 = r2.getHeight();
            r2 = (float) r2;
            r9 = r13.rect;
            r9 = r9.width();
            r9 = r3 / r9;
            r10 = r13.rect;
            r10 = r10.height();
            r10 = r2 / r10;
            r11 = r13.shaderMatrix;
            r11.reset();
            r11 = java.lang.Math.min(r9, r10);
            r11 = r5 / r11;
            r3 = r3 / r10;
            r10 = r13.rect;
            r10 = r10.width();
            r12 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
            r10 = (r3 > r10 ? 1 : (r3 == r10 ? 0 : -1));
            if (r10 <= 0) goto L_0x0152;
        L_0x0143:
            r2 = r13.shaderMatrix;
            r9 = r13.rect;
            r9 = r9.width();
            r3 = r3 - r9;
            r3 = r3 / r12;
            r6 = r6 - r3;
            r2.setTranslate(r6, r7);
            goto L_0x0161;
        L_0x0152:
            r2 = r2 / r9;
            r3 = r13.shaderMatrix;
            r9 = r13.rect;
            r9 = r9.height();
            r2 = r2 - r9;
            r2 = r2 / r12;
            r7 = r7 - r2;
            r3.setTranslate(r6, r7);
        L_0x0161:
            r2 = r13.shaderMatrix;
            r2.preScale(r11, r11);
            r2 = r13.bitmapShader;
            r3 = r13.shaderMatrix;
            r2.setLocalMatrix(r3);
            r2 = r13.rect;
            r3 = org.telegram.messenger.AndroidUtilities.dp(r8);
            r3 = (float) r3;
            r6 = org.telegram.messenger.AndroidUtilities.dp(r8);
            r6 = (float) r6;
            r7 = r13.bitmapPaint;
            r14.drawRoundRect(r2, r3, r6, r7);
            goto L_0x01a7;
        L_0x017f:
            r3 = r13.rect;
            r6 = r3.left;
            r6 = (int) r6;
            r7 = r3.top;
            r7 = (int) r7;
            r9 = r3.right;
            r9 = (int) r9;
            r3 = r3.bottom;
            r3 = (int) r3;
            r2.setBounds(r6, r7, r9, r3);
            r2 = r13.backgroundDrawable;
            r2.draw(r14);
            goto L_0x01a7;
        L_0x0196:
            r2 = r13.rect;
            r3 = org.telegram.messenger.AndroidUtilities.dp(r8);
            r3 = (float) r3;
            r6 = org.telegram.messenger.AndroidUtilities.dp(r8);
            r6 = (float) r6;
            r7 = r13.paint;
            r14.drawRoundRect(r2, r3, r6, r7);
        L_0x01a7:
            r2 = r13.button;
            r3 = NUM; // 0x66ffffff float:6.0446287E23 double:8.537717435E-315;
            r6 = -1;
            r2.setColor(r3, r6);
            r2 = r13.themeInfo;
            r3 = r2.accentBaseColor;
            r6 = -5000269; // 0xffffffffffb3b3b3 float:NaN double:NaN;
            if (r3 == 0) goto L_0x01f3;
        L_0x01b9:
            r2 = r2.name;
            r3 = "Day";
            r2 = r3.equals(r2);
            if (r2 != 0) goto L_0x01cf;
        L_0x01c3:
            r2 = r13.themeInfo;
            r2 = r2.name;
            r3 = "Arctic Blue";
            r2 = r3.equals(r2);
            if (r2 == 0) goto L_0x0216;
        L_0x01cf:
            r2 = r13.button;
            r3 = r13.oldCheckColor;
            r7 = r13.checkColor;
            r3 = r13.blend(r3, r7);
            r2.setColor(r6, r3);
            r2 = org.telegram.ui.ActionBar.Theme.chat_instantViewRectPaint;
            r2.setColor(r4);
            r2 = r13.rect;
            r3 = org.telegram.messenger.AndroidUtilities.dp(r8);
            r3 = (float) r3;
            r6 = org.telegram.messenger.AndroidUtilities.dp(r8);
            r6 = (float) r6;
            r7 = org.telegram.ui.ActionBar.Theme.chat_instantViewRectPaint;
            r14.drawRoundRect(r2, r3, r6, r7);
            goto L_0x0216;
        L_0x01f3:
            r3 = r13.hasWhiteBackground;
            if (r3 == 0) goto L_0x0216;
        L_0x01f7:
            r3 = r13.button;
            r2 = r2.getPreviewOutColor();
            r3.setColor(r6, r2);
            r2 = org.telegram.ui.ActionBar.Theme.chat_instantViewRectPaint;
            r2.setColor(r4);
            r2 = r13.rect;
            r3 = org.telegram.messenger.AndroidUtilities.dp(r8);
            r3 = (float) r3;
            r6 = org.telegram.messenger.AndroidUtilities.dp(r8);
            r6 = (float) r6;
            r7 = org.telegram.ui.ActionBar.Theme.chat_instantViewRectPaint;
            r14.drawRoundRect(r2, r3, r6, r7);
        L_0x0216:
            r2 = r13.inDrawable;
            r3 = org.telegram.messenger.AndroidUtilities.dp(r8);
            r3 = r3 + r0;
            r1 = org.telegram.messenger.AndroidUtilities.dp(r1);
            r6 = NUM; // 0x42440000 float:49.0 double:5.492788177E-315;
            r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
            r6 = r6 + r0;
            r7 = NUM; // 0x42100000 float:36.0 double:5.47595105E-315;
            r7 = org.telegram.messenger.AndroidUtilities.dp(r7);
            r2.setBounds(r3, r1, r6, r7);
            r1 = r13.inDrawable;
            r1.draw(r14);
            r1 = r13.outDrawable;
            r2 = NUM; // 0x41d80000 float:27.0 double:5.457818764E-315;
            r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
            r2 = r2 + r0;
            r3 = NUM; // 0x42240000 float:41.0 double:5.48242687E-315;
            r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
            r6 = NUM; // 0x428CLASSNAME float:70.0 double:5.51610112E-315;
            r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
            r0 = r0 + r6;
            r6 = NUM; // 0x425CLASSNAME float:55.0 double:5.50055916E-315;
            r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
            r1.setBounds(r2, r3, r0, r6);
            r0 = r13.outDrawable;
            r0.draw(r14);
            r0 = r13.optionsDrawable;
            if (r0 == 0) goto L_0x0292;
        L_0x025e:
            r0 = org.telegram.ui.Cells.ThemesHorizontalListCell.this;
            r0 = r0.currentType;
            if (r0 != 0) goto L_0x0292;
        L_0x0266:
            r0 = r13.rect;
            r0 = r0.right;
            r0 = (int) r0;
            r1 = NUM; // 0x41800000 float:16.0 double:5.42932517E-315;
            r1 = org.telegram.messenger.AndroidUtilities.dp(r1);
            r0 = r0 - r1;
            r1 = r13.rect;
            r1 = r1.top;
            r1 = (int) r1;
            r2 = org.telegram.messenger.AndroidUtilities.dp(r8);
            r1 = r1 + r2;
            r2 = r13.optionsDrawable;
            r3 = r2.getIntrinsicWidth();
            r3 = r3 + r0;
            r6 = r13.optionsDrawable;
            r6 = r6.getIntrinsicHeight();
            r6 = r6 + r1;
            r2.setBounds(r0, r1, r3, r6);
            r0 = r13.optionsDrawable;
            r0.draw(r14);
        L_0x0292:
            r0 = r13.themeInfo;
            r0 = r0.info;
            r1 = "windowBackgroundWhiteGrayText7";
            r2 = 0;
            if (r0 == 0) goto L_0x0308;
        L_0x029c:
            r0 = r0.document;
            if (r0 != 0) goto L_0x0308;
        L_0x02a0:
            r0 = r13.button;
            r0.setAlpha(r2);
            r0 = org.telegram.ui.ActionBar.Theme.chat_instantViewRectPaint;
            r0.setColor(r4);
            r0 = r13.rect;
            r2 = org.telegram.messenger.AndroidUtilities.dp(r8);
            r2 = (float) r2;
            r3 = org.telegram.messenger.AndroidUtilities.dp(r8);
            r3 = (float) r3;
            r4 = org.telegram.ui.ActionBar.Theme.chat_instantViewRectPaint;
            r14.drawRoundRect(r0, r2, r3, r4);
            r0 = r13.loadingDrawable;
            if (r0 == 0) goto L_0x03d8;
        L_0x02bf:
            r0 = org.telegram.ui.ActionBar.Theme.getColor(r1);
            r1 = r13.loadingColor;
            if (r1 == r0) goto L_0x02ce;
        L_0x02c7:
            r1 = r13.loadingDrawable;
            r13.loadingColor = r0;
            org.telegram.ui.ActionBar.Theme.setDrawableColor(r1, r0);
        L_0x02ce:
            r0 = r13.rect;
            r0 = r0.centerX();
            r1 = r13.loadingDrawable;
            r1 = r1.getIntrinsicWidth();
            r1 = r1 / 2;
            r1 = (float) r1;
            r0 = r0 - r1;
            r0 = (int) r0;
            r1 = r13.rect;
            r1 = r1.centerY();
            r2 = r13.loadingDrawable;
            r2 = r2.getIntrinsicHeight();
            r2 = r2 / 2;
            r2 = (float) r2;
            r1 = r1 - r2;
            r1 = (int) r1;
            r2 = r13.loadingDrawable;
            r3 = r2.getIntrinsicWidth();
            r3 = r3 + r0;
            r4 = r13.loadingDrawable;
            r4 = r4.getIntrinsicHeight();
            r4 = r4 + r1;
            r2.setBounds(r0, r1, r3, r4);
            r0 = r13.loadingDrawable;
            r0.draw(r14);
            goto L_0x03d8;
        L_0x0308:
            r0 = r13.themeInfo;
            r3 = r0.info;
            if (r3 == 0) goto L_0x0312;
        L_0x030e:
            r0 = r0.themeLoaded;
            if (r0 == 0) goto L_0x0318;
        L_0x0312:
            r0 = r13.placeholderAlpha;
            r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1));
            if (r0 <= 0) goto L_0x03c9;
        L_0x0318:
            r0 = r13.button;
            r3 = r13.placeholderAlpha;
            r5 = r5 - r3;
            r0.setAlpha(r5);
            r0 = r13.paint;
            r3 = "windowBackgroundGray";
            r3 = org.telegram.ui.ActionBar.Theme.getColor(r3);
            r0.setColor(r3);
            r0 = r13.paint;
            r3 = r13.placeholderAlpha;
            r4 = NUM; // 0x437var_ float:255.0 double:5.5947823E-315;
            r3 = r3 * r4;
            r3 = (int) r3;
            r0.setAlpha(r3);
            r0 = r13.rect;
            r3 = org.telegram.messenger.AndroidUtilities.dp(r8);
            r3 = (float) r3;
            r5 = org.telegram.messenger.AndroidUtilities.dp(r8);
            r5 = (float) r5;
            r6 = r13.paint;
            r14.drawRoundRect(r0, r3, r5, r6);
            r0 = r13.loadingDrawable;
            if (r0 == 0) goto L_0x039e;
        L_0x034d:
            r0 = org.telegram.ui.ActionBar.Theme.getColor(r1);
            r1 = r13.loadingColor;
            if (r1 == r0) goto L_0x035c;
        L_0x0355:
            r1 = r13.loadingDrawable;
            r13.loadingColor = r0;
            org.telegram.ui.ActionBar.Theme.setDrawableColor(r1, r0);
        L_0x035c:
            r0 = r13.rect;
            r0 = r0.centerX();
            r1 = r13.loadingDrawable;
            r1 = r1.getIntrinsicWidth();
            r1 = r1 / 2;
            r1 = (float) r1;
            r0 = r0 - r1;
            r0 = (int) r0;
            r1 = r13.rect;
            r1 = r1.centerY();
            r3 = r13.loadingDrawable;
            r3 = r3.getIntrinsicHeight();
            r3 = r3 / 2;
            r3 = (float) r3;
            r1 = r1 - r3;
            r1 = (int) r1;
            r3 = r13.loadingDrawable;
            r5 = r13.placeholderAlpha;
            r5 = r5 * r4;
            r4 = (int) r5;
            r3.setAlpha(r4);
            r3 = r13.loadingDrawable;
            r4 = r3.getIntrinsicWidth();
            r4 = r4 + r0;
            r5 = r13.loadingDrawable;
            r5 = r5.getIntrinsicHeight();
            r5 = r5 + r1;
            r3.setBounds(r0, r1, r4, r5);
            r0 = r13.loadingDrawable;
            r0.draw(r14);
        L_0x039e:
            r14 = r13.themeInfo;
            r14 = r14.themeLoaded;
            if (r14 == 0) goto L_0x03d8;
        L_0x03a4:
            r0 = android.os.SystemClock.uptimeMillis();
            r3 = 17;
            r5 = r13.lastDrawTime;
            r5 = r0 - r5;
            r3 = java.lang.Math.min(r3, r5);
            r13.lastDrawTime = r0;
            r14 = r13.placeholderAlpha;
            r0 = (float) r3;
            r1 = NUM; // 0x43340000 float:180.0 double:5.570497984E-315;
            r0 = r0 / r1;
            r14 = r14 - r0;
            r13.placeholderAlpha = r14;
            r14 = r13.placeholderAlpha;
            r14 = (r14 > r2 ? 1 : (r14 == r2 ? 0 : -1));
            if (r14 >= 0) goto L_0x03c5;
        L_0x03c3:
            r13.placeholderAlpha = r2;
        L_0x03c5:
            r13.invalidate();
            goto L_0x03d8;
        L_0x03c9:
            r14 = r13.button;
            r14 = r14.getAlpha();
            r14 = (r14 > r5 ? 1 : (r14 == r5 ? 0 : -1));
            if (r14 == 0) goto L_0x03d8;
        L_0x03d3:
            r14 = r13.button;
            r14.setAlpha(r5);
        L_0x03d8:
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ThemesHorizontalListCell$InnerThemeView.onDraw(android.graphics.Canvas):void");
        }

        private int blend(int i, int i2) {
            float f = this.accentState;
            if (f == 1.0f) {
                return i2;
            }
            return ((Integer) this.evaluator.evaluate(f, Integer.valueOf(i), Integer.valueOf(i2))).intValue();
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
        selectTheme(((InnerThemeView) view).themeInfo);
        i = view.getLeft();
        int right = view.getRight();
        if (i < 0) {
            smoothScrollBy(i - AndroidUtilities.dp(8.0f), 0);
        } else if (right > getMeasuredWidth()) {
            smoothScrollBy(right - getMeasuredWidth(), 0);
        }
    }

    public /* synthetic */ boolean lambda$new$1$ThemesHorizontalListCell(View view, int i) {
        showOptionsForTheme(((InnerThemeView) view).themeInfo);
        return true;
    }

    public void selectTheme(ThemeInfo themeInfo) {
        TL_theme tL_theme = themeInfo.info;
        if (tL_theme != null) {
            if (!themeInfo.themeLoaded) {
                return;
            }
            if (tL_theme.document == null) {
                presentFragment(new ThemeSetUrlActivity(themeInfo, null, true));
                return;
            }
        }
        int i = 0;
        if (!TextUtils.isEmpty(themeInfo.assetName)) {
            PatternsLoader.createLoader(false);
        }
        if (this.currentType != 2) {
            Editor edit = ApplicationLoader.applicationContext.getSharedPreferences("themeconfig", 0).edit();
            String str = (this.currentType == 1 || themeInfo.isDark()) ? "lastDarkTheme" : "lastDayTheme";
            edit.putString(str, themeInfo.getKey());
            edit.commit();
        }
        if (this.currentType == 1) {
            if (themeInfo != Theme.getCurrentNightTheme()) {
                Theme.setCurrentNightTheme(themeInfo);
            } else {
                return;
            }
        } else if (themeInfo != Theme.getCurrentTheme()) {
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.needSetDayNightTheme, themeInfo, Boolean.valueOf(false), null, Integer.valueOf(-1));
        } else {
            return;
        }
        updateRows();
        int childCount = getChildCount();
        while (i < childCount) {
            View childAt = getChildAt(i);
            if (childAt instanceof InnerThemeView) {
                ((InnerThemeView) childAt).updateCurrentThemeCheck();
            }
            i++;
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
        for (int i = 0; i < 3; i++) {
            NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.fileDidLoad);
            NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.fileDidFailToLoad);
        }
    }

    /* Access modifiers changed, original: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        for (int i = 0; i < 3; i++) {
            NotificationCenter.getInstance(i).removeObserver(this, NotificationCenter.fileDidLoad);
            NotificationCenter.getInstance(i).removeObserver(this, NotificationCenter.fileDidFailToLoad);
        }
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.fileDidLoad) {
            String str = (String) objArr[0];
            File file = (File) objArr[1];
            ThemeInfo themeInfo = (ThemeInfo) this.loadingThemes.get(str);
            if (themeInfo != null) {
                this.loadingThemes.remove(str);
                if (this.loadingWallpapers.remove(themeInfo) != null) {
                    Utilities.globalQueue.postRunnable(new -$$Lambda$ThemesHorizontalListCell$p_UmD2l5pY3SvkQSJFDqw5WT8mE(this, themeInfo, file));
                } else {
                    checkVisibleTheme(themeInfo);
                }
            }
        } else if (i == NotificationCenter.fileDidFailToLoad) {
            this.loadingThemes.remove((String) objArr[0]);
        }
    }

    public /* synthetic */ void lambda$didReceivedNotification$3$ThemesHorizontalListCell(ThemeInfo themeInfo, File file) {
        themeInfo.badWallpaper = themeInfo.createBackground(file, themeInfo.pathToWallpaper) ^ 1;
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
