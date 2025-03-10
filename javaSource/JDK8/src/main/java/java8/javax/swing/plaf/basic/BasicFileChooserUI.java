/*
 * Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */

package java8.javax.swing.plaf.basic;

import javax.swing.*;
import javax.swing.filechooser.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.event.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.BasicDirectoryModel;
import javax.swing.plaf.basic.BasicTransferable;
import java.awt.*;
import java.awt.event.*;
import java.awt.datatransfer.*;
import java.beans.*;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.regex.*;
import sun.awt.shell.ShellFolder;
import sun.swing.*;
import sun.swing.SwingUtilities2;

/**
 * Basic L&amp;F implementation of a FileChooser.
 *
 * @author Jeff Dinkins
 */
public class BasicFileChooserUI extends FileChooserUI {

    /* FileView icons */
    protected Icon directoryIcon = null;
    protected Icon fileIcon = null;
    protected Icon computerIcon = null;
    protected Icon hardDriveIcon = null;
    protected Icon floppyDriveIcon = null;

    protected Icon newFolderIcon = null;
    protected Icon upFolderIcon = null;
    protected Icon homeFolderIcon = null;
    protected Icon listViewIcon = null;
    protected Icon detailsViewIcon = null;
    protected Icon viewMenuIcon = null;

    protected int saveButtonMnemonic = 0;
    protected int openButtonMnemonic = 0;
    protected int cancelButtonMnemonic = 0;
    protected int updateButtonMnemonic = 0;
    protected int helpButtonMnemonic = 0;

    /**
     * The mnemonic keycode used for the approve button when a directory
     * is selected and the current selection mode is FILES_ONLY.
     *
     * @since 1.4
     */
    protected int directoryOpenButtonMnemonic = 0;

    protected String saveButtonText = null;
    protected String openButtonText = null;
    protected String cancelButtonText = null;
    protected String updateButtonText = null;
    protected String helpButtonText = null;

    /**
     * The label text displayed on the approve button when a directory
     * is selected and the current selection mode is FILES_ONLY.
     *
     * @since 1.4
     */
    protected String directoryOpenButtonText = null;

    private String openDialogTitleText = null;
    private String saveDialogTitleText = null;

    protected String saveButtonToolTipText = null;
    protected String openButtonToolTipText = null;
    protected String cancelButtonToolTipText = null;
    protected String updateButtonToolTipText = null;
    protected String helpButtonToolTipText = null;

    /**
     * The tooltip text displayed on the approve button when a directory
     * is selected and the current selection mode is FILES_ONLY.
     *
     * @since 1.4
     */
    protected String directoryOpenButtonToolTipText = null;

    // Some generic FileChooser functions
    private Action approveSelectionAction = new ApproveSelectionAction();
    private Action cancelSelectionAction = new CancelSelectionAction();
    private Action updateAction = new UpdateAction();
    private Action newFolderAction;
    private Action goHomeAction = new GoHomeAction();
    private Action changeToParentDirectoryAction = new ChangeToParentDirectoryAction();

    private String newFolderErrorSeparator = null;
    private String newFolderErrorText = null;
    private String newFolderParentDoesntExistTitleText = null;
    private String newFolderParentDoesntExistText = null;
    private String fileDescriptionText = null;
    private String directoryDescriptionText = null;

    private JFileChooser filechooser = null;

    private boolean directorySelected = false;
    private File directory = null;

    private PropertyChangeListener propertyChangeListener = null;
    private AcceptAllFileFilter acceptAllFileFilter = new AcceptAllFileFilter();
    private FileFilter actualFileFilter = null;
    private GlobFilter globFilter = null;
    private BasicDirectoryModel model = null;
    private BasicFileView fileView = new BasicFileView();
    private boolean usesSingleFilePane;
    private boolean readOnly;

    // The accessoryPanel is a container to place the JFileChooser accessory component
    private JPanel accessoryPanel = null;
    private Handler handler;

    /**
     * Creates a {@code BasicFileChooserUI} implementation
     * for the specified component. By default
     * the {@code BasicLookAndFeel} class uses
     * {@code createUI} methods of all basic UIs classes
     * to instantiate UIs.
     *
     * @param c the {@code JFileChooser} which needs a UI
     * @return the {@code BasicFileChooserUI} object
     *
     * @see UIDefaults#getUI(JComponent)
     * @since 1.7
     */
    public static ComponentUI createUI(JComponent c) {
        return new javax.swing.plaf.basic.BasicFileChooserUI((JFileChooser) c);
    }

    public BasicFileChooserUI(JFileChooser b) {
    }

    public void installUI(JComponent c) {
        accessoryPanel = new JPanel(new BorderLayout());
        filechooser = (JFileChooser) c;

        createModel();

        clearIconCache();

        installDefaults(filechooser);
        installComponents(filechooser);
        installListeners(filechooser);
        filechooser.applyComponentOrientation(filechooser.getComponentOrientation());
    }

    public void uninstallUI(JComponent c) {
        uninstallListeners(filechooser);
        uninstallComponents(filechooser);
        uninstallDefaults(filechooser);

        if(accessoryPanel != null) {
            accessoryPanel.removeAll();
        }

        accessoryPanel = null;
        getFileChooser().removeAll();

        handler = null;
    }

    public void installComponents(JFileChooser fc) {
    }

    public void uninstallComponents(JFileChooser fc) {
    }

    protected void installListeners(JFileChooser fc) {
        propertyChangeListener = createPropertyChangeListener(fc);
        if(propertyChangeListener != null) {
            fc.addPropertyChangeListener(propertyChangeListener);
        }
        fc.addPropertyChangeListener(getModel());

        InputMap inputMap = getInputMap(JComponent.
                                        WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        SwingUtilities.replaceUIInputMap(fc, JComponent.
                                         WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, inputMap);
        ActionMap actionMap = getActionMap();
        SwingUtilities.replaceUIActionMap(fc, actionMap);
    }

    InputMap getInputMap(int condition) {
        if (condition == JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT) {
            return (InputMap)DefaultLookup.get(getFileChooser(), this,
                    "FileChooser.ancestorInputMap");
        }
        return null;
    }

    ActionMap getActionMap() {
        return createActionMap();
    }

    ActionMap createActionMap() {
        ActionMap map = new ActionMapUIResource();

        Action refreshAction = new UIAction(FilePane.ACTION_REFRESH) {
            public void actionPerformed(ActionEvent evt) {
                getFileChooser().rescanCurrentDirectory();
            }
        };

        map.put(FilePane.ACTION_APPROVE_SELECTION, getApproveSelectionAction());
        map.put(FilePane.ACTION_CANCEL, getCancelSelectionAction());
        map.put(FilePane.ACTION_REFRESH, refreshAction);
        map.put(FilePane.ACTION_CHANGE_TO_PARENT_DIRECTORY,
                getChangeToParentDirectoryAction());
        return map;
    }


    protected void uninstallListeners(JFileChooser fc) {
        if(propertyChangeListener != null) {
            fc.removePropertyChangeListener(propertyChangeListener);
        }
        fc.removePropertyChangeListener(getModel());
        SwingUtilities.replaceUIInputMap(fc, JComponent.
                                         WHEN_ANCESTOR_OF_FOCUSED_COMPONENT, null);
        SwingUtilities.replaceUIActionMap(fc, null);
    }


    protected void installDefaults(JFileChooser fc) {
        installIcons(fc);
        installStrings(fc);
        usesSingleFilePane = UIManager.getBoolean("FileChooser.usesSingleFilePane");
        readOnly           = UIManager.getBoolean("FileChooser.readOnly");
        TransferHandler th = fc.getTransferHandler();
        if (th == null || th instanceof UIResource) {
            fc.setTransferHandler(defaultTransferHandler);
        }
        LookAndFeel.installProperty(fc, "opaque", Boolean.FALSE);
    }

    protected void installIcons(JFileChooser fc) {
        directoryIcon    = UIManager.getIcon("FileView.directoryIcon");
        fileIcon         = UIManager.getIcon("FileView.fileIcon");
        computerIcon     = UIManager.getIcon("FileView.computerIcon");
        hardDriveIcon    = UIManager.getIcon("FileView.hardDriveIcon");
        floppyDriveIcon  = UIManager.getIcon("FileView.floppyDriveIcon");

        newFolderIcon    = UIManager.getIcon("FileChooser.newFolderIcon");
        upFolderIcon     = UIManager.getIcon("FileChooser.upFolderIcon");
        homeFolderIcon   = UIManager.getIcon("FileChooser.homeFolderIcon");
        detailsViewIcon  = UIManager.getIcon("FileChooser.detailsViewIcon");
        listViewIcon     = UIManager.getIcon("FileChooser.listViewIcon");
        viewMenuIcon     = UIManager.getIcon("FileChooser.viewMenuIcon");
    }

    protected void installStrings(JFileChooser fc) {

        Locale l = fc.getLocale();
        newFolderErrorText = UIManager.getString("FileChooser.newFolderErrorText",l);
        newFolderErrorSeparator = UIManager.getString("FileChooser.newFolderErrorSeparator",l);

        newFolderParentDoesntExistTitleText = UIManager.getString("FileChooser.newFolderParentDoesntExistTitleText", l);
        newFolderParentDoesntExistText = UIManager.getString("FileChooser.newFolderParentDoesntExistText", l);

        fileDescriptionText = UIManager.getString("FileChooser.fileDescriptionText",l);
        directoryDescriptionText = UIManager.getString("FileChooser.directoryDescriptionText",l);

        saveButtonText   = UIManager.getString("FileChooser.saveButtonText",l);
        openButtonText   = UIManager.getString("FileChooser.openButtonText",l);
        saveDialogTitleText = UIManager.getString("FileChooser.saveDialogTitleText",l);
        openDialogTitleText = UIManager.getString("FileChooser.openDialogTitleText",l);
        cancelButtonText = UIManager.getString("FileChooser.cancelButtonText",l);
        updateButtonText = UIManager.getString("FileChooser.updateButtonText",l);
        helpButtonText   = UIManager.getString("FileChooser.helpButtonText",l);
        directoryOpenButtonText = UIManager.getString("FileChooser.directoryOpenButtonText",l);

        saveButtonMnemonic   = getMnemonic("FileChooser.saveButtonMnemonic", l);
        openButtonMnemonic   = getMnemonic("FileChooser.openButtonMnemonic", l);
        cancelButtonMnemonic = getMnemonic("FileChooser.cancelButtonMnemonic", l);
        updateButtonMnemonic = getMnemonic("FileChooser.updateButtonMnemonic", l);
        helpButtonMnemonic   = getMnemonic("FileChooser.helpButtonMnemonic", l);
        directoryOpenButtonMnemonic = getMnemonic("FileChooser.directoryOpenButtonMnemonic", l);

        saveButtonToolTipText   = UIManager.getString("FileChooser.saveButtonToolTipText",l);
        openButtonToolTipText   = UIManager.getString("FileChooser.openButtonToolTipText",l);
        cancelButtonToolTipText = UIManager.getString("FileChooser.cancelButtonToolTipText",l);
        updateButtonToolTipText = UIManager.getString("FileChooser.updateButtonToolTipText",l);
        helpButtonToolTipText   = UIManager.getString("FileChooser.helpButtonToolTipText",l);
        directoryOpenButtonToolTipText = UIManager.getString("FileChooser.directoryOpenButtonToolTipText",l);
    }

    protected void uninstallDefaults(JFileChooser fc) {
        uninstallIcons(fc);
        uninstallStrings(fc);
        if (fc.getTransferHandler() instanceof UIResource) {
            fc.setTransferHandler(null);
        }
    }

    protected void uninstallIcons(JFileChooser fc) {
        directoryIcon    = null;
        fileIcon         = null;
        computerIcon     = null;
        hardDriveIcon    = null;
        floppyDriveIcon  = null;

        newFolderIcon    = null;
        upFolderIcon     = null;
        homeFolderIcon   = null;
        detailsViewIcon  = null;
        listViewIcon     = null;
        viewMenuIcon     = null;
    }

    protected void uninstallStrings(JFileChooser fc) {
        saveButtonText   = null;
        openButtonText   = null;
        cancelButtonText = null;
        updateButtonText = null;
        helpButtonText   = null;
        directoryOpenButtonText = null;

        saveButtonToolTipText = null;
        openButtonToolTipText = null;
        cancelButtonToolTipText = null;
        updateButtonToolTipText = null;
        helpButtonToolTipText = null;
        directoryOpenButtonToolTipText = null;
    }

    protected void createModel() {
        if (model != null) {
            model.invalidateFileCache();
        }
        model = new BasicDirectoryModel(getFileChooser());
    }

    public BasicDirectoryModel getModel() {
        return model;
    }

    public PropertyChangeListener createPropertyChangeListener(JFileChooser fc) {
        return null;
    }

    public String getFileName() {
        return null;
    }

    public String getDirectoryName() {
        return null;
    }

    public void setFileName(String filename) {
    }

    public void setDirectoryName(String dirname) {
    }

    public void rescanCurrentDirectory(JFileChooser fc) {
    }

    public void ensureFileIsVisible(JFileChooser fc, File f) {
    }

    public JFileChooser getFileChooser() {
        return filechooser;
    }

    public JPanel getAccessoryPanel() {
        return accessoryPanel;
    }

    protected JButton getApproveButton(JFileChooser fc) {
        return null;
    }

    public JButton getDefaultButton(JFileChooser fc) {
        return getApproveButton(fc);
    }

    public String getApproveButtonToolTipText(JFileChooser fc) {
        String tooltipText = fc.getApproveButtonToolTipText();
        if(tooltipText != null) {
            return tooltipText;
        }

        if(fc.getDialogType() == JFileChooser.OPEN_DIALOG) {
            return openButtonToolTipText;
        } else if(fc.getDialogType() == JFileChooser.SAVE_DIALOG) {
            return saveButtonToolTipText;
        }
        return null;
    }

    public void clearIconCache() {
        fileView.clearIconCache();
    }


    // ********************************************
    // ************ Create Listeners **************
    // ********************************************

    private Handler getHandler() {
        if (handler == null) {
            handler = new Handler();
        }
        return handler;
    }

    protected MouseListener createDoubleClickListener(JFileChooser fc,
                                                      JList list) {
        return new Handler(list);
    }

    public ListSelectionListener createListSelectionListener(JFileChooser fc) {
        return getHandler();
    }

    private class Handler implements MouseListener, ListSelectionListener {
        JList list;

        Handler() {
        }

        Handler(JList list) {
            this.list = list;
        }

        public void mouseClicked(MouseEvent evt) {
            // Note: we can't depend on evt.getSource() because of backward
            // compatibility
            if (list != null &&
                SwingUtilities.isLeftMouseButton(evt) &&
                (evt.getClickCount()%2 == 0)) {

                int index = SwingUtilities2.loc2IndexFileList(list, evt.getPoint());
                if (index >= 0) {
                    File f = (File)list.getModel().getElementAt(index);
                    try {
                        // Strip trailing ".."
                        f = ShellFolder.getNormalizedFile(f);
                    } catch (IOException ex) {
                        // That's ok, we'll use f as is
                    }
                    if(getFileChooser().isTraversable(f)) {
                        list.clearSelection();
                        changeDirectory(f);
                    } else {
                        getFileChooser().approveSelection();
                    }
                }
            }
        }

        public void mouseEntered(MouseEvent evt) {
            if (list != null) {
                TransferHandler th1 = getFileChooser().getTransferHandler();
                TransferHandler th2 = list.getTransferHandler();
                if (th1 != th2) {
                    list.setTransferHandler(th1);
                }
                if (getFileChooser().getDragEnabled() != list.getDragEnabled()) {
                    list.setDragEnabled(getFileChooser().getDragEnabled());
                }
            }
        }

        public void mouseExited(MouseEvent evt) {
        }

        public void mousePressed(MouseEvent evt) {
        }

        public void mouseReleased(MouseEvent evt) {
        }

        public void valueChanged(ListSelectionEvent evt) {
            if(!evt.getValueIsAdjusting()) {
                JFileChooser chooser = getFileChooser();
                FileSystemView fsv = chooser.getFileSystemView();
                JList list = (JList)evt.getSource();

                int fsm = chooser.getFileSelectionMode();
                boolean useSetDirectory = usesSingleFilePane &&
                                          (fsm == JFileChooser.FILES_ONLY);

                if (chooser.isMultiSelectionEnabled()) {
                    File[] files = null;
                    Object[] objects = list.getSelectedValues();
                    if (objects != null) {
                        if (objects.length == 1
                            && ((File)objects[0]).isDirectory()
                            && chooser.isTraversable(((File)objects[0]))
                            && (useSetDirectory || !fsv.isFileSystem(((File)objects[0])))) {
                            setDirectorySelected(true);
                            setDirectory(((File)objects[0]));
                        } else {
                            ArrayList<File> fList = new ArrayList<File>(objects.length);
                            for (Object object : objects) {
                                File f = (File) object;
                                boolean isDir = f.isDirectory();
                                if ((chooser.isFileSelectionEnabled() && !isDir)
                                    || (chooser.isDirectorySelectionEnabled()
                                        && fsv.isFileSystem(f)
                                        && isDir)) {
                                    fList.add(f);
                                }
                            }
                            if (fList.size() > 0) {
                                files = fList.toArray(new File[fList.size()]);
                            }
                            setDirectorySelected(false);
                        }
                    }
                    chooser.setSelectedFiles(files);
                } else {
                    File file = (File)list.getSelectedValue();
                    if (file != null
                        && file.isDirectory()
                        && chooser.isTraversable(file)
                        && (useSetDirectory || !fsv.isFileSystem(file))) {

                        setDirectorySelected(true);
                        setDirectory(file);
                        if (usesSingleFilePane) {
                            chooser.setSelectedFile(null);
                        }
                    } else {
                        setDirectorySelected(false);
                        if (file != null) {
                            chooser.setSelectedFile(file);
                        }
                    }
                }
            }
        }
    }

    protected class DoubleClickListener extends MouseAdapter {
        // NOTE: This class exists only for backward compatibility. All
        // its functionality has been moved into Handler. If you need to add
        // new functionality add it to the Handler, but make sure this
        // class calls into the Handler.
        Handler handler;
        public  DoubleClickListener(JList list) {
            handler = new Handler(list);
        }

        /**
         * The JList used for representing the files is created by subclasses, but the
         * selection is monitored in this class.  The TransferHandler installed in the
         * JFileChooser is also installed in the file list as it is used as the actual
         * transfer source.  The list is updated on a mouse enter to reflect the current
         * data transfer state of the file chooser.
         */
        public void mouseEntered(MouseEvent e) {
            handler.mouseEntered(e);
        }

        public void mouseClicked(MouseEvent e) {
            handler.mouseClicked(e);
        }
    }

    protected class SelectionListener implements ListSelectionListener {
        // NOTE: This class exists only for backward compatibility. All
        // its functionality has been moved into Handler. If you need to add
        // new functionality add it to the Handler, but make sure this
        // class calls into the Handler.
        public void valueChanged(ListSelectionEvent e) {
            getHandler().valueChanged(e);
        }
    }

    /**
     * Property to remember whether a directory is currently selected in the UI.
     *
     * @return <code>true</code> iff a directory is currently selected.
     * @since 1.4
     */
    protected boolean isDirectorySelected() {
        return directorySelected;
    }

    /**
     * Property to remember whether a directory is currently selected in the UI.
     * This is normally called by the UI on a selection event.
     *
     * @param b iff a directory is currently selected.
     * @since 1.4
     */
    protected void setDirectorySelected(boolean b) {
        directorySelected = b;
    }

    /**
     * Property to remember the directory that is currently selected in the UI.
     *
     * @return the value of the <code>directory</code> property
     * @see #setDirectory
     * @since 1.4
     */
    protected File getDirectory() {
        return directory;
    }

    /**
     * Property to remember the directory that is currently selected in the UI.
     * This is normally called by the UI on a selection event.
     *
     * @param f the <code>File</code> object representing the directory that is
     *          currently selected
     * @since 1.4
     */
    protected void setDirectory(File f) {
        directory = f;
    }

    /**
     * Returns the mnemonic for the given key.
     */
    private int getMnemonic(String key, Locale l) {
        return SwingUtilities2.getUIDefaultsInt(key, l);
    }

    // *******************************************************
    // ************ FileChooser UI PLAF methods **************
    // *******************************************************

    /**
     * Returns the default accept all file filter
     */
    public FileFilter getAcceptAllFileFilter(JFileChooser fc) {
        return acceptAllFileFilter;
    }


    public FileView getFileView(JFileChooser fc) {
        return fileView;
    }


    /**
     * Returns the title of this dialog
     */
    public String getDialogTitle(JFileChooser fc) {
        String dialogTitle = fc.getDialogTitle();
        if (dialogTitle != null) {
            return dialogTitle;
        } else if (fc.getDialogType() == JFileChooser.OPEN_DIALOG) {
            return openDialogTitleText;
        } else if (fc.getDialogType() == JFileChooser.SAVE_DIALOG) {
            return saveDialogTitleText;
        } else {
            return getApproveButtonText(fc);
        }
    }


    public int getApproveButtonMnemonic(JFileChooser fc) {
        int mnemonic = fc.getApproveButtonMnemonic();
        if (mnemonic > 0) {
            return mnemonic;
        } else if (fc.getDialogType() == JFileChooser.OPEN_DIALOG) {
            return openButtonMnemonic;
        } else if (fc.getDialogType() == JFileChooser.SAVE_DIALOG) {
            return saveButtonMnemonic;
        } else {
            return mnemonic;
        }
    }

    public String getApproveButtonText(JFileChooser fc) {
        String buttonText = fc.getApproveButtonText();
        if (buttonText != null) {
            return buttonText;
        } else if (fc.getDialogType() == JFileChooser.OPEN_DIALOG) {
            return openButtonText;
        } else if (fc.getDialogType() == JFileChooser.SAVE_DIALOG) {
            return saveButtonText;
        } else {
            return null;
        }
    }


    // *****************************
    // ***** Directory Actions *****
    // *****************************

    public Action getNewFolderAction() {
        if (newFolderAction == null) {
            newFolderAction = new NewFolderAction();
            // Note: Don't return null for readOnly, it might
            // break older apps.
            if (readOnly) {
                newFolderAction.setEnabled(false);
            }
        }
        return newFolderAction;
    }

    public Action getGoHomeAction() {
        return goHomeAction;
    }

    public Action getChangeToParentDirectoryAction() {
        return changeToParentDirectoryAction;
    }

    public Action getApproveSelectionAction() {
        return approveSelectionAction;
    }

    public Action getCancelSelectionAction() {
        return cancelSelectionAction;
    }

    public Action getUpdateAction() {
        return updateAction;
    }


    /**
     * Creates a new folder.
     */
    protected class NewFolderAction extends AbstractAction {
        protected NewFolderAction() {
            super(FilePane.ACTION_NEW_FOLDER);
        }
        public void actionPerformed(ActionEvent e) {
            if (readOnly) {
                return;
            }
            JFileChooser fc = getFileChooser();
            File currentDirectory = fc.getCurrentDirectory();

            if (!currentDirectory.exists()) {
                JOptionPane.showMessageDialog(
                    fc,
                    newFolderParentDoesntExistText,
                    newFolderParentDoesntExistTitleText, JOptionPane.WARNING_MESSAGE);
                return;
            }

            File newFolder;
            try {
                newFolder = fc.getFileSystemView().createNewFolder(currentDirectory);
                if (fc.isMultiSelectionEnabled()) {
                    fc.setSelectedFiles(new File[] { newFolder });
                } else {
                    fc.setSelectedFile(newFolder);
                }
            } catch (IOException exc) {
                JOptionPane.showMessageDialog(
                    fc,
                    newFolderErrorText + newFolderErrorSeparator + exc,
                    newFolderErrorText, JOptionPane.ERROR_MESSAGE);
                return;
            }

            fc.rescanCurrentDirectory();
        }
    }

    /**
     * Acts on the "home" key event or equivalent event.
     */
    protected class GoHomeAction extends AbstractAction {
        protected GoHomeAction() {
            super("Go Home");
        }
        public void actionPerformed(ActionEvent e) {
            JFileChooser fc = getFileChooser();
            changeDirectory(fc.getFileSystemView().getHomeDirectory());
        }
    }

    protected class ChangeToParentDirectoryAction extends AbstractAction {
        protected ChangeToParentDirectoryAction() {
            super("Go Up");
            putValue(Action.ACTION_COMMAND_KEY, FilePane.ACTION_CHANGE_TO_PARENT_DIRECTORY);
        }
        public void actionPerformed(ActionEvent e) {
            getFileChooser().changeToParentDirectory();
        }
    }

    /**
     * Responds to an Open or Save request
     */
    protected class ApproveSelectionAction extends AbstractAction {
        protected ApproveSelectionAction() {
            super(FilePane.ACTION_APPROVE_SELECTION);
        }
        public void actionPerformed(ActionEvent e) {
            if (isDirectorySelected()) {
                File dir = getDirectory();
                if (dir != null) {
                    try {
                        // Strip trailing ".."
                        dir = ShellFolder.getNormalizedFile(dir);
                    } catch (IOException ex) {
                        // Ok, use f as is
                    }
                    changeDirectory(dir);
                    return;
                }
            }

            JFileChooser chooser = getFileChooser();

            String filename = getFileName();
            FileSystemView fs = chooser.getFileSystemView();
            File dir = chooser.getCurrentDirectory();

            if (filename != null) {
                // Remove whitespaces from end of filename
                int i = filename.length() - 1;

                while (i >=0 && filename.charAt(i) <= ' ') {
                    i--;
                }

                filename = filename.substring(0, i + 1);
            }

            if (filename == null || filename.length() == 0) {
                // no file selected, multiple selection off, therefore cancel the approve action
                resetGlobFilter();
                return;
            }

            File selectedFile = null;
            File[] selectedFiles = null;

            // Unix: Resolve '~' to user's home directory
            if (File.separatorChar == '/') {
                if (filename.startsWith("~/")) {
                    filename = System.getProperty("user.home") + filename.substring(1);
                } else if (filename.equals("~")) {
                    filename = System.getProperty("user.home");
                }
            }

            if (chooser.isMultiSelectionEnabled() && filename.length() > 1 &&
                    filename.charAt(0) == '"' && filename.charAt(filename.length() - 1) == '"') {
                List<File> fList = new ArrayList<File>();

                String[] files = filename.substring(1, filename.length() - 1).split("\" \"");
                // Optimize searching files by names in "children" array
                Arrays.sort(files);

                File[] children = null;
                int childIndex = 0;

                for (String str : files) {
                    File file = fs.createFileObject(str);
                    if (!file.isAbsolute()) {
                        if (children == null) {
                            children = fs.getFiles(dir, false);
                            Arrays.sort(children);
                        }
                        for (int k = 0; k < children.length; k++) {
                            int l = (childIndex + k) % children.length;
                            if (children[l].getName().equals(str)) {
                                file = children[l];
                                childIndex = l + 1;
                                break;
                            }
                        }
                    }
                    fList.add(file);
                }

                if (!fList.isEmpty()) {
                    selectedFiles = fList.toArray(new File[fList.size()]);
                }
                resetGlobFilter();
            } else {
                selectedFile = fs.createFileObject(filename);
                if (!selectedFile.isAbsolute()) {
                    selectedFile = fs.getChild(dir, filename);
                }
                // check for wildcard pattern
                FileFilter currentFilter = chooser.getFileFilter();
                if (!selectedFile.exists() && isGlobPattern(filename)) {
                    changeDirectory(selectedFile.getParentFile());
                    if (globFilter == null) {
                        globFilter = new GlobFilter();
                    }
                    try {
                        globFilter.setPattern(selectedFile.getName());
                        if (!(currentFilter instanceof GlobFilter)) {
                            actualFileFilter = currentFilter;
                        }
                        chooser.setFileFilter(null);
                        chooser.setFileFilter(globFilter);
                        return;
                    } catch (PatternSyntaxException pse) {
                        // Not a valid glob pattern. Abandon filter.
                    }
                }

                resetGlobFilter();

                // Check for directory change action
                boolean isDir = (selectedFile != null && selectedFile.isDirectory());
                boolean isTrav = (selectedFile != null && chooser.isTraversable(selectedFile));
                boolean isDirSelEnabled = chooser.isDirectorySelectionEnabled();
                boolean isFileSelEnabled = chooser.isFileSelectionEnabled();
                boolean isCtrl = (e != null && (e.getModifiers() &
                            Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()) != 0);

                if (isDir && isTrav && (isCtrl || !isDirSelEnabled)) {
                    changeDirectory(selectedFile);
                    return;
                } else if ((isDir || !isFileSelEnabled)
                        && (!isDir || !isDirSelEnabled)
                        && (!isDirSelEnabled || selectedFile.exists())) {
                    selectedFile = null;
                }
            }

            if (selectedFiles != null || selectedFile != null) {
                if (selectedFiles != null || chooser.isMultiSelectionEnabled()) {
                    if (selectedFiles == null) {
                        selectedFiles = new File[] { selectedFile };
                    }
                    chooser.setSelectedFiles(selectedFiles);
                    // Do it again. This is a fix for bug 4949273 to force the
                    // selected value in case the ListSelectionModel clears it
                    // for non-existing file names.
                    chooser.setSelectedFiles(selectedFiles);
                } else {
                    chooser.setSelectedFile(selectedFile);
                }
                chooser.approveSelection();
            } else {
                if (chooser.isMultiSelectionEnabled()) {
                    chooser.setSelectedFiles(null);
                } else {
                    chooser.setSelectedFile(null);
                }
                chooser.cancelSelection();
            }
        }
    }


    private void resetGlobFilter() {
        if (actualFileFilter != null) {
            JFileChooser chooser = getFileChooser();
            FileFilter currentFilter = chooser.getFileFilter();
            if (currentFilter != null && currentFilter.equals(globFilter)) {
                chooser.setFileFilter(actualFileFilter);
                chooser.removeChoosableFileFilter(globFilter);
            }
            actualFileFilter = null;
        }
    }

    private static boolean isGlobPattern(String filename) {
        return ((File.separatorChar == '\\' && (filename.indexOf('*') >= 0
                                                  || filename.indexOf('?') >= 0))
                || (File.separatorChar == '/' && (filename.indexOf('*') >= 0
                                                  || filename.indexOf('?') >= 0
                                                  || filename.indexOf('[') >= 0)));
    }


    /* A file filter which accepts file patterns containing
     * the special wildcards *? on Windows and *?[] on Unix.
     */
    class GlobFilter extends FileFilter {
        Pattern pattern;
        String globPattern;

        public void setPattern(String globPattern) {
            char[] gPat = globPattern.toCharArray();
            char[] rPat = new char[gPat.length * 2];
            boolean isWin32 = (File.separatorChar == '\\');
            boolean inBrackets = false;
            int j = 0;

            this.globPattern = globPattern;

            if (isWin32) {
                // On windows, a pattern ending with *.* is equal to ending with *
                int len = gPat.length;
                if (globPattern.endsWith("*.*")) {
                    len -= 2;
                }
                for (int i = 0; i < len; i++) {
                    switch(gPat[i]) {
                      case '*':
                        rPat[j++] = '.';
                        rPat[j++] = '*';
                        break;

                      case '?':
                        rPat[j++] = '.';
                        break;

                      case '\\':
                        rPat[j++] = '\\';
                        rPat[j++] = '\\';
                        break;

                      default:
                        if ("+()^$.{}[]".indexOf(gPat[i]) >= 0) {
                            rPat[j++] = '\\';
                        }
                        rPat[j++] = gPat[i];
                        break;
                    }
                }
            } else {
                for (int i = 0; i < gPat.length; i++) {
                    switch(gPat[i]) {
                      case '*':
                        if (!inBrackets) {
                            rPat[j++] = '.';
                        }
                        rPat[j++] = '*';
                        break;

                      case '?':
                        rPat[j++] = inBrackets ? '?' : '.';
                        break;

                      case '[':
                        inBrackets = true;
                        rPat[j++] = gPat[i];

                        if (i < gPat.length - 1) {
                            switch (gPat[i+1]) {
                              case '!':
                              case '^':
                                rPat[j++] = '^';
                                i++;
                                break;

                              case ']':
                                rPat[j++] = gPat[++i];
                                break;
                            }
                        }
                        break;

                      case ']':
                        rPat[j++] = gPat[i];
                        inBrackets = false;
                        break;

                      case '\\':
                        if (i == 0 && gPat.length > 1 && gPat[1] == '~') {
                            rPat[j++] = gPat[++i];
                        } else {
                            rPat[j++] = '\\';
                            if (i < gPat.length - 1 && "*?[]".indexOf(gPat[i+1]) >= 0) {
                                rPat[j++] = gPat[++i];
                            } else {
                                rPat[j++] = '\\';
                            }
                        }
                        break;

                      default:
                        //if ("+()|^$.{}<>".indexOf(gPat[i]) >= 0) {
                        if (!Character.isLetterOrDigit(gPat[i])) {
                            rPat[j++] = '\\';
                        }
                        rPat[j++] = gPat[i];
                        break;
                    }
                }
            }
            this.pattern = Pattern.compile(new String(rPat, 0, j), Pattern.CASE_INSENSITIVE);
        }

        public boolean accept(File f) {
            if (f == null) {
                return false;
            }
            if (f.isDirectory()) {
                return true;
            }
            return pattern.matcher(f.getName()).matches();
        }

        public String getDescription() {
            return globPattern;
        }
    }

    /**
     * Responds to a cancel request.
     */
    protected class CancelSelectionAction extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
            getFileChooser().cancelSelection();
        }
    }

    /**
     * Rescans the files in the current directory
     */
    protected class UpdateAction extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
            JFileChooser fc = getFileChooser();
            fc.setCurrentDirectory(fc.getFileSystemView().createFileObject(getDirectoryName()));
            fc.rescanCurrentDirectory();
        }
    }


    private void changeDirectory(File dir) {
        JFileChooser fc = getFileChooser();
        // Traverse shortcuts on Windows
        if (dir != null && FilePane.usesShellFolder(fc)) {
            try {
                ShellFolder shellFolder = ShellFolder.getShellFolder(dir);

                if (shellFolder.isLink()) {
                    File linkedTo = shellFolder.getLinkLocation();

                    // If linkedTo is null we try to use dir
                    if (linkedTo != null) {
                        if (fc.isTraversable(linkedTo)) {
                            dir = linkedTo;
                        } else {
                            return;
                        }
                    } else {
                        dir = shellFolder;
                    }
                }
            } catch (FileNotFoundException ex) {
                return;
            }
        }
        fc.setCurrentDirectory(dir);
        if (fc.getFileSelectionMode() == JFileChooser.FILES_AND_DIRECTORIES &&
            fc.getFileSystemView().isFileSystem(dir)) {

            setFileName(dir.getAbsolutePath());
        }
    }


    // *****************************************
    // ***** default AcceptAll file filter *****
    // *****************************************
    protected class AcceptAllFileFilter extends FileFilter {

        public AcceptAllFileFilter() {
        }

        public boolean accept(File f) {
            return true;
        }

        public String getDescription() {
            return UIManager.getString("FileChooser.acceptAllFileFilterText");
        }
    }


    // ***********************
    // * FileView operations *
    // ***********************
    protected class BasicFileView extends FileView {
        /* FileView type descriptions */
        // PENDING(jeff) - pass in the icon cache size
        protected Hashtable<File,Icon> iconCache = new Hashtable<File,Icon>();

        public BasicFileView() {
        }

        public void clearIconCache() {
            iconCache = new Hashtable<File,Icon>();
        }

        public String getName(File f) {
            // Note: Returns display name rather than file name
            String fileName = null;
            if(f != null) {
                fileName = getFileChooser().getFileSystemView().getSystemDisplayName(f);
            }
            return fileName;
        }


        public String getDescription(File f) {
            return f.getName();
        }

        public String getTypeDescription(File f) {
            String type = getFileChooser().getFileSystemView().getSystemTypeDescription(f);
            if (type == null) {
                if (f.isDirectory()) {
                    type = directoryDescriptionText;
                } else {
                    type = fileDescriptionText;
                }
            }
            return type;
        }

        public Icon getCachedIcon(File f) {
            return iconCache.get(f);
        }

        public void cacheIcon(File f, Icon i) {
            if(f == null || i == null) {
                return;
            }
            iconCache.put(f, i);
        }

        public Icon getIcon(File f) {
            Icon icon = getCachedIcon(f);
            if(icon != null) {
                return icon;
            }
            icon = fileIcon;
            if (f != null) {
                FileSystemView fsv = getFileChooser().getFileSystemView();

                if (fsv.isFloppyDrive(f)) {
                    icon = floppyDriveIcon;
                } else if (fsv.isDrive(f)) {
                    icon = hardDriveIcon;
                } else if (fsv.isComputerNode(f)) {
                    icon = computerIcon;
                } else if (f.isDirectory()) {
                    icon = directoryIcon;
                }
            }
            cacheIcon(f, icon);
            return icon;
        }

        public Boolean isHidden(File f) {
            String name = f.getName();
            if(name != null && name.charAt(0) == '.') {
                return Boolean.TRUE;
            } else {
                return Boolean.FALSE;
            }
        }
    }

    private static final TransferHandler defaultTransferHandler = new FileTransferHandler();

    /**
     * Data transfer support for the file chooser.  Since files are currently presented
     * as a list, the list support is reused with the added flavor of DataFlavor.javaFileListFlavor
     */
    static class FileTransferHandler extends TransferHandler implements UIResource {

        /**
         * Create a Transferable to use as the source for a data transfer.
         *
         * @param c  The component holding the data to be transfered.  This
         *  argument is provided to enable sharing of TransferHandlers by
         *  multiple components.
         * @return  The representation of the data to be transfered.
         *
         */
        protected Transferable createTransferable(JComponent c) {
            Object[] values = null;
            if (c instanceof JList) {
                values = ((JList)c).getSelectedValues();
            } else if (c instanceof JTable) {
                JTable table = (JTable)c;
                int[] rows = table.getSelectedRows();
                if (rows != null) {
                    values = new Object[rows.length];
                    for (int i=0; i<rows.length; i++) {
                        values[i] = table.getValueAt(rows[i], 0);
                    }
                }
            }
            if (values == null || values.length == 0) {
                return null;
            }

            StringBuffer plainBuf = new StringBuffer();
            StringBuffer htmlBuf = new StringBuffer();

            htmlBuf.append("<html>\n<body>\n<ul>\n");

            for (Object obj : values) {
                String val = ((obj == null) ? "" : obj.toString());
                plainBuf.append(val + "\n");
                htmlBuf.append("  <li>" + val + "\n");
            }

            // remove the last newline
            plainBuf.deleteCharAt(plainBuf.length() - 1);
            htmlBuf.append("</ul>\n</body>\n</html>");

            return new FileTransferable(plainBuf.toString(), htmlBuf.toString(), values);
        }

        public int getSourceActions(JComponent c) {
            return COPY;
        }

        static class FileTransferable extends javax.swing.plaf.basic.BasicTransferable {

            Object[] fileData;

            FileTransferable(String plainData, String htmlData, Object[] fileData) {
                super(plainData, htmlData);
                this.fileData = fileData;
            }

            /**
             * Best format of the file chooser is DataFlavor.javaFileListFlavor.
             */
            protected DataFlavor[] getRicherFlavors() {
                DataFlavor[] flavors = new DataFlavor[1];
                flavors[0] = DataFlavor.javaFileListFlavor;
                return flavors;
            }

            /**
             * The only richer format supported is the file list flavor
             */
            protected Object getRicherData(DataFlavor flavor) {
                if (DataFlavor.javaFileListFlavor.equals(flavor)) {
                    ArrayList<Object> files = new ArrayList<Object>();
                    for (Object file : this.fileData) {
                        files.add(file);
                    }
                    return files;
                }
                return null;
            }

        }
    }
}
