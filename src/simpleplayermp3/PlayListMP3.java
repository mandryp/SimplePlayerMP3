package simpleplayermp3;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.filechooser.FileNameExtensionFilter;

public class PlayListMP3 {

    private final DefaultListModel myListModel;
    private final JList myList;
    private TrackMP3 curTrack;
    private final SimplePlayerMP3 curPlay;
    private SimplePlayerMP3 prevPlay;
    private final JLabel jlStatus;

    public PlayListMP3() {

        curPlay = new SimplePlayerMP3();

//      основной фрейм
        JFrame frame = new JFrame("Плеер MP3");
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

//      сохраним временный список при закрытии
        frame.addWindowListener(new MyWindowListener());
        frame.setSize(600, 300);
        JPanel jp = new JPanel(new BorderLayout());

//      Меню
        JMenuBar jm = new JMenuBar();
        JMenu jmFile = new JMenu("Файл");
        ImageIcon iconOpen = new javax.swing.ImageIcon(getClass().getResource("/icons/open.png"));
        ImageIcon iconSave = new javax.swing.ImageIcon(getClass().getResource("/icons/save.png"));
        ImageIcon iconAdd = new javax.swing.ImageIcon(getClass().getResource("/icons/add.png"));
        ImageIcon iconDel = new javax.swing.ImageIcon(getClass().getResource("/icons/del.png"));
        ImageIcon iconPlay = new javax.swing.ImageIcon(getClass().getResource("/icons/play.png"));
        ImageIcon iconStop = new javax.swing.ImageIcon(getClass().getResource("/icons/stop.png"));

        jmFile.add(setPlMenuItem("Открыть список", "open", iconOpen));
        jmFile.add(setPlMenuItem("Сохранить список", "save", iconSave));
        jmFile.add(setPlMenuItem("Добавить треки", "add", iconAdd));
        jmFile.add(setPlMenuItem("Удалить треки", "remove", iconDel));
        jm.add(jmFile);

//      Создаем контекстное меню
        JPopupMenu popupMenu = new JPopupMenu();
        popupMenu.add(setPlMenuItem("Играть трек", "play", iconPlay));
        popupMenu.add(setPlMenuItem("Остановить", "stop", iconStop));

//      Строка состояния        
        jlStatus = new JLabel("     ");

//      список плейлист
        myListModel = new DefaultListModel();
        myList = new JList(myListModel);
        myList.setComponentPopupMenu(popupMenu);
        myList.addMouseListener(new MyMouseListener());
        JScrollPane jsp = new JScrollPane(myList);

        jp.add(jm, BorderLayout.NORTH);
        jp.add(jsp, BorderLayout.CENTER);
        jp.add(jlStatus, BorderLayout.SOUTH);
        frame.getContentPane().add(jp);

        String userDir = System.getProperty("user.dir");
        File tmpFile = new File(userDir + File.separator + "tmp.pls");
        if (tmpFile.exists()) {
            FileOperations.readListFromFile(tmpFile, myListModel);
        }

        frame.setVisible(true);
    }

    public void setStatusText(String text) {
        jlStatus.setText(text);
    }

    public DefaultListModel getMyListModel() {
        return myListModel;
    }

    public JList getMyList() {
        return myList;
    }

    public TrackMP3 getCurTrack() {
        return curTrack;
    }

    public void setPlayCurTrack(int Ind) {
        myList.setSelectedIndex(Ind);
        curTrack = (TrackMP3) myListModel.getElementAt(Ind);
        curPlay.play();
    }

    private JMenuItem setPlMenuItem(String text, String actionCommand, ImageIcon icon) {
        JMenuItem mni = new JMenuItem(text, icon);
        mni.setActionCommand(actionCommand);
        mni.addActionListener(new MenuActionListener());
        return mni;
    }

    private class MenuActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setAcceptAllFileFilterUsed(false);
            switch (e.getActionCommand()) {
                case "play": {
                    curTrack = (TrackMP3) myListModel.getElementAt(myList.getSelectedIndex());
                    curPlay.play();

                    break;
                }
                case "stop": {
                    if (curPlay != null) {
                        curPlay.stop();
                    }
                    break;
                }
                case "open": {
                    fileChooser.setFileFilter(new FileNameExtensionFilter("Список треков, *.pls", "pls"));
                    int showOpenDialog = fileChooser.showOpenDialog(null);
                    if (showOpenDialog == JFileChooser.APPROVE_OPTION) {
                        File file = fileChooser.getSelectedFile();
                        FileOperations.readListFromFile(file, myListModel);
                    }
                    break;
                }
                case "save": {
                    fileChooser.setFileFilter(new FileNameExtensionFilter("Список треков, *.pls", "pls"));
                    int showSaveDialog = fileChooser.showSaveDialog(null);
                    if (showSaveDialog == JFileChooser.APPROVE_OPTION) {
                        File file = fileChooser.getSelectedFile();
                        if (!file.getPath().endsWith(".pls")) {
                            file = new File(file.getPath() + ".pls");
                        }
                        FileOperations.saveListToFile(file, myListModel);
                    }
                    break;
                }
                case "add": {
                    FileNameExtensionFilter filter = new FileNameExtensionFilter("Tracks mp3", "mp3");
                    System.out.println("1");
                    fileChooser.setFileFilter(filter);
                    fileChooser.setMultiSelectionEnabled(true);
                    fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                    int showOpenDialog = fileChooser.showOpenDialog(null);
                    if (showOpenDialog == JFileChooser.APPROVE_OPTION) {
                        File[] files = fileChooser.getSelectedFiles();
                        for (File file1 : files) {
                            File file = file1;
                            FileOperations.addFileToList(file, myListModel);
                        }
                    }
                    break;
                }
                case "remove": {
                    int[] selInds = myList.getSelectedIndices();
                    for (int i = 0; i < selInds.length; i++) {
                        int selInd = selInds[i];
                        myListModel.remove(selInd - i);
                    }
                    break;
                }

            }
        }

    }

    private class MyMouseListener extends MouseAdapter {

        @Override
        public void mouseReleased(MouseEvent e) {
            if (e.getButton() == 3) {
                getTrackFromList(e);
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
            if (e.getButton() == 3) {
                getTrackFromList(e);
            }
        }

        public void getTrackFromList(MouseEvent e) {

            int i = myList.locationToIndex(e.getPoint());
            myList.setSelectedIndex(i);

        }

        @Override
        public void mouseClicked(MouseEvent e) {

//          запуск по двойному клику
            if (e.getClickCount() == 2) {
                curPlay.stop();
                int i = myList.locationToIndex(e.getPoint());

                if (i >= 0) {
                    curTrack = (TrackMP3) myListModel.getElementAt(i);
                    curPlay.play();
                }
            }

        }
    }

    private class MyWindowListener extends WindowAdapter {

        @Override
        public void windowClosing(WindowEvent e) {
            int op = JOptionPane.showConfirmDialog(null, "Закрыть плеер?");
            if (op == 0) {
                curPlay.stop();
                e.getWindow().setVisible(false);
                String userDir = System.getProperty("user.dir");
                File tmpFile = new File(userDir + File.separator + "tmp.pls");

                if (myListModel.isEmpty()) {
                    if (tmpFile.exists()) {
                        tmpFile.delete();
                    }
                    System.exit(0);
                }

                if (!tmpFile.exists()) {
                    try {
                        tmpFile.createNewFile();
                    } catch (IOException ex) {
                        Logger.getLogger(PlayListMP3.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                FileOperations.saveListToFile(tmpFile, myListModel);
                System.exit(0);
            }
        }
    }
}
