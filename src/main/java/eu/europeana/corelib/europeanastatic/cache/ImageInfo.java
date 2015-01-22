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


import java.io.IOException;

import org.apache.commons.lang.StringUtils;

/**
 * Image dimensions.
 *
 * @author Borys Omelayenko
 */

public class ImageInfo {

    private static final String DIMENSIONS = "Dimensions: ";
    int width = -1;
    int height = -1;

    public boolean hasDimentions() {
        return width != -1 && height != -1;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public ImageInfo(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public ImageInfo() {
        this(-1, -1);
    }

    public String serialize() throws IOException {
        return DIMENSIONS + width + "," + height;
    }

    public boolean deserialize(String in) throws IOException {
        if (StringUtils.isEmpty(in)) {
            return fail(in);
        }
        if (in.startsWith(DIMENSIONS)) {
            String[] dimensions = in.substring(DIMENSIONS.length()).trim().split(",");
            if (dimensions.length == 2 && StringUtils.isNumeric(dimensions[0]) && StringUtils.isNumeric(dimensions[1])) {
                width = Integer.parseInt(dimensions[0]);
                height = Integer.parseInt(dimensions[1]);
            } else {
                return fail(in);
            }
        } else {
            return fail(in);
        }
        return true;
    }

    public boolean fail(String in) throws IOException {
        if (StringUtils.isEmpty(in)) {
            reasonToFail = "Empty or null string passed when a string with dimensions expected, e.g. 'Dimensions: 200,200'";
        } else {
            reasonToFail = "Misformatted serialized dimensions: " + in.replaceAll("\\n", "");           
        }
        return false;
    }
    
    private String reasonToFail = null;

    public String getReasonToFail() {
        return reasonToFail;
    }
    
}