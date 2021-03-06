/* *************************************************************************
 *
 *  TMPotter - Bi-text Aligner/TMX Editor
 *
 *  Copyright (C) 2015-2016 Hiroshi Miura
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
import static org.tmpotter.util.StringUtil.formatText;
import static org.tmpotter.util.StringUtil.restoreText;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.tmpotter.core.AlignmentChanges;
import org.tmpotter.core.ProjectProperties;
import org.tmpotter.core.TmData;
import org.tmpotter.core.TmpxReader;
import org.tmpotter.core.TmxpWriter;
import org.tmpotter.filters.FilterManager;
import org.tmpotter.preferences.Preferences;
import org.tmpotter.preferences.RuntimePreferences;
import org.tmpotter.segmentation.Segmenter;
import org.tmpotter.ui.dialogs.About;
import org.tmpotter.ui.wizard.ExportPreference;
import org.tmpotter.ui.wizard.ExportWizard;
import org.tmpotter.ui.wizard.ImportPreference;
import org.tmpotter.ui.wizard.ImportWizard;
import org.tmpotter.util.Utilities;
import org.tmpotter.util.gui.NegativeDefaultButtonJOptionPane;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;


/**
 * Action Handlers.
 *
 * @author Hiroshi Miura
 */
final class ActionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActionHandler.class);
    private final javax.swing.JFrame parent;
    private ModelMediator modelMediator;
    private final TmData tmData;
    private final FilterManager filterManager;

    public ActionHandler(final javax.swing.JFrame parent, TmData tmData,
                         FilterManager filterManager) {
        this.parent = parent;
        this.tmData = tmData;
        this.filterManager = filterManager;
    }

    public void setModelMediator(ModelMediator mediator) {
        this.modelMediator = mediator;
    }

    // proxy for aqua
    public boolean displayAbout() {
        menuItemHelpAboutActionPerformed();
        return true;
    }

    private void onOpenFile(File file) {
        modelMediator.setFilePathProject(file);
        modelMediator.buildDisplay();
        try {
            TmpxReader reader = new TmpxReader(modelMediator.getProjectProperties());
            reader.loadDocument(tmData);
        } catch (Exception ex) {
            LOGGER.info(ex.getMessage());
        }
        modelMediator.initializeTmView();
        modelMediator.updateTmView();
        modelMediator.enableButtonsOnOpenFile(true);
    }

    private void doImport(ImportPreference pref) {
        String filter = pref.getFilter(); // Now support "BiTextFilter" and "PoFilter"
        RuntimePreferences.setUserHome(pref.getOriginalFilePath());
        modelMediator.buildDisplay();
        Segmenter.setSrx(Preferences.getSrx());
        try {
            tmData.newDocuments();
            filterManager.loadFile(pref, modelMediator.getProjectProperties(),
                    tmData.getDocumentOriginal(), tmData.getDocumentTranslation(), filter);
            tmData.matchArrays();

            modelMediator.initializeTmView();
            modelMediator.updateTmView();
            modelMediator.enableButtonsOnOpenFile(true);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(parent, getString("MSG.ERROR"),
                    getString("MSG.ERROR.FILE_READ"), JOptionPane.ERROR_MESSAGE);
        }

        modelMediator.setProjectName(FilenameUtils.removeExtension(pref.getOriginalFilePath()
                .getName()));
    }

    public void doExport(ExportPreference pref) {
        String filter = pref.getFilter(); // Now support "TmxFilter"
        int level = pref.getLevel();
        File outFile = new File(pref.getFilePath());
        Map<String, String> options = new HashMap<>();
        options.put("level", String.valueOf(level));
        try {
            filterManager.saveFile(outFile, modelMediator.getProjectProperties(),
                    tmData.getDocumentOriginal(), tmData.getDocumentTranslation(),
                    options, filter);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(parent, getString("MSG.ERROR"),
                    getString("MSG.ERROR.FILE_WRITE"), JOptionPane.ERROR_MESSAGE);
        }
    }

    public void onTableClicked(MouseEvent evt) {
        javax.swing.JTable table = (javax.swing.JTable) evt.getSource();
        Point point = evt.getPoint();
        int row = table.rowAtPoint(point);
        int col = table.columnAtPoint(point);

        table.setRowSelectionInterval(row, row);
        table.setColumnSelectionInterval(col, col);

        tmData.setPositionTextArea(0);
        if (tmData.getIndexPrevious() < tmData.getDocumentOriginalSize()) {
            tmData.setOriginalDocumentAnt(restoreText(modelMediator.getLeftEdit()));
            tmData.setTranslationDocumentAnt(restoreText(modelMediator.getRightEdit()));
        }
        modelMediator.setLeftEdit(formatText(modelMediator.getLeftSegment(modelMediator
                .getTmViewSelectedRow())));
        modelMediator.setRightEdit(formatText(modelMediator.getRightSegment(modelMediator
                .getTmViewSelectedRow())));
        tmData.setBothIndex(modelMediator.getTmViewSelectedRow());
        if (tmData.isIdentTop()) {
            modelMediator.setJoinEnabled(false);
        } else {
            modelMediator.setJoinEnabled(true);
        }
        modelMediator.updateTmView();
    }

    public void onTablePressed(final KeyEvent event) {
        int index;
        if (modelMediator.getTmViewSelectedRow() != -1) {
            index = modelMediator.getTmViewSelectedRow();
            tmData.setPositionTextArea(0);
        } else {
            index = 1;
        }
        if (index < modelMediator.getTmViewRows() - 1) {
            if ((event.getKeyCode() == KeyEvent.VK_DOWN)
                    || (event.getKeyCode() == KeyEvent.VK_NUMPAD2)) {
                if (tmData.getIndexPrevious() < tmData.getDocumentOriginalSize()) {
                    tmData.setOriginalDocumentAnt(restoreText(modelMediator.getLeftEdit()));
                    tmData.setTranslationDocumentAnt(restoreText(modelMediator.getRightEdit()));
                }
                modelMediator.setLeftEdit(formatText(modelMediator.getLeftSegment(index + 1)));
                modelMediator.setRightEdit(formatText(modelMediator.getRightSegment(index + 1)));
                tmData.setIndexCurrent(index + 1);
            } else if ((event.getKeyCode() == KeyEvent.VK_UP)
                    || (event.getKeyCode() == KeyEvent.VK_NUMPAD8)) {
                tmData.setIndexCurrent(index - 1);
                if (index == 0) {
                    index = 1;
                    tmData.setIndexCurrent(0);
                }
                if (tmData.getIndexPrevious() < tmData.getDocumentOriginalSize()) {
                    tmData.setOriginalDocumentAnt(restoreText(modelMediator.getLeftEdit()));
                    tmData.setTranslationDocumentAnt(restoreText(modelMediator.getRightEdit()));
                }
                modelMediator.setLeftEdit(formatText(modelMediator.getLeftSegment(index - 1)));
                modelMediator.setRightEdit(formatText(modelMediator.getRightSegment(index - 1)));
            }
            if (tmData.isIdentTop()) {
                modelMediator.setJoinEnabled(false);
            } else {
                modelMediator.setJoinEnabled(true);
            }
            tmData.setIndexPrevious(tmData.getIndexCurrent());
        }
        modelMediator.updateTmView();
    }

    /**
     * Join on Original.
     */
    public final void onOriginalJoin() {
        tmData.incrementChanges();
        tmData.join(TmData.Side.ORIGINAL);
        modelMediator.updateTmView();
        modelMediator.setUndoEnabled(true);
    }

    /**
     * Delete on original document.
     */
    public final void onOriginalDelete() {
        tmData.incrementChanges();
        tmData.delete(TmData.Side.ORIGINAL);
        modelMediator.updateTmView();
        modelMediator.setUndoEnabled(true);
    }

    /**
     * Split on original document.
     */
    public final void onOriginalSplit() {
        tmData.incrementChanges();
        tmData.split(TmData.Side.ORIGINAL);
        modelMediator.updateTmView();
        modelMediator.setUndoEnabled(true);
    }

    /**
     * delete on translation document.
     */
    public final void onTranslationDelete() {
        tmData.incrementChanges();
        tmData.delete(TmData.Side.TRANSLATION);
        modelMediator.updateTmView();
        modelMediator.setUndoEnabled(true);
    }

    /**
     * join on translation document.
     */
    public final void onTranslationJoin() {
        tmData.incrementChanges();
        tmData.join(TmData.Side.TRANSLATION);
        modelMediator.updateTmView();
        modelMediator.setUndoEnabled(true);
    }

    /**
     * split on translation document.
     */
    public final void onTranslationSplit() {
        tmData.incrementChanges();
        tmData.split(TmData.Side.TRANSLATION);
        modelMediator.updateTmView();
        modelMediator.setUndoEnabled(true);
    }

    /**
     * remove blank rows in TMView.
     */
    public final void onRemoveBlankRows() {
        int maxTamArrays = 0;
        int cont = 0;
        int cleanedLines = 0;
        final int[] numCleared = new int[1000];  // default = 1000 - why?
        int cont2 = 0;

        maxTamArrays = Utilities.largerSize(tmData.getDocumentOriginalSize(),
            tmData.getDocumentTranslationSize()) - 1;

        while (cont <= (maxTamArrays - cleanedLines)) {
            if (tmData.isDocumentOriginalEmpty(cont)
                    && tmData.isDocumentTranslationEmpty(cont)) {
                cleanedLines++;
                numCleared[cont2] = cont + cont2;
                cont2++;
                tmData.removeDocumentOriginal(cont);
                tmData.removeDocumentTranslation(cont);
            } else {
                cont++;
            }
        }

        JOptionPane.showMessageDialog(parent, getString("MSG.ERASED") + " "
                + cleanedLines + " " + getString("MSG.BLANK_ROWS"));

        if (cleanedLines > 0) {
            tmData.incrementChanges();

            AlignmentChanges changes = new AlignmentChanges(AlignmentChanges.OperationKind.REMOVE,
                    0, TmData.Side.TRANSLATION, "", 0);
            tmData.addArrayListChanges(tmData.getIdentChanges(), changes);
            changes.setNumEliminada(numCleared, cleanedLines);
            modelMediator.setUndoEnabled(true);
            modelMediator.updateTmView();
        }
    }

    /**
     * Split on TU.
     */
    public final void onTuSplit() {
        tmData.tuSplit((modelMediator.getTmViewSelectedColumn() == 1) ? TmData.Side.ORIGINAL
                : TmData.Side.TRANSLATION);
        modelMediator.updateTmView();
        modelMediator.setUndoEnabled(true);
    }

    /**
     * Undo last change.
     */
    public void undoChanges() {
        AlignmentChanges ultChanges;
        ultChanges = tmData.getArrayListChanges(tmData.getIdentChanges());
        tmData.setIndexCurrent(ultChanges.getIdent_linea());
        AlignmentChanges.OperationKind operationKind = ultChanges.getKind();
        tmData.setIdentAntAsLabel();
        switch (operationKind) {
            case JOIN:
                tmData.undoJoin();
                break;
            case DELETE:
                tmData.undoDelete();
                modelMediator.updateTmView();
                break;
            case SPLIT:
                tmData.undoSplit();
                break;
            case REMOVE:
                tmData.undoRemove();
                break;
            case TUSPLIT:
                tmData.undoTuSplit(ultChanges.getSource());
                break;
            default:
                break;
        }
        modelMediator.updateTmView();
        tmData.removeArrayListChanges(tmData.getIdentChanges());
        int currentChange = tmData.decrementChanges();

        if (currentChange == -1) {
            modelMediator.setUndoEnabled(false);
        } else {
            modelMediator.setUndoEnabled(true);
        }
    }

    private void cleanTmData() {
        for (int cont = 0; cont < (tmData.getDocumentOriginalSize() - 1); cont++) {
            if (tmData.isDocumentOriginalEmpty(cont)
                    && tmData.isDocumentTranslationEmpty(cont)) {
                tmData.removeDocumentOriginal(cont);
                tmData.removeDocumentTranslation(cont);
            }
        }
    }
    /**
     * Save project.
     */
    private void saveProject() {
        ProjectProperties prop = modelMediator.getProjectProperties();
        if (prop.getFilePathProject() == null) {
            saveProjectAs();
            return;
        }
        try {
            cleanTmData();
            TmxpWriter.writeTmxp(prop,
                tmData.getDocumentOriginal(),
                tmData.getDocumentTranslation());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(parent, JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Save project as specified filename.
     */
    private void saveProjectAs() {
        File outFile = new File(modelMediator.getProjectName().concat(".tmpx"));
        try {
            boolean save = false;
            boolean cancel = false;
            while (!save && !cancel) {
                final JFileChooser fc = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter("TMX File", "tmpx");
                fc.setFileFilter(filter);
                boolean nameOfUser = false;
                while (!nameOfUser) {
                    fc.setLocation(230, 300);
                    fc.setCurrentDirectory(RuntimePreferences.getUserHome());
                    fc.setDialogTitle(getString("DLG.SAVEAS"));
                    fc.setMultiSelectionEnabled(false);
                    fc.setSelectedFile(outFile);
                    fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
                    RuntimePreferences.setUserHome(fc.getCurrentDirectory());
                    int returnVal;
                    returnVal = fc.showSaveDialog(parent);
                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        outFile = fc.getSelectedFile();
                        if (!outFile.getName().endsWith(".tmpx")) {
                            outFile = new File(outFile.getName().concat(".tmpx"));
                        }
                        nameOfUser = true;
                    } else {
                        nameOfUser = true;
                        cancel = true;
                    }
                }
                int selected;
                if (nameOfUser && !cancel) {
                    if (outFile.exists()) {
                        final Object[] options = {getString("BTN.SAVE"),
                            getString("BTN.CANCEL")};
                        selected = JOptionPane.showOptionDialog(parent,
                            getString("MSG.FILE_EXISTS"), getString("MSG.WARNING"),
                            JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,
                            null, options, options[0]);
                        if (selected == 0) {
                            save = true;
                        }
                    } else {
                        save = true;
                    }
                }
            }
            if (save) {
                cleanTmData();
                ProjectProperties prop = modelMediator.getProjectProperties();
                prop.setFilePathProject(outFile);
                TmxpWriter.writeTmxp(prop,
                        tmData.getDocumentOriginal(),
                        tmData.getDocumentTranslation());
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(parent, JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * clear empty segment.
     * <p>
     * <p>
     * Initialize values to start the validation of the following alignment
     */
    private void clear() {
        modelMediator.tmDataClear();
        modelMediator.clearProjectProperties();
        modelMediator.setUndoEnabled(false);
        modelMediator.tmViewClear();
        modelMediator.editSegmentClear();
    }

    /**
     * Quit application.
     *
     * @return boolean - OS X Aqua integration only
     */
    public boolean quit() {
        menuItemFileQuitActionPerformed();
        return true;
    }

    /**
     * Action entry points.
     * <p>
     * All action entry points named with +ActionPerformed().
     */
    public void onFileOpen() {
        final JFileChooser fc = new JFileChooser();
        File filePath = RuntimePreferences.getUserHome();
        fc.setCurrentDirectory(filePath);
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "Project File(.tmpx)", "tmpx");
        fc.setFileFilter(filter);
        fc.setMultiSelectionEnabled(false);
        final int returnVal = fc.showOpenDialog(parent);
        filePath = fc.getCurrentDirectory();
        RuntimePreferences.setUserHome(filePath);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            filePath = fc.getSelectedFile();

            if (filePath.getName().endsWith(".tmpx")
                    && filePath.exists()) {
                try {
                    final FileInputStream fr = new FileInputStream(filePath);
                    fr.close();
                    onOpenFile(filePath);
                } catch (Exception ex) {
                 JOptionPane.showMessageDialog(parent,
                        getString("MSG.ERROR.FILE_NOTFOUND"),
                        getString("MSG.ERROR"), JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(parent,
                        getString("MSG.ERROR.FILE_NOTFOUND"),
                        getString("MSG.ERROR"), JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void onNewImport() {
        final ImportWizard dlg = new ImportWizard(parent, true);
        //dlg.setFilePath(RuntimePreferences.getUserHome());
        dlg.setModal(true);
        dlg.setVisible(true);
        dlg.dispose();
        if (dlg.isFinished()) {
            dlg.updatePref();
            doImport(dlg.getPref());
        }
    }

    public void onExport() {
        final ExportWizard dlg = new ExportWizard(parent, true);
        dlg.setModal(true);
        dlg.setVisible(true);
        if (dlg.isFinished()) {
            ExportPreference pref = new ExportPreference();
            pref.setFilePath(dlg.getFilePath());
            String sel = dlg.getSelection();
            if ("Level2".equals(sel)) {
                pref.setLevel(2);
            } else {
                pref.setLevel(1);
            }
            pref.setFilter("TmxFilter");
            doExport(pref);
        }
    }

    public void buttonSaveActionPerformed() {
        saveProject();
    }

    public void buttonSaveAsActionPerformed() {
        saveProjectAs();
    }

    public void menuItemFileSaveAsActionPerformed() {
        saveProjectAs();
    }

    //  ToDo: implement proper functionality; not used currently
    public void menuItemFileSaveActionPerformed() {
        saveProject();
    }

    /**
     * Actions to perform when closing alignment/editing session.
     * <p>
     * <p>
     * Leaves the text as it was so that it can be processed later.
     */
    public void menuItemFileCloseActionPerformed() {
        clear();
        modelMediator.enableButtonsOnOpenFile(false);
    }

    public void buttonCloseActionPerformed() {
        menuItemFileCloseActionPerformed();
    }

    private void realCloseApplication() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                parent.dispose();
            }
        });
    }

    public void menuItemFileExportActionPerformed() {
        onExport();
    }

    public void menuItemFileQuitActionPerformed() {
        int result = NegativeDefaultButtonJOptionPane.showConfirmDialog(null,
                "Do you want to quit application?", "Warning", JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            realCloseApplication();
        }
    }

    public void buttonExitActionPerformed() {
        menuItemFileQuitActionPerformed();
    }

    public void buttonUndoActionPerformed() {
        undoChanges();
    }

    public void menuItemUndoActionPerformed() {
        undoChanges();
    }

    public void buttonRedoActionPerformed() {
        // FIXME: implement me.
    }

    public void menuItemRedoActionPerformed() {
        // FIXME: implement me.
    }

    public void buttonNewActionPerformed() {
        onNewImport();
    }

    public void menuItemFileImportActionPerformed() {
        onNewImport();
    }

    public void menuItemFileOpenActionPerformed() {
        onFileOpen();
    }

    public void buttonOpenActionPerformed() {
        onFileOpen();
    }

    public void menuItemOriginalDeleteActionPerformed() {
        onOriginalDelete();
    }

    public void buttonOriginalDeleteActionPerformed() {
        onOriginalDelete();
    }

    public void menuItemOriginalJoinActionPerformed() {
        onOriginalJoin();
    }

    public void buttonOriginalJoinActionPerformed() {
        onOriginalJoin();
    }

    public void menuItemOriginalSplitActionPerformed() {
        onOriginalSplit();
    }

    public void buttonOriginalSplitActionPerformed() {
        onOriginalSplit();
    }

    public void menuItemTranslationDeleteActionPerformed() {
        onTranslationDelete();
    }

    public void buttonTranslationDeleteActionPerformed() {
        onTranslationDelete();
    }

    public void menuItemTranslationJoinActionPerformed() {
        onTranslationJoin();
    }

    public void buttonTranslationJoinActionPerformed() {
        onTranslationJoin();
    }

    public void menuItemTranslationSplitActionPerformed() {
        onTranslationSplit();
    }

    public void buttonTranslationSplitActionPerformed() {
        onTranslationSplit();
    }

    public void menuItemRemoveBlankRowsActionPerformed() {
        onRemoveBlankRows();
    }

    public void buttonRemoveBlankRowsActionPerformed() {
        onRemoveBlankRows();
    }

    public void menuItemTuSplitActionPerformed() {
        onTuSplit();
    }

    public void buttonTuSplitActionPerformed() {
        onTuSplit();
    }

    public void menuItemSettingsActionPerformed() {
        // FIXME: Implement me.
    }

    public void menuItemHelpAboutActionPerformed() {
        new About(parent, true).setVisible(true);
    }

}
