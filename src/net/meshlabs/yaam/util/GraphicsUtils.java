package net.meshlabs.yaam.util;

import android.util.Log;

import com.threed.jpct.Loader;
import com.threed.jpct.Object3D;
import com.threed.jpct.PolygonManager;
import com.threed.jpct.SimpleVector;
import com.threed.jpct.TextureInfo;

public class GraphicsUtils {
	
	public static void tileTexture(Object3D obj, float tileFactor) {
		PolygonManager pm = obj.getPolygonManager();

		int end = pm.getMaxPolygonID();
		for (int i = 0; i < end; i++) {
			SimpleVector uv0 = pm.getTextureUV(i, 0);
			SimpleVector uv1 = pm.getTextureUV(i, 1);
			SimpleVector uv2 = pm.getTextureUV(i, 2);

			uv0.scalarMul(tileFactor);
			uv1.scalarMul(tileFactor);
			uv2.scalarMul(tileFactor);

			int id = pm.getPolygonTexture(i);

			TextureInfo ti = new TextureInfo(id, uv0.x, uv0.y, uv1.x, uv1.y,
					uv2.x, uv2.y);
			pm.setPolygonTexture(i, ti);
		}
	}
	
	/*
	 * polygons=number of polygons to print
	 * indexOffset=starting polygonID to print
	 */
	public static void printPolyInfo(Object3D obj, int polygons, int indexOffset, boolean fullInfo) {
		PolygonManager pm = obj.getPolygonManager();
		SimpleVector[] verts = new SimpleVector[3];
		SimpleVector[] uvs = new SimpleVector[3];
		
		int end = polygons+indexOffset;
		if (end < pm.getMaxPolygonID()) {
			end = pm.getMaxPolygonID();
		}
		
		for (int i=indexOffset; i<(polygons+indexOffset); i++) {
			for (int j=0; j<3; j++) {
				verts[j] = pm.getTransformedVertex(i, j);
				uvs[j] = pm.getTextureUV(i, j);
			}
			Log.i("ModelObj", "Poly"+i+" vertices:"+verts[0]+", "+verts[1]+", "+verts[2]);
			if (fullInfo) {
				Log.i("ModelObj", "Poly"+i+" UVs:"+uvs[0]+", "+uvs[1]+", "+uvs[2]);
			}
		}
	}

}
