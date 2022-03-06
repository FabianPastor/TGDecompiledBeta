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
                r11 = 2131628890(0x7f0e135a, float:1.8885085E38)
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
                r10 = 2131166226(0x7var_, float:1.7946691E38)
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
                r12 = 2131427349(0x7f0b0015, float:1.8476312E38)
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
                r3 = 2131165363(0x7var_b3, float:1.794494E38)
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
                r3 = 2131165391(0x7var_cf, float:1.7944998E38)
                r1.setImageResource(r3)
            L_0x010e:
                r18.updateDialogs()
                r1 = 2131165472(0x7var_, float:1.7945162E38)
                java.lang.String r3 = "windowBackgroundGrayShadow"
                android.graphics.drawable.Drawable r1 = org.telegram.ui.ActionBar.Theme.getThemedDrawable((android.content.Context) r2, (int) r1, (java.lang.String) r3)
                r0.shadowDrawable = r1
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.EditWidgetActivity.WidgetPreviewCell.<init>(org.telegram.ui.EditWidgetActivity, android.content.Context):void");
        }

        /* JADX WARNING: Code restructure failed: missing block: B:103:0x0284, code lost:
            if ((r0 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChannelMigrateFrom) == false) goto L_0x0287;
         */
        /* JADX WARNING: Removed duplicated region for block: B:18:0x008e  */
        /* JADX WARNING: Removed duplicated region for block: B:19:0x0099  */
        /* JADX WARNING: Removed duplicated region for block: B:230:0x058b  */
        /* JADX WARNING: Removed duplicated region for block: B:237:0x05ce  */
        /* JADX WARNING: Removed duplicated region for block: B:241:0x0623  */
        /* JADX WARNING: Removed duplicated region for block: B:267:0x0712  */
        /* JADX WARNING: Removed duplicated region for block: B:276:0x073b  */
        /* JADX WARNING: Removed duplicated region for block: B:365:0x08d1  */
        /* JADX WARNING: Removed duplicated region for block: B:378:0x0921  */
        /* JADX WARNING: Removed duplicated region for block: B:67:0x016a  */
        /* JADX WARNING: Removed duplicated region for block: B:70:0x0178 A[Catch:{ all -> 0x021c }] */
        /* JADX WARNING: Removed duplicated region for block: B:73:0x018d A[Catch:{ all -> 0x021c }] */
        /* JADX WARNING: Removed duplicated region for block: B:83:0x01b8 A[Catch:{ all -> 0x021c }] */
        /* JADX WARNING: Removed duplicated region for block: B:92:0x0233  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void updateDialogs() {
            /*
                r20 = this;
                r1 = r20
                org.telegram.ui.EditWidgetActivity r0 = r1.this$0
                int r0 = r0.widgetType
                r2 = 1111490560(0x42400000, float:48.0)
                r3 = 2131627763(0x7f0e0ef3, float:1.88828E38)
                java.lang.String r4 = "SavedMessages"
                r5 = 0
                r6 = 0
                r8 = 8
                r9 = 2
                r12 = 0
                if (r0 != 0) goto L_0x0665
                r13 = 0
            L_0x0019:
                if (r13 >= r9) goto L_0x063f
                org.telegram.ui.EditWidgetActivity r0 = r1.this$0
                java.util.ArrayList r0 = r0.selectedDialogs
                boolean r0 = r0.isEmpty()
                if (r0 == 0) goto L_0x0046
                org.telegram.ui.EditWidgetActivity r0 = r1.this$0
                org.telegram.messenger.MessagesController r0 = r0.getMessagesController()
                java.util.ArrayList<org.telegram.tgnet.TLRPC$Dialog> r0 = r0.dialogsServerOnly
                int r0 = r0.size()
                if (r13 >= r0) goto L_0x0044
                org.telegram.ui.EditWidgetActivity r0 = r1.this$0
                org.telegram.messenger.MessagesController r0 = r0.getMessagesController()
                java.util.ArrayList<org.telegram.tgnet.TLRPC$Dialog> r0 = r0.dialogsServerOnly
                java.lang.Object r0 = r0.get(r13)
                org.telegram.tgnet.TLRPC$Dialog r0 = (org.telegram.tgnet.TLRPC$Dialog) r0
                goto L_0x0089
            L_0x0044:
                r0 = 0
                goto L_0x0089
            L_0x0046:
                org.telegram.ui.EditWidgetActivity r0 = r1.this$0
                java.util.ArrayList r0 = r0.selectedDialogs
                int r0 = r0.size()
                if (r13 >= r0) goto L_0x008b
                org.telegram.ui.EditWidgetActivity r0 = r1.this$0
                org.telegram.messenger.MessagesController r0 = r0.getMessagesController()
                androidx.collection.LongSparseArray<org.telegram.tgnet.TLRPC$Dialog> r0 = r0.dialogs_dict
                org.telegram.ui.EditWidgetActivity r14 = r1.this$0
                java.util.ArrayList r14 = r14.selectedDialogs
                java.lang.Object r14 = r14.get(r13)
                java.lang.Long r14 = (java.lang.Long) r14
                long r14 = r14.longValue()
                java.lang.Object r0 = r0.get(r14)
                org.telegram.tgnet.TLRPC$Dialog r0 = (org.telegram.tgnet.TLRPC$Dialog) r0
                if (r0 != 0) goto L_0x0089
                org.telegram.tgnet.TLRPC$TL_dialog r0 = new org.telegram.tgnet.TLRPC$TL_dialog
                r0.<init>()
                org.telegram.ui.EditWidgetActivity r14 = r1.this$0
                java.util.ArrayList r14 = r14.selectedDialogs
                java.lang.Object r14 = r14.get(r13)
                java.lang.Long r14 = (java.lang.Long) r14
                long r14 = r14.longValue()
                r0.id = r14
            L_0x0089:
                r14 = r0
                goto L_0x008c
            L_0x008b:
                r14 = 0
            L_0x008c:
                if (r14 != 0) goto L_0x0099
                android.view.ViewGroup[] r0 = r1.cells
                r0 = r0[r13]
                r0.setVisibility(r8)
                r17 = r4
                goto L_0x0630
            L_0x0099:
                android.view.ViewGroup[] r0 = r1.cells
                r0 = r0[r13]
                r0.setVisibility(r12)
                long r8 = r14.id
                boolean r0 = org.telegram.messenger.DialogObject.isUserDialog(r8)
                java.lang.String r8 = ""
                if (r0 == 0) goto L_0x0122
                org.telegram.ui.EditWidgetActivity r0 = r1.this$0
                org.telegram.messenger.MessagesController r0 = r0.getMessagesController()
                long r10 = r14.id
                java.lang.Long r10 = java.lang.Long.valueOf(r10)
                org.telegram.tgnet.TLRPC$User r0 = r0.getUser(r10)
                if (r0 == 0) goto L_0x011d
                boolean r10 = org.telegram.messenger.UserObject.isUserSelf(r0)
                if (r10 == 0) goto L_0x00c7
                java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r4, r3)
                goto L_0x00ef
            L_0x00c7:
                boolean r10 = org.telegram.messenger.UserObject.isReplyUser((org.telegram.tgnet.TLRPC$User) r0)
                if (r10 == 0) goto L_0x00d7
                r10 = 2131627625(0x7f0e0e69, float:1.888252E38)
                java.lang.String r11 = "RepliesTitle"
                java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r11, r10)
                goto L_0x00ef
            L_0x00d7:
                boolean r10 = org.telegram.messenger.UserObject.isDeleted(r0)
                if (r10 == 0) goto L_0x00e7
                r10 = 2131625950(0x7f0e07de, float:1.8879122E38)
                java.lang.String r11 = "HiddenName"
                java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r11, r10)
                goto L_0x00ef
            L_0x00e7:
                java.lang.String r10 = r0.first_name
                java.lang.String r11 = r0.last_name
                java.lang.String r10 = org.telegram.messenger.ContactsController.formatName(r10, r11)
            L_0x00ef:
                boolean r11 = org.telegram.messenger.UserObject.isReplyUser((org.telegram.tgnet.TLRPC$User) r0)
                if (r11 != 0) goto L_0x0114
                boolean r11 = org.telegram.messenger.UserObject.isUserSelf(r0)
                if (r11 != 0) goto L_0x0114
                org.telegram.tgnet.TLRPC$UserProfilePhoto r11 = r0.photo
                if (r11 == 0) goto L_0x0114
                org.telegram.tgnet.TLRPC$FileLocation r11 = r11.photo_small
                if (r11 == 0) goto L_0x0114
                r17 = r10
                long r9 = r11.volume_id
                int r18 = (r9 > r6 ? 1 : (r9 == r6 ? 0 : -1))
                if (r18 == 0) goto L_0x0116
                int r9 = r11.local_id
                if (r9 == 0) goto L_0x0116
                r9 = r11
                r10 = r17
                r3 = 0
                goto L_0x011a
            L_0x0114:
                r17 = r10
            L_0x0116:
                r10 = r17
                r3 = 0
                r9 = 0
            L_0x011a:
                r17 = r4
                goto L_0x0158
            L_0x011d:
                r17 = r4
                r10 = r8
                r3 = 0
                goto L_0x0157
            L_0x0122:
                org.telegram.ui.EditWidgetActivity r0 = r1.this$0
                org.telegram.messenger.MessagesController r0 = r0.getMessagesController()
                long r9 = r14.id
                long r9 = -r9
                java.lang.Long r9 = java.lang.Long.valueOf(r9)
                org.telegram.tgnet.TLRPC$Chat r9 = r0.getChat(r9)
                if (r9 == 0) goto L_0x0152
                java.lang.String r10 = r9.title
                org.telegram.tgnet.TLRPC$ChatPhoto r0 = r9.photo
                if (r0 == 0) goto L_0x014f
                org.telegram.tgnet.TLRPC$FileLocation r0 = r0.photo_small
                if (r0 == 0) goto L_0x014f
                r17 = r4
                long r3 = r0.volume_id
                int r18 = (r3 > r6 ? 1 : (r3 == r6 ? 0 : -1))
                if (r18 == 0) goto L_0x0155
                int r3 = r0.local_id
                if (r3 == 0) goto L_0x0155
                r3 = r9
                r9 = r0
                r0 = 0
                goto L_0x0158
            L_0x014f:
                r17 = r4
                goto L_0x0155
            L_0x0152:
                r17 = r4
                r10 = r8
            L_0x0155:
                r3 = r9
                r0 = 0
            L_0x0157:
                r9 = 0
            L_0x0158:
                android.view.ViewGroup[] r4 = r1.cells
                r4 = r4[r13]
                r11 = 2131230910(0x7var_be, float:1.8077886E38)
                android.view.View r4 = r4.findViewById(r11)
                android.widget.TextView r4 = (android.widget.TextView) r4
                r4.setText(r10)
                if (r9 == 0) goto L_0x0178
                r4 = 1
                java.io.File r9 = org.telegram.messenger.FileLoader.getPathToAttach(r9, r4)     // Catch:{ all -> 0x021c }
                java.lang.String r4 = r9.toString()     // Catch:{ all -> 0x021c }
                android.graphics.Bitmap r9 = android.graphics.BitmapFactory.decodeFile(r4)     // Catch:{ all -> 0x021c }
                goto L_0x0179
            L_0x0178:
                r9 = 0
            L_0x0179:
                int r4 = org.telegram.messenger.AndroidUtilities.dp(r2)     // Catch:{ all -> 0x021c }
                android.graphics.Bitmap$Config r10 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ all -> 0x021c }
                android.graphics.Bitmap r10 = android.graphics.Bitmap.createBitmap(r4, r4, r10)     // Catch:{ all -> 0x021c }
                r10.eraseColor(r12)     // Catch:{ all -> 0x021c }
                android.graphics.Canvas r11 = new android.graphics.Canvas     // Catch:{ all -> 0x021c }
                r11.<init>(r10)     // Catch:{ all -> 0x021c }
                if (r9 != 0) goto L_0x01b8
                if (r0 == 0) goto L_0x01ab
                org.telegram.ui.Components.AvatarDrawable r9 = new org.telegram.ui.Components.AvatarDrawable     // Catch:{ all -> 0x021c }
                r9.<init>((org.telegram.tgnet.TLRPC$User) r0)     // Catch:{ all -> 0x021c }
                boolean r19 = org.telegram.messenger.UserObject.isReplyUser((org.telegram.tgnet.TLRPC$User) r0)     // Catch:{ all -> 0x021c }
                if (r19 == 0) goto L_0x01a0
                r0 = 12
                r9.setAvatarType(r0)     // Catch:{ all -> 0x021c }
                goto L_0x01b0
            L_0x01a0:
                boolean r0 = org.telegram.messenger.UserObject.isUserSelf(r0)     // Catch:{ all -> 0x021c }
                if (r0 == 0) goto L_0x01b0
                r15 = 1
                r9.setAvatarType(r15)     // Catch:{ all -> 0x021c }
                goto L_0x01b0
            L_0x01ab:
                org.telegram.ui.Components.AvatarDrawable r9 = new org.telegram.ui.Components.AvatarDrawable     // Catch:{ all -> 0x021c }
                r9.<init>((org.telegram.tgnet.TLRPC$Chat) r3)     // Catch:{ all -> 0x021c }
            L_0x01b0:
                r9.setBounds(r12, r12, r4, r4)     // Catch:{ all -> 0x021c }
                r9.draw(r11)     // Catch:{ all -> 0x021c }
            L_0x01b6:
                r2 = 0
                goto L_0x0208
            L_0x01b8:
                android.graphics.BitmapShader r0 = new android.graphics.BitmapShader     // Catch:{ all -> 0x021c }
                android.graphics.Shader$TileMode r15 = android.graphics.Shader.TileMode.CLAMP     // Catch:{ all -> 0x021c }
                r0.<init>(r9, r15, r15)     // Catch:{ all -> 0x021c }
                android.graphics.Paint r15 = r1.roundPaint     // Catch:{ all -> 0x021c }
                if (r15 != 0) goto L_0x01d2
                android.graphics.Paint r15 = new android.graphics.Paint     // Catch:{ all -> 0x021c }
                r2 = 1
                r15.<init>(r2)     // Catch:{ all -> 0x021c }
                r1.roundPaint = r15     // Catch:{ all -> 0x021c }
                android.graphics.RectF r2 = new android.graphics.RectF     // Catch:{ all -> 0x021c }
                r2.<init>()     // Catch:{ all -> 0x021c }
                r1.bitmapRect = r2     // Catch:{ all -> 0x021c }
            L_0x01d2:
                float r2 = (float) r4     // Catch:{ all -> 0x021c }
                int r4 = r9.getWidth()     // Catch:{ all -> 0x021c }
                float r4 = (float) r4     // Catch:{ all -> 0x021c }
                float r2 = r2 / r4
                r11.save()     // Catch:{ all -> 0x021c }
                r11.scale(r2, r2)     // Catch:{ all -> 0x021c }
                android.graphics.Paint r2 = r1.roundPaint     // Catch:{ all -> 0x021c }
                r2.setShader(r0)     // Catch:{ all -> 0x021c }
                android.graphics.RectF r0 = r1.bitmapRect     // Catch:{ all -> 0x021c }
                int r2 = r9.getWidth()     // Catch:{ all -> 0x021c }
                float r2 = (float) r2     // Catch:{ all -> 0x021c }
                int r4 = r9.getHeight()     // Catch:{ all -> 0x021c }
                float r4 = (float) r4     // Catch:{ all -> 0x021c }
                r0.set(r5, r5, r2, r4)     // Catch:{ all -> 0x021c }
                android.graphics.RectF r0 = r1.bitmapRect     // Catch:{ all -> 0x021c }
                int r2 = r9.getWidth()     // Catch:{ all -> 0x021c }
                float r2 = (float) r2     // Catch:{ all -> 0x021c }
                int r4 = r9.getHeight()     // Catch:{ all -> 0x021c }
                float r4 = (float) r4     // Catch:{ all -> 0x021c }
                android.graphics.Paint r9 = r1.roundPaint     // Catch:{ all -> 0x021c }
                r11.drawRoundRect(r0, r2, r4, r9)     // Catch:{ all -> 0x021c }
                r11.restore()     // Catch:{ all -> 0x021c }
                goto L_0x01b6
            L_0x0208:
                r11.setBitmap(r2)     // Catch:{ all -> 0x021c }
                android.view.ViewGroup[] r0 = r1.cells     // Catch:{ all -> 0x021c }
                r0 = r0[r13]     // Catch:{ all -> 0x021c }
                r2 = 2131230906(0x7var_ba, float:1.8077878E38)
                android.view.View r0 = r0.findViewById(r2)     // Catch:{ all -> 0x021c }
                android.widget.ImageView r0 = (android.widget.ImageView) r0     // Catch:{ all -> 0x021c }
                r0.setImageBitmap(r10)     // Catch:{ all -> 0x021c }
                goto L_0x0220
            L_0x021c:
                r0 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            L_0x0220:
                org.telegram.ui.EditWidgetActivity r0 = r1.this$0
                org.telegram.messenger.MessagesController r0 = r0.getMessagesController()
                androidx.collection.LongSparseArray<org.telegram.messenger.MessageObject> r0 = r0.dialogMessage
                long r10 = r14.id
                java.lang.Object r0 = r0.get(r10)
                r2 = r0
                org.telegram.messenger.MessageObject r2 = (org.telegram.messenger.MessageObject) r2
                if (r2 == 0) goto L_0x058b
                long r4 = r2.getFromChatId()
                int r0 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
                if (r0 <= 0) goto L_0x024c
                org.telegram.ui.EditWidgetActivity r0 = r1.this$0
                org.telegram.messenger.MessagesController r0 = r0.getMessagesController()
                java.lang.Long r4 = java.lang.Long.valueOf(r4)
                org.telegram.tgnet.TLRPC$User r0 = r0.getUser(r4)
                r4 = r0
                r0 = 0
                goto L_0x025c
            L_0x024c:
                org.telegram.ui.EditWidgetActivity r0 = r1.this$0
                org.telegram.messenger.MessagesController r0 = r0.getMessagesController()
                long r4 = -r4
                java.lang.Long r4 = java.lang.Long.valueOf(r4)
                org.telegram.tgnet.TLRPC$Chat r0 = r0.getChat(r4)
                r4 = 0
            L_0x025c:
                android.content.Context r5 = r20.getContext()
                android.content.res.Resources r5 = r5.getResources()
                r9 = 2131034146(0x7var_, float:1.7678801E38)
                int r5 = r5.getColor(r9)
                org.telegram.tgnet.TLRPC$Message r9 = r2.messageOwner
                boolean r9 = r9 instanceof org.telegram.tgnet.TLRPC$TL_messageService
                r11 = 2131034141(0x7var_d, float:1.7678791E38)
                if (r9 == 0) goto L_0x0297
                boolean r0 = org.telegram.messenger.ChatObject.isChannel(r3)
                if (r0 == 0) goto L_0x0287
                org.telegram.tgnet.TLRPC$Message r0 = r2.messageOwner
                org.telegram.tgnet.TLRPC$MessageAction r0 = r0.action
                boolean r3 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageActionHistoryClear
                if (r3 != 0) goto L_0x0289
                boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChannelMigrateFrom
                if (r0 == 0) goto L_0x0287
                goto L_0x0289
            L_0x0287:
                java.lang.CharSequence r8 = r2.messageText
            L_0x0289:
                android.content.Context r0 = r20.getContext()
                android.content.res.Resources r0 = r0.getResources()
                int r5 = r0.getColor(r11)
                goto L_0x0550
            L_0x0297:
                if (r3 == 0) goto L_0x0461
                long r10 = r3.id
                int r9 = (r10 > r6 ? 1 : (r10 == r6 ? 0 : -1))
                if (r9 <= 0) goto L_0x0461
                if (r0 != 0) goto L_0x0461
                boolean r0 = org.telegram.messenger.ChatObject.isChannel(r3)
                if (r0 == 0) goto L_0x02ad
                boolean r0 = org.telegram.messenger.ChatObject.isMegagroup(r3)
                if (r0 == 0) goto L_0x0461
            L_0x02ad:
                boolean r0 = r2.isOutOwner()
                if (r0 == 0) goto L_0x02be
                r0 = 2131625857(0x7f0e0781, float:1.8878934E38)
                java.lang.String r3 = "FromYou"
                java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r3, r0)
            L_0x02bc:
                r3 = r0
                goto L_0x02ce
            L_0x02be:
                if (r4 == 0) goto L_0x02cb
                java.lang.String r0 = org.telegram.messenger.UserObject.getFirstName(r4)
                java.lang.String r3 = "\n"
                java.lang.String r0 = r0.replace(r3, r8)
                goto L_0x02bc
            L_0x02cb:
                java.lang.String r0 = "DELETED"
                goto L_0x02bc
            L_0x02ce:
                java.lang.String r0 = "%2$s: ⁨%1$s⁩"
                java.lang.CharSequence r4 = r2.caption
                r9 = 32
                r10 = 10
                r11 = 150(0x96, float:2.1E-43)
                if (r4 == 0) goto L_0x0334
                java.lang.String r4 = r4.toString()
                int r8 = r4.length()
                if (r8 <= r11) goto L_0x02e8
                java.lang.String r4 = r4.substring(r12, r11)
            L_0x02e8:
                boolean r8 = r2.isVideo()
                if (r8 == 0) goto L_0x02f2
                java.lang.String r8 = "📹 "
            L_0x02f0:
                r11 = 2
                goto L_0x0310
            L_0x02f2:
                boolean r8 = r2.isVoice()
                if (r8 == 0) goto L_0x02fb
                java.lang.String r8 = "🎤 "
                goto L_0x02f0
            L_0x02fb:
                boolean r8 = r2.isMusic()
                if (r8 == 0) goto L_0x0304
                java.lang.String r8 = "🎧 "
                goto L_0x02f0
            L_0x0304:
                boolean r8 = r2.isPhoto()
                if (r8 == 0) goto L_0x030d
                java.lang.String r8 = "🖼 "
                goto L_0x02f0
            L_0x030d:
                java.lang.String r8 = "📎 "
                goto L_0x02f0
            L_0x0310:
                java.lang.Object[] r15 = new java.lang.Object[r11]
                java.lang.StringBuilder r11 = new java.lang.StringBuilder
                r11.<init>()
                r11.append(r8)
                java.lang.String r4 = r4.replace(r10, r9)
                r11.append(r4)
                java.lang.String r4 = r11.toString()
                r15[r12] = r4
                r4 = 1
                r15[r4] = r3
                java.lang.String r0 = java.lang.String.format(r0, r15)
                android.text.SpannableStringBuilder r0 = android.text.SpannableStringBuilder.valueOf(r0)
                goto L_0x0446
            L_0x0334:
                org.telegram.tgnet.TLRPC$Message r4 = r2.messageOwner
                org.telegram.tgnet.TLRPC$MessageMedia r4 = r4.media
                if (r4 == 0) goto L_0x0419
                boolean r4 = r2.isMediaEmpty()
                if (r4 != 0) goto L_0x0419
                android.content.Context r4 = r20.getContext()
                android.content.res.Resources r4 = r4.getResources()
                r5 = 2131034141(0x7var_d, float:1.7678791E38)
                int r4 = r4.getColor(r5)
                org.telegram.tgnet.TLRPC$Message r5 = r2.messageOwner
                org.telegram.tgnet.TLRPC$MessageMedia r5 = r5.media
                boolean r8 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
                r11 = 18
                if (r8 == 0) goto L_0x037f
                org.telegram.tgnet.TLRPC$TL_messageMediaPoll r5 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r5
                int r8 = android.os.Build.VERSION.SDK_INT
                if (r8 < r11) goto L_0x036f
                r8 = 1
                java.lang.Object[] r11 = new java.lang.Object[r8]
                org.telegram.tgnet.TLRPC$Poll r5 = r5.poll
                java.lang.String r5 = r5.question
                r11[r12] = r5
                java.lang.String r5 = "📊 ⁨%s⁩"
                java.lang.String r5 = java.lang.String.format(r5, r11)
                goto L_0x03a6
            L_0x036f:
                r8 = 1
                java.lang.Object[] r11 = new java.lang.Object[r8]
                org.telegram.tgnet.TLRPC$Poll r5 = r5.poll
                java.lang.String r5 = r5.question
                r11[r12] = r5
                java.lang.String r5 = "📊 %s"
                java.lang.String r5 = java.lang.String.format(r5, r11)
                goto L_0x03a6
            L_0x037f:
                boolean r8 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
                if (r8 == 0) goto L_0x03a8
                int r8 = android.os.Build.VERSION.SDK_INT
                if (r8 < r11) goto L_0x0397
                r8 = 1
                java.lang.Object[] r11 = new java.lang.Object[r8]
                org.telegram.tgnet.TLRPC$TL_game r5 = r5.game
                java.lang.String r5 = r5.title
                r11[r12] = r5
                java.lang.String r5 = "🎮 ⁨%s⁩"
                java.lang.String r5 = java.lang.String.format(r5, r11)
                goto L_0x03a6
            L_0x0397:
                r8 = 1
                java.lang.Object[] r11 = new java.lang.Object[r8]
                org.telegram.tgnet.TLRPC$TL_game r5 = r5.game
                java.lang.String r5 = r5.title
                r11[r12] = r5
                java.lang.String r5 = "🎮 %s"
                java.lang.String r5 = java.lang.String.format(r5, r11)
            L_0x03a6:
                r15 = 1
                goto L_0x03e8
            L_0x03a8:
                int r5 = r2.type
                r8 = 14
                if (r5 != r8) goto L_0x03e1
                int r5 = android.os.Build.VERSION.SDK_INT
                if (r5 < r11) goto L_0x03ca
                r5 = 2
                java.lang.Object[] r8 = new java.lang.Object[r5]
                java.lang.String r11 = r2.getMusicAuthor()
                r8[r12] = r11
                java.lang.String r11 = r2.getMusicTitle()
                r15 = 1
                r8[r15] = r11
                java.lang.String r11 = "🎧 ⁨%s - %s⁩"
                java.lang.String r8 = java.lang.String.format(r11, r8)
                r5 = r8
                goto L_0x03e8
            L_0x03ca:
                r5 = 2
                r15 = 1
                java.lang.Object[] r8 = new java.lang.Object[r5]
                java.lang.String r5 = r2.getMusicAuthor()
                r8[r12] = r5
                java.lang.String r5 = r2.getMusicTitle()
                r8[r15] = r5
                java.lang.String r5 = "🎧 %s - %s"
                java.lang.String r5 = java.lang.String.format(r5, r8)
                goto L_0x03e8
            L_0x03e1:
                r15 = 1
                java.lang.CharSequence r5 = r2.messageText
                java.lang.String r5 = r5.toString()
            L_0x03e8:
                java.lang.String r5 = r5.replace(r10, r9)
                r8 = 2
                java.lang.Object[] r9 = new java.lang.Object[r8]
                r9[r12] = r5
                r9[r15] = r3
                java.lang.String r0 = java.lang.String.format(r0, r9)
                android.text.SpannableStringBuilder r5 = android.text.SpannableStringBuilder.valueOf(r0)
                org.telegram.ui.Components.ForegroundColorSpanThemable r0 = new org.telegram.ui.Components.ForegroundColorSpanThemable     // Catch:{ Exception -> 0x0412 }
                java.lang.String r8 = "chats_attachMessage"
                r0.<init>(r8)     // Catch:{ Exception -> 0x0412 }
                int r8 = r3.length()     // Catch:{ Exception -> 0x0412 }
                r9 = 2
                int r8 = r8 + r9
                int r9 = r5.length()     // Catch:{ Exception -> 0x0412 }
                r10 = 33
                r5.setSpan(r0, r8, r9, r10)     // Catch:{ Exception -> 0x0412 }
                goto L_0x0416
            L_0x0412:
                r0 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            L_0x0416:
                r8 = r5
                r5 = r4
                goto L_0x0447
            L_0x0419:
                org.telegram.tgnet.TLRPC$Message r4 = r2.messageOwner
                java.lang.String r4 = r4.message
                if (r4 == 0) goto L_0x0442
                int r8 = r4.length()
                if (r8 <= r11) goto L_0x0429
                java.lang.String r4 = r4.substring(r12, r11)
            L_0x0429:
                java.lang.String r4 = r4.replace(r10, r9)
                java.lang.String r4 = r4.trim()
                r8 = 2
                java.lang.Object[] r9 = new java.lang.Object[r8]
                r9[r12] = r4
                r4 = 1
                r9[r4] = r3
                java.lang.String r0 = java.lang.String.format(r0, r9)
                android.text.SpannableStringBuilder r0 = android.text.SpannableStringBuilder.valueOf(r0)
                goto L_0x0446
            L_0x0442:
                android.text.SpannableStringBuilder r0 = android.text.SpannableStringBuilder.valueOf(r8)
            L_0x0446:
                r8 = r0
            L_0x0447:
                org.telegram.ui.Components.ForegroundColorSpanThemable r0 = new org.telegram.ui.Components.ForegroundColorSpanThemable     // Catch:{ Exception -> 0x045b }
                java.lang.String r4 = "chats_nameMessage"
                r0.<init>(r4)     // Catch:{ Exception -> 0x045b }
                int r3 = r3.length()     // Catch:{ Exception -> 0x045b }
                r4 = 1
                int r3 = r3 + r4
                r4 = 33
                r8.setSpan(r0, r12, r3, r4)     // Catch:{ Exception -> 0x045b }
                goto L_0x0550
            L_0x045b:
                r0 = move-exception
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
                goto L_0x0550
            L_0x0461:
                org.telegram.tgnet.TLRPC$Message r0 = r2.messageOwner
                org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
                boolean r3 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto
                if (r3 == 0) goto L_0x047e
                org.telegram.tgnet.TLRPC$Photo r3 = r0.photo
                boolean r3 = r3 instanceof org.telegram.tgnet.TLRPC$TL_photoEmpty
                if (r3 == 0) goto L_0x047e
                int r3 = r0.ttl_seconds
                if (r3 == 0) goto L_0x047e
                r0 = 2131624431(0x7f0e01ef, float:1.8876042E38)
                java.lang.String r3 = "AttachPhotoExpired"
                java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r3, r0)
                goto L_0x0550
            L_0x047e:
                boolean r3 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
                if (r3 == 0) goto L_0x0497
                org.telegram.tgnet.TLRPC$Document r3 = r0.document
                boolean r3 = r3 instanceof org.telegram.tgnet.TLRPC$TL_documentEmpty
                if (r3 == 0) goto L_0x0497
                int r3 = r0.ttl_seconds
                if (r3 == 0) goto L_0x0497
                r0 = 2131624437(0x7f0e01f5, float:1.8876054E38)
                java.lang.String r3 = "AttachVideoExpired"
                java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r3, r0)
                goto L_0x0550
            L_0x0497:
                java.lang.CharSequence r3 = r2.caption
                if (r3 == 0) goto L_0x04d4
                boolean r0 = r2.isVideo()
                if (r0 == 0) goto L_0x04a4
                java.lang.String r0 = "📹 "
                goto L_0x04c1
            L_0x04a4:
                boolean r0 = r2.isVoice()
                if (r0 == 0) goto L_0x04ad
                java.lang.String r0 = "🎤 "
                goto L_0x04c1
            L_0x04ad:
                boolean r0 = r2.isMusic()
                if (r0 == 0) goto L_0x04b6
                java.lang.String r0 = "🎧 "
                goto L_0x04c1
            L_0x04b6:
                boolean r0 = r2.isPhoto()
                if (r0 == 0) goto L_0x04bf
                java.lang.String r0 = "🖼 "
                goto L_0x04c1
            L_0x04bf:
                java.lang.String r0 = "📎 "
            L_0x04c1:
                java.lang.StringBuilder r3 = new java.lang.StringBuilder
                r3.<init>()
                r3.append(r0)
                java.lang.CharSequence r0 = r2.caption
                r3.append(r0)
                java.lang.String r8 = r3.toString()
                goto L_0x0550
            L_0x04d4:
                boolean r3 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
                if (r3 == 0) goto L_0x04f1
                org.telegram.tgnet.TLRPC$TL_messageMediaPoll r0 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r0
                java.lang.StringBuilder r3 = new java.lang.StringBuilder
                r3.<init>()
                java.lang.String r4 = "📊 "
                r3.append(r4)
                org.telegram.tgnet.TLRPC$Poll r0 = r0.poll
                java.lang.String r0 = r0.question
                r3.append(r0)
                java.lang.String r0 = r3.toString()
            L_0x04ef:
                r8 = r0
                goto L_0x0535
            L_0x04f1:
                boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
                if (r0 == 0) goto L_0x050f
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
                goto L_0x04ef
            L_0x050f:
                int r0 = r2.type
                r3 = 14
                if (r0 != r3) goto L_0x052c
                r3 = 2
                java.lang.Object[] r0 = new java.lang.Object[r3]
                java.lang.String r3 = r2.getMusicAuthor()
                r0[r12] = r3
                java.lang.String r3 = r2.getMusicTitle()
                r4 = 1
                r0[r4] = r3
                java.lang.String r3 = "🎧 %s - %s"
                java.lang.String r0 = java.lang.String.format(r3, r0)
                goto L_0x04ef
            L_0x052c:
                java.lang.CharSequence r0 = r2.messageText
                java.util.ArrayList<java.lang.String> r3 = r2.highlightedWords
                r4 = 0
                org.telegram.messenger.AndroidUtilities.highlightText((java.lang.CharSequence) r0, (java.util.ArrayList<java.lang.String>) r3, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r4)
                goto L_0x04ef
            L_0x0535:
                org.telegram.tgnet.TLRPC$Message r0 = r2.messageOwner
                org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
                if (r0 == 0) goto L_0x0550
                boolean r0 = r2.isMediaEmpty()
                if (r0 != 0) goto L_0x0550
                android.content.Context r0 = r20.getContext()
                android.content.res.Resources r0 = r0.getResources()
                r3 = 2131034141(0x7var_d, float:1.7678791E38)
                int r5 = r0.getColor(r3)
            L_0x0550:
                android.view.ViewGroup[] r0 = r1.cells
                r0 = r0[r13]
                r3 = 2131230911(0x7var_bf, float:1.8077888E38)
                android.view.View r0 = r0.findViewById(r3)
                android.widget.TextView r0 = (android.widget.TextView) r0
                org.telegram.tgnet.TLRPC$Message r2 = r2.messageOwner
                int r2 = r2.date
                long r2 = (long) r2
                java.lang.String r2 = org.telegram.messenger.LocaleController.stringForMessageListDate(r2)
                r0.setText(r2)
                android.view.ViewGroup[] r0 = r1.cells
                r0 = r0[r13]
                r2 = 2131230909(0x7var_bd, float:1.8077884E38)
                android.view.View r0 = r0.findViewById(r2)
                android.widget.TextView r0 = (android.widget.TextView) r0
                java.lang.String r3 = r8.toString()
                r0.setText(r3)
                android.view.ViewGroup[] r0 = r1.cells
                r0 = r0[r13]
                android.view.View r0 = r0.findViewById(r2)
                android.widget.TextView r0 = (android.widget.TextView) r0
                r0.setTextColor(r5)
                goto L_0x05c7
            L_0x058b:
                int r0 = r14.last_message_date
                if (r0 == 0) goto L_0x05a7
                android.view.ViewGroup[] r0 = r1.cells
                r0 = r0[r13]
                r2 = 2131230911(0x7var_bf, float:1.8077888E38)
                android.view.View r0 = r0.findViewById(r2)
                android.widget.TextView r0 = (android.widget.TextView) r0
                int r2 = r14.last_message_date
                long r2 = (long) r2
                java.lang.String r2 = org.telegram.messenger.LocaleController.stringForMessageListDate(r2)
                r0.setText(r2)
                goto L_0x05b7
            L_0x05a7:
                r2 = 2131230911(0x7var_bf, float:1.8077888E38)
                android.view.ViewGroup[] r0 = r1.cells
                r0 = r0[r13]
                android.view.View r0 = r0.findViewById(r2)
                android.widget.TextView r0 = (android.widget.TextView) r0
                r0.setText(r8)
            L_0x05b7:
                android.view.ViewGroup[] r0 = r1.cells
                r0 = r0[r13]
                r2 = 2131230909(0x7var_bd, float:1.8077884E38)
                android.view.View r0 = r0.findViewById(r2)
                android.widget.TextView r0 = (android.widget.TextView) r0
                r0.setText(r8)
            L_0x05c7:
                int r0 = r14.unread_count
                r2 = 2131230907(0x7var_bb, float:1.807788E38)
                if (r0 <= 0) goto L_0x0623
                android.view.ViewGroup[] r0 = r1.cells
                r0 = r0[r13]
                android.view.View r0 = r0.findViewById(r2)
                android.widget.TextView r0 = (android.widget.TextView) r0
                r3 = 1
                java.lang.Object[] r4 = new java.lang.Object[r3]
                int r3 = r14.unread_count
                java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
                r4[r12] = r3
                java.lang.String r3 = "%d"
                java.lang.String r3 = java.lang.String.format(r3, r4)
                r0.setText(r3)
                android.view.ViewGroup[] r0 = r1.cells
                r0 = r0[r13]
                android.view.View r0 = r0.findViewById(r2)
                r0.setVisibility(r12)
                org.telegram.ui.EditWidgetActivity r0 = r1.this$0
                org.telegram.messenger.MessagesController r0 = r0.getMessagesController()
                long r3 = r14.id
                boolean r0 = r0.isDialogMuted(r3)
                if (r0 == 0) goto L_0x0614
                android.view.ViewGroup[] r0 = r1.cells
                r0 = r0[r13]
                android.view.View r0 = r0.findViewById(r2)
                r2 = 2131166230(0x7var_, float:1.79467E38)
                r0.setBackgroundResource(r2)
                goto L_0x0630
            L_0x0614:
                android.view.ViewGroup[] r0 = r1.cells
                r0 = r0[r13]
                android.view.View r0 = r0.findViewById(r2)
                r2 = 2131166229(0x7var_, float:1.7946697E38)
                r0.setBackgroundResource(r2)
                goto L_0x0630
            L_0x0623:
                android.view.ViewGroup[] r0 = r1.cells
                r0 = r0[r13]
                android.view.View r0 = r0.findViewById(r2)
                r2 = 8
                r0.setVisibility(r2)
            L_0x0630:
                int r13 = r13 + 1
                r4 = r17
                r2 = 1111490560(0x42400000, float:48.0)
                r3 = 2131627763(0x7f0e0ef3, float:1.88828E38)
                r5 = 0
                r8 = 8
                r9 = 2
                goto L_0x0019
            L_0x063f:
                android.view.ViewGroup[] r0 = r1.cells
                r0 = r0[r12]
                r2 = 2131230908(0x7var_bc, float:1.8077882E38)
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
                goto L_0x094f
            L_0x0665:
                r17 = r4
                r4 = 1
                org.telegram.ui.EditWidgetActivity r0 = r1.this$0
                int r0 = r0.widgetType
                if (r0 != r4) goto L_0x094f
                r2 = 0
                r3 = 2
            L_0x0672:
                if (r2 >= r3) goto L_0x094f
                r4 = 0
            L_0x0675:
                if (r4 >= r3) goto L_0x0941
                int r0 = r2 * 2
                int r0 = r0 + r4
                org.telegram.ui.EditWidgetActivity r3 = r1.this$0
                java.util.ArrayList r3 = r3.selectedDialogs
                boolean r3 = r3.isEmpty()
                if (r3 == 0) goto L_0x06c0
                org.telegram.ui.EditWidgetActivity r3 = r1.this$0
                org.telegram.messenger.MediaDataController r3 = r3.getMediaDataController()
                java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_topPeer> r3 = r3.hints
                int r3 = r3.size()
                if (r0 >= r3) goto L_0x0709
                org.telegram.ui.EditWidgetActivity r3 = r1.this$0
                org.telegram.messenger.MediaDataController r3 = r3.getMediaDataController()
                java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_topPeer> r3 = r3.hints
                java.lang.Object r3 = r3.get(r0)
                org.telegram.tgnet.TLRPC$TL_topPeer r3 = (org.telegram.tgnet.TLRPC$TL_topPeer) r3
                org.telegram.tgnet.TLRPC$Peer r3 = r3.peer
                long r10 = r3.user_id
                org.telegram.ui.EditWidgetActivity r3 = r1.this$0
                org.telegram.messenger.MessagesController r3 = r3.getMessagesController()
                androidx.collection.LongSparseArray<org.telegram.tgnet.TLRPC$Dialog> r3 = r3.dialogs_dict
                java.lang.Object r3 = r3.get(r10)
                org.telegram.tgnet.TLRPC$Dialog r3 = (org.telegram.tgnet.TLRPC$Dialog) r3
                if (r3 != 0) goto L_0x06bd
                org.telegram.tgnet.TLRPC$TL_dialog r3 = new org.telegram.tgnet.TLRPC$TL_dialog
                r3.<init>()
                r3.id = r10
            L_0x06bd:
                r16 = r3
                goto L_0x0706
            L_0x06c0:
                org.telegram.ui.EditWidgetActivity r3 = r1.this$0
                java.util.ArrayList r3 = r3.selectedDialogs
                int r3 = r3.size()
                if (r0 >= r3) goto L_0x0709
                org.telegram.ui.EditWidgetActivity r3 = r1.this$0
                org.telegram.messenger.MessagesController r3 = r3.getMessagesController()
                androidx.collection.LongSparseArray<org.telegram.tgnet.TLRPC$Dialog> r3 = r3.dialogs_dict
                org.telegram.ui.EditWidgetActivity r5 = r1.this$0
                java.util.ArrayList r5 = r5.selectedDialogs
                java.lang.Object r5 = r5.get(r0)
                java.lang.Long r5 = (java.lang.Long) r5
                long r10 = r5.longValue()
                java.lang.Object r3 = r3.get(r10)
                r16 = r3
                org.telegram.tgnet.TLRPC$Dialog r16 = (org.telegram.tgnet.TLRPC$Dialog) r16
                if (r16 != 0) goto L_0x0706
                org.telegram.tgnet.TLRPC$TL_dialog r3 = new org.telegram.tgnet.TLRPC$TL_dialog
                r3.<init>()
                org.telegram.ui.EditWidgetActivity r5 = r1.this$0
                java.util.ArrayList r5 = r5.selectedDialogs
                java.lang.Object r5 = r5.get(r0)
                java.lang.Long r5 = (java.lang.Long) r5
                long r10 = r5.longValue()
                r3.id = r10
                goto L_0x070a
            L_0x0706:
                r3 = r16
                goto L_0x070a
            L_0x0709:
                r3 = 0
            L_0x070a:
                r5 = 2131230790(0x7var_, float:1.8077643E38)
                r8 = 2131230791(0x7var_, float:1.8077645E38)
                if (r3 != 0) goto L_0x073b
                android.view.ViewGroup[] r3 = r1.cells
                r3 = r3[r2]
                if (r4 != 0) goto L_0x0719
                goto L_0x071c
            L_0x0719:
                r5 = 2131230791(0x7var_, float:1.8077645E38)
            L_0x071c:
                android.view.View r3 = r3.findViewById(r5)
                r5 = 4
                r3.setVisibility(r5)
                if (r0 == 0) goto L_0x0729
                r3 = 2
                if (r0 != r3) goto L_0x0732
            L_0x0729:
                android.view.ViewGroup[] r0 = r1.cells
                r0 = r0[r2]
                r3 = 8
                r0.setVisibility(r3)
            L_0x0732:
                r10 = r17
                r3 = 1
                r6 = 0
                r7 = 0
                r11 = 1111490560(0x42400000, float:48.0)
                goto L_0x0938
            L_0x073b:
                android.view.ViewGroup[] r10 = r1.cells
                r10 = r10[r2]
                if (r4 != 0) goto L_0x0742
                goto L_0x0745
            L_0x0742:
                r5 = 2131230791(0x7var_, float:1.8077645E38)
            L_0x0745:
                android.view.View r5 = r10.findViewById(r5)
                r5.setVisibility(r12)
                r5 = 2
                if (r0 == 0) goto L_0x0751
                if (r0 != r5) goto L_0x0758
            L_0x0751:
                android.view.ViewGroup[] r0 = r1.cells
                r0 = r0[r2]
                r0.setVisibility(r12)
            L_0x0758:
                long r10 = r3.id
                boolean r0 = org.telegram.messenger.DialogObject.isUserDialog(r10)
                if (r0 == 0) goto L_0x07ce
                org.telegram.ui.EditWidgetActivity r0 = r1.this$0
                org.telegram.messenger.MessagesController r0 = r0.getMessagesController()
                long r10 = r3.id
                java.lang.Long r8 = java.lang.Long.valueOf(r10)
                org.telegram.tgnet.TLRPC$User r0 = r0.getUser(r8)
                boolean r8 = org.telegram.messenger.UserObject.isUserSelf(r0)
                if (r8 == 0) goto L_0x0780
                r10 = r17
                r8 = 2131627763(0x7f0e0ef3, float:1.88828E38)
                java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r10, r8)
                goto L_0x07a9
            L_0x0780:
                r10 = r17
                r8 = 2131627763(0x7f0e0ef3, float:1.88828E38)
                boolean r11 = org.telegram.messenger.UserObject.isReplyUser((org.telegram.tgnet.TLRPC$User) r0)
                if (r11 == 0) goto L_0x0795
                r11 = 2131627625(0x7f0e0e69, float:1.888252E38)
                java.lang.String r13 = "RepliesTitle"
                java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r13, r11)
                goto L_0x07a9
            L_0x0795:
                boolean r11 = org.telegram.messenger.UserObject.isDeleted(r0)
                if (r11 == 0) goto L_0x07a5
                r11 = 2131625950(0x7f0e07de, float:1.8879122E38)
                java.lang.String r13 = "HiddenName"
                java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r13, r11)
                goto L_0x07a9
            L_0x07a5:
                java.lang.String r11 = org.telegram.messenger.UserObject.getFirstName(r0)
            L_0x07a9:
                boolean r13 = org.telegram.messenger.UserObject.isReplyUser((org.telegram.tgnet.TLRPC$User) r0)
                if (r13 != 0) goto L_0x07cb
                boolean r13 = org.telegram.messenger.UserObject.isUserSelf(r0)
                if (r13 != 0) goto L_0x07cb
                if (r0 == 0) goto L_0x07cb
                org.telegram.tgnet.TLRPC$UserProfilePhoto r13 = r0.photo
                if (r13 == 0) goto L_0x07cb
                org.telegram.tgnet.TLRPC$FileLocation r13 = r13.photo_small
                if (r13 == 0) goto L_0x07cb
                long r8 = r13.volume_id
                int r14 = (r8 > r6 ? 1 : (r8 == r6 ? 0 : -1))
                if (r14 == 0) goto L_0x07cb
                int r8 = r13.local_id
                if (r8 == 0) goto L_0x07cb
                r8 = r13
                goto L_0x07cc
            L_0x07cb:
                r8 = 0
            L_0x07cc:
                r9 = 0
                goto L_0x07fb
            L_0x07ce:
                r10 = r17
                org.telegram.ui.EditWidgetActivity r0 = r1.this$0
                org.telegram.messenger.MessagesController r0 = r0.getMessagesController()
                long r8 = r3.id
                long r8 = -r8
                java.lang.Long r8 = java.lang.Long.valueOf(r8)
                org.telegram.tgnet.TLRPC$Chat r0 = r0.getChat(r8)
                java.lang.String r11 = r0.title
                org.telegram.tgnet.TLRPC$ChatPhoto r8 = r0.photo
                if (r8 == 0) goto L_0x07f8
                org.telegram.tgnet.TLRPC$FileLocation r8 = r8.photo_small
                if (r8 == 0) goto L_0x07f8
                long r13 = r8.volume_id
                int r9 = (r13 > r6 ? 1 : (r13 == r6 ? 0 : -1))
                if (r9 == 0) goto L_0x07f8
                int r9 = r8.local_id
                if (r9 == 0) goto L_0x07f8
                r9 = r0
                r0 = 0
                goto L_0x07fb
            L_0x07f8:
                r9 = r0
                r0 = 0
                r8 = 0
            L_0x07fb:
                android.view.ViewGroup[] r13 = r1.cells
                r13 = r13[r2]
                if (r4 != 0) goto L_0x0805
                r14 = 2131230798(0x7var_e, float:1.8077659E38)
                goto L_0x0808
            L_0x0805:
                r14 = 2131230799(0x7var_f, float:1.807766E38)
            L_0x0808:
                android.view.View r13 = r13.findViewById(r14)
                android.widget.TextView r13 = (android.widget.TextView) r13
                r13.setText(r11)
                if (r8 == 0) goto L_0x0828
                r11 = 1
                java.io.File r8 = org.telegram.messenger.FileLoader.getPathToAttach(r8, r11)     // Catch:{ all -> 0x0821 }
                java.lang.String r8 = r8.toString()     // Catch:{ all -> 0x0821 }
                android.graphics.Bitmap r8 = android.graphics.BitmapFactory.decodeFile(r8)     // Catch:{ all -> 0x0821 }
                goto L_0x0829
            L_0x0821:
                r0 = move-exception
                r6 = 0
                r7 = 0
                r11 = 1111490560(0x42400000, float:48.0)
                goto L_0x08ca
            L_0x0828:
                r8 = 0
            L_0x0829:
                r11 = 1111490560(0x42400000, float:48.0)
                int r13 = org.telegram.messenger.AndroidUtilities.dp(r11)     // Catch:{ all -> 0x08c7 }
                android.graphics.Bitmap$Config r14 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ all -> 0x08c7 }
                android.graphics.Bitmap r14 = android.graphics.Bitmap.createBitmap(r13, r13, r14)     // Catch:{ all -> 0x08c7 }
                r14.eraseColor(r12)     // Catch:{ all -> 0x08c7 }
                android.graphics.Canvas r5 = new android.graphics.Canvas     // Catch:{ all -> 0x08c7 }
                r5.<init>(r14)     // Catch:{ all -> 0x08c7 }
                if (r8 != 0) goto L_0x086b
                if (r0 == 0) goto L_0x085d
                org.telegram.ui.Components.AvatarDrawable r8 = new org.telegram.ui.Components.AvatarDrawable     // Catch:{ all -> 0x08c7 }
                r8.<init>((org.telegram.tgnet.TLRPC$User) r0)     // Catch:{ all -> 0x08c7 }
                boolean r9 = org.telegram.messenger.UserObject.isReplyUser((org.telegram.tgnet.TLRPC$User) r0)     // Catch:{ all -> 0x08c7 }
                if (r9 == 0) goto L_0x0852
                r0 = 12
                r8.setAvatarType(r0)     // Catch:{ all -> 0x08c7 }
                goto L_0x0862
            L_0x0852:
                boolean r0 = org.telegram.messenger.UserObject.isUserSelf(r0)     // Catch:{ all -> 0x08c7 }
                if (r0 == 0) goto L_0x0862
                r9 = 1
                r8.setAvatarType(r9)     // Catch:{ all -> 0x08c7 }
                goto L_0x0862
            L_0x085d:
                org.telegram.ui.Components.AvatarDrawable r8 = new org.telegram.ui.Components.AvatarDrawable     // Catch:{ all -> 0x08c7 }
                r8.<init>((org.telegram.tgnet.TLRPC$Chat) r9)     // Catch:{ all -> 0x08c7 }
            L_0x0862:
                r8.setBounds(r12, r12, r13, r13)     // Catch:{ all -> 0x08c7 }
                r8.draw(r5)     // Catch:{ all -> 0x08c7 }
                r6 = 0
            L_0x0869:
                r7 = 0
                goto L_0x08a9
            L_0x086b:
                android.graphics.BitmapShader r0 = new android.graphics.BitmapShader     // Catch:{ all -> 0x08c7 }
                android.graphics.Shader$TileMode r9 = android.graphics.Shader.TileMode.CLAMP     // Catch:{ all -> 0x08c7 }
                r0.<init>(r8, r9, r9)     // Catch:{ all -> 0x08c7 }
                float r9 = (float) r13     // Catch:{ all -> 0x08c7 }
                int r13 = r8.getWidth()     // Catch:{ all -> 0x08c7 }
                float r13 = (float) r13     // Catch:{ all -> 0x08c7 }
                float r9 = r9 / r13
                r5.save()     // Catch:{ all -> 0x08c7 }
                r5.scale(r9, r9)     // Catch:{ all -> 0x08c7 }
                android.graphics.Paint r9 = r1.roundPaint     // Catch:{ all -> 0x08c7 }
                r9.setShader(r0)     // Catch:{ all -> 0x08c7 }
                android.graphics.RectF r0 = r1.bitmapRect     // Catch:{ all -> 0x08c7 }
                int r9 = r8.getWidth()     // Catch:{ all -> 0x08c7 }
                float r9 = (float) r9     // Catch:{ all -> 0x08c7 }
                int r13 = r8.getHeight()     // Catch:{ all -> 0x08c7 }
                float r13 = (float) r13
                r6 = 0
                r0.set(r6, r6, r9, r13)     // Catch:{ all -> 0x08c5 }
                android.graphics.RectF r0 = r1.bitmapRect     // Catch:{ all -> 0x08c5 }
                int r7 = r8.getWidth()     // Catch:{ all -> 0x08c5 }
                float r7 = (float) r7     // Catch:{ all -> 0x08c5 }
                int r8 = r8.getHeight()     // Catch:{ all -> 0x08c5 }
                float r8 = (float) r8     // Catch:{ all -> 0x08c5 }
                android.graphics.Paint r9 = r1.roundPaint     // Catch:{ all -> 0x08c5 }
                r5.drawRoundRect(r0, r7, r8, r9)     // Catch:{ all -> 0x08c5 }
                r5.restore()     // Catch:{ all -> 0x08c5 }
                goto L_0x0869
            L_0x08a9:
                r5.setBitmap(r7)     // Catch:{ all -> 0x08c3 }
                android.view.ViewGroup[] r0 = r1.cells     // Catch:{ all -> 0x08c3 }
                r0 = r0[r2]     // Catch:{ all -> 0x08c3 }
                if (r4 != 0) goto L_0x08b6
                r5 = 2131230792(0x7var_, float:1.8077647E38)
                goto L_0x08b9
            L_0x08b6:
                r5 = 2131230793(0x7var_, float:1.8077649E38)
            L_0x08b9:
                android.view.View r0 = r0.findViewById(r5)     // Catch:{ all -> 0x08c3 }
                android.widget.ImageView r0 = (android.widget.ImageView) r0     // Catch:{ all -> 0x08c3 }
                r0.setImageBitmap(r14)     // Catch:{ all -> 0x08c3 }
                goto L_0x08cd
            L_0x08c3:
                r0 = move-exception
                goto L_0x08ca
            L_0x08c5:
                r0 = move-exception
                goto L_0x08c9
            L_0x08c7:
                r0 = move-exception
                r6 = 0
            L_0x08c9:
                r7 = 0
            L_0x08ca:
                org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            L_0x08cd:
                int r0 = r3.unread_count
                if (r0 <= 0) goto L_0x0921
                r3 = 99
                if (r0 <= r3) goto L_0x08e7
                r3 = 1
                java.lang.Object[] r0 = new java.lang.Object[r3]
                r5 = 99
                java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
                r0[r12] = r5
                java.lang.String r5 = "%d+"
                java.lang.String r0 = java.lang.String.format(r5, r0)
                goto L_0x08f6
            L_0x08e7:
                r3 = 1
                java.lang.Object[] r5 = new java.lang.Object[r3]
                java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
                r5[r12] = r0
                java.lang.String r0 = "%d"
                java.lang.String r0 = java.lang.String.format(r0, r5)
            L_0x08f6:
                android.view.ViewGroup[] r5 = r1.cells
                r5 = r5[r2]
                if (r4 != 0) goto L_0x0900
                r8 = 2131230794(0x7var_a, float:1.807765E38)
                goto L_0x0903
            L_0x0900:
                r8 = 2131230795(0x7var_b, float:1.8077653E38)
            L_0x0903:
                android.view.View r5 = r5.findViewById(r8)
                android.widget.TextView r5 = (android.widget.TextView) r5
                r5.setText(r0)
                android.view.ViewGroup[] r0 = r1.cells
                r0 = r0[r2]
                if (r4 != 0) goto L_0x0916
                r5 = 2131230796(0x7var_c, float:1.8077655E38)
                goto L_0x0919
            L_0x0916:
                r5 = 2131230797(0x7var_d, float:1.8077657E38)
            L_0x0919:
                android.view.View r0 = r0.findViewById(r5)
                r0.setVisibility(r12)
                goto L_0x0938
            L_0x0921:
                r3 = 1
                android.view.ViewGroup[] r0 = r1.cells
                r0 = r0[r2]
                if (r4 != 0) goto L_0x092c
                r5 = 2131230796(0x7var_c, float:1.8077655E38)
                goto L_0x092f
            L_0x092c:
                r5 = 2131230797(0x7var_d, float:1.8077657E38)
            L_0x092f:
                android.view.View r0 = r0.findViewById(r5)
                r5 = 8
                r0.setVisibility(r5)
            L_0x0938:
                int r4 = r4 + 1
                r17 = r10
                r3 = 2
                r6 = 0
                goto L_0x0675
            L_0x0941:
                r10 = r17
                r3 = 1
                r6 = 0
                r7 = 0
                r11 = 1111490560(0x42400000, float:48.0)
                int r2 = r2 + 1
                r3 = 2
                r6 = 0
                goto L_0x0672
            L_0x094f:
                android.view.ViewGroup[] r0 = r1.cells
                r0 = r0[r12]
                int r0 = r0.getVisibility()
                if (r0 != 0) goto L_0x0965
                org.telegram.ui.EditWidgetActivity r0 = r1.this$0
                android.widget.ImageView r0 = r0.previewImageView
                r2 = 8
                r0.setVisibility(r2)
                goto L_0x096e
            L_0x0965:
                org.telegram.ui.EditWidgetActivity r0 = r1.this$0
                android.widget.ImageView r0 = r0.previewImageView
                r0.setVisibility(r12)
            L_0x096e:
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

    public EditWidgetActivity(int i, int i2) {
        this.widgetType = i;
        this.currentWidgetId = i2;
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        getMessagesStorage().getWidgetDialogIds(this.currentWidgetId, this.widgetType, this.selectedDialogs, arrayList, arrayList2, true);
        getMessagesController().putUsers(arrayList, true);
        getMessagesController().putChats(arrayList2, true);
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
                    SharedPreferences.Editor edit = EditWidgetActivity.this.getParentActivity().getSharedPreferences("shortcut_widget", 0).edit();
                    edit.putInt("account" + EditWidgetActivity.this.currentWidgetId, EditWidgetActivity.this.currentAccount);
                    edit.putInt("type" + EditWidgetActivity.this.currentWidgetId, EditWidgetActivity.this.widgetType);
                    edit.commit();
                    AppWidgetManager instance = AppWidgetManager.getInstance(EditWidgetActivity.this.getParentActivity());
                    if (EditWidgetActivity.this.widgetType == 0) {
                        ChatsWidgetProvider.updateWidget(EditWidgetActivity.this.getParentActivity(), instance, EditWidgetActivity.this.currentWidgetId);
                    } else {
                        ContactsWidgetProvider.updateWidget(EditWidgetActivity.this.getParentActivity(), instance, EditWidgetActivity.this.currentWidgetId);
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

            public void onLongClickRelease() {
            }

            public void onMove(float f, float f2) {
            }

            public boolean onItemClick(View view, int i, float f, float f2) {
                if (EditWidgetActivity.this.getParentActivity() != null && (view instanceof GroupCreateUserCell)) {
                    ((ImageView) view.getTag(NUM)).getHitRect(this.rect);
                    if (!this.rect.contains((int) f, (int) f2)) {
                        AlertDialog.Builder builder = new AlertDialog.Builder((Context) EditWidgetActivity.this.getParentActivity());
                        builder.setItems(new CharSequence[]{LocaleController.getString("Delete", NUM)}, new EditWidgetActivity$2$$ExternalSyntheticLambda0(this, i));
                        EditWidgetActivity.this.showDialog(builder.create());
                        return true;
                    }
                }
                return false;
            }

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$onItemClick$0(int i, DialogInterface dialogInterface, int i2) {
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
    public /* synthetic */ void lambda$createView$1(Context context, View view, int i) {
        if (i == this.selectChatsRow) {
            InviteMembersBottomSheet inviteMembersBottomSheet = new InviteMembersBottomSheet(context, this.currentAccount, (LongSparseArray<TLObject>) null, 0, this, (Theme.ResourcesProvider) null);
            inviteMembersBottomSheet.setDelegate(new EditWidgetActivity$$ExternalSyntheticLambda1(this), this.selectedDialogs);
            inviteMembersBottomSheet.setSelectedContacts(this.selectedDialogs);
            showDialog(inviteMembersBottomSheet);
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$0(ArrayList arrayList) {
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
                imageView.setOnTouchListener(new EditWidgetActivity$ListAdapter$$ExternalSyntheticLambda0(this, groupCreateUserCell2));
                imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chats_pinnedIcon"), PorterDuff.Mode.MULTIPLY));
                groupCreateUserCell = groupCreateUserCell2;
            } else {
                groupCreateUserCell = EditWidgetActivity.this.widgetPreviewCell = new WidgetPreviewCell(EditWidgetActivity.this, this.mContext);
            }
            return new RecyclerListView.Holder(groupCreateUserCell);
        }

        /* access modifiers changed from: private */
        public /* synthetic */ boolean lambda$onCreateViewHolder$0(GroupCreateUserCell groupCreateUserCell, View view, MotionEvent motionEvent) {
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
                long longValue = ((Long) EditWidgetActivity.this.selectedDialogs.get(i - EditWidgetActivity.this.chatsStartRow)).longValue();
                if (DialogObject.isUserDialog(longValue)) {
                    TLRPC$User user = EditWidgetActivity.this.getMessagesController().getUser(Long.valueOf(longValue));
                    if (i == EditWidgetActivity.this.chatsEndRow - 1) {
                        z = false;
                    }
                    groupCreateUserCell.setObject(user, (CharSequence) null, (CharSequence) null, z);
                    return;
                }
                TLRPC$Chat chat = EditWidgetActivity.this.getMessagesController().getChat(Long.valueOf(-longValue));
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
            int access$1300 = i - EditWidgetActivity.this.chatsStartRow;
            int access$13002 = i2 - EditWidgetActivity.this.chatsStartRow;
            int access$100 = EditWidgetActivity.this.chatsEndRow - EditWidgetActivity.this.chatsStartRow;
            if (access$1300 < 0 || access$13002 < 0 || access$1300 >= access$100 || access$13002 >= access$100) {
                return false;
            }
            EditWidgetActivity.this.selectedDialogs.set(access$1300, (Long) EditWidgetActivity.this.selectedDialogs.get(access$13002));
            EditWidgetActivity.this.selectedDialogs.set(access$13002, (Long) EditWidgetActivity.this.selectedDialogs.get(access$1300));
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
