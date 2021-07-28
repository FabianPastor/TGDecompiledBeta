package org.telegram.ui.Components;

import android.graphics.Bitmap;
import android.text.TextUtils;
import java.util.concurrent.CountDownLatch;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$TL_messages_stickerSet;
import org.telegram.ui.Cells.ChatMessageCell;

public class SlotsDrawable extends RLottieDrawable {
    private ReelValue center;
    private int[] frameCounts = new int[5];
    private int[] frameNums = new int[5];
    private ReelValue left;
    private long[] nativePtrs = new long[5];
    private boolean playWinAnimation;
    private ReelValue right;
    private int[] secondFrameCounts = new int[3];
    private int[] secondFrameNums = new int[3];
    private long[] secondNativePtrs = new long[3];

    enum ReelValue {
        bar,
        berries,
        lemon,
        seven,
        sevenWin
    }

    public SlotsDrawable(String str, int i, int i2) {
        super(str, i, i2);
        this.loadFrameRunnable = new Runnable() {
            public final void run() {
                SlotsDrawable.this.lambda$new$0$SlotsDrawable();
            }
        };
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ void lambda$new$0$SlotsDrawable() {
        int i;
        Runnable runnable;
        if (!this.isRecycled) {
            if (this.nativePtr == 0 || (this.isDice == 2 && this.secondNativePtr == 0)) {
                CountDownLatch countDownLatch = this.frameWaitSync;
                if (countDownLatch != null) {
                    countDownLatch.countDown();
                }
                RLottieDrawable.uiHandler.post(this.uiRunnableNoFrame);
                return;
            }
            if (this.backgroundBitmap == null) {
                try {
                    this.backgroundBitmap = Bitmap.createBitmap(this.width, this.height, Bitmap.Config.ARGB_8888);
                } catch (Throwable th) {
                    FileLog.e(th);
                }
            }
            if (this.backgroundBitmap != null) {
                try {
                    if (this.isDice == 1) {
                        int i2 = 0;
                        i = -1;
                        while (true) {
                            long[] jArr = this.nativePtrs;
                            if (i2 >= jArr.length) {
                                break;
                            }
                            i = RLottieDrawable.getFrame(jArr[i2], this.frameNums[i2], this.backgroundBitmap, this.width, this.height, this.backgroundBitmap.getRowBytes(), i2 == 0);
                            if (i2 != 0) {
                                int[] iArr = this.frameNums;
                                if (iArr[i2] + 1 < this.frameCounts[i2]) {
                                    iArr[i2] = iArr[i2] + 1;
                                } else if (i2 != 4) {
                                    iArr[i2] = 0;
                                    this.nextFrameIsLast = false;
                                    if (this.secondNativePtr != 0) {
                                        this.isDice = 2;
                                    }
                                }
                            }
                            i2++;
                        }
                    } else {
                        if (this.setLastFrame) {
                            int i3 = 0;
                            while (true) {
                                int[] iArr2 = this.secondFrameNums;
                                if (i3 >= iArr2.length) {
                                    break;
                                }
                                iArr2[i3] = this.secondFrameCounts[i3] - 1;
                                i3++;
                            }
                        }
                        if (this.playWinAnimation) {
                            int[] iArr3 = this.frameNums;
                            if (iArr3[0] + 1 < this.frameCounts[0]) {
                                iArr3[0] = iArr3[0] + 1;
                            } else {
                                iArr3[0] = -1;
                            }
                        }
                        RLottieDrawable.getFrame(this.nativePtrs[0], Math.max(this.frameNums[0], 0), this.backgroundBitmap, this.width, this.height, this.backgroundBitmap.getRowBytes(), true);
                        int i4 = 0;
                        while (true) {
                            long[] jArr2 = this.secondNativePtrs;
                            if (i4 >= jArr2.length) {
                                break;
                            }
                            long j = jArr2[i4];
                            int[] iArr4 = this.secondFrameNums;
                            RLottieDrawable.getFrame(j, iArr4[i4] >= 0 ? iArr4[i4] : this.secondFrameCounts[i4] - 1, this.backgroundBitmap, this.width, this.height, this.backgroundBitmap.getRowBytes(), false);
                            if (!this.nextFrameIsLast) {
                                int[] iArr5 = this.secondFrameNums;
                                if (iArr5[i4] + 1 < this.secondFrameCounts[i4]) {
                                    iArr5[i4] = iArr5[i4] + 1;
                                } else {
                                    iArr5[i4] = -1;
                                }
                            }
                            i4++;
                        }
                        i = RLottieDrawable.getFrame(this.nativePtrs[4], this.frameNums[4], this.backgroundBitmap, this.width, this.height, this.backgroundBitmap.getRowBytes(), false);
                        int[] iArr6 = this.frameNums;
                        if (iArr6[4] + 1 < this.frameCounts[4]) {
                            iArr6[4] = iArr6[4] + 1;
                        }
                        int[] iArr7 = this.secondFrameNums;
                        if (iArr7[0] == -1 && iArr7[1] == -1 && iArr7[2] == -1) {
                            this.nextFrameIsLast = true;
                            this.autoRepeatPlayCount++;
                        }
                        ReelValue reelValue = this.left;
                        ReelValue reelValue2 = this.right;
                        if (reelValue != reelValue2 || reelValue2 != this.center) {
                            this.frameNums[0] = -1;
                        } else if (this.secondFrameNums[0] == this.secondFrameCounts[0] - 100) {
                            this.playWinAnimation = true;
                            if (reelValue == ReelValue.sevenWin && (runnable = (Runnable) this.onFinishCallback.get()) != null) {
                                AndroidUtilities.runOnUIThread(runnable);
                            }
                        }
                    }
                    if (i == -1) {
                        RLottieDrawable.uiHandler.post(this.uiRunnableNoFrame);
                        CountDownLatch countDownLatch2 = this.frameWaitSync;
                        if (countDownLatch2 != null) {
                            countDownLatch2.countDown();
                            return;
                        }
                        return;
                    }
                    this.nextRenderingBitmap = this.backgroundBitmap;
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
            }
            RLottieDrawable.uiHandler.post(this.uiRunnable);
            CountDownLatch countDownLatch3 = this.frameWaitSync;
            if (countDownLatch3 != null) {
                countDownLatch3.countDown();
            }
        }
    }

    private ReelValue reelValue(int i) {
        if (i == 0) {
            return ReelValue.bar;
        }
        if (i == 1) {
            return ReelValue.berries;
        }
        if (i != 2) {
            return ReelValue.seven;
        }
        return ReelValue.lemon;
    }

    private void init(int i) {
        int i2 = i - 1;
        ReelValue reelValue = reelValue(i2 & 3);
        ReelValue reelValue2 = reelValue((i2 >> 2) & 3);
        ReelValue reelValue3 = reelValue(i2 >> 4);
        ReelValue reelValue4 = ReelValue.seven;
        if (reelValue == reelValue4 && reelValue2 == reelValue4 && reelValue3 == reelValue4) {
            reelValue = ReelValue.sevenWin;
            reelValue3 = reelValue;
            reelValue2 = reelValue3;
        }
        this.left = reelValue;
        this.center = reelValue2;
        this.right = reelValue3;
    }

    public boolean setBaseDice(ChatMessageCell chatMessageCell, TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet) {
        if (this.nativePtr == 0 && !this.loadingInBackground) {
            this.loadingInBackground = true;
            MessageObject messageObject = chatMessageCell.getMessageObject();
            Utilities.globalQueue.postRunnable(new Runnable(tLRPC$TL_messages_stickerSet, chatMessageCell.getMessageObject().currentAccount, messageObject, chatMessageCell) {
                public final /* synthetic */ TLRPC$TL_messages_stickerSet f$1;
                public final /* synthetic */ int f$2;
                public final /* synthetic */ MessageObject f$3;
                public final /* synthetic */ ChatMessageCell f$4;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r5;
                }

                public final void run() {
                    SlotsDrawable.this.lambda$setBaseDice$5$SlotsDrawable(this.f$1, this.f$2, this.f$3, this.f$4);
                }
            });
        }
        return true;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$setBaseDice$5 */
    public /* synthetic */ void lambda$setBaseDice$5$SlotsDrawable(TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet, int i, MessageObject messageObject, ChatMessageCell chatMessageCell) {
        if (this.destroyAfterLoading) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public final void run() {
                    SlotsDrawable.this.lambda$setBaseDice$1$SlotsDrawable();
                }
            });
            return;
        }
        int i2 = 0;
        boolean z = false;
        while (true) {
            long[] jArr = this.nativePtrs;
            if (i2 >= jArr.length) {
                break;
            }
            if (jArr[i2] == 0) {
                int i3 = 2;
                if (i2 == 0) {
                    i3 = 1;
                } else if (i2 == 1) {
                    i3 = 8;
                } else if (i2 == 2) {
                    i3 = 14;
                } else if (i2 == 3) {
                    i3 = 20;
                }
                TLRPC$Document tLRPC$Document = tLRPC$TL_messages_stickerSet.documents.get(i3);
                String readRes = RLottieDrawable.readRes(FileLoader.getPathToAttach(tLRPC$Document, true), 0);
                if (TextUtils.isEmpty(readRes)) {
                    AndroidUtilities.runOnUIThread(new Runnable(i, messageObject, chatMessageCell, tLRPC$TL_messages_stickerSet) {
                        public final /* synthetic */ int f$1;
                        public final /* synthetic */ MessageObject f$2;
                        public final /* synthetic */ ChatMessageCell f$3;
                        public final /* synthetic */ TLRPC$TL_messages_stickerSet f$4;

                        {
                            this.f$1 = r2;
                            this.f$2 = r3;
                            this.f$3 = r4;
                            this.f$4 = r5;
                        }

                        public final void run() {
                            SlotsDrawable.lambda$setBaseDice$2(TLRPC$Document.this, this.f$1, this.f$2, this.f$3, this.f$4);
                        }
                    });
                    z = true;
                } else {
                    this.nativePtrs[i2] = RLottieDrawable.createWithJson(readRes, "dice", this.metaData, (int[]) null);
                    this.frameCounts[i2] = this.metaData[0];
                }
            }
            i2++;
        }
        if (z) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public final void run() {
                    SlotsDrawable.this.lambda$setBaseDice$3$SlotsDrawable();
                }
            });
        } else {
            AndroidUtilities.runOnUIThread(new Runnable(i, chatMessageCell) {
                public final /* synthetic */ int f$1;
                public final /* synthetic */ ChatMessageCell f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    SlotsDrawable.this.lambda$setBaseDice$4$SlotsDrawable(this.f$1, this.f$2);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$setBaseDice$1 */
    public /* synthetic */ void lambda$setBaseDice$1$SlotsDrawable() {
        this.loadingInBackground = false;
        if (!this.secondLoadingInBackground && this.destroyAfterLoading) {
            recycle();
        }
    }

    static /* synthetic */ void lambda$setBaseDice$2(TLRPC$Document tLRPC$Document, int i, MessageObject messageObject, ChatMessageCell chatMessageCell, TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet) {
        DownloadController.getInstance(i).addLoadingFileObserver(FileLoader.getAttachFileName(tLRPC$Document), messageObject, chatMessageCell);
        FileLoader.getInstance(i).loadFile(tLRPC$Document, tLRPC$TL_messages_stickerSet, 1, 1);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$setBaseDice$3 */
    public /* synthetic */ void lambda$setBaseDice$3$SlotsDrawable() {
        this.loadingInBackground = false;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$setBaseDice$4 */
    public /* synthetic */ void lambda$setBaseDice$4$SlotsDrawable(int i, ChatMessageCell chatMessageCell) {
        this.loadingInBackground = false;
        if (this.secondLoadingInBackground || !this.destroyAfterLoading) {
            this.nativePtr = this.nativePtrs[0];
            DownloadController.getInstance(i).removeLoadingFileObserver(chatMessageCell);
            this.timeBetweenFrames = Math.max(16, (int) (1000.0f / ((float) this.metaData[1])));
            scheduleNextGetFrame();
            invalidateInternal();
            return;
        }
        recycle();
    }

    public boolean setDiceNumber(ChatMessageCell chatMessageCell, int i, TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet, boolean z) {
        if (this.secondNativePtr == 0 && !this.secondLoadingInBackground) {
            init(i);
            MessageObject messageObject = chatMessageCell.getMessageObject();
            int i2 = chatMessageCell.getMessageObject().currentAccount;
            this.secondLoadingInBackground = true;
            Utilities.globalQueue.postRunnable(new Runnable(tLRPC$TL_messages_stickerSet, i2, messageObject, chatMessageCell, z) {
                public final /* synthetic */ TLRPC$TL_messages_stickerSet f$1;
                public final /* synthetic */ int f$2;
                public final /* synthetic */ MessageObject f$3;
                public final /* synthetic */ ChatMessageCell f$4;
                public final /* synthetic */ boolean f$5;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r5;
                    this.f$5 = r6;
                }

                public final void run() {
                    SlotsDrawable.this.lambda$setDiceNumber$10$SlotsDrawable(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
                }
            });
        }
        return true;
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:61:0x00be  */
    /* JADX WARNING: Removed duplicated region for block: B:62:0x00d1  */
    /* renamed from: lambda$setDiceNumber$10 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$setDiceNumber$10$SlotsDrawable(org.telegram.tgnet.TLRPC$TL_messages_stickerSet r18, int r19, org.telegram.messenger.MessageObject r20, org.telegram.ui.Cells.ChatMessageCell r21, boolean r22) {
        /*
            r17 = this;
            r0 = r17
            boolean r1 = r0.destroyAfterLoading
            if (r1 == 0) goto L_0x000f
            org.telegram.ui.Components.-$$Lambda$SlotsDrawable$bqf5Qeg-Zl-v1SK8e_n8by8HfhM r1 = new org.telegram.ui.Components.-$$Lambda$SlotsDrawable$bqf5Qeg-Zl-v1SK8e_n8by8HfhM
            r1.<init>()
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r1)
            return
        L_0x000f:
            r1 = 0
            r2 = 0
            r3 = 0
        L_0x0012:
            long[] r4 = r0.secondNativePtrs
            int r5 = r4.length
            r6 = 2
            int r5 = r5 + r6
            if (r2 >= r5) goto L_0x0107
            r7 = 0
            r5 = 4
            r9 = 3
            r10 = 1
            if (r2 > r6) goto L_0x0093
            r11 = r4[r2]
            int r4 = (r11 > r7 ? 1 : (r11 == r7 ? 0 : -1))
            if (r4 == 0) goto L_0x0028
            goto L_0x009b
        L_0x0028:
            if (r2 != 0) goto L_0x004f
            org.telegram.ui.Components.SlotsDrawable$ReelValue r4 = r0.left
            org.telegram.ui.Components.SlotsDrawable$ReelValue r7 = org.telegram.ui.Components.SlotsDrawable.ReelValue.bar
            if (r4 != r7) goto L_0x0035
            r4 = 5
        L_0x0031:
            r7 = r18
            goto L_0x00a7
        L_0x0035:
            org.telegram.ui.Components.SlotsDrawable$ReelValue r7 = org.telegram.ui.Components.SlotsDrawable.ReelValue.berries
            if (r4 != r7) goto L_0x003b
            r4 = 6
            goto L_0x0031
        L_0x003b:
            org.telegram.ui.Components.SlotsDrawable$ReelValue r7 = org.telegram.ui.Components.SlotsDrawable.ReelValue.lemon
            if (r4 != r7) goto L_0x0041
            r4 = 7
            goto L_0x0031
        L_0x0041:
            org.telegram.ui.Components.SlotsDrawable$ReelValue r7 = org.telegram.ui.Components.SlotsDrawable.ReelValue.seven
            if (r4 != r7) goto L_0x004a
            r7 = r18
            r4 = 4
            goto L_0x00a7
        L_0x004a:
            r7 = r18
            r4 = 3
            goto L_0x00a7
        L_0x004f:
            if (r2 != r10) goto L_0x0072
            org.telegram.ui.Components.SlotsDrawable$ReelValue r4 = r0.center
            org.telegram.ui.Components.SlotsDrawable$ReelValue r7 = org.telegram.ui.Components.SlotsDrawable.ReelValue.bar
            if (r4 != r7) goto L_0x005a
            r4 = 11
            goto L_0x0031
        L_0x005a:
            org.telegram.ui.Components.SlotsDrawable$ReelValue r7 = org.telegram.ui.Components.SlotsDrawable.ReelValue.berries
            if (r4 != r7) goto L_0x0061
            r4 = 12
            goto L_0x0031
        L_0x0061:
            org.telegram.ui.Components.SlotsDrawable$ReelValue r7 = org.telegram.ui.Components.SlotsDrawable.ReelValue.lemon
            if (r4 != r7) goto L_0x0068
            r4 = 13
            goto L_0x0031
        L_0x0068:
            org.telegram.ui.Components.SlotsDrawable$ReelValue r7 = org.telegram.ui.Components.SlotsDrawable.ReelValue.seven
            if (r4 != r7) goto L_0x006f
            r4 = 10
            goto L_0x0031
        L_0x006f:
            r4 = 9
            goto L_0x0031
        L_0x0072:
            org.telegram.ui.Components.SlotsDrawable$ReelValue r4 = r0.right
            org.telegram.ui.Components.SlotsDrawable$ReelValue r7 = org.telegram.ui.Components.SlotsDrawable.ReelValue.bar
            if (r4 != r7) goto L_0x007b
            r4 = 17
            goto L_0x0031
        L_0x007b:
            org.telegram.ui.Components.SlotsDrawable$ReelValue r7 = org.telegram.ui.Components.SlotsDrawable.ReelValue.berries
            if (r4 != r7) goto L_0x0082
            r4 = 18
            goto L_0x0031
        L_0x0082:
            org.telegram.ui.Components.SlotsDrawable$ReelValue r7 = org.telegram.ui.Components.SlotsDrawable.ReelValue.lemon
            if (r4 != r7) goto L_0x0089
            r4 = 19
            goto L_0x0031
        L_0x0089:
            org.telegram.ui.Components.SlotsDrawable$ReelValue r7 = org.telegram.ui.Components.SlotsDrawable.ReelValue.seven
            if (r4 != r7) goto L_0x0090
            r4 = 16
            goto L_0x0031
        L_0x0090:
            r4 = 15
            goto L_0x0031
        L_0x0093:
            long[] r4 = r0.nativePtrs
            r11 = r4[r2]
            int r4 = (r11 > r7 ? 1 : (r11 == r7 ? 0 : -1))
            if (r4 == 0) goto L_0x009e
        L_0x009b:
            r7 = r18
            goto L_0x0103
        L_0x009e:
            if (r2 != r9) goto L_0x00a4
            r7 = r18
            r4 = 1
            goto L_0x00a7
        L_0x00a4:
            r7 = r18
            r4 = 2
        L_0x00a7:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r8 = r7.documents
            java.lang.Object r4 = r8.get(r4)
            r12 = r4
            org.telegram.tgnet.TLRPC$Document r12 = (org.telegram.tgnet.TLRPC$Document) r12
            java.io.File r4 = org.telegram.messenger.FileLoader.getPathToAttach(r12, r10)
            java.lang.String r4 = org.telegram.ui.Components.RLottieDrawable.readRes(r4, r1)
            boolean r8 = android.text.TextUtils.isEmpty(r4)
            if (r8 == 0) goto L_0x00d1
            org.telegram.ui.Components.-$$Lambda$SlotsDrawable$var_sGcRSekH2IrvR4dU-xTTEDd4 r3 = new org.telegram.ui.Components.-$$Lambda$SlotsDrawable$var_sGcRSekH2IrvR4dU-xTTEDd4
            r11 = r3
            r13 = r19
            r14 = r20
            r15 = r21
            r16 = r18
            r11.<init>(r13, r14, r15, r16)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r3)
            r3 = 1
            goto L_0x0103
        L_0x00d1:
            r8 = 0
            java.lang.String r10 = "dice"
            if (r2 > r6) goto L_0x00e9
            long[] r5 = r0.secondNativePtrs
            int[] r6 = r0.metaData
            long r8 = org.telegram.ui.Components.RLottieDrawable.createWithJson(r4, r10, r6, r8)
            r5[r2] = r8
            int[] r4 = r0.secondFrameCounts
            int[] r5 = r0.metaData
            r5 = r5[r1]
            r4[r2] = r5
            goto L_0x0103
        L_0x00e9:
            long[] r6 = r0.nativePtrs
            if (r2 != r9) goto L_0x00ef
            r11 = 0
            goto L_0x00f0
        L_0x00ef:
            r11 = 4
        L_0x00f0:
            int[] r12 = r0.metaData
            long r12 = org.telegram.ui.Components.RLottieDrawable.createWithJson(r4, r10, r12, r8)
            r6[r11] = r12
            int[] r4 = r0.frameCounts
            if (r2 != r9) goto L_0x00fd
            r5 = 0
        L_0x00fd:
            int[] r6 = r0.metaData
            r6 = r6[r1]
            r4[r5] = r6
        L_0x0103:
            int r2 = r2 + 1
            goto L_0x0012
        L_0x0107:
            if (r3 == 0) goto L_0x0112
            org.telegram.ui.Components.-$$Lambda$SlotsDrawable$c4ZUeEoyv32UM2Hf0du5mp9LiQ0 r1 = new org.telegram.ui.Components.-$$Lambda$SlotsDrawable$c4ZUeEoyv32UM2Hf0du5mp9LiQ0
            r1.<init>()
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r1)
            return
        L_0x0112:
            org.telegram.ui.Components.-$$Lambda$SlotsDrawable$ReXgvrSEBXnqzbP-Y3rnTvEii4Y r1 = new org.telegram.ui.Components.-$$Lambda$SlotsDrawable$ReXgvrSEBXnqzbP-Y3rnTvEii4Y
            r2 = r19
            r3 = r21
            r4 = r22
            r1.<init>(r4, r2, r3)
            org.telegram.messenger.AndroidUtilities.runOnUIThread(r1)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.SlotsDrawable.lambda$setDiceNumber$10$SlotsDrawable(org.telegram.tgnet.TLRPC$TL_messages_stickerSet, int, org.telegram.messenger.MessageObject, org.telegram.ui.Cells.ChatMessageCell, boolean):void");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$setDiceNumber$6 */
    public /* synthetic */ void lambda$setDiceNumber$6$SlotsDrawable() {
        this.secondLoadingInBackground = false;
        if (!this.loadingInBackground && this.destroyAfterLoading) {
            recycle();
        }
    }

    static /* synthetic */ void lambda$setDiceNumber$7(TLRPC$Document tLRPC$Document, int i, MessageObject messageObject, ChatMessageCell chatMessageCell, TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet) {
        DownloadController.getInstance(i).addLoadingFileObserver(FileLoader.getAttachFileName(tLRPC$Document), messageObject, chatMessageCell);
        FileLoader.getInstance(i).loadFile(tLRPC$Document, tLRPC$TL_messages_stickerSet, 1, 1);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$setDiceNumber$8 */
    public /* synthetic */ void lambda$setDiceNumber$8$SlotsDrawable() {
        this.secondLoadingInBackground = false;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$setDiceNumber$9 */
    public /* synthetic */ void lambda$setDiceNumber$9$SlotsDrawable(boolean z, int i, ChatMessageCell chatMessageCell) {
        if (z && this.nextRenderingBitmap == null && this.renderingBitmap == null && this.loadFrameTask == null) {
            this.isDice = 2;
            this.setLastFrame = true;
        }
        this.secondLoadingInBackground = false;
        if (this.loadingInBackground || !this.destroyAfterLoading) {
            this.secondNativePtr = this.secondNativePtrs[0];
            DownloadController.getInstance(i).removeLoadingFileObserver(chatMessageCell);
            this.timeBetweenFrames = Math.max(16, (int) (1000.0f / ((float) this.metaData[1])));
            scheduleNextGetFrame();
            invalidateInternal();
            return;
        }
        recycle();
    }

    public void recycle() {
        int i = 0;
        this.isRunning = false;
        this.isRecycled = true;
        checkRunningTasks();
        if (this.loadingInBackground || this.secondLoadingInBackground) {
            this.destroyAfterLoading = true;
        } else if (this.loadFrameTask == null && this.cacheGenerateTask == null) {
            int i2 = 0;
            while (true) {
                long[] jArr = this.nativePtrs;
                if (i2 >= jArr.length) {
                    break;
                }
                if (jArr[i2] != 0) {
                    if (jArr[i2] == this.nativePtr) {
                        this.nativePtr = 0;
                    }
                    RLottieDrawable.destroy(this.nativePtrs[i2]);
                    this.nativePtrs[i2] = 0;
                }
                i2++;
            }
            while (true) {
                long[] jArr2 = this.secondNativePtrs;
                if (i < jArr2.length) {
                    if (jArr2[i] != 0) {
                        if (jArr2[i] == this.secondNativePtr) {
                            this.secondNativePtr = 0;
                        }
                        RLottieDrawable.destroy(this.secondNativePtrs[i]);
                        this.secondNativePtrs[i] = 0;
                    }
                    i++;
                } else {
                    recycleResources();
                    return;
                }
            }
        } else {
            this.destroyWhenDone = true;
        }
    }

    /* access modifiers changed from: protected */
    public void decodeFrameFinishedInternal() {
        if (this.destroyWhenDone) {
            checkRunningTasks();
            if (this.loadFrameTask == null && this.cacheGenerateTask == null) {
                int i = 0;
                int i2 = 0;
                while (true) {
                    long[] jArr = this.nativePtrs;
                    if (i2 >= jArr.length) {
                        break;
                    }
                    if (jArr[i2] != 0) {
                        RLottieDrawable.destroy(jArr[i2]);
                        this.nativePtrs[i2] = 0;
                    }
                    i2++;
                }
                while (true) {
                    long[] jArr2 = this.secondNativePtrs;
                    if (i >= jArr2.length) {
                        break;
                    }
                    if (jArr2[i] != 0) {
                        RLottieDrawable.destroy(jArr2[i]);
                        this.secondNativePtrs[i] = 0;
                    }
                    i++;
                }
            }
        }
        if (this.nativePtr == 0 && this.secondNativePtr == 0) {
            recycleResources();
            return;
        }
        this.waitingForNextTask = true;
        if (!hasParentView()) {
            stop();
        }
        scheduleNextGetFrame();
    }
}
