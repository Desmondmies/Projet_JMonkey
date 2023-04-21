package fr.univtln.jlaffaill662.Fx;

import com.jme3.asset.AssetManager;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

public class FireParticle extends Node{
    public FireParticle(AssetManager assetManager, Vector3f pos) {
        super("Fire");
        ParticleEmitter fire = new ParticleEmitter("Fire", ParticleMesh.Type.Triangle, 30);
        Material m = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
        m.setTexture("Texture", assetManager.loadTexture("Effects/fire.PNG"));

        fire.setMaterial(m);
        fire.setImagesX(5);
        fire.setImagesY(1);
        fire.setEndColor(ColorRGBA.Red);
        fire.setStartColor(ColorRGBA.Yellow);

        fire.getParticleInfluencer().setInitialVelocity(new Vector3f(0, 0, 0));
        fire.setStartSize(3f);
        fire.setEndSize(.1f);
        fire.setGravity(Vector3f.ZERO);
        fire.setLowLife(1f);
        fire.setHighLife(3f);

        fire.getParticleInfluencer().setVelocityVariation(0.3f);

        // fire.setLocalTranslation(pos);
        setLocalTranslation(pos);
        attachChild(fire);
    }
}
