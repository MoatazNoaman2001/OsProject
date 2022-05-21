import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Fat {
    private int[] FAT_TABLE =  new int[1024];
    private VirtualDiskImpl disk;


    public Fat() throws IOException {
        disk = new VirtualDiskImpl();
        ReadFatTable();
    }

    public VirtualDiskImpl getDisk() {
        return disk;
    }

    public void Write_Fat_Table() throws IOException {
        disk.WriteFateTable(FAT_TABLE);
    }

    public void ReadFatTable() throws IOException {
        for (int i = 0; i < FAT_TABLE.length; i++) {
            FAT_TABLE[i] = disk.getNext(i);
        }
    }

    public int getAvailableBlocks() throws IOException {
        return (int) Arrays.stream(Arrays.copyOfRange(FAT_TABLE , 5 , FAT_TABLE.length)).filter(value -> value == '*').count();
    }

    public int[] get_Fat_Table(){
        return FAT_TABLE;
    }


    public byte[] getFatTableBytes() throws IOException {
        return disk.getBytes();
    }

    public int getAvailableBlock() throws IOException {
        for (int i = 5; i < FAT_TABLE.length; i++) {
            if (FAT_TABLE[i] ==  '*') return i;
        }
        return -1;
    }

    private int getNext(int index) throws IOException {
        return FAT_TABLE[index];
    }
    private void setNext(int index , int value) throws IOException {
        disk.SetNext(index , value);
        FAT_TABLE[index] = value;
    }

    public void setAvailableBlock(int index , int value) {
        FAT_TABLE[index] = value;
    }
}
