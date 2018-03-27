package so.wwb.gamebox.mobile.app.model;


import org.soul.commons.bean.IEntity;
import org.soul.commons.support.Nonpersistent;


/**
 * 银行表实体
 *
 * @author admin
 * @tableAuthor simon
 * @time 2016-6-28 10:20:44
 */
//region your codes 1
public class AppBank implements IEntity<Integer> {
//endregion your codes 1

	//region your codes 3
	private static final long serialVersionUID = 8421170453616165251L;
	//endregion your codes 3

	//region properties
	/** 主键 */
	private Integer id;
	/** 字典code */
	private String bankName;
	/** 排序 */
	private Integer orderNum;
	/**国际化bankName*/
	private String interlinguaBankName;

	//endregion


	//region constuctors
	public AppBank(){
	}

	public AppBank(Integer id){
		this.id = id;
	}
	//endregion


	//region getters and setters
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer value) {
		this.id = value;
	}
	@org.soul.model.common.Sortable
	public String getBankName() {
		return this.bankName;
	}

	public void setBankName(String value) {
		this.bankName = value;
	}

	@org.soul.model.common.Sortable
	public Integer getOrderNum() {
		return this.orderNum;
	}

	public void setOrderNum(Integer value) {
		this.orderNum = value;
	}

	@Nonpersistent
	public String getInterlinguaBankName() {
		return interlinguaBankName;
	}

	public void setInterlinguaBankName(String interlinguaBankName) {
		this.interlinguaBankName = interlinguaBankName;
	}
	//endregion

	//region your codes 2

	//endregion your codes 2

}