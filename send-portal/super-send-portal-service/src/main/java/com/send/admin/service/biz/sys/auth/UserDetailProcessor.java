package com.send.admin.service.biz.sys.auth;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.send.dao.repository.*;
import com.send.model.auth.UserDetail;
import com.send.model.db.mysql.*;
import com.send.model.db.mysql.bo.config.impl.PasswordConfigDetail;
import com.send.model.enums.PermissionTypeEnum;
import com.send.admin.service.biz.config.ParamConfigCacheService;
import com.send.admin.service.bo.sys.LoginRequestBO;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author WangCheng
 * @version 1.0
 * @CreateTime: 2025-04-05 11:27
 * @Description:
 * @Company: Information Technology Company
 */
@Component
public class UserDetailProcessor {

    @Resource
    private TbSysUserDao tbSysUserDao;

    @Resource
    private TbSysRoleDao tbSysRoleDao;

    @Resource
    private TbSysUserRoleDao tbSysUserRoleDao;

    @Resource
    private TbSysRolePermissionDao tbSysRolePermissionDao;

    @Resource
    private TbSysPermissionDao tbSysPermissionDao;

    @Autowired
    private ParamConfigCacheService paramConfigCacheService;

    public UserDetail process(LoginRequestBO loginRequestBO) {

        /* 查询基本信息 */
        TbSysUser tbSysUser = tbSysUserDao.selectOne(Wrappers.lambdaQuery(TbSysUser.class)
                .eq(TbSysUser::getUsername, loginRequestBO.getUsername()));

        return buildUserDetail(tbSysUser);
    }

    public UserDetail buildUserDetail(TbSysUser tbSysUser) {

        /* 用户角色 & 角色 */
        List<TbSysUserRole> tbSysUserRoleList = tbSysUserRoleDao.selectList(Wrappers.lambdaQuery(TbSysUserRole.class)
                .eq(TbSysUserRole::getUserId, tbSysUser.getId()));
        List<TbSysRole> tbSysRoleList = Collections.emptyList();
        List<TbSysPermission> tbSysPermissionList = Collections.emptyList();
        if (CollectionUtils.isNotEmpty(tbSysUserRoleList)) {

            List<Integer> roleIdList = tbSysUserRoleList.stream().map(TbSysUserRole::getRoleId).collect(Collectors.toList());
            tbSysRoleList = tbSysRoleDao.selectBatchIds(roleIdList);

            /* 角色 x 权限 */
            List<TbSysRolePermission> tbSysRolePermissionList = tbSysRolePermissionDao.selectList(Wrappers.lambdaQuery(TbSysRolePermission.class)
                    .in(TbSysRolePermission::getRoleId, roleIdList)
            );

            if (CollectionUtils.isNotEmpty(tbSysRolePermissionList)) {
                List<Integer> permissionIdList = tbSysRolePermissionList.stream().map(TbSysRolePermission::getPermissionId).collect(Collectors.toList());
                tbSysPermissionList = tbSysPermissionDao.selectList(Wrappers.lambdaQuery(TbSysPermission.class)
                        .in(TbSysPermission::getId, permissionIdList));
            }

        }
        List<String> permissionURIList = tbSysPermissionList.stream()
                .filter(x -> PermissionTypeEnum.URI.getValue() == x.getType())
                .map(TbSysPermission::getUri).collect(Collectors.toList());
        /* 组装 */
        UserDetail userDetail = new UserDetail();
        userDetail.setId(tbSysUser.getId());
        userDetail.setUsername(tbSysUser.getUsername());
        userDetail.setNickName(tbSysUser.getNickname());
        userDetail.setRoleList(tbSysRoleList.stream().map(TbSysRole::getRole).collect(Collectors.toList()));
        userDetail.setPermissionList(tbSysPermissionList);
        userDetail.setPermissionURIList(permissionURIList);
        userDetail.setPasswordUpdateTime(tbSysUser.getPasswordUpdateTime());
        userDetail.setExpireType(tbSysUser.getExpireType());
        userDetail.setExpireDate(tbSysUser.getExpireDate());
        userDetail.setBuiltinFlag(tbSysUser.getBuiltinFlag());
        userDetail.setLoggedFlag(tbSysUser.getLoggedFlag());
        userDetail.setEnabled(tbSysUser.getEnabled());
        userDetail.setPasswordExpired(validatePasswordExpired(tbSysUser));

        return userDetail;
    }

    private boolean validatePasswordExpired(TbSysUser tbSysUser) {
        PasswordConfigDetail detail = paramConfigCacheService.getPasswordConfigDetail();
        boolean expireEnable;
        int expireDuration;
        if (detail == null) {
            expireEnable = false;
            expireDuration = 90;
        } else {
            expireEnable = ObjectUtils.defaultIfNull(detail.getExpireEnable(), false);
            expireDuration = ObjectUtils.defaultIfNull(detail.getExpireDuration(), 90);
        }
        if (!expireEnable) {
            return false;
        }
        return tbSysUser.getPasswordUpdateTime().isBefore(LocalDateTime.now().minusDays(expireDuration));
    }
}
