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
import org.telegram.tgnet.TLRPC.WebPage;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.CheckBox2;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.LetterDrawable;
import org.telegram.ui.Components.LinkPath;

public class SharedLinkCell extends FrameLayout {
    private CheckBox2 checkBox;
    private boolean checkingForLongPress = false;
    private SharedLinkCellDelegate delegate;
    private int description2Y = AndroidUtilities.dp(30.0f);
    private StaticLayout descriptionLayout;
    private StaticLayout descriptionLayout2;
    private TextPaint descriptionTextPaint;
    private int descriptionY = AndroidUtilities.dp(30.0f);
    private boolean drawLinkImageView;
    private LetterDrawable letterDrawable;
    private ImageReceiver linkImageView;
    private ArrayList<StaticLayout> linkLayout = new ArrayList();
    private boolean linkPreviewPressed;
    private int linkY;
    ArrayList<String> links = new ArrayList();
    private MessageObject message;
    private boolean needDivider;
    private CheckForLongPress pendingCheckForLongPress = null;
    private CheckForTap pendingCheckForTap = null;
    private int pressCount = 0;
    private int pressedLink;
    private StaticLayout titleLayout;
    private TextPaint titleTextPaint;
    private int titleY = AndroidUtilities.dp(10.0f);
    private LinkPath urlPath;

    class CheckForLongPress implements Runnable {
        public int currentPressCount;

        CheckForLongPress() {
        }

        public void run() {
            if (SharedLinkCell.this.checkingForLongPress && SharedLinkCell.this.getParent() != null && this.currentPressCount == SharedLinkCell.this.pressCount) {
                SharedLinkCell.this.checkingForLongPress = false;
                SharedLinkCell.this.performHapticFeedback(0);
                if (SharedLinkCell.this.pressedLink >= 0) {
                    SharedLinkCellDelegate access$400 = SharedLinkCell.this.delegate;
                    SharedLinkCell sharedLinkCell = SharedLinkCell.this;
                    access$400.onLinkLongPress((String) sharedLinkCell.links.get(sharedLinkCell.pressedLink));
                }
                MotionEvent obtain = MotionEvent.obtain(0, 0, 3, 0.0f, 0.0f, 0);
                SharedLinkCell.this.onTouchEvent(obtain);
                obtain.recycle();
            }
        }
    }

    private final class CheckForTap implements Runnable {
        private CheckForTap() {
        }

        public void run() {
            SharedLinkCell sharedLinkCell;
            if (SharedLinkCell.this.pendingCheckForLongPress == null) {
                sharedLinkCell = SharedLinkCell.this;
                sharedLinkCell.pendingCheckForLongPress = new CheckForLongPress();
            }
            SharedLinkCell.this.pendingCheckForLongPress.currentPressCount = SharedLinkCell.access$104(SharedLinkCell.this);
            sharedLinkCell = SharedLinkCell.this;
            sharedLinkCell.postDelayed(sharedLinkCell.pendingCheckForLongPress, (long) (ViewConfiguration.getLongPressTimeout() - ViewConfiguration.getTapTimeout()));
        }
    }

    public interface SharedLinkCellDelegate {
        boolean canPerformActions();

        void needOpenWebView(WebPage webPage);

        void onLinkLongPress(String str);
    }

    static /* synthetic */ int access$104(SharedLinkCell sharedLinkCell) {
        int i = sharedLinkCell.pressCount + 1;
        sharedLinkCell.pressCount = i;
        return i;
    }

    /* Access modifiers changed, original: protected */
    public void startCheckLongPress() {
        if (!this.checkingForLongPress) {
            this.checkingForLongPress = true;
            if (this.pendingCheckForTap == null) {
                this.pendingCheckForTap = new CheckForTap();
            }
            postDelayed(this.pendingCheckForTap, (long) ViewConfiguration.getTapTimeout());
        }
    }

    /* Access modifiers changed, original: protected */
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
        this.urlPath = new LinkPath();
        this.urlPath.setUseRoundRect(true);
        this.titleTextPaint = new TextPaint(1);
        this.titleTextPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.titleTextPaint.setColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.descriptionTextPaint = new TextPaint(1);
        this.titleTextPaint.setTextSize((float) AndroidUtilities.dp(14.0f));
        this.descriptionTextPaint.setTextSize((float) AndroidUtilities.dp(14.0f));
        setWillNotDraw(false);
        this.linkImageView = new ImageReceiver(this);
        this.linkImageView.setRoundRadius(AndroidUtilities.dp(4.0f));
        this.letterDrawable = new LetterDrawable();
        this.checkBox = new CheckBox2(context, 21);
        this.checkBox.setVisibility(4);
        this.checkBox.setColor(null, "windowBackgroundWhite", "checkboxCheck");
        this.checkBox.setDrawUnchecked(false);
        this.checkBox.setDrawBackgroundAsArc(2);
        addView(this.checkBox, LayoutHelper.createFrame(24, 24.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 44.0f, 44.0f, LocaleController.isRTL ? 44.0f : 0.0f, 0.0f));
    }

    /* Access modifiers changed, original: protected */
    /* JADX WARNING: Removed duplicated region for block: B:93:0x01da A:{Catch:{ Exception -> 0x0212 }} */
    /* JADX WARNING: Removed duplicated region for block: B:93:0x01da A:{Catch:{ Exception -> 0x0212 }} */
    /* JADX WARNING: Removed duplicated region for block: B:113:0x0236 A:{SYNTHETIC, Splitter:B:113:0x0236} */
    /* JADX WARNING: Removed duplicated region for block: B:123:0x0279  */
    /* JADX WARNING: Removed duplicated region for block: B:122:0x0274  */
    /* JADX WARNING: Removed duplicated region for block: B:126:0x0284 A:{SYNTHETIC, Splitter:B:126:0x0284} */
    /* JADX WARNING: Removed duplicated region for block: B:134:0x02b9 A:{SYNTHETIC, Splitter:B:134:0x02b9} */
    /* JADX WARNING: Removed duplicated region for block: B:142:0x02e0  */
    /* JADX WARNING: Removed duplicated region for block: B:158:0x036a  */
    /* JADX WARNING: Removed duplicated region for block: B:157:0x035f  */
    /* JADX WARNING: Removed duplicated region for block: B:161:0x0383  */
    /* JADX WARNING: Removed duplicated region for block: B:185:0x0452  */
    /* JADX WARNING: Removed duplicated region for block: B:188:0x045f  */
    @android.annotation.SuppressLint({"DrawAllocation"})
    public void onMeasure(int r28, int r29) {
        /*
        r27 = this;
        r1 = r27;
        r2 = 0;
        r1.drawLinkImageView = r2;
        r3 = 0;
        r1.descriptionLayout = r3;
        r1.titleLayout = r3;
        r1.descriptionLayout2 = r3;
        r0 = r1.linkLayout;
        r0.clear();
        r0 = r1.links;
        r0.clear();
        r0 = android.view.View.MeasureSpec.getSize(r28);
        r4 = org.telegram.messenger.AndroidUtilities.leftBaseline;
        r4 = (float) r4;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r0 = r0 - r4;
        r4 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = r0 - r4;
        r0 = r1.message;
        r5 = r0.messageOwner;
        r5 = r5.media;
        r6 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaWebPage;
        r13 = 1;
        if (r6 == 0) goto L_0x005f;
    L_0x0035:
        r5 = r5.webpage;
        r6 = r5 instanceof org.telegram.tgnet.TLRPC.TL_webPage;
        if (r6 == 0) goto L_0x005f;
    L_0x003b:
        r6 = r0.photoThumbs;
        if (r6 != 0) goto L_0x0046;
    L_0x003f:
        r6 = r5.photo;
        if (r6 == 0) goto L_0x0046;
    L_0x0043:
        r0.generateThumbs(r13);
    L_0x0046:
        r0 = r5.photo;
        if (r0 == 0) goto L_0x0052;
    L_0x004a:
        r0 = r1.message;
        r0 = r0.photoThumbs;
        if (r0 == 0) goto L_0x0052;
    L_0x0050:
        r0 = 1;
        goto L_0x0053;
    L_0x0052:
        r0 = 0;
    L_0x0053:
        r6 = r5.title;
        if (r6 != 0) goto L_0x0059;
    L_0x0057:
        r6 = r5.site_name;
    L_0x0059:
        r7 = r5.description;
        r5 = r5.url;
        r14 = r0;
        goto L_0x0063;
    L_0x005f:
        r5 = r3;
        r6 = r5;
        r7 = r6;
        r14 = 0;
    L_0x0063:
        r0 = r1.message;
        if (r0 == 0) goto L_0x0220;
    L_0x0067:
        r0 = r0.messageOwner;
        r0 = r0.entities;
        r0 = r0.isEmpty();
        if (r0 != 0) goto L_0x0220;
    L_0x0071:
        r0 = r3;
        r8 = r7;
        r7 = r6;
        r6 = 0;
    L_0x0075:
        r9 = r1.message;
        r9 = r9.messageOwner;
        r9 = r9.entities;
        r9 = r9.size();
        if (r6 >= r9) goto L_0x021c;
    L_0x0081:
        r9 = r1.message;
        r9 = r9.messageOwner;
        r9 = r9.entities;
        r9 = r9.get(r6);
        r9 = (org.telegram.tgnet.TLRPC.MessageEntity) r9;
        r10 = r9.length;
        if (r10 <= 0) goto L_0x0217;
    L_0x0091:
        r10 = r9.offset;
        if (r10 < 0) goto L_0x0217;
    L_0x0095:
        r11 = r1.message;
        r11 = r11.messageOwner;
        r11 = r11.message;
        r11 = r11.length();
        if (r10 < r11) goto L_0x00a3;
    L_0x00a1:
        goto L_0x0217;
    L_0x00a3:
        r10 = r9.offset;
        r11 = r9.length;
        r10 = r10 + r11;
        r11 = r1.message;
        r11 = r11.messageOwner;
        r11 = r11.message;
        r11 = r11.length();
        if (r10 <= r11) goto L_0x00c3;
    L_0x00b4:
        r10 = r1.message;
        r10 = r10.messageOwner;
        r10 = r10.message;
        r10 = r10.length();
        r11 = r9.offset;
        r10 = r10 - r11;
        r9.length = r10;
    L_0x00c3:
        if (r6 != 0) goto L_0x00f4;
    L_0x00c5:
        if (r5 == 0) goto L_0x00f4;
    L_0x00c7:
        r10 = r9.offset;
        if (r10 != 0) goto L_0x00d9;
    L_0x00cb:
        r10 = r9.length;
        r11 = r1.message;
        r11 = r11.messageOwner;
        r11 = r11.message;
        r11 = r11.length();
        if (r10 == r11) goto L_0x00f4;
    L_0x00d9:
        r10 = r1.message;
        r10 = r10.messageOwner;
        r10 = r10.entities;
        r10 = r10.size();
        if (r10 != r13) goto L_0x00ee;
    L_0x00e5:
        if (r8 != 0) goto L_0x00f4;
    L_0x00e7:
        r0 = r1.message;
        r0 = r0.messageOwner;
        r0 = r0.message;
        goto L_0x00f4;
    L_0x00ee:
        r0 = r1.message;
        r0 = r0.messageOwner;
        r0 = r0.message;
    L_0x00f4:
        r10 = r0;
        r0 = r9 instanceof org.telegram.tgnet.TLRPC.TL_messageEntityTextUrl;	 Catch:{ Exception -> 0x0212 }
        if (r0 != 0) goto L_0x015b;
    L_0x00f9:
        r0 = r9 instanceof org.telegram.tgnet.TLRPC.TL_messageEntityUrl;	 Catch:{ Exception -> 0x0212 }
        if (r0 == 0) goto L_0x00fe;
    L_0x00fd:
        goto L_0x015b;
    L_0x00fe:
        r0 = r9 instanceof org.telegram.tgnet.TLRPC.TL_messageEntityEmail;	 Catch:{ Exception -> 0x0212 }
        if (r0 == 0) goto L_0x0158;
    L_0x0102:
        if (r7 == 0) goto L_0x010a;
    L_0x0104:
        r0 = r7.length();	 Catch:{ Exception -> 0x0212 }
        if (r0 != 0) goto L_0x0158;
    L_0x010a:
        r0 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0212 }
        r0.<init>();	 Catch:{ Exception -> 0x0212 }
        r11 = "mailto:";
        r0.append(r11);	 Catch:{ Exception -> 0x0212 }
        r11 = r1.message;	 Catch:{ Exception -> 0x0212 }
        r11 = r11.messageOwner;	 Catch:{ Exception -> 0x0212 }
        r11 = r11.message;	 Catch:{ Exception -> 0x0212 }
        r12 = r9.offset;	 Catch:{ Exception -> 0x0212 }
        r15 = r9.offset;	 Catch:{ Exception -> 0x0212 }
        r3 = r9.length;	 Catch:{ Exception -> 0x0212 }
        r15 = r15 + r3;
        r3 = r11.substring(r12, r15);	 Catch:{ Exception -> 0x0212 }
        r0.append(r3);	 Catch:{ Exception -> 0x0212 }
        r3 = r0.toString();	 Catch:{ Exception -> 0x0212 }
        r0 = r1.message;	 Catch:{ Exception -> 0x0212 }
        r0 = r0.messageOwner;	 Catch:{ Exception -> 0x0212 }
        r0 = r0.message;	 Catch:{ Exception -> 0x0212 }
        r11 = r9.offset;	 Catch:{ Exception -> 0x0212 }
        r12 = r9.offset;	 Catch:{ Exception -> 0x0212 }
        r15 = r9.length;	 Catch:{ Exception -> 0x0212 }
        r12 = r12 + r15;
        r7 = r0.substring(r11, r12);	 Catch:{ Exception -> 0x0212 }
        r0 = r9.offset;	 Catch:{ Exception -> 0x0212 }
        if (r0 != 0) goto L_0x014f;
    L_0x0141:
        r0 = r9.length;	 Catch:{ Exception -> 0x0212 }
        r9 = r1.message;	 Catch:{ Exception -> 0x0212 }
        r9 = r9.messageOwner;	 Catch:{ Exception -> 0x0212 }
        r9 = r9.message;	 Catch:{ Exception -> 0x0212 }
        r9 = r9.length();	 Catch:{ Exception -> 0x0212 }
        if (r0 == r9) goto L_0x01d8;
    L_0x014f:
        r0 = r1.message;	 Catch:{ Exception -> 0x0212 }
        r0 = r0.messageOwner;	 Catch:{ Exception -> 0x0212 }
        r0 = r0.message;	 Catch:{ Exception -> 0x0212 }
        r8 = r0;
        goto L_0x01d8;
    L_0x0158:
        r3 = 0;
        goto L_0x01d8;
    L_0x015b:
        r0 = r9 instanceof org.telegram.tgnet.TLRPC.TL_messageEntityUrl;	 Catch:{ Exception -> 0x0212 }
        if (r0 == 0) goto L_0x0171;
    L_0x015f:
        r0 = r1.message;	 Catch:{ Exception -> 0x0212 }
        r0 = r0.messageOwner;	 Catch:{ Exception -> 0x0212 }
        r0 = r0.message;	 Catch:{ Exception -> 0x0212 }
        r3 = r9.offset;	 Catch:{ Exception -> 0x0212 }
        r11 = r9.offset;	 Catch:{ Exception -> 0x0212 }
        r12 = r9.length;	 Catch:{ Exception -> 0x0212 }
        r11 = r11 + r12;
        r0 = r0.substring(r3, r11);	 Catch:{ Exception -> 0x0212 }
        goto L_0x0173;
    L_0x0171:
        r0 = r9.url;	 Catch:{ Exception -> 0x0212 }
    L_0x0173:
        r3 = r0;
        if (r7 == 0) goto L_0x017c;
    L_0x0176:
        r0 = r7.length();	 Catch:{ Exception -> 0x0212 }
        if (r0 != 0) goto L_0x01d8;
    L_0x017c:
        r0 = android.net.Uri.parse(r3);	 Catch:{ Exception -> 0x020f }
        r0 = r0.getHost();	 Catch:{ Exception -> 0x020f }
        if (r0 != 0) goto L_0x0188;
    L_0x0186:
        r7 = r3;
        goto L_0x0189;
    L_0x0188:
        r7 = r0;
    L_0x0189:
        if (r7 == 0) goto L_0x01c0;
    L_0x018b:
        r0 = 46;
        r11 = r7.lastIndexOf(r0);	 Catch:{ Exception -> 0x0212 }
        if (r11 < 0) goto L_0x01c0;
    L_0x0193:
        r7 = r7.substring(r2, r11);	 Catch:{ Exception -> 0x0212 }
        r0 = r7.lastIndexOf(r0);	 Catch:{ Exception -> 0x0212 }
        if (r0 < 0) goto L_0x01a4;
    L_0x019d:
        r0 = r0 + 1;
        r0 = r7.substring(r0);	 Catch:{ Exception -> 0x0212 }
        r7 = r0;
    L_0x01a4:
        r0 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0212 }
        r0.<init>();	 Catch:{ Exception -> 0x0212 }
        r11 = r7.substring(r2, r13);	 Catch:{ Exception -> 0x0212 }
        r11 = r11.toUpperCase();	 Catch:{ Exception -> 0x0212 }
        r0.append(r11);	 Catch:{ Exception -> 0x0212 }
        r11 = r7.substring(r13);	 Catch:{ Exception -> 0x0212 }
        r0.append(r11);	 Catch:{ Exception -> 0x0212 }
        r0 = r0.toString();	 Catch:{ Exception -> 0x0212 }
        r7 = r0;
    L_0x01c0:
        r0 = r9.offset;	 Catch:{ Exception -> 0x0212 }
        if (r0 != 0) goto L_0x01d2;
    L_0x01c4:
        r0 = r9.length;	 Catch:{ Exception -> 0x0212 }
        r9 = r1.message;	 Catch:{ Exception -> 0x0212 }
        r9 = r9.messageOwner;	 Catch:{ Exception -> 0x0212 }
        r9 = r9.message;	 Catch:{ Exception -> 0x0212 }
        r9 = r9.length();	 Catch:{ Exception -> 0x0212 }
        if (r0 == r9) goto L_0x01d8;
    L_0x01d2:
        r0 = r1.message;	 Catch:{ Exception -> 0x0212 }
        r0 = r0.messageOwner;	 Catch:{ Exception -> 0x0212 }
        r8 = r0.message;	 Catch:{ Exception -> 0x0212 }
    L_0x01d8:
        if (r3 == 0) goto L_0x0216;
    L_0x01da:
        r0 = r3.toLowerCase();	 Catch:{ Exception -> 0x0212 }
        r9 = "http";
        r0 = r0.indexOf(r9);	 Catch:{ Exception -> 0x0212 }
        if (r0 == 0) goto L_0x0209;
    L_0x01e6:
        r0 = r3.toLowerCase();	 Catch:{ Exception -> 0x0212 }
        r9 = "mailto";
        r0 = r0.indexOf(r9);	 Catch:{ Exception -> 0x0212 }
        if (r0 == 0) goto L_0x0209;
    L_0x01f2:
        r0 = r1.links;	 Catch:{ Exception -> 0x0212 }
        r9 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0212 }
        r9.<init>();	 Catch:{ Exception -> 0x0212 }
        r11 = "http://";
        r9.append(r11);	 Catch:{ Exception -> 0x0212 }
        r9.append(r3);	 Catch:{ Exception -> 0x0212 }
        r3 = r9.toString();	 Catch:{ Exception -> 0x0212 }
        r0.add(r3);	 Catch:{ Exception -> 0x0212 }
        goto L_0x0216;
    L_0x0209:
        r0 = r1.links;	 Catch:{ Exception -> 0x0212 }
        r0.add(r3);	 Catch:{ Exception -> 0x0212 }
        goto L_0x0216;
    L_0x020f:
        r0 = move-exception;
        r7 = r3;
        goto L_0x0213;
    L_0x0212:
        r0 = move-exception;
    L_0x0213:
        org.telegram.messenger.FileLog.e(r0);
    L_0x0216:
        r0 = r10;
    L_0x0217:
        r6 = r6 + 1;
        r3 = 0;
        goto L_0x0075;
    L_0x021c:
        r12 = r0;
        r3 = r7;
        r11 = r8;
        goto L_0x0223;
    L_0x0220:
        r3 = r6;
        r11 = r7;
        r12 = 0;
    L_0x0223:
        if (r5 == 0) goto L_0x0232;
    L_0x0225:
        r0 = r1.links;
        r0 = r0.isEmpty();
        if (r0 == 0) goto L_0x0232;
    L_0x022d:
        r0 = r1.links;
        r0.add(r5);
    L_0x0232:
        r15 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
        if (r3 == 0) goto L_0x026c;
    L_0x0236:
        r6 = r1.titleTextPaint;	 Catch:{ Exception -> 0x0263 }
        r9 = 0;
        r10 = 3;
        r5 = r3;
        r7 = r4;
        r8 = r4;
        r0 = org.telegram.ui.Cells.ChatMessageCell.generateStaticLayout(r5, r6, r7, r8, r9, r10);	 Catch:{ Exception -> 0x0263 }
        r1.titleLayout = r0;	 Catch:{ Exception -> 0x0263 }
        r0 = r1.titleLayout;	 Catch:{ Exception -> 0x0263 }
        r0 = r0.getLineCount();	 Catch:{ Exception -> 0x0263 }
        if (r0 <= 0) goto L_0x0267;
    L_0x024b:
        r0 = r1.titleY;	 Catch:{ Exception -> 0x0263 }
        r5 = r1.titleLayout;	 Catch:{ Exception -> 0x0263 }
        r6 = r1.titleLayout;	 Catch:{ Exception -> 0x0263 }
        r6 = r6.getLineCount();	 Catch:{ Exception -> 0x0263 }
        r6 = r6 - r13;
        r5 = r5.getLineBottom(r6);	 Catch:{ Exception -> 0x0263 }
        r0 = r0 + r5;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r15);	 Catch:{ Exception -> 0x0263 }
        r0 = r0 + r5;
        r1.descriptionY = r0;	 Catch:{ Exception -> 0x0263 }
        goto L_0x0267;
    L_0x0263:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x0267:
        r0 = r1.letterDrawable;
        r0.setTitle(r3);
    L_0x026c:
        r0 = r1.descriptionY;
        r1.description2Y = r0;
        r0 = r1.titleLayout;
        if (r0 == 0) goto L_0x0279;
    L_0x0274:
        r0 = r0.getLineCount();
        goto L_0x027a;
    L_0x0279:
        r0 = 0;
    L_0x027a:
        r0 = 4 - r0;
        r3 = java.lang.Math.max(r13, r0);
        r16 = NUM; // 0x40a00000 float:5.0 double:5.356796015E-315;
        if (r11 == 0) goto L_0x02b5;
    L_0x0284:
        r6 = r1.descriptionTextPaint;	 Catch:{ Exception -> 0x02b1 }
        r9 = 0;
        r5 = r11;
        r7 = r4;
        r8 = r4;
        r10 = r3;
        r0 = org.telegram.ui.Cells.ChatMessageCell.generateStaticLayout(r5, r6, r7, r8, r9, r10);	 Catch:{ Exception -> 0x02b1 }
        r1.descriptionLayout = r0;	 Catch:{ Exception -> 0x02b1 }
        r0 = r1.descriptionLayout;	 Catch:{ Exception -> 0x02b1 }
        r0 = r0.getLineCount();	 Catch:{ Exception -> 0x02b1 }
        if (r0 <= 0) goto L_0x02b5;
    L_0x0299:
        r0 = r1.descriptionY;	 Catch:{ Exception -> 0x02b1 }
        r5 = r1.descriptionLayout;	 Catch:{ Exception -> 0x02b1 }
        r6 = r1.descriptionLayout;	 Catch:{ Exception -> 0x02b1 }
        r6 = r6.getLineCount();	 Catch:{ Exception -> 0x02b1 }
        r6 = r6 - r13;
        r5 = r5.getLineBottom(r6);	 Catch:{ Exception -> 0x02b1 }
        r0 = r0 + r5;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r16);	 Catch:{ Exception -> 0x02b1 }
        r0 = r0 + r5;
        r1.description2Y = r0;	 Catch:{ Exception -> 0x02b1 }
        goto L_0x02b5;
    L_0x02b1:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x02b5:
        r17 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
        if (r12 == 0) goto L_0x02d8;
    L_0x02b9:
        r6 = r1.descriptionTextPaint;	 Catch:{ Exception -> 0x02d4 }
        r9 = 0;
        r5 = r12;
        r7 = r4;
        r8 = r4;
        r10 = r3;
        r0 = org.telegram.ui.Cells.ChatMessageCell.generateStaticLayout(r5, r6, r7, r8, r9, r10);	 Catch:{ Exception -> 0x02d4 }
        r1.descriptionLayout2 = r0;	 Catch:{ Exception -> 0x02d4 }
        r0 = r1.descriptionLayout;	 Catch:{ Exception -> 0x02d4 }
        if (r0 == 0) goto L_0x02d8;
    L_0x02ca:
        r0 = r1.description2Y;	 Catch:{ Exception -> 0x02d4 }
        r3 = org.telegram.messenger.AndroidUtilities.dp(r17);	 Catch:{ Exception -> 0x02d4 }
        r0 = r0 + r3;
        r1.description2Y = r0;	 Catch:{ Exception -> 0x02d4 }
        goto L_0x02d8;
    L_0x02d4:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x02d8:
        r0 = r1.links;
        r0 = r0.isEmpty();
        if (r0 != 0) goto L_0x0355;
    L_0x02e0:
        r3 = 0;
    L_0x02e1:
        r0 = r1.links;
        r0 = r0.size();
        if (r3 >= r0) goto L_0x0355;
    L_0x02e9:
        r0 = r1.links;	 Catch:{ Exception -> 0x034e }
        r0 = r0.get(r3);	 Catch:{ Exception -> 0x034e }
        r0 = (java.lang.String) r0;	 Catch:{ Exception -> 0x034e }
        r5 = r1.descriptionTextPaint;	 Catch:{ Exception -> 0x034e }
        r5 = r5.measureText(r0);	 Catch:{ Exception -> 0x034e }
        r5 = (double) r5;	 Catch:{ Exception -> 0x034e }
        r5 = java.lang.Math.ceil(r5);	 Catch:{ Exception -> 0x034e }
        r5 = (int) r5;	 Catch:{ Exception -> 0x034e }
        r6 = 10;
        r7 = 32;
        r0 = r0.replace(r6, r7);	 Catch:{ Exception -> 0x034e }
        r6 = r1.descriptionTextPaint;	 Catch:{ Exception -> 0x034e }
        r5 = java.lang.Math.min(r5, r4);	 Catch:{ Exception -> 0x034e }
        r5 = (float) r5;	 Catch:{ Exception -> 0x034e }
        r7 = android.text.TextUtils.TruncateAt.MIDDLE;	 Catch:{ Exception -> 0x034e }
        r6 = android.text.TextUtils.ellipsize(r0, r6, r5, r7);	 Catch:{ Exception -> 0x034e }
        r0 = new android.text.StaticLayout;	 Catch:{ Exception -> 0x034e }
        r7 = r1.descriptionTextPaint;	 Catch:{ Exception -> 0x034e }
        r9 = android.text.Layout.Alignment.ALIGN_NORMAL;	 Catch:{ Exception -> 0x034e }
        r10 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r11 = 0;
        r12 = 0;
        r5 = r0;
        r8 = r4;
        r5.<init>(r6, r7, r8, r9, r10, r11, r12);	 Catch:{ Exception -> 0x034e }
        r5 = r1.description2Y;	 Catch:{ Exception -> 0x034e }
        r1.linkY = r5;	 Catch:{ Exception -> 0x034e }
        r5 = r1.descriptionLayout2;	 Catch:{ Exception -> 0x034e }
        if (r5 == 0) goto L_0x0348;
    L_0x0329:
        r5 = r1.descriptionLayout2;	 Catch:{ Exception -> 0x034e }
        r5 = r5.getLineCount();	 Catch:{ Exception -> 0x034e }
        if (r5 == 0) goto L_0x0348;
    L_0x0331:
        r5 = r1.linkY;	 Catch:{ Exception -> 0x034e }
        r6 = r1.descriptionLayout2;	 Catch:{ Exception -> 0x034e }
        r7 = r1.descriptionLayout2;	 Catch:{ Exception -> 0x034e }
        r7 = r7.getLineCount();	 Catch:{ Exception -> 0x034e }
        r7 = r7 - r13;
        r6 = r6.getLineBottom(r7);	 Catch:{ Exception -> 0x034e }
        r7 = org.telegram.messenger.AndroidUtilities.dp(r16);	 Catch:{ Exception -> 0x034e }
        r6 = r6 + r7;
        r5 = r5 + r6;
        r1.linkY = r5;	 Catch:{ Exception -> 0x034e }
    L_0x0348:
        r5 = r1.linkLayout;	 Catch:{ Exception -> 0x034e }
        r5.add(r0);	 Catch:{ Exception -> 0x034e }
        goto L_0x0352;
    L_0x034e:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x0352:
        r3 = r3 + 1;
        goto L_0x02e1;
    L_0x0355:
        r0 = NUM; // 0x42500000 float:52.0 double:5.496673668E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r3 = org.telegram.messenger.LocaleController.isRTL;
        if (r3 == 0) goto L_0x036a;
    L_0x035f:
        r3 = android.view.View.MeasureSpec.getSize(r28);
        r4 = org.telegram.messenger.AndroidUtilities.dp(r17);
        r3 = r3 - r4;
        r3 = r3 - r0;
        goto L_0x036e;
    L_0x036a:
        r3 = org.telegram.messenger.AndroidUtilities.dp(r17);
    L_0x036e:
        r4 = r1.letterDrawable;
        r5 = NUM; // 0x41300000 float:11.0 double:5.4034219E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r7 = r3 + r0;
        r8 = NUM; // 0x427CLASSNAME float:63.0 double:5.510920465E-315;
        r8 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r4.setBounds(r3, r6, r7, r8);
        if (r14 == 0) goto L_0x03fb;
    L_0x0383:
        r4 = r1.message;
        r4 = r4.photoThumbs;
        r4 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r4, r0, r13);
        r6 = r1.message;
        r6 = r6.photoThumbs;
        r7 = 80;
        r6 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r6, r7);
        if (r6 != r4) goto L_0x0398;
    L_0x0397:
        r6 = 0;
    L_0x0398:
        r7 = -1;
        r4.size = r7;
        if (r6 == 0) goto L_0x039f;
    L_0x039d:
        r6.size = r7;
    L_0x039f:
        r7 = r1.linkImageView;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r7.setImageCoords(r3, r5, r0, r0);
        org.telegram.messenger.FileLoader.getAttachFileName(r4);
        r3 = java.util.Locale.US;
        r5 = 2;
        r7 = new java.lang.Object[r5];
        r8 = java.lang.Integer.valueOf(r0);
        r7[r2] = r8;
        r8 = java.lang.Integer.valueOf(r0);
        r7[r13] = r8;
        r8 = "%d_%d";
        r20 = java.lang.String.format(r3, r8, r7);
        r3 = java.util.Locale.US;
        r5 = new java.lang.Object[r5];
        r7 = java.lang.Integer.valueOf(r0);
        r5[r2] = r7;
        r0 = java.lang.Integer.valueOf(r0);
        r5[r13] = r0;
        r0 = "%d_%d_b";
        r22 = java.lang.String.format(r3, r0, r5);
        r0 = r1.linkImageView;
        r3 = r1.message;
        r3 = r3.photoThumbsObject;
        r19 = org.telegram.messenger.ImageLocation.getForObject(r4, r3);
        r3 = r1.message;
        r3 = r3.photoThumbsObject;
        r21 = org.telegram.messenger.ImageLocation.getForObject(r6, r3);
        r23 = 0;
        r24 = 0;
        r3 = r1.message;
        r26 = 0;
        r18 = r0;
        r25 = r3;
        r18.setImage(r19, r20, r21, r22, r23, r24, r25, r26);
        r1.drawLinkImageView = r13;
    L_0x03fb:
        r0 = r1.titleLayout;
        if (r0 == 0) goto L_0x0417;
    L_0x03ff:
        r0 = r0.getLineCount();
        if (r0 == 0) goto L_0x0417;
    L_0x0405:
        r0 = r1.titleLayout;
        r3 = r0.getLineCount();
        r3 = r3 - r13;
        r0 = r0.getLineBottom(r3);
        r3 = org.telegram.messenger.AndroidUtilities.dp(r15);
        r0 = r0 + r3;
        r0 = r0 + r2;
        goto L_0x0418;
    L_0x0417:
        r0 = 0;
    L_0x0418:
        r3 = r1.descriptionLayout;
        if (r3 == 0) goto L_0x0433;
    L_0x041c:
        r3 = r3.getLineCount();
        if (r3 == 0) goto L_0x0433;
    L_0x0422:
        r3 = r1.descriptionLayout;
        r4 = r3.getLineCount();
        r4 = r4 - r13;
        r3 = r3.getLineBottom(r4);
        r4 = org.telegram.messenger.AndroidUtilities.dp(r16);
        r3 = r3 + r4;
        r0 = r0 + r3;
    L_0x0433:
        r3 = r1.descriptionLayout2;
        if (r3 == 0) goto L_0x0457;
    L_0x0437:
        r3 = r3.getLineCount();
        if (r3 == 0) goto L_0x0457;
    L_0x043d:
        r3 = r1.descriptionLayout2;
        r4 = r3.getLineCount();
        r4 = r4 - r13;
        r3 = r3.getLineBottom(r4);
        r4 = org.telegram.messenger.AndroidUtilities.dp(r16);
        r3 = r3 + r4;
        r0 = r0 + r3;
        r3 = r1.descriptionLayout;
        if (r3 == 0) goto L_0x0457;
    L_0x0452:
        r3 = org.telegram.messenger.AndroidUtilities.dp(r17);
        r0 = r0 + r3;
    L_0x0457:
        r3 = r1.linkLayout;
        r3 = r3.size();
        if (r2 >= r3) goto L_0x047a;
    L_0x045f:
        r3 = r1.linkLayout;
        r3 = r3.get(r2);
        r3 = (android.text.StaticLayout) r3;
        r4 = r3.getLineCount();
        if (r4 <= 0) goto L_0x0477;
    L_0x046d:
        r4 = r3.getLineCount();
        r4 = r4 - r13;
        r3 = r3.getLineBottom(r4);
        r0 = r0 + r3;
    L_0x0477:
        r2 = r2 + 1;
        goto L_0x0457;
    L_0x047a:
        r2 = r1.checkBox;
        r3 = NUM; // 0x41CLASSNAME float:24.0 double:5.450047783E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r5 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r4 = android.view.View.MeasureSpec.makeMeasureSpec(r4, r5);
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r3 = android.view.View.MeasureSpec.makeMeasureSpec(r3, r5);
        r2.measure(r4, r3);
        r2 = android.view.View.MeasureSpec.getSize(r28);
        r3 = NUM; // 0x42980000 float:76.0 double:5.51998661E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r4 = NUM; // 0x41880000 float:17.0 double:5.431915495E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r0 = r0 + r4;
        r0 = java.lang.Math.max(r3, r0);
        r3 = r1.needDivider;
        r0 = r0 + r3;
        r1.setMeasuredDimension(r2, r0);
        return;
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

    /* Access modifiers changed, original: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.drawLinkImageView) {
            this.linkImageView.onDetachedFromWindow();
        }
    }

    /* Access modifiers changed, original: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.drawLinkImageView) {
            this.linkImageView.onAttachedToWindow();
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:67:0x010a  */
    /* JADX WARNING: Removed duplicated region for block: B:67:0x010a  */
    /* JADX WARNING: Removed duplicated region for block: B:79:? A:{SYNTHETIC, RETURN, SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:71:0x0114  */
    /* JADX WARNING: Removed duplicated region for block: B:71:0x0114  */
    /* JADX WARNING: Removed duplicated region for block: B:79:? A:{SYNTHETIC, RETURN, SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:79:? A:{SYNTHETIC, RETURN, SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:71:0x0114  */
    public boolean onTouchEvent(android.view.MotionEvent r12) {
        /*
        r11 = this;
        r0 = r11.message;
        r1 = 1;
        r2 = 0;
        if (r0 == 0) goto L_0x010e;
    L_0x0006:
        r0 = r11.linkLayout;
        r0 = r0.isEmpty();
        if (r0 != 0) goto L_0x010e;
    L_0x000e:
        r0 = r11.delegate;
        if (r0 == 0) goto L_0x010e;
    L_0x0012:
        r0 = r0.canPerformActions();
        if (r0 == 0) goto L_0x010e;
    L_0x0018:
        r0 = r12.getAction();
        if (r0 == 0) goto L_0x0035;
    L_0x001e:
        r0 = r11.linkPreviewPressed;
        if (r0 == 0) goto L_0x0029;
    L_0x0022:
        r0 = r12.getAction();
        if (r0 != r1) goto L_0x0029;
    L_0x0028:
        goto L_0x0035;
    L_0x0029:
        r0 = r12.getAction();
        r3 = 3;
        if (r0 != r3) goto L_0x0111;
    L_0x0030:
        r11.resetPressedLink();
        goto L_0x0111;
    L_0x0035:
        r0 = r12.getX();
        r0 = (int) r0;
        r3 = r12.getY();
        r3 = (int) r3;
        r4 = 0;
        r5 = 0;
    L_0x0041:
        r6 = r11.linkLayout;
        r6 = r6.size();
        if (r4 >= r6) goto L_0x0106;
    L_0x0049:
        r6 = r11.linkLayout;
        r6 = r6.get(r4);
        r6 = (android.text.StaticLayout) r6;
        r7 = r6.getLineCount();
        if (r7 <= 0) goto L_0x0102;
    L_0x0057:
        r7 = r6.getLineCount();
        r7 = r7 - r1;
        r7 = r6.getLineBottom(r7);
        r8 = org.telegram.messenger.LocaleController.isRTL;
        if (r8 == 0) goto L_0x0067;
    L_0x0064:
        r8 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        goto L_0x006a;
    L_0x0067:
        r8 = org.telegram.messenger.AndroidUtilities.leftBaseline;
        r8 = (float) r8;
    L_0x006a:
        r8 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r9 = (float) r0;
        r8 = (float) r8;
        r10 = r6.getLineLeft(r2);
        r10 = r10 + r8;
        r10 = (r9 > r10 ? 1 : (r9 == r10 ? 0 : -1));
        if (r10 < 0) goto L_0x0101;
    L_0x0079:
        r10 = r6.getLineWidth(r2);
        r8 = r8 + r10;
        r8 = (r9 > r8 ? 1 : (r9 == r8 ? 0 : -1));
        if (r8 > 0) goto L_0x0101;
    L_0x0082:
        r8 = r11.linkY;
        r9 = r8 + r5;
        if (r3 < r9) goto L_0x0101;
    L_0x0088:
        r8 = r8 + r5;
        r8 = r8 + r7;
        if (r3 > r8) goto L_0x0101;
    L_0x008c:
        r0 = r12.getAction();
        if (r0 != 0) goto L_0x00b5;
    L_0x0092:
        r11.resetPressedLink();
        r11.pressedLink = r4;
        r11.linkPreviewPressed = r1;
        r11.startCheckLongPress();
        r0 = r11.urlPath;	 Catch:{ Exception -> 0x00b0 }
        r3 = 0;
        r0.setCurrentLayout(r6, r2, r3);	 Catch:{ Exception -> 0x00b0 }
        r0 = r6.getText();	 Catch:{ Exception -> 0x00b0 }
        r0 = r0.length();	 Catch:{ Exception -> 0x00b0 }
        r3 = r11.urlPath;	 Catch:{ Exception -> 0x00b0 }
        r6.getSelectionPath(r2, r0, r3);	 Catch:{ Exception -> 0x00b0 }
        goto L_0x00fc;
    L_0x00b0:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
        goto L_0x00fc;
    L_0x00b5:
        r0 = r11.linkPreviewPressed;
        if (r0 == 0) goto L_0x00ff;
    L_0x00b9:
        r0 = r11.pressedLink;	 Catch:{ Exception -> 0x00f5 }
        if (r0 != 0) goto L_0x00ce;
    L_0x00bd:
        r0 = r11.message;	 Catch:{ Exception -> 0x00f5 }
        r0 = r0.messageOwner;	 Catch:{ Exception -> 0x00f5 }
        r0 = r0.media;	 Catch:{ Exception -> 0x00f5 }
        if (r0 == 0) goto L_0x00ce;
    L_0x00c5:
        r0 = r11.message;	 Catch:{ Exception -> 0x00f5 }
        r0 = r0.messageOwner;	 Catch:{ Exception -> 0x00f5 }
        r0 = r0.media;	 Catch:{ Exception -> 0x00f5 }
        r0 = r0.webpage;	 Catch:{ Exception -> 0x00f5 }
        goto L_0x00cf;
    L_0x00ce:
        r0 = 0;
    L_0x00cf:
        if (r0 == 0) goto L_0x00e3;
    L_0x00d1:
        r3 = r0.embed_url;	 Catch:{ Exception -> 0x00f5 }
        if (r3 == 0) goto L_0x00e3;
    L_0x00d5:
        r3 = r0.embed_url;	 Catch:{ Exception -> 0x00f5 }
        r3 = r3.length();	 Catch:{ Exception -> 0x00f5 }
        if (r3 == 0) goto L_0x00e3;
    L_0x00dd:
        r3 = r11.delegate;	 Catch:{ Exception -> 0x00f5 }
        r3.needOpenWebView(r0);	 Catch:{ Exception -> 0x00f5 }
        goto L_0x00f9;
    L_0x00e3:
        r0 = r11.getContext();	 Catch:{ Exception -> 0x00f5 }
        r3 = r11.links;	 Catch:{ Exception -> 0x00f5 }
        r4 = r11.pressedLink;	 Catch:{ Exception -> 0x00f5 }
        r3 = r3.get(r4);	 Catch:{ Exception -> 0x00f5 }
        r3 = (java.lang.String) r3;	 Catch:{ Exception -> 0x00f5 }
        org.telegram.messenger.browser.Browser.openUrl(r0, r3);	 Catch:{ Exception -> 0x00f5 }
        goto L_0x00f9;
    L_0x00f5:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x00f9:
        r11.resetPressedLink();
    L_0x00fc:
        r0 = 1;
        r3 = 1;
        goto L_0x0108;
    L_0x00ff:
        r0 = 1;
        goto L_0x0107;
    L_0x0101:
        r5 = r5 + r7;
    L_0x0102:
        r4 = r4 + 1;
        goto L_0x0041;
    L_0x0106:
        r0 = 0;
    L_0x0107:
        r3 = 0;
    L_0x0108:
        if (r0 != 0) goto L_0x0112;
    L_0x010a:
        r11.resetPressedLink();
        goto L_0x0112;
    L_0x010e:
        r11.resetPressedLink();
    L_0x0111:
        r3 = 0;
    L_0x0112:
        if (r3 != 0) goto L_0x011c;
    L_0x0114:
        r12 = super.onTouchEvent(r12);
        if (r12 == 0) goto L_0x011b;
    L_0x011a:
        goto L_0x011c;
    L_0x011b:
        r1 = 0;
    L_0x011c:
        return r1;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.SharedLinkCell.onTouchEvent(android.view.MotionEvent):boolean");
    }

    public String getLink(int i) {
        return (i < 0 || i >= this.links.size()) ? null : (String) this.links.get(i);
    }

    /* Access modifiers changed, original: protected */
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

    /* Access modifiers changed, original: protected */
    public void onDraw(Canvas canvas) {
        if (this.titleLayout != null) {
            canvas.save();
            canvas.translate((float) AndroidUtilities.dp(LocaleController.isRTL ? 8.0f : (float) AndroidUtilities.leftBaseline), (float) this.titleY);
            this.titleLayout.draw(canvas);
            canvas.restore();
        }
        String str = "windowBackgroundWhiteBlackText";
        if (this.descriptionLayout != null) {
            this.descriptionTextPaint.setColor(Theme.getColor(str));
            canvas.save();
            canvas.translate((float) AndroidUtilities.dp(LocaleController.isRTL ? 8.0f : (float) AndroidUtilities.leftBaseline), (float) this.descriptionY);
            this.descriptionLayout.draw(canvas);
            canvas.restore();
        }
        if (this.descriptionLayout2 != null) {
            this.descriptionTextPaint.setColor(Theme.getColor(str));
            canvas.save();
            canvas.translate((float) AndroidUtilities.dp(LocaleController.isRTL ? 8.0f : (float) AndroidUtilities.leftBaseline), (float) this.description2Y);
            this.descriptionLayout2.draw(canvas);
            canvas.restore();
        }
        if (!this.linkLayout.isEmpty()) {
            this.descriptionTextPaint.setColor(Theme.getColor("windowBackgroundWhiteLinkText"));
            int i = 0;
            for (int i2 = 0; i2 < this.linkLayout.size(); i2++) {
                StaticLayout staticLayout = (StaticLayout) this.linkLayout.get(i2);
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
        StringBuilder stringBuilder = new StringBuilder();
        StaticLayout staticLayout = this.titleLayout;
        if (staticLayout != null) {
            stringBuilder.append(staticLayout.getText());
        }
        String str = ", ";
        if (this.descriptionLayout != null) {
            stringBuilder.append(str);
            stringBuilder.append(this.descriptionLayout.getText());
        }
        if (this.descriptionLayout2 != null) {
            stringBuilder.append(str);
            stringBuilder.append(this.descriptionLayout2.getText());
        }
        if (this.checkBox.isChecked()) {
            accessibilityNodeInfo.setChecked(true);
            accessibilityNodeInfo.setCheckable(true);
        }
    }
}
