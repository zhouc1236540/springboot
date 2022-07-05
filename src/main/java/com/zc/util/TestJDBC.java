package com.zc.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author: 山毛榉
 * @date : 2022/6/24 15:33
 * @version: 1.0
 */
public class TestJDBC  {
    public static void main(String[] args) throws Exception {
        //1、注册驱动类
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        //2、连接数据库
        String url ="jdbc:sqlserver://185.168.1.200:50980;DatabaseName=data1125";
        String username ="GLZC";
        String password = "Gl123456@.";
        Connection con = DriverManager.getConnection(url, username, password);
        //3、创建PreparedStatement对象，用来执行sql语句
        String sql = " SELECT  \n" +
                "\t\t'http://192.168.21.23:8998/eosimage/'+CASE WHEN B.KSTSCB='Y' THEN B.tpsy +'/'+B.ksbm ELSE B.GCTPSY+'/'+B.GCTMC END+'.jpg' AS 图片,\n" +
                "\t\t 'D:\\订单图片\\300x300像素\\'+B.ksbm+'.JPG' as 图片300\t\n" +
                " FROM dbo.XSDDSH A\n" +
                "INNER JOIN XSDDSB B ON A.DJLSH=B.DJLSH\n" +
                "WHERE A.DjLsh=74882";
        PreparedStatement ps = con.prepareStatement(sql);
        //4、给占位符赋值
        //5、执行sql语句(接收结果集)
        ResultSet rs = ps.executeQuery();
        //6、处理结果:遍历结果集
        while(rs.next()) {//只要结果集中有数据 就返回true
            //每循环一次，就默认是一行数据
            //获取每一行中的 每一个字段数据

            String name =rs.getString(1);


            System.out.println("name:"+name);
        }
        //7、关闭
        rs.close();
        con.close();
        ps.close();
    }


}
