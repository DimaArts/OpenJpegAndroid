package org.openJpeg;

/**
 * Created by gorshunovdv on 10/26/2017.
 */

public class LibraryInitializer {
    private static boolean isInitialized = false;

    public static void initializeLibrary() {
        if (!isInitialized) {
            try {
                System.loadLibrary("openjpeg");
                isInitialized = true;
            } catch (Throwable t) {
                throw new ExceptionInInitializerError("OpenJPEG Java Decoder: probably impossible to find the C library");
            }
        }
    }
}
