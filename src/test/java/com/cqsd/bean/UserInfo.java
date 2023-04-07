package com.cqsd.bean;


import java.util.StringJoiner;

/**
 * @author caseycheng
 * @date 2022/12/15-10:44
 **/
public class UserInfo extends BaseEntry {
    public static final int GENDER_SECRET = 0;//保密
    public static final int GENDER_MALE = 1;//男
    public static final int GENDER_FEMALE = 2;//女
    public static final int STARTE_NORMAL = 0;//正常
    public static final int STARTE_DISABLE = 1;//冻结
    private String nickname;//呢称
    private String phone;//手机号
    private String email;//邮箱
    private String password;//密码
    private Integer gender = GENDER_SECRET;//信别
    private Integer level = 0;//用户级别
    private String city;//所在城市
    private String headImgUrl;//头像
    private String info;//个性签名
    private Integer state = STARTE_NORMAL;//状态
    private UserInfo userInfo;

    public String getNickname() {
        return nickname;
    }

    public UserInfo setNickname(String nickname) {
        this.nickname = nickname;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public UserInfo setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public UserInfo setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public UserInfo setPassword(String password) {
        this.password = password;
        return this;
    }

    public Integer getGender() {
        return gender;
    }

    public UserInfo setGender(Integer gender) {
        this.gender = gender;
        return this;
    }

    public Integer getLevel() {
        return level;
    }

    public UserInfo setLevel(Integer level) {
        this.level = level;
        return this;
    }

    public String getCity() {
        return city;
    }

    public UserInfo setCity(String city) {
        this.city = city;
        return this;
    }

    public String getHeadImgUrl() {
        return headImgUrl;
    }

    public UserInfo setHeadImgUrl(String headImgUrl) {
        this.headImgUrl = headImgUrl;
        return this;
    }

    public String getInfo() {
        return info;
    }

    public UserInfo setInfo(String info) {
        this.info = info;
        return this;
    }

    public Integer getState() {
        return state;
    }

    public UserInfo setState(Integer state) {
        this.state = state;
        return this;
    }

    public UserInfo setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
        return this;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", UserInfo.class.getSimpleName() + "[", "]")
                .add("nickname='" + nickname + "'")
                .add("phone='" + phone + "'")
                .add("email='" + email + "'")
                .add("password='" + password + "'")
                .add("gender=" + gender)
                .add("level=" + level)
                .add("city='" + city + "'")
                .add("headImgUrl='" + headImgUrl + "'")
                .add("info='" + info + "'")
                .add("state=" + state)
                .add("userInfo=" + userInfo)
                .toString();
    }
}
