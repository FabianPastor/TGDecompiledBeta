package org.telegram.ui.Components.Premium.GLIcon;

import android.content.Context;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;

public final class ObjLoader {
    public float[] normals;
    public int numFaces;
    public float[] positions;
    public float[] textureCoordinates;

    public ObjLoader(Context context, String file) {
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        ArrayList arrayList3 = new ArrayList();
        try {
            try {
                DataInputStream inputStream = new DataInputStream(context.getAssets().open(file));
                int n = inputStream.readInt();
                for (int i = 0; i < n; i++) {
                    arrayList.add(Float.valueOf(inputStream.readFloat()));
                }
                int n2 = inputStream.readInt();
                for (int i2 = 0; i2 < n2; i2++) {
                    arrayList3.add(Float.valueOf(inputStream.readFloat()));
                }
                int n3 = inputStream.readInt();
                for (int i3 = 0; i3 < n3; i3++) {
                    arrayList2.add(Float.valueOf(inputStream.readFloat()));
                }
                int n4 = inputStream.readInt();
                this.numFaces = n4;
                this.normals = new float[(n4 * 3)];
                this.textureCoordinates = new float[(n4 * 2)];
                this.positions = new float[(n4 * 3)];
                int positionIndex = 0;
                int normalIndex = 0;
                int textureIndex = 0;
                int i4 = 0;
                while (i4 < n4) {
                    int index = inputStream.readInt() * 3;
                    int positionIndex2 = positionIndex + 1;
                    int index2 = index + 1;
                    this.positions[positionIndex] = ((Float) arrayList.get(index)).floatValue();
                    int positionIndex3 = positionIndex2 + 1;
                    this.positions[positionIndex2] = ((Float) arrayList.get(index2)).floatValue();
                    int positionIndex4 = positionIndex3 + 1;
                    this.positions[positionIndex3] = ((Float) arrayList.get(index2 + 1)).floatValue();
                    int index3 = inputStream.readInt() * 2;
                    int normalIndex2 = normalIndex + 1;
                    this.textureCoordinates[normalIndex] = ((Float) arrayList3.get(index3)).floatValue();
                    normalIndex = normalIndex2 + 1;
                    this.textureCoordinates[normalIndex2] = 1.0f - ((Float) arrayList3.get(index3 + 1)).floatValue();
                    int index4 = inputStream.readInt() * 3;
                    int textureIndex2 = textureIndex + 1;
                    int index5 = index4 + 1;
                    this.normals[textureIndex] = ((Float) arrayList2.get(index4)).floatValue();
                    int textureIndex3 = textureIndex2 + 1;
                    this.normals[textureIndex2] = ((Float) arrayList2.get(index5)).floatValue();
                    int textureIndex4 = textureIndex3 + 1;
                    this.normals[textureIndex3] = ((Float) arrayList2.get(index5 + 1)).floatValue();
                    i4++;
                    textureIndex = textureIndex4;
                    positionIndex = positionIndex4;
                }
            } catch (IOException e) {
                e = e;
                e.printStackTrace();
            }
        } catch (IOException e2) {
            e = e2;
            String str = file;
            e.printStackTrace();
        }
    }
}
