package fr.univtln.jlaffaill662.Scenes;

import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

public class Level0 implements ILevel{

    private Vector3f playerPos;
    private Vector3f endGatePos;
    private Node level;

    public Level0() {
        level = LevelParser.parse("Level0");
        playerPos = level.getChild("PlayerSpawnPoint").getLocalTranslation();
        endGatePos = level.getChild("EndGate").getLocalTranslation();
    }

    @Override
    public Node getNode() { return level; }

    @Override
    public Vector3f getPlayerPos() { return playerPos; }

    @Override
    public Vector3f getEndGatePos() { return endGatePos; }
}
