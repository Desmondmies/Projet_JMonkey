package fr.univtln.jlaffaill662.Z_TEST;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Quad;

public class TestAppState extends BaseAppState {

    private int floorSize = 25;

    private Geometry floor;

    private Node rootNode;

    @Override
    protected void initialize(Application app) {
        Quad q = new Quad(floorSize, floorSize);
        floor = new Geometry("Floor", q);
        Material m = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        m.setColor("Color", ColorRGBA.Blue);
    
        floor.setMaterial(m);
        floor.rotate(-FastMath.HALF_PI, 0, 0);

        Vector3f moveToCenter = new Vector3f(-floorSize / 2, 0, floorSize / 2);
        floor.setLocalTranslation( floor.getLocalTranslation().add(moveToCenter) );
        Vector3f floorPos = floor.getLocalTranslation();
        floor.setLocalTranslation(floorPos.x, 0f, floorPos.z);

        RigidBodyControl fControl = new RigidBodyControl(0);
        floor.addControl(fControl);
        BulletAppState bulletAppState = app.getStateManager().getState(BulletAppState.class);
        bulletAppState.getPhysicsSpace().add(fControl);

        rootNode = (Node) app.getViewPort().getScenes().get(0);
        rootNode.attachChild(floor);
    }

    @Override
    protected void cleanup(Application app) {
        floor.removeFromParent();
    }

    @Override
    protected void onEnable() { }

    @Override
    protected void onDisable() { }    
}
