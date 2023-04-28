package fr.univtln.jlaffaill662.Environment;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.texture.Texture;

public class Door extends Node{

    public Door(int id, AssetManager assetManager, BulletAppState bulletAppState, int posX, int posY) {
        super("Door");
        setLocalTranslation(posX, posY+1, 0f);
        
        Geometry geo = new Geometry("DoorGeometry", new Box(0.5f, 2f, 1f));
        Material m = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        Texture doorImg = assetManager.loadTexture("Textures/Environment/Door.png");
        m.setTexture("ColorMap", doorImg);
        m.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);

        geo.setMaterial(m);
        geo.setLocalTranslation(posX, posY+1, 0f);

        setUserData("ID", id);

        RigidBodyControl rb = new RigidBodyControl( new BoxCollisionShape(new Vector3f(0.5f, 1f, 1f)), 0);
        geo.addControl(rb);

        bulletAppState.getPhysicsSpace().add(rb);
        
        attachChild(geo);
    }
}
