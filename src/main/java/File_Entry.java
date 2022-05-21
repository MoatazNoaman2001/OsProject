import Models.Directory_Entry;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

public class File_Entry extends Directory_Entry {
    private Directory parent;
    private String content;

    public File_Entry(String fileName, byte fileAttr, int fileSize, int fileCluster, String Content, Directory parent) {
        super(fileName, fileAttr, fileSize, fileCluster);
        this.parent = parent;
        this.content = Content;
    }

    public File_Entry(Directory_Entry entry) {
        super(entry.getFileName(), entry.getFileAttr(), entry.getFileSize(), entry.getFileCluster());
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Directory getParent() {
        return parent;
    }

    public void setParent(Directory parent) {
        this.parent = parent;
    }

    void ReadFile(VirtualDiskImpl disk) throws IOException {
        byte[] abytes = disk.getBytes(getFileCluster());
        String st = new String(abytes, StandardCharsets.UTF_8);
        try {
            content = st.substring(st.indexOf(getFileName() + ": ") + (getFileName() + ": ").length(), st.indexOf(" :/s>"));
            if (true) return;
        }catch (Exception e){}
        for (int i = 0; i < abytes.length; i++)
            if (new String(Arrays.copyOfRange(abytes, i, i + getFileName().length()), StandardCharsets.UTF_8)
                    .equals(getFileName())) {
                String content = new String(Arrays.copyOfRange(abytes, i + (getFileName() + ": ").length()
                        , abytes.length), StandardCharsets.UTF_8);
                content = content.substring(0, " :/s>".length());
                System.out.println(content);
                this.content = content;
            }
    }

    void WriteFile(Fat fat) throws IOException {
        byte[] abytes = fat.getDisk().getBytes(getFileCluster());
        if (new String(abytes, StandardCharsets.UTF_8).contains(getFileName())) {
            DeleteFile(fat.getDisk());
            WriteFile(fat);
        }
        ArrayList<Byte> bytes = new ArrayList<>();
        int numOfBlocks = content.getBytes().length / 1_024;
        byte[] bytes1 = ("</" + getFileName() + ": ").getBytes();

        this.setFileCluster(fat.getAvailableBlock());
        if (numOfBlocks == 0) {
            for (byte aByte : bytes1) bytes.add(aByte);
            for (byte aByte : content.getBytes(StandardCharsets.UTF_8)) bytes.add(aByte);
            for (byte aByte : " :/s>".getBytes()) bytes.add(aByte);
            byte[] ab = new byte[bytes.size()];
            for (int i = 0; i < bytes.size(); i++) ab[i] = bytes.get(i);
            System.out.println("available block: " + getFileCluster());
            System.out.println("ab size : " + ab.length);
            fat.getDisk().setBytes(getFileCluster(), ab);
        } else {
            for (byte aByte : bytes1) bytes.add(aByte);
            for (int i = getFileCluster(), j = 0; j < Math.ceil(numOfBlocks / 1024); i++, j++) {
                int to = 0;
                if (i == getFileCluster() + numOfBlocks - 1) to = content.getBytes().length;
                else to = i + 1024 + 1024;

                System.out.println("content: " + content.getBytes().length);

                for (byte aByte : Arrays.copyOfRange(content.getBytes(), j * 1024, to)) bytes.add(aByte);
                byte[] ab = new byte[bytes.subList(i, to).size()];
                for (int i2 = 0; i2 < bytes.subList(i, to).size(); i2++) ab[i2] = bytes.get(i2);
                fat.getDisk().setBytes(to, ab);
            }
            for (byte aByte : " :/s>".getBytes()) bytes.add(aByte);
        }
        System.out.println(this);
        parent.getEntries().add(this);
        parent.updateDirectory(fat.getDisk());
    }

    void DeleteFile(VirtualDiskImpl disk) throws IOException {
        byte[] abytes = disk.getBytes(getFileCluster());
        for (int i = 0; i < abytes.length; i++)
            if (new String(Arrays.copyOfRange(abytes, i, i + getFileName().length()), StandardCharsets.UTF_8)
                    .equals(getFileName())) {
                String content = new String(Arrays.copyOfRange(abytes, i - 1
                        , abytes.length), StandardCharsets.UTF_8);
                content = content.substring(0, content.indexOf(":/s>") + ":/s>".length());
                System.out.println("con: " + content);
                String Block = new String(abytes, StandardCharsets.UTF_8);
                Block = Block.substring(0, i) + new String(new byte[content.length()], StandardCharsets.UTF_8)
                        + Block.substring(" :/s>".length() + " :/s>".length());
                disk.setBytes(getFileCluster(), Block.getBytes(StandardCharsets.UTF_8));
//                Arrays.equals(abytes , i , )
            }
    }
}
