package com.zc.util;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.logging.log4j.Logger;

import java.io.*;

import static com.sun.xml.internal.ws.spi.db.BindingContextFactory.LOGGER;

/**
 * @author: 山毛榉
 * @date : 2022/6/27 10:55
 * @version: 1.0
 */
public class FileCopy {
    //private static final Logger LOGGER = Logger.getLogger(FTPClientUtil.class);

    public static void main(String[] args) throws IOException {
         FileCopy.connect("http://192.168.21.23/",8998,"anonymous",null);

       // FileCopy.copyFile("K:\\折扣分析.txt","K:\\images");

    }

    /**
     * 文件复制粘贴过程，一般是某某文件复制到某某文件夹内，所以复制文件路径是包括该文件的(要加上后缀)，而粘贴文件路径是只到某目录
     * @param copyPath :复制文件的绝对路径,一定要有后缀  如: "C:\\text\\新建文件夹\\文件名.txt"  不一定是txt格式的其他格式也可以，包括音频，视频，照片,只要它们可以被记事本打开就行
     * @param pastePath ：粘贴位置的绝对路径	如："C:\\dev\\新建文件夹"
     * @throws IOException
     * return
     */
    public  static  String copyFile(String copyPath , String pastePath) throws IOException{
        //新建文件类，就是获取该路径的文件或文件夹
        File copyfile = new File(copyPath);
        //exists()方法判断目标文件是否存在，如果文件本身就不存在那就没必要复制了
        //isFile()方法判断目标文件是否是一个标准文件(意思就是除文件夹的其他文件)
        if(!copyfile.exists() && copyfile.isFile()){
            return "未找到该文件";
        }
        //获取该文件的文件名
        String filename = copyfile.getName();
        //File.separator和“\\”一个意思
        pastePath += File.separator + filename;
        //新建文件类，并不是新建一个文件，新建文件会在读写过程之前建
        File pastefile = new File(pastePath);
        //判断粘贴路径上的目录是否存在，如果不存在就新建目录mkdirs()方法是创建多级目录
        if(!pastefile.getParentFile().exists()){
            pastefile.getParentFile().mkdirs();
        }
        //判断粘贴路径上是否已存在命名相同的文件，如果存在就将它删除
        if(pastefile.exists()){
            pastefile.delete();
        }
        //文件输入字节流
        InputStream in = new FileInputStream(copyfile);
        //文件输出字节流
        OutputStream out = new FileOutputStream(pastefile);
        //缓冲流输入输出流，加快读写速度
        BufferedInputStream bin = new BufferedInputStream(in);
        BufferedOutputStream bout = new BufferedOutputStream(out);
        //将内容转换成流的形式，已字节的大小，存放在字节数组中，容易进行读写操作
        byte[] byt = new byte[1024];
        int len = 0;
        //边读边写将复制文件内的内容读到字节数组中，再写入粘贴路径的文件中
        while((len = bin.read(byt)) != -1){
            bout.write(byt,0,len);
        }
        //关闭缓冲流，一定要关闭流，没关闭表示读写操作没有完成，会影响结果或其他流
        //关闭流的顺序，一定是先关闭缓存流，再关闭字节流
        bin.close();
        bout.close();
        in.close();
        out.close();
        return "文件复制完成";
    }




        /**
         * 连接文件服务器
         * @param addr 文件服务器地址
         * @param port 端口
         * @param username 用户名
         * @param password 密码
         * @throws Exception
         */
        public static FTPClient connect(String addr, int port, String username, String password) {
            LOGGER.info("【连接文件服务器】addr = " + addr + " , port : " + port + " , username = " + username + " , password = "
                    + password);

            FTPClient ftpClient = new FTPClient();
            try {
                // 连接
                ftpClient.connect(addr, port);
                // 登录
                ftpClient.login(username, password);
                // 被动模式：每次数据连接之前，ftp client告诉ftp server开通一个端口来传输数据（参考资料：FTP主动/被动模式的解释）
                ftpClient.enterLocalPassiveMode();
                ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            } catch (Exception e) {
                //LOGGER.error("【连接文件服务器失败】", e);
                throw new RuntimeException("连接文件服务器失败");
            }
            // 判断文件服务器是否可用？？
            if (!FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
                //关闭连接
            }
            return ftpClient;
        }


}
