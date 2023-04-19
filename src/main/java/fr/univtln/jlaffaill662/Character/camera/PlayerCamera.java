package fr.univtln.jlaffaill662.Character.camera;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.input.InputManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;

public class PlayerCamera extends BaseAppState {

    // private Node rootNode;

    private Node cameraPlayer;
    private Node guiNode;
    private PlayerCameraControls cameraControls;

    @Override
    protected void initialize(Application app) {
        // rootNode = (Node) app.getViewPort().getScenes().get(0);

        Geometry testCamBox = new Geometry("TestCam", new Box(10f, 10f, 10f)); //pixel size, it's displayed in guiNode
        Material m = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        m.setColor("Color", ColorRGBA.Black);
        testCamBox.setMaterial(m);

        cameraPlayer = new Node("CameraPlayer");
        cameraPlayer.attachChild(testCamBox);

        setupInputsControls( app );

        // rootNode.attachChild(cameraPlayer);
        guiNode.attachChild(cameraPlayer);
    }

    public void setGuiNode(Node guiNode) { 
        this.guiNode = guiNode;
    }

    private void setupInputsControls( Application app ) {
        cameraControls = new PlayerCameraControls(app);
        cameraControls.setSpatial(cameraPlayer);

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
