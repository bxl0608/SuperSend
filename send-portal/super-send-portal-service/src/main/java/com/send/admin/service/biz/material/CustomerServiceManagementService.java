package com.send.admin.service.biz.material;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.project.base.common.thread.ThreadContext;
import com.project.base.model.exception.BusinessException;
import com.project.base.model.pagination.PageList;
import com.project.base.mysql.pagination.PageTool;
import com.send.admin.service.biz.sys.AuthService;
import com.send.admin.service.bo.customer.CreateCustomerServiceManagementRequestBo;
import com.send.admin.service.bo.customer.DeleteCustomerServiceManagementRequestBo;
import com.send.admin.service.bo.customer.PageQueryCustomerServiceManagementRequestBo;
import com.send.admin.service.bo.customer.UpdateCustomerServiceManagementRequestBo;
import com.send.admin.service.bo.material.CreateMaterialLibraryRequestBo;
import com.send.admin.service.bo.material.DeleteMaterialLibraryRequestBo;
import com.send.admin.service.bo.material.PageQueryMaterialLibraryRequestBo;
import com.send.admin.service.bo.material.UpdateMaterialLibraryRequestBo;
import com.send.admin.service.tool.PageValidateTool;
import com.send.admin.service.tool.PhoneNumberValidator;
import com.send.dao.repository.CustomerServiceManagementDao;
import com.send.dao.repository.MaterialLibraryDao;
import com.send.model.auth.UserDetail;
import com.send.model.db.mysql.CustomerServiceManagement;
import com.send.model.db.mysql.MaterialLibrary;
import com.send.model.enums.CustomerServiceManagementEnum;
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
public class CustomerServiceManagementService {

    @Autowired
    private CustomerServiceManagementDao customerServiceManagementDao;

    /**
     * 分页查询
     *
     * @param requestBo 入参
     * @return 分页结果
     */
    public PageList<CustomerServiceManagement> pageList(PageQueryCustomerServiceManagementRequestBo requestBo) {
        PageValidateTool.pageValidate(requestBo.getPage());

        Page<CustomerServiceManagement> page = PageTool.buildPage(requestBo.getPage());
        LambdaQueryWrapper<CustomerServiceManagement> customerServiceManagementLambdaQueryWrapper = Wrappers.lambdaQuery(CustomerServiceManagement.class);
        if (StringUtils.isNotBlank(requestBo.getCustomerName())) {
            customerServiceManagementLambdaQueryWrapper.like(CustomerServiceManagement::getCustomerName, requestBo.getCustomerName());
        }
        if (requestBo.getStartTime() != null) {
            customerServiceManagementLambdaQueryWrapper.ge(CustomerServiceManagement::getUpdateTime, requestBo.getStartTime());
        }
        if (requestBo.getEndTime() != null) {
            customerServiceManagementLambdaQueryWrapper.le(CustomerServiceManagement::getUpdateTime, requestBo.getEndTime());
        }
        UserDetail userDetail = ThreadContext.get(AuthService.KEY_USER_DETAIL);
        if (userDetail == null) {
            throw new BusinessException(MasterExceptionEnum.ERR_ACCESS_DENY);
        }
        customerServiceManagementLambdaQueryWrapper.eq(CustomerServiceManagement::getUserId, userDetail.getId());
        customerServiceManagementLambdaQueryWrapper.orderByDesc(CustomerServiceManagement::getUpdateTime);
        Page<CustomerServiceManagement> materialLibraryPage = customerServiceManagementDao.selectPage(page, customerServiceManagementLambdaQueryWrapper);
        return PageTool.buildPageList(materialLibraryPage.getRecords(), page);
    }

    /**
     * 创建客服管理
     *
     * @param bo 入参
     * @return 回参
     */
    @Transactional(rollbackFor = Exception.class)
    public int create(CreateCustomerServiceManagementRequestBo bo) {
        UserDetail userDetail = ThreadContext.get(AuthService.KEY_USER_DETAIL);
        if (userDetail == null) {
            throw new BusinessException(MasterExceptionEnum.ERR_ACCESS_DENY);
        }
        //判断客服名是否为11位手机号
        boolean validPhoneNumber = PhoneNumberValidator.isValidPhoneNumber(bo.getCustomerName());
        if (!validPhoneNumber) {
            throw new BusinessException(MasterExceptionEnum.CUSTOMER_NAME_NOT_MATCH_PHONE_NUMBER);
        }
        //判断客服名称是否重名
        LambdaQueryWrapper<CustomerServiceManagement> wrappers = Wrappers.lambdaQuery(CustomerServiceManagement.class).eq(CustomerServiceManagement::getCustomerName, bo.getCustomerName()).eq(CustomerServiceManagement::getUserId, userDetail.getId());
        List<CustomerServiceManagement> materialLibraries = customerServiceManagementDao.selectList(wrappers);
        if (CollectionUtils.isNotEmpty(materialLibraries)) {
            throw new BusinessException(MasterExceptionEnum.CUSTOMER_NAME_SAME);
        }
        CustomerServiceManagement customerServiceManagement = new CustomerServiceManagement();
        customerServiceManagement.setCustomerName(bo.getCustomerName());
        customerServiceManagement.setRemarks(bo.getRemarks());
        customerServiceManagement.setUserId(userDetail.getId());
        customerServiceManagement.setUserName(userDetail.getUsername());
        customerServiceManagement.setStatus(CustomerServiceManagementEnum.OFFLINE.getStatus());
        LocalDateTime now = LocalDateTime.now();
        customerServiceManagement.setCreateTime(now);
        customerServiceManagement.setUpdateTime(now);
        int count = customerServiceManagementDao.insert(customerServiceManagement);
        return count;
    }

    /**
     * 更新客服
     *
     * @param bo 入参
     * @return 回参
     */
    @Transactional(rollbackFor = Exception.class)
    public int update(UpdateCustomerServiceManagementRequestBo bo) {
        //判断素材是否存在
        CustomerServiceManagement customerServiceManagement = customerServiceManagementDao.selectById(bo.getId());
        if (customerServiceManagement == null) {
            throw new BusinessException(MasterExceptionEnum.NOT_EXIST_ID);
        }
        //较验是否有权限更新
        validPermission(customerServiceManagement.getUserId());
        UserDetail userDetail = ThreadContext.get(AuthService.KEY_USER_DETAIL);
        if (userDetail == null) {
            throw new BusinessException(MasterExceptionEnum.ERR_ACCESS_DENY);
        }
        customerServiceManagement.setRemarks(bo.getRemarks());
        customerServiceManagement.setUpdateTime(LocalDateTime.now());
        int count = customerServiceManagementDao.updateById(customerServiceManagement);
        return count;
    }

    /**
     * 删除客服
     *
     * @param bo 入参
     * @return 回参
     */
    @Transactional(rollbackFor = Exception.class)
    public int delete(DeleteCustomerServiceManagementRequestBo bo) {
        //判断素材是否存在
        CustomerServiceManagement customerServiceManagement = customerServiceManagementDao.selectById(bo.getId());
        if (customerServiceManagement == null) {
            throw new BusinessException(MasterExceptionEnum.NOT_EXIST_ID);
        }
        //todo:哪种情况下的客服可以被删除
        if(!CustomerServiceManagementEnum.OFFLINE.getStatus().equals(customerServiceManagement.getStatus())){
            throw new BusinessException(MasterExceptionEnum.CUSTOMER_SERVICE_ONLINE);
        }
        //较验是否有权限删除
        validPermission(customerServiceManagement.getUserId());
        int count = customerServiceManagementDao.deleteById(bo.getId());
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
