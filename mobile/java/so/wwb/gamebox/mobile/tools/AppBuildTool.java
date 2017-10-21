package so.wwb.gamebox.mobile.tools;

import com.google.common.io.Files;
import org.soul.commons.log.Log;
import org.soul.commons.log.LogFactory;
import org.soul.commons.security.AesTool;
import org.soul.commons.security.CryptoTool;
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
        writeFile(file, plist);

    }

    /**
     * 写入文件
     * @param file
     * @param name
     */
    private static void writeFile(File file, String name) {
        byte bt[];
        bt = name.getBytes();
        try {
            FileOutputStream in = new FileOutputStream(file);
            try {
                in.write(bt, 0, bt.length);
                in.close();
                LOG.info("写入文件:" + file.getName() +"----成功");
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
        String[] sites = new String[]{
                /*"69,7cxt,一指通彩票",        "70,1wl5,天天彩票",          "71,8l6r,超博娱乐",        "76,XH5Z,澳门永利",
                "110,cabu,超博娱乐",         "111,74bk,新葡京娱乐场",     "112,SNRM,威尼斯人娱乐",    "113,jwdg,伟德娱乐",
                "114,ojlj,金沙娱乐场",       "116,bldz,电子777娱乐",     "117,yytt,澳门威尼斯人",    "118,ys3q,澳门星际娱乐",
                "119,nu9r,UEDBET",          "120,cvqb,演示站点",        "121,pnuw,太阳城集团",      "123,seck,皇家赌场",
                "124,98ph,澳门金沙",         "126,8wu8,永利娱乐城",       "129,0a74,新葡京娱乐城",    "130,ixpm,澳门新葡京",
                "133,xwc7,澳门威尼斯人",      "134,o7av,澳门威尼斯人",     "135,miv5,澳门金沙娱乐场",   "136,5rdu,澳门威尼斯人",
                "140,ix2i,澳门威尼斯人",      "141,hihk,BET365",         "142,vbgt,新葡京娱乐场",     "143,zhcu,太阳城娱乐城",
                "150,zz1g,威尼斯人",         "151,tloz,bet365",         "153,cqkv,澳门威尼斯人",     "155,zqq5,澳门银河娱乐城",
                "156,xmil,新葡京娱乐场",     "157,uhte,澳门金沙赌场",      "158,7p44,九狮国际娱乐城",  "159,mhi7,威尼斯人赌场",
                "161,n0o7,BET365亚洲",      "162,bgst,葡京娱乐场",       "163,duzr,FUNGAME",        "165,g7oq,澳门赌场",
                "167,hzy3,新葡京赌场",       "168,lont,澳门威尼斯人",      "171,ihqx,宝开娱乐",        "172,izbv,豪森国际",
                "173,jr3j,濠利会娱乐城",     "175,x0le,银河娱乐城",       "176,qgjl,全胜娱乐",        "177,x1dv,威尼斯人娱乐场",
                "178,ptxa,点金坊",           "179,rosz,澳门威尼斯人",     "180,qfxk,皇冠国际",         "181,4w3g,超博娱乐",
                "182,ixuf,雄伟集团",         "183,nrpf,美高梅赌场",       "185,fyxi,Phoenix Gaming",  "186,cwad,赛博体育",
                "187,b02h,澳门银河",         "188,acpb,鸿泰国际",         "189,a56r,永利贵宾会",       "190,yj4v,万博体育",
                "191,d1hg,澳门赌场",         "192,gc7p,星河娱乐城",       "193,f9wn,皇冠娱乐",        "195,xjvs,大唐娱乐城",
                "196,6rrt,澳门巴黎人",        "197,sn2m,完美彩票",         "198,urbr,金沙娱乐场",      "199,n5ns,澳门威尼斯人",
                "200,cghs,钱多多娱乐城",      "201,vtfw,澳门威尼斯人",     "202,ucuy,博亿娱乐城",       "203,q5tj,中博娱乐城",
                "205,dfvp,百乐博",           "206,lnd9,葡京国际",         "207,xlei,bet365",         "208,npsa,彩中彩",
                "209,arau,大发OK",           "210,1lgt,威廉希尔",         "211,3qj8,大发OK",         "212,cmu6,金元宝娱乐城",
                "213,8y1c,万豪国际",         "215,mjiu,盈泰娱乐",         "216,cbe1,COD娱樂",         "217,w7ls,云顶国际",
                "218,osxg,永利娱乐城",        "219,tcjp,鼎彩国际",         "220,yrdy,皇朝娱乐",        "221,ionm,金沙娱乐场",
                "222,4hwq,美高梅娱乐城",      "223,elpc,大咖汇",           "225,gwkk,永利娱乐场",      "226,oiqg,新葡京娱乐场",
                "227,mkoz,银河娱乐城",        "229,ixyu,美高梅娱乐城",      "228,idr9,亚盈国际",       "800,7vhp,四海娱乐",
                "801,cx7r,万达彩票",          "230,r7pt,金沙娱乐城",       "231,vxcb,新濠国际",        "232,z1yn,新亚洲",
                "233,87lr,金沙娱乐城",        "235,cspr,美高梅娱乐城",      "236,8gez,拉斯维加斯国际",   "802,98jb,凤凰彩票",
                "237,akm1,金沙娱乐城",        "238,wlf6,BET365",          "803,yg9x,头彩",           "239,5e7b,云顶娱乐",*/
//                "805,yrxk,亿彩汇",
//                "251,e2ce,威尼斯人",
//                "252,pox4,COBO超博",
//                "253,7rda,澳门威尼斯人",
//                "255,p0a7,澳门威尼斯人",
//                "256,vqwq,星博国际",
//                "257,wnmt,盛大国际",
                "258,yqgk,金凯娱乐"
        };

        /* SELECT '"'||ss.id||','||ss.code||','||si."value"||'",' FROM sys_site ss LEFT JOIN site_i18n si ON ss."id" = si.site_id WHERE si.locale = 'zh_CN' AND si."type"='site_name' AND ss.status<>'2' AND ss.id not in (75,80) AND ss.id > 183 order by ss.id; */

        File flavor = new File(ROOT_PATH + "android/Flavors.go");
        if (flavor.exists()) flavor.delete();
        File build = new File(ROOT_PATH + "ios/Build.go");
        if (build.exists()) build.delete();

        for (int i = 0; i < sites.length; i++) {
            Integer siteId = Integer.valueOf(sites[i].split(",")[0]);
            String code = sites[i].split(",")[1];
            String name = sites[i].split(",")[2];
            getIosPlist(code, "2.1.0", siteId, name);
            getIosBuild(siteId, name, code);
            getAndroidFlavors(siteId, name, code);
            buildAndroidProject(siteId, code, name);
        }

        LOG.info(String.format("共 %d 个站点", sites.length));

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
        if (siteId == 69 || siteId == 70 || siteId == 197 || siteId == 800 || siteId == 801 || siteId == 802 || siteId == 803 || siteId == 805)
            siteType = "lottery";
        return siteType;
    }

    private static String setTheme(Integer siteId) {
        String theme = "default.skin";
        if (siteId == 119 || siteId == 171) {
            theme = "blue.skin";
        }
        else if (siteId == 141 || siteId == 161 || siteId == 207 || siteId == 238) {
            theme = "green.skin";
        }
        else if (siteId == 185) {
            theme = "pink.skin";
        }
        else if (siteId == 69 || siteId == 70 || siteId == 197 || siteId == 800 || siteId == 801 || siteId == 802 || siteId == 803 || siteId == 805) {
            theme = "lottery.skin";
        }
        return theme;
    }

    private static void copyTheme(Integer siteId, String from, String to) {
        try {
            if (siteId == 119 || siteId == 171) {
                Files.copy(new File(from, "blue.skin"), new File(to, "blue.skin"));
            } else if (siteId == 141 || siteId == 161 || siteId == 207 || siteId == 238) {
                Files.copy(new File(from, "green.skin"), new File(to, "green.skin"));
            } else if (siteId == 185) {
                Files.copy(new File(from, "pink.skin"), new File(to, "pink.skin"));
            } else if (siteId == 69 || siteId == 70 || siteId == 197 || siteId == 800 || siteId == 801 || siteId == 802 || siteId == 803 || siteId == 805) {
                Files.copy(new File(from, "lottery.skin"), new File(to, "lottery.skin"));
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
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
     * 构建Android项目工程
     * @param siteId 站点ID
     * @param code 站点CODE
     */
    private static void buildAndroidProject(Integer siteId, String code, String name) {
        String path = ROOT_PATH + "flavors/app_" + code + "/";
        String resPath = ROOT_PATH + "res/";
        String logoPath = resPath + "drawable-hdpi/";
        String skinPAth = resPath + "skin/";

        // 创建工程根目录
        File pathFile = new File(path);
        if (!pathFile.exists()) {
            pathFile.mkdirs();
            LOG.info("创建Android工程目录【" + siteId + "】" + code);
        }

        String iconName = String.format("app_icon_%d.png", siteId);
        String logoName = String.format("app_logo_%d.png", siteId);
        String makeFile = "makefile";

        String[] mipmaps = {"mipmap-xhdpi", "mipmap-xxhdpi", "mipmap-xxxhdpi"};
        try {
            // 创建mipmap
            for (String mipmap : mipmaps) {
                File file = new File(path, mipmap);
                if (!file.exists()) {
                    file.mkdirs();
                }

                File icon = new File(file.getPath(), iconName);
                Files.copy(new File(String.format("%s%s/%s", resPath, mipmap, iconName)), icon);
                icon.renameTo(new File(icon.getParent(), "app_icon.png"));
                Files.copy(new File(String.format("%s%s/ic_launcher_round.png", resPath, mipmap, iconName)), new File(file.getPath(), "ic_launcher_round.png"));
            }

            File logo = new File(path, logoName);
            Files.copy(new File(logoPath, logoName), new File(path, logoName));
            logo.renameTo(new File(path, "app_logo.png"));
            Files.copy(new File(resPath, makeFile), new File(path, makeFile));

            // 复制主题
            copyTheme(siteId, skinPAth, path);

            //创建参数文件
            createParamFile(siteId, code, name, path);

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

    }

    /**
     * 创建参数文件
     * @throws IOException
     */
    private static void createParamFile(Integer siteId, String code, String name, String path) throws IOException {
        File paramFile = new File(path, "parameter.properties");
        paramFile.createNewFile();

        StringBuilder param = new StringBuilder();
        param.append("${app_name}=").append(name.replace(" ", "_space")).append("\n");
        param.append("${app_code}=").append(code).append("\n");
        param.append("${app_sid}=").append(CryptoTool.aesEncrypt(String.valueOf(siteId), code)).append("\n");
        param.append("${site_type}=").append(setSiteType(siteId)).append("\n");
        param.append("${app_logo}=").append("app_logo").append("\n");
        param.append("${app_icon}=").append("app_icon").append("\n");
        param.append("${applicationId}=").append("com.dawoo.gamebox.sid").append(siteId).append("\n");
        param.append("${theme}=").append(setTheme(siteId)).append("\n");

        writeFile(paramFile, param.toString());
    }

    public static void main(String[] args) {
        getAppBuild();
        try {
            System.out.println("---android版本号加密：" + AesTool.encrypt("24", AppUpdate.KEY_UPDATE));
//            System.out.println("---ios版本号加密：" + md5SysUserPermission("7", "ios"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
