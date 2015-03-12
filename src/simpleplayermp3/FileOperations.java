/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simpleplayermp3;

import javax.swing.*;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

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
                        for (Object list1 : list) lm.addElement(list1);
                    }
                } else {
                    for (Object list1 : list) lm.addElement(list1);
                }

            }
            ois.close();
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
        } catch (IOException ex) {
            Logger.getLogger(FileOperations.class
                    .getName()).log(Level.SEVERE, null, ex);
        }

    }

    private static void addFolderToList(File folder, DefaultListModel dlm) {

        File[] folderFiles = folder.listFiles();
        if (folderFiles != null) {
            for (File folderFile : folderFiles) {
                FileOperations.addFileToList(folderFile, dlm);
            }
        }

    }

    public static void addFileToList(File file, DefaultListModel dlm) {

        if (file.isDirectory()) {
            FileOperations.addFolderToList(file, dlm);
        }
        else if (file.getName().endsWith(".mp3") && !dlm.contains(file.getName())) {
            dlm.addElement(new TrackMP3(file));
        }

    }

}
