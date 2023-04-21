package fr.univtln.jlaffaill662;

import java.util.logging.Level;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.math.Vector3f;
import com.jme3.system.AppSettings;

import fr.univtln.jlaffaill662.Character.camera.PlayerCamera;
import fr.univtln.jlaffaill662.Character.platformer.PlayerPlatformer;
import fr.univtln.jlaffaill662.Environment.SkyboxAppState;
import fr.univtln.jlaffaill662.Fx.ImageDisplay;
import fr.univtln.jlaffaill662.Game.GameManager;
import fr.univtln.jlaffaill662.Scenes.LevelSelector;

/**
 * Main App Launcher
 */
public class App extends SimpleApplication
{
    private final Vector3f WORLD_CAM_POS = new Vector3f( 9f, 7, 25);
    private final Vector3f WORLD_CAM_LOOKAT = new Vector3f( 9f, 6.5f, 0);

    private BulletAppState bulletAppState;

    private PlayerPlatformer playerPlatformer;
    private PlayerCamera playerCamera;
    private GameManager gameManager;

    private boolean gameEnded = false;

    public static void main( String[] args )
    {
        java.util.logging.Logger.getLogger("").setLevel(Level.WARNING);

        App app = new App();

        AppSettings settings = new AppSettings(true);
        settings.setTitle("Teddy Wanna Live !!!");
        app.setSettings(settings);
        
        app.start();
    }

    @Override
    public void simpleInitApp() {
        System.out.println("\nGAME STARTED ! (Press Escape to quit)");
        setDisplayStatView(false);

        cam.setLocation( WORLD_CAM_POS );
        cam.lookAt(WORLD_CAM_LOOKAT, Vector3f.UNIT_Y);
        
        //disable cam movement
        getFlyByCamera().setEnabled(false);

        initPhysics();
        setupAllAppStates();
    }

    public boolean getGameEnded() { return gameEnded; }

    public void pauseApp() {
        gameEnded = true;
        stateManager.detach(playerPlatformer);
        stateManager.detach(playerCamera);
        stateManager.detach(gameManager);

        bulletAppState.cleanup();
    }

    public void lose() {
        pauseApp();
        ImageDisplay.showImage(assetManager, 
                                guiNode, 
                                "./Images/GAME_OVER.png", 
                                settings.getWidth() / 2, 
                                settings.getHeight() / 2, 
                                350, 
                                100);
    }   

    public void win() {
        pauseApp();
        ImageDisplay.showImage(assetManager, 
                                guiNode, 
                                "./Images/VICTORY.png", 
                                settings.getWidth() / 2, 
                                settings.getHeight() / 2, 
                                350, 
                                100);
    }

    private void initPhysics() {
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
    }

    private void setupAllAppStates() {
        stateManager.attach( new LevelSelector() );
        stateManager.attach( new SkyboxAppState() );
        
        playerPlatformer = new PlayerPlatformer();
        stateManager.attach( playerPlatformer );

        playerCamera = new PlayerCamera();
        playerCamera.setGuiNode( getGuiNode() );
        playerCamera.setApp(this);
        stateManager.attach(playerCamera);

        gameManager = new GameManager();
        gameManager.setSimpleApp(this);
        stateManager.attach( gameManager );
    }
}
