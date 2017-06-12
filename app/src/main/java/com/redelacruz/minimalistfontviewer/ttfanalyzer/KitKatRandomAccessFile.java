package com.redelacruz.minimalistfontviewer.ttfanalyzer;

import android.content.Context;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * This class provides a backup solution to obtain random access to a file on a removable
 * drive or emulated storage on KitKat or greater devices.
 */
public class KitKatRandomAccessFile implements TTFInputStream {

    //TODO: Full analysis of this class so we can incorporate it into the TTFAnalyzer.

    private ParcelFileDescriptor pfdInput, pfdOutput;
    private FileInputStream fis;
    private FileOutputStream fos;
    private long position;

    public KitKatRandomAccessFile(Uri treeUri, Context context) throws FileNotFoundException {
        try {
            pfdInput = context.getContentResolver().openFileDescriptor(treeUri, "r");
            pfdOutput = context.getContentResolver().openFileDescriptor(treeUri, "rw");
            fis = new FileInputStream(pfdInput.getFileDescriptor());
            fos = new FileOutputStream(pfdOutput.getFileDescriptor());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public int read(ByteBuffer buffer) {
        try {
            FileChannel fch = fis.getChannel();
            fch.position(position);
            int bytesRead = fch.read(buffer);
            position = fch.position();
            return bytesRead;
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public int write(ByteBuffer buffer) {
        try {
            FileChannel fch = fos.getChannel();
            fch.position(position);
            int bytesWrite = fch.write(buffer);
            position = fch.position();
            return bytesWrite;
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public long position() throws IOException {
        return position;
    }

    public KitKatRandomAccessFile position(long newPosition) throws IOException {
        position = newPosition;
        return this;
    }

    public long size() throws IOException {
        return fis.getChannel().size();
    }

    public void force(boolean metadata) throws IOException {
        fos.getChannel().force(metadata);
        pfdOutput.getFileDescriptor().sync();
    }

    public long truncate(long size) throws Exception {
        FileChannel fch = fos.getChannel();
        try {
            fch.truncate(size);
            return fch.size();
        } catch (Exception e){ // Attention! Truncate is broken on removable SD card of Android 5.0
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public int read(byte[] b) throws IOException {
        return read(ByteBuffer.wrap(b));
    }

    @Override
    public int read() throws IOException {
        return read(new byte[1]);
    }

    @Override
    public void seek(long pos) throws IOException {
        position(pos);
    }

    public void close() throws IOException {
        FileChannel fch = fos.getChannel();
        fch.close();

        fos.close();
        fis.close();
        pfdInput.close();
        pfdOutput.close();
    }
}
