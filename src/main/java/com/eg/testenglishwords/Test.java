package com.eg.testenglishwords;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Test {

    public static String getAudioDownloadUrl(String word, String type) {
        return "https://dict.youdao.com/dictvoice?audio=" + word + "&type=" + type;
    }

    public static String getAudioDownloadUrl(String word) {
        return "https://dict.youdao.com/dictvoice?audio=" + word + "&type=2";
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
        return new File("C:\\Users\\thedoflin\\Downloads\\audio");
    }

    public static void downloadAudio(String word) {
        String url = getAudioDownloadUrl(word);
        char c = word.charAt(0);
        File audioFile = new File(getAudioFolder() + File.separator + c, word + ".mp3");
        try {
            FileUtils.copyURLToFile(new URL(url), audioFile);
            System.out.println(Thread.currentThread().getName() + "\t" + word);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        List<String> words = readWords();
        ExecutorService executorService = Executors.newFixedThreadPool(40);
        words.forEach(word -> executorService.submit(() -> downloadAudio(word)));
        executorService.shutdown();
    }

}
