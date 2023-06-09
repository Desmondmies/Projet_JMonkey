package fr.univtln.jlaffaill662.Character.camera;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.texture.Texture;

import fr.univtln.jlaffaill662.App;

public class PlayerCamera extends BaseAppState {

    private App appMain;

    private Node cameraPlayer;
    private Node guiNode;
    private PlayerCameraControls cameraControls;

    @Override
    protected void initialize(Application app) {
        Geometry testCamBox = new Geometry("TestCam", new Box(17f, 17f, 1f)); //pixel size, it's displayed in guiNode
        Material m = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        Texture t = app.getAssetManager().loadTexture("Images/crosshair.png");
        m.setTexture("ColorMap", t);
        m.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        testCamBox.setMaterial(m);

        testCamBox.setLocalTranslation(0, 0, -10);

        cameraPlayer = new Node("CameraPlayer");
        cameraPlayer.attachChild(testCamBox);

        setupInputsControls( app );

        guiNode.attachChild(cameraPlayer);
    }

    public void setGuiNode(Node guiNode) { this.guiNode = guiNode; }

    public void setApp(App appMain) { this.appMain = appMain; }

    private void setupInputsControls( Application app ) {
        cameraControls = new PlayerCameraControls(app);
        cameraControls.setSpatial(cameraPlayer);
        cameraControls.setApp(appMain);

        cameraPlayer.addControl(cameraControls);
    }

    @Override
    protected void cleanup(Application app) {
        cameraPlayer.removeFromParent();        
    }

    @Override
    protected void onDisable() { }

    @Override
    protected void onEnable() { }
    
}
