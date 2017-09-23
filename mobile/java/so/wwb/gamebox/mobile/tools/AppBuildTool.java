package so.wwb.gamebox.mobile.tools;

import org.soul.commons.lang.string.StringTool;
import org.soul.commons.log.Log;
import org.soul.commons.log.LogFactory;
import org.soul.commons.security.AesTool;
import org.soul.commons.security.CryptoTool;
import org.soul.commons.security.DigestTool;
import so.wwb.gamebox.model.boss.po.AppUpdate;

import java.awt.*;
import java.io.*;

/**
 *
 * Created by bill on 3/21/16.
 */
public class AppBuildTool {

    private static final Log LOG = LogFactory.getLog(AppBuildTool.class);

    private static final String ROOT_PATH = "/home/fei/app/";

    private static final String SALT_CODE = "ONFwe#(*FAS&$-932+)#9JI830*#@<-90-13~32@#%SDF*_(#DSF&*FSE!DO&$DW";

    private static String sysUserPermissionSalt(String username) {
        if (StringTool.isNotBlank(username)) {
            String SALT_SYS_USER_PWD =      "AgDz+&?MB|G{*%bH%pf@w4y8;Avt>0R9en,~.L]|db[16=Jr.)0wa^U@~%QNe(Xk";
            return SALT_SYS_USER_PWD + username;
        }
        return "bnQElt-?a:nCYg@!|>kt42HvjdEaZ_?f.CR`MGKqDrXozKnGFo%*f;{C,|F+H`9r";
    }

    /**
     * 安全密码
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
        byte bt[];
        bt = plist.getBytes();
        try {
            FileOutputStream in = new FileOutputStream(file);
            try {
                in.write(bt, 0, bt.length);
                in.close();
                System.out.println("写入文件:" + file.getName() +"----成功");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    /**
     * 设置所有站点的配置信息
     */
    private static void getAppBuild(){
        String[] ids = new String[]{
                "11,FHAN,肥肥的",
                /*
                "69,7cxt,百发彩票",
                "70,1wl5,天天彩票",
                "71,8l6r,超博娱乐",
                "76,XH5Z,澳门永利",
                "110,cabu,超博娱乐",
                "111,74bk,新葡京娱乐场",
                "112,SNRM,威尼斯人娱乐",
                "113,jwdg,伟德娱乐",
                "114,ojlj,澳门金沙娱乐场",
                "116,bldz,电子777娱乐",
                "117,yytt,澳门威尼斯人",
                "118,ys3q,澳门星际娱乐",
                "119,nu9r,UEDBET",
                "120,cvqb,演示站点",
                "121,pnuw,太阳城集团",
                "123,seck,皇家赌场",
                "124,98ph,澳门金沙",
                "125,bqmn,新葡京娱乐场",
                "126,8wu8,澳门永利娱乐城",
                "129,0a74,新葡京娱乐城",
                "133,xwc7,澳门威尼斯人",
                "134,o7av,澳门威尼斯人",
                "135,miv5,澳门金沙娱乐场",
                "136,5rdu,澳门威尼斯人",
                "140,ix2i,澳门威尼斯人",
                "141,hihk,BET365",
                "142,vbgt,澳门新葡京娱乐场",
                "143,zhcu,太阳城娱乐城",
                "150,zz1g,威尼斯人",
                "151,tloz,bet365",
                "153,cqkv,澳门威尼斯人",
                "155,zqq5,澳门银河娱乐城",
                "156,xmil,新葡京娱乐场",
                "157,uhte,澳门金沙赌场",
                "158,7p44,九狮国际娱乐城",
                "159,mhi7,澳门威尼斯人赌场",
                "161,n0o7,bet365亚洲官网",
                "162,bgst,葡京娱乐场",
                "163,duzr,FUNGAME",
                "165,g7oq,澳门赌场",
                "167,hzy3,澳门新葡京赌场",
                "168,lont,澳门威尼斯人",
                "169,c79k,澳门银河在线赌场Casino",
                "171,ihqx,宝开娱乐",
                "172,izbv,豪森国际",
                "173,jr3j,濠利会娱乐城",
                "175,x0le,澳门银河娱乐城",
                "176,qgjl,全胜娱乐",
                "177,x1dv,澳门威尼斯人娱乐场",
                "178,ptxa,点金坊",
                "179,rosz,威尼斯人",
                "180,qfxk,皇冠国际",
                "181,4w3g,超博娱乐",
                "182,ixuf,雄伟集团",
                "183,nrpf,美高梅赌场",*/
                "185,fyxi,Phoenix Gaming"/*,
                "186,cwad,赛博体育",
                "187,b02h,澳门银河",
                "188,acpb,鸿泰国际",
                "189,a56r,澳门永利贵宾会",
                "190,yj4v,万博体育",
                "191,d1hg,澳门赌场",
                "192,gc7p,星河娱乐城",
                "193,f9wn,皇冠娱乐",
                "195,xjvs,大唐娱乐城",
                "196,6rrt,澳门巴黎人",
                "197,sn2m,完美彩票",
                "198,urbr,澳门金沙娱乐场",
                "199,n5ns,澳门威尼斯人",
                "200,cghs,钱多多娱乐城",
                "201,vtfw,澳门威尼斯人",
                "202,ucuy,博亿娱乐城",
                "203,q5tj,中博娱乐城",
                "205,dfvp,百乐博",
                "206,lnd9,葡京国际",
                "207,xlei,BET365",
                "208,npsa,彩中彩",
                "209,arau,大发OK",
                "210,1lgt,威廉希尔",
                "211,3qj8,大发OK",
                "212,cmu6,金元宝娱乐城",
                "213,8y1c,万豪国际",
                "215,mjiu,盈泰娱乐",
                "216,cbe1,COD娱樂"*/
        };

        /* SELECT '"'||ss.id||','||ss.code||','||si."value"||'",' FROM sys_site ss LEFT JOIN site_i18n si ON ss."id" = si.site_id WHERE si.locale = 'zh_CN' AND si."type"='site_name' AND ss.status<>'2' AND ss.id > 183 order by ss.id; */

        File flavor = new File(ROOT_PATH + "android/Flavors.go");
        if (flavor.exists()) flavor.delete();
        File build = new File(ROOT_PATH + "ios/Build.go");
        if (build.exists()) build.delete();

        for (int i = 0; i < ids.length; i++) {
            Integer siteId = Integer.valueOf(ids[i].split(",")[0]);
            String code = ids[i].split(",")[1];
            String name = ids[i].split(",")[2];
//            getIosPlist(code, "2.1.0", siteId, name);
//            getIosBuild(siteId, name, code);
//            getIosImage(siteId);
            getAndroidFlavors(siteId, name, code);
//            getAndroidApk(code, "3.1.5");
        }

        System.out.println(String.format("共 %d 个站点", ids.length));

        try {
            // 调用默认程序打开文件
            Desktop.getDesktop().open(new File(ROOT_PATH + "android/Flavors.go"));
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            try {
                Desktop.getDesktop().open(new File(ROOT_PATH + "ios/Build.go"));
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }

    /**
     * 获取安卓app配置信息
     */
    private static void getAndroidFlavors(Integer siteId, String name, String code){
        StringBuilder flavor = new StringBuilder();
        flavor.append("_").append(code).append(" {");
        flavor.append("\n\t").append("applicationId ").append("\"com.dawoo.gamebox.sid").append(siteId).append("\"");
        flavor.append("\n\t").append("resValue ").append("\"string\", ").append("\"app_name\", ").append("\"").append(name).append("\"");
        flavor.append("\n\t").append("resValue ").append("\"string\", ").append("\"app_code\", ").append("\"").append(code).append("\"");
        flavor.append("\n\t").append("resValue ").append("\"string\", ").append("\"app_sid\", ").append("\"").append(CryptoTool.aesEncrypt(String.valueOf(siteId), code)).append("\"");
        flavor.append("\n\t").append("resValue ").append("\"string\", ").append("\"site_type\", ").append("\"").append(setSiteType(siteId)).append("\"");
        flavor.append("\n\t").append("resValue ").append("\"string\", ").append("\"theme\", ").append("\"").append(setTheme(siteId)).append("\"");
        flavor.append("\n\t").append("resValue ").append("\"string\", ").append("\"app_logo\", ").append("\"app_logo_").append(siteId).append("\"");
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
                System.out.println("创建文件: app/android/Flavors.go！");
            }

            appendConent(path + "/Flavors.go", flavor.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String setSiteType(Integer siteId) {
        String siteType = "integrated";
        if (siteId == 69 || siteId == 70 || siteId == 197)
            siteType = "lottery";
        return siteType;
    }

    private static String setTheme(Integer siteId) {
        String theme = "default.skin";
        if (siteId == 119 || siteId == 171) theme = "blue.skin";
        else if (siteId == 141 || siteId == 161 || siteId == 207) theme = "green.skin";
        else if (siteId == 185) theme = "pink.skin";
        else if (siteId == 69 || siteId == 70 || siteId == 197)
            theme = "lottery.skin";
        return theme;
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
     */
    private static void getIosBuild(Integer siteId, String name, String code){
        StringBuilder builder = new StringBuilder();
        builder.append("#elif _").append(code).append(" //").append(name).append("\n\n");
        builder.append("#define CODE\t@").append("\"").append(code).append("\"\n");
        builder.append("#define S\t\t@").append("\"").append(CryptoTool.aesEncrypt(String.valueOf(siteId), code)).append("\"\n");
        builder.append("#define SID\t\t@").append("\"").append(siteId).append("\"\n");
        builder.append("#define THEME\t@").append("\"").append(setTheme(siteId)).append("\"\n");
        builder.append("#define SITE_TYPE\t@").append("\"").append(setSiteType(siteId)).append("\"\n\n");

        try {
            String path = ROOT_PATH + "ios";
            File pathFile = new File(path);
            if (!pathFile.exists()) {
                pathFile.mkdirs();
                System.out.println("创建目录:" + path + "----成功");
            }

            File file = new File(path + "/Build.go");
            if (file.createNewFile()) {
                System.out.println("创建文件: app/ios/Build.go！");
            }

            appendConent(path + "/Build.go", builder.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取IOS桌面图标和配置信息
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
        byte bt[];
        bt = contents.getBytes();
        try {
            FileOutputStream in = new FileOutputStream(contentFile);
            try {
                in.write(bt, 0, bt.length);
                in.close();
                System.out.println("写入文件:" + contentFile.getName() +"----成功");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
//        getAppBuild();
        System.out.println("---android版本号加密：" + md5SysUserPermission("21", "android"));
        try {
            System.out.println("---android版本号加密：" + AesTool.encrypt("19", AppUpdate.KEY_UPDATE));
//          System.out.println("---ios版本号加密：" + md5SysUserPermission("7", "ios"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
