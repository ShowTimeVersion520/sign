package com.showtime.sign.utils;

import com.alibaba.fastjson.JSONObject;
import com.showtime.sign.constant.LoginTicketFieldConstant;
import com.showtime.sign.model.entity.LoginTicket;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import tk.mybatis.mapper.entity.Example;

import java.security.MessageDigest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
public class SignUtil {


    public static String _DOMAIN = "http://127.0.0.1:8080/";
    public static String EXCEL_DIR = "D:/downloadDemo/";
    public static String[] EXCEL_FILE_EXTD = new String[] {"xls"};


    public static boolean isFileAllowed(String fileName) {
        for (String ext : EXCEL_FILE_EXTD) {
            if (ext.equals(fileName)) {
                return true;
            }
        }
        return false;
    }
    public static String getJSONString(int code) {
        JSONObject json = new JSONObject();
        json.put("code", code);
        return json.toJSONString();
    }

    public static String getJSONString(int code, String msg) {
        JSONObject json = new JSONObject();
        json.put("code", code);
        json.put("msg", msg);
        return json.toJSONString();
    }

    public static String getJSONString(int code, Map<String, Object> map) {
        JSONObject json = new JSONObject();
        json.put("code", code);
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            json.put(entry.getKey(), entry.getValue());
        }
        return json.toJSONString();
    }

    /** 可能出现多线程问题，之后修改 */
    public static boolean checkExcelExt(MultipartFile file) {
        // 正确性检验
        int dotPos = file.getOriginalFilename().lastIndexOf(".");
        if (dotPos < 0) {
            return true;
        }
        String fileExt = file.getOriginalFilename().substring(dotPos + 1).toLowerCase();
        if (!isFileAllowed(fileExt)) {
            return true;
        }
        return false;
    }

    /** 可能出现多线程问题，之后修改 */
    public static Map<String, Object> checkUsernameAndPassword(String username, String password){
        Map<String, Object> map = new HashMap<String, Object>();
        if (StringUtils.isBlank(username)) {
            map.put("msgname", "用户名不能为空");
            return map;
        }

        if (StringUtils.isBlank(password)) {
            map.put("msgpwd", "密码不能为空");
            return map;
        }

        return map;
    }

    public static Example logout(String ticket) {
        Example example = new Example(LoginTicket.class);
        Example.Criteria criteria = example.createCriteria();

        criteria.andEqualTo(LoginTicketFieldConstant.TICKET, ticket);

        return example;
    }

    /** 可能出现多线程问题，之后修改 */
    public static LoginTicket InitLoginTicket(Long accountId) {
        LoginTicket ticket = new LoginTicket();
        Date date = new Date();
        date.setTime(date.getTime() + 1000*3600*24);
        //超时时间
        ticket.setExpired(date);
        ticket.setStatus((byte) 1);
        ticket.setTicket(UUID.randomUUID().toString().replaceAll("-", ""));
        ticket.setAccountId(accountId);
        return ticket;
    }

    /** 可能出现多线程问题，之后修改 */
    public static String MD5(String key) {
        char hexDigits[] = {
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
        };
        try {
            byte[] btInput = key.getBytes();
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            mdInst.update(btInput);
            // 获得密文
            byte[] md = mdInst.digest();
            // 把密文转换成十六进制的字符串形式
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            log.error("生成MD5失败", e);
            return null;
        }
    }
}
