-- ----------------------------
-- Table structure for sys_permission
-- ----------------------------
DROP TABLE IF EXISTS `tb_sys_permission`;
CREATE TABLE tb_sys_permission
(
    id              int(11)      NOT NULL AUTO_INCREMENT,
    parent_id       int(11)               DEFAULT NULL COMMENT '父菜单ID',
    type            int(11)      NOT NULL DEFAULT 1 COMMENT '0 菜单, 1 接口',
    uri             varchar(100) NOT NULL DEFAULT '' COMMENT 'URI权限字符串',
    seq             int(11)      NOT NULL DEFAULT 0 COMMENT '菜单排序字段',
    front_router    varchar(50)  NOT NULL DEFAULT '' COMMENT '前端路由',
    front_menu_name varchar(100) NOT NULL DEFAULT '' COMMENT '菜单默认显示名称',
    front_icon      varchar(100) NOT NULL DEFAULT '' COMMENT '菜单图标',
    create_time     datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id)
)
    ENGINE = INNODB,
    CHARACTER SET utf8mb4,
    COLLATE utf8mb4_general_ci,
    COMMENT = '系统权限';

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `tb_sys_role`;
CREATE TABLE `tb_sys_role`
(
    id          int(11)      NOT NULL AUTO_INCREMENT,
    role        varchar(50)  NOT NULL DEFAULT '' COMMENT '角色名',
    role_desc   varchar(100) NOT NULL DEFAULT '' COMMENT '角色描述',
    create_time datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
)
    ENGINE = INNODB,
    CHARACTER SET utf8mb4,
    COLLATE utf8mb4_general_ci,
    COMMENT = '角色';


-- ----------------------------
-- Table structure for sys_role_permission
-- ----------------------------
DROP TABLE IF EXISTS `tb_sys_role_permission`;
CREATE TABLE `tb_sys_role_permission`
(
    id            int(11) NOT NULL AUTO_INCREMENT,
    role_id       int(11) NOT NULL COMMENT '角色ID',
    permission_id int(11) NOT NULL COMMENT '权限ID',
    PRIMARY KEY (id)
)
ENGINE = INNODB,
    CHARACTER SET utf8mb4,
    COLLATE utf8mb4_general_ci;

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `tb_sys_user`;
CREATE TABLE tb_sys_user
(
    id                   int(11)                      NOT NULL AUTO_INCREMENT,
    username             varchar(20)                  NOT NULL DEFAULT '' COMMENT '用户名',
    password             varchar(255)                 NOT NULL DEFAULT '' COMMENT '密码',
    password_salt        varchar(255)                 NOT NULL DEFAULT '' COMMENT '密码盐',
    nickname             varchar(20)                  NOT NULL DEFAULT '' COMMENT '昵称或者全名',
    enabled              tinyint(4)                   NOT NULL DEFAULT 1 COMMENT '1-正常，0-封禁',
    expire_type          tinyint(1) UNSIGNED          NOT NULL COMMENT '0-永久用户,1-临时用户',
    expire_date          datetime                              DEFAULT NULL COMMENT '有效期',
    builtin_flag         tinyint(1) UNSIGNED          NOT NULL COMMENT '0-非内置用户，1-内置用户',
    logged_flag          tinyint(1) UNSIGNED ZEROFILL NOT NULL COMMENT '0-已登录，1-未登录过',
    password_update_time datetime                              DEFAULT NULL COMMENT '密码更新时间',
    create_time          datetime                     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    account_balance      NUMERIC(10, 4)               NOT NULL DEFAULT 0.0000 COMMENT '账户余额',
    update_time          datetime                              DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    delete_flag          tinyint(1)                   NOT NULL COMMENT '删除符 已删除 1，未删除 0 ',
    PRIMARY KEY (id)
)
    ENGINE = INNODB,
    CHARACTER SET utf8mb4,
    COLLATE utf8mb4_general_ci;

DROP TABLE IF EXISTS `account_detail`;
CREATE TABLE account_detail
(
    id                   int(11)                      NOT NULL AUTO_INCREMENT,
    user_id              int(11)                      NOT NULL  COMMENT '用户id',
    username             varchar(50)                  DEFAULT ''  COMMENT '充值账户用户名',
    operator_id          int(11)                      NOT NULL  COMMENT '操作人id',
    operator_username    varchar(50)                  DEFAULT ''  COMMENT '操作人用户名',
    change_type          tinyint(4)                   NOT NULL DEFAULT 1 COMMENT '1:系统充值,2:用户消费',
    order_number         varchar(50)                  NOT NULL DEFAULT '' COMMENT '订单号',
    change_amount        varchar(50)                  NOT NULL DEFAULT '' COMMENT '变动金额',
    change_before_amount NUMERIC(10, 4)               NOT NULL  COMMENT '变动前余额',
    change_after_amount  NUMERIC(10, 4)               NOT NULL COMMENT '变动后余额',
    remarks              varchar(500)                 DEFAULT '' COMMENT '备注',
    create_time          datetime                     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id)
)
    ENGINE = INNODB,
    CHARACTER SET utf8mb4,
    COLLATE utf8mb4_general_ci;

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `tb_sys_user_role`;
CREATE TABLE tb_sys_user_role
(
    id      int(11) NOT NULL AUTO_INCREMENT,
    user_id int(11) NOT NULL COMMENT '用户ID',
    role_id int(11) NOT NULL COMMENT '角色ID',
    PRIMARY KEY (id)
)
    ENGINE = INNODB,
    CHARACTER SET utf8mb4,
    COLLATE utf8mb4_general_ci;


--
-- Create table `tb_config`
--
DROP TABLE IF EXISTS `tb_config`;
CREATE TABLE tb_config
(
    id          int(11)     NOT NULL AUTO_INCREMENT COMMENT 'id',
    type        varchar(30) NOT NULL COMMENT '系统参数类型',
    detail      text        NOT NULL COMMENT '详情',
    create_time datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time datetime             DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (id)
)
    ENGINE = INNODB,
    CHARACTER SET utf8mb4,
    COLLATE utf8mb4_general_ci,
    COMMENT = '系统参数配置';