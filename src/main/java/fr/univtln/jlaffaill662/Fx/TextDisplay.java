package fr.univtln.jlaffaill662.Fx;

import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Node;

public class TextDisplay {

    public static void showTxt(AssetManager assetManager, Node guiNode, String text, ColorRGBA color, int posX, int posY, int size) {
        BitmapFont guiFont = assetManager.loadFont("Interface/Fonts/Console.fnt");

        BitmapText txt = new BitmapText(guiFont);
        txt.setSize(size);
        txt.setText(text);
        txt.setColor(color);

        float xPos = posX - txt.getLineWidth() / 2;
        float yPos = posY + txt.getLineHeight() / 2;

        txt.setLocalTranslation( xPos, yPos, 0 );

        BitmapText outline = new BitmapText(guiFont);
        outline.setSize(size + 1);
        outline.setText(text);
        outline.setColor(ColorRGBA.Black);
        outline.setLocalTranslation( xPos - 1, yPos + 1, -1 );

        guiNode.attachChild(txt);
        guiNode.attachChild(outline);
    }
}
