package fr.univtln.jlaffaill662.Character.camera;

import com.jme3.app.Application;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Sphere;
import com.jme3.texture.Texture;

import fr.univtln.jlaffaill662.Environment.CollisionEnum;
import fr.univtln.jlaffaill662.Game.GameManager;

public class Projectile {

    public enum Type {
        BALL
    }

    private static GameManager gameManager;

    private final static float SHOOTING_PERFORMANCE_FORCE = 28f;

    public static void shoot(Application app, Vector3f startPos) {
        AssetManager am = app.getAssetManager();
        Node rootNode = (Node) app.getViewPort().getScenes().get(0);
        Camera cam = app.getCamera();
        BulletAppState bulletAppState = app.getStateManager().getState(BulletAppState.class);

        if (gameManager == null) gameManager = app.getStateManager().getState(GameManager.class);

        Vector3f spawnPos = cam.getWorldCoordinates( new Vector2f(startPos.x, startPos.y), 0);
        spawnPos.z -= 0.2f; //prevent z-fighting from cam perspective
        Vector3f shootDirection = new Vector3f( spawnPos.x - cam.getLocation().x, (spawnPos.y - cam.getLocation().y) + 0.22f, cam.getDirection().z);

        switch ( gameManager.getProjectile() ) {
            case BALL:
                shootProjectile(am, rootNode, bulletAppState, spawnPos, shootDirection);
                break;
        }
    }

    private static Geometry makeProjectileGeometry(String name, Mesh mesh, Material material) {
        Geometry g = new Geometry(name, mesh);
        g.setMaterial(material);
        return g;
    }

    private static RigidBodyControl makeProjectilePhysics(CollisionShape shape, float mass) { return new RigidBodyControl(shape, mass); }

    private static void shootProjectile(AssetManager assetManager, Node rootNode, BulletAppState bulletAppState, Vector3f startPos, Vector3f shootDir) {
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        Texture ballTex = assetManager.loadTexture("Textures/ball.png");
        mat.setTexture("ColorMap", ballTex);

        Mesh mesh = new Sphere(10, 10, 0.2f, false, false);
        Geometry testBox = makeProjectileGeometry("Projectile", mesh, mat);

        testBox.setLocalTranslation(startPos);

        RigidBodyControl rb = makeProjectilePhysics(new SphereCollisionShape(0.2f), 2);
        rb.setCollisionGroup( CollisionEnum.PROJECTILE.ordinal() );
        rb.setCollideWithGroups( CollisionEnum.GHOST.ordinal() );
        rb.addCollideWithGroup( CollisionEnum.WALL.ordinal() );
        rb.addCollideWithGroup( CollisionEnum.DEFAULT.ordinal() );
        testBox.addControl(rb);

        rb.setLinearVelocity(shootDir.normalize().mult(SHOOTING_PERFORMANCE_FORCE));

        bulletAppState.getPhysicsSpace().add(rb);
        rootNode.attachChild(testBox);
    }
}
