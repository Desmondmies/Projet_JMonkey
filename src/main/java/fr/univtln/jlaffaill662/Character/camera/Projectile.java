package fr.univtln.jlaffaill662.Character.camera;

import com.jme3.app.Application;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Sphere;

public class Projectile {

    private final static float SHOOTING_TESTBOX_FORCE = 15f;
    
    //maybe get info of projectile from GameManager
    //if player touches power up to change up projectile type
    //(like dart arrow / cannon / electric gun / water gun)
    //projectile gets info of which projectile to shoot from gameManager
    //since player most certainly change info of projectile on collision in GameManager script

    public static void shoot(Application app, Vector3f startPos) {
        //switch to projectile type got from gameManager singleton
        AssetManager am = app.getAssetManager();
        Node rootNode = (Node) app.getViewPort().getScenes().get(0);
        Camera cam = app.getCamera();
        BulletAppState bulletAppState = app.getStateManager().getState(BulletAppState.class);

        Vector3f spawnPos = cam.getWorldCoordinates( new Vector2f(startPos.x, startPos.y), 0f);
        Vector3f shootDirection = new Vector3f(spawnPos.x, cam.getDirection().y + 0.3f, cam.getDirection().z);

        shootTestBox(am, rootNode, bulletAppState, spawnPos, shootDirection);
    }

    private static Geometry makeProjectileGeometry(String name, Mesh mesh, Material material) {
        Geometry g = new Geometry(name, mesh);
        g.setMaterial(material);
        return g;
    }

    private static RigidBodyControl makeProjectilePhysics(CollisionShape shape, float mass) { return new RigidBodyControl(shape, mass); }

    private static void shootTestBox(AssetManager assetManager, Node rootNode, BulletAppState bulletAppState, Vector3f startPos, Vector3f shootDir) {
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Cyan);

        Mesh mesh = new Sphere(10, 10, 0.2f, false, false);
        Geometry testBox = makeProjectileGeometry("TestBoxProjectile", mesh, mat);

        testBox.setLocalTranslation(startPos);

        RigidBodyControl rb = makeProjectilePhysics(new SphereCollisionShape(0.2f), 2);
        testBox.addControl(rb);

        rb.setLinearVelocity(shootDir.normalize().mult(SHOOTING_TESTBOX_FORCE));

        bulletAppState.getPhysicsSpace().add(rb);
        rootNode.attachChild(testBox);
    }

    // private void shootDart() {}
    // private void shootWater() {}
    // private void shootBomb() {}
}
