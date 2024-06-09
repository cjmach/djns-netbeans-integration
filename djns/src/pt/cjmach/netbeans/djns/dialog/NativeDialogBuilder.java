/*
 * Copyright (C) 2024  Carlos Machado
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package pt.cjmach.netbeans.djns.dialog;

import chrriis.dj.nativeswing.swtimpl.components.JDirectoryDialog;
import chrriis.dj.nativeswing.swtimpl.components.JFileDialog;
import java.awt.Component;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.openide.util.NbPreferences;
import org.openide.windows.WindowManager;

/**
 *
 * @author cmachado
 */
public class NativeDialogBuilder {

    private final String key;
    private final DialogType type;
    private String title;
    private File defaultWorkingDir;
    private String description;
    // TODO: Allow to add filters
    private final List<FileNameExtensionFilter> filters;
    private FileNameExtensionFilter filter;
    private boolean acceptAllFilesFilter;

    public NativeDialogBuilder(Class<?> key) {
        this(key, DialogType.FILE);
    }

    public NativeDialogBuilder(Class<?> key, DialogType type) {
        this(key.getName(), type);
    }

    public NativeDialogBuilder(String key, DialogType type) {
        Objects.requireNonNull(key, "key");
        this.key = key;
        this.type = type;
        this.filters = new ArrayList<>();
    }

    public NativeDialogBuilder addFileFilter(FileNameExtensionFilter filter) {
        Objects.requireNonNull(filter, "filter");
        filters.add(filter);
        return this;
    }

    private String getWorkingDirectory() {
        return NbPreferences.forModule(NativeDialogBuilder.class).get(key + ".workingdir", null);
    }

    private void setWorkingDirectory(String dir) {
        NbPreferences.forModule(NativeDialogBuilder.class).put(key + ".workingdir", dir);
    }

    public NativeDialogBuilder setAcceptAllFileFilterUsed(boolean accept) {
        acceptAllFilesFilter = accept;
        return this;
    }

    public NativeDialogBuilder setDefaultWorkingDirectory(File dir) {
        defaultWorkingDir = dir;
        return this;
    }

    public NativeDialogBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    public NativeDialogBuilder setFileFilter(FileNameExtensionFilter filter) {
        this.filter = filter;
        return this;
    }

    public NativeDialogBuilder setTitle(String title) {
        this.title = title;
        return this;
    }

    public File showOpenDialog() {
        return showOpenDialog(WindowManager.getDefault().getMainWindow());
    }

    public File showOpenDialog(Component parent) {
        switch (type) {
            case FILE:
                JFileDialog fileDialog = createFileDialog(
                        JFileDialog.DialogType.OPEN_DIALOG_TYPE,
                        JFileDialog.SelectionMode.SINGLE_SELECTION);
                fileDialog.show(parent);
                String selectedFileName = fileDialog.getSelectedFileName();
                if (selectedFileName == null) {
                    return null;
                }
                String parentDir = fileDialog.getParentDirectory();
                setWorkingDirectory(parentDir);
                return new File(parentDir, selectedFileName);

            case DIRECTORY:
                JDirectoryDialog directoryDialog = createDirectoryDialog();
                directoryDialog.show(parent);
                String selectedDir = directoryDialog.getSelectedDirectory();
                if (selectedDir == null) {
                    return null;
                }
                setWorkingDirectory(selectedDir);
                return new File(selectedDir);
        }
        return null; // should not happen
    }

    public File[] showMultiOpenDialog() {
        return showMultiOpenDialog(WindowManager.getDefault().getMainWindow());
    }

    public File[] showMultiOpenDialog(Component parent) {
        // ignores DialogType
        JFileDialog dialog = createFileDialog(
                JFileDialog.DialogType.OPEN_DIALOG_TYPE,
                JFileDialog.SelectionMode.MULTIPLE_SELECTION);
        dialog.show(parent);
        String[] selectedFileNames = dialog.getSelectedFileNames();
        if (selectedFileNames == null) {
            return null;
        }
        String parentDir = dialog.getParentDirectory();
        setWorkingDirectory(parentDir);

        File[] result = new File[selectedFileNames.length];
        for (int i = 0; i < selectedFileNames.length; i++) {
            String fileName = selectedFileNames[i];
            result[i] = new File(parentDir, fileName);
        }
        return result;
    }

    public File showSaveDialog() {
        return showSaveDialog(WindowManager.getDefault().getMainWindow());
    }

    public File showSaveDialog(Component parent) {
        // ignores DialogType
        JFileDialog dialog = createFileDialog(
                JFileDialog.DialogType.SAVE_DIALOG_TYPE,
                JFileDialog.SelectionMode.MULTIPLE_SELECTION);
        dialog.setConfirmedOverwrite(true);
        dialog.show(parent);
        String selectedFileName = dialog.getSelectedFileName();
        if (selectedFileName == null) {
            return null;
        }
        String parentDir = dialog.getParentDirectory();
        setWorkingDirectory(parentDir);
        return new File(parentDir, selectedFileName);
    }

    private JDirectoryDialog createDirectoryDialog() {
        JDirectoryDialog dialog = new JDirectoryDialog();
        dialog.setTitle(title);
        dialog.setMessage(description);
        String path = getWorkingDirectory();
        if (path != null) {
            dialog.setSelectedDirectory(path);
        } else if (defaultWorkingDir != null && defaultWorkingDir.isDirectory()) {
            dialog.setSelectedDirectory(defaultWorkingDir.getAbsolutePath());
        }
        return dialog;
    }

    private JFileDialog createFileDialog(JFileDialog.DialogType dialogType, JFileDialog.SelectionMode selectionMode) {
        JFileDialog dialog = new JFileDialog();
        dialog.setDialogType(dialogType);
        dialog.setSelectionMode(selectionMode);
        dialog.setTitle(title);
        setupFilters(dialog);

        String path = getWorkingDirectory();
        if (path != null) {
            dialog.setParentDirectory(path);
        } else if (defaultWorkingDir != null && defaultWorkingDir.isDirectory()) {
            dialog.setParentDirectory(defaultWorkingDir.getAbsolutePath());
        }
        return dialog;
    }

    private void setupFilters(JFileDialog dialog) {
        ArrayList<String> extensionFilters = new ArrayList<>();
        ArrayList<String> extensionFilterNames = new ArrayList<>();
        int selectedFilter = -1;
        for (int i = 0; i < filters.size(); i++) {
            FileNameExtensionFilter extensionFilter = filters.get(i);
            extensionFilters.add(getExtensionFilters(extensionFilter));
            extensionFilterNames.add(extensionFilter.getDescription());
            if (extensionFilter == filter) {
                selectedFilter = i;
            }
        }
        if (selectedFilter < 0) {
            if (filter == null) {
                selectedFilter = 0;
            } else { // filters list does not contain filter
                String extensionFilter = getExtensionFilters(filter);
                String extensionFilterName = filter.getDescription();
                selectedFilter = filters.size();
                extensionFilters.add(extensionFilter);
                extensionFilterNames.add(extensionFilterName);
            }
        }
        if (acceptAllFilesFilter) {
            extensionFilters.add("*");
            extensionFilterNames.add(UIManager.getString("FileChooser.acceptAllFileFilterText"));
        }
        if (!extensionFilters.isEmpty()) {
            dialog.setExtensionFilters(
                    extensionFilters.toArray(String[]::new),
                    extensionFilterNames.toArray(String[]::new),
                    selectedFilter);
        }
    }

    private String getExtensionFilters(FileNameExtensionFilter extensionFilter) {
        String[] extensions = extensionFilter.getExtensions();
        for (int j = 0; j < extensions.length; j++) {
            extensions[j] = "*." + extensions[j];
        }
        return String.join(";", extensions);
    }
}
