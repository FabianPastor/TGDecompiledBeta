package org.telegram.ui.Adapters;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.LongSparseArray;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$InputStickerSet;
import org.telegram.tgnet.TLRPC$StickerSet;
import org.telegram.tgnet.TLRPC$StickerSetCovered;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_messages_foundStickerSets;
import org.telegram.tgnet.TLRPC$TL_messages_getStickers;
import org.telegram.tgnet.TLRPC$TL_messages_searchStickerSets;
import org.telegram.tgnet.TLRPC$TL_messages_stickerSet;
import org.telegram.tgnet.TLRPC$TL_messages_stickers;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.EmptyCell;
import org.telegram.ui.Cells.FeaturedStickerSetInfoCell;
import org.telegram.ui.Cells.StickerEmojiCell;
import org.telegram.ui.Cells.StickerSetNameCell;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;

public class StickersSearchAdapter extends RecyclerListView.SelectionAdapter {
    private SparseArray<Object> cache = new SparseArray<>();
    private SparseArray<Object> cacheParent = new SparseArray<>();
    boolean cleared;
    private final Context context;
    /* access modifiers changed from: private */
    public final int currentAccount = UserConfig.selectedAccount;
    /* access modifiers changed from: private */
    public final Delegate delegate;
    /* access modifiers changed from: private */
    public ArrayList<ArrayList<TLRPC$Document>> emojiArrays = new ArrayList<>();
    /* access modifiers changed from: private */
    public int emojiSearchId;
    /* access modifiers changed from: private */
    public HashMap<ArrayList<TLRPC$Document>, String> emojiStickers = new HashMap<>();
    private ImageView emptyImageView;
    private TextView emptyTextView;
    private final LongSparseArray<TLRPC$StickerSetCovered> installingStickerSets;
    /* access modifiers changed from: private */
    public ArrayList<TLRPC$TL_messages_stickerSet> localPacks = new ArrayList<>();
    /* access modifiers changed from: private */
    public HashMap<TLRPC$TL_messages_stickerSet, Integer> localPacksByName = new HashMap<>();
    /* access modifiers changed from: private */
    public HashMap<TLRPC$TL_messages_stickerSet, Boolean> localPacksByShortName = new HashMap<>();
    private SparseArray<String> positionToEmoji = new SparseArray<>();
    private SparseIntArray positionToRow = new SparseIntArray();
    private SparseArray<TLRPC$StickerSetCovered> positionsToSets = new SparseArray<>();
    private final TLRPC$StickerSetCovered[] primaryInstallingStickerSets;
    private final LongSparseArray<TLRPC$StickerSetCovered> removingStickerSets;
    /* access modifiers changed from: private */
    public int reqId;
    /* access modifiers changed from: private */
    public int reqId2;
    private SparseArray<Object> rowStartPack = new SparseArray<>();
    /* access modifiers changed from: private */
    public String searchQuery;
    private Runnable searchRunnable = new Runnable() {
        private void clear() {
            StickersSearchAdapter stickersSearchAdapter = StickersSearchAdapter.this;
            if (!stickersSearchAdapter.cleared) {
                stickersSearchAdapter.cleared = true;
                stickersSearchAdapter.emojiStickers.clear();
                StickersSearchAdapter.this.emojiArrays.clear();
                StickersSearchAdapter.this.localPacks.clear();
                StickersSearchAdapter.this.serverPacks.clear();
                StickersSearchAdapter.this.localPacksByShortName.clear();
                StickersSearchAdapter.this.localPacksByName.clear();
            }
        }

        /* JADX WARNING: Code restructure failed: missing block: B:14:0x0073, code lost:
            if (r5.charAt(r9) <= 57343) goto L_0x008f;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:20:0x008d, code lost:
            if (r5.charAt(r9) != 9794) goto L_0x00aa;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void run() {
            /*
                r13 = this;
                org.telegram.ui.Adapters.StickersSearchAdapter r0 = org.telegram.ui.Adapters.StickersSearchAdapter.this
                java.lang.String r0 = r0.searchQuery
                boolean r0 = android.text.TextUtils.isEmpty(r0)
                if (r0 == 0) goto L_0x000d
                return
            L_0x000d:
                org.telegram.ui.Adapters.StickersSearchAdapter r0 = org.telegram.ui.Adapters.StickersSearchAdapter.this
                org.telegram.ui.Adapters.StickersSearchAdapter$Delegate r0 = r0.delegate
                r0.onSearchStart()
                org.telegram.ui.Adapters.StickersSearchAdapter r0 = org.telegram.ui.Adapters.StickersSearchAdapter.this
                r1 = 0
                r0.cleared = r1
                int r0 = org.telegram.ui.Adapters.StickersSearchAdapter.access$804(r0)
                java.util.ArrayList r2 = new java.util.ArrayList
                r2.<init>(r1)
                android.util.LongSparseArray r3 = new android.util.LongSparseArray
                r3.<init>(r1)
                org.telegram.ui.Adapters.StickersSearchAdapter r4 = org.telegram.ui.Adapters.StickersSearchAdapter.this
                int r4 = r4.currentAccount
                org.telegram.messenger.MediaDataController r4 = org.telegram.messenger.MediaDataController.getInstance(r4)
                java.util.HashMap r4 = r4.getAllStickers()
                org.telegram.ui.Adapters.StickersSearchAdapter r5 = org.telegram.ui.Adapters.StickersSearchAdapter.this
                java.lang.String r5 = r5.searchQuery
                int r5 = r5.length()
                r6 = 14
                r7 = 1
                if (r5 > r6) goto L_0x011a
                org.telegram.ui.Adapters.StickersSearchAdapter r5 = org.telegram.ui.Adapters.StickersSearchAdapter.this
                java.lang.String r5 = r5.searchQuery
                int r6 = r5.length()
                r8 = 0
            L_0x0051:
                if (r8 >= r6) goto L_0x00d1
                int r9 = r6 + -1
                r10 = 2
                if (r8 >= r9) goto L_0x00aa
                char r9 = r5.charAt(r8)
                r11 = 55356(0xd83c, float:7.757E-41)
                if (r9 != r11) goto L_0x0075
                int r9 = r8 + 1
                char r11 = r5.charAt(r9)
                r12 = 57339(0xdffb, float:8.0349E-41)
                if (r11 < r12) goto L_0x0075
                char r9 = r5.charAt(r9)
                r11 = 57343(0xdfff, float:8.0355E-41)
                if (r9 <= r11) goto L_0x008f
            L_0x0075:
                char r9 = r5.charAt(r8)
                r11 = 8205(0x200d, float:1.1498E-41)
                if (r9 != r11) goto L_0x00aa
                int r9 = r8 + 1
                char r11 = r5.charAt(r9)
                r12 = 9792(0x2640, float:1.3722E-41)
                if (r11 == r12) goto L_0x008f
                char r9 = r5.charAt(r9)
                r11 = 9794(0x2642, float:1.3724E-41)
                if (r9 != r11) goto L_0x00aa
            L_0x008f:
                java.lang.CharSequence[] r9 = new java.lang.CharSequence[r10]
                java.lang.CharSequence r10 = r5.subSequence(r1, r8)
                r9[r1] = r10
                int r10 = r8 + 2
                int r11 = r5.length()
                java.lang.CharSequence r5 = r5.subSequence(r10, r11)
                r9[r7] = r5
                java.lang.CharSequence r5 = android.text.TextUtils.concat(r9)
                int r6 = r6 + -2
                goto L_0x00cd
            L_0x00aa:
                char r9 = r5.charAt(r8)
                r11 = 65039(0xfe0f, float:9.1139E-41)
                if (r9 != r11) goto L_0x00cf
                java.lang.CharSequence[] r9 = new java.lang.CharSequence[r10]
                java.lang.CharSequence r10 = r5.subSequence(r1, r8)
                r9[r1] = r10
                int r10 = r8 + 1
                int r11 = r5.length()
                java.lang.CharSequence r5 = r5.subSequence(r10, r11)
                r9[r7] = r5
                java.lang.CharSequence r5 = android.text.TextUtils.concat(r9)
                int r6 = r6 + -1
            L_0x00cd:
                int r8 = r8 + -1
            L_0x00cf:
                int r8 = r8 + r7
                goto L_0x0051
            L_0x00d1:
                if (r4 == 0) goto L_0x00de
                java.lang.String r5 = r5.toString()
                java.lang.Object r5 = r4.get(r5)
                java.util.ArrayList r5 = (java.util.ArrayList) r5
                goto L_0x00df
            L_0x00de:
                r5 = 0
            L_0x00df:
                if (r5 == 0) goto L_0x011a
                boolean r6 = r5.isEmpty()
                if (r6 != 0) goto L_0x011a
                r13.clear()
                r2.addAll(r5)
                int r6 = r5.size()
                r8 = 0
            L_0x00f2:
                if (r8 >= r6) goto L_0x0102
                java.lang.Object r9 = r5.get(r8)
                org.telegram.tgnet.TLRPC$Document r9 = (org.telegram.tgnet.TLRPC$Document) r9
                long r10 = r9.id
                r3.put(r10, r9)
                int r8 = r8 + 1
                goto L_0x00f2
            L_0x0102:
                org.telegram.ui.Adapters.StickersSearchAdapter r5 = org.telegram.ui.Adapters.StickersSearchAdapter.this
                java.util.HashMap r5 = r5.emojiStickers
                org.telegram.ui.Adapters.StickersSearchAdapter r6 = org.telegram.ui.Adapters.StickersSearchAdapter.this
                java.lang.String r6 = r6.searchQuery
                r5.put(r2, r6)
                org.telegram.ui.Adapters.StickersSearchAdapter r5 = org.telegram.ui.Adapters.StickersSearchAdapter.this
                java.util.ArrayList r5 = r5.emojiArrays
                r5.add(r2)
            L_0x011a:
                if (r4 == 0) goto L_0x017a
                boolean r5 = r4.isEmpty()
                if (r5 != 0) goto L_0x017a
                org.telegram.ui.Adapters.StickersSearchAdapter r5 = org.telegram.ui.Adapters.StickersSearchAdapter.this
                java.lang.String r5 = r5.searchQuery
                int r5 = r5.length()
                if (r5 <= r7) goto L_0x017a
                java.lang.String[] r5 = org.telegram.messenger.AndroidUtilities.getCurrentKeyboardLanguage()
                org.telegram.ui.Adapters.StickersSearchAdapter r6 = org.telegram.ui.Adapters.StickersSearchAdapter.this
                org.telegram.ui.Adapters.StickersSearchAdapter$Delegate r6 = r6.delegate
                java.lang.String[] r6 = r6.getLastSearchKeyboardLanguage()
                boolean r6 = java.util.Arrays.equals(r6, r5)
                if (r6 != 0) goto L_0x014f
                org.telegram.ui.Adapters.StickersSearchAdapter r6 = org.telegram.ui.Adapters.StickersSearchAdapter.this
                int r6 = r6.currentAccount
                org.telegram.messenger.MediaDataController r6 = org.telegram.messenger.MediaDataController.getInstance(r6)
                r6.fetchNewEmojiKeywords(r5)
            L_0x014f:
                org.telegram.ui.Adapters.StickersSearchAdapter r6 = org.telegram.ui.Adapters.StickersSearchAdapter.this
                org.telegram.ui.Adapters.StickersSearchAdapter$Delegate r6 = r6.delegate
                r6.setLastSearchKeyboardLanguage(r5)
                org.telegram.ui.Adapters.StickersSearchAdapter r5 = org.telegram.ui.Adapters.StickersSearchAdapter.this
                int r5 = r5.currentAccount
                org.telegram.messenger.MediaDataController r5 = org.telegram.messenger.MediaDataController.getInstance(r5)
                org.telegram.ui.Adapters.StickersSearchAdapter r6 = org.telegram.ui.Adapters.StickersSearchAdapter.this
                org.telegram.ui.Adapters.StickersSearchAdapter$Delegate r6 = r6.delegate
                java.lang.String[] r6 = r6.getLastSearchKeyboardLanguage()
                org.telegram.ui.Adapters.StickersSearchAdapter r8 = org.telegram.ui.Adapters.StickersSearchAdapter.this
                java.lang.String r8 = r8.searchQuery
                org.telegram.ui.Adapters.StickersSearchAdapter$1$$ExternalSyntheticLambda2 r9 = new org.telegram.ui.Adapters.StickersSearchAdapter$1$$ExternalSyntheticLambda2
                r9.<init>(r13, r0, r4)
                r5.getEmojiSuggestions(r6, r8, r1, r9)
            L_0x017a:
                org.telegram.ui.Adapters.StickersSearchAdapter r0 = org.telegram.ui.Adapters.StickersSearchAdapter.this
                int r0 = r0.currentAccount
                org.telegram.messenger.MediaDataController r0 = org.telegram.messenger.MediaDataController.getInstance(r0)
                java.util.ArrayList r0 = r0.getStickerSets(r1)
                int r4 = r0.size()
                r5 = 0
            L_0x018d:
                r6 = 32
                if (r5 >= r4) goto L_0x0209
                java.lang.Object r8 = r0.get(r5)
                org.telegram.tgnet.TLRPC$TL_messages_stickerSet r8 = (org.telegram.tgnet.TLRPC$TL_messages_stickerSet) r8
                org.telegram.tgnet.TLRPC$StickerSet r9 = r8.set
                java.lang.String r9 = r9.title
                org.telegram.ui.Adapters.StickersSearchAdapter r10 = org.telegram.ui.Adapters.StickersSearchAdapter.this
                java.lang.String r10 = r10.searchQuery
                int r9 = org.telegram.messenger.AndroidUtilities.indexOfIgnoreCase(r9, r10)
                if (r9 < 0) goto L_0x01cf
                if (r9 == 0) goto L_0x01b5
                org.telegram.tgnet.TLRPC$StickerSet r10 = r8.set
                java.lang.String r10 = r10.title
                int r11 = r9 + -1
                char r10 = r10.charAt(r11)
                if (r10 != r6) goto L_0x0206
            L_0x01b5:
                r13.clear()
                org.telegram.ui.Adapters.StickersSearchAdapter r6 = org.telegram.ui.Adapters.StickersSearchAdapter.this
                java.util.ArrayList r6 = r6.localPacks
                r6.add(r8)
                org.telegram.ui.Adapters.StickersSearchAdapter r6 = org.telegram.ui.Adapters.StickersSearchAdapter.this
                java.util.HashMap r6 = r6.localPacksByName
                java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
                r6.put(r8, r9)
                goto L_0x0206
            L_0x01cf:
                org.telegram.tgnet.TLRPC$StickerSet r9 = r8.set
                java.lang.String r9 = r9.short_name
                if (r9 == 0) goto L_0x0206
                org.telegram.ui.Adapters.StickersSearchAdapter r10 = org.telegram.ui.Adapters.StickersSearchAdapter.this
                java.lang.String r10 = r10.searchQuery
                int r9 = org.telegram.messenger.AndroidUtilities.indexOfIgnoreCase(r9, r10)
                if (r9 < 0) goto L_0x0206
                if (r9 == 0) goto L_0x01ef
                org.telegram.tgnet.TLRPC$StickerSet r10 = r8.set
                java.lang.String r10 = r10.short_name
                int r9 = r9 + -1
                char r9 = r10.charAt(r9)
                if (r9 != r6) goto L_0x0206
            L_0x01ef:
                r13.clear()
                org.telegram.ui.Adapters.StickersSearchAdapter r6 = org.telegram.ui.Adapters.StickersSearchAdapter.this
                java.util.ArrayList r6 = r6.localPacks
                r6.add(r8)
                org.telegram.ui.Adapters.StickersSearchAdapter r6 = org.telegram.ui.Adapters.StickersSearchAdapter.this
                java.util.HashMap r6 = r6.localPacksByShortName
                java.lang.Boolean r9 = java.lang.Boolean.TRUE
                r6.put(r8, r9)
            L_0x0206:
                int r5 = r5 + 1
                goto L_0x018d
            L_0x0209:
                org.telegram.ui.Adapters.StickersSearchAdapter r0 = org.telegram.ui.Adapters.StickersSearchAdapter.this
                int r0 = r0.currentAccount
                org.telegram.messenger.MediaDataController r0 = org.telegram.messenger.MediaDataController.getInstance(r0)
                r4 = 3
                java.util.ArrayList r0 = r0.getStickerSets(r4)
                int r4 = r0.size()
                r5 = 0
            L_0x021d:
                if (r5 >= r4) goto L_0x0297
                java.lang.Object r8 = r0.get(r5)
                org.telegram.tgnet.TLRPC$TL_messages_stickerSet r8 = (org.telegram.tgnet.TLRPC$TL_messages_stickerSet) r8
                org.telegram.tgnet.TLRPC$StickerSet r9 = r8.set
                java.lang.String r9 = r9.title
                org.telegram.ui.Adapters.StickersSearchAdapter r10 = org.telegram.ui.Adapters.StickersSearchAdapter.this
                java.lang.String r10 = r10.searchQuery
                int r9 = org.telegram.messenger.AndroidUtilities.indexOfIgnoreCase(r9, r10)
                if (r9 < 0) goto L_0x025d
                if (r9 == 0) goto L_0x0243
                org.telegram.tgnet.TLRPC$StickerSet r10 = r8.set
                java.lang.String r10 = r10.title
                int r11 = r9 + -1
                char r10 = r10.charAt(r11)
                if (r10 != r6) goto L_0x0294
            L_0x0243:
                r13.clear()
                org.telegram.ui.Adapters.StickersSearchAdapter r10 = org.telegram.ui.Adapters.StickersSearchAdapter.this
                java.util.ArrayList r10 = r10.localPacks
                r10.add(r8)
                org.telegram.ui.Adapters.StickersSearchAdapter r10 = org.telegram.ui.Adapters.StickersSearchAdapter.this
                java.util.HashMap r10 = r10.localPacksByName
                java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
                r10.put(r8, r9)
                goto L_0x0294
            L_0x025d:
                org.telegram.tgnet.TLRPC$StickerSet r9 = r8.set
                java.lang.String r9 = r9.short_name
                if (r9 == 0) goto L_0x0294
                org.telegram.ui.Adapters.StickersSearchAdapter r10 = org.telegram.ui.Adapters.StickersSearchAdapter.this
                java.lang.String r10 = r10.searchQuery
                int r9 = org.telegram.messenger.AndroidUtilities.indexOfIgnoreCase(r9, r10)
                if (r9 < 0) goto L_0x0294
                if (r9 == 0) goto L_0x027d
                org.telegram.tgnet.TLRPC$StickerSet r10 = r8.set
                java.lang.String r10 = r10.short_name
                int r9 = r9 + -1
                char r9 = r10.charAt(r9)
                if (r9 != r6) goto L_0x0294
            L_0x027d:
                r13.clear()
                org.telegram.ui.Adapters.StickersSearchAdapter r9 = org.telegram.ui.Adapters.StickersSearchAdapter.this
                java.util.ArrayList r9 = r9.localPacks
                r9.add(r8)
                org.telegram.ui.Adapters.StickersSearchAdapter r9 = org.telegram.ui.Adapters.StickersSearchAdapter.this
                java.util.HashMap r9 = r9.localPacksByShortName
                java.lang.Boolean r10 = java.lang.Boolean.TRUE
                r9.put(r8, r10)
            L_0x0294:
                int r5 = r5 + 1
                goto L_0x021d
            L_0x0297:
                org.telegram.ui.Adapters.StickersSearchAdapter r0 = org.telegram.ui.Adapters.StickersSearchAdapter.this
                java.util.ArrayList r0 = r0.localPacks
                boolean r0 = r0.isEmpty()
                if (r0 == 0) goto L_0x02af
                org.telegram.ui.Adapters.StickersSearchAdapter r0 = org.telegram.ui.Adapters.StickersSearchAdapter.this
                java.util.HashMap r0 = r0.emojiStickers
                boolean r0 = r0.isEmpty()
                if (r0 != 0) goto L_0x02b8
            L_0x02af:
                org.telegram.ui.Adapters.StickersSearchAdapter r0 = org.telegram.ui.Adapters.StickersSearchAdapter.this
                org.telegram.ui.Adapters.StickersSearchAdapter$Delegate r0 = r0.delegate
                r0.setAdapterVisible(r7)
            L_0x02b8:
                org.telegram.tgnet.TLRPC$TL_messages_searchStickerSets r0 = new org.telegram.tgnet.TLRPC$TL_messages_searchStickerSets
                r0.<init>()
                org.telegram.ui.Adapters.StickersSearchAdapter r4 = org.telegram.ui.Adapters.StickersSearchAdapter.this
                java.lang.String r4 = r4.searchQuery
                r0.q = r4
                org.telegram.ui.Adapters.StickersSearchAdapter r4 = org.telegram.ui.Adapters.StickersSearchAdapter.this
                int r5 = r4.currentAccount
                org.telegram.tgnet.ConnectionsManager r5 = org.telegram.tgnet.ConnectionsManager.getInstance(r5)
                org.telegram.ui.Adapters.StickersSearchAdapter$1$$ExternalSyntheticLambda4 r6 = new org.telegram.ui.Adapters.StickersSearchAdapter$1$$ExternalSyntheticLambda4
                r6.<init>(r13, r0)
                int r0 = r5.sendRequest(r0, r6)
                int unused = r4.reqId = r0
                org.telegram.ui.Adapters.StickersSearchAdapter r0 = org.telegram.ui.Adapters.StickersSearchAdapter.this
                java.lang.String r0 = r0.searchQuery
                boolean r0 = org.telegram.messenger.Emoji.isValidEmoji(r0)
                if (r0 == 0) goto L_0x030c
                org.telegram.tgnet.TLRPC$TL_messages_getStickers r0 = new org.telegram.tgnet.TLRPC$TL_messages_getStickers
                r0.<init>()
                org.telegram.ui.Adapters.StickersSearchAdapter r4 = org.telegram.ui.Adapters.StickersSearchAdapter.this
                java.lang.String r4 = r4.searchQuery
                r0.emoticon = r4
                r0.hash = r1
                org.telegram.ui.Adapters.StickersSearchAdapter r1 = org.telegram.ui.Adapters.StickersSearchAdapter.this
                int r4 = r1.currentAccount
                org.telegram.tgnet.ConnectionsManager r4 = org.telegram.tgnet.ConnectionsManager.getInstance(r4)
                org.telegram.ui.Adapters.StickersSearchAdapter$1$$ExternalSyntheticLambda3 r5 = new org.telegram.ui.Adapters.StickersSearchAdapter$1$$ExternalSyntheticLambda3
                r5.<init>(r13, r0, r2, r3)
                int r0 = r4.sendRequest(r0, r5)
                int unused = r1.reqId2 = r0
            L_0x030c:
                org.telegram.ui.Adapters.StickersSearchAdapter r0 = org.telegram.ui.Adapters.StickersSearchAdapter.this
                r0.notifyDataSetChanged()
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Adapters.StickersSearchAdapter.AnonymousClass1.run():void");
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$run$0(int i, HashMap hashMap, ArrayList arrayList, String str) {
            if (i == StickersSearchAdapter.this.emojiSearchId) {
                int size = arrayList.size();
                boolean z = false;
                for (int i2 = 0; i2 < size; i2++) {
                    String str2 = ((MediaDataController.KeywordResult) arrayList.get(i2)).emoji;
                    ArrayList arrayList2 = hashMap != null ? (ArrayList) hashMap.get(str2) : null;
                    if (arrayList2 != null && !arrayList2.isEmpty()) {
                        clear();
                        if (!StickersSearchAdapter.this.emojiStickers.containsKey(arrayList2)) {
                            StickersSearchAdapter.this.emojiStickers.put(arrayList2, str2);
                            StickersSearchAdapter.this.emojiArrays.add(arrayList2);
                            z = true;
                        }
                    }
                }
                if (z) {
                    StickersSearchAdapter.this.notifyDataSetChanged();
                }
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$run$2(TLRPC$TL_messages_searchStickerSets tLRPC$TL_messages_searchStickerSets, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
            if (tLObject instanceof TLRPC$TL_messages_foundStickerSets) {
                AndroidUtilities.runOnUIThread(new StickersSearchAdapter$1$$ExternalSyntheticLambda1(this, tLRPC$TL_messages_searchStickerSets, tLObject));
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$run$1(TLRPC$TL_messages_searchStickerSets tLRPC$TL_messages_searchStickerSets, TLObject tLObject) {
            if (tLRPC$TL_messages_searchStickerSets.q.equals(StickersSearchAdapter.this.searchQuery)) {
                clear();
                StickersSearchAdapter.this.delegate.onSearchStop();
                int unused = StickersSearchAdapter.this.reqId = 0;
                StickersSearchAdapter.this.delegate.setAdapterVisible(true);
                StickersSearchAdapter.this.serverPacks.addAll(((TLRPC$TL_messages_foundStickerSets) tLObject).sets);
                StickersSearchAdapter.this.notifyDataSetChanged();
            }
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$run$4(TLRPC$TL_messages_getStickers tLRPC$TL_messages_getStickers, ArrayList arrayList, LongSparseArray longSparseArray, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
            AndroidUtilities.runOnUIThread(new StickersSearchAdapter$1$$ExternalSyntheticLambda0(this, tLRPC$TL_messages_getStickers, tLObject, arrayList, longSparseArray));
        }

        /* access modifiers changed from: private */
        public /* synthetic */ void lambda$run$3(TLRPC$TL_messages_getStickers tLRPC$TL_messages_getStickers, TLObject tLObject, ArrayList arrayList, LongSparseArray longSparseArray) {
            if (tLRPC$TL_messages_getStickers.emoticon.equals(StickersSearchAdapter.this.searchQuery)) {
                int unused = StickersSearchAdapter.this.reqId2 = 0;
                if (tLObject instanceof TLRPC$TL_messages_stickers) {
                    TLRPC$TL_messages_stickers tLRPC$TL_messages_stickers = (TLRPC$TL_messages_stickers) tLObject;
                    int size = arrayList.size();
                    int size2 = tLRPC$TL_messages_stickers.stickers.size();
                    for (int i = 0; i < size2; i++) {
                        TLRPC$Document tLRPC$Document = tLRPC$TL_messages_stickers.stickers.get(i);
                        if (longSparseArray.indexOfKey(tLRPC$Document.id) < 0) {
                            arrayList.add(tLRPC$Document);
                        }
                    }
                    if (size != arrayList.size()) {
                        StickersSearchAdapter.this.emojiStickers.put(arrayList, StickersSearchAdapter.this.searchQuery);
                        if (size == 0) {
                            StickersSearchAdapter.this.emojiArrays.add(arrayList);
                        }
                        StickersSearchAdapter.this.notifyDataSetChanged();
                    }
                }
            }
        }
    };
    /* access modifiers changed from: private */
    public ArrayList<TLRPC$StickerSetCovered> serverPacks = new ArrayList<>();
    private int totalItems;

    public interface Delegate {
        String[] getLastSearchKeyboardLanguage();

        int getStickersPerRow();

        void onSearchStart();

        void onSearchStop();

        void onStickerSetAdd(TLRPC$StickerSetCovered tLRPC$StickerSetCovered, boolean z);

        void onStickerSetRemove(TLRPC$StickerSetCovered tLRPC$StickerSetCovered);

        void setAdapterVisible(boolean z);

        void setLastSearchKeyboardLanguage(String[] strArr);
    }

    public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
        return false;
    }

    static /* synthetic */ int access$804(StickersSearchAdapter stickersSearchAdapter) {
        int i = stickersSearchAdapter.emojiSearchId + 1;
        stickersSearchAdapter.emojiSearchId = i;
        return i;
    }

    public StickersSearchAdapter(Context context2, Delegate delegate2, TLRPC$StickerSetCovered[] tLRPC$StickerSetCoveredArr, LongSparseArray<TLRPC$StickerSetCovered> longSparseArray, LongSparseArray<TLRPC$StickerSetCovered> longSparseArray2) {
        this.context = context2;
        this.delegate = delegate2;
        this.primaryInstallingStickerSets = tLRPC$StickerSetCoveredArr;
        this.installingStickerSets = longSparseArray;
        this.removingStickerSets = longSparseArray2;
    }

    public int getItemCount() {
        return Math.max(1, this.totalItems + 1);
    }

    public void search(String str) {
        if (this.reqId != 0) {
            ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.reqId, true);
            this.reqId = 0;
        }
        if (this.reqId2 != 0) {
            ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.reqId2, true);
            this.reqId2 = 0;
        }
        if (TextUtils.isEmpty(str)) {
            this.searchQuery = null;
            this.localPacks.clear();
            this.emojiStickers.clear();
            this.serverPacks.clear();
            this.delegate.setAdapterVisible(false);
            notifyDataSetChanged();
        } else {
            this.searchQuery = str.toLowerCase();
        }
        AndroidUtilities.cancelRunOnUIThread(this.searchRunnable);
        AndroidUtilities.runOnUIThread(this.searchRunnable, 300);
    }

    public int getItemViewType(int i) {
        if (i == 0 && this.totalItems == 0) {
            return 5;
        }
        if (i == getItemCount() - 1) {
            return 4;
        }
        Object obj = this.cache.get(i);
        if (obj == null) {
            return 1;
        }
        if (obj instanceof TLRPC$Document) {
            return 0;
        }
        return obj instanceof TLRPC$StickerSetCovered ? 3 : 2;
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreateViewHolder$0(View view) {
        FeaturedStickerSetInfoCell featuredStickerSetInfoCell = (FeaturedStickerSetInfoCell) view.getParent();
        TLRPC$StickerSetCovered stickerSet = featuredStickerSetInfoCell.getStickerSet();
        if (stickerSet != null && this.installingStickerSets.indexOfKey(stickerSet.set.id) < 0 && this.removingStickerSets.indexOfKey(stickerSet.set.id) < 0) {
            if (featuredStickerSetInfoCell.isInstalled()) {
                this.removingStickerSets.put(stickerSet.set.id, stickerSet);
                this.delegate.onStickerSetRemove(featuredStickerSetInfoCell.getStickerSet());
                return;
            }
            installStickerSet(stickerSet, featuredStickerSetInfoCell);
        }
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view;
        LinearLayout linearLayout;
        if (i != 0) {
            if (i == 1) {
                view = new EmptyCell(this.context);
            } else if (i == 2) {
                linearLayout = new StickerSetNameCell(this.context, false, true);
            } else if (i == 3) {
                FeaturedStickerSetInfoCell featuredStickerSetInfoCell = new FeaturedStickerSetInfoCell(this.context, 17, true);
                featuredStickerSetInfoCell.setAddOnClickListener(new StickersSearchAdapter$$ExternalSyntheticLambda0(this));
                linearLayout = featuredStickerSetInfoCell;
            } else if (i == 4) {
                view = new View(this.context);
            } else if (i != 5) {
                view = null;
            } else {
                LinearLayout linearLayout2 = new LinearLayout(this.context);
                linearLayout2.setOrientation(1);
                linearLayout2.setGravity(17);
                ImageView imageView = new ImageView(this.context);
                this.emptyImageView = imageView;
                imageView.setScaleType(ImageView.ScaleType.CENTER);
                this.emptyImageView.setImageResource(NUM);
                this.emptyImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_emojiPanelEmptyText"), PorterDuff.Mode.MULTIPLY));
                linearLayout2.addView(this.emptyImageView, LayoutHelper.createLinear(-2, -2));
                linearLayout2.addView(new Space(this.context), LayoutHelper.createLinear(-1, 15));
                TextView textView = new TextView(this.context);
                this.emptyTextView = textView;
                textView.setText(LocaleController.getString("NoStickersFound", NUM));
                this.emptyTextView.setTextSize(1, 16.0f);
                this.emptyTextView.setTextColor(Theme.getColor("chat_emojiPanelEmptyText"));
                linearLayout2.addView(this.emptyTextView, LayoutHelper.createLinear(-2, -2));
                linearLayout2.setMinimumHeight(AndroidUtilities.dp(112.0f));
                linearLayout2.setLayoutParams(LayoutHelper.createFrame(-1, -1.0f));
                linearLayout = linearLayout2;
            }
            return new RecyclerListView.Holder(view);
        }
        AnonymousClass2 r8 = new StickerEmojiCell(this, this.context, false) {
            public void onMeasure(int i, int i2) {
                super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(82.0f), NUM));
            }
        };
        r8.getImageView().setLayerNum(3);
        linearLayout = r8;
        view = linearLayout;
        return new RecyclerListView.Holder(view);
    }

    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        int itemViewType = viewHolder.getItemViewType();
        if (itemViewType == 0) {
            ((StickerEmojiCell) viewHolder.itemView).setSticker((TLRPC$Document) this.cache.get(i), (SendMessagesHelper.ImportingSticker) null, this.cacheParent.get(i), this.positionToEmoji.get(i), false);
        } else if (itemViewType == 1) {
            ((EmptyCell) viewHolder.itemView).setHeight(0);
        } else if (itemViewType == 2) {
            StickerSetNameCell stickerSetNameCell = (StickerSetNameCell) viewHolder.itemView;
            Object obj = this.cache.get(i);
            if (obj instanceof TLRPC$TL_messages_stickerSet) {
                TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet = (TLRPC$TL_messages_stickerSet) obj;
                if (TextUtils.isEmpty(this.searchQuery) || !this.localPacksByShortName.containsKey(tLRPC$TL_messages_stickerSet)) {
                    Integer num = this.localPacksByName.get(tLRPC$TL_messages_stickerSet);
                    TLRPC$StickerSet tLRPC$StickerSet = tLRPC$TL_messages_stickerSet.set;
                    if (!(tLRPC$StickerSet == null || num == null)) {
                        stickerSetNameCell.setText(tLRPC$StickerSet.title, 0, num.intValue(), !TextUtils.isEmpty(this.searchQuery) ? this.searchQuery.length() : 0);
                    }
                    stickerSetNameCell.setUrl((CharSequence) null, 0);
                    return;
                }
                TLRPC$StickerSet tLRPC$StickerSet2 = tLRPC$TL_messages_stickerSet.set;
                if (tLRPC$StickerSet2 != null) {
                    stickerSetNameCell.setText(tLRPC$StickerSet2.title, 0);
                }
                stickerSetNameCell.setUrl(tLRPC$TL_messages_stickerSet.set.short_name, this.searchQuery.length());
            }
        } else if (itemViewType == 3) {
            bindFeaturedStickerSetInfoCell((FeaturedStickerSetInfoCell) viewHolder.itemView, i, false);
        }
    }

    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i, List list) {
        if (!list.contains(0) || viewHolder.getItemViewType() != 3) {
            super.onBindViewHolder(viewHolder, i, list);
        } else {
            bindFeaturedStickerSetInfoCell((FeaturedStickerSetInfoCell) viewHolder.itemView, i, true);
        }
    }

    public void installStickerSet(TLRPC$InputStickerSet tLRPC$InputStickerSet) {
        for (int i = 0; i < this.serverPacks.size(); i++) {
            TLRPC$StickerSetCovered tLRPC$StickerSetCovered = this.serverPacks.get(i);
            if (tLRPC$StickerSetCovered.set.id == tLRPC$InputStickerSet.id) {
                installStickerSet(tLRPC$StickerSetCovered, (FeaturedStickerSetInfoCell) null);
                return;
            }
        }
    }

    public void installStickerSet(TLRPC$StickerSetCovered tLRPC$StickerSetCovered, FeaturedStickerSetInfoCell featuredStickerSetInfoCell) {
        boolean z;
        int i = 0;
        while (true) {
            TLRPC$StickerSetCovered[] tLRPC$StickerSetCoveredArr = this.primaryInstallingStickerSets;
            if (i >= tLRPC$StickerSetCoveredArr.length) {
                break;
            }
            if (tLRPC$StickerSetCoveredArr[i] != null) {
                TLRPC$TL_messages_stickerSet stickerSetById = MediaDataController.getInstance(this.currentAccount).getStickerSetById(this.primaryInstallingStickerSets[i].set.id);
                if (stickerSetById != null && !stickerSetById.set.archived) {
                    this.primaryInstallingStickerSets[i] = null;
                    break;
                } else if (this.primaryInstallingStickerSets[i].set.id == tLRPC$StickerSetCovered.set.id) {
                    return;
                }
            }
            i++;
        }
        int i2 = 0;
        while (true) {
            TLRPC$StickerSetCovered[] tLRPC$StickerSetCoveredArr2 = this.primaryInstallingStickerSets;
            if (i2 >= tLRPC$StickerSetCoveredArr2.length) {
                z = false;
                break;
            } else if (tLRPC$StickerSetCoveredArr2[i2] == null) {
                tLRPC$StickerSetCoveredArr2[i2] = tLRPC$StickerSetCovered;
                z = true;
                break;
            } else {
                i2++;
            }
        }
        if (!z && featuredStickerSetInfoCell != null) {
            featuredStickerSetInfoCell.setAddDrawProgress(true, true);
        }
        this.installingStickerSets.put(tLRPC$StickerSetCovered.set.id, tLRPC$StickerSetCovered);
        if (featuredStickerSetInfoCell != null) {
            this.delegate.onStickerSetAdd(featuredStickerSetInfoCell.getStickerSet(), z);
            return;
        }
        int size = this.positionsToSets.size();
        int i3 = 0;
        while (i3 < size) {
            TLRPC$StickerSetCovered tLRPC$StickerSetCovered2 = this.positionsToSets.get(i3);
            if (tLRPC$StickerSetCovered2 == null || tLRPC$StickerSetCovered2.set.id != tLRPC$StickerSetCovered.set.id) {
                i3++;
            } else {
                notifyItemChanged(i3, 0);
                return;
            }
        }
    }

    private void bindFeaturedStickerSetInfoCell(FeaturedStickerSetInfoCell featuredStickerSetInfoCell, int i, boolean z) {
        boolean z2;
        boolean z3;
        boolean z4;
        FeaturedStickerSetInfoCell featuredStickerSetInfoCell2 = featuredStickerSetInfoCell;
        int i2 = i;
        MediaDataController instance = MediaDataController.getInstance(this.currentAccount);
        ArrayList<Long> unreadStickerSets = instance.getUnreadStickerSets();
        TLRPC$StickerSetCovered tLRPC$StickerSetCovered = (TLRPC$StickerSetCovered) this.cache.get(i2);
        boolean z5 = true;
        boolean z6 = unreadStickerSets != null && unreadStickerSets.contains(Long.valueOf(tLRPC$StickerSetCovered.set.id));
        int i3 = 0;
        while (true) {
            TLRPC$StickerSetCovered[] tLRPC$StickerSetCoveredArr = this.primaryInstallingStickerSets;
            if (i3 >= tLRPC$StickerSetCoveredArr.length) {
                z2 = false;
                break;
            }
            if (tLRPC$StickerSetCoveredArr[i3] != null) {
                TLRPC$TL_messages_stickerSet stickerSetById = MediaDataController.getInstance(this.currentAccount).getStickerSetById(this.primaryInstallingStickerSets[i3].set.id);
                if (stickerSetById != null && !stickerSetById.set.archived) {
                    this.primaryInstallingStickerSets[i3] = null;
                } else if (this.primaryInstallingStickerSets[i3].set.id == tLRPC$StickerSetCovered.set.id) {
                    z2 = true;
                    break;
                }
            }
            i3++;
        }
        int indexOfIgnoreCase = TextUtils.isEmpty(this.searchQuery) ? -1 : AndroidUtilities.indexOfIgnoreCase(tLRPC$StickerSetCovered.set.title, this.searchQuery);
        if (indexOfIgnoreCase >= 0) {
            featuredStickerSetInfoCell.setStickerSet(tLRPC$StickerSetCovered, z6, z, indexOfIgnoreCase, this.searchQuery.length(), z2);
        } else {
            featuredStickerSetInfoCell.setStickerSet(tLRPC$StickerSetCovered, z6, z, 0, 0, z2);
            if (!TextUtils.isEmpty(this.searchQuery) && AndroidUtilities.indexOfIgnoreCase(tLRPC$StickerSetCovered.set.short_name, this.searchQuery) == 0) {
                featuredStickerSetInfoCell2.setUrl(tLRPC$StickerSetCovered.set.short_name, this.searchQuery.length());
            }
        }
        if (z6) {
            instance.markFaturedStickersByIdAsRead(tLRPC$StickerSetCovered.set.id);
        }
        boolean z7 = this.installingStickerSets.indexOfKey(tLRPC$StickerSetCovered.set.id) >= 0;
        boolean z8 = this.removingStickerSets.indexOfKey(tLRPC$StickerSetCovered.set.id) >= 0;
        if (z7 || z8) {
            if (z7 && featuredStickerSetInfoCell.isInstalled()) {
                this.installingStickerSets.remove(tLRPC$StickerSetCovered.set.id);
                z7 = false;
            } else if (z8 && !featuredStickerSetInfoCell.isInstalled()) {
                this.removingStickerSets.remove(tLRPC$StickerSetCovered.set.id);
            }
        }
        if (z2 || !z7) {
            z4 = z;
            z3 = false;
        } else {
            z4 = z;
            z3 = true;
        }
        featuredStickerSetInfoCell2.setAddDrawProgress(z3, z4);
        instance.preloadStickerSetThumb(tLRPC$StickerSetCovered);
        if (i2 <= 0) {
            z5 = false;
        }
        featuredStickerSetInfoCell2.setNeedDivider(z5);
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v2, resolved type: org.telegram.tgnet.TLRPC$StickerSetCovered} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v21, resolved type: org.telegram.tgnet.TLRPC$TL_messages_stickerSet} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v22, resolved type: org.telegram.tgnet.TLRPC$StickerSetCovered} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v23, resolved type: org.telegram.tgnet.TLRPC$StickerSetCovered} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void notifyDataSetChanged() {
        /*
            r19 = this;
            r0 = r19
            android.util.SparseArray<java.lang.Object> r1 = r0.rowStartPack
            r1.clear()
            android.util.SparseIntArray r1 = r0.positionToRow
            r1.clear()
            android.util.SparseArray<java.lang.Object> r1 = r0.cache
            r1.clear()
            android.util.SparseArray<org.telegram.tgnet.TLRPC$StickerSetCovered> r1 = r0.positionsToSets
            r1.clear()
            android.util.SparseArray<java.lang.String> r1 = r0.positionToEmoji
            r1.clear()
            r1 = 0
            r0.totalItems = r1
            java.util.ArrayList<org.telegram.tgnet.TLRPC$StickerSetCovered> r2 = r0.serverPacks
            int r2 = r2.size()
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_messages_stickerSet> r3 = r0.localPacks
            int r3 = r3.size()
            java.util.ArrayList<java.util.ArrayList<org.telegram.tgnet.TLRPC$Document>> r4 = r0.emojiArrays
            boolean r4 = r4.isEmpty()
            r4 = r4 ^ 1
            r5 = 0
            r6 = 0
        L_0x0034:
            int r7 = r2 + r3
            int r7 = r7 + r4
            if (r5 >= r7) goto L_0x01ab
            if (r5 >= r3) goto L_0x0049
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_messages_stickerSet> r7 = r0.localPacks
            java.lang.Object r7 = r7.get(r5)
            org.telegram.tgnet.TLRPC$TL_messages_stickerSet r7 = (org.telegram.tgnet.TLRPC$TL_messages_stickerSet) r7
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r8 = r7.documents
            r16 = r2
            goto L_0x0110
        L_0x0049:
            int r7 = r5 - r3
            if (r7 >= r4) goto L_0x0102
            java.util.ArrayList<java.util.ArrayList<org.telegram.tgnet.TLRPC$Document>> r7 = r0.emojiArrays
            int r7 = r7.size()
            java.lang.String r8 = ""
            r9 = 0
            r10 = 0
        L_0x0057:
            if (r9 >= r7) goto L_0x00d0
            java.util.ArrayList<java.util.ArrayList<org.telegram.tgnet.TLRPC$Document>> r11 = r0.emojiArrays
            java.lang.Object r11 = r11.get(r9)
            java.util.ArrayList r11 = (java.util.ArrayList) r11
            java.util.HashMap<java.util.ArrayList<org.telegram.tgnet.TLRPC$Document>, java.lang.String> r12 = r0.emojiStickers
            java.lang.Object r12 = r12.get(r11)
            java.lang.String r12 = (java.lang.String) r12
            if (r12 == 0) goto L_0x007a
            boolean r13 = r8.equals(r12)
            if (r13 != 0) goto L_0x007a
            android.util.SparseArray<java.lang.String> r8 = r0.positionToEmoji
            int r13 = r0.totalItems
            int r13 = r13 + r10
            r8.put(r13, r12)
            r8 = r12
        L_0x007a:
            int r12 = r11.size()
            r13 = 0
        L_0x007f:
            if (r13 >= r12) goto L_0x00c6
            int r14 = r0.totalItems
            int r14 = r14 + r10
            org.telegram.ui.Adapters.StickersSearchAdapter$Delegate r15 = r0.delegate
            int r15 = r15.getStickersPerRow()
            int r15 = r10 / r15
            int r15 = r15 + r6
            java.lang.Object r16 = r11.get(r13)
            r1 = r16
            org.telegram.tgnet.TLRPC$Document r1 = (org.telegram.tgnet.TLRPC$Document) r1
            r16 = r2
            android.util.SparseArray<java.lang.Object> r2 = r0.cache
            r2.put(r14, r1)
            int r2 = r0.currentAccount
            org.telegram.messenger.MediaDataController r2 = org.telegram.messenger.MediaDataController.getInstance(r2)
            r17 = r7
            r18 = r8
            long r7 = org.telegram.messenger.MediaDataController.getStickerSetId(r1)
            org.telegram.tgnet.TLRPC$TL_messages_stickerSet r1 = r2.getStickerSetById(r7)
            if (r1 == 0) goto L_0x00b5
            android.util.SparseArray<java.lang.Object> r2 = r0.cacheParent
            r2.put(r14, r1)
        L_0x00b5:
            android.util.SparseIntArray r1 = r0.positionToRow
            r1.put(r14, r15)
            int r10 = r10 + 1
            int r13 = r13 + 1
            r2 = r16
            r7 = r17
            r8 = r18
            r1 = 0
            goto L_0x007f
        L_0x00c6:
            r16 = r2
            r17 = r7
            r18 = r8
            int r9 = r9 + 1
            r1 = 0
            goto L_0x0057
        L_0x00d0:
            r16 = r2
            float r1 = (float) r10
            org.telegram.ui.Adapters.StickersSearchAdapter$Delegate r2 = r0.delegate
            int r2 = r2.getStickersPerRow()
            float r2 = (float) r2
            float r1 = r1 / r2
            double r1 = (double) r1
            double r1 = java.lang.Math.ceil(r1)
            int r1 = (int) r1
            r2 = 0
        L_0x00e2:
            if (r2 >= r1) goto L_0x00f2
            android.util.SparseArray<java.lang.Object> r7 = r0.rowStartPack
            int r8 = r6 + r2
            java.lang.Integer r9 = java.lang.Integer.valueOf(r10)
            r7.put(r8, r9)
            int r2 = r2 + 1
            goto L_0x00e2
        L_0x00f2:
            int r2 = r0.totalItems
            org.telegram.ui.Adapters.StickersSearchAdapter$Delegate r7 = r0.delegate
            int r7 = r7.getStickersPerRow()
            int r7 = r7 * r1
            int r2 = r2 + r7
            r0.totalItems = r2
            int r6 = r6 + r1
            goto L_0x01a4
        L_0x0102:
            r16 = r2
            int r7 = r7 - r4
            java.util.ArrayList<org.telegram.tgnet.TLRPC$StickerSetCovered> r1 = r0.serverPacks
            java.lang.Object r1 = r1.get(r7)
            r7 = r1
            org.telegram.tgnet.TLRPC$StickerSetCovered r7 = (org.telegram.tgnet.TLRPC$StickerSetCovered) r7
            java.util.ArrayList<org.telegram.tgnet.TLRPC$Document> r8 = r7.covers
        L_0x0110:
            boolean r1 = r8.isEmpty()
            if (r1 == 0) goto L_0x0118
            goto L_0x01a4
        L_0x0118:
            int r1 = r8.size()
            float r1 = (float) r1
            org.telegram.ui.Adapters.StickersSearchAdapter$Delegate r2 = r0.delegate
            int r2 = r2.getStickersPerRow()
            float r2 = (float) r2
            float r1 = r1 / r2
            double r1 = (double) r1
            double r1 = java.lang.Math.ceil(r1)
            int r1 = (int) r1
            android.util.SparseArray<java.lang.Object> r2 = r0.cache
            int r9 = r0.totalItems
            r2.put(r9, r7)
            if (r5 < r3) goto L_0x0142
            boolean r2 = r7 instanceof org.telegram.tgnet.TLRPC$StickerSetCovered
            if (r2 == 0) goto L_0x0142
            android.util.SparseArray<org.telegram.tgnet.TLRPC$StickerSetCovered> r2 = r0.positionsToSets
            int r9 = r0.totalItems
            r10 = r7
            org.telegram.tgnet.TLRPC$StickerSetCovered r10 = (org.telegram.tgnet.TLRPC$StickerSetCovered) r10
            r2.put(r9, r10)
        L_0x0142:
            android.util.SparseIntArray r2 = r0.positionToRow
            int r9 = r0.totalItems
            r2.put(r9, r6)
            int r2 = r8.size()
            r9 = 0
        L_0x014e:
            if (r9 >= r2) goto L_0x0185
            int r10 = r9 + 1
            int r11 = r0.totalItems
            int r11 = r11 + r10
            int r12 = r6 + 1
            org.telegram.ui.Adapters.StickersSearchAdapter$Delegate r13 = r0.delegate
            int r13 = r13.getStickersPerRow()
            int r13 = r9 / r13
            int r12 = r12 + r13
            java.lang.Object r9 = r8.get(r9)
            org.telegram.tgnet.TLRPC$Document r9 = (org.telegram.tgnet.TLRPC$Document) r9
            android.util.SparseArray<java.lang.Object> r13 = r0.cache
            r13.put(r11, r9)
            android.util.SparseArray<java.lang.Object> r9 = r0.cacheParent
            r9.put(r11, r7)
            android.util.SparseIntArray r9 = r0.positionToRow
            r9.put(r11, r12)
            if (r5 < r3) goto L_0x0183
            boolean r9 = r7 instanceof org.telegram.tgnet.TLRPC$StickerSetCovered
            if (r9 == 0) goto L_0x0183
            android.util.SparseArray<org.telegram.tgnet.TLRPC$StickerSetCovered> r9 = r0.positionsToSets
            r12 = r7
            org.telegram.tgnet.TLRPC$StickerSetCovered r12 = (org.telegram.tgnet.TLRPC$StickerSetCovered) r12
            r9.put(r11, r12)
        L_0x0183:
            r9 = r10
            goto L_0x014e
        L_0x0185:
            int r2 = r1 + 1
            r8 = 0
        L_0x0188:
            if (r8 >= r2) goto L_0x0194
            android.util.SparseArray<java.lang.Object> r9 = r0.rowStartPack
            int r10 = r6 + r8
            r9.put(r10, r7)
            int r8 = r8 + 1
            goto L_0x0188
        L_0x0194:
            int r7 = r0.totalItems
            org.telegram.ui.Adapters.StickersSearchAdapter$Delegate r8 = r0.delegate
            int r8 = r8.getStickersPerRow()
            int r1 = r1 * r8
            int r1 = r1 + 1
            int r7 = r7 + r1
            r0.totalItems = r7
            int r6 = r6 + r2
        L_0x01a4:
            int r5 = r5 + 1
            r2 = r16
            r1 = 0
            goto L_0x0034
        L_0x01ab:
            super.notifyDataSetChanged()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Adapters.StickersSearchAdapter.notifyDataSetChanged():void");
    }

    public int getSpanSize(int i) {
        if (i == this.totalItems || (this.cache.get(i) != null && !(this.cache.get(i) instanceof TLRPC$Document))) {
            return this.delegate.getStickersPerRow();
        }
        return 1;
    }

    public TLRPC$StickerSetCovered getSetForPosition(int i) {
        return this.positionsToSets.get(i);
    }

    public void updateColors(RecyclerListView recyclerListView) {
        int childCount = recyclerListView.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = recyclerListView.getChildAt(i);
            if (childAt instanceof FeaturedStickerSetInfoCell) {
                ((FeaturedStickerSetInfoCell) childAt).updateColors();
            } else if (childAt instanceof StickerSetNameCell) {
                ((StickerSetNameCell) childAt).updateColors();
            }
        }
    }

    public void getThemeDescriptions(List<ThemeDescription> list, RecyclerListView recyclerListView, ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate) {
        List<ThemeDescription> list2 = list;
        FeaturedStickerSetInfoCell.createThemeDescriptions(list, recyclerListView, themeDescriptionDelegate);
        StickerSetNameCell.createThemeDescriptions(list, recyclerListView, themeDescriptionDelegate);
        list2.add(new ThemeDescription(this.emptyImageView, ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_emojiPanelEmptyText"));
        list2.add(new ThemeDescription(this.emptyTextView, ThemeDescription.FLAG_TEXTCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "chat_emojiPanelEmptyText"));
    }
}
