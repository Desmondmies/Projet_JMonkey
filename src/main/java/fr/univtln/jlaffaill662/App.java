package fr.univtln.jlaffaill662;

import java.util.logging.Level;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;

import fr.univtln.jlaffaill662.Character.camera.PlayerCamera;
import fr.univtln.jlaffaill662.Character.platformer.PlayerPlatformer;
import fr.univtln.jlaffaill662.Environment.SkyboxAppState;
import fr.univtln.jlaffaill662.Fx.TextDisplay;
import fr.univtln.jlaffaill662.Game.GameManager;
import fr.univtln.jlaffaill662.Scenes.LevelSelector;
import fr.univtln.jlaffaill662.Z_TEST.TestAppState;

/**
 * Main App Launcher
 */
public class App extends SimpleApplication
{
    private final Vector3f WORLD_CAM_POS = new Vector3f( 10, 5, 25);
    private final Vector3f WORLD_CAM_LOOKAT = new Vector3f( 10, 4.5f, 0);

    private BulletAppState bulletAppState;

    private PlayerPlatformer playerPlatformer;
    private PlayerCamera playerCamera;
    private GameManager gameManager;

    public static void main( String[] args )
    {
        java.util.logging.Logger.getLogger("").setLevel(Level.WARNING);

        App app = new App();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        System.out.println("\nWork In Progress !");
        setDisplayStatView(false);
        // gameManager = GameManager.getInstance();

        cam.setLocation( WORLD_CAM_POS );
        cam.lookAt(WORLD_CAM_LOOKAT, Vector3f.UNIT_Y);
        
        //disable cam movement
        getFlyByCamera().setEnabled(false);

        initPhysics();
        setupAllAppStates();

        //test next up, player + gravity + capsule collider + inputs

        //init environments (models, materials, lights, props)
        //init players and camera
        //init inputs (maybe in player directly)
        //start game (maybe timer, change game state)

        //add to gui node main arcade frame

        //main menu (insert coin ?)
        //then start game, fade main menu away, lava rise

        //don't forget adding player collision group
    }

    public void pauseApp() {
        // stateManager.cleanup();

        stateManager.detach(playerPlatformer);
        stateManager.detach(playerCamera);
        stateManager.detach(gameManager);

        bulletAppState.cleanup();
    }

    // public void quitApp() { stop(); }

    public void lose() {
        pauseApp();
        TextDisplay.showTxt(assetManager, 
                            guiNode, 
                            "YOU LOSE :c",
                            ColorRGBA.Red, 
                            settings.getWidth() / 2, 
                            settings.getHeight() / 2, 
                            70);
    }   

    public void win() {
        pauseApp();
        TextDisplay.showTxt(assetManager, 
                            guiNode, 
                            "YOU WON !!!", 
                            ColorRGBA.Green,
                            settings.getWidth() / 2,
                            settings.getHeight() / 2,
                            80);
    }

    private void initPhysics() {
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
    }

    private void setupAllAppStates() {
        // stateManager.attach( new TestAppState() );
        stateManager.attach( new LevelSelector() );
        stateManager.attach( new SkyboxAppState() );
        
        playerPlatformer = new PlayerPlatformer();
        stateManager.attach( playerPlatformer );

        playerCamera = new PlayerCamera();
        playerCamera.setGuiNode( getGuiNode() );
        stateManager.attach(playerCamera);

        gameManager = new GameManager();
        gameManager.setSimpleApp(this);
        stateManager.attach( gameManager );
    }
}
