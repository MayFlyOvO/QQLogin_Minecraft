package com.gameobj.qqlogin;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.Date;

public class Captcha {
    private String captcha;
    private Date expireTime;
    private long qq; // 为 0 表示未绑定QQ玩家

    public Captcha(String captcha, Date expireTime, long qq) {
        this.captcha = captcha;
        this.expireTime = expireTime;
        this.qq = qq;
    }

    public String getCaptcha() {
        return captcha;
    }

    public void setCaptcha(String captcha) {
        this.captcha = captcha;
    }

    public Date getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Date expireTime) {
        this.expireTime = expireTime;
    }

    public long getQQ() {
        return qq;
    }

    public void setQQ(long qq) {
        this.qq = qq;
    }

    @Override
    public String toString() {
        return "Captcha{" +
                "captcha='" + captcha + '\'' +
                ", expireTime=" + expireTime +
                ", qq=" + qq +
                '}';
    }

    public static Captcha create(long qq) {
        FileConfiguration config = QQLogin.getInstance().getConfig();

        // 过期时间
        Date expireTime = new Date(System.currentTimeMillis() + 1000L * config.getInt("captcha.expire"));
        // 验证码类型
        int captchaType = config.getInt("captcha.type");

        // 验证码长度
        int captchaLength = config.getInt("captcha.length");

        // 根据验证码类型生成验证码
        StringBuilder captcha = new StringBuilder();
        switch (captchaType) {// 数字
            case 1:
                // 字母
                for (int i = 0; i < captchaLength; i++) {
                    captcha.append(getRandomChar("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"));
                }
                break;
            case 2:
                // 数字+字母
                for (int i = 0; i < captchaLength; i++) {
                    captcha.append(getRandomChar("0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"));
                }
                break;
            case 3:
                // 自定义字符
                String custom = config.getString("captcha.custom");
                for (int i = 0; i < captchaLength; i++) {
                    captcha.append(getRandomChar(custom));
                }
                break;
            default:
                // 默认数字
                for (int i = 0; i < captchaLength; i++) {
                    captcha.append(getRandomChar("0123456789"));
                }
        }

        return new Captcha(captcha.toString(), expireTime, qq);
    }

    // 检查验证码是否过期
    public boolean isExpired() {
        return System.currentTimeMillis() > expireTime.getTime();
    }

    // 验证码是否正确
    public boolean verify(String captcha) {
        return captcha.equals(this.captcha);
    }

    // 获取随机字符
    private static char getRandomChar(String str) {
        int index = (int) (Math.random() * str.length());
        return str.charAt(index);
    }
}
