package so.wwb.gamebox.mobile.App.model;

import so.wwb.gamebox.model.master.operation.po.VActivityMessage;

import java.util.List;
import java.util.Map;

/**
 * Created by legend on 18-1-2.
 */

    /**
     * Created by legend on 18-1-2.
     */
    public class ActivityTypeApp {
        /**
         *  拿出来的是一个活动的类别， (其他优惠，测试优惠)
         */
        /**  */
        private Integer id;
        /** 模块编号 */
        private String module;
        /** 国际化信息类型 */
        private String type;
        /** 国际化key */
        private String key;
        /** 两位小写语言代码_两位大写国家代码 */
        private String locale;
        /** 国际化后的值 */
        private String value;
        /** 站点id */
        private Integer siteId;
        /** 备注 */
        private String remark;
        /** 默认值 */
        private String defaultValue;
        /** 是否内置 */
        private Boolean builtIn;

        private String cacheKey;
        private String cacheKeyLocale;

        /**
         * 对应类别 数据的列表
         */
        private Map<String,List<VActivityMessage>> typeMessageMap;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getModule() {
            return module;
        }

        public void setModule(String module) {
            this.module = module;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getLocale() {
            return locale;
        }

        public void setLocale(String locale) {
            this.locale = locale;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public Integer getSiteId() {
            return siteId;
        }

        public void setSiteId(Integer siteId) {
            this.siteId = siteId;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

        public String getDefaultValue() {
            return defaultValue;
        }

        public void setDefaultValue(String defaultValue) {
            this.defaultValue = defaultValue;
        }

        public Boolean getBuiltIn() {
            return builtIn;
        }

        public void setBuiltIn(Boolean builtIn) {
            this.builtIn = builtIn;
        }

        public String getCacheKey() {
            return cacheKey;
        }

        public void setCacheKey(String cacheKey) {
            this.cacheKey = cacheKey;
        }

        public String getCacheKeyLocale() {
            return cacheKeyLocale;
        }

        public void setCacheKeyLocale(String cacheKeyLocale) {
            this.cacheKeyLocale = cacheKeyLocale;
        }

        public Map<String, List<VActivityMessage>> getTypeMessageMap() {
            return typeMessageMap;
        }

        public void setTypeMessageMap(Map<String, List<VActivityMessage>> typeMessageMap) {
            this.typeMessageMap = typeMessageMap;
        }
    }

