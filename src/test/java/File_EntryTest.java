import org.junit.jupiter.api.Test;

import java.io.IOException;

class File_EntryTest {

    @Test
    void readFile() {
    }

    @Test
    void writeFile() throws IOException {
        File_Entry file_entry = new File_Entry("textFile" , Byte.parseByte(Integer.toHexString(0x11))
                ,"kjhfksahdkfhkasdf".length(),1 ,"kjhfksahdkfhkasdf" , MainRoot.getMainRoot());
        file_entry.WriteFile(new VirtualDiskImpl());
        System.out.println(Integer.toOctalString(0x11));
        file_entry.DeleteFile(new VirtualDiskImpl());
    }

    @Test
    void deleteFile() {
    }
}