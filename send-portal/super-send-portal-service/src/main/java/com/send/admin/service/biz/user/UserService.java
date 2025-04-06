package com.send.admin.service.biz.user;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.send.common.security.PasswordTool;
import com.send.common.tool.TypeConvertTool;
import com.send.dao.repository.TbSysRoleDao;
import com.send.dao.repository.TbSysUserDao;
import com.send.dao.repository.TbSysUserRoleDao;
import com.send.model.auth.UserDetail;
import com.send.model.db.mysql.TbSysRole;
import com.send.model.db.mysql.TbSysUser;
import com.send.model.db.mysql.TbSysUserRole;
import com.send.model.db.mysql.bo.PageQueryUserBo;
import com.send.model.db.mysql.vo.PageQueryUserVo;
import com.send.model.enums.UserExpireType;
import com.send.model.enums.UserRoleEnum;
import com.send.model.exception.MasterExceptionEnum;
import com.send.model.i18n.I18nParamConstant;
import com.send.admin.service.biz.sys.AuthService;
import com.send.admin.service.biz.sys.LoginRetryCountService;
import com.send.admin.service.biz.sys.TokenService;
import com.send.admin.service.biz.sys.auth.UserDetailProcessor;
import com.send.admin.service.bo.user.*;
import com.send.admin.service.tool.PageValidateTool;
import com.send.admin.service.tool.RsaLocalTool;
import com.project.base.common.thread.ThreadContext;
import com.project.base.model.exception.BusinessException;
import com.project.base.model.pagination.PageList;
import com.project.base.model.pagination.PageRequest;
import com.project.base.mysql.pagination.PageTool;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author WangCheng
 * @version 1.0
 * @CreateTime: 2025-04-05 11:27
 * @Description:
 * @Company: Information Technology Company
 */
@Slf4j
@Service
public class UserService {

    public static final LocalDateTime MAX_DATE_TIME = LocalDateTime.of(9999, 12, 31, 23, 59, 59);
    public static final String LAST_SQL_LIMIT_1 = " limit 1";
    public static final String USERNAME_REGEX = "^[A-Za-z0-9][@_A-Za-z0-9]{0,19}$";
    public static final String CN_NAME_REGEX = "^[A-Za-z0-9\\u4e00-\\u9fa5][\\.\\:\\/A-Za-z0-9\\u4e00-\\u9fa5@_-]{0,19}$";
    @Autowired
    private TbSysUserDao tbSysUserDao;

    @Autowired
    private TbSysRoleDao tbSysRoleDao;

    @Autowired
    private TbSysUserRoleDao tbSysUserRoleDao;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UserDetailProcessor userDetailProcessor;

    @Autowired
    private LoginRetryCountService loginRetryCountService;

    @Transactional(rollbackFor = Exception.class)
    public Long recharge(RechargeUserRequestBo bo) {
        validateLoginUserIsAdmin();
        if (bo.getId() == null) {
            throw new BusinessException(MasterExceptionEnum.NOT_NULL, I18nParamConstant.PARAM_ID);
        }
        TbSysUser tbSysUser = tbSysUserDao.selectById(bo.getId());
        if (tbSysUser == null) {
            throw new BusinessException(MasterExceptionEnum.NOT_EXIST, I18nParamConstant.PARAM_ID);
        }
        long mount = tbSysUser.getAccountBalance() + bo.getAccountBalance();
        tbSysUser.setAccountBalance(mount);
        tbSysUserDao.updateById(tbSysUser);
        return mount;
    }

    /**
     * 创建用户
     *
     * @param bo 入参
     * @return 回参
     */
    @Transactional(rollbackFor = Exception.class)
    public int create(CreateUserRequestBo bo) {
        bo.setExpireType(0);
        bo.setExpireDate(LocalDateTime.now().plusYears(100L));
        validateLoginUserIsAdmin();
        if (bo.getEnabled() == null) {
            throw new BusinessException(MasterExceptionEnum.NOT_NULL, I18nParamConstant.PARAM_STATUS);
        }
        // 校验
        if (StringUtils.isBlank(bo.getUsername())) {
            throw new BusinessException(MasterExceptionEnum.NOT_BLANK, I18nParamConstant.PARAM_USERNAME);
        }
        if (StringUtils.isBlank(bo.getPassword())) {
            throw new BusinessException(MasterExceptionEnum.NOT_BLANK, I18nParamConstant.PARAM_PASSWORD);
        }
        if (!bo.getUsername().matches(USERNAME_REGEX)) {
            throw new BusinessException(MasterExceptionEnum.INVALID, I18nParamConstant.PARAM_USERNAME);
        }
        if (StringUtils.isNotBlank(bo.getNickname()) && !bo.getNickname().matches(CN_NAME_REGEX)) {
            throw new BusinessException(MasterExceptionEnum.INVALID, I18nParamConstant.PARAM_USERNAME);
        }
        List<Integer> roleIdList = validateRoleIdList4Create(bo.getRoleNameList());

        TbSysUser queryByUsername = tbSysUserDao.selectOne(Wrappers.lambdaQuery(TbSysUser.class).eq(TbSysUser::getUsername, bo.getUsername()).last(LAST_SQL_LIMIT_1));
        if (queryByUsername != null) {
            throw new BusinessException(MasterExceptionEnum.EXIST, I18nParamConstant.PARAM_USERNAME);
        }

        TbSysUser tbSysUser = buildTbSysUser4Create(bo);

        // 创建user
        int insertCount = tbSysUserDao.insert(tbSysUser);
        if (insertCount == 0) {
            return 0;
        }
        // 创建用户角色列表
        for (Integer roleId : roleIdList) {
            TbSysUserRole tbSysUserRole = new TbSysUserRole();
            tbSysUserRole.setUserId(tbSysUser.getId());
            tbSysUserRole.setRoleId(roleId);
            tbSysUserRoleDao.insert(tbSysUserRole);
        }
        return insertCount;
    }

    /**
     * 更新用户
     *
     * @param bo 入参
     * @return 回参
     */
    @Transactional(rollbackFor = Exception.class)
    public int update(UpdateUserRequestBo bo) {
        bo.setExpireType(0);
        bo.setExpireDate(LocalDateTime.now().plusYears(100L));
        validateLoginUserIsAdmin();

        if (bo.getId() == null) {
            throw new BusinessException(MasterExceptionEnum.NOT_NULL, I18nParamConstant.PARAM_ID);
        }
        if (bo.getEnabled() == null) {
            throw new BusinessException(MasterExceptionEnum.NOT_NULL, I18nParamConstant.PARAM_STATUS);
        }
        if (StringUtils.isNotBlank(bo.getNickname()) && !bo.getNickname().matches(CN_NAME_REGEX)) {
            throw new BusinessException(MasterExceptionEnum.INVALID, I18nParamConstant.PARAM_USERNAME);
        }
        // id存在性校验
        TbSysUser oldUser = tbSysUserDao.selectById(bo.getId());
        if (oldUser == null) {
            throw new BusinessException(MasterExceptionEnum.NOT_EXIST, I18nParamConstant.PARAM_ID);
        }
        // 内置用户不允许修改
        if (BooleanUtils.isTrue(oldUser.getBuiltinFlag())) {
            throw new BusinessException(MasterExceptionEnum.NO_PERMISSION_OPERATE, I18nParamConstant.PARAM_USER);
        }
        // roleId校验
        List<Integer> roleIdList = validateRoleIdList4Update(bo.getRoleNameList());

        // 构建需要更新的user对象
        TbSysUser tbSysUser4Update = buildTbSysUser4Update(bo);

        // 更新userRole
        boolean roleChanged = modifyUserRoleList(bo, roleIdList);

        // 更新user
        int count = tbSysUserDao.updateById(tbSysUser4Update);

        if (roleChanged) {
            // 若角色发生变化，则清除token缓存
            tokenService.clearByUserId(bo.getId());
        } else {
            // 更新缓存
            updateUserDetailCache(bo.getId());
        }
        return count;
    }

    private void updateUserDetailCache(Integer userId) {
        TbSysUser tbSysUser = tbSysUserDao.selectById(userId);
        UserDetail userDetail = userDetailProcessor.buildUserDetail(tbSysUser);
        tokenService.updateUserDetail(userDetail);
    }

    /**
     * 更新密码：当前登录用户必须是目标用户，即只能自己修改自己的密码
     *
     * @param bo 入参
     * @return 更新数量
     */
    public int updatePwd(UpdateUserPwdRequestBo bo) {
        if (bo.getId() == null) {
            throw new BusinessException(MasterExceptionEnum.NOT_NULL, I18nParamConstant.PARAM_ID);
        }
        if (StringUtils.isBlank(bo.getOldPassword())) {
            throw new BusinessException(MasterExceptionEnum.NOT_NULL, I18nParamConstant.PARAM_OLD_PASSWORD);
        }
        if (StringUtils.isBlank(bo.getNewPassword())) {
            throw new BusinessException(MasterExceptionEnum.NOT_NULL, I18nParamConstant.PARAM_NEW_PASSWORD);
        }
        // 当前登录用户必须是目标用户，即只能自己修改自己的密码
        UserDetail userDetail = ThreadContext.get(AuthService.KEY_USER_DETAIL);
        if (userDetail == null) {
            throw new BusinessException(MasterExceptionEnum.ERR_ACCESS_DENY);
        }
        if (!Objects.equals(userDetail.getId(), bo.getId())) {
            throw new BusinessException(MasterExceptionEnum.NO_PERMISSION_OPERATE, I18nParamConstant.PARAM_USER);
        }

        // id存在性校验
        TbSysUser oldUser = tbSysUserDao.selectById(bo.getId());
        if (oldUser == null) {
            throw new BusinessException(MasterExceptionEnum.NOT_EXIST, I18nParamConstant.PARAM_ID);
        }
        // 校验旧密码是否正确
        String hash = passwordDecryptAndHash(oldUser.getPasswordSalt(), bo.getOldPassword());
        if (!Objects.equals(hash, oldUser.getPassword())) {
            throw new BusinessException(MasterExceptionEnum.ERROR, I18nParamConstant.PARAM_OLD_PASSWORD);
        }
        TbSysUser tbSysUser = buildTbSysUser4UpdatePwd(bo);
        int count = tbSysUserDao.updateById(tbSysUser);
        // 更新缓存
        updateUserDetailCache(bo.getId());

        // 如果是首次登录进行改密或密码过期改密，改密成功后，需要清除未改密之前生成的token，防止token再次被利用访问其他接口
        if (BooleanUtils.isNotTrue(userDetail.getLoggedFlag()) || BooleanUtils.isTrue(userDetail.getPasswordExpired())) {
            tokenService.clearByUserId(oldUser.getId());
        }
        return count;
    }

    /**
     * 用户登录成功后：更新用户的enabled字段为true，即解锁
     *
     * @param username 入参
     */
    public void updateEnabledAfterLoginSuccess(String username) {
        if (StringUtils.isBlank(username)) {
            return;
        }
        TbSysUser old = tbSysUserDao.selectOne(Wrappers.lambdaQuery(TbSysUser.class).eq(TbSysUser::getUsername, username).last(LAST_SQL_LIMIT_1));
        if (old == null) {
            return;
        }
        updateEnabled(old, true);
    }

    /**
     * 管理员更新锁定或激活状态
     *
     * @param bo 入参
     * @return 更新数量
     */
    public int updateEnabledByAdmin(UpdateUserEnabledRequestBo bo) {
        /*
         * 1、当前登录用户必须是管理员
         * 2、目标用户不能是当前登录用户
         */
        UserDetail userDetail = validateLoginUserIsAdmin();

        if (userDetail.getId().equals(bo.getId())) {
            throw new BusinessException(MasterExceptionEnum.NO_PERMISSION_OPERATE, I18nParamConstant.PARAM_USER);
        }

        // 校验入参
        Integer id = bo.getId();
        if (id == null) {
            throw new BusinessException(MasterExceptionEnum.NOT_NULL, I18nParamConstant.PARAM_ID);
        }
        // id存在性校验
        TbSysUser old = tbSysUserDao.selectById(id);
        if (old == null) {
            throw new BusinessException(MasterExceptionEnum.NOT_EXIST, I18nParamConstant.PARAM_ID);
        }
        // 内置用户不允许修改
        if (BooleanUtils.isTrue(old.getBuiltinFlag())) {
            throw new BusinessException(MasterExceptionEnum.NO_PERMISSION_OPERATE, I18nParamConstant.PARAM_USER);
        }
        return updateEnabled(old, bo.getEnabled());
    }

    private int updateEnabled(TbSysUser old, Boolean enabled) {
        if (enabled == null || Objects.equals(enabled, old.getEnabled())) {
            if (BooleanUtils.isTrue(enabled)) {
                // 更新缓存
                loginRetryCountService.clear(old.getUsername());
                updateUserDetailCache(old.getId());
            }
            return 0;
        }

        TbSysUser tbSysUser = new TbSysUser();
        tbSysUser.setId(old.getId());
        tbSysUser.setEnabled(enabled);
        int count = tbSysUserDao.updateById(tbSysUser);
        // 若用户被锁定，清除用户缓存
        if (BooleanUtils.isFalse(enabled)) {
            // 清除缓存
            tokenService.clearByUserId(old.getId());
        } else {
            // 更新缓存
            loginRetryCountService.clear(old.getUsername());
            updateUserDetailCache(old.getId());
        }
        return count;
    }

    /**
     * 管理员重置用户密码
     *
     * @param bo 入参信息
     * @return 影响数量
     */
    public int resetPwd(ResetUserPwdRequestBo bo) {
        /*
         * 1、当前登录用户必须是管理员
         * 2、目标用户不能是当前登录用户
         */
        UserDetail userDetail = validateLoginUserIsAdmin();

        if (userDetail.getId().equals(bo.getId())) {
            throw new BusinessException(MasterExceptionEnum.NO_PERMISSION_OPERATE, I18nParamConstant.PARAM_USER);
        }

        // 校验入参
        if (bo.getId() == null) {
            throw new BusinessException(MasterExceptionEnum.NOT_NULL, I18nParamConstant.PARAM_ID);
        }
        if (StringUtils.isBlank(bo.getNewPassword())) {
            throw new BusinessException(MasterExceptionEnum.NOT_NULL, I18nParamConstant.PARAM_NEW_PASSWORD);
        }
        // id存在性校验
        TbSysUser oldUser = tbSysUserDao.selectById(bo.getId());
        if (oldUser == null) {
            throw new BusinessException(MasterExceptionEnum.NOT_EXIST, I18nParamConstant.PARAM_ID);
        }
        // 内置用户不允许重置密码
        if (BooleanUtils.isTrue(oldUser.getBuiltinFlag())) {
            throw new BusinessException(MasterExceptionEnum.NO_PERMISSION_OPERATE, I18nParamConstant.PARAM_USER);
        }
        // 修改密码
        TbSysUser tbSysUser = buildTbSysUser4ResetPwd(bo);
        int count = tbSysUserDao.updateById(tbSysUser);
        // 更新缓存
        updateUserDetailCache(bo.getId());
        return count;
    }

    @Transactional(rollbackFor = Exception.class)
    public int delete(DeleteUserRequestBo bo) {
        /*
         * 1、当前登录用户必须是管理员
         * 2、目标用户不能是当前登录用户
         */
        UserDetail userDetail = validateLoginUserIsAdmin();
        if (userDetail.getId().equals(bo.getId())) {
            throw new BusinessException(MasterExceptionEnum.NO_PERMISSION_OPERATE, I18nParamConstant.PARAM_USER);
        }

        // id校验
        if (bo.getId() == null) {
            throw new BusinessException(MasterExceptionEnum.NOT_NULL, I18nParamConstant.PARAM_ID);
        }
        TbSysUser oldUser = tbSysUserDao.selectById(bo.getId());
        if (oldUser == null) {
            throw new BusinessException(MasterExceptionEnum.NOT_EXIST, I18nParamConstant.PARAM_ID);
        }
        // 内置用户不允许删除
        if (BooleanUtils.isTrue(oldUser.getBuiltinFlag())) {
            throw new BusinessException(MasterExceptionEnum.NO_PERMISSION_OPERATE, I18nParamConstant.PARAM_USER);
        }
        // 删除 UserRole
        tbSysUserRoleDao.delete(Wrappers.lambdaQuery(TbSysUserRole.class).eq(TbSysUserRole::getUserId, bo.getId()));
        // 删除 user
        int count = tbSysUserDao.deleteById(bo.getId());
        // 清除缓存
        tokenService.clearByUserId(bo.getId());
        return count;
    }

    /**
     * 分页查询用户列表
     *
     * @param bo 入参
     * @return 用户列表信息
     */
    public PageList<PageQueryUserResponseBo> list(PageQueryUserRequestBo bo) {
        //
        PageRequest page = bo.getPage();
        PageValidateTool.pageValidate(page);

        Integer roleId = null;
        if (StringUtils.isNotBlank(bo.getRole()) && !"all".equalsIgnoreCase(bo.getRole())) {
            TbSysRole tbSysRole = tbSysRoleDao.selectOne(
                    Wrappers.lambdaQuery(TbSysRole.class).eq(TbSysRole::getRole, bo.getRole().toLowerCase()).last(LAST_SQL_LIMIT_1));
            if (tbSysRole == null) {
                return PageTool.buildPageList(bo.getPage().getPageSize(), bo.getPage().getCurrentPage(), 0, Collections.emptyList());
            }
            roleId = tbSysRole.getId();
        }
        PageQueryUserVo pageQueryVo = buildPageQueryParam(bo, roleId);
        Integer totalCount = tbSysUserDao.count(pageQueryVo);
        if (totalCount == null || totalCount == 0) {
            return PageTool.buildPageList(bo.getPage().getPageSize(), bo.getPage().getCurrentPage(), 0, Collections.emptyList());
        }
        List<PageQueryUserBo> pageList = tbSysUserDao.page(pageQueryVo);
        if (CollectionUtils.isEmpty(pageList)) {
            return PageTool.buildPageList(bo.getPage().getPageSize(), bo.getPage().getCurrentPage(), totalCount, Collections.emptyList());
        }
        List<PageQueryUserResponseBo> responseBoList = pageList.stream().map(userBo -> buildPageResponseBo(userBo, pageQueryVo)).collect(Collectors.toList());
        return PageTool.buildPageList(bo.getPage().getPageSize(), bo.getPage().getCurrentPage(), totalCount, responseBoList);
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

    @SneakyThrows
    private String passwordDecryptAndHash(String passwordSalt, String encryptedPassword) {
        String decryptedPassword = RsaLocalTool.privateDecrypt(encryptedPassword);
        return PasswordTool.hash(passwordSalt, decryptedPassword);
    }

    /**
     * @param bo              对象
     * @param inputRoleIdList 入参的角色列表
     * @return 角色是否变化
     */
    private boolean modifyUserRoleList(UpdateUserRequestBo bo, List<Integer> inputRoleIdList) {
        if (CollectionUtils.isEmpty(inputRoleIdList)) {
            return false;
        }
        boolean roleChanged = false;
        List<TbSysUserRole> tbSysUserRoles = tbSysUserRoleDao.selectList(Wrappers.lambdaQuery(TbSysUserRole.class).eq(TbSysUserRole::getUserId, bo.getId()));
        List<Integer> currentRoleIdList = tbSysUserRoles.stream().map(TbSysUserRole::getRoleId).filter(Objects::nonNull).collect(Collectors.toList());
        List<Integer> needDeletedRoleIds = currentRoleIdList.stream().filter(roleId -> !inputRoleIdList.contains(roleId)).collect(Collectors.toList());
        List<Integer> needAddedRoleIds = inputRoleIdList.stream().filter(roleId -> !currentRoleIdList.contains(roleId)).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(needDeletedRoleIds)) {
            roleChanged = true;
            tbSysUserRoleDao.delete(Wrappers.lambdaQuery(TbSysUserRole.class).eq(TbSysUserRole::getUserId, bo.getId()).in(TbSysUserRole::getRoleId, needDeletedRoleIds));
        }
        if (CollectionUtils.isNotEmpty(needAddedRoleIds)) {
            roleChanged = true;
            for (Integer needAddedRoleId : needAddedRoleIds) {
                TbSysUserRole tbSysUserRole = new TbSysUserRole();
                tbSysUserRole.setUserId(bo.getId());
                tbSysUserRole.setRoleId(needAddedRoleId);
                tbSysUserRoleDao.insert(tbSysUserRole);
            }
        }
        return roleChanged;
    }

    private TbSysUser buildTbSysUser4Create(CreateUserRequestBo bo) {
        TbSysUser tbSysUser = new TbSysUser();
        tbSysUser.setUsername(bo.getUsername());

        String passwordSalt = PasswordTool.genSalt();
        String hash = passwordDecryptAndHash(passwordSalt, bo.getPassword());
        tbSysUser.setPasswordSalt(passwordSalt);
        tbSysUser.setPassword(hash);

        tbSysUser.setNickname(bo.getNickname());

        buildExpireInfo(tbSysUser, bo.getExpireType(), bo.getExpireDate(), true);

        LocalDateTime now = LocalDateTime.now();
        tbSysUser.setPasswordUpdateTime(now);
        tbSysUser.setCreateTime(now);
        tbSysUser.setUpdateTime(now);
        tbSysUser.setEnabled(true);
        tbSysUser.setBuiltinFlag(false);
        tbSysUser.setLoggedFlag(false);
        tbSysUser.setDeleteFlag(false);
        return tbSysUser;
    }

    private void buildExpireInfo(TbSysUser tbSysUser, Integer expireType, LocalDateTime expireDate, boolean isCreate) {
        if (UserExpireType.NEVER.getType().equals(expireType)) {
            tbSysUser.setExpireType(expireType);
            tbSysUser.setExpireDate(MAX_DATE_TIME);
        } else if (UserExpireType.TEMPORARY.getType().equals(expireType)) {
            if (expireDate == null) {
                throw new BusinessException(MasterExceptionEnum.NOT_NULL, I18nParamConstant.PARAM_EXPIRE_DATE);
            }
            if (expireDate.isBefore(LocalDateTime.now())) {
                throw new BusinessException(MasterExceptionEnum.ERROR, I18nParamConstant.PARAM_EXPIRE_DATE);
            }
            tbSysUser.setExpireType(expireType);
            tbSysUser.setExpireDate(expireDate);
        } else {
            if (isCreate) {
                if (expireType == null) {
                    throw new BusinessException(MasterExceptionEnum.NOT_NULL, I18nParamConstant.PARAM_EXPIRE_TYPE);
                }
                throw new BusinessException(MasterExceptionEnum.INVALID, I18nParamConstant.PARAM_EXPIRE_TYPE);
            }
        }
    }

    private TbSysUser buildTbSysUser4Update(UpdateUserRequestBo bo) {
        TbSysUser tbSysUser = new TbSysUser();
        tbSysUser.setId(bo.getId());
        tbSysUser.setNickname(StringUtils.trimToNull(bo.getNickname()));
        buildExpireInfo(tbSysUser, bo.getExpireType(), bo.getExpireDate(), false);

        tbSysUser.setUpdateTime(LocalDateTime.now());
        return tbSysUser;
    }

    private TbSysUser buildTbSysUser4UpdatePwd(UpdateUserPwdRequestBo bo) {
        TbSysUser tbSysUser = new TbSysUser();
        tbSysUser.setId(bo.getId());

        String passwordSalt = PasswordTool.genSalt();
        String hash = passwordDecryptAndHash(passwordSalt, bo.getNewPassword());
        tbSysUser.setPasswordSalt(passwordSalt);
        tbSysUser.setPassword(hash);

        LocalDateTime now = LocalDateTime.now();
        tbSysUser.setPasswordUpdateTime(now);
        tbSysUser.setUpdateTime(now);
        tbSysUser.setLoggedFlag(true);
        return tbSysUser;
    }

    private TbSysUser buildTbSysUser4ResetPwd(ResetUserPwdRequestBo bo) {
        TbSysUser tbSysUser = new TbSysUser();
        tbSysUser.setId(bo.getId());

        String passwordSalt = PasswordTool.genSalt();
        String hash = passwordDecryptAndHash(passwordSalt, bo.getNewPassword());
        tbSysUser.setPasswordSalt(passwordSalt);
        tbSysUser.setPassword(hash);

        LocalDateTime now = LocalDateTime.now();
        tbSysUser.setPasswordUpdateTime(now);
        tbSysUser.setUpdateTime(now);
        // 下次登录，必须强制改密
        tbSysUser.setLoggedFlag(false);
        return tbSysUser;
    }

    private List<Integer> validateRoleIdList4Create(List<String> roleList) {
        if (CollectionUtils.isEmpty(roleList)) {
            throw new BusinessException(MasterExceptionEnum.NOT_EMPTY, I18nParamConstant.PARAM_ROLE_LIST);
        }
        List<String> distinctRoleList = roleList.stream().filter(StringUtils::isNotBlank).map(StringUtils::trim).distinct().collect(Collectors.toList());
        if (CollectionUtils.isEmpty(distinctRoleList)) {
            throw new BusinessException(MasterExceptionEnum.INVALID, I18nParamConstant.PARAM_ROLE_LIST);
        }
        List<TbSysRole> tbSysRoles = tbSysRoleDao.selectList(Wrappers.lambdaQuery(TbSysRole.class).in(TbSysRole::getRole, distinctRoleList));
        if (CollectionUtils.size(tbSysRoles) < CollectionUtils.size(distinctRoleList)) {
            throw new BusinessException(MasterExceptionEnum.NOT_EXIST, I18nParamConstant.PARAM_ROLE_ID);
        }
        return tbSysRoles.stream().map(TbSysRole::getId).collect(Collectors.toList());
    }

    private List<Integer> validateRoleIdList4Update(List<String> roleList) {
        if (CollectionUtils.isEmpty(roleList)) {
            return Collections.emptyList();
        }
        List<String> distinctRoleList = roleList.stream().filter(StringUtils::isNotBlank).map(StringUtils::trim).distinct().collect(Collectors.toList());
        if (CollectionUtils.isEmpty(distinctRoleList)) {
            return Collections.emptyList();
        }
        List<TbSysRole> tbSysRoles = tbSysRoleDao.selectList(Wrappers.lambdaQuery(TbSysRole.class).in(TbSysRole::getRole, distinctRoleList));
        if (CollectionUtils.size(tbSysRoles) < CollectionUtils.size(distinctRoleList)) {
            throw new BusinessException(MasterExceptionEnum.NOT_EXIST, I18nParamConstant.PARAM_ROLE_ID);
        }
        return tbSysRoles.stream().map(TbSysRole::getId).collect(Collectors.toList());
    }

    private PageQueryUserResponseBo buildPageResponseBo(PageQueryUserBo userBo, PageQueryUserVo pageQueryVo) {
        PageQueryUserResponseBo responseBo = new PageQueryUserResponseBo();
        responseBo.setId(userBo.getId());
        responseBo.setUsername(userBo.getUsername());

        responseBo.setNickname(userBo.getNickname());
        if (UserExpireType.TEMPORARY.getType().equals(userBo.getExpireType()) && userBo.getExpireDate() != null) {
            responseBo.setExpireDate(userBo.getExpireDate());
            responseBo.setExpired(LocalDateTime.now().isAfter(userBo.getExpireDate()));
        } else {
            responseBo.setExpired(false);
        }
        responseBo.setLocked(BooleanUtils.isNotTrue(userBo.getEnabled()) || pageQueryVo.getLockedUsernames().contains(userBo.getUsername()));

        List<TbSysRole> tbRoleList;
        if (pageQueryVo.getRoleId() != null) {
            tbRoleList = tbSysRoleDao.findByUserId(responseBo.getId());
        } else {
            tbRoleList = tbSysRoleDao.selectBatchIds(buildRoleIdList(userBo.getRoleIds()));
        }
        responseBo.setRoleNameList(tbRoleList.stream().map(TbSysRole::getRole).distinct().collect(Collectors.toList()));
        responseBo.setBuiltinFlag(userBo.getBuiltinFlag());
        return responseBo;
    }

    private PageQueryUserVo buildPageQueryParam(PageQueryUserRequestBo bo, Integer roleId) {
        PageQueryUserVo pageQueryVo = new PageQueryUserVo();
        PageRequest page = bo.getPage();
        int from = (page.getCurrentPage() - 1) * page.getPageSize();
        pageQueryVo.setFrom(from);
        pageQueryVo.setOffset(page.getPageSize());
        pageQueryVo.setUsername(StringUtils.trimToNull(bo.getUsername()));
        pageQueryVo.setRoleId(roleId);
        pageQueryVo.setStatus(bo.getStatus());
        pageQueryVo.setLockedUsernames(new ArrayList<>(loginRetryCountService.getAllUsername()));
        return pageQueryVo;
    }

    private List<Integer> buildRoleIdList(String roleIds) {
        if (StringUtils.isBlank(roleIds)) {
            return Collections.emptyList();
        }
        String[] roleArray = roleIds.split(",");
        if (ArrayUtils.isEmpty(roleArray)) {
            return Collections.emptyList();
        }
        return Arrays.stream(roleArray).map(TypeConvertTool::stringToInteger).filter(Objects::nonNull).distinct().collect(Collectors.toList());
    }

    public boolean isEnabled(String username) {
        TbSysUser old = tbSysUserDao.selectOne(Wrappers.lambdaQuery(TbSysUser.class).eq(TbSysUser::getUsername, username).last(LAST_SQL_LIMIT_1));
        if (old == null) {
            throw new BusinessException(MasterExceptionEnum.NOT_EXIST, I18nParamConstant.PARAM_USERNAME);
        }
        return BooleanUtils.isTrue(old.getEnabled());
    }
}
