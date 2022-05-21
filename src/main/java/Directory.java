import Models.Directory_Entry;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Directory extends Directory_Entry {
    private ArrayList<Directory_Entry> entries;
    private final Directory parent;

    public Directory(String fileName, byte fileAttr, int fileSize, int fileCluster, ArrayList<Directory_Entry> entries, Directory parent) throws IOException {
        super(fileName, fileAttr, fileSize, fileCluster);
        this.entries = entries;
        this.parent = parent;
    }

    public Directory(ArrayList<Directory_Entry> entries, Directory parent) throws IOException {
        super(parent.getFileName(), parent.getFileAttr(), parent.getFileSize(), parent.getFileCluster());
        this.entries = entries;
        this.parent = parent;
    }

    public Directory(Directory_Entry entry, Directory parent) throws IOException {
        super(entry.getFileName(), entry.getFileAttr(), entry.getFileSize(), entry.getFileCluster());
        this.parent = parent;
    }

    public void setEntries(ArrayList<Directory_Entry> entries) {
        this.entries = entries;
    }

    public ArrayList<Directory_Entry> getEntries() {
        return entries;
    }

    synchronized void WriteDirectory(Fat fat) throws IOException {
        VirtualDiskImpl vvd = fat.getDisk();
        List<Byte> bytes = new LinkedList<>();
        if (fat.getAvailableBlocks() == 0) {
            System.out.println("no enough space");
            return;
        }
        if (this.getFileName().equals("root")) {
            this.setFileAttr((byte) '+');
            byte[] bs = getBytes();
            vvd.setBytes(0, bs);
            vvd.SetNext(5, 'P');
            fat.setAvailableBlock(0, 1);
        } else {
            this.setFileSize(getBytes().length + parent.getBytes().length);
//            int count = 0;
//            for (byte aByte : vvd.getBytes(fat.getAvailableBlock() - 1)) if (aByte == 0) count++;
//            if (getFileSize() < 1024)
//                if (count >= getFileSize()) setFileCluster(vvd.getAvailableBlock() - 1);
//                else setFileCluster(vvd.getAvailableBlock());
//            else {
//                if (count >= getFileSize() % 1024) setFileCluster(vvd.getAvailableBlock() - 1);
//                else setFileCluster(vvd.getAvailableBlock());
//            }
            this.setFileCluster(fat.getAvailableBlock());

            if (parent.getFileName().trim().equals("root")) {
                if (parent.getEntries().isEmpty())
                    parent.setEntries(new ArrayList<>(List.of(this)));
                else {
                    ArrayList<Directory_Entry> en = new ArrayList<>(parent.getEntries());
                    if (en.stream().noneMatch(entry -> entry.getFileName().equals(getFileName())))
                        en.add(this);
                    en = new ArrayList<>(new LinkedHashSet<>(en));
                    parent.setEntries(en);
                }
                System.out.println(this.getFileName() + "  "+ this.getFileCluster());
                parent.updateDirectory(vvd);
            }
            for (byte aByte : getBytes()) bytes.add(aByte);
            for (byte b : "Parent: ".getBytes()) bytes.add(b);
            for (byte aByte : parent.getBytes()) bytes.add(aByte);
            for (byte b : " :p>".getBytes()) bytes.add(b);
            if (bytes.size() < 1024) {
                byte[] arr = new byte[bytes.size()];
                for (Byte aByte : bytes) arr[bytes.indexOf(aByte)] = aByte;
                vvd.setBytes(getFileCluster(), arr);
                vvd.SetNext(getFileCluster(), 'P');
            } else {
                if (fat.getAvailableBlock() - 1 == getFileCluster()) {
                    int numOfBlocks = (bytes.size() / 1024) + (bytes.size() % 1024);
                    if (fat.getAvailableBlocks() > numOfBlocks) {
                        for (int i = 0; i < bytes.size(); i += 1024) {
                            byte[] bb = new byte[1024];
                            if (bytes.size() < i + 1024) {
                                for (int j = (bytes.size() / 1024) * 1024, o = 0; j < bytes.size()
                                        ; j++, o++) {
                                    bb[o] = bytes.subList(j, bytes.size()).get(j);
                                }
                                vvd.setBytes(getFileCluster(), bb);
                                vvd.SetNext(getFileCluster(), vvd.getAvailableBlock() + 1);
                                fat.setAvailableBlock(getFileCluster(), -1);
                            } else {
                                for (int i1 = 0; i1 < bytes.subList(i, i + 1024).size(); i1++)
                                    bb[i1] = bytes.subList(i, i + 1024).get(i1);
                                vvd.setBytes(vvd.getAvailableBlock(), bb);
                                vvd.SetNext(vvd.getAvailableBlock(), vvd.getAvailableBlock() + 1);
                                fat.setAvailableBlock(vvd.getAvailableBlock(), vvd.getAvailableBlock() + 1);
                            }
                        }
                    } else {
                        System.out.println("no enough space for " + numOfBlocks + " of blocks");
                    }
                }
            }

            parent.getEntries().add(this);
            parent.updateDirectory(vvd);
        }
    }

    void ReadDirectory(VirtualDiskImpl vvd) throws IOException {
        byte[] allbytes = vvd.getBytes(getFileCluster());
        String str = new String(allbytes, StandardCharsets.UTF_8);
        int index = str.indexOf("list: ") + "list: ".length();
        ArrayList<Directory_Entry> ent = new ArrayList<>();
        for (int i = index; i < str.indexOf(" :l>"); i += 32) {
            Directory_Entry entry = new Directory_Entry(Arrays.copyOfRange(allbytes, i, i + 32));
            entry.setFileName(entry.getFileName().trim());
            if (entries.stream().noneMatch(e1 -> e1.getFileName().equals(entry.getFileName()))) {
                if (entry.getFileName().contains("+"))
                    entry.setFileName(entry.getFileName().substring(0, entry.getFileName().indexOf("+") - 1));
                entries.add(entry);
            }
            ent.add(entry);
        }
        entries = getEntries().stream().filter(e -> ent.stream().anyMatch(t -> e.getFileName().equals(t.getFileName()))).collect(Collectors.toCollection(ArrayList::new));
    }

    int SearchDirectory(String name, VirtualDiskImpl disk) throws IOException {
        ReadDirectory(disk);
        return entries.stream().filter(entry1 -> entry1.getFileName().equals(name)).findFirst().map(entries::indexOf).orElse(-1);
    }

    boolean updateDirectory(VirtualDiskImpl disk) throws IOException {
//        WriteDirectory(fat);
        List<Byte> bytes = new ArrayList<>();
        for (byte aByte : getBytes()) bytes.add(aByte);
        if (parent != null) {
            for (byte b : "Parent: ".getBytes()) bytes.add(b);
            for (byte aByte : parent.getBytes()) bytes.add(aByte);
            for (byte b : " :p>".getBytes()) bytes.add(b);
        }
        for (byte aByte : "list: ".getBytes()) bytes.add(aByte);
        for (Directory_Entry entry : entries) for (byte aByte : entry.getBytes()) bytes.add(aByte);
        byte[] bytes1 = " :l>".getBytes();
        for (byte aByte : bytes1) bytes.add(aByte);
        byte[] ab = new byte[bytes.size()];
        IntStream.range(0, bytes.size()).forEachOrdered(i -> ab[i] = bytes.get(i));
        int numberOfBlocks = Math.max(ab.length / 1024, 1);
        if (numberOfBlocks == 1)
            return disk.setBytes(getFileCluster(), ab);
        else {
            for (int i = 0, j = getFileCluster(); i < ab.length; i += 1024, j++) {
                if (i + 1024 > ab.length) {
                    boolean res = disk.setBytes(j, Arrays.copyOfRange(ab, i, ab.length));
                    if (!res) {
                        System.out.println("error happen in updating " + getFileName());
                        return false;
                    }
                    break;
                }
                boolean res = disk.setBytes(j, Arrays.copyOfRange(ab, i, i + 1024));
                if (!res) {
                    System.out.println("error happen in updating " + getFileName());
                    return false;
                }
            }
            return true;
        }
    }

    boolean DeleteDirectory(VirtualDiskImpl disk, Directory_Entry entry) throws IOException {
        if (entries.remove(entry)) {
            disk.SetNext(entry.getFileCluster(), '*');
            return updateDirectory(disk);
        }
        return false;
    }

    public Directory getParent() {
        return parent;
    }
}
