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

package org.tmpotter.filters.wikimedia;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.tmpotter.ui.wizard.IImportWizardPanel;
import org.tmpotter.ui.wizard.ImportPreference;
import org.tmpotter.ui.wizard.ImportWizardController;
import org.tmpotter.ui.wizard.ImportWizardSelectTypePanel;
import org.tmpotter.util.Localization;
import org.tmpotter.util.gui.GuiUtils;

import java.net.URI;
import java.net.URISyntaxException;


/**
 * Wikimedia download and import filter wizard panel.
 *
 * @author Hiroshi Miura
 */
public class ImportWizardWikimedia extends javax.swing.JPanel implements IImportWizardPanel {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImportWizardWikimedia.class
            .getName());

    public static final String id = "WikimediaFilter";
    private final String[] idiom = Localization.getLanguageList();
    private ImportWizardController wizardController;
    private ImportPreference pref;
    private IImportWizardPanel panel;
    private boolean canDownload = false;

    /**
     * Creates new form ImportWizardWikimedia
     */
    public ImportWizardWikimedia() {
    }

    public void init(final ImportWizardController controller, ImportPreference pref) {
        wizardController = controller;
        this.pref = pref;
        initComponents();
        panel = new ImportWizardWikimediaDownload();
        panel.init(wizardController, pref);
        wizardController.registerPanel(panel.getId(), panel);
    }

    public void onShow() {
        GuiUtils.addChangeListener(textFieldSourceUrl, e -> onInputUrl());
        GuiUtils.addChangeListener(textFieldTranslationUrl, e -> onInputUrl());
    }

    public String getId() {
        return id;
    }

    public boolean isCombinedFormat() {
        return false;
    }

    public javax.swing.JPanel getPanel() {
        return this;
    }

    public String getName() {
        return "Wikimedia download";
    }

    public String getDesc() {
        return "Wikimeida download.";
    }

    public String getBackCommand() {
        return ImportWizardSelectTypePanel.id;
    }

    public String getNextFinishCommand() {
        return ImportWizardWikimediaDownload.id;
    }

    public void updatePref() {
        pref.setSourceEncoding("UTF-8");
        pref.setTranslationEncoding("UTF-8");
        pref.setOriginalLang(getSourceLocale());
        pref.setTranslationLang(getTargetLocale());
        pref.setOriginalFilePath(getOriginalFileUri());
        pref.setTranslationFilePath(getTranslationFileUri());
    }

    private void onInputUrl() {
        if (canDownload) {
            return;
        }
        if (isUrlValid()) {
            canDownload = true;
            wizardController.setButtonNextEnabled(true);
        }
    }

    private boolean isUrlValid() {
        if (textFieldSourceUrl.getText() == null || textFieldTranslationUrl.getText() == null) {
            return false;
        }
        if (textFieldSourceUrl.getText().equals("") || textFieldTranslationUrl.getText().equals("")) {
            return false;
        }
        try {
            URI sourceUri = new URI(textFieldSourceUrl.getText());
            URI translationUri = new URI(textFieldTranslationUrl.getText());
            if (!sourceUri.isAbsolute()) {
                return false;
            }
            if (!translationUri.isAbsolute()) {
                return false;
            }
        } catch (URISyntaxException ex) {
            return false;
        }
        return true;
    }

    public final String getSourceLocale() {
        return Localization.getLanguageCode(comboSourceLang.getSelectedIndex());
    }

    public final String getTargetLocale() {
        return Localization.getLanguageCode(comboTranslationLang.getSelectedIndex());
    }

    public final URI getOriginalFileUri() {
        URI uri;
        try {
            uri = new URI(textFieldSourceUrl.getText());
            return uri;
        } catch (Exception ex) {
            return null;
        }
    }

    public final URI getTranslationFileUri() {
        URI uri;
        try {
            uri = new URI(textFieldTranslationUrl.getText());
            return uri;
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT
     * modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
        // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
        private void initComponents() {

                jLabel1 = new javax.swing.JLabel();
                textFieldSourceUrl = new javax.swing.JTextField();
                jLabel2 = new javax.swing.JLabel();
                jLabel3 = new javax.swing.JLabel();
                textFieldTranslationUrl = new javax.swing.JTextField();
                jLabel4 = new javax.swing.JLabel();
                comboSourceLang = new javax.swing.JComboBox<>();
                jLabel5 = new javax.swing.JLabel();
                comboTranslationLang = new javax.swing.JComboBox<>();

                jLabel1.setText("Download Wikimedia page...");

                textFieldSourceUrl.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                textFieldSourceUrlActionPerformed(evt);
                        }
                });

                jLabel2.setText("Source text URL:");

                jLabel3.setText("Translation text URL:");

                textFieldTranslationUrl.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                textFieldTranslationUrlActionPerformed(evt);
                        }
                });

                jLabel4.setText("Language:");

                comboSourceLang.setModel(new javax.swing.DefaultComboBoxModel<>(idiom));

                jLabel5.setText("Language:");

                comboTranslationLang.setModel(new javax.swing.DefaultComboBoxModel<>(idiom));

                javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
                this.setLayout(layout);
                layout.setHorizontalGroup(
                        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(38, 38, 38)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(jLabel5)
                                                .addGap(38, 38, 38)
                                                .addComponent(comboTranslationLang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                .addComponent(jLabel3)
                                                .addComponent(jLabel2)
                                                .addComponent(jLabel1)
                                                .addComponent(textFieldSourceUrl)
                                                .addComponent(textFieldTranslationUrl, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
                                                .addGroup(layout.createSequentialGroup()
                                                        .addGap(6, 6, 6)
                                                        .addComponent(jLabel4)
                                                        .addGap(34, 34, 34)
                                                        .addComponent(comboSourceLang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                .addContainerGap(62, Short.MAX_VALUE))
                );
                layout.setVerticalGroup(
                        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(34, 34, 34)
                                .addComponent(jLabel1)
                                .addGap(23, 23, 23)
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(textFieldSourceUrl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel4)
                                        .addComponent(comboSourceLang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(24, 24, 24)
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(textFieldTranslationUrl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel5)
                                        .addComponent(comboTranslationLang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addContainerGap(50, Short.MAX_VALUE))
                );
        }// </editor-fold>//GEN-END:initComponents

        private void textFieldSourceUrlActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_textFieldSourceUrlActionPerformed
        }//GEN-LAST:event_textFieldSourceUrlActionPerformed

        private void textFieldTranslationUrlActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_textFieldTranslationUrlActionPerformed
        }//GEN-LAST:event_textFieldTranslationUrlActionPerformed


        // Variables declaration - do not modify//GEN-BEGIN:variables
        private javax.swing.JComboBox<String> comboSourceLang;
        private javax.swing.JComboBox<String> comboTranslationLang;
        private javax.swing.JLabel jLabel1;
        private javax.swing.JLabel jLabel2;
        private javax.swing.JLabel jLabel3;
        private javax.swing.JLabel jLabel4;
        private javax.swing.JLabel jLabel5;
        private javax.swing.JTextField textFieldSourceUrl;
        private javax.swing.JTextField textFieldTranslationUrl;
        // End of variables declaration//GEN-END:variables
}
