package com.send.admin.service.biz.sys;

import com.send.model.auth.UserDetail;
import com.send.model.db.mysql.TbSysPermission;
import com.send.model.enums.PermissionTypeEnum;
import com.send.admin.service.bo.sys.RouterResponseBO;
import com.project.base.common.thread.ThreadContext;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author WangCheng
 * @version 1.0
 * @CreateTime: 2025-04-05 11:27
 * @Description:
 * @Company: Information Technology Company
 */
@Service
public class PermissionService {

    /**
     * 列出router集合
     *
     * @return 菜单路由结果
     */
    public RouterResponseBO listRouter() {
        UserDetail userDetail = ThreadContext.get(AuthService.KEY_USER_DETAIL);
        List<TbSysPermission> permissionList = userDetail.getPermissionList()
                .stream()
                .filter(x -> PermissionTypeEnum.ROUTER.getValue() == x.getType())
                .collect(Collectors.toList());

        /* 查出第一级菜单 */
        List<TbSysPermission> firstLevelPermissionList = permissionList
                .stream()
                .filter(x -> x.getParentId() == null)
                .collect(Collectors.toList());

        List<RouterResponseBO.FrontRouter> firstLevelRouterList = new ArrayList<>();
        for (TbSysPermission firstLevelPermission : firstLevelPermissionList) {

            /* 第一级 */
            RouterResponseBO.FrontRouter firstLevelFrontRouter = new RouterResponseBO.FrontRouter();
            firstLevelFrontRouter.setRouter(firstLevelPermission.getFrontRouter());
            firstLevelFrontRouter.setIcon(firstLevelPermission.getFrontIcon());
            firstLevelFrontRouter.setMenuName(firstLevelPermission.getFrontMenuName());
            firstLevelRouterList.add(firstLevelFrontRouter);

            /* 第二级 */
            List<RouterResponseBO.FrontRouter> secondLevelRouterList = new ArrayList<>();
            firstLevelFrontRouter.setChildren(secondLevelRouterList);

            List<TbSysPermission> secondLevelPermissionList = permissionList.stream()
                    .filter(x -> x.getParentId() != null
                            && x.getParentId().equals(firstLevelPermission.getId()))
                    .collect(Collectors.toList());
            for (TbSysPermission secondLevelPermission : secondLevelPermissionList) {
                RouterResponseBO.FrontRouter secondLevelFrontRouter = new RouterResponseBO.FrontRouter();
                secondLevelFrontRouter.setRouter(secondLevelPermission.getFrontRouter());
                secondLevelFrontRouter.setIcon(secondLevelPermission.getFrontIcon());
                secondLevelFrontRouter.setMenuName(secondLevelPermission.getFrontMenuName());
                secondLevelRouterList.add(secondLevelFrontRouter);

                /* 第三级，目前这块业务设计不合理，最终要去掉 shit code */
                List<RouterResponseBO.FrontRouter> thirdLevelRouterList = new ArrayList<>();
                secondLevelFrontRouter.setChildren(thirdLevelRouterList);

                List<TbSysPermission> thirdLevelPermissionList = permissionList.stream()
                        .filter(x -> x.getParentId() != null
                                && x.getParentId().equals(secondLevelPermission.getId()))
                        .collect(Collectors.toList());
                for (TbSysPermission thirdLevelPermission : thirdLevelPermissionList) {
                    RouterResponseBO.FrontRouter thirdLevelFrontRouter = new RouterResponseBO.FrontRouter();
                    thirdLevelFrontRouter.setRouter(thirdLevelPermission.getFrontRouter());
                    thirdLevelFrontRouter.setIcon(thirdLevelPermission.getFrontIcon());
                    thirdLevelFrontRouter.setMenuName(thirdLevelPermission.getFrontMenuName());
                    thirdLevelRouterList.add(thirdLevelFrontRouter);
                }

            }
        }

        /* 构建对象返回 */
        RouterResponseBO routerResponseBO = new RouterResponseBO();
        routerResponseBO.setRouterList(firstLevelRouterList);

        return routerResponseBO;

    }

}
