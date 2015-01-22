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


/**
 * The cache stores these different-sized versions of the item.
 *
 * @author Borys Omelayenko
 */

public enum ItemSize {
    ORIGINAL,
    FULL_DOC, // 380 x 200
    BRIEF_DOC, // 110 x 160
    TINY // 27 x 40    
}
