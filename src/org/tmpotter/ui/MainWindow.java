/* *************************************************************************
 *
 *  TMPotter - Bi-text Aligner/TMX Editor
 *
 *  Copyright (C) 2015 Hiroshi Miura
 *
 *  This file come from bitext2tmx.
 *
 *  Copyright (C) 2005-2006 Susana Santos Antón
 *            (C) 2006-2009 Raymond: Martin et al
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

package org.tmpotter.ui;

import static org.tmpotter.util.Localization.getString;

import com.vlsolutions.swing.docking.DockingDesktop;

import org.tmpotter.core.Document;
import org.tmpotter.engine.SegmentChanges;
import org.tmpotter.util.Platform;
import org.tmpotter.util.Utilities;
import org.tmpotter.util.gui.AquaAdapter;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import java.io.File;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;


/**
 * Main window class,.
 * 
 */
@SuppressWarnings("serial")
public final class MainWindow extends JFrame implements WindowListener {
  protected final AlignmentsView viewAlignments = new AlignmentsView(this);
  protected final SegmentEditor editLeftSegment = new SegmentEditor(this);
  protected final SegmentEditor editRightSegment = new SegmentEditor(this);
  protected final ControlView viewControls = new ControlView(this);

  protected MainWindowMenuHandlers handler;
  protected MainWindowMenus mainWindowMenu;
  protected MainWindowFonts mainWindowFonts;

  protected DockingDesktop desktop;

  //  Menubar
  protected final JMenuBar menuBar = new JMenuBar();

  //  Statusbar
  protected JPanel panelStatusBar;
  protected JLabel labelStatusBar;

  protected Document documentOriginal;
  protected Document documentTranslation;

  protected final ArrayList arrayListBitext;

  protected final ArrayList<SegmentChanges> arrayListChanges;
  protected final ArrayList arrayListLang;

  protected int topArrays;    //  =  0;
  protected int positionTextArea;  //  =  0;
  protected int identChanges = -1;
  protected int identLabel;  //  =  0;
  protected int identAnt;     //  =  0;

  protected String stringLangOriginal = "en";
  protected String stringLangTranslation = "en";
  protected String stringOriginal;
  protected String stringTranslation;

  protected File userHome = new File(System.getProperty("user.home"));
  protected File filePathOriginal;
  protected File filePathTranslation;


  /**
   * Main window class.
   * 
   */
  public MainWindow() {
    this.arrayListBitext = new ArrayList();
    this.arrayListChanges = new ArrayList<>();
    this.arrayListLang = new ArrayList();

    handler = new MainWindowMenuHandlers(this);
    mainWindowMenu = new MainWindowMenus(this, handler);
    mainWindowFonts = new MainWindowFonts(this, mainWindowMenu);

    MainWindowUi.initDockingUi(this);
    makeMenus();
    MainWindowUi.makeUi(this);
    setWindowIcon();

    //  Proxy callbacks from/to Mac OS X Aqua global menubar for Quit and About
    try {
      AquaAdapter.connect(handler, "displayAbout", AquaAdapter.AquaEvent.ABOUT);
      AquaAdapter.connect(handler, "quit", AquaAdapter.AquaEvent.QUIT);
    } catch (final NoClassDefFoundError e) {
      System.out.println(e);
    }

    setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
    addWindowListener(new WindowAdapter() {
      @Override
      public final void windowClosing(final WindowEvent event) {
        handler.quit();
      }
    });

    final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    final Dimension frameSize = this.getSize();

    if (frameSize.height > screenSize.height) {
      frameSize.height = screenSize.height;
    }
    if (frameSize.width > screenSize.width) {
      frameSize.width = screenSize.width;
    }
    this.setLocation((screenSize.width - frameSize.width) / 2,
            (screenSize.height - frameSize.height) / 2);
    setFonts(null);
  }

  protected ImageIcon getDesktopIcon(final String iconName) {
    if (Platform.isMacOsx()) {
      return (mainWindowMenu.getIcon("desktop/osx/" + iconName));
    }
    return (mainWindowMenu.getIcon("desktop/" + iconName));
  }

  /**
   * Set root window icon.
   */
  private void setWindowIcon() {
    try {
      //setIconImage( Icons.getIcon( "icon-small.png" ).getImage() ); 
    } catch (final Exception e) {
      System.out.println("Error loading icon: " + e);
    }
  }

  private void makeMenus() {
    menuBar.add(mainWindowMenu.getMenuFile());
    menuBar.add(mainWindowMenu.getMenuSettings());
    menuBar.add(mainWindowMenu.getMenuHelp());
    setJMenuBar(menuBar);
  }

  /**
   * Updates the changes adding a "join" change in the "undo" array and performs
   * the "join". (not sure about the translation)
   *
   * @param textAreaIzq :TRUE if the left text (source text) has to be joined
   */
  private void join(final boolean textAreaIzq) {
    if (identLabel != topArrays) {
      final SegmentChanges Changes = new SegmentChanges(0, positionTextArea,
              textAreaIzq, "", identLabel);
      arrayListChanges.add(identChanges, Changes);

      if (textAreaIzq) {
        Changes.setFrase(documentOriginal.get(identLabel));
      } else {
        Changes.setFrase(documentTranslation.get(identLabel));
      }

      if (textAreaIzq) {
        documentOriginal.join(identLabel);
      } else {
        documentTranslation.join(identLabel);
      } 
    }
  }

  /**
   * Delete text. 
   * 
   * <p>This function updates the changes adding a delete change
   * to the undo array and deletes
   *
   * @param textAreaIzq :TRUE if the left hand (source text) has to be deleted
   */
  private void delete(final boolean textAreaIzq) {
    final SegmentChanges Changes = new SegmentChanges(1, positionTextArea,
            textAreaIzq, "", identLabel);
    arrayListChanges.add(identChanges, Changes);

    if (textAreaIzq) {
      Changes.setFrase(documentOriginal.get(identLabel));
    } else {
      Changes.setFrase(documentTranslation.get(identLabel));
    }

    if (textAreaIzq) {
      documentOriginal.delete(identLabel);
    } else {
      documentTranslation.delete(identLabel);
    }
  }

  /**
   * Function Split. 
   * 
   * <p>This function updates the changes adding a split to the undo
   * array and performs the splitting
   *
   * @param textAreaIzq :TRUE if the left hand (source text) has to be split
   */
  private void split(final boolean textAreaIzq) {
    if (textAreaIzq) {
      if (positionTextArea >= documentOriginal.get(identLabel).length()) {
        positionTextArea = 0;
      }
    } else if (positionTextArea >= documentTranslation.get(identLabel).length()) {
      positionTextArea = 0;
    }
    final SegmentChanges Changes = new SegmentChanges(2, positionTextArea,
            textAreaIzq, "", identLabel);
    arrayListChanges.add(identChanges, Changes);
    if (textAreaIzq) {
      Changes.setFrase(documentOriginal.get(identLabel));
    } else {
      Changes.setFrase(documentTranslation.get(identLabel));
    }

    if (textAreaIzq) {
      documentOriginal.split(identLabel, Changes.getPosition());
    } else {
      documentTranslation.split(identLabel, Changes.getPosition());
    }
  }

  /**
   * Update the row in table with mods.
   * 
   * <p>This function updates the rows in the table with the
   * modifications performed, adds rows or removes them.
   */
  protected void updateAlignmentsView() {
    viewAlignments.updateView();
  }

  //  Accessed by ControlView
  final void onOriginalJoin() {
    identChanges++;
    join(true);
    updateAlignmentsView();
  }

  //  Accessed by ControlView
  final void onOriginalDelete() {
    identChanges++;
    delete(true);
    updateAlignmentsView();
  }

  //  Accessed by ControlView
  final void onOriginalSplit() {
    identChanges++;
    split(true);
    updateAlignmentsView();
  }

  //  Accessed by ControlView
  final void onTranslationJoin() {
    identChanges++;
    join(false);
    updateAlignmentsView();
  }

  //  Accessed by ControlView
  final void onTranslationDelete() {
    identChanges++;
    delete(false);
    updateAlignmentsView();
  }

  //  Accessed by ControlView
  final void onTranslationSplitCv() {
    identChanges++;
    split(false);
    updateAlignmentsView();
  }

  //  Accessed by ControlView
  final void onUndoCv() {
    handler.undoChanges();
    arrayListChanges.remove(identChanges);
    identChanges--;

    if (identChanges == -1) {
      viewControls.setUndoEnabled(false);
    }
  }

  //  Accessed by SegmentEditor
  public final void setTextAreaPosition(int position) {
    positionTextArea = position;
  }

  //  Accessed by ControlView currently
  final void onRemoveBlankRows() {
    int maxTamArrays = 0;
    int cont = 0;
    int lineasLimpiar = 0;
    final int[] numEliminadas = new int[1000];  // default = 1000 - why?
    int cont2 = 0;

    maxTamArrays = Utilities.largerSize(documentOriginal.size(), documentTranslation.size()) - 1;

    while (cont <= (maxTamArrays - lineasLimpiar)) {
      if ((documentOriginal.get(cont) == null 
              || documentOriginal.get(cont).equals(""))
            && (documentTranslation.get(cont) == null 
              || documentTranslation.get(cont).equals(""))) {
        lineasLimpiar++;
        numEliminadas[cont2] = cont + cont2;
        cont2++;
        documentOriginal.remove(cont);
        documentTranslation.remove(cont);
      } else {
        cont++;
      }
    }

    JOptionPane.showMessageDialog(this, getString("MSG.ERASED") + " "
            + lineasLimpiar + " " + getString("MSG.BLANK.ROWS"));

    if (lineasLimpiar > 0) {
      identChanges++;

      SegmentChanges changes = new SegmentChanges(3, 0, false, "", 0);
      arrayListChanges.add(identChanges, changes);
      changes.setNumEliminada(numEliminadas, lineasLimpiar);
      viewControls.setUndoEnabled(true);
      updateAlignmentsView();
    }
  }

  //  Accessed by ControlView currently
  final void onTuSplit() {
    int izq;
    int cont;
    SegmentChanges changes;
    //  Done in _vwControls
    //_btnUndo.setEnabled( true );
    identChanges++;

    izq = viewAlignments.getSelectedColumn();

    documentOriginal.add(documentOriginal.size(),
            documentOriginal.get(documentOriginal.size() - 1));
    documentTranslation.add(documentTranslation.size(),
            documentTranslation.get(documentTranslation.size() - 1));

    if (izq == 1) {
      // Columna izq.
      // Left column.
      changes = new SegmentChanges(4, 0, true, "", identLabel);

      for (cont = documentTranslation.size() - 1; cont > identLabel; cont--) {
        documentTranslation.set(cont, documentTranslation.get(cont - 1));

        if (cont > (identLabel + 1)) {
          documentOriginal.set(cont, documentOriginal.get(cont - 1));
        } else {
          documentOriginal.set(cont, "");
        }
      }

      documentTranslation.set(identLabel, "");
    } else {
      changes = new SegmentChanges(4, 0, false, "", identLabel);

      for (cont = documentOriginal.size() - 1; cont > identLabel; cont--) {
        documentOriginal.set(cont, documentOriginal.get(cont - 1));

        if (cont > (identLabel + 1)) {
          documentTranslation.set(cont, documentTranslation.get(cont - 1));
        } else {
          documentTranslation.set(cont, "");
        }
      }

      documentOriginal.set(identLabel, "");
    }

    arrayListChanges.add(identChanges, changes);
    updateAlignmentsView();
  }

  /**
   * Fonts mutator Delegates actual setting of fonts to specific methods.
   *
   * <p> Passing in null causes default values to be used - used at startup or for
   * reset Passing in a font causes all UI elements to be the same - used with
   * the 'All' window area when selected in the fonts dialog
   *
   * @param font to be configured
   */
  public final void setFonts(final Font font) {
    mainWindowFonts.setUserInterfaceFont(font, this);
    mainWindowFonts.setTableFont(font, this);
    mainWindowFonts.setTableHeaderFont(font);
    mainWindowFonts.setSourceEditorFont(font, this);
    mainWindowFonts.setTargetEditorFont(font, this);
    viewControls.setFonts(font);
  }

  public final void setTableFont(final Font font) {
    mainWindowFonts.setTableFont(font, this);
  }

  public final void setUserInterfaceFont(final Font font) {
    mainWindowFonts.setUserInterfaceFont(font, this);
  }

  public final void setTableHeaderFont(final Font font) {
    mainWindowFonts.setTableHeaderFont(font);
  }

  public final void setSourceEditorFont(final Font font) {
    mainWindowFonts.setSourceEditorFont(font, this);
  }

  public final void setTargetEditorFont(final Font font) {
    mainWindowFonts.setTargetEditorFont(font, this);
  }

  /**
   * Font family names accessor.
   *
   * @return String[] font family names
   */
  public final String[] getFontFamilyNames() {
    GraphicsEnvironment graphics = GraphicsEnvironment.getLocalGraphicsEnvironment();
    return graphics.getAvailableFontFamilyNames();
  }


  //  WindowListener Overrides
  @Override
  public final void windowActivated(final WindowEvent evt) {
  }

  @Override
  public final void windowClosed(final WindowEvent evt) {
  }

  @Override
  public final void windowClosing(final WindowEvent evt) {
    if (evt.getSource() == this) {
      handler.menuItemFileQuitActionPerformed();
    }
  }

  @Override
  public final void windowDeactivated(final WindowEvent evt) {
  }

  @Override
  public final void windowDeiconified(final WindowEvent evt) {
  }

  @Override
  public final void windowIconified(final WindowEvent evt) {
  }

  @Override
  public final void windowOpened(final WindowEvent evt) {
  }

  /**
   * Function IgualarArrays: adds rows to the smallest array and deletes blank
   * rows.
   */
  void matchArrays() {
    boolean limpiar = true;
    while (documentOriginal.size() > documentTranslation.size()) {
      documentTranslation.add(documentTranslation.size(), "");
    }
    while (documentTranslation.size() > documentOriginal.size()) {
      documentOriginal.add(documentOriginal.size(), "");
    }
    while (limpiar) {
      if (documentOriginal.get(documentOriginal.size() - 1) == null
          || (documentOriginal.get(documentOriginal.size() - 1).equals(""))
          && (documentTranslation.get(documentTranslation.size() - 1) == null
          || documentTranslation.get(documentTranslation.size() - 1)
                  .equals(""))) {
        documentOriginal.remove(documentOriginal.size() - 1);
        documentTranslation.remove(documentTranslation.size() - 1);
      } else {
        limpiar = false;
      }
    }
    topArrays = documentOriginal.size() - 1;
    if (identLabel > (documentOriginal.size() - 1)) {
      identLabel = documentOriginal.size() - 1;
    }
  }

}