package com.curtisnewbie.module.task.web;

import com.curtisnewbie.common.advice.RoleRequired;
import com.curtisnewbie.common.trace.TraceUtils;
import com.curtisnewbie.common.util.EnumUtils;
import com.curtisnewbie.common.vo.PageablePayloadSingleton;
import com.curtisnewbie.common.vo.Result;
import com.curtisnewbie.module.task.constants.TaskConcurrentEnabled;
import com.curtisnewbie.module.task.constants.TaskEnabled;
import com.curtisnewbie.module.task.scheduling.JobUtils;
import com.curtisnewbie.module.task.service.NodeCoordinationService;
import com.curtisnewbie.module.task.service.TaskHistoryService;
import com.curtisnewbie.module.task.service.TaskService;
import com.curtisnewbie.module.task.vo.*;
import com.curtisnewbie.module.task.web.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

import static com.curtisnewbie.common.util.AssertUtils.notNull;
import static com.curtisnewbie.common.util.BeanCopyUtils.toType;

/**
 * Task Controller
 *
 * @author yongjie.zhuang
 */
@RestController
@RequestMapping("${web.base-path}/task")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskHistoryService taskHistoryService;

    @Autowired
    private NodeCoordinationService nodeCoordinationService;

    @RoleRequired(role = "admin")
    @PostMapping("/list")
    public Result<ListTaskByPageRespWebVo> listTaskByPage(@RequestBody ListTaskByPageReqWebVo reqVo) {
        notNull(reqVo.getPagingVo());

        final ListTaskByPageReqVo listTaskByPageReqVo = toType(reqVo, ListTaskByPageReqVo.class);
        PageablePayloadSingleton<List<ListTaskByPageRespVo>> pi = taskService.listByPage(listTaskByPageReqVo, reqVo.getPagingVo());
        ListTaskByPageRespWebVo resp = new ListTaskByPageRespWebVo();
        resp.setPagingVo(pi.getPagingVo());
        resp.setList(
                pi.getPayload()
                        .stream()
                        .map(v -> toType(v, TaskWebVo.class))
                        .collect(Collectors.toList())
        );
        return Result.of(resp);
    }

    @RoleRequired(role = "admin")
    @PostMapping("/history")
    public Result<ListTaskHistoryByPageRespWebVo> listTaskHistoryByPage(@RequestBody ListTaskHistoryByPageReqVo reqVo) {

        notNull(reqVo.getPagingVo());
        PageablePayloadSingleton<List<ListTaskHistoryByPageRespVo>> pi = taskHistoryService.findByPage(reqVo);

        ListTaskHistoryByPageRespWebVo resp = new ListTaskHistoryByPageRespWebVo();
        resp.setList(
                pi.getPayload()
                        .stream()
                        .map(v -> toType(v, TaskHistoryWebVo.class))
                        .collect(Collectors.toList())
        );
        resp.setPagingVo(pi.getPagingVo());
        return Result.of(resp);
    }

    @RoleRequired(role = "admin")
    @PostMapping("/update")
    public Result<Void> update(@RequestBody UpdateTaskReqVo vo) {
        notNull(vo.getId());

        if (vo.getCronExpr() != null && !JobUtils.isCronExprValid(vo.getCronExpr())) {
            return Result.error("Cron expression illegal");
        }
        if (vo.getEnabled() != null) {
            TaskEnabled tce = EnumUtils.parse(vo.getEnabled(), TaskEnabled.class);
            notNull(tce);
        }
        if (vo.getConcurrentEnabled() != null) {
            TaskConcurrentEnabled tce = EnumUtils.parse(vo.getConcurrentEnabled(), TaskConcurrentEnabled.class);
            notNull(tce);
        }
        vo.setUpdateBy(TraceUtils.tUser().getUsername());
        taskService.updateById(vo);
        return Result.ok();
    }

    @RoleRequired(role = "admin")
    @PostMapping("/trigger")
    public Result<Void> trigger(@RequestBody TriggerTaskReqVo vo) {
        notNull(vo.getId());
        final TaskVo tv = taskService.selectById(vo.getId());
        Assert.notNull(tv, "Task doesn't exist");
        nodeCoordinationService.coordinateJobTriggering(tv, TraceUtils.tUser().getUsername());
        return Result.ok();
    }
}
