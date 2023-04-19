package fr.univtln.jlaffaill662.Character.platformer;

import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.app.state.BaseAppState;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.input.InputManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.control.Control;
import com.jme3.scene.shape.Box;

import fr.univtln.jlaffaill662.Environment.CollisionEnum;
import fr.univtln.jlaffaill662.Scenes.ILevel;
import fr.univtln.jlaffaill662.Scenes.LevelSelector;

public class PlayerPlatformer extends BaseAppState{

    private final float playerHeight = 1.5f;
    private final float playerRadius = 0.5f;
    private final float playerMass = 7f;
    private final Vector3f gravity = new Vector3f(0f, -400f, 0f);
    private final float friction = 5f;
    // private final float linearDamping = 0.9f;

    private Node player;
    private Control playerControl;

    private Vector3f startPos;

    private Node rootNode;
    // private AssetManager assetManager;
    private AppStateManager stateManager;
    private BulletAppState bulletAppState;

    @Override
    protected void initialize(Application app) {
        rootNode = (Node) app.getViewPort().getScenes().get(0);
        // assetManager = app.getAssetManager();
        stateManager = app.getStateManager();

        // Spatial playerSpatial = assetManager.loadAsset("Models/Player/Platformer.java");
        Geometry testPlayer = new Geometry("TestPlayer", new Box(0.5f, playerHeight, 0.5f) );
        Material m = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        m.setColor("Color", ColorRGBA.Blue);
        testPlayer.setMaterial(m);
        
        player = new Node("PlayerPlatform");
        player.attachChild(testPlayer);
        // player.attachChild(playerSpatial);

        setDefaultPosition( app.getStateManager().getState(LevelSelector.class).getCurrentLevel() );
        setupPhysics();
        setupInputControls( app.getInputManager() );

        rootNode.attachChild(player);
    }

    private void setDefaultPosition( ILevel level ) { 
        startPos = level.getPlayerPos();
        setPosition(startPos);
    }
    private void setPosition( Vector3f pos ) { player.setLocalTranslation(pos); }

    public void respawn() { setPosition(startPos); }

    public void setCanMove(boolean value) { ((PlayerPlatformerControls) playerControl).setCanMove(value); }

    private void setupPhysics() {
        RigidBodyControl playerRb = new RigidBodyControl( new BoxCollisionShape( new Vector3f(playerRadius, playerHeight, playerRadius) ), playerMass );
        playerRb.setAngularFactor(0f);
        playerRb.setGravity( gravity );
        playerRb.setFriction( friction );
        // playerRb.setLinearDamping( linearDamping );

        playerRb.setCollisionGroup( CollisionEnum.PLAYER.ordinal() );
        playerRb.setCollideWithGroups( CollisionEnum.DEFAULT.ordinal() );
        playerRb.addCollideWithGroup( CollisionEnum.WALL.ordinal() );

        player.addControl(playerRb);

        bulletAppState = stateManager.getState(BulletAppState.class);
        bulletAppState.getPhysicsSpace().add(playerRb);
    }

    private void setupInputControls( InputManager inputManager ) {
        playerControl = new PlayerPlatformerControls( rootNode, inputManager, bulletAppState );
        playerControl.setSpatial(player);
        ((PlayerPlatformerControls) playerControl).setFloorNode( rootNode.getChild("Floor") );
        player.addControl(playerControl);
    }

    @Override
    protected void cleanup(Application app) {
        player.removeFromParent();
    }

    @Override
    protected void onEnable() { }

    @Override
    protected void onDisable() { }
}
