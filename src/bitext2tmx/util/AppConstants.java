/**************************************************************************
 *
 *  bitext2tmx - Bitext Aligner/TMX Editor
 *
 *  Copyright (C) 2006-2009 Raymond: Martin et al
 *  Copyright (C) 2015 Hiroshi Miura
 *
 *  This file is part of bitext2tmx.
 *
 *  bitext2tmx is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  bitext2tmx is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with bitext2tmx.  If not, see http://www.gnu.org/licenses/.
 *
 **************************************************************************/

package bitext2tmx.util;

import static bitext2tmx.util.Localization.getString;

import java.io.File;
import java.util.ResourceBundle;
import java.util.regex.Pattern;


/**
 *  Constants - class level (global scope).
 *
 */
public class AppConstants {
  private static final String __VERSION_KEY = "version";
  private static final String __UPDATE_KEY = "update";
  private static final String __REVISION_KEY = "revision";
  private static String BRANDING = "";
  private static final String NAME = getString( "WND.APP.TITLE" );
  private static final String VERSION = ResourceBundle
          .getBundle("bitext2tmx/Version")
          .getString(__VERSION_KEY);
  private static final String UPDATE = ResourceBundle
          .getBundle("bitext2tmx/Version")
          .getString(__UPDATE_KEY);
  private static final String REVISION = ResourceBundle
          .getBundle("bitext2tmx/Version")
          .getString(__REVISION_KEY);
  public static final String COPYRIGHT   = "Copyright (C) 2005-2009,2015";
  public static final String LICENSE     = 
          "Released as Free Software under GPL v3 and later";
  public static final String AUTHORS     = 
          "Susana Santos Antón, Raymond: Martin, Hiroshi Miura et al.";
  public static final String BUILDCLASSPATH = "build"
          + File.separator + "classes";
  public static final int READ_AHEAD_LIMIT = 65536;

  public static final Pattern XML_ENCODING = Pattern
            .compile("<\\?xml.*?encoding\\s*=\\s*\"(\\S+?)\".*?\\?>");

  public static final String APPLICATION_JAR = "bitext2mx.jar";
  public static final String DEBUG_CLASSPATH = File.separator + "classes";

  // Encodings.java
  public static final String ENCODINGS_UTF8        = "UTF-8";
  public static final String ENCODINGS_ISO8859_1   = "ISO-8859-1";
  public static final String ENCODINGS_CP932       = "CP932";
  public static final String ENCODINGS_DEFAULT     = "Default";
  public static final String [] straEncodings = {
      ENCODINGS_UTF8, ENCODINGS_ISO8859_1, ENCODINGS_CP932, ENCODINGS_DEFAULT
  };

  /**
   * Make app name and version string for human.
   *
   * @return string to indicate for human reading
   */
  public static String getDisplayNameAndVersion() {
    if (UPDATE != null && !UPDATE.equals("0")) {
      return StringUtil.format(getString("app-version-template-pretty-update"),
                    getApplicationDisplayName(), VERSION, UPDATE);
    } else {
      return StringUtil.format(getString("app-version-template-pretty"),
                    getApplicationDisplayName(), VERSION);
    }
  }

  /**
   * Get the application name for display purposes (includes branding).
   *
   * @return  application name for human reading.
   */
  public static String getApplicationDisplayName() {
    return BRANDING.isEmpty() ? NAME : NAME + " " + BRANDING;
  }
  
  /**
   * Get the application description.
   * 
   * @return string to describe application
   */
  public static String getApplicationDescription() {
    return getString("WND.APP.DESCRIPTION");
  }
  
  public static String getAppNameAndVersion() {
    return StringUtil.format(getString("app-version-template"), NAME, VERSION, UPDATE, REVISION);
  }

  public static String getVersion() {
    return StringUtil.format(getString("version-template"), VERSION, UPDATE, REVISION);
  }
}
