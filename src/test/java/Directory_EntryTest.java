import Models.Directory_Entry;
import org.junit.jupiter.api.Test;

import java.io.IOException;

class Directory_EntryTest {

    @Test
    void getBytes() throws IOException {
        Fat fat = new Fat();
        fat.Write_Fat_Table();
        Directory_Entry entry = new Directory_Entry(new Directory_Entry("newFilekjafshdkfh.txt", (byte) 0x01, 4, 45).getBytes());
        System.out.println(entry);

    }
}