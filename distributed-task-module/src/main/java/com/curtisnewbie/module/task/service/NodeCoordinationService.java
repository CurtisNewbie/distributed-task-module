package com.curtisnewbie.module.task.service;

/**
 * Service that coordinates between nodes
 *
 * @author yongjie.zhuang
 */
public interface NodeCoordinationService {

    /**
     * Whether current node is a main node (coordinator)
     */
    boolean isMainNode();

}
