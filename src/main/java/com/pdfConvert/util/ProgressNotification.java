package com.pdfConvert.util;

@FunctionalInterface
public interface ProgressNotification {
    void run(int currentPage, int totalNumberOfPages);
}
