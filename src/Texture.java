import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

// テクスチャ情報保存クラス
public class Texture {
	public static ArrayList<BufferedImage>tex = new ArrayList<>();
	public static String[] mtl = null;

	// 指定された名前のテクスチャを捜索・保存
	public static void addTexture(String mtlName){
		for (int i = 0; i < mtl.length; i = i + 1){
			if (mtl[i].equals("newmtl " + mtlName)){
				try {
					tex.add(ImageIO.read(new File(mtl[i + 1].split(" ")[1].replace("\\", "/"))));
					break;
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}
	}

	// 現在のテクスチャidを返す
	public static int getNowPointer(){
		return tex.size() - 1;
	}

	// マテリアルファイルのフルロード
	public static void getMtlFile(String mtlFile){
		System.out.printf("マテリアルデータ[%s]の読み込み......\n", mtlFile);
		Path path = Paths.get(mtlFile);

		try {
			List<String>tmp = Files.readAllLines(path);
			String[] lines = new String[tmp.size()];

			for (int i = 0; i < tmp.size(); i = i + 1){
				lines[i] = tmp.get(i);
			}

			mtl = lines;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		System.out.printf("マテリアルデータ[%s]の読み込み完了\n", mtlFile);
	}

	// テクスチャidからテクスチャを取得
	public static BufferedImage getTexture(int n){
		if ((n >= 0) && (n < tex.size())){
			return tex.get(n);
		}
		return null;
	}
}
