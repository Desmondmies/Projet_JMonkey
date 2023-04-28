package fr.univtln.jlaffaill662.Fx;

import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioNode;
import com.jme3.audio.AudioData.DataType;
import com.jme3.scene.Node;

public class AudioPlayer {
    private static AudioPlayer instance;

    private AudioNode switchSound;
    private AudioNode shootSound;
    private AudioNode gameOverSound;
    private AudioNode victorySound;
    private AudioNode ambiant;

    public static AudioPlayer getInstance() {
        if (instance == null) instance = new AudioPlayer();
        return instance;
    }

    public void initialize(AssetManager assetManager, Node rootNode) {
        //play ambient music
        ambiant = new AudioNode(assetManager, "Sound/ambiant.wav", DataType.Stream);
        ambiant.setLooping(true);
        ambiant.setPositional(false);
        ambiant.setVolume(1);
        rootNode.attachChild(ambiant);
        ambiant.play();

        //load each audio node
        switchSound = loadSound(assetManager, "Sound/switch.wav", 1);
        shootSound = loadSound(assetManager, "Sound/shoot.wav", 1);
        gameOverSound = loadSound(assetManager, "Sound/game_over.wav", 1);
        victorySound = loadSound(assetManager, "Sound/victory.wav", 1);

        rootNode.attachChild(switchSound);
        rootNode.attachChild(shootSound);
        rootNode.attachChild(gameOverSound);
        rootNode.attachChild(victorySound);
    }

    private AudioNode loadSound(AssetManager assetManager, String name, float volume) {
        AudioNode a = new AudioNode(assetManager, name, DataType.Buffer);
        a.setLooping(false);
        a.setPositional(false);
        a.setVolume(volume);

        return a;
    }

    public void stopAmbiant() { ambiant.stop(); }

    public void playSwitch() { switchSound.playInstance(); }
    public void playShoot() { shootSound.playInstance(); }
    public void playGameOver() { gameOverSound.playInstance(); }
    public void playVictory() { victorySound.playInstance(); }
}
