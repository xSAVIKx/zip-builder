package com.github.xsavikx.zipbuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeroturnaround.zip.ZipUtil;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

public class MainDialog extends JDialog {
    private static final File CURRENT_FOLDER = new File("./");
    private final Configuration configuration;
    private JPanel contentPane;
    private JButton chooseFolderButton;
    private JButton createZipButton;
    private File directoryToZip;

    public MainDialog(Configuration configuration) {
        log().info("Creating main dialog");
        this.configuration = configuration;
        initLastFolderFromConfiguration();
        setContentPane(contentPane);
        setModalityType(DEFAULT_MODALITY_TYPE);
        setResizable(false);
        getRootPane().setDefaultButton(chooseFolderButton);

        chooseFolderButton.addActionListener(e -> chooseFolder());

        createZipButton.addActionListener(e -> createZip());
        createZipButton.setEnabled(false);

        // call createZip() when cross is clicked
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onClose();
            }
        });

        // call onClose() on ESCAPE
        contentPane.registerKeyboardAction(e -> onClose(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void initLastFolderFromConfiguration() {
        String lastFolder = configuration.getLastFolder();
        if (lastFolder != null) {
            File directoryToZip = new File(lastFolder);
            if (directoryToZip.exists()) {
                this.directoryToZip = directoryToZip;
            }
        }
    }

    private void chooseFolder() {
        JFileChooser jFileChooser = new JFileChooser();
        jFileChooser.setDialogTitle("Select folder to archive.");
        jFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        jFileChooser.setAcceptAllFileFilterUsed(false);
        int returnVal = jFileChooser.showSaveDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            directoryToZip = jFileChooser.getSelectedFile();
            log().debug("Directory '{}' was chosen to be zipped.", directoryToZip.getAbsolutePath());
            createZipButton.setEnabled(true);
        } else {
            directoryToZip = null;
            log().debug("No directory was chosen. Setting directoryToZip to 'null'");
            createZipButton.setEnabled(false);
        }
    }

    private void createZip() {
        if (directoryToZip != null) {
            String zipFileName = directoryToZip.getName() + ".zip";
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(this,
                        String.format("Creation of '%s' started.", zipFileName),
                        "Creation of zip started.",
                        JOptionPane.INFORMATION_MESSAGE);
            });
            log().info("Creating .zip archive for directory '{}'", directoryToZip.getAbsolutePath());
            ZipUtil.pack(directoryToZip, new File(CURRENT_FOLDER, zipFileName));
            log().info("Zip archive '{}' was successfully created.", zipFileName);
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(this,
                        String.format("Zip archive '%s' was successfully created.\n Check '%s' folder.", zipFileName, CURRENT_FOLDER.getAbsolutePath()),
                        "Zip archive created.",
                        JOptionPane.INFORMATION_MESSAGE);
            });
        } else {
            log().warn("Directory wasn't chosen, but 'createZip' button was pushed.");
        }
    }

    private void onClose() {
        log().info("Closing application.");
    }

    public static void main(String[] args) {
        MainDialog dialog = new MainDialog(Configuration.getInstance());
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }

    private enum LogSingleton {
        INSTANCE;

        @SuppressWarnings("NonSerializableFieldInSerializableClass")
        private final Logger value = LoggerFactory.getLogger(MainDialog.class);
    }

    private static Logger log() {
        return LogSingleton.INSTANCE.value;
    }
}
