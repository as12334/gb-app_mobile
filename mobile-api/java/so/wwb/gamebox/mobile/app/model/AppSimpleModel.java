package so.wwb.gamebox.mobile.app.model;

public class AppSimpleModel{

	private String code;

	private String name;

	private Boolean status;

	public static AppSimpleModel groupAppSimpleModel(String code , String name){
		AppSimpleModel appSimpleModel = new AppSimpleModel();
		appSimpleModel.setCode(code);
		appSimpleModel.setName(name);
		return appSimpleModel;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Boolean getStatus() {
		return status;
	}

	public void setStatus(Boolean status) {
		this.status = status;
	}
}