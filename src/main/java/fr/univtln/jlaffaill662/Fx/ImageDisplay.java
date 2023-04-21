package fr.univtln.jlaffaill662.Fx;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Node;
import com.jme3.texture.Texture2D;
import com.jme3.ui.Picture;

public class ImageDisplay {

    public static void showImage(AssetManager assetManager, Node guiNode, String pathToImage, int posX, int posY, int sizeX, int sizeY) {
        
        Texture2D tex = new Texture2D( assetManager.loadTexture(pathToImage).getImage() );

        Picture pic = new Picture(pathToImage);
        pic.setTexture(assetManager, tex, true);

        pic.setWidth(sizeX);
        pic.setHeight(sizeY);
        pic.setPosition(posX - sizeX/2, posY + sizeY/2);

        guiNode.attachChild(pic);
    }
}
