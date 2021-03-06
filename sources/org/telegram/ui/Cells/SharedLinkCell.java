package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.tgnet.TLRPC$WebPage;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.CheckBox2;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.LetterDrawable;
import org.telegram.ui.Components.LinkPath;

public class SharedLinkCell extends FrameLayout {
    private StaticLayout captionLayout;
    private TextPaint captionTextPaint;
    private int captionY;
    private CheckBox2 checkBox;
    /* access modifiers changed from: private */
    public boolean checkingForLongPress;
    private StaticLayout dateLayout;
    private int dateLayoutX;
    /* access modifiers changed from: private */
    public SharedLinkCellDelegate delegate;
    private TextPaint description2TextPaint;
    private int description2Y;
    private StaticLayout descriptionLayout;
    private StaticLayout descriptionLayout2;
    private TextPaint descriptionTextPaint;
    private int descriptionY;
    private boolean drawLinkImageView;
    private StaticLayout fromInfoLayout;
    private int fromInfoLayoutY;
    private LetterDrawable letterDrawable;
    private ImageReceiver linkImageView;
    private ArrayList<StaticLayout> linkLayout;
    private boolean linkPreviewPressed;
    private int linkY;
    ArrayList<String> links;
    private MessageObject message;
    private boolean needDivider;
    /* access modifiers changed from: private */
    public CheckForLongPress pendingCheckForLongPress;
    private CheckForTap pendingCheckForTap;
    /* access modifiers changed from: private */
    public int pressCount;
    /* access modifiers changed from: private */
    public int pressedLink;
    private StaticLayout titleLayout;
    private TextPaint titleTextPaint;
    private int titleY;
    private LinkPath urlPath;
    private int viewType;

    public interface SharedLinkCellDelegate {
        boolean canPerformActions();

        void needOpenWebView(TLRPC$WebPage tLRPC$WebPage);

        void onLinkPress(String str, boolean z);
    }

    static /* synthetic */ int access$104(SharedLinkCell sharedLinkCell) {
        int i = sharedLinkCell.pressCount + 1;
        sharedLinkCell.pressCount = i;
        return i;
    }

    private final class CheckForTap implements Runnable {
        private CheckForTap() {
        }

        public void run() {
            if (SharedLinkCell.this.pendingCheckForLongPress == null) {
                SharedLinkCell sharedLinkCell = SharedLinkCell.this;
                CheckForLongPress unused = sharedLinkCell.pendingCheckForLongPress = new CheckForLongPress();
            }
            SharedLinkCell.this.pendingCheckForLongPress.currentPressCount = SharedLinkCell.access$104(SharedLinkCell.this);
            SharedLinkCell sharedLinkCell2 = SharedLinkCell.this;
            sharedLinkCell2.postDelayed(sharedLinkCell2.pendingCheckForLongPress, (long) (ViewConfiguration.getLongPressTimeout() - ViewConfiguration.getTapTimeout()));
        }
    }

    class CheckForLongPress implements Runnable {
        public int currentPressCount;

        CheckForLongPress() {
        }

        public void run() {
            if (SharedLinkCell.this.checkingForLongPress && SharedLinkCell.this.getParent() != null && this.currentPressCount == SharedLinkCell.this.pressCount) {
                boolean unused = SharedLinkCell.this.checkingForLongPress = false;
                SharedLinkCell.this.performHapticFeedback(0);
                if (SharedLinkCell.this.pressedLink >= 0) {
                    SharedLinkCellDelegate access$400 = SharedLinkCell.this.delegate;
                    SharedLinkCell sharedLinkCell = SharedLinkCell.this;
                    access$400.onLinkPress(sharedLinkCell.links.get(sharedLinkCell.pressedLink), true);
                }
                MotionEvent obtain = MotionEvent.obtain(0, 0, 3, 0.0f, 0.0f, 0);
                SharedLinkCell.this.onTouchEvent(obtain);
                obtain.recycle();
            }
        }
    }

    /* access modifiers changed from: protected */
    public void startCheckLongPress() {
        if (!this.checkingForLongPress) {
            this.checkingForLongPress = true;
            if (this.pendingCheckForTap == null) {
                this.pendingCheckForTap = new CheckForTap();
            }
            postDelayed(this.pendingCheckForTap, (long) ViewConfiguration.getTapTimeout());
        }
    }

    /* access modifiers changed from: protected */
    public void cancelCheckLongPress() {
        this.checkingForLongPress = false;
        CheckForLongPress checkForLongPress = this.pendingCheckForLongPress;
        if (checkForLongPress != null) {
            removeCallbacks(checkForLongPress);
        }
        CheckForTap checkForTap = this.pendingCheckForTap;
        if (checkForTap != null) {
            removeCallbacks(checkForTap);
        }
    }

    public SharedLinkCell(Context context) {
        this(context, 0);
    }

    public SharedLinkCell(Context context, int i) {
        super(context);
        this.checkingForLongPress = false;
        this.pendingCheckForLongPress = null;
        this.pressCount = 0;
        this.pendingCheckForTap = null;
        this.links = new ArrayList<>();
        this.linkLayout = new ArrayList<>();
        this.titleY = AndroidUtilities.dp(10.0f);
        this.descriptionY = AndroidUtilities.dp(30.0f);
        this.description2Y = AndroidUtilities.dp(30.0f);
        this.captionY = AndroidUtilities.dp(30.0f);
        this.fromInfoLayoutY = AndroidUtilities.dp(30.0f);
        this.viewType = i;
        setFocusable(true);
        LinkPath linkPath = new LinkPath();
        this.urlPath = linkPath;
        linkPath.setUseRoundRect(true);
        TextPaint textPaint = new TextPaint(1);
        this.titleTextPaint = textPaint;
        textPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.titleTextPaint.setColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.descriptionTextPaint = new TextPaint(1);
        this.titleTextPaint.setTextSize((float) AndroidUtilities.dp(14.0f));
        this.descriptionTextPaint.setTextSize((float) AndroidUtilities.dp(14.0f));
        setWillNotDraw(false);
        ImageReceiver imageReceiver = new ImageReceiver(this);
        this.linkImageView = imageReceiver;
        imageReceiver.setRoundRadius(AndroidUtilities.dp(4.0f));
        this.letterDrawable = new LetterDrawable();
        CheckBox2 checkBox2 = new CheckBox2(context, 21);
        this.checkBox = checkBox2;
        checkBox2.setVisibility(4);
        this.checkBox.setColor((String) null, "windowBackgroundWhite", "checkboxCheck");
        this.checkBox.setDrawUnchecked(false);
        this.checkBox.setDrawBackgroundAsArc(2);
        CheckBox2 checkBox22 = this.checkBox;
        boolean z = LocaleController.isRTL;
        addView(checkBox22, LayoutHelper.createFrame(24, 24.0f, (z ? 5 : 3) | 48, z ? 0.0f : 44.0f, 44.0f, z ? 44.0f : 0.0f, 0.0f));
        if (i == 1) {
            TextPaint textPaint2 = new TextPaint(1);
            this.description2TextPaint = textPaint2;
            textPaint2.setTextSize((float) AndroidUtilities.dp(13.0f));
        }
        TextPaint textPaint3 = new TextPaint(1);
        this.captionTextPaint = textPaint3;
        textPaint3.setTextSize((float) AndroidUtilities.dp(13.0f));
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:114:0x0223  */
    /* JADX WARNING: Removed duplicated region for block: B:121:0x0237  */
    /* JADX WARNING: Removed duplicated region for block: B:122:0x0273  */
    /* JADX WARNING: Removed duplicated region for block: B:125:0x0278 A[SYNTHETIC, Splitter:B:125:0x0278] */
    /* JADX WARNING: Removed duplicated region for block: B:138:0x02d0  */
    /* JADX WARNING: Removed duplicated region for block: B:139:0x02d5  */
    /* JADX WARNING: Removed duplicated region for block: B:142:0x02e0  */
    /* JADX WARNING: Removed duplicated region for block: B:143:0x02e3  */
    /* JADX WARNING: Removed duplicated region for block: B:146:0x02e8 A[SYNTHETIC, Splitter:B:146:0x02e8] */
    /* JADX WARNING: Removed duplicated region for block: B:154:0x0319 A[SYNTHETIC, Splitter:B:154:0x0319] */
    /* JADX WARNING: Removed duplicated region for block: B:169:0x03b3  */
    /* JADX WARNING: Removed duplicated region for block: B:172:0x03d2  */
    /* JADX WARNING: Removed duplicated region for block: B:187:0x044d  */
    /* JADX WARNING: Removed duplicated region for block: B:188:0x0458  */
    /* JADX WARNING: Removed duplicated region for block: B:191:0x0471  */
    /* JADX WARNING: Removed duplicated region for block: B:200:0x04ee  */
    /* JADX WARNING: Removed duplicated region for block: B:205:0x050b  */
    /* JADX WARNING: Removed duplicated region for block: B:206:0x051d  */
    /* JADX WARNING: Removed duplicated region for block: B:223:0x0573  */
    /* JADX WARNING: Removed duplicated region for block: B:227:0x0581  */
    /* JADX WARNING: Removed duplicated region for block: B:233:0x05a1  */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x0073  */
    /* JADX WARNING: Removed duplicated region for block: B:99:0x01d7 A[Catch:{ Exception -> 0x0217 }] */
    @android.annotation.SuppressLint({"DrawAllocation"})
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onMeasure(int r29, int r30) {
        /*
            r28 = this;
            r1 = r28
            r2 = 0
            r1.drawLinkImageView = r2
            r3 = 0
            r1.descriptionLayout = r3
            r1.titleLayout = r3
            r1.descriptionLayout2 = r3
            r1.captionLayout = r3
            java.util.ArrayList<android.text.StaticLayout> r0 = r1.linkLayout
            r0.clear()
            java.util.ArrayList<java.lang.String> r0 = r1.links
            r0.clear()
            int r0 = android.view.View.MeasureSpec.getSize(r29)
            int r4 = org.telegram.messenger.AndroidUtilities.leftBaseline
            float r4 = (float) r4
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r0 = r0 - r4
            r4 = 1090519040(0x41000000, float:8.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r5 = r0 - r5
            org.telegram.messenger.MessageObject r0 = r1.message
            org.telegram.tgnet.TLRPC$Message r6 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r6 = r6.media
            boolean r7 = r6 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaWebPage
            r14 = 1
            if (r7 == 0) goto L_0x0061
            org.telegram.tgnet.TLRPC$WebPage r6 = r6.webpage
            boolean r7 = r6 instanceof org.telegram.tgnet.TLRPC$TL_webPage
            if (r7 == 0) goto L_0x0061
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r7 = r0.photoThumbs
            if (r7 != 0) goto L_0x0048
            org.telegram.tgnet.TLRPC$Photo r7 = r6.photo
            if (r7 == 0) goto L_0x0048
            r0.generateThumbs(r14)
        L_0x0048:
            org.telegram.tgnet.TLRPC$Photo r0 = r6.photo
            if (r0 == 0) goto L_0x0054
            org.telegram.messenger.MessageObject r0 = r1.message
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r0 = r0.photoThumbs
            if (r0 == 0) goto L_0x0054
            r0 = 1
            goto L_0x0055
        L_0x0054:
            r0 = 0
        L_0x0055:
            java.lang.String r7 = r6.title
            if (r7 != 0) goto L_0x005b
            java.lang.String r7 = r6.site_name
        L_0x005b:
            java.lang.String r8 = r6.description
            java.lang.String r6 = r6.url
            r15 = r0
            goto L_0x0065
        L_0x0061:
            r6 = r3
            r7 = r6
            r8 = r7
            r15 = 0
        L_0x0065:
            org.telegram.messenger.MessageObject r0 = r1.message
            if (r0 == 0) goto L_0x0223
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r0 = r0.entities
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x0223
            r0 = r3
            r9 = 0
        L_0x0075:
            org.telegram.messenger.MessageObject r10 = r1.message
            org.telegram.tgnet.TLRPC$Message r10 = r10.messageOwner
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r10 = r10.entities
            int r10 = r10.size()
            if (r9 >= r10) goto L_0x0221
            org.telegram.messenger.MessageObject r10 = r1.message
            org.telegram.tgnet.TLRPC$Message r10 = r10.messageOwner
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r10 = r10.entities
            java.lang.Object r10 = r10.get(r9)
            org.telegram.tgnet.TLRPC$MessageEntity r10 = (org.telegram.tgnet.TLRPC$MessageEntity) r10
            int r11 = r10.length
            if (r11 <= 0) goto L_0x021c
            int r11 = r10.offset
            if (r11 < 0) goto L_0x021c
            org.telegram.messenger.MessageObject r12 = r1.message
            org.telegram.tgnet.TLRPC$Message r12 = r12.messageOwner
            java.lang.String r12 = r12.message
            int r12 = r12.length()
            if (r11 < r12) goto L_0x00a3
            goto L_0x021c
        L_0x00a3:
            int r11 = r10.offset
            int r12 = r10.length
            int r11 = r11 + r12
            org.telegram.messenger.MessageObject r12 = r1.message
            org.telegram.tgnet.TLRPC$Message r12 = r12.messageOwner
            java.lang.String r12 = r12.message
            int r12 = r12.length()
            if (r11 <= r12) goto L_0x00c3
            org.telegram.messenger.MessageObject r11 = r1.message
            org.telegram.tgnet.TLRPC$Message r11 = r11.messageOwner
            java.lang.String r11 = r11.message
            int r11 = r11.length()
            int r12 = r10.offset
            int r11 = r11 - r12
            r10.length = r11
        L_0x00c3:
            if (r9 != 0) goto L_0x00f4
            if (r6 == 0) goto L_0x00f4
            int r11 = r10.offset
            if (r11 != 0) goto L_0x00d9
            int r11 = r10.length
            org.telegram.messenger.MessageObject r12 = r1.message
            org.telegram.tgnet.TLRPC$Message r12 = r12.messageOwner
            java.lang.String r12 = r12.message
            int r12 = r12.length()
            if (r11 == r12) goto L_0x00f4
        L_0x00d9:
            org.telegram.messenger.MessageObject r11 = r1.message
            org.telegram.tgnet.TLRPC$Message r11 = r11.messageOwner
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r11 = r11.entities
            int r11 = r11.size()
            if (r11 != r14) goto L_0x00ee
            if (r8 != 0) goto L_0x00f4
            org.telegram.messenger.MessageObject r0 = r1.message
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            goto L_0x00f4
        L_0x00ee:
            org.telegram.messenger.MessageObject r0 = r1.message
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
        L_0x00f4:
            r11 = r0
            boolean r0 = r10 instanceof org.telegram.tgnet.TLRPC$TL_messageEntityTextUrl     // Catch:{ Exception -> 0x0217 }
            if (r0 != 0) goto L_0x0158
            boolean r0 = r10 instanceof org.telegram.tgnet.TLRPC$TL_messageEntityUrl     // Catch:{ Exception -> 0x0217 }
            if (r0 == 0) goto L_0x00fe
            goto L_0x0158
        L_0x00fe:
            boolean r0 = r10 instanceof org.telegram.tgnet.TLRPC$TL_messageEntityEmail     // Catch:{ Exception -> 0x0217 }
            if (r0 == 0) goto L_0x0155
            if (r7 == 0) goto L_0x010a
            int r0 = r7.length()     // Catch:{ Exception -> 0x0217 }
            if (r0 != 0) goto L_0x0155
        L_0x010a:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0217 }
            r0.<init>()     // Catch:{ Exception -> 0x0217 }
            java.lang.String r12 = "mailto:"
            r0.append(r12)     // Catch:{ Exception -> 0x0217 }
            org.telegram.messenger.MessageObject r12 = r1.message     // Catch:{ Exception -> 0x0217 }
            org.telegram.tgnet.TLRPC$Message r12 = r12.messageOwner     // Catch:{ Exception -> 0x0217 }
            java.lang.String r12 = r12.message     // Catch:{ Exception -> 0x0217 }
            int r13 = r10.offset     // Catch:{ Exception -> 0x0217 }
            int r3 = r10.length     // Catch:{ Exception -> 0x0217 }
            int r3 = r3 + r13
            java.lang.String r3 = r12.substring(r13, r3)     // Catch:{ Exception -> 0x0217 }
            r0.append(r3)     // Catch:{ Exception -> 0x0217 }
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x0217 }
            org.telegram.messenger.MessageObject r3 = r1.message     // Catch:{ Exception -> 0x0217 }
            org.telegram.tgnet.TLRPC$Message r3 = r3.messageOwner     // Catch:{ Exception -> 0x0217 }
            java.lang.String r3 = r3.message     // Catch:{ Exception -> 0x0217 }
            int r12 = r10.offset     // Catch:{ Exception -> 0x0217 }
            int r13 = r10.length     // Catch:{ Exception -> 0x0217 }
            int r13 = r13 + r12
            java.lang.String r3 = r3.substring(r12, r13)     // Catch:{ Exception -> 0x0217 }
            int r7 = r10.offset     // Catch:{ Exception -> 0x0214 }
            if (r7 != 0) goto L_0x014b
            int r7 = r10.length     // Catch:{ Exception -> 0x0214 }
            org.telegram.messenger.MessageObject r10 = r1.message     // Catch:{ Exception -> 0x0214 }
            org.telegram.tgnet.TLRPC$Message r10 = r10.messageOwner     // Catch:{ Exception -> 0x0214 }
            java.lang.String r10 = r10.message     // Catch:{ Exception -> 0x0214 }
            int r10 = r10.length()     // Catch:{ Exception -> 0x0214 }
            if (r7 == r10) goto L_0x0152
        L_0x014b:
            org.telegram.messenger.MessageObject r7 = r1.message     // Catch:{ Exception -> 0x0214 }
            org.telegram.tgnet.TLRPC$Message r7 = r7.messageOwner     // Catch:{ Exception -> 0x0214 }
            java.lang.String r7 = r7.message     // Catch:{ Exception -> 0x0214 }
            r8 = r7
        L_0x0152:
            r7 = r3
            goto L_0x01d5
        L_0x0155:
            r0 = 0
            goto L_0x01d5
        L_0x0158:
            boolean r0 = r10 instanceof org.telegram.tgnet.TLRPC$TL_messageEntityUrl     // Catch:{ Exception -> 0x0217 }
            if (r0 == 0) goto L_0x016c
            org.telegram.messenger.MessageObject r0 = r1.message     // Catch:{ Exception -> 0x0217 }
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner     // Catch:{ Exception -> 0x0217 }
            java.lang.String r0 = r0.message     // Catch:{ Exception -> 0x0217 }
            int r3 = r10.offset     // Catch:{ Exception -> 0x0217 }
            int r12 = r10.length     // Catch:{ Exception -> 0x0217 }
            int r12 = r12 + r3
            java.lang.String r0 = r0.substring(r3, r12)     // Catch:{ Exception -> 0x0217 }
            goto L_0x016e
        L_0x016c:
            java.lang.String r0 = r10.url     // Catch:{ Exception -> 0x0217 }
        L_0x016e:
            r3 = r0
            if (r7 == 0) goto L_0x017a
            int r0 = r7.length()     // Catch:{ Exception -> 0x0217 }
            if (r0 != 0) goto L_0x0178
            goto L_0x017a
        L_0x0178:
            r0 = r3
            goto L_0x01d5
        L_0x017a:
            android.net.Uri r0 = android.net.Uri.parse(r3)     // Catch:{ Exception -> 0x0214 }
            java.lang.String r0 = r0.getHost()     // Catch:{ Exception -> 0x0214 }
            if (r0 != 0) goto L_0x0186
            r7 = r3
            goto L_0x0187
        L_0x0186:
            r7 = r0
        L_0x0187:
            if (r7 == 0) goto L_0x01bc
            r0 = 46
            int r12 = r7.lastIndexOf(r0)     // Catch:{ Exception -> 0x0217 }
            if (r12 < 0) goto L_0x01bc
            java.lang.String r7 = r7.substring(r2, r12)     // Catch:{ Exception -> 0x0217 }
            int r0 = r7.lastIndexOf(r0)     // Catch:{ Exception -> 0x0217 }
            if (r0 < 0) goto L_0x01a1
            int r0 = r0 + 1
            java.lang.String r7 = r7.substring(r0)     // Catch:{ Exception -> 0x0217 }
        L_0x01a1:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0217 }
            r0.<init>()     // Catch:{ Exception -> 0x0217 }
            java.lang.String r12 = r7.substring(r2, r14)     // Catch:{ Exception -> 0x0217 }
            java.lang.String r12 = r12.toUpperCase()     // Catch:{ Exception -> 0x0217 }
            r0.append(r12)     // Catch:{ Exception -> 0x0217 }
            java.lang.String r12 = r7.substring(r14)     // Catch:{ Exception -> 0x0217 }
            r0.append(r12)     // Catch:{ Exception -> 0x0217 }
            java.lang.String r7 = r0.toString()     // Catch:{ Exception -> 0x0217 }
        L_0x01bc:
            int r0 = r10.offset     // Catch:{ Exception -> 0x0217 }
            if (r0 != 0) goto L_0x01ce
            int r0 = r10.length     // Catch:{ Exception -> 0x0217 }
            org.telegram.messenger.MessageObject r10 = r1.message     // Catch:{ Exception -> 0x0217 }
            org.telegram.tgnet.TLRPC$Message r10 = r10.messageOwner     // Catch:{ Exception -> 0x0217 }
            java.lang.String r10 = r10.message     // Catch:{ Exception -> 0x0217 }
            int r10 = r10.length()     // Catch:{ Exception -> 0x0217 }
            if (r0 == r10) goto L_0x0178
        L_0x01ce:
            org.telegram.messenger.MessageObject r0 = r1.message     // Catch:{ Exception -> 0x0217 }
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner     // Catch:{ Exception -> 0x0217 }
            java.lang.String r8 = r0.message     // Catch:{ Exception -> 0x0217 }
            goto L_0x0178
        L_0x01d5:
            if (r0 == 0) goto L_0x021b
            java.lang.String r3 = "://"
            boolean r3 = r0.contains(r3)     // Catch:{ Exception -> 0x0217 }
            if (r3 != 0) goto L_0x020e
            java.lang.String r3 = r0.toLowerCase()     // Catch:{ Exception -> 0x0217 }
            java.lang.String r10 = "http"
            int r3 = r3.indexOf(r10)     // Catch:{ Exception -> 0x0217 }
            if (r3 == 0) goto L_0x020e
            java.lang.String r3 = r0.toLowerCase()     // Catch:{ Exception -> 0x0217 }
            java.lang.String r10 = "mailto"
            int r3 = r3.indexOf(r10)     // Catch:{ Exception -> 0x0217 }
            if (r3 == 0) goto L_0x020e
            java.util.ArrayList<java.lang.String> r3 = r1.links     // Catch:{ Exception -> 0x0217 }
            java.lang.StringBuilder r10 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0217 }
            r10.<init>()     // Catch:{ Exception -> 0x0217 }
            java.lang.String r12 = "http://"
            r10.append(r12)     // Catch:{ Exception -> 0x0217 }
            r10.append(r0)     // Catch:{ Exception -> 0x0217 }
            java.lang.String r0 = r10.toString()     // Catch:{ Exception -> 0x0217 }
            r3.add(r0)     // Catch:{ Exception -> 0x0217 }
            goto L_0x021b
        L_0x020e:
            java.util.ArrayList<java.lang.String> r3 = r1.links     // Catch:{ Exception -> 0x0217 }
            r3.add(r0)     // Catch:{ Exception -> 0x0217 }
            goto L_0x021b
        L_0x0214:
            r0 = move-exception
            r7 = r3
            goto L_0x0218
        L_0x0217:
            r0 = move-exception
        L_0x0218:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x021b:
            r0 = r11
        L_0x021c:
            int r9 = r9 + 1
            r3 = 0
            goto L_0x0075
        L_0x0221:
            r3 = r0
            goto L_0x0224
        L_0x0223:
            r3 = 0
        L_0x0224:
            if (r6 == 0) goto L_0x0233
            java.util.ArrayList<java.lang.String> r0 = r1.links
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L_0x0233
            java.util.ArrayList<java.lang.String> r0 = r1.links
            r0.add(r6)
        L_0x0233:
            int r0 = r1.viewType
            if (r0 != r14) goto L_0x0273
            org.telegram.messenger.MessageObject r0 = r1.message
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            int r0 = r0.date
            long r9 = (long) r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.stringForMessageListDate(r9)
            android.text.TextPaint r6 = r1.description2TextPaint
            float r6 = r6.measureText(r0)
            double r9 = (double) r6
            double r9 = java.lang.Math.ceil(r9)
            int r6 = (int) r9
            android.text.TextPaint r9 = r1.description2TextPaint
            r20 = 0
            r21 = 1
            r16 = r0
            r17 = r9
            r18 = r6
            r19 = r6
            android.text.StaticLayout r0 = org.telegram.ui.Cells.ChatMessageCell.generateStaticLayout(r16, r17, r18, r19, r20, r21)
            r1.dateLayout = r0
            int r0 = r5 - r6
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r0 = r0 - r4
            r1.dateLayoutX = r0
            r0 = 1094713344(0x41400000, float:12.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            int r6 = r6 + r0
            goto L_0x0274
        L_0x0273:
            r6 = 0
        L_0x0274:
            r4 = 1082130432(0x40800000, float:4.0)
            if (r7 == 0) goto L_0x02c8
            org.telegram.messenger.MessageObject r0 = r1.message     // Catch:{ Exception -> 0x02bf }
            java.util.ArrayList<java.lang.String> r0 = r0.highlightedWords     // Catch:{ Exception -> 0x02bf }
            java.lang.CharSequence r0 = org.telegram.messenger.AndroidUtilities.highlightText((java.lang.CharSequence) r7, (java.util.ArrayList<java.lang.String>) r0)     // Catch:{ Exception -> 0x02bf }
            if (r0 == 0) goto L_0x0285
            r16 = r0
            goto L_0x0287
        L_0x0285:
            r16 = r7
        L_0x0287:
            android.text.TextPaint r0 = r1.titleTextPaint     // Catch:{ Exception -> 0x02bf }
            int r6 = r5 - r6
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r4)     // Catch:{ Exception -> 0x02bf }
            int r18 = r6 - r9
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r4)     // Catch:{ Exception -> 0x02bf }
            int r19 = r6 - r9
            r20 = 0
            r21 = 3
            r17 = r0
            android.text.StaticLayout r0 = org.telegram.ui.Cells.ChatMessageCell.generateStaticLayout(r16, r17, r18, r19, r20, r21)     // Catch:{ Exception -> 0x02bf }
            r1.titleLayout = r0     // Catch:{ Exception -> 0x02bf }
            int r0 = r0.getLineCount()     // Catch:{ Exception -> 0x02bf }
            if (r0 <= 0) goto L_0x02c3
            int r0 = r1.titleY     // Catch:{ Exception -> 0x02bf }
            android.text.StaticLayout r6 = r1.titleLayout     // Catch:{ Exception -> 0x02bf }
            int r9 = r6.getLineCount()     // Catch:{ Exception -> 0x02bf }
            int r9 = r9 - r14
            int r6 = r6.getLineBottom(r9)     // Catch:{ Exception -> 0x02bf }
            int r0 = r0 + r6
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r4)     // Catch:{ Exception -> 0x02bf }
            int r0 = r0 + r6
            r1.descriptionY = r0     // Catch:{ Exception -> 0x02bf }
            goto L_0x02c3
        L_0x02bf:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x02c3:
            org.telegram.ui.Components.LetterDrawable r0 = r1.letterDrawable
            r0.setTitle(r7)
        L_0x02c8:
            int r0 = r1.descriptionY
            r1.description2Y = r0
            android.text.StaticLayout r0 = r1.titleLayout
            if (r0 == 0) goto L_0x02d5
            int r0 = r0.getLineCount()
            goto L_0x02d6
        L_0x02d5:
            r0 = 0
        L_0x02d6:
            int r0 = 4 - r0
            int r16 = java.lang.Math.max(r14, r0)
            int r0 = r1.viewType
            if (r0 != r14) goto L_0x02e3
            r3 = 0
            r6 = 0
            goto L_0x02e4
        L_0x02e3:
            r6 = r8
        L_0x02e4:
            r17 = 1084227584(0x40a00000, float:5.0)
            if (r6 == 0) goto L_0x0315
            android.text.TextPaint r7 = r1.descriptionTextPaint     // Catch:{ Exception -> 0x0311 }
            r10 = 0
            r8 = r5
            r9 = r5
            r11 = r16
            android.text.StaticLayout r0 = org.telegram.ui.Cells.ChatMessageCell.generateStaticLayout(r6, r7, r8, r9, r10, r11)     // Catch:{ Exception -> 0x0311 }
            r1.descriptionLayout = r0     // Catch:{ Exception -> 0x0311 }
            int r0 = r0.getLineCount()     // Catch:{ Exception -> 0x0311 }
            if (r0 <= 0) goto L_0x0315
            int r0 = r1.descriptionY     // Catch:{ Exception -> 0x0311 }
            android.text.StaticLayout r6 = r1.descriptionLayout     // Catch:{ Exception -> 0x0311 }
            int r7 = r6.getLineCount()     // Catch:{ Exception -> 0x0311 }
            int r7 = r7 - r14
            int r6 = r6.getLineBottom(r7)     // Catch:{ Exception -> 0x0311 }
            int r0 = r0 + r6
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r17)     // Catch:{ Exception -> 0x0311 }
            int r0 = r0 + r6
            r1.description2Y = r0     // Catch:{ Exception -> 0x0311 }
            goto L_0x0315
        L_0x0311:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0315:
            r18 = 1092616192(0x41200000, float:10.0)
            if (r3 == 0) goto L_0x0339
            android.text.TextPaint r7 = r1.descriptionTextPaint     // Catch:{ Exception -> 0x0335 }
            r10 = 0
            r6 = r3
            r8 = r5
            r9 = r5
            r11 = r16
            android.text.StaticLayout r0 = org.telegram.ui.Cells.ChatMessageCell.generateStaticLayout(r6, r7, r8, r9, r10, r11)     // Catch:{ Exception -> 0x0335 }
            r1.descriptionLayout2 = r0     // Catch:{ Exception -> 0x0335 }
            android.text.StaticLayout r0 = r1.descriptionLayout     // Catch:{ Exception -> 0x0335 }
            if (r0 == 0) goto L_0x0339
            int r0 = r1.description2Y     // Catch:{ Exception -> 0x0335 }
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r18)     // Catch:{ Exception -> 0x0335 }
            int r0 = r0 + r3
            r1.description2Y = r0     // Catch:{ Exception -> 0x0335 }
            goto L_0x0339
        L_0x0335:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0339:
            org.telegram.messenger.MessageObject r0 = r1.message
            if (r0 == 0) goto L_0x03af
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x03af
            org.telegram.messenger.MessageObject r0 = r1.message
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            java.lang.String r3 = "\n"
            java.lang.String r6 = " "
            java.lang.String r0 = r0.replace(r3, r6)
            java.lang.String r3 = " +"
            java.lang.String r0 = r0.replaceAll(r3, r6)
            java.lang.String r0 = r0.trim()
            android.text.TextPaint r3 = org.telegram.ui.ActionBar.Theme.chat_msgTextPaint
            android.graphics.Paint$FontMetricsInt r3 = r3.getFontMetricsInt()
            r6 = 1101004800(0x41a00000, float:20.0)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            java.lang.CharSequence r0 = org.telegram.messenger.Emoji.replaceEmoji(r0, r3, r6, r2)
            org.telegram.messenger.MessageObject r3 = r1.message
            java.util.ArrayList<java.lang.String> r3 = r3.highlightedWords
            java.lang.CharSequence r0 = org.telegram.messenger.AndroidUtilities.highlightText((java.lang.CharSequence) r0, (java.util.ArrayList<java.lang.String>) r3)
            if (r0 == 0) goto L_0x03af
            org.telegram.messenger.MessageObject r3 = r1.message
            java.util.ArrayList<java.lang.String> r3 = r3.highlightedWords
            java.lang.Object r3 = r3.get(r2)
            java.lang.String r3 = (java.lang.String) r3
            android.text.TextPaint r6 = r1.captionTextPaint
            r7 = 130(0x82, float:1.82E-43)
            java.lang.CharSequence r0 = org.telegram.messenger.AndroidUtilities.ellipsizeCenterEnd(r0, r3, r5, r6, r7)
            android.text.TextPaint r3 = r1.captionTextPaint
            float r6 = (float) r5
            android.text.TextUtils$TruncateAt r7 = android.text.TextUtils.TruncateAt.END
            java.lang.CharSequence r20 = android.text.TextUtils.ellipsize(r0, r3, r6, r7)
            android.text.StaticLayout r0 = new android.text.StaticLayout
            android.text.TextPaint r3 = r1.captionTextPaint
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r22 = r5 + r6
            android.text.Layout$Alignment r23 = android.text.Layout.Alignment.ALIGN_NORMAL
            r24 = 1065353216(0x3var_, float:1.0)
            r25 = 0
            r26 = 0
            r19 = r0
            r21 = r3
            r19.<init>(r20, r21, r22, r23, r24, r25, r26)
            r1.captionLayout = r0
        L_0x03af:
            android.text.StaticLayout r0 = r1.captionLayout
            if (r0 == 0) goto L_0x03ca
            int r3 = r1.descriptionY
            r1.captionY = r3
            int r6 = r0.getLineCount()
            int r6 = r6 - r14
            int r0 = r0.getLineBottom(r6)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r17)
            int r0 = r0 + r6
            int r3 = r3 + r0
            r1.descriptionY = r3
            r1.description2Y = r3
        L_0x03ca:
            java.util.ArrayList<java.lang.String> r0 = r1.links
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x0443
            r3 = 0
        L_0x03d3:
            java.util.ArrayList<java.lang.String> r0 = r1.links
            int r0 = r0.size()
            if (r3 >= r0) goto L_0x0443
            java.util.ArrayList<java.lang.String> r0 = r1.links     // Catch:{ Exception -> 0x043c }
            java.lang.Object r0 = r0.get(r3)     // Catch:{ Exception -> 0x043c }
            java.lang.String r0 = (java.lang.String) r0     // Catch:{ Exception -> 0x043c }
            android.text.TextPaint r6 = r1.descriptionTextPaint     // Catch:{ Exception -> 0x043c }
            float r6 = r6.measureText(r0)     // Catch:{ Exception -> 0x043c }
            double r6 = (double) r6     // Catch:{ Exception -> 0x043c }
            double r6 = java.lang.Math.ceil(r6)     // Catch:{ Exception -> 0x043c }
            int r6 = (int) r6     // Catch:{ Exception -> 0x043c }
            r7 = 10
            r8 = 32
            java.lang.String r0 = r0.replace(r7, r8)     // Catch:{ Exception -> 0x043c }
            android.text.TextPaint r7 = r1.descriptionTextPaint     // Catch:{ Exception -> 0x043c }
            int r6 = java.lang.Math.min(r6, r5)     // Catch:{ Exception -> 0x043c }
            float r6 = (float) r6     // Catch:{ Exception -> 0x043c }
            android.text.TextUtils$TruncateAt r8 = android.text.TextUtils.TruncateAt.MIDDLE     // Catch:{ Exception -> 0x043c }
            java.lang.CharSequence r7 = android.text.TextUtils.ellipsize(r0, r7, r6, r8)     // Catch:{ Exception -> 0x043c }
            android.text.StaticLayout r0 = new android.text.StaticLayout     // Catch:{ Exception -> 0x043c }
            android.text.TextPaint r8 = r1.descriptionTextPaint     // Catch:{ Exception -> 0x043c }
            android.text.Layout$Alignment r10 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x043c }
            r11 = 1065353216(0x3var_, float:1.0)
            r12 = 0
            r13 = 0
            r6 = r0
            r9 = r5
            r6.<init>(r7, r8, r9, r10, r11, r12, r13)     // Catch:{ Exception -> 0x043c }
            int r6 = r1.description2Y     // Catch:{ Exception -> 0x043c }
            r1.linkY = r6     // Catch:{ Exception -> 0x043c }
            android.text.StaticLayout r6 = r1.descriptionLayout2     // Catch:{ Exception -> 0x043c }
            if (r6 == 0) goto L_0x0436
            int r6 = r6.getLineCount()     // Catch:{ Exception -> 0x043c }
            if (r6 == 0) goto L_0x0436
            int r6 = r1.linkY     // Catch:{ Exception -> 0x043c }
            android.text.StaticLayout r7 = r1.descriptionLayout2     // Catch:{ Exception -> 0x043c }
            int r8 = r7.getLineCount()     // Catch:{ Exception -> 0x043c }
            int r8 = r8 - r14
            int r7 = r7.getLineBottom(r8)     // Catch:{ Exception -> 0x043c }
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r17)     // Catch:{ Exception -> 0x043c }
            int r7 = r7 + r8
            int r6 = r6 + r7
            r1.linkY = r6     // Catch:{ Exception -> 0x043c }
        L_0x0436:
            java.util.ArrayList<android.text.StaticLayout> r6 = r1.linkLayout     // Catch:{ Exception -> 0x043c }
            r6.add(r0)     // Catch:{ Exception -> 0x043c }
            goto L_0x0440
        L_0x043c:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0440:
            int r3 = r3 + 1
            goto L_0x03d3
        L_0x0443:
            r0 = 1112539136(0x42500000, float:52.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            boolean r3 = org.telegram.messenger.LocaleController.isRTL
            if (r3 == 0) goto L_0x0458
            int r3 = android.view.View.MeasureSpec.getSize(r29)
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r18)
            int r3 = r3 - r6
            int r3 = r3 - r0
            goto L_0x045c
        L_0x0458:
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r18)
        L_0x045c:
            org.telegram.ui.Components.LetterDrawable r6 = r1.letterDrawable
            r7 = 1093664768(0x41300000, float:11.0)
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r7)
            int r9 = r3 + r0
            r10 = 1115422720(0x427CLASSNAME, float:63.0)
            int r10 = org.telegram.messenger.AndroidUtilities.dp(r10)
            r6.setBounds(r3, r8, r9, r10)
            if (r15 == 0) goto L_0x04ea
            org.telegram.messenger.MessageObject r6 = r1.message
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r6 = r6.photoThumbs
            org.telegram.tgnet.TLRPC$PhotoSize r6 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r6, r0, r14)
            org.telegram.messenger.MessageObject r8 = r1.message
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r8 = r8.photoThumbs
            r9 = 80
            org.telegram.tgnet.TLRPC$PhotoSize r8 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r8, r9)
            if (r8 != r6) goto L_0x0486
            r8 = 0
        L_0x0486:
            r9 = -1
            r6.size = r9
            if (r8 == 0) goto L_0x048d
            r8.size = r9
        L_0x048d:
            org.telegram.messenger.ImageReceiver r9 = r1.linkImageView
            float r3 = (float) r3
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            float r7 = (float) r7
            float r10 = (float) r0
            r9.setImageCoords(r3, r7, r10, r10)
            org.telegram.messenger.FileLoader.getAttachFileName(r6)
            java.util.Locale r3 = java.util.Locale.US
            r7 = 2
            java.lang.Object[] r9 = new java.lang.Object[r7]
            java.lang.Integer r10 = java.lang.Integer.valueOf(r0)
            r9[r2] = r10
            java.lang.Integer r10 = java.lang.Integer.valueOf(r0)
            r9[r14] = r10
            java.lang.String r10 = "%d_%d"
            java.lang.String r21 = java.lang.String.format(r3, r10, r9)
            java.lang.Object[] r7 = new java.lang.Object[r7]
            java.lang.Integer r9 = java.lang.Integer.valueOf(r0)
            r7[r2] = r9
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            r7[r14] = r0
            java.lang.String r0 = "%d_%d_b"
            java.lang.String r23 = java.lang.String.format(r3, r0, r7)
            org.telegram.messenger.ImageReceiver r0 = r1.linkImageView
            org.telegram.messenger.MessageObject r3 = r1.message
            org.telegram.tgnet.TLObject r3 = r3.photoThumbsObject
            org.telegram.messenger.ImageLocation r20 = org.telegram.messenger.ImageLocation.getForObject(r6, r3)
            org.telegram.messenger.MessageObject r3 = r1.message
            org.telegram.tgnet.TLObject r3 = r3.photoThumbsObject
            org.telegram.messenger.ImageLocation r22 = org.telegram.messenger.ImageLocation.getForObject(r8, r3)
            r24 = 0
            r25 = 0
            org.telegram.messenger.MessageObject r3 = r1.message
            r27 = 0
            r19 = r0
            r26 = r3
            r19.setImage(r20, r21, r22, r23, r24, r25, r26, r27)
            r1.drawLinkImageView = r14
        L_0x04ea:
            int r0 = r1.viewType
            if (r0 != r14) goto L_0x0501
            org.telegram.messenger.MessageObject r0 = r1.message
            java.lang.CharSequence r6 = org.telegram.ui.FilteredSearchView.createFromInfoString(r0)
            android.text.TextPaint r7 = r1.description2TextPaint
            r10 = 0
            r8 = r5
            r9 = r5
            r11 = r16
            android.text.StaticLayout r0 = org.telegram.ui.Cells.ChatMessageCell.generateStaticLayout(r6, r7, r8, r9, r10, r11)
            r1.fromInfoLayout = r0
        L_0x0501:
            android.text.StaticLayout r0 = r1.titleLayout
            if (r0 == 0) goto L_0x051d
            int r0 = r0.getLineCount()
            if (r0 == 0) goto L_0x051d
            android.text.StaticLayout r0 = r1.titleLayout
            int r3 = r0.getLineCount()
            int r3 = r3 - r14
            int r0 = r0.getLineBottom(r3)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r0 = r0 + r3
            int r0 = r0 + r2
            goto L_0x051e
        L_0x051d:
            r0 = 0
        L_0x051e:
            android.text.StaticLayout r3 = r1.captionLayout
            if (r3 == 0) goto L_0x0539
            int r3 = r3.getLineCount()
            if (r3 == 0) goto L_0x0539
            android.text.StaticLayout r3 = r1.captionLayout
            int r4 = r3.getLineCount()
            int r4 = r4 - r14
            int r3 = r3.getLineBottom(r4)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r17)
            int r3 = r3 + r4
            int r0 = r0 + r3
        L_0x0539:
            android.text.StaticLayout r3 = r1.descriptionLayout
            if (r3 == 0) goto L_0x0554
            int r3 = r3.getLineCount()
            if (r3 == 0) goto L_0x0554
            android.text.StaticLayout r3 = r1.descriptionLayout
            int r4 = r3.getLineCount()
            int r4 = r4 - r14
            int r3 = r3.getLineBottom(r4)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r17)
            int r3 = r3 + r4
            int r0 = r0 + r3
        L_0x0554:
            android.text.StaticLayout r3 = r1.descriptionLayout2
            if (r3 == 0) goto L_0x0578
            int r3 = r3.getLineCount()
            if (r3 == 0) goto L_0x0578
            android.text.StaticLayout r3 = r1.descriptionLayout2
            int r4 = r3.getLineCount()
            int r4 = r4 - r14
            int r3 = r3.getLineBottom(r4)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r17)
            int r3 = r3 + r4
            int r0 = r0 + r3
            android.text.StaticLayout r3 = r1.descriptionLayout
            if (r3 == 0) goto L_0x0578
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r18)
            int r0 = r0 + r3
        L_0x0578:
            r3 = 0
        L_0x0579:
            java.util.ArrayList<android.text.StaticLayout> r4 = r1.linkLayout
            int r4 = r4.size()
            if (r2 >= r4) goto L_0x059c
            java.util.ArrayList<android.text.StaticLayout> r4 = r1.linkLayout
            java.lang.Object r4 = r4.get(r2)
            android.text.StaticLayout r4 = (android.text.StaticLayout) r4
            int r5 = r4.getLineCount()
            if (r5 <= 0) goto L_0x0599
            int r5 = r4.getLineCount()
            int r5 = r5 - r14
            int r4 = r4.getLineBottom(r5)
            int r3 = r3 + r4
        L_0x0599:
            int r2 = r2 + 1
            goto L_0x0579
        L_0x059c:
            int r0 = r0 + r3
            android.text.StaticLayout r2 = r1.fromInfoLayout
            if (r2 == 0) goto L_0x05bc
            int r2 = r1.linkY
            int r2 = r2 + r3
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r17)
            int r2 = r2 + r3
            r1.fromInfoLayoutY = r2
            android.text.StaticLayout r2 = r1.fromInfoLayout
            int r3 = r2.getLineCount()
            int r3 = r3 - r14
            int r2 = r2.getLineBottom(r3)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r17)
            int r2 = r2 + r3
            int r0 = r0 + r2
        L_0x05bc:
            org.telegram.ui.Components.CheckBox2 r2 = r1.checkBox
            r3 = 1103101952(0x41CLASSNAME, float:24.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r5 = 1073741824(0x40000000, float:2.0)
            int r4 = android.view.View.MeasureSpec.makeMeasureSpec(r4, r5)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r3 = android.view.View.MeasureSpec.makeMeasureSpec(r3, r5)
            r2.measure(r4, r3)
            int r2 = android.view.View.MeasureSpec.getSize(r29)
            r3 = 1117257728(0x42980000, float:76.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r4 = 1099431936(0x41880000, float:17.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r0 = r0 + r4
            int r0 = java.lang.Math.max(r3, r0)
            boolean r3 = r1.needDivider
            int r0 = r0 + r3
            r1.setMeasuredDimension(r2, r0)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.SharedLinkCell.onMeasure(int, int):void");
    }

    public void setLink(MessageObject messageObject, boolean z) {
        this.needDivider = z;
        resetPressedLink();
        this.message = messageObject;
        requestLayout();
    }

    public void setDelegate(SharedLinkCellDelegate sharedLinkCellDelegate) {
        this.delegate = sharedLinkCellDelegate;
    }

    public MessageObject getMessage() {
        return this.message;
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.drawLinkImageView) {
            this.linkImageView.onDetachedFromWindow();
        }
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.drawLinkImageView) {
            this.linkImageView.onAttachedToWindow();
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:65:0x00fd, code lost:
        r3 = false;
     */
    /* JADX WARNING: Removed duplicated region for block: B:73:0x0111 A[ORIG_RETURN, RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:79:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onTouchEvent(android.view.MotionEvent r12) {
        /*
            r11 = this;
            org.telegram.messenger.MessageObject r0 = r11.message
            r1 = 1
            r2 = 0
            if (r0 == 0) goto L_0x0104
            java.util.ArrayList<android.text.StaticLayout> r0 = r11.linkLayout
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x0104
            org.telegram.ui.Cells.SharedLinkCell$SharedLinkCellDelegate r0 = r11.delegate
            if (r0 == 0) goto L_0x0104
            boolean r0 = r0.canPerformActions()
            if (r0 == 0) goto L_0x0104
            int r0 = r12.getAction()
            if (r0 == 0) goto L_0x0035
            boolean r0 = r11.linkPreviewPressed
            if (r0 == 0) goto L_0x0029
            int r0 = r12.getAction()
            if (r0 != r1) goto L_0x0029
            goto L_0x0035
        L_0x0029:
            int r0 = r12.getAction()
            r3 = 3
            if (r0 != r3) goto L_0x0107
            r11.resetPressedLink()
            goto L_0x0107
        L_0x0035:
            float r0 = r12.getX()
            int r0 = (int) r0
            float r3 = r12.getY()
            int r3 = (int) r3
            r4 = 0
            r5 = 0
        L_0x0041:
            java.util.ArrayList<android.text.StaticLayout> r6 = r11.linkLayout
            int r6 = r6.size()
            if (r4 >= r6) goto L_0x00fc
            java.util.ArrayList<android.text.StaticLayout> r6 = r11.linkLayout
            java.lang.Object r6 = r6.get(r4)
            android.text.StaticLayout r6 = (android.text.StaticLayout) r6
            int r7 = r6.getLineCount()
            if (r7 <= 0) goto L_0x00f8
            int r7 = r6.getLineCount()
            int r7 = r7 - r1
            int r7 = r6.getLineBottom(r7)
            boolean r8 = org.telegram.messenger.LocaleController.isRTL
            if (r8 == 0) goto L_0x0067
            r8 = 1090519040(0x41000000, float:8.0)
            goto L_0x006a
        L_0x0067:
            int r8 = org.telegram.messenger.AndroidUtilities.leftBaseline
            float r8 = (float) r8
        L_0x006a:
            int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
            float r9 = (float) r0
            float r8 = (float) r8
            float r10 = r6.getLineLeft(r2)
            float r10 = r10 + r8
            int r10 = (r9 > r10 ? 1 : (r9 == r10 ? 0 : -1))
            if (r10 < 0) goto L_0x00f7
            float r10 = r6.getLineWidth(r2)
            float r8 = r8 + r10
            int r8 = (r9 > r8 ? 1 : (r9 == r8 ? 0 : -1))
            if (r8 > 0) goto L_0x00f7
            int r8 = r11.linkY
            int r9 = r8 + r5
            if (r3 < r9) goto L_0x00f7
            int r8 = r8 + r5
            int r8 = r8 + r7
            if (r3 > r8) goto L_0x00f7
            int r0 = r12.getAction()
            if (r0 != 0) goto L_0x00b5
            r11.resetPressedLink()
            r11.pressedLink = r4
            r11.linkPreviewPressed = r1
            r11.startCheckLongPress()
            org.telegram.ui.Components.LinkPath r0 = r11.urlPath     // Catch:{ Exception -> 0x00b0 }
            r3 = 0
            r0.setCurrentLayout(r6, r2, r3)     // Catch:{ Exception -> 0x00b0 }
            java.lang.CharSequence r0 = r6.getText()     // Catch:{ Exception -> 0x00b0 }
            int r0 = r0.length()     // Catch:{ Exception -> 0x00b0 }
            org.telegram.ui.Components.LinkPath r3 = r11.urlPath     // Catch:{ Exception -> 0x00b0 }
            r6.getSelectionPath(r2, r0, r3)     // Catch:{ Exception -> 0x00b0 }
            goto L_0x00f2
        L_0x00b0:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x00f2
        L_0x00b5:
            boolean r0 = r11.linkPreviewPressed
            if (r0 == 0) goto L_0x00f5
            int r0 = r11.pressedLink     // Catch:{ Exception -> 0x00eb }
            if (r0 != 0) goto L_0x00c8
            org.telegram.messenger.MessageObject r0 = r11.message     // Catch:{ Exception -> 0x00eb }
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner     // Catch:{ Exception -> 0x00eb }
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media     // Catch:{ Exception -> 0x00eb }
            if (r0 == 0) goto L_0x00c8
            org.telegram.tgnet.TLRPC$WebPage r0 = r0.webpage     // Catch:{ Exception -> 0x00eb }
            goto L_0x00c9
        L_0x00c8:
            r0 = 0
        L_0x00c9:
            if (r0 == 0) goto L_0x00db
            java.lang.String r3 = r0.embed_url     // Catch:{ Exception -> 0x00eb }
            if (r3 == 0) goto L_0x00db
            int r3 = r3.length()     // Catch:{ Exception -> 0x00eb }
            if (r3 == 0) goto L_0x00db
            org.telegram.ui.Cells.SharedLinkCell$SharedLinkCellDelegate r3 = r11.delegate     // Catch:{ Exception -> 0x00eb }
            r3.needOpenWebView(r0)     // Catch:{ Exception -> 0x00eb }
            goto L_0x00ef
        L_0x00db:
            org.telegram.ui.Cells.SharedLinkCell$SharedLinkCellDelegate r0 = r11.delegate     // Catch:{ Exception -> 0x00eb }
            java.util.ArrayList<java.lang.String> r3 = r11.links     // Catch:{ Exception -> 0x00eb }
            int r4 = r11.pressedLink     // Catch:{ Exception -> 0x00eb }
            java.lang.Object r3 = r3.get(r4)     // Catch:{ Exception -> 0x00eb }
            java.lang.String r3 = (java.lang.String) r3     // Catch:{ Exception -> 0x00eb }
            r0.onLinkPress(r3, r2)     // Catch:{ Exception -> 0x00eb }
            goto L_0x00ef
        L_0x00eb:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x00ef:
            r11.resetPressedLink()
        L_0x00f2:
            r0 = 1
            r3 = 1
            goto L_0x00fe
        L_0x00f5:
            r0 = 1
            goto L_0x00fd
        L_0x00f7:
            int r5 = r5 + r7
        L_0x00f8:
            int r4 = r4 + 1
            goto L_0x0041
        L_0x00fc:
            r0 = 0
        L_0x00fd:
            r3 = 0
        L_0x00fe:
            if (r0 != 0) goto L_0x0108
            r11.resetPressedLink()
            goto L_0x0108
        L_0x0104:
            r11.resetPressedLink()
        L_0x0107:
            r3 = 0
        L_0x0108:
            if (r3 != 0) goto L_0x0112
            boolean r12 = super.onTouchEvent(r12)
            if (r12 == 0) goto L_0x0111
            goto L_0x0112
        L_0x0111:
            r1 = 0
        L_0x0112:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.SharedLinkCell.onTouchEvent(android.view.MotionEvent):boolean");
    }

    public String getLink(int i) {
        if (i < 0 || i >= this.links.size()) {
            return null;
        }
        return this.links.get(i);
    }

    /* access modifiers changed from: protected */
    public void resetPressedLink() {
        this.pressedLink = -1;
        this.linkPreviewPressed = false;
        cancelCheckLongPress();
        invalidate();
    }

    public void setChecked(boolean z, boolean z2) {
        if (this.checkBox.getVisibility() != 0) {
            this.checkBox.setVisibility(0);
        }
        this.checkBox.setChecked(z, z2);
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        if (this.viewType == 1) {
            this.description2TextPaint.setColor(Theme.getColor("windowBackgroundWhiteGrayText3"));
        }
        float f = 8.0f;
        if (this.dateLayout != null) {
            canvas.save();
            canvas.translate((float) (AndroidUtilities.dp(LocaleController.isRTL ? 8.0f : (float) AndroidUtilities.leftBaseline) + (LocaleController.isRTL ? 0 : this.dateLayoutX)), (float) this.titleY);
            this.dateLayout.draw(canvas);
            canvas.restore();
        }
        if (this.titleLayout != null) {
            canvas.save();
            float dp = (float) AndroidUtilities.dp(LocaleController.isRTL ? 8.0f : (float) AndroidUtilities.leftBaseline);
            if (LocaleController.isRTL) {
                StaticLayout staticLayout = this.dateLayout;
                dp += staticLayout == null ? 0.0f : (float) (staticLayout.getWidth() + AndroidUtilities.dp(4.0f));
            }
            canvas.translate(dp, (float) this.titleY);
            this.titleLayout.draw(canvas);
            canvas.restore();
        }
        if (this.captionLayout != null) {
            this.captionTextPaint.setColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            canvas.save();
            canvas.translate((float) AndroidUtilities.dp(LocaleController.isRTL ? 8.0f : (float) AndroidUtilities.leftBaseline), (float) this.captionY);
            this.captionLayout.draw(canvas);
            canvas.restore();
        }
        if (this.descriptionLayout != null) {
            this.descriptionTextPaint.setColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            canvas.save();
            canvas.translate((float) AndroidUtilities.dp(LocaleController.isRTL ? 8.0f : (float) AndroidUtilities.leftBaseline), (float) this.descriptionY);
            this.descriptionLayout.draw(canvas);
            canvas.restore();
        }
        if (this.descriptionLayout2 != null) {
            this.descriptionTextPaint.setColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            canvas.save();
            canvas.translate((float) AndroidUtilities.dp(LocaleController.isRTL ? 8.0f : (float) AndroidUtilities.leftBaseline), (float) this.description2Y);
            this.descriptionLayout2.draw(canvas);
            canvas.restore();
        }
        if (!this.linkLayout.isEmpty()) {
            this.descriptionTextPaint.setColor(Theme.getColor("windowBackgroundWhiteLinkText"));
            int i = 0;
            for (int i2 = 0; i2 < this.linkLayout.size(); i2++) {
                StaticLayout staticLayout2 = this.linkLayout.get(i2);
                if (staticLayout2.getLineCount() > 0) {
                    canvas.save();
                    canvas.translate((float) AndroidUtilities.dp(LocaleController.isRTL ? 8.0f : (float) AndroidUtilities.leftBaseline), (float) (this.linkY + i));
                    if (this.pressedLink == i2) {
                        canvas.drawPath(this.urlPath, Theme.linkSelectionPaint);
                    }
                    staticLayout2.draw(canvas);
                    canvas.restore();
                    i += staticLayout2.getLineBottom(staticLayout2.getLineCount() - 1);
                }
            }
        }
        if (this.fromInfoLayout != null) {
            canvas.save();
            if (!LocaleController.isRTL) {
                f = (float) AndroidUtilities.leftBaseline;
            }
            canvas.translate((float) AndroidUtilities.dp(f), (float) this.fromInfoLayoutY);
            this.fromInfoLayout.draw(canvas);
            canvas.restore();
        }
        this.letterDrawable.draw(canvas);
        if (this.drawLinkImageView) {
            this.linkImageView.draw(canvas);
        }
        if (!this.needDivider) {
            return;
        }
        if (LocaleController.isRTL) {
            canvas.drawLine(0.0f, (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - AndroidUtilities.dp((float) AndroidUtilities.leftBaseline)), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
        } else {
            canvas.drawLine((float) AndroidUtilities.dp((float) AndroidUtilities.leftBaseline), (float) (getMeasuredHeight() - 1), (float) getMeasuredWidth(), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
        }
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        StringBuilder sb = new StringBuilder();
        StaticLayout staticLayout = this.titleLayout;
        if (staticLayout != null) {
            sb.append(staticLayout.getText());
        }
        if (this.descriptionLayout != null) {
            sb.append(", ");
            sb.append(this.descriptionLayout.getText());
        }
        if (this.descriptionLayout2 != null) {
            sb.append(", ");
            sb.append(this.descriptionLayout2.getText());
        }
        accessibilityNodeInfo.setText(sb.toString());
        if (this.checkBox.isChecked()) {
            accessibilityNodeInfo.setChecked(true);
            accessibilityNodeInfo.setCheckable(true);
        }
    }
}
