package ru.gosuslugi.pgu.pdf.builder;

import java.io.File;
import java.util.Comparator;

public class FilesComparatorFactory {

    private static final String SERVICE_CODE_10000000104 = "10000000104";

    static public Comparator<File> getComparator(String serviceCode) {
        if (serviceCode.equals(SERVICE_CODE_10000000104) ) {
            return Comparator.comparing(File::getName);
        }
        return null;
    }
}
