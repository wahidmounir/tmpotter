/*
 * Copyright (C) 2015 miurahr
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package bitext2tmx.core;

import junit.framework.TestCase;

/**
 *
 * @author miurahr
 */
public class AlignerTest extends TestCase {
  
  public AlignerTest(String testName) {
    super(testName);
  }
  
  @Override
  protected void setUp() throws Exception {
    super.setUp();
  }
  
  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
  }

  /**
   * Test of align method, of class Align.
   */
  public void testAlign() {
    System.out.println("align");
    Document orig = new Document();
    orig.add("");
    Document trans = new Document();
    trans.add("");
    boolean expResult = true;
    boolean result = TranslationAligner.align(orig, trans);
    assertEquals(expResult, result);
  }
  
}