import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.Thread.sleep;

public class ColorDominant {
    public static void main(String args[]) throws Exception {
        File file = new File("C:\\Users\\Mo3taz kayad\\Downloads\\stock-photo-london-england-october-fc-paris-saint-germain-logo-on-glossy-button-isolated-on-1014183955.jpg");
        ImageInputStream is = ImageIO.createImageInputStream(file);
        Iterator iter = ImageIO.getImageReaders(is);

        if (!iter.hasNext()) {
            System.out.println("Cannot load the specified file " + file);
            System.exit(1);
        }
        ImageReader imageReader = (ImageReader) iter.next();
        imageReader.setInput(is);

        BufferedImage image = imageReader.read(0);

        int height = image.getHeight();
        int width = image.getWidth();

        Map m = new HashMap();
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int rgb = image.getRGB(i, j);
                int[] rgbArr = getRGBArr(rgb);
                // Filter out grays....
                if (!isGray(rgbArr)) {
                    Integer counter = (Integer) m.get(rgb);
                    if (counter == null)
                        counter = 0;
                    counter++;
                    m.put(rgb, counter);
                }
            }
        }
        String colourHex = getMostCommonColour(m);
        System.out.println(colourHex);

        while (true) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(Runtime.getRuntime().exec(System.getenv("windir") + "\\System32\\tasklist.exe").getInputStream()));
            var strings = reader.lines().sorted().filter(s -> s.startsWith("Notepad")).collect(Collectors.joining(","));
            System.out.println(strings.isBlank() ? "notepad is terminated" : "1\t" + strings);
        }

//        FileWriter fileWriter = new FileWriter(new File("C:\\Users\\Mo3taz kayad\\Desktop\\javaText.txt").getAbsolutePath()
//                , true);
//        fileWriter.write(new char[1024*1024]);
//        fileWriter.flush();
//        fileWriter.close();
//        FileWriter fileWriter2 = new FileWriter("C:\\Users\\Mo3taz kayad\\Desktop\\javaText.txt" , true);
//        fileWriter2.append(new String(new char[1024]), 0 , 1024);
//        char[] fat = new char[1024*1024];
//        Arrays.fill(fat , '*');
//        fileWriter2.append(new String(fat) , 1024 , 1024*5);
//        fileWriter2.flush();;

    }


    public static String getMostCommonColour(Map map) {
        List list = new LinkedList(map.entrySet());
        Collections.sort(list, (Comparator) (o1, o2) -> ((Comparable) ((Map.Entry) (o1)).getValue())
                .compareTo(((Map.Entry) (o2)).getValue()));
        Map.Entry me = (Map.Entry) list.get(list.size() - 1);
        int[] rgb = getRGBArr((Integer) me.getKey());
        return Integer.toHexString(rgb[0]) + " " + Integer.toHexString(rgb[1]) + " " + Integer.toHexString(rgb[2]);
    }

    public static int[] getRGBArr(int pixel) {
        int alpha = (pixel >> 24) & 0xff;
        int red = (pixel >> 16) & 0xff;
        int green = (pixel >> 8) & 0xff;
        int blue = (pixel) & 0xff;
        return new int[]{red, green, blue};

    }

    public static boolean isGray(int[] rgbArr) {
        int rgDiff = rgbArr[0] - rgbArr[1];
        int rbDiff = rgbArr[0] - rgbArr[2];
        // Filter out black, white and grays...... (tolerance within 10 pixels)
        int tolerance = 10;
        if (rgDiff > tolerance || rgDiff < -tolerance)
            if (rbDiff > tolerance || rbDiff < -tolerance) {
                return false;
            }
        return true;
    }
}
