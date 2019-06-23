package org.telegram.messenger;

import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.Icon;
import android.service.chooser.ChooserTarget;
import android.service.chooser.ChooserTargetService;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import org.telegram.ui.LaunchActivity;

@TargetApi(23)
public class TgChooserTargetService extends ChooserTargetService {
    private RectF bitmapRect;
    private Paint roundPaint;

    public List<ChooserTarget> onGetChooserTargets(ComponentName componentName, IntentFilter intentFilter) {
        int i = UserConfig.selectedAccount;
        ArrayList arrayList = new ArrayList();
        if (!UserConfig.getInstance(i).isClientActivated() || !MessagesController.getGlobalMainSettings().getBoolean("direct_share", true)) {
            return arrayList;
        }
        ImageLoader.getInstance();
        CountDownLatch countDownLatch = new CountDownLatch(1);
        MessagesStorage.getInstance(i).getStorageQueue().postRunnable(new -$$Lambda$TgChooserTargetService$Tk7TkprF_pFKgMncgdcbNzQeTqs(this, i, arrayList, new ComponentName(getPackageName(), LaunchActivity.class.getCanonicalName()), countDownLatch));
        try {
            countDownLatch.await();
        } catch (Exception e) {
            FileLog.e(e);
        }
        return arrayList;
    }

    /* JADX WARNING: Removed duplicated region for block: B:72:0x01a3  */
    /* JADX WARNING: Removed duplicated region for block: B:69:0x0187  */
    /* JADX WARNING: Removed duplicated region for block: B:69:0x0187  */
    /* JADX WARNING: Removed duplicated region for block: B:72:0x01a3  */
    public /* synthetic */ void lambda$onGetChooserTargets$0$TgChooserTargetService(int r17, java.util.List r18, android.content.ComponentName r19, java.util.concurrent.CountDownLatch r20) {
        /*
        r16 = this;
        r1 = r16;
        r2 = new java.util.ArrayList;
        r2.<init>();
        r3 = new java.util.ArrayList;
        r3.<init>();
        r4 = new java.util.ArrayList;
        r4.<init>();
        r5 = 1;
        r6 = 0;
        r0 = new java.util.ArrayList;	 Catch:{ Exception -> 0x00c4 }
        r0.<init>();	 Catch:{ Exception -> 0x00c4 }
        r7 = org.telegram.messenger.UserConfig.getInstance(r17);	 Catch:{ Exception -> 0x00c4 }
        r7 = r7.getClientUserId();	 Catch:{ Exception -> 0x00c4 }
        r7 = java.lang.Integer.valueOf(r7);	 Catch:{ Exception -> 0x00c4 }
        r0.add(r7);	 Catch:{ Exception -> 0x00c4 }
        r7 = new java.util.ArrayList;	 Catch:{ Exception -> 0x00c4 }
        r7.<init>();	 Catch:{ Exception -> 0x00c4 }
        r8 = org.telegram.messenger.MessagesStorage.getInstance(r17);	 Catch:{ Exception -> 0x00c4 }
        r8 = r8.getDatabase();	 Catch:{ Exception -> 0x00c4 }
        r9 = java.util.Locale.US;	 Catch:{ Exception -> 0x00c4 }
        r10 = "SELECT did FROM dialogs ORDER BY date DESC LIMIT %d,%d";
        r11 = 2;
        r11 = new java.lang.Object[r11];	 Catch:{ Exception -> 0x00c4 }
        r12 = java.lang.Integer.valueOf(r6);	 Catch:{ Exception -> 0x00c4 }
        r11[r6] = r12;	 Catch:{ Exception -> 0x00c4 }
        r12 = 30;
        r12 = java.lang.Integer.valueOf(r12);	 Catch:{ Exception -> 0x00c4 }
        r11[r5] = r12;	 Catch:{ Exception -> 0x00c4 }
        r9 = java.lang.String.format(r9, r10, r11);	 Catch:{ Exception -> 0x00c4 }
        r10 = new java.lang.Object[r6];	 Catch:{ Exception -> 0x00c4 }
        r8 = r8.queryFinalized(r9, r10);	 Catch:{ Exception -> 0x00c4 }
    L_0x0053:
        r9 = r8.next();	 Catch:{ Exception -> 0x00c4 }
        if (r9 == 0) goto L_0x009c;
    L_0x0059:
        r9 = r8.longValue(r6);	 Catch:{ Exception -> 0x00c4 }
        r11 = (int) r9;	 Catch:{ Exception -> 0x00c4 }
        r12 = 32;
        r9 = r9 >> r12;
        r10 = (int) r9;	 Catch:{ Exception -> 0x00c4 }
        if (r11 == 0) goto L_0x0053;
    L_0x0064:
        if (r10 != r5) goto L_0x0067;
    L_0x0066:
        goto L_0x0053;
    L_0x0067:
        if (r11 <= 0) goto L_0x007b;
    L_0x0069:
        r9 = java.lang.Integer.valueOf(r11);	 Catch:{ Exception -> 0x00c4 }
        r9 = r0.contains(r9);	 Catch:{ Exception -> 0x00c4 }
        if (r9 != 0) goto L_0x008d;
    L_0x0073:
        r9 = java.lang.Integer.valueOf(r11);	 Catch:{ Exception -> 0x00c4 }
        r0.add(r9);	 Catch:{ Exception -> 0x00c4 }
        goto L_0x008d;
    L_0x007b:
        r9 = -r11;
        r10 = java.lang.Integer.valueOf(r9);	 Catch:{ Exception -> 0x00c4 }
        r10 = r7.contains(r10);	 Catch:{ Exception -> 0x00c4 }
        if (r10 != 0) goto L_0x008d;
    L_0x0086:
        r9 = java.lang.Integer.valueOf(r9);	 Catch:{ Exception -> 0x00c4 }
        r7.add(r9);	 Catch:{ Exception -> 0x00c4 }
    L_0x008d:
        r9 = java.lang.Integer.valueOf(r11);	 Catch:{ Exception -> 0x00c4 }
        r2.add(r9);	 Catch:{ Exception -> 0x00c4 }
        r9 = r2.size();	 Catch:{ Exception -> 0x00c4 }
        r10 = 8;
        if (r9 != r10) goto L_0x0053;
    L_0x009c:
        r8.dispose();	 Catch:{ Exception -> 0x00c4 }
        r8 = r7.isEmpty();	 Catch:{ Exception -> 0x00c4 }
        r9 = ",";
        if (r8 != 0) goto L_0x00b2;
    L_0x00a7:
        r8 = org.telegram.messenger.MessagesStorage.getInstance(r17);	 Catch:{ Exception -> 0x00c4 }
        r7 = android.text.TextUtils.join(r9, r7);	 Catch:{ Exception -> 0x00c4 }
        r8.getChatsInternal(r7, r3);	 Catch:{ Exception -> 0x00c4 }
    L_0x00b2:
        r7 = r0.isEmpty();	 Catch:{ Exception -> 0x00c4 }
        if (r7 != 0) goto L_0x00c8;
    L_0x00b8:
        r7 = org.telegram.messenger.MessagesStorage.getInstance(r17);	 Catch:{ Exception -> 0x00c4 }
        r0 = android.text.TextUtils.join(r9, r0);	 Catch:{ Exception -> 0x00c4 }
        r7.getUsersInternal(r0, r4);	 Catch:{ Exception -> 0x00c4 }
        goto L_0x00c8;
    L_0x00c4:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x00c8:
        r0 = org.telegram.messenger.Utilities.random;
        r7 = r0.nextLong();
        org.telegram.messenger.SharedConfig.directShareHash = r7;
        r0 = org.telegram.messenger.ApplicationLoader.applicationContext;
        r7 = "mainconfig";
        r0 = r0.getSharedPreferences(r7, r6);
        r0 = r0.edit();
        r7 = org.telegram.messenger.SharedConfig.directShareHash;
        r9 = "directShareHash";
        r0 = r0.putLong(r9, r7);
        r0.commit();
        r0 = 0;
    L_0x00e8:
        r7 = r2.size();
        if (r0 >= r7) goto L_0x01a9;
    L_0x00ee:
        r13 = new android.os.Bundle;
        r13.<init>();
        r7 = r2.get(r0);
        r7 = (java.lang.Integer) r7;
        r7 = r7.intValue();
        r8 = "hash";
        r9 = "dialogId";
        r10 = 0;
        if (r7 <= 0) goto L_0x0142;
    L_0x0104:
        r11 = 0;
    L_0x0105:
        r12 = r4.size();
        if (r11 >= r12) goto L_0x013f;
    L_0x010b:
        r12 = r4.get(r11);
        r12 = (org.telegram.tgnet.TLRPC.User) r12;
        r14 = r12.id;
        if (r14 != r7) goto L_0x013c;
    L_0x0115:
        r11 = r12.bot;
        if (r11 != 0) goto L_0x013f;
    L_0x0119:
        r14 = (long) r7;
        r13.putLong(r9, r14);
        r14 = org.telegram.messenger.SharedConfig.directShareHash;
        r13.putLong(r8, r14);
        r7 = r12.photo;
        if (r7 == 0) goto L_0x0133;
    L_0x0126:
        r7 = r7.photo_small;
        if (r7 == 0) goto L_0x0133;
    L_0x012a:
        r7 = org.telegram.messenger.FileLoader.getPathToAttach(r7, r5);
        r7 = r1.createRoundBitmap(r7);
        r10 = r7;
    L_0x0133:
        r7 = r12.first_name;
        r8 = r12.last_name;
        r7 = org.telegram.messenger.ContactsController.formatName(r7, r8);
        goto L_0x0140;
    L_0x013c:
        r11 = r11 + 1;
        goto L_0x0105;
    L_0x013f:
        r7 = r10;
    L_0x0140:
        r9 = r7;
        goto L_0x0185;
    L_0x0142:
        r11 = 0;
    L_0x0143:
        r12 = r3.size();
        if (r11 >= r12) goto L_0x0184;
    L_0x0149:
        r12 = r3.get(r11);
        r12 = (org.telegram.tgnet.TLRPC.Chat) r12;
        r14 = r12.id;
        r15 = -r7;
        if (r14 != r15) goto L_0x0181;
    L_0x0154:
        r11 = org.telegram.messenger.ChatObject.isNotInChat(r12);
        if (r11 != 0) goto L_0x0184;
    L_0x015a:
        r11 = org.telegram.messenger.ChatObject.isChannel(r12);
        if (r11 == 0) goto L_0x0164;
    L_0x0160:
        r11 = r12.megagroup;
        if (r11 == 0) goto L_0x0184;
    L_0x0164:
        r14 = (long) r7;
        r13.putLong(r9, r14);
        r14 = org.telegram.messenger.SharedConfig.directShareHash;
        r13.putLong(r8, r14);
        r7 = r12.photo;
        if (r7 == 0) goto L_0x017e;
    L_0x0171:
        r7 = r7.photo_small;
        if (r7 == 0) goto L_0x017e;
    L_0x0175:
        r7 = org.telegram.messenger.FileLoader.getPathToAttach(r7, r5);
        r7 = r1.createRoundBitmap(r7);
        r10 = r7;
    L_0x017e:
        r7 = r12.title;
        goto L_0x0140;
    L_0x0181:
        r11 = r11 + 1;
        goto L_0x0143;
    L_0x0184:
        r9 = r10;
    L_0x0185:
        if (r9 == 0) goto L_0x01a3;
    L_0x0187:
        if (r10 != 0) goto L_0x0193;
    L_0x0189:
        r7 = org.telegram.messenger.ApplicationLoader.applicationContext;
        r8 = NUM; // 0x7var_ float:1.7945357E38 double:1.0529356927E-314;
        r7 = android.graphics.drawable.Icon.createWithResource(r7, r8);
        r10 = r7;
    L_0x0193:
        r7 = new android.service.chooser.ChooserTarget;
        r11 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r8 = r7;
        r12 = r19;
        r8.<init>(r9, r10, r11, r12, r13);
        r8 = r18;
        r8.add(r7);
        goto L_0x01a5;
    L_0x01a3:
        r8 = r18;
    L_0x01a5:
        r0 = r0 + 1;
        goto L_0x00e8;
    L_0x01a9:
        r20.countDown();
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.TgChooserTargetService.lambda$onGetChooserTargets$0$TgChooserTargetService(int, java.util.List, android.content.ComponentName, java.util.concurrent.CountDownLatch):void");
    }

    private Icon createRoundBitmap(File file) {
        try {
            Bitmap decodeFile = BitmapFactory.decodeFile(file.toString());
            if (decodeFile != null) {
                Bitmap createBitmap = Bitmap.createBitmap(decodeFile.getWidth(), decodeFile.getHeight(), Config.ARGB_8888);
                createBitmap.eraseColor(0);
                Canvas canvas = new Canvas(createBitmap);
                BitmapShader bitmapShader = new BitmapShader(decodeFile, TileMode.CLAMP, TileMode.CLAMP);
                if (this.roundPaint == null) {
                    this.roundPaint = new Paint(1);
                    this.bitmapRect = new RectF();
                }
                this.roundPaint.setShader(bitmapShader);
                this.bitmapRect.set(0.0f, 0.0f, (float) decodeFile.getWidth(), (float) decodeFile.getHeight());
                canvas.drawRoundRect(this.bitmapRect, (float) decodeFile.getWidth(), (float) decodeFile.getHeight(), this.roundPaint);
                return Icon.createWithBitmap(createBitmap);
            }
        } catch (Throwable th) {
            FileLog.e(th);
        }
        return null;
    }
}
