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
public class Generator {
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

    private BlockType[][] world;
    private int size;
    private List<Room> rooms = new ArrayList<Room>();

    public Generator(int size) {
        this.size = size;
        this.world = new BlockType[size][size];
    }

    private final Random random = new Random();

    public static void main(String[] args) throws IOException {
        Generator generator = new Generator(150);
        generator.generate();
        generator.render("a");
    }

    public void render(String name) throws IOException {
        BufferedImage image = new BufferedImage(size * 10, size * 10, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = (Graphics2D) image.getGraphics();
        Color c;
        for (int i = 0; i < world.length; i++) {
            for (int j = 0; j < world[i].length; j++) {
                BlockType blockType = world[i][j];
                if (blockType == null) {
                    c = Color.BLACK;
                } else {
                    if (blockType == BlockType.ROOM) {
                        c = Color.RED;
                    } else {
                        c = Color.GREEN;
                    }
                }

                graphics.setColor(c);
                graphics.fillRect(i * 10, j * 10, 10, 10);
            }
        }
        for (Room room : rooms) {
            graphics.setColor(Color.BLUE);
            graphics.drawRect(room.bottomLeft.x * 10, room.bottomLeft.y* 10, room.width*10, room.height*10);
        }
        image.flush();
        ImageIO.write(image, "png", new File(name + "test2.png"));
    }

    public void print() {
        for (int i = 0; i < world.length; i++) {
            BlockType[] floats = world[i];
            for (int j = 0; j < floats.length; j++) {
                if (floats[j] == null) {
                    System.out.print("E");
                } else if (floats[j] == BlockType.CORRIDOR)
                    System.out.print("C");
                else System.out.print("R");
            }
            System.out.println();
        }
    }

    public void generate() throws IOException {
        placeCenterRoom();
        long time = System.currentTimeMillis();
        int counter = 0;
        while (counter != 20) {
            Point[] walls = getWalls();
//        render("1");
//        for (Point wall : walls) {
//            world[wall.x][wall.y]=BlockType.CORRIDOR;
//        }
//        render("2");
            int qi = random.nextInt(walls.length);
//        generateRoom(walls[i]);
            Point element = walls[qi];
//        BlockType type = random.nextBoolean() ? BlockType.CORRIDOR : BlockType.ROOM;
//        if (type == BlockType.CORRIDOR) {
//            generateCorridor(element);
//        } else {
            boolean b = generateRoom(element);
            if (b) {
                counter++;
            }
            render("ASD");
//        }
        }
        System.out.println(System.currentTimeMillis() - time + "msec");
    }

    private boolean generateRoom(Point wall) {
        int width = random.nextInt(5) + 3;//2-5
        int height = random.nextInt(5) + 3;//2-5
        int x = 0, y = 0;
        //проверки на возможно вставки
        if (world[wall.x][wall.y - 1] != null) {
            //NORTH         ++
            int contactX = random.nextInt(width);
            x = wall.x - contactX;
            y = wall.y + 1;
            if (!canPlaceRoom(x - 1, y, width + 2, height + 1)) {
                return false;
            }
        } else if (world[wall.x][wall.y + 1] != null) {
            //SOUTH ++
            int contactX = random.nextInt(width);
            x = wall.x - contactX;
            y = wall.y - height;
            if (!canPlaceRoom(x - 1, y - 1, width + 2, height + 2)) {
                return false;
            }
        } else if (world[wall.x - 1][wall.y] != null) {
            //east
            int contactY = random.nextInt(height);
            y = wall.y - contactY;
            x = wall.x + 1;
            if (!canPlaceRoom(x, y - 1, width + 1, height + 2)) {
                return false;
            }
        } else if (world[wall.x + 1][wall.y] != null) {
            //west                 ++
            int contactY = random.nextInt(height);
            y = wall.y - contactY;
            x = wall.x - width;
            if (!canPlaceRoom(x - 1, y - 1, width + 2, height + 2)) {
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

    private boolean canPlaceRoom(int x, int y, int w, int h) {
        for (Room room : rooms) {
            if (room.intersects(x, y, w, h)) {
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
        return walls.toArray(new Point[0]);
    }

    private void placeCenterRoom() {
        int width = random.nextInt(5) + 3;//2-5
        int height = random.nextInt(5) + 3;//2-5
        int x = size / 2 - width / 2;
        int y = size / 2 - height / 2;
        Point bottomLeft = new Point(x, y);
        Room room = new Room(bottomLeft, width, height);
        rooms.add(room);

        placeRoom(x, y, width, height);
    }

    private void placeRoom(int x, int y, int centerRoomWidth, int centerRoomHeight) {
        for (int idx = 0; idx < centerRoomWidth; idx++) {
            for (int idy = 0; idy < centerRoomHeight; idy++) {
                world[idx + x][idy + y] = BlockType.ROOM;
            }
        }
    }


    private static class Room {
        public Point bottomLeft;
        private int width;
        private int height;

        public Room(Point bottomLeft, int width, int height) {
            this.bottomLeft = bottomLeft;
            this.width = width;
            this.height = height;
            this.walls = getWalls();
        }

        private List<Point> walls = new ArrayList<Point>();

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
