package eu.europeana.corelib.europeanastatic.controllers;
/*
 * Copyright 2007 EDL FOUNDATION
 *
 * Licensed under the EUPL, Version 1.1 or - as soon they
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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import eu.europeana.corelib.definitions.solr.DocType;
import eu.europeana.corelib.europeanastatic.cache.ImageCache;
import eu.europeana.corelib.europeanastatic.cache.ImageInfo;
import eu.europeana.corelib.europeanastatic.cache.ItemSize;
import eu.europeana.corelib.europeanastatic.cache.MimeType;

/**
 * Controller for image cache.
 *
 * @author Gerald de Jong, Beautiful Code BV, <geralddejong@gmail.com>
 * @author Borys Omelayenko
 */


@Controller
public class ImageController {
    private Logger log = Logger.getLogger(getClass());
    private static final String DEFAULT_IMAGE = "/cache/unknown.png";
    private static final String[][] TYPES = {
        {ItemSize.TINY.toString(), DocType.TEXT.toString(), "/cache/item-text-tiny.gif"},
        {ItemSize.TINY.toString(), DocType.IMAGE.toString(), "/cache/item-image-tiny.gif"},
        {ItemSize.TINY.toString(), DocType.SOUND.toString(), "/cache/item-sound-tiny.gif"},
        {ItemSize.TINY.toString(), DocType.VIDEO.toString(), "/cache/item-video-tiny.gif"},
        {ItemSize.TINY.toString(), DocType._3D.toString(), "/cache/item-3d-tiny.gif"},
        {ItemSize.BRIEF_DOC.toString(), DocType.TEXT.toString(), "/cache/item-text.gif"},
        {ItemSize.BRIEF_DOC.toString(), DocType.IMAGE.toString(), "/cache/item-image.gif"},
        {ItemSize.BRIEF_DOC.toString(), DocType.SOUND.toString(), "/cache/item-sound.gif"},
        {ItemSize.BRIEF_DOC.toString(), DocType.VIDEO.toString(), "/cache/item-video.gif"},
        {ItemSize.BRIEF_DOC.toString(), DocType._3D.toString(), "/cache/item-3d.gif"},
        {ItemSize.FULL_DOC.toString(), DocType.TEXT.toString(), "/cache/item-text-large.gif"},
        {ItemSize.FULL_DOC.toString(), DocType.IMAGE.toString(), "/cache/item-image-large.gif"},
        {ItemSize.FULL_DOC.toString(), DocType.SOUND.toString(), "/cache/item-sound-large.gif"},
        {ItemSize.FULL_DOC.toString(), DocType.VIDEO.toString(), "/cache/item-video-large.gif"},
        {ItemSize.FULL_DOC.toString(), DocType._3D.toString(), "/cache/item-3d-large.gif"}
    };
    private static final int CACHE_DURATION_IN_SECOND = 60 * 60 * 24; // 24 hours

    @Resource
    private ImageCache imageCache;

    @RequestMapping("/image")
    public void imageController(
            HttpServletResponse response,
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "uri", required = false) String uri,
            @RequestParam(value = "size", required = false) String sizeString,
            @RequestParam(value = "show", required = false, defaultValue = "image") String whatToShow
    ) throws IOException {
        ItemSize itemSize = getItemSize(sizeString);
        if (uri == null) {
            log.warn("Needs 'uri' parameter");
            reportURIRequired(response, type, itemSize);
        } else {
            File cachedFile = findCachedImageFile(uri, itemSize);
            if ("debug".equals(whatToShow)) {
                respondWithDebug(response, cachedFile);
            } else {
                if (cachedFile == null) {
                    reportURINotCached(response, uri, type, itemSize, "Nothing in cache for this URI.");
                } else {
                    if ("info".equals(whatToShow)) {
                        respondWithInfo(response, cachedFile);
                    } else {
                        if (!respondWithImage(response, cachedFile)) {
                            reportURINotCached(response, uri, type, itemSize, "Cacheing of this URI resulted in an error.");
                        }
                    }
                }
            }
        }
    }

    ItemSize getItemSize(String sizeString) {
        ItemSize itemSize = ItemSize.FULL_DOC;
        if (sizeString != null) {
            try {
                itemSize = ItemSize.valueOf(sizeString);
            }
            catch (IllegalArgumentException e) {
                log.warn("Parameter 'size' is invalid");
                itemSize = ItemSize.FULL_DOC;
            }
        }
        else {
            log.warn("No 'size' parameter, using " + itemSize);
        }
        return itemSize;
    }

    File findCachedImageFile(String uri, ItemSize itemSize) throws UnsupportedEncodingException {
        String decodedUri = URLDecoder.decode(uri, "UTF-8");
        return imageCache.getFile(itemSize, decodedUri);
    }


    private boolean respondWithImage(HttpServletResponse response, File cachedFile) throws IOException {
        MimeType mimeType = imageCache.getMimeType(cachedFile);
        if (mimeType == MimeType.ERROR) {
            return false;
        }
        response.setContentType(mimeType.getType());
        setImageCacheControlHeaders(response);
        OutputStream out = response.getOutputStream();
        InputStream in = new FileInputStream(cachedFile);
        try {
            IOUtils.copy(in, out);
        } finally {
            in.close();
            out.close();
        }
        return true;
    }

    private void respondWithInfo(HttpServletResponse response, File cachedFile) throws IOException {

        ImageInfo imageInfo = new ImageInfo();
        MimeType mimeType = imageCache.getMimeType(cachedFile);
        if (mimeType == MimeType.JPG || mimeType == MimeType.JPEG || mimeType == MimeType.PNG) {
            imageInfo = imageCache.fetchImageInfo(cachedFile);
        }

        response.setContentType("text/plain");
        PrintWriter out = response.getWriter();
        try {
            out.println(imageInfo.serialize());
        } finally {
            out.close();
        }
    }

    private void respondWithDebug(HttpServletResponse response, File cachedFile) throws IOException {

        response.setContentType("text/plain");
        PrintWriter out = response.getWriter();
        try {
            if (cachedFile == null) {
                out.println("Image file was not copied to the image repository");
            } else {
                out.println("Local file: " + cachedFile.getCanonicalPath());
                if (cachedFile.exists()) {
                    out.println("Exists");
                    if (cachedFile.canRead()) {
                        out.println("Can be read");
                        out.println("Size: " + cachedFile.length());
                    } else {
                        out.println("Cannot be read - permissions are wrong");
                    }
                } else {
                    out.println("Does not exist");
                }
                out.println("And by the way, there are " + (cachedFile.getFreeSpace() / 1024 / 1024) + " Mb left on the partition where images are stored");
            }
        } finally {
            out.close();
        }
    }

    private void reportURIRequired(HttpServletResponse response, String type, ItemSize size) throws IOException {
        if (type == null) {
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            try {
                out.println("<html><head><title>Cache Servlet</title></head><body>");
                out.println("<h1>URI parameter required!</h1>");
                out.println("</body></html>");
            } finally {
                out.close();
            }
        }
        else {
            respondWithDefaultImage(response, type, size);
        }
    }

    private void reportURINotCached(HttpServletResponse response, String uri, String type, ItemSize size, String reason) throws IOException {
        if (type == null) {
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            try {
                out.println("<html><head><title>Cache Servlet</title></head><body>");
                out.println("<h1>URI not cached!</h1>");
                out.println("<pre>");
                out.println(uri);
                out.println("</pre>");
                out.println("<font color='red'>");
                out.println("Reason: " + reason);
                out.println("</font>");
                out.println("</body></html>");
            } finally {
                out.close();
            }
        }
        else {
            respondWithDefaultImage(response, type, size);
        }
    }

    private void respondWithDefaultImage(HttpServletResponse response, String type, ItemSize size) throws IOException {
        response.setContentType("image/png");
        setImageCacheControlHeaders(response);
        String resource = DEFAULT_IMAGE;
        for (String[] array : TYPES) {
            if (array[0].equals(size.toString()) && array[1].equalsIgnoreCase(type)) {
                resource = array[2];
            }
        }
        InputStream in = null;
        OutputStream out = null;
        try {
            in = getClass().getResourceAsStream(resource);
            out = response.getOutputStream();
            IOUtils.copy(in, out);
        } finally {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
        }
    }

    /* Sets the HTTP header that instruct cache control to the browser. */

    private void setImageCacheControlHeaders(HttpServletResponse response) {
        long now = System.currentTimeMillis();
        response.addHeader("Cache-Control", "max-age=" + CACHE_DURATION_IN_SECOND);
        response.addHeader("Cache-Control", "must-revalidate"); //optional
        response.setDateHeader("Last-Modified", now);
        response.setDateHeader("Expires", now + CACHE_DURATION_IN_SECOND * 1000);
    }
}