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
    private CheckBox2 checkBox;
    /* access modifiers changed from: private */
    public boolean checkingForLongPress = false;
    /* access modifiers changed from: private */
    public SharedLinkCellDelegate delegate;
    private int description2Y = AndroidUtilities.dp(30.0f);
    private StaticLayout descriptionLayout;
    private StaticLayout descriptionLayout2;
    private TextPaint descriptionTextPaint;
    private int descriptionY = AndroidUtilities.dp(30.0f);
    private boolean drawLinkImageView;
    private LetterDrawable letterDrawable;
    private ImageReceiver linkImageView;
    private ArrayList<StaticLayout> linkLayout = new ArrayList<>();
    private boolean linkPreviewPressed;
    private int linkY;
    ArrayList<String> links = new ArrayList<>();
    private MessageObject message;
    private boolean needDivider;
    /* access modifiers changed from: private */
    public CheckForLongPress pendingCheckForLongPress = null;
    private CheckForTap pendingCheckForTap = null;
    /* access modifiers changed from: private */
    public int pressCount = 0;
    /* access modifiers changed from: private */
    public int pressedLink;
    private StaticLayout titleLayout;
    private TextPaint titleTextPaint;
    private int titleY = AndroidUtilities.dp(10.0f);
    private LinkPath urlPath;

    public interface SharedLinkCellDelegate {
        boolean canPerformActions();

        void needOpenWebView(TLRPC$WebPage tLRPC$WebPage);

        void onLinkLongPress(String str);
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
                    access$400.onLinkLongPress(sharedLinkCell.links.get(sharedLinkCell.pressedLink));
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
        super(context);
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
        addView(this.checkBox, LayoutHelper.createFrame(24, 24.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 44.0f, 44.0f, LocaleController.isRTL ? 44.0f : 0.0f, 0.0f));
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:112:0x0221  */
    /* JADX WARNING: Removed duplicated region for block: B:119:0x0237 A[SYNTHETIC, Splitter:B:119:0x0237] */
    /* JADX WARNING: Removed duplicated region for block: B:128:0x0273  */
    /* JADX WARNING: Removed duplicated region for block: B:129:0x0278  */
    /* JADX WARNING: Removed duplicated region for block: B:132:0x0283 A[SYNTHETIC, Splitter:B:132:0x0283] */
    /* JADX WARNING: Removed duplicated region for block: B:140:0x02b6 A[SYNTHETIC, Splitter:B:140:0x02b6] */
    /* JADX WARNING: Removed duplicated region for block: B:148:0x02dd  */
    /* JADX WARNING: Removed duplicated region for block: B:168:0x0365  */
    /* JADX WARNING: Removed duplicated region for block: B:169:0x0370  */
    /* JADX WARNING: Removed duplicated region for block: B:172:0x0389  */
    /* JADX WARNING: Removed duplicated region for block: B:183:0x040b  */
    /* JADX WARNING: Removed duplicated region for block: B:184:0x041d  */
    /* JADX WARNING: Removed duplicated region for block: B:196:0x0458  */
    /* JADX WARNING: Removed duplicated region for block: B:199:0x0465  */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x0071  */
    /* JADX WARNING: Removed duplicated region for block: B:99:0x01db A[Catch:{ Exception -> 0x0213 }] */
    @android.annotation.SuppressLint({"DrawAllocation"})
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onMeasure(int r27, int r28) {
        /*
            r26 = this;
            r1 = r26
            r2 = 0
            r1.drawLinkImageView = r2
            r3 = 0
            r1.descriptionLayout = r3
            r1.titleLayout = r3
            r1.descriptionLayout2 = r3
            java.util.ArrayList<android.text.StaticLayout> r0 = r1.linkLayout
            r0.clear()
            java.util.ArrayList<java.lang.String> r0 = r1.links
            r0.clear()
            int r0 = android.view.View.MeasureSpec.getSize(r27)
            int r4 = org.telegram.messenger.AndroidUtilities.leftBaseline
            float r4 = (float) r4
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r0 = r0 - r4
            r4 = 1090519040(0x41000000, float:8.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r4 = r0 - r4
            org.telegram.messenger.MessageObject r0 = r1.message
            org.telegram.tgnet.TLRPC$Message r5 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r5.media
            boolean r6 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaWebPage
            r13 = 1
            if (r6 == 0) goto L_0x005f
            org.telegram.tgnet.TLRPC$WebPage r5 = r5.webpage
            boolean r6 = r5 instanceof org.telegram.tgnet.TLRPC$TL_webPage
            if (r6 == 0) goto L_0x005f
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r6 = r0.photoThumbs
            if (r6 != 0) goto L_0x0046
            org.telegram.tgnet.TLRPC$Photo r6 = r5.photo
            if (r6 == 0) goto L_0x0046
            r0.generateThumbs(r13)
        L_0x0046:
            org.telegram.tgnet.TLRPC$Photo r0 = r5.photo
            if (r0 == 0) goto L_0x0052
            org.telegram.messenger.MessageObject r0 = r1.message
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r0 = r0.photoThumbs
            if (r0 == 0) goto L_0x0052
            r0 = 1
            goto L_0x0053
        L_0x0052:
            r0 = 0
        L_0x0053:
            java.lang.String r6 = r5.title
            if (r6 != 0) goto L_0x0059
            java.lang.String r6 = r5.site_name
        L_0x0059:
            java.lang.String r7 = r5.description
            java.lang.String r5 = r5.url
            r14 = r0
            goto L_0x0063
        L_0x005f:
            r5 = r3
            r6 = r5
            r7 = r6
            r14 = 0
        L_0x0063:
            org.telegram.messenger.MessageObject r0 = r1.message
            if (r0 == 0) goto L_0x0221
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r0 = r0.entities
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x0221
            r0 = r3
            r8 = 0
        L_0x0073:
            org.telegram.messenger.MessageObject r9 = r1.message
            org.telegram.tgnet.TLRPC$Message r9 = r9.messageOwner
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r9 = r9.entities
            int r9 = r9.size()
            if (r8 >= r9) goto L_0x021d
            org.telegram.messenger.MessageObject r9 = r1.message
            org.telegram.tgnet.TLRPC$Message r9 = r9.messageOwner
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r9 = r9.entities
            java.lang.Object r9 = r9.get(r8)
            org.telegram.tgnet.TLRPC$MessageEntity r9 = (org.telegram.tgnet.TLRPC$MessageEntity) r9
            int r10 = r9.length
            if (r10 <= 0) goto L_0x0218
            int r10 = r9.offset
            if (r10 < 0) goto L_0x0218
            org.telegram.messenger.MessageObject r11 = r1.message
            org.telegram.tgnet.TLRPC$Message r11 = r11.messageOwner
            java.lang.String r11 = r11.message
            int r11 = r11.length()
            if (r10 < r11) goto L_0x00a1
            goto L_0x0218
        L_0x00a1:
            int r10 = r9.offset
            int r11 = r9.length
            int r10 = r10 + r11
            org.telegram.messenger.MessageObject r11 = r1.message
            org.telegram.tgnet.TLRPC$Message r11 = r11.messageOwner
            java.lang.String r11 = r11.message
            int r11 = r11.length()
            if (r10 <= r11) goto L_0x00c1
            org.telegram.messenger.MessageObject r10 = r1.message
            org.telegram.tgnet.TLRPC$Message r10 = r10.messageOwner
            java.lang.String r10 = r10.message
            int r10 = r10.length()
            int r11 = r9.offset
            int r10 = r10 - r11
            r9.length = r10
        L_0x00c1:
            if (r8 != 0) goto L_0x00f2
            if (r5 == 0) goto L_0x00f2
            int r10 = r9.offset
            if (r10 != 0) goto L_0x00d7
            int r10 = r9.length
            org.telegram.messenger.MessageObject r11 = r1.message
            org.telegram.tgnet.TLRPC$Message r11 = r11.messageOwner
            java.lang.String r11 = r11.message
            int r11 = r11.length()
            if (r10 == r11) goto L_0x00f2
        L_0x00d7:
            org.telegram.messenger.MessageObject r10 = r1.message
            org.telegram.tgnet.TLRPC$Message r10 = r10.messageOwner
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r10 = r10.entities
            int r10 = r10.size()
            if (r10 != r13) goto L_0x00ec
            if (r7 != 0) goto L_0x00f2
            org.telegram.messenger.MessageObject r0 = r1.message
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
            goto L_0x00f2
        L_0x00ec:
            org.telegram.messenger.MessageObject r0 = r1.message
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            java.lang.String r0 = r0.message
        L_0x00f2:
            r10 = r0
            boolean r0 = r9 instanceof org.telegram.tgnet.TLRPC$TL_messageEntityTextUrl     // Catch:{ Exception -> 0x0213 }
            if (r0 != 0) goto L_0x015a
            boolean r0 = r9 instanceof org.telegram.tgnet.TLRPC$TL_messageEntityUrl     // Catch:{ Exception -> 0x0213 }
            if (r0 == 0) goto L_0x00fc
            goto L_0x015a
        L_0x00fc:
            boolean r0 = r9 instanceof org.telegram.tgnet.TLRPC$TL_messageEntityEmail     // Catch:{ Exception -> 0x0213 }
            if (r0 == 0) goto L_0x0157
            if (r6 == 0) goto L_0x0108
            int r0 = r6.length()     // Catch:{ Exception -> 0x0213 }
            if (r0 != 0) goto L_0x0157
        L_0x0108:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0213 }
            r0.<init>()     // Catch:{ Exception -> 0x0213 }
            java.lang.String r11 = "mailto:"
            r0.append(r11)     // Catch:{ Exception -> 0x0213 }
            org.telegram.messenger.MessageObject r11 = r1.message     // Catch:{ Exception -> 0x0213 }
            org.telegram.tgnet.TLRPC$Message r11 = r11.messageOwner     // Catch:{ Exception -> 0x0213 }
            java.lang.String r11 = r11.message     // Catch:{ Exception -> 0x0213 }
            int r12 = r9.offset     // Catch:{ Exception -> 0x0213 }
            int r15 = r9.offset     // Catch:{ Exception -> 0x0213 }
            int r3 = r9.length     // Catch:{ Exception -> 0x0213 }
            int r15 = r15 + r3
            java.lang.String r3 = r11.substring(r12, r15)     // Catch:{ Exception -> 0x0213 }
            r0.append(r3)     // Catch:{ Exception -> 0x0213 }
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x0213 }
            org.telegram.messenger.MessageObject r3 = r1.message     // Catch:{ Exception -> 0x0213 }
            org.telegram.tgnet.TLRPC$Message r3 = r3.messageOwner     // Catch:{ Exception -> 0x0213 }
            java.lang.String r3 = r3.message     // Catch:{ Exception -> 0x0213 }
            int r11 = r9.offset     // Catch:{ Exception -> 0x0213 }
            int r12 = r9.offset     // Catch:{ Exception -> 0x0213 }
            int r15 = r9.length     // Catch:{ Exception -> 0x0213 }
            int r12 = r12 + r15
            java.lang.String r3 = r3.substring(r11, r12)     // Catch:{ Exception -> 0x0213 }
            int r6 = r9.offset     // Catch:{ Exception -> 0x0210 }
            if (r6 != 0) goto L_0x014d
            int r6 = r9.length     // Catch:{ Exception -> 0x0210 }
            org.telegram.messenger.MessageObject r9 = r1.message     // Catch:{ Exception -> 0x0210 }
            org.telegram.tgnet.TLRPC$Message r9 = r9.messageOwner     // Catch:{ Exception -> 0x0210 }
            java.lang.String r9 = r9.message     // Catch:{ Exception -> 0x0210 }
            int r9 = r9.length()     // Catch:{ Exception -> 0x0210 }
            if (r6 == r9) goto L_0x0154
        L_0x014d:
            org.telegram.messenger.MessageObject r6 = r1.message     // Catch:{ Exception -> 0x0210 }
            org.telegram.tgnet.TLRPC$Message r6 = r6.messageOwner     // Catch:{ Exception -> 0x0210 }
            java.lang.String r6 = r6.message     // Catch:{ Exception -> 0x0210 }
            r7 = r6
        L_0x0154:
            r6 = r3
            goto L_0x01d9
        L_0x0157:
            r0 = 0
            goto L_0x01d9
        L_0x015a:
            boolean r0 = r9 instanceof org.telegram.tgnet.TLRPC$TL_messageEntityUrl     // Catch:{ Exception -> 0x0213 }
            if (r0 == 0) goto L_0x0170
            org.telegram.messenger.MessageObject r0 = r1.message     // Catch:{ Exception -> 0x0213 }
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner     // Catch:{ Exception -> 0x0213 }
            java.lang.String r0 = r0.message     // Catch:{ Exception -> 0x0213 }
            int r3 = r9.offset     // Catch:{ Exception -> 0x0213 }
            int r11 = r9.offset     // Catch:{ Exception -> 0x0213 }
            int r12 = r9.length     // Catch:{ Exception -> 0x0213 }
            int r11 = r11 + r12
            java.lang.String r0 = r0.substring(r3, r11)     // Catch:{ Exception -> 0x0213 }
            goto L_0x0172
        L_0x0170:
            java.lang.String r0 = r9.url     // Catch:{ Exception -> 0x0213 }
        L_0x0172:
            r3 = r0
            if (r6 == 0) goto L_0x017e
            int r0 = r6.length()     // Catch:{ Exception -> 0x0213 }
            if (r0 != 0) goto L_0x017c
            goto L_0x017e
        L_0x017c:
            r0 = r3
            goto L_0x01d9
        L_0x017e:
            android.net.Uri r0 = android.net.Uri.parse(r3)     // Catch:{ Exception -> 0x0210 }
            java.lang.String r0 = r0.getHost()     // Catch:{ Exception -> 0x0210 }
            if (r0 != 0) goto L_0x018a
            r6 = r3
            goto L_0x018b
        L_0x018a:
            r6 = r0
        L_0x018b:
            if (r6 == 0) goto L_0x01c0
            r0 = 46
            int r11 = r6.lastIndexOf(r0)     // Catch:{ Exception -> 0x0213 }
            if (r11 < 0) goto L_0x01c0
            java.lang.String r6 = r6.substring(r2, r11)     // Catch:{ Exception -> 0x0213 }
            int r0 = r6.lastIndexOf(r0)     // Catch:{ Exception -> 0x0213 }
            if (r0 < 0) goto L_0x01a5
            int r0 = r0 + 1
            java.lang.String r6 = r6.substring(r0)     // Catch:{ Exception -> 0x0213 }
        L_0x01a5:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0213 }
            r0.<init>()     // Catch:{ Exception -> 0x0213 }
            java.lang.String r11 = r6.substring(r2, r13)     // Catch:{ Exception -> 0x0213 }
            java.lang.String r11 = r11.toUpperCase()     // Catch:{ Exception -> 0x0213 }
            r0.append(r11)     // Catch:{ Exception -> 0x0213 }
            java.lang.String r11 = r6.substring(r13)     // Catch:{ Exception -> 0x0213 }
            r0.append(r11)     // Catch:{ Exception -> 0x0213 }
            java.lang.String r6 = r0.toString()     // Catch:{ Exception -> 0x0213 }
        L_0x01c0:
            int r0 = r9.offset     // Catch:{ Exception -> 0x0213 }
            if (r0 != 0) goto L_0x01d2
            int r0 = r9.length     // Catch:{ Exception -> 0x0213 }
            org.telegram.messenger.MessageObject r9 = r1.message     // Catch:{ Exception -> 0x0213 }
            org.telegram.tgnet.TLRPC$Message r9 = r9.messageOwner     // Catch:{ Exception -> 0x0213 }
            java.lang.String r9 = r9.message     // Catch:{ Exception -> 0x0213 }
            int r9 = r9.length()     // Catch:{ Exception -> 0x0213 }
            if (r0 == r9) goto L_0x017c
        L_0x01d2:
            org.telegram.messenger.MessageObject r0 = r1.message     // Catch:{ Exception -> 0x0213 }
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner     // Catch:{ Exception -> 0x0213 }
            java.lang.String r7 = r0.message     // Catch:{ Exception -> 0x0213 }
            goto L_0x017c
        L_0x01d9:
            if (r0 == 0) goto L_0x0217
            java.lang.String r3 = r0.toLowerCase()     // Catch:{ Exception -> 0x0213 }
            java.lang.String r9 = "http"
            int r3 = r3.indexOf(r9)     // Catch:{ Exception -> 0x0213 }
            if (r3 == 0) goto L_0x020a
            java.lang.String r3 = r0.toLowerCase()     // Catch:{ Exception -> 0x0213 }
            java.lang.String r9 = "mailto"
            int r3 = r3.indexOf(r9)     // Catch:{ Exception -> 0x0213 }
            if (r3 == 0) goto L_0x020a
            java.util.ArrayList<java.lang.String> r3 = r1.links     // Catch:{ Exception -> 0x0213 }
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0213 }
            r9.<init>()     // Catch:{ Exception -> 0x0213 }
            java.lang.String r11 = "http://"
            r9.append(r11)     // Catch:{ Exception -> 0x0213 }
            r9.append(r0)     // Catch:{ Exception -> 0x0213 }
            java.lang.String r0 = r9.toString()     // Catch:{ Exception -> 0x0213 }
            r3.add(r0)     // Catch:{ Exception -> 0x0213 }
            goto L_0x0217
        L_0x020a:
            java.util.ArrayList<java.lang.String> r3 = r1.links     // Catch:{ Exception -> 0x0213 }
            r3.add(r0)     // Catch:{ Exception -> 0x0213 }
            goto L_0x0217
        L_0x0210:
            r0 = move-exception
            r6 = r3
            goto L_0x0214
        L_0x0213:
            r0 = move-exception
        L_0x0214:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0217:
            r0 = r10
        L_0x0218:
            int r8 = r8 + 1
            r3 = 0
            goto L_0x0073
        L_0x021d:
            r11 = r0
            r12 = r6
            r3 = r7
            goto L_0x0224
        L_0x0221:
            r12 = r6
            r3 = r7
            r11 = 0
        L_0x0224:
            if (r5 == 0) goto L_0x0233
            java.util.ArrayList<java.lang.String> r0 = r1.links
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L_0x0233
            java.util.ArrayList<java.lang.String> r0 = r1.links
            r0.add(r5)
        L_0x0233:
            r15 = 1082130432(0x40800000, float:4.0)
            if (r12 == 0) goto L_0x026b
            android.text.TextPaint r6 = r1.titleTextPaint     // Catch:{ Exception -> 0x0262 }
            r9 = 0
            r10 = 3
            r5 = r12
            r7 = r4
            r8 = r4
            android.text.StaticLayout r0 = org.telegram.ui.Cells.ChatMessageCell.generateStaticLayout(r5, r6, r7, r8, r9, r10)     // Catch:{ Exception -> 0x0262 }
            r1.titleLayout = r0     // Catch:{ Exception -> 0x0262 }
            int r0 = r0.getLineCount()     // Catch:{ Exception -> 0x0262 }
            if (r0 <= 0) goto L_0x0266
            int r0 = r1.titleY     // Catch:{ Exception -> 0x0262 }
            android.text.StaticLayout r5 = r1.titleLayout     // Catch:{ Exception -> 0x0262 }
            android.text.StaticLayout r6 = r1.titleLayout     // Catch:{ Exception -> 0x0262 }
            int r6 = r6.getLineCount()     // Catch:{ Exception -> 0x0262 }
            int r6 = r6 - r13
            int r5 = r5.getLineBottom(r6)     // Catch:{ Exception -> 0x0262 }
            int r0 = r0 + r5
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r15)     // Catch:{ Exception -> 0x0262 }
            int r0 = r0 + r5
            r1.descriptionY = r0     // Catch:{ Exception -> 0x0262 }
            goto L_0x0266
        L_0x0262:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0266:
            org.telegram.ui.Components.LetterDrawable r0 = r1.letterDrawable
            r0.setTitle(r12)
        L_0x026b:
            int r0 = r1.descriptionY
            r1.description2Y = r0
            android.text.StaticLayout r0 = r1.titleLayout
            if (r0 == 0) goto L_0x0278
            int r0 = r0.getLineCount()
            goto L_0x0279
        L_0x0278:
            r0 = 0
        L_0x0279:
            int r0 = 4 - r0
            int r12 = java.lang.Math.max(r13, r0)
            r16 = 1084227584(0x40a00000, float:5.0)
            if (r3 == 0) goto L_0x02b2
            android.text.TextPaint r6 = r1.descriptionTextPaint     // Catch:{ Exception -> 0x02ae }
            r9 = 0
            r5 = r3
            r7 = r4
            r8 = r4
            r10 = r12
            android.text.StaticLayout r0 = org.telegram.ui.Cells.ChatMessageCell.generateStaticLayout(r5, r6, r7, r8, r9, r10)     // Catch:{ Exception -> 0x02ae }
            r1.descriptionLayout = r0     // Catch:{ Exception -> 0x02ae }
            int r0 = r0.getLineCount()     // Catch:{ Exception -> 0x02ae }
            if (r0 <= 0) goto L_0x02b2
            int r0 = r1.descriptionY     // Catch:{ Exception -> 0x02ae }
            android.text.StaticLayout r3 = r1.descriptionLayout     // Catch:{ Exception -> 0x02ae }
            android.text.StaticLayout r5 = r1.descriptionLayout     // Catch:{ Exception -> 0x02ae }
            int r5 = r5.getLineCount()     // Catch:{ Exception -> 0x02ae }
            int r5 = r5 - r13
            int r3 = r3.getLineBottom(r5)     // Catch:{ Exception -> 0x02ae }
            int r0 = r0 + r3
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r16)     // Catch:{ Exception -> 0x02ae }
            int r0 = r0 + r3
            r1.description2Y = r0     // Catch:{ Exception -> 0x02ae }
            goto L_0x02b2
        L_0x02ae:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x02b2:
            r3 = 1092616192(0x41200000, float:10.0)
            if (r11 == 0) goto L_0x02d5
            android.text.TextPaint r6 = r1.descriptionTextPaint     // Catch:{ Exception -> 0x02d1 }
            r9 = 0
            r5 = r11
            r7 = r4
            r8 = r4
            r10 = r12
            android.text.StaticLayout r0 = org.telegram.ui.Cells.ChatMessageCell.generateStaticLayout(r5, r6, r7, r8, r9, r10)     // Catch:{ Exception -> 0x02d1 }
            r1.descriptionLayout2 = r0     // Catch:{ Exception -> 0x02d1 }
            android.text.StaticLayout r0 = r1.descriptionLayout     // Catch:{ Exception -> 0x02d1 }
            if (r0 == 0) goto L_0x02d5
            int r0 = r1.description2Y     // Catch:{ Exception -> 0x02d1 }
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r3)     // Catch:{ Exception -> 0x02d1 }
            int r0 = r0 + r5
            r1.description2Y = r0     // Catch:{ Exception -> 0x02d1 }
            goto L_0x02d5
        L_0x02d1:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x02d5:
            java.util.ArrayList<java.lang.String> r0 = r1.links
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x035b
            r12 = 0
        L_0x02de:
            java.util.ArrayList<java.lang.String> r0 = r1.links
            int r0 = r0.size()
            if (r12 >= r0) goto L_0x035b
            java.util.ArrayList<java.lang.String> r0 = r1.links     // Catch:{ Exception -> 0x0352 }
            java.lang.Object r0 = r0.get(r12)     // Catch:{ Exception -> 0x0352 }
            java.lang.String r0 = (java.lang.String) r0     // Catch:{ Exception -> 0x0352 }
            android.text.TextPaint r5 = r1.descriptionTextPaint     // Catch:{ Exception -> 0x0352 }
            float r5 = r5.measureText(r0)     // Catch:{ Exception -> 0x0352 }
            double r5 = (double) r5     // Catch:{ Exception -> 0x0352 }
            double r5 = java.lang.Math.ceil(r5)     // Catch:{ Exception -> 0x0352 }
            int r5 = (int) r5     // Catch:{ Exception -> 0x0352 }
            r6 = 10
            r7 = 32
            java.lang.String r0 = r0.replace(r6, r7)     // Catch:{ Exception -> 0x0352 }
            android.text.TextPaint r6 = r1.descriptionTextPaint     // Catch:{ Exception -> 0x0352 }
            int r5 = java.lang.Math.min(r5, r4)     // Catch:{ Exception -> 0x0352 }
            float r5 = (float) r5     // Catch:{ Exception -> 0x0352 }
            android.text.TextUtils$TruncateAt r7 = android.text.TextUtils.TruncateAt.MIDDLE     // Catch:{ Exception -> 0x0352 }
            java.lang.CharSequence r6 = android.text.TextUtils.ellipsize(r0, r6, r5, r7)     // Catch:{ Exception -> 0x0352 }
            android.text.StaticLayout r0 = new android.text.StaticLayout     // Catch:{ Exception -> 0x0352 }
            android.text.TextPaint r7 = r1.descriptionTextPaint     // Catch:{ Exception -> 0x0352 }
            android.text.Layout$Alignment r9 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x0352 }
            r10 = 1065353216(0x3var_, float:1.0)
            r11 = 0
            r17 = 0
            r5 = r0
            r8 = r4
            r18 = r12
            r12 = r17
            r5.<init>(r6, r7, r8, r9, r10, r11, r12)     // Catch:{ Exception -> 0x0350 }
            int r5 = r1.description2Y     // Catch:{ Exception -> 0x0350 }
            r1.linkY = r5     // Catch:{ Exception -> 0x0350 }
            android.text.StaticLayout r5 = r1.descriptionLayout2     // Catch:{ Exception -> 0x0350 }
            if (r5 == 0) goto L_0x034a
            android.text.StaticLayout r5 = r1.descriptionLayout2     // Catch:{ Exception -> 0x0350 }
            int r5 = r5.getLineCount()     // Catch:{ Exception -> 0x0350 }
            if (r5 == 0) goto L_0x034a
            int r5 = r1.linkY     // Catch:{ Exception -> 0x0350 }
            android.text.StaticLayout r6 = r1.descriptionLayout2     // Catch:{ Exception -> 0x0350 }
            android.text.StaticLayout r7 = r1.descriptionLayout2     // Catch:{ Exception -> 0x0350 }
            int r7 = r7.getLineCount()     // Catch:{ Exception -> 0x0350 }
            int r7 = r7 - r13
            int r6 = r6.getLineBottom(r7)     // Catch:{ Exception -> 0x0350 }
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r16)     // Catch:{ Exception -> 0x0350 }
            int r6 = r6 + r7
            int r5 = r5 + r6
            r1.linkY = r5     // Catch:{ Exception -> 0x0350 }
        L_0x034a:
            java.util.ArrayList<android.text.StaticLayout> r5 = r1.linkLayout     // Catch:{ Exception -> 0x0350 }
            r5.add(r0)     // Catch:{ Exception -> 0x0350 }
            goto L_0x0358
        L_0x0350:
            r0 = move-exception
            goto L_0x0355
        L_0x0352:
            r0 = move-exception
            r18 = r12
        L_0x0355:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0358:
            int r12 = r18 + 1
            goto L_0x02de
        L_0x035b:
            r0 = 1112539136(0x42500000, float:52.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            boolean r4 = org.telegram.messenger.LocaleController.isRTL
            if (r4 == 0) goto L_0x0370
            int r4 = android.view.View.MeasureSpec.getSize(r27)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r4 = r4 - r5
            int r4 = r4 - r0
            goto L_0x0374
        L_0x0370:
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r3)
        L_0x0374:
            org.telegram.ui.Components.LetterDrawable r5 = r1.letterDrawable
            r6 = 1093664768(0x41300000, float:11.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r8 = r4 + r0
            r9 = 1115422720(0x427CLASSNAME, float:63.0)
            int r9 = org.telegram.messenger.AndroidUtilities.dp(r9)
            r5.setBounds(r4, r7, r8, r9)
            if (r14 == 0) goto L_0x0401
            org.telegram.messenger.MessageObject r5 = r1.message
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r5 = r5.photoThumbs
            org.telegram.tgnet.TLRPC$PhotoSize r5 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r5, r0, r13)
            org.telegram.messenger.MessageObject r7 = r1.message
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r7 = r7.photoThumbs
            r8 = 80
            org.telegram.tgnet.TLRPC$PhotoSize r7 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r7, r8)
            if (r7 != r5) goto L_0x039e
            r7 = 0
        L_0x039e:
            r8 = -1
            r5.size = r8
            if (r7 == 0) goto L_0x03a5
            r7.size = r8
        L_0x03a5:
            org.telegram.messenger.ImageReceiver r8 = r1.linkImageView
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            r8.setImageCoords(r4, r6, r0, r0)
            org.telegram.messenger.FileLoader.getAttachFileName(r5)
            java.util.Locale r4 = java.util.Locale.US
            r6 = 2
            java.lang.Object[] r8 = new java.lang.Object[r6]
            java.lang.Integer r9 = java.lang.Integer.valueOf(r0)
            r8[r2] = r9
            java.lang.Integer r9 = java.lang.Integer.valueOf(r0)
            r8[r13] = r9
            java.lang.String r9 = "%d_%d"
            java.lang.String r19 = java.lang.String.format(r4, r9, r8)
            java.util.Locale r4 = java.util.Locale.US
            java.lang.Object[] r6 = new java.lang.Object[r6]
            java.lang.Integer r8 = java.lang.Integer.valueOf(r0)
            r6[r2] = r8
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            r6[r13] = r0
            java.lang.String r0 = "%d_%d_b"
            java.lang.String r21 = java.lang.String.format(r4, r0, r6)
            org.telegram.messenger.ImageReceiver r0 = r1.linkImageView
            org.telegram.messenger.MessageObject r4 = r1.message
            org.telegram.tgnet.TLObject r4 = r4.photoThumbsObject
            org.telegram.messenger.ImageLocation r18 = org.telegram.messenger.ImageLocation.getForObject(r5, r4)
            org.telegram.messenger.MessageObject r4 = r1.message
            org.telegram.tgnet.TLObject r4 = r4.photoThumbsObject
            org.telegram.messenger.ImageLocation r20 = org.telegram.messenger.ImageLocation.getForObject(r7, r4)
            r22 = 0
            r23 = 0
            org.telegram.messenger.MessageObject r4 = r1.message
            r25 = 0
            r17 = r0
            r24 = r4
            r17.setImage(r18, r19, r20, r21, r22, r23, r24, r25)
            r1.drawLinkImageView = r13
        L_0x0401:
            android.text.StaticLayout r0 = r1.titleLayout
            if (r0 == 0) goto L_0x041d
            int r0 = r0.getLineCount()
            if (r0 == 0) goto L_0x041d
            android.text.StaticLayout r0 = r1.titleLayout
            int r4 = r0.getLineCount()
            int r4 = r4 - r13
            int r0 = r0.getLineBottom(r4)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r15)
            int r0 = r0 + r4
            int r0 = r0 + r2
            goto L_0x041e
        L_0x041d:
            r0 = 0
        L_0x041e:
            android.text.StaticLayout r4 = r1.descriptionLayout
            if (r4 == 0) goto L_0x0439
            int r4 = r4.getLineCount()
            if (r4 == 0) goto L_0x0439
            android.text.StaticLayout r4 = r1.descriptionLayout
            int r5 = r4.getLineCount()
            int r5 = r5 - r13
            int r4 = r4.getLineBottom(r5)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r16)
            int r4 = r4 + r5
            int r0 = r0 + r4
        L_0x0439:
            android.text.StaticLayout r4 = r1.descriptionLayout2
            if (r4 == 0) goto L_0x045d
            int r4 = r4.getLineCount()
            if (r4 == 0) goto L_0x045d
            android.text.StaticLayout r4 = r1.descriptionLayout2
            int r5 = r4.getLineCount()
            int r5 = r5 - r13
            int r4 = r4.getLineBottom(r5)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r16)
            int r4 = r4 + r5
            int r0 = r0 + r4
            android.text.StaticLayout r4 = r1.descriptionLayout
            if (r4 == 0) goto L_0x045d
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r0 = r0 + r3
        L_0x045d:
            java.util.ArrayList<android.text.StaticLayout> r3 = r1.linkLayout
            int r3 = r3.size()
            if (r2 >= r3) goto L_0x0480
            java.util.ArrayList<android.text.StaticLayout> r3 = r1.linkLayout
            java.lang.Object r3 = r3.get(r2)
            android.text.StaticLayout r3 = (android.text.StaticLayout) r3
            int r4 = r3.getLineCount()
            if (r4 <= 0) goto L_0x047d
            int r4 = r3.getLineCount()
            int r4 = r4 - r13
            int r3 = r3.getLineBottom(r4)
            int r0 = r0 + r3
        L_0x047d:
            int r2 = r2 + 1
            goto L_0x045d
        L_0x0480:
            org.telegram.ui.Components.CheckBox2 r2 = r1.checkBox
            r3 = 1103101952(0x41CLASSNAME, float:24.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r5 = 1073741824(0x40000000, float:2.0)
            int r4 = android.view.View.MeasureSpec.makeMeasureSpec(r4, r5)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            int r3 = android.view.View.MeasureSpec.makeMeasureSpec(r3, r5)
            r2.measure(r4, r3)
            int r2 = android.view.View.MeasureSpec.getSize(r27)
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

    /* JADX WARNING: Code restructure failed: missing block: B:65:0x0107, code lost:
        r3 = false;
     */
    /* JADX WARNING: Removed duplicated region for block: B:73:0x011b A[ORIG_RETURN, RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:79:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onTouchEvent(android.view.MotionEvent r12) {
        /*
            r11 = this;
            org.telegram.messenger.MessageObject r0 = r11.message
            r1 = 1
            r2 = 0
            if (r0 == 0) goto L_0x010e
            java.util.ArrayList<android.text.StaticLayout> r0 = r11.linkLayout
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x010e
            org.telegram.ui.Cells.SharedLinkCell$SharedLinkCellDelegate r0 = r11.delegate
            if (r0 == 0) goto L_0x010e
            boolean r0 = r0.canPerformActions()
            if (r0 == 0) goto L_0x010e
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
            if (r0 != r3) goto L_0x0111
            r11.resetPressedLink()
            goto L_0x0111
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
            if (r4 >= r6) goto L_0x0106
            java.util.ArrayList<android.text.StaticLayout> r6 = r11.linkLayout
            java.lang.Object r6 = r6.get(r4)
            android.text.StaticLayout r6 = (android.text.StaticLayout) r6
            int r7 = r6.getLineCount()
            if (r7 <= 0) goto L_0x0102
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
            if (r10 < 0) goto L_0x0101
            float r10 = r6.getLineWidth(r2)
            float r8 = r8 + r10
            int r8 = (r9 > r8 ? 1 : (r9 == r8 ? 0 : -1))
            if (r8 > 0) goto L_0x0101
            int r8 = r11.linkY
            int r9 = r8 + r5
            if (r3 < r9) goto L_0x0101
            int r8 = r8 + r5
            int r8 = r8 + r7
            if (r3 > r8) goto L_0x0101
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
            goto L_0x00fc
        L_0x00b0:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x00fc
        L_0x00b5:
            boolean r0 = r11.linkPreviewPressed
            if (r0 == 0) goto L_0x00ff
            int r0 = r11.pressedLink     // Catch:{ Exception -> 0x00f5 }
            if (r0 != 0) goto L_0x00ce
            org.telegram.messenger.MessageObject r0 = r11.message     // Catch:{ Exception -> 0x00f5 }
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner     // Catch:{ Exception -> 0x00f5 }
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media     // Catch:{ Exception -> 0x00f5 }
            if (r0 == 0) goto L_0x00ce
            org.telegram.messenger.MessageObject r0 = r11.message     // Catch:{ Exception -> 0x00f5 }
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner     // Catch:{ Exception -> 0x00f5 }
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media     // Catch:{ Exception -> 0x00f5 }
            org.telegram.tgnet.TLRPC$WebPage r0 = r0.webpage     // Catch:{ Exception -> 0x00f5 }
            goto L_0x00cf
        L_0x00ce:
            r0 = 0
        L_0x00cf:
            if (r0 == 0) goto L_0x00e3
            java.lang.String r3 = r0.embed_url     // Catch:{ Exception -> 0x00f5 }
            if (r3 == 0) goto L_0x00e3
            java.lang.String r3 = r0.embed_url     // Catch:{ Exception -> 0x00f5 }
            int r3 = r3.length()     // Catch:{ Exception -> 0x00f5 }
            if (r3 == 0) goto L_0x00e3
            org.telegram.ui.Cells.SharedLinkCell$SharedLinkCellDelegate r3 = r11.delegate     // Catch:{ Exception -> 0x00f5 }
            r3.needOpenWebView(r0)     // Catch:{ Exception -> 0x00f5 }
            goto L_0x00f9
        L_0x00e3:
            android.content.Context r0 = r11.getContext()     // Catch:{ Exception -> 0x00f5 }
            java.util.ArrayList<java.lang.String> r3 = r11.links     // Catch:{ Exception -> 0x00f5 }
            int r4 = r11.pressedLink     // Catch:{ Exception -> 0x00f5 }
            java.lang.Object r3 = r3.get(r4)     // Catch:{ Exception -> 0x00f5 }
            java.lang.String r3 = (java.lang.String) r3     // Catch:{ Exception -> 0x00f5 }
            org.telegram.messenger.browser.Browser.openUrl((android.content.Context) r0, (java.lang.String) r3)     // Catch:{ Exception -> 0x00f5 }
            goto L_0x00f9
        L_0x00f5:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x00f9:
            r11.resetPressedLink()
        L_0x00fc:
            r0 = 1
            r3 = 1
            goto L_0x0108
        L_0x00ff:
            r0 = 1
            goto L_0x0107
        L_0x0101:
            int r5 = r5 + r7
        L_0x0102:
            int r4 = r4 + 1
            goto L_0x0041
        L_0x0106:
            r0 = 0
        L_0x0107:
            r3 = 0
        L_0x0108:
            if (r0 != 0) goto L_0x0112
            r11.resetPressedLink()
            goto L_0x0112
        L_0x010e:
            r11.resetPressedLink()
        L_0x0111:
            r3 = 0
        L_0x0112:
            if (r3 != 0) goto L_0x011c
            boolean r12 = super.onTouchEvent(r12)
            if (r12 == 0) goto L_0x011b
            goto L_0x011c
        L_0x011b:
            r1 = 0
        L_0x011c:
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
        if (this.titleLayout != null) {
            canvas.save();
            canvas.translate((float) AndroidUtilities.dp(LocaleController.isRTL ? 8.0f : (float) AndroidUtilities.leftBaseline), (float) this.titleY);
            this.titleLayout.draw(canvas);
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
                StaticLayout staticLayout = this.linkLayout.get(i2);
                if (staticLayout.getLineCount() > 0) {
                    canvas.save();
                    canvas.translate((float) AndroidUtilities.dp(LocaleController.isRTL ? 8.0f : (float) AndroidUtilities.leftBaseline), (float) (this.linkY + i));
                    if (this.pressedLink == i2) {
                        canvas.drawPath(this.urlPath, Theme.linkSelectionPaint);
                    }
                    staticLayout.draw(canvas);
                    canvas.restore();
                    i += staticLayout.getLineBottom(staticLayout.getLineCount() - 1);
                }
            }
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
        if (this.checkBox.isChecked()) {
            accessibilityNodeInfo.setChecked(true);
            accessibilityNodeInfo.setCheckable(true);
        }
    }
}
