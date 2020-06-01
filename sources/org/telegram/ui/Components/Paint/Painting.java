package org.telegram.ui.Components.Paint;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.opengl.GLES20;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Map;
import java.util.UUID;
import org.telegram.messenger.DispatchQueue;
import org.telegram.ui.Components.Size;

public class Painting {
    private Path activePath;
    private RectF activeStrokeBounds;
    private Slice backupSlice;
    private Texture bitmapTexture;
    private Brush brush;
    private Texture brushTexture;
    private int[] buffers = new int[1];
    private ByteBuffer dataBuffer;
    private PaintingDelegate delegate;
    private int paintTexture;
    private boolean paused;
    private float[] projection;
    private float[] renderProjection;
    private RenderState renderState = new RenderState();
    private RenderView renderView;
    private int reusableFramebuffer;
    private Map<String, Shader> shaders;
    private Size size;
    private int suppressChangesCounter;
    private ByteBuffer textureBuffer;
    private ByteBuffer vertexBuffer;

    public interface PaintingDelegate {
        void contentChanged();

        DispatchQueue requestDispatchQueue();

        UndoStore requestUndoStore();
    }

    public static class PaintingData {
        public Bitmap bitmap;
        public ByteBuffer data;

        PaintingData(Bitmap bitmap2, ByteBuffer byteBuffer) {
            this.bitmap = bitmap2;
            this.data = byteBuffer;
        }
    }

    public Painting(Size size2) {
        this.size = size2;
        this.dataBuffer = ByteBuffer.allocateDirect(((int) size2.width) * ((int) size2.height) * 4);
        Size size3 = this.size;
        this.projection = GLMatrix.LoadOrtho(0.0f, size3.width, 0.0f, size3.height, -1.0f, 1.0f);
        if (this.vertexBuffer == null) {
            ByteBuffer allocateDirect = ByteBuffer.allocateDirect(32);
            this.vertexBuffer = allocateDirect;
            allocateDirect.order(ByteOrder.nativeOrder());
        }
        this.vertexBuffer.putFloat(0.0f);
        this.vertexBuffer.putFloat(0.0f);
        this.vertexBuffer.putFloat(this.size.width);
        this.vertexBuffer.putFloat(0.0f);
        this.vertexBuffer.putFloat(0.0f);
        this.vertexBuffer.putFloat(this.size.height);
        this.vertexBuffer.putFloat(this.size.width);
        this.vertexBuffer.putFloat(this.size.height);
        this.vertexBuffer.rewind();
        if (this.textureBuffer == null) {
            ByteBuffer allocateDirect2 = ByteBuffer.allocateDirect(32);
            this.textureBuffer = allocateDirect2;
            allocateDirect2.order(ByteOrder.nativeOrder());
            this.textureBuffer.putFloat(0.0f);
            this.textureBuffer.putFloat(0.0f);
            this.textureBuffer.putFloat(1.0f);
            this.textureBuffer.putFloat(0.0f);
            this.textureBuffer.putFloat(0.0f);
            this.textureBuffer.putFloat(1.0f);
            this.textureBuffer.putFloat(1.0f);
            this.textureBuffer.putFloat(1.0f);
            this.textureBuffer.rewind();
        }
    }

    public void setDelegate(PaintingDelegate paintingDelegate) {
        this.delegate = paintingDelegate;
    }

    public void setRenderView(RenderView renderView2) {
        this.renderView = renderView2;
    }

    public Size getSize() {
        return this.size;
    }

    public RectF getBounds() {
        Size size2 = this.size;
        return new RectF(0.0f, 0.0f, size2.width, size2.height);
    }

    private boolean isSuppressingChanges() {
        return this.suppressChangesCounter > 0;
    }

    private void beginSuppressingChanges() {
        this.suppressChangesCounter++;
    }

    private void endSuppressingChanges() {
        this.suppressChangesCounter--;
    }

    public void setBitmap(Bitmap bitmap) {
        if (this.bitmapTexture == null) {
            this.bitmapTexture = new Texture(bitmap);
        }
    }

    public void paintStroke(Path path, boolean z, Runnable runnable) {
        this.renderView.performInContext(new Runnable(path, z, runnable) {
            private final /* synthetic */ Path f$1;
            private final /* synthetic */ boolean f$2;
            private final /* synthetic */ Runnable f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                Painting.this.lambda$paintStroke$0$Painting(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    public /* synthetic */ void lambda$paintStroke$0$Painting(Path path, boolean z, Runnable runnable) {
        RectF rectF;
        this.activePath = path;
        GLES20.glBindFramebuffer(36160, getReusableFramebuffer());
        GLES20.glFramebufferTexture2D(36160, 36064, 3553, getPaintTexture(), 0);
        Utils.HasGLError();
        if (GLES20.glCheckFramebufferStatus(36160) == 36053) {
            Size size2 = this.size;
            GLES20.glViewport(0, 0, (int) size2.width, (int) size2.height);
            if (z) {
                GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
                GLES20.glClear(16384);
            }
            Map<String, Shader> map = this.shaders;
            if (map != null) {
                Shader shader = map.get(this.brush.isLightSaber() ? "brushLight" : "brush");
                if (shader != null) {
                    GLES20.glUseProgram(shader.program);
                    if (this.brushTexture == null) {
                        this.brushTexture = new Texture(this.brush.getStamp());
                    }
                    GLES20.glActiveTexture(33984);
                    GLES20.glBindTexture(3553, this.brushTexture.texture());
                    GLES20.glUniformMatrix4fv(shader.getUniform("mvpMatrix"), 1, false, FloatBuffer.wrap(this.projection));
                    GLES20.glUniform1i(shader.getUniform("texture"), 0);
                    rectF = Render.RenderPath(path, this.renderState);
                } else {
                    return;
                }
            } else {
                return;
            }
        } else {
            rectF = null;
        }
        GLES20.glBindFramebuffer(36160, 0);
        PaintingDelegate paintingDelegate = this.delegate;
        if (paintingDelegate != null) {
            paintingDelegate.contentChanged();
        }
        RectF rectF2 = this.activeStrokeBounds;
        if (rectF2 != null) {
            rectF2.union(rectF);
        } else {
            this.activeStrokeBounds = rectF;
        }
        if (runnable != null) {
            runnable.run();
        }
    }

    public void commitStroke(int i) {
        this.renderView.performInContext(new Runnable(i) {
            private final /* synthetic */ int f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                Painting.this.lambda$commitStroke$1$Painting(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$commitStroke$1$Painting(int i) {
        PaintingDelegate paintingDelegate;
        registerUndo(this.activeStrokeBounds);
        beginSuppressingChanges();
        GLES20.glBindFramebuffer(36160, getReusableFramebuffer());
        GLES20.glFramebufferTexture2D(36160, 36064, 3553, getTexture(), 0);
        Size size2 = this.size;
        GLES20.glViewport(0, 0, (int) size2.width, (int) size2.height);
        Shader shader = this.shaders.get(this.brush.isLightSaber() ? "compositeWithMaskLight" : "compositeWithMask");
        GLES20.glUseProgram(shader.program);
        GLES20.glUniformMatrix4fv(shader.getUniform("mvpMatrix"), 1, false, FloatBuffer.wrap(this.projection));
        GLES20.glUniform1i(shader.getUniform("texture"), 0);
        GLES20.glUniform1i(shader.getUniform("mask"), 1);
        Shader.SetColorUniform(shader.getUniform("color"), i);
        GLES20.glActiveTexture(33984);
        GLES20.glBindTexture(3553, getTexture());
        GLES20.glTexParameteri(3553, 10241, 9728);
        GLES20.glActiveTexture(33985);
        GLES20.glBindTexture(3553, getPaintTexture());
        GLES20.glBlendFunc(1, 0);
        GLES20.glVertexAttribPointer(0, 2, 5126, false, 8, this.vertexBuffer);
        GLES20.glEnableVertexAttribArray(0);
        GLES20.glVertexAttribPointer(1, 2, 5126, false, 8, this.textureBuffer);
        GLES20.glEnableVertexAttribArray(1);
        GLES20.glDrawArrays(5, 0, 4);
        GLES20.glBindTexture(3553, getTexture());
        GLES20.glTexParameteri(3553, 10241, 9729);
        GLES20.glBindFramebuffer(36160, 0);
        if (!isSuppressingChanges() && (paintingDelegate = this.delegate) != null) {
            paintingDelegate.contentChanged();
        }
        endSuppressingChanges();
        this.renderState.reset();
        this.activeStrokeBounds = null;
        this.activePath = null;
    }

    public void clearStroke() {
        this.renderView.performInContext(new Runnable() {
            public final void run() {
                Painting.this.lambda$clearStroke$2$Painting();
            }
        });
    }

    public /* synthetic */ void lambda$clearStroke$2$Painting() {
        GLES20.glBindFramebuffer(36160, getReusableFramebuffer());
        GLES20.glFramebufferTexture2D(36160, 36064, 3553, getPaintTexture(), 0);
        Utils.HasGLError();
        if (GLES20.glCheckFramebufferStatus(36160) == 36053) {
            Size size2 = this.size;
            GLES20.glViewport(0, 0, (int) size2.width, (int) size2.height);
            GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
            GLES20.glClear(16384);
        }
        GLES20.glBindFramebuffer(36160, 0);
        PaintingDelegate paintingDelegate = this.delegate;
        if (paintingDelegate != null) {
            paintingDelegate.contentChanged();
        }
        this.renderState.reset();
        this.activeStrokeBounds = null;
        this.activePath = null;
    }

    private void registerUndo(RectF rectF) {
        if (rectF != null && rectF.setIntersect(rectF, getBounds())) {
            this.delegate.requestUndoStore().registerUndo(UUID.randomUUID(), new Runnable(new Slice(getPaintingData(rectF, true).data, rectF, this.delegate.requestDispatchQueue())) {
                private final /* synthetic */ Slice f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    Painting.this.lambda$registerUndo$3$Painting(this.f$1);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: restoreSlice */
    public void lambda$registerUndo$3$Painting(Slice slice) {
        this.renderView.performInContext(new Runnable(slice) {
            private final /* synthetic */ Slice f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                Painting.this.lambda$restoreSlice$4$Painting(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$restoreSlice$4$Painting(Slice slice) {
        PaintingDelegate paintingDelegate;
        ByteBuffer data = slice.getData();
        GLES20.glBindTexture(3553, getTexture());
        GLES20.glTexSubImage2D(3553, 0, slice.getX(), slice.getY(), slice.getWidth(), slice.getHeight(), 6408, 5121, data);
        if (!isSuppressingChanges() && (paintingDelegate = this.delegate) != null) {
            paintingDelegate.contentChanged();
        }
        slice.cleanResources();
    }

    public void setRenderProjection(float[] fArr) {
        this.renderProjection = fArr;
    }

    public void render() {
        if (this.shaders != null) {
            if (this.activePath != null) {
                render(getPaintTexture(), this.activePath.getColor());
            } else {
                renderBlit();
            }
        }
    }

    private void render(int i, int i2) {
        Shader shader = this.shaders.get(this.brush.isLightSaber() ? "blitWithMaskLight" : "blitWithMask");
        if (shader != null) {
            GLES20.glUseProgram(shader.program);
            GLES20.glUniformMatrix4fv(shader.getUniform("mvpMatrix"), 1, false, FloatBuffer.wrap(this.renderProjection));
            GLES20.glUniform1i(shader.getUniform("texture"), 0);
            GLES20.glUniform1i(shader.getUniform("mask"), 1);
            Shader.SetColorUniform(shader.getUniform("color"), i2);
            GLES20.glActiveTexture(33984);
            GLES20.glBindTexture(3553, getTexture());
            GLES20.glActiveTexture(33985);
            GLES20.glBindTexture(3553, i);
            GLES20.glBlendFunc(1, 771);
            GLES20.glVertexAttribPointer(0, 2, 5126, false, 8, this.vertexBuffer);
            GLES20.glEnableVertexAttribArray(0);
            GLES20.glVertexAttribPointer(1, 2, 5126, false, 8, this.textureBuffer);
            GLES20.glEnableVertexAttribArray(1);
            GLES20.glDrawArrays(5, 0, 4);
            Utils.HasGLError();
        }
    }

    private void renderBlit() {
        Shader shader = this.shaders.get("blit");
        if (shader != null) {
            GLES20.glUseProgram(shader.program);
            GLES20.glUniformMatrix4fv(shader.getUniform("mvpMatrix"), 1, false, FloatBuffer.wrap(this.renderProjection));
            GLES20.glUniform1i(shader.getUniform("texture"), 0);
            GLES20.glActiveTexture(33984);
            GLES20.glBindTexture(3553, getTexture());
            GLES20.glBlendFunc(1, 771);
            GLES20.glVertexAttribPointer(0, 2, 5126, false, 8, this.vertexBuffer);
            GLES20.glEnableVertexAttribArray(0);
            GLES20.glVertexAttribPointer(1, 2, 5126, false, 8, this.textureBuffer);
            GLES20.glEnableVertexAttribArray(1);
            GLES20.glDrawArrays(5, 0, 4);
            Utils.HasGLError();
        }
    }

    public PaintingData getPaintingData(RectF rectF, boolean z) {
        PaintingData paintingData;
        RectF rectF2 = rectF;
        int i = (int) rectF2.left;
        int i2 = (int) rectF2.top;
        int width = (int) rectF.width();
        int height = (int) rectF.height();
        GLES20.glGenFramebuffers(1, this.buffers, 0);
        int i3 = this.buffers[0];
        GLES20.glBindFramebuffer(36160, i3);
        GLES20.glGenTextures(1, this.buffers, 0);
        int i4 = this.buffers[0];
        GLES20.glBindTexture(3553, i4);
        GLES20.glTexParameteri(3553, 10241, 9729);
        GLES20.glTexParameteri(3553, 10240, 9729);
        GLES20.glTexParameteri(3553, 10242, 33071);
        GLES20.glTexParameteri(3553, 10243, 33071);
        int i5 = i4;
        int i6 = i3;
        GLES20.glTexImage2D(3553, 0, 6408, width, height, 0, 6408, 5121, (Buffer) null);
        GLES20.glFramebufferTexture2D(36160, 36064, 3553, i5, 0);
        Size size2 = this.size;
        GLES20.glViewport(0, 0, (int) size2.width, (int) size2.height);
        Map<String, Shader> map = this.shaders;
        if (map == null) {
            return null;
        }
        Shader shader = map.get(z ? "nonPremultipliedBlit" : "blit");
        if (shader == null) {
            return null;
        }
        GLES20.glUseProgram(shader.program);
        Matrix matrix = new Matrix();
        matrix.preTranslate((float) (-i), (float) (-i2));
        GLES20.glUniformMatrix4fv(shader.getUniform("mvpMatrix"), 1, false, FloatBuffer.wrap(GLMatrix.MultiplyMat4f(this.projection, GLMatrix.LoadGraphicsMatrix(matrix))));
        if (z) {
            GLES20.glUniform1i(shader.getUniform("texture"), 0);
            GLES20.glActiveTexture(33984);
            GLES20.glBindTexture(3553, getTexture());
        } else {
            GLES20.glUniform1i(shader.getUniform("texture"), 0);
            GLES20.glActiveTexture(33984);
            GLES20.glBindTexture(3553, this.bitmapTexture.texture());
            GLES20.glActiveTexture(33984);
            GLES20.glBindTexture(3553, getTexture());
        }
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        GLES20.glClear(16384);
        GLES20.glBlendFunc(1, 771);
        GLES20.glVertexAttribPointer(0, 2, 5126, false, 8, this.vertexBuffer);
        GLES20.glEnableVertexAttribArray(0);
        GLES20.glVertexAttribPointer(1, 2, 5126, false, 8, this.textureBuffer);
        GLES20.glEnableVertexAttribArray(1);
        GLES20.glDrawArrays(5, 0, 4);
        this.dataBuffer.limit(width * height * 4);
        GLES20.glReadPixels(0, 0, width, height, 6408, 5121, this.dataBuffer);
        if (z) {
            paintingData = new PaintingData((Bitmap) null, this.dataBuffer);
        } else {
            Bitmap createBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            createBitmap.copyPixelsFromBuffer(this.dataBuffer);
            paintingData = new PaintingData(createBitmap, (ByteBuffer) null);
        }
        int[] iArr = this.buffers;
        iArr[0] = i6;
        GLES20.glDeleteFramebuffers(1, iArr, 0);
        int[] iArr2 = this.buffers;
        iArr2[0] = i5;
        GLES20.glDeleteTextures(1, iArr2, 0);
        return paintingData;
    }

    public void setBrush(Brush brush2) {
        this.brush = brush2;
        Texture texture = this.brushTexture;
        if (texture != null) {
            texture.cleanResources(true);
            this.brushTexture = null;
        }
    }

    public boolean isPaused() {
        return this.paused;
    }

    public void onPause(Runnable runnable) {
        this.renderView.performInContext(new Runnable(runnable) {
            private final /* synthetic */ Runnable f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                Painting.this.lambda$onPause$5$Painting(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$onPause$5$Painting(Runnable runnable) {
        this.paused = true;
        this.backupSlice = new Slice(getPaintingData(getBounds(), true).data, getBounds(), this.delegate.requestDispatchQueue());
        cleanResources(false);
        if (runnable != null) {
            runnable.run();
        }
    }

    public void onResume() {
        lambda$registerUndo$3$Painting(this.backupSlice);
        this.backupSlice = null;
        this.paused = false;
    }

    public void cleanResources(boolean z) {
        int i = this.reusableFramebuffer;
        if (i != 0) {
            int[] iArr = this.buffers;
            iArr[0] = i;
            GLES20.glDeleteFramebuffers(1, iArr, 0);
            this.reusableFramebuffer = 0;
        }
        this.bitmapTexture.cleanResources(z);
        int i2 = this.paintTexture;
        if (i2 != 0) {
            int[] iArr2 = this.buffers;
            iArr2[0] = i2;
            GLES20.glDeleteTextures(1, iArr2, 0);
            this.paintTexture = 0;
        }
        Texture texture = this.brushTexture;
        if (texture != null) {
            texture.cleanResources(true);
            this.brushTexture = null;
        }
        Map<String, Shader> map = this.shaders;
        if (map != null) {
            for (Shader cleanResources : map.values()) {
                cleanResources.cleanResources();
            }
            this.shaders = null;
        }
    }

    private int getReusableFramebuffer() {
        if (this.reusableFramebuffer == 0) {
            int[] iArr = new int[1];
            GLES20.glGenFramebuffers(1, iArr, 0);
            this.reusableFramebuffer = iArr[0];
            Utils.HasGLError();
        }
        return this.reusableFramebuffer;
    }

    private int getTexture() {
        Texture texture = this.bitmapTexture;
        if (texture != null) {
            return texture.texture();
        }
        return 0;
    }

    private int getPaintTexture() {
        if (this.paintTexture == 0) {
            this.paintTexture = Texture.generateTexture(this.size);
        }
        return this.paintTexture;
    }

    public void setupShaders() {
        this.shaders = ShaderSet.setup();
    }
}
