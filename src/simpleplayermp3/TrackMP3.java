package simpleplayermp3;

import java.io.File;
import java.io.Serializable;

public class TrackMP3 implements Serializable{
    
    private final String fileName;
    private final String name;

    public TrackMP3(File file) {
        
        this.fileName = file.getPath();
        this.name = file.getName();
        
    }

    @Override
    public String toString() {
        return getName();
    }

    public String getFileName() {
        return fileName;
    }

    public String getName() {
        return name;
    }
    

}
