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

package bitext2tmx.ui.dialogs;

import static bitext2tmx.util.Localization.getString;
import static org.openide.awt.Mnemonics.setLocalizedText;

import bitext2tmx.ui.Icons;
import bitext2tmx.ui.MainWindow;
import bitext2tmx.util.AppConstants;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;



/**
 *  About Dialog.
 * 
 *  @author Hiroshi Miura
 */
@SuppressWarnings("serial")
public final class About extends JDialog {

  public About( MainWindow mainWindow ) {
    this(mainWindow, true );
  }

  public About(MainWindow mainWindow, Boolean modal) {
    super(mainWindow, modal);
    initComponents();
  }

  @SuppressWarnings("unchecked")
  private void initComponents() {
    setTitle( getString( "DLG.ABOUT.TITLE" ) );
    setModal( true );
    setResizable( false );

    addWindowListener( new WindowAdapter() {
      @Override
      public void windowClosing( final WindowEvent evt ) {
        onClose(); 
      } 
    });

    label1.setText(AppConstants.getDisplayNameAndVersion() );
    label2.setText(AppConstants.getApplicationDescription() );
    label3.setText(AppConstants.COPYRIGHT );
    label4.setText(AppConstants.AUTHORS );

    final GridBagConstraints gbc = new GridBagConstraints();

    getContentPane().setLayout( new GridBagLayout() );

    gbc.gridwidth = 1;
    gbc.gridx     = 0;
    gbc.gridy     = 0;
    gbc.fill      = GridBagConstraints.CENTER;
    gbc.weightx   = 1.0;
    gbc.insets    = new Insets( 5, 10, 5, 10 );

    getContentPane().add( label1, gbc );

    gbc.gridy = 1;
    getContentPane().add( label2, gbc );

    gbc.gridy = 2;
    getContentPane().add( label3, gbc );

    gbc.gridy = 3;
    getContentPane().add( label4, gbc );

    labelIcon.setIcon( Icons.getIcon( "icon-medium.png" ) );
    panelButtons.add( labelIcon, BorderLayout.WEST );

    gbc.gridy     = 5;
    gbc.anchor    = GridBagConstraints.SOUTHWEST;
    gbc.insets    = new Insets( 5, 25, 5, 10 );

    getContentPane().add( labelIcon, gbc );

    setLocalizedText( buttonClose, getString( "BTN.CLOSE" ) );
    buttonClose.addActionListener( new ActionListener() {
      @Override
      public void actionPerformed( final ActionEvent evt ) {
        onClose(); 
      }
    });

    gbc.anchor    = GridBagConstraints.CENTER;
    getContentPane().add( buttonClose, gbc );

    final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    setBounds( ( screenSize.width - 400 ) / 2,
        ( screenSize.height - 200 ) / 2, 400, 200 );
  }

  private void onClose() {
    setVisible( false );
    dispose();
  }

  // Variables declaration
  private final JLabel   label1       = new JLabel();
  private final JLabel   label2       = new JLabel();
  private final JLabel   label3       = new JLabel();
  private final JLabel   label4       = new JLabel();
  private final JLabel   labelIcon    = new JLabel();
  private final JButton  buttonClose  = new JButton();
  private final JPanel   panelButtons = new JPanel();
  // End of variables declaration
}