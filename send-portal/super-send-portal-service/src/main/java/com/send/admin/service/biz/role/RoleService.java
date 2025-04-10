package com.send.admin.service.biz.role;

import com.send.admin.service.biz.config.I18nService;
import com.send.admin.service.bo.role.RoleResponseBo;
import com.send.dao.repository.TbSysRoleDao;
import com.send.model.db.mysql.TbSysRole;
import com.send.model.enums.UserRoleEnum;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author WangCheng
 * @version 1.0
 * @CreateTime: 2025-04-06 18:27
 * @Description:
 * @Company: Information Technology Company
 */
@Service
public class RoleService {

    @Autowired
    private TbSysRoleDao tbSysRoleDao;
    @Autowired
    private I18nService i18nService;

    /**
     * 列出router集合
     *
     * @return 菜单路由结果
     */
    public List<RoleResponseBo> list() {
        List<TbSysRole> tbSysRoles = tbSysRoleDao.selectList(null);
        List<RoleResponseBo> bos = new ArrayList<>();
        if (CollectionUtils.isEmpty(tbSysRoles)) {
            RoleResponseBo bo = new RoleResponseBo();
            bo.setId(UserRoleEnum.ADMIN.getType());
            bo.setRoleType(UserRoleEnum.ADMIN.getName());
            //bo.setRoleName(UserRoleEnum.ADMIN.getValue());
            bo.setRoleName(i18nService.getMessage(UserRoleEnum.ADMIN.getName()));
            bos.add(bo);
            RoleResponseBo boCommon = new RoleResponseBo();
            bo.setId(UserRoleEnum.COMMON.getType());
            bo.setRoleType(UserRoleEnum.COMMON.getName());
            //bo.setRoleName(UserRoleEnum.COMMON.getValue());
            bo.setRoleName(i18nService.getMessage(UserRoleEnum.COMMON.getName()));
            bos.add(boCommon);
            return bos;
        }
        tbSysRoles.forEach(tbSysRole -> {
            RoleResponseBo bo = new RoleResponseBo();
            //bo.setRoleName(tbSysRole.getRoleDesc());
            bo.setRoleName(i18nService.getMessage(tbSysRole.getRole()));
            bo.setRoleType(tbSysRole.getRole());
            bo.setId(tbSysRole.getId());
            bos.add(bo);
        });
        return bos;

    }

}
