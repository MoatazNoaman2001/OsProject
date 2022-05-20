import Models.Directory_Entry;
import com.google.gson.Gson;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MainRoot {


    public static String path = System.getProperty("user.dir");
    private static Fat fat;

    static {
        try {
            fat = new Fat();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Directory CurrentDir;

    private static ArrayList<File_Entry> files = new ArrayList<>();
    private static ArrayList<Directory> dirs = new ArrayList<>();

    public MainRoot() throws IOException {
        if (Arrays.equals(fat.getDisk().getBytes(0), new byte[1024])) {
            Directory root = new Directory("root", (byte) 0x11, 1, 0, new ArrayList<>(), null);
            root.WriteDirectory(fat);
            root.ReadDirectory(fat.getDisk());
            Directory user = new Directory("user", (byte) 0x11, 1, 0, new ArrayList<>(), root);
            user.WriteDirectory(fat);
            Directory user2 = new Directory("user2", (byte) 0x11, 1, 0, new ArrayList<>(), root);
            user2.WriteDirectory(fat);
            CurrentDir = root;
        } else {
            CurrentDir = getMainRoot();
            CurrentDir.ReadDirectory(fat.getDisk());
        }
        path += "$" + CurrentDir.getFileName().trim() + "/>";
    }

    public static void main(String[] args) throws IOException {
        MainRoot root = new MainRoot();
        try {
            CurrentDir.ReadDirectory(fat.getDisk());
        } catch (IOException | NullPointerException e) {
            System.out.println("start up error please restart again");
            e.printStackTrace();
        }
        Scanner scanner = new Scanner(System.in);
        String command = "";
        do {
            System.out.print(path + " ");
            command = scanner.nextLine();
            if (!CMD.test(command, path)) System.out.println("an error happen unknown reason");
        } while (!command.trim().equalsIgnoreCase("quit"));

//        TreeMap leagues= getMyRequest("https://app.sportdataapi.com/api/v1/soccer/leagues?country_id=3");

//        FileOutputStream stream = new FileOutputStream("C:\\Users\\Mo3taz kayad\\Desktop\\fatTable.txt");
    }


    public static BiPredicate<String, Directory> MD = (s, d) -> {
        Directory dir;
        try {
            dir = new Directory(s, (byte) '+', 1, 0, new ArrayList<>(), d);
            if (CurrentDir.SearchDirectory(s, fat.getDisk()) != -1) System.out.println("already exist");
            CurrentDir.ReadDirectory(fat.getDisk());
            dir.WriteDirectory(fat);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    };
    public static BiPredicate<String[], Directory_Entry> MDR = (s, d) -> {
        File_Entry file = null;
        try {
            file = new File_Entry(s[1], (byte) '-', 1, 0, "", CurrentDir);
            CurrentDir.ReadDirectory(fat.getDisk());
            file.WriteFile(fat.getDisk());
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    };
    public static BiPredicate<String, String> RN = (oldName, newName) -> {
        Directory_Entry entry = null;
        try {
            int index = CurrentDir.SearchDirectory(oldName, fat.getDisk());
            entry = CurrentDir.getEntries().get(index);
            System.out.println((char) entry.getFileAttr());
            entry.setFileName(newName);
            CurrentDir.getEntries().remove(index);
            CurrentDir.getEntries().add(entry);
            CurrentDir.updateDirectory(fat.getDisk());
        } catch (IOException | IndexOutOfBoundsException e) {
            if (e instanceof IndexOutOfBoundsException)
                System.out.println("no such folder name");
            else
                e.printStackTrace();
        }
        return false;
    };
    public static BiPredicate<String, String> IMPORT = (path, S) -> {
        File_Entry file = null;
        StringBuilder builder = new StringBuilder(), pathBuilder = new StringBuilder(path);
        path.replace("\"", "\\");
        File file1 = new File(path);
        try (Stream<String> stream = Files.lines(file1.toPath())) {
            stream.forEachOrdered(builder::append);
            if (S.isEmpty()) S = Path.of(path).getFileName().toString();
            file = new File_Entry(S, (byte) '-', builder.toString().getBytes().length, 2,
                    builder.toString(), CurrentDir);
            file.WriteFile(fat.getDisk());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    };
    public static BiPredicate<String, String> EXPORT = (filename, outName) -> {
        try {
            File_Entry file = new File_Entry(CurrentDir.getEntries().get(CurrentDir.SearchDirectory(filename, fat.getDisk())));
            file.ReadFile(fat.getDisk());
            String str = file.getContent();
            FileOutputStream stream = new FileOutputStream(System.getProperty("user.dir") + "\\" + outName);
            stream.write(str.getBytes(StandardCharsets.UTF_8));
            stream.close();
        } catch (IOException | IndexOutOfBoundsException e) {
            if (e instanceof IndexOutOfBoundsException)
                System.out.println("no such file name");
            else
                e.printStackTrace();
            return false;
        }
        return true;
    };
    private static Consumer<String> CAT = s -> System.out.println(files.stream()
            .filter(fileName -> fileName.getFileName().trim().equals(s.trim()))
            .map(File_Entry::getContent).findFirst()
            .orElse("no content")
    );

    private static Consumer<String> CD = name -> {
        try {
            Directory dir = dirs.stream()
                    .filter(fileName -> fileName.getFileName().trim().equals(name.trim()))
                    .findFirst()
                    .get();
            if (Objects.isNull(dir.getParent())) {
                System.out.println("no such directory");
            } else {
                dir.ReadDirectory(fat.getDisk());
                CurrentDir = dir;
                CurrentDir.ReadDirectory(fat.getDisk());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    };
    private static BiConsumer<String, String> CP = (name, dir) -> {
        try {
            File_Entry file = files.stream()
                    .filter(fileName -> fileName.getFileName().trim().equals(name.trim()))
                    .findFirst()
                    .get();
            Directory dire = dirs.stream()
                    .filter(fileName -> fileName.getFileName().trim().equals(dir.trim()))
                    .findFirst()
                    .get();
            dire.getEntries().add(file);
            dire.updateDirectory(fat.getDisk());
            file.setParent(dire);
            file.DeleteFile(fat.getDisk());
            file.WriteFile(fat.getDisk());
            System.out.println("file Copied");
        } catch (Exception e) {
            System.out.println("no such file name");
        }
    };
    private static Consumer<String> DEL = (nfile) -> {
        try {
            File_Entry file = files.stream()
                    .filter(fileName -> fileName.getFileName().trim().equals(nfile.trim()))
                    .findFirst()
                    .get();
            CurrentDir.DeleteDirectory(fat.getDisk(), file);
            System.out.println("file deleted");
        } catch (Exception e) {
            System.out.println("no such file name");
        }
    };
    private static Consumer<String> RD = (nfile) -> {
        try {
            Directory dir = dirs.stream()
                    .filter(fileName -> fileName.getFileName().trim().equals(nfile.trim()))
                    .findFirst()
                    .get();
            System.out.println(dir.getFileName());
            dirs.remove(dir);
            CurrentDir.DeleteDirectory(fat.getDisk(), dir);
            System.out.println("folder deleted");
        } catch (Exception e) {
            System.out.println("no such folder name");
        }
    };
    private static final BiPredicate<String, String> CMD = (command, path) -> {
        try {
            CurrentDir.ReadDirectory(fat.getDisk());
        } catch (IOException | NullPointerException e) {
            if (e instanceof NullPointerException)
                System.out.println("restart program for apply and conform environment");
            e.printStackTrace();
        }
        getAllDirs();
        getAllFiles();
        if (command.isBlank()) {
            System.out.println("write some thing");
        } else if (command.equalsIgnoreCase("cls")) {
            try {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
        } else if (command.trim().startsWith("help")) {
            if (command.trim().equals("help")) {
                System.out.println("\t\tcls\t\t\t to clear the board");
                System.out.println("\t\tdir\t\t\t to print the current dir file and folder");
                System.out.println("\t\tquit\t\t\t to close the program window");
                System.out.println("\t\tmd\t\t\t to create new Directory, you could user -r property to create file");
                System.out.println("\t\tdel\t\t\t to delete file from current root");
                System.out.println("\t\trd\t\t\t to delete folder from current root");
                System.out.println("\t\thelp\t\t\t are you serious, help for display keyword dialog, you could type name of keyword to know its function");
                System.out.println("\t\tcp\t\t\t to copy folder, you could user -r property to copy file");
                System.out.println("\t\trn\t\t\t rn to rename folder, you could user -r property to rename file");
                System.out.println("\t\tcat\t\t\t rn to display content of file");
                System.out.println("\t\trn\t\t\t use to navigate between folders ");
                System.out.println("\t\timport\t\t\t import to take folder and store it");
                System.out.println("\t\texport\t\t\t export file to specified location");
            } else
                switch (command.trim().split(" ")[1].toLowerCase(Locale.ROOT)) {
                    case "cls":
                        System.out.println("\t\tcls\t\t\t to clear the board");
                        break;
                    case "dir":
                        System.out.println("\t\tdir\t\t\t to print the current dir file and folder");
                        break;
                    case "quit":
                        System.out.println("\t\tquit\t\t\t to close the program window");
                        break;
                    case "md":
                        System.out.println("\t\tmd\t\t\t to create new Directory, you could user -r property to create file");
                        break;
                    case "del":
                        System.out.println("\t\tdel\t\t\t to delete file from current root");
                        break;
                    case "rd":
                        System.out.println("\t\tdel\t\t\t to delete folder from current root");
                        break;
                    case "help":
                        System.out.println("\t\thelp\t\t\t are you serious, help for display keyword dialog, you could type name of keyword to know its function");
                        break;
                    case "cp":
                        System.out.println("\t\tcp\t\t\t to copy folder, you could user -r property to copy file");
                        break;
                    case "rn":
                        System.out.println("\t\trn\t\t\t rn to rename folder, you could user -r property to rename file");
                        break;
                    case "cat":
                        System.out.println("\t\tcat\t\t\t rn to display content of file");
                        break;
                    case "cd":
                        System.out.println("\t\trn\t\t\t use to navigate between folders ");
                        break;
                    case "import":
                        System.out.println("\t\timport\t\t\t import to take folder and store it");
                    case "export":
                        System.out.println("\t\timport\t\t\t import to take folder and store it");
                }
        } else if (command.equals("dir")) {
            DIR();
        } else if (command.equals("quit")) {
            System.out.println("thanks for using win cmd");
            try {
                Runtime.getRuntime().exec("taskkill /f /im cmd.exe");
            } catch (IOException ignored) {
            }
        } else if (command.trim().startsWith("md")) {
            if (command.trim().equals("md")) {
                System.out.println("write name of directory");
                return true;
            }
            if (command.split(" ")[0].equals("md")) {
                if (command.trim().split(" ")[1].equals("-r")) {

                }
                if (command.trim().split(" ").length > 2) {
                    String[] dirs = Arrays.copyOfRange(command.trim().split(" "), 1, command.trim().split(" ").length);
                    for (String dir : dirs) {
                        if (dir.contains("{")) {
                            if (dir.contains("{") && dir.contains("}") && dir.contains("..")) {
                                int startRange = Integer.parseInt(dir.substring(dir.indexOf('{'), dir.indexOf(".."))),
                                        EndRange = Integer.parseInt(dir.substring(dir.indexOf(".."), dir.indexOf('}')));
                                System.out.println("start: " + startRange + "\t" + "end: " + EndRange);
                                return true;
//                                for (int i = startRange; i < EndRange; i++) {
//
//                                }
                            } else {
                                System.out.println("malformed command check sentence");
                            }
                        } else {
                            if (MD.test(dir, CurrentDir)) {
                                try {
                                    CurrentDir = getMainRoot();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }

                } else {
                    String dir = command.trim().split(" ")[1];
                    if (dir.contains("{")) {
                        if (dir.contains("{") && dir.contains("}") && dir.contains("..")) {
                            int startRange = Integer.parseInt(dir.substring(dir.indexOf('{') + 1, dir.indexOf(".."))),
                                    EndRange = Integer.parseInt(dir.substring(dir.indexOf("..") + 2, dir.indexOf('}')));
                            String dirname = dir.substring(0, dir.indexOf('{'));

                            System.out.println("start: " + startRange + "\t" + "end: " + EndRange);

                            for (int i = startRange; i < EndRange; i++) {
                                if (MD.test(dirname + i, CurrentDir)) {
                                    try {
                                        CurrentDir = getMainRoot();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        } else {
                            System.out.println("malformed command check sentence");
                        }
                    } else {
                        if (MD.test(dir, CurrentDir)) {
                            try {
                                CurrentDir = getMainRoot();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        } else if (command.trim().startsWith("del")) {
            if (command.trim().equals("del")) {
                System.out.println("write name of file");
                return true;
            } else if (command.split(" ")[0].equals("del")) {
                try {
                    DEL.accept(command.trim().split(" ")[1]);
                    CurrentDir.ReadDirectory(fat.getDisk());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        } else if (command.trim().startsWith("rd")) {
            if (command.trim().equals("rd")) {
                System.out.println("write name of folder");
                return true;
            } else if (command.split(" ")[0].equals("rd")) {
                try {
                    RD.accept(command.trim().split(" ")[1]);
                    CurrentDir.ReadDirectory(fat.getDisk());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        } else if (command.trim().startsWith("rn")) {
            if (command.trim().equals("rn")) {
                System.out.println("type folder name");
            } else {
                if (command.trim().split(" ")[1].equals("-r")) {
                } else {
                    if (command.trim().split(" ").length == 2)
                        System.out.println("don't forget to type the new name");
                    else
                        RN.test(command.trim().split(" ")[1], command.trim().split(" ")[2]);
                }
            }
        } else if (command.trim().startsWith("import")) {
            if (command.trim().equals("import")) {
                System.out.println("type file path");
                System.out.println("ATTENTION ,folder path should not have any spaces, if so put it first on desktop");
            } else {
                if (command.trim().split(" ").length == 3) {
                    if (command.trim().split(" ")[2].contains("\\"))
                        IMPORT.test(command.trim().split(" ")[1].concat(" " + command.trim().split(" ")[2]), command.trim().split(" ")[2]);
                    else IMPORT.test(command.trim().split(" ")[1], command.trim().split(" ")[2]);
                } else if (command.trim().split(" ").length == 2) {
                    IMPORT.test(command.trim().split(" ")[1], "");
                } else {
                    StringBuilder con_path = new StringBuilder();
                    for (int i = 1; i < command.trim().split(" ").length - 1; i++) {
                        con_path.append(" ").append(command.trim().split(" ")[i]);
                    }
                    IMPORT.test(con_path.toString().trim(), command.trim().split(" ")[command.trim().split(" ").length - 1]);
                }
            }
        } else if (command.trim().startsWith("export")) {
            if (command.trim().equals("export")) {
                System.out.println("type file path");
                System.out.println("ATTENTION ,folder path should not have any spaces, if so put it first on desktop");
            } else {
                if (command.trim().split(" ").length == 2)
                    System.out.println("enter new file name");
                else
                    EXPORT.test(command.trim().split(" ")[1], command.trim().split(" ")[2]);
            }
        } else if (command.trim().startsWith("cd")) {
            if (!command.trim().equals("cd")) {
                if (command.trim().split(" ").length == 2) {
                    if (Objects.equals(command.trim().split(" ")[1], "..")) {
                        try {
                            CurrentDir = getMainRoot();
                            MainRoot.path = System.getProperty("user.dir") + "$" + CurrentDir.getFileName().trim();
                            DIR();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else
                        CD.accept(command.trim().split(" ")[1]);
                }
            }
            DIR();
        } else if (command.trim().startsWith("cp")) {
            if (!command.trim().equals("cp")) {
                if (command.trim().split(" ").length == 2) {
                    System.out.println("write folder name");
                } else {
                    CP.accept(command.trim().split(" ")[1], command.trim().split(" ")[2]);
                }
            } else {
                System.out.println("write file name");
            }
            DIR();
        } else if (command.trim().startsWith("cat")) {
            if (command.trim().equals("cat")) {
                System.out.println("type file name");
            } else {
                CAT.accept(command.trim().split(" ")[1]);
            }
        } else {
            System.out.println("unknown command");
        }
        return true;
    };

    private static void DIR() {
        if (CurrentDir.getParent() == null)
            System.out.println("\t" + "Directory of " + CurrentDir.getFileName().trim() + ": ");
        else {
            StringBuilder path = new StringBuilder(CurrentDir.getFileName().trim());
            Directory dir = CurrentDir;
            while (dir.getParent() != null) {
                path.insert(0, dir.getParent().getFileName().trim() + "/");
                dir = CurrentDir.getParent();
            }
            System.out.println("\t" + "Directory of " + path + ": ");
            MainRoot.path = path.toString();
        }
        if (!dirs.isEmpty()) {
            dirs.stream().sorted(Comparator.comparing(Directory_Entry::getFileName)).forEach(entry -> System.out.println("\t\t\t<DIR> " + entry.getFileName().trim() + "\t" + (char) entry.getFileAttr()));
        }
        if (!files.isEmpty()) {
            files.stream().sorted().forEach(e -> System.out.println("\t\t\t" + "      " + e.getFileName().trim() + "\t" + (char) e.getFileAttr()));
        }


        try {
            if (Objects.nonNull(files))
                System.out.println("\t\t\t" + files.size() + " File(s) " + "\t" +
                        files.stream().mapToInt(value -> {
                            try {
                                value.ReadFile(fat.getDisk());
                                System.out.println(Objects.isNull(value.getContent()));
                                return (value.getContent() != null ? value.getContent().length() : 0) + value.getBytes().length;
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            return 0;
                        }).sum());
            if (Objects.nonNull(dirs))
                System.out.println("\t\t\t" + dirs.size() + " dir(s)" + "\t" + (fat.getAvailableBlocks() * 1024)
                        + " free bytes");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void getAllFiles() {
        files = CurrentDir.getEntries().stream().filter(e -> e.getFileAttr() == '-')
                .distinct()
                .map(File_Entry::new)
                .peek(file_entry -> {
                    try {
                        file_entry.ReadFile(fat.getDisk());
                        file_entry.setParent(CurrentDir);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                })
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private static void getAllDirs() {
        dirs = CurrentDir.getEntries().stream()
                .map(entry -> {
                    try {
                        return new Directory(entry.getFileName(), entry.getFileAttr(), entry.getFileSize(), entry.getFileCluster(), new ArrayList<>(), CurrentDir);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                })
                .peek(directory -> {
                    try {
                        if (Objects.nonNull(directory))
                            directory.ReadDirectory(fat.getDisk());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                })
                .filter(e -> e.getFileAttr() == '+').distinct().collect(Collectors.toCollection(ArrayList::new));
    }

    private static TreeMap getMyRequest(String url) throws IOException {
        TreeMap tree = new TreeMap<>();
        String res = getJson(url);
        Gson gson = new Gson();
        if (!res.isEmpty()) {
            tree = gson.fromJson(res, TreeMap.class);
        }
        return tree;
    }

    public static Directory getMainRoot() throws IOException {
        //init
        VirtualDiskImpl disk = fat.getDisk();
        byte[] allbytes = disk.getBytes(0);
        Directory_Entry par = null;
        byte[] parr = null;
        ArrayList<Directory_Entry> arrayList = new ArrayList<>();
        Directory_Entry entry = null;

        //cut and transform
        for (int i = 0; i < allbytes.length; i++) {
            if (new String(Arrays.copyOfRange(allbytes, i, i + "root".length()), StandardCharsets.UTF_8).equals("root")) {
                entry = new Directory_Entry(Arrays.copyOfRange(allbytes, i, i + 32));
            }
        }
        if (Arrays.equals(Arrays.copyOfRange(allbytes, 32, 32 + "Parent: ".getBytes().length), "Parent: ".getBytes())) {
            parr = Arrays.copyOfRange(allbytes, 32 + "Parent: ".getBytes().length
                    , 32 * 2 + "Parent: ".getBytes().length);
            par = new Directory_Entry(parr);
        }
        int start = 32 * 2 + "Parent: ".getBytes().length + " :p>".getBytes().length;
        if (Arrays.equals(Arrays.copyOfRange(allbytes, start, start + "list: ".getBytes().length), "list: ".getBytes())) {
            parr = Arrays.copyOfRange(allbytes, start, start + " :l>".getBytes().length);
            for (int i = 0; i < parr.length; i += 32) {
                Directory_Entry entry1 = new Directory_Entry(Arrays.copyOfRange(parr, i, i + 32));
                if (arrayList.stream().noneMatch(entry2 -> entry2.getFileName().equalsIgnoreCase(entry1.getFileName()))) {
                    arrayList.add(entry1);
                }
            }
            if (parr.length == 32)
                par = new Directory_Entry(parr);
        }
        //print results
//        System.out.println("entry: " + entry);
//        System.out.println(arrayList);
//        System.out.println(par);

        //return
        if (entry != null)
            return new Directory(entry.getFileName(), entry.getFileAttr(), entry.getFileSize(), entry.getFileCluster()
                    , arrayList, null);
        else {
            System.out.println("cant found root");
            try {
                Files.delete(Path.of(System.getProperty("user.dir") + "\\fatTable.txt"));
            } catch (Exception ignored) {
            }
            return null;
        }
    }
    private static String getJson(String url) throws IOException {
        HttpsURLConnection connection = (HttpsURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("GET");
        connection.setUseCaches(true);
        connection.setRequestProperty("apikey", "f1e21bd0-cc90-11ec-89f2-edc9b68fdf57");
//        connection.setRequestProperty("country_id" , "48");
        connection.connect();
        String res = "";
        int Status = connection.getResponseCode();

        if (Status == 200) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder b = new StringBuilder();
            String line = "";
            while ((line = reader.readLine()) != null) {
                b.append(line + "\n");
            }
            res = b.toString();
        }
        return res;
    }
}
