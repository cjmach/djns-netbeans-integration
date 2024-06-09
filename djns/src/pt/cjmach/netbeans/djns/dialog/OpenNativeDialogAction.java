/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/NetBeansModuleDevelopment-files/actionListener.java to edit this template
 */
package pt.cjmach.netbeans.djns.dialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

@ActionID(
        category = "Tools",
        id = "pt.cjmach.netbeans.djns.dialog.OpenNativeDialogAction"
)
@ActionRegistration(
        displayName = "#CTL_OpenNativeDialogAction"
)
@ActionReference(path = "Menu/Tools", position = 0)
@Messages("CTL_OpenNativeDialogAction=Open Dialog...")
public final class OpenNativeDialogAction implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        NativeDialogBuilder builder = new NativeDialogBuilder(OpenNativeDialogAction.class)
                .setTitle("Testing 123...")
                .setAcceptAllFileFilterUsed(true)
                .setFileFilter(new FileNameExtensionFilter("Image files", "jpg", "gif", "png"));
        File file = builder.showSaveDialog();
        System.out.println(file);
    }
}
