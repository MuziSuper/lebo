package cn.muzisheng.lebo.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 微信开放平台/小程序错误码枚举
 * <p>
 * 包含微信API调用的所有错误码及其描述
 * 错误码参考：https://developers.weixin.qq.com/doc/oplatform/developers/errCode/
 * </p>
 */
@Getter
@AllArgsConstructor
public enum WXCodeEnum {

// ==================== 公共错误码 ====================

    /**
     * 系统繁忙，此时请开发者稍候再试
     */
    SYSTEM_ERROR(-1, "系统繁忙，此时请开发者稍候再试"),

    /**
     * 请求成功
     */
    OK(0, "请求成功"),

    /**
     * 获取 access_token 时 AppSecret 错误，或者 access_token 无效
     */
    INVALID_CREDENTIAL(40001, "获取 access_token 时 AppSecret 错误，或者 access_token 无效。请开发者认真比对 AppSecret 的正确性，或查看是否正在为恰当的公众号调用接口"),

    /**
     * 不合法的凭证类型
     */
    INVALID_GRANT_TYPE(40002, "不合法的凭证类型"),

    /**
     * 不合法的 OpenID
     */
    INVALID_OPENID(40003, "不合法的 OpenID ，请开发者确认 OpenID （该用户）是否已关注公众号，或是否是其他公众号的 OpenID"),

    /**
     * 不合法的媒体文件类型
     */
    INVALID_MEDIA_TYPE(40004, "不合法的媒体文件类型"),

    /**
     * 上传素材文件格式不对
     */
    INVALID_FILE_TYPE(40005, "上传素材文件格式不对"),

    /**
     * 上传素材文件大小超出限制
     */
    INVALID_MEDIA_SIZE(40006, "上传素材文件大小超出限制"),

    /**
     * 不合法的媒体文件 id
     */
    INVALID_MEDIA_ID(40007, "不合法的媒体文件 id"),

    /**
     * 不合法的消息类型
     */
    INVALID_MESSAGE_TYPE(40008, "不合法的消息类型"),

    /**
     * 图片尺寸太大
     */
    INVALID_IMAGE_SIZE(40009, "图片尺寸太大"),

    /**
     * 不合法的语音文件大小
     */
    INVALID_VOICE_SIZE(40010, "不合法的语音文件大小"),

    /**
     * 不合法的视频文件大小
     */
    INVALID_VIDEO_SIZE(40011, "不合法的视频文件大小"),

    /**
     * 不合法的缩略图文件大小
     */
    INVALID_THUMB_SIZE(40012, "不合法的缩略图文件大小"),

    /**
     * 不合法的appid
     */
    INVALID_APPID(40013, "不合法的appid"),

    /**
     * 不合法的 access_token
     */
    INVALID_ACCESS_TOKEN(40014, "不合法的 access_token ，请开发者认真比对 access_token 的有效性（如是否过期），或查看是否正在为恰当的公众号调用接口"),

    /**
     * 不合法的菜单类型
     */
    INVALID_MENU_TYPE(40015, "不合法的菜单类型"),

    /**
     * 不合法的按钮个数
     */
    INVALID_BUTTON_SIZE(40016, "不合法的按钮个数"),

    /**
     * 不合法的按钮类型
     */
    INVALID_BUTTON_TYPE(40017, "不合法的按钮类型"),

    /**
     * 不合法的按钮名字长度
     */
    INVALID_BUTTON_NAME_SIZE(40018, "不合法的按钮名字长度"),

    /**
     * 不合法的按钮 KEY 长度
     */
    INVALID_BUTTON_KEY_SIZE(40019, "不合法的按钮 KEY 长度"),

    /**
     * 不合法的按钮 URL 长度
     */
    INVALID_BUTTON_URL_SIZE(40020, "不合法的按钮 URL 长度"),

    /**
     * 不合法的菜单版本号
     */
    INVALID_MENU_VERSION(40021, "不合法的菜单版本号"),

    /**
     * 不合法的子菜单级数
     */
    INVALID_SUB_MENU_LEVEL(40022, "不合法的子菜单级数"),

    /**
     * 不合法的子菜单按钮个数
     */
    INVALID_SUB_BUTTON_SIZE(40023, "不合法的子菜单按钮个数"),

    /**
     * 不合法的子菜单按钮类型
     */
    INVALID_SUB_BUTTON_TYPE(40024, "不合法的子菜单按钮类型"),

    /**
     * 不合法的子菜单按钮名字长度
     */
    INVALID_SUB_BUTTON_NAME_SIZE(40025, "不合法的子菜单按钮名字长度"),

    /**
     * 不合法的子菜单按钮 KEY 长度
     */
    INVALID_SUB_BUTTON_KEY_SIZE(40026, "不合法的子菜单按钮 KEY 长度"),

    /**
     * 不合法的子菜单按钮 URL 长度
     */
    INVALID_SUB_BUTTON_URL_SIZE(40027, "不合法的子菜单按钮 URL 长度"),

    /**
     * 不合法的自定义菜单使用用户
     */
    INVALID_MENU_API_USER(40028, "不合法的自定义菜单使用用户"),

    /**
     * 无效的 oauth_code
     */
    INVALID_CODE(40029, "无效的 oauth_code"),

    /**
     * 不合法的 refresh_token
     */
    INVALID_REFRESH_TOKEN(40030, "不合法的 refresh_token"),

    /**
     * 不合法的 openid 列表
     */
    INVALID_OPENID_LIST(40031, "不合法的 openid 列表"),

    /**
     * 不合法的 openid 列表长度
     */
    INVALID_OPENID_LIST_SIZE(40032, "不合法的 openid 列表长度"),

    /**
     * 不合法的请求字符，不能包含 \\u*** 格式的字符
     */
    INVALID_CHARSET(40033, "不合法的请求字符，不能包含 \\uxxxx 格式的字符"),

    /**
     * 不合法的参数
     */
    INVALID_ARGS_SIZE(40035, "不合法的参数"),

    /**
     * 不合法的 template_id 长度
     */
    INVALID_TEMPLATE_ID_SIZE(40036, "不合法的 template_id 长度"),

    /**
     * 不合法的 template_id
     */
    INVALID_TEMPLATE_ID(40037, "不合法的 template_id"),

    /**
     * 不合法的请求格式
     */
    INVALID_PACKAGING_TYPE(40038, "不合法的请求格式"),

    /**
     * 不合法的 URL 长度
     */
    INVALID_URL_SIZE(40039, "不合法的 URL 长度"),

    /**
     * 无效的url
     */
    INVALID_URL_DOMAIN(40048, "无效的url"),

    /**
     * 不合法的分组 id
     */
    INVALID_TIMELINE_TYPE(40050, "不合法的分组 id"),

    /**
     * 分组名字不合法
     */
    INVALID_GROUP_NAME(40051, "分组名字不合法"),

    /**
     * 不合法的子菜单按钮 url 域名
     */
    INVALID_SUB_BUTTON_URL_DOMAIN(40054, "不合法的子菜单按钮 url 域名"),

    /**
     * 不合法的菜单按钮 url 域名
     */
    INVALID_BUTTON_URL_DOMAIN(40055, "不合法的菜单按钮 url 域名"),

    /**
     * 删除单篇图文时，指定的 article_idx 不合法
     */
    INVALID_ARTICLE_IDX(40060, "删除单篇图文时，指定的 article_idx 不合法"),

    /**
     * 不合法的 url ，递交的页面被sitemap标记为拦截
     */
    INVALID_URL(40066, "不合法的 url ，递交的页面被sitemap标记为拦截"),

    /**
     * 参数错误
     */
    INVALID_ARGS(40097, "参数错误"),

    /**
     * 缺少参数
     */
    MISSING_PARAMETER(40101, "缺少参数"),

    /**
     * 微信号不合法
     */
    INVALID_USERNAME(40132, "微信号不合法"),

    /**
     * 不支持的图片格式
     */
    INVALID_IMAGE_FORMAT(40137, "不支持的图片格式"),

    /**
     * 请勿添加其他公众号的主页链接
     */
    CONTAIN_OTHER_HOME_PAGE_URL(40155, "请勿添加其他公众号的主页链接"),

    /**
     * oauth_code已使用
     */
    CODE_BEEN_USED(40163, "oauth_code已使用"),

    /**
     * IP地址不在白名单中
     */
    INVALID_IP_NOT_IN_WHITELIST(40164, "IP地址不在白名单中"),

    /**
     * 缺少 access_token 参数
     */
    ACCESS_TOKEN_MISSING(41001, "缺少 access_token 参数"),

    /**
     * 缺少 appid 参数
     */
    APPID_MISSING(41002, "缺少 appid 参数"),

    /**
     * 缺少 refresh_token 参数
     */
    REFRESH_TOKEN_MISSING(41003, "缺少 refresh_token 参数"),

    /**
     * 缺少 secret 参数
     */
    APPSECRET_MISSING(41004, "缺少 secret 参数"),

    /**
     * 缺少多媒体文件数据，传输素材无视频或图片内容
     */
    MEDIA_DATA_MISSING(41005, "缺少多媒体文件数据，传输素材无视频或图片内容"),

    /**
     * 缺少 media_id 参数
     */
    MEDIA_ID_MISSING(41006, "缺少 media_id 参数"),

    /**
     * 缺少子菜单数据
     */
    SUB_MENU_DATA_MISSING(41007, "缺少子菜单数据"),

    /**
     * 缺少 oauth code
     */
    MISSING_CODE(41008, "缺少 oauth code"),

    /**
     * 缺少 openid
     */
    MISSING_OPENID(41009, "缺少 openid"),

    /**
     * 缺失 url 参数
     */
    MISSING_URL(41010, "缺失 url 参数"),

    /**
     * 缺少必要的请求参数
     */
    MISSING_REQUIRED_FIELDS(41011, "缺少必要的请求参数"),

    /**
     * access_token 超时
     */
    ACCESS_TOKEN_EXPIRED(42001, "access_token 超时，请检查 access_token 的有效期"),

    /**
     * refresh_token 超时
     */
    REFRESH_TOKEN_EXPIRED(42002, "refresh_token 超时"),

    /**
     * oauth_code 超时
     */
    CODE_EXPIRED(42003, "oauth_code 超时"),

    /**
     * 用户修改微信密码，access_token 和 refresh_token 失效，需要重新授权
     */
    ACCESS_TOKEN_AND_REFRESH_TOKEN_EXCEPTION(42007, "用户修改微信密码，accesstoken 和 refreshtoken 失效，需要重新授权"),

    /**
     * 需要 GET 请求
     */
    REQUIRE_GET_METHOD(43001, "需要 GET 请求"),

    /**
     * 需要 POST 请求
     */
    REQUIRE_POST_METHOD(43002, "需要 POST 请求"),

    /**
     * 需要 HTTPS 请求
     */
    REQUIRE_HTTPS(43003, "需要 HTTPS 请求"),

    /**
     * 需要接收者关注
     */
    REQUIRE_SUBSCRIBE(43004, "需要接收者关注"),

    /**
     * 需要好友关系
     */
    REQUIRE_FRIEND_RELATIONS(43005, "需要好友关系"),

    /**
     * 需要将接收者从黑名单中移除
     */
    REQUIRE_REMOVE_BLACKLIST(43019, "需要将接收者从黑名单中移除"),

    /**
     * 多媒体文件为空
     */
    EMPTY_MEDIA_DATA(44001, "多媒体文件为空"),

    /**
     * POST 的数据包为空
     */
    EMPTY_POST_DATA(44002, "POST 的数据包为空"),

    /**
     * 图文消息内容为空
     */
    EMPTY_NEWS_DATA(44003, "图文消息内容为空"),

    /**
     * 文本消息内容为空
     */
    EMPTY_CONTENT(44004, "文本消息内容为空"),

    /**
     * 空白的列表
     */
    EMPTY_LIST_SIZE(44005, "空白的列表"),

    /**
     * 消息内容超过限制
     */
    CONTENT_SIZE_OUT_OF_LIMIT(45002, "消息内容超过限制"),

    /**
     * 标题字段超过限制
     */
    TITLE_SIZE_OUT_OF_LIMIT(45003, "标题字段超过限制"),

    /**
     * 描述字段超过限制
     */
    DESCRIPTION_SIZE_OUT_OF_LIMIT(45004, "描述字段超过限制"),

    /**
     * 链接字段超过限制
     */
    URL_SIZE_OUT_OF_LIMIT(45005, "链接字段超过限制"),

    /**
     * 图片链接字段超过限制
     */
    PICURL_SIZE_OUT_OF_LIMIT(45006, "图片链接字段超过限制"),

    /**
     * 语音播放时间超过限制
     */
    PLAYTIME_OUT_OF_LIMIT(45007, "语音播放时间超过限制"),

    /**
     * 图文消息超过限制
     */
    ARTICLE_SIZE_OUT_OF_LIMIT(45008, "图文消息超过限制"),

    /**
     * 接口调用超过限制
     */
    REACH_MAX_API_DAILY_QUOTA_LIMIT(45009, "接口调用超过限制"),

    /**
     * 创建菜单个数超过限制
     */
    CREATE_MENU_LIMIT(45010, "创建菜单个数超过限制"),

    /**
     * API 调用太频繁，请稍候再试
     */
    API_MINUTE_QUOTA_REACH_LIMIT(45011, "API 调用太频繁，请稍候再试"),

    /**
     * 模板大小超过限制
     */
    TEMPLATE_SIZE_OUT_OF_LIMIT(45012, "模板大小超过限制"),

    /**
     * 回复时间超过限制
     */
    RESPONSE_OUT_OF_TIME_LIMIT(45015, "回复时间超过限制"),

    /**
     * 系统分组，不允许修改
     */
    CANT_MODIFY_SYS_GROUP(45016, "系统分组，不允许修改"),

    /**
     * 分组名字过长
     */
    CANT_SET_GROUP_NAME_TOO_LONG(45017, "分组名字过长"),

    /**
     * 分组数量超过上限
     */
    TOO_MANY_GROUP(45018, "分组数量超过上限"),

    /**
     * api 功能未授权
     */
    API_UNAUTHORIZED(48001, "api 功能未授权，请确认公众号已获得该接口，可以在公众平台官网 - 开发者中心页中查看接口权限"),

    /**
     * api 接口被封禁
     */
    API_FORBIDDEN_FOR_IRREGULARITIES(48004, "api 接口被封禁，请登录 mp.weixin.qq.com 查看详情"),

    /**
     * 用户受限，可能是用户账号被冻结或注销
     */
    USER_LIMITED(50002, "用户受限，可能是用户账号被冻结或注销"),

// ==================== 小程序相关错误码 ====================

    /**
     * POST参数非法
     */
    POST_PARAMS_ILLEGAL(1003, "POST参数非法"),

    /**
     * 商品id不存在
     */
    PRODUCT_ID_NOT_EXIST(20002, "商品id不存在"),

    /**
     * 无效的form_id
     */
    INVALID_FORM_ID(41028, "无效的form_id"),

    /**
     * form_id使用次数达到限制
     */
    FORM_ID_USED_COUNT_REACH_LIMIT(41029, "form_id使用次数达到限制"),

    /**
     * page路径不正确，需要保证在现网版本小程序中存在，与app.json保持一致
     */
    INVALID_PAGE(41030, "page路径不正确，需要保证在现网版本小程序中存在，与app.json保持一致"),

    /**
     * form_id已被屏蔽
     */
    FORM_ID_BLOCKED(41031, "form_id已被屏蔽"),

    /**
     * 不允许使用已提交的form_id发送消息（处罚）
     */
    NOT_ALLOW_SEND_WITH_SUBMITTED_FORM_ID(41032, "不允许使用已提交的form_id发送消息（处罚）"),

    /**
     * 不允许使用已提交的form_id发送消息（处罚）
     */
    NOT_ALLOW_SEND_WITH_SUBMITTED_FORM_ID_2(41034, "不允许使用已提交的form_id发送消息（处罚）"),

    /**
     * 不允许使用预支付id发送消息（处罚）
     */
    NOT_ALLOW_SEND_WITH_PREPAY_ID(41035, "不允许使用预支付id发送消息（处罚）"),

    /**
     * 模板消息大小超过限制
     */
    TEMPLATE_MESSAGE_SIZE_OUT_OF_LIMIT(45014, "模板消息大小超过限制"),

// ==================== 第三方平台相关错误码 ====================

    /**
     * 组件访问token超时
     */
    COMPONENT_ACCESS_TOKEN_EXPIRED(42006, "component_access_token超时"),

    /**
     * 缺少component_access_token
     */
    MISSING_COMPONENT_ACCESS_TOKEN(41021, "缺少component_access_token"),

    /**
     * API未授权给第三方平台
     */
    API_IS_UNAUTHORIZED_TO_COMPONENT(61007, "API未授权给第三方平台"),

    /**
     * 必须使用component_token调用第三方平台API
     */
    MUST_USE_COMPONENT_TOKEN(61014, "必须使用component_token调用第三方平台API"),

    /**
     * API功能类别需要组件确认
     */
    FUNCTION_CATEGORY_NEED_CONFIRM(61016, "API功能类别需要组件确认"),

    /**
     * IP地址未注册（不在白名单中）
     */
    ACCESS_CLIENTIP_NOT_REGISTERED(61004, "IP地址未注册（不在白名单中）"),

// ==================== 其他错误码 ====================

    /**
     * 多媒体文件大小超过限制
     */
    MEDIA_SIZE_OUT_OF_LIMIT(45001, "多媒体文件大小超过限制"),

    /**
     * 服务器给同一用户发送相同内容触发秒级频控
     */
    SECOND_LEVEL_RATE_LIMITING(40258, "服务器给同一用户发送相同内容触发秒级频控"),

    /**
     * 标题为空
     */
    EMPTY_MEDIA_TITLE(40227, "标题为空"),

    /**
     * 运单ID不存在
     */
    WAYBILL_ID_NOT_FOUND(40199, "运单ID不存在"),

    /**
     * 用户拒绝接受消息
     */
    USER_REFUSE_ACCEPT_MSG(43101, "用户拒绝接受消息，如果用户之前曾经订阅过，则表示用户取消了订阅关系"),

    /**
     * 小程序未认证
     */
    REQUIRE_VERIFY(43016, "小程序未认证"),

    /**
     * IP地址未注册（不在白名单中）
     */
    ACCESS_CLIENTIP_NOT_REGISTERED_NOT_IN_WHITELIST(45035, "IP地址未注册（不在白名单中）"),

    /**
     * 缺少component_appid
     */
    MISSING_COMPONENT_APPID(41018, "缺少component_appid"),

    /**
     * 插件token超时
     */
    PLUGIN_TOKEN_EXPIRED(42004, "插件token超时"),

    /**
     * API使用超时
     */
    API_USAGE_EXPIRED(42005, "API使用超时"),

    /**
     * VoIP通话key超时
     */
    VOIP_CALL_KEY_EXPIRED(42008, "VoIP通话key超时"),

    /**
     * 客户端临时token超时
     */
    CLIENT_TMP_TOKEN_EXPIRED(42009, "客户端临时token超时"),

    /**
     * 需要业务用户授权
     */
    REQUIRE_BIZUSER_AUTHORIZE(43007, "需要业务用户授权"),

    /**
     * 需要业务支付授权
     */
    REQUIRE_BIZ_PAY_AUTH(43008, "需要业务支付授权"),

    /**
     * 不能使用自定义码，需要授权
     */
    CANNOT_USE_CUSTOM_CODE(43009, "不能使用自定义码，需要授权"),

    /**
     * 不能使用余额，需要授权
     */
    CANNOT_USE_BALANCE(43010, "不能使用余额，需要授权"),

    /**
     * 不能使用红包，需要授权
     */
    CANNOT_USE_BONUS(43011, "不能使用红包，需要授权"),

    /**
     * 不能使用自定义URL，需要授权
     */
    CANNOT_USE_CUSTOM_URL(43012, "不能使用自定义URL，需要授权"),

    /**
     * 不能使用摇一摇卡券，需要授权
     */
    CANNOT_USE_SHAKE_CARD(43013, "不能使用摇一摇卡券，需要授权"),

    /**
     * 需要检查代理
     */
    REQUIRE_CHECK_AGENT(43014, "需要检查代理"),

    /**
     * 需要微信团队授权才能使用此功能
     */
    REQUIRE_AUTHORIZE_BY_WECHAT_TEAM(43015, "需要微信团队授权才能使用此功能"),

    /**
     * 需要位置ID
     */
    REQUIRE_LOCATION_ID(43017, "需要位置ID"),

    /**
     * 码没有被标记
     */
    CODE_HAS_NO_BEEN_MARK(43018, "码没有被标记"),

    /**
     * 修改模板太频繁
     */
    CHANGE_TEMPLATE_TOO_FREQUENTLY(43100, "修改模板太频繁"),

    /**
     * 模板不是订阅类型
     */
    TEMPLATE_NOT_SUBSCRIPTIONTYPE(43102, "模板不是订阅类型"),

    /**
     * 此API只能取消订阅
     */
    API_ONLY_CANCEL_SUBSCRIPTION(43103, "此API只能取消订阅"),

    /**
     * 此appid没有权限
     */
    APPID_NO_PERMISSION(43104, "此appid没有权限"),

    /**
     * 新闻与template_id没有绑定关系
     */
    NEWS_NO_BINDING_RELATION(43105, "新闻与template_id没有绑定关系"),

    /**
     * 不允许添加模板（处罚）
     */
    NOT_ALLOW_ADD_TEMPLATE(43106, "不允许添加模板（处罚）");

    /**
     * 错误码
     */
    private final Integer code;

    /**
     * 错误描述
     */
    private final String description;
}
