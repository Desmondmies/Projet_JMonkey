package fr.univtln.jlaffaill662.Character.camera;

import com.jme3.app.Application;
import com.jme3.input.InputManager;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;

import fr.univtln.jlaffaill662.App;
import fr.univtln.jlaffaill662.Fx.AudioPlayer;

public class PlayerCameraControls extends AbstractControl{

    private Vector2f mousePos = new Vector2f(0f, 0f);

    private InputManager inputManager;
    private Application app;
    private App appMain;

    private AudioPlayer audioPlayer;

    public PlayerCameraControls(Application app) {
        this.app = app;
        this.inputManager = app.getInputManager();
        audioPlayer = AudioPlayer.getInstance();
    }

    @Override
    public void setSpatial(Spatial spatial){
        super.setSpatial(spatial);

        if (spatial != null) initInputs();
    }

    public void setApp(App appMain) { this.appMain = appMain; }

    private void initInputs() {
        if (!inputManager.hasMapping("MouseX"))
            inputManager.addMapping("MouseX", new MouseAxisTrigger(MouseInput.AXIS_X, false));
        if (!inputManager.hasMapping("MouseY"))
            inputManager.addMapping("MouseY", new MouseAxisTrigger(MouseInput.AXIS_Y, false));

        //shooting interaction (mouse left button click to shoot)
        if (!inputManager.hasMapping("Shoot"))
            inputManager.addMapping("Shoot", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));

        inputManager.addListener(actionListener, "Shoot");
    }

    private ActionListener actionListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean keyPressed, float tpf) {
            if (appMain.getGameEnded()) return;
            if(name.equals("Shoot") && !keyPressed) shoot();
        }
    };

    private void shoot(){
        //shoot from cursor to scene, cam direction
        Projectile.shoot(app, spatial.getLocalTranslation());
        audioPlayer.playShoot();
    }

    @Override
    protected void controlUpdate(float tpf) {
        mousePos = inputManager.getCursorPosition();
        spatial.setLocalTranslation( new Vector3f( mousePos.x, mousePos.y, 0f ) );
    }  
    
    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) { }
}
