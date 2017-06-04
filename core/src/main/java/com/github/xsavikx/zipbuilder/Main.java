package com.github.xsavikx.zipbuilder;

public class Main {
    public static void main(String[] args) {
        MainDialog dialog = new MainDialog(Configuration.getInstance());
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
