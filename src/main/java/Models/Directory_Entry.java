package Models;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;

public class Directory_Entry {
    private String fileName;
    private byte fileAttr;
    private byte[] fileData;
    private int fileSize;
    private int fileCluster;

    public Directory_Entry(String fileName, byte fileAttr, int fileSize,  int fileCluster) {
        if (fileName.getBytes().length > 11) {
            String s1 = "", NewName;
            try {
                s1 = fileName.substring(fileName.lastIndexOf('.'));
            } catch (Exception e) {
                NewName = fileName.substring(0, 11);
            }
            NewName = fileName.substring(0, 11 - s1.length());
            fileName = NewName;
            fileName += s1;
        }
        this.fileName = fileName;
        this.fileAttr = fileAttr;
        this.fileSize = fileSize;
        this.fileData = new byte[12];
        this.fileCluster = fileCluster;
    }

    public Directory_Entry(byte[] bytes) {
        String str = new String(bytes, StandardCharsets.UTF_8);
        ByteBuffer byteBuffer = ByteBuffer.wrap(Arrays.copyOfRange(bytes, 0, 11));
        try {
        this.fileName = new String(bytes).substring(0 , new String(bytes).indexOf(new String(bytes).contains("-") ? '-' : '+') -1);

        }catch (Exception e){
            if (new String(Arrays.copyOfRange(bytes, 0, 11), StandardCharsets.UTF_8).contains(" "))
                this.fileName = new String(Arrays.copyOfRange(bytes, 0, 11), StandardCharsets.UTF_8).substring(0
                        , new String(Arrays.copyOfRange(bytes, 0, 11), StandardCharsets.UTF_8).indexOf(' '));
            else this.fileName = new String(Arrays.copyOfRange(bytes, 0, 11), StandardCharsets.UTF_8);

//            this.fileName  = fileName.substring(0 , fileName.contains("+")? fileName.indexOf('+') : fileName.indexOf('_'));
        }

        this.fileAttr = (byte) str.chars().filter(value -> value == '+' || value == '-').findFirst().orElse('+');
        this.fileData = new byte[12];
        this.fileSize = ByteBuffer.wrap(Arrays.copyOfRange(bytes, 24, 28)).getInt();
        this.fileCluster = ByteBuffer.wrap(Arrays.copyOfRange(bytes, 28, 32)).getInt();
    }


    public byte[] getBytes() {
        Directory_Entry entry = this;
        byte[] totalBytes = new byte[32];
        System.arraycopy(entry.fileName.getBytes(), 0, totalBytes, 0, entry.fileName.getBytes().length);
        totalBytes[entry.fileName.getBytes().length + 1] = entry.fileAttr;
        System.arraycopy(entry.fileData, 0, totalBytes, 12, entry.fileData.length);
        byte[] fileSizeBytes = ByteBuffer.allocate(4).putInt(entry.fileSize).array();
        System.arraycopy(fileSizeBytes, 0, totalBytes, 24, fileSizeBytes.length);
        byte[] fileClusterBytes = ByteBuffer.allocate(4).putInt(entry.fileSize).array();
        System.arraycopy(fileClusterBytes, 0, totalBytes, 28, fileClusterBytes.length);
        return totalBytes;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public byte getFileAttr() {
        return fileAttr;
    }

    public void setFileAttr(byte fileAttr) {
        this.fileAttr = fileAttr;
    }

    public byte[] getFileData() {
        return fileData;
    }

    public void setFileData(byte[] fileData) {
        this.fileData = fileData;
    }

    public int getFileSize() {
        return fileSize;
    }

    public void setFileSize(int fileSize) {
        this.fileSize = fileSize;
    }

    public int getFileCluster() {
        return fileCluster;
    }

    public void setFileCluster(int fileCluster) {
        this.fileCluster = fileCluster;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Directory_Entry entry = (Directory_Entry) o;
        return fileAttr == entry.fileAttr && fileSize == entry.fileSize && fileCluster == entry.fileCluster && Objects.equals(fileName, entry.fileName) && Arrays.equals(fileData, entry.fileData);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(fileName, fileAttr, fileSize, fileCluster);
        result = 31 * result + Arrays.hashCode(fileData);
        return result;
    }

    @Override
    public String toString() {
        return "Models.Directory_Entry{" +
                "fileName='" + fileName + '\'' +
                ", fileAttr=" + (char) fileAttr +
                ", fileData=" + Arrays.toString(fileData) +
                ", fileSize=" + fileSize +
                ", fileCluster=" + fileCluster +
                '}';
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {

        return super.clone();
    }
}
