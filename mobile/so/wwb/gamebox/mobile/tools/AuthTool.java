package so.wwb.gamebox.mobile.tools;

import org.soul.commons.lang.string.StringTool;
import org.soul.commons.log.Log;
import org.soul.commons.log.LogFactory;
import org.soul.commons.security.CryptoTool;
import org.soul.commons.security.DigestTool;

import java.awt.*;
import java.io.*;

/**
 *
 * Created by bill on 3/21/16.
 */
public class AuthTool {

    private static final Log LOG = LogFactory.getLog(AuthTool.class);

    private static final String ROOT_PATH = "/home/leo/app/";

    //用户密码
    private static String SALT_SYS_USER_PWD = "AgDz+&?MB|G{*%bH%pf@w4y8;Avt>0R9en,~.L]|db[16=Jr.)0wa^U@~%QNe(Xk";

    //用户安全密码
    private static String SALT_SYS_USER_PERMISSION_PWD = "bnQElt-?a:nCYg@!|>kt42HvjdEaZ_?f.CR`MGKqDrXozKnGFo%*f;{C,|F+H`9r";

    private static String sysUserPermissionSalt(String username){
        if (StringTool.isNotBlank(username)) {
            return SALT_SYS_USER_PWD + username;
        }
        return SALT_SYS_USER_PERMISSION_PWD;
    }

    /**
     * 安全密码
     * @param source
     * @param username
     * @return
     */
    private static String md5SysUserPermission(String source, String username){
        String salt = sysUserPermissionSalt(StringTool.lowerCase(username));
        return DigestTool.getMD5(source, salt);
    }

    private static boolean isUpperCase(String code){
        for(int i = 0; i < code.length(); i++){
            if(Character.isUpperCase(code.charAt(i))){
                return true;
            }
        }
        return false;
    }

    /**
     * 批量生成ios plist文件,并且将ipa包放在plist文件统一目录
     * @param code
     * @param versionName
     * @param siteId
     * @param siteName
     */
    public static void getIosPlist(String code, String versionName, Integer siteId, String siteName){
        String path = ROOT_PATH + "ios/" +versionName + "/" + code + "/";

        File pathFile = new File(path);
        if(!pathFile.exists()){
            pathFile.mkdirs();
            System.out.println("创建目录:" + path +"----成功");
        }
        File ipa;
        //判断code是否包含大写字母,
        //
        if (isUpperCase(code)){
            String newPath = ROOT_PATH + "ios/" +versionName + "/" + code.toLowerCase() + "/";
            ipa = new File(newPath,"Copy of webViewtest.ipa");
        }else{
            ipa = new File(path,"Copy of webViewtest.ipa");
        }
        if(ipa.exists()){
            ipa.renameTo(new File(path + "/app_".concat(code).concat("_").concat(versionName).concat(".ipa")));
        }
        File file = new File(path,"app_".concat(code).concat("_").concat(versionName).concat(".plist"));
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String plist = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<!DOCTYPE plist PUBLIC \"-//Apple//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">\n" +
                "<plist version=\"1.0\">\n" +
                "<dict>\n" +
                "\t<key>items</key>\n" +
                "\t<array>\n" +
                "\t\t<dict>\n" +
                "\t\t\t<key>assets</key>\n" +
                "\t\t\t<array>\n" +
                "\t\t\t\t<dict>\n" +
                "\t\t\t\t\t<key>kind</key>\n" +
                "\t\t\t\t\t<string>software-package</string>\n" +
                "\t\t\t\t\t<key>url</key>\n" +
                "\t\t\t\t\t<string>https://dsda3112.com/ios/" + code + "/app_" + code + "_" + versionName + ".ipa</string>\n" +
                "\t\t\t\t</dict>\n" +
                "\t\t\t\t<dict>\n" +
                "\t\t\t\t\t<key>kind</key>\n" +
                "\t\t\t\t\t<string>full-size-image</string>\n" +
                "\t\t\t\t\t<key>needs-shine</key>\n" +
                "\t\t\t\t\t<false/>\n" +
                "\t\t\t\t\t<key>url</key>\n" +
                "\t\t\t\t\t<string></string>\n" +
                "\t\t\t\t</dict>\n" +
                "\t\t\t\t<dict>\n" +
                "\t\t\t\t\t<key>kind</key>\n" +
                "\t\t\t\t\t<string>display-image</string>\n" +
                "\t\t\t\t\t<key>needs-shine</key>\n" +
                "\t\t\t\t\t<true/>\n" +
                "\t\t\t\t\t<key>url</key>\n" +
                "\t\t\t\t\t<string></string>\n" +
                "\t\t\t\t</dict>\n" +
                "\t\t\t</array>\n" +
                "\t\t\t<key>metadata</key>\n" +
                "\t\t\t<dict>\n" +
                "\t\t\t\t<key>bundle-identifier</key>\n" +
                "\t\t\t\t<string>com.dawoo.gamebox.sid" + siteId + "</string>\n" +
                "\t\t\t\t<key>bundle-version</key>\n" +
                "\t\t\t\t<string>" + versionName + "</string>\n" +
                "\t\t\t\t<key>kind</key>\n" +
                "\t\t\t\t<string>software</string>\n" +
                "\t\t\t\t<key>subtitle</key>\n" +
                "\t\t\t\t<string>UDID</string>\n" +
                "\t\t\t\t<key>title</key>\n" +
                "\t\t\t\t<string>" + siteName + "</string>\n" +
                "\t\t\t</dict>\n" +
                "\t\t</dict>\n" +
                "\t</array>\n" +
                "</dict>\n" +
                "</plist>";
        byte bt[] = new byte[1024];
        bt = plist.getBytes();
        try {
            FileOutputStream in = new FileOutputStream(file);
            try {
                in.write(bt, 0, bt.length);
                in.close();
                // boolean success=true;
                System.out.println("写入文件:" + file.getName() +"----成功");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /**
     * 设置所有站点的配置信息
     */
    private static void getAppBuild(){
        String[] ids = new String[]{
//                "1,0001,开发一",
//                "21,rf80,测试01",
//                "71,8l6r,超博娱乐",
//                "76,XH5Z,澳门永利",
//                "110,cabu,超博娱乐",
//                "111,74bk,新葡京娱乐场",
//                "112,SNRM,威尼斯人娱乐",
//                "113,jwdg,伟德娱乐",
//                "114,ojlj,澳门金沙娱乐场",
//                "116,bldz,电子777娱乐",
//                "117,yytt,澳门威尼斯人",
//                "118,ys3q,澳门星际娱乐",
//                "119,nu9r,UEDBET",
//                "120,cvqb,演示站点",
//                "121,pnuw,太阳城集团",
//                "123,seck,皇家赌场",
//                "124,98ph,澳门金沙",
//                "125,bqmn,新葡京娱乐场",
//                "126,8wu8,澳门永利娱乐城",
//                "128,xjc9,新葡京娱乐场",
//                "129,0a74,新葡京娱乐城",
//                "133,xwc7,澳门威尼斯人",
//                "134,o7av,澳门威尼斯人",
//                "135,miv5,澳门金沙娱乐场",
//                "136,5rdu,澳门威尼斯人",
//                "140,ix2i,澳门威尼斯人",
//                "141,hihk,BET365",
//                "142,vbgt,澳门新葡京娱乐场",
//                "143,zhcu,太阳城娱乐城",
//                "150,zz1g,威尼斯人",
//                "151,tloz,bet365",
//                "153,cqkv,澳门威尼斯人",
//                "155,zqq5,澳门银河娱乐城",
//                "156,xmil,新葡京娱乐场",
//                "157,uhte,澳门金沙赌场",
//                "158,7p44,九狮国际娱乐城",
//                "159,mhi7,澳门威尼斯人赌场",
//                "161,n0o7,bet365亚洲官网",
//                "162,bgst,葡京娱乐场",
//                "163,duzr,FUNGAME",
//                "165,g7oq,澳门赌场",
//                "167,hzy3,澳门新葡京赌场",
//                "168,lont,澳门威尼斯人",
                "169,c79k,澳门银河在线赌场Casino",
                "171,ihqx,宝开娱乐",
                "172,izbv,豪森国际",
                "173,jr3j,濠利会娱乐城",
                "175,x0le,澳门银河娱乐城",
                "176,qgjl,全胜娱乐",
//                "177,x1dv,澳门威尼斯人娱乐场",
//                "178,ptxa,点金坊",
//                "179,rosz,英雄联盟",
//                "180,qfxk,皇冠国际",
//                "181,4w3g,超博娱乐",
//                "182,ixuf,雄伟集团",
//                "183,nrpf,美高梅赌场",
//                "185,fyxi,フェニックス・エンターテイメント"
        };

        /* SELECT '"'||ss.id||','||ss.code||','||si."value"||'",' FROM sys_site ss LEFT JOIN site_i18n si ON ss."id" = si.site_id WHERE si.locale = 'zh_CN' AND si."type"='site_name' AND ss.status<>'2' AND ss.id > 183 order by ss.id */

        File flavor = new File(ROOT_PATH + "android/Flavors.go");
        if (flavor.exists()) flavor.delete();

        for (int i = 0; i < ids.length; i++) {
            Integer siteId = Integer.valueOf(ids[i].split(",")[0]);
            String code = ids[i].split(",")[1];
            String name = ids[i].split(",")[2];
//            getIosPlist(code, "1.0.5", siteId, name);
            getIosBuild(siteId, name, code);
//            getAndroidApk(code, "3.1.0");
//            getAndroidFlavors(siteId, name, code);
//            getIosImage(siteId);
        }

        try {
            System.out.println(String.format("共 %d 个站点", ids.length));
            // 调用默认程序打开文件
            Desktop.getDesktop().open(new File(ROOT_PATH + "android/Flavors.go"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取安卓app配置信息
     * @param siteId
     * @param name
     * @param code
     */
    private static void getAndroidFlavors(Integer siteId, String name, String code){
        String theme = "default.skin";
        if (siteId == 119) theme = "blue.skin";
        else if (siteId == 141 || siteId == 161) theme = "green.skin";
        else if (siteId == 185) theme = "pink.skin";

        StringBuilder flavor = new StringBuilder();
        flavor.append("_").append(code).append(" {");
        flavor.append("\n\t").append("applicationId ").append("\"com.dawoo.gamebox.sid").append(siteId).append("\"");
        flavor.append("\n\t").append("resValue ").append("\"string\", ").append("\"app_name\", ").append("\"").append(name).append("\"");
        flavor.append("\n\t").append("resValue ").append("\"string\", ").append("\"app_code\", ").append("\"").append(code).append("\"");
        flavor.append("\n\t").append("resValue ").append("\"string\", ").append("\"app_sid\", ").append("\"").append(CryptoTool.aesEncrypt(String.valueOf(siteId), code)).append("\"");
        flavor.append("\n\t").append("resValue ").append("\"string\", ").append("\"theme\", ").append("\"").append(theme).append("\"");
        flavor.append("\n\t").append("manifestPlaceholders = [app_icon:\"@mipmap/app_icon_").append(siteId).append("\"]");
        flavor.append("\n}\n");

        try {
            String path = ROOT_PATH + "android";
            File pathFile = new File(path);
            if (!pathFile.exists()) {
                pathFile.mkdirs();
                System.out.println("创建目录:" + path + "----成功");
            }

            File file = new File(path + "/Flavors.go");
            if (file.createNewFile()) {
                System.out.println("创建文件/home/fei/app/android/Flavors.go！");
            }

            appendConent(path + "/Flavors.go", flavor.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void appendConent(String fileName, String content) {
        FileWriter writer = null;
        try {
            // 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
            writer = new FileWriter(fileName, true);
            writer.write(content);
        } catch (IOException e) {
            LOG.error(e, "写入文件出错");
        } finally {
            try {
                if(writer != null){
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取ios App站点信息
     * @param siteId
     * @param name
     * @param code
     */
    private static void getIosBuild(Integer siteId, String name, String code){
        System.out.println("#elif _" + code + " //" + name + "\n" +
                "\n" +
                "#define CODE   @\"" + code + "\"\n" +
                "#define S   @\"" + CryptoTool.aesEncrypt(String.valueOf(siteId), code) + "\"\n" +
                "#define SID   @\"" + siteId + "\"\n" +
                "#define COLOR   @\"" + "DEFAULT" + "\"\n\n");
    }

    /**
     * 获取IOS桌面图标和配置信息
     * @param siteId
     */
    public static void getIosImage(Integer siteId){
        String path = ROOT_PATH + "ios/ios_app_icon/AppIcon-" + siteId + ".appiconset";
        File imageFile1 = new File(ROOT_PATH + "ios/120x120-icon/app_icon_" + siteId + ".png");
        File imageFile2 = new File(ROOT_PATH + "ios/180x180-icon/app_icon_" + siteId + ".png");

        File pathFile = new File(path);
        if(!pathFile.exists()){
            pathFile.mkdirs();
            System.out.println("创建目录:" + path +"----成功");
        }
        File contentFile = new File(path, "Contents.json");

        imageFile1.renameTo(new File(path + "/app_icon_" + siteId + ".png"));
        imageFile2.renameTo(new File(path + "/app_icon_" + siteId + "-1.png"));
        try {
            contentFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String contents = "{\n" +
                "  \"images\" : [\n" +
                "    {\n" +
                "      \"idiom\" : \"iphone\",\n" +
                "      \"size\" : \"20x20\",\n" +
                "      \"scale\" : \"2x\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"idiom\" : \"iphone\",\n" +
                "      \"size\" : \"20x20\",\n" +
                "      \"scale\" : \"3x\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"idiom\" : \"iphone\",\n" +
                "      \"size\" : \"29x29\",\n" +
                "      \"scale\" : \"1x\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"idiom\" : \"iphone\",\n" +
                "      \"size\" : \"29x29\",\n" +
                "      \"scale\" : \"2x\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"idiom\" : \"iphone\",\n" +
                "      \"size\" : \"29x29\",\n" +
                "      \"scale\" : \"3x\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"idiom\" : \"iphone\",\n" +
                "      \"size\" : \"40x40\",\n" +
                "      \"scale\" : \"2x\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"idiom\" : \"iphone\",\n" +
                "      \"size\" : \"40x40\",\n" +
                "      \"scale\" : \"3x\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"idiom\" : \"iphone\",\n" +
                "      \"size\" : \"57x57\",\n" +
                "      \"scale\" : \"1x\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"idiom\" : \"iphone\",\n" +
                "      \"size\" : \"57x57\",\n" +
                "      \"scale\" : \"2x\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"size\" : \"60x60\",\n" +
                "      \"idiom\" : \"iphone\",\n" +
                "      \"filename\" : \"app_icon_" + siteId + ".png\",\n" +
                "      \"scale\" : \"2x\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"size\" : \"60x60\",\n" +
                "      \"idiom\" : \"iphone\",\n" +
                "      \"filename\" : \"app_icon_" + siteId + "-1.png\",\n" +
                "      \"scale\" : \"3x\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"info\" : {\n" +
                "    \"version\" : 1,\n" +
                "    \"author\" : \"xcode\"\n" +
                "  }\n" +
                "}";
        byte bt[] = new byte[1024];
        bt = contents.getBytes();
        try {
            FileOutputStream in = new FileOutputStream(contentFile);
            try {
                in.write(bt, 0, bt.length);
                in.close();
                // boolean success=true;
                System.out.println("写入文件:" + contentFile.getName() +"----成功");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 将安卓APK放在以code命名的文件夹中
     * @param code
     * @param versionName
     */
    public static void getAndroidApk(String code, String versionName){
        File codeFile = new File(ROOT_PATH + "android/" + versionName + "/" + code);
        if(!codeFile.exists()){
            codeFile.mkdirs();
        }
        File file = new File(ROOT_PATH + "android/" + versionName + "/app_" + code + "_" + versionName + ".apk");
        File renameFile = new File(ROOT_PATH + "android/" + versionName + "/" + code + "/app_" + code + "_" + versionName + ".apk");

        if(file.exists()){
            file.renameTo(renameFile);
        }
    }

    public static void main(String[] args) {
        getAppBuild();
        System.out.println("---android版本号加密：" + md5SysUserPermission("6", "android"));
        System.out.println("---ios版本号加密：" + md5SysUserPermission("3", "ios"));
    }
}