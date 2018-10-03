package com.kquallis.gastimate;

import javax.swing.SwingUtilities;

/*
 * Kadeem Quallis
 * 3/15/18ß
*/
public class GastimateAppNew {
	public static void main(String [] args){
		
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				new GastimateGUI();
			}
		}
		);				
	}
}
