package ru.spb.hibissscus.sound;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import javazoom.jl.player.Player;

import javax.swing.*;

import org.apache.http.util.ByteArrayBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

/**
 * Mp3 player in the separately thread.
 */
class SoundWorker extends SwingWorker<Void, Void> {

    /**
     * Logger
     */
    private static final Logger LOG = LoggerFactory
            .getLogger(SoundWorker.class);

    /**
     * Mp3 player
     */
    private Player player;

    /**
     * Sound bytes
     */
    private InputStream bis;

    /**
     * Constructor.
     * 
     * @param inputStream
     *            входной поток
     */
    public SoundWorker(final InputStream inputStream) {
        bis = new BufferedInputStream(inputStream);
    }

    /**
     * Constructor.
     * 
     * @param records
     *            список батовых представлений записей
     * @throws IOException
     *             ошибка сведения дорожек
     */
    public SoundWorker(final List<byte[]> records) throws IOException {
        Preconditions.checkNotNull(records, " Records cannot be empty");
        configure(makeAllRecords(records));
    }

    /**
     * Constructor
     * 
     * @param record
     *            байтовая звуковая дорожка
     * @throws java.io.IOException
     *             ошибка сведения дорожек
     */
    public SoundWorker(final byte[] record) throws IOException {
        Preconditions.checkNotNull(record, " Record cannot be empty");
        configure(record);
    }

    /**
     * Configure sound worker
     * 
     * @param record
     */
    private void configure(final byte[] record) {
        bis = new ByteArrayInputStream(record);
    }

    /**
     * Метод склеивающий все записи воедино
     * 
     * @param records
     *            список звуховых дорожек
     * @return массив байт звукового файла
     * @throws java.io.IOException
     *             тип генерируемого исключения
     */
    private byte[] makeAllRecords(final List<byte[]> records)
            throws IOException {

        int capacity = 0;
        for (byte[] rec : records) {
            capacity = capacity + rec.length;
        }

        ByteArrayBuffer arrayBuffer = new ByteArrayBuffer(capacity);
        for (byte[] rec : records) {
            if (rec.length > 0) {
                arrayBuffer.append(rec, 0, rec.length);
            }
        }
        return arrayBuffer.buffer();
    }

    @Override
    protected Void doInBackground() throws Exception {
        player = new Player(bis);
        player.play();
        return null;
    }

    @Override
    protected void finalize() throws Throwable {
        bis.close();
        super.finalize();
    }

    /**
     * Stop this mp3 player
     */
    public void cancel() {
        try {
            bis.close();
        } catch (IOException e) {
            e.printStackTrace();
            LOG.error("Exception type: {}. message: {}", e.getClass(), e);
        }
        if (player != null)
            player.close();
        this.cancel(true);
    }
}
