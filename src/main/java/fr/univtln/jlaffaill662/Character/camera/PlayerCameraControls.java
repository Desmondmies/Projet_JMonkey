package fr.univtln.jlaffaill662.Character.camera;

import com.jme3.app.Application;
import com.jme3.input.InputManager;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;

public class PlayerCameraControls extends AbstractControl{

    private Vector2f mouseAcc = new Vector2f(0f, 0f); //useful for viewing mouse movement later with joysticks
    //use mouseAcc to clamp and change position of object in local box clamping area like zeus banquet hand
    private Vector2f mousePos = new Vector2f(0f, 0f);

    private InputManager inputManager;
    private Application app;

    public PlayerCameraControls(Application app) {
        this.app = app;
        this.inputManager = app.getInputManager();
    }

    @Override
    public void setSpatial(Spatial spatial){
        super.setSpatial(spatial);

        if (spatial != null) initInputs();
    }

    private void initInputs() {
        if (!inputManager.hasMapping("MouseX"))
            inputManager.addMapping("MouseX", new MouseAxisTrigger(MouseInput.AXIS_X, false));
        if (!inputManager.hasMapping("MouseY"))
            inputManager.addMapping("MouseY", new MouseAxisTrigger(MouseInput.AXIS_Y, false));

        //shooting interaction (mouse left button click to shoot)
        if (!inputManager.hasMapping("Shoot"))
            inputManager.addMapping("Shoot", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));

        inputManager.addListener(actionListener, "Shoot");
        inputManager.addListener(analogListener, "MouseX", "MouseY");
    }

    private ActionListener actionListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean keyPressed, float tpf) {
            if(name.equals("Shoot") && !keyPressed) shoot();
        }
    };

    private AnalogListener analogListener = new AnalogListener() {
        @Override
        public void onAnalog(String name, float value, float tpf) {
            if (value == 0f) {
                mouseAcc = Vector2f.ZERO;
                return;
            }

            if (name.equals("MouseX")) mouseAcc.x = value;
            if (name.equals("MouseY")) mouseAcc.y = value;

            //modify mouse joystick pos based on mouseAcc            
        }
    };

    private void shoot(){
        //shoot from cursor to scene, cam direction
        Projectile.shoot(app, spatial.getLocalTranslation());
    }

    @Override
    protected void controlUpdate(float tpf) {
        mousePos = inputManager.getCursorPosition();
        // float z = cam.getViewToProjectionZ(3);
        // Vector3f mouseWorldPos = cam.getWorldCoordinates( new Vector2f(mousePos.x, mousePos.y), z);
        // spatial.setLocalTranslation(mouseWorldPos);
        spatial.setLocalTranslation( new Vector3f( mousePos.x, mousePos.y, 0f ) );
    }  
    
    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) { }
}
