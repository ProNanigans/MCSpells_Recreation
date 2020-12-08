package me.nanigans.potterworldspells.Utils.Config;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PathCreator {

    /**
     * Creates a new file/directory if the specified path is not found
     * @param path the path to create the file or get the file -> needs plugin.datafolder
     * @return the file the path leads to
     * @throws IOException an error when a file fails to create
     */
    public static File createPath(String path) throws IOException {

        File file = new File(path);
        if(!file.exists()) {
            Path paths = Paths.get(path);
            Files.createDirectories(paths);
        }
        return file;

    }

    /**
     * Creates a new file/directory if the specified path is not found
     * @param path the path to create the file or get the file -> needs plugin.datafolder
     * @return the file the path leads to
     * @throws IOException an error when a file fails to create
     */
    public static File createFile(String path) throws IOException{

        File file = new File(path);
        if(!file.exists()) {
            Path paths = Paths.get(path);
            try {
                Files.createFile(paths);
            }catch(IOException e){
                createPath(path.substring(0, path.lastIndexOf("/")));
                Files.createFile(paths);
            }

        }
        return file;

    }

}
