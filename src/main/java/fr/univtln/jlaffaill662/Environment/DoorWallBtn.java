package fr.univtln.jlaffaill662.Environment;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.texture.Texture;

public class DoorWallBtn extends Node{
    public DoorWallBtn(int id, AssetManager assetManager, BulletAppState bulletAppState, int posX, int posY) {
        super("DoorWallBtn");
        setLocalTranslation(posX, posY, 0f);

        Geometry wall = new Geometry("Wall", new Box(1f, 1f, 1f));

        Material mWall = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        Texture doorWallImg = assetManager.loadTexture("Textures/Environment/WallBtn.png");
        mWall.setTexture("ColorMap", doorWallImg);

        wall.setMaterial(mWall);
        wall.setLocalTranslation(posX, posY, 0f);

        setUserData("ID", id);

        RigidBodyControl rb = new RigidBodyControl(0);
        wall.addControl(rb);

        bulletAppState.getPhysicsSpace().add(rb);
        
        attachChild(wall);
    }
}
