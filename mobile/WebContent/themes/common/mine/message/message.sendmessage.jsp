<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/include/include.inc.jsp" %>
<div class="mui-row">
    <!--发送消息-->
    <form id="messageForm">
        <gb:token/>
        <div id="validateRule" style="display: none">${validate}</div>
        <div class="notice-form">
            <div class="mui-input-group mine-form">
                <div class="mui-input-row">
                    <label for="">${views.mine_auto['类型']}：</label>
                    <div class="ct">
                        <div class="gb-select">
                            <a href="#advisoryType" class="m-r-0"><span id="displayType">${views.mine_auto['请选择']}</span>
                                <input type="hidden" name="result.advisoryType">
                                <i class="mui-icon mui-icon-arrowdown"></i>
                            </a>
                        </div>
                    </div>
                </div>
                <div class="mui-input-row"><label for="">${views.mine_auto['标题']}：</label>
                    <div class="ct">
                        <input type="text" placeholder="${views.mine_auto['请输入标题']}" name="result.advisoryTitle">
                    </div>
                </div>
                <div class="mui-input-row" style="line-height: 0; padding: 10px;">
                    <textarea placeholder="${views.mine_auto['输入内容']}" name="result.advisoryContent"></textarea>
                </div>
                <div class="mui-input-row _captcha" id="captcha_div" style="${isOpenCaptcha ? "" :'display: none;'}">
                <div class="form-row">
                    <div class="cont">
                        <input type="text" class="mui-input ico6" maxlength="4" placeholder="${views.mine_auto['请输入验证码']}" name="captcha" id="captcha">
                        <div class="gb-vcode">
                            <img class="_captcha_img" src="${root}/captcha/feedback.html" data-src="${root}/captcha/feedback.html" alt="">
                        </div>
                    </div>
                </div>
            </div>
            </div>
        </div>
        <div class="gb-form-foot" style="padding: 0 40px;">
            <div class="mui-pull-right" style="width: 47%;">
                <a href="" class="mui-btn mui-btn-primary submit" id="advisoryReset">${views.common_report['取消']}</a>
            </div>
            <a id="advisorySubmit" class="mui-btn mui-btn-primary submit" style="width: 47%;">${views.mine_auto['提交']}</a>
        </div>
    </form>

</div>
