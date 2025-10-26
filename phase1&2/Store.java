/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package student.course.registration.system;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ahlamabudiab
 */
public class Store {
    
    private final Path path;
    public Store(String fileName) {
        Path dirSource = Paths.get(System.getProperty("user.dir"),"data");
        try {
            Files.createDirectories(dirSource);
        } catch (IOException ex) {
            Logger.getLogger(Store.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.path = dirSource.resolve(fileName);
        if(!Files.exists(this.path)) {
            try {
                Files.createFile(this.path);
            } catch (IOException ex) {
                Logger.getLogger(Store.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    } 
    public synchronized List<String> readFile() throws IOException{
        if(!Files.exists(path))
           return new ArrayList<>();
       return Files.readAllLines(path, StandardCharsets.UTF_8);
    }
    public synchronized void writeFile(List<String> lines){
        try {
            Files.write(path, lines, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException ex) {
            Logger.getLogger(Store.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public synchronized void printInFile(String content){
        BufferedWriter buffer = null;
        try {
            buffer = Files.newBufferedWriter(path,StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            buffer.write(content);
            buffer.newLine();
        } catch (IOException ex) {
            Logger.getLogger(Store.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                buffer.close();
            } catch (IOException ex) {
                Logger.getLogger(Store.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
