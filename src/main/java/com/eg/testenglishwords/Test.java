package com.eg.testenglishwords;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Test {

    public static String getAudioDownloadUrl(String word, String type) {
        return "https://dict.youdao.com/dictvoice?audio=" + word + "&type=" + type;
    }

    public static File getWordsFile() {
        String basePath = Test.class.getResource("/").getPath();
        return new File(basePath, "words_alpha.txt");
    }

    public static List<String> readWords() {
        List<String> words = null;
        try {
            words = FileUtils.readLines(getWordsFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return words;
    }

    public static File getAudioFolder() {
        return new File("C:/Users/thedoflin/Downloads/youdao");
    }

    public static String downloadAndCompareMd5(Set<String> md5List, String url, File file)
            throws IOException {
        try {
            FileUtils.copyURLToFile(new URL(url), file);
        } catch (IOException e1) {
            e1.printStackTrace();
            try {
                FileUtils.copyURLToFile(new URL(url), file);
            } catch (IOException e2) {
                e2.printStackTrace();
                FileUtils.copyURLToFile(new URL(url), file);
            }
        }
        System.out.println(Thread.currentThread().getName() + " " + url);
        FileInputStream fileInputStream = new FileInputStream(file);
        String md5 = DigestUtils.md5Hex(fileInputStream);
        fileInputStream.close();
        if (md5List.contains(md5)) {
            boolean deleteResult = file.delete();
            System.out.println("file.delete() " + file.getName() + " " + deleteResult);
        }
        md5List.add(md5);
        return md5;
    }

    public static void downloadAudio(String word) throws IOException {
        Set<String> md5List = new HashSet<>();
        for (int type = 1; type <= 2; type++) {
            String url = getAudioDownloadUrl(word, type + "");

            String which;
            if (type == 1)
                which = "uk";
            else which = "us";

            File audioFile = new File(getAudioFolder() + "/" + word.charAt(0) + "/"
                    + word + "/" + which + "/" + word + ".mp3");
            String md5 = downloadAndCompareMd5(md5List, url, audioFile);
            File renameFile = new File(audioFile.getParent(), md5 + ".mp3");
            audioFile.renameTo(renameFile);
            System.out.println(renameFile);
        }
    }

    //    https://fanyi.baidu.com/gettts?lan=en&text=abstract&spd=3&source=web
    public static void main(String[] args) {
        List<String> words = readWords();
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        words.forEach(word -> executorService.submit(() -> {
            try {
                downloadAudio(word);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));
        executorService.shutdown();
    }

}
