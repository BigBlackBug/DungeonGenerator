package com.company;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

/**
 * Created by bigbl on 6/8/2015.
 */
public class DungeonGenerator {

    private static final int CORRIDOR_LENGTH = 4;    //2
    private static final int ROOM_DIMENSION = 8;  //3
    private static final int SCALE = 10;
    private static final int MAX_ROOM_PLACEMENT_ATTEMPTS = 10;
    private static final float CORRIDOR_PLACEMENT_CHANCE = 0.90f;
    private final Random random = new Random();
    private BlockType[][] world;
    private int size;
    private List<Room> rooms = new ArrayList<Room>();
    private List<Room> corridors = new ArrayList<Room>();

    public DungeonGenerator(int size) {
        this.size = size;
        this.world = new BlockType[size][size];
    }

    public void render(String name) throws IOException {
        BufferedImage image = new BufferedImage(size * SCALE, size * SCALE, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = (Graphics2D) image.getGraphics();
        Color c;
        for (int i = 0; i < world.length; i++) {
            for (int j = 0; j < world[i].length; j++) {
                BlockType blockType = world[i][j];
                if (blockType == null) {
                    c = Color.DARK_GRAY;
                } else {
                    if (blockType == BlockType.ROOM) {
                        c = Color.RED;
                    } else {
                        c = Color.GREEN;
                    }
                }

                graphics.setColor(c);
                graphics.fillRect(i * SCALE, j * SCALE, SCALE, SCALE);
            }
        }
        for (Room room : rooms) {
            graphics.setColor(Color.BLUE);
            graphics.drawRect(room.bottomLeft.x * SCALE, room.bottomLeft.y * SCALE, room.width * SCALE, room.height * SCALE);
        }
        image.flush();
        ImageIO.write(image, "png", new File(name));
    }

    public void generate(int roomNumber) throws IOException {
        placeCenterRoom();
        long time = System.currentTimeMillis();
        int counter = 0;
        while (counter != roomNumber) {
            Point[] walls = getWalls();
            int qi = random.nextInt(walls.length);
            Point element = walls[qi];
            boolean success;
            BlockType type = getNextElementType();
            if (type == BlockType.CORRIDOR) {
                success = generateCorridor(element);
            } else {
                success = generateRoom(element);
            }
            if (success) {
                counter++;
            }
        }
        System.out.println(System.currentTimeMillis() - time + " msec");
    }

    private BlockType getNextElementType() {
        float rand = random.nextFloat();
        return rand < CORRIDOR_PLACEMENT_CHANCE ? BlockType.CORRIDOR : BlockType.ROOM;
    }

    private boolean generateCorridor(Point wall) {
        int width;
        int height;
        int x, y;
        int edgeX, edgeY;

        int randomSize = getCorridorSize();
        if (world[wall.x][wall.y - 1] != null) {
            //NORTH         ++
            width = 1;
            height = randomSize;
            x = wall.x;
            y = wall.y;
            edgeX = wall.x;
            edgeY = wall.y + height;
            if (!canPlace(x - 1, y, width + 2, height + 1)) {
                return false;
            }
        } else if (world[wall.x][wall.y + 1] != null) {
            //SOUTH ++
            width = 1;
            height = randomSize;
            x = wall.x;
            y = wall.y - height;
            edgeX = wall.x;
            edgeY = wall.y - height;
            if (!canPlace(x - 1, y - 1, width + 2, height + 2)) {
                return false;
            }
        } else if (world[wall.x - 1][wall.y] != null) {
            //east
            width = randomSize;
            height = 1;
            x = wall.x;
            y = wall.y;
            edgeX = wall.x + width;
            edgeY = wall.y;
            if (!canPlace(x, y - 1, width + 1, height + 2)) {
                return false;
            }
        } else if (world[wall.x + 1][wall.y] != null) {
            //west                 ++
            width = randomSize;
            height = 1;
            x = wall.x - width;
            y = wall.y;
            edgeX = wall.x - width;
            edgeY = wall.y;
            if (!canPlace(x - 1, y - 1, width + 2, height + 2)) {
                return false;
            }
        } else {
            //hit the corner
            return false;
        }

        //чек все 4 стороны
        world[wall.x][wall.y] = BlockType.CORRIDOR;
        placeCorridor(x, y, width, height);

        int attempt = MAX_ROOM_PLACEMENT_ATTEMPTS;

        boolean roomPlaced = false;
        while (attempt-- > 0 && !roomPlaced) {
            roomPlaced = generateRoom(new Point(edgeX, edgeY));
        }

        if (roomPlaced) {
            Point bottomLeft = new Point(x, y);
            Room corridor = new Room(bottomLeft, width, height);
            corridors.add(corridor);
            return true;
        } else {
            world[wall.x][wall.y] = null;
            clearCorridor(x, y, width, height);
            return false;
        }
    }

    private int getCorridorSize() {
        return random.nextInt(CORRIDOR_LENGTH) + 2;
    }

    private void clearCorridor(int x, int y, int width, int height) {
        for (int idx = 0; idx < width; idx++) {
            for (int idy = 0; idy < height; idy++) {
                world[idx + x][idy + y] = null;
            }
        }
    }

    private boolean generateRoom(Point wall) {
        int width = getRoomDimension();
        int height = getRoomDimension();
        int x, y;
        //проверки на возможно вставки
        if (world[wall.x][wall.y - 1] != null) {
            //NORTH         ++
            int contactX = random.nextInt(width);
            x = wall.x - contactX;
            y = wall.y + 1;
            if (!canPlace(x - 1, y, width + 2, height + 1)) {
                return false;
            }
        } else if (world[wall.x][wall.y + 1] != null) {
            //SOUTH ++
            int contactX = random.nextInt(width);
            x = wall.x - contactX;
            y = wall.y - height;
            if (!canPlace(x - 1, y - 1, width + 2, height + 2)) {
                return false;
            }
        } else if (world[wall.x - 1][wall.y] != null) {
            //east
            int contactY = random.nextInt(height);
            y = wall.y - contactY;
            x = wall.x + 1;
            if (!canPlace(x, y - 1, width + 1, height + 2)) {
                return false;
            }
        } else if (world[wall.x + 1][wall.y] != null) {
            //west                 ++
            int contactY = random.nextInt(height);
            y = wall.y - contactY;
            x = wall.x - width;
            if (!canPlace(x - 1, y - 1, width + 2, height + 2)) {
                return false;
            }
        } else {
            //попали на угол
            return false;
        }
        world[wall.x][wall.y] = BlockType.CORRIDOR;
        placeRoom(x, y, width, height);
        Point bottomLeft = new Point(x, y);
        Room room = new Room(bottomLeft, width, height);
        rooms.add(room);
        //чек все 4 стороны
        return true;
    }

    private int getRoomDimension() {
        return random.nextInt(ROOM_DIMENSION) + 3;//2-5
    }

    private boolean canPlace(int x, int y, int w, int h) {
        for (Room room : rooms) {
            if (room.intersects(x, y, w, h)) {
                return false;
            }
        }
        for (Room corr : corridors) {
            if (corr.intersects(x, y, w, h)) {
                return false;
            }
        }
        return true;

    }

    private Point[] getWalls() {
        Set<Point> walls = new HashSet<Point>();
        for (Room room : rooms) {
            walls.addAll(room.getWalls());
        }
        for (Room corrridor : corridors) {
            walls.addAll(corrridor.getWalls());
        }
        return walls.toArray(new Point[0]);
    }

    private void placeCenterRoom() {
        int width = random.nextInt(5) + 3;
        int height = random.nextInt(5) + 3;
        int x = size / 2 - width / 2;
        int y = size / 2 - height / 2;
        Point bottomLeft = new Point(x, y);
        Room room = new Room(bottomLeft, width, height);
        rooms.add(room);

        placeRoom(x, y, width, height);
    }

    private void placeRoom(int x, int y, int width, int height) {
        for (int idx = 0; idx < width; idx++) {
            for (int idy = 0; idy < height; idy++) {
                world[idx + x][idy + y] = BlockType.ROOM;
            }
        }
    }

    private void placeCorridor(int x, int y, int width, int height) {
        for (int idx = 0; idx < width; idx++) {
            for (int idy = 0; idy < height; idy++) {
                world[idx + x][idy + y] = BlockType.CORRIDOR;
            }
        }
    }

    private static enum BlockType {
        CORRIDOR, ROOM
    }

    private static class Point {
        public int x, y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Point point = (Point) o;

            if (x != point.x) return false;
            if (y != point.y) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = x;
            result = 31 * result + y;
            return result;
        }
    }

    private static class Room {
        public Point bottomLeft;
        private int width;
        private int height;
        private List<Point> walls;

        public Room(Point bottomLeft, int width, int height) {
            this.bottomLeft = bottomLeft;
            this.width = width;
            this.height = height;
            this.walls = getWalls();
        }

        public List<Point> getWalls() {
            List<Point> walls = new ArrayList<Point>();
            //hori
            for (int x = bottomLeft.x - 1; x <= bottomLeft.x + width; x++) {
                //add checks
                walls.add(new Point(x, bottomLeft.y - 1));
                walls.add(new Point(x, bottomLeft.y + height));
            }
            //vert
            for (int y = bottomLeft.y; y <= bottomLeft.y + height; y++) {
                walls.add(new Point(bottomLeft.x - 1, y));
                walls.add(new Point(bottomLeft.x + width, y));
            }
            return walls;
        }

        public boolean intersects(int x, int y, int w, int h) {
            return (x + w > bottomLeft.x &&
                    y + h > bottomLeft.y &&
                    x < bottomLeft.x + width &&
                    y < bottomLeft.y + height);
        }
    }

}
