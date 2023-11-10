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

import java.io.File;
import java.io.FilenameFilter;

import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.Roi;

@Plugin(type = Command.class, headless = true, menuPath = "Plugins>Decorrelation Analysis")
public class Decorrelation_Analysis extends CancelableCommand {

	private static final String RMIN_DESCRIPTION = "Minimum radius [0,rMax] (normalized frequencies) used for decorrelation analysis";
	private static final String RMAX_DESCRIPTION = "Maximum radius [rMin,1] (normalized frequencies) used for decorrelation analysis";
	private static final String NR_DESCRIPTION = "[10,100], Sampling of decorrelation curve";
	private static final String NG_DESCRIPTION = "[5,40], Number of high-pass image analyzed";
	private static final String DO_PLOT_DESCRIPTION = "Plot decorrelation analysis";
	private static final String BATCH_STACK_DESCRIPTION = "Batch process all dimensions of the active image";
	private static final String BATCH_FOLDER_DESCRIPTION = "Batch process all images in folder";

	@Parameter(label = "Radius min :", description = RMIN_DESCRIPTION, min = "0.0", max = "1.0", stepSize = "0.001", validater = "validateRadius")
	private double rMin = 0.0;
	@Parameter(label = "Radius max :", description = RMAX_DESCRIPTION, min = "0.0", max = "1.0", stepSize = "0.001", validater = "validateRadius")
	private double rMax = 1.0;
	@Parameter(label = "Nr :", description = NR_DESCRIPTION, min = "10", validater = "validateNr")
	private int nr = 50;
	@Parameter(label = "Ng :", description = NG_DESCRIPTION, min = "5", validater = "validateNg")
	private int ng = 10;
	@Parameter(label = "Do plot", description = DO_PLOT_DESCRIPTION)
	private boolean doPlot = true;
	@Parameter(label = "Batch stack", description = BATCH_STACK_DESCRIPTION)
	private boolean batchStack = false;
	@Parameter(label = "Batch folder", description = BATCH_FOLDER_DESCRIPTION)
	private boolean batchFolder = false;
	@Parameter(label = "Save path", style = "directory", required = false)
	private File savePath;

	private void validateRadius() {
		if (!Double.isFinite(rMin)) {
			this.cancel("Radius min parameter is not a valid number");
			return;
		}
		if (!Double.isFinite(rMax)) {
			this.cancel("Radius max parameter is not a valid number");
			return;
		}
		if (rMin >= rMax) {
			this.cancel("Radius max must be stricly greater than radius min");
			return;
		}
		if (rMin < 0 || rMin > 1) {
			this.cancel("Radius min must be between 0 and 1");
			return;
		}
		if (rMax < 0 || rMax > 1) {
			this.cancel("Radius max must be between 0 and 1");
			return;
		}
	}

	private void validateNr() {
		if (nr < 10) {
			this.cancel("Nr parameter must be >= 10");
			return;
		}
	}

	private void validateNg() {
		if (ng < 5) {
			this.cancel("Ng parameter must be >= 5");
			return;
		}
	}

	@Override
	public void run() {
		if (this.isCanceled()) {
			return;
		}

		initDecorrelationAnalysis(rMin, rMax, nr, ng, doPlot, batchFolder, batchStack);
	}

	private void initDecorrelationAnalysis(double rmin, double rmax, int Nr, int Ng, boolean doPlot,
			boolean batch, boolean batchStack) {

		// File selection management
		if (batch == false) {
			ImagePlus im = (ImagePlus) ij.WindowManager.getCurrentImage();

			if (im == null) {
				// open file selections tool for images
				IJ.open();
				im = ij.WindowManager.getCurrentImage();
				im.show();
			}

			// check if current image has a rectangle ROI selection
			if (im.getRoi() != null) {
				if (im.getRoi().getType() == Roi.RECTANGLE) {// Rectangle ROI
				} else {
					IJ.log("Non rectangular ROI not supported.\n"
							+ "Deleting ROI and continue analysis.");
					im.deleteRoi();
				}
			}

			if (batchStack == false) {
				// Extract current image
				ImageStack stack = im.getStack();
				ImagePlus imp = new ImagePlus(stack.getSliceLabel(im.getCurrentSlice()),
						stack.getProcessor(im.getCurrentSlice()));
				imp.setCalibration(im.getCalibration());
				imp.setRoi(im.getRoi());
				imp.setTitle(im.getTitle());

				im = imp;
			}

			// Image ready to be processed

			// Create ImageDecorrelationAnalysis object
			ImageDecorrelationAnalysis IDA = new ImageDecorrelationAnalysis(im, rmin,
					rmax, Nr, Ng, doPlot, null);

			// Run the analysis
			IDA.startAnalysis();

		} else { // open selection tool for folder with images in them
			ij.io.DirectoryChooser gd = new ij.io.DirectoryChooser(
					"Please select a directory containing images to be processed.");
			String dirPath = gd.getDirectory();
			File dir = new File(dirPath);
			if (!dir.isDirectory()) {
				this.cancel("Save path must be a directory");
				return;
			}

			String[] files = dir.list(new FilenameFilter() {
				public boolean accept(File dir, String name) {
					int index = name.indexOf('.', name.length() - 5);
					if (index != -1) {
						String ext = name.substring(index, name.length());

						if (ext.equals(".tif") || ext.equals(".tiff") || ext.equals(".png") ||
								ext.equals(".ome") || ext.equals(".bmp"))
							return true;
						else
							return false;
					} else
						return false;
				}
			});

			for (int k = 0; k < files.length; k++) {
				String file = files[k];
				ImagePlus im = IJ.openImage(dirPath + File.separator + file);
				im.show();
				String savePath;
				if (im != null) {
					if (k == files.length - 1)
						savePath = dirPath;
					else
						savePath = null;

					ImageDecorrelationAnalysis IDA = new ImageDecorrelationAnalysis(im, rmin,
							rmax, Nr, Ng, doPlot,
							savePath);
					// Run the analysis
					IDA.startAnalysis();
				}
			}

		}
	}

}
