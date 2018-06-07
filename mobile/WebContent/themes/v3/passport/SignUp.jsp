<%--@elvariable id="field" type="java.util.List<so.wwb.gamebox.model.master.setting.po.FieldSort>"--%>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="../include/include.inc.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <title>${views.passport_auto['注册']}</title>
    <%@ include file="../include/include.head.jsp" %>
    <link rel="stylesheet" type="text/css" href="${resRoot}/themes/mui.poppicker.css"/>
    <link rel="stylesheet" type="text/css" href="${resRoot}/themes/mui.picker.css"/>
    <link rel="stylesheet" type="text/css" href="${resRoot}/themes/mui.dtpicker.css"/>
</head>
<body>
<!-- 主页面标题 -->
<header class="mui-bar mui-bar-nav">
    <a style="color: #fff;" class="mui-icon mui-icon-arrowleft mui-pull-left"
       data-rel='{"target":"goToLastPage","opType":"function"}'></a>
    <h1 class="mui-title">${views.passport_auto['会员注册']}</h1>
</header>

<div class="mui-content mui-scroll-wrapper mui-content-without-footer-address login-content">
    <div class="mui-scroll">
        <!-- 主界面具体展示内容 -->
        <section class="type-content">
            <div id="register_content" class="mui-control-content mui-active register-part"><!--注册部分的内容-->
                <form class="mui-input-group" id="regForm">
                    <div id="validateRule" style="display: none">${rule}</div>
                    <input type="hidden" value='${requiredJson}' name="requiredJson">
                    <input type="hidden" value="${registCode}" name="recommendRegisterCode"/>
                    <input type="hidden" name="editType" value="player">
                    <c:if test="${registCodeField}">
                        <div class="mui-input-row">
                            <label>${views.register['signUp.recommendUserInputCode']}${isRequiredForRegisterCode?'<i class="icon-star"></i>':''}</label>
                            <input type="text" name="recommendUserInputCode" class="mui-input-clear" autocomplete="off"
                                   value="${registCode}"
                                   placeholder="${isRequiredForRegisterCode ? views.passport_auto['请输入推荐码'] : views.passport_auto['无推荐人可不填写']}">
                        </div>
                    </c:if>
                    <c:forEach items="${field}" var="i">
                        <%--验证码放在最后，介绍人放在最前面--%>
                        <c:if test="${i.name!='verificationCode'&&i.name!='regCode'&&i.name!='serviceTerms'}">
                            <c:set var="isRequired" value="${i.isRequired!='2'}"/>
                            <c:choose>
                                <%--生日--%>
                                <c:when test="${i.name=='birthday'}">
                                    <div class="mui-input-row">
                                        <label>${views.passport_auto['生日']}${isRequired?'<i class="icon-star"></i>':''}</label>
                                        <c:set var="minYear"
                                               value="${soulFn:formatDateTz(params['minDate'], DateFormat.YEAR, timeZone)}"/>
                                        <c:set var="endYear"
                                               value="${soulFn:formatDateTz(params['maxDate'], DateFormat.YEAR, timeZone)}"/>
                                        <input type="text" id="dateButton"
                                               data-options='{"type":"date","beginYear":${minYear},"endYear":${endYear}}'
                                               class="mui-input-select" readonly=""
                                               placeholder="${views.register['signUp.sysUser.birthday']}">
                                        <input type="hidden" name="sysUser.birthday" id="sysUser.birthday" value=""/>
                                        <span class="mui-icon mui-icon-arrowdown"></span>
                                    </div>
                                </c:when>
                                <%--登录密码--%>
                                <c:when test="${i.name=='password'}">
                                    <div class="mui-input-row ">
                                        <c:set var="name" value="${signUpDataMap[i.name]}"/>
                                        <c:set var="key" value="signUp.${name}"/>
                                        <label>${views.register[key]}${isRequired?'<i class="icon-star"></i>':''}</label>
                                        <input type="password" class="mui-input-password" name="${name}"
                                               placeholder="${views.passport_auto['密码长度']}">
                                    </div>
                                    <div class="mui-input-row">
                                        <label>${views.register['signUp.sysUser.confirmPassword']}${isRequired?'<i class="icon-star"></i>':''}</label>
                                        <input type="password" name="confirmPassword" class="mui-input-password"
                                               placeholder="${views.passport_auto['再次输入您的登录密码']}">
                                    </div>
                                </c:when>
                                <%--安全密码--%>
                                <c:when test="${i.name=='paymentPassword'}">
                                    <div class="mui-input-row ">
                                        <c:set var="name" value="${signUpDataMap[i.name]}"/>
                                        <c:set var="key" value="signUp.${name}"/>
                                        <label>${views.register[key]}${isRequired?'<i class="icon-star"></i>':''}</label>
                                        <input type="password" name="${name}" maxlength="6" class="mui-input-password"
                                               placeholder="${views.passport_auto['输入6位数字密码']}">
                                    </div>
                                    <div class="mui-input-row ">
                                        <label>${views.register['signUp.sysUser.confirmPermissionPwd']}${isRequired?'<i class="icon-star"></i>':''}</label>
                                        <input type="password" name="confirmPermissionPwd" maxlength="6"
                                               class="mui-input-password"
                                               placeholder="${views.passport_auto['再次输入6位数字密码']}">
                                    </div>
                                </c:when>
                                <%--时区--%>
                                <c:when test="${i.name=='defaultTimezone'}">
                                    <div class="mui-input-row">
                                        <label>${views.passport_auto['时区']}${isRequired?'<i class="icon-star"></i>':''}</label>
                                        <c:set var="zone" value="${params['timezone']}"/>
                                        <input type="text" class="mui-input-select" readonly
                                               placeholder="${dicts.common.time_zone[zone]}">
                                        <input type="hidden" name="sysUser.defaultTimezone" value="${zone}"/>
                                        <span class="mui-icon mui-icon-arrowdown"></span>
                                    </div>
                                </c:when>
                                <%---货币--%>
                                <c:when test="${i.name=='mainCurrency'}">
                                    <div class="mui-input-row">
                                        <label>${views.passport_auto['货币']}${isRequired?'<i class="icon-star"></i>':''}</label>
                                        <input type="text" id="currencyButton" class="mui-input-select" readonly
                                               placeholder="${dicts.common.currency[params.currency]}">
                                        <input type="hidden" name="sysUser.defaultCurrency" id="sysUser.defaultCurrency"
                                               value="${params.currency}"/>
                                        <span class="mui-icon mui-icon-arrowdown"></span>
                                    </div>
                                </c:when>
                                <%--主语言--%>
                                <c:when test="${i.name=='defaultLocale'}">
                                    <div class="mui-input-row">
                                        <label>${views.passport_auto['主语言']}${isRequired?'<i class="icon-star"></i>':''}</label>
                                        <input type="text" id="localeButton" class="mui-input-select" readonly
                                               placeholder="${fn:substringBefore(dicts.common.language['zh_CN'], '#')}">
                                        <input type="hidden" name="sysUser.defaultLocale" id="sysUser.defaultLocale"
                                               value="zh_CN"/>
                                        <span class="mui-icon mui-icon-arrowdown"></span>
                                    </div>
                                </c:when>
                                <%--性别--%>
                                <c:when test="${i.name=='sex'}">
                                    <div class="mui-input-row">
                                        <label>${views.passport_auto['性别']}${isRequired?'<i class="icon-star"></i>':''}</label>
                                        <div id="sexDiv">
                                            <input type="text" id="sexButton" class="mui-input-select" readonly
                                                   placeholder="${views.register['signUp.sysUser.sex']}">
                                            <input type="hidden" name="sysUser.sex" id="sysUser.sex" value=""/>
                                            <span class="mui-icon mui-icon-arrowdown"></span>
                                        </div>
                                    </div>
                                </c:when>
                                <%--安全问题--%>
                                <c:when test="${i.name=='securityIssues'}">
                                    <div class="mui-input-row">
                                        <label>${views.register['signUp.sysUserProtection.question1']}${isRequired?'<i class="icon-star"></i>':''}</label>
                                        <input type="text" id="questionButton" class="mui-input-select" readonly
                                               placeholder=" ${views.register['signUp.sysUserProtection.question1']}">
                                        <input type="hidden" name="sysUserProtection.question1"
                                               id="sysUserProtection.question1" value=""/>
                                        <span class="mui-icon mui-icon-arrowdown"></span>
                                    </div>

                                    <div class="mui-input-row">
                                        <label>${views.passport_auto['回答安全问题']}${isRequired?'<i class="icon-star"></i>':''}</label>
                                        <input type="text" name="sysUserProtection.answer1" maxlength="30"
                                               class="mui-input-clear"
                                               placeholder="${views.register['signUp.sysUserProtection.answer1']}">
                                    </div>
                                </c:when>
                                <%--email--%>
                                <c:when test="${i.name=='201'}">
                                    <div class="mui-input-row">
                                        <c:set var="name" value="${signUpDataMap[i.name]}"/>
                                        <c:set var="key" value="signUp.${name}"/>
                                        <label>${views.passport_auto['邮箱']}${isRequired||isEmail?'<i class="icon-star"></i>':''}</label>
                                        <input type="text" name="${name}" class="mui-input-clear mui-input"
                                               placeholder="${views.register[key]}">
                                    </div>
                                    <c:if test="${isEmail}">
                                        <div class="mui-input-row">
                                            <input name="checkEmail" value="checkEmail" type="hidden">
                                            <input type="text" name="emailCode"
                                                   placeholder="*${views.register['signUp.emailCode']}">
                                            <div class="gb-vcode">
                                                <button id="sendEmailCode"
                                                        data-rel='{"target":"sendEmailCode","opType":"function"}'
                                                        class="mui-btn-primary mui-btn-outlined"
                                                        type="button">${views.passport_auto['发送验证码']}</button>
                                            </div>
                                        </div>
                                    </c:if>
                                </c:when>
                                <%--手机--%>
                                <c:when test="${i.name=='110'}">
                                    <div class="mui-input-row">
                                        <c:set var="name" value="${signUpDataMap[i.name]}"/>
                                        <c:set var="key" value="signUp.${name}"/>
                                        <label>${views.register[key]}${isRequired||isPhone?'<i class="icon-star"></i>':''}</label>
                                        <input type="text" name="${name}" class="mui-input-clear mui-input"
                                               placeholder="${views.register[key]}">
                                    </div>
                                    <c:if test="${isPhone}">
                                        <div class="mui-input-row">
                                            <input name="checkPhone" value="checkPhone" type="hidden"/>
                                            <input name="phone.status" value="11" type="hidden"/>
                                            <input type="text" name="phoneCode" class=mui-input"
                                                   placeholder="*${views.register['signUp.phoneCode']}">
                                            <div class="gb-vcode">
                                                <button id="sendPhoneCode"
                                                        data-rel='{"target":"sendPhoneCode","opType":"function"}'
                                                        class="mui-btn-primary mui-btn-outlined"
                                                        type="button">${views.passport_auto['发送验证码']}</button>
                                            </div>
                                        </div>
                                    </c:if>
                                </c:when>
                                <c:otherwise>
                                    <div class="mui-input-row">
                                        <c:set var="name" value="${signUpDataMap[i.name]}"/>
                                        <c:set var="key" value="signUp.${name}"/>
                                        <label>${views.register[key]}${isRequired?'<i class="icon-star"></i>':''}</label>
                                        <input type="text" name="${name}" class="mui-input-clear mui-input"
                                               placeholder="${views.register[key]}">
                                    </div>
                                </c:otherwise>
                            </c:choose>
                        </c:if>
                    </c:forEach>
                    <div class="mui-input-row">
                        <label>${views.passport_auto['请输入验证码']}<i class="icon-star"></i></label>
                        <div class="mui-flex">
                            <input type="text" class="mui-input-clear" placeholder="${views.passport_auto['请输入验证码']}"
                                   maxlength="4" name="captchaCode">
                            <img data-rel='{"target":"captchaImg","opType":"function","src":"${root}/captcha/pmregister.html"}'
                                 data-src="${root}/captcha/pmregister.html" class="captcha_img"/>
                        </div>
                    </div>
                    <div class="mui-input-row mui-checkbox mui-left tk">
                        <a data-rel='{"target":"terms","opType":"function"}'>${views.register['signUp.termsOfService']}</a>
                        <input name="termsOfService" value="11" type="checkbox"
                               data-rel='{"target":"termsOfService","opType":"function"}' checked>
                    </div>
                    <div class="mui-button-row">
                        <button type="button" class="mui-btn mui-btn-block btn-ok"
                                data-rel='{"target":"register","opType":"function"}'>立即注册
                        </button>
                    </div>
                </form>
            </div>
        </section>
    </div> <!--mui-scroll 闭合标签-->
</div>  <!--mui-content 闭合标签-->
</body>
<%@ include file="../include/include.js.jsp" %>
<script src="${resRoot}/js/mui/mui.poppicker.js?v=${rcVersion}"></script>
<script src="${resRoot}/js/mui/mui.picker.js?v=${rcVersion}"></script>
<script src="${resRoot}/js/mui/mui.dtpicker.js?v=${rcVersion}"></script>
<script src="${resRoot}/js/passport/SignUp.js?v=${rcVersion}"></script>
</html>
<%@ include file="/include/include.footer.jsp" %>