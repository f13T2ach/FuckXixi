package com.cxk.fuckxixi.user;

public class Consts {

    //固定参数
    public static String ClientId = "20008";

    public static String BrandId = "570678343";

    //请求参数名
    public static String _userName = "userName";

    public static String _passWord = "passWord";

    public static String _clientId = "clientId";

    public static String _brandId = "brandId";

    //正常情况下的个人资料的显示
    public static String FormatOfProfile = "班级：%s \n学校：%s\n账户：%s\n令牌：%s\n\n暂不支持查看作业列表";

    //API地址
    public static String GetTokenUrl = "https://eapi.ciwong.com/gateway/oauth/v2/token";

    public static String GetProfileUrl = "https://eapi.ciwong.com/gateway/v4/relation/class/get_my_classes?clientId="+ClientId+"&accessToken=";

    public static String requestTokenUrl = "https://eapi.ciwong.com/gateway/oauth/v2/token?&clientId="+ClientId+"&brandId="+BrandId+"&userName="+UserProfileUnit.getUsername()+"&passWord="+UserProfileUnit.getPassword();

}
