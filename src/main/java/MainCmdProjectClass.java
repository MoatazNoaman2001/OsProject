import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.attribute.FileAttribute;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MainCmdProjectClass {

    public static void main(String[] args) throws IOException, InterruptedException {

        System.out.println(Arrays.toString(ByteBuffer.allocate(4).putInt(1503511000).array()));
        System.out.println(ByteBuffer.wrap(new byte[]{126 ,126 , 126 ,126}).getInt());
        System.out.println(System.getProperty("user.dir"));
        System.out.println(System.getProperty("os.name"));
        System.out.println(System.getProperty("os.version"));


//        System.out.println("hi");
//        try {
//            Files.createFile(new File("C:\\Users\\Mo3taz kayad\\Desktop\\tntpx.txt").toPath());
//            Files.writeString(new File("C:\\Users\\Mo3taz kayad\\Desktop\\tntpx.txt").toPath(),
//                    new String("يلي سرتي تعباك \n قلبك اسود بلاك".getBytes(), StandardCharsets.UTF_8));
//        } catch (IOException ignored) {
//            Files.writeString(new File("C:\\Users\\Mo3taz kayad\\Desktop\\tntpx.txt").toPath(),
//                    new String("يلي سرتي تعباك \n قلبك اسود بلاك".getBytes(), StandardCharsets.UTF_8));
//        }

//        Files.createTempFile(new File("C:\\Users\\Mo3taz kayad\\Desktop").toPath()
//        ,"nntftxt"  ,"gh");


//        HashMap<String , Integer> hashMap = new HashMap<>();
//        hashMap.put("cello" , 2000);
//        hashMap.put("matip" , 5000);
//        hashMap.put("cian" , 4500);
//        hashMap.put("cello" , 3000);
//        hashMap.merge("matip" , 5200 , (integer, integer2) -> integer2);
//        hashMap.compute("cian" , (s, integer) -> integer > 5000? integer : integer +1000);
//        hashMap.computeIfAbsent("modritch"  , s -> 8521 );
//        hashMap.computeIfPresent("cello" , (s, integer) -> integer * 5/3);
//
//        List<String> ss = new ArrayList<>(List.of("hi", "mr", "moataz", "welcome", "to", "java"));
//
//        ss.replaceAll(s -> s.contains("moataz")? "mo3taz" : s);
//        ss.forEach(s -> System.out.print(s + "\t"));
//        System.out.println();
//        FileOutputStream fileStream = new FileOutputStream("C:\\Users\\Mo3taz kayad\\Desktop\\tntpx.txt");
//        hashMap.forEach((s, integer) -> System.out.println("key: " + s + " value: " + integer));
//        System.out.println(hashMap.values().stream().collect(Collectors.filtering(t -> Integer.valueOf(t.toString()) < 6000
//        , Collectors.toList())).toString());

//        FileInputStream stream = new FileInputStream("C:\\Users\\Mo3taz kayad\\Desktop\\tntpx2.txt");
        //cmd commands
        System.out.println("\n\n");
        Map<String, String> SysStructure = System.getenv();
        System.out.println(SysStructure.get("USERDOMAIN_ROAMINGPROFILE") + " program type: " + SysStructure.get("SESSIONNAME")
                + "\n maven home path: " + SysStructure.get("MAVEN_HOME"));
        String path = System.getProperty("user.dir");

        Scanner scanner = new Scanner(System.in);
        String Command = "";
        do {
            System.out.print(path + ">");
            Command = scanner.nextLine();
            path = FactoryManger.CmdIterateProgram.apply(Command.stripLeading(), path);
        } while (!Command.trim().equals("exit"));
    }
}
