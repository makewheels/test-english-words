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
        return new File("C:/Users/thedoflin/Downloads/audio");
    }

    public static boolean downloadAndCompareMd5(Set<String> md5List, String url, File file) throws IOException {
        FileUtils.copyURLToFile(new URL(url), file);
        System.out.println(Thread.currentThread().getName() + " download " + url);
        FileInputStream fileInputStream = new FileInputStream(file);
        String md5 = DigestUtils.md5Hex(fileInputStream);
        fileInputStream.close();
        if (md5List.contains(md5)) {
            file.delete();
            return false;
        }
        md5List.add(md5);
        return true;
    }

    public static void downloadAudio(String word) throws IOException {
        Set<String> md5List = new HashSet<>();
        for (int type = 1; type < 20; type++) {
            String url = getAudioDownloadUrl(word, type + "");
            File audioFile = new File(getAudioFolder() + "/" + word.charAt(0) + "/"
                    + word + "/" + word + "-" + type + ".mp3");
            downloadAndCompareMd5(md5List, url, audioFile);
        }
    }

    public static void main(String[] args) {
        List<String> words = readWords();
        ExecutorService executorService = Executors.newFixedThreadPool(1);
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
