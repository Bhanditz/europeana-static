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


import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.imageio.ImageIO;

import org.apache.log4j.Logger;

/**
 * Cache digital objects from local files.
 *
 * @author Borys Omelayenko
 * @author Gerald de Jong <geralddejong@gmail.com>
 */

public class ImageCacheImpl implements ImageCache {

    private Logger log = Logger.getLogger(getClass());

	@Resource
	private ImageRepository imageRepository;

	private Map<String, MimeType> extensionMap = new HashMap<String, MimeType>();
	private Map<String, MimeType> mimeMap = new HashMap<String, MimeType>();

	public ImageCacheImpl() {
		for (MimeType t : MimeType.values()) {
			mimeMap.put(t.getType(), t);
			extensionMap.put(t.getExtension(), t);
		}
	}

	public void setImageRepository(ImageRepository imageRepository) {
		this.imageRepository = imageRepository;
		if (!imageRepository.canRead()) {
			log.error("Repository is not there or cannot be accessed at " + imageRepository);
		}
	}

	@Override
	public File getFile(ItemSize itemSize, String uri) {
		return imageRepository.findFile(itemSize, uri);
	}

	@Override
    public ImageInfo fetchImageInfo(File file) {

        BufferedImage image = null;
        try {
            image = ImageIO.read(file);
            return new ImageInfo(image.getWidth(), image.getHeight());
        } catch (Exception e) {
            log.warn(e);
            return new ImageInfo();
        }
    }

	@Override
	public MimeType getMimeType(File cachedFile) {
		int dot = cachedFile.getName().lastIndexOf(".");
		String extension = cachedFile.getName().substring(dot);
		MimeType mt = extensionMap.get(extension);
		if (mt == null) {
			return MimeType.ERROR;
		}
		else {
			return mt;
		}
	}

}
