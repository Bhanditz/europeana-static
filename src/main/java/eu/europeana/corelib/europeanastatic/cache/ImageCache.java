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

/**
 * Cache digital objects from local files
 *
 * @author Borys Omelayenko
 * @author Gerald de Jong <geralddejong@gmail.com>
 */

public interface ImageCache {

    /**
     * Get a file object which will lead to the proper cached file
     *
     * @param itemSize which item size do you want
     * @param uri where this thing was fetched
     * @return a file object
     */

    File getFile(ItemSize itemSize, String uri);

    /**
     * Discover what mime time is represented by the cached file
     *
     * @param cachedFile which file
     * @return what mime time is it.
     */

    MimeType getMimeType(File cachedFile);

    /**
     * Fetches image info: dimensions.
     */

    ImageInfo fetchImageInfo(File file);

}
