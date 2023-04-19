package fr.univtln.jlaffaill662.Scenes;

import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

public interface ILevel {
    public Node getNode();
    public Vector3f getPlayerPos();
    public Vector3f getEndGatePos();
}
