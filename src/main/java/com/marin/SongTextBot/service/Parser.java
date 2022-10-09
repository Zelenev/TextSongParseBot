package com.marin.SongTextBot.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class Parser {

    public static Document getPage(String url) throws IOException {
        Document page = Jsoup.parse(new URL(url), 3000);
        return page;
    }

    public static void printText(Elements originalText, Elements translateText, Elements containerText, FileWriter csvWriter) throws IOException {

        ArrayList<String> originalList = new ArrayList<>();

        for (Element originalItem : originalText) {
            originalList.add(originalItem.text());
//            if (originalItem.select("strong").contains(containerText.select("strong").first())){
//                break;
//            }
        }

        ArrayList<String> translateList = new ArrayList<>();

        for (Element translateItem : translateText) {
            translateList.add(translateItem.text());
//            if (translateItem.select("strong[class=few]").equals(containerText.select("strong[class=few]"))){
//                break;
//            }
        }
        printList(originalList, translateList, csvWriter);
    }

    private static void printList(ArrayList<String> originalList, ArrayList<String> translateList, FileWriter csvWriter) throws IOException {

        for (int i = 0; i < originalList.size(); i++) {
            csvWriter.write(originalList.get(i));
            csvWriter.write("\n");
            csvWriter.write(translateList.get(i));
            csvWriter.write("\n");
            csvWriter.write("\n");

        }

        csvWriter.flush();
        csvWriter.close();

    }

    public static File textProcess(String message) throws IOException {

        String url = message;
        String[] fileNameArray = url.split("/");
        int index = fileNameArray.length;
        String fileName = fileNameArray[index - 1];

        Document page = getPage(url);
        Elements containerText = page.select("div[class=string_container]");
        Elements originalText = containerText.select("div[class=original]");
        Elements translateText = containerText.select("div[class=translate]");
        String textFileName = fileName.substring(0, fileName.length()-5) + ".txt";

        FileWriter csvWriter = new FileWriter(textFileName);

        printText(originalText,translateText, containerText, csvWriter);
        File file = ResourceUtils.getFile("C:\\Users\\zelen\\Desktop\\Coding\\Java\\SongTextBot\\"+textFileName);
        System.out.println("Файл успешно создан!");

        return file;
    }
}
