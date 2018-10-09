/* Code provided on stackoverflow.com by IvanRF */

package com.kquallis.gastimate;

	import java.awt.Component;
	import java.util.ArrayList;

	import javax.swing.DefaultListCellRenderer;
	import javax.swing.JComponent;
	import javax.swing.JList;

	public class ComboboxToolTipRenderer extends DefaultListCellRenderer {
	    /**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		ArrayList<String> tooltips;

	    @Override
	    public Component getListCellRendererComponent(JList list, Object value,
	                        int index, boolean isSelected, boolean cellHasFocus) {

	        JComponent comp = (JComponent) super.getListCellRendererComponent(list,
	                value, index, isSelected, cellHasFocus);

	        if (-1 < index && null != value && null != tooltips) {
	            list.setToolTipText(tooltips.get(index));
	        }
	        return comp;
	    }

	    public void setTooltips(ArrayList<String> tooltips) {
	        this.tooltips = tooltips;
	    }
	}
