/* *************************************************************************
 *
 *  TMPotter - Bi-text Aligner/TMX Editor
 *
 *  Copyright (C) 2016 Hiroshi Miura
 *
 *  This file is part of TMPotter.
 *
 *  TMPotter is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  TMPotter is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with TMPotter.  If not, see http://www.gnu.org/licenses/.
 *
 * *************************************************************************/

package org.tmpotter.filters.xliff;

import net.sf.okapi.lib.xliff2.core.Fragment;
import net.sf.okapi.lib.xliff2.core.Segment;
import net.sf.okapi.lib.xliff2.core.Unit;
import net.sf.okapi.lib.xliff2.reader.Event;
import net.sf.okapi.lib.xliff2.reader.XLIFFReader;
import net.sf.okapi.lib.xliff2.writer.XLIFFWriter;

import org.tmpotter.core.Document;
import org.tmpotter.filters.FilterContext;
import org.tmpotter.filters.IFilter;
import org.tmpotter.filters.IParseCallback;

import java.io.File;
import java.util.Map;


/**
 * XLIFF input/output filter.
 * @author Hiroshi Miura
 */
public class Xliff2Filter implements IFilter {
    private Xliff2Filter self;

    /**
     * Constructor.
     */
    public Xliff2Filter() {
        this.self = this;
    }


    /**
     * Return file format name 'XLIFF2'.
     * @return format name  'XLIFF2'.
     */
    public String getFileFormatName() {
        return "XLIFF2";
    }

    /**
     * Return hint string.
     * @return hint string.
     */
    public String getHint() {
        return "XLIFF version 2 file.";
    }

    /**
     * TMX filter read UTF-8 file and not encoding variable.
     * @return false because UTF-8 encoding fixed.
     */
    public boolean isSourceEncodingVariable() {
        return false;
    }

    /**
     * TMX filter read UTF-8 file and not encoding variable.
     * @return false because UTF-8 encoding fixed.
     */
    public boolean isTargetEncodingVariable() {
        return false;
    }

    /**
     * Return fuzzy mark.
     * @return mark.
     */
    public String getFuzzyMark() {
        return "";
    }

    /**
     * TMX is combined format.
     * @return true
     */
    public boolean isCombinedFileFormat() {
        return true;
    }

    /**
     * Check the file is supported with this filter.
     * @param inFile  Source file.
     * @param config  filter's configuration options
     * @param context processing context
     * @return true if supported, otherwise false.
     */
    public boolean isFileSupported(File inFile, Map<String, String> config, FilterContext context) {
        if (inFile.getName().endsWith(".xlf")) {
            return true;
        }
        return false;
    }

    /**
     * Parse file.
     * @param inFile   file to parse
     * @param config   filter's configuration options
     * @param fc filter context.
     * @param callback callback for parsed data
     * @throws Exception when error is happened.
     */
    public void parseFile(File inFile, Map<String, String> config, FilterContext fc,
                          IParseCallback callback) throws Exception {
        try (XLIFFReader reader = new XLIFFReader()) {
            reader.open(inFile);
            while (reader.hasNext()) {
                Event event = reader.next();
                if (event.isUnit()) {
                    Unit unit = event.getUnit();
                    for (Segment segment : unit.getSegments()) {
                        callback.addEntry(null, segment.getSource().getPlainText(),
                                segment.getTarget().getPlainText(), false,
                                "",null, self);
                    }
                }
            }
        }

    }

    /**
     * Parse file.
     * @param sourceFile    source file
     * @param translateFile translated file
     * @param config        filter's configuration options
     * @param fc    filter context.
     * @param callback      callback for store aligned data
     * @throws Exception when error is happened.
     */
    @Override
    public void parseFile(File sourceFile, File translateFile, Map<String, String> config,
                          FilterContext fc, IParseCallback callback) throws Exception {
        parseFile(sourceFile, config, fc, callback);
    }

    @Override
    public boolean isSaveSupported() {
        return true;
    }

    @Override
    public void saveFile(File outFile, Document docOriginal, Document docTranslation,
                         Map<String, String> config, FilterContext fc) {
        try ( XLIFFWriter writer = new XLIFFWriter() ) {
            writer.create(outFile, fc.getSourceLang().toString());
            for (int i = 0; i < docOriginal.size(); i++) {
                Unit unit = new Unit(String.format("u%d", i));
                Segment segment = unit.appendSegment();
                Fragment srcContent = segment.getSource();
                srcContent.append(docOriginal.get(i));
                Fragment trsContent = segment.getTarget();
                trsContent.append(docTranslation.get(i));
            }
        }
    }
}
