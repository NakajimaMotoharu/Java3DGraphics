import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

// テクスチャマッピングの処理を行うためのクラス
public class ImageCalc {
	// テクスチャ貼り付け用
	public static BufferedImage movingImageTex(BufferedImage in, BufferedImage out, Polygon pIn, Polygon pOut, double[][] depth, double dist, int[] color){
		Polygon pNowIn = new Polygon(pIn.xpoints, pIn.ypoints, pIn.npoints);
		Polygon pNowOut = new Polygon(pOut.xpoints, pOut.ypoints, pOut.npoints);

		// 画像1の変換元領域を取得
		BufferedImage tmpIn = getCutImage(in, pIn);
		if (tmpIn == null){
			return out;
		}
		Rectangle rTmpIn = pNowIn.getBounds();
		pNowIn.translate(-rTmpIn.x, -rTmpIn.y);

		// 画像1を正規化(点0, 1で構成される辺をy = 0の位置に移動)
		tmpIn = getNormalizationImage(tmpIn, pNowIn);
		if (tmpIn == null){
			return out;
		}

		// 画像2の変換後領域を取得
		BufferedImage tmpOut = getCutImage(out, pOut);
		if (tmpOut == null){
			return out;
		}
		Rectangle rTmpOut = pNowOut.getBounds();
		pNowOut.translate(-rTmpOut.x, -rTmpOut.y);

		// 画像2を正規化
		tmpOut = getNormalizationImage(tmpOut, pNowOut);
		if (tmpOut == null){
			return out;
		}

		// 変換前後での底面・高さの倍率を取得し、拡大縮小変換を適用
		double dx = (double) pNowOut.xpoints[1] / pNowIn.xpoints[1];
		double dy = (double) pNowOut.ypoints[2] / pNowIn.ypoints[2];
		tmpIn = getResizeImage(tmpIn, dx, dy);
		if (tmpIn == null){
			return out;
		}
		int[] xList = pNowIn.xpoints.clone(), yList = pNowIn.ypoints.clone();
		pNowIn.reset();
		for (int i = 0; i < xList.length; i = i + 1){
			pNowIn.addPoint((int) (xList[i] * dx), (int) (yList[i] * dy));
		}

		// せん断加工
		double rad1 = Math.atan2(pNowIn.ypoints[2], pNowIn.xpoints[2]);
		double rad2 = Math.atan2(pNowOut.ypoints[2], pNowOut.xpoints[2]);
		tmpIn = getShearMappingImage(tmpIn, rad1 - rad2, pNowIn);
		if (tmpIn == null){
			return out;
		}

		// 画像2の角度へせん断し、回転 操作後の位置にpNowInが変更される
		tmpIn = getDeNormalization(tmpIn, pNowIn, pOut);
		if (tmpIn == null){
			return out;
		}

		// 画像2へ重ね合わせ
		BufferedImage ans = new BufferedImage(out.getWidth(), out.getHeight(), BufferedImage.TYPE_INT_RGB);
		Rectangle rt = pOut.getBounds();
		Graphics2D g2d = ans.createGraphics();
		g2d.drawImage(out, 0, 0, null);
		g2d.dispose();
		for (int x = rt.x; x <= rt.x + rt.width; x = x + 1){
			for (int y = rt.y; y <= rt.y + rt.height; y = y + 1){
				if (pOut.contains(x, y) && (depth[y][x] >= dist)){
					int tc = tmpIn.getRGB(x - rt.x, y - rt.y);
					Color cl = new Color(tc);
					int r = (int) (cl.getRed() * (color[0] / 255d));
					int g = (int) (cl.getGreen() * (color[1] / 255d));
					int b = (int) (cl.getBlue() * (color[2] / 255d));

					ans.setRGB(x, y, (r << 16 | g << 8) | b);
					depth[y][x] = dist;
				}
			}
		}

		return ans;
	}

	// 正規化された画像を指定されたPolygonに変換

	public static BufferedImage getDeNormalization(BufferedImage img, Polygon in, Polygon out){
		// 各ラウンドでのPolygon生成
		int[] xp = out.xpoints.clone(), yp = out.ypoints.clone();

		// ステップ1 pを点0を原点へ(po1) imgに変更なし
		int dx = xp[0], dy = yp[0];
		for (int i = 0; i < out.npoints; i = i + 1){
			xp[i] = xp[i] - dx;
			yp[i] = yp[i] - dy;
		}
		Polygon po1 = new Polygon(xp, yp, out.npoints);

		// ステップ2 点0, 点1で構成される辺をy = 0へ(po2)
		xp = po1.xpoints.clone();
		yp = po1.ypoints.clone();
		Polygon po2 = new Polygon();
		double th2 = Math.atan2(yp[1], xp[1]);
		for (int i = 0; i < xp.length; i = i + 1){
			int x = xp[i], y = yp[i];
			double rad = Math.atan2(y, x) - th2;
			double l = Math.sqrt(x * x + y * y);
			int nx = (int) (l * Math.cos(rad));
			int ny = (int) (l * Math.sin(rad));
			po2.addPoint(nx, ny);
		}

		// ステップ3 もし点2のy座標が負なら反転(po3)
		Polygon po3 = new Polygon();
		po3.addPoint(po2.xpoints[0], po2.ypoints[0]);
		po3.addPoint(po2.xpoints[1], po2.ypoints[1]);
		if (po2.ypoints[2] < 0){
			po3.addPoint(po2.xpoints[2], -po2.ypoints[2]);
		} else {
			po3.addPoint(po2.xpoints[2], po2.ypoints[2]);
		}

		// ステップ3状態にimgを変換
		Rectangle ro3 = po3.getBounds();
		if (ro3.width == 0 || 0 == ro3.height) return null;
		BufferedImage map3 = new BufferedImage(ro3.width, ro3.height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = map3.createGraphics();
		AffineTransform af = g2d.getTransform();
		af.setToShear(Math.tan(Math.PI / 2 - Math.atan2(po3.ypoints[2], po3.xpoints[2])), 0);
		g2d.setTransform(af);
		g2d.drawImage(img, 0, 0, null);
		g2d.dispose();

		// ステップ2状態にimgを変換
		Rectangle ro2 = po2.getBounds();
		BufferedImage map2 = new BufferedImage(ro2.width, ro2.height, BufferedImage.TYPE_INT_RGB);
		g2d = map2.createGraphics();
		if (po2.ypoints[2] < 0){
			g2d.drawImage(map3, 0, ro2.height, ro2.width, -ro2.height, null);
		} else {
			g2d.drawImage(map3, 0, 0, null);
		}
		g2d.dispose();

		// ステップ1状態にimgを変換
		Rectangle ro1 = po1.getBounds();
		int field = (int) (Math.sqrt(ro1.width * ro1.width + ro1.height * ro1.height) * 2);
		BufferedImage map1 = new BufferedImage(field, field, BufferedImage.TYPE_INT_RGB);
		g2d = map1.createGraphics();
		af = g2d.getTransform();
		af.setToRotation(th2, field / 2.0, field / 2.0);
		g2d.setTransform(af);
		g2d.drawImage(map2, field / 2, field / 2, null);
		g2d.dispose();

		// 回答を提出
		BufferedImage ans = new BufferedImage(ro1.width, ro1.height, BufferedImage.TYPE_INT_RGB);
		g2d = ans.createGraphics();
		g2d.drawImage(map1, -field / 2 - ro1.x, -field / 2 - ro1.y, null);
		g2d.dispose();

		// inを最終加工
		in.reset();
		for (int i = 0; i < 3; i = i + 1){
			in.addPoint(po1.xpoints[i] - ro1.x, po1.ypoints[i] - ro1.y);
		}

		return ans;
	}

	// 画像を平行方向にthetaせん断 outの正方形に出力
	public static BufferedImage getShearMappingImage(BufferedImage img, double theta, Polygon p){
		// 変換後の点3のx座標より画像サイズを決定
		double rad = Math.atan2(p.ypoints[2], p.xpoints[2]) - theta;
		if (rad == 0) return null;
		int aftX = (int) (Math.cos(rad) / Math.sin(rad) * p.ypoints[2]);

		int[] xList = p.xpoints.clone(), yList = p.ypoints.clone();
		p.reset();
		p.addPoint(xList[0], yList[0]);
		p.addPoint(xList[1], yList[1]);
		p.addPoint(aftX, yList[2]);

		boolean mov = false;
		int height = img.getHeight(), width = img.getWidth();
		if (aftX < 0){
			width = width - aftX;
			mov = true;
			p.translate(-aftX, 0);
		} else if (aftX > width){
			width = aftX;
		}

		BufferedImage tmp = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

		Graphics2D g2d = tmp.createGraphics();
		AffineTransform at = g2d.getTransform();
		at.setToShear(theta, 0);
		g2d.setTransform(at);
		if (mov){
			g2d.drawImage(img, -aftX, 0, null);
		} else {
			g2d.drawImage(img, 0, 0, null);
		}
		g2d.dispose();

		return tmp;
	}

	// 画像をdx, dy倍してreturn
	public static BufferedImage getResizeImage(BufferedImage img, double dx, double dy) {
		boolean fx = false, fy = false;

		// 倍率が負だった場合の前処理
		if (dx < 0){
			fx = true;
			dx = -dx;
		}
		if (dy < 0){
			fy = true;
			dy = -dy;
		}

		if ((int) (img.getWidth() * dx) == 0 || 0 == (int) (img.getHeight() * dy)) return null;
		BufferedImage ans = new BufferedImage((int) (img.getWidth() * dx), (int) (img.getHeight() * dy), BufferedImage.TYPE_INT_RGB);
		int x1 = 0, x2 = ans.getWidth(), y1 = 0, y2 = ans.getHeight();

		// 倍率が不だった場合の後処理
		if (fx){
			x1 = x2;
			x2 = -x2;
		}
		if (fy){
			y1 = y2;
			y2 = -y2;
		}

		Graphics2D g2d = ans.createGraphics();
		g2d.drawImage(img, x1, y1, x2, y2, null);
		g2d.dispose();

		return ans;
	}

	// imgを正規化(点0, 1で構成される辺をy = 0の位置に移動、この時点0は原点へ) 引数imgはすでにgetCutImage加工されているものとする(pも)
	public static BufferedImage getNormalizationImage(BufferedImage img, Polygon p){
		Rectangle rectIn = p.getBounds();
		int field = (int) Math.sqrt(((rectIn.width - rectIn.x) * (rectIn.width - rectIn.x)) + ((rectIn.height - rectIn.y) * (rectIn.height - rectIn.y))) * 2;
		BufferedImage map1 = new BufferedImage(field, field, BufferedImage.TYPE_INT_RGB);

		// 点0が原点(中心)になるようにマップに画像描画
		Graphics2D g2d = map1.createGraphics();
		g2d.drawImage(img, field / 2 - p.xpoints[0], field / 2 - p.ypoints[0], null);
		g2d.dispose();

		// 何度回転が必要なのかthetaとして計算+アフィン変換を利用して回転
		double theta = Math.atan2(p.ypoints[1] - p.ypoints[0], p.xpoints[1] - p.xpoints[0]);
		BufferedImage map2 = new BufferedImage(field, field, BufferedImage.TYPE_INT_RGB);
		g2d = map2.createGraphics();
		AffineTransform at = g2d.getTransform();
		at.setToRotation(-theta, field / 2.0, field / 2.0);
		g2d.setTransform(at);
		g2d.drawImage(map1, 0, 0, null);
		g2d.dispose();

		// ひとつ上の処理に合わせてPolygonの位置再調整
		int[] xp = p.xpoints.clone(), yp = p.ypoints.clone();
		p.reset();
		for (int i = 0; i < xp.length; i = i + 1){
			int x = xp[i] - xp[0], y = yp[i] - yp[0];
			double rad = Math.atan2(y, x) - theta;
			double l = Math.sqrt(x * x + y * y);
			int nx = (int) (l * Math.cos(rad));
			int ny = (int) (l * Math.sin(rad));
			p.addPoint(nx, ny);
		}

		// もし点2のy座標が負であれば上下反転
		BufferedImage map3 = new BufferedImage(field, field, BufferedImage.TYPE_INT_RGB);
		g2d = map3.createGraphics();
		if (p.ypoints[2] < 0){
			// 画像反転処理
			g2d.drawImage(map2, 0, field, field, -field, null);
			// polygon再設定
			int[] x = p.xpoints, y = p.ypoints;
			p.reset();
			for (int i = 0; i < x.length; i = i + 1){
				if (y[i] < 0){
					y[i] = -y[i];
				}
				p.addPoint(x[i], y[i]);
			}
		} else {
			// なにもしない
			g2d.drawImage(map2, 0, 0, null);
		}
		g2d.dispose();

		// 点0における角度が90度になるように設定し、画像切り取り
		double rad = Math.atan2(p.ypoints[2], p.xpoints[2]);
		xp = p.xpoints.clone();
		yp = p.ypoints.clone();
		p.reset();
		p.addPoint(xp[0], yp[0]);
		p.addPoint(xp[1], yp[1]);
		p.addPoint(0, yp[2]);

		Rectangle ans = p.getBounds();
		if (ans.width == 0 || ans.height == 0) return null;
		BufferedImage map4 = new BufferedImage(ans.width, ans.height, BufferedImage.TYPE_INT_RGB);
		g2d = map4.createGraphics();

		at = g2d.getTransform();
		at.setToShear(-Math.tan(Math.PI / 2 - rad), 0);
		g2d.setTransform(at);

		g2d.drawImage(map3, field / -2, field / -2, null);
		g2d.dispose();

		return map4;
	}

	// imgから指定された領域を取得
	public static BufferedImage getCutImage(BufferedImage img, Polygon src){
		Rectangle rect = src.getBounds();
		if (rect.width == 0 || 0 == rect.height) return null;
		BufferedImage ans = new BufferedImage(rect.width, rect.height, BufferedImage.TYPE_INT_RGB);

		Graphics2D g2d = ans.createGraphics();
		g2d.drawImage(img, -rect.x, -rect.y, null);
		g2d.dispose();

		return ans;
	}

}
