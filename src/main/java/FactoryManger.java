import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class FactoryManger {

    public static void main(String[] args) throws IOException, InterruptedException {
//        String text = "http://geer.egybest.me/vs-mirror/vidstream.to/dostream?k=86db666480f4d012eBx-F8BQ27TtcFeLv.Q5eNZZENDR5rhCgmcw5FuQ__.bEhFMnIvRHRFSmZVc1I1T3EyUnlyd2krZ1FodUF3Z3JNRHcxUWJYVWpsVnl5TTQvdkZRK2lvM043QlJXKzVFYzN6dFpFWDZZaUFQNXFMUTFuckRpSktuQXN2blIxK3pPbTNzN1VGYzJCdnNMQ2ZxWVcyRG4xR0JZNXdCSWFKR2lVbGhHZFVaRWlpVGFxRUIyK2xZeUVVb0JhOUJzNHc9PQ__";
//        Document document = Jsoup.connect("http://geer.egybest.me/vs-mirror/vidstream.to/dostream?k=86db666480f4d012eBx-F8BQ27TtcFeLv.Q5eNZZENDR5rhCgmcw5FuQ__.bEhFMnIvRHRFSmZVc1I1T3EyUnlyd2krZ1FodUF3Z3JNRHcxUWJYVWpsVnl5TTQvdkZRK2lvM043QlJXKzVFYzN6dFpFWDZZaUFQNXFMUTFuckRpSktuQXN2blIxK3pPbTNzN1VGYzJCdnNMQ2ZxWVcyRG4xR0JZNXdCSWFKR2lVbGhHZFVaRWlpVGFxRUIyK2xZeUVVb0JhOUJzNHc9PQ__")
//                .get();
//        System.out.println("lines count: " + document.body());
//
//        FileOutputStream fileOutputStream = new FileOutputStream(text);
        File originaleFile = new File("C:\\Users\\Mo3taz kayad\\IdeaProjects\\NewMvnProject\\src\\main\\java\\FactoryManger.java"),
                targetFile = new File("C:\\Users\\Mo3taz kayad\\IdeaProjects\\NewMvnProject\\target", originaleFile.getName());

        Function<Integer, int[]> integerFunction = integer -> IntStream.iterate(0, v -> v + 1).limit(integer)
                .filter(value -> value == 2 || ((value < 9 & value > 2) && value % 2 != 0) ||
                        IntStream.range(2, 9).noneMatch(x -> value % x == 0)).toArray();
        long start = System.nanoTime();
        int[] primeNumber = integerFunction.apply(1000);
        System.out.println("\n mille sec: " + TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start));
        long start2 = System.nanoTime();
        int[] primeNumber2 = streamarray(1000);
        System.out.println("\n mille sec: " + TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start2));


        System.out.println(Arrays.stream(primeNumber).summaryStatistics());
        System.out.println(Arrays.stream(primeNumber).skip(15).summaryStatistics());

        Stream.generate(new Supplier<Integer>() {
            @Override
            public Integer get() {
                return Math.toIntExact(TimeUnit.NANOSECONDS.toMillis(System.nanoTime()));
            }
        }).limit(10).sorted().forEach(integer -> System.out.print("\t" + integer));

        System.out.println("");
        Stream.iterate(1, new UnaryOperator<Integer>() {
                    @Override
                    public Integer apply(Integer integer) {
                        return integer + 9;
                    }
                })
                .limit(10).sorted().forEach(integer -> System.out.print("\t" + integer));
        System.out.println();
        System.out.println(Stream.iterate(6, new Predicate<Integer>() {
                    @Override
                    public boolean test(Integer integer) {
                        return integer > 5;
                    }
                }, new UnaryOperator<Integer>() {
                    @Override
                    public Integer apply(Integer integer) {
                        return integer + 1;
                    }
                })
                .limit(105).sorted().map(String::valueOf)
                .collect(Collectors.joining(", ", "[", "]")));

        System.out.println("\n");
        int sum = Stream.of(1, 2, 3).reduce(0, Integer::sum);
        System.out.println("Stream.of(1,2,3).reduce(0, acc): " + sum);

        String str = Stream.of(1, 2, 3, 4)
                .reduce("0", (i, i2) -> i + i2.toString(), (x, y) -> x + ", " + y);
        System.out.println(str);




        //stream dropWhile and TackWhile
        Stream<String> paths = Files.lines(Paths.get("C:\\Users\\Mo3taz kayad\\IdeaProjects\\NewMvnProject\\src\\main\\java\\FactoryManger.java"));
        paths.dropWhile(s -> !s.contains("System.out.println(\"\\n\\n\");"))
                .takeWhile(s -> !s.contains("FileSystems.getDefault().getFileStores().iterator()"))
                .forEach(System.out::println);

        //print sys Directory information
        FileSystems.getDefault().getFileStores().iterator().forEachRemaining(fileStore -> {
            try {
                System.out.println("file name:" + fileStore.name()
                        + "\n\ttype: " + fileStore.type()
                        + "\n\ttotal size: " + fileStore.getTotalSpace()
                        + "\n\tused space: " + fileStore.getUsableSpace()
                        + "\n\tfree space: " + (fileStore.getTotalSpace() - fileStore.getUsableSpace()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        //search in files by stream
        long Strt = System.nanoTime();
        Path HardDiskDir = FileSystems.getDefault().getPath("D:\\");
        try (Stream<Path> pathStream = Files.list(HardDiskDir)) {
            pathStream.forEach(pt -> {
                BiPredicate<Path, BasicFileAttributes> select =
                        (p, b) -> p.getFileName().toString().contains("chapter");
                try (Stream<Path> pathStream2 = Files.find(pt, 4, select)) {
                    pathStream2.map(Path::getFileName).forEach(System.out::println);
                } catch (Exception ex) {
//                    ex.printStackTrace();
                }
            });
        }
        System.out.println("search run time: " + TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - Strt) + " milli ");

//        Path dir = FileSystems.getDefault().getPath("s");
//        System.getenv().forEach((s, s2) -> System.out.println(s + ": " + s2));
    }

    static private int[] streamarray(int limit) {
        return IntStream.iterate(0, v -> v + 1).limit(limit)
                .filter(value -> value == 2 || ((value < 9 & value > 2) && value % 2 != 0) ||
                        IntStream.range(2, 9).noneMatch(x -> value % x == 0)).toArray();
    }


    static private final Function<String, Void> DirFun = new Function<String, Void>() {
        @Override
        public Void apply(String s) {
            Path dir = FileSystems.getDefault().getPath(s);
            try (Stream<Path> pathStream = Files.list(dir)) {
                pathStream.map(path -> {
                    try {
//                        long size = 0;
//                        List<Path> paths = Files.list(path).collect(Collectors.toList());
//                        for (Path path1 : paths) {
//                            if (path1.toFile().isFile()) {
//                                FileInputStream inputStream
//                                        = new FileInputStream(path1.toFile());
//                                size += inputStream.readAllBytes().length;
//                            }
//                        }
                        BasicFileAttributes fileAttributes = Files.readAttributes(path, BasicFileAttributes.class);
                        FileTime time = fileAttributes.creationTime();
                        SimpleDateFormat dateFormat
                                = new SimpleDateFormat("yyy-MM-dd hh:mm a");
                        String date = dateFormat.format(new Date(time.toMillis()));

                        return date + "\t\t" + fileAttributes.size() + "\t" + path.getFileName();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return path.getFileName();
                }).forEach(System.out::println);
            } catch (Exception ex) {
            }
            return null;
        }
    };
    static private final BiPredicate<String, String> MkDirFun = new BiPredicate<String, String>() {
        @Override
        public boolean test(String s, String s2) {
            Path dir = FileSystems.getDefault().getPath(s);
            File file = new File(dir.toFile(), s2);
            return file.mkdir();
        }
    };
    static private final BiPredicate<String, String> DlFolderDirFun = new BiPredicate<String, String>() {
        @Override
        public boolean test(String s, String s2) {
            Path dir = FileSystems.getDefault().getPath(s);
            File file = new File(dir.toFile(), s2);
            return file.delete();
        }
    };
    static private final BiFunction<String, String, List<Path>> SearchCommandFun = new BiFunction<String, String, List<Path>>() {
        @Override
        public List<Path> apply(String s, String s2) {
            long Strt = System.nanoTime();
            AtomicReference<List<Path>> result = new AtomicReference<>();
            Path HardDiskDir = FileSystems.getDefault().getPath(s);
            try (Stream<Path> pathStream = Files.list(HardDiskDir)) {
                pathStream.forEach(path -> {
                    BiPredicate<Path, BasicFileAttributes> select =
                            (p, b) -> {
                                if (path.toAbsolutePath().toString().contains(s2) && s2.contains("/"))
                                    return true;
                                return p.getFileName().toString().contains(s2) |
                                        p.getFileName().toString().toLowerCase()
                                                .contains(s2.toLowerCase());
                            };
                    try (Stream<Path> pathStream2 = Files.find(path, 8, select)) {
                        result.set(pathStream2.map(Path::getFileName).collect(Collectors.toList()));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("search run time: " + TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - Strt) + " milli ");
            return result.get();
        }
    };
    static private final BiPredicate<String, String> CopyFile = new BiPredicate<String, String>() {
        @Override
        public boolean test(String s, String s2) {
            try {
                File originaleFile = new File(s),
                        targetFile = new File(s2, originaleFile.getName());
                targetFile.mkdirs();
                Files.copy(originaleFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("error in copy probably in path \n" + s + "\n" + s2);
            }
            return false;
        }
    };

    static private final BiPredicate<String, String> MoveFiles = new BiPredicate<String, String>() {
        @Override
        public boolean test(String s, String s2) {
            try {
                File originaleFile = new File(s),
                        targetFile = new File(s2, originaleFile.getName());
                targetFile.mkdirs();
                Files.move(originaleFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("error in copy probably in path \n" + s + "\n" + s2);
            }
            return false;
        }
    };
    public static final BiFunction<String, String, String> CmdIterateProgram = (command, path) -> {
        String originalCommand = command;
        command = command.toLowerCase();
        if (command.startsWith("cd")) {
            String[] commandWords = command.trim().split(" ", 2);
            String[] originalCommandWords = originalCommand.trim().split(" ", 2);
            if (commandWords.length <= 1) {
                System.out.println("inComplete command of cd \r you should write path name or double dot for back");
            } else {
                if (!commandWords[1].equals("..")) {
                    if (commandWords.length > 2)
                        originalCommandWords[1] = originalCommandWords[1].concat(" " + originalCommandWords[2]);
                    if (commandWords[1].startsWith("c:")
                            || commandWords[1].startsWith("d:")) {
                        if (commandWords[1].lastIndexOf(".") > -1) {
                            DirFun.apply(commandWords[1].substring(0, commandWords[1].lastIndexOf("\\")));
                            path = commandWords[1].substring(0, commandWords[1].lastIndexOf("\\"));
                        } else if (commandWords[1].contains(".")) {
                            System.out.println("use specific tool to open format like notepad for .txt");
                        } else {
                            DirFun.apply(originalCommandWords[1]);
                            path = originalCommandWords[1];
                        }
                    } else {
                        if (commandWords[1].length() > 10) {
                            if (SearchCommandFun.apply(path, originalCommandWords[1]).size() == 0)
                                System.out.println("error no files with this name");
                            return path;
                        }
                        if (commandWords[1].contains(".")) {
                            System.out.println("use specific tool to open format like notepad for .txt");
                        } else {
                            path = path.concat("\\" + commandWords[1]);
                            DirFun.apply(path);
                        }
                    }
                } else {
                    path = path.substring(0, path.lastIndexOf("\\"));
                }
            }
        }
        else if (command.startsWith("dir")) {
            if (command.trim().length() > "dir".length()) {
                System.out.println("dir fun should not take any argument please check the command and try again");
            }
            DirFun.apply(path);
        }
        else if (command.equals("exit")) {
            System.out.println("thanks for using win cmd");
	    try{Runtime.getRuntime().exec("taskkill /f /im cmd.exe");}catch(IOException e){}
        }
        else if (command.startsWith("mkdir")) {
            String[] commandWords = command.split(" ");
            if (commandWords.length <= 1) {
                System.out.println("inComplete command of mkdir \r you should write folder name");
            } else {
                if (commandWords[1].contains(".")) {
                    System.out.println("you should use md to create files not mkdir -_-");
                } else {
                    MkDirFun.test(path, commandWords[1]);
                }
            }
        }
        else if (command.startsWith("del")) {
            String[] commandWords = command.split(" ");
            if (commandWords.length <= 1) {
                System.out.println("inComplete command of delete \r you should write folder name");
            } else {
                DlFolderDirFun.test(path, commandWords[1]);
            }
        }
        else if (command.startsWith("cat")) {
            String[] commandWords = command.split(" ");
            if (commandWords.length <= 1) {
                System.out.println("error cant execute this command");
            } else {
                try {
                    Files.lines(Paths.get(path.concat("\\" + originalCommand.split(" ")[1]))).forEach(System.out::println);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        else if (command.startsWith("search")) {
            String[] commandWords = command.split(" ");
            if (commandWords.length <= 1) {
                System.out.println("error cant execute this command");
            } else {
                SearchCommandFun.apply(path, commandWords[1]).forEach(System.out::println);
            }
        }
        else if (command.startsWith("date")) {
            System.out.println("current date is: " + Calendar.getInstance().getTime().toGMTString());
        }
        else if (command.startsWith("copy")) {
            String[] originalCommandWords = originalCommand.split(" ", 2),
                    commandWords = command.split(" ", 2);

            if (originalCommandWords.length == 3) {
                if ((commandWords[1].startsWith("\"")) && (commandWords[1].endsWith("\"")) &&
                        (commandWords[2].startsWith("\"") && (commandWords[2].endsWith("\"")))) {
                    if (commandWords[2].substring(1).startsWith("c:")
                            || commandWords[2].substring(1).startsWith("d:")) {
                        if (CopyFile.test(path.concat("\\" + originalCommandWords[1].substring(1, originalCommandWords[1].length() - 1)), originalCommandWords[2].substring(1, originalCommandWords[2].length() - 1)))
                            System.out.println("succeeded");
                    } else {
                        System.out.println("there an error in second parameter \n" +
                                "would you like to search for this path in current destination? y or n");
                        if (new Scanner(System.in).nextLine().equalsIgnoreCase("y")) {
                            SearchCommandFun.apply(path, originalCommandWords[2]).forEach(System.out::println);
                            System.out.println("choose wanted path and try again");
                        }
                    }

                } else {
                    System.out.println("you should put \" in original path and target path");
                }

            }
            else {
                System.out.println("enter target path");
                String target = new Scanner(System.in).nextLine();
                if (!target.isBlank()) {
                    boolean targetBool = target.toLowerCase().startsWith("c:") | target.toLowerCase().startsWith("d:");
                    if (targetBool && commandWords[1].startsWith("c:") | commandWords[1].startsWith("d:")) {
                        if (CopyFile.test(originalCommandWords[1], target))
                            System.out.println("succeeded");
                    } else if (target.toLowerCase().contains("desktop")) {
                        if (commandWords[1].startsWith("c:") | commandWords[1].startsWith("d:")) {
                            if (CopyFile.test(originalCommandWords[1], "C:\\Users\\Mo3taz kayad\\Desktop"))
                                System.out.println("succeeded");
                        }else{
                            if (CopyFile.test(path.concat("\\" + originalCommandWords[1]), "C:\\Users\\Mo3taz kayad\\Desktop"))
                                System.out.println("succeeded");
                        }
                    } else if (targetBool && !(commandWords[1].startsWith("c:") | commandWords[1].startsWith("d:"))) {
                        if (CopyFile.test(path.concat("\\" + originalCommandWords[1]), target))
                            System.out.println("succeeded");
                    } else if (!targetBool && commandWords[1].startsWith("c:") | commandWords[1].startsWith("d:")) {
                        target = path.concat("\\" + target);
                        if (CopyFile.test("\\" + originalCommandWords[1], target))
                            System.out.println("succeeded");
                    } else {
                        target = path.concat("\\" + target);
                        if (!(commandWords[1].startsWith("c:") | commandWords[1].startsWith("d:"))) {
                            if (CopyFile.test(path.concat("\\" + originalCommandWords[1]), target))
                                System.out.println("succeeded");
                        } else {
                            if (CopyFile.test(originalCommandWords[1], target))
                                System.out.println("succeeded");
                        }
                    }
                }
            }

        }
        else if (command.startsWith("move")){
            String[] originalCommandWords = originalCommand.split(" ", 2),
                    commandWords = command.split(" ", 2);
            System.out.println("enter target path");
            String target = new Scanner(System.in).nextLine();
            if (!target.isBlank()) {
                boolean targetBool = target.toLowerCase().startsWith("c:") | target.toLowerCase().startsWith("d:");
                if (targetBool && commandWords[1].startsWith("c:") | commandWords[1].startsWith("d:")) {
                    if (MoveFiles.test(originalCommandWords[1], target))
                        System.out.println("succeeded");
                } else if (target.toLowerCase().contains("desktop")) {
                    if (commandWords[1].startsWith("c:") | commandWords[1].startsWith("d:")) {
                        if (MoveFiles.test(originalCommandWords[1], "C:\\Users\\Mo3taz kayad\\Desktop"))
                            System.out.println("succeeded");
                    }else{
                        if (MoveFiles.test(path.concat("\\" + originalCommandWords[1]), "C:\\Users\\Mo3taz kayad\\Desktop"))
                            System.out.println("succeeded");
                    }
                } else if (targetBool && !(commandWords[1].startsWith("c:") | commandWords[1].startsWith("d:"))) {
                    if (MoveFiles.test(path.concat("\\" + originalCommandWords[1]), target))
                        System.out.println("succeeded");
                } else if (!targetBool && commandWords[1].startsWith("c:") | commandWords[1].startsWith("d:")) {
                    target = path.concat("\\" + target);
                    if (MoveFiles.test("\\" + originalCommandWords[1], target))
                        System.out.println("succeeded");
                } else {
                    target = path.concat("\\" + target);
                    if (!(commandWords[1].startsWith("c:") | commandWords[1].startsWith("d:"))) {
                        if (MoveFiles.test(path.concat("\\" + originalCommandWords[1]), target))
                            System.out.println("succeeded");
                    } else {
                        if (MoveFiles.test(originalCommandWords[1], target))
                            System.out.println("succeeded");
                    }
                }
            }

        }
        else if (command.startsWith("cls")) {
            try {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
        }
        else if (command.startsWith("help")) {
            System.out.println(
                    "CD             Displays the name of or changes the current directory." +
                            "\nDEL            Deletes one or more files.\n" +
                            "\nDIR            Displays a list of files and subdirectories in a directory." +
                            "\nEXIT           Quits the CMD.EXE program (command interpreter)." +
                            "\nMKDIR          Creates a directory." +
                            "\nDEL            Deletes one or more files." +
                            "\nCOPY           Copies one or more files to another location." +
                            "\ncat           display the content of txt,py,java,etc... file" +
                            "\nDATE           Displays or sets the date.");


        } else {
            System.out.println("undetermined command" + command);
        }
        return path;
    };

    static void CmdFun(Scanner scanner, String path) {
        scanner = new Scanner(System.in);
        System.out.print(path + ">");
        String command = scanner.nextLine();
        if (command.startsWith("cd")) {
            String[] commandWords = command.split(" ");
            if (commandWords.length <= 1) {
                System.out.println("inComplete command of cd \r you should write path name or double dot for back");
            } else {
                if (!commandWords[1].equals("..")) {
                    if (commandWords[1].contains(".")) {
                        System.out.println("use specific tool to open format like notepad for .txt");
                    } else {
                        System.out.println(commandWords[1]);
                        path = path.concat("\\" + commandWords[1]);
                        DirFun.apply(path);
                    }
                } else {
                    path = path.substring(0, path.lastIndexOf("\\"));
                }
            }
        } else if (command.startsWith("dir")) {
            if (command.trim().length() > "dir".length()) {
                System.out.println("dir fun should not take any argument please check the command and try again");
            }
            DirFun.apply(path);
        } else if (command.startsWith("mkdir")) {
            String[] commandWords = command.split(" ");
            if (commandWords.length <= 1) {
                System.out.println("inComplete command of mkdir \r you should write folder name");
            } else {
                if (commandWords[1].contains(".")) {
                    System.out.println("you should use md to create files not mkdir -_-");
                } else {
                    MkDirFun.test(path, commandWords[1]);
                }
            }
        } else if (command.startsWith("del")) {
            String[] commandWords = command.split(" ");
            if (commandWords.length <= 1) {
                System.out.println("inComplete command of delete \r you should write folder name");
            } else {
                DlFolderDirFun.test(path, commandWords[1]);
            }
        } else {
            System.out.println("undetermined command: " + command);
            CmdFun(scanner, path);
        }
    }

}
