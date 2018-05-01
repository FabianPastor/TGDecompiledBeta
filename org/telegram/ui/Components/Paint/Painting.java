package org.telegram.ui.Components.Paint;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.opengl.GLES20;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import org.telegram.messenger.DispatchQueue;
import org.telegram.ui.Components.Size;

public class Painting
{
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
  
  public Painting(Size paramSize)
  {
    this.size = paramSize;
    this.dataBuffer = ByteBuffer.allocateDirect((int)this.size.width * (int)this.size.height * 4);
    this.projection = GLMatrix.LoadOrtho(0.0F, this.size.width, 0.0F, this.size.height, -1.0F, 1.0F);
    if (this.vertexBuffer == null)
    {
      this.vertexBuffer = ByteBuffer.allocateDirect(32);
      this.vertexBuffer.order(ByteOrder.nativeOrder());
    }
    this.vertexBuffer.putFloat(0.0F);
    this.vertexBuffer.putFloat(0.0F);
    this.vertexBuffer.putFloat(this.size.width);
    this.vertexBuffer.putFloat(0.0F);
    this.vertexBuffer.putFloat(0.0F);
    this.vertexBuffer.putFloat(this.size.height);
    this.vertexBuffer.putFloat(this.size.width);
    this.vertexBuffer.putFloat(this.size.height);
    this.vertexBuffer.rewind();
    if (this.textureBuffer == null)
    {
      this.textureBuffer = ByteBuffer.allocateDirect(32);
      this.textureBuffer.order(ByteOrder.nativeOrder());
      this.textureBuffer.putFloat(0.0F);
      this.textureBuffer.putFloat(0.0F);
      this.textureBuffer.putFloat(1.0F);
      this.textureBuffer.putFloat(0.0F);
      this.textureBuffer.putFloat(0.0F);
      this.textureBuffer.putFloat(1.0F);
      this.textureBuffer.putFloat(1.0F);
      this.textureBuffer.putFloat(1.0F);
      this.textureBuffer.rewind();
    }
  }
  
  private void beginSuppressingChanges()
  {
    this.suppressChangesCounter += 1;
  }
  
  private void endSuppressingChanges()
  {
    this.suppressChangesCounter -= 1;
  }
  
  private int getPaintTexture()
  {
    if (this.paintTexture == 0) {
      this.paintTexture = Texture.generateTexture(this.size);
    }
    return this.paintTexture;
  }
  
  private int getReusableFramebuffer()
  {
    if (this.reusableFramebuffer == 0)
    {
      int[] arrayOfInt = new int[1];
      GLES20.glGenFramebuffers(1, arrayOfInt, 0);
      this.reusableFramebuffer = arrayOfInt[0];
      Utils.HasGLError();
    }
    return this.reusableFramebuffer;
  }
  
  private int getTexture()
  {
    if (this.bitmapTexture != null) {}
    for (int i = this.bitmapTexture.texture();; i = 0) {
      return i;
    }
  }
  
  private boolean isSuppressingChanges()
  {
    if (this.suppressChangesCounter > 0) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  private void registerUndo(final RectF paramRectF)
  {
    if (paramRectF == null) {}
    for (;;)
    {
      return;
      if (paramRectF.setIntersect(paramRectF, getBounds()))
      {
        paramRectF = new Slice(getPaintingData(paramRectF, true).data, paramRectF, this.delegate.requestDispatchQueue());
        this.delegate.requestUndoStore().registerUndo(UUID.randomUUID(), new Runnable()
        {
          public void run()
          {
            Painting.this.restoreSlice(paramRectF);
          }
        });
      }
    }
  }
  
  private void render(int paramInt1, int paramInt2)
  {
    Map localMap = this.shaders;
    Object localObject;
    if (this.brush.isLightSaber())
    {
      localObject = "blitWithMaskLight";
      localObject = (Shader)localMap.get(localObject);
      if (localObject != null) {
        break label49;
      }
    }
    for (;;)
    {
      return;
      localObject = "blitWithMask";
      break;
      label49:
      GLES20.glUseProgram(((Shader)localObject).program);
      GLES20.glUniformMatrix4fv(((Shader)localObject).getUniform("mvpMatrix"), 1, false, FloatBuffer.wrap(this.renderProjection));
      GLES20.glUniform1i(((Shader)localObject).getUniform("texture"), 0);
      GLES20.glUniform1i(((Shader)localObject).getUniform("mask"), 1);
      Shader.SetColorUniform(((Shader)localObject).getUniform("color"), paramInt2);
      GLES20.glActiveTexture(33984);
      GLES20.glBindTexture(3553, getTexture());
      GLES20.glActiveTexture(33985);
      GLES20.glBindTexture(3553, paramInt1);
      GLES20.glBlendFunc(1, 771);
      GLES20.glVertexAttribPointer(0, 2, 5126, false, 8, this.vertexBuffer);
      GLES20.glEnableVertexAttribArray(0);
      GLES20.glVertexAttribPointer(1, 2, 5126, false, 8, this.textureBuffer);
      GLES20.glEnableVertexAttribArray(1);
      GLES20.glDrawArrays(5, 0, 4);
      Utils.HasGLError();
    }
  }
  
  private void renderBlit()
  {
    Shader localShader = (Shader)this.shaders.get("blit");
    if (localShader == null) {}
    for (;;)
    {
      return;
      GLES20.glUseProgram(localShader.program);
      GLES20.glUniformMatrix4fv(localShader.getUniform("mvpMatrix"), 1, false, FloatBuffer.wrap(this.renderProjection));
      GLES20.glUniform1i(localShader.getUniform("texture"), 0);
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
  
  private void restoreSlice(final Slice paramSlice)
  {
    this.renderView.performInContext(new Runnable()
    {
      public void run()
      {
        ByteBuffer localByteBuffer = paramSlice.getData();
        GLES20.glBindTexture(3553, Painting.this.getTexture());
        GLES20.glTexSubImage2D(3553, 0, paramSlice.getX(), paramSlice.getY(), paramSlice.getWidth(), paramSlice.getHeight(), 6408, 5121, localByteBuffer);
        if ((!Painting.this.isSuppressingChanges()) && (Painting.this.delegate != null)) {
          Painting.this.delegate.contentChanged(paramSlice.getBounds());
        }
        paramSlice.cleanResources();
      }
    });
  }
  
  private void update(RectF paramRectF, Runnable paramRunnable)
  {
    GLES20.glBindFramebuffer(36160, getReusableFramebuffer());
    GLES20.glFramebufferTexture2D(36160, 36064, 3553, getTexture(), 0);
    if (GLES20.glCheckFramebufferStatus(36160) == 36053)
    {
      GLES20.glViewport(0, 0, (int)this.size.width, (int)this.size.height);
      paramRunnable.run();
    }
    GLES20.glBindFramebuffer(36160, 0);
    if ((!isSuppressingChanges()) && (this.delegate != null)) {
      this.delegate.contentChanged(paramRectF);
    }
  }
  
  public void cleanResources(boolean paramBoolean)
  {
    if (this.reusableFramebuffer != 0)
    {
      this.buffers[0] = this.reusableFramebuffer;
      GLES20.glDeleteFramebuffers(1, this.buffers, 0);
      this.reusableFramebuffer = 0;
    }
    this.bitmapTexture.cleanResources(paramBoolean);
    if (this.paintTexture != 0)
    {
      this.buffers[0] = this.paintTexture;
      GLES20.glDeleteTextures(1, this.buffers, 0);
      this.paintTexture = 0;
    }
    if (this.brushTexture != null)
    {
      this.brushTexture.cleanResources(true);
      this.brushTexture = null;
    }
    if (this.shaders != null)
    {
      Iterator localIterator = this.shaders.values().iterator();
      while (localIterator.hasNext()) {
        ((Shader)localIterator.next()).cleanResources();
      }
      this.shaders = null;
    }
  }
  
  public void commitStroke(final int paramInt)
  {
    this.renderView.performInContext(new Runnable()
    {
      public void run()
      {
        Painting.this.registerUndo(Painting.this.activeStrokeBounds);
        Painting.this.beginSuppressingChanges();
        Painting.this.update(null, new Runnable()
        {
          public void run()
          {
            if (Painting.this.shaders == null) {
              return;
            }
            Map localMap = Painting.this.shaders;
            if (Painting.this.brush.isLightSaber()) {}
            for (Object localObject = "compositeWithMaskLight";; localObject = "compositeWithMask")
            {
              localObject = (Shader)localMap.get(localObject);
              if (localObject == null) {
                break;
              }
              GLES20.glUseProgram(((Shader)localObject).program);
              GLES20.glUniformMatrix4fv(((Shader)localObject).getUniform("mvpMatrix"), 1, false, FloatBuffer.wrap(Painting.this.projection));
              GLES20.glUniform1i(((Shader)localObject).getUniform("mask"), 0);
              Shader.SetColorUniform(((Shader)localObject).getUniform("color"), Painting.2.this.val$color);
              GLES20.glActiveTexture(33984);
              GLES20.glBindTexture(3553, Painting.this.getPaintTexture());
              GLES20.glBlendFuncSeparate(770, 771, 770, 1);
              GLES20.glVertexAttribPointer(0, 2, 5126, false, 8, Painting.this.vertexBuffer);
              GLES20.glEnableVertexAttribArray(0);
              GLES20.glVertexAttribPointer(1, 2, 5126, false, 8, Painting.this.textureBuffer);
              GLES20.glEnableVertexAttribArray(1);
              GLES20.glDrawArrays(5, 0, 4);
              break;
            }
          }
        });
        Painting.this.endSuppressingChanges();
        Painting.this.renderState.reset();
        Painting.access$1002(Painting.this, null);
        Painting.access$002(Painting.this, null);
      }
    });
  }
  
  public RectF getBounds()
  {
    return new RectF(0.0F, 0.0F, this.size.width, this.size.height);
  }
  
  public PaintingData getPaintingData(RectF paramRectF, boolean paramBoolean)
  {
    int i = (int)paramRectF.left;
    int j = (int)paramRectF.top;
    int k = (int)paramRectF.width();
    int m = (int)paramRectF.height();
    GLES20.glGenFramebuffers(1, this.buffers, 0);
    int n = this.buffers[0];
    GLES20.glBindFramebuffer(36160, n);
    GLES20.glGenTextures(1, this.buffers, 0);
    int i1 = this.buffers[0];
    GLES20.glBindTexture(3553, i1);
    GLES20.glTexParameteri(3553, 10241, 9729);
    GLES20.glTexParameteri(3553, 10240, 9729);
    GLES20.glTexParameteri(3553, 10242, 33071);
    GLES20.glTexParameteri(3553, 10243, 33071);
    GLES20.glTexImage2D(3553, 0, 6408, k, m, 0, 6408, 5121, null);
    GLES20.glFramebufferTexture2D(36160, 36064, 3553, i1, 0);
    GLES20.glViewport(0, 0, (int)this.size.width, (int)this.size.height);
    if (this.shaders == null)
    {
      paramRectF = null;
      return paramRectF;
    }
    Object localObject = this.shaders;
    if (paramBoolean) {}
    for (paramRectF = "nonPremultipliedBlit";; paramRectF = "blit")
    {
      paramRectF = (Shader)((Map)localObject).get(paramRectF);
      if (paramRectF != null) {
        break label236;
      }
      paramRectF = null;
      break;
    }
    label236:
    GLES20.glUseProgram(paramRectF.program);
    localObject = new Matrix();
    ((Matrix)localObject).preTranslate(-i, -j);
    localObject = GLMatrix.LoadGraphicsMatrix((Matrix)localObject);
    localObject = GLMatrix.MultiplyMat4f(this.projection, (float[])localObject);
    GLES20.glUniformMatrix4fv(paramRectF.getUniform("mvpMatrix"), 1, false, FloatBuffer.wrap((float[])localObject));
    if (paramBoolean)
    {
      GLES20.glUniform1i(paramRectF.getUniform("texture"), 0);
      GLES20.glActiveTexture(33984);
      GLES20.glBindTexture(3553, getTexture());
      label331:
      GLES20.glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
      GLES20.glClear(16384);
      GLES20.glBlendFunc(1, 771);
      GLES20.glVertexAttribPointer(0, 2, 5126, false, 8, this.vertexBuffer);
      GLES20.glEnableVertexAttribArray(0);
      GLES20.glVertexAttribPointer(1, 2, 5126, false, 8, this.textureBuffer);
      GLES20.glEnableVertexAttribArray(1);
      GLES20.glDrawArrays(5, 0, 4);
      this.dataBuffer.limit(k * m * 4);
      GLES20.glReadPixels(0, 0, k, m, 6408, 5121, this.dataBuffer);
      if (!paramBoolean) {
        break label533;
      }
    }
    for (paramRectF = new PaintingData(null, this.dataBuffer);; paramRectF = new PaintingData(paramRectF, null))
    {
      this.buffers[0] = n;
      GLES20.glDeleteFramebuffers(1, this.buffers, 0);
      this.buffers[0] = i1;
      GLES20.glDeleteTextures(1, this.buffers, 0);
      break;
      GLES20.glUniform1i(paramRectF.getUniform("texture"), 0);
      GLES20.glActiveTexture(33984);
      GLES20.glBindTexture(3553, this.bitmapTexture.texture());
      GLES20.glActiveTexture(33984);
      GLES20.glBindTexture(3553, getTexture());
      break label331;
      label533:
      paramRectF = Bitmap.createBitmap(k, m, Bitmap.Config.ARGB_8888);
      paramRectF.copyPixelsFromBuffer(this.dataBuffer);
    }
  }
  
  public Size getSize()
  {
    return this.size;
  }
  
  public boolean isPaused()
  {
    return this.paused;
  }
  
  public void onPause(final Runnable paramRunnable)
  {
    this.renderView.performInContext(new Runnable()
    {
      public void run()
      {
        Painting.access$2002(Painting.this, true);
        Painting.PaintingData localPaintingData = Painting.this.getPaintingData(Painting.this.getBounds(), true);
        Painting.access$2102(Painting.this, new Slice(localPaintingData.data, Painting.this.getBounds(), Painting.this.delegate.requestDispatchQueue()));
        Painting.this.cleanResources(false);
        if (paramRunnable != null) {
          paramRunnable.run();
        }
      }
    });
  }
  
  public void onResume()
  {
    restoreSlice(this.backupSlice);
    this.backupSlice = null;
    this.paused = false;
  }
  
  public void paintStroke(final Path paramPath, final boolean paramBoolean, final Runnable paramRunnable)
  {
    this.renderView.performInContext(new Runnable()
    {
      public void run()
      {
        Painting.access$002(Painting.this, paramPath);
        Object localObject = null;
        GLES20.glBindFramebuffer(36160, Painting.this.getReusableFramebuffer());
        GLES20.glFramebufferTexture2D(36160, 36064, 3553, Painting.this.getPaintTexture(), 0);
        Utils.HasGLError();
        if (GLES20.glCheckFramebufferStatus(36160) == 36053)
        {
          GLES20.glViewport(0, 0, (int)Painting.this.size.width, (int)Painting.this.size.height);
          if (paramBoolean)
          {
            GLES20.glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
            GLES20.glClear(16384);
          }
          if (Painting.this.shaders != null) {}
        }
        label141:
        label342:
        label352:
        for (;;)
        {
          return;
          Map localMap = Painting.this.shaders;
          if (Painting.this.brush.isLightSaber())
          {
            localObject = "brushLight";
            localObject = (Shader)localMap.get(localObject);
            if (localObject == null) {
              continue;
            }
            GLES20.glUseProgram(((Shader)localObject).program);
            if (Painting.this.brushTexture == null) {
              Painting.access$602(Painting.this, new Texture(Painting.this.brush.getStamp()));
            }
            GLES20.glActiveTexture(33984);
            GLES20.glBindTexture(3553, Painting.this.brushTexture.texture());
            GLES20.glUniformMatrix4fv(((Shader)localObject).getUniform("mvpMatrix"), 1, false, FloatBuffer.wrap(Painting.this.projection));
            GLES20.glUniform1i(((Shader)localObject).getUniform("texture"), 0);
            localObject = Render.RenderPath(paramPath, Painting.this.renderState);
            GLES20.glBindFramebuffer(36160, 0);
            if (Painting.this.delegate != null) {
              Painting.this.delegate.contentChanged((RectF)localObject);
            }
            if (Painting.this.activeStrokeBounds == null) {
              break label342;
            }
            Painting.this.activeStrokeBounds.union((RectF)localObject);
          }
          for (;;)
          {
            if (paramRunnable == null) {
              break label352;
            }
            paramRunnable.run();
            break;
            localObject = "brush";
            break label141;
            Painting.access$1002(Painting.this, (RectF)localObject);
          }
        }
      }
    });
  }
  
  public void render()
  {
    if (this.shaders == null) {}
    for (;;)
    {
      return;
      if (this.activePath != null) {
        render(getPaintTexture(), this.activePath.getColor());
      } else {
        renderBlit();
      }
    }
  }
  
  public void setBitmap(Bitmap paramBitmap)
  {
    if (this.bitmapTexture != null) {}
    for (;;)
    {
      return;
      this.bitmapTexture = new Texture(paramBitmap);
    }
  }
  
  public void setBrush(Brush paramBrush)
  {
    this.brush = paramBrush;
    if (this.brushTexture != null)
    {
      this.brushTexture.cleanResources(true);
      this.brushTexture = null;
    }
  }
  
  public void setDelegate(PaintingDelegate paramPaintingDelegate)
  {
    this.delegate = paramPaintingDelegate;
  }
  
  public void setRenderProjection(float[] paramArrayOfFloat)
  {
    this.renderProjection = paramArrayOfFloat;
  }
  
  public void setRenderView(RenderView paramRenderView)
  {
    this.renderView = paramRenderView;
  }
  
  public void setupShaders()
  {
    this.shaders = ShaderSet.setup();
  }
  
  public class PaintingData
  {
    public Bitmap bitmap;
    public ByteBuffer data;
    
    PaintingData(Bitmap paramBitmap, ByteBuffer paramByteBuffer)
    {
      this.bitmap = paramBitmap;
      this.data = paramByteBuffer;
    }
  }
  
  public static abstract interface PaintingDelegate
  {
    public abstract void contentChanged(RectF paramRectF);
    
    public abstract DispatchQueue requestDispatchQueue();
    
    public abstract UndoStore requestUndoStore();
    
    public abstract void strokeCommited();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Components/Paint/Painting.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */