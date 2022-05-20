import Models.Directory_Entry;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static java.util.Arrays.asList;

class DirectoryTest {

//    private SimpleDateFormat format = new SimpleDateFormat("EEE dd/MM hh:mm");
//    private Date
    @Test
    void writeDirectory() throws IOException {
        Fat fat = new Fat();
        fat.Write_Fat_Table();

        Directory_Entry entry = new Directory_Entry("newFolder", (byte) 0x11, 5 , 0);
        Directory parent = new Directory("root" , (byte) '+', 100 , 5 , new ArrayList<>() , null);
        Directory directory = new Directory(
                new ArrayList<>(asList(
                entry ,
                new Directory_Entry("user", (byte) 0x11, 1 , 0),
                new Directory_Entry("document", (byte) 0x11, 1 , 0),
                new Directory_Entry("desktop", (byte) 0x11, 1 , 0),
                new Directory_Entry("recycle bin", (byte) 0x11, 1 , 0)
        )) , parent);
        directory.WriteDirectory(fat);
        System.out.println(directory.getEntries().size());
        directory.ReadDirectory(new VirtualDiskImpl());
        System.out.println(directory.getEntries());
    }

    @Test
    void readDirectory() throws IOException {
//        Directory directory = new Directory(new ArrayList<>() , new Directory_Entry("root" , (byte) 0x11, 100 , 1));
//        directory.ReadDirectory(new VirtualDiskImpl());
//        System.out.println(directory.getEntries());
    }
}