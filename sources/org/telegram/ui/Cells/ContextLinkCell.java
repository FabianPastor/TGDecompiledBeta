package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.text.StaticLayout;
import android.text.TextUtils;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.AccelerateInterpolator;
import java.io.File;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.DownloadController.FileDownloadProgressListener;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.WebFile;
import org.telegram.tgnet.TLRPC.BotInlineMessage;
import org.telegram.tgnet.TLRPC.BotInlineResult;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.MessageMedia;
import org.telegram.tgnet.TLRPC.Peer;
import org.telegram.tgnet.TLRPC.Photo;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.TL_botInlineMessageMediaGeo;
import org.telegram.tgnet.TLRPC.TL_botInlineMessageMediaVenue;
import org.telegram.tgnet.TLRPC.TL_document;
import org.telegram.tgnet.TLRPC.TL_documentAttributeAudio;
import org.telegram.tgnet.TLRPC.TL_documentAttributeFilename;
import org.telegram.tgnet.TLRPC.TL_message;
import org.telegram.tgnet.TLRPC.TL_messageMediaDocument;
import org.telegram.tgnet.TLRPC.TL_peerUser;
import org.telegram.tgnet.TLRPC.TL_webDocument;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LetterDrawable;
import org.telegram.ui.Components.RadialProgress2;
import org.telegram.ui.PhotoViewer;

public class ContextLinkCell extends View implements FileDownloadProgressListener {
    private static final int DOCUMENT_ATTACH_TYPE_AUDIO = 3;
    private static final int DOCUMENT_ATTACH_TYPE_DOCUMENT = 1;
    private static final int DOCUMENT_ATTACH_TYPE_GEO = 8;
    private static final int DOCUMENT_ATTACH_TYPE_GIF = 2;
    private static final int DOCUMENT_ATTACH_TYPE_MUSIC = 5;
    private static final int DOCUMENT_ATTACH_TYPE_NONE = 0;
    private static final int DOCUMENT_ATTACH_TYPE_PHOTO = 7;
    private static final int DOCUMENT_ATTACH_TYPE_STICKER = 6;
    private static final int DOCUMENT_ATTACH_TYPE_VIDEO = 4;
    private static AccelerateInterpolator interpolator = new AccelerateInterpolator(0.5f);
    private int TAG;
    private boolean buttonPressed;
    private int buttonState;
    private boolean canPreviewGif;
    private int currentAccount = UserConfig.selectedAccount;
    private MessageObject currentMessageObject;
    private PhotoSize currentPhotoObject;
    private ContextLinkCellDelegate delegate;
    private StaticLayout descriptionLayout;
    private int descriptionY = AndroidUtilities.dp(27.0f);
    private Document documentAttach;
    private int documentAttachType;
    private boolean drawLinkImageView;
    private BotInlineResult inlineResult;
    private long lastUpdateTime;
    private LetterDrawable letterDrawable;
    private ImageReceiver linkImageView = new ImageReceiver(this);
    private StaticLayout linkLayout;
    private int linkY;
    private boolean mediaWebpage;
    private boolean needDivider;
    private boolean needShadow;
    private Object parentObject;
    private Photo photoAttach;
    private RadialProgress2 radialProgress;
    private float scale;
    private boolean scaled;
    private StaticLayout titleLayout;
    private int titleY = AndroidUtilities.dp(7.0f);

    public interface ContextLinkCellDelegate {
        void didPressedImage(ContextLinkCell contextLinkCell);
    }

    public void onProgressUpload(String str, float f, boolean z) {
    }

    public ContextLinkCell(Context context) {
        super(context);
        this.linkImageView.setLayerNum(1);
        this.linkImageView.setUseSharedAnimationQueue(true);
        this.letterDrawable = new LetterDrawable();
        this.radialProgress = new RadialProgress2(this);
        this.TAG = DownloadController.getInstance(this.currentAccount).generateObserverTag();
        setFocusable(true);
    }

    /* Access modifiers changed, original: protected */
    /* JADX WARNING: Removed duplicated region for block: B:110:0x0284  */
    /* JADX WARNING: Removed duplicated region for block: B:110:0x0284  */
    /* JADX WARNING: Removed duplicated region for block: B:92:0x0217  */
    /* JADX WARNING: Removed duplicated region for block: B:106:0x0268  */
    /* JADX WARNING: Removed duplicated region for block: B:105:0x024a  */
    /* JADX WARNING: Removed duplicated region for block: B:151:0x0356  */
    /* JADX WARNING: Removed duplicated region for block: B:147:0x02fb  */
    /* JADX WARNING: Removed duplicated region for block: B:155:0x0366  */
    /* JADX WARNING: Removed duplicated region for block: B:154:0x0364  */
    /* JADX WARNING: Removed duplicated region for block: B:164:0x03db  */
    /* JADX WARNING: Removed duplicated region for block: B:158:0x036e  */
    /* JADX WARNING: Removed duplicated region for block: B:183:0x04dd  */
    /* JADX WARNING: Removed duplicated region for block: B:179:0x04a1  */
    /* JADX WARNING: Removed duplicated region for block: B:111:0x028b  */
    /* JADX WARNING: Removed duplicated region for block: B:77:0x01dc  */
    /* JADX WARNING: Removed duplicated region for block: B:114:0x0292  */
    /* JADX WARNING: Removed duplicated region for block: B:131:0x02ce  */
    /* JADX WARNING: Removed duplicated region for block: B:128:0x02c2  */
    /* JADX WARNING: Removed duplicated region for block: B:147:0x02fb  */
    /* JADX WARNING: Removed duplicated region for block: B:151:0x0356  */
    /* JADX WARNING: Removed duplicated region for block: B:154:0x0364  */
    /* JADX WARNING: Removed duplicated region for block: B:155:0x0366  */
    /* JADX WARNING: Removed duplicated region for block: B:158:0x036e  */
    /* JADX WARNING: Removed duplicated region for block: B:164:0x03db  */
    /* JADX WARNING: Removed duplicated region for block: B:179:0x04a1  */
    /* JADX WARNING: Removed duplicated region for block: B:183:0x04dd  */
    /* JADX WARNING: Removed duplicated region for block: B:67:0x01ba  */
    /* JADX WARNING: Removed duplicated region for block: B:55:0x0175  */
    /* JADX WARNING: Removed duplicated region for block: B:77:0x01dc  */
    /* JADX WARNING: Removed duplicated region for block: B:111:0x028b  */
    /* JADX WARNING: Removed duplicated region for block: B:114:0x0292  */
    /* JADX WARNING: Removed duplicated region for block: B:128:0x02c2  */
    /* JADX WARNING: Removed duplicated region for block: B:131:0x02ce  */
    /* JADX WARNING: Removed duplicated region for block: B:151:0x0356  */
    /* JADX WARNING: Removed duplicated region for block: B:147:0x02fb  */
    /* JADX WARNING: Removed duplicated region for block: B:155:0x0366  */
    /* JADX WARNING: Removed duplicated region for block: B:154:0x0364  */
    /* JADX WARNING: Removed duplicated region for block: B:164:0x03db  */
    /* JADX WARNING: Removed duplicated region for block: B:158:0x036e  */
    /* JADX WARNING: Removed duplicated region for block: B:183:0x04dd  */
    /* JADX WARNING: Removed duplicated region for block: B:179:0x04a1  */
    /* JADX WARNING: Removed duplicated region for block: B:18:0x0064  */
    /* JADX WARNING: Removed duplicated region for block: B:55:0x0175  */
    /* JADX WARNING: Removed duplicated region for block: B:67:0x01ba  */
    /* JADX WARNING: Removed duplicated region for block: B:111:0x028b  */
    /* JADX WARNING: Removed duplicated region for block: B:77:0x01dc  */
    /* JADX WARNING: Removed duplicated region for block: B:114:0x0292  */
    /* JADX WARNING: Removed duplicated region for block: B:131:0x02ce  */
    /* JADX WARNING: Removed duplicated region for block: B:128:0x02c2  */
    /* JADX WARNING: Removed duplicated region for block: B:147:0x02fb  */
    /* JADX WARNING: Removed duplicated region for block: B:151:0x0356  */
    /* JADX WARNING: Removed duplicated region for block: B:154:0x0364  */
    /* JADX WARNING: Removed duplicated region for block: B:155:0x0366  */
    /* JADX WARNING: Removed duplicated region for block: B:158:0x036e  */
    /* JADX WARNING: Removed duplicated region for block: B:164:0x03db  */
    /* JADX WARNING: Removed duplicated region for block: B:179:0x04a1  */
    /* JADX WARNING: Removed duplicated region for block: B:183:0x04dd  */
    /* JADX WARNING: Removed duplicated region for block: B:43:0x012b A:{SYNTHETIC, Splitter:B:43:0x012b} */
    /* JADX WARNING: Removed duplicated region for block: B:67:0x01ba  */
    /* JADX WARNING: Removed duplicated region for block: B:55:0x0175  */
    /* JADX WARNING: Removed duplicated region for block: B:77:0x01dc  */
    /* JADX WARNING: Removed duplicated region for block: B:111:0x028b  */
    /* JADX WARNING: Removed duplicated region for block: B:114:0x0292  */
    /* JADX WARNING: Removed duplicated region for block: B:128:0x02c2  */
    /* JADX WARNING: Removed duplicated region for block: B:131:0x02ce  */
    /* JADX WARNING: Removed duplicated region for block: B:151:0x0356  */
    /* JADX WARNING: Removed duplicated region for block: B:147:0x02fb  */
    /* JADX WARNING: Removed duplicated region for block: B:155:0x0366  */
    /* JADX WARNING: Removed duplicated region for block: B:154:0x0364  */
    /* JADX WARNING: Removed duplicated region for block: B:164:0x03db  */
    /* JADX WARNING: Removed duplicated region for block: B:158:0x036e  */
    /* JADX WARNING: Removed duplicated region for block: B:183:0x04dd  */
    /* JADX WARNING: Removed duplicated region for block: B:179:0x04a1  */
    /* JADX WARNING: Removed duplicated region for block: B:43:0x012b A:{SYNTHETIC, Splitter:B:43:0x012b} */
    /* JADX WARNING: Removed duplicated region for block: B:55:0x0175  */
    /* JADX WARNING: Removed duplicated region for block: B:67:0x01ba  */
    /* JADX WARNING: Removed duplicated region for block: B:111:0x028b  */
    /* JADX WARNING: Removed duplicated region for block: B:77:0x01dc  */
    /* JADX WARNING: Removed duplicated region for block: B:114:0x0292  */
    /* JADX WARNING: Removed duplicated region for block: B:131:0x02ce  */
    /* JADX WARNING: Removed duplicated region for block: B:128:0x02c2  */
    /* JADX WARNING: Removed duplicated region for block: B:147:0x02fb  */
    /* JADX WARNING: Removed duplicated region for block: B:151:0x0356  */
    /* JADX WARNING: Removed duplicated region for block: B:154:0x0364  */
    /* JADX WARNING: Removed duplicated region for block: B:155:0x0366  */
    /* JADX WARNING: Removed duplicated region for block: B:158:0x036e  */
    /* JADX WARNING: Removed duplicated region for block: B:164:0x03db  */
    /* JADX WARNING: Removed duplicated region for block: B:179:0x04a1  */
    /* JADX WARNING: Removed duplicated region for block: B:183:0x04dd  */
    /* JADX WARNING: Removed duplicated region for block: B:67:0x01ba  */
    /* JADX WARNING: Removed duplicated region for block: B:55:0x0175  */
    /* JADX WARNING: Removed duplicated region for block: B:77:0x01dc  */
    /* JADX WARNING: Removed duplicated region for block: B:111:0x028b  */
    /* JADX WARNING: Removed duplicated region for block: B:114:0x0292  */
    /* JADX WARNING: Removed duplicated region for block: B:128:0x02c2  */
    /* JADX WARNING: Removed duplicated region for block: B:131:0x02ce  */
    /* JADX WARNING: Removed duplicated region for block: B:151:0x0356  */
    /* JADX WARNING: Removed duplicated region for block: B:147:0x02fb  */
    /* JADX WARNING: Removed duplicated region for block: B:155:0x0366  */
    /* JADX WARNING: Removed duplicated region for block: B:154:0x0364  */
    /* JADX WARNING: Removed duplicated region for block: B:164:0x03db  */
    /* JADX WARNING: Removed duplicated region for block: B:158:0x036e  */
    /* JADX WARNING: Removed duplicated region for block: B:183:0x04dd  */
    /* JADX WARNING: Removed duplicated region for block: B:179:0x04a1  */
    /* JADX WARNING: Missing block: B:72:0x01d2, code skipped:
            if (r3 == r1.currentPhotoObject) goto L_0x01d4;
     */
    @android.annotation.SuppressLint({"DrawAllocation"})
    public void onMeasure(int r38, int r39) {
        /*
        r37 = this;
        r1 = r37;
        r2 = 0;
        r1.drawLinkImageView = r2;
        r3 = 0;
        r1.descriptionLayout = r3;
        r1.titleLayout = r3;
        r1.linkLayout = r3;
        r1.currentPhotoObject = r3;
        r0 = NUM; // 0x41d80000 float:27.0 double:5.457818764E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r1.linkY = r0;
        r0 = r1.inlineResult;
        r4 = NUM; // 0x42CLASSNAME float:100.0 double:5.53552857E-315;
        if (r0 != 0) goto L_0x002c;
    L_0x001c:
        r0 = r1.documentAttach;
        if (r0 != 0) goto L_0x002c;
    L_0x0020:
        r0 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r2 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r1.setMeasuredDimension(r0, r2);
        return;
    L_0x002c:
        r5 = android.view.View.MeasureSpec.getSize(r38);
        r0 = org.telegram.messenger.AndroidUtilities.leftBaseline;
        r0 = (float) r0;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r0 = r5 - r0;
        r6 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        r7 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r7 = r0 - r7;
        r0 = r1.documentAttach;
        if (r0 == 0) goto L_0x004e;
    L_0x0045:
        r8 = new java.util.ArrayList;
        r0 = r0.thumbs;
        r8.<init>(r0);
    L_0x004c:
        r15 = r8;
        goto L_0x005f;
    L_0x004e:
        r0 = r1.inlineResult;
        if (r0 == 0) goto L_0x005e;
    L_0x0052:
        r0 = r0.photo;
        if (r0 == 0) goto L_0x005e;
    L_0x0056:
        r8 = new java.util.ArrayList;
        r0 = r0.sizes;
        r8.<init>(r0);
        goto L_0x004c;
    L_0x005e:
        r15 = r3;
    L_0x005f:
        r0 = r1.mediaWebpage;
        r14 = 1;
        if (r0 != 0) goto L_0x016b;
    L_0x0064:
        r0 = r1.inlineResult;
        if (r0 == 0) goto L_0x016b;
    L_0x0068:
        r0 = r0.title;
        r13 = 32;
        r12 = 10;
        if (r0 == 0) goto L_0x00ca;
    L_0x0070:
        r8 = org.telegram.ui.ActionBar.Theme.chat_contextResult_titleTextPaint;	 Catch:{ Exception -> 0x00bd }
        r0 = r8.measureText(r0);	 Catch:{ Exception -> 0x00bd }
        r8 = (double) r0;	 Catch:{ Exception -> 0x00bd }
        r8 = java.lang.Math.ceil(r8);	 Catch:{ Exception -> 0x00bd }
        r0 = (int) r8;	 Catch:{ Exception -> 0x00bd }
        r8 = r1.inlineResult;	 Catch:{ Exception -> 0x00bd }
        r8 = r8.title;	 Catch:{ Exception -> 0x00bd }
        r8 = r8.replace(r12, r13);	 Catch:{ Exception -> 0x00bd }
        r9 = org.telegram.ui.ActionBar.Theme.chat_contextResult_titleTextPaint;	 Catch:{ Exception -> 0x00bd }
        r9 = r9.getFontMetricsInt();	 Catch:{ Exception -> 0x00bd }
        r10 = NUM; // 0x41700000 float:15.0 double:5.424144515E-315;
        r10 = org.telegram.messenger.AndroidUtilities.dp(r10);	 Catch:{ Exception -> 0x00bd }
        r8 = org.telegram.messenger.Emoji.replaceEmoji(r8, r9, r10, r2);	 Catch:{ Exception -> 0x00bd }
        r9 = org.telegram.ui.ActionBar.Theme.chat_contextResult_titleTextPaint;	 Catch:{ Exception -> 0x00bd }
        r0 = java.lang.Math.min(r0, r7);	 Catch:{ Exception -> 0x00bd }
        r0 = (float) r0;	 Catch:{ Exception -> 0x00bd }
        r10 = android.text.TextUtils.TruncateAt.END;	 Catch:{ Exception -> 0x00bd }
        r17 = android.text.TextUtils.ellipsize(r8, r9, r0, r10);	 Catch:{ Exception -> 0x00bd }
        r0 = new android.text.StaticLayout;	 Catch:{ Exception -> 0x00bd }
        r18 = org.telegram.ui.ActionBar.Theme.chat_contextResult_titleTextPaint;	 Catch:{ Exception -> 0x00bd }
        r8 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
        r8 = org.telegram.messenger.AndroidUtilities.dp(r8);	 Catch:{ Exception -> 0x00bd }
        r19 = r7 + r8;
        r20 = android.text.Layout.Alignment.ALIGN_NORMAL;	 Catch:{ Exception -> 0x00bd }
        r21 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r22 = 0;
        r23 = 0;
        r16 = r0;
        r16.<init>(r17, r18, r19, r20, r21, r22, r23);	 Catch:{ Exception -> 0x00bd }
        r1.titleLayout = r0;	 Catch:{ Exception -> 0x00bd }
        goto L_0x00c1;
    L_0x00bd:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x00c1:
        r0 = r1.letterDrawable;
        r8 = r1.inlineResult;
        r8 = r8.title;
        r0.setTitle(r8);
    L_0x00ca:
        r0 = r1.inlineResult;
        r0 = r0.description;
        if (r0 == 0) goto L_0x0121;
    L_0x00d0:
        r8 = org.telegram.ui.ActionBar.Theme.chat_contextResult_descriptionTextPaint;	 Catch:{ Exception -> 0x0118 }
        r8 = r8.getFontMetricsInt();	 Catch:{ Exception -> 0x0118 }
        r9 = NUM; // 0x41500000 float:13.0 double:5.413783207E-315;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r9);	 Catch:{ Exception -> 0x0118 }
        r8 = org.telegram.messenger.Emoji.replaceEmoji(r0, r8, r9, r2);	 Catch:{ Exception -> 0x0118 }
        r9 = org.telegram.ui.ActionBar.Theme.chat_contextResult_descriptionTextPaint;	 Catch:{ Exception -> 0x0118 }
        r0 = 0;
        r16 = 3;
        r10 = r7;
        r11 = r7;
        r3 = 10;
        r12 = r0;
        r6 = 32;
        r13 = r16;
        r0 = org.telegram.ui.Cells.ChatMessageCell.generateStaticLayout(r8, r9, r10, r11, r12, r13);	 Catch:{ Exception -> 0x0116 }
        r1.descriptionLayout = r0;	 Catch:{ Exception -> 0x0116 }
        r0 = r1.descriptionLayout;	 Catch:{ Exception -> 0x0116 }
        r0 = r0.getLineCount();	 Catch:{ Exception -> 0x0116 }
        if (r0 <= 0) goto L_0x0125;
    L_0x00fc:
        r0 = r1.descriptionY;	 Catch:{ Exception -> 0x0116 }
        r8 = r1.descriptionLayout;	 Catch:{ Exception -> 0x0116 }
        r9 = r1.descriptionLayout;	 Catch:{ Exception -> 0x0116 }
        r9 = r9.getLineCount();	 Catch:{ Exception -> 0x0116 }
        r9 = r9 - r14;
        r8 = r8.getLineBottom(r9);	 Catch:{ Exception -> 0x0116 }
        r0 = r0 + r8;
        r8 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r8 = org.telegram.messenger.AndroidUtilities.dp(r8);	 Catch:{ Exception -> 0x0116 }
        r0 = r0 + r8;
        r1.linkY = r0;	 Catch:{ Exception -> 0x0116 }
        goto L_0x0125;
    L_0x0116:
        r0 = move-exception;
        goto L_0x011d;
    L_0x0118:
        r0 = move-exception;
        r3 = 10;
        r6 = 32;
    L_0x011d:
        org.telegram.messenger.FileLog.e(r0);
        goto L_0x0125;
    L_0x0121:
        r3 = 10;
        r6 = 32;
    L_0x0125:
        r0 = r1.inlineResult;
        r0 = r0.url;
        if (r0 == 0) goto L_0x016b;
    L_0x012b:
        r8 = org.telegram.ui.ActionBar.Theme.chat_contextResult_descriptionTextPaint;	 Catch:{ Exception -> 0x0164 }
        r0 = r8.measureText(r0);	 Catch:{ Exception -> 0x0164 }
        r8 = (double) r0;	 Catch:{ Exception -> 0x0164 }
        r8 = java.lang.Math.ceil(r8);	 Catch:{ Exception -> 0x0164 }
        r0 = (int) r8;	 Catch:{ Exception -> 0x0164 }
        r8 = r1.inlineResult;	 Catch:{ Exception -> 0x0164 }
        r8 = r8.url;	 Catch:{ Exception -> 0x0164 }
        r3 = r8.replace(r3, r6);	 Catch:{ Exception -> 0x0164 }
        r6 = org.telegram.ui.ActionBar.Theme.chat_contextResult_descriptionTextPaint;	 Catch:{ Exception -> 0x0164 }
        r0 = java.lang.Math.min(r0, r7);	 Catch:{ Exception -> 0x0164 }
        r0 = (float) r0;	 Catch:{ Exception -> 0x0164 }
        r8 = android.text.TextUtils.TruncateAt.MIDDLE;	 Catch:{ Exception -> 0x0164 }
        r9 = android.text.TextUtils.ellipsize(r3, r6, r0, r8);	 Catch:{ Exception -> 0x0164 }
        r0 = new android.text.StaticLayout;	 Catch:{ Exception -> 0x0164 }
        r10 = org.telegram.ui.ActionBar.Theme.chat_contextResult_descriptionTextPaint;	 Catch:{ Exception -> 0x0164 }
        r12 = android.text.Layout.Alignment.ALIGN_NORMAL;	 Catch:{ Exception -> 0x0164 }
        r13 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r3 = 0;
        r6 = 0;
        r8 = r0;
        r11 = r7;
        r7 = 1;
        r14 = r3;
        r3 = r15;
        r15 = r6;
        r8.<init>(r9, r10, r11, r12, r13, r14, r15);	 Catch:{ Exception -> 0x0162 }
        r1.linkLayout = r0;	 Catch:{ Exception -> 0x0162 }
        goto L_0x016d;
    L_0x0162:
        r0 = move-exception;
        goto L_0x0167;
    L_0x0164:
        r0 = move-exception;
        r3 = r15;
        r7 = 1;
    L_0x0167:
        org.telegram.messenger.FileLog.e(r0);
        goto L_0x016d;
    L_0x016b:
        r3 = r15;
        r7 = 1;
    L_0x016d:
        r0 = r1.documentAttach;
        r6 = 3;
        r8 = 5;
        r9 = 80;
        if (r0 == 0) goto L_0x01ba;
    L_0x0175:
        r0 = org.telegram.messenger.MessageObject.isGifDocument(r0);
        r3 = 90;
        if (r0 == 0) goto L_0x0188;
    L_0x017d:
        r0 = r1.documentAttach;
        r0 = r0.thumbs;
        r0 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r0, r3);
        r1.currentPhotoObject = r0;
        goto L_0x01d4;
    L_0x0188:
        r0 = r1.documentAttach;
        r0 = org.telegram.messenger.MessageObject.isStickerDocument(r0);
        if (r0 != 0) goto L_0x01aa;
    L_0x0190:
        r0 = r1.documentAttach;
        r0 = org.telegram.messenger.MessageObject.isAnimatedStickerDocument(r0, r7);
        if (r0 == 0) goto L_0x0199;
    L_0x0198:
        goto L_0x01aa;
    L_0x0199:
        r0 = r1.documentAttachType;
        if (r0 == r8) goto L_0x01d4;
    L_0x019d:
        if (r0 == r6) goto L_0x01d4;
    L_0x019f:
        r0 = r1.documentAttach;
        r0 = r0.thumbs;
        r0 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r0, r3);
        r1.currentPhotoObject = r0;
        goto L_0x01d4;
    L_0x01aa:
        r0 = r1.documentAttach;
        r0 = r0.thumbs;
        r0 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r0, r3);
        r1.currentPhotoObject = r0;
        r3 = "webp";
        r25 = r3;
        r3 = 0;
        goto L_0x01d7;
    L_0x01ba:
        r0 = r1.inlineResult;
        if (r0 == 0) goto L_0x01d4;
    L_0x01be:
        r0 = r0.photo;
        if (r0 == 0) goto L_0x01d4;
    L_0x01c2:
        r0 = org.telegram.messenger.AndroidUtilities.getPhotoSize();
        r0 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r3, r0, r7);
        r1.currentPhotoObject = r0;
        r3 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r3, r9);
        r0 = r1.currentPhotoObject;
        if (r3 != r0) goto L_0x01d5;
    L_0x01d4:
        r3 = 0;
    L_0x01d5:
        r25 = 0;
    L_0x01d7:
        r0 = r1.inlineResult;
        r10 = 2;
        if (r0 == 0) goto L_0x028b;
    L_0x01dc:
        r11 = r0.content;
        r11 = r11 instanceof org.telegram.tgnet.TLRPC.TL_webDocument;
        if (r11 == 0) goto L_0x0214;
    L_0x01e2:
        r0 = r0.type;
        if (r0 == 0) goto L_0x0214;
    L_0x01e6:
        r11 = "gif";
        r0 = r0.startsWith(r11);
        if (r0 == 0) goto L_0x01f7;
    L_0x01ee:
        r0 = r1.inlineResult;
        r0 = r0.content;
        r0 = (org.telegram.tgnet.TLRPC.TL_webDocument) r0;
        r1.documentAttachType = r10;
        goto L_0x0215;
    L_0x01f7:
        r0 = r1.inlineResult;
        r0 = r0.type;
        r11 = "photo";
        r0 = r0.equals(r11);
        if (r0 == 0) goto L_0x0214;
    L_0x0203:
        r0 = r1.inlineResult;
        r11 = r0.thumb;
        r12 = r11 instanceof org.telegram.tgnet.TLRPC.TL_webDocument;
        if (r12 == 0) goto L_0x020f;
    L_0x020b:
        r0 = r11;
        r0 = (org.telegram.tgnet.TLRPC.TL_webDocument) r0;
        goto L_0x0215;
    L_0x020f:
        r0 = r0.content;
        r0 = (org.telegram.tgnet.TLRPC.TL_webDocument) r0;
        goto L_0x0215;
    L_0x0214:
        r0 = 0;
    L_0x0215:
        if (r0 != 0) goto L_0x0222;
    L_0x0217:
        r11 = r1.inlineResult;
        r11 = r11.thumb;
        r12 = r11 instanceof org.telegram.tgnet.TLRPC.TL_webDocument;
        if (r12 == 0) goto L_0x0222;
    L_0x021f:
        r0 = r11;
        r0 = (org.telegram.tgnet.TLRPC.TL_webDocument) r0;
    L_0x0222:
        if (r0 != 0) goto L_0x027f;
    L_0x0224:
        r11 = r1.currentPhotoObject;
        if (r11 != 0) goto L_0x027f;
    L_0x0228:
        if (r3 != 0) goto L_0x027f;
    L_0x022a:
        r11 = r1.inlineResult;
        r11 = r11.send_message;
        r12 = r11 instanceof org.telegram.tgnet.TLRPC.TL_botInlineMessageMediaVenue;
        if (r12 != 0) goto L_0x0236;
    L_0x0232:
        r11 = r11 instanceof org.telegram.tgnet.TLRPC.TL_botInlineMessageMediaGeo;
        if (r11 == 0) goto L_0x027f;
    L_0x0236:
        r11 = r1.inlineResult;
        r11 = r11.send_message;
        r11 = r11.geo;
        r12 = r11.lat;
        r14 = r11._long;
        r11 = r1.currentAccount;
        r11 = org.telegram.messenger.MessagesController.getInstance(r11);
        r11 = r11.mapProvider;
        if (r11 != r10) goto L_0x0268;
    L_0x024a:
        r11 = r1.inlineResult;
        r11 = r11.send_message;
        r11 = r11.geo;
        r12 = 15;
        r13 = org.telegram.messenger.AndroidUtilities.density;
        r13 = (double) r13;
        r13 = java.lang.Math.ceil(r13);
        r13 = (int) r13;
        r13 = java.lang.Math.min(r10, r13);
        r14 = 72;
        r11 = org.telegram.messenger.WebFile.createWithGeoPoint(r11, r14, r14, r12, r13);
        r17 = r11;
        r11 = 0;
        goto L_0x0282;
    L_0x0268:
        r11 = r1.currentAccount;
        r31 = 72;
        r32 = 72;
        r33 = 1;
        r34 = 15;
        r35 = -1;
        r26 = r11;
        r27 = r12;
        r29 = r14;
        r11 = org.telegram.messenger.AndroidUtilities.formapMapUrl(r26, r27, r29, r31, r32, r33, r34, r35);
        goto L_0x0280;
    L_0x027f:
        r11 = 0;
    L_0x0280:
        r17 = 0;
    L_0x0282:
        if (r0 == 0) goto L_0x028e;
    L_0x0284:
        r0 = org.telegram.messenger.WebFile.createWithWebDocument(r0);
        r17 = r0;
        goto L_0x028e;
    L_0x028b:
        r11 = 0;
        r17 = 0;
    L_0x028e:
        r0 = r1.documentAttach;
        if (r0 == 0) goto L_0x02b8;
    L_0x0292:
        r0 = 0;
    L_0x0293:
        r12 = r1.documentAttach;
        r12 = r12.attributes;
        r12 = r12.size();
        if (r0 >= r12) goto L_0x02b8;
    L_0x029d:
        r12 = r1.documentAttach;
        r12 = r12.attributes;
        r12 = r12.get(r0);
        r12 = (org.telegram.tgnet.TLRPC.DocumentAttribute) r12;
        r13 = r12 instanceof org.telegram.tgnet.TLRPC.TL_documentAttributeImageSize;
        if (r13 != 0) goto L_0x02b3;
    L_0x02ab:
        r13 = r12 instanceof org.telegram.tgnet.TLRPC.TL_documentAttributeVideo;
        if (r13 == 0) goto L_0x02b0;
    L_0x02af:
        goto L_0x02b3;
    L_0x02b0:
        r0 = r0 + 1;
        goto L_0x0293;
    L_0x02b3:
        r0 = r12.w;
        r12 = r12.h;
        goto L_0x02ba;
    L_0x02b8:
        r0 = 0;
        r12 = 0;
    L_0x02ba:
        if (r0 == 0) goto L_0x02be;
    L_0x02bc:
        if (r12 != 0) goto L_0x02db;
    L_0x02be:
        r13 = r1.currentPhotoObject;
        if (r13 == 0) goto L_0x02ce;
    L_0x02c2:
        if (r3 == 0) goto L_0x02c7;
    L_0x02c4:
        r0 = -1;
        r3.size = r0;
    L_0x02c7:
        r0 = r1.currentPhotoObject;
        r12 = r0.w;
        r0 = r0.h;
        goto L_0x02e0;
    L_0x02ce:
        r13 = r1.inlineResult;
        if (r13 == 0) goto L_0x02db;
    L_0x02d2:
        r0 = org.telegram.messenger.MessageObject.getInlineResultWidthAndHeight(r13);
        r12 = r0[r2];
        r0 = r0[r7];
        goto L_0x02e0;
    L_0x02db:
        r36 = r12;
        r12 = r0;
        r0 = r36;
    L_0x02e0:
        r13 = NUM; // 0x42a00000 float:80.0 double:5.522576936E-315;
        if (r12 == 0) goto L_0x02e6;
    L_0x02e4:
        if (r0 != 0) goto L_0x02eb;
    L_0x02e6:
        r12 = org.telegram.messenger.AndroidUtilities.dp(r13);
        r0 = r12;
    L_0x02eb:
        r14 = r1.documentAttach;
        if (r14 != 0) goto L_0x02f7;
    L_0x02ef:
        r14 = r1.currentPhotoObject;
        if (r14 != 0) goto L_0x02f7;
    L_0x02f3:
        if (r17 != 0) goto L_0x02f7;
    L_0x02f5:
        if (r11 == 0) goto L_0x049b;
    L_0x02f7:
        r14 = r1.mediaWebpage;
        if (r14 == 0) goto L_0x0356;
    L_0x02fb:
        r12 = (float) r12;
        r0 = (float) r0;
        r13 = org.telegram.messenger.AndroidUtilities.dp(r13);
        r13 = (float) r13;
        r0 = r0 / r13;
        r12 = r12 / r0;
        r0 = (int) r12;
        r12 = r1.documentAttachType;
        if (r12 != r10) goto L_0x0327;
    L_0x0309:
        r12 = java.util.Locale.US;
        r13 = new java.lang.Object[r10];
        r0 = (float) r0;
        r14 = org.telegram.messenger.AndroidUtilities.density;
        r0 = r0 / r14;
        r0 = (int) r0;
        r0 = java.lang.Integer.valueOf(r0);
        r13[r2] = r0;
        r0 = java.lang.Integer.valueOf(r9);
        r13[r7] = r0;
        r0 = "%d_%d_b";
        r0 = java.lang.String.format(r12, r0, r13);
        r30 = r0;
        goto L_0x035d;
    L_0x0327:
        r12 = java.util.Locale.US;
        r13 = new java.lang.Object[r10];
        r0 = (float) r0;
        r14 = org.telegram.messenger.AndroidUtilities.density;
        r0 = r0 / r14;
        r0 = (int) r0;
        r0 = java.lang.Integer.valueOf(r0);
        r13[r2] = r0;
        r0 = java.lang.Integer.valueOf(r9);
        r13[r7] = r0;
        r0 = "%d_%d";
        r0 = java.lang.String.format(r12, r0, r13);
        r9 = new java.lang.StringBuilder;
        r9.<init>();
        r9.append(r0);
        r12 = "_b";
        r9.append(r12);
        r9 = r9.toString();
        r30 = r9;
        goto L_0x035d;
    L_0x0356:
        r0 = "52_52_b";
        r9 = "52_52";
        r30 = r0;
        r0 = r9;
    L_0x035d:
        r9 = r1.linkImageView;
        r12 = r1.documentAttachType;
        r13 = 6;
        if (r12 != r13) goto L_0x0366;
    L_0x0364:
        r12 = 1;
        goto L_0x0367;
    L_0x0366:
        r12 = 0;
    L_0x0367:
        r9.setAspectFit(r12);
        r9 = r1.documentAttachType;
        if (r9 != r10) goto L_0x03db;
    L_0x036e:
        r3 = r1.documentAttach;
        if (r3 == 0) goto L_0x0397;
    L_0x0372:
        r9 = r1.linkImageView;
        r20 = org.telegram.messenger.ImageLocation.getForDocument(r3);
        r21 = 0;
        r3 = r1.currentPhotoObject;
        r11 = r1.documentAttach;
        r22 = org.telegram.messenger.ImageLocation.getForDocument(r3, r11);
        r3 = r1.documentAttach;
        r3 = r3.size;
        r11 = r1.parentObject;
        r27 = 0;
        r19 = r9;
        r23 = r0;
        r24 = r3;
        r26 = r11;
        r19.setImage(r20, r21, r22, r23, r24, r25, r26, r27);
        goto L_0x0499;
    L_0x0397:
        if (r17 == 0) goto L_0x03ba;
    L_0x0399:
        r3 = r1.linkImageView;
        r20 = org.telegram.messenger.ImageLocation.getForWebFile(r17);
        r21 = 0;
        r9 = r1.currentPhotoObject;
        r11 = r1.photoAttach;
        r22 = org.telegram.messenger.ImageLocation.getForPhoto(r9, r11);
        r24 = -1;
        r9 = r1.parentObject;
        r27 = 1;
        r19 = r3;
        r23 = r0;
        r26 = r9;
        r19.setImage(r20, r21, r22, r23, r24, r25, r26, r27);
        goto L_0x0499;
    L_0x03ba:
        r3 = r1.linkImageView;
        r20 = org.telegram.messenger.ImageLocation.getForPath(r11);
        r21 = 0;
        r9 = r1.currentPhotoObject;
        r11 = r1.photoAttach;
        r22 = org.telegram.messenger.ImageLocation.getForPhoto(r9, r11);
        r24 = -1;
        r9 = r1.parentObject;
        r27 = 1;
        r19 = r3;
        r23 = r0;
        r26 = r9;
        r19.setImage(r20, r21, r22, r23, r24, r25, r26, r27);
        goto L_0x0499;
    L_0x03db:
        r9 = r1.currentPhotoObject;
        if (r9 == 0) goto L_0x045c;
    L_0x03df:
        r9 = r1.documentAttach;
        r9 = org.telegram.messenger.MessageObject.canAutoplayAnimatedSticker(r9);
        if (r9 == 0) goto L_0x040e;
    L_0x03e7:
        r0 = r1.linkImageView;
        r3 = r1.documentAttach;
        r27 = org.telegram.messenger.ImageLocation.getForDocument(r3);
        r3 = r1.currentPhotoObject;
        r9 = r1.documentAttach;
        r29 = org.telegram.messenger.ImageLocation.getForDocument(r3, r9);
        r3 = r1.currentPhotoObject;
        r3 = r3.size;
        r32 = 0;
        r9 = r1.parentObject;
        r34 = 0;
        r28 = "80_80";
        r26 = r0;
        r31 = r3;
        r33 = r9;
        r26.setImage(r27, r28, r29, r30, r31, r32, r33, r34);
        goto L_0x0499;
    L_0x040e:
        r9 = r1.documentAttach;
        if (r9 == 0) goto L_0x0436;
    L_0x0412:
        r11 = r1.linkImageView;
        r12 = r1.currentPhotoObject;
        r20 = org.telegram.messenger.ImageLocation.getForDocument(r12, r9);
        r9 = r1.photoAttach;
        r22 = org.telegram.messenger.ImageLocation.getForPhoto(r3, r9);
        r3 = r1.currentPhotoObject;
        r3 = r3.size;
        r9 = r1.parentObject;
        r27 = 0;
        r19 = r11;
        r21 = r0;
        r23 = r30;
        r24 = r3;
        r26 = r9;
        r19.setImage(r20, r21, r22, r23, r24, r25, r26, r27);
        goto L_0x0499;
    L_0x0436:
        r9 = r1.linkImageView;
        r11 = r1.currentPhotoObject;
        r12 = r1.photoAttach;
        r20 = org.telegram.messenger.ImageLocation.getForPhoto(r11, r12);
        r11 = r1.photoAttach;
        r22 = org.telegram.messenger.ImageLocation.getForPhoto(r3, r11);
        r3 = r1.currentPhotoObject;
        r3 = r3.size;
        r11 = r1.parentObject;
        r27 = 0;
        r19 = r9;
        r21 = r0;
        r23 = r30;
        r24 = r3;
        r26 = r11;
        r19.setImage(r20, r21, r22, r23, r24, r25, r26, r27);
        goto L_0x0499;
    L_0x045c:
        if (r17 == 0) goto L_0x047c;
    L_0x045e:
        r9 = r1.linkImageView;
        r20 = org.telegram.messenger.ImageLocation.getForWebFile(r17);
        r11 = r1.photoAttach;
        r22 = org.telegram.messenger.ImageLocation.getForPhoto(r3, r11);
        r24 = -1;
        r3 = r1.parentObject;
        r27 = 1;
        r19 = r9;
        r21 = r0;
        r23 = r30;
        r26 = r3;
        r19.setImage(r20, r21, r22, r23, r24, r25, r26, r27);
        goto L_0x0499;
    L_0x047c:
        r9 = r1.linkImageView;
        r20 = org.telegram.messenger.ImageLocation.getForPath(r11);
        r11 = r1.photoAttach;
        r22 = org.telegram.messenger.ImageLocation.getForPhoto(r3, r11);
        r24 = -1;
        r3 = r1.parentObject;
        r27 = 1;
        r19 = r9;
        r21 = r0;
        r23 = r30;
        r26 = r3;
        r19.setImage(r20, r21, r22, r23, r24, r25, r26, r27);
    L_0x0499:
        r1.drawLinkImageView = r7;
    L_0x049b:
        r0 = r1.mediaWebpage;
        r3 = NUM; // 0x41CLASSNAME float:24.0 double:5.450047783E-315;
        if (r0 == 0) goto L_0x04dd;
    L_0x04a1:
        r0 = android.view.View.MeasureSpec.getSize(r39);
        if (r0 != 0) goto L_0x04ab;
    L_0x04a7:
        r0 = org.telegram.messenger.AndroidUtilities.dp(r4);
    L_0x04ab:
        r1.setMeasuredDimension(r5, r0);
        r4 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r4 = r5 - r4;
        r4 = r4 / r10;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r6 = r0 - r6;
        r6 = r6 / r10;
        r7 = r1.radialProgress;
        r8 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r8 = r8 + r4;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r3 = r3 + r6;
        r7.setProgressRect(r4, r6, r8, r3);
        r3 = r1.radialProgress;
        r4 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r3.setCircleRadius(r4);
        r3 = r1.linkImageView;
        r3.setImageCoords(r2, r2, r5, r0);
        goto L_0x05a9;
    L_0x04dd:
        r0 = r1.titleLayout;
        if (r0 == 0) goto L_0x04f3;
    L_0x04e1:
        r0 = r0.getLineCount();
        if (r0 == 0) goto L_0x04f3;
    L_0x04e7:
        r0 = r1.titleLayout;
        r4 = r0.getLineCount();
        r4 = r4 - r7;
        r0 = r0.getLineBottom(r4);
        r2 = r2 + r0;
    L_0x04f3:
        r0 = r1.descriptionLayout;
        if (r0 == 0) goto L_0x0509;
    L_0x04f7:
        r0 = r0.getLineCount();
        if (r0 == 0) goto L_0x0509;
    L_0x04fd:
        r0 = r1.descriptionLayout;
        r4 = r0.getLineCount();
        r4 = r4 - r7;
        r0 = r0.getLineBottom(r4);
        r2 = r2 + r0;
    L_0x0509:
        r0 = r1.linkLayout;
        if (r0 == 0) goto L_0x051f;
    L_0x050d:
        r0 = r0.getLineCount();
        if (r0 <= 0) goto L_0x051f;
    L_0x0513:
        r0 = r1.linkLayout;
        r4 = r0.getLineCount();
        r4 = r4 - r7;
        r0 = r0.getLineBottom(r4);
        r2 = r2 + r0;
    L_0x051f:
        r0 = NUM; // 0x42500000 float:52.0 double:5.496673668E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r0 = java.lang.Math.max(r0, r2);
        r2 = android.view.View.MeasureSpec.getSize(r38);
        r4 = NUM; // 0x42880000 float:68.0 double:5.514805956E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r5 = NUM; // 0x41800000 float:16.0 double:5.42932517E-315;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r0 = r0 + r5;
        r0 = java.lang.Math.max(r4, r0);
        r4 = r1.needDivider;
        r0 = r0 + r4;
        r1.setMeasuredDimension(r2, r0);
        r0 = NUM; // 0x42500000 float:52.0 double:5.496673668E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r2 = org.telegram.messenger.LocaleController.isRTL;
        if (r2 == 0) goto L_0x055b;
    L_0x054e:
        r2 = android.view.View.MeasureSpec.getSize(r38);
        r4 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r2 = r2 - r5;
        r2 = r2 - r0;
        goto L_0x0561;
    L_0x055b:
        r4 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r4);
    L_0x0561:
        r5 = r1.letterDrawable;
        r7 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r9 = r2 + r0;
        r10 = NUM; // 0x42700000 float:60.0 double:5.507034975E-315;
        r10 = org.telegram.messenger.AndroidUtilities.dp(r10);
        r5.setBounds(r2, r7, r9, r10);
        r5 = r1.linkImageView;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r5.setImageCoords(r2, r4, r0, r0);
        r0 = r1.documentAttachType;
        if (r0 == r6) goto L_0x0581;
    L_0x057f:
        if (r0 != r8) goto L_0x05a9;
    L_0x0581:
        r0 = r1.radialProgress;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r0.setCircleRadius(r3);
        r0 = r1.radialProgress;
        r3 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r3 = r3 + r2;
        r4 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r5 = NUM; // 0x42400000 float:48.0 double:5.491493014E-315;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r2 = r2 + r5;
        r5 = NUM; // 0x42600000 float:56.0 double:5.50185432E-315;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r0.setProgressRect(r3, r4, r2, r5);
    L_0x05a9:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ContextLinkCell.onMeasure(int, int):void");
    }

    private void setAttachType() {
        this.currentMessageObject = null;
        this.documentAttachType = 0;
        Document document = this.documentAttach;
        if (document == null) {
            BotInlineResult botInlineResult = this.inlineResult;
            if (botInlineResult != null) {
                if (botInlineResult.photo != null) {
                    this.documentAttachType = 7;
                } else if (botInlineResult.type.equals("audio")) {
                    this.documentAttachType = 5;
                } else if (this.inlineResult.type.equals("voice")) {
                    this.documentAttachType = 3;
                }
            }
        } else if (MessageObject.isGifDocument(document)) {
            this.documentAttachType = 2;
        } else if (MessageObject.isStickerDocument(this.documentAttach) || MessageObject.isAnimatedStickerDocument(this.documentAttach, true)) {
            this.documentAttachType = 6;
        } else if (MessageObject.isMusicDocument(this.documentAttach)) {
            this.documentAttachType = 5;
        } else if (MessageObject.isVoiceDocument(this.documentAttach)) {
            this.documentAttachType = 3;
        }
        int i = this.documentAttachType;
        if (i == 3 || i == 5) {
            TL_message tL_message = new TL_message();
            tL_message.out = true;
            tL_message.id = -Utilities.random.nextInt();
            tL_message.to_id = new TL_peerUser();
            Peer peer = tL_message.to_id;
            int clientUserId = UserConfig.getInstance(this.currentAccount).getClientUserId();
            tL_message.from_id = clientUserId;
            peer.user_id = clientUserId;
            tL_message.date = (int) (System.currentTimeMillis() / 1000);
            String str = "";
            tL_message.message = str;
            tL_message.media = new TL_messageMediaDocument();
            MessageMedia messageMedia = tL_message.media;
            messageMedia.flags |= 3;
            messageMedia.document = new TL_document();
            messageMedia = tL_message.media;
            messageMedia.document.file_reference = new byte[0];
            tL_message.flags |= 768;
            Document document2 = this.documentAttach;
            if (document2 != null) {
                messageMedia.document = document2;
                tL_message.attachPath = str;
            } else {
                String str2 = "mp3";
                String str3 = "ogg";
                String httpUrlExtension = ImageLoader.getHttpUrlExtension(this.inlineResult.content.url, this.documentAttachType == 5 ? str2 : str3);
                document2 = tL_message.media.document;
                document2.id = 0;
                document2.access_hash = 0;
                document2.date = tL_message.date;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("audio/");
                stringBuilder.append(httpUrlExtension);
                document2.mime_type = stringBuilder.toString();
                Document document3 = tL_message.media.document;
                document3.size = 0;
                document3.dc_id = 0;
                TL_documentAttributeAudio tL_documentAttributeAudio = new TL_documentAttributeAudio();
                tL_documentAttributeAudio.duration = MessageObject.getInlineResultDuration(this.inlineResult);
                String str4 = this.inlineResult.title;
                if (str4 == null) {
                    str4 = str;
                }
                tL_documentAttributeAudio.title = str4;
                str4 = this.inlineResult.description;
                if (str4 != null) {
                    str = str4;
                }
                tL_documentAttributeAudio.performer = str;
                tL_documentAttributeAudio.flags |= 3;
                if (this.documentAttachType == 3) {
                    tL_documentAttributeAudio.voice = true;
                }
                tL_message.media.document.attributes.add(tL_documentAttributeAudio);
                TL_documentAttributeFilename tL_documentAttributeFilename = new TL_documentAttributeFilename();
                StringBuilder stringBuilder2 = new StringBuilder();
                stringBuilder2.append(Utilities.MD5(this.inlineResult.content.url));
                str = ".";
                stringBuilder2.append(str);
                stringBuilder2.append(ImageLoader.getHttpUrlExtension(this.inlineResult.content.url, this.documentAttachType == 5 ? str2 : str3));
                tL_documentAttributeFilename.file_name = stringBuilder2.toString();
                tL_message.media.document.attributes.add(tL_documentAttributeFilename);
                File directory = FileLoader.getDirectory(4);
                StringBuilder stringBuilder3 = new StringBuilder();
                stringBuilder3.append(Utilities.MD5(this.inlineResult.content.url));
                stringBuilder3.append(str);
                str = this.inlineResult.content.url;
                if (this.documentAttachType != 5) {
                    str2 = str3;
                }
                stringBuilder3.append(ImageLoader.getHttpUrlExtension(str, str2));
                tL_message.attachPath = new File(directory, stringBuilder3.toString()).getAbsolutePath();
            }
            this.currentMessageObject = new MessageObject(this.currentAccount, tL_message, false);
        }
    }

    public void setLink(BotInlineResult botInlineResult, boolean z, boolean z2, boolean z3) {
        this.needDivider = z2;
        this.needShadow = z3;
        this.inlineResult = botInlineResult;
        this.parentObject = botInlineResult;
        botInlineResult = this.inlineResult;
        if (botInlineResult != null) {
            this.documentAttach = botInlineResult.document;
            this.photoAttach = botInlineResult.photo;
        } else {
            this.documentAttach = null;
            this.photoAttach = null;
        }
        this.mediaWebpage = z;
        setAttachType();
        requestLayout();
        updateButtonState(false, false);
    }

    public void setGif(Document document, boolean z) {
        this.needDivider = z;
        this.needShadow = false;
        this.inlineResult = null;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("gif");
        stringBuilder.append(document);
        this.parentObject = stringBuilder.toString();
        this.documentAttach = document;
        this.photoAttach = null;
        this.mediaWebpage = true;
        setAttachType();
        requestLayout();
        updateButtonState(false, false);
    }

    public boolean isSticker() {
        return this.documentAttachType == 6;
    }

    public boolean isGif() {
        return this.documentAttachType == 2 && this.canPreviewGif;
    }

    public boolean showingBitmap() {
        return this.linkImageView.getBitmap() != null;
    }

    public Document getDocument() {
        return this.documentAttach;
    }

    public BotInlineResult getBotInlineResult() {
        return this.inlineResult;
    }

    public ImageReceiver getPhotoImage() {
        return this.linkImageView;
    }

    public void setScaled(boolean z) {
        this.scaled = z;
        this.lastUpdateTime = System.currentTimeMillis();
        invalidate();
    }

    public void setCanPreviewGif(boolean z) {
        this.canPreviewGif = z;
    }

    public boolean isCanPreviewGif() {
        return this.canPreviewGif;
    }

    /* Access modifiers changed, original: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.drawLinkImageView) {
            this.linkImageView.onDetachedFromWindow();
        }
        this.radialProgress.onDetachedFromWindow();
        DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
    }

    /* Access modifiers changed, original: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.drawLinkImageView && this.linkImageView.onAttachedToWindow()) {
            updateButtonState(false, false);
        }
        this.radialProgress.onAttachedToWindow();
    }

    public MessageObject getMessageObject() {
        return this.currentMessageObject;
    }

    /* JADX WARNING: Removed duplicated region for block: B:53:0x00db  */
    /* JADX WARNING: Removed duplicated region for block: B:53:0x00db  */
    public boolean onTouchEvent(android.view.MotionEvent r9) {
        /*
        r8 = this;
        r0 = r8.mediaWebpage;
        if (r0 != 0) goto L_0x00e0;
    L_0x0004:
        r0 = r8.delegate;
        if (r0 == 0) goto L_0x00e0;
    L_0x0008:
        r0 = r8.inlineResult;
        if (r0 != 0) goto L_0x000e;
    L_0x000c:
        goto L_0x00e0;
    L_0x000e:
        r0 = r9.getX();
        r0 = (int) r0;
        r1 = r9.getY();
        r1 = (int) r1;
        r2 = NUM; // 0x42400000 float:48.0 double:5.491493014E-315;
        org.telegram.messenger.AndroidUtilities.dp(r2);
        r2 = r8.documentAttachType;
        r3 = 2;
        r4 = 3;
        r5 = 1;
        r6 = 0;
        if (r2 == r4) goto L_0x0083;
    L_0x0025:
        r7 = 5;
        if (r2 != r7) goto L_0x0029;
    L_0x0028:
        goto L_0x0083;
    L_0x0029:
        r2 = r8.inlineResult;
        if (r2 == 0) goto L_0x0081;
    L_0x002d:
        r2 = r2.content;
        if (r2 == 0) goto L_0x0081;
    L_0x0031:
        r2 = r2.url;
        r2 = android.text.TextUtils.isEmpty(r2);
        if (r2 != 0) goto L_0x0081;
    L_0x0039:
        r2 = r9.getAction();
        if (r2 != 0) goto L_0x004f;
    L_0x003f:
        r2 = r8.letterDrawable;
        r2 = r2.getBounds();
        r0 = r2.contains(r0, r1);
        if (r0 == 0) goto L_0x0081;
    L_0x004b:
        r8.buttonPressed = r5;
        goto L_0x00d9;
    L_0x004f:
        r2 = r8.buttonPressed;
        if (r2 == 0) goto L_0x0081;
    L_0x0053:
        r2 = r9.getAction();
        if (r2 != r5) goto L_0x0064;
    L_0x0059:
        r8.buttonPressed = r6;
        r8.playSoundEffect(r6);
        r0 = r8.delegate;
        r0.didPressedImage(r8);
        goto L_0x0081;
    L_0x0064:
        r2 = r9.getAction();
        if (r2 != r4) goto L_0x006d;
    L_0x006a:
        r8.buttonPressed = r6;
        goto L_0x0081;
    L_0x006d:
        r2 = r9.getAction();
        if (r2 != r3) goto L_0x0081;
    L_0x0073:
        r2 = r8.letterDrawable;
        r2 = r2.getBounds();
        r0 = r2.contains(r0, r1);
        if (r0 != 0) goto L_0x0081;
    L_0x007f:
        r8.buttonPressed = r6;
    L_0x0081:
        r5 = 0;
        goto L_0x00d9;
    L_0x0083:
        r2 = r8.letterDrawable;
        r2 = r2.getBounds();
        r0 = r2.contains(r0, r1);
        r1 = r9.getAction();
        if (r1 != 0) goto L_0x00a2;
    L_0x0093:
        if (r0 == 0) goto L_0x0081;
    L_0x0095:
        r8.buttonPressed = r5;
        r0 = r8.radialProgress;
        r1 = r8.buttonPressed;
        r0.setPressed(r1, r6);
        r8.invalidate();
        goto L_0x00d9;
    L_0x00a2:
        r1 = r8.buttonPressed;
        if (r1 == 0) goto L_0x0081;
    L_0x00a6:
        r1 = r9.getAction();
        if (r1 != r5) goto L_0x00b8;
    L_0x00ac:
        r8.buttonPressed = r6;
        r8.playSoundEffect(r6);
        r8.didPressedButton();
        r8.invalidate();
        goto L_0x00d1;
    L_0x00b8:
        r1 = r9.getAction();
        if (r1 != r4) goto L_0x00c4;
    L_0x00be:
        r8.buttonPressed = r6;
        r8.invalidate();
        goto L_0x00d1;
    L_0x00c4:
        r1 = r9.getAction();
        if (r1 != r3) goto L_0x00d1;
    L_0x00ca:
        if (r0 != 0) goto L_0x00d1;
    L_0x00cc:
        r8.buttonPressed = r6;
        r8.invalidate();
    L_0x00d1:
        r0 = r8.radialProgress;
        r1 = r8.buttonPressed;
        r0.setPressed(r1, r6);
        goto L_0x0081;
    L_0x00d9:
        if (r5 != 0) goto L_0x00df;
    L_0x00db:
        r5 = super.onTouchEvent(r9);
    L_0x00df:
        return r5;
    L_0x00e0:
        r9 = super.onTouchEvent(r9);
        return r9;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ContextLinkCell.onTouchEvent(android.view.MotionEvent):boolean");
    }

    private void didPressedButton() {
        int i = this.documentAttachType;
        if (i == 3 || i == 5) {
            i = this.buttonState;
            if (i == 0) {
                if (MediaController.getInstance().playMessage(this.currentMessageObject)) {
                    this.buttonState = 1;
                    this.radialProgress.setIcon(getIconForCurrentState(), false, true);
                    invalidate();
                }
            } else if (i == 1) {
                if (MediaController.getInstance().lambda$startAudioAgain$5$MediaController(this.currentMessageObject)) {
                    this.buttonState = 0;
                    this.radialProgress.setIcon(getIconForCurrentState(), false, true);
                    invalidate();
                }
            } else if (i == 2) {
                this.radialProgress.setProgress(0.0f, false);
                if (this.documentAttach != null) {
                    FileLoader.getInstance(this.currentAccount).loadFile(this.documentAttach, this.inlineResult, 1, 0);
                } else if (this.inlineResult.content instanceof TL_webDocument) {
                    FileLoader.getInstance(this.currentAccount).loadFile(WebFile.createWithWebDocument(this.inlineResult.content), 1, 1);
                }
                this.buttonState = 4;
                this.radialProgress.setIcon(getIconForCurrentState(), false, true);
                invalidate();
            } else if (i == 4) {
                if (this.documentAttach != null) {
                    FileLoader.getInstance(this.currentAccount).cancelLoadFile(this.documentAttach);
                } else if (this.inlineResult.content instanceof TL_webDocument) {
                    FileLoader.getInstance(this.currentAccount).cancelLoadFile(WebFile.createWithWebDocument(this.inlineResult.content));
                }
                this.buttonState = 2;
                this.radialProgress.setIcon(getIconForCurrentState(), false, true);
                invalidate();
            }
        }
    }

    /* Access modifiers changed, original: protected */
    public void onDraw(Canvas canvas) {
        BotInlineResult botInlineResult;
        int intrinsicWidth;
        float f = 8.0f;
        if (this.titleLayout != null) {
            canvas.save();
            canvas.translate((float) AndroidUtilities.dp(LocaleController.isRTL ? 8.0f : (float) AndroidUtilities.leftBaseline), (float) this.titleY);
            this.titleLayout.draw(canvas);
            canvas.restore();
        }
        if (this.descriptionLayout != null) {
            Theme.chat_contextResult_descriptionTextPaint.setColor(Theme.getColor("windowBackgroundWhiteGrayText2"));
            canvas.save();
            canvas.translate((float) AndroidUtilities.dp(LocaleController.isRTL ? 8.0f : (float) AndroidUtilities.leftBaseline), (float) this.descriptionY);
            this.descriptionLayout.draw(canvas);
            canvas.restore();
        }
        if (this.linkLayout != null) {
            Theme.chat_contextResult_descriptionTextPaint.setColor(Theme.getColor("windowBackgroundWhiteLinkText"));
            canvas.save();
            if (!LocaleController.isRTL) {
                f = (float) AndroidUtilities.leftBaseline;
            }
            canvas.translate((float) AndroidUtilities.dp(f), (float) this.linkY);
            this.linkLayout.draw(canvas);
            canvas.restore();
        }
        int imageX;
        int imageY;
        if (this.mediaWebpage) {
            botInlineResult = this.inlineResult;
            if (botInlineResult != null) {
                BotInlineMessage botInlineMessage = botInlineResult.send_message;
                if ((botInlineMessage instanceof TL_botInlineMessageMediaGeo) || (botInlineMessage instanceof TL_botInlineMessageMediaVenue)) {
                    intrinsicWidth = Theme.chat_inlineResultLocation.getIntrinsicWidth();
                    int intrinsicHeight = Theme.chat_inlineResultLocation.getIntrinsicHeight();
                    imageX = this.linkImageView.getImageX() + ((this.linkImageView.getImageWidth() - intrinsicWidth) / 2);
                    imageY = this.linkImageView.getImageY() + ((this.linkImageView.getImageHeight() - intrinsicHeight) / 2);
                    canvas.drawRect((float) this.linkImageView.getImageX(), (float) this.linkImageView.getImageY(), (float) (this.linkImageView.getImageX() + this.linkImageView.getImageWidth()), (float) (this.linkImageView.getImageY() + this.linkImageView.getImageHeight()), LetterDrawable.paint);
                    Theme.chat_inlineResultLocation.setBounds(imageX, imageY, intrinsicWidth + imageX, intrinsicHeight + imageY);
                    Theme.chat_inlineResultLocation.draw(canvas);
                }
            }
        } else {
            if (!this.drawLinkImageView || PhotoViewer.isShowingImage(this.inlineResult)) {
                this.letterDrawable.setAlpha(255);
            } else {
                this.letterDrawable.setAlpha((int) ((1.0f - this.linkImageView.getCurrentAlpha()) * 255.0f));
            }
            intrinsicWidth = this.documentAttachType;
            if (intrinsicWidth == 3 || intrinsicWidth == 5) {
                this.radialProgress.setProgressColor(Theme.getColor(this.buttonPressed ? "chat_inAudioSelectedProgress" : "chat_inAudioProgress"));
                this.radialProgress.draw(canvas);
            } else {
                botInlineResult = this.inlineResult;
                int imageY2;
                if (botInlineResult == null || !botInlineResult.type.equals("file")) {
                    botInlineResult = this.inlineResult;
                    if (botInlineResult == null || !(botInlineResult.type.equals("audio") || this.inlineResult.type.equals("voice"))) {
                        botInlineResult = this.inlineResult;
                        if (botInlineResult == null || !(botInlineResult.type.equals("venue") || this.inlineResult.type.equals("geo"))) {
                            this.letterDrawable.draw(canvas);
                        } else {
                            intrinsicWidth = Theme.chat_inlineResultLocation.getIntrinsicWidth();
                            imageX = Theme.chat_inlineResultLocation.getIntrinsicHeight();
                            imageY = this.linkImageView.getImageX() + ((AndroidUtilities.dp(52.0f) - intrinsicWidth) / 2);
                            imageY2 = this.linkImageView.getImageY() + ((AndroidUtilities.dp(52.0f) - imageX) / 2);
                            canvas.drawRect((float) this.linkImageView.getImageX(), (float) this.linkImageView.getImageY(), (float) (this.linkImageView.getImageX() + AndroidUtilities.dp(52.0f)), (float) (this.linkImageView.getImageY() + AndroidUtilities.dp(52.0f)), LetterDrawable.paint);
                            Theme.chat_inlineResultLocation.setBounds(imageY, imageY2, intrinsicWidth + imageY, imageX + imageY2);
                            Theme.chat_inlineResultLocation.draw(canvas);
                        }
                    } else {
                        intrinsicWidth = Theme.chat_inlineResultAudio.getIntrinsicWidth();
                        imageX = Theme.chat_inlineResultAudio.getIntrinsicHeight();
                        imageY = this.linkImageView.getImageX() + ((AndroidUtilities.dp(52.0f) - intrinsicWidth) / 2);
                        imageY2 = this.linkImageView.getImageY() + ((AndroidUtilities.dp(52.0f) - imageX) / 2);
                        canvas.drawRect((float) this.linkImageView.getImageX(), (float) this.linkImageView.getImageY(), (float) (this.linkImageView.getImageX() + AndroidUtilities.dp(52.0f)), (float) (this.linkImageView.getImageY() + AndroidUtilities.dp(52.0f)), LetterDrawable.paint);
                        Theme.chat_inlineResultAudio.setBounds(imageY, imageY2, intrinsicWidth + imageY, imageX + imageY2);
                        Theme.chat_inlineResultAudio.draw(canvas);
                    }
                } else {
                    intrinsicWidth = Theme.chat_inlineResultFile.getIntrinsicWidth();
                    imageX = Theme.chat_inlineResultFile.getIntrinsicHeight();
                    imageY = this.linkImageView.getImageX() + ((AndroidUtilities.dp(52.0f) - intrinsicWidth) / 2);
                    imageY2 = this.linkImageView.getImageY() + ((AndroidUtilities.dp(52.0f) - imageX) / 2);
                    canvas.drawRect((float) this.linkImageView.getImageX(), (float) this.linkImageView.getImageY(), (float) (this.linkImageView.getImageX() + AndroidUtilities.dp(52.0f)), (float) (this.linkImageView.getImageY() + AndroidUtilities.dp(52.0f)), LetterDrawable.paint);
                    Theme.chat_inlineResultFile.setBounds(imageY, imageY2, intrinsicWidth + imageY, imageX + imageY2);
                    Theme.chat_inlineResultFile.draw(canvas);
                }
            }
        }
        if (this.drawLinkImageView) {
            float f2;
            botInlineResult = this.inlineResult;
            if (botInlineResult != null) {
                this.linkImageView.setVisible(PhotoViewer.isShowingImage(botInlineResult) ^ 1, false);
            }
            canvas.save();
            if ((this.scaled && this.scale != 0.8f) || !(this.scaled || this.scale == 1.0f)) {
                long currentTimeMillis = System.currentTimeMillis();
                long j = currentTimeMillis - this.lastUpdateTime;
                this.lastUpdateTime = currentTimeMillis;
                if (this.scaled) {
                    f2 = this.scale;
                    if (f2 != 0.8f) {
                        this.scale = f2 - (((float) j) / 400.0f);
                        if (this.scale < 0.8f) {
                            this.scale = 0.8f;
                        }
                        invalidate();
                    }
                }
                this.scale += ((float) j) / 400.0f;
                if (this.scale > 1.0f) {
                    this.scale = 1.0f;
                }
                invalidate();
            }
            f2 = this.scale;
            canvas.scale(f2, f2, (float) (getMeasuredWidth() / 2), (float) (getMeasuredHeight() / 2));
            this.linkImageView.draw(canvas);
            canvas.restore();
        }
        if (this.mediaWebpage) {
            intrinsicWidth = this.documentAttachType;
            if (intrinsicWidth == 7 || intrinsicWidth == 2) {
                this.radialProgress.draw(canvas);
            }
        }
        if (this.needDivider && !this.mediaWebpage) {
            if (LocaleController.isRTL) {
                canvas.drawLine(0.0f, (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - AndroidUtilities.dp((float) AndroidUtilities.leftBaseline)), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
            } else {
                canvas.drawLine((float) AndroidUtilities.dp((float) AndroidUtilities.leftBaseline), (float) (getMeasuredHeight() - 1), (float) getMeasuredWidth(), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
            }
        }
        if (this.needShadow) {
            Theme.chat_contextResult_shadowUnderSwitchDrawable.setBounds(0, 0, getMeasuredWidth(), AndroidUtilities.dp(3.0f));
            Theme.chat_contextResult_shadowUnderSwitchDrawable.draw(canvas);
        }
    }

    private int getIconForCurrentState() {
        int i = this.documentAttachType;
        int i2 = 4;
        if (i == 3 || i == 5) {
            this.radialProgress.setColors("chat_inLoader", "chat_inLoaderSelected", "chat_inMediaIcon", "chat_inMediaIconSelected");
            i = this.buttonState;
            if (i == 1) {
                return 1;
            }
            if (i == 2) {
                return 2;
            }
            if (i == 4) {
                return 3;
            }
            return 0;
        }
        this.radialProgress.setColors("chat_mediaLoaderPhoto", "chat_mediaLoaderPhotoSelected", "chat_mediaLoaderPhotoIcon", "chat_mediaLoaderPhotoIconSelected");
        if (this.buttonState == 1) {
            i2 = 10;
        }
        return i2;
    }

    /* JADX WARNING: Removed duplicated region for block: B:36:0x0121  */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x0120 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x0120 A:{RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:36:0x0121  */
    /* JADX WARNING: Removed duplicated region for block: B:36:0x0121  */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x0120 A:{RETURN} */
    public void updateButtonState(boolean r10, boolean r11) {
        /*
        r9 = this;
        r0 = r9.documentAttachType;
        r1 = 0;
        r2 = 1;
        r3 = ".";
        r4 = 3;
        r5 = 4;
        r6 = 5;
        if (r0 == r6) goto L_0x00c9;
    L_0x000b:
        if (r0 != r4) goto L_0x000f;
    L_0x000d:
        goto L_0x00c9;
    L_0x000f:
        r0 = r9.mediaWebpage;
        if (r0 == 0) goto L_0x0119;
    L_0x0013:
        r0 = r9.inlineResult;
        if (r0 == 0) goto L_0x00ba;
    L_0x0017:
        r7 = r0.document;
        r8 = r7 instanceof org.telegram.tgnet.TLRPC.TL_document;
        if (r8 == 0) goto L_0x002b;
    L_0x001d:
        r1 = org.telegram.messenger.FileLoader.getAttachFileName(r7);
        r0 = r9.inlineResult;
        r0 = r0.document;
        r0 = org.telegram.messenger.FileLoader.getPathToAttach(r0);
        goto L_0x011a;
    L_0x002b:
        r7 = r0.photo;
        r8 = r7 instanceof org.telegram.tgnet.TLRPC.TL_photo;
        if (r8 == 0) goto L_0x004b;
    L_0x0031:
        r0 = r7.sizes;
        r1 = org.telegram.messenger.AndroidUtilities.getPhotoSize();
        r0 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r0, r1, r2);
        r9.currentPhotoObject = r0;
        r0 = r9.currentPhotoObject;
        r1 = org.telegram.messenger.FileLoader.getAttachFileName(r0);
        r0 = r9.currentPhotoObject;
        r0 = org.telegram.messenger.FileLoader.getPathToAttach(r0);
        goto L_0x011a;
    L_0x004b:
        r7 = r0.content;
        r7 = r7 instanceof org.telegram.tgnet.TLRPC.TL_webDocument;
        r8 = "jpg";
        if (r7 == 0) goto L_0x0084;
    L_0x0053:
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r1 = r9.inlineResult;
        r1 = r1.content;
        r1 = r1.url;
        r1 = org.telegram.messenger.Utilities.MD5(r1);
        r0.append(r1);
        r0.append(r3);
        r1 = r9.inlineResult;
        r1 = r1.content;
        r1 = r1.url;
        r1 = org.telegram.messenger.ImageLoader.getHttpUrlExtension(r1, r8);
        r0.append(r1);
        r1 = r0.toString();
        r0 = new java.io.File;
        r3 = org.telegram.messenger.FileLoader.getDirectory(r5);
        r0.<init>(r3, r1);
        goto L_0x011a;
    L_0x0084:
        r0 = r0.thumb;
        r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_webDocument;
        if (r0 == 0) goto L_0x0119;
    L_0x008a:
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r1 = r9.inlineResult;
        r1 = r1.thumb;
        r1 = r1.url;
        r1 = org.telegram.messenger.Utilities.MD5(r1);
        r0.append(r1);
        r0.append(r3);
        r1 = r9.inlineResult;
        r1 = r1.thumb;
        r1 = r1.url;
        r1 = org.telegram.messenger.ImageLoader.getHttpUrlExtension(r1, r8);
        r0.append(r1);
        r1 = r0.toString();
        r0 = new java.io.File;
        r3 = org.telegram.messenger.FileLoader.getDirectory(r5);
        r0.<init>(r3, r1);
        goto L_0x011a;
    L_0x00ba:
        r0 = r9.documentAttach;
        if (r0 == 0) goto L_0x0119;
    L_0x00be:
        r1 = org.telegram.messenger.FileLoader.getAttachFileName(r0);
        r0 = r9.documentAttach;
        r0 = org.telegram.messenger.FileLoader.getPathToAttach(r0);
        goto L_0x011a;
    L_0x00c9:
        r0 = r9.documentAttach;
        if (r0 == 0) goto L_0x00d8;
    L_0x00cd:
        r1 = org.telegram.messenger.FileLoader.getAttachFileName(r0);
        r0 = r9.documentAttach;
        r0 = org.telegram.messenger.FileLoader.getPathToAttach(r0);
        goto L_0x011a;
    L_0x00d8:
        r0 = r9.inlineResult;
        r0 = r0.content;
        r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_webDocument;
        if (r0 == 0) goto L_0x0119;
    L_0x00e0:
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r1 = r9.inlineResult;
        r1 = r1.content;
        r1 = r1.url;
        r1 = org.telegram.messenger.Utilities.MD5(r1);
        r0.append(r1);
        r0.append(r3);
        r1 = r9.inlineResult;
        r1 = r1.content;
        r1 = r1.url;
        r3 = r9.documentAttachType;
        if (r3 != r6) goto L_0x0102;
    L_0x00ff:
        r3 = "mp3";
        goto L_0x0104;
    L_0x0102:
        r3 = "ogg";
    L_0x0104:
        r1 = org.telegram.messenger.ImageLoader.getHttpUrlExtension(r1, r3);
        r0.append(r1);
        r1 = r0.toString();
        r0 = new java.io.File;
        r3 = org.telegram.messenger.FileLoader.getDirectory(r5);
        r0.<init>(r3, r1);
        goto L_0x011a;
    L_0x0119:
        r0 = r1;
    L_0x011a:
        r3 = android.text.TextUtils.isEmpty(r1);
        if (r3 == 0) goto L_0x0121;
    L_0x0120:
        return;
    L_0x0121:
        r0 = r0.exists();
        r3 = 0;
        if (r0 != 0) goto L_0x01a6;
    L_0x0128:
        r0 = r9.currentAccount;
        r0 = org.telegram.messenger.DownloadController.getInstance(r0);
        r0.addLoadingFileObserver(r1, r9);
        r0 = r9.documentAttachType;
        r7 = 0;
        if (r0 == r6) goto L_0x0158;
    L_0x0136:
        if (r0 != r4) goto L_0x0139;
    L_0x0138:
        goto L_0x0158;
    L_0x0139:
        r9.buttonState = r2;
        r0 = org.telegram.messenger.ImageLoader.getInstance();
        r0 = r0.getFileProgress(r1);
        if (r0 == 0) goto L_0x0149;
    L_0x0145:
        r7 = r0.floatValue();
    L_0x0149:
        r0 = r9.radialProgress;
        r0.setProgress(r7, r3);
        r0 = r9.radialProgress;
        r1 = r9.getIconForCurrentState();
        r0.setIcon(r1, r10, r11);
        goto L_0x01a2;
    L_0x0158:
        r0 = r9.documentAttach;
        if (r0 == 0) goto L_0x0167;
    L_0x015c:
        r0 = r9.currentAccount;
        r0 = org.telegram.messenger.FileLoader.getInstance(r0);
        r0 = r0.isLoadingFile(r1);
        goto L_0x016f;
    L_0x0167:
        r0 = org.telegram.messenger.ImageLoader.getInstance();
        r0 = r0.isLoadingHttpFile(r1);
    L_0x016f:
        if (r0 != 0) goto L_0x017e;
    L_0x0171:
        r0 = 2;
        r9.buttonState = r0;
        r0 = r9.radialProgress;
        r1 = r9.getIconForCurrentState();
        r0.setIcon(r1, r10, r11);
        goto L_0x01a2;
    L_0x017e:
        r9.buttonState = r5;
        r0 = org.telegram.messenger.ImageLoader.getInstance();
        r0 = r0.getFileProgress(r1);
        if (r0 == 0) goto L_0x0194;
    L_0x018a:
        r1 = r9.radialProgress;
        r0 = r0.floatValue();
        r1.setProgress(r0, r11);
        goto L_0x0199;
    L_0x0194:
        r0 = r9.radialProgress;
        r0.setProgress(r7, r11);
    L_0x0199:
        r0 = r9.radialProgress;
        r1 = r9.getIconForCurrentState();
        r0.setIcon(r1, r10, r11);
    L_0x01a2:
        r9.invalidate();
        goto L_0x01eb;
    L_0x01a6:
        r0 = r9.currentAccount;
        r0 = org.telegram.messenger.DownloadController.getInstance(r0);
        r0.removeLoadingFileObserver(r9);
        r0 = r9.documentAttachType;
        if (r0 == r6) goto L_0x01ba;
    L_0x01b3:
        if (r0 != r4) goto L_0x01b6;
    L_0x01b5:
        goto L_0x01ba;
    L_0x01b6:
        r0 = -1;
        r9.buttonState = r0;
        goto L_0x01df;
    L_0x01ba:
        r0 = org.telegram.messenger.MediaController.getInstance();
        r1 = r9.currentMessageObject;
        r0 = r0.isPlayingMessage(r1);
        if (r0 == 0) goto L_0x01d6;
    L_0x01c6:
        if (r0 == 0) goto L_0x01d3;
    L_0x01c8:
        r0 = org.telegram.messenger.MediaController.getInstance();
        r0 = r0.isMessagePaused();
        if (r0 == 0) goto L_0x01d3;
    L_0x01d2:
        goto L_0x01d6;
    L_0x01d3:
        r9.buttonState = r2;
        goto L_0x01d8;
    L_0x01d6:
        r9.buttonState = r3;
    L_0x01d8:
        r0 = r9.radialProgress;
        r1 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r0.setProgress(r1, r11);
    L_0x01df:
        r0 = r9.radialProgress;
        r1 = r9.getIconForCurrentState();
        r0.setIcon(r1, r10, r11);
        r9.invalidate();
    L_0x01eb:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ContextLinkCell.updateButtonState(boolean, boolean):void");
    }

    public void setDelegate(ContextLinkCellDelegate contextLinkCellDelegate) {
        this.delegate = contextLinkCellDelegate;
    }

    public BotInlineResult getResult() {
        return this.inlineResult;
    }

    public void onFailedDownload(String str, boolean z) {
        updateButtonState(true, z);
    }

    public void onSuccessDownload(String str) {
        this.radialProgress.setProgress(1.0f, true);
        updateButtonState(false, true);
    }

    public void onProgressDownload(String str, float f) {
        this.radialProgress.setProgress(f, true);
        int i = this.documentAttachType;
        if (i == 3 || i == 5) {
            if (this.buttonState != 4) {
                updateButtonState(false, true);
            }
        } else if (this.buttonState != 1) {
            updateButtonState(false, true);
        }
    }

    public int getObserverTag() {
        return this.TAG;
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        StringBuilder stringBuilder = new StringBuilder();
        String str = ", ";
        switch (this.documentAttachType) {
            case 1:
                stringBuilder.append(LocaleController.getString("AttachDocument", NUM));
                break;
            case 2:
                stringBuilder.append(LocaleController.getString("AttachGif", NUM));
                break;
            case 3:
                stringBuilder.append(LocaleController.getString("AttachAudio", NUM));
                break;
            case 4:
                stringBuilder.append(LocaleController.getString("AttachVideo", NUM));
                break;
            case 5:
                stringBuilder.append(LocaleController.getString("AttachMusic", NUM));
                if (!(this.descriptionLayout == null || this.titleLayout == null)) {
                    stringBuilder.append(str);
                    stringBuilder.append(LocaleController.formatString("AccDescrMusicInfo", NUM, this.descriptionLayout.getText(), this.titleLayout.getText()));
                    break;
                }
            case 6:
                stringBuilder.append(LocaleController.getString("AttachSticker", NUM));
                break;
            case 7:
                stringBuilder.append(LocaleController.getString("AttachPhoto", NUM));
                break;
            case 8:
                stringBuilder.append(LocaleController.getString("AttachLocation", NUM));
                break;
            default:
                StaticLayout staticLayout = this.titleLayout;
                if (!(staticLayout == null || TextUtils.isEmpty(staticLayout.getText()))) {
                    stringBuilder.append(this.titleLayout.getText());
                }
                staticLayout = this.descriptionLayout;
                if (!(staticLayout == null || TextUtils.isEmpty(staticLayout.getText()))) {
                    if (stringBuilder.length() > 0) {
                        stringBuilder.append(str);
                    }
                    stringBuilder.append(this.descriptionLayout.getText());
                    break;
                }
        }
        accessibilityNodeInfo.setText(stringBuilder);
    }
}
