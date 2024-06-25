package com.pdfConvert.util;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.imageio.ImageIO;
import java.io.File;

/**
 * @author Sergey
 * @version 2.0.16(Apache PDFBox version support)
 */
public class ConvertUtil {

    public static void convert(File sourceFile, File destinationFile, String FILE_EXTENSION, int DPI, ProgressNotification progressNotification) throws Exception {
        if (!destinationFile.exists()) {
            destinationFile.mkdir();
            System.out.println("Folder Created -> " + destinationFile.getAbsolutePath());
        }
        if (sourceFile.exists()) {
            System.out.println("Images copied to Folder Location: " + destinationFile.getAbsolutePath());
            final PDDocument DOCUMENT = PDDocument.load(sourceFile);
            final PDFRenderer PDF_RENDER = new PDFRenderer(DOCUMENT);

            final int numberOfPages = DOCUMENT.getNumberOfPages();
            System.out.println("Total files to be converting -> " + numberOfPages);

            final String fileName = sourceFile.getName().replace(".pdf", "");
            for (int i = 0; i < numberOfPages; ++i) {
                final File outPutFile = new File(destinationFile.getAbsolutePath() + "/" + fileName + " - " + (i + 1) + "." + FILE_EXTENSION);
                ImageIO.write(PDF_RENDER.renderImageWithDPI(i, DPI, ImageType.RGB), FILE_EXTENSION, outPutFile);
                progressNotification.run(i, numberOfPages);
            }

            DOCUMENT.close();
            System.out.println("Converted Images are saved at -> " + destinationFile.getAbsolutePath());
        } else {
            System.err.println(sourceFile.getName() + " File not exists");
        }
    }


}
