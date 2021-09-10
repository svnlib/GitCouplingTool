package com.svnlib.gitcouplingtool.model;

public enum ExportFormat {
    JSON("json"),
    GML("gml");

    private final String fileExtension;

    ExportFormat(final String fileExtension) {
        this.fileExtension = fileExtension;
    }

    public String getFileExtension() {
        return this.fileExtension;
    }
}
