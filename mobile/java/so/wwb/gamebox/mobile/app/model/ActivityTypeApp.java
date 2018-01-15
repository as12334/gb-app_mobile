package so.wwb.gamebox.mobile.app.model;

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

        private Integer id;


        private String activityKey; // 活动类别的　key
        private String activityTypeName; //活动类别　具体的名称


        public String getActivityTypeName() {
            return activityTypeName;
        }

        public void setActivityTypeName(String activityTypeName) {
            this.activityTypeName = activityTypeName;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getActivityKey() {
            return activityKey;
        }

        public void setActivityKey(String activityKey) {
            this.activityKey = activityKey;
        }
    }

