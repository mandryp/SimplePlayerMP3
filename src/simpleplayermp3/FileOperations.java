/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simpleplayermp3;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;

/**
 *
 * @author ap
 */
public class FileOperations {

    public static void readListFromFile(File file, DefaultListModel lm) {

        ObjectInputStream ois;
        try {
            ois = new ObjectInputStream(new FileInputStream(file));
            Object[] list = (Object[]) ois.readObject();
            if (list.length > 0) {
                if (!lm.isEmpty()) {
                    int op = JOptionPane.showConfirmDialog(null, "Текущий плейлист не пуст. Заменить?");
                    if (op == 0) {
                        lm.clear();
                        for (Object list1 : list) {
                            lm.addElement(list1);
                        }
                    }
                } else {
                    for (Object list1 : list) {
                        lm.addElement(list1);
                    }
                }

            }
            ois.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FileOperations.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(FileOperations.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void saveListToFile(File file, DefaultListModel lm) {

        try {
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
                oos.writeObject(lm.toArray());
                oos.flush();

            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FileOperations.class
                    .getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FileOperations.class
                    .getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void addFolderToList(File folder, DefaultListModel dlm) {

        File[] folderFiles = folder.listFiles();
        for (File folderFile : folderFiles) {
            FileOperations.addFileToList(folderFile, dlm);
        }

    }

    public static void addFileToList(File file, DefaultListModel dlm) {

        if (file.isDirectory()) {
            FileOperations.addFolderToList(file, dlm);
        } else if (file.getName().endsWith(".mp3") && !dlm.contains(file.getName())) {
            dlm.addElement(new TrackMP3(file));
        }

    }

}
