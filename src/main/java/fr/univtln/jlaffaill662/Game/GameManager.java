package fr.univtln.jlaffaill662.Game;

import java.util.ArrayList;
import java.util.List;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.control.GhostControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;

import fr.univtln.jlaffaill662.App;
import fr.univtln.jlaffaill662.Character.platformer.PlayerPlatformer;
import fr.univtln.jlaffaill662.Environment.CollisionEnum;
import fr.univtln.jlaffaill662.Environment.TriggerZone;
import fr.univtln.jlaffaill662.Scenes.LevelSelector;

public class GameManager extends BaseAppState {

    private Node rootNode;
    private AssetManager assetManager;
    private BulletAppState bulletAppState;

    private Camera cam;
    private Spatial lava;

    private float lavaOffset = 3f;

    // private PlayerPlatformer player;
    private RigidBodyControl playerRb;

    private Node endGateNode;
    private GhostControl endGate;

    private LevelSelector level;

    private App simpleApp;

    private Vector3f initCamPos;
    private Vector3f initLavaPos;

    private float timer = 0f;
    private final float RISE_FACTOR = 0.001f;

    @Override
    protected void initialize(Application app) {
        rootNode = (Node) app.getViewPort().getScenes().get(0);
        assetManager = app.getAssetManager();
        bulletAppState = app.getStateManager().getState(BulletAppState.class);
        level = app.getStateManager().getState(LevelSelector.class);
        cam = app.getCamera();

        initCamPos = cam.getLocation();

        Spatial playerSpatial = rootNode.getChild("PlayerPlatform");
        // player = app.getStateManager().getState(PlayerPlatformer.class);
        playerRb = playerSpatial.getControl(RigidBodyControl.class);

        initLava();
        initEndGate();

        // bulletAppState.setDebugEnabled(true);
        bulletAppState.getPhysicsSpace().addCollisionListener( triggerListener );
    }

    private void initLava() {
        lava = new Geometry("LavaGeo", new Box(30, 100, 5));
        Material lavaM = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        lavaM.setColor("Color", ColorRGBA.Red);

        lava.setMaterial(lavaM);
        lava.setLocalTranslation(0, -100, 0);
        initLavaPos = lava.getLocalTranslation();

        rootNode.attachChild(lava);
    }

    private void initEndGate() {
        endGateNode = new TriggerZone("EndGate", 
                                    rootNode, 
                                    bulletAppState, 
                                    new BoxCollisionShape(new Vector3f(1f, 1f, 1f)),
                                    CollisionEnum.PLAYER);
                                
        endGateNode.setLocalTranslation( level.getCurrentLevel().getEndGatePos() );
        endGate = endGateNode.getControl(GhostControl.class);

        rootNode.attachChild(endGateNode);
    }

    public void setSimpleApp(App app) { simpleApp = app; }

    @Override
    public void update(float tpf) {
        super.update(tpf);

        timer += tpf; //game timer
        rise();

        //check if player y level is below camera, if so, dead
        if (playerRb.getPhysicsLocation().y <= (cam.getLocation().y - lavaOffset))
            die();
    }

    private void rise() {
        cam.setLocation( initCamPos.add( 0f, timer * RISE_FACTOR, 0f ) );
        lava.setLocalTranslation( initLavaPos.add( 0f, timer * RISE_FACTOR, 0f ) );
    }

    private PhysicsCollisionListener triggerListener = new PhysicsCollisionListener() {
        @Override
        public void collision(PhysicsCollisionEvent event) {
            //handles trigger death zone and power up to player
            PhysicsCollisionObject p1 = event.getObjectA();
            PhysicsCollisionObject p2 = event.getObjectB();

            //determine if p1 is player or p2
            if (p1 != playerRb && p2 != playerRb) return;

            //collision for endgate
            if (endGate.getOverlappingCount() > 0){
                for (PhysicsCollisionObject obj : endGate.getOverlappingObjects())
                    if (playerRb == obj) win();
            }
        }
    };

    private void win() { simpleApp.win(); }
    private void die() { simpleApp.lose(); }

    @Override
    protected void cleanup(Application app) { 
        lava.removeFromParent(); 
        endGateNode.removeFromParent();
    }

    @Override
    protected void onEnable() { }

    @Override
    protected void onDisable() { }
}
