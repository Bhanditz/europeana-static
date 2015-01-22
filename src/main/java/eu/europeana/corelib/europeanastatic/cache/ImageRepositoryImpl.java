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
import java.io.FileFilter;

/**
 *
 * @author Borys Omelayenko
 */

public class ImageRepositoryImpl extends RepositoryImpl implements ImageRepository {

    @Override
    public File findFile(ItemSize itemSize, String uri) {
        final String hash = createHash(uri);
        File file = null;
        if (file == null) {
            file = getFileNewLayout(itemSize, hash);
        }
        if (file == null) {
            file = getFileOldLayoutDirect(itemSize, hash);
        }
//        if (file == null) {
//            file = getFileOldLayout(itemSize, hash);
//        }
        return file;
    }

    @Override
    public File makeFile(ItemSize itemSize, String uri) {
        return makeFile(itemSize, uri, "jpg");
    }

    @Override
    public File makeFile(ItemSize itemSize, String uri, String extention) {
        String hash = createHash(uri);
        String filename = getFilename(hash, Style.FUTURE);
        File cacheRoot = new File(getRoot(), getDirectory(hash, itemSize, Style.FUTURE));
        return new File(cacheRoot, filename + "." + extention);
    }

    @Override
    public File getScriptsDir() {
        File dir =  new File(getRoot(), "scripts");
        if (!dir.exists()) {
            dir.mkdir();
        }
        return dir;
    }

    File getFileNewLayout(ItemSize itemSize, String hash) {
        final String filename = getFilename(hash, Style.FUTURE);
        File cacheRoot = new File(getRoot(), getDirectory(hash, itemSize, Style.FUTURE));

        if (!cacheRoot.exists()) { 
            // this can happen during the transition period
            //throw new RuntimeException("Cache root should already have been created: " + cacheRoot.getAbsolutePath());
            return null;
        }
        File file = new File(cacheRoot, filename + ".jpg");
        return file.exists() ? file : null;
    }

    File getFileOldLayoutDirect(ItemSize itemSize, String hash) {
        final String filename = getFilename(hash, Style.PAST);
        File cacheRoot = new File(getRoot(), getDirectory(hash, itemSize, Style.PAST));
        if (!cacheRoot.exists()) { 
            return null;
        }
        File file = new File(cacheRoot, filename + "." + itemSize + ".jpg");
        if (file.exists()) {
            return file;
        }
        file = new File(cacheRoot, filename + "." + itemSize + ".png");
        if (file.exists()) {
            return file;
        }
        return null;
    }

    File getFileOldLayout(ItemSize itemSize, String hash) {
        final String filename = getFilename(hash, Style.PAST);
        File cacheRoot = new File(getRoot(), getDirectory(hash, itemSize, Style.PAST));
        if (!cacheRoot.exists()) { 
            return null;
        }
        File[] files = cacheRoot.listFiles(new FileFilter() { // todo: can we avoid listing and go directly?

            @Override
            public boolean accept(File pathname) {
                return pathname.getName().startsWith(filename);
            }
        });
        if (files.length == 0) {
            return null;
        }
        else {
            return files[0];
        }
    }

    String getDirectory(String hash, ItemSize itemSize, Style style) {
        switch (style) {
        case PAST:
            return hash.substring(0, 3);
        case FUTURE:
            return getDirectory(itemSize.toString(), hash);
        default:
            throw new RuntimeException();
        }
    }

    String getFilename(String hash, Style style) {
        switch (style) {
        case PAST:
            return hash.substring(3);
        case FUTURE:
            return hash;
        default:
            throw new RuntimeException();
        }
    }
}