package ru.spb.hibissscus.sound;

import java.io.IOException;
import java.util.List;

/**
 * MP3 player.
 */
public class SoundMaster {

    /**
     * INSTANCE.
     */
    private static SoundMaster instance;
    /**
     * Mp3 player in the separately thread.
     */
    private SoundWorker soundWorker;

    /**
     * Constructor.
     *
     * @return SoundMaster instance
     */
    public static SoundMaster getInstance() {
        if (instance == null) {
            instance = new SoundMaster();
        }
        return instance;
    }

    /**
     * Метод проигрывающий байтовый звуковой файл
     *
     * @param record звуковая дорожка в байтах
     * @throws java.io.IOException тип генерируемого исключения
     */
    public void playRecord(final byte[] record) throws IOException {
        stopPlayAny();
        soundWorker = new SoundWorker(record);
        soundWorker.execute();
    }

    /**
     * Метод проигрывающий набор байтовых звуковых файлов
     *
     * @param records список звуховых дорожек
     * @throws java.io.IOException тип генерируемого исключения
     */
    public void playAllRecords(final List<byte[]> records) throws IOException {
        stopPlayAny();
        soundWorker = new SoundWorker(records);
        soundWorker.execute();
    }

    /**
     * Stop any sound
     */
    public void stopPlayAny() {
        if (soundWorker != null) {
            soundWorker.cancel();
        }
    }

}
