import Models.Directory_Entry;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;

interface VirtualDisk {
    void create() throws IOException;

    byte[] getBytes() throws IOException;

    int getAvailableBlock() throws IOException;

    int getNext(int index) throws IOException;

    boolean SetNext(int index, int value) throws IOException;

    boolean setBytes(int index, byte[] bytes) throws IOException;

    byte[] getBytes(int index) throws IOException;

//    boolean updateBlock(int index , int from , int to, byte[] bytes) throws IOException;
}

class VirtualDiskImpl implements VirtualDisk {

    private String storeDir = System.getProperty("user.dir") + "\\fatTable.txt";

    public VirtualDiskImpl() throws IOException {
        FileInputStream InputStream = null;
        try {
            InputStream = new FileInputStream(storeDir);
        } catch (FileNotFoundException e) {
            FileOutputStream outputStream = new FileOutputStream(storeDir);
            InputStream = new FileInputStream(storeDir);
        }
        if (InputStream.readAllBytes().length == 0) {
            create();
        }
    }

    @Override
    public void create() throws IOException {
        FileOutputStream outputStreamOUT = new FileOutputStream(storeDir);
        byte[][] bytes = new byte[1024][1024];
        outputStreamOUT.write(bytes[0]);
        int[] n2 = new int[1024];
        Arrays.fill(n2, 0, 4, -1);
        Arrays.fill(n2, 5, n2.length, '*');
        for (int j : n2) {
            outputStreamOUT.write(ByteBuffer.allocate(4).putInt(j).array());
        }
        for (int i = 5; i < 1024; i++) {
            outputStreamOUT.write(bytes[i]);
        }
        outputStreamOUT.close();
    }

    @Override
    public byte[] getBytes() throws IOException {
        FileInputStream InputStream = new FileInputStream(storeDir);
        byte[] bytes = InputStream.readAllBytes();
        return Arrays.copyOfRange(bytes, 1024 * 5, bytes.length);
    }

    @Override
    public synchronized int getAvailableBlock() throws IOException {
        byte[] bytes = new FileInputStream(storeDir).readAllBytes();
        byte[] FatTable = Arrays.copyOfRange(bytes, 1024, 1024 * 5);
        int[] FatTableIntArr = new int[FatTable.length / 4];
        int i = 0;
        int j = 0;
        while (i <= FatTable.length - 1) {
            FatTableIntArr[j] = ConvertToInt(FatTable[i], FatTable[i + 1], FatTable[i + 2], FatTable[i + 3]);
            j++;
            i += 4;
        }
        return IntStream.range(0, FatTableIntArr.length).filter(i1 -> FatTableIntArr[i1] == '*').findFirst().orElse(-1);
    }

    @Override
    public int getNext(int index) throws IOException {
        if (index > 1024)
            return -1;
        FileInputStream InputStream = new FileInputStream(storeDir);
        byte[] bytes = InputStream.readAllBytes();
        byte[] FatTable = Arrays.copyOfRange(bytes, 1024, 1024 * 5);
        int[] FatTableIntArr = new int[FatTable.length / 4];
        int i = 0;
        int j = 0;
        while (i <= FatTable.length - 1) {
            FatTableIntArr[j] = ConvertToInt(FatTable[i], FatTable[i + 1], FatTable[i + 2], FatTable[i + 3]);
            j++;
            i += 4;
        }
        return FatTableIntArr[index];
    }

    @Override
    public boolean SetNext(int index, int value) throws IOException {
        if (index >= 1024)
            return false;
        FileInputStream InputStream = new FileInputStream(storeDir);
        byte[] bytes = InputStream.readAllBytes();
        byte[] FatTableIN = Arrays.copyOfRange(bytes, 1024, 1024 * 5),
                FatTableOUT = new byte[FatTableIN.length];
        int[] FatTableIntArr = new int[FatTableIN.length / 4];
        int i = 0;
        int j = 0;
        while (i <= FatTableIN.length - 1) {
            FatTableIntArr[j] = ConvertToInt(FatTableIN[i], FatTableIN[i + 1], FatTableIN[i + 2], FatTableIN[i + 3]);
            j++;
            i += 4;
        }
        FatTableIntArr[index] = value;
        int s = 0, l = 0;
        while (s <= FatTableIntArr.length - 1) {
            byte[] bytes1 = ByteBuffer.allocate(4).putInt(FatTableIntArr[s]).array();
            FatTableOUT[l] = bytes1[0];
            FatTableOUT[l + 1] = bytes1[1];
            FatTableOUT[l + 2] = bytes1[2];
            FatTableOUT[l + 3] = bytes1[3];
            l += 4;
            s++;
        }
        System.arraycopy(FatTableOUT, 0, bytes, 1024, 1024 * 4);
        FileOutputStream outputStream = new FileOutputStream(storeDir);
        outputStream.write(bytes);
        outputStream.close();
        return true;
    }

    @Override
    public boolean setBytes(int index, byte[] bytes) throws IOException {
        if (index > 1024 || index == -1 || bytes.length > 1024) {
            return false;
        }
        byte[] ab = new FileInputStream(storeDir).readAllBytes();
        byte[] nb = new byte[1024 * 1024 - 1024 * 5];
        if (ab.length - 5120 >= 0) System.arraycopy(ab, 1024 * 5, nb, 0, ab.length - 5120);
        byte[][] blocks = new byte[1024][1024];
        for (int i = 0, j = 0; i < nb.length; i += 1024, j++) blocks[j] = Arrays.copyOfRange(nb, i, i + 1024);
//        if (Arrays.equals(blocks[index], new byte[1024]))
        System.arraycopy(bytes, 0, blocks[index], 0, bytes.length);
//        else {
//            int ind = 0;
//            for (int i = 1023; i >= 0; i--) {
//                if (blocks[index][i] != 0) {
//                    ind = ++i;
//                    break;
//                }
//            }
//            System.arraycopy(bytes, 0, blocks[index], ind, Math.min(bytes.length , blocks[index].length - ind));
//        }
        SetNext(index, 'p');
        System.arraycopy(blocks[index], 0, nb, index * 1024, 1024);
        System.arraycopy(nb, 0, ab, 1024 * 5, ab.length - 5120);
        FileOutputStream stream = new FileOutputStream(storeDir);
        stream.write(ab);
        stream.close();
        return true;
    }

    @Override
    public byte[] getBytes(int index) throws IOException {
        if (index > 1024 || index < 0) {
            return new byte[0];
        }
        FileInputStream InputStream = new FileInputStream(storeDir);
        byte[] ab = InputStream.readAllBytes();
        byte[] nb = new byte[1024 * 1024 - 1024 * 5];
        if (ab.length - 5120 >= 0) System.arraycopy(ab, 1024 * 5, nb, 0, ab.length - 5120);
        byte[][] blocks = new byte[1024][1024];
        for (int i = 0, j = 0; i < nb.length; i += 1024, j++) {
            blocks[j] = Arrays.copyOfRange(nb, i, i + 1024);
        }
        return blocks[index];
    }

//    @Override
//    public boolean updateBlock(int index, int from, int to , byte[] bytes) throws IOException {
//        byte[] ab = new FileInputStream(storeDir).readAllBytes();
//        byte[] nb = new byte[1024 * 1024 - 1024 * 5];
//        if (ab.length - 5120 >= 0) System.arraycopy(ab, 1024 * 5, nb, 0, ab.length - 5120);
//        byte[][] blocks = new byte[1024][1024];
//        for (int i = 0, j = 0; i < nb.length; i += 1024, j++) blocks[j] = Arrays.copyOfRange(nb, i, i + 1024);
//        for (int i = from; i < from + bytes.length; i++)
//            if (new Directory_Entry(Arrays.copyOfRange(blocks[index], i, i + 32)).getFileName().trim().isBlank()) {
//                Directory_Entry entry = new Directory_Entry(Arrays.copyOfRange(bytes, 0, 32));
//                entry.setFileCluster(getAvailableBlock());
//                System.arraycopy(entry.getBytes(), 0, bytes, 0, 32);
//                if (bytes.length > 1024){
//                    for (int j = 0 , o = getAvailableBlock(); j < bytes.length; j+= 1024 , o++) {
//                        Arrays.copyOfRange(bytes , j ,  j +1024);
//                        System.arraycopy(bytes, 0, blocks[o], from, bytes.length);
//                    }
//                }else{
//                    System.arraycopy(bytes, 0, blocks[index], from, bytes.length);
//                }
//                break;
//            }
//        System.arraycopy(bytes , 0 , blocks[index] , from , bytes.length);
//        System.arraycopy(blocks[index], 0, nb, index * 1024, 1024);
//        System.arraycopy(nb, 0, ab, 1024 * 5, ab.length - 5120);
//        FileOutputStream stream = new FileOutputStream(storeDir);
//        stream.write(ab);
//        stream.close();
//        return true;
//    }
//
//    public boolean DirectSetBlock(int index  , byte[] bytes) throws IOException {
//        byte[] ab = new FileInputStream(storeDir).readAllBytes();
//        byte[] nb = new byte[1024 * 1024 - 1024 * 5];
//        if (ab.length - 5120 >= 0) System.arraycopy(ab, 1024 * 5, nb, 0, ab.length - 5120);
//        byte[][] blocks = new byte[1024][1024];
//        for (int i = 0, j = 0; i < nb.length; i += 1024, j++) blocks[j] = Arrays.copyOfRange(nb, i, i + 1024);
//        System.arraycopy(bytes , 0 , blocks[index] , 0 , 1024);
//        System.arraycopy(blocks[index], 0, nb, index * 1024, 1024);
//        System.arraycopy(nb, 0, ab, 1024 * 5, ab.length - 5120);
//        FileOutputStream stream = new FileOutputStream(storeDir);
//        stream.write(ab);
//        stream.close();
//        return true;
//    }

    private int ConvertToInt(byte b, byte b1, byte b2, byte b3) {
        byte[] bytes = {b, b1, b2, b3};
        return ByteBuffer.wrap(bytes).getInt();
    }

    public void WriteFateTable(int[] fat_table) throws IOException {
        byte[] bytes = new FileInputStream(storeDir).readAllBytes();
        byte[] FatTableIN = Arrays.copyOfRange(bytes, 1024, 1024 * 4);
//                , FatTableOUT = new byte[FatTableIN.length];
        List<Byte> byteList = new LinkedList<>();
        for (int i : fat_table) {
            byte[] inttobytes = ByteBuffer.allocate(4).putInt(i).array();
            for (byte inttobyte : inttobytes) {
                byteList.add(inttobyte);
            }
        }

        byte[] finalBytes = new byte[byteList.size()];
        for (Byte aByte : byteList) {
            finalBytes[byteList.indexOf(aByte)] = aByte;
        }

//        int[] FatTableIntArr = new int[FatTableIN.length / 4];
//
//        int s = 0, l = 0;
//        while (s <= FatTableIntArr.length - 1) {
//            byte[] bytes1 = ByteBuffer.allocate(4).putInt(FatTableIntArr[s]).array();
//            FatTableOUT[l] = bytes1[0];
//            FatTableOUT[l + 1] = bytes1[1];
//            FatTableOUT[l + 2] = bytes1[2];
//            FatTableOUT[l + 3] = bytes1[3];
//            l += 4;
//            s++;
//        }
//        System.arraycopy(FatTableOUT, 0, bytes, 1024, 1024 * 5 - 1024);
        System.arraycopy(finalBytes, 0, bytes, 1024, 1024 * 4);
        FileOutputStream outputStreamOUT = new FileOutputStream(storeDir);
        outputStreamOUT.write(bytes);
        outputStreamOUT.close();
    }
}
