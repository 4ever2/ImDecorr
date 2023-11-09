/* 
% 
%   ImageDecorrelationAnalysis GUI and input manager
%
% ---------------------------------------
%
% A detailled description of the method can be found in : 
% "Descloux, A., K. S. Grußmayer, and A. Radenovic. "Parameter-free image 
% resolution estimation based on decorrelation analysis."
% Nature methods (2019): 1-7."
%
%   Copyright © 2018 Adrien Descloux - adrien.descloux@epfl.ch, 
%   École Polytechnique Fédérale de Lausanne, LBEN/LOB,
%   BM 5.134, Station 17, 1015 Lausanne, Switzerland.
%
%  	This program is free software: you can redistribute it and/or modify
%  	it under the terms of the GNU General Public License as published by
% 	the Free Software Foundation, either version 3 of the License, or
%  	(at your option) any later version.
%
%  	This program is distributed in the hope that it will be useful,
%  	but WITHOUT ANY WARRANTY; without even the implied warranty of
%  	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
%  	GNU General Public License for more details.
%
% 	You should have received a copy of the GNU General Public License
%  	along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package com.github.imdecorr;


import javax.swing.JFrame;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import java.awt.BorderLayout;
import java.io.File;
import java.io.FilenameFilter;

import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.NonBlockingGenericDialog;
import ij.gui.Roi;
import ij.plugin.PlugIn;

public class Decorrelation_Analysis implements PlugIn {
	private double rMin;
	private double rMax;
	private int nr;
	private int ng;
	private boolean doPlot;
	private boolean batchStack;
	private boolean batchFolder;

	@Override
	public void run(String arg) {
		switch (arg.toLowerCase()) {
			case "about":
				showAbout();
				break;
			case "run":
				runPlugin();
				break;
			default:
				throw new UnsupportedOperationException(String.format("Unimplemented method '%s'", arg));
		}
	}

	private void runPlugin() {
		if (!showDialog())
			return;

		//initDecorrelationAnalysis(rMin, rMax, nr, ng, doPlot, batchFolder, batchStack);
		IJ.log(String.format("%f, %f, %d, %d, %b, %b, %b", rMin, rMax, nr, ng, doPlot, batchStack, batchFolder));
	}

	private boolean showDialog() {
		NonBlockingGenericDialog dialog = new NonBlockingGenericDialog("Image Decorrelation Analysis");
		dialog.addMessage("Settings");

		dialog.addNumericField("Radius_min", 0);
		dialog.addToSameRow();
		dialog.addNumericField("Radius_max", 1);
		dialog.addNumericField("Nr", 50);
		dialog.addToSameRow();
		dialog.addNumericField("Ng", 10);
		dialog.addCheckbox("Do_plot", true);
		dialog.addToSameRow();
		dialog.addCheckbox("Batch_stack", false);
		dialog.addToSameRow();
		dialog.addCheckbox("Batch_folder", false);

		dialog.showDialog();
		if (dialog.wasCanceled())
			return false;

		rMin = dialog.getNextNumber();
		rMax = dialog.getNextNumber();
		nr = (int) dialog.getNextNumber();
		ng = (int) dialog.getNextNumber();
		doPlot = dialog.getNextBoolean();
		batchStack = dialog.getNextBoolean();
		batchFolder = dialog.getNextBoolean();

		return true;
	}
	}

	private String getVersion() {
		return getClass().getPackage().getImplementationVersion();
	}

	private String getPluginName() {
		return getClass().getPackage().getImplementationTitle();
	}

	private void showAbout() {
		String titleText = String.format("<h1> About %s</h1>", getPluginName());
		String versionText = String.format("Version %s <br>", getVersion());
		String aboutText = new String("<html>" +
				titleText +
				"Author : Adrien Descloux <br>" +
				"LBEN STI EPFL Switzerland <br>" +
				versionText +
				"<br />" +
				"A detailled description of the method can be found in : <br />" +
				"Descloux, A., K. S. Grußmayer, and A. Radenovic. Parameter-free image  <br>" +
				"resolution estimation based on decorrelation analysis." +
				"Nature methods (2019): 1-7. <br>" +
				"<br />" +
				"If this software has been usefull to your research, please consider citing <br>" +
				"<a href=\"https://www.nature.com/articles/s41592-019-0515-7#article-info\">https://doi.org/10.1038/s41592-019-0515-7</a> <br>"
				+
				"<br />" +
				"<h2>License</h2>" +
				"Copyright © 2018 Adrien Descloux - adrien.descloux@epfl.ch, <br>" +
				"École Polytechnique Fédérale de Lausanne, LBEN/LOB,<br>" +
				"BM 5.134, Station 17, 1015 Lausanne, Switzerland. <br>" +
				"<br />" +
				"This program is free software: you can redistribute it and/or modify <br>" +
				"it under the terms of the GNU General Public License as published by<br>" +
				"the Free Software Foundation, either version 3 of the License, or<br>" +
				"(at your option) any later version. <br>" +
				"<br />" +
				"This program is distributed in the hope that it will be useful,<br>" +
				"but WITHOUT ANY WARRANTY; without even the implied warranty of<br>" +
				"MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the<br>" +
				"GNU General Public License for more details. <br>" +
				"<br />" +
				"You should have received a copy of the GNU General Public License <br>" +
				"along with this program.  If not, see <a href=\"http://www.gnu.org/licenses/\">http://www.gnu.org/licenses/</a>. </html>");

		JTextPane tf = new JTextPane();
		tf.setEditable(false);
		tf.setBorder(null);
		tf.setForeground(UIManager.getColor("Label.foreground"));
		tf.setFont(UIManager.getFont("Label.font"));
		tf.setContentType("text/html");
		tf.setText(aboutText);
		JFrame frame = new JFrame("About this plugin");
		frame.add(tf, BorderLayout.CENTER);
		frame.setSize(500, 650);
		frame.setVisible(true);
	}

	/**
	 * Main method for debugging.
	 *
	 * For debugging, it is convenient to have a method that starts ImageJ, loads
	 * an image and calls the plugin, e.g. after setting breakpoints.
	 *
	 * @param args unused
	 */
	public static void main(String[] args) throws Exception {
		// set the plugins.dir property to make the plugin appear in the Plugins menu
		// see: https://stackoverflow.com/a/7060464/1207769
		Class<?> clazz = Decorrelation_Analysis.class;
		java.net.URL url = clazz.getProtectionDomain().getCodeSource().getLocation();
		java.io.File file = new java.io.File(url.toURI());
		System.setProperty("plugins.dir", file.getAbsolutePath());

		// start ImageJ
		new ImageJ();

		// run the plugin
		IJ.runPlugIn(clazz.getName(), "");
	}
}
