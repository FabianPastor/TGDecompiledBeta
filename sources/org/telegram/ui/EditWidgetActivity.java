package org.telegram.ui;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.SpannableStringBuilder;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import androidx.collection.LongSparseArray;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatsWidgetProvider;
import org.telegram.messenger.ContactsWidgetProvider;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.SharedConfig;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.GroupCreateUserCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Components.BackgroundGradientDrawable;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.InviteMembersBottomSheet;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;

public class EditWidgetActivity extends BaseFragment {
    public static final int TYPE_CHATS = 0;
    public static final int TYPE_CONTACTS = 1;
    private static final int done_item = 1;
    /* access modifiers changed from: private */
    public int chatsEndRow;
    /* access modifiers changed from: private */
    public int chatsStartRow;
    /* access modifiers changed from: private */
    public int currentWidgetId;
    /* access modifiers changed from: private */
    public EditWidgetActivityDelegate delegate;
    /* access modifiers changed from: private */
    public int infoRow;
    /* access modifiers changed from: private */
    public ItemTouchHelper itemTouchHelper;
    /* access modifiers changed from: private */
    public ListAdapter listAdapter;
    /* access modifiers changed from: private */
    public RecyclerListView listView;
    /* access modifiers changed from: private */
    public ImageView previewImageView;
    /* access modifiers changed from: private */
    public int previewRow;
    /* access modifiers changed from: private */
    public int rowCount;
    /* access modifiers changed from: private */
    public int selectChatsRow;
    /* access modifiers changed from: private */
    public ArrayList<Long> selectedDialogs = new ArrayList<>();
    private FrameLayout widgetPreview;
    /* access modifiers changed from: private */
    public WidgetPreviewCell widgetPreviewCell;
    /* access modifiers changed from: private */
    public int widgetType;

    public interface EditWidgetActivityDelegate {
        void didSelectDialogs(ArrayList<Long> arrayList);
    }

    public class TouchHelperCallback extends ItemTouchHelper.Callback {
        private boolean moved;

        public TouchHelperCallback() {
        }

        public boolean isLongPressDragEnabled() {
            return false;
        }

        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            if (viewHolder.getItemViewType() != 3) {
                return makeMovementFlags(0, 0);
            }
            return makeMovementFlags(3, 0);
        }

        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder source, RecyclerView.ViewHolder target) {
            boolean z = false;
            if (source.getItemViewType() != target.getItemViewType()) {
                return false;
            }
            int p1 = source.getAdapterPosition();
            int p2 = target.getAdapterPosition();
            if (EditWidgetActivity.this.listAdapter.swapElements(p1, p2)) {
                ((GroupCreateUserCell) source.itemView).setDrawDivider(p2 != EditWidgetActivity.this.chatsEndRow - 1);
                GroupCreateUserCell groupCreateUserCell = (GroupCreateUserCell) target.itemView;
                if (p1 != EditWidgetActivity.this.chatsEndRow - 1) {
                    z = true;
                }
                groupCreateUserCell.setDrawDivider(z);
                this.moved = true;
            }
            return true;
        }

        public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }

        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
            if (actionState != 0) {
                EditWidgetActivity.this.listView.cancelClickRunnables(false);
                viewHolder.itemView.setPressed(true);
            } else if (this.moved) {
                if (EditWidgetActivity.this.widgetPreviewCell != null) {
                    EditWidgetActivity.this.widgetPreviewCell.updateDialogs();
                }
                this.moved = false;
            }
            super.onSelectedChanged(viewHolder, actionState);
        }

        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        }

        public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            super.clearView(recyclerView, viewHolder);
            viewHolder.itemView.setPressed(false);
        }
    }

    public class WidgetPreviewCell extends FrameLayout {
        private Drawable backgroundDrawable;
        private BackgroundGradientDrawable.Disposable backgroundGradientDisposable;
        private RectF bitmapRect = new RectF();
        private ViewGroup[] cells = new ViewGroup[2];
        private Drawable oldBackgroundDrawable;
        private BackgroundGradientDrawable.Disposable oldBackgroundGradientDisposable;
        private Paint roundPaint = new Paint(1);
        private Drawable shadowDrawable;
        final /* synthetic */ EditWidgetActivity this$0;

        /* JADX WARNING: Illegal instructions before constructor call */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public WidgetPreviewCell(org.telegram.ui.EditWidgetActivity r19, android.content.Context r20) {
            /*
                r18 = this;
                r0 = r18
                r1 = r19
                r2 = r20
                r0.this$0 = r1
                r0.<init>(r2)
                android.graphics.Paint r3 = new android.graphics.Paint
                r4 = 1
                r3.<init>(r4)
                r0.roundPaint = r3
                android.graphics.RectF r3 = new android.graphics.RectF
                r3.<init>()
                r0.bitmapRect = r3
                r3 = 2
                android.view.ViewGroup[] r5 = new android.view.ViewGroup[r3]
                r0.cells = r5
                r5 = 0
                r0.setWillNotDraw(r5)
                r6 = 1103101952(0x41CLASSNAME, float:24.0)
                int r7 = org.telegram.messenger.AndroidUtilities.dp(r6)
                int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
                r0.setPadding(r5, r7, r5, r6)
                android.widget.LinearLayout r5 = new android.widget.LinearLayout
                r5.<init>(r2)
                r5.setOrientation(r4)
                r6 = -2
                r7 = 17
                android.widget.FrameLayout$LayoutParams r8 = org.telegram.ui.Components.LayoutHelper.createFrame((int) r6, (int) r6, (int) r7)
                r0.addView(r5, r8)
                org.telegram.ui.Cells.ChatActionCell r8 = new org.telegram.ui.Cells.ChatActionCell
                r8.<init>(r2)
                java.lang.String r9 = "WidgetPreview"
                r10 = 2131628700(0x7f0e129c, float:1.88847E38)
                java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r9, r10)
                r8.setCustomText(r9)
                r10 = -2
                r11 = -2
                r12 = 17
                r13 = 0
                r14 = 0
                r15 = 0
                r16 = 4
                android.widget.LinearLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r10, (int) r11, (int) r12, (int) r13, (int) r14, (int) r15, (int) r16)
                r5.addView(r8, r9)
                android.widget.LinearLayout r9 = new android.widget.LinearLayout
                r9.<init>(r2)
                r9.setOrientation(r4)
                r10 = 2131166203(0x7var_fb, float:1.7946645E38)
                r9.setBackgroundResource(r10)
                r12 = -2
                r13 = 17
                r14 = 10
                r16 = 10
                r17 = 0
                android.widget.LinearLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r11, (int) r12, (int) r13, (int) r14, (int) r15, (int) r16, (int) r17)
                r5.addView(r9, r10)
                android.widget.ImageView r10 = new android.widget.ImageView
                r10.<init>(r2)
                android.widget.ImageView unused = r1.previewImageView = r10
                int r10 = r19.widgetType
                r11 = 0
                r12 = 160(0xa0, float:2.24E-43)
                if (r10 != 0) goto L_0x00d1
                r4 = 0
            L_0x0093:
                if (r4 >= r3) goto L_0x00b9
                android.view.ViewGroup[] r10 = r0.cells
                android.app.Activity r13 = r19.getParentActivity()
                android.view.LayoutInflater r13 = r13.getLayoutInflater()
                r14 = 2131427349(0x7f0b0015, float:1.8476312E38)
                android.view.View r13 = r13.inflate(r14, r11)
                android.view.ViewGroup r13 = (android.view.ViewGroup) r13
                r10[r4] = r13
                android.view.ViewGroup[] r10 = r0.cells
                r10 = r10[r4]
                r13 = -1
                android.widget.LinearLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createLinear(r13, r6)
                r9.addView(r10, r13)
                int r4 = r4 + 1
                goto L_0x0093
            L_0x00b9:
                android.widget.ImageView r3 = r19.previewImageView
                r4 = 218(0xda, float:3.05E-43)
                android.widget.LinearLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r4, (int) r12, (int) r7)
                r9.addView(r3, r4)
                android.widget.ImageView r3 = r19.previewImageView
                r4 = 2131165360(0x7var_b0, float:1.7944935E38)
                r3.setImageResource(r4)
                goto L_0x0112
            L_0x00d1:
                int r10 = r19.widgetType
                if (r10 != r4) goto L_0x0112
                r4 = 0
            L_0x00d8:
                if (r4 >= r3) goto L_0x00fd
                android.view.ViewGroup[] r10 = r0.cells
                android.app.Activity r13 = r19.getParentActivity()
                android.view.LayoutInflater r13 = r13.getLayoutInflater()
                r14 = 2131427330(0x7f0b0002, float:1.8476273E38)
                android.view.View r13 = r13.inflate(r14, r11)
                android.view.ViewGroup r13 = (android.view.ViewGroup) r13
                r10[r4] = r13
                android.view.ViewGroup[] r10 = r0.cells
                r10 = r10[r4]
                android.widget.LinearLayout$LayoutParams r13 = org.telegram.ui.Components.LayoutHelper.createLinear(r12, r6)
                r9.addView(r10, r13)
                int r4 = r4 + 1
                goto L_0x00d8
            L_0x00fd:
                android.widget.ImageView r3 = r19.previewImageView
                android.widget.LinearLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r12, (int) r12, (int) r7)
                r9.addView(r3, r4)
                android.widget.ImageView r3 = r19.previewImageView
                r4 = 2131165388(0x7var_cc, float:1.7944992E38)
                r3.setImageResource(r4)
            L_0x0112:
                r18.updateDialogs()
                r3 = 2131165466(0x7var_a, float:1.794515E38)
                java.lang.String r4 = "windowBackgroundGrayShadow"
                android.graphics.drawable.Drawable r3 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r2, (int) r3, (java.lang.String) r4)
                r0.shadowDrawable = r3
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.EditWidgetActivity.WidgetPreviewCell.<init>(org.telegram.ui.EditWidgetActivity, android.content.Context):void");
        }

        /* JADX WARNING: Code restructure failed: missing block: B:297:0x084d, code lost:
            if (r3 == 2) goto L_0x0851;
         */
        /* JADX WARNING: Removed duplicated region for block: B:250:0x06ba  */
        /* JADX WARNING: Removed duplicated region for block: B:254:0x0710  */
        /* JADX WARNING: Removed duplicated region for block: B:341:0x0933  */
        /* JADX WARNING: Removed duplicated region for block: B:342:0x0937  */
        /* JADX WARNING: Removed duplicated region for block: B:345:0x0946  */
        /* JADX WARNING: Removed duplicated region for block: B:355:0x097d  */
        /* JADX WARNING: Removed duplicated region for block: B:366:0x09ae A[SYNTHETIC, Splitter:B:366:0x09ae] */
        /* JADX WARNING: Removed duplicated region for block: B:381:0x0a00 A[Catch:{ all -> 0x0a11 }] */
        /* JADX WARNING: Removed duplicated region for block: B:382:0x0a04 A[Catch:{ all -> 0x0a11 }] */
        /* JADX WARNING: Removed duplicated region for block: B:396:0x0a31  */
        /* JADX WARNING: Removed duplicated region for block: B:409:0x0a88  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void updateDialogs() {
            /*
                r33 = this;
                r1 = r33
                org.telegram.ui.EditWidgetActivity r0 = r1.this$0
                int r0 = r0.widgetType
                r2 = 1111490560(0x42400000, float:48.0)
                r3 = 2131627603(0x7f0e0e53, float:1.8882475E38)
                java.lang.String r4 = "SavedMessages"
                r7 = 0
                r9 = 8
                r10 = 2
                r11 = 1
                r12 = 0
                if (r0 != 0) goto L_0x0758
                r0 = 0
                r13 = r0
            L_0x001a:
                if (r13 >= r10) goto L_0x072f
                org.telegram.ui.EditWidgetActivity r0 = r1.this$0
                java.util.ArrayList r0 = r0.selectedDialogs
                boolean r0 = r0.isEmpty()
                if (r0 == 0) goto L_0x0048
                org.telegram.ui.EditWidgetActivity r0 = r1.this$0
                org.telegram.messenger.MessagesController r0 = r0.getMessagesController()
                java.util.ArrayList<org.telegram.tgnet.TLRPC$Dialog> r0 = r0.dialogsServerOnly
                int r0 = r0.size()
                if (r13 >= r0) goto L_0x0045
                org.telegram.ui.EditWidgetActivity r0 = r1.this$0
                org.telegram.messenger.MessagesController r0 = r0.getMessagesController()
                java.util.ArrayList<org.telegram.tgnet.TLRPC$Dialog> r0 = r0.dialogsServerOnly
                java.lang.Object r0 = r0.get(r13)
                org.telegram.tgnet.TLRPC$Dialog r0 = (org.telegram.tgnet.TLRPC.Dialog) r0
                goto L_0x0046
            L_0x0045:
                r0 = 0
            L_0x0046:
                r14 = r0
                goto L_0x0092
            L_0x0048:
                org.telegram.ui.EditWidgetActivity r0 = r1.this$0
                java.util.ArrayList r0 = r0.selectedDialogs
                int r0 = r0.size()
                if (r13 >= r0) goto L_0x0090
                org.telegram.ui.EditWidgetActivity r0 = r1.this$0
                org.telegram.messenger.MessagesController r0 = r0.getMessagesController()
                androidx.collection.LongSparseArray<org.telegram.tgnet.TLRPC$Dialog> r0 = r0.dialogs_dict
                org.telegram.ui.EditWidgetActivity r14 = r1.this$0
                java.util.ArrayList r14 = r14.selectedDialogs
                java.lang.Object r14 = r14.get(r13)
                java.lang.Long r14 = (java.lang.Long) r14
                long r14 = r14.longValue()
                java.lang.Object r0 = r0.get(r14)
                org.telegram.tgnet.TLRPC$Dialog r0 = (org.telegram.tgnet.TLRPC.Dialog) r0
                if (r0 != 0) goto L_0x008e
                org.telegram.tgnet.TLRPC$TL_dialog r14 = new org.telegram.tgnet.TLRPC$TL_dialog
                r14.<init>()
                r0 = r14
                org.telegram.ui.EditWidgetActivity r14 = r1.this$0
                java.util.ArrayList r14 = r14.selectedDialogs
                java.lang.Object r14 = r14.get(r13)
                java.lang.Long r14 = (java.lang.Long) r14
                long r14 = r14.longValue()
                r0.id = r14
                r14 = r0
                goto L_0x0092
            L_0x008e:
                r14 = r0
                goto L_0x0092
            L_0x0090:
                r0 = 0
                r14 = r0
            L_0x0092:
                if (r14 != 0) goto L_0x00a1
                android.view.ViewGroup[] r0 = r1.cells
                r0 = r0[r13]
                r0.setVisibility(r9)
                r19 = r4
                r26 = r13
                goto L_0x071d
            L_0x00a1:
                android.view.ViewGroup[] r0 = r1.cells
                r0 = r0[r13]
                r0.setVisibility(r12)
                java.lang.String r0 = ""
                r15 = 0
                r16 = 0
                r17 = 0
                long r9 = r14.id
                boolean r9 = org.telegram.messenger.DialogObject.isUserDialog(r9)
                if (r9 == 0) goto L_0x013a
                org.telegram.ui.EditWidgetActivity r9 = r1.this$0
                org.telegram.messenger.MessagesController r9 = r9.getMessagesController()
                long r5 = r14.id
                java.lang.Long r5 = java.lang.Long.valueOf(r5)
                org.telegram.tgnet.TLRPC$User r5 = r9.getUser(r5)
                if (r5 == 0) goto L_0x0134
                boolean r6 = org.telegram.messenger.UserObject.isUserSelf(r5)
                if (r6 == 0) goto L_0x00d4
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r4, r3)
                goto L_0x00fc
            L_0x00d4:
                boolean r6 = org.telegram.messenger.UserObject.isReplyUser((org.telegram.tgnet.TLRPC.User) r5)
                if (r6 == 0) goto L_0x00e4
                r6 = 2131627470(0x7f0e0dce, float:1.8882205E38)
                java.lang.String r9 = "RepliesTitle"
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r9, r6)
                goto L_0x00fc
            L_0x00e4:
                boolean r6 = org.telegram.messenger.UserObject.isDeleted(r5)
                if (r6 == 0) goto L_0x00f4
                r6 = 2131625886(0x7f0e079e, float:1.8878993E38)
                java.lang.String r9 = "HiddenName"
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r9, r6)
                goto L_0x00fc
            L_0x00f4:
                java.lang.String r6 = r5.first_name
                java.lang.String r9 = r5.last_name
                java.lang.String r0 = org.telegram.messenger.ContactsController.formatName(r6, r9)
            L_0x00fc:
                boolean r6 = org.telegram.messenger.UserObject.isReplyUser((org.telegram.tgnet.TLRPC.User) r5)
                if (r6 != 0) goto L_0x012e
                boolean r6 = org.telegram.messenger.UserObject.isUserSelf(r5)
                if (r6 != 0) goto L_0x012e
                org.telegram.tgnet.TLRPC$UserProfilePhoto r6 = r5.photo
                if (r6 == 0) goto L_0x012e
                org.telegram.tgnet.TLRPC$UserProfilePhoto r6 = r5.photo
                org.telegram.tgnet.TLRPC$FileLocation r6 = r6.photo_small
                if (r6 == 0) goto L_0x012e
                org.telegram.tgnet.TLRPC$UserProfilePhoto r6 = r5.photo
                org.telegram.tgnet.TLRPC$FileLocation r6 = r6.photo_small
                r19 = r4
                long r3 = r6.volume_id
                int r6 = (r3 > r7 ? 1 : (r3 == r7 ? 0 : -1))
                if (r6 == 0) goto L_0x0130
                org.telegram.tgnet.TLRPC$UserProfilePhoto r3 = r5.photo
                org.telegram.tgnet.TLRPC$FileLocation r3 = r3.photo_small
                int r3 = r3.local_id
                if (r3 == 0) goto L_0x0130
                org.telegram.tgnet.TLRPC$UserProfilePhoto r3 = r5.photo
                org.telegram.tgnet.TLRPC$FileLocation r15 = r3.photo_small
                r3 = r0
                r4 = r17
                goto L_0x017f
            L_0x012e:
                r19 = r4
            L_0x0130:
                r3 = r0
                r4 = r17
                goto L_0x017f
            L_0x0134:
                r19 = r4
                r3 = r0
                r4 = r17
                goto L_0x017f
            L_0x013a:
                r19 = r4
                org.telegram.ui.EditWidgetActivity r3 = r1.this$0
                org.telegram.messenger.MessagesController r3 = r3.getMessagesController()
                long r4 = r14.id
                long r4 = -r4
                java.lang.Long r4 = java.lang.Long.valueOf(r4)
                org.telegram.tgnet.TLRPC$Chat r3 = r3.getChat(r4)
                if (r3 == 0) goto L_0x017b
                java.lang.String r0 = r3.title
                org.telegram.tgnet.TLRPC$ChatPhoto r4 = r3.photo
                if (r4 == 0) goto L_0x0176
                org.telegram.tgnet.TLRPC$ChatPhoto r4 = r3.photo
                org.telegram.tgnet.TLRPC$FileLocation r4 = r4.photo_small
                if (r4 == 0) goto L_0x0176
                org.telegram.tgnet.TLRPC$ChatPhoto r4 = r3.photo
                org.telegram.tgnet.TLRPC$FileLocation r4 = r4.photo_small
                long r4 = r4.volume_id
                int r6 = (r4 > r7 ? 1 : (r4 == r7 ? 0 : -1))
                if (r6 == 0) goto L_0x0176
                org.telegram.tgnet.TLRPC$ChatPhoto r4 = r3.photo
                org.telegram.tgnet.TLRPC$FileLocation r4 = r4.photo_small
                int r4 = r4.local_id
                if (r4 == 0) goto L_0x0176
                org.telegram.tgnet.TLRPC$ChatPhoto r4 = r3.photo
                org.telegram.tgnet.TLRPC$FileLocation r15 = r4.photo_small
                r4 = r3
                r5 = r16
                r3 = r0
                goto L_0x017f
            L_0x0176:
                r4 = r3
                r5 = r16
                r3 = r0
                goto L_0x017f
            L_0x017b:
                r4 = r3
                r5 = r16
                r3 = r0
            L_0x017f:
                android.view.ViewGroup[] r0 = r1.cells
                r0 = r0[r13]
                r6 = 2131230908(0x7var_bc, float:1.8077882E38)
                android.view.View r0 = r0.findViewById(r6)
                android.widget.TextView r0 = (android.widget.TextView) r0
                r0.setText(r3)
                r0 = 0
                if (r15 == 0) goto L_0x01a0
                java.io.File r6 = org.telegram.messenger.FileLoader.getPathToAttach(r15, r11)     // Catch:{ all -> 0x0247 }
                java.lang.String r16 = r6.toString()     // Catch:{ all -> 0x0247 }
                android.graphics.Bitmap r16 = android.graphics.BitmapFactory.decodeFile(r16)     // Catch:{ all -> 0x0247 }
                r0 = r16
            L_0x01a0:
                int r6 = org.telegram.messenger.AndroidUtilities.dp(r2)     // Catch:{ all -> 0x0247 }
                android.graphics.Bitmap$Config r9 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ all -> 0x0247 }
                android.graphics.Bitmap r9 = android.graphics.Bitmap.createBitmap(r6, r6, r9)     // Catch:{ all -> 0x0247 }
                r9.eraseColor(r12)     // Catch:{ all -> 0x0247 }
                android.graphics.Canvas r10 = new android.graphics.Canvas     // Catch:{ all -> 0x0247 }
                r10.<init>(r9)     // Catch:{ all -> 0x0247 }
                if (r0 != 0) goto L_0x01de
                if (r5 == 0) goto L_0x01d1
                org.telegram.ui.Components.AvatarDrawable r2 = new org.telegram.ui.Components.AvatarDrawable     // Catch:{ all -> 0x0247 }
                r2.<init>((org.telegram.tgnet.TLRPC.User) r5)     // Catch:{ all -> 0x0247 }
                boolean r20 = org.telegram.messenger.UserObject.isReplyUser((org.telegram.tgnet.TLRPC.User) r5)     // Catch:{ all -> 0x0247 }
                if (r20 == 0) goto L_0x01c7
                r7 = 12
                r2.setAvatarType(r7)     // Catch:{ all -> 0x0247 }
                goto L_0x01d6
            L_0x01c7:
                boolean r7 = org.telegram.messenger.UserObject.isUserSelf(r5)     // Catch:{ all -> 0x0247 }
                if (r7 == 0) goto L_0x01d6
                r2.setAvatarType(r11)     // Catch:{ all -> 0x0247 }
                goto L_0x01d6
            L_0x01d1:
                org.telegram.ui.Components.AvatarDrawable r2 = new org.telegram.ui.Components.AvatarDrawable     // Catch:{ all -> 0x0247 }
                r2.<init>((org.telegram.tgnet.TLRPC.Chat) r4)     // Catch:{ all -> 0x0247 }
            L_0x01d6:
                r2.setBounds(r12, r12, r6, r6)     // Catch:{ all -> 0x0247 }
                r2.draw(r10)     // Catch:{ all -> 0x0247 }
                r2 = r10
                goto L_0x0232
            L_0x01de:
                android.graphics.BitmapShader r2 = new android.graphics.BitmapShader     // Catch:{ all -> 0x0247 }
                android.graphics.Shader$TileMode r7 = android.graphics.Shader.TileMode.CLAMP     // Catch:{ all -> 0x0247 }
                android.graphics.Shader$TileMode r8 = android.graphics.Shader.TileMode.CLAMP     // Catch:{ all -> 0x0247 }
                r2.<init>(r0, r7, r8)     // Catch:{ all -> 0x0247 }
                android.graphics.Paint r7 = r1.roundPaint     // Catch:{ all -> 0x0247 }
                if (r7 != 0) goto L_0x01f9
                android.graphics.Paint r7 = new android.graphics.Paint     // Catch:{ all -> 0x0247 }
                r7.<init>(r11)     // Catch:{ all -> 0x0247 }
                r1.roundPaint = r7     // Catch:{ all -> 0x0247 }
                android.graphics.RectF r7 = new android.graphics.RectF     // Catch:{ all -> 0x0247 }
                r7.<init>()     // Catch:{ all -> 0x0247 }
                r1.bitmapRect = r7     // Catch:{ all -> 0x0247 }
            L_0x01f9:
                float r7 = (float) r6     // Catch:{ all -> 0x0247 }
                int r8 = r0.getWidth()     // Catch:{ all -> 0x0247 }
                float r8 = (float) r8     // Catch:{ all -> 0x0247 }
                float r7 = r7 / r8
                r10.save()     // Catch:{ all -> 0x0247 }
                r10.scale(r7, r7)     // Catch:{ all -> 0x0247 }
                android.graphics.Paint r8 = r1.roundPaint     // Catch:{ all -> 0x0247 }
                r8.setShader(r2)     // Catch:{ all -> 0x0247 }
                android.graphics.RectF r8 = r1.bitmapRect     // Catch:{ all -> 0x0247 }
                int r11 = r0.getWidth()     // Catch:{ all -> 0x0247 }
                float r11 = (float) r11     // Catch:{ all -> 0x0247 }
                int r12 = r0.getHeight()     // Catch:{ all -> 0x0247 }
                float r12 = (float) r12     // Catch:{ all -> 0x0247 }
                r24 = r2
                r2 = 0
                r8.set(r2, r2, r11, r12)     // Catch:{ all -> 0x0247 }
                r2 = r10
                android.graphics.RectF r8 = r1.bitmapRect     // Catch:{ all -> 0x0247 }
                int r11 = r0.getWidth()     // Catch:{ all -> 0x0247 }
                float r11 = (float) r11     // Catch:{ all -> 0x0247 }
                int r12 = r0.getHeight()     // Catch:{ all -> 0x0247 }
                float r12 = (float) r12     // Catch:{ all -> 0x0247 }
                android.graphics.Paint r10 = r1.roundPaint     // Catch:{ all -> 0x0247 }
                r2.drawRoundRect(r8, r11, r12, r10)     // Catch:{ all -> 0x0247 }
                r2.restore()     // Catch:{ all -> 0x0247 }
            L_0x0232:
                r7 = 0
                r2.setBitmap(r7)     // Catch:{ all -> 0x0247 }
                android.view.ViewGroup[] r7 = r1.cells     // Catch:{ all -> 0x0247 }
                r7 = r7[r13]     // Catch:{ all -> 0x0247 }
                r8 = 2131230904(0x7var_b8, float:1.8077874E38)
                android.view.View r7 = r7.findViewById(r8)     // Catch:{ all -> 0x0247 }
                android.widget.ImageView r7 = (android.widget.ImageView) r7     // Catch:{ all -> 0x0247 }
                r7.setImageBitmap(r9)     // Catch:{ all -> 0x0247 }
                goto L_0x024b
            L_0x0247:
                r0 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            L_0x024b:
                org.telegram.ui.EditWidgetActivity r0 = r1.this$0
                org.telegram.messenger.MessagesController r0 = r0.getMessagesController()
                androidx.collection.LongSparseArray<org.telegram.messenger.MessageObject> r0 = r0.dialogMessage
                long r6 = r14.id
                java.lang.Object r0 = r0.get(r6)
                r2 = r0
                org.telegram.messenger.MessageObject r2 = (org.telegram.messenger.MessageObject) r2
                java.lang.String r0 = ""
                if (r2 == 0) goto L_0x066f
                r8 = 0
                r9 = 0
                long r10 = r2.getFromChatId()
                r20 = 0
                int r12 = (r10 > r20 ? 1 : (r10 == r20 ? 0 : -1))
                if (r12 <= 0) goto L_0x027b
                org.telegram.ui.EditWidgetActivity r12 = r1.this$0
                org.telegram.messenger.MessagesController r12 = r12.getMessagesController()
                java.lang.Long r6 = java.lang.Long.valueOf(r10)
                org.telegram.tgnet.TLRPC$User r8 = r12.getUser(r6)
                goto L_0x028e
            L_0x027b:
                org.telegram.ui.EditWidgetActivity r6 = r1.this$0
                org.telegram.messenger.MessagesController r6 = r6.getMessagesController()
                r25 = r8
                long r7 = -r10
                java.lang.Long r7 = java.lang.Long.valueOf(r7)
                org.telegram.tgnet.TLRPC$Chat r9 = r6.getChat(r7)
                r8 = r25
            L_0x028e:
                android.content.Context r6 = r33.getContext()
                android.content.res.Resources r6 = r6.getResources()
                r7 = 2131034146(0x7var_, float:1.7678801E38)
                int r6 = r6.getColor(r7)
                org.telegram.tgnet.TLRPC$Message r7 = r2.messageOwner
                boolean r7 = r7 instanceof org.telegram.tgnet.TLRPC.TL_messageService
                r12 = 2131034141(0x7var_d, float:1.7678791E38)
                if (r7 == 0) goto L_0x02d7
                boolean r0 = org.telegram.messenger.ChatObject.isChannel(r4)
                if (r0 == 0) goto L_0x02bf
                org.telegram.tgnet.TLRPC$Message r0 = r2.messageOwner
                org.telegram.tgnet.TLRPC$MessageAction r0 = r0.action
                boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageActionHistoryClear
                if (r0 != 0) goto L_0x02bc
                org.telegram.tgnet.TLRPC$Message r0 = r2.messageOwner
                org.telegram.tgnet.TLRPC$MessageAction r0 = r0.action
                boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChannelMigrateFrom
                if (r0 == 0) goto L_0x02bf
            L_0x02bc:
                java.lang.String r0 = ""
                goto L_0x02c1
            L_0x02bf:
                java.lang.CharSequence r0 = r2.messageText
            L_0x02c1:
                android.content.Context r7 = r33.getContext()
                android.content.res.Resources r7 = r7.getResources()
                int r6 = r7.getColor(r12)
                r28 = r3
                r29 = r4
                r31 = r5
                r26 = r13
                goto L_0x0634
            L_0x02d7:
                r7 = 1
                if (r4 == 0) goto L_0x0513
                r26 = r13
                long r12 = r4.id
                r20 = 0
                int r28 = (r12 > r20 ? 1 : (r12 == r20 ? 0 : -1))
                if (r28 <= 0) goto L_0x050a
                if (r9 != 0) goto L_0x050a
                boolean r12 = org.telegram.messenger.ChatObject.isChannel(r4)
                if (r12 == 0) goto L_0x02fd
                boolean r12 = org.telegram.messenger.ChatObject.isMegagroup(r4)
                if (r12 == 0) goto L_0x02f3
                goto L_0x02fd
            L_0x02f3:
                r28 = r3
                r29 = r4
                r31 = r5
                r32 = r6
                goto L_0x051d
            L_0x02fd:
                boolean r12 = r2.isOutOwner()
                if (r12 == 0) goto L_0x030d
                r12 = 2131625794(0x7f0e0742, float:1.8878806E38)
                java.lang.String r13 = "FromYou"
                java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r13, r12)
                goto L_0x031c
            L_0x030d:
                if (r8 == 0) goto L_0x031a
                java.lang.String r12 = org.telegram.messenger.UserObject.getFirstName(r8)
                java.lang.String r13 = "\n"
                java.lang.String r12 = r12.replace(r13, r0)
                goto L_0x031c
            L_0x031a:
                java.lang.String r12 = "DELETED"
            L_0x031c:
                java.lang.String r13 = "%2$s: ⁨%1$s⁩"
                r28 = r3
                java.lang.CharSequence r3 = r2.caption
                r29 = r4
                r4 = 150(0x96, float:2.1E-43)
                if (r3 == 0) goto L_0x0392
                java.lang.CharSequence r0 = r2.caption
                java.lang.String r0 = r0.toString()
                int r3 = r0.length()
                if (r3 <= r4) goto L_0x0339
                r3 = 0
                java.lang.String r0 = r0.substring(r3, r4)
            L_0x0339:
                boolean r3 = r2.isVideo()
                if (r3 == 0) goto L_0x0342
                java.lang.String r3 = "📹 "
                goto L_0x035f
            L_0x0342:
                boolean r3 = r2.isVoice()
                if (r3 == 0) goto L_0x034b
                java.lang.String r3 = "🎤 "
                goto L_0x035f
            L_0x034b:
                boolean r3 = r2.isMusic()
                if (r3 == 0) goto L_0x0354
                java.lang.String r3 = "🎧 "
                goto L_0x035f
            L_0x0354:
                boolean r3 = r2.isPhoto()
                if (r3 == 0) goto L_0x035d
                java.lang.String r3 = "🖼 "
                goto L_0x035f
            L_0x035d:
                java.lang.String r3 = "📎 "
            L_0x035f:
                r31 = r5
                r4 = 2
                java.lang.Object[] r5 = new java.lang.Object[r4]
                java.lang.StringBuilder r4 = new java.lang.StringBuilder
                r4.<init>()
                r4.append(r3)
                r27 = r3
                r32 = r6
                r3 = 32
                r6 = 10
                java.lang.String r3 = r0.replace(r6, r3)
                r4.append(r3)
                java.lang.String r3 = r4.toString()
                r4 = 0
                r5[r4] = r3
                r3 = 1
                r5[r3] = r12
                java.lang.String r3 = java.lang.String.format(r13, r5)
                android.text.SpannableStringBuilder r0 = android.text.SpannableStringBuilder.valueOf(r3)
                r5 = r0
                r6 = r32
                goto L_0x04e7
            L_0x0392:
                r31 = r5
                r32 = r6
                org.telegram.tgnet.TLRPC$Message r3 = r2.messageOwner
                org.telegram.tgnet.TLRPC$MessageMedia r3 = r3.media
                if (r3 == 0) goto L_0x04a9
                boolean r3 = r2.isMediaEmpty()
                if (r3 != 0) goto L_0x04a9
                android.content.Context r0 = r33.getContext()
                android.content.res.Resources r0 = r0.getResources()
                r3 = 2131034141(0x7var_d, float:1.7678791E38)
                int r3 = r0.getColor(r3)
                org.telegram.tgnet.TLRPC$Message r0 = r2.messageOwner
                org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
                boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPoll
                r4 = 18
                if (r0 == 0) goto L_0x03ea
                org.telegram.tgnet.TLRPC$Message r0 = r2.messageOwner
                org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
                org.telegram.tgnet.TLRPC$TL_messageMediaPoll r0 = (org.telegram.tgnet.TLRPC.TL_messageMediaPoll) r0
                int r5 = android.os.Build.VERSION.SDK_INT
                if (r5 < r4) goto L_0x03d7
                r4 = 1
                java.lang.Object[] r5 = new java.lang.Object[r4]
                org.telegram.tgnet.TLRPC$Poll r6 = r0.poll
                java.lang.String r6 = r6.question
                r23 = 0
                r5[r23] = r6
                java.lang.String r6 = "📊 ⁨%s⁩"
                java.lang.String r5 = java.lang.String.format(r6, r5)
                goto L_0x03e8
            L_0x03d7:
                r4 = 1
                r23 = 0
                java.lang.Object[] r5 = new java.lang.Object[r4]
                org.telegram.tgnet.TLRPC$Poll r4 = r0.poll
                java.lang.String r4 = r4.question
                r5[r23] = r4
                java.lang.String r4 = "📊 %s"
                java.lang.String r5 = java.lang.String.format(r4, r5)
            L_0x03e8:
                goto L_0x0462
            L_0x03ea:
                org.telegram.tgnet.TLRPC$Message r0 = r2.messageOwner
                org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
                boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGame
                if (r0 == 0) goto L_0x0420
                int r0 = android.os.Build.VERSION.SDK_INT
                if (r0 < r4) goto L_0x040b
                r4 = 1
                java.lang.Object[] r0 = new java.lang.Object[r4]
                org.telegram.tgnet.TLRPC$Message r5 = r2.messageOwner
                org.telegram.tgnet.TLRPC$MessageMedia r5 = r5.media
                org.telegram.tgnet.TLRPC$TL_game r5 = r5.game
                java.lang.String r5 = r5.title
                r6 = 0
                r0[r6] = r5
                java.lang.String r5 = "🎮 ⁨%s⁩"
                java.lang.String r5 = java.lang.String.format(r5, r0)
                goto L_0x0462
            L_0x040b:
                r4 = 1
                r6 = 0
                java.lang.Object[] r0 = new java.lang.Object[r4]
                org.telegram.tgnet.TLRPC$Message r4 = r2.messageOwner
                org.telegram.tgnet.TLRPC$MessageMedia r4 = r4.media
                org.telegram.tgnet.TLRPC$TL_game r4 = r4.game
                java.lang.String r4 = r4.title
                r0[r6] = r4
                java.lang.String r4 = "🎮 %s"
                java.lang.String r5 = java.lang.String.format(r4, r0)
                goto L_0x0462
            L_0x0420:
                int r0 = r2.type
                r5 = 14
                if (r0 != r5) goto L_0x045c
                int r0 = android.os.Build.VERSION.SDK_INT
                if (r0 < r4) goto L_0x0443
                r4 = 2
                java.lang.Object[] r0 = new java.lang.Object[r4]
                java.lang.String r5 = r2.getMusicAuthor()
                r6 = 0
                r0[r6] = r5
                java.lang.String r5 = r2.getMusicTitle()
                r22 = 1
                r0[r22] = r5
                java.lang.String r5 = "🎧 ⁨%s - %s⁩"
                java.lang.String r5 = java.lang.String.format(r5, r0)
                goto L_0x0462
            L_0x0443:
                r4 = 2
                r6 = 0
                r22 = 1
                java.lang.Object[] r0 = new java.lang.Object[r4]
                java.lang.String r4 = r2.getMusicAuthor()
                r0[r6] = r4
                java.lang.String r4 = r2.getMusicTitle()
                r0[r22] = r4
                java.lang.String r4 = "🎧 %s - %s"
                java.lang.String r5 = java.lang.String.format(r4, r0)
                goto L_0x0462
            L_0x045c:
                java.lang.CharSequence r0 = r2.messageText
                java.lang.String r5 = r0.toString()
            L_0x0462:
                r0 = 32
                r4 = 10
                java.lang.String r4 = r5.replace(r4, r0)
                r5 = 2
                java.lang.Object[] r0 = new java.lang.Object[r5]
                r5 = 0
                r0[r5] = r4
                r5 = 1
                r0[r5] = r12
                java.lang.String r0 = java.lang.String.format(r13, r0)
                android.text.SpannableStringBuilder r5 = android.text.SpannableStringBuilder.valueOf(r0)
                org.telegram.ui.Components.ForegroundColorSpanThemable r0 = new org.telegram.ui.Components.ForegroundColorSpanThemable     // Catch:{ Exception -> 0x049e }
                java.lang.String r6 = "chats_attachMessage"
                r0.<init>(r6)     // Catch:{ Exception -> 0x049e }
                int r6 = r12.length()     // Catch:{ Exception -> 0x049e }
                r18 = 2
                int r6 = r6 + 2
                r27 = r3
                int r3 = r5.length()     // Catch:{ Exception -> 0x049a }
                r30 = r4
                r4 = 33
                r5.setSpan(r0, r6, r3, r4)     // Catch:{ Exception -> 0x0498 }
                goto L_0x04a6
            L_0x0498:
                r0 = move-exception
                goto L_0x04a3
            L_0x049a:
                r0 = move-exception
                r30 = r4
                goto L_0x04a3
            L_0x049e:
                r0 = move-exception
                r27 = r3
                r30 = r4
            L_0x04a3:
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            L_0x04a6:
                r6 = r27
                goto L_0x04e7
            L_0x04a9:
                org.telegram.tgnet.TLRPC$Message r3 = r2.messageOwner
                java.lang.String r3 = r3.message
                if (r3 == 0) goto L_0x04e0
                org.telegram.tgnet.TLRPC$Message r0 = r2.messageOwner
                java.lang.String r0 = r0.message
                int r3 = r0.length()
                if (r3 <= r4) goto L_0x04bf
                r3 = 0
                java.lang.String r0 = r0.substring(r3, r4)
                goto L_0x04c0
            L_0x04bf:
                r3 = 0
            L_0x04c0:
                r4 = 32
                r5 = 10
                java.lang.String r4 = r0.replace(r5, r4)
                java.lang.String r0 = r4.trim()
                r4 = 2
                java.lang.Object[] r5 = new java.lang.Object[r4]
                r5[r3] = r0
                r3 = 1
                r5[r3] = r12
                java.lang.String r3 = java.lang.String.format(r13, r5)
                android.text.SpannableStringBuilder r0 = android.text.SpannableStringBuilder.valueOf(r3)
                r5 = r0
                r6 = r32
                goto L_0x04e7
            L_0x04e0:
                android.text.SpannableStringBuilder r0 = android.text.SpannableStringBuilder.valueOf(r0)
                r5 = r0
                r6 = r32
            L_0x04e7:
                org.telegram.ui.Components.ForegroundColorSpanThemable r0 = new org.telegram.ui.Components.ForegroundColorSpanThemable     // Catch:{ Exception -> 0x04ff }
                java.lang.String r3 = "chats_nameMessage"
                r0.<init>(r3)     // Catch:{ Exception -> 0x04ff }
                int r3 = r12.length()     // Catch:{ Exception -> 0x04ff }
                r4 = 1
                int r3 = r3 + r4
                r4 = 33
                r27 = r6
                r6 = 0
                r5.setSpan(r0, r6, r3, r4)     // Catch:{ Exception -> 0x04fd }
                goto L_0x0505
            L_0x04fd:
                r0 = move-exception
                goto L_0x0502
            L_0x04ff:
                r0 = move-exception
                r27 = r6
            L_0x0502:
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            L_0x0505:
                r0 = r5
                r6 = r27
                goto L_0x0634
            L_0x050a:
                r28 = r3
                r29 = r4
                r31 = r5
                r32 = r6
                goto L_0x051d
            L_0x0513:
                r28 = r3
                r29 = r4
                r31 = r5
                r32 = r6
                r26 = r13
            L_0x051d:
                org.telegram.tgnet.TLRPC$Message r0 = r2.messageOwner
                org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
                boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPhoto
                if (r0 == 0) goto L_0x0544
                org.telegram.tgnet.TLRPC$Message r0 = r2.messageOwner
                org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
                org.telegram.tgnet.TLRPC$Photo r0 = r0.photo
                boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_photoEmpty
                if (r0 == 0) goto L_0x0544
                org.telegram.tgnet.TLRPC$Message r0 = r2.messageOwner
                org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
                int r0 = r0.ttl_seconds
                if (r0 == 0) goto L_0x0544
                r0 = 2131624422(0x7f0e01e6, float:1.8876023E38)
                java.lang.String r3 = "AttachPhotoExpired"
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r3, r0)
                r6 = r32
                goto L_0x0634
            L_0x0544:
                org.telegram.tgnet.TLRPC$Message r0 = r2.messageOwner
                org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
                boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaDocument
                if (r0 == 0) goto L_0x056b
                org.telegram.tgnet.TLRPC$Message r0 = r2.messageOwner
                org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
                org.telegram.tgnet.TLRPC$Document r0 = r0.document
                boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_documentEmpty
                if (r0 == 0) goto L_0x056b
                org.telegram.tgnet.TLRPC$Message r0 = r2.messageOwner
                org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
                int r0 = r0.ttl_seconds
                if (r0 == 0) goto L_0x056b
                r0 = 2131624428(0x7f0e01ec, float:1.8876035E38)
                java.lang.String r3 = "AttachVideoExpired"
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r3, r0)
                r6 = r32
                goto L_0x0634
            L_0x056b:
                java.lang.CharSequence r0 = r2.caption
                if (r0 == 0) goto L_0x05aa
                boolean r0 = r2.isVideo()
                if (r0 == 0) goto L_0x0578
                java.lang.String r0 = "📹 "
                goto L_0x0595
            L_0x0578:
                boolean r0 = r2.isVoice()
                if (r0 == 0) goto L_0x0581
                java.lang.String r0 = "🎤 "
                goto L_0x0595
            L_0x0581:
                boolean r0 = r2.isMusic()
                if (r0 == 0) goto L_0x058a
                java.lang.String r0 = "🎧 "
                goto L_0x0595
            L_0x058a:
                boolean r0 = r2.isPhoto()
                if (r0 == 0) goto L_0x0593
                java.lang.String r0 = "🖼 "
                goto L_0x0595
            L_0x0593:
                java.lang.String r0 = "📎 "
            L_0x0595:
                java.lang.StringBuilder r3 = new java.lang.StringBuilder
                r3.<init>()
                r3.append(r0)
                java.lang.CharSequence r4 = r2.caption
                r3.append(r4)
                java.lang.String r0 = r3.toString()
                r6 = r32
                goto L_0x0634
            L_0x05aa:
                org.telegram.tgnet.TLRPC$Message r0 = r2.messageOwner
                org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
                boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPoll
                if (r0 == 0) goto L_0x05ce
                org.telegram.tgnet.TLRPC$Message r0 = r2.messageOwner
                org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
                org.telegram.tgnet.TLRPC$TL_messageMediaPoll r0 = (org.telegram.tgnet.TLRPC.TL_messageMediaPoll) r0
                java.lang.StringBuilder r3 = new java.lang.StringBuilder
                r3.<init>()
                java.lang.String r4 = "📊 "
                r3.append(r4)
                org.telegram.tgnet.TLRPC$Poll r4 = r0.poll
                java.lang.String r4 = r4.question
                r3.append(r4)
                java.lang.String r0 = r3.toString()
                goto L_0x0616
            L_0x05ce:
                org.telegram.tgnet.TLRPC$Message r0 = r2.messageOwner
                org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
                boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGame
                if (r0 == 0) goto L_0x05f0
                java.lang.StringBuilder r0 = new java.lang.StringBuilder
                r0.<init>()
                java.lang.String r3 = "🎮 "
                r0.append(r3)
                org.telegram.tgnet.TLRPC$Message r3 = r2.messageOwner
                org.telegram.tgnet.TLRPC$MessageMedia r3 = r3.media
                org.telegram.tgnet.TLRPC$TL_game r3 = r3.game
                java.lang.String r3 = r3.title
                r0.append(r3)
                java.lang.String r0 = r0.toString()
                goto L_0x0616
            L_0x05f0:
                int r0 = r2.type
                r3 = 14
                if (r0 != r3) goto L_0x060e
                r3 = 2
                java.lang.Object[] r0 = new java.lang.Object[r3]
                java.lang.String r3 = r2.getMusicAuthor()
                r4 = 0
                r0[r4] = r3
                java.lang.String r3 = r2.getMusicTitle()
                r4 = 1
                r0[r4] = r3
                java.lang.String r3 = "🎧 %s - %s"
                java.lang.String r0 = java.lang.String.format(r3, r0)
                goto L_0x0616
            L_0x060e:
                java.lang.CharSequence r0 = r2.messageText
                java.util.ArrayList<java.lang.String> r3 = r2.highlightedWords
                r4 = 0
                org.telegram.messenger.AndroidUtilities.highlightText((java.lang.CharSequence) r0, (java.util.ArrayList<java.lang.String>) r3, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r4)
            L_0x0616:
                org.telegram.tgnet.TLRPC$Message r3 = r2.messageOwner
                org.telegram.tgnet.TLRPC$MessageMedia r3 = r3.media
                if (r3 == 0) goto L_0x0632
                boolean r3 = r2.isMediaEmpty()
                if (r3 != 0) goto L_0x0632
                android.content.Context r3 = r33.getContext()
                android.content.res.Resources r3 = r3.getResources()
                r4 = 2131034141(0x7var_d, float:1.7678791E38)
                int r6 = r3.getColor(r4)
                goto L_0x0634
            L_0x0632:
                r6 = r32
            L_0x0634:
                android.view.ViewGroup[] r3 = r1.cells
                r3 = r3[r26]
                r4 = 2131230909(0x7var_bd, float:1.8077884E38)
                android.view.View r3 = r3.findViewById(r4)
                android.widget.TextView r3 = (android.widget.TextView) r3
                org.telegram.tgnet.TLRPC$Message r4 = r2.messageOwner
                int r4 = r4.date
                long r4 = (long) r4
                java.lang.String r4 = org.telegram.messenger.LocaleController.stringForMessageListDate(r4)
                r3.setText(r4)
                android.view.ViewGroup[] r3 = r1.cells
                r3 = r3[r26]
                r4 = 2131230907(0x7var_bb, float:1.807788E38)
                android.view.View r3 = r3.findViewById(r4)
                android.widget.TextView r3 = (android.widget.TextView) r3
                java.lang.String r5 = r0.toString()
                r3.setText(r5)
                android.view.ViewGroup[] r3 = r1.cells
                r3 = r3[r26]
                android.view.View r3 = r3.findViewById(r4)
                android.widget.TextView r3 = (android.widget.TextView) r3
                r3.setTextColor(r6)
                goto L_0x06b3
            L_0x066f:
                r28 = r3
                r29 = r4
                r31 = r5
                r26 = r13
                int r3 = r14.last_message_date
                if (r3 == 0) goto L_0x0693
                android.view.ViewGroup[] r3 = r1.cells
                r3 = r3[r26]
                r4 = 2131230909(0x7var_bd, float:1.8077884E38)
                android.view.View r3 = r3.findViewById(r4)
                android.widget.TextView r3 = (android.widget.TextView) r3
                int r4 = r14.last_message_date
                long r4 = (long) r4
                java.lang.String r4 = org.telegram.messenger.LocaleController.stringForMessageListDate(r4)
                r3.setText(r4)
                goto L_0x06a3
            L_0x0693:
                r4 = 2131230909(0x7var_bd, float:1.8077884E38)
                android.view.ViewGroup[] r3 = r1.cells
                r3 = r3[r26]
                android.view.View r3 = r3.findViewById(r4)
                android.widget.TextView r3 = (android.widget.TextView) r3
                r3.setText(r0)
            L_0x06a3:
                android.view.ViewGroup[] r3 = r1.cells
                r3 = r3[r26]
                r4 = 2131230907(0x7var_bb, float:1.807788E38)
                android.view.View r3 = r3.findViewById(r4)
                android.widget.TextView r3 = (android.widget.TextView) r3
                r3.setText(r0)
            L_0x06b3:
                int r0 = r14.unread_count
                r3 = 2131230905(0x7var_b9, float:1.8077876E38)
                if (r0 <= 0) goto L_0x0710
                android.view.ViewGroup[] r0 = r1.cells
                r0 = r0[r26]
                android.view.View r0 = r0.findViewById(r3)
                android.widget.TextView r0 = (android.widget.TextView) r0
                r4 = 1
                java.lang.Object[] r5 = new java.lang.Object[r4]
                int r4 = r14.unread_count
                java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
                r6 = 0
                r5[r6] = r4
                java.lang.String r4 = "%d"
                java.lang.String r4 = java.lang.String.format(r4, r5)
                r0.setText(r4)
                android.view.ViewGroup[] r0 = r1.cells
                r0 = r0[r26]
                android.view.View r0 = r0.findViewById(r3)
                r0.setVisibility(r6)
                org.telegram.ui.EditWidgetActivity r0 = r1.this$0
                org.telegram.messenger.MessagesController r0 = r0.getMessagesController()
                long r4 = r14.id
                boolean r0 = r0.isDialogMuted(r4)
                if (r0 == 0) goto L_0x0701
                android.view.ViewGroup[] r0 = r1.cells
                r0 = r0[r26]
                android.view.View r0 = r0.findViewById(r3)
                r3 = 2131166207(0x7var_ff, float:1.7946653E38)
                r0.setBackgroundResource(r3)
                goto L_0x071d
            L_0x0701:
                android.view.ViewGroup[] r0 = r1.cells
                r0 = r0[r26]
                android.view.View r0 = r0.findViewById(r3)
                r3 = 2131166206(0x7var_fe, float:1.794665E38)
                r0.setBackgroundResource(r3)
                goto L_0x071d
            L_0x0710:
                android.view.ViewGroup[] r0 = r1.cells
                r0 = r0[r26]
                android.view.View r0 = r0.findViewById(r3)
                r3 = 8
                r0.setVisibility(r3)
            L_0x071d:
                int r13 = r26 + 1
                r4 = r19
                r2 = 1111490560(0x42400000, float:48.0)
                r3 = 2131627603(0x7f0e0e53, float:1.8882475E38)
                r7 = 0
                r9 = 8
                r10 = 2
                r11 = 1
                r12 = 0
                goto L_0x001a
            L_0x072f:
                r26 = r13
                android.view.ViewGroup[] r0 = r1.cells
                r2 = 0
                r0 = r0[r2]
                r2 = 2131230906(0x7var_ba, float:1.8077878E38)
                android.view.View r0 = r0.findViewById(r2)
                android.view.ViewGroup[] r3 = r1.cells
                r4 = 1
                r3 = r3[r4]
                int r3 = r3.getVisibility()
                r0.setVisibility(r3)
                android.view.ViewGroup[] r0 = r1.cells
                r0 = r0[r4]
                android.view.View r0 = r0.findViewById(r2)
                r2 = 8
                r0.setVisibility(r2)
                goto L_0x0aae
            L_0x0758:
                r19 = r4
                r4 = 1
                org.telegram.ui.EditWidgetActivity r0 = r1.this$0
                int r0 = r0.widgetType
                if (r0 != r4) goto L_0x0aae
                r0 = 0
                r2 = r0
            L_0x0765:
                r3 = 2
                if (r2 >= r3) goto L_0x0aae
                r0 = 0
                r4 = r0
            L_0x076a:
                if (r4 >= r3) goto L_0x0aa6
                int r0 = r2 * 2
                int r3 = r0 + r4
                org.telegram.ui.EditWidgetActivity r0 = r1.this$0
                java.util.ArrayList r0 = r0.selectedDialogs
                boolean r0 = r0.isEmpty()
                if (r0 == 0) goto L_0x07b9
                org.telegram.ui.EditWidgetActivity r0 = r1.this$0
                org.telegram.messenger.MediaDataController r0 = r0.getMediaDataController()
                java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_topPeer> r0 = r0.hints
                int r0 = r0.size()
                if (r3 >= r0) goto L_0x07b6
                org.telegram.ui.EditWidgetActivity r0 = r1.this$0
                org.telegram.messenger.MediaDataController r0 = r0.getMediaDataController()
                java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_topPeer> r0 = r0.hints
                java.lang.Object r0 = r0.get(r3)
                org.telegram.tgnet.TLRPC$TL_topPeer r0 = (org.telegram.tgnet.TLRPC.TL_topPeer) r0
                org.telegram.tgnet.TLRPC$Peer r0 = r0.peer
                long r5 = r0.user_id
                org.telegram.ui.EditWidgetActivity r0 = r1.this$0
                org.telegram.messenger.MessagesController r0 = r0.getMessagesController()
                androidx.collection.LongSparseArray<org.telegram.tgnet.TLRPC$Dialog> r0 = r0.dialogs_dict
                java.lang.Object r0 = r0.get(r5)
                org.telegram.tgnet.TLRPC$Dialog r0 = (org.telegram.tgnet.TLRPC.Dialog) r0
                if (r0 != 0) goto L_0x07b4
                org.telegram.tgnet.TLRPC$TL_dialog r7 = new org.telegram.tgnet.TLRPC$TL_dialog
                r7.<init>()
                r0 = r7
                r0.id = r5
            L_0x07b4:
                r5 = r0
                goto L_0x0803
            L_0x07b6:
                r0 = 0
                r5 = r0
                goto L_0x0803
            L_0x07b9:
                org.telegram.ui.EditWidgetActivity r0 = r1.this$0
                java.util.ArrayList r0 = r0.selectedDialogs
                int r0 = r0.size()
                if (r3 >= r0) goto L_0x0801
                org.telegram.ui.EditWidgetActivity r0 = r1.this$0
                org.telegram.messenger.MessagesController r0 = r0.getMessagesController()
                androidx.collection.LongSparseArray<org.telegram.tgnet.TLRPC$Dialog> r0 = r0.dialogs_dict
                org.telegram.ui.EditWidgetActivity r5 = r1.this$0
                java.util.ArrayList r5 = r5.selectedDialogs
                java.lang.Object r5 = r5.get(r3)
                java.lang.Long r5 = (java.lang.Long) r5
                long r5 = r5.longValue()
                java.lang.Object r0 = r0.get(r5)
                org.telegram.tgnet.TLRPC$Dialog r0 = (org.telegram.tgnet.TLRPC.Dialog) r0
                if (r0 != 0) goto L_0x07ff
                org.telegram.tgnet.TLRPC$TL_dialog r5 = new org.telegram.tgnet.TLRPC$TL_dialog
                r5.<init>()
                r0 = r5
                org.telegram.ui.EditWidgetActivity r5 = r1.this$0
                java.util.ArrayList r5 = r5.selectedDialogs
                java.lang.Object r5 = r5.get(r3)
                java.lang.Long r5 = (java.lang.Long) r5
                long r5 = r5.longValue()
                r0.id = r5
                r5 = r0
                goto L_0x0803
            L_0x07ff:
                r5 = r0
                goto L_0x0803
            L_0x0801:
                r0 = 0
                r5 = r0
            L_0x0803:
                r0 = 2131230789(0x7var_, float:1.807764E38)
                r6 = 2131230790(0x7var_, float:1.8077643E38)
                if (r5 != 0) goto L_0x0838
                android.view.ViewGroup[] r7 = r1.cells
                r7 = r7[r2]
                if (r4 != 0) goto L_0x0812
                goto L_0x0815
            L_0x0812:
                r0 = 2131230790(0x7var_, float:1.8077643E38)
            L_0x0815:
                android.view.View r0 = r7.findViewById(r0)
                r6 = 4
                r0.setVisibility(r6)
                if (r3 == 0) goto L_0x0829
                r6 = 2
                if (r3 != r6) goto L_0x0823
                goto L_0x0829
            L_0x0823:
                r10 = r19
                r3 = 0
                r6 = 1
                goto L_0x0a9f
            L_0x0829:
                android.view.ViewGroup[] r0 = r1.cells
                r0 = r0[r2]
                r6 = 8
                r0.setVisibility(r6)
                r10 = r19
                r3 = 0
                r6 = 1
                goto L_0x0a9f
            L_0x0838:
                android.view.ViewGroup[] r7 = r1.cells
                r7 = r7[r2]
                if (r4 != 0) goto L_0x083f
                goto L_0x0842
            L_0x083f:
                r0 = 2131230790(0x7var_, float:1.8077643E38)
            L_0x0842:
                android.view.View r0 = r7.findViewById(r0)
                r6 = 0
                r0.setVisibility(r6)
                if (r3 == 0) goto L_0x0850
                r6 = 2
                if (r3 != r6) goto L_0x0859
                goto L_0x0851
            L_0x0850:
                r6 = 2
            L_0x0851:
                android.view.ViewGroup[] r0 = r1.cells
                r0 = r0[r2]
                r7 = 0
                r0.setVisibility(r7)
            L_0x0859:
                r0 = 0
                r7 = 0
                r8 = 0
                long r9 = r5.id
                boolean r9 = org.telegram.messenger.DialogObject.isUserDialog(r9)
                if (r9 == 0) goto L_0x08e8
                org.telegram.ui.EditWidgetActivity r9 = r1.this$0
                org.telegram.messenger.MessagesController r9 = r9.getMessagesController()
                long r10 = r5.id
                java.lang.Long r10 = java.lang.Long.valueOf(r10)
                org.telegram.tgnet.TLRPC$User r7 = r9.getUser(r10)
                boolean r9 = org.telegram.messenger.UserObject.isUserSelf(r7)
                if (r9 == 0) goto L_0x0884
                r10 = r19
                r9 = 2131627603(0x7f0e0e53, float:1.8882475E38)
                java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r10, r9)
                goto L_0x08ad
            L_0x0884:
                r10 = r19
                r9 = 2131627603(0x7f0e0e53, float:1.8882475E38)
                boolean r11 = org.telegram.messenger.UserObject.isReplyUser((org.telegram.tgnet.TLRPC.User) r7)
                if (r11 == 0) goto L_0x0899
                r11 = 2131627470(0x7f0e0dce, float:1.8882205E38)
                java.lang.String r12 = "RepliesTitle"
                java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r12, r11)
                goto L_0x08ad
            L_0x0899:
                boolean r11 = org.telegram.messenger.UserObject.isDeleted(r7)
                if (r11 == 0) goto L_0x08a9
                r11 = 2131625886(0x7f0e079e, float:1.8878993E38)
                java.lang.String r12 = "HiddenName"
                java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r12, r11)
                goto L_0x08ad
            L_0x08a9:
                java.lang.String r11 = org.telegram.messenger.UserObject.getFirstName(r7)
            L_0x08ad:
                boolean r12 = org.telegram.messenger.UserObject.isReplyUser((org.telegram.tgnet.TLRPC.User) r7)
                if (r12 != 0) goto L_0x08e4
                boolean r12 = org.telegram.messenger.UserObject.isUserSelf(r7)
                if (r12 != 0) goto L_0x08e4
                if (r7 == 0) goto L_0x08e4
                org.telegram.tgnet.TLRPC$UserProfilePhoto r12 = r7.photo
                if (r12 == 0) goto L_0x08e4
                org.telegram.tgnet.TLRPC$UserProfilePhoto r12 = r7.photo
                org.telegram.tgnet.TLRPC$FileLocation r12 = r12.photo_small
                if (r12 == 0) goto L_0x08e4
                org.telegram.tgnet.TLRPC$UserProfilePhoto r12 = r7.photo
                org.telegram.tgnet.TLRPC$FileLocation r12 = r12.photo_small
                long r12 = r12.volume_id
                r14 = 0
                int r16 = (r12 > r14 ? 1 : (r12 == r14 ? 0 : -1))
                if (r16 == 0) goto L_0x08e4
                org.telegram.tgnet.TLRPC$UserProfilePhoto r12 = r7.photo
                org.telegram.tgnet.TLRPC$FileLocation r12 = r12.photo_small
                int r12 = r12.local_id
                if (r12 == 0) goto L_0x08e4
                org.telegram.tgnet.TLRPC$UserProfilePhoto r12 = r7.photo
                org.telegram.tgnet.TLRPC$FileLocation r0 = r12.photo_small
                r12 = r11
                r14 = 0
                r11 = r8
                r8 = r7
                r7 = r0
                goto L_0x092d
            L_0x08e4:
                r12 = r11
                r14 = 0
                goto L_0x092a
            L_0x08e8:
                r10 = r19
                r9 = 2131627603(0x7f0e0e53, float:1.8882475E38)
                org.telegram.ui.EditWidgetActivity r11 = r1.this$0
                org.telegram.messenger.MessagesController r11 = r11.getMessagesController()
                long r12 = r5.id
                long r12 = -r12
                java.lang.Long r12 = java.lang.Long.valueOf(r12)
                org.telegram.tgnet.TLRPC$Chat r8 = r11.getChat(r12)
                java.lang.String r11 = r8.title
                org.telegram.tgnet.TLRPC$ChatPhoto r12 = r8.photo
                if (r12 == 0) goto L_0x0927
                org.telegram.tgnet.TLRPC$ChatPhoto r12 = r8.photo
                org.telegram.tgnet.TLRPC$FileLocation r12 = r12.photo_small
                if (r12 == 0) goto L_0x0927
                org.telegram.tgnet.TLRPC$ChatPhoto r12 = r8.photo
                org.telegram.tgnet.TLRPC$FileLocation r12 = r12.photo_small
                long r12 = r12.volume_id
                r14 = 0
                int r16 = (r12 > r14 ? 1 : (r12 == r14 ? 0 : -1))
                if (r16 == 0) goto L_0x0929
                org.telegram.tgnet.TLRPC$ChatPhoto r12 = r8.photo
                org.telegram.tgnet.TLRPC$FileLocation r12 = r12.photo_small
                int r12 = r12.local_id
                if (r12 == 0) goto L_0x0929
                org.telegram.tgnet.TLRPC$ChatPhoto r12 = r8.photo
                org.telegram.tgnet.TLRPC$FileLocation r0 = r12.photo_small
                r12 = r11
                r11 = r8
                r8 = r7
                r7 = r0
                goto L_0x092d
            L_0x0927:
                r14 = 0
            L_0x0929:
                r12 = r11
            L_0x092a:
                r11 = r8
                r8 = r7
                r7 = r0
            L_0x092d:
                android.view.ViewGroup[] r0 = r1.cells
                r0 = r0[r2]
                if (r4 != 0) goto L_0x0937
                r13 = 2131230797(0x7var_d, float:1.8077657E38)
                goto L_0x093a
            L_0x0937:
                r13 = 2131230798(0x7var_e, float:1.8077659E38)
            L_0x093a:
                android.view.View r0 = r0.findViewById(r13)
                android.widget.TextView r0 = (android.widget.TextView) r0
                r0.setText(r12)
                r0 = 0
                if (r7 == 0) goto L_0x0962
                r13 = 1
                java.io.File r16 = org.telegram.messenger.FileLoader.getPathToAttach(r7, r13)     // Catch:{ all -> 0x0958 }
                r13 = r16
                java.lang.String r16 = r13.toString()     // Catch:{ all -> 0x0958 }
                android.graphics.Bitmap r16 = android.graphics.BitmapFactory.decodeFile(r16)     // Catch:{ all -> 0x0958 }
                r0 = r16
                goto L_0x0962
            L_0x0958:
                r0 = move-exception
                r18 = r3
                r25 = r7
                r26 = r8
                r3 = 0
                goto L_0x0a2a
            L_0x0962:
                r13 = 1111490560(0x42400000, float:48.0)
                int r16 = org.telegram.messenger.AndroidUtilities.dp(r13)     // Catch:{ all -> 0x0a22 }
                r18 = r16
                android.graphics.Bitmap$Config r6 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ all -> 0x0a22 }
                r9 = r18
                android.graphics.Bitmap r6 = android.graphics.Bitmap.createBitmap(r9, r9, r6)     // Catch:{ all -> 0x0a22 }
                r13 = 0
                r6.eraseColor(r13)     // Catch:{ all -> 0x0a22 }
                android.graphics.Canvas r13 = new android.graphics.Canvas     // Catch:{ all -> 0x0a22 }
                r13.<init>(r6)     // Catch:{ all -> 0x0a22 }
                if (r0 != 0) goto L_0x09ae
                if (r8 == 0) goto L_0x099b
                org.telegram.ui.Components.AvatarDrawable r14 = new org.telegram.ui.Components.AvatarDrawable     // Catch:{ all -> 0x0958 }
                r14.<init>((org.telegram.tgnet.TLRPC.User) r8)     // Catch:{ all -> 0x0958 }
                boolean r15 = org.telegram.messenger.UserObject.isReplyUser((org.telegram.tgnet.TLRPC.User) r8)     // Catch:{ all -> 0x0958 }
                if (r15 == 0) goto L_0x0990
                r15 = 12
                r14.setAvatarType(r15)     // Catch:{ all -> 0x0958 }
                goto L_0x09a0
            L_0x0990:
                boolean r15 = org.telegram.messenger.UserObject.isUserSelf(r8)     // Catch:{ all -> 0x0958 }
                if (r15 == 0) goto L_0x09a0
                r15 = 1
                r14.setAvatarType(r15)     // Catch:{ all -> 0x0958 }
                goto L_0x09a0
            L_0x099b:
                org.telegram.ui.Components.AvatarDrawable r14 = new org.telegram.ui.Components.AvatarDrawable     // Catch:{ all -> 0x0958 }
                r14.<init>((org.telegram.tgnet.TLRPC.Chat) r11)     // Catch:{ all -> 0x0958 }
            L_0x09a0:
                r15 = 0
                r14.setBounds(r15, r15, r9, r9)     // Catch:{ all -> 0x0958 }
                r14.draw(r13)     // Catch:{ all -> 0x0958 }
                r18 = r3
                r25 = r7
                r26 = r8
                goto L_0x09f6
            L_0x09ae:
                android.graphics.BitmapShader r14 = new android.graphics.BitmapShader     // Catch:{ all -> 0x0a22 }
                android.graphics.Shader$TileMode r15 = android.graphics.Shader.TileMode.CLAMP     // Catch:{ all -> 0x0a22 }
                r18 = r3
                android.graphics.Shader$TileMode r3 = android.graphics.Shader.TileMode.CLAMP     // Catch:{ all -> 0x0a1b }
                r14.<init>(r0, r15, r3)     // Catch:{ all -> 0x0a1b }
                r3 = r14
                float r14 = (float) r9     // Catch:{ all -> 0x0a1b }
                int r15 = r0.getWidth()     // Catch:{ all -> 0x0a1b }
                float r15 = (float) r15     // Catch:{ all -> 0x0a1b }
                float r14 = r14 / r15
                r13.save()     // Catch:{ all -> 0x0a1b }
                r13.scale(r14, r14)     // Catch:{ all -> 0x0a1b }
                android.graphics.Paint r15 = r1.roundPaint     // Catch:{ all -> 0x0a1b }
                r15.setShader(r3)     // Catch:{ all -> 0x0a1b }
                android.graphics.RectF r15 = r1.bitmapRect     // Catch:{ all -> 0x0a1b }
                r24 = r3
                int r3 = r0.getWidth()     // Catch:{ all -> 0x0a1b }
                float r3 = (float) r3
                r25 = r7
                int r7 = r0.getHeight()     // Catch:{ all -> 0x0a16 }
                float r7 = (float) r7
                r26 = r8
                r8 = 0
                r15.set(r8, r8, r3, r7)     // Catch:{ all -> 0x0a13 }
                android.graphics.RectF r3 = r1.bitmapRect     // Catch:{ all -> 0x0a13 }
                int r7 = r0.getWidth()     // Catch:{ all -> 0x0a13 }
                float r7 = (float) r7     // Catch:{ all -> 0x0a13 }
                int r15 = r0.getHeight()     // Catch:{ all -> 0x0a13 }
                float r15 = (float) r15     // Catch:{ all -> 0x0a13 }
                android.graphics.Paint r8 = r1.roundPaint     // Catch:{ all -> 0x0a13 }
                r13.drawRoundRect(r3, r7, r15, r8)     // Catch:{ all -> 0x0a13 }
                r13.restore()     // Catch:{ all -> 0x0a13 }
            L_0x09f6:
                r3 = 0
                r13.setBitmap(r3)     // Catch:{ all -> 0x0a11 }
                android.view.ViewGroup[] r7 = r1.cells     // Catch:{ all -> 0x0a11 }
                r7 = r7[r2]     // Catch:{ all -> 0x0a11 }
                if (r4 != 0) goto L_0x0a04
                r8 = 2131230791(0x7var_, float:1.8077645E38)
                goto L_0x0a07
            L_0x0a04:
                r8 = 2131230792(0x7var_, float:1.8077647E38)
            L_0x0a07:
                android.view.View r7 = r7.findViewById(r8)     // Catch:{ all -> 0x0a11 }
                android.widget.ImageView r7 = (android.widget.ImageView) r7     // Catch:{ all -> 0x0a11 }
                r7.setImageBitmap(r6)     // Catch:{ all -> 0x0a11 }
                goto L_0x0a2d
            L_0x0a11:
                r0 = move-exception
                goto L_0x0a2a
            L_0x0a13:
                r0 = move-exception
                r3 = 0
                goto L_0x0a2a
            L_0x0a16:
                r0 = move-exception
                r26 = r8
                r3 = 0
                goto L_0x0a2a
            L_0x0a1b:
                r0 = move-exception
                r25 = r7
                r26 = r8
                r3 = 0
                goto L_0x0a2a
            L_0x0a22:
                r0 = move-exception
                r18 = r3
                r25 = r7
                r26 = r8
                r3 = 0
            L_0x0a2a:
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            L_0x0a2d:
                int r0 = r5.unread_count
                if (r0 <= 0) goto L_0x0a88
                int r0 = r5.unread_count
                r6 = 99
                if (r0 <= r6) goto L_0x0a4a
                r6 = 1
                java.lang.Object[] r0 = new java.lang.Object[r6]
                r7 = 99
                java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
                r8 = 0
                r0[r8] = r7
                java.lang.String r7 = "%d+"
                java.lang.String r0 = java.lang.String.format(r7, r0)
                goto L_0x0a5c
            L_0x0a4a:
                r6 = 1
                r8 = 0
                java.lang.Object[] r0 = new java.lang.Object[r6]
                int r7 = r5.unread_count
                java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
                r0[r8] = r7
                java.lang.String r7 = "%d"
                java.lang.String r0 = java.lang.String.format(r7, r0)
            L_0x0a5c:
                android.view.ViewGroup[] r7 = r1.cells
                r7 = r7[r2]
                if (r4 != 0) goto L_0x0a66
                r8 = 2131230793(0x7var_, float:1.8077649E38)
                goto L_0x0a69
            L_0x0a66:
                r8 = 2131230794(0x7var_a, float:1.807765E38)
            L_0x0a69:
                android.view.View r7 = r7.findViewById(r8)
                android.widget.TextView r7 = (android.widget.TextView) r7
                r7.setText(r0)
                android.view.ViewGroup[] r7 = r1.cells
                r7 = r7[r2]
                if (r4 != 0) goto L_0x0a7c
                r8 = 2131230795(0x7var_b, float:1.8077653E38)
                goto L_0x0a7f
            L_0x0a7c:
                r8 = 2131230796(0x7var_c, float:1.8077655E38)
            L_0x0a7f:
                android.view.View r7 = r7.findViewById(r8)
                r8 = 0
                r7.setVisibility(r8)
                goto L_0x0a9f
            L_0x0a88:
                r6 = 1
                android.view.ViewGroup[] r0 = r1.cells
                r0 = r0[r2]
                if (r4 != 0) goto L_0x0a93
                r7 = 2131230795(0x7var_b, float:1.8077653E38)
                goto L_0x0a96
            L_0x0a93:
                r7 = 2131230796(0x7var_c, float:1.8077655E38)
            L_0x0a96:
                android.view.View r0 = r0.findViewById(r7)
                r7 = 8
                r0.setVisibility(r7)
            L_0x0a9f:
                int r4 = r4 + 1
                r19 = r10
                r3 = 2
                goto L_0x076a
            L_0x0aa6:
                r10 = r19
                r3 = 0
                r6 = 1
                int r2 = r2 + 1
                goto L_0x0765
            L_0x0aae:
                android.view.ViewGroup[] r0 = r1.cells
                r2 = 0
                r0 = r0[r2]
                int r0 = r0.getVisibility()
                if (r0 != 0) goto L_0x0ac5
                org.telegram.ui.EditWidgetActivity r0 = r1.this$0
                android.widget.ImageView r0 = r0.previewImageView
                r2 = 8
                r0.setVisibility(r2)
                goto L_0x0ace
            L_0x0ac5:
                org.telegram.ui.EditWidgetActivity r0 = r1.this$0
                android.widget.ImageView r0 = r0.previewImageView
                r0.setVisibility(r2)
            L_0x0ace:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.EditWidgetActivity.WidgetPreviewCell.updateDialogs():void");
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(264.0f), NUM));
        }

        /* access modifiers changed from: protected */
        /* JADX WARNING: Removed duplicated region for block: B:51:0x012e  */
        /* JADX WARNING: Removed duplicated region for block: B:56:0x0140  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onDraw(android.graphics.Canvas r18) {
            /*
                r17 = this;
                r0 = r17
                r1 = r18
                android.graphics.drawable.Drawable r2 = org.telegram.ui.ActionBar.Theme.getCachedWallpaperNonBlocking()
                android.graphics.drawable.Drawable r3 = r0.backgroundDrawable
                r4 = 0
                if (r2 == r3) goto L_0x0029
                if (r2 == 0) goto L_0x0029
                boolean r3 = org.telegram.ui.ActionBar.Theme.isAnimatingColor()
                if (r3 == 0) goto L_0x001e
                android.graphics.drawable.Drawable r3 = r0.backgroundDrawable
                r0.oldBackgroundDrawable = r3
                org.telegram.ui.Components.BackgroundGradientDrawable$Disposable r3 = r0.backgroundGradientDisposable
                r0.oldBackgroundGradientDisposable = r3
                goto L_0x0027
            L_0x001e:
                org.telegram.ui.Components.BackgroundGradientDrawable$Disposable r3 = r0.backgroundGradientDisposable
                if (r3 == 0) goto L_0x0027
                r3.dispose()
                r0.backgroundGradientDisposable = r4
            L_0x0027:
                r0.backgroundDrawable = r2
            L_0x0029:
                org.telegram.ui.EditWidgetActivity r3 = r0.this$0
                org.telegram.ui.ActionBar.ActionBarLayout r3 = r3.parentLayout
                float r3 = r3.getThemeAnimationValue()
                r5 = 0
            L_0x0034:
                r6 = 2
                r7 = 0
                if (r5 >= r6) goto L_0x0145
                if (r5 != 0) goto L_0x003d
                android.graphics.drawable.Drawable r8 = r0.oldBackgroundDrawable
                goto L_0x003f
            L_0x003d:
                android.graphics.drawable.Drawable r8 = r0.backgroundDrawable
            L_0x003f:
                if (r8 != 0) goto L_0x0045
                r16 = r5
                goto L_0x0141
            L_0x0045:
                r9 = 1
                if (r5 != r9) goto L_0x005d
                android.graphics.drawable.Drawable r9 = r0.oldBackgroundDrawable
                if (r9 == 0) goto L_0x005d
                org.telegram.ui.EditWidgetActivity r9 = r0.this$0
                org.telegram.ui.ActionBar.ActionBarLayout r9 = r9.parentLayout
                if (r9 == 0) goto L_0x005d
                r9 = 1132396544(0x437var_, float:255.0)
                float r9 = r9 * r3
                int r9 = (int) r9
                r8.setAlpha(r9)
                goto L_0x0062
            L_0x005d:
                r9 = 255(0xff, float:3.57E-43)
                r8.setAlpha(r9)
            L_0x0062:
                boolean r9 = r8 instanceof android.graphics.drawable.ColorDrawable
                if (r9 != 0) goto L_0x0104
                boolean r9 = r8 instanceof android.graphics.drawable.GradientDrawable
                if (r9 != 0) goto L_0x0104
                boolean r9 = r8 instanceof org.telegram.ui.Components.MotionBackgroundDrawable
                if (r9 == 0) goto L_0x0072
                r16 = r5
                goto L_0x0106
            L_0x0072:
                boolean r9 = r8 instanceof android.graphics.drawable.BitmapDrawable
                if (r9 == 0) goto L_0x0101
                r9 = r8
                android.graphics.drawable.BitmapDrawable r9 = (android.graphics.drawable.BitmapDrawable) r9
                android.graphics.Shader$TileMode r10 = r9.getTileModeX()
                android.graphics.Shader$TileMode r11 = android.graphics.Shader.TileMode.REPEAT
                if (r10 != r11) goto L_0x00aa
                r18.save()
                r6 = 1073741824(0x40000000, float:2.0)
                float r10 = org.telegram.messenger.AndroidUtilities.density
                float r6 = r6 / r10
                r1.scale(r6, r6)
                int r10 = r17.getMeasuredWidth()
                float r10 = (float) r10
                float r10 = r10 / r6
                double r10 = (double) r10
                double r10 = java.lang.Math.ceil(r10)
                int r10 = (int) r10
                int r11 = r17.getMeasuredHeight()
                float r11 = (float) r11
                float r11 = r11 / r6
                double r11 = (double) r11
                double r11 = java.lang.Math.ceil(r11)
                int r11 = (int) r11
                r8.setBounds(r7, r7, r10, r11)
                r16 = r5
                goto L_0x00fa
            L_0x00aa:
                int r10 = r17.getMeasuredHeight()
                int r11 = r17.getMeasuredWidth()
                float r11 = (float) r11
                int r12 = r8.getIntrinsicWidth()
                float r12 = (float) r12
                float r11 = r11 / r12
                float r12 = (float) r10
                int r13 = r8.getIntrinsicHeight()
                float r13 = (float) r13
                float r12 = r12 / r13
                float r13 = java.lang.Math.max(r11, r12)
                int r14 = r8.getIntrinsicWidth()
                float r14 = (float) r14
                float r14 = r14 * r13
                double r14 = (double) r14
                double r14 = java.lang.Math.ceil(r14)
                int r14 = (int) r14
                int r15 = r8.getIntrinsicHeight()
                float r15 = (float) r15
                float r15 = r15 * r13
                r16 = r5
                double r4 = (double) r15
                double r4 = java.lang.Math.ceil(r4)
                int r4 = (int) r4
                int r5 = r17.getMeasuredWidth()
                int r5 = r5 - r14
                int r5 = r5 / r6
                int r15 = r10 - r4
                int r15 = r15 / r6
                r18.save()
                int r6 = r17.getMeasuredHeight()
                r1.clipRect(r7, r7, r14, r6)
                int r6 = r5 + r14
                int r7 = r15 + r4
                r8.setBounds(r5, r15, r6, r7)
            L_0x00fa:
                r8.draw(r1)
                r18.restore()
                goto L_0x0122
            L_0x0101:
                r16 = r5
                goto L_0x0122
            L_0x0104:
                r16 = r5
            L_0x0106:
                int r4 = r17.getMeasuredWidth()
                int r5 = r17.getMeasuredHeight()
                r8.setBounds(r7, r7, r4, r5)
                boolean r4 = r8 instanceof org.telegram.ui.Components.BackgroundGradientDrawable
                if (r4 == 0) goto L_0x011f
                r4 = r8
                org.telegram.ui.Components.BackgroundGradientDrawable r4 = (org.telegram.ui.Components.BackgroundGradientDrawable) r4
                org.telegram.ui.Components.BackgroundGradientDrawable$Disposable r5 = r4.drawExactBoundsSize(r1, r0)
                r0.backgroundGradientDisposable = r5
                goto L_0x0122
            L_0x011f:
                r8.draw(r1)
            L_0x0122:
                if (r16 != 0) goto L_0x0140
                android.graphics.drawable.Drawable r4 = r0.oldBackgroundDrawable
                if (r4 == 0) goto L_0x0140
                r4 = 1065353216(0x3var_, float:1.0)
                int r4 = (r3 > r4 ? 1 : (r3 == r4 ? 0 : -1))
                if (r4 < 0) goto L_0x0140
                org.telegram.ui.Components.BackgroundGradientDrawable$Disposable r4 = r0.oldBackgroundGradientDisposable
                if (r4 == 0) goto L_0x0139
                r4.dispose()
                r4 = 0
                r0.oldBackgroundGradientDisposable = r4
                goto L_0x013a
            L_0x0139:
                r4 = 0
            L_0x013a:
                r0.oldBackgroundDrawable = r4
                r17.invalidate()
                goto L_0x0141
            L_0x0140:
                r4 = 0
            L_0x0141:
                int r5 = r16 + 1
                goto L_0x0034
            L_0x0145:
                r16 = r5
                android.graphics.drawable.Drawable r4 = r0.shadowDrawable
                int r5 = r17.getMeasuredWidth()
                int r6 = r17.getMeasuredHeight()
                r4.setBounds(r7, r7, r5, r6)
                android.graphics.drawable.Drawable r4 = r0.shadowDrawable
                r4.draw(r1)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.EditWidgetActivity.WidgetPreviewCell.onDraw(android.graphics.Canvas):void");
        }

        /* access modifiers changed from: protected */
        public void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            BackgroundGradientDrawable.Disposable disposable = this.backgroundGradientDisposable;
            if (disposable != null) {
                disposable.dispose();
                this.backgroundGradientDisposable = null;
            }
            BackgroundGradientDrawable.Disposable disposable2 = this.oldBackgroundGradientDisposable;
            if (disposable2 != null) {
                disposable2.dispose();
                this.oldBackgroundGradientDisposable = null;
            }
        }

        public boolean onInterceptTouchEvent(MotionEvent ev) {
            return false;
        }

        public boolean dispatchTouchEvent(MotionEvent ev) {
            return false;
        }

        /* access modifiers changed from: protected */
        public void dispatchSetPressed(boolean pressed) {
        }

        public boolean onTouchEvent(MotionEvent event) {
            return false;
        }
    }

    public EditWidgetActivity(int type, int widgetId) {
        this.widgetType = type;
        this.currentWidgetId = widgetId;
        ArrayList<TLRPC.User> users = new ArrayList<>();
        ArrayList<TLRPC.Chat> chats = new ArrayList<>();
        getMessagesStorage().getWidgetDialogIds(this.currentWidgetId, this.widgetType, this.selectedDialogs, users, chats, true);
        getMessagesController().putUsers(users, true);
        getMessagesController().putChats(chats, true);
        updateRows();
    }

    public boolean onFragmentCreate() {
        DialogsActivity.loadDialogs(AccountInstance.getInstance(this.currentAccount));
        getMediaDataController().loadHints(true);
        return super.onFragmentCreate();
    }

    /* access modifiers changed from: private */
    public void updateRows() {
        this.rowCount = 0;
        int i = 0 + 1;
        this.rowCount = i;
        this.previewRow = 0;
        this.rowCount = i + 1;
        this.selectChatsRow = i;
        if (this.selectedDialogs.isEmpty()) {
            this.chatsStartRow = -1;
            this.chatsEndRow = -1;
        } else {
            int i2 = this.rowCount;
            this.chatsStartRow = i2;
            int size = i2 + this.selectedDialogs.size();
            this.rowCount = size;
            this.chatsEndRow = size;
        }
        int i3 = this.rowCount;
        this.rowCount = i3 + 1;
        this.infoRow = i3;
        ListAdapter listAdapter2 = this.listAdapter;
        if (listAdapter2 != null) {
            listAdapter2.notifyDataSetChanged();
        }
    }

    public void setDelegate(EditWidgetActivityDelegate editWidgetActivityDelegate) {
        this.delegate = editWidgetActivityDelegate;
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(NUM);
        this.actionBar.setAllowOverlayTitle(false);
        if (AndroidUtilities.isTablet()) {
            this.actionBar.setOccupyStatusBar(false);
        }
        if (this.widgetType == 0) {
            this.actionBar.setTitle(LocaleController.getString("WidgetChats", NUM));
        } else {
            this.actionBar.setTitle(LocaleController.getString("WidgetShortcuts", NUM));
        }
        this.actionBar.createMenu().addItem(1, (CharSequence) LocaleController.getString("Done", NUM).toUpperCase());
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    if (EditWidgetActivity.this.delegate == null) {
                        EditWidgetActivity.this.finishActivity();
                    } else {
                        EditWidgetActivity.this.finishFragment();
                    }
                } else if (id == 1 && EditWidgetActivity.this.getParentActivity() != null) {
                    EditWidgetActivity.this.getMessagesStorage().putWidgetDialogs(EditWidgetActivity.this.currentWidgetId, EditWidgetActivity.this.selectedDialogs);
                    SharedPreferences.Editor editor = EditWidgetActivity.this.getParentActivity().getSharedPreferences("shortcut_widget", 0).edit();
                    editor.putInt("account" + EditWidgetActivity.this.currentWidgetId, EditWidgetActivity.this.currentAccount);
                    editor.putInt("type" + EditWidgetActivity.this.currentWidgetId, EditWidgetActivity.this.widgetType);
                    editor.commit();
                    AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(EditWidgetActivity.this.getParentActivity());
                    if (EditWidgetActivity.this.widgetType == 0) {
                        ChatsWidgetProvider.updateWidget(EditWidgetActivity.this.getParentActivity(), appWidgetManager, EditWidgetActivity.this.currentWidgetId);
                    } else {
                        ContactsWidgetProvider.updateWidget(EditWidgetActivity.this.getParentActivity(), appWidgetManager, EditWidgetActivity.this.currentWidgetId);
                    }
                    if (EditWidgetActivity.this.delegate != null) {
                        EditWidgetActivity.this.delegate.didSelectDialogs(EditWidgetActivity.this.selectedDialogs);
                    } else {
                        EditWidgetActivity.this.finishActivity();
                    }
                }
            }
        });
        this.listAdapter = new ListAdapter(context);
        FrameLayout frameLayout = new FrameLayout(context);
        frameLayout.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
        this.fragmentView = frameLayout;
        RecyclerListView recyclerListView = new RecyclerListView(context);
        this.listView = recyclerListView;
        recyclerListView.setLayoutManager(new LinearLayoutManager(context, 1, false));
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setAdapter(this.listAdapter);
        ((DefaultItemAnimator) this.listView.getItemAnimator()).setDelayAnimations(false);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        ItemTouchHelper itemTouchHelper2 = new ItemTouchHelper(new TouchHelperCallback());
        this.itemTouchHelper = itemTouchHelper2;
        itemTouchHelper2.attachToRecyclerView(this.listView);
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new EditWidgetActivity$$ExternalSyntheticLambda2(this, context));
        this.listView.setOnItemLongClickListener((RecyclerListView.OnItemLongClickListenerExtended) new RecyclerListView.OnItemLongClickListenerExtended() {
            private Rect rect = new Rect();

            public boolean onItemClick(View view, int position, float x, float y) {
                if (EditWidgetActivity.this.getParentActivity() == null || !(view instanceof GroupCreateUserCell)) {
                    return false;
                }
                ((ImageView) view.getTag(NUM)).getHitRect(this.rect);
                if (this.rect.contains((int) x, (int) y)) {
                    return false;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder((Context) EditWidgetActivity.this.getParentActivity());
                builder.setItems(new CharSequence[]{LocaleController.getString("Delete", NUM)}, new EditWidgetActivity$2$$ExternalSyntheticLambda0(this, position));
                EditWidgetActivity.this.showDialog(builder.create());
                return true;
            }

            /* renamed from: lambda$onItemClick$0$org-telegram-ui-EditWidgetActivity$2  reason: not valid java name */
            public /* synthetic */ void m2891lambda$onItemClick$0$orgtelegramuiEditWidgetActivity$2(int position, DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    EditWidgetActivity.this.selectedDialogs.remove(position - EditWidgetActivity.this.chatsStartRow);
                    EditWidgetActivity.this.updateRows();
                    if (EditWidgetActivity.this.widgetPreviewCell != null) {
                        EditWidgetActivity.this.widgetPreviewCell.updateDialogs();
                    }
                }
            }

            public void onMove(float dx, float dy) {
            }

            public void onLongClickRelease() {
            }
        });
        return this.fragmentView;
    }

    /* renamed from: lambda$createView$1$org-telegram-ui-EditWidgetActivity  reason: not valid java name */
    public /* synthetic */ void m2890lambda$createView$1$orgtelegramuiEditWidgetActivity(Context context, View view, int position) {
        if (position == this.selectChatsRow) {
            InviteMembersBottomSheet inviteMembersBottomSheet = new InviteMembersBottomSheet(context, this.currentAccount, (LongSparseArray<TLObject>) null, 0, this, (Theme.ResourcesProvider) null);
            inviteMembersBottomSheet.setDelegate(new EditWidgetActivity$$ExternalSyntheticLambda1(this), this.selectedDialogs);
            inviteMembersBottomSheet.setSelectedContacts(this.selectedDialogs);
            showDialog(inviteMembersBottomSheet);
        }
    }

    /* renamed from: lambda$createView$0$org-telegram-ui-EditWidgetActivity  reason: not valid java name */
    public /* synthetic */ void m2889lambda$createView$0$orgtelegramuiEditWidgetActivity(ArrayList dids) {
        this.selectedDialogs.clear();
        this.selectedDialogs.addAll(dids);
        updateRows();
        WidgetPreviewCell widgetPreviewCell2 = this.widgetPreviewCell;
        if (widgetPreviewCell2 != null) {
            widgetPreviewCell2.updateDialogs();
        }
    }

    /* access modifiers changed from: private */
    public void finishActivity() {
        if (getParentActivity() != null) {
            getParentActivity().finish();
            AndroidUtilities.runOnUIThread(new EditWidgetActivity$$ExternalSyntheticLambda0(this), 1000);
        }
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public int getItemCount() {
            return EditWidgetActivity.this.rowCount;
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int type = holder.getItemViewType();
            return type == 1 || type == 3;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            GroupCreateUserCell cell;
            int i;
            switch (viewType) {
                case 0:
                    TextInfoPrivacyCell textInfoPrivacyCell = new TextInfoPrivacyCell(this.mContext);
                    textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                    cell = textInfoPrivacyCell;
                    break;
                case 1:
                    TextCell textCell = new TextCell(this.mContext);
                    textCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    cell = textCell;
                    break;
                case 2:
                    cell = EditWidgetActivity.this.widgetPreviewCell = new WidgetPreviewCell(EditWidgetActivity.this, this.mContext);
                    break;
                default:
                    GroupCreateUserCell cell2 = new GroupCreateUserCell(this.mContext, 0, 0, false);
                    ImageView sortImageView = new ImageView(this.mContext);
                    sortImageView.setImageResource(NUM);
                    sortImageView.setScaleType(ImageView.ScaleType.CENTER);
                    cell2.setTag(NUM, sortImageView);
                    if (LocaleController.isRTL) {
                        i = 3;
                    } else {
                        i = 5;
                    }
                    cell2.addView(sortImageView, LayoutHelper.createFrame(40, -1.0f, i | 16, 10.0f, 0.0f, 10.0f, 0.0f));
                    sortImageView.setOnTouchListener(new EditWidgetActivity$ListAdapter$$ExternalSyntheticLambda0(this, cell2));
                    sortImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chats_pinnedIcon"), PorterDuff.Mode.MULTIPLY));
                    GroupCreateUserCell groupCreateUserCell = cell2;
                    cell = cell2;
                    break;
            }
            return new RecyclerListView.Holder(cell);
        }

        /* renamed from: lambda$onCreateViewHolder$0$org-telegram-ui-EditWidgetActivity$ListAdapter  reason: not valid java name */
        public /* synthetic */ boolean m2892x37efab4f(GroupCreateUserCell cell, View v, MotionEvent event) {
            if (event.getAction() != 0) {
                return false;
            }
            EditWidgetActivity.this.itemTouchHelper.startDrag(EditWidgetActivity.this.listView.getChildViewHolder(cell));
            return false;
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            boolean z = true;
            switch (holder.getItemViewType()) {
                case 0:
                    TextInfoPrivacyCell cell = (TextInfoPrivacyCell) holder.itemView;
                    if (position == EditWidgetActivity.this.infoRow) {
                        SpannableStringBuilder builder = new SpannableStringBuilder();
                        if (EditWidgetActivity.this.widgetType == 0) {
                            builder.append(LocaleController.getString("EditWidgetChatsInfo", NUM));
                        } else if (EditWidgetActivity.this.widgetType == 1) {
                            builder.append(LocaleController.getString("EditWidgetContactsInfo", NUM));
                        }
                        if (SharedConfig.passcodeHash.length() > 0) {
                            builder.append("\n\n").append(AndroidUtilities.replaceTags(LocaleController.getString("WidgetPasscode2", NUM)));
                        }
                        cell.setText(builder);
                        return;
                    }
                    return;
                case 1:
                    TextCell cell2 = (TextCell) holder.itemView;
                    cell2.setColors((String) null, "windowBackgroundWhiteBlueText4");
                    Drawable drawable1 = this.mContext.getResources().getDrawable(NUM);
                    Drawable drawable2 = this.mContext.getResources().getDrawable(NUM);
                    drawable1.setColorFilter(new PorterDuffColorFilter(Theme.getColor("switchTrackChecked"), PorterDuff.Mode.MULTIPLY));
                    drawable2.setColorFilter(new PorterDuffColorFilter(Theme.getColor("checkboxCheck"), PorterDuff.Mode.MULTIPLY));
                    CombinedDrawable combinedDrawable = new CombinedDrawable(drawable1, drawable2);
                    String string = LocaleController.getString("SelectChats", NUM);
                    if (EditWidgetActivity.this.chatsStartRow == -1) {
                        z = false;
                    }
                    cell2.setTextAndIcon(string, (Drawable) combinedDrawable, z);
                    cell2.getImageView().setPadding(0, AndroidUtilities.dp(7.0f), 0, 0);
                    return;
                case 3:
                    GroupCreateUserCell cell3 = (GroupCreateUserCell) holder.itemView;
                    long did = ((Long) EditWidgetActivity.this.selectedDialogs.get(position - EditWidgetActivity.this.chatsStartRow)).longValue();
                    if (DialogObject.isUserDialog(did)) {
                        TLRPC.User user = EditWidgetActivity.this.getMessagesController().getUser(Long.valueOf(did));
                        if (position == EditWidgetActivity.this.chatsEndRow - 1) {
                            z = false;
                        }
                        cell3.setObject(user, (CharSequence) null, (CharSequence) null, z);
                        return;
                    }
                    TLRPC.Chat chat = EditWidgetActivity.this.getMessagesController().getChat(Long.valueOf(-did));
                    if (position == EditWidgetActivity.this.chatsEndRow - 1) {
                        z = false;
                    }
                    cell3.setObject(chat, (CharSequence) null, (CharSequence) null, z);
                    return;
                default:
                    return;
            }
        }

        public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
            int type = holder.getItemViewType();
            if (type == 3 || type == 1) {
                holder.itemView.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            }
        }

        public int getItemViewType(int position) {
            if (position == EditWidgetActivity.this.previewRow) {
                return 2;
            }
            if (position == EditWidgetActivity.this.selectChatsRow) {
                return 1;
            }
            if (position == EditWidgetActivity.this.infoRow) {
                return 0;
            }
            return 3;
        }

        public boolean swapElements(int fromIndex, int toIndex) {
            int idx1 = fromIndex - EditWidgetActivity.this.chatsStartRow;
            int idx2 = toIndex - EditWidgetActivity.this.chatsStartRow;
            int count = EditWidgetActivity.this.chatsEndRow - EditWidgetActivity.this.chatsStartRow;
            if (idx1 < 0 || idx2 < 0 || idx1 >= count || idx2 >= count) {
                return false;
            }
            EditWidgetActivity.this.selectedDialogs.set(idx1, (Long) EditWidgetActivity.this.selectedDialogs.get(idx2));
            EditWidgetActivity.this.selectedDialogs.set(idx2, (Long) EditWidgetActivity.this.selectedDialogs.get(idx1));
            notifyItemMoved(fromIndex, toIndex);
            return true;
        }
    }

    public boolean isSwipeBackEnabled(MotionEvent event) {
        return false;
    }

    public boolean onBackPressed() {
        if (this.delegate != null) {
            return super.onBackPressed();
        }
        finishActivity();
        return false;
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = new ArrayList<>();
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        themeDescriptions.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultIcon"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultTitle"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUBACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSubmenuBackground"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSubmenuItem"));
        themeDescriptions.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM | ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSubmenuItemIcon"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        themeDescriptions.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "divider"));
        themeDescriptions.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText4"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueText4"));
        themeDescriptions.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueText4"));
        return themeDescriptions;
    }
}
