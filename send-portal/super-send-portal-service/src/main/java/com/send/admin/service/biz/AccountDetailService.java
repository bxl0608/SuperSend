package com.send.admin.service.biz;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.project.base.common.thread.ThreadContext;
import com.project.base.model.exception.BusinessException;
import com.project.base.model.pagination.PageList;
import com.project.base.mysql.pagination.PageTool;
import com.send.admin.service.biz.sys.AuthService;
import com.send.admin.service.bo.PageQueryAccountDetailRequestBo;
import com.send.admin.service.bo.user.RechargeUserRequestBo;
import com.send.dao.repository.AccountDetailDao;
import com.send.model.auth.UserDetail;
import com.send.model.db.mysql.AccountDetail;
import com.send.model.db.mysql.TbSysUser;
import com.send.model.enums.ChangeTypeEnum;
import com.send.model.enums.UserRoleEnum;
import com.send.model.exception.MasterExceptionEnum;
import com.send.model.i18n.I18nParamConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import com.send.admin.service.tool.PageValidateTool;

/**
 * Compang: Information Technology Company
 *
 * @author WangCheng
 * @version 1.0
 * @date 2025年04月07日 19:27
 * @description：
 */

@Slf4j
@Service
public class AccountDetailService {

    @Autowired
    private AccountDetailDao accountDetailDao;


    /**
     * 分页查询
     *
     * @param requestBo 入参
     * @return 分页结果
     */
    public PageList<AccountDetail> pageList(PageQueryAccountDetailRequestBo requestBo) {
        PageValidateTool.pageValidate(requestBo.getPage());

        Page<AccountDetail> page = PageTool.buildPage(requestBo.getPage());
        LambdaQueryWrapper<AccountDetail> accountDetailLambdaQueryWrapper = Wrappers.lambdaQuery(AccountDetail.class);
        if (requestBo.getChangeType() != null) {
            accountDetailLambdaQueryWrapper.eq(AccountDetail::getChangeType, requestBo.getChangeType());
        }
        if (StringUtils.isNotBlank(requestBo.getOrderNumber())) {
            accountDetailLambdaQueryWrapper.like(AccountDetail::getOrderNumber, requestBo.getOrderNumber());
        }
        if (requestBo.getStartTime() != null) {
            accountDetailLambdaQueryWrapper.ge(AccountDetail::getCreateTime, requestBo.getStartTime());
        }
        if (requestBo.getEndTime() != null) {
            accountDetailLambdaQueryWrapper.le(AccountDetail::getCreateTime, requestBo.getEndTime());
        }
        UserDetail userDetail = ThreadContext.get(AuthService.KEY_USER_DETAIL);
        if (userDetail == null) {
            throw new BusinessException(MasterExceptionEnum.ERR_ACCESS_DENY);
        }
        accountDetailLambdaQueryWrapper.eq(AccountDetail::getUserId, userDetail.getId());
        accountDetailLambdaQueryWrapper.orderByDesc(AccountDetail::getCreateTime);
        Page<AccountDetail> accountDetailPage = accountDetailDao.selectPage(page, accountDetailLambdaQueryWrapper);
        return PageTool.buildPageList(accountDetailPage.getRecords(), page);
    }

    /**
     * 充值金额
     *
     * @param bo
     * @param sysUser
     * @param mount
     */
    public void addAccountDetail(RechargeUserRequestBo bo, TbSysUser sysUser, BigDecimal mount) {
        AccountDetail accountDetail = new AccountDetail();
        if (hasPlusSign(bo.getAccountBalance())) {
            accountDetail.setChangeAmount(bo.getAccountBalance().toString());
        } else {
            accountDetail.setChangeAmount("+" + bo.getAccountBalance().toString());
        }
        accountDetail.setChangeType(bo.getChangeType());
        accountDetail.setRemarks(bo.getRemarks());
        accountDetail.setCreateTime(LocalDateTime.now());
        accountDetail.setUserId(bo.getId());
        accountDetail.setUsername(sysUser.getUsername());
        accountDetail.setChangeAfterAmount(mount);
        UserDetail userDetail = validateLoginUserIsAdmin();
        accountDetail.setOperatorId(userDetail.getId());
        accountDetail.setOperatorUsername(userDetail.getUsername());
        accountDetail.setOrderNumber(buildOrderNumberShow());
        accountDetail.setChangeBeforeAmount(sysUser.getAccountBalance());
        accountDetailDao.insert(accountDetail);
    }

    public boolean hasPlusSign(BigDecimal number) {
        return number.toString().startsWith("+");
    }

    private UserDetail validateLoginUserIsAdmin() {
        UserDetail userDetail = ThreadContext.get(AuthService.KEY_USER_DETAIL);
        if (userDetail == null) {
            throw new BusinessException(MasterExceptionEnum.ERR_ACCESS_DENY);
        }
        if (CollectionUtils.isEmpty(userDetail.getRoleList()) || !userDetail.getRoleList().contains(UserRoleEnum.ADMIN.getName())) {
            throw new BusinessException(MasterExceptionEnum.NO_PERMISSION_OPERATE, I18nParamConstant.PARAM_USER);
        }
        return userDetail;
    }


    /**
     * 订单号
     *
     * @return 回参
     */
    private String buildOrderNumberShow() {
        Random rand = new Random();
        int randomInt = rand.nextInt(900000) + 100000;
        //char c = (char) (Math.random() * 26 + 'a');
        StringBuilder builder = new StringBuilder().append(new Date().getTime());
        builder.append(randomInt);
        return builder.toString();
    }

}
