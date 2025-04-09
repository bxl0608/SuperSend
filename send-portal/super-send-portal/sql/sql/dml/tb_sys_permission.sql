INSERT INTO tb_sys_permission(id, parent_id, type, uri, seq, front_router, front_menu_name, front_icon, create_time)
VALUES
-- menu --

(10, NULL, 1, '', 2, 'homepage', '首页', '', '2021-12-17 00:00:00'),

(20, NULL, 1, '', 1, 'userManagement', '用户管理', '', '2021-12-17 00:00:00'),

(30, NULL, 1, '', 1, 'WhatsApp', 'WhatsApp', '', '2021-12-17 00:00:00'),
(31, 30, 1, '', 3, 'importAccounts', '导入账号', '', '2021-12-17 00:00:00'),
(32, 30, 1, '', 3, 'listOfAccounts', '账号列表', '', '2021-12-17 00:00:00'),
(33, 30, 1, '', 3, 'accountGrouping', '账号分组', '', '2021-12-17 00:00:00'),
(34, 30, 1, '', 3, 'customerServiceManagement', '客服管理', '', '2021-12-17 00:00:00'),
(35, 30, 1, '', 3, 'massTasks', '群发任务', '', '2021-12-17 00:00:00'),

(40, NULL, 1, '', 9, 'library', '素材库', '', '2021-12-17 00:00:00'),
(41, 40, 1, '', 3, 'textualMaterials', '文字素材', '', '2021-12-17 00:00:00'),
(42, 40, 1, '', 3, 'quickReplyFromCustomerService', '客服快捷回复', '', '2021-12-17 00:00:00'),

(50, NULL, 1, '', 9, 'dataManagement', '数据管理', '', '2021-12-17 00:00:00'),
(51, 50, 1, '', 3, 'dataWarehouse', '数据仓库', '', '2021-12-17 00:00:00'),

(60, NULL, 1, '', 9, 'financialManagement', '财务管理', '', '2021-12-17 00:00:00'),
(61, 60, 1, '', 3, 'accountDetails', '账户明细', '', '2021-12-17 00:00:00'),

(70, NULL, 1, '', 9, 'deviceManagement', '设备管理', '', '2021-12-17 00:00:00'),
(71, 70, 1, '', 3, 'aListOfDevices', '设备列表', '', '2021-12-17 00:00:00'),
(72, 70, 1, '', 3, 'deviceGrouping', '设备分组', '', '2021-12-17 00:00:00'),

-- api --
(100, NULL, 2, '/portal/permission/listRouter', 0, '', '', '', '2023-03-15 11:19:49'),
(101, NULL, 2, '/portal/auth/logout', 0, '', '', '', '2023-03-15 11:19:49'),
(102, NULL, 2, '/portal/role/list', 0, '', '', '', '2023-03-22 00:00:00'),

(110, NULL, 2, '/portal//account/detail/list', 0, '', '', '', '2023-03-22 00:00:00'),

(160, NULL, 2, '/portal/user/list', 0, '', '', '', '2023-03-22 00:00:00'),
(161, NULL, 2, '/portal/user/create', 0, '', '', '', '2023-03-22 00:00:00'),
(162, NULL, 2, '/portal/user/update', 0, '', '', '', '2023-03-22 00:00:00'),
(163, NULL, 2, '/portal/user/enable', 0, '', '', '', '2023-03-22 00:00:00'),
(164, NULL, 2, '/portal/user/updatePwd', 0, '', '', '', '2023-03-22 00:00:00'),
(165, NULL, 2, '/portal/user/resetPwd', 0, '', '', '', '2023-03-22 00:00:00'),
(166, NULL, 2, '/portal/user/delete', 0, '', '', '', '2023-03-22 00:00:00'),
(167, NULL, 2, '/portal/user/recharge', 0, '', '', '', '2023-03-22 00:00:00'),
(170, NULL, 2, '/portal/materialLibrary/*', 0, '', '', '', '2023-03-22 00:00:00'),
(171, NULL, 2, '/portal/customerServiceManagement/*', 0, '', '', '', '2023-03-22 00:00:00'),
(172, NULL, 2, '/portal/quickReplyCustomer/*', 0, '', '', '', '2023-03-22 00:00:00');