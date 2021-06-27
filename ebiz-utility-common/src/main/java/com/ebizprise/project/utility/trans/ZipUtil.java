package com.ebizprise.project.utility.trans;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.EncryptionMethod;

/**
 * Zip 處理工具
 * 
 * The <code>ZipUtil</code>
 * 
 * @author andrew.lee
 * @version 1.0, Created at 2018年9月8日
 */
public class ZipUtil {
    
    /**
     * 壓縮檔案到指定目錄
     * 
     * @param files
     * @param zipPathAndName
     * @param removeOldFile
     * @return boolean
     * @throws Exception 
     * @throws ZipException 
     */
    public static boolean packageZipFiles(List<File> files,String zipPathAndName, boolean removeOldFile,String pswd) throws Exception {
        try {
            ZipFile zipFile = new ZipFile(zipPathAndName);
            ZipParameters parameters = new ZipParameters();
            
            if(StringUtils.isNotBlank(pswd)) {
                parameters.setEncryptFiles(true);
                parameters.setEncryptionMethod(EncryptionMethod.ZIP_STANDARD);
                zipFile.setPassword(pswd.toCharArray());
            }
            zipFile.addFiles(files, parameters);
            

        } catch(Exception e) {
            throw e;
        } finally {
            if(removeOldFile) {
                for(File target : files) {
                    target.delete();
                }
            }
        }
        
        return true;
    }
    
    /**
     * 壓縮檔案到指定目錄
     * 
     * @param file
     * @param zipPathAndName
     * @param removeOldFile
     * @return boolean
     * @throws Exception 
     * @throws ZipException 
     */
    public static boolean packageZipFiles(File file,String zipPathAndName, boolean removeOldFile,String pswd) throws Exception {
    	List<File> files = new ArrayList<>();
    	files.add(file);

    	return packageZipFiles(files, zipPathAndName, removeOldFile, pswd);
    }
    
    /**
     * 解壓縮檔案
     * 
     * @param path
     * @param charset
     * @throws IOException
     */
    public static boolean unzip(String path,boolean removeOldFile,String pswd) throws Exception{
        String fullPath = Paths.get(path).toString();
        String targetPath = Paths.get(path).getParent().toString();
        
        if(!fullPath.endsWith(".zip")) {
            return false;
        }
        
        try {
            ZipFile zipFile = new ZipFile(path);
            if(zipFile.isEncrypted() && StringUtils.isNotBlank(pswd)) {
                zipFile.setPassword(pswd.toCharArray());
            } 
            
            zipFile.setCharset(StandardCharsets.UTF_8);
            zipFile.extractAll(targetPath);
            
            return true;
        } catch(Exception e) {
            throw e;
        } finally {
            if(removeOldFile) {
                File file = new File(path);
                if(file.exists()) {
                    file.delete();
                }
            }
        }
    }
    
    /**
     * 解壓縮檔案
     * 
     * @param path
     * @param charset
     * @throws Exception 
     */
    public static boolean unzip(String path,boolean removeOldFile) throws Exception{
        return unzip(path,removeOldFile,"");
    }
    
    /**
     * 壓縮檔案
     * 
     * @param files
     * @param zipPathAndName
     * @param removeOldFile
     * @return boolean
     * @throws Exception 
     */
    public static boolean packageZipFiles(List<File> files,String zipPathAndName, boolean removeOldFile) throws Exception {
        return packageZipFiles(files, zipPathAndName, removeOldFile, "");
    }
    
    /**
     * 取得壓縮檔內的所有檔案名稱
     * 
     * @param archivePath
     * @return List
     * @throws Exception
     */
    public static List<String> getZipFileNames(String archivePath) throws Exception {
        List<String> rtnLs = new ArrayList<>();
        ZipFile zipFile = new ZipFile(archivePath);
        
        for(FileHeader header : zipFile.getFileHeaders()) {
            rtnLs.add(header.getFileName());
        }
        
        return rtnLs;
    }

}
