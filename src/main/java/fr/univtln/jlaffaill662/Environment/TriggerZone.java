package fr.univtln.jlaffaill662.Environment;

import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.GhostControl;
import com.jme3.scene.Node;

public class TriggerZone extends Node{
    
    public TriggerZone(String name, Node rootNode, BulletAppState bulletAppState, CollisionShape shape, CollisionEnum collideWith) {
        super(name);

        GhostControl g = new GhostControl(shape);
        int collisionGroup = collideWith.ordinal();
        g.setCollisionGroup( CollisionEnum.GHOST.ordinal() );
        g.setCollideWithGroups( collisionGroup );

        this.addControl(g);

        rootNode.attachChild(this);
        bulletAppState.getPhysicsSpace().add(g);
    }
}
