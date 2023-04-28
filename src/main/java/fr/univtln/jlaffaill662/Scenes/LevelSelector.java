package fr.univtln.jlaffaill662.Scenes;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.bullet.BulletAppState;
import com.jme3.scene.Node;

public class LevelSelector extends BaseAppState {

    private int currentLevelSelected = 0;

    private ILevel currentLevel;

    private Node rootNode;

    @Override
    protected void initialize(Application app) {
        rootNode = (Node) app.getViewPort().getScenes().get(0);

        setupLevelParser( app );
        setupLevel();

        rootNode.attachChild( currentLevel.getNode() );
    }

    private void setupLevelParser(Application app) {
        LevelParser.initParser(app.getAssetManager(), app.getStateManager().getState(BulletAppState.class));
    }

    private void setupLevel() {
        switch(currentLevelSelected) {
            case 0: currentLevel = new Level0(); break;
        }
    }

    public ILevel getCurrentLevel() { return currentLevel; }

    @Override
    protected void cleanup(Application app) {
        currentLevel.getNode().removeFromParent();
    }

    @Override
    protected void onEnable() { }

    @Override
    protected void onDisable() { }
    
}
