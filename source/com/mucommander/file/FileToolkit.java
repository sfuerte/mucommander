
package com.mucommander.file;

import java.io.IOException;
import java.io.InputStream;

/**
 * This class contains static methods which perform common file operations.
 *
 * @author Maxence Bernard
 */
public class FileToolkit {

    /**
     * Matches a path typed by the user (which can be relative to the current folder or absolute)
     * to an AbstractFile (folder). The folder returned will always exist.
     * If the given path doesn't correspond to any existing folder, a null value will be returned.
     */
    public static Object[] resolvePath(String destPath, AbstractFile currentFolder) {
        // Current path, including trailing separator
        String currentPath = currentFolder.getAbsolutePath(true);
        AbstractFile destFolder;

        if(com.mucommander.Debug.ON) com.mucommander.Debug.trace("destPath="+destPath+" currentPath="+currentPath);

        // If destination starts with './' or '.', replace '.' by current folder's path
        if(destPath.startsWith(".\\") || destPath.startsWith("./"))
            destPath = currentPath + destPath.substring(2, destPath.length());
        else if(destPath.equals("."))
            destPath = currentPath + destPath.substring(1, destPath.length());

        if(com.mucommander.Debug.ON) com.mucommander.Debug.trace("destPath ="+destPath);

        String newName = null;

        // Level 0, folder exists, newName is null

        // destPath points to an absolute and existing folder
        if ((destFolder=FileFactory.getFile(destPath))!=null
            && destFolder.exists()
            && destFolder.isDirectory()) {
            if(com.mucommander.Debug.ON) com.mucommander.Debug.trace("found existing folder for "+destPath+" destFolder="+destFolder.getAbsolutePath()+" destURL="+destFolder.getURL()+" URL filename="+destFolder.getURL().getFilename());
        }

        // destPath points to an existing folder relative to current folder
        else if ((destFolder=FileFactory.getFile(currentPath+destPath))!=null
         && destFolder.exists()
         && destFolder.isDirectory()) {
            if(com.mucommander.Debug.ON) com.mucommander.Debug.trace("found existing folder "+currentPath+destPath);
        }

        // Level 1, path includes a new destination filename
        else {
            // Removes ending separator character (if any)
            char c = destPath.charAt(destPath.length()-1);
            // Separator characters can be mixed
            if(c=='/' || c=='\\')
                destPath = destPath.substring(0,destPath.length()-1);

            // Extracts the new destination filename
            int pos = Math.max(destPath.lastIndexOf('/'), destPath.lastIndexOf('\\'));
            if (pos!=-1) {
                newName = destPath.substring(pos+1, destPath.length());
                destPath = destPath.substring(0,pos+1);
            }
            else  {
                newName = destPath;
                destPath = "";
            }

            if(com.mucommander.Debug.ON) com.mucommander.Debug.trace("level1, destPath="+destPath+" newname="+newName);
            // destPath points to an absolute and existing folder
            if (!destPath.equals("") && (destFolder=FileFactory.getFile(destPath))!=null && destFolder.exists()) {
                if(com.mucommander.Debug.ON) com.mucommander.Debug.trace("found existing folder "+destPath+" newname="+newName);
            }

            // destPath points to an existing folder relative to current folder
            else if ((destFolder=FileFactory.getFile(currentPath+destPath))!=null && destFolder.exists()) {
                if(com.mucommander.Debug.ON) com.mucommander.Debug.trace("found existing folder "+currentPath+destPath+" newname="+newName);
            }

            else {
                if(com.mucommander.Debug.ON) com.mucommander.Debug.trace("no match, returning null");
                return null;
            }
        }

        if(com.mucommander.Debug.ON) com.mucommander.Debug.trace("destFolder="+(destFolder==null?null:destFolder.getAbsolutePath())+" newName="+newName);
        return new Object[] {destFolder, newName};
    }


    /**
     * Copies the resource designated by the given path within the application JAR file to the specified destination file.
     *
     * @param resourceFilePath path within the application JAR file of the file to copy
     * @param destFile the destination file the resource file will be copied to
     * @throws IOException if an error occurred while either reading the resource file or writing the destination file
     */
    public static void copyResource(String resourceFilePath, AbstractFile destFile) throws IOException {
        InputStream in = null;
        try {
            in = resourceFilePath.getClass().getResourceAsStream(resourceFilePath);
            destFile.copyStream(in, false);
        }
        finally {
            if(in!=null)
                try { in.close(); }
                catch(IOException e) {}
        }
    }


    /**
     * Recursively calculates the total size for the given files and folders.
     */
    public static long getFileSize(AbstractFile files[]) {
        AbstractFile file;
        long total = 0;
        long fileSize;
        for(int i=0; i<files.length; i++) {
            file = files[i];
            if(file.isDirectory() && !file.isSymlink()) {
                try {
                    total += getFileCount(file.ls());
                }
                catch(IOException e) {
                }
            }
            else {
                fileSize = file.getSize();
                if(fileSize>0)
                    total += fileSize;
            }
        }
        return total;
    }

    /**
     * Recursively calculates the total number of files.
     */
    public static int getFileCount(AbstractFile files[]) {
        AbstractFile file;
        int total = 0;
        for(int i=0; i<files.length; i++) {
            file = files[i];
            if(file.isDirectory() && !file.isSymlink()) {
                try {
                    total += getFileCount(file.ls());
                }
                catch(IOException e) {
                }
            }
            else
                total++;
        }
        return total;
    }
}
