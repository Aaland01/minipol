package monopol;

import java.io.FileNotFoundException;
import java.io.IOException;

public interface IFsaveGame {
    
    void saveToFile(String file) throws IOException, FileNotFoundException;

    void loadFromFile(String file) throws IOException, FileNotFoundException;

}
