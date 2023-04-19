package fr.univtln.jlaffaill662.Character.platformer;

import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;

import fr.univtln.jlaffaill662.Character.InputMapping;

public class PlayerPlatformerControls extends AbstractControl{

    private final InputMapping left = new InputMapping("Left", new int[] { KeyInput.KEY_A, KeyInput.KEY_LEFT });
    private final InputMapping right = new InputMapping("Right", new int[] { KeyInput.KEY_D, KeyInput.KEY_RIGHT });
    // private final InputMapping up = new InputMapping("Up", new int[] { KeyInput.KEY_W, KeyInput.KEY_UP });
    // private final InputMapping down = new InputMapping("Down", new int[] { KeyInput.KEY_S, KeyInput.KEY_DOWN });
    private final InputMapping jump = new InputMapping("Jump", new int[] { KeyInput.KEY_SPACE });

    private final float speed = 45f;
    private final int jumpForce = 120;
    private float movingForceFactor = 1f; //default to 1f, not on ground, set it to 0.7f
    private final float decelarationFactor = 0.15f;
    private final float additionnalGravityPull = 10f;
    // private final float airDragForce = 0.7f;

    private Vector3f moveDir = new Vector3f(0f, 0f, 0f);

    private final int MAX_Y_VELOCITY = -3;

    private boolean canMove = true;
    private boolean isJumping;
    private boolean isGrounded;

    private float jumpTimer = 0f;
    private final float jumpTimerMax = 0.2f;

    private InputManager inputManager;
    private BulletAppState bulletAppState;
    private RigidBodyControl spatialRb;

    private Node floorLayout;
    private CollisionResults res = new CollisionResults();

    private RigidBodyControl floorRb;

    public PlayerPlatformerControls(Node rootNode, InputManager inputManager, BulletAppState bulletAppState) {
        floorLayout = (Node) rootNode.getChild("FloorLayout");
        this.inputManager = inputManager;
        this.bulletAppState = bulletAppState;
    }

    @Override
    public void setSpatial(Spatial spatial) {
        super.setSpatial(spatial);
        if (spatial != null) {
            spatialRb = (RigidBodyControl) spatial.getControl(RigidBodyControl.class);
            initInputs();
            initPhysics();
        }
    }

    public void setFloorNode(Spatial floor) { 
        this.floorRb = (RigidBodyControl) floor.getControl(RigidBodyControl.class); 
    }

    public void setCanMove(boolean value) { canMove = value; }

    private void initInputs(){
        // inputManager.deleteMapping( SimpleApplication.INPUT_MAPPING_MEMORY);
        // inputManager.deleteMapping( SimpleApplication.INPUT_MAPPING_CAMERA_POS );
        // inputManager.deleteMapping( SimpleApplication.INPUT_MAPPING_HIDE_STATS );

        if (!isAlreadyMapped(left)) addMapping(left);
        if (!isAlreadyMapped(right)) addMapping(right);
        // if (!isAlreadyMapped(up)) addMapping(up);
        // if (!isAlreadyMapped(down)) addMapping(down);
        if (!isAlreadyMapped(jump)) addMapping(jump);

        inputManager.addListener(analogListener, left.getMappingName(), right.getMappingName());// up.getMappingName(), down.getMappingName() );
        inputManager.addListener(actionListener, jump.getMappingName() );
    }

    private void initPhysics(){
        bulletAppState.getPhysicsSpace().addCollisionListener(collisionListener);
    }

    private boolean isAlreadyMapped(InputMapping input) { return inputManager.hasMapping( input.getMappingName() ); }
    private void addMapping(InputMapping input) { inputManager.addMapping( input.getMappingName(), input.getMappings() ); }

    private ActionListener actionListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean isPressed, float tpf) {
            if (!canMove) return;
            if (isJumping) return;
            if (!isGrounded) return;

            if (name.equals("Jump") && !isPressed) isJumping = true;
        };
    };

    private AnalogListener analogListener = new AnalogListener() {
        @Override
        public void onAnalog(String name, float value, float tpf) {
            
            if (value == 0f) {
                moveDir = Vector3f.ZERO;
                return;
            }
            if (!canMove) return;

            if (name.equals("Left")) moveDir.x -= speed;
            if (name.equals("Right")) moveDir.x += speed;
            // if (name.equals("Up")) moveDir.z -= speed;
            // if (name.equals("Down")) moveDir.z += speed;

            moveDir.mult( tpf );
        };
    };

    private PhysicsCollisionListener collisionListener = new PhysicsCollisionListener() {
        @Override
        public void collision(PhysicsCollisionEvent event) {
            if (isJumping) return;
            PhysicsCollisionObject p1 = event.getObjectA();
            PhysicsCollisionObject p2 = event.getObjectB();

            if (p1 == spatialRb && p2 == floorRb || p1 == floorRb && p2 == spatialRb){
                isGrounded = true;
                moveDir.y = 0;
                movingForceFactor = 1f;
            }
        }
    };

    @Override
    protected void controlUpdate(float tpf) {
        if (!canMove) return;
        limitYSpeed();
        getFeetNode();

        jump(tpf);
        move();

        if (!isGrounded) {
            additionnalGravity();
            // airDrag();
        } 
    }

    private void limitYSpeed() {
        moveDir.y = moveDir.y <= MAX_Y_VELOCITY ? MAX_Y_VELOCITY : moveDir.y;
    }

    private void getFeetNode() {
        Ray ray = new Ray(spatial.getLocalTranslation().subtract( new Vector3f(0f, 1.5f, 0f) ), Vector3f.UNIT_Y.negate());
        floorLayout.collideWith(ray, res);

        if (res.size() <= 0) return;
        CollisionResult r = res.getClosestCollision();
        res.clear();
        // RigidBodyControl rb = r.getGeometry().getControl(RigidBodyControl.class);
        setFloorNode( r.getGeometry() );
    }

    private void jump(float tpf) {
        if (isJumping && isGrounded) doJump();
        if (jumpTimer > 0){
            jumpTimer -= tpf;
            if (jumpTimer <= 0) isJumping = false; //helps for collision delay, and prevent player from jumping over and over
        }
    }

    private void move() {
        if (moveDir.distance(Vector3f.ZERO) <= 0.1f) return;
        spatialRb.applyCentralForce( moveDir.mult(movingForceFactor) );

        if (moveDir.distance(Vector3f.ZERO) > 0.01f) decelarate();
    }

    private void doJump() {
        spatialRb.applyImpulse( Vector3f.UNIT_Y.mult( jumpForce ) , Vector3f.ZERO );
        isGrounded = false;
        movingForceFactor = 0.7f;
        jumpTimer = jumpTimerMax;
    }

    private void decelarate() {
        float yVelocity = moveDir.y;
        moveDir = moveDir.add( moveDir.negate().mult( decelarationFactor ) ); 
        moveDir.y = yVelocity;
    }

    private void additionnalGravity() {
        moveDir = moveDir.add( Vector3f.UNIT_Y.negate().mult( additionnalGravityPull ) );
    }

    // private void airDrag() {
    //     moveDir = moveDir.add( new Vector3f( -moveDir.x * airDragForce, 0f, -moveDir.z * airDragForce) );
    // }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) { }
    
}
