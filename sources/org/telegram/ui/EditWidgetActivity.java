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
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.text.SpannableStringBuilder;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatsWidgetProvider;
import org.telegram.messenger.ContactsWidgetProvider;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.SharedConfig;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$User;
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
import org.telegram.ui.Components.MotionBackgroundDrawable;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.EditWidgetActivity;

public class EditWidgetActivity extends BaseFragment {
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
    public boolean isEdit;
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
    /* access modifiers changed from: private */
    public WidgetPreviewCell widgetPreviewCell;
    /* access modifiers changed from: private */
    public int widgetType;

    public interface EditWidgetActivityDelegate {
        void didSelectDialogs(ArrayList<Long> arrayList);
    }

    public boolean isSwipeBackEnabled(MotionEvent motionEvent) {
        return false;
    }

    public class TouchHelperCallback extends ItemTouchHelper.Callback {
        private boolean moved;

        public boolean isLongPressDragEnabled() {
            return false;
        }

        public void onSwiped(RecyclerView.ViewHolder viewHolder, int i) {
        }

        public TouchHelperCallback() {
        }

        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            if (viewHolder.getItemViewType() != 3) {
                return ItemTouchHelper.Callback.makeMovementFlags(0, 0);
            }
            return ItemTouchHelper.Callback.makeMovementFlags(3, 0);
        }

        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder viewHolder2) {
            boolean z = false;
            if (viewHolder.getItemViewType() != viewHolder2.getItemViewType()) {
                return false;
            }
            int adapterPosition = viewHolder.getAdapterPosition();
            int adapterPosition2 = viewHolder2.getAdapterPosition();
            if (EditWidgetActivity.this.listAdapter.swapElements(adapterPosition, adapterPosition2)) {
                ((GroupCreateUserCell) viewHolder.itemView).setDrawDivider(adapterPosition2 != EditWidgetActivity.this.chatsEndRow - 1);
                GroupCreateUserCell groupCreateUserCell = (GroupCreateUserCell) viewHolder2.itemView;
                if (adapterPosition != EditWidgetActivity.this.chatsEndRow - 1) {
                    z = true;
                }
                groupCreateUserCell.setDrawDivider(z);
                this.moved = true;
            }
            return true;
        }

        public void onChildDraw(Canvas canvas, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float f, float f2, int i, boolean z) {
            super.onChildDraw(canvas, recyclerView, viewHolder, f, f2, i, z);
        }

        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int i) {
            if (i != 0) {
                EditWidgetActivity.this.listView.cancelClickRunnables(false);
                viewHolder.itemView.setPressed(true);
            } else if (this.moved) {
                if (EditWidgetActivity.this.widgetPreviewCell != null) {
                    EditWidgetActivity.this.widgetPreviewCell.updateDialogs();
                }
                this.moved = false;
            }
            super.onSelectedChanged(viewHolder, i);
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

        /* access modifiers changed from: protected */
        public void dispatchSetPressed(boolean z) {
        }

        public boolean dispatchTouchEvent(MotionEvent motionEvent) {
            return false;
        }

        public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
            return false;
        }

        public boolean onTouchEvent(MotionEvent motionEvent) {
            return false;
        }

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
                android.widget.LinearLayout r6 = new android.widget.LinearLayout
                r6.<init>(r2)
                r6.setOrientation(r4)
                r7 = -2
                r8 = 17
                android.widget.FrameLayout$LayoutParams r9 = org.telegram.ui.Components.LayoutHelper.createFrame(r7, r7, r8)
                r0.addView(r6, r9)
                org.telegram.ui.Cells.ChatActionCell r9 = new org.telegram.ui.Cells.ChatActionCell
                r9.<init>(r2)
                java.lang.String r10 = "WidgetPreview"
                r11 = 2131628324(0x7f0e1124, float:1.8883937E38)
                java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r10, r11)
                r9.setCustomText(r10)
                r11 = -2
                r12 = -2
                r13 = 17
                r14 = 0
                r15 = 0
                r16 = 0
                r17 = 4
                android.widget.LinearLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r11, (int) r12, (int) r13, (int) r14, (int) r15, (int) r16, (int) r17)
                r6.addView(r9, r10)
                android.widget.LinearLayout r9 = new android.widget.LinearLayout
                r9.<init>(r2)
                r9.setOrientation(r4)
                r10 = 2131166142(0x7var_be, float:1.794652E38)
                r9.setBackgroundResource(r10)
                r14 = 10
                r16 = 10
                r17 = 0
                android.widget.LinearLayout$LayoutParams r10 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r11, (int) r12, (int) r13, (int) r14, (int) r15, (int) r16, (int) r17)
                r6.addView(r9, r10)
                android.widget.ImageView r6 = new android.widget.ImageView
                r6.<init>(r2)
                android.widget.ImageView unused = r1.previewImageView = r6
                int r6 = r19.widgetType
                r10 = 0
                r11 = 160(0xa0, float:2.24E-43)
                if (r6 != 0) goto L_0x00ce
            L_0x0090:
                if (r5 >= r3) goto L_0x00b6
                android.view.ViewGroup[] r4 = r0.cells
                android.app.Activity r6 = r19.getParentActivity()
                android.view.LayoutInflater r6 = r6.getLayoutInflater()
                r12 = 2131427346(0x7f0b0012, float:1.8476306E38)
                android.view.View r6 = r6.inflate(r12, r10)
                android.view.ViewGroup r6 = (android.view.ViewGroup) r6
                r4[r5] = r6
                android.view.ViewGroup[] r4 = r0.cells
                r4 = r4[r5]
                r6 = -1
                android.widget.LinearLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createLinear(r6, r7)
                r9.addView(r4, r6)
                int r5 = r5 + 1
                goto L_0x0090
            L_0x00b6:
                android.widget.ImageView r3 = r19.previewImageView
                r4 = 218(0xda, float:3.05E-43)
                android.widget.LinearLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r4, (int) r11, (int) r8)
                r9.addView(r3, r4)
                android.widget.ImageView r1 = r19.previewImageView
                r3 = 2131165357(0x7var_ad, float:1.7944929E38)
                r1.setImageResource(r3)
                goto L_0x010e
            L_0x00ce:
                int r6 = r19.widgetType
                if (r6 != r4) goto L_0x010e
            L_0x00d4:
                if (r5 >= r3) goto L_0x00f9
                android.view.ViewGroup[] r4 = r0.cells
                android.app.Activity r6 = r19.getParentActivity()
                android.view.LayoutInflater r6 = r6.getLayoutInflater()
                r12 = 2131427330(0x7f0b0002, float:1.8476273E38)
                android.view.View r6 = r6.inflate(r12, r10)
                android.view.ViewGroup r6 = (android.view.ViewGroup) r6
                r4[r5] = r6
                android.view.ViewGroup[] r4 = r0.cells
                r4 = r4[r5]
                android.widget.LinearLayout$LayoutParams r6 = org.telegram.ui.Components.LayoutHelper.createLinear(r11, r7)
                r9.addView(r4, r6)
                int r5 = r5 + 1
                goto L_0x00d4
            L_0x00f9:
                android.widget.ImageView r3 = r19.previewImageView
                android.widget.LinearLayout$LayoutParams r4 = org.telegram.ui.Components.LayoutHelper.createLinear((int) r11, (int) r11, (int) r8)
                r9.addView(r3, r4)
                android.widget.ImageView r1 = r19.previewImageView
                r3 = 2131165385(0x7var_c9, float:1.7944986E38)
                r1.setImageResource(r3)
            L_0x010e:
                r18.updateDialogs()
                r1 = 2131165443(0x7var_, float:1.7945103E38)
                java.lang.String r3 = "windowBackgroundGrayShadow"
                android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r2, (int) r1, (java.lang.String) r3)
                r0.shadowDrawable = r1
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.EditWidgetActivity.WidgetPreviewCell.<init>(org.telegram.ui.EditWidgetActivity, android.content.Context):void");
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v58, resolved type: org.telegram.tgnet.TLRPC$Chat} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v42, resolved type: org.telegram.tgnet.TLRPC$User} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v71, resolved type: org.telegram.tgnet.TLRPC$Chat} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v196, resolved type: android.graphics.Bitmap} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v17, resolved type: org.telegram.tgnet.TLRPC$FileLocation} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v72, resolved type: org.telegram.tgnet.TLRPC$Chat} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v74, resolved type: org.telegram.tgnet.TLRPC$Chat} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v18, resolved type: org.telegram.tgnet.TLRPC$FileLocation} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v77, resolved type: org.telegram.tgnet.TLRPC$Chat} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v78, resolved type: android.graphics.Bitmap} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v19, resolved type: org.telegram.tgnet.TLRPC$FileLocation} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v23, resolved type: org.telegram.tgnet.TLRPC$FileLocation} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v223, resolved type: org.telegram.tgnet.TLRPC$User} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v224, resolved type: org.telegram.tgnet.TLRPC$User} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v226, resolved type: org.telegram.tgnet.TLRPC$TL_dialog} */
        /* JADX WARNING: type inference failed for: r14v5, types: [org.telegram.tgnet.TLRPC$Dialog] */
        /* JADX WARNING: type inference failed for: r15v0, types: [org.telegram.tgnet.TLObject] */
        /* JADX WARNING: type inference failed for: r0v66, types: [org.telegram.tgnet.TLRPC$User] */
        /* JADX WARNING: type inference failed for: r9v26, types: [org.telegram.tgnet.TLRPC$User] */
        /* JADX WARNING: type inference failed for: r9v39 */
        /* JADX WARNING: type inference failed for: r0v197 */
        /* JADX WARNING: type inference failed for: r3v81 */
        /* JADX WARNING: type inference failed for: r14v6 */
        /* JADX WARNING: type inference failed for: r14v7 */
        /* JADX WARNING: type inference failed for: r0v222 */
        /* JADX WARNING: type inference failed for: r3v82 */
        /* JADX WARNING: Code restructure failed: missing block: B:99:0x026f, code lost:
            if ((r0 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChannelMigrateFrom) == false) goto L_0x0272;
         */
        /* JADX WARNING: Multi-variable type inference failed */
        /* JADX WARNING: Removed duplicated region for block: B:18:0x0090  */
        /* JADX WARNING: Removed duplicated region for block: B:19:0x009b  */
        /* JADX WARNING: Removed duplicated region for block: B:261:0x06da  */
        /* JADX WARNING: Removed duplicated region for block: B:270:0x0704  */
        /* JADX WARNING: Removed duplicated region for block: B:359:0x08a3  */
        /* JADX WARNING: Removed duplicated region for block: B:372:0x08f1  */
        /* JADX WARNING: Unknown variable types count: 1 */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void updateDialogs() {
            /*
                r20 = this;
                r1 = r20
                org.telegram.ui.EditWidgetActivity r0 = r1.this$0
                int r0 = r0.widgetType
                r2 = 1111490560(0x42400000, float:48.0)
                r3 = 2131627331(0x7f0e0d43, float:1.8881923E38)
                java.lang.String r4 = "SavedMessages"
                r5 = 0
                r6 = 0
                r8 = 8
                r9 = 2
                r10 = 0
                r11 = 1
                r12 = 0
                if (r0 != 0) goto L_0x0634
                r13 = 0
            L_0x001b:
                if (r13 >= r9) goto L_0x060f
                org.telegram.ui.EditWidgetActivity r0 = r1.this$0
                java.util.ArrayList r0 = r0.selectedDialogs
                boolean r0 = r0.isEmpty()
                if (r0 == 0) goto L_0x0048
                org.telegram.ui.EditWidgetActivity r0 = r1.this$0
                org.telegram.messenger.MessagesController r0 = r0.getMessagesController()
                java.util.ArrayList<org.telegram.tgnet.TLRPC$Dialog> r0 = r0.dialogsServerOnly
                int r0 = r0.size()
                if (r13 >= r0) goto L_0x0046
                org.telegram.ui.EditWidgetActivity r0 = r1.this$0
                org.telegram.messenger.MessagesController r0 = r0.getMessagesController()
                java.util.ArrayList<org.telegram.tgnet.TLRPC$Dialog> r0 = r0.dialogsServerOnly
                java.lang.Object r0 = r0.get(r13)
                org.telegram.tgnet.TLRPC$Dialog r0 = (org.telegram.tgnet.TLRPC$Dialog) r0
                goto L_0x008b
            L_0x0046:
                r0 = r10
                goto L_0x008b
            L_0x0048:
                org.telegram.ui.EditWidgetActivity r0 = r1.this$0
                java.util.ArrayList r0 = r0.selectedDialogs
                int r0 = r0.size()
                if (r13 >= r0) goto L_0x008d
                org.telegram.ui.EditWidgetActivity r0 = r1.this$0
                org.telegram.messenger.MessagesController r0 = r0.getMessagesController()
                android.util.LongSparseArray<org.telegram.tgnet.TLRPC$Dialog> r0 = r0.dialogs_dict
                org.telegram.ui.EditWidgetActivity r14 = r1.this$0
                java.util.ArrayList r14 = r14.selectedDialogs
                java.lang.Object r14 = r14.get(r13)
                java.lang.Long r14 = (java.lang.Long) r14
                long r14 = r14.longValue()
                java.lang.Object r0 = r0.get(r14)
                org.telegram.tgnet.TLRPC$Dialog r0 = (org.telegram.tgnet.TLRPC$Dialog) r0
                if (r0 != 0) goto L_0x008b
                org.telegram.tgnet.TLRPC$TL_dialog r0 = new org.telegram.tgnet.TLRPC$TL_dialog
                r0.<init>()
                org.telegram.ui.EditWidgetActivity r14 = r1.this$0
                java.util.ArrayList r14 = r14.selectedDialogs
                java.lang.Object r14 = r14.get(r13)
                java.lang.Long r14 = (java.lang.Long) r14
                long r14 = r14.longValue()
                r0.id = r14
            L_0x008b:
                r14 = r0
                goto L_0x008e
            L_0x008d:
                r14 = r10
            L_0x008e:
                if (r14 != 0) goto L_0x009b
                android.view.ViewGroup[] r0 = r1.cells
                r0 = r0[r13]
                r0.setVisibility(r8)
                r18 = r4
                goto L_0x05fd
            L_0x009b:
                android.view.ViewGroup[] r0 = r1.cells
                r0 = r0[r13]
                r0.setVisibility(r12)
                long r8 = r14.id
                int r0 = (int) r8
                java.lang.String r8 = ""
                if (r0 <= 0) goto L_0x0117
                org.telegram.ui.EditWidgetActivity r9 = r1.this$0
                org.telegram.messenger.MessagesController r9 = r9.getMessagesController()
                java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
                org.telegram.tgnet.TLRPC$User r0 = r9.getUser(r0)
                if (r0 == 0) goto L_0x0111
                boolean r9 = org.telegram.messenger.UserObject.isUserSelf(r0)
                if (r9 == 0) goto L_0x00c4
                java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r4, r3)
                goto L_0x00ec
            L_0x00c4:
                boolean r9 = org.telegram.messenger.UserObject.isReplyUser((org.telegram.tgnet.TLRPC$User) r0)
                if (r9 == 0) goto L_0x00d4
                r9 = 2131627212(0x7f0e0ccc, float:1.8881682E38)
                java.lang.String r15 = "RepliesTitle"
                java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r15, r9)
                goto L_0x00ec
            L_0x00d4:
                boolean r9 = org.telegram.messenger.UserObject.isDeleted(r0)
                if (r9 == 0) goto L_0x00e4
                r9 = 2131625779(0x7f0e0733, float:1.8878776E38)
                java.lang.String r15 = "HiddenName"
                java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r15, r9)
                goto L_0x00ec
            L_0x00e4:
                java.lang.String r9 = r0.first_name
                java.lang.String r15 = r0.last_name
                java.lang.String r9 = org.telegram.messenger.ContactsController.formatName(r9, r15)
            L_0x00ec:
                boolean r15 = org.telegram.messenger.UserObject.isReplyUser((org.telegram.tgnet.TLRPC$User) r0)
                if (r15 != 0) goto L_0x010e
                boolean r15 = org.telegram.messenger.UserObject.isUserSelf(r0)
                if (r15 != 0) goto L_0x010e
                org.telegram.tgnet.TLRPC$UserProfilePhoto r15 = r0.photo
                if (r15 == 0) goto L_0x010e
                org.telegram.tgnet.TLRPC$FileLocation r15 = r15.photo_small
                if (r15 == 0) goto L_0x010e
                r18 = r4
                long r3 = r15.volume_id
                int r19 = (r3 > r6 ? 1 : (r3 == r6 ? 0 : -1))
                if (r19 == 0) goto L_0x0114
                int r3 = r15.local_id
                if (r3 == 0) goto L_0x0114
                r3 = r10
                goto L_0x0147
            L_0x010e:
                r18 = r4
                goto L_0x0114
            L_0x0111:
                r18 = r4
                r9 = r8
            L_0x0114:
                r3 = r10
                r15 = r3
                goto L_0x0147
            L_0x0117:
                r18 = r4
                org.telegram.ui.EditWidgetActivity r3 = r1.this$0
                org.telegram.messenger.MessagesController r3 = r3.getMessagesController()
                int r0 = -r0
                java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
                org.telegram.tgnet.TLRPC$Chat r0 = r3.getChat(r0)
                if (r0 == 0) goto L_0x0143
                java.lang.String r9 = r0.title
                org.telegram.tgnet.TLRPC$ChatPhoto r3 = r0.photo
                if (r3 == 0) goto L_0x0141
                org.telegram.tgnet.TLRPC$FileLocation r15 = r3.photo_small
                if (r15 == 0) goto L_0x0141
                long r3 = r15.volume_id
                int r19 = (r3 > r6 ? 1 : (r3 == r6 ? 0 : -1))
                if (r19 == 0) goto L_0x0141
                int r3 = r15.local_id
                if (r3 == 0) goto L_0x0141
                r3 = r0
                r0 = r10
                goto L_0x0147
            L_0x0141:
                r3 = r0
                goto L_0x0145
            L_0x0143:
                r3 = r0
                r9 = r8
            L_0x0145:
                r0 = r10
                r15 = r0
            L_0x0147:
                android.view.ViewGroup[] r4 = r1.cells
                r4 = r4[r13]
                r6 = 2131230908(0x7var_bc, float:1.8077882E38)
                android.view.View r4 = r4.findViewById(r6)
                android.widget.TextView r4 = (android.widget.TextView) r4
                r4.setText(r9)
                if (r15 == 0) goto L_0x0166
                java.io.File r4 = org.telegram.messenger.FileLoader.getPathToAttach(r15, r11)     // Catch:{ all -> 0x0206 }
                java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x0206 }
                android.graphics.Bitmap r4 = android.graphics.BitmapFactory.decodeFile(r4)     // Catch:{ all -> 0x0206 }
                goto L_0x0167
            L_0x0166:
                r4 = r10
            L_0x0167:
                int r6 = org.telegram.messenger.AndroidUtilities.dp(r2)     // Catch:{ all -> 0x0206 }
                android.graphics.Bitmap$Config r7 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ all -> 0x0206 }
                android.graphics.Bitmap r7 = android.graphics.Bitmap.createBitmap(r6, r6, r7)     // Catch:{ all -> 0x0206 }
                r7.eraseColor(r12)     // Catch:{ all -> 0x0206 }
                android.graphics.Canvas r9 = new android.graphics.Canvas     // Catch:{ all -> 0x0206 }
                r9.<init>(r7)     // Catch:{ all -> 0x0206 }
                if (r4 != 0) goto L_0x01a4
                if (r0 == 0) goto L_0x0198
                org.telegram.ui.Components.AvatarDrawable r4 = new org.telegram.ui.Components.AvatarDrawable     // Catch:{ all -> 0x0206 }
                r4.<init>((org.telegram.tgnet.TLRPC$User) r0)     // Catch:{ all -> 0x0206 }
                boolean r15 = org.telegram.messenger.UserObject.isReplyUser((org.telegram.tgnet.TLRPC$User) r0)     // Catch:{ all -> 0x0206 }
                if (r15 == 0) goto L_0x018e
                r0 = 12
                r4.setAvatarType(r0)     // Catch:{ all -> 0x0206 }
                goto L_0x019d
            L_0x018e:
                boolean r0 = org.telegram.messenger.UserObject.isUserSelf(r0)     // Catch:{ all -> 0x0206 }
                if (r0 == 0) goto L_0x019d
                r4.setAvatarType(r11)     // Catch:{ all -> 0x0206 }
                goto L_0x019d
            L_0x0198:
                org.telegram.ui.Components.AvatarDrawable r4 = new org.telegram.ui.Components.AvatarDrawable     // Catch:{ all -> 0x0206 }
                r4.<init>((org.telegram.tgnet.TLRPC$Chat) r3)     // Catch:{ all -> 0x0206 }
            L_0x019d:
                r4.setBounds(r12, r12, r6, r6)     // Catch:{ all -> 0x0206 }
                r4.draw(r9)     // Catch:{ all -> 0x0206 }
                goto L_0x01f2
            L_0x01a4:
                android.graphics.BitmapShader r0 = new android.graphics.BitmapShader     // Catch:{ all -> 0x0206 }
                android.graphics.Shader$TileMode r15 = android.graphics.Shader.TileMode.CLAMP     // Catch:{ all -> 0x0206 }
                r0.<init>(r4, r15, r15)     // Catch:{ all -> 0x0206 }
                android.graphics.Paint r15 = r1.roundPaint     // Catch:{ all -> 0x0206 }
                if (r15 != 0) goto L_0x01bd
                android.graphics.Paint r15 = new android.graphics.Paint     // Catch:{ all -> 0x0206 }
                r15.<init>(r11)     // Catch:{ all -> 0x0206 }
                r1.roundPaint = r15     // Catch:{ all -> 0x0206 }
                android.graphics.RectF r15 = new android.graphics.RectF     // Catch:{ all -> 0x0206 }
                r15.<init>()     // Catch:{ all -> 0x0206 }
                r1.bitmapRect = r15     // Catch:{ all -> 0x0206 }
            L_0x01bd:
                float r6 = (float) r6     // Catch:{ all -> 0x0206 }
                int r15 = r4.getWidth()     // Catch:{ all -> 0x0206 }
                float r15 = (float) r15     // Catch:{ all -> 0x0206 }
                float r6 = r6 / r15
                r9.save()     // Catch:{ all -> 0x0206 }
                r9.scale(r6, r6)     // Catch:{ all -> 0x0206 }
                android.graphics.Paint r6 = r1.roundPaint     // Catch:{ all -> 0x0206 }
                r6.setShader(r0)     // Catch:{ all -> 0x0206 }
                android.graphics.RectF r0 = r1.bitmapRect     // Catch:{ all -> 0x0206 }
                int r6 = r4.getWidth()     // Catch:{ all -> 0x0206 }
                float r6 = (float) r6     // Catch:{ all -> 0x0206 }
                int r15 = r4.getHeight()     // Catch:{ all -> 0x0206 }
                float r15 = (float) r15     // Catch:{ all -> 0x0206 }
                r0.set(r5, r5, r6, r15)     // Catch:{ all -> 0x0206 }
                android.graphics.RectF r0 = r1.bitmapRect     // Catch:{ all -> 0x0206 }
                int r6 = r4.getWidth()     // Catch:{ all -> 0x0206 }
                float r6 = (float) r6     // Catch:{ all -> 0x0206 }
                int r4 = r4.getHeight()     // Catch:{ all -> 0x0206 }
                float r4 = (float) r4     // Catch:{ all -> 0x0206 }
                android.graphics.Paint r15 = r1.roundPaint     // Catch:{ all -> 0x0206 }
                r9.drawRoundRect(r0, r6, r4, r15)     // Catch:{ all -> 0x0206 }
                r9.restore()     // Catch:{ all -> 0x0206 }
            L_0x01f2:
                r9.setBitmap(r10)     // Catch:{ all -> 0x0206 }
                android.view.ViewGroup[] r0 = r1.cells     // Catch:{ all -> 0x0206 }
                r0 = r0[r13]     // Catch:{ all -> 0x0206 }
                r4 = 2131230904(0x7var_b8, float:1.8077874E38)
                android.view.View r0 = r0.findViewById(r4)     // Catch:{ all -> 0x0206 }
                android.widget.ImageView r0 = (android.widget.ImageView) r0     // Catch:{ all -> 0x0206 }
                r0.setImageBitmap(r7)     // Catch:{ all -> 0x0206 }
                goto L_0x020a
            L_0x0206:
                r0 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            L_0x020a:
                org.telegram.ui.EditWidgetActivity r0 = r1.this$0
                org.telegram.messenger.MessagesController r0 = r0.getMessagesController()
                android.util.LongSparseArray<org.telegram.messenger.MessageObject> r0 = r0.dialogMessage
                long r6 = r14.id
                java.lang.Object r0 = r0.get(r6)
                r4 = r0
                org.telegram.messenger.MessageObject r4 = (org.telegram.messenger.MessageObject) r4
                r7 = 2131230909(0x7var_bd, float:1.8077884E38)
                if (r4 == 0) goto L_0x055f
                int r0 = r4.getFromChatId()
                if (r0 <= 0) goto L_0x0237
                org.telegram.ui.EditWidgetActivity r9 = r1.this$0
                org.telegram.messenger.MessagesController r9 = r9.getMessagesController()
                java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
                org.telegram.tgnet.TLRPC$User r0 = r9.getUser(r0)
                r9 = r0
                r0 = r10
                goto L_0x0247
            L_0x0237:
                org.telegram.ui.EditWidgetActivity r9 = r1.this$0
                org.telegram.messenger.MessagesController r9 = r9.getMessagesController()
                int r0 = -r0
                java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
                org.telegram.tgnet.TLRPC$Chat r0 = r9.getChat(r0)
                r9 = r10
            L_0x0247:
                android.content.Context r15 = r20.getContext()
                android.content.res.Resources r15 = r15.getResources()
                r10 = 2131034146(0x7var_, float:1.7678801E38)
                int r10 = r15.getColor(r10)
                org.telegram.tgnet.TLRPC$Message r15 = r4.messageOwner
                boolean r15 = r15 instanceof org.telegram.tgnet.TLRPC$TL_messageService
                r5 = 2131034141(0x7var_d, float:1.7678791E38)
                if (r15 == 0) goto L_0x0282
                boolean r0 = org.telegram.messenger.ChatObject.isChannel(r3)
                if (r0 == 0) goto L_0x0272
                org.telegram.tgnet.TLRPC$Message r0 = r4.messageOwner
                org.telegram.tgnet.TLRPC$MessageAction r0 = r0.action
                boolean r3 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageActionHistoryClear
                if (r3 != 0) goto L_0x0274
                boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChannelMigrateFrom
                if (r0 == 0) goto L_0x0272
                goto L_0x0274
            L_0x0272:
                java.lang.CharSequence r8 = r4.messageText
            L_0x0274:
                android.content.Context r0 = r20.getContext()
                android.content.res.Resources r0 = r0.getResources()
                int r10 = r0.getColor(r5)
                goto L_0x0527
            L_0x0282:
                if (r3 == 0) goto L_0x043d
                int r15 = r3.id
                if (r15 <= 0) goto L_0x043d
                if (r0 != 0) goto L_0x043d
                boolean r0 = org.telegram.messenger.ChatObject.isChannel(r3)
                if (r0 == 0) goto L_0x0296
                boolean r0 = org.telegram.messenger.ChatObject.isMegagroup(r3)
                if (r0 == 0) goto L_0x043d
            L_0x0296:
                boolean r0 = r4.isOutOwner()
                if (r0 == 0) goto L_0x02a7
                r0 = 2131625689(0x7f0e06d9, float:1.8878593E38)
                java.lang.String r3 = "FromYou"
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r3, r0)
            L_0x02a5:
                r3 = r0
                goto L_0x02b7
            L_0x02a7:
                if (r9 == 0) goto L_0x02b4
                java.lang.String r0 = org.telegram.messenger.UserObject.getFirstName(r9)
                java.lang.String r3 = "\n"
                java.lang.String r0 = r0.replace(r3, r8)
                goto L_0x02a5
            L_0x02b4:
                java.lang.String r0 = "DELETED"
                goto L_0x02a5
            L_0x02b7:
                java.lang.String r0 = "%2$s: ⁨%1$s⁩"
                java.lang.CharSequence r9 = r4.caption
                r15 = 32
                r2 = 10
                r6 = 150(0x96, float:2.1E-43)
                if (r9 == 0) goto L_0x031c
                java.lang.String r5 = r9.toString()
                int r8 = r5.length()
                if (r8 <= r6) goto L_0x02d1
                java.lang.String r5 = r5.substring(r12, r6)
            L_0x02d1:
                boolean r6 = r4.isVideo()
                if (r6 == 0) goto L_0x02db
                java.lang.String r6 = "📹 "
            L_0x02d9:
                r8 = 2
                goto L_0x02f9
            L_0x02db:
                boolean r6 = r4.isVoice()
                if (r6 == 0) goto L_0x02e4
                java.lang.String r6 = "🎤 "
                goto L_0x02d9
            L_0x02e4:
                boolean r6 = r4.isMusic()
                if (r6 == 0) goto L_0x02ed
                java.lang.String r6 = "🎧 "
                goto L_0x02d9
            L_0x02ed:
                boolean r6 = r4.isPhoto()
                if (r6 == 0) goto L_0x02f6
                java.lang.String r6 = "🖼 "
                goto L_0x02d9
            L_0x02f6:
                java.lang.String r6 = "📎 "
                goto L_0x02d9
            L_0x02f9:
                java.lang.Object[] r9 = new java.lang.Object[r8]
                java.lang.StringBuilder r8 = new java.lang.StringBuilder
                r8.<init>()
                r8.append(r6)
                java.lang.String r2 = r5.replace(r2, r15)
                r8.append(r2)
                java.lang.String r2 = r8.toString()
                r9[r12] = r2
                r9[r11] = r3
                java.lang.String r0 = java.lang.String.format(r0, r9)
                android.text.SpannableStringBuilder r0 = android.text.SpannableStringBuilder.valueOf(r0)
                goto L_0x0423
            L_0x031c:
                org.telegram.tgnet.TLRPC$Message r9 = r4.messageOwner
                org.telegram.tgnet.TLRPC$MessageMedia r9 = r9.media
                if (r9 == 0) goto L_0x03f7
                boolean r9 = r4.isMediaEmpty()
                if (r9 != 0) goto L_0x03f7
                android.content.Context r6 = r20.getContext()
                android.content.res.Resources r6 = r6.getResources()
                int r5 = r6.getColor(r5)
                org.telegram.tgnet.TLRPC$Message r6 = r4.messageOwner
                org.telegram.tgnet.TLRPC$MessageMedia r6 = r6.media
                boolean r8 = r6 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
                r9 = 18
                if (r8 == 0) goto L_0x0363
                org.telegram.tgnet.TLRPC$TL_messageMediaPoll r6 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r6
                int r8 = android.os.Build.VERSION.SDK_INT
                if (r8 < r9) goto L_0x0354
                java.lang.Object[] r8 = new java.lang.Object[r11]
                org.telegram.tgnet.TLRPC$Poll r6 = r6.poll
                java.lang.String r6 = r6.question
                r8[r12] = r6
                java.lang.String r6 = "📊 ⁨%s⁩"
                java.lang.String r6 = java.lang.String.format(r6, r8)
                goto L_0x03c6
            L_0x0354:
                java.lang.Object[] r8 = new java.lang.Object[r11]
                org.telegram.tgnet.TLRPC$Poll r6 = r6.poll
                java.lang.String r6 = r6.question
                r8[r12] = r6
                java.lang.String r6 = "📊 %s"
                java.lang.String r6 = java.lang.String.format(r6, r8)
                goto L_0x03c6
            L_0x0363:
                boolean r8 = r6 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
                if (r8 == 0) goto L_0x0389
                int r8 = android.os.Build.VERSION.SDK_INT
                if (r8 < r9) goto L_0x037a
                java.lang.Object[] r8 = new java.lang.Object[r11]
                org.telegram.tgnet.TLRPC$TL_game r6 = r6.game
                java.lang.String r6 = r6.title
                r8[r12] = r6
                java.lang.String r6 = "🎮 ⁨%s⁩"
                java.lang.String r6 = java.lang.String.format(r6, r8)
                goto L_0x03c6
            L_0x037a:
                java.lang.Object[] r8 = new java.lang.Object[r11]
                org.telegram.tgnet.TLRPC$TL_game r6 = r6.game
                java.lang.String r6 = r6.title
                r8[r12] = r6
                java.lang.String r6 = "🎮 %s"
                java.lang.String r6 = java.lang.String.format(r6, r8)
                goto L_0x03c6
            L_0x0389:
                int r6 = r4.type
                r8 = 14
                if (r6 != r8) goto L_0x03c0
                int r6 = android.os.Build.VERSION.SDK_INT
                if (r6 < r9) goto L_0x03aa
                r6 = 2
                java.lang.Object[] r8 = new java.lang.Object[r6]
                java.lang.String r9 = r4.getMusicAuthor()
                r8[r12] = r9
                java.lang.String r9 = r4.getMusicTitle()
                r8[r11] = r9
                java.lang.String r9 = "🎧 ⁨%s - %s⁩"
                java.lang.String r8 = java.lang.String.format(r9, r8)
                r6 = r8
                goto L_0x03c6
            L_0x03aa:
                r6 = 2
                java.lang.Object[] r8 = new java.lang.Object[r6]
                java.lang.String r6 = r4.getMusicAuthor()
                r8[r12] = r6
                java.lang.String r6 = r4.getMusicTitle()
                r8[r11] = r6
                java.lang.String r6 = "🎧 %s - %s"
                java.lang.String r6 = java.lang.String.format(r6, r8)
                goto L_0x03c6
            L_0x03c0:
                java.lang.CharSequence r6 = r4.messageText
                java.lang.String r6 = r6.toString()
            L_0x03c6:
                java.lang.String r2 = r6.replace(r2, r15)
                r6 = 2
                java.lang.Object[] r8 = new java.lang.Object[r6]
                r8[r12] = r2
                r8[r11] = r3
                java.lang.String r0 = java.lang.String.format(r0, r8)
                android.text.SpannableStringBuilder r2 = android.text.SpannableStringBuilder.valueOf(r0)
                org.telegram.ui.Components.ForegroundColorSpanThemable r0 = new org.telegram.ui.Components.ForegroundColorSpanThemable     // Catch:{ Exception -> 0x03f0 }
                java.lang.String r6 = "chats_attachMessage"
                r0.<init>(r6)     // Catch:{ Exception -> 0x03f0 }
                int r6 = r3.length()     // Catch:{ Exception -> 0x03f0 }
                r8 = 2
                int r6 = r6 + r8
                int r8 = r2.length()     // Catch:{ Exception -> 0x03f0 }
                r9 = 33
                r2.setSpan(r0, r6, r8, r9)     // Catch:{ Exception -> 0x03f0 }
                goto L_0x03f4
            L_0x03f0:
                r0 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            L_0x03f4:
                r8 = r2
                r10 = r5
                goto L_0x0424
            L_0x03f7:
                org.telegram.tgnet.TLRPC$Message r5 = r4.messageOwner
                java.lang.String r5 = r5.message
                if (r5 == 0) goto L_0x041f
                int r8 = r5.length()
                if (r8 <= r6) goto L_0x0407
                java.lang.String r5 = r5.substring(r12, r6)
            L_0x0407:
                java.lang.String r2 = r5.replace(r2, r15)
                java.lang.String r2 = r2.trim()
                r5 = 2
                java.lang.Object[] r6 = new java.lang.Object[r5]
                r6[r12] = r2
                r6[r11] = r3
                java.lang.String r0 = java.lang.String.format(r0, r6)
                android.text.SpannableStringBuilder r0 = android.text.SpannableStringBuilder.valueOf(r0)
                goto L_0x0423
            L_0x041f:
                android.text.SpannableStringBuilder r0 = android.text.SpannableStringBuilder.valueOf(r8)
            L_0x0423:
                r8 = r0
            L_0x0424:
                org.telegram.ui.Components.ForegroundColorSpanThemable r0 = new org.telegram.ui.Components.ForegroundColorSpanThemable     // Catch:{ Exception -> 0x0437 }
                java.lang.String r2 = "chats_nameMessage"
                r0.<init>(r2)     // Catch:{ Exception -> 0x0437 }
                int r2 = r3.length()     // Catch:{ Exception -> 0x0437 }
                int r2 = r2 + r11
                r3 = 33
                r8.setSpan(r0, r12, r2, r3)     // Catch:{ Exception -> 0x0437 }
                goto L_0x0527
            L_0x0437:
                r0 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
                goto L_0x0527
            L_0x043d:
                org.telegram.tgnet.TLRPC$Message r0 = r4.messageOwner
                org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
                boolean r2 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto
                if (r2 == 0) goto L_0x045a
                org.telegram.tgnet.TLRPC$Photo r2 = r0.photo
                boolean r2 = r2 instanceof org.telegram.tgnet.TLRPC$TL_photoEmpty
                if (r2 == 0) goto L_0x045a
                int r2 = r0.ttl_seconds
                if (r2 == 0) goto L_0x045a
                r0 = 2131624403(0x7f0e01d3, float:1.8875985E38)
                java.lang.String r2 = "AttachPhotoExpired"
                java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r2, r0)
                goto L_0x0527
            L_0x045a:
                boolean r2 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
                if (r2 == 0) goto L_0x0473
                org.telegram.tgnet.TLRPC$Document r2 = r0.document
                boolean r2 = r2 instanceof org.telegram.tgnet.TLRPC$TL_documentEmpty
                if (r2 == 0) goto L_0x0473
                int r2 = r0.ttl_seconds
                if (r2 == 0) goto L_0x0473
                r0 = 2131624409(0x7f0e01d9, float:1.8875997E38)
                java.lang.String r2 = "AttachVideoExpired"
                java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r2, r0)
                goto L_0x0527
            L_0x0473:
                java.lang.CharSequence r2 = r4.caption
                if (r2 == 0) goto L_0x04b0
                boolean r0 = r4.isVideo()
                if (r0 == 0) goto L_0x0480
                java.lang.String r0 = "📹 "
                goto L_0x049d
            L_0x0480:
                boolean r0 = r4.isVoice()
                if (r0 == 0) goto L_0x0489
                java.lang.String r0 = "🎤 "
                goto L_0x049d
            L_0x0489:
                boolean r0 = r4.isMusic()
                if (r0 == 0) goto L_0x0492
                java.lang.String r0 = "🎧 "
                goto L_0x049d
            L_0x0492:
                boolean r0 = r4.isPhoto()
                if (r0 == 0) goto L_0x049b
                java.lang.String r0 = "🖼 "
                goto L_0x049d
            L_0x049b:
                java.lang.String r0 = "📎 "
            L_0x049d:
                java.lang.StringBuilder r2 = new java.lang.StringBuilder
                r2.<init>()
                r2.append(r0)
                java.lang.CharSequence r0 = r4.caption
                r2.append(r0)
                java.lang.String r8 = r2.toString()
                goto L_0x0527
            L_0x04b0:
                boolean r2 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
                if (r2 == 0) goto L_0x04cd
                org.telegram.tgnet.TLRPC$TL_messageMediaPoll r0 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r0
                java.lang.StringBuilder r2 = new java.lang.StringBuilder
                r2.<init>()
                java.lang.String r3 = "📊 "
                r2.append(r3)
                org.telegram.tgnet.TLRPC$Poll r0 = r0.poll
                java.lang.String r0 = r0.question
                r2.append(r0)
                java.lang.String r0 = r2.toString()
            L_0x04cb:
                r8 = r0
                goto L_0x050f
            L_0x04cd:
                boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
                if (r0 == 0) goto L_0x04eb
                java.lang.StringBuilder r0 = new java.lang.StringBuilder
                r0.<init>()
                java.lang.String r2 = "🎮 "
                r0.append(r2)
                org.telegram.tgnet.TLRPC$Message r2 = r4.messageOwner
                org.telegram.tgnet.TLRPC$MessageMedia r2 = r2.media
                org.telegram.tgnet.TLRPC$TL_game r2 = r2.game
                java.lang.String r2 = r2.title
                r0.append(r2)
                java.lang.String r0 = r0.toString()
                goto L_0x04cb
            L_0x04eb:
                int r0 = r4.type
                r2 = 14
                if (r0 != r2) goto L_0x0507
                r2 = 2
                java.lang.Object[] r0 = new java.lang.Object[r2]
                java.lang.String r2 = r4.getMusicAuthor()
                r0[r12] = r2
                java.lang.String r2 = r4.getMusicTitle()
                r0[r11] = r2
                java.lang.String r2 = "🎧 %s - %s"
                java.lang.String r0 = java.lang.String.format(r2, r0)
                goto L_0x04cb
            L_0x0507:
                java.lang.CharSequence r0 = r4.messageText
                java.util.ArrayList<java.lang.String> r2 = r4.highlightedWords
                org.telegram.messenger.AndroidUtilities.highlightText((java.lang.CharSequence) r0, (java.util.ArrayList<java.lang.String>) r2)
                goto L_0x04cb
            L_0x050f:
                org.telegram.tgnet.TLRPC$Message r0 = r4.messageOwner
                org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
                if (r0 == 0) goto L_0x0527
                boolean r0 = r4.isMediaEmpty()
                if (r0 != 0) goto L_0x0527
                android.content.Context r0 = r20.getContext()
                android.content.res.Resources r0 = r0.getResources()
                int r10 = r0.getColor(r5)
            L_0x0527:
                android.view.ViewGroup[] r0 = r1.cells
                r0 = r0[r13]
                android.view.View r0 = r0.findViewById(r7)
                android.widget.TextView r0 = (android.widget.TextView) r0
                org.telegram.tgnet.TLRPC$Message r2 = r4.messageOwner
                int r2 = r2.date
                long r2 = (long) r2
                java.lang.String r2 = org.telegram.messenger.LocaleController.stringForMessageListDate(r2)
                r0.setText(r2)
                android.view.ViewGroup[] r0 = r1.cells
                r0 = r0[r13]
                r2 = 2131230907(0x7var_bb, float:1.807788E38)
                android.view.View r0 = r0.findViewById(r2)
                android.widget.TextView r0 = (android.widget.TextView) r0
                java.lang.String r3 = r8.toString()
                r0.setText(r3)
                android.view.ViewGroup[] r0 = r1.cells
                r0 = r0[r13]
                android.view.View r0 = r0.findViewById(r2)
                android.widget.TextView r0 = (android.widget.TextView) r0
                r0.setTextColor(r10)
                goto L_0x0595
            L_0x055f:
                int r0 = r14.last_message_date
                if (r0 == 0) goto L_0x0578
                android.view.ViewGroup[] r0 = r1.cells
                r0 = r0[r13]
                android.view.View r0 = r0.findViewById(r7)
                android.widget.TextView r0 = (android.widget.TextView) r0
                int r2 = r14.last_message_date
                long r2 = (long) r2
                java.lang.String r2 = org.telegram.messenger.LocaleController.stringForMessageListDate(r2)
                r0.setText(r2)
                goto L_0x0585
            L_0x0578:
                android.view.ViewGroup[] r0 = r1.cells
                r0 = r0[r13]
                android.view.View r0 = r0.findViewById(r7)
                android.widget.TextView r0 = (android.widget.TextView) r0
                r0.setText(r8)
            L_0x0585:
                android.view.ViewGroup[] r0 = r1.cells
                r0 = r0[r13]
                r2 = 2131230907(0x7var_bb, float:1.807788E38)
                android.view.View r0 = r0.findViewById(r2)
                android.widget.TextView r0 = (android.widget.TextView) r0
                r0.setText(r8)
            L_0x0595:
                int r0 = r14.unread_count
                r2 = 2131230905(0x7var_b9, float:1.8077876E38)
                if (r0 <= 0) goto L_0x05f0
                android.view.ViewGroup[] r0 = r1.cells
                r0 = r0[r13]
                android.view.View r0 = r0.findViewById(r2)
                android.widget.TextView r0 = (android.widget.TextView) r0
                java.lang.Object[] r3 = new java.lang.Object[r11]
                int r4 = r14.unread_count
                java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
                r3[r12] = r4
                java.lang.String r4 = "%d"
                java.lang.String r3 = java.lang.String.format(r4, r3)
                r0.setText(r3)
                android.view.ViewGroup[] r0 = r1.cells
                r0 = r0[r13]
                android.view.View r0 = r0.findViewById(r2)
                r0.setVisibility(r12)
                org.telegram.ui.EditWidgetActivity r0 = r1.this$0
                org.telegram.messenger.MessagesController r0 = r0.getMessagesController()
                long r3 = r14.id
                boolean r0 = r0.isDialogMuted(r3)
                if (r0 == 0) goto L_0x05e1
                android.view.ViewGroup[] r0 = r1.cells
                r0 = r0[r13]
                android.view.View r0 = r0.findViewById(r2)
                r2 = 2131166146(0x7var_c2, float:1.794653E38)
                r0.setBackgroundResource(r2)
                goto L_0x05fd
            L_0x05e1:
                android.view.ViewGroup[] r0 = r1.cells
                r0 = r0[r13]
                android.view.View r0 = r0.findViewById(r2)
                r2 = 2131166145(0x7var_c1, float:1.7946527E38)
                r0.setBackgroundResource(r2)
                goto L_0x05fd
            L_0x05f0:
                android.view.ViewGroup[] r0 = r1.cells
                r0 = r0[r13]
                android.view.View r0 = r0.findViewById(r2)
                r2 = 8
                r0.setVisibility(r2)
            L_0x05fd:
                int r13 = r13 + 1
                r4 = r18
                r2 = 1111490560(0x42400000, float:48.0)
                r3 = 2131627331(0x7f0e0d43, float:1.8881923E38)
                r5 = 0
                r6 = 0
                r8 = 8
                r9 = 2
                r10 = 0
                goto L_0x001b
            L_0x060f:
                android.view.ViewGroup[] r0 = r1.cells
                r0 = r0[r12]
                r2 = 2131230906(0x7var_ba, float:1.8077878E38)
                android.view.View r0 = r0.findViewById(r2)
                android.view.ViewGroup[] r3 = r1.cells
                r3 = r3[r11]
                int r3 = r3.getVisibility()
                r0.setVisibility(r3)
                android.view.ViewGroup[] r0 = r1.cells
                r0 = r0[r11]
                android.view.View r0 = r0.findViewById(r2)
                r2 = 8
                r0.setVisibility(r2)
                goto L_0x091a
            L_0x0634:
                r18 = r4
                org.telegram.ui.EditWidgetActivity r0 = r1.this$0
                int r0 = r0.widgetType
                if (r0 != r11) goto L_0x091a
                r2 = 0
            L_0x063f:
                r3 = 2
                if (r2 >= r3) goto L_0x091a
                r4 = 0
            L_0x0643:
                if (r4 >= r3) goto L_0x090e
                int r0 = r2 * 2
                int r0 = r0 + r4
                org.telegram.ui.EditWidgetActivity r3 = r1.this$0
                java.util.ArrayList r3 = r3.selectedDialogs
                boolean r3 = r3.isEmpty()
                if (r3 == 0) goto L_0x068d
                org.telegram.ui.EditWidgetActivity r3 = r1.this$0
                org.telegram.messenger.MediaDataController r3 = r3.getMediaDataController()
                java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_topPeer> r3 = r3.hints
                int r3 = r3.size()
                if (r0 >= r3) goto L_0x06d1
                org.telegram.ui.EditWidgetActivity r3 = r1.this$0
                org.telegram.messenger.MediaDataController r3 = r3.getMediaDataController()
                java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_topPeer> r3 = r3.hints
                java.lang.Object r3 = r3.get(r0)
                org.telegram.tgnet.TLRPC$TL_topPeer r3 = (org.telegram.tgnet.TLRPC$TL_topPeer) r3
                org.telegram.tgnet.TLRPC$Peer r3 = r3.peer
                int r3 = r3.user_id
                org.telegram.ui.EditWidgetActivity r5 = r1.this$0
                org.telegram.messenger.MessagesController r5 = r5.getMessagesController()
                android.util.LongSparseArray<org.telegram.tgnet.TLRPC$Dialog> r5 = r5.dialogs_dict
                long r6 = (long) r3
                java.lang.Object r3 = r5.get(r6)
                org.telegram.tgnet.TLRPC$Dialog r3 = (org.telegram.tgnet.TLRPC$Dialog) r3
                if (r3 != 0) goto L_0x06d2
                org.telegram.tgnet.TLRPC$TL_dialog r3 = new org.telegram.tgnet.TLRPC$TL_dialog
                r3.<init>()
                r3.id = r6
                goto L_0x06d2
            L_0x068d:
                org.telegram.ui.EditWidgetActivity r3 = r1.this$0
                java.util.ArrayList r3 = r3.selectedDialogs
                int r3 = r3.size()
                if (r0 >= r3) goto L_0x06d1
                org.telegram.ui.EditWidgetActivity r3 = r1.this$0
                org.telegram.messenger.MessagesController r3 = r3.getMessagesController()
                android.util.LongSparseArray<org.telegram.tgnet.TLRPC$Dialog> r3 = r3.dialogs_dict
                org.telegram.ui.EditWidgetActivity r5 = r1.this$0
                java.util.ArrayList r5 = r5.selectedDialogs
                java.lang.Object r5 = r5.get(r0)
                java.lang.Long r5 = (java.lang.Long) r5
                long r5 = r5.longValue()
                java.lang.Object r3 = r3.get(r5)
                org.telegram.tgnet.TLRPC$Dialog r3 = (org.telegram.tgnet.TLRPC$Dialog) r3
                if (r3 != 0) goto L_0x06d2
                org.telegram.tgnet.TLRPC$TL_dialog r3 = new org.telegram.tgnet.TLRPC$TL_dialog
                r3.<init>()
                org.telegram.ui.EditWidgetActivity r5 = r1.this$0
                java.util.ArrayList r5 = r5.selectedDialogs
                java.lang.Object r5 = r5.get(r0)
                java.lang.Long r5 = (java.lang.Long) r5
                long r5 = r5.longValue()
                r3.id = r5
                goto L_0x06d2
            L_0x06d1:
                r3 = 0
            L_0x06d2:
                r5 = 2131230789(0x7var_, float:1.807764E38)
                r6 = 2131230790(0x7var_, float:1.8077643E38)
                if (r3 != 0) goto L_0x0704
                android.view.ViewGroup[] r3 = r1.cells
                r3 = r3[r2]
                if (r4 != 0) goto L_0x06e1
                goto L_0x06e4
            L_0x06e1:
                r5 = 2131230790(0x7var_, float:1.8077643E38)
            L_0x06e4:
                android.view.View r3 = r3.findViewById(r5)
                r5 = 4
                r3.setVisibility(r5)
                if (r0 == 0) goto L_0x06f1
                r3 = 2
                if (r0 != r3) goto L_0x06fa
            L_0x06f1:
                android.view.ViewGroup[] r0 = r1.cells
                r0 = r0[r2]
                r3 = 8
                r0.setVisibility(r3)
            L_0x06fa:
                r7 = r18
                r6 = 0
                r8 = 0
                r10 = 1111490560(0x42400000, float:48.0)
                r16 = 0
                goto L_0x0907
            L_0x0704:
                android.view.ViewGroup[] r7 = r1.cells
                r7 = r7[r2]
                if (r4 != 0) goto L_0x070b
                goto L_0x070e
            L_0x070b:
                r5 = 2131230790(0x7var_, float:1.8077643E38)
            L_0x070e:
                android.view.View r5 = r7.findViewById(r5)
                r5.setVisibility(r12)
                r5 = 2
                if (r0 == 0) goto L_0x071a
                if (r0 != r5) goto L_0x0721
            L_0x071a:
                android.view.ViewGroup[] r0 = r1.cells
                r0 = r0[r2]
                r0.setVisibility(r12)
            L_0x0721:
                long r6 = r3.id
                int r0 = (int) r6
                if (r0 <= 0) goto L_0x079b
                org.telegram.ui.EditWidgetActivity r6 = r1.this$0
                org.telegram.messenger.MessagesController r6 = r6.getMessagesController()
                java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
                org.telegram.tgnet.TLRPC$User r0 = r6.getUser(r0)
                boolean r6 = org.telegram.messenger.UserObject.isUserSelf(r0)
                if (r6 == 0) goto L_0x0744
                r7 = r18
                r6 = 2131627331(0x7f0e0d43, float:1.8881923E38)
                java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r7, r6)
                goto L_0x076d
            L_0x0744:
                r7 = r18
                r6 = 2131627331(0x7f0e0d43, float:1.8881923E38)
                boolean r8 = org.telegram.messenger.UserObject.isReplyUser((org.telegram.tgnet.TLRPC$User) r0)
                if (r8 == 0) goto L_0x0759
                r8 = 2131627212(0x7f0e0ccc, float:1.8881682E38)
                java.lang.String r9 = "RepliesTitle"
                java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r9, r8)
                goto L_0x076d
            L_0x0759:
                boolean r8 = org.telegram.messenger.UserObject.isDeleted(r0)
                if (r8 == 0) goto L_0x0769
                r8 = 2131625779(0x7f0e0733, float:1.8878776E38)
                java.lang.String r9 = "HiddenName"
                java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r9, r8)
                goto L_0x076d
            L_0x0769:
                java.lang.String r8 = org.telegram.messenger.UserObject.getFirstName(r0)
            L_0x076d:
                boolean r9 = org.telegram.messenger.UserObject.isReplyUser((org.telegram.tgnet.TLRPC$User) r0)
                if (r9 != 0) goto L_0x0795
                boolean r9 = org.telegram.messenger.UserObject.isUserSelf(r0)
                if (r9 != 0) goto L_0x0795
                if (r0 == 0) goto L_0x0795
                org.telegram.tgnet.TLRPC$UserProfilePhoto r9 = r0.photo
                if (r9 == 0) goto L_0x0795
                org.telegram.tgnet.TLRPC$FileLocation r9 = r9.photo_small
                if (r9 == 0) goto L_0x0795
                long r13 = r9.volume_id
                r16 = 0
                int r10 = (r13 > r16 ? 1 : (r13 == r16 ? 0 : -1))
                if (r10 == 0) goto L_0x0795
                int r10 = r9.local_id
                if (r10 == 0) goto L_0x0795
                r10 = r9
                r16 = 0
                r9 = r8
                r8 = 0
                goto L_0x07ce
            L_0x0795:
                r9 = r8
                r8 = 0
                r10 = 0
                r16 = 0
                goto L_0x07ce
            L_0x079b:
                r7 = r18
                r6 = 2131627331(0x7f0e0d43, float:1.8881923E38)
                org.telegram.ui.EditWidgetActivity r8 = r1.this$0
                org.telegram.messenger.MessagesController r8 = r8.getMessagesController()
                int r0 = -r0
                java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
                org.telegram.tgnet.TLRPC$Chat r0 = r8.getChat(r0)
                java.lang.String r8 = r0.title
                org.telegram.tgnet.TLRPC$ChatPhoto r9 = r0.photo
                if (r9 == 0) goto L_0x07c8
                org.telegram.tgnet.TLRPC$FileLocation r9 = r9.photo_small
                if (r9 == 0) goto L_0x07c8
                long r13 = r9.volume_id
                r16 = 0
                int r10 = (r13 > r16 ? 1 : (r13 == r16 ? 0 : -1))
                if (r10 == 0) goto L_0x07ca
                int r10 = r9.local_id
                if (r10 == 0) goto L_0x07ca
                r10 = r9
                r9 = r8
                goto L_0x07cc
            L_0x07c8:
                r16 = 0
            L_0x07ca:
                r9 = r8
                r10 = 0
            L_0x07cc:
                r8 = r0
                r0 = 0
            L_0x07ce:
                android.view.ViewGroup[] r13 = r1.cells
                r13 = r13[r2]
                if (r4 != 0) goto L_0x07d8
                r14 = 2131230797(0x7var_d, float:1.8077657E38)
                goto L_0x07db
            L_0x07d8:
                r14 = 2131230798(0x7var_e, float:1.8077659E38)
            L_0x07db:
                android.view.View r13 = r13.findViewById(r14)
                android.widget.TextView r13 = (android.widget.TextView) r13
                r13.setText(r9)
                if (r10 == 0) goto L_0x07fa
                java.io.File r9 = org.telegram.messenger.FileLoader.getPathToAttach(r10, r11)     // Catch:{ all -> 0x07f3 }
                java.lang.String r9 = r9.toString()     // Catch:{ all -> 0x07f3 }
                android.graphics.Bitmap r9 = android.graphics.BitmapFactory.decodeFile(r9)     // Catch:{ all -> 0x07f3 }
                goto L_0x07fb
            L_0x07f3:
                r0 = move-exception
                r6 = 0
                r8 = 0
                r10 = 1111490560(0x42400000, float:48.0)
                goto L_0x089c
            L_0x07fa:
                r9 = 0
            L_0x07fb:
                r10 = 1111490560(0x42400000, float:48.0)
                int r13 = org.telegram.messenger.AndroidUtilities.dp(r10)     // Catch:{ all -> 0x0899 }
                android.graphics.Bitmap$Config r14 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ all -> 0x0899 }
                android.graphics.Bitmap r14 = android.graphics.Bitmap.createBitmap(r13, r13, r14)     // Catch:{ all -> 0x0899 }
                r14.eraseColor(r12)     // Catch:{ all -> 0x0899 }
                android.graphics.Canvas r5 = new android.graphics.Canvas     // Catch:{ all -> 0x0899 }
                r5.<init>(r14)     // Catch:{ all -> 0x0899 }
                if (r9 != 0) goto L_0x083d
                if (r0 == 0) goto L_0x082e
                org.telegram.ui.Components.AvatarDrawable r8 = new org.telegram.ui.Components.AvatarDrawable     // Catch:{ all -> 0x0899 }
                r8.<init>((org.telegram.tgnet.TLRPC$User) r0)     // Catch:{ all -> 0x0899 }
                boolean r9 = org.telegram.messenger.UserObject.isReplyUser((org.telegram.tgnet.TLRPC$User) r0)     // Catch:{ all -> 0x0899 }
                if (r9 == 0) goto L_0x0824
                r0 = 12
                r8.setAvatarType(r0)     // Catch:{ all -> 0x0899 }
                goto L_0x0834
            L_0x0824:
                boolean r0 = org.telegram.messenger.UserObject.isUserSelf(r0)     // Catch:{ all -> 0x0899 }
                if (r0 == 0) goto L_0x0834
                r8.setAvatarType(r11)     // Catch:{ all -> 0x0899 }
                goto L_0x0834
            L_0x082e:
                org.telegram.ui.Components.AvatarDrawable r0 = new org.telegram.ui.Components.AvatarDrawable     // Catch:{ all -> 0x0899 }
                r0.<init>((org.telegram.tgnet.TLRPC$Chat) r8)     // Catch:{ all -> 0x0899 }
                r8 = r0
            L_0x0834:
                r8.setBounds(r12, r12, r13, r13)     // Catch:{ all -> 0x0899 }
                r8.draw(r5)     // Catch:{ all -> 0x0899 }
                r6 = 0
            L_0x083b:
                r8 = 0
                goto L_0x087b
            L_0x083d:
                android.graphics.BitmapShader r0 = new android.graphics.BitmapShader     // Catch:{ all -> 0x0899 }
                android.graphics.Shader$TileMode r8 = android.graphics.Shader.TileMode.CLAMP     // Catch:{ all -> 0x0899 }
                r0.<init>(r9, r8, r8)     // Catch:{ all -> 0x0899 }
                float r8 = (float) r13     // Catch:{ all -> 0x0899 }
                int r13 = r9.getWidth()     // Catch:{ all -> 0x0899 }
                float r13 = (float) r13     // Catch:{ all -> 0x0899 }
                float r8 = r8 / r13
                r5.save()     // Catch:{ all -> 0x0899 }
                r5.scale(r8, r8)     // Catch:{ all -> 0x0899 }
                android.graphics.Paint r8 = r1.roundPaint     // Catch:{ all -> 0x0899 }
                r8.setShader(r0)     // Catch:{ all -> 0x0899 }
                android.graphics.RectF r0 = r1.bitmapRect     // Catch:{ all -> 0x0899 }
                int r8 = r9.getWidth()     // Catch:{ all -> 0x0899 }
                float r8 = (float) r8     // Catch:{ all -> 0x0899 }
                int r13 = r9.getHeight()     // Catch:{ all -> 0x0899 }
                float r13 = (float) r13
                r6 = 0
                r0.set(r6, r6, r8, r13)     // Catch:{ all -> 0x0897 }
                android.graphics.RectF r0 = r1.bitmapRect     // Catch:{ all -> 0x0897 }
                int r8 = r9.getWidth()     // Catch:{ all -> 0x0897 }
                float r8 = (float) r8     // Catch:{ all -> 0x0897 }
                int r9 = r9.getHeight()     // Catch:{ all -> 0x0897 }
                float r9 = (float) r9     // Catch:{ all -> 0x0897 }
                android.graphics.Paint r13 = r1.roundPaint     // Catch:{ all -> 0x0897 }
                r5.drawRoundRect(r0, r8, r9, r13)     // Catch:{ all -> 0x0897 }
                r5.restore()     // Catch:{ all -> 0x0897 }
                goto L_0x083b
            L_0x087b:
                r5.setBitmap(r8)     // Catch:{ all -> 0x0895 }
                android.view.ViewGroup[] r0 = r1.cells     // Catch:{ all -> 0x0895 }
                r0 = r0[r2]     // Catch:{ all -> 0x0895 }
                if (r4 != 0) goto L_0x0888
                r5 = 2131230791(0x7var_, float:1.8077645E38)
                goto L_0x088b
            L_0x0888:
                r5 = 2131230792(0x7var_, float:1.8077647E38)
            L_0x088b:
                android.view.View r0 = r0.findViewById(r5)     // Catch:{ all -> 0x0895 }
                android.widget.ImageView r0 = (android.widget.ImageView) r0     // Catch:{ all -> 0x0895 }
                r0.setImageBitmap(r14)     // Catch:{ all -> 0x0895 }
                goto L_0x089f
            L_0x0895:
                r0 = move-exception
                goto L_0x089c
            L_0x0897:
                r0 = move-exception
                goto L_0x089b
            L_0x0899:
                r0 = move-exception
                r6 = 0
            L_0x089b:
                r8 = 0
            L_0x089c:
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            L_0x089f:
                int r0 = r3.unread_count
                if (r0 <= 0) goto L_0x08f1
                r3 = 99
                if (r0 <= r3) goto L_0x08b8
                java.lang.Object[] r0 = new java.lang.Object[r11]
                r3 = 99
                java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
                r0[r12] = r3
                java.lang.String r3 = "%d+"
                java.lang.String r0 = java.lang.String.format(r3, r0)
                goto L_0x08c6
            L_0x08b8:
                java.lang.Object[] r3 = new java.lang.Object[r11]
                java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
                r3[r12] = r0
                java.lang.String r0 = "%d"
                java.lang.String r0 = java.lang.String.format(r0, r3)
            L_0x08c6:
                android.view.ViewGroup[] r3 = r1.cells
                r3 = r3[r2]
                if (r4 != 0) goto L_0x08d0
                r5 = 2131230793(0x7var_, float:1.8077649E38)
                goto L_0x08d3
            L_0x08d0:
                r5 = 2131230794(0x7var_a, float:1.807765E38)
            L_0x08d3:
                android.view.View r3 = r3.findViewById(r5)
                android.widget.TextView r3 = (android.widget.TextView) r3
                r3.setText(r0)
                android.view.ViewGroup[] r0 = r1.cells
                r0 = r0[r2]
                if (r4 != 0) goto L_0x08e6
                r3 = 2131230795(0x7var_b, float:1.8077653E38)
                goto L_0x08e9
            L_0x08e6:
                r3 = 2131230796(0x7var_c, float:1.8077655E38)
            L_0x08e9:
                android.view.View r0 = r0.findViewById(r3)
                r0.setVisibility(r12)
                goto L_0x0907
            L_0x08f1:
                android.view.ViewGroup[] r0 = r1.cells
                r0 = r0[r2]
                if (r4 != 0) goto L_0x08fb
                r3 = 2131230795(0x7var_b, float:1.8077653E38)
                goto L_0x08fe
            L_0x08fb:
                r3 = 2131230796(0x7var_c, float:1.8077655E38)
            L_0x08fe:
                android.view.View r0 = r0.findViewById(r3)
                r3 = 8
                r0.setVisibility(r3)
            L_0x0907:
                int r4 = r4 + 1
                r18 = r7
                r3 = 2
                goto L_0x0643
            L_0x090e:
                r7 = r18
                r6 = 0
                r8 = 0
                r10 = 1111490560(0x42400000, float:48.0)
                r16 = 0
                int r2 = r2 + 1
                goto L_0x063f
            L_0x091a:
                android.view.ViewGroup[] r0 = r1.cells
                r0 = r0[r12]
                int r0 = r0.getVisibility()
                if (r0 != 0) goto L_0x0930
                org.telegram.ui.EditWidgetActivity r0 = r1.this$0
                android.widget.ImageView r0 = r0.previewImageView
                r2 = 8
                r0.setVisibility(r2)
                goto L_0x0939
            L_0x0930:
                org.telegram.ui.EditWidgetActivity r0 = r1.this$0
                android.widget.ImageView r0 = r0.previewImageView
                r0.setVisibility(r12)
            L_0x0939:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.EditWidgetActivity.WidgetPreviewCell.updateDialogs():void");
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int i, int i2) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(264.0f), NUM));
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            Drawable cachedWallpaperNonBlocking = Theme.getCachedWallpaperNonBlocking();
            if (!(cachedWallpaperNonBlocking == this.backgroundDrawable || cachedWallpaperNonBlocking == null)) {
                if (Theme.isAnimatingColor()) {
                    this.oldBackgroundDrawable = this.backgroundDrawable;
                    this.oldBackgroundGradientDisposable = this.backgroundGradientDisposable;
                } else {
                    BackgroundGradientDrawable.Disposable disposable = this.backgroundGradientDisposable;
                    if (disposable != null) {
                        disposable.dispose();
                        this.backgroundGradientDisposable = null;
                    }
                }
                this.backgroundDrawable = cachedWallpaperNonBlocking;
            }
            float themeAnimationValue = this.this$0.parentLayout.getThemeAnimationValue();
            int i = 0;
            while (i < 2) {
                Drawable drawable = i == 0 ? this.oldBackgroundDrawable : this.backgroundDrawable;
                if (drawable != null) {
                    if (i != 1 || this.oldBackgroundDrawable == null || this.this$0.parentLayout == null) {
                        drawable.setAlpha(255);
                    } else {
                        drawable.setAlpha((int) (255.0f * themeAnimationValue));
                    }
                    if ((drawable instanceof ColorDrawable) || (drawable instanceof GradientDrawable) || (drawable instanceof MotionBackgroundDrawable)) {
                        drawable.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
                        if (drawable instanceof BackgroundGradientDrawable) {
                            this.backgroundGradientDisposable = ((BackgroundGradientDrawable) drawable).drawExactBoundsSize(canvas, this);
                        } else {
                            drawable.draw(canvas);
                        }
                    } else if (drawable instanceof BitmapDrawable) {
                        if (((BitmapDrawable) drawable).getTileModeX() == Shader.TileMode.REPEAT) {
                            canvas.save();
                            float f = 2.0f / AndroidUtilities.density;
                            canvas.scale(f, f);
                            drawable.setBounds(0, 0, (int) Math.ceil((double) (((float) getMeasuredWidth()) / f)), (int) Math.ceil((double) (((float) getMeasuredHeight()) / f)));
                        } else {
                            int measuredHeight = getMeasuredHeight();
                            float max = Math.max(((float) getMeasuredWidth()) / ((float) drawable.getIntrinsicWidth()), ((float) measuredHeight) / ((float) drawable.getIntrinsicHeight()));
                            int ceil = (int) Math.ceil((double) (((float) drawable.getIntrinsicWidth()) * max));
                            int ceil2 = (int) Math.ceil((double) (((float) drawable.getIntrinsicHeight()) * max));
                            int measuredWidth = (getMeasuredWidth() - ceil) / 2;
                            int i2 = (measuredHeight - ceil2) / 2;
                            canvas.save();
                            canvas.clipRect(0, 0, ceil, getMeasuredHeight());
                            drawable.setBounds(measuredWidth, i2, ceil + measuredWidth, ceil2 + i2);
                        }
                        drawable.draw(canvas);
                        canvas.restore();
                    }
                    if (i == 0 && this.oldBackgroundDrawable != null && themeAnimationValue >= 1.0f) {
                        BackgroundGradientDrawable.Disposable disposable2 = this.oldBackgroundGradientDisposable;
                        if (disposable2 != null) {
                            disposable2.dispose();
                            this.oldBackgroundGradientDisposable = null;
                        }
                        this.oldBackgroundDrawable = null;
                        invalidate();
                    }
                }
                i++;
            }
            this.shadowDrawable.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
            this.shadowDrawable.draw(canvas);
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
    }

    public EditWidgetActivity(int i, int i2, boolean z) {
        this.widgetType = i;
        this.currentWidgetId = i2;
        this.isEdit = z;
        if (z) {
            ArrayList arrayList = new ArrayList();
            ArrayList arrayList2 = new ArrayList();
            getMessagesStorage().getWidgetDialogIds(this.currentWidgetId, this.widgetType, this.selectedDialogs, arrayList, arrayList2, true);
            getMessagesController().putUsers(arrayList, true);
            getMessagesController().putChats(arrayList2, true);
        }
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
            public void onItemClick(int i) {
                if (i == -1) {
                    if (EditWidgetActivity.this.delegate == null) {
                        EditWidgetActivity.this.finishActivity();
                    } else {
                        EditWidgetActivity.this.finishFragment();
                    }
                } else if (i == 1 && EditWidgetActivity.this.getParentActivity() != null) {
                    EditWidgetActivity.this.getMessagesStorage().putWidgetDialogs(EditWidgetActivity.this.currentWidgetId, EditWidgetActivity.this.selectedDialogs);
                    SharedPreferences sharedPreferences = EditWidgetActivity.this.getParentActivity().getSharedPreferences("shortcut_widget", 0);
                    SharedPreferences.Editor edit = sharedPreferences.edit();
                    edit.putInt("account" + EditWidgetActivity.this.currentWidgetId, EditWidgetActivity.this.currentAccount).commit();
                    SharedPreferences.Editor edit2 = sharedPreferences.edit();
                    edit2.putInt("type" + EditWidgetActivity.this.currentWidgetId, EditWidgetActivity.this.widgetType).commit();
                    AppWidgetManager instance = AppWidgetManager.getInstance(EditWidgetActivity.this.getParentActivity());
                    if (EditWidgetActivity.this.widgetType == 0) {
                        ChatsWidgetProvider.updateWidget(EditWidgetActivity.this.getParentActivity(), instance, EditWidgetActivity.this.currentWidgetId, EditWidgetActivity.this.isEdit);
                    } else {
                        ContactsWidgetProvider.updateWidget(EditWidgetActivity.this.getParentActivity(), instance, EditWidgetActivity.this.currentWidgetId, EditWidgetActivity.this.isEdit);
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
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new RecyclerListView.OnItemClickListener(context) {
            public final /* synthetic */ Context f$1;

            {
                this.f$1 = r2;
            }

            public final void onItemClick(View view, int i) {
                EditWidgetActivity.this.lambda$createView$1$EditWidgetActivity(this.f$1, view, i);
            }
        });
        this.listView.setOnItemLongClickListener((RecyclerListView.OnItemLongClickListenerExtended) new RecyclerListView.OnItemLongClickListenerExtended() {
            private Rect rect = new Rect();

            public void onLongClickRelease() {
            }

            public void onMove(float f, float f2) {
            }

            public boolean onItemClick(View view, int i, float f, float f2) {
                if (EditWidgetActivity.this.getParentActivity() != null && (view instanceof GroupCreateUserCell)) {
                    ((ImageView) view.getTag(NUM)).getHitRect(this.rect);
                    if (!this.rect.contains((int) f, (int) f2)) {
                        AlertDialog.Builder builder = new AlertDialog.Builder((Context) EditWidgetActivity.this.getParentActivity());
                        builder.setItems(new CharSequence[]{LocaleController.getString("Delete", NUM)}, new DialogInterface.OnClickListener(i) {
                            public final /* synthetic */ int f$1;

                            {
                                this.f$1 = r2;
                            }

                            public final void onClick(DialogInterface dialogInterface, int i) {
                                EditWidgetActivity.AnonymousClass2.this.lambda$onItemClick$0$EditWidgetActivity$2(this.f$1, dialogInterface, i);
                            }
                        });
                        EditWidgetActivity.this.showDialog(builder.create());
                        return true;
                    }
                }
                return false;
            }

            /* access modifiers changed from: private */
            /* renamed from: lambda$onItemClick$0 */
            public /* synthetic */ void lambda$onItemClick$0$EditWidgetActivity$2(int i, DialogInterface dialogInterface, int i2) {
                if (i2 == 0) {
                    EditWidgetActivity.this.selectedDialogs.remove(i - EditWidgetActivity.this.chatsStartRow);
                    EditWidgetActivity.this.updateRows();
                    if (EditWidgetActivity.this.widgetPreviewCell != null) {
                        EditWidgetActivity.this.widgetPreviewCell.updateDialogs();
                    }
                }
            }
        });
        return this.fragmentView;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$1 */
    public /* synthetic */ void lambda$createView$1$EditWidgetActivity(Context context, View view, int i) {
        if (i == this.selectChatsRow) {
            InviteMembersBottomSheet inviteMembersBottomSheet = new InviteMembersBottomSheet(context, this.currentAccount, (SparseArray<TLObject>) null, 0, this);
            inviteMembersBottomSheet.setDelegate(new InviteMembersBottomSheet.InviteMembersBottomSheetDelegate() {
                public final void didSelectDialogs(ArrayList arrayList) {
                    EditWidgetActivity.this.lambda$createView$0$EditWidgetActivity(arrayList);
                }
            }, this.selectedDialogs);
            inviteMembersBottomSheet.setSelectedContacts(this.selectedDialogs);
            showDialog(inviteMembersBottomSheet);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$createView$0 */
    public /* synthetic */ void lambda$createView$0$EditWidgetActivity(ArrayList arrayList) {
        this.selectedDialogs.clear();
        this.selectedDialogs.addAll(arrayList);
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
            AndroidUtilities.runOnUIThread(new Runnable() {
                public final void run() {
                    EditWidgetActivity.this.removeSelfFromStack();
                }
            }, 1000);
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

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            int itemViewType = viewHolder.getItemViewType();
            return itemViewType == 1 || itemViewType == 3;
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            GroupCreateUserCell groupCreateUserCell;
            if (i == 0) {
                TextInfoPrivacyCell textInfoPrivacyCell = new TextInfoPrivacyCell(this.mContext);
                textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, NUM, "windowBackgroundGrayShadow"));
                groupCreateUserCell = textInfoPrivacyCell;
            } else if (i == 1) {
                TextCell textCell = new TextCell(this.mContext);
                textCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                groupCreateUserCell = textCell;
            } else if (i != 2) {
                GroupCreateUserCell groupCreateUserCell2 = new GroupCreateUserCell(this.mContext, 0, 0, false);
                ImageView imageView = new ImageView(this.mContext);
                imageView.setImageResource(NUM);
                imageView.setScaleType(ImageView.ScaleType.CENTER);
                groupCreateUserCell2.setTag(NUM, imageView);
                groupCreateUserCell2.addView(imageView, LayoutHelper.createFrame(40, -1.0f, (LocaleController.isRTL ? 3 : 5) | 16, 10.0f, 0.0f, 10.0f, 0.0f));
                imageView.setOnTouchListener(new View.OnTouchListener(groupCreateUserCell2) {
                    public final /* synthetic */ GroupCreateUserCell f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final boolean onTouch(View view, MotionEvent motionEvent) {
                        return EditWidgetActivity.ListAdapter.this.lambda$onCreateViewHolder$0$EditWidgetActivity$ListAdapter(this.f$1, view, motionEvent);
                    }
                });
                imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chats_pinnedIcon"), PorterDuff.Mode.MULTIPLY));
                groupCreateUserCell = groupCreateUserCell2;
            } else {
                groupCreateUserCell = EditWidgetActivity.this.widgetPreviewCell = new WidgetPreviewCell(EditWidgetActivity.this, this.mContext);
            }
            return new RecyclerListView.Holder(groupCreateUserCell);
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$onCreateViewHolder$0 */
        public /* synthetic */ boolean lambda$onCreateViewHolder$0$EditWidgetActivity$ListAdapter(GroupCreateUserCell groupCreateUserCell, View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() != 0) {
                return false;
            }
            EditWidgetActivity.this.itemTouchHelper.startDrag(EditWidgetActivity.this.listView.getChildViewHolder(groupCreateUserCell));
            return false;
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            int itemViewType = viewHolder.getItemViewType();
            boolean z = true;
            if (itemViewType == 0) {
                TextInfoPrivacyCell textInfoPrivacyCell = (TextInfoPrivacyCell) viewHolder.itemView;
                if (i == EditWidgetActivity.this.infoRow) {
                    SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
                    if (EditWidgetActivity.this.widgetType == 0) {
                        spannableStringBuilder.append(LocaleController.getString("EditWidgetChatsInfo", NUM));
                    } else if (EditWidgetActivity.this.widgetType == 1) {
                        spannableStringBuilder.append(LocaleController.getString("EditWidgetContactsInfo", NUM));
                    }
                    if (SharedConfig.passcodeHash.length() > 0) {
                        spannableStringBuilder.append("\n\n").append(AndroidUtilities.replaceTags(LocaleController.getString("WidgetPasscode2", NUM)));
                    }
                    textInfoPrivacyCell.setText(spannableStringBuilder);
                }
            } else if (itemViewType == 1) {
                TextCell textCell = (TextCell) viewHolder.itemView;
                textCell.setColors((String) null, "windowBackgroundWhiteBlueText4");
                Drawable drawable = this.mContext.getResources().getDrawable(NUM);
                Drawable drawable2 = this.mContext.getResources().getDrawable(NUM);
                drawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor("switchTrackChecked"), PorterDuff.Mode.MULTIPLY));
                drawable2.setColorFilter(new PorterDuffColorFilter(Theme.getColor("checkboxCheck"), PorterDuff.Mode.MULTIPLY));
                CombinedDrawable combinedDrawable = new CombinedDrawable(drawable, drawable2);
                String string = LocaleController.getString("SelectChats", NUM);
                if (EditWidgetActivity.this.chatsStartRow == -1) {
                    z = false;
                }
                textCell.setTextAndIcon(string, (Drawable) combinedDrawable, z);
                textCell.getImageView().setPadding(0, AndroidUtilities.dp(7.0f), 0, 0);
            } else if (itemViewType == 3) {
                GroupCreateUserCell groupCreateUserCell = (GroupCreateUserCell) viewHolder.itemView;
                int longValue = (int) ((Long) EditWidgetActivity.this.selectedDialogs.get(i - EditWidgetActivity.this.chatsStartRow)).longValue();
                if (longValue > 0) {
                    TLRPC$User user = EditWidgetActivity.this.getMessagesController().getUser(Integer.valueOf(longValue));
                    if (i == EditWidgetActivity.this.chatsEndRow - 1) {
                        z = false;
                    }
                    groupCreateUserCell.setObject(user, (CharSequence) null, (CharSequence) null, z);
                    return;
                }
                TLRPC$Chat chat = EditWidgetActivity.this.getMessagesController().getChat(Integer.valueOf(-longValue));
                if (i == EditWidgetActivity.this.chatsEndRow - 1) {
                    z = false;
                }
                groupCreateUserCell.setObject(chat, (CharSequence) null, (CharSequence) null, z);
            }
        }

        public void onViewAttachedToWindow(RecyclerView.ViewHolder viewHolder) {
            int itemViewType = viewHolder.getItemViewType();
            if (itemViewType == 3 || itemViewType == 1) {
                viewHolder.itemView.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
            }
        }

        public int getItemViewType(int i) {
            if (i == EditWidgetActivity.this.previewRow) {
                return 2;
            }
            if (i == EditWidgetActivity.this.selectChatsRow) {
                return 1;
            }
            return i == EditWidgetActivity.this.infoRow ? 0 : 3;
        }

        public boolean swapElements(int i, int i2) {
            int access$1400 = i - EditWidgetActivity.this.chatsStartRow;
            int access$14002 = i2 - EditWidgetActivity.this.chatsStartRow;
            int access$100 = EditWidgetActivity.this.chatsEndRow - EditWidgetActivity.this.chatsStartRow;
            if (access$1400 < 0 || access$14002 < 0 || access$1400 >= access$100 || access$14002 >= access$100) {
                return false;
            }
            EditWidgetActivity.this.selectedDialogs.set(access$1400, (Long) EditWidgetActivity.this.selectedDialogs.get(access$14002));
            EditWidgetActivity.this.selectedDialogs.set(access$14002, (Long) EditWidgetActivity.this.selectedDialogs.get(access$1400));
            notifyItemMoved(i, i2);
            return true;
        }
    }

    public boolean onBackPressed() {
        if (this.delegate != null) {
            return super.onBackPressed();
        }
        finishActivity();
        return false;
    }

    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{TextCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhite"));
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGray"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefault"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultIcon"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultTitle"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSelector"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUBACKGROUND, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSubmenuBackground"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSubmenuItem"));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM | ThemeDescription.FLAG_IMAGECOLOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "actionBarDefaultSubmenuItemIcon"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, (Class[]) null, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "listSelectorSDK21"));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "divider"));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, (Paint) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundGrayShadow"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteGrayText4"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueText4"));
        arrayList.add(new ThemeDescription((View) this.listView, 0, new Class[]{TextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, "windowBackgroundWhiteBlueText4"));
        return arrayList;
    }
}
