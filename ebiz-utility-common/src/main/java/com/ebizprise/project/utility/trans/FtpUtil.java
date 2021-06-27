package com.ebizprise.project.utility.trans;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

public class FtpUtil {

  private static Log logger = LogFactory.getLog(FtpUtil.class);

  private FTPClient ftp;

  private String ftpServer;

  private String ftpPort;

  private String userName;

  private String password;

  public void setFtpServer(String ftpServer) {
    this.ftpServer = ftpServer;
  }

  public void setFtpPort(String ftpPort) {
    this.ftpPort = ftpPort;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public FtpUtil(String ftpServer, String ftpPort, String userName, String password) {
    this.ftpServer = ftpServer;
    this.ftpPort = ftpPort;
    this.userName = userName;
    this.password = password;
  }

  /**
   *連接Ftp服務器
   *
   * @return SUCCESS 其他:失敗訊息
   */
  public String loginToFtpServer() {
    if (null == ftp) {
      try {
        ftp = new FTPClient();

        //不設端口 默認使用默認端口登錄21
        if (StringUtils.isEmpty(ftpPort)) {
          ftp.connect(this.ftpServer);
        } else {
          ftp.connect(this.ftpServer, Integer.parseInt(ftpPort));
        }

        //下面三行代碼必須要有,而且不能改變編碼格式否則不能正確下載中文文件
        ftp.setControlEncoding("UTF-8");
        FTPClientConfig conf = new FTPClientConfig(FTPClientConfig.SYST_NT);
        conf.setServerLanguageCode("en");

        //驗證通道連接是否建立成功(回傳響應碼)
        if (!FTPReply.isPositiveCompletion(ftp.getReplyCode())) {
          ftp.disconnect();
          logger.error("FTP連接失敗，ip:" + this.ftpServer);
          return "FTP連接失敗，ip:" + this.ftpServer;
        }

        if (!ftp.login(this.userName, this.password)) {
          logger
              .error("FTP登錄失敗，ip:" + this.ftpServer + ";userName:" + userName + ";port:" + ftpPort);
          return "FTP登錄失敗，ip:" + this.ftpServer + ";userName:" + userName + ";port:" + ftpPort;
        }
      } catch (Exception e) {
        logger.error("FTP連接失敗", e);
        return "FTP連接失敗" + e;
      }
    }
    logger.info("Ftp" + this.ftpServer + "登錄成功!;port:" + ftpPort);
    return "SUCCESS";
  }

  /**
   * 關閉ftp連接 <功能詳細描述> void
   */
  public void closeConnection() {
    if (null != ftp && ftp.isConnected()) {
      try {
        ftp.disconnect();
        logger.info("FTP" + this.ftpServer + "連接關閉");
      } catch (Exception e) {
        logger.error("FTP連接關閉異常", e);
      }
    }
  }

  /**
   * 上傳文件到ftp <功能詳細描述>
   *
   * @param remotePath ftp路徑
   * @param localFileName 要上傳的本地文件
   * @return SECCESS:上傳成功 其他:上傳失敗信息
   */
  public String uploadFile(String remotePath, String localFileName) throws IOException {
    FileInputStream in = null;
    try {
      // 存放在Ftp上的文件名稱
      String remoteFileName = "";
      ftp.setFileType(FTP.BINARY_FILE_TYPE);
      if (StringUtils.isEmpty(localFileName) || StringUtils.isEmpty(remotePath)) {
        logger.error("文件上傳Ftp失敗目標路徑或源路徑錯誤");
        return "文件上傳Ftp失敗目標路徑或源路徑錯誤";
      }
      remoteFileName = localFileName
          .substring(localFileName.lastIndexOf("/") + 1, localFileName.length());

      // 确保文件路径存在
      ftp.makeDirectory(remotePath);

      if (!ftp.changeWorkingDirectory(remotePath)) {
        logger.error("切換目錄[" + remotePath + "]失敗");
        return "切換目錄[" + remotePath + "]失敗";
      }

      // 上傳之前先刪除原來文件,防止重複對賬(文件不存不報異常)
      ftp.deleteFile(remoteFileName);

      in = new FileInputStream(new File(localFileName));
      ftp.storeFile(new String(remoteFileName.getBytes("UTF-8")), in);
    } catch (Exception e) {
      logger.error("文件上傳Ftp失敗:", e);
      return "文件上傳Ftp失敗:" + e;
    } finally {
      if (null != in) {
        in.close();
      }
    }
    return "SUCCESS";
  }

  /**
   * 獲取文件夾下文件名稱列表 <功能詳細描述>
   *
   * @param remotePath 文件夾路徑
   * @return List<String> 文件名稱列表
   */
  public List<String> getFileList(String remotePath) {
    try {
      if (ftp.changeWorkingDirectory(remotePath)) {
        String[] str = ftp.listNames();
        if (null == str || str.length < 0) {
          return null;
        }
        return Arrays.asList(str);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   *從Ftp下載文件 <功能詳細描述>
   *
   * @param remotePath Ftp上文件路徑
   * @param remoteFileName 遠端檔名
   * @param localFileName 下載到本地檔案路徑(帶檔名)
   * @return SUCCESS:成功 其他:失敗信息
   */
  public String download(String remotePath, String remoteFileName, String localFileName)
      throws IOException {
    FileOutputStream oStream = null;
    try {
      // 切換到指定目錄下
      if (ftp.changeWorkingDirectory(remotePath)) {
        oStream = new FileOutputStream(new File(localFileName));

        if (!ftp.retrieveFile(remoteFileName, oStream)) {
          logger.info("從Ftp上下載文件失敗！" + remoteFileName);
          return "從Ftp上下載文件失敗！" + remoteFileName;
        }
      } else {
        logger.info("對賬文件下載失敗，不能正常切換至目錄" + remotePath + ";目錄不存在！");
        return "對賬文件下載失敗，不能正常切換至目錄" + remotePath + ";目錄不存在！";
      }
    } catch (Exception e) {
      logger.info("Ftp上檔案" + remoteFileName + "下載失敗!", e);
      return "Ftp上檔案" + remoteFileName + "下載失敗!" + e;
    } finally {
      if (null != oStream) {
        oStream.close();
      }
    }
    return "SUCCESS";
  }

  /**
   *刪除Ftp上的目錄 包括其中的檔案 <功能詳細描述>
   *
   * @param pathName 檔案路徑
   * @return SUCCESS:成功 其他:失敗
   */
  public String removeDirectoryALLFile(String pathName) {
    try {
      FTPFile[] files = ftp.listFiles(pathName);
      if (null != files && files.length > 0) {
        for (FTPFile file : files) {
          if (file.isDirectory()) {
            removeDirectoryALLFile(pathName + "/" + file.getName());

            //切換到父目錄，不然刪不掉目錄
            ftp.changeWorkingDirectory(pathName.substring(0, pathName.lastIndexOf("/")));
            ftp.removeDirectory(pathName);
          } else {
            if (!ftp.deleteFile(pathName + "/" + file.getName())) {
              return "刪除指定目錄" + pathName + "/" + file.getName() + "失敗!";
            }
          }
        }
      }
      //切換到父目錄，不然刪不掉目錄
      ftp.changeWorkingDirectory(pathName.substring(0, pathName.lastIndexOf("/")));
      ftp.removeDirectory(pathName);
    } catch (IOException e) {
      logger.error("刪除指定目錄" + pathName + "失敗：" + e);
      e.printStackTrace();
      return "刪除指定目錄" + pathName + "失敗：" + e;
    }
    return "SUCCESS";
  }

  /**
   * 刪除指定檔案
   *
   * @param filePath 檔案路径(含檔名)
   * @return SUCCESS:成功 其他:失敗信息
   */
  public String removeFile(String filePath) {
    try {
      if (StringUtils.isNotEmpty(filePath)) {
        if (!ftp.deleteFile(filePath)) {
          return "刪除檔案" + filePath + "失敗！";
        }
      }
    } catch (IOException e) {
      logger.error("刪除檔案失敗：", e);
      e.printStackTrace();
      return "刪除檔案" + filePath + "失敗！" + e;
    }
    return "SUCCESS";
  }

  /**
   *向文件頭添加合計信息
   *
   * @param localPath 目標檔案所在目錄路徑
   * @param desFile 目標檔名
   * @param mergeStr 插入字符串信息
   * @return SUCCESS:成功 其他:失敗信息
   */
  public static String mergeFile(String localPath, String desFile, String mergeStr)
      throws IOException {
    // 向目標文件頭中添加合計信息
    FileInputStream inputStream = null;
    FileOutputStream fileOutStream = null;
    try {
      inputStream = new FileInputStream(localPath + "/" + desFile);
      byte allBytes[] = new byte[inputStream.available()];
      inputStream.read(allBytes);

      fileOutStream = new FileOutputStream(localPath + "/" + desFile);
      fileOutStream.write(mergeStr.getBytes());
      fileOutStream.write(allBytes);
    } catch (IOException e) {
      return e.getMessage();
    } finally {
      if (null != fileOutStream) {
        fileOutStream.close();
      }
      if (null != inputStream) {
        inputStream.close();
      }
    }
    return "SUCCESS";
  }

  /**
   * 刪除備份目錄下不符合時間的檔案 <功能詳細描述>
   *
   * @param dailyBakPath 備份目錄
   * @param dailyBakDate 有效時間 1、5、9等
   * @return SUCCESS:成功 其他:失敗信息
   */
  public static String deleteFile(String dailyBakPath, String dailyBakDate) {
    try {
      // 獲取符合規則的日期
      SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");

      // 當天時間
      Calendar theday = Calendar.getInstance();

      // 存放符合備份的日期時間 數組長度為備份的天數+1
      String[] dates = new String[Integer.valueOf(dailyBakDate) + 1];
      for (int i = 0; i < dates.length; i++) {
        dates[i] = df.format(theday.getTime());

        // 獲取上一天的時間
        theday.add(Calendar.DATE, -1);
      }

      File a = new File(dailyBakPath);
      if (!a.exists()) {
        return dailyBakPath + "目錄不存在!";
      }

      // 獲取目錄下所有檔案
      String[] fileArr = a.list();

      // 遍歷檔案名稱，查看是否在保留日期dates內,不在則刪除
      for (int i = 0; i < fileArr.length; i++) {
        boolean canDele = true;
        for (int j = 0; j < dates.length; j++) {
          // 不刪除dates內開頭的檔案 和 檔名包含error的文件
          if (fileArr[i].startsWith(dates[j]) || fileArr[i].contains("error")) {
            canDele = false;
            break;
          }
        }
        if (canDele) {
          deletefile(dailyBakPath + "/" + fileArr[i]);
        }
      }
    } catch (Exception e) {
      logger.info("刪除目錄內容操作出錯,請查看配置路徑或保留時間是否正確！");
      return "刪除目錄內容操作出錯,請查看配置路徑或保留時間是否正確！";
    }
    return "SUCCESS";
  }

  /**
   * 根據參數,刪除目錄(下文件及文件夾)或文件
   *
   * @param delpath 文件夾路徑或文件路徑
   * @return boolean true:成功 false:失敗
   */
  public static boolean deletefile(String delpath) {
    try {
      File file = new File(delpath);
      if (file.isDirectory()) {
        String[] fileList = file.list();
        for (String fileName : fileList) {
          deletefile(delpath + "\\" + fileName);
        }
      }
      file.delete();
    } catch (Exception e) {
      logger.error("deletefile() Exception:" + e.getMessage());
    }
    return true;
  }

  public static void main(String[] args) throws IOException {
    //System.out.println(deletefile("G:/Q"));

    FtpUtil ftpUtil = new FtpUtil("192.168.132.110", "21", "a", "a");
    System.out.println(ftpUtil.loginToFtpServer());

    // System.out.println(ftpUtil.removeDirectoryALLFile("/home/tbcs/zhangvb/epay/20140820"));

    // FTPClient client=new FTPClient();
    // client.connect("192.168.132.110");
    // client.login("a","a");
    // System.out.println(client.removeDirectory("/home/tbcs/zhangvb/epay/test"));
  }
}
