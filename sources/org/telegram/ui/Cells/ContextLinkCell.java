package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.text.StaticLayout;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import com.google.android.exoplayer2.util.MimeTypes;
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
import org.telegram.tgnet.TLRPC.BotInlineResult;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.MessageMedia;
import org.telegram.tgnet.TLRPC.Peer;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.TL_botInlineMessageMediaGeo;
import org.telegram.tgnet.TLRPC.TL_botInlineMessageMediaVenue;
import org.telegram.tgnet.TLRPC.TL_document;
import org.telegram.tgnet.TLRPC.TL_documentAttributeAudio;
import org.telegram.tgnet.TLRPC.TL_documentAttributeFilename;
import org.telegram.tgnet.TLRPC.TL_message;
import org.telegram.tgnet.TLRPC.TL_messageMediaDocument;
import org.telegram.tgnet.TLRPC.TL_peerUser;
import org.telegram.tgnet.TLRPC.TL_photo;
import org.telegram.tgnet.TLRPC.TL_photoSizeEmpty;
import org.telegram.tgnet.TLRPC.TL_webDocument;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LetterDrawable;
import org.telegram.ui.Components.RadialProgress;
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
    private int TAG = DownloadController.getInstance(this.currentAccount).generateObserverTag();
    private boolean buttonPressed;
    private int buttonState;
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
    private LetterDrawable letterDrawable = new LetterDrawable();
    private ImageReceiver linkImageView = new ImageReceiver(this);
    private StaticLayout linkLayout;
    private int linkY;
    private boolean mediaWebpage;
    private boolean needDivider;
    private boolean needShadow;
    private RadialProgress radialProgress = new RadialProgress(this);
    private float scale;
    private boolean scaled;
    private StaticLayout titleLayout;
    private int titleY = AndroidUtilities.dp(7.0f);

    public interface ContextLinkCellDelegate {
        void didPressedImage(ContextLinkCell contextLinkCell);
    }

    public ContextLinkCell(Context context) {
        super(context);
    }

    @android.annotation.SuppressLint({"DrawAllocation"})
    protected void onMeasure(int r56, int r57) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Unknown predecessor block by arg (r47_3 'photoThumbs' java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize>) in PHI: PHI: (r47_2 'photoThumbs' java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize>) = (r47_1 'photoThumbs' java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize>), (r47_0 'photoThumbs' java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize>), (r47_0 'photoThumbs' java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize>), (r47_3 'photoThumbs' java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize>) binds: {(r47_1 'photoThumbs' java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize>)=B:7:0x0066, (r47_0 'photoThumbs' java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize>)=B:110:0x03dd, (r47_0 'photoThumbs' java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize>)=B:112:0x03e5, (r47_3 'photoThumbs' java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize>)=B:113:0x03e7}
	at jadx.core.dex.instructions.PhiInsn.replaceArg(PhiInsn.java:79)
	at jadx.core.dex.visitors.ModVisitor.processInvoke(ModVisitor.java:222)
	at jadx.core.dex.visitors.ModVisitor.replaceStep(ModVisitor.java:83)
	at jadx.core.dex.visitors.ModVisitor.visit(ModVisitor.java:68)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:60)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
        /*
        r55 = this;
        r4 = 0;
        r0 = r55;
        r0.drawLinkImageView = r4;
        r4 = 0;
        r0 = r55;
        r0.descriptionLayout = r4;
        r4 = 0;
        r0 = r55;
        r0.titleLayout = r4;
        r4 = 0;
        r0 = r55;
        r0.linkLayout = r4;
        r4 = 0;
        r0 = r55;
        r0.currentPhotoObject = r4;
        r4 = NUM; // 0x41d80000 float:27.0 double:5.457818764E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r0 = r55;
        r0.linkY = r4;
        r0 = r55;
        r4 = r0.inlineResult;
        if (r4 != 0) goto L_0x0041;
    L_0x0029:
        r0 = r55;
        r4 = r0.documentAttach;
        if (r4 != 0) goto L_0x0041;
    L_0x002f:
        r4 = NUM; // 0x42c80000 float:100.0 double:5.53552857E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r6 = NUM; // 0x42c80000 float:100.0 double:5.53552857E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r0 = r55;
        r0.setMeasuredDimension(r4, r6);
    L_0x0040:
        return;
    L_0x0041:
        r49 = android.view.View.MeasureSpec.getSize(r56);
        r4 = org.telegram.messenger.AndroidUtilities.leftBaseline;
        r4 = (float) r4;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = r49 - r4;
        r6 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r46 = r4 - r6;
        r41 = 0;
        r47 = 0;
        r22 = 0;
        r51 = 0;
        r23 = 0;
        r0 = r55;
        r4 = r0.documentAttach;
        if (r4 == 0) goto L_0x03d9;
    L_0x0066:
        r47 = new java.util.ArrayList;
        r47.<init>();
        r0 = r55;
        r4 = r0.documentAttach;
        r4 = r4.thumb;
        r0 = r47;
        r0.add(r4);
    L_0x0076:
        r0 = r55;
        r4 = r0.mediaWebpage;
        if (r4 != 0) goto L_0x019d;
    L_0x007c:
        r0 = r55;
        r4 = r0.inlineResult;
        if (r4 == 0) goto L_0x019d;
    L_0x0082:
        r0 = r55;
        r4 = r0.inlineResult;
        r4 = r4.title;
        if (r4 == 0) goto L_0x00f4;
    L_0x008a:
        r4 = org.telegram.ui.ActionBar.Theme.chat_contextResult_titleTextPaint;	 Catch:{ Exception -> 0x03f8 }
        r0 = r55;	 Catch:{ Exception -> 0x03f8 }
        r6 = r0.inlineResult;	 Catch:{ Exception -> 0x03f8 }
        r6 = r6.title;	 Catch:{ Exception -> 0x03f8 }
        r4 = r4.measureText(r6);	 Catch:{ Exception -> 0x03f8 }
        r8 = (double) r4;	 Catch:{ Exception -> 0x03f8 }
        r8 = java.lang.Math.ceil(r8);	 Catch:{ Exception -> 0x03f8 }
        r0 = (int) r8;	 Catch:{ Exception -> 0x03f8 }
        r52 = r0;	 Catch:{ Exception -> 0x03f8 }
        r0 = r55;	 Catch:{ Exception -> 0x03f8 }
        r4 = r0.inlineResult;	 Catch:{ Exception -> 0x03f8 }
        r4 = r4.title;	 Catch:{ Exception -> 0x03f8 }
        r6 = 10;	 Catch:{ Exception -> 0x03f8 }
        r8 = 32;	 Catch:{ Exception -> 0x03f8 }
        r4 = r4.replace(r6, r8);	 Catch:{ Exception -> 0x03f8 }
        r6 = org.telegram.ui.ActionBar.Theme.chat_contextResult_titleTextPaint;	 Catch:{ Exception -> 0x03f8 }
        r6 = r6.getFontMetricsInt();	 Catch:{ Exception -> 0x03f8 }
        r8 = NUM; // 0x41700000 float:15.0 double:5.424144515E-315;	 Catch:{ Exception -> 0x03f8 }
        r8 = org.telegram.messenger.AndroidUtilities.dp(r8);	 Catch:{ Exception -> 0x03f8 }
        r9 = 0;	 Catch:{ Exception -> 0x03f8 }
        r4 = org.telegram.messenger.Emoji.replaceEmoji(r4, r6, r8, r9);	 Catch:{ Exception -> 0x03f8 }
        r6 = org.telegram.ui.ActionBar.Theme.chat_contextResult_titleTextPaint;	 Catch:{ Exception -> 0x03f8 }
        r0 = r52;	 Catch:{ Exception -> 0x03f8 }
        r1 = r46;	 Catch:{ Exception -> 0x03f8 }
        r8 = java.lang.Math.min(r0, r1);	 Catch:{ Exception -> 0x03f8 }
        r8 = (float) r8;	 Catch:{ Exception -> 0x03f8 }
        r9 = android.text.TextUtils.TruncateAt.END;	 Catch:{ Exception -> 0x03f8 }
        r5 = android.text.TextUtils.ellipsize(r4, r6, r8, r9);	 Catch:{ Exception -> 0x03f8 }
        r4 = new android.text.StaticLayout;	 Catch:{ Exception -> 0x03f8 }
        r6 = org.telegram.ui.ActionBar.Theme.chat_contextResult_titleTextPaint;	 Catch:{ Exception -> 0x03f8 }
        r8 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;	 Catch:{ Exception -> 0x03f8 }
        r8 = org.telegram.messenger.AndroidUtilities.dp(r8);	 Catch:{ Exception -> 0x03f8 }
        r7 = r46 + r8;	 Catch:{ Exception -> 0x03f8 }
        r8 = android.text.Layout.Alignment.ALIGN_NORMAL;	 Catch:{ Exception -> 0x03f8 }
        r9 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;	 Catch:{ Exception -> 0x03f8 }
        r10 = 0;	 Catch:{ Exception -> 0x03f8 }
        r11 = 0;	 Catch:{ Exception -> 0x03f8 }
        r4.<init>(r5, r6, r7, r8, r9, r10, r11);	 Catch:{ Exception -> 0x03f8 }
        r0 = r55;	 Catch:{ Exception -> 0x03f8 }
        r0.titleLayout = r4;	 Catch:{ Exception -> 0x03f8 }
    L_0x00e7:
        r0 = r55;
        r4 = r0.letterDrawable;
        r0 = r55;
        r6 = r0.inlineResult;
        r6 = r6.title;
        r4.setTitle(r6);
    L_0x00f4:
        r0 = r55;
        r4 = r0.inlineResult;
        r4 = r4.description;
        if (r4 == 0) goto L_0x014f;
    L_0x00fc:
        r0 = r55;	 Catch:{ Exception -> 0x03fe }
        r4 = r0.inlineResult;	 Catch:{ Exception -> 0x03fe }
        r4 = r4.description;	 Catch:{ Exception -> 0x03fe }
        r6 = org.telegram.ui.ActionBar.Theme.chat_contextResult_descriptionTextPaint;	 Catch:{ Exception -> 0x03fe }
        r6 = r6.getFontMetricsInt();	 Catch:{ Exception -> 0x03fe }
        r8 = NUM; // 0x41500000 float:13.0 double:5.413783207E-315;	 Catch:{ Exception -> 0x03fe }
        r8 = org.telegram.messenger.AndroidUtilities.dp(r8);	 Catch:{ Exception -> 0x03fe }
        r9 = 0;	 Catch:{ Exception -> 0x03fe }
        r6 = org.telegram.messenger.Emoji.replaceEmoji(r4, r6, r8, r9);	 Catch:{ Exception -> 0x03fe }
        r7 = org.telegram.ui.ActionBar.Theme.chat_contextResult_descriptionTextPaint;	 Catch:{ Exception -> 0x03fe }
        r10 = 0;	 Catch:{ Exception -> 0x03fe }
        r11 = 3;	 Catch:{ Exception -> 0x03fe }
        r8 = r46;	 Catch:{ Exception -> 0x03fe }
        r9 = r46;	 Catch:{ Exception -> 0x03fe }
        r4 = org.telegram.ui.Cells.ChatMessageCell.generateStaticLayout(r6, r7, r8, r9, r10, r11);	 Catch:{ Exception -> 0x03fe }
        r0 = r55;	 Catch:{ Exception -> 0x03fe }
        r0.descriptionLayout = r4;	 Catch:{ Exception -> 0x03fe }
        r0 = r55;	 Catch:{ Exception -> 0x03fe }
        r4 = r0.descriptionLayout;	 Catch:{ Exception -> 0x03fe }
        r4 = r4.getLineCount();	 Catch:{ Exception -> 0x03fe }
        if (r4 <= 0) goto L_0x014f;	 Catch:{ Exception -> 0x03fe }
    L_0x012d:
        r0 = r55;	 Catch:{ Exception -> 0x03fe }
        r4 = r0.descriptionY;	 Catch:{ Exception -> 0x03fe }
        r0 = r55;	 Catch:{ Exception -> 0x03fe }
        r6 = r0.descriptionLayout;	 Catch:{ Exception -> 0x03fe }
        r0 = r55;	 Catch:{ Exception -> 0x03fe }
        r8 = r0.descriptionLayout;	 Catch:{ Exception -> 0x03fe }
        r8 = r8.getLineCount();	 Catch:{ Exception -> 0x03fe }
        r8 = r8 + -1;	 Catch:{ Exception -> 0x03fe }
        r6 = r6.getLineBottom(r8);	 Catch:{ Exception -> 0x03fe }
        r4 = r4 + r6;	 Catch:{ Exception -> 0x03fe }
        r6 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;	 Catch:{ Exception -> 0x03fe }
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);	 Catch:{ Exception -> 0x03fe }
        r4 = r4 + r6;	 Catch:{ Exception -> 0x03fe }
        r0 = r55;	 Catch:{ Exception -> 0x03fe }
        r0.linkY = r4;	 Catch:{ Exception -> 0x03fe }
    L_0x014f:
        r0 = r55;
        r4 = r0.inlineResult;
        r4 = r4.url;
        if (r4 == 0) goto L_0x019d;
    L_0x0157:
        r4 = org.telegram.ui.ActionBar.Theme.chat_contextResult_descriptionTextPaint;	 Catch:{ Exception -> 0x0404 }
        r0 = r55;	 Catch:{ Exception -> 0x0404 }
        r6 = r0.inlineResult;	 Catch:{ Exception -> 0x0404 }
        r6 = r6.url;	 Catch:{ Exception -> 0x0404 }
        r4 = r4.measureText(r6);	 Catch:{ Exception -> 0x0404 }
        r8 = (double) r4;	 Catch:{ Exception -> 0x0404 }
        r8 = java.lang.Math.ceil(r8);	 Catch:{ Exception -> 0x0404 }
        r0 = (int) r8;	 Catch:{ Exception -> 0x0404 }
        r52 = r0;	 Catch:{ Exception -> 0x0404 }
        r0 = r55;	 Catch:{ Exception -> 0x0404 }
        r4 = r0.inlineResult;	 Catch:{ Exception -> 0x0404 }
        r4 = r4.url;	 Catch:{ Exception -> 0x0404 }
        r6 = 10;	 Catch:{ Exception -> 0x0404 }
        r8 = 32;	 Catch:{ Exception -> 0x0404 }
        r4 = r4.replace(r6, r8);	 Catch:{ Exception -> 0x0404 }
        r6 = org.telegram.ui.ActionBar.Theme.chat_contextResult_descriptionTextPaint;	 Catch:{ Exception -> 0x0404 }
        r0 = r52;	 Catch:{ Exception -> 0x0404 }
        r1 = r46;	 Catch:{ Exception -> 0x0404 }
        r8 = java.lang.Math.min(r0, r1);	 Catch:{ Exception -> 0x0404 }
        r8 = (float) r8;	 Catch:{ Exception -> 0x0404 }
        r9 = android.text.TextUtils.TruncateAt.MIDDLE;	 Catch:{ Exception -> 0x0404 }
        r7 = android.text.TextUtils.ellipsize(r4, r6, r8, r9);	 Catch:{ Exception -> 0x0404 }
        r6 = new android.text.StaticLayout;	 Catch:{ Exception -> 0x0404 }
        r8 = org.telegram.ui.ActionBar.Theme.chat_contextResult_descriptionTextPaint;	 Catch:{ Exception -> 0x0404 }
        r10 = android.text.Layout.Alignment.ALIGN_NORMAL;	 Catch:{ Exception -> 0x0404 }
        r11 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;	 Catch:{ Exception -> 0x0404 }
        r12 = 0;	 Catch:{ Exception -> 0x0404 }
        r13 = 0;	 Catch:{ Exception -> 0x0404 }
        r9 = r46;	 Catch:{ Exception -> 0x0404 }
        r6.<init>(r7, r8, r9, r10, r11, r12, r13);	 Catch:{ Exception -> 0x0404 }
        r0 = r55;	 Catch:{ Exception -> 0x0404 }
        r0.linkLayout = r6;	 Catch:{ Exception -> 0x0404 }
    L_0x019d:
        r20 = 0;
        r0 = r55;
        r4 = r0.documentAttach;
        if (r4 == 0) goto L_0x043d;
    L_0x01a5:
        r0 = r55;
        r4 = r0.documentAttach;
        r4 = org.telegram.messenger.MessageObject.isGifDocument(r4);
        if (r4 == 0) goto L_0x040a;
    L_0x01af:
        r0 = r55;
        r4 = r0.documentAttach;
        r4 = r4.thumb;
        r0 = r55;
        r0.currentPhotoObject = r4;
    L_0x01b9:
        r0 = r55;
        r4 = r0.inlineResult;
        if (r4 == 0) goto L_0x0274;
    L_0x01bf:
        r0 = r55;
        r4 = r0.inlineResult;
        r4 = r4.content;
        r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_webDocument;
        if (r4 == 0) goto L_0x01f6;
    L_0x01c9:
        r0 = r55;
        r4 = r0.inlineResult;
        r4 = r4.type;
        if (r4 == 0) goto L_0x01f6;
    L_0x01d1:
        r0 = r55;
        r4 = r0.inlineResult;
        r4 = r4.type;
        r6 = "gif";
        r4 = r4.startsWith(r6);
        if (r4 == 0) goto L_0x046e;
    L_0x01e0:
        r0 = r55;
        r4 = r0.documentAttachType;
        r6 = 2;
        if (r4 == r6) goto L_0x01f6;
    L_0x01e7:
        r0 = r55;
        r4 = r0.inlineResult;
        r0 = r4.content;
        r51 = r0;
        r51 = (org.telegram.tgnet.TLRPC.TL_webDocument) r51;
        r4 = 2;
        r0 = r55;
        r0.documentAttachType = r4;
    L_0x01f6:
        if (r51 != 0) goto L_0x020c;
    L_0x01f8:
        r0 = r55;
        r4 = r0.inlineResult;
        r4 = r4.thumb;
        r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_webDocument;
        if (r4 == 0) goto L_0x020c;
    L_0x0202:
        r0 = r55;
        r4 = r0.inlineResult;
        r0 = r4.thumb;
        r51 = r0;
        r51 = (org.telegram.tgnet.TLRPC.TL_webDocument) r51;
    L_0x020c:
        if (r51 != 0) goto L_0x026e;
    L_0x020e:
        r0 = r55;
        r4 = r0.currentPhotoObject;
        if (r4 != 0) goto L_0x026e;
    L_0x0214:
        if (r41 != 0) goto L_0x026e;
    L_0x0216:
        r0 = r55;
        r4 = r0.inlineResult;
        r4 = r4.send_message;
        r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_botInlineMessageMediaVenue;
        if (r4 != 0) goto L_0x022a;
    L_0x0220:
        r0 = r55;
        r4 = r0.inlineResult;
        r4 = r4.send_message;
        r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_botInlineMessageMediaGeo;
        if (r4 == 0) goto L_0x026e;
    L_0x022a:
        r0 = r55;
        r4 = r0.inlineResult;
        r4 = r4.send_message;
        r4 = r4.geo;
        r10 = r4.lat;
        r0 = r55;
        r4 = r0.inlineResult;
        r4 = r4.send_message;
        r4 = r4.geo;
        r12 = r4._long;
        r0 = r55;
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.MessagesController.getInstance(r4);
        r4 = r4.mapProvider;
        r6 = 2;
        if (r4 != r6) goto L_0x049f;
    L_0x024b:
        r0 = r55;
        r4 = r0.inlineResult;
        r4 = r4.send_message;
        r4 = r4.geo;
        r6 = 72;
        r8 = 72;
        r9 = 15;
        r14 = 2;
        r15 = org.telegram.messenger.AndroidUtilities.density;
        r0 = (double) r15;
        r16 = r0;
        r16 = java.lang.Math.ceil(r16);
        r0 = r16;
        r15 = (int) r0;
        r14 = java.lang.Math.min(r14, r15);
        r22 = org.telegram.messenger.WebFile.createWithGeoPoint(r4, r6, r8, r9, r14);
    L_0x026e:
        if (r51 == 0) goto L_0x0274;
    L_0x0270:
        r22 = org.telegram.messenger.WebFile.createWithWebDocument(r51);
    L_0x0274:
        r50 = 0;
        r43 = 0;
        r0 = r55;
        r4 = r0.documentAttach;
        if (r4 == 0) goto L_0x02b4;
    L_0x027e:
        r40 = 0;
    L_0x0280:
        r0 = r55;
        r4 = r0.documentAttach;
        r4 = r4.attributes;
        r4 = r4.size();
        r0 = r40;
        if (r0 >= r4) goto L_0x02b4;
    L_0x028e:
        r0 = r55;
        r4 = r0.documentAttach;
        r4 = r4.attributes;
        r0 = r40;
        r39 = r4.get(r0);
        r39 = (org.telegram.tgnet.TLRPC.DocumentAttribute) r39;
        r0 = r39;
        r4 = r0 instanceof org.telegram.tgnet.TLRPC.TL_documentAttributeImageSize;
        if (r4 != 0) goto L_0x02a8;
    L_0x02a2:
        r0 = r39;
        r4 = r0 instanceof org.telegram.tgnet.TLRPC.TL_documentAttributeVideo;
        if (r4 == 0) goto L_0x04b1;
    L_0x02a8:
        r0 = r39;
        r0 = r0.f27w;
        r50 = r0;
        r0 = r39;
        r0 = r0.f26h;
        r43 = r0;
    L_0x02b4:
        if (r50 == 0) goto L_0x02b8;
    L_0x02b6:
        if (r43 != 0) goto L_0x02d5;
    L_0x02b8:
        r0 = r55;
        r4 = r0.currentPhotoObject;
        if (r4 == 0) goto L_0x04b5;
    L_0x02be:
        if (r41 == 0) goto L_0x02c5;
    L_0x02c0:
        r4 = -1;
        r0 = r41;
        r0.size = r4;
    L_0x02c5:
        r0 = r55;
        r4 = r0.currentPhotoObject;
        r0 = r4.f34w;
        r50 = r0;
        r0 = r55;
        r4 = r0.currentPhotoObject;
        r0 = r4.f33h;
        r43 = r0;
    L_0x02d5:
        if (r50 == 0) goto L_0x02d9;
    L_0x02d7:
        if (r43 != 0) goto L_0x02e1;
    L_0x02d9:
        r4 = NUM; // 0x42a00000 float:80.0 double:5.522576936E-315;
        r43 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r50 = r43;
    L_0x02e1:
        r0 = r55;
        r4 = r0.documentAttach;
        if (r4 != 0) goto L_0x02f1;
    L_0x02e7:
        r0 = r55;
        r4 = r0.currentPhotoObject;
        if (r4 != 0) goto L_0x02f1;
    L_0x02ed:
        if (r22 != 0) goto L_0x02f1;
    L_0x02ef:
        if (r23 == 0) goto L_0x037e;
    L_0x02f1:
        r28 = "52_52_b";
        r0 = r55;
        r4 = r0.mediaWebpage;
        if (r4 == 0) goto L_0x0506;
    L_0x02fa:
        r0 = r50;
        r4 = (float) r0;
        r0 = r43;
        r6 = (float) r0;
        r8 = NUM; // 0x42a00000 float:80.0 double:5.522576936E-315;
        r8 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r8 = (float) r8;
        r6 = r6 / r8;
        r4 = r4 / r6;
        r0 = (int) r4;
        r52 = r0;
        r0 = r55;
        r4 = r0.documentAttachType;
        r6 = 2;
        if (r4 != r6) goto L_0x04cb;
    L_0x0313:
        r4 = java.util.Locale.US;
        r6 = "%d_%d_b";
        r8 = 2;
        r8 = new java.lang.Object[r8];
        r9 = 0;
        r0 = r52;
        r14 = (float) r0;
        r15 = org.telegram.messenger.AndroidUtilities.density;
        r14 = r14 / r15;
        r14 = (int) r14;
        r14 = java.lang.Integer.valueOf(r14);
        r8[r9] = r14;
        r9 = 1;
        r14 = 80;
        r14 = java.lang.Integer.valueOf(r14);
        r8[r9] = r14;
        r18 = java.lang.String.format(r4, r6, r8);
        r28 = r18;
    L_0x0338:
        r0 = r55;
        r6 = r0.linkImageView;
        r0 = r55;
        r4 = r0.documentAttachType;
        r8 = 6;
        if (r4 != r8) goto L_0x050b;
    L_0x0343:
        r4 = 1;
    L_0x0344:
        r6.setAspectFit(r4);
        r0 = r55;
        r4 = r0.documentAttachType;
        r6 = 2;
        if (r4 != r6) goto L_0x053a;
    L_0x034e:
        r0 = r55;
        r4 = r0.documentAttach;
        if (r4 == 0) goto L_0x0512;
    L_0x0354:
        r0 = r55;
        r14 = r0.linkImageView;
        r0 = r55;
        r15 = r0.documentAttach;
        r16 = 0;
        r0 = r55;
        r4 = r0.currentPhotoObject;
        if (r4 == 0) goto L_0x050e;
    L_0x0364:
        r0 = r55;
        r4 = r0.currentPhotoObject;
        r0 = r4.location;
        r17 = r0;
    L_0x036c:
        r0 = r55;
        r4 = r0.documentAttach;
        r0 = r4.size;
        r19 = r0;
        r21 = 0;
        r14.setImage(r15, r16, r17, r18, r19, r20, r21);
    L_0x0379:
        r4 = 1;
        r0 = r55;
        r0.drawLinkImageView = r4;
    L_0x037e:
        r0 = r55;
        r4 = r0.mediaWebpage;
        if (r4 == 0) goto L_0x0592;
    L_0x0384:
        r52 = r49;
        r44 = android.view.View.MeasureSpec.getSize(r57);
        if (r44 != 0) goto L_0x0392;
    L_0x038c:
        r4 = NUM; // 0x42c80000 float:100.0 double:5.53552857E-315;
        r44 = org.telegram.messenger.AndroidUtilities.dp(r4);
    L_0x0392:
        r0 = r55;
        r1 = r52;
        r2 = r44;
        r0.setMeasuredDimension(r1, r2);
        r4 = NUM; // 0x41c00000 float:24.0 double:5.450047783E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = r52 - r4;
        r53 = r4 / 2;
        r4 = NUM; // 0x41c00000 float:24.0 double:5.450047783E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = r44 - r4;
        r54 = r4 / 2;
        r0 = r55;
        r4 = r0.radialProgress;
        r6 = NUM; // 0x41c00000 float:24.0 double:5.450047783E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r6 = r6 + r53;
        r8 = NUM; // 0x41c00000 float:24.0 double:5.450047783E-315;
        r8 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r8 = r8 + r54;
        r0 = r53;
        r1 = r54;
        r4.setProgressRect(r0, r1, r6, r8);
        r0 = r55;
        r4 = r0.linkImageView;
        r6 = 0;
        r8 = 0;
        r0 = r52;
        r1 = r44;
        r4.setImageCoords(r6, r8, r0, r1);
        goto L_0x0040;
    L_0x03d9:
        r0 = r55;
        r4 = r0.inlineResult;
        if (r4 == 0) goto L_0x0076;
    L_0x03df:
        r0 = r55;
        r4 = r0.inlineResult;
        r4 = r4.photo;
        if (r4 == 0) goto L_0x0076;
    L_0x03e7:
        r47 = new java.util.ArrayList;
        r0 = r55;
        r4 = r0.inlineResult;
        r4 = r4.photo;
        r4 = r4.sizes;
        r0 = r47;
        r0.<init>(r4);
        goto L_0x0076;
    L_0x03f8:
        r42 = move-exception;
        org.telegram.messenger.FileLog.m8e(r42);
        goto L_0x00e7;
    L_0x03fe:
        r42 = move-exception;
        org.telegram.messenger.FileLog.m8e(r42);
        goto L_0x014f;
    L_0x0404:
        r42 = move-exception;
        org.telegram.messenger.FileLog.m8e(r42);
        goto L_0x019d;
    L_0x040a:
        r0 = r55;
        r4 = r0.documentAttach;
        r4 = org.telegram.messenger.MessageObject.isStickerDocument(r4);
        if (r4 == 0) goto L_0x0423;
    L_0x0414:
        r0 = r55;
        r4 = r0.documentAttach;
        r4 = r4.thumb;
        r0 = r55;
        r0.currentPhotoObject = r4;
        r20 = "webp";
        goto L_0x01b9;
    L_0x0423:
        r0 = r55;
        r4 = r0.documentAttachType;
        r6 = 5;
        if (r4 == r6) goto L_0x01b9;
    L_0x042a:
        r0 = r55;
        r4 = r0.documentAttachType;
        r6 = 3;
        if (r4 == r6) goto L_0x01b9;
    L_0x0431:
        r0 = r55;
        r4 = r0.documentAttach;
        r4 = r4.thumb;
        r0 = r55;
        r0.currentPhotoObject = r4;
        goto L_0x01b9;
    L_0x043d:
        r0 = r55;
        r4 = r0.inlineResult;
        if (r4 == 0) goto L_0x01b9;
    L_0x0443:
        r0 = r55;
        r4 = r0.inlineResult;
        r4 = r4.photo;
        if (r4 == 0) goto L_0x01b9;
    L_0x044b:
        r4 = org.telegram.messenger.AndroidUtilities.getPhotoSize();
        r6 = 1;
        r0 = r47;
        r4 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r0, r4, r6);
        r0 = r55;
        r0.currentPhotoObject = r4;
        r4 = 80;
        r0 = r47;
        r41 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r0, r4);
        r0 = r55;
        r4 = r0.currentPhotoObject;
        r0 = r41;
        if (r0 != r4) goto L_0x01b9;
    L_0x046a:
        r41 = 0;
        goto L_0x01b9;
    L_0x046e:
        r0 = r55;
        r4 = r0.inlineResult;
        r4 = r4.type;
        r6 = "photo";
        r4 = r4.equals(r6);
        if (r4 == 0) goto L_0x01f6;
    L_0x047d:
        r0 = r55;
        r4 = r0.inlineResult;
        r4 = r4.thumb;
        r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_webDocument;
        if (r4 == 0) goto L_0x0493;
    L_0x0487:
        r0 = r55;
        r4 = r0.inlineResult;
        r0 = r4.thumb;
        r51 = r0;
        r51 = (org.telegram.tgnet.TLRPC.TL_webDocument) r51;
        goto L_0x01f6;
    L_0x0493:
        r0 = r55;
        r4 = r0.inlineResult;
        r0 = r4.content;
        r51 = r0;
        r51 = (org.telegram.tgnet.TLRPC.TL_webDocument) r51;
        goto L_0x01f6;
    L_0x049f:
        r0 = r55;
        r9 = r0.currentAccount;
        r14 = 72;
        r15 = 72;
        r16 = 1;
        r17 = 15;
        r23 = org.telegram.messenger.AndroidUtilities.formapMapUrl(r9, r10, r12, r14, r15, r16, r17);
        goto L_0x026e;
    L_0x04b1:
        r40 = r40 + 1;
        goto L_0x0280;
    L_0x04b5:
        r0 = r55;
        r4 = r0.inlineResult;
        if (r4 == 0) goto L_0x02d5;
    L_0x04bb:
        r0 = r55;
        r4 = r0.inlineResult;
        r48 = org.telegram.messenger.MessageObject.getInlineResultWidthAndHeight(r4);
        r4 = 0;
        r50 = r48[r4];
        r4 = 1;
        r43 = r48[r4];
        goto L_0x02d5;
    L_0x04cb:
        r4 = java.util.Locale.US;
        r6 = "%d_%d";
        r8 = 2;
        r8 = new java.lang.Object[r8];
        r9 = 0;
        r0 = r52;
        r14 = (float) r0;
        r15 = org.telegram.messenger.AndroidUtilities.density;
        r14 = r14 / r15;
        r14 = (int) r14;
        r14 = java.lang.Integer.valueOf(r14);
        r8[r9] = r14;
        r9 = 1;
        r14 = 80;
        r14 = java.lang.Integer.valueOf(r14);
        r8[r9] = r14;
        r18 = java.lang.String.format(r4, r6, r8);
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r0 = r18;
        r4 = r4.append(r0);
        r6 = "_b";
        r4 = r4.append(r6);
        r28 = r4.toString();
        goto L_0x0338;
    L_0x0506:
        r18 = "52_52";
        goto L_0x0338;
    L_0x050b:
        r4 = 0;
        goto L_0x0344;
    L_0x050e:
        r17 = 0;
        goto L_0x036c;
    L_0x0512:
        r0 = r55;
        r0 = r0.linkImageView;
        r21 = r0;
        r24 = 0;
        r25 = 0;
        r0 = r55;
        r4 = r0.currentPhotoObject;
        if (r4 == 0) goto L_0x0537;
    L_0x0522:
        r0 = r55;
        r4 = r0.currentPhotoObject;
        r0 = r4.location;
        r26 = r0;
    L_0x052a:
        r28 = -1;
        r30 = 1;
        r27 = r18;
        r29 = r20;
        r21.setImage(r22, r23, r24, r25, r26, r27, r28, r29, r30);
        goto L_0x0379;
    L_0x0537:
        r26 = 0;
        goto L_0x052a;
    L_0x053a:
        r0 = r55;
        r4 = r0.currentPhotoObject;
        if (r4 == 0) goto L_0x056c;
    L_0x0540:
        r0 = r55;
        r0 = r0.linkImageView;
        r24 = r0;
        r0 = r55;
        r4 = r0.currentPhotoObject;
        r0 = r4.location;
        r25 = r0;
        if (r41 == 0) goto L_0x0569;
    L_0x0550:
        r0 = r41;
        r0 = r0.location;
        r27 = r0;
    L_0x0556:
        r0 = r55;
        r4 = r0.currentPhotoObject;
        r0 = r4.size;
        r29 = r0;
        r31 = 0;
        r26 = r18;
        r30 = r20;
        r24.setImage(r25, r26, r27, r28, r29, r30, r31);
        goto L_0x0379;
    L_0x0569:
        r27 = 0;
        goto L_0x0556;
    L_0x056c:
        r0 = r55;
        r0 = r0.linkImageView;
        r29 = r0;
        r33 = 0;
        if (r41 == 0) goto L_0x058f;
    L_0x0576:
        r0 = r41;
        r0 = r0.location;
        r34 = r0;
    L_0x057c:
        r36 = -1;
        r38 = 1;
        r30 = r22;
        r31 = r23;
        r32 = r18;
        r35 = r28;
        r37 = r20;
        r29.setImage(r30, r31, r32, r33, r34, r35, r36, r37, r38);
        goto L_0x0379;
    L_0x058f:
        r34 = 0;
        goto L_0x057c;
    L_0x0592:
        r44 = 0;
        r0 = r55;
        r4 = r0.titleLayout;
        if (r4 == 0) goto L_0x05b8;
    L_0x059a:
        r0 = r55;
        r4 = r0.titleLayout;
        r4 = r4.getLineCount();
        if (r4 == 0) goto L_0x05b8;
    L_0x05a4:
        r0 = r55;
        r4 = r0.titleLayout;
        r0 = r55;
        r6 = r0.titleLayout;
        r6 = r6.getLineCount();
        r6 = r6 + -1;
        r4 = r4.getLineBottom(r6);
        r44 = r44 + r4;
    L_0x05b8:
        r0 = r55;
        r4 = r0.descriptionLayout;
        if (r4 == 0) goto L_0x05dc;
    L_0x05be:
        r0 = r55;
        r4 = r0.descriptionLayout;
        r4 = r4.getLineCount();
        if (r4 == 0) goto L_0x05dc;
    L_0x05c8:
        r0 = r55;
        r4 = r0.descriptionLayout;
        r0 = r55;
        r6 = r0.descriptionLayout;
        r6 = r6.getLineCount();
        r6 = r6 + -1;
        r4 = r4.getLineBottom(r6);
        r44 = r44 + r4;
    L_0x05dc:
        r0 = r55;
        r4 = r0.linkLayout;
        if (r4 == 0) goto L_0x0600;
    L_0x05e2:
        r0 = r55;
        r4 = r0.linkLayout;
        r4 = r4.getLineCount();
        if (r4 <= 0) goto L_0x0600;
    L_0x05ec:
        r0 = r55;
        r4 = r0.linkLayout;
        r0 = r55;
        r6 = r0.linkLayout;
        r6 = r6.getLineCount();
        r6 = r6 + -1;
        r4 = r4.getLineBottom(r6);
        r44 = r44 + r4;
    L_0x0600:
        r4 = NUM; // 0x42500000 float:52.0 double:5.496673668E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r0 = r44;
        r44 = java.lang.Math.max(r4, r0);
        r6 = android.view.View.MeasureSpec.getSize(r56);
        r4 = NUM; // 0x42880000 float:68.0 double:5.514805956E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r8 = NUM; // 0x41800000 float:16.0 double:5.42932517E-315;
        r8 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r8 = r8 + r44;
        r8 = java.lang.Math.max(r4, r8);
        r0 = r55;
        r4 = r0.needDivider;
        if (r4 == 0) goto L_0x06a3;
    L_0x0628:
        r4 = 1;
    L_0x0629:
        r4 = r4 + r8;
        r0 = r55;
        r0.setMeasuredDimension(r6, r4);
        r4 = NUM; // 0x42500000 float:52.0 double:5.496673668E-315;
        r45 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = org.telegram.messenger.LocaleController.isRTL;
        if (r4 == 0) goto L_0x06a5;
    L_0x0639:
        r4 = android.view.View.MeasureSpec.getSize(r56);
        r6 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 - r6;
        r53 = r4 - r45;
    L_0x0646:
        r0 = r55;
        r4 = r0.letterDrawable;
        r6 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r8 = r53 + r45;
        r9 = NUM; // 0x42700000 float:60.0 double:5.507034975E-315;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r0 = r53;
        r4.setBounds(r0, r6, r8, r9);
        r0 = r55;
        r4 = r0.linkImageView;
        r6 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r0 = r53;
        r1 = r45;
        r2 = r45;
        r4.setImageCoords(r0, r6, r1, r2);
        r0 = r55;
        r4 = r0.documentAttachType;
        r6 = 3;
        if (r4 == r6) goto L_0x067e;
    L_0x0677:
        r0 = r55;
        r4 = r0.documentAttachType;
        r6 = 5;
        if (r4 != r6) goto L_0x0040;
    L_0x067e:
        r0 = r55;
        r4 = r0.radialProgress;
        r6 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r6 = r6 + r53;
        r8 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
        r8 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r9 = NUM; // 0x42400000 float:48.0 double:5.491493014E-315;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r9 = r9 + r53;
        r14 = NUM; // 0x42600000 float:56.0 double:5.50185432E-315;
        r14 = org.telegram.messenger.AndroidUtilities.dp(r14);
        r4.setProgressRect(r6, r8, r9, r14);
        goto L_0x0040;
    L_0x06a3:
        r4 = 0;
        goto L_0x0629;
    L_0x06a5:
        r4 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        r53 = org.telegram.messenger.AndroidUtilities.dp(r4);
        goto L_0x0646;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ContextLinkCell.onMeasure(int, int):void");
    }

    private void setAttachType() {
        this.currentMessageObject = null;
        this.documentAttachType = 0;
        if (this.documentAttach != null) {
            if (MessageObject.isGifDocument(this.documentAttach)) {
                this.documentAttachType = 2;
            } else if (MessageObject.isStickerDocument(this.documentAttach)) {
                this.documentAttachType = 6;
            } else if (MessageObject.isMusicDocument(this.documentAttach)) {
                this.documentAttachType = 5;
            } else if (MessageObject.isVoiceDocument(this.documentAttach)) {
                this.documentAttachType = 3;
            }
        } else if (this.inlineResult != null) {
            if (this.inlineResult.photo != null) {
                this.documentAttachType = 7;
            } else if (this.inlineResult.type.equals(MimeTypes.BASE_TYPE_AUDIO)) {
                this.documentAttachType = 5;
            } else if (this.inlineResult.type.equals("voice")) {
                this.documentAttachType = 3;
            }
        }
        if (this.documentAttachType == 3 || this.documentAttachType == 5) {
            TL_message message = new TL_message();
            message.out = true;
            message.id = -Utilities.random.nextInt();
            message.to_id = new TL_peerUser();
            Peer peer = message.to_id;
            int clientUserId = UserConfig.getInstance(this.currentAccount).getClientUserId();
            message.from_id = clientUserId;
            peer.user_id = clientUserId;
            message.date = (int) (System.currentTimeMillis() / 1000);
            message.message = TtmlNode.ANONYMOUS_REGION_ID;
            message.media = new TL_messageMediaDocument();
            MessageMedia messageMedia = message.media;
            messageMedia.flags |= 3;
            message.media.document = new TL_document();
            message.flags |= 768;
            if (this.documentAttach != null) {
                message.media.document = this.documentAttach;
                message.attachPath = TtmlNode.ANONYMOUS_REGION_ID;
            } else {
                String str;
                String ext = ImageLoader.getHttpUrlExtension(this.inlineResult.content.url, this.documentAttachType == 5 ? "mp3" : "ogg");
                message.media.document.id = 0;
                message.media.document.access_hash = 0;
                message.media.document.date = message.date;
                message.media.document.mime_type = "audio/" + ext;
                message.media.document.size = 0;
                message.media.document.thumb = new TL_photoSizeEmpty();
                message.media.document.thumb.type = "s";
                message.media.document.dc_id = 0;
                TL_documentAttributeAudio attributeAudio = new TL_documentAttributeAudio();
                attributeAudio.duration = MessageObject.getInlineResultDuration(this.inlineResult);
                attributeAudio.title = this.inlineResult.title != null ? this.inlineResult.title : TtmlNode.ANONYMOUS_REGION_ID;
                attributeAudio.performer = this.inlineResult.description != null ? this.inlineResult.description : TtmlNode.ANONYMOUS_REGION_ID;
                attributeAudio.flags |= 3;
                if (this.documentAttachType == 3) {
                    attributeAudio.voice = true;
                }
                message.media.document.attributes.add(attributeAudio);
                TL_documentAttributeFilename fileName = new TL_documentAttributeFilename();
                fileName.file_name = Utilities.MD5(this.inlineResult.content.url) + "." + ImageLoader.getHttpUrlExtension(this.inlineResult.content.url, this.documentAttachType == 5 ? "mp3" : "ogg");
                message.media.document.attributes.add(fileName);
                File directory = FileLoader.getDirectory(4);
                StringBuilder append = new StringBuilder().append(Utilities.MD5(this.inlineResult.content.url)).append(".");
                String str2 = this.inlineResult.content.url;
                if (this.documentAttachType == 5) {
                    str = "mp3";
                } else {
                    str = "ogg";
                }
                message.attachPath = new File(directory, append.append(ImageLoader.getHttpUrlExtension(str2, str)).toString()).getAbsolutePath();
            }
            this.currentMessageObject = new MessageObject(this.currentAccount, message, false);
        }
    }

    public void setLink(BotInlineResult contextResult, boolean media, boolean divider, boolean shadow) {
        this.needDivider = divider;
        this.needShadow = shadow;
        this.inlineResult = contextResult;
        if (this.inlineResult == null || this.inlineResult.document == null) {
            this.documentAttach = null;
        } else {
            this.documentAttach = this.inlineResult.document;
        }
        this.mediaWebpage = media;
        setAttachType();
        requestLayout();
        updateButtonState(false);
    }

    public void setGif(Document document, boolean divider) {
        this.needDivider = divider;
        this.needShadow = false;
        this.inlineResult = null;
        this.documentAttach = document;
        this.mediaWebpage = true;
        setAttachType();
        requestLayout();
        updateButtonState(false);
    }

    public boolean isSticker() {
        return this.documentAttachType == 6;
    }

    public boolean showingBitmap() {
        return this.linkImageView.getBitmap() != null;
    }

    public Document getDocument() {
        return this.documentAttach;
    }

    public ImageReceiver getPhotoImage() {
        return this.linkImageView;
    }

    public void setScaled(boolean value) {
        this.scaled = value;
        this.lastUpdateTime = System.currentTimeMillis();
        invalidate();
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.drawLinkImageView) {
            this.linkImageView.onDetachedFromWindow();
        }
        DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.drawLinkImageView && this.linkImageView.onAttachedToWindow()) {
            updateButtonState(false);
        }
    }

    public MessageObject getMessageObject() {
        return this.currentMessageObject;
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (this.mediaWebpage || this.delegate == null || this.inlineResult == null) {
            return super.onTouchEvent(event);
        }
        int x = (int) event.getX();
        int y = (int) event.getY();
        boolean result = false;
        int side = AndroidUtilities.dp(48.0f);
        if (this.documentAttachType == 3 || this.documentAttachType == 5) {
            boolean area = this.letterDrawable.getBounds().contains(x, y);
            if (event.getAction() == 0) {
                if (area) {
                    this.buttonPressed = true;
                    invalidate();
                    result = true;
                    this.radialProgress.swapBackground(getDrawableForCurrentState());
                }
            } else if (this.buttonPressed) {
                if (event.getAction() == 1) {
                    this.buttonPressed = false;
                    playSoundEffect(0);
                    didPressedButton();
                    invalidate();
                } else if (event.getAction() == 3) {
                    this.buttonPressed = false;
                    invalidate();
                } else if (event.getAction() == 2 && !area) {
                    this.buttonPressed = false;
                    invalidate();
                }
                this.radialProgress.swapBackground(getDrawableForCurrentState());
            }
        } else if (!(this.inlineResult == null || this.inlineResult.content == null || TextUtils.isEmpty(this.inlineResult.content.url))) {
            if (event.getAction() == 0) {
                if (this.letterDrawable.getBounds().contains(x, y)) {
                    this.buttonPressed = true;
                    result = true;
                }
            } else if (this.buttonPressed) {
                if (event.getAction() == 1) {
                    this.buttonPressed = false;
                    playSoundEffect(0);
                    this.delegate.didPressedImage(this);
                } else if (event.getAction() == 3) {
                    this.buttonPressed = false;
                } else if (event.getAction() == 2 && !this.letterDrawable.getBounds().contains(x, y)) {
                    this.buttonPressed = false;
                }
            }
        }
        if (result) {
            return result;
        }
        return super.onTouchEvent(event);
    }

    private void didPressedButton() {
        if (this.documentAttachType != 3 && this.documentAttachType != 5) {
            return;
        }
        if (this.buttonState == 0) {
            if (MediaController.getInstance().playMessage(this.currentMessageObject)) {
                this.buttonState = 1;
                this.radialProgress.setBackground(getDrawableForCurrentState(), false, false);
                invalidate();
            }
        } else if (this.buttonState == 1) {
            if (MediaController.getInstance().pauseMessage(this.currentMessageObject)) {
                this.buttonState = 0;
                this.radialProgress.setBackground(getDrawableForCurrentState(), false, false);
                invalidate();
            }
        } else if (this.buttonState == 2) {
            this.radialProgress.setProgress(0.0f, false);
            if (this.documentAttach != null) {
                FileLoader.getInstance(this.currentAccount).loadFile(this.documentAttach, true, 0);
            } else if (this.inlineResult.content instanceof TL_webDocument) {
                FileLoader.getInstance(this.currentAccount).loadFile(WebFile.createWithWebDocument(this.inlineResult.content), true, 1);
            }
            this.buttonState = 4;
            this.radialProgress.setBackground(getDrawableForCurrentState(), true, false);
            invalidate();
        } else if (this.buttonState == 4) {
            if (this.documentAttach != null) {
                FileLoader.getInstance(this.currentAccount).cancelLoadFile(this.documentAttach);
            } else if (this.inlineResult.content instanceof TL_webDocument) {
                FileLoader.getInstance(this.currentAccount).cancelLoadFile(WebFile.createWithWebDocument(this.inlineResult.content));
            }
            this.buttonState = 2;
            this.radialProgress.setBackground(getDrawableForCurrentState(), false, false);
            invalidate();
        }
    }

    protected void onDraw(Canvas canvas) {
        if (this.titleLayout != null) {
            canvas.save();
            canvas.translate((float) AndroidUtilities.dp(LocaleController.isRTL ? 8.0f : (float) AndroidUtilities.leftBaseline), (float) this.titleY);
            this.titleLayout.draw(canvas);
            canvas.restore();
        }
        if (this.descriptionLayout != null) {
            Theme.chat_contextResult_descriptionTextPaint.setColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2));
            canvas.save();
            canvas.translate((float) AndroidUtilities.dp(LocaleController.isRTL ? 8.0f : (float) AndroidUtilities.leftBaseline), (float) this.descriptionY);
            this.descriptionLayout.draw(canvas);
            canvas.restore();
        }
        if (this.linkLayout != null) {
            Theme.chat_contextResult_descriptionTextPaint.setColor(Theme.getColor(Theme.key_windowBackgroundWhiteLinkText));
            canvas.save();
            canvas.translate((float) AndroidUtilities.dp(LocaleController.isRTL ? 8.0f : (float) AndroidUtilities.leftBaseline), (float) this.linkY);
            this.linkLayout.draw(canvas);
            canvas.restore();
        }
        int w;
        int h;
        int x;
        int y;
        if (this.mediaWebpage) {
            if (this.inlineResult != null && ((this.inlineResult.send_message instanceof TL_botInlineMessageMediaGeo) || (this.inlineResult.send_message instanceof TL_botInlineMessageMediaVenue))) {
                w = Theme.chat_inlineResultLocation.getIntrinsicWidth();
                h = Theme.chat_inlineResultLocation.getIntrinsicHeight();
                x = this.linkImageView.getImageX() + ((this.linkImageView.getImageWidth() - w) / 2);
                y = this.linkImageView.getImageY() + ((this.linkImageView.getImageHeight() - h) / 2);
                canvas.drawRect((float) this.linkImageView.getImageX(), (float) this.linkImageView.getImageY(), (float) (this.linkImageView.getImageX() + this.linkImageView.getImageWidth()), (float) (this.linkImageView.getImageY() + this.linkImageView.getImageHeight()), LetterDrawable.paint);
                Theme.chat_inlineResultLocation.setBounds(x, y, x + w, y + h);
                Theme.chat_inlineResultLocation.draw(canvas);
            }
        } else if (this.documentAttachType == 3 || this.documentAttachType == 5) {
            this.radialProgress.setProgressColor(Theme.getColor(this.buttonPressed ? Theme.key_chat_inAudioSelectedProgress : Theme.key_chat_inAudioProgress));
            this.radialProgress.draw(canvas);
        } else if (this.inlineResult != null && this.inlineResult.type.equals("file")) {
            w = Theme.chat_inlineResultFile.getIntrinsicWidth();
            h = Theme.chat_inlineResultFile.getIntrinsicHeight();
            x = this.linkImageView.getImageX() + ((AndroidUtilities.dp(52.0f) - w) / 2);
            y = this.linkImageView.getImageY() + ((AndroidUtilities.dp(52.0f) - h) / 2);
            canvas.drawRect((float) this.linkImageView.getImageX(), (float) this.linkImageView.getImageY(), (float) (this.linkImageView.getImageX() + AndroidUtilities.dp(52.0f)), (float) (this.linkImageView.getImageY() + AndroidUtilities.dp(52.0f)), LetterDrawable.paint);
            Theme.chat_inlineResultFile.setBounds(x, y, x + w, y + h);
            Theme.chat_inlineResultFile.draw(canvas);
        } else if (this.inlineResult != null && (this.inlineResult.type.equals(MimeTypes.BASE_TYPE_AUDIO) || this.inlineResult.type.equals("voice"))) {
            w = Theme.chat_inlineResultAudio.getIntrinsicWidth();
            h = Theme.chat_inlineResultAudio.getIntrinsicHeight();
            x = this.linkImageView.getImageX() + ((AndroidUtilities.dp(52.0f) - w) / 2);
            y = this.linkImageView.getImageY() + ((AndroidUtilities.dp(52.0f) - h) / 2);
            canvas.drawRect((float) this.linkImageView.getImageX(), (float) this.linkImageView.getImageY(), (float) (this.linkImageView.getImageX() + AndroidUtilities.dp(52.0f)), (float) (this.linkImageView.getImageY() + AndroidUtilities.dp(52.0f)), LetterDrawable.paint);
            Theme.chat_inlineResultAudio.setBounds(x, y, x + w, y + h);
            Theme.chat_inlineResultAudio.draw(canvas);
        } else if (this.inlineResult == null || !(this.inlineResult.type.equals("venue") || this.inlineResult.type.equals("geo"))) {
            this.letterDrawable.draw(canvas);
        } else {
            w = Theme.chat_inlineResultLocation.getIntrinsicWidth();
            h = Theme.chat_inlineResultLocation.getIntrinsicHeight();
            x = this.linkImageView.getImageX() + ((AndroidUtilities.dp(52.0f) - w) / 2);
            y = this.linkImageView.getImageY() + ((AndroidUtilities.dp(52.0f) - h) / 2);
            canvas.drawRect((float) this.linkImageView.getImageX(), (float) this.linkImageView.getImageY(), (float) (this.linkImageView.getImageX() + AndroidUtilities.dp(52.0f)), (float) (this.linkImageView.getImageY() + AndroidUtilities.dp(52.0f)), LetterDrawable.paint);
            Theme.chat_inlineResultLocation.setBounds(x, y, x + w, y + h);
            Theme.chat_inlineResultLocation.draw(canvas);
        }
        if (this.drawLinkImageView) {
            if (this.inlineResult != null) {
                this.linkImageView.setVisible(!PhotoViewer.isShowingImage(this.inlineResult), false);
            }
            canvas.save();
            if ((this.scaled && this.scale != 0.8f) || !(this.scaled || this.scale == 1.0f)) {
                long newTime = System.currentTimeMillis();
                long dt = newTime - this.lastUpdateTime;
                this.lastUpdateTime = newTime;
                if (!this.scaled || this.scale == 0.8f) {
                    this.scale += ((float) dt) / 400.0f;
                    if (this.scale > 1.0f) {
                        this.scale = 1.0f;
                    }
                } else {
                    this.scale -= ((float) dt) / 400.0f;
                    if (this.scale < 0.8f) {
                        this.scale = 0.8f;
                    }
                }
                invalidate();
            }
            canvas.scale(this.scale, this.scale, (float) (getMeasuredWidth() / 2), (float) (getMeasuredHeight() / 2));
            this.linkImageView.draw(canvas);
            canvas.restore();
        }
        if (this.mediaWebpage && (this.documentAttachType == 7 || this.documentAttachType == 2)) {
            this.radialProgress.draw(canvas);
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

    private Drawable getDrawableForCurrentState() {
        int i = 1;
        if (this.documentAttachType != 3 && this.documentAttachType != 5) {
            return this.buttonState == 1 ? Theme.chat_photoStatesDrawables[5][0] : null;
        } else {
            if (this.buttonState == -1) {
                return null;
            }
            this.radialProgress.setAlphaForPrevious(false);
            Drawable[] drawableArr = Theme.chat_fileStatesDrawable[this.buttonState + 5];
            if (!this.buttonPressed) {
                i = 0;
            }
            return drawableArr[i];
        }
    }

    public void updateButtonState(boolean animated) {
        float setProgress = 0.0f;
        String fileName = null;
        File cacheFile = null;
        if (this.documentAttachType == 5 || this.documentAttachType == 3) {
            if (this.documentAttach != null) {
                fileName = FileLoader.getAttachFileName(this.documentAttach);
                cacheFile = FileLoader.getPathToAttach(this.documentAttach);
            } else if (this.inlineResult.content instanceof TL_webDocument) {
                fileName = Utilities.MD5(this.inlineResult.content.url) + "." + ImageLoader.getHttpUrlExtension(this.inlineResult.content.url, this.documentAttachType == 5 ? "mp3" : "ogg");
                cacheFile = new File(FileLoader.getDirectory(4), fileName);
            }
        } else if (this.mediaWebpage) {
            if (this.inlineResult != null) {
                if (this.inlineResult.document instanceof TL_document) {
                    fileName = FileLoader.getAttachFileName(this.inlineResult.document);
                    cacheFile = FileLoader.getPathToAttach(this.inlineResult.document);
                } else if (this.inlineResult.photo instanceof TL_photo) {
                    this.currentPhotoObject = FileLoader.getClosestPhotoSizeWithSize(this.inlineResult.photo.sizes, AndroidUtilities.getPhotoSize(), true);
                    fileName = FileLoader.getAttachFileName(this.currentPhotoObject);
                    cacheFile = FileLoader.getPathToAttach(this.currentPhotoObject);
                } else if (this.inlineResult.content instanceof TL_webDocument) {
                    fileName = Utilities.MD5(this.inlineResult.content.url) + "." + ImageLoader.getHttpUrlExtension(this.inlineResult.content.url, "jpg");
                    cacheFile = new File(FileLoader.getDirectory(4), fileName);
                } else if (this.inlineResult.thumb instanceof TL_webDocument) {
                    fileName = Utilities.MD5(this.inlineResult.thumb.url) + "." + ImageLoader.getHttpUrlExtension(this.inlineResult.thumb.url, "jpg");
                    cacheFile = new File(FileLoader.getDirectory(4), fileName);
                }
            } else if (this.documentAttach != null) {
                fileName = FileLoader.getAttachFileName(this.documentAttach);
                cacheFile = FileLoader.getPathToAttach(this.documentAttach);
            }
        }
        if (TextUtils.isEmpty(fileName)) {
            this.radialProgress.setBackground(null, false, false);
        } else if (cacheFile.exists()) {
            DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
            if (this.documentAttachType == 5 || this.documentAttachType == 3) {
                boolean playing = MediaController.getInstance().isPlayingMessage(this.currentMessageObject);
                if (!playing || (playing && MediaController.getInstance().isMessagePaused())) {
                    this.buttonState = 0;
                } else {
                    this.buttonState = 1;
                }
            } else {
                this.buttonState = -1;
            }
            this.radialProgress.setBackground(getDrawableForCurrentState(), false, animated);
            invalidate();
        } else {
            DownloadController.getInstance(this.currentAccount).addLoadingFileObserver(fileName, this);
            Float progress;
            if (this.documentAttachType == 5 || this.documentAttachType == 3) {
                boolean isLoading;
                if (this.documentAttach != null) {
                    isLoading = FileLoader.getInstance(this.currentAccount).isLoadingFile(fileName);
                } else {
                    isLoading = ImageLoader.getInstance().isLoadingHttpFile(fileName);
                }
                if (isLoading) {
                    this.buttonState = 4;
                    progress = ImageLoader.getInstance().getFileProgress(fileName);
                    if (progress != null) {
                        this.radialProgress.setProgress(progress.floatValue(), animated);
                    } else {
                        this.radialProgress.setProgress(0.0f, animated);
                    }
                    this.radialProgress.setBackground(getDrawableForCurrentState(), true, animated);
                } else {
                    this.buttonState = 2;
                    this.radialProgress.setProgress(0.0f, animated);
                    this.radialProgress.setBackground(getDrawableForCurrentState(), false, animated);
                }
            } else {
                this.buttonState = 1;
                progress = ImageLoader.getInstance().getFileProgress(fileName);
                if (progress != null) {
                    setProgress = progress.floatValue();
                }
                this.radialProgress.setProgress(setProgress, false);
                this.radialProgress.setBackground(getDrawableForCurrentState(), true, animated);
            }
            invalidate();
        }
    }

    public void setDelegate(ContextLinkCellDelegate contextLinkCellDelegate) {
        this.delegate = contextLinkCellDelegate;
    }

    public BotInlineResult getResult() {
        return this.inlineResult;
    }

    public void onFailedDownload(String fileName) {
        updateButtonState(false);
    }

    public void onSuccessDownload(String fileName) {
        this.radialProgress.setProgress(1.0f, true);
        updateButtonState(true);
    }

    public void onProgressDownload(String fileName, float progress) {
        this.radialProgress.setProgress(progress, true);
        if (this.documentAttachType == 3 || this.documentAttachType == 5) {
            if (this.buttonState != 4) {
                updateButtonState(false);
            }
        } else if (this.buttonState != 1) {
            updateButtonState(false);
        }
    }

    public void onProgressUpload(String fileName, float progress, boolean isEncrypted) {
    }

    public int getObserverTag() {
        return this.TAG;
    }
}
