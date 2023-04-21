package fr.univtln.jlaffaill662.Scenes;

import java.nio.ByteBuffer;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.texture.Image;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;

import fr.univtln.jlaffaill662.Environment.CollisionEnum;
import fr.univtln.jlaffaill662.Environment.Door;
import fr.univtln.jlaffaill662.Environment.DoorWallBtn;

public class LevelParser {

    private static enum LevelData {
        AIR( new ColorRGBA(255, 255, 255, 1f) ),
        FLOOR( new ColorRGBA(188, 188, 188, 1f) ),
        WALL( new ColorRGBA(125, 125, 125, 1f) ),
        PLAYER_START_POS( new ColorRGBA(0, 255, 0, 1f) ),
        ENDGATE( new ColorRGBA(0, 0, 255, 1f) ), //problem, red endgate detected as blue
        DOOR( new ColorRGBA(214, 220, 40, 1f) ),
        BTN_WALL( new ColorRGBA(220, 40, 158, 1f) );

        private final ColorRGBA c;

        LevelData(ColorRGBA c) {
            this.c = c;
        }

        public ColorRGBA getColor() { return c; }
    }

    private static AssetManager assetManager;
    private static BulletAppState bulletAppState;

    private static Material floorMat;
    private static Material wallMat;

    private static int doorIndex = 0;
    private static boolean hasConnection = false;

    public static void initParser(AssetManager assetManager, BulletAppState bulletAppState) {
        LevelParser.assetManager = assetManager;
        LevelParser.bulletAppState = bulletAppState;
        
        LevelParser.floorMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        Texture floorImg = assetManager.loadTexture("Textures/Environment/Floor.png");
        LevelParser.floorMat.setTexture("ColorMap", floorImg);
        // LevelParser.floorMat.setColor("Color", ColorRGBA.Gray);

        LevelParser.wallMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        Texture wallImg = assetManager.loadTexture("Textures/Environment/Wall.png");
        LevelParser.wallMat.setTexture("ColorMap", wallImg);
        // LevelParser.wallMat.setColor("Color", ColorRGBA.Black);
    }
    
    public static Node parse(String filename) {
        Texture2D tex = (Texture2D) assetManager.loadTexture("Levels/" + filename + ".png");
        Image img = tex.getImage();
        ByteBuffer pixels = img.getData(0);

        Node level = new Node(filename);

        Node floorLayout = new Node("FloorLayout");
        Node wallLayout = new Node("WallLayout");
        Node doorLayout = new Node("DoorLayout");
        Node btnWallLayout = new Node("Btn_WallLayout");

        int width = img.getWidth();
        int height = img.getHeight();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pos = (y * width + x) * 4;
                int r = pixels.get(pos + 1) & 0xFF;
                // int b = pixels.get(pos + 1) & 0xFF;
                // int g = pixels.get(pos + 2) & 0xFF;
                int g = pixels.get(pos + 2) & 0xFF;
                int b = pixels.get(pos + 3) & 0xFF;

                // System.out.println("pixel : " + x + "," + y + " color : " + r + "," + g + "," + b);
                ColorRGBA color = new ColorRGBA(r, g, b, 1f);

                int worldX = 2 * x;
                int worldY = 2 * y;

                if (color.equals( LevelData.AIR.getColor()) ) continue;

                if (color.equals( LevelData.FLOOR.getColor() )) { floorLayout.attachChild( createFloor(worldX, worldY) ); continue; }
                if (color.equals( LevelData.WALL.getColor() )) { wallLayout.attachChild( createWall(worldX, worldY) ); continue; }

                if (color.equals( LevelData.DOOR.getColor() )) { doorLayout.attachChild( addDoor(worldX, worldY) ); continue; }
                if (color.equals( LevelData.BTN_WALL.getColor() )) { btnWallLayout.attachChild( addDoorBtn(worldX, worldY) ); continue; }

                if (color.equals( LevelData.PLAYER_START_POS.getColor() )) { level.attachChild( placePlayerSpawnPoint(worldX, worldY) ); continue; }

                if (color.equals( LevelData.ENDGATE.getColor() )) { level.attachChild( placeEndGate(worldX, worldY) ); continue; }


            }
        }

        level.attachChild(floorLayout);
        level.attachChild(wallLayout);
        level.attachChild(doorLayout);
        level.attachChild(btnWallLayout);

        return level;
    }

    private static void increaseConnection() {
        hasConnection = false;
        doorIndex++;
    }

    private static Spatial addDoor(int x, int y) {
        Door d = new Door(doorIndex, assetManager, bulletAppState, x, y);
        
        if (hasConnection) increaseConnection();
        else hasConnection = true;

        return d;
    }

    private static Spatial addDoorBtn(int x, int y) {
        DoorWallBtn d = new DoorWallBtn(doorIndex, assetManager, bulletAppState, x, y);

        if (hasConnection) increaseConnection();
        else hasConnection = true;

        return d;
    }

    private static Spatial createFloor(int x, int y) {
        Spatial floor = new Geometry("Floor", new Box(1f, 1f, 1f));
        floor.setLocalTranslation(x, y, 0f);

        floor.setMaterial(floorMat);

        RigidBodyControl noMassRB = new RigidBodyControl(new BoxCollisionShape(new Vector3f(1f, 1f, 1f)), 0f);
        noMassRB.setCollisionGroup(CollisionEnum.DEFAULT.ordinal());
        noMassRB.setCollideWithGroups(CollisionEnum.PLAYER.ordinal());
        floor.addControl(noMassRB);
        bulletAppState.getPhysicsSpace().add(noMassRB);

        //add floor collision group?
        return floor;
    }

    private static Spatial createWall(int x, int y) {
        Spatial wall = new Geometry("Wall", new Box(1f, 1f, 1f));
        wall.setLocalTranslation(x, y, 0f);

        wall.setMaterial(wallMat);

        RigidBodyControl noMassRB = new RigidBodyControl(new BoxCollisionShape(new Vector3f(1f, 1f, 1f)), 0f);
        noMassRB.setCollisionGroup(CollisionEnum.WALL.ordinal());
        noMassRB.setCollideWithGroups(CollisionEnum.PLAYER.ordinal());
        wall.addControl(noMassRB);
        bulletAppState.getPhysicsSpace().add(noMassRB);

        //add wall collision group?
        return wall;
    }

    private static Spatial placePlayerSpawnPoint(int x, int y) {
        Node playerPos = new Node("PlayerSpawnPoint");
        playerPos.setLocalTranslation(x, y+2, 0f);
        return playerPos;
    }

    private static Spatial placeEndGate(int x, int y) {
        // Spatial endGate = new Geometry("EndGate", new Box(y, x, 0f));
        Node endGate = new Node("EndGate");
        endGate.setLocalTranslation(x, y, 0f);
        return endGate;
    }
}
