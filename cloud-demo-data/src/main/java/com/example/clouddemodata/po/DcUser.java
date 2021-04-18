package com.example.clouddemodata.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 用户信息(登入的基本信息)
 * </p>
 *
 * @author jobob
 * @since 2021-04-14
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class DcUser implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID,用户唯一表示
     */
    @TableId(value = "user_id", type = IdType.AUTO)
    private Integer userId;

    /**
     * 用户名，唯一
     */
    private String userName;

    /**
     * 用户名，唯一
     */
    private String userPhone;

    /**
     * ip，唯一
     */
    private String ip;


    /**
     * 设备号，唯一
     */
    private String deviceId;

    /**
     * 用户密码，MD5
     */
    private String userPassword;

    /**
     * 用户来源（0：未知；1：SDK；2：平台app）
     */
    private Integer platformType;

    /**
     * 用户注册方式， 0:账号密码注册， 1:手机号码注册， 2，第三方登录注册
     */
    private Integer regType;

    /**
     * 用户来源（0：未知；1：SDK；2：平台app）
     */
    private Integer gender;

    /**
     * 用户注册方式， 0:账号密码注册， 1:手机号码注册， 2，第三方登录注册
     */
    private Integer userStatus;

    /**
     * 首次注册渠道ID
     */
    private Integer channelId;

    /**
     * 用户注册时间
     */
    private Integer regTime;

    /**
     * 添加时间
     */
    private Integer ctime;


}
