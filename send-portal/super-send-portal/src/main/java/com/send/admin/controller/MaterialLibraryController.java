package com.send.admin.controller;

import com.project.base.model.pagination.PageList;
import com.project.base.model.web.CommonResponse;
import com.send.admin.service.biz.material.MaterialLibraryService;
import com.send.admin.service.bo.material.CreateMaterialLibraryRequestBo;
import com.send.admin.service.bo.material.DeleteMaterialLibraryRequestBo;
import com.send.admin.service.bo.material.PageQueryMaterialLibraryRequestBo;
import com.send.admin.service.bo.material.UpdateMaterialLibraryRequestBo;
import com.send.model.db.mysql.MaterialLibrary;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Compang: Information Technology Company
 *
 * @author WangCheng
 * @version 1.0
 * @date 2025年04月07日 20:45
 * @description：
 */
@Slf4j
@RestController
@RequestMapping("/materialLibrary")
@Api(tags = "文字素材模块")
public class MaterialLibraryController {

    @Autowired
    private MaterialLibraryService materialLibraryService;

    @PostMapping("/list")
    @ApiOperation(value = "文字素材列表")
    public CommonResponse<PageList<MaterialLibrary>> list(@RequestBody PageQueryMaterialLibraryRequestBo vo) {
        PageList<MaterialLibrary> responseBo = materialLibraryService.pageList(vo);
        return CommonResponse.builder(responseBo).build();
    }

    @PostMapping("/create")
    @ApiOperation(value = "文字素材创建")
    public CommonResponse<Integer> create(@Validated @RequestBody CreateMaterialLibraryRequestBo vo) {
        int count = materialLibraryService.create(vo);
        return CommonResponse.builder(count).build();
    }


    @PostMapping("/update")
    @ApiOperation(value = "文字素材更新")
    public CommonResponse<Integer> update(@Validated @RequestBody UpdateMaterialLibraryRequestBo vo) {
        int count = materialLibraryService.update(vo);
        return CommonResponse.builder(count).build();
    }

    @PostMapping("/delete")
    @ApiOperation(value = "文字素材删除")
    public CommonResponse<Integer> delete(@Validated @RequestBody DeleteMaterialLibraryRequestBo vo) {
        int count = materialLibraryService.delete(vo);
        return CommonResponse.builder(count).build();
    }
}
