/**
 * ************************************************************************
 * bitext2tmx - Bitext Aligner/TMX Editor.
 *  Copyright (C) 2005-2009  Raymond: Martin
 *           (C) 2015 Hiroshi Miura
 * Copyright (C) 2000-2006 Keith Godfrey and Maxym Mykhalchuk 
 *
 * This is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OmegaT is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see http://www.gnu.org/licenses/.
 *************************************************************************
 */

package bitext2tmx.filters;

/**
 * One attribute of a tag.
 *
 * @author Maxym Mykhalchuk
 */
public class Attribute {

  private String name;

  /**
   * Returns attribute's name.
   */
  public String getName() {
    return name;
  }

  private String value;

  /**
   * Returns attribute's value.
   */
  public String getValue() {
    return value;
  }

  /**
   * Sets attribute's value.
   * <p>
   * Actually an ugly hack to allow quick & dirty translation of attributes.
   * Normal solution requires too much time :-(
   */
  public void setValue(String value) {
    this.value = value;
  }

  /**
   * Creates a new instance of Attribute.
   * 
   * @param name attribute name
   * @param value attribute val
   */
  public Attribute(String name, String value) {
    this.name = name;
    this.value = value;
  }

  /**
   * Returns a string representation of the attribute. name="value".
   */
  public String toString() {
    return name + "=\"" + value + "\"";
  }
}
