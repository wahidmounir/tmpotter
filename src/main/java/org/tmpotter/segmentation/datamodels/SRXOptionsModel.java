/* *************************************************************************
 *
 *  TMPotter - Bi-text Aligner/TMX Editor
 *
 *  Copyright (C) 2015 Hiroshi Miura
 *
 *  this come from OmegaT.
 *
 *  Copyright (C) 2000-2006 Keith Godfrey and Maxym Mykhalchuk
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

package org.tmpotter.segmentation.datamodels;

import org.tmpotter.segmentation.SRX;

import javax.swing.ButtonModel;
import javax.swing.JToggleButton;


/**
 * SRX Options model class.
 *
 * @author Maxym Mykhalchuk
 */
@SuppressWarnings("serial")
public class SRXOptionsModel extends JToggleButton.ToggleButtonModel {

    protected SRX srx;

    /**
     * Creates a new instance of SRXOptionsModel.
     */
    protected SRXOptionsModel(SRX srx) {
        this.srx = srx;
    }

    /**
     * a model for segment subflows checkbox.
     */
    private static class SegmentSubflowsModel extends SRXOptionsModel {

        SegmentSubflowsModel(SRX srx) {
            super(srx);
        }

        public void setSelected(boolean selected) {
            srx.setSegmentSubflows(selected);
            super.setSelected(selected);
        }

        public boolean isSelected() {
            return srx.isSegmentSubflows();
        }

    }

    /**
     * a model for segment subflows checkbox.
     */
    public static ButtonModel getSegmentSubflowsModel(SRX srx) {
        return new SegmentSubflowsModel(srx);
    }

    /**
     * a model for including starting formatting tags at the end boundary of the
     * segment checkbox.
     */
    private static class IncludeStartingTagsModel extends SRXOptionsModel {

        IncludeStartingTagsModel(SRX srx) {
            super(srx);
        }

        public void setSelected(boolean selected) {
            srx.setIncludeStartingTags(selected);
            super.setSelected(selected);
        }

        public boolean isSelected() {
            return srx.isIncludeStartingTags();
        }

    }

    /**
     * a model for including starting formatting tags at the end boundary of the
     * segment checkbox.
     */
    public static ButtonModel getIncludeStartingTagsModel(SRX srx) {
        return new IncludeStartingTagsModel(srx);
    }

    /**
     * a model for including ending formatting tags at the end boundary of the
     * segment checkbox.
     */
    private static class IncludeEndingTagsModel extends SRXOptionsModel {

        IncludeEndingTagsModel(SRX srx) {
            super(srx);
        }

        public void setSelected(boolean selected) {
            srx.setIncludeEndingTags(selected);
            super.setSelected(selected);
        }

        public boolean isSelected() {
            return srx.isIncludeEndingTags();
        }

    }

    /**
     * a model for including ending formatting tags at the end boundary of the
     * segment checkbox.
     */
    public static ButtonModel getIncludeEndingTagsModel(SRX srx) {
        return new IncludeEndingTagsModel(srx);
    }

    /**
     * a model for including isolated formatting tags at the end boundary of the
     * segment checkbox.
     */
    private static class IncludeIsolatedTagsModel extends SRXOptionsModel {

        IncludeIsolatedTagsModel(SRX srx) {
            super(srx);
        }

        public void setSelected(boolean selected) {
            srx.setIncludeIsolatedTags(selected);
            super.setSelected(selected);
        }

        public boolean isSelected() {
            return srx.isIncludeIsolatedTags();
        }

    }

    /**
     * a model for including isolated formatting tags at the end boundary of the
     * segment checkbox.
     */
    public static ButtonModel getIncludeIsolatedTagsModel(SRX srx) {
        return new IncludeIsolatedTagsModel(srx);
    }

}
