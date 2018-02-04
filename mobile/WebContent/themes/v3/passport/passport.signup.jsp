<%--@elvariable id="field" type="java.util.List<so.wwb.gamebox.model.master.setting.po.FieldSort>"--%>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/include/include.inc.jsp" %>

<body>
<!-- 主页面标题 -->
<header class="mui-bar mui-bar-nav mui-bar-blue">
    <a class="mui-icon mui-icon-arrowleft mui-pull-left"></a>
    <h1 class="mui-title">注册</h1>
</header>
<div class="mui-content mui-scroll-wrapper mui-content-without-footer-address login-content">
    <div class="mui-scroll">
        <!-- 主界面具体展示内容 -->
        <section class="type-content">
            <div id="register_content" class="mui-control-content mui-active register-part"><!--注册部分的内容-->
                <form class="mui-input-group">
                    <div id="validateRule" style="display: none">${rule}</div>
                    <input type="hidden" value='${requiredJson}' name="requiredJson">
                    <input type="hidden" value="${registCode}" name="recommendRegisterCode"/>
                    <input type="hidden" name="editType" value="player">

                    <c:if test="${registCodeField}">
                        <div class="mui-input-row">
                            <label>${views.register['signUp.recommendUserInputCode']}${isRequiredForRegisterCode?'<i class="icon-star"></i>':''}</label>
                            <input type="text" name="recommendUserInputCode" class="mui-input-clear" autocomplete="off"
                                   value="${registCode}" placeholder="${isRequiredForRegisterCode ? views.passport_auto['请输入推荐码'] : views.passport_auto['无推荐人可不填写']}">
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
                                        <label >${views.passport_auto['生日']}${isRequired?'<i class="icon-star"></i>':''}</label>
                                        <c:set var="minYear" value="${soulFn:formatDateTz(params['minDate'], DateFormat.YEAR, timeZone)}"/>
                                        <c:set var="endYear" value="${soulFn:formatDateTz(params['maxDate'], DateFormat.YEAR, timeZone)}"/>
                                        <button id="dateButton" data-options='{"type":"date","beginYear":${minYear},"endYear":${endYear}}'>
                                                ${views.register['signUp.sysUser.birthday']}</button>
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
                                        <input type="password" class="mui-input-password" name="${name}" placeholder="${views.passport_auto['密码长度']}">
                                    </div>
                                    <div class="mui-input-row">
                                        <label>${views.register['signUp.sysUser.confirmPassword']}${isRequired?'<i class="icon-star"></i>':''}</label>
                                        <input type="password" name="confirmPassword" class="mui-input-password" placeholder="${views.passport_auto['密码长度']}">
                                    </div>
                                </c:when>
                                <%--安全密码--%>
                                <c:when test="${i.name=='paymentPassword'}">
                                    <div class="mui-input-row ">
                                        <c:set var="name" value="${signUpDataMap[i.name]}"/>
                                        <c:set var="key" value="signUp.${name}"/>
                                        <label>${views.register[key]}${isRequired?'<i class="icon-star"></i>':''}</label>
                                        <input type="password" name="${name}" maxlength="6" class="mui-input-password" placeholder="${views.passport_auto['输入6位数字密码']}">
                                    </div>
                                    <div class="mui-input-row ">
                                        <label>${views.register['signUp.sysUser.confirmPermissionPwd']}${isRequired?'<i class="icon-star"></i>':''}</label>
                                        <input type="password" name="confirmPermissionPwd" maxlength="6" class="mui-input-password" placeholder="${views.passport_auto['再次输入6位数字密码']}">
                                    </div>
                                </c:when>
                                <%--时区--%>
                                <c:when test="${i.name=='defaultTimezone'}">
                                    <div class="mui-input-row">
                                        <label>${views.passport_auto['时区']}${isRequired?'<i class="icon-star"></i>':''}</label>
                                        <c:set var="zone" value="${params['timezone']}"/>
                                        <button class="mui-disabled" disabled >${dicts.common.time_zone[zone]}</button>
                                        <input type="hidden" name="sysUser.defaultTimezone" value="${zone}"/>
                                        <span class="mui-icon mui-icon-arrowdown"></span>
                                    </div>
                                </c:when>
                                <%---货币--%>
                                <c:when test="${i.name=='mainCurrency'}">
                                    <div class="mui-input-row">
                                        <label>${views.passport_auto['货币']}${isRequired?'<i class="icon-star"></i>':''}</label>
                                        <button id="currencyButton" >${dicts.common.currency[params.currency]}</button>
                                        <input type="hidden" name="sysUser.defaultCurrency" id="sysUser.defaultCurrency" value="${params.currency}"/>
                                        <span class="mui-icon mui-icon-arrowdown"></span>
                                    </div>
                                </c:when>
                                <%--主语言--%>
                                <c:when test="${i.name=='defaultLocale'}">
                                    <div class="mui-input-row">
                                        <label>${views.passport_auto['主语言']}${isRequired?'<i class="icon-star"></i>':''}</label>
                                        <button id="localeButton" >${fn:substringBefore(dicts.common.language['zh_CN'], '#')}</button>
                                        <input type="hidden" name="sysUser.defaultLocale" id="sysUser.defaultLocale" value="zh_CN"/>
                                        <span class="mui-icon mui-icon-arrowdown"></span>
                                    </div>
                                </c:when>
                                <%--性别--%>
                                <c:when test="${i.name=='sex'}">
                                    <div class="mui-input-row">
                                        <label>${views.passport_auto['性别']}${isRequired?'<i class="icon-star"></i>':''}</label>
                                        <button id="sexButton" >${views.register['signUp.sysUser.sex']}</button>
                                        <input type="hidden" name="sysUser.sex" id="sysUser.sex" value=""/>
                                        <span class="mui-icon mui-icon-arrowdown"></span>
                                    </div>
                                </c:when>
                                <%--安全问题--%>
                                <c:when test="${i.name=='securityIssues'}">
                                    <div class="mui-input-row">
                                        <label>${views.register['signUp.sysUserProtection.question1']}${isRequired?'<i class="icon-star"></i>':''}</label>
                                        <button id="questionButton"
                                                style="overflow: hidden;white-space: nowrap;text-overflow: ellipsis;">
                                                ${views.register['signUp.sysUserProtection.question1']}
                                        </button>
                                        <input type="hidden" name="sysUserProtection.question1" id="sysUserProtection.question1" value=""/>
                                        <span class="mui-icon mui-icon-arrowdown"></span>
                                    </div>
                                    <div class="mui-input-row">
                                        <label>${views.passport_auto['回答安全问题']}${isRequired?'<i class="icon-star"></i>':''}</label>
                                        <input type="text" name="sysUserProtection.answer1" maxlength="30"
                                               class="mui-input-clear" placeholder="${views.register['signUp.sysUserProtection.answer1']}">
                                    </div>
                                </c:when>
                                <%--email--%>
                                <c:when test="${i.name=='201'}">
                                    <div class="mui-input-row">
                                        <c:set var="name" value="${signUpDataMap[i.name]}"/>
                                        <c:set var="key" value="signUp.${name}"/>
                                        <label>${isRequired||isEmail?'<span class="red">*</span>':''}${views.passport_auto['邮箱']}</label>
                                        <input type="text" name="${name}" class="mui-input-clear mui-input" placeholder="${views.register[key]}">

                                    </div>
                                    <c:if test="${isEmail}">
                                        <div class="mui-input-row">
                                            <input name="checkEmail" value="checkEmail" type="hidden">
                                            <input type="text" name="emailCode" placeholder="*${views.register['signUp.emailCode']}">
                                            <div class="gb-vcode">
                                                <button id="sendEmailCode" class="mui-btn-primary mui-btn-outlined" type="button">${views.passport_auto['发送验证码']}</button>
                                            </div>
                                        </div>
                                    </c:if>
                                </c:when>
                                <%--手机--%>
                                <c:when test="${i.name=='110'}">
                                    <div class="mui-input-row">
                                        <c:set var="name" value="${signUpDataMap[i.name]}"/>
                                        <c:set var="key" value="signUp.${name}"/>
                                        <label>${isRequired||isPhone?'<span class="red">*</span>':''}${views.register[key]}</label>
                                        <input type="text" name="${name}" class="mui-input-clear mui-input" placeholder="${views.register[key]}">


                                    </div>
                                    <c:if test="${isPhone}">
                                        <div class="mui-input-row">
                                            <input name="checkPhone" value="checkPhone" type="hidden">
                                            <input type="text" name="phoneCode" class=mui-input" placeholder="*${views.register['signUp.phoneCode']}">
                                            <div class="gb-vcode">
                                                <button id="sendPhoneCode" class="mui-btn-primary mui-btn-outlined" type="button">${views.passport_auto['发送验证码']}</button>
                                            </div>

                                        </div>
                                    </c:if>
                                </c:when>
                                <c:otherwise>
                                    <div class="mui-input-row">
                                        <c:set var="name" value="${signUpDataMap[i.name]}"/>
                                        <c:set var="key" value="signUp.${name}"/>
                                        <label>${isRequired?'<span class="red">*</span>':''}${views.register[key]}</label>
                                        <input type="text" name="${name}" class="mui-input-clear mui-input" placeholder="${views.register[key]}">

                                    </div>
                                </c:otherwise>
                            </c:choose>
                        </c:if>
                    </c:forEach>

                    <div class="mui-input-row">
                        <label class="mui-ellipsis">输入用户名<i class="icon-star"></i></label>
                        <input type="text" class="mui-input-clear" placeholder="输入8-20位字母数字组合">
                    </div>
                    <div class="mui-input-row">
                        <label>输入密码<i class="icon-star"></i></label>
                        <input type="password" class="mui-input-password" placeholder="输入8-20位字母数字组合">
                    </div>
                    <div class="mui-input-row">
                        <label>输入确认密码<i class="icon-star"></i></label>
                        <input type="password" class="mui-input-password" placeholder="输入8-20位字母数字组合">
                    </div>
                    <div class="mui-input-row">
                        <label>输入真实姓名<i class="icon-star"></i></label>
                        <input type="text" class="mui-input-clear" placeholder="输入与银行卡相同姓名">
                    </div>
                    <div class="mui-input-row">
                        <label>输入取款密码<i class="icon-star"></i></label>
                        <input type="password" class="mui-input-password" placeholder="输入6位数字密码">
                    </div>
                    <div class="mui-input-row">
                        <label>主语言</label>
                        <input type="text" class="mui-input-select" readonly placeholder="请选择语言">
                        <span class="mui-icon mui-icon-arrowdown"></span>
                    </div>
                    <div class="mui-input-row">
                        <label>安全问题</label>
                        <input type="text" class="mui-input-select" readonly placeholder="请选择问题">
                        <span class="mui-icon mui-icon-arrowdown"></span>
                    </div>
                    <div class="mui-input-row">
                        <label>请输入回答</label>
                        <input type="text" class="mui-input-clear" placeholder="请选择安全问题正确答案">
                    </div>
                    <div class="mui-input-row">
                        <label>请输入验证码</label>
                        <div class="mui-flex">
                            <input type="text" class="mui-input-clear" placeholder="请输入验证码">
                            <img src="../../mobile-v3/images/code.png"/>
                        </div>
                    </div>
                    <div class="mui-input-row mui-checkbox mui-left tk">
                        <a>我已满合法博彩年龄，同意各项开户条约</a>
                        <input name="checkbox1" value="Item 1" type="checkbox" checked>
                    </div>
                    <div class="mui-button-row">
                        <button type="button" class="mui-btn mui-btn-block btn-ok" >立即注册</button>
                    </div>
                </form>
            </div>
        </section>
    </div> <!--mui-scroll 闭合标签-->
</div>  <!--mui-content 闭合标签-->
</body>
<%@ include file="../include/include.js.jsp" %>


<%--
<body class="gb-theme mine-page index register">
    <header class="mui-bar mui-bar-nav ${os eq 'android'?'mui-hide':''}">
        <%@ include file="/include/include.toolbar.jsp" %>
        <h1 class="mui-title">${views.passport_auto['会员注册']}</h1>
        <c:if test="${os ne 'app_ios' }">
        <a class="mui-icon mui-icon-home mui-pull-right" data-href="/mainIndex.html"></a>
        </c:if>
    </header>
    <!--滚动区域-->
    <div class="mui-content mui-scroll-wrapper mui-fullscreen" ${os eq 'android'?'style="padding-top:0!important"':''}>
        <div>
            <div class="gb-fullpage">
                <div class="form-wrap">
                    <form id="regForm" class="mui-input-group">
                <div id="validateRule" style="display: none">${rule}</div>
                <input type="hidden" value='${requiredJson}' name="requiredJson">
                <input type="hidden" value="${registCode}" name="recommendRegisterCode"/>
                <input type="hidden" name="editType" value="player">

                        <c:if test="${registCodeField}">
                            <div class="mui-input-row">
                                <label>${isRequiredForRegisterCode?'<span class="red">*</span>':''}${views.register['signUp.recommendUserInputCode']}</label>
                                <input type="text" name="recommendUserInputCode" class="mui-input-clear " autocomplete="off"
                                       value="${registCode}" placeholder="${isRequiredForRegisterCode ? views.passport_auto['请输入推荐码'] : views.passport_auto['无推荐人可不填写']}">
                            </div>
                        </c:if>

                        <c:forEach items="${field}" var="i">
                            &lt;%&ndash;验证码放在最后，介绍人放在最前面&ndash;%&gt;
                            <c:if test="${i.name!='verificationCode'&&i.name!='regCode'&&i.name!='serviceTerms'}">
                                <c:set var="isRequired" value="${i.isRequired!='2'}"/>
                                <c:choose>
                                    &lt;%&ndash;生日&ndash;%&gt;
                                    <c:when test="${i.name=='birthday'}">
                                        <div class="mui-input-row">
                                            <label >${isRequired?'<span class="red">*</span>':''}${views.passport_auto['生日']}</label>
                                            <c:set var="minYear" value="${soulFn:formatDateTz(params['minDate'], DateFormat.YEAR, timeZone)}"/>
                                            <c:set var="endYear" value="${soulFn:formatDateTz(params['maxDate'], DateFormat.YEAR, timeZone)}"/>
                                            <button id="dateButton" data-options='{"type":"date","beginYear":${minYear},"endYear":${endYear}}'
                                                    >${views.register['signUp.sysUser.birthday']}</button>
                                            <input type="hidden" name="sysUser.birthday" id="sysUser.birthday" value=""/>
                                            <span class="mui-icon mui-icon-arrowdown"></span>
                                        </div>
                                    </c:when>
                                    &lt;%&ndash;登录密码&ndash;%&gt;
                                    <c:when test="${i.name=='password'}">
                                        <div class="mui-input-row ">
                                            <c:set var="name" value="${signUpDataMap[i.name]}"/>
                                            <c:set var="key" value="signUp.${name}"/>
                                            <label>${isRequired?'<span class="red">*</span>':''}${views.register[key]}</label>
                                            <input type="password" class="mui-input-password" name="${name}" placeholder="${views.passport_auto['密码长度']}">
                                        </div>
                                        <div class="mui-input-row">
                                            <label>${isRequired?'<span class="red">*</span>':''}${views.register['signUp.sysUser.confirmPassword']}</label>
                                            <input type="password" name="confirmPassword" class="mui-input-password" placeholder="${views.passport_auto['密码长度']}">
                                        </div>
                                    </c:when>
                                    &lt;%&ndash;安全密码&ndash;%&gt;
                                    <c:when test="${i.name=='paymentPassword'}">
                                        <div class="mui-input-row ">
                                            <c:set var="name" value="${signUpDataMap[i.name]}"/>
                                            <c:set var="key" value="signUp.${name}"/>
                                            <label>${isRequired?'<span class="red">*</span>':''}${views.register[key]}</label>
                                            <input type="password" name="${name}" maxlength="6" class="mui-input-password" placeholder="${views.passport_auto['输入6位数字密码']}">
                                        </div>
                                        <div class="mui-input-row ">
                                            <label>${isRequired?'<span class="red">*</span>':''}${views.register['signUp.sysUser.confirmPermissionPwd']}</label>
                                            <input type="password" name="confirmPermissionPwd" maxlength="6" class="mui-input-password" placeholder="${views.passport_auto['再次输入6位数字密码']}">
                                        </div>
                                    </c:when>
                                    &lt;%&ndash;时区&ndash;%&gt;
                                    <c:when test="${i.name=='defaultTimezone'}">
                                        <div class="mui-input-row">
                                            <label style="width: 10%;margin-top: 10px;">${isRequired?'<span class="red">*</span>':''}${views.passport_auto['时区']}</label>
                                            <c:set var="zone" value="${params['timezone']}"/>
                                            <button class="mui-disabled" disabled >${dicts.common.time_zone[zone]}</button>
                                            <input type="hidden" name="sysUser.defaultTimezone" value="${zone}"/>
                                            <span class="mui-icon mui-icon-arrowdown"></span>
                                        </div>
                                    </c:when>
                                    &lt;%&ndash;-货币&ndash;%&gt;
                                    <c:when test="${i.name=='mainCurrency'}">
                                        <div class="mui-input-row">
                                            <label style="width: 10%;margin-top: 10px;">${isRequired?'<span class="red">*</span>':''}${views.passport_auto['货币']}</label>
                                            <button id="currencyButton" >${dicts.common.currency[params.currency]}</button>
                                            <input type="hidden" name="sysUser.defaultCurrency" id="sysUser.defaultCurrency" value="${params.currency}"/>
                                            <span class="mui-icon mui-icon-arrowdown"></span>
                                        </div>
                                    </c:when>
                                    &lt;%&ndash;主语言&ndash;%&gt;
                                    <c:when test="${i.name=='defaultLocale'}">
                                        <div class="mui-input-row">
                                            <label style="width: 10%;margin-top: 10px;">${isRequired?'<span class="red">*</span>':''}${views.passport_auto['主语言']}</label>
                                            <button id="localeButton" >${fn:substringBefore(dicts.common.language['zh_CN'], '#')}</button>
                                            <input type="hidden" name="sysUser.defaultLocale" id="sysUser.defaultLocale" value="zh_CN"/>
                                            <span class="mui-icon mui-icon-arrowdown"></span>
                                        </div>
                                    </c:when>
                                    &lt;%&ndash;性别&ndash;%&gt;
                                    <c:when test="${i.name=='sex'}">
                                        <div class="mui-input-row">
                                            <label style="width: 10%;margin-top: 10px;">${isRequired?'<span class="red">*</span>':''}${views.passport_auto['性别']}</label>
                                            <button id="sexButton" >${views.register['signUp.sysUser.sex']}</button>
                                            <input type="hidden" name="sysUser.sex" id="sysUser.sex" value=""/>
                                            <span class="mui-icon mui-icon-arrowdown"></span>
                                        </div>
                                    </c:when>
                                    &lt;%&ndash;安全问题&ndash;%&gt;
                                    <c:when test="${i.name=='securityIssues'}">
                                        <div class="mui-input-row">
                                            <label style="width: 10%;margin-top: 10px;">${isRequired?'<span class="red">*</span>':''}${views.register['signUp.sysUserProtection.question1']}</label>
                                            <button id="questionButton"
                                                    style="overflow: hidden;white-space: nowrap;text-overflow: ellipsis;">
                                                    ${views.register['signUp.sysUserProtection.question1']}
                                            </button>
                                            <input type="hidden" name="sysUserProtection.question1" id="sysUserProtection.question1" value=""/>
                                            <span class="mui-icon mui-icon-arrowdown"></span>
                                        </div>
                                        <div class="mui-input-row">
                                            <label>${isRequired?'<span class="red">*</span>':''}${views.passport_auto['回答安全问题']}</label>
                                                    <input type="text" name="sysUserProtection.answer1" maxlength="30"
                                                           class="mui-input-clear" placeholder="${views.register['signUp.sysUserProtection.answer1']}">
                                        </div>
                                    </c:when>
                                    &lt;%&ndash;email&ndash;%&gt;
                                    <c:when test="${i.name=='201'}">
                                        <div class="mui-input-row">
                                                    <c:set var="name" value="${signUpDataMap[i.name]}"/>
                                                    <c:set var="key" value="signUp.${name}"/>
                                            <label>${isRequired||isEmail?'<span class="red">*</span>':''}${views.passport_auto['邮箱']}</label>
                                                    <input type="text" name="${name}" class="mui-input-clear mui-input" placeholder="${views.register[key]}">

                                        </div>
                                        <c:if test="${isEmail}">
                                            <div class="mui-input-row">
                                                        <input name="checkEmail" value="checkEmail" type="hidden">
                                                        <input type="text" name="emailCode" placeholder="*${views.register['signUp.emailCode']}">
                                                        <div class="gb-vcode">
                                                            <button id="sendEmailCode" class="mui-btn-primary mui-btn-outlined" type="button">${views.passport_auto['发送验证码']}</button>
                                                        </div>
                                            </div>
                                        </c:if>
                                    </c:when>
                                    &lt;%&ndash;手机&ndash;%&gt;
                                    <c:when test="${i.name=='110'}">
                                        <div class="mui-input-row">
                                                    <c:set var="name" value="${signUpDataMap[i.name]}"/>
                                                    <c:set var="key" value="signUp.${name}"/>
                                            <label>${isRequired||isPhone?'<span class="red">*</span>':''}${views.register[key]}</label>
                                                    <input type="text" name="${name}" class="mui-input-clear mui-input" placeholder="${views.register[key]}">


                                        </div>
                                        <c:if test="${isPhone}">
                                            <div class="mui-input-row">
                                                        <input name="checkPhone" value="checkPhone" type="hidden">
                                                        <input type="text" name="phoneCode" class=mui-input" placeholder="*${views.register['signUp.phoneCode']}">
                                                        <div class="gb-vcode">
                                                            <button id="sendPhoneCode" class="mui-btn-primary mui-btn-outlined" type="button">${views.passport_auto['发送验证码']}</button>
                                                        </div>

                                            </div>
                                        </c:if>
                                    </c:when>
                                    <c:otherwise>
                                        <div class="mui-input-row">
                                                    <c:set var="name" value="${signUpDataMap[i.name]}"/>
                                                    <c:set var="key" value="signUp.${name}"/>
                                            <label>${isRequired?'<span class="red">*</span>':''}${views.register[key]}</label>
                                                    <input type="text" name="${name}" class="mui-input-clear mui-input" placeholder="${views.register[key]}">

                                        </div>
                                    </c:otherwise>
                                </c:choose>
                            </c:if>
                        </c:forEach>

                        <div class="mui-input-row final y-code">
                            <div class="text" style="background: none">
                                    <span class="vcode">
                                        <img src="${root}/captcha/pmregister.html"
                                             data-src="${root}/captcha/pmregister.html" alt="" class="captcha_img">
                                    </span>
                            </div>
                            <label><span class="red">*</span>${views.passport_auto['请输入验证码']}</label>
                            <input type="text" class="ico6" placeholder="${views.passport_auto['请输入验证码']}" maxlength="4" name="captchaCode">
                        </div>
                    <div class="mui-input-row mui-checkbox" style="height: 48px">
                        <label></label>
                        <input name="termsOfService" value="11" type="checkbox" checked >
                        <a  style="height: 40px;line-height: 40px;vertical-align: text-bottom;font-size: 12px;text-decoration: underline;" data-href="${root}/index/protocol.html">${views.register['signUp.termsOfService']}</a>
                    </div>
                    <div class="gb-form-foot p-t-0">
                        <button id='login' class="submit mui-btn mui-btn-primary  _register" type="button">${views.passport_auto['确认']}</button>
                    </div>
            </form>
                </div>
            </div>
        </div>
        <div class="mui-off-canvas-backdrop"></div>
    </div>
</body>
<%@ include file="/include/include.base.js.common.jsp" %>
<script type="text/javascript" src="${root}/mobile/message_<%=SessionManagerCommon.getLocale().toString()%>.js?v=${rcVersion}"></script>
<script>
    var language = '${language.replace('_','-')}';
    var isLogin = '${isLogin}';
</script>
<script src="${resRoot}/js/mui/mui.min.js?v=${rcVersion}"></script>
<script src="${resComRoot}/js/jquery/jquery-2.1.1.min.js?v=${rcVersion}"></script>
<script src="${resComRoot}/js/jquery/plugins/jquery.validate/jquery.validate.min.js?v=${rcVersion}"></script>
<script src="${resRoot}/js/jquery/jquery.validate.extend.mobile.js?v=${rcVersion}"></script>
<script src="${resRoot}/js/common/global.js?v=${rcVersion}"></script>
<script src="${resRoot}/js/mui/mui.poppicker.js?v=${rcVersion}" type="text/javascript" charset="utf-8"></script>
<script src="${resRoot}/js/mui/mui.picker.js?v=${rcVersion}" type="text/javascript" charset="utf-8"></script>
<script src="${resRoot}/js/mui/mui.dtpicker.js?v=${rcVersion}" type="text/javascript" charset="utf-8"></script>
<script src="${resRoot}/js/passport/SignUp.js?v=${rcVersion}"></script>
--%>
