package com.strawci.ci.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtils {
    public static File extractResource (final File path, final String resourceName) throws Exception {
        InputStream stream = null;
        OutputStream resStreamOut = null;
        File file = new File(path, resourceName);

        try {
            stream = FileUtils.class.getClassLoader().getResourceAsStream(resourceName);//note that each / is a directory down in the "jar tree" been the jar the root of the tree
            if(stream == null) {
                throw new Exception("Cannot get resource \"" + resourceName + "\" from Jar file.");
            }

            int readBytes;
            byte[] buffer = new byte[4096];
            resStreamOut = new FileOutputStream(file);
            while ((readBytes = stream.read(buffer)) > 0) {
                resStreamOut.write(buffer, 0, readBytes);
            }
        } catch (Exception ex) {
            throw ex;
        } finally {
            if (stream != null)
                stream.close();

            if (resStreamOut != null)
                resStreamOut.close();
        }

        return file;
    }
}
