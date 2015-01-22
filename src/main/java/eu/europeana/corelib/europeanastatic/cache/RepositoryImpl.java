package eu.europeana.corelib.europeanastatic.cache;

/*
 * Copyright 2007 EDL FOUNDATION
 *
 * Licensed under the EUPL, Version 1.0 or - as soon they
 * will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 * you may not use this work except in compliance with the
 * Licence.
 * You may obtain a copy of the Licence at:
 *
 * http://ec.europa.eu/idabc/eupl
 *
 * Unless required by applicable law or agreed to in
 * writing, software distributed under the Licence is
 * distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.
 * See the Licence for the specific language governing
 * permissions and limitations under the Licence.
 */


import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

/**
 * Managing local directory with images.
 *
 * @author Borys Omelayenko
 */

public class RepositoryImpl implements Repository {

    protected static final String SLASH = File.separator;
    protected static final String[] HEX = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F"};

    private final MessageDigest digest;

    private File root;

    public RepositoryImpl() {
        try {
            digest = MessageDigest.getInstance("SHA-256");
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace(System.err);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setRoot(File root) {
        this.root = root;
    }

    @Override
    public File getRoot() {
        return root;
    }

    @Override
    public int createCacheDirectories(String prefix) throws IOException {
        if (!getRoot().exists()) {
            return -1;
        } else {  
            getRoot().mkdirs();
            int count = 0;
            for (String dirAB : makeHexLetterPairs()) {
                for (String dirCD : makeHexLetterPairs()) {
                    String directoryString = String.format("%s%s%s", dirAB, SLASH, dirCD);
                    FileUtils.forceMkdir(new File(getRoot(), String.format("%s%s%s", prefix, SLASH, directoryString)));
                    count++;
                }
                count++;
            }
            return count;
        }
    }

    @Override
    public boolean canRead() {
        return root.exists() && root.isDirectory() && root.canRead();
    }

    @Override
    public boolean canWrite() {
        return root.exists() && root.isDirectory() && root.canWrite();
    }

    @Override
    public String toString() {
        return root.getPath();
    }

    String createHash(String uri) {
        byte[] raw;
        synchronized (digest) {
            digest.reset();
            try {
                raw = digest.digest(uri.getBytes("UTF-8"));
            }
            catch (UnsupportedEncodingException e) {
                e.printStackTrace(System.err);
                throw new RuntimeException(e);
            }
        }
        char[] hex = new char[2 * raw.length];
        int index = 0;
        for (byte b : raw) {
            int v = b & 0xFF;
            hex[index++] = HEX[v >>> 4].charAt(0);
            hex[index++] = HEX[v & 0xF].charAt(0);
        }
        return new String(hex);
    }

    protected String getDirectory(String prefix, String hash) {
        return prefix + SLASH + hash.substring(0, 2) + SLASH + hash.substring(2, 4);
    }

    public static List<String> makeHexLetterPairs() {
        List<String> hexLetterPairs = new ArrayList<String>();
        for (String dirA : HEX) {
            for (String dirB : HEX) {
                hexLetterPairs.add(dirA + dirB);
            }
        }
        return hexLetterPairs;
    }

    public static List<String> makeHexLetters() {
        List<String> hexLetters = new ArrayList<String>();
        for (String dirA : HEX) {
            hexLetters.add(dirA);
        }
        return hexLetters;
    }

}
