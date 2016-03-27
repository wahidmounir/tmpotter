/**************************************************************************
 *
 *  TMPotter - Bi-text Aligner/TMX Editor
 *
 *  Copyright (C) 2015,2016 Hiroshi Miura
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
 **************************************************************************/

package org.tmpotter.core;

import org.tmpotter.util.AppConstants;

import java.io.File;


/**
 *
 * @author miurahr
 */
public class TmxWriterTest extends TmxTestBase {

  /**
   * Test of writeTmx method, of class TmxWriter.
   * @throws java.lang.Exception
   */
  public void testWriteTmx() throws Exception {
    System.out.println("writeTmx");
    File outputFile = new File(this.getClass().getResource("/tmx/test_write_tmx.tmx").getFile());
    File expectedFile = new File(this.getClass().getResource("/tmx/expected_write_tmx.tmx").getFile());
    Document originalDocument = new Document();
    originalDocument.add("Sentense one.");
    originalDocument.add("Sentense two.");
    String langOriginal = "EN";
    Document translationDocument = new Document();
    translationDocument.add("Sentense one in Japanese.");
    translationDocument.add("Sentense two in Japanese.");
    String langTranslation = "JA";
    String encoding = AppConstants.ENCODINGS_UTF8;
    TmxWriter.writeTmx(outputFile, originalDocument, langOriginal,
            translationDocument, langTranslation, encoding);
    compareTmx(outputFile, expectedFile, 4);
    outputFile.delete();
  }
}