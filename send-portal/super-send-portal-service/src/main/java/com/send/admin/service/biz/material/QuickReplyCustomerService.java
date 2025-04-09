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
import com.send.admin.service.bo.reply.CreateQuickReplyCustomerRequestBo;
import com.send.admin.service.bo.reply.DeleteQuickReplyCustomerRequestBo;
import com.send.admin.service.bo.reply.PageQueryQuickReplyCustomerRequestBo;
import com.send.admin.service.bo.reply.UpdateQuickReplyCustomerRequestBo;
import com.send.admin.service.tool.PageValidateTool;
import com.send.dao.repository.MaterialLibraryDao;
import com.send.dao.repository.QuickReplyCustomerDao;
import com.send.model.auth.UserDetail;
import com.send.model.db.mysql.MaterialLibrary;
import com.send.model.db.mysql.QuickReplyCustomer;
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
public class QuickReplyCustomerService {

    @Autowired
    private QuickReplyCustomerDao quickReplyCustomerDao;

    /**
     * 分页查询
     *
     * @param requestBo 入参
     * @return 分页结果
     */
    public PageList<QuickReplyCustomer> pageList(PageQueryQuickReplyCustomerRequestBo requestBo) {
        PageValidateTool.pageValidate(requestBo.getPage());

        Page<QuickReplyCustomer> page = PageTool.buildPage(requestBo.getPage());
        LambdaQueryWrapper<QuickReplyCustomer> quickReplyCustomerLambdaQueryWrapper = Wrappers.lambdaQuery(QuickReplyCustomer.class);
        if (StringUtils.isNotBlank(requestBo.getContent())) {
            quickReplyCustomerLambdaQueryWrapper.like(QuickReplyCustomer::getContent, requestBo.getContent());
        }
        if (requestBo.getStartTime() != null) {
            quickReplyCustomerLambdaQueryWrapper.ge(QuickReplyCustomer::getUpdateTime, requestBo.getStartTime());
        }
        if (requestBo.getEndTime() != null) {
            quickReplyCustomerLambdaQueryWrapper.le(QuickReplyCustomer::getUpdateTime, requestBo.getEndTime());
        }
        UserDetail userDetail = ThreadContext.get(AuthService.KEY_USER_DETAIL);
        if (userDetail == null) {
            throw new BusinessException(MasterExceptionEnum.ERR_ACCESS_DENY);
        }
        quickReplyCustomerLambdaQueryWrapper.eq(QuickReplyCustomer::getUserId, userDetail.getId());
        quickReplyCustomerLambdaQueryWrapper.orderByDesc(QuickReplyCustomer::getUpdateTime);
        Page<QuickReplyCustomer> materialLibraryPage = quickReplyCustomerDao.selectPage(page, quickReplyCustomerLambdaQueryWrapper);
        return PageTool.buildPageList(materialLibraryPage.getRecords(), page);
    }

    /**
     * 创建客服快捷回复
     *
     * @param bo 入参
     * @return 回参
     */
    @Transactional(rollbackFor = Exception.class)
    public int create(CreateQuickReplyCustomerRequestBo bo) {
        UserDetail userDetail = ThreadContext.get(AuthService.KEY_USER_DETAIL);
        if (userDetail == null) {
            throw new BusinessException(MasterExceptionEnum.ERR_ACCESS_DENY);
        }
        //判断客服快捷回复内容是否重复
        LambdaQueryWrapper<QuickReplyCustomer> wrappers = Wrappers.lambdaQuery(QuickReplyCustomer.class).eq(QuickReplyCustomer::getContent, bo.getContent()).eq(QuickReplyCustomer::getUserId, userDetail.getId());
        List<QuickReplyCustomer> quickReplyCustomers = quickReplyCustomerDao.selectList(wrappers);
        if (CollectionUtils.isNotEmpty(quickReplyCustomers)) {
            throw new BusinessException(MasterExceptionEnum.QUICK_REPLY_NAME_SAME);
        }
        QuickReplyCustomer quickReplyCustomer = new QuickReplyCustomer();
        quickReplyCustomer.setContent(bo.getContent());
        quickReplyCustomer.setType(1);
        quickReplyCustomer.setUserId(userDetail.getId());
        quickReplyCustomer.setUserName(userDetail.getUsername());
        LocalDateTime now = LocalDateTime.now();
        quickReplyCustomer.setCreateTime(now);
        quickReplyCustomer.setUpdateTime(now);
        int count = quickReplyCustomerDao.insert(quickReplyCustomer);
        return count;
    }

    /**
     * 更新客服快捷回复
     *
     * @param bo 入参
     * @return 回参
     */
    @Transactional(rollbackFor = Exception.class)
    public int update(UpdateQuickReplyCustomerRequestBo bo) {
        //判断客服快捷回复是否存在
        QuickReplyCustomer quickReplyCustomer = quickReplyCustomerDao.selectById(bo.getId());
        if (quickReplyCustomer == null) {
            throw new BusinessException(MasterExceptionEnum.NOT_EXIST_ID);
        }
        UserDetail userDetail = ThreadContext.get(AuthService.KEY_USER_DETAIL);
        if (userDetail == null) {
            throw new BusinessException(MasterExceptionEnum.ERR_ACCESS_DENY);
        }
        //较验是否有权限更新
        validPermission(quickReplyCustomer.getUserId());
        //判断客服快捷回复是否重复
        LambdaQueryWrapper<QuickReplyCustomer> wrappers = Wrappers.lambdaQuery(QuickReplyCustomer.class).eq(QuickReplyCustomer::getContent, bo.getContent()).eq(QuickReplyCustomer::getUserId, userDetail.getId()).notIn(QuickReplyCustomer::getId, bo.getId());
        List<QuickReplyCustomer> quickReplyCustomers = quickReplyCustomerDao.selectList(wrappers);
        if (CollectionUtils.isNotEmpty(quickReplyCustomers)) {
            throw new BusinessException(MasterExceptionEnum.QUICK_REPLY_NAME_SAME);
        }
        quickReplyCustomer.setContent(bo.getContent());
        quickReplyCustomer.setUpdateTime(LocalDateTime.now());
        int count = quickReplyCustomerDao.updateById(quickReplyCustomer);
        return count;
    }

    /**
     * 删除客服快捷回复
     *
     * @param bo 入参
     * @return 回参
     */
    @Transactional(rollbackFor = Exception.class)
    public int delete(DeleteQuickReplyCustomerRequestBo bo) {
        //判断素材是否存在
        QuickReplyCustomer quickReplyCustomer = quickReplyCustomerDao.selectById(bo.getId());
        if (quickReplyCustomer == null) {
            throw new BusinessException(MasterExceptionEnum.NOT_EXIST_ID);
        }
        //较验是否有权限更新
        validPermission(quickReplyCustomer.getUserId());
        int count = quickReplyCustomerDao.deleteById(bo.getId());
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
