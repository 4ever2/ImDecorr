package com.github.imdecorr;


import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import java.awt.BorderLayout;

import org.scijava.command.Command;
import org.scijava.plugin.Plugin;

@Plugin(type = Command.class, headless = false, menuPath = "Help>About Plugins>Decorrelation Analysis...")
public class About implements Command {

    @Override
    public void run() {
        showAbout();
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
        JScrollPane scrPane = new JScrollPane(tf);
        frame.add(scrPane, BorderLayout.CENTER);
        frame.setSize(500, 650);
        frame.setVisible(true);
    }
}
