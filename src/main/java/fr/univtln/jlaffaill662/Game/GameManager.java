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
import com.jme3.material.RenderState;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.texture.Texture;

import fr.univtln.jlaffaill662.App;
import fr.univtln.jlaffaill662.Character.camera.Projectile;
import fr.univtln.jlaffaill662.Environment.CollisionEnum;
import fr.univtln.jlaffaill662.Environment.DoorWallBtn;
import fr.univtln.jlaffaill662.Environment.TriggerZone;
import fr.univtln.jlaffaill662.Fx.AudioPlayer;
import fr.univtln.jlaffaill662.Fx.FireParticle;
import fr.univtln.jlaffaill662.Scenes.LevelSelector;

public class GameManager extends BaseAppState {

    private Node rootNode;
    private AssetManager assetManager;
    private BulletAppState bulletAppState;

    private Camera cam;
    private Node lava;

    private float lavaOffset = 5.8f;

    private RigidBodyControl playerRb;

    private Node endGateNode;
    private GhostControl endGate;

    private LevelSelector level;

    private App simpleApp;
    private AudioPlayer audioPlayer;

    private Vector3f initCamPos;
    private Vector3f initLavaPos;

    private float timer = 0f;
    private final float RISE_FACTOR = 0.001f;
    private final float RISE_FACTOR_MAX = 0.022f;

    private final float RISE_DELTA = 0.65f;

    private List<Node> doorsList = new ArrayList<>();
    private List<Node> btnDoorsList = new ArrayList<>();

    private Projectile.Type currentWeapon = Projectile.Type.BALL;

    @Override
    protected void initialize(Application app) {
        rootNode = (Node) app.getViewPort().getScenes().get(0);
        assetManager = app.getAssetManager();
        bulletAppState = app.getStateManager().getState(BulletAppState.class);
        level = app.getStateManager().getState(LevelSelector.class);
        cam = app.getCamera();

        audioPlayer = AudioPlayer.getInstance();

        initCamPos = cam.getLocation();

        Spatial playerSpatial = rootNode.getChild("PlayerPlatform");
        playerRb = playerSpatial.getControl(RigidBodyControl.class);

        initLava();
        initEndGate();
        initDoorBtns();
        initDoor();

        // bulletAppState.setDebugEnabled(true);
        bulletAppState.getPhysicsSpace().addCollisionListener( triggerListener );
    }

    private void initLava() {
        lava = new Node("Lava");
        Geometry l = new Geometry("LavaGeo", new Box(30, 100, 5));
        Material lavaM = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        Texture lavaImg = assetManager.loadTexture("Textures/Environment/lava.png");
        lavaM.setTexture("ColorMap", lavaImg);

        l.setMaterial(lavaM);
        lava.attachChild(l);

        FireParticle f1 = new FireParticle(assetManager, new Vector3f(2.5f, 102f, 3f));
        FireParticle f2 = new FireParticle(assetManager, new Vector3f(5.5f, 102f, 4f));
        FireParticle f3 = new FireParticle(assetManager, new Vector3f(8.6f, 102f, 3.5f));
        FireParticle f4 = new FireParticle(assetManager, new Vector3f(10.7f, 102f, 4.3f));
        FireParticle f5 = new FireParticle(assetManager, new Vector3f(13.4f, 102f, 3.3f));
        FireParticle f6 = new FireParticle(assetManager, new Vector3f(15.3f, 102f, 4f));
        lava.attachChild(f1);
        lava.attachChild(f2);
        lava.attachChild(f3);
        lava.attachChild(f4);
        lava.attachChild(f5);
        lava.attachChild(f6);

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

        Geometry endGateVisual = new Geometry("EndGateVisual", new Box(1f, 2f, 0.05f));
        Material endGateM = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        Texture t = assetManager.loadTexture("Textures/Environment/endgate.png");
        endGateM.setTexture("ColorMap", t);
        endGateM.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        endGateVisual.setQueueBucket(Bucket.Transparent);

        endGateVisual.setMaterial(endGateM);
        endGateVisual.setLocalTranslation(0f, 1f, 0f);
        endGateNode.attachChild(endGateVisual);

        rootNode.attachChild(endGateNode);
    }

    private void initDoorBtns() {
        List<Spatial> doorBtns = ((Node)rootNode.getChild("Btn_WallLayout")).getChildren();

        for(Spatial door : doorBtns) {
            TriggerZone btn = new TriggerZone("DoorBtn", 
                                            rootNode, 
                                            bulletAppState, 
                                            new BoxCollisionShape(new Vector3f(0.85f, 0.85f, 0.1f)), 
                                            CollisionEnum.PROJECTILE);
            btn.setLocalTranslation( 0f, 0f, 1.2f );
            btnDoorsList.add( (Node)door );
            ((Node) door).attachChild(btn);
        }
    }

    private void initDoor() {
        List<Spatial> doors = ((Node)rootNode.getChild("DoorLayout")).getChildren();

        for (Spatial door : doors) { doorsList.add( (Node)door ); }
    }

    public void setSimpleApp(App app) { simpleApp = app; }

    public Projectile.Type getProjectile() { return currentWeapon; }
    public void changeProjectile(Projectile.Type newWeapon) { this.currentWeapon = newWeapon; }

    @Override
    public void update(float tpf) {
        super.update(tpf);

        timer += tpf * RISE_DELTA; //game timer
        rise();

        //check if player y level is below camera, if so, dead
        if (playerRb.getPhysicsLocation().y <= (cam.getLocation().y - lavaOffset))
            die();
    }

    private void rise() {
        float riseAmount = (timer * RISE_FACTOR) > RISE_FACTOR_MAX ? RISE_FACTOR_MAX : timer * RISE_FACTOR;
        cam.setLocation( initCamPos.add( 0f, riseAmount, 0f ) );
        lava.setLocalTranslation( initLavaPos.add( 0f, riseAmount, 0f ) );
    }

    private PhysicsCollisionListener triggerListener = new PhysicsCollisionListener() {
        @Override
        public void collision(PhysicsCollisionEvent event) {
            PhysicsCollisionObject p1 = event.getObjectA();
            PhysicsCollisionObject p2 = event.getObjectB();

            //collision for endgate
            if (endGate.getOverlappingCount() > 0){
                //determine if p1 or p2 is player
                if (p1 != playerRb && p2 != playerRb) return;
                for (PhysicsCollisionObject obj : endGate.getOverlappingObjects())
                    if (playerRb == obj) win();
            }

            if (p1.getClass() == GhostControl.class || p2.getClass() == GhostControl.class){
                Spatial sP1 = (Spatial) p1.getUserObject();
                Spatial sP2 = (Spatial) p2.getUserObject();
                
                if (sP1.getName() == "Wall" || sP2.getName() == "Wall") return;
                if (sP1.getName() != "Projectile" && sP2.getName() != "Projectile") return;
                
                for (Node btn : btnDoorsList) {
                    String btnKey = btn.getUserData("ID").toString();
                    Object p1Obj = sP1.getParent().getUserData("ID");
                    Object p2Obj = sP2.getParent().getUserData("ID");

                    if (p1Obj != null)
                        if (!p1Obj.toString().equals(btnKey)) continue;
                    else if (p2Obj != null)
                        if (!p2Obj.toString().equals(btnKey)) continue;
                    else break;

                    for (Node door : doorsList) {
                        //remove door if correct btn is activated, play sound
                        if (door.getUserData("ID").toString().equals( btnKey )) {
                            door.getChild("DoorGeometry").getControl(RigidBodyControl.class).setEnabled(false);;
                            door.removeFromParent();
                            doorsList.remove(door);
                            ((DoorWallBtn) btn).changeToOnTexture();
                            btnDoorsList.remove(btn);

                            audioPlayer.playSwitch();
                            return;
                        }                        
                    }
                }
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
