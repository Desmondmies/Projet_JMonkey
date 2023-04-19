package fr.univtln.jlaffaill662.Environment;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.asset.AssetManager;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.util.SkyFactory;

public class SkyboxAppState extends BaseAppState{

    private Spatial sky;

    private Node rootNode;
    private AssetManager assetManager;

    @Override
    protected void initialize(Application app) {
        rootNode = (Node) app.getViewPort().getScenes().get(0);
        assetManager = app.getAssetManager();

        sky = SkyFactory.createSky(assetManager, "Textures/Skybox/BrightSky.dds", SkyFactory.EnvMapType.CubeMap);

        rootNode.attachChild(sky);
    }

    @Override
    protected void cleanup(Application app) {
        sky.removeFromParent();
    }

    @Override
    protected void onEnable() { }

    @Override
    protected void onDisable() { }
    
}
