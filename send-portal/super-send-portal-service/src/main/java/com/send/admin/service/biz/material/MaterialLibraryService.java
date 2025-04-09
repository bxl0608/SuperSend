package com.send.admin.service.biz.material;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.project.base.common.thread.ThreadContext;
import com.project.base.model.exception.BusinessException;
import com.project.base.model.pagination.PageList;
import com.project.base.mysql.pagination.PageTool;
import com.send.admin.service.biz.sys.AuthService;
import com.send.admin.service.bo.material.CreateMaterialLibraryRequestBo;
import com.send.admin.service.bo.material.DeleteMaterialLibraryRequestBo;
import com.send.admin.service.bo.material.PageQueryMaterialLibraryRequestBo;
import com.send.admin.service.bo.material.UpdateMaterialLibraryRequestBo;
import com.send.admin.service.tool.PageValidateTool;
import com.send.dao.repository.MaterialLibraryDao;
import com.send.model.auth.UserDetail;
import com.send.model.db.mysql.MaterialLibrary;
import com.send.model.exception.MasterExceptionEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Compang: Information Technology Company
 *
 * @author WangCheng
 * @version 1.0
 * @date 2025年04月09日 20:32
 * @description：
 */
@Slf4j
@Service
public class MaterialLibraryService {

    @Autowired
    private MaterialLibraryDao materialLibraryDao;

    /**
     * 分页查询
     *
     * @param requestBo 入参
     * @return 分页结果
     */
    public PageList<MaterialLibrary> pageList(PageQueryMaterialLibraryRequestBo requestBo) {
        PageValidateTool.pageValidate(requestBo.getPage());

        Page<MaterialLibrary> page = PageTool.buildPage(requestBo.getPage());
        LambdaQueryWrapper<MaterialLibrary> materialLibraryLambdaQueryWrapper = Wrappers.lambdaQuery(MaterialLibrary.class);
        if (StringUtils.isNotBlank(requestBo.getName())) {
            materialLibraryLambdaQueryWrapper.like(MaterialLibrary::getName, requestBo.getName());
        }
        if (requestBo.getStartTime() != null) {
            materialLibraryLambdaQueryWrapper.ge(MaterialLibrary::getUpdateTime, requestBo.getStartTime());
        }
        if (requestBo.getEndTime() != null) {
            materialLibraryLambdaQueryWrapper.le(MaterialLibrary::getUpdateTime, requestBo.getEndTime());
        }
        UserDetail userDetail = ThreadContext.get(AuthService.KEY_USER_DETAIL);
        if (userDetail == null) {
            throw new BusinessException(MasterExceptionEnum.ERR_ACCESS_DENY);
        }
        materialLibraryLambdaQueryWrapper.eq(MaterialLibrary::getUserId, userDetail.getId());
        materialLibraryLambdaQueryWrapper.orderByDesc(MaterialLibrary::getUpdateTime);
        Page<MaterialLibrary> materialLibraryPage = materialLibraryDao.selectPage(page, materialLibraryLambdaQueryWrapper);
        return PageTool.buildPageList(materialLibraryPage.getRecords(), page);
    }

    /**
     * 创建文字素材
     *
     * @param bo 入参
     * @return 回参
     */
    @Transactional(rollbackFor = Exception.class)
    public int create(CreateMaterialLibraryRequestBo bo) {
        UserDetail userDetail = ThreadContext.get(AuthService.KEY_USER_DETAIL);
        if (userDetail == null) {
            throw new BusinessException(MasterExceptionEnum.ERR_ACCESS_DENY);
        }
        //判断素材名称是否重名
        LambdaQueryWrapper<MaterialLibrary> wrappers = Wrappers.lambdaQuery(MaterialLibrary.class).eq(MaterialLibrary::getName, bo.getName()).eq(MaterialLibrary::getUserId, userDetail.getId());
        List<MaterialLibrary> materialLibraries = materialLibraryDao.selectList(wrappers);
        if (CollectionUtils.isNotEmpty(materialLibraries)) {
            throw new BusinessException(MasterExceptionEnum.MATERIAL_NAME_SAME);
        }
        MaterialLibrary materialLibrary = new MaterialLibrary();
        materialLibrary.setName(bo.getName());
        materialLibrary.setContent(bo.getContent());
        materialLibrary.setType(1);
        materialLibrary.setRemarks(bo.getRemarks());
        materialLibrary.setUserId(userDetail.getId());
        materialLibrary.setUserName(userDetail.getUsername());
        LocalDateTime now = LocalDateTime.now();
        materialLibrary.setCreateTime(now);
        materialLibrary.setUpdateTime(now);
        int count = materialLibraryDao.insert(materialLibrary);
        return count;
    }

    /**
     * 更新文字素材
     *
     * @param bo 入参
     * @return 回参
     */
    @Transactional(rollbackFor = Exception.class)
    public int update(UpdateMaterialLibraryRequestBo bo) {
        //判断素材是否存在
        MaterialLibrary materialLibrary = materialLibraryDao.selectById(bo.getId());
        if (materialLibrary == null) {
            throw new BusinessException(MasterExceptionEnum.NOT_EXIST_ID);
        }
        UserDetail userDetail = ThreadContext.get(AuthService.KEY_USER_DETAIL);
        if (userDetail == null) {
            throw new BusinessException(MasterExceptionEnum.ERR_ACCESS_DENY);
        }
        //较验是否有权限更新
        validPermission(materialLibrary.getUserId());
        //判断素材名称是否重名
        LambdaQueryWrapper<MaterialLibrary> wrappers = Wrappers.lambdaQuery(MaterialLibrary.class).eq(MaterialLibrary::getName, bo.getName()).eq(MaterialLibrary::getUserId, userDetail.getId()).notIn(MaterialLibrary::getId,bo.getId());
        List<MaterialLibrary> materialLibraries = materialLibraryDao.selectList(wrappers);
        if (CollectionUtils.isNotEmpty(materialLibraries)) {
            throw new BusinessException(MasterExceptionEnum.MATERIAL_NAME_SAME);
        }
        materialLibrary.setName(bo.getName());
        materialLibrary.setContent(bo.getContent());
        materialLibrary.setRemarks(bo.getRemarks());
        materialLibrary.setUpdateTime(LocalDateTime.now());
        int count = materialLibraryDao.updateById(materialLibrary);
        return count;
    }

    /**
     * 删除文字素材
     *
     * @param bo 入参
     * @return 回参
     */
    @Transactional(rollbackFor = Exception.class)
    public int delete(DeleteMaterialLibraryRequestBo bo) {
        //判断素材是否存在
        MaterialLibrary materialLibrary = materialLibraryDao.selectById(bo.getId());
        if (materialLibrary == null) {
            throw new BusinessException(MasterExceptionEnum.NOT_EXIST_ID);
        }
        //较验是否有权限删除
        validPermission(materialLibrary.getUserId());
        int count = materialLibraryDao.deleteById(bo.getId());
        return count;
    }

    /**
     * 判断当前用户是否有修改权限
     *
     * @param userId
     */
    public void validPermission(Integer userId) {
        UserDetail userDetail = ThreadContext.get(AuthService.KEY_USER_DETAIL);
        if (userDetail == null) {
            throw new BusinessException(MasterExceptionEnum.ERR_ACCESS_DENY);
        }
        if (userDetail.getId() != null && userDetail.getId().equals(userId)) {
            return;
        }
        throw new BusinessException(MasterExceptionEnum.ERR_EDIT_FORBIDDEN);
    }
}
