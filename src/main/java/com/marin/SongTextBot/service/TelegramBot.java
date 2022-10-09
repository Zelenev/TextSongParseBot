package com.marin.SongTextBot.service;

import com.marin.SongTextBot.config.BotConfig;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

@Component
public class TelegramBot extends TelegramLongPollingBot {

    final BotConfig config;

    public TelegramBot(BotConfig config) {
        this.config = config;
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()){
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            switch (update.getMessage().getText()){
                case "/start":
                    sendMessage(chatId, "Добро пожаловать, " + update.getMessage().getChat().getFirstName());
                    try {
                        deleteMessageCommand(chatId, update);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                    break;
                case "/help":
                    sendMessage(chatId, "Отправь мне ссылку, и я пришлю файл. Вот и все. Ну и что?");
                    try {
                        deleteMessageCommand(chatId, update);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    if (update.getMessage().getText().contains(".html")){
                        try {
                            startCommandReceived(chatId, update.getMessage().getChat().getFirstName(), messageText, update);
                            try {
                                deleteMessageCommand(chatId, update);
                            } catch (TelegramApiException e) {
                                e.printStackTrace();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                    } else {
                        sendMessage(chatId, "Я тебя не понимаю. /help, чтобы получить помощь!");
                        try {
                            deleteMessageCommand(chatId, update);
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
            }
        }
    }

    private void startCommandReceived(long chatId, String name, String text, Update update) throws IOException, TelegramApiException {
        Parser parser = new Parser();
        File document = parser.textProcess(text);

        sendDocument(chatId, document);

    }

    private void deleteMessageCommand(long chatId,Update update ) throws TelegramApiException {
        DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setChatId(String.valueOf(update.getMessage().getChatId()));
        deleteMessage.setMessageId(update.getMessage().getMessageId());
        execute(deleteMessage);
        deleteMessage.setMessageId(update.getMessage().getMessageId() - 1);
        execute(deleteMessage);
    }

    @SneakyThrows
    private void sendMessage(long chatId, String sendText){
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(sendText);
        execute(message);
        message.getReplyToMessageId();

    }

    @SneakyThrows
    public void sendDocument(long chatId, File sendFile) {
        SendDocument sendDocument = new SendDocument();
        sendDocument.setChatId(String.valueOf(chatId));
        sendDocument.setDocument(new InputFile(sendFile));
        execute(sendDocument);
    }
}
